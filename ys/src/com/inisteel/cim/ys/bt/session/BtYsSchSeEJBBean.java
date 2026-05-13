/**
 * @(#)BtYsSchSeEJBBean
 *
 * @version          V1.00
 * @author           조병기
 * @date             2014/12/22
 *
 * @description      BILLET 야드 Schedule 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.bt.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ys.bt.dao.BtYsDAO;
import com.inisteel.cim.ys.common.session.YsComm;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;



/**
 *      [A] 클래스명 : BILLET 야드 Schedule 처리
 *
 * @ejb.bean name="BtYsSchSeEJB" jndi-name="BtYsSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class BtYsSchSeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	private BtYsDAO BtYsDao = new BtYsDAO();
	private YsComm YsComm = new YsComm();	
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	/**	
	 *      [A] 오퍼레이션명 : 크레인스케줄(YDYDJ400)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYSYSJ202(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인스케줄[BtYsSchSeEJB.rcvYSYSJ202] < " + rcvMsg.getResultMsg();
	
		String logId = rcvMsg.getResultCode();
		JDTORecordSet rsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");
		//Vector 선언
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

			JDTORecord jrRtn = null;	//전문 Return
//			String trtGp   = "";		//처리구분
			String trtMsg  = ""; 		//처리메세지
			String ydL3Msg = ""; 		//야드L3MESSAGE
//			String tmpStr  = "";		//임시변수

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
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchWbook
				SELECT YD_WBOOK_ID
				  FROM TB_YS_WRKBOOK
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND DEL_YN      = 'N'
				   AND YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
				                             FROM TB_YS_CRNSCH
                            WHERE DEL_YN = 'N')
				 */
				rsWbook = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchWbook", logId, methodNm, "작업예약 조회");
				
			} else if (!"".equals(ydSchCd)) {
				//스케줄코드가 있으면
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchWbookSchcd
				SELECT YD_WBOOK_ID
				  FROM (SELECT YD_WBOOK_ID
				          FROM TB_YS_WRKBOOK
				         WHERE YD_SCH_CD = :V_YD_SCH_CD
				           AND DEL_YN    = 'N'
				           AND YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
				                                     FROM TB_YS_CRNSCH
				                                    WHERE DEL_YN = 'N')
				         ORDER BY YD_SCH_PRIOR, YD_WBOOK_ID)
				 WHERE ROWNUM = 1
				 */
				rsWbook = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchWbookSchcd", logId, methodNm, "작업예약 조회");
				 
			} else if (!"".equals(ydEqpId)) {
				//설비ID가 있으면
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchWbookEqp
				SELECT YD_WBOOK_ID
				  FROM (SELECT WB.YD_WBOOK_ID
				          FROM 
				               (SELECT A.YD_GP
				                      ,A.YD_BAY_GP
				                      ,YD_SCH_CD
				                      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN1
				                            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN2
				                        END AS YD_WRK_CRN
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
				                --    AND A.YD_SCH_CD LIKE :V_YD_SCH_CD || '%'
				                    AND   A.YD_DATA_GP = 'M'
				                    AND   A.YD_SCH_GP = B.YD_SCH_GP
				                    AND   A.YD_GP = B.YD_GP
				                    AND   A.YD_BAY_GP = B.YD_BAY_GP
				                    AND   A.YD_CRN_STAT1 = B.STAT1
				                    AND   A.YD_CRN_STAT2 = B.STAT2
				                ) SR
				              ,TB_YS_WRKBOOK WB
				         WHERE SR.YD_SCH_CD       = WB.YD_SCH_CD
				           AND SR.YD_WRK_CRN      = :V_YD_EQP_ID
				           AND WB.DEL_YN          = 'N'
				           AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
				                                        FROM TB_YS_CRNSCH
				                                       WHERE DEL_YN = 'N')
				         ORDER BY WB.YD_SCH_PRIOR, WB.YD_WBOOK_ID)
				 WHERE ROWNUM = 1
				 */
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
			EJBConnector tranConn = new EJBConnector("default", "BtYsSchSeEJB", this);
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
			String sYD_WBOOK_DT   = "";	
			trtMsg = "상태정보Check [작업예약ID : " + ydWbookId + "]";

			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchStat 
			SELECT WB.YD_GP                                   --야드구분
			      ,WB.YD_BAY_GP                               --야드동구분
			      ,WB.YD_SCH_CD                               --야드스케쥴코드
			      ,WB.YD_SCH_PRIOR                            --야드스케쥴우선순위
			      ,WB.YD_TO_LOC_DCSN_MTD                      --야드TO위치결정방법
			      ,WB.YD_TO_LOC_GUIDE                         --야드TO위치GUIDE
			      ,CASE WHEN WB.YD_SCH_CD LIKE '__TR__U_' THEN 'C' --차량상차
			            WHEN WB.YD_SCH_CD LIKE '__TC__U_' THEN 'T' --대차상차
			            WHEN LENGTH(WB.YD_TO_LOC_GUIDE) >= 4       --SPAN구분 이상이면
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
			                                      ,YD_EQP_GP AS YD_SCH_GP
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
				ydToLocDcsnMtd         = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_DCSN_MTD" ));	//야드To위치결정방법
				ydToLocGuide           = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_GUIDE"    ));	//야드To위치Guide
				toLocChkGp             = commUtils.trim(jrChk.getFieldString("TO_LOC_CHK_GP"      ));	//To위치점검구분
				ydSchPrior             = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN_PRIOR"   ));	//야드스케쥴우선순위
				String ydWrkPlanCrn    = commUtils.trim(jrChk.getFieldString("YD_WRK_PLAN_CRN"    ));	//야드작업계획크레인
				String ydEqpStatPln    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_PLN"    ));	//야드설비상태(작업계획크레인)
				String ydEqpWrkModePln = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_PLN"));	//야드설비작업Mode(작업계획크레인)
				String ydWrkCrn        = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN"         ));	//야드작업크레인
				String ydEqpStatWrk    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_WRK"    ));	//야드설비상태(작업크레인)
				String cmDupYn         = commUtils.trim(jrChk.getFieldString("CM_DUP_YN"          ));	//크레인스케줄 재료중복여부
				String clDupGp         = commUtils.trim(jrChk.getFieldString("CL_DUP_GP"          ));	//크레인스케줄 저장위치중복여부
				int ttMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("TT_MTL_SH"),"0"));	//전체 재료매수
				int wmMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("WM_MTL_SH"),"0"));	//작업예약 재료매수
				int stMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("ST_MTL_SH"),"0"));	//저장품 재료매수
				int slMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("SL_MTL_SH"),"0"));	//적치단 재료매수
				int statCSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("STAT_C_SH"),"0"));	//적치중인 재료매수(야드적치단재료상태가 적치 중[C] 인 재료수)
				int abLocSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("AB_LOC_SH"),"0"));	//저장위치이상 재료매수
				sYD_WBOOK_DT          = commUtils.trim(jrChk.getFieldString("YD_WBOOK_DT"          ));	
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
				} else {
					ydEqpId   = ydWrkCrn;		//야드설비ID
					ydEqpStat = ydEqpStatWrk;	//야드설비상태
				}
				commUtils.printLog(logId, trtMsg + " >> 작업예약 지정크레인[" + ydWrkPlanCrn + "]으로 설정", "SL");
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
						throw new Exception("오류:" + trtMsg + " >> 구내운송 상차작업 운송장비코드 없음");
					} else if ("G".equals(ydCarUseGp)) {
						if ("".equals(carNo)) {
								throw new Exception("오류:" + trtMsg + " >> 출하차량 상차작업 차량번호 또는 카드번호 없음");
						}
					}
				}
				
				//야드To위치Guide 값이 4자리 이상이고 To 야드동이 같을 경우가 아니면
				//TO 위치 가이드('G') 아니면 야드To위치결정방법,야드To위치Guide CLEAR
				if (!"G".equals(toLocChkGp)) {
					ydToLocDcsnMtd = ""; //야드To위치결정방법
					ydToLocGuide   = ""; //야드To위치Guide
				}
			}
	
			JDTORecord jrParamSet = JDTORecordFactory.getInstance().create();
			jrParamSet.setResultCode(logId);	//Log ID
			jrParamSet.setResultMsg(methodNm);	//Log Method Name
			jrParamSet.setField("YD_WBOOK_ID"			, ydWbookId); //야드작업예약ID
			jrParamSet.setField("YD_SCH_CD"  			, ydSchCd  ); //야드스케쥴코드
			jrParamSet.setField("YD_EQP_ID"  			, ydEqpId  ); //야드설비ID
			jrParamSet.setField("YD_SCH_PRIOR"  		, ydSchPrior  ); //야드스케쥴우선순위
			jrParamSet.setField("YD_TO_LOC_DCSN_MTD"  	, ydToLocDcsnMtd  ); 	//야드To위치결정방법
			jrParamSet.setField("YD_TO_LOC_GUIDE"  		, ydToLocGuide  ); 		//야드To위치Guide
			jrParamSet.setField("MODIFIER"   			, modifier ); //수정자
			jrParamSet.setField("YD_WBOOK_DT"  		    , sYD_WBOOK_DT );
			
			/**********************************************************
			* 2.그룹핑 파라미터 셋팅
            *  적치단의 재료상태를 권상대기로 변경처리 
            *  주작업 및 보조 작업 셋팅
  			**********************************************************/

			//------------------------------------------------------------------------------------------------------------
			//	그룹핑 파라미터 셋팅
			//------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, "그룹핑 파라미터 셋팅 시작", "SL");			

			JDTORecordSet outRecset	= JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			if( ydSchCd.substring(2,4).equals("TR") && ydSchCd.substring(6,7).equals("L")) {  
				/*
				 * 빌렛차량하차시 단우선으로 그룹핑 될수 있도록 따로 분리
				 * 2017.12.21 윤재광
				 */
				intRtnVal = this.CrnSchGrpTr(logId, methodNm, jrParamSet, outRecset);
			}else{
				intRtnVal = this.CrnSchGrp(logId, methodNm, jrParamSet, outRecset);
			}
			
			if(intRtnVal == -1) {
				m_ctx.setRollbackOnly();
				throw new Exception("오류:그룹핑 파라미터 셋팅 없음");
			}
			//------------------------------------------------------------------------------------------------------------
			//	크레인스케줄과 크레인작업재료 등록 이때 권상위치 U로 변경
			//------------------------------------------------------------------------------------------------------------
			
			commUtils.printLog(logId, "크레인스케줄과 크레인작업재료 등록 시작 ", "SL");			
				
			intRtnVal = this.CrnSchIns(logId, methodNm, outRecset, jrParamSet);
			if(intRtnVal == -1) {
				m_ctx.setRollbackOnly();
				throw new DAOException("크레인스케줄 및 작업재료 등록 오류");
			}

			//------------------------------------------------------------------------------------------------------------
			//	TO 저장위치 결정
			//------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, "TO 저장위치 등록 시작 ", "SL");			
				
			intRtnVal = this.LocSrcRngDataSet(logId, methodNm, jrParamSet);
			if(intRtnVal == -1) {
				m_ctx.setRollbackOnly();
				throw new DAOException("TO 저장위치 등록  오류");
			}
			

			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄의 설비호기 재호출
			//-------------------------------------------------------------------------------------------------------------
			JDTORecordSet rsCrnsch = commDao.select(jrParamSet, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsCrnSch", logId, methodNm, "크레인스케줄 조회"); 	    	
			
			commUtils.printLog(logId, "작업예약 ID로 크레인스케줄 조회 스케줄의 횟수 : " + rsCrnsch.size(), "SL");	
			String szYD_EQP_ID	= "";
			
			if(rsCrnsch.size()>0){
				rsCrnsch.first();
				JDTORecord recInPara = rsCrnsch.getRecord();
				szYD_EQP_ID	= commUtils.trim(recInPara.getFieldString("YD_EQP_ID")); //야드설비ID
				ydEqpStat	= commUtils.trim(recInPara.getFieldString("YD_EQP_STAT")); 
				
			}else{
				szYD_EQP_ID = ydEqpId; 
			}
			//-------------------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------------------
    		//크레인작업지시 호출
        	//-------------------------------------------------------------------------------------------------------------
			//야드설비상태가 대기이면 내부크레인작업지시요구 전송
			if ("W".equals(ydEqpStat)) {
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name

				jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ001"               ); //JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_EQP_ID"         , szYD_EQP_ID              ); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태
				
				jrRtn = commUtils.addSndData(jrYdMsg);
			}

			commUtils.printLog(logId, "[스케쥴메인종료]", "SL");
			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
     * 오퍼레이션명 : TO 위치 결정
     * @param  inRecord, recGetCrnWrkMtl, rsResultCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int LocSrcRngDataSet (String logId, String methodNms, JDTORecord inRecord)throws JDTOException{
    	String methodNm = "TO 위치 결정[BtYsSchSeEJB.LocSrcRngDataSet] < " + methodNms;
    	JDTORecordSet rsCrnsch    		= JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsCrnwrkmtl 		= JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecord recWbook      		= null;
    	JDTORecord recCrnSch      		= null;
    	JDTORecord recInTemp 			= null;
    	JDTORecordSet rsTemp 			= null;

    	String szMsg        			= "";     	  
    	
    	String szCrnSchId 			= "";
    	String szSchCd    			= "";
    	String szToLocDcsnMtd 		= "";
//    	String szToRtnMsg           = "N";
		String szWbookId  			= "";
		String szEqpId    			= "";
    	int intRtnVal 				= 0 ;
    	String szRtnMsg = ""; 
    	String szYS_DN_WO_LOC_NEW ="";
    	
		String szYS_STK_COL_GP	= "";
		String szYS_STK_BED_NO	= "";
		String szYS_STK_LYR_NO	= "";
		String szYD_AID_WRK_UPDN_GP = "";		
		String szCRN_YD_TO_LOC_GUIDE = "";
		
        try{
        	commUtils.printLog(logId, methodNm, "S+");
        	//-------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------
			//파라미터 Null Check
        	szWbookId 	= commUtils.trim(inRecord.getFieldString("YD_WBOOK_ID" ));	
        	szEqpId 	= commUtils.trim(inRecord.getFieldString("YD_EQP_ID"   ));	
        			
			//-------------------------------------------------------------------------------------------------------------
			
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
				szMsg = "["+methodNm+"] getYdWrkbook data not found";
				commUtils.printLog(logId, "[스케쥴메인종료]", "SL");
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
			      , (SELECT YD_STKBED_USG_CD FROM TB_YS_STKCOL WHERE YS_STK_COL_GP = SUBSTR(B.YS_UP_WO_LOC,1,6)) AS BED_USG_CD          
			  FROM TB_YS_EQP    A                                               
			      ,TB_YS_CRNSCH B                                               
			 WHERE B.YD_EQP_ID   = A.YD_EQP_ID                                  
			   AND B.YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND B.YD_EQP_ID   = :V_YD_EQP_ID                         
			   AND (B.DEL_YN IS NULL OR B.DEL_YN <> 'Y')                        
			 ORDER BY B.YD_CRN_SCH_ID   
			 */
			
			rsCrnsch = commDao.select(inRecord, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnschByEqpIdandWBookId", logId, methodNm, "크레인스케줄 조회"); 
	    	
			
			szMsg = "작업예약 ID로 크레인스케줄 조회 스케줄의 횟수 : " + rsCrnsch.size();
			commUtils.printLog(logId, szMsg, "SL");
			//-------------------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄의 권하지시위치 결정
			//-------------------------------------------------------------------------------------------------------------
		    for(int Loop_i = 1; Loop_i <= rsCrnsch.size(); Loop_i++) {

        		rsCrnsch.absolute(Loop_i);
        		recCrnSch  = rsCrnsch.getRecord();
        		
        		//크레인스케줄Data저장
        		szCrnSchId     		= commUtils.trim(recCrnSch.getFieldString("YD_CRN_SCH_ID"));
        		szSchCd        		= commUtils.trim(recCrnSch.getFieldString("YD_SCH_CD"));
        		szToLocDcsnMtd 		= commUtils.trim(recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"));
        		szYD_AID_WRK_UPDN_GP= commUtils.trim(recCrnSch.getFieldString("YD_AID_WRK_UPDN_GP"  ));		
        		szCRN_YD_TO_LOC_GUIDE	= commUtils.trim(recCrnSch.getFieldString("YD_TO_LOC_GUIDE"  ));
        		
        		szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]에 대한 권하지시위치 결정 ";
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
    				szMsg = "LocSrcRngDataSet : 위치검색범위 조회 Data Setting 실패!!";
    				commUtils.printLog(logId, szMsg, "SL");
        		}

    			recWbook = JDTORecordFactory.getInstance().create();
    			recWbook.setRecord(recInTemp);
       		
        		//보조작업인 경우
            	if(szToLocDcsnMtd.equals("W")) {
            		/**********************************************************
    				* 보조작업인 경우 TO위치 결정 (야드로...)
    				**********************************************************/            		
            		szMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]은 보조작업 스케줄의  To위치 결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
        			
        			szRtnMsg = this.procDummyToLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
    				
    				if( szRtnMsg.equals(YsConstant.RETN_CD_SUCCESS) ) {
    					szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]은 보조작업 스케줄의  To위치 결정 성공";
            			commUtils.printLog(logId, szMsg, "SL");
    				}else{
    					szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]은 보조작업 스케줄의  To위치 결정 실패!!";
            			commUtils.printLog(logId, szMsg, "SL");
    				}
    				
    				szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]은 보조작업 스케줄의 To위치 결정 완료 - 메세지 : " + szRtnMsg;
        			commUtils.printLog(logId, szMsg, "SL");
               
            	} else if(szToLocDcsnMtd.equals("C")) {
            		/**********************************************************
    				* 분리작업
    				**********************************************************/            		
            		szMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]은 분리작업 스케줄의  To위치 결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
        			
        			if( szSchCd.substring(2,4).equals("TZ")) {
                		/**********************************************************
        				* 장입 분리작업
        				**********************************************************/            		
            			if(szYD_AID_WRK_UPDN_GP.equals("1")){ 			// 원위치 -> 임시 
            				
            				szRtnMsg = this.procSprToLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook, szYD_AID_WRK_UPDN_GP );
        				
            			} else if(szYD_AID_WRK_UPDN_GP.equals("2")){ 	// 임시 -> 장입대
            				
            				szRtnMsg = this.procSprToLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook, szYD_AID_WRK_UPDN_GP );
        				
            			} else if(szYD_AID_WRK_UPDN_GP.equals("3")){ 	// 임시 -> 원위치
            				
            				szRtnMsg = this.procSprToLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook, szYD_AID_WRK_UPDN_GP );
            				
            				if(szRtnMsg.equals("0")) {
            					
            					recWbook.setField("YD_TO_LOC_GUIDE", "");
            					szRtnMsg = this.procMainWrkLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
            					
            				}
        				}    
            			
           				szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 분리작업 [TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 완료 - 메세지 : " + szRtnMsg;
        				commUtils.printLog(logId, szMsg, "SL");
            			
        			} else {
                		/**********************************************************
        				* 일반 분리작업
        				**********************************************************/            		
            			if(szYD_AID_WRK_UPDN_GP.equals("1")){ // 원위치 -> 임시 
            				
            				szRtnMsg = this.procSprToLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook, szYD_AID_WRK_UPDN_GP );
        				
            			} else if(szYD_AID_WRK_UPDN_GP.equals("2")){ // 임시 -> TO_위치
            				
            				szRtnMsg = this.procMainWrkLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
        				
            			} else if(szYD_AID_WRK_UPDN_GP.equals("3")){ // 임시 -> 원위치
            				
            				szRtnMsg = this.procSprToLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook, szYD_AID_WRK_UPDN_GP);
            				            				
            				if(szRtnMsg.equals("0")) {
            					
            					recWbook.setField("YD_TO_LOC_GUIDE", "");
            					szRtnMsg = this.procMainWrkLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
            					
            				}            				
            				
        				}      				
        				szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 분리작업 [TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 완료 - 메세지 : " + szRtnMsg;
        				commUtils.printLog(logId, szMsg, "SL");
        			}
           		
            	} else {
  
            		if( szSchCd.substring(2,4).equals("TR") && (szSchCd.substring(6,7).equals("U")||szSchCd.substring(6,7).equals("L")) ) {  
                		/**********************************************************************
        				* 차량출고 : 차량이 정지한 적치열 조회 ==> TO위치가 됨  ==> TO 위치 가이드에 등록 처리
        				***********************************************************************/            		

            			
            			szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량출고 주작업[TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 시작";
            			commUtils.printLog(logId, szMsg, "SL");

            			String szYD_CAR_USE_GP	= commUtils.trim(recWbook.getFieldString("YD_CAR_USE_GP"));	//차량사용구분
	    				String szTRN_EQP_CD		= commUtils.trim(recWbook.getFieldString("TRN_EQP_CD"));	//운송장비코드
	    				String szCAR_NO			= commUtils.trim(recWbook.getFieldString("CAR_NO"));		//차량번호
	    				
	    				szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량상차작업예약["+szWbookId+"]의 차량정보[차량사용구분:"+szYD_CAR_USE_GP+", 운송장비코드:"+szTRN_EQP_CD+", 차량번호:"+szCAR_NO+"]에 대한 적치베드 조회 시작";
	    				commUtils.printLog(logId, szMsg, "SL");
	    				
	    				JDTORecordSet rsBed	= JDTORecordFactory.getInstance().createRecordSet("");;
	    				recInPara = JDTORecordFactory.getInstance().create();
	    				
	    				if( szYD_CAR_USE_GP.equals("L") ) {				//구내운송
	    					
	    					recInPara.setField("YD_CRN_SCH_ID"	, 	szCrnSchId);
	    					recInPara.setField("YD_WBOOK_ID"	, 	szWbookId);
	    					recInPara.setField("TRN_EQP_CD"		, 	szTRN_EQP_CD);
		    				recInPara.setField("YD_CAR_USE_GP"	, 	szYD_CAR_USE_GP);
		    				
		    				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedByCarUseGpandTrnEqpCdBt
		    				WITH TEMP_TABLE AS ( 
							    SELECT -- 쪼가리 빌렛이 하단에 실리지 않도록 하는 쿼리
							        CASE WHEN B.CRN_CNT >= 3 THEN 
							                   CASE WHEN A.MTL_CNT >= 6 THEN 'N' ELSE 'Y' END
							             ELSE 'N' 
							        END AS LYR_YN
							      FROM
							          (
							            SELECT COUNT(*) AS MTL_CNT
							              FROM TB_YS_CRNSCH A
							                  ,TB_YS_CRNWRKMTL B
							             WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
							               AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
							          )A,  
							          (
							            SELECT COUNT(*) AS CRN_CNT
							              FROM TB_YS_CRNSCH 
							             WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID 
							               AND DEL_YN = 'N'
							          )B
							)
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
							      ,TEMP_TABLE TT    
							 WHERE SB.YS_STK_COL_GP = SL.YS_STK_COL_GP
							   AND SB.YS_STK_BED_NO = SL.YS_STK_BED_NO
							   AND SB.YS_STK_COL_GP = BL.YS_STK_COL_GP
							   AND SB.DEL_YN        = 'N'
							   AND SB.YD_STK_BED_ACT_STAT = 'L' --적치가능
							   AND SL.YS_STK_LYR_NO IN (DECODE(TT.LYR_YN,'Y','02','01'),'02') 
							 GROUP BY SB.YS_STK_COL_GP, SL.YS_STK_LYR_NO, SB.YS_STK_BED_NO  
							 ORDER BY SB.YS_STK_COL_GP, SL.YS_STK_LYR_NO, SB.YS_STK_BED_NO 
							 )
							WHERE YD_MTL_SH = 0
							  AND ROWNUM = 1
		    				*/   
		    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedByCarUseGpandTrnEqpCdBt", logId, methodNm, "차량BED 조회"); 
		            		
		            		if(rsBed.size() <= 0) {
		        				szMsg = "LocSrcRngDataSet : 구내운송차량 READ 실패!";
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
		    				
		    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedByCarUseGpandCarNoBl", logId, methodNm, "출하차량 BED 조회"); 
		            		
		            		if(rsBed.size() <= 0) {
		        				szMsg = "LocSrcRngDataSet : 출하차량 READ 실패!";
		        				commUtils.printLog(logId, szMsg, "SL");
		            		} else {
		            			szRtnMsg = YsConstant.RETN_CD_SUCCESS;
		            		}
		    				
	    				}
	    				
	    				
	    				if( szRtnMsg.equals(YsConstant.RETN_CD_SUCCESS) ) {
	    					rsBed.first();
	    					recInPara = rsBed.getRecord();
	    					szYS_STK_COL_GP	= commUtils.trim(recInPara.getFieldString("YS_STK_COL_GP"));//차량정지위치 적치열
	    					szYS_STK_BED_NO	= commUtils.trim(recInPara.getFieldString("YS_STK_BED_NO"));//차량정지위치 적치베드
	    					szYS_STK_LYR_NO	= commUtils.trim(recInPara.getFieldString("YS_STK_LYR_NO"));//차량정지위치 적치단
	    					
	    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_STK_COL_GP + szYS_STK_BED_NO + szYS_STK_LYR_NO);
	    					
	    				}else{
		    				
	    					recWbook.setField("YD_TO_LOC_GUIDE", "");
	    				}
	    				
	    				//-----------------------------------------------------------------------------------------------
        				this.procMainWrkLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
        				
        				szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량출고 주작업[TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 완료 - 메세지 : " + szRtnMsg;
        				commUtils.printLog(logId, szMsg, "SL");
            			
            			//-----------------------------------------------------------------------------------------------
        			} else if( szSchCd.substring(2,4).equals("TZ") && szSchCd.substring(6,7).equals("U") ) {  
                		/**********************************************
                		* 장입 : 장입위치  ==> TO위치가 됨  ==> TO 위치 가이드에 등록 처리
        				******************************************************************************/            		
            			
            			szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 장입 주작업[TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 시작";
            			commUtils.printLog(logId, szMsg, "SL");

	    				JDTORecordSet rsBed	= JDTORecordFactory.getInstance().createRecordSet("");;
	    				recInPara = JDTORecordFactory.getInstance().create();
	    				
    					recInPara.setField("YS_STK_COL_GP", 	szSchCd.substring(0,6));
    					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTcBT 
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
    					 WHERE SB.YS_STK_COL_GP IN(:V_YS_STK_COL_GP,SUBSTR(:V_YS_STK_COL_GP,1,2)||'TZ11')
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
	    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTcBT", logId, methodNm, "장입위치 조회"); 
	            		
	            		if(rsBed.size() <= 0) {
	        				szMsg = "LocSrcRngDataSet : 장입위치 BED READ 실패!";
	        				commUtils.printLog(logId, szMsg, "SL");

	        				recWbook.setField("YD_TO_LOC_GUIDE", "");
	        				
	        				throw new DAOException("빌렛장입위치없슴:윤재광 발견");
	        				
	            		} else {
	    					rsBed.first();
	    					recInPara = rsBed.getRecord();
	    					szYS_STK_COL_GP	= commUtils.trim(recInPara.getFieldString("YS_STK_COL_GP"));
	    					szYS_STK_BED_NO	= commUtils.trim(recInPara.getFieldString("YS_STK_BED_NO"));
	    					szYS_STK_LYR_NO	= commUtils.trim(recInPara.getFieldString("YS_STK_LYR_NO"));
    					
	    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_STK_COL_GP + szYS_STK_BED_NO+szYS_STK_LYR_NO);
	            		}	
	    					
	    				//-----------------------------------------------------------------------------------------------
        				szRtnMsg = this.procMainWrkLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
        				
        				szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 장입  주작업[TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 완료 - 메세지 : " + szRtnMsg;
        				commUtils.printLog(logId, szMsg, "SL");
            			
            			//-----------------------------------------------------------------------------------------------
         			} else if( szSchCd.substring(2,6).equals("LB01") && szSchCd.substring(6,7).equals("L") ) {  
                		/*****************************************************************************
        				* 장입이상재 추출
        				******************************************************************************/            		
            		
            			
            			szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 장입이상재 추출[TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 시작";
            			commUtils.printLog(logId, szMsg, "SL");

	    				JDTORecordSet rsBed	= JDTORecordFactory.getInstance().createRecordSet("");;
	    				recInPara = JDTORecordFactory.getInstance().create();
	    				
    					recInPara.setField("YS_STK_COL_GP", 	szSchCd.substring(0,2)+"TY");
    					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTY
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
    					 WHERE SB.YS_STK_COL_GP LIKE :V_YS_STK_COL_GP ||'%'
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
	    				rsBed = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTY", logId, methodNm, "임시적재위치 BED 조회"); 
	            		
	            		if(rsBed.size() <= 0) {
	        				szMsg = "LocSrcRngDataSet : 임시적재위치 BED READ 실패!";
	        				commUtils.printLog(logId, szMsg, "SL");

	        				recWbook.setField("YD_TO_LOC_GUIDE", "");
	        				
	            		} else {
	    					rsBed.first();
	    					recInPara = rsBed.getRecord();
	    					szYS_STK_COL_GP	= commUtils.trim(recInPara.getFieldString("YS_STK_COL_GP"));
	    					szYS_STK_BED_NO	= commUtils.trim(recInPara.getFieldString("YS_STK_BED_NO"));
	    					szYS_STK_LYR_NO	= commUtils.trim(recInPara.getFieldString("YS_STK_LYR_NO"));
    					
	    					recWbook.setField("YD_TO_LOC_GUIDE", szYS_STK_COL_GP + szYS_STK_BED_NO+szYS_STK_LYR_NO);
	            		}	
	    					
	    				//-----------------------------------------------------------------------------------------------
        				szRtnMsg = this.procMainWrkLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
        				
        				szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 장입이상재 추출  주작업[TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 완료 - 메세지 : " + szRtnMsg;
        				commUtils.printLog(logId, szMsg, "SL");
            			
         			} else if( szSchCd.substring(2,4).equals("TF") && szSchCd.substring(6,7).equals("L") ) { 
        				
          				/*****************************************************************************
        				* 입고시 TO 위치 결정
        				******************************************************************************/      
      				
        				szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 입고주작업[TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 시작";
        				commUtils.printLog(logId, szMsg, "SL");
        				
        				szRtnMsg = this.procMainWrkLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
        				
        				
        				szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 입고주작업[TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 완료 - 메세지 : " + szRtnMsg;
        				commUtils.printLog(logId, szMsg, "SL");
        			} else {
        				
          				/*****************************************************************************
        				* 주작업 TO위치 결정 모듈 호출  (야드로 TO 위치 결정 )
        				******************************************************************************/      
      				
        				szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 주작업[TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 시작";
        				commUtils.printLog(logId, szMsg, "SL");
        				
        				szRtnMsg = this.procMainWrkLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook);
        				
        				if( szRtnMsg.equals(YsConstant.RETN_CD_SUCCESS) ) {
        					szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 주작업[TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 성공";
        					commUtils.printLog(logId, szMsg, "SL");
        				}else{
        					szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 주작업[TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 실패!!";
        					commUtils.printLog(logId, szMsg, "SL");
        				}
        				
        				szMsg = "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "] : 주작업[TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 완료 - 메세지 : " + szRtnMsg;
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
	    				szMsg = "크레인스케줄 To위치 Default값 등록 실패!!";
	    				commUtils.printLog(logId, szMsg, "SL");
					}
				}else{
					szYS_DN_WO_LOC_NEW = commUtils.trim(recInPara.getFieldString("YD_EQP_ID_NEW")); //스판 범위 해당 지정 크레인 호기 
				 
					if(!"".equals(szYS_DN_WO_LOC_NEW)) {
						/*com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtEqpId
						UPDATE TB_YS_CRNSCH
						   SET MODIFIER = :V_MODIFIER
						     , MOD_DDTT = SYSDATE
						     , YD_EQP_ID = :V_YD_EQP_ID_NEW
						 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						   AND DEL_YN         = 'N'
						 */  
						intRtnVal = commDao.update(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtEqpId", logId, methodNm, "크레인스케줄 갱신2");
						if(intRtnVal <= 0){
		    				szMsg = "크레인스케줄 스판 지정 호기변경 실패!!";
		    				commUtils.printLog(logId, szMsg, "SL");
						}
					}
				}
			}
			
		//-------------------------------------------------------------------------------------------------------------
			
        	commUtils.printLog(logId, methodNm, "S-");
			return intRtnVal = 1;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    }//end of LocSrcRngDataSet()
    	
	/**
     * 오퍼레이션명 : 스케줄링 크레인 스케줄 등록
     *  
     * @param  ● vResult, msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int CrnSchIns (String logId, String methodNms ,JDTORecordSet outRecset, JDTORecord jrParamSet) throws JDTOException {
   		String methodNm = "스케줄링 크레인 스케줄 등록[BtYsSchSeEJB.CrnSchIns] < " + methodNms;
		JDTORecord recInCrn    = null;
		JDTORecordSet rsBedLyr = null;
		JDTORecord recBedLyr = null;
		int intRtnVal = 0;
		String szName = "SYSTEM";
		String szMsg = "";
		
		try{
			
			commUtils.printLog(logId, methodNm, "S+");
			
			String szEqpId  		= commUtils.trim(jrParamSet.getFieldString("YD_EQP_ID"  ));
			String szSchCd  		= commUtils.trim(jrParamSet.getFieldString("YD_SCH_CD"  ));
			String szYD_SCH_PRIOR   = commUtils.trim(jrParamSet.getFieldString("YD_SCH_PRIOR"  ));
			String szYD_WBOOK_DT  	= commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_DT"  ));
			commUtils.printParam(logId, outRecset);
			
			for(int i = 1; i <= outRecset.size(); i++) {

				outRecset.absolute(i);
				recInCrn = JDTORecordFactory.getInstance().create();
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
				recInCrn.setField("YD_WBOOK_DT",       			szYD_WBOOK_DT);
				recInCrn.setField("YD_SCH_ST_GP",      			"A");
				recInCrn.setField("YS_UP_WO_LOC",     			recInCrn.getFieldString("YS_STK_COL_GP") + recInCrn.getFieldString("YS_STK_BED_NO"));
				recInCrn.setField("YS_UP_WO_LAYER",   			recInCrn.getFieldString("YS_STK_LYR_NO"));
				
				szMsg = "YD_TO_LOC_DCSN_MTD : " +  recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD");
				commUtils.printLog(logId, szMsg, "SL");
				
				if(commUtils.trim(recInCrn.getFieldString("YS_UP_WO_LOC")).equals("")){
					szMsg = "권상지시위치가 없습니다.";
					throw new JDTOException(szMsg);
				}
				
				recInCrn.setField("YD_WRK_PROG_STAT", "W");
				// 권상위치 재료정보
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrBt 
				SELECT * FROM
				(
				SELECT A.YS_STK_COL_GP   
				     , A.YS_STK_BED_NO   
				     , A.YS_STK_LYR_NO   
				     , A.YS_STK_SEQ_NO   
				     , A.SSTL_NO         
				     , B.YD_STK_LOT_TP
				     , B.YD_STK_LOT_CD
				     , B.HCR_GP
				     , B.STL_PROG_CD
				     , B.YS_MTL_ITEM
				  FROM TB_YS_STKLYR A
				     , TB_YS_STOCK B 
				 WHERE A.SSTL_NO = B.SSTL_NO
				   AND A.YS_STK_COL_GP = :V_YS_STK_COL_GP
				   AND A.YS_STK_BED_NO = :V_YS_STK_BED_NO
				   AND A.YS_STK_LYR_NO = :V_YS_STK_LYR_NO
				   AND A.SSTL_NO IS NOT NULL 
				   AND A.DEL_YN = 'N'
				   AND NVL(:V_WRK_SPR,'ALL') = 'ALL' 
				UNION ALL   
				SELECT A.YS_STK_COL_GP   
				     , A.YS_STK_BED_NO   
				     , A.YS_STK_LYR_NO   
				     , A.YS_STK_SEQ_NO   
				     , A.SSTL_NO         
				     , B.YD_STK_LOT_TP
				     , B.YD_STK_LOT_CD
				     , B.HCR_GP
				     , B.STL_PROG_CD
				     , B.YS_MTL_ITEM
				  FROM TB_YS_STKLYR A
				     , TB_YS_STOCK B 
				 WHERE A.SSTL_NO = B.SSTL_NO
				   AND A.YS_STK_COL_GP = :V_YS_STK_COL_GP
				   AND A.YS_STK_BED_NO = :V_YS_STK_BED_NO
				   AND A.YS_STK_LYR_NO = :V_YS_STK_LYR_NO
				   AND A.SSTL_NO IS NOT NULL 
				   AND A.DEL_YN = 'N'
				   AND :V_WRK_SPR = 'TARGET' 
				   AND A.SSTL_NO IN (SELECT SSTL_NO FROM TB_YS_WRKBOOKMTL                          
						              WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID  
					  	                AND DEL_YN = 'N'
				                        AND ((SUBSTR(YS_STK_COL_GP,1,4) = 'BBTC' AND YD_UP_COLL_SEQ = '99') 
				                          OR (SUBSTR(YS_STK_COL_GP,1,4) <> 'BBTC'))) 
				UNION ALL   
				SELECT A.YS_STK_COL_GP  
				     , A.YS_STK_BED_NO  
				     , A.YS_STK_LYR_NO  
				     , A.YS_STK_SEQ_NO  
				     , A.SSTL_NO            
				     , B.YD_STK_LOT_TP
				     , B.YD_STK_LOT_CD
				     , B.HCR_GP
				     , B.STL_PROG_CD
				     , B.YS_MTL_ITEM
				  FROM TB_YS_STKLYR A
				     , TB_YS_STOCK B 
				 WHERE A.SSTL_NO = B.SSTL_NO
				   AND A.YS_STK_COL_GP = :V_YS_STK_COL_GP
				   AND A.YS_STK_BED_NO = :V_YS_STK_BED_NO
				   AND A.YS_STK_LYR_NO = :V_YS_STK_LYR_NO
				   AND A.SSTL_NO IS NOT NULL 
				   AND A.DEL_YN = 'N'
				   AND :V_WRK_SPR = 'DUMMY' 
				   AND A.SSTL_NO NOT IN (SELECT SSTL_NO FROM TB_YS_WRKBOOKMTL                          
					 	                  WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID  
						                    AND DEL_YN = 'N'
				                            AND ((SUBSTR(YS_STK_COL_GP,1,4) = 'BBTC' AND YD_UP_COLL_SEQ = '99') 
				                             OR (SUBSTR(YS_STK_COL_GP,1,4) <> 'BBTC')) )
				)                             
				ORDER BY YS_STK_COL_GP , YS_STK_BED_NO , YS_STK_LYR_NO , YS_STK_SEQ_NO                             
				   */
				
				rsBedLyr  = JDTORecordFactory.getInstance().createRecordSet("");
				

				rsBedLyr = commDao.select(recInCrn, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrBt", logId, methodNm, "크레인스케줄재료정보 조회");
				if (rsBedLyr.size() <= 0) {
					throw new Exception("오류:크레인작업재료적재위치 >> 조회 Data 없음");
				}			
				
				/**********************************************************
				*  분리작업TO위치결정 작업에 필요함
				**********************************************************/
				if(recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("C")){
					
					if(commUtils.trim(recInCrn.getFieldString("TARGET_LOC")).equals("TY")){
						recInCrn.setField("YD_TO_LOC_GUIDE",     recInCrn.getFieldString("YS_STK_COL_GP").substring(0,2) + "TY01");
					} else if(commUtils.trim(recInCrn.getFieldString("TARGET_LOC")).equals("TZ")){
						recInCrn.setField("YD_TO_LOC_GUIDE",     recInCrn.getFieldString("YS_STK_COL_GP").substring(0,2) + "TZ01");
					} else if(commUtils.trim(recInCrn.getFieldString("TARGET_LOC")).equals("YD")){
						recInCrn.setField("YD_TO_LOC_GUIDE",     "");
					} else {
						recInCrn.setField("YD_TO_LOC_GUIDE",     "");
					}
					
					
					if(recInCrn.getFieldString("WRK_SPR").equals("ALL")) {
						recInCrn.setField("YD_AID_WRK_UPDN_GP","1" );  //야드보조작업상하구분
	
					} else if(recInCrn.getFieldString("WRK_SPR").equals("TARGET")) {
						// 가상의 적재위치 SET
						recInCrn.setField("YS_UP_WO_LOC",     recInCrn.getFieldString("YS_STK_COL_GP").substring(0,2) + "TYXXXX" );
						recInCrn.setField("YS_UP_WO_LAYER",   "01");
						
						recInCrn.setField("YD_AID_WRK_UPDN_GP","2" ); 
					} else { // DUMMY
						// 권상위치가 TO위치 결정시 권하 위치가 됨
						// 권상위치는 if(recInCrn.getFieldString("WRK_SPR").equals("ALL")) 이 돌면 UPDATE 됨
						// 가상의 적재위치 SET
						recInCrn.setField("YS_UP_WO_LOC",     recInCrn.getFieldString("YS_STK_COL_GP").substring(0,2) + "TYXXXX" );
						recInCrn.setField("YS_UP_WO_LAYER",   "01");
	
						recInCrn.setField("YD_AID_WRK_UPDN_GP","3" ); 
					}
				}
	
				recInCrn.setField("YD_EQP_WRK_SH", "" + rsBedLyr.size());  //재료매수

				intRtnVal = commDao.insert(recInCrn, "com.inisteel.cim.ys.common.dao.YsCommDAO.insYdCrnsch", logId, methodNm, "TB_YS_CRNSCH 생성");
				if(intRtnVal < 1) {
					szMsg = "크레인 스케줄 등록중  Error!! ErrorCode: " + intRtnVal;
					commUtils.printLog(logId, szMsg, "SL");
				}

				/**********************************************************
				*  적치단의 재료상태를 권상대기로 변경
				**********************************************************/			
				if((recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("C")) && ((recInCrn.getFieldString("WRK_SPR").equals("TARGET"))
                                                                                  ||(recInCrn.getFieldString("WRK_SPR").equals("DUMMY"))) ){  // 연속작업


				} else {
					recBedLyr = JDTORecordFactory.getInstance().create();
					for(int Loop_k = 1; Loop_k <= rsBedLyr.size(); Loop_k++) {
						
						rsBedLyr.absolute(Loop_k);
						recBedLyr.setRecord( rsBedLyr.getRecord() );	
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
				for(int Loop_k = 1; Loop_k <= rsBedLyr.size(); Loop_k++) {
					
					rsBedLyr.absolute(Loop_k);
					
					recBedLyr  = rsBedLyr.getRecord();
					recInCrnMtl.setField("SSTL_NO"			,  commUtils.trim(recBedLyr.getFieldString("SSTL_NO"  )));
					recInCrnMtl.setField("YS_STK_LYR_NO"	,  "01");
					recInCrnMtl.setField("YS_STK_SEQ_NO"	,  ""+Loop_k);
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
			}
			
			commUtils.printLog(logId, methodNm, "S-");
			return intRtnVal = 1;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
        
    }//end of CrnSchIns()
	
	
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
		String methodNm = "크레인스케줄 작업예약재료 수정[BtYsSchSeEJB.updCrnSchWB] < " + jrParam.getResultMsg();
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
			
			BtYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updWmStrLoc", logId, methodNm, "작업예약재료 저장위치 수정");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 분리작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procSprToLoc(String logId, String methodNms, JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recWbook, String szCASE_GP) throws JDTOException {
    	String methodNm = "빌렛 분리작업TO위치결정[BtYsSchSeEJB.procSprToLoc] < " + methodNms;
		String szLogMsg					= null;
		JDTORecord		recCrnwrkmtl		= null;
		commUtils.printParam(logId, recCrnSch);	
		JDTORecordSet rsBed	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recInBed = JDTORecordFactory.getInstance().create();
		
		String szYS_DN_WO_LOC					= null;
		String szYS_DN_WO_LAYER					= null;
		String szYD_STKBED_USG_CD				= null;
		
		String szRtnMsg = "";
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		rsCrnwrkmtl.first();
		recCrnwrkmtl = rsCrnwrkmtl.getRecord();
		
		String szYD_CRN_SCH_ID  	= commUtils.trim(recCrnwrkmtl.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
		String szYD_EQP_ID  		= commUtils.trim(recCrnSch.getFieldString("YD_EQP_ID"  ));		//크레인설비ID
		String szYS_UP_WO_LOC 		= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LOC"  ));		
		String szYS_UP_WO_LAYER 	= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LAYER"  ));		
		String szYD_AID_WRK_UPDN_GP = commUtils.trim(recCrnSch.getFieldString("YD_AID_WRK_UPDN_GP"  ));		
		String szYD_TO_LOC_DCSN_MTD = commUtils.trim(recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"  ));		
		String szCRN_YD_TO_LOC_GUIDE= commUtils.trim(recCrnSch.getFieldString("YD_TO_LOC_GUIDE"  ));	
		
		String szYD_SCH_CD 			= commUtils.trim(recWbook.getFieldString("YD_SCH_CD"  ));			//크레인스케줄코드
		String szYD_WBOOK_ID		= commUtils.trim(recWbook.getFieldString("YD_WBOOK_ID"  ));		
//11.20		
		String szYD_CHG_NO  		= commUtils.trim(recCrnwrkmtl.getFieldString("YD_CHG_NO"  ));		//장입순번
//		int intMTL_CNT 				= Integer.parseInt(commUtils.nvl(recCrnwrkmtl.getFieldString("SH_CNT"),"0"));
		
		// 장입시
//		if( szYD_SCH_CD.substring(2,4).equals("TZ") && szYD_SCH_CD.substring(6,7).equals("U") ) {

		String szYS_STK_COL_GP = "";
		String szYS_STK_BED_NO = "";
		String szYS_STK_LYR_NO = "";
		recInBed= JDTORecordFactory.getInstance().create();
		
		// 임시 적치대로 
		szLogMsg = "빌렛  분리작업TO위치결정[BtYsSchSeEJB.procSprToLoc] : !" + szYD_AID_WRK_UPDN_GP + ">> CASE_GP : "+szCASE_GP;
		commUtils.printLog(logId, szLogMsg, "SL");
		//----------------------------------------------------------------------------------------------------------------------
		//	권하위치 SEARCH
		//----------------------------------------------------------------------------------------------------------------------
		
		if(szCASE_GP.endsWith("1")) {
			//----------------------------------------------------------------------------------------------------------------------
			//	------------->> 임시 적치대 검색 chito 2016.10
			//----------------------------------------------------------------------------------------------------------------------
			
			if(szCRN_YD_TO_LOC_GUIDE.length() > 6){
				if("TY".equals(szCRN_YD_TO_LOC_GUIDE.substring(2 , 4))){
					recInBed.setField("YD_TO_LOC_GUIDE", 		szCRN_YD_TO_LOC_GUIDE);
				}else{
					recInBed.setField("YD_TO_LOC_GUIDE", 		szYD_EQP_ID.substring(0, 2)+ "TY");	
				}
			}else{
				recInBed.setField("YD_TO_LOC_GUIDE", 		szYD_EQP_ID.substring(0, 2)+ "TY");	
			}	
			
			recInBed.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
			recInBed.setField("YD_UP_STK_LOC", 		szYD_SCH_CD);
			
			szLogMsg = "["+ methodNm +"] TOSQL:["+szYD_CRN_SCH_ID+ "]  분리작업TO위치결정 적재위치 가이드  YD_TO_LOC_GUIDE ["+szYD_EQP_ID.substring(0, 2)+ "TY"+"]의 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			rsBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLocGuideTY", logId, methodNm, "가이드  베드 조회");
			if (rsBed.size() <= 0) {
				szLogMsg = "이적 BED 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YsConstant.RETN_CD_FAILURE;
			}	
			rsBed.absolute(1);
			recInBed = rsBed.getRecord();
			szYS_STK_COL_GP = commUtils.trim(recInBed.getFieldString("YS_STK_COL_GP"  ));		//권하지시위치 TEMP
			szYS_STK_BED_NO = commUtils.trim(recInBed.getFieldString("YS_STK_BED_NO"  ));		//권하지시위치 TEMP
			szYS_STK_LYR_NO = commUtils.trim(recInBed.getFieldString("YS_STK_LYR_NO"  ));		//권하지시위치 TEMP
			
			szYS_DN_WO_LOC		= szYS_STK_COL_GP + szYS_STK_BED_NO;
			szYS_DN_WO_LAYER 	= szYS_STK_LYR_NO;

			if( szYS_DN_WO_LAYER.equals("00") ) {							//값이 없으면
				szYS_DN_WO_LAYER = "01";										 //1단
			}else{														     //값 이 존재하면
				szYS_DN_WO_LAYER = commUtils.stringPlusInt(szYS_STK_LYR_NO, 1);	//조회된 적치단 + 1
			}
			
		} else if(szCASE_GP.endsWith("2")) {
			//----------------------------------------------------------------------------------------------------------------------
			//	------------->> 장임대 검색
			//----------------------------------------------------------------------------------------------------------------------

			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchAll
			SELECT A.YD_CRN_SCH_ID
			     , A.YD_EQP_ID
			     , A.YD_SCH_CD
			     , B.SSTL_NO
			     , A.YS_UP_WO_LOC
			     , A.YS_UP_WO_LAYER
			  FROM TB_YS_CRNSCH A
			     , TB_YS_CRNWRKMTL B
			 WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
			   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID

			*/
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecord recTemp = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
			
			JDTORecordSet  rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchAll", logId, methodNm, "적재위치조회 조회");
			if (rsResult.size() <= 0) {
				return "0";
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szYS_UP_WO_LOC 			= recTemp.getFieldString("YS_UP_WO_LOC");
			szYS_UP_WO_LAYER 		= recTemp.getFieldString("YS_UP_WO_LAYER");
			
			szLogMsg = "["+ methodNm +"] 조회된 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			recInBed.setField("YS_STK_COL_GP", 	szYD_SCH_CD.substring(0,6));
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
			 WHERE SB.YS_STK_COL_GP LIKE :V_YS_STK_COL_GP
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
			rsBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTc", logId, methodNm, "장입위치 BED 조회"); 
    		
    		if(rsBed.size() <= 0) {
    			szLogMsg = "LocSrcRngDataSet : 장입위치 BED READ 실패!";
				commUtils.printLog(logId, szLogMsg, "SL");

				return YsConstant.RETN_CD_FAILURE;
				
    		} else {
				rsBed.first();
				recInBed = rsBed.getRecord();
				szYS_STK_COL_GP	= commUtils.trim(recInBed.getFieldString("YS_STK_COL_GP"));
				szYS_STK_BED_NO	= commUtils.trim(recInBed.getFieldString("YS_STK_BED_NO"));
				szYS_STK_LYR_NO	= commUtils.trim(recInBed.getFieldString("YS_STK_LYR_NO"));
    		}	
			
    		szYS_DN_WO_LOC 		= szYS_STK_COL_GP + szYS_STK_BED_NO;
			szYS_DN_WO_LAYER 	= szYS_STK_LYR_NO;
			
			//-----------------------------------------------------------------------------------------------
			commUtils.printLog(logId, "분리 작업 장입 주작업 To위치 결정 완료결과:" + szRtnMsg, "SL");
			
		} else if(szCASE_GP.endsWith("3")) {
			//----------------------------------------------------------------------------------------------------------------------
			//	------------->> 원위치 검색
			//----------------------------------------------------------------------------------------------------------------------

			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchAll
			SELECT A.YD_CRN_SCH_ID
			     , A.YD_EQP_ID
			     , A.YD_SCH_CD
			     , B.SSTL_NO
			     , A.YS_UP_WO_LOC
			     , A.YS_UP_WO_LAYER
			  FROM TB_YS_CRNSCH A
			     , TB_YS_CRNWRKMTL B
			 WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
			   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID

			*/
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecord recTemp = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
			
			JDTORecordSet  rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchAll", logId, methodNm, "적재위치조회 조회");
			if (rsResult.size() <= 0) {
				return "0";
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szYS_UP_WO_LOC 			= recTemp.getFieldString("YS_UP_WO_LOC");
			szYS_UP_WO_LAYER 		= recTemp.getFieldString("YS_UP_WO_LAYER");
			
			szLogMsg = "["+ methodNm +"] 조회된 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			//-------------------------------------------------------------------------------------------------------------
    		// (1)인 분리 작업 권상위치
        	//-------------------------------------------------------------------------------------------------------------
    		recInBed.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
    		recInBed.setField("YD_EQP_ID",   szYD_EQP_ID);
    		rsBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnschByEqpIdandWBookId", logId, methodNm, "크레인스케줄 조회");   		
    		
    		for(int Loop_i = 1; Loop_i <= rsBed.size(); Loop_i++) {
    			rsBed.absolute(Loop_i);
				recInBed = JDTORecordFactory.getInstance().create();
				recInBed.setRecord(rsBed.getRecord());
				
				if(commUtils.trim(recInBed.getFieldString("YD_AID_WRK_UPDN_GP")).equals("1")) {
				
					szYS_DN_WO_LOC 	 	= commUtils.trim(recInBed.getFieldString("YS_UP_WO_LOC"));
					szYS_DN_WO_LAYER 	= commUtils.trim(recInBed.getFieldString("YS_UP_WO_LAYER"));
					szYD_STKBED_USG_CD 	= commUtils.trim(recInBed.getFieldString("BED_USG_CD"));
				}
			}
		}
// 11.20 추가
		if(szCASE_GP.endsWith("3")) {
			
			commUtils.printLog(logId,  "["+ methodNm +"] 원위치 적치가능여부 확인", "SL");
			
			JDTORecord recTemp1 = JDTORecordFactory.getInstance().create();
			recTemp1.setField("YD_UP_STK_COL_GP"	, szYS_UP_WO_LOC.substring(0,6)); // 권상위치		
			recTemp1.setField("LOC_YS_STK_COL_GP"	, szYS_DN_WO_LOC.substring(0,6)); // 권하위치		
			recTemp1.setField("LOC_YS_STK_BED_NO"	, szYS_DN_WO_LOC.substring(6)  ); // 권하위치BED		
			recTemp1.setField("LOC_YS_STK_LYR_NO"	, szYS_DN_WO_LAYER             ); // 권하위치단	
			recTemp1.setField("YD_STKBED_USG_CD"	, szYD_STKBED_USG_CD);		
			recTemp1.setField("YD_CHG_NO"			, szYD_CHG_NO);		
			recTemp1.setField("YD_TO_LOC_DCSN_MTD"	, szYD_TO_LOC_DCSN_MTD);		
			recTemp1.setField("YD_CHG_NO_CHK_YN"	, "Y");	
			JDTORecord recResult = this.procLocAbleCheckCntUpLoc(logId, methodNms, recTemp1) ;
			//적치불가
			if(commUtils.trim(recResult.getFieldString("ABLE_YN")).equals("N")){
				return "0";
			}		
		}
//END		

			
		JDTORecord RecSetLoc = JDTORecordFactory.getInstance().create();
		RecSetLoc.setField("YD_CRN_SCH_ID", 		szYD_CRN_SCH_ID); 
		RecSetLoc.setField("YD_EQP_ID", 			szYD_EQP_ID);	 
		RecSetLoc.setField("YS_UP_WO_LOC", 			szYS_UP_WO_LOC); 
		RecSetLoc.setField("YS_UP_WO_LAYER",		szYS_UP_WO_LAYER);	 
		RecSetLoc.setField("YS_DN_WO_LOC", 			szYS_DN_WO_LOC);
		RecSetLoc.setField("YS_DN_WO_LAYER", 		szYS_DN_WO_LAYER);

		RecSetLoc.setField("YD_AID_WRK_UPDN_GP", 	szYD_AID_WRK_UPDN_GP);
		RecSetLoc.setField("YD_TO_LOC_DCSN_MTD", 	szYD_TO_LOC_DCSN_MTD);
		RecSetLoc.setField("YD_WBOOK_ID", 			szYD_WBOOK_ID);
		RecSetLoc.setField("YD_SCH_CD", 			szYD_SCH_CD);	 
		
		this.procUpdateLocSpr(logId,methodNms,recCrnwrkmtl  ,RecSetLoc );
		
		commUtils.printLog(logId, methodNm, "S-");	
		//----------------------------------------------------------------------------------------------------------------------
    	// ERROR 발생시 ?
		//----------------------------------------------------------------------------------------------------------------------
		return YsConstant.RETN_CD_SUCCESS;
	}	
	
	
	
	
	/**
	 * 오퍼레이션명 : 크레인 스케줄 GROUPING PARAMETER DATA SETTING
	 *  
	 * @param  String     szEqpId, szSchCd, rsMinWrkBookMtl, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public int CrnSchGrp(String logId, String methodNms ,JDTORecord recMainPara, JDTORecordSet rsReturn)throws JDTOException  {
		String methodNm = "크레인 스케줄 GROUPING[BtYsSchSeEJB.CrnSchGrp] < " + methodNms;
		JDTORecordSet rsSelBed       = JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecord recInPara     = null;
		JDTORecord recPara       = null;
		JDTORecord recStkLyr     = null;
		JDTORecordSet rsResult    = JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsWrkbookCol= JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		int intYdUpCollSeq = 0;
		int intHandlingCnt = 1;

		try {

			//------------------------------------------------------------------------------------------------------------
			//	rsMinWrkBookMtl - 열BED 단위로 최소 단정보 READ
			//------------------------------------------------------------------------------------------------------------

			commUtils.printLog(logId, methodNm, "S+");			

			String szYD_SCH_CD = commUtils.trim(recMainPara.getFieldString("YD_SCH_CD"));
			String szYD_TO_LOC_GUIDE = commUtils.trim(recMainPara.getFieldString("YD_TO_LOC_GUIDE"));
			
			//------------------------------------------------------------------------------------------------------------
			//	열 단위로 적치대 용도코드 READ
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
			
		
			//------------------------------------------------------------------------------------------------------------
			//------------------------------------------------------------------------------------------------------------
			
			for(int Loop_i = 1; Loop_i <= rsWrkbookmtl.size(); Loop_i++) {
				rsWrkbookmtl.absolute(Loop_i);
				recPara = rsWrkbookmtl.getRecord();
				//# 열인 경우 열단위로 처리함
				if("V1".equals(commUtils.trim(recPara.getFieldString("YD_STKBED_USG_CD"))) ) {
					
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("YD_WBOOK_ID"  , commUtils.trim(recPara.getFieldString("YD_WBOOK_ID")));  
					recInPara.setField("YS_STK_COL_GP", commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));

					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpGroup 
					SELECT YD_WBOOK_ID        AS YD_WBOOK_ID
					      ,YS_STK_COL_GP      AS YS_STK_COL_GP
					      ,MIN(YS_STK_LYR_NO) AS YS_STK_LYR_NO
					      ,'V1'               AS YD_STKBED_USG_CD
					  FROM TB_YS_WRKBOOKMTL
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND YS_STK_COL_GP = :V_YS_STK_COL_GP
					   AND DEL_YN='N'
					 GROUP BY YD_WBOOK_ID, YS_STK_COL_GP
					 ORDER BY YS_STK_COL_GP DESC
					 */
					rsWrkbookCol = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpGroup", logId, methodNm, "크레인스케줄재료정보 조회");
					if (rsWrkbookCol.size() <= 0) {
						throw new Exception("오류:크레인작업재료조회 >> 조회 Data 없음");
					}
					for(int Loop_j = 1; Loop_j <= rsWrkbookCol.size(); Loop_j++) {
						rsWrkbookCol.absolute(Loop_j);
						//
						rsSelBed.addRecord(rsWrkbookCol.getRecord());
					}
					
				} else {
					
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("YD_WBOOK_ID"  , commUtils.trim(recPara.getFieldString("YD_WBOOK_ID")));  
					recInPara.setField("YS_STK_COL_GP", commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));

					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpBedGroup
					SELECT YD_WBOOK_ID        AS YD_WBOOK_ID
					      ,YS_STK_COL_GP      AS YS_STK_COL_GP
					      ,YS_STK_BED_NO      AS YS_STK_BED_NO
					      ,MIN(YS_STK_LYR_NO) AS YS_STK_LYR_NO
					      ,'V2'               AS YD_STKBED_USG_CD
					  FROM TB_YS_WRKBOOKMTL
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND YS_STK_COL_GP = :V_YS_STK_COL_GP
					   AND DEL_YN='N'
					 GROUP BY YD_WBOOK_ID, YS_STK_COL_GP, YS_STK_BED_NO
					 ORDER BY YS_STK_COL_GP, YS_STK_BED_NO DESC
					 */
					rsWrkbookCol = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpBedGroup", logId, methodNm, "크레인스케줄재료정보 조회");
					if (rsWrkbookCol.size() <= 0) {
						throw new Exception("오류:크레인작업재료조회 >> 조회 Data 없음");
					}
					for(int Loop_j = 1; Loop_j <= rsWrkbookCol.size(); Loop_j++) {
						rsWrkbookCol.absolute(Loop_j);
						//
						rsSelBed.addRecord(rsWrkbookCol.getRecord());
					}
				}
			}
			commUtils.printParam(logId, rsSelBed);
			//------------------------------------------------------------------------------------------------------------
			// 열별 베드별로  최하단 정보를 조회해서  
			// 각 재료의 레코드에 주작업/보조작업, TO위치결정방법 파라미터 설정
			//------------------------------------------------------------------------------------------------------------
			
			for(int Loop_i = 1; Loop_i <= rsSelBed.size(); Loop_i++) {
				
				rsSelBed.absolute(Loop_i);  //적치Bed를 조회한다.

				recPara = rsSelBed.getRecord();				
				
				
				if (recPara.getFieldString("YD_STKBED_USG_CD").equals("V1")) { //#BED

					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("YD_WBOOK_ID",        recPara.getFieldString("YD_WBOOK_ID"));
					recInPara.setField("YS_STK_COL_GP",      recPara.getFieldString("YS_STK_COL_GP"));
					recInPara.setField("YS_STK_LYR_NO",      recPara.getFieldString("YS_STK_LYR_NO"));  
					
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyrbyWBookIdGroupV1
					SELECT X.SSTL_NO       
					     , X.YS_STK_COL_GP  
					     , X.YS_STK_BED_NO  
					     , X.YS_STK_LYR_NO  
					     , X.YD_UP_COLL_SEQ 
					     , Y.YD_MTL_T       
					     , Y.YD_MTL_W       
					     , Y.YD_MTL_L       
					     , Y.YD_MTL_WT      
					     , Y.YD_STK_LOT_TP  
					     , Y.YD_STK_LOT_CD  
					     , Y.YS_MTL_ITEM    
					     , Y.HCR_GP         
					     , Y.STL_PROG_CD    
					     , X.YD_STK_LYR_MTL_STAT 
					     , X.WRKBOOKMTL_CNT -- 해당단에 작업예약매수
					     , (SELECT COUNT(*)  
					          FROM TB_YS_STKLYR A                                   
					         WHERE A.YS_STK_COL_GP     = X.YS_STK_COL_GP
					           AND A.YS_STK_BED_NO     = X.YS_STK_BED_NO
					           AND A.YS_STK_LYR_NO     = X.YS_STK_LYR_NO
					           AND  A.SSTL_NO IS NOT NULL
					       )  AS LYR_CNT   -- 해당단에 작업재료매수 
					     , (SELECT DECODE(SUM((
					                 SELECT COUNT(*)
					                   FROM TB_YS_STKLYR A
					                      , TB_YS_STOCK B
					                  WHERE A.SSTL_NO = B.SSTL_NO
					                    AND A.YS_STK_COL_GP = A1.YS_STK_COL_GP
					                    AND A.YS_STK_BED_NO = A1.YS_STK_BED_NO
					                    AND A.YS_STK_LYR_NO = A1.YS_STK_LYR_NO
					                    AND A.YS_STK_SEQ_NO > A1.YS_STK_SEQ_NO
					                    AND B.YD_CHG_NO < B1.YD_CHG_NO
					              )),0,'Y','N') AS CHECK_SUM
					         FROM TB_YS_STKLYR A1
					            , TB_YS_STOCK B1
					        WHERE A1.SSTL_NO = B1.SSTL_NO
					          AND A1.YS_STK_COL_GP = X.YS_STK_COL_GP
					          AND A1.YS_STK_BED_NO = X.YS_STK_BED_NO 
					          AND A1.YS_STK_LYR_NO = X.YS_STK_LYR_NO) AS YD_CHG_NO_SEQ_YN -- 장입순번 CEHCK    
					     , CASE WHEN X.YS_STK_LYR_NO = :V_YS_STK_LYR_NO THEN 'Y' ELSE 'N' END AS LOWEST_LYR_NO
					  FROM (SELECT MAX(A.SSTL_NO)        AS SSTL_NO                  
					             , A.YS_STK_COL_GP       AS YS_STK_COL_GP           
					             , A.YS_STK_BED_NO       AS YS_STK_BED_NO           
					             , A.YS_STK_LYR_NO       AS YS_STK_LYR_NO           
					             , MAX(B.YD_UP_COLL_SEQ )      AS YD_UP_COLL_SEQ          
					             , MAX(A.YD_STK_LYR_MTL_STAT)  AS YD_STK_LYR_MTL_STAT     
					             , COUNT(A.SSTL_NO)           AS WRKBOOKMTL_CNT            
					          FROM TB_YS_STKLYR A                                   
					             , (SELECT SSTL_NO                                   
					                     , YD_UP_COLL_SEQ                           
					                     , DEL_YN                                   
					                  FROM TB_YS_WRKBOOKMTL                         
					                 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID ) B     
					         WHERE A.SSTL_NO           = B.SSTL_NO(+)              
					           AND A.YS_STK_COL_GP     = :V_YS_STK_COL_GP
					           AND A.YS_STK_LYR_NO     > :V_YS_STK_LYR_NO
					           AND A.YD_STK_LYR_MTL_STAT = 'C'
					           AND (A.DEL_YN <> 'Y' OR A.DEL_YN IS NULL)            
					           AND (B.DEL_YN <> 'Y' OR B.DEL_YN IS NULL)
					          GROUP BY YS_STK_COL_GP, YS_STK_BED_NO,A.YS_STK_LYR_NO 
					         
					        UNION ALL
					          
					        SELECT MAX(A.SSTL_NO)           AS SSTL_NO                  
					             , A.YS_STK_COL_GP            AS YS_STK_COL_GP           
					             , A.YS_STK_BED_NO            AS YS_STK_BED_NO           
					             , A.YS_STK_LYR_NO            AS YS_STK_LYR_NO           
					             , MAX(B.YD_UP_COLL_SEQ )     AS YD_UP_COLL_SEQ          
					             , MAX(A.YD_STK_LYR_MTL_STAT) AS YD_STK_LYR_MTL_STAT    
					             , COUNT(A.SSTL_NO)           AS WRKBOOKMTL_CNT                  
					          FROM TB_YS_STKLYR A                                   
					             , (SELECT SSTL_NO                                   
					                     , YD_UP_COLL_SEQ                           
					                     , YS_STK_COL_GP                           
					                     , YS_STK_BED_NO                           
					                     , DEL_YN                                   
					                  FROM TB_YS_WRKBOOKMTL                         
					                 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID ) B  
					         WHERE A.SSTL_NO           = B.SSTL_NO(+)              
					           AND A.YS_STK_COL_GP     = B.YS_STK_COL_GP
					           AND A.YS_STK_BED_NO     = B.YS_STK_BED_NO
					           AND A.YS_STK_COL_GP     = :V_YS_STK_COL_GP
					           AND A.YS_STK_LYR_NO     = :V_YS_STK_LYR_NO
					           AND A.YD_STK_LYR_MTL_STAT = 'C'
					           AND (A.DEL_YN <> 'Y' OR A.DEL_YN IS NULL)            
					           AND (B.DEL_YN <> 'Y' OR B.DEL_YN IS NULL)
					          GROUP BY A.YS_STK_COL_GP, A.YS_STK_BED_NO, A.YS_STK_LYR_NO ) X        
					      ,TB_YS_STOCK Y                                            
					 WHERE X.SSTL_NO = Y.SSTL_NO                                      
					   AND (Y.DEL_YN <> 'Y' OR Y.DEL_YN IS NULL)                    
					 ORDER BY X.YS_STK_COL_GP,  X.YS_STK_LYR_NO DESC, X.YS_STK_BED_NO
					 */
	
					rsResult = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyrbyWBookIdGroupV1", logId, methodNm, "BED 정보 조회");
					if (rsResult.size() <= 0) {
						throw new Exception("오류:BED정보조회 >> 조회 Data 없음");
					}
				} else {
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("YD_WBOOK_ID",        recPara.getFieldString("YD_WBOOK_ID"));
					recInPara.setField("YS_STK_COL_GP",      recPara.getFieldString("YS_STK_COL_GP"));
					recInPara.setField("YS_STK_BED_NO",      recPara.getFieldString("YS_STK_BED_NO"));
					recInPara.setField("YS_STK_LYR_NO",      recPara.getFieldString("YS_STK_LYR_NO"));  

					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyrbyWBookIdGroupV2
					SELECT X.SSTL_NO        
					     , X.YS_STK_COL_GP  
					     , X.YS_STK_BED_NO  
					     , X.YS_STK_LYR_NO  
					     , X.YD_UP_COLL_SEQ 
					     , Y.YD_MTL_T       
					     , Y.YD_MTL_W       
					     , Y.YD_MTL_L       
					     , Y.YD_MTL_WT      
					     , Y.YD_STK_LOT_TP  
					     , Y.YD_STK_LOT_CD  
					     , Y.YS_MTL_ITEM    
					     , Y.HCR_GP         
					     , Y.STL_PROG_CD    
					     , X.YD_STK_LYR_MTL_STAT 
					     , (SELECT SUM((SELECT 1  FROM TB_YS_WRKBOOKMTL                          
					                     WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID  
					                       AND SSTL_NO = A.SSTL_NO)) 
					           FROM TB_YS_STKLYR A                                               
					         WHERE A.YS_STK_COL_GP     = X.YS_STK_COL_GP
					           AND A.YS_STK_BED_NO     = X.YS_STK_BED_NO
					           AND A.YS_STK_LYR_NO     = X.YS_STK_LYR_NO
					       )  AS WRKBOOKMTL_CNT -- 해당단에 작업예약매수
					     , (SELECT COUNT(*)  
					          FROM TB_YS_STKLYR A                                   
					         WHERE A.YS_STK_COL_GP     = X.YS_STK_COL_GP
					           AND A.YS_STK_BED_NO     = X.YS_STK_BED_NO
					           AND A.YS_STK_LYR_NO     = X.YS_STK_LYR_NO
					           AND A.SSTL_NO IS NOT NULL
					       )  AS LYR_CNT     -- 해당단에 작업재료매수 
					     , (SELECT DECODE(SUM((
					                 SELECT COUNT(*)
					                   FROM TB_YS_STKLYR A
					                      , TB_YS_STOCK B
					                  WHERE A.SSTL_NO = B.SSTL_NO
					                    AND A.YS_STK_COL_GP = A1.YS_STK_COL_GP
					                    AND A.YS_STK_BED_NO = A1.YS_STK_BED_NO
					                    AND A.YS_STK_LYR_NO = A1.YS_STK_LYR_NO
					                    AND A.YS_STK_SEQ_NO > A1.YS_STK_SEQ_NO
					                    AND B.YD_CHG_NO < B1.YD_CHG_NO
					              )),0,'Y','N') AS CHECK_SUM
					         FROM TB_YS_STKLYR A1
					            , TB_YS_STOCK B1
					        WHERE A1.SSTL_NO = B1.SSTL_NO
					          AND A1.YS_STK_COL_GP = X.YS_STK_COL_GP
					          AND A1.YS_STK_BED_NO = X.YS_STK_BED_NO 
					          AND A1.YS_STK_LYR_NO = X.YS_STK_LYR_NO) AS YD_CHG_NO_SEQ_YN -- 장입순번 CEHCK  
					     , CASE WHEN X.YS_STK_LYR_NO = :V_YS_STK_LYR_NO THEN 'Y' ELSE 'N' END AS LOWEST_LYR_NO  
					  FROM (SELECT MAX(A.SSTL_NO)        AS SSTL_NO                  
					             , A.YS_STK_COL_GP       AS YS_STK_COL_GP           
					             , A.YS_STK_BED_NO       AS YS_STK_BED_NO           
					             , A.YS_STK_LYR_NO       AS YS_STK_LYR_NO           
					             , MAX(B.YD_UP_COLL_SEQ)      AS YD_UP_COLL_SEQ          
					             , MAX(A.YD_STK_LYR_MTL_STAT) AS YD_STK_LYR_MTL_STAT
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
					           AND (B.DEL_YN <> 'Y' OR B.DEL_YN IS NULL)
					         GROUP BY YS_STK_COL_GP, YS_STK_BED_NO,A.YS_STK_LYR_NO) X        
					      ,TB_YS_STOCK Y                                            
					 WHERE X.SSTL_NO = Y.SSTL_NO                                      
					   AND (Y.DEL_YN <> 'Y' OR Y.DEL_YN IS NULL)                    
					 ORDER BY X.YS_STK_COL_GP, X.YS_STK_BED_NO, X.YS_STK_LYR_NO DESC
					 */
	
					rsResult = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyrbyWBookIdGroupV2", logId, methodNm, "BED 정보 조회");
					if (rsResult.size() <= 0) {
						throw new Exception("오류:BED정보조회 >> 조회 Data 없음");
					}					
				}
	    		
				JDTORecord recInPara9 = JDTORecordFactory.getInstance().create();
	    		
				for(int Loop_j = 1; Loop_j <= rsResult.size(); Loop_j++) {
	    			rsResult.absolute(Loop_j);
	    			recStkLyr = rsResult.getRecord();

	    			recStkLyr.setField("YD_WBOOK_ID"	, recPara.getFieldString("YD_WBOOK_ID"));
	    			
	    			String szLOWEST_LYR_NO 	= commUtils.trim(recStkLyr.getFieldString("LOWEST_LYR_NO"));
	    			String szYS_STK_COL_GP 	= commUtils.trim(recStkLyr.getFieldString("YS_STK_COL_GP"));
	    			String szYS_STK_BED_NO 	= commUtils.trim(recStkLyr.getFieldString("YS_STK_BED_NO"));
	    			String szYS_STK_LYR_NO 	= commUtils.trim(recStkLyr.getFieldString("YS_STK_LYR_NO"));
	    			String szYD_STK_LOT_CD 	= commUtils.trim(recStkLyr.getFieldString("YD_STK_LOT_CD"));
	    			String szWRKBOOKMTL_CNT = commUtils.trim(recStkLyr.getFieldString("WRKBOOKMTL_CNT"));
	    			String szCHGNO_SEQ_YN = commUtils.trim(recStkLyr.getFieldString("YD_CHG_NO_SEQ_YN"));
	    			String szBED_CNT 		= ""+Loop_i;
	    			String szYD_WBOOK_ID 	= recPara.getFieldString("YD_WBOOK_ID");
		    		
	    			//주작업여부판단
	    			if(commUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ") > 0) {          //주작업

//---------> 01.12. 
// 권상위치가 TY 인 경우  분리 작업 없이 바로 장입처리 함
	    				
	    				if(  ( szYD_SCH_CD.substring(2,4).equals("TZ") && szYD_SCH_CD.substring(6,7).equals("U") ) 
	    						&& szYS_STK_COL_GP.substring(2, 4).equals("TY") ) {
	    					
	    					recInPara9 = JDTORecordFactory.getInstance().create();
							// 임시BED -> 장입대
	    					recInPara9.setField("LOWEST_LYR_NO"		, szLOWEST_LYR_NO);
	    					recInPara9.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
	    					recInPara9.setField("YS_STK_BED_NO"		, szYS_STK_BED_NO);
	    					recInPara9.setField("YS_STK_LYR_NO"		, szYS_STK_LYR_NO);
	    					recInPara9.setField("YD_STK_LOT_CD"		, szYD_STK_LOT_CD);
	    					recInPara9.setField("WRKBOOKMTL_CNT"	, szWRKBOOKMTL_CNT);
	    					recInPara9.setField("BED_CNT"			, szBED_CNT);
	    					recInPara9.setField("YD_WBOOK_ID"		, szYD_WBOOK_ID);
	    					recInPara9.setField("HANDLING_CNT"		, ""+intHandlingCnt);
							
							recInPara9.setField("MAIN_WRK_YN"		, "Y");
							recInPara9.setField("UP_COLL_BASE"		, "" + (rsResult.size() - intYdUpCollSeq + 1));     	
		    				recInPara9.setField("YD_UP_COLL_SEQ"	, ""+commUtils.paraRecChkNullInt(recInPara9,"YD_UP_COLL_SEQ"));
		    				recInPara9.setField("WRK_SPR"			, "TARGET" );
		    				recInPara9.setField("TARGET_LOC"		, "TZ" );
		    				recInPara9.setField("YD_TO_LOC_DCSN_MTD", "M");
				    		intHandlingCnt++;  		    		//핸들링 카운트 증가
		    				rsReturn.addRecord(recInPara9);
				    		commUtils.printParam(logId, rsReturn);
				    		
	    				} else
//<----- 추가 
	    					if(  ( szYD_SCH_CD.substring(2,4).equals("TZ") && szYD_SCH_CD.substring(6,7).equals("U") ) 
	    						&& ((commUtils.paraRecChkNullInt(recStkLyr,"WRKBOOKMTL_CNT") != commUtils.paraRecChkNullInt(recStkLyr,"LYR_CNT"))
	    							||(szCHGNO_SEQ_YN.equals("N")))  //장입순번 추가	
	    				    ) {
	    					
	    					commUtils.printLog(logId, "분리작업 시작함 ", "S+");		
	    					
	    					commUtils.printLog(logId, "WRKBOOKMTL_CNT" +commUtils.paraRecChkNullInt(recStkLyr,"WRKBOOKMTL_CNT")  , "SL");	
	    					commUtils.printLog(logId, "분리작업 LYR_CNT "+ commUtils.paraRecChkNullInt(recStkLyr,"LYR_CNT"), "SL");	
	    					commUtils.printLog(logId, "szCHGNO_SEQ_YN "+ szCHGNO_SEQ_YN, "SL");	
	    					
	    					if((commUtils.paraRecChkNullInt(recStkLyr,"WRKBOOKMTL_CNT") != commUtils.paraRecChkNullInt(recStkLyr,"LYR_CNT"))) {
	    						/**********************************************
	                    		* 장입 재료 매수 와 단 매수  틀림
	            				******************************************************************************/            		
	    						// 현적치위치 - > 임시BED -> 장입대 -> 현적재위치
		      					// 현적치위치 - > 임시BED 작업
		    					
		      					recInPara9 = JDTORecordFactory.getInstance().create();
		    					recInPara9.setField("LOWEST_LYR_NO"		, szLOWEST_LYR_NO);
		    					recInPara9.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
		    					recInPara9.setField("YS_STK_BED_NO"		, szYS_STK_BED_NO);
		    					recInPara9.setField("YS_STK_LYR_NO"		, szYS_STK_LYR_NO);
		    					recInPara9.setField("YD_STK_LOT_CD"		, szYD_STK_LOT_CD);
		    					recInPara9.setField("WRKBOOKMTL_CNT"	, szWRKBOOKMTL_CNT);
			    				recInPara9.setField("MAIN_WRK_YN"		, "N");
		    					recInPara9.setField("UP_COLL_BASE"		, "" );     	
		    					recInPara9.setField("YD_UP_COLL_SEQ"	, ""+commUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ"));
		    					recInPara9.setField("WRK_SPR"			, "ALL" );
		    					recInPara9.setField("TARGET_LOC"		, "TY" );
		    					recInPara9.setField("YD_TO_LOC_DCSN_MTD", "C");   // 분리 작업
		    					recInPara9.setField("BED_CNT"			, szBED_CNT);
		    					recInPara9.setField("YD_WBOOK_ID"		, szYD_WBOOK_ID);
		    					recInPara9.setField("HANDLING_CNT"		, ""+intHandlingCnt);
		    					intHandlingCnt++;
								rsReturn.addRecord(recInPara9);
								
								
								// 작업 분리
								/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchWrkSpr  
								SELECT 'TARGET' AS GP
								     , '2'      AS SEQ
								     , A.YS_STK_COL_GP
								     , A.YS_STK_BED_NO
								     , A.YS_STK_LYR_NO          
								  FROM TB_YS_STKLYR A
								 WHERE A.YS_STK_COL_GP     = :V_YS_STK_COL_GP 
								   AND A.YS_STK_BED_NO     = :V_YS_STK_BED_NO 
								   AND A.YS_STK_LYR_NO     = :V_YS_STK_LYR_NO
								   AND A.SSTL_NO  IN (SELECT SSTL_NO                                   
								                        FROM TB_YS_WRKBOOKMTL                         
								                       WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID)
								 GROUP BY A.YS_STK_COL_GP,A.YS_STK_BED_NO,A.YS_STK_LYR_NO                                                
								UNION ALL                       
								SELECT 'DUMMY' AS GP
								     , '3'     AS SEQ
								     , A.YS_STK_COL_GP,A.YS_STK_BED_NO,A.YS_STK_LYR_NO          
								  FROM TB_YS_STKLYR A
								 WHERE A.YS_STK_COL_GP     = :V_YS_STK_COL_GP 
								   AND A.YS_STK_BED_NO     = :V_YS_STK_BED_NO 
								   AND A.YS_STK_LYR_NO     = :V_YS_STK_LYR_NO
								   AND A.SSTL_NO NOT IN (SELECT SSTL_NO                                   
								                           FROM TB_YS_WRKBOOKMTL                         
								                          WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID)  
								 GROUP BY A.YS_STK_COL_GP,A.YS_STK_BED_NO,A.YS_STK_LYR_NO                         
								 */
								JDTORecord recWrkbookSpr = JDTORecordFactory.getInstance().create();
								JDTORecordSet rsWrkbookSpr = commDao.select(recStkLyr, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchWrkSpr", logId, methodNm, "작업예약재료분리정보 조회");
								if (rsWrkbookSpr.size() <= 0) {
									throw new Exception("작업예약재료분리정보 조회 >> 조회 Data 없음");
								} else {
									
									String SprGp = "";
									
									
									for(int Loop_k = 1; Loop_k <= rsWrkbookSpr.size(); Loop_k++) {
										rsWrkbookSpr.absolute(Loop_k);
										recWrkbookSpr  = rsWrkbookSpr.getRecord();
										SprGp = commUtils.trim(recWrkbookSpr.getFieldString("GP"));
										
										recInPara9 = JDTORecordFactory.getInstance().create();
										// 장입대 -> 현적재위치
				    					recInPara9.setField("LOWEST_LYR_NO"		, szLOWEST_LYR_NO);
				    					recInPara9.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
				    					recInPara9.setField("YS_STK_BED_NO"		, szYS_STK_BED_NO);
				    					recInPara9.setField("YS_STK_LYR_NO"		, szYS_STK_LYR_NO);
				    					recInPara9.setField("YD_STK_LOT_CD"		, szYD_STK_LOT_CD);
				    					recInPara9.setField("WRKBOOKMTL_CNT"	, szWRKBOOKMTL_CNT);
				    					recInPara9.setField("BED_CNT"			, szBED_CNT);
				    					recInPara9.setField("YD_WBOOK_ID"		, szYD_WBOOK_ID);
				    					recInPara9.setField("HANDLING_CNT"		, ""+intHandlingCnt);
										
							    		intHandlingCnt++;  		    		//핸들링 카운트 증가
							    		
	
										if(SprGp.equals("TARGET")){
											// 임시BED -> 장입대
											recInPara9.setField("MAIN_WRK_YN"		, "Y");
											recInPara9.setField("UP_COLL_BASE"		, "" + (rsResult.size() - intYdUpCollSeq + 1));     	
						    				recInPara9.setField("YD_UP_COLL_SEQ"	, ""+commUtils.paraRecChkNullInt(recInPara9,"YD_UP_COLL_SEQ"));
						    				recInPara9.setField("WRK_SPR"			, SprGp );
						    				recInPara9.setField("YD_TO_LOC_DCSN_MTD", "C");
						    				recInPara9.setField("TARGET_LOC"		, "TZ" );
											rsReturn.addRecord(recInPara9);
								    		commUtils.printParam(logId, rsReturn);
						    			} else { 
											if(!szYS_STK_COL_GP.substring(2, 4).equals("TY")){
							    				// 장입대 -> 현적재위치
												recInPara9.setField("MAIN_WRK_YN"		, "N");
												recInPara9.setField("UP_COLL_BASE"		, "");
												recInPara9.setField("YD_UP_COLL_SEQ"	, ""+commUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ"));
												recInPara9.setField("WRK_SPR"			, SprGp );
												recInPara9.setField("YD_TO_LOC_DCSN_MTD", "C");
												recInPara9.setField("TARGET_LOC"		, "YD" );
												rsReturn.addRecord(recInPara9);
									    		commUtils.printParam(logId, rsReturn);
											} else {
												commUtils.printLog(logId, "임시적치대에서 현적재위치로 가는 작업 스케쥴 생성 안함 ", "S+");		
											}
						    			}
									}  //for
								}
		    				} else {
	    						/******************************************************************************
	                    		* 장입 재료 매수 와 단 매수 는 같으나 장입순번 순서가  틀림
	            				******************************************************************************/            		
		    					// 현적치위치 - > 임시BED  
		      					recInPara9 = JDTORecordFactory.getInstance().create();
		    					recInPara9.setField("LOWEST_LYR_NO"		, szLOWEST_LYR_NO);
		    					recInPara9.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
		    					recInPara9.setField("YS_STK_BED_NO"		, szYS_STK_BED_NO);
		    					recInPara9.setField("YS_STK_LYR_NO"		, szYS_STK_LYR_NO);
		    					recInPara9.setField("YD_STK_LOT_CD"		, szYD_STK_LOT_CD);
		    					recInPara9.setField("WRKBOOKMTL_CNT"	, szWRKBOOKMTL_CNT);
			    				recInPara9.setField("MAIN_WRK_YN"		, "N");
		    					recInPara9.setField("UP_COLL_BASE"		, "" );     	
		    					recInPara9.setField("YD_UP_COLL_SEQ"	, ""+commUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ"));
		    					recInPara9.setField("WRK_SPR"			, "ALL" );
		    					recInPara9.setField("YD_TO_LOC_DCSN_MTD", "C");   // 분리 작업
		    					recInPara9.setField("TARGET_LOC"		, "TY" );
		    					recInPara9.setField("BED_CNT"			, szBED_CNT);
		    					recInPara9.setField("YD_WBOOK_ID"		, szYD_WBOOK_ID);
		    					recInPara9.setField("HANDLING_CNT"		, ""+intHandlingCnt);
		    					intHandlingCnt++;
								rsReturn.addRecord(recInPara9);
								
								recInPara9 = JDTORecordFactory.getInstance().create();
								// 임시BED -> 장입대
		    					recInPara9.setField("LOWEST_LYR_NO"		, szLOWEST_LYR_NO);
		    					recInPara9.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
		    					recInPara9.setField("YS_STK_BED_NO"		, szYS_STK_BED_NO);
		    					recInPara9.setField("YS_STK_LYR_NO"		, szYS_STK_LYR_NO);
		    					recInPara9.setField("YD_STK_LOT_CD"		, szYD_STK_LOT_CD);
		    					recInPara9.setField("WRKBOOKMTL_CNT"	, szWRKBOOKMTL_CNT);
		    					recInPara9.setField("BED_CNT"			, szBED_CNT);
		    					recInPara9.setField("YD_WBOOK_ID"		, szYD_WBOOK_ID);
		    					recInPara9.setField("HANDLING_CNT"		, ""+intHandlingCnt);
								
								recInPara9.setField("MAIN_WRK_YN"		, "Y");
								recInPara9.setField("UP_COLL_BASE"		, "" + (rsResult.size() - intYdUpCollSeq + 1));     	
			    				recInPara9.setField("YD_UP_COLL_SEQ"	, ""+commUtils.paraRecChkNullInt(recInPara9,"YD_UP_COLL_SEQ"));
			    				recInPara9.setField("WRK_SPR"			, "TARGET" );
			    				recInPara9.setField("TARGET_LOC"		, "TZ" );
			    				recInPara9.setField("YD_TO_LOC_DCSN_MTD", "C");
					    		intHandlingCnt++;  		    		//핸들링 카운트 증가
			    				rsReturn.addRecord(recInPara9);
					    		commUtils.printParam(logId, rsReturn);								
		    				}
								
	    				} else {
	    				
		    				
	    					// 일반주작업
	    					if((commUtils.paraRecChkNullInt(recStkLyr,"WRKBOOKMTL_CNT") != commUtils.paraRecChkNullInt(recStkLyr,"LYR_CNT"))) {
	    						// 현적치위치 -> 임시BED -> TO위치 
		      					//             임시BED -> TO위치
		    					
		      					recInPara9 = JDTORecordFactory.getInstance().create();
		    					recInPara9.setField("LOWEST_LYR_NO"		, szLOWEST_LYR_NO);
		    					recInPara9.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
		    					recInPara9.setField("YS_STK_BED_NO"		, szYS_STK_BED_NO);
		    					recInPara9.setField("YS_STK_LYR_NO"		, szYS_STK_LYR_NO);
		    					recInPara9.setField("YD_STK_LOT_CD"		, szYD_STK_LOT_CD);
		    					recInPara9.setField("WRKBOOKMTL_CNT"	, szWRKBOOKMTL_CNT);
			    				recInPara9.setField("MAIN_WRK_YN"		, "N");
		    					recInPara9.setField("UP_COLL_BASE"		, "" );     	
		    					recInPara9.setField("YD_UP_COLL_SEQ"	, ""+commUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ"));
		    					recInPara9.setField("WRK_SPR"			, "ALL" );
		    					recInPara9.setField("TARGET_LOC"		, "TY" );
		    					recInPara9.setField("YD_TO_LOC_DCSN_MTD", "C");   // 분리 작업
		    					recInPara9.setField("BED_CNT"			, szBED_CNT);
		    					recInPara9.setField("YD_WBOOK_ID"		, szYD_WBOOK_ID);
		    					recInPara9.setField("HANDLING_CNT"		, ""+intHandlingCnt);
		    					intHandlingCnt++;
								rsReturn.addRecord(recInPara9);
								
								
								// 작업 분리
								/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchWrkSpr  
								SELECT 'TARGET' AS GP
								     , '2'      AS SEQ
								     , A.YS_STK_COL_GP
								     , A.YS_STK_BED_NO
								     , A.YS_STK_LYR_NO          
								  FROM TB_YS_STKLYR A
								 WHERE A.YS_STK_COL_GP     = :V_YS_STK_COL_GP 
								   AND A.YS_STK_BED_NO     = :V_YS_STK_BED_NO 
								   AND A.YS_STK_LYR_NO     = :V_YS_STK_LYR_NO
								   AND A.SSTL_NO  IN (SELECT SSTL_NO                                   
								                        FROM TB_YS_WRKBOOKMTL                         
								                       WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID)
								 GROUP BY A.YS_STK_COL_GP,A.YS_STK_BED_NO,A.YS_STK_LYR_NO                                                
								UNION ALL                       
								SELECT 'DUMMY' AS GP
								     , '3'     AS SEQ
								     , A.YS_STK_COL_GP,A.YS_STK_BED_NO,A.YS_STK_LYR_NO          
								  FROM TB_YS_STKLYR A
								 WHERE A.YS_STK_COL_GP     = :V_YS_STK_COL_GP 
								   AND A.YS_STK_BED_NO     = :V_YS_STK_BED_NO 
								   AND A.YS_STK_LYR_NO     = :V_YS_STK_LYR_NO
								   AND A.SSTL_NO NOT IN (SELECT SSTL_NO                                   
								                           FROM TB_YS_WRKBOOKMTL                         
								                          WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID)  
								 GROUP BY A.YS_STK_COL_GP,A.YS_STK_BED_NO,A.YS_STK_LYR_NO                         
								 */
								JDTORecord recWrkbookSpr = JDTORecordFactory.getInstance().create();
								JDTORecordSet rsWrkbookSpr =JDTORecordFactory.getInstance().createRecordSet("Temp");
								
								if(!"TY".equals(szYD_TO_LOC_GUIDE.substring(2 , 4))){ //chito 2016.10
									rsWrkbookSpr = commDao.select(recStkLyr, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchWrkSpr", logId, methodNm, "작업예약재료분리정보 조회");
								}else {
									rsWrkbookSpr = commDao.select(recStkLyr, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchWrkSpr2", logId, methodNm, "작업예약재료분리정보 조회2");
								}
								
								
								if (rsWrkbookSpr.size() <= 0) {
									throw new Exception("작업예약재료분리정보 조회 >> 조회 Data 없음");
								} else {
									
									String SprGp = "";
									
									
									for(int Loop_k = 1; Loop_k <= rsWrkbookSpr.size(); Loop_k++) {
										rsWrkbookSpr.absolute(Loop_k);
										recWrkbookSpr  = rsWrkbookSpr.getRecord();
										SprGp = commUtils.trim(recWrkbookSpr.getFieldString("GP"));
										
										recInPara9 = JDTORecordFactory.getInstance().create();
										// 장입대 -> 현적재위치
				    					recInPara9.setField("LOWEST_LYR_NO"		, szLOWEST_LYR_NO);
				    					recInPara9.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
				    					recInPara9.setField("YS_STK_BED_NO"		, szYS_STK_BED_NO);
				    					recInPara9.setField("YS_STK_LYR_NO"		, szYS_STK_LYR_NO);
				    					recInPara9.setField("YD_STK_LOT_CD"		, szYD_STK_LOT_CD);
				    					recInPara9.setField("WRKBOOKMTL_CNT"	, szWRKBOOKMTL_CNT);
				    					recInPara9.setField("BED_CNT"			, szBED_CNT);
				    					recInPara9.setField("YD_WBOOK_ID"		, szYD_WBOOK_ID);
				    					recInPara9.setField("HANDLING_CNT"		, ""+intHandlingCnt);
							    		intHandlingCnt++;  		    		//핸들링 카운트 증가

							    		
							    		if(SprGp.equals("TARGET") ){
											// 임시BED -> 야드
											recInPara9.setField("MAIN_WRK_YN"		, "Y");
											recInPara9.setField("UP_COLL_BASE"		, "" + (rsResult.size() - intYdUpCollSeq + 1));     	
						    				recInPara9.setField("YD_UP_COLL_SEQ"	, ""+commUtils.paraRecChkNullInt(recInPara9,"YD_UP_COLL_SEQ"));
						    				recInPara9.setField("WRK_SPR"			, SprGp );
						    				recInPara9.setField("YD_TO_LOC_DCSN_MTD", "C");
					    					recInPara9.setField("TARGET_LOC"		, "YD" );
											rsReturn.addRecord(recInPara9);
								    		commUtils.printParam(logId, rsReturn);
						    			} else { 
						    				// 임시BED -> 야드
						    				if(!szYS_STK_COL_GP.substring(2, 4).equals("TY")){
												recInPara9.setField("MAIN_WRK_YN"		, "Y");
												recInPara9.setField("UP_COLL_BASE"		, "");
												recInPara9.setField("YD_UP_COLL_SEQ"	, ""+commUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ"));
												recInPara9.setField("WRK_SPR"			, SprGp );
												recInPara9.setField("YD_TO_LOC_DCSN_MTD", "C");
						    					recInPara9.setField("TARGET_LOC"		, "YD" );
												rsReturn.addRecord(recInPara9);
									    		commUtils.printParam(logId, rsReturn);
						    				} else {
												commUtils.printLog(logId, "임시적치대에서 현적재위치로 가는 작업 스케쥴 생성 안함 ", "S+");		
											}	
						    			}
									}  //for
								}			
	    					} else {
	    						// 해당단의 주작업 매수와  적치된 매수가 동일 한 경우
	    						recInPara9 = JDTORecordFactory.getInstance().create();
		    					recInPara9.setField("HANDLING_CNT"		, ""+intHandlingCnt);
		    					intHandlingCnt++;
		    					recInPara9.setField("LOWEST_LYR_NO"		, szLOWEST_LYR_NO);
		    					recInPara9.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
		    					recInPara9.setField("YS_STK_BED_NO"		, szYS_STK_BED_NO);
		    					recInPara9.setField("YS_STK_LYR_NO"		, szYS_STK_LYR_NO);
		    					recInPara9.setField("YD_STK_LOT_CD"		, szYD_STK_LOT_CD);
		    					recInPara9.setField("WRKBOOKMTL_CNT"	, szWRKBOOKMTL_CNT);
		    					recInPara9.setField("WRK_SPR"			, "ALL");
		    					recInPara9.setField("BED_CNT"			, szBED_CNT);
		    					recInPara9.setField("YD_WBOOK_ID"		, szYD_WBOOK_ID);
		    					recInPara9.setField("MAIN_WRK_YN"		, "Y");
		    					recInPara9.setField("UP_COLL_BASE", "" + (rsResult.size() - intYdUpCollSeq + 1));     	
		    					recInPara9.setField("YD_TO_LOC_DCSN_MTD", "M");
		    					recInPara9.setField("YD_UP_COLL_SEQ"	, ""+commUtils.paraRecChkNullInt(recInPara9,"YD_UP_COLL_SEQ"));
		    					recInPara9.setField("TARGET_LOC"		, "YD" );
			    				
		    					rsReturn.addRecord(recInPara9);	    			
	    						
	    					}
	    				}
	    			}else{
	 					recInPara9 = JDTORecordFactory.getInstance().create();
    					// 일반 보조 작업
    					recInPara9.setField("LOWEST_LYR_NO"		, szLOWEST_LYR_NO);
    					recInPara9.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
    					recInPara9.setField("YS_STK_BED_NO"		, szYS_STK_BED_NO);
    					recInPara9.setField("YS_STK_LYR_NO"		, szYS_STK_LYR_NO);
    					recInPara9.setField("YD_STK_LOT_CD"		, szYD_STK_LOT_CD);
    					recInPara9.setField("WRKBOOKMTL_CNT"	, szWRKBOOKMTL_CNT);
    					recInPara9.setField("WRK_SPR"			, "ALL");
    					recInPara9.setField("BED_CNT"			, szBED_CNT);
    					recInPara9.setField("YD_WBOOK_ID"		, szYD_WBOOK_ID);
    					recInPara9.setField("HANDLING_CNT"		, ""+intHandlingCnt);
    					recInPara9.setField("TARGET_LOC"		, "YD" );
    					intHandlingCnt++;
	    				
    					recInPara9.setField("MAIN_WRK_YN", "N");
	    				recInPara9.setField("YD_TO_LOC_DCSN_MTD", "W");
	    				recInPara9.setField("UP_COLL_BASE","");
	    				recInPara9.setField("YD_UP_COLL_SEQ", ""+commUtils.paraRecChkNullInt(recInPara9,"YD_UP_COLL_SEQ"));
	    				rsReturn.addRecord(recInPara9);
	    			}
	    			

	    		}//end of for
	    		
			}//end of for

			commUtils.printLog(logId, methodNm, "S-");			
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return YsConstant.RETN_INT_SUCCESS;
	}
	
	/**
	 * 오퍼레이션명 : 크레인 스케줄 GROUPING PARAMETER DATA SETTING
	 *  
	 * @param  String     szEqpId, szSchCd, rsMinWrkBookMtl, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public int CrnSchGrpTr(String logId, String methodNms ,JDTORecord recMainPara, JDTORecordSet rsReturn)throws JDTOException  {
		String methodNm = "크레인 스케줄 GROUPING[BtYsSchSeEJB.CrnSchGrp] < " + methodNms;
		JDTORecordSet rsSelBed       = JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecord recInPara     = null;
		JDTORecord recPara       = null;
		JDTORecord recStkLyr     = null;
		JDTORecordSet rsResult    = JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsWrkbookCol= JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		int intYdUpCollSeq = 0;
		int intHandlingCnt = 1;

		try {

			//------------------------------------------------------------------------------------------------------------
			//	rsMinWrkBookMtl - 열BED 단위로 최소 단정보 READ
			//------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S+");			

			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpBedGroupTr 
			SELECT YD_WBOOK_ID        AS YD_WBOOK_ID
			      ,YS_STK_COL_GP      AS YS_STK_COL_GP
			      ,YS_STK_BED_NO      AS YS_STK_BED_NO
			      ,YS_STK_LYR_NO      AS YS_STK_LYR_NO
			  FROM TB_YS_WRKBOOKMTL
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND DEL_YN='N'
			 GROUP BY YD_WBOOK_ID, YS_STK_COL_GP, YS_STK_BED_NO, YS_STK_LYR_NO
			 ORDER BY YS_STK_COL_GP, YS_STK_LYR_NO DESC, YS_STK_BED_NO 
			 */
			rsWrkbookCol = commDao.select(recMainPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookColGpBedGroupTr", logId, methodNm, "크레인스케줄재료정보 조회");
			if (rsWrkbookCol.size() <= 0) {
				throw new Exception("오류:크레인작업재료조회 >> 조회 Data 없음");
			}
			for(int Loop_j = 1; Loop_j <= rsWrkbookCol.size(); Loop_j++) {
				rsWrkbookCol.absolute(Loop_j);
				rsSelBed.addRecord(rsWrkbookCol.getRecord());
			}

			
			commUtils.printParam(logId, rsSelBed);
			//------------------------------------------------------------------------------------------------------------
			// 열별 베드별로  최하단 정보를 조회해서  
			// 각 재료의 레코드에 주작업/보조작업, TO위치결정방법 파라미터 설정
			//------------------------------------------------------------------------------------------------------------
			
			for(int Loop_i = 1; Loop_i <= rsSelBed.size(); Loop_i++) {
				
				rsSelBed.absolute(Loop_i);  //적치Bed를 조회한다.

				recPara = rsSelBed.getRecord();				
							
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setField("YD_WBOOK_ID",        recPara.getFieldString("YD_WBOOK_ID"));
				recInPara.setField("YS_STK_COL_GP",      recPara.getFieldString("YS_STK_COL_GP"));
				recInPara.setField("YS_STK_BED_NO",      recPara.getFieldString("YS_STK_BED_NO"));
				recInPara.setField("YS_STK_LYR_NO",      recPara.getFieldString("YS_STK_LYR_NO"));  

				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyrbyWBookIdGroupTr
				SELECT X.SSTL_NO        
				     , X.YS_STK_COL_GP  
				     , X.YS_STK_BED_NO  
				     , X.YS_STK_LYR_NO  
				     , X.YD_UP_COLL_SEQ 
				     , Y.YD_MTL_T       
				     , Y.YD_MTL_W       
				     , Y.YD_MTL_L       
				     , Y.YD_MTL_WT      
				     , Y.YD_STK_LOT_TP  
				     , (SELECT BLOOM_STACK_LOT_NO FROM USRPBA.TB_PB_BLOOMCOMM AA WHERE AA.BLM_NO=X.SSTL_NO
				       UNION ALL
				       SELECT BILLET_STACK_LOT_NO FROM USRPBA.TB_PB_BILLETCOMM AA WHERE AA.BLT_NO=X.SSTL_NO
				       )AS YD_STK_LOT_CD
				     , Y.YS_MTL_ITEM    
				     , Y.HCR_GP         
				     , Y.STL_PROG_CD    
				     , X.YD_STK_LYR_MTL_STAT 
				     , (SELECT SUM((SELECT 1  FROM TB_YS_WRKBOOKMTL                          
				                     WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID  
				                       AND SSTL_NO = A.SSTL_NO)) 
				           FROM TB_YS_STKLYR A                                               
				         WHERE A.YS_STK_COL_GP     = X.YS_STK_COL_GP
				           AND A.YS_STK_BED_NO     = X.YS_STK_BED_NO
				           AND A.YS_STK_LYR_NO     = X.YS_STK_LYR_NO
				       )  AS WRKBOOKMTL_CNT -- 해당단에 작업예약매수
				     , CASE WHEN X.YS_STK_LYR_NO = :V_YS_STK_LYR_NO THEN 'Y' ELSE 'N' END AS LOWEST_LYR_NO  
				  FROM (SELECT MAX(A.SSTL_NO)        AS SSTL_NO                  
				             , A.YS_STK_COL_GP       AS YS_STK_COL_GP           
				             , A.YS_STK_BED_NO       AS YS_STK_BED_NO           
				             , A.YS_STK_LYR_NO       AS YS_STK_LYR_NO           
				             , MAX(B.YD_UP_COLL_SEQ)      AS YD_UP_COLL_SEQ          
				             , MAX(A.YD_STK_LYR_MTL_STAT) AS YD_STK_LYR_MTL_STAT
				          FROM TB_YS_STKLYR A                                   
				              ,(SELECT SSTL_NO                                   
				                     , YD_UP_COLL_SEQ                           
				                     , DEL_YN                                   
				                  FROM TB_YS_WRKBOOKMTL                         
				                 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID ) B     
				         WHERE A.SSTL_NO              = B.SSTL_NO(+)              
				           AND A.YS_STK_COL_GP       = :V_YS_STK_COL_GP      
				           AND A.YS_STK_BED_NO       = :V_YS_STK_BED_NO     
				           AND A.YS_STK_LYR_NO       = :V_YS_STK_LYR_NO   
				           AND A.YD_STK_LYR_MTL_STAT = 'C' 
				           AND (A.DEL_YN <> 'Y' OR A.DEL_YN IS NULL)            
				           AND (B.DEL_YN <> 'Y' OR B.DEL_YN IS NULL)
				         GROUP BY YS_STK_COL_GP, YS_STK_BED_NO,A.YS_STK_LYR_NO) X        
				      ,TB_YS_STOCK Y                                            
				 WHERE X.SSTL_NO = Y.SSTL_NO                                      
				   AND (Y.DEL_YN <> 'Y' OR Y.DEL_YN IS NULL)                    
				 ORDER BY X.YS_STK_COL_GP, X.YS_STK_BED_NO, X.YS_STK_LYR_NO DESC
				 */

				rsResult = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyrbyWBookIdGroupTr", logId, methodNm, "BED 정보 조회");
				if (rsResult.size() <= 0) {
					throw new Exception("오류:BED정보조회 >> 조회 Data 없음");
				}					
				
				JDTORecord recInPara9 = JDTORecordFactory.getInstance().create();
	    		
				for(int Loop_j = 1; Loop_j <= rsResult.size(); Loop_j++) {
	    			rsResult.absolute(Loop_j);
	    			recStkLyr = rsResult.getRecord();

	    			recStkLyr.setField("YD_WBOOK_ID"	, recPara.getFieldString("YD_WBOOK_ID"));
	    			
	    			String szLOWEST_LYR_NO 	= commUtils.trim(recStkLyr.getFieldString("LOWEST_LYR_NO"));
	    			String szYS_STK_COL_GP 	= commUtils.trim(recStkLyr.getFieldString("YS_STK_COL_GP"));
	    			String szYS_STK_BED_NO 	= commUtils.trim(recStkLyr.getFieldString("YS_STK_BED_NO"));
	    			String szYS_STK_LYR_NO 	= commUtils.trim(recStkLyr.getFieldString("YS_STK_LYR_NO"));
	    			String szYD_STK_LOT_CD 	= commUtils.trim(recStkLyr.getFieldString("YD_STK_LOT_CD"));
	    			String szWRKBOOKMTL_CNT = commUtils.trim(recStkLyr.getFieldString("WRKBOOKMTL_CNT"));
	    			String szBED_CNT 		= ""+Loop_i;
	    			String szYD_WBOOK_ID 	= recPara.getFieldString("YD_WBOOK_ID");
		    		
    				// 해당단의 주작업 매수와  적치된 매수가 동일 한 경우
					recInPara9 = JDTORecordFactory.getInstance().create();
					recInPara9.setField("HANDLING_CNT"		, ""+intHandlingCnt);
					intHandlingCnt++;
					recInPara9.setField("LOWEST_LYR_NO"		, szLOWEST_LYR_NO);
					recInPara9.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
					recInPara9.setField("YS_STK_BED_NO"		, szYS_STK_BED_NO);
					recInPara9.setField("YS_STK_LYR_NO"		, szYS_STK_LYR_NO);
					recInPara9.setField("YD_STK_LOT_CD"		, szYD_STK_LOT_CD);
					recInPara9.setField("WRKBOOKMTL_CNT"	, szWRKBOOKMTL_CNT);
					recInPara9.setField("WRK_SPR"			, "ALL");
					recInPara9.setField("BED_CNT"			, szBED_CNT);
					recInPara9.setField("YD_WBOOK_ID"		, szYD_WBOOK_ID);
					recInPara9.setField("MAIN_WRK_YN"		, "Y");
					recInPara9.setField("UP_COLL_BASE", "" + (rsResult.size() - intYdUpCollSeq + 1));     	
					recInPara9.setField("YD_TO_LOC_DCSN_MTD", "M");
					recInPara9.setField("YD_UP_COLL_SEQ"	, ""+commUtils.paraRecChkNullInt(recInPara9,"YD_UP_COLL_SEQ"));
					recInPara9.setField("TARGET_LOC"		, "YD" );
    				
					rsReturn.addRecord(recInPara9);	    			
	    		
	    		}//end of for
	    		
			}//end of for

			commUtils.printLog(logId, methodNm, "S-");			
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
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
    public String procDummyToLoc(String logId, String methodNms, JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recWbook) throws JDTOException {
    	String methodNm = "빌렛 보조작업TO위치결정[BtYsSchSeEJB.procDummyToLoc] < " + methodNms;
    	String LocalmethodNm = "빌렛 보조작업TO위치결정[BtYsSchSeEJB.procDummyToLoc] "; 

		JDTORecordSet	rsResult		= null;
		JDTORecordSet	rsStock			= null;
		JDTORecord		recStock		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		JDTORecord		recCrnwrkmtl	= null;
		
		String szYS_UP_WO_LOC			= "";
		String szYS_UP_WO_LAYER			= null;
		String szYS_DN_WO_LOC			= null;
		String szYS_DN_WO_LAYER			= null;
		String szYD_UP_STK_COL_GP		= null;
		String szYD_UP_STK_BED_NO		= null;
		String szLogMsg					= null;

		JDTORecordSet outRsResult 		= null;
		JDTORecord    outRecResult 		= null;

		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		recCrnwrkmtl = rsCrnwrkmtl.getRecord();
		
		String szYD_EQP_ID  	= commUtils.trim(recCrnSch.getFieldString("YD_EQP_ID"));		//크레인설비ID
		String szBED_USG_CD  	= commUtils.trim(recCrnSch.getFieldString("BED_USG_CD"));		//적치대 용도 코드(권상위치 제외 용도)
		String szYD_SCH_PRIOR	= commUtils.trim(recCrnSch.getFieldString("YD_SCH_PRIOR"));		//분리작업시 필요
		String szYD_WBOOK_ID	= commUtils.trim(recCrnSch.getFieldString("YD_WBOOK_ID"));		//분리작업시 필요
		
		String szYD_CRN_SCH_ID 	= commUtils.trim(recCrnwrkmtl.getFieldString("YD_CRN_SCH_ID"));	//크레인스케줄ID
		String szSSTL_NO 		= commUtils.trim(recCrnwrkmtl.getFieldString("SSTL_NO"));		//크레인작업재료 
		String szYD_CHG_NO  	= commUtils.trim(recCrnwrkmtl.getFieldString("YD_CHG_NO"));		//장입순번
		int intMTL_CNT 			= Integer.parseInt(commUtils.nvl(recCrnwrkmtl.getFieldString("SH_CNT"),"0"));
		String szYD_TO_LOC_DCSN_MTD	= commUtils.trim(recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"  ));
		
		commUtils.printParam(logId, recWbook);
		
		
//		szYD_TO_LOC_GUIDE 	= commUtils.trim(recWbook.getFieldString("YD_TO_LOC_GUIDE"  ));		//사용자지정위치
		String szYD_SCH_CD 	= commUtils.trim(recWbook.getFieldString("YD_SCH_CD"  ));			//크레인스케줄코드
		
		//----------------------------------------------------------------------------------------------------------------------
		//	야드 저장품 정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ LocalmethodNm +"] 크레인작업재료의  단 최대SEQ 재료정보["+szSSTL_NO+"]를 저장품에서 조회 시작 ";
		commUtils.printLog(logId, szLogMsg, "SL");
		
		
		rsStock = commDao.select(recCrnwrkmtl, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStock", logId, methodNm, "저장품 조회");
		if (rsStock.size() <= 0) {
			szLogMsg = "["+ LocalmethodNm +"]  재료정보["+szSSTL_NO+"]를 저장품에서 조회 실패 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			return "0";
		}
		
		rsStock.first();
		recStock = rsStock.getRecord();
		
		String szYD_MTL_L_GP 	= commUtils.trim(recStock.getFieldString("YD_MTL_L_GP"  ));		//크레인작업 최하단재료의길이구분
		String szYD_MTL_W_GP 	= commUtils.trim(recStock.getFieldString("YD_MTL_W_GP"  ));		//크레인작업 최하단재료의폭구분
		String szYD_MTL_T_GP 	= commUtils.trim(recStock.getFieldString("YD_MTL_T_GP"  ));		//크레인작업 최하단재료의두께구분
		String szHEAT_NO   		= commUtils.trim(recStock.getFieldString("HEAT_NO"  ));			//HEAT_NO
		String szYD_STK_LOT_CD	= commUtils.trim(recStock.getFieldString("YD_STK_LOT_CD"));

		
		szLogMsg = "["+ LocalmethodNm +"] 크레인작업재료의  단 최대SEQ ["+szSSTL_NO+"]를 저장품에서 조회 완료 - 두께구분["+szYD_MTL_T_GP +"], 산적 LOT코드["+szYD_STK_LOT_CD+"]";
		commUtils.printLog(logId, szLogMsg, "SL");

		//----------------------------------------------------------------------------------------------------------------------
		//	권상중이거나 권하중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYS_UP_WO_LOC 		= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LOC"  ));		
		szYS_UP_WO_LAYER 	= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LAYER"  ));		

		if( szYS_UP_WO_LOC.equals("") ) {
			
			szLogMsg = "["+ methodNm +"] 크레인작업재료의 최하단 재료정보["+szSSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
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
			
			szLogMsg = "["+ LocalmethodNm +"] 크레인작업재료의  단 최대SEQ 재료정보["+szSSTL_NO+"]에 대한 권하 또는 권상위치 조회 완료  ";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YS_STK_COL_GP");
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YS_STK_BED_NO");
			szYS_UP_WO_LOC 			= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYS_UP_WO_LAYER 		= recTemp.getFieldString("YS_STK_LYR_NO");
			
			szLogMsg = "["+ LocalmethodNm +"] 조회된 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]";
			commUtils.printLog(logId, szLogMsg, "SL");

		}else{
		
			szYD_UP_STK_COL_GP = szYS_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYS_UP_WO_LOC.substring(6);
			
			szLogMsg = "["+ LocalmethodNm +"] 크레인스케줄에 등록된 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]";
			commUtils.printLog(logId, szLogMsg, "SL");
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
    	recTemp.setField("YD_STK_LOT_CD", 		szYD_STK_LOT_CD);											//크레인작업 최하단재료의 HEAT_NO
    	recTemp.setField("YD_MTL_T_GP", 		szYD_MTL_T_GP);										//크레인작업 최하단재료의 두께구분
    	recTemp.setField("YD_MTL_L_GP", 		szYD_MTL_L_GP);										//크레인작업 최하단재료의 길이구분
    	recTemp.setField("YD_MTL_W_GP", 		szYD_MTL_W_GP);										//크레인작업 최하단재료의 폭구분
    	recTemp.setField("YD_EQP_ID", 			szYD_EQP_ID);										//크레인설비ID
    	recTemp.setField("BED_USG_CD", 			szBED_USG_CD);										//적치대 용도 코드(권상위치 제외 용도)
    	recTemp.setField("YD_UP_STK_LOC", 		szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO);		
    	recTemp.setField("YD_CHG_NO", 			szYD_CHG_NO);										//권상 MIN(장입순번)
    	//----------------------------------------------------------------------------------------------------------------------
    	// 동일한  사양의 적치가능한 베드 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ LocalmethodNm +"] TOSQL:["+szYD_CRN_SCH_ID+ "] 동일한사양  두께구분["+szYD_MTL_T_GP +"], 산적 LOT코드["+szYD_STK_LOT_CD+"] 의 적치가능한 베드 조회 시작";
		commUtils.printLog(logId, szLogMsg, "SL");
			
		/* com.inisteel.cim.ys.common.dao.YsCommDAO.getToYardMoveBt 
		WITH PARA_TBL1 AS (
		 SELECT :V_HEAT_NO        AS P_HEAT_NO
		      , :V_YD_STK_LOT_CD     AS P_YD_STK_LOT_CD
		      , :V_YD_MTL_T_GP    AS P_YD_MTL_T_GP
		      , :V_YS_STK_COL_GP  AS P_YS_STK_COL_GP
		      , :V_YS_STK_BED_NO  AS P_YS_STK_BED_NO
		      , :V_BED_USG_CD     AS P_BED_USG_CD
		   FROM DUAL
		)
		SELECT A.SEQ_NUM 
		     , A.YD_STKBED_USG_CD  
		     , A.YS_STK_COL_GP
		     , A.YS_STK_BED_NO
		     , A.YS_STK_LYR_NO
		     , A.YS_STK_SEQ_NO
		     , A.MAX_SSTL_NO
		     , A.MTL_STAT_UP_CNT 
		     , A.HEAT_NO
		     , A.STLKIND_CD
		     , A.YD_MTL_T_GP
		  FROM
		(  
		        -- 적치대 BED 검색
		        SELECT CASE WHEN NVL(B.YD_STK_LOT_CD,'*')  = C.P_YD_STK_LOT_CD                                              THEN '9' 
			                WHEN NVL(substr(B.YD_STK_LOT_CD,1,15),'*')  = substr(C.P_YD_STK_LOT_CD,1,15)                    THEN '8' 
			                WHEN NVL(substr(B.YD_STK_LOT_CD,1,13),'*')  = substr(C.P_YD_STK_LOT_CD,1,13)                    THEN '7' 
			                WHEN NVL(substr(B.YD_STK_LOT_CD,1,10),'*')  = substr(C.P_YD_STK_LOT_CD,1,10)                    THEN '6' 
		                    WHEN NVL(substr(B.YD_STK_LOT_CD,11,2),'*')  = substr(C.P_YD_STK_LOT_CD,11,2)                    THEN '4' 
		                    ELSE '1' END  SEQ_NUM -- 우선순위
		             , 'V2' AS YD_STKBED_USG_CD
		             , A.YS_STK_COL_GP
		             , A.YS_STK_BED_NO
		             , A.YS_STK_LYR_NO 
		             , A.YS_STK_SEQ_NO 
		             , A.MAX_SSTL_NO
		             , A.MTL_STAT_UP_CNT 
		             , B.HEAT_NO
		             , B.STLKIND_CD
		             , B.YD_MTL_T_GP
		             , C.P_YS_STK_COL_GP
		             , C.P_YS_STK_BED_NO
		             , C.P_BED_USG_CD
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
		                     , ROW_NUMBER() OVER( PARTITION BY A1.YS_STK_COL_GP,A1.YS_STK_BED_NO ORDER BY A1.YS_STK_COL_GP,A1.YS_STK_BED_NO DESC, A1.YS_STK_LYR_NO DESC  ) AS CC
		                     , SUM(DECODE(A1.YD_STK_LYR_MTL_STAT,'U',1,0)) OVER (PARTITION BY A1.YS_STK_COL_GP,A1.YS_STK_BED_NO )  AS MTL_STAT_UP_CNT
		                  FROM TB_YS_STKLYR A1
		                     , TB_YS_STKCOL B1
		                     , PARA_TBL1 C1
		                 WHERE A1.YS_STK_COL_GP = B1.YS_STK_COL_GP
		                   AND NVL(B1.YD_STKBED_USG_CD,'*') <> 'V1'  -- 적치대 BED
		                   AND A1.SSTL_NO > ' '
		                   AND A1.DEL_YN = 'N'
		                   AND SUBSTR(A1.YS_STK_COL_GP,1,2) = SUBSTR(C1.P_YS_STK_COL_GP,1,2)
		                 ORDER BY A1.YS_STK_COL_GP,A1.YS_STK_BED_NO DESC
		                 ) 
		                 WHERE CC = 1 
		                   AND MTL_STAT_UP_CNT = 0  -- 권상 예약이 안된 열 
		               ) A
		             , TB_YS_STOCK  B 
		             , PARA_TBL1  C
		             , TB_YS_STKBED D
		         WHERE A.MAX_SSTL_NO = B.SSTL_NO
		           AND A.YS_STK_COL_GP = D.YS_STK_COL_GP
		           AND A.YS_STK_BED_NO = D.YS_STK_BED_NO
		           AND NVL(D.YD_STK_BED_ACT_STAT,'*') = 'L'
		           AND NVL(B.YD_MTL_T_GP,'*') = C.P_YD_MTL_T_GP
		           AND SUBSTR(A.YS_STK_COL_GP,1,2) = SUBSTR(C.P_YS_STK_COL_GP,1,2)  -- 동일 동 
		        
		        UNION ALL
		        -- 적치대 공BED
		        SELECT '5' AS SEQ_NUM
		             , 'V2' AS YD_STKBED_USG_CD
		             , A.YS_STK_COL_GP
		             , A.YS_STK_BED_NO   AS YS_STK_BED_NO
		             , ''   AS YS_STK_LYR_NO 
		             , ''   AS YS_STK_SEQ_NO 
		             , ''   AS SSTL_NO
		             , 0    AS MTL_STAT_UP_CNT 
		             , ''   AS HEAT_NO
		             , ''   AS STLKIND_CD
		             , ''   AS P_YD_MTL_T_GP 
		             , C.P_YS_STK_COL_GP
		             , C.P_YS_STK_BED_NO
		             , C.P_BED_USG_CD
		             
		          FROM TB_YS_STKLYR A
		             , (SELECT COUNT(A1.SSTL_NO)  AS SUM_CNT
		                     , A1.YS_STK_COL_GP
		                     , A1.YS_STK_BED_NO
		                  FROM TB_YS_STKLYR A1
		                     , TB_YS_STKCOL B1
		                     , PARA_TBL1 C1
		                 WHERE A1.YS_STK_COL_GP = B1.YS_STK_COL_GP
		                   AND SUBSTR(A1.YS_STK_COL_GP,1,2) = SUBSTR(C1.P_YS_STK_COL_GP,1,2) 
		                   AND NVL(B1.YD_STKBED_USG_CD,'*') <> 'V1'  -- 적치대 공BED
		                 GROUP BY A1.YS_STK_COL_GP, A1.YS_STK_BED_NO
		               ) B
		             , PARA_TBL1 C
		             , TB_YS_STKBED D
		         WHERE A.YS_STK_COL_GP = B.YS_STK_COL_GP
		           AND A.YS_STK_BED_NO = B.YS_STK_BED_NO
		           AND SUBSTR(A.YS_STK_COL_GP,1,2) = SUBSTR(C.P_YS_STK_COL_GP,1,2) -- 동일 동 
		           AND B.SUM_CNT = 0 
		           AND A.YS_STK_COL_GP = D.YS_STK_COL_GP
		           AND A.YS_STK_BED_NO = D.YS_STK_BED_NO
		           AND NVL(D.YD_STK_BED_ACT_STAT,'*') = 'L'
		         GROUP BY A.YS_STK_COL_GP,A.YS_STK_BED_NO
		        UNION ALL
		        -- # BED 검색
		        SELECT CASE WHEN NVL(B.YD_STK_LOT_CD,'*')  = C.P_YD_STK_LOT_CD                                              THEN '9' 
			                WHEN NVL(substr(B.YD_STK_LOT_CD,1,15),'*')  = substr(C.P_YD_STK_LOT_CD,1,15)                    THEN '8' 
			                WHEN NVL(substr(B.YD_STK_LOT_CD,1,13),'*')  = substr(C.P_YD_STK_LOT_CD,1,13)                    THEN '7' 
			                WHEN NVL(substr(B.YD_STK_LOT_CD,1,10),'*')  = substr(C.P_YD_STK_LOT_CD,1,10)                    THEN '6' 
		                    WHEN NVL(substr(B.YD_STK_LOT_CD,11,2),'*')  = substr(C.P_YD_STK_LOT_CD,11,2)                    THEN '4' 
		                    ELSE '1' END  SEQ_NUM -- 우선순위
		             ,'V1' AS YD_STKBED_USG_CD
		             , A.YS_STK_COL_GP
		             , A.MAX_YS_STK_BED_NO
		             , A.MAX_YS_STK_LYR_NO 
		             , A.MAX_YS_STK_SEQ_NO 
		             , A.MAX_SSTL_NO
		             , A.MTL_STAT_UP_CNT 
		             , B.HEAT_NO
		             , B.STLKIND_CD
		             , B.YD_MTL_T_GP
		             , C.P_YS_STK_COL_GP
		             , C.P_YS_STK_BED_NO
		             , C.P_BED_USG_CD
		          FROM
		               (
		                SELECT YS_STK_COL_GP
		                     , MAX_YS_STK_BED_NO
		                     , MAX_YS_STK_LYR_NO 
		                     , MAX_YS_STK_SEQ_NO 
		                     , MAX_SSTL_NO
		                     , MTL_STAT_UP_CNT -- 권상예약 수
		                  FROM 
		                (
		                SELECT A1.YS_STK_COL_GP
		                     , A1.YS_STK_BED_NO AS MAX_YS_STK_BED_NO
		                     , A1.YS_STK_LYR_NO AS MAX_YS_STK_LYR_NO 
		                     , A1.YS_STK_SEQ_NO AS MAX_YS_STK_SEQ_NO 
		                     , A1.SSTL_NO       AS MAX_SSTL_NO
		                     , A1.YD_STK_LYR_MTL_STAT
		                     , ROW_NUMBER() OVER( PARTITION BY A1.YS_STK_COL_GP ORDER BY A1.YS_STK_COL_GP,A1.YS_STK_BED_NO DESC,A1.YS_STK_LYR_NO DESC ,A1.YS_STK_SEQ_NO DESC  ) AS CC
		                     , SUM(DECODE(A1.YD_STK_LYR_MTL_STAT,'U',1,0)) OVER (PARTITION BY A1.YS_STK_COL_GP )  AS MTL_STAT_UP_CNT
		                  FROM TB_YS_STKLYR A1
		                     , TB_YS_STKCOL B1
		                     , PARA_TBL1 C1
		                 WHERE A1.YS_STK_COL_GP = B1.YS_STK_COL_GP
		                   AND B1.YD_STKBED_USG_CD = 'V1'  -- # BED
		                   AND A1.SSTL_NO > ' '
		                   AND A1.DEL_YN = 'N'
		                   AND SUBSTR(A1.YS_STK_COL_GP,1,2) = SUBSTR(C1.P_YS_STK_COL_GP,1,2)
		--                   AND NVL(B1.YS_STK_COL_T_GP,'*') = C1.P_YD_MTL_T_GP
		                 ORDER BY A1.YS_STK_COL_GP,A1.YS_STK_BED_NO DESC,A1.YS_STK_LYR_NO DESC,A1.YS_STK_SEQ_NO DESC
		                 ) 
		                 WHERE CC = 1 
		                   AND MTL_STAT_UP_CNT = 0  -- 권상 예약이 안된 열 
		               ) A
		             , TB_YS_STOCK  B 
		             , PARA_TBL1  C
		         WHERE A.MAX_SSTL_NO = B.SSTL_NO
		           AND NVL(B.YD_MTL_T_GP,'*') = C.P_YD_MTL_T_GP
		        UNION ALL

		        -- #BED 공BED
		        SELECT '1' AS SEQ_NUM
		             , 'V1' AS YD_STKBED_USG_CD
		             , A.YS_STK_COL_GP
		             , ''   AS YS_STK_BED_NO
		             , ''   AS YS_STK_LYR_NO 
		             , ''   AS YS_STK_SEQ_NO 
		             , ''   AS SSTL_NO
		             , 0    AS MTL_STAT_UP_CNT 
		             , ''   AS HEAT_NO
		             , ''   AS STLKIND_CD
		             , ''   AS YD_MTL_T_GP 
		             , C.P_YS_STK_COL_GP
		             , C.P_YS_STK_BED_NO
		             , C.P_BED_USG_CD
		          FROM TB_YS_STKLYR A
		             , (SELECT COUNT(A1.SSTL_NO)  AS SUM_CNT
		                     , A1.YS_STK_COL_GP
		                  FROM TB_YS_STKLYR A1
		                     , TB_YS_STKCOL B1
		                     , PARA_TBL1 C1
		                 WHERE A1.YS_STK_COL_GP = B1.YS_STK_COL_GP
		                   AND SUBSTR(A1.YS_STK_COL_GP,1,2) = SUBSTR(C1.P_YS_STK_COL_GP,1,2) 
		                   AND NVL(B1.YD_STKBED_USG_CD,'*') = 'V1'  -- 적치대 공BED
		                   AND NVL(B1.YS_STK_COL_T_GP,'*') = C1.P_YD_MTL_T_GP         
		                 GROUP BY A1.YS_STK_COL_GP
		               ) B
		             , PARA_TBL1 C
		         WHERE A.YS_STK_COL_GP = B.YS_STK_COL_GP
		           AND B.SUM_CNT = 0 
		         GROUP BY A.YS_STK_COL_GP
		        ) A
		WHERE SUBSTR(YS_STK_COL_GP,3,1) IN ('0','1')  -- 일반야드
		AND (CASE WHEN P_BED_USG_CD = 'V1' THEN YS_STK_COL_GP 
		            WHEN NVL(P_BED_USG_CD,'V2') = 'V2' THEN YS_STK_COL_GP|| YS_STK_BED_NO END) <> 
		      (CASE WHEN P_BED_USG_CD = 'V1' THEN P_YS_STK_COL_GP 
		            WHEN NVL(P_BED_USG_CD,'V2') = 'V2'THEN P_YS_STK_COL_GP|| P_YS_STK_BED_NO END) -- 자신BED(적치대) 및 열(#BED) 제외
		              
		ORDER BY SEQ_NUM DESC,YS_STK_COL_GP,YS_STK_BED_NO
		*/
		
		
		outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getToYardMoveBt", logId, methodNm, "동일한 사양의 적치가능한 베드 조회");
		if (outRsResult.size() <= 0) {
			szLogMsg = "["+ LocalmethodNm +"] 동일한  사양의 적치가능한 베드 조회 실패 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			return YsConstant.RETN_CD_FAILURE;
		}
	    
		JDTORecord  recResult   = null;
		String szYD_STKBED_USG_CD = "";
		String szLocYS_STK_COL_GP = "";
		String szLocYS_STK_BED_NO = "";
//		String sABLE_DCSN_MTD     = "";
	    // 적치 가능 여부 CEHCK	
		for(int i = 1; i <= outRsResult.size(); i++) {

			outRsResult.absolute(i);
			outRecResult  = outRsResult.getRecord();
			szYD_STKBED_USG_CD 	= commUtils.trim(outRecResult.getFieldString("YD_STKBED_USG_CD"  ));		//적치대 상태
			szLocYS_STK_COL_GP  = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));
			szLocYS_STK_BED_NO  = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));
			
			JDTORecord recTemp1 = JDTORecordFactory.getInstance().create();
			recTemp1.setField("YD_UP_STK_COL_GP", 			szYD_UP_STK_COL_GP);		
			recTemp1.setField("LOC_YS_STK_COL_GP", 			szLocYS_STK_COL_GP);		
			recTemp1.setField("LOC_YS_STK_BED_NO", 			szLocYS_STK_BED_NO);		
			recTemp1.setField("YD_STKBED_USG_CD", 			szYD_STKBED_USG_CD);		
			recTemp1.setField("YD_CHG_NO", 					szYD_CHG_NO);		
			recTemp1.setField("YD_TO_LOC_DCSN_MTD", 		szYD_TO_LOC_DCSN_MTD);		
			recTemp1.setField("YD_CHG_NO_CHK_YN", 			"N");		
			recTemp1.setField("MTL_CNT", 			""+intMTL_CNT);	
			recTemp1.setField("YD_SCH_CD", 					szYD_SCH_CD);
			
			
			recResult = this.procLocAbleCheckCnt(logId, methodNms, recTemp1) ;
			
			if(commUtils.trim(recResult.getFieldString("ABLE_YN"  )).equals("Y")){
//				sABLE_DCSN_MTD 		= commUtils.trim(recResult.getFieldString("ABLE_DCSN_MTD"));
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
		RecSetLoc.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);	 
		RecSetLoc.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);	 
		RecSetLoc.setField("YS_UP_WO_LOC", 		szYS_UP_WO_LOC); 
		RecSetLoc.setField("YS_UP_WO_LAYER",	szYS_UP_WO_LAYER);	 
		RecSetLoc.setField("YS_DN_WO_LOC", 		szYS_DN_WO_LOC);        
		RecSetLoc.setField("YS_DN_WO_LAYER", 	szYS_DN_WO_LAYER);
		RecSetLoc.setField("YD_TO_LOC_DCSN_MTD", 	szYD_TO_LOC_DCSN_MTD);
		
		
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
	public JDTORecord procLocAbleCheckCnt(String logId, String methodNms,JDTORecord jrParamSet) throws JDTOException {
		String methodNm = "적재가능 여부check[BtYsSchSeEJB.procLocAbleCheckCnt] < " + methodNms;
		String LocalmethodNm = "적재가능 여부check[BtYsSchSeEJB.procLocAbleCheckCnt] ";

		String szLogMsg					= null;
		JDTORecord		recInBed		= null;
		String szYS_DN_WO_LOC			= "";
		String szYS_DN_WO_LAYER			= null;
		JDTORecordSet RsResultBed 	    = null;
		JDTORecord    RecResultBed 		= null;
		String szTEMP_YS_STK_COL_GP 	= "";
		String szTEMP_YS_STK_BED_NO 	= "";
		String szTEMP_YS_STK_LYR_NO 	= "";
		String szTEMP_STL_CNT 	= "";
		
		
		String sYdUpLoc 			= commUtils.trim(jrParamSet.getFieldString("YD_UP_STK_COL_GP"));
		String sYdLoc 				= commUtils.trim(jrParamSet.getFieldString("LOC_YS_STK_COL_GP"));
		String sYdLocBed 			= commUtils.trim(jrParamSet.getFieldString("LOC_YS_STK_BED_NO"));
		String sYD_STKBED_USG_CD 	= commUtils.trim(jrParamSet.getFieldString("YD_STKBED_USG_CD"));
		String sYD_CHG_NO 			= commUtils.trim(jrParamSet.getFieldString("YD_CHG_NO"));
		String szYD_TO_LOC_DCSN_MTD = commUtils.trim(jrParamSet.getFieldString("YD_TO_LOC_DCSN_MTD"));
		String sYD_CHG_NO_YN 		= commUtils.trim(jrParamSet.getFieldString("YD_CHG_NO_CHK_YN"));
		String szYD_SCH_CD			= commUtils.trim(jrParamSet.getFieldString("YD_SCH_CD"));
		int intMTL_CNT 			= Integer.parseInt(commUtils.nvl(jrParamSet.getFieldString("MTL_CNT"),"0"));
		commUtils.printParam(logId, jrParamSet);
		int iYD_STK_BED_LYR_MAX	= 0;		
		int iBED_YD_CHG_NO = 0;
		int iYD_CHG_NO = Integer.parseInt(sYD_CHG_NO);			
		
		JDTORecord RecRtn = JDTORecordFactory.getInstance().create();	
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");
       
		RecRtn.setField("ABLE_DCSN_MTD", 	"Y");  //기본

		if (sYD_STKBED_USG_CD.equals("V1")) {
			//
			// # BED
			//	
			recInBed= JDTORecordFactory.getInstance().create();
			recInBed.setField("YS_STK_COL_GP", 	sYdLoc);		//권하지시위치 TEMP
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedAnalysisBl 
			SELECT AA.YS_STK_COL_GP 
			     , AA.YS_STK_BED_NO 
			     , AA.YS_STK_LYR_NO 
			     , AA.STL_CNT
			     , AA.BED_DAN_CHECK
			     , AA.MAX_COL_DAN_MOD_GP
			     , BB.YD_STR_GTR_CD 
			     , BB.YS_STK_BED_L_GP 
			     , BB.YS_STK_BED_W_GP 
			     , BB.YD_STK_BED_ACT_STAT 
			     , BB.YD_STK_BED_WHIO_STAT 
			     , BB.YD_STK_BED_LYR_MAX 
			     , BB.YD_STK_BED_WT_MAX 
			     , BB.YD_STK_BED_H_MAX 
			     , BB.YD_STK_BED_L_MAX 
			     , BB.YD_STK_BED_W_MAX 
			     , NVL((SELECT MIN(YD_CHG_NO) 
			           FROM TB_YS_STOCK A1
			                  , TB_YS_STKLYR B1
			         WHERE A1.SSTL_NO = B1.SSTL_NO
			           AND B1.YS_STK_COL_GP = AA.YS_STK_COL_GP
			           AND A1.YD_CHG_NO IS NOT NULL
			       ),0) YD_CHG_NO
			  FROM
			(  
			SELECT A.YS_STK_COL_GP
			     , A.YS_STK_BED_NO
			     , A.YS_STK_LYR_NO
			     , CASE WHEN ROUND(MOD(A.YS_STK_LYR_NO/2,1)) = 0 THEN 
			            CASE WHEN ROUND(MOD(A.YS_STK_BED_NO/2,1)) = 0 THEN 'Y'
			            ELSE 'N' END
			       ELSE CASE WHEN ROUND(MOD(A.YS_STK_BED_NO/2,1)) = 1 THEN 'Y'
			            ELSE 'N' END
			       END AS BED_DAN_CHECK
			     , ROUND(MOD(A.YS_STK_LYR_NO/2,1)) AS MAX_COL_DAN_MOD_GP  
			     , SUM (DECODE(A.YD_STK_LYR_MTL_STAT,'C',1,'D',1,0)) AS STL_CNT
			  FROM TB_YS_STKLYR A
			 WHERE A.YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND A.YS_STK_LYR_NO = 
			                   (SELECT NVL(MAX(YS_STK_LYR_NO),'01') 
			                      FROM TB_YS_STKLYR 
			                     WHERE YS_STK_COL_GP =A.YS_STK_COL_GP
			                       AND SSTL_NO IS NOT NULL)
			 GROUP BY A.YS_STK_COL_GP
			     , A.YS_STK_BED_NO
			     , A.YS_STK_LYR_NO                
			 ) AA
			 , TB_YS_STKBED BB
			WHERE AA.YS_STK_COL_GP = BB.YS_STK_COL_GP(+)
			  AND AA.YS_STK_BED_NO = BB.YS_STK_BED_NO(+)
			  AND BB.YD_STK_BED_ACT_STAT= 'L' 
			--  AND AA.BED_DAN_CHECK = 'Y'
			ORDER BY AA.YS_STK_COL_GP
			     , AA.YS_STK_BED_NO
			     , AA.YS_STK_LYR_NO
	    	 */
	    	
	    	RsResultBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedAnalysisBl", logId, methodNm, "이적대상  BED 조회");
			if (RsResultBed.size() <= 0) {
				szLogMsg = "["+ LocalmethodNm +"] 이적 대상 BED 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				RecRtn.setField("ABLE_YN", 	"N");  //불가
				return RecRtn;
			}
			
			// 열별 MAX단에 적치 가능 여부 CEHCK	
			for(int j = 1; j <= RsResultBed.size(); j++) {
				RsResultBed.absolute(j);
				RecResultBed  = RsResultBed.getRecord();
				szTEMP_YS_STK_COL_GP 		= commUtils.trim(RecResultBed.getFieldString("YS_STK_COL_GP"  )); //CA0104		
				szTEMP_YS_STK_BED_NO 		= commUtils.trim(RecResultBed.getFieldString("YS_STK_BED_NO"  )); //02
				szTEMP_YS_STK_LYR_NO 		= commUtils.trim(RecResultBed.getFieldString("YS_STK_LYR_NO"  )); //02
				szTEMP_STL_CNT       		= commUtils.trim(RecResultBed.getFieldString("STL_CNT"  )); //02
				iBED_YD_CHG_NO	 			= commUtils.paraRecChkNullInt(RecResultBed,"YD_CHG_NO");  //해당열의 MIN 장입순번
				/////////////////////////////////////		
				if(sYD_CHG_NO_YN.equals("Y")) {
					if ( (iYD_CHG_NO == 0) && (iBED_YD_CHG_NO > 0)) {     // 대상이일반재이고 BED가 장입재 인경우
						commUtils.printLog(logId,"["+ LocalmethodNm +"] 장입재가 해당열에 있습니다. ", "SL");
						RecRtn.setField("ABLE_YN", 	"N");  //불가
						return RecRtn;	
					}
	
					if ( (iYD_CHG_NO > 0) && (iBED_YD_CHG_NO > 0) && (iYD_CHG_NO > iBED_YD_CHG_NO )) {     // 대상이일반재이고 BED가 장입재 인경우
						commUtils.printLog(logId,"["+ LocalmethodNm +"] 장입순번이 빠른 장입재가 해당열에 있습니다. ", "SL");
						RecRtn.setField("ABLE_YN", 	"N");  //불가
						return RecRtn;	
					}
				}	
				////////////////////////////////////////////	
				iYD_STK_BED_LYR_MAX	 = commUtils.paraRecChkNullInt(RecResultBed,"YD_STK_BED_LYR_MAX");
				if (szTEMP_STL_CNT.equals("0")){  //공BED
					commUtils.printLog(logId, "["+ LocalmethodNm +"] TO 위치 가이드 처리 #위치 결정:szYS_DN_WO_LOC:" +  szTEMP_YS_STK_COL_GP + szTEMP_YS_STK_BED_NO + "szYS_DN_WO_LAYER:" + szTEMP_YS_STK_LYR_NO, "SL");
					szYS_DN_WO_LOC 		= szTEMP_YS_STK_COL_GP + szTEMP_YS_STK_BED_NO;
					szYS_DN_WO_LAYER 	= szTEMP_YS_STK_LYR_NO; 
					break;	
				}
				
			}
			
            if (szYS_DN_WO_LOC.equals("")) {
            	szYS_DN_WO_LOC 		= szTEMP_YS_STK_COL_GP + "01";
    			szYS_DN_WO_LAYER 	= commUtils.stringPlusInt(""+szTEMP_YS_STK_LYR_NO, 1);             	
            }
            if(Integer.parseInt(szYS_DN_WO_LAYER) > iYD_STK_BED_LYR_MAX){
            	szLogMsg = "["+ LocalmethodNm +"] 최대 적재 가능단 초과 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				RecRtn.setField("ABLE_YN", 	"N");  //불가
				return RecRtn;
            }		
 
		} else {
			// 적치대 BED
			recInBed= JDTORecordFactory.getInstance().create();
	
			recInBed.setField("YS_STK_COL_GP", 	sYdLoc);		
			recInBed.setField("YS_STK_BED_NO", 	sYdLocBed);		
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedAnalysis 
			SELECT B.YD_STR_GTR_CD 
			     , B.YS_STK_BED_T_GP 
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
			     , CASE WHEN YS_STK_BED_T_GP = 'L' THEN 5 
			            WHEN YS_STK_BED_T_GP = 'M' THEN 6 
			            WHEN YS_STK_BED_T_GP = 'S' THEN 7  
			            END AS LOC_ABLE 
			      
			     , (SELECT COUNT(*)  
			          FROM TB_YS_STKLYR  
			         WHERE YS_STK_COL_GP = B.YS_STK_COL_GP  
			           AND YS_STK_BED_NO = B.YS_STK_BED_NO   
			           AND YS_STK_LYR_NO = C.YS_STK_LYR_NO  
			           AND SSTL_NO IS NOT NULL) AS LOC_MTL_CNT                
			  FROM ( 
			        SELECT A.YS_STK_COL_GP 
			             , A.YS_STK_BED_NO 
			             , A.YD_STR_GTR_CD 
			             , A.YS_STK_BED_T_GP 
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
	    	
	    	RsResultBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedAnalysis", logId, methodNm, "적치 가능 여부 CEHCK 조회");
			if (RsResultBed.size() <= 0) {
				szLogMsg = "["+ LocalmethodNm +"] 적치 가능 여부 CEHCK 조회 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				
			}
			RsResultBed.first();
			RecResultBed = RsResultBed.getRecord();
	
			String szYD_STK_BED_ACT_STAT  	= commUtils.trim(RecResultBed.getFieldString("YD_STK_BED_ACT_STAT"  ));
			String szYD_STK_BED_WHIO_STAT 	= commUtils.trim(RecResultBed.getFieldString("YD_STK_BED_WHIO_STAT"  ));
			String szYD_STK_LYR_MTL_STAT  	= commUtils.trim(RecResultBed.getFieldString("YD_STK_LYR_MTL_STAT_U"  ));
			String szYS_STK_LYR_NO 			= commUtils.trim(RecResultBed.getFieldString("YS_STK_LYR_NO"  ));
			iBED_YD_CHG_NO	 				= commUtils.paraRecChkNullInt(RecResultBed,"YD_CHG_NO");  //해당열의 MIN 장입순번
			iYD_STK_BED_LYR_MAX 			= commUtils.paraRecChkNullInt(RecResultBed,"YD_STK_BED_LYR_MAX");
			
			if(sYD_CHG_NO_YN.equals("Y")) {
				if ( (iYD_CHG_NO == 0) && (iBED_YD_CHG_NO > 0)) {     // 대상이일반재이고 BED가 장입재 인경우
					commUtils.printLog(logId,"["+ LocalmethodNm +"] 장입재가 해당열에 있습니다. ", "SL");
					RecRtn.setField("ABLE_YN", 	"N");  //불가
					return RecRtn;	
				}
		
				if ( (iYD_CHG_NO > 0) && (iBED_YD_CHG_NO > 0) && (iYD_CHG_NO > iBED_YD_CHG_NO )) {     // 대상이일반재이고 BED가 장입재 인경우
					commUtils.printLog(logId, "["+ LocalmethodNm +"] 장입순번이 빠른 장입재가 해당열에 있습니다. ", "SL");
					RecRtn.setField("ABLE_YN", 	"N");  //불가
					return RecRtn;	
				}
			}
			
			if( !szYD_STK_BED_ACT_STAT.equals("L") ) {  // 적치가능
				szLogMsg = "["+ LocalmethodNm +"] 해당하는 적치열["+sYdLoc+"], 적치베드["+sYdLocBed+"]의 활성상태["+szYD_STK_BED_ACT_STAT+"]가 적치가능상태가 아닙니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				RecRtn.setField("ABLE_YN", 	"N");  //불가
				return RecRtn;
			}
			
			if( !szYD_STK_BED_WHIO_STAT.equals("E") ) { // 입출고 가능 
				szLogMsg = "["+ LocalmethodNm +"] 해당하는 적치열["+sYdLoc+"], 적치베드["+sYdLocBed+"]의 입출고상태["+szYD_STK_BED_WHIO_STAT+"]가 입고가능상태가 아닙니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				RecRtn.setField("ABLE_YN", 	"N");  //불가
				return RecRtn;
			}
			
			if( szYD_STK_LYR_MTL_STAT.equals("U")) {				//권상대기이면 적치불가능
				szLogMsg = "["+ LocalmethodNm +"] 적치재료상태["+szYD_STK_LYR_MTL_STAT+"]가 권상대기이므로 적치불가능";
				commUtils.printLog(logId, szLogMsg, "SL");
				RecRtn.setField("ABLE_YN", 	"N");  //불가
				return RecRtn;
			}
//12.04			
			// 야드별로 적재 가능 단 확인 후 수정 해야 함
			if(Integer.parseInt(commUtils.stringPlusInt(szYS_STK_LYR_NO, 1)) > iYD_STK_BED_LYR_MAX){
            	szLogMsg = "["+ LocalmethodNm +"] 최대 적재 가능단 초과 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				RecRtn.setField("ABLE_YN", 	"N");  //불가
				return RecRtn;
            }
			
			/*
			 * 권하위치 최종결정정보 셋팅.
			 */
			szYS_DN_WO_LOC		= sYdLoc + sYdLocBed;
			szYS_DN_WO_LAYER 	= szYS_STK_LYR_NO;
	
			szLogMsg = "["+ LocalmethodNm +"] 해당 권하위치["+szYS_DN_WO_LOC+"] 해당권하위치단["+szYS_DN_WO_LAYER+"].";
			commUtils.printLog(logId, szLogMsg, "SL");		
			
			
			int iLOC_ABLE		= commUtils.paraRecChkNullInt(RecResultBed,"LOC_ABLE");     // 해당단의 적재 가능 매수
			int iLOC_MTL_CNT	= commUtils.paraRecChkNullInt(RecResultBed,"LOC_MTL_CNT");  // 해당단의 적재된 매수
			
			szLogMsg = "★★★★★["+ LocalmethodNm +"] szYD_TO_LOC_DCSN_MTD ["+szYD_TO_LOC_DCSN_MTD+"]  TO위치 단의 적재 가능 매수["+iLOC_ABLE+"] TO위치 단의 적재된 매수["+iLOC_MTL_CNT+"].크레인스케쥴 작업 매수["+intMTL_CNT+"]";
			commUtils.printLog(logId, szLogMsg, "SL");		
			
			if( szYS_DN_WO_LAYER.equals("00") ) {							
				szYS_DN_WO_LAYER = "01";									
			
			} else {				
				
				if(iLOC_MTL_CNT == 0 ) {  // 해당단에 적재매수가 0이면 
					szYS_DN_WO_LAYER = commUtils.stringPlusInt(szYS_STK_LYR_NO, 1);	
					
				} else if (iLOC_ABLE  == iLOC_MTL_CNT ) {
					// 적재가능 매수와 적재된 매수가 동일 한 경우 
					// 최상단 자투리 없는 경우
					szYS_DN_WO_LAYER = commUtils.stringPlusInt(szYS_STK_LYR_NO, 1);	
//SJH 12.09                 
                } else if (iLOC_ABLE  < intMTL_CNT ) {
                    // 적재가능 매수와 작업지시 매수가 동일 한 경우 
                    // 최상단 자투리 없는 경우
                	szYS_DN_WO_LAYER = "";
//                 
				} else if ((iLOC_MTL_CNT != 0) && (!szYD_TO_LOC_DCSN_MTD.equals("C"))) {
					if (szYD_TO_LOC_DCSN_MTD.equals("W")) {
						szYS_DN_WO_LOC = "";
					} else  {
						if(szYD_STK_LYR_MTL_STAT.equals("C")) {
							// 해당단에 적재된 매수 보다 적재가능 매수가 큰 경우 
							// 1.크레인 작업지시 재편성처리 해야 함 
							//   1.1.현재 지시    ->  임시BED TY로 
							//   1.2 TO위치 결정된 bed 최상단 재료 ->  TY로
							//   1.3 조합된 TY -> 	TO위치 결정된 bed 로
							
							szLogMsg = "["+ LocalmethodNm +"] 모음 작업 함$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$";
							commUtils.printLog(logId, szLogMsg, "SL");
							
							RecRtn.setField("ABLE_DCSN_MTD"			, 	"S");
							RecRtn.setField("ABLE_YS_DN_WO_LOC"		, 	szYS_DN_WO_LOC);   //적재가능 위치    
							RecRtn.setField("ABLE_YS_DN_WO_LAYER"	, 	szYS_DN_WO_LAYER); //적재가능 위치
							if(!sYdUpLoc.substring(2, 4).equals("TY")) {
								
								//인출 작업 시(입고)
								if("C".equals(szYD_SCH_CD.substring(0 , 1)) && "TF".equals(szYD_SCH_CD.substring(2 , 4))){
									
									//낱본단위 합적 작업이 가능 한 경우 
									if(iLOC_ABLE >=iLOC_MTL_CNT +intMTL_CNT ){
										RecRtn.setField("YS_MERG_YN", 	"Y"); //단순 합적가능(낱본이 안남는 경우로 3번 크레인 작업 진행)	
									}else {
										RecRtn.setField("YS_MERG_YN", 	"S"); //복합 합적가능(낱본이 남는 경우로 4번 크레인 작업 진행)	
									}
									
								}
								
								recInBed= JDTORecordFactory.getInstance().create();								
								recInBed.setField("YD_UP_STK_LOC", 		szYD_SCH_CD); 
								recInBed.setField("YD_TO_LOC_GUIDE", 	szYS_DN_WO_LOC.substring(0, 2)+ "TY");
								
								JDTORecordSet rsBed	= JDTORecordFactory.getInstance().createRecordSet("");
								rsBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLocGuideTY", logId, methodNm, "임시  베드 조회");
								if (rsBed.size() > 0) {
						
									rsBed.absolute(1);
									recInBed = rsBed.getRecord();
									String szYS_STK_COL_GP = commUtils.trim(recInBed.getFieldString("YS_STK_COL_GP"  ));		
									String szYS_STK_BED_NO = commUtils.trim(recInBed.getFieldString("YS_STK_BED_NO"  ));		
									szYS_STK_LYR_NO        = commUtils.trim(recInBed.getFieldString("YS_STK_LYR_NO"  ));		
									
									szYS_DN_WO_LOC		= szYS_STK_COL_GP + szYS_STK_BED_NO;
									szYS_DN_WO_LAYER 	= szYS_STK_LYR_NO;
						
									if( szYS_DN_WO_LAYER.equals("00") ) {							//값이 없으면
										szYS_DN_WO_LAYER = "01";										 //1단
									}else{														     //값 이 존재하면
										szYS_DN_WO_LAYER = commUtils.stringPlusInt(szYS_STK_LYR_NO, 0);	//조회된 적치단 + 1
									}            
								} else {
									szYS_DN_WO_LOC = "";
								}
							} else {
	                            szYS_DN_WO_LOC = "";
	                        }
						} else {
                            szYS_DN_WO_LOC = "";
						} 
					} 
				} else {
					szYS_DN_WO_LOC = "";
				}
			}
		}
        if(!szYS_DN_WO_LOC.equals("")) {
        	RecRtn.setField("ABLE_YN", 	"Y");
        } else {
        	RecRtn.setField("ABLE_YN", 	"N");
        }
        
		RecRtn.setField("YS_DN_WO_LOC", 	szYS_DN_WO_LOC);  //현재 스케쥴 TO위치
		RecRtn.setField("YS_DN_WO_LAYER", 	szYS_DN_WO_LAYER); //현재 스케쥴 TO위치

		commUtils.printParam(logId, RecRtn);		
		
		commUtils.printLog(logId, methodNm, "S-");
		
		return RecRtn;
		
	}
	/**
	 * 원위치적재가능 여부 CHECK
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procLocAbleCheckCntUpLoc(String logId, String methodNms,JDTORecord jrParamSet) throws JDTOException {
		String methodNm = "적재가능 여부check[BtYsSchSeEJB.procLocAbleCheckCntUpLoc] < " + methodNms;
		String LocalmethodNm = "적재가능 여부check[BtYsSchSeEJB.procLocAbleCheckCntUpLoc] ";

		String szLogMsg					= null;
		JDTORecord		recInBed		= null;
		String szYS_DN_WO_LOC			= "";
		String szYS_DN_WO_LAYER			= null;
		JDTORecordSet RsResultBed 	    = null;
		JDTORecord    RecResultBed 		= null;
		String szTEMP_YS_STK_COL_GP 	= "";
		String szTEMP_YS_STK_BED_NO 	= "";
		String szTEMP_YS_STK_LYR_NO 	= "";
		String szTEMP_STL_CNT 	= "";
		
		String sYdLoc 				= commUtils.trim(jrParamSet.getFieldString("LOC_YS_STK_COL_GP"));
		String sYdLocBed 			= commUtils.trim(jrParamSet.getFieldString("LOC_YS_STK_BED_NO"));
		String sYdLocLyr 			= commUtils.trim(jrParamSet.getFieldString("LOC_YS_STK_LYR_NO"));
		String sYD_STKBED_USG_CD 	= commUtils.trim(jrParamSet.getFieldString("YD_STKBED_USG_CD"));
		String sYD_CHG_NO 			= commUtils.trim(jrParamSet.getFieldString("YD_CHG_NO"));
		String sYD_CHG_NO_YN 		= commUtils.trim(jrParamSet.getFieldString("YD_CHG_NO_CHK_YN"));
		
		commUtils.printParam(logId, jrParamSet);

		int iYD_STK_BED_LYR_MAX	= 0;		
		int iBED_YD_CHG_NO = 0;
		int iYD_CHG_NO = Integer.parseInt(sYD_CHG_NO);			
		
		JDTORecord RecRtn = JDTORecordFactory.getInstance().create();	
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");
       
		RecRtn.setField("ABLE_DCSN_MTD", 	"Y");  //기본

		if (sYD_STKBED_USG_CD.equals("V1")) {
			//
			// # BED
			//	
			recInBed= JDTORecordFactory.getInstance().create();
			recInBed.setField("YS_STK_COL_GP", 	sYdLoc);		//권하지시위치 
			recInBed.setField("YS_STK_LYR_NO", 	sYdLocLyr);		//권하지시위치단 
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedAnalysisBlUpLoc  
			SELECT AA.YS_STK_COL_GP 
			     , AA.YS_STK_BED_NO 
			     , AA.YS_STK_LYR_NO 
			     , AA.STL_CNT
			     , AA.BED_DAN_CHECK
			     , AA.MAX_COL_DAN_MOD_GP
			     , BB.YD_STR_GTR_CD 
			     , BB.YS_STK_BED_L_GP 
			     , BB.YS_STK_BED_W_GP 
			     , BB.YD_STK_BED_ACT_STAT 
			     , BB.YD_STK_BED_WHIO_STAT 
			     , BB.YD_STK_BED_LYR_MAX 
			     , BB.YD_STK_BED_WT_MAX 
			     , BB.YD_STK_BED_H_MAX 
			     , BB.YD_STK_BED_L_MAX 
			     , BB.YD_STK_BED_W_MAX 
			     , NVL((SELECT MIN(YD_CHG_NO) 
			           FROM TB_YS_STOCK A1
			                  , TB_YS_STKLYR B1
			         WHERE A1.SSTL_NO = B1.SSTL_NO
			           AND B1.YS_STK_COL_GP = AA.YS_STK_COL_GP
			           AND A1.YD_CHG_NO IS NOT NULL
			       ),0) YD_CHG_NO
			  FROM
			(  
			SELECT A.YS_STK_COL_GP
			     , A.YS_STK_BED_NO
			     , A.YS_STK_LYR_NO
			     , CASE WHEN ROUND(MOD(A.YS_STK_LYR_NO/2,1)) = 0 THEN 
			            CASE WHEN ROUND(MOD(A.YS_STK_BED_NO/2,1)) = 0 THEN 'Y'
			            ELSE 'N' END
			       ELSE CASE WHEN ROUND(MOD(A.YS_STK_BED_NO/2,1)) = 1 THEN 'Y'
			            ELSE 'N' END
			       END AS BED_DAN_CHECK
			     , ROUND(MOD(A.YS_STK_LYR_NO/2,1)) AS MAX_COL_DAN_MOD_GP  
			     , SUM (DECODE(A.YD_STK_LYR_MTL_STAT,'C',1,'D',1,0)) AS STL_CNT
			  FROM TB_YS_STKLYR A
			 WHERE A.YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND A.YS_STK_LYR_NO = 
			                   (SELECT NVL(MAX(YS_STK_LYR_NO),'01') 
			                      FROM TB_YS_STKLYR 
			                     WHERE YS_STK_COL_GP =A.YS_STK_COL_GP
			                       AND SSTL_NO IS NOT NULL
			                       AND YS_STK_LYR_NO < :V_YS_STK_LYR_NO)
			 GROUP BY A.YS_STK_COL_GP
			     , A.YS_STK_BED_NO
			     , A.YS_STK_LYR_NO                
			 ) AA
			 , TB_YS_STKBED BB
			WHERE AA.YS_STK_COL_GP = BB.YS_STK_COL_GP(+)
			  AND AA.YS_STK_BED_NO = BB.YS_STK_BED_NO(+)
			  AND BB.YD_STK_BED_ACT_STAT= 'L' 
			  AND 1 = CASE WHEN AA.STL_CNT > 0  AND SUBSTR(AA.YS_STK_COL_GP,3,2) IN ('TZ','TC','TY') THEN 0 ELSE 1 END
			ORDER BY AA.YS_STK_COL_GP
			     , AA.YS_STK_BED_NO
			     , AA.YS_STK_LYR_NO
	    	 */
	    	
	    	RsResultBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedAnalysisBlUpLoc", logId, methodNm, "이적대상  BED 조회");
			if (RsResultBed.size() <= 0) {
				szLogMsg = "["+ LocalmethodNm +"] 이적 대상 BED 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				RecRtn.setField("ABLE_YN", 	"N");  //불가
				return RecRtn;
			}
			
			// 열별 MAX단에 적치 가능 여부 CEHCK	
			for(int j = 1; j <= RsResultBed.size(); j++) {
				RsResultBed.absolute(j);
				RecResultBed  = RsResultBed.getRecord();
				szTEMP_YS_STK_COL_GP 		= commUtils.trim(RecResultBed.getFieldString("YS_STK_COL_GP"  )); //CA0104		
				szTEMP_YS_STK_BED_NO 		= commUtils.trim(RecResultBed.getFieldString("YS_STK_BED_NO"  )); //02
				szTEMP_YS_STK_LYR_NO 		= commUtils.trim(RecResultBed.getFieldString("YS_STK_LYR_NO"  )); //02
				szTEMP_STL_CNT       		= commUtils.trim(RecResultBed.getFieldString("STL_CNT"  )); //02
				iBED_YD_CHG_NO	 			= commUtils.paraRecChkNullInt(RecResultBed,"YD_CHG_NO");  //해당열의 MIN 장입순번
				/////////////////////////////////////		
				if(sYD_CHG_NO_YN.equals("Y")) {
					if ( (iYD_CHG_NO == 0) && (iBED_YD_CHG_NO > 0)) {     // 대상이일반재이고 BED가 장입재 인경우
						commUtils.printLog(logId,"["+ LocalmethodNm +"] 장입재가 해당열에 있습니다. ", "SL");
						RecRtn.setField("ABLE_YN", 	"N");  //불가
						return RecRtn;	
					}
	
					if ( (iYD_CHG_NO > 0) && (iBED_YD_CHG_NO > 0) && (iYD_CHG_NO > iBED_YD_CHG_NO )) {     // 대상이일반재이고 BED가 장입재 인경우
						commUtils.printLog(logId,"["+ LocalmethodNm +"] 장입순번이 빠른 장입재가 해당열에 있습니다. ", "SL");
						RecRtn.setField("ABLE_YN", 	"N");  //불가
						return RecRtn;	
					}
				}	
				////////////////////////////////////////////	
				iYD_STK_BED_LYR_MAX	 = commUtils.paraRecChkNullInt(RecResultBed,"YD_STK_BED_LYR_MAX");
				if (szTEMP_STL_CNT.equals("0")){  //공BED
					commUtils.printLog(logId, "["+ LocalmethodNm +"] TO 위치 가이드 처리 #위치 결정:szYS_DN_WO_LOC:" +  szTEMP_YS_STK_COL_GP + szTEMP_YS_STK_BED_NO + "szYS_DN_WO_LAYER:" + szTEMP_YS_STK_LYR_NO, "SL");
					szYS_DN_WO_LOC 		= szTEMP_YS_STK_COL_GP + szTEMP_YS_STK_BED_NO;
					szYS_DN_WO_LAYER 	= szTEMP_YS_STK_LYR_NO; 
					break;	
				}
				
			}
			
            if (szYS_DN_WO_LOC.equals("")) {
            	szYS_DN_WO_LOC 		= szTEMP_YS_STK_COL_GP + "01";
    			szYS_DN_WO_LAYER 	= commUtils.stringPlusInt(""+szTEMP_YS_STK_LYR_NO, 1);             	
            }
            if(Integer.parseInt(szYS_DN_WO_LAYER) > iYD_STK_BED_LYR_MAX){
            	szLogMsg = "["+ LocalmethodNm +"] 최대 적재 가능단 초과 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				RecRtn.setField("ABLE_YN", 	"N");  //불가
				return RecRtn;
            }		
 
		} else {
			// 적치대 BED
			recInBed= JDTORecordFactory.getInstance().create();
	
			recInBed.setField("YS_STK_COL_GP", 	sYdLoc);		
			recInBed.setField("YS_STK_BED_NO", 	sYdLocBed);	
			recInBed.setField("YS_STK_LYR_NO", 	sYdLocLyr);
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedAnalysisUpLoc
			SELECT B.YD_STR_GTR_CD 
			     , B.YS_STK_BED_T_GP 
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
			                                           AND B1.YS_STK_LYR_NO < :V_YS_STK_LYR_NO
					           AND A1.YD_CHG_NO IS NOT NULL 
					       ),0) YD_CHG_NO                
			  FROM ( 
			        SELECT A.YS_STK_COL_GP 
			             , A.YS_STK_BED_NO 
			             , A.YD_STR_GTR_CD 
			             , A.YS_STK_BED_T_GP 
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
			            , B.YD_MTL_T_GP 
			         FROM TB_YS_STKLYR A 
			            , TB_YS_STOCK B
			        WHERE A.YS_STK_COL_GP = :V_YS_STK_COL_GP 
			          AND A.YS_STK_BED_NO = :V_YS_STK_BED_NO 
			          AND A.YS_STK_LYR_NO < :V_YS_STK_LYR_NO
			          AND A.SSTL_NO = B.SSTL_NO
			          AND A.YD_STK_LYR_ACT_STAT = 'E' 
			          AND A.YD_STK_LYR_MTL_STAT IN ('C', 'U', 'D') 
			          AND A.DEL_YN = 'N' 
			       ) C 
			 WHERE B.YS_STK_COL_GP = C.YS_STK_COL_GP(+) 
			   AND B.YS_STK_BED_NO = C.YS_STK_BED_NO(+) 
			 ORDER BY B.YS_STK_COL_GP ASC, B.YS_STK_BED_NO ASC, C.YS_STK_LYR_NO DESC
			 */
	    	
	    	RsResultBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedAnalysisUpLoc", logId, methodNm, "적치 가능 여부 CEHCK 조회");
			if (RsResultBed.size() <= 0) {
				szLogMsg = "["+ LocalmethodNm +"] 적치 가능 여부 CEHCK 조회 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				
			}
			RsResultBed.first();
			RecResultBed = RsResultBed.getRecord();
	
			String szYD_STK_BED_ACT_STAT  	= commUtils.trim(RecResultBed.getFieldString("YD_STK_BED_ACT_STAT"  ));
			String szYD_STK_BED_WHIO_STAT 	= commUtils.trim(RecResultBed.getFieldString("YD_STK_BED_WHIO_STAT"  ));
			String szYD_STK_LYR_MTL_STAT  	= commUtils.trim(RecResultBed.getFieldString("YD_STK_LYR_MTL_STAT_U"  ));
			String szYS_STK_LYR_NO 			= commUtils.trim(RecResultBed.getFieldString("YS_STK_LYR_NO"  ));
			iBED_YD_CHG_NO	 				= commUtils.paraRecChkNullInt(RecResultBed,"YD_CHG_NO");  //해당열의 MIN 장입순번
			iYD_STK_BED_LYR_MAX 			= commUtils.paraRecChkNullInt(RecResultBed,"YD_STK_BED_LYR_MAX");
			if(sYD_CHG_NO_YN.equals("Y")) {
				if ( (iYD_CHG_NO == 0) && (iBED_YD_CHG_NO > 0)) {     // 대상이일반재이고 BED가 장입재 인경우
					commUtils.printLog(logId,"["+ LocalmethodNm +"] 장입재가 해당열에 있습니다. ", "SL");
					RecRtn.setField("ABLE_YN", 	"N");  //불가
					return RecRtn;	
				}
		
				if ( (iYD_CHG_NO > 0) && (iBED_YD_CHG_NO > 0) && (iYD_CHG_NO > iBED_YD_CHG_NO )) {     // 대상이일반재이고 BED가 장입재 인경우
					commUtils.printLog(logId, "["+ LocalmethodNm +"] 장입순번이 빠른 장입재가 해당열에 있습니다. ", "SL");
					RecRtn.setField("ABLE_YN", 	"N");  //불가
					return RecRtn;	
				}
			}
			
			if( !szYD_STK_BED_ACT_STAT.equals("L") ) {  // 적치가능
				szLogMsg = "["+ LocalmethodNm +"] 해당하는 적치열["+sYdLoc+"], 적치베드["+sYdLocBed+"]의 활성상태["+szYD_STK_BED_ACT_STAT+"]가 적치가능상태가 아닙니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				RecRtn.setField("ABLE_YN", 	"N");  //불가
				return RecRtn;
			}
			
			if( !szYD_STK_BED_WHIO_STAT.equals("E") ) { // 입출고 가능 
				szLogMsg = "["+ LocalmethodNm +"] 해당하는 적치열["+sYdLoc+"], 적치베드["+sYdLocBed+"]의 입출고상태["+szYD_STK_BED_WHIO_STAT+"]가 입고가능상태가 아닙니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				RecRtn.setField("ABLE_YN", 	"N");  //불가
				return RecRtn;
			}
			
//11.20			if( szYD_STK_LYR_MTL_STAT.equals("U")) {				//권상대기이면 적치불가능
			if( szYD_STK_LYR_MTL_STAT.equals("U")) {			
				szLogMsg = "["+ LocalmethodNm +"] 적치재료상태["+szYD_STK_LYR_MTL_STAT+"]가 권상대기이므로 적치불가능";
				commUtils.printLog(logId, szLogMsg, "SL");
				RecRtn.setField("ABLE_YN", 	"N");  //불가
				return RecRtn;
			}
			
//12.04		
			// 야드별로 적재 가능 단 확인 후 수정 해야 함
			if(Integer.parseInt(szYS_STK_LYR_NO) > iYD_STK_BED_LYR_MAX){
            	szLogMsg = "["+ LocalmethodNm +"] 최대 적재 가능단 초과 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				RecRtn.setField("ABLE_YN", 	"N");  //불가
				return RecRtn;
            }
			
			/*
			 * 권하위치 최종결정정보 셋팅.
			 */
			szYS_DN_WO_LOC		= sYdLoc + sYdLocBed;
			szYS_DN_WO_LAYER 	= szYS_STK_LYR_NO;
	
			szLogMsg = "["+ LocalmethodNm +"] 해당 권하위치["+szYS_DN_WO_LOC+"] 해당권하위치단["+szYS_DN_WO_LAYER+"].";
			commUtils.printLog(logId, szLogMsg, "SL");		

		}
        if(!szYS_DN_WO_LOC.equals("")) {
        	RecRtn.setField("ABLE_YN", 	"Y");
        } else {
        	RecRtn.setField("ABLE_YN", 	"N");
        }
        
		RecRtn.setField("YS_DN_WO_LOC", 	szYS_DN_WO_LOC);  //현재 스케쥴 TO위치
		RecRtn.setField("YS_DN_WO_LAYER", 	szYS_DN_WO_LAYER); //현재 스케쥴 TO위치

		commUtils.printParam(logId, RecRtn);		
		
		commUtils.printLog(logId, methodNm, "S-");
		
		return RecRtn;
		
	}		
	/**
	 * 크레인 스케쥴 ADD
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */	
	public String procCrnWrkAdd(String logId, String methodNms, JDTORecord recResult) throws JDTOException {
 		String methodNm = "스케줄링 크레인 스케줄 등록[BlYsSchSeEJB.procCrnWrkAdd] < " + methodNms;
		JDTORecord recInPara   = null;
		JDTORecordSet rsMoveWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
		int intRtnVal = 0;
		String szName = "SYSTEM";
		String szMsg = "";
		String ydCrnSchId ="";
		try{
			
			commUtils.printLog(logId, methodNm, "S+");
			
			String szYD_EQP_ID  		= commUtils.trim(recResult.getFieldString("YD_EQP_ID"));
			String szYD_SCH_CD 			= commUtils.trim(recResult.getFieldString("YD_SCH_CD"));
			String szYD_WBOOK_ID  		= commUtils.trim(recResult.getFieldString("YD_WBOOK_ID"));
			String szYD_SCH_PRIOR 		= commUtils.trim(recResult.getFieldString("YD_SCH_PRIOR"));
			String szFR_YS_UP_WO_LOC  	= commUtils.trim(recResult.getFieldString("YS_UP_WO_LOC"));
			String szFR_YS_UP_WO_LAYER 	= commUtils.trim(recResult.getFieldString("YS_UP_WO_LAYER"));
			String szTO_YS_DN_WO_LOC  	= commUtils.trim(recResult.getFieldString("YS_DN_WO_LOC"));
			String szTO_YS_DN_WO_LAYER 	= commUtils.trim(recResult.getFieldString("YS_DN_WO_LAYER"));
			String szYD_CRN_SCH_ID   	= commUtils.trim(recResult.getFieldString("YD_CRN_SCH_ID"));
			String szYD_CRN_SCH_NEXT1 	= commUtils.trim(recResult.getFieldString("YD_CRN_SCH_NEXT1"));
			String szYD_CRN_SCH_NEXT2 	= commUtils.trim(recResult.getFieldString("YD_CRN_SCH_NEXT2"));
			String szYD_TO_LOC_DCSN_MTD	= commUtils.trim(recResult.getFieldString("YD_TO_LOC_DCSN_MTD"));
			String szYD_AID_WRK_UPDN_GP = commUtils.trim(recResult.getFieldString("YD_AID_WRK_UPDN_GP"));
			String szYS_MERG_YN		    = commUtils.trim(recResult.getFieldString("YS_MERG_YN"));
			
			
			
			commUtils.printParam(logId, recResult);

			JDTORecord recInBed= JDTORecordFactory.getInstance().create();
			// 권하위치가 임시BED 인 경우
			if(szYD_AID_WRK_UPDN_GP.equals("2")) {
				
//				//Y:단순 합적가능(낱본이 안남는 경우로 3번 크레인 작업 진행) , S:복합 합적가능(낱본이 남는 경우로 4번 크레인 작업 진행)					
//				if("Y".equals(szYS_MERG_YN)){
//					
//				}else{
					recInBed.setField("YD_UP_STK_LOC", 		szYD_SCH_CD);
					recInBed.setField("YD_TO_LOC_GUIDE", 		szTO_YS_DN_WO_LOC);
					
					JDTORecordSet rsBed	= JDTORecordFactory.getInstance().createRecordSet("");
					rsBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLocGuideTY", logId, methodNm, "임시  베드 조회");
					if (rsBed.size() > 0) {
			
						rsBed.absolute(1);
						recInBed = rsBed.getRecord();
						String szYS_STK_COL_GP = commUtils.trim(recInBed.getFieldString("YS_STK_COL_GP"  ));		//권하지시위치 TEMP
						String szYS_STK_BED_NO = commUtils.trim(recInBed.getFieldString("YS_STK_BED_NO"  ));		//권하지시위치 TEMP
						String szYS_STK_LYR_NO = commUtils.trim(recInBed.getFieldString("YS_STK_LYR_NO"  ));		//권하지시위치 TEMP
						
						szTO_YS_DN_WO_LOC		= szYS_STK_COL_GP + szYS_STK_BED_NO;
						szTO_YS_DN_WO_LAYER 	= szYS_STK_LYR_NO;
			
						if( szTO_YS_DN_WO_LAYER.equals("00") ) {							//값이 없으면
							szTO_YS_DN_WO_LAYER = "01";										 //1단
						}else{														     //값 이 존재하면
							szTO_YS_DN_WO_LAYER = commUtils.stringPlusInt(szYS_STK_LYR_NO, 0);	//조회된 적치단
						}            
					} else {
					
						return YsConstant.RETN_CD_FAILURE;
					}	
//				}
			
				//이적 작업 재료 정보 조회
				rsMoveWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("");
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setField("YS_STK_COL_GP", szFR_YS_UP_WO_LOC.substring(0, 6));
				recInPara.setField("YS_STK_BED_NO", szFR_YS_UP_WO_LOC.substring(6));
				recInPara.setField("YS_STK_LYR_NO", szFR_YS_UP_WO_LAYER);
	
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyrSstlNo 
				SELECT * FROM 
				(
				SELECT A.YS_STK_COL_GP
				     , A.YS_STK_BED_NO
				     , A.YS_STK_LYR_NO
				     , A.YS_STK_SEQ_NO
				     , A.SSTL_NO
				     , B.YD_STK_LOT_TP
				     , B.YD_STK_LOT_CD
				     , B.HCR_GP
				     , B.STL_PROG_CD
				     , B.YS_MTL_ITEM
				     , CASE WHEN YD_MTL_T_GP = 'L' THEN 5
				            WHEN YD_MTL_T_GP = 'M' THEN 6
				            WHEN YD_MTL_T_GP = 'S' THEN 7
				            ELSE 0 
				            END  AS MTL_CNT
				  FROM TB_YS_STKLYR A
				     , TB_YS_STOCK  B
				 WHERE A.SSTL_NO = B.SSTL_NO
				   AND A.YS_STK_COL_GP = :V_YS_STK_COL_GP
				   AND A.YS_STK_BED_NO = :V_YS_STK_BED_NO
				   AND A.YS_STK_LYR_NO = :V_YS_STK_LYR_NO
				   AND A.YD_STK_LYR_MTL_STAT = 'C'
				ORDER BY YS_STK_COL_GP, YS_STK_BED_NO, YS_STK_LYR_NO, YS_STK_SEQ_NO
				) WHERE ROWNUM <= MTL_CNT
				*/	   
				rsMoveWrkBookMtl = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyrSstlNo", logId, methodNm, "이적재료정보 조회");
				if (rsMoveWrkBookMtl.size() <= 0) {
					return YsConstant.RETN_CD_FAILURE;
				}			
			} else if(szYD_AID_WRK_UPDN_GP.equals("3")) {
				 
				//이적 작업 재료 정보 조회
				rsMoveWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("");
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setField("YD_CRN_SCH_ID1", szYD_CRN_SCH_ID);
				recInPara.setField("YD_CRN_SCH_ID2", szYD_CRN_SCH_NEXT1);
	
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyrSstlNoNext3
				SELECT * FROM 
				(SELECT CASE WHEN  A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID2 THEN '1'
				                 ELSE '2' 
				            END  AS SEQ                 
				     , B.YS_STK_COL_GP
				     , B.YS_STK_BED_NO
				     , B.YS_STK_LYR_NO
				     , B.YS_STK_SEQ_NO
				     , A.SSTL_NO 
				     , C.YD_STK_LOT_TP
				     , C.YD_STK_LOT_CD
				     , C.HCR_GP
				     , C.STL_PROG_CD
				     , C.YS_MTL_ITEM
				     , CASE WHEN YD_MTL_T_GP = 'L' THEN 5
				            WHEN YD_MTL_T_GP = 'M' THEN 6
				            WHEN YD_MTL_T_GP = 'S' THEN 7
				            ELSE 0 
				            END  AS MTL_CNT
				  FROM TB_YS_CRNWRKMTL A
				     , TB_YS_CRNSCH D
				     , TB_YS_STKLYR B
				     , TB_YS_STOCK  C
				 WHERE A.YD_CRN_SCH_ID IN (:V_YD_CRN_SCH_ID1,:V_YD_CRN_SCH_ID2)
				   AND A.YD_CRN_SCH_ID = D.YD_CRN_SCH_ID
				   AND A.SSTL_NO = B.SSTL_NO 
				   AND A.SSTL_NO = C.SSTL_NO 
				   AND B.YS_STK_COL_GP LIKE 'C_TY%'
				   AND A.DEL_YN = 'N'
				ORDER BY SEQ, B.YS_STK_COL_GP,B.YS_STK_BED_NO,B.YS_STK_LYR_NO,B.YS_STK_SEQ_NO
				) WHERE ROWNUM <= MTL_CNT
				*/	   
				rsMoveWrkBookMtl = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyrSstlNoNext3", logId, methodNm, "크레인스케줄재료정보 조회");
				if (rsMoveWrkBookMtl.size() > 0) {
					
					rsMoveWrkBookMtl.absolute(1);
					recInBed = rsMoveWrkBookMtl.getRecord();
					String szYS_STK_COL_GP = commUtils.trim(recInBed.getFieldString("YS_STK_COL_GP"  ));		//권하지시위치 TEMP
					String szYS_STK_BED_NO = commUtils.trim(recInBed.getFieldString("YS_STK_BED_NO"  ));		//권하지시위치 TEMP
					String szYS_STK_LYR_NO = commUtils.trim(recInBed.getFieldString("YS_STK_LYR_NO"  ));		//권하지시위치 TEMP
					
					szFR_YS_UP_WO_LOC		= szYS_STK_COL_GP + szYS_STK_BED_NO;
					szFR_YS_UP_WO_LAYER 	= szYS_STK_LYR_NO;
		
				} else {
					return YsConstant.RETN_CD_FAILURE;
				}	
				
			} else if(szYD_AID_WRK_UPDN_GP.equals("4")) {
				
				//이적 작업 재료 정보 조회
				rsMoveWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("");
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setField("YD_CRN_SCH_ID1", szYD_CRN_SCH_ID);
				recInPara.setField("YD_CRN_SCH_ID2", szYD_CRN_SCH_NEXT1);
				recInPara.setField("YD_CRN_SCH_ID3", szYD_CRN_SCH_NEXT2);
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyrSstlNoNext4 
				SELECT B.YS_STK_COL_GP
				     , B.YS_STK_BED_NO
				     , B.YS_STK_LYR_NO
				     , B.YS_STK_SEQ_NO
				     , A.SSTL_NO 
				     , C.YD_STK_LOT_TP
				     , C.YD_STK_LOT_CD
				     , C.HCR_GP
				     , C.STL_PROG_CD
				     , C.YS_MTL_ITEM
				  FROM TB_YS_CRNWRKMTL A
				     , TB_YS_CRNSCH D
				     , TB_YS_STKLYR B
				     , TB_YS_STOCK  C
				 WHERE A.YD_CRN_SCH_ID IN (:V_YD_CRN_SCH_ID1,:V_YD_CRN_SCH_ID2)
				   AND A.YD_CRN_SCH_ID = D.YD_CRN_SCH_ID
				   AND A.SSTL_NO = B.SSTL_NO 
				   AND A.SSTL_NO = C.SSTL_NO 
				   AND B.YS_STK_COL_GP LIKE 'C_TY%'
				   AND A.DEL_YN = 'N'
				   AND A.SSTL_NO NOT IN (SELECT SSTL_NO FROM TB_YS_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID3  )
				ORDER BY B.YS_STK_COL_GP,B.YS_STK_BED_NO,B.YS_STK_LYR_NO,B.YS_STK_SEQ_NO
				*/	   
				rsMoveWrkBookMtl = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyrSstlNoNext4", logId, methodNm, "크레인스케줄재료정보 조회");
				if (rsMoveWrkBookMtl.size() > 0) {
					
					rsMoveWrkBookMtl.absolute(1);
					recInBed = rsMoveWrkBookMtl.getRecord();
					String szYS_STK_COL_GP = commUtils.trim(recInBed.getFieldString("YS_STK_COL_GP"  ));		//권하지시위치 TEMP
					String szYS_STK_BED_NO = commUtils.trim(recInBed.getFieldString("YS_STK_BED_NO"  ));		//권하지시위치 TEMP
					String szYS_STK_LYR_NO = commUtils.trim(recInBed.getFieldString("YS_STK_LYR_NO"  ));		//권하지시위치 TEMP
					
					szFR_YS_UP_WO_LOC		= szYS_STK_COL_GP + szYS_STK_BED_NO;
					szFR_YS_UP_WO_LAYER 	= szYS_STK_LYR_NO;
		
				} else {
					return YsConstant.RETN_CD_SUCCESS;
				}						
			}
			
			

			rsMoveWrkBookMtl.first();
			
			JDTORecord resMoveWrkBookMtl = rsMoveWrkBookMtl.getRecord();
			JDTORecord recInCrn = JDTORecordFactory.getInstance().create();
			/**********************************************************
			*  크레인 스케줄 등록
			**********************************************************/			
			//크레인스케줄ID를 할당받는다
			ydCrnSchId = commDao.getSeqId(logId, methodNm, "CrnSch");

			recInCrn.setField("YD_CRN_SCH_ID",   	ydCrnSchId);
			recInCrn.setField("YD_WBOOK_ID",   		szYD_WBOOK_ID);
			recInCrn.setField("YD_EQP_ID",        	szYD_EQP_ID);
			recInCrn.setField("YD_GP",            	resMoveWrkBookMtl.getFieldString("YS_STK_COL_GP").substring(0,1));
			recInCrn.setField("YD_BAY_GP",        	resMoveWrkBookMtl.getFieldString("YS_STK_COL_GP").substring(1,2));
			recInCrn.setField("YD_SCH_CD",        	szYD_SCH_CD);	
			recInCrn.setField("REGISTER",         	szName);	
			recInCrn.setField("YD_SCH_PRIOR",      	szYD_SCH_PRIOR);
			recInCrn.setField("YD_SCH_ST_GP",    	"A");
			recInCrn.setField("YS_UP_WO_LOC",     	resMoveWrkBookMtl.getFieldString("YS_STK_COL_GP") + resMoveWrkBookMtl.getFieldString("YS_STK_BED_NO"));
			recInCrn.setField("YS_UP_WO_LAYER",   	resMoveWrkBookMtl.getFieldString("YS_STK_LYR_NO"));
			recInCrn.setField("YD_WRK_PROG_STAT", 	"W");
			
			if(recInCrn.getFieldString("YS_UP_WO_LOC").trim().equals("")){
				szMsg = "권상지시위치가 없습니다.";
				return YsConstant.RETN_CD_FAILURE;
			}
			recInCrn.setField("YD_EQP_WRK_SH", "" + rsMoveWrkBookMtl.size());  //재료매수


			intRtnVal = commDao.insert(recInCrn, "com.inisteel.cim.ys.common.dao.YsCommDAO.insYdCrnsch", logId, methodNm, "TB_YS_CRNSCH 생성");
			if(intRtnVal < 1) {
				szMsg = "크레인 스케줄 등록중  Error!! ErrorCode: " + intRtnVal;
				commUtils.printLog(logId, szMsg, "SL");
				return YsConstant.RETN_CD_FAILURE;
			}

			// 모음 작업시 3번째 4번째 스케쥴 만들때 권상 위치가 전 저장위치와 중복되면 SKIP 처리 함
			// 권하는 별도(procUpdateLoc) 처리 해야 함
			JDTORecordSet rsMaxSeq1 = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInPara1 	= JDTORecordFactory.getInstance().create();
			JDTORecord recPara1 	= JDTORecordFactory.getInstance().create();
			String szLYR_UPDATE_YN  = "";
			if( szYD_AID_WRK_UPDN_GP.equals("1") || szYD_AID_WRK_UPDN_GP.equals("2")){
				szLYR_UPDATE_YN = "Y";
				
			} else {
				recInPara1.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
				recInPara1.setField("YD_CRN_SCH_ID", 	ydCrnSchId);
				recInPara1.setField("YS_STK_COL_GP", 	resMoveWrkBookMtl.getFieldString("YS_STK_COL_GP"));
				recInPara1.setField("YS_STK_BED_NO", 	resMoveWrkBookMtl.getFieldString("YS_STK_BED_NO"));
				recInPara1.setField("YS_STK_LYR_NO", 	resMoveWrkBookMtl.getFieldString("YS_STK_LYR_NO"));
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDnMtlStatChk 
				SELECT DECODE(NVL(SUM(CASE WHEN :V_YS_STK_COL_GP || :V_YS_STK_BED_NO || :V_YS_STK_LYR_NO IN (BEF_UP_WO_LOC,BEF_DN_WO_LOC) THEN 1
				                     ELSE 0 END),0),0,'Y','N') AS LYR_UPDATE_YN  
				  FROM
				        (SELECT YS_UP_WO_LOC||YS_UP_WO_LAYER AS BEF_UP_WO_LOC
				              , YS_DN_WO_LOC||YS_DN_WO_LAYER AS BEF_DN_WO_LOC
				          FROM TB_YS_CRNSCH
				         WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				           AND YD_CRN_SCH_ID < :V_YD_CRN_SCH_ID
				           AND YD_TO_LOC_DCSN_MTD = 'C'
				           AND DEL_YN = 'N'
				        )   
				*/
				rsMaxSeq1 = commDao.select(recInPara1, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDnMtlStatChk", logId, methodNm, "권상정보 검색");
				rsMaxSeq1.first();
				recPara1 = rsMaxSeq1.getRecord();
				commUtils.printLog(logId, methodNm + commUtils.trim(recPara1.getFieldString("LYR_UPDATE_YN"  )) + ">>" + szYD_AID_WRK_UPDN_GP  , "SL"); 
				
				szLYR_UPDATE_YN = commUtils.trim(recPara1.getFieldString("LYR_UPDATE_YN"  ));
			}	
			
			JDTORecord recBedLyr = JDTORecordFactory.getInstance().create();
			if(szLYR_UPDATE_YN.equals("Y")){	
				/**********************************************************
				*  적치단의 재료상태를 권상대기로 변경
				**********************************************************/			
				for(int Loop_k = 1; Loop_k <= rsMoveWrkBookMtl.size(); Loop_k++) {
					
					rsMoveWrkBookMtl.absolute(Loop_k);
					recBedLyr.setRecord( rsMoveWrkBookMtl.getRecord() );	
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
						return YsConstant.RETN_CD_FAILURE;
					}
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
			recInCrnMtl.setField("YD_AID_WRK_YN"	, "N"); //보조작업
			recInCrnMtl.setField("REGISTER"			, szName);
			recInCrnMtl.setField("MOD_DDTT"			, "");
			
			recBedLyr = JDTORecordFactory.getInstance().create();
			for(int Loop_k = 1; Loop_k <= rsMoveWrkBookMtl.size(); Loop_k++) {
				
				rsMoveWrkBookMtl.absolute(Loop_k);
				
				recBedLyr  = rsMoveWrkBookMtl.getRecord();
				recInCrnMtl.setField("SSTL_NO"			,  commUtils.trim(recBedLyr.getFieldString("SSTL_NO"  )));
				recInCrnMtl.setField("YS_STK_LYR_NO"	,  "01");
				recInCrnMtl.setField("YS_STK_SEQ_NO"	,  ""+Loop_k );
				
				recInCrnMtl.setField("YD_STK_LOT_TP"	,  commUtils.trim(recBedLyr.getFieldString("YD_STK_LOT_TP"  )));
				recInCrnMtl.setField("YD_STK_LOT_CD"	,  commUtils.trim(recBedLyr.getFieldString("YD_STK_LOT_CD"  )));
				recInCrnMtl.setField("HCR_GP"			,  commUtils.trim(recBedLyr.getFieldString("HCR_GP"  )));
				recInCrnMtl.setField("STL_PROG_CD"		,  commUtils.trim(recBedLyr.getFieldString("STL_PROG_CD"  )));
				recInCrnMtl.setField("YS_MTL_ITEM"		,  commUtils.trim(recBedLyr.getFieldString("YS_MTL_ITEM"  )));
				recInCrnMtl.setField("YS_ROUTE_GP"		,  "");
				recInCrnMtl.setField("YD_TO_LOC_DCSN_MTD"	,  szYD_TO_LOC_DCSN_MTD);
				//크레인작업재료 생성
				
				intRtnVal = commDao.insert(recInCrnMtl, "com.inisteel.cim.ys.common.dao.YsCommDAO.insYdCrnwrkmtl", logId, methodNm, "TB_YS_CRNWRKMTL 생성");
				if(intRtnVal <= 0) {
					szMsg = "크레인 스케줄 작업재료 등록중 실패: " + intRtnVal;
					commUtils.printLog(logId, szMsg, "SL");
					return YsConstant.RETN_CD_FAILURE;
				}
				
				if(intRtnVal <= 0) {
					commUtils.printLog(logId, "[" + methodNm + "] 적치단[" + recInCrn.getFieldString("YS_STK_COL_GP") + "]활성화중 ERROR 발생", "SL");
					return YsConstant.RETN_CD_FAILURE;
				}
			
			}	
			


			//크레인작업재료조회
			JDTORecordSet rsCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
    		JDTORecord recInData = JDTORecordFactory.getInstance().create();
    		
    		recInData.setField("YD_CRN_SCH_ID", ydCrnSchId);
    		
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
				szMsg =methodNm + "크레인스케쥴 재료 조회";
				commUtils.printLog(logId, szMsg, "SL");
				return YsConstant.RETN_CD_FAILURE;
    		}			
    		rsCrnwrkmtl.first();
    		JDTORecord recCrnwrkmtl = rsCrnwrkmtl.getRecord();
 
    		
			JDTORecord RecSetLoc = JDTORecordFactory.getInstance().create();
			RecSetLoc.setField("YD_CRN_SCH_ID", 	ydCrnSchId); 
			RecSetLoc.setField("YD_WBOOK_ID",   	szYD_WBOOK_ID);
			RecSetLoc.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
			RecSetLoc.setField("YS_UP_WO_LOC", 		szFR_YS_UP_WO_LOC); 
			RecSetLoc.setField("YS_UP_WO_LAYER",	szFR_YS_UP_WO_LAYER);	 
			RecSetLoc.setField("YS_DN_WO_LOC", 		szTO_YS_DN_WO_LOC);
			RecSetLoc.setField("YS_DN_WO_LAYER", 	szTO_YS_DN_WO_LAYER);

			RecSetLoc.setField("YD_TO_LOC_DCSN_MTD"	,szYD_TO_LOC_DCSN_MTD);
			RecSetLoc.setField("YD_AID_WRK_UPDN_GP"	,szYD_AID_WRK_UPDN_GP);
			RecSetLoc.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
			RecSetLoc.setField("YS_MERG_YN", 		szYS_MERG_YN);	 
			
			this.procUpdateLoc(logId, methodNms, recCrnwrkmtl, RecSetLoc );
	
			
			commUtils.printLog(logId, methodNm, "S-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return  ydCrnSchId;
        
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
	public String procMainWrkLoc(String logId, String methodNms, JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recWbook) throws JDTOException {
		String methodNm = "빌렛 주작업TO위치결정[BtYsSchSeEJB.procMainWrkLoc] < " + methodNms;
		String LocalmethodNm = "빌렛 주작업TO위치결정[BtYsSchSeEJB.procMainWrkLoc] " ;

		JDTORecordSet	rsResult		= null;
		JDTORecordSet	rsStock			= null;
		JDTORecordSet RsResultBed 	    = null;
		JDTORecord		recStock		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		JDTORecord		recCrnwrkmtl		= null;
		String szLogMsg					= null;
		String szYS_UP_WO_LOC			= null;
		String szYS_UP_WO_LAYER			= null;
		String szYS_DN_WO_LOC			= "";
		String szYS_DN_WO_LAYER			= null;
		String szYD_UP_STK_COL_GP		= null;
		String szYD_UP_STK_BED_NO		= null;

		JDTORecordSet outRsResult 		= null;
		JDTORecord    outRecResult 		= null;
		JDTORecord    RecResultBed 		= null;
		

		JDTORecord  recResult   = null;
		String szYD_STKBED_USG_CD = "";
		String szLocYS_STK_COL_GP = "";
		String szLocYS_STK_BED_NO = "";
		
		String sABLE_DCSN_MTD = "";      // 적치대 작업 방법 'Y':일반 ,'S':모음작업
		String szYS_MERG_YN = "";     
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		recCrnwrkmtl = rsCrnwrkmtl.getRecord();
		
		szLogMsg = "["+ LocalmethodNm +"] -------------------- 크레인작업재료의  단 최대SEQ 재료정보 확인 --------------------";
		commUtils.printLog(logId, szLogMsg, "SL");

		
		String szYD_CRN_SCH_ID  = commUtils.trim(recCrnwrkmtl.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
		String szSSTL_NO 		= commUtils.trim(recCrnwrkmtl.getFieldString("SSTL_NO"  ));			//크레인작업재료 중 최대 길이
		String szYD_CHG_NO  	= commUtils.trim(recCrnwrkmtl.getFieldString("YD_CHG_NO"  ));		//장입순번
	//	String szYD_EQP_WRK_SH  = commUtils.trim(recCrnwrkmtl.getFieldString("SH_CNT"  ));			//크레인작업재료 총매수

		String szYD_EQP_ID  	= commUtils.trim(recCrnSch.getFieldString("YD_EQP_ID"  ));			//크레인설비ID
		String szBED_USG_CD		= commUtils.trim(recCrnSch.getFieldString("BED_USG_CD"  ));			//적치대 용도 코드(권상위치 제외 용도)
		String szYD_SCH_PRIOR	= commUtils.trim(recCrnSch.getFieldString("YD_SCH_PRIOR"  ));	//분리작업시 필요
		String szYD_WBOOK_ID	= commUtils.trim(recCrnSch.getFieldString("YD_WBOOK_ID"  ));	//분리작업시 필요
		
		String szYD_TO_LOC_DCSN_MTD	= commUtils.trim(recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"  ));	
		int intMTL_CNT 			= Integer.parseInt(commUtils.nvl(recCrnwrkmtl.getFieldString("SH_CNT"),"0"));
		commUtils.printParam(logId, recWbook);
		
		szLogMsg = "["+ LocalmethodNm +"] -------------------- 작업예약정보 확인 --------------------";
		commUtils.printLog(logId, szLogMsg, "SL");
		
		String szYD_TO_LOC_GUIDE= commUtils.trim(recWbook.getFieldString("YD_TO_LOC_GUIDE"  ));		//사용자지정위치
		String szYD_SCH_CD 		= commUtils.trim(recWbook.getFieldString("YD_SCH_CD"  ));			//크레인스케줄코드
		
		//----------------------------------------------------------------------------------------------------------------------
		//	야드 저장품 정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ LocalmethodNm +"] 크레인작업재료의  단 최대SEQ 재료정보["+szSSTL_NO+"]를 저장품에서 조회 시작 ";
		commUtils.printLog(logId, szLogMsg, "SL");
		
		
		rsStock = commDao.select(recCrnwrkmtl, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStock", logId, methodNm, "저장품 조회");
		if (rsStock.size() <= 0) {
			szLogMsg = "["+ methodNm +"]  재료정보["+szSSTL_NO+"]를 저장품에서 조회 실패 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			return YsConstant.RETN_CD_FAILURE;
		}
		
		rsStock.first();
		recStock = rsStock.getRecord();
		
		String szYD_MTL_T_GP 	= commUtils.trim(recStock.getFieldString("YD_MTL_T_GP"  ));		//크레인작업 최하단재료의두께구분
		String szYD_MTL_L_GP 	= commUtils.trim(recStock.getFieldString("YD_MTL_L_GP"  ));		//크레인작업 최하단재료의길이구분
		String szYD_MTL_W_GP 	= commUtils.trim(recStock.getFieldString("YD_MTL_W_GP"  ));		//크레인작업 최하단재료의폭구분
		String szHEAT_NO   		= commUtils.trim(recStock.getFieldString("HEAT_NO"  ));			//HEAT_NO
		String szYD_STK_LOT_CD	= commUtils.trim(recStock.getFieldString("YD_STK_LOT_CD"));
		
		szLogMsg = "["+ LocalmethodNm +"] 크레인작업재료의  단 최대SEQ ["+szSSTL_NO+"]를 저장품에서 조회 완료 - 두께구분["+szYD_MTL_T_GP+"], 폭구분["+szYD_MTL_W_GP+"], 산적LOT CD["+szYD_STK_LOT_CD+"], HEAT_NO["+szHEAT_NO+"]";
		commUtils.printLog(logId, szLogMsg, "SL");

		//----------------------------------------------------------------------------------------------------------------------
		//	권상중이거나 권하중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYS_UP_WO_LOC 		= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LOC"  ));		
		szYS_UP_WO_LAYER 	= commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LAYER"  ));		

		if( szYS_UP_WO_LOC.equals("") ) {
			
			szLogMsg = "["+ LocalmethodNm +"] 크레인작업재료의 최하단 재료정보["+szSSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
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
			
			szLogMsg = "["+ LocalmethodNm +"] 크레인작업재료의  단 최대SEQ 재료정보["+szSSTL_NO+"]에 대한 권하 또는 권상위치 조회 완료  ";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YS_STK_COL_GP");
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YS_STK_BED_NO");
			szYS_UP_WO_LOC 			= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYS_UP_WO_LAYER 		= recTemp.getFieldString("YS_STK_LYR_NO");
			
			szLogMsg = "["+ LocalmethodNm +"] 조회된 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]";
			commUtils.printLog(logId, szLogMsg, "SL");
		
		} else if (szYS_UP_WO_LOC.substring(2, 8).equals("TYXXXX")) {
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchAll
			SELECT A.YD_CRN_SCH_ID
			     , A.YD_EQP_ID
			     , A.YD_SCH_CD
			     , B.SSTL_NO
			     , A.YS_UP_WO_LOC
			     , A.YS_UP_WO_LAYER
			  FROM TB_YS_CRNSCH A
			     , TB_YS_CRNWRKMTL B
			 WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
			   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
	
			*/
			recPara = JDTORecordFactory.getInstance().create();
			recTemp = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
			
			rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchAll", logId, methodNm, "크레인 재료 정보 조회");
			if (rsResult.size() <= 0) {
				return "0";
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YS_UP_WO_LOC").substring(0, 6);
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YS_UP_WO_LOC").substring(6);
			szYS_UP_WO_LOC 			= recTemp.getFieldString("YS_UP_WO_LOC");
			szYS_UP_WO_LAYER 		= recTemp.getFieldString("YS_UP_WO_LAYER");

		}else{
		
			szYD_UP_STK_COL_GP = szYS_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYS_UP_WO_LOC.substring(6);
			
			szLogMsg = "["+ LocalmethodNm +"] 크레인스케줄에 등록된 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]";
			commUtils.printLog(logId, szLogMsg, "SL");
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	재료와 같은 동일HEAT,동일강종 의 적치가능한 베드 해당 동의 모든 위치를 조회 
		//----------------------------------------------------------------------------------------------------------------------
		
		//권상지시위치에 따라 알맞은 적치가능한 베드 검색 방법을 적용
    	recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("YS_STK_COL_GP", 		szYD_UP_STK_COL_GP);								//권상지시위치 - 적치열
    	recTemp.setField("YS_STK_BED_NO", 		szYD_UP_STK_BED_NO);								//권상지시위치 - 적치베드
    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);										//크레인 스케줄코드
    	recTemp.setField("HEAT_NO", 			szHEAT_NO);											//크레인작업 최상단재료의 HEAT_NO
    	recTemp.setField("YD_MTL_L_GP", 		szYD_MTL_L_GP);										//크레인작업 최상단재료의 길이구분
    	recTemp.setField("YD_MTL_W_GP", 		szYD_MTL_W_GP);										//크레인작업 최상단재료의 폭구분
    	recTemp.setField("YD_MTL_T_GP", 		szYD_MTL_T_GP);										//크레인작업 최상단재료의 두께구분
    	recTemp.setField("YD_EQP_ID", 			szYD_EQP_ID);										//크레인설비ID
       	recTemp.setField("YD_STK_LOT_CD", 		szYD_STK_LOT_CD);									//크레인작업 산적LOT코드
    	recTemp.setField("BED_USG_CD", 			szBED_USG_CD);										//적치대 용도 코드(권상위치 제외 용도)
    	recTemp.setField("YD_CHG_NO", 			szYD_CHG_NO);										//권상 MIN(장입순번)

    	recTemp.setField("YD_UP_STK_LOC", 		szYD_UP_STK_COL_GP+szYD_UP_STK_BED_NO);		
    	//----------------------------------------------------------------------------------------------------------------------
		//	동일한  HEAT_NO의 적치가능한 베드 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ LocalmethodNm +"] 동일한사양  HEAT_NO: ["+szHEAT_NO+"] 산적LOT CD: ["+szYD_STK_LOT_CD+"] 의 적치가능한 베드 조회 시작";
		commUtils.printLog(logId, szLogMsg, "SL");
		/**********************************************************************
		* 적치대  TO 위치 가이드 처리
		***********************************************************************/     
		
		if((szYD_TO_LOC_GUIDE.length() == 6)|| (szYD_TO_LOC_GUIDE.length() == 8)) {
			recTemp.setField("YD_TO_LOC_GUIDE", 		szYD_TO_LOC_GUIDE);	
			
			szLogMsg = "["+ LocalmethodNm +"] TOSQL:["+szYD_CRN_SCH_ID+ "]  적치대TO위치결정 적재위치 가이드  YD_TO_LOC_GUIDE ["+szYD_TO_LOC_GUIDE+"]의 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			/**********************************************************************
			* 열+ 베드  지정된 경우 -> 화면 
			***********************************************************************/            		

			RsResultBed = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLocGuideBt", logId, methodNm, "가이드  COL , BED 조회");
			if (RsResultBed.size() <= 0) {
				szLogMsg = "가이드  COL , BED 조회 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				szYS_DN_WO_LOC = "";
			} else {
				
				RsResultBed.first();
				RecResultBed = RsResultBed.getRecord();
				
				szYD_STKBED_USG_CD 	= commUtils.trim(RecResultBed.getFieldString("YD_STKBED_USG_CD"  ));		//적치대 상태
				szLocYS_STK_COL_GP  = commUtils.trim(RecResultBed.getFieldString("YS_STK_COL_GP"  ));
				szLocYS_STK_BED_NO  = commUtils.trim(RecResultBed.getFieldString("YS_STK_BED_NO"  ));
				
//				recResult = this.procLocAbleCheck(logId, methodNms,szLocYS_STK_COL_GP ,szLocYS_STK_BED_NO,szYD_STKBED_USG_CD,szYD_CHG_NO) ;
				JDTORecord recTemp1 = JDTORecordFactory.getInstance().create();
				recTemp1.setField("YD_UP_STK_COL_GP", 	szYD_UP_STK_COL_GP);		
				recTemp1.setField("LOC_YS_STK_COL_GP", 	szLocYS_STK_COL_GP);		
				recTemp1.setField("LOC_YS_STK_BED_NO", 	szLocYS_STK_BED_NO);		
				recTemp1.setField("YD_STKBED_USG_CD", 	szYD_STKBED_USG_CD);		
				recTemp1.setField("YD_CHG_NO", 			szYD_CHG_NO);		
				recTemp1.setField("YD_TO_LOC_DCSN_MTD", szYD_TO_LOC_DCSN_MTD);		
				recTemp1.setField("YD_CHG_NO_CHK_YN", 	"N");	
				recTemp1.setField("MTL_CNT", 			""+intMTL_CNT);	
				recTemp1.setField("YD_SCH_CD", 			szYD_SCH_CD);
				
				recResult = this.procLocAbleCheckCnt(logId, methodNms, recTemp1) ;
				
				if(commUtils.trim(recResult.getFieldString("ABLE_YN"  )).equals("Y")){
					sABLE_DCSN_MTD 		= commUtils.trim(recResult.getFieldString("ABLE_DCSN_MTD"));
					szYS_DN_WO_LOC 		= commUtils.trim(recResult.getFieldString("YS_DN_WO_LOC"  )); //CA0104		
					szYS_DN_WO_LAYER 	= commUtils.trim(recResult.getFieldString("YS_DN_WO_LAYER"  )); //02
					szYS_MERG_YN 		= commUtils.trim(recResult.getFieldString("YS_MERG_YN"  ));  //Y:단순 합적가능(낱본이 안남는 경우로 3번 크레인 작업 진행) , S:복합 합적가능(낱본이 남는 경우로 4번 크레인 작업 진행)
				}
			}	
		} else if(szYD_TO_LOC_GUIDE.length() == 10) {
			/**********************************************************************
			* 열+ 베드+ 단  지정된 경우 -> 차량상차, 대차 상차 
			***********************************************************************/            		
			szLogMsg = "["+ LocalmethodNm +"] 적재위치 가이드 열+ 베드+ 단  지정된 경우 -> 차량상차 ["+szYD_TO_LOC_GUIDE+"]의 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");

			szYS_DN_WO_LOC 		= szYD_TO_LOC_GUIDE.substring(0,8);
			szYS_DN_WO_LAYER 	= szYD_TO_LOC_GUIDE.substring(8,10);				
		}
	
		String sABLE_YS_DN_WO_LOC = "";
		String sABLE_YS_DN_WO_LAYER = "";
		
		if(szYS_DN_WO_LOC.equals("")) {
	   		/**********************************************************************
			* TO 위치 가이드  가 아닌 경우
			***********************************************************************/            		
	    	//----------------------------------------------------------------------------------------------------------------------
	    	// 동일한  사양의 적치가능한 베드 조회
			//----------------------------------------------------------------------------------------------------------------------
			szLogMsg = "["+ LocalmethodNm +"] TOSQL:["+szYD_CRN_SCH_ID+ "] 동일한사양  HEAT_NO: ["+szHEAT_NO+"] 산적LOT CD: ["+szYD_STK_LOT_CD+"] 의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
				
			outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getToYardMoveBt", logId, methodNm, "동일한 사양의 적치가능한 베드 조회");
			if (outRsResult.size() <= 0) {
				szLogMsg = "["+ LocalmethodNm +"] 동일한  사양의 적치가능한 베드 조회 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YsConstant.RETN_CD_FAILURE;
			}

		    
		    // 적치 가능 여부 CEHCK	
			for(int i = 1; i <= outRsResult.size(); i++) {

				outRsResult.absolute(i);
				outRecResult  = outRsResult.getRecord();
				
				szYD_STKBED_USG_CD 	= commUtils.trim(outRecResult.getFieldString("YD_STKBED_USG_CD"  ));		//적치대 상태
				szLocYS_STK_COL_GP  = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));
				szLocYS_STK_BED_NO  = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));
				
//				if (szYD_SCH_CD.substring(2,4).equals("TF") && szYD_SCH_CD.substring(6,7).equals("L") && !szYD_STKBED_USG_CD.equals("V1")){
					
				JDTORecord recTemp1 = JDTORecordFactory.getInstance().create();
				recTemp1.setField("YD_UP_STK_COL_GP", 			szYD_UP_STK_COL_GP);		
				recTemp1.setField("LOC_YS_STK_COL_GP", 			szLocYS_STK_COL_GP);		
				recTemp1.setField("LOC_YS_STK_BED_NO", 			szLocYS_STK_BED_NO);		
				recTemp1.setField("YD_STKBED_USG_CD", 			szYD_STKBED_USG_CD);		
				recTemp1.setField("YD_CHG_NO", 					szYD_CHG_NO);		
				recTemp1.setField("YD_TO_LOC_DCSN_MTD", 		szYD_TO_LOC_DCSN_MTD);		
				recTemp1.setField("YD_CHG_NO_CHK_YN", 			"Y");	
				recTemp1.setField("MTL_CNT", 					""+intMTL_CNT);	
				recTemp1.setField("YD_SCH_CD", 					szYD_SCH_CD);
				
				recResult = this.procLocAbleCheckCnt(logId, methodNms, recTemp1) ;

				if(commUtils.trim(recResult.getFieldString("ABLE_YN")).equals("Y")){
					
					sABLE_DCSN_MTD 	= commUtils.trim(recResult.getFieldString("ABLE_DCSN_MTD"));
					szYS_DN_WO_LOC 	= commUtils.trim(recResult.getFieldString("YS_DN_WO_LOC"  )); 		//현재 스케쥴 TO위치
					szYS_DN_WO_LAYER= commUtils.trim(recResult.getFieldString("YS_DN_WO_LAYER"  ));   //현재 스케쥴 TO위치
					szYS_MERG_YN 	= commUtils.trim(recResult.getFieldString("YS_MERG_YN"  ));  //Y:단순 합적가능(낱본이 안남는 경우로 3번 크레인 작업 진행) , S:복합 합적가능(낱본이 남는 경우로 4번 크레인 작업 진행)
					break;
				} else {
					continue;
				}
			}			
		}
		
		commUtils.printLog(logId, "모음작업 (" + sABLE_DCSN_MTD+ ")"+"합적작업 (" + szYS_MERG_YN+ ")", "SL");
		

		JDTORecord RecSetLoc = JDTORecordFactory.getInstance().create();
		RecSetLoc.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
		RecSetLoc.setField("YD_EQP_ID", 		szYD_EQP_ID);	
		RecSetLoc.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
		RecSetLoc.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);	 
		RecSetLoc.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);	 
		RecSetLoc.setField("YS_UP_WO_LOC", 		szYS_UP_WO_LOC); 
		RecSetLoc.setField("YS_UP_WO_LAYER",	szYS_UP_WO_LAYER);	 
		RecSetLoc.setField("YS_DN_WO_LOC", 		szYS_DN_WO_LOC);        
		RecSetLoc.setField("YS_DN_WO_LAYER", 	szYS_DN_WO_LAYER);
		RecSetLoc.setField("YD_TO_LOC_DCSN_MTD", 	szYD_TO_LOC_DCSN_MTD);
		RecSetLoc.setField("YD_AID_WRK_UPDN_GP", 	"1");
		
		if(!sABLE_DCSN_MTD.equals("S") ) {  //모음작업 추가됨
			this.procUpdateLoc(logId,methodNms,recCrnwrkmtl  ,RecSetLoc );
			
		} else {
		
			String ydCrnSchIdNext1 = "";
			String ydCrnSchIdNext2 = "";
			String ydCrnSchIdNext3 = "";
			
			sABLE_YS_DN_WO_LOC 		= commUtils.trim(recResult.getFieldString("ABLE_YS_DN_WO_LOC"));   //검색된 적재가능 위치
			sABLE_YS_DN_WO_LAYER 	= commUtils.trim(recResult.getFieldString("ABLE_YS_DN_WO_LAYER")); //검색된 적재가능 위치

			for(int i = 1; i <= 4; i++) {
				commUtils.printLog(logId, "모음작업 (" + i + ")$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$", "SL");
				RecSetLoc.setField("YD_TO_LOC_DCSN_MTD", 	"S");
				RecSetLoc.setField("YD_AID_WRK_UPDN_GP", 	"" + i);
				RecSetLoc.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID);          			//재료정보 필요
				RecSetLoc.setField("YD_CRN_SCH_NEXT1", 	ydCrnSchIdNext1);          			//재료정보 필요
				RecSetLoc.setField("YD_CRN_SCH_NEXT2", 	ydCrnSchIdNext2);          			//재료정보 필요
				
				if(i == 1) {
					
					// 이적대상  -> 임시BED로  &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
					this.procUpdateLoc(logId,methodNms, recCrnwrkmtl  ,RecSetLoc );
					//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
					
				} else if(i == 2) {	
				
					// 검색된 적재위치 -> 임시BED로   &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& 
					RecSetLoc.setField("YS_MERG_YN", 		szYS_MERG_YN);
					RecSetLoc.setField("YS_UP_WO_LOC", 		sABLE_YS_DN_WO_LOC);            //권상위치(검색된 적재가능 위치)
					RecSetLoc.setField("YS_UP_WO_LAYER",	sABLE_YS_DN_WO_LAYER);	        //권상위치(검색된 적재가능 위치)
					
				    //Y:단순 합적가능(낱본이 안남는 경우로 3번 크레인 작업 진행) , S:복합 합적가능(낱본이 남는 경우로 4번 크레인 작업 진행)					
//					if("Y".equals(szYS_MERG_YN)){
//						RecSetLoc.setField("YS_DN_WO_LOC", 		szYS_DN_WO_LOC);            //임시 BED로 
//					}else {
//						RecSetLoc.setField("YS_DN_WO_LOC", 		sABLE_YS_DN_WO_LOC.substring(0,2) + "TY");            //임시 BED로 
//					}
					 
					RecSetLoc.setField("YS_DN_WO_LOC", 		sABLE_YS_DN_WO_LOC.substring(0,2) + "TY");            //임시 BED로 
					RecSetLoc.setField("YS_DN_WO_LAYER", 	"01");      //권하위치
					
					// 2:TO위치 결정 보조작업 추가  
					ydCrnSchIdNext1 = this.procCrnWrkAdd(logId, methodNms, RecSetLoc);
					if( ydCrnSchIdNext1.equals(YsConstant.RETN_CD_FAILURE) ) {
						return YsConstant.RETN_CD_FAILURE;
					}
					//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

				} else if(i == 3) {	
					
					// 인출/장입 대상 임시BED   -> 검색된 적재위치 &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
					
					//Y:단순 합적가능(낱본이 안남는 경우로 3번 크레인 작업 진행) , S:복합 합적가능(낱본이 남는 경우로 4번 크레인 작업 진행)					
					if("Y".equals(szYS_MERG_YN)){
						RecSetLoc.setField("YS_UP_WO_LOC", 		szYS_DN_WO_LOC);            //임시 BED로 권상위치
					}else {
						RecSetLoc.setField("YS_UP_WO_LOC", 		sABLE_YS_DN_WO_LOC.substring(0,2) + "TY");            //임시 BED로 권상위치
					} 
					
					RecSetLoc.setField("YS_UP_WO_LAYER",	"01");	        //권상위치
					RecSetLoc.setField("YS_DN_WO_LOC", 		sABLE_YS_DN_WO_LOC);            //권하위치
					RecSetLoc.setField("YS_DN_WO_LAYER", 	sABLE_YS_DN_WO_LAYER);          //권하위치
					
					// 3:원위치 결정 보조작업 추가  
					ydCrnSchIdNext2 = this.procCrnWrkAdd(logId, methodNms, RecSetLoc);
					if( ydCrnSchIdNext2.equals(YsConstant.RETN_CD_FAILURE) ) {
						return YsConstant.RETN_CD_FAILURE;
					}
					//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

				} else if(i == 4) {	
					
					// 인출/장입 대상 임시BED 남아 있는 대상   -> 검색된 적재위치 &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& 
					if( szYD_SCH_CD.substring(2,4).equals("TF") && szYD_SCH_CD.substring(6,7).equals("L") && "S".equals(szYS_MERG_YN) ) {
						// 인출/장입 대상 임시BED   -> 검색된 적재위치
						RecSetLoc.setField("YS_MERG_YN", 		szYS_MERG_YN);
						RecSetLoc.setField("YS_UP_WO_LOC", 		sABLE_YS_DN_WO_LOC.substring(0,2) + "TY");            //임시 BED로 권상위치 
						RecSetLoc.setField("YS_UP_WO_LAYER",	"01");	        //권상위치
						RecSetLoc.setField("YS_DN_WO_LOC", 		sABLE_YS_DN_WO_LOC);            //권하위치
						RecSetLoc.setField("YS_DN_WO_LAYER", 	commUtils.stringPlusInt(""+sABLE_YS_DN_WO_LAYER, 1));          //권하위치
						
						
						// 3:원위치 결정 보조작업 추가  
						ydCrnSchIdNext3 = this.procCrnWrkAdd(logId, methodNms, RecSetLoc);
						if( ydCrnSchIdNext3.equals(YsConstant.RETN_CD_FAILURE) ) {
							return YsConstant.RETN_CD_FAILURE;
						} else if( ydCrnSchIdNext3.equals(YsConstant.RETN_CD_SUCCESS) ) {
	                      // 작업이 없는 경우가 있음
						}
					} else {
						// TY BED에서 남아 있는 재료 정보 READ 하여 권하위치 생성
						
						RecSetLoc.setField("YS_UP_WO_LOC", 		sABLE_YS_DN_WO_LOC.substring(0,2) + "TY");            //임시 BED로 권상위치
						RecSetLoc.setField("YS_UP_WO_LAYER",	"01");	        //권상위치
						RecSetLoc.setField("YS_DN_WO_LOC", 		sABLE_YS_DN_WO_LOC);            //권하위치
						RecSetLoc.setField("YS_DN_WO_LAYER", 	commUtils.stringPlusInt(""+sABLE_YS_DN_WO_LAYER, 1));          //권하위치
						
						
						// 3:원위치 결정 보조작업 추가  
						ydCrnSchIdNext3 = this.procCrnWrkAdd(logId, methodNms, RecSetLoc);
						if( ydCrnSchIdNext3.equals(YsConstant.RETN_CD_FAILURE) ) {
							return YsConstant.RETN_CD_FAILURE;
						} else if( ydCrnSchIdNext3.equals(YsConstant.RETN_CD_SUCCESS) ) {
	                      // 작업이 없는 경우가 있음
						}
					}
					//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

				}		
			}	
		}
		
		//----------------------------------------------------------------------------------------------------------------------
    	// ERROR 발생시 ?
		//----------------------------------------------------------------------------------------------------------------------
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
	public String procUpdateLoc(String logId, String methodNms, JDTORecord recCrnwrkmtl, JDTORecord RecSetLoc) throws JDTOException {
		String methodNm = "빌렛TO위치 UPDATE[BtYsSchSeEJB.procUpdateLoc] < " + methodNms;
		String LocalmethodNm = "빌렛TO위치 UPDATE[BtYsSchSeEJB.procUpdateLoc]";
		String szLogMsg					= null;
		String szRtnMsg 				= YsConstant.RETN_CD_SUCCESS;
		JDTORecord		recInBed		= null;
		int intRtnVal					= 0;
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String szYD_EQP_WRK_MAX_W 	= commUtils.trim(recCrnwrkmtl.getFieldString("MAX_MTL_W"  ));		//크레인작업재료 중 최대 폭
		String szYD_EQP_WRK_MAX_L 	= commUtils.trim(recCrnwrkmtl.getFieldString("MAX_MTL_L"  ));		//크레인작업재료 중 최대 길이
		int intYD_EQP_WRK_SH    	= commUtils.paraRecChkNullInt(recCrnwrkmtl,"SH_CNT");				//크레인작업재료 총매수
		int intYD_EQP_WRK_WT    	= commUtils.paraRecChkNullInt(recCrnwrkmtl,"SUM_MTL_WT");			//크레인작업재료 총중량
		double dblYD_EQP_WRK_T     	= commUtils.paraRecChkNullDouble(recCrnwrkmtl,"SUM_MTL_T");			//크레인작업재료 총높이
	
		String szYD_CRN_SCH_ID  	= commUtils.trim(RecSetLoc.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
		String szYD_EQP_ID  		= commUtils.trim(RecSetLoc.getFieldString("YD_EQP_ID"  ));	
		String szYS_UP_WO_LOC		= commUtils.trim(RecSetLoc.getFieldString("YS_UP_WO_LOC"  ));			
		String szYS_UP_WO_LAYER		= commUtils.trim(RecSetLoc.getFieldString("YS_UP_WO_LAYER"  ));			
		String szYS_DN_WO_LOC		= commUtils.trim(RecSetLoc.getFieldString("YS_DN_WO_LOC"  ));			
		String szYS_DN_WO_LAYER		= commUtils.trim(RecSetLoc.getFieldString("YS_DN_WO_LAYER"  ));			

		String szYD_TO_LOC_DCSN_MTD	= commUtils.trim(RecSetLoc.getFieldString("YD_TO_LOC_DCSN_MTD"  ));			
		String szYD_AID_WRK_UPDN_GP	= commUtils.trim(RecSetLoc.getFieldString("YD_AID_WRK_UPDN_GP"  ));			
		String szYD_WBOOK_ID		= commUtils.trim(RecSetLoc.getFieldString("YD_WBOOK_ID"  ));
		String szMODIFIER 			= commUtils.trim(recCrnwrkmtl.getFieldString("MODIFIER"  ));		//MODIFIER
		String szYD_SCH_CD 		    = commUtils.trim(RecSetLoc.getFieldString("YD_SCH_CD"  ));			//크레인스케줄코드
		String szYS_MERG_YN		    = commUtils.trim(RecSetLoc.getFieldString("YS_MERG_YN"  ));	 
		
		if (szYS_DN_WO_LOC.equals("")) {
			return YsConstant.RETN_CD_FAILURE;
		}
	
		commUtils.printParam(logId, RecSetLoc);
		//----------------------------------------------------------------------------------------------------------------------
		// 권하지시위치 수정
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ LocalmethodNm +"] 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]을 크레인스케줄에 수정 시작";
		commUtils.printLog(logId, szLogMsg, "SL");
		
		JDTORecordSet RsBedUpXy = JDTORecordFactory.getInstance().createRecordSet("");
		recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YS_STK_COL_GP", 			szYS_UP_WO_LOC.substring(0, 6)); //권상지시위치
		recInBed.setField("YS_STK_BED_NO", 			szYS_UP_WO_LOC.substring(6));	 //권상지시위치
		recInBed.setField("YS_DAN", 				szYS_UP_WO_LAYER);	 //권상지시위치
			
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
		      ,YD_STK_BED_XAXIS1
		      ,YD_STK_BED_YAXIS1
		      ,YD_STK_BED_ZAXIS1 
		      ,CASE WHEN MOD(:V_YS_DAN,2) = 0 THEN 0
		             ELSE 1 END DAN_GP
		  FROM TB_YS_STKBED A
		 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
		   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
		   AND DEL_YN ='N'
			 */  
		RsBedUpXy = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybed", logId, methodNm, "권상 BED 좌표 조회");
		if (RsBedUpXy.size() <= 0) {
			szLogMsg =  "["+ LocalmethodNm +"] 권상 BED 좌표 조회 검색 실패 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			
		}
		RsBedUpXy.first();
		JDTORecord RecUpBedXy = RsBedUpXy.getRecord();

		JDTORecordSet RsDnBedXy = JDTORecordFactory.getInstance().createRecordSet("");
		recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YS_STK_COL_GP", 			szYS_DN_WO_LOC.substring(0, 6));										//권하지시위치
		recInBed.setField("YS_STK_BED_NO", 			szYS_DN_WO_LOC.substring(6));										//권하지시위치
		recInBed.setField("YS_DAN", 				szYS_DN_WO_LAYER);	 //권상지시위치	
		
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
		      ,YD_STK_BED_XAXIS1
		      ,YD_STK_BED_YAXIS1
		      ,YD_STK_BED_ZAXIS1 
		      ,CASE WHEN MOD(:V_YS_DAN,2) = 0 THEN 0
		             ELSE 1 END DAN_GP
		  FROM TB_YS_STKBED A
		 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
		   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
		   AND DEL_YN ='N'
			 */  
		RsDnBedXy = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybed", logId, methodNm, "권하 BED 좌표 조회");
		if (RsDnBedXy.size() <= 0) {
			szLogMsg =  "["+ LocalmethodNm +"] 권하 BED 좌표 검색 실패 ";
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
		
		if(commUtils.trim(RecUpBedXy.getFieldString("YD_STK_COL_DIR_GP")).equals("V1") && commUtils.trim(RecUpBedXy.getFieldString("DAN_GP")).equals("2") ) {
			recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS1"  ))) ;
			recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS1"  ))) ;
			recUpCrnSch.setField("YD_UP_WO_LOC_ZAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_ZAXIS1"  )) ) ;
			
		} else {
			recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
			recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
			recUpCrnSch.setField("YD_UP_WO_LOC_ZAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_ZAXIS"  )) ) ;
		}
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
		
		if(commUtils.trim(RecDnBedXy.getFieldString("YD_STK_COL_DIR_GP")).equals("V1") && commUtils.trim(RecDnBedXy.getFieldString("DAN_GP")).equals("2") ) {
			recUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS1"  )) ) ;
			recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS1"  )) ) ;
			recUpCrnSch.setField("YD_DN_WO_LOC_ZAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_ZAXIS1"  )) ) ;
			
		} else {
			recUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS"  )) ) ;
			recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS"  )) ) ;
			recUpCrnSch.setField("YD_DN_WO_LOC_ZAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_ZAXIS"  )) ) ;
		}
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
	
		if(szYD_TO_LOC_DCSN_MTD.equals("S")) {
			recUpCrnSch.setField("YD_TO_LOC_DCSN_MTD", 					szYD_TO_LOC_DCSN_MTD);
			recUpCrnSch.setField("YD_AID_WRK_UPDN_GP", 					szYD_AID_WRK_UPDN_GP);
		}
		
		intRtnVal = commDao.update(recUpCrnSch, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCrnWrkSidedelyn", logId, methodNm, "크레인스케쥴 갱신");
		
		if(intRtnVal <= 0) {
			szLogMsg = "["+ LocalmethodNm +"] 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단[" +szYS_DN_WO_LAYER +" ]을 크레인스케줄에 수정 중 ERROR 발생";
			commUtils.printLog(logId, szLogMsg, "SL");
			return YsConstant.RETN_CD_FAILURE;
		}		
		
    	szLogMsg = "["+ LocalmethodNm +"] 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]을 크레인스케줄에 수정 완료";
		commUtils.printLog(logId, szLogMsg, "SL");

		//----------------------------------------------------------------------------------------------------------------------
		//	권하지시위치에 재료를 권하대기로 등록
		//  권상위치 정보 READ 하여 권하 위치 SET
		//----------------------------------------------------------------------------------------------------------------------
    	szLogMsg = "["+ LocalmethodNm +"] 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]에 크레인작업재료 등록 시작";
		commUtils.printLog(logId, szLogMsg, "SL");

		JDTORecordSet rsOutBed 	=  JDTORecordFactory.getInstance().createRecordSet("");		
		JDTORecord resOutBed	= JDTORecordFactory.getInstance().create();
		JDTORecord resTYBed		= JDTORecordFactory.getInstance().create();
		
		
		recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);									
		recInBed.setField("YS_STK_COL_GP", 			szYS_UP_WO_LOC.substring(0, 6));										
		recInBed.setField("YS_STK_BED_NO", 			szYS_UP_WO_LOC.substring(6));										
		recInBed.setField("YS_STK_LYR_NO", 			commUtils.stringPlusInt(szYS_UP_WO_LAYER,0));										

		
//01.13 추가
//장입이면서 권상위치가  TY 이고 권하위치가 장입대 인 경우
//크레인 작업지시 SEQ 변경처리		
//권하위치를 장입순으로	업데이트			
		if(szYS_UP_WO_LOC.substring(2, 4).equals("TY") 
				&& szYS_DN_WO_LOC.substring(2, 4).equals("TZ") 
				&& (szYD_SCH_CD.substring(2,4).equals("TZ") && szYD_SCH_CD.substring(6,7).equals("U"))){
			
			szLogMsg = "["+ LocalmethodNm +"] 장입이면서 권상위치가  TY 이고 권하위치가 장입대 인 경우 권하위치를 장입순으로	";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyrTZ    
			-- 장입대권하시 재배열(장입순번 필요)
			WITH PARA_TBL1 AS ( 
			      SELECT SUBSTR(YD_SCH_CD,3,2) AS YD_SCH
			                     FROM TB_YS_CRNSCH 
			                    WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			    ) 
			SELECT A.YS_STK_COL_GP            AS YS_STK_COL_GP
			     , A.YS_STK_BED_NO            AS YS_STK_BED_NO
			     , A.YS_STK_LYR_NO            AS YS_STK_LYR_NO
			     , decode(YD_SCH ,'YD',A.YS_STK_SEQ_NO,ROW_NUMBER() OVER(ORDER BY NVL(B.YD_CHG_NO,'0'), A.YS_STK_SEQ_NO)) AS YS_STK_SEQ_NO
			     , A.SSTL_NO                   AS SSTL_NO
			     , B.YD_CHG_NO
			  FROM TB_YS_STKLYR A
			     , TB_YS_STOCK B
			     , PARA_TBL1
			 WHERE A.SSTL_NO = B.SSTL_NO
			   AND A.YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND A.YS_STK_BED_NO = :V_YS_STK_BED_NO
			   AND A.YS_STK_LYR_NO = :V_YS_STK_LYR_NO
			   AND A.SSTL_NO IS NOT NULL
			   AND A.SSTL_NO IN (SELECT SSTL_NO FROM TB_YS_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
			   AND A.DEL_YN = 'N'
			  ORDER BY decode(YD_SCH ,'YD','0',NVL(B.YD_CHG_NO,'0')), A.YS_STK_SEQ_NO
			*/  
		
			rsOutBed 	= commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyrTZ", logId, methodNm, "권상정보 검색");
			if (rsOutBed.size() <= 0) {
				szLogMsg = "["+ LocalmethodNm +"] 권하위치를 장입순으로	권상정보 BED 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
			
			} else {
				JDTORecord jrParam1		= JDTORecordFactory.getInstance().create();;
				for(int i = 1; i <= rsOutBed.size(); i++) {
					rsOutBed.absolute(i);
					resTYBed  = rsOutBed.getRecord();
					jrParam1.setField("MODIFIER"	 ,	szMODIFIER);
					jrParam1.setField("YD_CRN_SCH_ID",	commUtils.trim(resTYBed.getFieldString("YD_CRN_SCH_ID")));
					jrParam1.setField("SSTL_NO"		 ,	commUtils.trim(resTYBed.getFieldString("SSTL_NO")));
					jrParam1.setField("YS_STK_SEQ_NO",  commUtils.trim(resTYBed.getFieldString("YS_STK_SEQ_NO")));

					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCrnwrkmtlSeq 
					UPDATE TB_YS_CRNWRKMTL
					   SET MOD_DDTT = SYSDATE      
					     , MODIFIER = :V_MODIFIER
					     , YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO
					 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					   AND SSTL_NO       = :V_SSTL_NO  
			    	 */  
					intRtnVal = commDao.update(jrParam1, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCrnwrkmtlSeq", logId, methodNm, "TB_YS_CRNWRKMTL 갱신");
					
					if(intRtnVal <= 0) {
						commUtils.printLog(logId, "[" + LocalmethodNm + "] 크레인작업재료 UPDATE ERROR 발생", "SL");
						return YsConstant.RETN_CD_FAILURE;
					}
				}				
			}
			
		} else 
//01.13 추가	완료		
		// 모음 작업	
		if(szYD_TO_LOC_DCSN_MTD.equals("S")) {
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyrBtTy 
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
			     , TB_YS_CRNWRKMTL D  
			 WHERE A.SSTL_NO = B.SSTL_NO
			   AND A.SSTL_NO = D.SSTL_NO
			   AND D.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			   AND A.YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND A.SSTL_NO IS NOT NULL 
			   AND A.DEL_YN = 'N'
				   */
			
			rsOutBed 	= commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyrBtTy", logId, methodNm, "권상정보 검색");
			if (rsOutBed.size() <= 0) {
				szLogMsg = "["+ LocalmethodNm +"] 모음 권상정보 BED 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
			}			
		} else {
				
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
			
			rsOutBed 	= commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyr", logId, methodNm, "권상정보 검색");
			if (rsOutBed.size() <= 0) {
				szLogMsg = "["+ LocalmethodNm +"] 권상정보 BED 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
			}
		}	
		
		JDTORecordSet rsMaxSeq 	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsMaxSeq1 = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recPara		= JDTORecordFactory.getInstance().create();
		JDTORecord recPara1 	= JDTORecordFactory.getInstance().create();
		JDTORecord recInPara1 	= JDTORecordFactory.getInstance().create();
		JDTORecord jrParam		= JDTORecordFactory.getInstance().create();
		
		String szLYR_UPDATE_YN  = "";
		
		// 모음 작업 (S) 추가
		if(szYD_TO_LOC_DCSN_MTD.equals("C")||szYD_TO_LOC_DCSN_MTD.equals("S")) {
		
			
			recInPara1.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
			recInPara1.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID);
			recInPara1.setField("YS_STK_COL_GP", 	szYS_DN_WO_LOC.substring(0, 6));
			recInPara1.setField("YS_STK_BED_NO", 	szYS_DN_WO_LOC.substring(6));
			recInPara1.setField("YS_STK_LYR_NO", 	commUtils.stringPlusInt(szYS_DN_WO_LAYER,0));
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDnMtlStatChk 
			SELECT DECODE(NVL(SUM(CASE WHEN :V_YS_STK_COL_GP || :V_YS_STK_BED_NO || :V_YS_STK_LYR_NO IN (BEF_UP_WO_LOC,BEF_DN_WO_LOC) THEN 1
			                     ELSE 0 END),0),0,'Y','N') AS LYR_UPDATE_YN  
			  FROM
			        (SELECT YS_UP_WO_LOC||YS_UP_WO_LAYER AS BEF_UP_WO_LOC
			              , YS_DN_WO_LOC||YS_DN_WO_LAYER AS BEF_DN_WO_LOC
			          FROM TB_YS_CRNSCH
			         WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			           AND YD_CRN_SCH_ID < :V_YD_CRN_SCH_ID
			           AND YD_TO_LOC_DCSN_MTD = 'C'
			           AND DEL_YN = 'N'
			        )   
			*/
			rsMaxSeq1 = commDao.select(recInPara1, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDnMtlStatChk", logId, methodNm, "권상정보 검색");
			rsMaxSeq1.first();
			recPara1 = rsMaxSeq1.getRecord();
			commUtils.printLog(logId, LocalmethodNm + commUtils.trim(recPara1.getFieldString("LYR_UPDATE_YN"  )) + ">>1"+rsOutBed.size()  , "SL"); 
			
			szLYR_UPDATE_YN = commUtils.trim(recPara1.getFieldString("LYR_UPDATE_YN"  ));
			
			//합적 이적 인경우 
			if("Y".equals(szYS_MERG_YN) && "2".equals(szYD_AID_WRK_UPDN_GP)){
				szLYR_UPDATE_YN = "Y";
			}
			
		} else {
			szLYR_UPDATE_YN = "Y";
		}		
		
		if(szLYR_UPDATE_YN.equals("Y")){		
			//적치단의 재료상태를 권하대기로 변경
			for(int i = 1; i <= rsOutBed.size(); i++) {
				rsOutBed.absolute(i);
				resOutBed  = rsOutBed.getRecord();
				jrParam.setField("YS_STK_COL_GP", 	szYS_DN_WO_LOC.substring(0, 6));
				jrParam.setField("YS_STK_BED_NO", 	szYS_DN_WO_LOC.substring(6));
				jrParam.setField("YS_STK_LYR_NO", 	commUtils.stringPlusInt(szYS_DN_WO_LAYER,0));
			//	jrParam.setField("YS_STK_SEQ_NO", 	commUtils.trim(resOutBed.getFieldString("YS_STK_SEQ_NO"  )));
				jrParam.setField("SSTL_NO",       	commUtils.trim(resOutBed.getFieldString("SSTL_NO"  )));
				jrParam.setField("YD_STK_LYR_MTL_STAT", "D");
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDnMtlStat 
				SELECT NVL(MAX(YS_STK_SEQ_NO),0) + 1 AS MAX_YS_STK_SEQ_NO 
				    FROM TB_YS_STKLYR
				   WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
				     AND YS_STK_BED_NO = :V_YS_STK_BED_NO 
				     AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
				     AND SSTL_NO IS NOT NULL
				     AND YD_STK_LYR_MTL_STAT= 'C'
				 */    
				rsMaxSeq = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDnMtlStat", logId, methodNm, "권상정보 검색");
				rsMaxSeq.first();
				recPara = rsMaxSeq.getRecord();
				commUtils.printLog(logId, LocalmethodNm + commUtils.trim(recPara.getFieldString("MAX_YS_STK_SEQ_NO"  )) + ">>2"+rsOutBed.size()  , "SL"); 
		
				jrParam.setField("YS_STK_SEQ_NO", 	 commUtils.trim(recPara.getFieldString("MAX_YS_STK_SEQ_NO"  ))); 
				
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
					commUtils.printLog(logId, "[" + LocalmethodNm + "] 적치단[" + jrParam.getFieldString("YS_STK_COL_GP") + "]활성화중 ERROR 발생", "SL");
					return YsConstant.RETN_CD_FAILURE;
				}
			}
		}	
		szLogMsg = "["+ LocalmethodNm +"] 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]에 크레인작업재료 등록 완료 - 메세지 : " + szRtnMsg;
		commUtils.printLog(logId, szLogMsg, "SL");
		//----------------------------------------------------------------------------------------------------------------------
    	
		//----------------------------------------------------------------------------------------------------------------------
		return YsConstant.RETN_CD_SUCCESS;
	}

	/**
	 * 분리작업 TO위치 UPDATE
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */	
	public String procUpdateLocSpr(String logId, String methodNms, JDTORecord recCrnwrkmtl, JDTORecord RecSetLoc) throws JDTOException {

		String methodNm = " 분리작업 TO위치 UPDATE[BtYsSchSeEJB.procUpdateLocSpr] < " + methodNms;
		String LocalmethodNm = " 분리작업 TO위치 UPDATE[BtYsSchSeEJB.procUpdateLocSpr] ";
		
		String szLogMsg					= null;
		String szRtnMsg 				= YsConstant.RETN_CD_SUCCESS;
		JDTORecord		recInBed		= null;
		int intRtnVal					= 0;
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String szYD_EQP_WRK_MAX_W 	= commUtils.trim(recCrnwrkmtl.getFieldString("MAX_MTL_W"  ));		//크레인작업재료 중 최대 폭
		String szYD_EQP_WRK_MAX_L 	= commUtils.trim(recCrnwrkmtl.getFieldString("MAX_MTL_L"  ));		//크레인작업재료 중 최대 길이
		int intYD_EQP_WRK_SH    	= commUtils.paraRecChkNullInt(recCrnwrkmtl,"SH_CNT");				//크레인작업재료 총매수
		int intYD_EQP_WRK_WT    	= commUtils.paraRecChkNullInt(recCrnwrkmtl,"SUM_MTL_WT");			//크레인작업재료 총중량
		double dblYD_EQP_WRK_T     	= commUtils.paraRecChkNullDouble(recCrnwrkmtl,"SUM_MTL_T");			//크레인작업재료 총높이
	
		String szYD_CRN_SCH_ID  	= commUtils.trim(RecSetLoc.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
		String szYD_EQP_ID  		= commUtils.trim(RecSetLoc.getFieldString("YD_EQP_ID"  ));	
		String szYS_UP_WO_LOC		= commUtils.trim(RecSetLoc.getFieldString("YS_UP_WO_LOC"  ));			
		String szYS_UP_WO_LAYER		= commUtils.trim(RecSetLoc.getFieldString("YS_UP_WO_LAYER"  ));			
		String szYS_DN_WO_LOC		= commUtils.trim(RecSetLoc.getFieldString("YS_DN_WO_LOC"  ));			
		String szYS_DN_WO_LAYER		= commUtils.trim(RecSetLoc.getFieldString("YS_DN_WO_LAYER"  ));

		String szYD_AID_WRK_UPDN_GP	= commUtils.trim(RecSetLoc.getFieldString("YD_AID_WRK_UPDN_GP"  ));			
		String szYD_WBOOK_ID		= commUtils.trim(RecSetLoc.getFieldString("YD_WBOOK_ID"  ));

		String szMODIFIER 			= commUtils.trim(recCrnwrkmtl.getFieldString("MODIFIER"  ));		//MODIFIER
		String szYD_SCH_CD 		    = commUtils.trim(RecSetLoc.getFieldString("YD_SCH_CD"  ));			//크레인스케줄코드		
		if (szYS_DN_WO_LOC.equals("")) {
			return YsConstant.RETN_CD_FAILURE;
		}
		if( szYD_AID_WRK_UPDN_GP.equals("")) {
			return YsConstant.RETN_CD_FAILURE;
		}
			
		commUtils.printParam(logId, RecSetLoc);
		//----------------------------------------------------------------------------------------------------------------------
		// 권상지시위치 수정
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ LocalmethodNm +"] 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]";
		commUtils.printLog(logId, szLogMsg, "SL");
		

		commUtils.printLog(logId, "권상위치 조회", "SL");
		
		//----------------------------------------------------------------------------------------------------------------------
		//	권상중이거나 권하중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		JDTORecordSet RsBedUpXy = JDTORecordFactory.getInstance().createRecordSet("");
		recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YS_STK_COL_GP", 			szYS_UP_WO_LOC.substring(0, 6)); //권상지시위치
		recInBed.setField("YS_STK_BED_NO", 			szYS_UP_WO_LOC.substring(6));	 //권상지시위치
		recInBed.setField("YS_DAN", 				szYS_UP_WO_LAYER);	 //권상지시위치
		
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
		      ,YD_STK_BED_XAXIS1
		      ,YD_STK_BED_YAXIS1
		      ,YD_STK_BED_ZAXIS1 
		      ,CASE WHEN MOD(:V_YS_DAN,2) = 0 THEN 0
		             ELSE 1 END DAN_GP
		  FROM TB_YS_STKBED A
		 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
		   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
		   AND DEL_YN ='N'
			 */  
		RsBedUpXy = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybed", logId, methodNm, "권상 BED 좌표 조회");
		if (RsBedUpXy.size() <= 0) {
			szLogMsg = "["+ LocalmethodNm +"] 권상 BED 좌표 조회 검색 실패 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			
		}
		RsBedUpXy.first();
		JDTORecord RecUpBedXy = RsBedUpXy.getRecord();

		JDTORecordSet RsDnBedXy = JDTORecordFactory.getInstance().createRecordSet("");
		recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YS_STK_COL_GP", 			szYS_DN_WO_LOC.substring(0, 6));										//권하지시위치
		recInBed.setField("YS_STK_BED_NO", 			szYS_DN_WO_LOC.substring(6));										//권하지시위치
		recInBed.setField("YS_DAN", 				szYS_DN_WO_LAYER);	 //권상지시위치
			
		
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
		      ,YD_STK_BED_XAXIS1
		      ,YD_STK_BED_YAXIS1
		      ,YD_STK_BED_ZAXIS1 
		      ,CASE WHEN MOD(:V_YS_DAN,2) = 0 THEN 0
		             ELSE 1 END DAN_GP
		  FROM TB_YS_STKBED A
		 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
		   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
		   AND DEL_YN ='N'
			 */  
		RsDnBedXy = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybed", logId, methodNm, "권하 BED 좌표 조회");
		if (RsDnBedXy.size() <= 0) {
			szLogMsg = "["+ LocalmethodNm +"] 권하 BED 좌표 검색 실패 ";
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
		
		if(commUtils.trim(RecUpBedXy.getFieldString("YD_STK_COL_DIR_GP")).equals("V1") && commUtils.trim(RecUpBedXy.getFieldString("DAN_GP")).equals("2") ) {
			recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS1"  ))) ;
			recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS1"  ))) ;
			recUpCrnSch.setField("YD_UP_WO_LOC_ZAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_ZAXIS1"  )) ) ;
			
		} else {
			recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
			recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
			recUpCrnSch.setField("YD_UP_WO_LOC_ZAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_ZAXIS"  )) ) ;
		}
		
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
		
		if(commUtils.trim(RecDnBedXy.getFieldString("YD_STK_COL_DIR_GP")).equals("V1") && commUtils.trim(RecDnBedXy.getFieldString("DAN_GP")).equals("2") ) {
			recUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS1"  )) ) ;
			recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS1"  )) ) ;
			recUpCrnSch.setField("YD_DN_WO_LOC_ZAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_ZAXIS1"  )) ) ;
			
		} else {
			recUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS"  )) ) ;
			recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS"  )) ) ;
			recUpCrnSch.setField("YD_DN_WO_LOC_ZAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_ZAXIS"  )) ) ;
		}
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
			szLogMsg = "["+ LocalmethodNm +"] 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단[" +szYS_DN_WO_LAYER +" ]을 크레인스케줄에 수정 중 ERROR 발생";
			commUtils.printLog(logId, szLogMsg, "SL");
			return YsConstant.RETN_CD_FAILURE;
		}		
		
    	szLogMsg = "["+ LocalmethodNm +"] 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]을 크레인스케줄에 수정 완료";
		commUtils.printLog(logId, szLogMsg, "SL");

		//----------------------------------------------------------------------------------------------------------------------
		//	권하지시위치에 재료를 권하대기로 등록
		//  권상위치 정보 READ 하여 권하 위치 SET
		//----------------------------------------------------------------------------------------------------------------------
    	szLogMsg = "["+ LocalmethodNm +"] 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]에 크레인작업재료 등록 시작";
		commUtils.printLog(logId, szLogMsg, "SL");
		
		JDTORecordSet rsOutBed = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord resOutBed= JDTORecordFactory.getInstance().create();
		JDTORecord jrParam= JDTORecordFactory.getInstance().create();
		recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);
		recInBed.setField("YS_STK_COL_GP", 			szYS_UP_WO_LOC.substring(0, 6));										
		recInBed.setField("YS_STK_BED_NO", 			szYS_UP_WO_LOC.substring(6));										
		recInBed.setField("YS_STK_LYR_NO", 			commUtils.stringPlusInt(szYS_UP_WO_LAYER,0));										

//01.13 추가
//장입이면서 권상위치가  TY 이고 권하위치가 장입대 인 경우
//크레인 작업지시 SEQ 변경처리		
//권하위치를 장입순으로	업데이트			
		if(szYS_UP_WO_LOC.substring(2, 4).equals("TY") 
				&& szYS_DN_WO_LOC.substring(2, 4).equals("TZ") 
				&& (szYD_SCH_CD.substring(2,4).equals("TZ") && szYD_SCH_CD.substring(6,7).equals("U"))){
			
			szLogMsg = "["+ LocalmethodNm +"] 장입이면서 권상위치가  TY 이고 권하위치가 장입대 인 경우 권하위치를 장입순으로	";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyrTZ    
			-- 장입대권하시 재배열(장입순번 필요)
			WITH PARA_TBL1 AS ( 
			      SELECT SUBSTR(YD_SCH_CD,3,2) AS YD_SCH
			                     FROM TB_YS_CRNSCH 
			                    WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			    ) 
			SELECT A.YS_STK_COL_GP            AS YS_STK_COL_GP
			     , A.YS_STK_BED_NO            AS YS_STK_BED_NO
			     , A.YS_STK_LYR_NO            AS YS_STK_LYR_NO
			     , decode(YD_SCH ,'YD',A.YS_STK_SEQ_NO,ROW_NUMBER() OVER(ORDER BY NVL(B.YD_CHG_NO,'0'), A.YS_STK_SEQ_NO)) AS YS_STK_SEQ_NO
			     , A.SSTL_NO                   AS SSTL_NO
			     , B.YD_CHG_NO
			  FROM TB_YS_STKLYR A
			     , TB_YS_STOCK B
			     , PARA_TBL1
			 WHERE A.SSTL_NO = B.SSTL_NO
			   AND A.YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND A.YS_STK_BED_NO = :V_YS_STK_BED_NO
			   AND A.YS_STK_LYR_NO = :V_YS_STK_LYR_NO
			   AND A.SSTL_NO IS NOT NULL
			   AND A.SSTL_NO IN (SELECT SSTL_NO FROM TB_YS_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
			   AND A.DEL_YN = 'N'
			  ORDER BY decode(YD_SCH ,'YD','0',NVL(B.YD_CHG_NO,'0')), A.YS_STK_SEQ_NO
			*/  
		
			rsOutBed 	= commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyrTZ", logId, methodNm, "권상정보 검색");
			if (rsOutBed.size() <= 0) {
				szLogMsg = "["+ LocalmethodNm +"] 권하위치를 장입순으로	권상정보 BED 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
			
			} else {
				JDTORecord jrParam1		= JDTORecordFactory.getInstance().create();;
				JDTORecord resTYBed		= JDTORecordFactory.getInstance().create();;
				for(int i = 1; i <= rsOutBed.size(); i++) {
					rsOutBed.absolute(i);
					resTYBed  = rsOutBed.getRecord();
					jrParam1.setField("MODIFIER"	 ,	szMODIFIER);
					jrParam1.setField("YD_CRN_SCH_ID",	commUtils.trim(resTYBed.getFieldString("YD_CRN_SCH_ID")));
					jrParam1.setField("SSTL_NO"		 ,	commUtils.trim(resTYBed.getFieldString("SSTL_NO")));
					jrParam1.setField("YS_STK_SEQ_NO",  commUtils.trim(resTYBed.getFieldString("YS_STK_SEQ_NO")));

					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCrnwrkmtlSeq 
					UPDATE TB_YS_CRNWRKMTL
					   SET MOD_DDTT = SYSDATE      
					     , MODIFIER = :V_MODIFIER
					     , YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO
					 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					   AND SSTL_NO       = :V_SSTL_NO  
			    	 */  
					intRtnVal = commDao.update(jrParam1, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCrnwrkmtlSeq", logId, methodNm, "TB_YS_CRNWRKMTL 갱신");
					
					if(intRtnVal <= 0) {
						commUtils.printLog(logId, "[" + LocalmethodNm + "] 크레인작업재료 UPDATE ERROR 발생", "SL");
						return YsConstant.RETN_CD_FAILURE;
					}
				}				
			}
//01.13 추가	완료
			
//		if( szYD_AID_WRK_UPDN_GP.equals("1")){   
//			//야드에서 임시 적치대로 갈 경우 
//			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyrTY     
//			-- 임시적치대에서 권하시 재배열(장입순번 필요
//			WITH PARA_TBL1 AS ( 
//			      SELECT SUBSTR(YD_SCH_CD,3,2) AS YD_SCH
//			                     FROM TB_YS_CRNSCH 
//			                    WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
//			    ) 
//			SELECT A.YS_STK_COL_GP            AS YS_STK_COL_GP
//			     , A.YS_STK_BED_NO            AS YS_STK_BED_NO
//			     , A.YS_STK_LYR_NO            AS YS_STK_LYR_NO
//			--   , ROW_NUMBER() OVER(ORDER BY B.YD_CHG_NO, A.YS_STK_SEQ_NO)  AS YS_STK_SEQ_NO
//			     , decode(YD_SCH ,'YD',A.YS_STK_SEQ_NO,ROW_NUMBER() OVER(ORDER BY B.YD_CHG_NO, A.YS_STK_SEQ_NO)) AS YS_STK_SEQ_NO
//			     , A.SSTL_NO                   AS SSTL_NO
//			  FROM TB_YS_STKLYR A
//			     , TB_YS_STOCK B
//			     , PARA_TBL1
//			 WHERE A.SSTL_NO = B.SSTL_NO
//			   AND A.YS_STK_COL_GP = :V_YS_STK_COL_GP
//			   AND A.YS_STK_BED_NO = :V_YS_STK_BED_NO
//			   AND A.YS_STK_LYR_NO = :V_YS_STK_LYR_NO
//			   AND A.SSTL_NO IS NOT NULL
//			   AND A.SSTL_NO IN (SELECT SSTL_NO FROM TB_YS_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
//			   AND A.DEL_YN = 'N'
//			  ORDER BY decode(YD_SCH ,'YD','0',B.YD_CHG_NO), A.YS_STK_SEQ_NO
//			 */ 
//			
//			rsOutBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyrTY", logId, methodNm, "권상정보 검색");
//			if (rsOutBed.size() <= 0) {
//				szLogMsg =  "["+ LocalmethodNm +"] 권상정보 BED 검색 실패 ";
//				commUtils.printLog(logId, szLogMsg, "SL");
//				
//			}
//			
		} else {
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
				szLogMsg =  "["+ LocalmethodNm +"] 권상정보 BED 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				
			}
		}	
		//----------------------------------------------------------------------------------------------------------------------
		//	분리작업
		//----------------------------------------------------------------------------------------------------------------------		
		
		szLogMsg =  "["+ LocalmethodNm +"] 적치단의 재료상태를 권하대기로 변경 " + szYD_AID_WRK_UPDN_GP;
		commUtils.printLog(logId, szLogMsg, "SL");
		
		//적치단의 재료상태를 권하대기로 변경
		//장입대까지 변경처리 함
		JDTORecordSet rsMaxSeq  = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		JDTORecord recInPara 	= JDTORecordFactory.getInstance().create();
		
		
		JDTORecordSet rsMaxSeq1 = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recPara1 	= JDTORecordFactory.getInstance().create();
		JDTORecord recInPara1 	= JDTORecordFactory.getInstance().create();
		String szLYR_UPDATE_YN  = "";
		
		if( szYD_AID_WRK_UPDN_GP.equals("1") || szYD_AID_WRK_UPDN_GP.equals("2")){
	
			if( szYD_AID_WRK_UPDN_GP.equals("2")){
		
				recInPara1.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
				recInPara1.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID);
				recInPara1.setField("YS_STK_COL_GP", 	szYS_DN_WO_LOC.substring(0, 6));
				recInPara1.setField("YS_STK_BED_NO", 	szYS_DN_WO_LOC.substring(6));
				recInPara1.setField("YS_STK_LYR_NO", 	commUtils.stringPlusInt(szYS_DN_WO_LAYER,0));
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDnMtlStatChk 
				SELECT DECODE(NVL(SUM(CASE WHEN :V_YS_STK_COL_GP || :V_YS_STK_BED_NO || :V_YS_STK_LYR_NO IN (BEF_UP_WO_LOC,BEF_DN_WO_LOC) THEN 1
				                     ELSE 0 END),0),0,'Y','N') AS LYR_UPDATE_YN  
				  FROM
				        (SELECT YS_UP_WO_LOC||YS_UP_WO_LAYER AS BEF_UP_WO_LOC
				              , YS_DN_WO_LOC||YS_DN_WO_LAYER AS BEF_DN_WO_LOC
				          FROM TB_YS_CRNSCH
				         WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				           AND YD_CRN_SCH_ID < :V_YD_CRN_SCH_ID
				           AND YD_TO_LOC_DCSN_MTD = 'C'
				           AND DEL_YN = 'N'
				        )   
				*/
				rsMaxSeq1 = commDao.select(recInPara1, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDnMtlStatChk", logId, methodNm, "권상정보 검색");
				rsMaxSeq1.first();
				recPara1 = rsMaxSeq1.getRecord();
				commUtils.printLog(logId, LocalmethodNm + commUtils.trim(recPara1.getFieldString("LYR_UPDATE_YN"  )) + ">>2"+rsOutBed.size()  , "SL"); 
				
				szLYR_UPDATE_YN = commUtils.trim(recPara1.getFieldString("LYR_UPDATE_YN"  ));
				
			} else {
				szLYR_UPDATE_YN = "Y";
			}
			
			
			if(szLYR_UPDATE_YN.equals("Y")){	
				//적치단의 재료상태를 권하대기로 변경
				for(int i = 1; i <= rsOutBed.size(); i++) {
					rsOutBed.absolute(i);
					resOutBed  = rsOutBed.getRecord();
					jrParam.setField("YS_STK_COL_GP", 	szYS_DN_WO_LOC.substring(0, 6));
					jrParam.setField("YS_STK_BED_NO", 	szYS_DN_WO_LOC.substring(6));
					jrParam.setField("YS_STK_LYR_NO", 	commUtils.stringPlusInt(szYS_DN_WO_LAYER,0));
		//			jrParam.setField("YS_STK_SEQ_NO", 	commUtils.trim(resOutBed.getFieldString("YS_STK_SEQ_NO"  )));
					jrParam.setField("SSTL_NO",       	commUtils.trim(resOutBed.getFieldString("SSTL_NO"  )));
					jrParam.setField("YD_STK_LYR_MTL_STAT", "D");
					jrParam.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
			
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDnMtlStat 
					SELECT NVL(MAX(YS_STK_SEQ_NO),0) + 1 AS MAX_YS_STK_SEQ_NO 
					    FROM TB_YS_STKLYR
					   WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
					     AND YS_STK_BED_NO = :V_YS_STK_BED_NO 
					     AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
					     AND YD_STK_LYR_MTL_STAT= 'C'
					 */    
					rsMaxSeq = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDnMtlStat", logId, methodNm, "권상정보 검색");
					rsMaxSeq.first();
					recPara = rsMaxSeq.getRecord();
					commUtils.printLog(logId, LocalmethodNm + commUtils.trim(recPara.getFieldString("MAX_YS_STK_SEQ_NO"  )) + ">>2"+rsOutBed.size()  , "SL"); 
					
					jrParam.setField("YS_STK_SEQ_NO", 	 commUtils.trim(recPara.getFieldString("MAX_YS_STK_SEQ_NO"  ))); 				
					
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
						commUtils.printLog(logId,  "["+ LocalmethodNm +"] 적치단[" + jrParam.getFieldString("YS_STK_COL_GP") + "]활성화중 ERROR 발생", "SL");
						return YsConstant.RETN_CD_FAILURE;
					}
				}
			}
		}
		
		if( szYD_AID_WRK_UPDN_GP.equals("1")){
			
	       	//-------------------------------------------------------------------------------------------------------------
			// 분리 작업  1인 경우1 권하 위치를 - >  2,3번 권상위치로 변경
	    	//-------------------------------------------------------------------------------------------------------------
			recInPara 	= JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtUpLoc
			UPDATE TB_YS_CRNSCH A
			   SET ( A.YS_UP_WO_LOC
			       , A.YS_UP_WO_LAYER 
			     ) = (SELECT YS_DN_WO_LOC,YS_DN_WO_LAYER
			            FROM TB_YS_CRNSCH
			           WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			             AND DEL_YN = 'N'
			             AND YD_TO_LOC_DCSN_MTD = 'C'
			             AND YD_AID_WRK_UPDN_GP = '1'
			          )   
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND DEL_YN = 'N'
			   AND YD_TO_LOC_DCSN_MTD = 'C'
			   AND YD_AID_WRK_UPDN_GP IN ('2','3')
			*/
			intRtnVal = commDao.update(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtUpLoc", logId, methodNm, "크레인스케줄 갱신");
			if(intRtnVal <= 0){
				commUtils.printLog(logId,  "["+ LocalmethodNm +"] 분할 작업 크레인스케줄 권상위치등록 실패!!", "SL");
			}
		
		}		
		szLogMsg = "["+ LocalmethodNm +"] 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]에 크레인작업재료 등록 완료 - 메세지 : " + szRtnMsg;
		commUtils.printLog(logId, szLogMsg, "SL");
		//----------------------------------------------------------------------------------------------------------------------
    	
		//----------------------------------------------------------------------------------------------------------------------
		return YsConstant.RETN_CD_SUCCESS;
	}		
	
}