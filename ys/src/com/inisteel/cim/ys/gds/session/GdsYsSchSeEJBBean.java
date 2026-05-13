/**
 * @(#)GdsYsSchSeEJBBean
 *
 * @version          V1.00
 * @author           조병기
 * @date             2014/12/22
 *
 * @description      제품(봉강) 야드 Schedule 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.gds.session;

import java.util.Vector;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ys.gds.dao.GdsYsDAO;
import com.sun.tools.javac.v8.util.Convert;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.session.YsComm;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;


/**
 *      [A] 클래스명 : 제품 봉강야드 Schedule 처리
 *
 * @ejb.bean name="GdsYsSchSeEJB" jndi-name="GdsYsSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class GdsYsSchSeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	private YsComm YsComm = new YsComm();
	private GdsYsDAO GdsYsDao = new GdsYsDAO();
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/**	
	 *      [A] 오퍼레이션명 : 봉강크레인스케줄(YSYSJ302)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYSYSJ302(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강크레인스케줄MAIN[GdsYsSchSeEJB.rcvYSYSJ302] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecordSet rsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");
		int intRtnVal = 0;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg);								//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"  )); //야드작업예약ID
			String ydSchCd    = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"    )); //야드스케쥴코드
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //야드설비ID

			if ("".equals(modifier)) { modifier = msgId; }

			commUtils.printLog(logId, "스케쥴코드[" + ydSchCd + "], 설비ID[" + ydEqpId + "], 작업예약ID[" + ydWbookId + "], 수정자[" + modifier + "]", "SL");

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
			JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();	//전문 Return
			String trtMsg  = ""; 		//처리메세지
			String ydL3Msg = ""; 		//야드L3MESSAGE
			Vector vecResult      		= new Vector();
			Vector vecReResult      	= new Vector();
			
			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
			jrParam.setField("YD_SCH_CD"  , ydSchCd  ); //야드스케쥴코드
			jrParam.setField("YD_EQP_ID"  , ydEqpId  ); //야드설비ID
			jrParam.setField("MODIFIER"   , modifier ); //수정자

			/**********************************************************
			* 1. 파라메타 정보  Check
			* 1.1 스케줄코드 상태 Check
			* 1.2 설비고장 및 OFF-LINE Check
			* 1.3 파라메타 정보 Check
			**********************************************************/
			//스케줄코드 Check
			if ("".equals(ydWbookId) && !"".equals(ydSchCd)) {
				JDTORecord jrChk = YsComm.chkSchCd(jrParam);
				
				ydL3Msg = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"));

				if (!"".equals(ydL3Msg)) {
					ydSchCd = "";
				}
			}

			//설비ID Check
			if ("".equals(ydWbookId) && "".equals(ydSchCd) && !"".equals(ydEqpId)) {
				JDTORecord jrChk = YsComm.chkEqpStat(jrParam);

				ydL3Msg = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"));

				if (!"".equals(ydL3Msg)) {
					ydEqpId = "";
				}
			}
			/**********************************************************
 			 * 1.3  파라메타 정보 Check
 			 * 1.3.1 파라메타 : 크레인설비ID                           -> 해당크레인설비ID로 만들어진 크레인스케줄금지가 되지 않고 크레인우선순위가 가장빠른 작업예약들 중에서
 			 * 	                                                                                                           가장빠른 작업예약을 하나 조회해서 작업 진행.
 			 * 1.3.2. 파라메타 : 크레인스케줄코드, 크레인설비ID            -> 크레인스케줄코드로 크레인스케줄이 생성되지 않은 작업예약들 중에서 가장빠른 작업예약을 하나 조회해서 작업 진행.
 			 * 1.3.3. 파라메타 : 크레인스케줄코드, 크레인설비ID, 작업예약ID	-> 해당작업예약ID로 직접 조회를 해서 작업진행 - 차량도착인 경우
			**********************************************************/			
			//작업예약ID 조회
			if (!"".equals(ydWbookId)) {
				rsWbook = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchWbook", logId, methodNm, "작업예약 조회");
				
			} else if (!"".equals(ydSchCd)) {
				rsWbook = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchWbookSchcd", logId, methodNm, "작업예약 조회");
				 
			} else if (!"".equals(ydEqpId)) {
				rsWbook = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchWbookEqp", logId, methodNm, "작업예약 조회");
				 
			} else {
				throw new Exception("오류:작업예약ID조회 항목 없음");
			}

			if (rsWbook != null && rsWbook.size() > 0) {
				ydWbookId = commUtils.trim(rsWbook.getRecord(0).getFieldString("YD_WBOOK_ID"));
			} else {
				throw new Exception("오류:" + trtMsg + " >> 작업예약정보 없음");
			}

			commUtils.printLog(logId, trtMsg + " >> 작업예약ID조회 [" + ydWbookId + "]", "SL");

			jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID

			/**********************************************************
			* 1.2 크레인 작업 재료에 현재 적치단 저장위치 Update (별도 Transaction 으로 처리)
			**********************************************************/
			EJBConnector tranConn = new EJBConnector("default", "GdsYsSchSeEJB", this);
			tranConn.trx("updCrnSchWB", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			
			/**********************************************************
			* 2.스케줄수행판단 모듈
			* 2.1 크레인 선택
			* 2.2 TO위치 사전 점검
			**********************************************************/
	
			//조회된 작업예약ID로 상태정보 Check
			String ydToLocDcsnMtd = ""; 	//야드To위치결정방법
			String ydToLocGuide   = ""; 	//야드To위치Guide
			String toLocChkGp     = ""; 	//To위치 점검을 위한 구분(G:To위치Guide, C:차량상차, T:대차상차)
			String trnEqpCd       = ""; 	//운송장비코드
			String ydEqpStat      = "";		//야드설비상태
			String ydSchPrior     = "";
			
			int ttMtlSh;			//전체 재료매수
			int wmMtlSh;			//작업예약 재료매수
			int stMtlSh;			//저장품 재료매수
			int slMtlSh;			//적치단 재료매수
			int statCSh;			//적치중인 재료매수(야드적치단재료상태가 적치 중[C] 인 재료수)
			int abLocSh;			//저장위치이상 재료매수

			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchStat  
			SELECT WB.YD_GP                                   --야드구분
			      ,WB.YD_BAY_GP                               --야드동구분
			      ,WB.YD_SCH_CD                               --야드스케쥴코드
			      ,WB.YD_SCH_PRIOR                            --야드스케쥴우선순위
			      ,WB.YD_TO_LOC_DCSN_MTD                      --야드TO위치결정방법
			      ,WB.YD_TO_LOC_GUIDE                         --야드TO위치GUIDE
			      ,CASE WHEN WB.YD_SCH_CD LIKE '__TR__U_' THEN 'C' --차량상차
			            WHEN WB.YD_SCH_CD LIKE '__TC__U_' THEN 'T' --대차상차
			            WHEN LENGTH(WB.YD_TO_LOC_GUIDE) >= 6       --SPAN구분 이상이면
			             AND WB.YD_TO_LOC_GUIDE LIKE WB.YD_GP||WB.YD_BAY_GP||'%' THEN 'G' --TO위치GUIDE
			            WHEN WB.YD_SCH_CD LIKE '__PU__U_'
			              OR WB.YD_SCH_CD LIKE '__DP__U_' THEN 'E' --불출
			      	    ELSE 'Z'                                   --기타
			      	END AS TO_LOC_CHK_GP       
			      ,TO_CHAR(WB.REG_DDTT,'YYYYMMDDHH24MISS') AS YD_WBOOK_DT --야드작업예약일시
			      ,WB.YD_WRK_PLAN_CRN                         --야드작업계획크레인
			      ,E0.YD_EQP_STAT      AS YD_EQP_STAT_PLN     --작업계획크레인 야드설비상태
			      ,E0.YD_EQP_WRK_MODE  AS YD_EQP_WRK_MODE_PLN --작업계획크레인 야드설비작업MODE
			      ,SR.WRK_CRN          AS YD_WRK_CRN            --야드작업크레인
			      ,SR.YD_WRK_CRN_PRIOR                        --야드작업크레인우선순위
			      ,E1.YD_EQP_STAT      AS YD_EQP_STAT_WRK     --작업크레인 야드설비상태
			      ,NVL(WM.TT_MTL_SH,0) AS TT_MTL_SH           --전체 재료매수
			      ,NVL(WM.WM_MTL_SH,0) AS WM_MTL_SH           --작업예약 재료매수
			      ,NVL(WM.ST_MTL_SH,0) AS ST_MTL_SH           --저장품 재료매수
			      ,NVL(WM.SL_MTL_SH,0) AS SL_MTL_SH           --적치단 재료매수
			      ,NVL(WM.STAT_C_SH,0) AS STAT_C_SH           --적치중인 재료매수
			      ,NVL(WM.STAT_SC_SH,0) AS STAT_SC_SH         --서냉재료매수
			      ,(SELECT COUNT(*)
			          FROM TB_YS_WRKBOOKMTL WM
			              ,TB_YS_STKLYR     SL
			         WHERE WM.SSTL_NO      = SL.SSTL_NO
			           AND WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
			           AND SL.YS_STK_COL_GP NOT LIKE SUBSTR(WB.YD_SCH_CD,1,2)||'%'
			           AND SL.YD_STK_LYR_MTL_STAT = 'C'
			           AND WM.DEL_YN      = 'N'
			           AND SL.DEL_YN      = 'N') AS AB_LOC_SH --저장위치이상 재료매수
			      ,(SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END
			          FROM TB_YS_WRKBOOKMTL WM
			              ,TB_YS_CRNSCH     CS
			              ,TB_YS_CRNWRKMTL  CM
			         WHERE WM.SSTL_NO        = CM.SSTL_NO
			           AND CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
			           AND WM.YD_WBOOK_ID   = WB.YD_WBOOK_ID
			           AND WM.DEL_YN        = 'N'
			           AND CM.DEL_YN        = 'N'
			           AND CS.DEL_YN        = 'N') AS CM_DUP_YN --크레인스케줄 재료중복여부
			      ,(SELECT MIN(CASE WHEN SL.YS_STK_COL_GP||SL.YS_STK_BED_NO = CS.YS_DN_WO_LOC THEN '1'
			                        WHEN SL.YS_STK_COL_GP||SL.YS_STK_BED_NO = CS.YS_UP_WO_LOC
			                         AND CS.YD_WRK_PROG_STAT != '2' THEN '2' END)
			          FROM TB_YS_WRKBOOKMTL WM
			              ,TB_YS_STKLYR     SL
			              ,TB_YS_CRNSCH     CS
			         WHERE WM.SSTL_NO      = SL.SSTL_NO
			           AND SL.YS_STK_COL_GP||SL.YS_STK_BED_NO IN (CS.YS_UP_WO_LOC, CS.YS_DN_WO_LOC)
			           AND WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
			           AND WM.DEL_YN      = 'N'
			           AND SL.DEL_YN      = 'N'
			           AND CS.DEL_YN      = 'N') AS CL_DUP_GP --크레인스케줄 저장위치중복여부
			      ,WB.YD_CAR_USE_GP                           --야드차량사용구분
			      ,WB.TRN_EQP_CD                              --운송장비코드
			      ,WB.CAR_NO                                  --차량번호
			      ,WB.CARD_NO                                 --카드번호
			      ,(SELECT YD_CURR_BAY_GP
			          FROM TB_YD_EQP EQ
			         WHERE EQ.YD_EQP_ID = SUBSTR(WB.YD_SCH_CD,1,1)||'XTC'||SUBSTR(WB.YD_SCH_CD,5,2)) AS YD_CURR_BAY_GP-- 현 대차위치
			  FROM TB_YS_WRKBOOK WB
			     ,  (SELECT A.YD_GP
			              ,A.YD_BAY_GP
			              ,YD_SCH_CD
			              ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN1
			                    WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN2
			                END AS WRK_CRN
			              ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN_PRIOR1
			                    WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN_PRIOR2
			                END AS YD_WRK_CRN_PRIOR
			              ,YD_SCH_CD_NM
			              ,YD_SCH_CONTENTS
			            FROM TB_YS_SCHRULE A
			                ,(
			                    SELECT YD_GP
			                          ,YD_BAY_GP
			                          ,YD_SCH_GP
			                          ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 1, YD_EQP_STAT, '') ELSE 'O' END), 'X') AS STAT1 
			                          ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 2, YD_EQP_STAT, '') ELSE 'X' END), 'X') AS STAT2 
			                    FROM   (
			                                SELECT YD_EQP_ID
			                                      ,YD_GP
			                                      ,YD_BAY_GP
			                                      ,DECODE(YD_EQP_STAT,'B','C','O') AS YD_EQP_STAT
			                                      ,SUBSTR(YD_EQP_NO,-1) AS CRN_NO 
			                                      ,DECODE(YD_EQP_GP,'CR','CR','S'||SUBSTR(YD_EQP_ID,-1)) AS YD_SCH_GP
			                                FROM   TB_YS_EQP
			                                WHERE  YD_EQP_GP IN ('CR','SC')
			                           )
			                    GROUP BY YD_GP, YD_BAY_GP , YD_SCH_GP             
			                 ) B
			            WHERE 1=1
			            AND   A.YD_DATA_GP = 'M'
			            AND   A.YD_SCH_GP = B.YD_SCH_GP
			            AND   A.YD_GP = B.YD_GP
			            AND   A.YD_BAY_GP = B.YD_BAY_GP
			            AND   A.YD_CRN_STAT1 = B.STAT1
			            AND   A.YD_CRN_STAT2 = B.STAT2
			        ) SR      
			      ,TB_YS_EQP     E0
			      ,TB_YS_EQP     E1
			      ,(SELECT WM.YD_WBOOK_ID
			              ,COUNT(*)                  AS TT_MTL_SH
			              ,COUNT(DISTINCT WM.SSTL_NO) AS WM_MTL_SH
			              ,COUNT(DISTINCT ST.SSTL_NO) AS ST_MTL_SH
			              ,COUNT(DISTINCT SL.SSTL_NO) AS SL_MTL_SH
			              ,SUM(DECODE(SL.YD_STK_LYR_MTL_STAT,'C',1)) AS STAT_C_SH --적치중인 재료매수
			              ,SUM(DECODE(ST.BLOOM_CL_MTD,'A',1))        AS STAT_SC_SH --서냉 재료매수
			          FROM TB_YS_WRKBOOKMTL WM
			              ,TB_YS_STOCK      ST
			              ,TB_YS_STKLYR     SL
			         WHERE WM.SSTL_NO      = ST.SSTL_NO(+)
			           AND WM.SSTL_NO      = SL.SSTL_NO(+)
			           AND WM.YD_WBOOK_ID = :V_YD_WBOOK_ID
			           AND WM.DEL_YN      = 'N'
			           AND ST.DEL_YN(+)   = 'N'
			           AND SL.DEL_YN(+)   = 'N'
			         GROUP BY WM.YD_WBOOK_ID) WM
			 WHERE WB.YD_SCH_CD       = SR.YD_SCH_CD(+)
			   AND SR.WRK_CRN         = E1.YD_EQP_ID(+)
			   AND WB.YD_WRK_PLAN_CRN = E0.YD_EQP_ID(+)
			   AND WB.YD_WBOOK_ID     = WM.YD_WBOOK_ID(+)
			   AND WB.YD_WBOOK_ID     = :V_YD_WBOOK_ID
			   AND WB.DEL_YN          = 'N'
			   AND E1.DEL_YN(+)       = 'N'
			   AND E0.DEL_YN(+)       = 'N'
			*/
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchStat", logId, methodNm, "작업예약 조회");
			if (jsChk.size() <= 0) {
				throw new Exception("오류:" + trtMsg + " >> 상태정보 없음");
			} else {
				JDTORecord jrChk = jsChk.getRecord(0);

				ydSchCd                = commUtils.trim(jrChk.getFieldString("YD_SCH_CD"          ));	//야드스케쥴코드
				ydToLocDcsnMtd         = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_DCSN_MTD" ));	//야드To위치결정방법 --F
				ydToLocGuide           = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_GUIDE"    ));	//야드To위치Guide  --KA01
				toLocChkGp             = commUtils.trim(jrChk.getFieldString("TO_LOC_CHK_GP"      ));	//To위치점검구분  --Z(기타)
				ydSchPrior             = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN_PRIOR"   ));	//야드스케쥴우선순위
				String ydWrkPlanCrn    = commUtils.trim(jrChk.getFieldString("YD_WRK_PLAN_CRN"    ));	//야드작업계획크레인
				String ydEqpStatPln    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_PLN"    ));	//야드설비상태(작업계획크레인)
				String ydEqpWrkModePln = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_PLN"));	//야드설비작업Mode(작업계획크레인)
				String ydWrkCrn        = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN"         ));	//야드작업크레인
				String ydEqpStatWrk    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_WRK"    ));	//야드설비상태(작업크레인)
				String cmDupYn         = commUtils.trim(jrChk.getFieldString("CM_DUP_YN"          ));	//크레인스케줄 재료중복여부
				String clDupGp         = commUtils.trim(jrChk.getFieldString("CL_DUP_GP"          ));	//크레인스케줄 저장위치중복여부
				ttMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("TT_MTL_SH"),"0"));	//전체 재료매수
				wmMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("WM_MTL_SH"),"0"));	//작업예약 재료매수
				stMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("ST_MTL_SH"),"0"));	//저장품 재료매수
				slMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("SL_MTL_SH"),"0"));	//적치단 재료매수
				statCSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("STAT_C_SH"),"0"));	//적치중인 재료매수(야드적치단재료상태가 적치 중[C] 인 재료수)
				abLocSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("AB_LOC_SH"),"0"));	//저장위치이상 재료매수
				
				if (wmMtlSh == 0) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료 정보 없음");
				} else if (wmMtlSh != ttMtlSh) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치단 중복 등록 [작업예약: " + wmMtlSh + ", 적치단: " + ttMtlSh + "]");
				} else if (wmMtlSh != slMtlSh) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치단 정보 이상 [" + (wmMtlSh - slMtlSh) + "매]");
				} else if (wmMtlSh != statCSh) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치중[C]이 아님 [" + (wmMtlSh - statCSh) + "매]");
				} else if (wmMtlSh != stMtlSh) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료 저장품 정보 이상 [" + (wmMtlSh - stMtlSh) + "매]");
				} else if (abLocSh > 0) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치 이상 [" + abLocSh + "매]");
				} else if ("Y".equals(cmDupYn)) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료가 기 등록된 크레인작업재료와 중복");
				} else if ("1".equals(clDupGp)) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권하위치와 중복");
				} else if ("2".equals(clDupGp)) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권상위치와 중복");
				}
				
				/**********************************************************
				* 1.3 크레인 결정
				**********************************************************/

				if (!"".equals(ydWrkPlanCrn) && !"B".equals(ydEqpStatPln) && "1".equals(ydEqpWrkModePln)) {
					//작업예약 지정크레인 : 최우선 지정
					ydEqpId   = ydWrkPlanCrn;	//야드설비ID
					ydEqpStat = ydEqpStatPln;	//야드설비상태
					commUtils.printLog(logId, trtMsg + " >> 작업예약 지정크레인[" + ydWrkPlanCrn + "]으로 설정", "SL");
				} else {
					ydEqpId   = ydWrkCrn;		//야드설비ID
					ydEqpStat = ydEqpStatWrk;	//야드설비상태
				}
				/**********************************************************
				* 1.4 To위치 사전 점검
				*     - 차량상차 작업('C')
				*     - 야드To위치Guide('G')
				**********************************************************/
				//To위치 사전 점검
				if ("C".equals(toLocChkGp)) {
					//차량상차작업
					String ydCarUseGp = commUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP"));	//야드차량사용구분
				           trnEqpCd   = commUtils.trim(jrChk.getFieldString("TRN_EQP_CD"   ));	//운송장비코드
					String carNo      = commUtils.trim(jrChk.getFieldString("CAR_NO"       ));	//차량번호

					if ("".equals(ydCarUseGp)) {
						throw new Exception("오류:" + trtMsg + " >> 차량상차작업 야드차량사용구분 없음");
						
					} else if ("L".equals(ydCarUseGp) && "".equals(trnEqpCd)) {
						// 구내운송
						throw new Exception("오류:" + trtMsg + " >> 구내운송 상차작업 운송장비코드 없음");
					} else if ("G".equals(ydCarUseGp)) {
						// 출하
						if ("".equals(carNo)) {
								throw new Exception("오류:" + trtMsg + " >> 출하차량 상차작업 차량번호 또는 카드번호 없음");
						}
					}
				}
				
				//야드To위치Guide 값이 4자리 이상이고 To 야드동이 같을 경우가 아니면
				//TO 위치 가이드('G') 아니면 야드To위치결정방법,야드To위치Guide CLEAR && KBC1이면 TO위치가이드 클리어 안시키기?
				if (!"G".equals(toLocChkGp)) {
					ydToLocDcsnMtd = ""; //야드To위치결정방법
					ydToLocGuide   = ""; //야드To위치Guide
				}
			}
	
			JDTORecord jrParamSet = JDTORecordFactory.getInstance().create();
			jrParamSet.setResultCode(logId);	//Log ID
			jrParamSet.setResultMsg(methodNm);	//Log Method Name
			jrParamSet.setField("YD_WBOOK_ID"			, ydWbookId); 			//야드작업예약ID
			jrParamSet.setField("YD_SCH_CD"  			, ydSchCd  ); 			//야드스케쥴코드
			jrParamSet.setField("YD_EQP_ID"  			, ydEqpId  ); 			//야드설비ID
			jrParamSet.setField("YD_SCH_PRIOR"  		, ydSchPrior  ); 		//야드스케쥴우선순위
			jrParamSet.setField("YD_TO_LOC_DCSN_MTD"  	, ydToLocDcsnMtd  ); 	//야드To위치결정방법
			jrParamSet.setField("YD_TO_LOC_GUIDE"  		, ydToLocGuide  ); 		//야드To위치Guide
			jrParamSet.setField("MODIFIER"   			, modifier ); 			//수정자
			jrParamSet.setField("YD_WBOOK_MTL_CNT"   	, ""+wmMtlSh ); 		//작업예약 매수
			
			/**********************************************************
			* 2.그룹핑 파라미터 셋팅
  			**********************************************************/
			commUtils.printLog(logId, "그룹핑 파라미터 셋팅 시작", "SL");			

			intRtnVal = this.CrnSchGrp(logId, methodNm, jrParamSet, vecResult);
			if(intRtnVal == -1) {
				m_ctx.setRollbackOnly();
				throw new Exception("오류:그룹핑 파라미터 셋팅 없음");
			}
			
			/**********************************************************
			* 2.크레인사양 비교Check
  			**********************************************************/
			commUtils.printLog(logId, "크레인사양 비교Check " + vecResult.size(), "SL");			

			intRtnVal = this.HandledDataCrnSpec(logId, methodNm, ydSchCd, vecResult, vecReResult);
			if(intRtnVal == -1) {
				m_ctx.setRollbackOnly();
				throw new Exception("오류:크레인사양 비교Check");
			}
			/**********************************************************
			* 3.크레인스케줄과 크레인작업재료 등록
  			**********************************************************/
			commUtils.printLog(logId, "크레인스케줄과 크레인작업재료 등록 시작 ", "SL");			
			intRtnVal = this.CrnSchIns(logId, methodNm, jrParamSet, vecReResult);

			if(intRtnVal == -1) {
				m_ctx.setRollbackOnly();
				throw new DAOException("크레인스케줄 및 작업재료 등록 오류");
			}

			/**********************************************************
			* 4.TO 저장위치 결정
  			**********************************************************/
			commUtils.printLog(logId, "TO 저장위치 결정 시작 ", "SL");			
			
			// A동 자동화 창고
			if(ydSchCd.substring(1, 2).equals("A")) {

				intRtnVal = this.LocSrcRngDataSetRbA(logId, methodNm, jrParamSet);
				
				commUtils.printLog(logId, "intRtnVal " + intRtnVal, "SL");			
				if(intRtnVal == -1) {
					m_ctx.setRollbackOnly();
					throw new DAOException("LocSrcRngDataSetRbA :TO 저장위치 등록  오류");
				} else if (intRtnVal == 9) {
					
					
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

					jrYdMsg.setField("YD_EQP_ID"    , "KXTC01"); //야드설비ID(대차)
					jrYdMsg.setField("YD_BAY_GP"	, "A"); 
					jrYdMsg.setField("YD_L2_ID"		, "N4"); 
					jrYdMsg.setField("MODIFIER"    	, "YSSCH"   ); //수정자

					try {
						/**********************************************************
						*  대차스케줄 공대차출발지시 처리 (별도 Transaction 으로 처리)
						**********************************************************/
						EJBConnector tranConn1 = new EJBConnector("default", "GdsYsSchSeEJB", this);
						jrRtn1 = (JDTORecord)tranConn1.trx("updTcarSchLevWo", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
					} catch (Exception se) {}
				}
				
			// B동 일반 창고
			} else {
				
				intRtnVal = this.LocSrcRngDataSetRbB(logId, methodNm, jrParamSet);
				
				if(intRtnVal == -1) {
					m_ctx.setRollbackOnly();
					throw new DAOException("LocSrcRngDataSetRbB :TO 저장위치 등록  오류");
				} else if (intRtnVal == 9) {
					
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

					jrYdMsg.setField("YD_EQP_ID"    , "KXTC01"); //야드설비ID(대차)
					jrYdMsg.setField("YD_BAY_GP"	, "B"); 
					jrYdMsg.setField("YD_L2_ID"		, "N4"); 
					jrYdMsg.setField("MODIFIER"    	, "YSSCH"   ); //수정자
					try {
						/**********************************************************
						*  대차스케줄 공대차출발지시 처리 (별도 Transaction 으로 처리)
						**********************************************************/
						EJBConnector tranConn1 = new EJBConnector("default", "GdsYsSchSeEJB", this);
						jrRtn1 = (JDTORecord)tranConn1.trx("updTcarSchLevWo", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
					} catch (Exception se) {}
				}
				
			}
			
			/**********************************************************
			* 5.크레인작업지시 호출
  			**********************************************************/
			
			if(ydEqpId.substring(2, 4).equals("SC")){
		       	//-------------------------------------------------------------------------------------------------------------
	    		// S-SCRANE 인 경우  작업 예약단위로 TO위치 결정된 정보 만 송신처리함
	        	//-------------------------------------------------------------------------------------------------------------
				JDTORecordSet rsSCrnsch 	= JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord    recSInPara 	= JDTORecordFactory.getInstance().create();
				JDTORecord    recPara 		= JDTORecordFactory.getInstance().create();
	        	String L2SndYn = "N";
	    		recSInPara.setField("YD_WBOOK_ID", ydWbookId);
	    		recSInPara.setField("YD_EQP_ID",   ydEqpId);
	    		rsSCrnsch = commDao.select(recSInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnschByEqpIdandWBookId", logId, methodNm, "크레인스케줄 조회");   		
	    		
	    		for(int Loop_i = 1; Loop_i <= rsSCrnsch.size(); Loop_i++) {
	    			rsSCrnsch.absolute(Loop_i);
	    			recPara = rsSCrnsch.getRecord();		
					if(commUtils.trim(recPara.getFieldString("YS_DN_WO_LOC")).equals("XX010101")) {
						L2SndYn = "N";
					} else {
						L2SndYn = "Y";
					}
	    		}
	    		recPara = JDTORecordFactory.getInstance().create();
	    		if (L2SndYn.equals("Y")) {
		    		for(int Loop_i = 1; Loop_i <= rsSCrnsch.size(); Loop_i++) {
		    			rsSCrnsch.absolute(Loop_i);
		    			recPara = rsSCrnsch.getRecord();	
		    			
		    			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
				
						jrParam.setField("MSG_GP"	, "I"   	); //전문구분
						jrParam.setField("INFO_GP"	, "I"   	); //정보구분
						jrParam.setField("YD_CRN_SCH_ID"	, commUtils.trim(recPara.getFieldString("YD_CRN_SCH_ID")));
						jrRtn1 = commUtils.addSndData(jrRtn1,commDao.getMsgL2("YSN6L006", jrParam));
		    		}
				}
				
			} else {
				if ("W".equals(ydEqpStat)) {
					//야드설비상태가 대기이면 내부크레인작업지시요구 전송
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
					jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ001"               ); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태
					
					jrRtn1 = commUtils.addSndData(jrRtn1,jrYdMsg);
					commUtils.printParam(logId+"99", jrRtn);
				}
			}	
			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn1;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**	
	 *      [A] 오퍼레이션명 : 봉강크레인스케줄 ALLBackUp(YSYSJ314)
	 *
	 * 	@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/

	public JDTORecord rcvYSYSJ314(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강 크레인스케줄 ALLBackUp[GdsYsSchSeEJB.rcvYSYSJ314] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecordSet rsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");

		JDTORecord jrRtn1 = null;	//전문 Return 
		
		int intRtnVal = 0;
		try{
			int wrkBookCnt = Integer.parseInt(commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_CNT")));
			
			for(int i=0; i<wrkBookCnt; i++){
				commUtils.printLog(logId, methodNm, "S+");
				String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
				String ohCrnWbookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"+(i+1)));
				//수신 항목 값
				JDTORecordSet rsSCrnsch 	= JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord    recSInPara 	= JDTORecordFactory.getInstance().create();

	    		recSInPara.setField("YD_WBOOK_ID", ohCrnWbookId);

	    		rsSCrnsch = commDao.select(recSInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbook", logId, methodNm, "작업예약 조회");   		

	    		rsSCrnsch.absolute(1);

	    		JDTORecord recPara = rsSCrnsch.getRecord();		

	    		String sch_cd =commUtils.trim(recPara.getFieldString("YD_SCH_CD"));

				JDTORecord jrYdMsg =  null;

				jrYdMsg = commUtils.getParam(logId, methodNm, rcvMsg.getFieldString("MODIFIER"));;
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name

				jrYdMsg.setField("JMS_TC_CD", "YSYSJ302");
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
				jrYdMsg.setField("YD_WBOOK_ID"       , ohCrnWbookId ); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_CD"         , sch_cd   ); //야드스케쥴코드
				jrYdMsg.setField("YD_EQP_ID"         , ""   ); //야드설비ID
				jrYdMsg.setField("YD_SCH_ST_GP"      , "A" ); //야드스케쥴기동구분
				jrYdMsg.setField("YD_SCH_REQ_GP"     , "M"); //야드스케쥴요청구분

				jrRtn1 = this.rcvYSYSJ302(jrYdMsg);

			}
			
			
			return jrRtn1;
			
		}catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
     * 오퍼레이션명 : 스케줄링 크레인 스케줄 등록
     *  
     * @param  ● vResult, msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int CrnSchIns(String logId, String methodNms , JDTORecord jrParamSet , Vector vecResult) throws JDTOException {
   		String methodNm = "스케줄링 크레인 스케줄 등록[GdsYsSchSeEJB.CrnSchIns] < " + methodNms;
		JDTORecord recInCrn    = null;
		JDTORecord recInTemp   = null;
		JDTORecord recInPara   = null;
		JDTORecord recInBed    = null;
		
		JDTORecordSet rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet outRecset = JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecord recBedLyr = null;
		int intRtnVal = 0;
		String szName = "SYSTEM";
		String szMsg = "";
		String szEqpId = "";
		String szSchCd = "";
		String szWbookId = "";
		String szYD_SCH_PRIOR     = null;
		String szREG_DDTT         = null;
		String szYD_SCH_ST_GP	  = null;
		String szYD_TO_LOC_DCSN_MTD	  = null;
		String szYD_EQP_WRK_MAX_L= "";
		
		try{
			
			commUtils.printLog(logId, methodNm, "S+");
			
			szEqpId  		= commUtils.trim(jrParamSet.getFieldString("YD_EQP_ID"  ));
			szSchCd  		= commUtils.trim(jrParamSet.getFieldString("YD_SCH_CD"  ));
			szWbookId  		= commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_ID"  ));
			szYD_SCH_PRIOR  = commUtils.trim(jrParamSet.getFieldString("YD_SCH_PRIOR"  ));
			
			commUtils.printParam(logId, vecResult);
			
			//작업예약재료조회
			rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("");
			recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_WBOOK_ID", szWbookId);

			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookbyMtlSCHCD 
			SELECT B.SSTL_NO             AS SSTL_NO                                                                  
			      ,B.YD_WBOOK_ID        AS YD_WBOOK_ID                                                             
			      ,B.YS_STK_COL_GP      AS YS_STK_COL_GP                                                           
			      ,B.YS_STK_BED_NO      AS YS_STK_BED_NO                                                           
			      ,B.YS_STK_LYR_NO      AS YS_STK_LYR_NO                                                           
			      ,B.YS_STK_SEQ_NO      AS YS_STK_SEQ_NO                                                           
			      ,B.YD_UP_COLL_SEQ     AS YD_UP_COLL_SEQ   
			      ,TO_CHAR(B.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
			      ,A.YD_STK_LOT_TP      AS YD_STK_LOT_TP                                                           
			      ,A.YD_STK_LOT_CD      AS YD_STK_LOT_CD                                                           
			      ,A.YS_MTL_ITEM        AS YS_MTL_ITEM      
			      ,A.YD_MTL_T           AS YD_MTL_T
			      ,A.YD_MTL_W           AS YD_MTL_W
			      ,A.YD_MTL_L           AS YD_MTL_L
			      ,A.YD_MTL_WT          AS YD_MTL_WT
			      ,C.YD_TO_LOC_GUIDE    AS YD_TO_LOC_GUIDE
			      ,C.YD_SCH_ST_GP       AS YD_SCH_ST_GP
			     ,A.YD_CAR_UPP_LOC_CD
			  FROM TB_YS_STOCK A                                                                               
			      ,(SELECT YD_WBOOK_ID    AS YD_WBOOK_ID                                                       
			              ,YS_STK_COL_GP  AS YS_STK_COL_GP                                                     
			              ,YS_STK_BED_NO  AS YS_STK_BED_NO                                                     
			              ,YS_STK_LYR_NO  AS YS_STK_LYR_NO                                                     
			              ,YS_STK_SEQ_NO  AS YS_STK_SEQ_NO                                                     
			              ,YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ                                                    
			              ,SSTL_NO         AS SSTL_NO
			              ,REG_DDTT       AS REG_DDTT
			         FROM TB_YS_WRKBOOKMTL                                                                      
			        WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID               
			        GROUP BY YD_WBOOK_ID, YS_STK_COL_GP, YS_STK_BED_NO, YS_STK_LYR_NO, YD_UP_COLL_SEQ,YS_STK_SEQ_NO, SSTL_NO, REG_DDTT  
			         ) B,
			        TB_YS_WRKBOOK C
			 WHERE  B.SSTL_NO = A.SSTL_NO(+)
			   AND  B.YD_WBOOK_ID = C.YD_WBOOK_ID
			 ORDER BY A.YD_CAR_UPP_LOC_CD ,YD_UP_COLL_SEQ DESC 
			 */
			rsWrkBookMtl = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookbyMtlSCHCD", logId, methodNm, "크레인스케줄재료정보 조회");
			if (rsWrkBookMtl.size() <= 0) {
				throw new Exception("오류:크레인작업재료조회 >> 조회 Data 없음");
			}			

			rsWrkBookMtl.absolute(1);
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setRecord(rsWrkBookMtl.getRecord());
			
			szREG_DDTT  		= commUtils.trim(recInTemp.getFieldString("REG_DDTT"  ));
			szYD_SCH_ST_GP 		= commUtils.trim(recInTemp.getFieldString("YD_SCH_ST_GP"  ));
			szYD_EQP_WRK_MAX_L  = commUtils.trim(recInTemp.getFieldString("YD_EQP_WRK_MAX_L"  ));
			
			commUtils.printLog(logId, "vecResult.size():" + vecResult.size(), "SL");
			//크레인 스케줄에 Insert한다.	
			
			for(int i = 0; i < vecResult.size(); i++) {

				outRecset = (JDTORecordSet) vecResult.get(i);

				recInCrn = JDTORecordFactory.getInstance().create();
				outRecset.first();
				recInCrn  = outRecset.getRecord();
				
				/**********************************************************
				*  크레인 스케줄 등록
				**********************************************************/			
				//크레인스케줄ID를 할당받는다
				String ydCrnSchId = commDao.getSeqId(logId, methodNm, "CrnSch");

				recInCrn.setField("YD_CRN_SCH_ID",   		 	ydCrnSchId);
				recInCrn.setField("YD_EQP_ID",        			szEqpId);
				recInCrn.setField("YD_GP",            			recInCrn.getFieldString("YS_STK_COL_GP").substring(0,1));
				recInCrn.setField("YD_BAY_GP",        			recInCrn.getFieldString("YS_STK_COL_GP").substring(1,2));
				recInCrn.setField("YD_SCH_CD",        			szSchCd);	
				recInCrn.setField("REGISTER",         			recInCrn.getFieldString("HANDLING_CNT"));	
				recInCrn.setField("YD_SCH_PRIOR",      			szYD_SCH_PRIOR);
				recInCrn.setField("YD_WBOOK_DT",       			szREG_DDTT);
				recInCrn.setField("YD_SCH_ST_GP",      			szYD_SCH_ST_GP);
				
				szYD_TO_LOC_DCSN_MTD =  commUtils.trim(recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD"));
				
				szMsg = "YD_TO_LOC_DCSN_MTD : " +  szYD_TO_LOC_DCSN_MTD;
				commUtils.printLog(logId, szMsg, "SL");
				
				recInCrn.setField("YS_UP_WO_LOC",     recInCrn.getFieldString("YS_STK_COL_GP") + recInCrn.getFieldString("YS_STK_BED_NO"));
				recInCrn.setField("YS_UP_WO_LAYER",   recInCrn.getFieldString("YS_STK_LYR_NO"));
				if(recInCrn.getFieldString("YS_UP_WO_LOC").trim().equals("")){
					szMsg = "권상지시위치가 없습니다.";
					throw new JDTOException(szMsg);
				}
							 	
				recInCrn.setField("YD_WRK_PROG_STAT", "W");
				recInCrn.setField("YD_EQP_WRK_SH", "" + outRecset.size());  //재료매수
				
				
				String szYS_STK_COL_GP =recInCrn.getFieldString("YS_STK_COL_GP");
				String szYS_STK_BED_NO =recInCrn.getFieldString("YS_STK_BED_NO");
				
				
				if("KA".equals(szYS_STK_COL_GP.substring(0 , 2))||"KB".equals(szYS_STK_COL_GP.substring(0 , 2))){
					JDTORecordSet RsBedUpXy = JDTORecordFactory.getInstance().createRecordSet("");
					recInBed= JDTORecordFactory.getInstance().create();
					recInBed.setField("YS_STK_COL_GP", 			szYS_STK_COL_GP); //권상지시위치
					recInBed.setField("YS_STK_BED_NO", 			szYS_STK_BED_NO);	 //권상지시위치
						
					/* Bed정보 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybed 
					SELECT YS_STK_COL_GP 
					      ,YS_STK_BED_NO 
					      ,YD_STR_GTR_CD 
					      ,YS_STK_BED_TP 
					      ,YS_STK_BED_T_GP 
					      ,YS_STK_BED_W_GP 
					      ,YS_STK_BED_L_GP 
					      ,YS_OUTDIA_GRP_GP
					      ,YD_STK_BED_DIR_GP 
					      ,YD_STK_BED_ACT_STAT 
					      ,YD_STK_BED_WHIO_STAT 
					      ,YD_STK_BED_USG_GP
					      ,YD_STK_BED_XAXIS 
					      ,YD_STK_BED_YAXIS
					      ,YD_STK_BED_ZAXIS
					      ,YD_STK_BED_LYR_MAX
					      ,YD_STK_BED_WT_MAX 
					      ,YD_STK_BED_H_MAX 
					      ,YD_STK_BED_L_MAX 
					      ,YD_STK_BED_W_MAX 
					      ,YD_STK_BED_XAXIS_TOL 
					      ,YD_STK_BED_YAXIS_TOL 
					      , (SELECT YD_STK_COL_DIR_GP FROM TB_YS_STKCOL WHERE YS_STK_COL_GP = A.YS_STK_COL_GP AND ROWNUM = 1) AS YD_STK_COL_DIR_GP
					  FROM TB_YS_STKBED
					 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
					   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
					   AND DEL_YN ='N'
						 */  
					RsBedUpXy = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybed", logId, methodNm, "권상 BED 좌표 조회");
					if (RsBedUpXy.size() <= 0) {	 
						
						szMsg = "권상 BED 좌표 조회 검색 실패  ";
						commUtils.printLog(logId, szMsg, "SL");
						
					}else{
						RsBedUpXy.first();
						JDTORecord RecUpBedXy = RsBedUpXy.getRecord();
				 
						 
						/**********************************************************
						* 1. 저장열 방향구분 = “주행방향” 이고 Crane =“ACR1” or “ACR2” or “BCR1” or “BCR2” 이면,
						*   ->  X 좌표값  = 기준값 + (제품길이 /2)::::Y 좌표값  = 기준값 
						* 2. 저장열 방향구분 = “횡행방향” 이고 Crane =“ACR1” or “ACR2” or “BCR1” or “BCR2” 이면,
						*   ->  X 기준값  = 기준값                           ::::Y 좌표값  = 기준값 + (제품길이 /2)
						**********************************************************/
						if(szYS_STK_COL_GP.substring(2, 4).equals("TR")||szYS_STK_COL_GP.substring(2, 4).equals("TC")||szYS_STK_COL_GP.substring(2, 4).equals("TS")) {
							recInCrn.setField("YD_UP_WO_LOC_XAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
							recInCrn.setField("YD_UP_WO_LOC_YAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
							
						} else if(commUtils.trim(RecUpBedXy.getFieldString("YD_STK_COL_DIR_GP")).equals("X") && szEqpId.substring(2, 4).equals("CR")) {
							recInCrn.setField("YD_UP_WO_LOC_XAXIS",      String.valueOf(commUtils.paraRecChkNullInt(RecUpBedXy,"YD_STK_BED_XAXIS")
									                                                     +(Integer.parseInt(szYD_EQP_WRK_MAX_L) /2))) ;
							recInCrn.setField("YD_UP_WO_LOC_YAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
							
						} else if(commUtils.trim(RecUpBedXy.getFieldString("YD_STK_COL_DIR_GP")).equals("Y") && szEqpId.substring(2, 4).equals("CR")) {
							recInCrn.setField("YD_UP_WO_LOC_XAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
							recInCrn.setField("YD_UP_WO_LOC_YAXIS",      String.valueOf(commUtils.paraRecChkNullInt(RecUpBedXy,"YD_STK_BED_YAXIS")
																						 +(Integer.parseInt(szYD_EQP_WRK_MAX_L) /2))) ;
						} else {
							recInCrn.setField("YD_UP_WO_LOC_XAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
							recInCrn.setField("YD_UP_WO_LOC_YAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
						}
						
						recInCrn.setField("YD_UP_WO_LOC_ZAXIS",  		commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_ZAXIS"  )) ) ;
						recInCrn.setField("YD_UP_WO_XAXIS_GAP_MAX",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
						recInCrn.setField("YD_UP_WO_XAXIS_GAP_MIN",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
						recInCrn.setField("YD_UP_WO_YAXIS_GAP_MAX",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
						recInCrn.setField("YD_UP_WO_YAXIS_GAP_MIN",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
						recInCrn.setField("YD_UP_WO_LOC_YAXIS1",  		"" ) ;
						recInCrn.setField("YD_UP_WO_LOC_YAXIS2",  		"" ) ;
						recInCrn.setField("YD_UP_WO_ZAXIS_GAP_MAX",  	"" ) ;
						recInCrn.setField("YD_UP_WO_ZAXIS_GAP_MIN",  	"" ) ;
					}
				}
				
				intRtnVal = commDao.insert(recInCrn, "com.inisteel.cim.ys.common.dao.YsCommDAO.insYdCrnsch", logId, methodNm, "TB_YS_CRNSCH 생성");
				if(intRtnVal < 1) {
					szMsg = "크레인 스케줄 등록중  Error!! ErrorCode: " + intRtnVal;
					commUtils.printLog(logId, szMsg, "SL");
				}

				/**********************************************************
				*  적치단의 재료상태를 권상대기로 변경
				**********************************************************/			
				recBedLyr = JDTORecordFactory.getInstance().create();
				for(int Loop_k = 1; Loop_k <= outRecset.size(); Loop_k++) {
					
					outRecset.absolute(Loop_k);
					recBedLyr.setRecord( outRecset.getRecord() );	
					recBedLyr.setField("YD_STK_LYR_MTL_STAT", "U");
				
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColBedGpUp   
					UPDATE TB_YS_STKLYR            
					   SET MOD_DDTT     = SYSDATE             
					     , MODIFIER     = :V_MODIFIER             
					     , YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT,YD_STK_LYR_MTL_STAT)
					 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
					   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
					   AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO   
					   AND SSTL_NO = :V_SSTL_NO
			    	 */  
					intRtnVal = commDao.update(recBedLyr, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColBedGpUp", logId, methodNm, "TB_YS_STKLYR 갱신");
					
					if(intRtnVal <= 0) {
						commUtils.printLog(logId, "[" + methodNm + "] 적치단[" + recInCrn.getFieldString("YS_STK_COL_GP") + "]활성화중 ERROR 발생", "SL");
						throw new Exception("적치단변경시 오류 발생.");
					}
				}
				
				/**********************************************************
				*  크레인 스케줄 작업재료 등록
				**********************************************************/			
				JDTORecord recInCrnMtl = JDTORecordFactory.getInstance().create();
				recInCrnMtl.setField("YD_CRN_SCH_ID", ydCrnSchId);
				/*
				 * 기존의 MAIN_WRK_YN 은 주작업이 Y 보조작업이 N으로 들어옴 크레인작업재료에는 보조작업여부에 값은 보조작업인경우 Y 주작업인경우 N로 셋팅!
				 */
				if(recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("W")){
					recInCrnMtl.setField("YD_AID_WRK_YN", "Y");
				}else{
					recInCrnMtl.setField("YD_AID_WRK_YN", "N");
				}
				recInCrnMtl.setField("REGISTER", szName);
				recInCrnMtl.setField("MOD_DDTT", "");
				
				recBedLyr = JDTORecordFactory.getInstance().create();
				for(int Loop_k = 1; Loop_k <= outRecset.size(); Loop_k++) {
					
					outRecset.absolute(Loop_k);
					
					recBedLyr  = outRecset.getRecord();
					recInCrnMtl.setField("SSTL_NO"			,  commUtils.trim(recBedLyr.getFieldString("SSTL_NO"  )));
					recInCrnMtl.setField("YS_STK_LYR_NO"	,  "01");
//					if((szSchCd.substring(2,4).equals("PC") || szSchCd.substring(2,4).equals("TY"))||szSchCd.substring(0,2).equals("KB") ) {
//2015.11.23					
					if((recInCrn.getFieldString("YS_STK_COL_GP").substring(0,3).equals("KB0"))||(recInCrn.getFieldString("YS_STK_COL_GP").substring(0,3).equals("KBT"))) {
						recInCrnMtl.setField("YS_STK_SEQ_NO"	, commUtils.trim(recBedLyr.getFieldString("YS_STK_SEQ_NO"  )));
					} else if((szSchCd.substring(2,4).equals("PC") || szSchCd.substring(2,4).equals("TY"))||szSchCd.substring(0,2).equals("KB") ) {
						recInCrnMtl.setField("YS_STK_SEQ_NO"	, ""+Loop_k);
					} else {
						recInCrnMtl.setField("YS_STK_SEQ_NO"	, commUtils.trim(recBedLyr.getFieldString("YS_STK_SEQ_NO"  )));
					}
					
					recInCrnMtl.setField("YD_STK_LOT_TP"	,  commUtils.trim(recBedLyr.getFieldString("YD_STK_LOT_TP"  )));
					recInCrnMtl.setField("YD_STK_LOT_CD"	,  commUtils.trim(recBedLyr.getFieldString("YD_STK_LOT_CD"  )));
					recInCrnMtl.setField("HCR_GP"			,  commUtils.trim(recBedLyr.getFieldString("HCR_GP"  )));
					recInCrnMtl.setField("STL_PROG_CD"		,  commUtils.trim(recBedLyr.getFieldString("STL_PROG_CD"  )));
					recInCrnMtl.setField("YS_MTL_ITEM"		,  commUtils.trim(recBedLyr.getFieldString("YS_MTL_ITEM"  )));
					recInCrnMtl.setField("YS_ROUTE_GP"		,  "");
					recInCrnMtl.setField("YD_TO_LOC_DCSN_MTD"	,  commUtils.trim(recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD")));
								//크레인작업재료 생성
					/* COM.INISTEEL.CIM.YS.COMMON.DAO.YSCOMMDAO.INSYDCRNWRKMTL
					INSERT INTO TB_YS_CRNWRKMTL
					       (
					       YD_CRN_SCH_ID
					      ,SSTL_NO
					      ,REGISTER
					      ,REG_DDTT
					      ,DEL_YN
					      ,YD_AID_WRK_YN
					      ,YS_STK_LYR_NO
					      ,YS_STK_SEQ_NO
					      ,YD_STK_LOT_TP
					      ,YD_STK_LOT_CD
					      ,HCR_GP
					      ,STL_PROG_CD
					      ,YS_MTL_ITEM
					      ,YS_ROUTE_GP
					      ,YD_TO_LOC_DCSN_MTD
					       )
					VALUES (
					       :V_YD_CRN_SCH_ID
					      ,:V_SSTL_NO
					      ,:V_REGISTER
					      ,SYSDATE
					      ,'N'
					      ,:V_YD_AID_WRK_YN
					      ,:V_YS_STK_LYR_NO
					      ,:V_YS_STK_SEQ_NO
					      ,:V_YD_STK_LOT_TP
					      ,:V_YD_STK_LOT_CD
					      ,:V_HCR_GP
					      ,:V_STL_PROG_CD
					      ,:V_YS_MTL_ITEM
					      ,:V_YS_ROUTE_GP
					      ,:V_YD_TO_LOC_DCSN_MTD
					       )
					  */     
					intRtnVal = commDao.insert(recInCrnMtl, "com.inisteel.cim.ys.common.dao.YsCommDAO.insYdCrnwrkmtl", logId, methodNm, "TB_YS_CRNWRKMTL 생성");
					if(intRtnVal <= 0) {
						szMsg = "크레인 스케줄 작업재료 등록중 실패: " + intRtnVal;
						commUtils.printLog(logId, szMsg, "SL");
						throw new Exception(szMsg);
					}
					
					if(intRtnVal <= 0) {
						commUtils.printLog(logId, "[" + methodNm + "] 적치단[" + recInCrn.getFieldString("YS_STK_COL_GP") + "]활성화중 ERROR 발생", "SL");
						throw new Exception("적치단변경시 오류 발생.");
					}
				
				}	
//				}
			}	
			commUtils.printLog(logId, methodNm, "S-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return YsConstant.RETN_INT_SUCCESS;
    }//end of CrnSchIns()
	
	
	/**
	 *      [A] 오퍼레이션명 : 대차스케줄 공대차출발지시 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord updTcarSchLevWo(JDTORecord jrParam) throws DAOException {
		String methodNm = "대차스케줄 공대차출발지시 처리[GdsYsSchSeEJB.updTcarSchLevWo] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {

			return commUtils.addSndData(YsComm.trtTcarSchLevWo(jrParam));
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 대차 출발처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void updCrnSchWB(JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인스케줄 작업예약재료 수정[GdsYsSchSeEJB.updCrnSchWB] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updWmStrLoc
				--크레인스케줄 작업예약재료 저장위치 수정
				 MERGE INTO TB_YS_WRKBOOKMTL WM USING (
				 SELECT YD_WBOOK_ID
				       ,SSTL_NO
				       ,:V_MODIFIER AS MODIFIER
				       ,SYSDATE     AS MOD_DDTT
				       ,YS_STK_COL_GP
				       ,YS_STK_BED_NO
				       ,YS_STK_LYR_NO
				       ,YS_STK_SEQ_NO
				       ,YD_UP_COLL_SEQ 
				   FROM (SELECT WM.*
				           FROM (SELECT WB.YD_WBOOK_ID
				                       ,WB.YD_SCH_CD
				                       ,WM.SSTL_NO
				                       ,SL.YS_STK_COL_GP
				                       ,SL.YS_STK_BED_NO
				                       ,SL.YS_STK_LYR_NO
				                       ,SL.YS_STK_SEQ_NO
				                       ,SL.YS_STK_COL_GP||SL.YS_STK_BED_NO||SL.YS_STK_LYR_NO AS YS_STR_LOC
				                       ,SL.YS_STK_COL_GP||SL.YS_STK_BED_NO AS YD_STK_COL_BED
				                       ,RANK() OVER(PARTITION BY SL.YS_STK_COL_GP,SL.YS_STK_BED_NO
				                                        ORDER BY SL.YS_STK_COL_GP,SL.YS_STK_BED_NO,SL.YS_STK_LYR_NO) AS YD_UP_COLL_SEQ
				                       
				                   FROM TB_YS_WRKBOOK WB
				                       ,TB_YS_WRKBOOKMTL WM
				                       ,TB_YS_STKLYR     SL
				                       ,TB_YS_STOCK      ST
				                  WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
				                    AND WM.SSTL_NO      = SL.SSTL_NO
				                    AND WM.SSTL_NO      = ST.SSTL_NO
				                    AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				                    AND WB.DEL_YN      = 'N'
				                    AND WM.DEL_YN      = 'N'
				                    AND SL.YD_STK_LYR_MTL_STAT = 'C'
				                    AND WB.YD_GP=SUBSTR(SL.YS_STK_COL_GP,1,1)
				                  ORDER BY YS_STR_LOC DESC) WM
				          ORDER BY YS_STR_LOC DESC)
				 ) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.SSTL_NO = DD.SSTL_NO)
				 WHEN MATCHED THEN UPDATE SET
				      WM.MODIFIER       = DD.MODIFIER
				     ,WM.MOD_DDTT       = DD.MOD_DDTT
				     ,WM.YS_STK_COL_GP  = DD.YS_STK_COL_GP
				     ,WM.YS_STK_BED_NO  = DD.YS_STK_BED_NO
				     ,WM.YS_STK_LYR_NO  = DD.YS_STK_LYR_NO
				     ,WM.YS_STK_SEQ_NO  = DD.YS_STK_SEQ_NO
				     ,WM.YD_UP_COLL_SEQ = DD.YD_UP_COLL_SEQ
			    */ 
			
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updWmStrLoc", logId, methodNm, "작업예약재료 저장위치 수정");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	/**
	 *      [A] 오퍼레이션명 : 저장품 등록 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void updStock(JDTORecord jrParam) throws DAOException {
		String methodNm = "저장품 정보 수정[GdsYsSchSeEJB.updStock] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYsStock 
			UPDATE TB_YS_STOCK
			   SET YD_RCPT_PLN_STR_LOC = NVL(:V_YD_RCPT_PLN_STR_LOC,YD_RCPT_PLN_STR_LOC)
			     , OFF_RSN             = NVL(:V_OFF_RSN, OFF_RSN)
			     , MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			 WHERE SSTL_NO           = :V_SSTL_NO  
		    */ 
			
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsStock", logId, methodNm, "저장품 입고예정위치 수정");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	

	/**
	 * 오퍼레이션명 :  크레인 스케줄 GROUPING PARAMETER DATA SETTING
	 *  
	 * @param  String     szEqpId, szSchCd, rsMinWrkBookMtl, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public int CrnSchGrp(String logId, String methodNms ,JDTORecord recMainPara, Vector vecResult)throws JDTOException  {
		String methodNm = "크레인 스케줄 GROUPING[GdsYsSchSeEJB.CrnSchGrp] < " + methodNms;
		String LocalmethodNm = "크레인 스케줄 GROUPING[GdsYsSchSeEJB.CrnSchGrp] ";
		JDTORecordSet rsSelBed       = JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecord recInPara     = null;
		JDTORecord recPara       = null;
		JDTORecord recStkLyr     = null;
		JDTORecordSet rsResult    = JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsWrkbookCol= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsReturn    = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		JDTORecord 		recYN 		= null; // YN_flag
		JDTORecord 		rYN     	= null; // YN_flag
		JDTORecordSet 	rsYN 		= null; // YN_flag
		String 			szYNFlag    = null; // YN_flag
		
		int intYdUpCollSeq = 0;
		int intHandlingCnt = 1;

		String szGROUP_GP = "";
		
		try {

			commUtils.printLog(logId, methodNm, "S+");			
			

			//------------------------------------------------------------------------------------------------------------
			//	크레인스케줄재료정보 조회
			//------------------------------------------------------------------------------------------------------------
			 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookBedUsgCd 
			SELECT YD_WBOOK_ID        AS YD_WBOOK_ID
			      ,YS_STK_COL_GP      AS YS_STK_COL_GP
			      ,(SELECT NVL(YD_STKBED_USG_CD,'V2') FROM  TB_YS_STKCOL WHERE YS_STK_COL_GP = A.YS_STK_COL_GP) AS YD_STKBED_USG_CD
			  FROM TB_YS_WRKBOOKMTL A
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND DEL_YN='N'
			 GROUP BY YD_WBOOK_ID, YS_STK_COL_GP
			 ORDER BY YS_STK_COL_GP
			 */
			JDTORecordSet rsWrkbookmtl = commDao.select(recMainPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookBedUsgCd", logId, methodNm, "크레인스케줄재료정보 조회");
			if (rsWrkbookmtl.size() <= 0) {
				throw new Exception("오류:크레인작업재료조회 >> 조회 Data 없음");
			}

			for(int Loop_i = 1; Loop_i <= rsWrkbookmtl.size(); Loop_i++) {
				rsWrkbookmtl.absolute(Loop_i);
				recPara = rsWrkbookmtl.getRecord();

				String szBedchk = commUtils.trim(recPara.getFieldString("YS_STK_COL_GP"));
				commUtils.printLog(logId,  "szBedchk: " + szBedchk, "SL");
							
				if(szBedchk.substring(2, 4).equals("TC")) {
					// 단까지 GROUP
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("GROUP_GP"     , "LYR");
					recInPara.setField("YD_WBOOK_ID"  , commUtils.trim(recPara.getFieldString("YD_WBOOK_ID")));  
					recInPara.setField("YS_STK_COL_GP", commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));
					/*com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpBedLyrGroupTC
					SELECT YD_WBOOK_ID        AS YD_WBOOK_ID
					      ,YS_STK_COL_GP      AS YS_STK_COL_GP
					      ,YS_STK_BED_NO      AS YS_STK_BED_NO
					      ,YS_STK_LYR_NO      AS YS_STK_LYR_NO
					      ,MIN(YS_STK_SEQ_NO) AS YS_STK_SEQ_NO
					      ,:V_GROUP_GP               AS GROUP_GP               
					  FROM TB_YS_WRKBOOKMTL
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND YS_STK_COL_GP = :V_YS_STK_COL_GP
					   AND DEL_YN='N'
					 GROUP BY YD_WBOOK_ID, YS_STK_COL_GP, YS_STK_BED_NO, YS_STK_LYR_NO
					 ORDER BY YS_STK_COL_GP, YS_STK_BED_NO, YS_STK_LYR_NO DESC, YS_STK_SEQ_NO 
					 */
					rsWrkbookCol = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpBedLyrGroupTC", logId, methodNm, "크레인스케줄재료정보 조회");
				
				} else if(szBedchk.substring(0, 3).equals("KA0")) {
					// 단까지 GROUP
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("GROUP_GP"     , "LYR");
					recInPara.setField("YD_WBOOK_ID"  , commUtils.trim(recPara.getFieldString("YD_WBOOK_ID")));  
					recInPara.setField("YS_STK_COL_GP", commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));
					/*com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpBedLyrGroup
					SELECT YD_WBOOK_ID        AS YD_WBOOK_ID
					      ,YS_STK_COL_GP      AS YS_STK_COL_GP
					      ,YS_STK_BED_NO      AS YS_STK_BED_NO
					      ,YS_STK_LYR_NO      AS YS_STK_LYR_NO
					      ,MIN(YS_STK_SEQ_NO) AS YS_STK_SEQ_NO
					      ,:V_GROUP_GP               AS GROUP_GP               
					  FROM TB_YS_WRKBOOKMTL
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND YS_STK_COL_GP = :V_YS_STK_COL_GP
					   AND DEL_YN='N'
					 GROUP BY YD_WBOOK_ID, YS_STK_COL_GP, YS_STK_BED_NO, YS_STK_LYR_NO
					 ORDER BY YS_STK_COL_GP, YS_STK_BED_NO, YS_STK_LYR_NO DESC, YS_STK_SEQ_NO 
					 */
					rsWrkbookCol = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpBedLyrGroup", logId, methodNm, "크레인스케줄재료정보 조회");

				}  else if(szBedchk.substring(0, 4).equals("KATY") || szBedchk.substring(0, 4).equals("KBTY")) {
					// 단까지 GROUP
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("GROUP_GP"     , "LYR_TY");
					recInPara.setField("YD_WBOOK_ID"  , commUtils.trim(recPara.getFieldString("YD_WBOOK_ID")));  
					recInPara.setField("YS_STK_COL_GP", commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));
					/*com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpBedLyrGroup
					SELECT YD_WBOOK_ID        AS YD_WBOOK_ID
					      ,YS_STK_COL_GP      AS YS_STK_COL_GP
					      ,YS_STK_BED_NO      AS YS_STK_BED_NO
					      ,YS_STK_LYR_NO      AS YS_STK_LYR_NO
					      ,MIN(YS_STK_SEQ_NO) AS YS_STK_SEQ_NO
					      ,:V_GROUP_GP               AS GROUP_GP               
					  FROM TB_YS_WRKBOOKMTL
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND YS_STK_COL_GP = :V_YS_STK_COL_GP
					   AND DEL_YN='N'
					 GROUP BY YD_WBOOK_ID, YS_STK_COL_GP, YS_STK_BED_NO, YS_STK_LYR_NO
					 ORDER BY YS_STK_COL_GP, YS_STK_BED_NO, YS_STK_LYR_NO DESC, YS_STK_SEQ_NO 
					 */
					rsWrkbookCol = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpBedLyrGroup", logId, methodNm, "크레인스케줄재료정보 조회");

				}else if((szBedchk.substring(0, 3).equals("KAP"))||(szBedchk.substring(0, 3).equals("KBP"))) {			
				
					//입고는 열 까지 GROUP
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("GROUP_GP"     , "COL");
					recInPara.setField("YD_WBOOK_ID"  , commUtils.trim(recPara.getFieldString("YD_WBOOK_ID")));  
					recInPara.setField("YS_STK_COL_GP", commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));

					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpGroup
					SELECT YD_WBOOK_ID        AS YD_WBOOK_ID
					      ,YS_STK_COL_GP      AS YS_STK_COL_GP
					      ,MIN(YS_STK_LYR_NO) AS YS_STK_LYR_NO
					      ,'V1'               AS YD_STKBED_USG_CD
					      ,:V_GROUP_GP               AS GROUP_GP  
					  FROM TB_YS_WRKBOOKMTL
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND YS_STK_COL_GP = :V_YS_STK_COL_GP
					   AND DEL_YN='N'
					 GROUP BY YD_WBOOK_ID, YS_STK_COL_GP
					 ORDER BY YS_STK_COL_GP DESC
					 */
					rsWrkbookCol = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpGroup", logId, methodNm, "크레인스케줄재료정보 조회");

				} else if(szBedchk.substring(0, 3).equals("KB0")) {		
					
				    //봉강 B동 일 경우 동일 단에서는  보조 작업이 발생하지 않는다 
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("GROUP_GP"     , "BED_B");
					recInPara.setField("YD_WBOOK_ID"  , commUtils.trim(recPara.getFieldString("YD_WBOOK_ID")));  
					recInPara.setField("YS_STK_COL_GP", commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));

					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpBedGroup 
					SELECT YD_WBOOK_ID        AS YD_WBOOK_ID
					      ,YS_STK_COL_GP      AS YS_STK_COL_GP
					      ,YS_STK_BED_NO      AS YS_STK_BED_NO
					      ,MIN(YS_STK_LYR_NO) AS YS_STK_LYR_NO
					      ,'V2'               AS YD_STKBED_USG_CD
					      ,:V_GROUP_GP               AS GROUP_GP
					  FROM TB_YS_WRKBOOKMTL
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND YS_STK_COL_GP = :V_YS_STK_COL_GP
					   AND DEL_YN='N'
					 GROUP BY YD_WBOOK_ID, YS_STK_COL_GP, YS_STK_BED_NO
					 ORDER BY YS_STK_COL_GP, YS_STK_BED_NO DESC
					*/ 
					rsWrkbookCol = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpBedGroup", logId, methodNm, "크레인스케줄재료정보 조회");

				} else {
					
				    //기타  BED 까지 GROUP	 
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("GROUP_GP"     , "BED");
					recInPara.setField("YD_WBOOK_ID"  , commUtils.trim(recPara.getFieldString("YD_WBOOK_ID")));  
					recInPara.setField("YS_STK_COL_GP", commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));

					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpBedGroup 
					SELECT YD_WBOOK_ID        AS YD_WBOOK_ID
					      ,YS_STK_COL_GP      AS YS_STK_COL_GP
					      ,YS_STK_BED_NO      AS YS_STK_BED_NO
					      ,MIN(YS_STK_LYR_NO) AS YS_STK_LYR_NO
					      ,'V2'               AS YD_STKBED_USG_CD
					      ,:V_GROUP_GP               AS GROUP_GP
					  FROM TB_YS_WRKBOOKMTL
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND YS_STK_COL_GP = :V_YS_STK_COL_GP
					   AND DEL_YN='N'
					 GROUP BY YD_WBOOK_ID, YS_STK_COL_GP, YS_STK_BED_NO
					 ORDER BY YS_STK_COL_GP, YS_STK_BED_NO DESC
					 */
					rsWrkbookCol = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpBedGroup", logId, methodNm, "크레인스케줄재료정보 조회");
				
				}	
				if (rsWrkbookCol.size() <= 0) {
					throw new Exception("오류:크레인작업재료조회 >> 조회 Data 없음");
				}
				for(int Loop_j = 1; Loop_j <= rsWrkbookCol.size(); Loop_j++) {
					rsWrkbookCol.absolute(Loop_j);
					//
					rsSelBed.addRecord(rsWrkbookCol.getRecord());
				}

			}
			commUtils.printParam(logId, rsSelBed);
			//------------------------------------------------------------------------------------------------------------
			// 열별 베드별로  최하단 정보를 조회해서  
			// 각 재료의 레코드에 주작업/보조작업, TO위치결정방법 파라미터 설정
			//------------------------------------------------------------------------------------------------------------
			
			
			String szLocChkbef = "";
			String szLocChk = "";
    					
			for(int Loop_i = 1; Loop_i <= rsSelBed.size(); Loop_i++) {
				
				rsSelBed.absolute(Loop_i);  //적치Bed를 조회한다.
				recPara = rsSelBed.getRecord();				
	
				szGROUP_GP = commUtils.trim(recPara.getFieldString("GROUP_GP"));
				
				if(szGROUP_GP.equals("COL")) {

					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("YD_WBOOK_ID",        commUtils.trim(recPara.getFieldString("YD_WBOOK_ID")));
					recInPara.setField("YS_STK_COL_GP",      commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getWBookIdGroupCol 
					SELECT X.SSTL_NO        AS SSTL_NO                                
					     , X.YS_STK_COL_GP  AS YS_STK_COL_GP                         
					     , X.YS_STK_BED_NO  AS YS_STK_BED_NO                         
					     , X.YS_STK_LYR_NO  AS YS_STK_LYR_NO                         
					     , X.YS_STK_SEQ_NO  AS YS_STK_SEQ_NO                         
					     , X.YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ                        
					     , Y.YD_MTL_T       AS YD_MTL_T                              
					     , Y.YD_MTL_W       AS YD_MTL_W                              
					     , Y.YD_MTL_L       AS YD_MTL_L                              
					     , Y.YD_MTL_WT      AS YD_MTL_WT                             
					     , Y.YD_STK_LOT_TP  AS YD_STK_LOT_TP                         
					     , Y.YD_STK_LOT_CD  AS YD_STK_LOT_CD                         
					     , Y.YS_MTL_ITEM    AS YS_MTL_ITEM
					     , Y.HCR_GP         AS HCR_GP
					     , Y.STL_PROG_CD    AS STL_PROG_CD
					     , X.YD_STK_LYR_MTL_STAT AS YD_STK_LYR_MTL_STAT              
					  FROM (SELECT A.SSTL_NO        AS SSTL_NO                  
					              ,A.YS_STK_COL_GP  AS YS_STK_COL_GP           
					              ,A.YS_STK_BED_NO  AS YS_STK_BED_NO           
					              ,A.YS_STK_LYR_NO  AS YS_STK_LYR_NO           
					              ,A.YS_STK_SEQ_NO  AS YS_STK_SEQ_NO           
					              ,B.YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ          
					              ,A.YD_STK_LYR_MTL_STAT AS YD_STK_LYR_MTL_STAT     
					          FROM TB_YS_STKLYR A                                   
					              ,(SELECT SSTL_NO                                   
					                     , YD_UP_COLL_SEQ                           
					                     , DEL_YN                                   
					                  FROM TB_YS_WRKBOOKMTL                         
					                 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID ) B     
					         WHERE A.SSTL_NO           = B.SSTL_NO              
					           AND A.YS_STK_COL_GP     = :V_YS_STK_COL_GP
					           AND A.YD_STK_LYR_MTL_STAT = 'C'
					           AND (A.DEL_YN <> 'Y' OR A.DEL_YN IS NULL)            
					           AND (B.DEL_YN <> 'Y' OR B.DEL_YN IS NULL)
					        ) X        
					      ,TB_YS_STOCK Y                                            
					 WHERE X.SSTL_NO = Y.SSTL_NO                                      
					   AND (Y.DEL_YN <> 'Y' OR Y.DEL_YN IS NULL)      
					ORDER BY X.YS_STK_COL_GP, CASE WHEN X.YS_STK_COL_GP IN ('KAPC02') THEN X.YS_STK_BED_NO ELSE '0' END DESC 
					                        , CASE WHEN X.YS_STK_COL_GP IN ('KAPC02') THEN '0'             ELSE X.YS_STK_BED_NO END  
					                        , X.YS_STK_LYR_NO DESC
 					*/                        
					rsResult = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWBookIdGroupCol", logId, methodNm, "BED 정보 조회");
					if (rsResult.size() <= 0) {
						throw new Exception("오류:BED정보조회 >> [" + szGROUP_GP + "] 조회 Data 없음");
					}		
					
				} else if(szGROUP_GP.equals("BED")) {
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("YD_WBOOK_ID",        commUtils.trim(recPara.getFieldString("YD_WBOOK_ID")));
					recInPara.setField("YS_STK_COL_GP",      commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));
					recInPara.setField("YS_STK_BED_NO",      commUtils.trim(recPara.getFieldString("YS_STK_BED_NO")));
					recInPara.setField("YS_STK_LYR_NO",      commUtils.trim(recPara.getFieldString("YS_STK_LYR_NO")));  

					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getWBookIdGroupBed
					SELECT X.SSTL_NO         AS SSTL_NO                                
					     , X.YS_STK_COL_GP  AS YS_STK_COL_GP                         
					     , X.YS_STK_BED_NO  AS YS_STK_BED_NO                         
					     , X.YS_STK_LYR_NO  AS YS_STK_LYR_NO                         
					     , X.YS_STK_SEQ_NO  AS YS_STK_SEQ_NO                         
					     , X.YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ                        
					     , Y.YD_MTL_T       AS YD_MTL_T                              
					     , Y.YD_MTL_W       AS YD_MTL_W                              
					     , Y.YD_MTL_L       AS YD_MTL_L                              
					     , Y.YD_MTL_WT      AS YD_MTL_WT                             
					     , Y.YD_STK_LOT_TP  AS YD_STK_LOT_TP                         
					     , Y.YD_STK_LOT_CD  AS YD_STK_LOT_CD                         
					     , Y.YS_MTL_ITEM    AS YS_MTL_ITEM
					     , Y.HCR_GP         AS HCR_GP
					     , Y.STL_PROG_CD    AS STL_PROG_CD
					     , X.YD_STK_LYR_MTL_STAT AS YD_STK_LYR_MTL_STAT              
					  FROM (SELECT A.SSTL_NO             AS SSTL_NO                  
					              ,A.YS_STK_COL_GP       AS YS_STK_COL_GP           
					              ,A.YS_STK_BED_NO       AS YS_STK_BED_NO           
					              ,A.YS_STK_LYR_NO       AS YS_STK_LYR_NO           
					              ,A.YS_STK_SEQ_NO       AS YS_STK_SEQ_NO           
					              ,B.YD_UP_COLL_SEQ      AS YD_UP_COLL_SEQ          
					              ,A.YD_STK_LYR_MTL_STAT AS YD_STK_LYR_MTL_STAT     
					          FROM TB_YS_STKLYR A                                   
					              ,(SELECT SSTL_NO                                   
					                     , YD_UP_COLL_SEQ                           
					                     , DEL_YN                                   
					                  FROM TB_YS_WRKBOOKMTL                         
					                 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID ) B     
					         WHERE A.SSTL_NO              = B.SSTL_NO(+)              
					           AND A.YS_STK_COL_GP       = :V_YS_STK_COL_GP      
					           AND A.YS_STK_BED_NO       = :V_YS_STK_BED_NO     
					           AND A.YS_STK_LYR_NO      >= :V_YS_STK_LYR_NO   
					           AND A.YD_STK_LYR_MTL_STAT = 'C' 
					           AND (A.DEL_YN <> 'Y' OR A.DEL_YN IS NULL)            
					           AND (B.DEL_YN <> 'Y' OR B.DEL_YN IS NULL)) X        
					      ,TB_YS_STOCK Y                                            
					 WHERE X.SSTL_NO = Y.SSTL_NO                                      
					   AND (Y.DEL_YN <> 'Y' OR Y.DEL_YN IS NULL)                    
					 ORDER BY X.YS_STK_COL_GP, X.YS_STK_BED_NO, X.YS_STK_LYR_NO DESC,X.YS_STK_SEQ_NO 
					 */
					rsResult = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWBookIdGroupBed", logId, methodNm, "BED 정보 조회");
					if (rsResult.size() <= 0) {
						throw new Exception("오류:BED정보조회 >> [" + szGROUP_GP + "] 조회 Data 없음");
					}					
				} else if(szGROUP_GP.equals("BED_B")) {
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("YD_WBOOK_ID",        commUtils.trim(recPara.getFieldString("YD_WBOOK_ID")));
					recInPara.setField("YS_STK_COL_GP",      commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));
					recInPara.setField("YS_STK_BED_NO",      commUtils.trim(recPara.getFieldString("YS_STK_BED_NO")));
					recInPara.setField("YS_STK_LYR_NO",      commUtils.trim(recPara.getFieldString("YS_STK_LYR_NO")));  


					rsResult = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWBookIdGroupBed_B", logId, methodNm, "BED 정보 조회");
					if (rsResult.size() <= 0) {
						throw new Exception("오류:BED정보조회 >> [" + szGROUP_GP + "] 조회 Data 없음");
					}					
				} else if(szGROUP_GP.equals("LYR")) {
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("YD_WBOOK_ID",        commUtils.trim(recPara.getFieldString("YD_WBOOK_ID")));
					recInPara.setField("YS_STK_COL_GP",      commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));
					recInPara.setField("YS_STK_BED_NO",      commUtils.trim(recPara.getFieldString("YS_STK_BED_NO")));
					recInPara.setField("YS_STK_LYR_NO",      commUtils.trim(recPara.getFieldString("YS_STK_LYR_NO")));  
					recInPara.setField("YS_STK_SEQ_NO",      commUtils.trim(recPara.getFieldString("YS_STK_SEQ_NO")));  
	
					rsResult = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWBookIdGroupLyrRbA", logId, methodNm, "BED 정보 조회");
					if (rsResult.size() <= 0) {
						throw new Exception("오류:BED정보조회 >> [" + szGROUP_GP + "] 조회 Data 없음");
					}					
				}  else if(szGROUP_GP.equals("LYR_TY")) {
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("YD_WBOOK_ID",        commUtils.trim(recPara.getFieldString("YD_WBOOK_ID")));
					recInPara.setField("YS_STK_COL_GP",      commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));
					recInPara.setField("YS_STK_BED_NO",      commUtils.trim(recPara.getFieldString("YS_STK_BED_NO")));
					recInPara.setField("YS_STK_LYR_NO",      commUtils.trim(recPara.getFieldString("YS_STK_LYR_NO")));  
					recInPara.setField("YS_STK_SEQ_NO",      commUtils.trim(recPara.getFieldString("YS_STK_SEQ_NO")));
					
					if(Loop_i==1){ // 작업예약의 제일윗단 스케줄을 만들때 더미작업 생성됨, 그 다음 단 스케줄을 만들때는 더미작업이 생성되면 안되도록 
						recInPara.setField("IS_FIRST",      	 "Y");	
					}else{
						recInPara.setField("IS_FIRST",      	 "N");
					}
					
					rsResult = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWBookIdGroupLyrTYRb", logId, methodNm, "BED 정보 조회");
					if (rsResult.size() <= 0) {
						throw new Exception("오류:BED정보조회 >> [" + szGROUP_GP + "] 조회 Data 없음");
					}					
				}

				//-------------------------------------------------------------------------------------------------------------
				//	Handling 분리
				//-------------------------------------------------------------------------------------------------------------

	    		for(int Loop_j = 1; Loop_j <= rsResult.size(); Loop_j++) {
	    			rsResult.absolute(Loop_j);
	    			recStkLyr = rsResult.getRecord();

	    			//Bed순서
	    			recStkLyr.setField("YD_WBOOK_ID"	, recPara.getFieldString("YD_WBOOK_ID"));
//	    			recStkLyr.setField("HANDLING_CNT"	, ""+intHandlingCnt); 	    				//HandlingCount
//		    		intHandlingCnt++;  		    		//핸들링 카운트 증가
		    		
	    			//주작업여부판단
		    		if(szGROUP_GP.equals("COL")) {
		    			
		    			szLocChk = commUtils.trim(recStkLyr.getFieldString("YS_STK_COL_GP")); 
		    			
		    		} else if(szGROUP_GP.equals("BED")) {

		    			szLocChk = commUtils.trim(recStkLyr.getFieldString("YS_STK_COL_GP")) 
		    			         + commUtils.trim(recStkLyr.getFieldString("YS_STK_BED_NO")); 
		    			
		    		} else if(szGROUP_GP.equals("BED_B")) {
		    			if(commUtils.trim(recStkLyr.getFieldString("YS_STK_COL_GP")).substring(0,3).equals("KB0")) {
			    			szLocChk = commUtils.trim(recStkLyr.getFieldString("YS_STK_COL_GP")) 
			         			 + commUtils.trim(recStkLyr.getFieldString("YS_STK_BED_NO"))
			         			 + commUtils.trim(recStkLyr.getFieldString("YS_STK_LYR_NO"))
			         			 ; 
		    			} else {
			    			szLocChk = commUtils.trim(recStkLyr.getFieldString("YS_STK_COL_GP")) 
			    			         + commUtils.trim(recStkLyr.getFieldString("YS_STK_BED_NO"));
		    			}
		    			
		    		} else if(szGROUP_GP.equals("LYR")) {

		    			szLocChk = commUtils.trim(recStkLyr.getFieldString("YS_STK_COL_GP")) 
   			         			 + commUtils.trim(recStkLyr.getFieldString("YS_STK_BED_NO"))
   			         			 + commUtils.trim(recStkLyr.getFieldString("YS_STK_LYR_NO"))
   			         			 ; 

		    		} else if(szGROUP_GP.equals("LYR_TY")) {
		    			szLocChk = commUtils.trim(recStkLyr.getFieldString("YS_STK_COL_GP")) 
   			         			 + commUtils.trim(recStkLyr.getFieldString("YS_STK_BED_NO"))
   			         			 + commUtils.trim(recStkLyr.getFieldString("YS_STK_LYR_NO"))
   			         			 ; 
		    		}
		    		// 최초
		    		if(Loop_i == 1 && Loop_j == 1) {
		    			szLocChkbef = szLocChk;
		    		}
		    			
		    		commUtils.printLog(logId, LocalmethodNm+ "szLocChkbef : " + szLocChkbef + ", szLocChk : " + szLocChk, "SL");
    				 		
		    		
	    			if(!szLocChkbef.equals(szLocChk)) {         
		    			intHandlingCnt++;  	
		    			recStkLyr.setField("HANDLING_CNT"	, ""+intHandlingCnt); 	    				//HandlingCount
		    			szLocChkbef = szLocChk;
	    			} else {
	    				recStkLyr.setField("HANDLING_CNT"	, ""+intHandlingCnt); 	    				//HandlingCount
	    			}

	    			//주작업여부판단
	    			if(commUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ") > 0) {          //주작업

	    				recStkLyr.setField("MAIN_WRK_YN", "Y");
	    				recStkLyr.setField("UP_COLL_BASE", "" + (rsResult.size() - intYdUpCollSeq + 1));     	//권상모음Base
						recStkLyr.setField("YD_TO_LOC_DCSN_MTD", "M");
						recStkLyr.setField("UP_COLL_SSTL_NO", "");
						recStkLyr.setField("YD_UP_COLL_SEQ", ""+commUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ"));
		    			
	    			}else{
	    				//보조작업
	    				recStkLyr.setField("MAIN_WRK_YN", "N");
	    				recStkLyr.setField("YD_TO_LOC_DCSN_MTD", "W");
	    				recStkLyr.setField("UP_COLL_SSTL_NO","");
	    				recStkLyr.setField("UP_COLL_BASE","");
	    				recStkLyr.setField("YD_UP_COLL_SEQ", ""+commUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ"));
	    			}
	    			
	    			rsReturn.addRecord(recStkLyr);

	    		}//end of for
			}//end of for
			
			commUtils.printParam(logId, rsReturn);
			
			JDTORecordSet rsHandling = null;
			
			int intHANDLING_CNT = 0;
			int intBefHANDLING_CNT = 0;
			
			int intGROUPLING_CNT = 0;
			int intGROUPLING_LIMIT = 3;
			
			String szMAIN_WRK_YN = "";
			String szBefMAIN_WRK_YN = "";
			
			// flag 임시처리 BO 20200916
			recYN = JDTORecordFactory.getInstance().create();
			rsYN = commDao.select(recYN, "com.inisteel.cim.ys.gds.session.GdsYsSchSeEJBBean.CrnSchGrp.YNflag", logId, methodNm, "YN_FLAG 그룹핑 3개 제한 FLAG 조회");
			rsYN.first();
			rYN = rsYN.getRecord();
			szYNFlag = commUtils.trim(rYN.getFieldString("YN_FLAG")); // YN_flag

			if (rsYN.size() > 0) {
				commUtils.printLog(logId, LocalmethodNm+ "YN_FLAG 그룹핑 3개 제한 FLAG 조회된 FLAG : "+szYNFlag + "-> Y : 신규로직", "SL");
			}					
			
			if("Y".equals(szYNFlag)){
				
		   		for (int Loop_i = 1; Loop_i <= rsReturn.size(); Loop_i++) {
		   			rsReturn.absolute(Loop_i);
	    			recPara = rsReturn.getRecord();
	    			//	권상모음순서
	    			intHANDLING_CNT = commUtils.paraRecChkNullInt(recPara,"HANDLING_CNT");
	    			szMAIN_WRK_YN   = commUtils.trim(recPara.getFieldString("MAIN_WRK_YN"));
	    			
					//-------------------------------------------------------------------------------------------------------------
	    			//	처음에 새그룹 생성
					//-------------------------------------------------------------------------------------------------------------
	    			if (Loop_i == 1) {
	    				
	    				rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    				rsHandling.addRecord(recPara);
	    				vecResult.add(rsHandling) ;
	    				
	    				intBefHANDLING_CNT = intHANDLING_CNT;
	    				szBefMAIN_WRK_YN   = szMAIN_WRK_YN;
	
	    				intGROUPLING_CNT = 1; // 그룹핑 카운트 초기화
	    				
	    				}else{				
			        		//-------------------------------------------------------------------------------------------------------------
			        		//	새그룹 생성 - HANDLING번호 다른 경우
			        		//-------------------------------------------------------------------------------------------------------------	    				
	    					if (intBefHANDLING_CNT != intHANDLING_CNT) {
	        					
		    					commUtils.printLog(logId, LocalmethodNm+ "[" + Loop_i + "]번째 현재료의 HANDLING 번호["+intHANDLING_CNT+"]와 이전재료의 HANDLING번호["+intBefHANDLING_CNT+"]가 틀림 새그룹 생성", "SL");
		    					
			    				rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    				rsHandling.addRecord(recPara);
			    				vecResult.add(rsHandling) ;
			    				intBefHANDLING_CNT = intHANDLING_CNT;
			    				szBefMAIN_WRK_YN   = szMAIN_WRK_YN;
			    				
			    				intGROUPLING_CNT = 1; // 그룹핑 카운트 초기화
			    				
			    				continue;
			    				
			    			//-------------------------------------------------------------------------------------------------------------
			    			//	새그룹 생성 - 현재 주작업재료, 보조작업재료
			    			//-------------------------------------------------------------------------------------------------------------
		    				}else if(!szBefMAIN_WRK_YN.equals(szMAIN_WRK_YN)) {
		    					
			    				commUtils.printLog(logId, LocalmethodNm+ "[" + Loop_i + "]번째 현재료의 주작업상태 ["+szMAIN_WRK_YN+"]와 이전재료의 주작업상태["+intBefHANDLING_CNT+"]가 다르므로 새그룹 생성", "SL");
			    				
			    				rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    				rsHandling.addRecord(recPara);
			    				vecResult.add(rsHandling) ;
			    				
			    				intBefHANDLING_CNT = intHANDLING_CNT;
			    				szBefMAIN_WRK_YN   = szMAIN_WRK_YN;
			    				
			    				intGROUPLING_CNT = 1; // 그룹핑 카운트 초기화
			    				
			    				continue;
			    				
			    			} else if(intGROUPLING_CNT == intGROUPLING_LIMIT) {
			    			//-------------------------------------------------------------------------------------------------------------
			    			//	새그룹 생성 - 그룹핑 매수가 3매를 초과한 경우
			    			//-------------------------------------------------------------------------------------------------------------
	
		    					commUtils.printLog(logId, LocalmethodNm+ "[" + Loop_i + "]번째 현재료의 GROUPING_CNT ["+intGROUPLING_CNT+"]가 GROUPING_LIMIT["+intGROUPLING_LIMIT+"]에 도달 새그룹 생성", "SL");
		    					
			    				rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    				rsHandling.addRecord(recPara);
			    				vecResult.add(rsHandling) ;
			    				intBefHANDLING_CNT = intHANDLING_CNT;
			    				szBefMAIN_WRK_YN   = szMAIN_WRK_YN;
			    				
			    				intGROUPLING_CNT = 1; // 그룹핑 카운트 초기화
			    				
			    				continue;
	    				
	    				//-------------------------------------------------------------------------------------------------------------
	    				//	기존 그룹에 추가
	    				//-------------------------------------------------------------------------------------------------------------
	    				} else {
	    					
	    					commUtils.printLog(logId, LocalmethodNm+ "[" + Loop_i + "]번째 기존 그룹에 추가", "SL");
	    					rsHandling.addRecord(recPara);
	    					intBefHANDLING_CNT = intHANDLING_CNT;
		    				szBefMAIN_WRK_YN   = szMAIN_WRK_YN;
		    				
		    				intGROUPLING_CNT++;
		    				
							continue;
	    				}
	    			}	
	    			
	    		}//end of for
	   		
			} else {
			
		   		for (int Loop_i = 1; Loop_i <= rsReturn.size(); Loop_i++) {
		   			rsReturn.absolute(Loop_i);
	    			recPara = rsReturn.getRecord();
	    			//	권상모음순서
	    			intHANDLING_CNT = commUtils.paraRecChkNullInt(recPara,"HANDLING_CNT");
	    			szMAIN_WRK_YN   = commUtils.trim(recPara.getFieldString("MAIN_WRK_YN"));
					
					//-------------------------------------------------------------------------------------------------------------
	    			//	처음에 새그룹 생성
					//-------------------------------------------------------------------------------------------------------------
	    			if (Loop_i == 1) {
	    				
	    				rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    				rsHandling.addRecord(recPara);
	    				vecResult.add(rsHandling) ;
	    				
	    				intBefHANDLING_CNT = intHANDLING_CNT;
	    				szBefMAIN_WRK_YN   = szMAIN_WRK_YN;
	    				
	    			} else {
	    				//-------------------------------------------------------------------------------------------------------------
	    				//	새그룹 생성 - HANDLING번호 다른 경우
	    				//-------------------------------------------------------------------------------------------------------------
	    				if (intBefHANDLING_CNT != intHANDLING_CNT) {
	    					
	    					commUtils.printLog(logId, LocalmethodNm+ "[" + Loop_i + "]번째 현재료의 HANDLING 번호["+intHANDLING_CNT+"]와 이전재료의 HANDLING번호["+intBefHANDLING_CNT+"]가 틀림 새그룹 생성", "SL");
		    				
		    				rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    				rsHandling.addRecord(recPara);
		    				vecResult.add(rsHandling) ;
		    				intBefHANDLING_CNT = intHANDLING_CNT;
		    				szBefMAIN_WRK_YN   = szMAIN_WRK_YN;
		    				
		    				continue;
	    				
		    			//-------------------------------------------------------------------------------------------------------------
		    			//	새그룹 생성 - 현재 주작업재료, 보조작업재료
		    			//-------------------------------------------------------------------------------------------------------------
	    				}else if (!szBefMAIN_WRK_YN.equals(szMAIN_WRK_YN)) {
	    					
		    				commUtils.printLog(logId, LocalmethodNm+ "[" + Loop_i + "]번째 현재료의 주작업상태 ["+szMAIN_WRK_YN+"]와 이전재료의 주작업상태["+intBefHANDLING_CNT+"]가 다르므로 새그룹 생성", "SL");
		    				
		    				rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    				rsHandling.addRecord(recPara);
		    				vecResult.add(rsHandling) ;
		    				
		    				intBefHANDLING_CNT = intHANDLING_CNT;
		    				szBefMAIN_WRK_YN   = szMAIN_WRK_YN;
		    				continue;
	    				//-------------------------------------------------------------------------------------------------------------
	    				//	기존 그룹에 추가
	    				//-------------------------------------------------------------------------------------------------------------
	    				} else {
	    					
	    					commUtils.printLog(logId, LocalmethodNm+ "[" + Loop_i + "]번째 기존 그룹에 추가", "SL");
	    					rsHandling.addRecord(recPara);
	    					intBefHANDLING_CNT = intHANDLING_CNT;
		    				szBefMAIN_WRK_YN   = szMAIN_WRK_YN;
							continue;
	    				}
	    			}	
	    			
	    		}//end of for
			}
			
			commUtils.printLog(logId, LocalmethodNm+ "rsWrkbookmtl.size(): " + rsWrkbookmtl.size(), "SL");
			commUtils.printParam(logId, vecResult);
			
			commUtils.printLog(logId, methodNm, "S-");			
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return YsConstant.RETN_INT_SUCCESS;
	} 

	
    /**
     * 오퍼레이션명 : 창고스케줄링 Handling Data 크레인사양Check
     *  
     * @param  ● szEqpId, vecHandledData, vecResult
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int HandledDataCrnSpec (String logId, String methodNms, String szYD_SCH_CD, Vector vecHandledData, Vector vecResult) throws JDTOException {
    	String methodNm = "창고스케줄링 Handling Data 크레인사양 [GdsYsSchSeEJB.HandledDataCrnSpec] < " + methodNms;
    	String LocalmethodNm = "창고스케줄링 Handling Data 크레인사양 [GdsYsSchSeEJB.HandledDataCrnSpec]";
		JDTORecord    recPara    = null;
		JDTORecordSet rsPara 	 = null;
		JDTORecordSet rsMain 	 = null;
		
		int intMtlSh      = 0;	
		try{	

			commUtils.printLog(logId, methodNm + vecHandledData.size(), "S+");			

			
			if(szYD_SCH_CD.equals("KATC01LM")){
				
				commUtils.printLog(logId, LocalmethodNm + "재료 매수 확인 " , "SL");
				
				for(int Loop_i = 0; Loop_i < vecHandledData.size(); Loop_i++) {
	    			intMtlSh = 0;
	    			rsPara = (JDTORecordSet)vecHandledData.get(Loop_i) ;
	    			rsPara.first();
	    			rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    			for(int Loop_j = 0; Loop_j < rsPara.size(); Loop_j++) {
	    				rsPara.absolute(Loop_j+1);
	    				//rsParac의 레코드를 읽어온다.
	    				recPara = rsPara.getRecord();
	    				intMtlSh++;
	    				
	    				//기존그룹 추가
	    				if (intMtlSh <= 2) {
	    					rsMain.addRecord(recPara);
	    					commUtils.printLog(logId, LocalmethodNm+ "기존그룹 LoopJ : " + Loop_j , "SL");
	    				//새그룹 생성
	    				} else {
	    					if(rsMain.size() > 0) {
    							vecResult.add(rsMain);
    						}

	    					rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    					rsMain.addRecord(recPara);
	    					
		    				intMtlSh = 1;
		    				commUtils.printLog(logId,  LocalmethodNm+ "새그룹 LoopJ : " + Loop_j , "SL");
	    				}
	    			}//end of infor
	    			
	    			vecResult.add(rsMain);
	    			
	    		}//end of outfor
			} else {
				// 대상재 그대로  vecHandledData -> Vector vecResult 
				commUtils.printLog(logId,  LocalmethodNm+ "대상재 동일하게  vecHandledData -> vecResult move 처리" , "SL");
				for(int Loop_i = 0; Loop_i < vecHandledData.size(); Loop_i++) {
	    			rsPara = (JDTORecordSet)vecHandledData.get(Loop_i) ;
	    			rsPara.first();
	    			rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    			for(int Loop_j = 0; Loop_j < rsPara.size(); Loop_j++) {
	    				rsPara.absolute(Loop_j+1);
	    				//rsParac의 레코드를 읽어온다.
	    				recPara = rsPara.getRecord();
    					rsMain.addRecord(recPara);
	    			}//end of infor
	    			vecResult.add(rsMain);
	    		}//end of outfor
			}
			commUtils.printParam(logId, vecResult);

			commUtils.printLog(logId, methodNm, "S-");			

			return 1;
			
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    }//end of HandledDataCrnSpec()
   
 	/**
	 * A동 주작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public String procMainWrkLocRbA(String logId, String methodNms, JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recWbook) throws JDTOException {
		String methodNm = "A동 주작업TO위치결정 [GdsYsSchSeEJB.procMainWrkLocRbA] < " + methodNms;
		String LocalmethodNm = "A동 주작업TO위치결정 [GdsYsSchSeEJB.procMainWrkLocRbA] ";
//		String szOperationName			= "주작업TO위치결정";

		String szLogMsg					= null;
		JDTORecordSet rsResult			= null;
		JDTORecordSet rsStock			= null;
		JDTORecordSet outRsResult 		= null;
		JDTORecord outRecResult 		= null;
		JDTORecord recStock				= null;
		JDTORecord recPara				= null;
		JDTORecord recTemp				= null;
		JDTORecord recTemp1				= null;
		JDTORecord recCrnwrkmtl			= null;
		String szYS_UP_WO_LOC			= null;
		String szYS_UP_WO_LAYER			= null;
		String szYS_DN_WO_LOC			= "";
		String szYS_DN_WO_LAYER			= null;
		String szYD_UP_STK_COL_GP		= null;
		String szYD_UP_STK_BED_NO		= null;
		String szYS_STK_COL_GP			= null;
		String szYS_STK_BED_NO			= null;
		String szYS_STK_LYR_NO 			= null;
		String szYD_MTL_L_GP			= null;						//야드재료길이구분
		String szYD_RCPT_PLN_STR_LOC	= "";						//입고예정위치
		int intMTL_CNT					= 0;						//재료매수
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		recCrnwrkmtl = rsCrnwrkmtl.getRecord();
		
		String szYD_CRN_SCH_ID 	= commUtils.trim(recCrnwrkmtl.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
		String szSSTL_NO 		= commUtils.trim(recCrnwrkmtl.getFieldString("SSTL_NO"  ));			//크레인작업재료 중 최대 길이
		String szYD_EQP_ID  	= commUtils.trim(recCrnSch.getFieldString("YD_EQP_ID"  ));			//크레인설비ID
		String szYD_TO_LOC_GUIDE= commUtils.trim(recWbook.getFieldString("YD_TO_LOC_GUIDE"  ));		//사용자지정위치
		String szYD_SCH_CD 		= commUtils.trim(recWbook.getFieldString("YD_SCH_CD"  ));			//크레인스케줄코드
		String szYD_WBOOK_ID  	= commUtils.trim(recWbook.getFieldString("YD_WBOOK_ID"  ));		//작업예약
		intMTL_CNT 				= Integer.parseInt(commUtils.nvl(recCrnwrkmtl.getFieldString("SH_CNT"),"0"));
		
		//----------------------------------------------------------------------------------------------------------------------
		//	야드 저장품 정보 READ
		//----------------------------------------------------------------------------------------------------------------------
//		String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "*", "*");
		
//		if("Y".equals(sApplyYnPI)){
			rsStock = commDao.select(recCrnwrkmtl, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStock_PI_PIDEV", logId, methodNm, "저장품 조회");
			if (rsStock.size() <= 0) {
				szLogMsg = LocalmethodNm + "재료정보["+szSSTL_NO+"]를 저장품에서 조회 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return "0";
			}
			
//		} else {
//			rsStock = commDao.select(recCrnwrkmtl, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStock", logId, methodNm, "저장품 조회");
//			if (rsStock.size() <= 0) {
//				szLogMsg = LocalmethodNm + "재료정보["+szSSTL_NO+"]를 저장품에서 조회 실패 ";
//				commUtils.printLog(logId, szLogMsg, "SL");
//				return "0";
//			}
//		}
		rsStock.first();
		recStock = rsStock.getRecord();
		
		String szCUST_CD   		= commUtils.trim(recStock.getFieldString("CUST_CD"));			//고객사CUST_CD
		String szHEAT_NO   		= commUtils.trim(recStock.getFieldString("HEAT_NO"  ));			//HEAT_NO
		String szDETAIL_ARR_CD 	= commUtils.trim(recStock.getFieldString("DETAIL_ARR_CD"  ));	//상세착지
		String szBUNDLE_T 		= commUtils.trim(recStock.getFieldString("BUNDLE_T"  ));	//BUNDLE 두께
		
		szLogMsg = LocalmethodNm + " 크레인작업재료의  단 최대SEQ ["+szSSTL_NO+"]를 저장품에서 조회 완료 - 고객사["+szCUST_CD+"], 상세착지["+szDETAIL_ARR_CD+"], HEAT_NO["+szHEAT_NO+"]";
		commUtils.printLog(logId, szLogMsg, "SL");

		//----------------------------------------------------------------------------------------------------------------------
		//	권상중이거나 권하중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYS_UP_WO_LOC 		= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LOC"  ));		
		szYS_UP_WO_LAYER 	= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LAYER"  ));		

		if( szYS_UP_WO_LOC.equals("") ) {
			
			szLogMsg = LocalmethodNm + " 크레인작업재료의 최하단 재료정보["+szSSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			
			 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO 
			SELECT YS_STK_COL_GP            AS YS_STK_COL_GP
			      ,YS_STK_BED_NO            AS YS_STK_BED_NO
			      ,YS_STK_LYR_NO            AS YS_STK_LYR_NO
			      ,YS_STK_SEQ_NO            AS YS_STK_SEQ_NO
			      ,REGISTER                 AS REGISTER
			      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
			      ,MODIFIER                 AS MODIFIER
			      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
			      ,DEL_YN                   AS DEL_YN
			      ,SSTL_NO                   AS SSTL_NO
			      ,YD_STK_LYR_ACT_STAT      AS YD_STK_LYR_ACT_STAT
			      ,YD_STK_LYR_MTL_STAT      AS YD_STK_LYR_MTL_STAT
			      ,YD_STK_LYR_XAXIS         AS YD_STK_LYR_XAXIS
			      ,YD_STK_LYR_YAXIS         AS YD_STK_LYR_YAXIS
			      ,YD_STK_LYR_ZAXIS         AS YD_STK_LYR_ZAXIS
			  FROM TB_YS_STKLYR
			 WHERE SSTL_NO = :V_SSTL_NO
			   AND NVL(YD_STK_LYR_MTL_STAT, '*') = :V_YD_STK_LYR_MTL_STAT
			   AND DEL_YN='N'
			*/
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("SSTL_NO", szSSTL_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT", "D");
			
			rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO", logId, methodNm, "적재위치조회 조회");
			if (rsResult.size() <= 0) {

				recPara.setField("YD_STK_LYR_MTL_STAT", "U");
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO", logId, methodNm, "적재위치조회 조회");
				if (rsResult.size() <= 0) {
					return "0";
				}	
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szLogMsg = LocalmethodNm + " 크레인작업재료의  단 최대SEQ 재료정보["+szSSTL_NO+"]에 대한 권하 또는 권상위치 조회 완료  ";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YS_STK_COL_GP");
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YS_STK_BED_NO");
			szYS_UP_WO_LOC 			= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYS_UP_WO_LAYER 		= recTemp.getFieldString("YS_STK_LYR_NO");
		}else{
			szYD_UP_STK_COL_GP = szYS_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYS_UP_WO_LOC.substring(6);
			
		}

		szLogMsg = LocalmethodNm + " 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]";
		commUtils.printLog(logId, szLogMsg, "SL");
		
		//----------------------------------------------------------------------------------------------------------------------
		//	재료와 같은 동일HEAT,동일강종 의 적치가능한 베드 해당 동의 모든 위치를 조회 
		//----------------------------------------------------------------------------------------------------------------------
		
		//권상지시위치에 따라 알맞은 적치가능한 베드 검색 방법을 적용
    	
    	recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("HEAT_NO", 			szHEAT_NO);											//크레인작업 최하단재료의 HEAT_NO
    	recTemp.setField("CUST_CD", 			szCUST_CD);											//크레인작업 최하단재료의 고객사
    	recTemp.setField("DETAIL_ARR_CD", 		szDETAIL_ARR_CD);									//크레인작업 최하단재료의 상세착지
    	recTemp.setField("YD_UP_STK_LOC", 		szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO+szYS_UP_WO_LAYER);		
    	recTemp.setField("BUNDLE_T", 			szBUNDLE_T);										//번들두께

    	recTemp.setField("YD_MTL_L_GP", 		szYD_MTL_L_GP);										//크레인작업 최하단재료의 길이구분
    	recTemp.setField("YD_EQP_ID", 			szYD_EQP_ID);										//크레인설비ID
    	recTemp.setField("SH_CNT", 				""+intMTL_CNT);										//재료매수
    	recTemp.setField("YS_STK_COL_GP", 		szYD_UP_STK_COL_GP);								//권상지시위치 - 적치열
    	recTemp.setField("YS_STK_BED_NO", 		szYD_UP_STK_BED_NO);								//권상지시위치 - 적치베드
    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);										//크레인 스케줄코드

		if(szYD_TO_LOC_GUIDE.length() == 10) {
			/**********************************************************************
			* 열+ 베드+ 단  지정된 경우 -> 차량상차, 
			*                         대차상차 , 
			*                         입고 CARRY 요구시 Traverser 위치 결정
			*                         출고             요구시 Traverser 위치 결정
			***********************************************************************/            		
			szLogMsg = LocalmethodNm + " 적재위치 가이드 열+ 베드+ 단  지정된 경우 ["+szYD_TO_LOC_GUIDE+"]의 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
	
			szYS_DN_WO_LOC 		= szYD_TO_LOC_GUIDE.substring(0,8);
			szYS_DN_WO_LAYER 	= szYD_TO_LOC_GUIDE.substring(8,10);
			
			recTemp1 = JDTORecordFactory.getInstance().create();
	    	recTemp1.setField("YS_STK_COL_GP",	szYD_TO_LOC_GUIDE.substring(0,6));
	    	recTemp1.setField("YS_STK_BED_NO",	szYD_TO_LOC_GUIDE.substring(6,8));	
	    	recTemp1.setField("YS_STK_LYR_NO",	szYD_TO_LOC_GUIDE.substring(8,10));
	    	recTemp1.setField("SH_CNT"		 ,	""+intMTL_CNT);
			
			outRsResult = commDao.select(recTemp1, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLocGuideChk", logId, methodNm, "10자리 위치 적합성 체크");
			if (outRsResult.size() <= 0) {
				szLogMsg = LocalmethodNm + "10자리 위치 적합성 체크 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YsConstant.RETN_CD_FAILURE;
			}
			outRsResult.first();
			outRecResult  = outRsResult.getRecord();
			String szIsOk = commUtils.trim(outRecResult.getFieldString("IS_OK"));		
			
			if("Y".equals(szIsOk)){
				
				szLogMsg = LocalmethodNm + " TOSQL:["+szYD_CRN_SCH_ID+ "] TO위치결정 적재위치 가이드  YD_TO_LOC_GUIDE ["+szYD_TO_LOC_GUIDE+"]의 베드 정합성체크 성공";
				commUtils.printLog(logId, szLogMsg, "SL");
			}else{
				szYS_DN_WO_LOC		= "";
				szYS_DN_WO_LAYER	= "";
				 
				szLogMsg = LocalmethodNm + " TOSQL:["+szYD_CRN_SCH_ID+ "] TO위치결정 적재위치 가이드  YD_TO_LOC_GUIDE ["+szYD_TO_LOC_GUIDE+"]의 베드 정합성체크 실패";
				commUtils.printLog(logId, szLogMsg, "SL");
			}
		
		} else if((szYD_TO_LOC_GUIDE.length() == 4) && (szYD_TO_LOC_GUIDE.substring(2,4).equals("TY"))) {
			// 입고가적치장 
			recTemp.setField("YD_TO_LOC_GUIDE", 		szYD_TO_LOC_GUIDE);	
			
			szLogMsg = LocalmethodNm + " TOSQL:["+szYD_CRN_SCH_ID+ "]  TO위치결정 적재위치 가이드  YD_TO_LOC_GUIDE ["+szYD_TO_LOC_GUIDE+"]의 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			if(szYD_TO_LOC_GUIDE.substring(0,4).equals("KATY") || szYD_TO_LOC_GUIDE.substring(0,4).equals("KBTY")){
				outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLocGuideTYmultiLyr", logId, methodNm, "가이드  다LYR TY베드 조회");
			} else{
				outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLocGuideTY", logId, methodNm, "가이드  1LYR TY베드 조회");
			}
			if (outRsResult.size() <= 0) {
				szLogMsg = LocalmethodNm + "이적 BED 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YsConstant.RETN_CD_FAILURE;
			}
			outRsResult.first(); // A동은 한번에 설정 / B동은  하나씩 가져오면서 1을 더하여 ABLE 여부 파악 반복
			outRecResult  = outRsResult.getRecord();
			szYS_STK_COL_GP = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));		//권하지시위치 TEMP
			szYS_STK_BED_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));		//권하지시위치 TEMP
			szYS_STK_LYR_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_LYR_NO"  ));		//권하지시위치 TEMP
			
			/*
			 * 권하위치 최종결정정보 셋팅.
			 */
			szYS_DN_WO_LOC		= szYS_STK_COL_GP + szYS_STK_BED_NO;
			szYS_DN_WO_LAYER 	= szYS_STK_LYR_NO;
			commUtils.printLog(logId, "1. A동 적치단[" + szYS_DN_WO_LAYER +"]", "SL");
			//----------------------------------------------------------------------------------------------------------------------
			//	적치가능한 베드의 적치단을 1 증가 시킴 - 값이 없으면 01(1단)으로 설정
			//----------------------------------------------------------------------------------------------------------------------
			if( szYS_DN_WO_LAYER.equals("00") ) {								//값이 없으면
				szYS_DN_WO_LAYER = "01";										//1단
			}else{														     	//값 이 존재하면
				szYS_DN_WO_LAYER = commUtils.stringPlusInt(szYS_STK_LYR_NO, 0);	//조회된 적치단 + 1
			}			
			commUtils.printLog(logId, "2. A동 적치단[" + szYS_DN_WO_LAYER +"]", "SL");
			
		} else if((szYD_TO_LOC_GUIDE.length() == 8)||(szYD_TO_LOC_GUIDE.length() == 6)) {
			recTemp.setField("YD_TO_LOC_GUIDE", 		szYD_TO_LOC_GUIDE);	
			
			szLogMsg = LocalmethodNm + " TOSQL:["+szYD_CRN_SCH_ID+ "]  TO위치결정 적재위치 가이드  YD_TO_LOC_GUIDE ["+szYD_TO_LOC_GUIDE+"]의 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			// 적치 가능 여부 CEHCK	
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getLocGuide8RbA_PIDEV 
			WITH PARA_TBL1 AS (
			     SELECT :V_HEAT_NO AS P_HEAT_NO
			          , :V_CUST_CD AS P_CUST_CD
			          , :V_DETAIL_ARR_CD AS P_DETAIL_ARR_CD
			          , :V_YD_UP_STK_LOC AS P_YD_UP_STK_LOC   
			          , :V_YD_TO_LOC_GUIDE AS P_YD_TO_LOC_GUIDE
			          , (SELECT ITEM 
			               FROM USRYSA.TB_YS_RULE
			              WHERE REPR_CD_GP = 'K00003'
			                AND CD_GP = '0001') AS RULE_T 
			       FROM DUAL
			)
			
			SELECT A.SEQ_NUM 
			     , A.YS_STK_COL_GP
			     , A.YS_STK_BED_NO 
			     , A.YS_STK_LYR_NO 
			     , A.YS_STK_SEQ_NO 
			     , A.SSTL_NO
			     , A.MTL_STAT_UP_CNT 
			  FROM
			(  
			        SELECT CASE WHEN NVL(B.HEAT_NO,'*') = C.P_HEAT_NO AND NVL(B.CUST_CD,'*') = C.P_CUST_CD AND NVL(B.DETAIL_ARR_CD,'*') = C.P_DETAIL_ARR_CD  THEN '8' 
			                    WHEN NVL(B.HEAT_NO,'*') = C.P_HEAT_NO AND NVL(B.CUST_CD,'*') = C.P_CUST_CD                                                   THEN '7' 
			                    WHEN NVL(B.CUST_CD,'*') = C.P_CUST_CD AND NVL(B.DETAIL_ARR_CD,'*') = C.P_DETAIL_ARR_CD                                       THEN '6' 
			                    WHEN NVL(B.CUST_CD,'*') = C.P_CUST_CD                                                                                        THEN '5' 
			                    ELSE '1' END  SEQ_NUM 
			             , A.YS_STK_COL_GP
			             , A.YS_STK_BED_NO
			             , A.YS_STK_LYR_NO 
			             , A.YS_STK_SEQ_NO 
			             , A.SSTL_NO
			             , A.MTL_STAT_UP_CNT 
			          FROM
			             (
			               SELECT YS_STK_COL_GP
			                    , YS_STK_BED_NO
			                    , YS_STK_LYR_NO
			                    , '2' AS YS_STK_SEQ_NO
			                    , SSTL_NO
			                    , MTL_STAT_UP_CNT
			                 FROM
			                      (
			                           SELECT SL.YS_STK_COL_GP
			                                , SL.YS_STK_BED_NO
			                                , SL.YS_STK_LYR_NO
			                                , MAX(CASE WHEN SL.YS_STK_SEQ_NO = '1' AND SL.SSTL_NO IS NOT NULL THEN 'Y' ELSE 'N' END) AS DAN_1
			                                , MAX(CASE WHEN SL.YS_STK_SEQ_NO = '2' AND SL.SSTL_NO IS NULL     THEN 'Y' ELSE 'N' END) AS DAN_2
			                                , MAX(SL.SSTL_NO)    AS SSTL_NO
			                                , COUNT(SL.SSTL_NO)  AS SSTL_CNT
			                                , (SUM(DECODE(SL.YD_STK_LYR_MTL_STAT,'U',1,0)) +
			                                   (SELECT COUNT(*) FROM TB_YS_WRKBOOKMTL 
			                                     WHERE DEL_YN ='N' 
			                                       AND YS_STK_COL_GP = SL.YS_STK_COL_GP
			                                       AND YS_STK_BED_NO = SL.YS_STK_BED_NO
			                                       AND YS_STK_LYR_NO = SL.YS_STK_LYR_NO
			                                    )  
			                                  )     AS MTL_STAT_UP_CNT --작업대기수
			                            FROM TB_YS_STKLYR SL
			                               , PARA_TBL1    CP
			                           WHERE SUBSTR(SL.YS_STK_COL_GP,1,2) = 'KA'
			                             AND SL.YS_STK_COL_GP||SL.YS_STK_BED_NO LIKE CP.P_YD_TO_LOC_GUIDE || '%' --가이드 열
			                             AND (    (SUBSTR(SL.YS_STK_COL_GP,3,1) = '0' AND SL.YS_STK_LYR_NO >= (CASE WHEN :V_BUNDLE_T < CP.RULE_T THEN 1 ELSE 4 END )
			                                                                          AND SL.YS_STK_LYR_NO <= (CASE WHEN :V_BUNDLE_T < CP.RULE_T THEN 3 ELSE 99 END )        
			                                      ) 
			                                   OR (SUBSTR(SL.YS_STK_COL_GP,3,1) <> '0') 
			                                 )
			                           GROUP BY SL.YS_STK_COL_GP, SL.YS_STK_BED_NO, SL.YS_STK_LYR_NO
			                       ) 
			                 WHERE DAN_1 = 'Y'
			                   AND DAN_2 = 'Y'
			                   AND SSTL_CNT = (2-:V_SH_CNT ) 
			                   AND MTL_STAT_UP_CNT = 0
			               ) A
			             , TB_YS_STOCK  B 
			             , PARA_TBL1  C
			             , TB_YS_STKBED D
			         WHERE A.SSTL_NO = B.SSTL_NO
			           AND A.YS_STK_COL_GP || A.YS_STK_BED_NO || A.YS_STK_LYR_NO  <> C.P_YD_UP_STK_LOC  --자신위치 제외
			           AND A.YS_STK_COL_GP = D.YS_STK_COL_GP
			           AND A.YS_STK_BED_NO = D.YS_STK_BED_NO
			           AND NVL(D.YD_STK_BED_ACT_STAT,'*') = 'L'
			        UNION ALL
			        
			         SELECT A1.SEQ_NUM 
			             , A1.YS_STK_COL_GP
			             , A1.YS_STK_BED_NO
			             , A1.YS_STK_LYR_NO 
			             , A1.YS_STK_SEQ_NO 
			             , A1.SSTL_NO
			             , A1.MTL_STAT_UP_CNT
			          FROM
			               (  
			                SELECT '2' SEQ_NUM 
			                     , C.YS_STK_COL_GP 
			                     , C.YS_STK_BED_NO 
			                     , DECODE(SUBSTR(C.YS_STK_COL_GP,3,1),'0', C.YS_STK_LYR_NO,'01') AS YS_STK_LYR_NO
			                     , '1' AS YS_STK_SEQ_NO 
			                     , ''  AS SSTL_NO 
			                     , 0   AS MTL_STAT_UP_CNT
			                  FROM               
			                       (SELECT COUNT(SL.SSTL_NO)  AS SUM_CNT 
			                             , SL.YS_STK_COL_GP 
			                             , SL.YS_STK_BED_NO 
			                             , SL.YS_STK_LYR_NO 
			                          FROM TB_YS_STKLYR SL 
			                             , PARA_TBL1    CP
			                             , TB_YS_STKBED SB
			                         WHERE SL.DEL_YN = 'N' 
			                           AND SUBSTR(SL.YS_STK_COL_GP,1,2) = 'KA'
			                           AND SL.YS_STK_COL_GP LIKE SUBSTR(CP.P_YD_TO_LOC_GUIDE,1,6) || '%' --가이드 열
			                           AND SL.YS_STK_BED_NO LIKE SUBSTR(CP.P_YD_TO_LOC_GUIDE,7,2) || '%' --가이드 열
			                           AND (    (SUBSTR(SL.YS_STK_COL_GP,3,1) = '0' AND SL.YS_STK_LYR_NO >= (CASE WHEN :V_BUNDLE_T < CP.RULE_T THEN 1 ELSE 4 END )
			                                                                        AND SL.YS_STK_LYR_NO <= (CASE WHEN :V_BUNDLE_T < CP.RULE_T THEN 3 ELSE 99 END )        
			                                    ) 
			                                 OR (SUBSTR(SL.YS_STK_COL_GP,3,1) <> '0') 
			                               )
			                           AND SL.YS_STK_COL_GP = SB.YS_STK_COL_GP
			                           AND SL.YS_STK_BED_NO = SB.YS_STK_BED_NO
			                           AND NVL(SB.YD_STK_BED_ACT_STAT,'*') = 'L'    
			                         GROUP BY SL.YS_STK_COL_GP,SL.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
			                         HAVING COUNT(SL.SSTL_NO) = 0               
			                       ) C 
			                ORDER BY SEQ_NUM DESC,YS_STK_COL_GP, YS_STK_BED_NO , ABS(YS_STK_LYR_NO - NVL(SUBSTR(:V_YD_UP_STK_LOC,9,2),'01'))
			                ) A1
			          WHERE ROWNUM = 1   
			        ) A
			ORDER BY SEQ_NUM DESC,YS_STK_COL_GP, YS_STK_BED_NO , ABS(YS_STK_LYR_NO - NVL(SUBSTR(:V_YD_UP_STK_LOC,9,2),'01'))

			 */
			outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLocGuide8RbA_PIDEV", logId, methodNm, "동일 적치가능한 베드 조회");
			if (outRsResult.size() <= 0) {
				szLogMsg = LocalmethodNm + "이적 BED 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YsConstant.RETN_CD_FAILURE;
			}

			outRsResult.first();
			outRecResult  = outRsResult.getRecord();
			szYS_STK_COL_GP = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));		//권하지시위치 TEMP
			szYS_STK_BED_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));		//권하지시위치 TEMP
			szYS_STK_LYR_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_LYR_NO"  ));		//권하지시위치 TEMP
	
			/*
			 * 권하위치 최종결정정보 셋팅.
			 */
			szYS_DN_WO_LOC		= szYS_STK_COL_GP + szYS_STK_BED_NO;
			szYS_DN_WO_LAYER 	= szYS_STK_LYR_NO;
			//----------------------------------------------------------------------------------------------------------------------
			//	적치가능한 베드의 적치단을 1 증가 시킴 - 값이 없으면 01(1단)으로 설정
			//----------------------------------------------------------------------------------------------------------------------
			if( szYS_DN_WO_LAYER.equals("00") ) {								//값이 없으면
				szYS_DN_WO_LAYER = "01";										//1단
			}else{														     	//값 이 존재하면
				szYS_DN_WO_LAYER = commUtils.stringPlusInt(szYS_STK_LYR_NO, 0);	//조회된 적치단 + 1
			}
		}	

		if (szYS_DN_WO_LOC.equals("")) {
			
			szLogMsg = LocalmethodNm + " TOSQL:["+szYD_CRN_SCH_ID+ "] 동일한 고객사["+szCUST_CD+"], 상세착지["+szDETAIL_ARR_CD+"], HEAT_NO["+szHEAT_NO+"]의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getMainWrkMoveRbA_PIDEV
			WITH PARA_TBL1 AS (
			 SELECT :V_HEAT_NO AS P_HEAT_NO
			      , :V_CUST_CD AS P_CUST_CD
			      , :V_DETAIL_ARR_CD AS P_DETAIL_ARR_CD
			      , :V_YD_UP_STK_LOC AS P_YD_UP_STK_LOC   
			      -- 권상위치 열 
			      , CASE WHEN SUBSTR(:V_YD_UP_STK_LOC,1,6)  = 'KATS01'THEN 'KA0101'
			             WHEN SUBSTR(:V_YD_UP_STK_LOC,1,6)  = 'KATS02'THEN 'KA0103'
			             WHEN SUBSTR(:V_YD_UP_STK_LOC,1,6)  = 'KATS03'THEN 'KA0105'
			             WHEN SUBSTR(:V_YD_UP_STK_LOC,1,6)  = 'KATS04'THEN 'KA0107'
			             WHEN SUBSTR(:V_YD_UP_STK_LOC,1,6)  = 'KATS05'THEN 'KA0109'
			             WHEN SUBSTR(:V_YD_UP_STK_LOC,1,6)  = 'KATS06'THEN 'KA0111'
			             WHEN SUBSTR(:V_YD_UP_STK_LOC,1,6)  = 'KATS07'THEN 'KA0113'
			             WHEN MOD(SUBSTR(:V_YD_UP_STK_LOC,6,1),2) = 0 THEN SUBSTR(:V_YD_UP_STK_LOC,1,2)||'010'||TO_CHAR(TO_NUMBER(SUBSTR(:V_YD_UP_STK_LOC ,6,1)) - 1)
			             ELSE SUBSTR(:V_YD_UP_STK_LOC,1,2)||'010'||SUBSTR(:V_YD_UP_STK_LOC ,6,1) END P_YS_STK_COL_GP1
			      , CASE WHEN SUBSTR(:V_YD_UP_STK_LOC,1,6)  = 'KATS01'THEN 'KA0102'
			             WHEN SUBSTR(:V_YD_UP_STK_LOC,1,6)  = 'KATS02'THEN 'KA0104'
			             WHEN SUBSTR(:V_YD_UP_STK_LOC,1,6)  = 'KATS03'THEN 'KA0106'
			             WHEN SUBSTR(:V_YD_UP_STK_LOC,1,6)  = 'KATS04'THEN 'KA0108'
			             WHEN SUBSTR(:V_YD_UP_STK_LOC,1,6)  = 'KATS05'THEN 'KA0110'
			             WHEN SUBSTR(:V_YD_UP_STK_LOC,1,6)  = 'KATS06'THEN 'KA0112'
			             WHEN SUBSTR(:V_YD_UP_STK_LOC,1,6)  = 'KATS07'THEN 'KA0114'
			            WHEN MOD(SUBSTR(:V_YD_UP_STK_LOC,6,1),2) = 0 THEN SUBSTR(:V_YD_UP_STK_LOC,1,2)||'010'||SUBSTR(:V_YD_UP_STK_LOC ,6,1)
			             ELSE SUBSTR(:V_YD_UP_STK_LOC,1,2)||'010'||TO_CHAR(TO_NUMBER(SUBSTR(:V_YD_UP_STK_LOC,6,1)) + 1) END P_YS_STK_COL_GP2
			      , (SELECT ITEM 
			           FROM USRYSA.TB_YS_RULE
			          WHERE REPR_CD_GP = 'K00003'
			            AND CD_GP = '0001') AS RULE_T       
			 FROM DUAL  
			)
			SELECT A.SEQ_NUM 
			     , A.YS_STK_COL_GP
			     , A.YS_STK_BED_NO 
			     , A.YS_STK_LYR_NO 
			     , A.YS_STK_SEQ_NO 
			     , A.SSTL_NO
			     , A.MTL_STAT_UP_CNT 
			  FROM
			(  
			        SELECT CASE WHEN NVL(B.HEAT_NO,'*') = C.P_HEAT_NO AND NVL(B.CUST_CD,'*') = C.P_CUST_CD AND NVL(B.DETAIL_ARR_CD,'*') = C.P_DETAIL_ARR_CD  THEN '8' 
			                    WHEN NVL(B.HEAT_NO,'*') = C.P_HEAT_NO AND NVL(B.CUST_CD,'*') = C.P_CUST_CD                                                   THEN '7' 
			                    WHEN NVL(B.CUST_CD,'*') = C.P_CUST_CD AND NVL(B.DETAIL_ARR_CD,'*') = C.P_DETAIL_ARR_CD                                       THEN '6' 
			                    WHEN NVL(B.CUST_CD,'*') = C.P_CUST_CD                                                                                        THEN '5' 
			                    ELSE '1' END  SEQ_NUM 
			             , A.YS_STK_COL_GP
			             , A.YS_STK_BED_NO
			             , A.YS_STK_LYR_NO 
			             , A.YS_STK_SEQ_NO 
			             , A.SSTL_NO
			             , A.MTL_STAT_UP_CNT 
			          FROM
			             (
			               SELECT YS_STK_COL_GP
			                    , YS_STK_BED_NO
			                    , YS_STK_LYR_NO
			                    , '2' AS YS_STK_SEQ_NO
			                    , SSTL_NO
			                    , MTL_STAT_UP_CNT
			                 FROM
			                      (
			                           SELECT SL.YS_STK_COL_GP
			                                , SL.YS_STK_BED_NO
			                                , SL.YS_STK_LYR_NO
			                                , MAX(CASE WHEN SL.YS_STK_SEQ_NO = '1' AND SL.SSTL_NO IS NOT NULL THEN 'Y' ELSE 'N' END) AS DAN_1
			                                , MAX(CASE WHEN SL.YS_STK_SEQ_NO = '2' AND SL.SSTL_NO IS NULL     THEN 'Y' ELSE 'N' END) AS DAN_2
			                                , MAX(SL.SSTL_NO)    AS SSTL_NO
			                                , COUNT(SL.SSTL_NO)  AS SSTL_CNT
			                                , (SUM(DECODE(SL.YD_STK_LYR_MTL_STAT,'U',1,0)) +
			                                   (SELECT COUNT(*) FROM TB_YS_WRKBOOKMTL 
			                                     WHERE DEL_YN ='N' 
			                                       AND YS_STK_COL_GP = SL.YS_STK_COL_GP
			                                       AND YS_STK_BED_NO = SL.YS_STK_BED_NO
			                                       AND YS_STK_LYR_NO = SL.YS_STK_LYR_NO
			                                    )  
			                                  )     AS MTL_STAT_UP_CNT --작업대기수
			                            FROM TB_YS_STKLYR SL
			                               , PARA_TBL1    CP
			                           WHERE SL.YS_STK_COL_GP IN (CP.P_YS_STK_COL_GP1, CP.P_YS_STK_COL_GP2) --같은열작업
			--                             AND (    (SUBSTR(SL.YS_STK_COL_GP,3,1) = '0' AND SL.YS_STK_LYR_NO >= (CASE WHEN :V_BUNDLE_T < TO_NUMBER(CP.RULE_T) THEN 1 ELSE 4 END )
			--                                                                          AND SL.YS_STK_LYR_NO <= (CASE WHEN :V_BUNDLE_T < TO_NUMBER(CP.RULE_T) THEN 3 ELSE 99 END )  
			                              AND (    (SUBSTR(SL.YS_STK_COL_GP,3,1) = '0' AND SL.YS_STK_LYR_NO >= (CASE WHEN :V_BUNDLE_T < TO_NUMBER(CP.RULE_T) THEN 1 ELSE 2 END )
			                                                                          AND SL.YS_STK_LYR_NO <= (CASE WHEN :V_BUNDLE_T < TO_NUMBER(CP.RULE_T) THEN 1 ELSE 99 END )                                              
			                                      ) 
			                                   OR (SUBSTR(SL.YS_STK_COL_GP,3,1) <> '0') 
			                                 )
			                             AND NVL(SL.YD_STK_LYR_ACT_STAT,'*') = 'E'    
			                           GROUP BY SL.YS_STK_COL_GP, SL.YS_STK_BED_NO, SL.YS_STK_LYR_NO
			                       ) 
			                 WHERE DAN_1 = 'Y'
			                   AND DAN_2 = 'Y'
			                   AND SSTL_CNT = (2-:V_SH_CNT ) 
			                   AND MTL_STAT_UP_CNT = 0
			               ) A
			             , TB_YS_STOCK  B 
			             , PARA_TBL1  C
			             , TB_YS_STKBED D
			             , TB_YS_STKCOL E
			         WHERE A.SSTL_NO = B.SSTL_NO
			           AND A.YS_STK_COL_GP || A.YS_STK_BED_NO || A.YS_STK_LYR_NO  <> C.P_YD_UP_STK_LOC  --자신위치 제외
			           AND A.YS_STK_COL_GP = D.YS_STK_COL_GP
			           AND A.YS_STK_BED_NO = D.YS_STK_BED_NO
			           AND NVL(D.YD_STK_BED_ACT_STAT,'*') = 'L'
			           AND D.YS_STK_COL_GP = E.YS_STK_COL_GP
			           AND NVL(E.YD_STK_COL_ACT_STAT,'*') = 'L'
			        UNION ALL
			        
			         SELECT A1.SEQ_NUM 
			             , A1.YS_STK_COL_GP
			             , A1.YS_STK_BED_NO
			             , A1.YS_STK_LYR_NO 
			             , A1.YS_STK_SEQ_NO 
			             , A1.SSTL_NO
			             , A1.MTL_STAT_UP_CNT
			          FROM
			               (  
			                SELECT '2' SEQ_NUM 
			                     , C.YS_STK_COL_GP 
			                     , C.YS_STK_BED_NO 
			                     , DECODE(SUBSTR(C.YS_STK_COL_GP,3,1),'0', C.YS_STK_LYR_NO, '01') AS YS_STK_LYR_NO
			                     , '1' AS YS_STK_SEQ_NO 
			                     , ''  AS SSTL_NO 
			                     , 0   AS MTL_STAT_UP_CNT
			                  FROM               
			                       (SELECT COUNT(SL.SSTL_NO)  AS SUM_CNT 
			                             , SL.YS_STK_COL_GP 
			                             , SL.YS_STK_BED_NO 
			                             , SL.YS_STK_LYR_NO 
			                          FROM TB_YS_STKLYR SL 
			                             , PARA_TBL1    CP
			                             , TB_YS_STKBED SB
			                             , TB_YS_STKCOL SC
			                         WHERE SL.DEL_YN = 'N' 
			                           AND SL.YS_STK_COL_GP IN (CP.P_YS_STK_COL_GP1, CP.P_YS_STK_COL_GP2) --같은열작업
			--                           AND (    (SUBSTR(SL.YS_STK_COL_GP,3,1) = '0' AND SL.YS_STK_LYR_NO >= (CASE WHEN :V_BUNDLE_T < TO_NUMBER(CP.RULE_T) THEN 1 ELSE 4 END )
			--                                                                        AND SL.YS_STK_LYR_NO <= (CASE WHEN :V_BUNDLE_T < TO_NUMBER(CP.RULE_T) THEN 3 ELSE 99 END )  
			                          AND (    (SUBSTR(SL.YS_STK_COL_GP,3,1) = '0' AND SL.YS_STK_LYR_NO >= (CASE WHEN :V_BUNDLE_T < TO_NUMBER(CP.RULE_T) THEN 1 ELSE 2 END )
			                                                                        AND SL.YS_STK_LYR_NO <= (CASE WHEN :V_BUNDLE_T < TO_NUMBER(CP.RULE_T) THEN 1 ELSE 99 END )                                               
			                                    ) 
			                                 OR (SUBSTR(SL.YS_STK_COL_GP,3,1) <> '0') 
			                               )
			                           AND SL.YS_STK_COL_GP = SB.YS_STK_COL_GP
			                           AND SL.YS_STK_BED_NO = SB.YS_STK_BED_NO
			                           -- 일반 베드일 경우 L 체크, TS일 경우 L체크 안함
			                           AND ((SUBSTR(SL.YS_STK_COL_GP,3,1) = '0' AND NVL(SB.YD_STK_BED_ACT_STAT,'*') = 'L')
			                                OR (SUBSTR(SL.YS_STK_COL_GP,3,1) <> '0'))   
			                                
			                           AND SB.YS_STK_COL_GP = SC.YS_STK_COL_GP
			                           AND NVL(SC.YD_STK_COL_ACT_STAT,'*') = 'L'    
			                           AND NVL(SL.YD_STK_LYR_ACT_STAT,'*') = 'E'    
			                         GROUP BY SL.YS_STK_COL_GP,SL.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
			                         HAVING COUNT(SL.SSTL_NO) = 0               
			                       ) C 
			                ORDER BY SEQ_NUM DESC,YS_STK_COL_GP, YS_STK_BED_NO , ABS(YS_STK_LYR_NO - NVL(SUBSTR(:V_YD_UP_STK_LOC,9,2),'01'))
			                ) A1
			          WHERE ROWNUM < 10   
			        ) A
			ORDER BY SEQ_NUM DESC,YS_STK_COL_GP, YS_STK_BED_NO , ABS(YS_STK_LYR_NO - NVL(SUBSTR(:V_YD_UP_STK_LOC,9,2),'01'))

				*/
			outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getMainWrkMoveRbA_PIDEV", logId, methodNm, "동일 적치가능한 베드 조회");
			if (outRsResult.size() <= 0) {
				szLogMsg = LocalmethodNm + "이적 BED 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YsConstant.RETN_CD_FAILURE;
			}

			// 적치 가능 여부 CEHCK	
			outRsResult.first();
			outRecResult  = outRsResult.getRecord();
			szYS_STK_COL_GP = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));		//권하지시위치 TEMP
			szYS_STK_BED_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));		//권하지시위치 TEMP
			szYS_STK_LYR_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_LYR_NO"  ));		//권하지시위치 TEMP
	
			/*
			 * 권하위치 최종결정정보 셋팅.
			 */
			szYS_DN_WO_LOC		= szYS_STK_COL_GP + szYS_STK_BED_NO;
			szYS_DN_WO_LAYER 	= szYS_STK_LYR_NO;
			//----------------------------------------------------------------------------------------------------------------------
			//	적치가능한 베드의 적치단을 1 증가 시킴 - 값이 없으면 01(1단)으로 설정
			//----------------------------------------------------------------------------------------------------------------------
			if( szYS_DN_WO_LAYER.equals("00") ) {								//값이 없으면
				szYS_DN_WO_LAYER = "01";										//1단
			}else{														     	//값 이 존재하면
				szYS_DN_WO_LAYER = commUtils.stringPlusInt(szYS_STK_LYR_NO, 0);	//조회된 적치단 + 1
			}
		}
		JDTORecord RecSetLoc = JDTORecordFactory.getInstance().create();
		RecSetLoc.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
		RecSetLoc.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
		RecSetLoc.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
		RecSetLoc.setField("YS_UP_WO_LOC", 		szYS_UP_WO_LOC); 
		RecSetLoc.setField("YS_UP_WO_LAYER",	szYS_UP_WO_LAYER);	 
		RecSetLoc.setField("YS_DN_WO_LOC", 		szYS_DN_WO_LOC);
		RecSetLoc.setField("YS_DN_WO_LAYER", 	szYS_DN_WO_LAYER);
		RecSetLoc.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
		RecSetLoc.setField("YD_RCPT_PLN_STR_LOC", 	szYD_RCPT_PLN_STR_LOC);
			
		this.procUpdateLoc(logId,methodNms,recCrnwrkmtl  ,RecSetLoc );
		//----------------------------------------------------------------------------------------------------------------------
    	// ERROR 발생시 ?
		//----------------------------------------------------------------------------------------------------------------------
		return YsConstant.RETN_CD_SUCCESS;
	}
	/**
	 * B동 주작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public String procMainWrkLocRbB(String logId, String methodNms, JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recWbook) throws JDTOException {
		String methodNm = "B동 주작업TO위치결정 [GdsYsSchSeEJB.procMainWrkLocRbB] < " + methodNms;
		String LocalmethodNm = "B동 주작업TO위치결정 [GdsYsSchSeEJB.procMainWrkLocRbB] " ;

		String szLogMsg					= null;
		JDTORecordSet rsResult			= null;
		JDTORecordSet rsStock			= null;
		JDTORecordSet outRsResult 		= null;
		JDTORecord outRecResult 		= null;
		JDTORecord recStock				= null;
		JDTORecord recPara				= null;
		JDTORecord recTemp				= null;
		JDTORecord		recResult			= null;
		JDTORecord recCrnwrkmtl			= null;
		String szYS_UP_WO_LOC			= null;
		String szYS_UP_WO_LAYER			= null;
		String szYS_DN_WO_LOC			= "";
		String szYS_DN_WO_LAYER			= null;
		String szYD_UP_STK_COL_GP		= null;
		String szYD_UP_STK_BED_NO		= null;
		String szYS_STK_COL_GP			= null;
		String szYS_STK_BED_NO			= null;
		int intSH_CNT					= 0;	
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		recCrnwrkmtl = rsCrnwrkmtl.getRecord();
		
		String szYD_CRN_SCH_ID 	= commUtils.trim(recCrnwrkmtl.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
		String szSSTL_NO 		= commUtils.trim(recCrnwrkmtl.getFieldString("SSTL_NO"  ));			//크레인작업재료 중 최대 길이
		String szYD_EQP_ID  	= commUtils.trim(recCrnSch.getFieldString("YD_EQP_ID"  ));			//크레인설비ID
		String szYD_TO_LOC_GUIDE= commUtils.trim(recWbook.getFieldString("YD_TO_LOC_GUIDE"  ));		//사용자지정위치
		String szYD_SCH_CD 		= commUtils.trim(recWbook.getFieldString("YD_SCH_CD"  ));			//크레인스케줄코드
		String szYD_WBOOK_ID  	= commUtils.trim(recWbook.getFieldString("YD_WBOOK_ID"  ));		//작업예약
		intSH_CNT 				= Integer.parseInt(commUtils.nvl(recCrnwrkmtl.getFieldString("SH_CNT"),"0"));
		
//		String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "*", "*");
//		if("Y".equals(sApplyYnPI)){
			rsStock = commDao.select(recCrnwrkmtl, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStock_PI_PIDEV", logId, methodNm, "저장품 조회");
			if (rsStock.size() <= 0) {
				szLogMsg = "["+ methodNm +"]  재료정보["+szSSTL_NO+"]를 저장품에서 조회 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return "0";
			}
//		} else {
//			rsStock = commDao.select(recCrnwrkmtl, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStock", logId, methodNm, "저장품 조회");
//			if (rsStock.size() <= 0) {
//				szLogMsg = "["+ methodNm +"]  재료정보["+szSSTL_NO+"]를 저장품에서 조회 실패 ";
//				commUtils.printLog(logId, szLogMsg, "SL");
//				return "0";
//			}
//		}
		rsStock.first();
		recStock = rsStock.getRecord();
		
		String szCUST_CD   		= commUtils.trim(recStock.getFieldString("CUST_CD"  ));			//고객사
		String szHEAT_NO   		= commUtils.trim(recStock.getFieldString("HEAT_NO"  ));			//HEAT_NO
		String szDETAIL_ARR_CD 	= commUtils.trim(recStock.getFieldString("DETAIL_ARR_CD"  ));	//상세착지
		String szYD_MTL_L_GP 	= commUtils.trim(recStock.getFieldString("YD_MTL_L_GP"  ));		//길이구분
		String szBNDL_PROG_CD 	= commUtils.trim(recStock.getFieldString("BNDL_PROG_CD"  ));	//진도코드
		
		szLogMsg = LocalmethodNm + " 크레인작업재료의  단 최대SEQ ["+szSSTL_NO+"]를 저장품에서 조회 완료 - 고객사["+szCUST_CD+"], 상세착지["+szDETAIL_ARR_CD+"], HEAT_NO["+szHEAT_NO+"]";
		commUtils.printLog(logId, szLogMsg, "SL");

		//----------------------------------------------------------------------------------------------------------------------
		//	권상중이거나 권하중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYS_UP_WO_LOC 		= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LOC"  ));		
		szYS_UP_WO_LAYER 	= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LAYER"  ));		

		if( szYS_UP_WO_LOC.equals("") ) {
			
			szLogMsg = LocalmethodNm + " 크레인작업재료의 최하단 재료정보["+szSSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			
			 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO 
			SELECT YS_STK_COL_GP            AS YS_STK_COL_GP
			      ,YS_STK_BED_NO            AS YS_STK_BED_NO
			      ,YS_STK_LYR_NO            AS YS_STK_LYR_NO
			      ,YS_STK_SEQ_NO            AS YS_STK_SEQ_NO
			      ,REGISTER                 AS REGISTER
			      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
			      ,MODIFIER                 AS MODIFIER
			      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
			      ,DEL_YN                   AS DEL_YN
			      ,SSTL_NO                   AS SSTL_NO
			      ,YD_STK_LYR_ACT_STAT      AS YD_STK_LYR_ACT_STAT
			      ,YD_STK_LYR_MTL_STAT      AS YD_STK_LYR_MTL_STAT
			      ,YD_STK_LYR_XAXIS         AS YD_STK_LYR_XAXIS
			      ,YD_STK_LYR_YAXIS         AS YD_STK_LYR_YAXIS
			      ,YD_STK_LYR_ZAXIS         AS YD_STK_LYR_ZAXIS
			  FROM TB_YS_STKLYR
			 WHERE SSTL_NO = :V_SSTL_NO
			   AND NVL(YD_STK_LYR_MTL_STAT, '*') = :V_YD_STK_LYR_MTL_STAT
			   AND DEL_YN='N'
			*/
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("SSTL_NO", szSSTL_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT", "D");
			
			rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO", logId, methodNm, "적재위치조회 조회");
			if (rsResult.size() <= 0) {

				recPara.setField("YD_STK_LYR_MTL_STAT", "U");
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO", logId, methodNm, "적재위치조회 조회");
				if (rsResult.size() <= 0) {
					return "0";
				}	
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szLogMsg = LocalmethodNm + " 크레인작업재료의  단 최대SEQ 재료정보["+szSSTL_NO+"]에 대한 권하 또는 권상위치 조회 완료  ";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YS_STK_COL_GP");
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YS_STK_BED_NO");
			szYS_UP_WO_LOC 			= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYS_UP_WO_LAYER 		= recTemp.getFieldString("YS_STK_LYR_NO");
			
		}else{
		
			szYD_UP_STK_COL_GP = szYS_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYS_UP_WO_LOC.substring(6);
			
		}
		szLogMsg = LocalmethodNm + " 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]";
		commUtils.printLog(logId, szLogMsg, "SL");

		//TO위치가이드가 KBC1일경우(만적시 지정 위치), 아래 로직 타지 않고, 바로 KBC1 베드 중 한곳으로 권하지시 위치 결정하고 빠져나감.
		//작업예약 내 TO위치가이드가 KBC1으로 지정된 경우 : 목적동이 A동이고, 만적일때. (목적동(B), TO위치가이드(KBC1), 대차예정설비(추가) 변경)
		//목적동이 B동인 경우, 실제 스케줄 생성 시점에 만적 판단후 만적이면 KBC1으로 보냄.(따라서 작업예약에는 KB1으로 예정위치 설정되어있음)
		if(szYD_TO_LOC_GUIDE.equals("KBC1"))
		{
			
			String szC1YS_DN_WO_LOC="";
			String szC1YS_DN_WO_LAYER="";
			
			/*
			SELECT *
			FROM 
			(
				SELECT ROWNUM AS RN,YS_STK_COL_GP||YS_STK_BED_NO AS YS_DN_WO_LOC,  YS_STK_LYR_NO AS YS_DN_WO_LAYER--, A.*
				FROM TB_YS_STKLYR A
				WHERE YS_STK_COL_GP = 'KBC101'
				AND SSTL_NO IS NULL
				AND YD_STK_LYR_MTL_STAT ='E'
				AND YS_STK_SEQ_NO ='1'
				ORDER BY YS_STK_COL_GP , YS_STK_BED_NO , YS_STK_LYR_NO , YS_STK_SEQ_NO 
			) A
			WHERE RN=1 
			 */
			
			rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNOByC1", logId, methodNm, "C1야드 적재위치조회 조회");
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szC1YS_DN_WO_LOC 		= recTemp.getFieldString("YS_DN_WO_LOC");
			szC1YS_DN_WO_LAYER 		= recTemp.getFieldString("YS_DN_WO_LAYER");
			
			JDTORecord RecSetLoc2 = JDTORecordFactory.getInstance().create();
			RecSetLoc2.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
			RecSetLoc2.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
			RecSetLoc2.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
			RecSetLoc2.setField("YS_UP_WO_LOC", 		szYS_UP_WO_LOC); 
			RecSetLoc2.setField("YS_UP_WO_LAYER",	szYS_UP_WO_LAYER);	 
			RecSetLoc2.setField("YS_DN_WO_LOC", 		szC1YS_DN_WO_LOC);//szYS_DN_WO_LOC);
			RecSetLoc2.setField("YS_DN_WO_LAYER", 	szC1YS_DN_WO_LAYER);//szYS_DN_WO_LAYER);
			RecSetLoc2.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
			RecSetLoc2.setField("YD_RCPT_PLN_STR_LOC","");
			
			this.procUpdateLoc(logId,methodNms,recCrnwrkmtl  ,RecSetLoc2 );
			return YsConstant.RETN_CD_SUCCESS;
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	재료와 같은 동일HEAT,동일강종 의 적치가능한 베드 해당 동의 모든 위치를 조회 
		//----------------------------------------------------------------------------------------------------------------------
		
		//권상지시위치에 따라 알맞은 적치가능한 베드 검색 방법을 적용
    	
    	recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("YS_STK_COL_GP", 		szYD_UP_STK_COL_GP);								//권상지시위치 - 적치열
    	recTemp.setField("YS_STK_BED_NO", 		szYD_UP_STK_BED_NO);								//권상지시위치 - 적치베드
    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);										//크레인 스케줄코드
    	recTemp.setField("HEAT_NO", 			szHEAT_NO);											//크레인작업 최하단재료의 HEAT_NO
    	recTemp.setField("CUST_CD", 			szCUST_CD);											//크레인작업 최하단재료의 고객사
    	recTemp.setField("DETAIL_ARR_CD", 		szDETAIL_ARR_CD);									//크레인작업 최하단재료의 상세착지
    	recTemp.setField("YD_MTL_L_GP", 		szYD_MTL_L_GP);										//크레인작업 최하단재료의 길이구분
    	recTemp.setField("YD_EQP_ID", 			szYD_EQP_ID);										//크레인설비ID
    	recTemp.setField("YD_UP_STK_LOC", 		szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO+szYS_UP_WO_LAYER);		
    	recTemp.setField("SH_CNT", 				""+intSH_CNT);										//재료매수
    	recTemp.setField("YD_CRN_SCH_ID", 		szYD_CRN_SCH_ID);										//재료매수

    	szLogMsg = LocalmethodNm + " 동일한 고객사["+szCUST_CD+"], 상세착지["+szDETAIL_ARR_CD+"], HEAT_NO["+szHEAT_NO+"], YD_TO_LOC_GUIDE ["+szYD_TO_LOC_GUIDE+ "]의 적치가능한 베드 조회 시작";
		commUtils.printLog(logId, szLogMsg, "SL");

		if("KBXXYY".equals(szYD_TO_LOC_GUIDE)) {//PC04에서 입고되는 제품에 대한 To위치 결정로직(KB010101,KB010201,KB010301,KB010401,KB010501, KB010601, KB010701, KB010801, KB010901, KB011001)
												//               판정보류재 추가 - KB011101,06 / KB011201,06   
			recTemp.setField("YD_TO_LOC_GUIDE", szYD_TO_LOC_GUIDE);	
			
			szLogMsg = LocalmethodNm + " TOSQL:["+szYD_CRN_SCH_ID+ "]  TO위치결정 적재위치 가이드  YD_TO_LOC_GUIDE ["+szYD_TO_LOC_GUIDE+"]의 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			//판정보류재 검색쿼리
			if("F".equals(szBNDL_PROG_CD)){
				outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLocGuideRbB_XXZZ_PIDEV", logId, methodNm, "가이드  베드 조회");
			}else{
				outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLocGuideRbB_XXYY_PIDEV", logId, methodNm, "가이드  베드 조회");
				//임시로 개발계내 결과 안나오게 쿼리 수정(1=2)
			}
			
			if (outRsResult.size() <= 0) { 
				szLogMsg = LocalmethodNm + "가이드 BED 검색 실패.";
				commUtils.printLog(logId, szLogMsg, "SL");
				
				//KBC1으로 권하위치 할당 후 로직 종료.
				/*
				JDTORecord RecSetLoc2 = JDTORecordFactory.getInstance().create();
				RecSetLoc2.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
				RecSetLoc2.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
				RecSetLoc2.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
				RecSetLoc2.setField("YS_UP_WO_LOC", 		szYS_UP_WO_LOC); 
				RecSetLoc2.setField("YS_UP_WO_LAYER",	szYS_UP_WO_LAYER);	 
				RecSetLoc2.setField("YS_DN_WO_LOC", 		"KBC10101");//szYS_DN_WO_LOC);
				RecSetLoc2.setField("YS_DN_WO_LAYER", 	"01");//szYS_DN_WO_LAYER);
				RecSetLoc2.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
				RecSetLoc2.setField("YD_RCPT_PLN_STR_LOC","");
				
				this.procUpdateLoc(logId,methodNms,recCrnwrkmtl  ,RecSetLoc2 );
				return YsConstant.RETN_CD_SUCCESS;
				*/
				return YsConstant.RETN_CD_FAILURE;
			}

			// 적치 가능 여부 CEHCK	
			for(int i = 1; i <= outRsResult.size(); i++) {
	
				outRsResult.absolute(i); // A동은 한번에 설정 / B동은  하나씩 가져오면서 1을 더하여 ABLE 여부 파악 반복
				outRecResult  = outRsResult.getRecord();
				szYS_STK_COL_GP = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));		//권하지시위치 TEMP
				szYS_STK_BED_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));		//권하지시위치 TEMP
			
				// 적치 가능 여부 CEHCK	
				recResult = this.procLocAbleCheck_XXYY(logId, methodNms, szYS_STK_COL_GP,szYS_STK_BED_NO);
				
				if(commUtils.trim(recResult.getFieldString("ABLE_YN"  )).equals("Y")){
					szYS_DN_WO_LOC 		= commUtils.trim(recResult.getFieldString("YS_DN_WO_LOC"  )); //CA0104		
					szYS_DN_WO_LAYER 	= commUtils.trim(recResult.getFieldString("YS_DN_WO_LAYER"  )); //02
					break;	
				} else {
					continue;
				}
			}	
			
		}else if(szYD_TO_LOC_GUIDE.length() == 10) {
			/**********************************************************************
			* 열+ 베드+ 단  지정된 경우 -> 차량상차, 대차 상차 
			***********************************************************************/            		
			szLogMsg = "["+ LocalmethodNm +"] 적재위치 가이드 열+ 베드+ 단  지정된 경우 -> 지정 YD_TO_LOC_GUIDE ["+szYD_TO_LOC_GUIDE+"]의 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			szYS_DN_WO_LOC 		= szYD_TO_LOC_GUIDE.substring(0,8);
			szYS_DN_WO_LAYER 	= szYD_TO_LOC_GUIDE.substring(8,10);
			
		} else if((szYD_TO_LOC_GUIDE.length() == 4) && (szYD_TO_LOC_GUIDE.substring(2,4).equals("TY"))) {
			
			recTemp.setField("YD_TO_LOC_GUIDE", 		szYD_TO_LOC_GUIDE);	

			szLogMsg = LocalmethodNm + " TOSQL:["+szYD_CRN_SCH_ID+ "]  TO위치결정 적재위치 가이드  YD_TO_LOC_GUIDE ["+szYD_TO_LOC_GUIDE+"]의 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");

			if(szYD_TO_LOC_GUIDE.substring(0,4).equals("KATY") || szYD_TO_LOC_GUIDE.substring(0,4).equals("KBTY")){
				outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLocGuideTYmultiLyr", logId, methodNm, "가이드  다LYR TY베드 조회");
			} else{
				outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLocGuideTY", logId, methodNm, "가이드  1LYR TY베드 조회");
			}
			if (outRsResult.size() <= 0) {
				szLogMsg = LocalmethodNm + "임시 BED 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YsConstant.RETN_CD_FAILURE;
			}

			// 적치 가능 여부 CEHCK	
			for(int i = 1; i <= outRsResult.size(); i++) {
	
				outRsResult.absolute(i);
				outRecResult  = outRsResult.getRecord();
				szYS_STK_COL_GP = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));		//권하지시위치 TEMP
				szYS_STK_BED_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));		//권하지시위치 TEMP
			
				// 적치 가능 여부 CEHCK	
				recResult = this.procLocAbleCheck(logId, methodNms, szYS_STK_COL_GP,szYS_STK_BED_NO);
				
				if(commUtils.trim(recResult.getFieldString("ABLE_YN"  )).equals("Y")){
					szYS_DN_WO_LOC 		= commUtils.trim(recResult.getFieldString("YS_DN_WO_LOC"  )); //CA0104		
					szYS_DN_WO_LAYER 	= commUtils.trim(recResult.getFieldString("YS_DN_WO_LAYER"  )); //02
					break;	
				} else {
					continue;
				}
			}	
		} else if((szYD_TO_LOC_GUIDE.length() == 8)||(szYD_TO_LOC_GUIDE.length() == 6)) {
			
			recTemp.setField("YD_TO_LOC_GUIDE", 		szYD_TO_LOC_GUIDE);	
			
			szLogMsg = LocalmethodNm + " TOSQL:["+szYD_CRN_SCH_ID+ "]  TO위치결정 적재위치 가이드  YD_TO_LOC_GUIDE ["+szYD_TO_LOC_GUIDE+"]의 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLocGuideRbB_PIDEV", logId, methodNm, "가이드  베드 조회");
			if (outRsResult.size() <= 0) {
				szLogMsg = LocalmethodNm + "가이드 BED 검색 실패.";// 지정위치 KBC1으로 권하위치 할당. ";

				commUtils.printLog(logId, szLogMsg, "SL");
				/*
				JDTORecord RecSetLoc2 = JDTORecordFactory.getInstance().create();
				RecSetLoc2.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
				RecSetLoc2.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
				RecSetLoc2.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
				RecSetLoc2.setField("YS_UP_WO_LOC", 		szYS_UP_WO_LOC); 
				RecSetLoc2.setField("YS_UP_WO_LAYER",	szYS_UP_WO_LAYER);	 
				RecSetLoc2.setField("YS_DN_WO_LOC", 		"KBC10101");//szYS_DN_WO_LOC);
				RecSetLoc2.setField("YS_DN_WO_LAYER", 	"01");//szYS_DN_WO_LAYER);
				RecSetLoc2.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
				RecSetLoc2.setField("YD_RCPT_PLN_STR_LOC","");
				
				this.procUpdateLoc(logId,methodNms,recCrnwrkmtl  ,RecSetLoc2 );
				return YsConstant.RETN_CD_SUCCESS;
				*/
				return YsConstant.RETN_CD_FAILURE;
			}

			// 적치 가능 여부 CEHCK	
			for(int i = 1; i <= outRsResult.size(); i++) {
	
				outRsResult.absolute(i);
				outRecResult  = outRsResult.getRecord();
				szYS_STK_COL_GP = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));		//권하지시위치 TEMP
				szYS_STK_BED_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));		//권하지시위치 TEMP
			
				// 적치 가능 여부 CEHCK	
				recResult = this.procLocAbleCheck(logId, methodNms, szYS_STK_COL_GP,szYS_STK_BED_NO);
				
				if(commUtils.trim(recResult.getFieldString("ABLE_YN"  )).equals("Y")){
					szYS_DN_WO_LOC 		= commUtils.trim(recResult.getFieldString("YS_DN_WO_LOC"  )); //CA0104		
					szYS_DN_WO_LAYER 	= commUtils.trim(recResult.getFieldString("YS_DN_WO_LAYER"  )); //02
					break;	
				} else {
					continue;
				}
			}	
		}
		
		if (szYS_DN_WO_LOC.equals("")) {
	
			szLogMsg = LocalmethodNm + " TOSQL:["+szYD_CRN_SCH_ID+ "] 동일한 고객사["+szCUST_CD+"], 상세착지["+szDETAIL_ARR_CD+"], HEAT_NO["+szHEAT_NO+"]의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			
			outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getMainWrkMoveRbB_PIDEV", logId, methodNm, "동일 적치가능한 베드 조회");
			if (outRsResult.size() <= 0) {  //여기서 기존 에는 없으면 EXCEPTION. 신규에는 없으면(만적시) 자동 신규 구역으로 권하위치 할당.
				szLogMsg = LocalmethodNm + "BED 검색 실패.";// 지정위치 KBC1으로 권하위치 할당 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				/*
				JDTORecord RecSetLoc2 = JDTORecordFactory.getInstance().create();
				RecSetLoc2.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
				RecSetLoc2.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
				RecSetLoc2.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
				RecSetLoc2.setField("YS_UP_WO_LOC", 		szYS_UP_WO_LOC); 
				RecSetLoc2.setField("YS_UP_WO_LAYER",	szYS_UP_WO_LAYER);	 
				RecSetLoc2.setField("YS_DN_WO_LOC", 		"KBC10101");//szYS_DN_WO_LOC);
				RecSetLoc2.setField("YS_DN_WO_LAYER", 	"01");//szYS_DN_WO_LAYER);
				RecSetLoc2.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
				RecSetLoc2.setField("YD_RCPT_PLN_STR_LOC","");
				
				this.procUpdateLoc(logId,methodNms,recCrnwrkmtl  ,RecSetLoc2 );
				return YsConstant.RETN_CD_SUCCESS;
				*/
				return YsConstant.RETN_CD_FAILURE;
			}
		    // 적치 가능 여부 CEHCK	
			for(int i = 1; i <= outRsResult.size(); i++) {
	
				outRsResult.absolute(i);
				outRecResult  = outRsResult.getRecord();
				szYS_STK_COL_GP = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));		//권하지시위치 TEMP
				szYS_STK_BED_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));		//권하지시위치 TEMP
			
				// 적치 가능 여부 CEHCK	
				recResult = this.procLocAbleCheck(logId, methodNms, szYS_STK_COL_GP,szYS_STK_BED_NO);
				
				if(commUtils.trim(recResult.getFieldString("ABLE_YN"  )).equals("Y")){
					szYS_DN_WO_LOC 		= commUtils.trim(recResult.getFieldString("YS_DN_WO_LOC"  )); //CA0104		
					szYS_DN_WO_LAYER 	= commUtils.trim(recResult.getFieldString("YS_DN_WO_LAYER"  )); //02
					break;	
				} else {
					continue;
				}
			}	
		}
		JDTORecord RecSetLoc = JDTORecordFactory.getInstance().create();
		RecSetLoc.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
		RecSetLoc.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
		RecSetLoc.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
		RecSetLoc.setField("YS_UP_WO_LOC", 		szYS_UP_WO_LOC); 
		RecSetLoc.setField("YS_UP_WO_LAYER",	szYS_UP_WO_LAYER);	 
		RecSetLoc.setField("YS_DN_WO_LOC", 		szYS_DN_WO_LOC);
		RecSetLoc.setField("YS_DN_WO_LAYER", 	szYS_DN_WO_LAYER);
		RecSetLoc.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
		RecSetLoc.setField("YD_RCPT_PLN_STR_LOC","");	
		
		
		this.procUpdateLoc(logId,methodNms,recCrnwrkmtl  ,RecSetLoc );
		//----------------------------------------------------------------------------------------------------------------------
    	// ERROR 발생시 ?
		//----------------------------------------------------------------------------------------------------------------------
		return YsConstant.RETN_CD_SUCCESS;
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
	public JDTORecord procLocAbleCheck(String logId, String methodNms, String szYS_STK_COL_GP, String szYS_STK_BED_NO) throws JDTOException {
		String methodNm = "적재가능 여부 check[GdsYsSchSeEJB.procLocAbleCheck] < " + methodNms;
		String LocalmethodNm = "적재가능 여부 check[GdsYsSchSeEJB.procLocAbleCheck]";
	
		String szLogMsg					= null;
		JDTORecord		recInBed		= null;
		String szYS_DN_WO_LOC			= "";
		String szYS_DN_WO_LAYER			= null;
		JDTORecordSet RsResultBed 	    = null;
		JDTORecord    outRecResult 		= null;
		
		JDTORecord RecRtn = JDTORecordFactory.getInstance().create();	
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");
			
		recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YS_STK_COL_GP", 	szYS_STK_COL_GP);		//권하지시위치 TEMP
		recInBed.setField("YS_STK_BED_NO", 	szYS_STK_BED_NO);		//권하지시위치 TEMP
		/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedAnalysis 
		SELECT B.YD_STR_GTR_CD
		     , B.YS_STK_BED_L_GP
		     , B.YS_STK_BED_W_GP
		     , B.YD_STK_BED_ACT_STAT
		     , B.YD_STK_BED_WHIO_STAT
		     , B.YD_STK_BED_LYR_MAX
		     , B.YD_STK_BED_WT_MAX
		     , B.YD_STK_BED_H_MAX
		     , B.YD_STK_BED_L_MAX
		     , B.YD_STK_BED_W_MAX
		     , B.YS_STK_COL_GP
		     , B.YS_STK_BED_NO
		     , NVL(C.YS_STK_LYR_NO,'00') AS YS_STK_LYR_NO
		     , C.SSTL_NO
		     , C.YD_STK_LYR_ACT_STAT
		     , C.YD_STK_LYR_MTL_STAT
		     , DECODE(NVL(SUM(C.U_CNT) OVER() , 0) , 0 , C.YD_STK_LYR_MTL_STAT, 'U') AS YD_STK_LYR_MTL_STAT_U  --적치상태 
		     , NVL((SELECT MIN(YD_CHG_NO) 
				           FROM TB_YS_STOCK A1
				              , TB_YS_STKLYR B1
				         WHERE A1.SSTL_NO = B1.SSTL_NO
				           AND B1.YS_STK_COL_GP = B.YS_STK_COL_GP
				           AND B1.YS_STK_BED_NO = B.YS_STK_BED_NO
				           AND A1.YD_CHG_NO IS NOT NULL
				       ),0) YD_CHG_NO
		  FROM (
		        SELECT A.YS_STK_COL_GP
		             , A.YS_STK_BED_NO
		             , A.YD_STR_GTR_CD
		             , A.YS_STK_BED_L_GP
		             , A.YS_STK_BED_W_GP
		             , A.YD_STK_BED_ACT_STAT
		             , A.YD_STK_BED_WHIO_STAT
		             , A.YD_STK_BED_LYR_MAX
		             , A.YD_STK_BED_WT_MAX
		             , A.YD_STK_BED_H_MAX
		             , A.YD_STK_BED_L_MAX
		             , A.YD_STK_BED_W_MAX
		         FROM TB_YS_STKBED A
		        WHERE A.YS_STK_COL_GP = :V_YS_STK_COL_GP
		          AND A.YS_STK_BED_NO = :V_YS_STK_BED_NO
		          AND A.DEL_YN = 'N'
		        ) B
		     , (
		       SELECT A.YS_STK_COL_GP
		            , A.YS_STK_BED_NO
		            , A.YS_STK_LYR_NO
		            , A.SSTL_NO
		            , A.YD_STK_LYR_ACT_STAT
		            , A.YD_STK_LYR_MTL_STAT
		            , CASE WHEN A.YD_STK_LYR_MTL_STAT = 'U' THEN 1 ELSE 0 END AS U_CNT 
		         FROM TB_YS_STKLYR A
		        WHERE A.YS_STK_COL_GP = :V_YS_STK_COL_GP
		          AND A.YS_STK_BED_NO = :V_YS_STK_BED_NO
		          AND A.YD_STK_LYR_ACT_STAT = 'E'
		          AND A.YD_STK_LYR_MTL_STAT IN ('C', 'U', 'D')
		          AND A.DEL_YN = 'N'
		       ) C
		 WHERE B.YS_STK_COL_GP = C.YS_STK_COL_GP(+)
		   AND B.YS_STK_BED_NO = C.YS_STK_BED_NO(+)
		 ORDER BY B.YS_STK_COL_GP ASC, B.YS_STK_BED_NO ASC, C.YS_STK_LYR_NO DESC
    	 */
    	
    	RsResultBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedAnalysis", logId, methodNm, "이적 가능BED 조회");
		if (RsResultBed.size() <= 0) {
			szLogMsg = LocalmethodNm + "이적 가능 BED 검색 실패 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			
		}
		RsResultBed.first();
		outRecResult = RsResultBed.getRecord();

		String szYD_STK_BED_ACT_STAT  	= commUtils.trim(outRecResult.getFieldString("YD_STK_BED_ACT_STAT"  ));
		String szYD_STK_BED_WHIO_STAT 	= commUtils.trim(outRecResult.getFieldString("YD_STK_BED_WHIO_STAT"  ));
		String szYD_STK_LYR_MTL_STAT  	= commUtils.trim(outRecResult.getFieldString("YD_STK_LYR_MTL_STAT_U"  ));
		String szYS_STK_LYR_NO 			= commUtils.trim(outRecResult.getFieldString("YS_STK_LYR_NO"  ));
		int iYD_STK_BED_LYR_MAX 		= commUtils.paraRecChkNullInt(outRecResult,"YD_STK_BED_LYR_MAX");
		
		if( !szYD_STK_BED_ACT_STAT.equals("L") ) {  // 적치가능
			szLogMsg = LocalmethodNm + " 해당하는 적치열["+szYS_STK_COL_GP+"], 적치베드["+szYS_STK_BED_NO+"]의 활성상태["+szYD_STK_BED_ACT_STAT+"]가 적치가능상태가 아닙니다.";
			commUtils.printLog(logId, szLogMsg, "SL");
			RecRtn.setField("ABLE_YN", 	"N");  //불가
			return RecRtn;

		}
		
		if( !szYD_STK_BED_WHIO_STAT.equals("E") ) { // 입출고 가능 
			szLogMsg =LocalmethodNm + " 해당하는 적치열["+szYS_STK_COL_GP+"], 적치베드["+szYS_STK_BED_NO+"]의 입출고상태["+szYD_STK_BED_WHIO_STAT+"]가 입고가능상태가 아닙니다.";
			commUtils.printLog(logId, szLogMsg, "SL");
			RecRtn.setField("ABLE_YN", 	"N");  //불가
			return RecRtn;

		}
		
		if( szYD_STK_LYR_MTL_STAT.equals("U")) {				//권상대기이면 적치불가능
			szLogMsg = LocalmethodNm + "  적치재료상태["+szYD_STK_LYR_MTL_STAT+"]가 권상대기이므로 적치불가능";
			commUtils.printLog(logId, szLogMsg, "SL");
			RecRtn.setField("ABLE_YN", 	"N");  //불가
			return RecRtn;

		}
		
		/*
		 * 권하위치 최종결정정보 셋팅.
		 */
		szYS_DN_WO_LOC		= szYS_STK_COL_GP + szYS_STK_BED_NO;
		szYS_DN_WO_LAYER 	= szYS_STK_LYR_NO;
		//----------------------------------------------------------------------------------------------------------------------
		//	적치가능한 베드의 적치단을 1 증가 시킴 - 값이 없으면 01(1단)으로 설정 (단,TYMultiLyr는 제외함)
		//----------------------------------------------------------------------------------------------------------------------
		if( szYS_DN_WO_LAYER.equals("00") ) {							//값이 없으면
			szYS_DN_WO_LAYER = "01";										 //1단
		}else{														     //값 이 존재하면
			if("TY".equals(szYS_DN_WO_LOC.substring(2, 4))){ // <- (TYMultiLyr는 제외한 곳)
				szYS_DN_WO_LAYER = szYS_STK_LYR_NO;
			} else{
				szYS_DN_WO_LAYER = commUtils.stringPlusInt(szYS_STK_LYR_NO, 1);	//조회된 적치단 + 1
			}
			//12.04		
			// 야드별로 적재 가능 단 확인 후 수정 해야 함
			if(Integer.parseInt(szYS_DN_WO_LAYER) > iYD_STK_BED_LYR_MAX){
	        	szLogMsg = "["+ LocalmethodNm +"] 최대 적재 가능단 초과 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				RecRtn.setField("ABLE_YN", 	"N");  //불가
				return RecRtn;
	        }		
		}
		
	    if(!szYS_DN_WO_LOC.equals("")) {
	    	RecRtn.setField("ABLE_YN", 	"Y");
	    } else {
	    	RecRtn.setField("ABLE_YN", 	"N");
	    }
	    
		RecRtn.setField("YS_DN_WO_LOC", 	szYS_DN_WO_LOC);
		RecRtn.setField("YS_DN_WO_LAYER", 	szYS_DN_WO_LAYER);
		
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
	public JDTORecord procLocAbleCheck_XXYY(String logId, String methodNms, String szYS_STK_COL_GP, String szYS_STK_BED_NO) throws JDTOException {
		String methodNm = "적재가능 여부 check[GdsYsSchSeEJB.procLocAbleCheck_XXYY] < " + methodNms;
		String LocalmethodNm = "적재가능 여부 check[GdsYsSchSeEJB.procLocAbleCheck_XXYY]";
	
		String szLogMsg					= null;
		JDTORecord		recInBed		= null;
		String szYS_DN_WO_LOC			= "";
		String szYS_DN_WO_LAYER			= null;
		JDTORecordSet RsResultBed 	    = null;
		JDTORecord    outRecResult 		= null;
		
		JDTORecord RecRtn = JDTORecordFactory.getInstance().create();	
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");
			
		recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YS_STK_COL_GP", 	szYS_STK_COL_GP);		//권하지시위치 TEMP
		recInBed.setField("YS_STK_BED_NO", 	szYS_STK_BED_NO);		//권하지시위치 TEMP
		
    	RsResultBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedAnalysis", logId, methodNm, "이적 가능BED 조회");
		if (RsResultBed.size() <= 0) {
			szLogMsg = LocalmethodNm + "이적 가능 BED 검색 실패 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			
		}
		RsResultBed.first();
		outRecResult = RsResultBed.getRecord();

		String szYD_STK_BED_ACT_STAT  	= commUtils.trim(outRecResult.getFieldString("YD_STK_BED_ACT_STAT"  ));
		String szYD_STK_BED_WHIO_STAT 	= commUtils.trim(outRecResult.getFieldString("YD_STK_BED_WHIO_STAT"  ));
		String szYD_STK_LYR_MTL_STAT  	= commUtils.trim(outRecResult.getFieldString("YD_STK_LYR_MTL_STAT_U"  ));
		String szYS_STK_LYR_NO 			= commUtils.trim(outRecResult.getFieldString("YS_STK_LYR_NO"  ));
		int iYD_STK_BED_LYR_MAX 		= commUtils.paraRecChkNullInt(outRecResult,"YD_STK_BED_LYR_MAX");
		
		if( !szYD_STK_BED_ACT_STAT.equals("L") ) {  // 적치가능
			szLogMsg = LocalmethodNm + " 해당하는 적치열["+szYS_STK_COL_GP+"], 적치베드["+szYS_STK_BED_NO+"]의 활성상태["+szYD_STK_BED_ACT_STAT+"]가 적치가능상태가 아닙니다.";
			commUtils.printLog(logId, szLogMsg, "SL");
			RecRtn.setField("ABLE_YN", 	"N");  //불가
			return RecRtn;

		}
		/* 
		 * KB010101,KB010201,KB010301,KB010401,KB010501, KB010601, KB010701, KB010801, KB010901, KB011001
		 * 해당 위치들의 완산상태(YD_STK_BED_WHIO_STAT) 값이 'G'로 셋팅되어 있슴
		 * 메인/보조 위치결정시 적재가능여부 체크는 'E'로 하고 있슴
		 * 따라서 메인/보조 위치결정시에는 해당위치들은 체크로직에서 걸러짐
		 */
		if( !szYD_STK_BED_WHIO_STAT.equals("G") ) { // 출하가적베드  가능 
			szLogMsg =LocalmethodNm + " 해당하는 적치열["+szYS_STK_COL_GP+"], 적치베드["+szYS_STK_BED_NO+"]의 입출고상태["+szYD_STK_BED_WHIO_STAT+"]가 출하가적베드 가능상태가 아닙니다.";
			commUtils.printLog(logId, szLogMsg, "SL");
			RecRtn.setField("ABLE_YN", 	"N");  //불가
			return RecRtn;

		}
		
		if( szYD_STK_LYR_MTL_STAT.equals("U")) {				//권상대기이면 적치불가능
			szLogMsg = LocalmethodNm + "  적치재료상태["+szYD_STK_LYR_MTL_STAT+"]가 권상대기이므로 적치불가능";
			commUtils.printLog(logId, szLogMsg, "SL");
			RecRtn.setField("ABLE_YN", 	"N");  //불가
			return RecRtn;

		}
		
		/*
		 * 권하위치 최종결정정보 셋팅.
		 */
		szYS_DN_WO_LOC		= szYS_STK_COL_GP + szYS_STK_BED_NO;
		szYS_DN_WO_LAYER 	= szYS_STK_LYR_NO;
		//----------------------------------------------------------------------------------------------------------------------
		//	적치가능한 베드의 적치단을 1 증가 시킴 - 값이 없으면 01(1단)으로 설정
		//----------------------------------------------------------------------------------------------------------------------
		if( szYS_DN_WO_LAYER.equals("00") ) {							//값이 없으면
			szYS_DN_WO_LAYER = "01";										 //1단
		}else{														     //값 이 존재하면
			szYS_DN_WO_LAYER = commUtils.stringPlusInt(szYS_STK_LYR_NO, 1);	//조회된 적치단 + 1
			//12.04		
			// 야드별로 적재 가능 단 확인 후 수정 해야 함
			if(Integer.parseInt(szYS_DN_WO_LAYER) > iYD_STK_BED_LYR_MAX){
	        	szLogMsg = "["+ LocalmethodNm +"] 최대 적재 가능단 초과 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				RecRtn.setField("ABLE_YN", 	"N");  //불가
				return RecRtn;
	        }		
		}
		
	    if(!szYS_DN_WO_LOC.equals("")) {
	    	RecRtn.setField("ABLE_YN", 	"Y");
	    } else {
	    	RecRtn.setField("ABLE_YN", 	"N");
	    }
	    
		RecRtn.setField("YS_DN_WO_LOC", 	szYS_DN_WO_LOC);
		RecRtn.setField("YS_DN_WO_LAYER", 	szYS_DN_WO_LAYER);
		
		return RecRtn;
		
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
	public String procUpdateLoc(String logId, String methodNms, JDTORecord recCrnwrkmtl, JDTORecord RecSetLoc) throws JDTOException {
		String methodNm = "TO위치 UPDATE[GdsYsSchSeEJB.procUpdateLoc] < " + methodNms;
		String LocalmethodNm = "TO위치 UPDATE[GdsYsSchSeEJB.procUpdateLoc] ";
		String szLogMsg					= null;
		JDTORecord		recInBed		= null;
		int intRtnVal					= 0;
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");
		try {

			String szYD_EQP_WRK_MAX_W 	= commUtils.trim(recCrnwrkmtl.getFieldString("MAX_MTL_W"  ));		//크레인작업재료 중 최대 폭
			String szYD_EQP_WRK_MAX_L 	= commUtils.trim(recCrnwrkmtl.getFieldString("MAX_MTL_L"  ));		//크레인작업재료 중 최대 길이
			int intYD_EQP_WRK_SH    	= commUtils.paraRecChkNullInt(recCrnwrkmtl,"SH_CNT");				//크레인작업재료 총매수
			int intYD_EQP_WRK_WT    	= commUtils.paraRecChkNullInt(recCrnwrkmtl,"SUM_MTL_WT");			//크레인작업재료 총중량
			double dblYD_EQP_WRK_T     	= commUtils.paraRecChkNullDouble(recCrnwrkmtl,"SUM_MTL_T");			//크레인작업재료 총높이

			String szYD_CRN_SCH_ID  	= commUtils.trim(RecSetLoc.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
			String szYD_EQP_ID  		= commUtils.trim(RecSetLoc.getFieldString("YD_EQP_ID"  ));	
			String szYD_SCH_CD  		= commUtils.trim(RecSetLoc.getFieldString("YD_SCH_CD"  ));	
			String szYS_UP_WO_LOC		= commUtils.trim(RecSetLoc.getFieldString("YS_UP_WO_LOC"  ));			
			String szYS_UP_WO_LAYER		= commUtils.trim(RecSetLoc.getFieldString("YS_UP_WO_LAYER"  ));			
			String szYS_DN_WO_LOC		= commUtils.trim(RecSetLoc.getFieldString("YS_DN_WO_LOC"  ));			
			String szYS_DN_WO_LAYER		= commUtils.trim(RecSetLoc.getFieldString("YS_DN_WO_LAYER"  ));			
			String szYD_RCPT_PLN_STR_LOC= commUtils.trim(RecSetLoc.getFieldString("YD_RCPT_PLN_STR_LOC"  )); // 입고예정위치			
			String szYD_WBOOK_ID  		= commUtils.trim(RecSetLoc.getFieldString("YD_WBOOK_ID"  ));		//작업예약

			String szYD_TO_LOC_DCSN_MTD = commUtils.trim(recCrnwrkmtl.getFieldString("YD_TO_LOC_DCSN_MTD"  ));		//주작업 구분

			String szMODIFIER 			= commUtils.trim(recCrnwrkmtl.getFieldString("MODIFIER"  ));		//MODIFIER
			if (szYS_DN_WO_LOC.equals("")) {

				return YsConstant.RETN_CD_FAILURE;
			}

			commUtils.printParam(logId, RecSetLoc);
			//----------------------------------------------------------------------------------------------------------------------
			// 권하지시위치 수정
			//----------------------------------------------------------------------------------------------------------------------
			szLogMsg = LocalmethodNm+ " 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]을 크레인스케줄에 수정 시작";
			commUtils.printLog(logId, szLogMsg, "SL");

			JDTORecordSet RsBedUpXy = JDTORecordFactory.getInstance().createRecordSet("");
			recInBed= JDTORecordFactory.getInstance().create();
			recInBed.setField("YS_STK_COL_GP", 			szYS_UP_WO_LOC.substring(0, 6)); //권상지시위치
			recInBed.setField("YS_STK_BED_NO", 			szYS_UP_WO_LOC.substring(6));	 //권상지시위치
				
			/* Bed정보 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybed 
			SELECT YS_STK_COL_GP 
			      ,YS_STK_BED_NO 
			      ,YD_STR_GTR_CD 
			      ,YS_STK_BED_TP 
			      ,YS_STK_BED_T_GP 
			      ,YS_STK_BED_W_GP 
			      ,YS_STK_BED_L_GP 
			      ,YS_OUTDIA_GRP_GP
			      ,YD_STK_BED_DIR_GP 
			      ,YD_STK_BED_ACT_STAT 
			      ,YD_STK_BED_WHIO_STAT 
			      ,YD_STK_BED_USG_GP
			      ,YD_STK_BED_XAXIS 
			      ,YD_STK_BED_YAXIS
			      ,YD_STK_BED_ZAXIS
			      ,YD_STK_BED_LYR_MAX
			      ,YD_STK_BED_WT_MAX 
			      ,YD_STK_BED_H_MAX 
			      ,YD_STK_BED_L_MAX 
			      ,YD_STK_BED_W_MAX 
			      ,YD_STK_BED_XAXIS_TOL 
			      ,YD_STK_BED_YAXIS_TOL 
			      , (SELECT YD_STK_COL_DIR_GP FROM TB_YS_STKCOL WHERE YS_STK_COL_GP = A.YS_STK_COL_GP AND ROWNUM = 1) AS YD_STK_COL_DIR_GP
			  FROM TB_YS_STKBED
			 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
			   AND DEL_YN ='N'
				 */  
			RsBedUpXy = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybed", logId, methodNm, "권상 BED 좌표 조회");
			if (RsBedUpXy.size() <= 0) {
				szLogMsg =  LocalmethodNm+ "권상 BED 좌표 조회 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				
			}
			RsBedUpXy.first();
			JDTORecord RecUpBedXy = RsBedUpXy.getRecord();
	
			JDTORecordSet RsDnBedXy = JDTORecordFactory.getInstance().createRecordSet("");
			recInBed= JDTORecordFactory.getInstance().create();
			recInBed.setField("YS_STK_COL_GP", 			szYS_DN_WO_LOC.substring(0, 6));										//권하지시위치
			recInBed.setField("YS_STK_BED_NO", 			szYS_DN_WO_LOC.substring(6));										//권하지시위치
				

			/* Bed정보 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybed 
			SELECT YS_STK_COL_GP 
			      ,YS_STK_BED_NO 
			      ,YD_STR_GTR_CD 
			      ,YS_STK_BED_TP 
			      ,YS_STK_BED_T_GP 
			      ,YS_STK_BED_W_GP 
			      ,YS_STK_BED_L_GP 
			      ,YS_OUTDIA_GRP_GP
			      ,YD_STK_BED_DIR_GP 
			      ,YD_STK_BED_ACT_STAT 
			      ,YD_STK_BED_WHIO_STAT 
			      ,YD_STK_BED_USG_GP
			      ,YD_STK_BED_XAXIS 
			      ,YD_STK_BED_YAXIS
			      ,YD_STK_BED_ZAXIS
			      ,YD_STK_BED_LYR_MAX
			      ,YD_STK_BED_WT_MAX 
			      ,YD_STK_BED_H_MAX 
			      ,YD_STK_BED_L_MAX 
			      ,YD_STK_BED_W_MAX 
			      ,YD_STK_BED_XAXIS_TOL 
			      ,YD_STK_BED_YAXIS_TOL 
			      , (SELECT YD_STK_COL_DIR_GP FROM TB_YS_STKCOL WHERE YS_STK_COL_GP = A.YS_STK_COL_GP AND ROWNUM = 1) AS YD_STK_COL_DIR_GP
			  FROM TB_YS_STKBED
			 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
			   AND DEL_YN ='N'
				 */  
			RsDnBedXy = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybed", logId, methodNm, "권하 BED 좌표 조회");
			if (RsDnBedXy.size() <= 0) {
				szLogMsg = LocalmethodNm+ "권하 BED 좌표 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				
			}
			RsDnBedXy.first();
			JDTORecord RecDnBedXy = RsDnBedXy.getRecord();
			
			
			JDTORecord recUpCrnSch = JDTORecordFactory.getInstance().create();
			recUpCrnSch.setField("YD_CRN_SCH_ID", 				szYD_CRN_SCH_ID);										//크레인스케줄ID
			recUpCrnSch.setField("YD_EQP_ID", 					szYD_EQP_ID);											//크레인설비ID
			
			//권상정보   					
			recUpCrnSch.setField("YS_UP_WO_LOC", 				szYS_UP_WO_LOC);										//권상지시위치
			recUpCrnSch.setField("YS_UP_WO_LAYER", 				szYS_UP_WO_LAYER);										//권상지시단
			recUpCrnSch.setField("YD_UP_STK_COL_GP", 			szYS_UP_WO_LOC.substring(0, 6));						//권상지시위치 - 적치열
			recUpCrnSch.setField("YD_UP_STK_BED_NO", 			szYS_UP_WO_LOC.substring(6));							//권상지시위치 - 적치베드
			/**********************************************************
			* 1. 저장열 방향구분 = “주행방향” 이고 Crane =“ACR1” or “ACR2” or “BCR1” or “BCR2” 이면,
			*   ->  X 좌표값  = 기준값 + (제품길이 /2)::::Y 좌표값  = 기준값 
			* 2. 저장열 방향구분 = “횡행방향” 이고 Crane =“ACR1” or “ACR2” or “BCR1” or “BCR2” 이면,
			*   ->  X 기준값  = 기준값                           ::::Y 좌표값  = 기준값 + (제품길이 /2)
			**********************************************************/
			if(szYS_UP_WO_LOC.substring(2, 4).equals("TR")||szYS_UP_WO_LOC.substring(2, 4).equals("TC")||szYS_UP_WO_LOC.substring(2, 4).equals("TS")) {
				recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
				recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
				
			} else if(commUtils.trim(RecUpBedXy.getFieldString("YD_STK_COL_DIR_GP")).equals("X") && szYD_EQP_ID.substring(2, 4).equals("CR")) {
				recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",      String.valueOf(commUtils.paraRecChkNullInt(RecUpBedXy,"YD_STK_BED_XAXIS")
						                                                     +(Integer.parseInt(szYD_EQP_WRK_MAX_L) /2))) ;
				recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
				
			} else if(commUtils.trim(RecUpBedXy.getFieldString("YD_STK_COL_DIR_GP")).equals("Y") && szYD_EQP_ID.substring(2, 4).equals("CR")) {
				recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
				recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",      String.valueOf(commUtils.paraRecChkNullInt(RecUpBedXy,"YD_STK_BED_YAXIS")
																			 +(Integer.parseInt(szYD_EQP_WRK_MAX_L) /2))) ;
			} else {
				recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
				recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
			}
			
			recUpCrnSch.setField("YD_UP_WO_LOC_ZAXIS",  		commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_ZAXIS"  )) ) ;
			recUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MAX",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			recUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MIN",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			recUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MAX",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			recUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MIN",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS1",  		"" ) ;
			recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS2",  		"" ) ;
			recUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MAX",  	"" ) ;
			recUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MIN",  	"" ) ;
			//권하정보   					
			recUpCrnSch.setField("YS_DN_WO_LOC", 				szYS_DN_WO_LOC);										//권하지시위치
			recUpCrnSch.setField("YS_DN_WO_LAYER", 				szYS_DN_WO_LAYER);										//권하지시단
			recUpCrnSch.setField("YD_DN_STK_COL_GP", 			szYS_DN_WO_LOC.substring(0, 6));						//권하지시위치 - 적치열
			recUpCrnSch.setField("YD_DN_STK_BED_NO", 			szYS_DN_WO_LOC.substring(6));							//권하지시위치 - 적치베드
	
			if(szYS_DN_WO_LOC.substring(2, 4).equals("TR")||szYS_DN_WO_LOC.substring(2, 4).equals("TC")||szYS_DN_WO_LOC.substring(2, 4).equals("TS")) {
				recUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
				recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
				
			} else if(commUtils.trim(RecDnBedXy.getFieldString("YD_STK_COL_DIR_GP")).equals("X") && szYD_EQP_ID.substring(2, 4).equals("CR")) {
				recUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",      String.valueOf(commUtils.paraRecChkNullInt(RecDnBedXy,"YD_STK_BED_XAXIS")
																			 +(Integer.parseInt(szYD_EQP_WRK_MAX_L) /2))) ;
				recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
				
			} else if(commUtils.trim(RecDnBedXy.getFieldString("YD_STK_COL_DIR_GP")).equals("Y") && szYD_EQP_ID.substring(2, 4).equals("CR")) {
				recUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
	
				recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",      String.valueOf(commUtils.paraRecChkNullInt(RecDnBedXy,"YD_STK_BED_YAXIS")
																			 +(Integer.parseInt(szYD_EQP_WRK_MAX_L) /2))) ;
			} else {
				recUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
				recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
			}
	
			recUpCrnSch.setField("YD_DN_WO_LOC_ZAXIS",  		commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_ZAXIS"  )) ) ;
			recUpCrnSch.setField("YD_DN_WO_XAXIS_GAP_MAX",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			recUpCrnSch.setField("YD_DN_WO_XAXIS_GAP_MIN",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			recUpCrnSch.setField("YD_DN_WO_YAXIS_GAP_MAX",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			recUpCrnSch.setField("YD_DN_WO_YAXIS_GAP_MIN",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS1",  		"" ) ;
			recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS2",  		"" ) ;
			recUpCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MAX",  	"" ) ;
			recUpCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MIN",  	"" ) ;
	
	
			//기타   					
			recUpCrnSch.setField("YD_EQP_WRK_SH", 				String.valueOf(intYD_EQP_WRK_SH));						//크레인작업재료 총매수
			recUpCrnSch.setField("YD_EQP_WRK_WT", 				String.valueOf(intYD_EQP_WRK_WT));						//크레인작업재료 총중량
			recUpCrnSch.setField("YD_EQP_WRK_T", 				String.valueOf(dblYD_EQP_WRK_T));						//크레인작업재료 총높이
			recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 			szYD_EQP_WRK_MAX_W);									//크레인작업재료 중 최대 폭
			recUpCrnSch.setField("YD_EQP_WRK_MAX_L", 			szYD_EQP_WRK_MAX_L);									//크레인작업재료 중 최대 길이
			recUpCrnSch.setField("MODIFIER", 					szMODIFIER);
		
			
			intRtnVal = commDao.update(recUpCrnSch, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCrnWrkSidedelyn", logId, methodNm, "크레인스케쥴 갱신");
			
			if(intRtnVal <= 0) {
				szLogMsg = LocalmethodNm+ " 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단[" +szYS_DN_WO_LAYER +" ]을 크레인스케줄에 수정 중 ERROR 발생";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YsConstant.RETN_CD_FAILURE;
			}		
			
			//----------------------------------------------------------------------------------------------------------------------
			//	권하지시위치에 재료를 권하대기로 등록
			//  권상위치 정보 READ 하여 권하 위치 SET
			//----------------------------------------------------------------------------------------------------------------------
	
			JDTORecordSet rsOutBed = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecordSet rsMaxSeq = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord resOutBed= JDTORecordFactory.getInstance().create();
			
			commUtils.printLog(logId, LocalmethodNm+ " 스케쥴 코드[" + szYD_SCH_CD + "] 주/보조구분 "+ szYD_TO_LOC_DCSN_MTD, "SL");
			if(szYD_SCH_CD.substring(2,4).equals("PC") || (szYD_SCH_CD.substring(2,6).equals("TR10") && (!szYD_TO_LOC_DCSN_MTD.equals("W")))){
	
				recInBed= JDTORecordFactory.getInstance().create();
				recInBed.setField("YS_STK_COL_GP", 	szYS_UP_WO_LOC.substring(0, 6));										
				recInBed.setField("YS_STK_BED_NO", 	szYS_UP_WO_LOC.substring(6));										
				recInBed.setField("YS_STK_LYR_NO", 	commUtils.stringPlusInt(szYS_UP_WO_LAYER,0));										
				recInBed.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID);
				recInBed.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID);
					
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyrByStlNoStockRbA 
				SELECT A.YS_STK_COL_GP            AS YS_STK_COL_GP
				     , A.YS_STK_BED_NO            AS YS_STK_BED_NO
				     , A.YS_STK_LYR_NO            AS YS_STK_LYR_NO
				     , A.YS_STK_SEQ_NO            AS YS_STK_SEQ_NO
				     , A.SSTL_NO                   AS SSTL_NO
				     , B.YD_STK_LOT_TP
				     , B.YD_STK_LOT_CD
				     , B.HCR_GP
				     , B.STL_PROG_CD
				     , B.YS_MTL_ITEM
				  FROM TB_YS_STKLYR A
				     , TB_YS_STOCK B 
				     , (SELECT YD_WBOOK_ID    AS YD_WBOOK_ID                                                       
				              ,YS_STK_COL_GP  AS YS_STK_COL_GP                                                     
				              ,YS_STK_BED_NO  AS YS_STK_BED_NO                                                     
				              ,YS_STK_LYR_NO  AS YS_STK_LYR_NO                                                     
				              ,YS_STK_SEQ_NO  AS YS_STK_SEQ_NO                                                     
				              ,YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ                                                    
				              ,SSTL_NO         AS SSTL_NO
				              ,REG_DDTT       AS REG_DDTT
				         FROM TB_YS_WRKBOOKMTL                                                                      
				        WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID  
				        ) C    
				     , TB_YS_CRNWRKMTL D  
				 WHERE A.SSTL_NO = B.SSTL_NO
				   AND A.SSTL_NO = C.SSTL_NO
				   AND A.SSTL_NO = D.SSTL_NO
				   AND D.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND A.YS_STK_COL_GP = :V_YS_STK_COL_GP
				   AND A.SSTL_NO IS NOT NULL 
				   AND A.DEL_YN = 'N'
				ORDER BY YS_STK_COL_GP, CASE WHEN YS_STK_COL_GP IN ('KAPC02') THEN  YS_STK_BED_NO ELSE '0' END  DESC
				                      , CASE WHEN YS_STK_COL_GP IN ('KAPC02') THEN  '0'           ELSE YS_STK_BED_NO END 
				                      , YS_STK_SEQ_NO
				 */
					
				rsOutBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyrByStlNoStockRbA", logId, methodNm, "권상정보 검색");
				if (rsOutBed.size() <= 0) {
					szLogMsg = LocalmethodNm+ "권상정보 BED 검색 실패 ";
					commUtils.printLog(logId, szLogMsg, "SL");
					
				}	
				
			} else if("TY".equals(szYD_SCH_CD.substring(2,4)) && "W".equals(szYD_TO_LOC_DCSN_MTD)) {
				recInBed= JDTORecordFactory.getInstance().create();
				recInBed.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);	
				recInBed.setField("YS_STK_COL_GP", 			szYS_UP_WO_LOC.substring(0, 6));										
				recInBed.setField("YS_STK_BED_NO", 			szYS_UP_WO_LOC.substring(6));										
				recInBed.setField("YS_STK_LYR_NO", 			commUtils.stringPlusInt(szYS_UP_WO_LAYER,0));										
						
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyrTYMultiLyr
				SELECT YS_STK_COL_GP            AS YS_STK_COL_GP
				     , YS_STK_BED_NO            AS YS_STK_BED_NO
				     , YS_STK_LYR_NO            AS YS_STK_LYR_NO
				     , YS_STK_SEQ_NO            AS YS_STK_SEQ_NO
				     , SSTL_NO                   AS SSTL_NO
				  FROM TB_YS_STKLYR
				 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
				   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
				--   AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
				   AND SSTL_NO IS NOT NULL
				   AND SSTL_NO IN (SELECT SSTL_NO FROM TB_YS_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
				   AND DEL_YN = 'N'
				*/
				
				rsOutBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyrTYMultiLyr", logId, methodNm, "TY multi lyr 권상정보 검색");  // TY MULTI LYR 권하 결정 타는 곳 (비오비오)
				if (rsOutBed.size() <= 0) {
					szLogMsg = LocalmethodNm+ "권상정보 BED 검색 실패 ";
					commUtils.printLog(logId, szLogMsg, "SL");
					
				}	
			} else {
				recInBed= JDTORecordFactory.getInstance().create();
				recInBed.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);	
				recInBed.setField("YS_STK_COL_GP", 			szYS_UP_WO_LOC.substring(0, 6));										
				recInBed.setField("YS_STK_BED_NO", 			szYS_UP_WO_LOC.substring(6));										
				recInBed.setField("YS_STK_LYR_NO", 			commUtils.stringPlusInt(szYS_UP_WO_LAYER,0));										
						
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyr
				SELECT YS_STK_COL_GP            AS YS_STK_COL_GP
				     , YS_STK_BED_NO            AS YS_STK_BED_NO
				     , YS_STK_LYR_NO            AS YS_STK_LYR_NO
				     , YS_STK_SEQ_NO            AS YS_STK_SEQ_NO
				     , SSTL_NO                   AS SSTL_NO
				  FROM TB_YS_STKLYR
				 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
				   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
				   AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
				   AND SSTL_NO IS NOT NULL
				   AND SSTL_NO IN (SELECT SSTL_NO FROM TB_YS_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
				   AND DEL_YN = 'N'
				*/
				
				
				rsOutBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyr", logId, methodNm, "권상정보 검색");
				if (rsOutBed.size() <= 0) {
					szLogMsg = LocalmethodNm+ "권상정보 BED 검색 실패 ";
					commUtils.printLog(logId, szLogMsg, "SL");
					
				}	
			}
			
			JDTORecord jrParam= JDTORecordFactory.getInstance().create();
			JDTORecord recPara= JDTORecordFactory.getInstance().create();
			//적치단의 재료상태를 권하대기로 변경
			for(int i = 1; i <= rsOutBed.size(); i++) {
				rsOutBed.absolute(i);
				resOutBed  = rsOutBed.getRecord();
			
				jrParam.setField("YS_STK_COL_GP", 	szYS_DN_WO_LOC.substring(0, 6));
				jrParam.setField("YS_STK_BED_NO", 	szYS_DN_WO_LOC.substring(6));
				jrParam.setField("YS_STK_LYR_NO", 	commUtils.stringPlusInt(szYS_DN_WO_LAYER,0)); // 권하 LYR 결정 (비오비오)
				jrParam.setField("SSTL_NO",       	commUtils.trim(resOutBed.getFieldString("SSTL_NO"  )));
				jrParam.setField("YD_STK_LYR_MTL_STAT", "D");
		
				commUtils.printLog(logId, LocalmethodNm+ " 스케쥴 코드[" + szYD_SCH_CD + " 주여구분 "+ szYD_TO_LOC_DCSN_MTD + " rsOutBed.size():" +rsOutBed.size(), "SL");
				if((szYD_SCH_CD.substring(2,6).equals("TR10") && (!szYD_TO_LOC_DCSN_MTD.equals("W")))){
					
					
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDnLyr 
					SELECT NVL(MAX(YS_STK_LYR_NO),0) + 1 AS MAX_YS_STK_LYR_NO
					    FROM TB_YS_STKLYR
					   WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
					     AND YS_STK_BED_NO = :V_YS_STK_BED_NO 
					     AND SSTL_NO IS NOT NULL
					 */    
					rsMaxSeq = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDnLyr", logId, methodNm, "권상정보 검색");
					rsMaxSeq.first();
					recPara = rsMaxSeq.getRecord();
					
					String szYS_STK_LYR_NO = commUtils.trim(recPara.getFieldString("MAX_YS_STK_LYR_NO"  ));
					
					commUtils.printLog(logId, LocalmethodNm + commUtils.trim(recPara.getFieldString("MAX_YS_STK_LYR_NO"  )) + ">>1"+rsOutBed.size()  , "SL"); 
					szYS_DN_WO_LAYER = szYS_STK_LYR_NO;
					jrParam.setField("YS_STK_LYR_NO", 	 szYS_STK_LYR_NO); 
					jrParam.setField("YS_STK_SEQ_NO", 	 "1");
					
				// SC 짝수열 입고시 SEQ_NO 바뀜	
				} else if(szYD_SCH_CD.substring(0,4).equals("KAHS")){
						
					if (szYS_DN_WO_LOC.substring(4, 6).equals("02")||szYS_DN_WO_LOC.substring(4, 6).equals("04")||szYS_DN_WO_LOC.substring(4, 6).equals("06")
					||	szYS_DN_WO_LOC.substring(4, 6).equals("08")||szYS_DN_WO_LOC.substring(4, 6).equals("10")||szYS_DN_WO_LOC.substring(4, 6).equals("12")
					||  szYS_DN_WO_LOC.substring(4, 6).equals("14")) { 
						
						
						commUtils.printLog(logId, LocalmethodNm + szYS_DN_WO_LOC + ">>2"+rsOutBed.size()  , "SL");
						
						if(rsOutBed.size() == 2){
							if(commUtils.trim(resOutBed.getFieldString("YS_STK_SEQ_NO")).equals("1")){
								jrParam.setField("YS_STK_SEQ_NO","2");  
							} else {
								jrParam.setField("YS_STK_SEQ_NO","1");
							}
						} else {
							/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDn 
							SELECT NVL(MAX(YS_STK_SEQ_NO),0) + 1 AS MAX_YS_STK_SEQ_NO 
							    FROM TB_YS_STKLYR
							   WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
							     AND YS_STK_BED_NO = :V_YS_STK_BED_NO 
							     AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
							     AND SSTL_NO IS NOT NULL
							 */    
							rsMaxSeq = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDn", logId, methodNm, "권하정보 검색");
							rsMaxSeq.first();
							recPara = rsMaxSeq.getRecord();
							commUtils.printLog(logId, LocalmethodNm + commUtils.trim(recPara.getFieldString("MAX_YS_STK_SEQ_NO"  )) + ">>2"+rsOutBed.size()  , "SL"); 
							
							jrParam.setField("YS_STK_SEQ_NO", 	 commUtils.trim(recPara.getFieldString("MAX_YS_STK_SEQ_NO"  ))); 
						}
					} else {
						
						commUtils.printLog(logId, LocalmethodNm + szYS_DN_WO_LOC + ">>3"+rsOutBed.size()  , "SL");
						
						if(rsOutBed.size() == 2){
							jrParam.setField("YS_STK_SEQ_NO",commUtils.trim(resOutBed.getFieldString("YS_STK_SEQ_NO")));
						} else {
							/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDn 
							SELECT NVL(MAX(YS_STK_SEQ_NO),0) + 1 AS MAX_YS_STK_SEQ_NO 
							    FROM TB_YS_STKLYR
							   WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
							     AND YS_STK_BED_NO = :V_YS_STK_BED_NO 
							     AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
							     AND SSTL_NO IS NOT NULL
							 */    
							rsMaxSeq = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDn", logId, methodNm, "권하정보 검색");
							rsMaxSeq.first();
							recPara = rsMaxSeq.getRecord();
							
							commUtils.printLog(logId, LocalmethodNm + commUtils.trim(recPara.getFieldString("MAX_YS_STK_SEQ_NO"  )) + ">>3"+rsOutBed.size()  , "SL"); 
							
							jrParam.setField("YS_STK_SEQ_NO", 	 commUtils.trim(recPara.getFieldString("MAX_YS_STK_SEQ_NO"  ))); 
						}	
					}
					
				} else if(szYS_DN_WO_LOC.substring(1, 4).equals("A01")){
					
					if (!szYS_UP_WO_LOC.substring(4, 6).equals(szYS_DN_WO_LOC.substring(4, 6))){ 
						
						commUtils.printLog(logId, LocalmethodNm + szYS_DN_WO_LOC + ">>4"+rsOutBed.size()  , "SL");
						
						if(rsOutBed.size() == 2){
							if(commUtils.trim(resOutBed.getFieldString("YS_STK_SEQ_NO")).equals("1")){
								jrParam.setField("YS_STK_SEQ_NO","2");  
							} else {
								jrParam.setField("YS_STK_SEQ_NO","1");
							}
						} else if(rsOutBed.size() == 1){
							/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDn 
							SELECT NVL(MAX(YS_STK_SEQ_NO),0) + 1 AS MAX_YS_STK_SEQ_NO 
							    FROM TB_YS_STKLYR
							   WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
							     AND YS_STK_BED_NO = :V_YS_STK_BED_NO 
							     AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
							     AND SSTL_NO IS NOT NULL
							 */    
							rsMaxSeq = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDn", logId, methodNm, "권하정보 검색");
							rsMaxSeq.first();
							recPara = rsMaxSeq.getRecord();
							
							commUtils.printLog(logId, LocalmethodNm + commUtils.trim(recPara.getFieldString("MAX_YS_STK_SEQ_NO"  )) + ">>4"+rsOutBed.size()  , "SL"); 
							
							jrParam.setField("YS_STK_SEQ_NO", 	 commUtils.trim(recPara.getFieldString("MAX_YS_STK_SEQ_NO"  )));  
						
						} else {
							jrParam.setField("YS_STK_SEQ_NO",commUtils.trim(resOutBed.getFieldString("YS_STK_SEQ_NO")));	
						}
					} else {
						commUtils.printLog(logId, LocalmethodNm + szYS_DN_WO_LOC + ">>5"+rsOutBed.size()  , "SL");
						if(rsOutBed.size() == 1){
							/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDn 
							SELECT NVL(MAX(YS_STK_SEQ_NO),0) + 1 AS MAX_YS_STK_SEQ_NO 
							    FROM TB_YS_STKLYR
							   WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
							     AND YS_STK_BED_NO = :V_YS_STK_BED_NO 
							     AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
							     AND SSTL_NO IS NOT NULL
							 */    
							rsMaxSeq = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDn", logId, methodNm, "권하정보 검색");
							rsMaxSeq.first();
							recPara = rsMaxSeq.getRecord();
							commUtils.printLog(logId, LocalmethodNm + commUtils.trim(recPara.getFieldString("MAX_YS_STK_SEQ_NO"  )) + ">>5"+rsOutBed.size()  , "SL"); 
							
							jrParam.setField("YS_STK_SEQ_NO", 	 commUtils.trim(recPara.getFieldString("MAX_YS_STK_SEQ_NO"  )));  
						
						} else {
							jrParam.setField("YS_STK_SEQ_NO",commUtils.trim(resOutBed.getFieldString("YS_STK_SEQ_NO")));	
						}
	
					}
					
				} else {
					commUtils.printLog(logId, LocalmethodNm + szYS_DN_WO_LOC + ">>6"+rsOutBed.size()  , "SL");
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDn 
					SELECT NVL(MAX(YS_STK_SEQ_NO),0) + 1 AS MAX_YS_STK_SEQ_NO 
					    FROM TB_YS_STKLYR
					   WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
					     AND YS_STK_BED_NO = :V_YS_STK_BED_NO 
					     AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
					     AND SSTL_NO IS NOT NULL
					 */    
					rsMaxSeq = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDn", logId, methodNm, "권하정보 검색"); // 권하정보 seq 하나씩 늘리기
					rsMaxSeq.first();
					recPara = rsMaxSeq.getRecord();
					commUtils.printLog(logId, LocalmethodNm + commUtils.trim(recPara.getFieldString("MAX_YS_STK_SEQ_NO"  )) + ">>6"+rsOutBed.size()  , "SL"); 
					
					jrParam.setField("YS_STK_SEQ_NO", 	 commUtils.trim(recPara.getFieldString("MAX_YS_STK_SEQ_NO"  )));  
				}		
				
	
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColBedGp  
		    	UPDATE TB_YS_STKLYR            
		    	   SET MOD_DDTT     = SYSDATE             
		    		 , MODIFIER     = :V_MODIFIER             
		    		 , YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT,YD_STK_LYR_ACT_STAT)
		    	     , SSTL_NO = NVL(:V_SSTL_NO,SSTL_NO)
		    	     , YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT,YD_STK_LYR_MTL_STAT)
		    	 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
		    	   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
		    	   AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO   
		    	   AND YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO   
		    	 */  
				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColBedGp", logId, methodNm, "TB_YS_STKLYR 갱신");
				
				if(intRtnVal <= 0) {
					commUtils.printLog(logId, LocalmethodNm+ " 적치단[" + jrParam.getFieldString("YS_STK_COL_GP") + "]활성화중 ERROR 발생", "SL");
					return YsConstant.RETN_CD_FAILURE;
				}
				
				/**********************************************************
				* 입고일때 입고 예정 위치 SET
				**********************************************************/
				JDTORecord jrParam1= JDTORecordFactory.getInstance().create();
				if (szYD_RCPT_PLN_STR_LOC.length() > 6) {
					if (szYD_SCH_CD.substring(2,4).equals("PC") && szYD_SCH_CD.substring(6,7).equals("L") && szYD_RCPT_PLN_STR_LOC.substring(2,3).equals("0")) { 
						jrParam1= JDTORecordFactory.getInstance().create();
						jrParam1.setResultCode(logId);	//Log ID
						jrParam1.setResultMsg(methodNm);	//Log Method Name						
						jrParam1.setField("SSTL_NO"				,commUtils.trim(resOutBed.getFieldString("SSTL_NO"  )));
						jrParam1.setField("YD_RCPT_PLN_STR_LOC"	,szYD_RCPT_PLN_STR_LOC);
						jrParam1.setField("MODIFIER"			,szMODIFIER);
						
						EJBConnector tranConn = new EJBConnector("default", "GdsYsSchSeEJB", this);
						tranConn.trx("updStock", new Class[] { JDTORecord.class }, new Object[] { jrParam1 });
						
					}
				}
				

				// 제품창고(K) B동 일반야드에 권하시 Bed 활성상태 변경 (해당 bed에 제품이 있어도 권하시 update. 해당 bed에 같은 길이 구분의 제품이 적치되어 있다는 전제)
				if(!szYS_DN_WO_LOC.equals("")) {
					if (szYS_DN_WO_LOC.matches("[K][B]\\d\\d\\d\\d\\d\\d") && szYS_DN_WO_LAYER.equals("01")) { 	
						jrParam.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID); //야드크레인스케쥴ID
						jrParam.setField("YS_STK_COL_GP", szYS_DN_WO_LOC.substring(0, 6)); //야드적치열구분
						jrParam.setField("YS_STK_BED_NO", szYS_DN_WO_LOC.substring(6, 8)); //야드적치Bed번호
						
						commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkBedActStat", logId, methodNm, "적치Bed 활성상태 변경");
					}
				}
			}
			szLogMsg =LocalmethodNm +" 크레인스케쥴 ID["+szYD_CRN_SCH_ID+"] TO위치결정>>>>>>>> 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]" ;
			commUtils.printLog(logId, szLogMsg, "SL");
			commUtils.printLog(logId, methodNm, "S-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return YsConstant.RETN_CD_SUCCESS;
	}
	/**
	 * A동 봉강보조작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procDummyToLocRbA(String logId, String methodNms, JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recWbook) throws JDTOException {
    	String methodNm = "A동 봉강보조작업TO위치결정[GdsYsSchSeEJB.procDummyToLocRbA] < " + methodNms;
    	String LocalmethodNm = "A동 봉강보조작업TO위치결정[GdsYsSchSeEJB.procDummyToLocRbA]";
		String szLogMsg					= null;
		JDTORecordSet	rsResult		= null;
		JDTORecordSet	rsStock			= null;
		JDTORecord		recStock		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		JDTORecord		recCrnwrkmtl	= null;
		String szYS_UP_WO_LOC			= null;
		String szYS_UP_WO_LAYER			= null;
		String szYS_DN_WO_LOC			= null;
		String szYS_DN_WO_LAYER			= null;
		String szYD_UP_STK_COL_GP		= null;
		String szYD_UP_STK_BED_NO		= null;
		JDTORecordSet outRsResult 		= null;
		JDTORecord    outRecResult 		= null;

		String szYS_STK_COL_GP  = "";
		String szYS_STK_BED_NO  = "";
		String szYS_STK_LYR_NO  = "";
		
		
		commUtils.printLog(logId, methodNm, "S+");

		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		recCrnwrkmtl = rsCrnwrkmtl.getRecord();
		
		String szYD_CRN_SCH_ID 	= commUtils.trim(recCrnwrkmtl.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
		String szSSTL_NO 		= commUtils.trim(recCrnwrkmtl.getFieldString("SSTL_NO"  ));			//크레인작업재료
		String szYD_EQP_ID  	= commUtils.trim(recCrnSch.getFieldString("YD_EQP_ID"  ));		//크레인설비ID
		String szYD_SCH_CD 		= commUtils.trim(recWbook.getFieldString("YD_SCH_CD"  ));			//크레인스케줄코드
		String szYD_WBOOK_ID 	= commUtils.trim(recWbook.getFieldString("YD_WBOOK_ID"  ));			//크레인스케줄코드
//		int intMTL_CNT 			= Integer.parseInt(commUtils.nvl(recCrnwrkmtl.getFieldString("SH_CNT"),"0"));
		
//		String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "*", "*");
//		if("Y".equals(sApplyYnPI)){
			rsStock = commDao.select(recCrnwrkmtl, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStock_PI_PIDEV", logId, methodNm, "저장품 조회");
			if (rsStock.size() <= 0) {
				szLogMsg = "["+ methodNm +"]  재료정보["+szSSTL_NO+"]를 저장품에서 조회 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return "0";
			}
//		} else {
//			rsStock = commDao.select(recCrnwrkmtl, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStock", logId, methodNm, "저장품 조회");
//			if (rsStock.size() <= 0) {
//				szLogMsg = "["+ methodNm +"]  재료정보["+szSSTL_NO+"]를 저장품에서 조회 실패 ";
//				commUtils.printLog(logId, szLogMsg, "SL");
//				return "0";
//			}
//		}
		rsStock.first();
		recStock = rsStock.getRecord();
		
		String szCUST_CD   		= commUtils.trim(recStock.getFieldString("CUST_CD"  ));			//고객사
		String szHEAT_NO   		= commUtils.trim(recStock.getFieldString("HEAT_NO"  ));			//HEAT_NO
		String szDETAIL_ARR_CD 	= commUtils.trim(recStock.getFieldString("DETAIL_ARR_CD"  ));			//상세착지
		String szBUNDLE_T 		= commUtils.trim(recStock.getFieldString("BUNDLE_T"  ));	//BUNDLE 두께
		
		szLogMsg = LocalmethodNm + "크레인작업재료의  단 최대SEQ ["+szSSTL_NO+"]를 저장품에서 조회 완료 - 고객사["+szCUST_CD+"], 상세착지["+szDETAIL_ARR_CD+"], HEAT_NO["+szHEAT_NO+"]";
		commUtils.printLog(logId, szLogMsg, "SL");

		//----------------------------------------------------------------------------------------------------------------------
		//	권상중이거나 권하중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYS_UP_WO_LOC 		= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LOC"  ));		
		szYS_UP_WO_LAYER 	= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LAYER"  ));		

		if( szYS_UP_WO_LOC.equals("") ) {
			
			szLogMsg = LocalmethodNm + " 크레인작업재료의  재료정보["+szSSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			
			 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO 
			SELECT YS_STK_COL_GP            AS YS_STK_COL_GP
			      ,YS_STK_BED_NO            AS YS_STK_BED_NO
			      ,YS_STK_LYR_NO            AS YS_STK_LYR_NO
			      ,YS_STK_SEQ_NO            AS YS_STK_SEQ_NO
			      ,REGISTER                 AS REGISTER
			      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
			      ,MODIFIER                 AS MODIFIER
			      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
			      ,DEL_YN                   AS DEL_YN
			      ,SSTL_NO                   AS SSTL_NO
			      ,YD_STK_LYR_ACT_STAT      AS YD_STK_LYR_ACT_STAT
			      ,YD_STK_LYR_MTL_STAT      AS YD_STK_LYR_MTL_STAT
			      ,YD_STK_LYR_XAXIS         AS YD_STK_LYR_XAXIS
			      ,YD_STK_LYR_YAXIS         AS YD_STK_LYR_YAXIS
			      ,YD_STK_LYR_ZAXIS         AS YD_STK_LYR_ZAXIS
			  FROM TB_YS_STKLYR
			 WHERE SSTL_NO = :V_SSTL_NO
			   AND NVL(YD_STK_LYR_MTL_STAT, '*') = :V_YD_STK_LYR_MTL_STAT
			   AND DEL_YN='N'
			*/
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("SSTL_NO", szSSTL_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT", "D");
			
			rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO", logId, methodNm, "적재위치조회 조회");
			if (rsResult.size() <= 0) {

				recPara.setField("YD_STK_LYR_MTL_STAT", "U");
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO", logId, methodNm, "적재위치조회 조회");
				if (rsResult.size() <= 0) {
					return "0";
				}	
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YS_STK_COL_GP");
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YS_STK_BED_NO");
			szYS_UP_WO_LOC 			= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYS_UP_WO_LAYER 		= recTemp.getFieldString("YS_STK_LYR_NO");
			
		}else{
		
			szYD_UP_STK_COL_GP = szYS_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYS_UP_WO_LOC.substring(6);
			
		}
		szLogMsg = LocalmethodNm + " 조회된 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]";
		commUtils.printLog(logId, szLogMsg, "SL");
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	재료와 같은 고객사CODE + 상세착지 + HEAT 의 적치가능한 베드 해당 동의 모든 위치를 조회 
		//----------------------------------------------------------------------------------------------------------------------
		
		//권상지시위치에 따라 알맞은 적치가능한 베드 검색 방법을 적용
		recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("HEAT_NO", 			szHEAT_NO);											//크레인작업 최하단재료의 HEAT_NO
    	recTemp.setField("CUST_CD", 			szCUST_CD);											//크레인작업 최하단재료의 고객사
    	recTemp.setField("DETAIL_ARR_CD", 		szDETAIL_ARR_CD);									//크레인작업 최하단재료의 상세착지
    	recTemp.setField("YD_UP_STK_LOC", 		szYS_UP_WO_LOC+szYS_UP_WO_LAYER );	                //권상위치	
    	recTemp.setField("BUNDLE_T", 			szBUNDLE_T);										//번들두께
    	
    	commUtils.printParam(logId, recTemp);
    	
    	szLogMsg = LocalmethodNm + " TOSQL:["+szYD_CRN_SCH_ID+ "] 동일한 고객사["+szCUST_CD+"], 상세착지["+szDETAIL_ARR_CD+"], HEAT_NO["+szHEAT_NO+"]의 적치가능한 베드 조회 시작";
		commUtils.printLog(logId, szLogMsg, "SL");
			
		/* com.inisteel.cim.ys.common.dao.YsCommDAO.getDummyMoveRbA_PIDEV
		WITH PARA_TBL1 AS (
		 SELECT :V_HEAT_NO AS P_HEAT_NO
		      , :V_CUST_CD AS P_CUST_CD
		      , :V_DETAIL_ARR_CD AS P_DETAIL_ARR_CD
		      , :V_YD_UP_STK_LOC AS P_YD_UP_STK_LOC   -- 해당열
		      , (SELECT ITEM 
		               FROM USRYSA.TB_YS_RULE
		              WHERE REPR_CD_GP = 'K00003'
		                AND CD_GP = '0001') AS RULE_T 
		   FROM DUAL
		)
		SELECT A.SEQ_NUM 
		     , A.YS_STK_COL_GP
		     , A.YS_STK_BED_NO
		     , A.YS_STK_LYR_NO
		     , A.YS_STK_SEQ_NO
		     , A.SSTL_NO
		     , A.MTL_STAT_UP_CNT 
		  FROM
		(  
		        SELECT CASE WHEN NVL(B.HEAT_NO,'*') = C.P_HEAT_NO AND NVL(B.CUST_CD,'*') = C.P_CUST_CD AND NVL(B.DETAIL_ARR_CD,'*') = C.P_DETAIL_ARR_CD  THEN '8' 
		                    WHEN NVL(B.HEAT_NO,'*') = C.P_HEAT_NO AND NVL(B.CUST_CD,'*') = C.P_CUST_CD                                                   THEN '7' 
		                    WHEN NVL(B.CUST_CD,'*') = C.P_CUST_CD AND NVL(B.DETAIL_ARR_CD,'*') = C.P_DETAIL_ARR_CD                                       THEN '6' 
		                    WHEN NVL(B.CUST_CD,'*') = C.P_CUST_CD                                                                                        THEN '5' 
		                    ELSE '1' END  SEQ_NUM 
		             , A.YS_STK_COL_GP
		             , A.YS_STK_BED_NO
		             , A.YS_STK_LYR_NO 
		             , A.YS_STK_SEQ_NO 
		             , A.SSTL_NO
		             , A.MTL_STAT_UP_CNT 
		          FROM
		             (
		               SELECT YS_STK_COL_GP
		                    , YS_STK_BED_NO
		                    , YS_STK_LYR_NO
		                    , '2' AS YS_STK_SEQ_NO
		                    , SSTL_NO
		                    , MTL_STAT_UP_CNT
		                 FROM
		                      (
		                           SELECT SL.YS_STK_COL_GP
		                                , SL.YS_STK_BED_NO
		                                , SL.YS_STK_LYR_NO
		                                , MAX(CASE WHEN SL.YS_STK_SEQ_NO = '1' AND SL.SSTL_NO IS NOT NULL THEN 'Y' ELSE 'N' END) AS DAN_1
		                                , MAX(CASE WHEN SL.YS_STK_SEQ_NO = '2' AND SL.SSTL_NO IS NULL     THEN 'Y' ELSE 'N' END) AS DAN_2
		                                , MAX(SL.SSTL_NO)    AS SSTL_NO
		                                , COUNT(SL.SSTL_NO)  AS SSTL_CNT
		                                , (SUM(DECODE(SL.YD_STK_LYR_MTL_STAT,'U',1,0)) +
		                                   (SELECT COUNT(*) FROM TB_YS_WRKBOOKMTL 
		                                     WHERE DEL_YN ='N' 
		                                       AND YS_STK_COL_GP = SL.YS_STK_COL_GP
		                                       AND YS_STK_BED_NO = SL.YS_STK_BED_NO
		                                       AND YS_STK_LYR_NO = SL.YS_STK_LYR_NO
		                                    )  
		                                  )     AS MTL_STAT_UP_CNT --작업대기수
		                            FROM TB_YS_STKLYR SL
		                               , PARA_TBL1   CP
		                           WHERE SL.YS_STK_COL_GP = SUBSTR(CP.P_YD_UP_STK_LOC,1,6) --같은열작업
		                             AND SL.YS_STK_LYR_NO >= (CASE WHEN :V_BUNDLE_T < CP.RULE_T THEN 1 ELSE 4  END )
		                             AND SL.YS_STK_LYR_NO <= (CASE WHEN :V_BUNDLE_T < CP.RULE_T THEN 3 ELSE 99 END )
		                           GROUP BY SL.YS_STK_COL_GP, SL.YS_STK_BED_NO, SL.YS_STK_LYR_NO
		                       ) 
		                 WHERE DAN_1 = 'Y'
		                   AND DAN_2 = 'Y'
		                   AND SSTL_CNT = 1 
		                   AND MTL_STAT_UP_CNT = 0
		               ) A
		             , TB_YS_STOCK  B 
		             , PARA_TBL1  C
		             , TB_YS_STKBED D
		         WHERE A.SSTL_NO = B.SSTL_NO
		           AND A.YS_STK_COL_GP || A.YS_STK_BED_NO || A.YS_STK_LYR_NO  <> C.P_YD_UP_STK_LOC  --자신위치 제외
		           AND A.YS_STK_COL_GP = D.YS_STK_COL_GP
		           AND A.YS_STK_BED_NO = D.YS_STK_BED_NO
		           AND NVL(D.YD_STK_BED_ACT_STAT,'*') = 'L'
		        
		        UNION ALL 
		        
		        SELECT A1.SEQ_NUM 
		             , A1.YS_STK_COL_GP
		             , A1.YS_STK_BED_NO
		             , A1.YS_STK_LYR_NO 
		             , A1.YS_STK_SEQ_NO 
		             , A1.SSTL_NO
		             , A1.MTL_STAT_UP_CNT
		          FROM
		               (  
		                SELECT '2' SEQ_NUM 
		                     , C.YS_STK_COL_GP 
		                     , C.YS_STK_BED_NO 
		                     , C.YS_STK_LYR_NO 
		                     , '1' AS YS_STK_SEQ_NO 
		                     , ''  AS SSTL_NO 
		                     , 0   AS MTL_STAT_UP_CNT
		                  FROM               
		                       (SELECT COUNT(SL.SSTL_NO)  AS SUM_CNT 
		                             , SL.YS_STK_COL_GP 
		                             , SL.YS_STK_BED_NO 
		                             , SL.YS_STK_LYR_NO 
		                          FROM TB_YS_STKLYR SL 
		                             , PARA_TBL1   CP
		                             , TB_YS_STKBED SB
		                         WHERE SL.DEL_YN = 'N' 
		                           AND SL.YS_STK_COL_GP = SB.YS_STK_COL_GP
		                           AND SL.YS_STK_BED_NO = SB.YS_STK_BED_NO
		                           AND NVL(SB.YD_STK_BED_ACT_STAT,'*') = 'L'
		                           AND SL.YS_STK_COL_GP = SUBSTR(CP.P_YD_UP_STK_LOC,1,6) --같은열작업
		                           AND SL.YS_STK_LYR_NO >= (CASE WHEN :V_BUNDLE_T < CP.RULE_T THEN 1 ELSE 4  END )
		                           AND SL.YS_STK_LYR_NO <= (CASE WHEN :V_BUNDLE_T < CP.RULE_T THEN 3 ELSE 99 END )
		                         GROUP BY SL.YS_STK_COL_GP,SL.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
		                         HAVING COUNT(SL.SSTL_NO) = 0               
		                       ) C 
		                ORDER BY SEQ_NUM DESC,YS_STK_COL_GP, YS_STK_BED_NO , ABS(YS_STK_LYR_NO - NVL(SUBSTR(:V_YD_UP_STK_LOC,9,2),'01'))
		                ) A1
		          WHERE ROWNUM = 1               
		) A
		ORDER BY SEQ_NUM DESC,YS_STK_COL_GP, YS_STK_BED_NO , ABS(YS_STK_LYR_NO - NVL(SUBSTR(:V_YD_UP_STK_LOC,9,2),'01'))
		*/
		
		
		outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getDummyMoveRbA_PIDEV", logId, methodNm, "DUMMY 적치가능한 베드 조회");
		if (outRsResult.size() <= 0) {
			szLogMsg =  LocalmethodNm+" 동일한 고객사["+szCUST_CD+"], 상세착지["+szDETAIL_ARR_CD+"], HEAT_NO["+szHEAT_NO+"]의 적치가능한 베드 조회 실패 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			return YsConstant.RETN_CD_FAILURE;
		}
	    

		outRsResult.first();
		outRecResult = outRsResult.getRecord();

		szYS_STK_COL_GP  	= commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));
		szYS_STK_BED_NO 	= commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));
		szYS_STK_LYR_NO  	= commUtils.trim(outRecResult.getFieldString("YS_STK_LYR_NO"  ));
	
		/*
		 * 권하위치 최종결정정보 셋팅.
		 */
		szYS_DN_WO_LOC		= szYS_STK_COL_GP + szYS_STK_BED_NO;
		szYS_DN_WO_LAYER 	= szYS_STK_LYR_NO;
	
		JDTORecord RecSetLoc = JDTORecordFactory.getInstance().create();
		RecSetLoc.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
		RecSetLoc.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
		RecSetLoc.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
		RecSetLoc.setField("YS_UP_WO_LOC", 		szYS_UP_WO_LOC); 
		RecSetLoc.setField("YS_UP_WO_LAYER",	szYS_UP_WO_LAYER);	 
		RecSetLoc.setField("YS_DN_WO_LOC", 		szYS_DN_WO_LOC);
		RecSetLoc.setField("YS_DN_WO_LAYER", 	szYS_DN_WO_LAYER);
		RecSetLoc.setField("YD_RCPT_PLN_STR_LOC","");
		RecSetLoc.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID); 
		
		this.procUpdateLoc(logId,methodNms,recCrnwrkmtl  ,RecSetLoc );
		//----------------------------------------------------------------------------------------------------------------------
		// ERROR 발생시 ?
		//----------------------------------------------------------------------------------------------------------------------
		
		commUtils.printLog(logId, methodNm, "S-");
		return YsConstant.RETN_CD_SUCCESS;
	}
	/**
	 * B동 봉강보조작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procDummyToLocRbB(String logId, String methodNms, JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recWbook) throws JDTOException {
    	String methodNm = "B동 봉강보조작업TO위치결정[GdsYsSchSeEJB.procDummyToLocRbB] < " + methodNms;
    	String LocalmethodNm = "B동 봉강보조작업TO위치결정[GdsYsSchSeEJB.procDummyToLocRbB]";

    	String szLogMsg					= null;
		JDTORecordSet	rsResult		= null;
		JDTORecordSet	rsStock			= null;
		JDTORecord		recStock		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		// 적치 가능 여부 CEHCK	
		JDTORecord      recResult       = null;
		

		JDTORecord		recCrnwrkmtl	= null;
		
		String szYS_UP_WO_LOC			= null;
		String szYS_UP_WO_LAYER			= null;
		String szYS_DN_WO_LOC			= null;
		String szYS_DN_WO_LAYER			= null;
		String szYD_UP_STK_COL_GP		= null;
		String szYD_UP_STK_BED_NO		= null;
		int intMTL_CNT			= 0;								//재료매수
		JDTORecordSet outRsResult 		= null;
		JDTORecord    outRecResult 		= null;

		String szYS_STK_COL_GP  = "";
		String szYS_STK_BED_NO  = "";

		
		commUtils.printLog(logId, methodNm, "S+");

		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		recCrnwrkmtl = rsCrnwrkmtl.getRecord();
		
		String szYD_CRN_SCH_ID = commUtils.trim(recCrnwrkmtl.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
		String szSSTL_NO 	   = commUtils.trim(recCrnwrkmtl.getFieldString("SSTL_NO"  ));			//크레인작업재료
		String szYD_EQP_ID     = commUtils.trim(recCrnSch.getFieldString("YD_EQP_ID"  ));			//크레인설비ID
		String szYD_SCH_CD 	   = commUtils.trim(recWbook.getFieldString("YD_SCH_CD"  ));			//크레인스케줄코드
		intMTL_CNT 			   = Integer.parseInt(commUtils.nvl(recCrnwrkmtl.getFieldString("SH_CNT"),"0"));
		String szYD_WBOOK_ID   = commUtils.trim(recWbook.getFieldString("YD_WBOOK_ID"  ));			//크레인설비ID
		
//		String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "*", "*");
//		if("Y".equals(sApplyYnPI)){
			rsStock = commDao.select(recCrnwrkmtl, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStock_PI_PIDEV", logId, methodNm, "저장품 조회");
			if (rsStock.size() <= 0) {
				szLogMsg = "["+ methodNm +"]  재료정보["+szSSTL_NO+"]를 저장품에서 조회 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return "0";
			}
//		} else {
//			rsStock = commDao.select(recCrnwrkmtl, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStock", logId, methodNm, "저장품 조회");
//			if (rsStock.size() <= 0) {
//				szLogMsg = "["+ methodNm +"]  재료정보["+szSSTL_NO+"]를 저장품에서 조회 실패 ";
//				commUtils.printLog(logId, szLogMsg, "SL");
//				return "0";
//			}			
//		}
		rsStock.first();
		recStock = rsStock.getRecord();
		
		String szCUST_CD   		= commUtils.trim(recStock.getFieldString("CUST_CD"  ));			//고객사
		String szHEAT_NO   		= commUtils.trim(recStock.getFieldString("HEAT_NO"  ));			//HEAT_NO
		String szDETAIL_ARR_CD 	= commUtils.trim(recStock.getFieldString("DETAIL_ARR_CD"  ));	//상세착지
		String szYD_MTL_L_GP 	= commUtils.trim(recStock.getFieldString("YD_MTL_L_GP"  ));		//재료의길이구분
		
		szLogMsg = LocalmethodNm + " 크레인작업재료의  단 최대SEQ ["+szSSTL_NO+"]를 저장품에서 조회 완료 - 고객사["+szCUST_CD+"], 상세착지["+szDETAIL_ARR_CD+"], HEAT_NO["+szHEAT_NO+"]";
		commUtils.printLog(logId, szLogMsg, "SL");

		//----------------------------------------------------------------------------------------------------------------------
		//	권상중이거나 권하중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYS_UP_WO_LOC 		= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LOC"  ));		
		szYS_UP_WO_LAYER 	= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LAYER"  ));		

		if( szYS_UP_WO_LOC.equals("") ) {
			
			szLogMsg = LocalmethodNm + "  크레인작업재료의  재료정보["+szSSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			
			 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO 
			SELECT YS_STK_COL_GP            AS YS_STK_COL_GP
			      ,YS_STK_BED_NO            AS YS_STK_BED_NO
			      ,YS_STK_LYR_NO            AS YS_STK_LYR_NO
			      ,YS_STK_SEQ_NO            AS YS_STK_SEQ_NO
			      ,REGISTER                 AS REGISTER
			      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
			      ,MODIFIER                 AS MODIFIER
			      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
			      ,DEL_YN                   AS DEL_YN
			      ,SSTL_NO                   AS SSTL_NO
			      ,YD_STK_LYR_ACT_STAT      AS YD_STK_LYR_ACT_STAT
			      ,YD_STK_LYR_MTL_STAT      AS YD_STK_LYR_MTL_STAT
			      ,YD_STK_LYR_XAXIS         AS YD_STK_LYR_XAXIS
			      ,YD_STK_LYR_YAXIS         AS YD_STK_LYR_YAXIS
			      ,YD_STK_LYR_ZAXIS         AS YD_STK_LYR_ZAXIS
			  FROM TB_YS_STKLYR
			 WHERE SSTL_NO = :V_SSTL_NO
			   AND NVL(YD_STK_LYR_MTL_STAT, '*') = :V_YD_STK_LYR_MTL_STAT
			   AND DEL_YN='N'
			*/
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("SSTL_NO", szSSTL_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT", "D");
			
			rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO", logId, methodNm, "적재위치조회 조회");
			if (rsResult.size() <= 0) {

				recPara.setField("YD_STK_LYR_MTL_STAT", "U");
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO", logId, methodNm, "적재위치조회 조회");
				if (rsResult.size() <= 0) {
					return "0";
				}	
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YS_STK_COL_GP");
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YS_STK_BED_NO");
			szYS_UP_WO_LOC 			= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYS_UP_WO_LAYER 		= recTemp.getFieldString("YS_STK_LYR_NO");
			
		}else{
		
			szYD_UP_STK_COL_GP = szYS_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYS_UP_WO_LOC.substring(6);
		}

		szLogMsg =  LocalmethodNm+" 조회된 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]";
		commUtils.printLog(logId, szLogMsg, "SL");

		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	재료와 같은 고객사CODE + 상세착지 + HEAT 의 적치가능한 베드 해당 동의 모든 위치를 조회 
		//----------------------------------------------------------------------------------------------------------------------
		
		//권상지시위치에 따라 알맞은 적치가능한 베드 검색 방법을 적용
		recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("YS_STK_COL_GP", 		szYD_UP_STK_COL_GP);								//권상지시위치 - 적치열
    	recTemp.setField("YS_STK_BED_NO", 		szYD_UP_STK_BED_NO);								//권상지시위치 - 적치베드
    	recTemp.setField("YD_MTL_L_GP", 		szYD_MTL_L_GP);										//크레인작업 재료의 길이구분
    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);										//크레인 스케줄코드
    	recTemp.setField("HEAT_NO", 			szHEAT_NO);											//크레인작업 재료의 HEAT_NO
    	recTemp.setField("CUST_CD", 			szCUST_CD);											//크레인작업 재료의 고객사
    	recTemp.setField("DETAIL_ARR_CD", 		szDETAIL_ARR_CD);									//크레인작업 재료의 상세착지
    	recTemp.setField("YD_EQP_ID", 			szYD_EQP_ID);										//크레인설비ID
    	recTemp.setField("YD_UP_STK_LOC", 		szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO+szYS_UP_WO_LAYER);	 //권상위치	
    	recTemp.setField("SH_CNT", 				""+intMTL_CNT);										//재료매수
    	recTemp.setField("YD_CRN_SCH_ID", 		szYD_CRN_SCH_ID);	
    	
    	commUtils.printParam(logId, recTemp);
    	
    	//----------------------------------------------------------------------------------------------------------------------
		//	동일한  HEAT_NO의 적치가능한 베드 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = LocalmethodNm + " TOSQL:["+szYD_CRN_SCH_ID+ "] 동일한 고객사["+szCUST_CD+"], 상세착지["+szDETAIL_ARR_CD+"], HEAT_NO["+szHEAT_NO+"]의 적치가능한 베드 조회 시작";
		commUtils.printLog(logId, szLogMsg, "SL");
			
		/* com.inisteel.cim.ys.common.dao.YsCommDAO.getDummyMoveRbB_PIDEV 
		WITH PARA_TBL1 AS ( 
		 SELECT :V_HEAT_NO AS P_HEAT_NO 
		      , :V_CUST_CD AS P_CUST_CD 
		      , :V_DETAIL_ARR_CD AS P_DETAIL_ARR_CD 
		      , :V_YD_MTL_L_GP AS P_YD_MTL_L_GP 
		      , :V_YD_UP_STK_LOC AS P_YD_UP_STK_LOC     
		   FROM DUAL 
		) 
		SELECT A.SEQ_NUM  
		     , A.YS_STK_COL_GP 
		     , A.YS_STK_BED_NO
		     , A.YS_STK_LYR_NO
		     , A.YS_STK_SEQ_NO
		     , A.MAX_SSTL_NO 
		     , A.MTL_STAT_UP_CNT  
		  FROM 
		(   
		        SELECT CASE WHEN NVL(B.HEAT_NO,'*') = C.P_HEAT_NO AND NVL(B.CUST_CD,'*') = C.P_CUST_CD AND NVL(B.DETAIL_ARR_CD,'*') = C.P_DETAIL_ARR_CD  THEN '8'  
		                    WHEN NVL(B.HEAT_NO,'*') = C.P_HEAT_NO AND NVL(B.CUST_CD,'*') = C.P_CUST_CD                                                   THEN '7'  
		                    WHEN NVL(B.CUST_CD,'*') = C.P_CUST_CD AND NVL(B.DETAIL_ARR_CD,'*') = C.P_DETAIL_ARR_CD                                       THEN '6'  
		                    WHEN NVL(B.CUST_CD,'*') = C.P_CUST_CD                                                                                        THEN '5'  
		                    ELSE '1' END  SEQ_NUM  
		             , A.YS_STK_COL_GP 
		             , A.YS_STK_BED_NO 
		             , A.YS_STK_LYR_NO  
		             , A.YS_STK_SEQ_NO  
		             , A.MAX_SSTL_NO 
		             , A.MTL_STAT_UP_CNT  
		          FROM 
		             ( 
		               SELECT YS_STK_COL_GP 
		                    , YS_STK_BED_NO  
		                    , YS_STK_LYR_NO  
		                    , YS_STK_SEQ_NO  
		                    , MAX_SSTL_NO 
		                    , MTL_STAT_UP_CNT -- 권상예약 수 
		                 FROM  
		                        ( 
		                        SELECT A1.YS_STK_COL_GP 
		                             , A1.YS_STK_BED_NO
		                             , A1.YS_STK_LYR_NO
		                             , A1.YS_STK_SEQ_NO
		                             , A1.SSTL_NO       AS MAX_SSTL_NO 
		                             , A1.YD_STK_LYR_MTL_STAT 
		                               -- BED 최상단 정보 
		                             , COUNT(A1.SSTL_NO) OVER( PARTITION BY A1.YS_STK_COL_GP,A1.YS_STK_COL_GP,A1.YS_STK_BED_NO, A1.YS_STK_LYR_NO) AS SSTL_CNT  
		                             , ROW_NUMBER() OVER( PARTITION BY A1.YS_STK_COL_GP,A1.YS_STK_BED_NO ORDER BY A1.YS_STK_COL_GP,A1.YS_STK_BED_NO DESC, A1.YS_STK_LYR_NO DESC) AS MTL_DAN 
		                             , SUM(DECODE(A1.YD_STK_LYR_MTL_STAT,'U',1,0)) OVER (PARTITION BY A1.YS_STK_COL_GP,A1.YS_STK_COL_GP,A1.YS_STK_BED_NO )  AS MTL_STAT_UP_CNT 
		                          FROM TB_YS_STKLYR A1 
		                             , TB_YS_STKBED B1 
		                             , PARA_TBL1   C1 
		                         WHERE A1.YS_STK_COL_GP = B1.YS_STK_COL_GP 
		                           AND A1.SSTL_NO > ' ' 
		                           AND A1.DEL_YN = 'N' 
		                           AND NVL(B1.YS_STK_BED_L_GP,'*') = C1.P_YD_MTL_L_GP 
		                           AND A1.YS_STK_COL_GP LIKE 'KB0%'
		                           AND NVL(B1.YD_STK_BED_ACT_STAT,'*') = 'L'
		                         ORDER BY A1.YS_STK_COL_GP,A1.YS_STK_BED_NO DESC 
		                        )  
		                 WHERE MTL_STAT_UP_CNT = 0  -- 권상 예약이 안된 열  
		                   AND MTL_DAN = 1          -- BED 최상단만 READ 
		               ) A 
		             , TB_YS_STOCK  B  
		             , PARA_TBL1  C 
		         WHERE A.MAX_SSTL_NO = B.SSTL_NO 
		           AND A.YS_STK_COL_GP||A.YS_STK_BED_NO <> SUBSTR(P_YD_UP_STK_LOC,1,8)  --자신위치 제외 
		         
		        UNION ALL 
		        -- 적치대 공BED 
		        SELECT SEQ_NUM 
		            , YS_STK_COL_GP 
		            , YS_STK_BED_NO 
		            , YS_STK_LYR_NO 
		            , YS_STK_SEQ_NO 
		            , SSTL_NO 
		            , MTL_STAT_UP_CNT   -- 권상예약 수 
		         FROM
		       (
		        SELECT '2' SEQ_NUM 
		                 , A.YS_STK_COL_GP 
		                 , A.YS_STK_BED_NO 
		                 , '01' AS YS_STK_LYR_NO 
		                 , '1'  AS YS_STK_SEQ_NO 
		                 , ''   AS SSTL_NO 
		                 , 0    AS MTL_STAT_UP_CNT   -- 권상예약 수 
		                 , D.P_YD_MTL_L_GP AS YD_MTL_L_GP
		                 , DECODE(P_YD_MTL_L_GP,'S',(SELECT COUNT(SSTL_NO)  AS SUM_CNT 
		                                               FROM TB_YS_STKLYR 
		                                               WHERE DEL_YN = 'N' 
		                                               AND YS_STK_COL_GP = A.YS_STK_COL_GP 
		                                               AND YS_STK_BED_NO BETWEEN  A.YS_STK_BED_NO AND A.YS_STK_BED_NO + 3  --4개BED
		                                               AND YD_STK_LYR_ACT_STAT ='E'
		                                            ),0) AS BED_S4
		                 , DECODE(P_YD_MTL_L_GP,'S',(SELECT COUNT(*) AS SUM_CNT 
		                                              FROM TB_YS_STKBED 
		                                             WHERE DEL_YN = 'N' 
		                                               AND YS_STK_COL_GP = A.YS_STK_COL_GP 
		                                               AND YS_STK_BED_NO BETWEEN  A.YS_STK_BED_NO AND A.YS_STK_BED_NO + 3 --4개BED
		                                               AND YD_STK_BED_ACT_STAT ='L'
		                                               AND YD_STK_BED_WHIO_STAT ='E'
		                   ),0) AS BED_CNT_S4
		                 , DECODE(P_YD_MTL_L_GP,'M',(SELECT COUNT(SSTL_NO)  AS SUM_CNT 
		                                              FROM TB_YS_STKLYR 
		                                             WHERE DEL_YN = 'N' 
		                                               AND YS_STK_COL_GP = A.YS_STK_COL_GP 
		                                               AND YS_STK_BED_NO BETWEEN  A.YS_STK_BED_NO AND A.YS_STK_BED_NO + 4  --5개BED
		                                               AND YD_STK_LYR_ACT_STAT ='E'
		                                              
		                   ),0) AS BED_M5
		                 , DECODE(P_YD_MTL_L_GP,'M',(SELECT COUNT(*) AS SUM_CNT 
		                                              FROM TB_YS_STKBED 
		                                             WHERE DEL_YN = 'N' 
		                                               AND YS_STK_COL_GP = A.YS_STK_COL_GP 
		                                               AND YS_STK_BED_NO BETWEEN  A.YS_STK_BED_NO AND A.YS_STK_BED_NO + 4  --5개BED
		                                               AND YD_STK_BED_ACT_STAT ='L'
		                                               AND YD_STK_BED_WHIO_STAT ='E'
		                   ),0) AS BED_CNT_M5
		                 , DECODE(P_YD_MTL_L_GP,'L',(SELECT COUNT(SSTL_NO)  AS SUM_CNT 
		                                              FROM TB_YS_STKLYR 
		                                             WHERE DEL_YN = 'N' 
		                                               AND YS_STK_COL_GP = A.YS_STK_COL_GP 
		                                               AND YS_STK_BED_NO BETWEEN  A.YS_STK_BED_NO AND A.YS_STK_BED_NO + 5  --6개BED
		                                                AND YD_STK_LYR_ACT_STAT ='E'
		                   ),0) AS BED_L6
		                 , DECODE(P_YD_MTL_L_GP,'L',(SELECT COUNT(*)  AS SUM_CNT 
		                                              FROM TB_YS_STKBED 
		                                             WHERE DEL_YN = 'N' 
		                                               AND YS_STK_COL_GP = A.YS_STK_COL_GP 
		                                               AND YS_STK_BED_NO BETWEEN  A.YS_STK_BED_NO AND A.YS_STK_BED_NO + 5  --6개BED
		                                               AND YD_STK_BED_ACT_STAT ='L'
		                                               AND YD_STK_BED_WHIO_STAT ='E'
		                   ),0) AS BED_CNT_L6
		                 , DECODE(P_YD_MTL_L_GP,'X',(SELECT COUNT(SSTL_NO)  AS SUM_CNT 
		                                              FROM TB_YS_STKLYR 
		                                             WHERE DEL_YN = 'N' 
		                                               AND YS_STK_COL_GP = A.YS_STK_COL_GP 
		                                               AND YS_STK_BED_NO BETWEEN  A.YS_STK_BED_NO AND A.YS_STK_BED_NO + 7  --8개BED
		                                               AND YD_STK_LYR_ACT_STAT ='E'
		                   ),0) AS BED_X8
		                 , DECODE(P_YD_MTL_L_GP,'X',(SELECT COUNT(*)  AS SUM_CNT 
		                                              FROM TB_YS_STKBED 
		                                             WHERE DEL_YN = 'N' 
		                                               AND YS_STK_COL_GP = A.YS_STK_COL_GP 
		                                               AND YS_STK_BED_NO BETWEEN  A.YS_STK_BED_NO AND A.YS_STK_BED_NO + 7 -- 8개BED
		                                               AND YD_STK_BED_ACT_STAT ='L'
		                                               AND YD_STK_BED_WHIO_STAT ='E'
		                   ),0) AS BED_CNT_X8
		          FROM TB_YS_STKBED A 
		             , (SELECT COUNT(SSTL_NO)  AS SUM_CNT 
		                     , YS_STK_COL_GP 
		                     , YS_STK_BED_NO 
		                  FROM TB_YS_STKLYR 
		                 WHERE DEL_YN = 'N' 
		                   AND YS_STK_COL_GP LIKE 'KB0%'
		                 GROUP BY YS_STK_COL_GP,YS_STK_BED_NO
		               ) B 
		             , PARA_TBL1 D 
		         WHERE A.YS_STK_COL_GP = B.YS_STK_COL_GP 
		           AND A.YS_STK_BED_NO = B.YS_STK_BED_NO  
		           AND A.YD_STK_BED_ACT_STAT ='L'
		           AND A.YD_STK_BED_WHIO_STAT ='E'
		           AND B.SUM_CNT = 0 
		               AND (A.YS_STK_COL_GP, A.YS_STK_BED_NO ) -- 크레인스케쥴에 권하위치+BED 제외처리
		               NOT IN (
		                        SELECT SB.YS_STK_COL_GP,SB.YS_STK_BED_NO 
		                          FROM TB_YS_STKBED SB
		                             , (
		                                SELECT A.YS_DN_WO_LOC
		                                     , SUBSTR(A.YS_DN_WO_LOC,1,6) AS YS_STK_COL_GP
		                                     , SUBSTR(A.YS_DN_WO_LOC,7,2) AS BASE_BED
		                                     , (SELECT YD_MTL_L_GP 
		                                          FROM TB_YS_CRNWRKMTL A1
		                                             , TB_YS_STOCK B1
		                                          WHERE A1.YD_CRN_SCH_ID = A.YD_CRN_SCH_ID
		                                            AND A1.SSTL_NO = B1.SSTL_NO 
		                                            AND ROWNUM = 1 ) YD_MTL_L_GP
		                                  FROM TB_YS_CRNSCH A
		                                 WHERE A.DEL_YN = 'N'
		                                   AND A.YD_GP = 'K'
		                                   AND A.YD_BAY_GP ='B'
		                                   AND A.YS_DN_WO_LOC LIKE 'KB0%'
		                                   AND A.YS_DN_WO_LAYER = '01' 
		                                   AND A.YD_CRN_SCH_ID < :V_YD_CRN_SCH_ID
		                                ) CRN
		                        WHERE SB.YS_STK_COL_GP = CRN.YS_STK_COL_GP
		                          AND SB.YS_STK_BED_NO BETWEEN  CRN.BASE_BED AND (CASE WHEN YD_MTL_L_GP = 'S' THEN CRN.BASE_BED + 3
		                                                                               WHEN YD_MTL_L_GP = 'M' THEN CRN.BASE_BED + 4
		                                                                               WHEN YD_MTL_L_GP = 'L' THEN CRN.BASE_BED + 5
		                                                                               WHEN YD_MTL_L_GP = 'X' THEN CRN.BASE_BED + 7 END)
		                       )     
		            ORDER BY YS_STK_BED_NO,YS_STK_COL_GP
		            )
		            WHERE DECODE(YD_MTL_L_GP,'S',4,'M',5,'L',6,'X',8) = BED_CNT_S4+BED_CNT_M5+BED_CNT_L6+BED_CNT_X8
		              AND BED_S4 + BED_M5 + BED_L6 + BED_X8 = 0
		              AND ROWNUM  < 5     
		            
		            ) A 
		WHERE SUBSTR(YS_STK_COL_GP,3,1) IN ('0','1')  -- 일반야드 
		  AND ROWNUM < 10
		ORDER BY SEQ_NUM DESC,YS_STK_COL_GP, YS_STK_LYR_NO,YS_STK_BED_NO
		*/
		
		
		outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getDummyMoveRbB_PIDEV", logId, methodNm, "동일한 적치가능한 베드 조회");
		if (outRsResult.size() <= 0) {
			szLogMsg = LocalmethodNm+"동일한  적치가능한 베드 검색 실패 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			return YsConstant.RETN_CD_FAILURE;
		}
	    
	    // 적치 가능 여부 CEHCK	
		for(int i = 1; i <= outRsResult.size(); i++) {

			outRsResult.absolute(i);
			outRecResult  = outRsResult.getRecord();
			szYS_STK_COL_GP = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));		//권하지시위치 TEMP
			szYS_STK_BED_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));		//권하지시위치 TEMP
			
			//신진희 :: 각 01 출하가적배드 비활성화 - 오주원 주임요청
			
			/* 
			 * FROM위치가 KB010101,KB010201,KB010301,KB010401,KB010501, KB010601, KB010701, KB010801, KB010901, KB011001 일 경우
			 * To위치도  KB010101,KB010201,KB010301,KB010401,KB010501, KB010601, KB010701, KB010801, KB010901, KB011001 이어야 함
	    	 */
			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "*", "*");
			if("Y".equals(sApplyYnPI)){
				if( "KB010101".equals(szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO)||
		    		"KB010201".equals(szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO)|| 
		    		"KB010301".equals(szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO)||
		    		"KB010401".equals(szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO)||
		    		"KB010501".equals(szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO)||
	    			"KB010601".equals(szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO)||
	    			"KB010701".equals(szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO)||
	    			"KB010801".equals(szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO)||
	    			"KB010901".equals(szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO)||
	    			"KB011001".equals(szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO)){
		    		
		    		if( "KB010101".equals(szYS_STK_COL_GP+szYS_STK_BED_NO)||
		    			"KB010201".equals(szYS_STK_COL_GP+szYS_STK_BED_NO)||
		    			"KB010301".equals(szYS_STK_COL_GP+szYS_STK_BED_NO)||
		    			"KB010401".equals(szYS_STK_COL_GP+szYS_STK_BED_NO)||
		    			"KB010501".equals(szYS_STK_COL_GP+szYS_STK_BED_NO)||
		        		"KB010601".equals(szYS_STK_COL_GP+szYS_STK_BED_NO)||
		        		"KB010701".equals(szYS_STK_COL_GP+szYS_STK_BED_NO)||
		        		"KB010801".equals(szYS_STK_COL_GP+szYS_STK_BED_NO)||
		        		"KB010901".equals(szYS_STK_COL_GP+szYS_STK_BED_NO)||
		        		"KB011001".equals(szYS_STK_COL_GP+szYS_STK_BED_NO)){
		    			
		    			szLogMsg = LocalmethodNm+"작업가능 : From위치="+szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO+"/To위치="+szYS_STK_COL_GP+szYS_STK_BED_NO;
		    			commUtils.printLog(logId, szLogMsg, "SL");
		    			
		    		}else{
		    			
		    			szLogMsg = LocalmethodNm+"작업불가능 : From위치="+szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO+"/To위치="+szYS_STK_COL_GP+szYS_STK_BED_NO;
		    			commUtils.printLog(logId, szLogMsg, "SL");
		    			continue;
		    		}
		    	}
	    	
	    	/* 
			 * FROM위치가 KB011101,KB011106,KB011201,KB011206 일 경우
			 * To위치도   KB011101,KB011106,KB011201,KB011206 이어야 함
	    	 */
		    	if( "KB011101".equals(szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO)||
		    		"KB011106".equals(szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO)||
		    		"KB011201".equals(szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO)||
		    		"KB011206".equals(szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO)){
		    		
		    		if( "KB011101".equals(szYS_STK_COL_GP+szYS_STK_BED_NO)||
		    			"KB011106".equals(szYS_STK_COL_GP+szYS_STK_BED_NO)||
		    			"KB011201".equals(szYS_STK_COL_GP+szYS_STK_BED_NO)||
		    			"KB011206".equals(szYS_STK_COL_GP+szYS_STK_BED_NO)){
		    			
		    			szLogMsg = LocalmethodNm+"작업가능 : From위치="+szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO+"/To위치="+szYS_STK_COL_GP+szYS_STK_BED_NO;
		    			commUtils.printLog(logId, szLogMsg, "SL");
		    			
		    		}else{
		    			
		    			szLogMsg = LocalmethodNm+"작업불가능 : From위치="+szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO+"/To위치="+szYS_STK_COL_GP+szYS_STK_BED_NO;
		    			commUtils.printLog(logId, szLogMsg, "SL");
		    			continue;
		    		}
		    	}
			}
	    	
			// 적치 가능 여부 CEHCK	
			recResult = this.procLocAbleCheck(logId, methodNms, szYS_STK_COL_GP,szYS_STK_BED_NO);
			
			if(commUtils.trim(recResult.getFieldString("ABLE_YN"  )).equals("Y")){
				szYS_DN_WO_LOC 		= commUtils.trim(recResult.getFieldString("YS_DN_WO_LOC"  )); //CA0104		
				szYS_DN_WO_LAYER 	= commUtils.trim(recResult.getFieldString("YS_DN_WO_LAYER"  )); //02
				break;	
			} else {
				continue;
			}
		}
		JDTORecord RecSetLoc = JDTORecordFactory.getInstance().create();
		RecSetLoc.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
		RecSetLoc.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
		RecSetLoc.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
		RecSetLoc.setField("YS_UP_WO_LOC", 		szYS_UP_WO_LOC); 
		RecSetLoc.setField("YS_UP_WO_LAYER",	szYS_UP_WO_LAYER);	 
		RecSetLoc.setField("YS_DN_WO_LOC", 		szYS_DN_WO_LOC);
		RecSetLoc.setField("YS_DN_WO_LAYER", 	szYS_DN_WO_LAYER);
		RecSetLoc.setField("YD_RCPT_PLN_STR_LOC","");
		RecSetLoc.setField("YD_WBOOK_ID", 	    szYD_WBOOK_ID); 
			
		this.procUpdateLoc(logId,methodNms,recCrnwrkmtl  ,RecSetLoc );
		//----------------------------------------------------------------------------------------------------------------------
		// ERROR 발생시 ?
		//----------------------------------------------------------------------------------------------------------------------
		
		commUtils.printLog(logId, methodNm, "S-");
		return YsConstant.RETN_CD_SUCCESS;
	} 

	/**
	 * A동 봉강보조작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procDummyToLocRbTY(String logId, String methodNms, JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recWbook) throws JDTOException {
    	String methodNm = "TY 봉강보조작업TO위치결정[GdsYsSchSeEJB.procDummyToLocRbTY] < " + methodNms;
    	String LocalmethodNm = "TY 봉강보조작업TO위치결정[GdsYsSchSeEJB.procDummyToLocRbTY]";

		String szLogMsg					= null;
		JDTORecordSet	rsResult		= null;
		JDTORecordSet	rsStock			= null;
		JDTORecord		recStock		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		JDTORecord		recCrnwrkmtl	= null;
		String szYS_UP_WO_LOC			= null;
		String szYS_UP_WO_LAYER			= null;
		String szYS_DN_WO_LOC			= null;
		String szYS_DN_WO_LAYER			= null;
		String szYD_UP_STK_COL_GP		= null;
		String szYD_UP_STK_BED_NO		= null;
		JDTORecordSet outRsResult 		= null;
		JDTORecord    outRecResult 		= null;

		String szYS_STK_COL_GP  = "";
		String szYS_STK_BED_NO  = "";
		String szYS_STK_LYR_NO  = "";
		
		
		commUtils.printLog(logId, methodNm, "S+");

		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		recCrnwrkmtl = rsCrnwrkmtl.getRecord();
		
		String szYD_CRN_SCH_ID 	= commUtils.trim(recCrnwrkmtl.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
		String szSSTL_NO 		= commUtils.trim(recCrnwrkmtl.getFieldString("SSTL_NO"  ));			//크레인작업재료
		String szYD_EQP_ID  	= commUtils.trim(recCrnSch.getFieldString("YD_EQP_ID"  ));		//크레인설비ID
		String szYD_SCH_CD 		= commUtils.trim(recWbook.getFieldString("YD_SCH_CD"  ));			//크레인스케줄코드
		String szYD_WBOOK_ID 	= commUtils.trim(recWbook.getFieldString("YD_WBOOK_ID"  ));			//크레인스케줄코드
		int intMTL_CNT 			= Integer.parseInt(commUtils.nvl(recCrnwrkmtl.getFieldString("SH_CNT"),"0"));
		
		rsStock = commDao.select(recCrnwrkmtl, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStock", logId, methodNm, "저장품 조회");
		if (rsStock.size() <= 0) {
			szLogMsg = "["+ methodNm +"]  재료정보["+szSSTL_NO+"]를 저장품에서 조회 실패 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			return "0";
		}
		
		rsStock.first();
		recStock = rsStock.getRecord();
		
		String szCUST_CD   		= commUtils.trim(recStock.getFieldString("CUST_CD"  ));			//고객사
		String szHEAT_NO   		= commUtils.trim(recStock.getFieldString("HEAT_NO"  ));			//HEAT_NO
		String szDETAIL_ARR_CD 	= commUtils.trim(recStock.getFieldString("DETAIL_ARR_CD"  ));			//상세착지
		String szBUNDLE_T 		= commUtils.trim(recStock.getFieldString("BUNDLE_T"  ));	//BUNDLE 두께
		
		szLogMsg = LocalmethodNm + "크레인작업재료의  단 최대SEQ ["+szSSTL_NO+"]를 저장품에서 조회 완료 - 고객사["+szCUST_CD+"], 상세착지["+szDETAIL_ARR_CD+"], HEAT_NO["+szHEAT_NO+"]";
		commUtils.printLog(logId, szLogMsg, "SL");

		//----------------------------------------------------------------------------------------------------------------------
		//	권상중이거나 권하중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYS_UP_WO_LOC 		= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LOC"  ));		
		szYS_UP_WO_LAYER 	= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LAYER"  ));		

		if( szYS_UP_WO_LOC.equals("") ) {
			
			szLogMsg = LocalmethodNm + " 크레인작업재료의  재료정보["+szSSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			
			 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO 
			SELECT YS_STK_COL_GP            AS YS_STK_COL_GP
			      ,YS_STK_BED_NO            AS YS_STK_BED_NO
			      ,YS_STK_LYR_NO            AS YS_STK_LYR_NO
			      ,YS_STK_SEQ_NO            AS YS_STK_SEQ_NO
			      ,REGISTER                 AS REGISTER
			      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
			      ,MODIFIER                 AS MODIFIER
			      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
			      ,DEL_YN                   AS DEL_YN
			      ,SSTL_NO                   AS SSTL_NO
			      ,YD_STK_LYR_ACT_STAT      AS YD_STK_LYR_ACT_STAT
			      ,YD_STK_LYR_MTL_STAT      AS YD_STK_LYR_MTL_STAT
			      ,YD_STK_LYR_XAXIS         AS YD_STK_LYR_XAXIS
			      ,YD_STK_LYR_YAXIS         AS YD_STK_LYR_YAXIS
			      ,YD_STK_LYR_ZAXIS         AS YD_STK_LYR_ZAXIS
			  FROM TB_YS_STKLYR
			 WHERE SSTL_NO = :V_SSTL_NO
			   AND NVL(YD_STK_LYR_MTL_STAT, '*') = :V_YD_STK_LYR_MTL_STAT
			   AND DEL_YN='N'
			*/
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("SSTL_NO", szSSTL_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT", "D");
			
			rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO", logId, methodNm, "적재위치조회 조회");
			if (rsResult.size() <= 0) {

				recPara.setField("YD_STK_LYR_MTL_STAT", "U");
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO", logId, methodNm, "적재위치조회 조회");
				if (rsResult.size() <= 0) {
					return "0";
				}	
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YS_STK_COL_GP");
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YS_STK_BED_NO");
			szYS_UP_WO_LOC 			= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYS_UP_WO_LAYER 		= recTemp.getFieldString("YS_STK_LYR_NO");
			
		}else{
		
			szYD_UP_STK_COL_GP = szYS_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYS_UP_WO_LOC.substring(6);
			
		}
		szLogMsg = LocalmethodNm + " 조회된 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]";
		commUtils.printLog(logId, szLogMsg, "SL");
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	재료와 같은 고객사CODE + 상세착지 + HEAT 의 적치가능한 베드 해당 동의 모든 위치를 조회 
		//----------------------------------------------------------------------------------------------------------------------
		
		//권상지시위치에 따라 알맞은 적치가능한 베드 검색 방법을 적용
		recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("HEAT_NO", 			szHEAT_NO);											//크레인작업 최하단재료의 HEAT_NO
    	recTemp.setField("CUST_CD", 			szCUST_CD);											//크레인작업 최하단재료의 고객사
    	recTemp.setField("DETAIL_ARR_CD", 		szDETAIL_ARR_CD);									//크레인작업 최하단재료의 상세착지
    	recTemp.setField("YD_UP_STK_LOC", 		szYS_UP_WO_LOC+szYS_UP_WO_LAYER );	                //권상위치	
    	recTemp.setField("BUNDLE_T", 			szBUNDLE_T);										//번들두께
    	recTemp.setField("SH_CNT", 				""+intMTL_CNT);										//작업매수
    	commUtils.printParam(logId, recTemp);
    	
    	szLogMsg = LocalmethodNm + " TOSQL:["+szYD_CRN_SCH_ID+ "] 동일한 고객사["+szCUST_CD+"], 상세착지["+szDETAIL_ARR_CD+"], HEAT_NO["+szHEAT_NO+"]의 적치가능한 베드 조회 시작";
		commUtils.printLog(logId, szLogMsg, "SL");
			
		/* com.inisteel.cim.ys.common.dao.YsCommDAO.getDummyMoveRbTY
			WITH PARA_TBL1 AS (
			 SELECT :V_YD_UP_STK_LOC AS P_YD_UP_STK_LOC   -- 권상열
			--      , :V_HEAT_NO AS P_HEAT_NO
			--      , :V_CUST_CD AS P_CUST_CD
			--      , :V_DETAIL_ARR_CD AS P_DETAIL_ARR_CD
			      , (SELECT ITEM 
			               FROM USRYSA.TB_YS_RULE
			              WHERE REPR_CD_GP = 'K00003'
			                AND CD_GP = '0001') AS RULE_T 
			   FROM DUAL
			)
			
			,TEMP_SH_CNT_TABLE AS (
			SELECT :V_SH_CNT AS P_SH_CNT
			  FROM DUAL
			)
			
			SELECT *
			FROM
			(
			SELECT A.SEQ_NUM 
			     , A.YS_STK_COL_GP
			     , A.YS_STK_BED_NO
			     , A.YS_STK_LYR_NO
			     , A.YS_STK_SEQ_NO
			     , A.SSTL_NO
			  FROM
			(
			        SELECT '1' SEQ_NUM 
			             , A.YS_STK_COL_GP
			             , A.YS_STK_BED_NO
			             , A.YS_STK_LYR_NO 
			             , A.YS_STK_SEQ_NO 
			             , A.SSTL_NO
			          FROM
			             (
			               SELECT YS_STK_COL_GP
			                    , YS_STK_BED_NO
			                    , YS_STK_LYR_NO
			                    , '2' AS YS_STK_SEQ_NO
			                    , SSTL_NO
			                 FROM
			                      (
			                       SELECT TB.*
			                            , MAX(CASE WHEN SSTL_CNT=3 THEN YS_STK_LYR_NO ELSE '00' END) OVER(PARTITION BY YS_STK_COL_GP) AS MAX_FULL_LYR
			                         FROM (
			                                   SELECT SL.YS_STK_COL_GP
			                                        , SL.YS_STK_BED_NO
			                                        , SL.YS_STK_LYR_NO
			                                        , MAX(CASE WHEN SL.YS_STK_SEQ_NO = '1' AND SL.SSTL_NO IS NULL THEN 'Y' ELSE 'N' END) AS DAN_1
			                                        , MAX(CASE WHEN SL.YS_STK_SEQ_NO = '2' AND SL.SSTL_NO IS NULL THEN 'Y' ELSE 'N' END) AS DAN_2
			                                        , MAX(CASE WHEN SL.YS_STK_SEQ_NO = '3' AND SL.SSTL_NO IS NULL THEN 'Y' ELSE 'N' END) AS DAN_3
			                                        , MAX(SL.SSTL_NO)    AS SSTL_NO
			                                        , COUNT(SL.SSTL_NO)  AS SSTL_CNT
			                                    FROM TB_YS_STKLYR SL
			                                       , PARA_TBL1   CP
			                                   WHERE 1=1
			                                     AND SL.YS_STK_COL_GP     LIKE SUBSTR(CP.P_YD_UP_STK_LOC,1,4)||'%' --같은스판 선택
			                                     AND SL.YS_STK_COL_GP NOT LIKE SUBSTR(CP.P_YD_UP_STK_LOC,1,6)||'%' --다른열  선택
			        --                             AND SL.YS_STK_LYR_NO >= (CASE WHEN :V_BUNDLE_T < TO_NUMBER(CP.RULE_T)  THEN 1 ELSE 4  END )
			        --                             AND SL.YS_STK_LYR_NO <= (CASE WHEN :V_BUNDLE_T < TO_NUMBER(CP.RULE_T)  THEN 3 ELSE 99 END )
			                                     
			        --                             AND SL.YS_STK_LYR_NO >= (CASE WHEN :V_BUNDLE_T < TO_NUMBER(CP.RULE_T)  THEN 1 ELSE 2  END )
			        --                             AND SL.YS_STK_LYR_NO <= (CASE WHEN :V_BUNDLE_T < TO_NUMBER(CP.RULE_T)  THEN 1 ELSE 99 END )
			                                     
			                                     AND NVL(SL.YD_STK_LYR_ACT_STAT,'*') = 'E'
			                                   GROUP BY SL.YS_STK_COL_GP, SL.YS_STK_BED_NO, SL.YS_STK_LYR_NO
			                              ) TB     
			                       )
			                     , TEMP_SH_CNT_TABLE
			                 WHERE YS_STK_LYR_NO > MAX_FULL_LYR
			                   AND SSTL_CNT + P_SH_CNT <= 3 -- 권하실적발생시  한 LYR에 3개를 넘지 않도록
			                   AND (     (P_SH_CNT = 1 AND DAN_3 = 'Y')
			                          OR (P_SH_CNT = 2 AND DAN_2 = 'Y' AND DAN_3 = 'Y')
			                          OR (P_SH_CNT = 3 AND DAN_1 = 'Y' AND DAN_2 = 'Y' AND DAN_3 = 'Y')
			                       )
			               ) A
			             , TB_YS_STOCK  B 
			             , PARA_TBL1  C
			             , TB_YS_STKBED D
			             , TB_YS_STKCOL E
			         WHERE A.SSTL_NO = B.SSTL_NO
			           AND A.YS_STK_COL_GP || A.YS_STK_BED_NO || A.YS_STK_LYR_NO  <> C.P_YD_UP_STK_LOC  --자신위치 제외
			           AND A.YS_STK_COL_GP = D.YS_STK_COL_GP
			           AND A.YS_STK_BED_NO = D.YS_STK_BED_NO
			           AND NVL(D.YD_STK_BED_ACT_STAT,'*') = 'L'
			           AND D.YS_STK_COL_GP = E.YS_STK_COL_GP
			           AND NVL(E.YD_STK_COL_ACT_STAT,'*') = 'L'
			) A
			ORDER BY YS_STK_COL_GP, SEQ_NUM ASC, YS_STK_LYR_NO, YS_STK_BED_NO
			)
			WHERE ROWNUM = 1
		*/
		
		
		outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getDummyMoveRbTY", logId, methodNm, "DUMMY 적치가능한 베드 조회");
		if (outRsResult.size() <= 0) {
			szLogMsg =  LocalmethodNm+" 동일한 고객사["+szCUST_CD+"], 상세착지["+szDETAIL_ARR_CD+"], HEAT_NO["+szHEAT_NO+"]의 적치가능한 베드 조회 실패 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			return YsConstant.RETN_CD_FAILURE;
		}
	    

		outRsResult.first();
		outRecResult = outRsResult.getRecord();

		szYS_STK_COL_GP  	= commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));
		szYS_STK_BED_NO 	= commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));
		szYS_STK_LYR_NO  	= commUtils.trim(outRecResult.getFieldString("YS_STK_LYR_NO"  ));
	
		/*
		 * 권하위치 최종결정정보 셋팅.
		 */
		szYS_DN_WO_LOC		= szYS_STK_COL_GP + szYS_STK_BED_NO;
		szYS_DN_WO_LAYER 	= szYS_STK_LYR_NO;
	
		JDTORecord RecSetLoc = JDTORecordFactory.getInstance().create();
		RecSetLoc.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
		RecSetLoc.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
		RecSetLoc.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
		RecSetLoc.setField("YS_UP_WO_LOC", 		szYS_UP_WO_LOC); 
		RecSetLoc.setField("YS_UP_WO_LAYER",	szYS_UP_WO_LAYER);	 
		RecSetLoc.setField("YS_DN_WO_LOC", 		szYS_DN_WO_LOC);
		RecSetLoc.setField("YS_DN_WO_LAYER", 	szYS_DN_WO_LAYER);
		RecSetLoc.setField("YD_RCPT_PLN_STR_LOC","");
		RecSetLoc.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID); 
		
		this.procUpdateLoc(logId,methodNms,recCrnwrkmtl  ,RecSetLoc );
		//----------------------------------------------------------------------------------------------------------------------
		// ERROR 발생시 ?
		//----------------------------------------------------------------------------------------------------------------------
		
		commUtils.printLog(logId, methodNm, "S-");
		return YsConstant.RETN_CD_SUCCESS;
	}    
    
	/**
     * 오퍼레이션명 : 봉강 A동  TO 위치 결정
     * @param  inRecord, recGetCrnWrkMtl, rsResultCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int LocSrcRngDataSetRbA (String logId, String methodNms, JDTORecord inRecord)throws JDTOException{
    	String methodNm = "봉강 A동  TO위치 결정[GdsYsSchSeEJB.LocSrcRngDataSetRbA] < " + methodNms;
    	String LocalmethodNm = "봉강 A동  TO위치 결정[GdsYsSchSeEJB.LocSrcRngDataSetRbA] ";
    	JDTORecordSet rsCrnsch    	= JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsCrnwrkmtl 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsBed			= JDTORecordFactory.getInstance().createRecordSet("");
    	JDTORecordSet rsTemp 		= JDTORecordFactory.getInstance().createRecordSet("");
    	JDTORecord recWbook      	= null;
    	JDTORecord recCrnSch      	= null;
    	JDTORecord recInTemp 		= null;
    	JDTORecord recBed 			= null;
    	String szMsg        		= "";     	  
    	String szCrnSchId 			= "";
    	String szSchCd    			= "";
    	String szToLocDcsnMtd 		= "";
		String szWbookId  			= "";
		String szEqpId    			= "";
		String szTcarSndyn    		= "N";
		String szWhsToLocDcsn 		= "Y"; //A동 입고시 목표동이  A동인 경우 TO위치 결정 안함  (Y:셋팅, N:미셋팅)
		String szSchSkip            = ""; 	
		
    	int intRtnVal 				= 0 ;
    	 
        try{
        	commUtils.printLog(logId, methodNm, "S+");
        	//-------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------
			//파라미터 Null Check
        	szWbookId 	= commUtils.trim(inRecord.getFieldString("YD_WBOOK_ID" ));	
        	szEqpId 	= commUtils.trim(inRecord.getFieldString("YD_EQP_ID"   ));	
        			
			//-------------------------------------------------------------------------------------------------------------
			//작업예약을 조회한다. To위치 결정방법이  사용자 지정인지 알기위해서...
			//-------------------------------------------------------------------------------------------------------------
			rsTemp 		= JDTORecordFactory.getInstance().createRecordSet("");
			
			 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbook 

			SELECT YD_WBOOK_ID  AS YD_WBOOK_ID
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
			      ,YD_CAR_USE_GP
			      ,TRN_EQP_CD AS TRN_EQP_CD
			      ,CAR_NO AS CAR_NO
			      ,CARD_NO AS CARD_NO
			   FROM TB_YS_WRKBOOK
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			 */
			rsTemp = commDao.select(inRecord, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbook", logId, methodNm, "작업예약 조회"); 
	    	
	    	if (rsTemp == null || rsTemp.size() <= 0) {
				commUtils.printLog(logId, LocalmethodNm+"[스케쥴메인종료]", "SL");
			}			
			
			rsTemp.absolute(1);
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setRecord(rsTemp.getRecord());
			
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄 조회
			//-------------------------------------------------------------------------------------------------------------
			rsCrnsch = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_WBOOK_ID", szWbookId);
			recInPara.setField("YD_EQP_ID"	, szEqpId);
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnschByEqpIdandWBookId
			SELECT A.YD_EQP_ID               AS YD_EQP_ID                       
			      ,A.YD_EQP_NAME             AS YD_EQP_NAME                     
			      ,B.YD_CRN_SCH_ID           AS YD_CRN_SCH_ID                   
			      ,B.REGISTER                AS REGISTER                        
			      ,TO_CHAR(B.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT                        
			      ,B.MODIFIER                AS MODIFIER                        
			      ,TO_CHAR(B.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT                        
			      ,B.DEL_YN                  AS DEL_YN                          
			      ,B.YD_WBOOK_ID             AS YD_WBOOK_ID                     
                            :     
			      ,B.YS_UP_WR_LOC            AS YS_UP_WR_LOC                    
			      ,B.YS_UP_WR_LAYER          AS YS_UP_WR_LAYER                  
			      ,B.YS_UP_WR_SEQ_NO         AS YS_UP_WR_SEQ_NO
			      ,B.YD_UP_WRK_ACT_GP        AS YD_UP_WRK_ACT_GP                
			      ,B.YD_UP_WR_XAXIS          AS YD_UP_WR_XAXIS                  
			      ,B.YD_UP_WR_YAXIS          AS YD_UP_WR_YAXIS                  
			      ,B.YD_UP_WR_YAXIS1         AS YD_UP_WR_YAXIS1                 
			      ,B.YD_UP_WR_YAXIS2         AS YD_UP_WR_YAXIS2                 
			      ,B.YD_UP_WR_ZAXIS          AS YD_UP_WR_ZAXIS                  
			      ,B.YS_DN_WR_LOC            AS YS_DN_WR_LOC                    
			      ,B.YS_DN_WR_LAYER          AS YS_DN_WR_LAYER                  
			      ,B.YS_DN_WR_SEQ_NO         AS YS_DN_WR_SEQ_NO
			      ,B.YD_DN_WRK_ACT_GP        AS YD_DN_WRK_ACT_GP                
			      ,B.YD_DN_WR_XAXIS          AS YD_DN_WR_XAXIS                  
			      ,B.YD_DN_WR_YAXIS          AS YD_DN_WR_YAXIS                  
			      ,B.YD_DN_WR_YAXIS1         AS YD_DN_WR_YAXIS1                 
			      ,B.YD_DN_WR_YAXIS2         AS YD_DN_WR_YAXIS2                 
			      ,B.YD_DN_WR_ZAXIS          AS YD_DN_WR_ZAXIS                  
			  FROM TB_YS_EQP    A                                               
			      ,TB_YS_CRNSCH B                                               
			 WHERE B.YD_EQP_ID   = A.YD_EQP_ID                                  
			   AND B.YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND B.YD_EQP_ID   = :V_YD_EQP_ID                         
			   AND (B.DEL_YN IS NULL OR B.DEL_YN <> 'Y')                        
			 ORDER BY B.YD_CRN_SCH_ID   
			 */
			
			rsCrnsch = commDao.select(inRecord, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnschByEqpIdandWBookId", logId, methodNm, "크레인스케줄 조회"); 
	    	
		
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄의 권하지시위치 결정
			//-------------------------------------------------------------------------------------------------------------
		    for(int Loop_i = 1; Loop_i <= rsCrnsch.size(); Loop_i++) {

        		rsCrnsch.absolute(Loop_i);
        		recCrnSch  = rsCrnsch.getRecord();
        		
        		//크레인스케줄Data저장
        		szCrnSchId     = recCrnSch.getFieldString("YD_CRN_SCH_ID");
        		szSchCd        = recCrnSch.getFieldString("YD_SCH_CD");
        		szToLocDcsnMtd = recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD");
        		
        		szMsg = LocalmethodNm+"[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]에 대한 권하지시위치 결정시작 ";
        		commUtils.printLog(logId, szMsg, "SL");
        		
        		//크레인작업재료조회
				rsCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
        		JDTORecord recInData = JDTORecordFactory.getInstance().create();
        		
        		recInData.setField("YD_CRN_SCH_ID", szCrnSchId);
        		
        		/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnwrkmtlBySchId 
        		SELECT A.SSTL_NO             AS SSTL_NO          
        		      ,A.YS_STK_LYR_NO       AS YS_STK_LYR_NO   
        		      ,A.YD_CRN_SCH_ID       AS YD_CRN_SCH_ID   
        		      ,A.REGISTER            AS REGISTER             
        		      ,A.REG_DDTT            AS REG_DDTT             
        		      ,A.MOD_DDTT            AS MOD_DDTT             
        		      ,A.MODIFIER            AS MODIFIER
        		      ,A.DEL_YN              AS DEL_YN                 
        		      ,A.YD_AID_WRK_YN       AS YD_AID_WRK_YN   
        		      ,A.HCR_GP              AS HCR_GP                 
        		      ,A.STL_PROG_CD         AS STL_PROG_CD       
        		      ,A.YS_ROUTE_GP         AS YS_ROUTE_GP
        		      ,B.YD_MTL_W            AS YD_MTL_W        
        		      ,B.YD_MTL_WT           AS YD_MTL_WT       
        		      ,B.YD_MTL_T            AS YD_MTL_T     
        		      ,B.YD_MTL_L            AS YD_MTL_L   
        		      ,B.YS_MTL_ITEM         AS YS_MTL_ITEM     
        		      ,B.YD_STK_LOT_TP       AS YD_STK_LOT_TP   
        		      ,B.YD_STK_LOT_CD       AS YD_STK_LOT_CD   
        		      ,B.REFUR_CHG_PLN_SERNO AS REFUR_CHG_PLN_SERNO
        		      ,B.BLOOM_CL_MTD        AS BLOOM_CL_MTD
        		      ,SUM(B.YD_MTL_WT) OVER (ORDER BY A.YS_STK_LYR_NO DESC) AS SUM_MTL_WT      
        		      ,SUM(B.YD_MTL_T)  OVER (ORDER BY A.YS_STK_LYR_NO DESC) AS SUM_MTL_T   
        		      ,MAX(B.YD_MTL_W)  OVER (ORDER BY A.YS_STK_LYR_NO DESC) AS MAX_MTL_W 
        		      ,MAX(B.YD_MTL_L)  OVER (ORDER BY A.YS_STK_LYR_NO DESC) AS MAX_MTL_L 
        		      ,COUNT(A.SSTL_NO) OVER (ORDER BY A.YS_STK_LYR_NO DESC) AS SH_CNT    
        		      ,NVL(MIN(B.YD_CHG_NO) OVER (ORDER BY A.YS_STK_LYR_NO DESC),0) AS YD_CHG_NO   
        		      ,(SELECT YS_UP_WO_LOC FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YS_UP_WO_LOC 
        		      ,(SELECT YS_UP_WO_LAYER FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YS_UP_WO_LAYER 
        		      ,(SELECT YD_WBOOK_ID FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YD_WBOOK_ID 
        		      ,(SELECT YD_EQP_ID FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YD_EQP_ID       
        		      ,A.YD_TO_LOC_DCSN_MTD AS YD_TO_LOC_DCSN_MTD       
        		  FROM TB_YS_CRNWRKMTL A                                                        
        		      ,TB_YS_STOCK     B                                                        
        		 WHERE A.SSTL_NO = B.SSTL_NO                                                      
        		   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
        		   AND A.DEL_YN = 'N'                                    
        		   AND B.DEL_YN = 'N'                                    
        		 ORDER BY A.YS_STK_LYR_NO
        		*/
        		
        		rsCrnwrkmtl = commDao.select(recInData, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnwrkmtlBySchId", logId, methodNm, "크레인스케줄재료 조회"); 
        		
        		if(rsCrnwrkmtl.size() <= 0) {
    				szMsg = LocalmethodNm + " : 위치검색범위 조회 Data Setting 실패!!";
    				commUtils.printLog(logId, szMsg, "SL");
        		}
        		
    			recWbook = JDTORecordFactory.getInstance().create();
    			recWbook.setRecord(recInTemp);
       		
            	if(szToLocDcsnMtd.equals("W")) {
            		/**********************************************************
    				* 봉강보조작업인 경우 TO위치 결정 ( 창고내 이적 ...)
    				**********************************************************/            		
            		szMsg = LocalmethodNm + "["+ Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]" + " 스케줄코드[" + szSchCd + "]" 
            							  + " isTY?[" + szSchCd.substring(2,4) + "]" + "은 보조작업 스케줄의  To위치 결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
        			
        			// TY임시베드와 메인창고의 더미작업 로직 분개
        			if("TY".equals(szSchCd.substring(2,4))){ 
        				szMsg = LocalmethodNm + "TY베드 To위치 결정 시작";
        				commUtils.printLog(logId, szMsg, "SL");
            			this.procDummyToLocRbTY(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);	
        			} else{
        				this.procDummyToLocRbA(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
        			}
        				
            	} else {
  
            		szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]" + "스케줄코드[" + szSchCd + "]" + " : 주작업 TO위치결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
        			
        			if( szSchCd.substring(2,4).equals("TR") && (szSchCd.substring(6,7).equals("U")  // 출하
            				||szSchCd.substring(6,7).equals("S")
            				||szSchCd.substring(6,7).equals("B")) ) {
                		/**********************************************************************
        				* 차량출고 : 차량이 정지한 적치열 조회 ==> TO위치가 됨  ==> TO 위치 가이드에 등록 처리
        				***********************************************************************/            		
            			String szRtnMsg = "";
            			String szYS_STK_COL_GP	= "";
            			String szYS_STK_BED_NO	= "";
            			String szYS_STK_LYR_NO	= "";
            			
            			
            			String szYD_CAR_USE_GP	= commUtils.trim(recWbook.getFieldString("YD_CAR_USE_GP"));	//차량사용구분
	    				String szTRN_EQP_CD		= commUtils.trim(recWbook.getFieldString("TRN_EQP_CD"));	//운송장비코드
	    				String szCAR_NO			= commUtils.trim(recWbook.getFieldString("CAR_NO"));		//차량번호
	    				
	    				szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량상차작업예약["+szWbookId+"]의 차량정보[차량사용구분:"+szYD_CAR_USE_GP+", 운송장비코드:"+szTRN_EQP_CD+", 차량번호:"+szCAR_NO+"]에 대한  조회 시작";
	    				commUtils.printLog(logId, szMsg, "SL");
	    					
	    				rsBed	= JDTORecordFactory.getInstance().createRecordSet("");;
	    				recInPara = JDTORecordFactory.getInstance().create();
	    				
	    				if( szYD_CAR_USE_GP.equals("L") ) {				//구내운송
	    					
	    					recInPara.setField("TRN_EQP_CD", 		szTRN_EQP_CD);
		    				recInPara.setField("YD_CAR_USE_GP", 	szYD_CAR_USE_GP);
		    				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedByCarUseGpandTrnEqpCdBl
		    				SELECT YS_STK_COL_GP
		    				     , YS_STK_BED_NO 
		    				     , YS_STK_LYR_NO 
		    				     , YD_MTL_SH 
		    				  FROM
		    				(
		    				SELECT SB.YS_STK_COL_GP
		    				     , SB.YS_STK_BED_NO 
		    				     , SL.YS_STK_LYR_NO 
		    				      ,COUNT(SL.SSTL_NO) AS YD_MTL_SH
		    				  FROM TB_YS_STKBED SB
		    				      ,TB_YS_STKLYR SL
		    				      ,(SELECT YS_STK_COL_GP                         
		    				          FROM TB_YS_STKCOL                          
		    				         WHERE YD_CAR_USE_GP =:V_YD_CAR_USE_GP
		    				           AND TRN_EQP_CD = :V_TRN_EQP_CD
		    				           AND YD_STK_COL_ACT_STAT = 'L'             
		    				           AND DEL_YN='N' ) BL
		    				 WHERE SB.YS_STK_COL_GP = SL.YS_STK_COL_GP
		    				   AND SB.YS_STK_BED_NO = SL.YS_STK_BED_NO
		    				   AND SB.YS_STK_COL_GP = BL.YS_STK_COL_GP
		    				   AND SB.DEL_YN        = 'N'
		    				   AND SB.YD_STK_BED_ACT_STAT = 'L' --적치가능
		    				 GROUP BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
		    				 ORDER BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
		    				 )
		    				WHERE YD_MTL_SH = 0
		    				   AND ROWNUM = 1
		    				*/   
		    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedByCarUseGpandTrnEqpCdBl", logId, methodNm, "구내운송차량 차량BED 조회"); 
		            		
		            		if(rsBed.size() <= 0) {
		        				szMsg = LocalmethodNm + " : 구내운송차량 READ 실패!";
		        				commUtils.printLog(logId, szMsg, "SL");
		            		} else {
		            			szRtnMsg = YsConstant.RETN_CD_SUCCESS;
		            		}	
	    				} else if( szYD_CAR_USE_GP.equals("G") ) {		//출하차량
	    					
	    					recInPara.setField("YD_CAR_USE_GP", 	szYD_CAR_USE_GP);
		    				recInPara.setField("CAR_NO", 			szCAR_NO);
//		    				recInPara.setField("CARD_NO", 			szCARD_NO);
		    				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedByCarUseGpandCarNoBl
		    				SELECT YS_STK_COL_GP
		    				     , YS_STK_BED_NO 
		    				     , YS_STK_LYR_NO 
		    				     , YD_MTL_SH 
		    				  FROM
		    				(
		    				SELECT SB.YS_STK_COL_GP
		    				     , SB.YS_STK_BED_NO 
		    				     , SL.YS_STK_LYR_NO 
		    				      ,COUNT(SL.SSTL_NO) AS YD_MTL_SH
		    				  FROM TB_YS_STKBED SB
		    				      ,TB_YS_STKLYR SL
		    				      ,(SELECT YS_STK_COL_GP                         
		    				          FROM TB_YS_STKCOL                          
		    				         WHERE YD_CAR_USE_GP =:V_YD_CAR_USE_GP
		    				           AND CAR_NO = :V_CAR_NO
		    				           AND YD_STK_COL_ACT_STAT = 'L'             
		    				           AND DEL_YN='N' ) BL
		    				 WHERE SB.YS_STK_COL_GP = SL.YS_STK_COL_GP
		    				   AND SB.YS_STK_BED_NO = SL.YS_STK_BED_NO
		    				   AND SB.YS_STK_COL_GP = BL.YS_STK_COL_GP
		    				   AND SB.DEL_YN        = 'N'
		    				   AND SB.YD_STK_BED_ACT_STAT = 'L' --적치가능
		    				 GROUP BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
		    				 ORDER BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
		    				 )
		    				WHERE YD_MTL_SH = 0
		    				  AND ROWNUM = 1
		    				   */
		    				
		    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedByCarUseGpandCarNoBl", logId, methodNm, "출하 차량BED 조회"); 
		            		
		            		if(rsBed.size() <= 0) {
		        				szMsg = LocalmethodNm + " : 출하차량 READ 실패!";
		        				commUtils.printLog(logId, szMsg, "SL");
		            		} else {
		            			szRtnMsg = YsConstant.RETN_CD_SUCCESS;
		            		}
		    				
	    				}
	    				
/* 12.07	    				
//	    				if( szRtnMsg.equals(YsConstant.RETN_CD_SUCCESS) ) {
//	    					rsBed.first();
//	    					recBed = rsBed.getRecord();
//	    					szYS_STK_COL_GP	= commUtils.trim(recBed.getFieldString("YS_STK_COL_GP"));//차량정지위치 적치열
//	    					szYS_STK_BED_NO	= commUtils.trim(recBed.getFieldString("YS_STK_BED_NO"));//차량정지위치 적치베드
//	    					szYS_STK_LYR_NO	= commUtils.trim(recBed.getFieldString("YS_STK_LYR_NO"));//차량정지위치 적치단
//	    					
//	    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_STK_COL_GP + szYS_STK_BED_NO + szYS_STK_LYR_NO);
//	    					szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량출고 [TO위치 가이드 결정  YD_TO_LOC_GUIDE : "+szYS_STK_COL_GP + szYS_STK_BED_NO + szYS_STK_LYR_NO+"]";
//	        				commUtils.printLog(logId, szMsg, "SL");
//	    				}else{
//	    					recWbook.setField("YD_TO_LOC_GUIDE", "");
//
//	    					szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량출고 [TO위치 가이드 미결정 ]";
//	        				commUtils.printLog(logId, szMsg, "SL");
//	    				}
	    				
	    				//-----------------------------------------------------------------------------------------------
//        				this.procMainWrkLocRbA(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
/*/
	    				if( szRtnMsg.equals(YsConstant.RETN_CD_SUCCESS) ) {
	    					rsBed.first();
	    					recBed = rsBed.getRecord();
	    					szYS_STK_COL_GP	= commUtils.trim(recBed.getFieldString("YS_STK_COL_GP"));//차량정지위치 적치열
	    					szYS_STK_BED_NO	= commUtils.trim(recBed.getFieldString("YS_STK_BED_NO"));//차량정지위치 적치베드
	    					szYS_STK_LYR_NO	= commUtils.trim(recBed.getFieldString("YS_STK_LYR_NO"));//차량정지위치 적치단
	    					
	    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_STK_COL_GP + szYS_STK_BED_NO + szYS_STK_LYR_NO);
	    					szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량출고 [TO위치 가이드 결정  YD_TO_LOC_GUIDE : "+szYS_STK_COL_GP + szYS_STK_BED_NO + szYS_STK_LYR_NO+"]";
	        				commUtils.printLog(logId, szMsg, "SL");
	        				this.procMainWrkLocRbA(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
	    				}	
	    				//-----------------------------------------------------------------------------------------------
        				
        				
           			} else if( (szSchCd.substring(2,4).equals("PC") || szSchCd.substring(2,4).equals("TY")) && szSchCd.substring(6,7).equals("L") ) {
                		/*****************************************************************************
        				* 입고 작업
        				*  - 야드 목적동이 틀린 경우
        				*    . 대차 위치 확인하여 대차가 작업동에 있으면 상차 
        				*      작업동에 없으면 대차 상태를 확인 하여 작업이 없는 경우  출발지시 처리하고 작업지시 편성처리 함  
        				******************************************************************************/            		
            			String szYS_STK_COL_GP	= "";
            			String szYS_STK_BED_NO	= "";
            			String szYS_STK_LYR_NO	= "";
            			
            			szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 입고 주작업 To위치 결정 시작";
            			commUtils.printLog(logId, szMsg, "SL");

            			String szYD_AIM_BAY_GP	= commUtils.trim(recWbook.getFieldString("YD_AIM_BAY_GP"));	//야드 목적동
//입고시진도추가
            			String szYD_TO_LOC_GUIDE= commUtils.trim(recWbook.getFieldString("YD_TO_LOC_GUIDE"));//입고대기가 아닌 경우 사용함
            			
            			szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId
            			                      + "] YD_TO_LOC_GUIDE : " + szYD_TO_LOC_GUIDE
            			                      + ", YD_AIM_BAY_GP : " + szYD_AIM_BAY_GP
            			                      + "";
            			commUtils.printLog(logId, szMsg, "SL");
            			
            			//가이드 없는 경우 ERROR 방지
	    				if(szYD_TO_LOC_GUIDE.length() < 4) {
	    					szYD_TO_LOC_GUIDE = "XXXX";
	    				}
            			rsBed		= JDTORecordFactory.getInstance().createRecordSet("");;
            			recBed  	= JDTORecordFactory.getInstance().create();
	    				recInPara  	= JDTORecordFactory.getInstance().create();
	    				//입고시입고대기가 아닌 경우 사용함
	    				
	    				if (!szYD_AIM_BAY_GP.equals(szSchCd.substring(1,2))) { //(스케줄코드:A, 목적동:B) OR (스케줄코드:B, 목적동:A)
	   				
	            			szMsg = LocalmethodNm + "타동으로 가야할 경우";
	            			commUtils.printLog(logId, szMsg, "SL");	    					
	    					
	    					// 타 동으로 가야 할 경우 
		    				// 대차 위치 확인하여 대차가 현재동에 있으면 상차 
		    				//                     현재동에 없으면 대차 상태를 확인 하여  출발지시 처리 함  
	    					
	    					JDTORecordSet jsTc	= JDTORecordFactory.getInstance().createRecordSet("");;
            				JDTORecord   recTc  = JDTORecordFactory.getInstance().create();
            				
            				
            				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getTcBayWrk
            				-- 대차 목적동에 대차작업 여부 
            				SELECT A.YD_EQP_ID
            				     , A.YD_CURR_BAY_GP
            				     , A.YD_HOME_BAY_GP
            				     , A.YD_EQP_STAT
            				     , (SELECT COUNT(*) 
            				          FROM TB_YS_STKLYR 
            				         WHERE YS_STK_COL_GP = 'K'||:V_YD_AIM_BAY_GP ||'TC01' 
            				           AND DEL_YN = 'N'
            				           AND SSTL_NO IS NOT NULL
            				       ) AS WK_CNT
            				  FROM TB_YS_EQP A
            				 WHERE A.YD_EQP_ID = :V_YD_EQP_ID
	    					*/ 
            				recInPara.setField("YD_EQP_ID", 	"KXTC01");
            				recInPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
            
	    					//대차하차스케쥴 조회
	    					jsTc = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getTcBayWrk", logId, methodNm, "대차작업 여부");
	    					
	    					if (jsTc.size() > 0) {
	    						
	    						recTc = jsTc.getRecord(0);
	    						String ydTcarCurrBayGp  = commUtils.trim(recTc.getFieldString("YD_CURR_BAY_GP" )); //야드대차스케쥴ID
	    						String tcarUdCmplYn 	= commUtils.trim(recTc.getFieldString("WK_CNT")); //대차하차완료여부
	    						String tcarYdEqpId 		= commUtils.trim(recTc.getFieldString("YD_EQP_ID"));
	    						String ydEqpStat 		= commUtils.trim(recTc.getFieldString("YD_EQP_STAT"));
	    						
	    						recInPara  = JDTORecordFactory.getInstance().create();
	    						if(ydEqpStat.equals("B")) {
	    							if(szSchCd.substring(2,4).equals("PC")){
		    							recWbook.setField("YD_TO_LOC_GUIDE", szSchCd.substring(0,2)+ "TY");
		    							szMsg = LocalmethodNm + " : 대차 고장시 임시 BED 로"+ szSchCd.substring(0,2)+ "TY";
		    							commUtils.printLog(logId, szMsg, "SL");
	    							} else {
	    								szSchSkip = "Y";
	    							}
	    						} else if (szSchCd.substring(1,2).equals(ydTcarCurrBayGp)) {
	    							//대차가위치가  같은동이면
	    							szMsg = LocalmethodNm + " : 대차 위치가  작업동과 같은동";
    		        				commUtils.printLog(logId, szMsg, "SL");
    		        				
	    							recInPara.setField("YS_STK_COL_GP", 	"K"+ydTcarCurrBayGp+"TC01");
	    	    					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTc
	    	    					SELECT YS_STK_COL_GP
	    	    					     , YS_STK_BED_NO 
	    	    					     , YS_STK_LYR_NO  
	    	    					     , YD_MTL_SH 
	    	    					  FROM
	    	    					(
	    	    					SELECT SB.YS_STK_COL_GP
	    	    					     , SB.YS_STK_BED_NO 
	    	    					     , SL.YS_STK_LYR_NO 
	    	    					      ,COUNT(SL.SSTL_NO)                  AS YD_MTL_SH
	    	    					  FROM TB_YS_STKBED SB
	    	    					      ,TB_YS_STKLYR SL
	    	    					 WHERE SB.YS_STK_COL_GP= :V_YS_STK_COL_GP
	    	    					   AND SB.YS_STK_COL_GP = SL.YS_STK_COL_GP
	    	    					   AND SB.YS_STK_BED_NO = SL.YS_STK_BED_NO
	    	    					   AND SB.DEL_YN        = 'N'
	    	    					   AND SB.YD_STK_BED_ACT_STAT = 'L' --적치가능
	    	    					 GROUP BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
	    	    					 ORDER BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
	    	    					 )
	    	    					 WHERE YD_MTL_SH = 0
	    	    					   AND ROWNUM = 1
	    	    					 
	    		    				   */
	    		    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTc", logId, methodNm, "대차BED 조회"); 
	    		            		
	    		            		if(rsBed.size() <= 0) {
	    		        				szMsg = LocalmethodNm + " : 대차 BED READ 실패!";
	    		        				commUtils.printLog(logId, szMsg, "SL");
	    	
	    		        				recWbook.setField("YD_TO_LOC_GUIDE", szSchCd.substring(0,2)+ "TY");
	    		        				
	    		            		} else {
	    		    					rsBed.first();
	    		    					recBed = rsBed.getRecord();
	    		    					szYS_STK_COL_GP	= commUtils.trim(recBed.getFieldString("YS_STK_COL_GP"));//대차정지위치 적치열
	    		    					szYS_STK_BED_NO	= commUtils.trim(recBed.getFieldString("YS_STK_BED_NO"));//대차정지위치 적치베드
	    		    					szYS_STK_LYR_NO	= commUtils.trim(recBed.getFieldString("YS_STK_LYR_NO"));//대차정지위치 적치단
	    	    					
	    		    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_STK_COL_GP + szYS_STK_BED_NO+szYS_STK_LYR_NO);
	    		            		}	    							
	    							
	    						} else {
	    							
	    							szMsg = LocalmethodNm + " : 대차위치가  작업동과 틀린 경우 + tcarUdCmplYn :"+ tcarUdCmplYn;
    		        				commUtils.printLog(logId, szMsg, "SL");
    		        				
	    							if (tcarUdCmplYn.equals("0")) {
//		    							JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//		    							jrYdMsg.setResultCode(logId);	//Log ID
//		    							jrYdMsg.setResultMsg(methodNm);	//Log Method Name		    						
//		    							jrYdMsg.setField("YD_EQP_ID"     , tcarYdEqpId); //야드설비ID(대차)
//		    							jrYdMsg.setField("YD_TCAR_SCH_ID", ""); //야드대차스케쥴ID
//		    							jrYdMsg.setField("MODIFIER"    	, "YSSCH"   ); //수정자
	    								
	    		        				recInPara.setField("YS_STK_COL_GP", 	"K"+szSchCd.substring(1,2)+"TC01");
		    		    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTcXbay", logId, methodNm, "대차BED(다른동) 조회"); 
		    		    				szTcarSndyn = "Y";
		    		    				
		    		            		if(rsBed.size() <= 0) {
		    		        				szMsg = LocalmethodNm + " : 대차 BED READ 실패!";
		    		        				commUtils.printLog(logId, szMsg, "SL");		    	
		    		        				recWbook.setField("YD_TO_LOC_GUIDE", szSchCd.substring(0,2)+ "TY");
		    		        				
		    		            		} else {
		    		    					rsBed.first();
		    		    					recBed = rsBed.getRecord();
		    		    					szYS_STK_COL_GP	= commUtils.trim(recBed.getFieldString("YS_STK_COL_GP"));//대차정지위치 적치열
		    		    					szYS_STK_BED_NO	= commUtils.trim(recBed.getFieldString("YS_STK_BED_NO"));//대차정지위치 적치베드
		    		    					szYS_STK_LYR_NO	= commUtils.trim(recBed.getFieldString("YS_STK_LYR_NO"));//대차정지위치 적치단
		    	    					
		    		    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_STK_COL_GP + szYS_STK_BED_NO+szYS_STK_LYR_NO);		    		    					

			    							szMsg =LocalmethodNm + " : 대차가위치가  틀린동 + tcarUdCmplYn :K" + szYS_STK_COL_GP + szYS_STK_BED_NO+szYS_STK_LYR_NO;
		    		        				commUtils.printLog(logId, szMsg, "SL");
		    		            		}
	    		        				
	    							} else {
	    								if(szSchCd.substring(2,4).equals("PC")){
			    							recWbook.setField("YD_TO_LOC_GUIDE", szSchCd.substring(0,2)+ "TY");
			    							szMsg = LocalmethodNm + " : 대차 고장시 임시 BED 로"+ szSchCd.substring(0,2)+ "TY";
			    							commUtils.printLog(logId, szMsg, "SL");
		    							} else {
		    								szSchSkip = "Y";
		    							}
	    							}
	    						}
	    					} else {
	    						szSchSkip = "Y";
	    					}
	    				
	    				
	    				} else if (szSchCd.substring(2,4).equals("PC") && szYD_TO_LOC_GUIDE.substring(2,4).equals("TY")) {
	    					
	    					// 비오비오비오 TY
	    					szWhsToLocDcsn = "Y";
	    					
    						szMsg = LocalmethodNm + "  : 작업동 TY로 입고처리 함";
	        				commUtils.printLog(logId, szMsg, "SL");
	    					
							// CARRY-OUT 시 이미 SETTING 함	                          					
							// recWbook.setField("YD_TO_LOC_GUIDE", szSchCd.substring(0,2)+ "TY");
	    					
	    				} else {
	    					// 비오비오비오 TC
	    					szWhsToLocDcsn = "N";
    						recWbook.setField("YD_TO_LOC_GUIDE", "");

    						szMsg = LocalmethodNm + "  : 작업동으로 입고처리 함  ";
	        				commUtils.printLog(logId, szMsg, "SL");
    					}

	    				if(szSchSkip.equals("")){
		    				if(szWhsToLocDcsn.equals("Y")){
		    					commUtils.printLog(logId, szMsg, "SL");
		    					this.procMainWrkLocRbA(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook); // PC에서 KA01,KATY입고 시 타는 로직
		    				} else {
		        				szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 입고  주작업TO위치 결정 안함 ->'KAXXXXXX' 로 결정 ";
		        				commUtils.printLog(logId, szMsg, "SL");
		    				}
	    				}	
           			} else if( szSchCd.substring(2,4).equals("TC") && szSchCd.substring(6,7).equals("L") ) {
                		/*****************************************************************************
        				* 대차입고 작업
        				******************************************************************************/            		

           				szWhsToLocDcsn = "N";
        				
           				szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 대차입고  주작업TO위치 결정 안함 ->'KAXXXXXX' 로 결정 ";
        				commUtils.printLog(logId, szMsg, "SL");
            			
          			} else if( szSchCd.substring(2,4).equals("TC") && szSchCd.substring(6,7).equals("U") ) {
                		/*****************************************************************************
        				* 대차반납 작업
        				******************************************************************************/            		
            			String szYS_STK_COL_GP	= "";
            			String szYS_STK_BED_NO	= "";
            			String szYS_STK_LYR_NO	= "";
            			
            			szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 대차반납 주작업 To위치 결정 시작";
            			commUtils.printLog(logId, szMsg, "SL");

            			String szYD_AIM_BAY_GP	= commUtils.trim(recWbook.getFieldString("YD_AIM_BAY_GP"));	//야드 목적동
	    				
            			rsBed	= JDTORecordFactory.getInstance().createRecordSet("");;
            			recBed  = JDTORecordFactory.getInstance().create();
	    				recInPara  = JDTORecordFactory.getInstance().create();
    					// 타 동으로 가야 할 경우 
	    				// 대차 위치 확인하여 대차가 현재동에 있으면 상차 
	    				//                     현재동에 없으면 대차 상태를 확인 하여  출발지시 처리 함  
    					
    					JDTORecordSet jsTc	= JDTORecordFactory.getInstance().createRecordSet("");;
        				JDTORecord   recTc  = JDTORecordFactory.getInstance().create();
        				
        				
        				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getTcBayWrk 
        				-- 재료 목표동에 에 대차작업 여부 
        				SELECT A.YD_EQP_ID
        				     , A.YD_CURR_BAY_GP
        				     , A.YD_HOME_BAY_GP 
        				     , (SELECT COUNT(*) 
        				          FROM TB_YS_STKLYR 
        				         WHERE YS_STK_COL_GP = 'K'||:V_YD_AIM_BAY_GP ||'TC01' 
        				           AND DEL_YN = 'N'
        				           AND SSTL_NO IS NOT NULL
        				       ) AS WK_CNT
        				  FROM TB_YS_EQP A
        				 WHERE A.YD_EQP_ID = :V_YD_EQP_ID
        				   AND A.YD_EQP_STAT <> 'B'
    					*/ 
        				recInPara.setField("YD_EQP_ID"		, "KXTC01");
        				recInPara.setField("YD_AIM_BAY_GP"	, szYD_AIM_BAY_GP);
        
    					//대차하차스케쥴 조회
    					jsTc = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getTcBayWrk", logId, methodNm, "대차작업 여부");
    					
    					if (jsTc.size() > 0) {
    						
    						recTc = jsTc.getRecord(0);
    						String ydTcarCurrBayGp  = commUtils.trim(recTc.getFieldString("YD_CURR_BAY_GP" )); //야드대차스케쥴ID
    						String tcarUdCmplYn 	= commUtils.trim(recTc.getFieldString("WK_CNT")); //대차하차완료여부
    						String tcarYdEqpId 		= commUtils.trim(recTc.getFieldString("YD_EQP_ID"));
    						String ydEqpStat 		= commUtils.trim(recTc.getFieldString("YD_EQP_STAT"));
    						
    						recInPara  = JDTORecordFactory.getInstance().create();
    						
    						if(ydEqpStat.equals("B")) {
    						
    							szSchSkip = "Y";
    						
    						} else if (szSchCd.substring(1,2).equals(ydTcarCurrBayGp)) {
    							//대차가위치가  같은동이면
    							szMsg = LocalmethodNm + " : 대차 위치가  작업동과 같은동";
		        				commUtils.printLog(logId, szMsg, "SL");
		        				
    							recInPara.setField("YS_STK_COL_GP", 	"K"+ydTcarCurrBayGp+"TC01");
    	    					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTc
    	    					SELECT YS_STK_COL_GP
    	    					     , YS_STK_BED_NO 
    	    					     , YS_STK_LYR_NO  
    	    					     , YD_MTL_SH 
    	    					  FROM
    	    					(
    	    					SELECT SB.YS_STK_COL_GP
    	    					     , SB.YS_STK_BED_NO 
    	    					     , SL.YS_STK_LYR_NO 
    	    					      ,COUNT(SL.SSTL_NO)                  AS YD_MTL_SH
    	    					  FROM TB_YS_STKBED SB
    	    					      ,TB_YS_STKLYR SL
    	    					 WHERE SB.YS_STK_COL_GP= :V_YS_STK_COL_GP
    	    					   AND SB.YS_STK_COL_GP = SL.YS_STK_COL_GP
    	    					   AND SB.YS_STK_BED_NO = SL.YS_STK_BED_NO
    	    					   AND SB.DEL_YN        = 'N'
    	    					   AND SB.YD_STK_BED_ACT_STAT = 'L' --적치가능
    	    					 GROUP BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
    	    					 ORDER BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
    	    					 )
    	    					 WHERE YD_MTL_SH = 0
    	    					   AND ROWNUM = 1
    	    					 
    		    				   */
    		    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTc", logId, methodNm, "대차BED 조회"); 
    		            		
    		            		if(rsBed.size() <= 0) {
    		        				szMsg = LocalmethodNm + " : 대차 BED READ 실패!";
    		        				commUtils.printLog(logId, szMsg, "SL");
    		            		} else {
    		    					rsBed.first();
    		    					recBed = rsBed.getRecord();
    		    					szYS_STK_COL_GP	= commUtils.trim(recBed.getFieldString("YS_STK_COL_GP"));//대차정지위치 적치열
    		    					szYS_STK_BED_NO	= commUtils.trim(recBed.getFieldString("YS_STK_BED_NO"));//대차정지위치 적치베드
    		    					szYS_STK_LYR_NO	= commUtils.trim(recBed.getFieldString("YS_STK_LYR_NO"));//대차정지위치 적치단
    	    					
    		    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_STK_COL_GP + szYS_STK_BED_NO+szYS_STK_LYR_NO);
    		    					
    		    					this.procMainWrkLocRbA(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
    		            		}	    							
    							
    						} else {
    							
    							szMsg = LocalmethodNm + " : 대차위치가  작업동과 틀린 경우 + tcarUdCmplYn :"+ tcarUdCmplYn;
		        				commUtils.printLog(logId, szMsg, "SL");
		        				
    							if (tcarUdCmplYn.equals("0")) {
//	    							JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//	    							jrYdMsg.setResultCode(logId);	//Log ID
//	    							jrYdMsg.setResultMsg(methodNm);	//Log Method Name		    						
//	    							jrYdMsg.setField("YD_EQP_ID"     , tcarYdEqpId); //야드설비ID(대차)
//	    							jrYdMsg.setField("YD_TCAR_SCH_ID", ""); //야드대차스케쥴ID
//	    							jrYdMsg.setField("MODIFIER"    	, "YSSCH"   ); //수정자
    								    								
    		        				recInPara.setField("YS_STK_COL_GP", 	"K"+szSchCd.substring(1,2)+"TC01");
	    		    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTcXbay", logId, methodNm, "대차BED(다른동) 조회"); 
	    		    				szTcarSndyn = "Y";
	    		    				
	    		            		if(rsBed.size() <= 0) {
	    		        				szMsg = LocalmethodNm + " : 대차 BED READ 실패!";
	    		        				commUtils.printLog(logId, szMsg, "SL");		    	
	    		        				recWbook.setField("YD_TO_LOC_GUIDE", szSchCd.substring(0,2)+ "TY");
	    		        				
	    		            		} else {
	    		    					rsBed.first();
	    		    					recBed = rsBed.getRecord();
	    		    					szYS_STK_COL_GP	= commUtils.trim(recBed.getFieldString("YS_STK_COL_GP"));//대차정지위치 적치열
	    		    					szYS_STK_BED_NO	= commUtils.trim(recBed.getFieldString("YS_STK_BED_NO"));//대차정지위치 적치베드
	    		    					szYS_STK_LYR_NO	= commUtils.trim(recBed.getFieldString("YS_STK_LYR_NO"));//대차정지위치 적치단
	    	    					
	    		    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_STK_COL_GP + szYS_STK_BED_NO+szYS_STK_LYR_NO);		    		    					

		    							szMsg =LocalmethodNm + " : 대차가위치가  틀린동 + tcarUdCmplYn :K" + szYS_STK_COL_GP + szYS_STK_BED_NO+szYS_STK_LYR_NO;
	    		        				commUtils.printLog(logId, szMsg, "SL");
	    		            		}
	    		            		
    		        				this.procMainWrkLocRbA(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
    							}
    						}

    					}	

    					

           			} else if( szSchCd.substring(4,8).equals("HSUM")||szSchCd.substring(4,8).equals("HSBM")) {
                		/*****************************************************************************
        				* 출고 SC 작업 / 반납
        				******************************************************************************/            		

        				String szYS_UP_WO_LOC	= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LOC"  ));		
            			String szYS_STK_COL_GP	= szYS_UP_WO_LOC.substring(0, 6);
            			String szYS_DN_WO_LOC   = "";
            			String szYS_DN_WO_LAYER  = "";
    					if       (szYS_STK_COL_GP.substring(4,6).equals("01")||szYS_STK_COL_GP.substring(4,6).equals("02") ) {
    						szYS_DN_WO_LOC      = "KAHS1101";
    						szYS_DN_WO_LAYER 	= "01";
    					} else if(szYS_STK_COL_GP.substring(4,6).equals("03")||szYS_STK_COL_GP.substring(4,6).equals("04") ) {
    						szYS_DN_WO_LOC      = "KAHS1201";
    						szYS_DN_WO_LAYER 	= "01";
    					} else if(szYS_STK_COL_GP.substring(4,6).equals("05")||szYS_STK_COL_GP.substring(4,6).equals("06") ) {
    						szYS_DN_WO_LOC      = "KAHS1301";
    						szYS_DN_WO_LAYER 	= "01";
    					} else if(szYS_STK_COL_GP.substring(4,6).equals("07")||szYS_STK_COL_GP.substring(4,6).equals("08") ) {
    						szYS_DN_WO_LOC      = "KAHS1401";
    						szYS_DN_WO_LAYER 	= "01";
    					} else if(szYS_STK_COL_GP.substring(4,6).equals("09")||szYS_STK_COL_GP.substring(4,6).equals("10") ) {
    						szYS_DN_WO_LOC      = "KAHS1501";
    						szYS_DN_WO_LAYER 	= "01";
    					} else if(szYS_STK_COL_GP.substring(4,6).equals("11")||szYS_STK_COL_GP.substring(4,6).equals("12") ) {
    						szYS_DN_WO_LOC      = "KAHS1601";
    						szYS_DN_WO_LAYER 	= "01";
    					} else if(szYS_STK_COL_GP.substring(4,6).equals("13")||szYS_STK_COL_GP.substring(4,6).equals("14") ) {
    						szYS_DN_WO_LOC      = "KAHS1701";
    						szYS_DN_WO_LAYER 	= "01";
    					}
    					
    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_DN_WO_LOC + szYS_DN_WO_LAYER); 

    					szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량출고SC [TO위치 가이드 결정  : "+szYS_DN_WO_LOC + szYS_DN_WO_LAYER+"]";
        				commUtils.printLog(logId, szMsg, "SL");   					
    					
            			this.procMainWrkLocRbA(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);

           			} else if( szSchCd.substring(2,4).equals("TS") && szSchCd.substring(6,7).equals("B") ) {
                		/*****************************************************************************
        				* 동내 반납 --> KACRA2 작업
        				******************************************************************************/            		
    					recWbook.setField("YD_TO_LOC_GUIDE", szSchCd.substring(0,2)+ "TY");

        				this.procMainWrkLocRbA(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
        				
            			//-----------------------------------------------------------------------------------------------        				
        			} else {
        				/*****************************************************************************
        				* 주작업 TO위치 결정 모듈 호출  (야드로 TO 위치 결정 )
        				******************************************************************************/      

        				szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 주작업 TO위치결정-야드로 TO위치결정 시작";
        				commUtils.printLog(logId, szMsg, "SL");
       				
        				this.procMainWrkLocRbA(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
        				
        				szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]은 주작업 스케줄의 To위치 결정 완료 ";
            			commUtils.printLog(logId, szMsg, "SL");
        			}
            	}
        	}        	
        	
        	//-------------------------------------------------------------------------------------------------------------
    		// To위치 결정 실패시 default값으로 xx010101을 설정
        	//-------------------------------------------------------------------------------------------------------------
        	rsCrnsch 	= JDTORecordFactory.getInstance().createRecordSet("");
    		recInPara 	= JDTORecordFactory.getInstance().create();
    		recInPara.setField("YD_WBOOK_ID", szWbookId);
    		recInPara.setField("YD_EQP_ID",   szEqpId);
    		rsCrnsch = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnschByEqpIdandWBookId", logId, methodNm, "크레인스케줄 조회");   		
    		
    		for(int Loop_i = 1; Loop_i <= rsCrnsch.size(); Loop_i++) {
				rsCrnsch.absolute(Loop_i);
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setRecord(rsCrnsch.getRecord());
				if((commUtils.trim(recInPara.getFieldString("YS_DN_WO_LOC")).equals("")) && (szWhsToLocDcsn.equals("N"))){
					// 입고시 TO위치 결정 
					recInPara.setField("YS_DN_WO_LOC", "KAXXXXXX");
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtDnLoc
					--크레인작업관리  - 
					UPDATE TB_YS_CRNSCH
					   SET MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					     , YS_DN_WO_LOC = :V_YS_DN_WO_LOC
					 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					   AND DEL_YN         = 'N'
					*/				   
					intRtnVal = commDao.update(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtDnLoc", logId, methodNm, "크레인스케줄 갱신");
					if(intRtnVal <= 0){
	    				szMsg = LocalmethodNm +"크레인스케줄 To위치 Default값 등록 실패!!";
	    				commUtils.printLog(logId, szMsg, "SL");
					}
				} else if(commUtils.trim(recInPara.getFieldString("YS_DN_WO_LOC")).equals("")) {
					recInPara.setField("YS_DN_WO_LOC", "XX010101");
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtDnLoc
					--크레인작업관리  - 
					UPDATE TB_YS_CRNSCH
					   SET MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					     , YS_DN_WO_LOC = :V_YS_DN_WO_LOC
					 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					   AND DEL_YN         = 'N'
					*/				   
					intRtnVal = commDao.update(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtDnLoc", logId, methodNm, "크레인스케줄 갱신");
					if(intRtnVal <= 0){
	    				szMsg = LocalmethodNm + "크레인스케줄 To위치 Default값 등록 실패!!";
	    				commUtils.printLog(logId, szMsg, "SL");
					}
				}
			}
			
		//-------------------------------------------------------------------------------------------------------------
			
        	commUtils.printLog(logId, methodNm, "S-");
    		if(szTcarSndyn.equals("Y")) {
    			return intRtnVal = 9;
    		} else {
    			return intRtnVal = 1;
    		}
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    }//end of LocSrcRngDataSetRbA()

	/**
     * 오퍼레이션명 : 봉강 B동  TO 위치 결정
     * @param  inRecord, recGetCrnWrkMtl, rsResultCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int LocSrcRngDataSetRbB (String logId, String methodNms, JDTORecord inRecord)throws JDTOException{
    	String methodNm = "봉강 B동  TO 위치 결정[GdsYsSchSeEJB.LocSrcRngDataSetRbB] < " + methodNms;
    	String LocalmethodNm = "봉강 B동  TO 위치 결정[GdsYsSchSeEJB.LocSrcRngDataSetRbB] ";
    	JDTORecordSet rsCrnsch    	= JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsCrnwrkmtl 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsBed			= JDTORecordFactory.getInstance().createRecordSet("");
    	JDTORecordSet rsTemp 		= JDTORecordFactory.getInstance().createRecordSet("");

    	JDTORecord recWbook      	= null;
    	JDTORecord recCrnSch      	= null;
    	JDTORecord recInTemp 		= null;
    	JDTORecord recBed 		= null;
    	String szMsg        		= "";     	  
    	String szCrnSchId 			= "";
    	String szSchCd    			= "";
    	String szToLocDcsnMtd 		= "";
    	String szWbookId  			= "";
    	String szTcarSndyn  		= "N";
		String szEqpId    			= "";
    	int intRtnVal 				= 0 ;
    	String szWhsToLocDcsn 		= "Y"; //A동 입고시 목표동이  A동인 경우 TO위치 결정 안함
    	String szSchSkip            = ""; 	
        try{
        	commUtils.printLog(logId, methodNm, "S+");
        	//-------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------
			//파라미터 Null Check
        	szWbookId 	= commUtils.trim(inRecord.getFieldString("YD_WBOOK_ID" ));	
        	szEqpId 	= commUtils.trim(inRecord.getFieldString("YD_EQP_ID"   ));	
        			
			//-------------------------------------------------------------------------------------------------------------
			//작업예약을 조회한다. To위치 결정방법이  사용자 지정인지 알기위해서...
			//-------------------------------------------------------------------------------------------------------------
			rsTemp 		= JDTORecordFactory.getInstance().createRecordSet("");
			
			 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbook 

			SELECT YD_WBOOK_ID  AS YD_WBOOK_ID
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
			      ,YD_CAR_USE_GP
			      ,TRN_EQP_CD AS TRN_EQP_CD
			      ,CAR_NO AS CAR_NO
			      ,CARD_NO AS CARD_NO
			   FROM TB_YS_WRKBOOK
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			 */
			rsTemp = commDao.select(inRecord, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbook", logId, methodNm, "작업예약 조회"); 
	    	
	    	if (rsTemp == null || rsTemp.size() <= 0) {
				commUtils.printLog(logId, LocalmethodNm + "[스케쥴메인종료]", "SL");
			}			
			
			rsTemp.absolute(1);
 			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setRecord(rsTemp.getRecord());
			
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄 조회
			//-------------------------------------------------------------------------------------------------------------
			rsCrnsch = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_WBOOK_ID", szWbookId);
			recInPara.setField("YD_EQP_ID"	, szEqpId);
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnschByEqpIdandWBookId
			SELECT A.YD_EQP_ID               AS YD_EQP_ID                       
			      ,A.YD_EQP_NAME             AS YD_EQP_NAME                     
			      ,B.YD_CRN_SCH_ID           AS YD_CRN_SCH_ID                   
			      ,B.REGISTER                AS REGISTER                        
			      ,TO_CHAR(B.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT                        
			      ,B.MODIFIER                AS MODIFIER                        
			      ,TO_CHAR(B.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT                        
			      ,B.DEL_YN                  AS DEL_YN                          
			      ,B.YD_WBOOK_ID             AS YD_WBOOK_ID                     
                            :     
			      ,B.YS_UP_WR_LOC            AS YS_UP_WR_LOC                    
			      ,B.YS_UP_WR_LAYER          AS YS_UP_WR_LAYER                  
			      ,B.YS_UP_WR_SEQ_NO         AS YS_UP_WR_SEQ_NO
			      ,B.YD_UP_WRK_ACT_GP        AS YD_UP_WRK_ACT_GP                
			      ,B.YD_UP_WR_XAXIS          AS YD_UP_WR_XAXIS                  
			      ,B.YD_UP_WR_YAXIS          AS YD_UP_WR_YAXIS                  
			      ,B.YD_UP_WR_YAXIS1         AS YD_UP_WR_YAXIS1                 
			      ,B.YD_UP_WR_YAXIS2         AS YD_UP_WR_YAXIS2                 
			      ,B.YD_UP_WR_ZAXIS          AS YD_UP_WR_ZAXIS                  
			      ,B.YS_DN_WR_LOC            AS YS_DN_WR_LOC                    
			      ,B.YS_DN_WR_LAYER          AS YS_DN_WR_LAYER                  
			      ,B.YS_DN_WR_SEQ_NO         AS YS_DN_WR_SEQ_NO
			      ,B.YD_DN_WRK_ACT_GP        AS YD_DN_WRK_ACT_GP                
			      ,B.YD_DN_WR_XAXIS          AS YD_DN_WR_XAXIS                  
			      ,B.YD_DN_WR_YAXIS          AS YD_DN_WR_YAXIS                  
			      ,B.YD_DN_WR_YAXIS1         AS YD_DN_WR_YAXIS1                 
			      ,B.YD_DN_WR_YAXIS2         AS YD_DN_WR_YAXIS2                 
			      ,B.YD_DN_WR_ZAXIS          AS YD_DN_WR_ZAXIS                  
			  FROM TB_YS_EQP    A                                               
			      ,TB_YS_CRNSCH B                                               
			 WHERE B.YD_EQP_ID   = A.YD_EQP_ID                                  
			   AND B.YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND B.YD_EQP_ID   = :V_YD_EQP_ID                         
			   AND (B.DEL_YN IS NULL OR B.DEL_YN <> 'Y')                        
			 ORDER BY B.YD_CRN_SCH_ID   
			 */
			
			rsCrnsch = commDao.select(inRecord, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnschByEqpIdandWBookId", logId, methodNm, "크레인스케줄 조회"); 
			
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄의 권하지시위치 결정
			//-------------------------------------------------------------------------------------------------------------
		    for(int Loop_i = 1; Loop_i <= rsCrnsch.size(); Loop_i++) {

        		rsCrnsch.absolute(Loop_i);
        		recCrnSch  = rsCrnsch.getRecord();
        		
        		//크레인스케줄Data저장
        		szCrnSchId     = recCrnSch.getFieldString("YD_CRN_SCH_ID");
        		szSchCd        = recCrnSch.getFieldString("YD_SCH_CD");
        		szToLocDcsnMtd = recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD");
        		
        		szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]에 대한 권하지시위치 결정시작 ";
        		commUtils.printLog(logId, szMsg, "SL");
        		
        		//크레인작업재료조회
				rsCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
        		JDTORecord recInData = JDTORecordFactory.getInstance().create();
        		
        		recInData.setField("YD_CRN_SCH_ID", szCrnSchId);
        		
        		/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnwrkmtlBySchId 
        		SELECT A.SSTL_NO             AS SSTL_NO          
        		      ,A.YS_STK_LYR_NO       AS YS_STK_LYR_NO   
        		      ,A.YD_CRN_SCH_ID       AS YD_CRN_SCH_ID   
        		      ,A.REGISTER            AS REGISTER             
        		      ,A.REG_DDTT            AS REG_DDTT             
        		      ,A.MOD_DDTT            AS MOD_DDTT             
        		      ,A.MODIFIER            AS MODIFIER
        		      ,A.DEL_YN              AS DEL_YN                 
        		      ,A.YD_AID_WRK_YN       AS YD_AID_WRK_YN   
        		      ,A.HCR_GP              AS HCR_GP                 
        		      ,A.STL_PROG_CD         AS STL_PROG_CD       
        		      ,A.YS_ROUTE_GP         AS YS_ROUTE_GP
        		      ,B.YD_MTL_W            AS YD_MTL_W        
        		      ,B.YD_MTL_WT           AS YD_MTL_WT       
        		      ,B.YD_MTL_T            AS YD_MTL_T     
        		      ,B.YD_MTL_L            AS YD_MTL_L   
        		      ,B.YS_MTL_ITEM         AS YS_MTL_ITEM     
        		      ,B.YD_STK_LOT_TP       AS YD_STK_LOT_TP   
        		      ,B.YD_STK_LOT_CD       AS YD_STK_LOT_CD   
        		      ,B.REFUR_CHG_PLN_SERNO AS REFUR_CHG_PLN_SERNO
        		      ,B.BLOOM_CL_MTD        AS BLOOM_CL_MTD
        		      ,SUM(B.YD_MTL_WT) OVER (ORDER BY A.YS_STK_LYR_NO DESC) AS SUM_MTL_WT      
        		      ,SUM(B.YD_MTL_T)  OVER (ORDER BY A.YS_STK_LYR_NO DESC) AS SUM_MTL_T   
        		      ,MAX(B.YD_MTL_W)  OVER (ORDER BY A.YS_STK_LYR_NO DESC) AS MAX_MTL_W 
        		      ,MAX(B.YD_MTL_L)  OVER (ORDER BY A.YS_STK_LYR_NO DESC) AS MAX_MTL_L 
        		      ,COUNT(A.SSTL_NO) OVER (ORDER BY A.YS_STK_LYR_NO DESC) AS SH_CNT    
        		      ,NVL(MIN(B.YD_CHG_NO) OVER (ORDER BY A.YS_STK_LYR_NO DESC),0) AS YD_CHG_NO   
        		      ,(SELECT YS_UP_WO_LOC FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YS_UP_WO_LOC 
        		      ,(SELECT YS_UP_WO_LAYER FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YS_UP_WO_LAYER 
        		      ,(SELECT YD_WBOOK_ID FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YD_WBOOK_ID 
        		      ,(SELECT YD_EQP_ID FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YD_EQP_ID       
        		      ,A.YD_TO_LOC_DCSN_MTD AS YD_TO_LOC_DCSN_MTD       
        		  FROM TB_YS_CRNWRKMTL A                                                        
        		      ,TB_YS_STOCK     B                                                        
        		 WHERE A.SSTL_NO = B.SSTL_NO                                                      
        		   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
        		   AND A.DEL_YN = 'N'                                    
        		   AND B.DEL_YN = 'N'                                    
        		 ORDER BY A.YS_STK_LYR_NO
        		*/
           		
        		rsCrnwrkmtl = commDao.select(recInData, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnwrkmtlBySchId", logId, methodNm, "크레인스케줄재료 조회"); 
        		
        		if(rsCrnwrkmtl.size() <= 0) {
    				szMsg = LocalmethodNm + " : 위치검색범위 조회 Data Setting 실패!!";
    				commUtils.printLog(logId, szMsg, "SL");
        		}

    			recWbook = JDTORecordFactory.getInstance().create();
    			recWbook.setRecord(recInTemp);
       		
            	if(szToLocDcsnMtd.equals("W")) {
            		/**********************************************************
    				* 봉강보조작업인 경우 TO위치 결정 (일반적치대로...)
    				**********************************************************/            		
            		szMsg = LocalmethodNm + "["+ Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]" + " 스케줄코드[" + szSchCd + "]" 
					  					  + " isTY?[" + szSchCd.substring(2,4) + "]" + "은 보조작업 스케줄의  To위치 결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
        			
        			// TY임시베드와 메인창고의 더미작업 로직 분개
        			if("TY".equals(szSchCd.substring(2,4))){ 
        				szMsg = LocalmethodNm + "TY베드 To위치 결정 시작";
        				commUtils.printLog(logId, szMsg, "SL");
            			this.procDummyToLocRbTY(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);	
        			} else{
        				this.procDummyToLocRbB(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
        			}
        			
            	} else {
            		
            		szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]" + "스케줄코드[" + szSchCd + "]" + " : 주작업 TO위치결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
  
            		if( szSchCd.substring(2,4).equals("TR") && (szSchCd.substring(6,7).equals("U")  // 출하
            				||szSchCd.substring(6,7).equals("S")
            				||szSchCd.substring(6,7).equals("B")) ) {
                		/**********************************************************************
        				* 차량출고 : 차량이 정지한 적치열 조회 ==> TO위치가 됨  ==> TO 위치 가이드에 등록 처리
        				***********************************************************************/            		
            			String szRtnMsg = "";
            			String szYS_STK_COL_GP	= "";
            			String szYS_STK_BED_NO	= "";
            			String szYS_STK_LYR_NO	= "";
            			
            			String szYD_CAR_USE_GP	= commUtils.trim(recWbook.getFieldString("YD_CAR_USE_GP"));	//차량사용구분
	    				String szTRN_EQP_CD		= commUtils.trim(recWbook.getFieldString("TRN_EQP_CD"));	//운송장비코드
	    				String szCAR_NO			= commUtils.trim(recWbook.getFieldString("CAR_NO"));		//차량번호
	    				
	    				szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량상차작업예약["+szWbookId+"]의 차량정보[차량사용구분:"+szYD_CAR_USE_GP+", 운송장비코드:"+szTRN_EQP_CD+", 차량번호:"+szCAR_NO+"]에 대한 적치베드 조회 시작";
	    				commUtils.printLog(logId, szMsg, "SL");
    				
	    				rsBed	= JDTORecordFactory.getInstance().createRecordSet("");;
	    				recInPara = JDTORecordFactory.getInstance().create();
	    				
	    				if( szYD_CAR_USE_GP.equals("L") ) {				//구내운송
	    					
	    					recInPara.setField("TRN_EQP_CD", 		szTRN_EQP_CD);
		    				recInPara.setField("YD_CAR_USE_GP", 	szYD_CAR_USE_GP);
		    				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedByCarUseGpandTrnEqpCdBl
		    				SELECT YS_STK_COL_GP
		    				     , YS_STK_BED_NO 
		    				     , YS_STK_LYR_NO 
		    				     , YD_MTL_SH 
		    				  FROM
		    				(
		    				SELECT SB.YS_STK_COL_GP
		    				     , SB.YS_STK_BED_NO 
		    				     , SL.YS_STK_LYR_NO 
		    				      ,COUNT(SL.SSTL_NO) AS YD_MTL_SH
		    				  FROM TB_YS_STKBED SB
		    				      ,TB_YS_STKLYR SL
		    				      ,(SELECT YS_STK_COL_GP                         
		    				          FROM TB_YS_STKCOL                          
		    				         WHERE YD_CAR_USE_GP =:V_YD_CAR_USE_GP
		    				           AND TRN_EQP_CD = :V_TRN_EQP_CD
		    				           AND YD_STK_COL_ACT_STAT = 'L'             
		    				           AND DEL_YN='N' ) BL
		    				 WHERE SB.YS_STK_COL_GP = SL.YS_STK_COL_GP
		    				   AND SB.YS_STK_BED_NO = SL.YS_STK_BED_NO
		    				   AND SB.YS_STK_COL_GP = BL.YS_STK_COL_GP
		    				   AND SB.DEL_YN        = 'N'
		    				   AND SB.YD_STK_BED_ACT_STAT = 'L' --적치가능
		    				 GROUP BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
		    				 ORDER BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
		    				 )
		    				WHERE YD_MTL_SH = 0
		    				   AND ROWNUM = 1
		    				*/   
		    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedByCarUseGpandTrnEqpCdBl", logId, methodNm, "구내운송차량 차량BED 조회"); 
		            		
		            		if(rsBed.size() <= 0) {
		            			szMsg = LocalmethodNm + " : 구내운송차량 READ 실패!";
		        				commUtils.printLog(logId, szMsg, "SL");
		            		} else {
		            			szRtnMsg = YsConstant.RETN_CD_SUCCESS;
		            		}	
	    				} else if( szYD_CAR_USE_GP.equals("G") ) {		//출하차량
	    					
	    					recInPara.setField("YD_CAR_USE_GP", 	szYD_CAR_USE_GP);
		    				recInPara.setField("CAR_NO", 			szCAR_NO);
//		    				recInPara.setField("CARD_NO", 			szCARD_NO);
		    				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedByCarUseGpandCarNoBl
		    				SELECT YS_STK_COL_GP
		    				     , YS_STK_BED_NO 
		    				     , YS_STK_LYR_NO 
		    				     , YD_MTL_SH 
		    				  FROM
		    				(
		    				SELECT SB.YS_STK_COL_GP
		    				     , SB.YS_STK_BED_NO 
		    				     , SL.YS_STK_LYR_NO 
		    				      ,COUNT(SL.SSTL_NO) AS YD_MTL_SH
		    				  FROM TB_YS_STKBED SB
		    				      ,TB_YS_STKLYR SL
		    				      ,(SELECT YS_STK_COL_GP                         
		    				          FROM TB_YS_STKCOL                          
		    				         WHERE YD_CAR_USE_GP =:V_YD_CAR_USE_GP
		    				           AND CAR_NO = :V_CAR_NO
		    				           AND YD_STK_COL_ACT_STAT = 'L'             
		    				           AND DEL_YN='N' ) BL
		    				 WHERE SB.YS_STK_COL_GP = SL.YS_STK_COL_GP
		    				   AND SB.YS_STK_BED_NO = SL.YS_STK_BED_NO
		    				   AND SB.YS_STK_COL_GP = BL.YS_STK_COL_GP
		    				   AND SB.DEL_YN        = 'N'
		    				   AND SB.YD_STK_BED_ACT_STAT = 'L' --적치가능
		    				 GROUP BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
		    				 ORDER BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
		    				 )
		    				WHERE YD_MTL_SH = 0
		    				  AND ROWNUM = 1
		    				   */
		    				
		    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedByCarUseGpandCarNoBl", logId, methodNm, "출하 차량BED 조회"); 
		            		
		            		if(rsBed.size() <= 0) {
		        				szMsg = LocalmethodNm + " : 출하차량 READ 실패!";
		        				commUtils.printLog(logId, szMsg, "SL");
		            		} else {
		            			szRtnMsg = YsConstant.RETN_CD_SUCCESS;
		            		}
		    				
	    				}
	    				
	    				
	    				if( szRtnMsg.equals(YsConstant.RETN_CD_SUCCESS) ) {
	    					rsBed.first();
	    					recBed = rsBed.getRecord();
	    					szYS_STK_COL_GP	= commUtils.trim(recBed.getFieldString("YS_STK_COL_GP"));//차량정지위치 적치열
	    					szYS_STK_BED_NO	= commUtils.trim(recBed.getFieldString("YS_STK_BED_NO"));//차량정지위치 적치베드
	    					szYS_STK_LYR_NO	= commUtils.trim(recBed.getFieldString("YS_STK_LYR_NO"));//차량정지위치 적치단
	    					
	    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_STK_COL_GP + szYS_STK_BED_NO + szYS_STK_LYR_NO);
	
	    					szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량출고 [TO위치 가이드 결정  YD_TO_LOC_GUIDE : "+szYS_STK_COL_GP + szYS_STK_BED_NO + szYS_STK_LYR_NO+"]";
	        				commUtils.printLog(logId, szMsg, "SL");
	    					
	    				}else{
		    				
	    					recWbook.setField("YD_TO_LOC_GUIDE", "");
	    					szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량출고 [TO위치 가이드 미결정 ]";
	        				commUtils.printLog(logId, szMsg, "SL");
	    				}
	    				
	    				//-----------------------------------------------------------------------------------------------
        				szRtnMsg = this.procMainWrkLocRbB(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
            			
            			//-----------------------------------------------------------------------------------------------
        			} else if( (szSchCd.substring(2,4).equals("PC") || szSchCd.substring(2,4).equals("TY")) && szSchCd.substring(6,7).equals("L") ) {  
                		/*****************************************************************************
        				* 입고 작업
        				*  - 야드 목적동이 틀린 경우
        				*    . 대차 위치 확인하여 대차가 작업동에 있으면 상차 
        				*      작업동에 없으면 대차 상태를 확인 하여 작업이 없는 경우  출발지시 처리하고 작업지시 편성처리 함  
        				*   
        				******************************************************************************/            		
            			String szYS_STK_COL_GP	= "";
            			String szYS_STK_BED_NO	= "";
            			String szYS_STK_LYR_NO	= "";
            			

            			String szYD_AIM_BAY_GP	= commUtils.trim(recWbook.getFieldString("YD_AIM_BAY_GP"));	//야드 목적동
	    				
            			rsBed	= JDTORecordFactory.getInstance().createRecordSet("");;
            			recBed  = JDTORecordFactory.getInstance().create();
	    				recInPara  = JDTORecordFactory.getInstance().create();
	    				
	    				if (!szYD_AIM_BAY_GP.equals(szSchCd.substring(1,2))) {
	    					// 타 동으로 가야 할 경우 
		    				// 대차 위치 확인하여 대차가 현재동에 있으면 상차 
		    				//                     현재동에 없으면 대차 상태를 확인 하여  출발지시 처리 함  
	    					
	    					JDTORecordSet jsTc	= JDTORecordFactory.getInstance().createRecordSet("");;
            				JDTORecord   recTc  = JDTORecordFactory.getInstance().create();
            				
            				
            				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getTcBayWrk 
            				-- 재료 목표동에 에 대차작업 여부 
            				SELECT A.YD_EQP_ID
            				     , A.YD_CURR_BAY_GP
            				     , A.YD_HOME_BAY_GP 
            				     , (SELECT COUNT(*) 
            				          FROM TB_YS_STKLYR 
            				         WHERE YS_STK_COL_GP = 'K'||:V_YD_AIM_BAY_GP ||'TC01' 
            				           AND DEL_YN = 'N'
            				           AND SSTL_NO IS NOT NULL
            				       ) AS WK_CNT
            				  FROM TB_YS_EQP A
            				 WHERE A.YD_EQP_ID = :V_YD_EQP_ID
            				   AND A.YD_EQP_STAT <> 'B'
	    					*/ 
            				recInPara.setField("YD_EQP_ID", 	"KXTC01");
            				recInPara.setField("YD_AIM_BAY_GP", 	szYD_AIM_BAY_GP);
            
	    					//대차하차스케쥴 조회
	    					jsTc = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getTcBayWrk", logId, methodNm, "대차작업 여부");
	    					
	    					if (jsTc.size() > 0) {
	    						
	    						recTc = jsTc.getRecord(0);
	    						String ydTcarCurrBayGp  = commUtils.trim(recTc.getFieldString("YD_CURR_BAY_GP" )); //야드대차스케쥴ID
	    						String tcarUdCmplYn 	= commUtils.trim(recTc.getFieldString("WK_CNT")); //대차하차완료여부
	    						String tcarYdEqpId 		= commUtils.trim(recTc.getFieldString("YD_EQP_ID"));
	    						String ydEqpStat 		= commUtils.trim(recTc.getFieldString("YD_EQP_STAT"));
	    						
	    						recInPara  = JDTORecordFactory.getInstance().create();
	    						
	    						if(ydEqpStat.equals("B")) {
	    							if(szSchCd.substring(2,4).equals("PC")){
		    							recWbook.setField("YD_TO_LOC_GUIDE", szSchCd.substring(0,2)+ "TY");
		    							szMsg = LocalmethodNm + " : 대차 고장시 임시 BED 로"+ szSchCd.substring(0,2)+ "TY";
		    							commUtils.printLog(logId, szMsg, "SL");
	    							} else {
	    								szSchSkip = "Y";
	    							}
	    						} else if (szSchCd.substring(1,2).equals(ydTcarCurrBayGp)) {
	    							//대차가위치가  같은동이면
	    							szMsg = LocalmethodNm + " : 대차 위치가  작업동과 같은동";
    		        				commUtils.printLog(logId, szMsg, "SL");
    		        				
	    							recInPara.setField("YS_STK_COL_GP", 	"K"+ydTcarCurrBayGp+"TC01");
	    	    					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTc
	    	    					SELECT YS_STK_COL_GP
	    	    					     , YS_STK_BED_NO 
	    	    					     , YS_STK_LYR_NO  
	    	    					     , YD_MTL_SH 
	    	    					  FROM
	    	    					(
	    	    					SELECT SB.YS_STK_COL_GP
	    	    					     , SB.YS_STK_BED_NO 
	    	    					     , SL.YS_STK_LYR_NO 
	    	    					      ,COUNT(SL.SSTL_NO)                  AS YD_MTL_SH
	    	    					  FROM TB_YS_STKBED SB
	    	    					      ,TB_YS_STKLYR SL
	    	    					 WHERE SB.YS_STK_COL_GP= :V_YS_STK_COL_GP
	    	    					   AND SB.YS_STK_COL_GP = SL.YS_STK_COL_GP
	    	    					   AND SB.YS_STK_BED_NO = SL.YS_STK_BED_NO
	    	    					   AND SB.DEL_YN        = 'N'
	    	    					   AND SB.YD_STK_BED_ACT_STAT = 'L' --적치가능
	    	    					 GROUP BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
	    	    					 ORDER BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
	    	    					 )
	    	    					 WHERE YD_MTL_SH = 0
	    	    					   AND ROWNUM = 1
	    	    					 
	    		    				   */
	    		    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTc", logId, methodNm, "대차BED 조회"); 
	    		            		
	    		            		if(rsBed.size() <= 0) {
	    		        				szMsg = LocalmethodNm + " : 대차 BED READ 실패!";
	    		        				commUtils.printLog(logId, szMsg, "SL");
	    	
	    		        				recWbook.setField("YD_TO_LOC_GUIDE", szSchCd.substring(0,2)+ "TY");
	    		        				
	    		            		} else {
	    		    					rsBed.first();
	    		    					recBed = rsBed.getRecord();
	    		    					szYS_STK_COL_GP	= commUtils.trim(recBed.getFieldString("YS_STK_COL_GP"));//대차정지위치 적치열
	    		    					szYS_STK_BED_NO	= commUtils.trim(recBed.getFieldString("YS_STK_BED_NO"));//대차정지위치 적치베드
	    		    					szYS_STK_LYR_NO	= commUtils.trim(recBed.getFieldString("YS_STK_LYR_NO"));//대차정지위치 적치단
	    	    					
	    		    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_STK_COL_GP + szYS_STK_BED_NO+szYS_STK_LYR_NO);
	    		            		}	    							
	    							
	    						} else {
	    							
	    							szMsg = LocalmethodNm + " : 대차위치가  작업동과 틀린 경우 + tcarUdCmplYn :"+ tcarUdCmplYn;
    		        				commUtils.printLog(logId, szMsg, "SL");
    		        				
	    							if (tcarUdCmplYn.equals("0")) {
//		    							JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//		    							jrYdMsg.setResultCode(logId);	//Log ID
//		    							jrYdMsg.setResultMsg(methodNm);	//Log Method Name		    						
//		    							jrYdMsg.setField("YD_EQP_ID"     , tcarYdEqpId); //야드설비ID(대차)
//		    							jrYdMsg.setField("YD_TCAR_SCH_ID", ""); //야드대차스케쥴ID
//		    							jrYdMsg.setField("MODIFIER"    	, "YSSCH"   ); //수정자

		    							recInPara.setField("YS_STK_COL_GP", 	"K"+szSchCd.substring(1,2)+"TC01");
		    		    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTcXbay", logId, methodNm, "대차BED(다른동) 조회"); 
		    		    				szTcarSndyn = "Y";
		    		    				
		    		            		if(rsBed.size() <= 0) {
		    		        				szMsg = LocalmethodNm + " : 대차 BED READ 실패!";
		    		        				commUtils.printLog(logId, szMsg, "SL");		    	
		    		        				recWbook.setField("YD_TO_LOC_GUIDE", szSchCd.substring(0,2)+ "TY");
		    		        				
		    		            		} else {
		    		    					rsBed.first();
		    		    					recBed = rsBed.getRecord();
		    		    					szYS_STK_COL_GP	= commUtils.trim(recBed.getFieldString("YS_STK_COL_GP"));//대차정지위치 적치열
		    		    					szYS_STK_BED_NO	= commUtils.trim(recBed.getFieldString("YS_STK_BED_NO"));//대차정지위치 적치베드
		    		    					szYS_STK_LYR_NO	= commUtils.trim(recBed.getFieldString("YS_STK_LYR_NO"));//대차정지위치 적치단
		    	    					
		    		    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_STK_COL_GP + szYS_STK_BED_NO+szYS_STK_LYR_NO);		    		    					

			    							szMsg =LocalmethodNm + " : 대차가위치가  틀린동 + tcarUdCmplYn :K" + szYS_STK_COL_GP + szYS_STK_BED_NO+szYS_STK_LYR_NO;
		    		        				commUtils.printLog(logId, szMsg, "SL");
		    		            		}
	    		        				
	    							} else {
	    								if(szSchCd.substring(2,4).equals("PC")){
			    							recWbook.setField("YD_TO_LOC_GUIDE", szSchCd.substring(0,2)+ "TY");
			    							szMsg = LocalmethodNm + " : 대차 고장시 임시 BED 로"+ szSchCd.substring(0,2)+ "TY";
			    							commUtils.printLog(logId, szMsg, "SL");
		    							} else {
		    								szSchSkip = "Y";
		    							}
	    							}
	    						}

	    					} else {
	    						szSchSkip = "Y";
	    					}
	    				
	    				
	    				} else {
	    					// 비오비오비오 TY, KB01
	    					
	    					szWhsToLocDcsn = "Y";
	    					
	    					String szYD_TO_LOC_GUIDE	= commUtils.trim(recWbook.getFieldString("YD_TO_LOC_GUIDE"));	//작업예약 등록된 TO위치가이드
	    					if(szYD_TO_LOC_GUIDE.equals("KBC1")){
	    						szMsg = LocalmethodNm + ": KBC1시 TO위치가이즈 초기화 안함.";
	    					}
	    					else{
	    						recWbook.setField("YD_TO_LOC_GUIDE", "");
	    						szMsg = LocalmethodNm + ": 작업동으로 입고처리 해야 함 ";
	    					}
							
							commUtils.printLog(logId, szMsg, "SL");
							
							
							
							/*
							 * 봉강 4PC[KBPC04] 제품 B동 전량 직입고
							 * 2020.05.13 오주원 주임 요청으로 다시 막음
							 */
							/*
							if("KBPC04LM".equals(szSchCd)){
								recWbook.setField("YD_TO_LOC_GUIDE", "KBXXYY");
								
								szMsg = LocalmethodNm + ": KBXXYY로 입고처리 해야 함 ";
								commUtils.printLog(logId, szMsg, "SL");
							}
							*/
	        			}	

	    				if(szSchSkip.equals("")){
		    				if(szWhsToLocDcsn.equals("Y")){
		    					this.procMainWrkLocRbB(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
		    					
		    				} else {
		        				szMsg =  LocalmethodNm +"[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 입고  주작업TO위치 결정 안함" ;
		        				commUtils.printLog(logId, szMsg, "SL");
		    				}
	    				}	
            			//-----------------------------------------------------------------------------------------------
// 반납과 포함	    				
//          			} else if( szSchCd.substring(2,4).equals("TC") && szSchCd.substring(6,7).equals("U") ) {  
//                		/*****************************************************************************
//        				* 대차상차 :  대차 도착된 위치  ==> TO위치가 됨  ==> TO 위치 가이드에 등록 처리
//        				******************************************************************************/            		
//
//          				String szYS_STK_COL_GP	= "";
//            			String szYS_STK_BED_NO	= "";
//            			String szYS_STK_LYR_NO	= "";
//            			
//            			szMsg =  LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 대차상차 주작업[TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 시작";
//            			commUtils.printLog(logId, szMsg, "SL");
//
//            			String szYD_WRK_PLAN_TCAR	= commUtils.trim(recWbook.getFieldString("YD_WRK_PLAN_TCAR"));	//야드 작업 계획 대차
//	    				
//	    				rsBed	= JDTORecordFactory.getInstance().createRecordSet("");;
//	    				recInPara = JDTORecordFactory.getInstance().create();
//	    				
//	    				if( !szYD_WRK_PLAN_TCAR.equals("") ) {	
//	    					
//	    					recInPara.setField("YS_STK_COL_GP", 	szSchCd.substring(0,6));
//		    				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTc 
//		    				SELECT YS_STK_COL_GP
//		    				     , YS_STK_BED_NO 
//		    				     , YS_STK_LYR_NO 
//		    				     , YD_MTL_SH 
//		    				  FROM
//		    				(
//		    				SELECT SB.YS_STK_COL_GP
//		    				     , SB.YS_STK_BED_NO 
//		    				     , SL.YS_STK_LYR_NO 
//		    				      ,COUNT(SL.SSTL_NO)                  AS YD_MTL_SH
//		    				  FROM TB_YS_STKBED SB
//		    				      ,TB_YS_STKLYR SL
//		    				 WHERE SB.YS_STK_COL_GP LIKE :V_YS_STK_COL_GP
//		    				   AND SB.YS_STK_COL_GP = SL.YS_STK_COL_GP
//		    				   AND SB.YS_STK_BED_NO = SL.YS_STK_BED_NO
//		    				   AND SB.DEL_YN        = 'N'
//		    				   AND SB.YD_STK_BED_ACT_STAT = 'L' --적치가능
//		    				 GROUP BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
//		    				 ORDER BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
//		    				 )
//		    				 WHERE YD_MTL_SH = 0
//		    				   AND ROWNUM = 1
//		    				   */
//		    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTc", logId, methodNm, "대차BED 조회"); 
//		            		
//		            		if(rsBed.size() <= 0) {
//		        				szMsg =  LocalmethodNm +" : 대차 BED READ 실패!";
//		        				commUtils.printLog(logId, szMsg, "SL");
//
//		        				recWbook.setField("YD_TO_LOC_GUIDE", "");
//		        				
//		            		} else {
//		    					rsBed.first();
//		    					recBed = rsBed.getRecord();
//		    					szYS_STK_COL_GP	= commUtils.trim(recBed.getFieldString("YS_STK_COL_GP"));//차량정지위치 적치열
//		    					szYS_STK_BED_NO	= commUtils.trim(recBed.getFieldString("YS_STK_BED_NO"));//차량정지위치 적치베드
//		    					szYS_STK_LYR_NO	= commUtils.trim(recBed.getFieldString("YS_STK_LYR_NO"));//차량정지위치 적치단
//	    					
//		    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_STK_COL_GP + szYS_STK_BED_NO+szYS_STK_LYR_NO);
//		            		}	
//	    					
//	    				}else{
//	    					recWbook.setField("YD_TO_LOC_GUIDE", "");
//	    				}
//	    				
//        				this.procMainWrkLocRbB(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
          			
          			} else if(( szSchCd.substring(2,4).equals("YD") && szSchCd.substring(6,7).equals("B"))
          			           || ( szSchCd.substring(2,4).equals("TC") && szSchCd.substring(6,7).equals("U") )) {
          				
          				String szYD_AIM_BAY_GP		= commUtils.trim(recWbook.getFieldString("YD_AIM_BAY_GP"));	//야드 목적동
          				String szYD_WRK_PLAN_TCAR	= commUtils.trim(recWbook.getFieldString("YD_WRK_PLAN_TCAR"));	//야드 목적동
	    				
          				if(!szYD_WRK_PLAN_TCAR.equals("")) {
	                		/*****************************************************************************
	        				* 대차반납 작업
	        				******************************************************************************/            		
	            			String szYS_STK_COL_GP	= "";
	            			String szYS_STK_BED_NO	= "";
	            			String szYS_STK_LYR_NO	= "";
	            			
	            			szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 대차반납 주작업 To위치 결정 시작";
	            			commUtils.printLog(logId, szMsg, "SL");
	
	            			
	            			rsBed	= JDTORecordFactory.getInstance().createRecordSet("");;
	            			recBed  = JDTORecordFactory.getInstance().create();
		    				recInPara  = JDTORecordFactory.getInstance().create();
	    					// 타 동으로 가야 할 경우 
		    				// 대차 위치 확인하여 대차가 현재동에 있으면 상차 
		    				//                     현재동에 없으면 대차 상태를 확인 하여  출발지시 처리 함  
	    					
	    					JDTORecordSet jsTc	= JDTORecordFactory.getInstance().createRecordSet("");;
	        				JDTORecord   recTc  = JDTORecordFactory.getInstance().create();
	        				
	        				
	        				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getTcBayWrk 
	        				-- 재료 목표동에 에 대차작업 여부 
	        				SELECT A.YD_EQP_ID
	        				     , A.YD_CURR_BAY_GP
	        				     , A.YD_HOME_BAY_GP 
	        				     , (SELECT COUNT(*) 
	        				          FROM TB_YS_STKLYR 
	        				         WHERE YS_STK_COL_GP = 'K'||:V_YD_AIM_BAY_GP ||'TC01' 
	        				           AND DEL_YN = 'N'
	        				           AND SSTL_NO IS NOT NULL
	        				       ) AS WK_CNT
	        				  FROM TB_YS_EQP A
	        				 WHERE A.YD_EQP_ID = :V_YD_EQP_ID
	        				   AND A.YD_EQP_STAT <> 'B'
	    					*/ 
	        				recInPara.setField("YD_EQP_ID"		, "KXTC01");
	        				recInPara.setField("YD_AIM_BAY_GP"	, szYD_AIM_BAY_GP);
	        
	    					//대차하차스케쥴 조회
	    					jsTc = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getTcBayWrk", logId, methodNm, "대차작업 여부");
	    					
	    					if (jsTc.size() > 0) {
	    						
	    						recTc = jsTc.getRecord(0);
	    						String ydTcarCurrBayGp  = commUtils.trim(recTc.getFieldString("YD_CURR_BAY_GP" )); //야드대차스케쥴ID
	    						String tcarUdCmplYn 	= commUtils.trim(recTc.getFieldString("WK_CNT")); //대차하차완료여부
	    						String tcarYdEqpId 		= commUtils.trim(recTc.getFieldString("YD_EQP_ID"));
	    						String ydEqpStat 		= commUtils.trim(recTc.getFieldString("YD_EQP_STAT"));
	    						recInPara  = JDTORecordFactory.getInstance().create();
	    						
	    						if(ydEqpStat.equals("B")) {
	    							
	    						} else if (szSchCd.substring(1,2).equals(ydTcarCurrBayGp)) {
	    							//대차가위치가  같은동이면
	    							szMsg = LocalmethodNm + " : 대차 위치가  작업동과 같은동";
			        				commUtils.printLog(logId, szMsg, "SL");
			        				
	    							recInPara.setField("YS_STK_COL_GP", 	"K"+ydTcarCurrBayGp+"TC01");
	    	    					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTc
	    	    					SELECT YS_STK_COL_GP
	    	    					     , YS_STK_BED_NO 
	    	    					     , YS_STK_LYR_NO  
	    	    					     , YD_MTL_SH 
	    	    					  FROM
	    	    					(
	    	    					SELECT SB.YS_STK_COL_GP
	    	    					     , SB.YS_STK_BED_NO 
	    	    					     , SL.YS_STK_LYR_NO 
	    	    					      ,COUNT(SL.SSTL_NO)                  AS YD_MTL_SH
	    	    					  FROM TB_YS_STKBED SB
	    	    					      ,TB_YS_STKLYR SL
	    	    					 WHERE SB.YS_STK_COL_GP= :V_YS_STK_COL_GP
	    	    					   AND SB.YS_STK_COL_GP = SL.YS_STK_COL_GP
	    	    					   AND SB.YS_STK_BED_NO = SL.YS_STK_BED_NO
	    	    					   AND SB.DEL_YN        = 'N'
	    	    					   AND SB.YD_STK_BED_ACT_STAT = 'L' --적치가능
	    	    					 GROUP BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
	    	    					 ORDER BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
	    	    					 )
	    	    					 WHERE YD_MTL_SH = 0
	    	    					   AND ROWNUM = 1
	    	    					 
	    		    				   */
	    		    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTc", logId, methodNm, "대차BED 조회"); 
	    		            		
	    		            		if(rsBed.size() <= 0) {
	    		        				szMsg = LocalmethodNm + " : 대차 BED READ 실패!";
	    		        				commUtils.printLog(logId, szMsg, "SL");
	    		            		} else {
	    		    					rsBed.first();
	    		    					recBed = rsBed.getRecord();
	    		    					szYS_STK_COL_GP	= commUtils.trim(recBed.getFieldString("YS_STK_COL_GP"));//대차정지위치 적치열
	    		    					szYS_STK_BED_NO	= commUtils.trim(recBed.getFieldString("YS_STK_BED_NO"));//대차정지위치 적치베드
	    		    					szYS_STK_LYR_NO	= commUtils.trim(recBed.getFieldString("YS_STK_LYR_NO"));//대차정지위치 적치단
	    	    					
	    		    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_STK_COL_GP + szYS_STK_BED_NO+szYS_STK_LYR_NO);
	    		    					
	    		    					this.procMainWrkLocRbB(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
	    		            		}	    							
	    							
	    						} else {	
	    							
	    							szMsg = LocalmethodNm + " : 대차위치가  작업동과 틀린 경우 + tcarUdCmplYn :"+ tcarUdCmplYn;
			        				commUtils.printLog(logId, szMsg, "SL");
			        				
	    							if (tcarUdCmplYn.equals("0")) {
//		    							JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//		    							jrYdMsg.setResultCode(logId);	//Log ID
//		    							jrYdMsg.setResultMsg(methodNm);	//Log Method Name		    						
//		    							jrYdMsg.setField("YD_EQP_ID"     	, tcarYdEqpId); //야드설비ID(대차)
//		    							jrYdMsg.setField("YD_TCAR_SCH_ID"	, ""); //야드대차스케쥴ID
//		    							jrYdMsg.setField("MODIFIER"    		, "YSSCH"   ); //수정자	    								
	    								
	    		        				recInPara.setField("YS_STK_COL_GP", 	"K"+szSchCd.substring(1,2)+"TC01");
		    		    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTcXbay", logId, methodNm, "대차BED(다른동) 조회"); 
		    		    				szTcarSndyn = "Y";
		    		    				
		    		            		if(rsBed.size() <= 0) {
		    		        				szMsg = LocalmethodNm + " : 대차 BED READ 실패!";
		    		        				commUtils.printLog(logId, szMsg, "SL");		    	
		    		        				recWbook.setField("YD_TO_LOC_GUIDE", szSchCd.substring(0,2)+ "TY");
		    		        				
		    		            		} else {
		    		    					rsBed.first();
		    		    					recBed = rsBed.getRecord();
		    		    					szYS_STK_COL_GP	= commUtils.trim(recBed.getFieldString("YS_STK_COL_GP"));//대차정지위치 적치열
		    		    					szYS_STK_BED_NO	= commUtils.trim(recBed.getFieldString("YS_STK_BED_NO"));//대차정지위치 적치베드
		    		    					szYS_STK_LYR_NO	= commUtils.trim(recBed.getFieldString("YS_STK_LYR_NO"));//대차정지위치 적치단
		    	    					
		    		    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_STK_COL_GP + szYS_STK_BED_NO+szYS_STK_LYR_NO);		    		    					

			    							szMsg =LocalmethodNm + " : 대차가위치가  틀린동 + tcarUdCmplYn :K" + szYS_STK_COL_GP + szYS_STK_BED_NO+szYS_STK_LYR_NO;
		    		        				commUtils.printLog(logId, szMsg, "SL");
		    		            		}
	    		        				this.procMainWrkLocRbB(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
	    							}
	    						}
	
	    					}	
          				} else {
          					
            				szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 일반반납 TO위치결정-야드로 TO위치결정 시작";
            				commUtils.printLog(logId, szMsg, "SL");
        				
            				this.procMainWrkLocRbB(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
        				
            				szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]은 일반반납주작업 스케줄의 To위치 결정 완료 ";
            				commUtils.printLog(logId, szMsg, "SL");
         				}
          				
   						
        			} else {
        				/*****************************************************************************
        				* 주작업 TO위치 결정 모듈 호출  (야드로 TO 위치 결정 )
        				******************************************************************************/      

        				szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 주작업 TO위치결정-야드로 TO위치결정 시작";
        				commUtils.printLog(logId, szMsg, "SL");
        				
        				this.procMainWrkLocRbB(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
        				
        				szMsg = LocalmethodNm + "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]은 주작업 스케줄의 To위치 결정 완료 ";
            			commUtils.printLog(logId, szMsg, "SL");
        			}
            	}
        	}        	
        	
        	//-------------------------------------------------------------------------------------------------------------
    		// To위치 결정 실패시 default값으로 xx010101을 설정
        	//-------------------------------------------------------------------------------------------------------------
        	rsCrnsch 	= JDTORecordFactory.getInstance().createRecordSet("");
    		recInPara 	= JDTORecordFactory.getInstance().create();
    		recInPara.setField("YD_WBOOK_ID", szWbookId);
    		recInPara.setField("YD_EQP_ID",   szEqpId);
    		rsCrnsch = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnschByEqpIdandWBookId", logId, methodNm, "크레인스케줄 조회");   		
    		
    		for(int Loop_i = 1; Loop_i <= rsCrnsch.size(); Loop_i++) {
				rsCrnsch.absolute(Loop_i);
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setRecord(rsCrnsch.getRecord());
				if(commUtils.trim(recInPara.getFieldString("YS_DN_WO_LOC")).equals("")) {
					recInPara.setField("YS_DN_WO_LOC", "XX010101");
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtDnLoc
					--크레인작업관리  - 
					UPDATE TB_YS_CRNSCH
					   SET MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					     , YS_DN_WO_LOC = :V_YS_DN_WO_LOC
					 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					   AND DEL_YN         = 'N'
					*/				   
					intRtnVal = commDao.update(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtDnLoc", logId, methodNm, "크레인스케줄 갱신");
					if(intRtnVal <= 0){
	    				szMsg = LocalmethodNm + "크레인스케줄 To위치 Default값 등록 실패!!";
	    				commUtils.printLog(logId, szMsg, "SL");
					}
				}
			}
			
		//-------------------------------------------------------------------------------------------------------------
        	commUtils.printLog(logId, methodNm, "S-");
    		if(szTcarSndyn.equals("Y")) {
    			return intRtnVal = 9;
    		} else {
    			return intRtnVal = 1;
    		}
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    }//end of LocSrcRngDataSetRbB()   
}