/**
 * @(#)BSlabSchSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      B열연 SLAB 야드 Schedule 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bslab.session;


import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.util.YmConstant;
import com.inisteel.cim.ym.bslab.session.BSlabComm;


import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

/**
 *      [A] 클래스명 : B열연 SLAB 야드 Schedule 처리 
 * 
 * @ejb.bean name="BSlabSchSeEJB" jndi-name="BSlabSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class BSlabSchSeEJBSBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();
	private BSlabComm bslabComm = new BSlabComm();
	private YmComm YmComm = new YmComm();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	
	
	/**	
	 *      [A] 오퍼레이션명 : SLAB 크레인스케줄(YMYMJ202)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ202(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "SLAB 크레인스케줄MAIN[BSlabSchSeEJB.rcvYMYMJ202] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecordSet jsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			jrRtn = commUtils.addSndData(jrRtn, this.procYMYMJ202(rcvMsg));
			
			commUtils.printLog(logId, methodNm + "[스케쥴메인종료]", "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 *      [A] 오퍼레이션명 : 슬라브크레인스케줄 멀티기동(YMYMJ302)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ203(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "슬라브크레인스케줄MAIN멀티기동 [BSlabSchSeEJB.멀티기동] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			int schCnt  = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("SCH_CNT"),"0")); 

			for(int i = 1 ; i<=schCnt; i++){				
				String ydWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"+i  )); //야드작업예약ID

				if(ydWbookId.equals("") ||ydWbookId.length() == 0 ) {
	           	   continue;
				}
				jrParam.setField("JMS_TC_CD"			, "YMYMJ202"); 
				jrParam.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
				jrParam.setField("YD_WBOOK_ID"			, ydWbookId); //야드작업예약ID
				jrParam.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
				jrParam.setField("YD_EQP_ID"  			, ""); //야드설비ID					
				jrRtn = commUtils.addSndData(jrRtn, this.procYMYMJ202(jrParam));
				
				// 이송하차 인 경우 동간작업기준에 대차 상차인 경우에는 1개만 기동 시킨다.
				if(i == 1) {
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPtToTcChk 
					SELECT *
					  FROM TB_YM_WRKBOOK WB
					     , (SELECT EQUIP_GP
					             , CARLD_SCH_CD
					             , CARUD_SCH_CD
					             , SUBSTR(CARLOAD_STOP_LOC,1,2)  ||SUBSTR(CARLD_SCH_CD,3,6) AS UP_SCH
					             , SUBSTR(CARUNLOAD_STOP_LOC,1,2)||SUBSTR(CARUD_SCH_CD,3,6) AS DN_SCH
					          FROM TB_YM_EQUIP WHERE EQUIP_GP LIKE '2XTC0%')  EQ 
					 WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND WB.DEL_YN = 'N'
					   AND WB.YD_SCH_CD = EQ.UP_SCH
					   AND WB.YD_SCH_CD LIKE '2_PT02LM
					*/
					JDTORecordSet jsPtToTcChk = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPtToTcChk", logId, methodNm, "대차 상차여부 조회");
					if(jsPtToTcChk.size() > 0) {
						break;
					}
				}
			}	
			
			commUtils.printLog(logId, methodNm + "[스케쥴메인종료]", "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**	
	 *      [A] 오퍼레이션명 : SLAB크레인스케줄(YMYMJ202)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procYMYMJ202(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "SLAB크레인스케줄MAIN[BSlabSchSeEJB.procYMYMJ202] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecordSet jsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");
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

			JDTORecord jrRtn = null;	 //전문 Return
			String trtMsg  = ""; 		 //처리메세지
			String ydL3Msg = ""; 		 //야드L3MESSAGE
			String stackLayerChkYn = ""; //적재위치 CHECK 여부
			
			String sAPP005_YN = YmComm.BCoilApplyYn("APP005","2","1");   //slab_log여부
			commUtils.printLog(logId,  "TO위치 LOG 적용:" + sAPP005_YN, "SL");	
			
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
				JDTORecord jrChk = YmComm.chkSchCd(jrParam);
				
				ydL3Msg = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"));

				if (!"".equals(ydL3Msg)) {
					ydSchCd = "";
				}
			}

			//설비ID Check
			if ("".equals(ydWbookId) && "".equals(ydSchCd) && !"".equals(ydEqpId)) {
				JDTORecord jrChk = YmComm.chkEqpStat(jrParam);

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
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbook
				SELECT YD_WBOOK_ID
				     -- 야드스케쥴분할구분을 저장위치 CHECK 여부로 사용
				     , NVL((SELECT YD_SCH_DIV_GP 
				              FROM TB_YM_SCHEDULERULE 
				             WHERE YD_SCH_CD = A.YD_SCH_CD),'Y') AS  STACK_LAYER_CHK_YN
				  FROM TB_YM_WRKBOOK A
				 WHERE A.YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND A.DEL_YN      = 'N'
				   AND A.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
				                               FROM TB_YM_CRNSCH
				                              WHERE DEL_YN = 'N')
				*/
				jsWbook = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbook", logId, methodNm, "작업예약 조회");
				
			} else if (!"".equals(ydSchCd)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbookEqp
				SELECT YD_WBOOK_ID
				 -- 야드스케쥴분할구분을 저장위치 CHECK 여부로 사용
				     , NVL(A.YD_SCH_DIV_GP,'Y') AS  STACK_LAYER_CHK_YN
				  FROM (SELECT WB.YD_WBOOK_ID
				             , SR.YD_SCH_DIV_GP
				          FROM TB_YM_SCHEDULERULE SR
				              ,TB_YM_WRKBOOK WB
				         WHERE SR.YD_SCH_CD       = WB.YD_SCH_CD
				           AND NVL(YD_WRK_PLAN_CRN,SR.YD_WRK_CRN)      = :V_YD_EQP_ID
				           AND SR.YD_SCH_PROH_EXN = 'N'
				           AND SR.DEL_YN          = 'N'
				           AND WB.DEL_YN          = 'N'
				           AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
				                                        FROM TB_YM_CRNSCH
				                                       WHERE DEL_YN = 'N')
				         ORDER BY WB.YD_SCH_PRIOR, WB.YD_WBOOK_ID) A
				 WHERE ROWNUM = 1
				*/ 
				jsWbook = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbookSchcd", logId, methodNm, "작업예약 조회");
				 
			} else if (!"".equals(ydEqpId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbookEqp
				SELECT YD_WBOOK_ID
				 -- 야드스케쥴분할구분을 저장위치 CHECK 여부로 사용
				     , NVL(A.YD_SCH_DIV_GP,'Y') AS  STACK_LAYER_CHK_YN
				  FROM (SELECT WB.YD_WBOOK_ID
				             , SR.YD_SCH_DIV_GP
				          FROM TB_YM_SCHEDULERULE SR
				              ,TB_YM_WRKBOOK WB
				         WHERE SR.YD_SCH_CD       = WB.YD_SCH_CD
				           AND NVL(WB.YD_WRK_PLAN_CRN,SR.YD_WRK_CRN)      = :V_YD_EQP_ID
				           AND SR.YD_SCH_PROH_EXN = 'N'
				           AND SR.DEL_YN          = 'N'
				           AND WB.DEL_YN          = 'N'
				           AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
				                                        FROM TB_YM_CRNSCH
				                                       WHERE DEL_YN = 'N')
				         ORDER BY WB.YD_SCH_PRIOR, WB.YD_WBOOK_ID) A
				 WHERE ROWNUM = 1
				*/ 
				jsWbook = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbookEqp", logId, methodNm, "작업예약 조회");
				 
			} else {
				throw new Exception("오류:작업예약ID조회 항목 없음");
			}

			if (jsWbook != null && jsWbook.size() > 0) {
				ydWbookId 		= commUtils.trim(jsWbook.getRecord(0).getFieldString("YD_WBOOK_ID"));
				stackLayerChkYn = commUtils.trim(jsWbook.getRecord(0).getFieldString("STACK_LAYER_CHK_YN"));
			} else {
				
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWrkBookMtldel
				SELECT YD_WBOOK_ID
				     , REGISTER
				     , MODIFIER
				     , DEL_YN
				  FROM TB_YM_WRKBOOK 
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND MODIFIER = 'SCH_DEL'  
				*/
				JDTORecordSet jsWbDelInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWrkBookMtldel", logId, methodNm, "작업 SKIP 여부");
				if(jsWbDelInfo.size() > 0 ) {
					commUtils.printLog(logId, "스케쥴코드[" + ydSchCd + "], 설비ID[" + ydEqpId + "], 작업예약ID[" + ydWbookId + "], 수정자[" + modifier + "] 상단작업예약과 결합" , "SL");
					return jrRtn;
				} else  {
					//throw new Exception("오류:" + trtMsg + " >> 작업예약정보 없음");
					commUtils.printLog(logId, "오류:" + trtMsg + " >> 작업예약정보 없음", "SL");
					return jrRtn;
				}
			}

			commUtils.printLog(logId, trtMsg + " >> 결정된 작업예약ID [" + ydWbookId + "]", "SL");

			jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID


			//2024.03.29 HJW 재료 중복적치 여부 확인하여 exception 추가 (ora- 에러 방지용).
			//작업예약 재료의 저장위치 등록된 걸로 추후 시스템에서 사용하기 때문에,
			//중복적치현상 발생으로 작업예약 재료의 위치를 정확히 가져오지 못하면, 추후 실적처리도 이상해질 수 있음.
			if(chkDupStkByWBookId(ydWbookId,logId)){
				commUtils.printLog(logId, "작업예약재료 중복적치현상 발생", "SL");
				throw new Exception("오류 : 작업예약재료 중복적치현상 발생");
			}
			
			
			/**********************************************************
			* 1.2 크레인 작업 재료에 현재 적치단 저장위치 Update (별도 Transaction 으로 처리)
			**********************************************************/
			EJBConnector tranConn = new EJBConnector("default", "BSlabSchSeEJB", this);
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
			
			String szYD_TO_LOC_GUIDE_FNL = ""; //E100 서포팅블록 이상시 To위치 가이드
			
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnSchStat
			--크레인스케줄 상태정보 조회 
			SELECT WB.YD_GP                                         --야드구분
			      ,WB.YD_BAY_GP                                     --야드동구분
			      ,WB.YD_SCH_CD                                     --야드스케쥴코드
			      ,WB.YD_SCH_PRIOR                                  --야드스케쥴우선순위
			      ,WB.YD_TO_LOC_DCSN_MTD                            --야드To위치결정방법
			      ,WB.YD_TO_LOC_GUIDE                               --야드To위치Guide
			      ,TO_CHAR(WB.REG_DDTT,'YYYYMMDDHH24MISS') AS YD_WBOOK_DT --야드작업예약일시
			      ,CASE WHEN WB.YD_SCH_CD LIKE '__PT__U_' THEN 'C'  --차량상차
			            WHEN WB.YD_SCH_CD LIKE '__TC__U_' THEN 'T'  --대차상차
			            WHEN LENGTH(WB.YD_TO_LOC_GUIDE) >= 4        --Span구분 이상이면
			             AND WB.YD_TO_LOC_GUIDE LIKE WB.YD_GP||WB.YD_BAY_GP||'%' THEN 'G' --To위치Guide
			      	    ELSE 'Z'                                    --기타
			      	END AS TO_LOC_CHK_GP                            --To위치점검구분
			      ,SR.YD_SCH_PROH_EXN                               --야드스케쥴금지유무
			      
			      ,WB.YD_WRK_PLAN_CRN AS YD_WRK_CRN_PLN             --계획크레인
			      ,E0.WPROG_STAT      AS YD_EQP_STAT_PLN            --계획크레인 야드설비상태
			      ,E0.WORK_MODE       AS YD_EQP_WRK_MODE_PLN        --계획크레인 야드설비작업Mode
			      
			      ,SR.YD_WRK_CRN                                    --작업크레인
			      ,SR.YD_WRK_CRN_PRIOR                              --작업크레인우선순위
			      ,E1.WPROG_STAT      AS YD_EQP_STAT_WRK            --작업크레인 야드설비상태
			      ,E1.WORK_MODE       AS YD_EQP_WRK_MODE_WRK        --작업크레인 야드설비작업Mode
			      
			      ,SR.YD_ALT_CRN                                    --대체크레인
			      ,SR.YD_ALT_CRN_PRIOR                              --대체크레인우선순위
			      ,E2.WPROG_STAT      AS YD_EQP_STAT_ALT            --대체크레인 야드설비상태
			      ,E2.WORK_MODE       AS YD_EQP_WRK_MODE_ALT        --대체크레인 야드설비작업Mode
			      
			      ,NVL(WM.TT_MTL_SH,0) AS TT_MTL_SH                 --전체 재료매수
			      ,NVL(WM.WM_MTL_SH,0) AS WM_MTL_SH                 --작업예약 재료매수
			      ,NVL(WM.ST_MTL_SH,0) AS ST_MTL_SH                 --저장품 재료매수
			      ,NVL(WM.SL_MTL_SH,0) AS SL_MTL_SH                 --적치단 재료매수
			      ,NVL(WM.STAT_C_SH,0) AS STAT_C_SH                 --적치중인 재료매수
			      ,(SELECT COUNT(*)
			          FROM TB_YM_WRKBOOKMTL WM
			              ,TB_YM_STACKLAYER     SL
			         WHERE WM.STOCK_ID    = SL.STOCK_ID
			           AND WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
			           AND SL.STACK_COL_GP NOT LIKE SUBSTR(WB.YD_SCH_CD,1,2)||'%'
			           AND SL.STACK_LAYER_STAT = 'C'
			           AND WM.DEL_YN      = 'N'
			           AND SL.DEL_YN      = 'N') AS AB_LOC_SH       --저장위치이상 재료매수
			      , 'N' AS CM_DUP_YN                                -- 크레인스케줄 재료중복여부
			      , 'N' AS CL_DUP_GP                                -- 크레인스케줄 저장위치중복여부
			      ,WB.YD_CAR_USE_GP                                 -- 야드차량사용구분
			      ,WB.TRN_EQP_CD                                    -- 운송장비코드
			      ,WB.CAR_NO                                        -- 차량번호
			      ,WB.CARD_NO                                       -- 카드번호
			      ,(SELECT SUBSTR(CURR_STOP_LOC,2,1)
			          FROM TB_YM_EQUIP EQ
			         WHERE EQ.EQUIP_GP = SUBSTR(WB.YD_SCH_CD,1,1)||'XTC'||SUBSTR(WB.YD_SCH_CD,5,2)) AS YD_CURR_BAY_GP
			      , NVL((SELECT CASE WHEN MIN(CASE WHEN S2.STACK_LAYER_STAT = 'D' THEN '1' END ) > 0 THEN 'Y' ELSE 'N' END 
			            FROM TB_YM_WRKBOOKMTL WM
			              , TB_YM_STACKLAYER SL
			              , TB_YM_STACKLAYER S2
			          WHERE WM.STOCK_ID      = SL.STOCK_ID
			            AND WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
			            AND WM.DEL_YN      = 'N'
			            AND SL.DEL_YN      = 'N'
			            AND S2.STACK_COL_GP = SL.STACK_COL_GP 
			            AND S2.STACK_BED_GP = SL.STACK_BED_GP
			            AND S2.STACK_LAYER_GP > SL.STACK_LAYER_GP),0) AS UP_DN_GP    --상단 권하 대상 여부         
			            
			      , (SELECT CASE WHEN MAX(CAL_SLAB_WT) > CASE WHEN E0.WPROG_STAT != 'B' AND E0.WORK_MODE != 2 THEN E0.STACK_MAX_QNTY 
			                                                  WHEN E1.WPROG_STAT != 'B' AND E1.WORK_MODE != 2 THEN E1.STACK_MAX_QNTY 
			                                                  WHEN E2.WPROG_STAT != 'B' AND E2.WORK_MODE != 2 THEN E2.STACK_MAX_QNTY 
			                                                  END   THEN 'N' ELSE 'Y' END 
			           FROM USRPTA.TB_PT_MSLABCOMM MSC
			              , TB_YM_WRKBOOKMTL WM
			          WHERE MSC.MSLAB_NO   = WM.STOCK_ID
			            AND WM.YD_WBOOK_ID = WB.YD_WBOOK_ID) AS SLAB_WT_YN    
			            
			      , NVL(( CASE WHEN YD_WRK_PLAN_TCAR IS NULL THEN 'N'
			                   WHEN YD_WRK_PLAN_TCAR IS NOT NULL AND 
			                          ( SELECT COUNT(*)
			                              FROM TB_YM_WRKBOOK WB
			                                 , (SELECT EQUIP_GP
			                                         , SUBSTR(CARLOAD_STOP_LOC,1,2)  ||SUBSTR(CARLD_SCH_CD,3,6) AS UP_SCH
			                                         , SUBSTR(CARUNLOAD_STOP_LOC,1,2)||SUBSTR(CARUD_SCH_CD,3,6) AS DN_SCH
			                                      FROM TB_YM_EQUIP WHERE EQUIP_GP LIKE '2XTC0%')  EQ 
			                             WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
			                               AND WB.DEL_YN = 'N'
			                               AND WB.YD_WRK_PLAN_TCAR = EQ.EQUIP_GP
			                               AND ((WB.YD_SCH_CD = UP_SCH) OR (WB.YD_SCH_CD = UP_SCH))
			                          ) = 0 THEN 'Y' 
			                 ELSE 'N' END) ,'N')       AS TC_FLAG      
			  FROM TB_YM_WRKBOOK WB
			      ,TB_YM_SCHEDULERULE SR
			      ,TB_YM_EQUIP     E0
			      ,TB_YM_EQUIP     E1
			      ,TB_YM_EQUIP     E2
			      ,(SELECT WM.YD_WBOOK_ID
			              ,COUNT(*)                    AS TT_MTL_SH
			              ,COUNT(DISTINCT WM.STOCK_ID) AS WM_MTL_SH
			              ,COUNT(DISTINCT ST.STOCK_ID) AS ST_MTL_SH
			              ,COUNT(DISTINCT SL.STOCK_ID) AS SL_MTL_SH
			              ,SUM(DECODE(SL.STACK_LAYER_STAT,'C',1,'U',1)) AS STAT_C_SH --적치중인 재료매수
			              ,MAX(WM.YD_UP_COLL_SEQ)      AS YD_UP_COLL_SEQ
			          FROM TB_YM_WRKBOOKMTL WM
			              ,TB_YM_STOCK      ST
			              ,(SELECT * FROM TB_YM_STACKLAYER WHERE STACK_LAYER_STAT IN ('C','U'))     SL
			         WHERE WM.STOCK_ID      = ST.STOCK_ID(+)
			           AND WM.STOCK_ID      = SL.STOCK_ID(+)
			           AND WM.YD_WBOOK_ID = :V_YD_WBOOK_ID
			           AND WM.DEL_YN      = 'N'
			           AND ST.DEL_YN(+)   = 'N'
			           AND SL.DEL_YN(+)   = 'N'
			         GROUP BY WM.YD_WBOOK_ID) WM
			 WHERE WB.YD_SCH_CD       = SR.YD_SCH_CD(+)
			   AND SR.YD_WRK_CRN      = E1.EQUIP_GP(+)
			   AND SR.YD_ALT_CRN      = E2.EQUIP_GP(+)
			   AND WB.YD_WRK_PLAN_CRN = E0.EQUIP_GP(+)
			   AND WB.YD_WBOOK_ID     = WM.YD_WBOOK_ID(+)
			   AND WB.YD_WBOOK_ID     = :V_YD_WBOOK_ID
			   AND WB.DEL_YN          = 'N'
			   AND SR.DEL_YN(+)       = 'N'
			   AND E1.DEL_YN(+)       = 'N'
			   AND E2.DEL_YN(+)       = 'N'
			   AND E0.DEL_YN(+)       = 'N'
			*/		   
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnSchStat", logId, methodNm, "작업예약 조회");
			if (jsChk.size() <= 0) {
				throw new Exception("오류:" + trtMsg + " >> 상태정보 없음");
			} else {
				JDTORecord jrChk = jsChk.getRecord(0);

				ydSchCd                = commUtils.trim(jrChk.getFieldString("YD_SCH_CD"          ));	//야드스케쥴코드
				ydToLocDcsnMtd         = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_DCSN_MTD" ));	//야드To위치결정방법
				ydToLocGuide           = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_GUIDE"    ));	//야드To위치Guide
				toLocChkGp             = commUtils.trim(jrChk.getFieldString("TO_LOC_CHK_GP"      ));	//To위치점검구분
				ydSchPrior             = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN_PRIOR"   ));	//야드스케쥴우선순위
				
				String ydWrkPlanCrn    = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN_PLN"    ));	//계획크레인
				String ydEqpStatPln    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_PLN"    ));	//계획크레인 야드설비상태
				String ydEqpWrkModePln = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_PLN"));	//계획크레인 야드설비작업Mode
				
				String ydWrkCrn        = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN"         ));	//작업크레인
				String ydEqpStatWrk    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_WRK"    ));	//작업크레인 야드설비상태
				String ydEqpWrkModeWrk = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_WRK"));	//작업크레인 야드설비작업Mode

				String ydAltCrn        = commUtils.trim(jrChk.getFieldString("YD_ALT_CRN"         ));	//대체크레인
				String ydEqpStatAlt    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_ALT"    ));	//대체크레인 야드설비상태
				String ydEqpWrkModeAlt = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_ALT"));	//대체크레인 야드설비작업Mode
				
				String cmDupYn         = commUtils.trim(jrChk.getFieldString("CM_DUP_YN"          ));	//크레인스케줄 재료중복여부
				String clDupGp         = commUtils.trim(jrChk.getFieldString("CL_DUP_GP"          ));	//크레인스케줄 저장위치중복여부
				String upDnGp          = commUtils.trim(jrChk.getFieldString("UP_DN_GP"           ));	//상단 권하 SLAB 여부
				String slabWtYn        = commUtils.trim(jrChk.getFieldString("SLAB_WT_YN"         ));	//중량 53000 이상 
				String tcFlag          = commUtils.trim(jrChk.getFieldString("TC_FLAG"            ));	//대차인 경우 동간작업기준 조회
				
				
//				String sYD_EQP_STAT_ALT= commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_ALT"    ));	//대체크레인 설비상태
//				String sYD_EQP_WRK_MODE_ALT = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_ALT"));	//야드설비작업Mode(상대크레인 작업모드)
				
				ttMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("TT_MTL_SH"),"0"));	//전체 재료매수
				wmMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("WM_MTL_SH"),"0"));	//작업예약 재료매수
				stMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("ST_MTL_SH"),"0"));	//저장품 재료매수
				slMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("SL_MTL_SH"),"0"));	//적치단 재료매수
				statCSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("STAT_C_SH"),"0"));	//적치중인 재료매수(야드적치단재료상태가 적치 중[C] 인 재료수)
				abLocSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("AB_LOC_SH"),"0"));	//저장위치이상 재료매수
				
				
				
				
				szYD_TO_LOC_GUIDE_FNL = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_GUIDE_FNL")); //E100 서포팅블럭 이상시 To위치 가이드 
				
				
				
				
				//▶▶▶▶▶▶▶LOG_TABLE 
				if(sAPP005_YN.equals("Y")) {
					if (wmMtlSh == 0) {
						 trtMsg = "오류:>> 작업예약재료 정보 없음";
					} else if (wmMtlSh != ttMtlSh) {
						 trtMsg = "오류:>> 작업예약재료 적치단 중복 등록 [작업예약: " + wmMtlSh + ", 적치단: " + ttMtlSh + "]";
					} else if (wmMtlSh != stMtlSh) {
						 trtMsg = "오류:>> 작업예약재료 저장품 정보 이상 [" + (wmMtlSh - stMtlSh) + "매]";
					} else if ("Y".equals(cmDupYn)) {
						 trtMsg = "오류:>> 작업예약재료가 기 등록된 크레인작업재료와 중복";
					} else if ("1".equals(clDupGp)) {
						 trtMsg = "오류:>> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권하위치와 중복";
					} else if ("2".equals(clDupGp)) {
						 trtMsg = "오류:>> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권상위치와 중복";
					} else if ("Y".equals(upDnGp)) {
						 trtMsg = "오류:>> 상단에 권하 작업이 있습니다. 불가합니다.";
					} else if ("B".equals(ydEqpStatWrk) && "B".equals(ydEqpStatAlt) ) {
						 trtMsg = "오류:>> 작업 불가 합니다. 크레인 고장";
					} else if ("2".equals(ydEqpWrkModePln) && "2".equals(ydEqpWrkModeAlt) ) {
						 trtMsg = "오류:>> 작업 불가 합니다. 크레인 OFF-LINE";
					} else if ("Y".equals(tcFlag)) {
						 trtMsg = "오류:>> 대차작업시 동간작업기준과 틀립니다.";
					}
					if(trtMsg.length() > 5) {
						JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
		    			jrLog.setField("STOCK_ID"		, "SLAB");
		    			jrLog.setField("YD_CRN_SCH_ID"	, ydWbookId);
		    			jrLog.setField("YD_GP"			, "2");
		    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
		    			jrLog.setField("SCH_CONTENTS"	, "스케쥴대상 점검실패:"+ trtMsg+" LOG :"+"\r\n" );
	
		    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
		    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
					}	
				}	
				
				
				if (wmMtlSh == 0) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료 정보 없음");
				} else if (wmMtlSh != ttMtlSh) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치단 중복 등록 [작업예약: " + wmMtlSh + ", 적치단: " + ttMtlSh + "]");
				} else if (wmMtlSh != stMtlSh) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료 저장품 정보 이상 [" + (wmMtlSh - stMtlSh) + "매]");
				} else if ("Y".equals(cmDupYn)) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료가 기 등록된 크레인작업재료와 중복");
				} else if ("1".equals(clDupGp)) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권하위치와 중복");
				} else if ("2".equals(clDupGp)) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권상위치와 중복");
				} else if ("Y".equals(upDnGp)) {
					throw new Exception("오류:" + trtMsg + " >> 상단에 권하 작업이 있습니다. 불가합니다.");
				} else if ("B".equals(ydEqpStatWrk) && "B".equals(ydEqpStatAlt) ) {
					throw new Exception("오류:" + trtMsg + " >> 작업 불가 합니다. 크레인 고장."); 
				} else if ("2".equals(ydEqpWrkModePln) && "2".equals(ydEqpWrkModeAlt) ) {
					throw new Exception("오류:" + trtMsg + " >> 작업 불가 합니다. 크레인 OFF-LINE"); 
				} else if ("Y".equals(tcFlag)) {
					throw new Exception("오류:" + trtMsg + " >> 대차 작업시 동간작업기준과 틀립니다..");
					
//				} else if ("N".equals(slabWtYn)) {
//					throw new Exception("오류:" + trtMsg + " >> slab중량이 53000이상 초과. 불가합니다.");
				}
					
				/**********************************************************
				* 1.3 크레인 결정
				**********************************************************/

				if (!"".equals(ydWrkPlanCrn) && !"B".equals(ydEqpStatPln) && "1".equals(ydEqpWrkModePln)) {
					//작업예약 지정크레인 : 최우선 지정
					ydEqpId   = ydWrkPlanCrn;	//야드설비ID
					ydEqpStat = ydEqpStatPln;	//야드설비상태
					commUtils.printLog(logId, trtMsg + " >> 작업예약 지정크레인[" + ydWrkPlanCrn + "]으로 설정", "SL");
				} else if (!"".equals(ydWrkCrn) && !"B".equals(ydEqpStatWrk) && "1".equals(ydEqpWrkModeWrk)) {
					//작업크레인 :
					ydEqpId   = ydWrkCrn;		//야드설비ID
					ydEqpStat = ydEqpStatWrk;	//야드설비상태
				} else if (!"".equals(ydAltCrn) && !"B".equals(ydEqpStatAlt) && "1".equals(ydEqpWrkModeAlt)) {
					//대체크레인
					ydEqpId   = ydAltCrn;		//야드설비ID
					ydEqpStat = ydEqpStatAlt;	//야드설비상태
				} else {
					throw new Exception("오류:" + trtMsg + " >> >> 작업 불가 합니다. 크레인 고장");
				}
				
//				/***********************************************************
//				 * 1.3.1 크레인이 고장상태이면 대체크레인으로 편성
//				 ***********************************************************/
//				// 크레인이 고장이고 대체크레인이 고장이 아니면 대체크레인으로 교체
//				if ("B".equals(ydEqpStat)||"B".equals(ydEqpStat)) {
//					if (!"".equals(sYD_ALT_CRN) && !"B".equals(sYD_EQP_STAT_ALT)) {
//						ydEqpId   = sYD_ALT_CRN;
//						ydEqpStat = sYD_EQP_STAT_ALT;
//					}
//					
//					// 지정크레인이 고장, 해당 스케줄의 작업크레인이 고장이 아닐때
//					if (!"B".equals(ydEqpStatWrk)) {
//						ydEqpId   = ydWrkCrn;		//야드설비ID
//						ydEqpStat = ydEqpStatWrk;	//야드설비상태
//					}
//				} 
				
				/**********************************************************
				* 1.4 To위치 사전 점검
				*     - 차량상차 작업('C')
				*     - 야드To위치Guide('G')
				**********************************************************/
				//To위치 사전 점검
				
				//▶▶▶▶▶▶▶LOG_TABLE 
				if(sAPP005_YN.equals("Y")) {
					if ("C".equals(toLocChkGp)) {
						//차량상차작업
						if ("".equals(commUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP")))) {
							trtMsg = "오류:>> 차량상차작업 야드차량사용구분 없음";
							
						} else if ("L".equals(commUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP"))) 
								&& "".equals(commUtils.trim(jrChk.getFieldString("TRN_EQP_CD"   )))) {
							// 구내운송
							trtMsg = "오류:>> 구내운송 상차작업 운송장비코드 없음";
						} else if ("G".equals(commUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP")))) {
							// 출하
							if ("".equals(commUtils.trim(jrChk.getFieldString("CAR_NO")))) {
								trtMsg = "오류:>> 출하차량 상차작업 차량번호 또는 카드번호 없음";	
							}
						}
					} 

					if(trtMsg.length() > 5) {

						JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
		    			jrLog.setField("STOCK_ID"		, "SLAB");
		    			jrLog.setField("YD_CRN_SCH_ID"	, ydWbookId);
		    			jrLog.setField("YD_GP"			, "2");
		    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
		    			jrLog.setField("SCH_CONTENTS"	, "스케쥴대상 점검실패:"+ trtMsg+" LOG :"+"\r\n" );
	
		    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
		    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
					}	
				}	
				
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
				
//				//야드To위치Guide 값이 4자리 이상이고 To 야드동이 같을 경우가 아니면
//				//TO 위치 가이드('G') 아니면 야드To위치결정방법,야드To위치Guide CLEAR
//				if (!"G".equals(toLocChkGp)) {
//					ydToLocDcsnMtd = ""; //야드To위치결정방법
//					ydToLocGuide   = ""; //야드To위치Guide
//				}
			}
	

			// 행선 및저장품 이동 조건이 없으면 update
			EJBConnector tranConn1 = new EJBConnector("default", "BSlabSchSeEJB", this);
			tranConn1.trx("updCrnSchStock", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			JDTORecord jrParamSet = JDTORecordFactory.getInstance().create();
			
			jrParamSet.setResultCode(logId);	//Log ID
			jrParamSet.setResultMsg(methodNm);	//Log Method Name
			jrParamSet.setField("MODIFIER"   			, modifier ); //수정자
			jrParamSet.setField("YD_WBOOK_ID"			, ydWbookId); 			//야드작업예약ID
			jrParamSet.setField("YD_SCH_CD"  			, ydSchCd  ); 			//야드스케쥴코드
			jrParamSet.setField("YD_EQP_ID"  			, ydEqpId  ); 			//야드설비ID
			jrParamSet.setField("YD_SCH_PRIOR"  		, ydSchPrior  ); 		//야드스케쥴우선순위
			jrParamSet.setField("YD_TO_LOC_DCSN_MTD"  	, ydToLocDcsnMtd  ); 	//야드To위치결정방법
			jrParamSet.setField("YD_TO_LOC_GUIDE"  		, ydToLocGuide  ); 		//야드To위치Guide
			jrParamSet.setField("YD_WBOOK_MTL_CNT"   	, ""+wmMtlSh ); 		//작업예약 매수
			jrParamSet.setField("LOG_YN"   				, sAPP005_YN ); 		//LOG여부
			
			jrParamSet.setField("YD_TO_LOC_GUIDE_FNL"  	, szYD_TO_LOC_GUIDE_FNL  ); //E100 서포팅블록 이상시 To위치 가이드
			
			commUtils.printParam(logId, jrParamSet);
			/**********************************************************
			* 2.그룹핑 파라미터 셋팅
            *  2-1.주작업 및 보조 작업 셋팅
  			**********************************************************/
			commUtils.printLog(logId, "그룹핑 파라미터 셋팅 시작", "SL");			

			JDTORecordSet jsRecset	= JDTORecordFactory.getInstance().createRecordSet("Temp");
			intRtnVal = this.CrnSchGrp(logId, methodNm, jrParamSet, jsRecset);
			if(intRtnVal != 1) {
				m_ctx.setRollbackOnly();
				throw new Exception("오류:그룹핑 파라미터 이상 (재료 정보확인)");
			}
			
			if(jsRecset.size() == 0) {
				m_ctx.setRollbackOnly();
				throw new Exception("오류:그룹핑 파라미터 이상 (재료 정보확인)");
			}
			/**********************************************************
			* 3.크레인스케줄과 크레인작업재료 등록
			*   3-1.적치단의 재료상태를 권상대기로 변경처리
			*   3-2.크레인스케줄과 크레인작업재료 등록
  			**********************************************************/
			commUtils.printLog(logId, "크레인스케줄과 크레인작업재료 등록 시작 ", "SL");			
				
			intRtnVal = this.CrnSchIns(logId, methodNm, jrParamSet, jsRecset);
			
			if(intRtnVal != 1) {
				m_ctx.setRollbackOnly();
				throw new DAOException("크레인스케줄 및 작업재료 등록 오류");
			}
			
			if(jsRecset.size() == 0) {
				m_ctx.setRollbackOnly();
				throw new Exception("오류:크레인스케줄 및 작업재료 등록 오류");
			}

			/**********************************************************
			* 4.TO 저장위치 결정
  			**********************************************************/
			commUtils.printLog(logId, "TO 저장위치 등록 시작 ", "SL");			
			
			JDTORecord jrLocSrcRngRtn = this.LocSrcRngDataSet(logId, methodNm, jrParamSet);
			
			commUtils.printLog(logId, commUtils.trim(jrLocSrcRngRtn.getFieldString("RTN")) + "ydEqpStat1:" + ydEqpStat, "SL");
			
			if (commUtils.trim(jrLocSrcRngRtn.getFieldString("RTN")).equals("-1")) {
				m_ctx.setRollbackOnly();
				throw new DAOException("TO 저장위치 등록  오류");
				
			} 

			
			if("2APT02UM".equals(ydSchCd)||"2DPT02UM".equals(ydSchCd)||"2EPT02UM".equals(ydSchCd)) {
				if(ydToLocGuide.length() == 6  && "PT".equals(ydToLocGuide.substring(2, 4))){
					/**********************************************************
					* 차량작업 예정정보 송신 (YMA8L008)
					**********************************************************/
					jrParam.setField("SEARCH_FLAG" 			, "4"				);	//1:상차도, 2:차량스케쥴 ID	
					jrParam.setField("PT_LOAD_LOC" 			, ydToLocGuide		); 	//상차도 위치
					jrRtn = commUtils.addSndData(jrRtn, YmComm.procCarPlanInfo_Slab(jrParam)); 
				}	
			}
						
			/**********************************************************
			* 5.크레인작업지시 호출
  			**********************************************************/
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnSchStatRe 
			SELECT A.YD_EQP_ID 
			     , (SELECT WPROG_STAT 
			          FROM TB_YM_EQUIP 
			         WHERE EQUIP_GP = A.YD_EQP_ID) AS YD_EQP_STAT
			  FROM TB_YM_CRNSCH A
			 WHERE A.YD_CRN_SCH_ID = ( SELECT MAX(YD_CRN_SCH_ID) AS YD_CRN_SCH_ID
			                           FROM TB_YM_CRNSCH 
			                          WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			                            AND DEL_YN  = 'N'
			                          )      
			   AND A.DEL_YN = 'N'   
			*/	   
			
			JDTORecordSet jsCrnSchStatRe = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnSchStatRe", logId, methodNm, "작업예약으로 크레인 스케쥴 조회");
			if (jsCrnSchStatRe.size() > 0) {
				ydEqpId   =  commUtils.trim(jsCrnSchStatRe.getRecord(0).getFieldString("YD_EQP_ID"));
				ydEqpStat =  commUtils.trim(jsCrnSchStatRe.getRecord(0).getFieldString("YD_EQP_STAT"));
			}  
			
			commUtils.printLog(logId, commUtils.trim(jrLocSrcRngRtn.getFieldString("RTN")) + "ydEqpStat2:" + ydEqpStat, "SL");
			if ("W".equals(ydEqpStat)) {
				commUtils.printLog(logId, commUtils.trim(jrLocSrcRngRtn.getFieldString("RTN")) + "ydEqpStat3:" + ydEqpStat, "SL");
				
				String sAPP018_YN = YmComm.BCoilApplyYn("APP018","2","1");   //slab_log여부
				commUtils.printLog(logId,  "스케쥴 명령선택 트렌젝션 적용:" + sAPP018_YN, "SL");	
				
				if(sAPP018_YN.equals("Y")) {

					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					jrYdMsg.setField("JMS_TC_CD"         , "YMYMJ001"               ); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태

					EJBConnector ejbConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
		    		jrRtn = (JDTORecord)ejbConn.trx("rcvA8YML007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
					
				} else {
					//야드설비상태가 대기이면 내부크레인작업지시요구 전송
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					jrYdMsg.setField("JMS_TC_CD"         , "YMYMJ001"               ); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				}
			} 
			
			commUtils.printLog(logId, methodNm + "[스케쥴메인종료]", "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
		String methodNm = "크레인스케줄 작업예약재료 수정[BSlabSchSeEJB.updCrnSchWB] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updWmStrLoc 
			--크레인스케줄 작업예약재료 저장위치 수정
			 MERGE INTO TB_YM_WRKBOOKMTL WM USING (
			 SELECT YD_WBOOK_ID
			       ,STOCK_ID
			       ,:V_MODIFIER AS MODIFIER
			       ,SYSDATE     AS MOD_DDTT
			       ,STACK_COL_GP
			       ,STACK_BED_GP
			       ,STACK_LAYER_GP
			       ,YD_UP_COLL_SEQ 
			   FROM (SELECT WM.*
			           FROM (SELECT WB.YD_WBOOK_ID
			                       ,WB.YD_SCH_CD
			                       ,WM.STOCK_ID
			                       ,SL.STACK_COL_GP
			                       ,SL.STACK_BED_GP
			                       ,SL.STACK_LAYER_GP
			                       ,SL.STACK_COL_GP||SL.STACK_BED_GP||SL.STACK_LAYER_GP AS YD_STR_LOC
			                       ,SL.STACK_COL_GP||SL.STACK_BED_GP AS YD_STK_COL_BED
			                       ,RANK() OVER(PARTITION BY SL.STACK_COL_GP,SL.STACK_BED_GP
			                                        ORDER BY SL.STACK_COL_GP,SL.STACK_BED_GP,SL.STACK_LAYER_GP) AS YD_UP_COLL_SEQ
			                       
			                   FROM TB_YM_WRKBOOK    WB
			                       ,TB_YM_WRKBOOKMTL WM
			                       ,TB_YM_STACKLAYER SL
			                       ,TB_YM_STOCK      ST
			                  WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
			                    AND WM.STOCK_ID    = SL.STOCK_ID
			                    AND WM.STOCK_ID    = ST.STOCK_ID
			                    AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
			                    AND WB.DEL_YN      = 'N'
			                    AND WM.DEL_YN      = 'N'
			                    AND SL.STACK_LAYER_STAT = 'C'
			                    AND WB.YD_GP=SUBSTR(SL.STACK_COL_GP,1,1)
			                  ORDER BY YD_STR_LOC DESC) WM
			          ORDER BY YD_STR_LOC DESC)
			 ) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.STOCK_ID = DD.STOCK_ID)
			 WHEN MATCHED THEN UPDATE SET
			      WM.MODIFIER       = DD.MODIFIER
			     ,WM.MOD_DDTT       = DD.MOD_DDTT
			     ,WM.STACK_COL_GP   = DD.STACK_COL_GP
			     ,WM.STACK_BED_GP   = DD.STACK_BED_GP
			     ,WM.STACK_LAYER_GP = DD.STACK_LAYER_GP
			     ,WM.YD_UP_COLL_SEQ = DD.YD_UP_COLL_SEQ
			    */   
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updWmStrLoc", logId, methodNm, "작업예약재료 저장위치 수정");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 오퍼레이션명 : B열연 SLAB야드 크레인 스케줄 GROUPING PARAMETER DATA SETTING
	 *  
	 * @param  String     szEqpId, szSchCd, rsMinWrkBookMtl, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public int CrnSchGrp( String logId, String methodNms, JDTORecord jrParamSet, JDTORecordSet jsReturn )throws JDTOException  {
    	String 	methodNm = "그룹핑 파라미터 셋팅 [BSlabSchSeEJB.CrnSchGrp] < " + methodNms;
    	
    	JDTORecordSet jsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
		//레코드셋 정렬 시
		String 	szLogMsg	= "";
		String 	szDBLogMsg	= "";
		String 	szParmLogMsg	= "";

		try {
			
			commUtils.printLog(logId, methodNm, "S+");			

			String ydSchCd 		= commUtils.trim(jrParamSet.getFieldString("YD_SCH_CD"));
			String ydWbookId 	= commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_ID"));
			String sAPP005_YN 	= commUtils.trim(jrParamSet.getFieldString("LOG_YN"));

			JDTORecord jrCrEquip = JDTORecordFactory.getInstance().create();		
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStatEqp 
			--설비상태조회 
			SELECT WPROG_STAT     AS YD_EQP_STAT
			     , WORK_MODE      AS YD_EQP_WRK_MODE
				 , STACK_MAX_QNTY	                  --적재 최대 수량
				 , STACK_MAX_WT		                  --적재 최대 중량
			     , CURR_STOP_LOC
			  FROM TB_YM_EQUIP EQ
			 WHERE EQUIP_GP = :V_YD_EQP_ID
			   AND DEL_YN    = 'N' 
			*/	   
			JDTORecordSet jsCrEquip = commDao.select(jrParamSet, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStatEqp", logId, methodNm, "설비상태조회");
			
			if (jsCrEquip.size() > 0) {
				jrCrEquip = jsCrEquip.getRecord(0);
			}			
			
			JDTORecord jrHandlingLog = JDTORecordFactory.getInstance().create();	
			
			
			if("YD".equals(ydSchCd.substring(2, 4))
					||(("TC".equals(ydSchCd.substring(2, 4))) && (("U".equals(ydSchCd.substring(6, 7))) || ("L".equals(ydSchCd.substring(6, 7)))))
					){
				//------------------------------------------------------------------------------------------------------------
				//동내이적은 슬라브 단위 작업예약 편성 
				//   -- 하단 작업예약과 그룹핑 가능 여부 Check   
				// 대차 상차 
				//   -- 하단 작업예약과 그룹핑 가능 여부 Check   
				//------------------------------------------------------------------------------------------------------------			
				/* com.inisteel.cim.ym.bslab.dao.BSalbDAO.getWorkBookBedYd 
				SELECT A.STACK_COL_GP 
				     , A.STACK_BED_GP 
				     , MAX(STACK_LAYER_GP)      AS MAX_STACK_LAYER_GP
				     , A.YD_WBOOK_ID            AS MAX_YD_WBOOK_ID
				     , A.YD_SCH_CD
				     , MIN(STACK_LAYER_GP)      AS MIN_STACK_LAYER_GP 
				     , CASE WHEN MAX(A.STOCK_ID_CNT) > 1 THEN NULL
				            ELSE MAX((SELECT WB.YD_WBOOK_ID
				                        FROM TB_YM_WRKBOOK    WB
				                           , TB_YM_WRKBOOKMTL WM
				                           , TB_YM_STACKLAYER SL
				                       WHERE WB.YD_WBOOK_ID    = WM.YD_WBOOK_ID
				                         AND WM.STOCK_ID       = SL.STOCK_ID
				                         AND WM.STOCK_ID       = A.STOCK_ID
				                         AND SL.STACK_COL_GP   = A.STACK_COL_GP
				                         AND SL.STACK_BED_GP   = A.STACK_BED_GP
				                         AND SL.STACK_LAYER_GP = A.STACK_LAYER_GP
				                         AND WB.YD_SCH_CD      = A.YD_SCH_CD
				                         AND NVL(WB.YD_AIM_BAY_GP,1)  = NVL(A.YD_AIM_BAY_GP,1)
				                         AND WB.DEL_YN = 'N'
				                         AND WM.DEL_YN = 'N'
				                         AND WB.YD_WBOOK_ID <> A.YD_WBOOK_ID 
				                         AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
								                                      FROM TB_YM_CRNSCH
								                                      WHERE DEL_YN = 'N')
				                         AND ROWNUM = 1))
				            END  AS MIN_YD_WBOOK_ID
				     , A.YD_WRK_PLAN_TCAR       
				  FROM (
				        SELECT A1.STACK_COL_GP 
				             , A1.STACK_BED_GP 
				             , A1.STACK_LAYER_GP
				             , A1.STOCK_ID
				             , B1.YD_WBOOK_ID 
				             , B1.YD_AIM_BAY_GP
				             , B1.YD_SCH_CD
				             , B1.STOCK_ID_CNT
				             , B1.YD_WRK_PLAN_TCAR
				         FROM TB_YM_STACKLAYER A1
				             , (
				                SELECT WB.YD_WBOOK_ID
				                     , WB.YD_SCH_CD
				                     , WB.YD_AIM_BAY_GP
				                     , WB.YD_WRK_PLAN_TCAR
				                     , SL.STACK_COL_GP 
				                     , SL.STACK_BED_GP 
				                     , SL.STACK_LAYER_GP
				                     , DECODE(TO_NUMBER(SL.STACK_LAYER_GP) -1,0,'01',LPAD(TO_NUMBER(SL.STACK_LAYER_GP) -1,2,'0')) AS DOWN_STACK_LAYER_GP
				                     , COUNT(*) OVER() AS STOCK_ID_CNT
				                  FROM TB_YM_WRKBOOK    WB
				                     , TB_YM_WRKBOOKMTL WM
				                     , TB_YM_STACKLAYER SL
				                 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                   AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				                   AND WM.STOCK_ID    = SL.STOCK_ID
				                   AND SL.STACK_LAYER_STAT = 'C'
				                   AND WB.DEL_YN = 'N'
				                   AND WM.DEL_YN = 'N'
				               ) B1
				         WHERE A1.STACK_COL_GP = B1.STACK_COL_GP
				           AND A1.STACK_BED_GP = B1.STACK_BED_GP
				           AND A1.STACK_LAYER_GP IN (B1.STACK_LAYER_GP, B1.DOWN_STACK_LAYER_GP)
				       ) A      
				 GROUP BY A.YD_WBOOK_ID, A.YD_SCH_CD, A.STACK_COL_GP, A.STACK_BED_GP,A.YD_WRK_PLAN_TCAR
				*/	   
				//JDTORecordSet jsWbBedYd = commDao.select(jrParamSet, "com.inisteel.cim.ym.bslab.dao.BSalbDAO.getWorkBookBedYd", logId, methodNm, "작업예약 BED 정보");
				
				JDTORecordSet jsWbBedYd;
				
				if(("TC".equals(ydSchCd.substring(2, 4)) &&  ("L".equals(ydSchCd.substring(6, 7)))) || "2BTC11UM".equals(ydSchCd)) { 
					//대차하차  + B동대차상차
					jsWbBedYd = commDao.select(jrParamSet, "com.inisteel.cim.ym.bslab.dao.BSalbDAO.getWorkBookBedYdTCL", logId, methodNm, "대차하차 - 작업예약 BED 정보");
				} else {
					jsWbBedYd = commDao.select(jrParamSet, "com.inisteel.cim.ym.bslab.dao.BSalbDAO.getWorkBookBedYd", logId, methodNm, "작업예약 BED 정보");
				}
				
				commUtils.printParam(logId, jsWbBedYd);
				if(jsWbBedYd.size() == 1) {
					String maxYdWbookId = commUtils.trim(jsWbBedYd.getRecord(0).getFieldString("MAX_YD_WBOOK_ID"));
					String minYdWbookId = commUtils.trim(jsWbBedYd.getRecord(0).getFieldString("MIN_YD_WBOOK_ID"));
					//하단 작업예약이 있으면 합체 가능 여부 CHECK
					if(!"".equals(minYdWbookId)) {
						boolean AbleYn = false;
						if("YD".equals(ydSchCd.substring(2, 4)) || ("TC".equals(ydSchCd.substring(2, 4))) && ( ("L".equals(ydSchCd.substring(6, 7))))){
							AbleYn = this.CrnSchGrpHandlingAbleChk   (logId, methodNms, ydSchCd, jsWbBedYd ,jrCrEquip) ;
						} else  {
							AbleYn = this.CrnSchGrpHandlingAbleChkTC (logId, methodNms, ydSchCd, jsWbBedYd ,jrCrEquip) ;
						}
						
						if(AbleYn) {
							
							JDTORecord jrParam = JDTORecordFactory.getInstance().create();
							jrParam.setResultCode(logId);	//Log ID
							jrParam.setResultMsg(methodNm);	//Log Method Name
							jrParam.setField("MODIFIER"       , "SCH_DEL"); 
							jrParam.setField("MAX_YD_WBOOK_ID", maxYdWbookId); 
							jrParam.setField("MIN_YD_WBOOK_ID", minYdWbookId); 
							jrParam.setField("YD_WBOOK_ID"    , minYdWbookId); 
							// 작업 예약 합체
							/* com.inisteel.cim.ym.bslab.dao.BSalbDAO.insWrkBoolMtlSum 
							INSERT INTO TB_YM_WRKBOOKMTL (
							       YD_WBOOK_ID
							     , STOCK_ID
							     , REGISTER
							     , REG_DDTT
							     , MODIFIER
							     , MOD_DDTT
							     , DEL_YN
							     , STACK_COL_GP
							     , STACK_BED_GP
							     , STACK_LAYER_GP
							     , YD_UP_COLL_SEQ
							)
							SELECT :V_MAX_YD_WBOOK_ID
							     , A.STOCK_ID
							     , 'SCH'
							     , SYSDATE
							     , 'SCH'
							     , SYSDATE
							     , 'N'
							     , A.STACK_COL_GP
							     , A.STACK_BED_GP
							     , A.STACK_LAYER_GP
							     , A.YD_UP_COLL_SEQ
							  FROM TB_YM_WRKBOOKMTL A
							 WHERE YD_WBOOK_ID = :V_MIN_YD_WBOOK_ID
							   AND ROWNUM = 1
					    	 */  
							commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSalbDAO.insWrkBoolMtlSum", logId, methodNm, "TB_YM_WRKBOOKMTL 갱신");
							
							//작업예약재료 삭제
							/*
							UPDATE TB_YM_WRKBOOKMTL
							   SET MODIFIER    = :V_MODIFIER
							      ,MOD_DDTT    = SYSDATE
							      ,DEL_YN      = 'Y'
							 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
							   AND DEL_YN      = 'N'
							 */
							commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL");				
							
							//작업예약 삭제
							/*
							UPDATE TB_YM_WRKBOOK
							   SET MODIFIER    = :V_MODIFIER
							      ,MOD_DDTT    = SYSDATE
							      ,DEL_YN      = 'Y'
							 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
							   AND DEL_YN      = 'N'
							 */
							commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDelYnWrkBook", logId, methodNm, "TB_YM_WRKBOOK");	
						}
					}	
				}
			} 
			
			if(("PT".equals(ydSchCd.substring(2, 4))) && ("L".equals(ydSchCd.substring(6, 7)))){  //이송하차
				//------------------------------------------------------------------------------------------------------------
				//동내이적은 슬라브 단위 작업예약 편성 
				//   -- 하단 작업예약과 그룹핑 가능 여부 Check   
				//이송하차 
				//   -- 하단 작업예약과 그룹핑 가능 여부 Check   
				//------------------------------------------------------------------------------------------------------------			
				/* com.inisteel.cim.ym.bslab.dao.BSalbDAO.getWorkBookBedYd 
				SELECT A.STACK_COL_GP 
				     , A.STACK_BED_GP 
				     , MAX(STACK_LAYER_GP)      AS MAX_STACK_LAYER_GP
				     , A.YD_WBOOK_ID            AS MAX_YD_WBOOK_ID
				     , A.YD_SCH_CD
				     , MIN(STACK_LAYER_GP)      AS MIN_STACK_LAYER_GP 
				     , CASE WHEN MAX(A.STOCK_ID_CNT) > 1 THEN NULL
				            ELSE MAX((SELECT WB.YD_WBOOK_ID
				                        FROM TB_YM_WRKBOOK    WB
				                           , TB_YM_WRKBOOKMTL WM
				                           , TB_YM_STACKLAYER SL
				                       WHERE WB.YD_WBOOK_ID    = WM.YD_WBOOK_ID
				                         AND WM.STOCK_ID       = SL.STOCK_ID
				                         AND WM.STOCK_ID       = A.STOCK_ID
				                         AND SL.STACK_COL_GP   = A.STACK_COL_GP
				                         AND SL.STACK_BED_GP   = A.STACK_BED_GP
				                         AND SL.STACK_LAYER_GP = A.STACK_LAYER_GP
				                         AND WB.YD_SCH_CD      = A.YD_SCH_CD
				                         AND WB.YD_AIM_BAY_GP  = A.YD_AIM_BAY_GP
				                         AND WB.DEL_YN = 'N'
				                         AND WM.DEL_YN = 'N'
				                         AND WB.YD_WBOOK_ID <> A.YD_WBOOK_ID 
				                         AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
								                                      FROM TB_YM_CRNSCH
								                                      WHERE DEL_YN = 'N')
				                         AND ROWNUM = 1))
				            END  AS MIN_YD_WBOOK_ID
				     , A.YD_WRK_PLAN_TCAR       
				  FROM (
				        SELECT A1.STACK_COL_GP 
				             , A1.STACK_BED_GP 
				             , A1.STACK_LAYER_GP
				             , A1.STOCK_ID
				             , B1.YD_WBOOK_ID 
				             , B1.YD_AIM_BAY_GP
				             , B1.YD_SCH_CD
				             , B1.STOCK_ID_CNT
				             , B1.YD_WRK_PLAN_TCAR
				         FROM TB_YM_STACKLAYER A1
				             , (
				                SELECT WB.YD_WBOOK_ID
				                     , WB.YD_SCH_CD
				                     , WB.YD_AIM_BAY_GP
				                     , WB.YD_WRK_PLAN_TCAR
				                     , SL.STACK_COL_GP 
				                     , SL.STACK_BED_GP 
				                     , SL.STACK_LAYER_GP
				                     , DECODE(TO_NUMBER(SL.STACK_LAYER_GP) -1,0,'01',LPAD(TO_NUMBER(SL.STACK_LAYER_GP) -1,2,'0')) AS DOWN_STACK_LAYER_GP
				                     , COUNT(*) OVER() AS STOCK_ID_CNT
				                  FROM TB_YM_WRKBOOK    WB
				                     , TB_YM_WRKBOOKMTL WM
				                     , TB_YM_STACKLAYER SL
				                 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                   AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				                   AND WM.STOCK_ID    = SL.STOCK_ID
				                   AND SL.STACK_LAYER_STAT = 'C'
				                   AND WB.DEL_YN = 'N'
				                   AND WM.DEL_YN = 'N'
				               ) B1
				         WHERE A1.STACK_COL_GP = B1.STACK_COL_GP
				           AND A1.STACK_BED_GP = B1.STACK_BED_GP
				           AND A1.STACK_LAYER_GP IN (B1.STACK_LAYER_GP, B1.DOWN_STACK_LAYER_GP)
				       ) A      
				 GROUP BY A.YD_WBOOK_ID, A.YD_SCH_CD, A.STACK_COL_GP, A.STACK_BED_GP,A.YD_WRK_PLAN_TCAR
				*/	   
				//JDTORecordSet jsWbBedYd = commDao.select(jrParamSet, "com.inisteel.cim.ym.bslab.dao.BSalbDAO.getWorkBookBedYd", logId, methodNm, "작업예약 BED 정보");
				JDTORecordSet jsWbBedYd = commDao.select(jrParamSet, "com.inisteel.cim.ym.bslab.dao.BSalbDAO.getWorkBookBedYdTCL", logId, methodNm, "작업예약 BED 정보");
				
				commUtils.printParam(logId, jsWbBedYd);
				if(jsWbBedYd.size() == 1) {
					String maxYdWbookId = commUtils.trim(jsWbBedYd.getRecord(0).getFieldString("MAX_YD_WBOOK_ID"));
					String minYdWbookId = commUtils.trim(jsWbBedYd.getRecord(0).getFieldString("MIN_YD_WBOOK_ID"));
					String sStackColGp  = commUtils.trim(jsWbBedYd.getRecord(0).getFieldString("STACK_COL_GP"));
					
					//하단 작업예약이 있으면 합체 가능 여부 CHECK
					if(!"".equals(minYdWbookId)) {
						boolean AbleYn = false;
						
						
						/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCarMtl 
						SELECT A.STACK_COL_GP 
						     , A.STACK_BED_GP 
						     , A.YD_SCH_CD
						     , A.MIN_STACK_LAYER_GP 
						     -- 이송하차를 대차로 하는지 CHECK 
						     , (SELECT DECODE(COUNT(*),0,'N','Y')
						          FROM TB_YM_EQUIP B
						             , TB_YM_TCARSCH C
						         WHERE B.EQUIP_GP = C.YD_EQP_ID
						           AND B.EQUIP_GP LIKE '2XTC0%'
						           AND SUBSTR(C.YD_CARLD_STOP_LOC,2,1) = SUBSTR(A.YD_SCH_CD,2,1)
						           AND SUBSTR(B.CARLD_SCH_CD,3,6)      = SUBSTR(A.YD_SCH_CD,3,6)
						           AND C.DEL_YN = 'N' 
						       ) AS TC_DOWN_YN   
						     , (SELECT EQUIP_GP
						          FROM TB_YM_EQUIP B
						             , TB_YM_TCARSCH C
						         WHERE B.EQUIP_GP = C.YD_EQP_ID
						           AND B.EQUIP_GP LIKE '2XTC0%'
						           AND SUBSTR(C.YD_CARLD_STOP_LOC,2,1) = SUBSTR(A.YD_SCH_CD,2,1)
						           AND SUBSTR(B.CARLD_SCH_CD,3,6)      = SUBSTR(A.YD_SCH_CD,3,6)
						           AND C.DEL_YN = 'N' 
						       ) AS EQUIP_GP   
						  FROM (
						        SELECT STACK_COL_GP 
						             , STACK_BED_GP 
						             , :V_YD_SCH_CD  AS YD_SCH_CD
						             , '01' AS MIN_STACK_LAYER_GP
						          FROM TB_YM_STACKLAYER
						         WHERE STACK_COL_GP = :V_STACK_COL_GP
						           AND STACK_BED_GP = '01'
						           AND STOCK_ID IS NOT NULL
						           AND STACK_LAYER_STAT = 'C'
						          GROUP BY STACK_COL_GP, STACK_BED_GP
							   ) A
						*/	   

						jrParamSet.setField("STACK_COL_GP", sStackColGp); 
						JDTORecordSet jsWbBedChk = commDao.select(jrParamSet, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCarMtl", logId, methodNm, "이송하차를 대차로 하는지 CHECK");
						commUtils.printParam(logId, jsWbBedChk);
						
						if(jsWbBedChk.size() > 0) {
							
							String sTcDownYn = commUtils.trim(jsWbBedChk.getRecord(0).getFieldString("TC_DOWN_YN"));
							// 이송하차를 대차로 함
							if("Y".equals(sTcDownYn)) {
								AbleYn = this.CrnSchGrpHandlingAbleChkTC   (logId, methodNms, ydSchCd, jsWbBedYd ,jrCrEquip) ;
							} else {
								AbleYn = this.CrnSchGrpHandlingAbleChkPT   (logId, methodNms, ydSchCd, jsWbBedYd ,jrCrEquip) ;
							}
							if(AbleYn) {
								
								JDTORecord jrParam = JDTORecordFactory.getInstance().create();
								jrParam.setResultCode(logId);	//Log ID
								jrParam.setResultMsg(methodNm);	//Log Method Name
								jrParam.setField("MODIFIER"       , "SCH_DEL"); 
								jrParam.setField("MAX_YD_WBOOK_ID", maxYdWbookId); 
								jrParam.setField("MIN_YD_WBOOK_ID", minYdWbookId); 
								jrParam.setField("YD_WBOOK_ID"    , minYdWbookId); 
								// 작업 예약 합체
								/* com.inisteel.cim.ym.bslab.dao.BSalbDAO.insWrkBoolMtlSum 
								INSERT INTO TB_YM_WRKBOOKMTL (
								       YD_WBOOK_ID
								     , STOCK_ID
								     , REGISTER
								     , REG_DDTT
								     , MODIFIER
								     , MOD_DDTT
								     , DEL_YN
								     , STACK_COL_GP
								     , STACK_BED_GP
								     , STACK_LAYER_GP
								     , YD_UP_COLL_SEQ
								)
								SELECT :V_MAX_YD_WBOOK_ID
								     , A.STOCK_ID
								     , 'SCH'
								     , SYSDATE
								     , 'SCH'
								     , SYSDATE
								     , 'N'
								     , A.STACK_COL_GP
								     , A.STACK_BED_GP
								     , A.STACK_LAYER_GP
								     , A.YD_UP_COLL_SEQ
								  FROM TB_YM_WRKBOOKMTL A
								 WHERE YD_WBOOK_ID = :V_MIN_YD_WBOOK_ID
								   AND ROWNUM = 1
						    	 */  
								commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSalbDAO.insWrkBoolMtlSum", logId, methodNm, "TB_YM_WRKBOOKMTL 갱신");
								
								//작업예약재료 삭제
								/*
								UPDATE TB_YM_WRKBOOKMTL
								   SET MODIFIER    = :V_MODIFIER
								      ,MOD_DDTT    = SYSDATE
								      ,DEL_YN      = 'Y'
								 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
								   AND DEL_YN      = 'N'
								 */
								commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL");				
								
								//작업예약 삭제
								/*
								UPDATE TB_YM_WRKBOOK
								   SET MODIFIER    = :V_MODIFIER
								      ,MOD_DDTT    = SYSDATE
								      ,DEL_YN      = 'Y'
								 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
								   AND DEL_YN      = 'N'
								 */
								commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDelYnWrkBook", logId, methodNm, "TB_YM_WRKBOOK");	
							}
						}	
					}
				}
			} 
			//------------------------------------------------------------------------------------------------------------
			// 작업 예약을 BED별로 분리   
			// 추후  BED우선순위 변경시 용의
			//------------------------------------------------------------------------------------------------------------			
			/* com.inisteel.cim.ym.bslab.dao.BSalbDAO.getWorkBookBed 
			SELECT A.YD_WBOOK_ID 
			     , B.STACK_COL_GP 
			     , B.STACK_BED_GP 
			     , A.YD_SCH_CD AS YD_SCH_CD
			     , MIN(B.STACK_LAYER_GP) AS MIN_STACK_LAYER_GP 
			     , MAX(B.YD_UP_COLL_SEQ) AS YD_UP_COLL_SEQ
			      -- 이송하차를 대차로 하는지 CHECK 
			     , (SELECT DECODE(COUNT(*),0,'N','Y')
			          FROM TB_YM_EQUIP B1
			             , TB_YM_TCARSCH C1
			         WHERE B1.EQUIP_GP = C1.YD_EQP_ID
			           AND B1.EQUIP_GP LIKE '2XTC0%'
			           AND SUBSTR(C1.YD_CARLD_STOP_LOC,2,1) = SUBSTR(A.YD_SCH_CD,2,1)
			           AND SUBSTR(B1.CARLD_SCH_CD,3,6)      = SUBSTR(A.YD_SCH_CD,3,6)
			           AND C1.DEL_YN = 'N' 
			           AND SUBSTR(A.YD_SCH_CD,3,6) = 'PT02LM'
			       ) AS TC_DOWN_YN 
			  FROM TB_YM_WRKBOOK   A
			     , TB_YM_WRKBOOKMTL B                          
			 WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
			   AND A.YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND A.DEL_YN = 'N'    
			   AND B.DEL_YN = 'N'    
			 GROUP BY A.YD_WBOOK_ID,B.STACK_COL_GP, B.STACK_BED_GP, A.YD_SCH_CD
			 ORDER BY YD_UP_COLL_SEQ
			*/	   
			JDTORecordSet jsWbBed = commDao.select(jrParamSet, "com.inisteel.cim.ym.bslab.dao.BSalbDAO.getWorkBookBed", logId, methodNm, "작업예약 BED우선순 정보");
			commUtils.printParam(logId, jsWbBed);
			if(jsWbBed.size() > 0) {
				
				String sTcDownYn = commUtils.trim(jsWbBed.getRecord(0).getFieldString("TC_DOWN_YN"));
				String sAPP021_YN = YmComm.BCoilApplyYn("APP021","2","1");   //slab_log여부
				commUtils.printLog(logId,  "이송하차신규 적용:" + sAPP021_YN, "SL");

				if(sAPP021_YN.equals("Y")) {
					// 이송하차를 대차로 하는경우 대차 로직 으로
					if("Y".equals(sTcDownYn)) {
						jsHandling = this.CrnSchGrpHandling   ( logId, methodNms, "3", ydSchCd, sAPP005_YN, ydWbookId, jsWbBed ,jrCrEquip);
						
					} else if(("PT".equals(ydSchCd.substring(2, 4))) && ("L".equals(ydSchCd.substring(6, 7)))){  //이송하차
						jsHandling = this.CrnSchGrpHandlingPT ( logId, methodNms, "1", ydSchCd, sAPP005_YN, ydWbookId, jsWbBed ,jrCrEquip);
						
					} else {	
						jsHandling = this.CrnSchGrpHandling   ( logId, methodNms, "1", ydSchCd, sAPP005_YN, ydWbookId, jsWbBed ,jrCrEquip);
					}
				} else {
					// 이송하차를 대차로 하는경우 대차 로직 으로
					if("Y".equals(sTcDownYn)) {
						jsHandling = this.CrnSchGrpHandling   ( logId, methodNms, "3", ydSchCd, sAPP005_YN, ydWbookId, jsWbBed ,jrCrEquip);
					} else {
						jsHandling = this.CrnSchGrpHandling   ( logId, methodNms, "1", ydSchCd, sAPP005_YN, ydWbookId, jsWbBed ,jrCrEquip);
					}
				}
			}	
			commUtils.printLog(logId, "HANDLING_수:" +jsHandling.size() , "SL");
			
			
			JDTORecord jrReturn  = JDTORecordFactory.getInstance().create();
			
			for(int Loop_i = 1; Loop_i <= jsHandling.size(); Loop_i++) {
			
				jsHandling.absolute(Loop_i);  
				jrReturn = jsHandling.getRecord();
				jsReturn.addRecord(jrReturn);
			}

			szDBLogMsg = commUtils.trim(jrHandlingLog.getFieldString("HANDLING _LOG")); 
			
			commUtils.printLog(logId, methodNm, "S-");			

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return YmConstant.RETN_INT_SUCCESS;

	} //end of 
	
    /**
     * 오퍼레이션명 :  Handling Data 크레인사양Check
     *  
     * @param  ● szEqpId, vecHandledData, vecResult
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public JDTORecord chkHandledDataCrnSpec (String logId, String methodNms, String ydSchCd, JDTORecord jrChkCr , JDTORecordSet jsSpacChk, String szParmLogMsg) throws JDTOException {
    	String methodNm = "Handling Data 크레인사양Check [BSlabSchSeEJB.chkHandledDataCrnSpec] < " +methodNms;
    	
    	JDTORecord jrSpacChk	= JDTORecordFactory.getInstance().create(); 	
    	JDTORecord jrRtnLog 	= JDTORecordFactory.getInstance().create();
    	
		float fmtlT     	= 0;		//재료두께
		float fmtlW     	= 0;		//재료폭
		int   imtlWtSum 	= 0;		//재료중량합
		float fmtlTSum  	= 0;		//재료두께합
		float fmtlWMax  	= 0;		//재료폭최대
		int   imtlWt    	= 0;		//재료중량
		float topfmtlW     	= 0;		//상단재료폭
		float botfmtlW     	= 0;		//하단재료폭
		int   iWidDif    	= 0;		//재료폭 gap
		
		String topSTOCK_ID  = "";
		String botSTOCK_ID  = "";
		String topSTACK_LAYER_GP  = "";
		String botSTACK_LAYER_GP  = "";

		commUtils.printLog(logId, methodNm, "S+");	
		try{	
			
			int imaxStackQty = Integer.parseInt(commUtils.nvl(jrChkCr.getFieldString("STACK_MAX_QNTY" ),"0")); 
			int imaxStackWt  = Integer.parseInt(commUtils.nvl(jrChkCr.getFieldString("STACK_MAX_WT" ),"0")); 
			int imaxStackW	 = Integer.parseInt(commUtils.nvl(jrChkCr.getFieldString("YM102_MAX_W" ),"2000"));  //1750 2매작업 폭 기준 (TB_YM_RUEL , YM102 ,MAX_W)

			commUtils.printLog(logId, "imaxStackQty [" + imaxStackQty + "] imaxStackWt[" + imaxStackWt +"] YM102_MAX_W[" + imaxStackW + "] ", "SL");

			if (jsSpacChk.size() > imaxStackQty)	 {  // 매수 CHECK
				jsSpacChk.last();
				jrSpacChk.setRecord(jsSpacChk.getRecord());
				topSTOCK_ID 		= commUtils.trim(jrSpacChk.getFieldString("STOCK_ID"));
				topSTACK_LAYER_GP	= commUtils.trim(jrSpacChk.getFieldString("STACK_LAYER_GP"));
				jrRtnLog.setField("HANDLE_RTN"  , "-1");
    			jrRtnLog.setField("HANDLE_CONTENTS"	, "단[" +topSTACK_LAYER_GP + "] "+ "재료번호[" +topSTOCK_ID + "] 권상 가능 매수로 인한 분리");
				return jrRtnLog;
			} 
			
			commUtils.printParam(logId, jsSpacChk);
			for(int Loop_i = 1; Loop_i <= jsSpacChk.size(); Loop_i++) {
				jsSpacChk.absolute(Loop_i);
				jrSpacChk = jsSpacChk.getRecord();
				fmtlT 	  = Float.parseFloat(commUtils.nvl(jrSpacChk.getFieldString("SLAB_T" ),"0")); 
				fmtlW 	  = Float.parseFloat(commUtils.nvl(jrSpacChk.getFieldString("SLAB_W" ),"0")); 
				imtlWt 	  = Integer.parseInt(commUtils.nvl(jrSpacChk.getFieldString("SLAB_WT" ),"0")); 
				iWidDif   = Integer.parseInt(commUtils.nvl(jrSpacChk.getFieldString("WID_DIF" ),"0")); 
				fmtlTSum  = fmtlTSum + fmtlT;
				imtlWtSum = imtlWtSum + imtlWt;
				if(Loop_i == 1) {
					topfmtlW = fmtlW;
					
					topSTOCK_ID 		= commUtils.trim(jrSpacChk.getFieldString("STOCK_ID"));
					topSTACK_LAYER_GP	= commUtils.trim(jrSpacChk.getFieldString("STACK_LAYER_GP"));
				} else {
					botfmtlW = fmtlW;
					
					botSTOCK_ID 		= commUtils.trim(jrSpacChk.getFieldString("STOCK_ID"));
					botSTACK_LAYER_GP	= commUtils.trim(jrSpacChk.getFieldString("STACK_LAYER_GP"));
				}
			}

			commUtils.printLog(logId, "상단 [" + topfmtlW + "] 하단[" + botfmtlW +"] 폭차이기준[" + iWidDif + "]", "SL");	
			
			if((topfmtlW - botfmtlW  > iWidDif ) && (botfmtlW != 0)) {
				commUtils.printLog(logId, "상단폭 [" + topfmtlW + "]이 하단[" + botfmtlW +"]폭 차이 ["+ iWidDif + "]보다 크다", "SL");
				
				szParmLogMsg = "단[" +topSTACK_LAYER_GP + "] "+ "상단재료번호[" +topSTOCK_ID + "] 상단폭 [" + topfmtlW + "]이 " 
						     + "단[" +botSTACK_LAYER_GP + "] "+ "하단재료번호[" +botSTOCK_ID + "] 하단폭 [" + botfmtlW + "]  폭 차이 ["+ iWidDif + "]보다 크거나 같음";
				
				jrRtnLog.setField("HANDLE_RTN"  , "-1");
    			jrRtnLog.setField("HANDLE_CONTENTS"	, szParmLogMsg);
				return jrRtnLog;

			}	
			
			if(topfmtlW >= imaxStackW || botfmtlW >= imaxStackW){
				commUtils.printLog(logId, "폭이 " + imaxStackW + " mm 이상 이면 1매작업", "SL");	
				szParmLogMsg =  "단[" +topSTACK_LAYER_GP + "] "+ "상단재료번호[" +topSTOCK_ID + "] 상단폭 [" + topfmtlW + "]이거나 "
				             + "단[" +botSTACK_LAYER_GP + "] "+ "하단재료번호[" +botSTOCK_ID + "] 하단폭 [" + botfmtlW + "] 폭이 " + imaxStackW + " mm 이상 이면 1매작업";

				jrRtnLog.setField("HANDLE_RTN"  , "-1");
    			jrRtnLog.setField("HANDLE_CONTENTS"	, szParmLogMsg);
				return jrRtnLog;
			}	
			
			if (imtlWtSum > imaxStackWt)	 {  // 중량 CHECK : 53000
				commUtils.printLog(logId, "중량 CHECK : "+ imaxStackWt+ " 보다 크다.", "SL");	

				szParmLogMsg = "단[" +topSTACK_LAYER_GP + "] "+ "상단재료번호[" +topSTOCK_ID + "]  하단재료번호[" +botSTOCK_ID + "] 합중량 [" + imtlWtSum +"]이"
				             + "단[" +botSTACK_LAYER_GP + "] "+ "중량 CHECK : "+ imaxStackWt+ "크다";
				jrRtnLog.setField("HANDLE_RTN"  , "-1");
    			jrRtnLog.setField("HANDLE_CONTENTS"	, szParmLogMsg);
				return jrRtnLog;
			} 
			
			commUtils.printLog(logId, methodNm, "S-");			

			jrRtnLog.setField("HANDLE_RTN"  , "1");
			return jrRtnLog;

			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    }//end of chkHandledDataCrnSpec()	
    	
	/**
     * 오퍼레이션명 : 스케줄링 크레인 스케줄 등록
     *  
     * @param  ● vResult, msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int CrnSchIns(String logId, String methodNms , JDTORecord jrParamSet , JDTORecordSet jsRecset) throws JDTOException {
   		String methodNm = "스케줄링 크레인 스케줄 등록[BSlabSchSeEJB.CrnSchIns] < " + methodNms;
		JDTORecord recInCrn    = null;
		int intRtnVal = 0;
		String szName = "SYSTEM";
		String szLogMsg = "";
		
		try{
			
			commUtils.printLog(logId, methodNm, "S+");
			String szEqpId			= commUtils.trim(jrParamSet.getFieldString("YD_EQP_ID"  ));
			String szSchCd  		= commUtils.trim(jrParamSet.getFieldString("YD_SCH_CD"  ));
			String szydWbookId      = commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_ID"  ));

			String szYD_SCH_PRIOR  	= commUtils.trim(jrParamSet.getFieldString("YD_SCH_PRIOR"  ));
			String szYD_WBOOK_DT  	= commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_DT"  ));
			String modifier			= commUtils.trim(jrParamSet.getFieldString("MODIFIER")); //수정자(Backup Only)
			
			String szYD_TO_LOC_GUIDE_FNL = commUtils.trim(jrParamSet.getFieldString("YD_TO_LOC_GUIDE_FNL")); //E100 서포팅블록 이상시 To위치 가이드
			String sAPP100_E100_YN = "N";
			
			if(!"".equals(szYD_TO_LOC_GUIDE_FNL)) {			
				sAPP100_E100_YN = YmComm.BCoilApplyYn("APP100","2","E100_YN");
			}
			
			JDTORecordSet jsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			commUtils.printParam(logId, jsRecset);
			 
			//크레인 스케줄에 Insert한다.				
			JDTORecord recInCrnMtl 	= JDTORecordFactory.getInstance().create();
			String sCrHandlingLot  	= "0";
			String ydCrnSchId 		= "0";
			String ydToLocGuide     = "";

			for(int i = 1; i <= jsRecset.size(); i++) {

				jsRecset.absolute(i);
				recInCrn = JDTORecordFactory.getInstance().create();
				recInCrn  = jsRecset.getRecord();
				recInCrn.setResultCode(logId);	//Log ID
				recInCrn.setResultMsg(methodNm);	//Log Method Name

				//=============================================================================================================================================
				String sYM2011_CRN_SCH_TX_YN = YmComm.BCoilApplyYn("YM2011","2","CRN_SCH_TX");
				commUtils.printLog(logId,  "==========[[[ YM2011 크레인 스케줄 등록 트랜잭션 분리 :" + sYM2011_CRN_SCH_TX_YN + " ]]]============", "SL");
				
				
				recInCrn.setField("YD_WBOOK_ID"			, szydWbookId);
				recInCrn.setField("STOCK_ID"			, commUtils.trim(recInCrn.getFieldString("STOCK_ID"  )));
				//작업예약ID 조회-----------------------------------------------------------------------------------------
				if (!"".equals(szydWbookId)) {
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbookChk
					SELECT YD_WBOOK_ID 
					  FROM TB_YM_WRKBOOK A
					 WHERE A.YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND A.DEL_YN      = 'N'
					   AND NOT EXISTS (SELECT 1 FROM TB_YM_CRNSCH B 
					                                , USRYMA.TB_YM_CRNWRKMTL C
					                           WHERE B.YD_CRN_SCH_ID = C.YD_CRN_SCH_ID
					                            AND B.DEL_YN='N' 
					                            AND C.DEL_YN='N' 
					                            AND C.STOCK_ID=:V_STOCK_ID
					                            AND B.YD_WBOOK_ID=A.YD_WBOOK_ID)
					*/
					jsWbook = commDao.select(recInCrn, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbookChk", logId, methodNm, "작업예약 조회(스케쥴생선 전)");
					
				}
				
				if (jsWbook == null || jsWbook.size() <= 0) {
					 
					szLogMsg = "["+ methodNm +"]크레인 스케줄 등록중  이미 크레인 스케쥴 존재 함.Error!! ydWbookId: " + szydWbookId;
					commUtils.printLog(logId, szLogMsg, "SL");
					return YmConstant.RETN_INT_FAILURE;		 
				}
				//-------------------------------------------------------------------------------------------------------
				
				
				if(!sCrHandlingLot.equals(commUtils.trim(recInCrn.getFieldString("HANDLING_CNT_SPEC")))){
					/**********************************************************
					*  크레인 스케줄 등록
					**********************************************************/			
					//크레인스케줄ID를 할당받는다
					ydCrnSchId = commDao.getSeqId(logId, methodNm, "CrnSch");
	
					recInCrn.setField("MODIFIER",			modifier);
					recInCrn.setField("YD_CRN_SCH_ID",		ydCrnSchId);
					recInCrn.setField("YD_WBOOK_ID",      	szydWbookId);
					recInCrn.setField("YD_EQP_ID",        	szEqpId);
					recInCrn.setField("YD_GP",            	recInCrn.getFieldString("STACK_COL_GP").substring(0,1));
					recInCrn.setField("YD_BAY_GP",        	recInCrn.getFieldString("STACK_COL_GP").substring(1,2));
					recInCrn.setField("YD_SCH_CD",        	szSchCd);	
					recInCrn.setField("REGISTER",         	recInCrn.getFieldString("HANDLING_CNT"));	
					recInCrn.setField("YD_SCH_PRIOR",     	szYD_SCH_PRIOR);
					recInCrn.setField("YD_WBOOK_DT",      	szYD_WBOOK_DT);
					recInCrn.setField("YD_SCH_ST_GP",     	"A");
					recInCrn.setField("YD_UP_WO_LOC",     	recInCrn.getFieldString("STACK_COL_GP") + recInCrn.getFieldString("STACK_BED_GP"));
					recInCrn.setField("YD_UP_WO_LAYER",   	recInCrn.getFieldString("STACK_LAYER_GP"));
//					recInCrn.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE);
					
					ydToLocGuide = commUtils.trim(recInCrn.getFieldString("YD_TO_LOC_GUIDE"));
					
	        		//--------------------------------------------------------------------------
	        		// E100 서포팅블럭 이상으로 크레인스케줄 재 실행 될 때 작업예약ID의 YD_TO_LOC_GUIDE_FNL 값이 있을 경우
					if("Y".equals(sAPP100_E100_YN)){
						ydToLocGuide = szYD_TO_LOC_GUIDE_FNL;
					}
	        		//--------------------------------------------------------------------------
					
					
					//보조작업 인 경우 TO위치 가이드 CLEAR
					if(recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("W")){
						recInCrn.setField("YD_TO_LOC_GUIDE", 	"");
					}else{
						recInCrn.setField("YD_TO_LOC_GUIDE", 	ydToLocGuide);
					}

					if(ydToLocGuide.length() > 3 ){
						//현재 작업동과  TO위치 가이드 동이 틀린 경우  CLEAR
						if(!ydToLocGuide.substring(1,2).equals(recInCrn.getFieldString("STACK_COL_GP").substring(1,2))){
							recInCrn.setField("YD_TO_LOC_GUIDE", 	"");
						}
					}
					
					recInCrn.setField("YD_WRK_PROG_STAT", 	"W");
					
					if(commUtils.trim(recInCrn.getFieldString("YD_UP_WO_LOC")).equals("")){
						szLogMsg = "["+ methodNm +"] 권상지시위치가 없습니다.";
						commUtils.printLog(logId, szLogMsg, "SL");    			
		    			return YmConstant.RETN_INT_FAILURE;				
					}
 
 
					if("Y".equals(sYM2011_CRN_SCH_TX_YN)) {
						//크레인 스케줄 등록 트랜잭션 분리
						bslabComm.execQueryId(recInCrn, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insYmCrnsch");
					} else {
						intRtnVal = commDao.insert(recInCrn, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insYmCrnsch", logId, methodNm, "TB_YM_CRNSCH 생성");
						
						if(intRtnVal < 1) {
							szLogMsg = "["+ methodNm +"]크레인 스케줄 등록중  Error!! ErrorCode: " + intRtnVal;
							commUtils.printLog(logId, szLogMsg, "SL");
							return YmConstant.RETN_INT_FAILURE;
						}						
					} 
				}
				
				
				/**********************************************************
				*  크레인 스케줄 작업재료 등록
				**********************************************************/			
				recInCrnMtl = JDTORecordFactory.getInstance().create();
				recInCrnMtl.setResultCode(logId);	//Log ID
				recInCrnMtl.setResultMsg(methodNm);	//Log Method Name
				recInCrnMtl.setField("YD_CRN_SCH_ID", ydCrnSchId);
				/*
				 * 기존의 MAIN_WRK_YN 은 주작업이 Y 보조작업이 N으로 들어옴 
				 * 크레인작업재료에는 보조작업여부에 값은 보조작업인경우 Y 주작업인경우 N로 셋팅!
				 */
				
				if(recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("W")){
					recInCrnMtl.setField("YD_AID_WRK_YN", "Y"); //보조작업
				}else{
					recInCrnMtl.setField("YD_AID_WRK_YN", "N");
				}
				recInCrnMtl.setField("REGISTER"				, modifier);
				recInCrnMtl.setField("MOD_DDTT"				, "");
				recInCrnMtl.setField("STOCK_ID"				, commUtils.trim(recInCrn.getFieldString("STOCK_ID"  	 )));
				recInCrnMtl.setField("STACK_LAYER_GP"		, commUtils.trim(recInCrn.getFieldString("STACK_LAYER_GP")));
				recInCrnMtl.setField("YD_STK_LOT_TP"		, commUtils.trim(recInCrn.getFieldString("YD_STK_LOT_TP" )));
				recInCrnMtl.setField("YD_STK_LOT_CD"		, commUtils.trim(recInCrn.getFieldString("YD_STK_LOT_CD" )));
				recInCrnMtl.setField("HCR_GP"				, commUtils.trim(recInCrn.getFieldString("HCR_GP"        )));
				recInCrnMtl.setField("STL_PROG_CD"			, commUtils.trim(recInCrn.getFieldString("STL_PROG_CD"   )));
				recInCrnMtl.setField("YD_MTL_ITEM"			, commUtils.trim(recInCrn.getFieldString("YD_MTL_ITEM"   )));
				recInCrnMtl.setField("YD_ROUTE_GP"			, "");
				recInCrnMtl.setField("YD_TO_LOC_DCSN_MTD"	, commUtils.trim(recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD")));

				//크레인작업재료 생성
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.insYmCrnwrkmtl
				MERGE INTO TB_YM_CRNWRKMTL CS USING (
				SELECT :V_YD_CRN_SCH_ID  AS V_YD_CRN_SCH_ID
				      ,:V_STOCK_ID       AS V_STOCK_ID
				      ,:V_REGISTER       AS V_REGISTER
				      ,:V_YD_AID_WRK_YN  AS V_YD_AID_WRK_YN
				      ,:V_STACK_LAYER_GP AS V_STACK_LAYER_GP
				      ,:V_YD_STK_LOT_TP  AS V_YD_STK_LOT_TP
				      ,:V_YD_STK_LOT_CD  AS V_YD_STK_LOT_CD
				      ,:V_HCR_GP         AS V_HCR_GP
				      ,:V_STL_PROG_CD    AS V_STL_PROG_CD
				      ,:V_YD_MTL_ITEM    AS V_YD_MTL_ITEM
				      ,:V_YD_ROUTE_GP    AS V_YD_ROUTE_GP
				      ,:V_YD_TO_LOC_DCSN_MTD AS V_YD_TO_LOC_DCSN_MTD
				  FROM DUAL
				) DD ON (CS.YD_CRN_SCH_ID = DD.V_YD_CRN_SCH_ID AND CS.STOCK_ID = DD.V_STOCK_ID )
				 WHEN MATCHED THEN                                               
				 UPDATE SET CS.MODIFIER         = DD.V_REGISTER
				              ,CS.MOD_DDTT      = SYSDATE
				              ,CS.DEL_YN        = 'N'
				              ,CS.YD_AID_WRK_YN = DD.V_YD_AID_WRK_YN
				              ,CS.STACK_LAYER_GP= DD.V_STACK_LAYER_GP
				              ,CS.YD_STK_LOT_TP = DD.V_YD_STK_LOT_TP
				              ,CS.YD_STK_LOT_CD = DD.V_YD_STK_LOT_CD
				              ,CS.HCR_GP        = DD.V_HCR_GP
				              ,CS.STL_PROG_CD   = DD.V_STL_PROG_CD
				              ,CS.YD_MTL_ITEM   = DD.V_YD_MTL_ITEM
				              ,CS.YD_ROUTE_GP   = DD.V_YD_ROUTE_GP
				              ,CS.YD_TO_LOC_DCSN_MTD=DD.V_YD_TO_LOC_DCSN_MTD
				WHEN NOT MATCHED THEN
				INSERT (CS.YD_CRN_SCH_ID
				      ,CS.STOCK_ID
				      ,CS.REGISTER
				      ,CS.REG_DDTT
				      ,CS.DEL_YN
				      ,CS.YD_AID_WRK_YN
				      ,CS.STACK_LAYER_GP
				      ,CS.YD_STK_LOT_TP
				      ,CS.YD_STK_LOT_CD
				      ,CS.HCR_GP
				      ,CS.STL_PROG_CD
				      ,CS.YD_MTL_ITEM
				      ,CS.YD_ROUTE_GP
				      ,CS.YD_TO_LOC_DCSN_MTD)
				VALUES (DD.V_YD_CRN_SCH_ID
				      ,DD.V_STOCK_ID
				      ,DD.V_REGISTER
				      ,SYSDATE
				      ,'N'
				      ,DD.V_YD_AID_WRK_YN
				      ,DD.V_STACK_LAYER_GP
				      ,DD.V_YD_STK_LOT_TP
				      ,DD.V_YD_STK_LOT_CD
				      ,DD.V_HCR_GP
				      ,DD.V_STL_PROG_CD
				      ,DD.V_YD_MTL_ITEM
				      ,DD.V_YD_ROUTE_GP
				      ,DD.V_YD_TO_LOC_DCSN_MTD)
				*/       
				
				if("Y".equals(sYM2011_CRN_SCH_TX_YN)) {
					//크레인 스케줄 등록 트랜잭션 분리
					bslabComm.execQueryId(recInCrnMtl, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insYmCrnwrkmtl");
				} else {
					intRtnVal = commDao.insert(recInCrnMtl, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insYmCrnwrkmtl", logId, methodNm, "TB_YM_CRNWRKMTL 생성");
					
					if(intRtnVal < 1) {
						szLogMsg = "["+ methodNm +"] 크레인 스케줄 작업재료 등록중 실패: " + intRtnVal;
						commUtils.printLog(logId, szLogMsg, "SL");
						return YmConstant.RETN_INT_FAILURE;
					}						
				} 
			  
				
				sCrHandlingLot = commUtils.trim(recInCrn.getFieldString("HANDLING_CNT_SPEC"  ));
				/**********************************************************
				*  적치단의 재료상태를 권상대기로 변경
				**********************************************************/		
				recInCrn.setField("STACK_LAYER_STAT", "U");
				
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStackLayerMtlStat  
				UPDATE TB_YM_STACKLAYER            
				   SET MOD_DDTT     = SYSDATE             
				     , STACK_LAYER_STAT = NVL(:V_STACK_LAYER_STAT,STACK_LAYER_STAT)
				     , MODIFIER     = :V_MODIFIER             
				 WHERE STACK_COL_GP = :V_STACK_COL_GP
				   AND STACK_BED_GP = :V_STACK_BED_GP
				   AND STACK_LAYER_GP = :V_STACK_LAYER_GP
				   AND STOCK_ID = :V_STOCK_ID
		    	 */  
				intRtnVal = commDao.update(recInCrn, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStackLayerMtlStat", logId, methodNm, "TB_YM_STACKLAYER 갱신");
				if(intRtnVal < 1) {
					commUtils.printLog(logId, "[" + methodNm + "] 재료[" + recInCrn.getFieldString("STOCK_ID") + "]적재단 변경시 오류", "SL");
					return YmConstant.RETN_INT_FAILURE;
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return  YmConstant.RETN_INT_SUCCESS;
        
    }//end of CrnSchIns()
     
    
    
	/**
     * 오퍼레이션명 : B열연 SLAB YARD - TO위치결정
     * @param  inRecord, recGetCrnWrkMtl, rsResultCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public JDTORecord LocSrcRngDataSet (String logId, String methodNms, JDTORecord inRecord)throws JDTOException{
    	String methodNm = "TO위치시작[BSlabSchSeEJB.LocSrcRngDataSet] < " + methodNms;
    	
    	JDTORecord jrLocSrcRngRtn = JDTORecordFactory.getInstance().create();
    	String szMsg        		= "";     	  
    	
    	int intRtnVal 				= 0 ;

    	try{
        	commUtils.printLog(logId, "★"+methodNm, "S+");
        	//-------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------
			//파라미터 Null Check
        	String szWbookId	= commUtils.trim(inRecord.getFieldString("YD_WBOOK_ID" ));	
        	String szEqpId 		= commUtils.trim(inRecord.getFieldString("YD_EQP_ID"   ));	
        	String modifier 	= commUtils.trim(inRecord.getFieldString("MODIFIER"   ));	
        	String sAPP005_YN 	= commUtils.trim(inRecord.getFieldString("LOG_YN"));
        	//-------------------------------------------------------------------------------------------------------------
			//작업예약을 조회한다. To위치 결정방법이  사용자 지정인지 알기위해서...
			//-------------------------------------------------------------------------------------------------------------
        	JDTORecordSet jsTemp 		= JDTORecordFactory.getInstance().createRecordSet("");
			
        	 /* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYmWrkbook

        	SELECT YD_WBOOK_ID      AS YD_WBOOK_ID
        	      ,REGISTER         AS REGISTER
        	      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
        	      ,MODIFIER         AS MODIFIER
        	      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
        	      ,DEL_YN           AS DEL_YN
        	      ,YD_GP            AS YD_GP
        	      ,YD_BAY_GP        AS YD_BAY_GP
        	      ,YD_SCH_CD        AS YD_SCH_CD
        	      ,YD_SCH_PRIOR     AS YD_SCH_PRIOR
        	      ,YD_SCH_PROG_STAT AS YD_SCH_PROG_STAT
        	      ,YD_SCH_ST_GP     AS YD_SCH_ST_GP
        	      ,YD_SCH_REQ_GP    AS YD_SCH_REQ_GP
        	      ,YD_AIM_YD_GP     AS YD_AIM_YD_GP
        	      ,YD_AIM_BAY_GP    AS YD_AIM_BAY_GP
        	      ,YD_CTS_RELAY_YN  AS YD_CTS_RELAY_YN
        	      ,YD_CTS_RELAY_BAY_GP  AS YD_CTS_RELAY_BAY_GP
        	      ,YD_TO_LOC_DCSN_MTD AS YD_TO_LOC_DCSN_MTD
        	      ,YD_TO_LOC_GUIDE  AS YD_TO_LOC_GUIDE
        	      ,YD_WRK_PLAN_TCAR AS YD_WRK_PLAN_TCAR
        	      ,YD_CAR_USE_GP
        	      ,TRN_EQP_CD       AS TRN_EQP_CD
        	      ,CAR_NO           AS CAR_NO
        	      ,CARD_NO          AS CARD_NO
        	   FROM TB_YM_WRKBOOK A
        	 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			 */
			jsTemp = commDao.select(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYmWrkbook", logId, methodNm, "작업예약 조회"); 
	    	
	    	if (jsTemp == null || jsTemp.size() <= 0) {
				commUtils.printLog(logId, methodNm + "[작업예약종료]", "SL");
				
    			jrLocSrcRngRtn.setField("RTN", "-1");

				return jrLocSrcRngRtn;
				
			}			
			
			jsTemp.absolute(1);
			JDTORecord jrWbook = JDTORecordFactory.getInstance().create();
			jrWbook.setRecord(jsTemp.getRecord());
			
			String szSchCd 	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄 조회
			//-------------------------------------------------------------------------------------------------------------
			
			JDTORecord jrInPara = JDTORecordFactory.getInstance().create();
			
			jrInPara.setResultCode(logId);	//Log ID
			jrInPara.setResultMsg(methodNm);	//Log Method Name
			jrInPara.setField("YD_WBOOK_ID"	, szWbookId);
			jrInPara.setField("YD_EQP_ID"	, szEqpId);

			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdCrnSchByWBookId 
			SELECT A.EQUIP_GP                AS YD_EQP_ID                       
			     , A.EQUIP_NAME              AS YD_EQP_NAME                     
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
			  FROM TB_YM_EQUIP  A                                               
			     , TB_YM_CRNSCH B                                               
			 WHERE B.YD_EQP_ID      = A.EQUIP_GP  
			   AND B.YD_WBOOK_ID    = :V_YD_WBOOK_ID
			   AND B.YD_EQP_ID      = :V_YD_EQP_ID                         
			   AND B.DEL_YN = 'N'
			 ORDER BY B.YD_CRN_SCH_ID
			 */
			
			JDTORecordSet jsCrnsch = commDao.select(jrInPara, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdCrnSchByWBookId", logId, methodNm, "크레인스케줄 조회"); 

			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄의 권하지시위치 결정
			//-------------------------------------------------------------------------------------------------------------
			String StockId				= "";
			String StackColGp 			= "";// 적치열
			String StackBedGp			= "";// 적치베드
			String StackLayerGp			= "";//차량정지위치 적치단
			String ydCrnSchId 			= "";
	    	
	    	String szToLocDcsnMtd 		= "";
	    	String szToLocGuide 		= "";
	    	String ydWrkPlanTcar        = "";
	    	String szStackColGp 		= "";// 적치열
	    	String upStackColGp 		= "";// 적치열
	    	String ydAimRtGp 		    = "";// 행선
	    	String ydBendingYn 		    = "";// bending 여부
	    	String toLocSuccessYn       = "N";
			int    iCoilWt              = 0;
	    	
			JDTORecord jrParam 		= JDTORecordFactory.getInstance().create();
			JDTORecord jrCrnSch 	= JDTORecordFactory.getInstance().create();
			JDTORecord jrCrnMtlSch 	= JDTORecordFactory.getInstance().create();
			JDTORecord jrToLoc 		= JDTORecordFactory.getInstance().create();
			JDTORecord jrToLocGuide = JDTORecordFactory.getInstance().create();
			
			JDTORecordSet jsCrnMtlSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecordSet jsToLoc     = JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecordSet jsToLocGuide= JDTORecordFactory.getInstance().createRecordSet("Temp");
			
		    for(int Loop_i = 1; Loop_i <= jsCrnsch.size(); Loop_i++) {

        		jsCrnsch.absolute(Loop_i);
        		jrCrnSch  = jsCrnsch.getRecord();
        		
        		//크레인스케줄Data저장
        		ydCrnSchId     = commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));
        		szSchCd        = commUtils.trim(jrCrnSch.getFieldString("YD_SCH_CD"));
        		szToLocDcsnMtd = commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"));
        		szToLocGuide   = commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_GUIDE"));
        		szStackColGp   = commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));  //권상위치
        		
        		commUtils.printLog(logId, "szToLocGuide" + szToLocGuide, "SL");
        		
        		if(szStackColGp.length() != 8) {
        			szMsg = "LocSrcRngDataSet : 재료 권상위치 실패!!";
    				commUtils.printLog(logId, szMsg, "SL");
    				break;
        		} else {
        			upStackColGp = szStackColGp.substring(0,6);   
        		}
 
    			jrInPara.setField("YD_CRN_SCH_ID", ydCrnSchId);
    			jrInPara.setField("MODIFIER", modifier);
    			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdCrnwrkmtlBySchId 
    			SELECT AA.*
    			     , (CASE WHEN T_YD_RULE_PL_RS_GP='S' AND YD_SCH_CD ='2ESE01UM' THEN 'S'
    			             WHEN T_YD_RULE_PL_RS_GP='Y' AND YD_SCH_CD ='2ESE01UM' THEN 'Y' 
    			             WHEN T_YD_RULE_PL_RS_GP='S' AND YD_SCH_CD<>'2ESE01UM' THEN ''
    			             ELSE T_YD_RULE_PL_RS_GP END) AS YD_RULE_PL_RS_GP 
    			  FROM
    			    (
    			    SELECT A.STOCK_ID            AS STOCK_ID          
    			          ,D.STACK_LAYER_GP      AS STACK_LAYER_GP   
    			          ,A.YD_CRN_SCH_ID       AS YD_CRN_SCH_ID   
    			          ,A.DEL_YN              AS DEL_YN                 
    			          ,A.YD_AID_WRK_YN       AS YD_AID_WRK_YN   
    			          ,A.HCR_GP              AS HCR_GP                 
    			          ,A.STL_PROG_CD         AS STL_PROG_CD       
    			          ,A.YD_ROUTE_GP         AS YD_ROUTE_GP
    			          ,B.SLAB_T              AS YD_MTL_T     
    			          ,B.SLAB_W              AS YD_MTL_W        
    			          ,B.SLAB_LEN            AS YD_MTL_L   
    			          ,B.SLAB_WT             AS YD_MTL_WT       
    			          ,A.YD_MTL_ITEM         AS YD_MTL_ITEM     
    			          ,A.YD_STK_LOT_TP       AS YD_STK_LOT_TP   
    			          ,A.YD_STK_LOT_CD       AS YD_STK_LOT_CD  
    			          ,C.CTS_RELAY_SADDLE    AS CTS_RELAY_SADDLE  
    			          ,CASE WHEN SUBSTR(B.YD_STR_LOC,3,2) IN ('CT','WB') THEN TO_NUMBER(CTS_RELAY_SADDLE)
    			                ELSE TO_NUMBER(C.CHARGE_LOT_NO) END AS CHARGE_LOT_NO  
    			--          ,TO_NUMBER(C.CHARGE_LOT_NO)   AS CHARGE_LOT_NO       
    			          ,NVL(C.YD_AIM_RT_GP,STOCK_MOVE_TERM)      AS YD_AIM_RT_GP  
    			          ,E.YD_SCH_CD
    			          ,:V_MODIFIER           AS MODIFIER
    			          ,B.ORD_YEOJAE_GP
    			          ,SUM(B.SLAB_T)     OVER () AS SUM_MTL_T   --두께합
    			          ,MAX(B.SLAB_W)     OVER () AS MAX_MTL_W   --최대폭 
    			          ,MAX(B.SLAB_LEN)   OVER () AS MAX_MTL_L   --최대길이 
    			          ,SUM(B.SLAB_WT)    OVER () AS SUM_MTL_WT  --중량합    
    			          ,COUNT(A.STOCK_ID) OVER () AS CRN_WRK_SH    
    			          ,MAX(C.YD_RULE_PL_RS_GP) OVER () T_YD_RULE_PL_RS_GP
    			      FROM TB_YM_CRNSCH    E                                                        
    			         , TB_YM_CRNWRKMTL A                                                        
    			         , VW_YD_SLABCOMM  B    
    			         , TB_YM_STOCK     C 
    			         , USRYMA.TB_YM_STACKLAYER D
    			     WHERE E.YD_CRN_SCH_ID = A.YD_CRN_SCH_ID                                                    
    			       AND A.STOCK_ID = B.SLAB_NO                                                    
    			       AND A.STOCK_ID = C.STOCK_ID                                                    
    			       AND A.STOCK_ID = D.STOCK_ID 
    			       AND D.STACK_LAYER_STAT IN ('C','U')
    			       AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
    			       AND A.DEL_YN = 'N'                                    
    			       AND E.DEL_YN = 'N'                                    
    			     ORDER BY D.STACK_LAYER_GP
    			) AA
    			WHERE ROWNUM = 1
        		*/
           		
        		jsCrnMtlSch = commDao.select(jrInPara, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdCrnwrkmtlBySchId", logId, methodNm, "크레인스케줄재료 최하단  조회"); 
        		if(jsCrnMtlSch.size() <= 0) {
    				szMsg = "LocSrcRngDataSet : 위치검색범위 조회 크레인재료 정보 READ 실패!!";
    				commUtils.printLog(logId, szMsg, "SL");
    				break;

        		} else {
        			
        			jrCrnMtlSch  = jsCrnMtlSch.getRecord(0);
        		}
        		ydAimRtGp      = commUtils.trim(jrCrnMtlSch.getFieldString("YD_AIM_RT_GP"));  //야드행선

        		
        		szMsg = "작업예약 " + szWbookId + " [" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]에 대한 권하지시위치 결정 "; //szWbookId
        		commUtils.printLog(logId, szMsg, "SL");
        		
           		jrInPara.setField("STACK_COL_GP"	, upStackColGp);
           		jrInPara.setField("YD_SCH_CD"	    , szSchCd);
           		jrInPara.setField("YD_ROUTE_GP"	    , ydAimRtGp);
           		           		
        		if(szToLocDcsnMtd.equals("W")) {
        			
        			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdDummySearch 
        			SELECT *
        			  FROM (
        			         SELECT STACK_COL_GP
        			              , YD_GP
        			    	      , BAY_GP
        			    	      , SECT_GP
        			              , COL_GP
        			    	      , TO_CHAR(ABS(TO_NUMBER(SECT_GP) - TO_NUMBER(SUBSTR(:V_STACK_COL_GP,3,2)))) AS BY_SECT_GP
        			    	      , TO_CHAR(ABS(TO_NUMBER(COL_GP)  - TO_NUMBER(SUBSTR(:V_STACK_COL_GP,5,2))))  AS BY_COL_GP
        			    	      , ROWNUM AS DUMMY
        			           FROM TB_YM_STACKCOL
        			          WHERE YD_GP 	       = SUBSTR(:V_STACK_COL_GP,1,1)
        			            AND BAY_GP 	       = SUBSTR(:V_STACK_COL_GP,2,1)
        			            AND STACK_COL_GP LIKE SUBSTR(:V_STACK_COL_GP,0,4)||'%'
        			            AND STACK_COL_ACTIVE_STAT  = 'L'
        			       ) A    
        			 WHERE ABS(TO_NUMBER(COL_GP)  - TO_NUMBER(SUBSTR(:V_STACK_COL_GP,5,2))) < 4
        			 ORDER BY BY_SECT_GP,
        				  	  BY_COL_GP,	  	 
        				  	  COL_GP 
        			*/	  	  
               		
            		jsToLoc = commDao.select(jrInPara, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdDummySearch", logId, methodNm, "보조작업 TO위치 결정");
            		if(jsToLoc.size() == 0){
            			szMsg = " ▶  보조작업 TO위치 결정 실패!! 열[" + upStackColGp + "] ◀  ";
        				commUtils.printLog(logId, szMsg, "SL");
        				break;
            		}            		
        		} else {
        			if(szToLocGuide.length() >= 8)        {
        			} else if(szToLocGuide.length() < 8 && szToLocGuide.length() > 2 ) {
        				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getToLocUserCol 
        				SELECT STACK_COL_GP
        				  FROM TB_YM_STACKCOL
        				 WHERE STACK_COL_GP LIKE :V_YD_TO_LOC_GUIDE ||'%' 
        				   AND YD_GP = '2'
        				 ORDER BY STACK_COL_GP
        				 */
        				jrInPara.setField("YD_TO_LOC_GUIDE"	    , szToLocGuide);
	        			jsToLoc = commDao.select(jrInPara, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getToLocUserCol", logId, methodNm, "사용자 지정 TO위치 결정"); 
	            		if(jsToLoc.size() == 0){
	            			szMsg = " ▶  사용자 지정 TO위치범위 조회 실패!! 열[" + upStackColGp + "] ◀  ";
	        				commUtils.printLog(logId, szMsg, "SL");
	        				break;
	            		}
        			} else {
	        			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdPrimaryWorkSearch 
	        			--주작업 적치열 검색
	        			SELECT SS.YD_SCH_CD
	        			     , SS.YD_ROUTE_GP
	        			     , SC.STACK_COL_BED_QNTY
	        			     , SS.STACK_COL_GP
	        			  FROM TB_YM_SCHLOCSRCH       SS
	        			     , TB_YM_SCHLOCSRCHPRIOR  SP   
	        			     , TB_YM_STACKCOL         SC
	        			 WHERE SS.YD_SCH_CD    = SP.YD_SCH_CD(+)
	        			   AND SS.YD_ROUTE_GP  = SP.YD_ROUTE_GP(+)
	        			   AND SS.YD_SCH_CD    = :V_YD_SCH_CD    --'2AYD01MM'
	        			   AND SS.YD_ROUTE_GP  = :V_YD_ROUTE_GP  --'3S'
	        			   AND SS.STACK_COL_GP = SC.STACK_COL_GP
	        			   AND SC.STACK_COL_ACTIVE_STAT = 'L'
	        			 ORDER BY YD_LOC_SRCH_RNG_SEQ
	        			*/ 
	        			jsToLoc = commDao.select(jrInPara, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdPrimaryWorkSearch", logId, methodNm, "주작업 TO위치 결정"); 
	            		if(jsToLoc.size() == 0){
	            			szMsg = " ▶  주작업  TO위치검색범위 조회 실패!! 스케줄 코드[" + szSchCd + "]행선[" + ydAimRtGp + "] ◀  ";
	        				commUtils.printLog(logId, szMsg, "SL");
	        				break;
	            		}
        			}	
        		}
        		/**********************************************************
				* LOG
				**********************************************************/ 
        		if(sAPP005_YN.equals("Y")) {
        			String szDBLogMsg   	= "";
        			for(int Loop_k = 1; Loop_k <= jsToLoc.size(); Loop_k++) {
        				jsToLoc.absolute(Loop_k);
        				jrToLoc  = jsToLoc.getRecord();
        				szDBLogMsg = szDBLogMsg + commUtils.trim(jrToLoc.getFieldString("STACK_COL_GP")) + ",";
        			}	
        			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getToLocGuide 
        			SELECT A.* 
        			     , :V_YD_CRN_SCH_ID AS P_YD_CRN_SCH_ID
        			  FROM 
        			(
        			SELECT (SELECT YD_CRN_SCH_ID 
        			          FROM TB_YM_CRNSCH 
        			         WHERE YD_WBOOK_ID = A1.YD_WBOOK_ID 
        			           AND DEL_YN = 'N'
        			           AND ROWNUM = 1   ) AS YD_CRN_SCH_ID
        			     , A1.YD_WBOOK_ID
        			     , NVL(A1.YD_TO_LOC_GUIDE,'AAA') AS YD_TO_LOC_GUIDE
        			  FROM TB_YM_WRKBOOK A1
        			     , TB_YM_WRKBOOKMTL B1
        			 WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
        			   AND A1.DEL_YN = 'N'
        			   AND B1.DEL_YN = 'N'
        			   AND A1.YD_GP  = '2'
        			   AND LENGTH(A1.YD_TO_LOC_GUIDE) = 8
        			) A 
        			GROUP BY YD_CRN_SCH_ID, YD_WBOOK_ID, YD_TO_LOC_GUIDE	
        			*/
        			jsToLocGuide = commDao.select(jrInPara, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getToLocGuide", logId, methodNm, "TO위치가이드 제외"); 
            		if(jsToLocGuide.size() > 0){
            			szDBLogMsg = szDBLogMsg + "\r\n" + "제외대상 TO위치 지정 : ";
            			for(int Loop_k = 1; Loop_k <= jsToLocGuide.size(); Loop_k++) {
            				jsToLocGuide.absolute(Loop_k);
            				jrToLocGuide  = jsToLocGuide.getRecord();
            				szDBLogMsg = szDBLogMsg + commUtils.trim(jrToLocGuide.getFieldString("YD_TO_LOC_GUIDE"))+ ",";
            			}	
            		}
	        		JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, "SLAB");
	    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
	    			jrLog.setField("YD_GP"			, "2");
	    			jrLog.setField("YD_SCH_CD"		, szSchCd);
	    			jrLog.setField("SCH_CONTENTS"	, "검색 열:"+ szDBLogMsg + "\r\n" );
	    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
        		
        		if(szToLocGuide.length() >= 8) {
            		/**********************************************************
    				* 사용자 지정 
    				**********************************************************/            		
            		szMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자지정 스케줄의  To위치 결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
        			
        			this.procToLocUser(logId, methodNm,sAPP005_YN, jrWbook, jrCrnSch, jrCrnMtlSch);
        			
    			} else if("SE01UM".equals(szSchCd.substring(2, 8))) {
					/************************************
					 * 1. 스카핑 보급
					 ***********************************/  
            		szMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 스카핑 보급 스케줄의  To위치 결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
					
					this.procToLocBending(logId, methodNm, sAPP005_YN, jrWbook, jrCrnSch, jrCrnMtlSch, jsToLoc);

    			} else {	
	   				/************************************
					 * 1.첫 검색위치 우선순위  : 기본적으로 설비 ,야드 
					 ***********************************/
	   				/************************************
					 * 2. TO 위치 설비
					 * 2.1 TO위치가 W/B, CTC인지를 체크한다.if ("WB".equals(curColGp.substring(2, 4))||"CT".equals(curColGp.substring(2, 4))) {
					 * 2.2 TO위치가 보온카바(BK)인지를 체크한다
					 * 2.3 TO위치가 차량,대차(TC)인지를 체크한다.
					 ***********************************/
   					jrToLoc  = jsToLoc.getRecord(0);
   	        		//예정위치
   					String planToLoc = commUtils.trim(jrToLoc.getFieldString("STACK_COL_GP"));
    				
	        		/** 설비 인 경우: WB/CT/BK/PT/TC**/	
					if(YmComm.chkEqpIdGp(szSchCd,planToLoc)) {
						szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 설비임";
						this.procToLocEqpId(logId, methodNm, sAPP005_YN, jrWbook, jrCrnSch, jrCrnMtlSch, jsToLoc);
						
					} else {	
	    				/************************************
						 * 3. 일반 야드
						 *  3.1 SLAB의 장입LOT번호가 존재하지 않는 경우. (산적LOT번호 단위로 TO위치 결정)
						 *  3.1.1 Slab 이송하차
						 *  3.2 SLAB의 장입LOT번호가 존재하는 경우.
						 ***********************************/  
						if(szToLocDcsnMtd.equals("W")) {
		    				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 보조작업 :야드로 TO 위치결정 시작";
		    				commUtils.printLog(logId, szMsg, "SL");
						} else {
		    				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 :야드로 TO 위치결정 시작";
		    				commUtils.printLog(logId, szMsg, "SL");
						}
	    				this.procToLocPrimaryWork(logId, methodNm,sAPP005_YN, jrWbook, jrCrnSch, jrCrnMtlSch, jsToLoc);
	    				        				
	    				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은  작업 스케줄의 To위치 결정 완료 ";
	        			commUtils.printLog(logId, szMsg, "SL");
            		}
						
    			}
        	}
        
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnSchStatRe 
			SELECT A.YD_EQP_ID 
			     , (SELECT WPROG_STAT 
			          FROM TB_YM_EQUIP 
			         WHERE EQUIP_GP = A.YD_EQP_ID) AS YD_EQP_STAT
			  FROM TB_YM_CRNSCH A
			 WHERE A.YD_CRN_SCH_ID = ( SELECT MAX(YD_CRN_SCH_ID) AS YD_CRN_SCH_ID
			                           FROM TB_YM_CRNSCH 
			                          WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			                            AND DEL_YN  = 'N'
			                          )      
			   AND A.DEL_YN = 'N'   
			*/	   
			
		    
		    JDTORecord jrInPara1 = JDTORecordFactory.getInstance().create();
			
			jrInPara1.setResultCode(logId);	//Log ID
			jrInPara1.setResultMsg(methodNm);	//Log Method Name
			jrInPara1.setField("YD_WBOOK_ID"	, szWbookId);
			jrInPara1.setField("YD_EQP_ID"		, szEqpId);    
			JDTORecordSet jsCrnSchStatRe = commDao.select(jrInPara1, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnSchStatRe", logId, methodNm, "작업예약으로 크레인 스케쥴 조회");
			if (jsCrnSchStatRe.size() > 0) {
				szEqpId   =  commUtils.trim(jsCrnSchStatRe.getRecord(0).getFieldString("YD_EQP_ID"));
			}  
        	//-------------------------------------------------------------------------------------------------------------
    		// To위치 결정 실패시 default값으로 xx010101을 설정
        	//-------------------------------------------------------------------------------------------------------------
        	jsCrnsch 	= JDTORecordFactory.getInstance().createRecordSet("");
    		jrInPara 	= JDTORecordFactory.getInstance().create();
    		jrInPara.setField("YD_WBOOK_ID", szWbookId);
    		jrInPara.setField("YD_EQP_ID",   szEqpId);
    		jsCrnsch = commDao.select(jrInPara, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCrnschByEqpIdandWBookId", logId, methodNm, "크레인스케줄 조회");   		
    		
    		for(int Loop_i = 1; Loop_i <= jsCrnsch.size(); Loop_i++) {
				jsCrnsch.absolute(Loop_i);
				jrInPara = JDTORecordFactory.getInstance().create();
				jrInPara.setRecord(jsCrnsch.getRecord());
				if(commUtils.trim(jrInPara.getFieldString("YD_DN_WO_LOC")).equals("")) {
					jrInPara.setField("YD_DN_WO_LOC", "XX010101");
								   
					intRtnVal = commDao.update(jrInPara, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtDnLoc", logId, methodNm, "크레인스케줄 갱신");
					if(intRtnVal <= 0){
						szMsg = methodNm + " 크레인스케줄 To위치 Default값 등록 실패!!";
	    				commUtils.printLog(logId, szMsg, "SL");
	        			jrLocSrcRngRtn.setField("RTN", "-1");
	        			return jrLocSrcRngRtn;
					}
				}
			}
			
		//-------------------------------------------------------------------------------------------------------------
    		
        	commUtils.printLog(logId, methodNm, "S-");
			return jrLocSrcRngRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    }//end of LocSrcRngDataSet()   
    
	/**
	 * 설비작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procToLocEqpId(String logId, String methodNms,String sAPP005_YN, JDTORecord jrWbook, JDTORecord jrCrnSch, JDTORecord jrCrnMtlSch, JDTORecordSet jsToLoc) throws JDTOException {
    	String methodNm = "TO 위치결정:설비작업 [BSlabSchSeEJB.procToLocEqpId] < " + methodNms;
    	String szLogMsg					= null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------

		String ydSchCd 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String ydWookId			= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));		//작업예약
		String ydWrkPlanTcar    = commUtils.trim(jrWbook.getFieldString("YD_WRK_PLAN_TCAR"));	//작업예약-야드작업계획대차 

		String ydCrnSchId 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String ydEqpId     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String ydUpWoLoc 		= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String ydUpWoLayer 		= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));
		String StockId 	    	= commUtils.trim(jrCrnMtlSch.getFieldString("STOCK_ID"));
		String ydBendingYn  	= commUtils.trim(jrCrnMtlSch.getFieldString("YD_RULE_PL_RS_GP"));  //BENDING 여부
		String ydToLocDcsnMtd 	= commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"  ));//주작업여부		
		String sRtnBedDan       = "";
		String planToLoc        = "";
		
		
		JDTORecord jrToLoc      = JDTORecordFactory.getInstance().create();
		commUtils.printLog(logId, methodNm, "S+");
		commUtils.printParam(logId, jrCrnSch);
		
		try {
			if( ydUpWoLoc.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}

		    for(int Loop_i = 1; Loop_i <= jsToLoc.size(); Loop_i++) {

		    	jsToLoc.absolute(Loop_i);
		    	jrToLoc  	= jsToLoc.getRecord();
		    	planToLoc 	= commUtils.trim(jrToLoc.getFieldString("STACK_COL_GP"));
		    	
			    // 장입
				if ("WB".equals(planToLoc.substring(2, 4))||"CT".equals(planToLoc.substring(2, 4))){
					sRtnBedDan = this.procToLocWbCt(logId, methodNm,sAPP005_YN, planToLoc,jrWbook,jrCrnMtlSch, jrCrnSch, jsToLoc);
					if(sRtnBedDan.length() < 10) {
						continue;				
					} else {
						break;
					}
				}
				// 보온뱅크
				if ("BK".equals(planToLoc.substring(2, 4))){
					sRtnBedDan = this.procToLocBk(logId, methodNm, sAPP005_YN,planToLoc,jrWbook,jrCrnMtlSch, jrCrnSch, jsToLoc);
					if(sRtnBedDan.length() < 10) {
						continue;			
					} else {
						break;
					}
				} 
				// 차량
				if ("PT".equals(planToLoc.substring(2, 4))){
					sRtnBedDan = this.procToLocPtTc(logId, methodNm,sAPP005_YN, planToLoc,jrWbook, jrCrnMtlSch, jrCrnSch, jsToLoc);
					if(sRtnBedDan.length() < 10) {
						continue;
					} else {
						break;
					}
				}		 
				// 대차
				if ("TC".equals(planToLoc.substring(2, 4))){
					
					String sAPP100_Z01_YN = YmComm.BCoilApplyYn("APP100","2","Z01_YN");  
					
					commUtils.printLog(logId,  "==========[[[ SLAB 대차 TO위치 고도화 적용여부 :" + sAPP100_Z01_YN + " , 권상위치 : " + ydUpWoLoc + " ]]]============", "SL");
					
					if("Y".equals(sAPP100_Z01_YN) && "TC".equals(ydUpWoLoc.substring(2, 4))) {
						// 대차 -> 대차 시 
						break;
					} else {
						sRtnBedDan = this.procToLocPtTc(logId, methodNm,sAPP005_YN, planToLoc,jrWbook, jrCrnMtlSch, jrCrnSch, jsToLoc);
						if(sRtnBedDan.length() < 10) {
							continue;
						} else {
							break;
						}
					}
				}					
		    }
		    
		    //*********************************************
		    if(sRtnBedDan.length() < 10) {
		    	if("TC".equals(planToLoc.substring(2, 4))) {
		    		commUtils.printLog(logId, "++++++++++++++++++++++++++++++ " + planToLoc + " , " +   ydUpWoLoc + " , " + ydEqpId + " , " + ydWrkPlanTcar + " ++++++++++++++++++++++++++++++", "SL");
		    		
					JDTORecord jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					
					jrParam.setField("YD_CRN_SCH_ID"      , ydCrnSchId     ); 
					jrParam.setField("CARLOAD_STOP_LOC"   , planToLoc.substring(0,4)     ); 
					jrParam.setField("EQUIP_GP"   		  , ydWrkPlanTcar    ); 
					
		    		
		    		JDTORecordSet jsCarLdLoc = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getTcCarLdStopLoc", logId, methodNm, "동간작업기준 조회-하차지");
		    		
		    		if(jsCarLdLoc.size()>0) {
		    			
		    			sRtnBedDan = jsCarLdLoc.getRecord(0).getFieldString("YD_DN_WO_LOC");
		    			
		    		} else {
		    			
		    			//if("B".equals(planToLoc.substring(1,2))) {
			    		//	sRtnBedDan = "2BTC220101";
		    			//} else if("A".equals(planToLoc.substring(1,2))) {
		    			//	sRtnBedDan = "2ATC220101";
		    			//}
						szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 TO위치 결정 실패(대차TO위치) ";
						commUtils.printLog(logId, szLogMsg, "SL");
						return YmConstant.RETN_CD_FAILURE;				
		    		}
		    	}
		    }
		    //*********************************************

		    if(sRtnBedDan.length() < 10) {
				// 설비에 대상위치 선택이 안된 경우 검색조건에 야드로 등록 되어 있는 경우 위치 결정 재 결정 
		    	sRtnBedDan = this.procToLocPrimaryWork(logId, methodNm, sAPP005_YN, jrWbook, jrCrnSch, jrCrnMtlSch,  jsToLoc);			
			}
					
			if(sRtnBedDan.length() < 10) {
				// 설비 실패시 미리 XXX 처리 함 
				JDTORecord jrParm= JDTORecordFactory.getInstance().create();
				jrParm.setField("YD_CRN_SCH_ID", 		ydCrnSchId);			
				commDao.update(jrParm, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtDnLoc1", logId, methodNm, "크레인스케줄 갱신");
				
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 TO위치 결정 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;				
			}
			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = JDTORecordFactory.getInstance().create();
			jrSetLoc.setField("YD_CRN_SCH_ID", 	ydCrnSchId); 
			jrSetLoc.setField("YD_EQP_ID", 		ydEqpId);	 
			jrSetLoc.setField("YD_SCH_CD", 		ydSchCd);	 
			jrSetLoc.setField("YD_UP_WO_LOC", 	ydUpWoLoc); 
			jrSetLoc.setField("YD_UP_WO_LAYER",	ydUpWoLayer);	 
			jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER", sRtnBedDan.substring(8,10));
			jrSetLoc.setField("YD_WBOOK_ID", 	ydWookId); 
				
			this.procUpdateLoc(logId,methodNm, jrSetLoc, jrCrnSch, jrCrnMtlSch  );
			//----------------------------------------------------------------------------------------------------------------------
			// ERROR 발생시 ?
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YmConstant.RETN_CD_SUCCESS;
	}  
    
	/**
	 * 주작업TO위치결정  -> 야드
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procToLocPrimaryWork(String logId, String methodNms, String sAPP005_YN,JDTORecord jrWbook, JDTORecord jrCrnSch,JDTORecord jrCrnMtlSch,JDTORecordSet jsToLoc) throws JDTOException {
    	String methodNm = "야드 TO위치결정[BSlabSchSeEJB.procToLocPrimaryWork] < " + methodNms;
    	String szLogMsg		= null;
		JDTORecord	jrTemp	= null;
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String ydSchCd 	  		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String ydWookId			= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));			//작업예약

		String ydCrnSchId 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String ydEqpId    		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String ydUpWoLoc 		= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String ydUpWoLayer 		= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));
		String szToLocDcsnMtd 	= commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"));

		String ydWrkSh 			= commUtils.trim(jrCrnMtlSch.getFieldString("CRN_WRK_SH"));         //권상매수 
		String StockId 	    	= commUtils.trim(jrCrnMtlSch.getFieldString("STOCK_ID"));
		String ydChargeLotNo	= commUtils.trim(jrCrnMtlSch.getFieldString("CHARGE_LOT_NO"));		
		String ydMaxWid     	= commUtils.trim(jrCrnMtlSch.getFieldString("MAX_MTL_W"));      //MAX 폭		
		String ydMaxLen     	= commUtils.trim(jrCrnMtlSch.getFieldString("MAX_MTL_L"));		//MAX 길이
		String sRtnBedDan 		= "";
		String szDBLogMsg   	= "";
		
		try {
		
			if( ydUpWoLoc.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
			
			if (jsToLoc.size() <= 0) {
				szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
			
			/*******************
			 * TO위치 설비 정보 분리
			 *******************/
			JDTORecordSet jsToLocNew = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord jrToLoc		 = JDTORecordFactory.getInstance().create(); 
			String sToLoc  = "";
			for(int Loop_i = 1; Loop_i <= jsToLoc.size(); Loop_i++) {

		    	jsToLoc.absolute(Loop_i);
		    	jrToLoc = jsToLoc.getRecord();
		    	sToLoc 	= commUtils.trim(jrToLoc.getFieldString("STACK_COL_GP"));
		    	if ("WB".equals(sToLoc.substring(2, 4))||"CT".equals(sToLoc.substring(2, 4))     
		    			 ||"PT".equals(sToLoc.substring(2, 4))||"TC".equals(sToLoc.substring(2, 4))){  /*||"BK".equals(sToLoc.substring(2, 4)) 삭제*/
		    	} else {
		    		jsToLocNew.addRecord(jrToLoc);
		    	}
			}   	
			
			if (jsToLocNew.size() <= 0) {
				szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
			
			JDTORecord	    jrSearchLoc	= JDTORecordFactory.getInstance().create();
			
			JDTORecord jrInParam = JDTORecordFactory.getInstance().create();
			jrInParam.setResultCode(logId);	//Log ID
			jrInParam.setResultMsg(methodNm);	//Log Method Name
			jrInParam.setField("STOCK_ID"       , StockId);													//권상 STOCK
			jrInParam.setField("YD_SCH_CD"		, ydSchCd);		//스케줄 코드
			jrInParam.setField("YD_EQP_ID"		, ydEqpId);		//설비ID
			jrInParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);	//크레인 스케쥴 ID
			jrInParam.setField("CRN_WRK_SH"		, ydWrkSh);	
			
			if ("".equals(ydChargeLotNo)) {
				szDBLogMsg = szDBLogMsg + "장입순번이 없는 경우 검색." + "\r\n";
				//----------------------------------------------------------------------------------------------------------------------
				//	장입 순번이 없는 경우
				//----------------------------------------------------------------------------------------------------------------------
				// 이송하차
				if("PT02LM".equals(ydSchCd.substring(2, 8))){
					
					jrSearchLoc = this.procToLocPtToYd(logId, methodNms,jrWbook,jrCrnMtlSch,jrCrnSch, jsToLocNew );
					
				} else {
					jrSearchLoc = this.procToLocSameLot(logId, methodNms,jrWbook,jrCrnMtlSch,jrCrnSch, jsToLocNew );
					
				}	
				if (jrSearchLoc == null) {
					commUtils.printLog(logId, "▶▶▶폭,길이 항목을 가지고 검색", "SL");
					jrInParam.setField("SELECT_GP"	, "WID/LEN");	//검색위치
					jrInParam.setField("MAX_MTL_W"	, ydMaxWid);	
					jrInParam.setField("MAX_MTL_L"	, ydMaxLen);	
					
					jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLocNew);
				}
				
			} else {	
				//----------------------------------------------------------------------------------------------------------------------
				//	장입 순번이 있는 경우
				//----------------------------------------------------------------------------------------------------------------------			

				commUtils.printLog(logId, "장입LOT번호가 존재하는 경우", "SL");
				
				szDBLogMsg = szDBLogMsg + "장입순번이 있는 경우 검색." + "\r\n";

				/*
				 * 1. 장입LOT번호TO위치 동일한 장입LOT번호 TO위치 검색
				 */
				commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶장입LOT 순위가 동일한 TO 위치 검색   ◀◀◀◀◀◀◀◀◀◀", "SL");
				szDBLogMsg = szDBLogMsg + "장입LOT 순위가 동일한 TO 위치 검색." + "\r\n";
				
				jrInParam.setField("SELECT_GP"	, "G");	//검색위치
				jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLocNew);
				
				if (jrSearchLoc == null ) { 
					
					if("PT02LM".equals(ydSchCd.substring(2,8))) { //이송하차일경우 검색안하고 다음 검새으로 넘어감

						commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶이송하차스케줄은 하단에 후순위 장입LOT번호 TO위치 검색 안함 ◀◀◀◀◀◀◀◀◀◀", "SL");
						szDBLogMsg = szDBLogMsg + "이송하차스케줄은 하단에 후순위 장입LOT번호 TO위치 검색 안함." + "\r\n";
						
					} else {  //C동,A동 대차하차시 큰 장입번호 위체 작은 장입번호가 올라갈 수 있다.
						
						commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶하단에 후순위 장입LOT번호 TO위치 검색◀◀◀◀◀◀◀◀◀◀", "SL");
						szDBLogMsg = szDBLogMsg + "하단에 후순위 장입LOT번호 TO위치 검색." + "\r\n";
	
						jrInParam.setField("SELECT_GP"	, "P");	
						jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLocNew);
					}
				}
				if (jrSearchLoc == null) {
					commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶장입LOT 순위 01단 TO 위치 검색         ◀◀◀◀◀◀◀◀◀◀", "SL");
					szDBLogMsg = szDBLogMsg + "장입LOT 순위 01단 TO 위치 검색." + "\r\n";

					jrInParam.setField("SELECT_GP"	, "E");	//검색위치
					jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLocNew);
				}
				if (jrSearchLoc == null) {
					commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶행선이 충당대기인 위치 검색                 ◀◀◀◀◀◀◀◀◀◀", "SL");
					szDBLogMsg = szDBLogMsg + "선이 충당대기인 위치 검색 검색." + "\r\n";

					jrInParam.setField("SELECT_GP"	, "B");	//검색위치
					jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLocNew);
				}
				if (jrSearchLoc == null) {
					commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶하단에 장입LOT번호가 없는 TO위치 검색 ◀◀◀◀◀◀◀◀◀◀", "SL");
					szDBLogMsg = szDBLogMsg + "하단에 장입LOT번호가 없는 TO위치 검색." + "\r\n";
					jrInParam.setField("SELECT_GP"	, "N");	//검색위치
					jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLocNew);
				}
				if (jrSearchLoc == null) {
					if (!"C".equals(ydUpWoLoc.substring(1,2))) {
						// 동일 산적 lot 검색
						szDBLogMsg = szDBLogMsg + "동일 산적 lot 검색." + "\r\n";
						jrSearchLoc = this.procToLocSameLot(logId, methodNms,jrWbook,jrCrnMtlSch,jrCrnSch, jsToLocNew );
					}
				}
			}
			
			//보조작업 인 경우  검색
			if (jrSearchLoc == null) {
				if(szToLocDcsnMtd.equals("W")){ 
					commUtils.printLog(logId, "▶▶▶폭(0),길이(0)", "SL");
					szDBLogMsg = szDBLogMsg + "보조 작업인 경우  폭(0),길이(0) 검색." + "\r\n";

					jrInParam.setField("SELECT_GP"	, "WID0/LEN0");	//검색위치
					jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLocNew);
				}
			}	
		
			if(jrSearchLoc != null) {
				String szStackColGp   = commUtils.trim(jrSearchLoc.getFieldString("STACK_COL_GP")) ;
				String szStackBedGp   = commUtils.trim(jrSearchLoc.getFieldString("STACK_BED_GP")) ;
				String szStackLayerGp = commUtils.trim(jrSearchLoc.getFieldString("STACK_LAYER_GP")) ;
				sRtnBedDan = szStackColGp+szStackBedGp+ szStackLayerGp ;
			}	
				
			if(sRtnBedDan.length() < 10) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 TO위치 결정 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				if(sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, "SLAB");
	    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
	    			jrLog.setField("YD_GP"			, "2");
	    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
	    			jrLog.setField("SCH_CONTENTS"	, "일반야드 검색 실패:"+ szDBLogMsg + "\r\n" );
	    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
				
				return YmConstant.RETN_CD_FAILURE;				
			} else {
				if(sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, "SLAB");
	    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
	    			jrLog.setField("YD_GP"			, "2");
	    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
	    			jrLog.setField("SCH_CONTENTS"	, "일반야드 검색 성공:"+ sRtnBedDan +" LOG:" + szDBLogMsg + "\r\n" );
	    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}	
			}
			
			
			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = JDTORecordFactory.getInstance().create();
			jrSetLoc.setField("YD_CRN_SCH_ID", 	ydCrnSchId); 
			jrSetLoc.setField("YD_EQP_ID", 		ydEqpId);	 
			jrSetLoc.setField("YD_SCH_CD", 		ydSchCd);	 
			jrSetLoc.setField("YD_UP_WO_LOC", 	ydUpWoLoc); 
			jrSetLoc.setField("YD_UP_WO_LAYER",	ydUpWoLayer);	 
			jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER", sRtnBedDan.substring(8,10));
			jrSetLoc.setField("YD_WBOOK_ID", 	ydWookId); 
				
			this.procUpdateLoc(logId,methodNm, jrSetLoc, jrCrnSch ,jrCrnMtlSch );
			//----------------------------------------------------------------------------------------------------------------------
			// ERROR 발생시 ?
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S-");
	
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return sRtnBedDan;
    }	   
		
	/**
	 * 동일 산적 LOT검색
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord procToLocSameLot(String logId, String methodNms,JDTORecord jrWbook, JDTORecord jrCrnMtlSch, JDTORecord jrCrnSch , JDTORecordSet jsToLoc) throws JDTOException {
    	String methodNm = "TO 위치결정:이송하차가 아닌경우 [BSlabSchSeEJB.procToLocSameLot] < " + methodNms;
		JDTORecord		jrTemp			= JDTORecordFactory.getInstance().create();
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------

		String ydSchCd 	   	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String ydEqpId     	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String StockId 	    = commUtils.trim(jrCrnMtlSch.getFieldString("STOCK_ID"));
		String ydWrkSh 		= commUtils.trim(jrCrnMtlSch.getFieldString("CRN_WRK_SH"));     //권상매수 
		String ordYeojaeGp	= commUtils.trim(jrCrnMtlSch.getFieldString("ORD_YEOJAE_GP"));     //주여구분 
		
		JDTORecord jrSearchLoc          = JDTORecordFactory.getInstance().create();
		String planToLoc = "";
		commUtils.printLog(logId, methodNm, "S+");
		try {

			JDTORecord jrInParam = JDTORecordFactory.getInstance().create();
			jrInParam.setResultCode(logId);	//Log ID
			jrInParam.setResultMsg(methodNm);	//Log Method Name
			jrInParam.setField("STOCK_ID"       , StockId);													//권상 STOCK
			jrInParam.setField("YD_SCH_CD"		, ydSchCd);		//스케줄 코드
			jrInParam.setField("YD_EQP_ID"		, ydEqpId);		//설비ID
			jrInParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);	//크레인 스케쥴 ID
			jrInParam.setField("CRN_WRK_SH"		, ydWrkSh);	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	해당 slab 행선
			//----------------------------------------------------------------------------------------------------------------------			
			String ydAimRtGp = "";
			JDTORecord jrRtnProg = YmComm.getSlabYdAimRtGp(jrInParam);
			if(jrRtnProg.size() > 0 ) {
				ydAimRtGp = commUtils.trim(jrRtnProg.getFieldString("YD_AIM_RT_GP"));
			}
			commUtils.printLog(logId, "야드 TO 위치 결정전 행선코드CHECK:"+ ydAimRtGp, "SL");
			
			
			
			if(ydAimRtGp.equals("D2")||ydAimRtGp.equals("D3")) {  //핸드스카핑재
				
				commUtils.printLog(logId, "▶▶▶공BED 검색", "SL");
				jrInParam.setField("SELECT_GP"	, "E");	
				jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLoc);
				
				if (jrSearchLoc == null) {
					commUtils.printLog(logId, "▶▶▶산적LOT가 동일한 TO 위치 검색	", "SL");
					jrInParam.setField("SELECT_GP"	, "S");	
					jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLoc);
				}

			} else {	
				/*********************
				 * 대상이 주문재 인 경우 
				 ********************/
				if(ordYeojaeGp.equals("1")) {
					commUtils.printLog(logId, "▶▶▶주여구분이 같고 동일강종이고 동일생산폭(30mm이내),동일주문두께,주문폭 장입대상재가없는BED TO 위치 검색", "SL");
					jrInParam.setField("SELECT_GP"	, "L");	
					jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLoc);
	
					if (jrSearchLoc == null) {
						commUtils.printLog(logId, "▶▶▶공BED 검색", "SL");
						jrInParam.setField("SELECT_GP"	, "E");	
						jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLoc);
					}
					if (jrSearchLoc == null) {
						commUtils.printLog(logId, "▶▶▶동일 강종/폭/길이 위치 검색	", "SL");
						jrInParam.setField("SELECT_GP"	, "S1");	
						jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLoc);
					}	
				} else {
				/************************
				 * 대상이 여재 인 경우 
				 ***********************/
					commUtils.printLog(logId, "▶▶▶산적LOT가 동일한 TO 위치 검색	", "SL");
					jrInParam.setField("SELECT_GP"	, "S");	
					jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLoc);
	
					if (jrSearchLoc == null) {
						commUtils.printLog(logId, "▶▶▶공BED 검색", "SL");
						jrInParam.setField("SELECT_GP"	, "E");	
						jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLoc);
					}
					if (jrSearchLoc == null) {
						commUtils.printLog(logId, "▶▶▶동일 강종/폭/길이 위치 검색	", "SL");
						jrInParam.setField("SELECT_GP"	, "S1");	
						jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLoc);
					}	
				}
			}
			
			if (jrSearchLoc == null) {
				commUtils.printLog(logId, "▶▶▶적치가능한 01단 이상정보를 검색", "SL");

				jrInParam.setField("SELECT_GP"	, "U");	//검색위치
				jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLoc);
			}
			
			//----------------------------------------------------------------------------------------------------------------------
			// ERROR 발생시 ?
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return jrSearchLoc;
	}      
 		
	/**
	 * BENDING TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procToLocBending(String logId, String methodNms, String sAPP005_YN ,JDTORecord jrWbook, JDTORecord jrCrnSch, JDTORecord jrCrnMtlSch , JDTORecordSet jsToLoc) throws JDTOException {
    	String methodNm = "TO 위치결정:Bending작업[BSlabSchSeEJB.procToLocBending] < " + methodNms;
    	String szLogMsg		= null;
    	 // 적치 가능 check
    	JDTORecordSet jsSearchLoc = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord	  jrSearchLoc = JDTORecordFactory.getInstance().create();
		JDTORecord	  jrToLoc	  = JDTORecordFactory.getInstance().create();
		JDTORecord	  jrInParam	  = JDTORecordFactory.getInstance().create();
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String ydSchCd 	  	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String ydWookId		= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));		//작업예약
		String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String ydEqpId    	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String ydUpWoLoc 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String ydUpWoLayer 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));
		String StockId 	    = commUtils.trim(jrCrnMtlSch.getFieldString("STOCK_ID"));
		String ydBendingYn  = commUtils.trim(jrCrnMtlSch.getFieldString("YD_RULE_PL_RS_GP"));  //BENDING 여부
		String ydWrkSh 		= commUtils.trim(jrCrnMtlSch.getFieldString("CRN_WRK_SH"));         //권상매수 
		String szDBLogMsg   = "";
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			if( ydUpWoLoc.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
			
			if (jsToLoc.size() <= 0) {
				szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
			
				return YmConstant.RETN_CD_FAILURE;
			}
			for(int i = 1; i <= jsToLoc.size(); i++) {

				jsToLoc.absolute(i);
				jrToLoc  = jsToLoc.getRecord();

				jrInParam = JDTORecordFactory.getInstance().create();
				jrInParam.setResultCode(logId);	//Log ID
				jrInParam.setResultMsg(methodNm);	//Log Method Name
				jrInParam.setField("STOCK_ID"       , StockId);													//권상 STOCK
				jrInParam.setField("YD_SCH_CD"		, ydSchCd);		//스케줄 코드
				jrInParam.setField("YD_EQP_ID"		, ydEqpId);		//설비ID
				jrInParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);	//크레인 스케쥴 ID
				jrInParam.setField("STACK_COL_GP"	, commUtils.trim(jrToLoc.getFieldString("STACK_COL_GP")));	//검색위치
				jrInParam.setField("CRN_WRK_SH"		, ydWrkSh);	
				
				if(ydBendingYn.equals("Y")){
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getToLocDefinebending 
					SELECT  A.STACK_COL_GP,
					        A.STACK_BED_GP,
					        A.STACK_LAYER_GP,
					        A.STACK_BED_ABLE_QNTY,
					        A.GRIP_TO,
					        A.TO_LOC        
					FROM    (
					        SELECT  CUR.STACK_COL_GP,    --적치 열 구분
					                CUR.STACK_BED_GP,    --적치 BED 구분
					                CUR.STACK_LAYER_GP,  --적치 단 구분
					                CUR.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
					                        
					                NVL(CUR.STACK_COL_GP, '')    ||
					                NVL(CUR.STACK_BED_GP, '') AS GRIP_TO,
					                
					                NVL(CUR.STACK_COL_GP, '')    ||
					                NVL(CUR.STACK_BED_GP, '')    ||
					                NVL(CUR.STACK_LAYER_GP, '') AS TO_LOC
					        FROM    (
					                SELECT  SK.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
					                        SL.STACK_COL_GP,
					                        SL.STACK_BED_GP,
					                        SL.STACK_LAYER_GP,
					                        DECODE(SL.STACK_LAYER_GP - 1,
					                            0, '01', 
					                            DECODE(SL.STACK_LAYER_GP - 1, 
					                                9, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1),
					                                DECODE(LENGTH(SL.STACK_LAYER_GP - 1),
					                                    1, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1),
					                                    TO_CHAR(SL.STACK_LAYER_GP - 1)))) AS PRE_LAYER_GP
					                                    
					                FROM    (
					                         SELECT '1' AS STACK_BED_ABLE_QNTY 
					                              , A.STACK_COL_GP   --적치 열 구분
					                              , A.STACK_BED_GP   --적치 BED 구분
					                              , (SELECT SUM(CASE WHEN STACK_LAYER_STAT = 'E' AND STACK_LAYER_ACTIVE_STAT = 'E' THEN 1 ELSE 0 END)
					                                   FROM TB_YM_STACKLAYER 
					                                  WHERE STACK_COL_GP = A.STACK_COL_GP 
					                                    AND STACK_BED_GP = A.STACK_BED_GP) AS ABLE_LOC_CNT   
					                           FROM TB_YM_STACKER A
					                          WHERE A.STACK_COL_GP            = :V_STACK_COL_GP
					                            AND A.STACK_BED_ACTIVE_STAT   = 'L'
					                          ORDER BY STACK_BED_GP ASC
					                        )SK,
					                        TB_YM_STACKLAYER SL
					                WHERE   SK.STACK_COL_GP = SL.STACK_COL_GP
					                AND     SK.STACK_BED_GP = SL.STACK_BED_GP
					                AND     SL.STACK_LAYER_STAT = 'E'
					                AND     SL.STACK_LAYER_ACTIVE_STAT = 'E'
					                AND     SL.STOCK_ID IS NULL
					 --적치 가능 매수 반드시                 
					                AND     SK.ABLE_LOC_CNT >= :V_CRN_WRK_SH                
					                ) CUR,
					                TB_YM_STACKLAYER SL1
					        WHERE   CUR.STACK_COL_GP = SL1.STACK_COL_GP
					        AND     CUR.STACK_BED_GP = SL1.STACK_BED_GP
					        AND     CUR.PRE_LAYER_GP = SL1.STACK_LAYER_GP
					        AND     SL1.STACK_LAYER_STAT   NOT IN ('U')
					        AND     CUR.STACK_COL_GP||CUR.STACK_BED_GP NOT IN (--작업예약 편성위치 제외
					                                                           SELECT C.STACK_COL_GP || C.STACK_BED_GP
					                                                             FROM TB_YM_WRKBOOK A 
					                                                                , TB_YM_WRKBOOKMTL B
					                                                                , TB_YM_STACKLAYER C
					                                                            WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
					                                                              AND B.STOCK_ID = C.STOCK_ID
					                                                              AND A.DEL_YN = 'N'
					                                                              AND B.DEL_YN = 'N' 
					                                                              AND C.STACK_LAYER_STAT IN ('C','U') 
					                                                           )
					        ORDER BY STACK_COL_GP, STACK_BED_GP, STACK_LAYER_GP
					    )A
					WHERE STACK_BED_GP='03' -- BENDING 존 
					  AND ROWNUM <=1
					*/  
					jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getToLocDefinebending", logId, methodNm, "Bending Bed 검색");
					szDBLogMsg = szDBLogMsg + "밴딩임: 위치 " + commUtils.trim(jrToLoc.getFieldString("STACK_COL_GP"));

				} else {
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocCol 
					SELECT  A.STACK_COL_GP,
					        A.STACK_BED_GP,
					        A.STACK_LAYER_GP,
					        A.STACK_BED_ABLE_QNTY,
					        A.GRIP_TO,
					        A.TO_LOC,
					        A.ABLE_LOC_CNT
					FROM    (
					        SELECT  CUR.STACK_COL_GP,    --적치 열 구분
					                CUR.STACK_BED_GP,    --적치 BED 구분
					                CUR.STACK_LAYER_GP,  --적치 단 구분
					                CUR.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
					                        
					                NVL(CUR.STACK_COL_GP, '')    ||
					                NVL(CUR.STACK_BED_GP, '') AS GRIP_TO,
					                
					                NVL(CUR.STACK_COL_GP, '')    ||
					                NVL(CUR.STACK_BED_GP, '')    ||
					                NVL(CUR.STACK_LAYER_GP, '') AS TO_LOC,
					                CUR.ABLE_LOC_CNT
					        FROM    (
					                SELECT  SK.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
					                        SL.STACK_COL_GP,
					                        SL.STACK_BED_GP,
					                        SL.STACK_LAYER_GP,
					                        DECODE(SL.STACK_LAYER_GP - 1,
					                            0, '01', 
					                            DECODE(SL.STACK_LAYER_GP - 1, 
					                                9, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1),
					                                DECODE(LENGTH(SL.STACK_LAYER_GP - 1),
					                                    1, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1),
					                                    TO_CHAR(SL.STACK_LAYER_GP - 1)))) AS PRE_LAYER_GP,
					                        ABLE_LOC_CNT                                    
					                FROM    (
					                         SELECT '1' AS STACK_BED_ABLE_QNTY, 
					                                A.STACK_COL_GP,   --적치 열 구분
					                                A.STACK_BED_GP   --적치 BED 구분
					                                , (SELECT SUM(CASE WHEN STACK_LAYER_STAT = 'E' AND STACK_LAYER_ACTIVE_STAT = 'E' THEN 1 ELSE 0 END)
					                                   FROM TB_YM_STACKLAYER 
					                                  WHERE STACK_COL_GP = A.STACK_COL_GP 
					                                    AND STACK_BED_GP = A.STACK_BED_GP) AS ABLE_LOC_CNT
					                           FROM TB_YM_STACKER A
					                          WHERE A.STACK_COL_GP            = :V_STACK_COL_GP
					                            AND A.STACK_BED_ACTIVE_STAT   = 'L'
					                          ORDER BY STACK_BED_GP ASC
					                        )SK,
					                        TB_YM_STACKLAYER SL
					                WHERE   SK.STACK_COL_GP = SL.STACK_COL_GP
					                AND     SK.STACK_BED_GP = SL.STACK_BED_GP
					                AND     SL.STACK_LAYER_STAT        = 'E'
					                AND     SL.STACK_LAYER_ACTIVE_STAT = 'E'
					                AND     SL.STOCK_ID IS NULL
					                --적치 가능 매수 반드시                 
					                AND     SK.ABLE_LOC_CNT >= nvl(:V_CRN_WRK_SH,2) 
					                ) CUR,
					                TB_YM_STACKLAYER SL1
					        WHERE   CUR.STACK_COL_GP = SL1.STACK_COL_GP
					        AND     CUR.STACK_BED_GP = SL1.STACK_BED_GP
					        AND     CUR.PRE_LAYER_GP = SL1.STACK_LAYER_GP
					        AND     SL1.STACK_LAYER_STAT   NOT IN ('U')
					        AND A.STACK_COL_GP||A.STACK_BED_GP NOT IN (
					                                               -- TO위치 가이드 제외
					                                              SELECT NVL(A1.YD_TO_LOC_GUIDE,'AAA')
					                                                FROM TB_YM_WRKBOOK A1
					                                                   , TB_YM_WRKBOOKMTL B1
					                                               WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
					                                                 AND A1.DEL_YN = 'N'
					                                                 AND B1.DEL_YN = 'N'
					                                                 AND A1.YD_GP  = '2'
					                                                 AND LENGTH(A1.YD_TO_LOC_GUIDE) = 8
					                                               GROUP BY A1.YD_TO_LOC_GUIDE
					                                              UNION ALL
					                                              SELECT NVL(B1.MTL_YD_TO_LOC_GUIDE,'AAA')
					                                                FROM TB_YM_WRKBOOK A1
					                                                   , TB_YM_WRKBOOKMTL B1
					                                               WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
					                                                 AND A1.DEL_YN = 'N'
					                                                 AND B1.DEL_YN = 'N'
					                                                 AND A1.YD_GP  = '2'
					                                                 AND LENGTH(B1.MTL_YD_TO_LOC_GUIDE) = 8
					                                               GROUP BY B1.MTL_YD_TO_LOC_GUIDE  
					                                              UNION ALL   
					                                               -- 작업예약된 BED 제외
					                                              SELECT NVL(C1.STACK_COL_GP || C1.STACK_BED_GP,'AAA')
					                                                FROM TB_YM_WRKBOOK A1 
					                                                   , TB_YM_WRKBOOKMTL B1
					                                                   , TB_YM_STACKLAYER C1
					                                               WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
					                                                 AND B1.STOCK_ID = C1.STOCK_ID
					                                                 AND A1.DEL_YN = 'N'
					                                                 AND B1.DEL_YN = 'N' 
					                                                 AND A1.YD_GP  = '2'
					                                                 AND C1.STACK_LAYER_STAT IN ('C','U') 
					                                               GROUP BY C1.STACK_COL_GP || C1.STACK_BED_GP  
					                                               )                                                          
					                                                           
					        ORDER BY STACK_COL_GP, STACK_BED_GP, STACK_LAYER_GP
					    )A
					WHERE  ROWNUM = 1
					*/
					jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocCol", logId, methodNm, "해당열 검색");
					szDBLogMsg = szDBLogMsg + "밴딩아님: 위치 " + commUtils.trim(jrToLoc.getFieldString("STACK_COL_GP")) ;

				}
				if(jsSearchLoc.size() > 0) {
					
					jsSearchLoc.absolute(1);
					jrSearchLoc  = jsSearchLoc.getRecord();
					break;
				} else {
					szDBLogMsg = szDBLogMsg + "실패:대상없음  "+ "\r\n";
				}
			}
			
			String sRtnBedDan 	= "";  //TO위치	
			
			if(jrSearchLoc != null) {
				String szStackColGp   = commUtils.trim(jrSearchLoc.getFieldString("STACK_COL_GP")) ;
				String szStackBedGp   = commUtils.trim(jrSearchLoc.getFieldString("STACK_BED_GP")) ;
				String szStackLayerGp = commUtils.trim(jrSearchLoc.getFieldString("STACK_LAYER_GP")) ;
				sRtnBedDan = szStackColGp + szStackBedGp + szStackLayerGp ;
			}	
				
			if(sRtnBedDan.length() < 10) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 TO위치 결정 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				
				if(sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, "SLAB");
	    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
	    			jrLog.setField("YD_GP"			, "2");
	    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
	    			jrLog.setField("SCH_CONTENTS"	, "2XSE01UM(스카핑보급) 실패:"+ "\r\n" );
	    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
				
				return YmConstant.RETN_CD_FAILURE;				
			} else {
				if(sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, "SLAB");
	    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
	    			jrLog.setField("YD_GP"			, "2");
	    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
	    			jrLog.setField("SCH_CONTENTS"	, szDBLogMsg + "TO위치 결정됨: 위치 " + sRtnBedDan + "\r\n" );
	    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
			}
			
			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = JDTORecordFactory.getInstance().create();
			jrSetLoc.setField("YD_CRN_SCH_ID", 	ydCrnSchId); 
			jrSetLoc.setField("YD_EQP_ID", 		ydEqpId);	 
			jrSetLoc.setField("YD_SCH_CD", 		ydSchCd);	 
			jrSetLoc.setField("YD_UP_WO_LOC", 	ydUpWoLoc); 
			jrSetLoc.setField("YD_UP_WO_LAYER",	ydUpWoLayer);	 
			jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER", sRtnBedDan.substring(8,10));
			jrSetLoc.setField("YD_WBOOK_ID", 	ydWookId); 
				
			this.procUpdateLoc(logId,methodNm, jrSetLoc, jrCrnSch ,jrCrnMtlSch );
			//----------------------------------------------------------------------------------------------------------------------
			// ERROR 발생시 ?
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YmConstant.RETN_CD_SUCCESS;
	}        
	/**
	 * TO BED 검색
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord procToLocBed(String logId, String methodNms,  JDTORecord jrData ,JDTORecordSet jsToLoc) throws JDTOException {
    	String methodNm = "TO위치 결정:TO BED 검색[BSlabSchSeEJB.procToLocBed] < " + methodNms;

    	//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String ydSchCd 	   	= commUtils.trim(jrData.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String StockId	   	= commUtils.trim(jrData.getFieldString("STOCK_ID"));			//크레인작업재료
		String ydCrnSchId 	= commUtils.trim(jrData.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String ydEqpId 		= commUtils.trim(jrData.getFieldString("YD_EQP_ID"));			//크레인설비ID
		
        String ydSelectGp   = commUtils.trim(jrData.getFieldString("SELECT_GP"));			//검색구분
		String ydMaxWid     = commUtils.trim(jrData.getFieldString("MAX_MTL_W"));      //MAX 폭		
		String ydMaxLen     = commUtils.trim(jrData.getFieldString("MAX_MTL_L"));		//MAX 길이
		String ydWrkSh 		= commUtils.trim(jrData.getFieldString("CRN_WRK_SH")); //권상매수		
		String StackColGp   = "";
		String equipGpYn    = "";
        try {
			
			JDTORecordSet jsSearchLoc = JDTORecordFactory.getInstance().createRecordSet("");
		    // 적치 가능 check
			JDTORecord	    jrSearchLoc	= null;
			JDTORecord	    jrToLoc	    = JDTORecordFactory.getInstance().create();
			JDTORecord	    jrInParam	= JDTORecordFactory.getInstance().create();
			
			for(int i = 1; i <= jsToLoc.size(); i++) {

				jsToLoc.absolute(i);
				jrToLoc  = jsToLoc.getRecord();

				jrInParam = JDTORecordFactory.getInstance().create();
				jrInParam.setResultCode(logId);	//Log ID
				jrInParam.setResultMsg(methodNm);	//Log Method Name
				jrInParam.setField("STOCK_ID"       , StockId);													//권상 STOCK
				jrInParam.setField("YD_SCH_CD"		, ydSchCd);		//스케줄 코드
				jrInParam.setField("YD_EQP_ID"		, ydEqpId);		//설비ID
				jrInParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);	//크레인 스케쥴 ID
				StackColGp = commUtils.trim(jrToLoc.getFieldString("STACK_COL_GP"));
				
				jrInParam.setField("STACK_COL_GP"	, StackColGp);	//검색위치
				jrInParam.setField("CRN_WRK_SH"		, ydWrkSh);
				if(StackColGp.matches("[2][A-E]\\d\\d\\d\\d")) {
					equipGpYn = "N";
				} else {
					//설비 여부
					if(YmComm.chkEqpIdGp(ydSchCd,commUtils.trim(jrToLoc.getFieldString("STACK_COL_GP")))){
						equipGpYn = "Y";
					} else{
						equipGpYn = "N";
					}
				}
				
				//설비가 아닌 경우 만 처리 함
				if(equipGpYn.equals("N")) {
					commUtils.printLog(logId, "검색열:"+ commUtils.trim(jrToLoc.getFieldString("STACK_COL_GP")), "S+");
					// DUMMY 관련	
					if(ydSelectGp.equals("E")) {
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocE", logId, methodNm, "공BED 검색");
					} else if(ydSelectGp.equals("S")) { 	
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocS", logId, methodNm, "동일 LOT번호 검색");
					} else if(ydSelectGp.equals("U")) { 	
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocU", logId, methodNm, "적치가능한 01단이상 정보를 검색");
					} else if(ydSelectGp.equals("WID/LEN")) { 	
						jrInParam.setField("MIN_WID"	, ydMaxWid);	
						jrInParam.setField("MAX_WID"	, ydMaxWid);	
						jrInParam.setField("MIN_LEN"	, ydMaxLen);	
						jrInParam.setField("MAX_LEN"	, ydMaxLen);	
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocColWidLen", logId, methodNm, "폭("+ydMaxWid+"),길이("+ydMaxLen+")항목을 가지고 적치가능한 곳을 검색");
					} else if(ydSelectGp.equals("WID0/LEN0")) { 	
						jrInParam.setField("MIN_WID"	, "999999");	
						jrInParam.setField("MAX_WID"	, "0");	
						jrInParam.setField("MIN_LEN"	, "999999");	
						jrInParam.setField("MAX_LEN"	, "0");	
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocColWidLen", logId, methodNm, "폭(0),길이(0)항목을 가지고 적치가능한 곳을 검색");
					} else if(ydSelectGp.equals("G")) { 	
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocG",  logId, methodNm, "최상단에 같은 장입순번이 있는 위치검색");
					} else if(ydSelectGp.equals("P")) { 	
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocP",  logId, methodNm, "최상단이 후순위 장입순번  검색");
					} else if(ydSelectGp.equals("N")) { 	
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocN",  logId, methodNm, "공bed가 아니고 최상단이 장입재가 아닌  검색");
					} else if(ydSelectGp.equals("L")) { 	
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocL",  logId, methodNm, "주여구분이 같고 동일강종이고  생산폭(30mm이내),동일 주문두께,주문폭 검색");
					} else if(ydSelectGp.equals("L2")) { 	
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocL2", logId, methodNm, "주여구분이 같고 동일강종이고 폭이 30mm 이내  검색");
					} else if(ydSelectGp.equals("M")) { 	
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocM",  logId, methodNm, "주여구분이 같고 대상재두께 폭인  주문두께,폭이보다 작은 경우 검색");
					} else if(ydSelectGp.equals("K")) { 	
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocK",  logId, methodNm, "동일강종  검색");

					} else if(ydSelectGp.equals("B")) { 	
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocB",  logId, methodNm, "행선이 충당대기 검색");
					} else if(ydSelectGp.equals("A")) { 	
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocA",  logId, methodNm, "충당대기 검색");
					} else if(ydSelectGp.equals("S1")) { 	
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocS1", logId, methodNm, "주여구분이 같고 동일 생산폭(30mm이내) 검색");
					}
					if(jsSearchLoc.size() > 0) {
						
						jsSearchLoc.absolute(1);
						jrSearchLoc  = jsSearchLoc.getRecord();
						return jrSearchLoc;
					}
				}	
			}
			
			
			commUtils.printLog(logId, methodNm, "S-");
			return jrSearchLoc;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
	}    
 
	/**
	 * 사용자지정작업(10,8자리)
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procToLocUser(String logId, String methodNms,String sAPP005_YN, JDTORecord jrWbook, JDTORecord jrCrnSch, JDTORecord jrCrnMtlSch) throws JDTOException {
    	String methodNm = "TO 위치결정:사용자지정작업[BSlabSchSeEJB.procToLocUser] < " + methodNms;
    	String szLogMsg					= null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String ydSchCd 	   	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));		//크레인스케줄코드
		String ydWookId		= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));	//작업예약
		String ydToLocGuide	= commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_GUIDE")); //작업예약
		String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String ydEqpId     	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String ydUpWoLoc 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String ydUpWoLayer 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));		
		String StockId 	    = commUtils.trim(jrCrnMtlSch.getFieldString("STOCK_ID"));
		String ydWrkSh 		= commUtils.trim(jrCrnMtlSch.getFieldString("CRN_WRK_SH"));         //권상매수

		String sRtnBedDan 	= "";  //TO위치	
		String SeaechLocYn 	= "";  //TO위치	
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			if( ydUpWoLoc.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}

			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setResultCode(logId);	//Log ID
			jrTemp.setResultMsg(methodNm);	//Log Method Name
			jrTemp.setField("STOCK_ID"			, StockId);											//권상 STOCK
			jrTemp.setField("YD_TO_LOC_GUIDE"	, ydToLocGuide);									//가이드
			jrTemp.setField("YD_SCH_CD"			, ydSchCd);											//스케쥴 코드
			jrTemp.setField("YD_CRN_SCH_ID"		, ydCrnSchId);										//크레인 작업지시 ID
			jrTemp.setField("YD_EQP_ID"			, ydEqpId);											//설비 번호
			jrTemp.setField("CRN_WRK_SH"		, ydWrkSh);	
			szLogMsg =  " TOSQL:["+ydCrnSchId+ "] 권상재료["+StockId +" +스케쥴 코드 : "+ ydSchCd + "의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
	      	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	사용자 지정한 위치만 검색
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			if(ydToLocGuide.length() == 10) {

				szLogMsg =  " 적재위치 가이드 10자리  열+ 베드+ 단  지정된 경우 ["+ydToLocGuide+"]의 베드 조회 시작";
				commUtils.printLog(logId, szLogMsg, "SL");
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStackLayerInfoWithPk
				SELECT CASE WHEN :V_STACK_COL_GP = '2CCT04' THEN ''
				            ELSE STOCK_ID END STOCK_ID
				     , CASE WHEN :V_STACK_COL_GP = '2CCT04' THEN 'E'
				            ELSE STACK_LAYER_STAT END STACK_LAYER_STAT 
				  FROM TB_YM_STACKLAYER
				 WHERE STACK_COL_GP  	= :V_STACK_COL_GP
				   AND STACK_BED_GP	    = :V_STACK_BED_GP 
				   AND STACK_LAYER_GP	= :V_STACK_LAYER_GP 
				*/
				jrTemp.setField("STACK_COL_GP"		, ydToLocGuide.substring(0,6));	
				jrTemp.setField("STACK_BED_GP"		, ydToLocGuide.substring(6,8));	
				jrTemp.setField("STACK_LAYER_GP"	, ydToLocGuide.substring(8));	
				
				commUtils.printLog(logId, "ydWrkSh" + ydWrkSh, "SL");
				JDTORecordSet outjsBed = commDao.select(jrTemp, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStackLayerInfoWithPk", logId, methodNm, "사용자10TOSQL 베드 조회");
				if (outjsBed.size() > 0) {
					JDTORecord outjrBed = outjsBed.getRecord(0);
 	 				if(outjrBed != null) {
 	 					String chkStockId   = commUtils.trim(outjrBed.getFieldString("STOCK_ID")) ;
 	 					String chkLayerStat = commUtils.trim(outjrBed.getFieldString("STACK_LAYER_STAT")) ;
 	 					if (!"".equals(chkStockId) && chkLayerStat.equals("C")) {
 	 						SeaechLocYn = "N";           // 미검출
 	 					} else {
 	 						sRtnBedDan = ydToLocGuide;
 	 					}
 	 				} else {
 	 					 SeaechLocYn = "N";
 	 				}
				} else { 
					SeaechLocYn = "N";
				}
				
//				if (SeaechLocYn.equals("N")) {
//					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocCol
//					SELECT  A.STACK_COL_GP,
//					        A.STACK_BED_GP,
//					        A.STACK_LAYER_GP,
//					        A.STACK_BED_ABLE_QNTY,
//					        A.GRIP_TO,
//					        A.TO_LOC,
//					        A.ABLE_LOC_CNT
//					FROM    (
//					        SELECT  CUR.STACK_COL_GP,    --적치 열 구분
//					                CUR.STACK_BED_GP,    --적치 BED 구분
//					                CUR.STACK_LAYER_GP,  --적치 단 구분
//					                CUR.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
//					                        
//					                NVL(CUR.STACK_COL_GP, '')    ||
//					                NVL(CUR.STACK_BED_GP, '') AS GRIP_TO,
//					                
//					                NVL(CUR.STACK_COL_GP, '')    ||
//					                NVL(CUR.STACK_BED_GP, '')    ||
//					                NVL(CUR.STACK_LAYER_GP, '') AS TO_LOC,
//					                CUR.ABLE_LOC_CNT
//					        FROM    (
//					                SELECT  SK.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
//					                        SL.STACK_COL_GP,
//					                        SL.STACK_BED_GP,
//					                        SL.STACK_LAYER_GP,
//					                        DECODE(SL.STACK_LAYER_GP - 1,
//					                            0, '01', 
//					                            DECODE(SL.STACK_LAYER_GP - 1, 
//					                                9, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1),
//					                                DECODE(LENGTH(SL.STACK_LAYER_GP - 1),
//					                                    1, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1),
//					                                    TO_CHAR(SL.STACK_LAYER_GP - 1)))) AS PRE_LAYER_GP,
//					                        ABLE_LOC_CNT                                    
//					                FROM    (
//					                         SELECT '1' AS STACK_BED_ABLE_QNTY, 
//					                                A.STACK_COL_GP,   --적치 열 구분
//					                                A.STACK_BED_GP   --적치 BED 구분
//					                                , (SELECT SUM(CASE WHEN STACK_LAYER_STAT = 'E' AND STACK_LAYER_ACTIVE_STAT = 'E' THEN 1 ELSE 0 END)
//					                                   FROM TB_YM_STACKLAYER 
//					                                  WHERE STACK_COL_GP = A.STACK_COL_GP 
//					                                    AND STACK_BED_GP = A.STACK_BED_GP) AS ABLE_LOC_CNT
//					                           FROM TB_YM_STACKER A
//					                          WHERE A.STACK_COL_GP            = :V_STACK_COL_GP
//					                            AND A.STACK_BED_ACTIVE_STAT   = 'L'
//					                          ORDER BY STACK_BED_GP ASC
//					                        )SK,
//					                        TB_YM_STACKLAYER SL
//					                WHERE   SK.STACK_COL_GP = SL.STACK_COL_GP
//					                AND     SK.STACK_BED_GP = SL.STACK_BED_GP
//					                AND     SL.STACK_LAYER_STAT        = 'E'
//					                AND     SL.STACK_LAYER_ACTIVE_STAT = 'E'
//					                AND     SL.STOCK_ID IS NULL
//					                --적치 가능 매수 반드시                 
//					                AND     SK.ABLE_LOC_CNT >= nvl(:V_CRN_WRK_SH,2) 
//					                ) CUR,
//					                TB_YM_STACKLAYER SL1
//					        WHERE   CUR.STACK_COL_GP = SL1.STACK_COL_GP
//					        AND     CUR.STACK_BED_GP = SL1.STACK_BED_GP
//					        AND     CUR.PRE_LAYER_GP = SL1.STACK_LAYER_GP
//					        AND     SL1.STACK_LAYER_STAT   NOT IN ('U')
//					        AND     CUR.STACK_COL_GP||CUR.STACK_BED_GP NOT IN ( --작업예약 편성위치 제외
//					                                                          SELECT C.STACK_COL_GP || C.STACK_BED_GP
//					                                                            FROM TB_YM_WRKBOOK A 
//					                                                               , TB_YM_WRKBOOKMTL B
//					                                                               , TB_YM_STACKLAYER C
//					                                                           WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
//					                                                             AND B.STOCK_ID = C.STOCK_ID
//					                                                             AND A.DEL_YN = 'N'
//					                                                             AND B.DEL_YN = 'N' 
//					                                                             AND C.STACK_LAYER_STAT IN ('C','U')
//					                                                           )
//					       -- TO위치 가이드 제외
//					       AND CUR.STACK_COL_GP||CUR.STACK_BED_GP NOT IN (
//					                                              SELECT NVL(A1.YD_TO_LOC_GUIDE,'AAA')
//					                                                FROM TB_YM_WRKBOOK A1
//					                                                   , TB_YM_WRKBOOKMTL B1
//					                                               WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
//					                                                 AND A1.DEL_YN = 'N'
//					                                                 AND B1.DEL_YN = 'N'
//					                                                 AND A1.YD_GP  = '2'
//					                                                 AND LENGTH(A1.YD_TO_LOC_GUIDE) = 8
//					                                              UNION ALL
//					                                              SELECT NVL(B1.MTL_YD_TO_LOC_GUIDE,'AAA')
//					                                                FROM TB_YM_WRKBOOK A1
//					                                                   , TB_YM_WRKBOOKMTL B1
//					                                               WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
//					                                                 AND A1.DEL_YN = 'N'
//					                                                 AND B1.DEL_YN = 'N'
//					                                                 AND A1.YD_GP  = '2'
//					                                                 AND LENGTH(B1.MTL_YD_TO_LOC_GUIDE) = 8
//					                                              )                                                           
//					                                                           
//					        ORDER BY STACK_COL_GP, STACK_BED_GP, STACK_LAYER_GP
//					    )A
//					WHERE  ROWNUM = 1
// 					 */	
// 					JDTORecordSet outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocCol", logId, methodNm, "해당열 조회");
// 	 				if (outjsResult.size() <= 0) {
// 	 					szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
// 	 					commUtils.printLog(logId, szLogMsg, "SL");
// 	 					
//     					if(sAPP005_YN.equals("Y")) {
//    		    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
//    		    			jrLog.setField("STOCK_ID"		, "SLAB");
//    		    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
//    		    			jrLog.setField("YD_GP"			, "2");
//    		    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
//    		    			jrLog.setField("SCH_CONTENTS"	, "사용자지정(procToLocUser) 불가:"+ "\r\n" + "적재위치 가이드 10자리  열+ 베드+ 단  지정된 경우 ["+ydToLocGuide+"]의 베드 조회" + "\r\n" );
//    		    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
//    		    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
//     					}
// 	 					
//  	 					return YmConstant.RETN_CD_FAILURE;
// 	 				}
// 	 				
// 	 				JDTORecord jrSearchLoc = outjsResult.getRecord(0);
// 	 				if(jrSearchLoc != null) {
// 	 					String szStackColGp   = commUtils.trim(jrSearchLoc.getFieldString("STACK_COL_GP")) ;
// 	 					String szStackBedGp   = commUtils.trim(jrSearchLoc.getFieldString("STACK_BED_GP")) ;
// 	 					String szStackLayerGp = commUtils.trim(jrSearchLoc.getFieldString("STACK_LAYER_GP")) ;
// 	 					sRtnBedDan = szStackColGp+szStackBedGp+ szStackLayerGp ;
// 	 				}					
//				}	
			} else {
				szLogMsg =  " 적재위치 가이드 8자리  지정된 경우 ["+ydToLocGuide+"]의 베드 조회 시작";
				commUtils.printLog(logId, szLogMsg, "SL");
				
				commUtils.printLog(logId, szLogMsg + ydSchCd.substring(2, 4), "SL");

                	 
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocColBed 
				SELECT  A.STACK_COL_GP,
				        A.STACK_BED_GP,
				        A.STACK_LAYER_GP,
				        A.STACK_BED_ABLE_QNTY,
				        A.GRIP_TO,
				        A.TO_LOC 
				,        A.STOCK_ID
				FROM    (
						SELECT  CUR.STACK_COL_GP,    --적치 열 구분
						        CUR.STACK_BED_GP,    --적치 BED 구분
						        CUR.STACK_LAYER_GP,  --적치 단 구분
						         SL1.STOCK_ID,  --적치 단 구분
						
						        CUR.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
						        CUR.STACK_BED_ABLE_WT,      --적치 BED 가능 중량
						        CUR.STACK_BED_ABLE_HIGH,    --적치 BED 가능 높이
						        CUR.STACK_BED_ABLE_W,       --적치 BED 가능 폭
						        CUR.STACK_BED_ABLE_LEN,     --적치 BED 가능 길이
						                
						        NVL(CUR.STACK_COL_GP, '')    ||
						        NVL(CUR.STACK_BED_GP, '') AS GRIP_TO,
						        
						        NVL(CUR.STACK_COL_GP, '')    ||
						        NVL(CUR.STACK_BED_GP, '')    ||
						        NVL(CUR.STACK_LAYER_GP, '') AS TO_LOC
						FROM    (
						        SELECT  SK.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
						                SK.STACK_BED_ABLE_WT,      --적치 BED 가능 중량
						                SK.STACK_BED_ABLE_HIGH,    --적치 BED 가능 높이
						                SK.STACK_BED_ABLE_W,       --적치 BED 가능 폭
						                SK.STACK_BED_ABLE_LEN,     --적치 BED 가능 길이
						
						                SL.STACK_COL_GP,
						                SL.STACK_BED_GP,
						                SL.STACK_LAYER_GP,
						                DECODE(SL.STACK_LAYER_GP - 1,
						                    0, '01', 
						                    DECODE(SL.STACK_LAYER_GP - 1, 
						                        9, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1),
						                        DECODE(LENGTH(SL.STACK_LAYER_GP - 1),
						                            1, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1),
						                            TO_CHAR(SL.STACK_LAYER_GP - 1)))) AS PRE_LAYER_GP
				                             
						        FROM    (SELECT A.STACK_COL_GP,   		--적치 열 구분
				                                A.STACK_BED_GP,   		--적치 BED 구분
				                                A.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
				                                A.STACK_BED_ABLE_WT,      --적치 BED 가능 중량
				                                A.STACK_BED_ABLE_HIGH,    --적치 BED 가능 높이
				                                A.STACK_BED_ABLE_W,       --적치 BED 가능 폭
				                                A.STACK_BED_ABLE_LEN  	--적치 BED 가능 길이
				                              , (SELECT SUM(CASE WHEN substr(STACK_COL_GP,3,2) IN ( 'WB', 'CT' ) THEN 1
				                                                 WHEN STACK_LAYER_STAT = 'E' AND STACK_LAYER_ACTIVE_STAT = 'E' THEN 1
				                                                 ELSE 0 END)
				                                   FROM TB_YM_STACKLAYER 
				                                  WHERE STACK_COL_GP = A.STACK_COL_GP 
				                                    AND STACK_BED_GP = A.STACK_BED_GP) AS ABLE_LOC_CNT
				                           FROM TB_YM_STACKER A
				                          WHERE A.STACK_COL_GP            = :V_STACK_COL_GP
				                            AND A.STACK_BED_GP            = :V_STACK_BED_GP
				                            AND A.STACK_BED_ACTIVE_STAT   = 'L'
				                            AND A.STACK_BED_ABLE_QNTY     >  0	 --적치 BED 가능 수량
				                          ORDER BY A.STACK_BED_GP ASC
				                        ) SK,
						                TB_YM_STACKLAYER SL
						        WHERE   SK.STACK_COL_GP = SL.STACK_COL_GP
						        AND     SK.STACK_BED_GP = SL.STACK_BED_GP
				                -- 적치 상태 CHECK 안함 ('WB','CT')
						        AND     1 = CASE WHEN SUBSTR(SK.STACK_COL_GP,3,2) IN (SELECT NVL(DTL_ITM1,'BBBB')
				                                                                        FROM USRYMA.TB_YM_RULE 
				                                                                       WHERE REPR_CD_GP = 'YM103'
				                                                                         AND CD_GP  = '2'
				                                                                         AND DEL_YN = 'N')    THEN 1
				                                 WHEN SL.STACK_LAYER_STAT IN ('E') AND  SL.STACK_LAYER_ACTIVE_STAT = 'E' THEN 1
				                                 ELSE 2 END
				               --적치 가능 매수 반드시                 
				                AND     SK.ABLE_LOC_CNT >= nvl(:V_CRN_WRK_SH,2)               
				                
						        ) CUR,
						        TB_YM_STACKLAYER SL1
						WHERE   CUR.STACK_COL_GP = SL1.STACK_COL_GP
						AND     CUR.STACK_BED_GP = SL1.STACK_BED_GP
						AND     CUR.PRE_LAYER_GP = SL1.STACK_LAYER_GP
						AND     SL1.STACK_LAYER_STAT   NOT IN ('U')
				        AND    CUR.STACK_COL_GP||CUR.STACK_BED_GP NOT IN ( --작업예약 편성위치 제외
				                                                          SELECT C.STACK_COL_GP || C.STACK_BED_GP
				                                                            FROM TB_YM_WRKBOOK A 
				                                                               , TB_YM_WRKBOOKMTL B
				                                                               , TB_YM_STACKLAYER C
				                                                           WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
				                                                             AND B.STOCK_ID = C.STOCK_ID
				                                                             AND A.DEL_YN = 'N'
				                                                             AND B.DEL_YN = 'N' 
				                                                             AND C.STACK_LAYER_STAT IN ('C','U')
				                                                           GROUP BY C.STACK_COL_GP || C.STACK_BED_GP
				                                                         )
						ORDER BY STACK_COL_GP, STACK_BED_GP, STACK_LAYER_GP
				     )A
				WHERE  ROWNUM = 1                                 
     			*/ 
				jrTemp.setField("STACK_COL_GP"	, ydToLocGuide.substring(0, 6));
				jrTemp.setField("STACK_BED_GP"	, ydToLocGuide.substring(6));
					
 				JDTORecordSet outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocColBed", logId, methodNm, "해당열 베드 조회");
 				if (outjsResult.size() <= 0) {
 					szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
 					commUtils.printLog(logId, szLogMsg, "SL");
 					
 					if(sAPP005_YN.equals("Y")) {
		    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
		    			jrLog.setField("STOCK_ID"		, "SLAB");
		    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
		    			jrLog.setField("YD_GP"			, "2");
		    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
		    			jrLog.setField("SCH_CONTENTS"	, "사용자지정(procToLocUser) 불가:"+ "\r\n" + "적재위치 가이드 6~ 8자리  지정된 경우 ["+ydToLocGuide+"]의 베드 조회" + "\r\n" );
		    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
		    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
					}
 					
 					return YmConstant.RETN_CD_FAILURE;
 				}
 				JDTORecord jrSearchLoc = outjsResult.getRecord(0);
 				if(jrSearchLoc != null) {
 					String szStackColGp   = commUtils.trim(jrSearchLoc.getFieldString("STACK_COL_GP")) ;
 					String szStackBedGp   = commUtils.trim(jrSearchLoc.getFieldString("STACK_BED_GP")) ;
 					String szStackLayerGp = commUtils.trim(jrSearchLoc.getFieldString("STACK_LAYER_GP")) ;
 					sRtnBedDan = szStackColGp+szStackBedGp+ szStackLayerGp ;
 					
 					//=================================================================================================== 
 					if("2CTC11LM".equals(ydSchCd) || "2CTC22LM".equals(ydSchCd)) {
 	 					//C동 대차하차는 장입순번 순위가 맞아야한다.
 	 					JDTORecordSet chkResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getToLocChgSeqChk", logId, methodNm, "TO위치 장입순번 체크");
 	 					if(chkResult.size() > 0) {
 	 						
 	 						int iSchChargeLotNo = chkResult.getRecord(0).getFieldInt("SCH_CHARGE_LOT_NO");
 	 						int iLyrChargeLotNo = chkResult.getRecord(0).getFieldInt("LYR_CHARGE_LOT_NO");
 	 						
 	 						if(iSchChargeLotNo > iLyrChargeLotNo) {
 	 							
 	 	 						sRtnBedDan = "";
 	 	 						
 	 	 	 					szLogMsg = ">>>>> C동 대차하차시 TO위치가이드가 정해져 있어도 장입순번이 역순일 경우 TO위치를 XX010101 지정!! " ;
 	 	 	 					commUtils.printLog(logId, szLogMsg, "SL");
 	 						}
 	 					}
 					}
 					//=================================================================================================== 
 				}					
            }	 

			if(sRtnBedDan.length() < 10) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 TO위치 결정 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				if(sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, "SLAB");
	    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
	    			jrLog.setField("YD_GP"			, "2");
	    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
	    			jrLog.setField("SCH_CONTENTS"	, "크레인작업재료의  재료정보["+StockId+"]에 대한 TO위치 결정 실패" + "\r\n" );
	    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
				return YmConstant.RETN_CD_FAILURE;				

			} else {
				if(sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, "SLAB");
	    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
	    			jrLog.setField("YD_GP"			, "2");
	    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
	    			jrLog.setField("SCH_CONTENTS"	, "사용자지정(procToLocUser) 성공:"+ "\r\n" + "적재위치 가이드  지정된 경우 ["+ydToLocGuide+"]의 베드 조회" + "\r\n" );
	    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
			}

			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = JDTORecordFactory.getInstance().create();
			jrSetLoc.setField("YD_CRN_SCH_ID", 	ydCrnSchId); 
			jrSetLoc.setField("YD_EQP_ID", 		ydEqpId);	 
			jrSetLoc.setField("YD_SCH_CD", 		ydSchCd);	 
			jrSetLoc.setField("YD_UP_WO_LOC", 	ydUpWoLoc); 
			jrSetLoc.setField("YD_UP_WO_LAYER",	ydUpWoLayer);	 
			jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER", sRtnBedDan.substring(8,10));
			jrSetLoc.setField("YD_WBOOK_ID", 	ydWookId); 
				
			this.procUpdateLoc(logId,methodNm, jrSetLoc, jrCrnSch ,jrCrnMtlSch );
			//----------------------------------------------------------------------------------------------------------------------
			// ERROR 발생시 ?
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YmConstant.RETN_CD_SUCCESS;
	}      
  
	/**
	 * 설비(WB,CT) 작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procToLocWbCt(String logId, String methodNms,String sAPP005_YN, String planToLoc, JDTORecord jrWbook, JDTORecord jrCrnMtlSch, JDTORecord jrCrnSch,JDTORecordSet jsToLoc) throws JDTOException {
    	String methodNm = "TO 위치결정:WB,CT 장입작업[BSlabSchSeEJB.procToLocWbCt] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= JDTORecordFactory.getInstance().create();;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------

		String ydUpWoLoc 		= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String sCtsRelaySaddle 	= commUtils.trim(jrCrnMtlSch.getFieldString("CTS_RELAY_SADDLE"));
		String sSlabLotNo 		= commUtils.trim(jrCrnMtlSch.getFieldString("CHARGE_LOT_NO")); 
		String StockId 	    	= commUtils.trim(jrCrnMtlSch.getFieldString("STOCK_ID"));
		
		String ydSchCd 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String ydWookId			= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));		//작업예약

		String ydCrnSchId 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String ydEqpId     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String ydWrkSh 		    = commUtils.trim(jrCrnMtlSch.getFieldString("CRN_WRK_SH")); //권상매수
		String sRtnBedDan       = "";
		String sWbCtYn          = "";
		String szDBLogMsg   	= "";
		
		commUtils.printLog(logId, methodNm, "S+");
		try {
			
			String currLotNo = "";
			String wbLotNo   = "";
		    	
    		if ("WB".equals(planToLoc.substring(2, 4))) {
					commUtils.printLog(logId, "현 대상재 장입번호를 보급할 수 있는지 체크 SKIP", "SL");
					szDBLogMsg = "현 대상재 장입번호를 WB에 보급할 수 있는지 체크  위치 :"+ planToLoc + "\r\n";

				if("LOT".equals(sCtsRelaySaddle)){
					sWbCtYn = "1";
				} else {
				
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocWB 
					--현재 W/B보급되어야 할 장입LOT 번호를 가져온다.
					SELECT ( SELECT NVL(MAX(CHARGE_LOT_NO),'') AS  CURR_LOT_NO
					          FROM (
					                SELECT CHARGE_LOT_NO
					                  FROM (
					                        SELECT TO_NUMBER(NVL(A.CHARGE_LOT_NO,99999999)) AS  CHARGE_LOT_NO
					                             , A.STOCK_ID
					                          FROM TB_YM_STOCK A
					                             , TB_YM_STACKLAYER B
					                         WHERE A.STOCK_ID = B.STOCK_ID
					                           AND B.STACK_COL_GP NOT LIKE '2A%'
					                           AND B.STACK_COL_GP NOT LIKE '2B%'
					                           AND B.STACK_LAYER_STAT  IN ('C','U')
					                           AND A.CHARGE_LOT_NO IS NOT NULL
					                           -- 기편성되어 있는거 제외
					                           AND A.STOCK_ID NOT IN ( SELECT NVL(BB.STOCK_ID,'KKK')
					                                                     FROM TB_YM_CRNSCH AA
					                                                        , TB_YM_CRNWRKMTL BB
					                                                    WHERE AA.YD_CRN_SCH_ID = BB.YD_CRN_SCH_ID
					                                                      AND AA.DEL_YN = 'N'
					                                                      AND BB.DEL_YN = 'N'
					                                                      AND AA.YD_DN_WO_LOC = '2CWB0101')
					                        ORDER BY TO_NUMBER(NVL(A.CHARGE_LOT_NO,99999999))
					                       ) A
					                 WHERE ROWNUM <= :V_CRN_WRK_SH
					                   AND ROWNUM <= 2   --2매이상 처리 안됨
					               )
					       ) AS CURR_LOT_NO
					     , (SELECT CTS_RELAY_SADDLE 
						      FROM TB_YM_STOCK 
							 WHERE STOCK_ID IN ( 
					                            SELECT STOCK_ID 
					                              FROM (
					--                                     SELECT NVL(STOCK_ID,'KKK') AS STOCK_ID
					--                                       FROM TB_YM_STACKLAYER
					--                                      WHERE STACK_COL_GP = :V_STACK_COL_GP
					--                                        AND STACK_BED_GP = '01'
					--                                        AND STOCK_ID IS NOT NULL
					--                                        AND STACK_LAYER_STAT = 'C'
					--                                      ORDER BY STACK_LAYER_GP DESC  
					                                        SELECT NVL(STL_NO,'KKK') AS STOCK_ID
					                                          FROM USRYMA.TB_YM_EQPTRACKING
					                                         WHERE STACK_COL_GP = :V_STACK_COL_GP
					                                           AND STACK_BED_GP = '01'
					                                           AND STL_NO IS NOT NULL
					                                         ORDER BY EQUIP_GP
					                                   ) 
					                             WHERE ROWNUM <=1 
					                           )
					       ) AS WB_LOT_NO                 
					  FROM DUAL 
					 */	  
					jrTemp = JDTORecordFactory.getInstance().create();
					jrTemp.setResultCode(logId);	//Log ID
					jrTemp.setResultMsg(methodNm);	//Log Method Name
					jrTemp.setField("YD_EQP_ID"		, ydEqpId);	
					jrTemp.setField("YD_SCH_CD"		, ydSchCd);	
					jrTemp.setField("YD_WBOOK_ID"	, ydWookId);	
					jrTemp.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
					jrTemp.setField("STACK_COL_GP"	, planToLoc);											
					jrTemp.setField("CRN_WRK_SH"	, ydWrkSh);											

					JDTORecordSet outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocWB", logId, methodNm, "WB_TOSQL 베드 조회");
	 				if (outjsResult.size() <= 0) {
	 					szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
	 					commUtils.printLog(logId, szLogMsg, "SL");
	 					return YmConstant.RETN_CD_FAILURE;
	 				} else {
	 					currLotNo 	= commUtils.trim(outjsResult.getRecord(0).getFieldString("CURR_LOT_NO")); //현재      LOT번호
	 					wbLotNo 	= commUtils.trim(outjsResult.getRecord(0).getFieldString("WB_LOT_NO"));   //장입대위 LOT번호
	 				}
				}
    		} else if("CT".equals(planToLoc.substring(2, 4))) {
				szDBLogMsg = "현 대상재 장입번호를  CT에 보급할 수 있는지 체크  위치 :"+ planToLoc + "\r\n";

				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocCT 
				--현재 CT 보급되어야 할 장입LOT 번호를 가져온다.
				SELECT ( SELECT  ZOIN.CHARGE_LOT_NO
				           FROM (
				                  SELECT MIN(TO_NUMBER(NVL(A.CHARGE_LOT_NO,99999999))) AS CHARGE_LOT_NO
				                    FROM TB_YM_STOCK A,
				                         TB_YM_STACKLAYER B
				                   WHERE A.CHARGE_LOT_NO IS NOT NULL
				                     AND A.STOCK_ID = B.STOCK_ID
				                     AND B.STACK_COL_GP LIKE SUBSTR(:V_STACK_COL_GP,1,2)||'%'
				                    ) ZOIN
				            WHERE   ROWNUM = 1
				       ) AS CURR_LOT_NO
				     , (SELECT CTS_RELAY_SADDLE 
					      FROM TB_YM_STOCK 
						 WHERE STOCK_ID IN ( 
				                            SELECT STOCK_ID 
				                              FROM (
				                                       SELECT NVL(STOCK_ID,'KKK') AS STOCK_ID
				                                          FROM USRYMA.TB_YM_STACKLAYER
				                                         WHERE STACK_COL_GP   = :V_STACK_COL_GP
				                                           AND STACK_BED_GP   = '01'
				                                           AND STOCK_ID IS NOT NULL
				                                         ORDER BY STACK_LAYER_GP 
				                                   ) 
				                             WHERE ROWNUM <=1 
				                           )
				       ) AS WB_LOT_NO                 
				  FROM DUAL 
    			 */ 
				jrTemp = JDTORecordFactory.getInstance().create();
				jrTemp.setResultCode(logId);	//Log ID
				jrTemp.setResultMsg(methodNm);	//Log Method Name
				jrTemp.setField("YD_EQP_ID"		, ydEqpId);	
				jrTemp.setField("YD_SCH_CD"		, ydSchCd);	
				jrTemp.setField("YD_WBOOK_ID"	, ydWookId);	
				jrTemp.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
				jrTemp.setField("STACK_COL_GP"	, planToLoc);											

				JDTORecordSet outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocCT", logId, methodNm, "CT_TOSQL 베드 조회");
 				if (outjsResult.size() <= 0) {
 					szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
 					commUtils.printLog(logId, szLogMsg, "SL");
 					return YmConstant.RETN_CD_FAILURE;
 				} else {
 					currLotNo 	= commUtils.trim(outjsResult.getRecord(0).getFieldString("CURR_LOT_NO")); //현재      LOT번호
 					wbLotNo 	= commUtils.trim(outjsResult.getRecord(0).getFieldString("WB_LOT_NO"));   //장입대위 LOT번호
 				}
    		}
	    		
			/*
			 * sSlabLotNo = 현재 저장품 장입번호 sCurLotNo = 현재 보급해야할 장입번호 sWbLotNo = 현재 WB
			 * 01번지에 있는 저장품의 장입번호
			 * 
			 * 3. 현재 작업할 SLAB 의 장입LOT순번과 CTC,W/B에 있는 또는 없으면 현재 보급할 장입LOT순번을 비교한다. 즉,
			 * 같으면 TO위치 적치 가능하다.
			 */
    		commUtils.printLog(logId, "재료의 장입번호 =>"+sSlabLotNo, "SL");
    		commUtils.printLog(logId, "설비의 장입번호 =>"+wbLotNo, "SL");
    		commUtils.printLog(logId, "보급할 장입번호 =>"+currLotNo, "SL");
    		
    		szDBLogMsg = szDBLogMsg + "재료의 장입번호 =>"+sSlabLotNo + "\r\n";
    		szDBLogMsg = szDBLogMsg + "설비의 장입번호 =>"+wbLotNo    + "\r\n";
    		szDBLogMsg = szDBLogMsg + "보급할 장입번호 =>"+currLotNo  + "\r\n";

    		
			if (sSlabLotNo.equals(currLotNo)) {

				if ("".equals(wbLotNo)) {
					sWbCtYn = "1";
					commUtils.printLog(logId, "재료의 장입번호 =>"+sSlabLotNo+ "보급할 장입번호 =>"+currLotNo + "CHARGE_LOT_NO =>장입대상=> WB 01번지 01단에 적치가능.", "SL");
					szDBLogMsg = szDBLogMsg + "재료의 장입번호 =>"+sSlabLotNo+ "보급할 장입번호 =>"+currLotNo + "CHARGE_LOT_NO =>장입대상=> WB 01번지 01단에 적치가능." + "\r\n";
				} else {
					if (sSlabLotNo.equals(wbLotNo)) {
						sWbCtYn = "1";
						commUtils.printLog(logId, "재료의 장입번호 =>"+sSlabLotNo+ "설비의 장입번호 =>"+wbLotNo + "CHARGE_LOT_NO =>장입대상=> WB 01번지 01단에 적치가능.", "SL");
						szDBLogMsg = szDBLogMsg + "재료의 장입번호 =>"+sSlabLotNo+ "설비의 장입번호 =>"+wbLotNo + "CHARGE_LOT_NO =>장입대상=> WB 01번지 01단에 적치가능." + "\r\n";
					} else {
						sWbCtYn = "2";
//기존은 로직 대기 상태로
						sWbCtYn = "1";
						commUtils.printLog(logId, "가능:CHARGE_LOT_NO =>장입대상=> WB 01번지 장입LOT 번호와 다릅니다..", "SL");
						szDBLogMsg = szDBLogMsg + "가능:CHARGE_LOT_NO =>장입대상=> WB 01번지 장입LOT 번호와 다릅니다.." + "\r\n";
					}
				}
			} else {
				sWbCtYn = "3";
				commUtils.printLog(logId, "불가:CHARGE_LOT_NO =>보급할 장입번호 대상 아님=> 장입LOT 번호가 다릅니다.", "SL");
				szDBLogMsg = szDBLogMsg + "불가:CHARGE_LOT_NO =>보급할 장입번호 대상 아님=> 장입LOT 번호가 다릅니다." + "\r\n";
			}
		
			
			if(sWbCtYn.equals("1")) {
				// 적치 가능 여부 검토
				jrTemp = JDTORecordFactory.getInstance().create();
				jrTemp.setResultCode(logId);	//Log ID
				jrTemp.setResultMsg(methodNm);	//Log Method Name
				jrTemp.setField("STACK_COL_GP"			, planToLoc);
				jrTemp.setField("STACK_BED_GP"			, "01");
				jrTemp.setField("EQUIP_GP"				, planToLoc);
				jrTemp.setField("CRN_WRK_SH"			, ydWrkSh);	
				
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocColBed 
				SELECT  A.STACK_COL_GP,
				        A.STACK_BED_GP,
				        A.STACK_LAYER_GP,
				        A.STACK_BED_ABLE_QNTY,
				        A.GRIP_TO,
				        A.TO_LOC 
				,        A.STOCK_ID
				FROM    (
						SELECT  CUR.STACK_COL_GP,    --적치 열 구분
						        CUR.STACK_BED_GP,    --적치 BED 구분
						        CUR.STACK_LAYER_GP,  --적치 단 구분
						         SL1.STOCK_ID,  --적치 단 구분
						
						        CUR.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
						        CUR.STACK_BED_ABLE_WT,      --적치 BED 가능 중량
						        CUR.STACK_BED_ABLE_HIGH,    --적치 BED 가능 높이
						        CUR.STACK_BED_ABLE_W,       --적치 BED 가능 폭
						        CUR.STACK_BED_ABLE_LEN,     --적치 BED 가능 길이
						                
						        NVL(CUR.STACK_COL_GP, '')    ||
						        NVL(CUR.STACK_BED_GP, '') AS GRIP_TO,
						        
						        NVL(CUR.STACK_COL_GP, '')    ||
						        NVL(CUR.STACK_BED_GP, '')    ||
						        NVL(CUR.STACK_LAYER_GP, '') AS TO_LOC
						FROM    (
						        SELECT  SK.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
						                SK.STACK_BED_ABLE_WT,      --적치 BED 가능 중량
						                SK.STACK_BED_ABLE_HIGH,    --적치 BED 가능 높이
						                SK.STACK_BED_ABLE_W,       --적치 BED 가능 폭
						                SK.STACK_BED_ABLE_LEN,     --적치 BED 가능 길이
						
						                SL.STACK_COL_GP,
						                SL.STACK_BED_GP,
						                SL.STACK_LAYER_GP,
						                DECODE(SL.STACK_LAYER_GP - 1,
						                    0, '01', 
						                    DECODE(SL.STACK_LAYER_GP - 1, 
						                        9, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1),
						                        DECODE(LENGTH(SL.STACK_LAYER_GP - 1),
						                            1, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1),
						                            TO_CHAR(SL.STACK_LAYER_GP - 1)))) AS PRE_LAYER_GP
				                             
						        FROM    (SELECT A.STACK_COL_GP,   		--적치 열 구분
				                                A.STACK_BED_GP,   		--적치 BED 구분
				                                A.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
				                                A.STACK_BED_ABLE_WT,      --적치 BED 가능 중량
				                                A.STACK_BED_ABLE_HIGH,    --적치 BED 가능 높이
				                                A.STACK_BED_ABLE_W,       --적치 BED 가능 폭
				                                A.STACK_BED_ABLE_LEN  	--적치 BED 가능 길이
				                              , (SELECT SUM(CASE WHEN substr(STACK_COL_GP,3,2) IN ( 'WB', 'CT' ) THEN 1
				                                                 WHEN STACK_LAYER_STAT = 'E' AND STACK_LAYER_ACTIVE_STAT = 'E' THEN 1
				                                                 ELSE 0 END)
				                                   FROM TB_YM_STACKLAYER 
				                                  WHERE STACK_COL_GP = A.STACK_COL_GP 
				                                    AND STACK_BED_GP = A.STACK_BED_GP) AS ABLE_LOC_CNT
				                           FROM TB_YM_STACKER A
				                          WHERE A.STACK_COL_GP            = :V_STACK_COL_GP
				                            AND A.STACK_BED_GP            = :V_STACK_BED_GP
				                            AND A.STACK_BED_ACTIVE_STAT   = 'L'
				                            AND A.STACK_BED_ABLE_QNTY     >  0	 --적치 BED 가능 수량
				                          ORDER BY A.STACK_BED_GP ASC
				                        ) SK,
						                TB_YM_STACKLAYER SL
						        WHERE   SK.STACK_COL_GP = SL.STACK_COL_GP
						        AND     SK.STACK_BED_GP = SL.STACK_BED_GP
				                -- 적치 상태 CHECK 안함 ('WB','CT')
						        AND     1 = CASE WHEN SUBSTR(SK.STACK_COL_GP,3,2) IN (SELECT NVL(DTL_ITM1,'BBBB')
				                                                                        FROM USRYMA.TB_YM_RULE 
				                                                                       WHERE REPR_CD_GP = 'YM103'
				                                                                         AND CD_GP  = '2'
				                                                                         AND DEL_YN = 'N')    THEN 1
				                                 WHEN SL.STACK_LAYER_STAT IN ('E') AND  SL.STACK_LAYER_ACTIVE_STAT = 'E' THEN 1
				                                 ELSE 2 END
				               --적치 가능 매수 반드시                 
				                AND     SK.ABLE_LOC_CNT >= nvl(:V_CRN_WRK_SH,2)               
				                
						        ) CUR,
						        TB_YM_STACKLAYER SL1
						WHERE   CUR.STACK_COL_GP = SL1.STACK_COL_GP
						AND     CUR.STACK_BED_GP = SL1.STACK_BED_GP
						AND     CUR.PRE_LAYER_GP = SL1.STACK_LAYER_GP
						AND     SL1.STACK_LAYER_STAT   NOT IN ('U')
				        AND    CUR.STACK_COL_GP||CUR.STACK_BED_GP NOT IN ( --작업예약 편성위치 제외
				                                                          SELECT C.STACK_COL_GP || C.STACK_BED_GP
				                                                            FROM TB_YM_WRKBOOK A 
				                                                               , TB_YM_WRKBOOKMTL B
				                                                               , TB_YM_STACKLAYER C
				                                                           WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
				                                                             AND B.STOCK_ID = C.STOCK_ID
				                                                             AND A.DEL_YN = 'N'
				                                                             AND B.DEL_YN = 'N' 
				                                                             AND C.STACK_LAYER_STAT IN ('C','U')
				                                                           GROUP BY C.STACK_COL_GP || C.STACK_BED_GP
				                                                         )
						ORDER BY STACK_COL_GP, STACK_BED_GP, STACK_LAYER_GP
				     )A
				WHERE  ROWNUM = 1                                        
				*/
 				JDTORecordSet jsBed = commDao.select(jrTemp, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocColBed", logId, methodNm, "해당열 베드 조회");
 				if (jsBed.size() <= 0) {
 					szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
 					commUtils.printLog(logId, szLogMsg, "SL");
 					return YmConstant.RETN_CD_FAILURE;
 				}
 				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectEquipInfo
 				--설비테이블의 정보를 리턴한다.
 				SELECT	EQUIP_GP,		--설비 구분
 				        YD_GP,			--YARD 구분
 				        BAY_GP,			--동 구분
 				        EQUIP_KIND,		--설비 종류
 				        EQUIP_NO,		--설비 번호
 				        EQUIP_NAME,		--설비 명
 				        EQUIP_ABB_NAME,	--설비 약어 명
 				        PALLET_NO,		--PALLET 번호
 				        EQUIP_STAT,		--설비 상태
 				        DECODE(EQUIP_STAT, 
 				           'C','고장','정상') AS EQUIP_STAT1,
 				        DOWN_CD,		--휴지 CODE
 				        STACK_MAX_QNTY,		--적재 최대 수량
 				        STACK_MAX_WT,		--적재 최대 중량
 				        STACK_STAT,		--적재 상태
 				        WPROG_STAT,     --작업진행 상태
 				        DECODE(WPROG_STAT,'B','고장','N','정상','R','복구','W','대기','1','권상작업지시','2','권상중','3','권하작업지시','4','권하완료') AS WPROG_STAT1,		
 				        WBOOK_ID,		--작업예약 ID
 				        WAIT_STOP_LOC,		--대기 정지 위치
 				        CURR_STOP_LOC,		--현재 정지 위치
 				        CARLOAD_STOP_LOC,		--상차 정지 위치
 				        CARUNLOAD_STOP_LOC,		--하차 정지 위치
 				        CARLOAD_ASSIGN_YN,		--상차 지정 구분
 				        CARLOAD_SCH_WORK_KIND,		--상차 SCHEDULE 작업 종류
 				        CARUNLOAD_ASSIGN_YN,		--하차 지정 구분
 				        CARUNLOAD_SCH_WORK_KIND,		--하차 SCHEDULE 작업 종류
 				        WORK_MODE,		--작업 MODE
 				        HMI_STAT,		--HMI 상태
 				        DECODE(HMI_STAT,
 				          'C','스케쥴금지','스케쥴사용') AS HMI_STAT1,
 				        BACKUP_EQUIP_YN,		--BACKUP 설비 유무
 				        BACKUP_EQUIP_KIND,		--BACKUP 설비 종류
 				        BACKUP_EQUIP_NO,		--BACKUP 설비 번호
 				        CTS_RELAY_YN,		--CTS 중계 구역 사용 유무
 				        CTS_RELAY_BAY,		--CTS 중계 구역 동
 				        REGISTER,		--등록자
 				        REG_DDTT,		--등록 일시
 				        MODIFIER,		--수정자
 				        MOD_DDTT,		--수정 일시
 				        DEL_YN		--삭제 유무
 				FROM	TB_YM_EQUIP
 				WHERE	EQUIP_GP = :V_EQUIP_GP 
 				*/
 				JDTORecordSet jsEquipOnOff = commDao.select(jrTemp, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectEquipInfo", logId, methodNm, "설비상태여부 조회");
 				if (jsEquipOnOff.size() <= 0) {
 					szLogMsg = methodNm+ "설비정보 READ 실패 ";
 					commUtils.printLog(logId, szLogMsg, "SL");
 					return YmConstant.RETN_CD_FAILURE;
 				}
 				//if("C".equals(commUtils.trim(jsEquipOnOff.getRecord(0).getFieldString("HMI_STAT"))) ||   //스케줄사용금지(C)
 				//   "B".equals(commUtils.trim(jsEquipOnOff.getRecord(0).getFieldString("WPROG_STAT")))){  //고장(B)
 				//	szDBLogMsg = szDBLogMsg + "불가:설비 고장 또는 스케줄사용금지 임 :" + planToLoc + "\r\n";
 				//	if(sAPP005_YN.equals("Y")) {
 		    	//		JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
 		    	//		jrLog.setField("STOCK_ID"		, "SLAB");
 		    	//		jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
 		    	//		jrLog.setField("YD_GP"			, "2");
 		    	//		jrLog.setField("YD_SCH_CD"		, ydSchCd);
 		    	//		jrLog.setField("SCH_CONTENTS"	, "보급 실패" + "\r\n"+ szDBLogMsg );
 		    	//		EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
 		    	//		SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
 				//	}
 				//	return YmConstant.RETN_CD_FAILURE;
 				//}
 				// 대상선택
 				sRtnBedDan = planToLoc + "01" + "01";
			}

			if(sRtnBedDan.length() < 10) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 TO위치 결정 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				
				if(sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, "SLAB");
	    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
	    			jrLog.setField("YD_GP"			, "2");
	    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
	    			jrLog.setField("SCH_CONTENTS"	, "보급 실패"+ "\r\n" + szDBLogMsg );
	    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}

				return YmConstant.RETN_CD_FAILURE;				
			}
			if(sAPP005_YN.equals("Y")) {
    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
    			jrLog.setField("STOCK_ID"		, "SLAB");
    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
    			jrLog.setField("YD_GP"			, "2");
    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
    			jrLog.setField("SCH_CONTENTS"	, "보급 성공" + "\r\n"+ szDBLogMsg );
    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
			}
				
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return sRtnBedDan;
	}      
	/**
	 * 설비(보온카바) 작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procToLocBk(String logId, String methodNms,String sAPP005_YN, String planToLoc, JDTORecord jrWbook, JDTORecord jrCrnMtlSch, JDTORecord jrCrnSch, JDTORecordSet jsToLoc) throws JDTOException {
    	String methodNm = "TO 위치결정:보온카바 [BSlabSchSeEJB.procToLocBk] < " + methodNms;
    	String szLogMsg					= null;
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------

		String ydSchCd 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String StockId 	    	= commUtils.trim(jrCrnMtlSch.getFieldString("STOCK_ID"));
		String ydCrnSchId 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String ydEqpId     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String ydWrkSh 			= commUtils.trim(jrCrnMtlSch.getFieldString("CRN_WRK_SH")); //권상매수
		String sRtnBedDan       = "";
		String szDBLogMsg   	= "";
		
		JDTORecordSet jsSearchLoc = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord jrSearchLoc    = JDTORecordFactory.getInstance().create();
		commUtils.printLog(logId, methodNm, "S+");
		try {
			
			JDTORecord jrInParam = JDTORecordFactory.getInstance().create();
			jrInParam.setResultCode(logId);	//Log ID
			jrInParam.setResultMsg(methodNm);	//Log Method Name
			jrInParam.setField("STOCK_ID"       , StockId);													//권상 STOCK
			jrInParam.setField("YD_SCH_CD"		, ydSchCd);		//스케줄 코드
			jrInParam.setField("YD_EQP_ID"		, ydEqpId);		//설비ID
			jrInParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);	//크레인 스케쥴 ID
			jrInParam.setField("STACK_COL_GP"	, planToLoc);	//위치
			jrInParam.setField("CRN_WRK_SH"		, ydWrkSh);	
			
			szDBLogMsg = szDBLogMsg + "보온카바 TO위치 검색 =>위치:" + planToLoc +"\r\n";
			
			
			if ("BK".equals(planToLoc.substring(2, 4))) { // B열연 A동 보온카바

				commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶보온카바 TO위치 검색 => 01단 이상 동일장입순번검색 ◀◀◀◀◀◀◀◀◀◀", "SL");
				
				jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocG", logId, methodNm, "장입LOT 순위가 동일한 TO 위치 검색");
				if(jsSearchLoc.size() > 0) {
					jsSearchLoc.absolute(1);
					jrSearchLoc  = jsSearchLoc.getRecord();
					szDBLogMsg = szDBLogMsg + "보온카바 TO위치 검색 => 01단 이상 동일장입순번검색 성공." + "\r\n";
				} else {
					szDBLogMsg = szDBLogMsg + "보온카바 TO위치 검색 => 01단 이상 동일장입순번검색 없음." + "\r\n";
					
					commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶보온카바 TO위치 검색 => 산적LOT코드로 검색◀◀◀◀◀◀◀◀◀◀", "SL");
					jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocS", logId, methodNm, "산적LOT가 동일한 TO 위치 검색");
					if(jsSearchLoc.size() > 0) {
						jsSearchLoc.absolute(1);
						jrSearchLoc  = jsSearchLoc.getRecord();
						szDBLogMsg = szDBLogMsg + "보온카바 TO위치 검색 => 산적LOT코드로 검색 성공." + "\r\n";
					} else {
						szDBLogMsg = szDBLogMsg + "보온카바 TO위치 검색 => 산적LOT코드로 검색 없음." + "\r\n";
						
						commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶보온카바 TO위치 검색 => 공 bed 로 검색◀◀◀◀◀◀◀◀◀◀", "SL");
						jsSearchLoc = commDao.select(jrInParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocE", logId, methodNm, "공BED  검색");
						if(jsSearchLoc.size() > 0) {
							jsSearchLoc.absolute(1);
							jrSearchLoc  = jsSearchLoc.getRecord();
							szDBLogMsg = szDBLogMsg + "보온카바 TO위치 검색 => 공 bed 로 검색 성공.." + "\r\n";
						} else {
							szDBLogMsg = szDBLogMsg + "보온카바 TO위치 검색 => 공 bed 로 검색 없음." + "\r\n";
							
							commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶보온카바 TO위치 검색 => 폭,길이 참조 검색   ◀◀◀◀◀◀◀◀◀◀", "SL");
							jrSearchLoc = this.procOtherBk(logId, methodNms,jrWbook,jrCrnMtlSch,jrCrnSch, jsToLoc );
							if(jrSearchLoc != null) {
								
								szDBLogMsg = szDBLogMsg + "TO 위치결정:중량,폭,길이,두께항목을 가지고 적치가능한 곳을 검색 성공" + "\r\n";
							}
						}
					}	
				}
			}	

			if(jrSearchLoc != null) {
				String szStackColGp   	= commUtils.trim(jrSearchLoc.getFieldString("STACK_COL_GP")) ;
				String szStackBedGp   	= commUtils.trim(jrSearchLoc.getFieldString("STACK_BED_GP")) ;
				String szStackLayerGp 	= commUtils.trim(jrSearchLoc.getFieldString("STACK_LAYER_GP")) ;
				sRtnBedDan 	= szStackColGp + szStackBedGp + szStackLayerGp ;
				
				if(sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, "SLAB");
	    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
	    			jrLog.setField("YD_GP"			, "2");
	    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
	    			jrLog.setField("SCH_CONTENTS"	, "보온카바 TO 위치결정 성공 위치:"+ sRtnBedDan + "\r\n" +szDBLogMsg+ "\r\n" );
	    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
				
				
			} else {
				szDBLogMsg = szDBLogMsg + "TO 위치결정:중량,폭,길이,두께항목을 가지고 적치가능한 곳을 검색없음" + "\r\n";
				if(sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, "SLAB");
	    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
	    			jrLog.setField("YD_GP"			, "2");
	    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
	    			jrLog.setField("SCH_CONTENTS"	, "보온카바 TO 위치결정 실패 :"+ "\r\n"+ szDBLogMsg + "\r\n" );
	    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
				
			}

			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return sRtnBedDan;
	}      
	/**
	 * 차량 및 대차 작업 TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procToLocPtTc(String logId, String methodNms,String sAPP005_YN,String planToLoc, JDTORecord jrWbook, JDTORecord jrCrnMtlSch, JDTORecord jrCrnSch,JDTORecordSet jsToLoc) throws JDTOException {
    	String methodNm = "TO 위치결정:차량 및 대차 [BSlabSchSeEJB.procToLocPtTc] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= JDTORecordFactory.getInstance().create();
		String szDBLogMsg					= null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------

		String StockId 	    	= commUtils.trim(jrCrnMtlSch.getFieldString("STOCK_ID"));
		String ydWrkSh 			= commUtils.trim(jrCrnMtlSch.getFieldString("CRN_WRK_SH")); //권상매수
		String ydWrkPlanTcar    = commUtils.trim(jrWbook.getFieldString("YD_WRK_PLAN_TCAR"));
		String ydSchCd 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String ydWookId			= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));		//작업예약

		String ydCrnSchId 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String ydEqpId     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
	
		String sRtnBedDan       = "";
		JDTORecord jrSearchLoc    = JDTORecordFactory.getInstance().create();
		JDTORecordSet outjsResult = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet outjsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
		
		
		commUtils.printLog(logId, methodNm, "S+");
		try {
			
			if ("TC".equals(planToLoc.substring(2, 4))){
				szDBLogMsg = szDBLogMsg + "대차인 경우 검색" + "\r\n";
				jrTemp = JDTORecordFactory.getInstance().create();
				jrTemp.setResultCode(logId);	//Log ID
				jrTemp.setResultMsg(methodNm);	//Log Method Name
				jrTemp.setField("YD_EQP_ID"			, ydEqpId);	
				jrTemp.setField("YD_SCH_CD"			, ydSchCd);	
				jrTemp.setField("YD_WBOOK_ID"		, ydWookId);	
				jrTemp.setField("YD_CRN_SCH_ID"		, ydCrnSchId);	
				jrTemp.setField("STACK_COL_GP"		, planToLoc);	
				jrTemp.setField("YD_WRK_PLAN_TCAR"	, ydWrkPlanTcar);	
				jrTemp.setField("CRN_WRK_SH"		, ydWrkSh);	
				
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocColTcLog 
				SELECT
				       (SELECT YD_CARLD_STOP_LOC
				          FROM USRYMA.TB_YM_TCARSCH  
				         WHERE DEL_YN = 'N'
				           AND YD_EQP_ID = B.YD_WRK_PLAN_TCAR ) AS CARLOAD_STOP_LOC  --상차위치
				     , CASE WHEN PALLET_NO IN ( 1,3 ) AND SUBSTR(CARLD_SCH_CD,3,6) = SUBSTR(:V_YD_SCH_CD,3,6) THEN 'Y'
				            ELSE 'N' END                        AS PRE_WRK_YN  --선작업 여부
				     , B.YD_WRK_PLAN_TCAR                       AS YD_WRK_PLAN_TCAR        
				  FROM TB_YM_EQUIP A 
				--     , (SELECT '2XTC0'||SUBSTR(:V_STACK_COL_GP,5,1) AS YD_WRK_PLAN_TCAR
				--          FROM TB_YM_WRKBOOK WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID) B
				       , (SELECT CASE WHEN  -- 스케쥴이 대차상차이고  재료가 대차위에 있는 경우 
				                         (SELECT MAX(SUBSTR(STACK_COL_GP,3,2))
				                            FROM TB_YM_WRKBOOKMTL 
				                           WHERE YD_WBOOK_ID =  A.YD_WBOOK_ID) = 'TC' 
				                     AND SUBSTR(A.YD_SCH_CD,3,6) = 'TC11UM'
				                     AND (SELECT COUNT(*)
				                            FROM TB_YM_EQUIP C
				                           WHERE EQUIP_GP LIKE '2XTC0%'
				                             AND EQUIP_GP <> A.YD_WRK_PLAN_TCAR
				                             AND SUBSTR(CARLOAD_STOP_LOC,1,2) ||SUBSTR(CARLD_SCH_CD,3,6) = A.YD_SCH_CD
				                             )  > 0
				                    THEN (SELECT EQUIP_GP
				                            FROM TB_YM_EQUIP C
				                           WHERE EQUIP_GP LIKE '2XTC0%'
				                             AND EQUIP_GP <> A.YD_WRK_PLAN_TCAR
				                             AND SUBSTR(CARLOAD_STOP_LOC,1,2) ||SUBSTR(CARLD_SCH_CD,3,6) = A.YD_SCH_CD
				                          )        
				                           
				                    ELSE NVL(YD_WRK_PLAN_TCAR, '2XTC0'||SUBSTR(:V_STACK_COL_GP,5,1)) END AS YD_WRK_PLAN_TCAR
				          FROM TB_YM_WRKBOOK A
				         WHERE A.YD_WBOOK_ID = :V_YD_WBOOK_ID) B

				 WHERE A.EQUIP_GP = B.YD_WRK_PLAN_TCAR
				 */
				outjsResult1 = commDao.select(jrTemp, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocColTcLog", logId, methodNm, "해당열 조회log");
				if(outjsResult1.size() > 0 ) {
					
					String carLoadStopLoc = commUtils.trim(outjsResult1.getRecord(0).getFieldString("CARLOAD_STOP_LOC"));
					szLogMsg  =  "★★★  대차스케쥴  상차위치:" + carLoadStopLoc;
					commUtils.printLog(logId, szLogMsg, "SL");
				}
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocColTc 
				WITH SEARCH_TBL AS (
				SELECT
				       (SELECT YD_CARLD_STOP_LOC
				          FROM USRYMA.TB_YM_TCARSCH  
				         WHERE DEL_YN = 'N'
				           AND YD_EQP_ID = B.YD_WRK_PLAN_TCAR ) AS CARLOAD_STOP_LOC  --상차위치
				     , CASE WHEN PALLET_NO IN ( 1,3 ) AND SUBSTR(CARLD_SCH_CD,3,6) = SUBSTR(:V_YD_SCH_CD,3,6) THEN 'Y'
				            ELSE 'N' END                        AS PRE_WRK_YN  --선작업 여부
				     , B.YD_WRK_PLAN_TCAR                       AS YD_WRK_PLAN_TCAR        
				  FROM TB_YM_EQUIP A 
				--     , (SELECT '2XTC0'||SUBSTR(:V_STACK_COL_GP,5,1) AS YD_WRK_PLAN_TCAR
				--          FROM TB_YM_WRKBOOK WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID) B
				       , (SELECT CASE WHEN  -- 스케쥴이 대차상차이고  재료가 대차위에 있는 경우 
				                         (SELECT MAX(SUBSTR(STACK_COL_GP,3,2))
				                            FROM TB_YM_WRKBOOKMTL 
				                           WHERE YD_WBOOK_ID =  A.YD_WBOOK_ID) = 'TC' 
				                     AND SUBSTR(A.YD_SCH_CD,3,6) = 'TC11UM'
				                     AND (SELECT COUNT(*)
				                            FROM TB_YM_EQUIP C
				                           WHERE EQUIP_GP LIKE '2XTC0%'
				                             AND EQUIP_GP <> A.YD_WRK_PLAN_TCAR
				                             AND SUBSTR(CARLOAD_STOP_LOC,1,2) ||SUBSTR(CARLD_SCH_CD,3,6) = A.YD_SCH_CD
				                             )  > 0
				                    THEN (SELECT EQUIP_GP
				                            FROM TB_YM_EQUIP C
				                           WHERE EQUIP_GP LIKE '2XTC0%'
				                             AND EQUIP_GP <> A.YD_WRK_PLAN_TCAR
				                             AND SUBSTR(CARLOAD_STOP_LOC,1,2) ||SUBSTR(CARLD_SCH_CD,3,6) = A.YD_SCH_CD
				                          )        
				                           
				                    ELSE NVL(YD_WRK_PLAN_TCAR, '2XTC0'||SUBSTR(:V_STACK_COL_GP,5,1)) END AS YD_WRK_PLAN_TCAR
				          FROM TB_YM_WRKBOOK A
				         WHERE A.YD_WBOOK_ID = :V_YD_WBOOK_ID) B

				 WHERE A.EQUIP_GP = B.YD_WRK_PLAN_TCAR

				)
				SELECT A.STACK_COL_GP  
				     , A.STACK_BED_GP  
				     , A.STACK_LAYER_GP  
				     , A.ABLE_LOC_CNT
				     , A.CARLOAD_STOP_LOC
				  FROM ( 
				        SELECT CUR.STACK_COL_GP     --적치 열 구분 
				             , CUR.STACK_BED_GP     --적치 BED 구분 
				             , CUR.STACK_LAYER_GP   --적치 단 구분 
				             , CUR.YD_WRK_PLAN_TCAR
				             , CUR.ABLE_LOC_CNT
				             , CUR.CARLOAD_STOP_LOC
				          FROM ( 
				                SELECT SL.STACK_COL_GP  
				                     , SL.STACK_BED_GP  
				                     , SL.STACK_LAYER_GP  
				                     , DECODE(SL.STACK_LAYER_GP - 1, 
				                            0, '01',  
				                            DECODE(SL.STACK_LAYER_GP - 1,  
				                                9, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1), 
				                                DECODE(LENGTH(SL.STACK_LAYER_GP - 1), 
				                                    1, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1), 
				                                    TO_CHAR(SL.STACK_LAYER_GP - 1)))) AS PRE_LAYER_GP 
				                     , SK.YD_WRK_PLAN_TCAR             
				                     , SK.ABLE_LOC_CNT  
				                     , B.CARLOAD_STOP_LOC
				                  FROM ( 
				                         SELECT A.STACK_COL_GP   --적치 열 구분 
				                              , A.STACK_BED_GP   --적치 BED 구분 
				                              , B.YD_WRK_PLAN_TCAR
				                              , (SELECT SUM(CASE WHEN B.PRE_WRK_YN     = 'Y'                                   THEN 1 
				                                                 WHEN STACK_LAYER_STAT = 'E' AND STACK_LAYER_ACTIVE_STAT = 'E' THEN 1 
				                                                 ELSE 0 END)
						                            FROM TB_YM_STACKLAYER 
								                   WHERE STACK_COL_GP = A.STACK_COL_GP 
								                     AND STACK_BED_GP = A.STACK_BED_GP) AS ABLE_LOC_CNT
				                           FROM TB_YM_STACKER A
				                              , SEARCH_TBL    B
				                          WHERE 1 = 1 
				                            AND A.STACK_COL_GP = :V_STACK_COL_GP   
				                            AND A.STACK_BED_GP = '01' 
				                            AND A.STACK_COL_GP =  B.CARLOAD_STOP_LOC
				                            AND 1 = CASE WHEN B.PRE_WRK_YN            = 'Y' THEN 1
				                                         WHEN A.STACK_BED_ACTIVE_STAT = 'L' THEN 1
				                                         ELSE 2  END 
				                          ORDER BY A.STACK_BED_GP ASC 
				                       )SK, 
				                       TB_YM_STACKLAYER SL, 
				                       SEARCH_TBL    B
				                 WHERE SK.STACK_COL_GP = SL.STACK_COL_GP 
				                   AND SK.STACK_BED_GP = SL.STACK_BED_GP 
				                   AND SL.STACK_LAYER_STAT = 'E' 
				                   AND 1 = CASE WHEN B.PRE_WRK_YN = 'Y' THEN 1
				                                WHEN SL.STACK_LAYER_ACTIVE_STAT = 'E' THEN 1
				                                ELSE 2  END 
				                   AND SL.STOCK_ID IS NULL 
				                    --적치 가능 매수 반드시                 
								   AND SK.ABLE_LOC_CNT >= nvl(:V_CRN_WRK_SH,2) 
				                ) CUR, 
				                TB_YM_STACKLAYER SL1 
				        WHERE   CUR.STACK_COL_GP = SL1.STACK_COL_GP 
				        AND     CUR.STACK_BED_GP = SL1.STACK_BED_GP 
				        AND     CUR.PRE_LAYER_GP = SL1.STACK_LAYER_GP 
				        AND     SL1.STACK_LAYER_STAT   NOT IN ('U') 
				        AND  SUBSTR(CUR.STACK_COL_GP,5,1) = SUBSTR(CUR.YD_WRK_PLAN_TCAR,6,1)
				        ORDER BY CUR.STACK_COL_GP, CUR.STACK_BED_GP, CUR.STACK_LAYER_GP 
				    )A 
				WHERE  ROWNUM <= 1
				*/
				
				outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocColTc", logId, methodNm, "해당열 조회");
				if (outjsResult.size() <= 0) {
					return YmConstant.RETN_CD_FAILURE;			
				}
			} else {
				
				szDBLogMsg = szDBLogMsg + "차량인 경우 검색" + "\r\n";

				jrTemp = JDTORecordFactory.getInstance().create();
				jrTemp.setResultCode(logId);	//Log ID
				jrTemp.setResultMsg(methodNm);	//Log Method Name
				jrTemp.setField("YD_EQP_ID"			, ydEqpId);	
				jrTemp.setField("YD_SCH_CD"			, ydSchCd);	
				jrTemp.setField("YD_CRN_SCH_ID"		, ydCrnSchId);	
				jrTemp.setField("STACK_COL_GP"		, planToLoc);	
				jrTemp.setField("CRN_WRK_SH"		, ydWrkSh);	
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocCol 
				SELECT  A.STACK_COL_GP,
				        A.STACK_BED_GP,
				        A.STACK_LAYER_GP,
				        A.STACK_BED_ABLE_QNTY,
				        A.GRIP_TO,
				        A.TO_LOC,
				        A.ABLE_LOC_CNT
				FROM    (
				        SELECT  CUR.STACK_COL_GP,    --적치 열 구분
				                CUR.STACK_BED_GP,    --적치 BED 구분
				                CUR.STACK_LAYER_GP,  --적치 단 구분
				                CUR.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
				                        
				                NVL(CUR.STACK_COL_GP, '')    ||
				                NVL(CUR.STACK_BED_GP, '') AS GRIP_TO,
				                
				                NVL(CUR.STACK_COL_GP, '')    ||
				                NVL(CUR.STACK_BED_GP, '')    ||
				                NVL(CUR.STACK_LAYER_GP, '') AS TO_LOC,
				                CUR.ABLE_LOC_CNT
				        FROM    (
				                SELECT  SK.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
				                        SL.STACK_COL_GP,
				                        SL.STACK_BED_GP,
				                        SL.STACK_LAYER_GP,
				                        DECODE(SL.STACK_LAYER_GP - 1,
				                            0, '01', 
				                            DECODE(SL.STACK_LAYER_GP - 1, 
				                                9, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1),
				                                DECODE(LENGTH(SL.STACK_LAYER_GP - 1),
				                                    1, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1),
				                                    TO_CHAR(SL.STACK_LAYER_GP - 1)))) AS PRE_LAYER_GP,
				                        ABLE_LOC_CNT                                    
				                FROM    (
				                         SELECT '1' AS STACK_BED_ABLE_QNTY, 
				                                A.STACK_COL_GP,   --적치 열 구분
				                                A.STACK_BED_GP   --적치 BED 구분
				                                , (SELECT SUM(CASE WHEN STACK_LAYER_STAT = 'E' AND STACK_LAYER_ACTIVE_STAT = 'E' THEN 1 ELSE 0 END)
				                                   FROM TB_YM_STACKLAYER 
				                                  WHERE STACK_COL_GP = A.STACK_COL_GP 
				                                    AND STACK_BED_GP = A.STACK_BED_GP) AS ABLE_LOC_CNT
				                           FROM TB_YM_STACKER A
				                          WHERE A.STACK_COL_GP            = :V_STACK_COL_GP
				                            AND A.STACK_BED_ACTIVE_STAT   = 'L'
				                          ORDER BY STACK_BED_GP ASC
				                        )SK,
				                        TB_YM_STACKLAYER SL
				                WHERE   SK.STACK_COL_GP = SL.STACK_COL_GP
				                AND     SK.STACK_BED_GP = SL.STACK_BED_GP
				                AND     SL.STACK_LAYER_STAT        = 'E'
				                AND     SL.STACK_LAYER_ACTIVE_STAT = 'E'
				                AND     SL.STOCK_ID IS NULL
				                --적치 가능 매수 반드시                 
				                AND     SK.ABLE_LOC_CNT >= nvl(:V_CRN_WRK_SH,2) 
				                ) CUR,
				                TB_YM_STACKLAYER SL1
				        WHERE   CUR.STACK_COL_GP = SL1.STACK_COL_GP
				        AND     CUR.STACK_BED_GP = SL1.STACK_BED_GP
				        AND     CUR.PRE_LAYER_GP = SL1.STACK_LAYER_GP
				        AND     SL1.STACK_LAYER_STAT   NOT IN ('U')
				        AND A.STACK_COL_GP||A.STACK_BED_GP NOT IN (
				                                               -- TO위치 가이드 제외
				                                              SELECT NVL(A1.YD_TO_LOC_GUIDE,'AAA')
				                                                FROM TB_YM_WRKBOOK A1
				                                                   , TB_YM_WRKBOOKMTL B1
				                                               WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                                                 AND A1.DEL_YN = 'N'
				                                                 AND B1.DEL_YN = 'N'
				                                                 AND A1.YD_GP  = '2'
				                                                 AND LENGTH(A1.YD_TO_LOC_GUIDE) = 8
				                                               GROUP BY A1.YD_TO_LOC_GUIDE
				                                              UNION ALL
				                                              SELECT NVL(B1.MTL_YD_TO_LOC_GUIDE,'AAA')
				                                                FROM TB_YM_WRKBOOK A1
				                                                   , TB_YM_WRKBOOKMTL B1
				                                               WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                                                 AND A1.DEL_YN = 'N'
				                                                 AND B1.DEL_YN = 'N'
				                                                 AND A1.YD_GP  = '2'
				                                                 AND LENGTH(B1.MTL_YD_TO_LOC_GUIDE) = 8
				                                               GROUP BY B1.MTL_YD_TO_LOC_GUIDE  
				                                              UNION ALL   
				                                               -- 작업예약된 BED 제외
				                                              SELECT NVL(C1.STACK_COL_GP || C1.STACK_BED_GP,'AAA')
				                                                FROM TB_YM_WRKBOOK A1 
				                                                   , TB_YM_WRKBOOKMTL B1
				                                                   , TB_YM_STACKLAYER C1
				                                               WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
				                                                 AND B1.STOCK_ID = C1.STOCK_ID
				                                                 AND A1.DEL_YN = 'N'
				                                                 AND B1.DEL_YN = 'N' 
				                                                 AND A1.YD_GP  = '2'
				                                                 AND C1.STACK_LAYER_STAT IN ('C','U') 
				                                               GROUP BY C1.STACK_COL_GP || C1.STACK_BED_GP  
				                                               )                                                          
				                                                           
				        ORDER BY STACK_COL_GP, STACK_BED_GP, STACK_LAYER_GP
				    )A
				WHERE  ROWNUM = 1
				*/
				
				outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocCol", logId, methodNm, "해당열 조회");
				if (outjsResult.size() <= 0) {
					return YmConstant.RETN_CD_FAILURE;			
				}				
				
			}
			jrSearchLoc = outjsResult.getRecord(0);
			if(jrSearchLoc != null) {
				String szStackColGp   = commUtils.trim(jrSearchLoc.getFieldString("STACK_COL_GP")) ;
				String szStackBedGp   = commUtils.trim(jrSearchLoc.getFieldString("STACK_BED_GP")) ;
				String szStackLayerGp = commUtils.trim(jrSearchLoc.getFieldString("STACK_LAYER_GP")) ;
				sRtnBedDan = szStackColGp+szStackBedGp+ szStackLayerGp ;
			}
			if(sRtnBedDan.length() < 10) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 TO위치 결정 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				if(sAPP005_YN.equals("Y")) {
	    		
					JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, "SLAB");
	    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
	    			jrLog.setField("YD_GP"			, "2");
	    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
	    			jrLog.setField("SCH_CONTENTS"	, "대차 및 차량 검색 실패:"+szDBLogMsg + "\r\n"  );
	    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
				
				return YmConstant.RETN_CD_FAILURE;				
			} else {
				
				if(sAPP005_YN.equals("Y")) {
		    		
					JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, "SLAB");
	    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
	    			jrLog.setField("YD_GP"			, "2");
	    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
	    			jrLog.setField("SCH_CONTENTS"	, "대차 및 차량 검색 성공:"+sRtnBedDan + "\r\n"  );
	    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
			}
			
			commUtils.printLog(logId, methodNm , "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return sRtnBedDan;
	}      

    
	/**
	 * 중량,폭,길이,두께항목을 가지고 적치가능한 곳을 검색
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord procOtherBk(String logId, String methodNms,JDTORecord jrWbook, JDTORecord jrCrnMtlSch, JDTORecord jrCrnSch , JDTORecordSet jsToLoc) throws JDTOException {
    	String methodNm = "TO 위치결정:중량,폭,길이,두께항목을 가지고 적치가능한 곳을 검색[BSlabSchSeEJB.procOtherBk] < " + methodNms;
    	String szLogMsg					= null;
    	JDTORecord		jrTemp			= JDTORecordFactory.getInstance().create();
    	JDTORecord		jrInParam		= JDTORecordFactory.getInstance().create();
    	
    	JDTORecord		jrSearchLoc		= JDTORecordFactory.getInstance().create();
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------

		String ydSchCd 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String ydCrnSchId 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String ydEqpId     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String StockId 	    	= commUtils.trim(jrCrnMtlSch.getFieldString("STOCK_ID"));
		//String sSlabT 		    = commUtils.trim(jrCrnMtlSch.getFieldString("SLAB_T")); 
		String sSlabW 		    = commUtils.trim(jrCrnMtlSch.getFieldString("MAX_MTL_W")); 
		String sSlabLen 		= commUtils.trim(jrCrnMtlSch.getFieldString("MAX_MTL_L")); 
		String ydWrkSh 			= commUtils.trim(jrCrnMtlSch.getFieldString("CRN_WRK_SH")); //권상매수
		
		JDTORecord jrToLoc          = JDTORecordFactory.getInstance().create();
		String planToLoc = "";
		commUtils.printLog(logId, methodNm, "S+");
		try {
			for (int Loop_i = 1; Loop_i <= jsToLoc.size(); Loop_i++) {
				jsToLoc.absolute(Loop_i);
				jrToLoc  = jsToLoc.getRecord();
	    		
	    		//TO예정위치
	    		planToLoc     = commUtils.trim(jrToLoc.getFieldString("STACK_COL_GP"));
	    		if(!YmComm.chkEqpIdGp(ydSchCd,planToLoc)) {
					
					if("PT02LM".equals(ydSchCd.substring(2, 8))){
						//신규방식(동일강종1,2,3,4,5, 기준))
						jrSearchLoc = this.procToLocPtToYd(logId, methodNms,jrWbook,jrCrnMtlSch,jrCrnSch, jsToLoc );
					}else{
						
						commUtils.printLog(logId, "▶▶▶폭,길이 항목을 가지고 검색", "SL");
						jrInParam = JDTORecordFactory.getInstance().create();
						jrInParam.setResultCode(logId);	//Log ID
						jrInParam.setResultMsg(methodNm);	//Log Method Name
						jrInParam.setField("STOCK_ID"       , StockId);		//권상 STOCK
						jrInParam.setField("YD_SCH_CD"		, ydSchCd);		//스케줄 코드
						jrInParam.setField("YD_EQP_ID"		, ydEqpId);		//설비ID
						jrInParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);	//크레인 스케쥴 ID
						jrInParam.setField("SELECT_GP"	, "WID/LEN");	//검색위치
						jrInParam.setField("MAX_MTL_W"	, sSlabW);	
						jrInParam.setField("MAX_MTL_L"	, sSlabLen);	
						jrInParam.setField("CRN_WRK_SH"	, ydWrkSh);	
						
						jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLoc);

						//기존 방식(가용 폭,길이 범위)
					}
					
					commUtils.printLog(logId, "SLAB SUB SUB TOLOC =>" + StockId + "=>" + planToLoc, "SL");
					if (jrSearchLoc != null) {
						break;
					}
				}else{
					
					jrTemp = JDTORecordFactory.getInstance().create();
					jrTemp.setResultCode(logId);	//Log ID
					jrTemp.setResultMsg(methodNm);	//Log Method Name
					jrTemp.setField("STACK_COL_GP"		, planToLoc);	
					jrTemp.setField("CRN_WRK_SH"		, ydWrkSh);	
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocCol
					SELECT  A.STACK_COL_GP,
					        A.STACK_BED_GP,
					        A.STACK_LAYER_GP,
					        A.STACK_BED_ABLE_QNTY,
					        A.GRIP_TO,
					        A.TO_LOC,
					        A.ABLE_LOC_CNT
					FROM    (
					        SELECT  CUR.STACK_COL_GP,    --적치 열 구분
					                CUR.STACK_BED_GP,    --적치 BED 구분
					                CUR.STACK_LAYER_GP,  --적치 단 구분
					                CUR.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
					                        
					                NVL(CUR.STACK_COL_GP, '')    ||
					                NVL(CUR.STACK_BED_GP, '') AS GRIP_TO,
					                
					                NVL(CUR.STACK_COL_GP, '')    ||
					                NVL(CUR.STACK_BED_GP, '')    ||
					                NVL(CUR.STACK_LAYER_GP, '') AS TO_LOC,
					                CUR.ABLE_LOC_CNT
					        FROM    (
					                SELECT  SK.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
					                        SL.STACK_COL_GP,
					                        SL.STACK_BED_GP,
					                        SL.STACK_LAYER_GP,
					                        DECODE(SL.STACK_LAYER_GP - 1,
					                            0, '01', 
					                            DECODE(SL.STACK_LAYER_GP - 1, 
					                                9, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1),
					                                DECODE(LENGTH(SL.STACK_LAYER_GP - 1),
					                                    1, '0' || TO_CHAR(SL.STACK_LAYER_GP - 1),
					                                    TO_CHAR(SL.STACK_LAYER_GP - 1)))) AS PRE_LAYER_GP,
					                        ABLE_LOC_CNT                                    
					                FROM    (
					                         SELECT '1' AS STACK_BED_ABLE_QNTY, 
					                                A.STACK_COL_GP,   --적치 열 구분
					                                A.STACK_BED_GP   --적치 BED 구분
					                                , (SELECT SUM(CASE WHEN STACK_LAYER_STAT = 'E' AND STACK_LAYER_ACTIVE_STAT = 'E' THEN 1 ELSE 0 END)
					                                   FROM TB_YM_STACKLAYER 
					                                  WHERE STACK_COL_GP = A.STACK_COL_GP 
					                                    AND STACK_BED_GP = A.STACK_BED_GP) AS ABLE_LOC_CNT
					                           FROM TB_YM_STACKER A
					                          WHERE A.STACK_COL_GP            = :V_STACK_COL_GP
					                            AND A.STACK_BED_ACTIVE_STAT   = 'L'
					                          ORDER BY STACK_BED_GP ASC
					                        )SK,
					                        TB_YM_STACKLAYER SL
					                WHERE   SK.STACK_COL_GP = SL.STACK_COL_GP
					                AND     SK.STACK_BED_GP = SL.STACK_BED_GP
					                AND     SL.STACK_LAYER_STAT        = 'E'
					                AND     SL.STACK_LAYER_ACTIVE_STAT = 'E'
					                AND     SL.STOCK_ID IS NULL
					                --적치 가능 매수 반드시                 
					                AND     SK.ABLE_LOC_CNT >= nvl(:V_CRN_WRK_SH,2) 
					                ) CUR,
					                TB_YM_STACKLAYER SL1
					        WHERE   CUR.STACK_COL_GP = SL1.STACK_COL_GP
					        AND     CUR.STACK_BED_GP = SL1.STACK_BED_GP
					        AND     CUR.PRE_LAYER_GP = SL1.STACK_LAYER_GP
					        AND     SL1.STACK_LAYER_STAT   NOT IN ('U')
					        AND A.STACK_COL_GP||A.STACK_BED_GP NOT IN (
					                                               -- TO위치 가이드 제외
					                                              SELECT NVL(A1.YD_TO_LOC_GUIDE,'AAA')
					                                                FROM TB_YM_WRKBOOK A1
					                                                   , TB_YM_WRKBOOKMTL B1
					                                               WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
					                                                 AND A1.DEL_YN = 'N'
					                                                 AND B1.DEL_YN = 'N'
					                                                 AND A1.YD_GP  = '2'
					                                                 AND LENGTH(A1.YD_TO_LOC_GUIDE) = 8
					                                               GROUP BY A1.YD_TO_LOC_GUIDE
					                                              UNION ALL
					                                              SELECT NVL(B1.MTL_YD_TO_LOC_GUIDE,'AAA')
					                                                FROM TB_YM_WRKBOOK A1
					                                                   , TB_YM_WRKBOOKMTL B1
					                                               WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
					                                                 AND A1.DEL_YN = 'N'
					                                                 AND B1.DEL_YN = 'N'
					                                                 AND A1.YD_GP  = '2'
					                                                 AND LENGTH(B1.MTL_YD_TO_LOC_GUIDE) = 8
					                                               GROUP BY B1.MTL_YD_TO_LOC_GUIDE  
					                                              UNION ALL   
					                                               -- 작업예약된 BED 제외
					                                              SELECT NVL(C1.STACK_COL_GP || C1.STACK_BED_GP,'AAA')
					                                                FROM TB_YM_WRKBOOK A1 
					                                                   , TB_YM_WRKBOOKMTL B1
					                                                   , TB_YM_STACKLAYER C1
					                                               WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
					                                                 AND B1.STOCK_ID = C1.STOCK_ID
					                                                 AND A1.DEL_YN = 'N'
					                                                 AND B1.DEL_YN = 'N' 
					                                                 AND A1.YD_GP  = '2'
					                                                 AND C1.STACK_LAYER_STAT IN ('C','U') 
					                                               GROUP BY C1.STACK_COL_GP || C1.STACK_BED_GP  
					                                               )                                                          
					                                                           
					        ORDER BY STACK_COL_GP, STACK_BED_GP, STACK_LAYER_GP
					    )A
					WHERE  ROWNUM = 1
					*/
					
					JDTORecordSet outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabToLocCol", logId, methodNm, "해당열 조회");
 	 				if (outjsResult.size() > 0) {
 	 	 				outjsResult.absolute(1);
 						jrSearchLoc  = outjsResult.getRecord();
 						break;
					}
				}
								
			}

			//----------------------------------------------------------------------------------------------------------------------
			// ERROR 발생시 ?
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return jrSearchLoc;
	}   
    
	/**
	 * 이송하차작업 TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord procToLocPtToYd(String logId, String methodNms,JDTORecord jrWbook, JDTORecord jrCrnMtlSch, JDTORecord jrCrnSch , JDTORecordSet jsToLoc) throws JDTOException {
    	String methodNm = "TO 위치결정:이송하차 작업[BSlabSchSeEJB.procToLocPtToYd] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= JDTORecordFactory.getInstance().create();;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------

		String ydSchCd 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String ydCrnSchId 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String ydEqpId     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String StockId 	    	= commUtils.trim(jrCrnMtlSch.getFieldString("STOCK_ID"));
		String ydWrkSh 			= commUtils.trim(jrCrnMtlSch.getFieldString("CRN_WRK_SH")); //권상매수
		
		
		JDTORecord jrSearchLoc          = JDTORecordFactory.getInstance().create();
		String planToLoc = "";
		commUtils.printLog(logId, methodNm, "S+");
		try {

			JDTORecord jrInParam = JDTORecordFactory.getInstance().create();
			jrInParam.setResultCode(logId);	//Log ID
			jrInParam.setResultMsg(methodNm);	//Log Method Name
			jrInParam.setField("STOCK_ID"       , StockId);													//권상 STOCK
			jrInParam.setField("YD_SCH_CD"		, ydSchCd);		//스케줄 코드
			jrInParam.setField("YD_EQP_ID"		, ydEqpId);		//설비ID
			jrInParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);	//크레인 스케쥴 ID
			jrInParam.setField("CRN_WRK_SH"		, ydWrkSh);	
			commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶ 동일강종 , 동일 생산폭(30mm이내), 동일 주문두께,주문폭  TO위치 검색◀◀◀◀◀◀◀◀◀◀", "SL");
			jrInParam.setField("SELECT_GP"	, "L");	//검색위치
			jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLoc);
			
			if (jrSearchLoc == null) {
				commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶ 공BED  TO위치 검색◀◀◀◀◀◀◀◀◀◀", "SL");
				jrInParam.setField("SELECT_GP"	, "E");	
				jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLoc);
			}
			if (jrSearchLoc == null) {
				commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶ (동일강종,동일 생산폭+-30 ) TO위치 검색/01단 TO위치 검색  TO위치 검색◀◀◀◀◀◀◀◀◀◀", "SL");
				jrInParam.setField("SELECT_GP"	, "L2");	
				jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLoc);
			}
			if (jrSearchLoc == null) {
				commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶ (1단에 주문두께와 폭이 작은것 ) TO위치 검색/01단 TO위치 검색  TO위치 검색◀◀◀◀◀◀◀◀◀◀", "SL");
				jrInParam.setField("SELECT_GP"	, "M");	
				jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLoc);
			}
			if (jrSearchLoc == null) {
				commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶ (적치가능한 02단 이상정보를 검색 ) TO위치 검색/01단 TO위치 검색  TO위치 검색◀◀◀◀◀◀◀◀◀◀", "SL");
				jrInParam.setField("SELECT_GP"	, "U");	
				jrSearchLoc = this.procToLocBed (logId, methodNms, jrInParam ,jsToLoc);
			}

			//----------------------------------------------------------------------------------------------------------------------
			// ERROR 발생시 ?
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return jrSearchLoc;
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
	public String procUpdateLoc(String logId, String methodNms, JDTORecord jrSetLoc, JDTORecord jrCrnSch, JDTORecord jrCrnMtlSch) throws JDTOException {
		String methodNm = "TO위치 UPDATE[BSlabSchSeEJB.procUpdateLoc] < " + methodNms;
		String LocalmethodNm = "TO위치 UPDATE[BSlabSchSeEJB.procUpdateLoc]" ;
		String szLogMsg					= null;
		JDTORecord		recInBed		= null;
		JDTORecord		jrParm		    = null;
		
		int intRtnVal					= 0;
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");
		try {

//			int intYdEqpWrkWt    	= commUtils.paraRecChkNullInt(jrCrnSch,"SUM_MTL_WT");			//크레인작업재료 총중량
//			double dblYdEqpWrkT     = commUtils.paraRecChkNullDouble(jrCrnSch,"SUM_MTL_T");			//크레인작업재료 총높이
			String szYdEqpWrkMaxW 	= commUtils.trim(jrCrnSch.getFieldString("MAX_MTL_W"  ));		//크레인작업재료 중 최대 폭
			String szYdEqpWrkMaxL 	= commUtils.trim(jrCrnSch.getFieldString("MAX_MTL_L"  ));		//크레인작업재료 중 최대 길이
			String ydToLocDcsnMtd 	= commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"  ));//주작업여부
			
			String ydCrnSchId  		= commUtils.trim(jrSetLoc.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
			String ydSchCd  		= commUtils.trim(jrSetLoc.getFieldString("YD_SCH_CD"  ));	
			String ydEqpId     		= commUtils.trim(jrSetLoc.getFieldString("YD_EQP_ID"));			//크레인설비ID
			String ydUpWoLoc		= commUtils.trim(jrSetLoc.getFieldString("YD_UP_WO_LOC"  ));			
			String ydUpWoLayer		= commUtils.trim(jrSetLoc.getFieldString("YD_UP_WO_LAYER"  ));			
			String ydDnWoLoc		= commUtils.trim(jrSetLoc.getFieldString("YD_DN_WO_LOC"  ));			
			String ydDnWoLayer		= commUtils.trim(jrSetLoc.getFieldString("YD_DN_WO_LAYER"  ));			
			String StockId 	   		= commUtils.trim(jrCrnMtlSch.getFieldString("STOCK_ID"  ));
			String ydBendingYn      = commUtils.trim(jrCrnMtlSch.getFieldString("YD_RULE_PL_RS_GP"));  //BENDING 여부
			String modifier 		= commUtils.trim(jrCrnMtlSch.getFieldString("MODIFIER"  ));		//MODIFIER
			int intYdEqpWrkSh       = commUtils.paraRecChkNullInt(jrCrnMtlSch,"CRN_WRK_SH");				//크레인작업재료 총매수
			int intYdEqpWrkWt    	= commUtils.paraRecChkNullInt(jrCrnMtlSch,"SUM_MTL_WT");			//크레인작업재료 총중량
			double dblYdEqpWrkT     = commUtils.paraRecChkNullDouble(jrCrnMtlSch,"SUM_MTL_T");			//크레인작업재료 총높이
					
			if (ydDnWoLoc.equals("")) {
				return YmConstant.RETN_CD_FAILURE;
			}
			
			commUtils.printParam(logId, jrSetLoc);
			//----------------------------------------------------------------------------------------------------------------------
			// 권하지시위치 수정
			//----------------------------------------------------------------------------------------------------------------------
			
			JDTORecordSet jsLayerUpXy = JDTORecordFactory.getInstance().createRecordSet("");
			recInBed= JDTORecordFactory.getInstance().create();
			recInBed.setField("STACK_COL_GP", 			ydUpWoLoc.substring(0, 6)); //권상지시위치
			recInBed.setField("STACK_BED_GP", 			ydUpWoLoc.substring(6));	 //권상지시위치
			recInBed.setField("STACK_LAYER_GP", 		ydUpWoLayer);
				
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkLayerBybed 
			SELECT A.STACK_COL_GP 
			     , A.STACK_BED_GP 
			     , A.STACK_LAYER_X_AXIS
			     , A.STACK_LAYER_Y_AXIS
			     , A.STACK_LAYER_Z_AXIS
			     , B.YD_STK_BED_XAXIS_TOL 
			     , B.YD_STK_BED_YAXIS_TOL 
			     , B.YD_STK_BED_ZAXIS_TOL 
			     , (SELECT ROTATION_ANGLE FROM TB_YM_STACKCOL WHERE STACK_COL_GP = A.STACK_COL_GP) AS ROTATION_ANGLE
			  FROM TB_YM_STACKLAYER A
			     , TB_YM_STACKER    B
			 WHERE A.STACK_COL_GP = B.STACK_COL_GP
			   AND A.STACK_BED_GP = B.STACK_BED_GP
			   AND A.STACK_COL_GP = :V_STACK_COL_GP
			   AND A.STACK_BED_GP = :V_STACK_BED_GP
			   AND A.STACK_LAYER_GP = :V_STACK_LAYER_GP
			   AND A.DEL_YN ='N'
			   AND B.DEL_YN ='N'
			*/  
			jsLayerUpXy = commDao.select(recInBed, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkLayerBybed", logId, methodNm, "권상 BED 좌표 조회");
			if (jsLayerUpXy.size() <= 0) {
				szLogMsg =  "확인:"+StockId+"권상 Layer 좌표 조회 검색 실패.";
				commUtils.printLog(logId, szLogMsg, "SL");
				
			}
			jsLayerUpXy.first();
			JDTORecord jrUpLayerXy = jsLayerUpXy.getRecord();
	
			JDTORecordSet jsDnLayerXy = JDTORecordFactory.getInstance().createRecordSet("");
			recInBed= JDTORecordFactory.getInstance().create();
			recInBed.setField("STACK_COL_GP", 			ydDnWoLoc.substring(0, 6));//권하지시위치
			recInBed.setField("STACK_BED_GP", 			ydDnWoLoc.substring(6));	//권하지시위치
			recInBed.setField("STACK_LAYER_GP", 		ydDnWoLayer);				
			
			if("PT02UM".equals(ydSchCd.substring(2,8)) && "PT".equals(ydDnWoLoc.substring(2, 4))) {
				//이송상차작업이며서 권하위치가 차량포인트인 경우 
				jsDnLayerXy = commDao.select(recInBed, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkLayerByYm2019", logId, methodNm, "이송상차 권하 BED 좌표 조회");
				if (jsDnLayerXy.size() <= 0) {
					szLogMsg = LocalmethodNm +" 이송하차 권하 Layer YM2019  좌표 검색 실패 ";
					commUtils.printLog(logId, szLogMsg, "SL");
					
				}
				
			} else {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkLayerBybed 
				SELECT A.STACK_COL_GP 
				     , A.STACK_BED_GP 
				     , C.DTL_ITM1
				     , (CASE  WHEN C.DTL_ITM1='Y' THEN NVL(B.YD_STK_BED_XAXIS_REAL,A.STACK_LAYER_X_AXIS) ELSE A.STACK_LAYER_X_AXIS END) AS STACK_LAYER_X_AXIS
				     , (CASE  WHEN C.DTL_ITM1='Y' THEN NVL(B.YD_STK_BED_YAXIS_REAL,A.STACK_LAYER_Y_AXIS) ELSE A.STACK_LAYER_Y_AXIS END) AS STACK_LAYER_Y_AXIS
				     , A.STACK_LAYER_Z_AXIS  AS STACK_LAYER_Z_AXIS
				     , B.YD_STK_BED_XAXIS_TOL 
				     , B.YD_STK_BED_YAXIS_TOL 
				     , B.YD_STK_BED_ZAXIS_TOL 
				     , (SELECT ROTATION_ANGLE FROM TB_YM_STACKCOL WHERE STACK_COL_GP = A.STACK_COL_GP) AS ROTATION_ANGLE
				  FROM TB_YM_STACKLAYER A
				     , TB_YM_STACKER    B
				     , (SELECT CD_GP||ITEM  AS ITEM, DTL_ITM1 
				         FROM  TB_YM_RULE  R
				         WHERE R.REPR_CD_GP='APP110'
				          AND R.DEL_YN='N'
				          AND CD_GP='2') C
				 WHERE A.STACK_COL_GP = B.STACK_COL_GP
				   AND A.STACK_BED_GP = B.STACK_BED_GP
				   AND SUBSTR(A.STACK_COL_GP,1,2)=C.ITEM
				   AND A.STACK_COL_GP = :V_STACK_COL_GP
				   AND A.STACK_BED_GP = :V_STACK_BED_GP
				   AND A.STACK_LAYER_GP = :V_STACK_LAYER_GP
				   AND A.DEL_YN ='N'
				   AND B.DEL_YN ='N'
				*/  
				jsDnLayerXy = commDao.select(recInBed, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkLayerBybed", logId, methodNm, "권하 BED 좌표 조회");
				if (jsDnLayerXy.size() <= 0) {
					szLogMsg = LocalmethodNm +" 권하 Layer 좌표 검색 실패 ";
					commUtils.printLog(logId, szLogMsg, "SL");
					
				}
			}
			
			jsDnLayerXy.first();
			JDTORecord jrDnLayerXy = jsDnLayerXy.getRecord();
			
			JDTORecord jrUpCrnSch = JDTORecordFactory.getInstance().create();
			jrUpCrnSch.setField("YD_CRN_SCH_ID", 			ydCrnSchId);										//크레인스케줄ID
			
			//권상정보   					
			jrUpCrnSch.setField("YD_UP_WO_LOC", 			ydUpWoLoc);										//권상지시위치
			jrUpCrnSch.setField("YD_UP_WO_LAYER", 			ydUpWoLayer);										//권상지시단
			jrUpCrnSch.setField("YD_UP_STK_COL_GP", 		ydUpWoLoc.substring(0, 6));						//권상지시위치 - 적치열
			jrUpCrnSch.setField("YD_UP_STK_BED_NO", 		ydUpWoLoc.substring(6));							//권상지시위치 - 적치베드
			jrUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",  		commUtils.trim(jrUpLayerXy.getFieldString("STACK_LAYER_X_AXIS"  ))) ;
			jrUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  		commUtils.trim(jrUpLayerXy.getFieldString("STACK_LAYER_Y_AXIS"  ))) ;
			jrUpCrnSch.setField("YD_UP_WO_LOC_ZAXIS",  		commUtils.trim(jrUpLayerXy.getFieldString("STACK_LAYER_Z_AXIS"  )) ) ;
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
			jrUpCrnSch.setField("YD_DN_WO_LOC", 			ydDnWoLoc);											//권하지시위치
			if(intYdEqpWrkSh == 1) {
				jrUpCrnSch.setField("YD_DN_WO_LAYER", 		ydDnWoLayer);										//권하지시단
			} else {
				jrUpCrnSch.setField("YD_DN_WO_LAYER", 		commUtils.stringPlusInt(ydDnWoLayer,1));										//권하지시단
				
			}
			jrUpCrnSch.setField("YD_DN_STK_COL_GP", 		ydDnWoLoc.substring(0, 6));							//권하지시위치 - 적치열
			jrUpCrnSch.setField("YD_DN_STK_BED_NO", 		ydDnWoLoc.substring(6));							//권하지시위치 - 적치베드
			
			
			jrUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",  		commUtils.trim(jrDnLayerXy.getFieldString("STACK_LAYER_X_AXIS"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  		commUtils.trim(jrDnLayerXy.getFieldString("STACK_LAYER_Y_AXIS"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_LOC_ZAXIS",  		commUtils.trim(jrDnLayerXy.getFieldString("STACK_LAYER_Z_AXIS"  )) ) ;
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
			jrUpCrnSch.setField("YD_EQP_WRK_SH", 			String.valueOf(intYdEqpWrkSh));						//크레인작업재료 총매수
			jrUpCrnSch.setField("YD_EQP_WRK_WT", 			String.valueOf(intYdEqpWrkWt));						//크레인작업재료 총중량
			jrUpCrnSch.setField("YD_EQP_WRK_T", 			String.valueOf(dblYdEqpWrkT));						//크레인작업재료 총높이
			jrUpCrnSch.setField("YD_EQP_WRK_MAX_W", 		szYdEqpWrkMaxW);									//크레인작업재료 중 최대 폭
			jrUpCrnSch.setField("YD_EQP_WRK_MAX_L", 		szYdEqpWrkMaxL);									//크레인작업재료 중 최대 길이
			jrUpCrnSch.setField("MODIFIER", 				modifier);
			
			szLogMsg =LocalmethodNm +" 크레인스케쥴 ID["+ydCrnSchId+"] 작업에 따른 설비명 변경처리시작 ▶▶▶▶▶ 권하지시위치["+ydDnWoLoc+"], 권하지시단["+ydDnWoLayer+"]" ;

			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdEqpChange
			WITH P_DATA AS 
			(
			SELECT :V_YD_EQP_ID             AS YD_EQP_ID
			     , SUBSTR(:V_YD_EQP_ID,2,1) AS YD_BAY_GP
			     , :V_YD_SCH_CD             AS YD_SCH_CD
			     , :V_BENDING_YN            AS BENDING_YN
			     , :V_YD_DN_WO_LOC          AS YD_DN_WO_LOC
			     , :V_YD_TO_LOC_DCSN_MTD    AS YD_TO_LOC_DCSN_MTD
			  FROM DUAL
			)
			SELECT CASE WHEN YD_TO_LOC_DCSN_MTD = 'S' THEN
			            CASE WHEN SUBSTR(YD_DN_WO_LOC,2,1) = 'E' AND BENDING_YN = 'S' THEN  '2ECRE3' 
			                 WHEN SUBSTR(YD_DN_WO_LOC,3,2) IN ('WB') THEN 
			                      (SELECT YD_WRK_CRN FROM TB_YM_SCHEDULERULE WHERE YD_SCH_CD = '2CWB01UM' AND ROWNUM  = 1 ) 
			                 WHEN SUBSTR(YD_DN_WO_LOC,3,2) IN ('CT') THEN  
			                      (SELECT YD_WRK_CRN FROM TB_YM_SCHEDULERULE WHERE YD_SCH_CD = '2'||YD_BAY_GP||'CCT01UM' AND ROWNUM  = 1 ) 
			            ELSE YD_EQP_ID END 
			       ELSE YD_EQP_ID END YD_EQP_ID          
			     , CASE WHEN YD_TO_LOC_DCSN_MTD = 'S' THEN
			            CASE WHEN YD_SCH_CD IN ('2CWB01UM')  AND SUBSTR(YD_DN_WO_LOC,3,2) NOT IN ('WB') THEN  '2CTC11LM' 
			            ELSE YD_SCH_CD END 
			       ELSE YD_SCH_CD END YD_SCH_CD          
			                    
			  FROM P_DATA

			*/  
			
			jrParm= JDTORecordFactory.getInstance().create();
			jrParm.setField("YD_EQP_ID", 			ydEqpId); 
			jrParm.setField("YD_SCH_CD", 			ydSchCd);	 
			jrParm.setField("BENDING_YN", 			ydBendingYn);			
			jrParm.setField("YD_UP_WO_LOC", 		ydUpWoLoc);			
			jrParm.setField("YD_DN_WO_LOC", 		ydDnWoLoc);			
			jrParm.setField("YD_TO_LOC_DCSN_MTD", 	ydToLocDcsnMtd);		
			jrParm.setField("YD_CRN_SCH_ID", 		ydCrnSchId);			

			JDTORecordSet jsEqpChange = commDao.select(jrParm, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdEqpChange", logId, methodNm, "설비 변경 여부 ");
			if (jsEqpChange.size() > 0) {
				String tmpYdEqpId = commUtils.trim(jsEqpChange.getRecord(0).getFieldString("YD_EQP_ID"));
				String tmpYdSchCd = commUtils.trim(jsEqpChange.getRecord(0).getFieldString("YD_SCH_CD"));
				if(!"".equals(tmpYdEqpId)) {
					jrUpCrnSch.setField("YD_EQP_ID", tmpYdEqpId);	
				}
				if(!"".equals(tmpYdSchCd)) {
					jrUpCrnSch.setField("YD_SCH_CD", tmpYdSchCd);	
				}
			}
			
			// 스카핑 1순위로 변경
			if(ydBendingYn.equals("S") && ("E".equals(ydEqpId.substring(1,1)))){			
				jrUpCrnSch.setField("YD_SCH_PRIOR", 	    "1");		
			}	
			szLogMsg =LocalmethodNm +" 크레인스케쥴 ID["+ydCrnSchId+"] 작업에 따른 설비명 변경처리완료 ▶▶▶▶▶ 권하지시위치["+ydDnWoLoc+"], 권하지시단["+ydDnWoLayer+"]" ;
			
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYdCrnWrkSidedelyn
			UPDATE TB_YM_CRNSCH
			   SET MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,YD_EQP_WRK_SH            = NVL(:V_YD_EQP_WRK_SH          ,YD_EQP_WRK_SH)
			      ,YD_EQP_WRK_WT            = NVL(:V_YD_EQP_WRK_WT          ,YD_EQP_WRK_WT)
			      ,YD_EQP_WRK_T             = NVL(:V_YD_EQP_WRK_T           ,YD_EQP_WRK_T)
			      ,YD_EQP_WRK_MAX_W         = NVL(:V_YD_EQP_WRK_MAX_W       ,YD_EQP_WRK_MAX_W)
			      ,YD_EQP_WRK_MAX_L         = NVL(:V_YD_EQP_WRK_MAX_L       ,YD_EQP_WRK_MAX_L)
			      ,YD_CRN_SB_CTL_H          = NVL(:V_YD_CRN_SB_CTL_H        ,YD_CRN_SB_CTL_H)
			      ,YD_CRN_GRAB_USE_RULE_ID  = NVL(:V_YD_CRN_GRAB_USE_RULE_ID,YD_CRN_GRAB_USE_RULE_ID)
			      ,YD_UP_WO_LOC             = NVL(:V_YD_UP_WO_LOC           ,YD_UP_WO_LOC)
			      ,YD_UP_WO_LAYER           = NVL(:V_YD_UP_WO_LAYER         ,YD_UP_WO_LAYER)
			      ,YD_UP_WO_LOC_XAXIS       = NVL(:V_YD_UP_WO_LOC_XAXIS     ,YD_UP_WO_LOC_XAXIS)
			      ,YD_UP_WO_XAXIS_GAP_MAX   = NVL(:V_YD_UP_WO_XAXIS_GAP_MAX ,YD_UP_WO_XAXIS_GAP_MAX)
			      ,YD_UP_WO_XAXIS_GAP_MIN   = NVL(:V_YD_UP_WO_XAXIS_GAP_MIN ,YD_UP_WO_XAXIS_GAP_MIN)
			      ,YD_UP_WO_LOC_YAXIS       = NVL(:V_YD_UP_WO_LOC_YAXIS     ,YD_UP_WO_LOC_YAXIS)
			      ,YD_UP_WO_LOC_YAXIS1      = NVL(:V_YD_UP_WO_LOC_YAXIS1    ,YD_UP_WO_LOC_YAXIS1)
			      ,YD_UP_WO_LOC_YAXIS2      = NVL(:V_YD_UP_WO_LOC_YAXIS2    ,YD_UP_WO_LOC_YAXIS2)
			      ,YD_UP_WO_YAXIS_GAP_MAX   = NVL(:V_YD_UP_WO_YAXIS_GAP_MAX ,YD_UP_WO_YAXIS_GAP_MAX)
			      ,YD_UP_WO_YAXIS_GAP_MIN   = NVL(:V_YD_UP_WO_YAXIS_GAP_MIN ,YD_UP_WO_YAXIS_GAP_MIN)
			      ,YD_UP_WO_LOC_ZAXIS       = NVL(:V_YD_UP_WO_LOC_ZAXIS     ,YD_UP_WO_LOC_ZAXIS)
			      ,YD_UP_WO_ZAXIS_GAP_MAX   = NVL(:V_YD_UP_WO_ZAXIS_GAP_MAX ,YD_UP_WO_ZAXIS_GAP_MAX)
			      ,YD_UP_WO_ZAXIS_GAP_MIN   = NVL(:V_YD_UP_WO_ZAXIS_GAP_MIN ,YD_UP_WO_ZAXIS_GAP_MIN)
			      ,YD_DN_WO_LOC             = NVL(:V_YD_DN_WO_LOC           ,YD_DN_WO_LOC)
			      ,YD_DN_WO_LAYER           = NVL(:V_YD_DN_WO_LAYER         ,YD_DN_WO_LAYER)
			      ,YD_DN_WO_LOC_XAXIS       = NVL(:V_YD_DN_WO_LOC_XAXIS     ,YD_DN_WO_LOC_XAXIS)
			      ,YD_DN_WO_XAXIS_GAP_MAX   = NVL(:V_YD_DN_WO_XAXIS_GAP_MAX ,YD_DN_WO_XAXIS_GAP_MAX)
			      ,YD_DN_WO_XAXIS_GAP_MIN   = NVL(:V_YD_DN_WO_XAXIS_GAP_MIN ,YD_DN_WO_XAXIS_GAP_MIN)
			      ,YD_DN_WO_LOC_YAXIS       = NVL(:V_YD_DN_WO_LOC_YAXIS     ,YD_DN_WO_LOC_YAXIS)
			      ,YD_DN_WO_LOC_YAXIS1      = NVL(:V_YD_DN_WO_LOC_YAXIS1    ,YD_DN_WO_LOC_YAXIS1)
			      ,YD_DN_WO_LOC_YAXIS2      = NVL(:V_YD_DN_WO_LOC_YAXIS2    ,YD_DN_WO_LOC_YAXIS2)
			      ,YD_DN_WO_YAXIS_GAP_MAX   = NVL(:V_YD_DN_WO_YAXIS_GAP_MAX ,YD_DN_WO_YAXIS_GAP_MAX)
			      ,YD_DN_WO_YAXIS_GAP_MIN   = NVL(:V_YD_DN_WO_YAXIS_GAP_MIN ,YD_DN_WO_YAXIS_GAP_MIN)
			      ,YD_DN_WO_LOC_ZAXIS       = NVL(:V_YD_DN_WO_LOC_ZAXIS     ,YD_DN_WO_LOC_ZAXIS)
			      ,YD_DN_WO_ZAXIS_GAP_MAX   = NVL(:V_YD_DN_WO_ZAXIS_GAP_MAX ,YD_DN_WO_ZAXIS_GAP_MAX)
			      ,YD_DN_WO_ZAXIS_GAP_MIN   = NVL(:V_YD_DN_WO_ZAXIS_GAP_MIN ,YD_DN_WO_ZAXIS_GAP_MIN)
			      ,YD_UP_WR_LOC             = NVL(:V_YD_UP_WR_LOC           ,YD_UP_WR_LOC)
			      ,YD_UP_WR_LAYER           = NVL(:V_YD_UP_WR_LAYER         ,YD_UP_WR_LAYER)
			      ,YD_UP_WRK_ACT_GP         = NVL(:V_YD_UP_WRK_ACT_GP       ,YD_UP_WRK_ACT_GP)
			      ,YD_UP_WR_XAXIS           = NVL(:V_YD_UP_WR_XAXIS         ,YD_UP_WR_XAXIS)
			      ,YD_UP_WR_YAXIS           = NVL(:V_YD_UP_WR_YAXIS         ,YD_UP_WR_YAXIS)
			      ,YD_UP_WR_YAXIS1          = NVL(:V_YD_UP_WR_YAXIS1        ,YD_UP_WR_YAXIS1)
			      ,YD_UP_WR_YAXIS2          = NVL(:V_YD_UP_WR_YAXIS2        ,YD_UP_WR_YAXIS2)
			      ,YD_UP_WR_ZAXIS           = NVL(:V_YD_UP_WR_ZAXIS         ,YD_UP_WR_ZAXIS)
			      ,YD_DN_WR_LOC             = NVL(:V_YD_DN_WR_LOC           ,YD_DN_WR_LOC)
			      ,YD_DN_WR_LAYER           = NVL(:V_YD_DN_WR_LAYER         ,YD_DN_WR_LAYER)
			      ,YD_DN_WRK_ACT_GP         = NVL(:V_YD_DN_WRK_ACT_GP       ,YD_DN_WRK_ACT_GP)
			      ,YD_DN_WR_XAXIS           = NVL(:V_YD_DN_WR_XAXIS         ,YD_DN_WR_XAXIS)
			      ,YD_DN_WR_YAXIS           = NVL(:V_YD_DN_WR_YAXIS         ,YD_DN_WR_YAXIS)
			      ,YD_DN_WR_YAXIS1          = NVL(:V_YD_DN_WR_YAXIS1        ,YD_DN_WR_YAXIS1)
			      ,YD_DN_WR_YAXIS2          = NVL(:V_YD_DN_WR_YAXIS2        ,YD_DN_WR_YAXIS2)
			      ,YD_DN_WR_ZAXIS           = NVL(:V_YD_DN_WR_ZAXIS         ,YD_DN_WR_ZAXIS)
			      ,YD_EQP_ID                = NVL(:V_YD_EQP_ID              ,YD_EQP_ID)
			      ,YD_SCH_CD                = NVL(:V_YD_SCH_CD              ,YD_SCH_CD)
			      ,YD_TO_LOC_DCSN_MTD       = NVL(:V_YD_TO_LOC_DCSN_MTD     ,YD_TO_LOC_DCSN_MTD)
			      ,YD_AID_WRK_UPDN_GP       = NVL(:V_YD_AID_WRK_UPDN_GP     ,YD_AID_WRK_UPDN_GP)
			      ,UP_ROTATION_ANGLE        = NVL(:V_UP_ROTATION_ANGLE      ,UP_ROTATION_ANGLE)
			      ,DOWN_ROTATION_ANGLE      = NVL(:V_DOWN_ROTATION_ANGLE    ,DOWN_ROTATION_ANGLE)
			      ,YD_SCH_PRIOR             = NVL(:V_YD_SCH_PRIOR    ,YD_SCH_PRIOR) 
			 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			*/ 
			
			intRtnVal = commDao.update(jrUpCrnSch, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYdCrnWrkSidedelyn", logId, methodNm, "크레인스케쥴 갱신");
			
			if(intRtnVal <= 0) {
				szLogMsg =  "확인:"+StockId+"권하지시위치["+ydDnWoLoc+"], 권하지시단[" +ydDnWoLayer +" ]을 크레인스케줄에 수정 중 ERROR 발생";

				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}		
			
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkLayUpInfo 
			SELECT STOCK_ID 
			  FROM TB_YM_CRNWRKMTL A
			 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			   AND DEL_YN = 'N'
			 ORDER BY STACK_LAYER_GP DESC 
			 */
			recInBed.setField("YD_CRN_SCH_ID", 		ydCrnSchId);				
			
			JDTORecordSet jsOutBed = commDao.select(recInBed, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkLayUpInfo", logId, methodNm, "권상정보 검색");
			JDTORecord jrOutBed	= JDTORecordFactory.getInstance().create(); 
			JDTORecord jrParam	= JDTORecordFactory.getInstance().create(); 
			for(int i = 1; i <= jsOutBed.size(); i++) {
				jsOutBed.absolute(i);
				jrOutBed  = jsOutBed.getRecord();
				
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setField("MODIFIER", 		modifier);
				jrParam.setField("STACK_COL_GP", 	ydDnWoLoc.substring(0, 6));
				jrParam.setField("STACK_BED_GP", 	ydDnWoLoc.substring(6));
				jrParam.setField("STACK_LAYER_GP", 	commUtils.stringPlusInt(ydDnWoLayer,i-1));
				jrParam.setField("STOCK_ID",       	commUtils.trim(jrOutBed.getFieldString("STOCK_ID")));
				jrParam.setField("STACK_LAYER_STAT", "D");
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrYdStkColBedGp 
				UPDATE TB_YM_STACKLAYER            
				   SET MOD_DDTT     = SYSDATE             
				     , MODIFIER     = :V_MODIFIER             
				     , STACK_LAYER_ACTIVE_STAT  = NVL(:V_STACK_LAYER_ACTIVE_STAT,STACK_LAYER_ACTIVE_STAT)
				     , STACK_LAYER_STAT         = NVL(:V_STACK_LAYER_STAT,STACK_LAYER_STAT)
				     , STOCK_ID                 = NVL(:V_STOCK_ID , STOCK_ID)
				 WHERE STACK_COL_GP     = :V_STACK_COL_GP
				   AND STACK_BED_GP     = :V_STACK_BED_GP
				   AND STACK_LAYER_GP   = :V_STACK_LAYER_GP
	  
			    */  
				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrYdStkColBedGp", logId, methodNm, "TB_YM_STKLYR 갱신");
			
				//저장품에 등록할 위치
				if(intRtnVal <= 0) {
					commUtils.printLog(logId,  "확인:"+StockId+"권하지시위치["+ydDnWoLoc+"], 권하지시단["+commUtils.stringPlusInt(ydDnWoLayer,i-1)+"] 활성화중 ERROR 발생", "SL");
					return YmConstant.RETN_CD_FAILURE;
				}
			}	

			szLogMsg =LocalmethodNm +" 크레인스케쥴 ID["+ydCrnSchId+"] TO 위치결정▶▶▶▶▶ 권하지시위치["+ydDnWoLoc+"], 권하지시단["+ydDnWoLayer+"]" ;
			commUtils.printLog(logId, szLogMsg, "SL");

			commUtils.printLog(logId, methodNm, "S-");

		//----------------------------------------------------------------------------------------------------------------------
    	
		//----------------------------------------------------------------------------------------------------------------------
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}		
		return YmConstant.RETN_CD_SUCCESS;
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
		String methodNm = "스케줄 To위치 로그 Log[BSlabSchSeEJB.insSchLog] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insSchLog 
			INSERT INTO TB_YM_SCHLOG
			     ( YM_SCHLOG_ID_SEQ
				 , STOCK_ID
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
			     ( YM_SCHLOG_ID_SEQ.NEXTVAL
				 , :V_STOCK_ID
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
			String sAPP310_YN = YmComm.BCoilApplyYn("APP310","2","*");   
			commUtils.printLog(logId,  "실좌표적용여부:" + sAPP310_YN, "SL");	
			if(sAPP310_YN.equals("Y") ) {
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insSchLog", logId, methodNm, "스케줄 To위치 로그");
			}
			 
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	
	/**
	 * 오퍼레이션명 : B열연 SLAB야드 크레인 스케줄 GROUPING PARAMETER DATA SETTING
	 *  
	 * @param  String     szEqpId, szSchCd, rsMinWrkBookMtl, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public JDTORecordSet CrnSchGrpHandling( String logId, String methodNms, String sFlag ,String ydSchCd, String sAPP005_YN, String ydWbookId,  JDTORecordSet jsWbBed,JDTORecord jrCrEquip) throws JDTOException  {
    	String 	methodNm = "그룹핑 파라미터 셋팅 [BSlabSchSeEJB.CrnSchGrpHandling] < " + methodNms;
    	
    	JDTORecordSet jsWbBedInfo = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet jsSpacChk   = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet jsRtn   	  = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
		//레코드셋 정렬 시
		String 	szLogMsg	 = "";
		String 	szDBLogMsg	 = "";
		String 	szParmLogMsg = "";
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");			


			//------------------------------------------------------------------------------------------------------------
			// 열별 베드별로  최하단 정보를 조회해서  
			// 각 재료의 레코드에 주작업/보조작업 에 따른 HANDLING_CNT 분리작업
			//------------------------------------------------------------------------------------------------------------
			/**********************************************************
			*  각 재료의 레코드에 주작업/보조작업 에 따른 HANDLING_CNT 분리작업
			**********************************************************/	
			JDTORecord jrWbBed   	  = JDTORecordFactory.getInstance().create();
			JDTORecord jrBedSpecOvGp  = JDTORecordFactory.getInstance().create();
			
			int iHandlingCnt    = 1;
			int iHandlingCntSpec= 1;
			
//			boolean bedSpecOvGp = false;
			
			for(int Loop_i = 1; Loop_i <= jsWbBed.size(); Loop_i++) {
				
				jsWbBed.absolute(Loop_i);  
				jrWbBed = jsWbBed.getRecord();
				
				if(("3".equals(sFlag))||("TC".equals(ydSchCd.substring(2, 4))) && ("U".equals(ydSchCd.substring(6, 7)))){
					jrWbBed.setField("FLAG", 		sFlag);
					jsWbBedInfo = commDao.select(jrWbBed, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getHandlingChkTc", logId, methodNm, "주보조분리 대상");
					
				} else {
	
					jrWbBed.setField("FLAG", 		sFlag);
					jsWbBedInfo = commDao.select(jrWbBed, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getHandlingChk", logId, methodNm, "주보조분리 대상");
				}
				if (jsWbBedInfo.size() <= 0) {
					szLogMsg = methodNm+ "크레인작업재료조회 >> 조회 Data 없음";
					commUtils.printLog(logId, szLogMsg, "SL");    			
	    			return jsRtn;				
				}
				jsSpacChk   = JDTORecordFactory.getInstance().createRecordSet("Temp");
				
				int iHandlingCntChk = 1;
    			String sBEND_LOG		= "";
    			String sDCSN_MTD_LOG	= "";
    			String sAIM_RT_LOG  	= "";
    			String sYD_RULE_PL_RS_GP= "";
    			String sSTOCK_ID        = "";
    			String sYD_AIM_RT_GP    = "";
    			String sSTACK_LAYER_GP  = "";
    			String sCHARGE_LOT_NO_DIV_LOG  	= "";
    			String sYD_TO_LOC_GUIDE_LOG	= "";
    			commUtils.printParam(logId, jsWbBedInfo);
				JDTORecord jrWbBedInfo  = JDTORecordFactory.getInstance().create();
				
				for(int Loop_j = 1; Loop_j <= jsWbBedInfo.size(); Loop_j++) {
					jsWbBedInfo.absolute(Loop_j);
					jrWbBedInfo = jsWbBedInfo.getRecord();
					
					jrWbBedInfo.setResultCode(logId);		//Log ID
					jrWbBedInfo.setResultMsg(methodNm);	//Log Method Name
					
	    			iHandlingCnt   			= commUtils.paraRecChkNullInt(jrWbBedInfo, "HANDLING_CNT" );    //주작업 ,보조작업 HandingCnt

	    			sBEND_LOG				= commUtils.trim(jrWbBedInfo.getFieldString("BEND_LOG"));
	    			sDCSN_MTD_LOG			= commUtils.trim(jrWbBedInfo.getFieldString("DCSN_MTD_LOG"));
	    			sAIM_RT_LOG  		   	= commUtils.trim(jrWbBedInfo.getFieldString("AIM_RT_LOG"));
	    			sCHARGE_LOT_NO_DIV_LOG 	= commUtils.trim(jrWbBedInfo.getFieldString("CHARGE_LOT_NO_DIV_LOG"));
	    			sYD_TO_LOC_GUIDE_LOG    = commUtils.trim(jrWbBedInfo.getFieldString("YD_TO_LOC_GUIDE_LOG"));
	    			
	    			sYD_RULE_PL_RS_GP		= commUtils.trim(jrWbBedInfo.getFieldString("YD_RULE_PL_RS_GP"));
	    			sSTOCK_ID				= commUtils.trim(jrWbBedInfo.getFieldString("STOCK_ID"));
	    			sYD_AIM_RT_GP			= commUtils.trim(jrWbBedInfo.getFieldString("YD_AIM_RT_GP"));
	    			sSTACK_LAYER_GP			= commUtils.trim(jrWbBedInfo.getFieldString("STACK_LAYER_GP"));
	    			
	    			/**********************************************************
	    			*  설비SPEC 에  따른 분리 작업
	    			*  -- 동일  HandlingCnt 인 경우 check
	    			**********************************************************/	

	    			if (iHandlingCntChk == iHandlingCnt) {
	    			
	    				jsSpacChk.addRecord(jrWbBedInfo);  // 비교 JDTORECORD
	    				
		    			jrBedSpecOvGp = this.chkHandledDataCrnSpec(logId,methodNms, ydSchCd, jrCrEquip, jsSpacChk ,szParmLogMsg);
     					String sHandleRtn 	 = commUtils.trim(jrBedSpecOvGp.getFieldString("HANDLE_RTN")) ;
     					String sHandleRtnMsg = commUtils.trim(jrBedSpecOvGp.getFieldString("HANDLE_CONTENTS")) ;

		    			// 가능 
		    			if (sHandleRtn.equals("1")) {
	    					szDBLogMsg = szDBLogMsg + "단[" +sSTACK_LAYER_GP + "] "+ "재료번호[" +sSTOCK_ID + "]   "+ "HANDLING 가능"+ "\r\n";
	    					commUtils.printLog(logId, szDBLogMsg, "SL");
		    				jrWbBedInfo.setField("HANDLING_CNT_SPEC", ""+iHandlingCntSpec);
		    				
		    			} else {
		    				iHandlingCntSpec ++;
		    				jrWbBedInfo.setField("HANDLING_CNT_SPEC", ""+iHandlingCntSpec);
		    				jsSpacChk = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    				jsSpacChk.addRecord(jrWbBedInfo);
		    				szDBLogMsg = szDBLogMsg + sHandleRtnMsg + "\r\n" ;
	    					szDBLogMsg = szDBLogMsg + "단[" +sSTACK_LAYER_GP + "] "+ "재료번호[" +sSTOCK_ID + "]   "+ "HANDLING 불가:"+sHandleRtnMsg + "\r\n";
		    			}
		    			
	    			} else {
    					
	    				iHandlingCntSpec ++;
	    				jrWbBedInfo.setField("HANDLING_CNT_SPEC", ""+iHandlingCntSpec);
	    				jsSpacChk = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    				jsSpacChk.addRecord(jrWbBedInfo);
	    				
	    				if("Y".equals(sBEND_LOG)) {
	    					szDBLogMsg = szDBLogMsg + "분리 사유 :밴딩으로  분리 됨[ " +sYD_RULE_PL_RS_GP + " ]"+ "\r\n";
	    					commUtils.printLog(logId, szDBLogMsg, "SL");
	    				} else
	    				if("Y".equals(sDCSN_MTD_LOG)) {
	    					szDBLogMsg = szDBLogMsg + "분리 사유 :다음 작업이 주/보조작업으로 분리 됨[ " +sDCSN_MTD_LOG + " ]"+ "\r\n";
	    					commUtils.printLog(logId, szDBLogMsg, "SL");
	    				} else
	    				if("Y".equals(sAIM_RT_LOG)) {
	    					szDBLogMsg = szDBLogMsg + "분리 사유 :주작업 행선으로 분리 됨[ " +sYD_AIM_RT_GP + " ]"+ "\r\n";
	    					commUtils.printLog(logId, szDBLogMsg, "SL");
	    				} else
	    				if("Y".equals(sYD_TO_LOC_GUIDE_LOG)) {
	    					szDBLogMsg = szDBLogMsg + "분리 사유 :이송하차 TO위치 가이드 분리 됨[ " +sYD_AIM_RT_GP + " ]"+ "\r\n";
	    					commUtils.printLog(logId, szDBLogMsg, "SL");
	    				} else
	    				if("Y".equals(sCHARGE_LOT_NO_DIV_LOG)) {
	    					szDBLogMsg = szDBLogMsg + "분리 사유 :장입LOT로 분리 됨[ " +sYD_AIM_RT_GP + " ]"+ "\r\n";
	    					commUtils.printLog(logId, szDBLogMsg, "SL");
		    			}
	    			}
	    			jsRtn.addRecord(jrWbBedInfo);
	    			iHandlingCntChk = iHandlingCnt;
				} //for Loop_j
			} //for Loop_i
			
			commUtils.printLog(logId, "HANDLING_수:" +jsRtn.size() , "SL");
			
			if(sAPP005_YN.equals("Y") && szDBLogMsg.length() > 0) {
				
				JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
    			jrLog.setField("STOCK_ID"		, "SLAB");
    			jrLog.setField("YD_CRN_SCH_ID"	, ydWbookId);
    			jrLog.setField("YD_GP"			, "2");
    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
    			jrLog.setField("SCH_CONTENTS"	, "핸들링 분리 사유:"+ "\r\n" + szDBLogMsg + "\r\n" );

    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
			}
						
			commUtils.printLog(logId, methodNm, "S-");			

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return jsRtn;

	} //end of 	
	
	/**
	 * 		[A] 오퍼레이션명 : B열연 SLAB야드 크레인 스케줄 이송하차 그룹핑 (팔레트 (B)에서 사용
	 *  
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * 		@param JDTORecord 
	 * 		@return JDTORecordSet
	 *      @throws DAOException
	 */
	public JDTORecordSet CrnSchGrpPT( String logId, String methodNms, JDTORecord jrParamSet )throws JDTOException  {
    	String 	methodNm = " 이송하차 그룹핑 [BSlabSchSeEJB.CrnSchGrpPT] < " + methodNms;
    	
    	JDTORecordSet jsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet jsReturn   = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
		//레코드셋 정렬 시
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");			

			String ydWbookId 	= commUtils.trim(jrParamSet.getFieldString("YD_CAR_SCH_ID"));
			String ydSchCd 		= commUtils.trim(jrParamSet.getFieldString("YD_SCH_CD"));
			String ydEqpId 		= commUtils.trim(jrParamSet.getFieldString("YD_EQP_ID"));
			String stacklColGp	= commUtils.trim(jrParamSet.getFieldString("STACK_COL_GP"));
			String sAPP005_YN 	= "N";

			JDTORecord jrCrEquip = JDTORecordFactory.getInstance().create();		
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStatEqp 
			--설비상태조회 
			SELECT WPROG_STAT     AS YD_EQP_STAT
			     , WORK_MODE      AS YD_EQP_WRK_MODE
				 , STACK_MAX_QNTY	                  --적재 최대 수량
				 , STACK_MAX_WT		                  --적재 최대 중량
			     , CURR_STOP_LOC
			  FROM TB_YM_EQUIP EQ
			 WHERE EQUIP_GP = :V_YD_EQP_ID
			   AND DEL_YN    = 'N'
			*/	   
			JDTORecordSet jsCrEquip = commDao.select(jrParamSet, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStatEqp", logId, methodNm, "설비상태조회");
			
			if (jsCrEquip.size() > 0) {
				jrCrEquip = jsCrEquip.getRecord(0);
			}			
			
			//------------------------------------------------------------------------------------------------------------
			// 작업 예약을 BED별로 분리   
			// 추후  BED우선순위 변경시 용의
			//------------------------------------------------------------------------------------------------------------			
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCarMtl
			SELECT A.STACK_COL_GP 
			     , A.STACK_BED_GP 
			     , A.YD_SCH_CD
			     , A.MIN_STACK_LAYER_GP 
			     -- 이송하차를 대차로 하는지 CHECK 
			     , (SELECT DECODE(COUNT(*),0,'N','Y')
			          FROM TB_YM_EQUIP B
			             , TB_YM_TCARSCH C
			         WHERE B.EQUIP_GP = C.YD_EQP_ID
			           AND B.EQUIP_GP LIKE '2XTC0%'
			           AND SUBSTR(C.YD_CARLD_STOP_LOC,2,1) = SUBSTR(A.YD_SCH_CD,2,1)
			           AND SUBSTR(B.CARLD_SCH_CD,3,6)      = SUBSTR(A.YD_SCH_CD,3,6)
			           AND C.DEL_YN = 'N' 
			       ) AS TC_DOWN_YN   
			     , (SELECT EQUIP_GP
			          FROM TB_YM_EQUIP B
			             , TB_YM_TCARSCH C
			         WHERE B.EQUIP_GP = C.YD_EQP_ID
			           AND B.EQUIP_GP LIKE '2XTC0%'
			           AND SUBSTR(C.YD_CARLD_STOP_LOC,2,1) = SUBSTR(A.YD_SCH_CD,2,1)
			           AND SUBSTR(B.CARLD_SCH_CD,3,6)      = SUBSTR(A.YD_SCH_CD,3,6)
			           AND C.DEL_YN = 'N' 
			       ) AS EQUIP_GP   
			  FROM (
			        SELECT STACK_COL_GP 
			             , STACK_BED_GP 
			             , :V_YD_SCH_CD  AS YD_SCH_CD
			             , '01' AS MIN_STACK_LAYER_GP
			          FROM TB_YM_STACKLAYER
			         WHERE STACK_COL_GP = :V_STACK_COL_GP
			           AND STACK_BED_GP = '01'
			           AND STOCK_ID IS NOT NULL
			           AND STACK_LAYER_STAT = 'C'
			          GROUP BY STACK_COL_GP, STACK_BED_GP
				   ) A
			*/	   
			JDTORecordSet jsWbBed = commDao.select(jrParamSet, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCarMtl", logId, methodNm, "작업예약 BED우선순 정보");
			commUtils.printParam(logId, jsWbBed);
			
			if(jsWbBed.size() > 0) {
				
				String sTcDownYn = commUtils.trim(jsWbBed.getRecord(0).getFieldString("TC_DOWN_YN"));
				// 이송하차를 대차로 함
				if("Y".equals(sTcDownYn)) {
					jsHandling = this.CrnSchGrpHandling( logId, methodNms, "3", ydSchCd, sAPP005_YN, ydWbookId, jsWbBed, jrCrEquip);
				} else {
					jsHandling = this.CrnSchGrpHandling( logId, methodNms, "2", ydSchCd, sAPP005_YN, ydWbookId, jsWbBed, jrCrEquip);
				}
				commUtils.printLog(logId, "HANDLING_수:" +jsHandling.size() , "SL");
				
				
				JDTORecord jrReturn  = JDTORecordFactory.getInstance().create();
				
				for(int Loop_i = 1; Loop_i <= jsHandling.size(); Loop_i++) {
				
					jsHandling.absolute(Loop_i);  
					jrReturn = jsHandling.getRecord();
					jsReturn.addRecord(jrReturn);
				}
			}	
			commUtils.printLog(logId, methodNm, "S-");			

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return jsReturn;

	} //end of 	
	

	/**
	 * 오퍼레이션명 : B열연 SLAB야드 크레인 스케줄 GROUPING PARAMETER DATA SETTING
	 *  
	 * @param  String     szEqpId, szSchCd, rsMinWrkBookMtl, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public boolean CrnSchGrpHandlingAbleChk( String logId, String methodNms, String ydSchCd, JDTORecordSet jsWbBed,JDTORecord jrCrEquip) throws JDTOException  {
    	String 	methodNm = "동내이적 그룹핑 가능여부 CHECK [BSlabSchSeEJB.CrnSchGrpHandlingAbleChk] < " + methodNms;
    	
    	JDTORecordSet jsWbBedInfo = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet jsSpacChk   = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
		//레코드셋 정렬 시
		String 	szLogMsg	= "";
		String 	szParmLogMsg	= "";
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");			


			//------------------------------------------------------------------------------------------------------------
			// 열별 베드별로  최하단 정보를 조회해서  
			// 각 재료의 레코드에 주작업/보조작업 에 따른 HANDLING_CNT 분리작업
			//------------------------------------------------------------------------------------------------------------
			/**********************************************************
			*  각 재료의 레코드에 주작업/보조작업 에 따른 HANDLING_CNT 분리작업
			**********************************************************/	
			JDTORecord jrWbBed   	  = JDTORecordFactory.getInstance().create();
			JDTORecord jrBedSpecOvGp  = JDTORecordFactory.getInstance().create();
			
			int iHandlingCnt    = 1;
			
			for(int Loop_i = 1; Loop_i <= jsWbBed.size(); Loop_i++) {
				
				jsWbBed.absolute(Loop_i);  
				jrWbBed = jsWbBed.getRecord();
				
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getHandlingAbleChk 
				WITH TABLE_TMP AS (
				SELECT A.*
				     , ROWNUM AS RNUM
				     , DECODE(YD_UP_COLL_SEQ, 0, 'W','S') AS YD_TO_LOC_DCSN_MTD

				  FROM
				    (
				    SELECT A.STACK_COL_GP 
				         , A.STACK_BED_GP 
				         , A.STACK_LAYER_GP 
				         , A.STOCK_ID 
				         , (SELECT COUNT(*)
				              FROM TB_YM_WRKBOOK    WB
				                 , TB_YM_WRKBOOKMTL WM
				             WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				               AND WM.STOCK_ID    = B.STOCK_ID
				               AND WB.DEL_YN = 'N'
				               AND WM.DEL_YN = 'N'
				               AND ROWNUM = 1
				           ) AS YD_UP_COLL_SEQ            
				         , NVL(B.CHARGE_LOT_NO, 'KK')               AS CHK_CHARGE_LOT_NO
				         , C.SLAB_T            
				         , C.SLAB_W            
				         , C.SLAB_LEN    
				         , C.CAL_SLAB_WT                            AS SLAB_WT      
				         , C.CURR_PROG_CD                           AS STL_PROG_CD     
				         , C.SPEC_ABBSYM       
				         , C.ORD_YEOJAE_GP       
				         , NVL(B.YD_AIM_RT_GP,B.STOCK_MOVE_TERM)    AS YD_AIM_RT_GP
				         , B.YD_RULE_PL_RS_GP                       AS YD_RULE_PL_RS_GP -- BENDING 구분
				         , :V_YD_SCH_CD                             AS YD_SCH_CD
				         , NVL((SELECT WB.YD_TO_LOC_GUIDE
				                  FROM TB_YM_WRKBOOK    WB
				                     , TB_YM_WRKBOOKMTL WM
				                 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                   AND WM.STOCK_ID    = B.STOCK_ID
				                   AND WB.DEL_YN = 'N'
				                   AND WM.DEL_YN = 'N'
				                   AND ROWNUM = 1
				              ),'Y') 
				           AS CHK_YD_TO_LOC_GUIDE
				        
				         , NVL((SELECT NVL(CHARGE_LOT_NO_DIV_YN,'Y')
				                  FROM TB_YM_WRKBOOK WB  
				                     , TB_YM_WRKBOOKMTL WM  
				                 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                   AND WM.STOCK_ID    = A.STOCK_ID
				                   AND WB.DEL_YN = 'N'
				                   AND WM.DEL_YN = 'N'
				                   AND ROWNUM = 1
				              ),'Y') 
				           AS DIV_CHARGE_LOT_NO_YN 
				         , (SELECT WB.YD_WBOOK_ID
				              FROM TB_YM_WRKBOOK WB  
				                 , TB_YM_WRKBOOKMTL WM  
				             WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				               AND WM.STOCK_ID    = A.STOCK_ID
				               AND WB.DEL_YN = 'N'
				               AND WM.DEL_YN = 'N'
				               AND ROWNUM = 1
				           ) 
				           AS YD_WBOOK_ID           
				      FROM TB_YM_STACKLAYER A 
				         , TB_YM_STOCK    B 
				         , USRYDA.VW_YD_SLABCOMM C
				     WHERE A.STOCK_ID = B.STOCK_ID  
				       AND A.STOCK_ID = C.SLAB_NO(+) 
				       AND A.STACK_COL_GP     = :V_STACK_COL_GP 
				       AND A.STACK_BED_GP     = :V_STACK_BED_GP   
				       AND A.STACK_LAYER_GP  >= :V_MIN_STACK_LAYER_GP
				       AND A.STACK_LAYER_GP  <= NVL(:V_MAX_STACK_LAYER_GP,'20')
				       AND A.STACK_LAYER_STAT = 'C'  
				       AND A.DEL_YN = 'N'
				     ORDER BY STACK_LAYER_GP DESC 
				    ) A
				)
				SELECT AA.*
				     , ABS((CASE WHEN AA.CNT = 0           
				                 THEN SUM(AA.CNT) OVER() - SUM(AA.CNT) OVER(ORDER BY AA.RNUM ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)
				            ELSE      SUM(AA.CNT) OVER() - SUM(AA.CNT) OVER(ORDER BY AA.RNUM ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) + 1 
				            END ) - SUM(CNT) OVER()) + 1  
				       AS HANDLING_CNT 
				  FROM (
				         --BENDING 은 1매씩
				         --주/보조분리
				         --행선분리:주작업만 행선 분리 한다
				        SELECT KK.* 
				             , CASE WHEN NVL(YD_RULE_PL_RS_GP,'N') != 'Y'        --BENDING 여부:'Y'BENDING 'S':스카핑
				                     AND LEAD(YD_TO_LOC_DCSN_MTD) OVER (ORDER BY STACK_LAYER_GP DESC) 
				                         = YD_TO_LOC_DCSN_MTD --주/보조분리
				                     --행선분리:주작업만 행선 분리 한다
				                     AND CASE WHEN YD_TO_LOC_DCSN_MTD = 'W'                                          THEN 'T'  
				                              WHEN LEAD(YD_AIM_RT_GP)        OVER (ORDER BY  STACK_LAYER_GP DESC) 
				                                   = YD_AIM_RT_GP                                                    THEN 'T' -- 모음
				                              ELSE 'F' END  = 'T'        
				                     --장입순번이 틀리면 분리
				                     --주작업이고 장입이면
				                     AND CASE WHEN YD_TO_LOC_DCSN_MTD   = 'S' AND  SUBSTR(YD_SCH_CD,3,2) = 'WB'      THEN 'T'
				                              WHEN DIV_CHARGE_LOT_NO_YN = 'N'                                        THEN 'T'
				                              WHEN LEAD(CHK_CHARGE_LOT_NO)   OVER (ORDER BY STACK_LAYER_GP DESC) 
				                                   = CHK_CHARGE_LOT_NO                                               THEN 'T'
				                              ELSE 'F' END  = 'T'        
				                     -- 이송하차 TO위치 가이드 분리
				                     AND CASE WHEN YD_TO_LOC_DCSN_MTD = 'W'                                          THEN 'T'
				                              WHEN LEAD(CHK_YD_TO_LOC_GUIDE) OVER (ORDER BY STACK_LAYER_GP DESC) 
				                                       = CHK_YD_TO_LOC_GUIDE                                         THEN 'T'
				                              ELSE 'F' END  = 'T'        
				                     -- 주여 분리
				                     AND CASE WHEN YD_TO_LOC_DCSN_MTD = 'W'                                          THEN 'T'
				                              WHEN LENGTH(CHK_YD_TO_LOC_GUIDE) = 8                                   THEN 'T'
				                              WHEN LEAD(ORD_YEOJAE_GP)       OVER (ORDER BY STACK_LAYER_GP DESC) 
				                                   = ORD_YEOJAE_GP                                                   THEN 'T'
				                              ELSE 'F' END  = 'T'        
				                    THEN 0
				                    ELSE 1 END  AS CNT
				             , (SELECT DTL_ITM1
				                 FROM USRYMA.TB_YM_RULE
				                WHERE REPR_CD_GP = 'YM102'
				                  AND ITEM       = CASE WHEN SUBSTR(YD_SCH_CD,3,2) = 'WB' AND YD_TO_LOC_DCSN_MTD = 'S'  THEN 'WB'
				                                        ELSE 'YD' END 
				                ) AS WID_DIF 
				        FROM TABLE_TMP KK
				       ) AA
				 ORDER BY RNUM 
				*/ 
				jsWbBedInfo = commDao.select(jrWbBed, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getHandlingAbleChk", logId, methodNm, "주보조분리 대상");
				if (jsWbBedInfo.size() <= 0) {
					szLogMsg = methodNm+ "크레인작업재료조회 >> 조회 Data 없음";
					commUtils.printLog(logId, szLogMsg, "SL");    			
	    			return false;				
				}
				jsSpacChk   = JDTORecordFactory.getInstance().createRecordSet("Temp");
				
				int iHandlingCntChk = 1;
    			String sSTOCK_ID        = "";
    			String sSTACK_LAYER_GP  = "";
    			commUtils.printParam(logId, jsWbBedInfo);
				JDTORecord jrWbBedInfo  = JDTORecordFactory.getInstance().create();
				
				for(int Loop_j = 1; Loop_j <= jsWbBedInfo.size(); Loop_j++) {
					jsWbBedInfo.absolute(Loop_j);
					jrWbBedInfo = jsWbBedInfo.getRecord();
					
					jrWbBedInfo.setResultCode(logId);		//Log ID
					jrWbBedInfo.setResultMsg(methodNm);	//Log Method Name
					
	    			iHandlingCnt   			= commUtils.paraRecChkNullInt(jrWbBedInfo, "HANDLING_CNT" );    //주작업 ,보조작업 HandingCnt

	    			sSTOCK_ID				= commUtils.trim(jrWbBedInfo.getFieldString("STOCK_ID"));
	    			sSTACK_LAYER_GP			= commUtils.trim(jrWbBedInfo.getFieldString("STACK_LAYER_GP"));
	    			
	    			/**********************************************************
	    			*  설비SPEC 에  따른 분리 작업
	    			*  -- 동일  HandlingCnt 인 경우 check
	    			**********************************************************/	

	    			if (iHandlingCntChk == iHandlingCnt) {
	    			
	    				jsSpacChk.addRecord(jrWbBedInfo);  // 비교 JDTORECORD
	    				
		    			jrBedSpecOvGp = this.chkHandledDataCrnSpec(logId,methodNms, ydSchCd, jrCrEquip, jsSpacChk ,szParmLogMsg);
     					String sHandleRtn 	 = commUtils.trim(jrBedSpecOvGp.getFieldString("HANDLE_RTN")) ;
		    			// 가능 
		    			if (sHandleRtn.equals("1")) {
	    					commUtils.printLog(logId,  "단[" +sSTACK_LAYER_GP + "] "+ "재료번호[" +sSTOCK_ID + "]   "+ "HANDLING 가능", "SL");
		    				
		    			} else {
	    					commUtils.printLog(logId,  "단[" +sSTACK_LAYER_GP + "] "+ "재료번호[" +sSTOCK_ID + "]   "+ "HANDLING 불가", "SL");
		    				return false;
		    			}
	    			} else {
    					commUtils.printLog(logId,  "단[" +sSTACK_LAYER_GP + "] "+ "재료번호[" +sSTOCK_ID + "]   "+ "HANDLING 불가", "SL");
	    				return false;
	    			}
				} 
			} 
			
			commUtils.printLog(logId, methodNm, "S-");			

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;

	} //end of 	
	/**
	 * 오퍼레이션명 : B열연 SLAB야드 크레인 스케줄 GROUPING PARAMETER DATA SETTING
	 *  
	 * @param  String     szEqpId, szSchCd, rsMinWrkBookMtl, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public boolean CrnSchGrpHandlingAbleChkPT( String logId, String methodNms, String ydSchCd, JDTORecordSet jsWbBed,JDTORecord jrCrEquip) throws JDTOException  {
    	String 	methodNm = "이송하차 그룹핑 가능여부 CHECK [BSlabSchSeEJB.CrnSchGrpHandlingAbleChkPT] < " + methodNms;
    	
    	JDTORecordSet jsWbBedInfo = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet jsSpacChk   = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
		//레코드셋 정렬 시
		String 	szLogMsg	= "";
		String 	szParmLogMsg	= "";
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");			

			String sAPP021_YN = YmComm.BCoilApplyYn("APP021","2","1");   //slab_log여부
			commUtils.printLog(logId,  "이송하차신규 적용:" + sAPP021_YN, "SL");
			//------------------------------------------------------------------------------------------------------------
			// 열별 베드별로  최하단 정보를 조회해서  
			// 각 재료의 레코드에 주작업/보조작업 에 따른 HANDLING_CNT 분리작업
			//------------------------------------------------------------------------------------------------------------
			/**********************************************************
			*  각 재료의 레코드에 주작업/보조작업 에 따른 HANDLING_CNT 분리작업
			**********************************************************/	
			JDTORecord jrWbBed   	  = JDTORecordFactory.getInstance().create();
			JDTORecord jrBedSpecOvGp  = JDTORecordFactory.getInstance().create();
			
			int iHandlingCnt    = 1;
			
			for(int Loop_i = 1; Loop_i <= jsWbBed.size(); Loop_i++) {
				
				jsWbBed.absolute(Loop_i);  
				jrWbBed = jsWbBed.getRecord();
				
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getHandlingAbleChkPT 
				WITH TABLE_TMP AS (
				SELECT A.*
				     , ROWNUM AS RNUM
				     , DECODE(YD_UP_COLL_SEQ, 0, 'W','S') AS YD_TO_LOC_DCSN_MTD

				  FROM
				    (
				    SELECT A.STACK_COL_GP 
				         , A.STACK_BED_GP 
				         , A.STACK_LAYER_GP 
				         , A.STOCK_ID 
				         , (SELECT COUNT(*)
				              FROM TB_YM_WRKBOOK    WB
				                 , TB_YM_WRKBOOKMTL WM
				             WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				               AND WM.STOCK_ID    = B.STOCK_ID
				               AND WB.DEL_YN = 'N'
				               AND WM.DEL_YN = 'N'
				               AND ROWNUM = 1
				           ) AS YD_UP_COLL_SEQ            
				         , NVL(B.CHARGE_LOT_NO, 'KK')               AS CHK_CHARGE_LOT_NO
				         , C.SLAB_T            
				         , C.SLAB_W            
				         , C.SLAB_LEN    
				         , C.CAL_SLAB_WT                            AS SLAB_WT      
				         , C.CURR_PROG_CD                           AS STL_PROG_CD     
				         , C.SPEC_ABBSYM       
				         , C.ORD_YEOJAE_GP       
				         , NVL(B.YD_AIM_RT_GP,B.STOCK_MOVE_TERM)    AS YD_AIM_RT_GP
				         , B.YD_RULE_PL_RS_GP                       AS YD_RULE_PL_RS_GP -- BENDING 구분
				         , :V_YD_SCH_CD                             AS YD_SCH_CD
				         , NVL((SELECT WB.YD_TO_LOC_GUIDE
				                  FROM TB_YM_WRKBOOK    WB
				                     , TB_YM_WRKBOOKMTL WM
				                 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                   AND WM.STOCK_ID    = B.STOCK_ID
				                   AND WB.DEL_YN = 'N'
				                   AND WM.DEL_YN = 'N'
				                   AND ROWNUM = 1
				              ),'Y') 
				           AS CHK_YD_TO_LOC_GUIDE
				        
				         , NVL((SELECT NVL(CHARGE_LOT_NO_DIV_YN,'Y')
				                  FROM TB_YM_WRKBOOK WB  
				                     , TB_YM_WRKBOOKMTL WM  
				                 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                   AND WM.STOCK_ID    = A.STOCK_ID
				                   AND WB.DEL_YN = 'N'
				                   AND WM.DEL_YN = 'N'
				                   AND ROWNUM = 1
				              ),'Y') 
				           AS DIV_CHARGE_LOT_NO_YN 
				         , (SELECT WB.YD_WBOOK_ID
				              FROM TB_YM_WRKBOOK WB  
				                 , TB_YM_WRKBOOKMTL WM  
				             WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				               AND WM.STOCK_ID    = A.STOCK_ID
				               AND WB.DEL_YN = 'N'
				               AND WM.DEL_YN = 'N'
				               AND ROWNUM = 1
				           ) 
				           AS YD_WBOOK_ID 
				         , NVL((CASE WHEN (SELECT RMTL_ASK_THK_FINAL 
				                             FROM VW_YD_ORDTWSEARCH 
				                            WHERE SLAB_NO = A.STOCK_ID) = '0' THEN D.ORD_CONV_T
				                ELSE (SELECT RMTL_ASK_THK_FINAL  FROM VW_YD_ORDTWSEARCH WHERE SLAB_NO = A.STOCK_ID)  END ),0)  AS ORD_CONV_T
				         , NVL((CASE WHEN (SELECT RMTL_PROD_WTH_FINAL FROM VW_YD_ORDTWSEARCH WHERE SLAB_NO = A.STOCK_ID) = '0' THEN D.ORD_CONV_W
				                     ELSE (SELECT RMTL_PROD_WTH_FINAL FROM VW_YD_ORDTWSEARCH WHERE SLAB_NO = A.STOCK_ID)  END ),0)  AS ORD_CONV_W            
				      FROM TB_YM_STACKLAYER A 
				         , TB_YM_STOCK    B 
				         , USRYDA.VW_YD_SLABCOMM C
				         , TB_PT_OSCOMM     D
				         , (SELECT :V_YD_SCH_CD AS YD_SCH_CD FROM DUAL) E         
				     WHERE A.STOCK_ID = B.STOCK_ID  
				       AND A.STOCK_ID = C.SLAB_NO(+) 
				       AND C.ORD_NO   = D.ORD_NO(+)
				       AND C.ORD_DTL  = D.ORD_DTL(+)
				       AND A.STACK_COL_GP     = :V_STACK_COL_GP 
				       AND A.STACK_BED_GP     = :V_STACK_BED_GP   
				       AND A.STACK_LAYER_GP  >= :V_MIN_STACK_LAYER_GP
				       AND A.STACK_LAYER_GP  <= NVL(:V_MAX_STACK_LAYER_GP,'20')
				       AND A.STACK_LAYER_STAT = 'C'  
				       AND A.DEL_YN = 'N'
				     ORDER BY STACK_LAYER_GP DESC 
				    ) A
				)
				SELECT AA.*
				     , ABS((CASE WHEN AA.CNT = 0           
				                 THEN SUM(AA.CNT) OVER() - SUM(AA.CNT) OVER(ORDER BY AA.RNUM ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)
				            ELSE      SUM(AA.CNT) OVER() - SUM(AA.CNT) OVER(ORDER BY AA.RNUM ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) + 1 
				            END ) - SUM(CNT) OVER()) + 1  
				       AS HANDLING_CNT 
				  FROM (
				         --BENDING 은 1매씩
				         --주/보조분리
				         --행선분리:주작업만 행선 분리 한다
				        SELECT KK.* 
				             , CASE WHEN NVL(YD_RULE_PL_RS_GP,'N') = 'N' --BENDING 여부:'Y'BENDING 'S':스카핑
				                     AND LEAD(NVL(YD_RULE_PL_RS_GP,'N')) OVER (ORDER BY STACK_LAYER_GP DESC) = NVL(YD_RULE_PL_RS_GP,'N') 
				                     AND LEAD(YD_TO_LOC_DCSN_MTD)        OVER (ORDER BY STACK_LAYER_GP DESC) = YD_TO_LOC_DCSN_MTD --주/보조분리
				                     --행선분리:주작업만 행선 분리 한다
				                     AND CASE WHEN LENGTH(CHK_YD_TO_LOC_GUIDE) = 8                                   THEN 'T'
				                              WHEN YD_TO_LOC_DCSN_MTD = 'W'                                          THEN 'T'
				                              WHEN LEAD(YD_AIM_RT_GP)            OVER (ORDER BY STACK_LAYER_GP DESC) 
				                                   = YD_AIM_RT_GP                                                    THEN 'T' -- 모음
				                              ELSE 'F' END  = 'T'        
				                     --장입순번이 틀리면 분리
				                     --주작업이고 장입이면
				                     AND CASE WHEN LENGTH(CHK_YD_TO_LOC_GUIDE) = 8                                   THEN 'T'
				                              WHEN YD_TO_LOC_DCSN_MTD = 'S' AND SUBSTR(YD_SCH_CD,3,2) = 'WB'         THEN 'T'
				                              WHEN DIV_CHARGE_LOT_NO_YN = 'N'                                        THEN 'T'
				                              WHEN LEAD(CHK_CHARGE_LOT_NO)       OVER (ORDER BY STACK_LAYER_GP DESC) 
				                                   = CHK_CHARGE_LOT_NO                                               THEN 'T'
				                              ELSE 'F' END  = 'T'        
				--                    -- 이송하차 TO위치 가이드 분리
				                     AND CASE WHEN YD_TO_LOC_DCSN_MTD = 'W'                                          THEN 'T'
				                              WHEN LEAD(NVL(CHK_YD_TO_LOC_GUIDE,'1'))     OVER (ORDER BY STACK_LAYER_GP DESC) 
				                                   = NVL(CHK_YD_TO_LOC_GUIDE,'1')                                             THEN 'T'
				                              ELSE 'F' END  = 'T'        
				--                    -- 주여 분리
				                     AND CASE WHEN LENGTH(CHK_YD_TO_LOC_GUIDE) = 8                                   THEN 'T'
				                              WHEN YD_TO_LOC_DCSN_MTD = 'W'                                          THEN 'T'
				                              WHEN LEAD(ORD_YEOJAE_GP)           OVER (ORDER BY STACK_LAYER_GP DESC) 
				                                   = ORD_YEOJAE_GP                                                   THEN 'T'
				                              ELSE 'F' END  = 'T'        
				--                    -- 주문두께 폭 
				                     AND CASE WHEN LENGTH(CHK_YD_TO_LOC_GUIDE) = 8                                   THEN 'T'
				                              WHEN YD_AIM_RT_GP  = 'D3'                                              THEN 'T'
				                              WHEN YD_TO_LOC_DCSN_MTD = 'S' AND SUBSTR(YD_SCH_CD,3,2) = 'WB'         THEN 'T'
				                              WHEN LEAD(ORD_CONV_T||ORD_CONV_W)  OVER (ORDER BY STACK_LAYER_GP DESC) 
				                                   = ORD_CONV_T||ORD_CONV_W                                          THEN 'T'
				                              ELSE 'F' END  = 'T'        
				                    THEN 0
				                    ELSE 1 END  AS CNT
				             , (SELECT DTL_ITM1
				                 FROM USRYMA.TB_YM_RULE
				                WHERE REPR_CD_GP = 'YM102'
				                  AND ITEM       = CASE WHEN SUBSTR(YD_SCH_CD,3,2) = 'WB' AND YD_TO_LOC_DCSN_MTD = 'S'  THEN 'WB'
				                                        ELSE 'YD' END 
				                ) AS WID_DIF 
				        FROM TABLE_TMP KK
				       ) AA
				 ORDER BY RNUM 
				*/ 				
				if(sAPP021_YN.equals("Y")) {
					jsWbBedInfo = commDao.select(jrWbBed, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getHandlingAbleChkPTNew", logId, methodNm, "주보조분리 대상");
				} else {
					jsWbBedInfo = commDao.select(jrWbBed, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getHandlingAbleChkPT", logId, methodNm, "주보조분리 대상");
				}
				if (jsWbBedInfo.size() <= 0) {
					szLogMsg = methodNm+ "크레인작업재료조회 >> 조회 Data 없음";
					commUtils.printLog(logId, szLogMsg, "SL");    			
	    			return false;				
				}
				jsSpacChk   = JDTORecordFactory.getInstance().createRecordSet("Temp");
				
				int iHandlingCntChk = 1;
    			String sSTOCK_ID        = "";
    			String sSTACK_LAYER_GP  = "";
    			commUtils.printParam(logId, jsWbBedInfo);
				JDTORecord jrWbBedInfo  = JDTORecordFactory.getInstance().create();
				
				for(int Loop_j = 1; Loop_j <= jsWbBedInfo.size(); Loop_j++) {
					jsWbBedInfo.absolute(Loop_j);
					jrWbBedInfo = jsWbBedInfo.getRecord();
					
					jrWbBedInfo.setResultCode(logId);		//Log ID
					jrWbBedInfo.setResultMsg(methodNm);	//Log Method Name
					
	    			iHandlingCnt   			= commUtils.paraRecChkNullInt(jrWbBedInfo, "HANDLING_CNT" );    //주작업 ,보조작업 HandingCnt

	    			sSTOCK_ID				= commUtils.trim(jrWbBedInfo.getFieldString("STOCK_ID"));
	    			sSTACK_LAYER_GP			= commUtils.trim(jrWbBedInfo.getFieldString("STACK_LAYER_GP"));
	    			
	    			/**********************************************************
	    			*  설비SPEC 에  따른 분리 작업
	    			*  -- 동일  HandlingCnt 인 경우 check
	    			**********************************************************/	

	    			if (iHandlingCntChk == iHandlingCnt) {
	    				//상위 쿼리에서 HANDLING_CNT 값이 "1" 인 경우는 추가로 2매 작업 가능한지 체크를 한다.
	    				// 폭이 1750 이상인지, 줄양이 53200 초과인지, 상단 폭이 하단 폭 보다 50 초과하여 넓으면 1매 작업 한다.
	    				jsSpacChk.addRecord(jrWbBedInfo);  // 비교 JDTORECORD
	    				
		    			jrBedSpecOvGp = this.chkHandledDataCrnSpec(logId,methodNms, ydSchCd, jrCrEquip, jsSpacChk ,szParmLogMsg);
     					String sHandleRtn 	 = commUtils.trim(jrBedSpecOvGp.getFieldString("HANDLE_RTN")) ;
		    			// 가능 
		    			if (sHandleRtn.equals("1")) {
	    					commUtils.printLog(logId,  "단[" +sSTACK_LAYER_GP + "] "+ "재료번호[" +sSTOCK_ID + "]   "+ "HANDLING 가능", "SL");
		    				
		    			} else {
	    					commUtils.printLog(logId,  "단[" +sSTACK_LAYER_GP + "] "+ "재료번호[" +sSTOCK_ID + "]   "+ "HANDLING 불가", "SL");
		    				return false;
		    			}
	    			} else {
	    				//상위 쿼리 에서 2매 작업 불가로 판단한 경우
    					commUtils.printLog(logId,  "단[" +sSTACK_LAYER_GP + "] "+ "재료번호[" +sSTOCK_ID + "]   쿼리결과 HANDLING_CNT = "+ iHandlingCnt + "로 HANDLING 불가", "SL");
	    				return false;
	    			}
				}
				
				//2매작업 가능할 때 상단 한단 중심점 차이를 체크한다.
				if(jsWbBedInfo.size() > 1) {
					
					//이송하차이고 해당 동 형상장치 사용구분이 'Y' 일때만 체크한다.
					String sYD_SCH_CD = jsWbBedInfo.getRecord(0).getFieldString("YD_SCH_CD");
					String sBAY_GP 	  = sYD_SCH_CD.substring(1,2);
					//String sDBAY_FS_YN = jsWbBedInfo.getRecord(0).getFieldString("DBAY_FS_YN");
					String sYM2006_FA_YN = YmComm.BCoilApplyYn("YM2006","2",sBAY_GP);
					
					commUtils.printLog(logId, ">>> 스케줄CD : " +  sYD_SCH_CD + " , " + sBAY_GP + " 동 형상장치 사용여부 : " + sYM2006_FA_YN  , "SL");
					
					if("Y".equals(sYM2006_FA_YN)) {
					
						String  sYM2012_2_WB_LEN_YN = YmComm.BCoilApplyYn("YM2012","2","WB_LEN");
						String  sYM2012_2_WB_LEN_YN2  = YmComm.BCoilApplyYn("YM2012","2","WB_LEN2");
						commUtils.printLog(logId,  "==========[[[ SLAB 이송하차시 X좌표 중심 값 차이와 길이를 체크하여 1매작업 실행여부  :" + sYM2012_2_WB_LEN_YN + " ]]]============", "SL");
						
						if("Y".equals(sYM2012_2_WB_LEN_YN)) {
							
							double dUpperSlabLen = Double.parseDouble(commUtils.nvl(jsWbBedInfo.getRecord(0).getFieldString("SLAB_LEN"),"0")); //상단 Slab 길이
							double dLowerSlabLen = Double.parseDouble(commUtils.nvl(jsWbBedInfo.getRecord(1).getFieldString("SLAB_LEN"),"0")); //하단 Slab 길이
							commUtils.printLog(logId, "=======:::: 상단 SLAB 길이 : " +  dUpperSlabLen , "SL");
							commUtils.printLog(logId, "=======:::: 하단 SLAB 길이 : " +  dLowerSlabLen , "SL");
							double dUpperSlabXaxis = Double.parseDouble(commUtils.nvl(jsWbBedInfo.getRecord(0).getFieldString("WGT_CENTER_XAXIS"),"0")); //상단 Slab X좌표
							double dLowerSlabXaxis = Double.parseDouble(commUtils.nvl(jsWbBedInfo.getRecord(1).getFieldString("WGT_CENTER_XAXIS"),"0")); //하단 Slab X좌표
							commUtils.printLog(logId, "=======:::: 상단 SLAB X축 : " +  dUpperSlabXaxis , "SL");
							commUtils.printLog(logId, "=======:::: 하단 SLAB X축 : " +  dLowerSlabXaxis , "SL");
							double dRuleSlabLen = Double.parseDouble(commUtils.nvl(jsWbBedInfo.getRecord(0).getFieldString("WB_LEN"),"0")); //길이기준
							commUtils.printLog(logId, "=======:::: 2매가능길이기준 : " +  dRuleSlabLen , "SL");
							
							if(dUpperSlabLen > 0
							&& dLowerSlabLen > 0
							&& dUpperSlabXaxis > 0 
							&& dLowerSlabXaxis > 0
							&& dRuleSlabLen > 0	) {
								double dGapXaxis = Math.abs(dUpperSlabXaxis - dLowerSlabXaxis);
								double dHalfLen = dUpperSlabLen/2;
								double dHalfRuleLen = dRuleSlabLen/2;
								double dGapSlabLen = Math.abs(dUpperSlabLen - dLowerSlabLen);
								
								if(dHalfLen + dGapXaxis > dHalfRuleLen) {
									commUtils.printLog(logId, "=======:::: 길이/2(" + dHalfLen + ") + X축 차이(" + dGapXaxis + ") < 길이기준값/2(" +  dHalfRuleLen + ") 에 의해서 1매 작업!!!", "SL");
									return false;
								}
								//2023.11.14 열연 김기훈 주임님 요청 --REQ202311508459
								//슬라브 길이차이 1미터 이상시 중심좌표차이 500 이상 나면 한매씩
								else if("Y".equals(sYM2012_2_WB_LEN_YN2) && dGapSlabLen >= 1000 && dGapXaxis >=500) {
									commUtils.printLog(logId, "=======:::: 길이 차이(" + dGapSlabLen + ") + X축 차이(" + dGapXaxis + ")  에 의해서 1매 작업!!!", "SL");
									return false;
								}
							}
						
						}
					}
				}
				
			} 
			
			commUtils.printLog(logId, methodNm, "S-");			

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;

	} //end of 	
		
	/**
	 * 오퍼레이션명 : B열연 SLAB야드 크레인 스케줄 GROUPING PARAMETER DATA SETTING
	 *  
	 * @param  String     szEqpId, szSchCd, rsMinWrkBookMtl, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public boolean CrnSchGrpHandlingAbleChkTC( String logId, String methodNms, String ydSchCd, JDTORecordSet jsWbBed,JDTORecord jrCrEquip) throws JDTOException  {
    	String 	methodNm = "대차상차 그룹핑 가능여부 CHECK [BSlabSchSeEJB.CrnSchGrpHandlingAbleChkTC] < " + methodNms;
    	
    	JDTORecordSet jsWbBedInfo = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet jsSpacChk   = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet jsRtn   	  = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
		//레코드셋 정렬 시
		String 	szLogMsg	= "";
		String 	szDBLogMsg	= "";
		String 	szParmLogMsg	= "";
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");			


			//------------------------------------------------------------------------------------------------------------
			// 열별 베드별로  최하단 정보를 조회해서  
			// 각 재료의 레코드에 주작업/보조작업 에 따른 HANDLING_CNT 분리작업
			//------------------------------------------------------------------------------------------------------------
			/**********************************************************
			*  각 재료의 레코드에 주작업/보조작업 에 따른 HANDLING_CNT 분리작업
			**********************************************************/	
			JDTORecord jrWbBed   	  = JDTORecordFactory.getInstance().create();
			JDTORecord jrBedSpecOvGp  = JDTORecordFactory.getInstance().create();
			
			int iHandlingCnt    = 1;
			
			for(int Loop_i = 1; Loop_i <= jsWbBed.size(); Loop_i++) {
				
				jsWbBed.absolute(Loop_i);  
				jrWbBed = jsWbBed.getRecord();
				
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getHandlingAbleChkTC 
				WITH TABLE_TMP AS (
				SELECT A.*
				     , B.EQUIP_GP
				     , B.TC_LYR_SH
				     , B.TC_MAX_QNTY
				     , B.TC_TIMES
				     , B.UP_LOC
				     , ROWNUM AS RNUM
				     , DECODE(YD_UP_COLL_SEQ, 0, 'W','S') AS YD_TO_LOC_DCSN_MTD

				  FROM
				    (
				    SELECT '1' AS TABLE_ROW
				         , A.STACK_COL_GP 
				         , A.STACK_BED_GP 
				         , A.STACK_LAYER_GP 
				         , A.STOCK_ID 
				         , (SELECT COUNT(*)
				              FROM TB_YM_WRKBOOK    WB
				                 , TB_YM_WRKBOOKMTL WM
				             WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				               AND WM.STOCK_ID    = B.STOCK_ID
				               AND WB.DEL_YN = 'N'
				               AND WM.DEL_YN = 'N'
				               AND ROWNUM = 1
				           )                AS YD_UP_COLL_SEQ            
				         , C.SLAB_T            
				         , C.SLAB_W            
				         , C.SLAB_LEN          
				         , C.CAL_SLAB_WT    AS SLAB_WT      
				         , :V_YD_SCH_CD     AS YD_SCH_CD
				      FROM TB_YM_STACKLAYER A 
				         , TB_YM_STOCK    B 
				         , USRYDA.VW_YD_SLABCOMM C
				         
				     WHERE A.STOCK_ID = B.STOCK_ID  
				       AND A.STOCK_ID = C.SLAB_NO(+) 
				       AND A.STACK_COL_GP     = :V_STACK_COL_GP 
				       AND A.STACK_BED_GP     = :V_STACK_BED_GP   
				       AND A.STACK_LAYER_GP  >= :V_MIN_STACK_LAYER_GP
				       AND A.STACK_LAYER_GP  <= NVL(:V_MAX_STACK_LAYER_GP,'20')
				       AND A.STACK_LAYER_STAT = 'C'  
				       AND A.DEL_YN = 'N'
				     ORDER BY STACK_LAYER_GP DESC 
				    ) A
				    , (
				       SELECT '1' AS TABLE_ROW
				            , EQ.EQUIP_GP
				            , (SELECT MAX(ITEM)
				                 FROM TB_YM_RULE
				                WHERE REPR_CD_GP = 'TCAR01'
				                  AND DEL_YN     = 'N'
				                  AND DTL_ITM2   = SUBSTR(:V_STACK_COL_GP,2,1)
				                  AND DTL_ITM1   = EQ.EQUIP_GP
				              ) UP_LOC                             
				            , (SELECT COUNT(*)
				                 FROM TB_YM_STACKLAYER
				                WHERE STACK_COL_GP IN (
				                                        SELECT MAX(ITEM)
				                                          FROM TB_YM_RULE
				                                         WHERE REPR_CD_GP = 'TCAR01'
				                                           AND DEL_YN     = 'N'
				                                           AND DTL_ITM2   = SUBSTR(:V_STACK_COL_GP,2,1)
				                                           AND DTL_ITM1   = EQ.EQUIP_GP
				                                      )     
				                  AND STOCK_ID IS NOT NULL
				                  AND DEL_YN = 'N'
				                  AND STACK_LAYER_STAT = 'C' ) 
				              AS TC_LYR_SH --현재 대차적치매수
				                  
				            , EQ.STACK_MAX_QNTY  AS TC_MAX_QNTY --설비기준 매수 
				            , EQ.CTS_RELAY_YN    AS TC_TIMES    --설비기준 횟수

				         FROM TB_YM_EQUIP   EQ 
				        WHERE EQ.EQUIP_GP = :V_YD_WRK_PLAN_TCAR
				     ) B    
				  WHERE A.TABLE_ROW = B.TABLE_ROW(+)    
				)
				SELECT AA.*
				     , ABS((CASE WHEN AA.CNT = 0           
				                 THEN SUM(AA.CNT) OVER() - SUM(AA.CNT) OVER(ORDER BY AA.RNUM ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)
				            ELSE      SUM(AA.CNT) OVER() - SUM(AA.CNT) OVER(ORDER BY AA.RNUM ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) + 1 
				            END ) - SUM(CNT) OVER()) + 1  
				       AS HANDLING_CNT 
				  FROM (
				        SELECT KK.*
				             , CASE WHEN TC_TIMES  IS NULL THEN 1
				                    WHEN TC_LYR_SH IS NULL THEN 1
				                    WHEN TC_TIMES >= TC_LYR_SH + 2 THEN 0
				                    
				                    ELSE 1 END  AS CNT
				             , (SELECT DTL_ITM1
				                 FROM USRYMA.TB_YM_RULE
				                WHERE REPR_CD_GP = 'YM102'
				                  AND ITEM       = CASE WHEN SUBSTR(YD_SCH_CD,3,2) = 'WB' AND YD_TO_LOC_DCSN_MTD = 'S'  THEN 'WB'
				                                        ELSE 'YD' END 
				                ) AS WID_DIF 
				        FROM TABLE_TMP KK
				 
				       ) AA
				 ORDER BY RNUM 
				*/ 
				jsWbBedInfo = commDao.select(jrWbBed, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getHandlingAbleChkTC", logId, methodNm, "주보조분리 대상");
				if (jsWbBedInfo.size() <= 0) {
					szLogMsg = methodNm+ "크레인작업재료조회 >> 조회 Data 없음";
					commUtils.printLog(logId, szLogMsg, "SL");    			
	    			return false;				
				}
				jsSpacChk   = JDTORecordFactory.getInstance().createRecordSet("Temp");
				
				int iHandlingCntChk = 1;
    			String sSTOCK_ID        = "";
    			String sSTACK_LAYER_GP  = "";
    			commUtils.printParam(logId, jsWbBedInfo);
				JDTORecord jrWbBedInfo  = JDTORecordFactory.getInstance().create();
				
				for(int Loop_j = 1; Loop_j <= jsWbBedInfo.size(); Loop_j++) {
					jsWbBedInfo.absolute(Loop_j);
					jrWbBedInfo = jsWbBedInfo.getRecord();
					
					jrWbBedInfo.setResultCode(logId);		//Log ID
					jrWbBedInfo.setResultMsg(methodNm);	//Log Method Name
					
	    			iHandlingCnt   			= commUtils.paraRecChkNullInt(jrWbBedInfo, "HANDLING_CNT" );    //주작업 ,보조작업 HandingCnt

	    			sSTOCK_ID				= commUtils.trim(jrWbBedInfo.getFieldString("STOCK_ID"));
	    			sSTACK_LAYER_GP			= commUtils.trim(jrWbBedInfo.getFieldString("STACK_LAYER_GP"));
	    			
	    			/**********************************************************
	    			*  설비SPEC 에  따른 분리 작업
	    			*  -- 동일  HandlingCnt 인 경우 check
	    			**********************************************************/	

	    			if (iHandlingCntChk == iHandlingCnt) {
	    			
	    				jsSpacChk.addRecord(jrWbBedInfo);  // 비교 JDTORECORD
	    				
		    			jrBedSpecOvGp = this.chkHandledDataCrnSpec(logId,methodNms, ydSchCd, jrCrEquip, jsSpacChk ,szParmLogMsg);
     					String sHandleRtn 	 = commUtils.trim(jrBedSpecOvGp.getFieldString("HANDLE_RTN")) ;
		    			// 가능 
		    			if (sHandleRtn.equals("1")) {
	    					commUtils.printLog(logId,  "단[" +sSTACK_LAYER_GP + "] "+ "재료번호[" +sSTOCK_ID + "]   "+ "HANDLING 가능", "SL");
		    				
		    			} else {
	    					commUtils.printLog(logId,  "단[" +sSTACK_LAYER_GP + "] "+ "재료번호[" +sSTOCK_ID + "]   "+ "HANDLING 불가", "SL");
		    				return false;
		    			}
	    			} else {
    					commUtils.printLog(logId,  "단[" +sSTACK_LAYER_GP + "] "+ "재료번호[" +sSTOCK_ID + "]   "+ "HANDLING 불가", "SL");
	    				return false;
	    			}
				} 
			} 
			
			commUtils.printLog(logId, methodNm, "S-");			

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;

	} //end of 	
	
	/**
	 *      [A] 오퍼레이션명 : 저장품 행선 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	*/
	public void updCrnSchStock(JDTORecord jrParam) throws DAOException {
		String methodNm = "저장품 행선 등록[BSlabSchSeEJB.updCrnSchStock] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		
		String ydWbookId  = commUtils.trim(jrParam.getFieldString("YD_WBOOK_ID"  )); //야드작업예약ID
		String ydSchCd    = commUtils.trim(jrParam.getFieldString("YD_SCH_CD"    )); //야드스케쥴코드
		String ydEqpId    = commUtils.trim(jrParam.getFieldString("YD_EQP_ID"    )); //야드설비ID
		String modifier   = commUtils.trim(jrParam.getFieldString("MODIFIER"    )); //야드설비ID

		try {
			
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWrkBookMtl 
			SELECT A.YD_WBOOK_ID
			     , B.STOCK_ID
			     , C.STOCK_MOVE_TERM
			     , C.YD_AIM_RT_GP
			  FROM TB_YM_WRKBOOK A
			     , TB_YM_WRKBOOKMTL B
			     , TB_YM_STOCK      C
			 WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
			   AND A.YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND B.STOCK_ID    = C.STOCK_ID
			   AND A.DEL_YN = 'N'      
			   AND B.DEL_YN = 'N'   
			*/
			JDTORecordSet jsWbMtlInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWrkBookMtl", logId, methodNm, "작업 재료 READ");
			
			JDTORecord jrWbMtlInfo  = JDTORecordFactory.getInstance().create();
			
			String ydAimRtGp = "";
			for(int Loop_j = 1; Loop_j <= jsWbMtlInfo.size(); Loop_j++) {
				jsWbMtlInfo.absolute(Loop_j);
				jrWbMtlInfo = jsWbMtlInfo.getRecord();
				
				jrWbMtlInfo.setResultCode(logId);		//Log ID
				jrWbMtlInfo.setResultMsg(methodNm);	//Log Method Name
				
    			if("".equals(commUtils.trim(jrWbMtlInfo.getFieldString("STOCK_MOVE_TERM")))) {
    				
    				ydAimRtGp = bslabComm.getStockMoveTerm(commUtils.trim(jrWbMtlInfo.getFieldString("STOCK_ID"))) ;
    				
    				if(!"".equals(ydAimRtGp)) {
	    				jrParam.setField("STOCK_MOVE_TERM" , ydAimRtGp);
	    				jrParam.setField("YD_AIM_RT_GP"    , ydAimRtGp);
	    				jrParam.setField("STOCK_ID"        , commUtils.trim(jrWbMtlInfo.getFieldString("STOCK_ID")));
	    				
	    				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockTransInfo
	    				UPDATE TB_YM_STOCK
	    				   SET MODIFIER   = :V_MODIFIER
	    				     , MOD_DDTT   = SYSDATE 
	    				     , STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
	    				     , YD_AIM_RT_GP    = :V_YD_AIM_RT_GP
	    				WHERE STOCK_ID = :V_STOCK_ID
	    				*/
	    				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockTransInfo", logId, methodNm, "TB_YM_STOCK 갱신");
    				}	
    			}
			}
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
		
	/**
	 * 오퍼레이션명 : B열연 SLAB야드 크레인 스케줄 GROUPING PARAMETER DATA SETTING
	 *  
	 * @param  String     szEqpId, szSchCd, rsMinWrkBookMtl, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public JDTORecordSet CrnSchGrpHandlingPT( String logId, String methodNms, String sFlag ,String ydSchCd, String sAPP005_YN, String ydWbookId,  JDTORecordSet jsWbBed,JDTORecord jrCrEquip) throws JDTOException  {
    	String 	methodNm = "이송하차 파라미터 셋팅 [BSlabSchSeEJB.CrnSchGrpHandlingPT] < " + methodNms;
    	
    	JDTORecordSet jsWbBedInfo = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet jsSpacChk   = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet jsRtn   	  = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
		//레코드셋 정렬 시
		String 	szLogMsg	 = "";
		String 	szDBLogMsg	 = "";
		String 	szParmLogMsg = "";
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");			


			//------------------------------------------------------------------------------------------------------------
			// 열별 베드별로  최하단 정보를 조회해서  
			// 각 재료의 레코드에 주작업/보조작업 에 따른 HANDLING_CNT 분리작업
			//------------------------------------------------------------------------------------------------------------
			/**********************************************************
			*  각 재료의 레코드에 주작업/보조작업 에 따른 HANDLING_CNT 분리작업
			**********************************************************/	
			JDTORecord jrWbBed   	  = JDTORecordFactory.getInstance().create();
			JDTORecord jrBedSpecOvGp  = JDTORecordFactory.getInstance().create();
			
			int iHandlingCnt    = 1;
			int iHandlingCntSpec= 1;
			
//			boolean bedSpecOvGp = false;
			
			for(int Loop_i = 1; Loop_i <= jsWbBed.size(); Loop_i++) {
				
				jsWbBed.absolute(Loop_i);  
				jrWbBed = jsWbBed.getRecord();
				
				
				jrWbBed.setField("FLAG", 		sFlag);
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getHandlingChkPT 
				-- LEAD 함수는 해당 파티션내의 바로 다음 Row 의 데이터를 참조
				-- LAG  함수는 해당 파티션내의 바로 위의 Row 데이터를 참조
				-- FLAG : 1 스케쥴 결정시
				-- FLAG : 2 이송하차시 
				WITH TABLE_TMP AS (
				SELECT A.*
				     , ROWNUM AS RNUM
				     , DECODE(YD_UP_COLL_SEQ, 0, 'W','S') AS YD_TO_LOC_DCSN_MTD

				  FROM
				    (
				    SELECT A.STACK_COL_GP 
				         , A.STACK_BED_GP 
				         , A.STACK_LAYER_GP 
				         , A.STOCK_ID 
				         , CASE WHEN NVL(:V_FLAG,'1') = '1' THEN
				              (SELECT COUNT(*)  
				                  FROM TB_YM_WRKBOOKMTL  
				                 WHERE YD_WBOOK_ID    = :V_YD_WBOOK_ID  
				                   AND STOCK_ID       = A.STOCK_ID
				                   AND DEL_YN = 'N'
				               ) 
				           ELSE
				                1     
				           END AS YD_UP_COLL_SEQ            
				         , B.CHARGE_LOT_NO     
				         , NVL(B.CHARGE_LOT_NO, 'KK') AS  CHK_CHARGE_LOT_NO
				         , C.SLAB_T            
				         , C.SLAB_W            
				         , C.SLAB_LEN          
				         , C.CAL_SLAB_WT    AS SLAB_WT      
				         , C.CURR_PROG_CD   AS STL_PROG_CD     
				         , C.SPEC_ABBSYM       
				         , C.ORD_YEOJAE_GP       
				         , ''               AS YD_STK_LOT_TP
				         , ''               AS YD_STK_LOT_CD
				         , ''               AS HCR_GP
				         , ''               AS YD_MTL_ITEM
				         , NVL(B.YD_AIM_RT_GP,B.STOCK_MOVE_TERM)   AS YD_AIM_RT_GP
				         , B.YD_RULE_PL_RS_GP                      AS YD_RULE_PL_RS_GP -- BENDING 구분
				         , E.YD_SCH_CD     AS YD_SCH_CD
				         , CASE WHEN NVL(:V_FLAG,'1') = '1' THEN
				                NVL((SELECT WB.YD_TO_LOC_GUIDE
				                       FROM TB_YM_WRKBOOK    WB
				                          , TB_YM_WRKBOOKMTL WM
				                      WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                        AND WM.STOCK_ID    = B.STOCK_ID
				                        AND WB.DEL_YN = 'N'
				                        AND WM.DEL_YN = 'N'
				                        AND ROWNUM = 1
				                   ),'Y') 
				           ELSE
				         -- 이송하차시 
				                'K' 
				           END  AS CHK_YD_TO_LOC_GUIDE

				         , CASE WHEN NVL(:V_FLAG,'1') = '1' THEN
				                (SELECT WB.YD_TO_LOC_GUIDE
				                   FROM TB_YM_WRKBOOK    WB
				                      , TB_YM_WRKBOOKMTL WM
				                  WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                    AND WM.STOCK_ID    = B.STOCK_ID
				                    AND WB.DEL_YN = 'N'
				                    AND WM.DEL_YN = 'N'
				                   AND ROWNUM = 1
				                )
				           ELSE
				         -- 이송하차
				                '' 
				           END  AS YD_TO_LOC_GUIDE

				         , CASE WHEN NVL(:V_FLAG,'1') = '1' THEN
				                NVL((SELECT NVL(CHARGE_LOT_NO_DIV_YN,'Y')
				                       FROM TB_YM_WRKBOOK
				                      WHERE YD_WBOOK_ID    = :V_YD_WBOOK_ID  
				                        AND DEL_YN = 'N'
				                   ),'Y') 
				           ELSE
				                'Y'      
				           END  AS DIV_CHARGE_LOT_NO_YN 
				         , NVL((CASE WHEN (SELECT RMTL_ASK_THK_FINAL 
				                             FROM VW_YD_ORDTWSEARCH 
				                            WHERE SLAB_NO = A.STOCK_ID) = '0' THEN D.ORD_CONV_T
				                ELSE (SELECT RMTL_ASK_THK_FINAL  FROM VW_YD_ORDTWSEARCH WHERE SLAB_NO = A.STOCK_ID)  END ),0)  AS ORD_CONV_T
				         , NVL((CASE WHEN (SELECT RMTL_PROD_WTH_FINAL FROM VW_YD_ORDTWSEARCH WHERE SLAB_NO = A.STOCK_ID) = '0' THEN D.ORD_CONV_W
				                     ELSE (SELECT RMTL_PROD_WTH_FINAL FROM VW_YD_ORDTWSEARCH WHERE SLAB_NO = A.STOCK_ID)  END ),0)  AS ORD_CONV_W   
				         , CASE WHEN SUBSTR(:V_YD_SCH_CD,3,6) = 'CT01UM' THEN
				                            (SELECT COUNT(*) 
				                               FROM TB_YM_STACKLAYER 
				                               WHERE STACK_COL_GP IN (
				                            
				                                              SELECT STACK_COL_GP 
				                                                FROM TB_YM_SCHLOCSRCH SS
				                                               WHERE SS.YD_SCH_CD = E.YD_SCH_CD 
				                                                 AND SS.YD_ROUTE_GP = NVL(B.YD_AIM_RT_GP,B.STOCK_MOVE_TERM)
				                                                 AND SS.DEL_YN = 'N'
				                                                 AND SUBSTR(SS.STACK_COL_GP,3,2) = 'CT'
				                                                 AND ROWNUM = 1
				                                         )  
				                                 AND STACK_BED_GP = '01'
				                                 AND STACK_LAYER_ACTIVE_STAT = 'E'
				                                 AND STACK_LAYER_STAT = 'E'
				                               )
				                ELSE 2 END CTC_LOC_CNT
				-- OPEN 된 BED 개수
				         , (SELECT COUNT(DISTINCT(STACK_COL_GP||STACK_BED_GP))
				              FROM TB_YM_STACKLAYER A1
				             WHERE A1.STACK_COL_GP LIKE '2%'
				               AND A1.STACK_LAYER_ACTIVE_STAT = 'E'
				               AND A1.STACK_LAYER_STAT = 'E'
				               AND A1.STACK_COL_GP  IN ( 
				                                    SELECT SS.STACK_COL_GP
				                                      FROM TB_YM_SCHLOCSRCH       SS
				                                         , TB_YM_SCHLOCSRCHPRIOR  SP   
				                                         , TB_YM_STACKCOL         SC
				                                     WHERE SS.YD_SCH_CD    = SP.YD_SCH_CD(+)
				                                       AND SS.YD_ROUTE_GP  = SP.YD_ROUTE_GP(+)
				                                       AND SS.YD_SCH_CD    = :V_YD_SCH_CD
				                                       AND SS.YD_ROUTE_GP  = NVL(B.YD_AIM_RT_GP,B.STOCK_MOVE_TERM)
				                                       AND SS.STACK_COL_GP = SC.STACK_COL_GP
				                                       AND SC.STACK_COL_ACTIVE_STAT = 'L'
				                                       AND SS.DEL_YN = 'N'
				                                   )      
				               AND NOT EXISTS (SELECT 1                     
				                                 FROM TB_YM_CRNSCH 
				                                WHERE DEL_YN = 'N'             
				                                  AND (    YD_UP_WO_LOC = A1.STACK_COL_GP||A1.STACK_BED_GP 
				                                        OR YD_DN_WO_LOC = A1.STACK_COL_GP||A1.STACK_BED_GP
				                                      )
				                               UNION ALL
				                               SELECT 1                     
				                                 FROM TB_YM_WRKBOOK   AA
				                                    , TB_YM_WRKBOOKMTL BB
				                                    , TB_YM_STACKLAYER CC
				                                WHERE AA.YD_WBOOK_ID = BB.YD_WBOOK_ID
				                                  AND AA.DEL_YN = 'N'             
				                                  AND BB.DEL_YN = 'N'    
				                                  AND BB.STOCK_ID = CC.STOCK_ID
				                                  
				                                  AND (    CC.STACK_COL_GP||CC.STACK_BED_GP = A1.STACK_COL_GP||A1.STACK_BED_GP 
				                                        OR CC.STACK_COL_GP||CC.STACK_BED_GP = A1.STACK_COL_GP||A1.STACK_BED_GP
				                                      )
				                             )                                    
				           ) OPEN_BED_CNT
				           -- OPEN 된 BED중 최대값
				         , (SELECT MAX(COUNT(*))
				              FROM TB_YM_STACKLAYER A1
				             WHERE A1.STACK_COL_GP LIKE '2%'
				               AND A1.STACK_LAYER_ACTIVE_STAT = 'E'
				               AND A1.STACK_LAYER_STAT = 'E'
				               AND A1.STACK_COL_GP  IN ( 
				                                        SELECT SS.STACK_COL_GP
				                                          FROM TB_YM_SCHLOCSRCH       SS
				                                             , TB_YM_SCHLOCSRCHPRIOR  SP   
				                                             , TB_YM_STACKCOL         SC
				                                         WHERE SS.YD_SCH_CD    = SP.YD_SCH_CD(+)
				                                           AND SS.YD_ROUTE_GP  = SP.YD_ROUTE_GP(+)
				                                           AND SS.YD_SCH_CD    = :V_YD_SCH_CD
				                                           AND SS.YD_ROUTE_GP  = NVL(B.YD_AIM_RT_GP,B.STOCK_MOVE_TERM)
				                                           AND SS.STACK_COL_GP = SC.STACK_COL_GP
				                                           AND SC.STACK_COL_ACTIVE_STAT = 'L'
				                                           AND SS.DEL_YN = 'N'
				                                       ) 
				               AND NOT EXISTS (SELECT 1                     
				                                 FROM TB_YM_CRNSCH 
				                                WHERE DEL_YN = 'N'             
				                                  AND (    YD_UP_WO_LOC = A1.STACK_COL_GP||A1.STACK_BED_GP 
				                                        OR YD_DN_WO_LOC = A1.STACK_COL_GP||A1.STACK_BED_GP
				                                      )
				                               UNION ALL
				                               SELECT 1                     
				                                 FROM TB_YM_WRKBOOK   AA
				                                    , TB_YM_WRKBOOKMTL BB
				                                    , TB_YM_STACKLAYER CC
				                                WHERE AA.YD_WBOOK_ID = BB.YD_WBOOK_ID
				                                  AND AA.DEL_YN = 'N'             
				                                  AND BB.DEL_YN = 'N'    
				                                  AND BB.STOCK_ID = CC.STOCK_ID
				                                  
				                                  AND (    CC.STACK_COL_GP||CC.STACK_BED_GP = A1.STACK_COL_GP||A1.STACK_BED_GP 
				                                        OR CC.STACK_COL_GP||CC.STACK_BED_GP = A1.STACK_COL_GP||A1.STACK_BED_GP
				                                      )
				                             )                                        
				             GROUP BY A1.STACK_COL_GP,A1.STACK_BED_GP
				           ) OPEN_MAX_DAN                                
				      FROM TB_YM_STACKLAYER A 
				         , TB_YM_STOCK      B 
				         , USRYDA.VW_YD_SLABCOMM C
				         , TB_PT_OSCOMM     D
				         , (SELECT :V_YD_SCH_CD AS YD_SCH_CD FROM DUAL) E
				     WHERE A.STOCK_ID = B.STOCK_ID  
				       AND A.STOCK_ID = C.SLAB_NO(+) 
				       AND C.ORD_NO   = D.ORD_NO(+)
				       AND C.ORD_DTL  = D.ORD_DTL(+)
				       AND A.STACK_COL_GP     = :V_STACK_COL_GP     
				       AND A.STACK_BED_GP     = :V_STACK_BED_GP   
				       AND A.STACK_LAYER_GP  >= :V_MIN_STACK_LAYER_GP
				       AND A.STACK_LAYER_STAT = 'C'  
				       AND A.DEL_YN = 'N'
				    ORDER BY STACK_LAYER_GP DESC 
				    ) A
				)
				SELECT AA.*
				     , ABS((CASE WHEN AA.CNT = 0           
				                 THEN SUM(AA.CNT) OVER() - SUM(AA.CNT) OVER(ORDER BY AA.RNUM ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)
				            ELSE      SUM(AA.CNT) OVER() - SUM(AA.CNT) OVER(ORDER BY AA.RNUM ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) + 1 
				            END ) - SUM(CNT) OVER()) + 1  
				       AS HANDLING_CNT 
				  FROM (
				         --BENDING 은 1매씩
				         --주/보조분리
				         --행선분리:주작업만 행선 분리 한다
				        SELECT KK.* 
				             , CASE WHEN NVL(YD_RULE_PL_RS_GP,'N') = 'N' --BENDING 여부:'Y'BENDING 'S':스카핑
				                     AND LEAD(NVL(YD_RULE_PL_RS_GP,'N')) OVER (ORDER BY STACK_LAYER_GP DESC) = NVL(YD_RULE_PL_RS_GP,'N') 
				                     AND LEAD(YD_TO_LOC_DCSN_MTD)        OVER (ORDER BY STACK_LAYER_GP DESC) = YD_TO_LOC_DCSN_MTD --주/보조분리
				                         -- 이송하차 시 OPEN MAX DAN  CHECK 함
				                     AND CASE WHEN SUBSTR(YD_SCH_CD,3,6) IN ('PT02LM','TR02LM') AND OPEN_MAX_DAN = 1 THEN 'F'
				                              ELSE 'T' END  = 'T'   
				                     --행선분리:주작업만 행선 분리 한다
				                     AND CASE WHEN LENGTH(CHK_YD_TO_LOC_GUIDE) = 8                                   THEN 'T'
				                              WHEN SUBSTR(YD_SCH_CD,3,6) IN ('PT02LM','TR02LM') AND OPEN_BED_CNT = 1 THEN 'T'
				                              WHEN YD_TO_LOC_DCSN_MTD = 'W'                                          THEN 'T'
				                              WHEN LEAD(YD_AIM_RT_GP)            OVER (ORDER BY STACK_LAYER_GP DESC) 
				                                   = YD_AIM_RT_GP                                                    THEN 'T' -- 모음
				                              ELSE 'F' END  = 'T'        
				                     --장입순번이 틀리면 분리
				                     --주작업이고 장입이면
				                     AND CASE WHEN YD_TO_LOC_DCSN_MTD = 'S' AND SUBSTR(YD_SCH_CD,3,2) = 'WB'         THEN 'T'
				                              -- 8자리 인 경우 A,C 동 대차하차일 경우 장입순번이 틀리면 분리 
				                              WHEN LENGTH(CHK_YD_TO_LOC_GUIDE) = 8 THEN
				                                   CASE WHEN SUBSTR(YD_SCH_CD,2,1) IN ('A', 'C' )
				                                         AND SUBSTR(YD_SCH_CD,3,6) IN ('TC11LM', 'TC22LM' )
				                                         AND LEAD(CHK_CHARGE_LOT_NO)  OVER (ORDER BY STACK_LAYER_GP DESC) 
				                                              < CHK_CHARGE_LOT_NO    THEN 'F'
				                                        ELSE 'T' END    
				                              WHEN SUBSTR(YD_SCH_CD,3,6) IN ('PT02LM','TR02LM') AND OPEN_BED_CNT = 1 THEN 'T'
				                              WHEN DIV_CHARGE_LOT_NO_YN = 'N'                                        THEN 'T'
				                              WHEN LEAD(CHK_CHARGE_LOT_NO)       OVER (ORDER BY STACK_LAYER_GP DESC) 
				                                   = CHK_CHARGE_LOT_NO                                               THEN 'T'
				                              ELSE 'F' END  = 'T'        
				--                    -- 이송하차 TO위치 가이드 분리
				                     AND CASE WHEN YD_TO_LOC_DCSN_MTD = 'W'                                          THEN 'T'
				                              WHEN LEAD(CHK_YD_TO_LOC_GUIDE)     OVER (ORDER BY STACK_LAYER_GP DESC) 
				                                   = CHK_YD_TO_LOC_GUIDE                                             THEN 'T'
				                              ELSE 'F' END  = 'T'        
				--                    -- 주여 분리
				                     AND CASE WHEN LENGTH(CHK_YD_TO_LOC_GUIDE) = 8                                   THEN 'T'
				                              WHEN SUBSTR(YD_SCH_CD,3,6) IN ('PT02LM','TR02LM') AND OPEN_BED_CNT = 1 THEN 'T'
				                              WHEN YD_TO_LOC_DCSN_MTD = 'W'                                          THEN 'T'
				                              WHEN LEAD(ORD_YEOJAE_GP)           OVER (ORDER BY STACK_LAYER_GP DESC) 
				                                   = ORD_YEOJAE_GP                                                   THEN 'T'
				                              ELSE 'F' END  = 'T'        
				--                    -- 주문두께 폭 (핸드 스카핑재 제외)
				                     AND CASE WHEN LENGTH(CHK_YD_TO_LOC_GUIDE) = 8                                   THEN 'T'
				                              WHEN SUBSTR(YD_SCH_CD,3,6) IN ('PT02LM','TR02LM') AND OPEN_BED_CNT = 1 THEN 'T'
				                              WHEN YD_TO_LOC_DCSN_MTD = 'S' AND SUBSTR(YD_SCH_CD,3,2) = 'WB'         THEN 'T'
				                              WHEN YD_AIM_RT_GP       = 'D3'                                         THEN 'T'
				                              --이송상차 제외
				                              WHEN SUBSTR(YD_SCH_CD,3,6) = 'PT02UM'                                  THEN 'T'
				                              WHEN LEAD(ORD_CONV_T||ORD_CONV_W)  OVER (ORDER BY STACK_LAYER_GP DESC) 
				                                   = ORD_CONV_T||ORD_CONV_W                                          THEN 'T'
				                              ELSE 'F' END  = 'T'        
				--                    -- CTC 위치는 TO_위치 매수 CHECK
				                     AND CASE WHEN CTC_LOC_CNT = 2                                                   THEN 'T'
				                              ELSE 'F' END  = 'T'        
				                    THEN 0
				                    ELSE 1 END  AS CNT
				   
				             , (SELECT DTL_ITM1
				                 FROM USRYMA.TB_YM_RULE
				                WHERE REPR_CD_GP = 'YM102'
				                  AND ITEM = CASE WHEN SUBSTR(YD_SCH_CD,3,2) = 'WB' AND YD_TO_LOC_DCSN_MTD = 'S'  THEN 'WB'
				                                  ELSE 'YD' END 
				                ) AS WID_DIF 
				 
				   -- LOG 용
				             , CASE WHEN NVL(YD_RULE_PL_RS_GP,'N') = 'Y' 
				                    THEN 'Y' ELSE 'N' END BEND_LOG      --BENDING 여부:'Y'BENDING 'S':스카핑
				             , CASE WHEN LEAD(YD_TO_LOC_DCSN_MTD) OVER (ORDER BY STACK_LAYER_GP DESC) <> YD_TO_LOC_DCSN_MTD 
				                    THEN 'Y' ELSE 'N' END DCSN_MTD_LOG  --주/보조분리
				             , CASE WHEN YD_TO_LOC_DCSN_MTD != 'W'  AND LEAD(YD_AIM_RT_GP) OVER (ORDER BY  STACK_LAYER_GP DESC) != YD_AIM_RT_GP
				                    THEN 'Y' ELSE 'N' END AIM_RT_LOG    --행선분리:주작업만 행선 분리 한다
				              --장입순번이 틀리면 분리
				             , CASE WHEN LEAD(CHK_CHARGE_LOT_NO) OVER (ORDER BY  STACK_LAYER_GP DESC)   != CHK_CHARGE_LOT_NO THEN 'Y'
				                    WHEN DIV_CHARGE_LOT_NO_YN = 'Y'  
				                    THEN 'Y' ELSE 'N' END CHARGE_LOT_NO_DIV_LOG   --장입순번이 틀리면 분리
				              --이송하차 TO 위치 가이드가 틀리면 분리
				             , CASE WHEN LEAD(CHK_YD_TO_LOC_GUIDE) OVER (ORDER BY  STACK_LAYER_GP DESC) != CHK_YD_TO_LOC_GUIDE 
				                    THEN 'Y' ELSE 'N' END YD_TO_LOC_GUIDE_LOG    
				              --주여가 틀리면 분리
				             , CASE WHEN LEAD(ORD_YEOJAE_GP) OVER (ORDER BY  STACK_LAYER_GP DESC) != ORD_YEOJAE_GP 
				                    THEN 'Y' ELSE 'N' END ORD_YEOJAE_GP_LOG    
				          FROM TABLE_TMP KK
				       ) AA
				 ORDER BY RNUM 
				 */
				jsWbBedInfo = commDao.select(jrWbBed, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getHandlingChkPT", logId, methodNm, "주보조분리 대상");
				
				if (jsWbBedInfo.size() <= 0) {
					szLogMsg = methodNm+ "크레인작업재료조회 >> 조회 Data 없음";
					commUtils.printLog(logId, szLogMsg, "SL");    			
	    			return jsRtn;				
				}
				jsSpacChk   = JDTORecordFactory.getInstance().createRecordSet("Temp");
				
				int iHandlingCntChk = 1;
    			String sBEND_LOG		= "";
    			String sDCSN_MTD_LOG	= "";
    			String sAIM_RT_LOG  	= "";
    			String sYD_RULE_PL_RS_GP= "";
    			String sSTOCK_ID        = "";
    			String sYD_AIM_RT_GP    = "";
    			String sSTACK_LAYER_GP  = "";
    			String sCHARGE_LOT_NO_DIV_LOG  	= "";
    			String sYD_TO_LOC_GUIDE_LOG	= "";
    			commUtils.printParam(logId, jsWbBedInfo);
				JDTORecord jrWbBedInfo  = JDTORecordFactory.getInstance().create();
				
				for(int Loop_j = 1; Loop_j <= jsWbBedInfo.size(); Loop_j++) {
					jsWbBedInfo.absolute(Loop_j);
					jrWbBedInfo = jsWbBedInfo.getRecord();
					
					jrWbBedInfo.setResultCode(logId);		//Log ID
					jrWbBedInfo.setResultMsg(methodNm);	//Log Method Name
					
	    			iHandlingCnt   			= commUtils.paraRecChkNullInt(jrWbBedInfo, "HANDLING_CNT" );    //주작업 ,보조작업 HandingCnt

	    			sBEND_LOG				= commUtils.trim(jrWbBedInfo.getFieldString("BEND_LOG"));
	    			sDCSN_MTD_LOG			= commUtils.trim(jrWbBedInfo.getFieldString("DCSN_MTD_LOG"));
	    			sAIM_RT_LOG  		   	= commUtils.trim(jrWbBedInfo.getFieldString("AIM_RT_LOG"));
	    			sCHARGE_LOT_NO_DIV_LOG 	= commUtils.trim(jrWbBedInfo.getFieldString("CHARGE_LOT_NO_DIV_LOG"));
	    			sYD_TO_LOC_GUIDE_LOG    = commUtils.trim(jrWbBedInfo.getFieldString("YD_TO_LOC_GUIDE_LOG"));
	    			
	    			sYD_RULE_PL_RS_GP		= commUtils.trim(jrWbBedInfo.getFieldString("YD_RULE_PL_RS_GP"));
	    			sSTOCK_ID				= commUtils.trim(jrWbBedInfo.getFieldString("STOCK_ID"));
	    			sYD_AIM_RT_GP			= commUtils.trim(jrWbBedInfo.getFieldString("YD_AIM_RT_GP"));
	    			sSTACK_LAYER_GP			= commUtils.trim(jrWbBedInfo.getFieldString("STACK_LAYER_GP"));
	    			
	    			/**********************************************************
	    			*  설비SPEC 에  따른 분리 작업
	    			*  -- 동일  HandlingCnt 인 경우 check
	    			**********************************************************/	

	    			if (iHandlingCntChk == iHandlingCnt) {
	    			
	    				jsSpacChk.addRecord(jrWbBedInfo);  // 비교 JDTORECORD
	    				
		    			jrBedSpecOvGp = this.chkHandledDataCrnSpec(logId,methodNms, ydSchCd, jrCrEquip, jsSpacChk ,szParmLogMsg);
     					String sHandleRtn 	 = commUtils.trim(jrBedSpecOvGp.getFieldString("HANDLE_RTN")) ;
     					String sHandleRtnMsg = commUtils.trim(jrBedSpecOvGp.getFieldString("HANDLE_CONTENTS")) ;

		    			// 가능 
		    			if (sHandleRtn.equals("1")) {
	    					szDBLogMsg = szDBLogMsg + "단[" +sSTACK_LAYER_GP + "] "+ "재료번호[" +sSTOCK_ID + "]   "+ "HANDLING 가능"+ "\r\n";
	    					commUtils.printLog(logId, szDBLogMsg, "SL");
		    				jrWbBedInfo.setField("HANDLING_CNT_SPEC", ""+iHandlingCntSpec);
		    				
		    			} else {
		    				iHandlingCntSpec ++;
		    				jrWbBedInfo.setField("HANDLING_CNT_SPEC", ""+iHandlingCntSpec);
		    				jsSpacChk = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    				jsSpacChk.addRecord(jrWbBedInfo);
		    				szDBLogMsg = szDBLogMsg + sHandleRtnMsg + "\r\n" ;
	    					szDBLogMsg = szDBLogMsg + "단[" +sSTACK_LAYER_GP + "] "+ "재료번호[" +sSTOCK_ID + "]   "+ "HANDLING 불가:"+sHandleRtnMsg + "\r\n";
		    			}
		    			
	    			} else {
    					
	    				iHandlingCntSpec ++;
	    				jrWbBedInfo.setField("HANDLING_CNT_SPEC", ""+iHandlingCntSpec);
	    				jsSpacChk = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    				jsSpacChk.addRecord(jrWbBedInfo);
	    				
	    				if("Y".equals(sBEND_LOG)) {
	    					szDBLogMsg = szDBLogMsg + "분리 사유 :밴딩으로  분리 됨[ " +sYD_RULE_PL_RS_GP + " ]"+ "\r\n";
	    					commUtils.printLog(logId, szDBLogMsg, "SL");
	    				} else
	    				if("Y".equals(sDCSN_MTD_LOG)) {
	    					szDBLogMsg = szDBLogMsg + "분리 사유 :다음 작업이 주/보조작업으로 분리 됨[ " +sDCSN_MTD_LOG + " ]"+ "\r\n";
	    					commUtils.printLog(logId, szDBLogMsg, "SL");
	    				} else
	    				if("Y".equals(sAIM_RT_LOG)) {
	    					szDBLogMsg = szDBLogMsg + "분리 사유 :주작업 행선으로 분리 됨[ " +sYD_AIM_RT_GP + " ]"+ "\r\n";
	    					commUtils.printLog(logId, szDBLogMsg, "SL");
	    				} else
	    				if("Y".equals(sYD_TO_LOC_GUIDE_LOG)) {
	    					szDBLogMsg = szDBLogMsg + "분리 사유 :이송하차 TO위치 가이드 분리 됨[ " +sYD_AIM_RT_GP + " ]"+ "\r\n";
	    					commUtils.printLog(logId, szDBLogMsg, "SL");
	    				} else
	    				if("Y".equals(sCHARGE_LOT_NO_DIV_LOG)) {
	    					szDBLogMsg = szDBLogMsg + "분리 사유 :장입LOT로 분리 됨[ " +sYD_AIM_RT_GP + " ]"+ "\r\n";
	    					commUtils.printLog(logId, szDBLogMsg, "SL");
		    			}
	    			}
	    			jsRtn.addRecord(jrWbBedInfo);
	    			iHandlingCntChk = iHandlingCnt;
				} //for Loop_j
			} //for Loop_i
			
			commUtils.printLog(logId, "HANDLING_수:" +jsRtn.size() , "SL");
			
			if(sAPP005_YN.equals("Y") && szDBLogMsg.length() > 0) {
				
				JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
    			jrLog.setField("STOCK_ID"		, "SLAB");
    			jrLog.setField("YD_CRN_SCH_ID"	, ydWbookId);
    			jrLog.setField("YD_GP"			, "2");
    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
    			jrLog.setField("SCH_CONTENTS"	, "핸들링 분리 사유:"+ "\r\n" + szDBLogMsg + "\r\n" );

    			EJBConnector SchLog = new EJBConnector("default", "BSlabSchSeEJB", this);
    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
			}
						
			commUtils.printLog(logId, methodNm, "S-");			

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return jsRtn;

	} //end of 			
	
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
			commUtils.printLog(logId, methodNm, "S+");
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			jrParam.setField("YD_WBOOK_ID",ydWbookId);
			
			JDTORecordSet recSetRes = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.chkDupStkByWBookId", logId, methodNm, "중복적치 조회"); 
			
			
			if(recSetRes == null || recSetRes.size() <=0 ) {
				commUtils.printLog(logId,"작업예약 ["+ydWbookId+"] 재료에 대해 중복적치 발생 안함", "SL");
				commUtils.printLog(logId, methodNm, "S-");
				return false;
			} 
			
			for(int ii=0; ii<recSetRes.size() ; ii++){
				int stkCnt = Integer.parseInt(commUtils.trim(recSetRes.getRecord(ii).getFieldString("STK_CNT")));
				String stlNo = commUtils.trim(recSetRes.getRecord(ii).getFieldString("STOCK_ID"));
				//중복적치된 재료 발견 
				if(stkCnt > 1) {
					commUtils.printLog(logId,"재료번호 ["+stlNo+"] 중복적치 발생", "SL");
					return true;
				}
			}
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return false;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
}