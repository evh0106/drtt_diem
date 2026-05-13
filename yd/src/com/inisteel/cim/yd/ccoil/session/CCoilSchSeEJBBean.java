/**
 * @(#)2CoilSchSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2019/07/02
 *
 * @description      2열연 COIL 야드 Schedule 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.yd.ccoil.session;

import jspeed.base.record.JDTOException;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.StringUtils;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.ccommon.dao.CCommDAO;
import com.inisteel.cim.yd.ccommon.util.CConstant;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.ccoil.dao.CCoilDAO;

/**
 *      [A] 클래스명 : 2열연 COIL 야드 Schedule 처리
 *
 * @ejb.bean name="CCoilSchSeEJB" jndi-name="CCoilSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class CCoilSchSeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private CCommUtils 	commUtils 	= new CCommUtils();
	private CCommDAO 	commDao 	= new CCommDAO();
	private CCoilDAO 	coilDao 	= new CCoilDAO();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	
	
	/**	 
	 *      [A] 오퍼레이션명 : 코일크레인스케줄(YDYDJ509:procY5CrnSchMain)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYDYDJ551(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일크레인스케줄MAIN[CCoilSchSeEJB.rcvYDYDJ551Tx] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
		
			commUtils.printLog(logId, mthdNm, "S+");
			
			JDTORecord jrRst = this.procYDYDJ551(rcvMsg);

			jrRtn = commUtils.addSndData(jrRtn, jrRst);
			
			String rtnCd	 = commUtils.nvl(jrRst.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRst.getFieldString("RTN_MSG"), "");
			
			commUtils.printParam(logId, jrRtn);
			
			jrRtn.setField("RTN_MSG", rtnMsg);	
			jrRtn.setField("RTN_CD"	, rtnCd);
			
			commUtils.printLog(logId, "[스케쥴메인종료]", "S-");
			return jrRtn;
 
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**	
	 *      [A] 오퍼레이션명 : 코일크레인스케줄(YDYDJ599)(procY5CrnSchMainB)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg 
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procYDYDJ551(JDTORecord rcvMsg) throws DAOException {
		String mthdNms = "코일크레인스케줄MAIN[CCoilSchSeEJB.procYDYDJ551] < " + rcvMsg.getResultMsg();
		String mthdNm  = "코일크레인스케줄MAIN[CCoilSchSeEJB.procYDYDJ551] " ;
		String logId   = rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, mthdNms, "S+");

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
			String ydL3Msg 		= ""; 	//야드L3MESSAGE
			String sUpLocChkYn 	= "";   //권상위치 CHECK 여부
			String rtnCd 	    = "";   //
			String rtnMsg 	    = "";   //

			JDTORecordSet jsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			String msgId      = commUtils.getMsgId(rcvMsg);								//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"  )); //야드작업예약ID
			String ydSchCd    = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"    )); //야드스케쥴코드
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //야드설비ID
			String sModifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			String supZoneDummy   = commUtils.nvl(rcvMsg.getFieldString("SUPZONE_DUMMY"), "N"); //보급재더미여부 확인
			String ydDualChk      = commUtils.nvl(rcvMsg.getFieldString("YD_DUAL_CHK"), "N");   //보급스케줄 생성시 이중생성 확인위한것
			
			// 멀티기동시 명령선택 1번만 기동하기 위해서(rcvYDYDJ552에서 등록됨
			String sMultiYn   = commUtils.nvl (rcvMsg.getFieldString("MULTI_YN"      ),"N"); 
			
			if ("".equals(sModifier)) { sModifier = msgId; }

			commUtils.printLog(logId, "스케쥴 기동 : 스케쥴코드["+ydSchCd+"], 설비ID["+ydEqpId+"], 작업예약ID["+ydWbookId+"], 수정자["+sModifier+"]", "SL");

			//조회 및 등록용
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
			jrParam.setField("YD_SCH_CD"  , ydSchCd  ); //야드스케쥴코드
			jrParam.setField("YD_EQP_ID"  , ydEqpId  ); //야드설비ID
			
			
			/*********************************************************
			 *  스케줄 정리
			 * -스케줄 재료없이 헤더만 있는 경우 발생
			 *********************************************************/
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnSch
			UPDATE TB_YD_CRNSCH
			   SET DEL_YN   = 'Y'
			     , MODIFIER = NVL(:V_MODIFIER,'AUTO_DEL')
			     , MOD_DDTT = SYSDATE
			WHERE YD_CRN_SCH_ID IN(     
			                        SELECT A.YD_CRN_SCH_ID
			                          FROM USRYDA.TB_YD_CRNSCH    A
			                             , USRYDA.TB_YD_CRNWRKMTL B
			                         WHERE A.YD_CRN_SCH_ID=B.YD_CRN_SCH_ID(+)
			                           AND A.DEL_YN = 'N'
			                           AND B.YD_CRN_SCH_ID IS NULL
			                           AND A.REG_DDTT <= SYSDATE-0.002
			                           AND A.YD_GP IN ('J','H')
			                      )
			 */
			commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnSch",logId, mthdNm,"이상 스케줄 정리");
			
			/*********************************************************
			   *  보급 스케줄 기동 시 동일 코일의 작업 예약 삭제
			   *  - 보급(출고) 스케줄이 기동되면, 해당 코일에 대한
			   *    크레인 스케줄이 생성되지 않은 다른 작업 예약을 자동 삭제
			   *  - 목적: 제품야드 → 소재야드 역이동 방지 (동내이적 등)
			   *  2026.04.08 RITM1551428
            **********************************************************/
			commUtils.printLog(logId, "CICD 2026.04.08 보급 스케줄 기동 시 동일 코일의 다른 작업 예약을 자동 삭제", "SL");
			String sAPP025_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "025");
			/*
			SELECT NVL(MAX(ITEM1),'N') AS APPLY_YN FROM USRYDA.TB_YD_RULE A 
			WHERE REPR_CD_GP = 'APP001' AND CD_GP = 'J' AND ITEM = '025';			 
			*/
			if ("Y".equals(sAPP025_YN)) {
				if (!"".equals(ydWbookId) && !"".equals(ydSchCd)) {
					
	                // 보급 스케줄 여부 확인 (TB_YD_SCHRULE.CD_CONTENTS LIKE '%보급%')
					JDTORecordSet jrSchChk = JDTORecordFactory.getInstance().createRecordSet("Temp");
					
					/*
					-- com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.checkIssueSchedule 
					-- 코일크레인스케줄MAIN[CCoilSchSeEJB.procYDYDJ551]
					-- 보급 스케줄 여부 확인
					SELECT 
					  CASE WHEN COUNT(*) > 0 THEN 'Y' 
					  	   ELSE 'N' END AS IS_ISSUE_SCH
					FROM USRYDA.TB_YD_SCHRULE A
					WHERE 1 = 1
					AND YD_SCH_CD = :V_YD_SCH_CD
					AND A.YD_GP = 'J'
					AND A.DEL_YN = 'N'
					AND ( A.YD_SCH_CD LIKE 'J_KE01UH' OR A.YD_SCH_CD LIKE 'J_FE01UH' )
					 */					
					jrSchChk = commDao.select(jrParam,  "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.checkIssueSchedule",  logId,  mthdNm,   "보급 스케줄 여부 확인" );

	
					String isIssueSchedule = "N";
					if (jrSchChk != null && jrSchChk.size() > 0) {
						isIssueSchedule = commUtils.nvl(jrSchChk.getRecord(0).getFieldString("IS_ISSUE_SCH"), "N");
					}
					
					if ("Y".equals(isIssueSchedule)) {
	
	                    jrParam.setField("YD_WBOOK_ID", ydWbookId);
	                    jrParam.setField("YD_SCH_CD", ydSchCd);
	
	                    /* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.delWrkbookByIssue
	                     * - 크레인 스케줄이 생성되지 않은 작업 예약만 삭제 (안전)
	                     * - 보급 외 다른 작업 예약 삭제 (동내이적 포함)

						UPDATE USRYDA.TB_YD_WRKBOOK WB
						SET DEL_YN   = 'Y'
						, MODIFIER = NVL(:V_MODIFIER, 'AUTO_DEL')
						, MOD_DDTT = SYSDATE
						WHERE WB.DEL_YN = 'N'
						-- 현재 보급 스케줄의 코일 목록과 동일한 코일 포함
						AND WB.YD_WBOOK_ID IN (
							SELECT DISTINCT WB2.YD_WBOOK_ID
							FROM USRYDA.TB_YD_WRKBOOKMTL WM2
							, USRYDA.TB_YD_WRKBOOK WB2
							, USRYDA.TB_YD_WRKBOOKMTL WM_CURR  -- 현재 보급 스케줄의 코일
							WHERE WM2.YD_WBOOK_ID = WB2.YD_WBOOK_ID
							AND WM2.DEL_YN = 'N'
							AND WB2.DEL_YN = 'N'
							-- 현재 보급 스케줄과 동일 코일
							AND WM2.STL_NO = WM_CURR.STL_NO
							AND WM_CURR.YD_WBOOK_ID = :V_YD_WBOOK_ID
							AND WM_CURR.DEL_YN = 'N'
						)
						-- 현재 기동 중인 보급 작업 예약은 제외
						AND WB.YD_WBOOK_ID != :V_YD_WBOOK_ID
						-- 크레인 스케줄이 생성되지 않은 작업 예약만 
						AND NOT EXISTS (
							SELECT 1
							FROM USRYDA.TB_YD_CRNSCH CS
							WHERE CS.YD_WBOOK_ID = WB.YD_WBOOK_ID
							AND CS.DEL_YN = 'N'
						)
						-- 보급(출고) 스케줄이 아닌 다른 작업만 삭제
						AND WB.YD_SCH_CD NOT LIKE 'J_KE01UH' -- SPM 보급
						AND WB.YD_SCH_CD NOT LIKE 'J_FE01UH' -- HFL 보급
	                    */
	                    int delCnt = commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.delWrkbookByIssue", logId, mthdNm, "보급 스케줄 기동 시 작업 예약 삭제");
	
	                    if (delCnt > 0) {
	                    commUtils.printLog(logId, "보급 스케줄[" + ydSchCd + "] 기동: 동일 코일의 작업 예약 " + delCnt + "건 자동 삭제 완료", "SL");
	                    }
					}
				}
			} // end APP025 보급 스케줄 기동 시 동일 코일의 다른 작업 예약을 자동 삭제
			
			/**********************************************************
			* 1. 파라메타 정보  Check
			* 1.1 스케줄코드 상태 Check
			* 1.2 설비고장 및 OFF-LINE Check
			* 1.3 파라메타 정보 Check
			**********************************************************/
			//스케줄코드 Check
			if ("".equals(ydWbookId) && !"".equals(ydSchCd)) {
				JDTORecord jrChk = coilDao.chkSchCd(jrParam);
				
				ydL3Msg = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"));

				if (!"".equals(ydL3Msg)) {
					ydSchCd = "";
				}
			}

			//설비ID Check
			if ("".equals(ydWbookId) && "".equals(ydSchCd) && !"".equals(ydEqpId)) {
				JDTORecord jrChk = coilDao.chkEqpStat(jrParam);

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
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnSchWbook
				SELECT A.YD_WBOOK_ID
				  FROM (SELECT WB.YD_WBOOK_ID
				             , SR.YD_SCH_DIV_GP
				          FROM TB_YD_SCHRULE SR
				              ,TB_YD_WRKBOOK WB
				         WHERE SR.YD_SCH_CD       = WB.YD_SCH_CD
				           AND WB.YD_WBOOK_ID     = :V_YD_WBOOK_ID
				           AND SR.YD_SCH_PROH_EXN = 'N'
				           AND SR.DEL_YN          = 'N'
				           AND WB.DEL_YN          = 'N'
				           AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
				                                        FROM TB_YD_CRNSCH
				                                       WHERE DEL_YN = 'N')
				         ORDER BY WB.YD_SCH_PRIOR, WB.YD_WBOOK_ID) A
				 WHERE ROWNUM = 1
				*/
				jsWbook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnSchWbook", logId, mthdNm, "작업예약 조회");
				
			} else if (!"".equals(ydSchCd)) {
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnSchWbookSchCd 
				SELECT A.YD_WBOOK_ID
				  FROM (SELECT WB.YD_WBOOK_ID
				             , SR.YD_SCH_DIV_GP
				          FROM TB_YD_SCHRULE SR
				              ,TB_YD_WRKBOOK WB
				         WHERE SR.YD_SCH_CD       = WB.YD_SCH_CD
				           AND WB.YD_SCH_CD      = :V_YD_SCH_CD
				           AND SR.YD_SCH_PROH_EXN = 'N'
				           AND SR.DEL_YN          = 'N'
				           AND WB.DEL_YN          = 'N'
				           AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
				                                        FROM TB_YD_CRNSCH
				                                       WHERE DEL_YN = 'N')
				         ORDER BY WB.YD_SCH_PRIOR, WB.YD_WBOOK_ID) A
				 WHERE ROWNUM = 1
				*/ 
				jsWbook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnSchWbookSchCd", logId, mthdNm, "작업예약 조회");
				 
			} else if (!"".equals(ydEqpId)) {
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnSchWbookEqp 
				SELECT YD_WBOOK_ID
				  FROM (SELECT WB.YD_WBOOK_ID
				             , SR.YD_SCH_DIV_GP
				          FROM TB_YD_SCHRULE SR
				              ,TB_YD_WRKBOOK WB
				         WHERE SR.YD_SCH_CD       = WB.YD_SCH_CD
				           AND NVL(WB.YD_WRK_PLAN_CRN,SR.YD_WRK_CRN) = :V_YD_EQP_ID
				           AND SR.YD_SCH_PROH_EXN = 'N'
				           AND SR.DEL_YN          = 'N' 
				           AND WB.DEL_YN          = 'N'
				           AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
				                                        FROM TB_YD_CRNSCH
				                                       WHERE DEL_YN = 'N')
				         ORDER BY WB.YD_SCH_PRIOR, WB.YD_WBOOK_ID) A
				 WHERE ROWNUM = 1
				*/
				jsWbook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnSchWbookEqp", logId, mthdNm, "작업예약 조회");
				 
			} else {
				commUtils.printLog(logId, "오류:작업예약정보 없음", "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", "오류:작업예약ID조회 항목 없음");
//				m_ctx.setRollbackOnly();
				return jrRtn;
			}

			if (jsWbook != null && jsWbook.size() > 0) {
				ydWbookId	= commUtils.trim(jsWbook.getRecord(0).getFieldString("YD_WBOOK_ID"));
			} else {
				commUtils.printLog(logId, "오류:작업예약정보 없음", "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", "오류:작업예약정보 없음");
//				m_ctx.setRollbackOnly();
				return jrRtn;
			}
			
			commUtils.printLog(logId,  " >> 결정된 작업예약ID [" + ydWbookId + "]", "SL");

			jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
			
//			/**********************************************************
//			* 1.2 크레인 작업 재료에 현재 적치단 저장위치 Update (별도 Transaction 으로 처리)
//			**********************************************************/
//			EJBConnector tranConn = new EJBConnector("default", "CCoilSchSeEJB", this);
//			tranConn.trx("updCrnSchWB", new Class[] { JDTORecord.class }, new Object[] { jrParam });

						
			/**********************************************************
			* 2.스케줄수행판단 모듈
			* 2.1 크레인 선택
			* 2.2 TO위치 사전 점검
			**********************************************************/
	
			//조회된 작업예약ID로 상태정보 Check
			String ydToLocDcsnMtd = ""; 	//야드To위치결정방법
			String ydToLocGuide   = ""; 	//야드To위치Guide
			String sToLocChkGp    = ""; 	//To위치 점검을 위한 구분(G:To위치Guide, C:차량상차, T:대차상차)
			String ydEqpStat      = "";		//야드설비상태
			String ydSchPrior     = "";
			String sWbRegister    = "";     //작업예약 등록자
			
			int iWmMtlSh;

			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnSchStat
			--크레인스케줄 상태정보 조회 
			SELECT WB.YD_GP                                         
			     , WB.YD_BAY_GP                                     
			     , WB.YD_SCH_CD                                     
			     , WB.YD_SCH_PRIOR                                  
			     , WB.YD_TO_LOC_DCSN_MTD                            
			     , WB.YD_TO_LOC_GUIDE                               
			     , TO_CHAR(WB.REG_DDTT,'YYYYMMDDHH24MISS')        AS YD_WBOOK_DT    --야드작업예약일시
			     , WB.YD_CAR_USE_GP                                 --야드차량사용구분
			     , WB.TRN_EQP_CD                                    --운송장비코드
			     , WB.CAR_NO                                        --차량번호
			     , WB.CARD_NO                                       --카드번호

			     , CASE WHEN WB.YD_SCH_CD LIKE '__PT__U_'                        THEN 'C' --차량상차
			            WHEN WB.YD_SCH_CD LIKE '__TC__U_'                        THEN 'T' --대차상차
			            WHEN LENGTH(WB.YD_TO_LOC_GUIDE) >= 4        
			             AND WB.YD_TO_LOC_GUIDE LIKE WB.YD_GP||WB.YD_BAY_GP||'%' THEN 'G' --To위치Guide
			     	    ELSE 'Z' END                                                      --기타
			       AS TO_LOC_CHK_GP                                                       --To위치점검구분
			     , SR.YD_SCH_PROH_EXN                                                     --야드스케쥴금지유무
			     
			     , (CASE WHEN (WB.YD_SCH_CD LIKE 'J_TC__UM' OR WB.YD_SCH_CD LIKE 'J_YD__MM')  
			             THEN WB.YD_WRK_PLAN_CRN ELSE '' END) AS YD_WRK_PLAN_CRN    --야드작업계획크레인
			             
			     , E0.YD_EQP_STAT      AS YD_EQP_STAT_PLN           --작업계획크레인 야드설비상태
			     , E0.YD_EQP_WRK_MODE  AS YD_EQP_WRK_MODE_PLN       --작업계획크레인 야드설비작업Mode
			     
			     , SR.YD_WRK_CRN                                    --야드작업크레인
			     , SR.YD_WRK_CRN_PRIOR                              --야드작업크레인우선순위
			     , E1.YD_EQP_STAT      AS YD_EQP_STAT_WRK           --작업크레인 야드설비상태
			     , E1.YD_EQP_WRK_MODE  AS YD_EQP_WRK_MODE_WRK       --작업크레인 야드설비작업Mode
			     
			     , SR.YD_ALT_CRN                                    --야드대체크레인
			     , SR.YD_ALT_CRN_PRIOR                              --야드대체크레인우선순위
			     , E2.YD_EQP_STAT      AS YD_EQP_STAT_ALT           --대체크레인 야드설비상태
			     , E2.YD_EQP_WRK_MODE  AS YD_EQP_WRK_MODE_ALT       --대체크레인 야드설비작업Mode
			     
			     , NVL(WM.TT_MTL_SH,0) AS TT_MTL_SH                 --전체 재료매수
			     , NVL(WM.WM_MTL_SH,0) AS WM_MTL_SH                 --작업예약 재료매수
			     , NVL(WM.ST_MTL_SH,0) AS ST_MTL_SH                 --저장품 재료매수
			     , NVL(WM.SL_MTL_SH,0) AS SL_MTL_SH                 --적치단 재료매수
			     , NVL(WM.STAT_C_SH,0) AS STAT_C_SH                 --적치중인 재료매수
			     , (SELECT COUNT(*)
			          FROM TB_YD_WRKBOOKMTL WM
			             , TB_YD_STKLYR     SL
			         WHERE WM.STL_NO      = SL.STL_NO
			           AND WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
			           AND SL.YD_STK_COL_GP NOT LIKE SUBSTR(WB.YD_SCH_CD,1,2)||'%'
			           AND SL.YD_STK_LYR_MTL_STAT = 'C'
			           AND WM.DEL_YN      = 'N'
			           AND SL.DEL_YN      = 'N') AS AB_LOC_SH       --저장위치이상 재료매수
			     , (SELECT YD_CURR_BAY_GP
			          FROM TB_YD_EQP  EQ
			         WHERE EQ.YD_EQP_ID = 'JXTC0'||SUBSTR(WB.YD_SCH_CD,6,1)) AS YD_CURR_BAY_GP  --대차현위치
			     , NVL((SELECT CASE WHEN MIN(CASE WHEN S2.YD_STK_LYR_MTL_STAT = 'D' THEN '1' END ) > 0 THEN 'Y' ELSE 'N' END 
			              FROM TB_YD_WRKBOOKMTL WM
			                 , TB_YD_STKLYR     SL
			                 , TB_YD_STKLYR     S2
			             WHERE WM.STL_NO      = SL.STL_NO
			               AND WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
			               AND WM.DEL_YN      = 'N'
			               AND SL.DEL_YN      = 'N'
			               AND S2.YD_STK_COL_GP = SL.YD_STK_COL_GP 
			               AND S2.YD_STK_BED_NO IN (SL.YD_STK_BED_NO, CASE WHEN SL.YD_STK_LYR_NO = '002' THEN TO_NUMBER(SL.YD_STK_BED_NO) 
			                                                               ELSE TO_NUMBER(SL.YD_STK_BED_NO) - TO_NUMBER(SF_YD_SKID_INTERVAL_GAP(S2.YD_STK_COL_GP)) END  )
			               AND S2.YD_STK_LYR_NO IN (CASE WHEN SL.YD_STK_LYR_NO = '002' THEN '002'
			                                             WHEN S2.YD_STK_BED_NO = TO_NUMBER(SL.YD_STK_BED_NO)  THEN '001'
			                                             ELSE '002' END , '002' )),'N') AS UP_DN_GP           --상단 권하 대상 여부  
			  FROM TB_YD_WRKBOOK WB
			     , (
			     
			        SELECT D.YD_SCH_CD
			             , D.YD_SCH_PROH_EXN
			             , (CASE -------------------------------H동 1번지 호기 변경대상---------------------------------------
			                     WHEN C.YD_BAY_GP='H'
			                      AND C.YD_EQP_GP BETWEEN '00' AND '99' 
			                      AND C.YD_STR_LOC LIKE 'JH%' 
			                      AND C.YD_STK_BED_NO = '01' 
			                      AND C.YD_STR_LOC NOT LIKE 'JH44%'
			                      AND C.YD_STK_LYR_NO = '001'                   
			                      AND D.YD_WRK_CRN  <> 'JHCRH3'
			                      AND A.YD_SCH_CD LIKE 'JH_____H'                                               THEN 'JHCRH1'
			                     -------------------------------H동보급 32,33스판 호기 변경대상---------------------------------------
			                     WHEN C.YD_BAY_GP='H' AND A.YD_SCH_CD='JHKE01UH' AND C.YD_EQP_GP IN('32','33')  THEN 'JHCRH1' 
			                     -------------------------------E동보급 32,33스판 호기 변경대상---------------------------------------
			                     WHEN C.YD_BAY_GP='E' AND A.YD_SCH_CD='JEKE01UH' AND C.YD_EQP_GP IN('32','33')  THEN 'JECRE1'              
			                     -------------------------------A동보급 31~36스판 호기 변경대상---------------------------------------
			                     WHEN C.YD_BAY_GP='A' AND A.YD_SCH_CD LIKE 'JH_____H' AND C.YD_EQP_GP BETWEEN '31' AND '36' AND C.YD_STR_LOC LIKE 'JA%'  THEN 'JACRA1' 
			                     -------------------------------A동보급 31,32스판 호기 변경대상---------------------------------------
			                     WHEN C.YD_BAY_GP='A' AND A.YD_SCH_CD='JAKE01UH' AND C.YD_EQP_GP IN('31','32')  THEN 'JACRA1'   
			                     -- BCD동 공냉재 이송 1통로 
			                     WHEN A.YD_SCH_CD IN ('JBPT21UM', 'JCPT21UM', 'JDPT21UM') THEN NVL(A.YD_WRK_PLAN_CRN, D.YD_WRK_CRN)
			                     ELSE D.YD_WRK_CRN END) AS YD_WRK_CRN
			             , D.YD_WRK_CRN_PRIOR                              --야드작업크레인우선순위
			             , (CASE -------------------------------H동 1번지 호기 변경대상---------------------------------------
			                     WHEN C.YD_BAY_GP='H'
			                      AND C.YD_EQP_GP BETWEEN '00' AND '99' 
			                      AND C.YD_STR_LOC LIKE 'JH%' 
			                      AND C.YD_STK_BED_NO = '01' 
			                      AND C.YD_STR_LOC NOT LIKE 'JH44%'
			                      AND C.YD_STK_LYR_NO = '001'                                                   
			                      AND D.YD_ALT_CRN  <> 'JHCRH3'
			                      AND A.YD_SCH_CD LIKE 'JH_____H'                                               THEN 'JHCRH2'
			                     -------------------------------H동보급 32,33스판 호기 변경대상---------------------------------------
			                     WHEN C.YD_BAY_GP='H' AND A.YD_SCH_CD='JHKE01UH' AND C.YD_EQP_GP IN('32','33')  THEN 'JHCRH2' 
			                     -------------------------------E동보급 32,33스판 호기 변경대상---------------------------------------
			                     WHEN C.YD_BAY_GP='E' AND A.YD_SCH_CD='JEKE01UH' AND C.YD_EQP_GP IN('32','33')  THEN 'JECRE2'              
			                     -------------------------------A동보급 31~36스판 호기 변경대상---------------------------------------
			                     WHEN C.YD_BAY_GP='A' AND A.YD_SCH_CD LIKE 'JH_____H' AND C.YD_EQP_GP BETWEEN '31' AND '36' AND C.YD_STR_LOC LIKE 'JA%'  THEN 'JACRA2' 
			                     -------------------------------A동보급 31,32스판 호기 변경대상---------------------------------------
			                     WHEN C.YD_BAY_GP='A' AND A.YD_SCH_CD='JAKE01UH' AND C.YD_EQP_GP IN('31','32')  THEN 'JACRA2'   
			                     -- BCD동 공냉재 이송 1통로 
			                     WHEN A.YD_SCH_CD IN ('JBPT21UM', 'JCPT21UM', 'JDPT21UM') THEN NVL2(A.YD_WRK_PLAN_CRN, D.YD_WRK_CRN, D.YD_ALT_CRN)
			                     ELSE D.YD_ALT_CRN END) AS YD_ALT_CRN                                    --야드대체크레인
			             , D.YD_ALT_CRN_PRIOR                              --야드대체크레인우선순위
			             , D.DEL_YN
			          FROM TB_YD_WRKBOOK A
			             , TB_YD_WRKBOOKMTL B
			             , TB_PT_COILCOMM C
			             , TB_YD_SCHRULE  D 
			         WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
			           AND A.YD_SCH_CD   = D.YD_SCH_CD
			           AND B.STL_NO      = C.COIL_NO
			           AND A.YD_WBOOK_ID = :V_YD_WBOOK_ID     
			     
			     ) SR
			     , TB_YD_EQP     E0
			     , TB_YD_EQP     E1
			     , TB_YD_EQP     E2
			     , (SELECT WM.YD_WBOOK_ID
			             , COUNT(*)                  AS TT_MTL_SH
			             , COUNT(DISTINCT WM.STL_NO) AS WM_MTL_SH
			             , COUNT(DISTINCT ST.STL_NO) AS ST_MTL_SH
			             -- 대차 하자는 CHECK 안함
			             --, COUNT(DISTINCT SL.STL_NO) AS SL_MTL_SH
			             , CASE WHEN MAX(WK.YD_SCH_CD) LIKE 'J_TC0_L%' THEN  COUNT(DISTINCT WM.STL_NO)
			                    ELSE COUNT(DISTINCT SL.STL_NO) END AS SL_MTL_SH
			             , SUM(DECODE(SL.YD_STK_LYR_MTL_STAT,'C',1,'U',1)) AS STAT_C_SH 
			             , MAX(WM.YD_UP_COLL_SEQ)      AS YD_UP_COLL_SEQ
			          FROM TB_YD_WRKBOOK    WK
			             , TB_YD_WRKBOOKMTL WM
			             , TB_YD_STOCK      ST
			             , (SELECT * FROM TB_YD_STKLYR WHERE YD_STK_LYR_MTL_STAT IN ('C','U'))     SL
			         WHERE WK.YD_WBOOK_ID = WM.YD_WBOOK_ID 
			           AND WM.STL_NO      = ST.STL_NO(+)
			           AND WM.STL_NO      = SL.STL_NO(+)
			           AND WM.YD_WBOOK_ID = :V_YD_WBOOK_ID
			           AND WM.DEL_YN      = 'N'
			           AND SL.DEL_YN(+)   = 'N'
			         GROUP BY WM.YD_WBOOK_ID) WM
			 WHERE WB.YD_SCH_CD       = SR.YD_SCH_CD(+)
			   AND SR.YD_WRK_CRN      = E1.YD_EQP_ID(+)
			   AND SR.YD_ALT_CRN      = E2.YD_EQP_ID(+)
			   AND (CASE WHEN (WB.YD_SCH_CD LIKE 'J_TC__UM' OR WB.YD_SCH_CD LIKE 'J_YD__MM')  THEN WB.YD_WRK_PLAN_CRN ELSE '' END) = E0.YD_EQP_ID(+)
			   AND WB.YD_WBOOK_ID     = WM.YD_WBOOK_ID(+)
			   AND WB.YD_WBOOK_ID     = :V_YD_WBOOK_ID
			   AND WB.DEL_YN          = 'N'
			   AND SR.DEL_YN(+)       = 'N'
			   AND E1.DEL_YN(+)       = 'N'
			   AND E2.DEL_YN(+)       = 'N'
			   AND E0.DEL_YN(+)       = 'N'
			*/
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnSchStat", logId, mthdNm, "작업예약 조회");
			if (jsChk.size() <= 0) { 
				
				commUtils.printLog(logId, "오류:상태정보 없음", "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", "오류:상태정보 없음");
				return jrRtn;			
			}
			
			
			JDTORecord jrChk = jsChk.getRecord(0);
			
			ydSchCd                = commUtils.trim(jrChk.getFieldString("YD_SCH_CD"          ));	//야드스케쥴코드
			ydToLocDcsnMtd         = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_DCSN_MTD" ));	//야드To위치결정방법
			ydToLocGuide           = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_GUIDE"    ));	//야드To위치Guide
			sToLocChkGp            = commUtils.trim(jrChk.getFieldString("TO_LOC_CHK_GP"      ));	//To위치점검구분
			ydSchPrior             = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN_PRIOR"   ));	//야드스케쥴우선순위
			sWbRegister            = commUtils.trim(jrChk.getFieldString("WB_REGISTER"        ));	//작업예약 등록자
			String ydWrkPlanCrn    = commUtils.trim(jrChk.getFieldString("YD_WRK_PLAN_CRN"    ));	//야드작업계획크레인
			String ydEqpStatPln    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_PLN"    ));	//야드설비상태(작업계획크레인)
			String ydEqpWrkModePln = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_PLN"));	//야드설비작업Mode(작업계획크레인)
			String ydWrkCrn        = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN"         ));	//야드작업크레인
			String ydEqpStatWrk    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_WRK"    ));	//야드설비상태(작업크레인)
			String sUpDnGp         = commUtils.trim(jrChk.getFieldString("UP_DN_GP"           ));	//상단권하대상('D') CHECK
			String sUpDnStlNo      = commUtils.trim(jrChk.getFieldString("UP_DN_STL_NO"       ));	//상단권하대상코일('D') CHECK
			String sUpAnrCrnGp     = commUtils.trim(jrChk.getFieldString("UP_ANR_CRN_GP"      ));	//타크레인 상단 권상스케쥴 여부
			//
			String ydAltCrn        = commUtils.trim(jrChk.getFieldString("YD_ALT_CRN"         ));	//대체크레인
			String ydEqpStatAlt    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_ALT"    ));	//대체크레인 설비상태
			int iMtlSh 			   = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("TT_MTL_SH"),"0"));	//전체 재료매수
			iWmMtlSh               = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("WM_MTL_SH"),"0"));	//작업예약 재료매수
			int iStMtlSh 		   = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("ST_MTL_SH"),"0"));	//저장품 재료매수
			int iSlMtlSh 		   = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("SL_MTL_SH"),"0"));	//적치단 재료매수
			int iStatCSh 		   = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("STAT_C_SH"),"0"));	//적치중인 재료매수(야드적치단재료상태가 적치 중[C] 인 재료수)
			int iAbLocSh 		   = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("AB_LOC_SH"),"0"));	//저장위치이상 재료매수
//				String sYM_CRANE_TRAVL_PROH = commUtils.trim(jrChk.getFieldString("YM_CRANE_TRAVL_PROH"    ));	//크레인 주행금지구간

			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getccoilUpLocChkYn
			SELECT CASE
			            ---- #2,3,5 HFL 동간입고, 보급, 입고 제외
			            WHEN SUBSTR(WB.YD_SCH_CD, 3, 6) IN ('TC01MM', 'TC02MM') AND SUBSTR(WB.YD_SCH_CD, 2, 1) IN ('B', 'D', 'F')
			            THEN 'Y'
			            WHEN SUBSTR(WB.YD_SCH_CD, 2, 3) IN  ('FFE', 'FFD', 'DFE', 'DFD', 'BFE', 'BFD')
			            THEN 'Y'
			            -----------------------------------------
			            WHEN WB.YD_SCH_CD LIKE 'J_CV0_LH' -- 수입
			            THEN 'N'
			            WHEN WB.YD_SCH_CD LIKE 'J_TC0_MM' -- SPM/HFL 동간입고
			            THEN 'N'
			            WHEN SUBSTR(WB.YD_SCH_CD, 4, 5) IN ( 'E03LH' -- SPM/HFL 입측TAKE-OUT
			                                               , 'D01LH' -- SPM/HFL 추출
			                                               , 'D02LH' -- SPM/HFL 재작업
			                                               , 'D03LH' -- SPM/HFL 출측TAKE-OUT
			                                               , 'D04LH' -- SPM 재작업, #1/#4/#5 HFL 차공정
			                                               , 'D05LH' -- SPM 스크랩추출
			                                               , 'D06LH' -- 
			                                               , 'D01LM' -- SPM/HFL 입고
			                                               )
			            THEN 'N'
			            ELSE 'Y'
			       END AS UP_LOC_CHK_YN
			  FROM TB_YD_WRKBOOK WB
			 WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
			 */	
			
			
			JDTORecordSet jsUpLocChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getccoilUpLocChkYn", logId, mthdNm, "권상위치 CHECK 조회");
			if (jsUpLocChk.size() <= 0) {
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", "오류:권상위치 CHECK 오류");
				return jrRtn;
			}
			sUpLocChkYn = jsUpLocChk.getRecord(0).getFieldString("UP_LOC_CHK_YN");
			
			commUtils.printLog(logId,  "sUpLocChkYn:"+ sUpLocChkYn, "SL");
			if ("Y".equals(sUpLocChkYn)) {
				if (iWmMtlSh == 0) {
					commUtils.printLog(logId,  "오류:작업예약재료 정보 없음", "SL");
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", "오류:상태정보 없음");
					return jrRtn;

				} else if (iWmMtlSh != iMtlSh) {
					commUtils.printLog(logId, "오류:작업예약재료 적치단 중복 등록 [작업예약: " + iWmMtlSh + ", 적치단: " + iWmMtlSh + "]", "SL");
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", "오류:작업예약재료 적치단 중복 등록 [작업예약: " + iWmMtlSh + ", 적치단: " + iWmMtlSh + "]");
					return jrRtn;
				} else if (iWmMtlSh != iSlMtlSh) {
					commUtils.printLog(logId, "오류:작업예약재료 적치단 정보 이상 [" + (iWmMtlSh - iSlMtlSh) + "매]", "SL");
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", "오류:작업예약재료 적치단 정보 이상 [" + (iWmMtlSh - iSlMtlSh) + "매]");
					return jrRtn;
				} else if (iWmMtlSh != iStatCSh) {
					commUtils.printLog(logId, "오류:작업예약재료 적치중[C]이 아님 [" + (iWmMtlSh - iStatCSh) + "매]", "SL");
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", "오류:작업예약재료 적치중[C]이 아님 [" + (iWmMtlSh - iStatCSh) + "매]");
					return jrRtn;
				} else if (iWmMtlSh != iStMtlSh) {
					commUtils.printLog(logId, "오류:작업예약재료 저장품 정보 이상 [" + (iWmMtlSh - iStMtlSh) + "매]", "SL");
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", "오류:작업예약재료 저장품 정보 이상 [" + (iWmMtlSh - iStMtlSh) + "매]");
					return jrRtn;
				} else if (iAbLocSh > 0) {
					commUtils.printLog(logId, "오류:작업예약재료의 현재위치 이상 [" + iAbLocSh + "매]", "SL");
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", "오류:작업예약재료의 현재위치 이상 [" + iAbLocSh + "매]");
					return jrRtn;
				} else if ("Y".equals(sUpDnGp)) {
					commUtils.printLog(logId, "오류:상단에 권하 작업이 있습니다. 불가합니다.", "SL");
//					jrRtn.setField("RTN_CD" , "0");	
//					jrRtn.setField("RTN_MSG", "오류:상단에 권하 작업이 있습니다. 불가합니다.");
//					return jrRtn;					 
				}
				
				/*
				if ("Y".equals(sUpAnrCrnGp)) {
					commUtils.printLog(logId, "오류:타크레인 상단 권상 작업이 있습니다. 불가합니다.", "SL");
					jrRtn.setField("RTN_CD" , "0");
					jrRtn.setField("RTN_MSG", "오류:타크레인 상단 권상 작업이 있습니다. 불가합니다.");
					return jrRtn;
				}
				*/
				
			} else {
				if (iWmMtlSh == 0) {
					commUtils.printLog(logId, "오류:작업예약재료 정보 없음", "SL");
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", "오류:작업예약재료 정보 없음");
					return jrRtn;
				} else if (iWmMtlSh != iMtlSh) {
					commUtils.printLog(logId, "오류:작업예약재료 적치단 중복 등록 [작업예약: " + iWmMtlSh + ", 적치단: " + iMtlSh + "]", "SL");
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", "오류:작업예약재료 적치단 중복 등록 [작업예약: " + iWmMtlSh + ", 적치단: " + iMtlSh + "]");
					return jrRtn;
				} else if (iWmMtlSh != iStMtlSh) {
					commUtils.printLog(logId, "오류:작업예약재료 저장품 정보 이상 [" + (iWmMtlSh - iStMtlSh) + "매]", "SL");
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", "오류:작업예약재료 저장품 정보 이상 [" + (iWmMtlSh - iStMtlSh) + "매]");
					return jrRtn;
				} else if ("Y".equals(sUpDnGp)) {
					commUtils.printLog(logId, "오류:상단에 권하 작업이 있습니다. 불가합니다.", "SL");
//					jrRtn.setField("RTN_CD" , "0");	
//					jrRtn.setField("RTN_MSG", "오류:상단에 권하 작업이 있습니다. 불가합니다.");
//					return jrRtn;
				}
			}
			
			
			
				
				
			/***************************************************************************
			 * 2단 대상 이면서 보조작업에 해당 하는 작업예약 존재 시 자동 삭제 처리  2024.03.19
			 ***************************************************************************/
			if ("Y".equals(sUpDnGp)) { 
				String sAPP024_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "024");   
				
				if("Y".equals(sAPP024_YN)){ 
					jrParam.setField("STL_NO", sUpDnStlNo); //상단 대기중인 크레인 스케줄 대상 조회
					
					JDTORecordSet jsOutSet = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.session.CCoilSchSeEJB.getYdWbookChk", logId, mthdNm, "대기 크레인 스케줄 체크");
					if ( jsOutSet.size() == 0  ) { 
						String sLogMsg = "연관된 크레인 스케줄이  존재하지 않음";
						commUtils.printLog(logId, sLogMsg + " STL_NO: " + commUtils.trim(sUpDnStlNo), "SL"); 
						
						jrRtn.setField("RTN_CD" , "0");	
						jrRtn.setField("RTN_MSG", "오류:상단에 권하 작업이 있습니다. 불가합니다.1");
						return jrRtn;
						 
					} else {
						
						jsOutSet.first();
						jrParam = JDTORecordFactory.getInstance().create();
						jrParam = commUtils.getParam(logId, mthdNm, sModifier);
						jrParam = jsOutSet.getRecord(); 
						  
						String ydDnWoLayer      = commUtils.trim(jrParam.getFieldString("YD_DN_WO_LAYER")); 
						
						
						commUtils.printLog(logId,  " YD_SCH_CD: " + ydSchCd +" YD_DN_WO_LAYER: " + ydDnWoLayer, "SL");  
						
						//SPM보급 스케줄 과 권하 위치가 2단인 경우
						if("KE01".equals(ydSchCd.substring(2, 6)) && "002".equals(ydDnWoLayer)){
						
					    	jrParam.setField("DEL_YN"		, "Y");
					    	jrParam.setField("MODIFIER"		, "강제취소");
					    	
							/**********************************************************
							* 크레인스케쥴 취소
							**********************************************************/
							EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
							JDTORecord jrCancelWrk = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
							
					    	String sRtnCd	= commUtils.nvl(jrCancelWrk.getFieldString("RTN_CD"), "0");
					    	String sRtnMsg	= commUtils.trim(jrCancelWrk.getFieldString("RTN_MSG"));
					    	
					    	if (!"1".equals(sRtnCd)) {
								commUtils.printLog(logId, sRtnMsg, "S-");
								
								jrRtn.setField("RTN_CD"	, "0");
								jrRtn.setField("RTN_MSG", sRtnMsg);
								return jrRtn;
					    	}
					    	
					    	if ( "0".equals(sRtnCd) ) {
					    		commUtils.printLog(logId, sRtnMsg, "SL");
					    		
								jrRtn.setField("RTN_CD"	, "0");
								jrRtn.setField("RTN_MSG", sRtnMsg);
								return jrRtn;
					    	}
					    	
					    	ydEqpStatWrk ="W";
						}else{
							jrRtn.setField("RTN_CD" , "0");	
							jrRtn.setField("RTN_MSG", "오류:상단에 권하 작업이 있습니다. 불가합니다.2");
							return jrRtn;
						}
					} 
				}else{
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", "오류:상단에 권하 작업이 있습니다. 불가합니다.");
					return jrRtn;
				}
				  
			}
			
			/**********************************************************
			* 1.2 To위치 사전 점검
			*     - 차량상차 작업('C')
			*     - 야드To위치Guide('G')
			**********************************************************/
			//To위치 사전 점검
			if ("C".equals(sToLocChkGp)) {
				//차량상차작업
				String ydCarUseGp = commUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP"));	//야드차량사용구분
				String sTrnEqpCd  = commUtils.trim(jrChk.getFieldString("TRN_EQP_CD"   ));	//운송장비코드
				String sCarNo     = commUtils.trim(jrChk.getFieldString("CAR_NO"       ));	//차량번호

				if ("".equals(ydCarUseGp)) {
					commUtils.printLog(logId, "오류:차량상차작업 야드차량사용구분 없음.", "SL");
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", "오류:차량상차작업 야드차량사용구분 없음.");
					return jrRtn;
					
				} else if ("L".equals(ydCarUseGp)) {
					// 구내운송
					if ("".equals(sTrnEqpCd)) {
						commUtils.printLog(logId, "오류:구내운송 상차작업 운송장비코드 없음.", "SL");
						jrRtn.setField("RTN_CD" , "0");	
						jrRtn.setField("RTN_MSG", "오류:구내운송 상차작업 운송장비코드 없음.");
						return jrRtn;
					}
				} else if ("G".equals(ydCarUseGp)) {
					// 출하
					if ("".equals(sCarNo)) {
						commUtils.printLog(logId, "오류:출하차량 상차작업 차량번호 또는 카드번호 없음.", "SL");
						jrRtn.setField("RTN_CD" , "0");	
						jrRtn.setField("RTN_MSG", "오류:출하차량 상차작업 차량번호 또는 카드번호 없음.");
						return jrRtn;
					}
				}
			} else if ("T".equals(sToLocChkGp)) {
				String ydCurrBayGp = commUtils.trim(jrChk.getFieldString("YD_CURR_BAY_GP"));	//대차 현위치
				if (!ydCurrBayGp.equals(ydSchCd.substring(1,2))) {
					commUtils.printLog(logId, "오류:대차상차인 경우 대차 현위치가 현재동에 없음", "SL");
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", "오류:대차상차인 경우 대차 현위치가 현재동에 없음.");
					return jrRtn;
				}
			}
			
			/**********************************************************
			* 1.3 크레인 결정
			**********************************************************/

			if (!"".equals(ydWrkPlanCrn) && !"B".equals(ydEqpStatPln) && "1".equals(ydEqpWrkModePln)) {
				//작업예약 지정크레인 : 최우선 지정
				ydEqpId   = ydWrkPlanCrn;	//야드설비ID
				ydEqpStat = ydEqpStatPln;	//야드설비상태
				commUtils.printLog(logId, " >> 작업예약 지정크레인[" + ydWrkPlanCrn + "]으로 설정", "SL");
			} else {
				ydEqpId   = ydWrkCrn;		//야드설비ID
				ydEqpStat = ydEqpStatWrk;	//야드설비상태
			}
			
			/***********************************************************
			 * 1.3.1 크레인이 고장상태이면 대체크레인으로 편성
			 ***********************************************************/
			// 크레인이 고장이고 대체크레인이 고장이 아니면 대체크레인으로 교체
			if ("B".equals(ydEqpStat)) {
				if (!"".equals(ydAltCrn) && !"B".equals(ydEqpStatAlt)) {
					ydEqpId   = ydAltCrn;
					ydEqpStat = ydEqpStatAlt;
				}
				
				// 지정크레인이 고장, 해당 스케줄의 작업크레인이 고장이 아닐때
				if (!"B".equals(ydEqpStatWrk)) {
					ydEqpId   = ydWrkCrn;		//야드설비ID
					ydEqpStat = ydEqpStatWrk;	//야드설비상태
				}
			} 
			
//			//야드To위치Guide 값이 4자리 이상이고 To 야드동이 같을 경우가 아니면
//			//TO 위치 가이드('G') 아니면 야드To위치결정방법,야드To위치Guide CLEAR
//			if (!"G".equals(sToLocChkGp)) {
//				ydToLocDcsnMtd = ""; //야드To위치결정방법
//				ydToLocGuide   = ""; //야드To위치Guide
//			}
//	
			
			if("Y".equals(supZoneDummy)){
				ydSchPrior = "0";
			}
			/***********************************************************************
			 * 스케줄 중복으로 생성된건 확인
			 * YYS 20230906
			 ************************************************************************/
//			String sAPP017_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","017"); 
//			commUtils.printLog(logId, "이중체크스케줄 기동 : ["+ydDualChk+"] 이중 스케줄생성 막음 : [" + sAPP017_YN+"]", "SL");
//			
//			if ("Y".equals(sAPP017_YN)) {
				if(!"".equals(ydWbookId)){ 
					/*
					 *SELECT *
						  FROM TB_CM_HSIFLOG 
						 WHERE IF_TYPE='J'
						  AND IF_TREAT_DONE_YN IN ('ES','DS')   --인큐/디큐 성공
						  AND IF_TREAT_DATE >=SYSDATE- 1/24/1200  --3초전~
						  AND IF_CD='YDYDJ551'
						  AND IF_CONTENTS LIKE '%'||:V_YD_WBOOK_ID||'%'
					 */
					jsWbook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnSchWbookESDS", logId, mthdNm, "작업예약 스케줄기동여부 조회");
					commUtils.printLog(logId, "TB_CM_HSIFLOG_SIZE : ["+jsWbook.size()+"] YD_WBOOK_ID : [" + ydWbookId+"]", "SL");
					if(jsWbook.size() > 1){
						commUtils.printLog(logId, "오류:1분전에 스케줄기동된 작업예약아이디 존재함", "SL");
						jrRtn.setField("RTN_CD" , "0");	
						jrRtn.setField("RTN_MSG", "오류:1분전에 스케줄기동된 작업예약아이디 존재함");
						return jrRtn;
					}
				}
//			}
			/*************************************************************************/
			
			JDTORecord jrParamSet = commUtils.getParam(logId, mthdNm, sModifier);
			jrParamSet.setField("YD_WBOOK_ID"			, ydWbookId); 			//야드작업예약ID
			jrParamSet.setField("YD_SCH_CD"  			, ydSchCd  ); 			//야드스케쥴코드
			jrParamSet.setField("YD_EQP_ID"  			, ydEqpId  ); 			//야드설비ID
			jrParamSet.setField("YD_SCH_PRIOR"  		, ydSchPrior  ); 		//야드스케쥴우선순위
			jrParamSet.setField("YD_TO_LOC_DCSN_MTD"  	, ydToLocDcsnMtd  ); 	//야드To위치결정방법
			jrParamSet.setField("YD_TO_LOC_GUIDE"  		, ydToLocGuide  ); 		//야드To위치Guide
			jrParamSet.setField("YD_WBOOK_MTL_CNT"   	, ""+iWmMtlSh ); 		//작업예약 매수
			jrParamSet.setField("UP_LOC_CHK_YN"   	    , sUpLocChkYn ); 		//권상적재위치 check여부
			jrParamSet.setField("WB_REGISTER"   		, sWbRegister ); 		//작업예약 등록자
			jrParamSet.setField("MODIFIER"   			, sModifier ); 		    //요청
			
			commUtils.printParam(logId, jrParamSet);
			/**********************************************************
			* 2.그룹핑 파라미터 셋팅
            *  2-1.주작업 및 보조 작업 셋팅
  			**********************************************************/
			commUtils.printLog(logId, "그룹핑 파라미터 셋팅 시작", "SL");			

			JDTORecordSet jsCoilGrp	= JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			JDTORecord jrSchGrp 	= this.crnSchGrp(logId, mthdNm, jrParamSet, jsCoilGrp);
			
			rtnCd	= commUtils.nvl(jrSchGrp.getFieldString("RTN_CD"),"0");
			rtnMsg  = commUtils.nvl(jrSchGrp.getFieldString("RTN_MSG"),"");
			
			if("0".equals(rtnCd)){
				commUtils.printLog(logId, "오류:그룹핑 파라미터 셋팅 없음", "SL");	
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", rtnMsg);	
				return jrRtn;
			}
			if("".equals(commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_ID")))) {
				commUtils.printLog(logId, "오류:재료상태 확인", "SL");	
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", rtnMsg);	
				return jrRtn;	
			}
			/**********************************************************
			* 3.크레인스케줄과 크레인작업재료 등록
			*   3-1.적치단의 재료상태를 권상대기로 변경처리
			*   3-2.크레인스케줄과 크레인작업재료 등록
  			**********************************************************/
			commUtils.printLog(logId, "크레인스케줄과 크레인작업재료 등록 시작 ", "SL");			
				
			JDTORecord jrSchIns 	= this.crnSchIns(logId, mthdNm, jrParamSet, jsCoilGrp);
			
			rtnCd	= commUtils.nvl(jrSchIns.getFieldString("RTN_CD"),"0");
			rtnMsg  = commUtils.nvl(jrSchIns.getFieldString("RTN_MSG"),"");
			
			if("0".equals(rtnCd)){
				m_ctx.setRollbackOnly();
				commUtils.printLog(logId, "오류:크레인스케줄과 크레인작업재료 등록", "SL");	
				jrRtn.setField("RTN_CD" 			, "0");	
				jrRtn.setField("RTN_MSG"			, rtnMsg);
				//전문 송신여부에 사용
				return jrRtn;
			}
			
			/**********************************************************
			* 4.TO위치결정
  			**********************************************************/
			commUtils.printLog(logId, "TO위치결정시작:"+ ydWbookId, "SL");			
			
			JDTORecord jrSchToLoc   = this.crnSchToLoc(logId, mthdNm, jrParamSet);
			rtnCd				 = commUtils.nvl(jrSchToLoc.getFieldString("RTN_CD"),"0");
			rtnMsg  			 = commUtils.nvl(jrSchToLoc.getFieldString("RTN_MSG"),"");
			String ydCrnSchIdRe  = commUtils.nvl(jrSchToLoc.getFieldString("YD_CRN_SCH_ID_RE"),"");
			String ydCrnSchId    = commUtils.nvl(jrSchToLoc.getFieldString("YD_CRN_SCH_ID"),"");
			String sTcMoveYn     = commUtils.nvl(jrSchToLoc.getFieldString("TC_MOVE_YN"),"N");
			String ydToXXChk     = commUtils.nvl(jrSchToLoc.getFieldString("YD_TOXX_CHK"),"N"); // TO위치 XX 결정된 경우

			commUtils.printLog(logId,  "rtnCd:" +  rtnCd  + "// ydEqpStat:" + ydEqpStat, "SL");
			
			if("0".equals(rtnCd)){
				m_ctx.setRollbackOnly();
				jrRtn.setField("RTN_CD" 		, "0");	
				jrRtn.setField("RTN_MSG"		, rtnMsg);	
				//전문 송신여부에 사용
				commUtils.printLog(logId, "오류:TO 저장위치 등록  오류", "SL");	
				return jrRtn;
			} else {
				
				if ("Y".equals(sTcMoveYn)) {

					JDTORecord jrTcSnd = commUtils.getParam(logId, mthdNm, sModifier);
					jrTcSnd.setField("YD_SCH_CD"	, ydSchCd);
					try {
						/**********************************************************
						*  제품은 중량오버인 경우 대차 자동 출발
						**********************************************************/
						EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
						JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procTcarStsSetTcarD", new Class[] { JDTORecord.class }, new Object[] { jrTcSnd });
						
						jrRtn = commUtils.addSndData(jrRtn , jrRtn1);
					} catch (Exception se) {}
				}	
			}
			
			
			String sLineOffChk = "N";
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_EQP_ID"	 , ydEqpId); 	
			jrParam.setField("YD_SCH_CD"	 , ydSchCd); 
			jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchId); 
			
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getLineOffChk
			SELECT CASE WHEN :V_YD_SCH_CD IN ( 'JHKD01LM', 'JHTC01MM', 'JHTC02MM'  --SPM1
			                                 , 'JGFD01LM', 'JGTC01MM', 'JGTC02MM'  --HFL1
			                                 , 'JEKD01LM', 'JETC01MM', 'JETC02MM'  --SPM2
			                                 , 'JCKD01LM', 'JCTC05MM'              --SPM3
			                                 , 'JBKD01LM', 'JBTC05MM'              --SPM4
			                                 -- 추출, 재작업보급, TAKE-OUT, 재작업추출, 스크랩추출, 지포장대기장
			                                 , 'JHKD01LH', 'JHKD02LH', 'JHKD03LH', 'JHKD04LH', 'JHKD05LH', 'JHKD06LH'
			                                 , 'JEKD01LH', 'JEKD02LH', 'JEKD03LH', 'JEKD04LH', 'JEKD05LH', 'JEKD06LH'
			                                 , 'JCKD01LH', 'JCKD02LH', 'JCKD03LH', 'JCKD04LH', 'JCKD05LH', 'JCKD06LH'
			                                 , 'JBKD01LH', 'JBKD02LH', 'JBKD03LH', 'JBKD04LH', 'JBKD05LH', 'JBKD06LH'
			                                 -- 추출, 재작업추출, TAKE-OUT, 차공정, 지포장대기장
			                                 , 'JGFD01LH', 'JGFD02LH', 'JGFD03LH', 'JGFD04LH', 'JGFD06LH'
			                                 ) THEN 'Y'
			            ELSE 'N'
			       END AS LINE_OFF_CHK
			  FROM DUAL
			*/
			JDTORecordSet jsLineOffChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getLineOffChk", logId, mthdNm, "LineOff 스케쥴"); 
			if (jsLineOffChk.size() > 0) {
				sLineOffChk    = commUtils.trim(jsLineOffChk.getRecord(0).getFieldString("LINE_OFF_CHK"   ));
				
			}
			//설비  Line-Off AC동 제외
			if ("Y".equals(sLineOffChk)) {
				// 해당 크레인 현재 Line-Off 작업인지 체크
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getWrkCrnSchId
				SELECT A.*
				  FROM TB_YD_CRNSCH A
				 WHERE A.YD_EQP_ID = :V_YD_EQP_ID
				--   AND A.YD_WRK_PROG_STAT IN  ('1', 'S', 'W')
				   AND A.YD_WRK_PROG_STAT IN  ('1', 'S')
				   AND A.YD_CRN_SCH_ID <> :V_YD_CRN_SCH_ID
				   AND A.DEL_YN = 'N'
				   AND SUBSTR(A.YD_UP_WO_LOC,1,4) =  (SELECT SUBSTR(YD_UP_WO_LOC,1,4) 
				                                        FROM TB_YD_CRNSCH
				                                       WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                                         AND DEL_YN = 'N'
				                                         AND ROWNUM = 1)
				*/ 
				JDTORecordSet jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getWrkCrnSchId", logId, mthdNm, "해당크레인 크레인스케줄에 같은 스케줄코드로 상태가 S,1 인 CRANE 스케줄ID 조회"); 
				
				if (jsResult.size() > 0) {

					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(mthdNm);	//Log Method Name

					jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ554"               ); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
					jrYdMsg.setField("YD_SCH_CD"  	   	 , ydSchCd                  ); //스케줄ID
					jrYdMsg.setField("YD_WBOOK_ID"  	 , ydWbookId                ); //작업예약ID
					
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				}
			}
			
			
			/**
			 * 5. 만들어진 크레인스케줄 정합성 체크
			 *  - 권상위치 이상체크 
			 */
			String sApp841 = coilDao.ApplyYn(logId, mthdNm, "APP841", "J", "*"); // 스케줄 체크여부
			if ("Y".equals(sApp841)) {
				commUtils.printLog(logId,"에러위치 체크"+ ydWbookId,"SL");
				jrParam.setField("YD_WBOOK_ID", ydWbookId); //작업예약ID
				/*
				--코일위치에러체크
				SELECT CASE WHEN D.YD_STK_LYR_NO = '001' THEN 0 
				            ELSE CASE WHEN -- 2단일 경우 코일 하단 코일 2개여부
				                          ((SELECT COUNT(*)
				                              FROM TB_YD_STKLYR      A --대상코일           
				                                 , TB_YD_STKLYR      B 
				                             WHERE 1 = 1  
				                               AND A.STL_NO = D.STL_NO
				                               AND B.YD_STK_COL_GP = A.YD_STK_COL_GP
				                               AND B.YD_STK_BED_NO IN (A.YD_STK_BED_NO, LPAD(TO_NUMBER(A.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(A.YD_STK_COL_GP), 2, '0'))  
				                               AND B.STL_NO IS NOT NULL
				                               AND A.YD_STK_LYR_MTL_STAT IN ('U')
				                               AND B.YD_STK_LYR_NO = '001') = 2 ) 
				                      AND ((SF_YD_WO_LOC_ZAXIS_AUTO(D.YD_STK_COL_GP,D.YD_STK_BED_NO, (SELECT COIL_OUTDIA  FROM TB_PT_COILCOMM P WHERE P.COIL_NO = D.STL_NO))) 
				                           > (SELECT MAX(COIL_OUTDIA)
				                                FROM TB_YD_STKLYR      A --대상코일           
				                                   , TB_YD_STKLYR      B 
				                                   , TB_PT_COILCOMM    C
				                               WHERE 1 = 1  
				                                 AND A.STL_NO = D.STL_NO
				                                 AND B.STL_NO = C.COIL_NO
				                                 AND B.YD_STK_COL_GP = A.YD_STK_COL_GP
				                                 AND B.YD_STK_BED_NO IN (A.YD_STK_BED_NO, LPAD(TO_NUMBER(A.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(A.YD_STK_COL_GP), 2, '0'))  
				                                 AND B.STL_NO IS NOT NULL
				                                 AND A.YD_STK_LYR_MTL_STAT IN ('U')
				                                 AND B.YD_STK_LYR_NO = '001')) 
				                    
				                      THEN 0 ELSE 1 END
				            END AS CHK_LOC
				     , WB.YD_WBOOK_ID
				  FROM TB_YD_STKLYR      D --대상코일           
				     , TB_YD_WRKBOOK    WB
				     , TB_YD_WRKBOOKMTL WM
				 WHERE 1 = 1  
				   AND D.STL_NO = WM.STL_NO
				   AND WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				   AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND D.YD_STK_LYR_MTL_STAT IN ('U')
				 */
				JDTORecordSet jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getChkCoilLoc", logId, mthdNm, "코일하단 위치 체크");
				
				if (jsResult.size() > 0) {
					
					String sChkLoc = jsResult.getRecord(0).getFieldString("CHK_LOC");
					
					if (!"0".equals(sChkLoc)) {
						jrParam.setField("YD_WRK_PROG_REQ_MSG", "[L3확인요망]적치상태불량 확인필요"); //작업예약ID
						/*
						UPDATE TB_YD_CRNSCH  
						   SET MODIFIER = :V_MODIFIER
						     , MOD_DDTT = SYSDATE
						     , YD_WRK_PROG_REQ_MSG = :V_YD_WRK_PROG_REQ_MSG
						 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnSchReqMsgByWbookid", logId, mthdNm, "스케줄 ERROR");
					}	
				}
			}
			
			commUtils.printLog(logId,  "작업지시 호출 여부(ydEqpStat/sMultiYn):" + ydEqpStat + "/"+ sMultiYn , "SL");
			/**********************************************************
			* 6.크레인작업지시 호출
  			**********************************************************/
			if ("W".equals(ydEqpStat) && "N".equals(sMultiYn)) {
				//야드설비상태가 대기이면 명령선택전문 전송
				JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
				jrYdMsg.setField("JMS_TC_CD"         , "Y5YDL007"               ); //JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태
				jrYdMsg.setField("YD_CRN_SCH_ID_RE"  , ydCrnSchIdRe             ); //명령선택에서 사용
				jrYdMsg.setField("YD_TOXX_CHK"  	 , ydToXXChk             	); //TO위치가 XX 결정된 경우 다른 크레인 작업 호출 
				
				
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
			} 
			
			commUtils.printParam(logId, jrRtn);
			
			jrRtn.setField("RTN_MSG", rtnMsg);	
			jrRtn.setField("RTN_CD"	, "1");	
			commUtils.printLog(logId, mthdNms, "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**	
	 *      [A] 오퍼레이션명 : LINE-OFF 긴급작업(YDYDJ554)
	 *		Line-Off 긴급작업 유/무인 동일 처리
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYDYDJ554(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "LINE-OFF긴급작업 [CCoilSchSeEJB.rcvYDYDJ554] < " + rcvMsg.getResultMsg();

		String logId		= rcvMsg.getResultCode();
		JDTORecord jrRtn	= JDTORecordFactory.getInstance().create();	//전문 Return
		JDTORecord jrParam	= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

		try {

			commUtils.printLog(logId, mthdNm, "S+");

	    	//수신항목 변수 저장
			String ydEqpId		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));	//설비번호(크레인번호)
			String ydSchCd		= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));	//설비번호(크레인번호)
			String ydWbookId	= commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"));	//야드작업예약ID

			String msgId	= commUtils.nvl(commUtils.getMsgId(rcvMsg),"YDYDJ554");	// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	// 수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			//변경자 설정 (insert,update 문에서 사용)
			jrParam.setField("MODIFIER", modifier);	//수정자
			jrParam.setResultCode(logId);			//Log ID
			jrParam.setResultMsg(mthdNm);			//Log Method Name

			/**************************************************
			 * 1. Line-Off 우선작업 크레인스케쥴 조회
			 *    가장 최근에 만들어진 크레인 스케줄ID를 가져온다.
			 **************************************************/
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getLastCrnSchId
			SELECT *
			  FROM TB_YD_CRNSCH
			 WHERE YD_EQP_ID   = :V_YD_EQP_ID
			   AND YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND DEL_YN = 'N'
			   AND YD_WRK_PROG_STAT IN ('W')
			 ORDER BY YD_CRN_SCH_ID DESC
			*/
			jrParam.setField("YD_EQP_ID"	, ydEqpId);
			jrParam.setField("YD_WBOOK_ID"	, ydWbookId);
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getLastCrnSchId", logId, mthdNm, "Line-Off 대상 크레인스케쥴 조회");

			if (jsCrnSch.size() > 0) {
				JDTORecord jrCrnSch = jsCrnSch.getRecord(0);

				String ydCrnSchId = commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));

				/**************************************************
				 * 2. 해당 크레인 현재 Line-Off 작업인지 체크
				 **************************************************/
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getWrkCrnSchId
				SELECT A.*
				  FROM TB_YD_CRNSCH A
				 WHERE A.YD_EQP_ID = :V_YD_EQP_ID
				--   AND A.YD_WRK_PROG_STAT IN  ('1', 'S', 'W')
				   AND A.YD_WRK_PROG_STAT IN  ('1', 'S')
				   AND A.YD_CRN_SCH_ID <> :V_YD_CRN_SCH_ID
				   AND A.DEL_YN = 'N'
				   AND SUBSTR(A.YD_UP_WO_LOC,1,4) =  (SELECT SUBSTR(YD_UP_WO_LOC,1,4)
				                                        FROM TB_YD_CRNSCH
				                                       WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                                         AND DEL_YN = 'N'
				                                         AND ROWNUM = 1)
				*/
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				JDTORecordSet jsCrnWrkOld = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getWrkCrnSchId", logId, mthdNm, "작업중인 Line-Off 크레인스케쥴 조회");

				if( jsCrnWrkOld.size() > 0 ) {

					String ydCrnSchIdOld	= commUtils.trim(jsCrnWrkOld.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
					String ydWrkProgStatOld	= commUtils.trim(jsCrnWrkOld.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));

					commUtils.printLog(logId, "Line-Off 긴급작업 크레인작업지시 변경", "SL");
					commUtils.printLog(logId, "YD_WRK_PROG_STAT["+ ydWrkProgStatOld +"] YD_CRN_SCH_ID["+ ydCrnSchIdOld +"]->["+ ydCrnSchId +"]", "SL");

					/**************************************************
					 * 3. 작업중인 Line-Off 작업 진행상태 초기화(W)
					 *    지시시간은 작업가능응답(Y5YDL015)에서 처리
					 **************************************************/
					/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnWrkMgtPriorWrkNext
					UPDATE TB_YD_CRNSCH A
					   SET MODIFIER           = :V_MODIFIER
					     , MOD_DDTT           = SYSDATE
					     , YD_WRK_PROG_STAT   = :V_YD_WRK_PROG_STAT
					     , YD_WORD_DT         = CASE WHEN :V_YD_WRK_PROG_STAT = 'W'         THEN NULL
					                                 WHEN :V_YD_WRK_PROG_STAT IN ('S', '1') THEN SYSDATE
					                                 ELSE YD_WORD_DT
					                            END
					     , YD_L2_REQUEST_STAT = NVL(:V_YD_L2_REQUEST_STAT, YD_L2_REQUEST_STAT)
					 WHERE YD_CRN_SCH_ID      = :V_YD_CRN_SCH_ID
					   AND DEL_YN             = 'N'
				    */
					jrParam.setField("YD_WRK_PROG_STAT"	, "W");
				    jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchIdOld);
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnWrkMgtPriorWrkNext", logId, mthdNm, "작업중인 Line-Off 크레인스케쥴 초기화");

					String ydDnWoLocOld = commUtils.trim(jsCrnWrkOld.getRecord(0).getFieldString("YD_DN_WO_LOC"));
					/**************************************************
					 * 4. 작업중인 Line-Off 권하위치 대차일때
					 *    TB_YD_STKLYR 권하정보 초기화
					 **************************************************/
					if( "TC".equals(ydDnWoLocOld.substring(2, 4)) && !"00".equals(ydDnWoLocOld.substring(6, 8))) {

						/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyr
						UPDATE TB_YD_STKLYR
						   SET MODIFIER = :V_MODIFIER
						     , MOD_DDTT = SYSDATE
						     , DEL_YN = 'N'
						     , STL_NO = :V_STL_NO
						     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
						 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
						   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
						   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
						*/
					    jrParam.setField("YD_STK_COL_GP"		, ydDnWoLocOld.substring(0, 6));
					    jrParam.setField("YD_STK_BED_NO"		, ydDnWoLocOld.substring(6, 8));
					    jrParam.setField("YD_STK_LYR_NO"		, "001");
					    jrParam.setField("STL_NO"				, "");
					    jrParam.setField("YD_STK_LYR_MTL_STAT"	, "E");
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyr", logId, mthdNm, "작업중인 Line-Off 대차권하위치 초기화");
					}

					String ydDnWoLoc = commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC"));
					/**************************************************
					 * 5. 우선 Line-Off 대차 권하위치 00번지이면 스케쥴 기동
					 *    TO위치 주작업
					 **************************************************/
					if( "TC".equals(ydDnWoLoc.substring(2, 4)) && "00".equals(ydDnWoLoc.substring(6, 8))) {

						jrParam.setField("YD_EQP_ID"	, ydEqpId);
						jrParam.setField("YD_WBOOK_ID"	, ydWbookId);

	                	/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdWrkbook
	                	SELECT YD_WBOOK_ID      AS YD_WBOOK_ID
	                		 ...
	                	     , (SELECT STL_NO
	                	          FROM TB_YD_WRKBOOKMTL
	                	         WHERE YD_WBOOK_ID = A.YD_WBOOK_ID
	                	           AND DEL_YN = 'N' AND ROWNUM = 1) AS STL_NO
	                	     , (SELECT ITEM1
	                	          FROM TB_YD_RULE
	                	         WHERE REPR_CD_GP = 'APP010'
	                	           AND DEL_YN = 'N') AS SCHLOG_YN
	                	  FROM TB_YD_WRKBOOK A
	                	 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
	        			 */
	                	JDTORecordSet jsWbook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdWrkbook", logId, mthdNm, "Line-Off 우선대상 작업예약 조회(대차상차)");

	        	    	if (jsWbook.size() < 1) {

	        				commUtils.printLog(logId, "오류:작업 예약 조회 시 오류 ydWbookId: " + ydWbookId, "SL");
	        				jrRtn.setField("RTN_CD" , "0");
	            			return jrRtn;
	        			}

	        	    	/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnSchByWBookId
	        	    	SELECT A.YD_EQP_ID               AS YD_EQP_ID
	        	    		   ...
	        	    	  FROM TB_YD_EQP  A
	        	    	     , TB_YD_CRNSCH B
	        	    	     , TB_YD_CRNWRKMTL C
	        	    	     , USRPTA.TB_PT_COILCOMM  D
	        	    	 WHERE B.YD_EQP_ID      = A.YD_EQP_ID
	        	    	   AND B.YD_CRN_SCH_ID  = C.YD_CRN_SCH_ID
	        	    	   AND C.STL_NO         = D.COIL_NO
	        	    	   AND B.YD_WBOOK_ID    = :V_YD_WBOOK_ID
	        	    	   AND B.YD_EQP_ID      = :V_YD_EQP_ID
	        	    	   AND B.DEL_YN = 'N'
	        	    	   AND C.DEL_YN = 'N'
	        	    	 ORDER BY B.YD_CRN_SCH_ID
	        	    	*/
	        	    	JDTORecordSet jsCrnSchTCar = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnSchByWBookId", logId, mthdNm, "Line-Off 우선대상 크레인스케줄 조회(대차상차)");

	        	    	if (jsCrnSchTCar.size() < 1) {

	        				commUtils.printLog(logId, "오류:크레인스케쥴 조회 시 오류 ydWbookId: " + ydWbookId, "SL");
	        				jrRtn.setField("RTN_CD" , "0");
	            			return jrRtn;
	        			}

	        	    	jsWbook.first();
	        	    	jsCrnSchTCar.first();
	        	    	JDTORecord jrWbook		= jsWbook.getRecord();
	        	    	JDTORecord jrCrnSchTCar	= jsCrnSchTCar.getRecord();

						// 동간입고
	    				JDTORecord jrRcvRtn = this.toLocPrimaryWork(logId, mthdNm, jrWbook, jrCrnSchTCar);
	    				jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));
	    				jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
	    				String sTcMoveYn = commUtils.nvl(jrRcvRtn.getFieldString("TC_MOVE_YN"), "N");

	    				// 중량 초과 대차 출발지시
	    				if( "Y".equals(sTcMoveYn) ) {
							JDTORecord jrTcSnd = commUtils.getParam(logId, mthdNm, modifier);
							jrTcSnd.setField("YD_SCH_CD", ydSchCd);
							try {
								/**********************************************************
								*  제품은 중량오버인 경우 대차 자동 출발
								**********************************************************/
								EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
								JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procTcarStsSetTcarD", new Class[] { JDTORecord.class }, new Object[] { jrTcSnd });

								jrRtn = commUtils.addSndData(jrRtn , jrRtn1);
							} catch (Exception se) {}
	    				}
	    				
        				/************************************
        				 * LINE OFF시  TO위치 결정 실패 UPDATE
        				 ************************************/
	    				if( "0".equals( commUtils.trim(jrRcvRtn.getFieldString("RTN_CD"))) ) {
			            	// TO위치 결정 실패시 XX010101 업데이트 처리
	        				commUtils.printLog(logId, "TO위치 결정 실패시 XX010101 업데이트 처리", "SL");
	        				
	        				JDTORecord jrParamSet	= commUtils.getParam(logId, mthdNm, modifier);
		        			jrParamSet.setField("YD_CRN_SCH_ID" , ydCrnSchId);
		        			jrParamSet.setField("YD_DN_WO_LOC"  , "XX010101");
	    					
		        			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnWrkMgtDnLoc 
		        			UPDATE TB_YD_CRNSCH CS
		        			   SET MODIFIER               = :V_MODIFIER
		        			     , MOD_DDTT               = SYSDATE
		        			     , YD_DN_WO_LOC           = :V_YD_DN_WO_LOC
		        			     , YD_UP_WO_LOC_XAXIS     = NVL(:V_YD_UP_WO_LOC_XAXIS     ,YD_UP_WO_LOC_XAXIS)
		        			     , YD_UP_WO_XAXIS_GAP_MAX = NVL(:V_YD_UP_WO_XAXIS_GAP_MAX ,YD_UP_WO_XAXIS_GAP_MAX)
		        			     , YD_UP_WO_XAXIS_GAP_MIN = NVL(:V_YD_UP_WO_XAXIS_GAP_MIN ,YD_UP_WO_XAXIS_GAP_MIN)
		        			     , YD_UP_WO_LOC_YAXIS     = NVL(:V_YD_UP_WO_LOC_YAXIS     ,YD_UP_WO_LOC_YAXIS)
		        			     , YD_UP_WO_YAXIS_GAP_MAX = NVL(:V_YD_UP_WO_YAXIS_GAP_MAX ,YD_UP_WO_YAXIS_GAP_MAX)
		        			     , YD_UP_WO_YAXIS_GAP_MIN = NVL(:V_YD_UP_WO_YAXIS_GAP_MIN ,YD_UP_WO_YAXIS_GAP_MIN)
		        			     , YD_UP_WO_LOC_ZAXIS     = NVL(:V_YD_UP_WO_LOC_ZAXIS     ,YD_UP_WO_LOC_ZAXIS)
		        			     , YD_UP_WO_ZAXIS_GAP_MAX = NVL(:V_YD_UP_WO_ZAXIS_GAP_MAX ,YD_UP_WO_ZAXIS_GAP_MAX)
		        			     , YD_UP_WO_ZAXIS_GAP_MIN = NVL(:V_YD_UP_WO_ZAXIS_GAP_MIN ,YD_UP_WO_ZAXIS_GAP_MIN)    
		        			     , UP_ROTATION_ANGLE      = NVL(:V_ROTATION_ANGLE         ,UP_ROTATION_ANGLE)
		        			     , YD_EQP_WRK_SH          = (SELECT COUNT(*) 
		        			                                   FROM TB_YD_CRNWRKMTL 
		        			                                  WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
		        			     , YD_CRN_GRAB_USE_RULE_ID= (SELECT B.YD_CAR_SCH_ID
		        			                                   FROM TB_YD_WRKBOOK A
		        			                                      , TB_YD_CARSCH B
		        			                                  WHERE B.DEL_YN='N'
		        			                                    AND (B.CAR_NO=A.CAR_NO OR B.TRN_EQP_CD = A.TRN_EQP_CD )
		        			                                    AND A.YD_WBOOK_ID =  (SELECT B1.YD_WBOOK_ID 
		        			                                                            FROM TB_YD_CRNSCH B1
		        			                                                           WHERE B1.YD_CRN_SCH_ID = CS.YD_CRN_SCH_ID )
		        			                                    AND ROWNUM <= 1)
		        			   
		        			 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
							*/
						    int intRtnVal = commDao.update(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnWrkMgtDnLoc", logId, mthdNm, "크레인스케줄 갱신");
						     
							if (intRtnVal <= 0) {
			    				commUtils.printLog(logId,  "크레인스케줄 To위치 Default값 등록 실패!!", "SL");
			    				jrRtn.setField("RTN_CD"	 , "0");
			    				jrRtn.setField("RTN_MSG" , "크레인스케줄 To위치 Default값 등록 실패!!");
			        			return jrRtn;
							}
	        			}
					}

					/**********************************************************
					*  일반입고 로직
					**********************************************************/
					JDTORecord jrParamSet = commUtils.getParam(logId, mthdNm, modifier);
					String ydDnWoLayerOld = commUtils.trim(jsCrnWrkOld.getRecord(0).getFieldString("YD_DN_WO_LAYER"));
					String sGenLineOff    = "N";  //일반입고 여부

					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getLineOffStartUpYn
					SELECT YD_SCH_CD
					  FROM TB_YD_SCHRULE
					 WHERE YD_SCH_CD = :V_YD_SCH_CD
					   AND DEL_YN = 'N'
					   AND YD_SCH_CD IN ('JHKD01LM','JGFD01LM','JEKD01LM','JCKD01LM','JBKD01LM')
					*/
					jrParamSet.setField("YD_SCH_CD" 	, ydSchCd);
					jrParamSet.setField("YD_CRN_SCH_ID" , ydCrnSchId);
					JDTORecordSet jsCrnSchChk = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getLineOffStartUpYn", logId, mthdNm, "일반작업 여부");
        	    	if (jsCrnSchChk.size() > 0) {
        	    		sGenLineOff = "Y";
        	    	}
					if ( "Y".equals(sGenLineOff) ) {
						/**************************************************
						 * 기존 야드 맵 clear
						 **************************************************/
						if (( !"XX010101".equals(ydDnWoLocOld) ) && ( !"".equals(ydDnWoLocOld) )){
							/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyr
							UPDATE TB_YD_STKLYR
							   SET MODIFIER = :V_MODIFIER
							     , MOD_DDTT = SYSDATE
							     , DEL_YN = 'N'
							     , STL_NO = :V_STL_NO
							     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
							 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
							   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
							   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
							*/
							jrParamSet.setField("YD_STK_COL_GP"			, ydDnWoLocOld.substring(0, 6));
							jrParamSet.setField("YD_STK_BED_NO"			, ydDnWoLocOld.substring(6, 8));
							jrParamSet.setField("YD_STK_LYR_NO"			, ydDnWoLayerOld);
							jrParamSet.setField("STL_NO"				, "");
							jrParamSet.setField("YD_STK_LYR_MTL_STAT"	, "E");
							commDao.update(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyr", logId, mthdNm, "작업중인 Line-Off 일반입고 초기화");
						}
						/**************************************************
						 * 5. 현재작업 스케쥴 기동처리함
						 **************************************************/
						if ("XX010101".equals(ydDnWoLoc)) {

							jrParamSet.setField("YD_EQP_ID"		, ydEqpId);
							jrParamSet.setField("YD_WBOOK_ID"	, ydWbookId);

							/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdWrkbook
		                	SELECT YD_WBOOK_ID      AS YD_WBOOK_ID
		                		 ...
		                	     , (SELECT STL_NO
		                	          FROM TB_YD_WRKBOOKMTL
		                	         WHERE YD_WBOOK_ID = A.YD_WBOOK_ID
		                	           AND DEL_YN = 'N' AND ROWNUM = 1) AS STL_NO
		                	     , (SELECT ITEM1
		                	          FROM TB_YD_RULE
		                	         WHERE REPR_CD_GP = 'APP010'
		                	           AND DEL_YN = 'N') AS SCHLOG_YN
		                	  FROM TB_YD_WRKBOOK A
		                	 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
		        			 */
		                	JDTORecordSet jsWbook1 = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdWrkbook", logId, mthdNm, "작업예약 조회(대차상차)");

		        	    	if (jsWbook1.size() < 1) {
		        				commUtils.printLog(logId, "오류:작업 예약 조회 시 오류 ydWbookId: " + ydWbookId, "SL");

		        				jrRtn.setField("RTN_CD" , "0");
		            			jrRtn.setField("RTN_MSG", "오류:작업 예약 조회 시 오류 ydWbookId: " + ydWbookId);
		            			return jrRtn;
		        			}

		        	    	/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnSchByWBookIdRe
		        	    	SELECT A.YD_EQP_ID               AS YD_EQP_ID
		        	    	     , A.YD_EQP_NAME             AS YD_EQP_NAME
		        	    	     , B.YD_CRN_SCH_ID           AS YD_CRN_SCH_ID
		        	    	     :
		        	    	     , D.EXTEND_CONVEYOR_BRANCH_CD -- 확장분기위치코드,
		        	    	     , D.HYSCO_TRANS_GP 	-- HYSCO이송수단,
		        	    	     , D.COOL_METHOD 	    -- 냉각방법,
		        	    	     , DECODE(D.CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',D.CURR_PROG_CD) AS CURR_PROG_CD
		        	    	     , D.RETURN_GP
		        	    	     , C.STL_NO

		        	    	  FROM TB_YD_EQP  A
		        	    	     , TB_YD_CRNSCH B
		        	    	     , TB_YD_CRNWRKMTL C
		        	    	     , USRPTA.TB_PT_COILCOMM  D
		        	    	 WHERE B.YD_EQP_ID      = A.YD_EQP_ID
		        	    	   AND B.YD_CRN_SCH_ID  = C.YD_CRN_SCH_ID
		        	    	   AND C.STL_NO         = D.COIL_NO
		        	    	   AND B.YD_CRN_SCH_ID  = :V_YD_CRN_SCH_ID
		        	    	   AND B.DEL_YN = 'N'
		        	    	   AND C.DEL_YN = 'N'
		        	    	 ORDER BY B.YD_CRN_SCH_ID
		        	    	*/
		        	    	JDTORecordSet jsCrnSchKd = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnSchByWBookIdRe", logId, mthdNm, "크레인스케줄 조회(대차상차)");

		        	    	if (jsCrnSchKd.size() < 1) {

		        				commUtils.printLog(logId, "오류:크레인스케쥴 조회 시 오류 ydWbookId: " + ydWbookId, "SL");

		        				jrRtn.setField("RTN_CD" , "0");
		            			jrRtn.setField("RTN_MSG", "오류:크레인스케쥴 조회 시 오류 ydWbookId: " + ydWbookId);
		            			return jrRtn;
		        			}

		        	    	jsWbook1.first();
		        	    	jsCrnSchKd.first();

		        	    	JDTORecord jrWbook1 = jsWbook1.getRecord();
		        	    	JDTORecord jrCrnSchKd = jsCrnSchKd.getRecord();

	        				/********************************************
	        				 * 일반입고 - 주작업TO위치
	        				 ********************************************/
	        				EJBConnector ejbConn = new EJBConnector("default","CCoilSchSeEJB", this);
	        				JDTORecord jrSchRtn = (JDTORecord)ejbConn.trx("toLocPrimaryWorkKD", new Class[] { String.class, String.class, JDTORecord.class, JDTORecord.class }, new Object[] {logId, mthdNm, jrWbook1, jrCrnSchKd });

							String rtnCd = commUtils.nvl(jrSchRtn.getFieldString("RTN_CD"), "0");
							String rtnMg = commUtils.nvl(jrSchRtn.getFieldString("RTN_MSG"), "");

							if( "0".equals(rtnCd) ) {
				            	// TO위치 결정 실패시 XX010101 업데이트 처리
		        				commUtils.printLog(logId, rtnMg, "SL");

		        				jrParamSet	= commUtils.getParam(logId, mthdNm, modifier);
			        			jrParamSet.setField("YD_CRN_SCH_ID" , ydCrnSchId);
			        			jrParamSet.setField("YD_DN_WO_LOC"  , "XX010101");

			        			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnWrkMgtDnLoc
			        			UPDATE TB_YD_CRNSCH CS
			        			   SET MODIFIER               = :V_MODIFIER
			        			     , MOD_DDTT               = SYSDATE
			        			     , YD_DN_WO_LOC           = :V_YD_DN_WO_LOC
			        			     , YD_UP_WO_LOC_XAXIS     = NVL(:V_YD_UP_WO_LOC_XAXIS     ,YD_UP_WO_LOC_XAXIS)
			        			     , YD_UP_WO_XAXIS_GAP_MAX = NVL(:V_YD_UP_WO_XAXIS_GAP_MAX ,YD_UP_WO_XAXIS_GAP_MAX)
			        			     , YD_UP_WO_XAXIS_GAP_MIN = NVL(:V_YD_UP_WO_XAXIS_GAP_MIN ,YD_UP_WO_XAXIS_GAP_MIN)
			        			     , YD_UP_WO_LOC_YAXIS     = NVL(:V_YD_UP_WO_LOC_YAXIS     ,YD_UP_WO_LOC_YAXIS)
			        			     , YD_UP_WO_YAXIS_GAP_MAX = NVL(:V_YD_UP_WO_YAXIS_GAP_MAX ,YD_UP_WO_YAXIS_GAP_MAX)
			        			     , YD_UP_WO_YAXIS_GAP_MIN = NVL(:V_YD_UP_WO_YAXIS_GAP_MIN ,YD_UP_WO_YAXIS_GAP_MIN)
			        			     , YD_UP_WO_LOC_ZAXIS     = NVL(:V_YD_UP_WO_LOC_ZAXIS     ,YD_UP_WO_LOC_ZAXIS)
			        			     , YD_UP_WO_ZAXIS_GAP_MAX = NVL(:V_YD_UP_WO_ZAXIS_GAP_MAX ,YD_UP_WO_ZAXIS_GAP_MAX)
			        			     , YD_UP_WO_ZAXIS_GAP_MIN = NVL(:V_YD_UP_WO_ZAXIS_GAP_MIN ,YD_UP_WO_ZAXIS_GAP_MIN)
			        			     , UP_ROTATION_ANGLE      = NVL(:V_ROTATION_ANGLE         ,UP_ROTATION_ANGLE)
			        			     , YD_EQP_WRK_SH          = (SELECT COUNT(*)
			        			                                   FROM TB_YD_CRNWRKMTL
			        			                                  WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
			        			     , YD_CRN_GRAB_USE_RULE_ID= (SELECT B.YD_CAR_SCH_ID
			        			                                   FROM TB_YD_WRKBOOK A
			        			                                      , TB_YD_CARSCH B
			        			                                  WHERE B.DEL_YN='N'
			        			                                    AND (B.CAR_NO=A.CAR_NO OR B.TRN_EQP_CD = A.TRN_EQP_CD )
			        			                                    AND A.YD_WBOOK_ID =  (SELECT B1.YD_WBOOK_ID
			        			                                                            FROM TB_YD_CRNSCH B1
			        			                                                           WHERE B1.YD_CRN_SCH_ID = CS.YD_CRN_SCH_ID )
			        			                                    AND ROWNUM <= 1)

			        			 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
								*/
							    int intRtnVal = commDao.update(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnWrkMgtDnLoc", logId, mthdNm, "크레인스케줄 갱신");

								if (intRtnVal <= 0) {

				    				commUtils.printLog(logId, "크레인스케줄 To위치 Default값 등록 실패!!", "SL");
				    				jrRtn.setField("RTN_CD"		, "0");
				    				jrRtn.setField("RTN_MSG"	, "크레인스케줄 To위치 Default값 등록 실패!!");
				        			return jrRtn;
								}
				            }
	        			}
					}
					/**************************************************
					 * 6. 우선 Line-Off 크레인스케쥴 YD_WRK_PROG_STAT = 'S'
					 *    야드L2 크레인작업지시(YDY5L004) 송신
					 **************************************************/
					/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnWrkMgtPriorWrkNext
					UPDATE TB_YD_CRNSCH A
					   SET MODIFIER           = :V_MODIFIER
					     , MOD_DDTT           = SYSDATE
					     , YD_WRK_PROG_STAT   = :V_YD_WRK_PROG_STAT
					     , YD_WORD_DT         = CASE WHEN :V_YD_WRK_PROG_STAT = 'W'         THEN NULL
					                                 WHEN :V_YD_WRK_PROG_STAT IN ('S', '1') THEN SYSDATE
					                                 ELSE YD_WORD_DT
					                            END
					     , YD_L2_REQUEST_STAT = NVL(:V_YD_L2_REQUEST_STAT, YD_L2_REQUEST_STAT)
					 WHERE YD_CRN_SCH_ID      = :V_YD_CRN_SCH_ID
					   AND DEL_YN             = 'N'
					*/
					jrParam.setField("YD_WRK_PROG_STAT"		, "S");
					jrParam.setField("YD_L2_REQUEST_STAT"	, "1");
					jrParam.setField("YD_CRN_SCH_ID"		, ydCrnSchId);
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnWrkMgtPriorWrkNext", logId, mthdNm, "Line-Off 우선대상 크레인스케쥴 진행상태 UPDATE");

					/**********************************************************
					* 7. 크레인작업지시(YDY5L004) 송신
					**********************************************************/
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);
					jrYdMsg.setResultMsg(mthdNm);

					jrYdMsg.setField("JMS_TC_CD"	, "YDY5L004");	//크레인작업작업지시
					jrYdMsg.setField("MSG_GP"		, "I");			//야드설비ID
					jrYdMsg.setField("YD_CRN_SCH_ID", ydCrnSchId);	//야드크레인스케쥴ID

					jrRtn = commUtils.addSndData(jrRtn,coilDao.getMsgL2("YDY5L004", jrYdMsg));
				}
			}

			commUtils.printLog(logId, mthdNm , "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 대차스케줄 공대차출발지시 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord updTcarSchLevWo(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차스케줄 공대차출발지시 처리[CCoilSchSeEJB.updTcarSchLevWo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn.trx("procTcarSchLevWo", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			//JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procTcarSchLevWo", new Class[] { GridData.class }, new Object[] { commUtils.jdtoRecordToGridParam(jrTcSnd) });
			
			jrRtn = commUtils.addSndData(jrRtn);			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} 
	
	/**
	 * [A] 오퍼레이션명 : 크레인스케줄 작업예약재료 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return void
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public void updCrnSchWB(JDTORecord jrParam) throws DAOException {
		String mthdNm = "크레인스케줄 작업예약재료 수정[CCoilSchSeEJB.updCrnSchWB] < " + jrParam.getResultMsg();
		String logId  = jrParam.getResultCode();

		try {
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 오퍼레이션명 : 2열연 코일야드 크레인 스케줄 GROUPING PARAMETER DATA SETTING (Y5CrnSchSortCoil())
	 *  
	 * @param  String sEqpId, ydSchCd, jrParamSet, jsCoilGrp
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public JDTORecord crnSchGrp( String logId, String mthdNms, JDTORecord jrParamSet, JDTORecordSet jsCoilGrp )throws JDTOException  {
    	String 	mthdNm = "그룹핑 파라미터 셋팅 [CCoilSchSeEJB.crnSchGrp] < " + mthdNms;
    	JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
    	
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");

			JDTORecordSet jsCrnSchResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	JDTORecordSet jsWrkBookMtl   = JDTORecordFactory.getInstance().createRecordSet("Temp");
			//레코드셋 정렬 시
			String 	sLogMsg  = "";
			int 	intRtnVal = 0;
			
			String ydWbookId 	= commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_ID"));
			String ydSchCd		= commUtils.trim(jrParamSet.getFieldString("YD_SCH_CD"));  
			String sUpLocChkYn	= commUtils.trim(jrParamSet.getFieldString("UP_LOC_CHK_YN"));
			String sModifier    = commUtils.trim(jrParamSet.getFieldString("MODIFIER"));
			
			commUtils.printLog(logId, "권상위치 CHECK 여부:" + sUpLocChkYn, "SL");   
			if ("Y".equals(sUpLocChkYn)) {
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getccoilWrkBookMtl
				WITH DATA_TABLE AS (
				 -- 대상재 최신적재위치 READ
				SELECT DISTINCT A.YD_STK_COL_GP
				              , A.YD_STK_BED_NO
				              , A.YD_STK_LYR_NO
				              , A.STL_NO
				              , A.YD_STK_LYR_MTL_STAT
				  FROM TB_YD_STKLYR A
				     , (SELECT A1.YD_STK_COL_GP
				             , A1.YD_STK_BED_NO
				             , A1.YD_STK_LYR_NO
				          FROM TB_YD_STKLYR A1 
				             , TB_YD_WRKBOOKMTL B1
				         WHERE A1.STL_NO = B1.STL_NO
				           AND B1.DEL_YN = 'N'
				           AND B1.YD_WBOOK_ID = :V_YD_WBOOK_ID ) B
				 WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
				   AND A.YD_STK_BED_NO IN (B.YD_STK_BED_NO, CASE WHEN B.YD_STK_LYR_NO = '002' THEN TO_NUMBER(B.YD_STK_BED_NO) 
							                                     ELSE TO_NUMBER(B.YD_STK_BED_NO) - TO_NUMBER(SF_YD_SKID_INTERVAL_GAP(A.YD_STK_COL_GP)) END  )
				   AND A.YD_STK_LYR_NO IN (CASE WHEN B.YD_STK_LYR_NO = '002' THEN '002'
							                    WHEN A.YD_STK_BED_NO = TO_NUMBER(B.YD_STK_BED_NO)  THEN '001'
							                    ELSE '002' END , '002' )
				   AND A.YD_STK_LYR_MTL_STAT IN ('C','U')     
				)
				SELECT CASE WHEN YD_STK_LYR_MTL_STAT = 'C' AND YD_WBOOK_ID IS NOT NULL THEN ROWNUM + 100
				            WHEN YD_STK_LYR_MTL_STAT = 'C' THEN ROWNUM
				            ELSE 0 END HANDLING_CNT
				     , CASE WHEN YD_WBOOK_ID IS NOT NULL THEN 'S' --주작업
				            ELSE 'W' END YD_TO_LOC_DCSN_MTD   
				     , D.* 
				  FROM
				(
				SELECT A.YD_STK_COL_GP
				     , A.YD_STK_BED_NO
				     , A.YD_STK_LYR_NO
				     , A.STL_NO
				     , A.YD_STK_LYR_MTL_STAT
				     , B.YD_WBOOK_ID
				     , B.YD_UP_COLL_SEQ
				     , C.YD_CRN_SCH_ID
				     , C.YD_SCH_CD   AS CRNSCH_YD_SCH_CD
				     , C.YD_WBOOK_ID AS CRNSCH_WBOOK_ID
				     , C.YD_WRK_PROG_STAT   
				  FROM DATA_TABLE A
				     , (SELECT YD_WBOOK_ID 
				             , STL_NO
				             , YD_UP_COLL_SEQ
				          FROM TB_YD_WRKBOOKMTL
				         WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID  ) B
				     , (SELECT AA.YD_CRN_SCH_ID
				             , AA.YD_WRK_PROG_STAT
				             , BB.STL_NO
				             , AA.YD_SCH_CD
				             , AA.YD_WBOOK_ID
				          FROM TB_YD_CRNSCH AA
				             , TB_YD_CRNWRKMTL BB
				         WHERE AA.YD_CRN_SCH_ID = BB.YD_CRN_SCH_ID
				           AND AA.DEL_YN = 'N'
				           AND BB.DEL_YN = 'N' ) C      
				 WHERE A.STL_NO = B.STL_NO(+)
				   AND A.STL_NO = C.STL_NO(+)
				 ORDER BY A.YD_STK_COL_GP, A.YD_STK_BED_NO, A.YD_STK_LYR_NO DESC 
				 ) D
				*/ 
				jsWrkBookMtl = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getccoilWrkBookMtl", logId, mthdNm, "작업예약 재료정보 조회");
				if (jsWrkBookMtl.size() <= 0) {
					sLogMsg = "크레인작업재료조회 >> 조회 Data 없음";
					commUtils.printLog(logId, sLogMsg, "SL");    			

					jrRtn.setField("RTN_CD" , "0");	
	    			jrRtn.setField("RTN_MSG", sLogMsg);
	    			return jrRtn;
				}
			} else {
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getccoilWrkBookMtlNoLayer
				SELECT A.STL_NO
				     , CASE WHEN B.YD_SCH_CD LIKE 'J_TC__MM' AND SUBSTR(B.YD_SCH_CD, 5, 2) IN ('01', '02')
				            -- 동간입고 대차 1,2번 / 5번 분리 -----------------------------------------
				                 THEN SUBSTR(B.YD_SCH_CD, 0, 2) ||
				                      DECODE(SUBSTR(B.YD_SCH_CD, 2, 1)
				                                  , 'H', 'KD01' -- #1 SPM
				                                  , 'E', 'KD02' -- #2 SPM
				                                  , 'A', 'KD05' -- #5 SPM
				                                  , 'G', 'FD01' -- #1 HFL
				                                  , 'C', 'FD04' -- #4 HFL
				                                  , 'XXXX')
				            WHEN B.YD_SCH_CD LIKE 'J_TC__MM' AND SUBSTR(B.YD_SCH_CD, 5, 2) = '05'
				                 THEN SUBSTR(B.YD_SCH_CD, 0, 2) ||
				                      DECODE(SUBSTR(B.YD_SCH_CD, 2, 1)
				                                  , 'C', 'KD03' -- #3 SPM
				                                  , 'B', 'KD04' -- #4 SPM
				                                  , 'XXXX')
				            -- 입고 -------------------------------------------------------------------
				            ELSE SUBSTR(B.YD_SCH_CD, 0, 4) ||
				                 DECODE(SUBSTR(B.YD_SCH_CD, 2, 2)
				                      , 'HK', '01' -- #1 SPM
				                      , 'EK', '02' -- #2 SPM
				                      , 'CK', '03' -- #3 SPM
				                      , 'BK', '04' -- #4 SPM
				                      , 'AK', '05' -- #5 SPM
				                      , 'GF', '01' -- #1 HFL
				                      , 'CF', '04' -- #4 HFL
				                      , '01')      -- 수입
				       END AS YD_STK_COL_GP
				     , '00'                        AS YD_STK_BED_NO
				     , '001'                       AS YD_STK_LYR_NO
				     , '1' AS HANDLING_CNT
				     , 'S' AS YD_TO_LOC_DCSN_MTD
				     , 'C' AS YD_STK_LYR_MTL_STAT
				  FROM TB_YD_WRKBOOKMTL A
				     , TB_YD_WRKBOOK    B
				     , TB_YD_STOCK      C
				 WHERE A.STL_NO       = C.STL_NO(+)
				   AND A.YD_WBOOK_ID = B.YD_WBOOK_ID
				   AND A.YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND A.DEL_YN = 'N'
				   AND B.DEL_YN = 'N'
			    */
				jsWrkBookMtl = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getccoilWrkBookMtlNoLayer", logId, mthdNm, "적재위치 CHECK 안함 작업예약 재료정보 조회");
				if (jsWrkBookMtl.size() <= 0) {
	    			
					sLogMsg = "크레인작업재료조회 >> 조회 Data 없음";
					commUtils.printLog(logId, sLogMsg, "SL");    			

					jrRtn.setField("RTN_CD" , "0");	
	    			jrRtn.setField("RTN_MSG", sLogMsg);
	    			return jrRtn;
	    			
				}	
			}
			JDTORecord jrWrkBookMtl = JDTORecordFactory.getInstance().create();
			JDTORecord jrParam      = JDTORecordFactory.getInstance().create();
			//작업예약재료를 조회해서 받는다. 코일은 재료별로 처리하도록...
			for (int Loop_i = 1; Loop_i <= jsWrkBookMtl.size(); Loop_i++) {
				jsWrkBookMtl.absolute(Loop_i);
				
				jrWrkBookMtl = JDTORecordFactory.getInstance().create();
				jrWrkBookMtl.setRecord( jsWrkBookMtl.getRecord() );
				
				//권상대기라면 해당 재료의 스케줄 코드를 확인한다.
				if ("U".equals(commUtils.trim(jrWrkBookMtl.getFieldString("YD_STK_LYR_MTL_STAT"))) ) {
					//현재 스케줄 코드와 상위베드단 재료의 스케줄코드를 비교한다.
					commUtils.printLog(logId, "Loop_i:" + Loop_i , "SL");  
					commUtils.printLog(logId, "CRNSCH_WBOOK_ID:" + commUtils.trim(jrWrkBookMtl.getFieldString("CRNSCH_WBOOK_ID")) , "SL");  
					commUtils.printLog(logId, "ydWbookId:" + ydWbookId , "SL");  
					
					if (!commUtils.trim(jrWrkBookMtl.getFieldString("CRNSCH_WBOOK_ID")).equals(ydWbookId)) {

						commUtils.printLog(logId, "작업 상태:" + jrWrkBookMtl.getFieldString("YD_WRK_PROG_STAT") , "SL");  
			    			
    		    		if ("W".equals(jrWrkBookMtl.getFieldString("YD_WRK_PROG_STAT"))) {
    		    			
    		    			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
    		    			jrParam.setField("YD_CRN_SCH_ID" , jrWrkBookMtl.getFieldString("YD_CRN_SCH_ID"));
    		    			jrParam.setField("YD_SCH_PRIOR"	, "1"); 
    		    		         
    		    		    /*com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updccoilCrnSchPrior
    		    		    UPDATE TB_YD_CRNSCH
    		    		       SET MODIFIER = :V_MODIFIER
    		    		         , MOD_DDTT = SYSDATE
    		    		         , YD_SCH_PRIOR = :V_YD_SCH_PRIOR 
    		    		     WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
    		    		    */		 
	    		    		intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updccoilCrnSchPrior", logId, mthdNm, "크레인스케쥴 갱신");
	    		    		
	    		    		if (intRtnVal <= 0) {

	    		    			sLogMsg = "실패:크레인스케쥴 UPDATE 할 항목이 없습니다";
	    						commUtils.printLog(logId, sLogMsg, "SL");    			

	    						jrRtn.setField("RTN_CD" , "0");	
	    		    			jrRtn.setField("RTN_MSG", sLogMsg);
	    		    			return jrRtn;	    		    			
	    		    		}
	    		    		
//					    	3. 해당 작업예약 update				    		    			
	    		    		 
	    		    		jrParam.setField("YD_SCH_CD"	, jrWrkBookMtl.getField("CRNSCH_YD_SCH_CD"));
	    		    		jrParam.setField("YD_WBOOK_ID"	, jrWrkBookMtl.getField("CRNSCH_WBOOK_ID"));
    		    		    /* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updccoilWrkBookPrior 
    		    		    UPDATE TB_YD_WRKBOOK
    		    		       SET MODIFIER = :V_MODIFIER
    		    		         , MOD_DDTT = SYSDATE
    		    		         , YD_SCH_PRIOR = :V_YD_SCH_PRIOR
    		    		         , YD_SCH_CD    = :V_YD_SCH_CD
    		    		     WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
    		    		    */
	    		    		intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updccoilWrkBookPrior", logId, mthdNm, "작업예약 갱신");
	    		    		if (intRtnVal <= 0) {
	    		    			
	    						sLogMsg = "실패:작업예약 우선순위 UPDATE 실패!";
	    						commUtils.printLog(logId,  sLogMsg, "SL");    			

	    						jrRtn.setField("RTN_CD" , "0");	
	    		    			jrRtn.setField("RTN_MSG", sLogMsg);
	    		    			return jrRtn;	 
	    		    		}	
    		    		}
    				}	
					//같다면 앞에서 이미 등록 되었기때문에 따로 등록하지않는다.우선순위 -> 1순위로 변경처리
					commUtils.printLog(logId, "그룹작업이 끝난 재료입니다. SKIP처리(정상)", "SL");  
				} else {
					//STACK_LAYER_STAT = 'U' 상태 RECORD 제거
					jsCrnSchResult.addRecord(jrWrkBookMtl);
				}	
			}				

			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_WBOOK_ID", ydWbookId);
 			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getccoilStockCar 
 			SELECT A.YD_WBOOK_ID         AS YD_WBOOK_ID
 			     , A.STL_NO  
 			     , B.YD_CAR_UPP_LOC_CD    AS YD_CAR_UPP_LOC_CD   --차상위치
 			     , (CASE WHEN MAX(B.CAR_NO) LIKE 'GT%' THEN 'TT' 
 			             WHEN MAX(B.CAR_NO) IS NULL THEN 'X' ELSE 'T' END) AS CAR_KIND
 			 FROM TB_YD_WRKBOOKMTL A
 			     ,TB_YD_STOCK B
 			 WHERE A.STL_NO = B.STL_NO
 			   AND A.YD_WBOOK_ID = :V_YD_WBOOK_ID
 			   AND A.DEL_YN='N'
 			 GROUP BY A.YD_WBOOK_ID, A.YD_STK_COL_GP, A.YD_STK_BED_NO,A.STL_NO,B.YD_CAR_UPP_LOC_CD
 			ORDER BY DECODE(CAR_KIND,'T',YD_CAR_UPP_LOC_CD,'1'),MAX(A.YD_UP_COLL_SEQ)
 			*/
			JDTORecordSet jsStockCar = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getccoilStockCar", logId, mthdNm, "재료상태정보(차량여부)");
			if (jsStockCar.size() <= 0) {
    			
				sLogMsg = "실패:작업재료 정보 이상.!";
				commUtils.printLog(logId, sLogMsg, "SL");    			

				jrRtn.setField("RTN_CD" , "0");	
    			jrRtn.setField("RTN_MSG", sLogMsg);
    			return jrRtn;	
			}
			
			jsStockCar.absolute(1);
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setRecord(jsStockCar.getRecord());
			
			String sCarKind = commUtils.trim(jrParam.getFieldString("CAR_KIND"));
			
			String ydSchCdGp = "";
			//레코드셋 정렬
			JDTORecord jrAfter = JDTORecordFactory.getInstance().create();
			JDTORecord jrCurrt = JDTORecordFactory.getInstance().create();

			
			//(trailer인경우)
			if ("T".equals(sCarKind)||"TR".equals(sCarKind)) {
				ydSchCdGp	=	ydSchCd.substring(2, 4);
			}
			
			//차량작업이 아닌경우 정렬
 			if (!"TR".equals(ydSchCdGp) && !"PT".equals(ydSchCdGp) && !"TT".equals(ydSchCdGp)) {
				
				for (int Loop_i = 1; Loop_i < jsCrnSchResult.size(); Loop_i++) {
				
					for (int Loop_j = Loop_i + 1; Loop_j < jsCrnSchResult.size() + 1; Loop_j++) {
						
						jsCrnSchResult.absolute(Loop_i);
						jrCurrt = jsCrnSchResult.getRecord();
						
						jsCrnSchResult.absolute(Loop_j);
						jrAfter = jsCrnSchResult.getRecord();

						if (jrCurrt.getFieldInt("HANDLING_CNT") > jrAfter.getFieldInt("HANDLING_CNT")) {
							jsCrnSchResult = this.procSchGrpSortCoil(Loop_i, Loop_j, jsCrnSchResult);
							if (jsCrnSchResult.size() < 1) {
	    		    			
	    						sLogMsg = "실패:재료 정렬 중 Error.!";
	    						commUtils.printLog(logId, sLogMsg, "SL");    			

	    						jrRtn.setField("RTN_CD" , "0");	
	    		    			jrRtn.setField("RTN_MSG", sLogMsg);
	    		    			return jrRtn;
							}
						}
					}
				} 
 			}


			//재료 중복
 			String sStlNo = "";
			for (int Loop_i = 1; Loop_i <= jsCrnSchResult.size(); Loop_i++) {
				jsCrnSchResult.absolute(Loop_i);
				/*System.out.println("HandlingCount = " 
				                     + rsReturn.getRecord().getFieldString("HANDLING_CNT") 
				                     + rsReturn.getRecord().getFieldString("STL_NO")
			                         + rsReturn.getRecord().getFieldString("YD_TO_LOC_DCSN_MTD"));
			    */                     
				jrCurrt = jsCrnSchResult.getRecord();
		
				if (!"".equals(jsCrnSchResult.getRecord().getFieldString("STL_NO")) && !jsCrnSchResult.getRecord().getFieldString("STL_NO").equals(sStlNo)) {
					sStlNo = jsCrnSchResult.getRecord().getFieldString("STL_NO");
					jsCoilGrp.addRecord(jrCurrt);
				}
			}
			
			commUtils.printLog(logId, mthdNm, "S-");	

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		jrRtn.setField("RTN_CD" , "1");	
		return jrRtn;

	} //end of 
	
	
	/**
     * 오퍼레이션명 : 레코드 치환(H/J)
     *  
     * @param   recPara1, recPara2, recResult
     * @return  intRtnVal '1': 성공   '-1': 실패
     * @throws  JDTOException
     */
    public JDTORecordSet procSchGrpSortCoil (int intLoop_i, int intLoop_j, JDTORecordSet jsCrnSchResult) {

    	JDTORecordSet jsTemp = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecord    jrTemp = JDTORecordFactory.getInstance().create();

    	try{
		
    		for (int Loop_i = 1;  Loop_i <= jsCrnSchResult.size(); Loop_i++) {
				if (Loop_i == intLoop_i) {
					jsCrnSchResult.absolute(intLoop_j);
					jrTemp = jsCrnSchResult.getRecord();
				}else if (Loop_i == intLoop_j) {
					jsCrnSchResult.absolute(intLoop_i);
					jrTemp = jsCrnSchResult.getRecord();
				}else{
					jsCrnSchResult.absolute(Loop_i);
					jrTemp = jsCrnSchResult.getRecord();
				}
				jsTemp.addRecord(jrTemp);
			}
		}catch(Exception e) {

        }//end of try~catch
		return jsTemp;
    }
    
	/**
     * 오퍼레이션명 : 스케줄링 크레인 스케줄 등록
     *  
     * @param   vResult, msgRecord
     * @return  intRtnVal
     * @throws  JDTOException
     */
    public JDTORecord crnSchIns(String logId, String mthdNms , JDTORecord jrParamSet , JDTORecordSet jsCoilGrp) throws JDTOException {
   		String mthdNm = "스케줄링 크레인 스케줄 등록[CCoilSchSeEJB.crnSchIns] < " + mthdNms;
    	JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try{
			
			commUtils.printLog(logId, mthdNm, "S+");
			
			String ydEqpId			= commUtils.trim(jrParamSet.getFieldString("YD_EQP_ID"));
			String ydSchCd  		= commUtils.trim(jrParamSet.getFieldString("YD_SCH_CD"));
			String ydWbookId      	= commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_ID"));
			String ydSchPrior  		= commUtils.trim(jrParamSet.getFieldString("YD_SCH_PRIOR"));
			String ydWbookDt 		= commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_DT"));
			String ydWbToLocGuide	= commUtils.trim(jrParamSet.getFieldString("YD_TO_LOC_GUIDE"));
			String sUpLocChkYn		= commUtils.trim(jrParamSet.getFieldString("UP_LOC_CHK_YN")); 
			String sModifier		= commUtils.trim(jrParamSet.getFieldString("MODIFIER"));       //수정자(Backup Only)
			String WbRegister       = commUtils.trim(jrParamSet.getFieldString("WB_REGISTER")); //작업예약 등록자
			
			commUtils.printParam(logId, jsCoilGrp);
			
 			//크레인 스케줄에 Insert한다.	
			JDTORecordSet jsWbook 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecord jrInCrn    	= JDTORecordFactory.getInstance().create();
			JDTORecord jrInCrnMtl	= JDTORecordFactory.getInstance().create();
			JDTORecord	jrParam2	= JDTORecordFactory.getInstance().create();
			
			int    intRtnVal  = 0;
			String sLogMsg    = "";
			for (int i = 1; i <= jsCoilGrp.size(); i++) {

				jsCoilGrp.absolute(i);
				jrInCrn = JDTORecordFactory.getInstance().create();
				jrInCrn = jsCoilGrp.getRecord();
				
				jrInCrn.setField("YD_WBOOK_ID"	, ydWbookId);
				jrInCrn.setField("STL_NO"		, commUtils.trim(jrInCrn.getFieldString("STL_NO"  )));
				
				
				//작업예약ID 조회-----------------------------------------------------------------------------------------
				if (!"".equals(ydWbookId)) {
					/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnSchWbookChk 
					SELECT YD_WBOOK_ID 
					     ,NVL((SELECT YD_STK_BED_NO FROM TB_YD_EQPTRACKING B
						       WHERE B.STL_NO= :V_STL_NO
						         AND EQP_CD= 'SPM5'), '00') AS EQP_BED_NO
					  FROM TB_YD_WRKBOOK A
					 WHERE A.YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND A.DEL_YN      = 'N'
					   AND NOT EXISTS (SELECT 1 FROM TB_YD_CRNSCH B 
					                               , TB_YD_CRNWRKMTL C
					                           WHERE B.YD_CRN_SCH_ID = C.YD_CRN_SCH_ID
					                             AND B.DEL_YN = 'N' 
					                             AND C.DEL_YN = 'N'
					                             AND C.STL_NO = :V_STL_NO
					                             AND B.YD_WBOOK_ID = A.YD_WBOOK_ID)
					*/
					jsWbook = commDao.select(jrInCrn, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnSchWbookChk", logId, mthdNm, "작업예약 조회(스케쥴생선 전)");
				}
				
				if (jsWbook.size() < 1) {
					
					sLogMsg = "오류:크레인 스케줄 등록중  이미 크레인 스케쥴 존재함";
					commUtils.printLog(logId, sLogMsg + " ydWbookId: " + ydWbookId, "SL");    			

					jrRtn.setField("RTN_CD" , "0");	
	    			jrRtn.setField("RTN_MSG", sLogMsg);
	    			return jrRtn;
				}
				//-------------------------------------------------------------------------------------------------------
 
				jsWbook.absolute(1);
				jrParam2 = JDTORecordFactory.getInstance().create();
				jrParam2.setRecord(jsWbook.getRecord());
				
				String seqpBedNo = commUtils.trim(jrParam2.getFieldString("EQP_BED_NO"));
				
				//#5 SPM 11번지 코일 입고 인 경우 우선순위 1순위 적용 REQ202406591095
				if("11".equals(seqpBedNo)){
					ydSchPrior = "1";
				}
	    		
				
				/**********************************************************
				*  크레인 스케줄 등록
				**********************************************************/			
				//크레인스케줄ID를 할당받는다
				String ydCrnSchId = coilDao.getSeqId(logId, mthdNm, "CrnSch");

				jrInCrn.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
				jrInCrn.setField("YD_WBOOK_ID"		, ydWbookId);
				jrInCrn.setField("YD_EQP_ID"		, ydEqpId);
				jrInCrn.setField("YD_GP"			, jrInCrn.getFieldString("YD_STK_COL_GP").substring(0,1));
				jrInCrn.setField("YD_BAY_GP"		, jrInCrn.getFieldString("YD_STK_COL_GP").substring(1,2));
				jrInCrn.setField("YD_SCH_CD"		, ydSchCd);	
				jrInCrn.setField("REGISTER"			, sModifier);	
				jrInCrn.setField("YD_SCH_PRIOR"		, ydSchPrior);
				jrInCrn.setField("YD_WBOOK_DT"		, ydWbookDt);
				jrInCrn.setField("YD_SCH_ST_GP"		, "A");
				jrInCrn.setField("YD_UP_WO_LOC"		, jrInCrn.getFieldString("YD_STK_COL_GP") + jrInCrn.getFieldString("YD_STK_BED_NO"));
				jrInCrn.setField("YD_UP_WO_LAYER"	, jrInCrn.getFieldString("YD_STK_LYR_NO"));
				jrInCrn.setField("YD_TO_LOC_GUIDE"	, ydWbToLocGuide);
				jrInCrn.setField("YD_WRK_PROG_STAT"	, "W");
				jrInCrn.setField("YD_SCH_REQ_GP"    , WbRegister);
				jrInCrn.setField("MODIFIER"    		, sModifier);
				
				if ("".equals(commUtils.trim(jrInCrn.getFieldString("YD_UP_WO_LOC")))) {
	    			
					sLogMsg = "오류:권상지시위치가 없습니다";
					commUtils.printLog(logId, sLogMsg + " ydWbookId: " + ydWbookId, "SL");    			

					jrRtn.setField("RTN_CD" , "0");	
	    			jrRtn.setField("RTN_MSG", sLogMsg);
	    			return jrRtn;
				}

				/***************************************************************************
				 * 트랜잭션 분리 ?
				 ***************************************************************************/
				//intRtnVal = commDao.insert(jrInCrn, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.insYdCrnsch", logId, mthdNm, "TB_YD_CRNSCH 생성");
				intRtnVal = commDao.insertTx(jrInCrn, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.insYdCrnsch", logId, mthdNm, "TB_YD_CRNSCH 생성");
				
				if (intRtnVal < 1) {

					sLogMsg = "오류:크레인 스케줄 등록중  Error";
					commUtils.printLog(logId, sLogMsg + " ydWbookId: " + ydWbookId, "SL");    			

					jrRtn.setField("RTN_CD" , "0");	
	    			jrRtn.setField("RTN_MSG", sLogMsg);
	    			return jrRtn;
					
					
				}
  
				/**********************************************************
				*  크레인 스케줄 작업재료 등록
				**********************************************************/			
				jrInCrnMtl = JDTORecordFactory.getInstance().create();
				jrInCrnMtl.setField("YD_CRN_SCH_ID", ydCrnSchId);
				/*
				 * 기존의 MAIN_WRK_YN 은 주작업이 Y 보조작업이 N으로 들어옴 
				 * 크레인작업재료에는 보조작업여부에 값은 보조작업인경우 Y 주작업인경우 N로 셋팅!
				 */
				
				if ("W".equals(jrInCrn.getFieldString("YD_TO_LOC_DCSN_MTD"))) {
					jrInCrnMtl.setField("YD_AID_WRK_YN", "Y"); //보조작업			 
					
				}else{
					jrInCrnMtl.setField("YD_AID_WRK_YN", "N");
				}
				jrInCrnMtl.setField("REGISTER"			, sModifier);
				jrInCrnMtl.setField("STL_NO"			, commUtils.trim(jrInCrn.getFieldString("STL_NO"  )));
				jrInCrnMtl.setField("YD_STK_LYR_NO"		, commUtils.trim(jrInCrn.getFieldString("YD_STK_LYR_NO"  )));
				jrInCrnMtl.setField("YD_STK_LOT_TP"		, commUtils.trim(jrInCrn.getFieldString("YD_STK_LOT_TP"  )));
				jrInCrnMtl.setField("YD_STK_LOT_CD"		, commUtils.trim(jrInCrn.getFieldString("YD_STK_LOT_CD"  )));
				jrInCrnMtl.setField("HCR_GP"			, commUtils.trim(jrInCrn.getFieldString("HCR_GP"  )));
				jrInCrnMtl.setField("STL_PROG_CD"		, commUtils.trim(jrInCrn.getFieldString("STL_PROG_CD"  )));
				jrInCrnMtl.setField("YD_MTL_ITEM"		, commUtils.trim(jrInCrn.getFieldString("YD_MTL_ITEM"  )));
				jrInCrnMtl.setField("YD_ROUTE_GP"		, "");
				jrInCrnMtl.setField("YD_TO_LOC_DCSN_MTD", commUtils.trim(jrInCrn.getFieldString("YD_TO_LOC_DCSN_MTD")));
				//크레인작업재료 생성
				
				
				
				/***************************************************************************
				 * 트랜잭션 분리 ?
				 ***************************************************************************/
				//intRtnVal = commDao.insert(jrInCrnMtl, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.insYdCrnwrkmtl", logId, mthdNm, "TB_YD_CRNWRKMTL 생성");
				intRtnVal = commDao.insertTx(jrInCrnMtl, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.insYdCrnwrkmtl", logId, mthdNm, "TB_YD_CRNWRKMTL 생성");
				
				if (intRtnVal < 1) {
					sLogMsg = "오류:크레인 스케줄 작업재료 등록중 실패";
					commUtils.printLog(logId, sLogMsg + " ydWbookId: " + ydWbookId, "SL");    			

					jrRtn.setField("RTN_CD" , "0");	
	    			jrRtn.setField("RTN_MSG", sLogMsg);
	    			return jrRtn;
				}
				
				commUtils.printLog(logId, "권상위치CHECK 여부:" + sUpLocChkYn, "SL"); 
				if ("Y".equals(sUpLocChkYn)) {
					/**********************************************************
					*  적치단의 재료상태를 권상대기로 변경
					**********************************************************/		
					jrInCrn.setField("YD_STK_LYR_MTL_STAT", "U");
					
					/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updStkLyrStlStat 
					UPDATE TB_YD_STKLYR            
					   SET MOD_DDTT     = SYSDATE             
					     , MODIFIER     = :V_MODIFIER             
					     , YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT,YD_STK_LYR_MTL_STAT)
					 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
					   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
					   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
					   AND STL_NO = :V_STL_NO
			    	 */  
					intRtnVal = commDao.update(jrInCrn, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updStkLyrStlStat", logId, mthdNm, "TB_YD_STKLYR 갱신");
					if (intRtnVal < 1) {
						sLogMsg = "오류:재료[" + jrInCrn.getFieldString("STL_NO") + "]적재단 변경시 오류";
						commUtils.printLog(logId, sLogMsg + " ydWbookId: " + ydWbookId, "SL");    			
						jrRtn.setField("RTN_CD" , "0");	
		    			jrRtn.setField("RTN_MSG", sLogMsg);
		    			return jrRtn;
					}
				} else {
					/**********************************************************
					*  적치단의 재료상태를 권상대기로 변경
					*  -ERROR 처리 안함
					**********************************************************/		
					jrInCrn.setField("YD_STK_LYR_MTL_STAT", "U");
					
					/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updStkLyrStlStatStlNo  
					UPDATE TB_YD_STKLYR            
					   SET MOD_DDTT     = SYSDATE             
					     , MODIFIER     = :V_MODIFIER             
					     , YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT,YD_STK_LYR_MTL_STAT)
					 WHERE SUBSTR(YD_STK_COL_GP,1,1) in ('H','J')
					   AND STL_NO = :V_STL_NO
			    	 */  
					intRtnVal = commDao.update(jrInCrn, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updStkLyrStlStatStlNo", logId, mthdNm, "TB_YD_STKLYR 갱신");
				}
				
				/**********************************************************
				* 설비 제품입고 대상 3본이상시 우선순위 UPDATE
				***********************************************************/
				commUtils.printLog(logId, "설비 LineOff 확인["+ jrInCrn.getFieldString("YD_STK_COL_GP").substring(2,4) +"]", "SL");
				if( "KD".equals(jrInCrn.getFieldString("YD_STK_COL_GP").substring(2,4)) ||
					"FD".equals(jrInCrn.getFieldString("YD_STK_COL_GP").substring(2,4))) {
					
					JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("YD_UP_WO_LOC", jrInCrn.getFieldString("YD_STK_COL_GP"));
					
					/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getLineOffSchPrior
					WITH P_PARAM AS (
					    SELECT :V_YD_UP_WO_LOC AS YD_UP_WO_LOC
					     FROM DUAL
					)
					SELECT X.*
					  FROM (SELECT ROW_NUMBER() OVER (ORDER BY CASE WHEN P.YD_UP_WO_LOC IN ('JAKD05', 'JCFD04') THEN C.YD_STK_BED_NO * 1
					                                            ELSE C.YD_STK_BED_NO * -1 END) AS WORK_SEQ
					             , C.YD_STK_BED_NO
					             , B.STL_NO
					             , A.YD_EQP_ID
					             , A.YD_CRN_SCH_ID
					             , A.YD_SCH_CD
					             , A.YD_SCH_PRIOR
					             , A.YD_WRK_PROG_STAT
					             , A.YD_UP_WO_LOC
					             , A.YD_UP_WO_LAYER
					             , A.YD_DN_WO_LOC
					             , A.YD_DN_WO_LAYER
					          FROM TB_YD_CRNSCH      A
					             , TB_YD_CRNWRKMTL   B
					             , TB_YD_EQPTRACKING C
					             , P_PARAM           P
					         WHERE A.YD_CRN_SCH_ID  = B.YD_CRN_SCH_ID
					           AND B.STL_NO         = C.STL_NO
					           AND A.YD_GP          = 'J'
					           AND YD_WRK_PROG_STAT = 'W' -- 대기중인 크레인스케쥴
					           AND A.DEL_YN         = 'N'
					           AND B.DEL_YN         = 'N'
					           AND 'Y' = CASE WHEN P.YD_UP_WO_LOC = 'JHKD01' AND A.YD_SCH_CD IN ('JHKD01LM','JHTC01MM','JHTC02MM') THEN 'Y' -- SPM1
					                          WHEN P.YD_UP_WO_LOC = 'JEKD02' AND A.YD_SCH_CD IN ('JEKD01LM','JETC01MM','JETC02MM') THEN 'Y' -- SPM2
					                          WHEN P.YD_UP_WO_LOC = 'JCKD03' AND A.YD_SCH_CD IN ('JCKD01LM','JCTC05MM')            THEN 'Y' -- SPM3
					                          WHEN P.YD_UP_WO_LOC = 'JBKD04' AND A.YD_SCH_CD IN ('JBKD01LM','JBTC05MM')            THEN 'Y' -- SPM4
					                          WHEN P.YD_UP_WO_LOC = 'JAKD05' AND A.YD_SCH_CD IN ('JAKD01LM','JATC01MM','JATC02MM') THEN 'Y' -- SPM5
					                          WHEN P.YD_UP_WO_LOC = 'JGFD01' AND A.YD_SCH_CD IN ('JGFD01LM','JGTC01MM','JGTC02MM') THEN 'Y' -- HFL1
					                          WHEN P.YD_UP_WO_LOC = 'JCFD04' AND A.YD_SCH_CD IN ('JCFD01LM','JCTC01MM','JCTC02MM') THEN 'Y' -- HFL4
					                          ELSE 'N'
					                     END
					       ) X
					 WHERE WORK_SEQ     >= 3 -- 입고대기코일 3본 이상
					   AND YD_SCH_PRIOR <> 1
					 ORDER BY WORK_SEQ DESC
					*/
					JDTORecordSet jsLineOff = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getLineOffSchPrior", logId, mthdNm, "제품입고스케쥴 조회");
					
					if( jsLineOff.size() > 0 ) {
						
						for(int x=0; x<jsLineOff.size(); x++) {
							JDTORecord jrLineOff = jsLineOff.getRecord(x);
							
							sLogMsg = (x+1) +"."+jrLineOff.getFieldString("YD_EQP_ID") +"/"+ jrLineOff.getFieldString("YD_CRN_SCH_ID") +"/"+ jrLineOff.getFieldString("YD_SCH_PRIOR");
							
							commUtils.printLog(logId, sLogMsg, "SL");
							
							jrParam.setField("YD_CRN_SCH_ID", jrLineOff.getFieldString("YD_CRN_SCH_ID"));
							jrParam.setField("YD_SCH_PRIOR"	, "1");	// 1순위 UPDATE
							/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdCrnsch03
							UPDATE TB_YD_CRNSCH
							   SET MODIFIER = :V_MODIFIER
							     , MOD_DDTT = SYSDATE
							     , YD_SCH_PRIOR = :V_YD_SCH_PRIOR
							 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
							*/
							intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdCrnsch03", logId, mthdNm, "크레인스케쥴 우선순위 UPDATE");
							
							if (intRtnVal < 1) {
								sLogMsg = "설비LineOff 우선순위 UPDATE 오류 Error YD_CRN_SCH_ID["+ jrLineOff.getFieldString("YD_CRN_SCH_ID") +"]";
								commUtils.printLog(logId, sLogMsg + " ydWbookId: " + ydWbookId, "SL");    			
							}
						}
					}
				}
			} 
			
			commUtils.printLog(logId, mthdNm, "S-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
		jrRtn.setField("RTN_CD" , "1");	
		return jrRtn;
        
    }
     
    
	/**
     * 오퍼레이션명 : 2열연 COIL YARD - TO 위치 결정
     * @param  inRecord, recGetCrnWrkMtl, rsResultCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public JDTORecord crnSchToLoc (String logId, String mthdNms, JDTORecord jrParamSet )throws JDTOException{
    	String mthdNm = "TO 위치 결정[CCoilSchSeEJB.crnSchToLoc] < " + mthdNms;
    	JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();	//전문 Return
    	JDTORecord jrRcvRtn = JDTORecordFactory.getInstance().create();	//전문 Return
    
    	try{
        	commUtils.printLog(logId, mthdNm, "S+");
        	
        	int intRtnVal  = 0;
        	String sLogMsg = "";  

        	//-------------------------------------------------------------------------------------------------------------
        	//	수정시 반드시 crnSchToLocRe 수정해야 함
        	//-------------------------------------------------------------------------------------------------------------
			//파라미터 Null Check
        	String ydWbookId	= commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_ID" ));	
        	String ydEqpId 		= commUtils.trim(jrParamSet.getFieldString("YD_EQP_ID"   ));	
        	String sModifier	= commUtils.trim(jrParamSet.getFieldString("MODIFIER"   ));	
        	
        	/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdWrkbook 
        	SELECT YD_WBOOK_ID      AS YD_WBOOK_ID
        	     , REGISTER         AS REGISTER
        	     , MODIFIER         AS MODIFIER
        	     , YD_GP            AS YD_GP
        	     , YD_BAY_GP        AS YD_BAY_GP
        	     , YD_SCH_CD        AS YD_SCH_CD
        	     , YD_SCH_PRIOR     AS YD_SCH_PRIOR
        	     , YD_SCH_PROG_STAT AS YD_SCH_PROG_STAT
        	     , YD_SCH_ST_GP     AS YD_SCH_ST_GP
        	     , YD_SCH_REQ_GP    AS YD_SCH_REQ_GP
        	     , YD_AIM_YD_GP     AS YD_AIM_YD_GP
        	     , YD_AIM_BAY_GP    AS YD_AIM_BAY_GP
        	     , YD_CTS_RELAY_YN  AS YD_CTS_RELAY_YN
        	     , YD_CTS_RELAY_BAY_GP  AS YD_CTS_RELAY_BAY_GP
        	     , YD_TO_LOC_DCSN_MTD   AS YD_TO_LOC_DCSN_MTD
        	     , YD_TO_LOC_GUIDE  AS YD_TO_LOC_GUIDE
        	     , YD_WRK_PLAN_TCAR AS YD_WRK_PLAN_TCAR
        	     , YD_CAR_USE_GP
        	     , TRN_EQP_CD       AS TRN_EQP_CD
        	     , CAR_NO           AS CAR_NO
        	     , CARD_NO          AS CARD_NO
        	     , (SELECT STL_NO 
        	          FROM TB_YD_WRKBOOKMTL 
        	         WHERE YD_WBOOK_ID = A.YD_WBOOK_ID 
        	           AND DEL_YN = 'N' AND ROWNUM = 1) AS STL_NO
        	     , (SELECT ITEM1 
        	          FROM TB_YD_RULE
        	         WHERE REPR_CD_GP = 'APP010'
        	           AND DEL_YN = 'N') AS SCHLOG_YN
        	  FROM TB_YD_WRKBOOK A
        	 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			 */
        	JDTORecordSet jsWbook = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdWrkbook", logId, mthdNm, "작업예약 조회"); 
	    	
	    	if (jsWbook.size() < 1) {
				
	    		sLogMsg = "오류:작업 예약 조회 시 오류";
				commUtils.printLog(logId, sLogMsg + " ydWbookId: " + ydWbookId, "SL");
				
				jrRtn.setField("RTN_CD" , "0");	
    			jrRtn.setField("RTN_MSG", sLogMsg);
    			return jrRtn;
			}			
			
	    	jsWbook.absolute(1);
			JDTORecord jrWbook = JDTORecordFactory.getInstance().create();
			jrWbook.setRecord(jsWbook.getRecord());
			jrWbook.setField("MODIFIER", sModifier);
			
			String ydSchCd 	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));
			String sStlNo 	= commUtils.trim(jrWbook.getFieldString("STL_NO"));   // 대표 재료만 스크랩여부 판단
			
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄 조회
			//-------------------------------------------------------------------------------------------------------------
			JDTORecordSet jsCrnSch = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord jrInPara = commUtils.getParam(logId, mthdNm, sModifier);
			jrInPara.setField("YD_WBOOK_ID"	, ydWbookId);
			jrInPara.setField("YD_EQP_ID"	, ydEqpId);

			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnSchByWBookId 
			SELECT A.YD_EQP_ID               AS YD_EQP_ID                       
			     , A.YD_EQP_NAME             AS YD_EQP_NAME                     
			     , B.YD_CRN_SCH_ID           AS YD_CRN_SCH_ID                   
			     , B.REGISTER                AS REGISTER                        
			     , TO_CHAR(B.REG_DDTT       , 'YYYYMMDDHH24MISS') AS REG_DDTT 
			     , B.MODIFIER                AS MODIFIER                        
			     , TO_CHAR(B.MOD_DDTT       , 'YYYYMMDDHH24MISS') AS MOD_DDTT 
			     , B.DEL_YN                  AS DEL_YN                          
			     , B.YD_WBOOK_ID             AS YD_WBOOK_ID                     
			     , B.YD_GP                   AS YD_GP                           
			     , B.YD_BAY_GP               AS YD_BAY_GP                       
			     , B.YD_SCH_CD               AS YD_SCH_CD                       
			     , B.YD_SCH_ST_GP            AS YD_SCH_ST_GP                    
			     , B.YD_SCH_REQ_GP           AS YD_SCH_REQ_GP                   
			     , B.YD_SCH_PRIOR            AS YD_SCH_PRIOR                    
			     , B.YD_EQP_WRK_STAT         AS YD_EQP_WRK_STAT                 
			     , B.YD_WRK_PROG_STAT        AS YD_WRK_PROG_STAT                
			     , TO_CHAR(B.YD_WBOOK_DT    , 'YYYYMMDDHH24MISS') AS YD_WBOOK_DT
			     , TO_CHAR(B.YD_SCH_DT      , 'YYYYMMDDHH24MISS') AS YD_SCH_DT
			     , TO_CHAR(B.YD_WORD_DT     , 'YYYYMMDDHH24MISS') AS YD_WORD_DT
			     , TO_CHAR(B.YD_UP_CMPL_DT  , 'YYYYMMDDHH24MISS') AS YD_UP_CMPL_DT
			     , TO_CHAR(B.YD_DN_CMPL_DT  , 'YYYYMMDDHH24MISS') AS YD_DN_CMPL_DT
			     , B.YD_WRK_HDS_DD           AS YD_WRK_HDS_DD                   
			     , B.YD_WRK_DUTY             AS YD_WRK_DUTY                     
			     , B.YD_WRK_PARTY            AS YD_WRK_PARTY                    
			     , B.YD_MAIN_WRK_MTL_SH      AS YD_MAIN_WRK_MTL_SH              
			     , B.YD_AID_WRK_MTL_SH       AS YD_AID_WRK_MTL_SH               
			     , B.YD_AID_WRK_UPDN_GP      AS YD_AID_WRK_UPDN_GP              
			     , B.YD_TO_LOC_DCSN_MTD      AS YD_TO_LOC_DCSN_MTD              
			     , B.YD_TO_LOC_GUIDE         AS YD_TO_LOC_GUIDE                 
			     , B.YD_EQP_WRK_SH           AS YD_EQP_WRK_SH                   
			     , B.YD_EQP_WRK_WT           AS YD_EQP_WRK_WT                   
			     , B.YD_EQP_WRK_T            AS YD_EQP_WRK_T                    
			     , B.YD_EQP_WRK_MAX_W        AS YD_EQP_WRK_MAX_W                
			     , B.YD_EQP_WRK_MAX_L        AS YD_EQP_WRK_MAX_L                
			     , B.YD_CRN_SB_CTL_H         AS YD_CRN_SB_CTL_H                 
			     , B.YD_CRN_GRAB_USE_RULE_ID AS YD_CRN_GRAB_USE_RULE_ID         
			     , B.YD_UP_WO_LOC            AS YD_UP_WO_LOC                    
			     , B.YD_UP_WO_LAYER          AS YD_UP_WO_LAYER                  
			     , B.YD_UP_WO_LOC_XAXIS      AS YD_UP_WO_LOC_XAXIS              
			     , B.YD_UP_WO_XAXIS_GAP_MAX  AS YD_UP_WO_XAXIS_GAP_MAX          
			     , B.YD_UP_WO_XAXIS_GAP_MIN  AS YD_UP_WO_XAXIS_GAP_MIN          
			     , B.YD_UP_WO_LOC_YAXIS      AS YD_UP_WO_LOC_YAXIS              
			     , B.YD_UP_WO_LOC_YAXIS1     AS YD_UP_WO_LOC_YAXIS1             
			     , B.YD_UP_WO_LOC_YAXIS2     AS YD_UP_WO_LOC_YAXIS2             
			     , B.YD_UP_WO_YAXIS_GAP_MAX  AS YD_UP_WO_YAXIS_GAP_MAX          
			     , B.YD_UP_WO_YAXIS_GAP_MIN  AS YD_UP_WO_YAXIS_GAP_MIN          
			     , B.YD_UP_WO_LOC_ZAXIS      AS YD_UP_WO_LOC_ZAXIS              
			     , B.YD_UP_WO_ZAXIS_GAP_MAX  AS YD_UP_WO_ZAXIS_GAP_MAX          
			     , B.YD_UP_WO_ZAXIS_GAP_MIN  AS YD_UP_WO_ZAXIS_GAP_MIN          
			     , B.YD_DN_WO_LOC            AS YD_DN_WO_LOC                    
			     , B.YD_DN_WO_LAYER          AS YD_DN_WO_LAYER                  
			     , B.YD_DN_WO_LOC_XAXIS      AS YD_DN_WO_LOC_XAXIS              
			     , B.YD_DN_WO_XAXIS_GAP_MAX  AS YD_DN_WO_XAXIS_GAP_MAX          
			     , B.YD_DN_WO_XAXIS_GAP_MIN  AS YD_DN_WO_XAXIS_GAP_MIN          
			     , B.YD_DN_WO_LOC_YAXIS      AS YD_DN_WO_LOC_YAXIS              
			     , B.YD_DN_WO_LOC_YAXIS1     AS YD_DN_WO_LOC_YAXIS1             
			     , B.YD_DN_WO_LOC_YAXIS2     AS YD_DN_WO_LOC_YAXIS2             
			     , B.YD_DN_WO_YAXIS_GAP_MAX  AS YD_DN_WO_YAXIS_GAP_MAX          
			     , B.YD_DN_WO_YAXIS_GAP_MIN  AS YD_DN_WO_YAXIS_GAP_MIN          
			     , B.YD_DN_WO_LOC_ZAXIS      AS YD_DN_WO_LOC_ZAXIS              
			     , B.YD_DN_WO_ZAXIS_GAP_MAX  AS YD_DN_WO_ZAXIS_GAP_MAX          
			     , B.YD_DN_WO_ZAXIS_GAP_MIN  AS YD_DN_WO_ZAXIS_GAP_MIN          
			     , B.YD_UP_WR_LOC            AS YD_UP_WR_LOC                    
			     , B.YD_UP_WR_LAYER          AS YD_UP_WR_LAYER                  
			     , B.YD_UP_WRK_ACT_GP        AS YD_UP_WRK_ACT_GP                
			     , B.YD_UP_WR_XAXIS          AS YD_UP_WR_XAXIS                  
			     , B.YD_UP_WR_YAXIS          AS YD_UP_WR_YAXIS                  
			     , B.YD_UP_WR_YAXIS1         AS YD_UP_WR_YAXIS1                 
			     , B.YD_UP_WR_YAXIS2         AS YD_UP_WR_YAXIS2                 
			     , B.YD_UP_WR_ZAXIS          AS YD_UP_WR_ZAXIS                  
			     , B.YD_DN_WR_LOC            AS YD_DN_WR_LOC                    
			     , B.YD_DN_WR_LAYER          AS YD_DN_WR_LAYER                  
			     , B.YD_DN_WRK_ACT_GP        AS YD_DN_WRK_ACT_GP                
			     , B.YD_DN_WR_XAXIS          AS YD_DN_WR_XAXIS                  
			     , B.YD_DN_WR_YAXIS          AS YD_DN_WR_YAXIS                  
			     , B.YD_DN_WR_YAXIS1         AS YD_DN_WR_YAXIS1                 
			     , B.YD_DN_WR_YAXIS2         AS YD_DN_WR_YAXIS2                 
			     , B.YD_DN_WR_ZAXIS          AS YD_DN_WR_ZAXIS   
			     , C.YD_AID_WRK_YN           AS YD_AID_WRK_YN   
			     , C.HCR_GP                  AS HCR_GP                 
			     , C.STL_PROG_CD             AS STL_PROG_CD       
			     , D.HR_PLNT_GP 		    
			     , D.ORD_NO 			-- 제작번호,
			     , D.ORD_DTL 		    -- 제작행번,
			     , D.COIL_T 			-- 코일두께,
			     , D.COIL_W 			-- 코일폭,
			     , D.CURR_COIL_LEN	    -- 코일길이,
			     , D.COIL_INDIA 		-- 코일내경,
			     , D.COIL_OUTDIA 	    -- 코일외경,
			     , DECODE(D.COIL_WT, 0, D.NET_CAL_WT, D.COIL_WT) 		--AS 코일중량,
			     , D.NEXT_PROC 		    -- 차공정,
			     , D.PLAN_PROC1         -- 계획공정,
			     , D.BRANCH_CD 		    -- 분기위치코드,
			     , D.EXTEND_CONVEYOR_BRANCH_CD -- 확장분기위치코드,
			     , D.HYSCO_TRANS_GP 	-- HYSCO이송수단,
			     , D.COOL_METHOD 	    -- 냉각방법,
			     , DECODE(D.CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',D.CURR_PROG_CD) AS CURR_PROG_CD
			     , D.RETURN_GP
			--     , (CASE WHEN EXISTS(SELECT 1 
			--                           FROM (SELECT STACK_RULE_NAME
			--    			                   FROM USRYDA.TB_YD_STKBED
			--    			                  WHERE YD_GP = '3' 
			--                                    AND STACK_COL_USAGE_CD='X' 
			--                                    AND BAY_GP ='A'
			--    			                    AND STACK_RULE_CD LIKE 'SPE%'
			--    			                    AND STACK_RULE_USE_YN='Y') 
			--                                T 
			--                          WHERE T.STACK_RULE_NAME = D.HR_SPEC_ABBSYM) 
			--             THEN 'Y' ELSE 'N' END ) AS JJANGGU_CHK  
			     , C.STL_NO       
			     , SUM(D.COIL_WT)   OVER (ORDER BY C.YD_STK_LYR_NO DESC) AS SUM_MTL_WT      
				 , SUM(D.COIL_T)    OVER (ORDER BY C.YD_STK_LYR_NO DESC) AS SUM_MTL_T   
				 , MAX(D.COIL_W)    OVER (ORDER BY C.YD_STK_LYR_NO DESC) AS MAX_MTL_W 
				 , MAX(D.COIL_LEN)  OVER (ORDER BY C.YD_STK_LYR_NO DESC) AS MAX_MTL_L 
				 , COUNT(D.COIL_NO) OVER (ORDER BY C.YD_STK_LYR_NO DESC) AS SH_CNT 
			  FROM TB_YD_EQP  A                                               
			     , TB_YD_CRNSCH B                                               
			     , TB_YD_CRNWRKMTL C                                               
			     , USRPTA.TB_PT_COILCOMM  D  
			 WHERE B.YD_EQP_ID      = A.YD_EQP_ID  
			   AND B.YD_CRN_SCH_ID  = C.YD_CRN_SCH_ID  
			   AND C.STL_NO         = D.COIL_NO
			   AND B.YD_WBOOK_ID    = :V_YD_WBOOK_ID
			   AND B.YD_EQP_ID      = :V_YD_EQP_ID                         
			   AND B.DEL_YN = 'N'
			   AND C.DEL_YN = 'N'
			 ORDER BY B.YD_CRN_SCH_ID
			 */
			
			jsCrnSch = commDao.select(jrInPara, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnSchByWBookId", logId, mthdNm, "크레인스케줄 조회"); 

			
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄의 권하지시위치 결정
			//-------------------------------------------------------------------------------------------------------------
			JDTORecord jrParam  	= commUtils.getParam(logId, mthdNm, sModifier);
			JDTORecord jrCrnSch 	= JDTORecordFactory.getInstance().create();

	    	String ydWrkPlanTcar	= "";
   			String ydStkColGp 		= "";	
			String ydStkBedNo		= "";	
			String ydStkLyrNo		= "";	
    		String ydCrnSchId 		= "";
    		String sCrnToLocDcsnMtd = "";
    		String sToLocGuide 		= "";
    		String rtnCd 	        = "";
    		
    		String sTcYdEqpId       = "";
    		String sTcToBay         = "";
    		String sTcMoveYn        = "N";
    		String sToLocGuideCol   = "";

			for (int Loop_i = 1; Loop_i <= jsCrnSch.size(); Loop_i++) {
		    	
				jsCrnSch.absolute(Loop_i);
        		jrCrnSch = jsCrnSch.getRecord();
        		jrCrnSch.setField("MODIFIER", sModifier);
		    	
        		//크레인스케줄Data저장
        		ydCrnSchId 		 = commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));
        		sCrnToLocDcsnMtd = commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"));
        		sToLocGuide 	 = commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_GUIDE"));
        		sStlNo			 = commUtils.trim(jrCrnSch.getFieldString("STL_NO"));
        		
        		if(sToLocGuide.length() >= 6) {
        			sToLocGuideCol = sToLocGuide.substring(0, 6);
        		} else {
        			sToLocGuideCol ="XXXXXX";
        		}
        		
        		if ("KD05LH".equals(ydSchCd.substring(2,8)) || "SC01MH".equals(ydSchCd.substring(2,8))) {       				
            		/**********************************************************
    				* Scrap 인 경우
    				**********************************************************/            		
       				sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은  Scrap To위치 결정 시작";
        			commUtils.printLog(logId, sLogMsg, "SL");
        			
        			jrRcvRtn = this.toLocScrap(logId, mthdNm, jrWbook, jrCrnSch);
        			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
        			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));

        		} else if ("W".equals(sCrnToLocDcsnMtd)) {
            		/**********************************************************
    				* 보조작업인 경우 TO위치 결정 (일반적치대로....)
    				**********************************************************/            		
            		sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 보조작업 스케줄의  To위치 결정 시작";
        			commUtils.printLog(logId, sLogMsg, "SL");
        			
    					jrRcvRtn = this.toLocDummy(logId, mthdNm, jrWbook, jrCrnSch);    				
        			
        			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
        			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));

            	} else if  ("GF01UH".equals(ydSchCd.substring(2,8)) ||"GF01LH".equals(ydSchCd.substring(2,8))) {       			
            		/**********************************************************
    				* 지포장장보급
    				* -- 지포장장은 설비와 동일 하게 검색
    				**********************************************************/            		
            		sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 지포장장보급 스케줄의  To위치 결정 시작";
        			commUtils.printLog(logId, sLogMsg, "SL");
        			
        			jrRcvRtn = this.toLocGF(logId, mthdNm, jrWbook, jrCrnSch);
        			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
        			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));

            	} else if ((("S".equals(sCrnToLocDcsnMtd)) && ( "KE".equals(ydSchCd.substring(2,4)) || "KD".equals(ydSchCd.substring(2,4))) && ("U".equals(ydSchCd.substring(6,7))) ) 
       			             ||  
       			          ( ("S".equals(sCrnToLocDcsnMtd)) && ( "FE".equals(ydSchCd.substring(2,4)) || "FD".equals(ydSchCd.substring(2,4))) && ("U".equals(ydSchCd.substring(6,7))) 
       			    		&& (("C".equals(ydSchCd.substring(1,2)))||("G".equals(ydSchCd.substring(1,2)))))
       			    ) {
 	           		/**********************************************************
 	   				* 주작업 HFL 설비/SPM설비보급 (결속장 제외)
 	   				**********************************************************/
             		sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 주작업 HFL 설비/SPM설비보급 (결속장 제외) 위 스케줄의  To위치 결정 시작";
 	       			commUtils.printLog(logId, sLogMsg, "SL");
 	       			
 	       			jrRcvRtn = this.toLocUpConveyor(logId, mthdNm, jrWbook, jrCrnSch);
 	    			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
 	    			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));

 
            	} else if ((("S".equals(sCrnToLocDcsnMtd)) && ("FE".equals(ydSchCd.substring(2,4))) && ("U".equals(ydSchCd.substring(6,7))) 
      			    		&& ("B".equals(ydSchCd.substring(1,2)) || "D".equals(ydSchCd.substring(1,2)) || "F".equals(ydSchCd.substring(1,2))))
      			     ) {
	           		/**********************************************************
	   				* 주작업 HFL 설비보급 (결속장)
	   				**********************************************************/
            		sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 주작업 HFL 설비보급 (결속장 ) 위 스케줄의  To위치 결정 시작";
	       			commUtils.printLog(logId, sLogMsg, "SL");
	       			jrParam.setField("TO_LOC_GUIDE", 	sToLocGuide);
	       			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkLayByHFL
	       			SELECT * 
	       			  FROM
	       			        (
	       			        SELECT YD_STK_COL_GP
	       			             , YD_STK_BED_NO
	       			             , YD_STK_LYR_NO
	       			             , CHK
	       			             , CASE WHEN CHK <YD_STK_BED_NO AND YD_STK_COL_GP IN('JFFE02','JDFE03','JBFE05') THEN YD_STK_BED_NO-CHK 
	       			                    WHEN CHK>=YD_STK_BED_NO AND YD_STK_COL_GP IN('JFFE02','JDFE03','JBFE05') THEN CHK+YD_STK_BED_NO
	       			                    ELSE TO_NUMBER(YD_STK_BED_NO) END  AS CHK2
	       			         FROM (
	       			              SELECT A.YD_STK_COL_GP
	       			                   , A.YD_STK_BED_NO
	       			                   , A.YD_STK_LYR_NO
	       			                   , (SELECT MAX(YD_STK_BED_NO) 
	       			                        FROM TB_YD_STKLYR 
	       			                       WHERE YD_STK_COL_GP IN('JFFE02','JDFE03','JBFE05')  
	       			                         AND YD_STK_LYR_MTL_STAT <> 'E') AS CHK
	       			                FROM TB_YD_STKLYR A
	       			               WHERE A.YD_STK_COL_GP = :V_TO_LOC_GUIDE
	       			                 AND A.DEL_YN = 'N'
	       			                 AND A.YD_STK_LYR_ACT_STAT = 'E'
	       			                 AND A.YD_STK_LYR_MTL_STAT = 'E'
	       			                 AND A.STL_NO IS NULL
	       			                 AND A.YD_STK_BED_NO <> '00'
	       			             ) X
	       			         ORDER BY CHK2 ,YD_STK_LYR_NO ,YD_STK_BED_NO
	       			       ) 
	       			 WHERE ROWNUM = 1 
	       			*/
	       			JDTORecordSet jsHflInfo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkLayByHFL", logId, mthdNm, "결속장 조회");
	       			if (jsHflInfo.size() <= 0 ) {
	        			sLogMsg = " :결속장 조회 실패!";
	    				commUtils.printLog(logId, sLogMsg, "SL");
	       			} else {	
		       			jsHflInfo.first();
						JDTORecord jrHflInfo = jsHflInfo.getRecord();
		       			
						ydStkColGp 	= commUtils.trim(jrHflInfo.getFieldString("YD_STK_COL_GP"));//적치열
						ydStkBedNo	= commUtils.trim(jrHflInfo.getFieldString("YD_STK_BED_NO"));//적치베드
						ydStkLyrNo  = commUtils.trim(jrHflInfo.getFieldString("YD_STK_LYR_NO"));//적치단
						
						jrWbook.setField("YD_TO_LOC_GUIDE", ydStkColGp + ydStkBedNo + ydStkLyrNo);
	
						sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 결속장 [TO위치 가이드 결정  YD_TO_LOC_GUIDE : "+ydStkColGp + ydStkBedNo + ydStkLyrNo+"]";
	    				commUtils.printLog(logId, sLogMsg, "SL");
	    				
	    				jrRcvRtn = this.toLocUser(logId, mthdNm, jrWbook, jrCrnSch);
	    				jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
	    				jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));	       			
	       			}	
            	} else if (  "S".equals(sCrnToLocDcsnMtd) &&("KD02LH".equals(ydSchCd.substring(2,8)))) {    
	           		/**********************************************************
	   				* 주작업 SPM 재작업 보급   
	   				**********************************************************/
	           		sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 설비위 재작업 보급 스케줄의  To위치 결정 시작";
	       			commUtils.printLog(logId, sLogMsg, "SL");
	       			
	       			jrRcvRtn = this.toLocUpConveyor(logId, mthdNm, jrWbook, jrCrnSch);
	    			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
	    			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
	       			
            	} else if (( "PT".equals(ydSchCd.substring(2,4)) || "TR".equals(ydSchCd.substring(2,4))) 
            			  && ("U".equals(ydSchCd.substring(6,7)))) {  // 출하
	           		/**********************************************************
	   				* 차량상차   
	   				**********************************************************/            		
            		
            		/**********************************************************************
    				* 차량출고 : 차량이 정지한 적치열 조회 ==> TO위치가 됨  ==> TO 위치 가이드에 등록 처리
    				***********************************************************************/            		
        			String rtnMsg = "";
        			
        			String ydCarUseGp   = commUtils.trim(jrWbook.getFieldString("YD_CAR_USE_GP"));	//차량사용구분
    				String sTrnEqpCd	= commUtils.trim(jrWbook.getFieldString("TRN_EQP_CD"));	//운송장비코드
    				String sCarNo		= commUtils.trim(jrWbook.getFieldString("CAR_NO"));		//차량번호
    				String sCardNo		= commUtils.trim(jrWbook.getFieldString("CARD_NO"));	//차량번호
    				
    				sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 차량상차작업예약["+ydWbookId+"]의 차량정보[차량사용구분:"+ydCarUseGp+", 운송장비코드:"+sTrnEqpCd+", 차량번호:"+sCarNo+"]에 대한 적치베드 조회 시작";
    				commUtils.printLog(logId, sLogMsg, "SL");
				
    				JDTORecordSet jsCar	= JDTORecordFactory.getInstance().createRecordSet("");
    				
    				commUtils.printLog(logId,  "차량구분 ydCarUseGp: " + ydCarUseGp , "SL");
    				if ( "L".equals(ydCarUseGp) ) {				//구내운송
    					commUtils.printLog(logId,  " TOSQL"+ydCrnSchId+ " 권상재료["+sStlNo +"] 의 적치가능한 베드 조회 시작", "SL");
 					
    					jrParam.setField("YD_CAR_USE_GP", 	ydCarUseGp);
    					jrParam.setField("TRN_EQP_CD", 		sTrnEqpCd);
    					/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkBedByCarUseGpandTrnEqpCd
    					SELECT YD_STK_COL_GP
    					     , YD_STK_BED_NO 
    					     , YD_STK_LYR_NO 
    					     , YD_MTL_SH 
    					  FROM
    					       (
    					        SELECT SB.YD_STK_COL_GP
    					             , SB.YD_STK_BED_NO 
    					             , SL.YD_STK_LYR_NO 
    					             , COUNT(SL.STL_NO) AS YD_MTL_SH
    					          FROM TB_YD_STKBED SB
    					             , TB_YD_STKLYR SL
    					             , (SELECT YD_STK_COL_GP                         
    					                  FROM TB_YD_CARPOINT                          
    					                 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
    					                   AND DEL_YN = 'N' ) SC
    					         WHERE SB.YD_STK_COL_GP = SL.YD_STK_COL_GP
    					           AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO
    					           AND SB.YD_STK_COL_GP = SC.YD_STK_COL_GP
    					           AND SB.DEL_YN        = 'N'
    					           AND SL.YD_STK_LYR_ACT_STAT = 'L' --적치가능
    					           AND SL.YD_STK_LYR_NO ='001' --차량위치는 1단만 적치
    					           AND SB.YD_STK_COL_GP||SB.YD_STK_BED_NO||SL.YD_STK_LYR_NO NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YD_CRNSCH WHERE DEL_YN = 'N')
    					           AND SL.STL_NO IS NULL
    					         GROUP BY SB.YD_STK_COL_GP, SB.YD_STK_BED_NO, SL.YD_STK_LYR_NO 
    					         ORDER BY SB.YD_STK_COL_GP, SB.YD_STK_BED_NO, SL.YD_STK_LYR_NO 
    					         )
    					 WHERE YD_MTL_SH = 0
    					   AND ROWNUM = 1
	    				*/   
    					jsCar = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkBedByCarUseGpandTrnEqpCd", logId, mthdNm, "구내운송차량 차량BED 조회"); 
	            		
	            		if (jsCar.size() <= 0) {
	            			sLogMsg = " : 구내운송차량 READ 실패!";
	        				commUtils.printLog(logId, sLogMsg, "SL");
	            		} else {
	            			rtnMsg = CConstant.RETN_CD_SUCCESS;
	            		}	
    				} else if ( "G".equals(ydCarUseGp) ) {		//출하차량
    					    // 냉연 이송                                             공냉재이송                         제품이송
    					if ( "PT0".equals(ydSchCd.substring(2,5)) || "PT2".equals(ydSchCd.substring(2,5)) || "PT3".equals(ydSchCd.substring(2,5))) {
    						// 이송상차
	    					commUtils.printLog(logId,  " TOSQL"+ydCrnSchId+ " 권상재료["+ sStlNo +"] 의 적치가능한 베드 조회 시작", "SL");
	    					
	    					jrParam.setField("YD_CAR_USE_GP"	, ydCarUseGp);
	    					jrParam.setField("CAR_NO"			, sCarNo);
	    					jrParam.setField("STL_NO"			, sStlNo);
	    					jrParam.setField("CARD_NO"			, sCardNo);
	    					jrParam.setField("YD_SCH_CD"	    , ydSchCd);
	    					
	    					/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkBedByCarUseGpandCarNoFrto
	    					SELECT YD_STK_COL_GP
	    					     , YD_STK_BED_NO
	    					     , YD_STK_LYR_NO
	    					     , YD_MTL_SH
	    					  FROM
	    					       (
	    					        SELECT SB.YD_STK_COL_GP
	    					             , SB.YD_STK_BED_NO
	    					             , SL.YD_STK_LYR_NO
	    					             , COUNT(SL.STL_NO) AS YD_MTL_SH
	    					          FROM TB_YD_STKBED SB
	    					             , TB_YD_STKLYR SL
	    					             , (SELECT YD_STK_COL_GP AS YD_STK_COL_GP
	    					                  FROM TB_YD_CARPOINT
	    					                 WHERE CAR_NO = :V_CAR_NO ) SC
	    					         WHERE SB.YD_STK_COL_GP = SL.YD_STK_COL_GP
	    					           AND SB.YD_STK_COL_GP = SC.YD_STK_COL_GP
	    					           AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO
	    					           AND SB.YD_STK_BED_NO LIKE NVL((SELECT ITEM
	    					                                           FROM TB_YD_RULE
	    					                                          WHERE REPR_CD_GP = 'APP101'
	    					                                            AND CD_GP =  (SELECT YD_CAR_UPP_LOC_CD FROM TB_YD_STOCK WHERE STL_NO = :V_STL_NO)
	    					                                         ), '%')
	    					           AND SB.DEL_YN       = 'N'
	    					           AND SB.YD_STK_BED_ACT_STAT = 'L' -- 적치가능
	    					           AND SL.YD_STK_LYR_NO       = '001'       -- 차량위치는 1단만 적치
	    					           AND SB.YD_STK_COL_GP||SB.YD_STK_BED_NO||SL.YD_STK_LYR_NO NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YD_CRNSCH WHERE DEL_YN = 'N')
	    					           AND SL.STL_NO IS NULL
	    					           AND SUBSTR(SB.YD_STK_COL_GP, 0, 2) = SUBSTR(:V_YD_SCH_CD, 0, 2)
	    					         GROUP BY SB.YD_STK_COL_GP, SB.YD_STK_BED_NO, SL.YD_STK_LYR_NO
	    					         ORDER BY SB.YD_STK_COL_GP, SB.YD_STK_BED_NO, SL.YD_STK_LYR_NO
	    					        )
	    					 WHERE YD_MTL_SH = 0
	    					   AND ROWNUM    = 1
	    					*/
	    					jsCar = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkBedByCarUseGpandCarNoFrto", logId, mthdNm, "냉연이송상차 차량BED 조회"); 
		            		
		            		if (jsCar.size() <= 0) {
		            			sLogMsg = "이송상차 READ 실패!";
		        				commUtils.printLog(logId, sLogMsg, "SL");
		            		} else {
		            			rtnMsg = CConstant.RETN_CD_SUCCESS;
		            		}    						
    						
    					} else {	
	    					
	    					commUtils.printLog(logId,  " TOSQL"+ydCrnSchId+ " 권상재료["+sStlNo +"] 의 적치가능한 베드 조회 시작", "SL");
	    					
	    					jrParam.setField("YD_CAR_USE_GP", ydCarUseGp);
	    					jrParam.setField("CAR_NO"		, sCarNo);
	    					
//PIDEV
//	    					String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
	    					
//	    					if("N".equals(sApplyYnPI)) {
//		    					jrParam.setField("CARD_NO"		, sCardNo);
//	    					} else{
//PIDEV_S :병행가동용:PI_YD
		    					jrParam.setField("PI_YD",    	"J");			    					
//	    					}
	    					jrParam.setField("YD_SCH_CD"	, ydSchCd);
	    					/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkBedByCarUseGpandCarNo 
	    					SELECT YD_STK_COL_GP
	    					     , YD_STK_BED_NO 
	    					     , YD_STK_LYR_NO 
	    					     , YD_MTL_SH 
	    					  FROM
	    					(
	    					SELECT SB.YD_STK_COL_GP
	    					     , SB.YD_STK_BED_NO 
	    					     , SL.YD_STK_LYR_NO 
	    					     , COUNT(SL.STL_NO) AS YD_MTL_SH
	    					  FROM TB_YD_STKBED SB
	    					     , TB_YD_STKLYR SL
	    					     , (SELECT YD_STK_COL_GP                    
	    					          FROM TB_YD_CARPOINT                          
	    					         WHERE CARD_NO = :V_CARD_NO
	    					           AND DEL_YN = 'N' ) SC
	    					 WHERE SB.YD_STK_COL_GP = SL.YD_STK_COL_GP
	    					   AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO
	    					   AND SB.YD_STK_COL_GP = SC.YD_STK_COL_GP
	    					   AND SB.DEL_YN        = 'N'
	    					   AND SB.YD_STK_BED_ACT_STAT = 'L' --적치가능
	    					   AND SL.YD_STK_LYR_NO = '001' --차량위치는 1단만 적치
	    					   AND SB.YD_STK_COL_GP||SB.YD_STK_BED_NO||SL.YD_STK_LYR_NO NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YD_CRNSCH WHERE DEL_YN = 'N')
	    					   --복수동 적용
	    					   AND SB.YD_STK_BED_NO||SL.YD_STK_LYR_NO NOT IN ( SELECT B.YD_STK_BED_NO||B.YD_STK_LYR_NO
	    					                                                     FROM TB_YD_CARSCH A
	    					                                                        , TB_YD_CARFTMVMTL B 
	    					                                                    WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
	    					                                                      AND A.CAR_NO = :V_CAR_NO
	    					                                                      AND A.CARD_NO= :V_CARD_NO
	    					                                                      AND A.DEL_YN = 'N'
	    					                                                      AND B.DEL_YN = 'N'
	    					                                                      AND A.CAR_NO NOT IN ('9999','9998','9997','9996','9995')
	    					                                                 )
	    					   AND SL.STL_NO IS NULL
	    					   AND SUBSTR(SB.YD_STK_COL_GP, 1, 2) = SUBSTR(:V_YD_SCH_CD, 1, 2)
	    					 GROUP BY SB.YD_STK_COL_GP, SB.YD_STK_BED_NO, SL.YD_STK_LYR_NO 
	    					 ORDER BY SB.YD_STK_COL_GP, SB.YD_STK_BED_NO, SL.YD_STK_LYR_NO 
	    					)
	    					 WHERE YD_MTL_SH = 0
	    					   AND ROWNUM = 1
		    				   */
		    				                                
	    					jsCar = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkBedByCarUseGpandCarNo_PIDEV", logId, mthdNm, "출하차량 BED 조회"); 
		            		
		            		if (jsCar.size() <= 0) {
		            			sLogMsg = "출하차량 READ 실패!";
		        				commUtils.printLog(logId, sLogMsg, "SL");
		            		} else {
		            			rtnMsg = CConstant.RETN_CD_SUCCESS;
		            		}
    					}	
    				}
    				
    				if ( rtnMsg.equals(CConstant.RETN_CD_SUCCESS) ) {
    					jsCar.first();
    					JDTORecord jrCar = jsCar.getRecord();
    					
    					ydStkColGp 	= commUtils.trim(jrCar.getFieldString("YD_STK_COL_GP"));//차량정지위치 적치열
    					ydStkBedNo	= commUtils.trim(jrCar.getFieldString("YD_STK_BED_NO"));//차량정지위치 적치베드
    					ydStkLyrNo  = commUtils.trim(jrCar.getFieldString("YD_STK_LYR_NO"));//차량정지위치 적치단
    					
    					jrWbook.setField("YD_TO_LOC_GUIDE", ydStkColGp + ydStkBedNo + ydStkLyrNo);

    					sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 차량출고 [TO위치 가이드 결정  YD_TO_LOC_GUIDE : "+ydStkColGp + ydStkBedNo + ydStkLyrNo+"]";
        				commUtils.printLog(logId, sLogMsg, "SL");
        				
        				jrRcvRtn = this.toLocUser(logId, mthdNm, jrWbook, jrCrnSch);
        				jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
        				jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
    				}	
		    	} else if (( "TC".equals(ydSchCd.substring(2,4)) && ("U".equals(ydSchCd.substring(6,7)) || "MM".equals(ydSchCd.substring(6,8))))
		    			// 동내이적인데 대차로 올라가능 경우 
		    			|| ( "YD".equals(ydSchCd.substring(2,4)) && "TC".equals(sToLocGuideCol.substring(2,4)) && "M".equals(ydSchCd.substring(6,7)))
		    			  )	{
		    		/*****************************************************************************
		    		 * 명령선택(Y5YDL007) 기동시에 권하번지 확정 처리
		    		 *  1.대차상차 작업
		    		 *  2.제품 동간입고 
    				******************************************************************************/
		    		
		    		sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 대차상차 주작업 To위치 결정 시작";
        			commUtils.printLog(logId, sLogMsg, "SL");

					jrRcvRtn = this.toLocUpTCar(logId, mthdNm, jrWbook, jrCrnSch);
						
      			} else if ((sToLocGuide.length() == 4)  && ("HC".equals(ydSchCd.substring(2,4)))) {
            		/**********************************************************
    				* 사용자 지정 : 소재 결로재 보급
    				**********************************************************/            		
      				sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자지정 소재 결로재 보급의  To위치 결정 시작";
        			commUtils.printLog(logId, sLogMsg, "SL");
        			 
    					jrRcvRtn = this.toLocUserHC(logId, mthdNm, jrWbook, jrCrnSch);
    				
        			
        			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
        			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
        			
   					sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자지정 소재 결로재 보급의 To위치 결정 완료 ";
        			commUtils.printLog(logId, sLogMsg, "SL");       			
        			
      			} else if (sToLocGuide.length() >= 4) {
      			// 20.02.12 강정선 : #2, #3, #5 HFL TO위치가이드 편집
//      				if( "JBFE01UH".equals(ydSchCd) ||
//      					"JDFE01UH".equals(ydSchCd) ||
//      					"JFFE01UH".equals(ydSchCd) ) {
//       					sLogMsg =  "스케줄코드["+ ydSchCd +"] #2,3,5 HFL TO위치가이드 수정["+ sToLocGuide.substring(0,6) +"]";
//            			commUtils.printLog(logId, sLogMsg, "SL"); 
//            			
//      					jrWbook.setField("YD_TO_LOC_GUIDE", sToLocGuide.substring(0,6));
//      				}
            		/**********************************************************
    				* 사용자 지정 : 일반야드 검색
    				**********************************************************/
      				sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자지정 스케줄의  To위치 결정 시작";
        			commUtils.printLog(logId, sLogMsg, "SL");
        			
        			jrRcvRtn = this.toLocUser(logId, mthdNm, jrWbook, jrCrnSch);
        			rtnCd =  commUtils.trim(jrRcvRtn.getFieldString("RTN_CD"));
        			
        			jrRtn.setField("RTN_CD" , rtnCd);	
        			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
        			
        			// 동내이적이고 TO위치 가이드가 8자리 미만인 경우만
        			if (("0".equals(rtnCd)) && ("YD".equals(ydSchCd.substring(2,4))) && (sToLocGuide.length() < 8) ) {
        				sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 TO위치결정-사용자 지정에서 야드로 TO위치결정 시작";
        				commUtils.printLog(logId, sLogMsg, "SL");
        				
        				/*
        				 *  동내이적시 해당열 XX면 해당 스판에서 검색
        				 *  To위치 업을시 해당 앞뒤 스판에서 재검색
        				 */
        				jrWbook.setField("YD_TO_LOC_GUIDE", sToLocGuide.substring(0, 4)); //해당 스판 검색
        				commUtils.printLog(logId, "sToLocGuide : " + sToLocGuide + " >> " + sToLocGuide.substring(0, 4), "SL");
        				
//        				int sFrontSpan = Integer.parseInt(sToLocGuide.substring(2, 4)) - 1;
//        				String sFSpan = sToLocGuide.substring(0, 2) + StringUtils.leftPad(sFrontSpan + "", 2, "0");
//        				int sRearSpan  = Integer.parseInt(sToLocGuide.substring(2, 4)) + 1;
//    					String sRSpan = sToLocGuide.substring(0, 2) + StringUtils.leftPad(sRearSpan  + "", 2, "0");
    					
//    					commUtils.printLog(logId, "★sFrontSpan : " + sFrontSpan, "SL");
//    					commUtils.printLog(logId, "★sFSpan : " + sFSpan, "SL");
//    					commUtils.printLog(logId, "★sRearSpan : " + sRearSpan, "SL");
//    					commUtils.printLog(logId, "★sRSpan : " + sRSpan, "SL");
    					
    					
        				jrRcvRtn = this.toLocUser(logId, mthdNm, jrWbook, jrCrnSch);
        				rtnCd =  commUtils.trim(jrRcvRtn.getFieldString("RTN_CD"));
        				
        				if ("0".equals(rtnCd)) {
        					// 해당스판 뒤 스판 검색
        					int sFrontSpan = Integer.parseInt(sToLocGuide.substring(2, 4)) - 1;
        					
        					String sFSpan = sToLocGuide.substring(0, 2) + StringUtils.leftPad(sFrontSpan + "", 2, "0");
        					
        					jrWbook.setField("YD_TO_LOC_GUIDE", sFSpan); //해당 스판 검색
        					commUtils.printLog(logId, "sToLocGuide : " + sToLocGuide + " >> " + sFSpan, "SL");
            				jrRcvRtn = this.toLocUser(logId, mthdNm, jrWbook, jrCrnSch);
            				rtnCd =  commUtils.trim(jrRcvRtn.getFieldString("RTN_CD"));
        				}
        				
        				if ("0".equals(rtnCd)) {
        					// 해당스판 뒤 스판 검색
        					int sRearSpan  = Integer.parseInt(sToLocGuide.substring(2, 4)) + 1;
        					
        					String sRSpan = sToLocGuide.substring(0, 2) + StringUtils.leftPad(sRearSpan  + "", 2, "0");
        					
        					jrWbook.setField("YD_TO_LOC_GUIDE", sRSpan); //해당 스판 검색
        					commUtils.printLog(logId, "sToLocGuide : " + sToLocGuide + " >> " + sRSpan, "SL");
            				jrRcvRtn = this.toLocUser(logId, mthdNm, jrWbook, jrCrnSch);
            				rtnCd =  commUtils.trim(jrRcvRtn.getFieldString("RTN_CD"));
        				}
        				jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
        				jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
        				
        				/*
        				jrRcvRtn = this.toLocPrimaryWork(logId, mthdNm, jrWbook, jrCrnSch);
        				jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
        				jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
        				*/
        				sTcMoveYn = commUtils.nvl(jrRcvRtn.getFieldString("TC_MOVE_YN"), "N");
        				
        				sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자 지정에서 주작업 스케줄의 To위치 결정 완료 ";
            			commUtils.printLog(logId, sLogMsg, "SL");
        			} 
        			// 동간이적시(대차하차)에도 To위치 가이드 위치검색 실패시 저장영역별 검색순서로 to위치 결정
        			else if (("0".equals(rtnCd)) && ("TC".equals(ydSchCd.substring(2,4))) && (sToLocGuide.length() < 8) && ("L".equals(ydSchCd.substring(6,7)))) {
        				sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 TO위치결정-사용자 지정에서 야드로 TO위치결정 시작";
        				commUtils.printLog(logId, sLogMsg, "SL");
        				
        				jrRcvRtn = this.toLocPrimaryWork(logId, mthdNm, jrWbook, jrCrnSch);
        				jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
        				jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
        				
        				sTcMoveYn = commUtils.nvl(jrRcvRtn.getFieldString("TC_MOVE_YN"), "N");
        				
        				sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자 지정에서 주작업 스케줄의 To위치 결정 완료 ";
            			commUtils.printLog(logId, sLogMsg, "SL");
        			}
        			// 크래들롤 추출시 HFL 보급위치 실패시 저장영역별 검색순서 TO위치 결정 JFCD01LH
        			else if( "0".equals(rtnCd) && "CD01LH".equals(ydSchCd.substring(2,8)) ) {
        				sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 TO위치결정-사용자 지정에서 야드로 TO위치결정 시작";
        				commUtils.printLog(logId, sLogMsg, "SL");
        				
        				jrRcvRtn = this.toLocPrimaryWork(logId, mthdNm, jrWbook, jrCrnSch);
        				jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
        				jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
        				
        				sTcMoveYn = commUtils.nvl(jrRcvRtn.getFieldString("TC_MOVE_YN"), "N");
        				
        				sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자 지정에서 주작업 스케줄의 To위치 결정 완료 ";
            			commUtils.printLog(logId, sLogMsg, "SL");
        			}
    			} else {
    				/*****************************************************************************
    				* 일반작업
    				******************************************************************************/

    				sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 TO위치결정-야드로 TO위치결정 시작";
    				commUtils.printLog(logId, sLogMsg, "SL");

    				jrRcvRtn = this.toLocPrimaryWork(logId, mthdNm, jrWbook, jrCrnSch);
    				jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
    				jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
    				
    				sTcMoveYn = commUtils.nvl(jrRcvRtn.getFieldString("TC_MOVE_YN"), "N");
    				
   					sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 주작업 스케줄의 To위치 결정 완료 ";
        			commUtils.printLog(logId, sLogMsg, "SL");
    			}
        	}
        	
        	//-------------------------------------------------------------------------------------------------------------
    		// To위치 결정 실패시 default값으로 xx010101을 설정
        	//-------------------------------------------------------------------------------------------------------------
			//jsCrnSch 	= JDTORecordFactory.getInstance().createRecordSet("");
    		jrInPara 	= commUtils.getParam(logId, mthdNm, sModifier);
    		jrInPara.setField("YD_WBOOK_ID", ydWbookId);
    		jrInPara.setField("YD_EQP_ID",   ydEqpId);
    		/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnschByEqpIdandWBookId
    		SELECT A.YD_EQP_ID               AS YD_EQP_ID
    		      ,A.YD_EQP_NAME             AS YD_EQP_NAME
    		      ,B.YD_CRN_SCH_ID           AS YD_CRN_SCH_ID
    		      ,B.REGISTER                AS REGISTER
    		      ,TO_CHAR(B.REG_DDTT       , 'YYYYMMDDHH24MISS') AS REG_DDTT
    		      ,B.MODIFIER                AS MODIFIER
    		      ,TO_CHAR(B.MOD_DDTT       , 'YYYYMMDDHH24MISS') AS MOD_DDTT
    		      ,B.DEL_YN                  AS DEL_YN
    		      ,B.YD_WBOOK_ID             AS YD_WBOOK_ID
    		      ,B.YD_EQP_ID               AS YD_EQP_ID
    		      ,B.YD_GP                   AS YD_GP
    		      ,B.YD_BAY_GP               AS YD_BAY_GP
    		      ,B.YD_SCH_CD               AS YD_SCH_CD
    		      ,B.YD_SCH_ST_GP            AS YD_SCH_ST_GP
    		      ,B.YD_SCH_REQ_GP           AS YD_SCH_REQ_GP
    		      ,B.YD_SCH_PRIOR            AS YD_SCH_PRIOR
    		      ,B.YD_EQP_WRK_STAT         AS YD_EQP_WRK_STAT
    		      ,B.YD_WRK_PROG_STAT        AS YD_WRK_PROG_STAT
    		      ,TO_CHAR(B.YD_WBOOK_DT    , 'YYYYMMDDHH24MISS') AS YD_WBOOK_DT
    		      ,TO_CHAR(B.YD_SCH_DT      , 'YYYYMMDDHH24MISS') AS YD_SCH_DT
    		      ,TO_CHAR(B.YD_WORD_DT     , 'YYYYMMDDHH24MISS') AS YD_WORD_DT
    		      ,TO_CHAR(B.YD_UP_CMPL_DT  , 'YYYYMMDDHH24MISS') AS YD_UP_CMPL_DT
    		      ,TO_CHAR(B.YD_DN_CMPL_DT  , 'YYYYMMDDHH24MISS') AS YD_DN_CMPL_DT
    		      ,B.YD_WRK_HDS_DD           AS YD_WRK_HDS_DD
    		      ,B.YD_WRK_DUTY             AS YD_WRK_DUTY
    		      ,B.YD_WRK_PARTY            AS YD_WRK_PARTY
    		      ,B.YD_MAIN_WRK_MTL_SH      AS YD_MAIN_WRK_MTL_SH
    		      ,B.YD_AID_WRK_MTL_SH       AS YD_AID_WRK_MTL_SH
    		      ,B.YD_AID_WRK_UPDN_GP      AS YD_AID_WRK_UPDN_GP
    		      ,B.YD_TO_LOC_DCSN_MTD      AS YD_TO_LOC_DCSN_MTD
    		      ,B.YD_TO_LOC_GUIDE         AS YD_TO_LOC_GUIDE
    		      ,B.YD_EQP_WRK_SH           AS YD_EQP_WRK_SH
    		      ,B.YD_EQP_WRK_WT           AS YD_EQP_WRK_WT
    		      ,B.YD_EQP_WRK_T            AS YD_EQP_WRK_T
    		      ,B.YD_EQP_WRK_MAX_W        AS YD_EQP_WRK_MAX_W
    		      ,B.YD_EQP_WRK_MAX_L        AS YD_EQP_WRK_MAX_L
    		      ,B.YD_CRN_SB_CTL_H         AS YD_CRN_SB_CTL_H
    		      ,B.YD_CRN_GRAB_USE_RULE_ID AS YD_CRN_GRAB_USE_RULE_ID
    		      ,B.YD_UP_WO_LOC            AS YD_UP_WO_LOC
    		      ,B.YD_UP_WO_LAYER          AS YD_UP_WO_LAYER
    		      ,B.YD_UP_WO_LOC_XAXIS      AS YD_UP_WO_LOC_XAXIS
    		      ,B.YD_UP_WO_XAXIS_GAP_MAX  AS YD_UP_WO_XAXIS_GAP_MAX
    		      ,B.YD_UP_WO_XAXIS_GAP_MIN  AS YD_UP_WO_XAXIS_GAP_MIN
    		      ,B.YD_UP_WO_LOC_YAXIS      AS YD_UP_WO_LOC_YAXIS
    		      ,B.YD_UP_WO_LOC_YAXIS1     AS YD_UP_WO_LOC_YAXIS1
    		      ,B.YD_UP_WO_LOC_YAXIS2     AS YD_UP_WO_LOC_YAXIS2
    		      ,B.YD_UP_WO_YAXIS_GAP_MAX  AS YD_UP_WO_YAXIS_GAP_MAX
    		      ,B.YD_UP_WO_YAXIS_GAP_MIN  AS YD_UP_WO_YAXIS_GAP_MIN
    		      ,B.YD_UP_WO_LOC_ZAXIS      AS YD_UP_WO_LOC_ZAXIS
    		      ,B.YD_UP_WO_ZAXIS_GAP_MAX  AS YD_UP_WO_ZAXIS_GAP_MAX
    		      ,B.YD_UP_WO_ZAXIS_GAP_MIN  AS YD_UP_WO_ZAXIS_GAP_MIN
    		      ,B.YD_DN_WO_LOC            AS YD_DN_WO_LOC
    		      ,B.YD_DN_WO_LAYER          AS YD_DN_WO_LAYER
    		      ,B.YD_DN_WO_LOC_XAXIS      AS YD_DN_WO_LOC_XAXIS
    		      ,B.YD_DN_WO_XAXIS_GAP_MAX  AS YD_DN_WO_XAXIS_GAP_MAX
    		      ,B.YD_DN_WO_XAXIS_GAP_MIN  AS YD_DN_WO_XAXIS_GAP_MIN
    		      ,B.YD_DN_WO_LOC_YAXIS      AS YD_DN_WO_LOC_YAXIS
    		      ,B.YD_DN_WO_LOC_YAXIS1     AS YD_DN_WO_LOC_YAXIS1
    		      ,B.YD_DN_WO_LOC_YAXIS2     AS YD_DN_WO_LOC_YAXIS2
    		      ,B.YD_DN_WO_YAXIS_GAP_MAX  AS YD_DN_WO_YAXIS_GAP_MAX
    		      ,B.YD_DN_WO_YAXIS_GAP_MIN  AS YD_DN_WO_YAXIS_GAP_MIN
    		      ,B.YD_DN_WO_LOC_ZAXIS      AS YD_DN_WO_LOC_ZAXIS
    		      ,B.YD_DN_WO_ZAXIS_GAP_MAX  AS YD_DN_WO_ZAXIS_GAP_MAX
    		      ,B.YD_DN_WO_ZAXIS_GAP_MIN  AS YD_DN_WO_ZAXIS_GAP_MIN
    		      ,B.YD_UP_WR_LOC            AS YD_UP_WR_LOC
    		      ,B.YD_UP_WR_LAYER          AS YD_UP_WR_LAYER
    		      ,B.YD_UP_WRK_ACT_GP        AS YD_UP_WRK_ACT_GP
    		      ,B.YD_UP_WR_XAXIS          AS YD_UP_WR_XAXIS
    		      ,B.YD_UP_WR_YAXIS          AS YD_UP_WR_YAXIS
    		      ,B.YD_UP_WR_YAXIS1         AS YD_UP_WR_YAXIS1
    		      ,B.YD_UP_WR_YAXIS2         AS YD_UP_WR_YAXIS2
    		      ,B.YD_UP_WR_ZAXIS          AS YD_UP_WR_ZAXIS
    		      ,B.YD_DN_WR_LOC            AS YD_DN_WR_LOC
    		      ,B.YD_DN_WR_LAYER          AS YD_DN_WR_LAYER
    		      ,B.YD_DN_WRK_ACT_GP        AS YD_DN_WRK_ACT_GP
    		      ,B.YD_DN_WR_XAXIS          AS YD_DN_WR_XAXIS
    		      ,B.YD_DN_WR_YAXIS          AS YD_DN_WR_YAXIS
    		      ,B.YD_DN_WR_YAXIS1         AS YD_DN_WR_YAXIS1
    		      ,B.YD_DN_WR_YAXIS2         AS YD_DN_WR_YAXIS2
    		      ,B.YD_DN_WR_ZAXIS          AS YD_DN_WR_ZAXIS
    		  FROM TB_YD_EQP    A
    		      ,TB_YD_CRNSCH B
    		 WHERE B.YD_EQP_ID   = A.YD_EQP_ID
    		   AND B.YD_WBOOK_ID = :V_YD_WBOOK_ID
    		   AND B.YD_EQP_ID   = :V_YD_EQP_ID
    		   AND B.DEL_YN      = 'N'
    		 ORDER BY B.YD_CRN_SCH_ID
    		 */
    		jsCrnSch = commDao.select(jrInPara, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnschByEqpIdandWBookId", logId, mthdNm, "크레인스케줄 조회");   		
    		
    		
    		JDTORecordSet jsUpCrnSch  = JDTORecordFactory.getInstance().createRecordSet("Temp");
    		JDTORecord    jrUpCrnSch  = JDTORecordFactory.getInstance().create();
    		String        ydUpWoLoc   = "";
    		String        ydUpWoLayer = "";
    		String        sCoilOutDia = "";
			
    		for (int Loop_i = 1; Loop_i <= jsCrnSch.size(); Loop_i++) {
    			jsCrnSch.absolute(Loop_i);
    			jrCrnSch = jsCrnSch.getRecord();
				if ("".equals(commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC")))) {
					jrInPara.setResultCode(logId);	//Log ID
					jrInPara.setResultMsg(mthdNm);	//Log Method Name
					jrInPara.setField("MOD_DDTT"	  , sModifier);
					jrInPara.setField("YD_DN_WO_LOC"  , "XX010101");
					jrInPara.setField("YD_CRN_SCH_ID" , commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID")));
					
					/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnWrkMgtDnLoc	
					SELECT A.YD_EQP_ID                AS YD_EQP_ID                 --야드설비ID
					     , A.YD_CRN_SCH_ID            AS YD_CRN_SCH_ID                 --야드설비ID
					     , A.YD_UP_WO_LOC             AS YD_UP_WO_LOC              --야드권상지시위치
					     , A.YD_UP_WO_LAYER           AS YD_UP_WO_LAYER            --야드권상지시단
					     , B.YD_STK_LYR_XAXIS         AS YD_UP_WO_LOC_XAXIS        --야드권상지시X축
					     , C.YD_STK_BED_XAXIS_TOL     AS YD_UP_WO_XAXIS_GAP_MAX    --야드권상지시X축오차최대
					     , C.YD_STK_BED_XAXIS_TOL     AS YD_UP_WO_XAXIS_GAP_MIN    --야드권상지시X축오차최소
					     , B.YD_STK_LYR_YAXIS         AS YD_UP_WO_LOC_YAXIS
					     , C.YD_STK_BED_YAXIS_TOL     AS YD_UP_WO_YAXIS_GAP_MAX    --야드권상지시Z축오차최대
					     , C.YD_STK_BED_YAXIS_TOL     AS YD_UP_WO_YAXIS_GAP_MIN    --야드권상지시Z축오차최소
					     , B.YD_STK_LYR_ZAXIS         AS YD_UP_WO_LOC_ZAXIS        --야드권상지시Z축
					     , C.YD_STK_BED_ZAXIS_TOL     AS YD_UP_WO_ZAXIS_GAP_MAX    --야드권상지시Z축오차최대
					     , C.YD_STK_BED_ZAXIS_TOL     AS YD_UP_WO_ZAXIS_GAP_MIN    --야드권상지시Z축오차최소
					     , (SELECT ROTATION_ANGLE FROM TB_YD_STKCOL WHERE YD_STK_COL_GP = B.YD_STK_COL_GP) AS ROTATION_ANGLE
					     , :V_MODIFIER                AS MODIFIER              
					     , SYSDATE                    AS MOD_DDTT  
					     , :V_YD_DN_WO_LOC            AS YD_DN_WO_LOC
					     , (SELECT COIL_OUTDIA 
					          FROM TB_YD_CRNWRKMTL CM
					             , TB_PT_COILCOMM  CC
					         WHERE CM.YD_CRN_SCH_ID = A.YD_CRN_SCH_ID
					           AND CM.STL_NO = CC.COIL_NO
					           AND ROWNUM = 1)  AS COIL_OUTDIA
					 FROM TB_YD_CRNSCH A
					    , TB_YD_STKLYR B
					    , TB_YD_STKBED C
					WHERE A.YD_CRN_SCH_ID     = :V_YD_CRN_SCH_ID 
					  AND SUBSTR(A.YD_UP_WO_LOC,1,6) = B.YD_STK_COL_GP
					  AND SUBSTR(A.YD_UP_WO_LOC,7,2) = B.YD_STK_BED_NO
					  AND A.YD_UP_WO_LAYER    = B.YD_STK_LYR_NO
					  AND SUBSTR(A.YD_UP_WO_LOC,1,6) = C.YD_STK_COL_GP
					  AND SUBSTR(A.YD_UP_WO_LOC,7,2) = C.YD_STK_BED_NO
					  AND ROWNUM = 1
					 */		
					jsUpCrnSch = commDao.select(jrInPara, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnWrkMgtDnLoc", logId, mthdNm, "크레인스케줄 조회");   			
					
					for (int Loop_j = 1; Loop_j <= jsUpCrnSch.size(); Loop_j++) {
						jsUpCrnSch.absolute(Loop_j);
						jrUpCrnSch = jsUpCrnSch.getRecord();
						
						ydUpWoLoc   = commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_LOC"));
						ydUpWoLayer = commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_LAYER"));
						sCoilOutDia = commUtils.trim(jrUpCrnSch.getFieldString("COIL_OUTDIA"));
						
						jrInPara.setField("YD_UP_WO_LOC_XAXIS"		, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_LOC_XAXIS")));
						jrInPara.setField("YD_UP_WO_XAXIS_GAP_MAX"	, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_XAXIS_GAP_MAX")));
						jrInPara.setField("YD_UP_WO_XAXIS_GAP_MIN"	, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_XAXIS_GAP_MIN")));
						jrInPara.setField("YD_UP_WO_LOC_YAXIS"		, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_LOC_YAXIS")));
						jrInPara.setField("YD_UP_WO_YAXIS_GAP_MAX"	, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_YAXIS_GAP_MAX")));
						jrInPara.setField("YD_UP_WO_YAXIS_GAP_MIN"	, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_YAXIS_GAP_MIN")));
						jrInPara.setField("YD_UP_WO_LOC_ZAXIS"		, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_LOC_ZAXIS")));
						jrInPara.setField("YD_UP_WO_ZAXIS_GAP_MAX"	, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_ZAXIS_GAP_MAX")));
						jrInPara.setField("YD_UP_WO_ZAXIS_GAP_MIN"	, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_ZAXIS_GAP_MIN")));
						jrInPara.setField("ROTATION_ANGLE"			, commUtils.trim(jrUpCrnSch.getFieldString("ROTATION_ANGLE")));
						jrInPara.setField("YD_CRN_SCH_ID"			, commUtils.trim(jrUpCrnSch.getFieldString("YD_CRN_SCH_ID")));

//0709  SJH 보정값 계산하는 FUNCTIOM 별도 있음(YDY5L004)에서 사용함						
//						if((!"H".equals(ydSchCd.substring(7, 8))) && ("002".equals(ydUpWoLayer))) {				
//							sLogMsg = "권상Y좌표재계산 ";
//							commUtils.printLog(logId, sLogMsg, "SL");
//							
////							int intRtnVal1 = searchCoilYdGdsClineY(logId, mthdNms,ydUpWoLoc.substring(0,6),ydUpWoLoc.substring(6,8),ydUpWoLayer,commUtils.trim(jrUpLayerXy.getFieldString("STACK_LAYER_Y_AXIS"  )));				
//							int iRtnY = this.getUpDnClineY(logId, mthdNm, ydUpWoLoc.substring(0,6),ydUpWoLoc.substring(6,8),ydUpWoLayer,commUtils.trim(jrUpCrnSch.getFieldString("YD_STK_LYR_YAXIS"  )));				
//							if(iRtnY > 0) {
//								sLogMsg = "권상Y좌표재계산값:"+ iRtnY;
//								commUtils.printLog(logId, sLogMsg, "SL");
//								jrInPara.setField("YD_UP_WO_LOC_YAXIS",  String.valueOf(iRtnY)) ;	
//							}
//						}
						if ("002".equals(ydUpWoLayer)) {	
							int iRtnY = this.getUpDnClineY(logId, mthdNm, ydUpWoLoc.substring(0,6),ydUpWoLoc.substring(6,8),ydUpWoLayer,sCoilOutDia);				
							if(iRtnY > 0) {
								sLogMsg = "권상Y좌표재계산값:"+ iRtnY;
								commUtils.printLog(logId, sLogMsg, "SL");
								jrInPara.setField("YD_UP_WO_LOC_YAXIS",  String.valueOf(iRtnY)) ;	
							}
						}	
						/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnWrkMgtDnLoc 
						UPDATE TB_YD_CRNSCH 
						   SET MODIFIER               = :V_MODIFIER
						     , MOD_DDTT               = SYSDATE
						     , YD_DN_WO_LOC           = :V_YD_DN_WO_LOC
						     , YD_UP_WO_LOC_XAXIS     = :V_YD_UP_WO_LOC_XAXIS
						     , YD_UP_WO_XAXIS_GAP_MAX = :V_YD_UP_WO_XAXIS_GAP_MAX
						     , YD_UP_WO_XAXIS_GAP_MIN = :V_YD_UP_WO_XAXIS_GAP_MIN
						     , YD_UP_WO_LOC_YAXIS     = :V_YD_UP_WO_LOC_YAXIS
						     , YD_UP_WO_YAXIS_GAP_MAX = :V_YD_UP_WO_YAXIS_GAP_MAX
						     , YD_UP_WO_YAXIS_GAP_MIN = :V_YD_UP_WO_YAXIS_GAP_MIN
						     , YD_UP_WO_LOC_ZAXIS     = :V_YD_UP_WO_LOC_ZAXIS
						     , YD_UP_WO_ZAXIS_GAP_MAX = :V_YD_UP_WO_ZAXIS_GAP_MAX
						     , YD_UP_WO_ZAXIS_GAP_MIN = :V_YD_UP_WO_ZAXIS_GAP_MIN     
						     , UP_ROTATION_ANGLE      = :V_ROTATION_ANGLE
						     , YD_EQP_WRK_SH          = (SELECT COUNT(*) 
						                                   FROM TB_YD_CRNWRKMTL 
						                                  WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
						 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID    
						 */
					    intRtnVal = commDao.update(jrInPara, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnWrkMgtDnLoc", logId, mthdNm, "크레인스케줄 갱신");
					     
						if (intRtnVal <= 0) {
							sLogMsg = "크레인스케줄 To위치 Default값 등록 실패!!";
		    				commUtils.printLog(logId, sLogMsg, "SL");
		    				jrRtn.setField("RTN_CD"		, "0");
		    				jrRtn.setField("RTN_MSG"	, sLogMsg);
		        			return jrRtn;
						}

						
						/********************************************************
						 * 정정보급존 자동이적 XX일때 스케쥴 취소
						 ********************************************************/
						ydSchCd = commUtils.trim(jrCrnSch.getFieldString("YD_SCH_CD"));
						if ("YD04MH".equals(ydSchCd.substring(2,8)) || "YD54MH".equals(ydSchCd.substring(2,8))
						 || "YD08MH".equals(ydSchCd.substring(2,8)) || "YD58MH".equals(ydSchCd.substring(2,8))	//자동이적 2	
						) {
						 
							String sAPP310 = coilDao.ApplyYn(logId, mthdNm, "APP310", "J", "*");
							commUtils.printLog(logId, "정정보급존 자동이적 XX스케쥴 취소 : "+ sAPP310, "SL");
							if ("Y".equals(sAPP310)) {
								
								jrParam = commUtils.getParam(logId, mthdNm, "AUTOCANCEL"); 
								jrParam.setField("YD_CRN_SCH_ID", commUtils.trim(jrUpCrnSch.getFieldString("YD_CRN_SCH_ID")));
								jrParam.setField("YD_STK_COL_GP", ydUpWoLoc.substring(0,6));
								jrParam.setField("YD_STK_BED_NO", ydUpWoLoc.substring(6,8));
								jrParam.setField("YD_STK_LYR_NO", ydUpWoLayer);
								jrParam.setField("DEL_YN"		, "Y");
								
								/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdStkLyrToXX
								UPDATE TB_YD_STKLYR
								   SET MODIFIER            = :V_MODIFIER
								     , MOD_DDTT            = SYSDATE
								     , YD_STK_LYR_MTL_STAT = NVL2(STL_NO, 'C', 'E')
								 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
								   AND YD_STK_BED_NO       = :V_YD_STK_BED_NO
								   AND YD_STK_LYR_NO       = :V_YD_STK_LYR_NO
								*/
								commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdStkLyrToXX", logId, mthdNm, "정정보급존 자동이적 권상위치 취소");
								
								/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnSchDelYn
								UPDATE TB_YD_CRNSCH
								   SET MODIFIER = :V_MODIFIER
								     , MOD_DDTT = SYSDATE
								     , DEL_YN   = :V_DEL_YN
								 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
								*/
								commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnSchDelYn", logId, mthdNm, "정정보급존 자동이적 스케쥴 취소");
								
								/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnSchMtlDelYn
								UPDATE TB_YD_CRNWRKMTL
								   SET MODIFIER = :V_MODIFIER
								     , MOD_DDTT = SYSDATE
								     , DEL_YN   = :V_DEL_YN
								 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
								*/
								commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnSchMtlDelYn", logId, mthdNm, "정정보급존 자동이적 스케쥴 재료 취소");
								
								jrRtn.setField("RTN_MSG", "["+ sStlNo +"]정정보급존 자동이적 TO위치 검색 실패!");
							}
						}
					}
				
					/********************************************************
					 * 권하위치 XX 일때 로그테이블에 등록
					 * 제품 : ('JAKD01LM','JBKD01LM','JCKD01LM','JHKD01LM','JEKD01LM','JCFD01LM','JGFD01LM')
					 * 소재 : CV01LH
					 ********************************************************/
					JDTORecord jrInXXLog 	= commUtils.getParam(logId, mthdNm, sModifier);
					String toLocDcsn   = commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"));
					String toLocStlNo  = commUtils.trim(jrCrnSch.getFieldString("STL_NO"));
					
					jrInXXLog.setField("YD_WBOOK_ID" , commUtils.trim(jrCrnSch.getFieldString("YD_WBOOK_ID")));
					jrInXXLog.setField("YD_SCH_CD"   , commUtils.trim(jrCrnSch.getFieldString("YD_SCH_CD")));
					jrInXXLog.setField("YD_EQP_ID"   , commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID")));	
					jrInXXLog.setField("STL_NO"      , toLocStlNo);	
					jrInXXLog.setField("YD_CRN_SCH_ID"       , commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID")));
					jrInXXLog.setField("YD_LOC_ABLECHECK"    , "crnSchToLoc"); 
					jrInXXLog.setField("YD_CRNSCHLOC_NOTE"   , toLocDcsn); //W:보조작업  S:주작업
					
					JDTORecordSet jsSchCdChk = commDao.select(jrInXXLog, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getSchCdChk", logId, mthdNm, "스케줄코드예외 조회"); 

					if (jsSchCdChk.size() > 0) {
						sLogMsg = "로그기록 없는 크레인스케줄  : "+ydSchCd;
	    				commUtils.printLog(logId, sLogMsg, "SL");
					}else{
							sLogMsg = "로그기록 START : "+ydSchCd;
		    				commUtils.printLog(logId, sLogMsg, "SL");
		    				commUtils.printLog(logId, "sCrnToLocDcsnMtd >> "+sCrnToLocDcsnMtd, "SL");
						   EJBConnector SchXXLog = new EJBConnector("default", "CCoilSchSeEJB", this);
						   SchXXLog.trx( "insSchXXLog" , new Class[] { JDTORecord.class }, new Object[] { jrInXXLog });
						   
						   jrRtn.setField("YD_TOXX_CHK"	, "Y");
					}						
			
				
				}
			}
			
		//-------------------------------------------------------------------------------------------------------------
    		if(jsCrnSch.size() > 0 ) {
	    		jsCrnSch.absolute(1);
				JDTORecord jrCrnSch1 =  jsCrnSch.getRecord();
				jrRtn.setField("YD_CRN_SCH_ID", commUtils.trim(jrCrnSch1.getFieldString("YD_CRN_SCH_ID")));

//				if ("XX010101".equals(commUtils.trim(jrCrnSch1.getFieldString("YD_CRN_SCH_ID")))) {
//					//첫번째 크레인작업이 TO위치가 결정이 되었다면...크레인작업지시 송신
//					jrRtn.setField("YD_CRN_SCH_ID_RE", commUtils.trim(jrCrnSch1.getFieldString("YD_CRN_SCH_ID")));
//				}	
    		}
    		
    		commUtils.printLog(logId, "대차이동지시 송신 여부(sTcMoveYn) :" + sTcMoveYn , "SL");
    		
        	if ("Y".equals(sTcMoveYn)) {   // 대차 자동출발 송신
        		jrRtn.setField("TC_MOVE_YN"	, "Y");
    		} else {
        		jrRtn.setField("TC_MOVE_YN"	, "N");
    		}

        	jrRtn.setField("RTN_CD", "1");
			
        	commUtils.printLog(logId, mthdNm, "S-");
        	return jrRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
    }    
    
    
	/**
	 * Scrap작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord toLocScrap(String logId, String mthdNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String mthdNm = "TO 위치 결정:Scrap작업[CCoilSchSeEJB.toLocScrap] < " + mthdNms;
    	String sLogMsg= null;

    	JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();	//전문 Return

    	/**---------------------------------------------
		* 스크랩작업은 적치가능 유무 판단 불 필요
		*
		----------------------------------------------*/
		try {
			

			commUtils.printLog(logId, mthdNm, "S+");

			String ydSchCd 	   	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"		));	// 크레인스케줄코드
			String ydWbookId	= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"	));	// 작업예약
			String schLogYn	    = commUtils.trim(jrWbook.getFieldString("SCHLOG_YN"		));	// 작업예약
			
			String sStlNo	   	= commUtils.trim(jrCrnSch.getFieldString("STL_NO"		));	// 크레인작업재료
			String ydEqpId     	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"	));	// 크레인설비ID
			String ydUpWoLoc 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"	));		
			String ydUpWoLayer 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));		
			String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));	// 크레인스케줄ID
			String ydToLocGuide = commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_GUIDE"));
			String sModifier    = commUtils.trim(jrCrnSch.getFieldString("MODIFIER"		));
			
			String ydRouteGp    = coilDao.getCoilYdRouteGp(logId,mthdNm,ydSchCd,sStlNo);	// 검색조건 행선

			String sDBLogMsg	= "";
			String sRtnBedDan 	= "";  //TO위치	
		    String ydStkColGp 	= "";
 			String ydStkBedNo 	= "";
 			String ydStkLyrNo 	= "";	
 			String sSchLogContents 	= "";	
 			
 			if ( "".equals(ydUpWoLoc) ) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, sLogMsg, "SL");
//				return CConstant.RETN_CD_FAILURE;
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			}
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);	// 크레인스케쥴ID
			jrParam.setField("YD_SCH_CD"		, ydSchCd);		// 스케쥴코드
			jrParam.setField("YD_ROUTE_GP"		, ydRouteGp);	// 행선
			
			sLogMsg =  " TOSQL"+ydCrnSchId+ " 권상재료["+sStlNo +" +스케쥴 코드 : "+ ydSchCd + "의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, sLogMsg, "SL");
	      	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	사용자 지정한 위치만 검색
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			if (ydToLocGuide.length() == 10) {
				sLogMsg =  " 적재위치 가이드 열+ 베드+ 단  지정된 경우 ["+ydToLocGuide+"]의 베드 조회 시작";
				commUtils.printLog(logId, sLogMsg, "SL");
				sRtnBedDan = ydToLocGuide;
				
			} else {
				commUtils.printLog(logId, sLogMsg + ydSchCd.substring(2, 4), "SL");
				
				/**********************************************************
				 * ★★★★ 검색조건은 스크랩꺼 사용 : 행선은 무조건 '동'+'Z' ★★★★
				 **********************************************************/
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocScrap
				 -- 대상 위치 SELECT
				WITH  TO_LOC_TABLE AS (
				    SELECT ABS(TO_NUMBER(SL.YD_STK_LYR_NO) - TO_NUMBER(C1.COIL_YD_STK_LYR_NO)) AS PRIOR4 -- LYR 우선
				         , X1.YD_SCH_CD
				         , SL.YD_STK_COL_GP         AS TAG_YD_STK_COL_GP
				         , SL.YD_STK_BED_NO         AS TAG_YD_STK_BED_NO
				         , SL.YD_STK_LYR_NO         AS TAG_YD_STK_LYR_NO
				         , SL.YD_STK_LYR_MTL_STAT   AS TAG_YD_STK_LYR_MTL_STAT
				         -- 열에 따른 코일 간격
				         , SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP)  AS INT
				         -- LEFT_BED
				         , DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
				                                    '002', SL.YD_STK_BED_NO)                               AS TAG_LEFT_BED
				         , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_LYR_NO,
				                                    '002', LPAD(TO_NUMBER(SL.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_LEFT_LAYER
				         -- RIGHT BED
				         , DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
				                                    '002', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'))
				                                                                                           AS TAG_RIGHT_BED
				         , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_LYR_NO,
				                                    '002', LPAD(TO_NUMBER(SL.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_RIGHT_LAYER
				         , C1.STL_NO
				         , C1.COIL_YD_STK_COL_GP
				         , C1.COIL_YD_STK_BED_NO
				         , C1.COIL_YD_STK_LYR_NO
				         , X1.YD_LOC_SRCH_RNG_SEQ
				         , X1.YD_STK_BED_SRCH_SEQ
				         -- 스크랩 중량물 끝번지부터
				         , CASE WHEN C1.COIL_WT < SCRAP_WT THEN ROW_NUMBER() OVER(ORDER BY SL.YD_STK_COL_GP, SL.YD_STK_BED_NO)
				                ELSE                            ROW_NUMBER() OVER(ORDER BY SL.YD_STK_COL_GP DESC, SL.YD_STK_BED_NO DESC)
				           END WT_ORD
				      FROM TB_YD_STKLYR SL
				         , (
				            SELECT SUBSTR(AA.YD_UP_WO_LOC,1,6)   AS COIL_YD_STK_COL_GP
				                 , SUBSTR(AA.YD_UP_WO_LOC,7,2)   AS COIL_YD_STK_BED_NO
				                 , AA.YD_UP_WO_LAYER             AS COIL_YD_STK_LYR_NO
				                 , CC.COIL_NO                    AS STL_NO
				                 , CC.COIL_WT                    AS COIL_WT
				                 -- 동별 스크랩 중량물 기준
				                 , (SELECT ITEM1
				                      FROM TB_YD_RULE
				                     WHERE REPR_CD_GP = 'SCRAP'
				                       AND CD_GP = 'J'
				                       AND ITEM = 'SWT'|| SUBSTR(AA.YD_SCH_CD, 2, 1)
				                   ) AS SCRAP_WT
				              FROM TB_YD_CRNSCH          AA
				                 , TB_YD_CRNWRKMTL       BB
				                 , USRPTA.TB_PT_COILCOMM CC
				             WHERE AA.YD_CRN_SCH_ID = BB.YD_CRN_SCH_ID
				               AND AA.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				               AND BB.STL_NO        = CC.COIL_NO
				           ) C1
				         , (
				            SELECT A.YD_SCH_CD                AS YD_SCH_CD
				                 , A.YD_ROUTE_GP              AS YD_ROUTE_GP
				                 , A.YD_LOC_SRCH_RNG_REG_SNO  AS YD_LOC_SRCH_RNG_REG_SNO
				                 , A.YD_LOC_SRCH_RNG_SEQ      AS YD_LOC_SRCH_RNG_SEQ
				                 , B.YD_STK_BED_SRCH_SEQ      AS YD_STK_BED_SRCH_SEQ
				                 , B.YD_STK_COL_GP            AS YD_STK_COL_GP
				                 , B.YD_STK_BED_NO            AS YD_STK_BED_NO
				              FROM TB_YD_LOCSRCHRNG A
				                 , TB_YD_LOCSRCHBED B
				             WHERE A.YD_LOC_SRCH_RNG_REG_SNO = B.YD_LOC_SRCH_RNG_REG_SNO
				               AND A.YD_SCH_CD               = :V_YD_SCH_CD
				               AND A.YD_ROUTE_GP             = SUBSTR(:V_YD_SCH_CD,2,1) ||'Z'
				               AND A.DEL_YN                  = 'N'
				               AND B.DEL_YN                  = 'N'
				           ) X1
				     WHERE SL.YD_STK_COL_GP             = X1.YD_STK_COL_GP
				       AND SL.YD_STK_BED_NO             = X1.YD_STK_BED_NO
				       AND SUBSTR(SL.YD_STK_COL_GP,1,1) = SUBSTR(C1.COIL_YD_STK_COL_GP,1,1) -- 야드구분
				       AND SUBSTR(SL.YD_STK_COL_GP,2,1) = SUBSTR(C1.COIL_YD_STK_COL_GP,2,1) -- 동구분
				       AND SL.YD_STK_LYR_NO       IN ('001', '002')
				       AND SL.YD_STK_LYR_ACT_STAT = 'E'
				       AND SL.YD_STK_LYR_MTL_STAT = 'E'
				       AND SL.DEL_YN              = 'N'
				       AND SL.YD_STK_BED_NO BETWEEN '00' AND '99'
				       -- 작업예약 TO위치 가이드 제외
				       AND SL.YD_STK_COL_GP||SL.YD_STK_BED_NO NOT IN ( SELECT NVL(YD_TO_LOC_GUIDE,'1') FROM TB_YD_WRKBOOK WHERE DEL_YN = 'N')
				)
				, TO_LOC_DATA_TABLE AS (
				-- TO위치 코일정보 SELECT
				SELECT A.PRIOR4
				     , A.STL_NO
				     , A.YD_SCH_CD
				     , A.TAG_YD_STK_COL_GP
				     , A.TAG_YD_STK_BED_NO
				     , A.TAG_YD_STK_LYR_NO
				     , A.TAG_LEFT_BED
				     , A.TAG_LEFT_LAYER
				     , A.YD_LOC_SRCH_RNG_SEQ
				     , A.YD_STK_BED_SRCH_SEQ
				     , (SELECT YD_STK_LYR_ACT_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_LEFT_ACTIVE_STAT
				     , (SELECT YD_STK_LYR_MTL_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_LEFT_LAYER_STAT
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_LEFT_STL_NO
				     , A.TAG_RIGHT_BED
				     , A.TAG_RIGHT_LAYER
				     , (SELECT YD_STK_LYR_ACT_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_RIGHT_ACTIVE_STAT
				     , (SELECT YD_STK_LYR_MTL_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_RIGHT_LAYER_STAT
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_RIGHT_STL_NO
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = '002'
				           AND B.DEL_YN = 'N') AS TAG_2DAN_LEFT_STL_NO
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_YD_STK_BED_NO
				           AND B.YD_STK_LYR_NO  = '002'
				           AND B.DEL_YN = 'N') AS TAG_2DAN_RIGHT_STL_NO
				     , A.WT_ORD
				  FROM TO_LOC_TABLE A
				)
				, TO_LOC_DATA_COMP_TABLE AS (
				--*--*--*--*--*--*-- 적치가능위치
				    SELECT K.*
				      FROM TO_LOC_DATA_TABLE K
				         , (SELECT 1 T_ROW, A.*
				              FROM USRPTA.TB_PT_COILCOMM  A ) C  --대상코일
				         , (SELECT 1 T_ROW, A.*
				              FROM USRPTA.TB_PT_COILCOMM  A ) L  --하단LEFT
				         , (SELECT 1 T_ROW, A.*
				              FROM USRPTA.TB_PT_COILCOMM  A ) R  --하단RIGHT
				     WHERE K.STL_NO           = C.COIL_NO(+)
				       AND K.TAG_LEFT_STL_NO  = L.COIL_NO(+)
				       AND K.TAG_RIGHT_STL_NO = R.COIL_NO(+)
				       --2단일 경우 좌우 적치 상태 CHECK
				       AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002'
				                         AND (TAG_LEFT_ACTIVE_STAT != 'E' OR TAG_RIGHT_ACTIVE_STAT != 'E'
				                           OR TAG_LEFT_LAYER_STAT  != 'C' OR TAG_RIGHT_LAYER_STAT  != 'C') THEN 0
				                    ELSE 1 END
				       --1단일 경우 좌우 상단 적치 상태 CHECK
				       AND 1 = CASE WHEN K.TAG_YD_STK_LYR_NO = '001' AND (TAG_2DAN_LEFT_STL_NO IS NOT NULL OR TAG_2DAN_RIGHT_STL_NO IS NOT NULL) THEN 0
				                    ELSE 1 END
				)
				--*--*--*--*--*--*-- 평점
				SELECT G.*
				     , '1' AS GRADE
				  FROM TO_LOC_DATA_COMP_TABLE G
				 ORDER BY GRADE
				        , WT_ORD
				*/
 				JDTORecordSet jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocScrap", logId, mthdNm, "Scrap 베드 조회");
 				if (jsResult.size() <= 0) {
 					sLogMsg = "적치가능한 베드 검색 실패 ";
 					commUtils.printLog(logId, sLogMsg, "SL");

 					if ("Y".equals(schLogYn)) {
 						sSchLogContents = "[getYdToLocScrap] 대상코일위치검색실패:"+ sStlNo+" LOG :"+"\r\n";
		    			this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );
					}
 					
 					jrRtn.setField("RTN_CD" , "0");	
 					jrRtn.setField("RTN_MSG", sLogMsg);
 					return jrRtn;
 				} else {
 					
 					ydStkColGp 	= commUtils.trim(jsResult.getRecord(0).getFieldString("TAG_YD_STK_COL_GP"));
 					ydStkBedNo 	= commUtils.trim(jsResult.getRecord(0).getFieldString("TAG_YD_STK_BED_NO"));
 					ydStkLyrNo  = commUtils.trim(jsResult.getRecord(0).getFieldString("TAG_YD_STK_LYR_NO"));
 					sRtnBedDan	= ydStkColGp + ydStkBedNo + ydStkLyrNo;
 					
 				}
 				
 				if (sRtnBedDan.length() < 10) {
 					sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 TO위치 결정 실패 ";
 					commUtils.printLog(logId, sLogMsg, "SL");

 					if ("Y".equals(schLogYn)) {
 						sSchLogContents = "[getYdToLocScrap]대상코일선택실패:"+ sStlNo+" LOG :"+"\r\n" + sDBLogMsg;
		    			this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );
					}
 					
 					jrRtn.setField("RTN_CD" , "0");	
 					jrRtn.setField("RTN_MSG", sLogMsg);
 					return jrRtn;			
 				}
 				
				if ("Y".equals(schLogYn)) {
					sSchLogContents = "[getYdToLocScrap]대상코일선택:"+ sStlNo+"  선택위치:"+ sRtnBedDan + " LOG :"+"\r\n" + sDBLogMsg;
	    			this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );
				}
 			}				
         

			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = commUtils.getParam(logId, mthdNm, sModifier);
			jrSetLoc.setField("YD_CRN_SCH_ID" , ydCrnSchId); 
			jrSetLoc.setField("YD_EQP_ID"     , ydEqpId);	 
			jrSetLoc.setField("YD_SCH_CD"     , ydSchCd);	 
			jrSetLoc.setField("YD_UP_WO_LOC"  , ydUpWoLoc); 
			jrSetLoc.setField("YD_UP_WO_LAYER", ydUpWoLayer);	 
			jrSetLoc.setField("YD_DN_WO_LOC"  ,	sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER", sRtnBedDan.substring(8));
			jrSetLoc.setField("YD_WBOOK_ID"   ,	ydWbookId); 
				
			JDTORecord jrRcvRtn = this.toLocUpdCrnSch(logId,mthdNm, jrSetLoc, jrCrnSch  );
			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
			
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
        }//end of try~catch			
	} 
    
	/**
	 * 지포장장 작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord toLocGF(String logId, String mthdNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String mthdNm = "TO 위치 결정:지포장장[CCoilSchSeEJB.toLocGF] < " + mthdNms;
    	String sLogMsg= null;

    	JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();	//전문 Return

    	/**---------------------------------------------
		* 지포장장은 적치가능 유무 판단 불 필요
		*
		----------------------------------------------*/
		try {
			

			commUtils.printLog(logId, mthdNm, "S+");

			String ydSchCd 	   	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"		));	// 크레인스케줄코드
			String ydWbookId	= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"	));	// 작업예약
			String schLogYn	    = commUtils.trim(jrWbook.getFieldString("SCHLOG_YN"		));	// 작업예약
			
			String sStlNo	   	= commUtils.trim(jrCrnSch.getFieldString("STL_NO"		));	// 크레인작업재료
			String ydEqpId     	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"	));	// 크레인설비ID
			String ydUpWoLoc 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"	));		
			String ydUpWoLayer 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));		
			String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));	// 크레인스케줄ID
			String ydToLocGuide = commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_GUIDE"));
			String sModifier    = commUtils.trim(jrCrnSch.getFieldString("MODIFIER"		));
			
			String ydRouteGp    = coilDao.getCoilYdRouteGp(logId,mthdNm,ydSchCd,sStlNo);	// 검색조건 행선

			String sDBLogMsg	= "";
			String sRtnBedDan 	= "";  //TO위치	
		    String ydStkColGp 	= "";
 			String ydStkBedNo 	= "";
 			String ydStkLyrNo 	= "";	
 			String sSchLogContents 	= "";	
 			
 			if ( "".equals(ydUpWoLoc) ) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, sLogMsg, "SL");
//				return CConstant.RETN_CD_FAILURE;
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			}
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);	// 크레인스케쥴ID
			jrParam.setField("YD_SCH_CD"		, ydSchCd);		// 스케쥴코드
			jrParam.setField("YD_ROUTE_GP"		, ydRouteGp);	// 행선
			
			sLogMsg =  " TOSQL"+ydCrnSchId+ " 권상재료["+sStlNo +" +스케쥴 코드 : "+ ydSchCd + "의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, sLogMsg, "SL");
	      	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	사용자 지정한 위치만 검색
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, sLogMsg + ydSchCd.substring(2, 4), "SL");
			
			/**********************************************************
			 **********************************************************/
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocGF 
			 -- 대상 위치 SELECT
			WITH  TO_LOC_TABLE AS (
			    SELECT ABS(TO_NUMBER(SL.YD_STK_LYR_NO) - TO_NUMBER(C1.COIL_YD_STK_LYR_NO)) AS PRIOR4 -- LYR 우선
			         , X1.YD_SCH_CD
			         , SL.YD_STK_COL_GP         AS TAG_YD_STK_COL_GP
			         , SL.YD_STK_BED_NO         AS TAG_YD_STK_BED_NO
			         , SL.YD_STK_LYR_NO         AS TAG_YD_STK_LYR_NO
			         , SL.YD_STK_LYR_MTL_STAT   AS TAG_YD_STK_LYR_MTL_STAT
			         -- 열에 따른 코일 간격
			         , SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP)  AS INT
			         , C1.STL_NO
			         , C1.COIL_W
			         , C1.COIL_YD_STK_COL_GP
			         , C1.COIL_YD_STK_BED_NO
			         , C1.COIL_YD_STK_LYR_NO
			         , C1.COIL_OUTDIA_GRP_GP
			         , X1.YD_LOC_SRCH_RNG_SEQ
			         , X1.YD_STK_BED_SRCH_SEQ
			         -- 적치가능여부 CHECK시 필요 : H/J/ABC/WRAP
			         , 'H' AS CHK_SKID_GP
			         , C1.W_GP
			      FROM TB_YD_STKLYR SL
			         , (
			             SELECT SUBSTR(AA.YD_UP_WO_LOC,1,6)   AS COIL_YD_STK_COL_GP
			                  , SUBSTR(AA.YD_UP_WO_LOC,7,2)   AS COIL_YD_STK_BED_NO
			                  , AA.YD_UP_WO_LAYER             AS COIL_YD_STK_LYR_NO
			                  , CC.COIL_NO                    AS STL_NO
			                  , CASE WHEN CC.COIL_OUTDIA <= 1280 THEN 'A'
			                         WHEN CC.COIL_OUTDIA >  1930 THEN 'C'
			                         ELSE 'B' END             AS COIL_OUTDIA_GRP_GP
			                  , CC.COIL_W
			                  , CASE WHEN CC.COIL_W > 1600 THEN 'L' -- 광폭
			                         ELSE                       'M' -- 보폭
			                    END AS W_GP
			               FROM TB_YD_CRNSCH          AA
			                  , TB_YD_CRNWRKMTL       BB
			                  , USRPTA.TB_PT_COILCOMM CC
			              WHERE AA.YD_CRN_SCH_ID = BB.YD_CRN_SCH_ID
			                AND AA.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			                AND BB.STL_NO        = CC.COIL_NO
			           ) C1
			         , (
			             SELECT A.YD_SCH_CD                AS YD_SCH_CD
			                  , A.YD_ROUTE_GP              AS YD_ROUTE_GP
			                  , A.YD_LOC_SRCH_RNG_REG_SNO  AS YD_LOC_SRCH_RNG_REG_SNO
			                  , A.YD_STR_GTR_CD            AS YD_STR_GTR_CD
			                  , A.YD_LOC_SRCH_RNG_SEQ      AS YD_LOC_SRCH_RNG_SEQ
			                  , A.YD_LOC_SRCH_RNG_ACT_STAT AS YD_LOC_SRCH_RNG_ACT_STAT
			                  , A.YD_STK_BED_SRCH_METHOD   AS YD_STK_BED_SRCH_METHOD
			                  , B.YD_LOC_SRCH_BED_REG_SNO  AS YD_LOC_SRCH_BED_REG_SNO
			                  , B.YD_STK_BED_SRCH_SEQ      AS YD_STK_BED_SRCH_SEQ
			                  , B.YD_STK_COL_GP            AS YD_STK_COL_GP
			                  , B.YD_STK_BED_NO            AS YD_STK_BED_NO
			                  , MOD(SUBSTR(B.YD_STK_COL_GP, 5, 2), 2) AS MOD_VAL
			               FROM TB_YD_LOCSRCHRNG A
			                  , TB_YD_LOCSRCHBED B
			              WHERE A.YD_LOC_SRCH_RNG_REG_SNO = B.YD_LOC_SRCH_RNG_REG_SNO
			                AND A.YD_SCH_CD               = :V_YD_SCH_CD
			                AND A.YD_ROUTE_GP             = :V_YD_ROUTE_GP
			                AND A.DEL_YN = 'N'
			                AND B.DEL_YN = 'N'
			          ) X1
			     WHERE SL.YD_STK_COL_GP       = X1.YD_STK_COL_GP
			       AND SL.YD_STK_BED_NO       = X1.YD_STK_BED_NO
			       AND SUBSTR(SL.YD_STK_COL_GP,1,1) = SUBSTR(C1.COIL_YD_STK_COL_GP,1,1) -- 야드구분
			       AND SUBSTR(SL.YD_STK_COL_GP,2,1) = SUBSTR(C1.COIL_YD_STK_COL_GP,2,1) -- 동구분
			       AND SL.YD_STK_LYR_NO       IN ('001')
			       AND SL.YD_STK_LYR_ACT_STAT = 'E'
			       AND SL.YD_STK_LYR_MTL_STAT = 'E'
			       AND SL.DEL_YN = 'N'
			       AND SL.YD_STK_BED_NO BETWEEN '00' AND '99'
			       -- 가상위치 제외
			       AND SUBSTR(SL.YD_STK_COL_GP,3,2) NOT IN ('01'  ,'80')
			       -- 결로적치 제외
			       AND SUBSTR(SL.YD_STK_COL_GP,1,4) NOT IN ('JE50','JF47')
			       -- 동일외경군(소재위치 제외)
			       -- 적치폭 기준 CHECK(소재위치 제외)
			       -- 설비 작업대상재 제외
			       -- 크레인 작업지시 제외
			       -- AND SL.YD_STK_COL_GP||SL.YD_STK_BED_NO||SL.YD_STK_LYR_NO NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YD_CRNSCH WHERE DEL_YN = 'N')
			   -- 작업예약 TO위치 가이드 제외
			       AND SL.YD_STK_COL_GP||SL.YD_STK_BED_NO NOT IN ( SELECT NVL(YD_TO_LOC_GUIDE,'1') FROM TB_YD_WRKBOOK WHERE DEL_YN = 'N')
			 )
			, TO_LOC_DATA_TABLE AS (
			-- TO위치 코일정보 SELECT
			SELECT A.PRIOR4
			     , A.STL_NO
			     , A.YD_SCH_CD
			     , A.TAG_YD_STK_COL_GP
			     , A.TAG_YD_STK_BED_NO
			     , A.TAG_YD_STK_LYR_NO
			     , A.YD_LOC_SRCH_RNG_SEQ
			     , A.YD_STK_BED_SRCH_SEQ
			     , A.COIL_OUTDIA_GRP_GP
			     , A.CHK_SKID_GP
			     , (SELECT MAX(YD_STK_BED_NO)
			          FROM TB_YD_STKLYR WHERE YD_STK_COL_GP = A.TAG_YD_STK_COL_GP AND ROWNUM = 1) AS COL_MAX_BED_NO
			     , A.W_GP
			  FROM TO_LOC_TABLE A
			), TO_LOC_DATA_COMP_TABLE AS (
			--*--*--*--*--*--*-- 적치가능위치
			    SELECT K.*
			      FROM TO_LOC_DATA_TABLE K
			         , (SELECT 1 T_ROW, A.*
			              FROM USRPTA.TB_PT_COILCOMM  A ) C  --대상코일
			     WHERE K.STL_NO           = C.COIL_NO(+)
			)
			--*--*--*--*--*--*-- 평점
			SELECT G.*
			     , '1' AS GRADE
			  FROM (SELECT A.*
			          FROM TO_LOC_DATA_COMP_TABLE A
			 ) G
			 ORDER BY GRADE
			       
			*/
			JDTORecordSet jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocGF", logId, mthdNm, "Scrap 베드 조회");
			if (jsResult.size() <= 0) {
				sLogMsg = "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, sLogMsg, "SL");

				if ("Y".equals(schLogYn)) {
					sSchLogContents = "[getYdToLocGF] 대상코일위치검색실패:"+ sStlNo+" LOG :"+"\r\n";
	    			this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );
				}
				
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			} else {
				
				ydStkColGp 	= commUtils.trim(jsResult.getRecord(0).getFieldString("TAG_YD_STK_COL_GP"));
				ydStkBedNo 	= commUtils.trim(jsResult.getRecord(0).getFieldString("TAG_YD_STK_BED_NO"));
				ydStkLyrNo  = commUtils.trim(jsResult.getRecord(0).getFieldString("TAG_YD_STK_LYR_NO"));
				sRtnBedDan	= ydStkColGp + ydStkBedNo + ydStkLyrNo;
				
			}
			
			if (sRtnBedDan.length() < 10) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 TO위치 결정 실패 ";
				commUtils.printLog(logId, sLogMsg, "SL");

				if ("Y".equals(schLogYn)) {
					sSchLogContents = "[getYdToLocGF] 대상코일선택실패:"+ sStlNo+" LOG :"+"\r\n" + sDBLogMsg;
	    			this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );
				}
				
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;			
			}
			
			if ("Y".equals(schLogYn)) {
				sSchLogContents = "[getYdToLocGF] 대상코일선택:"+ sStlNo+"  선택위치:"+ sRtnBedDan + " LOG :"+"\r\n" + sDBLogMsg;
    			this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );
			}
		
         

			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = commUtils.getParam(logId, mthdNm, sModifier);
			jrSetLoc.setField("YD_CRN_SCH_ID" , ydCrnSchId); 
			jrSetLoc.setField("YD_EQP_ID"     , ydEqpId);	 
			jrSetLoc.setField("YD_SCH_CD"     , ydSchCd);	 
			jrSetLoc.setField("YD_UP_WO_LOC"  , ydUpWoLoc); 
			jrSetLoc.setField("YD_UP_WO_LAYER", ydUpWoLayer);	 
			jrSetLoc.setField("YD_DN_WO_LOC"  ,	sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER", sRtnBedDan.substring(8));
			jrSetLoc.setField("YD_WBOOK_ID"   ,	ydWbookId); 
				
			JDTORecord jrRcvRtn = this.toLocUpdCrnSch(logId,mthdNm, jrSetLoc, jrCrnSch  );
			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
			
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
        }//end of try~catch			
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
    public JDTORecord toLocDummy(String logId, String mthdNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String mthdNm = "TO위치결정:보조작업[CCoilSchSeEJB.toLocDummy] < " + mthdNms;
    	String sLogMsg = null;
    	JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");

			String ydSchCd 	   	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"  ));			//크레인스케줄코드
			String ydWbookId	= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"  ));			//작업예약
			String schLogYn	    = commUtils.trim(jrWbook.getFieldString("SCHLOG_YN"  ));			//작업예약
			String sDummyUpGp   = commUtils.trim(jrWbook.getFieldString("DUMMY_UP_GP"  ));		    //DUMMY 권상구분
			
			String sStlNo	   	= commUtils.trim(jrCrnSch.getFieldString("STL_NO"));				//크레인작업재료
			String ydEqpId     	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"  ));			//크레인설비ID
			String ydUpWoLoc 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"  ));		
			String ydUpWoLayer 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"  ));		
			String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"  ));		//크레인스케줄ID
			String sModifier    = commUtils.trim(jrCrnSch.getFieldString("MODIFIER"));
			String ydRouteGp    = coilDao.getCoilYdRouteGp(logId,mthdNm,ydSchCd,sStlNo);       //검색조건 행선
			
			String sSchLogContents 	= "";

			/********************************************************
				 * 권하위치 XX 일때 로그테이블에 등록
				 ********************************************************/
//				JDTORecord jrInXXLog 	= commUtils.getParam(logId, mthdNm, sModifier);
//				jrInXXLog.setField("YD_WBOOK_ID" , ydWbookId);
//				jrInXXLog.setField("YD_SCH_CD"   , ydSchCd);
//				jrInXXLog.setField("YD_EQP_ID"   , ydEqpId);	
//				jrInXXLog.setField("STL_NO"      , sStlNo);		
//				jrInXXLog.setField("YD_CRN_SCH_ID"       , ydCrnSchId);
//				jrInXXLog.setField("YD_LOC_ABLECHECK"    , "crnSchToLocDummy"); 
//				jrInXXLog.setField("YD_TO_LOC_DCSN_MTD"  , commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"))); //W:보조작업  S:주작업
			
			if ( "".equals(ydUpWoLoc) ) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, sLogMsg, "SL");
				
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;

			}
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	    	jrParam.setField("STL_NO"      		, sStlNo);		//권상 STOCK
	    	jrParam.setField("YD_SCH_CD"		, ydSchCd);		//스케줄 코드
	    	jrParam.setField("YD_EQP_ID"		, ydEqpId);		//설비ID
	    	jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);	//크레인 스케쥴 ID
	    	jrParam.setField("YD_UP_WO_LOC" 	, ydUpWoLoc);		
			jrParam.setField("YD_UP_WO_LAYER" 	, ydUpWoLayer); 
			jrParam.setField("YD_ROUTE_GP"  	, ydRouteGp);  
			
			sLogMsg =  " TOSQL"+ydCrnSchId+ " 권상재료["+sStlNo +" 의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, sLogMsg, "SL");
	      	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	DUMMY 의 적치가능한 베드 조회
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocDummy
			 -- 대상 위치 SELECT
			WITH TO_LOC_TABLE AS (
			SELECT ABS(TO_NUMBER(SUBSTR(SL.YD_STK_COL_GP,3,2)) - TO_NUMBER(SUBSTR(CC.COIL_YD_STK_COL_GP,3,2)))  AS PRIOR1 --SPAN+열   우선
			     , ABS(TO_NUMBER(SUBSTR(SL.YD_STK_COL_GP,5,2)) - TO_NUMBER(SUBSTR(CC.COIL_YD_STK_COL_GP,5,2)))  AS PRIOR2 --BED       우선
			     , ABS(TO_NUMBER(SL.YD_STK_BED_NO)             - TO_NUMBER(CC.COIL_YD_STK_BED_NO))              AS PRIOR3 --BED       우선
			     , ABS(TO_NUMBER(SL.YD_STK_LYR_NO)             - TO_NUMBER(CC.COIL_YD_STK_LYR_NO))              AS PRIOR4 --LYR       우선
			     , X1.YD_SCH_CD
			     , SL.YD_STK_COL_GP         AS TAG_YD_STK_COL_GP
			     , SL.YD_STK_BED_NO         AS TAG_YD_STK_BED_NO
			     , SL.YD_STK_LYR_NO         AS TAG_YD_STK_LYR_NO
			     , SL.YD_STK_LYR_MTL_STAT   AS TAG_YD_STK_LYR_MTL_STAT
			     -- 열에 따른 코일 간격
			     , SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP)  AS INT
			     , DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
			                                '002', SL.YD_STK_BED_NO)                               AS TAG_LEFT_BED

			     , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_LYR_NO,
			                                '002', LPAD(TO_NUMBER(SL.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_LEFT_LAYER

			     , DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
			                                '002', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'))
			                                                                                       AS TAG_RIGHT_BED
			     , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_LYR_NO,
			                                '002', LPAD(TO_NUMBER(SL.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_RIGHT_LAYER
			     , SC.MATL_SUP_MTD_GP
			     , SC.FROM_W_CHK
			     , SC.TO_W_CHK
			     , SC.COL_OUTDIA_GRP_GP
			     , SC.COL_YD_LOC_GP
			     , SC.COL_W_GP
			     , SC.COL_YD_STK_SKID_GP
			     , CC.STL_NO
			     , CC.COIL_W
			     , CC.COIL_W_GP
			     , CC.COIL_YD_STK_COL_GP
			     , CC.COIL_YD_STK_BED_NO
			     , CC.COIL_YD_STK_LYR_NO
			     , CC.COIL_OUTDIA_GRP_GP
			     , CC.G_PAKAGE_YN
			     -- 적치가능여부 CHECK시 필요 :H/J/ABC/WRAP
			     , CASE --WHEN SUBSTR(X1.YD_SCH_CD,8,1) = 'H' THEN 'H'
			            WHEN X1.SEARCH_LOC = 'H'            THEN 'H'
			            WHEN SC.COL_YD_STK_SKID_GP    = 'F' THEN 'ABC'
			            ELSE 'J' END  AS CHK_SKID_GP
			     , SC.YD_STK_COL_TOLOC_STAT               
			  FROM TB_YD_STKLYR SL
			     , ( SELECT YD_STK_COL_GP
			              , YD_GP
			              , YD_BAY_GP
			              , YD_COIL_OUTDIA_GRP_GP  AS COL_OUTDIA_GRP_GP
			              , MATL_SUP_MTD_GP
			              , (CASE WHEN MATL_SUP_MTD_GP = 'A' THEN 0
			                      WHEN MATL_SUP_MTD_GP = 'B' THEN 1400
			                      ELSE 0                END) AS FROM_W_CHK
			              , (CASE WHEN MATL_SUP_MTD_GP = 'A' THEN 1150
			                      WHEN MATL_SUP_MTD_GP = 'B' THEN 3000
			                      ELSE 3000             END) AS TO_W_CHK
			              , YD_LOC_GP       AS COL_YD_LOC_GP
			              , YD_STK_SKID_GP  AS COL_YD_STK_SKID_GP
			              , YD_STK_COL_W_GP AS COL_W_GP
			              , YD_STK_COL_TOLOC_STAT 
			           FROM TB_YD_STKCOL
			          WHERE DEL_YN = 'N'
			            AND YD_GP  = 'J'

			       ) SC
			     , (
			         SELECT A1.YD_STK_COL_GP  AS COIL_YD_STK_COL_GP
			              , A1.YD_STK_BED_NO  AS COIL_YD_STK_BED_NO
			              , A1.YD_STK_LYR_NO  AS COIL_YD_STK_LYR_NO
			              , A1.STL_NO
			              , CASE WHEN CC.COIL_OUTDIA <= 1280 THEN 'A'
			                     WHEN CC.COIL_OUTDIA >  1930 THEN 'C'
			                     ELSE 'B' END  AS COIL_OUTDIA_GRP_GP
			              , CC.COIL_W
			              , CASE WHEN CC.COIL_W < 1601     THEN 'M' ELSE 'L' END AS COIL_W_GP
			              
			              , (CASE WHEN DECODE(CC.CURR_PROG_CD,'F','XX',(SELECT WRAP_METHOD_CD 
			                                                              FROM TB_PT_OSCOMM 
			                                                             WHERE ORD_NO = CC.ORD_NO
			                                                               AND ORD_DTL = CC.ORD_DTL))='EB'
			                  AND CC.ITEMNAME_CD NOT IN ('HAP','HBP','HCP','HAS','HBS','HCS','HAT','HBT','HCT')
			                  AND CC.ORD_YEOJAE_GP='1'
			                  
			             THEN 'Y' ELSE 'N' END)        AS G_PAKAGE_YN
			           FROM TB_YD_STKLYR A1
			              , TB_YD_STKCOL B1
			              , USRPTA.TB_PT_COILCOMM CC
			          WHERE A1.YD_STK_COL_GP = B1.YD_STK_COL_GP
			            AND A1.STL_NO = CC.COIL_NO
			            AND A1.STL_NO = :V_STL_NO
			            AND A1.YD_STK_LYR_MTL_STAT IN ('C','U')
			            AND SUBSTR(A1.YD_STK_COL_GP,3,2)   BETWEEN '00' AND '99'
			            AND A1.DEL_YN = 'N'
			            AND B1.DEL_YN = 'N'
			       ) CC
			     , (
			         SELECT :V_YD_SCH_CD      AS YD_SCH_CD 
			              , B1.YD_LOC_GP      AS SEARCH_LOC
			           FROM TB_YD_STKLYR A1
			              , TB_YD_STKCOL B1
			          WHERE A1.YD_STK_COL_GP = B1.YD_STK_COL_GP
			            AND A1.STL_NO = :V_STL_NO
			            AND A1.YD_STK_LYR_MTL_STAT IN ('C','U')
			            AND SUBSTR(A1.YD_STK_COL_GP,3,2)   BETWEEN '00' AND '99'
			            AND A1.DEL_YN = 'N'
			            AND B1.DEL_YN = 'N'
			       ) X1
			 WHERE SL.YD_STK_COL_GP       = SC.YD_STK_COL_GP
			   AND SUBSTR(SL.YD_STK_COL_GP,3,2) BETWEEN '00' AND '99'
			   AND SUBSTR(SL.YD_STK_COL_GP,1,1) = SUBSTR(CC.COIL_YD_STK_COL_GP,1,1) --
			   AND SUBSTR(SL.YD_STK_COL_GP,2,1) = SUBSTR(CC.COIL_YD_STK_COL_GP,2,1) --
			   AND SL.YD_STK_LYR_NO       IN ('001','002')
			   AND SL.YD_STK_LYR_ACT_STAT = 'E'
			   AND SL.YD_STK_LYR_MTL_STAT = 'E'
			   AND SL.DEL_YN = 'N'
			   AND SL.YD_STK_BED_NO BETWEEN '00' AND '99'
			   -- 가상위치 제외
			   AND SUBSTR(SL.YD_STK_COL_GP,3,2) NOT IN ('01'  ,'80')
			   -- 결로적치 제외
			   AND SUBSTR(SL.YD_STK_COL_GP,1,4) NOT IN ('JE50','JF47')
			   --권상코일 위치구분과 대상위치의 위치구분이 동일해야함(더미재는 원래위치로)
			   AND SC.COL_YD_LOC_GP =  X1.SEARCH_LOC
			   -- 제품에 TO위치 기준 적용
			   AND 'Y' = CASE WHEN X1.SEARCH_LOC = 'H'            THEN 'Y'
			                  WHEN SC.YD_STK_COL_TOLOC_STAT = 'Y' THEN 'Y' 
			                  ELSE 'N' END   
			-- 동일외경군(소재위치 제외)
			   AND 'Y' = CASE WHEN X1.SEARCH_LOC   = 'H' THEN 'Y'
			                  WHEN X1.SEARCH_LOC   = 'J' AND NVL(SC.COL_OUTDIA_GRP_GP,'*') = CC.COIL_OUTDIA_GRP_GP THEN 'Y'
			                  ELSE 'N' END
			   -- 적치폭구분(소재위치 제외)
			   AND 'Y' = CASE WHEN X1.SEARCH_LOC   = 'H' THEN 'Y'
			                  WHEN X1.SEARCH_LOC   = 'J' AND SC.MATL_SUP_MTD_GP IS NOT NULL THEN
			                       CASE WHEN CC.COIL_W BETWEEN SC.FROM_W_CHK AND SC.TO_W_CHK THEN 'Y'
			                            ELSE 'N' END 
			                  WHEN X1.SEARCH_LOC   = 'J' AND SC.MATL_SUP_MTD_GP IS NULL AND SC.COL_W_GP = CC.COIL_W_GP THEN 'Y'
			                  ELSE 'N' END
			   -- 작업예약 TO위치 가이드 제외
			   AND SL.YD_STK_COL_GP||SL.YD_STK_BED_NO NOT IN ( SELECT NVL(YD_TO_LOC_GUIDE,'1') FROM TB_YD_WRKBOOK WHERE DEL_YN = 'N')
			   -- 동별 야드 설비(SPM,HFL) 위치 제외처리
			   AND SL.YD_STK_COL_GP BETWEEN (
			                            SELECT SUBSTR(YD_STK_COL_GP,1,2)
			                                || CASE WHEN SUBSTR(YD_STK_COL_GP,2,1) = 'A' THEN CASE WHEN SUBSTR(YD_STK_COL_GP,3,2)  >= 50 THEN '50'
			                                                                                       ELSE TRIM(TO_CHAR(TO_NUMBER(SUBSTR(YD_STK_COL_GP,3,2))-15,'09')) END
			                                        WHEN SUBSTR(YD_STK_COL_GP,2,1) = 'B' THEN CASE WHEN SUBSTR(YD_STK_COL_GP,3,2)  >= 24 THEN '24'
			                                                                                       ELSE TRIM(TO_CHAR(TO_NUMBER(SUBSTR(YD_STK_COL_GP,3,2))-15,'09')) END
			                                        WHEN SUBSTR(YD_STK_COL_GP,2,1) = 'C' THEN CASE WHEN SUBSTR(YD_STK_COL_GP,3,2)  >= 25 THEN '25'
			                                                                                       ELSE TRIM(TO_CHAR(TO_NUMBER(SUBSTR(YD_STK_COL_GP,3,2))-15,'09')) END
			                                        WHEN SUBSTR(YD_STK_COL_GP,2,1) = 'E' THEN CASE WHEN SUBSTR(YD_STK_COL_GP,3,2)  >= 44 THEN '44'
			                                                                                       ELSE TRIM(TO_CHAR(TO_NUMBER(SUBSTR(YD_STK_COL_GP,3,2))-15,'09')) END
			                                        WHEN SUBSTR(YD_STK_COL_GP,2,1) = 'G' THEN CASE WHEN SUBSTR(YD_STK_COL_GP,3,2)  >= 45 THEN '45'
			                                                                                       ELSE TRIM(TO_CHAR(TO_NUMBER(SUBSTR(YD_STK_COL_GP,3,2))-15,'09')) END
			                                        WHEN SUBSTR(YD_STK_COL_GP,2,1) = 'H' THEN CASE WHEN SUBSTR(YD_STK_COL_GP,3,2)  >= 47 THEN '47'
			                                                                                       ELSE TRIM(TO_CHAR(TO_NUMBER(SUBSTR(YD_STK_COL_GP,3,2))-15,'09')) END
			                                        ELSE TRIM(TO_CHAR(TO_NUMBER(SUBSTR(YD_STK_COL_GP,3,2))-15,'09'))
			                                        END
			                                 ||'01'
			                              FROM TB_YD_STKLYR
			                             WHERE STL_NO  =  :V_STL_NO
			                               AND YD_STK_LYR_MTL_STAT IN ('C','U')
			                               AND DEL_YN = 'N'
			                            )
			                        AND (    --우측스판
			                            SELECT SUBSTR(YD_STK_COL_GP,1,2)
			                                || CASE WHEN SUBSTR(YD_STK_COL_GP,2,1) = 'A' THEN CASE WHEN SUBSTR(YD_STK_COL_GP,3,2)  <  50 THEN '49'
			                                                                                       ELSE TRIM(TO_CHAR(TO_NUMBER(SUBSTR(YD_STK_COL_GP,3,2))+15,'09')) END
			                                        WHEN SUBSTR(YD_STK_COL_GP,2,1) = 'B' THEN CASE WHEN SUBSTR(YD_STK_COL_GP,3,2)  <  24 THEN '23'
			                                                                                       ELSE TRIM(TO_CHAR(TO_NUMBER(SUBSTR(YD_STK_COL_GP,3,2))+15,'09')) END
			                                        WHEN SUBSTR(YD_STK_COL_GP,2,1) = 'C' THEN CASE WHEN SUBSTR(YD_STK_COL_GP,3,2)  <  25 THEN '25'
			                                                                                       ELSE TRIM(TO_CHAR(TO_NUMBER(SUBSTR(YD_STK_COL_GP,3,2))+15,'09')) END
			                                        WHEN SUBSTR(YD_STK_COL_GP,2,1) = 'E' THEN CASE WHEN SUBSTR(YD_STK_COL_GP,3,2)  <  44 THEN '43'
			                                                                                       ELSE TRIM(TO_CHAR(TO_NUMBER(SUBSTR(YD_STK_COL_GP,3,2))+15,'09')) END
			                                        WHEN SUBSTR(YD_STK_COL_GP,2,1) = 'G' THEN CASE WHEN SUBSTR(YD_STK_COL_GP,3,2)  <  45 THEN '44'
			                                                                                       ELSE TRIM(TO_CHAR(TO_NUMBER(SUBSTR(YD_STK_COL_GP,3,2))+15,'09')) END
			                                        WHEN SUBSTR(YD_STK_COL_GP,2,1) = 'H' THEN CASE WHEN SUBSTR(YD_STK_COL_GP,3,2)  <  47 THEN '46'
			                                                                                       ELSE TRIM(TO_CHAR(TO_NUMBER(SUBSTR(YD_STK_COL_GP,3,2))+15,'09')) END
			                                        ELSE TRIM(TO_CHAR(TO_NUMBER(SUBSTR(YD_STK_COL_GP,3,2))+15,'09'))
			                                        END
			                                || '08'
			                              FROM TB_YD_STKLYR
			                             WHERE STL_NO  =  :V_STL_NO
			                               AND YD_STK_LYR_MTL_STAT IN ('C','U')
			                               AND DEL_YN = 'N'
			                             )
			   --B,C동 지포장 적치 불가 지역 체크   --B동 13스판 3열부터 14스판 5열까지   --C동 13스판 6열부터 15스판 2열까지
			   AND 'Y' = CASE WHEN X1.SEARCH_LOC   = 'H' THEN 'Y'
			                  WHEN G_PAKAGE_YN = 'N' THEN 'Y'
			                  WHEN G_PAKAGE_YN = 'Y' AND SL.YD_STK_COL_GP NOT IN ('JB1205'
			                                                                  ,'JB1301','JB1302','JB1303','JB1304','JB1305','JB1306'
			                                                                  ,'JB1401','JB1402','JB1403','JB1404','JB1405'
			                                                                  ,'JC1205'
			                                                                  ,'JC1301','JC1302','JC1303','JC1304','JC1305','JC1306'
			                                                                  ,'JC1401','JC1402','JC1403','JC1404','JC1405','JC1406'
			                                                                  ,'JC1501','JC1502')
			                  THEN 'Y'
			                  ELSE 'N' END                             
			 )
			, TO_LOC_DATA_TABLE AS (
			-- TO위치 코일정보 SELECT
			SELECT A.PRIOR1
			     , A.PRIOR2
			     , A.PRIOR3
			     , A.PRIOR4
			     , A.STL_NO
			     , NVL(B.YD_EQP_WRK_MODE2,'A') AS YD_EQP_WRK_MODE2
			     , A.YD_SCH_CD
			     , A.TAG_YD_STK_COL_GP
			     , A.TAG_YD_STK_BED_NO
			     , A.TAG_YD_STK_LYR_NO
			     , A.COL_OUTDIA_GRP_GP
			     , A.COL_W_GP
			     , A.COL_YD_LOC_GP
			     , A.COL_YD_STK_SKID_GP
			     , A.COIL_OUTDIA_GRP_GP
			     , A.COIL_W_GP
			     , A.CHK_SKID_GP
			     , A.G_PAKAGE_YN
			     , (SELECT MAX(YD_STK_BED_NO)
			          FROM TB_YD_STKLYR WHERE YD_STK_COL_GP = A.TAG_YD_STK_COL_GP AND ROWNUM = 1) AS COL_MAX_BED_NO
			     , (SELECT YD_STK_LYR_ACT_STAT
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO = A.TAG_LEFT_BED
			           AND B.YD_STK_LYR_NO = A.TAG_LEFT_LAYER
			           AND B.DEL_YN = 'N')   AS TAG_LEFT_ACTIVE_STAT
			     , (SELECT YD_STK_LYR_MTL_STAT
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO = A.TAG_LEFT_BED
			           AND B.YD_STK_LYR_NO = A.TAG_LEFT_LAYER
			           AND B.DEL_YN = 'N')   AS TAG_LEFT_LAYER_STAT
			     , (SELECT STL_NO
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO = A.TAG_LEFT_BED
			           AND B.YD_STK_LYR_NO = A.TAG_LEFT_LAYER
			           AND B.DEL_YN = 'N')   AS TAG_LEFT_STL_NO
			     , A.TAG_RIGHT_BED
			     , A.TAG_RIGHT_LAYER
			     , (SELECT YD_STK_LYR_ACT_STAT
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO   = A.TAG_RIGHT_BED
			           AND B.YD_STK_LYR_NO   = A.TAG_RIGHT_LAYER
			           AND B.DEL_YN = 'N' ) AS  TAG_RIGHT_ACTIVE_STAT
			     , (SELECT YD_STK_LYR_MTL_STAT
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO   = A.TAG_RIGHT_BED
			           AND B.YD_STK_LYR_NO   = A.TAG_RIGHT_LAYER
			           AND B.DEL_YN = 'N' ) AS  TAG_RIGHT_LAYER_STAT
			     , (SELECT STL_NO
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO   = A.TAG_RIGHT_BED
			           AND B.YD_STK_LYR_NO   = A.TAG_RIGHT_LAYER
			           AND B.DEL_YN = 'N' ) AS  TAG_RIGHT_STL_NO
			     , (SELECT STL_NO
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
			           AND B.YD_STK_LYR_NO   = '002'
			           AND B.DEL_YN = 'N') AS TAG_2DAN_LEFT_STL_NO
			     , (SELECT STL_NO
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO   = A.TAG_YD_STK_BED_NO
			           AND B.YD_STK_LYR_NO   = '002'
			           AND B.DEL_YN = 'N') AS TAG_2DAN_RIGHT_STL_NO
			  FROM TO_LOC_TABLE A
			--     , (SELECT 'A' AS YD_EQP_WRK_MODE2 FROM TB_YD_EQP WHERE YD_EQP_ID = :V_YD_EQP_ID) B
			     , (SELECT DECODE(B.ITEM1, 'Y', 'A', 'M') AS YD_EQP_WRK_MODE2
			          FROM TB_YD_EQP A
			             , TB_YD_RULE B
			         WHERE A.YD_EQP_ID = B.CD_GP
			           AND B.REPR_CD_GP = 'APP100'
			           AND A.YD_EQP_ID = :V_YD_EQP_ID) B
			), TO_LOC_DATA_COMP_TABLE AS (
			--*--*--*--*--*--*-- 적치가능위치
			SELECT *
			  FROM (
			        -- 배차 편성 4대분 제외
			        SELECT CASE WHEN ( SELECT COUNT(*)
			                             FROM (
			                                    SELECT ROW_NUMBER() OVER(PARTITION BY CS.YD_CARLD_STOP_LOC ORDER BY CS.YD_BAYIN_WO_SEQ, CS.YD_CAR_SCH_ID) AS RN
			                                         , CS.*
			                                      FROM TB_YD_CARSCH CS
			                                     WHERE CS.DEL_YN = 'N'
			                                       AND CS.YD_CARLD_STOP_LOC  LIKE 'J%'
			                                   ) CS
			                                , TB_YD_STOCK    ST
			                            WHERE CS.RN < 5
			                              AND CS.YD_CARLD_STOP_LOC  LIKE 'J'|| YD_SCH_CD|| '%'
			                              AND CS.TRANS_ORD_DATE  = ST.TRANS_ORD_DATE
			                              AND CS.TRANS_ORD_SEQNO = ST.TRANS_ORD_SEQNO 
			                              AND ST.STL_NO = NVL(L_COIL_NO,'-') 
			                              AND ROWNUM <= 1
			                         ) > 0  AND L_PROG_CD IN ('6','L') AND TAG_YD_STK_LYR_NO = '002' THEN 1 ELSE 0 END 
			                      AS L_CAR_YN        
			             , CASE WHEN ( SELECT COUNT(*)
			                             FROM (
			                                    SELECT ROW_NUMBER() OVER(PARTITION BY CS.YD_CARLD_STOP_LOC ORDER BY CS.YD_BAYIN_WO_SEQ, CS.YD_CAR_SCH_ID) AS RN
			                                         , CS.*
			                                      FROM TB_YD_CARSCH CS
			                                     WHERE CS.DEL_YN = 'N'
			                                       AND CS.YD_CARLD_STOP_LOC  LIKE 'J%'
			                                   ) CS
			                                , TB_YD_STOCK    ST
			                            WHERE CS.RN < 5
			                              AND CS.YD_CARLD_STOP_LOC  LIKE 'J'|| YD_SCH_CD|| '%'
			                              AND CS.TRANS_ORD_DATE  = ST.TRANS_ORD_DATE
			                              AND CS.TRANS_ORD_SEQNO = ST.TRANS_ORD_SEQNO 
			                              AND ST.STL_NO = NVL(R_COIL_NO,'-') 
			                              AND ROWNUM <= 1
			                         ) > 0  AND R_PROG_CD IN ('6','L') AND TAG_YD_STK_LYR_NO = '002' THEN 1 ELSE 0 END 
			                      AS R_CAR_YN 
			             , CASE WHEN (  SELECT COUNT(*)
			                              FROM TB_YD_STOCK A
			                                 , TB_YD_CARSCH B
			                             WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
			                               AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
			                               AND B.DEL_YN = 'N'
			                               AND A.STL_NO = NVL(L_COIL_NO,'-')
			                               AND ROWNUM <= 1
			                         ) > 0 AND TAG_YD_STK_LYR_NO = '002' THEN 1 ELSE 0 END  
			                      AS L_CAR_CHK
			             , CASE WHEN (  SELECT COUNT(*)
			                              FROM TB_YD_STOCK A
			                                 , TB_YD_CARSCH B
			                             WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
			                               AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
			                               AND B.DEL_YN = 'N'
			                               AND A.STL_NO = NVL(R_COIL_NO,'-')
			                               AND ROWNUM <= 1
			                         ) > 0 AND TAG_YD_STK_LYR_NO = '002' THEN 1 ELSE 0 END  
			                      AS R_CAR_CHK  
			             , KK.*
			          FROM
			               (
			                SELECT K.*
			                     , CASE WHEN TAG_LEFT_STL_NO  IS NOT NULL AND TAG_RIGHT_STL_NO IS NOT NULL THEN '2'
			                            WHEN TAG_LEFT_STL_NO  IS NOT NULL                                  THEN '2'
			                            WHEN TAG_RIGHT_STL_NO IS NOT NULL                                  THEN '2'
			                            ELSE '3' END --공BED
			                       AS PRIOR5
			                     , C.COIL_NO      AS C_COIL_NO
			                     , C.COIL_T       AS C_THICK
			                     , C.COIL_W       AS C_WIDTH
			                     , C.COIL_WT      AS C_WEIGTH
			                     , C.COIL_OUTDIA  AS C_OUTDIA
			                     , C.CURR_PROG_CD AS C_PROG_CD
			                     , C.ORD_NO       AS C_ORD_NO     -- 주문번호
			                     , C.ORD_DTL      AS C_ORD_DTL    -- 주문행번
			                     , C.DEMANDER_CD  AS C_DEMANDER_CD-- 수요가코드
			                     , (CASE WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
			                             WHEN C.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN C.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN C.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN C.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN C.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             ELSE '' END
			                       ) AS C_YEOJAE_CAUSE_CD
			                     , TRUNC((SYSDATE - C.HRMILL_CMPL_DT )*24)             AS C_HOT_COIL_MIN
			                     , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
			                            WHEN C.COIL_OUTDIA >  1930 THEN 'C'
			                            ELSE 'B' END  AS C_COIL_OUTDIA_GP
			                     , CASE WHEN C.COIL_W < 1601     THEN 'M' ELSE 'L' END AS C_COIL_W_GP
			                     , L.COIL_NO      AS L_COIL_NO
			                     , L.COIL_T       AS L_THICK
			                     , L.COIL_W       AS L_WIDTH
			                     , L.COIL_WT      AS L_WEIGTH
			                     , L.COIL_OUTDIA  AS L_OUTDIA
			                     , L.CURR_PROG_CD AS L_PROG_CD
			                     , L.ORD_NO       AS L_ORD_NO     -- 주문번호
			                     , L.ORD_DTL      AS L_ORD_DTL    -- 주문행번
			                     , L.DEMANDER_CD  AS L_DEMANDER_CD-- 수요가코드
			                     , (CASE WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
			                             WHEN L.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN L.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN L.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN L.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN L.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             ELSE '' END
			                       ) AS L_YEOJAE_CAUSE_CD
			                     , TRUNC((SYSDATE - L.HRMILL_CMPL_DT )*24) AS L_HOT_COIL_MIN
			                     , R.COIL_NO      AS R_COIL_NO
			                     , R.COIL_W       AS R_WIDTH
			                     , R.COIL_T       AS R_THICK
			                     , R.COIL_WT      AS R_WEIGTH
			                     , R.COIL_OUTDIA  AS R_OUTDIA
			                     , R.CURR_PROG_CD AS R_PROG_CD
			                     , R.ORD_NO       AS R_ORD_NO     -- 주문번호
			                     , R.ORD_DTL      AS R_ORD_DTL    -- 주문행번
			                     , R.DEMANDER_CD  AS R_DEMANDER_CD-- 수요가코드
			                     , (CASE WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
			                             WHEN R.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN R.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN R.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN R.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN R.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             ELSE '' END
			                       ) AS R_YEOJAE_CAUSE_CD
			                     , TRUNC((SYSDATE - R.HRMILL_CMPL_DT )*24) R_HOT_COIL_MIN
			                     , L.ISHOT        AS L_ISHOT
			                     , R.ISHOT        AS R_ISHOT
			                     , C.ISHOT        AS C_ISHOT
			                  FROM TO_LOC_DATA_TABLE K
			                     , (SELECT 1 T_ROW, A.*
			                             ,    CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 96
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 48
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                  END  AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
			                          FROM USRPTA.TB_PT_COILCOMM  A ) C  --대상코일
			                     , (SELECT 1 T_ROW, A.*
			                          ,
			                                  CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 96
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 48
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                  END  AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
			                          FROM USRPTA.TB_PT_COILCOMM  A ) L  --하단LEFT
			                     , (SELECT 1 T_ROW, A.*
			                             ,
			                                          CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
			                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
			                                                    THEN 'TRUE'
			                                                    ELSE 'FALSE'
			                                               END
			                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
			                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 96
			                                                    THEN 'TRUE'
			                                                    ELSE 'FALSE'
			                                               END
			                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
			                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
			                                                    THEN 'TRUE'
			                                                    ELSE 'FALSE'
			                                               END
			                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
			                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 48
			                                                    THEN 'TRUE'
			                                                    ELSE 'FALSE'
			                                               END
			                                          END  AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
			                          FROM USRPTA.TB_PT_COILCOMM  A ) R  --하단RIGHT
			                 WHERE K.STL_NO           = C.COIL_NO(+)
			                   AND K.TAG_LEFT_STL_NO  = L.COIL_NO(+)
			                   AND K.TAG_RIGHT_STL_NO = R.COIL_NO(+)
			                   --짱구코일 2단제외
			--                   AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND JJANG_GP = 'Y'                      THEN 0
			--                                ELSE 1 END
			                   --2단일 경우 1단에 핫코일이 존재 시 2단제의
			--                           AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND  C.ISHOT ='TRUE' AND L.ISHOT ='TRUE' AND R.ISHOT='TRUE' THEN 1
			--                                        WHEN TAG_YD_STK_LYR_NO = '002' AND (L.ISHOT ='TRUE' OR R.ISHOT='TRUE') THEN 0
			--                                        ELSE 1 END
			                   AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND C.ISHOT ='TRUE' THEN CASE WHEN L.ISHOT ='TRUE' AND R.ISHOT='TRUE' THEN 1
			                                                                                             ELSE 0 END    
			                                ELSE 1 END
			--                   --2단일 경우 좌우 적치 상태 CHECK
			                   AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002'
			                                     AND (TAG_LEFT_ACTIVE_STAT != 'E' OR TAG_RIGHT_ACTIVE_STAT != 'E'
			                                       OR TAG_LEFT_LAYER_STAT  != 'C' OR TAG_RIGHT_LAYER_STAT  != 'C') THEN 0
			                                ELSE 1 END
			--                   --1단일 경우 좌우 상단 적치 상태 CHECK
			                   AND 1 = CASE WHEN K.TAG_YD_STK_LYR_NO = '001' AND (TAG_2DAN_LEFT_STL_NO IS NOT NULL OR TAG_2DAN_RIGHT_STL_NO IS NOT NULL) THEN 0
			                                ELSE 1 END
			               ) KK
			      )
			-- 황주임님 하단에 'L','6'제외요청        
			 WHERE L_CAR_YN + R_CAR_YN = 0 
			)
			--*--*--*--*--*--*-- 평점
			SELECT G.*
			     , CASE  -- 1단이면서 소재인 경우
			            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
			            THEN CASE WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌우) 진도코드
			                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌단) 진도코드
			                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우단) 진도코드
			                      WHEN L_COIL_NO  IS NULL     AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
			                      WHEN R_COIL_NO  IS NULL     AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED
			                      ELSE '7' END
			             -- 2단이면서 소재인 경우
			            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
			            THEN CASE WHEN L_WB        = 'Y'      OR  R_WB       = 'Y'      THEN '9' --좌하단우하단 작업 예약
			                      WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌하단우하단) 진도코드
			                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌하단) 진도코드
			                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우하단) 진도코드
			                      ELSE '2' END
			            -- 1단이면서 제품인 경우
			            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8')
			            THEN CASE WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
			                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
			                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
			                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우축 동일 고객사+주문번호행번

			                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
			                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우측 동일 주문번호행번

			                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌측   동일 주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우측   동일 주문번호행번
			                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우측 동일 주문번호
			                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우측   동일 주문번호
			                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌측   동일 주문번호
			                      WHEN L_COIL_NO IS NULL      AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
			                      WHEN R_COIL_NO IS NULL      AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED

			                      ELSE '7' END
			            -- 2단이면서 제품인 경우
			            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8') --제품
			            THEN CASE WHEN L_WB = 'Y'             OR  R_WB  = 'Y'           THEN '9' --좌하단우하단 작업 예약
			                      WHEN L_PROG_CD IN ('6','L') OR  R_PROG_CD IN ('6','L')THEN '8' --상차지시
			                      WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
			                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
			                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
			                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 고객사+주문번호행번

			                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
			                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 주문번호행번

			                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌하단   동일 주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우하단   동일 주문번호행번
			                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우하단 동일 주문번호
			                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우하단   동일 주문번호
			                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌하단   동일 주문번호
			                      ELSE '2' END

			             ELSE '7' END GRADE
			  FROM (SELECT A.*
			             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
			                  FROM TB_YD_WRKBOOK    A1
			                     , TB_YD_WRKBOOKMTL B1
			                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			                   AND A1.DEL_YN      = 'N'
			                   AND B1.DEL_YN      = 'N'
			                   AND B1.STL_NO      = A.C_COIL_NO
			               ) C_WB
			             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
			                  FROM TB_YD_WRKBOOK    A1
			                     , TB_YD_WRKBOOKMTL B1
			                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			                   AND A1.DEL_YN      = 'N'
			                   AND B1.DEL_YN      = 'N'
			                   AND B1.STL_NO      = A.L_COIL_NO
			               ) L_WB
			             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
			                  FROM TB_YD_WRKBOOK    A1
			                     , TB_YD_WRKBOOKMTL B1
			                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			                   AND A1.DEL_YN      = 'N'
			                   AND B1.DEL_YN      = 'N'
			                   AND B1.STL_NO      = A.R_COIL_NO
			               ) R_WB
			          FROM TO_LOC_DATA_COMP_TABLE A
			       ) G
			 ORDER BY GRADE
			        , L_CAR_CHK + R_CAR_CHK
			        , PRIOR1
			        , PRIOR2
			        , PRIOR3
			        , PRIOR4 DESC
			*/
			
			JDTORecordSet jsDummySearch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocDummy", logId, mthdNm, "동일한 적치가능한 베드 조회");
			if (jsDummySearch.size() <= 0) {
				sLogMsg = "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, sLogMsg, "SL");
			
//LOG_TABLE 
				if ("Y".equals(schLogYn)) {
					sSchLogContents = "[getYdToLocDummy]대상코일위치검색실패:"+ sStlNo+" LOG :"+"\r\n";
					this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
				}
				
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			}

	    	//----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 TO위치 상수값 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrRuleConst = this.toLocRuleConst(logId, mthdNm);

			//----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 LOGIC 적용여부 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrLocRuleApply = this.toLocRuleApply(logId, mthdNm);
			commUtils.printParam(logId, jrLocRuleApply);
			
	    	String ydStkColGp 	 = "";
			String ydStkBedNo 	 = "";
			String ydStkLyrNo 	 = "";	
			String sRtnBedDan 	 = "";	
			String sLocAbleRtn 	 = "";
			String sLocAbleRtnMsg = "";
			String sGrade 	     = "";	
			String sDBLogMsg	 = "";
			JDTORecord jrDummySearch = JDTORecordFactory.getInstance().create();
			JDTORecord jrLocAbleRtn  = JDTORecordFactory.getInstance().create();
			for (int i = 1; i <= jsDummySearch.size(); i++) {

				jsDummySearch.absolute(i);
				jrDummySearch  = jsDummySearch.getRecord();
				
				sGrade 		= commUtils.nvl (jrDummySearch.getFieldString("GRADE"),"9");
				ydStkColGp 	= commUtils.trim(jrDummySearch.getFieldString("TAG_YD_STK_COL_GP"  ));
				ydStkBedNo 	= commUtils.trim(jrDummySearch.getFieldString("TAG_YD_STK_BED_NO"  ));
				ydStkLyrNo 	= commUtils.trim(jrDummySearch.getFieldString("TAG_YD_STK_LYR_NO"  ));	
				
				commUtils.printParam(logId, jrDummySearch);
				/**************************************
				 *         적치 가능 check
		   		 **************************************/			
				jrLocAbleRtn = this.toLocAbleCheck(logId, mthdNm, jrDummySearch, jrRuleConst, jrLocRuleApply);

				sLocAbleRtn    = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
				sLocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
				if (sLocAbleRtnMsg.length() > 0 ) {
					sDBLogMsg = sDBLogMsg + sLocAbleRtnMsg +"\r\n";
				}
				
				
				if ("1".equals(sLocAbleRtn)) {
					sLogMsg = ydStkColGp+ydStkBedNo+ ydStkLyrNo+"  적치가능 위치평점:"+ sGrade;
					commUtils.printLog(logId, sLogMsg, "SL");				
				    //적치가능 
					sRtnBedDan = ydStkColGp + ydStkBedNo + ydStkLyrNo;
	    			break;
				}
			}	
						
			String sAPP901 = coilDao.ApplyYn(logId, mthdNm, "APP901", "J", "*");
			commUtils.printLog(logId, "TO위치 RULE상수[신sAPP901] : " + sAPP901, "SL");
			
    		if ("Y".equals(sAPP901)) {
    			/***************************************************
				** 신 로직
				** XX이면서 소재인경우  폭및 외경구분 안하는 1단을 다시 검색
				****************************************************/	    			
    			if (sRtnBedDan.length() < 10) {
    				
 					if ("H".equals(ydSchCd.substring(7, 8))) {
 						commUtils.printLog(logId, "▼▼▼▼▼▼▼▼▼▼ 소재 1단  재검색 ▼▼▼▼▼▼▼▼▼▼", "SL");
 						
 						for (int j = 1; j <= jsDummySearch.size(); j++) {

 							jsDummySearch.absolute(j);
 							jrDummySearch  = jsDummySearch.getRecord();
 							
 							sGrade 		= commUtils.nvl (jrDummySearch.getFieldString("GRADE"),"9");
 							ydStkColGp 	= commUtils.trim(jrDummySearch.getFieldString("TAG_YD_STK_COL_GP"  ));
 							ydStkBedNo 	= commUtils.trim(jrDummySearch.getFieldString("TAG_YD_STK_BED_NO"  ));
 							ydStkLyrNo 	= commUtils.trim(jrDummySearch.getFieldString("TAG_YD_STK_LYR_NO"  ));	
 							
 							if ("001".equals(ydStkLyrNo)) { 
 								/**************************************
 								 *         적치 가능 check
 						   		 **************************************/			
 								//소재 1단일 경우 재검색 
 								jrLocRuleApply.setField("WID_DIA_CHK_YN", "N");	
 								JDTORecord jrLocAbleRtn1 = this.toLocAbleCheck(logId, mthdNm, jrDummySearch, jrRuleConst, jrLocRuleApply);
 			
 								sLocAbleRtn    = commUtils.trim(jrLocAbleRtn1.getFieldString("LOC_ABLE_RTN")) ;
 								sLocAbleRtnMsg = commUtils.trim(jrLocAbleRtn1.getFieldString("LOC_ABLE_CONTENTS")) ;
 								if (sLocAbleRtnMsg.length() > 0 ) {
 									sDBLogMsg = sDBLogMsg + sLocAbleRtnMsg +"\r\n";
 								}
 								
 								if ("1".equals(sLocAbleRtn)) {
 									sLogMsg = ydStkColGp+ydStkBedNo+ ydStkLyrNo+"  적치가능 위치평점:"+ sGrade;
 									commUtils.printLog(logId, sLogMsg, "SL");				
 								    //적치가능 
 									sRtnBedDan = ydStkColGp + ydStkBedNo + ydStkLyrNo;
 			 						commUtils.printLog(logId, "▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲", "SL");

 					    			break;
 								}
 							}
 						} //for
 						commUtils.printLog(logId, "▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲", "SL");

 						if (sRtnBedDan.length() < 10) {
 		 					sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 Dummy TO위치 결정 실패2 ";
 		 					commUtils.printLog(logId, sLogMsg, "SL");
 		 					if ("Y".equals(schLogYn)) {
 								sSchLogContents = "[getYdToLocDummy]대상코일선택실패:"+ sStlNo+" LOG :" +"\r\n" +  sDBLogMsg;
 								this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );	
 								
 							} 		 					
								
 		 					jrRtn.setField("RTN_CD" , "0");	
		 					jrRtn.setField("RTN_MSG", sLogMsg);
		 					return jrRtn;
 						}	
 					} else {
    				
 						/***************************************************
 	    				** 제품인 경우
 	    				****************************************************/		    				
	 					sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 Dummy TO위치 결정 실패 ";
	 					commUtils.printLog(logId, sLogMsg, "SL");
	 				
	 					if ("Y".equals(schLogYn)) {
	 						sSchLogContents = "[getYdToLocDummy]사용자 지정 대상코일선택실패:"+ sStlNo+" LOG :"+"\r\n" + sDBLogMsg;
	 						this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		

	 					}

	 					jrRtn.setField("RTN_CD" , "0");	
	 					jrRtn.setField("RTN_MSG", sLogMsg);
	 					return jrRtn;
 					}	
 				}	
    		} else {
				if (sRtnBedDan.length() < 10) {
					sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 TO위치 결정 실패 ";
					commUtils.printLog(logId, sLogMsg, "SL");
					
					if ("Y".equals(schLogYn)) {
						sSchLogContents = "[getYdToLocDummy]대상코일선택실패:"+ sStlNo+" LOG :" +"\r\n" +  sDBLogMsg;
						this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );	
						
					}
										
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", sLogMsg);
					return jrRtn;				
				}
    		}	
			if ("Y".equals(schLogYn)) {
				sSchLogContents = "[getYdToLocDummy]대상코일선택:"+ sStlNo+"  선택위치:"+ sRtnBedDan + "  평점:"+ sGrade +" LOG :" +"\r\n"+ sDBLogMsg;
				this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
				
			}
			
			
			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = commUtils.getParam(logId, mthdNm, sModifier); 
			jrSetLoc.setField("YD_CRN_SCH_ID", 	ydCrnSchId); 
			jrSetLoc.setField("YD_EQP_ID", 		ydEqpId);	 
			jrSetLoc.setField("YD_SCH_CD", 		ydSchCd);	 
			jrSetLoc.setField("YD_UP_WO_LOC", 	ydUpWoLoc); 
			jrSetLoc.setField("YD_UP_WO_LAYER",	ydUpWoLayer);	 
			jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER", sRtnBedDan.substring(8));
			jrSetLoc.setField("YD_WBOOK_ID", 	ydWbookId); 
				
			JDTORecord jrRcvRtn = this.toLocUpdCrnSch(logId,mthdNm, jrSetLoc, jrCrnSch  );
			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
			
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;							
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
	    }//end of try~catch				
	}    
 

    
	/**
	 * 소재 결로 보급 사용자 지정작업
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord toLocUserHC(String logId, String mthdNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String mthdNm = "TO위치결정:소재 결로보급 사용자 지정작업[CCoilSchSeEJB.toLocUserHC] < " + mthdNms;
    	String sLogMsg = null;
    	JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
		
			commUtils.printLog(logId, mthdNm, "S+");

			String ydSchCd 	   	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));		//크레인스케줄코드
			String ydWookId		= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));	//작업예약
			String ydToLocGuide	= commUtils.trim(jrWbook.getFieldString("YD_TO_LOC_GUIDE")); //작업예약
			String schLogYn	    = commUtils.trim(jrWbook.getFieldString("SCHLOG_YN"  ));			//작업예약
			
			String sStlNo	   	= commUtils.trim(jrCrnSch.getFieldString("STL_NO"));			//크레인작업재료
			String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
			String ydEqpId     	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
			String ydUpWoLoc 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
			String ydUpWoLayer 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));
			String sModifier    = commUtils.trim(jrCrnSch.getFieldString("MODIFIER"));
			
			String sSchLogContents 	= "";
			if ( "".equals(ydUpWoLoc) ) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			}
			

			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			JDTORecord jrParam  = commUtils.getParam(logId, mthdNm, sModifier); 
			jrParam.setField("STL_NO"			, sStlNo);											//권상 STOCK
			jrParam.setField("YD_TO_LOC_GUIDE"	, ydToLocGuide);									//가이드
			jrParam.setField("YD_SCH_CD"		, ydSchCd);											//스케쥴 코드
			jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);										//크레인 작업지시 ID
			jrParam.setField("YD_EQP_ID"		, ydEqpId);											//설비 번호
			jrParam.setField("YD_UP_WO_LOC" 	, ydUpWoLoc);  
			jrParam.setField("YD_UP_WO_LAYER" 	, ydUpWoLayer); 
			
			sLogMsg =  " TOSQL"+ydCrnSchId+ " 권상재료["+sStlNo +" +스케쥴 코드 : "+ ydSchCd + "의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, sLogMsg, "SL");
	      	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	사용자 지정한 위치만 검색
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocUserHC 
			 -- 결로대상 위치 SELECT
			WITH TO_LOC_TABLE AS (
			SELECT ABS(TO_NUMBER(SL.YD_STK_BED_NO)  - TO_NUMBER(C1.COIL_YD_STK_BED_NO)) AS PRIOR3 --BED우선
			   , ABS(TO_NUMBER(SL.YD_STK_LYR_NO)  - TO_NUMBER(C1.COIL_YD_STK_LYR_NO)) AS PRIOR4 --LYR우선
			   , X1.YD_SCH_CD
			   , SL.YD_STK_COL_GP     AS TAG_YD_STK_COL_GP
			   , SL.YD_STK_BED_NO     AS TAG_YD_STK_BED_NO
			   , SL.YD_STK_LYR_NO     AS TAG_YD_STK_LYR_NO
			   , SL.YD_STK_LYR_MTL_STAT AS TAG_YD_STK_LYR_MTL_STAT
			   -- 열에 따른 코일 간격
			   , SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP)  AS INT
			   , DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
			                              '002', SL.YD_STK_BED_NO)                 AS TAG_LEFT_BED

			   , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_LYR_NO,
			                              '002', LPAD(TO_NUMBER(SL.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_LEFT_LAYER

			   , DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
			                              '002', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'))
			                                             AS TAG_RIGHT_BED
			   , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_LYR_NO,
			                              '002', LPAD(TO_NUMBER(SL.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_RIGHT_LAYER

			   , SC.COL_OUTDIA_GRP_GP
			   , SC.COL_W_GP
			   , SC.COL_YD_LOC_GP
			     , SC.COL_YD_STK_SKID_GP
			   , C1.STL_NO
			   , C1.COIL_W
			   , C1.COIL_W_GP
			   , C1.COIL_YD_STK_COL_GP
			   , C1.COIL_YD_STK_BED_NO
			   , C1.COIL_YD_STK_LYR_NO
			   , C1.COIL_OUTDIA_GRP_GP
			   -- 적치가능여부 CHECK시 필요 :H/J/ABC/WRAP
			   , 'H'  AS CHK_SKID_GP
			  FROM TB_YD_STKLYR SL
			   , ( SELECT YD_STK_COL_GP
			        , YD_GP
			        , YD_BAY_GP
			        , YD_EQP_GP
			        --SKID 외경구분
			        , YD_COIL_OUTDIA_GRP_GP  AS COL_OUTDIA_GRP_GP
			        -- 소재보급방법
			        , YD_LOC_GP   AS COL_YD_LOC_GP
			        , YD_STK_SKID_GP  AS COL_YD_STK_SKID_GP
			        , YD_STK_COL_W_GP AS COL_W_GP
			       FROM TB_YD_STKCOL
			      WHERE DEL_YN = 'N'
			      AND YD_GP  = 'J'
			     ) SC
			   , (
			     SELECT A1.YD_STK_COL_GP  AS COIL_YD_STK_COL_GP
			        , A1.YD_STK_BED_NO  AS COIL_YD_STK_BED_NO
			        , A1.YD_STK_LYR_NO  AS COIL_YD_STK_LYR_NO
			        , A1.STL_NO
			        , CASE WHEN CC.COIL_OUTDIA <= 1280 THEN 'A'
			           WHEN CC.COIL_OUTDIA >  1930 THEN 'C'
			           ELSE 'B' END  AS COIL_OUTDIA_GRP_GP
			              , CASE WHEN CC.COIL_W < 1601 THEN 'M'
			                     ELSE 'L' END
			                AS COIL_W_GP
			        , CC.COIL_W
			       FROM TB_YD_STKLYR A1
			        , TB_YD_STKCOL B1
			        , TB_PT_COILCOMM CC
			      WHERE A1.YD_STK_COL_GP = B1.YD_STK_COL_GP
			      AND A1.STL_NO = CC.COIL_NO
			      AND A1.STL_NO = :V_STL_NO
			      AND A1.YD_STK_LYR_MTL_STAT IN ('C','U')
			      AND SUBSTR(A1.YD_STK_COL_GP,3,2)   BETWEEN '00' AND '99'
			      AND A1.DEL_YN = 'N'
			      AND B1.DEL_YN = 'N'
			     ) C1
			   , (
			     SELECT :V_YD_SCH_CD        AS YD_SCH_CD
			          , NVL(:V_YD_TO_LOC_GUIDE,'XXXX')  AS YD_TO_LOC_GUIDE
			       FROM DUAL
			     ) X1
			 WHERE SL.YD_STK_COL_GP     = SC.YD_STK_COL_GP
			   AND SL.YD_STK_COL_GP     LIKE SUBSTR(X1.YD_TO_LOC_GUIDE,1,4) || '%' --가이드 열
			   AND SUBSTR(SL.YD_STK_COL_GP,1,1) = SUBSTR(C1.COIL_YD_STK_COL_GP,1,1) --
			   AND SUBSTR(SL.YD_STK_COL_GP,2,1) = SUBSTR(C1.COIL_YD_STK_COL_GP,2,1) --
			   AND SC.YD_EQP_GP           BETWEEN '01' AND '99'
			   AND SL.YD_STK_LYR_NO     IN ('001','002')
			   AND SL.YD_STK_LYR_MTL_STAT = 'E'
			   AND SL.YD_STK_LYR_ACT_STAT = DECODE(SUBSTR(X1.YD_SCH_CD, 7, 2)
			                  , 'LM', 'S'  -- 보급
			                  , 'UM', 'E') -- 추출
			   AND SL.DEL_YN = 'N'
			   -- 크레인 작업지시 제외
			   -- AND SL.YD_STK_COL_GP||SL.YD_STK_BED_NO||SL.YD_STK_LYR_NO NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YD_CRNSCH WHERE DEL_YN = 'N')
			   -- 작업예약 TO위치 가이드 제외
			   AND SL.YD_STK_COL_GP||SL.YD_STK_BED_NO NOT IN ( SELECT NVL(YD_TO_LOC_GUIDE,'1') FROM TB_YD_WRKBOOK WHERE DEL_YN = 'N')
			 )
			, TO_LOC_DATA_TABLE AS (
			-- TO위치 코일정보 SELECT
			SELECT A.PRIOR3
			   , A.PRIOR4
			   , A.STL_NO
			   , A.YD_SCH_CD
			   , B.YD_EQP_WRK_MODE2
			   , A.TAG_YD_STK_COL_GP
			   , A.TAG_YD_STK_BED_NO
			   , A.TAG_YD_STK_LYR_NO
			   , A.TAG_LEFT_BED
			   , A.TAG_LEFT_LAYER
			   , A.COL_OUTDIA_GRP_GP
			   , A.COL_YD_LOC_GP
			   , A.COL_W_GP
			   , A.COIL_OUTDIA_GRP_GP
			     , A.COIL_W_GP
			   , A.CHK_SKID_GP
			   , A.COL_YD_STK_SKID_GP
			   , (SELECT YD_STK_LYR_ACT_STAT
			      FROM TB_YD_STKLYR B
			     WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			       AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
			       AND B.YD_STK_LYR_NO   = A.TAG_LEFT_LAYER
			       AND B.DEL_YN = 'N' ) AS TAG_LEFT_ACTIVE_STAT
			   , (SELECT YD_STK_LYR_MTL_STAT
			      FROM TB_YD_STKLYR B
			     WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			       AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
			       AND B.YD_STK_LYR_NO   = A.TAG_LEFT_LAYER
			       AND B.DEL_YN = 'N' ) AS TAG_LEFT_LAYER_STAT
			   , (SELECT STL_NO
			      FROM TB_YD_STKLYR B
			     WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			       AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
			       AND B.YD_STK_LYR_NO   = A.TAG_LEFT_LAYER
			       AND B.DEL_YN = 'N' ) AS TAG_LEFT_STL_NO
			   , A.TAG_RIGHT_BED
			   , A.TAG_RIGHT_LAYER
			   , (SELECT YD_STK_LYR_ACT_STAT
			      FROM TB_YD_STKLYR B
			     WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			       AND B.YD_STK_BED_NO   = A.TAG_RIGHT_BED
			       AND B.YD_STK_LYR_NO   = A.TAG_RIGHT_LAYER
			       AND B.DEL_YN = 'N' ) AS TAG_RIGHT_ACTIVE_STAT
			   , (SELECT YD_STK_LYR_MTL_STAT
			      FROM TB_YD_STKLYR B
			     WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			       AND B.YD_STK_BED_NO   = A.TAG_RIGHT_BED
			       AND B.YD_STK_LYR_NO   = A.TAG_RIGHT_LAYER
			       AND B.DEL_YN = 'N' ) AS TAG_RIGHT_LAYER_STAT
			   , (SELECT STL_NO
			      FROM TB_YD_STKLYR B
			     WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			       AND B.YD_STK_BED_NO   = A.TAG_RIGHT_BED
			       AND B.YD_STK_LYR_NO   = A.TAG_RIGHT_LAYER
			       AND B.DEL_YN = 'N' ) AS  TAG_RIGHT_STL_NO
			   , (SELECT STL_NO
			      FROM TB_YD_STKLYR B
			     WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			       AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
			       AND B.YD_STK_LYR_NO   = '002'
			       AND B.DEL_YN = 'N' ) AS TAG_2DAN_LEFT_STL_NO
			   , (SELECT STL_NO
			      FROM TB_YD_STKLYR B
			     WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			       AND B.YD_STK_BED_NO   = A.TAG_YD_STK_BED_NO
			       AND B.YD_STK_LYR_NO   = '002'
			       AND B.DEL_YN = 'N' ) AS TAG_2DAN_RIGHT_STL_NO
			  FROM TO_LOC_TABLE A
			     , (SELECT YD_EQP_WRK_MODE2 FROM TB_YD_EQP WHERE YD_EQP_ID = :V_YD_EQP_ID) B
			), TO_LOC_DATA_COMP_TABLE AS (
			--*--*--*--*--*--*-- 적치가능위치
			SELECT *
			  FROM (
			    SELECT (SELECT COUNT(*)
			          FROM TB_YD_STOCK A
			           , TB_YD_CARSCH B
			         WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
			           AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
			           AND B.DEL_YN = 'N'
			           AND A.STL_NO = NVL(L_COIL_NO,'-')
			           AND ROWNUM <= 1) AS L_CAR_CHK
			       , (SELECT COUNT(*)
			          FROM TB_YD_STOCK A
			           , TB_YD_CARSCH B
			         WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
			           AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
			           AND B.DEL_YN = 'N'
			           AND A.STL_NO = NVL(R_COIL_NO,'-')
			           AND ROWNUM <= 1) AS R_CAR_CHK
			       , KK.*
			     --    , ROW_NUMBER() OVER(PARTITION BY PRIOR4 ORDER BY PRIOR1,PRIOR2,PRIOR3 DESC ) GROUP_ROW
			      FROM
			         (
			        SELECT K.*
			--             , CASE WHEN TAG_LEFT_STL_NO  IS NOT NULL AND TAG_RIGHT_STL_NO IS NOT NULL THEN '2'
			--                WHEN TAG_LEFT_STL_NO  IS NOT NULL                  THEN '2'
			--                WHEN TAG_RIGHT_STL_NO IS NOT NULL                  THEN '2'
			--                ELSE '3' END --공BED
			--             AS PRIOR4

			           , C.COIL_NO    AS C_COIL_NO
			           , C.COIL_T     AS C_THICK
			           , C.COIL_W     AS C_WIDTH
			           , C.COIL_WT    AS C_WEIGTH
			           , C.COIL_OUTDIA  AS C_OUTDIA
			           , C.CURR_PROG_CD AS C_PROG_CD
			           , C.ORD_NO     AS C_ORD_NO   -- 주문번호
			           , C.ORD_DTL    AS C_ORD_DTL    -- 주문행번
			           , C.DEMANDER_CD  AS C_DEMANDER_CD-- 수요가코드
			           , (CASE WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
			               WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
			               WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
			               WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
			               WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
			               WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
			               WHEN C.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			               WHEN C.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			               WHEN C.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			               WHEN C.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			               WHEN C.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			               ELSE '' END
			             ) AS C_YEOJAE_CAUSE_CD
			           , TRUNC((SYSDATE - C.HRMILL_CMPL_DT )*24)         AS C_HOT_COIL_MIN
			           , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
			              WHEN C.COIL_OUTDIA >  1930 THEN 'C'
			              ELSE 'B' END  AS C_COIL_OUTDIA_GP
			           , CASE WHEN C.COIL_W < 1601   THEN 'M' ELSE 'L' END AS C_COIL_W_GP
			           , L.COIL_NO    AS L_COIL_NO
			           , L.COIL_T     AS L_THICK
			           , L.COIL_W     AS L_WIDTH
			           , L.COIL_WT    AS L_WEIGTH
			           , L.COIL_OUTDIA  AS L_OUTDIA
			           , L.CURR_PROG_CD AS L_PROG_CD
			           , L.ORD_NO     AS L_ORD_NO   -- 주문번호
			           , L.ORD_DTL    AS L_ORD_DTL    -- 주문행번
			           , L.DEMANDER_CD  AS L_DEMANDER_CD-- 수요가코드
			           , (CASE WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
			               WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
			               WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
			               WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
			               WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
			               WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
			               WHEN L.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			               WHEN L.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			               WHEN L.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			               WHEN L.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			               WHEN L.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			               ELSE '' END
			             ) AS L_YEOJAE_CAUSE_CD
			           , TRUNC((SYSDATE - L.HRMILL_CMPL_DT )*24) AS L_HOT_COIL_MIN
			           , R.COIL_NO    AS R_COIL_NO
			           , R.COIL_W     AS R_WIDTH
			           , R.COIL_T     AS R_THICK
			           , R.COIL_WT    AS R_WEIGTH
			           , R.COIL_OUTDIA  AS R_OUTDIA
			           , R.CURR_PROG_CD AS R_PROG_CD
			           , R.ORD_NO     AS R_ORD_NO   -- 주문번호
			           , R.ORD_DTL    AS R_ORD_DTL    -- 주문행번
			           , R.DEMANDER_CD  AS R_DEMANDER_CD-- 수요가코드
			           , (CASE WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
			               WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
			               WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
			               WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
			               WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
			               WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
			               WHEN R.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			               WHEN R.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			               WHEN R.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			               WHEN R.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			               WHEN R.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			               ELSE '' END
			             ) AS R_YEOJAE_CAUSE_CD
			           , TRUNC((SYSDATE - R.HRMILL_CMPL_DT )*24) R_HOT_COIL_MIN
			           , L.ISHOT      AS L_ISHOT
			           , R.ISHOT      AS R_ISHOT
			          FROM TO_LOC_DATA_TABLE K
			           , (SELECT 1 T_ROW, A.*
			              FROM USRPTA.TB_PT_COILCOMM  A ) C  --대상코일
			           , (SELECT 1 T_ROW, A.*
			              ,DECODE(NVL(DECODE(STL_APPEAR_GP,'Y','CG','CM'),'CM'),'CM',
			                  CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
			                     CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
			                      THEN 'TRUE'
			                      ELSE 'FALSE'
			                     END
			                     WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
			                     CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 96
			                      THEN 'TRUE'
			                      ELSE 'FALSE'
			                     END
			                     WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
			                     CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
			                      THEN 'TRUE'
			                      ELSE 'FALSE'
			                     END
			                     WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
			                     CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 48
			                      THEN 'TRUE'
			                      ELSE 'FALSE'
			                     END
			                  END) AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
			              FROM USRPTA.TB_PT_COILCOMM  A ) L  --하단LEFT
			           , (SELECT 1 T_ROW, A.*
			               ,DECODE(NVL(DECODE(STL_APPEAR_GP,'Y','CG','CM'),'CM'),'CM',
			                      CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
			                         CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
			                          THEN 'TRUE'
			                          ELSE 'FALSE'
			                         END
			                         WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
			                         CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 96
			                          THEN 'TRUE'
			                          ELSE 'FALSE'
			                         END
			                         WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
			                         CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
			                          THEN 'TRUE'
			                          ELSE 'FALSE'
			                         END
			                         WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
			                         CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 48
			                          THEN 'TRUE'
			                          ELSE 'FALSE'
			                         END
			                      END) AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
			              FROM USRPTA.TB_PT_COILCOMM  A ) R  --하단RIGHT
			         WHERE K.STL_NO       = C.COIL_NO(+)
			           AND K.TAG_LEFT_STL_NO  = L.COIL_NO(+)
			           AND K.TAG_RIGHT_STL_NO = R.COIL_NO(+)
			           --짱구코일 2단제외
			--           AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND JJANG_GP = 'Y'            THEN 0
			--                  ELSE 1 END
			           --2단일 경우 1단에 핫코일이 존재 시 2단제의
			           AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND (L.ISHOT ='TRUE' OR R.ISHOT='TRUE') THEN 0
			                ELSE 1 END
			--           --2단일 경우 좌우 적치 상태 CHECK
			           AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002'
			                   AND (TAG_LEFT_ACTIVE_STAT != 'E' OR TAG_RIGHT_ACTIVE_STAT != 'E'
			                     OR TAG_LEFT_LAYER_STAT  != 'C' OR TAG_RIGHT_LAYER_STAT  != 'C') THEN 0
			                ELSE 1 END
			--           --1단일 경우 좌우 상단 적치 상태 CHECK
			           AND 1 = CASE WHEN K.TAG_YD_STK_LYR_NO = '001' AND (TAG_2DAN_LEFT_STL_NO IS NOT NULL OR TAG_2DAN_RIGHT_STL_NO IS NOT NULL) THEN 0
			                ELSE 1 END

			         ) KK
			           --무인크레인 이동가능 여부 CHECK
			--       WHERE ( TAG_YD_STK_COL_GP IN (SELECT R.ITEM
			--                       FROM TB_YD_RULE    R
			--                        , AUTO_CR_TABLE A
			--                      WHERE R.REPR_CD_GP = 'CR0001'
			--                      AND R.CD_GP    = A.EQUIP_GP
			--                      AND R.DEL_YN   = 'N')
			--         )
			     )
			-- WHERE ((PRIOR4 < 3) OR (PRIOR4 = 3 AND GROUP_ROW <= 10))  -- 공BED 10개만 검색
			)
			--*--*--*--*--*--*-- 평점
			SELECT G.*
			   , CASE  -- 1단이면서 소재인 경우
			      WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
			      THEN CASE WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌우) 진도코드
			            WHEN C_PROG_CD  = L_PROG_CD             THEN '2' --동일(좌단) 진도코드
			            WHEN C_PROG_CD  = R_PROG_CD             THEN '2' --동일(우단) 진도코드
			            WHEN L_COIL_NO  IS NULL   AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
			            WHEN R_COIL_NO  IS NULL   AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED
			            ELSE '7' END
			       -- 2단이면서 소재인 경우
			      WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
			      THEN CASE --WHEN L_JJANG_GP  = 'Y'    OR  R_JJANG_GP = 'Y'    THEN '9' --짱구 위에서 못 올린다.
			            WHEN L_WB      = 'Y'    OR  R_WB     = 'Y'    THEN '8' --좌하단우하단 작업 예약
			            WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌하단우하단) 진도코드
			            WHEN C_PROG_CD  = L_PROG_CD             THEN '2' --동일(좌하단) 진도코드
			            WHEN C_PROG_CD  = R_PROG_CD             THEN '2' --동일(우하단) 진도코드
			            ELSE '2' END
			      -- 1단이면서 제품인 경우
			      WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8')
			      THEN CASE WHEN C_DEMANDER_CD      =   L_DEMANDER_CD
			               AND C_DEMANDER_CD    =   R_DEMANDER_CD
			               AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
			               AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL THEN '2' --좌우축 동일 고객사+주문번호행번

			            WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
			               AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL THEN '2' --좌우측 동일 주문번호행번

			            WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL THEN '2' --좌측   동일 주문번호행번
			            WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL THEN '2' --우측   동일 주문번호행번
			            WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO THEN '2' --좌우측 동일 주문번호
			            WHEN C_ORD_NO = L_ORD_NO                THEN '2' --우측   동일 주문번호
			            WHEN C_ORD_NO = R_ORD_NO                THEN '2' --좌측   동일 주문번호
			            WHEN L_COIL_NO IS NULL    AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
			            WHEN R_COIL_NO IS NULL    AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED

			            ELSE '7' END
			      -- 2단이면서 제품인 경우
			      WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8') --제품
			      THEN CASE --WHEN L_JJANG_GP = 'Y'   OR  R_JJANG_GP = 'Y'    THEN '9' -- 짱구 위에서 못 올린다.
			            WHEN L_WB = 'Y'       OR  R_WB  = 'Y'     THEN '8' --좌하단우하단 작업 예약
			            WHEN C_DEMANDER_CD      =   L_DEMANDER_CD
			               AND C_DEMANDER_CD    =   R_DEMANDER_CD
			               AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
			               AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL THEN '2' --좌우하단 동일 고객사+주문번호행번

			            WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
			               AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL THEN '2' --좌우하단 동일 주문번호행번

			            WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL THEN '2' --좌하단 동일 주문번호행번
			            WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL THEN '2' --우하단 동일 주문번호행번
			            WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO THEN '2' --좌우하단 동일 주문번호
			            WHEN C_ORD_NO = L_ORD_NO                THEN '2' --우하단 동일 주문번호
			            WHEN C_ORD_NO = R_ORD_NO                THEN '2' --좌하단 동일 주문번호
			            ELSE '2' END

			       ELSE '9' END GRADE
			  FROM (SELECT A.*
			       , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
			          FROM TB_YD_WRKBOOK  A1
			           , TB_YD_WRKBOOKMTL B1
			         WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			           AND A1.DEL_YN    = 'N'
			           AND B1.DEL_YN    = 'N'
			           AND B1.STL_NO    = A.C_COIL_NO
			         ) C_WB
			       , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
			          FROM TB_YD_WRKBOOK  A1
			           , TB_YD_WRKBOOKMTL B1
			         WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			           AND A1.DEL_YN    = 'N'
			           AND B1.DEL_YN    = 'N'
			           AND B1.STL_NO    = A.L_COIL_NO
			         ) L_WB
			       , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
			          FROM TB_YD_WRKBOOK  A1
			           , TB_YD_WRKBOOKMTL B1
			         WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			           AND A1.DEL_YN    = 'N'
			           AND B1.DEL_YN    = 'N'
			           AND B1.STL_NO    = A.R_COIL_NO
			         ) R_WB
			      FROM TO_LOC_DATA_COMP_TABLE A
			     ) G
			 ORDER BY GRADE
			    , L_CAR_CHK + R_CAR_CHK
			    , PRIOR3     -- BED
			    , PRIOR4 DESC  -- 단
			*/

			JDTORecordSet jsUserRslt = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocUserHC", logId, mthdNm, "결로보급 베드 조회");
			if (jsUserRslt.size() <= 0) {
				sLogMsg = "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, sLogMsg, "SL");

				if ("Y".equals(schLogYn)) {
					sSchLogContents = "[getYdToLocUserHC]대상코일위치검색실패:"+ sStlNo+" LOG :"+"\r\n";
					this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
				}
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
				
			}
			
			//----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 SCH RULL 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord  jrRuleConst = this.toLocRuleConst(logId, mthdNm);
			
			//----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 LOGIC 적용여부 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrLocRuleApply = this.toLocRuleApply(logId, mthdNm);
			commUtils.printParam(logId, jrLocRuleApply);
			
		    // 적치 가능 check
			JDTORecord jrUserRslt = JDTORecordFactory.getInstance().create();

	    	String ydStkColGp 	 = "";
			String ydStkBedNo 	 = "";
			String ydStkLyrNo 	 = "";	
			String sRtnBedDan 	 = "";	
			String sLocAbleRtn 	 = "";
			String sLocAbleRtnMsg = "";
			String sGrade 	     = "";	
			String sDBLogMsg	 = "";			
			for (int i = 1; i <= jsUserRslt.size(); i++) {

				jsUserRslt.absolute(i);
				jrUserRslt  = jsUserRslt.getRecord();
				
				sGrade 		= commUtils.nvl (jrUserRslt.getFieldString("GRADE"),"9");
				ydStkColGp 	= commUtils.trim(jrUserRslt.getFieldString("TAG_YD_STK_COL_GP"  ));
				ydStkBedNo 	= commUtils.trim(jrUserRslt.getFieldString("TAG_YD_STK_BED_NO"  ));
				ydStkLyrNo 	= commUtils.trim(jrUserRslt.getFieldString("TAG_YD_STK_LYR_NO"  ));	

				/**************************************
				 *         적치 가능 check
		   		 **************************************/	
				JDTORecord jrLocAbleRtn = this.toLocAbleCheck(logId, mthdNm, jrUserRslt, jrRuleConst, jrLocRuleApply);

				sLocAbleRtn    = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
				sLocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
				if (sLocAbleRtnMsg.length() > 0 ) {
					sDBLogMsg = sDBLogMsg + sLocAbleRtnMsg +"\r\n";
				}
				
				if ("1".equals(sLocAbleRtn)) {
					sLogMsg = ydStkColGp + ydStkBedNo + ydStkLyrNo+"  적치가능 위치평점:"+ sGrade;
					commUtils.printLog(logId, sLogMsg, "SL");				
				    //적치가능 
					sRtnBedDan = ydStkColGp + ydStkBedNo + ydStkLyrNo ;
	    			break;
				}
			}
			
			if (sRtnBedDan.length() < 10) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 결로보급 사용자지정 TO위치 결정 실패 ";
				commUtils.printLog(logId, sLogMsg, "SL");
			
				if ("Y".equals(schLogYn)) {
					sSchLogContents = "[getYdToLocUserHC] 결로보급사용자 지정 대상코일선택실패:"+ sStlNo+" LOG :"+"\r\n" + sDBLogMsg;
					this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
				}
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sDBLogMsg);
				return jrRtn;
								
			}
			
			if ("Y".equals(schLogYn)) {
				sSchLogContents = "[getYdToLocUserHC]대상코일선택:"+ sStlNo+"  선택위치:"+ sRtnBedDan + "  평점:" + sGrade +" LOG :"+"\r\n" + sDBLogMsg;
				this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
			}
 					

			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = commUtils.getParam(logId, mthdNm, sModifier);
			jrSetLoc.setField("YD_CRN_SCH_ID", 	ydCrnSchId); 
			jrSetLoc.setField("YD_EQP_ID", 		ydEqpId);	 
			jrSetLoc.setField("YD_SCH_CD", 		ydSchCd);	 
			jrSetLoc.setField("YD_UP_WO_LOC", 	ydUpWoLoc); 
			jrSetLoc.setField("YD_UP_WO_LAYER",	ydUpWoLayer);	 
			jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER", sRtnBedDan.substring(8));
			jrSetLoc.setField("YD_WBOOK_ID", 	ydWookId); 
				
			JDTORecord jrRcvRtn = this.toLocUpdCrnSch(logId,mthdNm, jrSetLoc, jrCrnSch  );
			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
			
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
	    }//end of try~catch				
	}      
  
	/**
	 * 사용자지정작업
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord toLocUser(String logId, String mthdNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String mthdNm = "TO위치결정:사용자지정작업[CCoilSchSeEJB.toLocUser] < " + mthdNms;
    	String sLogMsg = null;
    	JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
		
			commUtils.printLog(logId, mthdNm, "S+");

			String ydSchCd 	   	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));		//크레인스케줄코드
			String ydWookId		= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));	//작업예약
			String ydToLocGuide	= commUtils.trim(jrWbook.getFieldString("YD_TO_LOC_GUIDE")); //작업예약
			String schLogYn	    = commUtils.trim(jrWbook.getFieldString("SCHLOG_YN"  ));			//작업예약
			
			String sStlNo	   	= commUtils.trim(jrCrnSch.getFieldString("STL_NO"));			//크레인작업재료
			String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
			String ydEqpId     	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
			String ydUpWoLoc 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
			String ydUpWoLayer 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));
			String sModifier    = commUtils.trim(jrCrnSch.getFieldString("MODIFIER"));
			String sSchLogContents	= "";
			
			String sRtnBedDan 	= "";  //TO위치	

			if ( "".equals(ydUpWoLoc) ) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			}
			

			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			JDTORecord jrParam  = commUtils.getParam(logId, mthdNm, sModifier); 
			jrParam.setField("STL_NO"			, sStlNo);											//권상 STOCK
			jrParam.setField("YD_TO_LOC_GUIDE"	, ydToLocGuide);									//가이드
			jrParam.setField("YD_SCH_CD"		, ydSchCd);											//스케쥴 코드
			jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);										//크레인 작업지시 ID
			jrParam.setField("YD_EQP_ID"		, ydEqpId);											//설비 번호
			jrParam.setField("YD_UP_WO_LOC" 	, ydUpWoLoc);  
			jrParam.setField("YD_UP_WO_LAYER" 	, ydUpWoLayer); 
			
			sLogMsg =  " TOSQL"+ydCrnSchId+ " 권상재료["+sStlNo +" +스케쥴 코드 : "+ ydSchCd + "의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, sLogMsg, "SL");
			
			
			 
			// 텔레스코프 보급 시 to위치 저장위치
			if("JFTE01".equals(ydToLocGuide)){
				ydToLocGuide = ydToLocGuide + "01001";
				
				sLogMsg = "텔레스코프 보급 시 to위치 저장위치 ["+ydToLocGuide+"]";
				commUtils.printLog(logId, sLogMsg, "SL");  
			}
	      	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	사용자 지정한 위치만 검색
			//  적재가능 위치 만 검색
			//  외경 CHECK 안함
			//----------------------------------------------------------------------------------------------------------------------
			if (ydToLocGuide.length() == 11) {
				// 차량/대차
				sLogMsg = "차량/대차 적재위치 가이드 열+ 베드+ 단  지정된 경우 ["+ydToLocGuide+"]의 베드 조회 시작";
				commUtils.printLog(logId, sLogMsg, "SL");
				sRtnBedDan = ydToLocGuide;
				
			} else {
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocUser
				 -- 대상 위치 SELECT
				WITH TO_LOC_TABLE AS (
				SELECT X1.YD_SCH_CD
				     , SL.YD_STK_COL_GP         AS TAG_YD_STK_COL_GP
				     , SL.YD_STK_BED_NO         AS TAG_YD_STK_BED_NO
				     , SL.YD_STK_LYR_NO         AS TAG_YD_STK_LYR_NO
				     , SL.YD_STK_LYR_MTL_STAT   AS TAG_YD_STK_LYR_MTL_STAT
				     -- 열에 따른 코일 간격
				     , SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP)  AS INT
				     , DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
				                                '002', SL.YD_STK_BED_NO)                               AS TAG_LEFT_BED

				     , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_LYR_NO,
				                                '002', LPAD(TO_NUMBER(SL.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_LEFT_LAYER

				     , DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
				                                '002', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'))
				                                                                                       AS TAG_RIGHT_BED
				     , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_LYR_NO,
				                                '002', LPAD(TO_NUMBER(SL.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_RIGHT_LAYER
				     , CASE WHEN SUBSTR(SL.YD_STK_COL_GP,3,2) IN ('01','80') THEN C1.COIL_OUTDIA_GRP_GP
				            ELSE SC.COL_OUTDIA_GRP_GP END COL_OUTDIA_GRP_GP
				     , CASE WHEN SUBSTR(SL.YD_STK_COL_GP,3,2) IN ('01','80') THEN C1.COIL_W_GP
				            ELSE SC.COL_W_GP END COL_W_GP            

				     , SC.COL_YD_LOC_GP
				     , SC.COL_YD_STK_SKID_GP
				     , C1.STL_NO
				     , C1.COIL_W
				     , C1.COIL_OUTDIA_GRP_GP
				     , C1.COIL_W_GP
				     , (CASE WHEN SL.YD_STK_COL_GP LIKE '__TC%' AND SL.YD_STK_BED_NO='01' THEN 1
				             WHEN SL.YD_STK_COL_GP LIKE '__TC%' AND SL.YD_STK_BED_NO='02' THEN 3
				             WHEN SL.YD_STK_COL_GP LIKE '__TC%' AND SL.YD_STK_BED_NO='03' THEN 2
				             ELSE 0  END)       AS SORT_TC
				     -- 적치가능여부 CHECK시 필요 :H/J/ABC/WRAP
				     , CASE WHEN SUBSTR(X1.YD_SCH_CD,8,1) = 'H' THEN 'H'
				            ELSE 'J' END  AS CHK_SKID_GP
				     , SC.YD_STK_COL_TOLOC_STAT       
				  FROM TB_YD_STKLYR SL
				     , ( SELECT YD_STK_COL_GP
				              , YD_GP
				              , YD_BAY_GP
				              --SKID 외경구분
				              , YD_COIL_OUTDIA_GRP_GP  AS COL_OUTDIA_GRP_GP
				              , YD_STK_SKID_GP         AS COL_YD_STK_SKID_GP
				              , DEL_YN
				              , YD_LOC_GP       AS COL_YD_LOC_GP
				              , YD_STK_COL_W_GP AS COL_W_GP
				              , YD_STK_COL_TOLOC_STAT
				           FROM TB_YD_STKCOL
				          WHERE DEL_YN = 'N'
				            AND YD_GP  = 'J'
				       ) SC
				     , (
				         SELECT CC.COIL_NO                    AS STL_NO
				              , CASE WHEN CC.COIL_OUTDIA <= 1280 THEN 'A'
				                     WHEN CC.COIL_OUTDIA >  1930 THEN 'C'
				                     ELSE 'B' END             AS COIL_OUTDIA_GRP_GP
				              , CC.COIL_W
				              , CASE WHEN CC.COIL_W < 1601 THEN 'M'
				                     ELSE 'L' END             AS COIL_W_GP
				           FROM USRPTA.TB_PT_COILCOMM CC
				          WHERE CC.COIL_NO = :V_STL_NO
				            AND ROWNUM = 1
				       ) C1
				     , (
				         SELECT :V_YD_SCH_CD        AS YD_SCH_CD
				              , :V_YD_TO_LOC_GUIDE  AS YD_TO_LOC_GUIDE
				           FROM DUAL
				       ) X1
				     , (SELECT ITEM1 AS TOT_YN FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP002' AND DEL_YN = 'N' AND ROWNUM = 1) YR
				 WHERE SL.YD_STK_COL_GP       = SC.YD_STK_COL_GP
				   AND SL.YD_STK_COL_GP       LIKE SUBSTR(X1.YD_TO_LOC_GUIDE,1,6) || '%' --가이드 열
				   AND SL.YD_STK_BED_NO       LIKE SUBSTR(X1.YD_TO_LOC_GUIDE,7,2) || '%' --가이드 BED
				   AND SL.YD_STK_LYR_NO       IN ('001','002')
				   AND SL.YD_STK_LYR_ACT_STAT = 'E'
				   AND SL.YD_STK_LYR_MTL_STAT = 'E'
				   AND SL.DEL_YN = 'N'
				   AND SC.DEL_YN = 'N'
				   AND SL.YD_STK_BED_NO BETWEEN '00' AND '99'
				   --****  야드 통합여부  *****
				   AND 'Y' = CASE WHEN YR.TOT_YN = 'N' THEN
				                       CASE WHEN SC.COL_YD_LOC_GP = DECODE(SUBSTR(YD_SCH_CD,8,1),'H','H','J') THEN 'Y'
				                            WHEN SC.COL_YD_LOC_GP = 'H' AND YD_SCH_CD LIKE 'J_PT_3_M'         THEN 'Y' -- 공냉재 이송은 소재
				                            ELSE 'N'
				                       END
				                  ELSE 'Y' END
				   -- 제품에 TO위치 기준 적용
				   AND 'Y' = CASE WHEN SC.COL_YD_LOC_GP = 'H'         THEN 'Y'
				                  WHEN SUBSTR(SC.YD_STK_COL_GP,3,2) IN ('01','80') then 'Y'
				                  WHEN SC.YD_STK_COL_TOLOC_STAT = 'Y' THEN 'Y' 
				                  ELSE 'N' END
				   -- 적치폭 구분 CHECK(소재위치)
				   AND 'Y' = CASE WHEN SC.COL_YD_LOC_GP = 'H'  THEN 'Y'
				                  WHEN SC.COL_YD_LOC_GP = 'J'  AND SUBSTR(SC.YD_STK_COL_GP,3,2) IN ('01','80')  THEN 'Y' --제품가상스판은 체크안함
				                  WHEN SC.COL_YD_LOC_GP = 'J'
				                       AND SUBSTR(SC.YD_STK_COL_GP,3,1) IN ('0','1','2','3','4','5','6')
				                       AND SC.COL_W_GP = C1.COIL_W_GP
				                       THEN 'Y'
				                  ELSE 'N' END
				   --제품단위이적은 가상스판포함. 열단위이적은 가상위치 제외
				   AND 'Y' = CASE WHEN SUBSTR(SC.YD_STK_COL_GP,3,2) IN ('01','80') AND LENGTH(X1.YD_TO_LOC_GUIDE) = 6 THEN 'N' --열단위이적 YD_TO_LOC_GUIDE 6자리
				                  WHEN SUBSTR(SC.YD_STK_COL_GP,3,2) IN ('01','80') AND SL.YD_STK_LYR_NO = '002'       THEN 'N'
				                  ELSE 'Y' END				                  
				   -- 설비 작업대상재 제외
				   -- 크레인 작업지시 제외
				   -- AND SL.YD_STK_COL_GP||SL.YD_STK_BED_NO||SL.YD_STK_LYR_NO NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YD_CRNSCH WHERE DEL_YN = 'N')
				   -- 작업예약 TO위치 가이드 제외
				   AND SL.YD_STK_COL_GP||SL.YD_STK_BED_NO NOT IN ( SELECT NVL(YD_TO_LOC_GUIDE,'1') FROM TB_YD_WRKBOOK WHERE DEL_YN = 'N' AND YD_TO_LOC_GUIDE <> X1.YD_TO_LOC_GUIDE)

				 )
				, TO_LOC_DATA_TABLE AS (
				-- TO위치 코일정보 SELECT
				SELECT A.STL_NO
				     , A.YD_SCH_CD
				--     , NVL((SELECT 'A' AS YD_EQP_WRK_MODE2 FROM TB_YD_EQP WHERE YD_EQP_ID = :V_YD_EQP_ID),'A') AS YD_EQP_WRK_MODE2
				     , (SELECT DECODE(ITEM1, 'Y', 'A', 'M') AS YD_EQP_WRK_MODE2 FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP100' AND CD_GP = :V_YD_EQP_ID) AS YD_EQP_WRK_MODE2
				     , A.TAG_YD_STK_COL_GP
				     , A.TAG_YD_STK_BED_NO
				     , A.TAG_YD_STK_LYR_NO
				     , A.TAG_LEFT_BED
				     , A.TAG_LEFT_LAYER
				     , A.SORT_TC
				     , A.COIL_OUTDIA_GRP_GP
				     , A.COL_OUTDIA_GRP_GP
				     , A.COL_YD_LOC_GP
				     , A.COL_W_GP
				     , A.COIL_W_GP
				     , A.CHK_SKID_GP
				     , A.COL_YD_STK_SKID_GP
				     , A.YD_STK_COL_TOLOC_STAT  
				     , (SELECT YD_STK_LYR_ACT_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO   = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N' ) AS TAG_LEFT_ACTIVE_STAT
				     , (SELECT YD_STK_LYR_MTL_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO   = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N' ) AS TAG_LEFT_LAYER_STAT
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO   = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N' ) AS TAG_LEFT_STL_NO
				     , A.TAG_RIGHT_BED
				     , A.TAG_RIGHT_LAYER
				     , (SELECT YD_STK_LYR_ACT_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO   = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO   = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N' ) AS TAG_RIGHT_ACTIVE_STAT
				     , (SELECT YD_STK_LYR_MTL_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO   = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO   = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N' ) AS TAG_RIGHT_LAYER_STAT
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO   = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO   = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N' ) AS  TAG_RIGHT_STL_NO
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO   = '002'
				           AND B.DEL_YN = 'N' ) AS TAG_2DAN_LEFT_STL_NO
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO   = A.TAG_YD_STK_BED_NO
				           AND B.YD_STK_LYR_NO   = '002'
				           AND B.DEL_YN = 'N' ) AS TAG_2DAN_RIGHT_STL_NO
				  FROM TO_LOC_TABLE A
				)
				, TO_LOC_DATA_COMP_TABLE AS (
				--*--*--*--*--*--*-- 적치가능위치
				SELECT *
				  FROM (
				        -- 배차 편성 4대분 제외
				        SELECT CASE WHEN ( SELECT COUNT(*)
				                             FROM (
				                                    SELECT ROW_NUMBER() OVER(PARTITION BY CS.YD_CARLD_STOP_LOC ORDER BY CS.YD_BAYIN_WO_SEQ, CS.YD_CAR_SCH_ID) AS RN
				                                         , CS.*
				                                      FROM TB_YD_CARSCH CS
				                                     WHERE CS.DEL_YN = 'N'
				                                       AND CS.YD_CARLD_STOP_LOC  LIKE 'J%'
				                                   ) CS
				                                , TB_YD_STOCK    ST
				                            WHERE CS.RN < 5
				                              AND CS.YD_CARLD_STOP_LOC  LIKE 'J'|| YD_SCH_CD|| '%'
				                              AND CS.TRANS_ORD_DATE  = ST.TRANS_ORD_DATE
				                              AND CS.TRANS_ORD_SEQNO = ST.TRANS_ORD_SEQNO 
				                              AND ST.STL_NO = NVL(L_COIL_NO,'-') 
				                              AND ROWNUM <= 1
				                         ) > 0  AND L_PROG_CD IN ('6','L') AND TAG_YD_STK_LYR_NO = '002' THEN 1 ELSE 0 END 
				                      AS L_CAR_YN        
				             , CASE WHEN ( SELECT COUNT(*)
				                             FROM (
				                                    SELECT ROW_NUMBER() OVER(PARTITION BY CS.YD_CARLD_STOP_LOC ORDER BY CS.YD_BAYIN_WO_SEQ, CS.YD_CAR_SCH_ID) AS RN
				                                         , CS.*
				                                      FROM TB_YD_CARSCH CS
				                                     WHERE CS.DEL_YN = 'N'
				                                       AND CS.YD_CARLD_STOP_LOC  LIKE 'J%'
				                                   ) CS
				                                , TB_YD_STOCK    ST
				                            WHERE CS.RN < 5
				                              AND CS.YD_CARLD_STOP_LOC  LIKE 'J'|| YD_SCH_CD|| '%'
				                              AND CS.TRANS_ORD_DATE  = ST.TRANS_ORD_DATE
				                              AND CS.TRANS_ORD_SEQNO = ST.TRANS_ORD_SEQNO 
				                              AND ST.STL_NO = NVL(R_COIL_NO,'-') 
				                              AND ROWNUM <= 1
				                         ) > 0  AND R_PROG_CD IN ('6','L') AND TAG_YD_STK_LYR_NO = '002' THEN 1 ELSE 0 END 
				                      AS R_CAR_YN 
				             , CASE WHEN (  SELECT COUNT(*)
				                              FROM TB_YD_STOCK A
				                                 , TB_YD_CARSCH B
				                             WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
				                               AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
				                               AND B.DEL_YN = 'N'
				                               AND A.STL_NO = NVL(L_COIL_NO,'-')
				                               AND ROWNUM <= 1
				                         ) > 0 AND TAG_YD_STK_LYR_NO = '002' THEN 1 ELSE 0 END  
				                      AS L_CAR_CHK
				             , CASE WHEN (  SELECT COUNT(*)
				                              FROM TB_YD_STOCK A
				                                 , TB_YD_CARSCH B
				                             WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
				                               AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
				                               AND B.DEL_YN = 'N'
				                               AND A.STL_NO = NVL(R_COIL_NO,'-')
				                               AND ROWNUM <= 1
				                         ) > 0 AND TAG_YD_STK_LYR_NO = '002' THEN 1 ELSE 0 END  
				                      AS R_CAR_CHK                    
				             -- 1단우선적치 B군(폭1200이상,폭1600미만,외경1800이상,외경1930미만) 
				             --           C군(폭1200이상,폭1600미만,외경1990이상) 서주임님
				             , CASE WHEN SUBSTR(TAG_YD_STK_COL_GP,1,2) IN ('JB') AND SUBSTR(TAG_YD_STK_COL_GP,3,2) BETWEEN '02' AND '14'  AND TAG_YD_STK_LYR_NO = '001' THEN  
				                         CASE WHEN C_COIL_OUTDIA_GP = 'B' AND C_WIDTH >= 1200 AND C_WIDTH  <= 1600 AND C_OUTDIA >= 1800 AND C_OUTDIA <= 1930 THEN '1'
				                              WHEN C_COIL_OUTDIA_GP = 'C' AND C_WIDTH >= 1200 AND C_WIDTH  <= 1600 AND C_OUTDIA >= 1990                      THEN '1'
				                              ELSE '2' END
				                    WHEN SUBSTR(TAG_YD_STK_COL_GP,1,2) IN ('JC') AND SUBSTR(TAG_YD_STK_COL_GP,3,2) BETWEEN '02' AND '15'  AND TAG_YD_STK_LYR_NO = '001' THEN  
				                         CASE WHEN C_COIL_OUTDIA_GP = 'B' AND C_WIDTH >= 1200 AND C_WIDTH  <= 1600 AND C_OUTDIA >= 1800 AND C_OUTDIA <= 1930 THEN '1'
				                              WHEN C_COIL_OUTDIA_GP = 'C' AND C_WIDTH >= 1200 AND C_WIDTH  <= 1600 AND C_OUTDIA >= 1990                      THEN '1'
				                              ELSE '2' END
				                    ELSE '2'          
				                END AS ABOVE_LYR
				             , KK.*
				         --    , ROW_NUMBER() OVER(PARTITION BY PRIOR4 ORDER BY PRIOR1,PRIOR2,PRIOR3 DESC ) GROUP_ROW
				          FROM
				               (
				                SELECT K.*
				                     , C.COIL_NO      AS C_COIL_NO
				                     , C.COIL_T       AS C_THICK
				                     , C.COIL_W       AS C_WIDTH
				                     , C.COIL_WT      AS C_WEIGTH
				                     , C.COIL_OUTDIA  AS C_OUTDIA
				                     , C.CURR_PROG_CD AS C_PROG_CD
				                     , C.ORD_NO       AS C_ORD_NO     -- 주문번호
				                     , C.ORD_DTL      AS C_ORD_DTL    -- 주문행번
				                     , C.DEMANDER_CD  AS C_DEMANDER_CD-- 수요가코드
				                     , (CASE WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
				                             WHEN C.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             ELSE '' END
				                       ) AS C_YEOJAE_CAUSE_CD
				                     , TRUNC((SYSDATE - C.HRMILL_CMPL_DT )*24)             AS C_HOT_COIL_MIN
				                     , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
				                            WHEN C.COIL_OUTDIA >  1930 THEN 'C'
				                            ELSE 'B' END  AS C_COIL_OUTDIA_GP
				                     , CASE WHEN C.COIL_W < 1601     THEN 'M' ELSE 'L' END AS C_COIL_W_GP
				                     , L.COIL_NO      AS L_COIL_NO
				                     , L.COIL_T       AS L_THICK
				                     , L.COIL_W       AS L_WIDTH
				                     , L.COIL_WT      AS L_WEIGTH
				                     , L.COIL_OUTDIA  AS L_OUTDIA
				                     , L.CURR_PROG_CD AS L_PROG_CD
				                     , L.ORD_NO       AS L_ORD_NO     -- 주문번호
				                     , L.ORD_DTL      AS L_ORD_DTL    -- 주문행번
				                     , L.DEMANDER_CD  AS L_DEMANDER_CD-- 수요가코드
				                     , (CASE WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
				                             WHEN L.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             ELSE '' END
				                       ) AS L_YEOJAE_CAUSE_CD
				                     , TRUNC((SYSDATE - L.HRMILL_CMPL_DT )*24) AS L_HOT_COIL_MIN
				                     , R.COIL_NO      AS R_COIL_NO
				                     , R.COIL_W       AS R_WIDTH
				                     , R.COIL_T       AS R_THICK
				                     , R.COIL_WT      AS R_WEIGTH
				                     , R.COIL_OUTDIA  AS R_OUTDIA
				                     , R.CURR_PROG_CD AS R_PROG_CD
				                     , R.ORD_NO       AS R_ORD_NO     -- 주문번호
				                     , R.ORD_DTL      AS R_ORD_DTL    -- 주문행번
				                     , R.DEMANDER_CD  AS R_DEMANDER_CD-- 수요가코드
				                     , (CASE WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
				                             WHEN R.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             ELSE '' END
				                       ) AS R_YEOJAE_CAUSE_CD
				                     , TRUNC((SYSDATE - R.HRMILL_CMPL_DT )*24) R_HOT_COIL_MIN
				                     , L.ISHOT        AS L_ISHOT
				                     , R.ISHOT        AS R_ISHOT
				                     , C.ISHOT        AS C_ISHOT
				                  FROM TO_LOC_DATA_TABLE K
				                     , (SELECT 1 T_ROW, A.*
				                             ,    CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 96
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 48
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                  END  AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
				                          FROM USRPTA.TB_PT_COILCOMM  A ) C  --대상코일
				                     , (SELECT 1 T_ROW, A.*
				                             , CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 96
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 48
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                  END  AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
				                          FROM USRPTA.TB_PT_COILCOMM  A ) L  --하단LEFT
				                     , (SELECT 1 T_ROW, A.*
				                             , CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 96
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 48
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                          END  AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
				                          FROM USRPTA.TB_PT_COILCOMM  A ) R  --하단RIGHT
				                 WHERE K.STL_NO           = C.COIL_NO(+)
				                   AND K.TAG_LEFT_STL_NO  = L.COIL_NO(+)
				                   AND K.TAG_RIGHT_STL_NO = R.COIL_NO(+)
				                   --2단일 경우 1단에 핫코일이 존재 시 2단제의
				                   AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND C.ISHOT ='TRUE' THEN CASE WHEN L.ISHOT ='TRUE' AND R.ISHOT='TRUE' THEN 1
				                                                                                             ELSE 0 END    
				                                ELSE 1 END
				                   --2단일 경우 좌우 적치 상태 CHECK
				                   AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002'
				                                     AND (TAG_LEFT_ACTIVE_STAT != 'E' OR TAG_RIGHT_ACTIVE_STAT != 'E'
				                                       OR TAG_LEFT_LAYER_STAT  != 'C' OR TAG_RIGHT_LAYER_STAT  != 'C') THEN 0
				                                ELSE 1 END
				                   --1단일 경우 좌우 상단 적치 상태 CHECK
				                   AND 1 = CASE WHEN K.TAG_YD_STK_LYR_NO = '001' AND (TAG_2DAN_LEFT_STL_NO IS NOT NULL OR TAG_2DAN_RIGHT_STL_NO IS NOT NULL) THEN 0
				                                ELSE 1 END

				               ) KK

				       )
				-- 황주임님 하단에 'L','6'제외요청        
				-- WHERE L_CAR_YN + R_CAR_YN = 0 
				 WHERE 'Y' = CASE WHEN YD_SCH_CD LIKE 'J_PT_3_M' THEN 'Y'
				                  WHEN L_CAR_YN + R_CAR_YN = 0   THEN 'Y'
				                  ELSE 'N'
				             END
				)
				--*--*--*--*--*--*-- 평점
				SELECT G.*
				     , CASE  -- 1단이면서 소재인 경우
				            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
				            THEN CASE WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌우) 진도코드
				                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌단) 진도코드
				                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우단) 진도코드
				                      WHEN L_COIL_NO  IS NULL     AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
				                      WHEN R_COIL_NO  IS NULL     AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED
				                      ELSE '7' END
				             -- 2단이면서 소재인 경우
				            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
				            THEN CASE --WHEN L_JJANG_GP  = 'Y'      OR  R_JJANG_GP = 'Y'      THEN '9' --짱구 위에서 못 올린다.
				                      WHEN L_WB        = 'Y'      OR  R_WB       = 'Y'      THEN '9' --좌하단우하단 작업 예약
				                      WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌하단우하단) 진도코드
				                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌하단) 진도코드
				                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우하단) 진도코드
				                      ELSE '2' END
				            -- 1단이면서 제품인 경우
				            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8')
				            THEN CASE WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
				                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
				                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우축 동일 고객사+주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우측 동일 주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌측   동일 주문번호행번
				                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우측   동일 주문번호행번
				                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우측 동일 주문번호
				                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우측   동일 주문번호
				                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌측   동일 주문번호
				                      WHEN L_COIL_NO IS NULL      AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
				                      WHEN R_COIL_NO IS NULL      AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED

				                      ELSE '7' END
				            -- 2단이면서 제품인 경우
				            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8') --제품
				            THEN CASE --WHEN L_JJANG_GP = 'Y'       OR  R_JJANG_GP = 'Y'      THEN '9' -- 짱구 위에서 못 올린다.
				                      WHEN L_WB = 'Y'             OR  R_WB  = 'Y'           THEN '9' --좌하단우하단 작업 예약
				                      WHEN L_PROG_CD IN ('6','L') OR  R_PROG_CD IN ('6','L')THEN '8' --상차지시
				                      WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
				                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
				                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 고객사+주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌하단   동일 주문번호행번
				                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우하단   동일 주문번호행번
				                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우하단 동일 주문번호
				                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우하단   동일 주문번호
				                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌하단   동일 주문번호
				                      ELSE '2' END

				             ELSE '7' END GRADE
				  FROM (SELECT A.*
				             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
				                  FROM TB_YD_WRKBOOK    A1
				                     , TB_YD_WRKBOOKMTL B1
				                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                   AND A1.DEL_YN      = 'N'
				                   AND B1.DEL_YN      = 'N'
				                   AND B1.STL_NO      = A.C_COIL_NO
				               ) C_WB
				             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
				                  FROM TB_YD_WRKBOOK    A1
				                     , TB_YD_WRKBOOKMTL B1
				                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                   AND A1.DEL_YN      = 'N'
				                   AND B1.DEL_YN      = 'N'
				                   AND B1.STL_NO      = A.L_COIL_NO
				               ) L_WB
				             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
				                  FROM TB_YD_WRKBOOK    A1
				                     , TB_YD_WRKBOOKMTL B1
				                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                   AND A1.DEL_YN      = 'N'
				                   AND B1.DEL_YN      = 'N'
				                   AND B1.STL_NO      = A.R_COIL_NO
				               ) R_WB
				          FROM TO_LOC_DATA_COMP_TABLE A
				       ) G
				 ORDER BY GRADE
				        , L_CAR_CHK + R_CAR_CHK
				*/
 				
 				JDTORecordSet jsUserRslt = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocUser", logId, mthdNm, "사용자 베드 조회");
 				if (jsUserRslt.size() <= 0) {
 					sLogMsg = "적치가능한 베드 검색 실패 ";
 					commUtils.printLog(logId, sLogMsg, "SL");

 					if ("Y".equals(schLogYn)) {
 						sSchLogContents = "[getYdToLocUser]사용자지정 대상코일위치검색실패:"+ sStlNo+" LOG :"+"\r\n";
 						this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
 					}

 					jrRtn.setField("RTN_CD" , "0");	
 					jrRtn.setField("RTN_MSG", sLogMsg);
 					return jrRtn;
 				}
 				
 				//----------------------------------------------------------------------------------------------------------------------
 				//	적재가능 위치 SCH RULL 검색
 				//----------------------------------------------------------------------------------------------------------------------
 				JDTORecord  jrRuleConst = this.toLocRuleConst(logId, mthdNm);
 				
 				//----------------------------------------------------------------------------------------------------------------------
 				//	적재가능 위치 LOGIC 적용여부 검색
 				//----------------------------------------------------------------------------------------------------------------------
 				JDTORecord jrLocRuleApply = this.toLocRuleApply(logId, mthdNm);
 				commUtils.printParam(logId, jrLocRuleApply);
 				
 					
 			    // 적치 가능 check
 				JDTORecord jrUserRslt = JDTORecordFactory.getInstance().create();
 				String sGrade         = "9";
 				String sLocAbleRtn 	  = "";
 				String sLocAbleRtnMsg = "";
 				String sDBLogMsg	  = "";
 				String ydStkColGp  	  = "";
 				String ydStkBedNo	  = "";
 				String ydStkLyrNo 	  = "";
 				for (int i = 1; i <= jsUserRslt.size(); i++) {

 					jsUserRslt.absolute(i);
 					jrUserRslt  = jsUserRslt.getRecord();
 					
 					sGrade 		= commUtils.nvl (jrUserRslt.getFieldString("GRADE"),"9");
 					ydStkColGp 	= commUtils.trim(jrUserRslt.getFieldString("TAG_YD_STK_COL_GP"  ));
 					ydStkBedNo 	= commUtils.trim(jrUserRslt.getFieldString("TAG_YD_STK_BED_NO"  ));
 					ydStkLyrNo 	= commUtils.trim(jrUserRslt.getFieldString("TAG_YD_STK_LYR_NO"  ));	

 					/**************************************
 					 *         적치 가능 check
 			   		 **************************************/
 					JDTORecord jrLocAbleRtn = this.toLocAbleCheck(logId, mthdNm, jrUserRslt, jrRuleConst, jrLocRuleApply);

 					sLocAbleRtn    = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
 					sLocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
 					if (sLocAbleRtnMsg.length() > 0 ) {
 						sDBLogMsg = sDBLogMsg + sLocAbleRtnMsg +"\r\n";
 					}
 					
 					
 					if ("1".equals(sLocAbleRtn)) {
 						sLogMsg = ydStkColGp + ydStkBedNo + ydStkLyrNo+"  적치가능 위치평점:"+ sGrade;
 						commUtils.printLog(logId, sLogMsg, "SL");				
 					    //적치가능 
 						sRtnBedDan = ydStkColGp + ydStkBedNo + ydStkLyrNo ;
 		    			break;
 					}
 				}
 				
				String sAPP901 = coilDao.ApplyYn(logId, mthdNm, "APP901", "J", "*");
				commUtils.printLog(logId, "TO위치 RULE상수[신sAPP901] : " + sAPP901, "SL");
				
	    		if ("Y".equals(sAPP901)) {
					/***************************************************
    				** 신 로직
    				** XX이면서 소재인경우  폭및 외경구분 안하는 1단을 다시 검색
    				****************************************************/	    			
	    			if (sRtnBedDan.length() < 10) {
	    				
	    				//소재이고  1단을 폭및 외경구분 안하는 걸루 다시 돌림
	 					if ("H".equals(ydSchCd.substring(7, 8))) {
	 						commUtils.printLog(logId, "▼▼▼▼▼▼▼▼▼▼ 소재 1단  재검색 ▼▼▼▼▼▼▼▼▼▼", "SL");
	 						
	 						for (int j = 1; j <= jsUserRslt.size(); j++) {

	 							jsUserRslt.absolute(j);
	 							jrUserRslt  = jsUserRslt.getRecord();
	 							
	 							sGrade 		= commUtils.nvl (jrUserRslt.getFieldString("GRADE"),"9");
	 							ydStkColGp 	= commUtils.trim(jrUserRslt.getFieldString("TAG_YD_STK_COL_GP"  ));
	 							ydStkBedNo 	= commUtils.trim(jrUserRslt.getFieldString("TAG_YD_STK_BED_NO"  ));
	 							ydStkLyrNo 	= commUtils.trim(jrUserRslt.getFieldString("TAG_YD_STK_LYR_NO"  ));	
	 							
	 							if ("001".equals(ydStkLyrNo)) { 
	 								/**************************************
	 								 *         적치 가능 check
	 						   		 **************************************/			
	 								//소재 1단일 경우 재검색 
	 								jrLocRuleApply.setField("WID_DIA_CHK_YN", "N");	
	 								JDTORecord jrLocAbleRtn1 = this.toLocAbleCheck(logId, mthdNm, jrUserRslt, jrRuleConst, jrLocRuleApply);
	 			
	 								sLocAbleRtn    = commUtils.trim(jrLocAbleRtn1.getFieldString("LOC_ABLE_RTN")) ;
	 								sLocAbleRtnMsg = commUtils.trim(jrLocAbleRtn1.getFieldString("LOC_ABLE_CONTENTS")) ;
	 								if (sLocAbleRtnMsg.length() > 0 ) {
	 									sDBLogMsg = sDBLogMsg + sLocAbleRtnMsg +"\r\n";
	 								}
	 								
	 								if ("1".equals(sLocAbleRtn)) {
	 									sLogMsg = ydStkColGp+ydStkBedNo+ ydStkLyrNo+"  적치가능 위치평점:"+ sGrade;
	 									commUtils.printLog(logId, sLogMsg, "SL");				
	 								    //적치가능 
	 									sRtnBedDan = ydStkColGp + ydStkBedNo + ydStkLyrNo;
	 									commUtils.printLog(logId, "▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲", "SL");
	 									
	 					    			break;
	 								}
	 							}	
	 						} //for 
	 						commUtils.printLog(logId, "▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲", "SL");
	 						
	 						if (sRtnBedDan.length() < 10) {
	 		 					sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 사용자지정 TO위치 결정 실패2 ";
	 		 					commUtils.printLog(logId, sLogMsg, "SL");
	 		 					if ("Y".equals(schLogYn)) {
	 								sSchLogContents = "[getYdToLocUser]대상코일선택실패:"+ sStlNo+" LOG :" +"\r\n" +  sDBLogMsg;
	 								this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
	 							}
	 		 					jrRtn.setField("RTN_CD" , "0");	
			 					jrRtn.setField("RTN_MSG", sDBLogMsg);
			 					return jrRtn;
	 						}	
	 						
	 						
	 					} else {
	    				
	 						/***************************************************
	 	    				** 제품인 경우
	 	    				****************************************************/		    				
		 					sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 사용자지정 TO위치 결정 실패 ";
		 					commUtils.printLog(logId, sLogMsg, "SL");
		 				
		 					if ("Y".equals(schLogYn)) {
		 						sSchLogContents = "[getYdToLocUser]사용자 지정 대상코일선택실패:"+ sStlNo+" LOG :"+"\r\n" + sDBLogMsg;
		 						this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
		 					}
		 					jrRtn.setField("RTN_CD" , "0");	
		 					jrRtn.setField("RTN_MSG", sDBLogMsg);
		 					return jrRtn;
	 					}	
	 				}
	    		} else {
					/***************************************************
    				** 기존 로직
    				****************************************************/
	 				if (sRtnBedDan.length() < 10) {
	 					sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 사용자지정 TO위치 결정 실패 ";
	 					commUtils.printLog(logId, sLogMsg, "SL");
	 				
	 					if ("Y".equals(schLogYn)) {
	 						sSchLogContents = "[getYdToLocUser]사용자 지정 대상코일선택실패:"+ sStlNo+" LOG :"+"\r\n" + sDBLogMsg;
	 						this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
	 					}
	 					jrRtn.setField("RTN_CD" , "0");	
	 					jrRtn.setField("RTN_MSG", sDBLogMsg);
	 					return jrRtn;		
	 				}
	    		}	
	    		
	    		
 				if ("Y".equals(schLogYn)) {
 					sSchLogContents = "[getYdToLocUser]대상코일선택:"+ sStlNo+"  선택위치:"+ sRtnBedDan + "  평점:" + sGrade +" LOG :"+"\r\n" + sDBLogMsg;
					this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
				}
     					
            }	 

			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = commUtils.getParam(logId, mthdNm, sModifier);
			jrSetLoc.setField("YD_CRN_SCH_ID"	, ydCrnSchId); 
			jrSetLoc.setField("YD_EQP_ID"		, ydEqpId);	 
			jrSetLoc.setField("YD_SCH_CD"		, ydSchCd);	 
			jrSetLoc.setField("YD_UP_WO_LOC"	, ydUpWoLoc); 
			jrSetLoc.setField("YD_UP_WO_LAYER"	, ydUpWoLayer);	 
			jrSetLoc.setField("YD_DN_WO_LOC"	, sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER"	, sRtnBedDan.substring(8));
			jrSetLoc.setField("YD_WBOOK_ID"		, ydWookId); 
				
			JDTORecord jrRcvRtn = this.toLocUpdCrnSch(logId,mthdNm, jrSetLoc, jrCrnSch  );
			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
			
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
	    }//end of try~catch				
	}      
  
        
	/**
	 * 설비SPM/HFL보급/TAKE-IN/재작업 
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord toLocUpConveyor(String logId, String mthdNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String mthdNm = "TO위치결정:설비SPM/HFL보급/TAKE-IN/재작업 [CCoilSchSeEJB.toLocUpConveyor] < " + mthdNms;
    	String sLogMsg = null;
    	JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();	//전문 Return
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------

		String ydSchCd 	   	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String ydWbookId	= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));		//작업예약
		String schLogYn 	= commUtils.trim(jrWbook.getFieldString("SCHLOG_YN"  ));	
		
		String sStlNo	   	= commUtils.trim(jrCrnSch.getFieldString("STL_NO"));			//크레인작업재료
		String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String ydEqpId     	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String ydUpWoLoc 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String ydUpWoLayer 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));
//		String ydBayGp  	= commUtils.trim(jrCrnSch.getFieldString("YD_SCH_CD").substring(1, 2)); // 동
		String sToLocGuide 	= commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_GUIDE"));
		String sModifier    = commUtils.trim(jrCrnSch.getFieldString("MODIFIER"));
		String sDBLogMsg 	= "";
		String sSchLogContents = "";
		
//==		Y5GetEqpLocSrchRngCoil
		try {
			commUtils.printLog(logId, mthdNm, "S+");

			String sRtnDnBedDan	= "";

			if ( "".equals(ydUpWoLoc) ) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			}
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_SCH_CD"		, ydSchCd);		//스케줄 코드
			jrParam.setField("YD_TO_LOC_GUIDE" 	, sToLocGuide);  
			
			sLogMsg =  " TOSQL"+ydCrnSchId+ " 권상재료["+sStlNo +"] 의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, sLogMsg, "SL");
			
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocEqpUp
			WITH P_TBL AS (
			SELECT :V_YD_SCH_CD       AS YD_SCH_CD 
			     , SUBSTR(:V_YD_TO_LOC_GUIDE,7,2) AS YD_STK_BED_NO
			  FROM DUAL
			)
			SELECT CASE  --TAKE IN
			            WHEN SUBSTR(YD_SCH_CD,3,4) = 'KE03'   AND SUBSTR(YD_SCH_CD,2,1) = 'A' THEN 'JAKE05'||YD_STK_BED_NO||'001'
			            WHEN SUBSTR(YD_SCH_CD,3,4) = 'KE03'   AND SUBSTR(YD_SCH_CD,2,1) = 'B' THEN 'JBKE04'||YD_STK_BED_NO||'001'
			            WHEN SUBSTR(YD_SCH_CD,3,4) = 'KE03'   AND SUBSTR(YD_SCH_CD,2,1) = 'C' THEN 'JCKE03'||YD_STK_BED_NO||'001'
			            WHEN SUBSTR(YD_SCH_CD,3,4) = 'KE03'   AND SUBSTR(YD_SCH_CD,2,1) = 'E' THEN 'JEKE02'||YD_STK_BED_NO||'001'
			            WHEN SUBSTR(YD_SCH_CD,3,4) = 'KE03'   AND SUBSTR(YD_SCH_CD,2,1) = 'H' THEN 'JHKE01'||YD_STK_BED_NO||'001'
			            WHEN SUBSTR(YD_SCH_CD,3,4) = 'FE03'   AND SUBSTR(YD_SCH_CD,2,1) = 'G' THEN 'JGFE01'||YD_STK_BED_NO||'001'
			            WHEN SUBSTR(YD_SCH_CD,3,4) = 'FE03'   AND SUBSTR(YD_SCH_CD,2,1) = 'C' THEN 'JCFE04'||YD_STK_BED_NO||'001'
			            --SPM 보급
			            WHEN SUBSTR(YD_SCH_CD,3,2) = 'KE'     AND SUBSTR(YD_SCH_CD,2,1) = 'A' THEN 'JAKE0500001'
			            WHEN SUBSTR(YD_SCH_CD,3,2) = 'KE'     AND SUBSTR(YD_SCH_CD,2,1) = 'B' THEN 'JBKE0400001'
			            WHEN SUBSTR(YD_SCH_CD,3,2) = 'KE'     AND SUBSTR(YD_SCH_CD,2,1) = 'C' THEN 'JCKE0300001'
			            WHEN SUBSTR(YD_SCH_CD,3,2) = 'KE'     AND SUBSTR(YD_SCH_CD,2,1) = 'E' THEN 'JEKE0200001'
			            WHEN SUBSTR(YD_SCH_CD,3,2) = 'KE'     AND SUBSTR(YD_SCH_CD,2,1) = 'H' THEN 'JHKE0100001'
			            --HFL 보급
			            WHEN SUBSTR(YD_SCH_CD,3,2) = 'FE'     AND SUBSTR(YD_SCH_CD,2,1) = 'C' THEN 'JCFE0400001'
			            WHEN SUBSTR(YD_SCH_CD,3,2) = 'FE'     AND SUBSTR(YD_SCH_CD,2,1) = 'G' THEN 'JGFE0100001'
			            --SPM 재보급
			            WHEN SUBSTR(YD_SCH_CD,3,6) = 'KD02LH' AND SUBSTR(YD_SCH_CD,2,1) = 'A' THEN 'JAKE0500001'
			            WHEN SUBSTR(YD_SCH_CD,3,6) = 'KD02LH' AND SUBSTR(YD_SCH_CD,2,1) = 'B' THEN 'JBKE0400001'
			            WHEN SUBSTR(YD_SCH_CD,3,6) = 'KD02LH' AND SUBSTR(YD_SCH_CD,2,1) = 'C' THEN 'JCKE0300001'
			            WHEN SUBSTR(YD_SCH_CD,3,6) = 'KD02LH' AND SUBSTR(YD_SCH_CD,2,1) = 'E' THEN 'JEKE0200001'
			            WHEN SUBSTR(YD_SCH_CD,3,6) = 'KD02LH' AND SUBSTR(YD_SCH_CD,2,1) = 'H' THEN 'JHKE0100001'
			            --SPM HFL보급
			            WHEN SUBSTR(YD_SCH_CD,3,6) = 'KD01UH' AND SUBSTR(YD_SCH_CD,2,1) = 'A' THEN 'JAKD0501001'
			            WHEN SUBSTR(YD_SCH_CD,3,6) = 'KD01UH' AND SUBSTR(YD_SCH_CD,2,1) = 'B' THEN 'JBKD0401001'
			            WHEN SUBSTR(YD_SCH_CD,3,6) = 'KD01UH' AND SUBSTR(YD_SCH_CD,2,1) = 'C' THEN 'JCKD0301001'
			            WHEN SUBSTR(YD_SCH_CD,3,6) = 'KD01UH' AND SUBSTR(YD_SCH_CD,2,1) = 'E' THEN 'JEKD0201001'
			            WHEN SUBSTR(YD_SCH_CD,3,6) = 'KD01UH' AND SUBSTR(YD_SCH_CD,2,1) = 'H' THEN 'JHKD0101001'
			            
			            ELSE '' END  AS RTN_BED_DAN   
			  FROM P_TBL
			*/
			JDTORecordSet jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocEqpUp", logId, mthdNm, "설비위 적치가능한 베드조회");
			
			if (jsResult.size() <= 0) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 TO위치 결정 실패 ";
				commUtils.printLog(logId, sLogMsg, "SL");
				
				if ("Y".equals(schLogYn)) {
					sSchLogContents = "[getYdToLocEqpUp]대상코일선택실패:"+ sStlNo+" LOG :"+"\r\n" + sDBLogMsg;
					this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
				}
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
				
			}
			jsResult.first();
			JDTORecord jrResult = jsResult.getRecord();
			sRtnDnBedDan  	= commUtils.trim(jrResult.getFieldString("RTN_BED_DAN"));			//TO위치
			if (sRtnDnBedDan.length() < 10) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 TO위치 결정 실패 ";
				commUtils.printLog(logId, sLogMsg, "SL");

				if ("Y".equals(schLogYn)) {
					sSchLogContents = "[getYdToLocEqpUp]대상코일선택실패:"+ sStlNo+" LOG :"+"\r\n" + sDBLogMsg;
					this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
				}
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;			
			}
		
			                                                                                              
			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = commUtils.getParam(logId, mthdNm, sModifier);
			jrSetLoc.setField("YD_CRN_SCH_ID", 	ydCrnSchId); 
			jrSetLoc.setField("YD_EQP_ID", 		ydEqpId);	 
			jrSetLoc.setField("YD_SCH_CD", 		ydSchCd);	 
			jrSetLoc.setField("YD_UP_WO_LOC", 	ydUpWoLoc); 
			jrSetLoc.setField("YD_UP_WO_LAYER",	ydUpWoLayer);	 
			jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnDnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER", sRtnDnBedDan.substring(8));
			jrSetLoc.setField("YD_WBOOK_ID", 	ydWbookId); 
				
			JDTORecord jrRcvRtn = this.toLocUpdCrnSch(logId,mthdNm, jrSetLoc, jrCrnSch  );
			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
			
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
	    }//end of try~catch				

	}      

	/**
	 * 주작업TO위치결정  -> 야드
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord toLocPrimaryWork(String logId, String mthdNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String mthdNm = "주작업TO위치결정[CCoilSchSeEJB.toLocPrimaryWork] < " + mthdNms;
    	String sLogMsg = null;
    	JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();	//전문 Return
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");

	    	String ydStkColGp   = "";
			String ydStkBedNo   = "";
			String ydStkLyrNo   = "";	
			String sRtnBedDan   = "";	
			String sDBLogMsg    = "";
			String sGrade 		= "9";
			
			String ydSchCd 	   	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
			String ydWbookId	= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));			//작업예약
			String schLogYn 	= commUtils.trim(jrWbook.getFieldString("SCHLOG_YN"  ));			

			String sStlNo	   	= commUtils.trim(jrCrnSch.getFieldString("STL_NO"));			//크레인작업재료
			String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
			String ydEqpId     	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
			String ydUpWoLoc 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
			String ydUpWoLayer 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));		
			String sModifier    = commUtils.trim(jrCrnSch.getFieldString("MODIFIER"));
			
			String ydRouteGp    = coilDao.getCoilYdRouteGp(logId,mthdNm,ydSchCd,sStlNo);       //검색조건 행선
			String sSchLogContents = "";
			String sTcMoveYn = "N";  // 대차 자동 출발 여부 (동간입고 처리)
			
			if ( "".equals(ydUpWoLoc) ) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			} 
			
			if ( "".equals(ydRouteGp) ) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 검색조건행선정보가 없습니다. ";
				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			} 
			
			// 수입 및 입고 대차
			if ( "*".equals(ydRouteGp) ) {
				sLogMsg = "스케쥴기동이 완료 되었습니다.";
				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			} 					
			
//			JDTORecord jrToLocPrimary = JDTORecordFactory.getInstance().create();			
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("STL_NO"			, sStlNo);		//권상 STOCK
			jrParam.setField("YD_SCH_CD"		, ydSchCd);		//스케줄 코드
			jrParam.setField("YD_EQP_ID"		, ydEqpId);		//설비ID
			jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);	//크레인 스케쥴 ID
			jrParam.setField("YD_UP_WO_LOC" 	, ydUpWoLoc);  
			jrParam.setField("YD_UP_WO_LAYER" 	, ydUpWoLayer);  
			jrParam.setField("YD_ROUTE_GP"  	, ydRouteGp);  
			
			sLogMsg =  " TOSQL"+ydCrnSchId+ " 권상재료["+sStlNo +" 의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, sLogMsg, "SL");
			
	    	//----------------------------------------------------------------------------------------------------------------------
			//	일반적인 적치가능한 베드 조회
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecordSet jsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			if ("H".equals(ydSchCd.substring(7, 8))) {
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocPrimaryWorkH
				 -- 대상 위치 SELECT
				WITH  TO_LOC_TABLE AS (
				    SELECT ABS(TO_NUMBER(SL.YD_STK_LYR_NO) - TO_NUMBER(CC.COIL_YD_STK_LYR_NO)) AS PRIOR4 -- LYR 우선
				         , X1.YD_SCH_CD
				         , CC.STL_NO
				         , SL.YD_STK_COL_GP         AS TAG_YD_STK_COL_GP
				         , SL.YD_STK_BED_NO         AS TAG_YD_STK_BED_NO
				         , SL.YD_STK_LYR_NO         AS TAG_YD_STK_LYR_NO
				         -- 열에 따른 코일 간격
				         , SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP)  AS INT
				         -- LEFT_BED
				         , DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
				                                    '002', SL.YD_STK_BED_NO)                               AS TAG_LEFT_BED
				         , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_LYR_NO,
				                                    '002', LPAD(TO_NUMBER(SL.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_LEFT_LAYER
				         -- RIGHT BED
				         , DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
				                                    '002', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'))
				                                                                                           AS TAG_RIGHT_BED
				         , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_LYR_NO,
				                                    '002', LPAD(TO_NUMBER(SL.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_RIGHT_LAYER
				         , SC.COL_OUTDIA_GRP_GP
				         , SC.COL_YD_LOC_GP
				         , SC.COL_W_GP
				         , SC.COL_YD_STK_SKID_GP
				         , CC.COIL_W
				         , CC.COIL_W_GP
				         , CC.COIL_YD_STK_COL_GP
				         , CC.COIL_YD_STK_BED_NO
				         , CC.COIL_YD_STK_LYR_NO
				         , CC.COIL_OUTDIA_GRP_GP
				         , X1.YD_LOC_SRCH_RNG_SEQ
				         , X1.YD_STK_BED_SRCH_SEQ
				         -- 적치가능여부 CHECK시 필요 : H/J/ABC/WRAP
				         , 'H' AS CHK_SKID_GP
				         , (CASE WHEN SL.YD_STK_COL_GP LIKE '__TC%' AND SL.YD_STK_BED_NO='01' THEN 1
				                 WHEN SL.YD_STK_COL_GP LIKE '__TC%' AND SL.YD_STK_BED_NO='02' THEN 3
				                 WHEN SL.YD_STK_COL_GP LIKE '__TC%' AND SL.YD_STK_BED_NO='03' THEN 2
				                 ELSE 0  END) AS SORT_TC
				         --공냉재광폭열         
				--         , (CASE WHEN X1.YD_SCH_CD LIKE 'J_YD05MH' AND CC.COIL_W > 1550 
				--                  AND SL.YD_STK_COL_GP IN ('JD4401','JD4402') THEN 1
				--                 WHEN X1.YD_SCH_CD LIKE 'J_YD05MH' AND CC.COIL_W > 1550 
				--                  AND SL.YD_STK_COL_GP IN ('JD4501','JD4502') THEN 2
				--                 WHEN X1.YD_SCH_CD LIKE 'J_YD06MH' AND CC.COIL_W > 1550 
				--                  AND SL.YD_STK_COL_GP IN ('JD4401','JD4402') THEN 1
				--                 WHEN X1.YD_SCH_CD LIKE 'J_YD06MH' AND CC.COIL_W > 1550 
				--                  AND SL.YD_STK_COL_GP IN ('JD4501','JD4502') THEN 2
				--                 ELSE 3 END ) AS SORT_COIL_W  
				         , (CASE WHEN X1.YD_SCH_CD LIKE 'J_YD05MH' AND CC.COIL_W < 1551
				                  AND SL.YD_STK_COL_GP IN ('JD4401','JD4402') THEN 3
				                 WHEN X1.YD_SCH_CD LIKE 'J_YD05MH' AND CC.COIL_W < 1551
				                  AND SL.YD_STK_COL_GP IN ('JD4501','JD4502') THEN 2
				                 WHEN X1.YD_SCH_CD LIKE 'J_YD06MH' AND CC.COIL_W < 1551
				                  AND SL.YD_STK_COL_GP IN ('JD4401','JD4402') THEN 3
				                 WHEN X1.YD_SCH_CD LIKE 'J_YD06MH' AND CC.COIL_W < 1551
				                  AND SL.YD_STK_COL_GP IN ('JD4501','JD4502') THEN 2
				                 ELSE 1 END ) AS SORT_COIL_W
				      FROM TB_YD_STKLYR SL
				         , ( SELECT YD_STK_COL_GP
				                  , YD_GP
				                  , YD_BAY_GP
				                  , YD_COIL_OUTDIA_GRP_GP  AS COL_OUTDIA_GRP_GP
				                  , MATL_SUP_MTD_GP
				                  , (CASE WHEN MATL_SUP_MTD_GP = 'A' THEN 0
				                          WHEN MATL_SUP_MTD_GP = 'B' THEN 1400
				                          ELSE 0                END) AS FROM_W_CHK
				                  , (CASE WHEN MATL_SUP_MTD_GP = 'A' THEN 1150
				                          WHEN MATL_SUP_MTD_GP = 'B' THEN 3000
				                          ELSE 3000             END) AS TO_W_CHK
				                  , YD_LOC_GP       AS COL_YD_LOC_GP
				                  , YD_STK_SKID_GP  AS COL_YD_STK_SKID_GP
				                  , YD_STK_COL_W_GP AS COL_W_GP
				               FROM TB_YD_STKCOL
				              WHERE DEL_YN = 'N'
				                AND YD_GP  = 'J'
				                AND SUBSTR(YD_STK_COL_GP,1,2) =  SUBSTR(:V_YD_SCH_CD,1,2)
				           ) SC
				         , (
				             SELECT SUBSTR(AA.YD_UP_WO_LOC,1,6)   AS COIL_YD_STK_COL_GP
				                  , SUBSTR(AA.YD_UP_WO_LOC,7,2)   AS COIL_YD_STK_BED_NO
				                  , AA.YD_UP_WO_LAYER             AS COIL_YD_STK_LYR_NO
				                  , CC.COIL_NO                    AS STL_NO
				                  , CASE WHEN CC.COIL_OUTDIA <= 1280 THEN 'A'
				                         WHEN CC.COIL_OUTDIA >  1930 THEN 'C'
				                         ELSE 'B' END             AS COIL_OUTDIA_GRP_GP
				                  , CC.COIL_W
				                  , CASE WHEN CC.COIL_W < 1601 THEN 'M'
				                         ELSE 'L' END             AS COIL_W_GP
				               FROM TB_YD_CRNSCH          AA
				                  , TB_YD_CRNWRKMTL       BB
				                  , USRPTA.TB_PT_COILCOMM CC
				              WHERE AA.YD_CRN_SCH_ID = BB.YD_CRN_SCH_ID
				                AND AA.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                AND BB.STL_NO        = CC.COIL_NO
				                AND AA.DEL_YN = 'N'
				                AND BB.DEL_YN = 'N'
				                AND ROWNUM = 1
				           ) CC
				         , (
				             SELECT A.YD_SCH_CD                AS YD_SCH_CD
				                  , A.YD_ROUTE_GP              AS YD_ROUTE_GP
				                  , A.YD_LOC_SRCH_RNG_REG_SNO  AS YD_LOC_SRCH_RNG_REG_SNO
				                  , A.YD_STR_GTR_CD            AS YD_STR_GTR_CD
				                  , A.YD_LOC_SRCH_RNG_SEQ      AS YD_LOC_SRCH_RNG_SEQ
				                  , B.YD_STK_BED_SRCH_SEQ      AS YD_STK_BED_SRCH_SEQ
				                  , B.YD_STK_COL_GP            AS YD_STK_COL_GP
				                  , B.YD_STK_BED_NO            AS YD_STK_BED_NO
				                  , (CASE WHEN B.YD_STK_COL_GP LIKE 'J_TC%' AND B.YD_STK_BED_NO='01' THEN 1
				                          WHEN B.YD_STK_COL_GP LIKE 'J_TC%' AND B.YD_STK_BED_NO='02' THEN 3
				                          WHEN B.YD_STK_COL_GP LIKE 'J_TC%' AND B.YD_STK_BED_NO='03' THEN 2
				                          ELSE 0
				                          END) AS SORT_TC
				               FROM TB_YD_LOCSRCHRNG A
				                  , TB_YD_LOCSRCHBED B
				              WHERE A.YD_LOC_SRCH_RNG_REG_SNO = B.YD_LOC_SRCH_RNG_REG_SNO
				                AND A.YD_SCH_CD               = :V_YD_SCH_CD
				                -- C동반입 HFL재(B3), SPM재(B4)
				                AND A.YD_ROUTE_GP             = (CASE WHEN A.YD_SCH_CD IN ('JCPT01LH','JCPT02LH')
				                                                      THEN (SELECT DECODE(A.NEXT_PROC, 'CH', 'B3', 'B4')
				                                                              FROM TB_PT_COILCOMM  A
				                                                                 , TB_YD_CRNWRKMTL B
				                                                             WHERE A.COIL_NO       = B.STL_NO
				                                                               AND B.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                                                               AND B.DEL_YN = 'N')
				                                                      ELSE :V_YD_ROUTE_GP
				                                                 END)
				                AND A.DEL_YN = 'N'
				                AND B.DEL_YN = 'N'
				          ) X1
				         , (SELECT ITEM1 AS TOT_YN FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP002' AND DEL_YN = 'N' AND ROWNUM = 1) YR
				     WHERE SL.YD_STK_COL_GP       = SC.YD_STK_COL_GP
				       AND SL.YD_STK_COL_GP       = X1.YD_STK_COL_GP
				       AND SL.YD_STK_BED_NO       = X1.YD_STK_BED_NO
				       AND SUBSTR(SL.YD_STK_COL_GP,1,2) = SUBSTR(CC.COIL_YD_STK_COL_GP,1,2) -- 야드동동일
				       AND SL.YD_STK_LYR_NO       IN ('001', '002')
				       AND SL.YD_STK_LYR_ACT_STAT = 'E'
				       AND SL.YD_STK_LYR_MTL_STAT = 'E'
				       AND SL.DEL_YN = 'N'
				       AND SL.YD_STK_BED_NO BETWEEN '00' AND '99'
				       -- 크레인 작업지시 제외
				       -- AND SL.YD_STK_COL_GP||SL.YD_STK_BED_NO||SL.YD_STK_LYR_NO NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YD_CRNSCH WHERE DEL_YN = 'N')
				       -- 작업예약 TO위치 가이드 제외
				       AND SL.YD_STK_COL_GP||SL.YD_STK_BED_NO NOT IN ( SELECT NVL(YD_TO_LOC_GUIDE,'1') FROM TB_YD_WRKBOOK WHERE DEL_YN = 'N')
				       -- 가상위치 제외
				       AND SUBSTR(SL.YD_STK_COL_GP,3,2) NOT IN ('01'  ,'80')
				--****  야드 통합여부  *****
				       AND 'Y' = CASE WHEN YR.TOT_YN = 'N' THEN
				                           CASE WHEN SC.COL_YD_LOC_GP = 'H' THEN 'Y' ELSE 'N' END
				                      ELSE 'Y' END
				       -- 동일외경군(소재위치 제외)
				       AND 'Y' = CASE WHEN SC.COL_YD_LOC_GP   = 'H' THEN 'Y'
				                      WHEN SC.COL_YD_LOC_GP   = 'J' AND  SC.COL_OUTDIA_GRP_GP = CC.COIL_OUTDIA_GRP_GP THEN 'Y'
				                      ELSE 'N' END
				        -- 동일폭구분(소재위치 제외)
				       AND 'Y' = CASE WHEN SC.COL_YD_LOC_GP   = 'H' THEN 'Y'
				                      WHEN SC.COL_YD_LOC_GP   = 'J' AND  SC.COL_W_GP = CC.COIL_W_GP THEN 'Y'
				                      WHEN SC.COL_YD_LOC_GP   = 'J'
				                       AND SC.MATL_SUP_MTD_GP IS NOT NULL
				                       AND CC.COIL_W BETWEEN SC.FROM_W_CHK AND SC.TO_W_CHK THEN 'Y'
				                  ELSE 'N' END
				       -- 공냉장 제외처리
				       AND 'Y' = CASE WHEN SC.COL_YD_LOC_GP = 'J' THEN
				                           CASE WHEN  SUBSTR(SL.YD_STK_COL_GP,1,4) NOT IN ('JA31','JA32','JB31','JC32')  THEN 'Y' ELSE 'N' END
				                      ELSE 'Y' END
				       -- 공냉재 광폭재/보폭재
				       AND 'Y' = CASE WHEN X1.YD_SCH_CD LIKE 'JDYD05MH' OR X1.YD_SCH_CD LIKE 'JDYD06MH'
				                      THEN CASE WHEN CC.COIL_W > 1550 AND SL.YD_STK_COL_GP IN ('JD4401','JD4402')
				                                THEN 'Y'
				                                WHEN CC.COIL_W < 1551
				                                THEN 'Y'
				                                ELSE 'N'
				                           END
				                      ELSE 'Y'
				                 END
				 )
				, TO_LOC_DATA_TABLE AS (
				-- TO위치 코일정보 SELECT
				SELECT A.PRIOR4
				     , A.STL_NO
				     , A.YD_SCH_CD
				     , NVL(B.YD_EQP_WRK_MODE2,'A') AS YD_EQP_WRK_MODE2 
				     , A.TAG_YD_STK_COL_GP
				     , A.TAG_YD_STK_BED_NO
				     , A.TAG_YD_STK_LYR_NO
				     , A.TAG_LEFT_BED
				     , A.TAG_LEFT_LAYER
				     , A.SORT_TC
				     , A.SORT_COIL_W
				     , A.YD_LOC_SRCH_RNG_SEQ
				     , A.YD_STK_BED_SRCH_SEQ
				     , A.COL_OUTDIA_GRP_GP
				     , A.COL_W_GP
				     , A.COL_YD_LOC_GP
				     , A.COL_YD_STK_SKID_GP
				     , A.COIL_OUTDIA_GRP_GP
				     , A.COIL_W_GP
				     , A.CHK_SKID_GP
				     , (SELECT MAX(YD_STK_BED_NO)
				          FROM TB_YD_STKLYR 
				         WHERE YD_STK_COL_GP = A.TAG_YD_STK_COL_GP AND ROWNUM = 1) AS COL_MAX_BED_NO
				     , (SELECT YD_STK_LYR_ACT_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_LEFT_ACTIVE_STAT
				     , (SELECT YD_STK_LYR_MTL_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_LEFT_LAYER_STAT
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_LEFT_STL_NO
				     , A.TAG_RIGHT_BED
				     , A.TAG_RIGHT_LAYER
				     , (SELECT YD_STK_LYR_ACT_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_RIGHT_ACTIVE_STAT
				     , (SELECT YD_STK_LYR_MTL_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_RIGHT_LAYER_STAT
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_RIGHT_STL_NO
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = '002'
				           AND B.DEL_YN = 'N') AS TAG_2DAN_LEFT_STL_NO
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_YD_STK_BED_NO
				           AND B.YD_STK_LYR_NO  = '002'
				           AND B.DEL_YN = 'N') AS TAG_2DAN_RIGHT_STL_NO
				  FROM TO_LOC_TABLE A
				--     , (SELECT YD_EQP_WRK_MODE2 FROM TB_YD_EQP WHERE YD_EQP_ID = :V_YD_EQP_ID) B
				     , (SELECT DECODE(ITEM1, 'Y', 'A', 'M') AS YD_EQP_WRK_MODE2 FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP100' AND CD_GP = :V_YD_EQP_ID) B
				), TO_LOC_DATA_COMP_TABLE AS (
				--*--*--*--*--*--*-- 적치가능위치
				SELECT *
				  FROM (
				          SELECT  
				--        SELECT (SELECT COUNT(*)
				--                  FROM TB_YD_STOCK A
				--                     , TB_YD_CARSCH B
				--                 WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
				--                   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
				--                   AND B.DEL_YN = 'N'
				--                   AND A.STL_NO = NVL(L_COIL_NO,'-')
				--                   AND ROWNUM <= 1) AS L_CAR_CHK
				--             , (SELECT COUNT(*)
				--                  FROM TB_YD_STOCK A
				--                     , TB_YD_CARSCH B
				--                 WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
				--                   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
				--                   AND B.DEL_YN = 'N'
				--                   AND A.STL_NO = NVL(R_COIL_NO,'-')
				--                   AND ROWNUM <= 1) AS R_CAR_CHK
				              KK.*
				          FROM
				               (
				                SELECT K.*
				                     , C.COIL_NO      AS C_COIL_NO
				                     , C.COIL_T       AS C_THICK
				                     , C.COIL_W       AS C_WIDTH
				                     , C.COIL_WT      AS C_WEIGTH
				                     , C.COIL_OUTDIA  AS C_OUTDIA
				                     , C.CURR_PROG_CD AS C_PROG_CD
				                     , C.ORD_NO       AS C_ORD_NO     -- 주문번호
				                     , C.ORD_DTL      AS C_ORD_DTL    -- 주문행번
				                     , C.DEMANDER_CD  AS C_DEMANDER_CD-- 수요가코드
				                     , (CASE WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
				                             WHEN C.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             ELSE '' END
				                       ) AS C_YEOJAE_CAUSE_CD
				                     , TRUNC((SYSDATE - C.HRMILL_CMPL_DT )*24)             AS C_HOT_COIL_MIN
				                     , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
				                            WHEN C.COIL_OUTDIA >  1930 THEN 'C'
				                            ELSE 'B' END  AS C_COIL_OUTDIA_GP
				                     , CASE WHEN C.COIL_W < 1601     THEN 'M' ELSE 'L' END AS C_COIL_W_GP
				                     , L.COIL_NO      AS L_COIL_NO
				                     , L.COIL_T       AS L_THICK
				                     , L.COIL_W       AS L_WIDTH
				                     , L.COIL_WT      AS L_WEIGTH
				                     , L.COIL_OUTDIA  AS L_OUTDIA
				                     , L.CURR_PROG_CD AS L_PROG_CD
				                     , L.ORD_NO       AS L_ORD_NO     -- 주문번호
				                     , L.ORD_DTL      AS L_ORD_DTL    -- 주문행번
				                     , L.DEMANDER_CD  AS L_DEMANDER_CD-- 수요가코드
				                     , (CASE WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
				                             WHEN L.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             ELSE '' END
				                       ) AS L_YEOJAE_CAUSE_CD
				                     , TRUNC((SYSDATE - L.HRMILL_CMPL_DT )*24) AS L_HOT_COIL_MIN
				                     , R.COIL_NO      AS R_COIL_NO
				                     , R.COIL_W       AS R_WIDTH
				                     , R.COIL_T       AS R_THICK
				                     , R.COIL_WT      AS R_WEIGTH
				                     , R.COIL_OUTDIA  AS R_OUTDIA
				                     , R.CURR_PROG_CD AS R_PROG_CD
				                     , R.ORD_NO       AS R_ORD_NO     -- 주문번호
				                     , R.ORD_DTL      AS R_ORD_DTL    -- 주문행번
				                     , R.DEMANDER_CD  AS R_DEMANDER_CD-- 수요가코드
				                     , (CASE WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
				                             WHEN R.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             ELSE '' END
				                       ) AS R_YEOJAE_CAUSE_CD
				                     , TRUNC((SYSDATE - R.HRMILL_CMPL_DT )*24) R_HOT_COIL_MIN
				                     , L.ISHOT        AS L_ISHOT
				                     , R.ISHOT        AS R_ISHOT
				                     , C.ISHOT        AS C_ISHOT
				                  FROM TO_LOC_DATA_TABLE K
				                     , (SELECT 1 T_ROW, A.*
				                             , CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 96
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 48
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                  END  AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의     
				                          FROM USRPTA.TB_PT_COILCOMM  A ) C  --대상코일
				                     , (SELECT 1 T_ROW, A.*
				                             , CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 96
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 48
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                  END  AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
				                          FROM USRPTA.TB_PT_COILCOMM  A ) L  --하단LEFT
				                     , (SELECT 1 T_ROW, A.*
				                             , CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 96
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 48
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                          END AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
				                          FROM USRPTA.TB_PT_COILCOMM  A ) R  --하단RIGHT
				                 WHERE K.STL_NO           = C.COIL_NO(+)
				                   AND K.TAG_LEFT_STL_NO  = L.COIL_NO(+)
				                   AND K.TAG_RIGHT_STL_NO = R.COIL_NO(+)
				                   --짱구코일 2단제외
				--                   AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND JJANG_GP = 'Y'                      THEN 0
				--                                ELSE 1 END
				                   --2단일 경우 1단에 핫코일이 존재 시 2단제의
				--                   AND 'Y' = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND  C.ISHOT ='TRUE' AND L.ISHOT ='TRUE' AND R.ISHOT='TRUE' THEN 'Y'
				--                                  WHEN TAG_YD_STK_LYR_NO = '002' AND (L.ISHOT ='TRUE' OR R.ISHOT='TRUE') THEN 'N'
				--                                  ELSE 'Y' END
				                   AND 'Y' = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND C.ISHOT ='TRUE' THEN CASE WHEN L.ISHOT ='TRUE' AND R.ISHOT='TRUE' THEN 'Y'
				                                                                                             ELSE 'N'END    
				                                ELSE 'Y' END
				--                   --2단일 경우 좌우 적치 상태 CHECK
				                   AND 'Y' = CASE WHEN TAG_YD_STK_LYR_NO = '002'
				                                       AND (TAG_LEFT_ACTIVE_STAT != 'E' OR TAG_RIGHT_ACTIVE_STAT != 'E'
				                                       OR TAG_LEFT_LAYER_STAT  != 'C' OR TAG_RIGHT_LAYER_STAT  != 'C') THEN 'N'
				                                  ELSE 'Y' END
				--                   --1단일 경우 좌우 상단 적치 상태 CHECK
				                   AND 'Y' = CASE WHEN K.TAG_YD_STK_LYR_NO = '001' AND (TAG_2DAN_LEFT_STL_NO IS NOT NULL OR TAG_2DAN_RIGHT_STL_NO IS NOT NULL) THEN 'N'
				                                  ELSE 'Y' END

				               ) KK
				                --무인크레인 이동가능 여부 CHECK
				--         WHERE ( TAG_YD_STK_COL_GP IN (SELECT R.ITEM
				--                                         FROM TB_YD_RULE      R
				--                                            , AUTO_CR_TABLE   A
				--                                        WHERE R.REPR_CD_GP = 'CR0001'
				--                                          AND R.CD_GP      = A.EQUIP_GP
				--                                          AND R.DEL_YN     = 'N')
				--               )
				       )
				)
				--*--*--*--*--*--*-- 평점
				SELECT G.*
				      , CASE  -- 1단이면서 소재인 경우
				            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
				            THEN CASE WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌우) 진도코드
				                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌단) 진도코드
				                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우단) 진도코드
				                      WHEN L_COIL_NO  IS NULL     AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
				                      WHEN R_COIL_NO  IS NULL     AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED
				                      ELSE '7' END
				             -- 2단이면서 소재인 경우
				            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
				            THEN CASE WHEN L_WB        = 'Y'      OR  R_WB       = 'Y'      THEN '9' --좌하단우하단 작업 예약
				                      WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌하단우하단) 진도코드
				                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌하단) 진도코드
				                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우하단) 진도코드
				                      ELSE '2' END
				            -- 1단이면서 제품인 경우
				            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8')
				            THEN CASE WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
				                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
				                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우축 동일 고객사+주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우측 동일 주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌측   동일 주문번호행번
				                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우측   동일 주문번호행번
				                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우측 동일 주문번호
				                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우측   동일 주문번호
				                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌측   동일 주문번호
				                      WHEN L_COIL_NO IS NULL      AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
				                      WHEN R_COIL_NO IS NULL      AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED

				                      ELSE '7' END
				            -- 2단이면서 제품인 경우
				            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8') --제품
				            THEN CASE WHEN L_WB = 'Y'             OR  R_WB  = 'Y'           THEN '9' --좌하단우하단 작업 예약
				                      WHEN L_PROG_CD IN ('6','L') OR  R_PROG_CD IN ('6','L')THEN '8' --상차지시
				                      WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
				                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
				                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 고객사+주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌하단   동일 주문번호행번
				                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우하단   동일 주문번호행번
				                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우하단 동일 주문번호
				                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우하단   동일 주문번호
				                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌하단   동일 주문번호
				                      ELSE '2' END

				             ELSE '7' END GRADE
				  FROM (SELECT A.*
				             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
				                  FROM TB_YD_WRKBOOK    A1
				                     , TB_YD_WRKBOOKMTL B1
				                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                   AND A1.DEL_YN      = 'N'
				                   AND B1.DEL_YN      = 'N'
				                   AND B1.STL_NO      = A.C_COIL_NO
				               ) C_WB
				             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
				                  FROM TB_YD_WRKBOOK    A1
				                     , TB_YD_WRKBOOKMTL B1
				                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                   AND A1.DEL_YN      = 'N'
				                   AND B1.DEL_YN      = 'N'
				                   AND B1.STL_NO      = A.L_COIL_NO
				               ) L_WB
				             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
				                  FROM TB_YD_WRKBOOK    A1
				                     , TB_YD_WRKBOOKMTL B1
				                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                   AND A1.DEL_YN      = 'N'
				                   AND B1.DEL_YN      = 'N'
				                   AND B1.STL_NO      = A.R_COIL_NO
				               ) R_WB
				          FROM TO_LOC_DATA_COMP_TABLE A
				       ) G
				 ORDER BY SORT_COIL_W --공냉재광폭열
				        , GRADE
				        , YD_LOC_SRCH_RNG_SEQ
				        , SORT_TC
				        , YD_STK_BED_SRCH_SEQ
				        , TAG_YD_STK_BED_NO
				        , PRIOR4 DESC
				*/
				jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocPrimaryWorkH", logId, mthdNm, "소재동일한 적치가능한 베드 조회");
			} else {
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocPrimaryWorkJ
				WITH TBL_COILWIDECHK AS (
				--적치폭 기준(1순위)
				SELECT A.*
				  FROM (
				        SELECT PT.STL_NO                AS STL_NO
				             , SL.YD_STK_COL_GP         AS YD_STK_COL_GP
				             , SL.YD_STK_BED_NO         AS YD_STK_BED_NO
				             , SL.YD_STK_LYR_NO         AS YD_STK_LYR_NO
				             , SL.YD_STK_LYR_MTL_STAT
				              --SKID 구분
				             , SC.YD_STK_SKID_GP        AS COL_YD_STK_SKID_GP
				              --SKID 외경구분
				             , SC.YD_COIL_OUTDIA_GRP_GP AS COL_OUTDIA_GRP_GP
				              --SKID 폭구분
				             , SC.YD_STK_COL_W_GP       AS COL_W_GP
				             , SC.YD_LOC_GP             AS COL_YD_LOC_GP
				             , 999                      AS YD_LOC_SRCH_RNG_SEQ
				             , 999                      AS YD_STK_BED_SRCH_SEQ
				             , PT.YD_SCH_CD             AS YD_SCH_CD
				             , 0                        AS SORT_TC
				             , PT.COIL_W                AS COIL_W
				             , (CASE WHEN SC.MATL_SUP_MTD_GP='A' THEN 0
				                     WHEN SC.MATL_SUP_MTD_GP='B' THEN 1400
				                     ELSE 0                END) AS FROM_CHK
				             , (CASE WHEN SC.MATL_SUP_MTD_GP='A' THEN 1150
				                     WHEN SC.MATL_SUP_MTD_GP='B' THEN 3000
				                     ELSE 3000             END) AS TO_CHK
				             , CASE WHEN SUBSTR(SL.YD_STK_COL_GP,2,1) IN('B','C') AND TO_NUMBER(SUBSTR(SL.YD_STK_COL_GP,3,2))<=40
				                        THEN 'FIRST'
				                    WHEN SUBSTR(SL.YD_STK_COL_GP,2,1) IN('B','C') AND TO_NUMBER(SUBSTR(SL.YD_STK_COL_GP,3,2))> 40
				                        THEN 'SECOND'
				                    ELSE 'ALL' END
				               AS BC_DONG_FLAG
				             , PT.YD_EQP_ID
				          FROM TB_YD_STKLYR SL
				             , TB_YD_STKCOL SC
				             , (
				                SELECT :V_YD_SCH_CD   AS YD_SCH_CD
				                     , :V_YD_EQP_ID   AS YD_EQP_ID
				                     , C.COIL_NO      AS STL_NO
				                     , C.COIL_W       AS COIL_W
				                     , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
				                            WHEN C.COIL_OUTDIA >  1930 THEN 'C'
				                            ELSE 'B' END
				                       AS COIL_OUTDIA_GRP_GP
				                  FROM TB_PT_COILCOMM C
				                 WHERE C.COIL_NO = :V_STL_NO
				                ) PT
				         WHERE SC.YD_GP = 'J'
				           AND SL.YD_STK_COL_GP = SC.YD_STK_COL_GP
				           AND SL.DEL_YN = 'N'
				           AND SC.DEL_YN = 'N'
				           AND SC.YD_EQP_GP     BETWEEN '01' AND '99'
				           AND SC.YD_LOC_GP ='J'
				           AND SUBSTR(SC.YD_STK_COL_GP,2,1) = SUBSTR(PT.YD_SCH_CD,2,1) --//동체크
				           AND SC.MATL_SUP_MTD_GP IS NOT NULL --//적치폭기준 0:1000~1100 , 1:1100~1199 , 4:1400~1499 , 5:1500~1600
				           AND SL.YD_STK_LYR_ACT_STAT = 'E'
				           AND SL.YD_STK_LYR_MTL_STAT = 'E'
				           AND SC.YD_STK_COL_TOLOC_STAT='Y'
				           AND NVL(SC.YD_COIL_OUTDIA_GRP_GP,'*') = PT.COIL_OUTDIA_GRP_GP
				     ) A
				 WHERE A.COIL_W BETWEEN FROM_CHK AND TO_CHK
				   AND CASE WHEN A.YD_EQP_ID IN ('JBCRB1','JBCRB2','JCCRC1','JCCRC2')
				            THEN 'FIRST'
				            WHEN A.YD_EQP_ID IN ('JBCRB3','JBCRB4','JCCRC3','JCCRC4')
				            THEN 'SECOND'
				            ELSE 'ALL' END
				       = A.BC_DONG_FLAG
				)
				, TO_COMM_TABLE AS (
				SELECT :V_YD_SCH_CD   AS YD_SCH_CD
				     , :V_YD_EQP_ID   AS YD_EQP_ID
				     , C.COIL_NO      AS STL_NO
				     , C.COIL_W       AS COIL_W
				     , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
				            WHEN C.COIL_OUTDIA >  1930 THEN 'C'
				            ELSE 'B' END
				       AS COIL_OUTDIA_GRP_GP
				     , CASE WHEN C.COIL_W < 1601 THEN 'M'
				            ELSE 'L' END
				       AS COIL_W_GP
				     --  지포장 여부
				     , (CASE WHEN DECODE(C.CURR_PROG_CD,'F','XX',B.WRAP_METHOD_CD)='EB'
				                  AND C.ITEMNAME_CD NOT IN ('HAP','HBP','HCP','HAS','HBS','HCS','HAT','HBT','HCT')
				                  AND C.ORD_YEOJAE_GP='1'
				             THEN 'Y' ELSE 'N' END)        AS G_PAKAGE_YN
				     --결로 엄격재
				     , NVL((SELECT 'Y' FROM USRYDA.VW_YD_CONDENSATIONMTL WHERE COIL_NO = C.COIL_NO ),'N') AS COND_YN
				     --결로 엄격재 EF동 여부
				     , NVL((SELECT 'Y' FROM USRYDA.VW_YD_CONDENSATION    WHERE STL_NO  = C.COIL_NO ),'N') AS CONDEF_YN
				     --결로 엄격재 A동 대차제어 여부
				     , NVL((SELECT (CASE WHEN (SELECT ITEM1
				                                 FROM TB_YD_RULE R
				                                WHERE R.REPR_CD_GP = 'J00004'
				                                  AND R.CD_GP      = SUBSTR(YD_EQP_ID,6,1)
				                               ) = 'Y' THEN RCPT_TCAR_AIM_BAY_GP ELSE 'X' END)
				              FROM TB_YD_EQP
				             WHERE YD_EQP_ID LIKE 'J_TC___M%'
				               AND RCPT_TCAR_AIM_BAY_GP='A'
				               AND YD_EQP_ID ='JX'||SUBSTR(:V_YD_SCH_CD,3,4) ),'X') AS AIM_BAY_GP
				  FROM TB_PT_COILCOMM C
				     , TB_PT_OSCOMM B
				 WHERE C.COIL_NO = :V_STL_NO
				   AND C.ORD_NO  = B.ORD_NO
				   AND C.ORD_DTL = B.ORD_DTL
				   AND ROWNUM = 1
				)
				, TO_LOC_TABLE AS (
				 -- 대상 위치 SELECT
				SELECT A.CHK
				     , A.STL_NO
				     , A.SORT_CHK
				     , A.YD_STK_COL_GP         AS TAG_YD_STK_COL_GP
				     , A.YD_STK_BED_NO         AS TAG_YD_STK_BED_NO
				     , A.YD_STK_LYR_NO         AS TAG_YD_STK_LYR_NO
				     , A.YD_STK_LYR_MTL_STAT
				     -- 열에 따른 코일 간격
				     , SF_YD_SKID_INTERVAL_GAP(A.YD_STK_COL_GP)  AS INT
				     , DECODE(A.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(A.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(A.YD_STK_COL_GP), 2, '0'),
				                               '002', A.YD_STK_BED_NO)                               AS TAG_LEFT_BED

				     , DECODE(A.YD_STK_LYR_NO, '001', A.YD_STK_LYR_NO,
				                               '002', LPAD(TO_NUMBER(A.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_LEFT_LAYER

				     , DECODE(A.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(A.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(A.YD_STK_COL_GP), 2, '0'),
				                               '002', LPAD(TO_NUMBER(A.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(A.YD_STK_COL_GP), 2, '0'))
				                                                                                     AS TAG_RIGHT_BED
				     , DECODE(A.YD_STK_LYR_NO, '001', A.YD_STK_LYR_NO,
				                               '002', LPAD(TO_NUMBER(A.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_RIGHT_LAYER
				     , A.YD_LOC_SRCH_RNG_SEQ
				     , A.YD_STK_BED_SRCH_SEQ
				     , A.YD_SCH_CD
				     , A.SORT_TC
				     , A.COL_OUTDIA_GRP_GP
				     , A.COL_W_GP
				     , A.COL_YD_STK_SKID_GP
				     , A.COL_YD_LOC_GP
				     , A.COIL_OUTDIA_GRP_GP
				     , A.COIL_W_GP
				     , A.COIL_W
				     --결로 엄격재 여부
				     , A.COND_YN
				     --결로 엄격재 EF동 여부
				     , A.CONDEF_YN
				     --결로 엄격재 A동 대차제어 여부
				     , A.AIM_BAY_GP
				     , A.G_PAKAGE_YN
				     , A.FROM_CHK
				     , A.TO_CHK
				     , A.CONDEN_YN
				     , A.TOT_YN
				     , 'J' AS CHK_SKID_GP

				  FROM (--적치폭 기준(1순위)
				        SELECT 1 AS CHK
				             , (CASE WHEN (PT.YD_SCH_CD LIKE 'J_TC__MM' OR PT.YD_SCH_CD LIKE 'J_TC__UM') THEN 2 ELSE 1 END ) AS SORT_CHK
				--             , PT.CONDEN_YN
				             , (CASE WHEN PT.YD_SCH_CD LIKE 'J_TC__MM' AND 'N'= (SELECT ITEM1 FROM TB_YD_RULE R WHERE R.REPR_CD_GP='J00004' AND R.CD_GP = SUBSTR(CW.YD_STK_COL_GP,6,1))
				                     THEN 'N'
				                     ELSE (CASE WHEN (SELECT 'Y' FROM VW_YD_CONDENSATIONMTL CO WHERE CO.COIL_NO = PT.STL_NO)='Y' AND CW.COIL_W<=1400 THEN 'Y' ELSE 'N' END)
				                 END ) AS CONDEN_YN
				             , PT.COND_YN
				             , PT.CONDEF_YN
				             , PT.AIM_BAY_GP
				             , PT.G_PAKAGE_YN
				             , PT.STL_NO
				             , PT.COIL_OUTDIA_GRP_GP
				             , PT.COIL_W_GP
				             , CW.YD_STK_COL_GP
				             , CW.YD_STK_BED_NO
				             , CW.YD_STK_LYR_NO
				             , CW.YD_STK_LYR_MTL_STAT
				              --SKID 구분
				             , CW.COL_YD_STK_SKID_GP
				              --SKID 외경구분
				             , CW.COL_OUTDIA_GRP_GP
				              --SKID 폭구분
				             , CW.COL_W_GP
				             , CW.COL_YD_LOC_GP
				             , CW.YD_LOC_SRCH_RNG_SEQ
				             , CW.YD_STK_BED_SRCH_SEQ
				             , CW.YD_SCH_CD
				             , CW.SORT_TC
				             , CW.COIL_W
				             , CW.FROM_CHK
				             , CW.TO_CHK
				             , YR.TOT_YN
				             , CW.BC_DONG_FLAG
				             , PT.YD_EQP_ID
				          FROM TBL_COILWIDECHK CW
				             , TO_COMM_TABLE  PT
				             , (SELECT ITEM1 AS TOT_YN FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP002' AND DEL_YN = 'N' AND ROWNUM = 1) YR
				        UNION  ALL
				        --검색조건(1순위)
				        SELECT /*+ USE_NL(X1 SL)
				               2 AS CHK
				             , 2 AS SORT_CHK
				--             , PT.CONDEN_YN
				             ,(CASE WHEN PT.YD_SCH_CD LIKE 'J_TC__MM' AND 'N'= (SELECT ITEM1 FROM TB_YD_RULE R WHERE R.REPR_CD_GP='J00004' AND R.CD_GP = SUBSTR(SL.YD_STK_COL_GP,6,1))
				                     THEN 'N'
				                     ELSE (CASE WHEN (SELECT 'Y' FROM VW_YD_CONDENSATIONMTL CO WHERE CO.COIL_NO =PT.STL_NO)='Y' AND PT.COIL_W<=1400 THEN 'Y' ELSE 'N' END)
				                 END ) AS CONDEN_YN
				             , PT.COND_YN
				             , PT.CONDEF_YN
				             , PT.AIM_BAY_GP
				             , PT.G_PAKAGE_YN
				             , PT.STL_NO
				             , PT.COIL_OUTDIA_GRP_GP
				             , PT.COIL_W_GP
				             , SL.YD_STK_COL_GP
				             , SL.YD_STK_BED_NO
				             , SL.YD_STK_LYR_NO
				             , SL.YD_STK_LYR_MTL_STAT
				              --SKID 구분
				             , SC.YD_STK_SKID_GP            AS COL_YD_STK_SKID_GP
				              --SKID 외경구분
				             , SC.YD_COIL_OUTDIA_GRP_GP     AS COL_OUTDIA_GRP_GP
				              --SKID 폭구분
				             , SC.YD_STK_COL_W_GP           AS COL_W_GP
				             , SC.YD_LOC_GP                 AS COL_YD_LOC_GP
				             , X1.YD_LOC_SRCH_RNG_SEQ
				             , X1.YD_STK_BED_SRCH_SEQ
				             , X1.YD_SCH_CD
				             , X1.SORT_TC
				             , PT.COIL_W
				             , 0            AS FROM_CHK
				             , 0            AS TO_CHK
				             , YR.TOT_YN
				             , SC.BC_DONG_FLAG
				             , PT.YD_EQP_ID
				          FROM TB_YD_STKLYR SL
				             , (SELECT A.*
				                     , CASE WHEN SUBSTR(A.YD_STK_COL_GP, 2, 1) IN ('B', 'C')
				                                 THEN CASE WHEN SUBSTR(A.YD_STK_COL_GP, 3, 2) BETWEEN '01' AND '99'
				                                                THEN CASE WHEN TO_NUMBER(SUBSTR(A.YD_STK_COL_GP, 3, 2)) <= 40
				                                                               THEN 'FIRST'
				                                                          ELSE 'SECOND'
				                                                     END
				                                           WHEN A.YD_STK_COL_GP LIKE 'J_TC05%' -- #5대차
				                                                THEN 'FIRST'
				                                           WHEN A.YD_STK_COL_GP LIKE 'J_K%'    -- #3,4SPM
				                                                THEN 'FIRST'
				                                           ELSE 'SECOND'
				                                      END
				                            ELSE 'ALL'
				                       END AS BC_DONG_FLAG
				                  FROM TB_YD_STKCOL A) SC
				             , TO_COMM_TABLE   PT
				             , (SELECT ITEM1 AS TOT_YN FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP002' AND DEL_YN = 'N' AND ROWNUM = 1) YR
				             , (SELECT A.YD_SCH_CD
				                     , A.YD_ROUTE_GP
				                     , A.YD_LOC_SRCH_RNG_REG_SNO
				                     , A.YD_LOC_SRCH_RNG_SEQ
				                     , B.YD_STK_BED_SRCH_SEQ
				                     , B.YD_STK_COL_GP
				                     , B.YD_STK_BED_NO
				                     , (CASE WHEN B.YD_STK_COL_GP LIKE 'J_TC%' AND B.YD_STK_BED_NO='01' THEN 1
				                             WHEN B.YD_STK_COL_GP LIKE 'J_TC%' AND B.YD_STK_BED_NO='02' THEN 3
				                             WHEN B.YD_STK_COL_GP LIKE 'J_TC%' AND B.YD_STK_BED_NO='03' THEN 2
				                             ELSE 0
				                         END) AS SORT_TC
				                  FROM TB_YD_LOCSRCHRNG A
				                     , TB_YD_LOCSRCHBED B
				                 WHERE A.YD_LOC_SRCH_RNG_REG_SNO = B.YD_LOC_SRCH_RNG_REG_SNO
				                   AND A.YD_SCH_CD               = :V_YD_SCH_CD
				                   AND A.YD_ROUTE_GP             = (CASE WHEN A.YD_SCH_CD LIKE 'J_FD01LM' THEN  'F0' ELSE :V_YD_ROUTE_GP END )
				                   AND A.DEL_YN = 'N'
				                   AND B.DEL_YN = 'N'
				                 ) X1
				         WHERE SC.YD_GP||'' = 'J'
				           AND SL.YD_STK_COL_GP = X1.YD_STK_COL_GP
				           AND SL.YD_STK_BED_NO = X1.YD_STK_BED_NO
				           AND SL.YD_STK_COL_GP = SC.YD_STK_COL_GP
				           AND SL.DEL_YN = 'N'
				           AND SC.DEL_YN = 'N'
				--           AND SC.YD_EQP_GP     BETWEEN '01' AND '99'
				           --****  야드 통합여부  *****
				           AND 'Y' = CASE WHEN YR.TOT_YN = 'N' THEN
				                               CASE WHEN SC.YD_LOC_GP = 'J' THEN 'Y' ELSE 'N' END
				                          ELSE 'Y'  END
				           -- 외경구분/폭구분 CEHCK (소재제외)
				           AND 'Y' = CASE WHEN SC.YD_LOC_GP = 'H' THEN 'Y'
				                          WHEN SUBSTR(SL.YD_STK_COL_GP,3,1) NOT IN ('1','8')
				                               AND NVL(SC.YD_COIL_OUTDIA_GRP_GP,'*')  = NVL(PT.COIL_OUTDIA_GRP_GP,'*')
				                               AND NVL(SC.YD_STK_COL_W_GP,'*')        = NVL(PT.COIL_W_GP,'*')
				                               THEN 'Y'
				                          WHEN SUBSTR(SL.YD_STK_COL_GP,3,1) IN ('1','8','C','D','F','K','M','P','S','T','G')
				                               THEN 'Y'
				                        -- 지포장은 외경/폭 구분 안함
				                          WHEN X1.YD_ROUTE_GP='G0' AND
				                               SL.YD_STK_COL_GP IN (SELECT YD_STK_COL_GP
				                                                      FROM TB_YD_LOCSRCHRNG A
				                                                         , TB_YD_LOCSRCHBED B
				                                                         , TB_YD_SCHRULE C
				                                                     WHERE A.YD_LOC_SRCH_RNG_REG_SNO=B.YD_LOC_SRCH_RNG_REG_SNO
				                                                       AND A.YD_SCH_CD=C.YD_SCH_CD
				                                                       AND A.YD_ROUTE_GP='G0'
				                                                       AND A.DEL_YN='N'
				                                                       AND C.CD_CONTENTS LIKE '%입고%'
				                                                       AND C.YD_GP IN('H','J')
				                                                     GROUP BY YD_STK_COL_GP
				                                                    )
				                               THEN 'Y'
				                          ELSE 'N' END
				           AND SL.YD_STK_LYR_ACT_STAT = 'E'
				           AND SL.YD_STK_LYR_MTL_STAT = 'E'
				           AND SC.YD_STK_COL_TOLOC_STAT = 'Y'
				           AND SC.MATL_SUP_MTD_GP IS NULL
				           AND CASE WHEN PT.YD_EQP_ID IN ('JBCRB1','JBCRB2','JCCRC1','JCCRC2')
				                    THEN 'FIRST'
				                    WHEN PT.YD_EQP_ID IN ('JBCRB3','JBCRB4','JCCRC3','JCCRC4')
				                    THEN 'SECOND'
				                    ELSE 'ALL' END
				              = SC.BC_DONG_FLAG
				           --적치폭 기준(1순위) 중복제외
				           AND X1.YD_STK_COL_GP NOT IN (SELECT YD_STK_COL_GP FROM TBL_COILWIDECHK)
				       ) A
				 WHERE 1 = 1
				   -- 크레인 작업지시 제외
				   -- AND YD_STK_COL_GP||YD_STK_BED_NO||YD_STK_LYR_NO NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YD_CRNSCH WHERE DEL_YN = 'N')
				   -- 작업예약 TO위치 가이드 제외
				   AND YD_STK_COL_GP||YD_STK_BED_NO NOT IN ( SELECT NVL(YD_TO_LOC_GUIDE,'1') FROM TB_YD_WRKBOOK WHERE DEL_YN = 'N')
				--****  야드 통합여부  *****
				   AND 'Y' = CASE WHEN TOT_YN = 'N' THEN
				                       CASE WHEN COL_YD_LOC_GP = 'J' THEN 'Y' ELSE 'N' END
				                  ELSE 'Y' END
				   --//공냉장 제외 처리
				   AND SUBSTR(YD_STK_COL_GP,1,4) NOT IN ('JA31','JA32','JB31','JC32')
				   --B,C동 지포장 적치 불가 지역 체크   --B동 13스판 3열부터 14스판 5열까지   --C동 13스판 6열부터 15스판 2열까지
				   AND 'Y' = CASE WHEN G_PAKAGE_YN = 'Y' AND YD_STK_COL_GP NOT IN ('JB1303','JB1304','JB1305','JB1306','JB1401','JB1402','JB1403','JB1404','JB1405',
				                                                                   'JC1306','JC1401','JC1402','JC1403','JC1404','JC1405','JC1406','JC1501','JC1502')
				                  THEN 'Y'
				                  WHEN G_PAKAGE_YN = 'N' THEN 'Y'
				                  ELSE 'N' END
				   --//엄격재 폭이 1400미만
				   AND 'Y' = CASE WHEN CONDEN_YN   = 'Y' AND SUBSTR(YD_STK_COL_GP,1,4) IN     ('JA50','JB13','JB14','JC14','JC15') THEN 'Y'
				              --//엄격재 아닌 경우
				                  WHEN CONDEN_YN   = 'N' AND SUBSTR(YD_STK_COL_GP,1,4) NOT IN ('JA50','JB13','JB14','JC14','JC15') THEN 'Y'
				                  ELSE 'N' END
				   --//대차 목적동이 A동이고 엄격재 인경우 대차 입고 제외
				   AND 'Y' = CASE WHEN AIM_BAY_GP  = 'X' THEN 'Y'
				                  WHEN AIM_BAY_GP  = 'A' AND (COND_YN='Y' OR CONDEF_YN='Y') AND YD_STK_COL_GP NOT LIKE '%TC%' THEN 'Y'
				                  WHEN AIM_BAY_GP  = 'A' AND COND_YN='N' AND CONDEF_YN='N' THEN 'Y'
				                  ELSE 'N' END
				   --//A동 결로엄격재를 출하 통로 주변에 적치 안되도록 개선 2020.02.17
				   AND 'Y' = CASE WHEN COND_YN = 'Y' AND YD_STK_COL_GP NOT IN ('JA5204','JA5205','JA5401','JA5402') THEN 'Y'
				                  WHEN COND_YN = 'N' THEN 'Y'
				                  ELSE 'N' END


				)

				, TO_LOC_DATA_TABLE AS (
				-- TO위치 코일정보 SELECT
				SELECT A.CHK
				     , A.STL_NO
				     , A.SORT_CHK
				     , B.YD_EQP_WRK_MODE2
				     , A.YD_SCH_CD
				     , A.TAG_YD_STK_COL_GP
				     , A.TAG_YD_STK_BED_NO
				     , A.TAG_YD_STK_LYR_NO
				     , A.TAG_LEFT_BED
				     , A.TAG_LEFT_LAYER
				     , A.SORT_TC
				     , A.YD_LOC_SRCH_RNG_SEQ
				     , A.YD_STK_BED_SRCH_SEQ
				     , (SELECT MAX(YD_STK_BED_NO)
				          FROM TB_YD_STKLYR WHERE YD_STK_COL_GP = A.TAG_YD_STK_COL_GP AND ROWNUM = 1) AS COL_MAX_BED_NO
				     , A.COL_OUTDIA_GRP_GP
				     , A.COL_W_GP
				     , A.COL_YD_LOC_GP
				     , A.COL_YD_STK_SKID_GP
				     , A.COIL_OUTDIA_GRP_GP
				     , A.COIL_W_GP
				     , A.CHK_SKID_GP
				     , A.TOT_YN
				     , (SELECT YD_STK_LYR_ACT_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_LEFT_ACTIVE_STAT
				     , (SELECT YD_STK_LYR_MTL_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_LEFT_LAYER_STAT
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_LEFT_STL_NO
				     , A.TAG_RIGHT_BED
				     , A.TAG_RIGHT_LAYER
				     , (SELECT YD_STK_LYR_ACT_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_RIGHT_ACTIVE_STAT
				     , (SELECT YD_STK_LYR_MTL_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_RIGHT_LAYER_STAT
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_RIGHT_STL_NO
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO   = '002'
				           AND B.DEL_YN = 'N' ) AS TAG_2DAN_LEFT_STL_NO
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO   = A.TAG_YD_STK_BED_NO
				           AND B.YD_STK_LYR_NO   = '002'
				           AND B.DEL_YN = 'N' ) AS TAG_2DAN_RIGHT_STL_NO
				  FROM TO_LOC_TABLE A
				     , (SELECT YD_EQP_WRK_MODE2 FROM TB_YD_EQP WHERE YD_EQP_ID = :V_YD_EQP_ID) B
				), TO_LOC_DATA_COMP_TABLE AS (
				--*--*--*--*--*--*-- 적치가능위치
				SELECT *
				  FROM (
				        SELECT (SELECT COUNT(*)
				                  FROM TB_YD_STOCK A
				                     , TB_YD_CARSCH B
				                 WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
				                   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
				                   AND B.DEL_YN = 'N'
				                   AND A.STL_NO = NVL(L_COIL_NO,'-')
				                   AND ROWNUM <= 1) AS L_CAR_CHK
				             , (SELECT COUNT(*)
				                  FROM TB_YD_STOCK A
				                     , TB_YD_CARSCH B
				                 WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
				                   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
				                   AND B.DEL_YN = 'N'
				                   AND A.STL_NO = NVL(R_COIL_NO,'-')
				                   AND ROWNUM <= 1) AS R_CAR_CHK
				             -- 1단우선적치 B군(폭1200이상,외경1800이상) C군(폭1200,외경1990이상)
				             , CASE WHEN C_COIL_OUTDIA_GP = 'B' AND C_WIDTH >= 1200 AND C_OUTDIA >= 1800 THEN '001'
				                    WHEN C_COIL_OUTDIA_GP = 'C' AND C_WIDTH >= 1200 AND C_OUTDIA >= 1990 THEN '001'
				                    ELSE '002'
				               END AS ABOVE_LYR
				             , KK.*
				          FROM
				               (
				                SELECT K.*
				                     , C.COIL_NO      AS C_COIL_NO
				                     , C.COIL_T       AS C_THICK
				                     , C.COIL_W       AS C_WIDTH
				                     , C.COIL_WT      AS C_WEIGTH
				                     , C.COIL_OUTDIA  AS C_OUTDIA
				                     , C.CURR_PROG_CD AS C_PROG_CD
				                     , C.ORD_NO       AS C_ORD_NO     -- 주문번호
				                     , C.ORD_DTL      AS C_ORD_DTL    -- 주문행번
				                     , C.DEMANDER_CD  AS C_DEMANDER_CD-- 수요가코드
				                     , (CASE WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
				                             WHEN C.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             ELSE '' END
				                       ) AS C_YEOJAE_CAUSE_CD
				                     , TRUNC((SYSDATE - C.HRMILL_CMPL_DT )*24)             AS C_HOT_COIL_MIN
				                     , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
				                            WHEN C.COIL_OUTDIA >  1930 THEN 'C'
				                            ELSE 'B' END  AS C_COIL_OUTDIA_GP
				                     , CASE WHEN C.COIL_W < 1601     THEN 'M' ELSE 'L' END AS C_COIL_W_GP
				                     , L.COIL_NO      AS L_COIL_NO
				                     , L.COIL_T       AS L_THICK
				                     , L.COIL_W       AS L_WIDTH
				                     , L.COIL_WT      AS L_WEIGTH
				                     , L.COIL_OUTDIA  AS L_OUTDIA
				                     , L.CURR_PROG_CD AS L_PROG_CD
				                     , L.ORD_NO       AS L_ORD_NO     -- 주문번호
				                     , L.ORD_DTL      AS L_ORD_DTL    -- 주문행번
				                     , L.DEMANDER_CD  AS L_DEMANDER_CD-- 수요가코드
				                     , (CASE WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
				                             WHEN L.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             ELSE '' END
				                       ) AS L_YEOJAE_CAUSE_CD
				                     , TRUNC((SYSDATE - L.HRMILL_CMPL_DT )*24) AS L_HOT_COIL_MIN
				                     , R.COIL_NO      AS R_COIL_NO
				                     , R.COIL_W       AS R_WIDTH
				                     , R.COIL_T       AS R_THICK
				                     , R.COIL_WT      AS R_WEIGTH
				                     , R.COIL_OUTDIA  AS R_OUTDIA
				                     , R.CURR_PROG_CD AS R_PROG_CD
				                     , R.ORD_NO       AS R_ORD_NO     -- 주문번호
				                     , R.ORD_DTL      AS R_ORD_DTL    -- 주문행번
				                     , R.DEMANDER_CD  AS R_DEMANDER_CD-- 수요가코드
				                     , (CASE WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
				                             WHEN R.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             ELSE '' END
				                       ) AS R_YEOJAE_CAUSE_CD
				                     , TRUNC((SYSDATE - R.HRMILL_CMPL_DT )*24) R_HOT_COIL_MIN
				                     , L.ISHOT        AS L_ISHOT
				                     , R.ISHOT        AS R_ISHOT
				                  FROM TO_LOC_DATA_TABLE K
				                     , (SELECT 1 T_ROW, A.*
				                          FROM USRPTA.TB_PT_COILCOMM  A ) C  --대상코일
				                     , (SELECT 1 T_ROW, A.*
				                          ,DECODE(NVL(DECODE(STL_APPEAR_GP,'Y','CG','CM'),'CM'),'CM',
				                                  CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 96
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 48
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                  END) AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
				                          FROM USRPTA.TB_PT_COILCOMM  A ) L  --하단LEFT
				                     , (SELECT 1 T_ROW, A.*
				                             ,DECODE(NVL(DECODE(STL_APPEAR_GP,'Y','CG','CM'),'CM'),'CM',
				                                          CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 96
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 48
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                          END) AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
				                          FROM USRPTA.TB_PT_COILCOMM  A ) R  --하단RIGHT
				                 WHERE K.STL_NO           = C.COIL_NO(+)
				                   AND K.TAG_LEFT_STL_NO  = L.COIL_NO(+)
				                   AND K.TAG_RIGHT_STL_NO = R.COIL_NO(+)
				                   --짱구코일 2단제외
				--                   AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND JJANG_GP = 'Y'                      THEN 0
				--                                ELSE 1 END
				                   --2단일 경우 1단에 핫코일이 존재 시 2단제의
				                   AND 'Y' = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND (L.ISHOT ='TRUE' OR R.ISHOT='TRUE') THEN 'N'
				                                  ELSE 'Y' END
				--                   --2단일 경우 좌우 적치 상태 CHECK
				                   AND 'Y' = CASE WHEN TAG_YD_STK_LYR_NO = '002'
				                                       AND (TAG_LEFT_ACTIVE_STAT != 'E' OR TAG_RIGHT_ACTIVE_STAT != 'E'
				                                       OR TAG_LEFT_LAYER_STAT  != 'C' OR TAG_RIGHT_LAYER_STAT  != 'C') THEN 'N'
				                                  ELSE 'Y' END
				--                   --1단일 경우 좌우 상단 적치 상태 CHECK
				                   AND 'Y' = CASE WHEN K.TAG_YD_STK_LYR_NO = '001' AND (TAG_2DAN_LEFT_STL_NO IS NOT NULL OR TAG_2DAN_RIGHT_STL_NO IS NOT NULL) THEN 'N'
				                                  ELSE 'Y' END

				               ) KK
				                   --무인크레인 이동가능 여부 CHECK
				--         WHERE ( TAG_YD_STK_COL_GP IN (SELECT R.ITEM
				--                                         FROM TB_YD_RULE      R
				--                                            , AUTO_CR_TABLE   A
				--                                        WHERE R.REPR_CD_GP = 'CR0001'
				--                                          AND R.CD_GP      = A.EQUIP_GP
				--                                          AND R.DEL_YN     = 'N')
				--               )
				       )
				)
				--*--*--*--*--*--*-- 평점
				SELECT G.*
				     , CASE  -- 1단이면서 소재인 경우
				            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
				            THEN CASE WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌우) 진도코드
				                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌단) 진도코드
				                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우단) 진도코드
				                      WHEN L_COIL_NO  IS NULL     AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
				                      WHEN R_COIL_NO  IS NULL     AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED
				                      ELSE '7' END
				             -- 2단이면서 소재인 경우
				            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
				            THEN CASE WHEN L_WB        = 'Y'      OR  R_WB       = 'Y'      THEN '8' --좌하단우하단 작업 예약
				                      WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌하단우하단) 진도코드
				                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌하단) 진도코드
				                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우하단) 진도코드
				                      ELSE '2' END
				            -- 1단이면서 제품인 경우
				            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8')
				            THEN CASE WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
				                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
				                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우축 동일 고객사+주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우측 동일 주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌측   동일 주문번호행번
				                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우측   동일 주문번호행번
				                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우측 동일 주문번호
				                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우측   동일 주문번호
				                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌측   동일 주문번호
				                      WHEN L_COIL_NO IS NULL      AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
				                      WHEN R_COIL_NO IS NULL      AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED

				                      ELSE '7' END
				            -- 2단이면서 제품인 경우
				            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8') --제품
				            THEN CASE WHEN L_WB = 'Y'             OR  R_WB  = 'Y'           THEN '8' --좌하단우하단 작업 예약
				                      WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
				                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
				                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 고객사+주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌하단   동일 주문번호행번
				                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우하단   동일 주문번호행번
				                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우하단 동일 주문번호
				                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우하단   동일 주문번호
				                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌하단   동일 주문번호
				                      ELSE '2' END

				             ELSE '9' END GRADE
				  FROM (SELECT A.*
				             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
				                  FROM TB_YD_WRKBOOK    A1
				                     , TB_YD_WRKBOOKMTL B1
				                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                   AND A1.DEL_YN      = 'N'
				                   AND B1.DEL_YN      = 'N'
				                   AND B1.STL_NO      = A.C_COIL_NO
				               ) C_WB
				             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
				                  FROM TB_YD_WRKBOOK    A1
				                     , TB_YD_WRKBOOKMTL B1
				                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                   AND A1.DEL_YN      = 'N'
				                   AND B1.DEL_YN      = 'N'
				                   AND B1.STL_NO      = A.L_COIL_NO
				               ) L_WB
				             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
				                  FROM TB_YD_WRKBOOK    A1
				                     , TB_YD_WRKBOOKMTL B1
				                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                   AND A1.DEL_YN      = 'N'
				                   AND B1.DEL_YN      = 'N'
				                   AND B1.STL_NO      = A.R_COIL_NO
				               ) R_WB
				          FROM TO_LOC_DATA_COMP_TABLE A
				       ) G
				 ORDER BY SORT_CHK
				        , YD_LOC_SRCH_RNG_SEQ  --동간입고시 대차 먼저 선정처리
				        , SORT_TC
				        , GRADE
				        , (CASE WHEN ABOVE_LYR = TAG_YD_STK_LYR_NO THEN 1 ELSE 2 END) -- 1단우선적치 B군(폭1200이상,외경1800이상) C군(폭1200,외경1990이상)
				        , YD_STK_BED_SRCH_SEQ
				        , TAG_YD_STK_BED_NO
				        , TAG_YD_STK_LYR_NO
				*/        
				jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocPrimaryWorkJ", logId, mthdNm, "제품동일한 적치가능한 베드 조회");
				
				/*******************************/
				/* TO위치 검색실패 시 추가 로직         */
				/*******************************/
				if(jsResult.size() <= 0) {
					/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocPrimaryWorkThirdJ
					WITH PARAM_TBL AS (
					SELECT :V_YD_SCH_CD      AS YD_SCH_CD
					     , :V_YD_ROUTE_GP    AS YD_ROUTE_GP
					     , :V_YD_EQP_ID      AS YD_EQP_ID
					     , C.COIL_NO         AS STL_NO
					     , C.COIL_W          AS COIL_W
					     , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
					            WHEN C.COIL_OUTDIA >  1930 THEN 'C'
					            ELSE 'B' END AS COIL_OUTDIA_GRP_GP
					     , CASE WHEN C.COIL_W < 1601 THEN 'M'
					            ELSE 'L' END AS COIL_W_GP
					  FROM TB_PT_COILCOMM C
					 WHERE C.COIL_NO = :V_STL_NO
					)
					, TO_LOC_ALL AS (
					SELECT X.STL_NO
					     , X.YD_STK_COL_GP         AS TAG_YD_STK_COL_GP
					     , X.YD_STK_BED_NO         AS TAG_YD_STK_BED_NO
					     , Z.YD_STK_LYR_NO         AS TAG_YD_STK_LYR_NO
					     , X.YD_STK_BED_WHIO_STAT
					     , X.YD_STK_BED_ACT_STAT
					     , X.YD_STK_BED_USG_GP
					     , X.COL_OUTDIA_GRP_GP
					     , X.COL_W_GP
					     , X.COL_YD_LOC_GP
					     , X.COL_YD_STK_SKID_GP
					     , X.COIL_OUTDIA_GRP_GP
					     , X.COIL_W_GP
					     , X.YD_SCH_CD
					     -- 열에 따른 코일 간격
					     , SF_YD_SKID_INTERVAL_GAP(X.YD_STK_COL_GP)  AS INT
					     , DECODE(Z.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(Z.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(Z.YD_STK_COL_GP), 2, '0'),
					                               '002', Z.YD_STK_BED_NO)                               AS TAG_LEFT_BED

					     , DECODE(Z.YD_STK_LYR_NO, '001', Z.YD_STK_LYR_NO,
					                               '002', LPAD(TO_NUMBER(Z.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_LEFT_LAYER

					     , DECODE(Z.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(Z.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(Z.YD_STK_COL_GP), 2, '0'),
					                               '002', LPAD(TO_NUMBER(Z.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(Z.YD_STK_COL_GP), 2, '0'))
					                                                                                       AS TAG_RIGHT_BED
					     , DECODE(Z.YD_STK_LYR_NO, '001', Z.YD_STK_LYR_NO,
					                               '002', LPAD(TO_NUMBER(Z.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_RIGHT_LAYER
					     -- 적치가능여부 CHECK시 필요 :H/J/ABC/WRAP
					     , 'J' AS CHK_SKID_GP
					  FROM (
					        SELECT C.YD_SCH_CD   AS YD_SCH_CD
					             , 'A1'          AS YD_ROUTE_GP
					             , C.STL_NO      AS STL_NO
					             , A.YD_GP
					             , A.YD_BAY_GP
					             , A.YD_STK_COL_NO
					             , A.YD_STK_COL_GP
					             , A.YD_STKBED_USG_CD
					             , B.YD_STK_BED_NO
					             , B.YD_STK_BED_USG_GP
					             , B.YD_STK_BED_ACT_STAT
					             , B.YD_STK_BED_WHIO_STAT
					             , B.YD_STK_BED_W_GP
					             , A.YD_COIL_OUTDIA_GRP_GP  AS COL_OUTDIA_GRP_GP
					             , A.YD_STK_SKID_GP         AS COL_YD_STK_SKID_GP
					             , A.YD_STK_COL_W_GP        AS COL_W_GP
					             , A.YD_LOC_GP              AS COL_YD_LOC_GP
					             , C.COIL_OUTDIA_GRP_GP
					             , C.COIL_W_GP
					             , CASE WHEN SUBSTR(A.YD_STK_COL_GP,2,1) IN('B','C') AND TO_NUMBER(SUBSTR(A.YD_STK_COL_GP,3,2))<=40
					                         THEN 'FIRST'
					                    WHEN SUBSTR(A.YD_STK_COL_GP,2,1) IN('B','C') AND TO_NUMBER(SUBSTR(A.YD_STK_COL_GP,3,2))> 40
					                         THEN 'SECOND'
					                    ELSE 'ALL' END
					               AS BC_DONG_FLAG
					             , C.YD_EQP_ID
					          FROM TB_YD_STKCOL A
					             , TB_YD_STKBED B
					             , PARAM_TBL    C
					         WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
					           AND A.YD_GP         = SUBSTR(C.YD_SCH_CD, 1, 1)
					           AND A.YD_BAY_GP     = SUBSTR(C.YD_SCH_CD, 2, 1)
					           AND A.YD_EQP_GP     BETWEEN '01' AND '99'
					           AND A.YD_EQP_GP NOT IN ('01','80')
					           AND A.DEL_YN = 'N'
					           AND B.DEL_YN = 'N'
					        MINUS
					        SELECT C.YD_SCH_CD   AS YD_SCH_CD
					             , C.YD_ROUTE_GP AS YD_ROUTE_GP
					             , C.STL_NO      AS STL_NO
					             , A.YD_GP
					             , A.YD_BAY_GP
					             , A.YD_STK_COL_NO
					             , A.YD_STK_COL_GP
					             , A.YD_STKBED_USG_CD
					             , B.YD_STK_BED_NO
					             , B.YD_STK_BED_USG_GP
					             , B.YD_STK_BED_ACT_STAT
					             , B.YD_STK_BED_WHIO_STAT
					             , B.YD_STK_BED_W_GP
					             , A.YD_COIL_OUTDIA_GRP_GP  AS COL_OUTDIA_GRP_GP
					             , A.YD_STK_SKID_GP         AS COL_YD_STK_SKID_GP
					             , A.YD_STK_COL_W_GP        AS COL_W_GP
					             , A.YD_LOC_GP              AS COL_YD_LOC_GP
					             , C.COIL_OUTDIA_GRP_GP
					             , C.COIL_W_GP
					             , CASE WHEN SUBSTR(A.YD_STK_COL_GP,2,1) IN('B','C') AND TO_NUMBER(SUBSTR(A.YD_STK_COL_GP,3,2))<=40
					                        THEN 'FIRST'
					                    WHEN SUBSTR(A.YD_STK_COL_GP,2,1) IN('B','C') AND TO_NUMBER(SUBSTR(A.YD_STK_COL_GP,3,2))> 40
					                        THEN 'SECOND'
					                    ELSE 'ALL' END
					               AS BC_DONG_FLAG
					             , C.YD_EQP_ID
					          FROM TB_YD_STKCOL A
					             , TB_YD_STKBED B
					             , PARAM_TBL    C
					         WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
					           AND A.YD_GP         = SUBSTR(C.YD_SCH_CD, 1, 1)
					           AND A.YD_BAY_GP     = SUBSTR(C.YD_SCH_CD, 2, 1)
					           AND A.YD_EQP_GP     BETWEEN '01' AND '99'
					           AND A.DEL_YN        = 'N'
					           AND B.DEL_YN        = 'N'
					           AND A.YD_STKBED_USG_CD = DECODE(C.YD_ROUTE_GP
					                                             , 'A1' ,'T'
					                                             , 'A2' ,'Y'
					                                             , 'A3' ,'S'
					                                             , 'D0' ,'J' )
					       ) X
					     , TB_YD_STKLYR Z
					     , TB_YD_STKCOL C
					 WHERE Z.YD_STK_COL_GP = X.YD_STK_COL_GP
					   AND Z.YD_STK_BED_NO = X.YD_STK_BED_NO
					   AND Z.YD_STK_COL_GP = C.YD_STK_COL_GP
					   AND C.YD_LOC_GP     = 'J'
					   AND C.YD_EQP_GP     NOT IN ('01', '80')
					   AND ( (
					            (NVL(X.COL_W_GP, '*') = NVL(X.COIL_W_GP, '*'))  AND  (NVL(X.COL_OUTDIA_GRP_GP, '*') = NVL(X.COIL_OUTDIA_GRP_GP, '*'))
					         )
					         OR
					         (
					            X.YD_ROUTE_GP = 'G0' AND
					                 X.YD_STK_COL_GP IN (SELECT YD_STK_COL_GP
					                                       FROM USRYDA.TB_YD_LOCSRCHRNG A
					                                          , USRYDA.TB_YD_LOCSRCHBED B
					                                          , USRYDA.TB_YD_SCHRULE C
					                                      WHERE A.YD_LOC_SRCH_RNG_REG_SNO = B.YD_LOC_SRCH_RNG_REG_SNO
					                                        AND A.YD_SCH_CD = C.YD_SCH_CD
					                                        AND YD_ROUTE_GP = 'G0'
					                                        AND A.DEL_YN    = 'N'
					                                        AND CD_CONTENTS LIKE '%입고%'
					                                        AND C.YD_GP     = 'Y'
					                                      GROUP BY YD_STK_COL_GP
					                                    )
					         )
					       )
					   AND Z.YD_STK_LYR_ACT_STAT   = (CASE WHEN SUBSTR(X.YD_SCH_CD, 3, 2) = 'HC' THEN 'S' ELSE 'E' END)
					   AND Z.YD_STK_LYR_MTL_STAT   = 'E'
					   AND Z.DEL_YN                = 'N'
					   AND C.YD_STK_COL_TOLOC_STAT = 'Y'
					   AND C.MATL_SUP_MTD_GP       IS NULL --//적치폭기준 0:1000~1100 , 1:1100~1199 , 4:1400~1499 , 5:1500~1600
					   AND SUBSTR(X.YD_STK_COL_GP, 3, 1) NOT IN ('6', '7')
					   AND SUBSTR(X.YD_STK_COL_GP, 1, 4) NOT IN ('JA31', 'JA32', 'JB31', 'JC32')  --//공냉장 제외 처리
					   AND CASE WHEN X.YD_EQP_ID IN ('JBCRB1','JBCRB2','JCCRC1','JCCRC2')
					            THEN 'FIRST'
					            WHEN X.YD_EQP_ID IN ('JBCRB3','JBCRB4','JCCRC3','JCCRC4')
					            THEN 'SECOND'
					            ELSE 'ALL' END
					       = BC_DONG_FLAG
					)
					, TO_LOC_DATA_TABLE AS (
					SELECT A.STL_NO
					     , B.YD_EQP_WRK_MODE2
					     , A.TAG_YD_STK_COL_GP
					     , A.TAG_YD_STK_BED_NO
					     , A.TAG_YD_STK_LYR_NO
					     , (SELECT MAX(YD_STK_BED_NO) FROM TB_YD_STKLYR WHERE YD_STK_COL_GP = A.TAG_YD_STK_COL_GP AND ROWNUM = 1) AS COL_MAX_BED_NO
					     , A.COL_OUTDIA_GRP_GP
					     , A.COL_W_GP
					     , A.COL_YD_LOC_GP
					     , A.COL_YD_STK_SKID_GP
					     , A.COIL_OUTDIA_GRP_GP
					     , A.COIL_W_GP
					     , A.CHK_SKID_GP
					     , A.TAG_LEFT_BED
					     , A.TAG_LEFT_LAYER
					     , (SELECT YD_STK_LYR_ACT_STAT
					          FROM TB_YD_STKLYR B
					         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
					           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
					           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
					           AND B.DEL_YN = 'N') AS TAG_LEFT_ACTIVE_STAT
					     , (SELECT YD_STK_LYR_MTL_STAT
					          FROM TB_YD_STKLYR B
					         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
					           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
					           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
					           AND B.DEL_YN = 'N') AS TAG_LEFT_LAYER_STAT
					     , (SELECT STL_NO
					          FROM TB_YD_STKLYR B
					         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
					           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
					           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
					           AND B.DEL_YN = 'N') AS TAG_LEFT_STL_NO
					     , A.TAG_RIGHT_BED
					     , A.TAG_RIGHT_LAYER
					     , (SELECT YD_STK_LYR_ACT_STAT
					          FROM TB_YD_STKLYR B
					         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
					           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
					           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
					           AND B.DEL_YN = 'N') AS TAG_RIGHT_ACTIVE_STAT
					     , (SELECT YD_STK_LYR_MTL_STAT
					          FROM TB_YD_STKLYR B
					         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
					           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
					           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
					           AND B.DEL_YN = 'N') AS TAG_RIGHT_LAYER_STAT
					     , (SELECT STL_NO
					          FROM TB_YD_STKLYR B
					         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
					           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
					           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
					           AND B.DEL_YN = 'N') AS TAG_RIGHT_STL_NO
					     , (SELECT STL_NO
					          FROM TB_YD_STKLYR B
					         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
					           AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
					           AND B.YD_STK_LYR_NO   = '002'
					           AND B.DEL_YN = 'N' ) AS TAG_2DAN_LEFT_STL_NO
					     , (SELECT STL_NO
					          FROM TB_YD_STKLYR B
					         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
					           AND B.YD_STK_BED_NO   = A.TAG_YD_STK_BED_NO
					           AND B.YD_STK_LYR_NO   = '002'
					           AND B.DEL_YN = 'N' ) AS TAG_2DAN_RIGHT_STL_NO
					 FROM TO_LOC_ALL A
					    , (SELECT YD_EQP_WRK_MODE2 FROM TB_YD_EQP WHERE YD_EQP_ID = :V_YD_EQP_ID) B
					)
					, TO_LOC_DATA_COMP_TABLE AS (
					--*--*--*--*--*--*-- 적치가능위치
					SELECT *
					  FROM (
					        SELECT
					               -- 1단우선적치 B군(폭1200이상,외경1800이상) C군(폭1200,외경1990이상)
					               CASE WHEN C_COIL_OUTDIA_GP = 'B' AND C_WIDTH >= 1200 AND C_OUTDIA >= 1800 THEN '001'
					                    WHEN C_COIL_OUTDIA_GP = 'C' AND C_WIDTH >= 1200 AND C_OUTDIA >= 1990 THEN '001'
					                    ELSE '002'
					               END AS ABOVE_LYR
					             , KK.*
					          FROM
					               (
					                SELECT K.*
					                     , C.COIL_NO      AS C_COIL_NO
					                     , C.COIL_T       AS C_THICK
					                     , C.COIL_W       AS C_WIDTH
					                     , C.COIL_WT      AS C_WEIGTH
					                     , C.COIL_OUTDIA  AS C_OUTDIA
					                     , C.CURR_PROG_CD AS C_PROG_CD
					                     , C.ORD_NO       AS C_ORD_NO      -- 주문번호
					                     , C.ORD_DTL      AS C_ORD_DTL     -- 주문행번
					                     , C.DEMANDER_CD  AS C_DEMANDER_CD -- 수요가코드
					                     , (CASE WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
					                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
					                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
					                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
					                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
					                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
					                             WHEN C.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
					                             WHEN C.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
					                             WHEN C.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
					                             WHEN C.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
					                             WHEN C.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
					                             ELSE '' END
					                       ) AS C_YEOJAE_CAUSE_CD
					                     , TRUNC((SYSDATE - C.HRMILL_CMPL_DT )*24)             AS C_HOT_COIL_MIN
					                     , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
					                            WHEN C.COIL_OUTDIA >  1930 THEN 'C'
					                            ELSE 'B' END  AS C_COIL_OUTDIA_GP
					                     , CASE WHEN C.COIL_W < 1601     THEN 'M' ELSE 'L' END AS C_COIL_W_GP
					                     , L.COIL_NO      AS L_COIL_NO
					                     , L.COIL_T       AS L_THICK
					                     , L.COIL_W       AS L_WIDTH
					                     , L.COIL_WT      AS L_WEIGTH
					                     , L.COIL_OUTDIA  AS L_OUTDIA
					                     , L.CURR_PROG_CD AS L_PROG_CD
					                     , L.ORD_NO       AS L_ORD_NO      -- 주문번호
					                     , L.ORD_DTL      AS L_ORD_DTL     -- 주문행번
					                     , L.DEMANDER_CD  AS L_DEMANDER_CD -- 수요가코드
					                     , (CASE WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
					                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
					                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
					                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
					                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
					                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
					                             WHEN L.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
					                             WHEN L.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
					                             WHEN L.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
					                             WHEN L.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
					                             WHEN L.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
					                             ELSE '' END
					                       ) AS L_YEOJAE_CAUSE_CD
					                     , TRUNC((SYSDATE - L.HRMILL_CMPL_DT )*24) AS L_HOT_COIL_MIN
					                     , R.COIL_NO      AS R_COIL_NO
					                     , R.COIL_W       AS R_WIDTH
					                     , R.COIL_T       AS R_THICK
					                     , R.COIL_WT      AS R_WEIGTH
					                     , R.COIL_OUTDIA  AS R_OUTDIA
					                     , R.CURR_PROG_CD AS R_PROG_CD
					                     , R.ORD_NO       AS R_ORD_NO      -- 주문번호
					                     , R.ORD_DTL      AS R_ORD_DTL     -- 주문행번
					                     , R.DEMANDER_CD  AS R_DEMANDER_CD -- 수요가코드
					                     , (CASE WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
					                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
					                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
					                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
					                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
					                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
					                             WHEN R.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
					                             WHEN R.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
					                             WHEN R.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
					                             WHEN R.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
					                             WHEN R.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
					                             ELSE '' END
					                       ) AS R_YEOJAE_CAUSE_CD
					                     , TRUNC((SYSDATE - R.HRMILL_CMPL_DT )*24) R_HOT_COIL_MIN
					                     , L.ISHOT        AS L_ISHOT
					                     , R.ISHOT        AS R_ISHOT
					                  FROM TO_LOC_DATA_TABLE K
					                     , (SELECT 1 T_ROW, A.*
					                          FROM USRPTA.TB_PT_COILCOMM  A ) C  --대상코일
					                     , (SELECT 1 T_ROW, A.*
					                          ,DECODE(NVL(DECODE(STL_APPEAR_GP,'Y','CG','CM'),'CM'),'CM',
					                                  CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
					                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
					                                            THEN 'TRUE'
					                                            ELSE 'FALSE'
					                                       END
					                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
					                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 96
					                                            THEN 'TRUE'
					                                            ELSE 'FALSE'
					                                       END
					                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
					                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
					                                            THEN 'TRUE'
					                                            ELSE 'FALSE'
					                                       END
					                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
					                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 48
					                                            THEN 'TRUE'
					                                            ELSE 'FALSE'
					                                       END
					                                  END) AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
					                          FROM USRPTA.TB_PT_COILCOMM  A ) L  --하단LEFT
					                     , (SELECT 1 T_ROW, A.*
					                             ,DECODE(NVL(DECODE(STL_APPEAR_GP,'Y','CG','CM'),'CM'),'CM',
					                                          CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
					                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
					                                                    THEN 'TRUE'
					                                                    ELSE 'FALSE'
					                                               END
					                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
					                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 96
					                                                    THEN 'TRUE'
					                                                    ELSE 'FALSE'
					                                               END
					                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
					                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
					                                                    THEN 'TRUE'
					                                                    ELSE 'FALSE'
					                                               END
					                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
					                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 48
					                                                    THEN 'TRUE'
					                                                    ELSE 'FALSE'
					                                               END
					                                          END) AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
					                          FROM USRPTA.TB_PT_COILCOMM  A ) R  --하단RIGHT
					                 WHERE K.STL_NO           = C.COIL_NO(+)
					                   AND K.TAG_LEFT_STL_NO  = L.COIL_NO(+)
					                   AND K.TAG_RIGHT_STL_NO = R.COIL_NO(+)

					                   --짱구코일 2단제외
					                   --AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND JJANG_GP = 'Y'                      THEN 0
					                   --             ELSE 1 END
					                   --2단일 경우 1단에 핫코일이 존재 시 2단제의
					                   AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND (L.ISHOT ='TRUE' OR R.ISHOT='TRUE') THEN 0
					                                ELSE 1 END
					                   --2단일 경우 좌우 적치 상태 CHECK
					                   AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002'
					                                     AND (TAG_LEFT_ACTIVE_STAT != 'E' OR TAG_RIGHT_ACTIVE_STAT != 'E'
					                                       OR TAG_LEFT_LAYER_STAT  != 'C' OR TAG_RIGHT_LAYER_STAT  != 'C') THEN 0
					                                ELSE 1 END
					                   --1단일 경우 좌우 상단 적치 상태 CHECK
					                   AND 1 = CASE WHEN K.TAG_YD_STK_LYR_NO = '001' AND (TAG_2DAN_LEFT_STL_NO IS NOT NULL OR TAG_2DAN_RIGHT_STL_NO IS NOT NULL) THEN 0
					                                ELSE 1 END

					               ) KK


					         --무인크레인 이동가능 여부 CHECK
					--         WHERE ( TAG_YD_STK_COL_GP IN (SELECT R.ITEM
					--                                         FROM TB_YD_RULE      R
					--                                            , AUTO_CR_TABLE   A
					--                                        WHERE R.REPR_CD_GP = 'CR0001'
					--                                          AND R.CD_GP      = A.EQUIP_GP
					--                                          AND R.DEL_YN     = 'N')
					--               )
					       )
					)
					--*--*--*--*--*--*-- 평점
					SELECT G.*
					     , CASE  -- 1단이면서 소재인 경우
					            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
					            THEN CASE WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌우) 진도코드
					                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌단) 진도코드
					                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우단) 진도코드
					                      WHEN L_COIL_NO  IS NULL     AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
					                      WHEN R_COIL_NO  IS NULL     AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED
					                      ELSE '7' END
					             -- 2단이면서 소재인 경우
					            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
					            THEN CASE --WHEN L_JJANG_GP  = 'Y'      OR  R_JJANG_GP = 'Y'      THEN '9' --짱구 위에서 못 올린다.
					                      WHEN L_WB        = 'Y'      OR  R_WB       = 'Y'      THEN '8' --좌하단우하단 작업 예약
					                      WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌하단우하단) 진도코드
					                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌하단) 진도코드
					                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우하단) 진도코드
					                      ELSE '2' END
					            -- 1단이면서 제품인 경우
					            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8')
					            THEN CASE WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
					                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
					                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
					                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우축 동일 고객사+주문번호행번

					                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
					                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우측 동일 주문번호행번

					                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌측   동일 주문번호행번
					                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우측   동일 주문번호행번
					                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우측 동일 주문번호
					                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우측   동일 주문번호
					                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌측   동일 주문번호
					                      WHEN L_COIL_NO IS NULL      AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
					                      WHEN R_COIL_NO IS NULL      AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED

					                      ELSE '7' END
					            -- 2단이면서 제품인 경우
					            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8') --제품
					            THEN CASE --WHEN L_JJANG_GP = 'Y'       OR  R_JJANG_GP = 'Y'      THEN '9' -- 짱구 위에서 못 올린다.
					                      WHEN L_WB = 'Y'             OR  R_WB  = 'Y'           THEN '8' --좌하단우하단 작업 예약
					                      WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
					                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
					                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
					                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 고객사+주문번호행번

					                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
					                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 주문번호행번

					                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌하단   동일 주문번호행번
					                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우하단   동일 주문번호행번
					                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우하단 동일 주문번호
					                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우하단   동일 주문번호
					                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌하단   동일 주문번호
					                      ELSE '2' END

					             ELSE '9' END GRADE
					  FROM (SELECT A.*
					             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
					                  FROM TB_YD_WRKBOOK    A1
					                     , TB_YD_WRKBOOKMTL B1
					                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
					                   AND A1.DEL_YN      = 'N'
					                   AND B1.DEL_YN      = 'N'
					                   AND B1.STL_NO      = A.C_COIL_NO
					               ) C_WB
					             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
					                  FROM TB_YD_WRKBOOK    A1
					                     , TB_YD_WRKBOOKMTL B1
					                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
					                   AND A1.DEL_YN      = 'N'
					                   AND B1.DEL_YN      = 'N'
					                   AND B1.STL_NO      = A.L_COIL_NO
					               ) L_WB
					             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
					                  FROM TB_YD_WRKBOOK    A1
					                     , TB_YD_WRKBOOKMTL B1
					                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
					                   AND A1.DEL_YN      = 'N'
					                   AND B1.DEL_YN      = 'N'
					                   AND B1.STL_NO      = A.R_COIL_NO
					               ) R_WB
					          FROM TO_LOC_DATA_COMP_TABLE A
					       ) G
					 ORDER BY GRADE
					        , (CASE WHEN ABOVE_LYR = TAG_YD_STK_LYR_NO THEN 1 ELSE 2 END) -- 1단우선적치 B군(폭1200이상,외경1800이상) C군(폭1200,외경1990이상)
					        , TAG_YD_STK_BED_NO
					        , TAG_YD_STK_LYR_NO
					 */       
					jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocPrimaryWorkThirdJ", logId, mthdNm, "제품동일한 적치가능한 베드 조회 Third");

				}
			}

			String sqlName = "getYdToLocPrimaryWorkJ";
			if ("H".equals(ydSchCd.substring(7, 8))) {
				sqlName = "getYdToLocPrimaryWorkH";
			}
			if (jsResult.size() <= 0) {
				sLogMsg = "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, sLogMsg, "SL");

				if ("Y".equals(schLogYn)) {
					sSchLogContents = "["+sqlName+"] 대상코일위치검색실패:"+ sStlNo+" LOG :"+"\r\n";
					this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
				}
				
				jrRtn.setField("RTN_CD" , "0");
				jrRtn.setField("RTN_MSG", sLogMsg);
					
				/*****************************
				 * 수입 명령선택 전 XX 위치로 처리
				 *****************************/
				if ( "CV".equals(ydSchCd.substring(2,4))) {
					jrRtn.setField("RTN_MSG", "수입처리 완료");
				}
				
				return jrRtn;
			} else {
				
				/****************************
				 *  log용
				 *****************************/
				JDTORecord jrResult1 = JDTORecordFactory.getInstance().create();
				String  sLocAbleMsg = "";
				for (int i = 1; i <= jsResult.size(); i++) {
					jsResult.absolute(i);
					jrResult1  = jsResult.getRecord();
								
					ydStkColGp 	= commUtils.trim(jrResult1.getFieldString("TAG_YD_STK_COL_GP"  ));
					ydStkBedNo 	= commUtils.trim(jrResult1.getFieldString("TAG_YD_STK_BED_NO"  ));
					ydStkLyrNo 	= commUtils.trim(jrResult1.getFieldString("TAG_YD_STK_LYR_NO"  ));
					sGrade 		= commUtils.nvl (jrResult1.getFieldString("GRADE"),"9");
					
					sLocAbleMsg  = sLocAbleMsg + "적치가능대상코일위치="+" 열:"+ ydStkColGp+" 베드:"+ydStkBedNo+" 단:"+ydStkLyrNo+" 평점:"+sGrade+"\r\n";
				}		
				if ("Y".equals(schLogYn)) {
					sSchLogContents = "["+sqlName+"] 대상코일위치검색:"+ sStlNo+"\r\n" + sLocAbleMsg;
					this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
				}
			}
		    //----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 SCH RULL 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord  jrRuleConst = this.toLocRuleConst(logId, mthdNm);
			//----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 LOGIC 적용여부 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrLocRuleApply = this.toLocRuleApply(logId, mthdNm);
			commUtils.printParam(logId, jrLocRuleApply);
			
			JDTORecord jrResult	= JDTORecordFactory.getInstance().create();
			JDTORecord jrSndMsg = JDTORecordFactory.getInstance().create();
			

			String sLocAbleRtn     = "";
			String sLocAbleRtnMsg  = "";
			boolean sTcLocAbleYn   = false;
			
			for (int i = 1; i <= jsResult.size(); i++) {
				jsResult.absolute(i);
				jrResult  = jsResult.getRecord();
							
				sGrade 		= commUtils.nvl (jrResult.getFieldString("GRADE"),"9");
				ydStkColGp 	= commUtils.trim(jrResult.getFieldString("TAG_YD_STK_COL_GP"  ));
				ydStkBedNo 	= commUtils.trim(jrResult.getFieldString("TAG_YD_STK_BED_NO"  ));
				ydStkLyrNo 	= commUtils.trim(jrResult.getFieldString("TAG_YD_STK_LYR_NO"  ));	
	
				if ("TC".equals(ydStkColGp.substring(2, 4))) {
					jrSndMsg = commUtils.getParam(logId, mthdNm, sModifier);
					
					jrSndMsg.setField("YD_WBOOK_ID" , ydWbookId);
					jrSndMsg.setField("YD_SCH_CD"   , ydSchCd);
					/******************************* 
					 * 대차위 상차가능 중량 및 매수 check
					 ******************************/
					sTcLocAbleYn = coilDao.chkTcLocAble(jrSndMsg);
					
					if(sTcLocAbleYn) {
						sLogMsg = ydStkColGp + ydStkBedNo + ydStkLyrNo+"  대차적치가능";
						commUtils.printLog(logId, sLogMsg, "SL");				
					    //적치가능 
						sRtnBedDan = ydStkColGp + ydStkBedNo + ydStkLyrNo;
						sDBLogMsg = sDBLogMsg + "대차상차:"+sStlNo+" 적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 코일 적치가능" +"\r\n";
		    			break;
					} else {
						sLogMsg = ydStkColGp + ydStkBedNo + ydStkLyrNo+"  대차중량오버로 적치 불가능";
						
						commUtils.printLog(logId, sLogMsg, "SL");				
						sDBLogMsg = sDBLogMsg + "대차상차:"+sStlNo+" 적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 대차중량오버로 적치 불가능" +"\r\n";
						
						if (!"H".equals(ydSchCd.substring(7,8))) {
							boolean bTcAutoStartAbleYn = coilDao.chkTcAutoStartAble(jrSndMsg);
							if (bTcAutoStartAbleYn) {
								sTcMoveYn = "Y";
	    						commUtils.printLog(logId, "대차중량OVER! 제품 대차 자동출발", "SL");
							}
						}
					}
				} else {
					/******************* 
					 * 적치가능 check
					 ******************/
					JDTORecord jrLocAbleRtn = this.toLocAbleCheck(logId, mthdNm, jrResult, jrRuleConst, jrLocRuleApply);
				//
					sLocAbleRtn    = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
					sLocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
					if (sLocAbleRtnMsg.length() > 0 ) {
						sDBLogMsg = sDBLogMsg + sLocAbleRtnMsg +"\r\n";
					}
					if ("1".equals(sLocAbleRtn)) {
						sLogMsg = ydStkColGp + ydStkBedNo + ydStkLyrNo+"  적치가능 위치평점:"+ sGrade;
						commUtils.printLog(logId, sLogMsg, "SL");				
					    //적치가능 
						sRtnBedDan = ydStkColGp + ydStkBedNo + ydStkLyrNo;
		    			break;
					}
				}
			}			
			
			String sAPP901 = coilDao.ApplyYn(logId, mthdNm, "APP901", "J", "*");
			commUtils.printLog(logId, "TO위치 RULE상수[신sAPP901] : " + sAPP901, "SL");
			if ("Y".equals(sAPP901)) {
				/***************************************************
				** 신 로직
				** XX이면서 소재인경우  폭및 외경구분 안하는 1단을 다시 검색
				****************************************************/	    			
    			if (sRtnBedDan.length() < 10) {
    				
 					if ("H".equals(ydSchCd.substring(7, 8))) {
 						commUtils.printLog(logId, "▼▼▼▼▼▼▼▼▼▼ 소재 1단  재검색 ▼▼▼▼▼▼▼▼▼▼", "SL");
 						
 						for (int j = 1; j <= jsResult.size(); j++) {

 							jsResult.absolute(j);
 							jrResult  = jsResult.getRecord();
 							
 							sGrade 		= commUtils.nvl (jrResult.getFieldString("GRADE"),"9");
 							ydStkColGp 	= commUtils.trim(jrResult.getFieldString("TAG_YD_STK_COL_GP"  ));
 							ydStkBedNo 	= commUtils.trim(jrResult.getFieldString("TAG_YD_STK_BED_NO"  ));
 							ydStkLyrNo 	= commUtils.trim(jrResult.getFieldString("TAG_YD_STK_LYR_NO"  ));	
 							
 							if ("001".equals(ydStkLyrNo)) { 
 								/**************************************
 								 *         적치 가능 check
 						   		 **************************************/			
 								//소재 1단일 경우 재검색 
 								jrLocRuleApply.setField("WID_DIA_CHK_YN", "N");	
 								JDTORecord jrLocAbleRtn1 = this.toLocAbleCheck(logId, mthdNm, jrResult, jrRuleConst, jrLocRuleApply);
 			
 								sLocAbleRtn    = commUtils.trim(jrLocAbleRtn1.getFieldString("LOC_ABLE_RTN")) ;
 								sLocAbleRtnMsg = commUtils.trim(jrLocAbleRtn1.getFieldString("LOC_ABLE_CONTENTS")) ;
 								if (sLocAbleRtnMsg.length() > 0 ) {
 									sDBLogMsg = sDBLogMsg + sLocAbleRtnMsg +"\r\n";
 								}
 								
 								if ("1".equals(sLocAbleRtn)) {
 									sLogMsg = ydStkColGp+ydStkBedNo+ ydStkLyrNo+"  적치가능 위치평점:"+ sGrade;
 									commUtils.printLog(logId, sLogMsg, "SL");				
 								    //적치가능 
 									sRtnBedDan = ydStkColGp + ydStkBedNo + ydStkLyrNo;
 									commUtils.printLog(logId, "▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲", "SL");
 									
 					    			break;
 								}
 							}	
 						} //for
 						commUtils.printLog(logId, "▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲", "SL");
 						
 						if (sRtnBedDan.length() < 10) {
 		 					sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 검색조건 TO위치 결정 실패2 ";
 		 					commUtils.printLog(logId, sLogMsg, "SL");
 		 					if ("Y".equals(schLogYn)) {
 								sSchLogContents = "["+sqlName+"] 대상코일선택실패:"+ sStlNo+" LOG :" +"\r\n" +  sDBLogMsg;
 								this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
 							}
 		 					jrRtn.setField("RTN_CD" , "0");	
		 					jrRtn.setField("RTN_MSG", jrResult);
		 					return jrRtn;
 						}	
 						
 						
 					} else {
    				
 						/***************************************************
 	    				** 제품인 경우
 	    				****************************************************/		    				
	 					sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 검색조건 TO위치 결정 실패 ";
	 					commUtils.printLog(logId, sLogMsg, "SL");
	 				
	 					if ("Y".equals(schLogYn)) {
	 						sSchLogContents = "["+sqlName+"]사용자 지정 대상코일선택실패:"+ sStlNo+" LOG :"+"\r\n" + sDBLogMsg;
	 						this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
	 					}
	 					jrRtn.setField("RTN_CD" , "0");	
	 					jrRtn.setField("RTN_MSG", jrResult);
	 					return jrRtn;
 					}	
 				}	
			} else {
				
				if (sRtnBedDan.length() < 10) {
					sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 TO위치 결정 실패 ";
					commUtils.printLog(logId, sLogMsg, "SL");
	
					if ("Y".equals(schLogYn)) {
						sSchLogContents = "["+sqlName+"] 대상코일선택실패:"+ sStlNo+"\r\n"+" LOG :"+"\r\n" + sDBLogMsg;
						this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
					}
					
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", sLogMsg);
					return jrRtn;				
				}
			}
			
			if ("Y".equals(schLogYn)) {
				sSchLogContents = "["+sqlName+"] 대상코일선택:"+ sStlNo+"  선택위치:"+ sRtnBedDan +"  평점:" + sGrade +"\r\n"+" LOG :"+"\r\n" + sDBLogMsg;
				this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
			}
			
			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = commUtils.getParam(logId, mthdNm, sModifier);

			jrSetLoc.setField("YD_CRN_SCH_ID"	, ydCrnSchId); 
			jrSetLoc.setField("YD_EQP_ID"		, ydEqpId);	 
			jrSetLoc.setField("YD_SCH_CD"		, ydSchCd);	 
			jrSetLoc.setField("YD_UP_WO_LOC"	, ydUpWoLoc); 
			jrSetLoc.setField("YD_UP_WO_LAYER"	, ydUpWoLayer);	 
			jrSetLoc.setField("YD_DN_WO_LOC"	, sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER"	, sRtnBedDan.substring(8));
			jrSetLoc.setField("YD_WBOOK_ID"		, ydWbookId); 
				
			JDTORecord jrRcvRtn = this.toLocUpdCrnSch(logId,mthdNm, jrSetLoc, jrCrnSch  );
			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
			jrRtn.setField("TC_MOVE_YN", sTcMoveYn);
			
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
	    }//end of try~catch				

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
	public JDTORecord toLocUpdCrnSch(String logId, String mthdNms, JDTORecord jrSetLoc, JDTORecord jrCrnWrk) throws JDTOException {
		String mthdNm = "TO위치 UPDATE[CCoilSchSeEJB.toLocUpdCrnSch] < " + mthdNms;
		String sLogMsg = null;
    	JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();	//전문 Return
		
//		int intRtnVal	= 0;
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			int iRtnY = 0;
			
			int iYdEqpWrkSh    	= Integer.parseInt  (commUtils.trim(jrCrnWrk.getFieldString("SH_CNT")));	//크레인작업재료 총매수	
			int iYdEqpWrkWt    	= Integer.parseInt  (commUtils.trim(jrCrnWrk.getFieldString("SUM_MTL_WT")));//크레인작업재료 총중량
			double dblYdEqpWrkT = Double.parseDouble(commUtils.trim(jrCrnWrk.getFieldString("SUM_MTL_T")));	//크레인작업재료 총높이
			
			String sModifier 	= commUtils.trim(jrSetLoc.getFieldString("MODIFIER"  ));		//MODIFIER
			String ydEqpWrkMaxW = commUtils.trim(jrCrnWrk.getFieldString("MAX_MTL_W"  ));		//크레인작업재료 중 최대 폭
			String ydEqpWrkMaxL = commUtils.trim(jrCrnWrk.getFieldString("MAX_MTL_L"  ));		//크레인작업재료 중 최대 길이
			String sStlNo 	   	= commUtils.trim(jrCrnWrk.getFieldString("STL_NO"  ));
			String ydSchCd 	   	= commUtils.trim(jrCrnWrk.getFieldString("YD_SCH_CD"  ));
			String sCoilOutDia  = commUtils.trim(jrCrnWrk.getFieldString("COIL_OUTDIA"  ));
			
			String ydCrnSchId  	= commUtils.trim(jrSetLoc.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
			String ydUpWoLoc 	= commUtils.trim(jrSetLoc.getFieldString("YD_UP_WO_LOC"  ));			
			String ydUpWoLayer	= commUtils.trim(jrSetLoc.getFieldString("YD_UP_WO_LAYER"  ));			
			String ydDnWoLoc	= commUtils.trim(jrSetLoc.getFieldString("YD_DN_WO_LOC"  ));			
			String ydDnWoLayer	= commUtils.trim(jrSetLoc.getFieldString("YD_DN_WO_LAYER"  ));
			
			if ("".equals(ydDnWoLoc)) {
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", "권하위치를 찾지 못했습니다.");
				return jrRtn;
			}
			
			commUtils.printParam(logId, jrSetLoc);
			//----------------------------------------------------------------------------------------------------------------------
			// 권상지시위치 수정
			//----------------------------------------------------------------------------------------------------------------------
			
			JDTORecordSet jsLayerUpXy = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord jrInBed = commUtils.getParam(logId, mthdNm, sModifier); 
			jrInBed.setField("YD_STK_COL_GP",	ydUpWoLoc.substring(0, 6));  //권상지시위치
			jrInBed.setField("YD_STK_BED_NO",	ydUpWoLoc.substring(6));	 //권상지시위치
			jrInBed.setField("YD_STK_LYR_NO",	ydUpWoLayer);
				
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkLayerBybed 
			SELECT A.YD_STK_COL_GP 
			     , A.YD_STK_BED_NO 
			     , A.YD_STK_LYR_NO
			     , A.YD_STK_LYR_XAXIS
			     , A.YD_STK_LYR_YAXIS
			     , A.YD_STK_LYR_ZAXIS
			     , B.YD_STK_BED_XAXIS_TOL 
			     , B.YD_STK_BED_YAXIS_TOL 
			     , B.YD_STK_BED_ZAXIS_TOL 
			     , (SELECT ROTATION_ANGLE FROM TB_YD_STKCOL WHERE YD_STK_COL_GP = A.YD_STK_COL_GP) AS ROTATION_ANGLE
			     , A.YD_STK_LYR_MTL_STAT
			  FROM TB_YD_STKLYR A
			     , TB_YD_STKBED B
			 WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
			   AND A.YD_STK_BED_NO = B.YD_STK_BED_NO
			   AND A.YD_STK_COL_GP = :V_YD_STK_COL_GP
			   AND A.YD_STK_BED_NO = :V_YD_STK_BED_NO
			   AND A.YD_STK_LYR_NO = :V_YD_STK_LYR_NO
			   AND A.DEL_YN ='N'
			   AND B.DEL_YN ='N'
			*/  
			jsLayerUpXy = commDao.select(jrInBed, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkLayerBybed", logId, mthdNm, "권상 BED 좌표 조회");
			if (jsLayerUpXy.size() <= 0) {
				sLogMsg =  "확인:"+sStlNo+"권상 Layer 좌표 조회 검색 실패.";
				commUtils.printLog(logId, sLogMsg, "SL");
				
			}
			jsLayerUpXy.first();
			JDTORecord jrUpLayerXy = jsLayerUpXy.getRecord();
	
			JDTORecordSet jsDnLayerXy = JDTORecordFactory.getInstance().createRecordSet("");
			jrInBed = commUtils.getParam(logId, mthdNm, sModifier); 
			jrInBed.setField("YD_STK_COL_GP", 	ydDnWoLoc.substring(0, 6));//권하지시위치
			jrInBed.setField("YD_STK_BED_NO", 	ydDnWoLoc.substring(6));	//권하지시위치
			jrInBed.setField("YD_STK_LYR_NO",	ydDnWoLayer);				
			
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkLayerBybed 
			SELECT A.YD_STK_COL_GP 
			     , A.YD_STK_BED_NO 
			     , A.YD_STK_LYR_NO
			     , A.YD_STK_LYR_XAXIS
			     , A.YD_STK_LYR_YAXIS
			     , A.YD_STK_LYR_ZAXIS
			     , B.YD_STK_BED_XAXIS_TOL 
			     , B.YD_STK_BED_YAXIS_TOL 
			     , B.YD_STK_BED_ZAXIS_TOL 
			     , (SELECT ROTATION_ANGLE FROM TB_YD_STKCOL WHERE YD_STK_COL_GP = A.YD_STK_COL_GP) AS ROTATION_ANGLE
			     , A.YD_STK_LYR_MTL_STAT
			  FROM TB_YD_STKLYR A
			     , TB_YD_STKBED B
			 WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
			   AND A.YD_STK_BED_NO = B.YD_STK_BED_NO
			   AND A.YD_STK_COL_GP = :V_YD_STK_COL_GP
			   AND A.YD_STK_BED_NO = :V_YD_STK_BED_NO
			   AND A.YD_STK_LYR_NO = :V_YD_STK_LYR_NO
			   AND A.DEL_YN ='N'
			   AND B.DEL_YN ='N'
			*/  
			jsDnLayerXy = commDao.select(jrInBed, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkLayerBybed", logId, mthdNm, "권하 BED 좌표 조회");
			if (jsDnLayerXy.size() <= 0) {
				sLogMsg = "권하 Layer 좌표 검색 실패 ";
				commUtils.printLog(logId, sLogMsg, "SL");
			}
			
			jsDnLayerXy.first();
			JDTORecord jrDnLayerXy = jsDnLayerXy.getRecord();
			
			JDTORecord jrUpCrnSch = commUtils.getParam(logId, mthdNm, sModifier); 
			jrUpCrnSch.setField("YD_CRN_SCH_ID", 			ydCrnSchId);										//크레인스케줄ID
			
			//권상정보   					
			jrUpCrnSch.setField("YD_UP_WO_LOC", 			ydUpWoLoc);										//권상지시위치
			jrUpCrnSch.setField("YD_UP_WO_LAYER", 			ydUpWoLayer);										//권상지시단
			jrUpCrnSch.setField("YD_UP_STK_COL_GP", 		ydUpWoLoc.substring(0, 6));						//권상지시위치 - 적치열
			jrUpCrnSch.setField("YD_UP_STK_BED_NO", 		ydUpWoLoc.substring(6));							//권상지시위치 - 적치베드
			jrUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",  		commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_LYR_XAXIS"  ))) ;
			jrUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  		commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_LYR_YAXIS"  ))) ;
			
//			if(("J".equals(ydSchCd.substring(0, 1))) && ("002".equals(ydUpWoLayer))) {
//			if((!"H".equals(ydSchCd.substring(7, 8))) && ("002".equals(ydUpWoLayer))) {				
//				sLogMsg = "권상Y좌표재계산 ";
//				commUtils.printLog(logId, sLogMsg, "SL");
//				
////				int intRtnVal1 = searchCoilYdGdsClineY(logId, mthdNms,ydUpWoLoc.substring(0,6),ydUpWoLoc.substring(6,8),ydUpWoLayer,commUtils.trim(jrUpLayerXy.getFieldString("STACK_LAYER_Y_AXIS"  )));				
//				iRtnY = this.getUpDnClineY(logId, mthdNm, ydUpWoLoc.substring(0,6),ydUpWoLoc.substring(6,8),ydUpWoLayer,commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_LYR_YAXIS"  )));
//				if(iRtnY > 0) {
//					sLogMsg = "권상Y좌표재계산값:"+ iRtnY;
//					commUtils.printLog(logId, sLogMsg, "SL");
//					jrUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  String.valueOf(iRtnY)) ;	
//				}
//			}
			
			if ("002".equals(ydUpWoLayer)) {	
				iRtnY = this.getUpDnClineY(logId, mthdNm, ydUpWoLoc.substring(0,6),ydUpWoLoc.substring(6,8),ydUpWoLayer,sCoilOutDia);				
				if(iRtnY > 0) {
					sLogMsg = "권상Y좌표재계산값:"+ iRtnY;
					commUtils.printLog(logId, sLogMsg, "SL");
					jrUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  String.valueOf(iRtnY)) ;	
				}
			}
			jrUpCrnSch.setField("YD_UP_WO_LOC_ZAXIS",  		commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_LYR_ZAXIS"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MAX",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MIN",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MAX",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MIN",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_LOC_YAXIS1",  	"" ) ;
			jrUpCrnSch.setField("YD_UP_WO_LOC_YAXIS2",  	"" ) ;
			jrUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MAX",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MIN",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("UP_ROTATION_ANGLE",  		commUtils.trim(jrUpLayerXy.getFieldString("ROTATION_ANGLE"  )) ) ;
			//권하정보   					
			jrUpCrnSch.setField("YD_DN_WO_LOC", 			ydDnWoLoc);										//권하지시위치
			jrUpCrnSch.setField("YD_DN_WO_LAYER", 			ydDnWoLayer);										//권하지시단
			jrUpCrnSch.setField("YD_DN_STK_COL_GP", 		ydDnWoLoc.substring(0, 6));						//권하지시위치 - 적치열
			jrUpCrnSch.setField("YD_DN_STK_BED_NO", 		ydDnWoLoc.substring(6));							//권하지시위치 - 적치베드
			
			
			jrUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",  		commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_LYR_XAXIS"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  		commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_LYR_YAXIS"  )) ) ;
			
//			if((!"H".equals(ydSchCd.substring(7, 8))) && ("002".equals(ydDnWoLayer))) {
//								
//				sLogMsg = "권하Y좌표재계산 ";
//				commUtils.printLog(logId, sLogMsg, "SL");
//				
//				iRtnY = this.getUpDnClineY(logId, mthdNm, ydDnWoLoc.substring(0,6),ydDnWoLoc.substring(6,8),ydDnWoLayer,commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_LYR_YAXIS"  )));				
//				if(iRtnY > 0) {
//					sLogMsg = "권하Y좌표재계산값:"+ iRtnY;
//					commUtils.printLog(logId, sLogMsg, "SL");
//					jrUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  String.valueOf(iRtnY)) ;	
//				}
//			}
			
			if ("002".equals(ydDnWoLayer)) {	
				iRtnY = this.getUpDnClineY(logId, mthdNm, ydDnWoLoc.substring(0,6),ydDnWoLoc.substring(6,8),ydDnWoLayer,sCoilOutDia);				
				if(iRtnY > 0) {
					sLogMsg = "권하Y좌표재계산값:"+ iRtnY;
					commUtils.printLog(logId, sLogMsg, "SL");
					jrUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  String.valueOf(iRtnY)) ;	
				}
			}
			
			jrUpCrnSch.setField("YD_DN_WO_LOC_ZAXIS",  		commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_LYR_ZAXIS"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_XAXIS_GAP_MAX",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_XAXIS_GAP_MIN",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_YAXIS_GAP_MAX",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_YAXIS_GAP_MIN",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_LOC_YAXIS1",  	"" ) ;
			jrUpCrnSch.setField("YD_DN_WO_LOC_YAXIS2",  	"" ) ;
			jrUpCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MAX",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MIN",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("DOWN_ROTATION_ANGLE",  	commUtils.trim(jrDnLayerXy.getFieldString("ROTATION_ANGLE"  )) ) ;
	
	
			//기타   					
			jrUpCrnSch.setField("YD_EQP_WRK_SH", 			String.valueOf(iYdEqpWrkSh));	   //크레인작업재료 총매수
			jrUpCrnSch.setField("YD_EQP_WRK_WT", 			String.valueOf(iYdEqpWrkWt));      //크레인작업재료 총중량
			jrUpCrnSch.setField("YD_EQP_WRK_T", 			String.valueOf(dblYdEqpWrkT));  //크레인작업재료 총높이
			jrUpCrnSch.setField("YD_EQP_WRK_MAX_W", 		ydEqpWrkMaxW);			//크레인작업재료 중 최대 폭
			jrUpCrnSch.setField("YD_EQP_WRK_MAX_L", 		ydEqpWrkMaxL);									//크레인작업재료 중 최대 길이
			jrUpCrnSch.setField("MODIFIER", 				sModifier);
			
			
			//H동 JHCRH2 호기 작업이 일반야드 작업 인 경우 H1호기로 변경처리 함
			JDTORecord jrParm = commUtils.getParam(logId, mthdNm, sModifier);
			jrParm.setField("YD_CRN_SCH_ID", ydCrnSchId);			

			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdEqpChange 
			SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END CRN_CHK_FLAG
			     , 'JHCRH1' AS CHG_YD_EQP_ID
			  FROM TB_YD_CRNSCH A
			     , TB_YD_STKCOL B
			 WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			   AND B.YD_STK_COL_GP = SUBSTR(A.YD_DN_WO_LOC,1,6)  
			   AND SUBSTR(A.YD_DN_WO_LOC,3,2) BETWEEN '00' AND '99' 
			   AND SUBSTR(A.YD_DN_WO_LOC,7,2) = '01' 
			   AND A.YD_DN_WO_LAYER     = '002' 
			   AND A.YD_TO_LOC_DCSN_MTD = 'S'  --주작업인 경우
			   AND B.YD_LOC_GP = 'H'
			   AND A.YD_EQP_ID = 'JHCRH2'
			*/	   
			
			JDTORecordSet jsEqpChange = commDao.select(jrParm, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdEqpChange", logId, mthdNm, "설비변경여부 ");
			if (jsEqpChange.size() > 0) {
				
				String sCrnChkFlag = commUtils.trim(jsEqpChange.getRecord(0).getFieldString("CRN_CHK_FLAG"));
				String sChgYdEqpId = commUtils.trim(jsEqpChange.getRecord(0).getFieldString("CHG_YD_EQP_ID"));
				commUtils.printLog(logId, "sCrnChkFlag :" + sCrnChkFlag  + "        sChgYdEqpId :" + sChgYdEqpId , "SL");
				if("Y".equals(sCrnChkFlag)) {
					if(!"".equals(sChgYdEqpId)) {
						jrUpCrnSch.setField("YD_EQP_ID", sChgYdEqpId);	
					}
				}	
			}
			
			// 2025.10.20 RITM1477310 D동 수입 스케줄 분개 요청
			// YDB533 기준 만족시 수입 스케줄 크레인 호기 변경처리 함
			JDTORecord jFDParm = commUtils.getParam(logId, mthdNm, sModifier);
			jFDParm.setField("YD_CRN_SCH_ID", ydCrnSchId);
			jFDParm.setField("YD_DN_WO_LOC", ydDnWoLoc);
						
			JDTORecordSet jsEqpChangeBRE = commDao.select(jFDParm, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdEqpChangeBRE", logId, mthdNm, "설비변경여부 BRE ");
			if (jsEqpChangeBRE.size() > 0) {
				
				String sCrnChkFlagBRE = commUtils.trim(jsEqpChangeBRE.getRecord(0).getFieldString("CRN_CHK_FLAG"));
				String sChgYdEqpIdBRE = commUtils.trim(jsEqpChangeBRE.getRecord(0).getFieldString("CHG_YD_EQP_ID"));
				String sYdEqpStatBRE = commUtils.trim(jsEqpChangeBRE.getRecord(0).getFieldString("YD_EQP_STAT"));
				String sYdDnWoLocBRE = commUtils.trim(jsEqpChangeBRE.getRecord(0).getFieldString("YD_DN_WO_LOC"));
				commUtils.printLog(logId, "sCrnChkFlag :" + sCrnChkFlagBRE  + " sChgYdEqpId :" + sChgYdEqpIdBRE + " sYdEqpStatBRE :" + sYdEqpStatBRE + " sYdDnWoLocBRE :" + sYdDnWoLocBRE + " ydDnWoLoc : " + ydDnWoLoc , "SL");
				if("Y".equals(sCrnChkFlagBRE) && !"B".equals(sYdEqpStatBRE)) {
					if(!"".equals(sChgYdEqpIdBRE)) {
						jrUpCrnSch.setField("YD_EQP_ID", sChgYdEqpIdBRE);	
					}
				}	
			}
			
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLoc 
			UPDATE TB_YD_CRNSCH
			   SET MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,............
 			 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID			      
			*/      
			
			int iRtnVal = commDao.update(jrUpCrnSch, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLoc", logId, mthdNm, "크레인스케쥴 갱신");
			
			if (iRtnVal <= 0) {
				sLogMsg =  "확인:"+sStlNo+"권하지시위치["+ydDnWoLoc+"], 권하지시단[" +ydDnWoLayer +" ]을 크레인스케줄에 수정 중 ERROR 발생";

				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			}		

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier); 
			jrParam.setField("YD_STK_COL_GP"		,	ydDnWoLoc.substring(0, 6));
			jrParam.setField("YD_STK_BED_NO"		, 	ydDnWoLoc.substring(6));
			jrParam.setField("YD_STK_LYR_NO"		, 	commUtils.stringPlusInt(ydDnWoLayer,0));
			jrParam.setField("STL_NO"				,   sStlNo);
			jrParam.setField("YD_STK_LYR_MTL_STAT"	, 	"D");
			
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdStkLyrYdStkColBedGp 
			UPDATE TB_YD_STKLYR            
			   SET MOD_DDTT     = SYSDATE             
			     , MODIFIER     = :V_MODIFIER             
			     , YD_STK_LYR_ACT_STAT  = NVL(:V_YD_STK_LYR_ACT_STAT, YD_STK_LYR_ACT_STAT)
			     , YD_STK_LYR_MTL_STAT  = NVL(:V_YD_STK_LYR_MTL_STAT, YD_STK_LYR_MTL_STAT)
			     , STL_NO               = NVL(:V_STL_NO , STL_NO)
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
			   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
		    */  
			iRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdStkLyrYdStkColBedGp", logId, mthdNm, "TB_YD_STKLYR 갱신");
		
			//저장품에 등록할 위치
			if (iRtnVal <= 0) {
				sLogMsg = "확인:"+sStlNo+"권하지시위치["+ydDnWoLoc+"], 권하지시단["+ydDnWoLayer+"] 활성화중 ERROR 발생";
				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			}
		
			sLogMsg = "크레인스케쥴 ID["+ydCrnSchId+"] TO위치결정>>>>>>>> 권하지시위치["+ydDnWoLoc+"], 권하지시단["+ydDnWoLayer+"]" ;
			commUtils.printLog(logId, sLogMsg, "SL");

			commUtils.printLog(logId, mthdNm, "S-");
			
			jrRtn.setField("RTN_CD" , "1");	
			return jrRtn;	
		//----------------------------------------------------------------------------------------------------------------------
    	
		//----------------------------------------------------------------------------------------------------------------------
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}    


	/**
	 * [A] 오퍼레이션명 : 코일적치가능 Check
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord toLocAbleCheck( String logId, String mthdNms, JDTORecord jrCoilCom, JDTORecord jrRuleConst, JDTORecord jrLocRuleApply) throws JDTOException  {
	  	String mthdNm = "적치가능 Check[CCoilSchSeEJB.toLocAbleCheck] < " + mthdNms;
	  	String rtnMsg =	"";
	  	JDTORecord jrRtn  = JDTORecordFactory.getInstance().create();	//전문 Return

	  	try {
			
	    	commUtils.printLog(logId, mthdNm, "S+");

	    	String ydSchCd		= commUtils.trim(jrCoilCom.getFieldString("YD_SCH_CD"));
	    	String sStlNo 		= commUtils.nvl (jrCoilCom.getFieldString("STL_NO"),"0");
			String ydStkColGp 	= commUtils.trim(jrCoilCom.getFieldString("TAG_YD_STK_COL_GP"));
			String ydStkBedNo 	= commUtils.trim(jrCoilCom.getFieldString("TAG_YD_STK_BED_NO"));
			String ydStkLyrNo	= commUtils.trim(jrCoilCom.getFieldString("TAG_YD_STK_LYR_NO"));

			String sLeftStlNo	= commUtils.trim(jrCoilCom.getFieldString("TAG_LEFT_STL_NO"  ));		//2단일 경우 좌측
			String sRightStlNo	= commUtils.trim(jrCoilCom.getFieldString("TAG_RIGHT_STL_NO"));			//2단일 경우 우측		
	    	String sRtnVal 		= "";
	    	
	    	commUtils.printLog(logId, "★코일번호:"+ sStlNo+ "  적치가능대상코일위치:"+ ydStkColGp+ydStkBedNo+ydStkLyrNo + "  좌측코일번호:"+ sLeftStlNo+"  우측코일번호:"+ sRightStlNo, "SL");

	    	/******************************
	    	 * 제품 가상스판 BASE CHECK 제외
	    	 ******************************/
			if( "01".equals(ydStkColGp.substring(2,4)) || "80".equals(ydStkColGp.substring(2,4)) ) {
				commUtils.printLog(logId, "저장위치["+ ydStkColGp +"] 가상스판 제외", "SL");
	        	commUtils.printLog(logId, mthdNm, "S-");
	        	jrRtn.setField("LOC_ABLE_RTN", "1");
				return jrRtn;
			}

			/*****************************
			 * 결로존 보급 BASE CHECK 제외
			 *****************************/
			String sTagYdStkLyrActStat	= commUtils.trim(jrCoilCom.getFieldString("TAG_YD_STK_LYR_ACT_STAT"));
			String sTagYdStkBedNo		= commUtils.trim(jrCoilCom.getFieldString("TAG_YD_STK_BED_NO"));
			
			commUtils.printLog(logId, "   YD_STK_LYR_ACT_STAT["+ sTagYdStkLyrActStat +"] 저장위치["+ ydStkColGp+sTagYdStkBedNo +"]", "SL");
			if( "S".equals(sTagYdStkLyrActStat) ) {
				commUtils.printLog(logId, "결로존보급["+ ydStkColGp+sTagYdStkBedNo +"] 제외", "SL");
	        	commUtils.printLog(logId, mthdNm, "S-");
	        	jrRtn.setField("LOC_ABLE_RTN", "1");
				return jrRtn;
			}
			
	    	/******************************
	    	 * 스크랩 저장위치 제외
	    	 ******************************/
			if( "SC".equals(ydStkColGp.substring(2,4)) ) {
				commUtils.printLog(logId, "저장위치["+ ydStkColGp +"] 스크랩 제외", "SL");
	        	commUtils.printLog(logId, mthdNm, "S-");
	        	jrRtn.setField("LOC_ABLE_RTN", "1");
				return jrRtn;
			}
			
	    	/******************************
	    	 * 소재 결속장 제외
	    	 ******************************/
			if( "FE".equals(ydStkColGp.substring(2,4)) ) {
				commUtils.printLog(logId, "저장위치["+ ydStkColGp +"] 결속장 제외", "SL");
	        	commUtils.printLog(logId, mthdNm, "S-");
	        	jrRtn.setField("LOC_ABLE_RTN", "1");
				return jrRtn;
			}
	    	
			if (sStlNo.length() == 0) {
				jrRtn.setField("LOC_ABLE_RTN"  		, "-1");
				jrRtn.setField("LOC_ABLE_CONTENTS"	, "<적치불가:대상재 READ 실패>");
    			
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;	
			}
			
			JDTORecord jrParam2 = commUtils.getParam(logId, mthdNm, "toLocAbleCheck");
			jrParam2.setField("YD_STK_COL_GP", commUtils.trim(ydStkColGp));
			
			String sYD_STKBED_USG_CD ="";
			/*
			 * SELECT *
				  FROM TB_YD_STKCOL
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND DEL_YN = 'N'
			 */
			JDTORecordSet jsStkCol = commDao.select(jrParam2, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdStkcol", logId, mthdNm, "TB_YD_STKCOL 대상 데이터");
	    	if(jsStkCol.size() >= 1){
			
	    		jsStkCol.first();
		    	JDTORecord jrStkCol = jsStkCol.getRecord();
				
				sYD_STKBED_USG_CD = commUtils.trim(jrStkCol.getFieldString("YD_STKBED_USG_CD"));
				//용도코드 : 수입:S , 보급1: J , 보급2: K , 광폭존 : W 
			}
	    	commUtils.printLog(logId,"   해당저장위치 용도구분 코드:" + sYD_STKBED_USG_CD, "SL");
	    	
			
			String sThick      	 		= commUtils.trim(jrCoilCom.getFieldString("C_THICK"));   			// 두께          
			String sWidth      	 		= commUtils.trim(jrCoilCom.getFieldString("C_WIDTH"));   			// 폭           
			String sWeigth      		= commUtils.trim(jrCoilCom.getFieldString("C_WEIGTH"));   			// 중량          
			String sOutDia      		= commUtils.trim(jrCoilCom.getFieldString("C_OUTDIA"));   			// 외경    
			String sYeojaeCauseCd 		= commUtils.trim(jrCoilCom.getFieldString("C_YEOJAE_CAUSE_CD"));   	// 여재구분    
			long   lngHotCoilTm     	= Long.parseLong( commUtils.nvl(jrCoilCom.getFieldString("C_HOT_COIL_MIN"),"0")); //HOT코일시간

			String sLeftThick      		= commUtils.trim(jrCoilCom.getFieldString("L_THICK"));   		           
			String sLeftWidth      		= commUtils.trim(jrCoilCom.getFieldString("L_WIDTH"));   		           
			String sLeftWeigth     		= commUtils.trim(jrCoilCom.getFieldString("L_WEIGTH"));   		          
			String sLeftOutDia     		= commUtils.trim(jrCoilCom.getFieldString("L_OUTDIA"));   		
			String sLeftYeojaeCauseCd 	= commUtils.trim(jrCoilCom.getFieldString("L_YEOJAE_CAUSE_CD"));     
			long   lngLeftHotCoilTm 	= Long.parseLong( commUtils.nvl(jrCoilCom.getFieldString("L_HOT_COIL_MIN"),"0"));
			String sLeftHrSpecAbbsym	= commUtils.trim(jrCoilCom.getFieldString("L_HR_SPEC_ABBSYM"));		// 1단 강종(SPFH590Y) 2단적치 불가('N')

	    	String sRightThick     		= commUtils.trim(jrCoilCom.getFieldString("R_THICK"));   		           
	    	String sRightWidth     		= commUtils.trim(jrCoilCom.getFieldString("R_WIDTH"));   		           
	    	String sRightWeigth    		= commUtils.trim(jrCoilCom.getFieldString("R_WEIGTH"));   		          
	    	String sRightOutDia    		= commUtils.trim(jrCoilCom.getFieldString("R_OUTDIA"));   		
	    	String sRightYeojaeCauseCd 	= commUtils.trim(jrCoilCom.getFieldString("R_YEOJAE_CAUSE_CD"));    
	    	long   lngRightHotCoilTm   	= Long.parseLong( commUtils.nvl(jrCoilCom.getFieldString("R_HOT_COIL_MIN"),"0"));
	    	String sRightHrSpecAbbsym	= commUtils.trim(jrCoilCom.getFieldString("R_HR_SPEC_ABBSYM"));		// 1단 강종(SPFH590Y) 2단적치 불가('N')
		    	
		    //대상코일정보	
	    	String sCoilOutdiaGp   		= commUtils.trim(jrCoilCom.getFieldString("C_COIL_OUTDIA_GP"));      
	    	String sCoilWGp    			= commUtils.trim(jrCoilCom.getFieldString("C_COIL_W_GP"));   	    
	    	//대상위치정보
	    	String sColOutdiaGp    		= commUtils.trim(jrCoilCom.getFieldString("COL_OUTDIA_GRP_GP"));      
	    	String sColWGp    			= commUtils.trim(jrCoilCom.getFieldString("COL_W_GP"));   		     
	    	String sColMaxBedNo  		= commUtils.trim(jrCoilCom.getFieldString("COL_MAX_BED_NO"));   
	    	String sColYdLocGp	        = commUtils.trim(jrCoilCom.getFieldString("COL_YD_LOC_GP"));       // 대상위치 소재 / 제품위치 구분
	    	String sColYdStkSkidGp	    = commUtils.trim(jrCoilCom.getFieldString("COL_YD_STK_SKID_GP"));  // 대상위치 SKID 구분 'F' : 고정,'C' :가변
	    	String sCrnAutoYn	        = commUtils.nvl (jrCoilCom.getFieldString("YD_EQP_WRK_MODE2"),"A");    // 해당크레인 오토여부 ('A':무인)
	    	/*************************
	    	 * rule table 기준정보 read
	    	 *************************/
			
	    	//long lngRuleODia1		= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("ODIA_1")		, "180")); 	//chk1ODiaDiff
	    	//long lngRuleWid1		= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("WID_1")		, "200")); 	//chk1WidDiff
	    	//long lngRuleCrnLift 	= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("CRANE_LEFT")	, "500")); 	//chk1WidthInterf
	    	long lngRuleWid2H		= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("WID_2_H")	, "300"));	//chk2WgtWidDiff (소재 2단 폭편차 300)
	    	long lngRuleWgt2H		= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("WGT_2_H")	, "2000"));	//chk2WgtWidDiff (소재 수입 1000kg 이하, 그외 2000kg 2단 가능)
	    	if( ydSchCd.indexOf("CV01LH") > -1 ) {
	    		lngRuleWgt2H		= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("WGT_CV_2_H")	, "1000"));
	    	}

			/*************************************************
			 * 소재/제품 기준값 분리
			 *    - 1단 좌우 외경편차
			 *    - 1단 좌우 폭 편차
			 *    - 1단 전후열좌우 폭간섭
			 *    - TO위치 2단일 경우 중량/폭편차
			 *************************************************/
	    	long lngRuleODia1	= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("ODIA_1_"+ sColYdLocGp)  		, "180")); 	//chk1ODiaDiff
			commUtils.printLog(logId, "   ODIA_1_"+ sColYdLocGp +"    :"+ lngRuleODia1, "SL");
			long lngRuleWid1		= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("WID_1_"+ sColYdLocGp)   	, "200")); 	//chk1WidDiff
			commUtils.printLog(logId, "   WID_1_"+ sColYdLocGp +"     :"+ lngRuleWid1, "SL");
			long lngRuleCrnLift	= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("CRANE_LEFT_"+ sColYdLocGp)	, "650"));
			commUtils.printLog(logId, "   CRANE_LEFT_"+ sColYdLocGp +":"+ lngRuleCrnLift, "SL");
			
	    	long lngRuleHotCoilTm	= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("HOT_COIL_TM")	,"48"));  	//chk2Yeojae
	    	long lngRuleWgtWid		= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("WGT_WID")  		,"0"));   	//chk2WgtWidDiff (제품 0,ABC: 100)
	    	long lngRuleWgtWidABC	= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("WGT_WID_ABC")	,"100"));   //chk2WgtWidDiff (제품 0,ABC: 100)
	    	
	    	long lngRuleADiaDiff	= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("A_ODIA_2")   	,"550"));   // 
	    	long lngRuleBDiaDiff	= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("B_ODIA_2")   	,"820"));   // 
	    	long lngRuleCDiaDiff	= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("C_ODIA_2")   	,"850"));   // 
	    	
	    	long lngADiaDiffABC		= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("A_ODIA_ABC")   	,"550"));   // 
	    	long lngBDiaDiffABC		= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("B_ODIA_ABC")   	,"820"));   // 
	    	long lngCDiaDiffABC		= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("C_ODIA_ABC")   	,"850"));   // 

	    	long lngRuleODia2		= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("ODIA_2")     	,"180"));   //chk2ODiaDiff
	    	// 수입 2단위치 1단 코일들 외경편차
	    	if( ydSchCd.indexOf("CV01LH") > -1 ) {
	    		lngRuleODia2		= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("ODIA_CV_2")     	,"180")); // 수입2단 적치시 1단코일들 외경편차
	    	}
	    	commUtils.printLog(logId,"   ODIA_2:"+ lngRuleODia2, "SL");
	    		
	    	long lngRuleBODiaABC	= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("B_GUN_ODIA_ABC")	,"1500"));  //chk2ODiaBGunDiffABC

	    	long lngRuleCvDongHotCoilTm	= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("CV_HOTCOIL_"+ydStkColGp.substring(1,2))	,"50"));	//수입 HOTCOIL 경과시간 체크
	    	long lngRuleCvHotCoilTm 	= Long.parseLong(commUtils.nvl(jrRuleConst.getFieldString("CV_HOTCOIL_TM")	,"50"));	//수입 HOTCOIL 경과시간 체크
			if ("F".equals(sColYdStkSkidGp)) {
				lngRuleWgtWid 	= lngRuleWgtWidABC;
				lngRuleADiaDiff	= lngADiaDiffABC;	// 
		    	lngRuleBDiaDiff	= lngBDiaDiffABC;   // 
		    	lngRuleCDiaDiff	= lngCDiaDiffABC;   // 
			}
			commUtils.printLog(logId,"   해당크레인 유무인여부:" + sCrnAutoYn+" >> CV_HOTCOIL_"+ydStkColGp.substring(1,2), "SL");
			commUtils.printLog(logId,"CV_HOTCOIL_"+ydStkColGp.substring(1,2)+" >>> " + lngRuleCvDongHotCoilTm , "SL");
			commUtils.printLog(logId,"CV_HOTCOIL_TM : " + lngRuleCvHotCoilTm , "SL");
			//sjh0621
			String sWidDiaChkYn = commUtils.nvl(jrLocRuleApply.getFieldString("WID_DIA_CHK_YN"), "Y");
			commUtils.printLog(logId,"   sWidDiaChkYn:" + sWidDiaChkYn , "SL");
			
			/******************************************************************* 
			 * 폭구분 계산
			 * 소재 제외 
			******************************************************************/
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0010 적용여부:"+ jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0010"), "SL");

			if (("Y".equals(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0010"))) && "A".equals(sCrnAutoYn)) {
	        	commUtils.printLog(logId, "     ==================== 폭 구분한다.====================", "SL");
	        	if(!sColWGp.equals(sCoilWGp)){

	    			rtnMsg = "     ▶ 폭 구분확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 코일"+ sCoilWGp+ "과 적치위치"+ sColWGp+ "구분이 틀립니다..";
	    			commUtils.printLog(logId, rtnMsg, "SL");
					jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
					jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);
					jrRtn.setField("LOC_ABLE_CHK" 		, "N"); //권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
					return jrRtn;
	        	}
			}

			
			/******************************************************************* 
			 * 외경군 계산
			 * 소재 제외
			 ******************************************************************/           	
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0020 적용여부:"+ jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0020"), "SL");
			if (("Y".equals(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0020"))) && "A".equals(sCrnAutoYn)) {
				commUtils.printLog(logId, "     ==================== 외경군 계산한다.====================", "SL");
	        	if(!sColOutdiaGp.equals(sCoilOutdiaGp)){
	        		
	    			rtnMsg = "     ▶ 외경군확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 코일"+ sCoilOutdiaGp+ "과 적치위치"+ sColOutdiaGp+ "군이 틀립니다..";
	    			commUtils.printLog(logId, rtnMsg, "SL");
					jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
					jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);
					jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
					return jrRtn;
	        	}
			}
			/******************************************************************* 
			 * 1단일 경우 좌우외경편차를 계산한다.
			 ******************************************************************/        	
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0030 적용여부:"+ jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0030"), "SL");
			//SJH0621
			if ("Y".equals(sWidDiaChkYn)) {
				if (("Y".equals(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0030"))) && "A".equals(sCrnAutoYn)) {
		        	if ("001".equals(ydStkLyrNo)) {
		        		
		            	commUtils.printLog(logId, "     ====================1단일 경우 좌우외경편차를 계산한다.====================", "SL");
		        		
		        		/********************************
		        		 * 수입 1단 외경편차CHK 제외
		        		 ********************************/
		        		if( ydSchCd.indexOf("CV01LH") > -1 ) {
	        				sRtnVal = CConstant.RETN_CD_SUCCESS;
	
		        		} else {
						
			        		commUtils.printLog(logId, "   "+ sStlNo +"-"+ydStkLyrNo+"-"+sOutDia+sLeftStlNo+"-"+sLeftOutDia+"-"+sRightStlNo+"-"+sRightOutDia , "SL");
		        			sRtnVal = this.chk1ODiaDiff( logId, mthdNm, sStlNo, ydStkLyrNo, sOutDia, sLeftStlNo, sLeftOutDia,  sRightStlNo, sRightOutDia, lngRuleODia1);
		        		}
						    		
		        		if (sRtnVal.equals(CConstant.RETN_CD_FAILURE)) {
		        			
		        			rtnMsg = "     ▶ 1단 : 외경편차확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 코일과 외경차이("+ lngRuleODia1 +")로 적치가 불가능한 1 단 입니다.";
		        			commUtils.printLog(logId, rtnMsg, "SL");
							jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
							jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);
							jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
							return jrRtn;
		        		}
		        	}
				}
			}	
			/******************************************************************* 
			 * 1단일 경우 좌우폭편차를 계산한다
			 ******************************************************************/
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0040 적용여부:"+ jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0040"), "SL");
			//SJH0621
			if ("Y".equals(sWidDiaChkYn)) {   // 광폭야드 인경우 폭편차 제외
				if (("Y".equals(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0040"))) && "A".equals(sCrnAutoYn)) {
		
		        	if ("001".equals(ydStkLyrNo)) {
		        		
		        		commUtils.printLog(logId, "     ====================1단일 경우 좌우폭기준를 계산한다.====================", "SL");
		        		
		        		/********************************
		        		 * 수입 1단 폭 편차CHK 제외
		        		 ********************************/
		        		if( ydSchCd.indexOf("CV01LH") > -1 ) {
	        				sRtnVal = CConstant.RETN_CD_SUCCESS;
	
		        		} else {
	
		        			sRtnVal = this.chk1WidDiff( logId, mthdNm, sStlNo, ydStkLyrNo, sWidth, sLeftStlNo, sLeftWidth,  sRightStlNo, sRightWidth,lngRuleWid1);
		        		}
		        		
		        		if (sRtnVal.equals(CConstant.RETN_CD_FAILURE)) {
		        			
		        			rtnMsg = "     ▶ 1단 : 폭 기준확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 코일과 폭기준(180mm)로 적치가 불가능한 1단 입니다.";
		        			commUtils.printLog(logId, rtnMsg, "SL");
							jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
							jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);
							jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
							return jrRtn;
		        		}
		        	}
				}
			}	
    		/******************************************************************* 
			 *  1단일 경우 전후열좌우 폭간섭를 계산한다
			 ******************************************************************/
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0050 적용여부:"+ jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0050"), "SL");
			if ( !"W".equals(sYD_STKBED_USG_CD)) {   // 광폭야드 인경우 폭편차 제외 2022.08.24 chito
				if (("Y".equals(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0050"))) && "A".equals(sCrnAutoYn)) {
	
		        	if ("001".equals(ydStkLyrNo) && commUtils.isNumeric(ydStkColGp.substring(2,4)) ) {
	
		        		commUtils.printLog(logId, "     ====================1단일 경우 전후열좌우 폭간섭를 계산한다.====================", "SL");
		        		
		            	sRtnVal = this.chk1WidInterf( logId, mthdNm, sStlNo, ydStkColGp, ydStkBedNo, ydStkLyrNo 
			            			                           , sColWGp, sWidth, sOutDia, sColMaxBedNo, sColYdStkSkidGp, lngRuleCrnLift);
		        		
		        		if (sRtnVal.equals(CConstant.RETN_CD_FAILURE)) {
			        		
		        			rtnMsg = "     ▶ 1단 : 전후열좌우 폭간섭확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 1단 전후열좌우 폭간섭으로 적치가 불가능한 위치입니다.";
		        			commUtils.printLog(logId, rtnMsg, "SL");
							jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
							jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);
							jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
							return jrRtn;
		        		}
		        	}
				}
			}

			/******************************************************************* 
			 *  좌표값으로 전후열좌우 폭간섭를 계산한다
			 ******************************************************************/
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0060 적용여부:"+ jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0060"), "SL");
			//if ( !"W".equals(sYD_STKBED_USG_CD)) {   // 광폭야드 인경우 폭편차 제외 2022.08.24 chito
			// 광폭열에서 1800 이상일때 크레인 에러로 인해 체크 YYS 2023 03 06 
			if (("Y".equals(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0060"))) && "A".equals(sCrnAutoYn)) {
				
				if( commUtils.isNumeric(ydStkColGp.substring(2,4) ) ) {

	        		commUtils.printLog(logId, "     ==================== 좌표값으로 전후열좌우 폭간섭 를 계산한다.====================", "SL");
	        		sRtnVal = "";
	        		/**********************
	        		 * 소재야드 분리
	        		 **********************/
	        		String sAPP010_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","010"); 
	    			commUtils.printLog(logId, "광폭열에서 폭간섭 체크 여부 : " + sAPP010_YN, "SL");	    			
	    			
	        		if( "H".equals(sColYdLocGp) ) {
	        			if ( "Y".equals(sAPP010_YN) ) { 
	    				    sRtnVal = this.chkColWidInterfH( logId, mthdNm, sStlNo, ydStkColGp, ydStkBedNo, ydStkLyrNo, lngRuleCrnLift);
		    			} else if("N".equals(sAPP010_YN) && !"W".equals(sYD_STKBED_USG_CD) ){
		    				sRtnVal = this.chkColWidInterfH( logId, mthdNm, sStlNo, ydStkColGp, ydStkBedNo, ydStkLyrNo, lngRuleCrnLift);
		    			}
	        			//sRtnVal = this.chkColWidInterfH( logId, mthdNm, sStlNo, ydStkColGp, ydStkBedNo, ydStkLyrNo, lngRuleCrnLift);
	        		} else if("J".equals(sColYdLocGp) && !"W".equals(sYD_STKBED_USG_CD)){ //제품은 광폭열 체크하지 않음
	        			
	        			sRtnVal = this.chkColWidInterf( logId, mthdNm, sStlNo, ydStkColGp, ydStkBedNo, ydStkLyrNo, lngRuleCrnLift);
	        		}
	        		
	        		if (sRtnVal.equals(CConstant.RETN_CD_FAILURE)) {

		        		rtnMsg = "     ▶ 좌표값 전후열좌우 폭간섭확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 1단 전후열좌우 폭간섭으로 적치가 불가능한 위치입니다.";
	        			commUtils.printLog(logId, rtnMsg, "SL");
						jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
						jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);	
						jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
						return jrRtn;
	        		}
				}
			}
			//}
			
			/*******************************************************************  
			 * 짱구 (유무인 둘다 체크함)
			 ******************************************************************/
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0070 적용여부:"+ jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0070"), "SL");
			if ("Y".equals(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0070"))) {
			    if ("002".equals(ydStkLyrNo) && !"".equals(sLeftStlNo) && !"".equals(sRightStlNo)) {
	        		commUtils.printLog(logId, "     ==================== 2단일 경우 짱구코일 2단적치 제외  START.====================", "SL");

			    	if (lngHotCoilTm > lngRuleHotCoilTm){   		//48
	        			sYeojaeCauseCd = "";
	        		}
	        		if (lngLeftHotCoilTm > lngRuleHotCoilTm){   	//48
	        			sLeftYeojaeCauseCd = "";
	        		}
	        		if (lngRightHotCoilTm > lngRuleHotCoilTm){   	//48
	        			sRightYeojaeCauseCd = "";
	        		}
	            	
	            	sRtnVal = this.chk2Yeojae(logId, mthdNm, sStlNo, sYeojaeCauseCd, sLeftStlNo, sLeftYeojaeCauseCd, sRightStlNo, sRightYeojaeCauseCd);
	        		
	        		if (sRtnVal.equals(CConstant.RETN_CD_FAILURE)) {
	        			rtnMsg = "     ▶ 2단 : 짱구 코일확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo + ") 짱구 코일 2단 적치 제한에 해당합니다.";
						jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
						jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);
						jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
						return jrRtn;
	        		}
	        	}
			}  
    		/******************************************************************* 
			 * 2단일 경우 1단좌우외경편차 를 계산
			 ******************************************************************/
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0080 적용여부:"+ jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0080"), "SL");
			if (("Y".equals(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0080"))) && "A".equals(sCrnAutoYn)) {

	        	if ("002".equals(ydStkLyrNo) && !"".equals(sLeftStlNo) && !"".equals(sRightStlNo)) {
		        	commUtils.printLog(logId, "     ====================2단인경우1단좌우외경편차====================", "SL");

		        	sRtnVal = this.chk2ODiaDiff( logId, mthdNm, ydStkLyrNo, sStlNo,  sOutDia
		        			                                              , sLeftStlNo , sLeftOutDia
		        			                                              , sRightStlNo, sRightOutDia
		        			                                              , lngRuleODia2);
	 
	        		if (sRtnVal.equals(CConstant.RETN_CD_FAILURE)) {
	        			rtnMsg = "     ▶ 2단 : 1단 좌우 외경 편차확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 1단 좌우 외경편차("+lngRuleODia2+")로 적치가 불가능한 2단 입니다.";
	        			commUtils.printLog(logId, rtnMsg, "SL");
						jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
						jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);	
						jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
						return jrRtn;
	        		}
	        	}        	
			}			
    		/******************************************************************* 
			 * 2단일 경우 중량/폭편차를 계산한다. searchCoilGdsYdWtCheckABC
			 ******************************************************************/         	
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0090 적용여부:"+ jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0090"), "SL");
			if (("Y".equals(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0090"))) && "A".equals(sCrnAutoYn)) {

	        	if ("002".equals(ydStkLyrNo) && !"".equals(sLeftStlNo) && !"".equals(sRightStlNo)) {

	        		commUtils.printLog(logId, "     ====================2단일 경우 중량/폭편차를 계산한다.====================", "SL");
	        		
	        		if( "H".equals(sColYdLocGp) ) {
	        			
		        		sRtnVal = chk2WgtWidDiffH(logId, mthdNm, ydSchCd, ydStkColGp
		        								, sStlNo		, sWidth		, sWeigth
		        								, sLeftStlNo	, sLeftWidth	, sLeftWeigth
		        								, sRightStlNo	, sRightWidth	, sRightWeigth
		        								, lngRuleWgt2H	// 2단 수입중량 제한
		        								, lngRuleWid2H	// 2단 폭 편차
                                				);
		        		commUtils.printLog(logId, "     chk2WgtWidDiffH : " + sRtnVal, "SL");
	        		} else {
	        		
		        		sRtnVal = chk2WgtWidDiff(logId, mthdNm,  ydStkColGp, sStlNo     , sThick     , sWidth    , sWeigth
		        				                                           , sLeftStlNo , sLeftThick ,sLeftWidth , sLeftWeigth
		        				                                           , sRightStlNo, sRightThick,sRightWidth, sRightWeigth
		        				                                           , lngRuleWgtWid  //ABC : + 100
		        				                                           );
		        		commUtils.printLog(logId, "     중량CHECK :" + sRtnVal, "SL");
	        		}
	                
	        		if (sRtnVal.equals(CConstant.RETN_CD_FAILURE)) {
	        			rtnMsg = "     ▶ 2단 : 중량/폭 편차확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 1단 코일과 중량/폭편차로 적치가 불가능한 2단 입니다.";
	        			commUtils.printLog(logId, rtnMsg, "SL");
						jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
						jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);	
						jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
	    				return jrRtn;
	           		} else if ("WID_FAILURE".equals(sRtnVal)) {
	        			rtnMsg = "     ▶ 2단 : 폭 편차확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 1단 코일과폭편차로 적치가 불가능한 2단 입니다.";
	        			commUtils.printLog(logId, rtnMsg, "SL");
						jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
						jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);	
						jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
	    				return jrRtn;
	           		} else if ("WGT_FAILURE".equals(sRtnVal)) {
	        			rtnMsg = "     ▶ 2단 중량 편차확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 1단 코일과중량편차로 적치가 불가능한 2단 입니다.";
	        			commUtils.printLog(logId, rtnMsg, "SL");
	    				jrRtn.setField("LOC_ABLE_RTN" 	, "-1");	
	    				jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);	
	    				jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
	    				return jrRtn;
	        		}
	        	}
			}		

    		/******************************************************************* 
			 *  2단일 경우 군별 외경간격을 계산한다
			 *  소재 제외
			 ******************************************************************/           	
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0100 적용여부:"+ jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0100"), "SL");
			if (("Y".equals(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0100"))) && "A".equals(sCrnAutoYn)) {

				if ("002".equals(ydStkLyrNo) && !"".equals(sLeftStlNo) && !"".equals(sRightStlNo)) {

					commUtils.printLog(logId, "     ====================2단일 경우 외경간격을 계산한다.====================", "SL");
	        		
					sRtnVal = this.chk2ODiaInterval(logId, mthdNm, ydStkColGp		, ydStkBedNo , ydStkLyrNo 
	        				                                     , sStlNo    		, sOutDia    , sColOutdiaGp
	        				                                     , sLeftStlNo		, sLeftOutDia
	        				                                     , sRightStlNo		, sRightOutDia
	        				                                     , sColYdStkSkidGp
	        				                                     , lngRuleADiaDiff	
	        				                                     , lngRuleBDiaDiff	
	        				                                     , lngRuleCDiaDiff	
	        				                                     );
	
	        		if (sRtnVal.equals(CConstant.RETN_CD_FAILURE)) {
	        			rtnMsg = "     ▶ 2단 : 외경간격확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 1단 코일이 외경간격편차로 적치가 불가능한 2단 입니다.";
	        			commUtils.printLog(logId, rtnMsg, "SL");
						jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
						jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);
						jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
						return jrRtn;
	        		}
	        	}
			}		
			
        	
    		/******************************************************************* 
			 * B군 인경우 2단일 경우 외경편차ABC를 계산한다.searchCoilYdGdsWidthDiffCheck3
			 ******************************************************************/
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0110 적용여부:"+ jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0110"), "SL");
			if (("Y".equals(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0110"))) && "A".equals(sCrnAutoYn)) {
				if( "B".equals(sCoilOutdiaGp) && "F".equals(sColYdStkSkidGp)){
	            	if ("002".equals(ydStkLyrNo) && !"".equals(sLeftStlNo) && !"".equals(sRightStlNo)) {

	            		commUtils.printLog(logId, "     ====================2단일 경우 'B'군이고 SKID TYPE이 고정형인 경우 외경2(1500미만)를 계산한다.====================", "SL");
		        		sRtnVal = chk2ODiaBGunDiffABC( logId, mthdNm, ydStkLyrNo, sStlNo, sOutDia
		        				                                                , sLeftStlNo, sLeftOutDia
		        				                                                , sRightStlNo, sRightOutDia
		        				                                                , lngRuleBODiaABC);
		 
		        		if (sRtnVal.equals(CConstant.RETN_CD_FAILURE)) {
		        			rtnMsg = "     ▶ 2단 : 외경2확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 1단 코일과 외경(1500 미만)로 적치가 불가능한 2단 입니다.";
		        			commUtils.printLog(logId, rtnMsg, "SL");
							jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
							jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);
							jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
							return jrRtn;
		        		}
		        	}      
	    		}
			}				
			
    		/******************************************************************* 
			 * 2단일 경우 횡행좌표 거리 계산 값에 의한 외경 CHECK
			 ******************************************************************/
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0120 적용여부:"+ jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0120"), "SL");
			if (("Y".equals(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0120"))) && "A".equals(sCrnAutoYn)) {

				if ("002".equals(ydStkLyrNo) && !"".equals(sLeftStlNo) && !"".equals(sRightStlNo)) {

					commUtils.printLog(logId, "     ====================2단일 경우 2단코일간 간격체크(20mm)====================", "SL");
	        		
					sRtnVal = this.chk2OverRun( logId, mthdNm, ydStkColGp, ydStkBedNo, ydStkLyrNo
	        				                                 , sStlNo     , sOutDia
	        				                                 , sLeftStlNo , sLeftOutDia
	        				                                 , sRightStlNo, sRightOutDia);
	
	        		if (sRtnVal.equals(CConstant.RETN_CD_FAILURE)) {
	        			rtnMsg = "     ▶ 2단 : 2단코일간 간격확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 2단코일간 간격체크(20mm) 값에 의한 적치가 불가능한 2단 입니다.";
	        			commUtils.printLog(logId, rtnMsg, "SL");
						jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
						jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);	
						jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
						return jrRtn;
	        		}
	        	}
			}	
			
    		/******************************************************************* 
			 *  2단일 경우 기울기 공식 적용.
			 ******************************************************************/ 
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0130 적용여부:"+ jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0130"), "SL");
			if (("Y".equals(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0130"))) && "A".equals(sCrnAutoYn)) {
				
	        	if ("002".equals(ydStkLyrNo) && !"".equals(sLeftStlNo) && !"".equals(sRightStlNo)) {

	        		commUtils.printLog(logId, "     ====================2단일 경우 기울기를 계산한다.====================", "SL");
	 
	        		String sGrpGpLoc        = "";
	        		String sCoilNoA 		= "";
	        		String sCoilNoB 		= "";
	        		String sCoilNoC 		= "";
	        		String sCoilNoD 		= "";
	        		String sCoilNoE 		= "";
	        		String sCoilNoF 		= "";
	        		String sCoilNoG 		= "";
	        		
	        		long   lngOutDiaA 		= 0;
	        		long   lngOutDiaB 		= 0;
	        		long   lngOutDiaC 		= 0;
	        		long   lngOutDiaD 		= 0;
	        		long   lngOutDiaE 		= 0;
	        		long   lngOutDiaF 		= 0;
	        		long   lngOutDiaG 		= 0;
	        		
	        		JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
	        		jrParam.setField("YD_STK_COL_GP", ydStkColGp);
	        		jrParam.setField("YD_STK_BED_NO", ydStkBedNo);
	        		jrParam.setField("YD_STK_LYR_NO", ydStkLyrNo);
	        		
	        		JDTORecordSet jsStkLyr1 = null;
	        		
	        		if( "C".equals(sColYdStkSkidGp) ) {
		        		/******************************
		        		 * 가변 SKID 기울기 (기존)
		        		 ******************************/
	        			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilCline
	        			WITH TEMP_TABLE AS (
	        			    SELECT 'A' AS COIL_GP FROM DUAL UNION ALL 
	        			    SELECT 'B' AS COIL_GP FROM DUAL UNION ALL 
	        			    SELECT 'C' AS COIL_GP FROM DUAL UNION ALL 
	        			    SELECT 'D' AS COIL_GP FROM DUAL UNION ALL 
	        			    SELECT 'E' AS COIL_GP FROM DUAL UNION ALL 
	        			    SELECT 'F' AS COIL_GP FROM DUAL UNION ALL 
	        			    SELECT 'G' AS COIL_GP FROM DUAL 
	        			),
	        			 TEMP_TABLE1 AS (
	        			    SELECT :V_YD_STK_COL_GP AS YD_STK_COL_GP
	        				     , :V_YD_STK_BED_NO AS YD_STK_BED_NO
	        				  FROM DUAL   
	        			)
	        			SELECT 
	        			        AA.COIL_GP
	        			        , AA.YD_STK_COL_GP
	        			        , AA.YD_STK_BED_NO
	        			        , AA.YD_STK_LYR_NO
	        			        , AA.STL_NO
	        			        , AA.COIL_OUTDIA
	        			        , AA.COIL_CNT
	        			        , DECODE(BB.YD_COIL_OUTDIA_GRP_GP ,'A',2 * 650,'B',3 * 650 ,'C',4 * 650) GRP_GP_LOC
	        			        , AA.YD_COIL_OUTDIA_GRP_GP
	        			        , AA.COIL_GP1
	        			 FROM (
	        			        SELECT Z1.COIL_GP
	        			             , NVL(Z2.YD_STK_COL_GP,(SELECT YD_STK_COL_GP FROM TEMP_TABLE1 )) AS YD_STK_COL_GP
	        			             , Z2.YD_STK_BED_NO
	        			             , YD_STK_LYR_NO
	        			             , STL_NO
	        			             , COIL_OUTDIA
	        			             , COIL_CNT
	        			             , GRP_GP_LOC
	        			             , YD_COIL_OUTDIA_GRP_GP
	        			             , COIL_GP1
	        			          FROM 
	        			          (SELECT * FROM TEMP_TABLE
	        			            ORDER BY COIL_GP) Z1,
	        			           (SELECT A.YD_STK_COL_GP
	        			                 , A.YD_STK_BED_NO
	        			                 , A.YD_STK_LYR_NO
	        			                 , A.STL_NO
	        			                 , C.COIL_OUTDIA
	        			                 , COUNT(STL_NO) OVER() COIL_CNT
	        			                 , DECODE(B.YD_COIL_OUTDIA_GRP_GP ,'A',2 * 650,'B',3 * 650 ,'C',4 * 650) GRP_GP_LOC
	        			                 , B.YD_COIL_OUTDIA_GRP_GP
	        			                 ,(CASE WHEN A.YD_STK_COL_GP ||A.YD_STK_BED_NO||A.YD_STK_LYR_NO 
	        			                           = D.YD_STK_COL_GP||TRIM(TO_CHAR(TO_NUMBER(D.YD_STK_BED_NO) - DECODE(B.YD_COIL_OUTDIA_GRP_GP ,'A',2,'B',3,'C',4),'00'))||'001' THEN 'B' 
	        			                        WHEN A.YD_STK_COL_GP ||A.YD_STK_BED_NO||A.YD_STK_LYR_NO 
	        			                           = D.YD_STK_COL_GP||TRIM(TO_CHAR(TO_NUMBER(D.YD_STK_BED_NO) - DECODE(B.YD_COIL_OUTDIA_GRP_GP ,'A',2,'B',3,'C',4),'00'))||'002' THEN 'A' 
	        			                        WHEN A.YD_STK_COL_GP ||A.YD_STK_BED_NO||A.YD_STK_LYR_NO 
	        			                           = D.YD_STK_COL_GP||D.YD_STK_BED_NO||'001' THEN 'C' 
	        			                        WHEN A.YD_STK_COL_GP ||A.YD_STK_BED_NO||A.YD_STK_LYR_NO 
	        			                           = D.YD_STK_COL_GP||D.YD_STK_BED_NO||'002' THEN 'D' 
	        			                        WHEN A.YD_STK_COL_GP ||A.YD_STK_BED_NO||A.YD_STK_LYR_NO 
	        			                           = D.YD_STK_COL_GP||TRIM(TO_CHAR(TO_NUMBER(D.YD_STK_BED_NO) + DECODE(B.YD_COIL_OUTDIA_GRP_GP ,'A',2,'B',3,'C',4),'00'))||'001' THEN 'E' 
	        			                        WHEN A.YD_STK_COL_GP ||A.YD_STK_BED_NO||A.YD_STK_LYR_NO 
	        			                           = D.YD_STK_COL_GP||TRIM(TO_CHAR(TO_NUMBER(D.YD_STK_BED_NO) + DECODE(B.YD_COIL_OUTDIA_GRP_GP ,'A',2,'B',3,'C',4),'00'))||'002' THEN 'F' 
	        			                        WHEN A.YD_STK_COL_GP ||A.YD_STK_BED_NO||A.YD_STK_LYR_NO 
	        			                           = D.YD_STK_COL_GP||TRIM(TO_CHAR(TO_NUMBER(D.YD_STK_BED_NO) + DECODE(B.YD_COIL_OUTDIA_GRP_GP ,'A',4,'B',6,'C',8),'00'))||'001' THEN 'G' 
	        			                     ELSE'' END) COIL_GP1
	        			              FROM TB_YD_STKLYR A
	        			                 , TB_YD_STKCOL B
	        			                 , USRPTA.TB_PT_COILCOMM C 
	        			                 , TEMP_TABLE1 D
	        			             WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP 
	        			               AND A.YD_STK_COL_GP = D.YD_STK_COL_GP 
	        			               AND A.YD_STK_BED_NO  IN (D.YD_STK_BED_NO
	        			                                      , TO_NUMBER(D.YD_STK_BED_NO) - DECODE(B.YD_COIL_OUTDIA_GRP_GP ,'A',2,'B',3,'C',4)
	        			                                      , TO_NUMBER(D.YD_STK_BED_NO) + DECODE(B.YD_COIL_OUTDIA_GRP_GP ,'A',2,'B',3,'C',4)
	        			                                      , TO_NUMBER(D.YD_STK_BED_NO) + DECODE(B.YD_COIL_OUTDIA_GRP_GP ,'A',4,'B',6,'C',8)
	        			                                      ) 
	        			               AND A.STL_NO = C.COIL_NO(+)
	        			            )Z2
	        			        WHERE	Z1.COIL_GP = Z2.COIL_GP1(+)  
	        			        ) AA
	        			       , TB_YD_STKCOL BB
	        			    WHERE AA.YD_STK_COL_GP=BB.YD_STK_COL_GP
	        			    ORDER BY COIL_GP	        			
	        			*/
	        			jsStkLyr1 = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilCline", logId, mthdNm, "적치단 조회(가변SKID)");
	        			
	        		} else {
		        		/******************************
		        		 * 고정 SKID 기울기 (신규)
		        		 ******************************/
	        			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilClineFixedSkid
	        			WITH TEMP_TABLE AS (
	        			    SELECT 'A' AS COIL_GP FROM DUAL UNION ALL
	        			    SELECT 'B' AS COIL_GP FROM DUAL UNION ALL
	        			    SELECT 'C' AS COIL_GP FROM DUAL UNION ALL
	        			    SELECT 'D' AS COIL_GP FROM DUAL UNION ALL
	        			    SELECT 'E' AS COIL_GP FROM DUAL UNION ALL
	        			    SELECT 'F' AS COIL_GP FROM DUAL UNION ALL
	        			    SELECT 'G' AS COIL_GP FROM DUAL
	        			),
	        			 TEMP_TABLE1 AS (
	        			    SELECT :V_YD_STK_COL_GP AS YD_STK_COL_GP
	        			         , :V_YD_STK_BED_NO AS YD_STK_BED_NO
	        			      FROM DUAL
	        			)
	        			SELECT AA.COIL_GP
	        			     , AA.YD_STK_COL_GP
	        			     , AA.YD_STK_BED_NO
	        			     , AA.YD_STK_LYR_NO
	        			     , AA.STL_NO
	        			     , AA.COIL_OUTDIA
	        			     , AA.COIL_CNT
	        			     , AA.YD_COIL_OUTDIA_GRP_GP
	        			     , AA.COIL_GP1
	        			     , CC.GRP_GP_LOC -- BED Y 간격
	        			  FROM (SELECT Z1.COIL_GP
	        			             , NVL(Z2.YD_STK_COL_GP,(SELECT YD_STK_COL_GP FROM TEMP_TABLE1 )) AS YD_STK_COL_GP
	        			             , Z2.YD_STK_BED_NO
	        			             , YD_STK_LYR_NO
	        			             , STL_NO
	        			             , COIL_OUTDIA
	        			             , COIL_CNT
	        			             , YD_COIL_OUTDIA_GRP_GP
	        			             , COIL_GP1
	        			          FROM (SELECT * FROM TEMP_TABLE ORDER BY COIL_GP) Z1
	        			             , (SELECT A.YD_STK_COL_GP
	        			                     , A.YD_STK_BED_NO
	        			                     , A.YD_STK_LYR_NO
	        			                     , A.STL_NO
	        			                     , C.COIL_OUTDIA
	        			                     , COUNT(STL_NO) OVER() COIL_CNT
	        			                     , B.YD_COIL_OUTDIA_GRP_GP
	        			                     , (CASE WHEN A.YD_STK_COL_GP || A.YD_STK_BED_NO || A.YD_STK_LYR_NO
	        			                                = D.YD_STK_COL_GP || LPAD(TO_NUMBER(D.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(D.YD_STK_COL_GP), 2, '0') || '001' THEN 'B'
	        			                             WHEN A.YD_STK_COL_GP || A.YD_STK_BED_NO || A.YD_STK_LYR_NO
	        			                                = D.YD_STK_COL_GP || LPAD(TO_NUMBER(D.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(D.YD_STK_COL_GP), 2, '0') || '002' THEN 'A'
	        			                             WHEN A.YD_STK_COL_GP || A.YD_STK_BED_NO || A.YD_STK_LYR_NO
	        			                                = D.YD_STK_COL_GP || D.YD_STK_BED_NO || '001' THEN 'C'
	        			                             WHEN A.YD_STK_COL_GP || A.YD_STK_BED_NO || A.YD_STK_LYR_NO
	        			                                = D.YD_STK_COL_GP || D.YD_STK_BED_NO || '002' THEN 'D'
	        			                             WHEN A.YD_STK_COL_GP || A.YD_STK_BED_NO || A.YD_STK_LYR_NO
	        			                                = D.YD_STK_COL_GP || LPAD(TO_NUMBER(D.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(D.YD_STK_COL_GP), 2, '0') || '001' THEN 'E'
	        			                             WHEN A.YD_STK_COL_GP || A.YD_STK_BED_NO || A.YD_STK_LYR_NO
	        			                                = D.YD_STK_COL_GP || LPAD(TO_NUMBER(D.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(D.YD_STK_COL_GP), 2, '0') || '002' THEN 'F'
	        			                             WHEN A.YD_STK_COL_GP || A.YD_STK_BED_NO || A.YD_STK_LYR_NO
	        			                                = D.YD_STK_COL_GP || LPAD(TO_NUMBER(D.YD_STK_BED_NO) + (SF_YD_SKID_INTERVAL_GAP(D.YD_STK_COL_GP)*2), 2, '0') || '001' THEN 'G'
	        			                             ELSE '' END
	        			                       ) COIL_GP1
	        			                  FROM TB_YD_STKLYR A
	        			                     , TB_YD_STKCOL B
	        			                     , USRPTA.TB_PT_COILCOMM C
	        			                     , TEMP_TABLE1 D
	        			                 WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
	        			                   AND A.YD_STK_COL_GP = D.YD_STK_COL_GP
	        			                   AND A.YD_STK_BED_NO IN (D.YD_STK_BED_NO
	        			                                         , LPAD(TO_NUMBER(D.YD_STK_BED_NO) -  SF_YD_SKID_INTERVAL_GAP(D.YD_STK_COL_GP), 2, '0')
	        			                                         , LPAD(TO_NUMBER(D.YD_STK_BED_NO) +  SF_YD_SKID_INTERVAL_GAP(D.YD_STK_COL_GP), 2, '0')
	        			                                         , LPAD(TO_NUMBER(D.YD_STK_BED_NO) + (SF_YD_SKID_INTERVAL_GAP(D.YD_STK_COL_GP)*2), 2, '0')
	        			                                          )
	        			                   AND A.STL_NO = C.COIL_NO(+)
	        			               )Z2
	        			         WHERE Z1.COIL_GP = Z2.COIL_GP1(+)
	        			       ) AA
	        			     , TB_YD_STKCOL BB
	        			     , (SELECT ABS(YAXIS - YAXIS2) AS GRP_GP_LOC
	        			          FROM (SELECT ( -- 기준BED Y좌표값
	        			                        SELECT AA.YD_STK_LYR_YAXIS
	        			                          FROM TB_YD_STKLYR AA
	        			                             , TEMP_TABLE1 D
	        			                         WHERE AA.YD_STK_COL_GP = D.YD_STK_COL_GP
	        			                           AND AA.YD_STK_LYR_NO = '001'
	        			                           AND AA.YD_STK_BED_NO = D.YD_STK_BED_NO
	        			                       ) AS YAXIS
	        			                     , ( -- 이전 or 다음 BED Y 좌표값
	        			                        SELECT YD_STK_LYR_YAXIS
	        			                          FROM TB_YD_STKLYR AA
	        			                             , TEMP_TABLE1 D
	        			                         WHERE AA.YD_STK_COL_GP = D.YD_STK_COL_GP
	        			                           AND AA.YD_STK_LYR_NO = '001'
	        			                           AND 'Y' = CASE WHEN TO_NUMBER(D.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(D.YD_STK_COL_GP) < 1
	        			                                           AND AA.YD_STK_BED_NO = LPAD(TO_NUMBER(D.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(D.YD_STK_COL_GP), 2, '0')
	        			                                               THEN 'Y'
	        			                                          WHEN AA.YD_STK_BED_NO = LPAD(TO_NUMBER(D.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(D.YD_STK_COL_GP), 2, '0')
	        			                                               THEN 'Y'
	        			                                          ELSE 'N'
	        			                                     END
	        			                       ) AS YAXIS2
	        			                  FROM DUAL
	        			               )
	        			       ) CC
	        			 WHERE AA.YD_STK_COL_GP = BB.YD_STK_COL_GP
	        			 ORDER BY COIL_GP
		        		*/
	        			jsStkLyr1 = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilClineFixedSkid", logId, mthdNm, "적치단 조회(고정SKID)");
	        		}
	     	    	
	        		
	            	
	            	if (jsStkLyr1.size() < 1) {
	    				rtnMsg = "     ▶ 2단 : 기울기확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ")로 조회중 error 발생! ";
	    				commUtils.printLog(logId, rtnMsg, "SL");
						jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
						jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);
						jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
	    				return jrRtn;
	            	}
	            	
	            	JDTORecord jrStkLyr1 = JDTORecordFactory.getInstance().create();	
	            	for(int Loop_i = 1; Loop_i <= jsStkLyr1.size(); Loop_i++) {
						jsStkLyr1.absolute(Loop_i);
						jrStkLyr1 = jsStkLyr1.getRecord();
						
						String sCoilGp		= commUtils.trim(jrStkLyr1.getFieldString("COIL_GP"));
						//좌측2단
						if ("A".equals(sCoilGp)) {
							sCoilNoA 		= commUtils.trim(jrStkLyr1.getFieldString("STL_NO")); 
							lngOutDiaA   	= Long.parseLong(commUtils.nvl(commUtils.trim(jrStkLyr1.getFieldString("COIL_OUTDIA")),"0"));
							sGrpGpLoc		= commUtils.nvl(commUtils.trim(jrStkLyr1.getFieldString("GRP_GP_LOC")),"0");  
						//좌측1단
						} else if ("B".equals(sCoilGp)) {
							sCoilNoB 		= commUtils.trim(jrStkLyr1.getFieldString("STL_NO")); 
							lngOutDiaB 		= Long.parseLong(commUtils.nvl(commUtils.trim(jrStkLyr1.getFieldString("COIL_OUTDIA")),"0"));  
						//대상1단
						} else if ("C".equals(sCoilGp)) {
							sCoilNoC 		= commUtils.trim(jrStkLyr1.getFieldString("STL_NO"));
							lngOutDiaC 		= Long.parseLong(commUtils.nvl(commUtils.trim(jrStkLyr1.getFieldString("COIL_OUTDIA")),"0"));  
						//대상2단 : 목적
						} else if ("D".equals(sCoilGp)) {
							sCoilNoD 		= sStlNo;
							lngOutDiaD 	 	= Long.parseLong(commUtils.nvl(sOutDia,"0"));  
						//우측1단
						} else if ("E".equals(sCoilGp)) {
							sCoilNoE 		= commUtils.trim(jrStkLyr1.getFieldString("STL_NO"));
							lngOutDiaE 		= Long.parseLong(commUtils.nvl(commUtils.trim(jrStkLyr1.getFieldString("COIL_OUTDIA")),"0"));  
						//우측2단
						} else if ("F".equals(sCoilGp)) {
							sCoilNoF 		= commUtils.trim(jrStkLyr1.getFieldString("STL_NO"));
							lngOutDiaF 		= Long.parseLong(commUtils.nvl(commUtils.trim(jrStkLyr1.getFieldString("COIL_OUTDIA")),"0"));  
						//우측+1 1단
						} else if ("G".equals(sCoilGp)) {
							sCoilNoG 		= commUtils.trim(jrStkLyr1.getFieldString("STL_NO")); 
							lngOutDiaG 		= Long.parseLong(commUtils.nvl(commUtils.trim(jrStkLyr1.getFieldString("COIL_OUTDIA")),"0"));  
						}
					}
	     	    		
	     	    	if((!"".equals(sCoilNoA)) 
	     	    			&& ( lngOutDiaB >= lngOutDiaC) 
	     	    			&& ( lngOutDiaE >= lngOutDiaC)){
	
	       				commUtils.printLog(logId, "     좌측2단에 코일이 있고 우측1,2단 코일이 대상1단 보다 큰 경우 기울기 공식적용 ", "SL");
	
	       				sRtnVal = chk2ClineFormal( logId, mthdNm, ydStkColGp, ydStkBedNo, ydStkLyrNo, sGrpGpLoc
	       						                        ,sCoilNoA,lngOutDiaA
	       						                        ,sCoilNoB,lngOutDiaB
	       						                        ,sCoilNoC,lngOutDiaC
	               				                        ,sCoilNoD,lngOutDiaD
	               				                        ,sCoilNoE,lngOutDiaE);
	
	               		if (sRtnVal.equals(CConstant.RETN_CD_FAILURE)) {
	            			rtnMsg = "     ▶ 2단 : 기울기확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 2단 기울기 편차 불가 합니다..";
	            			commUtils.printLog(logId, rtnMsg, "SL");
	    					jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
	    					jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);	
	    					jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
	    					return jrRtn;
	            		}
	     	    	} else if((!"".equals(sCoilNoF)) && (!"".equals(sCoilNoG)) 
	     	    			&& ( lngOutDiaC >= lngOutDiaE ) 
	     	    			&& ( lngOutDiaG >= lngOutDiaE)){	
	
	     	    		commUtils.printLog(logId, "     우측2단/우측+1단 코일이 있고 대상코일과우측+1단코일이 우측1단코일보다 큰경우 기울기 공식적용 ", "SL");
	       				sRtnVal = chk2ClineFormal( logId, mthdNm, ydStkColGp, ydStkBedNo, ydStkLyrNo, sGrpGpLoc
	       												,sCoilNoD,lngOutDiaD
	       												,sCoilNoC,lngOutDiaC
	       												,sCoilNoE,lngOutDiaE
	       						                        ,sCoilNoF,lngOutDiaF
	       						                        ,sCoilNoG,lngOutDiaG);
	
	               		if (sRtnVal.equals(CConstant.RETN_CD_FAILURE)) {
	            			rtnMsg = "     ▶ 2단일 경우 기울기확인:"+sStlNo+"적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo  + ") 2단 기울기 편차 불가 합니다..";
	            			commUtils.printLog(logId, rtnMsg, "SL");
	    					jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
	    					jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);	
	    					jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
	    					return jrRtn;
	            		}
	            	} else {
	               		commUtils.printLog(logId, "     기울기공식 적용대상이 아닙니다.", "SL");
	               	}	
	         	}        	
			}
			
    		/******************************************************************* 
			 *  수입 전후열좌우 HOTCOIL CHK 21.05.28
			 ******************************************************************/
			String getBaseCheck = jrRuleConst.getFieldString("BASE_CHEK");
			commUtils.printLog(logId,"   getBaseCheck:" + getBaseCheck , "SL");
			
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0140 적용여부:"+ commUtils.trim(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0140")), "SL");
			if ( !"W".equals(sYD_STKBED_USG_CD)) {   // 광폭야드 인경우 폭편차 제외 2022.08.24 chito
			if (("Y".equals( commUtils.trim(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0140"))) ) && "A".equals(sCrnAutoYn)) {
				if( ydSchCd.indexOf("CV01LH") > 0 ) {
					
					commUtils.printLog(logId, "     ==================== 수입 전후열좌우 HOTCOIL 체크 ====================", "SL"); 

					JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
	        		jrParam.setField("YD_STK_COL_GP"	, ydStkColGp);
	        		jrParam.setField("YD_STK_BED_NO"	, ydStkBedNo);
	        		jrParam.setField("YD_STK_LYR_NO"	, ydStkLyrNo);
	        		jrParam.setField("STL_NO"	        , sStlNo);
	        		jrParam.setField("YD_SCH_CD"	    , ydSchCd);
	        		jrParam.setField("CV_HOTCOIL_TM"	, Long.toString(lngRuleCvHotCoilTm));
	        		jrParam.setField("CV_HOTCOIL_DONG"	, Long.toString(lngRuleCvDongHotCoilTm));
	        		commUtils.printLog(logId, jrParam+" >> 수입 전후열좌우 HOTCOIL 체크", "SL"); 

	        		/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCVHotCoilTimeChk
	        		WITH TEMP_PARAM AS(
	        		    SELECT SUBSTR(YD_STK_COL_GP, 0, 1) || SUBSTR(YD_STK_COL_GP, 2, 1) AS YD_BAY_GP
	        		         , YD_STK_COL_GP
	        		         , YD_STK_BED_NO
	        		         , YD_STK_LYR_NO
	        		         , (SELECT DTL_ITEM2 FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP003' AND ITEM_VALUE1 = SUBSTR(YD_STK_COL_GP, 2, 1)) AS CV_HOTCOIL_TM
	        		         , YD_SCH_CD
	        		         , STL_NO
	        		      FROM (SELECT :V_YD_STK_COL_GP AS YD_STK_COL_GP
	        		                 , :V_YD_STK_BED_NO AS YD_STK_BED_NO
	        		                 , :V_YD_STK_LYR_NO AS YD_STK_LYR_NO
	        		                 , :V_CV_HOTCOIL_TM AS CV_HOTCOIL_TM
	        		                 , :V_YD_SCH_CD     AS YD_SCH_CD
	        		                 , :V_STL_NO        AS STL_NO
	        		              FROM DUAL
	        		           ) A
	        		)
	        		, TBL_RANK AS (
	        		    SELECT A.*
	        		      FROM (SELECT RANK() OVER(ORDER BY AA.YD_STK_COL_GP) AS NUM
	        		                 , AA.YD_STK_COL_GP
	        		              FROM TB_YD_STKCOL AA
	        		                 , TEMP_PARAM   P
	        		             WHERE AA.YD_STK_COL_GP LIKE P.YD_BAY_GP ||'%'
	        		               AND SUBSTR(AA.YD_STK_COL_GP,3,2) BETWEEN '01' AND '99'
	        		           ) A          
	        		         , (SELECT DISTINCT AA.YD_SCH_CD
	        		                 , BB.YD_STK_COL_GP
	        		              FROM TB_YD_LOCSRCHRNG AA
	        		                 , TB_YD_LOCSRCHBED BB
	        		                 , TEMP_PARAM       P
	        		             WHERE AA.YD_LOC_SRCH_RNG_REG_SNO = BB.YD_LOC_SRCH_RNG_REG_SNO
	        		               AND SUBSTR(AA.YD_SCH_CD, 0, 2) = P.YD_BAY_GP
	        		               AND SUBSTR(AA.YD_SCH_CD, 3, 6) = 'CV01LH'
	        		               AND AA.DEL_YN    = 'N'
	        		               AND BB.DEL_YN    = 'N'
	        		           ) B
	        		     WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
	        		)
	        		, TBL_FNDCOL AS (
	        		SELECT *
	        		  FROM (
	        		        SELECT ABS(A.NUM-B.NUM) AS SEQ
	        		             , A.*
	        		             , P_COL
	        		             , SUBSTR(A.YD_STK_COL_GP, 5, 2) AS A_COL
	        		          FROM TBL_RANK A
	        		             , (SELECT C.NUM
	        		                     , P.YD_STK_COL_GP
	        		                     , SUBSTR(P.YD_STK_COL_GP, 5, 2) AS P_COL
	        		                  FROM TEMP_PARAM   P
	        		                     , TBL_RANK     C
	        		                 WHERE P.YD_STK_COL_GP = C.YD_STK_COL_GP
	        		               ) B
	        		         WHERE 1 = 1
	        		           
	        		        --   AND A.NUM IN (B.NUM, B.NUM-1, B.NUM+1)
	        		           
	        		           AND 'Y' = CASE WHEN B.P_COL IN ('01') THEN CASE WHEN B.YD_STK_COL_GP IN ('JD3501','JE3501','JG3501','JH3501') AND A.NUM IN (B.NUM, B.NUM-1, B.NUM+1) THEN 'Y'
	        		                                                           WHEN B.P_COL = '01' AND A.NUM IN (B.NUM, B.NUM+1) THEN 'Y'
	        		                                                      ELSE 'N' END
	        		                          WHEN A.NUM IN (B.NUM, B.NUM-1, B.NUM+1) THEN 'Y' 
	        		                          ELSE 'N' END
	        		       )
	        		 WHERE 1 = 1       
	        		   AND 'Y' = CASE WHEN A_COL = '01' AND TO_NUMBER(A_COL) < TO_NUMBER(P_COL) THEN 'N'
	        		                  ELSE 'Y' END
	        		 ORDER BY NUM
	        		) -- 각동 트레킹 정보중 10베드에 코일이 있는지 판단 
	        		, TBL_TRACKING_INFO AS (SELECT AA.STL_NO ,AA.YD_BAY_GP    
	        		  FROM (SELECT A.YD_GP || A.YD_BAY_GP || 'CV01' AS YD_STK_COL_GP
	        		             , SUBSTR(A.EQP_GP, 5, 2) AS YD_STK_BED_NO
	        		             , A.PROC_GP
	        		             , A.EQP_GP
	        		             , A.STL_NO
	        		             , A.YD_EQP_NM  
	        		             , A.YD_BAY_GP
	        		             , COUNT(A.STL_NO) OVER() AS TOTALCOUNT
	        		          FROM TB_YD_EQPTRACKING     A
	        		              ,TEMP_PARAM   TP
	        		         WHERE A.YD_GP = 'J'
	        		           AND (A.EQP_GP LIKE 'CV%' OR A.EQP_GP LIKE 'WBF%') -- 컨베이어, WAKING BEAM    
	        		           AND A.YD_BAY_GP = SUBSTR(TP.YD_STK_COL_GP, 2, 1) 
	        		           AND SUBSTR(A.EQP_GP, 5, 2) IN ('10')-- 마지막 베드에 코일이 있는지 확인 후 스킵결정
	        		         ORDER BY A.YD_BAY_GP,A.SORT_SEQ
	        		       ) AA
	        		)
	        		----------------------------------------------------------------------------------------------------검색조건 생성 쿼리 
	        		,
	        		DATA_TBL AS -- 출하 검색조건(행선) 얻기위한 데이터
	        		(
	        		SELECT A.ORD_YEOJAE_GP
	        		     -- 인도조건
	        		     , CASE WHEN A.ORD_YEOJAE_GP = '2' THEN ''
	        		            ELSE SUBSTR(C.DELIVER_TERM_CD,1,1) END AS DELIVER_TERM_CD 
	        		     --포장방법       
	        		     , DECODE(A.CURR_PROG_CD,'F','XX',C.WRAP_METHOD_CD) AS WRAP_METHOD_CD  
	        		     -- 용도       
	        		     , (CASE WHEN A.ORD_NO LIKE 'G%' AND A.CURR_PROG_CD='H' THEN 'F'
	        		             WHEN A.ITEMNAME_CD IN ('HAP','HBP','HCP','HAS','HBS','HCS','HAT','HBT','HCT','HCJ') THEN 'F' ELSE 'Y' END) AS USAGE_CD     
	        		     , A.COIL_NO
	        		  FROM USRPTA.TB_PT_COILCOMM A
	        		     , TB_YD_STOCK    B 
	        		     , USRPTA.TB_PT_OSCOMM   C 
	        		     , TEMP_PARAM CC
	        		 WHERE A.COIL_NO = B.STL_NO
	        		   AND A.COIL_NO = CC.STL_NO
	        		   AND A.ORD_NO  = C.ORD_NO(+)
	        		   AND A.ORD_DTL = C.ORD_DTL(+)  
	        		)
	        		,
	        		ROUTE_GP_TBL AS -- ROUTE_GP 검색조건 조회
	        		(
	        		SELECT     --소재장
	        		       CASE WHEN SUBSTR(TP.YD_SCH_CD,8,1) = 'H' AND APP_YN = 'N' THEN
	        		                 ( 
	        		                   SELECT CASE --지포장 보급
	        		                               WHEN TP.YD_SCH_CD IN ('JBGF01UH','JCGF01UH','JEGF01UH','JHGF01UH')                   THEN 'G0'
	        		                               --C동반입 HFL재(B3), SPM재(B4)
	        		                               WHEN TP.YD_SCH_CD IN ('JCPT01LH','JCPT02LH') THEN DECODE(CC.NEXT_PROC,'CH','B3','B4') 
	        		                               ELSE (SELECT YD_AIM_RT_GP FROM TB_YD_STOCK WHERE STL_NO = CC.COIL_NO) 

	        		                          END
	        		                     FROM TB_PT_COILCOMM CC
	        		                        , TEMP_PARAM TM
	        		                    WHERE CC.COIL_NO = TM.STL_NO 
	        		                 ) 
	        		             -- 소재 신 검색조건     
	        		            WHEN SUBSTR(TP.YD_SCH_CD,8,1) = 'H' AND APP_YN = 'Y' THEN
	        		                 ( 
	        		                   SELECT CASE WHEN TP.YD_SCH_CD IN ('JBGF01UH','JCGF01UH','JEGF01UH','JHGF01UH')  THEN 'G0' --지포장 보급
	        		                               --C동반입 HFL재(B3), SPM재(B4)
	        		                               WHEN TP.YD_SCH_CD IN ('JCPT01LH','JCPT02LH') THEN DECODE(CC.NEXT_PROC,'CH','B3','B4') 
	        		                               -- 특수강 (590Y)
	        		                               WHEN TP.YD_SCH_CD IN ('JBCV01LH','JCCV01LH') 
	        		                                   AND CC.HR_SPEC_ABBSYM LIKE '%590Y' THEN  SUBSTR(TP.YD_SCH_CD,2,1) ||'Y'
	        		                               WHEN TP.YD_SCH_CD LIKE 'J_TR13LH' THEN SUBSTR(TP.YD_SCH_CD,2,1)||SUBSTR(CC.NEXT_PROC,2,1)   
	        		                               -- RULE 에 포함 안된것은 '_Z'처리
	        		                               WHEN SUBSTR(CC.NEXT_PROC,1,1) = SUBSTR(TP.YD_SCH_CD,2,1) 
	        		                                     AND SUBSTR(CC.NEXT_PROC,2,1) IN ('H','K','R') THEN 
	        		                                              CASE WHEN CC.NEXT_PROC IN ( SELECT NVL(DTL_ITEM2,'*') FROM TB_YD_RULE
	        		                                                                           WHERE REPR_CD_GP  = 'APP007'
	        		                                                                             AND DEL_YN ='N'
	        		                                                                            ) THEN  CC.NEXT_PROC 
	        		                                                   ELSE SUBSTR(TP.YD_SCH_CD,2,1) ||'Z' 
	        		                                                   END                             
	        		                               -- 공냉재
	        		                               WHEN SUBSTR(CC.NEXT_PROC,2,1) = 'A'
	        		                                 OR EXISTS (SELECT 1
	        		                                              FROM TB_HR_C_SHEARWOWR SR
	        		                                             WHERE SR.COIL_NO      = CC.COIL_NO
	        		                                               AND SR.HR_PLNT_GP   = 'C'
	        		                                               AND SR.WORK_STAT    = '*'
	        		                                               AND SR.WORD_PROC LIKE '%A'
	        		                                               AND SR.RECEIPT_HOLD_SCRAP_CAUSE_GP IN ('I', 'B')
	        		                                               AND SR.STEP_NO = (SELECT MAX(STEP_NO) FROM TB_HR_C_SHEARWOWR WHERE COIL_NO = SR.COIL_NO)
	        		                                           ) THEN SUBSTR(TP.YD_SCH_CD,2,1) ||'A'
	        		                                          
	        		                              ELSE SUBSTR(TP.YD_SCH_CD,2,1) ||'Z'
	        		                               END  NEXT_PROC
	        		                      FROM TB_PT_COILCOMM CC
	        		                           , TEMP_PARAM TM
	        		                     WHERE CC.COIL_NO = TM.STL_NO 
	        		                 )      
	        		           --제품장           
	        		            ELSE 
	        		                ( 
	        		                   SELECT CASE 
	        		                               WHEN ST.YD_AIM_RT_GP  = 'F3'  THEN 'F0'
	        		                               WHEN TP.YD_SCH_CD IN ('JBGF01LM','JCGF01LM','JEGF01LM','JHGF01LM') THEN 'G0'
	        		                               WHEN TP.YD_SCH_CD IN ( 'JAKD01LM'                                 ,'JATC05MM'
	        		                                                     ,'JBKD01LM'           ,'JBTC01MM','JBTC02MM','JBTC05MM'
	        		                                                     ,'JCKD01LM','JCFD01LM','JCTC01MM','JCTC02MM','JCTC05MM'
	        		                                                     ,'JDFD01LM','JDTC01MM','JDTC02MM'
	        		                                                     ,'JEKD01LM','JETC01MM','JETC02MM'
	        		                                                     ,'JFFD01LM','JFTC01MM','JFTC02MM'
	        		                                                     ,'JGFD01LM','JGTC01MM','JGTC02MM'
	        		                                                     ,'JHKD01LM'           ,'JHTC01MM','JHTC02MM') 
	        		                                                 AND BT.WRAP_METHOD_CD = 'EB' AND BT.USAGE_CD NOT IN ('F') THEN 'G0'
	        		                               ELSE 
	        		                                    (SELECT A.CD_GP AS CODE
	        		                                       FROM VW_YD_YDB700 A
	        		                                      WHERE A.ORD_YEOJAE_GP LIKE NVL(BT.ORD_YEOJAE_GP,'') ||'%' 
	        		                                        AND A.ITM_NM        LIKE NVL(BT.DELIVER_TERM_CD ,'') ||'%' 
	        		                                        AND ((BT.ORD_YEOJAE_GP IS NOT NULL AND (A.CD_GP <> 'F0') AND (A.CD_GP <> 'G0'))
	        		                                             OR ((BT.ORD_YEOJAE_GP IS NULL) )
	        		                                            )
	        		                                        AND ROWNUM = 1    
	        		                                    )        
	        		                          END
	        		                     FROM TB_YD_STOCK  ST  
	        		                        , DATA_TBL     BT
	        		                        , TEMP_PARAM TM
	        		                    WHERE  ST.STL_NO = BT.COIL_NO
	        		                       AND ST.STL_NO = TM.STL_NO   
	        		                       AND ROWNUM = 1
	        		                 ) 
	        		            
	        		       END 
	        		       AS YD_ROUTE_GP
	        		  FROM 
	        		       ( SELECT YD_SCH_CD 
	        		              , NVL((SELECT ITEM1 
	        		                       FROM TB_YD_RULE 
	        		                      WHERE REPR_CD_GP = 'APP005'
	        		                        AND CD_GP = 'J' AND ITEM = '*' AND DEL_YN = 'N'),'N') AS APP_YN 
	        		           FROM TEMP_PARAM)  TP
	        		)
	        		,
	        		TEMP_TO_LOC AS ( -- 영역별검색순서  

	        		SELECT BB.YD_STK_COL_GP
	        		           FROM (
	        		             SELECT  B.YD_STK_COL_GP ,B.YD_STK_BED_SRCH_SEQ                
	        		               FROM TB_YD_LOCSRCHRNG A
	        		                  , TB_YD_LOCSRCHBED B
	        		                  , TEMP_PARAM CC
	        		              WHERE A.YD_LOC_SRCH_RNG_REG_SNO = B.YD_LOC_SRCH_RNG_REG_SNO
	        		                AND A.YD_SCH_CD     = CC.YD_SCH_CD 
	        		                AND A.YD_ROUTE_GP  IN  (SELECT YD_ROUTE_GP FROM ROUTE_GP_TBL) 
	        		                AND B.YD_STK_COL_GP LIKE 'J%'
	        		                AND A.DEL_YN = 'N'
	        		                AND B.DEL_YN = 'N'            
	        		              ORDER BY B.YD_STK_BED_SRCH_SEQ
	        		              ) BB
	        		            WHERE 1=1             
	        		              GROUP BY BB.YD_STK_COL_GP
	        		)
	        		,
	        		RETENTION_RATE AS (
	        		SELECT ROUND(STL_CNT / CNT * 100, 2) AS RATE
	        		  FROM(
	        		        SELECT COUNT(*) AS CNT
	        		             , SUM(CASE WHEN SL.STL_NO IS NOT NULL THEN 1 ELSE 0 END) AS STL_CNT 
	        		          FROM TB_YD_STKLYR SL
	        		             , TB_YD_STKCOL SC
	        		             , TEMP_PARAM CC
	        		         WHERE SC.YD_STK_COL_GP = SL.YD_STK_COL_GP 
	        		           AND SC.YD_GP = 'J'
	        		           AND SC.YD_STK_COL_W_GP <> 'L' -- 광폭열 제외
	        		           AND SC.YD_BAY_GP LIKE   SUBSTR( CC.YD_SCH_CD  ,2,1) ||'%'
	        		           AND SC.YD_STK_COL_GP IN (SELECT YD_STK_COL_GP FROM TEMP_TO_LOC)
	        		           AND SL.YD_STK_LYR_ACT_STAT = 'E'
	        		           
	        		       )
	        		)
	        		-----------------------------------------------------------------------------------------------------------------검색조건 생성 END
	        		SELECT AA.* FROM
	        		  (
	        		   SELECT C.HRMILL_CMPL_DT
	        		         , TRUNC( (SYSDATE - C.HRMILL_CMPL_DT)*24 ) AS TM
	        		         , B.SEQ
	        		         , B.NUM
	        		         , A.YD_STK_COL_GP
	        		         , A.YD_STK_BED_NO
	        		         , A.YD_STK_LYR_NO
	        		         , A.STL_NO
	        		         , A.YD_STK_LYR_ACT_STAT
	        		         , A.YD_STK_LYR_MTL_STAT
	        		         , (SELECT STL_NO FROM TBL_TRACKING_INFO) AS TRACKING_INFO
	        		         , (SELECT RATE FROM RETENTION_RATE) AS RETENTION_RATE -- 수입존 적치율
	        		         , (SELECT TO_NUMBER(DTL_ITEM8)
	        		               FROM TB_YD_RULE 
	        		              WHERE REPR_CD_GP = 'APP003'
	        		                AND CD_GP = 'J'  AND DEL_YN = 'N' AND ITEM_VALUE1 = SUBSTR( P.YD_SCH_CD  ,2,1)
	        		           ) AS IMRATE -- 수입존 적치율 기준값
	        		      FROM TB_YD_STKLYR A
	        		         , TBL_FNDCOL   B
	        		         , TEMP_PARAM   P
	        		         , USRPTA.TB_PT_COILCOMM C
	        		     WHERE A.YD_STK_COL_GP       = B.YD_STK_COL_GP
	        		       AND A.STL_NO              = C.COIL_NO
	        		       AND A.YD_STK_LYR_MTL_STAT = 'C'
	        		       -- 전/후 열 같은번지 같은 단만 체크. 21.12.03 이강일주임
	        		    --   AND 'Y' = CASE WHEN A.YD_STK_BED_NO            = P.YD_STK_BED_NO
	        		    --                   AND A.YD_STK_LYR_NO            = P.YD_STK_LYR_NO              THEN 'Y'
	        		    --                  WHEN P.YD_STK_LYR_NO            = '002'
	        		    --                   AND A.YD_STK_COL_GP            = P.YD_STK_COL_GP
	        		    --                   AND TO_NUMBER(A.YD_STK_BED_NO) IN (TO_NUMBER(P.YD_STK_BED_NO), TO_NUMBER(P.YD_STK_BED_NO)+1)
	        		    --                   AND A.YD_STK_LYR_NO            = '001'
	        		    --                       THEN 'Y'
	        		    --                  ELSE 'N'
	        		    --             END
	        		       -- 전/후 좌우
	        		       AND 'Y' = CASE WHEN A.YD_STK_BED_NO            = P.YD_STK_BED_NO              THEN 'Y'
	        		                      WHEN TO_NUMBER(A.YD_STK_BED_NO) = TO_NUMBER(P.YD_STK_BED_NO)-1 THEN 'Y'
	        		                      WHEN TO_NUMBER(A.YD_STK_BED_NO) = TO_NUMBER(P.YD_STK_BED_NO)+1
	        		                       AND A.YD_STK_LYR_NO            = '001'                        THEN 'Y'
	        		                 END
	        		       AND TRUNC( (SYSDATE - C.HRMILL_CMPL_DT)*24 )  >= P.CV_HOTCOIL_TM
	        		       
	        		     ORDER BY  TRUNC( (SYSDATE - C.HRMILL_CMPL_DT)*24 )  ,  A.YD_STK_COL_GP, A.YD_STK_BED_NO, A.YD_STK_LYR_NO
	        		     )  AA
	        		 WHERE 1=1
	        		  AND ( AA.TRACKING_INFO IS  NULL         --코일이 있으면 HOT COIL 체크 스킵 
	        		     OR AA.RETENTION_RATE <= AA.IMRATE )  --적치율이 기준값보다 크면 HOT COIL 체크 스킵 
	        		*/
	        		JDTORecordSet jsCoilWidth = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCVHotCoilTimeChk", logId, mthdNm, "   수입 전후열좌우 HOTCOIL 기준시간초과 조회");
	        		
	        		/*
	    			 * B/C 동 590Y HOTCOIL 체크하지 않음  로직 추가 YYS 202212
	    			 */
	        		commUtils.printLog(logId, "스케줄코드 : "+ydSchCd+" > HOTCOIL 체크여부 : "+getBaseCheck, "SL");
      			    String sAPP002_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "002");

	        		
	    				String hrSpecAbbsym	= "";
	    				String nextProc	= "";
	    				String ydGong	= "Z";
	    				jrParam.setField("COIL_NO"	        , sStlNo);//590Y 인코일 체크하여 핫코일체크 여부 결정 --590Y 코일은 핫코일 체크 안함 보급존으로 가기때문임 YYS 202212
	    				JDTORecordSet rsResultCoilComm = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getCOILCOMM", logId, mthdNm, "COILCOMM 존재 하는지 확인");
	    				
	    				if( rsResultCoilComm.size() > 0 ) {
	    					hrSpecAbbsym	= commUtils.trim(rsResultCoilComm.getRecord(0).getFieldString("HR_SPEC_ABBSYM"));
	    					nextProc	    = commUtils.trim(rsResultCoilComm.getRecord(0).getFieldString("NEXT_PROC"));
	    					if("Y".equals(sAPP002_YN)){
		    					if(nextProc.length() > 1){
		    						ydGong = nextProc.substring(1, 2);
		    					}
	    					}	
	    					
	    				}
	    				commUtils.printLog(logId, "스케줄코드 : "+ydSchCd+" > 코일규격 : "+hrSpecAbbsym+" 공냉재 여부 : "+nextProc, "SL");
	    				if( (("JBCV01LH".equals(ydSchCd) || "JCCV01LH".equals(ydSchCd)) && hrSpecAbbsym.indexOf("590Y") > 0 ) 
	    						|| ("Y".equals(getBaseCheck))
	    						|| ("A".equals(ydGong)) ){ //공냉재 HOTCOIL체크 스킵 20230627 YYS 이강일 주임 요청 
	    					commUtils.printLog(logId, "스케줄코드 : "+ydSchCd+" > 코일규격 : "+hrSpecAbbsym, "SL");
	    					commUtils.printLog(logId, "스케줄코드 : "+ydSchCd+" > HOTCOIL 체크여부 : "+getBaseCheck+" 공냉재 여부 : "+ydGong, "SL");
	    				}else{
	    					
			        		if( jsCoilWidth.size() > 0 ) {
			        			String tmpStlNo			= commUtils.trim(jsCoilWidth.getRecord(0).getFieldString("STL_NO"));
			        			String tmpTm			= commUtils.trim(jsCoilWidth.getRecord(0).getFieldString("TM"));
			        			String tmpYdStkColGp	= commUtils.trim(jsCoilWidth.getRecord(0).getFieldString("YD_STK_COL_GP"));
			        			String tmpYdStkBedNo	= commUtils.trim(jsCoilWidth.getRecord(0).getFieldString("YD_STK_BED_NO"));
			        			String tmpYdStkLyrNo	= commUtils.trim(jsCoilWidth.getRecord(0).getFieldString("YD_STK_LYR_NO"));
			        			rtnMsg = "     ▶ 적치단("+ ydStkColGp + ydStkBedNo + ydStkLyrNo +") 전후열좌우 HOTCOIL[기준치:"+Long.toString(lngRuleCvDongHotCoilTm)+"] 시간 초과.. 저장위치["+ tmpYdStkColGp + tmpYdStkBedNo +"-"+ tmpYdStkLyrNo +"] 코일번호["+ tmpStlNo +"] 경과시간["+ tmpTm +"]";
			        			
			        			commUtils.printLog(logId, rtnMsg, "SL");
								jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
								jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);	
								jrRtn.setField("LOC_ABLE_CHK" 		, "Y");//권화위치 선택화면에서 보여줄지를 결정 Y:보여짐
								jrRtn.setField("LOC_CHK_MATH" 	    , "전후열좌우 HOTCOIL[기준치:"+Long.toString(lngRuleCvDongHotCoilTm)+"] 시간 초과["+ tmpTm +"]");
								
								return jrRtn;
			        		}
	    				}

				}
			  }
			}
			
			/*************************************************
			 * 수입 광폭재 체크 21.05.28
			 * 1단 만 체크.. 2단은 폭 편차에서 체크됨
			 *************************************************/
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0150 적용여부:"+ commUtils.trim(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0150")), "SL");
			if (("Y".equals( commUtils.trim(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0150"))) ) && "A".equals(sCrnAutoYn)) {
				if( "001".equals(ydStkLyrNo) && (ydSchCd.indexOf("CV01LH") > 0) ) {

					commUtils.printLog(logId, "     ==================== 수입 전/후 열 광폭재 체크 ====================", "SL");
					
	        		JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
	        		jrParam.setField("YD_STK_COL_GP", ydStkColGp);
	        		jrParam.setField("YD_STK_BED_NO", ydStkBedNo);
	        		
	        		/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCVFrontBackCoilWidth
	        		WITH TEMP_PARAM AS(
	        		    SELECT SUBSTR(YD_STK_COL_GP, 0, 2) AS YD_BAY_GP
	        		         , YD_STK_COL_GP
	        		         , YD_STK_BED_NO
	        		      FROM (SELECT :V_YD_STK_COL_GP AS YD_STK_COL_GP
	        		                 , :V_YD_STK_BED_NO AS YD_STK_BED_NO
	        		              FROM DUAL
	        		           ) A
	        		)
	        		, TBL_RANK AS (
	        		    SELECT A.*
	        		      FROM (SELECT RANK() OVER(ORDER BY AA.YD_STK_COL_GP) AS NUM
	        		                 , AA.YD_STK_COL_GP
	        		              FROM TB_YD_STKCOL AA
	        		                 , TEMP_PARAM   P
	        		             WHERE AA.YD_STK_COL_GP LIKE P.YD_BAY_GP ||'%'
	        		               AND SUBSTR(AA.YD_STK_COL_GP,3,2) BETWEEN '01' AND '99'
	        		           ) A
	        		         , (SELECT DISTINCT AA.YD_SCH_CD
	        		                 , BB.YD_STK_COL_GP
	        		              FROM TB_YD_LOCSRCHRNG AA
	        		                 , TB_YD_LOCSRCHBED BB
	        		                 , TEMP_PARAM       P
	        		             WHERE AA.YD_LOC_SRCH_RNG_REG_SNO = BB.YD_LOC_SRCH_RNG_REG_SNO
	        		               AND SUBSTR(AA.YD_SCH_CD, 0, 2) = P.YD_BAY_GP
	        		               AND SUBSTR(AA.YD_SCH_CD, 3, 6) = 'CV01LH'
	        		               AND AA.DEL_YN    = 'N'
	        		               AND BB.DEL_YN    = 'N'
	        		           ) B
	        		     WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
	        		)
	        		, TBL_FNDCOL AS (
	        		SELECT A.*
	        		  FROM TBL_RANK A
	        		     , (SELECT C.NUM
	        		          FROM TEMP_PARAM   P
	        		             , TBL_RANK     C
	        		         WHERE P.YD_STK_COL_GP = C.YD_STK_COL_GP
	        		       ) B
	        		 WHERE A.NUM IN (B.NUM-1, B.NUM+1)
	        		 ORDER BY A.NUM
	        		)
	        		SELECT C.COIL_W
	        		     , A.*
	        		  FROM TB_YD_STKLYR A
	        		     , TBL_FNDCOL   B
	        		     , TEMP_PARAM   P
	        		     , USRPTA.TB_PT_COILCOMM C
	        		 WHERE A.YD_STK_COL_GP       = B.YD_STK_COL_GP
	        		   AND A.YD_STK_BED_NO       = P.YD_STK_BED_NO
	        		   AND A.STL_NO              = C.COIL_NO
	        		   AND A.YD_STK_LYR_NO       = '001'
	        		   AND A.YD_STK_LYR_MTL_STAT = 'C'
	        		   AND C.COIL_W             >= 1530
	        		*/
	        		JDTORecordSet jsCoilWidth = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCVFrontBackCoilWidth", logId, mthdNm, "   수입 전후열좌우 광폭재 조회");
	        		
	        		if( jsCoilWidth.size() > 0 ) {
	        			String tmpStlNo			= commUtils.trim(jsCoilWidth.getRecord(0).getFieldString("STL_NO"));
	        			String tmpCoilW			= commUtils.trim(jsCoilWidth.getRecord(0).getFieldString("COIL_W"));
	        			String tmpYdStkColGp	= commUtils.trim(jsCoilWidth.getRecord(0).getFieldString("YD_STK_COL_GP"));
	        			String tmpYdStkBedNo	= commUtils.trim(jsCoilWidth.getRecord(0).getFieldString("YD_STK_BED_NO"));
	        			String tmpYdStkLyrNo	= commUtils.trim(jsCoilWidth.getRecord(0).getFieldString("YD_STK_LYR_NO"));
	        			rtnMsg = "     ▶ 수입 전후열좌우 광폭 존재.. 저장위치["+ tmpYdStkColGp + tmpYdStkBedNo +"-"+ tmpYdStkLyrNo +"] 코일번호["+ tmpStlNo +"] 코일폭["+ tmpCoilW +"]";
	        			
            			commUtils.printLog(logId, rtnMsg, "SL");
    					jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
    					jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);	
    					jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
    					return jrRtn;
	        		}
				}
			}
			
			/*************************************************
			 * 소재 SPFH590Y 강종 체크 : 2단 위치일때 1단 SPFH590Y 강종 체크
			 *************************************************/
			commUtils.printLog(logId,"   sColYdLocGp:" + sColYdLocGp +  "_YN_0160 적용여부:"+ commUtils.trim(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0160")), "SL");
			if ( ("Y".equals( commUtils.trim(jrLocRuleApply.getFieldString(sColYdLocGp+"_YN_0160")))) ) {
				if( "002".equals(ydStkLyrNo) && "H".equals(sColYdLocGp) ) {

					commUtils.printLog(logId, "     ==================== 소재 SPFH590Y 강종 체크 ====================", "SL");

					if( "".equals(sLeftHrSpecAbbsym) && "".equals(sRightHrSpecAbbsym) ) {

						sRtnVal = CConstant.RETN_CD_SUCCESS;
					} else {

            			rtnMsg = "     ▶ 2단일 경우 1단 SPFH590Y 강종 체크:좌측["+ sLeftStlNo +"] 가능여부["+ sLeftHrSpecAbbsym +"]-우측["+ sRightStlNo +"] 가능여부["+ sRightHrSpecAbbsym +"]";
            			commUtils.printLog(logId, rtnMsg, "SL");

    					jrRtn.setField("LOC_ABLE_RTN"		, "-1");
    					jrRtn.setField("LOC_ABLE_CONTENTS"	, rtnMsg);
    					jrRtn.setField("LOC_ABLE_CHK" 		, "N");//권화위치 선택화면에서 보여줄지를 결정 N:보여지지 않음
    					return jrRtn;
					}
				}
			}
			
        	commUtils.printLog(logId, mthdNm, "S-");

        	jrRtn.setField("LOC_ABLE_RTN" 		, "1");	
			return jrRtn;
		} catch(Exception e) {
			
			rtnMsg = "코일야드 To위치검색 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			
			jrRtn.setField("LOC_ABLE_RTN" 		, "-1");	
			jrRtn.setField("LOC_ABLE_CONTENTS" 	, rtnMsg);	
			return jrRtn;
		}
	} 	

	/**
	 *      [A] 오퍼레이션명 : 스케줄 To위치 로그 Log
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void insSchLog(JDTORecord jrParam) throws DAOException {
		String mthdNm = "스케줄 To위치 로그 Log[CCoilSchSeEJB.insSchLog] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.insSchLog 
			INSERT INTO TB_YD_SCHLOG
			     ( TB_SCHLOG_ID
			     , STL_NO
			     , YD_CRN_SCH_ID
			     , YD_GP
			     , YD_SCH_CD
			     , REGISTER
			     , REG_DDTT
			     , MODIFIER
			     , MOD_DDTT
			     , DEL_YN
			     , SCH_CONTENTS
			     )
			VALUES
			     ( YD_SCHLOG_ID_SEQ.NEXTVAL
			     , :V_STL_NO
			     , :V_YD_CRN_SCH_ID
			     , :V_YD_GP
			     , :V_YD_SCH_CD
			     , 'log'
			     , SYSDATE
			     , 'log'
			     , SYSDATE
			     , 'N'	 
			     , :V_SCH_CONTENTS
			     )
			*/     
			 
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.insSchLog", logId, mthdNm, "스케줄 To위치 로그");
			
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 스케줄 To위치 XX Log
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void insSchXXLog(JDTORecord jrParam) throws DAOException {
		String mthdNm = "스케줄 To위치 XX 로그 Log[CCoilSchSeEJB.insSchXXLog] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			//파라미터 Null Check
        	String ydWbookId = commUtils.trim(jrParam.getFieldString("YD_WBOOK_ID" ));	
        	String ydEqpId 	 = commUtils.trim(jrParam.getFieldString("YD_EQP_ID"   ));	
        	String sModifier = commUtils.trim(jrParam.getFieldString("MODIFIER"    ));	
        	String ydSchCd 	 = commUtils.trim(jrParam.getFieldString("YD_SCH_CD"   ));
			String sStlNo 	 = commUtils.trim(jrParam.getFieldString("STL_NO"      ));   // 대표 재료만 스크랩여부 판단
			String ydCrnSchId 	  = commUtils.trim(jrParam.getFieldString("YD_CRN_SCH_ID"      ));
			String ydLocAbleCheck = commUtils.trim(jrParam.getFieldString("YD_LOC_ABLECHECK"      ));			
			String ydLocNote = commUtils.trim(jrParam.getFieldString("YD_CRNSCHLOC_NOTE"      ));			
			
			commUtils.printLog(logId, jrParam+" : jrParam_XX", "S+");
			commUtils.printLog(logId, ydSchCd+" : XX010101", "S+");
		    /*
		     * 
				INSERT INTO TB_YD_CRNSCHLOCHIST
				    (
				          YD_CRNLOC_SEQ       
				        , YD_CRN_SCH_ID       
				        , STL_NO              
				        , YD_GP               
				        , REGISTER            
				        , REG_DDTT            
				        , MODIFIER            
				        , MOD_DDTT           
				        , YD_BAY_GP          
				        , YD_EQP_ID          
				        , YD_DN_WO_LOC       
				        , YD_WBOOK_ID        
				        , YD_SCH_CD          
				        , YD_LOC_ABLECHECK    
				        , YD_CRNSCHLOC_NOTE   
				        , YD_DONGIMP_RATE    
				        , YD_DONG_RATE     
				    ) VALUES ( 
				        TO_CHAR(SYSDATE, 'YYYYMMDDHH24MI')||LPAD(YD_CARSCHLOG_SEQ.nextval,6,'0')      
				        , :V_YD_CRN_SCH_ID       
				        , :V_STL_NO              
				        , :V_YD_GP               
				        , :V_MODIFIER            
				        , SYSDATE            
				        , :V_MODIFIER            
				        , SYSDATE           
				        , :V_YD_BAY_GP          
				        , :V_YD_EQP_ID          
				        , :V_YD_DN_WO_LOC       
				        , :V_YD_WBOOK_ID        
				        , :V_YD_SCH_CD          
				        , :V_YD_LOC_ABLECHECK    
				        , :V_YD_CRNSCHLOC_NOTE   
				        , :V_YD_DONGIMP_RATE    
				        , :V_YD_DONG_RATE
				    )
             */
			JDTORecord jrLog  				= commUtils.getParam(logId, mthdNm, ""); 
			String ydLocGp = "J";
			if ("H".equals(ydSchCd.substring(7, 8))) {
				ydLocGp = "H";
			}
			
			JDTORecordSet jsRetentionRate  = JDTORecordFactory.getInstance().createRecordSet("Temp");
    		
    		String        ydRetentionRate   = "";
    		String        ydDongRate   = "";
    		commUtils.printLog(logId, ydSchCd.substring(1, 2)+" : XX010101", "S+");
    		commUtils.printLog(logId, sStlNo+" : XX010101", "S+");
				jrLog.setField("YD_CRN_SCH_ID"	 , ydCrnSchId);
				jrLog.setField("STL_NO"		     , sStlNo);           
				jrLog.setField("YD_LOC_GP"		 , ydLocGp); 
										
				jrLog.setField("YD_GP"		     , "J"); 
				jrLog.setField("MODIFIER"		 , sModifier);         
				jrLog.setField("YD_BAY_GP"		 , ydSchCd.substring(1, 2));        
				jrLog.setField("YD_EQP_ID"		 , ydEqpId);        
				jrLog.setField("YD_DN_WO_LOC"	 , "XX010101");     
				jrLog.setField("YD_WBOOK_ID"	 , ydWbookId);      
				jrLog.setField("YD_SCH_CD"		 , ydSchCd);        
				jrLog.setField("YD_LOC_ABLECHECK"		, ydLocAbleCheck); 
				jrLog.setField("YD_CRNSCHLOC_NOTE"		, ydLocNote);
					
				String ydRouteGp    = ydSchCd.substring(1, 2)+"Z";       //검색조건 행선			
			
				jsRetentionRate = commDao.select(jrLog, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getRetentionRate", logId, mthdNm, "적치율 조회");   			
				if (jsRetentionRate.size() > 0) {					        	    		
					ydDongRate 	    = commUtils.trim(jsRetentionRate.getRecord(0).getFieldString("STK_RT"   ));//동 적치율
					ydRetentionRate = commUtils.trim(jsRetentionRate.getRecord(0).getFieldString("RETENTION_RATE"   ));// 영역별 적치율
					ydRouteGp 	    = commUtils.trim(jsRetentionRate.getRecord(0).getFieldString("ROUTE_GP"   ));
				}else{
					String sMsg = "오류:적치율 조회 시 오류 YD_CRN_SCH_ID: " + ydCrnSchId;
    				commUtils.printLog(logId, sMsg, "S+");
				}
				commUtils.printLog(logId, "ydRouteGp >> "+ydRouteGp, "S+");
				jrLog.setField("YD_ROUTE_GP"	        , ydRouteGp);
				jrLog.setField("YD_LOC_ABLECHECK"		, ydRouteGp+":"+ydLocAbleCheck); 
				jrLog.setField("YD_DONGIMP_RATE"		, ydRetentionRate); 
				jrLog.setField("YD_DONG_RATE"		    , ydDongRate);     
				
				String sAPP014_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "014");   
				
				if("Y".equals(sAPP014_YN)){	
			        commDao.update(jrLog, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.inCrnschLocXXHist", logId, mthdNm, "xx 로그기록");
				}
			
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * TO위치 RULE //TODO
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord toLocRuleConst(String logId, String mthdNms) throws JDTOException {
    	String mthdNm = "TO위치 RULE상수 [CCoilSchSeEJB.toLocRuleConst] < " + mthdNms;
    	
    	JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
    	
    	try {
			
			commUtils.printLog(logId, mthdNm, "S+");
			
			JDTORecordSet jsAppRst = JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecord    jrAppRst = JDTORecordFactory.getInstance().create(); 
			
			/**********************************************************
			* 1. TB_YD_RULE 조회
			**********************************************************/
			if(coilDao.getYdRule(logId, mthdNm, "APP003","J","", jsAppRst)) {
				
				/**
				 * TO위치 적용여부    >>송 
				 */
				for(int i=1; i<=jsAppRst.size(); i++) {
					jsAppRst.absolute(i);
					jrAppRst  = jsAppRst.getRecord();
					
    				if( "".equals(commUtils.trim(jrAppRst.getFieldString("DTL_ITEM1"))) ) continue;
    				
					if ("GUN_ODIA".equals(commUtils.trim(jrAppRst.getFieldString("DTL_ITEM1")))) {
						
						jrRtn.setField("A_ODIA_2"	, commUtils.nvl(jrAppRst.getFieldString("DTL_ITEM2"), "550")); 
						jrRtn.setField("B_ODIA_2"	, commUtils.nvl(jrAppRst.getFieldString("DTL_ITEM4"), "820")); 
						jrRtn.setField("C_ODIA_2"	, commUtils.nvl(jrAppRst.getFieldString("DTL_ITEM6"), "850"));
						
		        	} else if ("GUN_ODIA_ABC".equals(commUtils.trim(jrAppRst.getFieldString("DTL_ITEM1")))) {
		        		
						jrRtn.setField("A_ODIA_ABC"	, commUtils.nvl(jrAppRst.getFieldString("DTL_ITEM2"), "550")); 
						jrRtn.setField("B_ODIA_ABC"	, commUtils.nvl(jrAppRst.getFieldString("DTL_ITEM4"), "820")); 
						jrRtn.setField("C_ODIA_ABC"	, commUtils.nvl(jrAppRst.getFieldString("DTL_ITEM6"), "850"));
						
		        	} else {
		        		
		        		jrRtn.setField(commUtils.trim(jrAppRst.getFieldString("DTL_ITEM1")), commUtils.trim(jrAppRst.getFieldString("DTL_ITEM2")));
		        	}
				}
			}	
			commUtils.printLog(logId, mthdNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
	    }
		
		return jrRtn;
	}      
    
	/**
	 * TO위치 RULE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord toLocRuleApply(String logId, String mthdNms) throws JDTOException {
    	String mthdNm = "TO위치 기준 적용여부 [CCoilSchSeEJB.toLocRuleApply] < " + mthdNms;
    	
    	JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
    	
    	try {
			
			commUtils.printLog(logId, mthdNm, "S+");
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("REPR_CD_GP" , "APP004");
			
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getToLocLogicChk 
			SELECT REPR_CD_GP
			     , ITEM ||'_YN_'|| CD_GP AS CHK_FLAG
			     , ITEM
			     , REPR_CD_CONTENTS
			     , ITEM1              AS APP004_YN 
			  FROM TB_YD_RULE
			 WHERE REPR_CD_GP = :V_REPR_CD_GP
			   AND ITEM NOT IN ('*')
			*/ 
			JDTORecordSet jsAppRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getToLocLogicChk", logId, mthdNm, "TO위치체크로직");
			
			JDTORecord jrAppRst = JDTORecordFactory.getInstance().create(); 
			for (int i = 1; i <= jsAppRst.size(); i++) {
	 
				jsAppRst.absolute(i);
				jrAppRst  = jsAppRst.getRecord();
				
				jrRtn.setField(jrAppRst.getFieldString("CHK_FLAG"), jrAppRst.getFieldString("APP004_YN"));
			}
			commUtils.printLog(logId, mthdNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
	    }//end of try~catch				
		return jrRtn;
	}      
    

	/**
	 * [A] 오퍼레이션명 : 1단 좌우외경편차Check(searchCoilGdsYdOutDiaDiffCheck)-제품
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String chk1ODiaDiff(String logId, String mthdNms, String sStlNo,      String ydStkLyrNo, String sOutDia
			                                               , String sLeftStlNo,  String sLeftOutDia 
			                                               , String sRightStlNo, String sRightOutDia
			                                               , long   lngRuleODia1) throws JDTOException  {
		String mthdNm = "1단좌우외경편차Check[CCoilSchSeEJB.chk1ODiaDiff] < " + mthdNms;
		String rtnMsg = "";
		String sRtnVal = CConstant.RETN_CD_FAILURE;
		
		try {
			
			commUtils.printLog(logId, "   "+ mthdNm, "S+");
	
			long lngOutDia 		= Long.parseLong(commUtils.nvl(sOutDia,"0"));
			long lngLeftOutDia 	= Long.parseLong(commUtils.nvl(sLeftOutDia,"0"));
			long lngRightOutDia	= Long.parseLong(commUtils.nvl(sRightOutDia,"0"));
			
			//좌우재료번호가 이적대상과 동일 한 경우 
			if(sRightStlNo.equals(sStlNo)){
				lngRightOutDia = 0;
				sRightStlNo    = "";
			} else if(sLeftStlNo.equals(sStlNo)){
				lngLeftOutDia  = 0;
				sLeftStlNo     = "";
			}
	
			commUtils.printLog(logId, "   대상재료번호(" + sStlNo         + ")=>>대상코일외경:"+lngOutDia, "SL");
			commUtils.printLog(logId, "   1단좌측재료번호(" + sLeftStlNo  +  ")=>>좌측코일외경:"+lngLeftOutDia, "SL");
			commUtils.printLog(logId, "   1단우측재료번호(" + sRightStlNo + ")=>>우측코일외경:"+lngRightOutDia, "SL");
			commUtils.printLog(logId, "   기준값 : " + lngRuleODia1, "SL");
			
			if ("001".equals(ydStkLyrNo)){
				// 좌우가 비워있는경우
				if ("".equals(sLeftStlNo) && "".equals(sRightStlNo)) {  // 좌우가 비워있는경우
	        		sRtnVal = CConstant.RETN_CD_SUCCESS;
	        		
				}else {
	
					commUtils.printLog(logId, "   "+sStlNo+"Math.abs(lngOutDia - lngRightOutDia)" + Math.abs(lngOutDia - lngRightOutDia), "SL");
					commUtils.printLog(logId, "   "+sStlNo+"Math.abs(lngOutDia - lngLeftOutDia))" + Math.abs(lngOutDia - lngLeftOutDia), "SL");
					
					//좌우코일이 존재 하는 경우 
					if(!"".equals(sLeftStlNo) && (!"".equals(sRightStlNo))) {
						if (Math.abs(lngOutDia - lngRightOutDia) <= lngRuleODia1 && Math.abs(lngOutDia - lngLeftOutDia) <= lngRuleODia1) {
			        		sRtnVal = CConstant.RETN_CD_SUCCESS;
			        	} else {
			        		sRtnVal = CConstant.RETN_CD_FAILURE;
			        	}
					}else {
						if (Math.abs(lngOutDia - lngRightOutDia) <= lngRuleODia1 || Math.abs(lngOutDia - lngLeftOutDia) <= lngRuleODia1) {
			        		sRtnVal = CConstant.RETN_CD_SUCCESS;
			        	} else {
			        		sRtnVal = CConstant.RETN_CD_FAILURE;
			        	}
					}
				}
			} 
			commUtils.printLog(logId, "   "+ mthdNm, "S-");
			return sRtnVal;
		} catch(Exception e) {
			rtnMsg = "확인:"+sStlNo+">>1단 좌우외경편차Check 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			return sRtnVal = CConstant.RETN_CD_FAILURE;
		}
		
	}


	/**
	 * [A] 오퍼레이션명 : 2단인 경우 군별 외경간격Check(searchCoilGdsYdOutDiaIntervalCheck)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String chk2ODiaInterval(String logId, String mthdNms, String ydStkColGp		, String ydStkBedNo , String ydStkLyrNo
			                                                   , String sStlNo			, String sOutDia    , String sColOutdiaGp
			                                                   , String sLeftStlNo		, String sLeftOutDia
			                                                   , String sRightStlNo		, String sRightOutDia
			                                                   , String sColYdStkSkidGp
			                                                   , long   lngRuleADiaDiff	
			                                                   , long   lngRuleBDiaDiff	
			                                                   , long   lngRuleCDiaDiff ) throws JDTOException  {
		String mthdNm = "2단인 경우 군별 외경간격Check[CCoilSchSeEJB.chk2ODiaInterval] < " + mthdNms;
		String rtnMsg = "";
		String sRtnVal = CConstant.RETN_CD_FAILURE;
 
		try {
			
			commUtils.printLog(logId, "   "+ mthdNm, "S+");

			long   lngDiaDiffConst 	= 0;           //외경간격상수
			long   lngBedLength    	= 0;           //군별소요길이
			long   lngLength 		= 0;         

			
        	//권하대상코일 외경 ;
			long lngOutDia 		= Long.parseLong(commUtils.nvl(sOutDia     ,"0"));
			long lngLeftOutDia 	= Long.parseLong(commUtils.nvl(sLeftOutDia ,"0"));
			long lngRightOutDia	= Long.parseLong(commUtils.nvl(sRightOutDia,"0"));

        	commUtils.printLog(logId, "   1단좌측재료번호(" + sLeftStlNo + ")=>>좌측코일외경:"+lngLeftOutDia, "SL");
			commUtils.printLog(logId, "   1단우측재료번호(" + sRightStlNo + ")=>>우측코일외경:"+lngRightOutDia, "SL");
        	commUtils.printLog(logId, "   대상재료번호(" + sStlNo + "):"+"대상위치외경군(" + sColOutdiaGp , "SL");
        	commUtils.printLog(logId, "   기준값 : lngRuleADiaDiff:" + lngRuleADiaDiff , "SL");
        	commUtils.printLog(logId, "   기준값 : lngRuleBDiaDiff:" + lngRuleBDiaDiff , "SL");
        	commUtils.printLog(logId, "   기준값 : lngRuleCDiaDiff:" + lngRuleCDiaDiff , "SL");

        	//고정 SKID 
			if ("F".equals(sColYdStkSkidGp)) {

				if ("A".equals(sColOutdiaGp)) {
	        		lngDiaDiffConst = lngRuleADiaDiff;		//550     
	        		lngBedLength    = 2200; 
	        	} else if ("B".equals(sColOutdiaGp)) {
	        		lngDiaDiffConst = lngRuleBDiaDiff;		//820;     
	        		lngBedLength    = 2200;
	        	} else if ("C".equals(sColOutdiaGp)) {
	        		lngDiaDiffConst = lngRuleCDiaDiff;		//850;     
	        		lngBedLength    = 2200;
	        	} else {
	        		rtnMsg = "   적치베드(" + ydStkColGp + ydStkBedNo + ") 외경군 설정이 안되어 있습니다.외경군설정을 확인하세요";
	        		commUtils.printLog(logId, rtnMsg, "SL");
	        		return sRtnVal = CConstant.RETN_CD_FAILURE;
	        	}
				
			} else {
				if ("A".equals(sColOutdiaGp)) {
	        		lngDiaDiffConst = lngRuleADiaDiff;		//550     
	        		lngBedLength    = 1300; 
	        	} else if ("B".equals(sColOutdiaGp)) {
	        		lngDiaDiffConst = lngRuleBDiaDiff;		//820;     
	        		lngBedLength    = 1950;
	        	} else if ("C".equals(sColOutdiaGp)) {
	        		lngDiaDiffConst = lngRuleCDiaDiff;		//850;     
	        		lngBedLength    = 2600;
	        	} else {
	        		rtnMsg = "   적치베드(" + ydStkColGp + ydStkBedNo + ") 외경군 설정이 안되어 있습니다.외경군설정을 확인하세요";
	        		commUtils.printLog(logId, rtnMsg, "SL");
	        		return sRtnVal = CConstant.RETN_CD_FAILURE;
	        	}
			}
        	
        	commUtils.printLog(logId, "   외경간격상수:"+lngDiaDiffConst, "SL");
        	
			lngLength = lngOutDia -(lngBedLength - ((lngLeftOutDia + lngRightOutDia) / 2));
			
        	commUtils.printLog(logId, "lngLength:"+lngLength, "SL");
        	
			if (lngLength > lngDiaDiffConst) {
        		sRtnVal = CConstant.RETN_CD_SUCCESS;
        	} else {
        		sRtnVal = CConstant.RETN_CD_FAILURE;
        	}
			commUtils.printLog(logId, "   "+ mthdNm, "S-");
			return sRtnVal;
			
		} catch(Exception e) {
			rtnMsg = "확인:"+sStlNo+">>2단인 경우 군별 외경간격Check 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			return sRtnVal = CConstant.RETN_CD_FAILURE;
		}
	}

	/**
	 * [A] 오퍼레이션명 : 2단인 경우 군별 외경간격Check(searchCoilGdsYdOutDiaIntervalCheck)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String chk2ODiaIntervalH(String logId, String mthdNms, String ydStkColGp		, String ydStkBedNo , String ydStkLyrNo
			                                                   , String sStlNo			, String sOutDia    , String sColOutdiaGp
			                                                   , String sLeftStlNo		, String sLeftOutDia
			                                                   , String sRightStlNo		, String sRightOutDia
			                                                   , String sColYdStkSkidGp
			                                                   , long   lngRuleADiaDiff	
			                                                   , long   lngRuleBDiaDiff	
			                                                   , long   lngRuleCDiaDiff ) throws JDTOException  {
		String mthdNm = "2단인 경우 군별 외경간격Check[CCoilSchSeEJB.chk2ODiaIntervalH] < " + mthdNms;
		String rtnMsg = "";
		String sRtnVal = CConstant.RETN_CD_FAILURE;

		try {
			
			commUtils.printLog(logId, "   "+ mthdNm, "S+");

			long   lngDiaDiffConst 	= 0;           //외경간격상수
			long   lngBedLength    	= 0;           //군별소요길이
			long   lngLength 		= 0;         

			
        	//권하대상코일 외경 ;
			long lngOutDia 		= Long.parseLong(commUtils.nvl(sOutDia     ,"0"));
			long lngLeftOutDia 	= Long.parseLong(commUtils.nvl(sLeftOutDia ,"0"));
			long lngRightOutDia	= Long.parseLong(commUtils.nvl(sRightOutDia,"0"));

        	commUtils.printLog(logId, "   1단좌측재료번호(" + sLeftStlNo + ")=>>좌측코일외경:"+lngLeftOutDia, "SL");
			commUtils.printLog(logId, "   1단우측재료번호(" + sRightStlNo + ")=>>우측코일외경:"+lngRightOutDia, "SL");
        	commUtils.printLog(logId, "   대상재료번호(" + sStlNo + "):"+"대상위치외경군(" + sColOutdiaGp , "SL");
        	commUtils.printLog(logId, "   기준값 : lngRuleADiaDiff:" + lngRuleADiaDiff , "SL");
        	commUtils.printLog(logId, "   기준값 : lngRuleBDiaDiff:" + lngRuleBDiaDiff , "SL");
        	commUtils.printLog(logId, "   기준값 : lngRuleCDiaDiff:" + lngRuleCDiaDiff , "SL");

        	//고정 SKID 
			if ("F".equals(sColYdStkSkidGp)) {

				if ("A".equals(sColOutdiaGp)) {
	        		lngDiaDiffConst = lngRuleADiaDiff;		//550     
	        		lngBedLength    = 2200; 
	        	} else if ("B".equals(sColOutdiaGp)) {
	        		lngDiaDiffConst = lngRuleBDiaDiff;		//820;     
	        		lngBedLength    = 2200;
	        	} else if ("C".equals(sColOutdiaGp)) {
	        		lngDiaDiffConst = lngRuleCDiaDiff;		//850;     
	        		lngBedLength    = 2200;
	        	} else {
	        		rtnMsg = "   적치베드(" + ydStkColGp + ydStkBedNo + ") 외경군 설정이 안되어 있습니다.외경군설정을 확인하세요";
	        		commUtils.printLog(logId, rtnMsg, "SL");
	        		return sRtnVal = CConstant.RETN_CD_FAILURE;
	        	}
				
			} else if ("C".equals(sColYdStkSkidGp)) {
				if ("A".equals(sColOutdiaGp)) {
	        		lngDiaDiffConst = lngRuleADiaDiff;		//550     
	        		lngBedLength    = 1300; 
	        	} else if ("B".equals(sColOutdiaGp)) {
	        		lngDiaDiffConst = lngRuleBDiaDiff;		//820;     
	        		lngBedLength    = 1950;
	        	} else if ("C".equals(sColOutdiaGp)) {
	        		lngDiaDiffConst = lngRuleCDiaDiff;		//850;     
	        		lngBedLength    = 2600;
	        	} else {
	        		rtnMsg = "   적치베드(" + ydStkColGp + ydStkBedNo + ") 외경군 설정이 안되어 있습니다.외경군설정을 확인하세요";
	        		commUtils.printLog(logId, rtnMsg, "SL");
	        		return sRtnVal = CConstant.RETN_CD_FAILURE;
	        	}
			} else {
				// 소재장
				// 군이 없는 경우 하단 외경 MAX코일 을 비교하여 군 정보 설정
				if (lngLeftOutDia > lngRightOutDia) {
					if (lngLeftOutDia < 1281 ) {    // A군과 동일
						lngDiaDiffConst = lngRuleADiaDiff;		//550     
		        		lngBedLength    = 2200; 
		        	} else if (lngLeftOutDia < 1931 ) {
		        		lngDiaDiffConst = lngRuleBDiaDiff;		//820;     
		        		lngBedLength    = 2200;
		        	} else {
		        		lngDiaDiffConst = lngRuleCDiaDiff;		//850;     
		        		lngBedLength    = 2200;
		        	}	
				} else {
					if (lngRightOutDia < 1281 ) {           // A군과 동일
						lngDiaDiffConst = lngRuleADiaDiff;		//550     
		        		lngBedLength    = 2200; 
		        	} else if (lngRightOutDia < 1931 ) {
		        		lngDiaDiffConst = lngRuleBDiaDiff;		//820;     
		        		lngBedLength    = 2200;
		        	} else {
		        		lngDiaDiffConst = lngRuleCDiaDiff;		//850;     
		        		lngBedLength    = 2200;
		        	}	
				}
			}
        	
        	commUtils.printLog(logId, "   외경간격상수:"+lngDiaDiffConst, "SL");
        	
			lngLength = lngOutDia -(lngBedLength - ((lngLeftOutDia + lngRightOutDia) / 2));
			
        	commUtils.printLog(logId, "lngLength:"+lngLength, "SL");
        	
			if (lngLength > lngDiaDiffConst) {
        		sRtnVal = CConstant.RETN_CD_SUCCESS;
        	} else {
        		sRtnVal = CConstant.RETN_CD_FAILURE;
        	}
			commUtils.printLog(logId, "   "+ mthdNm, "S-");
			return sRtnVal;
			
		} catch(Exception e) {
			rtnMsg = "확인:"+sStlNo+">>2단인 경우 군별 외경간격Check 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			return sRtnVal = CConstant.RETN_CD_FAILURE;
		}
	}

	/**
	 * [A] 오퍼레이션명 : 2단 코일간 간격 CHECK(searchCoilGdsYdOutDiaOverRunCheck)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String chk2OverRun(String logId, String mthdNms, String ydStkColGp , String ydStkBedNo, String ydStkLyrNo
			                                              , String sStlNo     , String lOutDia
			                                              , String sLeftStlNo , String lLeftOutDia
			                                              , String sRightStlNo, String lRightOutDia) throws JDTOException  {
		String mthdNm = "2단 코일간 간격 CHECK[CCoilSchSeEJB.chk2OverRun] < " + mthdNms;
		String rtnMsg = "";
		String sRtnVal = CConstant.RETN_CD_FAILURE;

		try {
			
			commUtils.printLog(logId, "   "+  mthdNm, "S+");
			
			if("".equals(sLeftStlNo) || "".equals(sRightStlNo)) {
				return sRtnVal;
			}
			
			// BED정보
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("YD_STK_COL_GP", ydStkColGp);
			jrParam.setField("YD_STK_BED_NO", ydStkBedNo);
			jrParam.setField("STL_NO"		, sStlNo);
    	    
//        	intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsBed, 319);
//			
//        	if (intRtnVal <= 0) {
//				rtnMsg = "적치베드(" + szYdStkColGp + szYdStkBedNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
//				ydUtils.putLog(szSessionName, szMethodName, rtnMsg, CConstant.ERROR);
//				return sRtnVal;
//			}
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrOverRunChk
			SELECT SF_YD_TO_COIL_OUTDIA_CHK(:V_YD_STK_COL_GP, :V_YD_STK_BED_NO, :V_STL_NO) AS CHK
			FROM DUAL
			*/
			JDTORecordSet jsStkBed = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrOverRunChk", logId, mthdNm, "적치단 조회");
        	if (jsStkBed.size() <= 0) {
				rtnMsg = "   적치베드(" + ydStkColGp + ydStkBedNo + ")로 조회중 error 발생! 에러코드:" + jsStkBed.size();
				commUtils.printLog(logId, rtnMsg, "SL");
				return sRtnVal;
			}
        	
        	jsStkBed.first();
        	JDTORecord jrStkBed = jsStkBed.getRecord();
        	
        	if("Y".equals(commUtils.trim(jrStkBed.getFieldString("CHK")))){
        		sRtnVal = CConstant.RETN_CD_SUCCESS;
        	}else{
        		sRtnVal = CConstant.RETN_CD_FAILURE;
        	}
        	
        	commUtils.printLog(logId, "   "+ mthdNm, "S-");
        	
			return sRtnVal;
				
		} catch(Exception e) {
			rtnMsg = "   횡행좌표 거리 계산 값에 의한 외경 CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			return sRtnVal = CConstant.RETN_CD_FAILURE;
		}
	} 


	/**
	 * [A] 오퍼레이션명 : 2단 중량/폭편차 계산(searchCoilGdsYdWtCheck:searchCoilGdsYdWtCheckABC)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String chk2WgtWidDiff(String logId, String mthdNms, String ydStkColGp
			                             , String sStlNo     , String sThick     , String sWidth     , String sWeigth
			                             , String sLeftStlNo , String sLeftThick , String sLeftWidth , String sLeftWeigth
			                             , String sRightStlNo, String sRightThick, String sRightWidth, String sRightWeigth
			                             , long   lngRuleWgtWid) throws JDTOException  {
		String mthdNm = "2단 중량/폭편차 계산[CCoilSchSeEJB.chk2WgtWidDiff] < " + mthdNms;
		
		String rtnMsg			=	"";
		String sRtnVal = CConstant.RETN_CD_FAILURE;
		
		double dblMinThick  	= 0;
	
		
		try {
			commUtils.printLog(logId,"   "+  mthdNm, "S+");
			
			double dblThick 		= Double.parseDouble(commUtils.nvl(sThick,"0"));
			long   lngWeigth 		= Long.parseLong(commUtils.nvl(sWeigth,"0"));
			double dblWidth 		= Double.parseDouble(commUtils.nvl(sWidth,"0"));
			
			double dblLeftThick 	= Double.parseDouble(commUtils.nvl(sLeftThick,"0"));
			long   lngLeftWeigth 	= Long.parseLong(commUtils.nvl(sLeftWeigth,"0"));
			double dblLeftWidth 	= Double.parseDouble(commUtils.nvl(sLeftWidth,"0"));
			
			double dblRightThick 	= Double.parseDouble(commUtils.nvl(sRightThick,"0"));
			long   lngRightWeigth	= Long.parseLong(commUtils.nvl(sRightWeigth,"0"));
			double dblRightWidth	= Double.parseDouble(commUtils.nvl(sRightWidth,"0"));
			
			double dblMinWidth  	= 0;  //
			double dblMaxWidth  	= 0;  //
			long   lngMinWeigth  	= 0;  // 최소코일중량
			
			if(lngLeftWeigth >= lngRightWeigth){
				lngMinWeigth = lngRightWeigth;  //1단 중량
				dblMinThick  = dblRightThick;   //1단 두께
				
			} else {
				lngMinWeigth = lngLeftWeigth;
				dblMinThick  = dblLeftThick;
			}
			
			if(dblLeftWidth >= dblRightWidth){
				dblMinWidth = dblRightWidth;
				dblMaxWidth = dblLeftWidth;
				
			} else {
				dblMinWidth = dblLeftWidth;
				dblMaxWidth = dblRightWidth;
				
			}
	   	
	    	rtnMsg = "   대상재료번호(" + sStlNo + ")=>>대상코일중량,두께,폭:"+lngWeigth+","+dblThick +","+dblWidth ;
	    	commUtils.printLog(logId, "   대상재료번호(" + sStlNo + ")=>>대상코일중량,두께,폭:"+lngWeigth+","+dblThick +","+dblWidth, "SL");
	    	commUtils.printLog(logId, "   1단좌측재료번호(" + sLeftStlNo + ")=>>좌측중량,두께,폭:"+lngLeftWeigth+","+dblLeftThick+","+dblLeftWidth, "SL");
			commUtils.printLog(logId, "   1단우측재료번호(" + sRightStlNo + ")=>>우측중량,두께,폭:"+lngRightWeigth+","+dblRightThick+","+dblRightWidth, "SL");
			commUtils.printLog(logId, "   "+sStlNo+ ":폭가중치:"+lngRuleWgtWid, "SL");	
	
	    	double dblChkVal  		= 0;  // 
			if  (dblMinThick < 7  ){
				if  (dblMinWidth < 1301  ){
					dblChkVal = lngMinWeigth * 0.13;
				} else {
					dblChkVal = lngMinWeigth * 0.14;
				}	
			} else {
				if  (dblMinWidth < 1301  ){
					dblChkVal = lngMinWeigth * 0.14;
				} else {
					dblChkVal = lngMinWeigth * 0.15;
				}	
			}
			double dblEndWeigth = lngMinWeigth + dblChkVal;
			
			if (lngWeigth <= dblEndWeigth){
				
				if (dblWidth <= dblMaxWidth + lngRuleWgtWid){  // 기준:0,ABC:100
					sRtnVal = "SUCCESS";
				} else {
					sRtnVal = "WID_FAILURE";
				}
	
			} else {
				sRtnVal = "WGT_FAILURE";
			}
			
			commUtils.printLog(logId, "   "+ mthdNm, "S-");
			return sRtnVal;
			
		} catch(Exception e) {
			rtnMsg = "   "+sStlNo+">>2단 중량/폭편차 계산 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			return sRtnVal = CConstant.RETN_CD_FAILURE;
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 2단 중량/폭편차 계산(searchCoilGdsYdWtCheck:searchCoilGdsYdWtCheckABC)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String chk2WgtWidDiffH(String logId, String mthdNms, String ydSchCd, String ydStkColGp
			                             , String sStlNo		, String sWidth		, String sWeigth
			                             , String sLeftStlNo	, String sLeftWidth	, String sLeftWeigth
			                             , String sRightStlNo	, String sRightWidth, String sRightWeigth
			                             , long lngRuleWgt2H	, long lngRuleWid2H
			                             ) throws JDTOException  {
		String mthdNm = "2단 중량/폭편차 계산[CCoilSchSeEJB.chk2WgtWidDiffH] < " + mthdNms;
		
		String rtnMsg	=	"";
		String sRtnVal	= CConstant.RETN_CD_FAILURE;
		
		try {
			commUtils.printLog(logId,"   "+  mthdNm, "S+");
			
			long   lngWeigth 		= Long.parseLong(commUtils.nvl(sWeigth,"0"));
			double dblWidth 		= Double.parseDouble(commUtils.nvl(sWidth,"0"));
			
			long   lngLeftWeigth 	= Long.parseLong(commUtils.nvl(sLeftWeigth,"0"));
			double dblLeftWidth 	= Double.parseDouble(commUtils.nvl(sLeftWidth,"0"));
			
			long   lngRightWeigth	= Long.parseLong(commUtils.nvl(sRightWeigth,"0"));
			double dblRightWidth	= Double.parseDouble(commUtils.nvl(sRightWidth,"0"));
			
			double dblMaxWidth  	= 0;  //
			long   lngMinWeigth  	= 0;  // 최소코일중량
			
			if(lngLeftWeigth >= lngRightWeigth){
				lngMinWeigth = lngRightWeigth;  //1단 최소 중량
				
			} else {
				lngMinWeigth = lngLeftWeigth;
			}
			
			if(dblLeftWidth >= dblRightWidth){	//1단 최대 폭 
				dblMaxWidth = dblLeftWidth;
				
			} else {
				dblMaxWidth = dblRightWidth;
				
			}
			 
	    	commUtils.printLog(logId, "   대상재료번호("+ sStlNo +")=>>중량["+ lngWeigth +"] 폭["+ dblWidth +"] 2단중량기준["+ lngRuleWgt2H +"] 2단폭기준["+ lngRuleWid2H +"]", "SL");
	    	commUtils.printLog(logId, "       1단 좌측("+ sLeftStlNo +")=>>중량["+ lngLeftWeigth +"] 폭["+ dblLeftWidth +"]", "SL");
	    	commUtils.printLog(logId, "       1단 우측("+ sRightStlNo +")=>>중량["+ lngRightWeigth +"] 폭["+ dblRightWidth +"]", "SL");
	    	
	    	/******************************
	    	 * 수입 스케쥴 2단 중량 체크 1000kg
	    	 * 그외 2000kg
	    	 ******************************/
    		// 1단 코일에 2단중량 값 초과시 failure
    		if( lngWeigth > (lngMinWeigth + lngRuleWgt2H) ) {
    			
    			sRtnVal = "WGT_FAILURE";
    			commUtils.printLog(logId, "       * WGT_FAILURE : 2단 중량 체크 (대상코일)"+ lngWeigth +" > ("+ (lngMinWeigth + lngRuleWgt2H) +")1단코일", "SL");
				commUtils.printLog(logId, "   "+ mthdNm, "S-");
				return sRtnVal;
    		}

	    	/******************************
	    	 * 1단 코일과 폭 편차 체크
	    	 ******************************/
    		// 1단 코일에 2단 폭 값 초과시 failure
	    	if( dblWidth > (dblMaxWidth + lngRuleWid2H) ) {
    			sRtnVal = "WID_FAILURE";
    			commUtils.printLog(logId, "       * WID_FAILURE : 2단 폭 체크 (대상코일)"+ dblWidth +" > ("+ (dblMaxWidth + lngRuleWid2H) +")1단코일", "SL");
				commUtils.printLog(logId, "   "+ mthdNm, "S-");
				return sRtnVal;
	    	}

			sRtnVal = "SUCCESS";
			commUtils.printLog(logId, "   "+ mthdNm, "S-");
			return sRtnVal;
			
		} catch(Exception e) {
			rtnMsg = "   "+sStlNo+">>2단 중량/폭편차 계산 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			return sRtnVal = CConstant.RETN_CD_FAILURE;
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 기울기 공식 적용(searchCoilYdGdsCline)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String chk2ClineFormal( String logId, String mthdNms, String ydStkColGp, String ydStkBedNo,String ydStkLyrNo, String sGrpGpLoc,
			                            String sCoilNoA,long lngOutDiaA,
			                            String sCoilNoB,long lngOutDiaB,
			                            String sCoilNoC,long lngOutDiaC,
			                            String sCoilNoD,long lngOutDiaD,
			                            String sCoilNoE,long lngOutDiaE) throws JDTOException  {
		String mthdNm = "기울기 공식 적용[CCoilSchSeEJB.chk2ClineFormal] < " + mthdNms;
		String rtnMsg = "";
		try {
			
			commUtils.printLog(logId, "   "+ mthdNm, "S+");

//			int    iY1 = 0; 
//			int    iY2 = 0; 
			rtnMsg =  "   lngsOutDiaA:"+lngOutDiaA + "/lngsOutDiaB:"+lngOutDiaB+ "/lngsOutDiaC:"+lngOutDiaC+ "/lngsOutDiaD:"+lngOutDiaD+ "/lngsOutDiaE:"+lngOutDiaE;
			commUtils.printLog(logId, rtnMsg, "SL");
			
			if(lngOutDiaC <= lngOutDiaE){

    			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
    			jrParam.setField("GRP_GP_LOC"	, sGrpGpLoc);
    			jrParam.setField("COIL_OUTDIA_A", ""+lngOutDiaA); //좌측2단               ---대상2단 : 목적
    			jrParam.setField("COIL_OUTDIA_B", ""+lngOutDiaB); //좌측1단               ---대상1단
    			jrParam.setField("COIL_OUTDIA_C", ""+lngOutDiaC); //대상1단               ---우측1단
    			jrParam.setField("COIL_OUTDIA_D", ""+lngOutDiaD); //대상2단 : 목적   ---우측2단
    			jrParam.setField("COIL_OUTDIA_E", ""+lngOutDiaE); //우측1단               ---우측+1 1단
    			
    			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilClineCheck1 
    			-- RC <= RE 최종 QUERY
    			WITH T1 AS ( 
    			    SELECT TO_NUMBER(:V_GRP_GP_LOC) LEN   --1950
    				     , TO_NUMBER(:V_COIL_OUTDIA_A) / 2 RA --1827
    					 , TO_NUMBER(:V_COIL_OUTDIA_B) / 2 RB --1782
    					 , TO_NUMBER(:V_COIL_OUTDIA_C) / 2 RC --1728
    					 , TO_NUMBER(:V_COIL_OUTDIA_D) / 2 RD --1745
    					 , TO_NUMBER(:V_COIL_OUTDIA_E) / 2 RE --1902
    				  FROM DUAL 
    			) 
    			 
    			SELECT NVL(ROUND((RD + RC) *
    			       ((NULLIF(((POWER(B.L,2) + POWER((RD + RC),2) - POWER((RD + RE),2)) ),0) / NULLIF((2 * B.L * (RD + RC)) * (LEN / B.L),0)
    			           ) - ( SQRT(1 - POWER(NULLIF(((POWER(B.L,2) + POWER((RD + RC),2) - POWER((RD + RE),2)) ),0) / NULLIF((2 * B.L * (RD + RC)),0),2)) *
    			               NULLIF((RE-RC),0) / NULLIF(B.L,0)
    			        )),0),0) Y1 
    			  FROM   (SELECT SQRT(POWER(LEN,2) + POWER((RE) - (RC),2)) L
    			            FROM   DUAL , T1) B			
    			     , T1 C
    			*/
    			JDTORecordSet jsCoilCline = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilClineCheck1", logId, mthdNm, "기울기 조회");
            	
            	if (jsCoilCline.size() < 1) {
            		return CConstant.RETN_CD_FAILURE;
            	}
            	
            	jsCoilCline.first();
            	JDTORecord jrCoilCline = jsCoilCline.getRecord();
            	
            	int iY1 = Integer.parseInt(commUtils.nvl(jrCoilCline.getFieldString("Y1"),"0")); 
     			
     	    	rtnMsg = "   대상재료번호(" + sCoilNoD + ")=>>Y1:"+iY1 ;
     	    	commUtils.printLog(logId, rtnMsg, "SL");
     	    	
     	    	if(lngOutDiaB >= lngOutDiaC){

          			jrParam = commUtils.getParam(logId, mthdNm, "");
          			jrParam.setField("GRP_GP_LOC"	, sGrpGpLoc);
          			jrParam.setField("COIL_OUTDIA_A", lngOutDiaA+"");
          			jrParam.setField("COIL_OUTDIA_B", lngOutDiaB+"");
          			jrParam.setField("COIL_OUTDIA_C", lngOutDiaC+"");
          			jrParam.setField("COIL_OUTDIA_D", lngOutDiaD+"");
          			jrParam.setField("COIL_OUTDIA_E", lngOutDiaE+"");
                	    
          			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilClineCheck2
          			-- RB >= RC 최종 QUERY
          			WITH T1 AS ( 
          			    SELECT TO_NUMBER(:V_GRP_GP_LOC) LEN   --1950
          				     , TO_NUMBER(:V_COIL_OUTDIA_A) / 2 RA --1827
          					 , TO_NUMBER(:V_COIL_OUTDIA_B) / 2 RB --1782
          					 , TO_NUMBER(:V_COIL_OUTDIA_C) / 2 RC --1728
          					 , TO_NUMBER(:V_COIL_OUTDIA_D) / 2 RD --1745
          					 , TO_NUMBER(:V_COIL_OUTDIA_E) / 2 RE --1902
          				  FROM DUAL 
          			) 
          			SELECT ROUND((RA + RC) * ((((POWER(A.L,2) + POWER((RA + RC),2) - POWER((RA + RB),2)) ) / (2 * A.L * (RA + RC)) * (LEN / A.L)) 
          			                     - (SQRT(1 - POWER(((POWER(A.L,2) + POWER((RA + RC),2) - POWER((RA + RB),2)) ) / (2 * A.L * (RA + RC)),2)) * (RB - RC) / A.L 
          			               )),0) Y2 
          			  FROM  (SELECT SQRT(POWER(LEN,2) + POWER((RB) - (RC),2)) L 
          			             FROM DUAL , T1) A 
          						, T1 B
          			*/			
          			JDTORecordSet jsCoilCline1 = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilClineCheck2", logId, mthdNm, "기울기 조회1");
                	
                	if (jsCoilCline1.size() < 1) {
                		return CConstant.RETN_CD_FAILURE;
                	}
                	
                	jsCoilCline1.first();

         	    	JDTORecord jrCoilCline1 = jsCoilCline1.getRecord();
         	    	
         	    	int iY2 = Integer.parseInt(commUtils.nvl(jrCoilCline1.getFieldString("Y2"),"0")); 
         	    	
         	    	rtnMsg = "   대상재료번호(" + sCoilNoD + ")=>>Y2:"+iY2 ;
         	    	commUtils.printLog(logId, rtnMsg, "SL");
         	    	

         	    	rtnMsg = "   대상재료번호(" + sCoilNoD + ")=>>Y1+Y2:" + (iY1 + iY2 ) ;
         	    	commUtils.printLog(logId, rtnMsg, "SL");
         	    	
         	    	rtnMsg = "   대상재료번호(" + sCoilNoD + ")=>>lngsCOIL_OUTDIA_A+lngsCOIL_OUTDIA_D/2:" + ((lngOutDiaA + lngOutDiaD)/2) + 50 ;
         	    	commUtils.printLog(logId, rtnMsg, "SL");
         	    	
         	    	if((iY1 + iY2) > (((lngOutDiaA + lngOutDiaD)/2) + 50)){
         				commUtils.printLog(logId, "   "+ mthdNm, "S-");
         	    		return CConstant.RETN_CD_SUCCESS;
         	    	} else {
         				commUtils.printLog(logId, "   "+ mthdNm, "S-");
         	    		return CConstant.RETN_CD_FAILURE;
        			}

    			} else {
    				commUtils.printLog(logId, "   "+ mthdNm, "S-");
    				return CConstant.RETN_CD_FAILURE;
    			}
     
			} else {
				commUtils.printLog(logId, "   "+ mthdNm, "S-");
				return CConstant.RETN_CD_FAILURE;
			}

 		} catch(Exception e) {
			rtnMsg = "   "+sCoilNoD+">>기울기 편차 CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			return CConstant.RETN_CD_FAILURE;
		}
		
	}


	/**
	 * [A] 오퍼레이션명 : Y좌표 공식 적용(searchCoilYdGdsClineY)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public int getUpDnClineY(String logId, String mthdNms, String ydStkColGp, String ydStkBedNo, String ydStkLyrNo, String sCoilOutDia) throws JDTOException  {
		String mthdNm = "Y좌표 공식 적용[CCoilSchSeEJB.getUpDnClineY] < " + mthdNms;
		String rtnMsg	= "";
		int intRtnVal   = 0;
		String sCoilNoD = "";
		try {
			commUtils.printLog(logId, "   "+ mthdNm, "S+");
			
   			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
   			jrParam.setField("YD_STK_COL_GP", ydStkColGp);
   			jrParam.setField("YD_STK_BED_NO", ydStkBedNo);
   			jrParam.setField("COIL_OUTDIA"  , sCoilOutDia);
   	    
   			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCoilClineY 
   			SELECT SF_YD_WO_LOC_YAXIS_AUTO (:V_YD_STK_COL_GP, :V_YD_STK_BED_NO, :V_COIL_OUTDIA) AS CLINEY
   			  FROM DUAL
 	    	*/ 
   			
   			JDTORecordSet jsCoilCline = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCoilClineY", logId, mthdNm, "Y좌표 공식 조회");
        	
        	if (jsCoilCline.size() < 1) {
				rtnMsg = "   적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo + ")로 조회중 error 발생! 건수이상:" + jsCoilCline.size();
				commUtils.printLog(logId, rtnMsg, "SL");
				return -1;
        	}
        	
    	
        	jsCoilCline.first();
 	    	JDTORecord jrCoilCline = jsCoilCline.getRecord();
 	    	int iY_valie = Integer.parseInt(commUtils.nvl(jrCoilCline.getFieldString("CLINEY"),"0")); 

        	if (iY_valie == 99999) {
				rtnMsg = "   적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo + ")로 조회중 error 발생! 에러코드:" + "99999";
				commUtils.printLog(logId, rtnMsg, "SL");
				return -1;
        	}

        	
 	    	rtnMsg = "   대상재료번호y값(" + sCoilNoD + ")=>>iY_valie:"+iY_valie ;
 	    	commUtils.printLog(logId, rtnMsg, "SL");
 	    	
 	    	commUtils.printLog(logId, "   "+ mthdNm, "S-"); 	
 	    	
 	    	return iY_valie;
     	   
 			
				
 		} catch(Exception e) {
			rtnMsg =  "   "+sCoilNoD+">>Y좌표 공식 적용 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			return -1;
		}
		
	}
//	/**
//	 * [A] 오퍼레이션명 : Y좌표 공식 적용(searchCoilYdGdsClineY)
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord
//	 * @return
//	 * @throws JDTOException
//	 */
//	public int getUpDnClineY(String logId, String mthdNms, String ydStkColGp, String  ydStkBedNo, String ydStkLyrNo, String sUpYdStkLyrYaxis) throws JDTOException  {
//		String mthdNm = "Y좌표 공식 적용[CCoilSchSeEJB.getUpDnClineY] < " + mthdNms;
//		String rtnMsg	= "";
//		int intRtnVal   = 0;
//		String sCoilNoD = "";
//		try {
//			commUtils.printLog(logId, "   "+ mthdNm, "S+");
//			
//   			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
//   			jrParam.setField("YD_STK_COL_GP", ydStkColGp);
//   			jrParam.setField("YD_STK_BED_NO", ydStkBedNo);
//   			jrParam.setField("YD_STK_LYR_NO", ydStkLyrNo);
//   	    
//   			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilClineY
//   			WITH TEMP_TABLE AS (
//   			    SELECT 'C' AS COIL_GP FROM DUAL UNION ALL
//   			    SELECT 'D' AS COIL_GP FROM DUAL UNION ALL
//   			    SELECT 'E' AS COIL_GP FROM DUAL
//   			),
//   			 TEMP_TABLE1 AS (
//   			    SELECT :V_YD_STK_COL_GP AS YD_STK_COL_GP
//   			         , :V_YD_STK_BED_NO AS YD_STK_BED_NO
//   			      FROM DUAL
//   			)
//   			SELECT *
//   			  FROM (SELECT * FROM TEMP_TABLE ORDER BY COIL_GP) Z1
//   			     , (SELECT A.YD_STK_COL_GP
//   			             , A.YD_STK_BED_NO
//   			             , A.YD_STK_LYR_NO
//   			             , A.STL_NO
//   			             , C.COIL_OUTDIA
//   			             , COUNT(STL_NO) OVER() AS COIL_CNT
//   			             , (CASE WHEN SUBSTR(A.YD_STK_COL_GP, 2, 1) IN ('A', 'B', 'C')
//   			                          THEN 2200
//   			                     ELSE SF_YD_SKID_INTERVAL_GAP(A.YD_STK_COL_GP) * 650
//   			                END) AS GRP_GP_LOC
//   			             , (CASE WHEN A.YD_STK_COL_GP || A.YD_STK_BED_NO || A.YD_STK_LYR_NO
//   			                        = D.YD_STK_COL_GP || D.YD_STK_BED_NO || '001' THEN 'C'
//   			                     WHEN A.YD_STK_COL_GP || A.YD_STK_BED_NO || A.YD_STK_LYR_NO
//   			                        = D.YD_STK_COL_GP || D.YD_STK_BED_NO || '002' THEN 'D'
//   			                     WHEN A.YD_STK_COL_GP || A.YD_STK_BED_NO || A.YD_STK_LYR_NO
//   			                        = D.YD_STK_COL_GP || LPAD(TO_NUMBER(D.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(A.YD_STK_COL_GP), 2, '0') || '001' THEN 'E'
//   			                     ELSE'' END) COIL_GP1
//   			          FROM TB_YD_STKLYR A
//   			             , TB_YD_STKCOL B
//   			             , USRPTA.TB_PT_COILCOMM C
//   			             , TEMP_TABLE1 D
//   			         WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
//   			           AND A.YD_STK_COL_GP = D.YD_STK_COL_GP
//   			           AND A.YD_STK_BED_NO IN (D.YD_STK_BED_NO
//   			                                 , LPAD(TO_NUMBER(D.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(A.YD_STK_COL_GP), 2, '0')
//   			                                  )
//   			           AND A.STL_NO = C.COIL_NO(+)
//   			       ) Z2
//   			 WHERE Z1.COIL_GP = Z2.COIL_GP1(+)
// 	    	*/ 
//   			
//   			JDTORecordSet jsStkLyr = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilClineY", logId, mthdNm, "Y좌표 공식 조회");
//        	
//        	if (jsStkLyr.size() < 1) {
//				rtnMsg = "   적치단(" + ydStkColGp + ydStkBedNo + ydStkLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
//				commUtils.printLog(logId, rtnMsg, "SL");
//				return -1;
//        	}
//        	
//        	JDTORecord jrStkLyr = JDTORecordFactory.getInstance().create();
//			String sCoilOutDiaC 	= "";  
//			String sCoilOutDiaD 	= "";  
//			String sCoilOutDiaE 	= "";  
//			String sGrpGpLoc        = "";
//
// 	    	for(int Loop_i = 1; Loop_i <= jsStkLyr.size(); Loop_i++) {
// 	    		jsStkLyr.absolute(Loop_i);
// 	    		jrStkLyr = jsStkLyr.getRecord();
//				
//				String sCoilGp = commUtils.trim(jrStkLyr.getFieldString("COIL_GP"));
//				//좌측1단
//				if ("C".equals(sCoilGp)) {
//					sGrpGpLoc		= commUtils.nvl(commUtils.trim(jrStkLyr.getFieldString("GRP_GP_LOC")),"0");  
//					
//					sCoilOutDiaC 	= commUtils.nvl(commUtils.trim(jrStkLyr.getFieldString("COIL_OUTDIA")),"0"); 
//				//대상2단 : 목적
//				} else if ("D".equals(sCoilGp)) {
//					sCoilNoD 		= commUtils.trim(jrStkLyr.getFieldString("STL_NO"));
//					sCoilOutDiaD 	= commUtils.nvl(commUtils.trim(jrStkLyr.getFieldString("COIL_OUTDIA")),"0");  
//				//우측1단
//				} else if ("E".equals(sCoilGp)) {
//					sCoilOutDiaE 	= commUtils.nvl(commUtils.trim(jrStkLyr.getFieldString("COIL_OUTDIA")),"0");
//				}
//			}
//
//			long lngGrpGpLoc	= Long.parseLong(sGrpGpLoc);
//			long lngCoilOutDiaC	= Long.parseLong(sCoilOutDiaC);
//			long lngCoilOutDiaE	= Long.parseLong(sCoilOutDiaE);
//			
//        	if (lngCoilOutDiaC == 0 || lngCoilOutDiaE == 0) {
//        		commUtils.printLog(logId, "하단 정보 이상", "SL");
// 	    		return -1;
//        	}			
//			
//			if(lngCoilOutDiaC <= lngCoilOutDiaE){
//
//    			jrParam = commUtils.getParam(logId, mthdNm, "");
//
//    			jrParam.setField("GRP_GP_LOC"	, sGrpGpLoc);
//    			jrParam.setField("COIL_OUTDIA_C", sCoilOutDiaC);
//    			jrParam.setField("COIL_OUTDIA_D", sCoilOutDiaD);
//    			jrParam.setField("COIL_OUTDIA_E", sCoilOutDiaE);
//    			
//    			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilClineCheck1
//    			-- RC <= RE 최종 QUERY
//    			WITH T1 AS ( 
//    			    SELECT TO_NUMBER(:V_GRP_GP_LOC) LEN   --1950
//    				     , TO_NUMBER(:V_COIL_OUTDIA_A) / 2 RA --1827
//    					 , TO_NUMBER(:V_COIL_OUTDIA_B) / 2 RB --1782
//    					 , TO_NUMBER(:V_COIL_OUTDIA_C) / 2 RC --1728
//    					 , TO_NUMBER(:V_COIL_OUTDIA_D) / 2 RD --1745
//    					 , TO_NUMBER(:V_COIL_OUTDIA_E) / 2 RE --1902
//    				  FROM DUAL 
//    			) 
//    			 
//    			SELECT NVL(ROUND((RD + RC) *
//    			       ((NULLIF(((POWER(B.L,2) + POWER((RD + RC),2) - POWER((RD + RE),2)) ),0) / NULLIF((2 * B.L * (RD + RC)) * (LEN / B.L),0)
//    			           ) - ( SQRT(1 - POWER(NULLIF(((POWER(B.L,2) + POWER((RD + RC),2) - POWER((RD + RE),2)) ),0) / NULLIF((2 * B.L * (RD + RC)),0),2)) *
//    			               NULLIF((RE-RC),0) / NULLIF(B.L,0)
//    			        )),0),0) Y1 
//    			  FROM   (SELECT SQRT(POWER(LEN,2) + POWER((RE) - (RC),2)) L
//    			            FROM   DUAL , T1) B			
//    			     , T1 C
//    			     
//    			*/
//    			JDTORecordSet jsCoilCline = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilClineCheck1", logId, mthdNm, "기울기 조회");
//            	
//            	if (jsCoilCline.size() < 1) {
//     	    		return -1;
//            	}
//    			
//            	jsCoilCline.first();
//     	    	JDTORecord jrCoilCline = jsCoilCline.getRecord();
//     	    	int iY1 = Integer.parseInt(commUtils.nvl(jrCoilCline.getFieldString("Y1"),"0")); 
//     			
//     	    	rtnMsg = "   대상재료번호(" + sCoilNoD + ")=>>Y1:"+iY1 ;
//     	    	commUtils.printLog(logId, rtnMsg, "SL");
//     	    	
//     	    	int iClineY = (int)(lngGrpGpLoc / 2 - iY1);
//     
//     	    	rtnMsg = "   대상재료번호1(" + sCoilNoD + ")=>>iClineY:"+iClineY + "/sUP_YD_STK_LYR_YAXIS:"+ sUpYdStkLyrYaxis ;
//     	    	commUtils.printLog(logId, rtnMsg, "SL");
//     
//     	    	int iY_valie = Integer.parseInt(sUpYdStkLyrYaxis) - iClineY ;
//
//     	    	rtnMsg = "   대상재료번호y값(" + sCoilNoD + ")=>>iY_valie:"+iClineY ;
//     	    	commUtils.printLog(logId, rtnMsg, "SL");
//     	    	
//     	    	commUtils.printLog(logId, "   "+ mthdNm, "S-"); 	
//     	    	
//     	    	return iY_valie;
//     	    	
//			} else {
//
//				jrParam = commUtils.getParam(logId, mthdNm, "");
//				jrParam.setField("GRP_GP_LOC"	, sGrpGpLoc);
//    			jrParam.setField("COIL_OUTDIA_C", sCoilOutDiaC);
//    			jrParam.setField("COIL_OUTDIA_D", sCoilOutDiaD);
//    			jrParam.setField("COIL_OUTDIA_E", sCoilOutDiaE);
//    			
//    			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilClineCheck2
//      			-- RB >= RC 최종 QUERY
//      			WITH T1 AS ( 
//      			    SELECT TO_NUMBER(:V_GRP_GP_LOC) LEN   --1950
//      				     , TO_NUMBER(:V_COIL_OUTDIA_A) / 2 RA --1827
//      					 , TO_NUMBER(:V_COIL_OUTDIA_B) / 2 RB --1782
//      					 , TO_NUMBER(:V_COIL_OUTDIA_C) / 2 RC --1728
//      					 , TO_NUMBER(:V_COIL_OUTDIA_D) / 2 RD --1745
//      					 , TO_NUMBER(:V_COIL_OUTDIA_E) / 2 RE --1902
//      				  FROM DUAL 
//      			) 
//      			SELECT ROUND((RA + RC) * ((((POWER(A.L,2) + POWER((RA + RC),2) - POWER((RA + RB),2)) ) / (2 * A.L * (RA + RC)) * (LEN / A.L)) 
//      			                     - (SQRT(1 - POWER(((POWER(A.L,2) + POWER((RA + RC),2) - POWER((RA + RB),2)) ) / (2 * A.L * (RA + RC)),2)) * (RB - RC) / A.L 
//      			               )),0) Y2 
//      			  FROM  (SELECT SQRT(POWER(LEN,2) + POWER((RB) - (RC),2)) L 
//      			             FROM DUAL , T1) A 
//      						, T1 B
//      			*/			
//      			JDTORecordSet jsCoilCline1 = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilClineCheck2", logId, mthdNm, "기울기 조회1");
//            	if (jsCoilCline1.size() < 1) {
//     	    		return -1;
//            	}
//            	
//            	jsCoilCline1.first();
//     	    	JDTORecord jrCoilCline1 = jsCoilCline1.getRecord();
//     	    	int iY2 = Integer.parseInt(commUtils.nvl(jrCoilCline1.getFieldString("Y2"),"0"));
//     			
//     	    	rtnMsg = "   대상재료번호(" + sCoilNoD + ")=>>Y2:"+iY2 ;
//     	    	commUtils.printLog(logId, rtnMsg, "SL");
//     	    	
//     	    	int iClineY = (int)(lngGrpGpLoc / 2 - iY2);
//     
//     	    	rtnMsg = "   대상재료번호2(" + sCoilNoD + ")=>>iClineY:"+iClineY + "/sUP_YD_STK_LYR_YAXIS:"+ sUpYdStkLyrYaxis;
//     	    	commUtils.printLog(logId, rtnMsg, "SL");
//
//     	    	int iY_valie = Integer.parseInt(sUpYdStkLyrYaxis) + iClineY ;
//
//     	    	rtnMsg = "   대상재료번호y값(" + sCoilNoD + ")=>>iY_valie:"+iClineY ;
//     	    	commUtils.printLog(logId, rtnMsg, "SL");
//     	    	
//     	    	commUtils.printLog(logId, "   "+ mthdNm, "S-"); 	
//     	    	return iY_valie;
//			}
// 			
//				
// 		} catch(Exception e) {
//			rtnMsg =  "   "+sCoilNoD+">>Y좌표 공식 적용 예외발생! 예외메세지: " + e.getMessage();
//			commUtils.printLog(logId, rtnMsg, "SL");
//			return -1;
//		}
//		
//	}
	/**
	 * [A] 오퍼레이션명 : 1단 좌우 폭편차 Check(searchCoilYdGdsWidthDiffCheck)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String chk1WidDiff(String logId, String mthdNms, String sStlNo,     String ydStkLyrNo, String sWidth
			                                                , String sLeftStlNo, String sLeftWidth
			                                                , String sRightStlNo, String sRightWidth
			                                                , long   lngRuleWid1) throws JDTOException  {
		String mthdNm = "1단좌우폭편차Check[CCoilSchSeEJB.chk1WidDiff] < " + mthdNms;
		String rtnMsg = "";
		String sRtnVal = CConstant.RETN_CD_FAILURE;
		 
		try {
			commUtils.printLog(logId,"   "+  mthdNm, "S+");
			
			double dblWidth 		= Double.parseDouble(commUtils.nvl(sWidth,"0"));
			double dblLeftWidth 	= Double.parseDouble(commUtils.nvl(sLeftWidth,"0"));
			double dblRighdWidth 	= Double.parseDouble(commUtils.nvl(sRightWidth,"0"));
			
//좌우재료번호가 이적대상과 동일 한 경우 
			if(sRightStlNo.equals(sStlNo)){
				dblRighdWidth = 0;
				sRightStlNo   = "";
			}else if(sLeftStlNo.equals(sStlNo)){
				dblLeftWidth  = 0;
				sLeftStlNo    = "";
			}

			commUtils.printLog(logId, "   대상재료번호     (" + sStlNo + ")=>>대상코일폭:"+dblWidth, "SL");
			commUtils.printLog(logId, "   1단좌측재료번호(" + sLeftStlNo  + ")=>>좌측코일폭:"+dblLeftWidth, "SL");
			commUtils.printLog(logId, "   1단우측재료번호(" + sRightStlNo + ")=>>우측코일폭:"+dblRighdWidth, "SL");
			commUtils.printLog(logId, "   기준값 : " + lngRuleWid1 , "SL");			
			
			if ("001".equals(ydStkLyrNo)){
				// 좌우가 비워있는경우
				if ("".equals(sLeftStlNo) && "".equals(sRightStlNo)) {  // 좌우가 비워있는경우
	        		sRtnVal = CConstant.RETN_CD_SUCCESS;
	        		
				}else{
					commUtils.printLog(logId, "   "+sStlNo+"Math.abs(lngWidth - lngRighdWidth)" + Math.abs(dblWidth - dblRighdWidth), "SL");
					commUtils.printLog(logId, "   "+sStlNo+"Math.abs(lngWidth - lngLeftWidth))" + Math.abs(dblWidth - dblLeftWidth), "SL");
//						좌우코일이 존재 하는 경우
					if (!"".equals(sLeftStlNo) && (!"".equals(sRightStlNo))) {
						if (Math.abs(dblWidth - dblRighdWidth) <= lngRuleWid1 && Math.abs(dblWidth - dblLeftWidth) <= lngRuleWid1) {
			        		sRtnVal = CConstant.RETN_CD_SUCCESS;
			        	} else {
			        		sRtnVal = CConstant.RETN_CD_FAILURE;
			        	}
					}else{
						if (Math.abs(dblWidth - dblRighdWidth) <= lngRuleWid1 || Math.abs(dblWidth - dblLeftWidth) <= lngRuleWid1) {
			        		sRtnVal = CConstant.RETN_CD_SUCCESS;
			        	} else {
			        		sRtnVal = CConstant.RETN_CD_FAILURE;
			        	}
					}
				}
			} 
			commUtils.printLog(logId, "   "+ mthdNm, "S-");
			
 			return sRtnVal;
		} catch(Exception e) {
			rtnMsg = "   "+sStlNo+">>1단 좌우 폭편차 Check 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			return sRtnVal = CConstant.RETN_CD_FAILURE;
		}
		
	}


	/**
	 * [A] 오퍼레이션명 : 2단인경우1단좌우외경편차Check(searchCoilYdGdsWidthDiffCheck2)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String chk2ODiaDiff(String logId, String mthdNms,  String ydStkLyrNo, String sStlNo     , String sOutDia
			                                                                   , String sLeftStlNo , String sLeftOutDia
			                                                                   , String sRightStlNo, String sRightOutDia
			                                                                   , long   lngRuleODia2 ) throws JDTOException  {
		String mthdNm = "2단인경우1단좌우외경편차Check[CCoilSchSeEJB.chk2ODiaDiff] < " + mthdNms;
		String rtnMsg = "";
		String sRtnVal = CConstant.RETN_CD_FAILURE;

		try {
			
			commUtils.printLog(logId, "   "+ mthdNm, "S+");
			
//외경편차			
			long lngOutDia 			= Long.parseLong(commUtils.nvl(sOutDia		,"0"));
			long lngLeftOutDia 		= Long.parseLong(commUtils.nvl(sLeftOutDia	,"0"));
			long lngRightOutDia		= Long.parseLong(commUtils.nvl(sRightOutDia	,"0"));

			commUtils.printLog(logId, "   대상재료번호(" + sStlNo + ")=>>대상코일외경:"+lngOutDia, "SL");
			commUtils.printLog(logId, "   1단좌측재료번호(" + sLeftStlNo + ")=>>좌측코일외경:"+lngLeftOutDia , "SL");
			commUtils.printLog(logId, "   1단우측재료번호(" + sRightStlNo + ")=>>우측코일외경:"+lngRightOutDia, "SL");
			commUtils.printLog(logId, "   기준값 : " + lngRuleODia2 , "SL");
			
			if (Math.abs(lngLeftOutDia - lngRightOutDia) < lngRuleODia2) {  
				sRtnVal = CConstant.RETN_CD_SUCCESS;
			} else {
				sRtnVal = CConstant.RETN_CD_FAILURE;
			}
			
			commUtils.printLog(logId, "   "+ mthdNm, "S-");
 			return sRtnVal;
 			
		} catch(Exception e) {
			rtnMsg = "   "+sStlNo+">> 2단인경우1단좌우외경편차Check 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			return sRtnVal = CConstant.RETN_CD_FAILURE;
		}
	}


	/**
	 * [A] 오퍼레이션명 : 2단B군인경우1단좌우외경편차Check(searchCoilYdGdsWidthDiffCheck4)
	 *                               
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String chk2ODiaBGunDiffABC(String logId, String mthdNms, String ydStkLyrNo, String sStlNo     , String sOutDia
			                                                                         , String sLeftStlNo , String sLeftOutDia
			                                                                         , String sRightStlNo, String sRightOutDia
			                                                                         , long   lngRuleBODiaABC ) throws JDTOException  {
		String mthdNm = "2단B군인경우1단좌우외경편차Check[CCoilSchSeEJB.chk2ODiaBGunDiffABC] < " + mthdNms;
		String rtnMsg = "";
		String sRtnVal = CConstant.RETN_CD_FAILURE;
				
		try {
			commUtils.printLog(logId, "   "+ mthdNm, "S+");

//외경편차			
			long lngOutDia 			= Long.parseLong(commUtils.nvl(sOutDia,"0"));
			long lngLeftOutDia 		= Long.parseLong(commUtils.nvl(sLeftOutDia,"0"));
			long lngRightOutDia		= Long.parseLong(commUtils.nvl(sRightOutDia,"0"));

			commUtils.printLog(logId, "   대상재료번호(" + sStlNo + ")=>>대상코일외경:      "+lngOutDia , "SL");
			commUtils.printLog(logId, "   1단좌측재료번호(" + sLeftStlNo + ")=>>좌측코일외경:"+lngLeftOutDia, "SL");
			commUtils.printLog(logId, "   1단우측재료번호(" + sRightStlNo + ")=>>우측코일외경:"+lngRightOutDia, "SL");
			commUtils.printLog(logId, "   기준값 : " + lngRuleBODiaABC , "SL");
			                                  //1500                                        //1500
			if (Math.abs(lngLeftOutDia) < lngRuleBODiaABC || Math.abs(lngRightOutDia) < lngRuleBODiaABC) {  
				sRtnVal = CConstant.RETN_CD_FAILURE;
			} else {
				sRtnVal = CConstant.RETN_CD_SUCCESS;
			}
			
//			아래 하드코딩 BRE로 전환 end
			commUtils.printLog(logId, "   "+ mthdNm, "S-");
			
 			return sRtnVal;
		} catch(Exception e) {
			rtnMsg = "   "+sStlNo+">> 2단B군인경우1단좌우외경편차Check 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			return sRtnVal = CConstant.RETN_CD_FAILURE;
		}
	}

	/**
	 * [A] 오퍼레이션명 : 전후좌우 폭간섭 Check(searchCoilYdGdsWidthDiffCheck3)
	 * @통합
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String chkColWidInterf( String logId, String mthdNms, String sStlNo,  String ydStkColGp, String ydStkBedNo, String ydStkLyrNo 
			                                                   , long   lngRuleCrnLift) throws JDTOException  {
		String mthdNm = "좌우열폭간섭Check[CCoilSchSeEJB.chkColWidInterf] < " + mthdNms;
		String rtnMsg = "";
		String sRtnVal = CConstant.RETN_CD_FAILURE;
		
		try {
			
			commUtils.printLog(logId, "   "+ mthdNm, "S+");

			rtnMsg = "   대상재료번호(" + sStlNo + ")=>>적치열 구분:"+ydStkColGp + " 번지:"+ydStkBedNo + " 단:"+ydStkLyrNo ;
			commUtils.printLog(logId, rtnMsg, "SL");
			

			if ("".equals(ydStkColGp)) {  
				sRtnVal = CConstant.RETN_CD_FAILURE;
				return sRtnVal;
			}

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
        	jrParam.setField("YD_STK_COL_GP" 	, ydStkColGp);	
        	jrParam.setField("YD_STK_BED_NO" 	, ydStkBedNo);	
        	jrParam.setField("YD_STK_LYR_NO" 	, ydStkLyrNo);	
        	jrParam.setField("STL_NO" 			, sStlNo);	
        	jrParam.setField("CRANE_GAP"		, ""+lngRuleCrnLift);	
        	
			/**********************************
			 * 대상재코일 좌표로 간섭코일중 최대폭 CHECK
			 **************************************/
        	
        	/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getChkColWidInterf 
        	WITH TARGET_PARA AS (
        	 SELECT YD_STK_COL_GP                             AS TK_YD_STK_COL_GP 
        	     , NVL(BEFO_YD_STK_COL_GP,NEXT_YD_STK_COL_GP) AS BEFO_YD_STK_COL_GP
        	     , NVL(NEXT_YD_STK_COL_GP,BEFO_YD_STK_COL_GP) AS NEXT_YD_STK_COL_GP
        	     -- 대상열이 없는 경우 무조건 OK 하기 위해  JB0101 이전 
        	     , CASE WHEN BEFO_YD_STK_COL_GP IS NULL THEN 'N' ELSE 'Y' END AS BEFO_CHK_YN
        	     , CASE WHEN NEXT_YD_STK_COL_GP IS NULL THEN 'N' ELSE 'Y' END AS NEXT_CHK_YN
        	  FROM
        	     (  SELECT A.YD_STK_COL_GP
        	             , LEAD(A.YD_STK_COL_GP) OVER (ORDER BY YD_STK_COL_GP DESC) BEFO_YD_STK_COL_GP
        	             , LAG (A.YD_STK_COL_GP) OVER (ORDER BY YD_STK_COL_GP DESC) NEXT_YD_STK_COL_GP
        	          FROM TB_YD_STKCOL A
        	         WHERE A.YD_STK_COL_GP LIKE SUBSTR(:V_YD_STK_COL_GP,1,2)||'%'
        	           AND A.DEL_YN = 'N'
        	         ORDER BY YD_STK_COL_GP
        	     )
        	 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP 
        	)
        	, TARGET_COL AS (
        	-- 대상열 검색
        	SELECT * 
        	  FROM
        	       (
        	        SELECT 'L'                   AS COL_GP
        	             , BEFO_CHK_YN           AS COL_CHK_YN         
        	             , SL.YD_STK_COL_GP      AS COL_YD_STK_COL_GP
        	             , SL.YD_STK_BED_NO      AS COL_YD_STK_BED_NO
        	             , SL.YD_STK_LYR_NO      AS COL_YD_STK_LYR_NO
        	             , SL.STL_NO             AS COL_STL_NO
        	             , SL.YD_STK_LYR_XAXIS   AS COL_YD_STK_LYR_XAXIS
        	             , SL.YD_STK_LYR_YAXIS   AS COL_YD_STK_LYR_YAXIS
        	             , NVL(CC.COIL_W,0)      AS COL_COIL_W
        	             , NVL(CC.COIL_OUTDIA,0) AS COL_COIL_OUTDIA
        	             , ROUND(SL.YD_STK_LYR_YAXIS - NVL((CC.COIL_OUTDIA / 2),0)) AS COL_MIN_YXIS
        	             , ROUND(SL.YD_STK_LYR_YAXIS + NVL((CC.COIL_OUTDIA / 2),0)) AS COL_MAX_YXIS
        	             
        	          FROM TARGET_PARA    TP
        	             , TB_YD_STKLYR   SL
        	             , TB_PT_COILCOMM CC
        	         WHERE TP.BEFO_YD_STK_COL_GP = SL.YD_STK_COL_GP
        	           AND SL.STL_NO = CC.COIL_NO(+)  
        	           AND SL.DEL_YN = 'N'
        	        UNION ALL
        	        SELECT 'R'                   AS COL_GP
        	             , NEXT_CHK_YN           AS COL_CHK_YN 
        	             , SL.YD_STK_COL_GP      AS COL_YD_STK_COL_GP
        	             , SL.YD_STK_BED_NO      AS COL_YD_STK_BED_NO
        	             , SL.YD_STK_LYR_NO      AS COL_YD_STK_LYR_NO
        	             , SL.STL_NO             AS COL_STL_NO
        	             , SL.YD_STK_LYR_XAXIS   AS COL_YD_STK_LYR_XAXIS
        	             , SL.YD_STK_LYR_YAXIS   AS COL_YD_STK_LYR_YAXIS
        	             , NVL(CC.COIL_W,0)      AS COL_COIL_W
        	             , NVL(CC.COIL_OUTDIA,0) AS COL_COIL_OUTDIA
        	             , ROUND(SL.YD_STK_LYR_YAXIS - NVL((CC.COIL_OUTDIA / 2),0)) AS COL_MIN_YXIS
        	             , ROUND(SL.YD_STK_LYR_YAXIS + NVL((CC.COIL_OUTDIA / 2),0)) AS COL_MAX_YXIS
        	          FROM TARGET_PARA    TP
        	             , TB_YD_STKLYR   SL
        	             , TB_PT_COILCOMM CC
        	         WHERE TP.NEXT_YD_STK_COL_GP = SL.YD_STK_COL_GP
        	           AND SL.STL_NO = CC.COIL_NO(+) 
        	           AND SL.DEL_YN = 'N'
        	       ) 
        	 ORDER BY COL_GP
        	        , COL_YD_STK_COL_GP
        	        , COL_YD_STK_BED_NO
        	        , COL_YD_STK_LYR_NO
        	) 
        	, TARGET_COL_DATA AS (
        	--대상코일 권하시 Y값(대상열+외경)에 따른 좌우대상열 코일정보 READ
        	SELECT TC.* 
        	     , TY.*  
        	     , MAX(TY.COL_COIL_W) OVER (PARTITION BY TY.COL_GP )  AS COL_COIL_MAX_W
        	  FROM TARGET_COL TY
        	     , (
        	        SELECT SL.YD_STK_COL_GP                                  AS COIL_YD_STK_COL_GP
        	             , CC.COIL_W                                         AS COIL_COIL_W
        	             , CC.COIL_OUTDIA                                    AS COIL_COIL_OUTDIA
        	             , SL.YD_STK_LYR_XAXIS                               AS COIL_YD_STK_LYR_XAXIS
        	             , SL.YD_STK_LYR_YAXIS                               AS COIL_YD_STK_LYR_YAXIS
        	             , ROUND(SL.YD_STK_LYR_YAXIS - (CC.COIL_OUTDIA / 2)) AS COIL_MIN_YXIS
        	             , ROUND(SL.YD_STK_LYR_YAXIS + (CC.COIL_OUTDIA / 2)) AS COIL_MAX_YXIS
        	          FROM TB_YD_STKLYR SL
        	             , (SELECT * FROM TB_PT_COILCOMM A WHERE  A.COIL_NO = :V_STL_NO) CC
        	         WHERE SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
        	           AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
        	           AND SL.YD_STK_LYR_NO = :V_YD_STK_LYR_NO
        	       ) TC

        	 WHERE 1 =1
        	   AND COIL_MIN_YXIS < COL_MAX_YXIS
        	   AND COIL_MAX_YXIS > COL_MIN_YXIS
        	)   
        	SELECT A.* 
        	     , CASE WHEN SUM (DECODE( ABLE_YN ,'Y',1,0 )) OVER () = '2' THEN 'Y'
        	            ELSE 'N' END AS LOC_ABLE_YN
        	  FROM (
        	        SELECT CASE WHEN COL_CHK_YN = 'N'      THEN 'Y' 
        	                    WHEN CD.COL_STL_NO IS NULL THEN 'Y'                                                                            --500
        	                    WHEN ABS(CD.COIL_YD_STK_LYR_XAXIS - CD.COL_YD_STK_LYR_XAXIS) - (CD.COL_COIL_MAX_W/2) - (CD.COIL_COIL_W/2) > :V_CRANE_GAP THEN 'Y' ELSE 'N'
        	                    END  ABLE_YN
        	             , ABS(CD.COIL_YD_STK_LYR_XAXIS - CD.COL_YD_STK_LYR_XAXIS) CC       
        	             , CD.COL_COIL_MAX_W / 2  AS COL_W2
        	             , CD.COIL_COIL_W    / 2  AS COIL_W2
        	             , ABS(CD.COIL_YD_STK_LYR_XAXIS - CD.COL_YD_STK_LYR_XAXIS) - (CD.COL_COIL_MAX_W/2) - (CD.COIL_COIL_W/2)  AS GAP       
        	             , CD.*       
        	          FROM TARGET_COL_DATA CD
        	         WHERE CD.COL_COIL_W = CD.COL_COIL_MAX_W
        	           AND COL_GP = 'L'
        	           AND ROWNUM = 1
        	        UNION ALL   
        	        SELECT CASE WHEN COL_CHK_YN = 'N'      THEN 'Y' 
        	                    WHEN CD.COL_STL_NO IS NULL THEN 'Y'                                                                             --500
        	                    WHEN ABS(CD.COL_YD_STK_LYR_XAXIS - CD.COIL_YD_STK_LYR_XAXIS) - (CD.COL_COIL_MAX_W/2) - (CD.COIL_COIL_W/2) > :V_CRANE_GAP THEN 'Y' ELSE 'N'
        	                    END  ABLE_YN
        	             , ABS(CD.COL_YD_STK_LYR_XAXIS - CD.COIL_YD_STK_LYR_XAXIS) CC       
        	             , CD.COL_COIL_MAX_W / 2  AS COL_W2
        	             , CD.COIL_COIL_W    / 2  AS COIL_W2
        	             , ABS(CD.COIL_YD_STK_LYR_XAXIS - CD.COL_YD_STK_LYR_XAXIS) - (CD.COL_COIL_MAX_W/2) - (CD.COIL_COIL_W/2)  AS GAP       
        	             , CD.*       
        	          FROM TARGET_COL_DATA CD
        	         WHERE CD.COL_COIL_W = CD.COL_COIL_MAX_W
        	           AND CD.COL_GP = 'R'
        	           AND ROWNUM = 1
        	        ) A
          */
          	JDTORecordSet jsColWid = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getChkColWidInterf", logId, mthdNm, "좌우열코일 MAX폭");
          	
          	JDTORecord jrColWid = JDTORecordFactory.getInstance().create();
          	String sAbleYn  = "";
          	String sColGp   = "";
          	
			if (jsColWid.size() == 2) {

				commUtils.printParam(logId, jsColWid);
	
				for (int i = 1; i <= jsColWid.size(); i++) {
	
					jsColWid.absolute(i);
					jrColWid  = jsColWid.getRecord();
					
					sAbleYn = commUtils.nvl (jrColWid.getFieldString("ABLE_YN"),"N");
					sColGp  = commUtils.nvl (jrColWid.getFieldString("COL_GP"),"");
					
					if ("L".equals(sColGp)) {
						if ("N".equals(sAbleYn)) {
				    		commUtils.printLog(logId, "   전열   "+commUtils.trim(jrColWid.getFieldString("YD_STK_COL_GP"))+" 폭체크에서  불가합니다." , "SL");
				    		return sRtnVal;
						}
					} else { 
						if ("N".equals(sAbleYn)) {
				    		commUtils.printLog(logId, "   다음열"+commUtils.trim(jrColWid.getFieldString("YD_STK_COL_GP"))+" 폭체크에서  불가합니다." , "SL");
				    		return sRtnVal;
						}
					}
				}	
			} else {	
	    		commUtils.printLog(logId, "   좌우열 폭체크에서  해당열 검색 이상발생." , "SL");
	    		return sRtnVal;
			}	
			commUtils.printLog(logId, rtnMsg, "SL");
			
			commUtils.printLog(logId, "   "+ mthdNm, "S-");
			return sRtnVal = CConstant.RETN_CD_SUCCESS;

		} catch(Exception e) {
			rtnMsg = "   "+sStlNo+">>1단전후좌우폭간섭Check중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			return sRtnVal = CConstant.RETN_CD_FAILURE;
		}
	}

	/**
	 * [A] 오퍼레이션명 : 소재 전후좌우 폭간섭 Check(searchCoilYdGdsWidthDiffCheck3)
	 * @통합
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String chkColWidInterfH( String logId, String mthdNms, String sStlNo,  String ydStkColGp, String ydStkBedNo, String ydStkLyrNo 
			                                                   , long   lngRuleCrnLift) throws JDTOException  {
		String mthdNm = "좌우열폭간섭Check[CCoilSchSeEJB.chkColWidInterfH] < " + mthdNms;
		String rtnMsg = "";
		String sRtnVal = CConstant.RETN_CD_FAILURE;
		
		try {
			
			commUtils.printLog(logId, "   "+ mthdNm, "S+");

			rtnMsg = "   대상재료번호(" + sStlNo + ")=>>적치열 구분:"+ydStkColGp + " 번지:"+ydStkBedNo + " 단:"+ydStkLyrNo ;
			commUtils.printLog(logId, rtnMsg, "SL");
			

			if ("".equals(ydStkColGp)) {  
				sRtnVal = CConstant.RETN_CD_FAILURE;
				return sRtnVal;
			}

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
        	jrParam.setField("YD_STK_COL_GP" 	, ydStkColGp);	
        	jrParam.setField("YD_STK_BED_NO" 	, ydStkBedNo);	
        	jrParam.setField("YD_STK_LYR_NO" 	, ydStkLyrNo);	
        	jrParam.setField("STL_NO" 			, sStlNo);	
        	jrParam.setField("CRANE_GAP"		, ""+lngRuleCrnLift);	
        	
			/**************************************
			 * 대상재코일 좌표로 간섭코일중 최대폭 CHECK
			 **************************************/
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getChkColWidInterfHNew
			WITH TEMP_PARAM AS(
			SELECT :V_STL_NO  AS P_STL_NO
			     , A.YD_STK_COL_GP
			     , A.YD_STK_BED_NO
			     , A.YD_STK_LYR_NO
			     , A.YD_STK_LYR_XAXIS AS P_LYR_XAXIS
			     , A.YD_STK_LYR_YAXIS AS P_LYR_YAXIS
			     , ROUND((SELECT COIL_W      FROM TB_PT_COILCOMM WHERE COIL_NO = :V_STL_NO )/2,0) AS P_COIL_W
			     , ROUND((SELECT COIL_OUTDIA FROM TB_PT_COILCOMM WHERE COIL_NO = :V_STL_NO )/2,0) AS P_COIL_ODIA
			  FROM TB_YD_STKLYR A
			 WHERE A.YD_STK_COL_GP = :V_YD_STK_COL_GP
			   AND A.YD_STK_BED_NO = :V_YD_STK_BED_NO
			   AND A.YD_STK_LYR_NO = :V_YD_STK_LYR_NO
			)
			, TEMP_COIL AS (
			SELECT CASE WHEN B.P_LYR_XAXIS > A.YD_STK_LYR_XAXIS THEN 'B'  --BEF
			            WHEN B.P_LYR_XAXIS < A.YD_STK_LYR_XAXIS THEN 'N'  --NEXT
			            ELSE 'D' END AS COIL_GP
			     , B.P_STL_NO
			     , B.P_COIL_W
			     , B.P_COIL_ODIA
			     , B.P_LYR_XAXIS
			     , B.P_LYR_YAXIS
			     , A.YD_STK_LYR_YAXIS - P_COIL_ODIA AS COIL_Y_MIN --최소BED Y계산(Y값-코일외경/2)
			     , A.YD_STK_LYR_YAXIS + P_COIL_ODIA AS COIL_Y_MAX --최대BED Y계산(Y값+코일외경/2)
			     , A.YD_STK_COL_GP
			     , A.YD_STK_BED_NO
			     , A.YD_STK_LYR_NO
			     , A.YD_STK_LYR_XAXIS
			     , A.YD_STK_LYR_YAXIS
			  FROM TB_YD_STKLYR A
			     , TEMP_PARAM   B
			 WHERE A.YD_STK_COL_GP LIKE SUBSTR(B.YD_STK_COL_GP,1,2) || '%'
			   AND A.YD_STK_BED_NO    = B.YD_STK_BED_NO
			   AND A.YD_STK_LYR_NO    = B.YD_STK_LYR_NO
			   AND SUBSTR(A.YD_STK_COL_GP,3,2) BETWEEN '01' AND '99'
			   --좌우열 검색
			   AND A.YD_STK_LYR_XAXIS BETWEEN B.P_LYR_XAXIS - 3000 AND B.P_LYR_XAXIS + 3000
			)
			, TEMP_BED_INFO AS (
			--해당 BED에 코일이 넣고 난뒤에 충돌가능 BED 검색
			SELECT B.COIL_GP
			     , B.P_COIL_W
			     , B.P_COIL_ODIA
			     , B.P_LYR_XAXIS
			     , B.P_LYR_YAXIS
			     , B.COIL_Y_MIN
			     , B.COIL_Y_MAX
			     , A.YD_STK_COL_GP
			     , A.YD_STK_BED_NO
			     , A.YD_STK_LYR_NO
			     , CASE WHEN B.COIL_GP = 'D' THEN B.P_STL_NO
			            ELSE A.STL_NO END AS STL_NO
			     , CASE WHEN B.COIL_GP = 'D' THEN B.P_COIL_W
			            ELSE ROUND((SELECT COIL_W      FROM TB_PT_COILCOMM WHERE COIL_NO = A.STL_NO)/2,0) END AS COL_H_COIL_W
			     , CASE WHEN B.COIL_GP = 'D' THEN B.P_COIL_ODIA
			            ELSE ROUND((SELECT COIL_OUTDIA FROM TB_PT_COILCOMM WHERE COIL_NO = A.STL_NO)/2,0) END AS COL_H_COIL_ODIA
			     , A.YD_STK_LYR_XAXIS
			     , A.YD_STK_LYR_YAXIS
			  FROM TB_YD_STKLYR A
			     , TEMP_COIL B
			 WHERE A.YD_STK_COL_GP  = B.YD_STK_COL_GP
			   AND A.YD_STK_BED_NO IN (LPAD(TO_NUMBER(B.YD_STK_BED_NO) - 1, 2, '0')
			                         , B.YD_STK_BED_NO
			                         , LPAD(TO_NUMBER(B.YD_STK_BED_NO) + 1, 2, '0'))
			   AND 'Y' = CASE WHEN B.COIL_GP = 'D' THEN
			                       CASE WHEN A.YD_STK_BED_NO = B.YD_STK_BED_NO AND A.YD_STK_LYR_NO = B.YD_STK_LYR_NO THEN 'Y'
			                            ELSE 'N' END
			                  WHEN A.YD_STK_BED_NO = LPAD(TO_NUMBER(B.YD_STK_BED_NO) + 1,2,'0')
			                   AND A.YD_STK_LYR_NO = '002' THEN 'N'
			                  ELSE 'Y'
			             END
			   --대상위치가 2단일 경우 2단만 검색
			   AND 'Y' = CASE WHEN :V_YD_STK_LYR_NO           = '001' THEN 'Y'
			                  ELSE CASE WHEN A.YD_STK_LYR_NO  = '002' THEN 'Y' ELSE 'N' END
			             END
			 ORDER BY A.YD_STK_COL_GP, A.YD_STK_BED_NO, A.YD_STK_LYR_NO
			)
			, TEMP_INFO AS (
			SELECT COIL_GP
			     , P_COIL_W
			     , P_COIL_ODIA
			     , P_LYR_XAXIS
			     , P_LYR_YAXIS
			     , COIL_Y_MIN
			     , COIL_Y_MAX
			     , YD_STK_COL_GP
			     , YD_STK_BED_NO
			     , YD_STK_LYR_NO
			     , STL_NO
			     , COL_H_COIL_W
			     , COL_H_COIL_ODIA
			     , YD_STK_LYR_XAXIS
			     , YD_STK_LYR_YAXIS
			     , YD_STK_LYR_YAXIS - COL_H_COIL_ODIA AS COL_Y_MIN
			     , YD_STK_LYR_YAXIS + COL_H_COIL_ODIA AS COL_Y_MAX
			     , NVL(MAX(COL_H_COIL_W) OVER (PARTITION BY COIL_GP ORDER BY COIL_GP ),0) AS COL_MAX_W
			  FROM TEMP_BED_INFO
			 WHERE YD_STK_LYR_YAXIS - COL_H_COIL_ODIA BETWEEN COIL_Y_MIN AND COIL_Y_MAX
			    OR YD_STK_LYR_YAXIS + COL_H_COIL_ODIA BETWEEN COIL_Y_MIN AND COIL_Y_MAX
			)
			SELECT CASE WHEN MIN(B_CHK) = 'N' THEN 'N'
			            ELSE 'Y' END ABLE_YN
			  FROM (
			        SELECT CASE WHEN (ABS(P_LYR_XAXIS - YD_STK_LYR_XAXIS) - COL_MAX_W - P_COIL_W) >= :V_CRANE_GAP THEN 'Y'
			                    ELSE 'N' END AS B_CHK
			             , ABS(YD_STK_LYR_XAXIS - P_LYR_XAXIS) - COL_MAX_W - P_COIL_W AS GAP
			             , A.*
			          FROM TEMP_INFO A
			         WHERE COIL_GP = 'B'
			           AND 'Y' = (SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END
			                        FROM TEMP_INFO
			                       WHERE COIL_GP = 'B')
			         UNION ALL
			        SELECT CASE WHEN (ABS(YD_STK_LYR_XAXIS - P_LYR_XAXIS) - COL_MAX_W - P_COIL_W) >= :V_CRANE_GAP THEN 'Y'
			                    ELSE 'N' END B_CHK
			             , ABS(YD_STK_LYR_XAXIS - P_LYR_XAXIS) - COL_MAX_W - P_COIL_W AS GAP
			             , A.*
			          FROM TEMP_INFO A
			         WHERE COIL_GP = 'N'
			           AND 'Y' = (SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END
			                        FROM TEMP_INFO
			                       WHERE COIL_GP = 'N')
			         UNION ALL
			        SELECT 'Y'
			             , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL
			             , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL
			          FROM DUAL
			       ) C
			*/
          	JDTORecordSet jsColWid = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getChkColWidInterfHNew", logId, mthdNm, "전후열 폭간섭 체크");
          	
          	if( jsColWid.size() == 0 ) {
          		rtnMsg = "     ▶ 전후열 폭간섭 체크에서 이상발생.";
          		commUtils.printLog(logId, rtnMsg, "SL");
          		return sRtnVal;
          	}
          	
          	String sAbleYn  = commUtils.trim(jsColWid.getRecord(0).getFieldString("ABLE_YN"));
          	if( "N".equals(sAbleYn) ) {
          		rtnMsg = "     ▶ 전후열 폭간섭으로 적치불가!!";
          		commUtils.printLog(logId, rtnMsg, "SL");
          		return sRtnVal;
          	}
          	
          	rtnMsg = "     chkColWidInterfH : SUCCESS";
      		commUtils.printLog(logId, rtnMsg, "SL");
			
			commUtils.printLog(logId, rtnMsg, "SL");
			
			commUtils.printLog(logId, "   "+ mthdNm, "S-");
			return sRtnVal = CConstant.RETN_CD_SUCCESS;

		} catch(Exception e) {
			rtnMsg = "   "+sStlNo+">>1단전후좌우폭간섭Check중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			return sRtnVal = CConstant.RETN_CD_FAILURE;
		}
		
	}



	/**
	 * [A] 오퍼레이션명 : 코일소재야드여재원인Check(searchCoilYdYeojaeCheck)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */                                     
	public String chk2Yeojae(String logId, String mthdNms, String sStlNo      , String sYeojaeCd
										                      , String sLeftStlNo  , String sLeftYeojaeCd
										                      , String sRightStlNo , String sRightYeojaeCd) throws JDTOException  {
		String mthdNm = "소재여재원인Check[CCoilSchSeEJB.chk2Yeojae] < " + mthdNms;
		String rtnMsg = "";
		String sRtnVal  = CConstant.RETN_CD_FAILURE;
		
		try {
			commUtils.printLog(logId,"   "+  mthdNm, "S+");
	    	
	    	if ( "3G".equals(sLeftYeojaeCd) || "3G".equals(sRightYeojaeCd)) {
	           	sRtnVal = CConstant.RETN_CD_FAILURE;
	    		commUtils.printLog(logId, "   2열연 짱구코일 여재구분실패(본,LEFT,RIGHT)=>>"+sYeojaeCd+"-"+sLeftYeojaeCd+"-"+sRightYeojaeCd, "SL");
	    	} else {
	    		sRtnVal = CConstant.RETN_CD_SUCCESS;
	    		commUtils.printLog(logId, "   2열연 짱구코일 여재구분성공(본,LEFT,RIGHT)=>>"+sYeojaeCd+"-"+sLeftYeojaeCd+"-"+sRightYeojaeCd, "SL");
	    	}
	    	commUtils.printLog(logId, "   "+ mthdNm, "S-");
	    	return sRtnVal;
	
		} catch(Exception e) {
			rtnMsg = "   여재원인Check 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			return sRtnVal = CConstant.RETN_CD_FAILURE;
		}
	}
	
	/**
     * 오퍼레이션명 : 스케쥴 로그여부
     *  
     * @param   vResult, msgRecord
     * @return  intRtnVal
     * @throws  JDTOException
     */
    public String procSchLogYN(String logId, String mthdNms ,String schLogYn ,String stlNo ,String ydCrnSchId , String ydSchCd ,String schLogContents) throws JDTOException {
   		String mthdNm = "스케쥴 로그여부[CCoilSchSeEJB.procSchLogYN] < " + mthdNms;
    	String sRtnVal = "0";
		
		try{
			
			commUtils.printLog(logId, mthdNm, "S+");
		
			JDTORecord jrLog  				= commUtils.getParam(logId, mthdNm, ""); 
			jrLog.setField("STL_NO"			, stlNo);
			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
			jrLog.setField("YD_GP"			, "J");
			jrLog.setField("YD_SCH_CD"		, ydSchCd);
			jrLog.setField("SCH_CONTENTS"	, schLogContents );
			
			EJBConnector SchLog = new EJBConnector("default", "CCoilSchSeEJB", this);
			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });

			commUtils.printLog(logId, mthdNm, "S-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
		return sRtnVal ;
        
    }
			
	
	/**
	 * 신기본 BASE CHECK
	 * 화면권하위치변경 변경 에서 사용(CoilLyrBaseCheck,CoilGdsLyrBaseCheck,CoilGdsLyrBaseCheckABC,CoilGdsLyrBaseCheckYardWrap)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord procStlNoBaseCheck(String logId, String mthdNms, JDTORecord jrLayer) throws JDTOException {
    	String mthdNm = "화면권하위치변경시 적치가능 CHECK[CCoilSchSeEJB.procStlNoBaseCheck] < " + mthdNms;
    	JDTORecord  jrRtn   = JDTORecordFactory.getInstance().create();	
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");

			String sStlNo	   	= commUtils.trim(jrLayer.getFieldString("STL_NO"));			//크레인작업재료
			String ydSchCd      = commUtils.trim(jrLayer.getFieldString("YD_SCH_CD"));		    //스케쥴 코드
			String ydCrnSchId   = commUtils.trim(jrLayer.getFieldString("YD_CRN_SCH_ID"));		//스케쥴 ID
			String ydStkColGp  	= commUtils.trim(jrLayer.getFieldString("YD_STK_COL_GP"));		//열
			String ydStkBedNo   = commUtils.trim(jrLayer.getFieldString("YD_STK_BED_NO"));		//BED
			String ydStkLyrNo   = commUtils.trim(jrLayer.getFieldString("YD_STK_LYR_NO"));		//단 
			String sRtnBedDan 	= "";  //TO위치	
	    	String sLogMsg		= "";
	    	String ydEqpId      = commUtils.trim(jrLayer.getFieldString("YD_EQP_ID"));
			
			if ("".equals(ydStkColGp)) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			}
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("STL_NO"			, sStlNo);			
			jrParam.setField("YD_SCH_CD"		, ydSchCd);			
			jrParam.setField("YD_STK_COL_GP"	, ydStkColGp);		
			jrParam.setField("YD_STK_BED_NO"	, ydStkBedNo);		
			jrParam.setField("YD_STK_LYR_NO"	, ydStkLyrNo);	
			jrParam.setField("YD_TO_LOC_GUIDE"	, ydStkColGp+ydStkBedNo);	
			jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);	
			
			sLogMsg =  "재료["+sStlNo +" +스케쥴 코드 : "+ ydSchCd + "열:"+ ydStkColGp + "베드:"+ydStkBedNo+ "단:" + ydStkLyrNo + "의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, sLogMsg, "SL");
 
			commUtils.printLog(logId, "YD_CRN_SCH_ID:" + ydCrnSchId, "SL");

			if("".equals(ydEqpId)) {	
			  if(!"".equals(ydCrnSchId)) {
				//야드스케쥴 조회
				/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCrnsch 
				SELECT YD_EQP_ID                        AS YD_EQP_ID
				     , YD_CRN_SCH_ID                    AS YD_CRN_SCH_ID
				             :
				     , YD_L2_REQUEST_STAT               AS YD_L2_REQUEST_STAT
				     , UP_ROTATION_ANGLE                AS UP_ROTATION_ANGLE
				     , DOWN_ROTATION_ANGLE              AS DOWN_ROTATION_ANGLE
				     , (CASE WHEN YD_DN_WO_LOC LIKE 'JH%' 
				              AND YD_EQP_ID='JHCRH2'
				              AND SUBSTR(YD_DN_WO_LOC,3,2) BETWEEN '00' AND '99' 
				              AND SUBSTR(YD_DN_WO_LOC,7,2) ='01' 
				              AND YD_DN_WO_LAYER = '001' 
				              AND YD_TO_LOC_DCSN_MTD ='S'  --주작업인 경우
				            THEN 'Y' ELSE 'N' END)    AS HDONG_YN
				     , YD_WRK_PROG_STAT AS YD_WRK_PROG_STAT_CD
				  FROM TB_YD_CRNSCH
				 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				 
				*/	   
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCrnsch", logId, mthdNm, "크레인스케쥴 조회"); 
				
				if (jsChk.size() > 0) {
					ydEqpId  = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"));
				}
								
			  } else {
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdSchRuleWrkCrn 
				SELECT CASE WHEN WRK_EQUIP_STAT != 'B' THEN YD_WRK_CRN
						    WHEN WRK_EQUIP_STAT  = 'B' AND YD_ALT_CRN_YN = 'Y' THEN 
				                  CASE WHEN ALT_EQUIP_STAT != 'B' THEN YD_ALT_CRN ELSE YD_WRK_CRN END
						    ELSE YD_WRK_CRN 
				       END AS YD_EQP_ID     
				  FROM (
				        SELECT SR.YD_SCH_CD
				             , SR.YD_WRK_CRN
				             , SR.YD_ALT_CRN_YN
				             , SR.YD_ALT_CRN
				             , SR.YD_ALT_CRN_PRIOR
				             , SR.CD_CONTENTS       
				             , SR.YD_SCH_PROH_EXN   
				             , (SELECT YD_EQP_STAT FROM TB_YD_EQP WHERE YD_EQP_ID = SR.YD_WRK_CRN) AS WRK_EQUIP_STAT
				             , (SELECT YD_EQP_STAT FROM TB_YD_EQP WHERE YD_EQP_ID = SR.YD_ALT_CRN) AS ALT_EQUIP_STAT
				          FROM TB_YD_SCHRULE   SR
				         WHERE SR.YD_SCH_CD = :V_YD_SCH_CD
				       )  
				*/	   
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdSchRuleWrkCrn", logId, mthdNm, "야드스케쥴 조회"); 
				
				if (jsChk.size() > 0) {
					ydEqpId  = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"));
				}
				
			  }
			}
			jrParam.setField("YD_EQP_ID"	, ydEqpId);	 
			
			commUtils.printLog(logId, "procStlNoBaseCheck:YD_EQP_ID:" + ydEqpId, "SL");
	    	//----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------

			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocUser
			 -- 대상 위치 SELECT
			WITH TO_LOC_TABLE AS (
			SELECT X1.YD_SCH_CD
			     , SL.YD_STK_COL_GP         AS TAG_YD_STK_COL_GP
			     , SL.YD_STK_BED_NO         AS TAG_YD_STK_BED_NO
			     , SL.YD_STK_LYR_NO         AS TAG_YD_STK_LYR_NO
			     , SL.YD_STK_LYR_MTL_STAT   AS TAG_YD_STK_LYR_MTL_STAT
			     -- 열에 따른 코일 간격
			     , SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP)  AS INT
			     , DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
			                                '002', SL.YD_STK_BED_NO)                               AS TAG_LEFT_BED

			     , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_LYR_NO,
			                                '002', LPAD(TO_NUMBER(SL.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_LEFT_LAYER

			     , DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
			                                '002', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'))
			                                                                                       AS TAG_RIGHT_BED
			     , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_LYR_NO,
			                                '002', LPAD(TO_NUMBER(SL.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_RIGHT_LAYER
			     , CASE WHEN SUBSTR(SL.YD_STK_COL_GP,3,2) IN ('01','80') THEN C1.COIL_OUTDIA_GRP_GP
			            ELSE SC.COL_OUTDIA_GRP_GP END COL_OUTDIA_GRP_GP
			     , CASE WHEN SUBSTR(SL.YD_STK_COL_GP,3,2) IN ('01','80') THEN C1.COIL_W_GP
			            ELSE SC.COL_W_GP END COL_W_GP            

			     , SC.COL_YD_LOC_GP
			     , SC.COL_YD_STK_SKID_GP
			     , C1.STL_NO
			     , C1.COIL_W
			     , C1.COIL_OUTDIA_GRP_GP
			     , C1.COIL_W_GP
			     , (CASE WHEN SL.YD_STK_COL_GP LIKE '__TC%' AND SL.YD_STK_BED_NO='01' THEN 1
			             WHEN SL.YD_STK_COL_GP LIKE '__TC%' AND SL.YD_STK_BED_NO='02' THEN 3
			             WHEN SL.YD_STK_COL_GP LIKE '__TC%' AND SL.YD_STK_BED_NO='03' THEN 2
			             ELSE 0  END)       AS SORT_TC
			     -- 적치가능여부 CHECK시 필요 :H/J/ABC/WRAP
			     , CASE WHEN SUBSTR(X1.YD_SCH_CD,8,1) = 'H' THEN 'H'
			            ELSE 'J' END  AS CHK_SKID_GP
			     , SC.YD_STK_COL_TOLOC_STAT       
			  FROM TB_YD_STKLYR SL
			     , ( SELECT YD_STK_COL_GP
			              , YD_GP
			              , YD_BAY_GP
			              --SKID 외경구분
			              , YD_COIL_OUTDIA_GRP_GP  AS COL_OUTDIA_GRP_GP
			              , YD_STK_SKID_GP         AS COL_YD_STK_SKID_GP
			              , DEL_YN
			              , YD_LOC_GP       AS COL_YD_LOC_GP
			              , YD_STK_COL_W_GP AS COL_W_GP
			              , YD_STK_COL_TOLOC_STAT
			           FROM TB_YD_STKCOL
			          WHERE DEL_YN = 'N'
			            AND YD_GP  = 'J'
			       ) SC
			     , (
			         SELECT CC.COIL_NO                    AS STL_NO
			              , CASE WHEN CC.COIL_OUTDIA <= 1280 THEN 'A'
			                     WHEN CC.COIL_OUTDIA >  1930 THEN 'C'
			                     ELSE 'B' END             AS COIL_OUTDIA_GRP_GP
			              , CC.COIL_W
			              , CASE WHEN CC.COIL_W < 1601 THEN 'M'
			                     ELSE 'L' END             AS COIL_W_GP
			           FROM USRPTA.TB_PT_COILCOMM CC
			          WHERE CC.COIL_NO = :V_STL_NO
			            AND ROWNUM = 1
			       ) C1
			     , (
			         SELECT :V_YD_SCH_CD        AS YD_SCH_CD
			              , :V_YD_TO_LOC_GUIDE  AS YD_TO_LOC_GUIDE
			           FROM DUAL
			       ) X1
			     , (SELECT ITEM1 AS TOT_YN FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP002' AND DEL_YN = 'N' AND ROWNUM = 1) YR
			 WHERE SL.YD_STK_COL_GP       = SC.YD_STK_COL_GP
			   AND SL.YD_STK_COL_GP       LIKE SUBSTR(X1.YD_TO_LOC_GUIDE,1,6) || '%' --가이드 열
			   AND SL.YD_STK_BED_NO       LIKE SUBSTR(X1.YD_TO_LOC_GUIDE,7,2) || '%' --가이드 BED
			   AND SL.YD_STK_LYR_NO       IN ('001','002')
			   AND SL.YD_STK_LYR_ACT_STAT = 'E'
			   AND SL.YD_STK_LYR_MTL_STAT = 'E'
			   AND SL.DEL_YN = 'N'
			   AND SC.DEL_YN = 'N'
			   AND SL.YD_STK_BED_NO BETWEEN '00' AND '99'
			   --****  야드 통합여부  *****
			   AND 'Y' = CASE WHEN YR.TOT_YN = 'N' THEN
			                       CASE WHEN SC.COL_YD_LOC_GP = DECODE(SUBSTR(YD_SCH_CD,8,1),'H','H','J') THEN 'Y'
			                            WHEN SC.COL_YD_LOC_GP = 'H' AND YD_SCH_CD LIKE 'J_PT_3_M'         THEN 'Y' -- 공냉재 이송은 소재
			                            ELSE 'N'
			                       END
			                  ELSE 'Y' END
			   -- 제품에 TO위치 기준 적용
			   AND 'Y' = CASE WHEN SC.COL_YD_LOC_GP = 'H'         THEN 'Y'
			                  WHEN SUBSTR(SC.YD_STK_COL_GP,3,2) IN ('01','80') then 'Y'
			                  WHEN SC.YD_STK_COL_TOLOC_STAT = 'Y' THEN 'Y' 
			                  ELSE 'N' END
			   -- 적치폭 구분 CHECK(소재위치)
			   AND 'Y' = CASE WHEN SC.COL_YD_LOC_GP = 'H'  THEN 'Y'
			                  WHEN SC.COL_YD_LOC_GP = 'J'  AND SUBSTR(SC.YD_STK_COL_GP,3,2) IN ('01','80')  THEN 'Y' --제품가상스판은 체크안함
			                  WHEN SC.COL_YD_LOC_GP = 'J'
			                       AND SUBSTR(SC.YD_STK_COL_GP,3,1) IN ('0','1','2','3','4','5','6')
			                       AND SC.COL_W_GP = C1.COIL_W_GP
			                       THEN 'Y'
			                  ELSE 'N' END
			   -- 설비 작업대상재 제외
			   -- 크레인 작업지시 제외
			   -- AND SL.YD_STK_COL_GP||SL.YD_STK_BED_NO||SL.YD_STK_LYR_NO NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YD_CRNSCH WHERE DEL_YN = 'N')
			   -- 작업예약 TO위치 가이드 제외
			   AND SL.YD_STK_COL_GP||SL.YD_STK_BED_NO NOT IN ( SELECT NVL(YD_TO_LOC_GUIDE,'1') FROM TB_YD_WRKBOOK WHERE DEL_YN = 'N' AND YD_TO_LOC_GUIDE <> X1.YD_TO_LOC_GUIDE)

			 )
			, TO_LOC_DATA_TABLE AS (
			-- TO위치 코일정보 SELECT
			SELECT A.STL_NO
			     , A.YD_SCH_CD
			--     , NVL((SELECT 'A' AS YD_EQP_WRK_MODE2 FROM TB_YD_EQP WHERE YD_EQP_ID = :V_YD_EQP_ID),'A') AS YD_EQP_WRK_MODE2
			     , (SELECT DECODE(ITEM1, 'Y', 'A', 'M') AS YD_EQP_WRK_MODE2 FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP100' AND CD_GP = :V_YD_EQP_ID) AS YD_EQP_WRK_MODE2
			     , A.TAG_YD_STK_COL_GP
			     , A.TAG_YD_STK_BED_NO
			     , A.TAG_YD_STK_LYR_NO
			     , A.TAG_LEFT_BED
			     , A.TAG_LEFT_LAYER
			     , A.SORT_TC
			     , A.COIL_OUTDIA_GRP_GP
			     , A.COL_OUTDIA_GRP_GP
			     , A.COL_YD_LOC_GP
			     , A.COL_W_GP
			     , A.COIL_W_GP
			     , A.CHK_SKID_GP
			     , A.COL_YD_STK_SKID_GP
			     , A.YD_STK_COL_TOLOC_STAT  
			     , (SELECT YD_STK_LYR_ACT_STAT
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
			           AND B.YD_STK_LYR_NO   = A.TAG_LEFT_LAYER
			           AND B.DEL_YN = 'N' ) AS TAG_LEFT_ACTIVE_STAT
			     , (SELECT YD_STK_LYR_MTL_STAT
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
			           AND B.YD_STK_LYR_NO   = A.TAG_LEFT_LAYER
			           AND B.DEL_YN = 'N' ) AS TAG_LEFT_LAYER_STAT
			     , (SELECT STL_NO
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
			           AND B.YD_STK_LYR_NO   = A.TAG_LEFT_LAYER
			           AND B.DEL_YN = 'N' ) AS TAG_LEFT_STL_NO
			     , A.TAG_RIGHT_BED
			     , A.TAG_RIGHT_LAYER
			     , (SELECT YD_STK_LYR_ACT_STAT
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO   = A.TAG_RIGHT_BED
			           AND B.YD_STK_LYR_NO   = A.TAG_RIGHT_LAYER
			           AND B.DEL_YN = 'N' ) AS TAG_RIGHT_ACTIVE_STAT
			     , (SELECT YD_STK_LYR_MTL_STAT
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO   = A.TAG_RIGHT_BED
			           AND B.YD_STK_LYR_NO   = A.TAG_RIGHT_LAYER
			           AND B.DEL_YN = 'N' ) AS TAG_RIGHT_LAYER_STAT
			     , (SELECT STL_NO
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO   = A.TAG_RIGHT_BED
			           AND B.YD_STK_LYR_NO   = A.TAG_RIGHT_LAYER
			           AND B.DEL_YN = 'N' ) AS  TAG_RIGHT_STL_NO
			     , (SELECT STL_NO
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
			           AND B.YD_STK_LYR_NO   = '002'
			           AND B.DEL_YN = 'N' ) AS TAG_2DAN_LEFT_STL_NO
			     , (SELECT STL_NO
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO   = A.TAG_YD_STK_BED_NO
			           AND B.YD_STK_LYR_NO   = '002'
			           AND B.DEL_YN = 'N' ) AS TAG_2DAN_RIGHT_STL_NO
			  FROM TO_LOC_TABLE A
			)
			, TO_LOC_DATA_COMP_TABLE AS (
			--*--*--*--*--*--*-- 적치가능위치
			SELECT *
			  FROM (
			        -- 배차 편성 4대분 제외
			        SELECT CASE WHEN ( SELECT COUNT(*)
			                             FROM (
			                                    SELECT ROW_NUMBER() OVER(PARTITION BY CS.YD_CARLD_STOP_LOC ORDER BY CS.YD_BAYIN_WO_SEQ, CS.YD_CAR_SCH_ID) AS RN
			                                         , CS.*
			                                      FROM TB_YD_CARSCH CS
			                                     WHERE CS.DEL_YN = 'N'
			                                       AND CS.YD_CARLD_STOP_LOC  LIKE 'J%'
			                                   ) CS
			                                , TB_YD_STOCK    ST
			                            WHERE CS.RN < 5
			                              AND CS.YD_CARLD_STOP_LOC  LIKE 'J'|| YD_SCH_CD|| '%'
			                              AND CS.TRANS_ORD_DATE  = ST.TRANS_ORD_DATE
			                              AND CS.TRANS_ORD_SEQNO = ST.TRANS_ORD_SEQNO 
			                              AND ST.STL_NO = NVL(L_COIL_NO,'-') 
			                              AND ROWNUM <= 1
			                         ) > 0  AND L_PROG_CD IN ('6','L') AND TAG_YD_STK_LYR_NO = '002' THEN 1 ELSE 0 END 
			                      AS L_CAR_YN        
			             , CASE WHEN ( SELECT COUNT(*)
			                             FROM (
			                                    SELECT ROW_NUMBER() OVER(PARTITION BY CS.YD_CARLD_STOP_LOC ORDER BY CS.YD_BAYIN_WO_SEQ, CS.YD_CAR_SCH_ID) AS RN
			                                         , CS.*
			                                      FROM TB_YD_CARSCH CS
			                                     WHERE CS.DEL_YN = 'N'
			                                       AND CS.YD_CARLD_STOP_LOC  LIKE 'J%'
			                                   ) CS
			                                , TB_YD_STOCK    ST
			                            WHERE CS.RN < 5
			                              AND CS.YD_CARLD_STOP_LOC  LIKE 'J'|| YD_SCH_CD|| '%'
			                              AND CS.TRANS_ORD_DATE  = ST.TRANS_ORD_DATE
			                              AND CS.TRANS_ORD_SEQNO = ST.TRANS_ORD_SEQNO 
			                              AND ST.STL_NO = NVL(R_COIL_NO,'-') 
			                              AND ROWNUM <= 1
			                         ) > 0  AND R_PROG_CD IN ('6','L') AND TAG_YD_STK_LYR_NO = '002' THEN 1 ELSE 0 END 
			                      AS R_CAR_YN 
			             , CASE WHEN (  SELECT COUNT(*)
			                              FROM TB_YD_STOCK A
			                                 , TB_YD_CARSCH B
			                             WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
			                               AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
			                               AND B.DEL_YN = 'N'
			                               AND A.STL_NO = NVL(L_COIL_NO,'-')
			                               AND ROWNUM <= 1
			                         ) > 0 AND TAG_YD_STK_LYR_NO = '002' THEN 1 ELSE 0 END  
			                      AS L_CAR_CHK
			             , CASE WHEN (  SELECT COUNT(*)
			                              FROM TB_YD_STOCK A
			                                 , TB_YD_CARSCH B
			                             WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
			                               AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
			                               AND B.DEL_YN = 'N'
			                               AND A.STL_NO = NVL(R_COIL_NO,'-')
			                               AND ROWNUM <= 1
			                         ) > 0 AND TAG_YD_STK_LYR_NO = '002' THEN 1 ELSE 0 END  
			                      AS R_CAR_CHK                    
			             -- 1단우선적치 B군(폭1200이상,폭1600미만,외경1800이상,외경1930미만) 
			             --           C군(폭1200이상,폭1600미만,외경1990이상) 서주임님
			             , CASE WHEN SUBSTR(TAG_YD_STK_COL_GP,1,2) IN ('JB') AND SUBSTR(TAG_YD_STK_COL_GP,3,2) BETWEEN '02' AND '14'  AND TAG_YD_STK_LYR_NO = '001' THEN  
			                         CASE WHEN C_COIL_OUTDIA_GP = 'B' AND C_WIDTH >= 1200 AND C_WIDTH  <= 1600 AND C_OUTDIA >= 1800 AND C_OUTDIA <= 1930 THEN '1'
			                              WHEN C_COIL_OUTDIA_GP = 'C' AND C_WIDTH >= 1200 AND C_WIDTH  <= 1600 AND C_OUTDIA >= 1990                      THEN '1'
			                              ELSE '2' END
			                    WHEN SUBSTR(TAG_YD_STK_COL_GP,1,2) IN ('JC') AND SUBSTR(TAG_YD_STK_COL_GP,3,2) BETWEEN '02' AND '15'  AND TAG_YD_STK_LYR_NO = '001' THEN  
			                         CASE WHEN C_COIL_OUTDIA_GP = 'B' AND C_WIDTH >= 1200 AND C_WIDTH  <= 1600 AND C_OUTDIA >= 1800 AND C_OUTDIA <= 1930 THEN '1'
			                              WHEN C_COIL_OUTDIA_GP = 'C' AND C_WIDTH >= 1200 AND C_WIDTH  <= 1600 AND C_OUTDIA >= 1990                      THEN '1'
			                              ELSE '2' END
			                    ELSE '2'          
			                END AS ABOVE_LYR
			             , KK.*
			         --    , ROW_NUMBER() OVER(PARTITION BY PRIOR4 ORDER BY PRIOR1,PRIOR2,PRIOR3 DESC ) GROUP_ROW
			          FROM
			               (
			                SELECT K.*
			                     , C.COIL_NO      AS C_COIL_NO
			                     , C.COIL_T       AS C_THICK
			                     , C.COIL_W       AS C_WIDTH
			                     , C.COIL_WT      AS C_WEIGTH
			                     , C.COIL_OUTDIA  AS C_OUTDIA
			                     , C.CURR_PROG_CD AS C_PROG_CD
			                     , C.ORD_NO       AS C_ORD_NO     -- 주문번호
			                     , C.ORD_DTL      AS C_ORD_DTL    -- 주문행번
			                     , C.DEMANDER_CD  AS C_DEMANDER_CD-- 수요가코드
			                     , (CASE WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
			                             WHEN C.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN C.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN C.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN C.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN C.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             ELSE '' END
			                       ) AS C_YEOJAE_CAUSE_CD
			                     , TRUNC((SYSDATE - C.HRMILL_CMPL_DT )*24)             AS C_HOT_COIL_MIN
			                     , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
			                            WHEN C.COIL_OUTDIA >  1930 THEN 'C'
			                            ELSE 'B' END  AS C_COIL_OUTDIA_GP
			                     , CASE WHEN C.COIL_W < 1601     THEN 'M' ELSE 'L' END AS C_COIL_W_GP
			                     , L.COIL_NO      AS L_COIL_NO
			                     , L.COIL_T       AS L_THICK
			                     , L.COIL_W       AS L_WIDTH
			                     , L.COIL_WT      AS L_WEIGTH
			                     , L.COIL_OUTDIA  AS L_OUTDIA
			                     , L.CURR_PROG_CD AS L_PROG_CD
			                     , L.ORD_NO       AS L_ORD_NO     -- 주문번호
			                     , L.ORD_DTL      AS L_ORD_DTL    -- 주문행번
			                     , L.DEMANDER_CD  AS L_DEMANDER_CD-- 수요가코드
			                     , (CASE WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
			                             WHEN L.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN L.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN L.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN L.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN L.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             ELSE '' END
			                       ) AS L_YEOJAE_CAUSE_CD
			                     , TRUNC((SYSDATE - L.HRMILL_CMPL_DT )*24) AS L_HOT_COIL_MIN
			                     , R.COIL_NO      AS R_COIL_NO
			                     , R.COIL_W       AS R_WIDTH
			                     , R.COIL_T       AS R_THICK
			                     , R.COIL_WT      AS R_WEIGTH
			                     , R.COIL_OUTDIA  AS R_OUTDIA
			                     , R.CURR_PROG_CD AS R_PROG_CD
			                     , R.ORD_NO       AS R_ORD_NO     -- 주문번호
			                     , R.ORD_DTL      AS R_ORD_DTL    -- 주문행번
			                     , R.DEMANDER_CD  AS R_DEMANDER_CD-- 수요가코드
			                     , (CASE WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
			                             WHEN R.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN R.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN R.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN R.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN R.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             ELSE '' END
			                       ) AS R_YEOJAE_CAUSE_CD
			                     , TRUNC((SYSDATE - R.HRMILL_CMPL_DT )*24) R_HOT_COIL_MIN
			                     , L.ISHOT        AS L_ISHOT
			                     , R.ISHOT        AS R_ISHOT
			                     , C.ISHOT        AS C_ISHOT
			                  FROM TO_LOC_DATA_TABLE K
			                     , (SELECT 1 T_ROW, A.*
			                             ,    CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 96
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 48
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                  END  AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
			                          FROM USRPTA.TB_PT_COILCOMM  A ) C  --대상코일
			                     , (SELECT 1 T_ROW, A.*
			                             , CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 96
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 48
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                  END  AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
			                          FROM USRPTA.TB_PT_COILCOMM  A ) L  --하단LEFT
			                     , (SELECT 1 T_ROW, A.*
			                             , CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
			                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
			                                                    THEN 'TRUE'
			                                                    ELSE 'FALSE'
			                                               END
			                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
			                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 96
			                                                    THEN 'TRUE'
			                                                    ELSE 'FALSE'
			                                               END
			                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
			                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 72
			                                                    THEN 'TRUE'
			                                                    ELSE 'FALSE'
			                                               END
			                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
			                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) < 48
			                                                    THEN 'TRUE'
			                                                    ELSE 'FALSE'
			                                               END
			                                          END  AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
			                          FROM USRPTA.TB_PT_COILCOMM  A ) R  --하단RIGHT
			                 WHERE K.STL_NO           = C.COIL_NO(+)
			                   AND K.TAG_LEFT_STL_NO  = L.COIL_NO(+)
			                   AND K.TAG_RIGHT_STL_NO = R.COIL_NO(+)
			                   --2단일 경우 1단에 핫코일이 존재 시 2단제의
			                   AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND C.ISHOT ='TRUE' THEN CASE WHEN L.ISHOT ='TRUE' AND R.ISHOT='TRUE' THEN 1
			                                                                                             ELSE 0 END    
			                                ELSE 1 END
			                   --2단일 경우 좌우 적치 상태 CHECK
			                   AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002'
			                                     AND (TAG_LEFT_ACTIVE_STAT != 'E' OR TAG_RIGHT_ACTIVE_STAT != 'E'
			                                       OR TAG_LEFT_LAYER_STAT  != 'C' OR TAG_RIGHT_LAYER_STAT  != 'C') THEN 0
			                                ELSE 1 END
			                   --1단일 경우 좌우 상단 적치 상태 CHECK
			                   AND 1 = CASE WHEN K.TAG_YD_STK_LYR_NO = '001' AND (TAG_2DAN_LEFT_STL_NO IS NOT NULL OR TAG_2DAN_RIGHT_STL_NO IS NOT NULL) THEN 0
			                                ELSE 1 END

			               ) KK

			       )
			-- 황주임님 하단에 'L','6'제외요청        
			-- WHERE L_CAR_YN + R_CAR_YN = 0 
			 WHERE 'Y' = CASE WHEN YD_SCH_CD LIKE 'J_PT_3_M' THEN 'Y'
			                  WHEN L_CAR_YN + R_CAR_YN = 0   THEN 'Y'
			                  ELSE 'N'
			             END
			)
			--*--*--*--*--*--*-- 평점
			SELECT G.*
			     , CASE  -- 1단이면서 소재인 경우
			            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
			            THEN CASE WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌우) 진도코드
			                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌단) 진도코드
			                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우단) 진도코드
			                      WHEN L_COIL_NO  IS NULL     AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
			                      WHEN R_COIL_NO  IS NULL     AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED
			                      ELSE '7' END
			             -- 2단이면서 소재인 경우
			            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
			            THEN CASE --WHEN L_JJANG_GP  = 'Y'      OR  R_JJANG_GP = 'Y'      THEN '9' --짱구 위에서 못 올린다.
			                      WHEN L_WB        = 'Y'      OR  R_WB       = 'Y'      THEN '9' --좌하단우하단 작업 예약
			                      WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌하단우하단) 진도코드
			                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌하단) 진도코드
			                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우하단) 진도코드
			                      ELSE '2' END
			            -- 1단이면서 제품인 경우
			            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8')
			            THEN CASE WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
			                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
			                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
			                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우축 동일 고객사+주문번호행번

			                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
			                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우측 동일 주문번호행번

			                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌측   동일 주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우측   동일 주문번호행번
			                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우측 동일 주문번호
			                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우측   동일 주문번호
			                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌측   동일 주문번호
			                      WHEN L_COIL_NO IS NULL      AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
			                      WHEN R_COIL_NO IS NULL      AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED

			                      ELSE '7' END
			            -- 2단이면서 제품인 경우
			            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8') --제품
			            THEN CASE --WHEN L_JJANG_GP = 'Y'       OR  R_JJANG_GP = 'Y'      THEN '9' -- 짱구 위에서 못 올린다.
			                      WHEN L_WB = 'Y'             OR  R_WB  = 'Y'           THEN '9' --좌하단우하단 작업 예약
			                      WHEN L_PROG_CD IN ('6','L') OR  R_PROG_CD IN ('6','L')THEN '8' --상차지시
			                      WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
			                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
			                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
			                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 고객사+주문번호행번

			                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
			                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 주문번호행번

			                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌하단   동일 주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우하단   동일 주문번호행번
			                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우하단 동일 주문번호
			                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우하단   동일 주문번호
			                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌하단   동일 주문번호
			                      ELSE '2' END

			             ELSE '7' END GRADE
			  FROM (SELECT A.*
			             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
			                  FROM TB_YD_WRKBOOK    A1
			                     , TB_YD_WRKBOOKMTL B1
			                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			                   AND A1.DEL_YN      = 'N'
			                   AND B1.DEL_YN      = 'N'
			                   AND B1.STL_NO      = A.C_COIL_NO
			               ) C_WB
			             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
			                  FROM TB_YD_WRKBOOK    A1
			                     , TB_YD_WRKBOOKMTL B1
			                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			                   AND A1.DEL_YN      = 'N'
			                   AND B1.DEL_YN      = 'N'
			                   AND B1.STL_NO      = A.L_COIL_NO
			               ) L_WB
			             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
			                  FROM TB_YD_WRKBOOK    A1
			                     , TB_YD_WRKBOOKMTL B1
			                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			                   AND A1.DEL_YN      = 'N'
			                   AND B1.DEL_YN      = 'N'
			                   AND B1.STL_NO      = A.R_COIL_NO
			               ) R_WB
			          FROM TO_LOC_DATA_COMP_TABLE A
			       ) G
			 ORDER BY GRADE
			        , L_CAR_CHK + R_CAR_CHK
			*/
			
			JDTORecordSet jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocUser", logId, mthdNm, "화면TO위치 변경 베드 조회");
			if (jsResult.size() <= 0) {
				sLogMsg = "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			}
	
			String sSchLogContents	= "";
			
			//----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 SCH RULL 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord  jrRuleConst = this.toLocRuleConst(logId, mthdNm);
			
			//----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 LOGIC 적용여부 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrLocRuleApply = this.toLocRuleApply(logId, mthdNm);
			commUtils.printParam(logId, jrLocRuleApply);
			
		 // 적치 가능 check
			JDTORecord jrAbleResult	= JDTORecordFactory.getInstance().create();
			JDTORecord jrLocAbleRtn = JDTORecordFactory.getInstance().create();
			String LocAbleRtn 	 = "";
			String LocAbleRtnMsg = "";
			String sLocAbleRtnChk = "";
			String sGrade 		 = "9";
			String sSchChk 		 = "";	
			String sWbChk 		 = "";
			String sHotChk 	     = "";		
			for (int i = 1; i <= jsResult.size(); i++) {

				jsResult.absolute(i);
				jrAbleResult  = jsResult.getRecord();
				
				sGrade 		= commUtils.nvl (jrAbleResult.getFieldString("GRADE"),"9");
				ydStkColGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_YD_STK_COL_GP"  ));
				ydStkBedNo 	= commUtils.trim(jrAbleResult.getFieldString("TAG_YD_STK_BED_NO"  ));
				ydStkLyrNo 	= commUtils.trim(jrAbleResult.getFieldString("TAG_YD_STK_LYR_NO"  ));	

				sSchChk 	= commUtils.trim(jrAbleResult.getFieldString("SCH_CHK"  ));	
				sWbChk 		= commUtils.trim(jrAbleResult.getFieldString("WB_CHK"  ));
				sHotChk 	= commUtils.trim(jrAbleResult.getFieldString("HOT_CHK"  ));
	
				//권하위치가 스케쥴에 편성된 위치입니다.
				if ("Y".equals(sSchChk)) {
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", "권하위치가 크레인스케쥴에 편성된 위치입니다.");
					return jrRtn;
				}	
					
				//권하위치가 TO위치 가이드에  편성된 위치입니다.
				if ("Y".equals(sWbChk)) {
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", "권하위치가 작업예약 TO위치가이드에  편성된 위치입니다.");
					return jrRtn;
				}	
                //2단일 경우 1단에 핫코일이 존재여부 
				if ("Y".equals(sHotChk)) {
					jrRtn.setField("RTN_CD" , "1단에 핫코일이 존재 합니다.");	
					jrRtn.setField("RTN_MSG", sLogMsg);
					return jrRtn;
				}	

				commUtils.printParam(logId, jrAbleResult);
				/**************************************
				 *         적치 가능 check
		   		 **************************************/
				
				/*
				 * AT0000_HOT코일 체크 안함 수입 권하위치선정 일때  YYS 202202
				 * */
				String sAPP004_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "004");			    
				if("Y".equals(sAPP004_YN)){
					jrRuleConst.setField("BASE_CHEK" , "Y");// HOT COIL CHECK >> 적용	
				}else{
					jrRuleConst.setField("BASE_CHEK" , "N");// HOT COIL CHECK >> 적용하지 않음	
				}
				
				jrLocAbleRtn = this.toLocAbleCheck(logId, mthdNm, jrAbleResult, jrRuleConst, jrLocRuleApply);

				LocAbleRtn 	  = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
				LocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
				
				if (LocAbleRtnMsg.length() > 0 ) {
					sSchLogContents = sSchLogContents + LocAbleRtnMsg +"\r\n";
				}
				if ("1".equals(LocAbleRtn)) {
					sLogMsg = ydStkColGp+ydStkBedNo+ ydStkLyrNo+"  적치가능 위치평점:"+ sGrade;
					commUtils.printLog(logId, sLogMsg, "SL");				
				    //적치가능 
					sRtnBedDan = ydStkColGp + ydStkBedNo + ydStkLyrNo;
	    			break;
				}
			}
			commUtils.printLog(logId, "sRtnBedDan" + sRtnBedDan, "SL");
			
			String sAPP901 = coilDao.ApplyYn(logId, mthdNm, "APP901", "J", "*");
			commUtils.printLog(logId, "TO위치 RULE상수[신sAPP901] : " + sAPP901, "SL");
		    // 적치 가능 check
			JDTORecord jrResult = JDTORecordFactory.getInstance().create();
			
    		if ("Y".equals(sAPP901)) {
				/***************************************************
				** 신 로직
				** XX이면서 소재인경우  폭및 외경구분 안하는 1단을 다시 검색
				****************************************************/
    			if (sRtnBedDan.length() < 10) {

    				//소재이고  1단을 폭및 외경구분 안하는 걸루 다시 돌림
 					if ("H".equals(ydSchCd.substring(7, 8))) {
 						commUtils.printLog(logId, "▼▼▼▼▼▼▼▼▼▼ 소재 1단  재검색 ▼▼▼▼▼▼▼▼▼▼", "SL");

 						for (int j = 1; j <= jsResult.size(); j++) {

 							jsResult.absolute(j);
 							jrResult = jsResult.getRecord();

 							sGrade 		= commUtils.nvl (jrResult.getFieldString("GRADE"),"9");
 							ydStkColGp 	= commUtils.trim(jrResult.getFieldString("TAG_YD_STK_COL_GP"  ));
 							ydStkBedNo 	= commUtils.trim(jrResult.getFieldString("TAG_YD_STK_BED_NO"  ));
 							ydStkLyrNo 	= commUtils.trim(jrResult.getFieldString("TAG_YD_STK_LYR_NO"  ));

 							if ("001".equals(ydStkLyrNo)) {
 								/**************************************
 								 *         적치 가능 check
 						   		 **************************************/
 								//소재 1단일 경우 재검색
 								jrLocRuleApply.setField("WID_DIA_CHK_YN", "N");
 								JDTORecord jrLocAbleRtn1 = this.toLocAbleCheck(logId, mthdNm, jrResult, jrRuleConst, jrLocRuleApply);

 								String sLocAbleRtn    = commUtils.trim(jrLocAbleRtn1.getFieldString("LOC_ABLE_RTN")) ;
 								//String sLocAbleRtnMsg = commUtils.trim(jrLocAbleRtn1.getFieldString("LOC_ABLE_CONTENTS")) ;


 								if ("1".equals(sLocAbleRtn)) {
 									sLogMsg = ydStkColGp+ydStkBedNo+ ydStkLyrNo+"  적치가능 위치평점:"+ sGrade;
 									commUtils.printLog(logId, sLogMsg, "SL");
 								    //적치가능
 									sRtnBedDan = ydStkColGp + ydStkBedNo + ydStkLyrNo;
 									commUtils.printLog(logId, "▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲", "SL");

 					    			break;
 								}
 							}
 						} //for
 						
						if (sRtnBedDan.length() < 10) {
							sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 적치기준 실패 :" + LocAbleRtnMsg;
							commUtils.printLog(logId, sLogMsg, "SL");
							jrRtn.setField("RTN_CD" , "0");	
							jrRtn.setField("RTN_MSG", sLogMsg);
	 						commUtils.printLog(logId, "▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲", "SL");
							return jrRtn;
						}
 						
 					} else {

 						/***************************************************
 	    				** 제품인 경우
 	    				****************************************************/
						if (sRtnBedDan.length() < 10) {
							sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 적치기준 실패 :" + LocAbleRtnMsg;
							commUtils.printLog(logId, sLogMsg, "SL");
							jrRtn.setField("RTN_CD" , "0");	
							jrRtn.setField("RTN_MSG", sLogMsg);
							return jrRtn;
						}
 					}
    			}
    		} else {
				if (sRtnBedDan.length() < 10) {
					sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 적치기준 실패 :" + LocAbleRtnMsg;
					commUtils.printLog(logId, sLogMsg, "SL");
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", sLogMsg);
					return jrRtn;
				}
    		}

			commUtils.printLog(logId, mthdNm, "S-");
			jrRtn.setField("RTN_CD" , "1");	
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
	    }	
	}

	/**	
	 *      [A] 오퍼레이션명 : 코일크레인스케줄 멀티기동
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYDYDJ552(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일크레인스케줄MAIN멀티기동 [CCoilSchSeEJB.rcvYDYDJ552] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");

			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			int    iSchCnt   = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("SCH_CNT"),"0")); 
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);

			/***************************************************************************
			 * 트랜잭션 분리 
			 ***************************************************************************/
			EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this); //추가
			 
			for (int i = 1 ; i<=iSchCnt; i++) {				
				String ydWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"+i  )); //야드작업예약ID

				if (ydWbookId.equals("") ||ydWbookId.length() == 0 ) {
	           	   continue;
				}
				jrParam.setField("YD_WBOOK_ID"			, ydWbookId); //야드작업예약ID
				jrParam.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
				jrParam.setField("YD_EQP_ID"  			, ""); //야드설비ID	
				//1번만 명령선택 하도록
				if(i == 1 ) {
					jrParam.setField("MULTI_YN"  		, "N"); //야드설비ID	
				} else{
					jrParam.setField("MULTI_YN"  		, "Y"); //야드설비ID	
				}
				JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procYDYDJ551", new Class[] { JDTORecord.class }, new Object[] { jrParam }); //추가
				jrRtn = commUtils.addSndData(jrRtn, jrRtn1); //추가			
			}	
			
			commUtils.printLog(logId, mthdNm + "[스케쥴메인종료]", "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	/**	
	 *      [A] 오퍼레이션명 : 코일크레인스케줄 멀티기동
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procYDYDJ552(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일크레인스케줄MAIN멀티기동 [CCoilSchSeEJB.procYDYDJ552] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");

			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			int    iSchCnt   = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("SCH_CNT"),"0")); 
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);

			/***************************************************************************
			 * 트랜잭션 분리 
			 ***************************************************************************/
			EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this); //추가
			 
			for (int i = 1 ; i<=iSchCnt; i++) {				
				String ydWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"+i  )); //야드작업예약ID

				if (ydWbookId.equals("") ||ydWbookId.length() == 0 ) {
	           	   continue;
				}
				jrParam.setField("YD_WBOOK_ID"			, ydWbookId); //야드작업예약ID
				jrParam.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
				jrParam.setField("YD_EQP_ID"  			, ""); //야드설비ID	
				//1번만 명령선택 하도록
				if(i == 1 ) {
					jrParam.setField("MULTI_YN"  		, "N"); //야드설비ID	
				} else{
					jrParam.setField("MULTI_YN"  		, "Y"); //야드설비ID	
				}
				JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procYDYDJ551", new Class[] { JDTORecord.class }, new Object[] { jrParam }); //추가
				jrRtn = commUtils.addSndData(jrRtn, jrRtn1); //추가			
			}	
			
			commUtils.printLog(logId, mthdNm + "[스케쥴메인종료]", "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
//	/**	
//	 *      [A] 오퍼레이션명 : 코일크레인스케줄 멀티기동
//	 *
//	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 *      @param JDTORecord rcvMsg
//	 *      @return JDTORecord
//	 *      @throws DAOException
//	*/
//	public JDTORecord procYDYDJ552(JDTORecord rcvMsg) throws DAOException {
//		String mthdNm = "코일크레인스케줄MAIN멀티기동 [CCoilSchSeEJB.procYDYDJ552] < " + rcvMsg.getResultMsg();
//		String logId = rcvMsg.getResultCode();
//		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
//		
//		try {
//			commUtils.printLog(logId, mthdNm, "S+");
//
//			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
//			int iSchCnt       = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("SCH_CNT"),"0")); 
//			
//			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
//
//			EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this); //추가
//			 
//			for (int i = 1 ; i<=iSchCnt; i++) {				
//				String ydWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"+i  )); //야드작업예약ID
//
//				if (ydWbookId.equals("") ||ydWbookId.length() == 0 ) {
//	           	   continue;
//				}
//				jrParam.setField("JMS_TC_CD"			, "YDYDJ551"); 
//				jrParam.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
//				jrParam.setField("YD_WBOOK_ID"			, ydWbookId); //야드작업예약ID
//				jrParam.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
//				jrParam.setField("YD_EQP_ID"  			, ""); //야드설비ID	
//				//1번만 명령선택 하도록
//				if(i == 1 ) {
//					jrParam.setField("MULTI_YN"  		, "N"); //야드설비ID	
//				} else{
//					jrParam.setField("MULTI_YN"  		, "Y"); //야드설비ID	
//				}
//				JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procYDYDJ551", new Class[] { JDTORecord.class }, new Object[] { jrParam }); //추가
//				jrRtn = commUtils.addSndData(jrRtn, jrRtn1); //추가
//			}	
//			
//			commUtils.printLog(logId, mthdNm + "[스케쥴메인종료]", "S-");
//			return jrRtn;
//
//		} catch (DAOException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
//		}
//	}


	/**
	 * [A] 오퍼레이션명 : 1단 전후좌우 폭간섭 Check(searchCoilYdGdsWidthDiffCheck3)
	 * @통합
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String chk1WidInterf( String logId, String mthdNms, String sStlNo,  String ydStkColGp, String ydStkBedNo, String ydStkLyrNo 
			                                                 , String sColWGp, String sWidth 
			                                                 , String sOutDia, String sMaxBedNo
			                                                 , String sColYdStkSkidGp 
			                                                 , long   lngRuleCrnLift) throws JDTOException  {
		String mthdNm = "1단전후열좌우폭간섭Check[CCoilSchSeEJB.chk1WidInterf] < " + mthdNms;
		String rtnMsg = "";
		String sRtnVal = CConstant.RETN_CD_FAILURE;
		
		try {
			
			commUtils.printLog(logId, "   "+ mthdNm, "S+");

			double dblMAX_W 	= 0;
			double dblWidthGap 	= 0;
//외경편차			
			long   lngOutDia 	= Long.parseLong    (commUtils.nvl(sOutDia,"0"));
			double dblWidth 	= Double.parseDouble(commUtils.nvl(sWidth,"0"));
			String sBun         = ydStkBedNo;
			
			
			rtnMsg = "   대상재료번호(" + sStlNo + ")=>>대상코일외경:"+lngOutDia + " 대상코일폭:"+dblWidth + " 대상위치열폭구분:"+sColWGp + " 대상열max:"+sMaxBedNo ;
			commUtils.printLog(logId, rtnMsg, "SL");
			rtnMsg = "   대상재료번호(" + sStlNo + ")=>>적치열 구분:"+ydStkColGp + " 번지:"+ydStkBedNo + " 단:"+ydStkLyrNo ;
			commUtils.printLog(logId, rtnMsg, "SL");

			if ("".equals(ydStkColGp)) {  
				sRtnVal = CConstant.RETN_CD_FAILURE;
				return sRtnVal;
			}
			if ("".equals(sColWGp)) {  
				rtnMsg = "   대상위치열폭구분(" + sColWGp + ")이 없음" ;
				commUtils.printLog(logId, rtnMsg, "SL");
				sRtnVal = CConstant.RETN_CD_FAILURE;
				return sRtnVal;
			}
			
			String sDong    = ydStkColGp.substring(1,2);
			String sSpan	= ydStkColGp.substring(0,4);
			String sYel		= ydStkColGp.substring(4,6);
			
			if("M".equals(sColWGp)) { 
				if (dblWidth <= 1040) {  // 보폭존이고 폭인 1040이하
					commUtils.printLog(logId, "정상", "SL");
					sRtnVal = CConstant.RETN_CD_SUCCESS;
					return sRtnVal;
				}
				
			}

			if("L".equals(sColWGp)) { 
				/*if (dblWidth <= 1600) {  // 광폭존이고 폭인 1640이상 >> 1600 으로 변경 YYS 202212
					commUtils.printLog(logId, "정상", "SL");
					sRtnVal = CConstant.RETN_CD_SUCCESS;
					return sRtnVal;
				}*/
				 /*
				 * 광폭기준값 변경 1650 >> 1600  로직 추가 YYS 202212
				 */
				String sAPP006_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "006");
				if("Y".equals(sAPP006_YN)){
					if (dblWidth <= 1600) {  // 광폭존이고 폭인 1640이상 >> 1600 으로 변경 YYS 202212
						commUtils.printLog(logId, "정상", "SL");
						sRtnVal = CConstant.RETN_CD_SUCCESS;
						return sRtnVal;
					}
	    	    }else{
	    	    	if (dblWidth <= 1640) {  // 광폭존이고 폭인 1640이상 >> 1600 으로 변경 YYS 202212
						commUtils.printLog(logId, "정상", "SL");
						sRtnVal = CConstant.RETN_CD_SUCCESS;
						return sRtnVal;
					}
	    	    }
			}		
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
        	jrParam.setField("YEL" 					, sYel);	
        	jrParam.setField("BUN" 					, sBun);	
        	jrParam.setField("STL_NO" 				, sStlNo);	
        	jrParam.setField("YD_STK_COL_GP_SPAN" 	, sSpan);	

			/**********************************
			 * 대상재코일 좌표로 간섭코일중 최대폭 CHECK
			 **************************************/
        	
        	/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilCOMMMaxWidth 
        	WITH TEMP_PARAM AS(  
        	SELECT :V_YEL     AS V_YEL 
        	     , :V_BUN     AS V_BUN
        	     , :V_STL_NO  AS V_STL_NO
        	     , :V_YD_STK_COL_GP_SPAN AS V_YD_STK_COL_GP_SPAN
        	  FROM DUAL 
        	) 
        	, TEMP_LR_SPAN AS (
        	SELECT A.*
        	  FROM USRYDA.TB_YD_STKLYR A
        	 WHERE A.YD_STK_COL_GP LIKE SUBSTR((SELECT V_YD_STK_COL_GP_SPAN FROM TEMP_PARAM),1,2)||'%'
        	   AND A.DEL_YN='N' 
        	   AND SUBSTR(A.YD_STK_COL_GP,3,2) BETWEEN '01' AND '99'
        	   AND A.YD_STK_COL_GP BETWEEN (--좌측스판
        	                                SUBSTR(:V_YD_STK_COL_GP_SPAN,1,2)||TRIM(TO_CHAR(TO_NUMBER(SUBSTR(:V_YD_STK_COL_GP_SPAN,3,2))-1,'09'))||'01'
        	                               )
        	                       AND     (    --우측스판
        	                                SUBSTR(:V_YD_STK_COL_GP_SPAN,1,2)||TRIM(TO_CHAR(TO_NUMBER(SUBSTR(:V_YD_STK_COL_GP_SPAN,3,2))+1,'09'))||'08'
        	                               )
        	)
        	, TEMP_LR_SPAN_H AS (
        	-- 소재와 교차 지점
        	SELECT  /*+ INDEX_DESC(A PK_YD_STKLYR) 
        	       MAX(YD_STK_COL_GP) AS YD_STK_COL_GP
        	  FROM TB_YD_STKLYR A
        	 WHERE A.DEL_YN='N'
        	   AND A.YD_STK_COL_GP LIKE 'H'||(SELECT SUBSTR(YD_STK_COL_GP,2,1) FROM TEMP_LR_SPAN WHERE ROWNUM<=1)||'%'
        	   AND SUBSTR(A.YD_STK_COL_GP,3,2) BETWEEN '01' AND '99'
        	   AND ROWNUM<=1
        	 GROUP BY SUBSTR(YD_STK_COL_GP,1,2)
        	    
        	) 
        	, TEMP_WRK_SPAN AS ( 
        	-- 작업 스판
        	SELECT DENSE_RANK() OVER(ORDER BY YD_STK_COL_GP) AS NUM , A.*
        	  FROM (
        	        SELECT /*+ INDEX(AA PK_YD_STKLYR)  AA.*
        	          FROM TB_YD_STKLYR AA
        	             , TEMP_LR_SPAN_H BB
        	          WHERE AA.YD_STK_COL_GP =BB.YD_STK_COL_GP
        	         UNION ALL
        	        SELECT  /*+ INDEX(CC PK_YD_STKLYR) CC.*
        	         FROM TEMP_LR_SPAN CC
        	       ) A 
        	)  
        	, TEMP_WRK_COL AS (
        	-- 작업 열
        	SELECT ROWNUM AS CNT
        	     , YD_STK_COL_GP 
        	     , YD_COIL_OUTDIA_GRP_GP
        	  FROM (SELECT YD_STK_COL_GP
        	             , YD_COIL_OUTDIA_GRP_GP
        	          FROM (
        	                 SELECT F.YD_COIL_OUTDIA_GRP_GP 
        	                      , ABS((SELECT NUM 
        	                               FROM TEMP_WRK_SPAN 
        	                              WHERE YD_STK_COL_GP LIKE '%'||SUBSTR(:V_YD_STK_COL_GP_SPAN,2,3) ||:V_YEL 
        	                                AND YD_STK_BED_NO = :V_BUN 
        	                                AND YD_STK_LYR_NO = '001' )-NUM) AS RANK_NUM 
        	                      , A.*
        	                   FROM TEMP_WRK_SPAN A
        	                      , USRYDA.TB_YD_STKCOL F
        	                  WHERE A.YD_STK_COL_GP=F.YD_STK_COL_GP
        	               ) B
        	         WHERE RANK_NUM IN (0,1)
        	         GROUP BY YD_STK_COL_GP,YD_COIL_OUTDIA_GRP_GP
        	         ORDER BY YD_STK_COL_GP
        	      ) C
        	)   
        	, TEMP_YAXIS AS ( 
        	--입력번지의 횡행값
        	SELECT F.YD_STK_LYR_YAXIS
        	     , E.V_YD_STK_COL_GP_SPAN||E.V_YEL AS V_YD_STK_COL_GP
        	     , E.V_BUN                         AS V_YD_STK_BED_NO
        	  FROM TEMP_PARAM E
        	     , TB_YD_STKLYR F
        	     , TEMP_WRK_COL C
        	 WHERE E.V_YD_STK_COL_GP_SPAN||E.V_YEL=F.YD_STK_COL_GP(+)
        	   AND E.V_BUN = F.YD_STK_BED_NO(+)
        	   AND F.YD_STK_LYR_NO(+)='001'
        	   AND ROWNUM <= 1
        	)
        	 SELECT *
        	   FROM (
        	         SELECT *
        	           FROM (
        	                SELECT A.YD_STK_COL_GP
        	                     , A.YD_STK_BED_NO
        	                     , A.YD_STK_LYR_NO
        	                     , A.YD_COIL_OUTDIA_GRP_GP
        	                     , A.COIL_OUTDIA
        	                     , A.CHK_YAXIS
        	                     , A.LEFT_YAXIS
        	                     , A.RIGHT_YAXIS
        	                     , A.STL_NO
        	                     , NVL(COIL_W,0) AS COIL_W 
        	                     , V_YD_STK_COL_GP
        	                     , V_YD_STK_BED_NO
        	                  FROM  (
        	                        SELECT C.YD_STK_LYR_YAXIS AS CHK_YAXIS
        	                             , (F.YD_STK_LYR_YAXIS - D.COIL_OUTDIA) AS LEFT_YAXIS
        	                             , (F.YD_STK_LYR_YAXIS + D.COIL_OUTDIA) AS RIGHT_YAXIS
        	                             , A.YD_COIL_OUTDIA_GRP_GP
        	                             , D.COIL_OUTDIA, B.* ,C.*,F.*
        	                          FROM TEMP_WRK_COL A
        	                             , TEMP_PARAM B
        	                             , TB_YD_STKLYR C
        	                             , TB_PT_COILCOMM D
        	                             , TEMP_YAXIS F
        	                         WHERE A.YD_STK_COL_GP = C.YD_STK_COL_GP 
        	                           AND B.V_STL_NO=D.COIL_NO 
        	                           AND C.YD_STK_LYR_YAXIS BETWEEN (F.YD_STK_LYR_YAXIS - D.COIL_OUTDIA) AND (F.YD_STK_LYR_YAXIS + D.COIL_OUTDIA)
        	                           AND C.YD_STK_LYR_ACT_STAT IN ('E','N')
        	                         
        	                       ) A
        	                     , USRPTA.TB_PT_COILCOMM B 
        	                 WHERE A.STL_NO = B.COIL_NO(+)
        	                ) Z
        	         WHERE YD_STK_COL_GP||YD_STK_BED_NO||YD_STK_LYR_NO<>V_YD_STK_COL_GP||V_YD_STK_BED_NO||'001' --//TO위치 놓을 위치 제외
        	       ) A
        	ORDER BY COIL_W DESC, YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO

          */
          	JDTORecordSet jsCoilMaxWid = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStklyrCoilCOMMMaxWidth", logId, mthdNm, "   코일 MAX폭");
        	
        	if (jsCoilMaxWid.size() <= 0) {
				return sRtnVal = CConstant.RETN_CD_FAILURE;
			} else {
				jsCoilMaxWid.absolute(1);
				JDTORecord jrCoilMaxWid = jsCoilMaxWid.getRecord();
				
				dblMAX_W	= Double.parseDouble(commUtils.nvl(jrCoilMaxWid.getFieldString("COIL_W"),"0")); 
		 
				rtnMsg = "   폭간섭위치 최대폭 대상(STL_NO:" + commUtils.trim(jrCoilMaxWid.getFieldString("STL_NO"))
													+ " 위치:"+commUtils.trim(jrCoilMaxWid.getFieldString("YD_STK_COL_GP"))
													+ commUtils.trim(jrCoilMaxWid.getFieldString("YD_STK_BED_NO"))
													+ commUtils.trim(jrCoilMaxWid.getFieldString("YD_STK_LYR_NO"))
													+ ")";
				commUtils.printLog(logId, rtnMsg, "SL");
			}	
			
        	rtnMsg = "   ROWCNT:"+jsCoilMaxWid.size() + "  폭간섭위치 최대폭(고정):" + dblMAX_W    ;
			commUtils.printLog(logId, rtnMsg, "SL");
			
			String sChkFlag = "";
			
			//폭(Width)제한 확인
			if(dblMAX_W == 0){
				
				rtnMsg = "   대상폭간격:" + dblWidth + "대상위치열폭구분:" + sColWGp + "대상위치열SKID TYPE:" + sColYdStkSkidGp  ;
				commUtils.printLog(logId, rtnMsg, "SL");
						 
				
				jrParam = commUtils.getParam(logId, mthdNm, "");
				jrParam.setField("YD_STK_COL_GP"      , ydStkColGp);	
				jrParam.setField("COIL_WIDTH" 	      , ""+dblWidth);	
				jrParam.setField("WIDTH_GP" 	      , sColWGp);	
				jrParam.setField("COL_YD_STK_SKID_GP" , sColYdStkSkidGp);	
				jrParam.setField("YD_STK_BED_NO"	  , ydStkBedNo);
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCoilWidthInter2 */ 
				/*WITH TEMP_PARAM AS (
					SELECT :V_YD_STK_COL_GP      AS YD_STK_COL_GP
					     , :V_COIL_WIDTH         AS COIL_WIDTH
					     , :V_WIDTH_GP           AS WIDTH_GP
					     , :V_COL_YD_STK_SKID_GP AS COL_YD_STK_SKID_GP
					     , :V_YD_STK_BED_NO      AS YD_STK_BED_NO
					  FROM DUAL
					)  -- JD0321
					SELECT CASE WHEN COL_YD_STK_SKID_GP = 'C' THEN   -- 가변일 경우
					            CASE WHEN YD_STK_COL_GP IN ('JH0601','JH4806') THEN 
					                      CASE WHEN TO_NUMBER(COIL_WIDTH) <= 1580 THEN 'T' ELSE 'F' END
					                 WHEN YD_STK_COL_GP IN ('JD0301','JE0301','JF0301'
					                                       ,'JE0701'
					                                       ,'JE0702'
					                                       ,'JD5207','JE5207','JF5207'
					                                       ,'JE4702'
					                                       ,'JE4701') THEN  
					                      CASE WHEN TO_NUMBER(COIL_WIDTH) <= 1440 THEN 'T' ELSE 'F' END
					                 WHEN YD_STK_COL_GP IN ('JG0301','JH0301','JG5207','JH5207') THEN  
					                      CASE WHEN TO_NUMBER(COIL_WIDTH) <= 1400 THEN 'T' ELSE 'F' END
					                 WHEN YD_STK_COL_GP IN ('JF0207','JG0207','JF5401') THEN  
					                      CASE WHEN TO_NUMBER(COIL_WIDTH) <= 1380 THEN 'T' ELSE 'F' END
					                 WHEN YD_STK_COL_GP IN ('JD0207','JE0207'
					                                       ,'JD0506','JE0506','JF0506','JG0506','JH0506'
					                                       ,'JD5401','JE5401'
					                                       ,'JD5001','JE5001','JF5001','JG5001','JH5001') THEN  
					                      CASE WHEN TO_NUMBER(COIL_WIDTH) <= 1340 THEN 'T' ELSE 'F' END
					                 WHEN YD_STK_COL_GP IN ('JH0207') THEN  
					                      CASE WHEN TO_NUMBER(COIL_WIDTH) <= 1300 THEN 'T' ELSE 'F' END
					                 WHEN WIDTH_GP = 'M' THEN  
					                      CASE WHEN TO_NUMBER(COIL_WIDTH) <= 1600 THEN 'T' ELSE 'F' END
					                 ELSE CASE WHEN TO_NUMBER(COIL_WIDTH) <= 2100 THEN 'T' ELSE 'F' END 
					            END
					       ELSE
					           CASE WHEN YD_STK_COL_GP = 'JD3706' AND YD_STK_BED_NO = '07' THEN  
					                      CASE WHEN TO_NUMBER(COIL_WIDTH) <= 1200 THEN 'T' ELSE 'F' END      
					                   WHEN WIDTH_GP = 'M' THEN              
					                      CASE WHEN TO_NUMBER(COIL_WIDTH) <= 1600 THEN 'T' ELSE 'F' END     
					                   ELSE                
					                      CASE WHEN TO_NUMBER(COIL_WIDTH) <= 2100 THEN 'T' ELSE 'F' END                 
					           END 
					       END  AS CHECK_FLAG        
					 FROM TEMP_PARAM
                 */
				
				JDTORecordSet jsWidthInter2 = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCoilWidthInter2", logId, mthdNm, "   폭간섭여부");
	        	if(jsWidthInter2.size() <= 0) {
					return sRtnVal = CConstant.RETN_CD_FAILURE;
				} else {
					jsWidthInter2.absolute(1);
					JDTORecord jrWidthInter2 = jsWidthInter2.getRecord();

					sChkFlag = commUtils.trim(jrWidthInter2.getFieldString("CHECK_FLAG")); 
					
					commUtils.printLog(logId, "   sChkFlag:" + sChkFlag , "SL");
				}					
				
	        	if ("T".equals(sChkFlag)) {
	        		sRtnVal = CConstant.RETN_CD_SUCCESS;
	        	} else if ("F".equals(sChkFlag)) {
	        		sRtnVal = CConstant.RETN_CD_FAILURE;
	        		commUtils.printLog(logId, "   "+ mthdNm, "S-");
	    			return sRtnVal;
	        	} else {
	        		sRtnVal = CConstant.RETN_CD_FAILURE;
	        		commUtils.printLog(logId, "   "+ mthdNm, "S-");
	    			return sRtnVal;
	        	}
	        	
			} else {
			
			//폭갭 (WidthGap)제한 확인
			//if(dblMAX_W > 0){
                                                             //500                                 //500               
				dblWidthGap = Math.abs(((dblWidth / 2) - 500) + ((dblMAX_W / 2) - 500)) ;

				rtnMsg = "   최종폭간격:" + dblWidthGap + "  위치:" + ydStkColGp + "  대상위치열폭구분:" + sColWGp  ;
				commUtils.printLog(logId, rtnMsg, "SL");
				
				jrParam = commUtils.getParam(logId, mthdNm, "");
				jrParam.setField("YD_STK_COL_GP"      , ydStkColGp);	
				jrParam.setField("WIDTH_GAP" 	      , ""+dblWidthGap);	
				jrParam.setField("WIDTH_GP" 	      , sColWGp);	
				jrParam.setField("COL_YD_STK_SKID_GP" , sColYdStkSkidGp);	
								
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCoilWidthInter 
				WITH TEMP_PARAM AS (
				SELECT :V_YD_STK_COL_GP      AS YD_STK_COL_GP
				     , :V_WIDTH_GAP          AS WIDTH_GAP
				     , :V_WIDTH_GP           AS WIDTH_GP
				     , :V_COL_YD_STK_SKID_GP AS COL_YD_STK_SKID_GP
				  FROM DUAL
				)  -- JD0321
				SELECT CASE WHEN COL_YD_STK_SKID_GP = 'C' THEN   -- 가변일 경우
				            CASE WHEN SUBSTR(YD_STK_COL_GP,2,1)  IN ('D','E','F','G','H') AND SUBSTR(YD_STK_COL_GP,3,2) = '55'   THEN  
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 951 THEN 'T' ELSE 'F' END
				                 WHEN SUBSTR(YD_STK_COL_GP,2,1)  IN ('G','H') AND SUBSTR(YD_STK_COL_GP,3,4) IN ('5402','5403','5404','5405','5406')  THEN
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 851 THEN 'T' ELSE 'F' END
				                 WHEN SUBSTR(YD_STK_COL_GP,2,1)  IN ('D','E','F','G','H')  AND SUBSTR(YD_STK_COL_GP,5,2) <> '01' AND SUBSTR(YD_STK_COL_GP,3,2) = '48' THEN  
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 851 THEN 'T' ELSE 'F' END
				                 WHEN SUBSTR(YD_STK_COL_GP,2,1)  IN ('A','B','C','D','E','F','G','H')  AND SUBSTR(YD_STK_COL_GP,3,2) IN ('56','57','58') THEN  
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 851 THEN 'T' ELSE 'F' END
				                 WHEN SUBSTR(YD_STK_COL_GP,2,1)  IN ('B','C') AND (SUBSTR(YD_STK_COL_GP,3,4) IN ('0505') OR SUBSTR(YD_STK_COL_GP,3,2) IN ('06')) THEN  
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 851 THEN 'T' ELSE 'F' END
				                 WHEN SUBSTR(YD_STK_COL_GP,2,1)  IN ('G','H') AND SUBSTR(YD_STK_COL_GP,3,4) IN ('5206','5205','5204','5203','5202','5401') THEN  
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 351 THEN 'T' ELSE 'F' END
				                 WHEN SUBSTR(YD_STK_COL_GP,2,1)  IN ('D','G','F') AND SUBSTR(YD_STK_COL_GP,3,4) IN ('4801') THEN  
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 651 THEN 'T' ELSE 'F' END
				                 WHEN WIDTH_GP = 'M' THEN  
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 351 THEN 'T' ELSE 'F' END
				                 ELSE CASE WHEN TO_NUMBER(WIDTH_GAP) < 851 THEN 'T' ELSE 'F' END 
				            END 
				       ELSE     
				            CASE WHEN SUBSTR(YD_STK_COL_GP,2,1)  IN ('D','E','F','G','H') AND SUBSTR(YD_STK_COL_GP,3,2) = '55'   THEN  
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 951 THEN 'T' ELSE 'F' END
				                 WHEN SUBSTR(YD_STK_COL_GP,2,1)  IN ('G','H') AND SUBSTR(YD_STK_COL_GP,3,4) IN ('5402','5403','5404','5405','5406')  THEN
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 851 THEN 'T' ELSE 'F' END
				                 WHEN SUBSTR(YD_STK_COL_GP,2,1)  IN ('D','E','F','G','H')  AND SUBSTR(YD_STK_COL_GP,3,2) = '48' THEN  
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 851 THEN 'T' ELSE 'F' END
				                 WHEN SUBSTR(YD_STK_COL_GP,2,1)  IN ('A','B','C','D','E','F','G','H')  AND SUBSTR(YD_STK_COL_GP,3,2) IN ('56','57','58') THEN  
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 851 THEN 'T' ELSE 'F' END
				                 WHEN SUBSTR(YD_STK_COL_GP,2,1)  IN ('B','C') AND (SUBSTR(YD_STK_COL_GP,3,4) IN ('0505') OR SUBSTR(YD_STK_COL_GP,3,2) IN ('06')) THEN  
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 851 THEN 'T' ELSE 'F' END
				                 WHEN SUBSTR(YD_STK_COL_GP,2,1)  IN ('G','H') AND SUBSTR(YD_STK_COL_GP,3,4) IN ('5206','5205','5204','5203','5202','5401') THEN  
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 351 THEN 'T' ELSE 'F' END
				                 WHEN SUBSTR(YD_STK_COL_GP,2,3)  IN ('A51','A52','A53','A54','A55'
				                                                    ,'B02','B03','B04','B05','B06'
				                                                    ,'B50','B51','B52','B53','B54','B55'
				                                                    ,'C02','C03','C04','C05','C06'
				                                                    ,'C52','C53','C54','C55') THEN  
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 851 THEN 'T' ELSE 'F' END
				                 WHEN SUBSTR(YD_STK_COL_GP,2,5)  IN ('C5105') THEN  
				                      CASE WHEN TO_NUMBER(WIDTH_GAP) < 851 THEN 'T' ELSE 'F' END
				                 WHEN WIDTH_GP = 'M' THEN  
				                      CASE WHEN SUBSTR(YD_STK_COL_GP,3,2) > '00' AND SUBSTR(YD_STK_COL_GP,3,2) < '99'  AND SUBSTR(YD_STK_COL_GP,3,2) > 57  THEN 
				                           CASE WHEN TO_NUMBER(WIDTH_GAP) < 351 THEN 'T' ELSE 'F' END
				                      ELSE 
				                           CASE WHEN TO_NUMBER(WIDTH_GAP) < 491 THEN 'T' ELSE 'F' END
				                      END      
				                 ELSE CASE WHEN TO_NUMBER(WIDTH_GAP) < 851 THEN 'T' ELSE 'F' END 
				                 END
				            END 
				            AS CHECK_FLAG   
				 FROM TEMP_PARAM
	          */	

				JDTORecordSet jsWidthInter = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCoilWidthInter", logId, mthdNm, "제품폭간섭여부");
	        	if (jsWidthInter.size() <= 0) {
					return sRtnVal = CConstant.RETN_CD_FAILURE;
				} else {
					jsWidthInter.absolute(1);
					JDTORecord jrWidthInter = jsWidthInter.getRecord();

					sChkFlag = commUtils.trim(jrWidthInter.getFieldString("CHECK_FLAG")); 
					
					commUtils.printLog(logId, "sChkFlag:" + sChkFlag , "SL");
				}					
				
	        	if ("T".equals(sChkFlag)) {
	        		sRtnVal = CConstant.RETN_CD_SUCCESS;
	        	} else if ("F".equals(sChkFlag)) {
	        		sRtnVal = CConstant.RETN_CD_FAILURE;
	        	} else {
	        		sRtnVal = CConstant.RETN_CD_FAILURE;		
				}
			}
				
			commUtils.printLog(logId, "   "+ mthdNm, "S-");
			return sRtnVal;

		} catch(Exception e) {
			rtnMsg = "   "+sStlNo+">>1단전후좌우폭간섭Check중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, rtnMsg, "SL");
			return sRtnVal = CConstant.RETN_CD_FAILURE;
		}
	}
	
	/**
	 * 동간입고/대차상차
	 * @param logId
	 * @param mthdNms
	 * @param jrWbook
	 * @param jrCrnSch
	 * @return jrRtn
	 * @throws JDTOException
	 */
	public JDTORecord toLocUpTCar(String logId,	String mthdNms,	JDTORecord jrWbook,	JDTORecord jrCrnSch) throws	JDTOException {
		String mthdNm =	"TO위치결정:대차상차 [CCoilSchSeEJB.toLocUpTCar] < " + mthdNms;
		String sLogMsg = null;
		JDTORecord jrRtn	= JDTORecordFactory.getInstance().create();	//전문 Return
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보	READ
		//----------------------------------------------------------------------------------------------------------------------
		String sModifier	= commUtils.trim(jrCrnSch.getFieldString("MODIFIER"));
		String ydSchCd		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			// 크레인스케줄코드
		String ydToLocGuide	= commUtils.trim(jrWbook.getFieldString("YD_TO_LOC_GUIDE"));			// 크레인스케줄코드
		
		String sStlNo		= commUtils.trim(jrCrnSch.getFieldString("STL_NO"));			// 크레인작업재료
		String ydCrnSchId	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		// 크레인스케줄ID
		String ydUpWoLoc	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));
		String ydUpWoLayer	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));

		int	iYdEqpWrkSh		= Integer.parseInt	(commUtils.trim(jrCrnSch.getFieldString("SH_CNT")));	// 크레인작업재료 총매수
		int	iYdEqpWrkWt		= Integer.parseInt	(commUtils.trim(jrCrnSch.getFieldString("SUM_MTL_WT")));// 크레인작업재료 총중량
		double dblYdEqpWrkT	= Double.parseDouble(commUtils.trim(jrCrnSch.getFieldString("SUM_MTL_T")));	// 크레인작업재료 총높이
		String ydEqpWrkMaxW	= commUtils.trim(jrCrnSch.getFieldString("MAX_MTL_W"  ));					// 크레인작업재료 중 최대 폭
		String ydEqpWrkMaxL	= commUtils.trim(jrCrnSch.getFieldString("MAX_MTL_L"  ));					// 크레인작업재료 중 최대 길이

		String ydDnWoLoc	= "";

		try	{
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId,	jrCrnSch);

			// 대차 00번지 UPDATE (대차입고, 동간이적)
			ydDnWoLoc = ydSchCd.substring(0, 6) + "00"; // ex) JGTC0100
			String ydDnWoLayer	= "001";
			
			// 권하위치 대차일때
			if(ydToLocGuide.length() >= 6) {
				if("TC".equals(ydToLocGuide.substring(2, 4))) { 
					ydDnWoLoc = ydToLocGuide.substring(0, 6) + "00"; // ex) JGTC0100
					 
				}
			}
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_EQP_ID", "JX" + ydDnWoLoc.substring(2, 6));
			
			JDTORecordSet jsYdEqp = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getTCarInfo", logId, mthdNm, "대차상태 조회2");
			
			if(jsYdEqp == null || jsYdEqp.size() < 1){
				jrRtn.setField("RTN_CD" 	, "0");	
				jrRtn.setField("RTN_MSG" 	, "대차상태 조회에 실패 하였습니다.");	
				return jrRtn;
				
			} else {
				jsYdEqp.first();
				JDTORecord jrYdEqp = jsYdEqp.getRecord(0);	  
				  String ydEqpStat = jrYdEqp.getFieldString("YD_EQP_STAT");
				  
				//REQ202407592703 야드설비상태(B:고장, N:정상, R:복구, P:롤이송 등)   
				if("P".equals(ydEqpStat)||"B".equals(ydEqpStat)){
					sLogMsg	= "현재 대차가 고장 또는 롤이송 중이라 사용 할 수 없습니다!";
					commUtils.printLog(logId, sLogMsg, "SL");
					jrRtn.setField("RTN_CD" 	, "0");	
					jrRtn.setField("RTN_MSG" 	, sLogMsg);	
					return jrRtn;
				}
			}

			if ( "".equals(ydUpWoLoc) )	{
				sLogMsg	= "크레인작업재료의	 재료정보["+sStlNo+"]에	대한 권하 또는 권상위치	이상 ";
				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			}

			/*****************************************
			 * 1. 권상위치 정보	재조회
			 *****************************************/
			JDTORecordSet jsLayerUpXy =	JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord jrInBed = commUtils.getParam(logId, mthdNm, sModifier);
			jrInBed.setField("YD_STK_COL_GP",	ydUpWoLoc.substring(0, 6));	 //권상지시위치
			jrInBed.setField("YD_STK_BED_NO",	ydUpWoLoc.substring(6));	 //권상지시위치
			jrInBed.setField("YD_STK_LYR_NO",	ydUpWoLayer);

			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkLayerBybed
			SELECT A.YD_STK_COL_GP
				 , A.YD_STK_BED_NO
				 , A.YD_STK_LYR_NO
				 , A.YD_STK_LYR_XAXIS
				 , A.YD_STK_LYR_YAXIS
				 , A.YD_STK_LYR_ZAXIS
				 , B.YD_STK_BED_XAXIS_TOL
				 , B.YD_STK_BED_YAXIS_TOL
				 , B.YD_STK_BED_ZAXIS_TOL
				 , (SELECT ROTATION_ANGLE FROM TB_YD_STKCOL	WHERE YD_STK_COL_GP	= A.YD_STK_COL_GP) AS ROTATION_ANGLE
				 , A.YD_STK_LYR_MTL_STAT
			  FROM TB_YD_STKLYR	A
				 , TB_YD_STKBED	B
			 WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
			   AND A.YD_STK_BED_NO = B.YD_STK_BED_NO
			   AND A.YD_STK_COL_GP = :V_YD_STK_COL_GP
			   AND A.YD_STK_BED_NO = :V_YD_STK_BED_NO
			   AND A.YD_STK_LYR_NO = :V_YD_STK_LYR_NO
			   AND A.DEL_YN	='N'
			   AND B.DEL_YN	='N'
			*/
			jsLayerUpXy	= commDao.select(jrInBed, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkLayerBybed",	logId, mthdNm, "권상 BED 좌표 조회");
			if (jsLayerUpXy.size() <= 0) {
				sLogMsg	=  "확인:"+sStlNo+"권상	Layer 좌표 조회	검색 실패.";
				commUtils.printLog(logId, sLogMsg, "SL");

			}
			jsLayerUpXy.first();
			JDTORecord jrUpLayerXy = jsLayerUpXy.getRecord();

			/*****************************************
			 * 2. 크레인스케쥴 UPDATE
			 *****************************************/
			JDTORecord jrUpCrnSch =	commUtils.getParam(logId, mthdNm, sModifier);
			jrUpCrnSch.setField("YD_CRN_SCH_ID",			ydCrnSchId);				// 크레인스케줄ID
			// 권상정보
			jrUpCrnSch.setField("YD_UP_WO_LOC",				ydUpWoLoc);					// 권상지시위치
			jrUpCrnSch.setField("YD_UP_WO_LAYER",			ydUpWoLayer);				// 권상지시단
			jrUpCrnSch.setField("YD_UP_STK_COL_GP",			ydUpWoLoc.substring(0, 6));	// 권상지시위치 - 적치열
			jrUpCrnSch.setField("YD_UP_STK_BED_NO",			ydUpWoLoc.substring(6));	// 권상지시위치 - 적치베드
			jrUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",		commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_LYR_XAXIS"    )));
			jrUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",		commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_LYR_YAXIS"    )));
			jrUpCrnSch.setField("YD_UP_WO_LOC_ZAXIS",		commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_LYR_ZAXIS"    )));
			jrUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MAX",	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL")));
			jrUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MIN",	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL")));
			jrUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MAX",	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL")));
			jrUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MIN",	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL")));
			jrUpCrnSch.setField("YD_UP_WO_LOC_YAXIS1",		"");
			jrUpCrnSch.setField("YD_UP_WO_LOC_YAXIS2",		"");
			jrUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MAX",	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL")));
			jrUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MIN",	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL")));
			jrUpCrnSch.setField("UP_ROTATION_ANGLE",		commUtils.trim(jrUpLayerXy.getFieldString("ROTATION_ANGLE"	    )));
			// 권하정보
			jrUpCrnSch.setField("YD_DN_WO_LOC",				ydDnWoLoc);					// 권하지시위치
			jrUpCrnSch.setField("YD_DN_WO_LAYER",			ydDnWoLayer);				// 권하지시단
			jrUpCrnSch.setField("YD_DN_STK_COL_GP",			ydDnWoLoc.substring(0, 6));	// 권하지시위치 - 적치열
			jrUpCrnSch.setField("YD_DN_STK_BED_NO",			ydDnWoLoc.substring(6));	// 권하지시위치 - 적치베드
			jrUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",		"");
			jrUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",		"");
			jrUpCrnSch.setField("YD_DN_WO_LOC_ZAXIS",		"");
			jrUpCrnSch.setField("YD_DN_WO_XAXIS_GAP_MAX",	"");
			jrUpCrnSch.setField("YD_DN_WO_XAXIS_GAP_MIN",	"");
			jrUpCrnSch.setField("YD_DN_WO_YAXIS_GAP_MAX",	"");
			jrUpCrnSch.setField("YD_DN_WO_YAXIS_GAP_MIN",	"");
			jrUpCrnSch.setField("YD_DN_WO_LOC_YAXIS1",		"");
			jrUpCrnSch.setField("YD_DN_WO_LOC_YAXIS2",		"");
			jrUpCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MAX",	"");
			jrUpCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MIN",	"");
			jrUpCrnSch.setField("DOWN_ROTATION_ANGLE",		"");

			//기타
			jrUpCrnSch.setField("YD_EQP_WRK_SH",			String.valueOf(iYdEqpWrkSh));	// 크레인작업재료 총매수
			jrUpCrnSch.setField("YD_EQP_WRK_WT",			String.valueOf(iYdEqpWrkWt));	// 크레인작업재료 총중량
			jrUpCrnSch.setField("YD_EQP_WRK_T",				String.valueOf(dblYdEqpWrkT));	// 크레인작업재료 총높이
			jrUpCrnSch.setField("YD_EQP_WRK_MAX_W",			ydEqpWrkMaxW);					// 크레인작업재료 중 최대 폭
			jrUpCrnSch.setField("YD_EQP_WRK_MAX_L",			ydEqpWrkMaxL);					// 크레인작업재료 중 최대 길이

			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLoc
			UPDATE TB_YD_CRNSCH
			   SET MODIFIER	= :V_MODIFIER
				  ,MOD_DDTT	= SYSDATE
				  ,............
			 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			*/

			int	iRtnVal	= commDao.update(jrUpCrnSch, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLoc", logId, mthdNm, "크레인스케쥴 갱신");

			if (iRtnVal	<= 0) {
				sLogMsg	=  "확인:"+sStlNo+"권하지시위치["+ydDnWoLoc+"], 권하지시단[" +ydDnWoLayer +" ]을 크레인스케줄에 수정 중 ERROR 발생";

				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			}

			commUtils.printLog(logId, mthdNm, "S-");

			jrRtn.setField("RTN_CD"	, "1");
			return jrRtn;

		} catch	(DAOException e) {
			throw e;
		} catch	(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 주작업TO위치결정  -> 야드 (명령선택 Y5YDL007 에서 TO위치 확정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord toLocPrimaryWorkCV(String logId, String mthdNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String mthdNm = "CV주작업TO위치결정[CCoilSchSeEJB.toLocPrimaryWorkCV] < " + mthdNms;
    	String sLogMsg = null;
    	JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();	//전문 Return
		//----------------------------------------------------------------------------------------------------------------------
		//	명령선택에서  수입 호출
		//----------------------------------------------------------------------------------------------------------------------
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");

	    	String ydStkColGp   = "";
			String ydStkBedNo   = "";
			String ydStkLyrNo   = "";	
			String sRtnBedDan   = "";	
			String sDBLogMsg    = "";
			String sGrade 		= "9";
			
			String ydSchCd 	   	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
			String ydWbookId	= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));			//작업예약
			String schLogYn 	= commUtils.trim(jrWbook.getFieldString("SCHLOG_YN"  ));			
			String reTolocYn 	= commUtils.trim(jrWbook.getFieldString("RE_TOLOC_YN"  ));	//안전구역으로 권하위치 재조회 여부
			
			String sStlNo	   	= commUtils.trim(jrCrnSch.getFieldString("STL_NO"));			//크레인작업재료
			String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
			String ydEqpId     	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
			String ydUpWoLoc 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
			String ydUpWoLayer 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));		
			String sModifier    = commUtils.trim(jrCrnSch.getFieldString("MODIFIER"));
			// 기존과 다름
			String ydRouteGp    = coilDao.getCoilYdRouteGpCV(logId,mthdNm,ydSchCd,sStlNo);       //검색조건 행선
			String sSchLogContents = "";
			String sTcMoveYn = "N";  // 대차 자동 출발 여부 (동간입고 처리)
			
			if ( "".equals(ydUpWoLoc) ) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			} 
			
			if ( "".equals(ydRouteGp) ) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 검색조건행선정보가 없습니다. ";
				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			} 
			
//			JDTORecord jrToLocPrimary = JDTORecordFactory.getInstance().create();			
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("STL_NO"			, sStlNo);		//권상 STOCK
			jrParam.setField("YD_SCH_CD"		, ydSchCd);		//스케줄 코드
			jrParam.setField("YD_EQP_ID"		, ydEqpId);		//설비ID
			jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);	//크레인 스케쥴 ID
			jrParam.setField("YD_UP_WO_LOC" 	, ydUpWoLoc);  
			jrParam.setField("YD_UP_WO_LAYER" 	, ydUpWoLayer);  
			jrParam.setField("YD_ROUTE_GP"  	, ydRouteGp);  
			jrParam.setField("RE_TOLOC_YN"  	, reTolocYn);  //사용하지 않음 보류 
			sLogMsg =  " TOSQL"+ydCrnSchId+ " 권상재료["+sStlNo +" 의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, sLogMsg, "SL");
			
	    	//----------------------------------------------------------------------------------------------------------------------
			//	일반적인 적치가능한 베드 조회
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecordSet jsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			if ("H".equals(ydSchCd.substring(7, 8))) {
				
				/***************************************
				 * 수입 사용자 지정 검색 시작열 적용
				 ***************************************/
				String sBayGp = ydSchCd.substring(1,2);
				if( "B".equals(sBayGp) || "C".equals(sBayGp) ) {
					if( "K".equals(ydRouteGp.substring(1,2)) ) {
						sBayGp += "2";
					} else {
						sBayGp += "1";
					}
				}
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocPrimaryWorkCV
				 -- 대상 위치 SELECT
				WITH TBL_BF_CRNWRK AS (
				     SELECT NVL(MAX(B.YD_STK_BED_SRCH_SEQ),1) AS CC1
				       FROM TB_YD_LOCSRCHRNG A
				          , TB_YD_LOCSRCHBED B
				          , (SELECT *
				               FROM TB_YD_WRKHIST
				              WHERE YD_GP = 'J'
				                AND YD_WRK_HIST_ID = (
				                                      SELECT MAX(YD_WRK_HIST_ID)
				                                        FROM TB_YD_WRKHIST         A
				                                           , USRPTA.TB_PT_COILCOMM C
				                                       WHERE A.STL_NO = C.COIL_NO
				                                         AND A.YD_GP = 'J'
				                                         AND A.YD_DN_CMPL_DT > SYSDATE - 3
				                                         AND A.YD_SCH_CD = :V_YD_SCH_CD
				                                         AND 'Y' = CASE WHEN SUBSTR(A.YD_SCH_CD, 2, 1) IN ('B', 'C')
				                                                         AND SUBSTR(:V_YD_ROUTE_GP, 2, 1) = 'A'
				                                                         AND SUBSTR(C.NEXT_PROC, 2, 1)    = 'H'
				                                                             THEN 'Y'
				                                                        WHEN SUBSTR(A.YD_SCH_CD, 2, 1) IN ('B', 'C')
				                                                         AND SUBSTR(:V_YD_ROUTE_GP, 2, 1) = SUBSTR(C.NEXT_PROC, 2, 1)
				                                                             THEN 'Y'
				                                                        WHEN SUBSTR(A.YD_SCH_CD, 2, 1) IN ('A', 'D', 'E', 'F', 'G', 'H')
				                                                             THEN 'Y'
				                                                        ELSE 'N'
				                                                   END
				--                                         AND :V_YD_ROUTE_GP = CASE WHEN SUBSTR(C.NEXT_PROC,1,1) = SUBSTR(A.YD_SCH_CD, 2, 1)
				--			                                                        AND SUBSTR(C.NEXT_PROC,2,1) IN ('H','K','R','A')
				--			                                                            THEN C.NEXT_PROC -- 차공정
				--                                                                   ELSE SUBSTR(A.YD_SCH_CD, 2, 1) ||'Z'
				--                                                              END
				                                      )
				            ) C
				      WHERE A.YD_LOC_SRCH_RNG_REG_SNO = B.YD_LOC_SRCH_RNG_REG_SNO
				        AND A.YD_SCH_CD               = :V_YD_SCH_CD
				        AND A.YD_SCH_CD               = C.YD_SCH_CD
				        -- C동반입 HFL재(B3), SPM재(B4)
				        AND A.YD_ROUTE_GP             = :V_YD_ROUTE_GP
				        AND B.YD_STK_COL_GP = SUBSTR(C.YD_DN_WR_LOC,0,6)
				      --  AND B.YD_STK_BED_NO = SUBSTR(C.YD_DN_WR_LOC,7,2)
				        AND A.DEL_YN = 'N'
				        AND B.DEL_YN = 'N'
				)
				, TO_LOC_TABLE AS (
				    SELECT ABS(TO_NUMBER(SL.YD_STK_LYR_NO) - TO_NUMBER(CC.COIL_YD_STK_LYR_NO)) AS PRIOR4 -- LYR 우선
				         , X1.YD_SCH_CD
				         , CC.STL_NO
				         , SL.YD_STK_COL_GP         AS TAG_YD_STK_COL_GP
				         , SL.YD_STK_BED_NO         AS TAG_YD_STK_BED_NO
				         , SL.YD_STK_LYR_NO         AS TAG_YD_STK_LYR_NO
				         -- 열에 따른 코일 간격
				         , SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP)  AS INT
				         -- LEFT_BED
				         , DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
				                                    '002', SL.YD_STK_BED_NO)                               AS TAG_LEFT_BED
				         , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_LYR_NO,
				                                    '002', LPAD(TO_NUMBER(SL.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_LEFT_LAYER
				         -- RIGHT BED
				         , DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
				                                    '002', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'))
				                                                                                           AS TAG_RIGHT_BED
				         , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_LYR_NO,
				                                    '002', LPAD(TO_NUMBER(SL.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_RIGHT_LAYER
				         , SC.COL_OUTDIA_GRP_GP
				         , SC.COL_YD_LOC_GP
				         , SC.COL_W_GP
				         , SC.COL_YD_STK_SKID_GP
				         , CC.COIL_W
				         , CC.COIL_W_GP
				         , CC.COIL_YD_STK_COL_GP
				         , CC.COIL_YD_STK_BED_NO
				         , CC.COIL_YD_STK_LYR_NO
				         , CC.COIL_OUTDIA_GRP_GP
				         , X1.YD_LOC_SRCH_RNG_SEQ
				         , X1.YD_STK_BED_SRCH_SEQ
				         -- 적치가능여부 CHECK시 필요 : H/J/ABC/WRAP
				         , 'H' AS CHK_SKID_GP
				      FROM TB_YD_STKLYR SL
				         , ( SELECT YD_STK_COL_GP
				                  , YD_GP
				                  , YD_BAY_GP
				                  , YD_COIL_OUTDIA_GRP_GP  AS COL_OUTDIA_GRP_GP
				                  , MATL_SUP_MTD_GP
				                  , (CASE WHEN MATL_SUP_MTD_GP = 'A' THEN 0
				                          WHEN MATL_SUP_MTD_GP = 'B' THEN 1400
				                          ELSE 0                END) AS FROM_W_CHK
				                  , (CASE WHEN MATL_SUP_MTD_GP = 'A' THEN 1150
				                          WHEN MATL_SUP_MTD_GP = 'B' THEN 3000
				                          ELSE 3000             END) AS TO_W_CHK
				                  , YD_LOC_GP       AS COL_YD_LOC_GP
				                  , YD_STK_SKID_GP  AS COL_YD_STK_SKID_GP
				                  , YD_STK_COL_W_GP AS COL_W_GP
				               FROM TB_YD_STKCOL
				              WHERE DEL_YN = 'N'
				                AND YD_GP  = 'J'
				                AND SUBSTR(YD_STK_COL_GP,1,2) =  SUBSTR(:V_YD_SCH_CD,1,2)
				           ) SC
				         , (
				             SELECT SUBSTR(AA.YD_UP_WO_LOC,1,6)   AS COIL_YD_STK_COL_GP
				                  , SUBSTR(AA.YD_UP_WO_LOC,7,2)   AS COIL_YD_STK_BED_NO
				                  , AA.YD_UP_WO_LAYER             AS COIL_YD_STK_LYR_NO
				                  , CC.COIL_NO                    AS STL_NO
				                  , CASE WHEN CC.COIL_OUTDIA <= 1280 THEN 'A'
				                         WHEN CC.COIL_OUTDIA >  1930 THEN 'C'
				                         ELSE 'B' END             AS COIL_OUTDIA_GRP_GP
				                  , CC.COIL_W
				                  , CASE WHEN CC.COIL_W < 1601 THEN 'M'
				                         ELSE 'L' END             AS COIL_W_GP
				               FROM TB_YD_CRNSCH          AA
				                  , TB_YD_CRNWRKMTL       BB
				                  , USRPTA.TB_PT_COILCOMM CC
				              WHERE AA.YD_CRN_SCH_ID = BB.YD_CRN_SCH_ID
				                AND AA.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                AND BB.STL_NO        = CC.COIL_NO
				                AND AA.DEL_YN = 'N'
				                AND BB.DEL_YN = 'N'
				                AND ROWNUM = 1
				           ) CC
				         , (
				             SELECT X.*
				                  --  최종 권하위치로 SEQ 변경
				                  , CASE WHEN YD_SCH_CD NOT IN ('JHCV01LH')
				                          AND YD_STK_BED_SRCH_SEQ1 < (SELECT CC1
				                                                        FROM TBL_BF_CRNWRK)  THEN YD_STK_BED_SRCH_SEQ1  * 100
				                         ELSE YD_STK_BED_SRCH_SEQ1 END YD_STK_BED_SRCH_SEQ
				              FROM
				                 (
				                 SELECT A.YD_SCH_CD                AS YD_SCH_CD
				                      , A.YD_LOC_SRCH_RNG_REG_SNO  AS YD_LOC_SRCH_RNG_REG_SNO
				                      , A.YD_LOC_SRCH_RNG_SEQ      AS YD_LOC_SRCH_RNG_SEQ
				                      , B.YD_STK_BED_SRCH_SEQ      AS YD_STK_BED_SRCH_SEQ1

				                      , B.YD_STK_COL_GP            AS YD_STK_COL_GP
				                      , B.YD_STK_BED_NO            AS YD_STK_BED_NO
				                   FROM TB_YD_LOCSRCHRNG A
				                      , TB_YD_LOCSRCHBED B

				                  WHERE A.YD_LOC_SRCH_RNG_REG_SNO = B.YD_LOC_SRCH_RNG_REG_SNO
				                    AND A.YD_SCH_CD               = :V_YD_SCH_CD
				                    -- C동반입 HFL재(B3), SPM재(B4)
				                    AND A.YD_ROUTE_GP             = :V_YD_ROUTE_GP
				                    AND A.DEL_YN = 'N'
				                    AND B.DEL_YN = 'N'
				                --  ORDER BY YD_LOC_SRCH_RNG_SEQ,YD_STK_BED_SRCH_SEQ
				                 ) X
				              --   ORDER BY YD_LOC_SRCH_RNG_SEQ, YD_STK_BED_SRCH_SEQ
				          ) X1
				         , (SELECT ITEM1 AS TOT_YN FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP002' AND DEL_YN = 'N' AND ROWNUM = 1) YR
				     WHERE SL.YD_STK_COL_GP       = SC.YD_STK_COL_GP
				       AND SL.YD_STK_COL_GP       = X1.YD_STK_COL_GP
				       AND SL.YD_STK_BED_NO       = X1.YD_STK_BED_NO
				       AND SUBSTR(SL.YD_STK_COL_GP,1,2) = SUBSTR(CC.COIL_YD_STK_COL_GP,1,2) -- 야드동동일
				       AND SL.YD_STK_LYR_NO       IN ('001', '002')
				       AND SL.YD_STK_LYR_ACT_STAT = 'E'
				       AND SL.YD_STK_LYR_MTL_STAT = 'E'
				       AND SL.DEL_YN = 'N'
				       AND SL.YD_STK_BED_NO BETWEEN '00' AND '99'
				       -- 작업예약 TO위치 가이드 제외
				       AND SL.YD_STK_COL_GP||SL.YD_STK_BED_NO NOT IN ( SELECT NVL(YD_TO_LOC_GUIDE,'1') FROM TB_YD_WRKBOOK WHERE DEL_YN = 'N')
				--****  야드 통합여부  *****
				       AND 'Y' = CASE WHEN YR.TOT_YN = 'N' THEN
				                           CASE WHEN SC.COL_YD_LOC_GP = 'H' THEN 'Y' ELSE 'N' END
				                      ELSE 'Y' END
				--****  수입 1단적치 모드  *****
				       AND 'Y' = CASE WHEN 'Y' = (SELECT ITEM1
				                                    FROM TB_YD_RULE
				                                   WHERE REPR_CD_GP = 'APP020'
				                                     AND CD_GP = 'J'
				                                     AND ITEM  = SUBSTR(X1.YD_SCH_CD, 2, 1))
				                       AND SL.YD_STK_LYR_NO = '002'
				                      THEN 'N'
				                      ELSE 'Y'
				                 END
				 )
				, TO_LOC_DATA_TABLE AS (
				-- TO위치 코일정보 SELECT
				SELECT A.PRIOR4
				     , A.STL_NO
				     , A.YD_SCH_CD
				     , CASE WHEN SUBSTR(A.YD_SCH_CD, 3, 6) IN ('YD03UH', 'YD04UH') THEN 'M' -- 반납/반송 2단적치기준 제외용
				            ELSE NVL(B.YD_EQP_WRK_MODE2,'A')
				       END AS YD_EQP_WRK_MODE2
				     , A.TAG_YD_STK_COL_GP
				     , A.TAG_YD_STK_BED_NO
				     , A.TAG_YD_STK_LYR_NO
				     , A.TAG_LEFT_BED
				     , A.TAG_LEFT_LAYER
				     , A.YD_LOC_SRCH_RNG_SEQ
				     , A.YD_STK_BED_SRCH_SEQ
				     , A.COL_OUTDIA_GRP_GP
				     , A.COL_W_GP
				     , A.COL_YD_LOC_GP
				     , A.COL_YD_STK_SKID_GP
				     , A.COIL_OUTDIA_GRP_GP
				     , A.COIL_W_GP
				     , A.CHK_SKID_GP
				     , (SELECT MAX(YD_STK_BED_NO)
				          FROM TB_YD_STKLYR
				         WHERE YD_STK_COL_GP = A.TAG_YD_STK_COL_GP AND ROWNUM = 1) AS COL_MAX_BED_NO
				     , (SELECT YD_STK_LYR_ACT_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_LEFT_ACTIVE_STAT
				     , (SELECT YD_STK_LYR_MTL_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_LEFT_LAYER_STAT
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_LEFT_STL_NO
				     , A.TAG_RIGHT_BED
				     , A.TAG_RIGHT_LAYER
				     , (SELECT YD_STK_LYR_ACT_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_RIGHT_ACTIVE_STAT
				     , (SELECT YD_STK_LYR_MTL_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_RIGHT_LAYER_STAT
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_RIGHT_STL_NO
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = '002'
				           AND B.DEL_YN = 'N') AS TAG_2DAN_LEFT_STL_NO
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_YD_STK_BED_NO
				           AND B.YD_STK_LYR_NO  = '002'
				           AND B.DEL_YN = 'N') AS TAG_2DAN_RIGHT_STL_NO
				  FROM TO_LOC_TABLE A
				     , (SELECT DECODE(ITEM1, 'Y', 'A', 'M') AS YD_EQP_WRK_MODE2 FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP100' AND CD_GP = :V_YD_EQP_ID) B
				), TO_LOC_DATA_COMP_TABLE AS (
				--*--*--*--*--*--*-- 적치가능위치
				SELECT *
				  FROM (
				          SELECT
				              KK.*
				          FROM
				               (
				                SELECT K.*
				                     , C.COIL_NO      AS C_COIL_NO
				                     , C.COIL_T       AS C_THICK
				                     , C.COIL_W       AS C_WIDTH
				                     , C.COIL_WT      AS C_WEIGTH
				                     , C.COIL_OUTDIA  AS C_OUTDIA
				                     , C.CURR_PROG_CD AS C_PROG_CD
				                     , C.ORD_NO       AS C_ORD_NO     -- 주문번호
				                     , C.ORD_DTL      AS C_ORD_DTL    -- 주문행번
				                     , C.DEMANDER_CD  AS C_DEMANDER_CD-- 수요가코드
				                     , (CASE WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
				                             WHEN C.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             ELSE '' END
				                       ) AS C_YEOJAE_CAUSE_CD
				                     , TRUNC((SYSDATE - C.HRMILL_CMPL_DT )*24)             AS C_HOT_COIL_MIN
				                     , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
				                            WHEN C.COIL_OUTDIA >  1930 THEN 'C'
				                            ELSE 'B' END  AS C_COIL_OUTDIA_GP
				                     , CASE WHEN C.COIL_W < 1601     THEN 'M' ELSE 'L' END AS C_COIL_W_GP
				                     , L.COIL_NO      AS L_COIL_NO
				                     , L.COIL_T       AS L_THICK
				                     , L.COIL_W       AS L_WIDTH
				                     , L.COIL_WT      AS L_WEIGTH
				                     , L.COIL_OUTDIA  AS L_OUTDIA
				                     , L.CURR_PROG_CD AS L_PROG_CD
				                     , L.ORD_NO       AS L_ORD_NO     -- 주문번호
				                     , L.ORD_DTL      AS L_ORD_DTL    -- 주문행번
				                     , L.DEMANDER_CD  AS L_DEMANDER_CD-- 수요가코드
				                     , (CASE WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
				                             WHEN L.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             ELSE '' END
				                       ) AS L_YEOJAE_CAUSE_CD
				                     , TRUNC((SYSDATE - L.HRMILL_CMPL_DT )*24) AS L_HOT_COIL_MIN
				                     , R.COIL_NO      AS R_COIL_NO
				                     , R.COIL_W       AS R_WIDTH
				                     , R.COIL_T       AS R_THICK
				                     , R.COIL_WT      AS R_WEIGTH
				                     , R.COIL_OUTDIA  AS R_OUTDIA
				                     , R.CURR_PROG_CD AS R_PROG_CD
				                     , R.ORD_NO       AS R_ORD_NO     -- 주문번호
				                     , R.ORD_DTL      AS R_ORD_DTL    -- 주문행번
				                     , R.DEMANDER_CD  AS R_DEMANDER_CD-- 수요가코드
				                     , (CASE WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
				                             WHEN R.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             ELSE '' END
				                       ) AS R_YEOJAE_CAUSE_CD
				                     , TRUNC((SYSDATE - R.HRMILL_CMPL_DT )*24) R_HOT_COIL_MIN
				                     , L.ISHOT        AS L_ISHOT
				                     , R.ISHOT        AS R_ISHOT
				                     , C.ISHOT        AS C_ISHOT
				                  FROM TO_LOC_DATA_TABLE K
				                     , (SELECT 1 T_ROW, A.*
				                             , CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) 
				                                    <  (SELECT DTL_ITEM1 
				                                          FROM TB_YD_RULE
				                                         WHERE REPR_CD_GP ='APP103'
				                                           AND CD_GP = SUBSTR(:V_YD_SCH_CD,2,1)
				                                           AND ITEM  = TO_NUMBER(TO_CHAR(SYSDATE,'MM')))
				                                    THEN 'TRUE'
				                                    ELSE 'FALSE'
				                                     END  AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
				                          FROM USRPTA.TB_PT_COILCOMM  A ) C  --대상코일
				                     , (SELECT 1 T_ROW, A.*
				                             , CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) 
				                                    <  (SELECT DTL_ITEM1 
				                                          FROM TB_YD_RULE
				                                         WHERE REPR_CD_GP ='APP103'
				                                           AND CD_GP = SUBSTR(:V_YD_SCH_CD,2,1)
				                                           AND ITEM  = TO_NUMBER(TO_CHAR(SYSDATE,'MM')))
				                                    THEN 'TRUE'
				                                    ELSE 'FALSE'
				                                     END  AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
				                          FROM USRPTA.TB_PT_COILCOMM  A ) L  --하단LEFT
				                     , (SELECT 1 T_ROW, A.*
				                             , CASE WHEN TO_NUMBER(TRUNC((SYSDATE - NVL(HRMILL_CMPL_DT,COIL_CREATE_DDTT)) * 24)) 
				                                    <  (SELECT DTL_ITEM1 
				                                          FROM TB_YD_RULE
				                                         WHERE REPR_CD_GP ='APP103'
				                                           AND CD_GP = SUBSTR(:V_YD_SCH_CD,2,1)
				                                           AND ITEM  = TO_NUMBER(TO_CHAR(SYSDATE,'MM')))
				                                    THEN 'TRUE'
				                                    ELSE 'FALSE'
				                                     END  AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
				                          FROM USRPTA.TB_PT_COILCOMM  A ) R  --하단RIGHT
				                 WHERE K.STL_NO           = C.COIL_NO(+)
				                   AND K.TAG_LEFT_STL_NO  = L.COIL_NO(+)
				                   AND K.TAG_RIGHT_STL_NO = R.COIL_NO(+)
				                   --짱구코일 2단제외
				--                   AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND JJANG_GP = 'Y'                      THEN 0
				--                                ELSE 1 END
				--                   --2단일 경우 좌우 적치 상태 CHECK
				                   AND 'Y' = CASE WHEN TAG_YD_STK_LYR_NO = '002'
				                                       AND (TAG_LEFT_ACTIVE_STAT != 'E' OR TAG_RIGHT_ACTIVE_STAT != 'E'
				                                       OR TAG_LEFT_LAYER_STAT  != 'C' OR TAG_RIGHT_LAYER_STAT  != 'C') THEN 'N'
				                                  ELSE 'Y' END
				--                   --1단일 경우 좌우 상단 적치 상태 CHECK
				                   AND 'Y' = CASE WHEN K.TAG_YD_STK_LYR_NO = '001' AND (TAG_2DAN_LEFT_STL_NO IS NOT NULL OR TAG_2DAN_RIGHT_STL_NO IS NOT NULL) THEN 'N'
				                                  ELSE 'Y' END

				               ) KK
				                --무인크레인 이동가능 여부 CHECK
				--         WHERE ( TAG_YD_STK_COL_GP IN (SELECT R.ITEM
				--                                         FROM TB_YD_RULE      R
				--                                            , AUTO_CR_TABLE   A
				--                                        WHERE R.REPR_CD_GP = 'CR0001'
				--                                          AND R.CD_GP      = A.EQUIP_GP
				--                                          AND R.DEL_YN     = 'N')
				--               )
				       )
				)
				--*--*--*--*--*--*-- 평점
				SELECT G.*
				      , CASE  -- 1단이면서 소재인 경우
				            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
				            THEN CASE WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌우) 진도코드
				                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌단) 진도코드
				                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우단) 진도코드
				                      WHEN L_COIL_NO  IS NULL     AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
				                      WHEN R_COIL_NO  IS NULL     AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED
				                      ELSE '7' END
				             -- 2단이면서 소재인 경우
				            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
				            THEN CASE WHEN L_WB        = 'Y'      OR  R_WB       = 'Y'      THEN '9' --좌하단우하단 작업 예약
				                      WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌하단우하단) 진도코드
				                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌하단) 진도코드
				                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우하단) 진도코드
				                      ELSE '2' END
				             ELSE '7' END GRADE
				  FROM (SELECT A.*
				             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
				                  FROM TB_YD_WRKBOOK    A1
				                     , TB_YD_WRKBOOKMTL B1
				                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                   AND A1.DEL_YN      = 'N'
				                   AND B1.DEL_YN      = 'N'
				                   AND B1.STL_NO      = A.C_COIL_NO
				               ) C_WB
				             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
				                  FROM TB_YD_WRKBOOK    A1
				                     , TB_YD_WRKBOOKMTL B1
				                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                   AND A1.DEL_YN      = 'N'
				                   AND B1.DEL_YN      = 'N'
				                   AND B1.STL_NO      = A.L_COIL_NO
				               ) L_WB
				             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
				                  FROM TB_YD_WRKBOOK    A1
				                     , TB_YD_WRKBOOKMTL B1
				                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                   AND A1.DEL_YN      = 'N'
				                   AND B1.DEL_YN      = 'N'
				                   AND B1.STL_NO      = A.R_COIL_NO
				               ) R_WB
				          FROM TO_LOC_DATA_COMP_TABLE A
				       ) G
				 ORDER BY YD_LOC_SRCH_RNG_SEQ
				        , YD_STK_BED_SRCH_SEQ
				        , GRADE
				        , TAG_YD_STK_BED_NO
				        , PRIOR4 DESC
				*/
				
				jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocPrimaryWorkCV", logId, mthdNm, "수입 적치가능한 베드 조회");
			}

			if (jsResult.size() <= 0) {
				sLogMsg = "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, sLogMsg, "SL");

				if ("Y".equals(schLogYn)) {
					sSchLogContents = "[getYdToLocPrimaryWorkCV] 대상코일위치검색실패:"+ sStlNo+" LOG :"+"\r\n";
					this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
				}
				
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			} else {
				
				/****************************
				 *  log용
				 *****************************/
				JDTORecord jrResult1 = JDTORecordFactory.getInstance().create();
				String  sLocAbleMsg = "";
				for (int i = 1; i <= jsResult.size(); i++) {
					jsResult.absolute(i);
					jrResult1  = jsResult.getRecord();
								
					ydStkColGp 	= commUtils.trim(jrResult1.getFieldString("TAG_YD_STK_COL_GP"  ));
					ydStkBedNo 	= commUtils.trim(jrResult1.getFieldString("TAG_YD_STK_BED_NO"  ));
					ydStkLyrNo 	= commUtils.trim(jrResult1.getFieldString("TAG_YD_STK_LYR_NO"  ));
					sGrade 		= commUtils.nvl (jrResult1.getFieldString("GRADE"),"9");
					
					sLocAbleMsg  = sLocAbleMsg + "적치가능대상코일위치="+" 열:"+ ydStkColGp+" 베드:"+ydStkBedNo+" 단:"+ydStkLyrNo+" 평점:"+sGrade+"\r\n";
				}		
				if ("Y".equals(schLogYn)) {
					sSchLogContents = "[getYdToLocPrimaryWorkCV] 대상코일위치검색:"+ sStlNo+"\r\n" + sLocAbleMsg;
					this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
				}
			}
		    //----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 SCH RULL 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord  jrRuleConst = this.toLocRuleConst(logId, mthdNm);
			//----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 LOGIC 적용여부 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrLocRuleApply = this.toLocRuleApply(logId, mthdNm);
			commUtils.printParam(logId, jrLocRuleApply);
			
			JDTORecord jrResult	= JDTORecordFactory.getInstance().create();
			JDTORecord jrSndMsg = JDTORecordFactory.getInstance().create();
			

			String sLocAbleRtn     = "";
			String sLocAbleRtnMsg  = "";
			boolean sTcLocAbleYn   = false;
			
			for (int i = 1; i <= jsResult.size(); i++) {
				jsResult.absolute(i);
				jrResult  = jsResult.getRecord();
							
				sGrade 		= commUtils.nvl (jrResult.getFieldString("GRADE"),"9");
				ydStkColGp 	= commUtils.trim(jrResult.getFieldString("TAG_YD_STK_COL_GP"  ));
				ydStkBedNo 	= commUtils.trim(jrResult.getFieldString("TAG_YD_STK_BED_NO"  ));
				ydStkLyrNo 	= commUtils.trim(jrResult.getFieldString("TAG_YD_STK_LYR_NO"  ));	
	
				/******************* 
				 * 적치가능 check
				 ******************/
				JDTORecord jrLocAbleRtn = this.toLocAbleCheck(logId, mthdNm, jrResult, jrRuleConst, jrLocRuleApply);
			//
				sLocAbleRtn    = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
				sLocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
				if (sLocAbleRtnMsg.length() > 0 ) {
					sDBLogMsg = sDBLogMsg + sLocAbleRtnMsg +"\r\n";
				}
				if ("1".equals(sLocAbleRtn)) {
					sLogMsg = ydStkColGp + ydStkBedNo + ydStkLyrNo+"  적치가능 위치평점:"+ sGrade;
					commUtils.printLog(logId, sLogMsg, "SL");				
				    //적치가능 
					sRtnBedDan = ydStkColGp + ydStkBedNo + ydStkLyrNo;
					
	    			break;
				}
			}			
			
			if (sRtnBedDan.length() < 10) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 TO위치 결정 실패 ";
				commUtils.printLog(logId, sLogMsg, "SL");

				if ("Y".equals(schLogYn)) {
					sSchLogContents = "[getYdToLocPrimaryWorkCV] 대상코일선택실패:"+ sStlNo+"\r\n"+" LOG :"+"\r\n" + sDBLogMsg;
					this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
				}
				
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;				
			}
			
			
			if ("Y".equals(schLogYn)) {
				sSchLogContents = "[getYdToLocPrimaryWorkCV] 대상코일선택:"+ sStlNo+"  선택위치:"+ sRtnBedDan +"  평점:" + sGrade +"\r\n"+" LOG :"+"\r\n" + sDBLogMsg;
				this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
			}
			
			//202302 YYS  권하위치 검색 쿼리에서 차량진입 상태값 체크하여 30스판보다 작은건 제외 적용./////////////////////////////////////////////////////////////////////////////////
//			String sAPP010_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","010"); 
//			commUtils.printLog(logId, "B/C 동 스크랩차량 들어왔을때 수입 권하위치 가능 지역으로 변경 : " + sAPP010_YN, "SL");
//			
//			if ("Y".equals(reTolocYn) && "Y".equals(sAPP010_YN) ) { //권하위치 재조회 설정 이면
//				jrRtn.setField("RTN_CD" , "1");	
//				jrRtn.setField("YD_DN_WO_LOC"	, sRtnBedDan.substring(0,8));
//				jrRtn.setField("YD_DN_WO_LAYER"	, sRtnBedDan.substring(8));
//				jrRtn.setField("RTN_MSG", sLogMsg);
//				return jrRtn;
//			}
			//////////////////////////////////////////////////////////////////////////////////
			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = commUtils.getParam(logId, mthdNm, sModifier);

			jrSetLoc.setField("YD_CRN_SCH_ID"	, ydCrnSchId); 
			jrSetLoc.setField("YD_EQP_ID"		, ydEqpId);	 
			jrSetLoc.setField("YD_SCH_CD"		, ydSchCd);	 
			jrSetLoc.setField("YD_UP_WO_LOC"	, ydUpWoLoc); 
			jrSetLoc.setField("YD_UP_WO_LAYER"	, ydUpWoLayer);	 
			jrSetLoc.setField("YD_DN_WO_LOC"	, sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER"	, sRtnBedDan.substring(8));
			jrSetLoc.setField("YD_WBOOK_ID"		, ydWbookId); 
				
			JDTORecord jrRcvRtn = this.toLocUpdCrnSch(logId,mthdNm, jrSetLoc, jrCrnSch  );
			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
			jrRtn.setField("TC_MOVE_YN", sTcMoveYn);
			
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
	    }//end of try~catch				

	}      
	/**	
	 *      [A] 오퍼레이션명 : 권하위치 중복 제어(YDYDJ556)
	 *      권상시 발생
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYDYDJ556(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "권하위치중복 ReSch [CCoilSchSeEJB.rcvYDYDJ556] < " + rcvMsg.getResultMsg();

		String logId		= rcvMsg.getResultCode();
		JDTORecord jrRtn	= JDTORecordFactory.getInstance().create();	//전문 Return

		try {

			commUtils.printLog(logId, mthdNm, "S+");

	    	//수신항목 변수 저장
			String msgId	= commUtils.nvl(commUtils.getMsgId(rcvMsg),"YDYDJ556");	// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sModifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	// 수정자(Backup Only)
			String ydCrnSchId = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"));//reSch할 코일번호
			
			if ("".equals(sModifier)) { sModifier = msgId; }

			
			JDTORecord jrRcvRtn	= JDTORecordFactory.getInstance().create();	//전문 Return
			String sLogMsg = "";
			String rtnMsg  = "";
			String rtnCd   = "";
			
			
			JDTORecord jrParam	= commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchId	);

			/**************************************************
			 * 1.크레인스케쥴 조회
			 **************************************************/
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdLocDupW
			SELECT A1.YD_CRN_SCH_ID
			     , A1.YD_WBOOK_ID
			     , A1.YD_EQP_ID
			     , A1.YD_SCH_CD
			     , A1.YD_SCH_PRIOR
			     , A1.YD_TO_LOC_DCSN_MTD
			     , A1.YD_TO_LOC_GUIDE
			     , A1.YD_EQP_WRK_SH
			     , A1.YD_DN_WO_LOC 
			  FROM TB_YD_CRNSCH A1
			 WHERE A1.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			   AND A1.DEL_YN = 'N'  
			   AND A1.YD_WRK_PROG_STAT = 'W'    
			*/
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdLocDupW", logId, mthdNm, "중복 대상 크레인스케쥴 조회");

			if (jsCrnSch.size() < 1) {
				jrRtn.setField("RTN_CD" 		, "1");	
				jrRtn.setField("RTN_MSG"		, rtnMsg);	
				//전문 송신여부에 사용
				commUtils.printLog(logId, "오류:TO 저장위치 등록  오류", "SL");	
				return jrRtn;
			}	
			JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
			/**********************************************************
			* 4.TO위치재결정
  			**********************************************************/
			commUtils.printLog(logId, "TO위치결정재시작:"+ ydCrnSchId, "SL");			

			JDTORecord jrParamSet = commUtils.getParam(logId, mthdNm, sModifier);
			String ydSchCd 			= commUtils.trim(jrCrnSch.getFieldString("YD_SCH_CD"));				//야드스케쥴코드
			String ydWbookId		= commUtils.trim(jrCrnSch.getFieldString("YD_WBOOK_ID"));				//야드스케쥴코드
			String ydEqpId 			= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));				//야드설비ID
			String ydSchPrior 		= commUtils.trim(jrCrnSch.getFieldString("YD_SCH_PRIOR"));			//야드스케쥴우선순위
			String ydToLocDcsnMtd 	= commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"));	//야드To위치결정방법
			String ydToLocGuide 	= commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_GUIDE"));		//야드To위치Guide
			
			jrParamSet.setField("YD_CRN_SCH_ID"  		, ydCrnSchId  ); 		//작업예약
			jrParamSet.setField("YD_WBOOK_ID"  			, ydWbookId  ); 		//작업예약
			jrParamSet.setField("YD_SCH_CD"  			, ydSchCd  ); 			//야드스케쥴코드
			jrParamSet.setField("YD_EQP_ID"  			, ydEqpId  ); 			//야드설비ID
			jrParamSet.setField("YD_SCH_PRIOR"  		, ydSchPrior  ); 		//야드스케쥴우선순위
			jrParamSet.setField("YD_TO_LOC_DCSN_MTD"  	, ydToLocDcsnMtd  ); 	//야드To위치결정방법
			jrParamSet.setField("YD_TO_LOC_GUIDE"  		, ydToLocGuide  ); 		//야드To위치Guide
			
			JDTORecord jrSchToLoc = this.crnSchToLocRe(logId, mthdNm, jrParamSet);
			rtnCd	= commUtils.nvl(jrSchToLoc.getFieldString("RTN_CD"),"0");
			rtnMsg  = commUtils.nvl(jrSchToLoc.getFieldString("RTN_MSG"),"");
			
			if("0".equals(rtnCd)){
				m_ctx.setRollbackOnly();
				jrRtn.setField("RTN_CD"  , "0");	
				jrRtn.setField("RTN_MSG" , rtnMsg);	
				//전문 송신여부에 사용
				commUtils.printLog(logId, "오류:TO 저장위치 재등록  오류", "SL");	
				return jrRtn;
			}

			commUtils.printLog(logId, mthdNm , "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
     * 오퍼레이션명 : 2열연 COIL YARD - TO 위치 재결정
     * crnSchToLoc: YD_WBOOK_ID건,crnSchToLocRe: YD_CRN_SCH_ID건
     * @param  inRecord, recGetCrnWrkMtl, rsResultCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public JDTORecord crnSchToLocRe (String logId, String mthdNms, JDTORecord jrParamSet )throws JDTOException{
    	String mthdNm = "TO 위치 재결정[CCoilSchSeEJB.crnSchToLocRe] < " + mthdNms;
    	JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();	//전문 Return
    	JDTORecord jrRcvRtn = JDTORecordFactory.getInstance().create();	//전문 Return
    
    	try{
        	commUtils.printLog(logId, mthdNm, "S+");

    		/**********************************************************
			* 이로직 수정시 반드시 crnSchToLoc 메소드 점검
			**********************************************************/             	
        	int intRtnVal  = 0;
        	String sLogMsg = "";  
        	//-------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------
			//파라미터 Null Check
        	String ydCrnSchId	= commUtils.trim(jrParamSet.getFieldString("YD_CRN_SCH_ID" ));	
        	String ydWbookId	= commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_ID" ));	
        	String ydEqpId 		= commUtils.trim(jrParamSet.getFieldString("YD_EQP_ID"   ));	
        	String sModifier	= commUtils.trim(jrParamSet.getFieldString("MODIFIER"   ));	
        	
        	JDTORecord jrParam	= commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchId	);
			jrParam.setField("YD_WBOOK_ID"   , ydWbookId	);
			jrParam.setField("YD_EQP_ID"     , ydEqpId	    );
        	
        	/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdWrkbook 
        	SELECT YD_WBOOK_ID      AS YD_WBOOK_ID
        	     , REGISTER         AS REGISTER
        	     ,:
        	     , YD_CAR_USE_GP
        	     , TRN_EQP_CD       AS TRN_EQP_CD
        	     , CAR_NO           AS CAR_NO
        	     , CARD_NO          AS CARD_NO
        	     , (SELECT STL_NO 
        	          FROM TB_YD_WRKBOOKMTL 
        	         WHERE YD_WBOOK_ID = A.YD_WBOOK_ID 
        	           AND DEL_YN = 'N' AND ROWNUM = 1) AS STL_NO
        	     , (SELECT ITEM1 
        	          FROM TB_YD_RULE
        	         WHERE REPR_CD_GP = 'APP010'
        	           AND DEL_YN = 'N') AS SCHLOG_YN
        	  FROM TB_YD_WRKBOOK A
        	 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			 */
        	JDTORecordSet jsWbook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdWrkbook", logId, mthdNm, "작업예약 조회"); 
	    	
	    	if (jsWbook.size() < 1) {
				
	    		sLogMsg = "오류:작업 예약 조회 시 오류";
				commUtils.printLog(logId, sLogMsg + " ydWbookId: " + ydWbookId, "SL");
				
				jrRtn.setField("RTN_CD" , "0");	
    			jrRtn.setField("RTN_MSG", sLogMsg);
    			return jrRtn;
			}			
			
	    	jsWbook.absolute(1);
			JDTORecord jrWbook = JDTORecordFactory.getInstance().create();
			jrWbook.setRecord(jsWbook.getRecord());
			jrWbook.setField("MODIFIER", sModifier);
			
			String ydSchCd 	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));
			String sStlNo 	= commUtils.trim(jrWbook.getFieldString("STL_NO"));   // 대표 재료만 스크랩여부 판단
			
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄 조회
			//-------------------------------------------------------------------------------------------------------------
			JDTORecordSet jsCrnSch = JDTORecordFactory.getInstance().createRecordSet("");


			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnSchByWBookIdRe 
			SELECT A.YD_EQP_ID               AS YD_EQP_ID                       
			     :        
			     , D.HYSCO_TRANS_GP 	-- HYSCO이송수단,
			     , D.COOL_METHOD 	    -- 냉각방법,
			     , DECODE(D.CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',D.CURR_PROG_CD) AS CURR_PROG_CD
			     , D.RETURN_GP
			     , C.STL_NO       
			     , NVL(SUM(D.COIL_WT)   OVER (ORDER BY C.YD_STK_LYR_NO DESC),0) AS SUM_MTL_WT      
				 , NVL(SUM(D.COIL_T)    OVER (ORDER BY C.YD_STK_LYR_NO DESC),0) AS SUM_MTL_T   
				 , NVL(MAX(D.COIL_W)    OVER (ORDER BY C.YD_STK_LYR_NO DESC),0) AS MAX_MTL_W 
				 , NVL(MAX(D.COIL_LEN)  OVER (ORDER BY C.YD_STK_LYR_NO DESC),0) AS MAX_MTL_L 
				 , COUNT(D.COIL_NO) OVER (ORDER BY C.YD_STK_LYR_NO DESC) AS SH_CNT 
			  FROM TB_YD_EQP  A                                               
			     , TB_YD_CRNSCH B                                               
			     , TB_YD_CRNWRKMTL C                                               
			     , USRPTA.TB_PT_COILCOMM  D  
			 WHERE B.YD_EQP_ID      = A.YD_EQP_ID  
			   AND B.YD_CRN_SCH_ID  = C.YD_CRN_SCH_ID  
			   AND C.STL_NO         = D.COIL_NO
			   AND B.YD_CRN_SCH_ID  = :V_YD_CRN_SCH_ID
			   AND B.DEL_YN = 'N'
			   AND C.DEL_YN = 'N'
			 ORDER BY B.YD_CRN_SCH_ID
			 */
			
			jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnSchByWBookIdRe", logId, mthdNm, "크레인스케줄 조회"); 

			
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄의 권하지시위치 결정: crnSchToLoc 로직과 동일
			//-------------------------------------------------------------------------------------------------------------
			JDTORecord jrCrnSch 	= JDTORecordFactory.getInstance().create();

   			String ydStkColGp 		= "";	
			String ydStkBedNo		= "";	
			String ydStkLyrNo		= "";	
    		String sCrnToLocDcsnMtd = "";
    		String sToLocGuide 		= "";
    		String rtnCd 	        = "";
    		String ydDnWoLoc        = "";
    		

    		String sTcMoveYn        = "N";
    		String sToLocGuideCol   = "";

			for (int Loop_i = 1; Loop_i <= jsCrnSch.size(); Loop_i++) {
		    	
				jsCrnSch.absolute(Loop_i);
        		jrCrnSch = jsCrnSch.getRecord();
        		jrCrnSch.setField("MODIFIER", sModifier);
		    	
        		//크레인스케줄Data저장
        		ydCrnSchId 		 = commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));
        		sCrnToLocDcsnMtd = commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"));
        		sToLocGuide 	 = commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_GUIDE"));
        		sStlNo			 = commUtils.trim(jrCrnSch.getFieldString("STL_NO"));
        		ydDnWoLoc		 = commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC"));
        		
        		if(sToLocGuide.length() >= 6) {
        			sToLocGuideCol = sToLocGuide.substring(0, 6);
        		} else {
        			sToLocGuideCol ="XXXXXX";
        		}
        		
        		if ("KD05LH".equals(ydSchCd.substring(2,8))) {       				
            		/**********************************************************
    				* Scrap 인 경우
    				**********************************************************/            		
       				sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은  Scrap To위치 재결정 시작";
        			commUtils.printLog(logId, sLogMsg, "SL");
        			
        			jrRcvRtn = this.toLocScrap(logId, mthdNm, jrWbook, jrCrnSch);
        			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
        			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));

        		} else if ("W".equals(sCrnToLocDcsnMtd)) {
            		/**********************************************************
    				* 보조작업인 경우 TO위치 결정 (일반적치대로....)
    				**********************************************************/            		
            		sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 보조작업 스케줄의  To위치 재결정 시작";
        			commUtils.printLog(logId, sLogMsg, "SL");
        			
        			jrRcvRtn = this.toLocDummy(logId, mthdNm, jrWbook, jrCrnSch);
        			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
        			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));

            	} else if  ("GF01UH".equals(ydSchCd.substring(2,8)) ||"GF01LH".equals(ydSchCd.substring(2,8))) {       			
            		/**********************************************************
    				* 지포장장보급
    				* -- 지포장장은 설비와 동일 하게 검색
    				**********************************************************/            		
            		sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 지포장장보급 스케줄의  To위치 재결정 시작";
        			commUtils.printLog(logId, sLogMsg, "SL");
        			
        			jrRcvRtn = this.toLocGF(logId, mthdNm, jrWbook, jrCrnSch);
        			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
        			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));

            	} else if ((("S".equals(sCrnToLocDcsnMtd)) && ( "KE".equals(ydSchCd.substring(2,4)) || "KD".equals(ydSchCd.substring(2,4))) && ("U".equals(ydSchCd.substring(6,7))) ) 
       			             ||  
       			          ( ("S".equals(sCrnToLocDcsnMtd)) && ( "FE".equals(ydSchCd.substring(2,4)) || "FD".equals(ydSchCd.substring(2,4))) && ("U".equals(ydSchCd.substring(6,7))) 
       			    		&& (("C".equals(ydSchCd.substring(1,2)))||("G".equals(ydSchCd.substring(1,2)))))
       			    ) {
 	           		/**********************************************************
 	   				* 주작업 HFL 설비/SPM설비보급 (결속장 제외)
 	   				**********************************************************/
            		sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 주작업 HFL 설비/SPM설비보급 (결속장 제외) 위 스케줄의  To위치 재결정 안함";
 	       			commUtils.printLog(logId, sLogMsg, "SL");
 	       			
 	       			sLogMsg = "크레인스케줄 To위치 재기동 등록 안함!!";
    				commUtils.printLog(logId, sLogMsg, "SL");
    				jrRtn.setField("RTN_CD"		, "0");
    				jrRtn.setField("RTN_MSG"	, sLogMsg);
        			return jrRtn;

 
            	} else if ((("S".equals(sCrnToLocDcsnMtd)) && ("FE".equals(ydSchCd.substring(2,4))) && ("U".equals(ydSchCd.substring(6,7))) 
      			    		&& ("B".equals(ydSchCd.substring(1,2)) || "D".equals(ydSchCd.substring(1,2)) || "F".equals(ydSchCd.substring(1,2))))
      			     ) {
	           		/**********************************************************
	   				* 주작업 HFL 설비보급 (결속장)
	   				**********************************************************/
            		sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 주작업 HFL 설비보급 (결속장 ) 위 스케줄의  To위치 재결정 시작";
	       			commUtils.printLog(logId, sLogMsg, "SL");
	       			jrParam.setField("TO_LOC_GUIDE", 	sToLocGuide);
	       			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkLayByHFL
	       			SELECT * 
	       			  FROM
	       			        (
	       			        SELECT YD_STK_COL_GP
	       			             , YD_STK_BED_NO
	       			             , YD_STK_LYR_NO
	       			             , CHK
	       			             , CASE WHEN CHK <YD_STK_BED_NO AND YD_STK_COL_GP IN('JFFE02','JDFE03','JBFE05') THEN YD_STK_BED_NO-CHK 
	       			                    WHEN CHK>=YD_STK_BED_NO AND YD_STK_COL_GP IN('JFFE02','JDFE03','JBFE05') THEN CHK+YD_STK_BED_NO
	       			                    ELSE TO_NUMBER(YD_STK_BED_NO) END  AS CHK2
	       			         FROM (
	       			              SELECT A.YD_STK_COL_GP
	       			                   , A.YD_STK_BED_NO
	       			                   , A.YD_STK_LYR_NO
	       			                   , (SELECT MAX(YD_STK_BED_NO) 
	       			                        FROM TB_YD_STKLYR 
	       			                       WHERE YD_STK_COL_GP IN('JFFE02','JDFE03','JBFE05')  
	       			                         AND YD_STK_LYR_MTL_STAT <> 'E') AS CHK
	       			                FROM TB_YD_STKLYR A
	       			               WHERE A.YD_STK_COL_GP = :V_TO_LOC_GUIDE
	       			                 AND A.DEL_YN = 'N'
	       			                 AND A.YD_STK_LYR_ACT_STAT = 'E'
	       			                 AND A.YD_STK_LYR_MTL_STAT = 'E'
	       			                 AND A.STL_NO IS NULL
	       			                 AND A.YD_STK_BED_NO <> '00'
	       			             ) X
	       			         ORDER BY CHK2 ,YD_STK_LYR_NO ,YD_STK_BED_NO
	       			       ) 
	       			 WHERE ROWNUM = 1 
	       			*/
	       			JDTORecordSet jsHflInfo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkLayByHFL", logId, mthdNm, "결속장 조회");
	       			if (jsHflInfo.size() <= 0 ) {
	        			sLogMsg = " :결속장 조회 실패!";
	    				commUtils.printLog(logId, sLogMsg, "SL");
	       			} else {	
		       			jsHflInfo.first();
						JDTORecord jrHflInfo = jsHflInfo.getRecord();
		       			
						ydStkColGp 	= commUtils.trim(jrHflInfo.getFieldString("YD_STK_COL_GP"));//적치열
						ydStkBedNo	= commUtils.trim(jrHflInfo.getFieldString("YD_STK_BED_NO"));//적치베드
						ydStkLyrNo  = commUtils.trim(jrHflInfo.getFieldString("YD_STK_LYR_NO"));//적치단
						
						jrWbook.setField("YD_TO_LOC_GUIDE", ydStkColGp + ydStkBedNo + ydStkLyrNo);
	
						sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 결속장 [TO위치 가이드 결정  YD_TO_LOC_GUIDE : "+ydStkColGp + ydStkBedNo + ydStkLyrNo+"]";
	    				commUtils.printLog(logId, sLogMsg, "SL");
	    				
	    				jrRcvRtn = this.toLocUser(logId, mthdNm, jrWbook, jrCrnSch);
	    				jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
	    				jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));	       			
	       			}	
            	} else if (  "S".equals(sCrnToLocDcsnMtd) &&("KD02LH".equals(ydSchCd.substring(2,8)))) {    
	           		/**********************************************************
	   				* 주작업 SPM 재작업 보급   
	   				**********************************************************/
	           		sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 설비위 재작업 보급 스케줄의  To위치 재결정 안함";
	       			commUtils.printLog(logId, sLogMsg, "SL");
	       			
 	       			sLogMsg = "크레인스케줄 To위치 재기동 등록 안함!!";
    				commUtils.printLog(logId, sLogMsg, "SL");
    				jrRtn.setField("RTN_CD"		, "0");
    				jrRtn.setField("RTN_MSG"	, sLogMsg);
        			return jrRtn;
	       			
            	} else if (( "PT".equals(ydSchCd.substring(2,4)) || "TR".equals(ydSchCd.substring(2,4))) 
            			  && ("U".equals(ydSchCd.substring(6,7)))) {  // 출하
	           		sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 출하 스케줄의  To위치 재결정 안함";
	       			commUtils.printLog(logId, sLogMsg, "SL");
 	       			
            		sLogMsg = "크레인스케줄 To위치 재기동 등록 안함!!";
    				commUtils.printLog(logId, sLogMsg, "SL");
    				jrRtn.setField("RTN_CD"		, "0");
    				jrRtn.setField("RTN_MSG"	, sLogMsg);
        			return jrRtn;            		
            		
		    	} else if (( "TC".equals(ydSchCd.substring(2,4)) && ("U".equals(ydSchCd.substring(6,7)) || "MM".equals(ydSchCd.substring(6,8))))
		    			// 동내이적인데 대차로 올라가능 경우 
		    			|| ( "YD".equals(ydSchCd.substring(2,4)) && "TC".equals(sToLocGuideCol.substring(2,4)) && "M".equals(ydSchCd.substring(6,7)) &&  "TC".equals(ydDnWoLoc.substring(2,4)))
		    			  )	{
		    		
		    		sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 대차상차 주작업 To위치 재결정 안함";
        			commUtils.printLog(logId, sLogMsg, "SL");
        			
		    		sLogMsg = "크레인스케줄 To위치 재기동 등록 안함!!";
    				commUtils.printLog(logId, sLogMsg, "SL");
    				jrRtn.setField("RTN_CD"		, "0");
    				jrRtn.setField("RTN_MSG"	, sLogMsg);
        			return jrRtn;
						
      			} else if ((sToLocGuide.length() == 4)  && ("HC".equals(ydSchCd.substring(2,4)))) {
            		/**********************************************************
    				* 사용자 지정 : 소재 결로재 보급
    				**********************************************************/            		
      				sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자지정 소재 결로재 보급의  To위치 재결정 시작";
        			commUtils.printLog(logId, sLogMsg, "SL");
        			
        			jrRcvRtn = this.toLocUserHC(logId, mthdNm, jrWbook, jrCrnSch);
        			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
        			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
        			
      			} else if (sToLocGuide.length() >= 4) {

            		/**********************************************************
    				* 사용자 지정 : 일반야드 검색
    				**********************************************************/
      				sLogMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자지정 스케줄의  To위치 재결정 시작";
        			commUtils.printLog(logId, sLogMsg, "SL");
        			
        			jrRcvRtn = this.toLocUser(logId, mthdNm, jrWbook, jrCrnSch);
        			rtnCd =  commUtils.trim(jrRcvRtn.getFieldString("RTN_CD"));
        			
        			jrRtn.setField("RTN_CD" , rtnCd);	
        			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
        			
        			// 동내이적이고 TO위치 가이드가 8자리 미만인 경우만
        			if (("0".equals(rtnCd)) && ("YD".equals(ydSchCd.substring(2,4))) && (sToLocGuide.length() < 8) ) {
        				sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 TO위치결정-사용자 지정에서 야드로 TO위치 재결정 시작";
        				commUtils.printLog(logId, sLogMsg, "SL");
        				
        				jrRcvRtn = this.toLocPrimaryWork(logId, mthdNm, jrWbook, jrCrnSch);
        				jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
        				jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
        				
        				sTcMoveYn = commUtils.nvl(jrRcvRtn.getFieldString("TC_MOVE_YN"), "N");
        				
        			} 
        			// 동간이적시(대차하차)에도 To위치 가이드 위치검색 실패시 저장영역별 검색순서로 to위치 결정
        			else if (("0".equals(rtnCd)) && ("TC".equals(ydSchCd.substring(2,4))) && (sToLocGuide.length() < 8) && ("L".equals(ydSchCd.substring(6,7)))) {
        				sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 TO위치결정-사용자 지정에서 야드로 TO위치 재결정 시작";
        				commUtils.printLog(logId, sLogMsg, "SL");
        				
        				jrRcvRtn = this.toLocPrimaryWork(logId, mthdNm, jrWbook, jrCrnSch);
        				jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
        				jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
        				
        				sTcMoveYn = commUtils.nvl(jrRcvRtn.getFieldString("TC_MOVE_YN"), "N");
        				
        			}
    			} else {
    				/*****************************************************************************
    				* 일반작업
    				******************************************************************************/

    				sLogMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 TO위치결정-야드로 TO위치 재결정 시작";
    				commUtils.printLog(logId, sLogMsg, "SL");

    				jrRcvRtn = this.toLocPrimaryWork(logId, mthdNm, jrWbook, jrCrnSch);
    				jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
    				jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
    				
    				sTcMoveYn = commUtils.nvl(jrRcvRtn.getFieldString("TC_MOVE_YN"), "N");
    				
    			}
        	}
        	
        	//-------------------------------------------------------------------------------------------------------------
    		// To위치 결정 실패시 default값으로 xx010101을 설정
        	//-------------------------------------------------------------------------------------------------------------
			
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnschByEqpIdandWBookIdRe
			SELECT A.YD_EQP_ID               AS YD_EQP_ID
			      ,A.YD_EQP_NAME             AS YD_EQP_NAME
			         :
			      ,B.YD_UP_WR_ZAXIS          AS YD_UP_WR_ZAXIS
			      ,B.YD_DN_WR_LOC            AS YD_DN_WR_LOC
			      ,B.YD_DN_WR_LAYER          AS YD_DN_WR_LAYER
			      ,B.YD_DN_WRK_ACT_GP        AS YD_DN_WRK_ACT_GP
			      ,B.YD_DN_WR_XAXIS          AS YD_DN_WR_XAXIS
			      ,B.YD_DN_WR_YAXIS          AS YD_DN_WR_YAXIS
			      ,B.YD_DN_WR_YAXIS1         AS YD_DN_WR_YAXIS1
			      ,B.YD_DN_WR_YAXIS2         AS YD_DN_WR_YAXIS2
			      ,B.YD_DN_WR_ZAXIS          AS YD_DN_WR_ZAXIS
			      ,(SELECT ROTATION_ANGLE FROM TB_YD_STKCOL WHERE YD_STK_COL_GP = SUBSTR(YD_UP_WO_LOC,1,6)) AS ROTATION_ANGLE
			  FROM TB_YD_EQP    A
			      ,TB_YD_CRNSCH B
			 WHERE B.YD_EQP_ID   = A.YD_EQP_ID
			   AND B.YD_CRN_SCH_ID  = :V_YD_CRN_SCH_ID
			   AND B.DEL_YN      = 'N'
			 ORDER BY B.YD_CRN_SCH_ID
    		 */
    		jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnschByEqpIdandWBookIdRe", logId, mthdNm, "크레인스케줄 조회");   		
    		
    		
    		JDTORecordSet jsUpCrnSch  = JDTORecordFactory.getInstance().createRecordSet("Temp");
    		JDTORecord    jrUpCrnSch  = JDTORecordFactory.getInstance().create();
    		String        ydUpWoLoc   = "";
    		String        ydUpWoLayer = "";
    		String        sCoilOutDia = "";
    		
    		JDTORecord    jrInPara  = JDTORecordFactory.getInstance().create();
    		for (int Loop_i = 1; Loop_i <= jsCrnSch.size(); Loop_i++) {
    			jsCrnSch.absolute(Loop_i);
    			jrCrnSch = jsCrnSch.getRecord();
				if ("".equals(commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC")))) {
					jrInPara.setResultCode(logId);	//Log ID
					jrInPara.setResultMsg(mthdNm);	//Log Method Name
					jrInPara.setField("MOD_DDTT"	  , sModifier);
					jrInPara.setField("YD_DN_WO_LOC"  , "XX010101");
					jrInPara.setField("YD_CRN_SCH_ID" , commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID")));
					
					/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnWrkMgtDnLoc	
					SELECT A.YD_EQP_ID                AS YD_EQP_ID                 --야드설비ID
					     , A.YD_CRN_SCH_ID            AS YD_CRN_SCH_ID                 --야드설비ID
					     , A.YD_UP_WO_LOC             AS YD_UP_WO_LOC              --야드권상지시위치
					     , A.YD_UP_WO_LAYER           AS YD_UP_WO_LAYER            --야드권상지시단
					     , B.YD_STK_LYR_XAXIS         AS YD_UP_WO_LOC_XAXIS        --야드권상지시X축
					     , C.YD_STK_BED_XAXIS_TOL     AS YD_UP_WO_XAXIS_GAP_MAX    --야드권상지시X축오차최대
					     , C.YD_STK_BED_XAXIS_TOL     AS YD_UP_WO_XAXIS_GAP_MIN    --야드권상지시X축오차최소
					     , B.YD_STK_LYR_YAXIS         AS YD_UP_WO_LOC_YAXIS
					     , C.YD_STK_BED_YAXIS_TOL     AS YD_UP_WO_YAXIS_GAP_MAX    --야드권상지시Z축오차최대
					     , C.YD_STK_BED_YAXIS_TOL     AS YD_UP_WO_YAXIS_GAP_MIN    --야드권상지시Z축오차최소
					     , B.YD_STK_LYR_ZAXIS         AS YD_UP_WO_LOC_ZAXIS        --야드권상지시Z축
					     , C.YD_STK_BED_ZAXIS_TOL     AS YD_UP_WO_ZAXIS_GAP_MAX    --야드권상지시Z축오차최대
					     , C.YD_STK_BED_ZAXIS_TOL     AS YD_UP_WO_ZAXIS_GAP_MIN    --야드권상지시Z축오차최소
					     , (SELECT ROTATION_ANGLE FROM TB_YD_STKCOL WHERE YD_STK_COL_GP = B.YD_STK_COL_GP) AS ROTATION_ANGLE
					     , :V_MODIFIER                AS MODIFIER              
					     , SYSDATE                    AS MOD_DDTT  
					     , :V_YD_DN_WO_LOC            AS YD_DN_WO_LOC
					     , (SELECT COIL_OUTDIA 
					          FROM TB_YD_CRNWRKMTL CM
					             , TB_PT_COILCOMM  CC
					         WHERE CM.YD_CRN_SCH_ID = A.YD_CRN_SCH_ID
					           AND CM.STL_NO = CC.COIL_NO
					           AND ROWNUM = 1)  AS COIL_OUTDIA
					 FROM TB_YD_CRNSCH A
					    , TB_YD_STKLYR B
					    , TB_YD_STKBED C
					WHERE A.YD_CRN_SCH_ID     = :V_YD_CRN_SCH_ID 
					  AND SUBSTR(A.YD_UP_WO_LOC,1,6) = B.YD_STK_COL_GP
					  AND SUBSTR(A.YD_UP_WO_LOC,7,2) = B.YD_STK_BED_NO
					  AND A.YD_UP_WO_LAYER    = B.YD_STK_LYR_NO
					  AND SUBSTR(A.YD_UP_WO_LOC,1,6) = C.YD_STK_COL_GP
					  AND SUBSTR(A.YD_UP_WO_LOC,7,2) = C.YD_STK_BED_NO
					  AND ROWNUM = 1
					 */		
					jsUpCrnSch = commDao.select(jrInPara, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnWrkMgtDnLoc", logId, mthdNm, "크레인스케줄 조회");   			
					
					for (int Loop_j = 1; Loop_j <= jsUpCrnSch.size(); Loop_j++) {
						jsUpCrnSch.absolute(Loop_j);
						jrUpCrnSch = jsUpCrnSch.getRecord();
						
						ydUpWoLoc   = commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_LOC"));
						ydUpWoLayer = commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_LAYER"));
						sCoilOutDia = commUtils.trim(jrUpCrnSch.getFieldString("COIL_OUTDIA"));
						
						jrInPara.setField("YD_UP_WO_LOC_XAXIS"		, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_LOC_XAXIS")));
						jrInPara.setField("YD_UP_WO_XAXIS_GAP_MAX"	, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_XAXIS_GAP_MAX")));
						jrInPara.setField("YD_UP_WO_XAXIS_GAP_MIN"	, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_XAXIS_GAP_MIN")));
						jrInPara.setField("YD_UP_WO_LOC_YAXIS"		, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_LOC_YAXIS")));
						jrInPara.setField("YD_UP_WO_YAXIS_GAP_MAX"	, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_YAXIS_GAP_MAX")));
						jrInPara.setField("YD_UP_WO_YAXIS_GAP_MIN"	, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_YAXIS_GAP_MIN")));
						jrInPara.setField("YD_UP_WO_LOC_ZAXIS"		, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_LOC_ZAXIS")));
						jrInPara.setField("YD_UP_WO_ZAXIS_GAP_MAX"	, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_ZAXIS_GAP_MAX")));
						jrInPara.setField("YD_UP_WO_ZAXIS_GAP_MIN"	, commUtils.trim(jrUpCrnSch.getFieldString("YD_UP_WO_ZAXIS_GAP_MIN")));
						jrInPara.setField("ROTATION_ANGLE"			, commUtils.trim(jrUpCrnSch.getFieldString("ROTATION_ANGLE")));
						jrInPara.setField("YD_CRN_SCH_ID"			, commUtils.trim(jrUpCrnSch.getFieldString("YD_CRN_SCH_ID")));

						if ("002".equals(ydUpWoLayer)) {	
							int iRtnY = this.getUpDnClineY(logId, mthdNm, ydUpWoLoc.substring(0,6),ydUpWoLoc.substring(6,8),ydUpWoLayer,sCoilOutDia);				
							if(iRtnY > 0) {
								sLogMsg = "권상Y좌표재계산값:"+ iRtnY;
								commUtils.printLog(logId, sLogMsg, "SL");
								jrInPara.setField("YD_UP_WO_LOC_YAXIS",  String.valueOf(iRtnY)) ;	
							}
						}	
						/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnWrkMgtDnLoc
						UPDATE TB_YD_CRNSCH CS
						   SET MODIFIER               = :V_MODIFIER
						     , MOD_DDTT               = SYSDATE
						     , YD_DN_WO_LOC           = :V_YD_DN_WO_LOC
						     , YD_UP_WO_LOC_XAXIS     = NVL(:V_YD_UP_WO_LOC_XAXIS     ,YD_UP_WO_LOC_XAXIS)
						     , YD_UP_WO_XAXIS_GAP_MAX = NVL(:V_YD_UP_WO_XAXIS_GAP_MAX ,YD_UP_WO_XAXIS_GAP_MAX)
						     , YD_UP_WO_XAXIS_GAP_MIN = NVL(:V_YD_UP_WO_XAXIS_GAP_MIN ,YD_UP_WO_XAXIS_GAP_MIN)
						     , YD_UP_WO_LOC_YAXIS     = NVL(:V_YD_UP_WO_LOC_YAXIS     ,YD_UP_WO_LOC_YAXIS)
						     , YD_UP_WO_YAXIS_GAP_MAX = NVL(:V_YD_UP_WO_YAXIS_GAP_MAX ,YD_UP_WO_YAXIS_GAP_MAX)
						     , YD_UP_WO_YAXIS_GAP_MIN = NVL(:V_YD_UP_WO_YAXIS_GAP_MIN ,YD_UP_WO_YAXIS_GAP_MIN)
						     , YD_UP_WO_LOC_ZAXIS     = NVL(:V_YD_UP_WO_LOC_ZAXIS     ,YD_UP_WO_LOC_ZAXIS)
						     , YD_UP_WO_ZAXIS_GAP_MAX = NVL(:V_YD_UP_WO_ZAXIS_GAP_MAX ,YD_UP_WO_ZAXIS_GAP_MAX)
						     , YD_UP_WO_ZAXIS_GAP_MIN = NVL(:V_YD_UP_WO_ZAXIS_GAP_MIN ,YD_UP_WO_ZAXIS_GAP_MIN)    
						     , UP_ROTATION_ANGLE      = NVL(:V_ROTATION_ANGLE         ,UP_ROTATION_ANGLE)
						     , YD_EQP_WRK_SH          = (SELECT COUNT(*) 
						                                   FROM TB_YD_CRNWRKMTL 
						                                  WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
						     , YD_CRN_GRAB_USE_RULE_ID= (SELECT B.YD_CAR_SCH_ID
						                                   FROM TB_YD_WRKBOOK A
						                                      , TB_YD_CARSCH B
						                                  WHERE B.DEL_YN='N'
						                                    AND (B.CAR_NO=A.CAR_NO OR B.TRN_EQP_CD = A.TRN_EQP_CD )
						                                    AND A.YD_WBOOK_ID =  (SELECT B1.YD_WBOOK_ID 
						                                                            FROM TB_YD_CRNSCH B1
						                                                           WHERE B1.YD_CRN_SCH_ID = CS.YD_CRN_SCH_ID )
						                                    AND ROWNUM <= 1)
						   
						 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID   
						 */
					    intRtnVal = commDao.update(jrInPara, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnWrkMgtDnLoc", logId, mthdNm, "크레인스케줄 갱신");
					     
						if (intRtnVal <= 0) {
							sLogMsg = "크레인스케줄 To위치 Default값 등록 실패!!";
		    				commUtils.printLog(logId, sLogMsg, "SL");
		    				jrRtn.setField("RTN_CD"		, "0");
		    				jrRtn.setField("RTN_MSG"	, sLogMsg);
		        			return jrRtn;
						}
					}	
				}
			}
			
		//-------------------------------------------------------------------------------------------------------------
        	jrRtn.setField("RTN_CD"  , "1");
        	jrRtn.setField("RTN_MSG" , "스케쥴 TO위치 재검색 완료");
        	commUtils.printLog(logId, mthdNm, "S-");
        	return jrRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
    }
	/**
	 * 주작업TO위치결정  -> 제품입고 (명령선택 Y5YDL007 에서 TO위치 확정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord toLocPrimaryWorkKD(String logId, String mthdNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String mthdNm = "주작업TO위치결정[CCoilSchSeEJB.toLocPrimaryWorkKD] < " + mthdNms;
    	String sLogMsg = null;
    	JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();	//전문 Return
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");

	    	String ydStkColGp   = "";
			String ydStkBedNo   = "";
			String ydStkLyrNo   = "";	
			String sRtnBedDan   = "";	
			String sDBLogMsg    = "";
			String sGrade 		= "9";
			
			String ydSchCd 	   	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
			String ydWbookId	= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));			//작업예약
			String schLogYn 	= commUtils.trim(jrWbook.getFieldString("SCHLOG_YN"  ));			

			String sStlNo	   	= commUtils.trim(jrCrnSch.getFieldString("STL_NO"));			//크레인작업재료
			String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
			String ydEqpId     	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
			String ydUpWoLoc 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
			String ydUpWoLayer 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));		
			String sModifier    = commUtils.trim(jrCrnSch.getFieldString("MODIFIER"));
			
			String ydRouteGp    = coilDao.getCoilYdRouteGpKd(logId,mthdNm,ydSchCd,sStlNo);       //검색조건 행선
			String sSchLogContents = "";
			String sTcMoveYn = "N";  // 대차 자동 출발 여부 (동간입고 처리)
			
			if ( "".equals(ydUpWoLoc) ) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			} 
			
			if ( "".equals(ydRouteGp) ) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 검색조건행선정보가 없습니다. ";
				commUtils.printLog(logId, sLogMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			} 
			
//			JDTORecord jrToLocPrimary = JDTORecordFactory.getInstance().create();			
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("STL_NO"			, sStlNo);		//권상 STOCK
			jrParam.setField("YD_SCH_CD"		, ydSchCd);		//스케줄 코드
			jrParam.setField("YD_EQP_ID"		, ydEqpId);		//설비ID
			jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);	//크레인 스케쥴 ID
			jrParam.setField("YD_UP_WO_LOC" 	, ydUpWoLoc);  
			jrParam.setField("YD_UP_WO_LAYER" 	, ydUpWoLayer);  
			jrParam.setField("YD_ROUTE_GP"  	, ydRouteGp);  
			
			sLogMsg =  " TOSQL"+ydCrnSchId+ " 권상재료["+sStlNo +" 의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, sLogMsg, "SL");
			
	    	//----------------------------------------------------------------------------------------------------------------------
			//	일반적인 적치가능한 베드 조회
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecordSet jsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");

			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocPrimaryWorkJ
			WITH TBL_COILWIDECHK AS (
			--적치폭 기준(1순위)
			SELECT A.*
			  FROM (
			        SELECT PT.STL_NO                AS STL_NO
			             , SL.YD_STK_COL_GP         AS YD_STK_COL_GP
			             , SL.YD_STK_BED_NO         AS YD_STK_BED_NO
			             , SL.YD_STK_LYR_NO         AS YD_STK_LYR_NO
			             , SL.YD_STK_LYR_MTL_STAT
			              --SKID 구분
			             , SC.YD_STK_SKID_GP        AS COL_YD_STK_SKID_GP
			              --SKID 외경구분
			             , SC.YD_COIL_OUTDIA_GRP_GP AS COL_OUTDIA_GRP_GP
			              --SKID 폭구분
			             , SC.YD_STK_COL_W_GP       AS COL_W_GP
			             , SC.YD_LOC_GP             AS COL_YD_LOC_GP
			             , 999                      AS YD_LOC_SRCH_RNG_SEQ
			             , 999                      AS YD_STK_BED_SRCH_SEQ
			             , PT.YD_SCH_CD             AS YD_SCH_CD
			             , 0                        AS SORT_TC
			             , PT.COIL_W                AS COIL_W
			             , (CASE WHEN SC.MATL_SUP_MTD_GP='A' THEN 0
			                     WHEN SC.MATL_SUP_MTD_GP='B' THEN 1400
			                     ELSE 0                END) AS FROM_CHK
			             , (CASE WHEN SC.MATL_SUP_MTD_GP='A' THEN 1150
			                     WHEN SC.MATL_SUP_MTD_GP='B' THEN 3000
			                     ELSE 3000             END) AS TO_CHK
			             , CASE WHEN SUBSTR(SL.YD_STK_COL_GP,2,1) IN('B','C') AND TO_NUMBER(SUBSTR(SL.YD_STK_COL_GP,3,2))<=40
			                        THEN 'FIRST'
			                    WHEN SUBSTR(SL.YD_STK_COL_GP,2,1) IN('B','C') AND TO_NUMBER(SUBSTR(SL.YD_STK_COL_GP,3,2))> 40
			                        THEN 'SECOND'
			                    ELSE 'ALL' END
			               AS BC_DONG_FLAG
			             , PT.YD_EQP_ID
			          FROM TB_YD_STKLYR SL
			             , TB_YD_STKCOL SC
			             , (
			                SELECT :V_YD_SCH_CD   AS YD_SCH_CD
			                     , :V_YD_EQP_ID   AS YD_EQP_ID
			                     , C.COIL_NO      AS STL_NO
			                     , C.COIL_W       AS COIL_W
			                     , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
			                            WHEN C.COIL_OUTDIA >  1930 THEN 'C'
			                            ELSE 'B' END
			                       AS COIL_OUTDIA_GRP_GP
			                  FROM TB_PT_COILCOMM C
			                 WHERE C.COIL_NO = :V_STL_NO
			                ) PT
			         WHERE SC.YD_GP = 'J'
			           AND SL.YD_STK_COL_GP = SC.YD_STK_COL_GP
			           AND SL.DEL_YN = 'N'
			           AND SC.DEL_YN = 'N'
			           AND SC.YD_EQP_GP     BETWEEN '01' AND '99'
			           AND SC.YD_LOC_GP ='J'
			           AND SUBSTR(SC.YD_STK_COL_GP,2,1) = SUBSTR(PT.YD_SCH_CD,2,1) --//동체크
			           AND SC.MATL_SUP_MTD_GP IS NOT NULL --//적치폭기준 0:1000~1100 , 1:1100~1199 , 4:1400~1499 , 5:1500~1600
			           AND SL.YD_STK_LYR_ACT_STAT = 'E'
			           AND SL.YD_STK_LYR_MTL_STAT = 'E'
			           AND SC.YD_STK_COL_TOLOC_STAT='Y'
			           AND NVL(SC.YD_COIL_OUTDIA_GRP_GP,'*') = PT.COIL_OUTDIA_GRP_GP
			     ) A
			 WHERE A.COIL_W BETWEEN FROM_CHK AND TO_CHK
			   AND CASE WHEN A.YD_EQP_ID IN ('JBCRB1','JBCRB2','JCCRC1','JCCRC2')
			            THEN 'FIRST'
			            WHEN A.YD_EQP_ID IN ('JBCRB3','JBCRB4','JCCRC3','JCCRC4')
			            THEN 'SECOND'
			            ELSE 'ALL' END
			       = A.BC_DONG_FLAG
			)
			, TO_COMM_TABLE AS (
			SELECT :V_YD_SCH_CD   AS YD_SCH_CD
			     , :V_YD_EQP_ID   AS YD_EQP_ID
			     , C.COIL_NO      AS STL_NO
			     , C.COIL_W       AS COIL_W
			     , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
			            WHEN C.COIL_OUTDIA >  1930 THEN 'C'
			            ELSE 'B' END
			       AS COIL_OUTDIA_GRP_GP
			     , CASE WHEN C.COIL_W < 1601 THEN 'M'
			            ELSE 'L' END
			       AS COIL_W_GP
			     --  지포장 여부
			     , (CASE WHEN DECODE(C.CURR_PROG_CD,'F','XX',B.WRAP_METHOD_CD)='EB'
			                  AND C.ITEMNAME_CD NOT IN ('HAP','HBP','HCP','HAS','HBS','HCS','HAT','HBT','HCT')
			                  AND C.ORD_YEOJAE_GP='1'
			             THEN 'Y' ELSE 'N' END)        AS G_PAKAGE_YN
			     --결로 엄격재
			     , NVL((SELECT 'Y' FROM USRYDA.VW_YD_CONDENSATIONMTL WHERE COIL_NO = C.COIL_NO ),'N') AS COND_YN
			     --결로 엄격재 EF동 여부
			     , NVL((SELECT 'Y' FROM USRYDA.VW_YD_CONDENSATION    WHERE STL_NO  = C.COIL_NO ),'N') AS CONDEF_YN
			     --결로 엄격재 A동 대차제어 여부
			     , NVL((SELECT (CASE WHEN (SELECT ITEM1
			                                 FROM TB_YD_RULE R
			                                WHERE R.REPR_CD_GP = 'J00004'
			                                  AND R.CD_GP      = SUBSTR(YD_EQP_ID,6,1)
			                               ) = 'Y' THEN RCPT_TCAR_AIM_BAY_GP ELSE 'X' END)
			              FROM TB_YD_EQP
			             WHERE YD_EQP_ID LIKE 'J_TC___M%'
			               AND RCPT_TCAR_AIM_BAY_GP='A'
			               AND YD_EQP_ID ='JX'||SUBSTR(:V_YD_SCH_CD,3,4) ),'X') AS AIM_BAY_GP
			  FROM TB_PT_COILCOMM C
			     , TB_PT_OSCOMM B
			 WHERE C.COIL_NO = :V_STL_NO
			   AND C.ORD_NO  = B.ORD_NO
			   AND C.ORD_DTL = B.ORD_DTL
			   AND ROWNUM = 1
			)
			, TO_LOC_TABLE AS (
			 -- 대상 위치 SELECT
			SELECT A.CHK
			     , A.STL_NO
			     , A.SORT_CHK
			     , A.YD_STK_COL_GP         AS TAG_YD_STK_COL_GP
			     , A.YD_STK_BED_NO         AS TAG_YD_STK_BED_NO
			     , A.YD_STK_LYR_NO         AS TAG_YD_STK_LYR_NO
			     , A.YD_STK_LYR_MTL_STAT
			     -- 열에 따른 코일 간격
			     , SF_YD_SKID_INTERVAL_GAP(A.YD_STK_COL_GP)  AS INT
			     , DECODE(A.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(A.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(A.YD_STK_COL_GP), 2, '0'),
			                               '002', A.YD_STK_BED_NO)                               AS TAG_LEFT_BED

			     , DECODE(A.YD_STK_LYR_NO, '001', A.YD_STK_LYR_NO,
			                               '002', LPAD(TO_NUMBER(A.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_LEFT_LAYER

			     , DECODE(A.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(A.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(A.YD_STK_COL_GP), 2, '0'),
			                               '002', LPAD(TO_NUMBER(A.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(A.YD_STK_COL_GP), 2, '0'))
			                                                                                     AS TAG_RIGHT_BED
			     , DECODE(A.YD_STK_LYR_NO, '001', A.YD_STK_LYR_NO,
			                               '002', LPAD(TO_NUMBER(A.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_RIGHT_LAYER
			     , A.YD_LOC_SRCH_RNG_SEQ
			     , A.YD_STK_BED_SRCH_SEQ
			     , A.YD_SCH_CD
			     , A.SORT_TC
			     , A.COL_OUTDIA_GRP_GP
			     , A.COL_W_GP
			     , A.COL_YD_STK_SKID_GP
			     , A.COL_YD_LOC_GP
			     , A.COIL_OUTDIA_GRP_GP
			     , A.COIL_W_GP
			     , A.COIL_W
			     --결로 엄격재 여부
			     , A.COND_YN
			     --결로 엄격재 EF동 여부
			     , A.CONDEF_YN
			     --결로 엄격재 A동 대차제어 여부
			     , A.AIM_BAY_GP
			     , A.G_PAKAGE_YN
			     , A.FROM_CHK
			     , A.TO_CHK
			     , A.CONDEN_YN
			     , A.TOT_YN
			     , 'J' AS CHK_SKID_GP

			  FROM (--적치폭 기준(1순위)
			        SELECT 1 AS CHK
			             , (CASE WHEN (PT.YD_SCH_CD LIKE 'J_TC__MM' OR PT.YD_SCH_CD LIKE 'J_TC__UM') THEN 2 ELSE 1 END ) AS SORT_CHK
			--             , PT.CONDEN_YN
			             , (CASE WHEN PT.YD_SCH_CD LIKE 'J_TC__MM' AND 'N'= (SELECT ITEM1 FROM TB_YD_RULE R WHERE R.REPR_CD_GP='J00004' AND R.CD_GP = SUBSTR(CW.YD_STK_COL_GP,6,1))
			                     THEN 'N'
			                     ELSE (CASE WHEN (SELECT 'Y' FROM VW_YD_CONDENSATIONMTL CO WHERE CO.COIL_NO = PT.STL_NO)='Y' AND CW.COIL_W<=1400 THEN 'Y' ELSE 'N' END)
			                 END ) AS CONDEN_YN
			             , PT.COND_YN
			             , PT.CONDEF_YN
			             , PT.AIM_BAY_GP
			             , PT.G_PAKAGE_YN
			             , PT.STL_NO
			             , PT.COIL_OUTDIA_GRP_GP
			             , PT.COIL_W_GP
			             , CW.YD_STK_COL_GP
			             , CW.YD_STK_BED_NO
			             , CW.YD_STK_LYR_NO
			             , CW.YD_STK_LYR_MTL_STAT
			              --SKID 구분
			             , CW.COL_YD_STK_SKID_GP
			              --SKID 외경구분
			             , CW.COL_OUTDIA_GRP_GP
			              --SKID 폭구분
			             , CW.COL_W_GP
			             , CW.COL_YD_LOC_GP
			             , CW.YD_LOC_SRCH_RNG_SEQ
			             , CW.YD_STK_BED_SRCH_SEQ
			             , CW.YD_SCH_CD
			             , CW.SORT_TC
			             , CW.COIL_W
			             , CW.FROM_CHK
			             , CW.TO_CHK
			             , YR.TOT_YN
			             , CW.BC_DONG_FLAG
			             , PT.YD_EQP_ID
			          FROM TBL_COILWIDECHK CW
			             , TO_COMM_TABLE  PT
			             , (SELECT ITEM1 AS TOT_YN FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP002' AND DEL_YN = 'N' AND ROWNUM = 1) YR
			        UNION  ALL
			        --검색조건(1순위)
			        SELECT /*+ USE_NL(X1 SL)
			               2 AS CHK
			             , 2 AS SORT_CHK
			--             , PT.CONDEN_YN
			             ,(CASE WHEN PT.YD_SCH_CD LIKE 'J_TC__MM' AND 'N'= (SELECT ITEM1 FROM TB_YD_RULE R WHERE R.REPR_CD_GP='J00004' AND R.CD_GP = SUBSTR(SL.YD_STK_COL_GP,6,1))
			                     THEN 'N'
			                     ELSE (CASE WHEN (SELECT 'Y' FROM VW_YD_CONDENSATIONMTL CO WHERE CO.COIL_NO =PT.STL_NO)='Y' AND PT.COIL_W<=1400 THEN 'Y' ELSE 'N' END)
			                 END ) AS CONDEN_YN
			             , PT.COND_YN
			             , PT.CONDEF_YN
			             , PT.AIM_BAY_GP
			             , PT.G_PAKAGE_YN
			             , PT.STL_NO
			             , PT.COIL_OUTDIA_GRP_GP
			             , PT.COIL_W_GP
			             , SL.YD_STK_COL_GP
			             , SL.YD_STK_BED_NO
			             , SL.YD_STK_LYR_NO
			             , SL.YD_STK_LYR_MTL_STAT
			              --SKID 구분
			             , SC.YD_STK_SKID_GP            AS COL_YD_STK_SKID_GP
			              --SKID 외경구분
			             , SC.YD_COIL_OUTDIA_GRP_GP     AS COL_OUTDIA_GRP_GP
			              --SKID 폭구분
			             , SC.YD_STK_COL_W_GP           AS COL_W_GP
			             , SC.YD_LOC_GP                 AS COL_YD_LOC_GP
			             , X1.YD_LOC_SRCH_RNG_SEQ
			             , X1.YD_STK_BED_SRCH_SEQ
			             , X1.YD_SCH_CD
			             , X1.SORT_TC
			             , PT.COIL_W
			             , 0            AS FROM_CHK
			             , 0            AS TO_CHK
			             , YR.TOT_YN
			             , SC.BC_DONG_FLAG
			             , PT.YD_EQP_ID
			          FROM TB_YD_STKLYR SL
			             , (SELECT A.*
			                     , CASE WHEN SUBSTR(A.YD_STK_COL_GP, 2, 1) IN ('B', 'C')
			                                 THEN CASE WHEN SUBSTR(A.YD_STK_COL_GP, 3, 2) BETWEEN '01' AND '99'
			                                                THEN CASE WHEN TO_NUMBER(SUBSTR(A.YD_STK_COL_GP, 3, 2)) <= 40
			                                                               THEN 'FIRST'
			                                                          ELSE 'SECOND'
			                                                     END
			                                           WHEN A.YD_STK_COL_GP LIKE 'J_TC05%' -- #5대차
			                                                THEN 'FIRST'
			                                           WHEN A.YD_STK_COL_GP LIKE 'J_K%'    -- #3,4SPM
			                                                THEN 'FIRST'
			                                           ELSE 'SECOND'
			                                      END
			                            ELSE 'ALL'
			                       END AS BC_DONG_FLAG
			                  FROM TB_YD_STKCOL A) SC
			             , TO_COMM_TABLE   PT
			             , (SELECT ITEM1 AS TOT_YN FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP002' AND DEL_YN = 'N' AND ROWNUM = 1) YR
			             , (SELECT A.YD_SCH_CD
			                     , A.YD_ROUTE_GP
			                     , A.YD_LOC_SRCH_RNG_REG_SNO
			                     , A.YD_LOC_SRCH_RNG_SEQ
			                     , B.YD_STK_BED_SRCH_SEQ
			                     , B.YD_STK_COL_GP
			                     , B.YD_STK_BED_NO
			                     , (CASE WHEN B.YD_STK_COL_GP LIKE 'J_TC%' AND B.YD_STK_BED_NO='01' THEN 1
			                             WHEN B.YD_STK_COL_GP LIKE 'J_TC%' AND B.YD_STK_BED_NO='02' THEN 3
			                             WHEN B.YD_STK_COL_GP LIKE 'J_TC%' AND B.YD_STK_BED_NO='03' THEN 2
			                             ELSE 0
			                         END) AS SORT_TC
			                  FROM TB_YD_LOCSRCHRNG A
			                     , TB_YD_LOCSRCHBED B
			                 WHERE A.YD_LOC_SRCH_RNG_REG_SNO = B.YD_LOC_SRCH_RNG_REG_SNO
			                   AND A.YD_SCH_CD               = :V_YD_SCH_CD
			                   AND A.YD_ROUTE_GP             = (CASE WHEN A.YD_SCH_CD LIKE 'J_FD01LM' THEN  'F0' ELSE :V_YD_ROUTE_GP END )
			                   AND A.DEL_YN = 'N'
			                   AND B.DEL_YN = 'N'
			                 ) X1
			         WHERE SC.YD_GP||'' = 'J'
			           AND SL.YD_STK_COL_GP = X1.YD_STK_COL_GP
			           AND SL.YD_STK_BED_NO = X1.YD_STK_BED_NO
			           AND SL.YD_STK_COL_GP = SC.YD_STK_COL_GP
			           AND SL.DEL_YN = 'N'
			           AND SC.DEL_YN = 'N'
			--           AND SC.YD_EQP_GP     BETWEEN '01' AND '99'
			           --****  야드 통합여부  *****
			           AND 'Y' = CASE WHEN YR.TOT_YN = 'N' THEN
			                               CASE WHEN SC.YD_LOC_GP = 'J' THEN 'Y' ELSE 'N' END
			                          ELSE 'Y'  END
			           -- 외경구분/폭구분 CEHCK (소재제외)
			           AND 'Y' = CASE WHEN SC.YD_LOC_GP = 'H' THEN 'Y'
			                          WHEN SUBSTR(SL.YD_STK_COL_GP,3,1) NOT IN ('1','8')
			                               AND NVL(SC.YD_COIL_OUTDIA_GRP_GP,'*')  = NVL(PT.COIL_OUTDIA_GRP_GP,'*')
			                               AND NVL(SC.YD_STK_COL_W_GP,'*')        = NVL(PT.COIL_W_GP,'*')
			                               THEN 'Y'
			                          WHEN SUBSTR(SL.YD_STK_COL_GP,3,1) IN ('1','8','C','D','F','K','M','P','S','T','G')
			                               THEN 'Y'
			                        -- 지포장은 외경/폭 구분 안함
			                          WHEN X1.YD_ROUTE_GP='G0' AND
			                               SL.YD_STK_COL_GP IN (SELECT YD_STK_COL_GP
			                                                      FROM TB_YD_LOCSRCHRNG A
			                                                         , TB_YD_LOCSRCHBED B
			                                                         , TB_YD_SCHRULE C
			                                                     WHERE A.YD_LOC_SRCH_RNG_REG_SNO=B.YD_LOC_SRCH_RNG_REG_SNO
			                                                       AND A.YD_SCH_CD=C.YD_SCH_CD
			                                                       AND A.YD_ROUTE_GP='G0'
			                                                       AND A.DEL_YN='N'
			                                                       AND C.CD_CONTENTS LIKE '%입고%'
			                                                       AND C.YD_GP IN('H','J')
			                                                     GROUP BY YD_STK_COL_GP
			                                                    )
			                               THEN 'Y'
			                          ELSE 'N' END
			           AND SL.YD_STK_LYR_ACT_STAT = 'E'
			           AND SL.YD_STK_LYR_MTL_STAT = 'E'
			           AND SC.YD_STK_COL_TOLOC_STAT = 'Y'
			           AND SC.MATL_SUP_MTD_GP IS NULL
			           AND CASE WHEN PT.YD_EQP_ID IN ('JBCRB1','JBCRB2','JCCRC1','JCCRC2')
			                    THEN 'FIRST'
			                    WHEN PT.YD_EQP_ID IN ('JBCRB3','JBCRB4','JCCRC3','JCCRC4')
			                    THEN 'SECOND'
			                    ELSE 'ALL' END
			              = SC.BC_DONG_FLAG
			           --적치폭 기준(1순위) 중복제외
			           AND X1.YD_STK_COL_GP NOT IN (SELECT YD_STK_COL_GP FROM TBL_COILWIDECHK)
			       ) A
			 WHERE 1 = 1
			   -- 크레인 작업지시 제외
			   -- AND YD_STK_COL_GP||YD_STK_BED_NO||YD_STK_LYR_NO NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YD_CRNSCH WHERE DEL_YN = 'N')
			   -- 작업예약 TO위치 가이드 제외
			   AND YD_STK_COL_GP||YD_STK_BED_NO NOT IN ( SELECT NVL(YD_TO_LOC_GUIDE,'1') FROM TB_YD_WRKBOOK WHERE DEL_YN = 'N')
			--****  야드 통합여부  *****
			   AND 'Y' = CASE WHEN TOT_YN = 'N' THEN
			                       CASE WHEN COL_YD_LOC_GP = 'J' THEN 'Y' ELSE 'N' END
			                  ELSE 'Y' END
			   --//공냉장 제외 처리
			   AND SUBSTR(YD_STK_COL_GP,1,4) NOT IN ('JA31','JA32','JB31','JC32')
			   --B,C동 지포장 적치 불가 지역 체크   --B동 13스판 3열부터 14스판 5열까지   --C동 13스판 6열부터 15스판 2열까지
			   AND 'Y' = CASE WHEN G_PAKAGE_YN = 'Y' AND YD_STK_COL_GP NOT IN ('JB1303','JB1304','JB1305','JB1306','JB1401','JB1402','JB1403','JB1404','JB1405',
			                                                                   'JC1306','JC1401','JC1402','JC1403','JC1404','JC1405','JC1406','JC1501','JC1502')
			                  THEN 'Y'
			                  WHEN G_PAKAGE_YN = 'N' THEN 'Y'
			                  ELSE 'N' END
			   --//엄격재 폭이 1400미만
			   AND 'Y' = CASE WHEN CONDEN_YN   = 'Y' AND SUBSTR(YD_STK_COL_GP,1,4) IN     ('JA50','JB13','JB14','JC14','JC15') THEN 'Y'
			              --//엄격재 아닌 경우
			                  WHEN CONDEN_YN   = 'N' AND SUBSTR(YD_STK_COL_GP,1,4) NOT IN ('JA50','JB13','JB14','JC14','JC15') THEN 'Y'
			                  ELSE 'N' END
			   --//대차 목적동이 A동이고 엄격재 인경우 대차 입고 제외
			   AND 'Y' = CASE WHEN AIM_BAY_GP  = 'X' THEN 'Y'
			                  WHEN AIM_BAY_GP  = 'A' AND (COND_YN='Y' OR CONDEF_YN='Y') AND YD_STK_COL_GP NOT LIKE '%TC%' THEN 'Y'
			                  WHEN AIM_BAY_GP  = 'A' AND COND_YN='N' AND CONDEF_YN='N' THEN 'Y'
			                  ELSE 'N' END
			   --//A동 결로엄격재를 출하 통로 주변에 적치 안되도록 개선 2020.02.17
			   AND 'Y' = CASE WHEN COND_YN = 'Y' AND YD_STK_COL_GP NOT IN ('JA5204','JA5205','JA5401','JA5402') THEN 'Y'
			                  WHEN COND_YN = 'N' THEN 'Y'
			                  ELSE 'N' END


			)

			, TO_LOC_DATA_TABLE AS (
			-- TO위치 코일정보 SELECT
			SELECT A.CHK
			     , A.STL_NO
			     , A.SORT_CHK
			     , B.YD_EQP_WRK_MODE2
			     , A.YD_SCH_CD
			     , A.TAG_YD_STK_COL_GP
			     , A.TAG_YD_STK_BED_NO
			     , A.TAG_YD_STK_LYR_NO
			     , A.TAG_LEFT_BED
			     , A.TAG_LEFT_LAYER
			     , A.SORT_TC
			     , A.YD_LOC_SRCH_RNG_SEQ
			     , A.YD_STK_BED_SRCH_SEQ
			     , (SELECT MAX(YD_STK_BED_NO)
			          FROM TB_YD_STKLYR WHERE YD_STK_COL_GP = A.TAG_YD_STK_COL_GP AND ROWNUM = 1) AS COL_MAX_BED_NO
			     , A.COL_OUTDIA_GRP_GP
			     , A.COL_W_GP
			     , A.COL_YD_LOC_GP
			     , A.COL_YD_STK_SKID_GP
			     , A.COIL_OUTDIA_GRP_GP
			     , A.COIL_W_GP
			     , A.CHK_SKID_GP
			     , A.TOT_YN
			     , (SELECT YD_STK_LYR_ACT_STAT
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
			           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
			           AND B.DEL_YN = 'N') AS TAG_LEFT_ACTIVE_STAT
			     , (SELECT YD_STK_LYR_MTL_STAT
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
			           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
			           AND B.DEL_YN = 'N') AS TAG_LEFT_LAYER_STAT
			     , (SELECT STL_NO
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
			           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
			           AND B.DEL_YN = 'N') AS TAG_LEFT_STL_NO
			     , A.TAG_RIGHT_BED
			     , A.TAG_RIGHT_LAYER
			     , (SELECT YD_STK_LYR_ACT_STAT
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
			           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
			           AND B.DEL_YN = 'N') AS TAG_RIGHT_ACTIVE_STAT
			     , (SELECT YD_STK_LYR_MTL_STAT
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
			           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
			           AND B.DEL_YN = 'N') AS TAG_RIGHT_LAYER_STAT
			     , (SELECT STL_NO
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
			           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
			           AND B.DEL_YN = 'N') AS TAG_RIGHT_STL_NO
			     , (SELECT STL_NO
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
			           AND B.YD_STK_LYR_NO   = '002'
			           AND B.DEL_YN = 'N' ) AS TAG_2DAN_LEFT_STL_NO
			     , (SELECT STL_NO
			          FROM TB_YD_STKLYR B
			         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
			           AND B.YD_STK_BED_NO   = A.TAG_YD_STK_BED_NO
			           AND B.YD_STK_LYR_NO   = '002'
			           AND B.DEL_YN = 'N' ) AS TAG_2DAN_RIGHT_STL_NO
			  FROM TO_LOC_TABLE A
			     , (SELECT YD_EQP_WRK_MODE2 FROM TB_YD_EQP WHERE YD_EQP_ID = :V_YD_EQP_ID) B
			), TO_LOC_DATA_COMP_TABLE AS (
			--*--*--*--*--*--*-- 적치가능위치
			SELECT *
			  FROM (
			        SELECT (SELECT COUNT(*)
			                  FROM TB_YD_STOCK A
			                     , TB_YD_CARSCH B
			                 WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
			                   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
			                   AND B.DEL_YN = 'N'
			                   AND A.STL_NO = NVL(L_COIL_NO,'-')
			                   AND ROWNUM <= 1) AS L_CAR_CHK
			             , (SELECT COUNT(*)
			                  FROM TB_YD_STOCK A
			                     , TB_YD_CARSCH B
			                 WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
			                   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
			                   AND B.DEL_YN = 'N'
			                   AND A.STL_NO = NVL(R_COIL_NO,'-')
			                   AND ROWNUM <= 1) AS R_CAR_CHK
			             -- 1단우선적치 B군(폭1200이상,외경1800이상) C군(폭1200,외경1990이상)
			             , CASE WHEN C_COIL_OUTDIA_GP = 'B' AND C_WIDTH >= 1200 AND C_OUTDIA >= 1800 THEN '001'
			                    WHEN C_COIL_OUTDIA_GP = 'C' AND C_WIDTH >= 1200 AND C_OUTDIA >= 1990 THEN '001'
			                    ELSE '002'
			               END AS ABOVE_LYR
			             , KK.*
			          FROM
			               (
			                SELECT K.*
			                     , C.COIL_NO      AS C_COIL_NO
			                     , C.COIL_T       AS C_THICK
			                     , C.COIL_W       AS C_WIDTH
			                     , C.COIL_WT      AS C_WEIGTH
			                     , C.COIL_OUTDIA  AS C_OUTDIA
			                     , C.CURR_PROG_CD AS C_PROG_CD
			                     , C.ORD_NO       AS C_ORD_NO     -- 주문번호
			                     , C.ORD_DTL      AS C_ORD_DTL    -- 주문행번
			                     , C.DEMANDER_CD  AS C_DEMANDER_CD-- 수요가코드
			                     , (CASE WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
			                             WHEN C.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN C.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN C.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN C.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN C.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             ELSE '' END
			                       ) AS C_YEOJAE_CAUSE_CD
			                     , TRUNC((SYSDATE - C.HRMILL_CMPL_DT )*24)             AS C_HOT_COIL_MIN
			                     , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
			                            WHEN C.COIL_OUTDIA >  1930 THEN 'C'
			                            ELSE 'B' END  AS C_COIL_OUTDIA_GP
			                     , CASE WHEN C.COIL_W < 1601     THEN 'M' ELSE 'L' END AS C_COIL_W_GP
			                     , L.COIL_NO      AS L_COIL_NO
			                     , L.COIL_T       AS L_THICK
			                     , L.COIL_W       AS L_WIDTH
			                     , L.COIL_WT      AS L_WEIGTH
			                     , L.COIL_OUTDIA  AS L_OUTDIA
			                     , L.CURR_PROG_CD AS L_PROG_CD
			                     , L.ORD_NO       AS L_ORD_NO     -- 주문번호
			                     , L.ORD_DTL      AS L_ORD_DTL    -- 주문행번
			                     , L.DEMANDER_CD  AS L_DEMANDER_CD-- 수요가코드
			                     , (CASE WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
			                             WHEN L.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN L.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN L.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN L.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN L.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             ELSE '' END
			                       ) AS L_YEOJAE_CAUSE_CD
			                     , TRUNC((SYSDATE - L.HRMILL_CMPL_DT )*24) AS L_HOT_COIL_MIN
			                     , R.COIL_NO      AS R_COIL_NO
			                     , R.COIL_W       AS R_WIDTH
			                     , R.COIL_T       AS R_THICK
			                     , R.COIL_WT      AS R_WEIGTH
			                     , R.COIL_OUTDIA  AS R_OUTDIA
			                     , R.CURR_PROG_CD AS R_PROG_CD
			                     , R.ORD_NO       AS R_ORD_NO     -- 주문번호
			                     , R.ORD_DTL      AS R_ORD_DTL    -- 주문행번
			                     , R.DEMANDER_CD  AS R_DEMANDER_CD-- 수요가코드
			                     , (CASE WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
			                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
			                             WHEN R.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN R.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN R.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN R.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             WHEN R.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
			                             ELSE '' END
			                       ) AS R_YEOJAE_CAUSE_CD
			                     , TRUNC((SYSDATE - R.HRMILL_CMPL_DT )*24) R_HOT_COIL_MIN
			                     , L.ISHOT        AS L_ISHOT
			                     , R.ISHOT        AS R_ISHOT
			                  FROM TO_LOC_DATA_TABLE K
			                     , (SELECT 1 T_ROW, A.*
			                          FROM USRPTA.TB_PT_COILCOMM  A ) C  --대상코일
			                     , (SELECT 1 T_ROW, A.*
			                          ,DECODE(NVL(DECODE(STL_APPEAR_GP,'Y','CG','CM'),'CM'),'CM',
			                                  CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 96
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
			                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 48
			                                            THEN 'TRUE'
			                                            ELSE 'FALSE'
			                                       END
			                                  END) AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
			                          FROM USRPTA.TB_PT_COILCOMM  A ) L  --하단LEFT
			                     , (SELECT 1 T_ROW, A.*
			                             ,DECODE(NVL(DECODE(STL_APPEAR_GP,'Y','CG','CM'),'CM'),'CM',
			                                          CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
			                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
			                                                    THEN 'TRUE'
			                                                    ELSE 'FALSE'
			                                               END
			                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
			                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 96
			                                                    THEN 'TRUE'
			                                                    ELSE 'FALSE'
			                                               END
			                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
			                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
			                                                    THEN 'TRUE'
			                                                    ELSE 'FALSE'
			                                               END
			                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
			                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 48
			                                                    THEN 'TRUE'
			                                                    ELSE 'FALSE'
			                                               END
			                                          END) AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
			                          FROM USRPTA.TB_PT_COILCOMM  A ) R  --하단RIGHT
			                 WHERE K.STL_NO           = C.COIL_NO(+)
			                   AND K.TAG_LEFT_STL_NO  = L.COIL_NO(+)
			                   AND K.TAG_RIGHT_STL_NO = R.COIL_NO(+)
			                   --짱구코일 2단제외
			--                   AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND JJANG_GP = 'Y'                      THEN 0
			--                                ELSE 1 END
			                   --2단일 경우 1단에 핫코일이 존재 시 2단제의
			                   AND 'Y' = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND (L.ISHOT ='TRUE' OR R.ISHOT='TRUE') THEN 'N'
			                                  ELSE 'Y' END
			--                   --2단일 경우 좌우 적치 상태 CHECK
			                   AND 'Y' = CASE WHEN TAG_YD_STK_LYR_NO = '002'
			                                       AND (TAG_LEFT_ACTIVE_STAT != 'E' OR TAG_RIGHT_ACTIVE_STAT != 'E'
			                                       OR TAG_LEFT_LAYER_STAT  != 'C' OR TAG_RIGHT_LAYER_STAT  != 'C') THEN 'N'
			                                  ELSE 'Y' END
			--                   --1단일 경우 좌우 상단 적치 상태 CHECK
			                   AND 'Y' = CASE WHEN K.TAG_YD_STK_LYR_NO = '001' AND (TAG_2DAN_LEFT_STL_NO IS NOT NULL OR TAG_2DAN_RIGHT_STL_NO IS NOT NULL) THEN 'N'
			                                  ELSE 'Y' END

			               ) KK
			                   --무인크레인 이동가능 여부 CHECK
			--         WHERE ( TAG_YD_STK_COL_GP IN (SELECT R.ITEM
			--                                         FROM TB_YD_RULE      R
			--                                            , AUTO_CR_TABLE   A
			--                                        WHERE R.REPR_CD_GP = 'CR0001'
			--                                          AND R.CD_GP      = A.EQUIP_GP
			--                                          AND R.DEL_YN     = 'N')
			--               )
			       )
			)
			--*--*--*--*--*--*-- 평점
			SELECT G.*
			     , CASE  -- 1단이면서 소재인 경우
			            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
			            THEN CASE WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌우) 진도코드
			                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌단) 진도코드
			                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우단) 진도코드
			                      WHEN L_COIL_NO  IS NULL     AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
			                      WHEN R_COIL_NO  IS NULL     AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED
			                      ELSE '7' END
			             -- 2단이면서 소재인 경우
			            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
			            THEN CASE WHEN L_WB        = 'Y'      OR  R_WB       = 'Y'      THEN '8' --좌하단우하단 작업 예약
			                      WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌하단우하단) 진도코드
			                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌하단) 진도코드
			                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우하단) 진도코드
			                      ELSE '2' END
			            -- 1단이면서 제품인 경우
			            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8')
			            THEN CASE WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
			                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
			                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
			                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우축 동일 고객사+주문번호행번

			                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
			                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우측 동일 주문번호행번

			                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌측   동일 주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우측   동일 주문번호행번
			                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우측 동일 주문번호
			                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우측   동일 주문번호
			                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌측   동일 주문번호
			                      WHEN L_COIL_NO IS NULL      AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
			                      WHEN R_COIL_NO IS NULL      AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED

			                      ELSE '7' END
			            -- 2단이면서 제품인 경우
			            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8') --제품
			            THEN CASE WHEN L_WB = 'Y'             OR  R_WB  = 'Y'           THEN '8' --좌하단우하단 작업 예약
			                      WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
			                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
			                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
			                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 고객사+주문번호행번

			                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
			                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 주문번호행번

			                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌하단   동일 주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우하단   동일 주문번호행번
			                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우하단 동일 주문번호
			                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우하단   동일 주문번호
			                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌하단   동일 주문번호
			                      ELSE '2' END

			             ELSE '9' END GRADE
			  FROM (SELECT A.*
			             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
			                  FROM TB_YD_WRKBOOK    A1
			                     , TB_YD_WRKBOOKMTL B1
			                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			                   AND A1.DEL_YN      = 'N'
			                   AND B1.DEL_YN      = 'N'
			                   AND B1.STL_NO      = A.C_COIL_NO
			               ) C_WB
			             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
			                  FROM TB_YD_WRKBOOK    A1
			                     , TB_YD_WRKBOOKMTL B1
			                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			                   AND A1.DEL_YN      = 'N'
			                   AND B1.DEL_YN      = 'N'
			                   AND B1.STL_NO      = A.L_COIL_NO
			               ) L_WB
			             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
			                  FROM TB_YD_WRKBOOK    A1
			                     , TB_YD_WRKBOOKMTL B1
			                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			                   AND A1.DEL_YN      = 'N'
			                   AND B1.DEL_YN      = 'N'
			                   AND B1.STL_NO      = A.R_COIL_NO
			               ) R_WB
			          FROM TO_LOC_DATA_COMP_TABLE A
			       ) G
			 ORDER BY SORT_CHK
			        , YD_LOC_SRCH_RNG_SEQ  --동간입고시 대차 먼저 선정처리
			        , SORT_TC
			        , GRADE
			        , (CASE WHEN ABOVE_LYR = TAG_YD_STK_LYR_NO THEN 1 ELSE 2 END) -- 1단우선적치 B군(폭1200이상,외경1800이상) C군(폭1200,외경1990이상)
			        , YD_STK_BED_SRCH_SEQ
			        , TAG_YD_STK_BED_NO
			        , TAG_YD_STK_LYR_NO
			*/        
			jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocPrimaryWorkJ", logId, mthdNm, "제품동일한 적치가능한 베드 조회");
			
			/*******************************/
			/* TO위치 검색실패 시 추가 로직         */
			/*******************************/
			if(jsResult.size() <= 0) {
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocPrimaryWorkThirdJ
				WITH PARAM_TBL AS (
				SELECT :V_YD_SCH_CD      AS YD_SCH_CD
				     , :V_YD_ROUTE_GP    AS YD_ROUTE_GP
				     , :V_YD_EQP_ID      AS YD_EQP_ID
				     , C.COIL_NO         AS STL_NO
				     , C.COIL_W          AS COIL_W
				     , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
				            WHEN C.COIL_OUTDIA >  1930 THEN 'C'
				            ELSE 'B' END AS COIL_OUTDIA_GRP_GP
				     , CASE WHEN C.COIL_W < 1601 THEN 'M'
				            ELSE 'L' END AS COIL_W_GP
				  FROM TB_PT_COILCOMM C
				 WHERE C.COIL_NO = :V_STL_NO
				)
				, TO_LOC_ALL AS (
				SELECT X.STL_NO
				     , X.YD_STK_COL_GP         AS TAG_YD_STK_COL_GP
				     , X.YD_STK_BED_NO         AS TAG_YD_STK_BED_NO
				     , Z.YD_STK_LYR_NO         AS TAG_YD_STK_LYR_NO
				     , X.YD_STK_BED_WHIO_STAT
				     , X.YD_STK_BED_ACT_STAT
				     , X.YD_STK_BED_USG_GP
				     , X.COL_OUTDIA_GRP_GP
				     , X.COL_W_GP
				     , X.COL_YD_LOC_GP
				     , X.COL_YD_STK_SKID_GP
				     , X.COIL_OUTDIA_GRP_GP
				     , X.COIL_W_GP
				     , X.YD_SCH_CD
				     -- 열에 따른 코일 간격
				     , SF_YD_SKID_INTERVAL_GAP(X.YD_STK_COL_GP)  AS INT
				     , DECODE(Z.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(Z.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(Z.YD_STK_COL_GP), 2, '0'),
				                               '002', Z.YD_STK_BED_NO)                               AS TAG_LEFT_BED

				     , DECODE(Z.YD_STK_LYR_NO, '001', Z.YD_STK_LYR_NO,
				                               '002', LPAD(TO_NUMBER(Z.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_LEFT_LAYER

				     , DECODE(Z.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(Z.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(Z.YD_STK_COL_GP), 2, '0'),
				                               '002', LPAD(TO_NUMBER(Z.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(Z.YD_STK_COL_GP), 2, '0'))
				                                                                                       AS TAG_RIGHT_BED
				     , DECODE(Z.YD_STK_LYR_NO, '001', Z.YD_STK_LYR_NO,
				                               '002', LPAD(TO_NUMBER(Z.YD_STK_LYR_NO) - 1, 3, '0')) AS TAG_RIGHT_LAYER
				     -- 적치가능여부 CHECK시 필요 :H/J/ABC/WRAP
				     , 'J' AS CHK_SKID_GP
				  FROM (
				        SELECT C.YD_SCH_CD   AS YD_SCH_CD
				             , 'A1'          AS YD_ROUTE_GP
				             , C.STL_NO      AS STL_NO
				             , A.YD_GP
				             , A.YD_BAY_GP
				             , A.YD_STK_COL_NO
				             , A.YD_STK_COL_GP
				             , A.YD_STKBED_USG_CD
				             , B.YD_STK_BED_NO
				             , B.YD_STK_BED_USG_GP
				             , B.YD_STK_BED_ACT_STAT
				             , B.YD_STK_BED_WHIO_STAT
				             , B.YD_STK_BED_W_GP
				             , A.YD_COIL_OUTDIA_GRP_GP  AS COL_OUTDIA_GRP_GP
				             , A.YD_STK_SKID_GP         AS COL_YD_STK_SKID_GP
				             , A.YD_STK_COL_W_GP        AS COL_W_GP
				             , A.YD_LOC_GP              AS COL_YD_LOC_GP
				             , C.COIL_OUTDIA_GRP_GP
				             , C.COIL_W_GP
				             , CASE WHEN SUBSTR(A.YD_STK_COL_GP,2,1) IN('B','C') AND TO_NUMBER(SUBSTR(A.YD_STK_COL_GP,3,2))<=40
				                         THEN 'FIRST'
				                    WHEN SUBSTR(A.YD_STK_COL_GP,2,1) IN('B','C') AND TO_NUMBER(SUBSTR(A.YD_STK_COL_GP,3,2))> 40
				                         THEN 'SECOND'
				                    ELSE 'ALL' END
				               AS BC_DONG_FLAG
				             , C.YD_EQP_ID
				          FROM TB_YD_STKCOL A
				             , TB_YD_STKBED B
				             , PARAM_TBL    C
				         WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
				           AND A.YD_GP         = SUBSTR(C.YD_SCH_CD, 1, 1)
				           AND A.YD_BAY_GP     = SUBSTR(C.YD_SCH_CD, 2, 1)
				           AND A.YD_EQP_GP     BETWEEN '01' AND '99'
				           AND A.YD_EQP_GP NOT IN ('01','80')
				           AND A.DEL_YN = 'N'
				           AND B.DEL_YN = 'N'
				        MINUS
				        SELECT C.YD_SCH_CD   AS YD_SCH_CD
				             , C.YD_ROUTE_GP AS YD_ROUTE_GP
				             , C.STL_NO      AS STL_NO
				             , A.YD_GP
				             , A.YD_BAY_GP
				             , A.YD_STK_COL_NO
				             , A.YD_STK_COL_GP
				             , A.YD_STKBED_USG_CD
				             , B.YD_STK_BED_NO
				             , B.YD_STK_BED_USG_GP
				             , B.YD_STK_BED_ACT_STAT
				             , B.YD_STK_BED_WHIO_STAT
				             , B.YD_STK_BED_W_GP
				             , A.YD_COIL_OUTDIA_GRP_GP  AS COL_OUTDIA_GRP_GP
				             , A.YD_STK_SKID_GP         AS COL_YD_STK_SKID_GP
				             , A.YD_STK_COL_W_GP        AS COL_W_GP
				             , A.YD_LOC_GP              AS COL_YD_LOC_GP
				             , C.COIL_OUTDIA_GRP_GP
				             , C.COIL_W_GP
				             , CASE WHEN SUBSTR(A.YD_STK_COL_GP,2,1) IN('B','C') AND TO_NUMBER(SUBSTR(A.YD_STK_COL_GP,3,2))<=40
				                        THEN 'FIRST'
				                    WHEN SUBSTR(A.YD_STK_COL_GP,2,1) IN('B','C') AND TO_NUMBER(SUBSTR(A.YD_STK_COL_GP,3,2))> 40
				                        THEN 'SECOND'
				                    ELSE 'ALL' END
				               AS BC_DONG_FLAG
				             , C.YD_EQP_ID
				          FROM TB_YD_STKCOL A
				             , TB_YD_STKBED B
				             , PARAM_TBL    C
				         WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
				           AND A.YD_GP         = SUBSTR(C.YD_SCH_CD, 1, 1)
				           AND A.YD_BAY_GP     = SUBSTR(C.YD_SCH_CD, 2, 1)
				           AND A.YD_EQP_GP     BETWEEN '01' AND '99'
				           AND A.DEL_YN        = 'N'
				           AND B.DEL_YN        = 'N'
				           AND A.YD_STKBED_USG_CD = DECODE(C.YD_ROUTE_GP
				                                             , 'A1' ,'T'
				                                             , 'A2' ,'Y'
				                                             , 'A3' ,'S'
				                                             , 'D0' ,'J' )
				       ) X
				     , TB_YD_STKLYR Z
				     , TB_YD_STKCOL C
				 WHERE Z.YD_STK_COL_GP = X.YD_STK_COL_GP
				   AND Z.YD_STK_BED_NO = X.YD_STK_BED_NO
				   AND Z.YD_STK_COL_GP = C.YD_STK_COL_GP
				   AND C.YD_LOC_GP     = 'J'
				   AND C.YD_EQP_GP     NOT IN ('01', '80')
				   AND ( (
				            (NVL(X.COL_W_GP, '*') = NVL(X.COIL_W_GP, '*'))  AND  (NVL(X.COL_OUTDIA_GRP_GP, '*') = NVL(X.COIL_OUTDIA_GRP_GP, '*'))
				         )
				         OR
				         (
				            X.YD_ROUTE_GP = 'G0' AND
				                 X.YD_STK_COL_GP IN (SELECT YD_STK_COL_GP
				                                       FROM USRYDA.TB_YD_LOCSRCHRNG A
				                                          , USRYDA.TB_YD_LOCSRCHBED B
				                                          , USRYDA.TB_YD_SCHRULE C
				                                      WHERE A.YD_LOC_SRCH_RNG_REG_SNO = B.YD_LOC_SRCH_RNG_REG_SNO
				                                        AND A.YD_SCH_CD = C.YD_SCH_CD
				                                        AND YD_ROUTE_GP = 'G0'
				                                        AND A.DEL_YN    = 'N'
				                                        AND CD_CONTENTS LIKE '%입고%'
				                                        AND C.YD_GP     = 'Y'
				                                      GROUP BY YD_STK_COL_GP
				                                    )
				         )
				       )
				   AND Z.YD_STK_LYR_ACT_STAT   = (CASE WHEN SUBSTR(X.YD_SCH_CD, 3, 2) = 'HC' THEN 'S' ELSE 'E' END)
				   AND Z.YD_STK_LYR_MTL_STAT   = 'E'
				   AND Z.DEL_YN                = 'N'
				   AND C.YD_STK_COL_TOLOC_STAT = 'Y'
				   AND C.MATL_SUP_MTD_GP       IS NULL --//적치폭기준 0:1000~1100 , 1:1100~1199 , 4:1400~1499 , 5:1500~1600
				   AND SUBSTR(X.YD_STK_COL_GP, 3, 1) NOT IN ('6', '7')
				   AND SUBSTR(X.YD_STK_COL_GP, 1, 4) NOT IN ('JA31', 'JA32', 'JB31', 'JC32')  --//공냉장 제외 처리
				   AND CASE WHEN X.YD_EQP_ID IN ('JBCRB1','JBCRB2','JCCRC1','JCCRC2')
				            THEN 'FIRST'
				            WHEN X.YD_EQP_ID IN ('JBCRB3','JBCRB4','JCCRC3','JCCRC4')
				            THEN 'SECOND'
				            ELSE 'ALL' END
				       = BC_DONG_FLAG
				)
				, TO_LOC_DATA_TABLE AS (
				SELECT A.STL_NO
				     , B.YD_EQP_WRK_MODE2
				     , A.TAG_YD_STK_COL_GP
				     , A.TAG_YD_STK_BED_NO
				     , A.TAG_YD_STK_LYR_NO
				     , (SELECT MAX(YD_STK_BED_NO) FROM TB_YD_STKLYR WHERE YD_STK_COL_GP = A.TAG_YD_STK_COL_GP AND ROWNUM = 1) AS COL_MAX_BED_NO
				     , A.COL_OUTDIA_GRP_GP
				     , A.COL_W_GP
				     , A.COL_YD_LOC_GP
				     , A.COL_YD_STK_SKID_GP
				     , A.COIL_OUTDIA_GRP_GP
				     , A.COIL_W_GP
				     , A.CHK_SKID_GP
				     , A.TAG_LEFT_BED
				     , A.TAG_LEFT_LAYER
				     , (SELECT YD_STK_LYR_ACT_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_LEFT_ACTIVE_STAT
				     , (SELECT YD_STK_LYR_MTL_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_LEFT_LAYER_STAT
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_LEFT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_LEFT_STL_NO
				     , A.TAG_RIGHT_BED
				     , A.TAG_RIGHT_LAYER
				     , (SELECT YD_STK_LYR_ACT_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_RIGHT_ACTIVE_STAT
				     , (SELECT YD_STK_LYR_MTL_STAT
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_RIGHT_LAYER_STAT
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP  = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO  = A.TAG_RIGHT_BED
				           AND B.YD_STK_LYR_NO  = A.TAG_RIGHT_LAYER
				           AND B.DEL_YN = 'N') AS TAG_RIGHT_STL_NO
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO   = A.TAG_LEFT_BED
				           AND B.YD_STK_LYR_NO   = '002'
				           AND B.DEL_YN = 'N' ) AS TAG_2DAN_LEFT_STL_NO
				     , (SELECT STL_NO
				          FROM TB_YD_STKLYR B
				         WHERE B.YD_STK_COL_GP   = A.TAG_YD_STK_COL_GP
				           AND B.YD_STK_BED_NO   = A.TAG_YD_STK_BED_NO
				           AND B.YD_STK_LYR_NO   = '002'
				           AND B.DEL_YN = 'N' ) AS TAG_2DAN_RIGHT_STL_NO
				 FROM TO_LOC_ALL A
				    , (SELECT YD_EQP_WRK_MODE2 FROM TB_YD_EQP WHERE YD_EQP_ID = :V_YD_EQP_ID) B
				)
				, TO_LOC_DATA_COMP_TABLE AS (
				--*--*--*--*--*--*-- 적치가능위치
				SELECT *
				  FROM (
				        SELECT
				               -- 1단우선적치 B군(폭1200이상,외경1800이상) C군(폭1200,외경1990이상)
				               CASE WHEN C_COIL_OUTDIA_GP = 'B' AND C_WIDTH >= 1200 AND C_OUTDIA >= 1800 THEN '001'
				                    WHEN C_COIL_OUTDIA_GP = 'C' AND C_WIDTH >= 1200 AND C_OUTDIA >= 1990 THEN '001'
				                    ELSE '002'
				               END AS ABOVE_LYR
				             , KK.*
				          FROM
				               (
				                SELECT K.*
				                     , C.COIL_NO      AS C_COIL_NO
				                     , C.COIL_T       AS C_THICK
				                     , C.COIL_W       AS C_WIDTH
				                     , C.COIL_WT      AS C_WEIGTH
				                     , C.COIL_OUTDIA  AS C_OUTDIA
				                     , C.CURR_PROG_CD AS C_PROG_CD
				                     , C.ORD_NO       AS C_ORD_NO      -- 주문번호
				                     , C.ORD_DTL      AS C_ORD_DTL     -- 주문행번
				                     , C.DEMANDER_CD  AS C_DEMANDER_CD -- 수요가코드
				                     , (CASE WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(C.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
				                             WHEN C.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN C.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR C.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             ELSE '' END
				                       ) AS C_YEOJAE_CAUSE_CD
				                     , TRUNC((SYSDATE - C.HRMILL_CMPL_DT )*24)             AS C_HOT_COIL_MIN
				                     , CASE WHEN C.COIL_OUTDIA <= 1280 THEN 'A'
				                            WHEN C.COIL_OUTDIA >  1930 THEN 'C'
				                            ELSE 'B' END  AS C_COIL_OUTDIA_GP
				                     , CASE WHEN C.COIL_W < 1601     THEN 'M' ELSE 'L' END AS C_COIL_W_GP
				                     , L.COIL_NO      AS L_COIL_NO
				                     , L.COIL_T       AS L_THICK
				                     , L.COIL_W       AS L_WIDTH
				                     , L.COIL_WT      AS L_WEIGTH
				                     , L.COIL_OUTDIA  AS L_OUTDIA
				                     , L.CURR_PROG_CD AS L_PROG_CD
				                     , L.ORD_NO       AS L_ORD_NO      -- 주문번호
				                     , L.ORD_DTL      AS L_ORD_DTL     -- 주문행번
				                     , L.DEMANDER_CD  AS L_DEMANDER_CD -- 수요가코드
				                     , (CASE WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(L.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
				                             WHEN L.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN L.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR L.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             ELSE '' END
				                       ) AS L_YEOJAE_CAUSE_CD
				                     , TRUNC((SYSDATE - L.HRMILL_CMPL_DT )*24) AS L_HOT_COIL_MIN
				                     , R.COIL_NO      AS R_COIL_NO
				                     , R.COIL_W       AS R_WIDTH
				                     , R.COIL_T       AS R_THICK
				                     , R.COIL_WT      AS R_WEIGTH
				                     , R.COIL_OUTDIA  AS R_OUTDIA
				                     , R.CURR_PROG_CD AS R_PROG_CD
				                     , R.ORD_NO       AS R_ORD_NO      -- 주문번호
				                     , R.ORD_DTL      AS R_ORD_DTL     -- 주문행번
				                     , R.DEMANDER_CD  AS R_DEMANDER_CD -- 수요가코드
				                     , (CASE WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD2,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD3,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD4,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD5,1,3) IN ('441','442','443') THEN ''
				                             WHEN SUBSTR(R.MID_INSPECT_DEFECT_CD1,1,3) IN ('451','452','453') THEN ''
				                             WHEN R.MID_INSPECT_DEFECT_CD1 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD2 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD3 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD4 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             WHEN R.MID_INSPECT_DEFECT_CD5 LIKE '44%' OR R.MID_INSPECT_DEFECT_CD1 LIKE '45%' THEN '3G'
				                             ELSE '' END
				                       ) AS R_YEOJAE_CAUSE_CD
				                     , TRUNC((SYSDATE - R.HRMILL_CMPL_DT )*24) R_HOT_COIL_MIN
				                     , L.ISHOT        AS L_ISHOT
				                     , R.ISHOT        AS R_ISHOT
				                  FROM TO_LOC_DATA_TABLE K
				                     , (SELECT 1 T_ROW, A.*
				                          FROM USRPTA.TB_PT_COILCOMM  A ) C  --대상코일
				                     , (SELECT 1 T_ROW, A.*
				                          ,DECODE(NVL(DECODE(STL_APPEAR_GP,'Y','CG','CM'),'CM'),'CM',
				                                  CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 96
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                       WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
				                                       CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 48
				                                            THEN 'TRUE'
				                                            ELSE 'FALSE'
				                                       END
				                                  END) AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
				                          FROM USRPTA.TB_PT_COILCOMM  A ) L  --하단LEFT
				                     , (SELECT 1 T_ROW, A.*
				                             ,DECODE(NVL(DECODE(STL_APPEAR_GP,'Y','CG','CM'),'CM'),'CM',
				                                          CASE WHEN TO_CHAR(SYSDATE,'MM') IN ('03','04','05') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('06','07','08') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 96
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('09','10','11') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 72
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                               WHEN TO_CHAR(SYSDATE,'MM') IN ('12','01','02') THEN
				                                               CASE WHEN TO_NUMBER(TRUNC((SYSDATE - COIL_CREATE_DDTT) * 24)) < 48
				                                                    THEN 'TRUE'
				                                                    ELSE 'FALSE'
				                                               END
				                                          END) AS ISHOT  -- 냉각경과일자에 따른 분기별 핫코일 정의
				                          FROM USRPTA.TB_PT_COILCOMM  A ) R  --하단RIGHT
				                 WHERE K.STL_NO           = C.COIL_NO(+)
				                   AND K.TAG_LEFT_STL_NO  = L.COIL_NO(+)
				                   AND K.TAG_RIGHT_STL_NO = R.COIL_NO(+)

				                   --짱구코일 2단제외
				                   --AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND JJANG_GP = 'Y'                      THEN 0
				                   --             ELSE 1 END
				                   --2단일 경우 1단에 핫코일이 존재 시 2단제의
				                   AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002' AND (L.ISHOT ='TRUE' OR R.ISHOT='TRUE') THEN 0
				                                ELSE 1 END
				                   --2단일 경우 좌우 적치 상태 CHECK
				                   AND 1 = CASE WHEN TAG_YD_STK_LYR_NO = '002'
				                                     AND (TAG_LEFT_ACTIVE_STAT != 'E' OR TAG_RIGHT_ACTIVE_STAT != 'E'
				                                       OR TAG_LEFT_LAYER_STAT  != 'C' OR TAG_RIGHT_LAYER_STAT  != 'C') THEN 0
				                                ELSE 1 END
				                   --1단일 경우 좌우 상단 적치 상태 CHECK
				                   AND 1 = CASE WHEN K.TAG_YD_STK_LYR_NO = '001' AND (TAG_2DAN_LEFT_STL_NO IS NOT NULL OR TAG_2DAN_RIGHT_STL_NO IS NOT NULL) THEN 0
				                                ELSE 1 END

				               ) KK


				         --무인크레인 이동가능 여부 CHECK
				--         WHERE ( TAG_YD_STK_COL_GP IN (SELECT R.ITEM
				--                                         FROM TB_YD_RULE      R
				--                                            , AUTO_CR_TABLE   A
				--                                        WHERE R.REPR_CD_GP = 'CR0001'
				--                                          AND R.CD_GP      = A.EQUIP_GP
				--                                          AND R.DEL_YN     = 'N')
				--               )
				       )
				)
				--*--*--*--*--*--*-- 평점
				SELECT G.*
				     , CASE  -- 1단이면서 소재인 경우
				            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
				            THEN CASE WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌우) 진도코드
				                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌단) 진도코드
				                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우단) 진도코드
				                      WHEN L_COIL_NO  IS NULL     AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
				                      WHEN R_COIL_NO  IS NULL     AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED
				                      ELSE '7' END
				             -- 2단이면서 소재인 경우
				            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
				            THEN CASE --WHEN L_JJANG_GP  = 'Y'      OR  R_JJANG_GP = 'Y'      THEN '9' --짱구 위에서 못 올린다.
				                      WHEN L_WB        = 'Y'      OR  R_WB       = 'Y'      THEN '8' --좌하단우하단 작업 예약
				                      WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '2' --동일(좌하단우하단) 진도코드
				                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌하단) 진도코드
				                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우하단) 진도코드
				                      ELSE '2' END
				            -- 1단이면서 제품인 경우
				            WHEN TAG_YD_STK_LYR_NO = '001'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8')
				            THEN CASE WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
				                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
				                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우축 동일 고객사+주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우측 동일 주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌측   동일 주문번호행번
				                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우측   동일 주문번호행번
				                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우측 동일 주문번호
				                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우측   동일 주문번호
				                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌측   동일 주문번호
				                      WHEN L_COIL_NO IS NULL      AND R_COIL_NO IS NOT NULL THEN '2' --좌 공BED
				                      WHEN R_COIL_NO IS NULL      AND L_COIL_NO IS NOT NULL THEN '2' --우 공BED

				                      ELSE '7' END
				            -- 2단이면서 제품인 경우
				            WHEN TAG_YD_STK_LYR_NO = '002'     AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8') --제품
				            THEN CASE --WHEN L_JJANG_GP = 'Y'       OR  R_JJANG_GP = 'Y'      THEN '9' -- 짱구 위에서 못 올린다.
				                      WHEN L_WB = 'Y'             OR  R_WB  = 'Y'           THEN '8' --좌하단우하단 작업 예약
				                      WHEN C_DEMANDER_CD          =   L_DEMANDER_CD
				                           AND C_DEMANDER_CD      =   R_DEMANDER_CD
				                           AND C_ORD_NO||C_ORD_DTL=   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 고객사+주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL
				                           AND C_ORD_NO||C_ORD_DTL=   R_ORD_NO||R_ORD_DTL   THEN '2' --좌우하단 동일 주문번호행번

				                      WHEN C_ORD_NO||C_ORD_DTL    =   L_ORD_NO||L_ORD_DTL   THEN '2' --좌하단   동일 주문번호행번
				                      WHEN C_ORD_NO||C_ORD_DTL    =   R_ORD_NO||R_ORD_DTL   THEN '2' --우하단   동일 주문번호행번
				                      WHEN C_ORD_NO = L_ORD_NO    AND C_ORD_NO = R_ORD_NO   THEN '2' --좌우하단 동일 주문번호
				                      WHEN C_ORD_NO = L_ORD_NO                              THEN '2' --우하단   동일 주문번호
				                      WHEN C_ORD_NO = R_ORD_NO                              THEN '2' --좌하단   동일 주문번호
				                      ELSE '2' END

				             ELSE '9' END GRADE
				  FROM (SELECT A.*
				             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
				                  FROM TB_YD_WRKBOOK    A1
				                     , TB_YD_WRKBOOKMTL B1
				                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                   AND A1.DEL_YN      = 'N'
				                   AND B1.DEL_YN      = 'N'
				                   AND B1.STL_NO      = A.C_COIL_NO
				               ) C_WB
				             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
				                  FROM TB_YD_WRKBOOK    A1
				                     , TB_YD_WRKBOOKMTL B1
				                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                   AND A1.DEL_YN      = 'N'
				                   AND B1.DEL_YN      = 'N'
				                   AND B1.STL_NO      = A.L_COIL_NO
				               ) L_WB
				             , (SELECT DECODE(COUNT(*), 0, 'N', 'Y')
				                  FROM TB_YD_WRKBOOK    A1
				                     , TB_YD_WRKBOOKMTL B1
				                 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                   AND A1.DEL_YN      = 'N'
				                   AND B1.DEL_YN      = 'N'
				                   AND B1.STL_NO      = A.R_COIL_NO
				               ) R_WB
				          FROM TO_LOC_DATA_COMP_TABLE A
				       ) G
				 ORDER BY GRADE
				        , (CASE WHEN ABOVE_LYR = TAG_YD_STK_LYR_NO THEN 1 ELSE 2 END) -- 1단우선적치 B군(폭1200이상,외경1800이상) C군(폭1200,외경1990이상)
				        , TAG_YD_STK_BED_NO
				        , TAG_YD_STK_LYR_NO
				 */       
				jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocPrimaryWorkThirdJ", logId, mthdNm, "제품동일한 적치가능한 베드 조회 Third");

			}
		

			
			if (jsResult.size() <= 0) {
				sLogMsg = "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, sLogMsg, "SL");

				if ("Y".equals(schLogYn)) {
					sSchLogContents = "[getYdToLocPrimaryWorkJ] 대상코일위치검색실패:"+ sStlNo+" LOG :"+"\r\n";
					this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
				}
				
				jrRtn.setField("RTN_CD" , "0");
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;
			} else {
				
				/****************************
				 *  log용
				 *****************************/
				JDTORecord jrResult1 = JDTORecordFactory.getInstance().create();
				String  sLocAbleMsg = "";
				for (int i = 1; i <= jsResult.size(); i++) {
					jsResult.absolute(i);
					jrResult1  = jsResult.getRecord();
								
					ydStkColGp 	= commUtils.trim(jrResult1.getFieldString("TAG_YD_STK_COL_GP"  ));
					ydStkBedNo 	= commUtils.trim(jrResult1.getFieldString("TAG_YD_STK_BED_NO"  ));
					ydStkLyrNo 	= commUtils.trim(jrResult1.getFieldString("TAG_YD_STK_LYR_NO"  ));
					sGrade 		= commUtils.nvl (jrResult1.getFieldString("GRADE"),"9");
					
					sLocAbleMsg  = sLocAbleMsg + "[getYdToLocPrimaryWorkJ] 적치가능대상코일위치="+" 열:"+ ydStkColGp+" 베드:"+ydStkBedNo+" 단:"+ydStkLyrNo+" 평점:"+sGrade+"\r\n";
				}		
				if ("Y".equals(schLogYn)) {
					sSchLogContents = "[getYdToLocPrimaryWorkJ] 대상코일위치검색:"+ sStlNo+"\r\n" + sLocAbleMsg;
					this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
				}
			}
		    //----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 SCH RULL 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord  jrRuleConst = this.toLocRuleConst(logId, mthdNm);
			//----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 LOGIC 적용여부 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrLocRuleApply = this.toLocRuleApply(logId, mthdNm);
			commUtils.printParam(logId, jrLocRuleApply);
			
			JDTORecord jrResult	= JDTORecordFactory.getInstance().create();

			String sLocAbleRtn     = "";
			String sLocAbleRtnMsg  = "";
			
			for (int i = 1; i <= jsResult.size(); i++) {
				jsResult.absolute(i);
				jrResult  = jsResult.getRecord();
							
				sGrade 		= commUtils.nvl (jrResult.getFieldString("GRADE"),"9");
				ydStkColGp 	= commUtils.trim(jrResult.getFieldString("TAG_YD_STK_COL_GP"  ));
				ydStkBedNo 	= commUtils.trim(jrResult.getFieldString("TAG_YD_STK_BED_NO"  ));
				ydStkLyrNo 	= commUtils.trim(jrResult.getFieldString("TAG_YD_STK_LYR_NO"  ));	
	
				/******************* 
				 * 적치가능 check
				 ******************/
				JDTORecord jrLocAbleRtn = this.toLocAbleCheck(logId, mthdNm, jrResult, jrRuleConst, jrLocRuleApply);
			//
				sLocAbleRtn    = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
				sLocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
				if (sLocAbleRtnMsg.length() > 0 ) {
					sDBLogMsg = sDBLogMsg + sLocAbleRtnMsg +"\r\n";
				}
				if ("1".equals(sLocAbleRtn)) {
					sLogMsg = ydStkColGp + ydStkBedNo + ydStkLyrNo+"  적치가능 위치평점:"+ sGrade;
					commUtils.printLog(logId, sLogMsg, "SL");				
				    //적치가능 
					sRtnBedDan = ydStkColGp + ydStkBedNo + ydStkLyrNo;
	    			break;
				}
			}			
			
			if (sRtnBedDan.length() < 10) {
				sLogMsg = "크레인작업재료의  재료정보["+sStlNo+"]에 대한 TO위치 결정 실패 ";
				commUtils.printLog(logId, sLogMsg, "SL");

				if ("Y".equals(schLogYn)) {
					sSchLogContents = "[getYdToLocPrimaryWorkJ] 대상코일선택실패:"+ sStlNo+"\r\n"+" LOG :"+"\r\n" + sDBLogMsg;
					this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
				}
				
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sLogMsg);
				return jrRtn;				
			}
			
			
			if ("Y".equals(schLogYn)) {
				sSchLogContents = "[getYdToLocPrimaryWorkJ] 대상코일선택:"+ sStlNo+"  선택위치:"+ sRtnBedDan +"  평점:" + sGrade +"\r\n"+" LOG :"+"\r\n" + sDBLogMsg;
				this.procSchLogYN( logId, mthdNm , schLogYn ,sStlNo ,ydCrnSchId , ydSchCd , sSchLogContents );		
			}
			
			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = commUtils.getParam(logId, mthdNm, sModifier);

			jrSetLoc.setField("YD_CRN_SCH_ID"	, ydCrnSchId); 
			jrSetLoc.setField("YD_EQP_ID"		, ydEqpId);	 
			jrSetLoc.setField("YD_SCH_CD"		, ydSchCd);	 
			jrSetLoc.setField("YD_UP_WO_LOC"	, ydUpWoLoc); 
			jrSetLoc.setField("YD_UP_WO_LAYER"	, ydUpWoLayer);	 
			jrSetLoc.setField("YD_DN_WO_LOC"	, sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER"	, sRtnBedDan.substring(8));
			jrSetLoc.setField("YD_WBOOK_ID"		, ydWbookId); 
				
			JDTORecord jrRcvRtn = this.toLocUpdCrnSch(logId,mthdNm, jrSetLoc, jrCrnSch  );
			jrRtn.setField("RTN_CD" , commUtils.trim(jrRcvRtn.getFieldString("RTN_CD")));	
			jrRtn.setField("RTN_MSG", commUtils.trim(jrRcvRtn.getFieldString("RTN_MSG")));
			jrRtn.setField("TC_MOVE_YN", sTcMoveYn);
			
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
	    }//end of try~catch				

	}      
}


