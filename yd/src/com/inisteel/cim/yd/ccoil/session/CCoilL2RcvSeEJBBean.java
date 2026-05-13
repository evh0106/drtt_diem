/**
 * @(#)CCoilL2RcvSeEJBBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2019/05/02
 * 
 * @description      2열연 COIL 야드 L2 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2019/05/02   정종균  이현진      최초 등록
 * 
 */
package com.inisteel.cim.yd.ccoil.session;


import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.ccoil.dao.CCoilDAO;
import com.inisteel.cim.yd.ccommon.dao.CCommDAO;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.ccommon.util.CConstant;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import com.inisteel.cim.yd.ccoil.session.CCoilJspSeEJBBean;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
/**
 *      [A] 클래스명 :  2열연 COIL 야드 L2 수신 
 * 
 * @ejb.bean name="CCoilL2RcvSeEJB" jndi-name="CCoilL2RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/

public class CCoilL2RcvSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private CCommUtils commUtils = new CCommUtils();
	private CCommDAO commDao = new CCommDAO();
	private CCoilDAO coilDao = new CCoilDAO();
	private CCoilJspSeEJBBean cCoilJspSeEJBBean = new CCoilJspSeEJBBean();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	
	/** 
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	
	/**
	 *      [A] 오퍼레이션명 : Crane Reschedule 처리 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnResch(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "크레인리스케줄[CCoilL2RcvSeEJB.trtCrnResch] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			rcvMsg.setResultMsg(mthdNm);	//Log Method Name

			String sModifier   = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//크레인작업지시 전문 Return
			 /* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnReschWrkBook
			--크레인리스케줄 작업예약 수정 
			MERGE INTO TB_YD_WRKBOOK WB USING (
			SELECT SR.YD_SCH_CD
			      ,DD.MODIFIER
			      ,DECODE(DD.BR_GP,'B',SR.YD_ALT_CRN_PRIOR,SR.YD_WRK_CRN_PRIOR) AS YD_SCH_PRIOR
			  FROM TB_YD_SCHRULE SR
			      ,(SELECT :V_YD_EQP_ID AS YD_EQP_ID
			              ,:V_MODIFIER  AS MODIFIER
			              ,:V_BR_GP     AS BR_GP --고장복구구분
			          FROM DUAL) DD
			 WHERE SR.YD_GP       = SUBSTR(DD.YD_EQP_ID,1,1)
			   AND SR.YD_BAY_GP   = SUBSTR(DD.YD_EQP_ID,2,1)
			   AND (SR.YD_WRK_CRN = DD.YD_EQP_ID OR SR.YD_ALT_CRN = DD.YD_EQP_ID)
			   AND SR.DEL_YN      = 'N'
			) DD ON (WB.YD_SCH_CD = DD.YD_SCH_CD AND WB.DEL_YN = 'N')
			WHEN MATCHED THEN UPDATE SET
				 WB.MODIFIER     = DD.MODIFIER
			    ,WB.MOD_DDTT     = SYSDATE
			    ,WB.YD_SCH_PRIOR = NVL(DD.YD_SCH_PRIOR,WB.YD_SCH_PRIOR)
			*/

			commDao.update(rcvMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnReschWrkBook", logId, mthdNm, "작업예약 야드스케쥴우선순위 수정");

			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnReschCrnSch
			--크레인리스케줄 크레인스케줄 수정 
			MERGE INTO TB_YD_CRNSCH CS USING (
			SELECT SR.YD_SCH_CD
			      ,DD.MODIFIER
			      ,DECODE(DD.BR_GP,'B',SR.YD_ALT_CRN_PRIOR,SR.YD_WRK_CRN_PRIOR) AS YD_SCH_PRIOR
			      ,DECODE(DD.YD_EQP_ID,SR.YD_WRK_CRN,DECODE(DD.BR_GP,'B',SR.YD_ALT_CRN,SR.YD_WRK_CRN)) AS YD_EQP_ID
			  FROM TB_YD_SCHRULE SR
			      ,(SELECT :V_YD_EQP_ID AS YD_EQP_ID
			              ,:V_MODIFIER  AS MODIFIER
			              ,:V_BR_GP     AS BR_GP --고장복구구분
			          FROM DUAL) DD
			 WHERE SR.YD_GP       = SUBSTR(DD.YD_EQP_ID,1,1)
			   AND SR.YD_BAY_GP   = SUBSTR(DD.YD_EQP_ID,2,1)
			   AND (SR.YD_WRK_CRN = DD.YD_EQP_ID OR SR.YD_ALT_CRN = DD.YD_EQP_ID)
			   AND SR.DEL_YN      = 'N'
			) DD ON (CS.YD_SCH_CD = DD.YD_SCH_CD 
			     AND CS.YD_WRK_PROG_STAT = 'W' 
			     AND CS.DEL_YN = 'N' 
			     AND (SELECT YD_WRK_PLAN_CRN 
			            FROM TB_YD_WRKBOOK WB
			           WHERE WB.YD_WBOOK_ID = CS.YD_WBOOK_ID
			             AND DEL_YN = 'N') IS NULL 
			        )
			WHEN MATCHED THEN UPDATE SET
				 CS.MODIFIER     = DD.MODIFIER
			   , CS.MOD_DDTT     = SYSDATE
			   , CS.YD_SCH_PRIOR = CASE WHEN CS.YD_SCH_PRIOR = 0 THEN 0
			                            ELSE NVL(DD.YD_SCH_PRIOR,CS.YD_SCH_PRIOR) END
			   , CS.YD_EQP_ID    = DECODE(DD.YD_EQP_ID,NULL,CS.YD_EQP_ID,DD.YD_EQP_ID)
			*/
			commDao.update(rcvMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnReschCrnSch", logId, mthdNm, "크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정");

			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCrnReschWoEqp
			--크레인리스케줄 작업지시설비 조회 
			SELECT EQ.YD_EQP_ID
			  FROM TB_YD_EQP EQ
			      ,(SELECT DISTINCT DECODE(DD.BR_GP,'B',SR.YD_ALT_CRN,SR.YD_WRK_CRN) AS YD_EQP_ID
			          FROM TB_YD_SCHRULE SR
			              ,(SELECT :V_YD_EQP_ID AS YD_EQP_ID
			                      ,:V_BR_GP     AS BR_GP --고장복구구분
			                  FROM DUAL) DD
			         WHERE SR.YD_GP      = SUBSTR(DD.YD_EQP_ID,1,1)
			           AND SR.YD_BAY_GP  = SUBSTR(DD.YD_EQP_ID,2,1)
			           AND SR.YD_WRK_CRN = DD.YD_EQP_ID
			           AND SR.DEL_YN     = 'N') SR
			 WHERE EQ.YD_EQP_ID   = SR.YD_EQP_ID
			   AND EQ.DEL_YN      = 'N'
			   AND EQ.YD_EQP_STAT = 'W'
			*/
			JDTORecordSet jsWoEqp = commDao.select(rcvMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCrnReschWoEqp", logId, mthdNm, "크레인작업지시 대상 설비 조회");

			int iSchCnt = jsWoEqp.size();

			JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);

			jrYdMsg.setField("JMS_TC_CD"         , "Y5YDL007"         ); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("MODIFIER"          , rcvMsg.getFieldString("MODIFIER")); //수정자

			for (int ii = 0; ii < iSchCnt; ii++) {
				jrYdMsg.setField("YD_EQP_ID"       , jsWoEqp.getRecord(ii).getFieldString("YD_EQP_ID")); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "W"); //야드작업진행상태
				
				//크레인작업지시요구 전문을 추가
				jrRtn = commUtils.addSndData(jrRtn, this.rcvY5YDL007(jrYdMsg));
			}
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : Crane Reschedule 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnReschH(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "소재크레인리스케줄[CCoilL2RcvSeEJB.trtCrnReschH] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			rcvMsg.setResultMsg(mthdNm);

			String sReSchYn    = commUtils.trim(rcvMsg.getFieldString("RE_SCH_YN")); //리스케줄 여부
			String sModifier   = commUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자(Backup Only)

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnReschWrkBookH
			MERGE INTO TB_YD_WRKBOOK WB USING (
			SELECT SR.YD_SCH_CD
			     , DD.MODIFIER
			     , DECODE(DD.BR_GP,'B',SR.YD_ALT_CRN_PRIOR,SR.YD_WRK_CRN_PRIOR) AS YD_SCH_PRIOR
			  FROM TB_YD_SCHRULE SR
			     , (SELECT :V_YD_EQP_ID AS YD_EQP_ID
			             , :V_MODIFIER  AS MODIFIER
			             , :V_BR_GP     AS BR_GP --고장복구구분
			          FROM DUAL
			       ) DD
			 WHERE SR.YD_GP       = SUBSTR(DD.YD_EQP_ID,1,1)
			   AND SR.YD_BAY_GP   = SUBSTR(DD.YD_EQP_ID,2,1)
			   AND (SR.YD_WRK_CRN = DD.YD_EQP_ID OR SR.YD_ALT_CRN = DD.YD_EQP_ID)
			   AND SR.DEL_YN      = 'N'
			) DD ON (WB.YD_SCH_CD = DD.YD_SCH_CD AND WB.DEL_YN = 'N')
			WHEN MATCHED THEN UPDATE SET
			     WB.MODIFIER     = DD.MODIFIER
			   , WB.MOD_DDTT     = SYSDATE
			   , WB.YD_SCH_PRIOR = NVL(DD.YD_SCH_PRIOR, WB.YD_SCH_PRIOR)
			*/
			commDao.update(rcvMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnReschWrkBookH", logId, mthdNm, "작업예약 야드스케쥴우선순위 수정");

			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnReschCrnSchH
			MERGE INTO TB_YD_CRNSCH CS USING (
			SELECT SR.YD_SCH_CD
			     , DD.MODIFIER
			     , DECODE(DD.BR_GP, 'B', SR.YD_ALT_CRN_PRIOR, SR.YD_WRK_CRN_PRIOR) AS YD_SCH_PRIOR
			     , DECODE(DD.YD_EQP_ID, SR.YD_WRK_CRN, DECODE(DD.BR_GP, 'B', SR.YD_ALT_CRN, SR.YD_WRK_CRN)) AS YD_EQP_ID
			  FROM TB_YD_SCHRULE SR
			     , (SELECT :V_YD_EQP_ID AS YD_EQP_ID
			             , :V_MODIFIER  AS MODIFIER
			             , :V_BR_GP     AS BR_GP --고장복구구분
			          FROM DUAL
			       ) DD
			 WHERE SR.YD_GP       = SUBSTR(DD.YD_EQP_ID,1,1)
			   AND SR.YD_BAY_GP   = SUBSTR(DD.YD_EQP_ID,2,1)
			   AND (SR.YD_WRK_CRN = DD.YD_EQP_ID OR SR.YD_ALT_CRN = DD.YD_EQP_ID)
			   AND SR.DEL_YN      = 'N'
			) DD ON (CS.YD_SCH_CD = DD.YD_SCH_CD
			     AND CS.YD_WRK_PROG_STAT = 'W'
			     AND CS.DEL_YN = 'N'
			     -- 스케쥴에서 작업범위 지정된 크레인작업 변경 안함
			     AND 'Y' = CASE WHEN CS.YD_SCH_CD = 'JHKE01UH' AND SUBSTR(CS.YD_UP_WO_LOC, 0, 4) IN ('JH32', 'JH33') THEN 'N'
			                    WHEN CS.YD_SCH_CD = 'JEKE01UH' AND SUBSTR(CS.YD_UP_WO_LOC, 0, 4) IN ('JE32', 'JE33') THEN 'N'
			                    ELSE 'Y'
			               END
			        )
			WHEN MATCHED THEN UPDATE SET
			     CS.MODIFIER     = DD.MODIFIER
			   , CS.MOD_DDTT     = SYSDATE
			   , CS.YD_SCH_PRIOR = NVL(DD.YD_SCH_PRIOR,CS.YD_SCH_PRIOR)
			   , CS.YD_EQP_ID    = DECODE(DD.YD_EQP_ID,NULL,CS.YD_EQP_ID,DD.YD_EQP_ID)
			*/
			commDao.update(rcvMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnReschCrnSchH", logId, mthdNm, "크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정");

			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCrnReschWoEqpH
			SELECT EQ.YD_EQP_ID
			  FROM TB_YD_EQP EQ
			     , (SELECT DISTINCT DECODE(DD.BR_GP, 'B', SR.YD_ALT_CRN,SR.YD_WRK_CRN) AS YD_EQP_ID
			          FROM TB_YD_SCHRULE SR
			             , (SELECT :V_YD_EQP_ID AS YD_EQP_ID
			                     , :V_BR_GP     AS BR_GP --고장복구구분
			                  FROM DUAL
			               ) DD
			         WHERE SR.YD_GP      = SUBSTR(DD.YD_EQP_ID,1,1)
			           AND SR.YD_BAY_GP  = SUBSTR(DD.YD_EQP_ID,2,1)
			           AND SR.YD_WRK_CRN = DD.YD_EQP_ID
			           AND SR.DEL_YN     = 'N'
			       ) SR
			 WHERE EQ.YD_EQP_ID   = SR.YD_EQP_ID
			   AND EQ.DEL_YN      = 'N'
			   AND EQ.YD_EQP_STAT = 'W'
			*/
			JDTORecordSet jsWoEqp = commDao.select(rcvMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCrnReschWoEqpH", logId, mthdNm, "크레인작업지시 대상 설비 조회");

			int iSchCnt = jsWoEqp.size();

			JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);

			jrYdMsg.setField("JMS_TC_CD"         , "Y5YDL007"         ); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("MODIFIER"          , rcvMsg.getFieldString("MODIFIER")); //수정자

			for (int ii = 0; ii < iSchCnt; ii++) {
				jrYdMsg.setField("YD_EQP_ID"       , jsWoEqp.getRecord(ii).getFieldString("YD_EQP_ID")); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "W"); //야드작업진행상태

				//크레인작업지시요구 전문을 추가
				jrRtn = commUtils.addSndData(jrRtn, this.rcvY5YDL007(jrYdMsg));
			}

			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : OFF-LINE 크레인 변경 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord offLineChgnCrn(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "OFF-LINE 크레인 변경 처리[CCoilL2RcvSeEJB.offLineChgnCrn] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecordSet rsResult    	= null;
		
		JDTORecord jrRtn = null;	//전문 Return
		
		try{
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			String sModifier	= commUtils.nvl(rcvMsg.getFieldString("MODIFIER"),"OFFLINE");
			String ydEqpId		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //야드설비ID
			String ydGp			= "";
			String ydBayGp		= "";
			
			String ydCrnSchId		= ""; //야드크레인스케쥴ID
			String ydWbookId		= ""; //야드작업예약ID
			String ydWrkProgStat	= ""; //야드작업진행상태
			String chgYdEqpId		= ""; //변경 야드설비ID(크레인)
			String chgYdSchPrior	= ""; //변경 야드스케쥴우선순위
			String chgYdEqpStat		= ""; //변경 야드설비상태
			String ydLocGp			= "";

		
			if ("".equals(ydEqpId)) {
				return jrRtn;
			} else {
				if (ydEqpId.length() < 2) {
					return jrRtn;
				} else {
					ydGp    = ydEqpId.substring(0,1);
					ydBayGp = ydEqpId.substring(1,2);
				}
			}
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);

			//해당 Crane 호기에 걸려 있는 스케줄 정보 조회
			jrParam.setField("YD_GP"		, ydGp); //야드구분
			jrParam.setField("YD_BAY_GP"	, ydBayGp); //동구분
			jrParam.setField("YD_EQP_ID"	, ydEqpId); //크레인번호
			rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCrnWrk", logId, mthdNm, "크레인스케줄조회");
			
			if (rsResult.size() <= 0) {
				commUtils.printLog(logId, "해당 Crane 호기에 걸려 있는 스케줄 정보 없음! "+ydEqpId, "[]");
				return jrRtn;
			}
			
			String[] arrYdWbookId = new String[rsResult.size()];
			
			for (int ii = 0; ii < rsResult.size(); ii++) {
			
				ydWbookId  = commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_WBOOK_ID"));
				ydCrnSchId = commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_CRN_SCH_ID"));
			
			    //작업할 야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (commUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }  //해당 값이 있는지를 Check
				
				arrYdWbookId[ii] = ydWbookId;

				/**********************************************************
				* 1. 크레인스케줄, 스케줄기준, 설비정보 Check
				* 1.1 크레인스케줄의 스케줄ID 및 설비상태 Check
				* 1.2 크레인스케줄 설비ID로 스케줄기준의 주 및 대체 크레인설비ID와 비교하여 변경 크레인설비ID와 순위를 Set
				* 1.3 변경 할 크레인 정보를 Check
				**********************************************************/
				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCraneChange1
				--크레인작업관리 크레인변경 조회
				SELECT CS.YD_WBOOK_ID
				     , CS.YD_WRK_PROG_STAT
				     , CS.YD_SCH_CD
				     , CS.YD_EQP_ID
				     , CS.CHG_YD_EQP_ID
				     , CS.CHG_YD_SCH_PRIOR
				     , (SELECT DECODE(YD_EQP_STAT, 'B', YD_EQP_STAT, 'W') FROM TB_YD_EQP EP WHERE EP.YD_EQP_ID = CS.CHG_YD_EQP_ID) AS CHG_YD_EQP_STAT
				     , EQ.YD_EQP_WRK_MODE AS CHG_YD_EQP_WRK_MODE
				     , EQ.YD_EQP_WRK_MODE2
				     , (SELECT YD_EQP_WRK_MODE2 FROM TB_YD_EQP WHERE YD_EQP_ID = CS.YD_EQP_ID) AS OLD_YD_EQP_WRK_MODE2
				     , (SELECT YD_EQP_WRK_MODE  FROM TB_YD_EQP WHERE YD_EQP_ID = CS.YD_EQP_ID) AS OLD_WORK_MODE
				     , (SELECT YD_EQP_STAT      FROM TB_YD_EQP WHERE YD_EQP_ID = CS.YD_EQP_ID) AS OLD_WPROG_STAT
				     , (SELECT YD_LOC_GP        FROM TB_YD_EQP WHERE YD_EQP_ID = CS.YD_EQP_ID) AS YD_LOC_GP
				  FROM TB_YD_EQP EQ
				      ,(
				        SELECT CS.YD_WBOOK_ID
				             , CS.YD_WRK_PROG_STAT
				             , CS.YD_SCH_CD
				             , CS.YD_EQP_ID
				             , (CASE WHEN CS.YD_EQP_ID = SR.YD_WRK_CRN  THEN YD_ALT_CRN
				                     WHEN CS.YD_EQP_ID = SR.YD_ALT_CRN  THEN YD_WRK_CRN
				                     ELSE CS.YD_EQP_ID
				                END)            AS CHG_YD_EQP_ID
				             , CS.YD_SCH_PRIOR  AS CHG_YD_SCH_PRIOR
				          FROM TB_YD_CRNSCH     CS
				             , TB_YD_SCHRULE    SR
				         WHERE CS.YD_SCH_CD     = SR.YD_SCH_CD
				           AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				           AND CS.DEL_YN        = 'N'
				       ) CS
				 WHERE CS.CHG_YD_EQP_ID = EQ.YD_EQP_ID
				*/
				JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCraneChange1", logId, mthdNm, "크레인변경 조회");

			    if (jsCrn == null || jsCrn.size() <= 0) {
					continue;
			    }
				
			    JDTORecord jrCrn = jsCrn.getRecord(0);
				
			    ydWrkProgStat   = commUtils.trim(jrCrn.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				ydEqpId         = commUtils.trim(jrCrn.getFieldString("YD_EQP_ID"		)); //야드설비ID
				chgYdEqpId      = commUtils.trim(jrCrn.getFieldString("CHG_YD_EQP_ID"	)); //변경 야드설비ID
				chgYdSchPrior   = commUtils.trim(jrCrn.getFieldString("CHG_YD_SCH_PRIOR")); //변경 야드스케쥴우선순위
				chgYdEqpStat    = commUtils.trim(jrCrn.getFieldString("CHG_YD_EQP_STAT"	)); //변경 야드설비상태
				ydLocGp			= commUtils.trim(jrCrn.getFieldString("YD_LOC_GP"		)); //소재/제품 야드구분
			
				/**********************************************************
				* 2. 작업예약 및 크레인스케줄 Table에  대체 크레인ID와 우선순위를 Update
				**********************************************************/
				jrParam.setField("YD_SCH_PRIOR"	, chgYdSchPrior);
				jrParam.setField("YD_EQP_ID"   	, chgYdEqpId   );
				
				/*  
				UPDATE TB_YD_WRKBOOK
				   SET MODIFIER     = :V_MODIFIER
				     , MOD_DDTT     = SYSDATE
				     , YD_SCH_PRIOR = NVL(TO_NUMBER(:V_YD_SCH_PRIOR),1)
				 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
				   AND DEL_YN       = 'N'
				*/

				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updWrkBookPrior", logId, mthdNm, "작업예약 Table 우선순위 Update");				
				
				// 지시 내려간 스케줄은 상태 유지
				if ("1".equals(ydWrkProgStat) || "S".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.1  OFF-LINE 크레인의 작업지시 취소 전문 송신
					**********************************************************/
					if( "H".equals(ydLocGp) ) {
						commUtils.printLog(logId, "OFF-LINE 크레인의 작업지시 취소 전문 송신 "+mthdNm, "SL");
						jrParam.setField("MSG_GP", "D"); //전문구분(취소)
						jrRtn = commUtils.addSndData(jrRtn,coilDao.getMsgL2("YDY5L004", jrParam));
					} else {
						commUtils.printLog(logId, "제품크레인 작업지시 취소 전문 없음 "+mthdNm, "SL");
					}
				}
				
				if( "H".equals(ydLocGp) ) {
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnWrkMgtW
					--크레인작업관리 크레인변경 크레인스케줄 수정
					UPDATE TB_YD_CRNSCH
					   SET MODIFIER     = :V_MODIFIER
					     , MOD_DDTT     = SYSDATE
					     , YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
					     , YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
					     , YD_WRK_PROG_STAT = 'W' 
					     , YD_WORD_DT   = SYSDATE
					 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
					   AND YD_WRK_PROG_STAT IN ('1','W','S')
					   AND DEL_YN = 'N'  
					*/
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnWrkMgtW", logId, mthdNm,  "크레인스케줄 Table 크레인ID, 우선순위 Update");					

				} else {
				
					// 지시 내려가지 않은 스케줄은 크레인만 변경
					if ("W".equals(ydWrkProgStat)) {
						/* 
						UPDATE TB_YD_CRNSCH
						   SET MODIFIER     = :V_MODIFIER
						     , MOD_DDTT     = SYSDATE
						     , YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
						     , YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
						     , YD_WRK_PROG_STAT = 'W' 
						     , YD_WORD_DT   = SYSDATE
						 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
						   AND YD_WRK_PROG_STAT IN ('1','W','S')
						   AND DEL_YN = 'N'   
						*/
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnWrkMgtW", logId, mthdNm,  "크레인스케줄 Table 크레인ID, 우선순위 Update");					
					}
				}
			}
			
			commUtils.printLog(logId, "변경된 크레인의 설비상태: "+ chgYdEqpStat + " " + mthdNm, "SL");
			
			if (!"".equals(chgYdEqpId) && !chgYdEqpId.equals(ydEqpId)) {
				//변경된 크레인 상태 w이면 명령선택기동 EQP
				if ("W".equals(chgYdEqpStat)) {
					
					/***********************************************************
					 * 크레인의 다음 스케줄 명령 선택 기동 - 운전모드변경 명령선택 기동 호출
					 ***********************************************************/
					//야드설비상태가 대기이면 명령선택전문 전송
					JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
					jrYdMsg.setField("JMS_TC_CD"         , "Y5YDL007"               ); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , chgYdEqpId               ); //야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT"  , chgYdEqpStat             ); //야드작업진행상태
					
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				}
			}
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	


	/**
	 *      [A] 오퍼레이션명 : 크레인권상실적(Y5YDL008)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL008(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "크레인권상실적[CCoilL2RcvSeEJB.rcvY5YDL008] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord resMsg = commUtils.getParam(logId, mthdNm, "Y5YDL008"); //크레인작업실적응답 전문 생성용
		boolean resYn 	  = true;	//크레인작업실적응답 전문 전송여부

		try {

			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			commUtils.printLog(logId, "CICD 2026.03.26 리프터 실적값 추가 반영 ", "SL");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydUpWrLoc     = commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LOC"    )); //야드권상실적위치
			String ydUpWrLayer   = commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LAYER"  )); //야드권상실적단
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축
			String ydEqpWrkMode2 = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE2")); //A:무인, R:리모컨, M:유인
			String ydLifterWrslt = commUtils.trim(rcvMsg.getFieldString("LIFTER_W_WRSLT"  )); //리프터 실적값  -- RITM1864222 2026.03.26 리프터실적값 추가
			
			String sModifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			JDTORecord jrRtn     = JDTORecordFactory.getInstance().create(); //전문 Return
			String ydL3HdRsCd    = "";   //야드L3처리결과코드
			String ydL3Msg       = "";   //야드L3MESSAGE
			
			String sCurrDt       = commUtils.getDateTime14();
			
			//크레인작업실적응답 전문 생성용 
			JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
			
			//크레인작업실적응답 전문 생성용
			resMsg.setField("MODIFIER"        , sModifier    );	//수정자
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "U"          ); //야드L2실적구분(권상실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)

			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "UP01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "UP02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydCrnSchId)) {
				ydL3HdRsCd = "UP03";
				ydL3Msg    = "오류:크레인스케쥴ID 없음";
			} else if ("".equals(ydUpWrLoc)|| ydUpWrLoc.length() < 8) {
				ydL3HdRsCd = "UP04";
				ydL3Msg    = "오류:권상실적위치 없음";
			} else if ("".equals(ydUpWrLayer)) {
				ydL3HdRsCd = "UP05";
				ydL3Msg    = "오류:권상실적단 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

//PIDEV
//PIDEV_S :병행가동용:PI_YD
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "CCoilL2RcvSeEJBBean => 크레인권상실적", "APPPI0", "J", "*");
			String sPI_YD = "";
//			if("Y".equals(sApplyYnPI)) {
				sPI_YD = "J";
//			}
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			jrParam.setField("YD_SCH_CD"       , ydSchCd      ); //
			jrParam.setField("YD_UP_WR_LOC"    , ydUpWrLoc    ); //
			jrParam.setField("YD_UP_WR_LAYER"  , ydUpWrLayer  ); //
			jrParam.setField("YD_UP_WRK_MODE2" , ydEqpWrkMode2); //
			jrParam.setField("LIFTER_W_WRSLT"  , ydLifterWrslt); //리프터 실적값 -- RITM1864222 2026.03.26 리프터실적값 추가

			jrParam.setField("YD_EQP_ID"       , ydEqpId      ); 
			jrParam.setField("YD_EQP_WRK_MODE" , ydEqpWrkMode );
			jrParam.setField("YD_WRK_PROG_STAT", ydWrkProgStat);
			jrParam.setField("YD_CRN_XAXIS"    , ydCrnXaxis   );
			jrParam.setField("YD_CRN_YAXIS"    , ydCrnYaxis   );
			jrParam.setField("YD_CRN_ZAXIS"    , ydCrnZaxis   );
			
			jrParam.setField("YD_UP_WR_XAXIS"  , ydCrnXaxis   );
			jrParam.setField("YD_UP_WR_YAXIS"  , ydCrnYaxis   );
			jrParam.setField("YD_UP_WR_ZAXIS"  , ydCrnZaxis   );
			
			//전문 송신지 위치 Check	:AUTO MANUAL BACKUP구분
			if ("1".equals(ydEqpWrkMode)) { 
				jrParam.setField("YD_UP_WRK_ACT_GP", "A") ;
			} else if ("9".equals(ydEqpWrkMode)) {
				jrParam.setField("YD_UP_WRK_ACT_GP", "B") ;
			}  else if ("0".equals(ydEqpWrkMode)) {
				jrParam.setField("YD_UP_WRK_ACT_GP", "M") ;
			}
			jrParam.setField("YD_UP_WRK_MODE2", ydEqpWrkMode2);
			jrParam.setField("YD_DN_WRK_MODE2", ydEqpWrkMode2);
			
			jrParam.setField("YD_UP_CMPL_DT"  , sCurrDt      ); //권상완료일시
			
			boolean bCarMvYn     = coilDao.chkCarMv(logId, mthdNm, ydSchCd); // 차량동간이적 여부
			boolean bCarMvYnRetn = coilDao.chkCarMvRetn(logId, mthdNm, ydSchCd); // 반품회송 여부
			
			/**********************************************************
			* 1. 
			**********************************************************/
			String ydGp          = "J";
			String ydWbookId     = "";  //야드작업예약ID
			String ydDnWoLoc     = "";
			String ydDnWoLayer   = "";
			String ydIsptor      = "";
			String ydTakeOutDt   = "";
			String ydTakeOutCd   = "";
			String sStlNo        = "";
//			String sStlAppearGp  = "";
			/* 
			SELECT A.*
			     , B.STL_NO                             AS STL_NO
			     , B.YD_AID_WRK_YN                      AS YD_AID_WRK_YN
			     , B.YD_STK_LYR_NO                      AS YD_STK_LYR_NO
			     , B.YD_STK_LOT_TP                      AS YD_STK_LOT_TP
			     , B.YD_STK_LOT_CD                      AS YD_STK_LOT_CD
			     , B.HCR_GP                             AS HCR_GP
			     , B.STL_PROG_CD                        AS STL_PROG_CD
			     , B.STL_APPEAR_GP                      AS STL_APPEAR_GP
			     , B.YD_MTL_ITEM                        AS YD_MTL_ITEM
			     , B.YD_ROUTE_GP                        AS YD_ROUTE_GP
			     , B.YD_AIM_YD_GP                       AS YD_AIM_YD_GP
			     , B.YD_MTL_WT                          AS YD_MTL_WT
			     , B.YD_ISPTOR
			     , B.YD_TAKE_OUT_DT
			     , B.YD_TAKE_OUT_CD
			     , (SELECT YD_EQP_WRK_MODE2 FROM TB_YD_EQP WHERE YD_EQP_ID = A.YD_EQP_ID) AS DB_YD_EQP_WRK_MODE2
			  FROM TB_YD_CRNSCH A
			     , (SELECT Y.YD_CRN_SCH_ID
			             , Y.STL_NO
			             , Y.YD_AID_WRK_YN
			             , Y.YD_STK_LYR_NO
			             , Y.YD_STK_LOT_TP
			             , Y.YD_STK_LOT_CD
			             , Y.HCR_GP
			              /* ,Y.STL_PROG_CD 크레인작업재료에 정보가 없음
			             , (SELECT CURR_PROG_CD   FROM TB_PT_COILCOMM WHERE COIL_NO = X.STL_NO) AS STL_PROG_CD
			             , (SELECT STL_APPEAR_GP  FROM TB_PT_COILCOMM WHERE COIL_NO = X.STL_NO) AS STL_APPEAR_GP
			             , Y.YD_MTL_ITEM
			             , Y.YD_ROUTE_GP
			             , X.YD_AIM_YD_GP
			             , X.YD_MTL_WT
			             , G.YD_ISPTOR
			             , G.YD_TAKE_OUT_DT
			             , G.YD_TAKE_OUT_CD
			          FROM TB_YD_STOCK     X
			             , TB_YD_CRNWRKMTL Y
			             , (SELECT * 
			                  FROM TB_YD_WRKBOOKMTL
			                 WHERE DEL_YN = 'N'
			               ) G
			         WHERE X.STL_NO = Y.STL_NO
			           AND X.STL_NO = G.STL_NO(+)
			      ) B
			 WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
			   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
			 ORDER BY B.YD_STK_LYR_NO
			 
			 */
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getStatCrnSch", logId, mthdNm, "크레인스케줄 정보조회");
			JDTORecord    jrCrnSch = null;
			
			if (jsCrnSch.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "UP11";
				ydL3Msg = "오류:크레인스케쥴 DB정보 없음";
			} else {
				jrCrnSch = jsCrnSch.getRecord(0);
				ydWbookId           = jrCrnSch.getFieldString("YD_WBOOK_ID"       ); //작업예약    
				ydDnWoLoc           = jrCrnSch.getFieldString("YD_DN_WO_LOC"      ); //권하지시위치
				ydDnWoLayer         = jrCrnSch.getFieldString("YD_DN_WO_LAYER"              ); //권하지시위치 레이어
				ydIsptor          	= jrCrnSch.getFieldString("YD_ISPTOR"         );
				ydTakeOutDt     	= jrCrnSch.getFieldString("YD_TAKE_OUT_DT"    );
				ydTakeOutCd     	= jrCrnSch.getFieldString("YD_TAKE_OUT_CD"    );
				ydWrkProgStat   	= jrCrnSch.getFieldString("YD_WRK_PROG_STAT"  );
				sStlNo              = jrCrnSch.getFieldString("STL_NO"            ); //재료번호
//				sStlAppearGp        = jrCrnSch.getFieldString("STL_APPEAR_GP"     ); //재료외형구분 E:소재, Y:제품

				String tmpStat      = commUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT"   )); //야드작업진행상태
				String tmpEqpId     = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"          )); //야드설비ID
				String tmpMode2     = commUtils.trim(jrCrnSch.getFieldString("DB_YD_EQP_WRK_MODE2")); //DB 작업모드2
				
				if (!"1".equals(tmpStat) && !"W".equals(tmpStat) && !"S".equals(tmpStat)) {
					ydL3HdRsCd = "UP12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "UP13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
				
				// 실적에서 MODE2 가 안올라 오는 경우 DB에 있는 DATA SETUP
				if ("".equals(ydEqpWrkMode2)) {
					ydEqpWrkMode2 = tmpMode2;
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			commUtils.printLog(logId, "권상실적"+sStlNo, "SL");
	        /*********************************************
	         * 설비상태 수정
	         *********************************************/
	        jrParam.setField("YD_EQP_ID"  , jrCrnSch.getFieldString("YD_EQP_ID"));
	        jrParam.setField("YD_EQP_STAT", "2");
	        
	        /*
			UPDATE TB_YD_EQP
			   SET MODIFIER    = :V_MODIFIER
			     , MOD_DDTT    = SYSDATE
			     , YD_EQP_STAT = :V_YD_EQP_STAT
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND DEL_YN    = 'N'
	         */
	        commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updStatEqp", logId, mthdNm, "설비상태 권상");
	        
	        /*********************************************
	         * 크레인스케줄 수정
	         *********************************************/
	        /*
			UPDATE TB_YD_CRNSCH
			   SET MODIFIER         = :V_MODIFIER
			     , MOD_DDTT         = SYSDATE
			     , YD_WRK_PROG_STAT = '2' --권상완료
			     , YD_UP_CMPL_DT    = TO_DATE(:V_YD_UP_CMPL_DT,'YYYYMMDDHH24MISS')
			     , YD_UP_WR_LOC     = :V_YD_UP_WR_LOC
			     , YD_UP_WR_LAYER   = :V_YD_UP_WR_LAYER
			     , YD_UP_WRK_ACT_GP = :V_YD_UP_WRK_ACT_GP
			     , YD_UP_WR_XAXIS   = TO_NUMBER(:V_YD_UP_WR_XAXIS)
			     , YD_UP_WR_YAXIS   = TO_NUMBER(:V_YD_UP_WR_YAXIS)
			     , YD_UP_WR_ZAXIS   = TO_NUMBER(:V_YD_UP_WR_ZAXIS)
			     , YD_UP_WRK_MODE2  = :V_YD_UP_WRK_MODE2
			     , LIFTER_W_WRSLT = TO_NUMBER(:V_LIFTER_W_WRSLT) -- RITM1864222 2026.03.26 리프터실적값 추가
			 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
	         */
	        commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL008CrnSch", logId, mthdNm, "크레인스케줄 권상으로수정"); // RITM1864222 2026.03.26 리프터실적값 추가
	        
	        /*********************************************
	         * 적치단 수정
	         *  - 권상위치 -> 크레인
	         *  - 권상위치 E
	         *********************************************/
	        /* 권상위치->크레인
			SELECT :V_YD_EQP_ID            AS YD_STK_COL_GP -- 크레인위치로
			     , '01'                    AS YD_STK_BED_NO
			     , TO_CHAR(ROWNUM,'FM000') AS YD_STK_LYR_NO
			     , A.STL_NO
			     , 'C'                     AS YD_STK_LYR_MTL_STAT --적치중
			  FROM TB_YD_CRNWRKMTL  A
			     , TB_YD_STKLYR     B
			     , TB_YD_CRNSCH     C 
			 WHERE A.STL_NO        = B.STL_NO
			   AND A.YD_CRN_SCH_ID = C.YD_CRN_SCH_ID
			   AND B.YD_STK_COL_GP = SUBSTR(C.YD_UP_WO_LOC,1,6) 
			   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			   AND A.DEL_YN        = 'N'
			   AND B.DEL_YN        = 'N'
			   AND ROWNUM = 1  
	         */
	        JDTORecordSet jsStklyrChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getY5YDL008UpStkLyrCR", logId, mthdNm, "권상정보 조회");
	        
			/*  
			--크레인권상실적 적치단(권상위치)  
			UPDATE TB_YD_STKLYR 
			   SET MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			     , STL_NO              = NULL
			     , YD_STK_LYR_MTL_STAT = 'E'
			 WHERE STL_NO IN (SELECT STL_NO FROM TB_YD_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)    
			   AND YD_STK_LYR_MTL_STAT    IN ('C', 'U')
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getY5YDL008UpStkLyr", logId, mthdNm, "적치단(권상위치) 수정");

	        if (jsStklyrChk.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "UP11";
				ydL3Msg    = "오류:크레인스케쥴 DB정보 없음";
	        } else {
	        	
	        	JDTORecord jrStklyrChk = jsStklyrChk.getRecord(0);
	        	jrStklyrChk.setField("MODIFIER", sModifier);	//수정자
        		/*
				UPDATE TB_YD_STKLYR
				   SET MODIFIER            = :V_MODIFIER
				     , MOD_DDTT            = SYSDATE
				     , STL_NO              = :V_STL_NO
				     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
				 WHERE YD_STK_COL_GP   = :V_YD_STK_COL_GP
				   AND YD_STK_BED_NO   = :V_YD_STK_BED_NO
				   AND YD_STK_LYR_NO   = :V_YD_STK_LYR_NO
				   AND SUBSTR(YD_STK_COL_GP,3,2) = 'CR'
        		 */
        		commDao.update(jrStklyrChk, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL008UpStkLyrToCR", logId, mthdNm, "크레인LYR 수정");
	        		
	        }
	        
            /*********************************************
	         * 크레인작업실적응답 전송 
	         *********************************************/
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
			}
			
	        commUtils.printLog(logId, " 권상위치(ydUpWrLoc) = " + ydUpWrLoc, "SL");
	        
	        /*********************************************
	         * 권상위치 CV
	         *********************************************/
	        if ("CV".equals(ydUpWrLoc.substring(2, 4))) {
	        	jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
	        	jrYdMsg.setField("JMS_TC_CD"    , "YDH1L001"); // JMSTC코드
	        	jrYdMsg.setField("MSG_GP"		, "I"		); // 전문구분
				jrYdMsg.setField("YD_EQP_ID"    , ydUpWrLoc.substring(0, 6));			// 권상CV위치 
				jrYdMsg.setField("STL_NO"       , jrCrnSch.getFieldString("STL_NO"));	// 재료번호
				jrYdMsg.setField("YD_STK_BED_NO", ydUpWrLoc.substring(6, 8));			// 권상CV BED
				
				//열연 압연 L2 전문 송신
				jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDH1L001", jrYdMsg));
				
	        } 
	        
	        /*********************************************
	         * 권상위치 대차
	         *********************************************/	        
	        if ("TC".equals(ydUpWrLoc.substring(2, 4))) {
	        	/*
				SELECT TS.YD_TCAR_SCH_ID
				      ,CASE WHEN NVL((SELECT COUNT(*)
				                        FROM TB_YD_TCARFTMVMTL TM
				                       WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
				                         AND TM.DEL_YN = 'N'),0) -
				                 NVL((SELECT COUNT(*)
				                        FROM TB_YD_CRNWRKMTL CM
				                       WHERE CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                         AND CM.DEL_YN = 'N'),0) > 0 THEN 'N' ELSE 'Y'
				       END AS TCAR_UD_CMPL_YN --대차하차완료여부
				  FROM TB_YD_TCARSCH TS
				 WHERE TS.YD_CARUD_STOP_LOC = SUBSTR(:V_YD_UP_WR_LOC,1,6)
				   AND TS.DEL_YN = 'N'
	        	 */
	        	JDTORecordSet jsTcSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getTcarSchUd", logId, mthdNm, "대차스케줄 조회");
	        	if (jsTcSch.size() > 0) {
	        	
		        	jsTcSch.first();
		        	JDTORecord jrTcSch  = jsTcSch.getRecord();
		        	String ydTcarSchId  = commUtils.trim(jrTcSch.getFieldString("YD_TCAR_SCH_ID" ));
		        	String tcarUdCmplYn = commUtils.trim(jrTcSch.getFieldString("TCAR_UD_CMPL_YN")); //대차하차완료여부
		        	
		        	commUtils.printLog(logId, "대차스케줄ID : " + ydTcarSchId, "SL");
		        	commUtils.printLog(logId, "하차완료 여부 : " + tcarUdCmplYn, "SL");
		        	
		        	jrParam.setField("YD_TCAR_SCH_ID", ydTcarSchId);
		        	
		        	if ("Y".equals(tcarUdCmplYn)) {
		        		//하차완료이면 대차스케줄 삭제 후 공대차출발지시
		        		//하차완료(공대차출발지시) 처리
						jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
						jrYdMsg.setField("CRANE_ID"      	, ydEqpId); 		//야드설비ID(CRANE)
						jrYdMsg.setField("YD_TCAR_SCH_ID"	, ydTcarSchId); 	//야드대차스케쥴ID
						jrYdMsg.setField("YD_CARUD_STOP_LOC", ydUpWrLoc.substring(0, 6)); //권상위치 -> 하차완료위치
						jrYdMsg.setField("YD_EQP_ID"     	, ydUpWrLoc.substring(0, 1) + "X" + ydUpWrLoc.substring(2, 6)); //야드설비ID(대차)
	
						jrRtn = commUtils.addSndData(jrRtn, this.trtTcarSchUdCmpl(jrYdMsg));
		        		
		        	} else {
		        		//하차완료가 아니면 대차이송재료 삭제후 대차작업실적 전송
		        		jrParam.setField("YD_TCAR_SCH_ID"   , ydTcarSchId              ); //야드대차스케쥴ID
						jrParam.setField("YD_CARUD_STOP_LOC", ydUpWrLoc.substring(0, 6)); //야드하차정지위치
						jrParam.setField("YD_CARUD_WRK_CRN" , ydEqpId                  ); //야드하차작업크레인
		        		/*
						UPDATE TB_YD_TCARSCH
						   SET MODIFIER          = :V_MODIFIER
						     , MOD_DDTT          = SYSDATE
						     , YD_CAR_PROG_STAT  = 'D' --하차개시
						     , YD_CARUD_ST_DT    = NVL(YD_CARUD_ST_DT,SYSDATE)
						     , YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC
						     , YD_CARUD_WRK_CRN  = :V_YD_CARUD_WRK_CRN
						 WHERE YD_TCAR_SCH_ID    = :V_YD_TCAR_SCH_ID
						   AND DEL_YN            = 'N'
		        		 */
		        		commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getY5YDL008TcarSchUd", logId, mthdNm, "대차스케줄(하차) 수정");
		        		
		        		/*
						MERGE INTO TB_YD_TCARFTMVMTL TM USING (
						SELECT :V_YD_TCAR_SCH_ID AS YD_TCAR_SCH_ID
						     , STL_NO
						  FROM TB_YD_CRNWRKMTL CM
						 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						   AND DEL_YN        = 'N'
						) DD ON (TM.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID AND TM.STL_NO = DD.STL_NO)
						WHEN MATCHED THEN UPDATE SET
							 TM.MODIFIER = :V_MODIFIER
						   , TM.MOD_DDTT = SYSDATE
						   , TM.DEL_YN   = 'Y'
		        		 */
		        		commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updTcarSchDelUd", logId, mthdNm, "대차 이송재료 삭제");	
		        	}
		        }
	        }

	        // PIDEV
	        commUtils.printLog(logId, "차량동간이적 : " + bCarMvYn, "SL");
	        
	        /*********************************************
	         * 권상위치 차량
	         *********************************************/
	        if (!bCarMvYn) { //차량동간이적이 아니면
	        	if ("PT".equals(ydUpWrLoc.substring(2, 4)) || "TR".equals(ydUpWrLoc.substring(2, 4)) ) {
	        		commUtils.printLog(logId, "권상위치 차량", "SL");
	    	        String ydCarSchId          = "";
	    	        String ydCarProgStat       = "";
	    	        String ydCarUseGp          = "";
	    	        String sTransEquipmentType = "";
	    	        String sCarKind            = "";
	    	        String ydCarWrkGp          = "";
	    	        
	    	        String sTrnEqpCd           = "";
	            	String sCarNo              = "";
//	            	String sCardNo             = "";
	        		jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	        		
	        		//권상실적위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
	        		jrParam.setField("YD_STK_COL_GP", ydUpWrLoc.substring(0, 6));
	        		
	        		JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStkColByPk", logId, mthdNm, "적치열조회");
	        		
	        		if (jsRst.size() <= 0) {
	        			commUtils.printLog(logId, "["+ydUpWrLoc.substring(0, 6)+"]차량정지위치 정보가 존재하지 않습니다.", "SL");
	        		} else {
	        			jsRst.first();
	        			sTrnEqpCd  = commUtils.trim(jsRst.getRecord().getFieldString("TRN_EQP_CD"   ));
	        			ydCarUseGp = commUtils.trim(jsRst.getRecord().getFieldString("YD_CAR_USE_GP"));
	        			sCarNo     = commUtils.trim(jsRst.getRecord().getFieldString("CAR_NO"       ));
//	        			sCardNo    = commUtils.trim(jsRst.getRecord().getFieldString("CARD_NO"      ));
	        			
	        			jrParam.setField("YD_CAR_USE_GP", ydCarUseGp);
	        			jrParam.setField("TRN_EQP_CD"   , sTrnEqpCd);
	        			jrParam.setField("STL_NO"       , sStlNo);
	        			
	        			/*
						SELECT *
						  FROM(SELECT * 
						         FROM(SELECT *  
						                FROM TB_YD_CARSCH 
						               WHERE DEL_YN     = 'N'
						                 AND 'L'        = :V_YD_CAR_USE_GP  
						                 AND TRN_EQP_CD = :V_TRN_EQP_CD
						               UNION ALL
						              SELECT A.*
						                FROM TB_YD_CARSCH A
						                   , TB_YD_STOCK B
						               WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
						                 AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
						                 AND A.DEL_YN = 'N'
						                 AND 'G'      = :V_YD_CAR_USE_GP  
						                 AND B.STL_NO = :V_STL_NO 
						               ) A
						          ORDER BY YD_CAR_SCH_ID DESC
						      ) B
						 WHERE ROWNUM <= 1
	        			 */
	        			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschByStlNoCarID", logId, mthdNm, "차량스케줄 조회");
	        			
	        			if (jsCarSch.size() <= 0){
	        				commUtils.printLog(logId, "차량스케줄 정보가 존재하지 않습니다.", "SL");
	        			} else {
	        				/*********************************************
	        		         * 하차개시 전문 송신 처리 - 구내운송
					         * 업무기준 Desc : 권상실적위치로 적치열에서 운송장비코드를 조회하고 그 코드로 차량스케줄을 조회하여 차량진행상태가
					         * 				하차검수 or 하차도착이면 하차개시로 차량스케줄을 업데이트하고 전문을 전송한다.
	        		         *********************************************/
	        				
	        				jsCarSch.absolute(1);
	        				
	        				ydCarSchId          = commUtils.trim(jsCarSch.getRecord().getFieldString("YD_CAR_SCH_ID"       )); //차량스케줄ID
		            		ydCarProgStat       = commUtils.trim(jsCarSch.getRecord().getFieldString("YD_CAR_PROG_STAT"    ));//야드차량진행상태
		            		ydCarUseGp          = commUtils.trim(jsCarSch.getRecord().getFieldString("YD_CAR_USE_GP"       ));//차량사용구분
		            		sTransEquipmentType = commUtils.trim(jsCarSch.getRecord().getFieldString("TRANS_EQUIPMENT_TYPE")); //운송장비TYPE P:PDA
		            		sCarKind            = commUtils.trim(jsCarSch.getRecord().getFieldString("CAR_KIND"            )); // 차량 종류
		            		ydCarWrkGp          = commUtils.trim(jsCarSch.getRecord().getFieldString("YD_CAR_WRK_GP"       )); // 차량 작업구분
		            		
		            		// PIDEV
		            		commUtils.printLog(logId, "ydCarSchId : " + ydCarSchId +
		            				" ydCarProgStat : " + ydCarProgStat +
		            				" ydCarUseGp : " + ydCarUseGp +
		            				" sTransEquipmentType : " + sTransEquipmentType +
		            				" sCarKind : " + sCarKind +
		            				" ydCarWrkGp : " + ydCarWrkGp, "SL");
		            		
		            		//하차검수이거나 하차도착일 때 하차개시 전문 송신
		            		if ("C".equals(ydCarProgStat) || "B".equals(ydCarProgStat)) {
		            			
		            			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		            			
		            			jrParam.setField("YD_CAR_SCH_ID"	, ydCarSchId); //차량스케줄ID
		    	    			jrParam.setField("YD_CAR_PROG_STAT"	, "D"       ); //차량진행상태 - 하차개시
		    	    			jrParam.setField("YD_CARUD_ST_DT"	, sCurrDt   ); //하차개시일시
		    	    			
		    	    			/*
								UPDATE TB_YD_CARSCH
								   SET MODIFIER         = :V_MODIFIER
								     , MOD_DDTT         = SYSDATE
								     , YD_CAR_PROG_STAT = :v_YD_CAR_PROG_STAT
								     , YD_CARUD_ST_DT   = :v_YD_CARUD_ST_DT
								 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
		    	    			 */
		    	    			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCarschProgStat", logId, mthdNm, "하차개시 등록");
		    	    			
		    	    			if ("L".equals(ydCarUseGp)) {
			    	    			/************************************
			    	    			 * 하차작업개시 송신 YDTSJ009
			    	    			 ************************************/
			    		    		jrYdMsg = JDTORecordFactory.getInstance().create();
			    	    			jrYdMsg.setField("JMS_TC_CD"		, "YDTSJ009");
			    	    			jrYdMsg.setField("YD_CAR_SCH_ID"	, ydCarSchId);
			    	    			jrYdMsg.setField("YD_STK_COL_GP"    , ydUpWrLoc.substring(0, 6));
			    	    			
			    	    			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDTSJ009", jrYdMsg));

		    	    			} else if ("P".equals(sTransEquipmentType)) { //출하PDA
		    	    				/************************************
			    	    			 * 코일제품이송 하차개시 전송PDA
			    	    			 ************************************/
		    	    				jrYdMsg = JDTORecordFactory.getInstance().create();
		    	    				
		    	    				//PIDEV            						
//            						if("Y".equals(sApplyYnPI)) {
            						
    		    	    				jrYdMsg.setField("MQ_TC_CD"    , "M10YDLMJ1111"); 
    		    	    				jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
    		    	    				jrYdMsg.setField("YD_GP"		, ydGp);
    									
    									jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("M10YDLMJ1111B", jrYdMsg));            							
            							
//            						} else {
//            							
//			    	    				jrYdMsg.setField("JMS_TC_CD"    , "YDDMR075"); 
//			    	    				jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
//			    	    				jrYdMsg.setField("YD_GP"		, ydGp);
//										
//										jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR075", jrYdMsg));
//										
//            						}
		            			}
		            		} // 전문송신
	        			} //차량스케줄 존재
	        			
	        			/*********************************************
        		         * 하차완료 전문 송신 처리 - 구내운송
						 * 업무기준 Desc : 마지막 코일을 권상하는 시점에 하차완료 전문을 전송함.
        		         *********************************************/
	        			//하차완료유무 체크
	        			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	        			jrParam.setField("YD_WBOOK_ID"	  , ydWbookId );
	        			jrParam.setField("YD_CRN_SCH_ID"  , ydCrnSchId);
	        			
	        			/*
						SELECT TS.YD_CAR_SCH_ID
						     , DECODE((SELECT COUNT(*) 
						                 FROM TB_YD_CARFTMVMTL 
						                WHERE YD_CAR_SCH_ID = TS.YD_CAR_SCH_ID
						                  AND DEL_YN = 'N'),1,'Y','N') AS CAR_UD_CMPL_YN --차량하차완료여부(권상실적에서 이송재료 삭제)
						     , TS.CAR_NO
						     , TS.ARR_WLOC_CD AS WLOC_CD
						     , TS.YD_PNT_CD3  AS YD_PNT_CD
						     , TS.TRANS_ORD_DATE
						     , TS.TRANS_ORD_SEQNO
						     , TS.TRANS_EQUIPMENT_TYPE  
						     , TS.YD_CAR_USE_GP
						     , TM.STL_NO
						     , TS.YD_EQP_WRK_STAT
						  FROM TB_YD_CARSCH     TS
						     , TB_YD_CARFTMVMTL TM
						     , TB_YD_CRNSCH     CS
						 WHERE TS.YD_CAR_SCH_ID = TM.YD_CAR_SCH_ID
						   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						   AND CS.YD_CRN_GRAB_USE_RULE_ID = TS.YD_CAR_SCH_ID
	        			 */
	        			JDTORecordSet jsCarUd = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarSchUd", logId, mthdNm, "하차완료 체크");
	        			if (jsCarUd.size() <= 0) {
	        				commUtils.printLog(logId, "차량스케줄 정보가 존재하지 않습니다.", "SL");
	        			} else {
	        				jsCarUd.first();
	        				String sCarUdCmplYn = jsCarUd.getRecord().getFieldString("CAR_UD_CMPL_YN" );
	        				String ydEqpWrkStat = jsCarUd.getRecord().getFieldString("YD_EQP_WRK_STAT");
	        				ydCarSchId          = jsCarUd.getRecord().getFieldString("YD_CAR_SCH_ID"  );
	        				
	        				// PIDEV
		            		commUtils.printLog(logId, "sCarUdCmplYn : " + sCarUdCmplYn +
		            				" ydEqpWrkStat : " + ydEqpWrkStat +
		            				" ydCarSchId : " + ydCarSchId +
		            				" ydCarWrkGp : " + ydCarWrkGp, "SL");
	        				
	        				// 하차완료
	        				if ("Y".equals(sCarUdCmplYn)) {
	        					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	        					jrParam.setField("YD_EQP_WRK_STAT"	    , "U"      ); //공차
	        					jrParam.setField("YD_CAR_PROG_STAT"	    , "E"      ); //하차완료
	        					jrParam.setField("YD_CAR_SCH_ID"        , ydCarSchId);
	        					jrParam.setField("YD_CARUD_CMPL_DT"     , commUtils.getDateTime14());
	        					
	        					/*
								UPDATE TB_YD_CARSCH
								   SET MOD_DDTT = SYSDATE
								     , MODIFIER = :V_MODIFIER
								     , YD_CAR_PROG_STAT = NVL(:V_YD_CAR_PROG_STAT, YD_CAR_PROG_STAT)
								     , YD_CARLD_ST_DT   = DECODE(NVL(:V_YD_CARLD_ST_DT  ,'NULL'),'NULL',YD_CARLD_ST_DT  ,SYSDATE)
								     , YD_CARLD_CMPL_DT = DECODE(NVL(:V_YD_CARLD_CMPL_DT,'NULL'),'NULL',YD_CARLD_CMPL_DT,SYSDATE)
								     , YD_CARUD_ST_DT   = DECODE(NVL(:V_YD_CARUD_ST_DT  ,'NULL'),'NULL',YD_CARUD_ST_DT  ,SYSDATE)
								     , YD_CARUD_CMPL_DT = DECODE(NVL(:V_YD_CARUD_CMPL_DT,'NULL'),'NULL',YD_CARUD_CMPL_DT,SYSDATE)
								     , YD_EQP_WRK_STAT  = NVL(:V_YD_EQP_WRK_STAT, YD_EQP_WRK_STAT)
								 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	        					 */
	        					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updMvCarSchCmpl", logId, mthdNm, "하차완료 등록");
	        					
	        					//반품,회송,부분하차인경우 여기서 차량출발처리를 한다.
	        					if (bCarMvYnRetn) {
	        						commUtils.printLog(logId, "TTcar 반품,회송,부분하차 자동차량출발 제외, 이전 차량 상차 스케줄로 원복", "SL");
	        						
	        						if ("TT".equals(sCarKind) && "3".equals(ydCarWrkGp)) {
	        							/**
	        							 * 현재 차량 스케줄 삭제 처리, 이전차량 스케줄 DEL_YN = Y 로 변경  
	        							 */
	        							jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	        							jrParam.setField("CAR_NO"   , sCarNo);
	        							
	        							/*
										SELECT * 
										  FROM(
										       SELECT A.YD_CAR_SCH_ID
										            , A.CAR_NO     
										            , A.DEL_YN
										            , DECODE(A.DEL_YN,'N','Y','Y','N') AS DEL_YN_TO -- 원복하기위한 뒤집기
										         FROM TB_YD_CARSCH A
										        WHERE CAR_NO = :V_CAR_NO
										        ORDER BY YD_CAR_SCH_ID DESC
										      )
										 WHERE ROWNUM < 3
	        							 */
	        							JDTORecordSet jsCarSchOld = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarSchReverseYN", logId, mthdNm, "이전차량스케줄 조회");
	        							
	        							if (jsCarSchOld.size() > 0) {
	        								
	        								String sDelYnTo      = "";
	        								String ydCarSchIdOld = "";
	        								
	        								for (int i = 1; i <= jsCarSchOld.size(); ++i) {
	        									jsCarSchOld.absolute(i);
	        									sDelYnTo      = commUtils.trim(jsCarSchOld.getRecord().getFieldString("DEL_YN_TO"    ));
	        									ydCarSchIdOld = commUtils.trim(jsCarSchOld.getRecord().getFieldString("YD_CAR_SCH_ID"));
	        								
	        									jrParam.setField("DEL_YN"       , sDelYnTo);
	        									jrParam.setField("YD_CAR_SCH_ID", ydCarSchIdOld);
	        									/*
												UPDATE TB_YD_CARSCH
												   SET MODIFIER  = :V_MODIFIER
												     , MOD_DDTT  = SYSDATE
												     , DEL_YN    = :V_DEL_YN
												 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	        									 */
	        									commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCarSchReverseYN", logId, mthdNm, "이전차량스케줄 복구");
	        								}
	        							}
	        							/**
	        							 * 이전 상차 완료 스케줄중 부분하차 코일 제외하고 나머지 상차 완료 원복  
	        							 */
	        							jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	        							jrParam.setField("CAR_NO"         , sCarNo);
	        							jrParam.setField("YD_CAR_SCH_ID"  , ydCarSchId);
	        							
	        							/*
										SELECT YD_CAR_SCH_ID
										     , STL_NO
										     , DEL_YN
										     , DECODE(DEL_YN,'N','Y','Y','N') AS DEL_YN_TO -- 원복하기위한 뒤집기
										  FROM TB_YD_CARFTMVMTL
										 WHERE YD_CAR_SCH_ID IN (SELECT YD_CAR_SCH_ID 
										                           FROM(SELECT A.YD_CAR_SCH_ID
										                                  FROM TB_YD_CARSCH A
										                                 WHERE CAR_NO = :V_CAR_NO
										                                 ORDER BY YD_CAR_SCH_ID DESC
										                               )
										                          WHERE ROWNUM < 3
										                         )
										   AND STL_NO IN (SELECT STL_NO
										                    FROM TB_YD_CARFTMVMTL
										                   WHERE YD_CAR_SCH_ID IN (SELECT YD_CAR_SCH_ID 
										                                             FROM(SELECT A.YD_CAR_SCH_ID
										                                                    FROM TB_YD_CARSCH A
										                                                   WHERE CAR_NO = :V_CAR_NO
										                                                   ORDER BY YD_CAR_SCH_ID DESC
										                                                 )
										                                            WHERE ROWNUM < 3
										                                          )
										                     AND YD_CAR_SCH_ID NOT IN :V_YD_CAR_SCH_ID
										                   MINUS
										                  SELECT STL_NO
										                    FROM TB_YD_CARFTMVMTL
										                   WHERE YD_CAR_SCH_ID IN (SELECT YD_CAR_SCH_ID 
										                                             FROM(SELECT A.YD_CAR_SCH_ID
										                                                    FROM TB_YD_CARSCH A
										                                                   WHERE CAR_NO = :V_CAR_NO
										                                                   ORDER BY YD_CAR_SCH_ID DESC
										                                                 )
										                                            WHERE ROWNUM < 3
										                                          )
										                     AND YD_CAR_SCH_ID IN :V_YD_CAR_SCH_ID
										                 )
	        							 */
	        							JDTORecordSet jsCarSchMtlOld = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarSchReverseYNMTL", logId, mthdNm, "이전차량스케줄재료 조회");
	        							
	        							if (jsCarSchMtlOld.size() > 0) {
	        								
	        								String sDelYnTo      = "";
	        								String ydCarSchIdOld = "";
	        								String sStlNoOld     = "";
	        								
	        								for (int i = 1; i <= jsCarSchMtlOld.size(); ++i) {
	        									jsCarSchMtlOld.absolute(i);
	        									sDelYnTo      = commUtils.trim(jsCarSchMtlOld.getRecord().getFieldString("DEL_YN_TO"    ));
	        									ydCarSchIdOld = commUtils.trim(jsCarSchMtlOld.getRecord().getFieldString("YD_CAR_SCH_ID"));
	        									sStlNoOld     = commUtils.trim(jsCarSchMtlOld.getRecord().getFieldString("STL_NO"       ));
	        								
	        									jrParam.setField("DEL_YN"       , sDelYnTo);
	        									jrParam.setField("YD_CAR_SCH_ID", ydCarSchIdOld);
	        									jrParam.setField("STL_NO"       , sStlNoOld);
	        									/*
												UPDATE TB_YD_CARFTMVMTL
												   SET MODIFIER  = :V_MODIFIER
												     , MOD_DDTT  = SYSDATE
												     , DEL_YN    = :V_DEL_YN
												 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
												   AND STL_NO        = :V_STL_NO
	        									 */
	        									commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCarSchReverseYNMTL", logId, mthdNm, "이전차량스케줄재료복구");
	        								} //end for	        								
	        							}
	        						} else {
	        							
	        							jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
	        	        				JDTORecordSet jsCarSchInfo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCarsch", logId, mthdNm, "차량스케줄 조회");
	        	        				
	        	        				if (jsCarSchInfo.size() > 0) {
	        	        					jsCarSchInfo.first();
	        	        					
		        							/**********************************************************
		            						* 코일출하차량 출발처리 - 맵비활성화
		            						**********************************************************/
		            						JDTORecord jrYdDmMsg = commUtils.getParam(logId, mthdNm, sModifier);
		        							jrYdDmMsg.setField("CAR_NO"         , commUtils.trim(jsCarSchInfo.getRecord().getFieldString("CAR_NO"         )));
		        							jrYdDmMsg.setField("CARD_NO"        , commUtils.trim(jsCarSchInfo.getRecord().getFieldString("CARD_NO"        )));			
		        							jrYdDmMsg.setField("SPOS_WLOC_CD"   , commUtils.trim(jsCarSchInfo.getRecord().getFieldString("SPOS_WLOC_CD"   )));
		        							jrYdDmMsg.setField("SPOS_YD_PNT_CD" , commUtils.trim(jsCarSchInfo.getRecord().getFieldString("YD_PNT_CD3"     )));
		        							jrYdDmMsg.setField("TRANS_ORD_DT"   , commUtils.trim(jsCarSchInfo.getRecord().getFieldString("TRANS_ORD_DATE" )));
		        							jrYdDmMsg.setField("TRANS_ORD_SEQNO", commUtils.trim(jsCarSchInfo.getRecord().getFieldString("TRANS_ORD_SEQNO")));
		        							//PIDEV_S :병행가동용:PI_YD
		        							//PIDEV_S :병행가동용:PI_YD
		        							jrYdDmMsg.setField("PI_YD",    	sPI_YD);		        							
		        							EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
		        							JDTORecord jrMsg = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { jrYdDmMsg });
		        							
		        							jrRtn = commUtils.addSndData(jrRtn, jrMsg);
	        	        				}
	        						}
	        					}
	        					
	        					//하차작업완료 송신YDTSJ010
        						if ("L".equals(ydCarUseGp)) {
        							/********************************
        							 * 소재차량하차완료 - 구내운송
        							 ********************************/
        							jrYdMsg = JDTORecordFactory.getInstance().create();
		    	    				jrYdMsg.setField("JMS_TC_CD"    , "YDTSJ010"); 
		    	    				jrYdMsg.setField("YD_SCH_CD"    , ydSchCd   );
		    	    				jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
		    	    				jrYdMsg.setField("YD_GP"		, ydGp      );
									
									jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDTSJ010", jrYdMsg));
        						}
        						
	        					if ("L".equals(ydEqpWrkStat)) { //영차

	        						//출하 PDA하차 작업 인경우
	        						if ("P".equals(sTransEquipmentType)) { //출하PDA
	        							/**********************************
	        							 * 코일제품이송 하차완료 전송PDA
	        							 **********************************/
	        							jrYdMsg = JDTORecordFactory.getInstance().create();
	        							
	        							//	PIDEV	        							
//	            						if("Y".equals(sApplyYnPI)) {
	            							
				    	    				jrYdMsg.setField("MQ_TC_CD"    , "M10YDLMJ1121"); 
				    	    				jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
				    	    				jrYdMsg.setField("YD_GP"		, ydGp      );
				    	    				jrYdMsg.setField("WR_DT"	    , sCurrDt   );
				    	    				
											jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("M10YDLMJ1121B", jrYdMsg));	            							
	            							
//	            						} else {
//
//				    	    				jrYdMsg.setField("JMS_TC_CD"    , "YDDMR076"); 
//				    	    				jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
//				    	    				jrYdMsg.setField("YD_GP"		, ydGp      );
//				    	    				jrYdMsg.setField("WR_DT"	    , sCurrDt   );
//				    	    				
//											jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR076", jrYdMsg));
//	            						}
	        						}
	        					} // L
	        				} //하차완료
	        			} //차량스케줄 존재
	        		} //저장위치정보 존재
	        		
	        		
	        		//차량이송재료 삭제
	        		jrParam = commUtils.getParam(logId, mthdNm, sModifier);
        			
        			jrParam.setField("YD_CAR_SCH_ID"	   , ydCarSchId);
        			jrParam.setField("DEL_YN"              , "Y"       );
        			jrParam.setField("STL_NO"              , sStlNo    );
        			/*
					UPDATE TB_YD_CARFTMVMTL
					   SET MODIFIER  = :V_MODIFIER
					     , MOD_DDTT  = SYSDATE
					     , DEL_YN    = :V_DEL_YN
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					   AND STL_NO        = :V_STL_NO
        			 */
	        		commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCarSchReverseYNMTL", logId, mthdNm, "차량이송재료 삭제");
        			
	            	//반품,회송,부분하차인경우 검수테이블 삭제 처리
	    			if (bCarMvYnRetn) {
						jrParam.setField("STL_NO"   , sStlNo);
						jrParam.setField("CAR_NO"   , sCarNo);
						/*
						UPDATE TB_YD_EXAMINATIONCHKLIST
						   SET MODIFIER         = :V_MODIFIER
						     , MOD_DDTT         = SYSDATE
						     , DEL_YN = 'Y'
						 WHERE DEL_YN = 'N'
						   AND STL_NO = :V_STL_NO
						   AND CAR_NO = :V_CAR_NO
						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCarExaminationDel", logId, mthdNm, "차량이송재료 삭제");
	    			}
	        	}
	        }
	        
	        /*********************************************
	         * 권하지시위치 차량
	         *********************************************/	        
	        if (!bCarMvYn) { //차량동간이적이 아니면
	        	if ("PT".equals(ydDnWoLoc.substring(2, 4)) || "TR".equals(ydDnWoLoc.substring(2, 4)) ) {
	        		commUtils.printLog(logId, " 권하지시위치 차량", "SL");
	        		
	    	        String ydCarSchId          = "";
	    	        String ydCarProgStat       = "";
	    	        String ydCarUseGp          = "";
	    	        String sTransEquipmentType = "";
//	    	        String sCarKind            = "";
	    	        String sCmbnCarldYn        = "";
	    	        
	    	        String sTrnEqpCd           = "";
	            	String sCarNo              = "";
	            	String sCardNo             = "";
	            	String sArrWlocCd          = "";
	            	String sTransOrdDate       = "";
	            	String sTransOrdSeqno      = "";
	        		
	        		jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	        		
	        		//권상실적위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
	        		jrParam.setField("YD_STK_COL_GP", ydDnWoLoc.substring(0, 6));
	        		
	        		/*
					SELECT *
					  FROM TB_YD_STKCOL
					 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
					   AND DEL_YN ='N'
	        		 */
	        		JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStkColByPk", logId, mthdNm, "상차위치 조회");
	        		
	        		if (jsRst.size() <= 0) {
	        			commUtils.printLog(logId, "["+ydDnWoLoc.substring(0, 6)+"]차량정지위치 정보가 존재하지 않습니다.", "SL");
	        		} else {
	        			jsRst.first();
	        			sTrnEqpCd  = commUtils.trim(jsRst.getRecord().getFieldString("TRN_EQP_CD"   ));
	        			ydCarUseGp = commUtils.trim(jsRst.getRecord().getFieldString("YD_CAR_USE_GP"));
	        			
	        			jrParam.setField("YD_CAR_USE_GP", ydCarUseGp);
	        			jrParam.setField("TRN_EQP_CD"   , sTrnEqpCd);
	        			jrParam.setField("STL_NO"       , sStlNo);
	        			
	        			/*
						SELECT *
						  FROM(SELECT * 
						         FROM(SELECT *  
						                FROM TB_YD_CARSCH 
						               WHERE DEL_YN     = 'N'
						                 AND 'L'        = :V_YD_CAR_USE_GP  
						                 AND TRN_EQP_CD = :V_TRN_EQP_CD
						               UNION ALL
						              SELECT A.*
						                FROM TB_YD_CARSCH A
						                   , TB_YD_STOCK B
						               WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
						                 AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
						                 AND A.DEL_YN = 'N'
						                 AND 'G'      = :V_YD_CAR_USE_GP  
						                 AND B.STL_NO = :V_STL_NO 
						               ) A
						          ORDER BY YD_CAR_SCH_ID DESC
						      ) B
						 WHERE ROWNUM <= 1
	        			 */
	        			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschByStlNoCarID", logId, mthdNm, "차량스케줄 조회");
	        			
	        			if (jsCarSch.size() <= 0){
	        				commUtils.printLog(logId, "차량스케줄 정보가 존재하지 않습니다.", "SL");
	        			} else {
	        				//차량진행상태를 파악하여 상차검수이거나 상차도착일 때만 상차개시 전문을 송신한다.
	        				ydCarSchId          = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID"       )); //차량스케줄ID
		            		ydCarProgStat       = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT"    )); //야드차량진행상태
		            		ydCarUseGp          = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_USE_GP"       )); //차량사용구분
		            		sCmbnCarldYn        = commUtils.nvl (jsCarSch.getRecord(0).getFieldString("CMBN_CARLD_YN"  ), "N"); //복수창고 마지막 창고,동 인경우
		            		
		            		sTransEquipmentType = commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_EQUIPMENT_TYPE")); //운송장비TYPE P:PDA
		            	    sCarNo 	            = commUtils.trim(jsCarSch.getRecord(0).getFieldString("CAR_NO"              ));
		            	    sCardNo	            = commUtils.trim(jsCarSch.getRecord(0).getFieldString("CARD_NO"             ));
		            		sTransOrdDate       = commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_DATE"      ));
		            		sTransOrdSeqno      = commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_SEQNO"     ));
		            		
		            		
		            		commUtils.printLog(logId, "sTransOrdDate  : " + sTransOrdDate , "SL");
		            		commUtils.printLog(logId, "sTransOrdSeqno : " + sTransOrdSeqno, "SL");
		            		commUtils.printLog(logId, "ydCarProgStat  : " + ydCarProgStat , "SL");
		            		commUtils.printLog(logId, "sCmbnCarldYn   : " + sCmbnCarldYn  , "SL");
		            		
		            		// PIDEV
		            		commUtils.printLog(logId, "ydCarUseGp   	 : " + ydCarUseGp    , "SL");
		            		
		            		//상차검수이거나 상차도착일 때 상차개시 전문 송신

		            		if (("3".equals(ydCarProgStat) || "2".equals(ydCarProgStat)) && !"E".equals(sCmbnCarldYn)) {
		            			/*
								SELECT STL_NO
								     , MATL_FTMV_WO_DT AS TRS_INDI_DT -- 이송지시
								     , SPOS_WLOC_CD
								     , ARR_WLOC_CD
								     , MTL_UGNT_GP AS URGENT_FRTOMOVE_WORD_GP -- 긴급구분
								  FROM TB_TS_MATL_FTMV_WO 
								 WHERE STL_NO            = :V_STL_NO
								   AND TS_MATL_FTMV_STAT_GP     = '1'
								   AND MATL_FTMV_WO_NML_HD_YN   = 'Y'
		            			 */
		            			JDTORecordSet jsTs = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getTsMtlArrWloc", logId, mthdNm, "착지개소코드 조회");
		            			
		            			if (jsTs.size() > 0) {
		            				sArrWlocCd = commUtils.trim(jsTs.getRecord(0).getFieldString("ARR_WLOC_CD"));
		            			}
		            			
		            			/***********************************
		            			 * 차량스케줄 상태 -> 상차개시
		            			 ***********************************/
	            				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	            				jrParam.setField("YD_CAR_SCH_ID"       , ydCarSchId ); //차량스케줄ID
                				jrParam.setField("YD_CAR_PROG_STAT"    , "4"        ); //차량진행상태 상차개시
            	    			jrParam.setField("YD_EQP_WRK_STAT"     , "U"        ); //작업상태(공차)
            	    			jrParam.setField("YD_CARLD_WRK_BOOK_ID", ydWbookId  ); //작업예약ID
            	    			jrParam.setField("YD_CARLD_ST_DT"      , sCurrDt    ); //상차개시일시
            	    			if ("L".equals(ydCarUseGp)) {					//구내운송
            	    				jrParam.setField("ARR_WLOC_CD"     , sArrWlocCd); //착지개소코드
            	    			}
	            				/*
								UPDATE TB_YD_CARSCH
								   SET MODIFIER         = :V_MODIFIER
								     , MOD_DDTT         = SYSDATE
								     , YD_CAR_PROG_STAT = :V_YD_CAR_PROG_STAT
								     , YD_EQP_WRK_STAT  = :V_YD_EQP_WRK_STAT
								     , YD_CARLD_ST_DT   = :V_YD_CARLD_ST_DT
								     , YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
								     , ARR_WLOC_CD      = NVL(:V_ARR_WLOC_CD, ARR_WLOC_CD)
								 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	            				 */
	            				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCarschProgStatLd", logId, mthdNm, "차량스케줄 수정");
	            				
		            			if ("G".equals(ydCarUseGp)) {
		            				/*******************
		            				 * 출하
		            				 *******************/
		            				if ("P".equals(sTransEquipmentType)) { //제품이송
		            					/**************************************
		            					 * 코일제품이송 상차개시 송신 YDDMR071
		            					 **************************************/
		            					jrYdMsg = JDTORecordFactory.getInstance().create();
		            					
		            					//PIDEV		            					
//	            						if("Y".equals(sApplyYnPI)) {
	            							
											jrYdMsg.setField("MQ_TC_CD"    , "M10YDLMJ1071"); 
											jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId	);
											jrYdMsg.setField("YD_GP"		, ydGp      	);	            							
											jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("M10YDLMJ1071B", jrYdMsg));
											
//	            						} else {
//	            							
//											jrYdMsg.setField("JMS_TC_CD"    , "YDDMR071"); 
//											jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
//											jrYdMsg.setField("YD_GP"		, ydGp      );
//											
//											jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR071", jrYdMsg));
//	            						}
	            						
		            				} else {
		            					jrParam.setField("COIL_NO"      , sStlNo);
		            					/*
										SELECT COIL_NO AS STL_NO 
										  FROM TB_DM_COILFRTOMOVEWORDDETAIL @DL_SMDB
										 WHERE 1 = 1
										   AND COIL_NO = :V_COIL_NO
										   AND DEL_YN  = 'N'
										   AND FRTOMOVE_WORD_SEQNO >= 700000
		            					 */
//PIDEV_S :병행가동용:PI_YD
		            					jrParam.setField("PI_YD",    	sPI_YD);			            					
		            					JDTORecordSet jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getRentprocFtmvWoTrgt_PIDEV", logId, mthdNm, "임가공대상재 조회");
		            					if (jsResult.size() > 0) {
		            						/***************************************************
		            						 * 임가공이송상하차개시 송신 YDDMR020
		            						 **************************************************/
		            						jrYdMsg = JDTORecordFactory.getInstance().create();
		            						
		            						//PIDEV		            					
//		            						if("Y".equals(sApplyYnPI)) {
		            							
												jrYdMsg.setField("MQ_TC_CD"    , "M10YDLMJ1075"); 
												jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId	);
												jrYdMsg.setField("YD_GP"		, ydGp      	);
												
												jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("M10YDLMJ1075", jrYdMsg));
												
//		            						} else {		            						
//		            						
//												jrYdMsg.setField("JMS_TC_CD"    , "YDDMR020"); 
//												jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
//												jrYdMsg.setField("YD_GP"		, ydGp      );
//												
//												jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR020", jrYdMsg));
//												
//		            						}
		            					} else {
		            						/***************************************************
		            						 * 출하상차작업개시 송신 YDDMR007 (코일출하상차개시)
		            						 ***************************************************/
		            						commUtils.printLog(logId, "출하상차작업개시 송신 YDDMR007 (코일출하상차개시)", "SL");
		            						jrYdMsg = JDTORecordFactory.getInstance().create();
		            						
		            						//PIDEV		            						
//		            						if("Y".equals(sApplyYnPI)) {

												jrYdMsg.setField("MQ_TC_CD"          	, "M10YDLMJ1071"); // JMSTC코드
												jrYdMsg.setField("MQ_TC_CREATE_DDTT"    , sCurrDt   ); // JMSTC생성일시
												jrYdMsg.setField("TRN_REQ_DATE"   		, sTransOrdDate ); // 운송작업지시일자
												jrYdMsg.setField("TRN_REQ_SEQ"  		, sTransOrdSeqno); // 운송작업지시순번													
												
												jrYdMsg.setField("CAR_NO"            	, sCarNo    ); // 차량번호
												jrYdMsg.setField("YD_GP"             	, "J"       ); // 야드구분
												jrYdMsg.setField("DIST_GOODS_GP"        , "H"       ); // 출하제품구분
												jrYdMsg.setField("SCH_YN"             	, "N"       ); // 스케쥴 여부
												
												jrYdMsg.setField("CARLOAD_START_DATE", commUtils.getDate8()); //상차개시일자
												jrYdMsg.setField("CARLOAD_START_TIME", commUtils.getTime6()); //상차개시시각
	            							
		            							
//		            						} else {
//		            						
//												jrYdMsg.setField("TC_CODE"           , "YDDMR007"); //JMSTC코드
//												jrYdMsg.setField("TC_CREATE_DDTT"    , sCurrDt   ); //JMSTC생성일시
//												jrYdMsg.setField("JMS_TC_CD"         , "YDDMR007"); //JMSTC코드
//												jrYdMsg.setField("JMS_TC_CREATE_DDTT", sCurrDt   ); //JMSTC생성일시
//												jrYdMsg.setField("CARD_NO"           , sCardNo   ); //카드번호
//												jrYdMsg.setField("CAR_NO"            , sCarNo    ); //차량번호
//												jrYdMsg.setField("YD_GP"             , "J"       ); //야드구분
//												jrYdMsg.setField("CARLOAD_START_DATE", commUtils.getDate8()); //상차개시일자
//												jrYdMsg.setField("CARLOAD_START_TIME", commUtils.getTime6()); //상차개시시각
//												jrYdMsg.setField("TRANS_WORD_DATE"   , sTransOrdDate ); //운송작업지시일자
//												jrYdMsg.setField("TRANS_WORD_SEQNO"  , sTransOrdSeqno); //운송작업지시순번
//											
//		            						}
		            						
											jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
		            					}
		            				}
		            			} else {
		            				/*************************************************
		            				 * 상차작업개시 송신 YDTSJ007 (구내운송 상차개시)
		            				 *************************************************/
		            				jrYdMsg = JDTORecordFactory.getInstance().create();
					    			jrYdMsg.setField("JMS_TC_CD"    , "YDTSJ007");					    			
					    			jrYdMsg.setField("ARR_WLOC_CD"  , sArrWlocCd); //착지개소코드
					    			jrYdMsg.setField("YD_SCH_CD"    , ydSchCd   );
			    	    			jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
			    	    			
			    	    			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDTSJ007", jrYdMsg));		            				
		            			}
		            			
		            			commUtils.printLog(logId, "상차작업개시 송신완료", "SL");
		            			
		            		} // 3 2 E
		            		
		            		if ("G".equals(ydCarUseGp)) { //출하차량
		            			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		            			jrParam.setField("YD_WBOOK_ID", ydWbookId);
		            			/*
								SELECT CARLD_CMPL_YN --상차완료유무
								     , (SELECT MAX(C.YD_SCH_CD) 
								          FROM TB_YD_CRNSCH C 
								         WHERE C.YD_WBOOK_ID = :V_YD_WBOOK_ID
								       ) AS YD_SCH_CD
								  FROM (
								        SELECT (CASE WHEN SUM(1) IS NULL THEN 'Y' ELSE 'N' END) AS CARLD_CMPL_YN
								          FROM TB_YD_CRNSCH    A
								             , TB_YD_CRNWRKMTL B
								         WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
								           AND A.YD_WBOOK_ID   = :V_YD_WBOOK_ID
								           AND A.DEL_YN = 'N'
								           AND B.DEL_YN = 'N'
								       ) A
								 WHERE 1 = 1
		            			 */
		            			JDTORecordSet jsCarLd = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarLdEndChk", logId, mthdNm, "상차완료유무 체크");
		            			if (jsCarLd.size() <= 0) {
		            				throw new Exception("해당 작업예약ID["+ydWbookId+"] 차량스케줄 정보가 존재하지 않습니다. 상차완료유무 체크 실패");
		            			} else {
		            				String sCarldCmplYn = jsCarLd.getRecord(0).getFieldString("CARLD_CMPL_YN"); 
		            				
		            				if ("P".equals(sTransEquipmentType)) {
		            					/**********************************************************
	            						 * 코일이송일품실적 YDDMR072
	            						 **********************************************************/
		            					jrYdMsg = JDTORecordFactory.getInstance().create();
		            					
		            					//PIDEV		            		        	
//										if("Y".equals(sApplyYnPI)) {
											
											jrYdMsg.setField("MQ_TC_CD"      , "M10YDLMJ1081");
			            		        	jrYdMsg.setField("YD_CAR_SCH_ID" , ydCarSchId);
			            					jrYdMsg.setField("STL_NO"		 , sStlNo    );
			            					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("M10YDLMJ1081B", jrYdMsg));
			            					
//										} else {
//											
//			            		        	jrYdMsg.setField("JMS_TC_CD"    , "YDDMR072");
//			            		        	jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
//			            					jrYdMsg.setField("STL_NO"		, sStlNo    );
//			            					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR072", jrYdMsg));
//			            					
//										}
		            					
		            				} else {
	            						jrParam.setField("STL_NO"       , sStlNo);
		            					
		            					/*
										SELECT STL_NO
										     , ''  AS RENTPROC_COMCD
										     , TRANS_ORD_SEQNO
										  FROM TB_YD_STOCK
										 WHERE STL_NO = :V_STL_NO
										   AND DEL_YN = 'N'
										   AND TRANS_ORD_SEQNO >= 700000 
		            					 */
//PIDEV_S :병행가동용:PI_YD
	            						jrParam.setField("PI_YD",    	sPI_YD);		
		            					JDTORecordSet jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYmPoFrtoInfo_PIDEV", logId, mthdNm, "임가공대상재 조회");
		            					if (jsResult.size() <= 0) {
		            						/**********************************************************
		            						 * 일품 상차실적 송신 YDDMR011 (코일일품출하상차실적 송신)
		            						 **********************************************************/
		            						jrYdMsg = JDTORecordFactory.getInstance().create();
											
		            						// PIDEV
//											if("Y".equals(sApplyYnPI)) {
												
												jrYdMsg.setField("MQ_TC_CD"     , "M10YDLMJ1081");
												jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
												jrYdMsg.setField("STL_NO"		, sStlNo    );
												
												if ("Y".equals(sCarldCmplYn)) { // 상차완료인 저장품이 ALL일때 처리
													jrYdMsg.setField("GOODS_EA","*");
							                    } else {
							                    	jrYdMsg.setField("GOODS_EA","1");
							                    }
												jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("M10YDLMJ1081A", jrYdMsg));
												
//											} else {
//												
//												jrYdMsg.setField("JMS_TC_CD"    , "YDDMR011"); 
//												jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
//												jrYdMsg.setField("STL_NO"		, sStlNo    );
//												
//												if ("Y".equals(sCarldCmplYn)) { // 상차완료인 저장품이 ALL일때 처리
//													jrYdMsg.setField("GOODS_EA","*");
//							                    } else {
//							                    	jrYdMsg.setField("GOODS_EA","1");
//							                    }
//												jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR011", jrYdMsg));
//												
//											}
		            					}
		            				}
		            			}
		            		} //출하차량
	        			}
	        		} //상차정지위치 존재
	        	}
	        }
	        
	        /*********************************************
	         * 권상위치 출측(Line-Off)
	         *********************************************/	    
	        JDTORecord jrParamSnd = commUtils.getParam(logId, mthdNm, sModifier);
	    	jrParamSnd.setField("YD_UP_WR_LOC"    , ydUpWrLoc); 
	    	jrParamSnd.setField("YD_SCH_CD"       , ydSchCd);

	    	/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdUpSendYN
	    	WITH TEMP_TBL AS (
	    	SELECT SUBSTR(:V_YD_UP_WR_LOC,1,6) AS YD_UP_WR_LOC
	    	     , :V_YD_SCH_CD    AS YD_SCH_CD
	    	  FROM DUAL
	    	)
	    	SELECT CASE WHEN YD_SCH_CD  IN (
	    	                                  'JAKD01LH','JAKD02LH','JAKD04LH','JAKD05LH','JAKD06LH'  --SPM5 출측
	    	                                 ,'JBKD01LH','JBKD02LH','JBKD04LH','JBKD05LH','JBKD06LH'  --SPM4 출측
	    	                                 ,'JCKD01LH','JCKD02LH','JCKD04LH','JCKD05LH','JCKD06LH'  --SPM3 출측
	    	                                 ,'JEKD01LH','JEKD02LH','JEKD04LH','JEKD05LH','JEKD06LH'  --SPM2 출측
	    	                                 ,'JHKD01LH','JHKD02LH','JHKD04LH','JHKD05LH','JHKD06LH'  --SPM1 출측
	    	                                 ,'JBFE04LH','JBFE06LH'                        --HFL5
	    	                                 ,'JCFD01LH','JCFD02LH','JCFD04LH','JCFD06LH'  --HFL4 출측
	    	                                 ,'JDFE04LH','JDFE06LH'                        --HFL3
	    	                                 ,'JFFE04LH','JFFE06LH'                        --HFL2
	    	                                 ,'JGFD01LH','JGFD02LH','JGFD04LH','JGFD06LH'  --HFL1 출측
	    	                                 ,'JAKD01LM','JATC01MM','JATC02MM'             --SPM5 입고
	    	                                 ,'JBKD01LM'                      ,'JBTC05MM'  --SPM4 입고
	    	                                 ,'JCKD01LM'                      ,'JCTC05MM'  --SPM3 입고
	    	                                 ,'JEKD01LM','JETC01MM','JETC02MM'             --SPM2 입고
	    	                                 ,'JHKD01LM','JHTC01MM','JHTC02MM'             --SPM1 입고
	    	                                 ,'JBFD01LM','JBTC01MM','JBTC02MM'             --HFL5 입고
	    	                                 ,'JCFD01LM','JCTC01MM','JCTC02MM'             --HFL4 입고
	    	                                 ,'JDFD01LM','JDTC01MM','JDTC02MM'             --HFL3 입고
	    	                                 ,'JFFD01LM','JFTC01MM','JFTC02MM'             --HFL2 입고
	    	                                 ,'JGFD01LM','JGTC01MM','JGTC02MM'             --HFL1 입고
	    	                                 )

	    	                  OR  SUBSTR(YD_SCH_CD,1,4) = 'JDYD'
	    	                  OR (SUBSTR(YD_SCH_CD,1,4) = 'JFYD' AND 'N' = (SELECT YD_SCH_PROH_EXN
	    	                                                                  FROM TB_YD_SCHRULE
	    	                                                                 WHERE YD_SCH_CD = 'JFFE01UH') -- #2HFL 보급스케쥴 금지유무
	    	                     )
	    	            THEN 'Y' ELSE 'N' END HR_LINE_OFF_SND_FLAG

	    	     , CASE WHEN YD_UP_WR_LOC IN ('JHKD01')
	    	             AND YD_SCH_CD    IN ('JHKD01LM','JHTC01MM','JHTC02MM','JHKD01LH','JHKD02LH','JHKD04LH','JHKD05LH','JHKD06LH') THEN 'YDH2L003'
	    	            WHEN YD_UP_WR_LOC IN ('JGFD01')
	    	             AND YD_SCH_CD    IN ('JGFD01LM','JGTC01MM','JGTC02MM','JGFD01LH','JGFD02LH','JGFD04LH','JGFD06LH') THEN 'YDH2L013'
	    	            WHEN YD_UP_WR_LOC IN ('JEKD02')
	    	             AND YD_SCH_CD    IN ('JEKD01LM','JETC01MM','JETC02MM','JEKD01LH','JEKD02LH','JEKD04LH','JEKD05LH','JEKD06LH') THEN 'YDH2L023'
	    	            WHEN YD_UP_WR_LOC IN ('JCKD03')
	    	             AND YD_SCH_CD    IN ('JCKD01LM','JCTC01MM','JCTC02MM','JCTC05MM','JCKD01LH','JCKD02LH','JCKD04LH','JCKD05LH','JCKD06LH') THEN 'YDH2L033'
	    	            WHEN YD_UP_WR_LOC IN ('JCFD04')
	    	             AND YD_SCH_CD    IN ('JCFD01LM','JCTC01MM','JCTC02MM','JCTC05MM','JCFD01LH','JCFD02LH','JCFD04LH','JCFD06LH') THEN 'YDH2L053'
	    	            WHEN YD_UP_WR_LOC IN ('JBKD04')
	    	             AND YD_SCH_CD    IN ('JBKD01LM','JBTC01MM','JBTC02MM','JBTC05MM','JBKD01LH','JBKD02LH','JBKD04LH','JBKD05LH','JBKD06LH') THEN 'YDH2L043'
	    	            WHEN YD_UP_WR_LOC IN ('JAKD05')
	    	             AND YD_SCH_CD    IN ('JAKD01LM','JATC01MM','JATC02MM','JATC05MM','JAKD01LH','JAKD02LH','JAKD04LH','JAKD05LH','JAKD06LH') THEN 'YDH2L073'
	    	            ELSE 'N' END HR_L2_LINE_OFF_SND_FLAG

	    	     , CASE WHEN YD_SCH_CD    IN ('JHKE03LH','JEKE03LH','JCKE03LH','JBKE03LH','JAKE03LH'  --SPM입측
	    	                                 ,'JHKD03LH'  -- 추가
	    	                                 ,'JGFE03LH','JFFE03LH','JDFE03LH','JCFE03LH','JBFE03LH'  --HFL입측
	    	                                  )
	    	            THEN 'Y' ELSE 'N' END HR_TAKE_OUT_SND_FLAG

	    	     , CASE WHEN YD_UP_WR_LOC = 'JHKE01' AND YD_SCH_CD = 'JHKE03LH' THEN 'YDH2L004'
	    	            WHEN YD_UP_WR_LOC = 'JHKD01' AND YD_SCH_CD = 'JHKD03LH' THEN 'YDH2L004' --추가
	    	            WHEN YD_UP_WR_LOC = 'JGFE01' AND YD_SCH_CD = 'JGFE03LH' THEN 'YDH2L014'
	    	            WHEN YD_UP_WR_LOC = 'JEKE02' AND YD_SCH_CD = 'JEKE03LH' THEN 'YDH2L024'
	    	            WHEN YD_UP_WR_LOC = 'JEKD02' AND YD_SCH_CD = 'JEKD03LH' THEN 'YDH2L024' --추가
	    	            WHEN YD_UP_WR_LOC = 'JCKE03' AND YD_SCH_CD = 'JCKE03LH' THEN 'YDH2L034'
	    	            WHEN YD_UP_WR_LOC = 'JCKD03' AND YD_SCH_CD = 'JCKD03LH' THEN 'YDH2L034' --추가
	    	            WHEN YD_UP_WR_LOC = 'JCFE04' AND YD_SCH_CD = 'JCFE03LH' THEN 'YDH2L054'
	    	            WHEN YD_UP_WR_LOC = 'JBKE04' AND YD_SCH_CD = 'JBKE03LH' THEN 'YDH2L044'
	    	            WHEN YD_UP_WR_LOC = 'JBKD04' AND YD_SCH_CD = 'JBKD03LH' THEN 'YDH2L044' --추가
	    	            WHEN YD_UP_WR_LOC = 'JAKE05' AND YD_SCH_CD = 'JAKE03LH' THEN 'YDH2L074'
	    	            WHEN YD_UP_WR_LOC = 'JAKD05' AND YD_SCH_CD = 'JAKD03LH' THEN 'YDH2L074' --추가
	    	            ELSE 'N' END HR_L2_TAKE_OUT_SND_FLAG

	    	  FROM TEMP_TBL
	        */
	        JDTORecordSet jsSndYn = commDao.select(jrParamSnd, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdUpSendYN", logId, mthdNm, "타시스템 송신여부");
	        
	        String sHrLineOffSndFlag 	= "N";
	        String sHrTakeOutSndFlag 	= "N";
	        String sHrL2LineOffSndFlag  = "N";
	        String sHrL2TakeOutSndFlag  = "N";
			if (jsSndYn.size() > 0) {
				jsSndYn.absolute(1);
				sHrLineOffSndFlag   = commUtils.trim(jsSndYn.getRecord().getFieldString("HR_LINE_OFF_SND_FLAG"));
				sHrL2LineOffSndFlag = commUtils.trim(jsSndYn.getRecord().getFieldString("HR_L2_LINE_OFF_SND_FLAG"));
				sHrTakeOutSndFlag   = commUtils.trim(jsSndYn.getRecord().getFieldString("HR_TAKE_OUT_SND_FLAG"));
				sHrL2TakeOutSndFlag = commUtils.trim(jsSndYn.getRecord().getFieldString("HR_L2_TAKE_OUT_SND_FLAG"));
			}
	        
			commUtils.printLog(logId, "열연조업LineOff송신여부   : "+ sHrLineOffSndFlag   + "열연조업TakeOut송신여부   : "+ sHrTakeOutSndFlag, "SL");
			commUtils.printLog(logId, "열연조업L2LineOff송신여부: "+ sHrL2LineOffSndFlag + "열연조업L2TakeOut송신여부: "+ sHrL2TakeOutSndFlag, "SL");
			// PIDEV
	        commUtils.printLog(logId, "ydUpWrLoc : " + ydUpWrLoc, "SL");
			
            if (ydUpWrLoc.startsWith("JFFE02") //HFL#2  //결속장
             || ydUpWrLoc.startsWith("JDFE03") //HFL#3
             || ydUpWrLoc.startsWith("JBFE05") //HFL#4
             
             || ydUpWrLoc.startsWith("JGFD01") //HFL#1
         	 || ydUpWrLoc.startsWith("JCFD04") //HFL#4
             
             || ydUpWrLoc.startsWith("JDCD01")  //크래들롤 CR->CD
             || ydUpWrLoc.startsWith("JFCD01")
             
             || ydUpWrLoc.startsWith("JHKD01") //SPM #1
             || ydUpWrLoc.startsWith("JEKD02") //SPM #2 //DD01 -> KD02
             || ydUpWrLoc.startsWith("JCKD03") //SPM #3
             || ydUpWrLoc.startsWith("JBKD04") //SPM #4
             || ydUpWrLoc.startsWith("JAKD05") //SPM #5  
 	         ) {
            	if ("Y".equals(sHrLineOffSndFlag) || "Y".equals(sHrTakeOutSndFlag)) { 
            		
    	        	jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
    	        	jrYdMsg.setField("JMS_TC_CD"         , "YDHRJ002"	); //JMSTC코드
    	        	jrYdMsg.setField("JMS_TC_CREATE_DDTT", sCurrDt      );
    	        	jrYdMsg.setField("STL_NO"            , sStlNo       ); //재료번호
    	        	
    	        	//REQ202405579856 진기양 계장  - 2HFL(FH) Take Out정보 인터페이스 요청 24.06.11
    	        	if("Y".equals(sHrLineOffSndFlag)){
    	        		jrYdMsg.setField("TREAT_GP"          , "3"    	    ); //1:보급, 2:보급취소, 3:추출, 4:Take-Out, 5:Take-In
    	        	}else{
    	        		jrYdMsg.setField("TREAT_GP"          , "4"    	    ); //1:보급, 2:보급취소, 3:추출, 4:Take-Out, 5:Take-In
    	        	}
    				
    				jrYdMsg.setField("YD_UP_CMPL_DT"     , sCurrDt      );
    				jrYdMsg.setField("YD_ISPTOR"         , ""           );
    				jrYdMsg.setField("YD_TAKE_OUT_DT"    , ""           );
    				jrYdMsg.setField("YD_TAKE_OUT_CD"    , ""           );
    				
    				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
    				commUtils.printLog(logId, "열연조업L3 정정추출완료실적 전문 송신", "SL");
    				
    				//HFL결속장에 대한 추출실적 전송 후 해당 스케줄코드로 다음 작업예약을 스케줄 기동 작업
    				if (ydUpWrLoc.startsWith("JBFE05")||   //HFL5
    					ydUpWrLoc.startsWith("JDFE03")||   //HFL3
    					ydUpWrLoc.startsWith("JFFE02")) {  //HFL2
    				
    					/*
						SELECT *
						  FROM TB_YD_STKLYR
						 WHERE YD_STK_COL_GP = SUBSTR(:V_YD_UP_WR_LOC,1,6)
						   AND STL_NO IS NULL
						   AND YD_STK_LYR_ACT_STAT = 'E'
						   AND YD_STK_BED_NO <> '00'
						 ORDER BY YD_STK_BED_NO
    					 */
    					JDTORecordSet jsStkLyr = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStklyrStlchk", logId, mthdNm, "결속장 보급위치 조회");
    					
    					if (jsStkLyr.size() > 0) {
    						JDTORecord jrCrnSchMsg = commUtils.getParam(logId, mthdNm, sModifier);
    						jrCrnSchMsg.setField("JMS_TC_CD"	     , "YDYDJ551");
    						jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT", sCurrDt   ); //JMSTC생성일시
    						jrCrnSchMsg.setField("YD_WBOOK_ID"       , ""        );
    						jrCrnSchMsg.setField("YD_EQP_ID"         , ""        );
    						jrCrnSchMsg.setField("YD_SCH_CD"         , ydUpWrLoc.substring(0, 2)+"FE01UH");
    						
    						jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
    						
    						commUtils.printLog(logId, "열연조업L3 정정추출완료실적 전문 송신 후 스케줄 기동", "SL");
    					} else {
    						commUtils.printLog(logId, "열연조업L3 정정추출완료실적 보급위치 없음", "SL");
    					}
    				}
                }
            	
            	jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
            	if (sHrL2LineOffSndFlag.startsWith("YDH2")) { 
            		jrYdMsg.setField("JMS_TC_CD"    , sHrL2LineOffSndFlag);				//  전문코드
					jrYdMsg.setField("YD_EQP_ID"    , ydUpWrLoc.substring(0,6));		// 야드설비ID
					jrYdMsg.setField("YD_STK_BED_NO", ydUpWrLoc.substring(6,8));		// 야드적치Bed번호 
					jrYdMsg.setField("STL_NO"       , sStlNo);							// 재료번호
					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDH2", jrYdMsg));
					commUtils.printLog(logId, "SPM1 정정출측Line-Off실적 전문("+sHrL2LineOffSndFlag+") 전문 송신", "SL");	
            		
            	}
            }
	        /*********************************************
	         * 권상위치 입측(Take-Out)
	         *********************************************/            
            if (ydUpWrLoc.startsWith("JHKE01")    //SPM1
             || ydUpWrLoc.startsWith("JEKE02")    //SPM2
             || ydUpWrLoc.startsWith("JCKE03")    //SPM3
 	 		 || ydUpWrLoc.startsWith("JBKE04")    //SPM4
 	 		 || ydUpWrLoc.startsWith("JAKE05")    //SPM5
 	 		 || ydUpWrLoc.startsWith("JGFE01")    //HFL1
 	         || ydUpWrLoc.startsWith("JCFE04")    //HFL4
 	         
 	         || ydUpWrLoc.startsWith("JGFD01")    //HFL1 G동 #1HFL출측TakeOut
	         || ydUpWrLoc.startsWith("JCFD04")    //HFL4 C동 #4HFL출측TakeOut
 	         // 추출존 TAKE OUT
 	         || ydUpWrLoc.startsWith("JHKD01")    //SPM1
             || ydUpWrLoc.startsWith("JEKD02")    //SPM2
             || ydUpWrLoc.startsWith("JCKD03")    //SPM3
 	 		 || ydUpWrLoc.startsWith("JBKD04")    //SPM4
 	 		 || ydUpWrLoc.startsWith("JAKD05")    //SPM5
 	 		
 	 	     ) { //HFL5 
            	// JGFD03LH JCFD03LH 조업L2 확인후
            	// 스케줄코드 추가 com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdUpSendYN
            	if ("Y".equals(sHrTakeOutSndFlag)) {
            		
    	        	jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
    	        	jrYdMsg.setField("JMS_TC_CD"         , "YDHRJ002" ); //JMSTC코드
    	        	jrYdMsg.setField("JMS_TC_CREATE_DDTT", sCurrDt    );
    	        	jrYdMsg.setField("STL_NO"            , sStlNo     ); //재료번호
    				jrYdMsg.setField("TREAT_GP"          , "4"    	  ); //1:보급, 2:보급취소, 3:추출, 4:Take-Out, 5:Take-In
    				jrYdMsg.setField("YD_UP_CMPL_DT"     , sCurrDt    );
    				jrYdMsg.setField("YD_ISPTOR"         , ydIsptor   );
	    			jrYdMsg.setField("YD_TAKE_OUT_DT"    , ydTakeOutDt);
	    			jrYdMsg.setField("YD_TAKE_OUT_CD"    , ydTakeOutCd);
    				
    				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
    				commUtils.printLog(logId, "열연조업L3 정정추출완료실적 전문 송신", "SL");
                 }
            	
            	jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
            	
            	if (sHrL2TakeOutSndFlag.startsWith("YDH2")) { 
            		jrYdMsg.setField("JMS_TC_CD"    , sHrL2TakeOutSndFlag);			// SPM1 전문코드
	    			jrYdMsg.setField("YD_EQP_ID"    , ydUpWrLoc.substring(0,6));	// 야드설비ID
	    			jrYdMsg.setField("YD_STK_BED_NO", ydUpWrLoc.substring(6,8));	// 야드적치Bed번호 
	    			jrYdMsg.setField("STL_NO"       , sStlNo);						// 재료번호
	    			
	    			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDH2", jrYdMsg));
					commUtils.printLog(logId, "정정입측TAKE-OUT실적 전문("+sHrL2TakeOutSndFlag+") 전문 송신", "SL");
            	}
            }
            
	        /*********************************************
	         * 권상위치 지포장(Line-Off)
	         *********************************************/            
            if (ydUpWrLoc.startsWith("JBGF01")
             || ydUpWrLoc.startsWith("JCGF01")
 	         || ydUpWrLoc.startsWith("JEGF01")
 	         || ydUpWrLoc.startsWith("JFGF01")
 	         || ydUpWrLoc.startsWith("JHGF01")) { 
            	
            	if ("JBGF01LM".equals(ydSchCd)
            	 || "JCGF01LM".equals(ydSchCd)
            	 || "JEGF01LM".equals(ydSchCd)
            	 || "JFGF01LM".equals(ydSchCd)
            	 || "JHGF01LM".equals(ydSchCd)) {
            		
    	        	jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
    	        	jrYdMsg.setField("JMS_TC_CD"         , "YDHRJ002" ); //JMSTC코드
    	        	jrYdMsg.setField("JMS_TC_CREATE_DDTT", sCurrDt    );
    	        	jrYdMsg.setField("STL_NO"            , sStlNo     ); //재료번호
    				jrYdMsg.setField("TREAT_GP"          , "3"    	  ); //1:보급, 2:보급취소, 3:추출, 4:Take-Out, 5:Take-In
    				jrYdMsg.setField("YD_UP_CMPL_DT"     , sCurrDt    );
    				jrYdMsg.setField("YD_ISPTOR"         , ""         );
    				jrYdMsg.setField("YD_TAKE_OUT_DT"    , ""         );
    				jrYdMsg.setField("YD_TAKE_OUT_CD"    , ""         );

    				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
    				commUtils.printLog(logId, "열연조업L3 정정추출완료실적 전문 송신", "SL");
    				
    				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStklyrStlGfchk 
    				SELECT * 
    				  FROM TB_YD_WRKBOOK
    				 WHERE DEL_YN = 'N'
    				   AND SUBSTR(YD_SCH_CD,1,2) = SUBSTR(:V_YD_UP_WR_LOC,1,2)
    				   AND YD_SCH_CD LIKE 'J_GF01_H'   
    				   AND 'Y' = CASE WHEN (SELECT COUNT(*)
    				                          FROM TB_YD_STKLYR
    				                         WHERE YD_STK_COL_GP = SUBSTR(:V_YD_UP_WR_LOC,1,6)
    				                           AND STL_NO IS NULL
    				                           AND YD_STK_LYR_ACT_STAT = 'E'
    				                           AND YD_STK_BED_NO <> '00') > 0 THEN 'Y'
    				                   ELSE 'N' END 
    				ORDER BY YD_SCH_PRIOR, YD_WBOOK_ID     
    				*/ 
    				JDTORecordSet jsWbookGf = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStklyrStlGfchk", logId, mthdNm, "결속장 보급위치 조회");
					
					if (jsWbookGf.size() > 0) {
						
						JDTORecord jrCrnSchMsg = commUtils.getParam(logId, mthdNm, sModifier);
						
						jrCrnSchMsg.setField("JMS_TC_CD"	     , "YDYDJ551");
						jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT", sCurrDt   ); //JMSTC생성일시
						jrCrnSchMsg.setField("YD_WBOOK_ID"       , commUtils.trim(jsWbookGf.getRecord(0).getFieldString("YD_WBOOK_ID")) );
						jrCrnSchMsg.setField("YD_EQP_ID"         , ""        );
						
						jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
			
						commUtils.printLog(logId, "지포장 스케쥴 기동처리", "SL");
					} else {
						commUtils.printLog(logId, "지포장위치에 코일이 있습니다", "SL");
						
					}
            	}
            }
            
	        /*********************************************
	         * 권상위치 크래들롤
	         *********************************************/
            if (ydUpWrLoc.startsWith("JDCD01") || ydUpWrLoc.startsWith("JFCD01")) { //CR->CD 크래들롤

            	jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
				jrYdMsg.setField("JMS_TC_CD"         , "YDHRJ003");	//전문코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", sCurrDt   );
				jrYdMsg.setField("STL_NO"            , sStlNo    );	//재료번호
				
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg); //YDHRJ003
				
				/*************************************
				 * 크래들롤 보급 작업예약 스케쥴 기동
				 *************************************/
				String sApp = coilDao.ApplyYn(logId, mthdNm, "APP319", "J", "*");
				commUtils.printLog(logId, "크래들롤 자동보급 : "+ sApp, "SL");
				if( "Y".equals(sApp) ) {
					
					jrParam.setField("YD_BAY_GP", ydUpWrLoc.substring(1, 2));
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdWrkbookCdChk
					SELECT *
					  FROM (
					        SELECT A.YD_SCH_CD
					             , A.YD_WBOOK_ID
					             , ROW_NUMBER() OVER(ORDER BY A.YD_WBOOK_ID) AS SEQ
					          FROM TB_YD_WRKBOOK A
					         WHERE A.YD_GP      = 'J'
					           AND A.YD_BAY_GP  = :V_YD_BAY_GP
					           AND A.YD_SCH_CD IN ('JDCD01UH', 'JFCD01UH')
					           AND NOT EXISTS(SELECT 1 FROM TB_YD_CRNSCH B WHERE B.YD_SCH_CD = A.YD_SCH_CD AND B.DEL_YN = 'N')
					           AND A.DEL_YN = 'N'
					       ) X
					 WHERE X.SEQ = 1
					*/
					JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdWrkbookCdChk", logId, mthdNm, "크래들롤 자동보급 대상 조회");
					if (jsRst.size() > 0) {
						JDTORecord jrCrnSchMsg = commUtils.getParam(logId, mthdNm, sModifier);
						jrCrnSchMsg.setField("JMS_TC_CD"         , "YDYDJ551"); //
						jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
						jrCrnSchMsg.setField("YD_WBOOK_ID"       , jsRst.getRecord(0).getFieldString("YD_WBOOK_ID"));
						
						jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
						
						commUtils.printLog(logId, "크래들롤 작업예약 스케줄 기동", "SL");
					} else {
						commUtils.printLog(logId, "크래들롤 작업예약 없음", "SL");
					}
				}
            }
            
             
            
            //텔레스코프 추출 시 열연정정추출완료실적(YDHRJ006)
            if ("JFTD01LH".equals(ydSchCd) && "JFTD0101".equals(ydUpWrLoc)){
            
	            jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
				jrYdMsg.setField("JMS_TC_CD"         , "YDHRJ006");					//열연조업 L3 정정보급완료 실적  전문코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", sCurrDt);
				jrYdMsg.setField("TREAT_GP"          , "3");						//3: 추출완료 4:TAKE OUT완료
				jrYdMsg.setField("STL_NO"            , sStlNo);						//재료번호
				jrYdMsg.setField("YD_UP_CMPL_DT"     , sCurrDt);	//야드권하완료일시 
				
				
				//열연조업 L3 정정추출완료 실적 전송
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				commUtils.printParam(logId, jrRtn);
				commUtils.printLog(logId, "○○○ 열연조업 L3 열연정정추출완료 실적 송신", "SL");
            }
            
            /**********************************************
			 * HFL결속장 크레인스케줄 기동
			 **********************************************/
            String sApp808 = coilDao.ApplyYn(logId, mthdNm, "APP808","J","*"); //HFL결속장 크레인스케줄 기동 여부
			commUtils.printLog(logId, "HFL결속장 크레인스케줄 기동여부 : " + sApp808, "SL");
			if ("Y".equals(sApp808)) {
				jrParam.setField("YD_BAY_GP", ydUpWrLoc.substring(1, 2));
				/*
				SELECT *
				  FROM (
				        SELECT YD_SCH_CD
				             , YD_WBOOK_ID
				             , (SELECT YD_WRK_CRN FROM TB_YD_SCHRULE B WHERE B.YD_SCH_CD = A.YD_SCH_CD) AS YD_EQP_ID
				          FROM TB_YD_WRKBOOK A
				         WHERE DEL_YN    = 'N'
				           AND YD_GP     = 'J'
				           AND YD_BAY_GP = :V_YD_BAY_GP
				           AND NOT EXISTS(SELECT 1 FROM USRYDA.TB_YD_CRNSCH B WHERE B.DEL_YN = 'N' AND B.YD_SCH_CD = A.YD_SCH_CD )
				           AND YD_BAY_GP IN ('F','D','B') --// HFL결속장이 있는 동
				           AND YD_SCH_CD LIKE 'J_FD01LM'  --HFL입고
				           AND 'N' = NVL((SELECT MATL_SUP_MTD_GP
				                            FROM TB_YD_STKCOL
				                           WHERE YD_GP = 'J'
				                             AND YD_EQP_GP = 'FE'
				                             AND YD_BAY_GP = A.YD_BAY_GP
				                             AND YD_STK_COL_NO IN ('02','03','05')
				                         ), 'N')
				        ORDER BY YD_WBOOK_ID
				       ) AA
				 WHERE ROWNUM <= 1
				 */
				JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdWrkbookHFLChk", logId, mthdNm, "자동보급 대상 조회");	
				
				if (jsRst.size() > 0) {
					for(int ii=0; ii<jsRst.size(); ii++){
						JDTORecord jrCrnSchMsg = commUtils.getParam(logId, mthdNm, sModifier);
						jrCrnSchMsg.setField("JMS_TC_CD"         , "YDYDJ551"); //
						jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
						jrCrnSchMsg.setField("YD_SCH_CD"         , jsRst.getRecord(ii).getFieldString("YD_SCH_CD"  )); 
						jrCrnSchMsg.setField("YD_EQP_ID"         , jsRst.getRecord(ii).getFieldString("YD_EQP_ID"  ));
						jrCrnSchMsg.setField("YD_WBOOK_ID"       , jsRst.getRecord(ii).getFieldString("YD_WBOOK_ID"));
						
							jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);						
							commUtils.printLog(logId, "다음 HFL결속장 작업예약 스케줄 기동", "SL");
					}

				} else {
					commUtils.printLog(logId, "HFL결속장 작업예약 없음", "SL");
				}
			}
			
			/**********************************************
			 * 지포장 자동 보급
			 **********************************************/
			String sApp807 = coilDao.ApplyYn(logId, mthdNm, "APP807","J","*"); //지포장 자동보급 여부
			commUtils.printLog(logId, "지포장 자동보급여부 : " + sApp807, "SL");
			if ("Y".equals(sApp807)) {
				JDTORecord jrMsg = commUtils.getParam(logId, mthdNm, sModifier);
				
				EJBConnector ejbConn = new EJBConnector("default", "CCoilL2RcvSeEJB", this);
				ejbConn.trx("procGWrapAutoSup", new Class[] { JDTORecord.class }, new Object[] { jrMsg });
			}
			
			/**********************************************
			 * 스케쥴 중복CHECK
			 * 현재 작업지시와 TO위치 동일한 작업지시가 있는 경우 TO위치재기동
			 **********************************************/
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CRN_SCH_ID"  , ydCrnSchId);
			jrParam.setField("YD_SCH_CD"      , ydSchCd);
			jrParam.setField("YD_EQP_ID"      , ydEqpId ); 
			
			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdLocDup
			SELECT A1.YD_CRN_SCH_ID
			     , A1.YD_DN_WO_LOC 
			     , (SELECT B1.YD_CRN_SCH_ID
			          FROM TB_YD_CRNSCH B1
			         WHERE B1.DEL_YN = 'N'
			           AND B1.YD_GP  = 'J'
			           AND B1.YD_DN_WO_LOC     = A1.YD_DN_WO_LOC 
			           AND B1.YD_DN_WO_LAYER   = A1.YD_DN_WO_LAYER
			           AND B1.YD_CRN_SCH_ID   != A1.YD_CRN_SCH_ID
			           AND B1.YD_WRK_PROG_STAT = 'W'
			           AND B1.YD_EQP_ID        = A1.YD_EQP_ID
			           AND ROWNUM = 1
			        ) AS DUP_YD_CRN_SCH_ID
			  FROM TB_YD_CRNSCH A1
			 WHERE A1.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			   AND A1.YD_EQP_ID     = :V_YD_EQP_ID
			   AND A1.DEL_YN = 'N'  
			   AND SUBSTR(YD_DN_WO_LOC,3,2) BETWEEN '01' AND '99'
			   AND 'Y' = CASE WHEN YD_DN_WO_LOC IN ('XX010101') THEN 'N'
			                  WHEN YD_DN_WO_LOC IS NULL THEN 'N'
			                  ELSE 'Y' END 
			*/
			JDTORecordSet jsCrnDup = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdLocDup", logId, mthdNm, "TO위치 중복");	
			
			if (jsCrnDup.size() > 0) {
				
				String sDupYdCrnSchId = commUtils.trim(jsCrnDup.getRecord(0).getFieldString("DUP_YD_CRN_SCH_ID")); 
				
				if(!"".equals(sDupYdCrnSchId)) {
					commUtils.printLog(logId, "TO위치 중복 발생:ydCrnSchId:"+ ydCrnSchId + "////sDupYdCrnSchId:" + sDupYdCrnSchId, "SL");
					jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
					jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ556"               ); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); 
					jrYdMsg.setField("YD_CRN_SCH_ID"     , sDupYdCrnSchId           ); 
					
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);					
				
				}	
			}
			
            /**********************************************
			 * 공냉재 자동입고 크레인스케줄 기동
			 **********************************************/
            String sApp824 = coilDao.ApplyYn(logId, mthdNm, "APP824","J","*"); //공냉재 자동입고
			commUtils.printLog(logId, "공냉재 자동입고 크레인스케줄 기동여부 : " + sApp824, "SL");
			if ("Y".equals(sApp824)) {
				jrParam.setField("YD_SCH_CD", ydSchCd);
				/*
				SELECT *
				  FROM (
				        SELECT YD_SCH_CD
				             , YD_WBOOK_ID
				             , (SELECT YD_WRK_CRN FROM TB_YD_SCHRULE B WHERE B.YD_SCH_CD = A.YD_SCH_CD) AS YD_EQP_ID
				          FROM TB_YD_WRKBOOK A
				         WHERE DEL_YN    = 'N'
				           AND YD_GP     = 'J'
				           AND YD_SCH_CD = :V_YD_SCH_CD
				           AND NOT EXISTS(SELECT 1 FROM USRYDA.TB_YD_CRNSCH B 
				                           WHERE B.DEL_YN = 'N'
				                             AND B.YD_SCH_CD   = A.YD_SCH_CD 
				                             AND B.YD_WBOOK_ID = A.YD_WBOOK_ID)
				           AND YD_SCH_CD LIKE 'J_YD04MM' --공냉재자동입고
				           AND NVL(SCH_CNCL_YN, 'N') != 'Y'        --사용자 취소 제외
				         ORDER BY YD_WBOOK_ID
				       ) AA
				 WHERE ROWNUM <= 1
				 */
				JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdWrkbookAirclChk", logId, mthdNm, "공냉재자동이적 조회");	
				
				if (jsRst.size() > 0) {
					JDTORecord jrCrnSchMsg = commUtils.getParam(logId, mthdNm, sModifier);
					jrCrnSchMsg.setField("JMS_TC_CD"         , "YDYDJ551"); //
					jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrCrnSchMsg.setField("YD_SCH_CD"         , jsRst.getRecord(0).getFieldString("YD_SCH_CD"  )); 
					jrCrnSchMsg.setField("YD_WBOOK_ID"       , jsRst.getRecord(0).getFieldString("YD_WBOOK_ID"));
					
					jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
					
					commUtils.printLog(logId, "다음 공냉재자동입고 작업예약 스케줄 기동", "SL");
				} else {
					commUtils.printLog(logId, "공냉재자동입고 작업예약 없음", "SL");
				}
			}
			
			/**********************************************
			 * 제품 동간이적 크레인스케줄 기동
			 **********************************************/
			String sAPP835_YN = coilDao.ApplyYn(logId, mthdNm, "APP835", "J", "*"); // 제품 동간이적 스케줄 기동
			if ("Y".equals(sAPP835_YN)) {
				if ("TC".equals(ydSchCd.substring(2, 4)) && "UM".equals(ydSchCd.substring(6, 8))) {
					/***********************************************************
					*  제품 대차 작업예약에 있는거 기동 할수 있는지 Check 하여 1개 기동처리함
					***********************************************************/
					jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
					
					jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ555"	);
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", sCurrDt  	);
					jrYdMsg.setField("YD_EQP_ID"         , ydEqpId		); // 야드설비ID
					jrYdMsg.setField("YD_SCH_CD"         , ydSchCd		); // 스케쥴코드

					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				}
			}
			
			/**********************************************************
			 * 2단작업으로 인한 1단 출하대상 스케줄 미기동시 크레인 스케줄 기동
			 **********************************************************/
			String sAPP836_YN = coilDao.ApplyYn(logId, mthdNm, "APP836", "J", "*");
			if ("Y".equals(sAPP836_YN) && "002".equals(ydUpWrLayer)) {
				jrParam.setField("YD_STK_COL_GP" , ydUpWrLoc.substring(0, 6) );
			    jrParam.setField("YD_STK_BED_NO" , ydUpWrLoc.substring(6, 8));
				/*
				SELECT    WB.*
				      FROM  TB_YD_WRKBOOKMTL CM
				          , TB_YD_WRKBOOK   WB
				          , TB_YD_CARPOINT  CP
				          , (
				             SELECT (SELECT STL_NO
				                      FROM TB_YD_STKLYR
				                     WHERE YD_STK_COL_GP = LY.YD_STK_COL_GP
				                       AND YD_STK_BED_NO = LY.L_BED
				                       AND YD_STK_LYR_NO = '001') AS L_STL_NO
				                 , (SELECT STL_NO
				                      FROM TB_YD_STKLYR
				                     WHERE YD_STK_COL_GP = LY.YD_STK_COL_GP
				                       AND YD_STK_BED_NO = LY.R_BED
				                       AND YD_STK_LYR_NO = '001') AS R_STL_NO
				              FROM (SELECT DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
				                                                    '002', SL.YD_STK_BED_NO)                               AS L_BED
				                         , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_BED_NO,
				                                                    '002', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'))
				                                                                                                           AS R_BED
				                         , SL.*
				                      FROM TB_YD_STKLYR  SL
				                     WHERE SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
				                       AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
				                       AND SL.YD_STK_LYR_NO = '002'
				                   ) LY
				              WHERE 1 = 1       
				             ) SL
				     WHERE CM.STL_NO IN (SL.L_STL_NO, SL.R_STL_NO)   
				       AND WB.YD_WBOOK_ID = CM.YD_WBOOK_ID
				       AND WB.CAR_NO  = CP.CAR_NO
				       AND CM.DEL_YN  = 'N' 
				       AND CP.DEL_YN  = 'N'
				       AND CP.YD_GP   = 'J'
				       AND WB.YD_GP   = 'J'
				       AND WB.CAR_NO IS NOT NULL       
				       AND SUBSTR(WB.YD_SCH_CD, 3, 2) IN ('PT','TR') 
				       AND NOT EXISTS (SELECT 1
				                     FROM TB_YD_WRKBOOK    WR
				                        , TB_YD_WRKBOOKMTL WM
				                        , TB_YD_CRNSCH     CS
				                    WHERE WR.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                      AND WR.YD_WBOOK_ID = CS.YD_WBOOK_ID
				                      AND WR.YD_GP       = 'J'
				                      AND WR.DEL_YN      = 'N'
				                      AND WM.DEL_YN      = 'N'
				                      AND CS.DEL_YN      = 'N'
				                      --AND WM.STL_NO      = CM.STL_NO
				                      AND WR.YD_WBOOK_ID = WB.YD_WBOOK_ID
				                  )
				 */
				JDTORecordSet jsCarLdSeq = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getDmWrkbookList_PIDEV", logId, mthdNm, "출하작업예약 조회");

				if (jsCarLdSeq.size() > 0 ) {
					
					JDTORecord jrDmSch = commUtils.getParam(logId, mthdNm, sModifier); 
					jrDmSch.setField("JMS_TC_CD"		 , "YDYDJ552");  //
					jrDmSch.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrDmSch.setField("YD_SCH_CD"         , ""); //야드스케쥴코드
					jrDmSch.setField("YD_EQP_ID"         , ""); //야드설비ID
					
					for (int i = 1; i <= jsCarLdSeq.size(); i++) {
						jsCarLdSeq.absolute(i);
						jrDmSch.setField("YD_WBOOK_ID" + i, jsCarLdSeq.getRecord().getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrDmSch.setField("SCH_CNT"        , "" + i); //작업예약 개수
					}
					commUtils.printLog(logId, "크레인스케줄 기동", "SL");
				}	
			}

			/**********************************************************
			 * 2단작업으로 인한 1단 보급대상 스케줄 미기동시 크레인 스케줄 기동
			 **********************************************************/
			String sAPP019_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","019"); 
			commUtils.printLog(logId, "1단 보급대상 미기동 크레인스케줄기동 : " + sAPP019_YN, "SL");
			if ("Y".equals(sAPP019_YN) && "002".equals(ydUpWrLayer)) {
				jrParam.setField("YD_STK_COL_GP" , ydUpWrLoc.substring(0, 6) );
			    jrParam.setField("YD_STK_BED_NO" , ydUpWrLoc.substring(6, 8));
				/*
				SELECT    WB.*
				      FROM  TB_YD_WRKBOOKMTL CM
				          , TB_YD_WRKBOOK   WB
				          , (
				             SELECT (SELECT STL_NO
				                      FROM TB_YD_STKLYR
				                     WHERE YD_STK_COL_GP = LY.YD_STK_COL_GP
				                       AND YD_STK_BED_NO = LY.L_BED
				                       AND YD_STK_LYR_NO = '001') AS L_STL_NO
				                 , (SELECT STL_NO
				                      FROM TB_YD_STKLYR
				                     WHERE YD_STK_COL_GP = LY.YD_STK_COL_GP
				                       AND YD_STK_BED_NO = LY.R_BED
				                       AND YD_STK_LYR_NO = '001') AS R_STL_NO
				              FROM (SELECT DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
				                                                    '002', SL.YD_STK_BED_NO)                               AS L_BED
				                         , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_BED_NO,
				                                                    '002', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'))
				                                                                                                           AS R_BED
				                         , SL.*
				                      FROM TB_YD_STKLYR  SL
				                     WHERE SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
				                       AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
				                       AND SL.YD_STK_LYR_NO = '002'
				                   ) LY
				              WHERE 1 = 1       
				             ) SL
				     WHERE CM.STL_NO IN (SL.L_STL_NO, SL.R_STL_NO)   
				       AND WB.YD_WBOOK_ID = CM.YD_WBOOK_ID
				       AND CM.DEL_YN  = 'N' 
				       AND WB.YD_GP   = 'J'
				       AND SUBSTR(WB.YD_SCH_CD, 3, 2) IN ('KE') 
				       AND NOT EXISTS (SELECT 1
				                     FROM TB_YD_WRKBOOK    WR
				                        , TB_YD_WRKBOOKMTL WM
				                        , TB_YD_CRNSCH     CS
				                    WHERE WR.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                      AND WR.YD_WBOOK_ID = CS.YD_WBOOK_ID
				                      AND WR.YD_GP       = 'J'
				                      AND WR.DEL_YN      = 'N'
				                      AND WM.DEL_YN      = 'N'
				                      AND CS.DEL_YN      = 'N'
				                      AND WR.YD_WBOOK_ID = WB.YD_WBOOK_ID
				                  )
				 */
				JDTORecordSet jsSPMLdSeq = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getDmWrkbookListKE", logId, mthdNm, "보급작업예약 조회");

				if (jsSPMLdSeq.size() > 0 ) {
					
					JDTORecord jrKESch = commUtils.getParam(logId, mthdNm, sModifier); 
					jrKESch.setField("JMS_TC_CD"		 , "YDYDJ552");  //
					jrKESch.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrKESch.setField("YD_SCH_CD"         , ""); //야드스케쥴코드
					jrKESch.setField("YD_EQP_ID"         , ""); //야드설비ID
					
					for (int i = 1; i <= jsSPMLdSeq.size(); i++) {
						jsSPMLdSeq.absolute(i);
						jrKESch.setField("YD_WBOOK_ID" + i, jsSPMLdSeq.getRecord().getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrKESch.setField("SCH_CNT"        , "" + i); //작업예약 개수
					}
					commUtils.printLog(logId, "보급크레인스케줄 기동", "SL");
				}	
			}

			
            /**********************************************
			 * 정정보급존 자동이적 스케쥴 기동
			 **********************************************/
			if (ydSchCd.indexOf("YD04MH") > -1 || ydSchCd.indexOf("YD54MH") > -1) {
				
				String sAppYn = coilDao.ApplyYn(logId, mthdNm, "APP317", "J", "*");
				commUtils.printLog(logId,  "==========[[[ APP317 정정보급존 자동이적(권상시 작업예약생성) : "+ sAppYn +" ]]]============", "SL");
				if( "Y".equals(sAppYn) ) {
					
					String ydBayGp = ydSchCd.substring(1, 2);
					jrParam	= commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("YD_BAY_GP",	ydBayGp);
					jrParam.setField("IF_SEND",		"N");		// 명령선택기동 안함
					jrParam.setField("SINGLE_SEND",	"N");       // 한두개 적치된 열의 코일을 이동
					/***********************************************************
					*  정정보급존 자동이적
					***********************************************************/
					EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
					JDTORecord jrSupplyZoneMove = (JDTORecord)ejbConn.trx("procSupplyZoneMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });

					String rtnCd	= commUtils.nvl(jrSupplyZoneMove.getFieldString("RTN_CD"), "0");
					String rtnMsg	= commUtils.nvl(jrSupplyZoneMove.getFieldString("RTN_MSG"), "");
					
					commUtils.printParam(logId, jrSupplyZoneMove);
					jrRtn = commUtils.addSndData(jrRtn, jrSupplyZoneMove);
					
		    		commUtils.printLog(logId, "RTN_CD["+ rtnCd +"] RTN_MSG["+ rtnMsg +"]", "SL");
				}
			}
			
			/***********************************************************
			 * 정정보급존으로 자동이적 처리 
			 * 수입존 적치율 75% 이상일때 실행
			 **********************************************************/
			/*com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getRotentionCV
				WITH  PARAM AS (
				  SELECT :V_YD_SCH_CD AS V_YD_SCH_CD FROM DUAL
				)
				,TEMP_TO_LOC AS ( -- 영역별검색순서  
	
				SELECT BB.YD_STK_COL_GP
				           FROM (
				             SELECT  B.YD_STK_COL_GP ,B.YD_STK_BED_SRCH_SEQ                
				               FROM TB_YD_LOCSRCHRNG A
				                  , TB_YD_LOCSRCHBED B
				                  , PARAM  C
				              WHERE A.YD_LOC_SRCH_RNG_REG_SNO = B.YD_LOC_SRCH_RNG_REG_SNO
				                AND A.YD_SCH_CD     = C.V_YD_SCH_CD
				                AND B.YD_STK_COL_GP LIKE 'J%'
				                AND A.DEL_YN = 'N'
				                AND B.DEL_YN = 'N'    
				                AND A.YD_LOC_SRCH_RNG_SEQ = '0'
				              ORDER BY B.YD_STK_BED_SRCH_SEQ
				              ) BB
				            WHERE 1=1             
				              GROUP BY BB.YD_STK_COL_GP
				)
				SELECT * FROM
				(
				SELECT TO_CHAR(STL_CNT / CNT * 100, '990.00') AS RATE
				  FROM(
				        SELECT COUNT(*) AS CNT
				             , SUM(CASE WHEN SL.STL_NO IS NOT NULL AND SL.YD_STK_LYR_MTL_STAT IN ('C','U') THEN 1 ELSE 0 END) AS STL_CNT 
				          FROM TB_YD_STKLYR SL
				             , TB_YD_STKCOL SC
				             , PARAM  C
				         WHERE SC.YD_STK_COL_GP = SL.YD_STK_COL_GP 
				           AND SC.YD_GP = 'J'
				           --AND SC.YD_STK_COL_W_GP <> 'L'
				           AND SC.YD_BAY_GP LIKE   SUBSTR( C.V_YD_SCH_CD  ,2,1) ||'%'
				           AND SC.YD_STK_COL_GP IN (SELECT YD_STK_COL_GP FROM TEMP_TO_LOC)
				           AND SL.YD_STK_LYR_ACT_STAT = 'E'
				           
				       )
				) BB
				WHERE BB.RATE > (SELECT DTL_ITEM1
                      FROM TB_YD_RULE
                     WHERE REPR_CD_GP = 'APP013'
                       AND ITEM  <> '*'
                       AND CD_GP  = 'J'
                       AND DEL_YN = 'N'
                       AND ITEM = SUBSTR( BB.V_YD_SCH_CD  ,2,1))

			*/
			
			if (ydSchCd.indexOf("CV01LH") > -1 ) {
				String sAPP012_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","012"); 
				String ydBayGp = ydSchCd.substring(1, 2);
				JDTORecordSet jsRotentionIm = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getRotentionCV", logId, mthdNm, "출하작업예약 조회");

				if(jsRotentionIm.size() > 0 &&  "Y".equals(sAPP012_YN) ) {					
					String rateCV = jsRotentionIm.getRecord(0).getFieldString("RATE");
					commUtils.printLog(logId,  "수입존 적치율 : "+rateCV+"["+ydBayGp+"] ==========[낱본장 수입존에서 정정보급존 자동이적(권상시 작업예약생성) : "+ sAPP012_YN +" ]]]============", "SL");
						
					JDTORecord jrSupplyParam	= commUtils.getParam(logId, mthdNm, sModifier);
					jrSupplyParam.setField("YD_BAY_GP",	ydBayGp);
					jrSupplyParam.setField("IF_SEND",		"N");	// 명령선택기동 안함
					jrSupplyParam.setField("SINGLE_SEND",	"Y");   // 한두개 적치된 열의 코일을 이동
					jrSupplyParam.setField("MODIFIER"	, "SgeMove");
					/***********************************************************
					*  정정보급존 자동이적
					***********************************************************/
					EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
					JDTORecord jrSupplyZoneMove3 = (JDTORecord)ejbConn.trx("procSupplyZoneMove", new Class[] { JDTORecord.class }, new Object[] { jrSupplyParam });

					String rtnCd	= commUtils.nvl(jrSupplyZoneMove3.getFieldString("RTN_CD"), "0");
					String rtnMsg	= commUtils.nvl(jrSupplyZoneMove3.getFieldString("RTN_MSG"), "");
					
					commUtils.printParam(logId, jrSupplyZoneMove3);
					jrRtn = commUtils.addSndData(jrRtn, jrSupplyZoneMove3);
					
		    		commUtils.printLog(logId, "RTN_CD["+ rtnCd +"] RTN_MSG["+ rtnMsg +"]", "SL");
				}
			}
			/***********************************************************
			*  정정보급존 자동이적2
			***********************************************************/
//			if (ydSchCd.indexOf("YD08MH") > -1 || ydSchCd.indexOf("YD58MH") > -1) { 
				
				String sAppYn = coilDao.ApplyYn(logId, mthdNm, "APP105", "J", "*");
				commUtils.printLog(logId,  "==========[[[ APP105 정정보급존 자동이적(권상시 작업예약생성) : "+ sAppYn +" ]]]============", "SL");
				if ("Y".equals(sAppYn)) {
					
					String ydBayGp = ydSchCd.substring(1, 2);
					jrParam	= commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("YD_BAY_GP",	ydBayGp);
					jrParam.setField("IF_SEND",		"N");		// 명령선택기동 안함
					
					EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
					JDTORecord jrSupplyZoneMove = (JDTORecord)ejbConn.trx("procSupplyZoneMove2", new Class[] { JDTORecord.class }, new Object[] { jrParam });

					String rtnCd	= commUtils.nvl(jrSupplyZoneMove.getFieldString("RTN_CD"), "0");
					String rtnMsg	= commUtils.nvl(jrSupplyZoneMove.getFieldString("RTN_MSG"), "");
					
					commUtils.printParam(logId, jrSupplyZoneMove);
					jrRtn = commUtils.addSndData(jrRtn, jrSupplyZoneMove);
					
		    		commUtils.printLog(logId, "RTN_CD["+ rtnCd +"] RTN_MSG["+ rtnMsg +"]", "SL");
				}
//			}
				/**********************************************
				 * 보급스케줄 없을고 자동이적2 작업예약 있으면 자동이적2 기동
				 **********************************************/
//				String sAPP000_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","000"); 
//				commUtils.printLog(logId, "자동이적2 예약 스케줄 기동 : " + sAPP000_YN, "SL");
//				
//				if ("Y".equals(sAPP000_YN)) {
//					String ydBayGp = ydSchCd.substring(1, 2);
//					jrParam.setField("YD_SCH_CD",   ydSchCd);
//					jrParam.setField("YD_BAY_GP",	ydBayGp);
//					/*
//					SELECT *
//					  FROM (
//					        SELECT A.YD_SCH_CD
//					             , A.YD_WBOOK_ID
//					          FROM TB_YD_WRKBOOK A
//					         WHERE A.DEL_YN    = 'N'
//					           AND A.YD_GP     = 'J'
//					           AND NOT EXISTS(SELECT 1 FROM USRYDA.TB_YD_CRNSCH B 
//					                           WHERE B.DEL_YN = 'N'
//					                             AND B.YD_SCH_CD   LIKE 'J_KE01UH' 
//					                             AND B.YD_WBOOK_ID = A.YD_WBOOK_ID
//					                             AND B.YD_BAY_GP   = :V_YD_BAY_GP
//					                             )
//					           AND A.YD_SCH_CD LIKE 'J_YD_8MH' -- 정정보급존 자동이적2
//					           AND A.YD_BAY_GP = :V_YD_BAY_GP
//					         ORDER BY A.YD_WBOOK_ID
//					       ) AA
//					 WHERE ROWNUM <= 1
//					 */
//					JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdWrkbookMove2Chk", logId, mthdNm, "자동이적2 예약 조회");	
//					
//					if (jsRst.size() > 0) {
//						JDTORecord jrCrnSchMsg = commUtils.getParam(logId, mthdNm, sModifier);
//						jrCrnSchMsg.setField("JMS_TC_CD"         , "YDYDJ551"); //
//						jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
//						jrCrnSchMsg.setField("YD_SCH_CD"         , jsRst.getRecord(0).getFieldString("YD_SCH_CD"  )); 
//						jrCrnSchMsg.setField("YD_WBOOK_ID"       , jsRst.getRecord(0).getFieldString("YD_WBOOK_ID"));
//						
//						jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
//						
//						commUtils.printLog(logId, "자동이적2 작업예약 스케줄 기동", "SL");
//					} else {
//						commUtils.printLog(logId, "자동이적2 작업예약 없음", "SL");
//					}
//				}
//				
			/***********************************************************
			*  스크랩 권하위치 재설정 (APP324)
			***********************************************************/		
				String sApp324Yn = coilDao.ApplyYn(logId, mthdNm, "APP324", "J", "*");
				commUtils.printLog(logId,  "==========[[[ APP324 스크랩 권하위치 재설정 : "+ sApp324Yn +" ]]]============", "SL");
				if ("Y".equals(sApp324Yn)) {
			        //대상위치가 2단일때 						
					if("002".equals(ydDnWoLayer) && "SC".equals(ydSchCd.substring(2,4))){
						JDTORecord jrScrapChange = commUtils.getParam(logId, mthdNm, sModifier);
						jrScrapChange.setField("MODIFIER" ,sModifier);
						jrScrapChange.setField("YD_CRN_SCH_ID" ,ydCrnSchId);
						jrScrapChange.setField("YD_SCH_CD" ,ydSchCd);
						jrScrapChange.setField("YD_DN_WO_LOC" ,ydDnWoLoc);
						jrScrapChange.setField("STL_NO" ,sStlNo);
						jrScrapChange.setField("YD_EQP_ID" ,ydEqpId);

						JDTORecord jrProc = this.procScrapChangeDownTo(jrScrapChange);
								            	
						jrRtn = commUtils.addSndData(jrRtn, jrProc);
					}
				}	
			/*End of 스크랩 자동이적 (APP305)--------------------------------------------------------------------*/	
				/**********************************************
				 *   보급 크레인스케줄 기동 YYS 202303
				 *  - 크레인작업이 없을 때 작업예약에 있는것 기동
				 **********************************************/
				String sAPP011_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","011"); 
				commUtils.printLog(logId, "보급예약 스케줄 기동 : " + sAPP011_YN, "SL");
				
				if ("Y".equals(sAPP011_YN)) {
					String ydBayGp = ydSchCd.substring(1, 2);
					 if ("H".equals(ydSchCd.substring(7, 8))) {
						jrParam = commUtils.getParam(logId, mthdNm, sModifier);
						jrParam.setField("YD_BAY_GP"  , ydBayGp);
						jrParam.setField("YD_EQP_ID"  , ydEqpId); // 야드설비ID
						
						/*com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getTakeInTrackingInfo
							WITH P_TRACKING AS --tracking 정보 조회
							(SELECT AA.EQP_CD
							     , AA.EQP_GP
							     , AA.YD_STK_COL_GP
							     , CASE WHEN AA.EQP_CD IN( 'HFL2', 'HFL3', 'HFL5')
							            THEN BB.STL_NO
							            ELSE AA.COIL_NO
							       END COIL_NO
							     , AA.SORT_SEQ
							     , BB.YD_STK_LYR_MTL_STAT
							     , BB.YD_STK_BED_NO
							     
							  FROM (SELECT EQP_CD
							             , EQP_GP
							             , YD_STK_COL_GP
							             , STL_NO AS COIL_NO
							             , CASE WHEN EQP_GP = 'ECC07' AND EQP_CD = 'SPM5' THEN 1  -- 1번째 위치는 예비로 비워둠  2번째 위치에 권하 하기 때문
							                    WHEN EQP_GP = 'ECC10' AND EQP_CD = 'SPM3' THEN 1  
							                    WHEN EQP_GP = 'ECC10' AND EQP_CD = 'SPM4' THEN 1  
							                    WHEN EQP_GP = 'ECC08' AND EQP_CD = 'SPM5' THEN 2  -- 1번째 위치는 예비로 비워둠 
							                    WHEN EQP_GP = 'ECC11' AND EQP_CD = 'SPM3' THEN 2  
							                    WHEN EQP_GP = 'ECC11' AND EQP_CD = 'SPM4' THEN 2
							               ELSE SORT_SEQ END  SORT_SEQ
							          FROM TB_YD_EQPTRACKING A
							         WHERE EQP_CD IN ('SPM1', 'SPM2', 'SPM3', 'SPM4', 'SPM5'
							                        , 'HFL1', 'HFL4')
							           AND SUBSTR(EQP_GP, 0, 2) <> 'CR'
							        UNION ALL
							        SELECT CASE WHEN YD_STK_COL_GP = 'JFFE02' THEN 'HFL2'
							                    WHEN YD_STK_COL_GP = 'JDFE03' THEN 'HFL3'
							                    WHEN YD_STK_COL_GP = 'JBFE05' THEN 'HFL5'
							               END AS EQP_CD
							             , CASE WHEN YD_STK_COL_GP = 'JFFE02' THEN 'K2-' || YD_STK_BED_NO
							                    WHEN YD_STK_COL_GP = 'JDFE03' THEN 'K3-' || YD_STK_BED_NO
							                    WHEN YD_STK_COL_GP = 'JBFE05' THEN 'K5-' || YD_STK_BED_NO
							               END AS EQP_GP
							             , YD_STK_COL_GP
							             , STL_NO
							             , TO_NUMBER(YD_STK_BED_NO)
							          FROM TB_YD_STKLYR
							         WHERE YD_STK_COL_GP IN ('JFFE02', 'JDFE03', 'JBFE05')
							           AND YD_STK_BED_NO <> '00'
							       ) AA
							     , (SELECT *
							          FROM TB_YD_STKLYR
							         WHERE SUBSTR(YD_STK_COL_GP,1,1) = 'J'
							           AND DEL_YN = 'N'
							           AND STL_NO IS NOT NULL
							           AND YD_STK_LYR_MTL_STAT IN ('C','U')
							           AND (YD_STK_COL_GP LIKE 'J_F_0_' or YD_STK_COL_GP LIKE 'J_K_0_')
							       ) BB
							 WHERE AA.COIL_NO = BB.STL_NO(+)
							     AND AA.SORT_SEQ = '1'
							     AND AA.EQP_CD IN ('HFL1','HFL4')
							)
							SELECT AA.YD_SCH_CD
							     , AA.YD_WBOOK_ID
							     , AA.YD_EQP_ID
							     , PP.COIL_NO
							     , AA.EQP_CD
							     , PP.EQP_GP
							     , (SELECT BB.YD_CRN_SCH_ID
							                      FROM TB_YD_CRNSCH BB
							                      WHERE 1=1 
							                       AND BB.YD_WBOOK_ID = AA.YD_WBOOK_ID --보급요구이거나 재작업 일때 제외
							                       AND BB.DEL_YN = 'N'
							                       AND (BB.YD_SCH_CD LIKE 'J_FE01UH' 
							                             OR  BB.YD_SCH_CD LIKE 'J_KE01UH'  
							                             OR  BB.YD_SCH_CD LIKE 'J_KD02LH' ) 
							                       AND ROWNUM = 1) YD_CRN_SCH_ID
							  FROM (
							        SELECT A.YD_SCH_CD
							             , A.YD_WBOOK_ID
							             , A.YD_WRK_PLAN_CRN AS YD_EQP_ID  -- (SELECT BB.YD_WRK_CRN FROM TB_YD_SCHRULE BB WHERE BB.YD_SCH_CD = A.YD_SCH_CD) AS YD_EQP_ID
							             , ROW_NUMBER() OVER (PARTITION BY A.YD_SCH_CD  ORDER BY A.YD_SCH_CD , A.YD_WBOOK_ID ) AS RANK_NO
							             , CASE WHEN A.YD_SCH_CD = 'JCFE01UH' THEN 'HFL4'
							                    WHEN A.YD_SCH_CD = 'JGFE01UH' THEN 'HFL1'
							                    WHEN A.YD_SCH_CD = 'JFFE01UH' THEN 'HFL2'
							                    WHEN A.YD_SCH_CD = 'JDFE01UH' THEN 'HFL3'
							                    WHEN A.YD_SCH_CD = 'JBFE01UH' THEN 'HFL5'
							                    WHEN A.YD_SCH_CD = 'JCKE01UH' THEN 'SPM3'
							                    WHEN A.YD_SCH_CD = 'JBKE01UH' THEN 'SPM4'
							                    WHEN A.YD_SCH_CD = 'JHKE01UH' THEN 'SPM1'
							                    WHEN A.YD_SCH_CD = 'JEKE01UH' THEN 'SPM2'
							                    WHEN A.YD_SCH_CD = 'JAKE01UH' THEN 'SPM5'
							               ELSE '' END  EQP_CD
							             , B.YD_SCH_PROH_EXN
							          FROM TB_YD_WRKBOOK A
							              , TB_YD_SCHRULE B               
							         WHERE A.DEL_YN    = 'N'
							           AND A.YD_GP     = 'J'
							           AND A.YD_SCH_CD = B.YD_SCH_CD
							          ORDER BY  A.YD_WBOOK_ID   
							          
							       ) AA
							       , P_TRACKING PP
							 WHERE 1=1 --ROWNUM <= 1
							 AND AA.RANK_NO = 1
							 AND AA.YD_SCH_PROH_EXN = 'N' --보급금지 여부
							 AND PP.EQP_CD = AA.EQP_CD
							 AND AA.YD_WBOOK_ID NOT IN (SELECT BB.YD_WBOOK_ID
							                      FROM TB_YD_CRNSCH BB
							                      WHERE 1=1 
							                       AND (BB.YD_SCH_CD LIKE 'J_FE01UH' 
							                             OR  BB.YD_SCH_CD LIKE 'J_KE01UH'  
							                             OR  BB.YD_SCH_CD LIKE 'J_KD02LH' ) --보급요구이거나 재작업 일때 제외
							                       AND BB.DEL_YN = 'N')
							 AND PP.COIL_NO  IS NULL
							     
							*/
						JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getTakeInTrackingInfo", logId, mthdNm, "자동보급 대상 조회");	
						
						if (jsRst.size() > 0) {
							for(int ii=0; ii<jsRst.size(); ii++){
								JDTORecord jrCrnSchMsg = commUtils.getParam(logId, mthdNm, sModifier);
								jrCrnSchMsg.setField("JMS_TC_CD"         , "YDYDJ551"); //
								jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
								jrCrnSchMsg.setField("YD_SCH_CD"         , jsRst.getRecord(ii).getFieldString("YD_SCH_CD"  )); 
								jrCrnSchMsg.setField("YD_EQP_ID"         , jsRst.getRecord(ii).getFieldString("YD_EQP_ID"  ));
								jrCrnSchMsg.setField("YD_WBOOK_ID"       , jsRst.getRecord(ii).getFieldString("YD_WBOOK_ID"));
								jrCrnSchMsg.setField("YD_DUAL_CHK"       , commUtils.nvl(jsRst.getRecord(ii).getFieldString("SCH_CHECK"),"N")); // 보조작업없을때만 이중스케줄 방지체크
								
								
								commUtils.printLog(logId, "다음 보급 작업예약 스케줄 기동"+jsRst.getRecord(ii).getFieldString("YD_SCH_CD"  ), "SL");
								commUtils.printLog(logId, "다음 보급 작업예약 스케줄 기동"+jsRst.getRecord(ii).getFieldString("YD_EQP_ID"  ), "SL");
								commUtils.printLog(logId, "다음 보급 작업예약 스케줄 기동"+jsRst.getRecord(ii).getFieldString("YD_WBOOK_ID"), "SL");
								commUtils.printLog(logId, "다음 보급 작업예약 스케줄 기동"+jsRst.getRecord(ii).getFieldString("EQP_CD"), "SL");
								
								jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
							}				
							
							
						} else {
							commUtils.printLog(logId, "다음 보급 작업예약 없음", "SL");
						}
					 }
				}
				String sAPP013_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "013");
				commUtils.printLog(logId,  "==========[[[ APP001 보급존 보급예약재료의 더미 작업진행 : "+ sAPP013_YN +" ]]]============", "SL");
				if ("Y".equals(sAPP013_YN)) {
					if ("H".equals(ydSchCd.substring(7, 8))) {
						String ydBayGp = ydSchCd.substring(1, 2);
						jrParam	= commUtils.getParam(logId, mthdNm, sModifier);
						jrParam.setField("YD_BAY_GP",	  ydBayGp);
						jrParam.setField("IF_SEND"  ,	  "N");		// 명령선택기동 안함
						jrParam.setField("SUPZONE_DUMMY" ,"Y");
						JDTORecordSet jsRotentionInfo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getSupZoneCheck", logId, mthdNm, "보급존적치율및 트레킹정보 조회");
						String trackInfo       = "";
						String rateSupZoneRate = "";
						String stl001Cnt       = ""; // 보급존내 비어있는 1단
						if(jsRotentionInfo.size() > 0 ) {					
							 trackInfo       = commUtils.nvl(jsRotentionInfo.getRecord(0).getFieldString("TRACK_INFO"), "0");     //보급존 트레킹정보
							 rateSupZoneRate = commUtils.nvl(jsRotentionInfo.getRecord(0).getFieldString("RETENTION_RATE"), "0"); //보급존 적치율
							 stl001Cnt       = commUtils.nvl(jsRotentionInfo.getRecord(0).getFieldString("STL_001CNT"), "0");     //보급존내 비어있는 1단
						}
						
						commUtils.printLog(logId, "보급존 트레킹정보:["+trackInfo+"] 보급존 적치율 : ["+rateSupZoneRate+"] 보급존내 비어있는 1단 갯수 : ["+stl001Cnt+"]", "SL");
						
						//if("0".equals(trackInfo) && "Y".equals(rateSupZoneRate)){
							EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
							JDTORecord jrZoneMoveDummy = (JDTORecord)ejbConn.trx("procSupplyZoneMoveDummy", new Class[] { JDTORecord.class }, new Object[] { jrParam });
	
							String rtnCd	= commUtils.nvl(jrZoneMoveDummy.getFieldString("RTN_CD"), "0");
							String rtnMsg	= commUtils.nvl(jrZoneMoveDummy.getFieldString("RTN_MSG"), "");
							
							commUtils.printParam(logId, jrZoneMoveDummy);
							jrRtn = commUtils.addSndData(jrRtn, jrZoneMoveDummy);
							
				    		commUtils.printLog(logId, "RTN_CD["+ rtnCd +"] RTN_MSG["+ rtnMsg +"]", "SL");
						//}
					}
				}
				
				 
			jrRtn.setField("RTN_CD"	, "1");
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}								

					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "CCommSeEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { coilDao.getYDY5L005(resMsg)});
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 스크랩 권하위치 재설정 (APP324)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="Required"      
	*/
	public JDTORecord procScrapChangeDownTo(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "스크랩 권하위치 재설정 (APP324)[CCoilL2RcvSeEJB.procScrapChangeDownTo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			String ydStkColGp = "";
			String ydStkBedNo = "";
			String ydL3Msg = "";
			String tagLeftBed = "";
			String tagRightBed = "";
			String ydDnWoLayer = "";
			String tagLeftStlNo = "";
			String tagRightStlNo = "";
			
			//전문 Return
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			//수신 항목 값
			String ydCrnSchId = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"));       
			String ydSchCd = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));
			String ydDnWoLoc = commUtils.trim(rcvMsg.getFieldString("YD_DN_WO_LOC"));
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
			String sStlNo = commUtils.trim(rcvMsg.getFieldString("STL_NO")); //재료번호
			String ydEqpId = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));
						
			ydStkColGp = ydDnWoLoc.substring(0, 6);
			ydStkBedNo = ydDnWoLoc.substring(6, 8);
						
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam	= commUtils.getParam(logId, mthdNm, sModifier);
						
			jrParam.setField("YD_CRN_SCH_ID" ,ydCrnSchId);
			jrParam.setField("YD_SCH_CD" ,ydSchCd);
			jrParam.setField("YD_STK_COL_GP" ,ydStkColGp);
			jrParam.setField("YD_STK_BED_NO" ,ydStkBedNo); 
			
			/*			 
			 -- 대상 위치 SELECT
			WITH  TO_LOC_TABLE AS (
			    SELECT C1.COIL_WT
			         , ABS(TO_NUMBER(SL.YD_STK_LYR_NO) - TO_NUMBER(C1.COIL_YD_STK_LYR_NO)) AS PRIOR4 -- LYR 우선
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
			             WHERE AA.YD_CRN_SCH_ID = BB.YD_CRN_SCH_ID(+)
			               AND AA.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			               AND BB.STL_NO        = CC.COIL_NO(+)
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
			--               AND A.YD_ROUTE_GP             = SUBSTR(:V_YD_SCH_CD,2,1) ||'Z'
			               AND A.DEL_YN                  = 'N'
			               AND B.DEL_YN                  = 'N'
			           ) X1
			     WHERE SL.YD_STK_COL_GP             = X1.YD_STK_COL_GP
			       AND SL.YD_STK_BED_NO             = X1.YD_STK_BED_NO
			       AND SUBSTR(SL.YD_STK_COL_GP,1,1) = SUBSTR(C1.COIL_YD_STK_COL_GP,1,1) -- 야드구분
			       AND SUBSTR(SL.YD_STK_COL_GP,2,1) = SUBSTR(C1.COIL_YD_STK_COL_GP,2,1) -- 동구분       
			--       AND SL.YD_STK_LYR_ACT_STAT = 'E'
			--       AND SL.YD_STK_LYR_MTL_STAT = 'E'
			       AND SL.DEL_YN              = 'N'
			       AND SL.YD_STK_COL_GP = :V_YD_STK_COL_GP -- 'JHSC01'
			       AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO -- '01'
			       AND SL.YD_STK_LYR_NO = '001'
			)
			, TO_LOC_DATA_TABLE AS (
			-- TO위치 코일정보 SELECT
			SELECT COIL_WT
			     , A.PRIOR4
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
			)
			--*--*--*--*--*--*-- 결과
			SELECT G.*     
			  FROM TO_LOC_DATA_COMP_TABLE G
			ORDER BY PRIOR4,TAG_YD_STK_COL_GP,TAG_YD_STK_BED_NO,TAG_YD_STK_LYR_NO   
			 */
			JDTORecordSet jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdToLocStateScrap", logId, mthdNm, "스크랩 대상위치 1단 상태 확인");
			JDTORecord    jrResult = null; 
			
			if (jsResult.size() > 0) {
				
				jrResult = jsResult.getRecord(0);
				tagLeftBed = jrResult.getFieldString("TAG_LEFT_BED");
				tagRightBed = jrResult.getFieldString("TAG_RIGHT_BED");
				tagLeftStlNo = jrResult.getFieldString("TAG_LEFT_STL_NO");
				tagRightStlNo = jrResult.getFieldString("TAG_RIGHT_STL_NO");
				
				if("".equals(tagLeftStlNo) || tagLeftStlNo == null){         //1단의 왼쪽에 스크랩코일이 없을 때 그 위치에 적재하도록 번지를 바꾸어준다.             
					ydDnWoLoc = ydDnWoLoc.substring(0,6) + tagLeftBed;
					ydDnWoLayer = "001";
				}
				if("".equals(tagRightStlNo) || tagRightStlNo == null){       //1단의 오른쪽에 스크랩코일이 없을 때 그 위치에 적재하도록 번지를 바꾸어준다.      
					ydDnWoLoc = ydDnWoLoc.substring(0,6) + tagRightBed;
					ydDnWoLayer = "001";
				}
						 
			    jrParam   = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("STL_NO"               ,sStlNo);
				jrParam.setField("YD_CRN_SCH_ID"        ,ydCrnSchId);
				jrParam.setField("YD_DN_WO_LOC"         ,ydDnWoLoc);
				jrParam.setField("YD_DN_WO_LAYER"       ,ydDnWoLayer);
				jrParam.setField("YD_EQP_ID"           	,ydEqpId);
				jrParam.setField("YD_LOC_GP"           	,"H"); //소재
				
				jrRtn = cCoilJspSeEJBBean.updToPosFixCoilHold(jrParam); 

										
			} else {
				ydL3Msg = "스크랩 대상위치 1단 상태 확인 SELECT 실패  " ;
        		commUtils.printLog(logId,ydL3Msg, "SL");
        		jrRtn.setField("RTN_CD" 	, "0");	
        		jrRtn.setField("RTN_MSG" 	, ydL3Msg);	
			}	
						
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}	
	}
	
	/**
	 *      [A] 오퍼레이션명 : 지포장자동보급
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"      
	*/
	public JDTORecord procGWrapAutoSup(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "지포장 자동보급[CCoilL2RcvSeEJB.procGWrapAutoSup] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			//수신 항목 값
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));       
			
			//전문 Return
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);

			/*
			--지포장 자동보급 대상재
			SELECT A.*
			     , CASE WHEN SUP_SCH_CNT > 0 THEN 'Y' ELSE 'N' END AS SUP_SCH_YN
			  FROM (
			        SELECT B.YD_STK_COL_GP
			             , B.YD_STK_BED_NO
			             , B.YD_STK_LYR_NO
			             , C.YD_BAY_GP
			             , A.*
			             , (SELECT COUNT(*) AS ABLE_LOC
			                  FROM TB_YD_STKLYR  SL
			                     , TB_YD_STKCOL  SC
			                 WHERE SC.YD_STK_COL_GP = SL.YD_STK_COL_GP
			                   AND SC.YD_GP     = 'J'
			                   AND SC.YD_EQP_GP = 'GF'
			                   AND SL.YD_STK_LYR_ACT_STAT = 'E'
			                   AND SL.YD_STK_LYR_MTL_STAT = 'E'
			                   AND SC.DEL_YN = 'N'
			                   AND SL.DEL_YN = 'N'
			                   AND SC.YD_BAY_GP = C.YD_BAY_GP
			               ) -
			               (SELECT COUNT(*) AS WB_CNT
			                  FROM TB_YD_WRKBOOK WB
			                 WHERE WB.DEL_YN = 'N'
			                   AND WB.YD_SCH_CD LIKE 'J_GF01UH' --지포장보급
			                   AND WB.YD_BAY_GP = C.YD_BAY_GP
			               ) AS SUP_ABLE_CNT
			             , (SELECT COUNT(*)
			                  FROM TB_YD_CRNSCH  CS
			                 WHERE CS.DEL_YN = 'N'
			                   AND CS.YD_SCH_CD LIKE 'J_GF01UH' --지포장보급
			                   AND CS.YD_BAY_GP = C.YD_BAY_GP
			--                   AND YD_WRK_PROG_STAT NOT IN ('2') --권상된 스케줄이 지포장보급일 경우 제외한 
			               ) AS SUP_SCH_CNT
			             , (SELECT EP.YD_EQP_STAT
			                  FROM TB_YD_EQP      EP
			                     , TB_YD_SCHRULE  SR
			                 WHERE EP.DEL_YN = 'N'
			                   AND SR.DEL_YN = 'N'
			                   AND EP.YD_GP  = 'J'
			                   AND SR.YD_GP  = 'J'
			                   AND SR.YD_SCH_CD LIKE 'J_GF01UH'
			                   AND EP.YD_EQP_ID = CASE WHEN EP.YD_EQP_STAT = 'B' AND SR.YD_ALT_CRN_YN = 'Y' THEN SR.YD_ALT_CRN
			                                           ELSE SR.YD_WRK_CRN END
			                   AND EP.YD_BAY_GP = C.YD_BAY_GP
			               ) AS SUP_EQP_STAT
			          FROM (
			                SELECT A.STL_NO
			                     , C.CURR_PROG_CD
			                     , C.ORD_NO
			                  FROM TB_YD_STOCK    A
			                     , TB_PT_COILCOMM C
			                     , TB_PT_OSCOMM   B
			                 WHERE A.STL_NO  = C.COIL_NO
			                   AND C.ORD_NO  = B.ORD_NO(+)
			                   AND C.ORD_DTL = B.ORD_DTL(+)
			                   AND A.YD_AIM_RT_GP = 'G0'  --지포장대상만 포함
			                   AND C.CURR_PROG_CD = 'C' --작업대기
			                   AND A.DEL_YN = 'N'
			               ) A
			             , TB_YD_STKLYR B
			             , TB_YD_STKCOL C
			         WHERE 1 = 1
			           AND A.STL_NO = B.STL_NO
			           AND B.YD_STK_COL_GP = C.YD_STK_COL_GP
			           AND C.YD_LOC_GP = 'H'
			           AND SUBSTR(B.YD_STK_COL_GP, 3, 2) BETWEEN '00' AND '99'
			           AND C.YD_GP  = 'J'
			           AND B.YD_STK_LYR_MTL_STAT <> 'D'
			           AND B.DEL_YN = 'N'
			           AND C.DEL_YN = 'N'
			           AND NOT EXISTS (SELECT 1
			                             FROM TB_YD_WRKBOOK    WB
			                                , TB_YD_WRKBOOKMTL WM
			                            WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
			                              AND WB.DEL_YN = 'N'
			                              AND WM.DEL_YN = 'N'
			                              AND WM.STL_NO = A.STL_NO
			                          )
			            AND A.ORD_NO NOT LIKE 'G%'                 --G주문재는 안됨
			            AND C.YD_BAY_GP IN ('B','C','E','H')       --
			       ) A
			 WHERE 1 = 1
			   AND SUP_ABLE_CNT > 0  --지포장 비어있는곳만
			 ORDER BY YD_STK_COL_GP
			        , YD_STK_BED_NO
			        , YD_STK_LYR_NO
			 */
			JDTORecordSet jsSupList = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getGWrapAutoSupTrgtList", logId, mthdNm, "자동보급 대상 조회");
			
			String sSupEqpStat = ""; //보급스케줄 작업크레인 상태
			String ydBayGp     = ""; //
			String sSupStlNo   = "";
			String sSupYdSchCd = "";
			String sSupSchYn   = "";
			
			for (int i = 0; i < jsSupList.size(); ++i) {
				
				sSupEqpStat = jsSupList.getRecord(i).getFieldString("SUP_EQP_STAT");
				ydBayGp     = jsSupList.getRecord(i).getFieldString("YD_BAY_GP"   );  
				sSupStlNo   = jsSupList.getRecord(i).getFieldString("STL_NO"      );
				sSupSchYn   = jsSupList.getRecord(i).getFieldString("SUP_SCH_YN"  );
				
				sSupYdSchCd = "J" + ydBayGp + "GF01UH";  //지포장 보급 SCH_CD

				jrParam = commUtils.getParam(logId, mthdNm, "AUTO_SUP");
				jrParam.setField("STL_SH"				, "1"        );  
				jrParam.setField("STL_NO1"				, sSupStlNo  );
				jrParam.setField("YD_UP_COLL_SEQ1"		, "1"        );
				jrParam.setField("YD_AIM_BAY_GP"		, ydBayGp    ); 
				jrParam.setField("YD_AIM_YD_GP"			, "J"        );
				jrParam.setField("YD_TO_LOC_DCSN_MTD"	, "S"        );
				jrParam.setField("TO_YD_STK_BED_NO"		, ""         );
				jrParam.setField("YD_SCH_CD"			, sSupYdSchCd);
				
				EJBConnector ejbConn1 = new EJBConnector("default", "CCoilWrkBookSeEJB", this);
				JDTORecord jrWrkBook = (JDTORecord)ejbConn1.trx("insWrkBookTx", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				// 크레인상태가 W 또는 보급스케줄이 없는경우  스케줄 기동. 그외의 경우 작업예약만 생성
				if ("W".equals(sSupEqpStat) || "N".equals(sSupSchYn)) {
					
					JDTORecord jrCrnSchMsg = commUtils.getParam(logId, mthdNm, sModifier);
					jrCrnSchMsg.setField("YD_SCH_CD", sSupYdSchCd); 
					
					//스케줄 호출
					EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this);
					JDTORecord jsMsg = (JDTORecord)ejbConn.trx("procYDYDJ551", new Class[] { JDTORecord.class }, new Object[] { jrCrnSchMsg });
					
					jrRtn = commUtils.addSndData(jrRtn, jsMsg);
				}
			} //END FOR
				
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}	
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 대차스케줄 하차완료 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarSchUdCmpl(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "대차스케줄 하차완료 처리[CCoilL2RcvSeEJB.trtTcarSchUdCmpl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			//수신 항목 값
			String ydEqpId     		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )); //야드설비ID(대차)
			String ydTcarSchId 		= commUtils.trim(rcvMsg.getFieldString("YD_TCAR_SCH_ID"   )); //야드대차스케쥴ID
			String CraneId	   		= commUtils.trim(rcvMsg.getFieldString("CRANE_ID"         )); //크레인 id
			String ydCarUdStopLoc	= commUtils.trim(rcvMsg.getFieldString("YD_CARUD_STOP_LOC")); //하차완료위치
			String sModifier        = commUtils.trim(rcvMsg.getFieldString("MODIFIER"         ));       
			
			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			}

			//전문 Return
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_EQP_ID", ydEqpId);	//야드설비ID

			/**********************************************************
			* 1. 대차 하차스케쥴 정보 조회
			**********************************************************/
/*			if ("".equals(ydTcarSchId)) {
				//대차하차스케쥴 조회
				 
				-- 대차스케줄 대차하차완료 조회  
				SELECT TS.YD_TCAR_SCH_ID
				  FROM TB_YM_TCARSCH TS
				 WHERE TS.YD_EQP_ID = :V_YD_EQP_ID
				   AND TS.DEL_YN    = 'N'
					   
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchUdCmpl", logId, mthdNm, "대차하차스케쥴 조회");
				
				if (jsChk != null && jsChk.size() > 0) {
					JDTORecord jrChk = jsChk.getRecord(0);
					ydTcarSchId = commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID")); //야드대차스케쥴ID
				} else {
					return jrRtn;
			    }
			}
*/
			jrParam.setField("YD_TCAR_SCH_ID", ydTcarSchId);	//야드대차스케쥴ID

			/**********************************************************
			* 2. 대차스케줄 삭제
			**********************************************************/
			/* 
			UPDATE TB_YD_TCARFTMVMTL
			   SET MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,DEL_YN   = 'Y'
			 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
			   AND DEL_YN   = 'N'
			*/	   
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updTcarSchDelMtl", logId, mthdNm, "대차이송재료 삭제");

	
			jrParam.setField("CRANE_ID"			, CraneId);			//크레인 id
			jrParam.setField("YD_CARUD_STOP_LOC", ydCarUdStopLoc);	//하차완료위치

			/*
			UPDATE TB_YD_TCARSCH 
			   SET MODIFIER          = :V_MODIFIER
			     , MOD_DDTT          = SYSDATE
			     , DEL_YN            = 'Y'
			     , YD_EQP_WRK_STAT   = 'U'                         --공차
			     , YD_CAR_PROG_STAT  = 'E'                         --하차완료
			     , YD_CARUD_ST_DT    = NVL(YD_CARUD_ST_DT,SYSDATE) --하차개시시간
			     , YD_CARUD_CMPL_DT  = SYSDATE                     --하차완료시간
			     , YD_CARUD_WRK_CRN  = :V_CRANE_ID                 --작업크레인
			     , YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC
			 WHERE YD_TCAR_SCH_ID    = :V_YD_TCAR_SCH_ID
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updTcarSchDelSch", logId, mthdNm, "대차스케줄 삭제");
			
			/**********************************************************
			* 4. 공대차출발지시 처리
			**********************************************************/
			
//			jrRtn = commUtils.addSndData(jrRtn, this.trtTcarSchLevWo(rcvMsg));
			
			JDTORecord jrTcSnd = commUtils.getParam(logId, mthdNm, sModifier);
			jrTcSnd.setField("YD_EQP_ID"	, ydEqpId);
			/**********************************************************
			*  대차스케줄 공대차출발지시 처리 (별도 Transaction 으로 처리)
			*  중량초 오버로 대차상차 불가능 (대차 자동 출발 )
			**********************************************************/
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procTcarSchLevWo", new Class[] { JDTORecord.class }, new Object[] { jrTcSnd });
			
			jrRtn = commUtils.addSndData(jrRtn , jrRtn1);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}	
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인권하실적(Y5YDL009)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL009(JDTORecord rcvMsg) throws DAOException {
		String mthdNm   = "크레인권하실적[CCoilL2RcvSeEJB.rcvY5YDL009] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		boolean resYn 	= true;	//크레인작업실적응답 전문 전송여부
		JDTORecord resMsg = commUtils.getParam(logId, mthdNm, "Y5YDL009"); //크레인작업실적응답 전문 생성용
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();	//전문 Return
			
			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태(4:권하완료, 5:강제권하)
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydDnWrLoc     = commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LOC"    )); //야드권하실적위치
			String ydDnWrLayer   = commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LAYER"  )); //야드권하실적단
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
//			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축	
			String ydEqpWrkMode2 = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE2")); //야드설비작업Mode2
			
			String sModifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			JDTORecord jrYdMsg   = commUtils.getParam(logId, mthdNm, sModifier); //전문전송용
			String currDt        = commUtils.getDateTime14(); //현재시각
			
			boolean bCarMvYn     = coilDao.chkCarMv(logId, mthdNm, ydSchCd); // 차량동간이적 여부
			
			//크레인작업실적응답 전문 생성용
			resMsg.setField("MODIFIER"        , sModifier    );	//수정자
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "D"          ); //야드L2실적구분 (권하실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "DN99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권하실적 수신처리"); //야드L3MESSAGE(Error)

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			jrParam.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrParam.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			jrParam.setField("YD_EQP_WRK_MODE" , ydEqpWrkMode );
			jrParam.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			jrParam.setField("YD_CRN_XAXIS"	   , ydCrnXaxis   ); //
			jrParam.setField("YD_CRN_YAXIS"    , ydCrnYaxis   ); //
			jrParam.setField("YD_DN_WR_LOC"    , ydDnWrLoc    ); //권하지시위치-논리좌표(8자리)
			jrParam.setField("YD_DN_WR_LAYER"  , ydDnWrLayer  ); //권하지시단  -논리좌표(3자리)
			jrParam.setField("YD_UP_WRK_MODE2" , ydEqpWrkMode2); //모드 추가
			jrParam.setField("YD_DN_WRK_MODE2" , ydEqpWrkMode2); //모드 추가
			jrParam.setField("YD_DN_CMPL_DT"   , currDt       ); //권하완료시각
			
			jrParam.setField("YD_DN_WR_XAXIS"  , ydCrnXaxis   ); //
			jrParam.setField("YD_DN_WR_YAXIS"  , ydCrnYaxis   ); //
			jrParam.setField("YD_DN_WR_ZAXIS"  , ydCrnYaxis   ); //
			
			JDTORecord jrTcSnd = commUtils.getParam(logId, mthdNm, sModifier);
			
			//PIDEV
			//PIDEV_S :병행가동용:PI_YD
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "크레인권하실적(Y5YDL009)", "APPPI0", "J", "*");
			String sPI_YD = "";
//			if("Y".equals(sApplyYnPI)) {
				sPI_YD = "J";
//			}
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			String ydL3HdRsCd 		= "";		//야드L3처리결과코드
			String ydL3Msg    		= ""; 		//야드L3MESSAGE
			
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "DN01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "DN02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydCrnSchId)) {
				ydL3HdRsCd = "DN03";
				ydL3Msg    = "오류:크레인스케쥴ID 없음";
			} else if ("".equals(ydDnWrLoc)||ydDnWrLoc.length() < 8) {
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 없음";	
			} else if ("XX".equals(ydDnWrLoc.substring(0, 2))) {
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 이상";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			String ydDnWrStkColGp = ydDnWrLoc.substring(0, 6); //권하위치 열
			String ydDnWrStkBedGp = ydDnWrLoc.substring(6, 8); //권하위치 번지
			
			jrParam.setField("YD_STK_COL_GP"  , ydDnWrStkColGp);
			jrParam.setField("YD_STK_BED_NO"  , ydDnWrStkBedGp);
			jrParam.setField("YD_STK_LYR_NO"  , ydDnWrLayer   );
			
			String ydDnWrkActGp   = "";  //AUTO MANUAL BACKUP구분
			if ("0".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "M"; //Manual
			} else if ("1".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "A"; //Auto
			} else if ("9".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "B"; //Backup
			}
			jrParam.setField("YD_DN_WRK_ACT_GP"  , ydDnWrkActGp);
			jrParam.setField("YD_EQP_STAT"  	 , "4"    ); //야드설비상태(권하완료)
			
			/**********************************************************
			* 1. 2단 권하시 1단 존재여부 체크
			**********************************************************/
			if ("002".equals(ydDnWrLayer)) {
				/*
				WITH TEMP_TABLE AS (
				SELECT A.YD_STK_COL_GP
				     , A.YD_STK_BED_NO AS YD_STK_BED_NO_LEFT
				     , (CASE WHEN B.YD_STK_SKID_GP = 'F' THEN LPAD(A.YD_STK_BED_NO+1, 2, '0')  --//소재야드OR고정스키드
				        ELSE LPAD(A.YD_STK_BED_NO + (CASE B.YD_COIL_OUTDIA_GRP_GP
				                                         WHEN 'A' THEN 2
				                                         WHEN 'B' THEN 3
				                                         WHEN 'C' THEN 4
				                                       END), 2, '0') END) AS YD_STK_BED_NO_RIGHT
				     , A.YD_STK_LYR_NO                                   
				  FROM TB_YD_STKLYR A
				     , TB_YD_STKCOL B
				 WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
				   AND A.YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND A.YD_STK_BED_NO = :V_YD_STK_BED_NO
				   AND A.YD_STK_LYR_NO = '001'
				   AND A.DEL_YN = 'N'
				   AND B.DEL_YN = 'N'
				   AND B.YD_GP  = 'J'
				)
				SELECT  --//1단 외쪽에 코일이 존재 하는 경우
				      (SELECT C.STL_NO
				         FROM TB_YD_STKLYR C
				            , TEMP_TABLE D
				        WHERE C.YD_STK_COL_GP = D.YD_STK_COL_GP
				          AND C.YD_STK_BED_NO = D.YD_STK_BED_NO_LEFT
				          AND C.YD_STK_LYR_NO = D.YD_STK_LYR_NO
				          AND C.STL_NO IS NOT NULL 
				      ) AS LEFT_COIL
				     , --//1단 오른쪽에 코일이 존재 하는 경우
				      (SELECT C.STL_NO
				         FROM TB_YD_STKLYR C
				            , TEMP_TABLE D
				        WHERE C.YD_STK_COL_GP = D.YD_STK_COL_GP
				          AND C.YD_STK_BED_NO = D.YD_STK_BED_NO_RIGHT
				          AND C.YD_STK_LYR_NO = D.YD_STK_LYR_NO
				          AND C.STL_NO IS NOT NULL
				      ) AS RIGTH_COIL
				  FROM DUAL
				 */
				JDTORecordSet jsLyrChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStklyrDanCheck", logId, mthdNm, "단 체크");
				
				if (jsLyrChk.size() > 0) {
					jsLyrChk.absolute(1);
					
					String sLeftCoil  = commUtils.trim(jsLyrChk.getRecord().getFieldString("LEFT_COIL"));
					String sRigthCoil = commUtils.trim(jsLyrChk.getRecord().getFieldString("RIGTH_COIL"));
					
					if ("".equals(sLeftCoil) || "".equals(sRigthCoil)) {
						ydL3Msg = "1단에 코일이 존재 하지 않아 권하불가";
						resMsg.setField("YD_L3_HD_RS_CD", "9999"); //야드L3처리결과코드
						resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
						
//						throw new Exception(ydL3Msg);
						commUtils.printLog(logId, ydL3Msg, "SL");
						
						jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
						
						commUtils.printLog(logId, mthdNm, "S-");
						return jrRtn; 
					}
				}
			}
			
			/**********************************************************
			* 2. 권하위치 체크 - 소재야드, 제품야드 확인
			**********************************************************/
			/*
			SELECT SC.*
			  FROM TB_YD_STKCOL SC
			 WHERE SC.YD_STK_COL_GP = :V_YD_STK_COL_GP
			 */
			JDTORecordSet jsCol = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStkColByPk", logId, mthdNm, "적치열조회");
			
			if (jsCol.size() <= 0) {
				resMsg.setField("YD_L3_HD_RS_CD", "9999"); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , "적치열 조회 에러"); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			String ydLocGp = commUtils.trim(jsCol.getRecord(0).getFieldString("YD_LOC_GP")); //소재제품야드 구분 H, J
			
			/**********************************************************
			* 3. 크레인스케줄 작업재료 조회
			**********************************************************/
			/* 
			--크레인스케줄상태 조회 
			SELECT A.*
			     , B.STL_NO                             AS STL_NO
			     , B.YD_AID_WRK_YN                      AS YD_AID_WRK_YN
			     , B.YD_STK_LYR_NO                      AS YD_STK_LYR_NO
			     , B.YD_STK_LOT_TP                      AS YD_STK_LOT_TP
			     , B.YD_STK_LOT_CD                      AS YD_STK_LOT_CD
			     , B.HCR_GP                             AS HCR_GP
			     , B.STL_PROG_CD                        AS STL_PROG_CD
			     , B.STL_APPEAR_GP                      AS STL_APPEAR_GP
			     , B.YD_MTL_ITEM                        AS YD_MTL_ITEM
			     , B.YD_ROUTE_GP                        AS YD_ROUTE_GP
			     , B.YD_AIM_YD_GP                       AS YD_AIM_YD_GP
			     , B.YD_MTL_WT                          AS YD_MTL_WT
			     , B.YD_ISPTOR
			     , B.YD_TAKE_OUT_DT
			     , B.YD_TAKE_OUT_CD
			     , (SELECT YD_EQP_WRK_MODE2 FROM TB_YD_EQP WHERE YD_EQP_ID = A.YD_EQP_ID) AS DB_YD_EQP_WRK_MODE2
			  FROM TB_YD_CRNSCH A
			     , (SELECT Y.YD_CRN_SCH_ID
			             , Y.STL_NO
			             , Y.YD_AID_WRK_YN
			             , Y.YD_STK_LYR_NO
			             , Y.YD_STK_LOT_TP
			             , Y.YD_STK_LOT_CD
			             , Y.HCR_GP
			             , (SELECT CURR_PROG_CD   FROM TB_PT_COILCOMM WHERE COIL_NO = X.STL_NO) AS STL_PROG_CD
			             , (SELECT STL_APPEAR_GP  FROM TB_PT_COILCOMM WHERE COIL_NO = X.STL_NO) AS STL_APPEAR_GP
			             , (SELECT NEXT_PROC   FROM TB_PT_COILCOMM WHERE COIL_NO = Y.STL_NO) AS NEXT_PROC
			             , Y.YD_MTL_ITEM
			             , Y.YD_ROUTE_GP
			             , X.YD_AIM_YD_GP
			             , X.YD_MTL_WT
			             , G.YD_ISPTOR
			             , G.YD_TAKE_OUT_DT
			             , G.YD_TAKE_OUT_CD
			          FROM TB_YD_STOCK     X
			             , TB_YD_CRNWRKMTL Y
			             , (SELECT * 
			                  FROM TB_YD_WRKBOOKMTL
			                 WHERE DEL_YN = 'N'
			               ) G
			         WHERE X.STL_NO = Y.STL_NO
			           AND X.STL_NO = G.STL_NO(+)
			      ) B
			 WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
			   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
			 ORDER BY B.YD_STK_LYR_NO
			*/	   
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getStatCrnSch", logId, mthdNm, "크레인스케줄상태 조회");
			if (jsCrnSch.size() <= 0) {
				resMsg.setField("YD_L3_HD_RS_CD", "DN11"); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , "오류:크레인스케쥴ID DB정보 없음"); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			jsCrnSch.first();
			JDTORecord jrCrnSch = jsCrnSch.getRecord();
			String sStlNo       = commUtils.trim(jrCrnSch.getFieldString("STL_NO"        ));
			String ydUpWrLoc    = commUtils.trim(jrCrnSch.getFieldString("YD_UP_WR_LOC"  ));
			String ydWbookId    = commUtils.trim(jrCrnSch.getFieldString("YD_WBOOK_ID"   ));  //작업예약 테이블 데이터를 가져오기 위해 예약ID 저장
	        String sCurrProgCd  = commUtils.trim(jrCrnSch.getFieldString("STL_PROG_CD"   ));
			String ydAidWrkYn   = commUtils.trim(jrCrnSch.getFieldString("YD_AID_WRK_YN" ));  
			String sStlAppearGp = commUtils.trim(jrCrnSch.getFieldString("STL_APPEAR_GP" ));
			//String ydMtlItem    = commUtils.trim(jrCrnSch.getFieldString("YD_MTL_ITEM"   ));  //CM:소재, CG:제품    
			String ydNextProc    = commUtils.trim(jrCrnSch.getFieldString("NEXT_PROC"   ));
			
			
			if ("".equals(ydUpWrLoc)) {
				ydUpWrLoc = commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));
			}
			jrParam.setField("YD_WBOOK_ID"       , ydWbookId      );
			
			
			commUtils.printLog(logId, "권하실적"+sStlNo, "SL"); 
			String sSTL_NO = sStlNo;
			/**********************************************************
			* 4. 강제권하
			**********************************************************/
			// PIDEV			
			commUtils.printLog(logId, "4.강제권하 - ydWrkProgStat : " + ydWrkProgStat + 
					" ydDnWrLoc : " + ydDnWrLoc +
					" ydDnWrLayer : " + ydDnWrLayer, "S+");
			
			if ("5".equals(ydWrkProgStat)) {
				
				String sStlNo5               = "";
				String sStkAbleYn            = "";
				String ydStkLyrXaxis         = "";
				String ydStkLyrYaxis         = "";
				String ydStkLyrMtlStat       = "";
				String ydStkLyrActStat       = "";
				String sStlNo01Curr          = "";
				String sStlNo01Next          = "";
				String sLyrStat01Curr        = "";
				String sLyrStat01Next        = "";       
				
				String sWrYdStkLocGp         = ""; //강제권하 실적을 담기위한 변수
				String sWrYdSrkLyrNo         = "";
				
				resMsg.setField("YD_L2_WR_GP", "F"); //야드L2실적구분 (강제권하)

				if ("KD05LH".equals(ydSchCd.substring(2,8))) { //스크랩 추출
					//스크랩추출은 에러체크 안함
					resMsg.setField("YD_L3_HD_RS_CD", "0000");
					resMsg.setField("YD_L3_MSG"     , ydDnWrLoc+ydDnWrLayer+ydStkLyrXaxis+ydStkLyrYaxis);
				}
				/**************************
				 * 1.1 L2 수신 논리좌표 존재
				 **************************/
				else if (!"".equals(ydDnWrLoc)) { 
					JDTORecordSet jsCrnLoc = null;
					sWrYdStkLocGp = ydDnWrLoc;
					sWrYdSrkLyrNo = ydDnWrLayer;
					
					if ("001".equals(ydDnWrLayer)) { // 1단인 경우
						/* 
						SELECT A.STL_NO
						     , CASE WHEN A.YD_STK_LYR_MTL_STAT IN ('E')
						            THEN 'Y' ELSE 'N' 
						       END AS STK_ABLE_YN
						     , A.YD_STK_LYR_MTL_STAT 
						     , A.YD_STK_LYR_ACT_STAT
						     , LPAD(YD_STK_LYR_XAXIS, 7, '0')  AS YD_STK_LYR_XAXIS
						     , LPAD(YD_STK_LYR_YAXIS, 5, '0')  AS YD_STK_LYR_YAXIS
						     , '' AS LYR_STAT_01_CURR
						     , '' AS STL_NO_01_CURR
						     , '' AS LYR_STAT_01_NEXT
						     , '' AS STL_NO_01_NEXT
						  FROM TB_YD_STKLYR    A 
						     , TB_YD_STKBED    B 
						 WHERE SUBSTR(A.YD_STK_COL_GP,1,1) = 'J' 
						   AND A.YD_STK_COL_GP = B.YD_STK_COL_GP  
						   AND A.YD_STK_BED_NO = B.YD_STK_BED_NO  
						   AND A.YD_STK_COL_GP|| A.YD_STK_BED_NO = :V_YD_DN_WR_LOC
						   AND A.YD_STK_LYR_NO = :V_YD_DN_WR_LAYER
						   AND A.DEL_YN = 'N' 
						   AND B.DEL_YN = 'N'              
						 */
						jsCrnLoc = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStklyrAddress", logId, mthdNm, "좌표에 해당하는 논리 값 READ 조회");
						commUtils.printLog(logId, "강제권하:["+ydDnWrLayer+"]"+" 논리좌표["+ydDnWrLoc+"]", "SL");
						
					} else if ("002".equals(ydDnWrLayer)) {// 2단 일경우 
						/*
						WITH PARAM AS (
						SELECT P_LOC_GP
						     , P_LYR_GP
						     , SUBSTR(P_LOC_GP, 1, 6) || LPAD(SUBSTR(P_LOC_GP, 7, 2) + 1, 2, 0) AS P_LOC_GP_P
						  FROM (
						        SELECT :V_YD_DN_WR_LOC   AS P_LOC_GP
						             , :V_YD_DN_WR_LAYER AS P_LYR_GP
						          FROM DUAL    
						       )
						)
						SELECT * 
						  FROM (
						        SELECT A.STL_NO --해당위치에 위치한 저장품
						             , CASE WHEN (SELECT YD_STK_LYR_MTL_STAT FROM TB_YD_STKLYR WHERE YD_STK_COL_GP||YD_STK_BED_NO = P_LOC_GP   AND YD_STK_LYR_NO = '01') = 'C' 
						                     AND (SELECT YD_STK_LYR_MTL_STAT FROM TB_YD_STKLYR WHERE YD_STK_COL_GP||YD_STK_BED_NO = P_LOC_GP_P AND YD_STK_LYR_NO = '01') = 'C' 
						                     AND A.YD_STK_LYR_MTL_STAT IN ('E')
						                    THEN 'Y' ELSE 'N'
						               END AS STK_ABLE_YN                    
						             , A.YD_STK_LYR_MTL_STAT 
						             , A.YD_STK_LYR_ACT_STAT
						             , LPAD(YD_STK_LYR_XAXIS, 7, '0')  AS YD_STK_LYR_XAXIS
						             , LPAD(YD_STK_LYR_YAXIS, 5, '0')  AS YD_STK_LYR_YAXIS
						             , STL_01.YD_STK_LYR_MTL_STAT AS LYR_STAT_01_CURR
						             , STL_01.STL_NO              AS STL_NO_01_CURR
						             , STL_02.YD_STK_LYR_MTL_STAT AS LYR_STAT_01_NEXT
						             , STL_02.STL_NO              AS STL_NO_01_NEXT
						          FROM TB_YD_STKLYR    A 
						             , TB_YD_STKBED    B 
						             , PARAM
						             , (SELECT STL_NO, YD_STK_LYR_MTL_STAT FROM TB_YD_STKLYR, PARAM WHERE YD_STK_COL_GP||YD_STK_BED_NO = P_LOC_GP   AND YD_STK_LYR_NO = '001') STL_01
						             , (SELECT STL_NO, YD_STK_LYR_MTL_STAT FROM TB_YD_STKLYR, PARAM WHERE YD_STK_COL_GP||YD_STK_BED_NO = P_LOC_GP_P AND YD_STK_LYR_NO = '001') STL_02
						         WHERE SUBSTR(A.YD_STK_COL_GP,1,1) = 'J' 
						           AND A.YD_STK_COL_GP = B.YD_STK_COL_GP  
						           AND A.YD_STK_BED_NO = B.YD_STK_BED_NO  
						           AND A.YD_STK_COL_GP|| A.YD_STK_BED_NO = P_LOC_GP
						           AND A.YD_STK_LYR_NO                = P_LYR_GP
						           AND A.YD_STK_LYR_NO = '002'
						           AND A.DEL_YN = 'N' 
						           AND B.DEL_YN = 'N'      
						      )
						 WHERE 1 = 1 
						 */
						jsCrnLoc = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStklyrAddressLyr2", logId, mthdNm, "좌표에 해당하는 논리 값 READ 조회");
						commUtils.printLog(logId, "강제권하:["+ydDnWrLayer+"]"+" 논리좌표["+ydDnWrLoc+"]", "SL");
						
					} else {
						resMsg.setField("YD_L3_HD_RS_CD", "E000");
						resMsg.setField("YD_L3_MSG"     , "단 정보 이상["+ydDnWrLayer+"]");
						commUtils.printLog(logId, "강제권하:["+ydDnWrLayer+"]"+" 논리좌표["+ydDnWrLoc+"]", "SL");
					}

					
					if (jsCrnLoc.size() == 1) {
						
						sStlNo5         = jsCrnLoc.getRecord(0).getFieldString("STL_NO");
						sStkAbleYn      = jsCrnLoc.getRecord(0).getFieldString("STK_ABLE_YN");
						ydStkLyrXaxis   = jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_XAXIS");
						ydStkLyrYaxis   = jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_YAXIS");
						ydStkLyrMtlStat = jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_MTL_STAT");
						ydStkLyrActStat = jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_ACT_STAT");
						
						sStlNo01Curr    = commUtils.nvl(jsCrnLoc.getRecord(0).getFieldString("STL_01_CURR"), "");
						sStlNo01Next    = commUtils.nvl(jsCrnLoc.getRecord(0).getFieldString("STL_01_NEXT"), "");
						sLyrStat01Curr  = commUtils.nvl(jsCrnLoc.getRecord(0).getFieldString("LYR_STAT_01_CURR"), "");
						sLyrStat01Next  = commUtils.nvl(jsCrnLoc.getRecord(0).getFieldString("LYR_STAT_01_NEXT"), "");
						
						
						// 해당 위치에 저장품 미존재 
						if ("".equals(sStlNo5) && "Y".equals(sStkAbleYn)) {
							
							resMsg.setField("YD_L3_HD_RS_CD", "0000");
							resMsg.setField("YD_L3_MSG"     , ydDnWrLoc+ydDnWrLayer+ydStkLyrXaxis+ydStkLyrYaxis); 
							
						} else {
							
							resMsg.setField("YD_L3_HD_RS_CD", "E000");
						
							// 1단 적치불가 사유
							if ("001".equals(ydDnWrLayer)) {
								
								if ("N".equals(ydStkLyrActStat)) {
									resMsg.setField("YD_L3_MSG" , "논리1단(적치불가상태)["+ydDnWrLoc+ydDnWrLayer+"]");
								} else {
									String sErrMsg = "";
									if ("C".equals(ydStkLyrMtlStat)) {
										sErrMsg = "적치중";
									} else {
										sErrMsg = "작업예약";
									}
									
									resMsg.setField("YD_L3_MSG" , "논리1단적치불가["+sStlNo5 +"("+ sErrMsg+ ")" +"]");
								}
							}
							
							// 2단 적치불가 사유
							if ("002".equals(ydDnWrLayer)) {
								if ("N".equals(ydStkLyrActStat)) {
									resMsg.setField("YD_L3_MSG" , "논리2단(사용불가)["+ydDnWrLoc+ydDnWrLayer+"]");
								} else if ("D".equals(ydStkLyrMtlStat)) {
									resMsg.setField("YD_L3_MSG" , "논리2단(작업예약존재)["+sStlNo5+" "+ydDnWrLoc+ydDnWrLayer+"]");
								} else if ("".equals(sStlNo01Curr) || "".equals(sStlNo01Next)) {
									resMsg.setField("YD_L3_MSG" , "논리2단불가 해당위치 1단저장품 미존재");
								} else if (!"C".equals(sLyrStat01Curr) || !"C".equals(sLyrStat01Next)) {
									resMsg.setField("YD_L3_MSG" , "논리2단불가 1단저장품 작업예약 존재");
								}
							}
							
						}
					} else { //논리좌표 조회	
						resMsg.setField("YD_L3_HD_RS_CD", "E002");
						resMsg.setField("YD_L3_MSG"     , "논리좌표 적치불가["+ydDnWrLoc+ydDnWrLayer+"]"); //해당 물리좌표 적치불가시 수신받은 물리좌표 리턴
					}
					
				} else { 
				/******************************
				 * 1.2 L2 수신 논리좌표 미존재
				 ******************************/ 
					/* 
					SELECT *
					  FROM (
					        SELECT *
					          FROM (
					                SELECT COUNT(*) OVER() AS LOC_ABLE_YN 
					                     , A.YD_STK_LYR_ACT_STAT
					                     , A.YD_STK_LYR_MTL_STAT
					                     , A.YD_STK_COL_GP|| A.YD_STK_BED_NO AS YD_DN_WR_LOC
					                     , A.YD_STK_LYR_NO                   AS YD_DN_WR_LAYER 
					                     , A.YD_STK_LYR_XAXIS 
					                     , A.YD_STK_LYR_YAXIS 
					                     , COUNT(*) OVER(PARTITION BY A.YD_STK_COL_GP|| A.YD_STK_BED_NO ORDER BY A.YD_STK_COL_GP|| A.YD_STK_BED_NO) AS YD_STK_COL_ABLE_YN 
					                  FROM TB_YD_STKLYR    A 
					                     , TB_YD_STKBED    B 
					                 WHERE SUBSTR(A.YD_STK_COL_GP,1,1) = 'J' 
					                   AND A.YD_STK_COL_GP = B.YD_STK_COL_GP  
					                   AND A.YD_STK_BED_NO = B.YD_STK_BED_NO  
					                   AND A.YD_STK_LYR_ACT_STAT = 'E' 
					                   AND A.YD_STK_LYR_MTL_STAT = 'E' 
					                   AND A.DEL_YN = 'N' 
					                   AND B.DEL_YN = 'N' 
					                   AND :V_YD_CRN_XAXIS  BETWEEN A.YD_STK_LYR_XAXIS - NVL(B.YD_STK_BED_XAXIS_TOL,0) AND A.YD_STK_LYR_XAXIS  + NVL(B.YD_STK_BED_XAXIS_TOL,0)
					                   AND :V_YD_CRN_YAXIS  BETWEEN A.YD_STK_LYR_YAXIS - NVL(B.YD_STK_BED_YAXIS_TOL,0) AND A.YD_STK_LYR_YAXIS  + NVL(B.YD_STK_BED_YAXIS_TOL,0)
					                   AND SUBSTR(A.YD_STK_COL_GP, 3, 2) BETWEEN '01' AND '99'
					                   AND SUBSTR(A.YD_STK_COL_GP, 2, 1) LIKE SUBSTR(:V_YD_EQP_ID, 2, 1) ||'%'
					                ) A
					         WHERE LOC_ABLE_YN <= 2 
					           AND YD_STK_COL_ABLE_YN <= 2
					         ORDER BY YD_DN_WR_LAYER
					       ) 
					 WHERE ROWNUM <= 1 				
					 */   
					JDTORecordSet jsCrnLoc = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStklyrXYZ", logId, mthdNm, "좌표에 해당하는 논리 값 READ 조회");
					
					if (jsCrnLoc.size() == 1) {
						JDTORecord rst = jsCrnLoc.getRecord(0);
						ydDnWrLoc     = commUtils.trim(rst.getFieldString("YD_DN_WR_LOC"  )); //야드권하실적위치
						ydDnWrLayer   = commUtils.trim(rst.getFieldString("YD_DN_WR_LAYER")); //야드권하실적단
						
						// PIDEV
	            		commUtils.printLog(logId, "ydDnWrLoc : " + ydDnWrLoc +
	            				" ydDnWrLayer : " + ydDnWrLayer, "SL");
						
						sWrYdStkLocGp = ydDnWrLoc;
						sWrYdSrkLyrNo = ydDnWrLayer;
						
						if ("001".equals(ydDnWrLayer)) {
							resMsg.setField("YD_L3_HD_RS_CD", "0000");
							resMsg.setField("YD_L3_MSG"     , ydDnWrLoc+ydDnWrLayer+ydCrnXaxis+ydCrnYaxis); //해당 물리좌표 적치가능시 논리좌표 리턴
							
						} else {
							// 논리좌표가 2단으로 나올 때 해당 논리좌표로 조회
							/*
							WITH PARAM AS (
							SELECT P_LOC_GP
							     , P_LYR_GP
							     , SUBSTR(P_LOC_GP, 1, 6) || LPAD(SUBSTR(P_LOC_GP, 7, 2) + 1, 2, 0) AS P_LOC_GP_P
							  FROM (
							        SELECT :V_YD_DN_WR_LOC   AS P_LOC_GP
							             , :V_YD_DN_WR_LAYER AS P_LYR_GP
							          FROM DUAL    
							       )
							)
							SELECT * 
							  FROM (
							        SELECT A.STL_NO --해당위치에 위치한 저장품
							             , CASE WHEN (SELECT YD_STK_LYR_MTL_STAT FROM TB_YD_STKLYR WHERE YD_STK_COL_GP||YD_STK_BED_NO = P_LOC_GP   AND YD_STK_LYR_NO = '01') = 'C' 
							                     AND (SELECT YD_STK_LYR_MTL_STAT FROM TB_YD_STKLYR WHERE YD_STK_COL_GP||YD_STK_BED_NO = P_LOC_GP_P AND YD_STK_LYR_NO = '01') = 'C' 
							                     AND A.YD_STK_LYR_MTL_STAT IN ('E')
							                    THEN 'Y' ELSE 'N'
							               END AS STK_ABLE_YN                    
							             , A.YD_STK_LYR_MTL_STAT 
							             , A.YD_STK_LYR_ACT_STAT
							             , LPAD(YD_STK_LYR_XAXIS, 7, '0')  AS YD_STK_LYR_XAXIS
							             , LPAD(YD_STK_LYR_YAXIS, 5, '0')  AS YD_STK_LYR_YAXIS
							             , STL_01.YD_STK_LYR_MTL_STAT AS LYR_STAT_01_CURR
							             , STL_01.STL_NO              AS STL_NO_01_CURR
							             , STL_02.YD_STK_LYR_MTL_STAT AS LYR_STAT_01_NEXT
							             , STL_02.STL_NO              AS STL_NO_01_NEXT
							          FROM TB_YD_STKLYR    A 
							             , TB_YD_STKBED    B 
							             , PARAM
							             , (SELECT STL_NO, YD_STK_LYR_MTL_STAT FROM TB_YD_STKLYR, PARAM WHERE YD_STK_COL_GP||YD_STK_BED_NO = P_LOC_GP   AND YD_STK_LYR_NO = '001') STL_01
							             , (SELECT STL_NO, YD_STK_LYR_MTL_STAT FROM TB_YD_STKLYR, PARAM WHERE YD_STK_COL_GP||YD_STK_BED_NO = P_LOC_GP_P AND YD_STK_LYR_NO = '001') STL_02
							         WHERE SUBSTR(A.YD_STK_COL_GP,1,1) = 'J' 
							           AND A.YD_STK_COL_GP = B.YD_STK_COL_GP  
							           AND A.YD_STK_BED_NO = B.YD_STK_BED_NO  
							           AND A.YD_STK_COL_GP|| A.YD_STK_BED_NO = P_LOC_GP
							           AND A.YD_STK_LYR_NO                = P_LYR_GP
							           AND A.YD_STK_LYR_NO = '002'
							           AND A.DEL_YN = 'N' 
							           AND B.DEL_YN = 'N'      
							      )
							 WHERE 1 = 1  
							 */
							jrParam.setField("YD_DN_WR_LOC"   , ydDnWrLoc);
							jrParam.setField("YD_DN_WR_LAYER" , ydDnWrLayer);
							jsCrnLoc = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStklyrAddressLyr2", logId, mthdNm, "좌표에 해당하는 논리 값 READ 조회");							
							
							if (jsCrnLoc.size() == 1) {
								
								sStlNo5          = jsCrnLoc.getRecord(0).getFieldString("STL_NO");
								sStkAbleYn      = jsCrnLoc.getRecord(0).getFieldString("STK_ABLE_YN");
								ydStkLyrXaxis   = jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_XAXIS");
								ydStkLyrYaxis   = jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_YAXIS");
								ydStkLyrMtlStat = jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_MTL_STAT");
								ydStkLyrActStat = jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_ACT_STAT");
								
								sStlNo01Curr    = jsCrnLoc.getRecord(0).getFieldString("STL_NO_01_CURR");
								sStlNo01Next    = jsCrnLoc.getRecord(0).getFieldString("STL_NO_01_NEXT");
								sLyrStat01Curr  = jsCrnLoc.getRecord(0).getFieldString("LYR_STAT_01_CURR");
								sLyrStat01Next  = jsCrnLoc.getRecord(0).getFieldString("LYR_STAT_01_NEXT");
								
								// PIDEV
			            		commUtils.printLog(logId, "sStlNo5 : " + sStlNo5 +
			            				" sStkAbleYn : " + sStkAbleYn +
			            				" ydStkLyrXaxis : " + ydStkLyrXaxis +
			            				" ydStkLyrYaxis : " + ydStkLyrYaxis +
			            				" ydStkLyrMtlStat : " + ydStkLyrMtlStat +
			            				" ydStkLyrActStat : " + ydStkLyrActStat +
			            				" sStlNo01Curr : " + sStlNo01Curr +
			            				" sStlNo01Next : " + sStlNo01Next +
			            				" sLyrStat01Curr : " + sLyrStat01Curr +
			            				" sLyrStat01Next : " + sLyrStat01Next, "SL");
								
								// 해당 위치에 저장품 미존재 
								if ("".equals(sStlNo5) && "Y".equals(sStkAbleYn)) {
									
									resMsg.setField("YD_L3_HD_RS_CD", "0000");
									resMsg.setField("YD_L3_MSG"     , ydDnWrLoc+ydDnWrLayer+ydStkLyrXaxis+ydStkLyrYaxis); 
									
								} else {
									
									resMsg.setField("YD_L3_HD_RS_CD", "E000");
								
									// 1단 적치불가 사유
									if ("001".equals(ydDnWrLayer)) {
										
										if ("N".equals(ydStkLyrActStat)) {
											resMsg.setField("YD_L3_MSG" , "물리1단(적치불가상태)["+ydDnWrLoc+ydDnWrLayer+"]");
										} else {
											resMsg.setField("YD_L3_MSG" , "물리1단적치불가["+sStlNo5 +"("+ ydStkLyrMtlStat=="C"?"적치중":"작업예약"+ ")" +"]");
										}
									}
									
									// 2단 적치불가 사유
									if ("002".equals(ydDnWrLayer)) {
										if ("N".equals(ydStkLyrActStat)) {
											resMsg.setField("YD_L3_MSG" , "물리2단(사용불가)["+ydDnWrLoc+ydDnWrLayer+"]");
										} else if ("D".equals(ydStkLyrMtlStat)) {
											resMsg.setField("YD_L3_MSG" , "물리2단(작업예약존재)["+sStlNo5+" "+ydDnWrLoc+ydDnWrLayer+"]");
										} else if ("".equals(sStlNo01Curr) || "".equals(sStlNo01Next)) {
											resMsg.setField("YD_L3_MSG" , "물리2단불가 해당위치 1단저장품 미존재");
										} else if (!"C".equals(sLyrStat01Curr) || !"C".equals(sLyrStat01Next)) {
											resMsg.setField("YD_L3_MSG" , "물리2단불가 1단저장품 작업예약 존재");
										}
									}
									
								}// else
							} //if (jsCrnLoc.size() == 1)
							
							
						} //물리좌표로 좌표 조회했을때 논리좌표가 2단으로 나왔을 때 
						
					} else {	
						resMsg.setField("YD_L3_HD_RS_CD", "E002");
						resMsg.setField("YD_L3_MSG"     , "물리좌표 적치불가["+ydCrnXaxis+" "+ydCrnYaxis+"]"); //해당 물리좌표 적치불가시 수신받은 물리좌표 리턴
					}
				}
				commUtils.printLog(logId, "sStlNo            = " + sStlNo5             , "SL");
				commUtils.printLog(logId, "sStkAbleYn        = " + sStkAbleYn         , "SL");
				commUtils.printLog(logId, "ydStkLyrXaxis     = " + ydStkLyrXaxis      , "SL");
				commUtils.printLog(logId, "ydStkLyrYaxis     = " + ydStkLyrYaxis      , "SL");
				commUtils.printLog(logId, "ydStkLyrMtlStat   = " + ydStkLyrMtlStat    , "SL");
				commUtils.printLog(logId, "ydStkLyrActStat   = " + ydStkLyrActStat    , "SL");
				commUtils.printLog(logId, "sStlNo01Curr      = " + sStlNo01Curr       , "SL");
				commUtils.printLog(logId, "sStlNo01Next      = " + sStlNo01Next       , "SL");
				commUtils.printLog(logId, "sLyrStat01Curr    = " + sLyrStat01Curr     , "SL");
				commUtils.printLog(logId, "sLyrStat01Next    = " + sLyrStat01Next     , "SL");
				
				//강제권하 정보 저장
				if ("0000".equals(resMsg.getFieldString("YD_L3_HD_RS_CD"))) {
					commUtils.printLog(logId, "강제권하 정보 저장", "SL");
					resMsg.setField("WR_YD_STK_LOC_GP", sWrYdStkLocGp );					
					resMsg.setField("WR_YD_STK_LYR_NO", sWrYdSrkLyrNo );
					/*
					MERGE INTO TB_YD_CRNSCH CS USING (
					SELECT SL.YD_STK_COL_GP
					     , SL.YD_STK_BED_NO
					     , SL.YD_STK_LYR_NO
					     , SL.YD_STK_LYR_XAXIS
					     , SL.YD_STK_LYR_YAXIS
					     , ROUND(CASE WHEN WR_YD_STK_LYR_NO = '002' THEN SF_YD_WO_LOC_ZAXIS_AUTO(SUBSTR(WR_YD_STK_LOC_GP,1,6),SUBSTR(WR_YD_STK_LOC_GP,7,2), CC.COIL_OUTDIA)
					                  ELSE NVL(YD_STK_LYR_ZAXIS ,0) + ( CC.COIL_OUTDIA / 2 )           
					              END)  AS YD_STK_LYR_ZAXIS
					     , CS.YD_CRN_SCH_ID
					  FROM TB_YD_STKLYR  SL
					     ,(
					       SELECT :V_WR_YD_STK_LOC_GP AS WR_YD_STK_LOC_GP
					            , :V_WR_YD_STK_LYR_NO AS WR_YD_STK_LYR_NO
					         FROM DUAL
					      ) P
					     , TB_YD_CRNSCH    CS
					     , TB_YD_CRNWRKMTL CM
					     , TB_PT_COILCOMM  CC
					 WHERE SL.YD_STK_COL_GP = SUBSTR(P.WR_YD_STK_LOC_GP, 1, 6)
					   AND SL.YD_STK_BED_NO = SUBSTR(P.WR_YD_STK_LOC_GP, 7, 2)
					   AND SL.YD_STK_LYR_NO = P.WR_YD_STK_LYR_NO
					   AND CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					   AND CM.STL_NO        = CC.COIL_NO
					   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					 ) DD ON (CS.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID)
					 WHEN MATCHED THEN UPDATE SET
					      CS.MODIFIER       = :V_MODIFIER
					    , CS.MOD_DDTT       = SYSDATE
					    , CS.YD_DN_WR_LOC   = DD.YD_STK_COL_GP||DD.YD_STK_LYR_NO
					    , CS.YD_DN_WR_LAYER = DD.YD_STK_LYR_NO
					    , CS.YD_DN_WR_XAXIS = DD.YD_STK_LYR_XAXIS
					    , CS.YD_DN_WR_YAXIS = DD.YD_STK_LYR_YAXIS
					    , CS.YD_DN_WR_ZAXIS = DD.YD_STK_LYR_ZAXIS
					 */
					commDao.update(resMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL009_5WR", logId, mthdNm, "강제권하 정보 저장");
					
				} else {
					commUtils.printLog(logId, "오류:강제권하위치 이상", "SL");
					
					jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
					
					commUtils.printLog(logId, mthdNm, "S-");
					return jrRtn; 
				}
			}	
			
			/**********************************************************
			* 8. 설비상태 권하로 변경
			*     권하지시위치와 권하실적위치 다르면 권하지시위치 맵정보 Clear 
			*     적치단 권하정보 등록
			*     크레인 작업재료 삭제
			*     크레인스케줄 삭제
			*     작업예약삭제
			*     작업예약재료삭제       
			**********************************************************/
			//설비(야드설비상태) 수정
			/*
			UPDATE TB_YD_EQP
			   SET MODIFIER    = :V_MODIFIER
			     , MOD_DDTT    = SYSDATE
			     , YD_EQP_STAT = :V_YD_EQP_STAT
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND DEL_YN    = 'N'
			*/	   
			//commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updStatEqp", logId, mthdNm, "설비상태 수정");
			
			String sAPP017_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","017"); 
			commUtils.printLog(logId, "설비update 트랜잭션 분리 : [" + sAPP017_YN+"]", "SL");
			
			if("Y".equals(sAPP017_YN)){
				commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updStatEqp", logId, mthdNm, "설비상태 수정tx");
			}else{
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updStatEqp", logId, mthdNm, "설비상태 수정");
			}
			//이전 권하위치 단을 재료단위로 READ Clear 한다.
			/*
			UPDATE TB_YD_STKLYR
			   SET MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			     , STL_NO              = NULL
			     , YD_STK_LYR_MTL_STAT = 'E'
			 WHERE 1=1
			   AND STL_NO IN (SELECT STL_NO 
			                    FROM TB_YD_CRNWRKMTL
			                   WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			                 )
			   AND SUBSTR(YD_STK_COL_GP,1,1) IN ('H','J')
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.clrUpDnWrkMtl", logId, mthdNm, "적치단 작업 크레인 및권상위치 Clear");
			
			/* 적치단 정보 등록
			UPDATE TB_YD_STKLYR
			   SET STL_NO = (SELECT STL_NO
						       FROM TB_YD_CRNWRKMTL
						      WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						     )
			     , MODIFIER            = :V_MODIFIER
				 , MOD_DDTT            = SYSDATE
				 , YD_STK_LYR_MTL_STAT = 'C'
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
			   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL009StkLyr", logId, mthdNm, "적치단 정보 등록");
			
			
			jrParam.setField("STL_NO"   , sStlNo  ); //권하 코일번호
//			jrParam.setField("YD_LOC"   , ydDnWrLoc); //야드권하실적위치
			jrParam.setField("YD_LOC"   , ydDnWrLoc+ydDnWrLayer); //야드권하실적위치
			/*
			UPDATE TB_PT_COILCOMM
			   SET (  
			         YD_GP                 -- 야드구분
			       , YD_BAY_GP             -- 동
			       , YD_EQP_GP             -- SPAN
			       , YD_STK_COL_NO         -- 적치열번지
			       , YD_STK_BED_NO         -- 적치번지
			       , YD_STK_LYR_NO         -- 적치단
			       , YD_STR_LOC            -- 현 저장위치코드
			       , YD_STR_LOC_HIS1       -- 전 저장위치코드
			       , YD_STR_LOC_HIS2       -- 전전 저장위치코드
			       ) =
			       (
			        SELECT 
			               SUBSTR(P_YD_LOC,1,1) AS YD_GP         -- 야드구분
			             , SUBSTR(P_YD_LOC,2,1) AS YD_BAY_GP     -- 동
			             , SUBSTR(P_YD_LOC,3,2) AS YD_EQP_GP     -- SPAN
			             , SUBSTR(P_YD_LOC,5,2) AS YD_STK_COL_NO -- 적치열번지
			             , SUBSTR(P_YD_LOC,7,2) AS YD_STK_BED_NO -- 적치번지
			             , SUBSTR(P_YD_LOC,9,3) AS YD_STK_LYR_NO -- 적치단
			             , P_YD_LOC        AS YD_STR_LOC         -- 현 저장위치코드   
			             , YD_STR_LOC      AS YD_STR_LOC_HIS1    -- 전현 저장위치코드
			             , YD_STR_LOC_HIS1 AS YD_STR_LOC_HIS2    -- 전전현 저장위치코드
			          FROM TB_PT_COILCOMM
			             ,(SELECT :V_YD_LOC AS P_YD_LOC FROM DUAL) 
			         WHERE COIL_NO = :V_STL_NO
			     )
			 WHERE COIL_NO = :V_STL_NO
			 */
			commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updateCoilCommonLocInfo", logId, mthdNm, "TB_PT_COILCOMM 저장위치 수정");
			
			
			
			/*
			-- 크레인권하실적 크레인스케줄 수정 
			UPDATE TB_YD_CRNSCH CS
			   SET CS.MODIFIER         =:V_MODIFIER 
			     , CS.MOD_DDTT         = SYSDATE
			     , CS.DEL_YN           = 'Y'
			     , CS.YD_WRK_PROG_STAT = '4' --권하완료
			     , CS.YD_WRK_HDS_DD    = SF_YD_WRK_HDS_DD(TO_DATE(:V_YD_DN_CMPL_DT,'YYYYMMDDHH24MISS'))
			     , CS.YD_WRK_DUTY      = SF_YD_WRK_DUTY(TO_DATE(:V_YD_DN_CMPL_DT,'YYYYMMDDHH24MISS'))
			     , CS.YD_WRK_PARTY     = SF_YD_WRK_PARTY(TO_DATE(:V_YD_DN_CMPL_DT,'YYYYMMDDHH24MISS'))
			     , CS.YD_DN_CMPL_DT    = TO_DATE(:V_YD_DN_CMPL_DT,'YYYYMMDDHH24MISS')
			     , CS.YD_DN_WR_LOC     = :V_YD_DN_WR_LOC 
			     , CS.YD_DN_WR_LAYER   = :V_YD_DN_WR_LAYER 
			     , CS.YD_DN_WRK_ACT_GP = :V_YD_DN_WRK_ACT_GP   
			     , CS.YD_DN_WR_XAXIS   = TO_NUMBER(:V_YD_DN_WR_XAXIS)
			     , CS.YD_DN_WR_YAXIS   = TO_NUMBER(:V_YD_DN_WR_YAXIS)  
			     , CS.YD_DN_WR_ZAXIS   = TO_NUMBER(:V_YD_DN_WR_ZAXIS)
			     , CS.YD_DN_WRK_MODE2  = :V_YD_DN_WRK_MODE2
			 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			 */   
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL009CrnSch", logId, mthdNm, "크레인스케쥴 삭제");
			
			//작업예약재료 삭제
			/*
			UPDATE TB_YD_WRKBOOKMTL
			   SET DEL_YN   = 'Y'
			     , MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND STL_NO IN ( 
			                  SELECT STL_NO 
			                    FROM TB_YD_CRNWRKMTL
			                   WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			                 )
			*/	   
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL009WbMtlDel", logId, mthdNm, "작업예약재료 삭제");

			//작업예약 삭제
			/*
			UPDATE TB_YD_WRKBOOK
			   SET MODIFIER         = :V_MODIFIER
				 , MOD_DDTT         = SYSDATE
			     , DEL_YN           = 'Y'
			     , YD_SCH_PROG_STAT = 'E' --End
			 WHERE DEL_YN           = 'N'
			   AND YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND 1 = (SELECT DECODE(COUNT(*), 0, 1, 0) 
			              FROM TB_YD_WRKBOOKMTL
			             WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			               AND DEL_YN = 'N'
			           )
			*/	   
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL009WbDel", logId, mthdNm, "작업예약 삭제");
			
			/* 
			--크레인권하실적 크레인작업재료 삭제 -
			UPDATE TB_YD_CRNWRKMTL
			   SET MODIFIER      = :V_MODIFIER
				 , MOD_DDTT      = SYSDATE
			     , DEL_YN        = 'Y'
			 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			   AND DEL_YN        = 'N'
			*/	   
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL009CrnMtl", logId, mthdNm, "크레인작업재료 삭제");
			
			/**********************************************************
			 * 중복코일 저장위치 변경 YYS 20230719
			 **********************************************************/
			
        	
    		JDTORecord jrInUpParam 	= commUtils.getParam(logId, mthdNm, sModifier);
    		String sAPP015_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","015"); 
			commUtils.printLog(logId, "중복재료 삭제처리 : " + sAPP015_YN, "SL");
			
			if ("Y".equals(sAPP015_YN)) {
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnWrkMgtUpLocCoilChek 	
				SELECT A.YD_EQP_ID                AS YD_EQP_ID               
				     , A.YD_CRN_SCH_ID            AS YD_CRN_SCH_ID            
				 FROM TB_YD_CRNSCH A
				    , TB_YD_CRNWRKMTL CM
				    , (
				        SELECT  :V_YD_UP_WR_LOC AS V_YD_UP_WR_LOC
				               ,:V_STL_NO       AS V_STL_NO
				           FROM DUAL
				      ) CD
				WHERE A.YD_CRN_SCH_ID      = CM.YD_CRN_SCH_ID 
				  AND A.YD_UP_WO_LOC       = CD.V_YD_UP_WR_LOC 
				  AND A.YD_TO_LOC_DCSN_MTD = 'W' --보조작업
				  AND A.YD_UP_WO_LAYER     = '002'
				  AND CM.STL_NO            = CD.V_STL_NO
				  AND SUBSTR(CD.V_YD_UP_WR_LOC,3,2) BETWEEN '01' AND '99'
				  AND SUBSTR(A.YD_SCH_CD,8,1) = 'H' -- 소재
				  AND A.DEL_YN  = 'N'
				ORDER BY A.YD_CRN_SCH_ID
				*/
				jrInUpParam.setField("YD_UP_WR_LOC"  , ydUpWrLoc   );
				jrInUpParam.setField("STL_NO"        , sStlNo      );
				jrInUpParam.setField("MODIFIER"      , "DupCoil"   );
	    		JDTORecordSet jsUpLocCoilChek = commDao.select(jrInUpParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnWrkMgtUpLocCoilChek", logId, mthdNm, "크레인스케줄 조회");   			
				if(jsUpLocCoilChek.size()>0 ){
					
							jrInUpParam.setField("YD_CRN_SCH_ID"			, commUtils.trim(jsUpLocCoilChek.getRecord(0).getFieldString("YD_CRN_SCH_ID")));
														
							/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnWrkMgtUpLoc 
							UPDATE TB_YD_CRNSCH 
							   SET MODIFIER               = :V_MODIFIER
							     , MOD_DDTT               = SYSDATE
							     , DEL_YN                 = 'Y'					     
							 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID    
							 */
						    int intRtnVal = commDao.update(jrInUpParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnWrkMgtUpLoc", logId, mthdNm, "크레인스케줄 갱신");
						    
				}
				   
			}
			/**********************************************************
			* 6. 권하위치가 대차
			**********************************************************/
			
			String tcarLdCmplYn = "N";
			commUtils.printLog(logId, "권하위치 대차인지 CHECK : " + "TC".equals(ydDnWrLoc.substring(2, 4)), "SL");
			if ("TC".equals(ydDnWrLoc.substring(2, 4))) {
				
				jrParam.setField("YD_WRK_PLAN_TCAR", ydDnWrLoc.substring(0, 1) + "X" + ydDnWrLoc.substring(2, 6));
				jrParam.setField("YD_CARLD_WRK_CRN", ydEqpId);
				//대차상차스케쥴 조회
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getY5YDL009TCarSchLd 
				-- 
				-- 크레인권하실적 상차 대차스케줄 조회
				WITH TC_LAY_INFO  AS
				-- 
				(
				SELECT NVL(COUNT(*),0)        AS TC_LAY_CNT
				     , NVL(SUM(C.COIL_WT),0)  AS TC_LAY_WGT
				  FROM TB_YD_STKLYR A
				     , TB_PT_COILCOMM C
				 WHERE A.STL_NO  = C.COIL_NO
				   AND A.YD_STK_COL_GP = SUBSTR(:V_YD_DN_WR_LOC,1,6)
				   AND A.STL_NO IS NOT NULL
				   AND A.DEL_YN = 'N'
				   AND A.YD_STK_LYR_MTL_STAT = 'C'
				), 
				NEXT_SCH_INFO  AS
				---- 다음 크레인 스케쥴 정보
				(
				SELECT 1                   AS NEXT_CRNSCH_CNT
				     , NVL(MAX(COIL_WT),0) AS NEXT_CRNSCH_WGT
				  FROM
				       (
				        SELECT C.COIL_WT 
				          FROM TB_YD_CRNSCH A
				             , TB_YD_CRNWRKMTL B
				             , TB_PT_COILCOMM C
				         WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
				           AND B.STL_NO        = C.COIL_NO
				           AND A.DEL_YN = 'N'
				           AND B.DEL_YN = 'N'
				           AND SUBSTR(A.YD_DN_WO_LOC,1,6) = SUBSTR(:V_YD_DN_WR_LOC,1,6)
				         ORDER BY A.YD_SCH_PRIOR ASC, A.YD_CRN_SCH_ID 
				       )
				 WHERE ROWNUM = 1

				), NEXT_WB_INFO  AS
				 -- 다음작업예약 정보
				(
				SELECT 1                   AS NEXT_WB_CNT
				     , NVL(MAX(COIL_WT),0) AS NEXT_WB_WGT
				   FROM (
				          SELECT C.COIL_WT
				               , WB.YD_GP
				               , WB.YD_BAY_GP
				            FROM TB_YD_WRKBOOK WB
				               , TB_YD_WRKBOOKMTL WM
				               , TB_YD_EQP  D   
				               , TB_PT_COILCOMM C
				           WHERE WB.YD_WBOOK_ID  = WM.YD_WBOOK_ID 
				             AND WM.STL_NO = C.COIL_NO
				             AND WB.YD_WRK_PLAN_TCAR = D.YD_EQP_ID
				             AND WB.YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
				             AND WB.DEL_YN = 'N'
				             AND WM.DEL_YN = 'N'
				--             AND WB.YD_SCH_CD LIKE 'J' || SUBSTR(:V_YD_DN_WR_LOC,2,1) || 'TC%'
				--             AND SUBSTR(WB.YD_SCH_CD,7,2) IN ('LH','UM','MM')
				             AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
				                                          FROM USRYDA.TB_YD_CRNSCH B 
				                                         WHERE B.DEL_YN = 'N' 
				                                           AND B.YD_GP  = 'J'
				                                           AND B.YD_BAY_GP = WB.YD_BAY_GP
				                                        )
				             ORDER BY WB.YD_SCH_PRIOR ASC, WB.YD_WBOOK_ID ASC 
				            ) A
				   WHERE ROWNUM = 1
				)
				SELECT YD_TCAR_SCH_ID 
				     , CASE WHEN YD_LOC_GP = 'H' THEN
				            CASE WHEN TC_LAY_CNT      >= TC_MAX_CNT      THEN 'Y'
				                 WHEN NEXT_CRNSCH_CNT +  NEXT_WB_CNT = 0 THEN 'Y'
				                 WHEN NEXT_CRNSCH_CNT = 1 THEN 
				                      CASE WHEN TC_LAY_WGT + NEXT_CRNSCH_WGT > TC_MAX_WGT THEN 'Y'
				                           ELSE 'N' END
				                 WHEN NEXT_WB_CNT     = 1 THEN 
				                      CASE WHEN TC_LAY_WGT + NEXT_WB_WGT > TC_MAX_WGT THEN 'Y'
				                           ELSE 'N' END
				                 ELSE 'N' END
				       ELSE 
				            CASE WHEN TC_LAY_CNT      >= TC_MAX_CNT      THEN 'Y'
				                 -- T01,T02 는 2매일경우 다음 작업이 없으면 출발
				                 WHEN TC_MAX_CNT = '3' AND TC_LAY_CNT = '2' AND NEXT_CRNSCH_CNT + NEXT_WB_CNT = 0 THEN 'Y'
				                 WHEN TC_MAX_CNT = '1' AND NEXT_CRNSCH_CNT + NEXT_WB_CNT = 0 THEN 'Y'
				                 WHEN NEXT_CRNSCH_CNT = 1 THEN 
				                      CASE WHEN TC_LAY_WGT + NEXT_CRNSCH_WGT > TC_MAX_WGT THEN 'Y'
				                           ELSE 'N' END
				                 WHEN NEXT_WB_CNT     = 1 THEN 
				                      CASE WHEN TC_LAY_WGT + NEXT_WB_WGT > TC_MAX_WGT THEN 'Y'
				                           ELSE 'N' END
				                 ELSE 'N' END
				       END AS TCAR_LD_CMPL_YN --대차상차완료여부

				     , YD_CARUD_STOP_LOC       
				     , YD_EQP_ID
				     , TC_LAY_CNT
				     , TC_LAY_WGT
				     , NEXT_CRNSCH_CNT
				     , NEXT_CRNSCH_WGT
				     , NEXT_WB_CNT
				     , NEXT_WB_WGT
				     , TC_MAX_CNT
				     , TC_MAX_WGT
				  FROM (
				        SELECT YD_TCAR_SCH_ID 
				             , A.TC_LAY_CNT 
				             , A.TC_LAY_WGT
				             , C.NEXT_CRNSCH_WGT 
				             , CASE WHEN C.NEXT_CRNSCH_WGT = 0 THEN 0 ELSE 1 END AS NEXT_CRNSCH_CNT
				             , D.NEXT_WB_WGT
				             , CASE WHEN D.NEXT_WB_WGT = 0 THEN 0 ELSE 1 END AS NEXT_WB_CNT
				             , EQ.STK_BED_MAX_QNTY AS TC_MAX_CNT
				             , EQ.STK_BED_MAX_WT   AS TC_MAX_WGT
				             , TS.YD_EQP_ID
				             , EQ.YD_LOC_GP
				             , 'J' || (SELECT YD_AIM_BAY_GP FROM TB_YD_WRKBOOK WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID )|| SUBSTR(TS.YD_EQP_ID,3,4)  
				             AS YD_CARUD_STOP_LOC  --야드하차정지위치
				          FROM TC_LAY_INFO    A
				             , NEXT_SCH_INFO  C
				             , NEXT_WB_INFO   D
				             , TB_YD_TCARSCH  TS
				             , TB_YD_EQP      EQ
				         WHERE TS.YD_EQP_ID = EQ.YD_EQP_ID
				           AND TS.YD_EQP_ID = :V_YD_WRK_PLAN_TCAR
				           AND TS.DEL_YN    = 'N'
				) 
				*/
				JDTORecordSet jsTc = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getY5YDL009TCarSchLd", logId, mthdNm, "대차상차스케쥴 조회 조회!!!");
				
				if (jsTc.size() > 0) {
					JDTORecord jrTc = jsTc.getRecord(0);

					jrParam.setField("YD_TCAR_SCH_ID", commUtils.trim(jrTc.getFieldString("YD_TCAR_SCH_ID"))); //야드대차스케쥴ID(이력등록시에도 사용)
					
					//대차이송재료 등록
					/*
					MERGE INTO TB_YD_TCARFTMVMTL TM USING (
					SELECT :V_YD_TCAR_SCH_ID AS YD_TCAR_SCH_ID
					     , CM.STL_NO
					     , :V_MODIFIER       AS MODIFIER
					     , SYSDATE           AS MOD_DDTT
					     , 'N'               AS DEL_YN
					     , SUBSTR(:V_YD_DN_WR_LOC,7,2) AS YD_STK_BED_NO
					     , '001'             AS YD_STK_LYR_NO
					     , CC.HCR_GP
					     , CC.CURR_PROG_CD   AS STL_PROG_CD
					  FROM TB_YD_CRNWRKMTL  CM
					     , TB_YD_STOCK      ST
					     , TB_PT_COILCOMM   CC  
					 WHERE CM.STL_NO        = ST.STL_NO
					   AND CM.STL_NO        = CC.COIL_NO
					   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					) DD ON (TM.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID AND TM.STL_NO = DD.STL_NO)
					WHEN NOT MATCHED THEN
					INSERT (TM.YD_TCAR_SCH_ID, TM.STL_NO       , TM.REGISTER   , TM.REG_DDTT     ,
					        TM.MODIFIER      , TM.MOD_DDTT     , TM.DEL_YN     , TM.YD_STK_BED_NO,
					        TM.YD_STK_LYR_NO, TM.HCR_GP        , TM.STL_PROG_CD )
					VALUES (DD.YD_TCAR_SCH_ID, DD.STL_NO       , DD.MODIFIER   , DD.MOD_DDTT     ,
					        DD.MODIFIER      , DD.MOD_DDTT     , DD.DEL_YN     , DD.YD_STK_BED_NO,
					        DD.YD_STK_LYR_NO, DD.HCR_GP        , DD.STL_PROG_CD )
					*/        
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insYdTCarMtl", logId, mthdNm, "대차이송재료 등록");

					tcarLdCmplYn         = commUtils.trim(jrTc.getFieldString("TCAR_LD_CMPL_YN"));    	//대차상차완료여부
					String tcarUdStopLoc = commUtils.trim(jrTc.getFieldString("YD_CARUD_STOP_LOC"));  	//하차 TO위치
//					String tcarEqpId 	 = commUtils.trim(jrTc.getFieldString("YD_EQP_ID"));  			//대차설비ID
					
					String ydTcWrkBookId = "";
					String sTcLayCnt 	 = commUtils.trim(jrTc.getFieldString("TC_LAY_CNT"));  			
					String sTcLayWgt 	 = commUtils.trim(jrTc.getFieldString("TC_LAY_WGT"));  			
					String sNextCoilCnt  = commUtils.trim(jrTc.getFieldString("NEXT_CRNSCH_CNT"));  		
					String sNextCoilWgt  = commUtils.trim(jrTc.getFieldString("NEXT_CRNSCH_WGT"));  		
					String sNextWbCnt 	 = commUtils.trim(jrTc.getFieldString("NEXT_WB_CNT"));  		
					String sNextWbWgt 	 = commUtils.trim(jrTc.getFieldString("NEXT_WB_WGT"));  		
					String sTcMaxCnt 	 = commUtils.trim(jrTc.getFieldString("TC_MAX_CNT"));  		
					String sTcMaxWgt 	 = commUtils.trim(jrTc.getFieldString("TC_MAX_WGT"));  			

					commUtils.printLog(logId, "상차완료기준    코일수량:" + sTcMaxCnt +"/   코일중량:" + sTcMaxWgt, "SL");

					commUtils.printLog(logId, "대차위            코일수량:" + sTcLayCnt    +"/   코일중량:" + sTcLayWgt, "SL");
					commUtils.printLog(logId, "크레인작업대상  코일수량:" + sNextCoilCnt +"/   코일중량:" + sNextCoilWgt, "SL");
					commUtils.printLog(logId, "작업예약          코일수량:" + sNextWbCnt +"/     코일중량:" + sNextWbWgt, "SL");

					
					commUtils.printLog(logId, "★★★★★  권하대차스케쥴:" + commUtils.trim(jrTc.getFieldString("YD_TCAR_SCH_ID")) 
							 +  " 대차상차완료여부:" + tcarLdCmplYn +  " 하차 위치 :" + tcarUdStopLoc +" ★★★★", "SL");
					
					jrParam.setField("YD_CARUD_STOP_LOC", tcarUdStopLoc);                               //TO위치
					
					if ("N".equals(tcarLdCmplYn)) {
						//상차완료가 아니면
						jrParam.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					} else {
						
						//상차완료이면 기존에 만들어진 하차스케쥴을 삭제후 새로 생성
						jrParam.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
						
						/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getY5YDL009WbTCarIns 
						SELECT TM.STL_NO
						     , TS.YD_CARUD_STOP_LOC AS YD_STK_COL_GP
						     , TM.YD_STK_BED_NO
						     , TM.YD_STK_LYR_NO
						     , COUNT(*) OVER () - ROW_NUMBER() OVER (ORDER BY YD_STK_LYR_NO) + 1 AS YD_UP_COLL_SEQ
						     , CASE WHEN SUBSTR(TS.YD_EQP_ID,3,4) IN ('TC01','TC02') AND SUBSTR(YD_CARUD_STOP_LOC,2,1) IN ('D','E')
						                 THEN ABS(TO_NUMBER(TM.YD_STK_BED_NO) - 10)
						            ELSE TO_NUMBER(TM.YD_STK_BED_NO)
						            END SORT_SEQ
						     , (SELECT AA.YD_WBOOK_ID
						          FROM TB_YD_CRNSCH  AA
						         WHERE AA.YD_CRN_SCH_ID = (SELECT MAX(CC.YD_CRN_SCH_ID)
						                                     FROM TB_YD_CRNSCH    CC
						                                        , TB_YD_CRNWRKMTL DD
						                                    WHERE CC.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID
						                                      AND DD.STL_NO        = TM.STL_NO
						                                      AND CC.YD_DN_WR_LOC  = TS.YD_CARLD_STOP_LOC || YD_STK_BED_NO)
						       ) AS YD_CARLD_WRK_BOOK_ID
						     , YD_CARUD_STOP_LOC  
						  FROM TB_YD_TCARSCH     TS
						     , TB_YD_TCARFTMVMTL TM
						 WHERE TS.YD_TCAR_SCH_ID = TM.YD_TCAR_SCH_ID
						   AND TM.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
						   AND TM.DEL_YN         = 'N'
						   -- 하차작업 예약 만들어 진거 제외 
						   AND TM.STL_NO  NOT IN ( SELECT STL_NO 
						                             FROM TB_YD_WRKBOOK A
						                                , TB_YD_WRKBOOKMTL B
						                            WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID    
						                              AND A.DEL_YN = 'N'
						                              AND B.DEL_YN = 'N' 
						                              AND A.YD_SCH_CD LIKE TS.YD_CARUD_STOP_LOC||'L'||'%')
						 ORDER BY SORT_SEQ
						*/
						JDTORecordSet jsDnTc = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getY5YDL009WbTCarIns", logId, mthdNm, "권하대상재!!!");
						if (jsDnTc.size() > 0) {
							JDTORecord jrParamTc = commUtils.getParam(logId, mthdNm, sModifier);
							JDTORecord jrDnTc 	 = JDTORecordFactory.getInstance().create();
							
							for (int nIdx = 0; nIdx < jsDnTc.size(); nIdx++) {
								
								jrParamTc = commUtils.getParam(logId, mthdNm, sModifier);
								//대차스케줄 처리 (영대차출발지시)
								//야드하차작업예약ID 생성
								String ydCarudWrkBookId = coilDao.getSeqId(logId, mthdNm, "WrkBook");
								jrDnTc = JDTORecordFactory.getInstance().create();
								
								if (nIdx == 0) {
									ydTcWrkBookId = ydCarudWrkBookId;
								}
								
								jrDnTc = jsDnTc.getRecord(nIdx);
								//하차지 작업예약 등록
								jrParamTc.setField("YD_CARUD_WRK_BOOK_ID", ydCarudWrkBookId); //야드하차작업예약ID
								jrParamTc.setField("YD_SCH_ST_GP"        , ydDnWrkActGp    ); //야드스케쥴기동구분
								jrParamTc.setField("YD_CARUD_STOP_LOC"   , tcarUdStopLoc   ); 
								jrParamTc.setField("YD_WBOOK_ID"         , commUtils.trim(jrDnTc.getFieldString("YD_CARLD_WRK_BOOK_ID"))); 
								
								/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL009WbTCarIns
								MERGE INTO TB_YD_WRKBOOK WB USING (
								SELECT :V_YD_CARUD_WRK_BOOK_ID AS YD_WBOOK_ID      --야드작업예약ID
								     , :V_MODIFIER             AS MODIFIER         --수정자
								     , SYSDATE                 AS MOD_DDTT         --수정일시
								     , 'N'                     AS DEL_YN           --삭제유무
								     , WB.YD_GP                                    --야드구분
								     , WB.YD_BAY_GP                                --야드동구분
								     , WB.YD_SCH_CD                                --야드스케쥴코드
								     , (SELECT SR.YD_WRK_CRN_PRIOR
								          FROM TB_YD_SCHRULE SR
								         WHERE SR.YD_SCH_CD = WB.YD_SCH_CD)
								                               AS YD_SCH_PRIOR     --야드스케쥴우선순위
								     , 'W'                     AS YD_SCH_PROG_STAT --야드스케쥴진행상태(스케줄수행대기)
								     , :V_YD_SCH_ST_GP         AS YD_SCH_ST_GP     --야드스케쥴기동구분
								     , '1'                     AS YD_SCH_REQ_GP    --야드스케쥴요청구분(대차상차완료)
								     , WB.YD_TO_LOC_DCSN_MTD                       --야드To위치결정방법
								     , WB.YD_TO_LOC_GUIDE                          --야드To위치Guide
								     , WB.YD_WRK_PLAN_TCAR                         --야드작업계획대차
								  FROM (SELECT WB.YD_GP
								             , SUBSTR(:V_YD_CARUD_STOP_LOC,2,1)         AS YD_BAY_GP
								--             , :V_YD_CARUD_STOP_LOC||'LM'               AS YD_SCH_CD
								             , :V_YD_CARUD_STOP_LOC||'L'|| DECODE(SUBSTR(WB.YD_SCH_CD, 8, 1), 'H', 'H','M') AS YD_SCH_CD

								             , CASE WHEN WB.YD_TO_LOC_DCSN_MTD = 'F' AND WB.YD_AIM_BAY_GP != SUBSTR(WB.YD_TO_LOC_GUIDE,2,1)
								                    THEN 'S' ELSE WB.YD_TO_LOC_DCSN_MTD END
								               AS YD_TO_LOC_DCSN_MTD
								             , CASE WHEN WB.YD_TO_LOC_DCSN_MTD = 'F' AND WB.YD_AIM_BAY_GP != SUBSTR(WB.YD_TO_LOC_GUIDE,2,1)
								                    THEN ''  ELSE WB.YD_TO_LOC_GUIDE    END
								               AS YD_TO_LOC_GUIDE
								             , WB.YD_WRK_PLAN_TCAR
								          FROM TB_YD_WRKBOOK WB
								         WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID) WB
								) DD ON (WB.YD_WBOOK_ID = DD.YD_WBOOK_ID)
								WHEN NOT MATCHED THEN
								INSERT (WB.YD_WBOOK_ID       , WB.REGISTER       , WB.REG_DDTT        , WB.MODIFIER    , WB.MOD_DDTT     ,
								        WB.DEL_YN            , WB.YD_GP          , WB.YD_BAY_GP       , WB.YD_SCH_CD   , WB.YD_SCH_PRIOR ,
								        WB.YD_SCH_PROG_STAT  , WB.YD_SCH_ST_GP   , WB.YD_SCH_REQ_GP   , WB.YD_AIM_YD_GP, WB.YD_AIM_BAY_GP,
								        WB.YD_TO_LOC_DCSN_MTD, WB.YD_TO_LOC_GUIDE, WB.YD_WRK_PLAN_TCAR)
								VALUES (DD.YD_WBOOK_ID       , DD.MODIFIER       , DD.MOD_DDTT        , DD.MODIFIER    , DD.MOD_DDTT     ,
								        DD.DEL_YN            , DD.YD_GP          , DD.YD_BAY_GP       , DD.YD_SCH_CD   , DD.YD_SCH_PRIOR ,
								        DD.YD_SCH_PROG_STAT  , DD.YD_SCH_ST_GP   , DD.YD_SCH_REQ_GP   , DD.YD_GP       , DD.YD_BAY_GP    ,
								        DD.YD_TO_LOC_DCSN_MTD, DD.YD_TO_LOC_GUIDE, DD.YD_WRK_PLAN_TCAR)
								*/
								commDao.update(jrParamTc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL009WbTCarIns", logId, mthdNm, "작업예약 등록");
							
		
								//작업예약재료 등록
								/* 
								INSERT INTO TB_YD_WRKBOOKMTL WM
								       (WM.YD_WBOOK_ID          , WM.STL_NO         , WM.REGISTER       , WM.REG_DDTT    ,
								        WM.MODIFIER             , WM.MOD_DDTT       , WM.DEL_YN         , WM.YD_STK_COL_GP,
								        WM.YD_STK_BED_NO        , WM.YD_STK_LYR_NO  , WM.YD_UP_COLL_SEQ)
								VALUES (:V_YD_CARUD_WRK_BOOK_ID , :V_STL_NO         , :V_MODIFIER       , SYSDATE        ,
								        :V_MODIFIER             , SYSDATE           , 'N'               , :V_YD_STK_COL_GP,
								        :V_YD_STK_BED_NO        , :V_YD_STK_LYR_NO  , :V_YD_UP_COLL_SEQ)        
								*/        
								jrParamTc.setField("STL_NO" 		, commUtils.trim(jrDnTc.getFieldString("STL_NO"))); 
								jrParamTc.setField("YD_STK_COL_GP" 	, commUtils.trim(jrDnTc.getFieldString("YD_STK_COL_GP"))); 
								jrParamTc.setField("YD_STK_BED_NO" 	, commUtils.trim(jrDnTc.getFieldString("YD_STK_BED_NO"))); 
								jrParamTc.setField("YD_STK_LYR_NO"  , commUtils.trim(jrDnTc.getFieldString("YD_STK_LYR_NO"))); 
								jrParamTc.setField("YD_UP_COLL_SEQ" , commUtils.trim(jrDnTc.getFieldString("YD_UP_COLL_SEQ"))); 
								
								
								commDao.update(jrParamTc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insY5YDL009WbMtlTCarIns", logId, mthdNm, "작업예약재료 등록");							        
							}
						}
					}
					
					jrParam.setField("YD_CARUD_WRK_BOOK_ID", ydTcWrkBookId); //야드하차작업예약ID
					//대차스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
					/* 
					MERGE INTO TB_YD_TCARSCH TS USING (
					SELECT TM.YD_TCAR_SCH_ID
					     , :V_MODIFIER              AS MODIFIER
					     , :V_YD_CAR_PROG_STAT      AS YD_CAR_PROG_STAT
					     , TM.YD_EQP_WRK_SH
					     , TM.YD_EQP_WRK_WT
					     , :V_YD_WBOOK_ID           AS YD_CARLD_WRK_BOOK_ID
					     , :V_YD_STK_COL_GP         AS YD_CARLD_STOP_LOC
					     , :V_YD_CARLD_WRK_CRN      AS YD_CARLD_WRK_CRN
					     , NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE) AS WR_DT
					     , WB.YD_WBOOK_ID           AS YD_CARUD_WRK_BOOK_ID
					     , NVL(:V_YD_CARUD_STOP_LOC,YD_CARUD_STOP_LOC)     AS YD_CARUD_STOP_LOC 
					  FROM TB_YD_WRKBOOK WB
					      ,(SELECT TM.YD_TCAR_SCH_ID
					             , YD_CARUD_STOP_LOC       AS YD_CARUD_STOP_LOC 
					             , COUNT(*)                AS YD_EQP_WRK_SH
					             , SUM(ST.COIL_WT)         AS YD_EQP_WRK_WT
					             , :V_YD_CARUD_WRK_BOOK_ID AS YD_CARUD_WRK_BOOK_ID
					          FROM TB_YD_TCARSCH     TS
					             , TB_YD_TCARFTMVMTL TM
					             , TB_PT_COILCOMM    ST
					         WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
					           AND TM.STL_NO         = ST.COIL_NO
					           AND TM.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
					           AND TM.DEL_YN         = 'N'
					         GROUP BY TM.YD_TCAR_SCH_ID , YD_CARUD_STOP_LOC ) TM
					  WHERE TM.YD_CARUD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
					) DD ON (TS.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID)
					WHEN MATCHED THEN UPDATE SET
					     TS.MODIFIER             = DD.MODIFIER
					    ,TS.MOD_DDTT             = SYSDATE
					    ,TS.YD_EQP_WRK_STAT      = 'L'                      --야드설비작업상태 :영차
					    ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT      --야드차량진행상태 
					    ,TS.YD_EQP_WRK_SH        = DD.YD_EQP_WRK_SH         --야드설비작업매수
					    ,TS.YD_EQP_WRK_WT        = DD.YD_EQP_WRK_WT         --야드설비작업중량
					    ,TS.YD_CARLD_WRK_BOOK_ID = DD.YD_CARLD_WRK_BOOK_ID  --야드상차작업예약ID
					    ,TS.YD_CARLD_STOP_LOC    = DD.YD_CARLD_STOP_LOC     --야드상차정지위치
					    ,TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT,DD.WR_DT)  --야드상차개시일시
					    ,TS.YD_CARLD_CMPL_DT     = DECODE(DD.YD_CAR_PROG_STAT,'5',DD.WR_DT,NULL)  --야드상차완료일시
					    ,TS.YD_CARLD_WRK_CRN     = DD.YD_CARLD_WRK_CRN      --야드상차작업크레인 
					    ,TS.YD_CARUD_WRK_BOOK_ID = DD.YD_CARUD_WRK_BOOK_ID  --야드하차작업예약ID 
					    ,TS.YD_CARUD_STOP_LOC    = DD.YD_CARUD_STOP_LOC     --야드하차정지위치
					 */	    
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL009TcarSchLd", logId, mthdNm, "대차스케줄 수정");
					
					//L2 영대차출발지시:권하완료전문송신후 출발지시송신처리로 변경
					if ("Y".equals(tcarLdCmplYn)) {
						jrTcSnd.setField("YD_TCAR_SCH_ID" 	, commUtils.trim(jrTc.getFieldString("YD_TCAR_SCH_ID")));
 					}						
					
				}
			}
			/**********************************************************
			* 7. 차량
			**********************************************************/
			String sDnCarFlag = ""; //차량 상차 플레그 ( 다음작업요구시 사용 )
			
			// PIDEV
			commUtils.printLog(logId, "7. 차량 - bCarMvYn : " + bCarMvYn + " ydDnWrLoc : " + ydDnWrLoc, "SL");
			
			if (!bCarMvYn) { //차량동간이적이 아니면
				
				JDTORecord jrParamCar = commUtils.getParam(logId, mthdNm, sModifier);
				
				/*************************************************
				 * 7.1 권하위치 차량
				 *   - 구내운송:이송상차완료
				 *   - 제품출하: 코일제품고간이송상하차완료
				 *************************************************/
				if ("PT".equals(ydDnWrLoc.substring(2, 4)) || "TR".equals(ydDnWrLoc.substring(2, 4))) {
					commUtils.printLog(logId, "권하위치 차량 : " + ydSchCd.substring(2 , 4), "SL");

					//상차 작업 예약 ID	Setting
					jrParamCar.setField("YD_CRN_SCH_ID"       , ydCrnSchId  );
					jrParamCar.setField("YD_STK_COL_GP"       , ydDnWrLoc.substring(0, 6));
					/*
					SELECT YD_CAR_SCH_ID
					     , YD_CAR_USE_GP 
					     , CAR_NO
					     , TRANS_ORD_DATE
					     , TRANS_ORD_SEQNO
					     , DEST_TEL_NO
					     , YD_STK_COL_GP
					     , WLOC_CD
					     , YD_PNT_CD
					     , YD_CAR_PROG_STAT  -- 차량진행
					     , BAY_WRK_CNT       -- 해당상차도 작업대상
					     , BAY_WRK_END_CNT   -- 해당 상차도 작업완료
					     , CASE WHEN BAY_WRK_CNT <= BAY_WRK_END_CNT THEN 'Y' ELSE'N' END AS CAR_LD_CMPL_YN      --해당상차도 [1123]경기99사1123
					     , TRANS_EQUIPMENT_TYPE    
					     , CMBN_CARLD_YN 
					  FROM
					       (
					        SELECT TS.YD_CAR_SCH_ID
					             , TS.YD_CAR_USE_GP
					             , TS.CAR_NO
					             , TS.TRANS_ORD_DATE
					             , TS.TRANS_ORD_SEQNO
					             , TS.DEST_TEL_NO
					             , TS.YD_CAR_PROG_STAT
					             , TS.TRANS_EQUIPMENT_TYPE
					             , TS.CMBN_CARLD_YN
					             , SC.YD_STK_COL_GP
					             , SC.WLOC_CD
					             , SC.YD_PNT_CD
					             -- 해당상차도 작업대상 건수   
					             , CASE WHEN WB.YD_CAR_USE_GP = 'L' THEN 
					                    -- 구내운송
					                         (SELECT COUNT(DISTINCT(A.STL_NO)) 
					                            FROM TB_YD_STOCK  A
					                               , TB_YD_STKLYR B 
					                           WHERE A.STL_NO               = B.STL_NO
					                             AND A.CAR_FRTOMOVE_WORD_NO = TS.FRTOMOVE_WORD_NO
					                             AND SUBSTR(B.YD_STK_COL_GP,1,2) = SUBSTR(SC.YD_STK_COL_GP,1,2))
					                             
					                    ELSE  
					                     -- 출하
					                        (SELECT COUNT(DISTINCT(B.STL_NO)) 
					                           FROM TB_YD_STOCK    B
					                              , TB_YD_CARPOINT C
					                          WHERE B.TRANS_ORD_DATE  = TS.TRANS_ORD_DATE
					                            AND B.TRANS_ORD_SEQNO = TS.TRANS_ORD_SEQNO
					                            AND C.YD_STK_COL_GP = SC.YD_STK_COL_GP)
					                      
					                     END  
					               AS BAY_WRK_CNT    
					               
					             -- 해당상차도 작업완료 건수                 
					             , CASE WHEN WB.YD_CAR_USE_GP = 'L' THEN 
					                    -- 구내운송
					                         (SELECT COUNT(DISTINCT(A.STL_NO)) + 1
					                            FROM TB_YD_STKLYR A
					                           WHERE ((SUBSTR(A.YD_STK_COL_GP,3,2) IN ('PT') AND YD_STK_LYR_MTL_STAT = 'C'))
					                             AND SUBSTR(A.YD_STK_COL_GP,1,2) = SUBSTR(SC.YD_STK_COL_GP,1,2)
					                             AND A.STL_NO IN (SELECT B.STL_NO
					                                                FROM TB_YD_STOCK B
					                                               WHERE B.CAR_FRTOMOVE_WORD_NO = TS.FRTOMOVE_WORD_NO))              
					                    ELSE 
					                    -- 출하
					                         (SELECT COUNT(DISTINCT(A.COIL_NO)) + 1
					                            FROM TB_PT_COILCOMM A
					                           WHERE SUBSTR(A.YD_STR_LOC,3,2) IN ('PT') 
					                             AND A.COIL_NO  IN (SELECT B.STL_NO
					                                                  FROM TB_YD_STOCK    B
					                                                     , TB_YD_CARPOINT C
					                                                 WHERE B.TRANS_ORD_DATE  = TS.TRANS_ORD_DATE
					                                                   AND B.TRANS_ORD_SEQNO = TS.TRANS_ORD_SEQNO
					                                                   AND C.YD_STK_COL_GP = SC.YD_STK_COL_GP))   
					                    END  
					               AS BAY_WRK_END_CNT   
					          FROM TB_YD_STKCOL SC
					             , TB_YD_CARSCH TS
					             , (SELECT TRN_EQP_CD , CAR_NO , CARD_NO ,YD_CAR_USE_GP, SUBSTR(YD_DN_WO_LOC,1,6) AS YD_DN_WO_LOC
					                  FROM TB_YD_CRNSCH  A
					                     , TB_YD_WRKBOOK B
					                 WHERE A.YD_WBOOK_ID   = B.YD_WBOOK_ID
					                   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
					               ) WB
					         WHERE SC.YD_STK_COL_GP = WB.YD_DN_WO_LOC
					           AND SC.YD_STK_COL_GP = :V_YD_STK_COL_GP
					           AND ((WB.YD_CAR_USE_GP = 'L' AND WB.TRN_EQP_CD = SC.TRN_EQP_CD) --구내운송
					             OR (WB.YD_CAR_USE_GP = 'G' AND WB.CARD_NO    = SC.CARD_NO AND WB.CAR_NO=SC.CAR_NO)) --출하차량
					           AND ((WB.YD_CAR_USE_GP = 'L' AND WB.TRN_EQP_CD = TS.TRN_EQP_CD) --구내운송
					             OR (WB.YD_CAR_USE_GP = 'G' AND WB.CARD_NO    = TS.CARD_NO AND WB.CAR_NO=TS.CAR_NO)) --출하차량  
					           AND TS.DEL_YN = 'N'
					         ORDER BY  TS.YD_CAR_SCH_ID DESC  
					       ) 
					 WHERE ROWNUM<=1  
					 */
					//PIDEV_S :병행가동용:PI_YD
					jrParamCar.setField("PI_YD",    	sPI_YD);							
					JDTORecordSet jsCarWrkbook = commDao.select(jrParamCar, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarSchLd_PIDEV", logId, mthdNm, "차량스케줄 조회");
					
					if (jsCarWrkbook.size() > 0) {
						String ydCarSchId  = commUtils.trim(jsCarWrkbook.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
				    	
				    	jrParamCar.setField("YD_CAR_SCH_ID"    , ydCarSchId    );
				    	jrParamCar.setField("YD_WBOOK_ID"      , ydWbookId     );
				    	jrParamCar.setField("YD_DN_WR_LOC"     , ydDnWrLoc     );
				    	jrParamCar.setField("YD_CRN_SCH_ID"    , ydCrnSchId    );
				    	jrParamCar.setField("YD_STK_COL_GP"    , ydDnWrStkColGp);
				    	jrParamCar.setField("WR_DT"            , currDt        );
				    	/*
						INSERT INTO TB_YD_CARFTMVMTL (
						       YD_CAR_SCH_ID
						     , STL_NO   
						     , REGISTER     
						     , REG_DDTT     
						     , MODIFIER     
						     , MOD_DDTT 
						     , DEL_YN       
						     , YD_STK_BED_NO
						     , YD_STK_LYR_NO
						     , HCR_GP
						     , STL_PROG_CD
						     , YD_MTL_ITEM
						     , YD_ROUTE_GP
						)
						SELECT :V_YD_CAR_SCH_ID
						     , CM.STL_NO
						     , :V_MODIFIER     
						     , SYSDATE         
						     , :V_MODIFIER     
						     , SYSDATE         
						     , 'N'             
						     , NVL(SUBSTR(NVL(:V_YD_DN_WR_LOC, YD_DN_WO_LOC),-2),'01') AS YD_STK_BED_NO
						     , '001' AS YD_STK_LYR_NO
						     , CM.HCR_GP
						     , CM.STL_PROG_CD
						     , CM.YD_MTL_ITEM
						     , CM.YD_ROUTE_GP
						  FROM TB_YD_CRNWRKMTL CM
						     , TB_YD_STOCK     ST
						     , TB_YD_CRNSCH    CR
						 WHERE CM.STL_NO        = ST.STL_NO
						   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						   AND CM.YD_CRN_SCH_ID = CR.YD_CRN_SCH_ID
				    	 */
				    	commDao.insert(jrParamCar, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insY5YDL009CarMtl", logId, mthdNm, "차량이송재료 등록");
				    	
				    	/*
						UPDATE TB_YD_CARSCH TS
						   SET TS.MODIFIER             = :V_MODIFIER
						     , TS.MOD_DDTT             = SYSDATE
						     , TS.YD_EQP_WRK_STAT      = 'L' --영차
						     , TS.YD_EQP_WRK_SH        = (SELECT COUNT(*) 
						                                    FROM TB_YD_CARFTMVMTL 
						                                   WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID)
						     , TS.YD_EQP_WRK_WT        = (SELECT SUM(COIL_WT) 
						                                    FROM TB_YD_CARFTMVMTL A
						                                       , TB_PT_COILCOMM   B
						                                   WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
						                                     AND A.STL_NO        = B.COIL_NO
						                                   
						                                   )
						     , TS.YD_PNT_CD3           = '0000'
						     , TS.YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
						     , TS.YD_CARLD_STOP_LOC    = :V_YD_STK_COL_GP
						     , TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT,NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE))
						     , TS.YD_CARLD_CMPL_DT     = DECODE(:V_YD_CAR_PROG_STAT,'5',NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE),NULL)
						WHERE TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
				    	 */
				    	commDao.update(jrParamCar, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL009CarSchLd", logId, mthdNm, "차량스케줄 수정");
					}
					
					//차량 작업 진행관리 호출
					JDTORecord jrCarMgt = commUtils.getParam(logId, mthdNm, sModifier);
	            	jrCarMgt.setField("YD_CRN_SCH_ID" , ydCrnSchId  );
	            	jrCarMgt.setField("YD_WBOOK_ID"   , ydWbookId   );
	            	jrCarMgt.setField("CAR_LDUD_GP"   , "U"         ); //U 권하위치 차량 L 권상위치 차량
	            	jrCarMgt.setField("YD_DN_WR_LOC"  , ydDnWrLoc   );
	            	jrCarMgt.setField("YD_UP_WR_LOC"  , ydUpWrLoc   );
	            	jrCarMgt.setField("STL_NO"        , sStlNo      );
	            	jrCarMgt.setField("STL_APPEAR_GP" , sStlAppearGp); //재료외형구분 E;소재 , Y:제품
	            	jrCarMgt.setField("YD_SCH_CD"     , ydSchCd     );
//PIDEV_S :병행가동용:PI_YD
	            	jrCarMgt.setField("PI_YD",    	sPI_YD);	
	            	/* *********************
	            	 * 차량 작업진행관리
	            	 * *********************/
	            	JDTORecord jrProc = this.procY5CarWrkStatCtrCoil(jrCarMgt);
					jrRtn = commUtils.addSndData(jrRtn, jrProc);
	            	
					jrParam.setField("YD_CAR_SCH_ID", jrProc.getFieldString("YD_CAR_SCH_ID"));
					
	            	//차량 상차 플레그 ( 다음작업요구시 사용 )
	            	if ("PT".equals(ydDnWrLoc.substring(2, 4))) {
	            		sDnCarFlag = "Y";
	            	}
				}
				
				/********************************************************************
				 * 7.2 권상실적 위치 차량
				 * - 구내운송:이송하차완료 , 제품출하: 코일제품고간이송상하차완료
				 ********************************************************************/
				if ("PT".equals(ydUpWrLoc.substring(2, 4)) || "TR".equals(ydUpWrLoc.substring(2, 4))) {
					commUtils.printLog(logId, "권상위치 차량 : " + ydSchCd.substring(2 , 4), "SL");
					
					//차량하차 작업일 경우 진도코드 갱신
					//코일소재일 경우에만 공통테이블 갱신
					if ("H".equals(ydLocGp)) {
						jrParamCar.setField("COIL_NO", sStlNo);
						/*
						UPDATE USRPTA.TB_PT_COILCOMM A
						   SET BEFOBEFO_PROG_CD         = (CASE WHEN CURR_PROG_CD ='C' THEN BEFOBEFO_PROG_CD         ELSE BEFO_PROG_CD END)
						     , BEFOBEFO_PROG_REG_DDTT   = (CASE WHEN CURR_PROG_CD ='C' THEN BEFOBEFO_PROG_REG_DDTT   ELSE BEFO_PROG_REG_DDTT END)
						     , BEFOBEFO_PROG_CD_REG_PGM = (CASE WHEN CURR_PROG_CD ='C' THEN BEFOBEFO_PROG_CD_REG_PGM ELSE BEFO_PROG_CD_REG_PGM END)
						     , BEFO_PROG_CD             = (CASE WHEN CURR_PROG_CD ='C' THEN BEFO_PROG_CD             ELSE CURR_PROG_CD END)
						     , BEFO_PROG_REG_DDTT       = (CASE WHEN CURR_PROG_CD ='C' THEN BEFO_PROG_REG_DDTT       ELSE CURR_PROG_REG_DDTT END)
						     , BEFO_PROG_CD_REG_PGM     = (CASE WHEN CURR_PROG_CD ='C' THEN BEFO_PROG_CD_REG_PGM     ELSE CURR_PROG_CD_REG_PGM END)
						     , STL_APPEAR_GP            = (CASE WHEN CURR_PROG_CD ='C' THEN STL_APPEAR_GP            ELSE 'E' END)
						     , GOODS_INLINE_GP          = (CASE WHEN CURR_PROG_CD ='C' THEN GOODS_INLINE_GP          ELSE 'M' END)
						     , CURR_PROG_CD             = (CASE WHEN CURR_PROG_CD ='C' THEN CURR_PROG_CD             
						                                        ELSE CASE WHEN CURR_PROG_CD != 'F' THEN DECODE(ORD_YEOJAE_GP, '1', 'B', 'Y')
						                                                  ELSE CURR_PROG_CD END --보류재인 경우 진도변경을 안함
						                                    END)
						     , CURR_PROG_REG_DDTT       = (CASE WHEN CURR_PROG_CD ='C' THEN CURR_PROG_REG_DDTT       ELSE SYSDATE END )
						     , CURR_PROG_CD_REG_PGM     = (CASE WHEN CURR_PROG_CD ='C' THEN CURR_PROG_CD_REG_PGM     ELSE 'rcvY5YDL009' END)
						     , FNL_REG_PGM              = (CASE WHEN CURR_PROG_CD ='C' THEN FNL_REG_PGM              ELSE 'rcvY5YDL009' END)
						     , MODIFIER                 = (CASE WHEN CURR_PROG_CD ='C' THEN MODIFIER                 ELSE :V_MODIFIER END)
						     , MOD_DDTT                 = (CASE WHEN CURR_PROG_CD ='C' THEN MOD_DDTT                 ELSE SYSDATE END )
						     , NEXT_PROC                = (CASE WHEN CURR_PROG_CD != 'F' 
						                                         AND ORD_YEOJAE_GP = '1' --B
						                                         AND NEXT_PROC IS NULL THEN (SELECT WO_CAR_PLNT_PROC_CD 
						                                                                       FROM USRYDA.TB_YD_STOCK
						                                                                      WHERE STL_NO = A.COIL_NO)
						                                        ELSE NEXT_PROC END)
						 WHERE COIL_NO = :V_COIL_NO
						 */
						commDao.updateTx(jrParamCar, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updPtCoilcommPROGCD", logId, mthdNm, "진도코드 갱신");
					}
					
					//차량 작업 진행관리 호출(하차)
					JDTORecord jrCarMgt = commUtils.getParam(logId, mthdNm, sModifier);
	            	jrCarMgt.setField("YD_CRN_SCH_ID" , ydCrnSchId  );
	            	jrCarMgt.setField("YD_WBOOK_ID"   , ydWbookId   );
	            	jrCarMgt.setField("CAR_LDUD_GP"   , "L"         ); //U 권하위치 차량 L 권상위치 차량
	            	jrCarMgt.setField("YD_DN_WR_LOC"  , ydDnWrLoc   );
	            	jrCarMgt.setField("YD_UP_WR_LOC"  , ydUpWrLoc   );
	            	jrCarMgt.setField("STL_NO"        , sStlNo      );
	            	jrCarMgt.setField("STL_APPEAR_GP" , sStlAppearGp); //재료외형구분 E;소재 , Y:제품
	            	jrCarMgt.setField("YD_SCH_CD"     , ydSchCd     );
	            	//PIDEV_S :병행가동용:PI_YD
	            	jrCarMgt.setField("PI_YD",    	sPI_YD);		            	
	            	/* *********************
	            	 * 차량 작업진행관리
	            	 * *********************/
	            	JDTORecord jrProc = this.procY5CarWrkStatCtrCoil(jrCarMgt);
					jrRtn = commUtils.addSndData(jrRtn, jrProc);
	            	
					jrParam.setField("YD_CAR_SCH_ID", jrProc.getFieldString("YD_CAR_SCH_ID"));
				}
			}

			commUtils.printLog(logId, "반납/반송 CHECK_야드구분["+ ydLocGp +"] 스케쥴코드["+ ydSchCd +"] 진도코드["+ sCurrProgCd +"]", "SL");
			//반납/반송인 경우 진도를 바꾼다 J -> B
			if ( "H".equals(ydLocGp)
			&&	( "JAYD03UH".equals(ydSchCd)
				||"JBYD03UH".equals(ydSchCd) 
				||"JBYD53UH".equals(ydSchCd) 
				||"JCYD03UH".equals(ydSchCd) 
				||"JCYD53UH".equals(ydSchCd) 
				||"JEYD03UH".equals(ydSchCd) 
				||"JFYD03UH".equals(ydSchCd) 
				||"JGYD03UH".equals(ydSchCd) 
				||"JHYD03UH".equals(ydSchCd) 
//				||"J".equals(ydSchCd.substring(6, 7)) //기존 로직 ydSchCd.substring(6, 8).equals("UJ")
				||"JAYD04UH".equals(ydSchCd)
				||"JBYD04UH".equals(ydSchCd)
				||"JBYD54UH".equals(ydSchCd)
				||"JCYD04UH".equals(ydSchCd)
				||"JCYD54UH".equals(ydSchCd)
				||"JDYD04UH".equals(ydSchCd)
				||"JEYD04UH".equals(ydSchCd)
				||"JFYD04UH".equals(ydSchCd)
				||"JGYD04UH".equals(ydSchCd)
				||"JHYD04UH".equals(ydSchCd)
				||"B".equals(ydSchCd.substring(6, 7))//"UB".substring(6, 8).equals(ydSchCd) 
				) 
				|| //소재야드에서 차량동간이적으로 직접 반품을 받는 경우
				("TR1".equals(ydSchCd.substring(2, 5)) && "H".equals(ydLocGp) && ("J".equals(sCurrProgCd)|| "5".equals(sCurrProgCd)))
			) {
		        jrParam.setField("COIL_NO", sStlNo);
				/*
				UPDATE USRPTA.TB_PT_COILCOMM A
				   SET BEFOBEFO_PROG_CD         = (CASE WHEN CURR_PROG_CD ='C' THEN BEFOBEFO_PROG_CD         ELSE BEFO_PROG_CD END)
				     , BEFOBEFO_PROG_REG_DDTT   = (CASE WHEN CURR_PROG_CD ='C' THEN BEFOBEFO_PROG_REG_DDTT   ELSE BEFO_PROG_REG_DDTT END)
				     , BEFOBEFO_PROG_CD_REG_PGM = (CASE WHEN CURR_PROG_CD ='C' THEN BEFOBEFO_PROG_CD_REG_PGM ELSE BEFO_PROG_CD_REG_PGM END)
				     , BEFO_PROG_CD             = (CASE WHEN CURR_PROG_CD ='C' THEN BEFO_PROG_CD             ELSE CURR_PROG_CD END)
				     , BEFO_PROG_REG_DDTT       = (CASE WHEN CURR_PROG_CD ='C' THEN BEFO_PROG_REG_DDTT       ELSE CURR_PROG_REG_DDTT END)
				     , BEFO_PROG_CD_REG_PGM     = (CASE WHEN CURR_PROG_CD ='C' THEN BEFO_PROG_CD_REG_PGM     ELSE CURR_PROG_CD_REG_PGM END)
				     , STL_APPEAR_GP            = (CASE WHEN CURR_PROG_CD ='C' THEN STL_APPEAR_GP            ELSE 'E' END)
				     , GOODS_INLINE_GP          = (CASE WHEN CURR_PROG_CD ='C' THEN GOODS_INLINE_GP          ELSE 'M' END)
				     , CURR_PROG_CD             = (CASE WHEN CURR_PROG_CD ='C' THEN CURR_PROG_CD             
				                                        ELSE CASE WHEN CURR_PROG_CD != 'F' THEN DECODE(ORD_YEOJAE_GP, '1', 'B', 'Y')
				                                                  ELSE CURR_PROG_CD END --보류재인 경우 진도변경을 안함
				                                    END)
				     , CURR_PROG_REG_DDTT       = (CASE WHEN CURR_PROG_CD ='C' THEN CURR_PROG_REG_DDTT       ELSE SYSDATE END )
				     , CURR_PROG_CD_REG_PGM     = (CASE WHEN CURR_PROG_CD ='C' THEN CURR_PROG_CD_REG_PGM     ELSE 'rcvY5YDL009' END)
				     , FNL_REG_PGM              = (CASE WHEN CURR_PROG_CD ='C' THEN FNL_REG_PGM              ELSE 'rcvY5YDL009' END)
				     , MODIFIER                 = (CASE WHEN CURR_PROG_CD ='C' THEN MODIFIER                 ELSE :V_MODIFIER END)
				     , MOD_DDTT                 = (CASE WHEN CURR_PROG_CD ='C' THEN MOD_DDTT                 ELSE SYSDATE END )
				     , NEXT_PROC                = (CASE WHEN CURR_PROG_CD != 'F' 
				                                         AND ORD_YEOJAE_GP = '1' --B
				                                         AND NEXT_PROC IS NULL THEN (SELECT WO_CAR_PLNT_PROC_CD 
				                                                                       FROM USRYDA.TB_YD_STOCK
				                                                                      WHERE STL_NO = A.COIL_NO)
				                                        ELSE NEXT_PROC END)
				 WHERE COIL_NO = :V_COIL_NO
				 */
		        /*
		         * 반품실적처리시에 진도코드 변경으로 적용
		         * 이젠 변경하지 않아도 됨 YYS 20230608
		         * 20231122 JGK 진행,물류 처리하는 곳이 없어 다시 원복 하기로 함.
		         */
				commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updPtCoilcommPROGCD", logId, mthdNm, "진도코드 갱신");
				
    	        /**********************************************************
     	        * 정정작업메세지이력등록 시작
     	        **********************************************************/
				jrParam.setField("STL_NO", sStlNo);
				/*
				SELECT STL_NO
				     , SNDBK_GP_ETC2
				     , SNDBK_REGISTER
				     , SNDBK_GP
				     , SNDBK_RSN_CD
				     , DECODE(SNDBK_GP,'N','[반납구분 : C열연실물반납] [반납기타Error : '||SNDBK_GP_ETC2||']', SNDBK_GP_ETC2 ) AS  SNDBK_GP_ETC
				  FROM (
				        SELECT STL_NO
				             , NVL(B.SNDBK_GP_ETC, (SELECT RETURN_ETC_ERR AS SNDBK_GP_ETC2
				                                      FROM TB_DM_GOODSRETURN A
				                                     WHERE GOODS_RETURN_STEP_NO = (SELECT MAX(GOODS_RETURN_STEP_NO)
				                                                                     FROM TB_DM_GOODSRETURN B
				                                                                    WHERE A.GOODS_NO=B.GOODS_NO)
				                                       AND A.GOODS_NO = B.STL_NO
				                                   )
				               ) AS SNDBK_GP_ETC2
				             , NVL(B.SNDBK_REGISTER, (SELECT REQUESTER AS REQUESTER
				                                        FROM TB_DM_GOODSRETURN A
				                                       WHERE GOODS_RETURN_STEP_NO = (SELECT MAX(GOODS_RETURN_STEP_NO)
				                                                                       FROM TB_DM_GOODSRETURN B
				                                                                      WHERE A.GOODS_NO = B.GOODS_NO)
				                                         AND A.GOODS_NO = B.STL_NO
				                                     )
				               ) AS SNDBK_REGISTER
				             , (CASE WHEN (SELECT CURR_PROG_CD FROM TB_PT_COILCOMM C WHERE C.COIL_NO = B.STL_NO ) = 'J' THEN 'N' ELSE 'S' END) AS SNDBK_GP
				             , SNDBK_RSN_CD
				          FROM TB_YD_STOCK B
				         WHERE STL_NO = :V_STL_NO
				       ) A
				 WHERE 1 = 1 
				 */
				JDTORecordSet jsSndbk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStockSndBk_PIDEV", logId, mthdNm, "반납정보 조회");
				
				if (jsSndbk.size() > 0) {
				
					JDTORecord rcvMsgArgs = JDTORecordFactory.getInstance().create();
	     	        rcvMsgArgs.setField("COIL_NO"         , sStlNo);
	     	        rcvMsgArgs.setField("SHEAR_WRK_MSG_GP", jsSndbk.getRecord(0).getFieldString("SNDBK_GP"));
	     	        rcvMsgArgs.setField("MSG_CONTENTS"    , jsSndbk.getRecord(0).getFieldString("SNDBK_GP_ETC"));
	     	        rcvMsgArgs.setField("userid"          , jsSndbk.getRecord(0).getFieldString("SNDBK_REGISTER"));
	     	        EJBConnector ejbConn2 = new EJBConnector("hsteelApp", "HrCommMgtFaEJB", this);
	     	        ejbConn2.trx("insHrShrMsgLog", new Class[] { JDTORecord.class }, new Object[] { rcvMsgArgs });
				}
			}
			
			/**********************************************************
			* 9. 조업실적 송신
			**********************************************************/
			
			JDTORecord jrParamSnd = commUtils.getParam(logId, mthdNm, sModifier);
	    	jrParamSnd.setField("YD_DN_WR_LOC"    , ydDnWrLoc); 
	    	jrParamSnd.setField("YD_SCH_CD"       , ydSchCd);
	    	/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdDnSendYN
	    	WITH TEMP_TBL AS (
	    	SELECT SUBSTR(:V_YD_DN_WR_LOC,1,6) AS YD_DN_WR_LOC
	    	     , :V_YD_SCH_CD                AS YD_SCH_CD
	    	  FROM DUAL
	    	)
	    	SELECT CASE WHEN YD_DN_WR_LOC = 'JAKE05' AND  YD_SCH_CD = 'JAKE01UH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JAKE05' AND  YD_SCH_CD = 'JAKD02LH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JAKD05' AND  YD_SCH_CD = 'JAKD01UH' THEN 'Y'

	    	            WHEN YD_DN_WR_LOC = 'JBKE04' AND  YD_SCH_CD = 'JBKE01UH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JBKE04' AND  YD_SCH_CD = 'JBKD02LH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JBKD04' AND  YD_SCH_CD = 'JBKD01UH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JBFE05' AND  YD_SCH_CD = 'JBFE01UH' THEN 'Y'

	    	            WHEN YD_DN_WR_LOC = 'JCKE03' AND  YD_SCH_CD = 'JCKE01UH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JCKE03' AND  YD_SCH_CD = 'JCKD02LH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JCKD03' AND  YD_SCH_CD = 'JCKD01UH' THEN 'Y'

	    	            WHEN YD_DN_WR_LOC = 'JCFE04' AND  YD_SCH_CD = 'JCFE01UH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JCFE04' AND  YD_SCH_CD = 'JCFD02LH' THEN 'Y'

	    	            WHEN YD_DN_WR_LOC = 'JDFE03' AND  YD_SCH_CD = 'JDFE01UH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JDCD01' AND  YD_SCH_CD = 'JDYD01MH' THEN 'Y' -- 동내이적삭제예정

	    	            WHEN YD_DN_WR_LOC = 'JEKE02' AND  YD_SCH_CD = 'JEKE01UH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JEKE02' AND  YD_SCH_CD = 'JEKD02LH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JEKD02' AND  YD_SCH_CD = 'JEKD01UH' THEN 'Y'

	    	            WHEN YD_DN_WR_LOC = 'JFFE02' AND  YD_SCH_CD = 'JFFE01UH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JFCD01' AND  YD_SCH_CD = 'JFYD01MH' THEN 'Y' -- 동내이적삭제예정

	    	            -- 크래들롤 보급
	    	            WHEN YD_DN_WR_LOC = 'JFCD01' AND  YD_SCH_CD = 'JFCD01UH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JDCD01' AND  YD_SCH_CD = 'JDCD01UH' THEN 'Y'
	    	            -- 크래들롤 추출
	    	            WHEN YD_DN_WR_LOC = 'JFFE02' AND  YD_SCH_CD = 'JFCD01LH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JDFE03' AND  YD_SCH_CD = 'JDCD01LH' THEN 'Y'

	    	            WHEN YD_DN_WR_LOC = 'JGFE01' AND  YD_SCH_CD = 'JGFE01UH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JGFE01' AND  YD_SCH_CD = 'JGFD02LH' THEN 'Y'

	    	            WHEN YD_DN_WR_LOC = 'JHKE01' AND  YD_SCH_CD = 'JHKE01UH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JHKE01' AND  YD_SCH_CD = 'JHKD02LH' THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JHKD01' AND  YD_SCH_CD = 'JHKD01UH' THEN 'Y'

	    	            WHEN YD_DN_WR_LOC = 'JBGF01' AND (SUBSTR(YD_SCH_CD,1,6) = 'JBGF01' OR SUBSTR(YD_SCH_CD,3,2) = 'TC') THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JCGF01' AND (SUBSTR(YD_SCH_CD,1,6) = 'JCGF01' OR SUBSTR(YD_SCH_CD,3,2) = 'TC') THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JEGF01' AND (SUBSTR(YD_SCH_CD,1,6) = 'JEGF01' OR SUBSTR(YD_SCH_CD,3,2) = 'TC') THEN 'Y'
	    	            WHEN YD_DN_WR_LOC = 'JHGF01' AND (SUBSTR(YD_SCH_CD,1,6) = 'JHGF01' OR SUBSTR(YD_SCH_CD,3,2) = 'TC') THEN 'Y'
	    	            ELSE 'N' END AS HR_LINE_IN_SND_FLAG

	    	     , CASE WHEN YD_DN_WR_LOC = 'JHKE01' AND  YD_SCH_CD = 'JHKE01UH' THEN 'YDH2L001'
	    	            WHEN YD_DN_WR_LOC = 'JHKD01' AND  YD_SCH_CD = 'JHKD01UH' THEN 'YDH2L001'
	    	            WHEN YD_DN_WR_LOC = 'JHKE01' AND  YD_SCH_CD = 'JHKD02LH' THEN 'YDH2L001'

	    	            WHEN YD_DN_WR_LOC = 'JGFE01' AND  YD_SCH_CD = 'JGFE01UH' THEN 'YDH2L011'
	    	            WHEN YD_DN_WR_LOC = 'JGFE01' AND  YD_SCH_CD = 'JGFD02LH' THEN 'YDH2L011'

	    	            WHEN YD_DN_WR_LOC = 'JEKE02' AND  YD_SCH_CD = 'JEKE01UH' THEN 'YDH2L021'
	    	            WHEN YD_DN_WR_LOC = 'JEKD02' AND  YD_SCH_CD = 'JEKD01UH' THEN 'YDH2L021'
	    	            WHEN YD_DN_WR_LOC = 'JEKE02' AND  YD_SCH_CD = 'JEKD02LH' THEN 'YDH2L021'

	    	            WHEN YD_DN_WR_LOC = 'JCKE03' AND  YD_SCH_CD = 'JCKE01UH' THEN 'YDH2L031'
	    	            WHEN YD_DN_WR_LOC = 'JCKD03' AND  YD_SCH_CD = 'JCKD01UH' THEN 'YDH2L031'
	    	            WHEN YD_DN_WR_LOC = 'JCKE03' AND  YD_SCH_CD = 'JCKD02LH' THEN 'YDH2L031'

	    	            WHEN YD_DN_WR_LOC = 'JCFE04' AND  YD_SCH_CD = 'JCFE01UH' THEN 'YDH2L051'
	    	            WHEN YD_DN_WR_LOC = 'JCFE04' AND  YD_SCH_CD = 'JCFD02LH' THEN 'YDH2L051'

	    	            WHEN YD_DN_WR_LOC = 'JBKE04' AND  YD_SCH_CD = 'JBKE01UH' THEN 'YDH2L041'
	    	            WHEN YD_DN_WR_LOC = 'JBKD04' AND  YD_SCH_CD = 'JBKD01UH' THEN 'YDH2L041'
	    	            WHEN YD_DN_WR_LOC = 'JBKE04' AND  YD_SCH_CD = 'JBKD02LH' THEN 'YDH2L041'

	    	            WHEN YD_DN_WR_LOC = 'JAKE05' AND  YD_SCH_CD = 'JAKE01UH' THEN 'YDH2L071'
	    	            WHEN YD_DN_WR_LOC = 'JAKD05' AND  YD_SCH_CD = 'JAKD01UH' THEN 'YDH2L071'
	    	            WHEN YD_DN_WR_LOC = 'JAKE05' AND  YD_SCH_CD = 'JAKD02LH' THEN 'YDH2L071'

	    	            ELSE 'N' END AS HR_L2_LINE_IN_SND_FLAG
	    	     , CASE WHEN YD_SCH_CD    IN ('JBFE03UH','JBKE03UH','JAKE03UH','JCKE03UH','JCFE03UH'
	    	                                 ,'JEKE03UH','JGFE03UH','JHKE03UH'
	    	                                  )
	    	            THEN 'Y' ELSE 'N' END HR_TAKE_IN_SND_FLAG
	    	     , CASE WHEN YD_DN_WR_LOC = 'JHKE01' AND YD_SCH_CD = 'JHKE03UH' THEN 'YDH2L001'
	    	            WHEN YD_DN_WR_LOC = 'JGFE01' AND YD_SCH_CD = 'JGFE03UH' THEN 'YDH2L011'
	    	            WHEN YD_DN_WR_LOC = 'JEKE02' AND YD_SCH_CD = 'JEKE03UH' THEN 'YDH2L021'
	    	            WHEN YD_DN_WR_LOC = 'JCKE03' AND YD_SCH_CD = 'JCKE03UH' THEN 'YDH2L031'
	    	            WHEN YD_DN_WR_LOC = 'JBKE04' AND YD_SCH_CD = 'JBKE03UH' THEN 'YDH2L041'
	    	            WHEN YD_DN_WR_LOC = 'JAKE05' AND YD_SCH_CD = 'JAKE03UH' THEN 'YDH2L071'
	    	            WHEN YD_DN_WR_LOC = 'JCFE04' AND YD_SCH_CD = 'JCFE03UH' THEN 'YDH2L051'
	    	            ELSE 'N' END HR_L2_TAKE_IN_SND_FLAG
	    	  FROM TEMP_TBL
	    	*/
			JDTORecordSet jsSndYn = commDao.select(jrParamSnd, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdDnSendYN", logId, mthdNm, "타시스템 송신여부");
		        
	        String sHrLineInSndFlag   = "N";
	        String sHrTakeInSndFlag   = "N";
	        String sHrL2LineInSndFlag = "N";
	        String sHrL2TakeInSndFlag = "N";
			if (jsSndYn.size() > 0) {
				jsSndYn.absolute(1);
				sHrLineInSndFlag   = commUtils.trim(jsSndYn.getRecord().getFieldString("HR_LINE_IN_SND_FLAG"));
				sHrL2LineInSndFlag = commUtils.trim(jsSndYn.getRecord().getFieldString("HR_L2_LINE_IN_SND_FLAG"));
				sHrTakeInSndFlag   = commUtils.trim(jsSndYn.getRecord().getFieldString("HR_TAKE_IN_SND_FLAG"));
				sHrL2TakeInSndFlag = commUtils.trim(jsSndYn.getRecord().getFieldString("HR_L2_TAKE_IN_SND_FLAG"));
			}
			commUtils.printLog(logId, "열연조업LineIn송신여부   : "+ sHrLineInSndFlag   + "열연조업TakeIn송신여부   : "+ sHrTakeInSndFlag, "SL");
			commUtils.printLog(logId, "열연조업L2LineIn송신여부: "+ sHrL2LineInSndFlag + "열연조업L2TakeIn송신여부: "+ sHrL2TakeInSndFlag, "SL");
			
			if ("Y".equals(sHrLineInSndFlag)) {
				commUtils.printLog(logId, " 보급인 경우 조업실적 송신", "SL");

				String sEqpGp = (String)commUtils.h_hstEqpGpMatch.get(ydDnWrLoc);
				
				jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
				jrYdMsg.setField("JMS_TC_CD"         , "YDHRJ001");					//열연조업 L3 정정보급완료 실적  전문코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt);
    			jrYdMsg.setField("STL_NO"            , sStlNo);						//재료번호
    			jrYdMsg.setField("YD_DN_RSLT_DT"     , currDt);						//야드권하완료일시
    			jrYdMsg.setField("EQP_GP"            , sEqpGp);
    			jrYdMsg.setField("TREAT_GP"          , "1");
				
    			//열연조업 L3 정정보급완료 실적 전송
    			jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
    			commUtils.printParam(logId, jrRtn);
    			commUtils.printLog(logId, "○○○ 열연조업 L3 정정보급완료 실적 송신", "SL");
    			
    			//품질 L3 열연정정입측보급실적 전송
    			JDTORecordSet jrYDQMJ002 = coilDao.getMsgL3("YDQMJ002", jrParam);
    			jrRtn = commUtils.addSndData(jrRtn, jrYDQMJ002);
    			commUtils.printParam(logId, jrYDQMJ002);
    			commUtils.printLog(logId, "○○○ 품질 L3 열연정정입측보급 실적 송신", "SL");
				
    			jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
    			if (sHrL2LineInSndFlag.startsWith("YDH2")) {
            		jrYdMsg.setField("JMS_TC_CD"    , sHrL2LineInSndFlag ); // SPM1 전문코드
	    			jrYdMsg.setField("YD_EQP_ID"    , ydDnWrStkColGp     ); // 야드설비ID
	    			jrYdMsg.setField("YD_STK_BED_NO", ydDnWrStkBedGp     ); // 야드적치Bed번호 
	    			jrYdMsg.setField("STL_NO"       , sStlNo             ); // 재료번호
	    			
	    			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDH2", jrYdMsg));
					commUtils.printLog(logId, "정정 보급완료실적 전문("+sHrL2LineInSndFlag+") 전문 송신", "SL");
    			}	
			}
            
            if (ydDnWrLoc.startsWith("JBKE04")
            ||  ydDnWrLoc.startsWith("JAKE05")
            ||  ydDnWrLoc.startsWith("JBFE05")
            ||  ydDnWrLoc.startsWith("JCFE04")
            ||  ydDnWrLoc.startsWith("JCKE03")
            ||  ydDnWrLoc.startsWith("JEKE02")
            ||  ydDnWrLoc.startsWith("JGFE01")
            ||  ydDnWrLoc.startsWith("JHKE01")) {
            
            	commUtils.printLog(logId, " 보급완료/TAKE-IN 실적 송신", "SL");
            	
            	if ("Y".equals(sHrTakeInSndFlag)) {
            		
    				String sEqpGp = (String)commUtils.h_hstEqpGpMatch.get(ydDnWrLoc);
    				
    				jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
    				jrYdMsg.setField("JMS_TC_CD"        , "YDHRJ001");					//열연조업 L3 정정보급완료 실적  전문코드
    				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt);
        			jrYdMsg.setField("STL_NO"           , sStlNo);						//재료번호
        			jrYdMsg.setField("YD_DN_RSLT_DT"    , currDt);						//야드권하완료일시
        			jrYdMsg.setField("EQP_GP"           , sEqpGp);
        			jrYdMsg.setField("TREAT_GP"         , "5");
        			
        			//열연조업 L3 정정보급완료 실적 전송
        			jrRtn = commUtils.addSndData(jrRtn, jrYdMsg); //YDHRJ001
        			
        			//품질 L3 열연정정입측보급실적 전송
        			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDQMJ002", jrParam));
            	}
            	
            	jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
            	if (sHrL2TakeInSndFlag.startsWith("YDH2")) {
	        		jrYdMsg.setField("JMS_TC_CD"    , sHrL2TakeInSndFlag ); // SPM1 전문코드
	    			jrYdMsg.setField("YD_EQP_ID"    , ydDnWrStkColGp     ); // 야드설비ID
	    			jrYdMsg.setField("YD_STK_BED_NO", ydDnWrStkBedGp     ); // 야드적치Bed번호 
	    			jrYdMsg.setField("STL_NO"       , sStlNo             ); // 재료번호
	    			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDH2", jrYdMsg));
					commUtils.printLog(logId, "2열연 정정Take-In실적 전문(YDH2L001) 송신 완료", "SL");
            	}
            }
            
            // 차공정존 사전 선별 대상 종료 처리
            // 2025.09.04
            if( !"FE".equals(ydDnWrLoc.substring(2, 4))
            && ( "JGFE01UH".equals(ydSchCd)
                 || "JFFE01UH".equals(ydSchCd)
                 || "JDFE01UH".equals(ydSchCd)
                 || "JCFE01UH".equals(ydSchCd)
                 || "JBFE01UH".equals(ydSchCd) 
                  )
            ) {
            	JDTORecord jrParamCoilShear = commUtils.getParam(logId, mthdNm, sModifier);
            	jrParamCoilShear.setField("STL_NO"           , sStlNo);
            	jrParamCoilShear.setField("YD_SCH_CD"       , ydSchCd);
            	jrParamCoilShear.setField("YD_DN_WR_LOC"    , ydDnWrLoc); 
            	
            	/* 
            	-- com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCoilShear_WrkWo
            	-- HFL 차공정재 사전 선별 대상 조회

            	SELECT 
            	  A.COIL_NO
            	, A.STEP_NO
            	, A.WORD_UNIT_NAME
            	, A.WORK_STAT
            	, A.CAUSE_GP
            	, A.DEL_YN
            	FROM USRYDA.TB_YD_COILSHEAR_WRKWO A
            	WHERE 1 = 1
            	AND A.COIL_NO = :V_STL_NO
            	AND A.WORK_STAT = 'B'
            	AND A.DEL_YN = 'N'
            	AND :V_YD_SCH_CD IN ('JGFE01UH', 'JFFE01UH', 'JDFE01UH', 'JCFE01UH', 'JBFE01UH')
            	AND :V_YD_DN_WR_LOC NOT LIKE '%FE%'
            	 */
            	
            	JDTORecordSet jsCoilShear = commDao.select(jrParamCoilShear, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCoilShear_WrkWo", logId, mthdNm, "차공정존 사전 선별 대상 조회");
            	
            	if ( jsCoilShear.size() > 0 ) {
            		
            		/* 
            		-- com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCoilShear_WrkWo 
            		-- HFL 차공정재 사전 선별 대상 종료 처리

            		UPDATE USRYDA.TB_YD_COILSHEAR_WRKWO A
            		SET WORK_STAT = 'E'
            		, DEL_YN = 'Y'
            		, MOD_DDTT = SYSDATE
            		, MODIFIER = :V_MODIFIER
            		WHERE 1 = 1
            		AND A.COIL_NO = :V_STL_NO
            		AND A.WORK_STAT = 'B'
            		AND A.DEL_YN = 'N'
            		AND :V_YD_SCH_CD IN ('JGFE01UH', 'JFFE01UH', 'JDFE01UH', 'JCFE01UH', 'JBFE01UH')
            		AND :V_YD_DN_WR_LOC NOT LIKE '%FE%'
            		*/

            		commDao.update(jrParamCoilShear, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCoilShear_WrkWo", logId, mthdNm, "차공정존 사전 선별 대상 종료 처리");
            	
            		//HFL결속장 스케줄코드로 다음 작업예약을 스케줄 기동 작업
            		if ( "JGFE01UH".equals(ydSchCd)
                      || "JFFE01UH".equals(ydSchCd)
                      || "JDFE01UH".equals(ydSchCd)
                      || "JCFE01UH".equals(ydSchCd)
                      || "JBFE01UH".equals(ydSchCd)  ) {
            			
            			String sCurrDt       = commUtils.getDateTime14();
            			
            			JDTORecordSet jsCoilShear_Lyr = commDao.select(jrParamCoilShear, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCoilShear_Lyr", logId, mthdNm, "HFL결속장 보급위치 조회");
    					
    					if (jsCoilShear_Lyr.size() > 0) {
    						
    						JDTORecord jrCoilShear_CrnSchMsg = commUtils.getParam(logId, mthdNm, sModifier);
    						jrCoilShear_CrnSchMsg.setField("JMS_TC_CD"	     , "YDYDJ551");
    						jrCoilShear_CrnSchMsg.setField("JMS_TC_CREATE_DDTT", sCurrDt   ); //JMSTC생성일시
    						jrCoilShear_CrnSchMsg.setField("YD_WBOOK_ID"       , ""        );
    						jrCoilShear_CrnSchMsg.setField("YD_EQP_ID"         , ""        );
    						jrCoilShear_CrnSchMsg.setField("YD_SCH_CD"         , ydSchCd);
    						
    						jrRtn = commUtils.addSndData(jrRtn, jrCoilShear_CrnSchMsg);
    						
    						commUtils.printLog(logId, "차공정존 사전 선별 대상 종료 처리 후 스케줄 기동", "SL");
    					} else {
    						commUtils.printLog(logId, "차공정존 사전 선별 대상 종료 처리 후 보급위치 없음", "SL");
    					}
            		}
            	}
            }
             
 
			/**********************************************************
			* 10. 출하실적 송신 
			* - 현진도코드가 입고대기(H)이고 야드가 제품야드(J)인 경우  강관입고대기 추가
			**********************************************************/
            boolean bYd = ydDnWrLoc.matches("[J][A-H]\\d\\d\\d\\d\\d\\d");
            commUtils.printLog(logId, "★span : "+ ydDnWrLoc.substring(2, 4), "SL");
            commUtils.printLog(logId, "★bYd  : "+ bYd, "SL");

            if ("J".equals(ydLocGp) && bYd) {
            	/*******************************
            	 * 코일입고작업실적 YDDMR001
            	 * - 제품 야드로 권하될 때만 송신
            	 *******************************/
            	if ("H".equals(sCurrProgCd) || "2".equals(sCurrProgCd)) { 
	            	jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
//	            	String sApplyYnPI_IN = ydPICommDAO.ApplyYnPI("", "크레인권하실적(Y5YDL009)", "APPPI0", "*", "*");
//PIDEV
	            	
//					if("Y".equals(sApplyYnPI_IN)) {
						
						jrYdMsg.setField("MQ_TC_CD"     , "M10YDLMJ1011");	
		    			jrYdMsg.setField("YD_WBOOK_ID"   , ydWbookId);	
						
		    			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("M10YDLMJ1011", jrYdMsg));						
		    			commUtils.printLog(logId, "출하관리 코일입고작업실적 송신(M10YDLMJ1011)", "SL");
					
//					} else {
//						
//						jrYdMsg.setField("JMS_TC_CD"     , "YDDMR001");	
//		    			jrYdMsg.setField("YD_WBOOK_ID"   , ydWbookId);	
//						
//		    			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR001", jrYdMsg));
//		    			commUtils.printLog(logId, "출하관리 코일입고작업실적 송신(YDDMR001)", "SL");
//		    			
//					}
	            }
            }
            
            if (!ydSchCd.endsWith("H")) {
            	/***********************************
            	 * 코일제품이적작업실적 YDDMR004
            	 **********************************/
	        	if (("YD".equals(ydSchCd.substring(2, 4)) && "MM".equals(ydSchCd.substring(6)))	// 일반야드 이적
	        	||  ("TC".equals(ydSchCd.substring(2, 4)) && "MM".equals(ydSchCd.substring(6))) 
	        	||  ("TC".equals(ydSchCd.substring(2, 4)) && "LM".equals(ydSchCd.substring(6)))) {	// 동간이적 대차 하차
	        		
	        		// PIDEV
//					if("Y".equals(sApplyYnPI)) {
						
						jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setField("MQ_TC_CD", "M10YDLMJ1031");
		        		jrYdMsg.setField("GOODS_NO", sStlNo);
		        		jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("M10YDLMJ1031", jrYdMsg));						
		        		commUtils.printLog(logId, "출하관리 코일제품이적작업실적 전송(M10YDLMJ1031)", "SL");
		        		
//					} else {
//						
//		        		jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);		        	
//		        		jrYdMsg.setField("JMS_TC_CD",  "YDDMR004");
//		        		jrYdMsg.setField("STL_NO"   ,  sStlNo    );
//		    			
//		        		jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR004", jrYdMsg));
//		        		commUtils.printLog(logId, "출하관리 코일제품이적작업실적 전송(YDDMR004)", "SL");
//		        		
//	        		}
		        }
	        }
            
			/**********************************************************
			* 11. 작업실적응답 송신(YDY5L005)
			**********************************************************/
            //크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
			}
			
			commUtils.printLog(logId, "영대차 출발지시["+ tcarLdCmplYn +"]", "SL");
			//L2 영대차출발지시
			if ("Y".equals(tcarLdCmplYn)) {
				jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L006", jrTcSnd));
			}			
			/**********************************************************
			* 13. 반납실적정보 송신
			*     반납시 조업메시지 전송
			*     반송대상재 정보 초기화
			**********************************************************/
			commUtils.printLog(logId, "출하반납실적정보 송신_야드구분["+ ydLocGp +"] 스케쥴코드["+ ydSchCd +"] 진도코드["+ sCurrProgCd +"] 보조작업유무["+ ydAidWrkYn +"]", "SL");
			//제품야드로 반품 후 소재야드로 반납 받는 경우
			if ((
				 ("JAYD03UH".equals(ydSchCd)
               	||"JBYD03UH".equals(ydSchCd)
            	||"JBYD53UH".equals(ydSchCd)
           		||"JCYD03UH".equals(ydSchCd)
           		||"JCYD53UH".equals(ydSchCd)
           		||"JDYD03UH".equals(ydSchCd)
           		||"JEYD03UH".equals(ydSchCd)
        		||"JFYD03UH".equals(ydSchCd)
        		||"JGYD03UH".equals(ydSchCd)
        		||"JHYD03UH".equals(ydSchCd)
        		||"J".equals(ydSchCd.substring(6, 7))) //기존 로직 ydSchCd.substring(6, 8).equals("UJ")
        		&& ("J".equals(sCurrProgCd)|| "5".equals(sCurrProgCd))
        		&& "H".equals(ydLocGp)
        		&& "N".equals(ydAidWrkYn) //주작업
		        )
		     || //소재야드에서 직접 반품을 받는 경우
		        (
		          ("JAPT04LH".equals(ydSchCd)
		        || "JBPT04LH".equals(ydSchCd)
        		|| "JCPT04LH".equals(ydSchCd)
        		|| "JDPT04LH".equals(ydSchCd)
        		|| "JEPT04LH".equals(ydSchCd)
        		|| "JFPT04LH".equals(ydSchCd)
        		|| "JGPT04LH".equals(ydSchCd)
        		|| "JHPT04LH".equals(ydSchCd)
        		
        		/* 소재 반품/부분하차일 경우 YYS 20230419
        		|| "JAPT43LH".equals(ydSchCd)
        		|| "JBPT43LH".equals(ydSchCd)
        		|| "JCPT43LH".equals(ydSchCd)
        		|| "JDPT43LH".equals(ydSchCd)
        		|| "JEPT43LH".equals(ydSchCd)        		
        		|| "JHPT43LH".equals(ydSchCd) 
        		*/
		          )
        		&& "H".equals(ydLocGp)
		        )           
		     || //소재야드에서 차량동간이적으로 직접 반품을 받는 경우
		    	(
		    	 "TR1".equals(ydSchCd.substring(2, 5))
        		&& "H".equals(ydLocGp) 
		        && ("J".equals(sCurrProgCd)|| "5".equals(sCurrProgCd))
		        )
		    ) {
				jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
				
				//PIDEV
//				if("Y".equals(sApplyYnPI)) {
					//외부송신
					jrYdMsg.setField("STL_NO",  sStlNo    );	
					jrYdMsg.setField("RETURN_REQ_INFO_YN" ,  "");
					jrRtn = commUtils.addSndData(jrRtn,  coilDao.getMsgL3("M10YDLMJ1021", jrYdMsg));
					commUtils.printLog(logId, "반납실적정보 송신(M10YDLMJ1021)", "SL");
//				} else {
//	        		jrYdMsg.setField("JMS_TC_CD",  "YDDMR034");
//	        		jrYdMsg.setField("STL_NO",  sStlNo    );
//	    			
//	        		jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR034", jrYdMsg));
//	        		commUtils.printLog(logId, "반납실적정보 송신(YDDMR034)", "SL");        		
// 				}
    			
			}
			
			
			/*
			 * 반납 시 조업 메시지 전송
			 */
            if ( //소재야드에서 직접 반품을 받는 경우
           	    (
           	      ("JAPT41LH".equals(ydSchCd) || "JAPT43LH".equals(ydSchCd)
        		|| "JBPT41LH".equals(ydSchCd) || "JBPT43LH".equals(ydSchCd) || "JBPT42LH".equals(ydSchCd)
        		|| "JCPT41LH".equals(ydSchCd) || "JCPT43LH".equals(ydSchCd) || "JCPT42LH".equals(ydSchCd)
        		|| "JDPT41LH".equals(ydSchCd) || "JDPT43LH".equals(ydSchCd)
        		|| "JEPT41LH".equals(ydSchCd) || "JEPT43LH".equals(ydSchCd)
        		|| "JFPT41LH".equals(ydSchCd)
        		|| "JGPT41LH".equals(ydSchCd) || "JGPT43LH".equals(ydSchCd)
        		|| "JHPT41LH".equals(ydSchCd) || "JHPT43LH".equals(ydSchCd)
        		  )
        		&& "H".equals(ydLocGp)
        		)
	         ||
	        	( //소재야드에서 차량동간이적으로 직접 반품을 받는 경우
	        	   "TR1".equals(ydSchCd.substring(2 , 5))
	        	&& "H".equals(ydLocGp) 
	        	&& ("J".equals(sCurrProgCd)|| "5".equals(sCurrProgCd))
	        	)
             ) {
            	commUtils.printLog(logId, "반납메시지 조업 등록", "SL");
            	jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);		        	
        		jrYdMsg.setField("STL_NO",  sStlNo);
        		/*
				INSERT INTO TB_HR_C_SHEARWOWR_MSG_LOG 
				SELECT CC.COIL_NO                                 
				     , SYSDATE                                 
				     , NVL(SR.STEP_NO, 1)
				     , NVL(SR.HR_PLNT_GP, CC.HR_PLNT_GP)
				     , 'J'                     
				     , ST.YD_ABMTL_REM                         
				     , ST.SNDBK_REGISTER                            
				  FROM USRPTA.TB_PT_COILCOMM CC
				     , USRHRA.TB_HR_C_SHEARWOWR SR
				     , USRYDA.TB_YD_STOCK ST
				 WHERE CC.COIL_NO = SR.COIL_NO(+)
				   AND CC.COIL_NO = ST.STL_NO
				   AND CC.COIL_NO = :V_STL_NO
				   AND (CC.COIL_NO,SYSDATE) NOT IN (SELECT COIL_NO,REG_DDTT FROM TB_HR_C_SHEARWOWR_MSG_LOG B WHERE B.COIL_NO=CC.COIL_NO)
				   AND NVL(SR.STEP_NO, 0) = (SELECT NVL(MAX(X.STEP_NO),0)
				                              FROM TB_HR_C_SHEARWOWR X
				                             WHERE X.COIL_NO = CC.COIL_NO)
        		 */
            	commDao.update(jrYdMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insHrShrMsgLog", logId, mthdNm, "반납메시지 등록");
            }

            
            /*
             * 반송대상재 정보 초기화
             */
	        if ("H".equals(ydLocGp) 
	        && ("JDYD04UH".equals(ydSchCd)
			  ||"JEYD04UH".equals(ydSchCd)
			  ||"JFYD04UH".equals(ydSchCd)
			  ||"JGYD04UH".equals(ydSchCd)
			  ||"JHYD04UH".equals(ydSchCd)
			   )
			||
	           ( //소재야드에서 차량동간이적으로 직접 반품을 받는 경우
	        	  "TR1".equals(ydSchCd.substring(2 , 5))
	        	&& "H".equals(ydLocGp) 
	        	&& ("J".equals(sCurrProgCd) || "5".equals(sCurrProgCd))
	            )
	         ) {
	        	commUtils.printLog(logId, "반송대상재 반납사유 정보 초기화", "SL");
            	jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);		        	
        		jrYdMsg.setField("STL_NO"         ,  sStlNo);
        		jrYdMsg.setField("SNDBK_RSN_CD"   , "");
				jrYdMsg.setField("SNDBK_REGISTER" , "");
				jrYdMsg.setField("SNDBK_GP"       , "");
				jrYdMsg.setField("YD_AIM_BAY_GP"  , "");
        		
				/*
				UPDATE TB_YD_STOCK 
				   SET SNDBK_RSN_CD   = :V_SNDBK_RSN_CD
				     , SNDBK_REGISTER = :V_SNDBK_REGISTER
					 , SNDBK_REG_DDTT = SYSDATE
					 , SNDBK_GP = :V_SNDBK_GP
				     , MODIFIER = :V_SNDBK_REGISTER
				     , MOD_DDTT = SYSDATE
				     , YD_AIM_BAY_GP = NVL(:V_YD_AIM_BAY_GP,YD_AIM_BAY_GP)
				     , SNDBK_GP_ETC  =:V_SNDBK_GP_ETC
				 WHERE STL_NO = :V_STL_NO 	
				 */
				commDao.update(jrYdMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCoilYdRetMgt", logId, mthdNm, "반납사유 초기화");
        	}
			/**********************************************************
			* 14. 진행관리 실적 송신
			**********************************************************/
	        commUtils.printLog(logId, "진행관리 실적송신_야드구분["+ ydLocGp +"] 스케쥴코드["+ ydSchCd +"] 진도코드["+ sCurrProgCd +"]", "SL");
	        if ( "H".equals(ydLocGp)
	        &&	("J".equals(sCurrProgCd)||"5".equals(sCurrProgCd)||
				 "B".equals(sCurrProgCd)||
				 "F".equals(sCurrProgCd)||
				 "G".equals(sCurrProgCd)||
				 "H".equals(sCurrProgCd)||
				 "Y".equals(sCurrProgCd))
			&&	( "JAYD03UH".equals(ydSchCd)
				||"JBYD03UH".equals(ydSchCd) 
				||"JBYD53UH".equals(ydSchCd) 
				||"JCYD03UH".equals(ydSchCd) 
				||"JCYD53UH".equals(ydSchCd) 
//PIDEV				
				||"JDYD03UH".equals(ydSchCd)
//
				||"JEYD03UH".equals(ydSchCd) 
				||"JFYD03UH".equals(ydSchCd) 
				||"JGYD03UH".equals(ydSchCd) 
				||"JHYD03UH".equals(ydSchCd) 
//				||"J".equals(ydSchCd.substring(6, 7)) //기존 로직 ydSchCd.substring(6, 8).equals("UJ")
				||"JAYD04UH".equals(ydSchCd)
				||"JBYD04UH".equals(ydSchCd)
				||"JBYD54UH".equals(ydSchCd)
				||"JCYD04UH".equals(ydSchCd)
				||"JCYD54UH".equals(ydSchCd)
				||"JDYD04UH".equals(ydSchCd)
				||"JEYD04UH".equals(ydSchCd)
				||"JFYD04UH".equals(ydSchCd)
				||"JGYD04UH".equals(ydSchCd)
				||"JHYD04UH".equals(ydSchCd)
				||"B".equals(ydSchCd.substring(6, 7))//"UB".substring(6, 8).equals(ydSchCd) 
				)) {
	        	commUtils.printLog(logId, "진행관리 실적전송 송신", "SL");
				
	        	jrParam.setField("COIL_NO", sStlNo);
	        	JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getPtCoilComm", logId, mthdNm, "코일공통 조회");
	        	
	        	if (jsRst.size() <= 0) {
	        		commUtils.printLog(logId, "COIL공통 테이블 조회오류", "SL");
	        	} else {
					jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);		        	
	        		jrYdMsg.setField("JMS_TC_CD" ,  "YDPTJ002");
	        		jrYdMsg.setField("COIL_NO"   ,  sStlNo    );
	    			
	        		jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDPTJ002", jrYdMsg));
				}
			}
	        
			/**********************************************************
			* 15. 수입시 조업Tc송신
			**********************************************************/
			if ("JACV01LH".equals(ydSchCd) ||
				"JBCV01LH".equals(ydSchCd) ||
				"JCCV01LH".equals(ydSchCd) ||
				"JDCV01LH".equals(ydSchCd) ||
				"JECV01LH".equals(ydSchCd) ||
				"JFCV01LH".equals(ydSchCd) ||
				"JGCV01LH".equals(ydSchCd) ||
				"JHCV01LH".equals(ydSchCd)) {
				
				jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier); 
				jrYdMsg.setField("JMS_TC_CD"			, "YDHRJ007");
				jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, currDt);
				jrYdMsg.setField("YD_GP"				, "J"); // 야드구분 추가	 
				jrYdMsg.setField("STL_NO"				, sStlNo);// 재료번호	 
		    	
		    	jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
		    	commUtils.printLog(logId, "열연정정추출완료실적 송신 YDHRJ007", "SL");
			}
			/**********************************************************
			* 16. 결로재추출시 조업Tc송신
			**********************************************************/
			if ("JEHC01UH".equals(ydSchCd) ||
				"JFHC01UH".equals(ydSchCd)) {
				
				jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier); 
				jrYdMsg.setField("JMS_TC_CD"         , "YDHRJ008");
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt);												    					    					    	
				jrYdMsg.setField("STL_NO"            , sStlNo);// 재료번호	 
		    	
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
		    	commUtils.printLog(logId, "결로재 추출실적 송신 YDHRJ008", "SL");
			}			
			
			/**********************************************************
			* 18. 차량 동간이적 작업
			**********************************************************/
			/* 차량동간이적 작업 
			 * 상차작업 인 경우(스케줄 코드로 구분)			
			 * 1. 하차지 차량  작업예약을 생성 한다.
			 * 2. 상차매수를 체크 해서 하차지 작업예약ID로 하차지 스케줄을 호출 한다.  			
			 * 하차작업 인 경우	
			 * 1. 하차매수를 체크 해서 상차지 작업예약ID로 상차지 스케줄을 호출 한다. */
			
			JDTORecord jrCarMv = commUtils.getParam(logId, mthdNm, sModifier);
			jrCarMv.setField("YD_SCH_CD"     ,  ydSchCd  );
			jrCarMv.setField("STL_NO"        ,  sStlNo   );
			jrCarMv.setField("YD_DN_WR_LOC"  ,  ydDnWrLoc);
			jrCarMv.setField("YD_WBOOK_ID"   ,  ydWbookId);
			jrCarMv.setField("YD_CRN_SCH_ID" ,  ydCrnSchId);
			
			if (bCarMvYn) { //차량동간이적이면
				
				JDTORecord jrProc = this.traillerMoveSch(jrCarMv);
				jrRtn = commUtils.addSndData(jrRtn, jrProc);
            	
				jrParam.setField("YD_CAR_SCH_ID", jrProc.getFieldString("YD_CAR_SCH_ID"));
        	}			

			/**********************************************************
			* 이력테이블 등록
			**********************************************************/
			jrParam.setField("STL_NO"    , sSTL_NO);
			jrParam.setField("NEXT_PROC" , ydNextProc);
			commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insYdWrkHist", logId, mthdNm, "작업이력 등록");
			
			 
			/**********************************************************
			* 대상재가 차량작업 대상일 경우 권하시 스케줄 기동
			**********************************************************/
			String sApp809 = coilDao.ApplyYn(logId, mthdNm, "APP809", "J", "*");
	    	
    		commUtils.printLog(logId, "상차대상 자동 스케줄기동 여부 : " + sApp809, "SL");
    		
    		if ("Y".equals(sApp809)) {
    			jrParam.setField("STL_NO", sStlNo);
    			/*
    			SELECT WB.YD_WBOOK_ID 
    			  FROM TB_YD_WRKBOOK    WB
    			     , TB_YD_WRKBOOKMTL WM
    			 WHERE WB.DEL_YN = 'N'
    			   AND WM.DEL_YN = 'N'
    			   AND WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
    			   AND NOT EXISTS (SELECT 1
    			                     FROM TB_YD_CRNSCH    CS 
    			                        , TB_YD_CRNWRKMTL CM
    			                    WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
    			                      AND CS.DEL_YN = 'N'
    			                      AND CM.DEL_YN = 'N'
    			                      AND CM.STL_NO = WM.STL_NO)
    			   AND WM.STL_NO = :V_STL_NO
    			   AND WB.YD_GP  = 'J'
    			   AND (WB.YD_SCH_CD LIKE '%PT%' OR WB.YD_SCH_CD LIKE '%TR0%') --차량동간이적 제외
    			*/
    			JDTORecordSet jsList = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCarSchNotStWrkbook", logId, mthdNm, "작업예약 조회");
    			
    			if (jsList.size() > 0) {
    				
    				String sWbookId = jsList.getRecord(0).getFieldString("YD_WBOOK_ID");
    				
    				commUtils.printLog(logId, "상차대상 작업예약ID : " + sWbookId, "SL");

    				JDTORecord jrMsg = JDTORecordFactory.getInstance().create();
					jrMsg = JDTORecordFactory.getInstance().create();
					jrMsg.setField("JMS_TC_CD"		   , "YDYDJ551"); 
					jrMsg.setField("JMS_TC_CREATE_DDTT", currDt    ); //JMSTC생성일시
					jrMsg.setField("YD_WBOOK_ID"       , sWbookId  ); //야드작업예약ID
					jrMsg.setField("YD_SCH_CD"         , ""        ); //야드스케쥴코드
					jrRtn = commUtils.addSndData(jrRtn, jrMsg);
    			}
    		}
    		
			/**********************************************************
			 * 2단작업으로 인한 1단 출하대상 스케줄 미기동시 크레인 스케줄 기동
			 **********************************************************/
			String sAPP837_YN = coilDao.ApplyYn(logId, mthdNm, "APP837", "J", "*");
			if ("Y".equals(sAPP837_YN) && "002".equals(ydDnWrLayer)) {
				jrParam.setField("YD_STK_COL_GP" , ydDnWrLoc.substring(0, 6) );
			    jrParam.setField("YD_STK_BED_NO" , ydDnWrLoc.substring(6, 8));
				/*
				SELECT    WB.*
			      FROM  TB_YD_WRKBOOKMTL CM
			          , TB_YD_WRKBOOK   WB
			          , TB_YD_CARPOINT  CP
			          , (
			             SELECT (SELECT STL_NO
			                      FROM TB_YD_STKLYR
			                     WHERE YD_STK_COL_GP = LY.YD_STK_COL_GP
			                       AND YD_STK_BED_NO = LY.L_BED
			                       AND YD_STK_LYR_NO = '001') AS L_STL_NO
			                 , (SELECT STL_NO
			                      FROM TB_YD_STKLYR
			                     WHERE YD_STK_COL_GP = LY.YD_STK_COL_GP
			                       AND YD_STK_BED_NO = LY.R_BED
			                       AND YD_STK_LYR_NO = '001') AS R_STL_NO
			              FROM (SELECT DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
			                                                    '002', SL.YD_STK_BED_NO)                               AS L_BED
			                         , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_BED_NO,
			                                                    '002', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'))
			                                                                                                           AS R_BED
			                         , SL.*
			                      FROM TB_YD_STKLYR  SL
			                     WHERE SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
			                       AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
			                       AND SL.YD_STK_LYR_NO = '002'
			                   ) LY
			              WHERE 1 = 1       
			             ) SL
			     WHERE CM.STL_NO IN (SL.L_STL_NO, SL.R_STL_NO)   
			       AND WB.YD_WBOOK_ID = CM.YD_WBOOK_ID
			       AND WB.CAR_NO  = CP.CAR_NO
			       AND CM.DEL_YN  = 'N' 
			       AND CP.DEL_YN  = 'N'
			       AND CP.YD_GP   = 'J'
			       AND WB.YD_GP   = 'J'
			       AND WB.CAR_NO IS NOT NULL       
			       AND SUBSTR(WB.YD_SCH_CD, 3, 2) IN ('PT','TR') 
			       AND NOT EXISTS (SELECT 1
			                     FROM TB_YD_WRKBOOK    WR
			                        , TB_YD_WRKBOOKMTL WM
			                        , TB_YD_CRNSCH     CS
			                    WHERE WR.YD_WBOOK_ID = WM.YD_WBOOK_ID
			                      AND WR.YD_WBOOK_ID = CS.YD_WBOOK_ID
			                      AND WR.YD_GP       = 'J'
			                      AND WR.DEL_YN      = 'N'
			                      AND WM.DEL_YN      = 'N'
			                      AND CS.DEL_YN      = 'N'
			                      --AND WM.STL_NO      = CM.STL_NO
			                      AND WR.YD_WBOOK_ID = WB.YD_WBOOK_ID
			                  )
				 */
				JDTORecordSet jsCarLdSeq = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getDmWrkbookList_PIDEV", logId, mthdNm, "출하작업예약 조회");

				if (jsCarLdSeq.size() > 0 ) {
					
					JDTORecord jrDmSch = commUtils.getParam(logId, mthdNm, sModifier); 
					jrDmSch.setField("JMS_TC_CD"		 , "YDYDJ552");  //
					jrDmSch.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrDmSch.setField("YD_SCH_CD"         , ""); //야드스케쥴코드
					jrDmSch.setField("YD_EQP_ID"         , ""); //야드설비ID
					
					for (int i = 1; i <= jsCarLdSeq.size(); i++) {
						jsCarLdSeq.absolute(i);
						jrDmSch.setField("YD_WBOOK_ID" + i, jsCarLdSeq.getRecord().getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrDmSch.setField("SCH_CNT"        , "" + i); //작업예약 개수
					}
					commUtils.printLog(logId, "크레인스케줄 기동", "SL");
				}	
			}
    		

			/**********************************************************
			 * 2단작업으로 인한 1단 보급대상 스케줄 미기동시 크레인 스케줄 기동
			 **********************************************************/
			String sAPP019_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","019"); 
			commUtils.printLog(logId, "1단 보급대상 미기동 크레인스케줄기동 : " + sAPP019_YN, "SL");
			if ("Y".equals(sAPP019_YN) && "002".equals(ydDnWrLayer)) {
				jrParam.setField("YD_STK_COL_GP" , ydDnWrLoc.substring(0, 6));
			    jrParam.setField("YD_STK_BED_NO" , ydDnWrLoc.substring(6, 8));
				/*
				SELECT    WB.*
				      FROM  TB_YD_WRKBOOKMTL CM
				          , TB_YD_WRKBOOK   WB
				          , (
				             SELECT (SELECT STL_NO
				                      FROM TB_YD_STKLYR
				                     WHERE YD_STK_COL_GP = LY.YD_STK_COL_GP
				                       AND YD_STK_BED_NO = LY.L_BED
				                       AND YD_STK_LYR_NO = '001') AS L_STL_NO
				                 , (SELECT STL_NO
				                      FROM TB_YD_STKLYR
				                     WHERE YD_STK_COL_GP = LY.YD_STK_COL_GP
				                       AND YD_STK_BED_NO = LY.R_BED
				                       AND YD_STK_LYR_NO = '001') AS R_STL_NO
				              FROM (SELECT DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
				                                                    '002', SL.YD_STK_BED_NO)                               AS L_BED
				                         , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_BED_NO,
				                                                    '002', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'))
				                                                                                                           AS R_BED
				                         , SL.*
				                      FROM TB_YD_STKLYR  SL
				                     WHERE SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
				                       AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
				                       AND SL.YD_STK_LYR_NO = '002'
				                   ) LY
				              WHERE 1 = 1       
				             ) SL
				     WHERE CM.STL_NO IN (SL.L_STL_NO, SL.R_STL_NO)   
				       AND WB.YD_WBOOK_ID = CM.YD_WBOOK_ID
				       AND CM.DEL_YN  = 'N' 
				       AND WB.YD_GP   = 'J'
				       AND SUBSTR(WB.YD_SCH_CD, 3, 2) IN ('KE') 
				       AND NOT EXISTS (SELECT 1
				                     FROM TB_YD_WRKBOOK    WR
				                        , TB_YD_WRKBOOKMTL WM
				                        , TB_YD_CRNSCH     CS
				                    WHERE WR.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                      AND WR.YD_WBOOK_ID = CS.YD_WBOOK_ID
				                      AND WR.YD_GP       = 'J'
				                      AND WR.DEL_YN      = 'N'
				                      AND WM.DEL_YN      = 'N'
				                      AND CS.DEL_YN      = 'N'
				                      AND WR.YD_WBOOK_ID = WB.YD_WBOOK_ID
				                  )
				 */
				JDTORecordSet jsSPMLdSeq = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getDmWrkbookListKE", logId, mthdNm, "보급작업예약 조회");

				if (jsSPMLdSeq.size() > 0 ) {
					
					JDTORecord jrKESch = commUtils.getParam(logId, mthdNm, sModifier); 
					jrKESch.setField("JMS_TC_CD"		 , "YDYDJ552");  //
					jrKESch.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrKESch.setField("YD_SCH_CD"         , ""); //야드스케쥴코드
					jrKESch.setField("YD_EQP_ID"         , ""); //야드설비ID
					
					for (int i = 1; i <= jsSPMLdSeq.size(); i++) {
						jsSPMLdSeq.absolute(i);
						jrKESch.setField("YD_WBOOK_ID" + i, jsSPMLdSeq.getRecord().getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrKESch.setField("SCH_CNT"        , "" + i); //작업예약 개수
					}
					commUtils.printLog(logId, "보급크레인스케줄 기동", "SL");
				}	
			}

			/**********************************************************
			* 19. 크레인작업지시 호출
			**********************************************************/
			jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);

			jrYdMsg.setField("JMS_TC_CD"		, "Y5YDL007"); //JMSTC코드 
			jrYdMsg.setField("YD_EQP_ID"		, ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT"	, "4"       ); //야드작업진행상태(권하완료)
			jrYdMsg.setField("YD_SCH_CD"		, ydSchCd   ); //야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID"	, ydCrnSchId); //야드크레인스케쥴ID
			jrYdMsg.setField("YD_DN_CAR_FLAG"	, sDnCarFlag);
			jrYdMsg.setField("TCAR_LD_CMPL_YN"	, tcarLdCmplYn); // 야드L2 영대차 출발지시
			
			//크레인작업지시 요구을 추가
			jrRtn = commUtils.addSndData(jrRtn, this.rcvY5YDL007(jrYdMsg));
			
			/**********************************************************
			* 20. 대차상차스케쥴 추가기동
			**********************************************************/
			commUtils.printLog(logId, "YD_SCH_CD["+ ydSchCd +"] YD_WRK_PROG_STAT["+ ydWrkProgStat +"] YD_DN_WR_LOC["+ ydDnWrLoc +"]", "SL");
			if (ydDnWrLoc.length() == 8 ) {
				
				String sDateTime = commUtils.getDateTime14();
    			if ("TC".equals(ydSchCd.substring(2, 4)) && "UH".equals(ydSchCd.substring(6, 8)) && "4".equals(ydWrkProgStat) ) {
					/***********************************************************
					*  소재 대차 작업예약에 있는거 기동 할수 있는지 Check 하여 1개 기동처리함
					***********************************************************/
    				jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);

					jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ555"	);
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", sDateTime	);
					jrYdMsg.setField("YD_EQP_ID"         , ydEqpId		); // 야드설비ID
					jrYdMsg.setField("YD_SCH_CD"         , ydSchCd		); // 스케쥴코드

					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
    			} // 동내이적 스케쥴 추가 1개 기동
    			else if( "YD01MH".equals(ydSchCd.substring(2, 8)) || "YD02MH".equals(ydSchCd.substring(2, 8)) || "YD51MH".equals(ydSchCd.substring(2, 8)) ) {
    				String sAPP314 = coilDao.ApplyYn(logId, mthdNm, "APP314", "J", "*");
    				commUtils.printLog(logId, "소재 동내이적 추가기동 : " + sAPP314, "SL");
        			if ("Y".equals(sAPP314)) {
						/***********************************************************
						*  소재 동내이적 스케쥴 
						***********************************************************/
	    				jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
	
						jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ555"	);
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", sDateTime	);
						jrYdMsg.setField("YD_EQP_ID"         , ydEqpId		); // 야드설비ID
						jrYdMsg.setField("YD_SCH_CD"         , ydSchCd		); // 스케쥴코드
	
						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
        			}
    			}

    			String sAPP030_YN = coilDao.ApplyYn(logId, mthdNm, "APP030", "J", "*");
    			String sAPP835_YN = coilDao.ApplyYn(logId, mthdNm, "APP835", "J", "*"); // 제품 동간이적 스케줄 기동
    			if ("Y".equals(sAPP030_YN) && "N".equals(sAPP835_YN)) {
        			if ("TC".equals(ydSchCd.substring(2, 4)) 
        			&& ("UM".equals(ydSchCd.substring(6, 8)) || "MM".equals(ydSchCd.substring(6, 8)))
        			&& "4".equals(ydWrkProgStat) ) {
						/***********************************************************
						*  제품 대차 작업예약에 있는거 기동 할수 있는지 Check 하여 1개 기동처리함
						***********************************************************/
        				//REQ202407592860  2열연 제품장 대차 시스템 개선 작업 (송인수 책임매니저)
        				commUtils.printLog(logId, "대차 상태를 확인하여 대차 작업예약에 있는거 기동 할수 있는지 Check 하여 1개 기동처리함", "SL");
        				
        				jrParam.setField("YD_EQP_ID" , "JX"+ ydSchCd.substring(2, 6));
        				
        				JDTORecordSet jsList = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdTcarSchByYdEqpId", logId, mthdNm, "대차스케줄조회1");
        				String sydCarProgStat = "";
        				
            			if (jsList.size() > 0) {            				
            				sydCarProgStat = jsList.getRecord(0).getFieldString("YD_CAR_PROG_STAT"); 
            			}
            			
            			commUtils.printLog(logId, "대차가 이동 상태 확인:" + sydCarProgStat, "SL");
        				 
            			// 상차출발(1), 상차완료(5), 하차출발 (A), 하차완료 (E) 인경우 다음 대차 작업을 호출 하지 않는다.
            			if("1".equals(sydCarProgStat)||"5".equals(sydCarProgStat)
            					|| "A".equals(sydCarProgStat)||"E".equals(sydCarProgStat)){            		 
            				commUtils.printLog(logId, "대차가 상차완료,하차출발 상태에서는 다음 작업지시 호출 안함.:" + sydCarProgStat, "SL");
            			}else{
	        				jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
	        				
							jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ555"	);
							jrYdMsg.setField("JMS_TC_CREATE_DDTT", sDateTime	);
							jrYdMsg.setField("YD_EQP_ID"         , ydEqpId		); // 야드설비ID
							jrYdMsg.setField("YD_SCH_CD"         , ydSchCd		); // 스케쥴코드
	
							jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
            			}
        			}
    			}
			}
			
			
			/***************************************
			 * HOT코일 보급 스케줄인 경우
			 * - 보급순서 및 보급개수 저장
			 ***************************************/
			if (ydSchCd.endsWith("HC02LM")) {
				String ydBayGp = ydSchCd.substring(1, 2);
				
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				
				jrParam.setField("YD_BAY_GP", ydBayGp);
				jrParam.setField("STL_NO"   , sSTL_NO);
				
				/*
				SELECT *
				  FROM (-- HOT COIL대상 가져 오기(B,C동)
				        SELECT SUBSTR(NVL(A.NEXT_PROC, A.PLAN_PROC1), 2, 1) AS NEXT_PROC
				             , A.HRMILL_CMPL_DT
				             , B.YD_STK_COL_GP AS YD_STK_COL_GP2
				             , B.YD_STK_BED_NO AS YD_STK_BED_NO2
				             , B.YD_STK_LYR_NO AS YD_STK_LYR_NO2
				             , SUBSTR(B.YD_STK_COL_GP, 2, 1) AS BAY_GP2
				             , B.STL_NO
				             , (CASE WHEN SUBSTR(B.YD_STK_COL_GP, 2, 1) IN ('B', 'C') AND TO_NUMBER(SUBSTR(B.YD_STK_COL_GP, 3, 2)) <=38 THEN 2 ELSE 1 END) AS SPAN_GP2
				             , DECODE(NVL(E.AB_OCCUR_GP_CD, '0')
				                    , '0', TRUNC(SYSDATE - E.MILL_WRK_DT) ||'일 ' || MOD(TRUNC((SYSDATE-E.MILL_WRK_DT)*24),24)||'시 '||MOD(TRUNC((SYSDATE-E.MILL_WRK_DT)*24*60),60)||'분') AS COIL_CREATE_DDTT
				             , A.COIL_W
				             , S.YD_COIL_OUTDIA_GRP_GP
				          FROM TB_PT_COILCOMM A
				             , TB_YD_STKLYR   B
				             , TB_YD_STKCOL   C
				             , TB_HR_B_MILLWR E
				             , TB_YD_STOCK    S
				         WHERE B.YD_STK_COL_GP = C.YD_STK_COL_GP
				           AND C.YD_LOC_GP    = 'H'
				           AND A.COIL_NO      = B.STL_NO
				           AND A.COIL_NO      = E.COIL_NO
				           AND A.COIL_NO      = S.STL_NO
				           AND E.MILL_WRK_DT >= SYSDATE - 1 / 24 * 10 -- 압연 10시간 이내일 것
				           AND A.COIL_W      <= 1400                  -- 압연실적폭이 1,400mm 이하인 코일을 보급
				           AND A.CURR_PROG_CD = 'B'                   -- 진도코드 "B(작업지시대기)"
				           AND B.YD_STK_LYR_MTL_STAT = 'C'
				           AND (A.NEXT_PROC LIKE '%K' OR A.NEXT_PROC LIKE '%A')    -- SPM/공냉재
				           AND SUBSTR(B.YD_STK_COL_GP, 3, 2) BETWEEN '15' AND '17' --// 반송장 위치
				           AND SUBSTR(B.YD_STK_COL_GP, 2, 1) IN ('B','C')
				           AND C.YD_BAY_GP = :V_YD_BAY_GP
				           AND 'Y' = (SELECT ITEM1 FROM TB_YD_RULE WHERE REPR_CD_GP ='H00032') -- 결로재보급 ON
				           AND NOT EXISTS (SELECT 1 FROM TB_YD_WRKBOOKMTL C WHERE C.STL_NO = B.STL_NO AND C.DEL_YN = 'N')
				           AND B.DEL_YN = 'N'
				           AND C.DEL_YN = 'N'
				       ) AA
				 WHERE 1 = 1
				 */
				JDTORecordSet jsCond = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getConHotCoilIn", logId, mthdNm, "보급대상재 조회");
				
				jrParam.setField("YD_CHG_NO"   , jsCond.size()+""); //보급개수
				
				/*
				UPDATE TB_YD_STOCK
				   SET MODIFIER      = :V_MODIFIER
				     , MOD_DDTT      = SYSDATE
				     , YD_CHG_NO     = :V_YD_CHG_NO --보급대상개수
				     , TEMP9         = (SELECT COUNT(STL_NO) + 1 AS CNT 
				                          FROM TB_YD_STKLYR SL
				                             , TB_YD_RULE   R
				                         WHERE REPR_CD_GP = 'APP806' 
				                           AND SL.YD_STK_COL_GP = DTL_ITEM1
				                           AND SL.YD_STK_COL_GP LIKE 'J'||:V_YD_BAY_GP||'%'
				                           AND SL.YD_STK_BED_NO = R.DTL_ITEM2
				                           AND SL.YD_STK_LYR_NO = '001'
				                       ) --보급순번
				 WHERE STL_NO = :V_STL_NO
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updCondStat", logId, mthdNm, "저장품 저장");
			}
			
            /**********************************************
			 * HOT코일 보급 크레인스케줄 기동
			 *  - 크레인작업이 없을 때 작업예약에 있는것 기동
			 **********************************************/
            String sApp831 = coilDao.ApplyYn(logId, mthdNm, "APP831","J","*"); //HOT코일 보급 크레인스케줄 기동 여부
			commUtils.printLog(logId, "HOT코일 보급 크레인스케줄 기동여부 : " + sApp831, "SL");
			if ("Y".equals(sApp831)) {
				String ydBayGp = ydSchCd.substring(1, 2);
				
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_BAY_GP", ydBayGp);
				/*
				SELECT *
				  FROM (
				        SELECT YD_SCH_CD
				             , YD_WBOOK_ID
				             , (SELECT YD_WRK_CRN FROM TB_YD_SCHRULE B WHERE B.YD_SCH_CD = A.YD_SCH_CD) AS YD_EQP_ID
				          FROM TB_YD_WRKBOOK A
				         WHERE DEL_YN    = 'N'
				           AND YD_GP     = 'J'
				           AND YD_BAY_GP = :V_YD_BAY_GP
				           AND NOT EXISTS(SELECT 1 FROM TB_YD_CRNSCH B WHERE B.DEL_YN = 'N' AND B.YD_SCH_CD = A.YD_SCH_CD )
				           AND YD_SCH_CD LIKE 'J_HC02LM'  --핫코일 보급
				           AND 'Y' = CASE WHEN (SELECT COUNT(*) FROM TB_YD_CRNSCH
				                                 WHERE DEL_YN = 'N'
				                                   AND YD_SCH_CD LIKE 'J_HC02LM'
				                                   AND YD_BAY_GP = :V_YD_BAY_GP) > 0 THEN 'N'
				                          ELSE 'Y' END
				         ORDER BY YD_WBOOK_ID
				       ) AA
				 WHERE ROWNUM <= 1 
				 */
				JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getHotCoilSupChk", logId, mthdNm, "자동보급 대상 조회");	
				
				if (jsRst.size() > 0) {
					JDTORecord jrCrnSchMsg = commUtils.getParam(logId, mthdNm, sModifier);
					jrCrnSchMsg.setField("JMS_TC_CD"         , "YDYDJ551"); //
					jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrCrnSchMsg.setField("YD_SCH_CD"         , jsRst.getRecord(0).getFieldString("YD_SCH_CD"  )); 
					jrCrnSchMsg.setField("YD_EQP_ID"         , jsRst.getRecord(0).getFieldString("YD_EQP_ID"  ));
					jrCrnSchMsg.setField("YD_WBOOK_ID"       , jsRst.getRecord(0).getFieldString("YD_WBOOK_ID"));
					
					jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
					
					commUtils.printLog(logId, "다음 HOT코일 보급 작업예약 스케줄 기동", "SL");
				} else {
					commUtils.printLog(logId, "HOT코일 보급 작업예약 없음", "SL");
				}
			}
			
            /**********************************************
			 * H동 스크랩 자동이적(3열 -> 1,2열 이적)
			 **********************************************/
            String sApp305 = coilDao.ApplyYn(logId, mthdNm, "APP305", "J", "*");
			commUtils.printLog(logId, "스크랩 자동이적 기동여부 : "+ sApp305, "SL");
			if ("Y".equals(sApp305)) {
				String sMsg			= "";
				/*
				SELECT A.STL_NO
				     , A.YD_BAY_GP
				     , A.YD_STK_COL_GP
				     , A.YD_STK_BED_NO
				  FROM (
				        SELECT SL.STL_NO 
				             , SC.YD_BAY_GP
				             , SL.YD_STK_COL_GP
				             , SL.YD_STK_BED_NO
				             , ROW_NUMBER() OVER(PARTITION BY YD_BAY_GP ORDER BY YD_STK_LYR_NO DESC, YD_STK_BED_NO) AS SEQ
				             , (SELECT COUNT(*) FROM TB_YD_STKLYR A WHERE A.YD_STK_COL_GP = SL.YD_STK_COL_GP AND A.YD_STK_LYR_MTL_STAT = 'U') AS SCH_CNT
				          FROM TB_YD_STKLYR  SL
				             , TB_YD_STKCOL  SC
				         WHERE SC.YD_STK_COL_GP = SL.YD_STK_COL_GP
				           AND SC.YD_BAY_GP IN ('A','E','H')
				           AND SL.YD_STK_COL_GP LIKE 'J_SC03'
				           AND SL.STL_NO IS NOT NULL
				           AND SL.YD_STK_LYR_MTL_STAT = 'C'
				       ) A
				      ,(SELECT SC.YD_BAY_GP
				             , COUNT(*) AS CNT_E
				          FROM TB_YD_STKLYR  SL
				             , TB_YD_STKCOL  SC
				         WHERE SC.YD_STK_COL_GP = SL.YD_STK_COL_GP
				           AND SC.YD_BAY_GP IN ('A','E','H')
				           AND SC.YD_EQP_GP = 'SC'
				           AND SC.YD_STK_COL_NO IN ('01','02')
				           AND SL.YD_STK_LYR_NO = '001'
				           AND SL.YD_STK_LYR_MTL_STAT = 'E'
				         GROUP BY SC.YD_BAY_GP
				      ) B
				 WHERE SEQ     = 1 --작업예약 1개씩 만듬
				   AND SCH_CNT = 0 --스크랩 이적작업이 없는 동
				   AND A.YD_BAY_GP = B.YD_BAY_GP
				   AND CNT_E   > 0 
				 */
				JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getScrapMvstTrgtList", logId, mthdNm, "스크랩이적대상 조회");
				
				for ( int i = 0; i < jsRst.size(); i++ ) {
					
					String ydBayGp    = jsRst.getRecord(i).getFieldString("YD_BAY_GP");
					String sScrapNo   = jsRst.getRecord(i).getFieldString("STL_NO");
					
					String wrkYdSchCd = "J"+ ydBayGp +"SC01MH";
					
					// 스크랩 자동이적 동별 적용
					String sApp305Yn = coilDao.ApplyYn(logId, mthdNm, "APP305", "J", ydBayGp);
					
					if ("Y".equals(sApp305Yn)) {
						
						/*********************************
			    		 * 1. 작업예약 등록
			    		 *********************************/
						jrParam	= commUtils.getParam(logId, mthdNm, sModifier);
						jrParam.setField("YD_SCH_CD"			, wrkYdSchCd);
						jrParam.setField("STL_SH"				, "1"		);
						jrParam.setField("STL_NO1"				, sScrapNo	);
						jrParam.setField("YD_TO_LOC_DCSN_MTD"	, "F"		);
						jrParam.setField("YD_AIM_YD_GP"			, "J"		);
						jrParam.setField("YD_AIM_BAY_GP"		, ydBayGp	);

						EJBConnector ejbConn = new EJBConnector("default", "CCoilWrkBookSeEJB", this);
						JDTORecord jrWrkBook = (JDTORecord)ejbConn.trx("insWrkBookTx", new Class[] { JDTORecord.class }, new Object[] { jrParam });

						String rtnCd	= commUtils.nvl(jrWrkBook.getFieldString("RTN_CD"), "0");
						String rtnMsg	= commUtils.nvl(jrWrkBook.getFieldString("RTN_MSG"), "");
						ydWbookId	 	= commUtils.nvl(jrWrkBook.getFieldString("YD_WBOOK_ID"), "");

						if( !"1".equals(rtnCd) ) {
							sMsg = "작업예약생성 실패 : 스크랩 자동이적 STL_NO["+ sStlNo +"] RTN_MSG:"+ rtnMsg;
				    		commUtils.printLog(logId, sMsg, "SL");

				    		jrRtn.setField("RTN_CD"	, "0");
				    		jrRtn.setField("RTN_MSG", sMsg);
						}
						
						JDTORecord jrCrnSchMsg = commUtils.getParam(logId, mthdNm, sModifier);
						jrCrnSchMsg.setField("JMS_TC_CD"         , "YDYDJ551"); //
						jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
						jrCrnSchMsg.setField("YD_WBOOK_ID"       , ydWbookId);
						
						jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
						
						sMsg = " >> "+ ydBayGp +"동 스크랩 자동이적 스케쥴 기동! 작업예약ID ["+ ydWbookId +"]";
						commUtils.printLog(logId, sMsg, "SL");
					}
				}
			}
			
			/**********************************************
			 * 더미스케줄과 중복 스케줄 존재 시 자동 취소
			 **********************************************/
			String sAPP022_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "022");
			commUtils.printLog(logId,  "==========[[[ APP001 더미스케줄과 중복 스케줄 존재 시 자동 취소 : "+ sAPP022_YN +" ]]]============", "SL");
			if ("Y".equals(sAPP022_YN)) {
				
				jrParam	= commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("STL_NO",	  sStlNo); 
				JDTORecordSet jsRotentionInfo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getStlNoSchInfo", logId, mthdNm, "보급존적치율및 트레킹정보 조회");
				
				//스케줄 중복 여부 체크(W, S 상태)					
				if(jsRotentionInfo.size() > 0 ) {		
					ydWbookId     = commUtils.nvl(jsRotentionInfo.getRecord(0).getFieldString("YD_WBOOK_ID"), "0");
					ydCrnSchId    = commUtils.nvl(jsRotentionInfo.getRecord(0).getFieldString("YD_CRN_SCH_ID"), "0");
					ydEqpId       = commUtils.nvl(jsRotentionInfo.getRecord(0).getFieldString("YD_EQP_ID"), "0");
					ydSchCd       = commUtils.nvl(jsRotentionInfo.getRecord(0).getFieldString("YD_SCH_CD"), "0");
					
					//스케줄 취소
					commUtils.printLog(logId, "스케쥴취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");
					
					jrParam.setField("YD_WBOOK_ID"  	, ydWbookId );
					jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
					jrParam.setField("YD_EQP_ID"    	, ydEqpId   );
					jrParam.setField("YD_SCH_CD"    	, ydSchCd   );
					jrParam.setField("YD_L2_RETURN_FLAG", "Y"       );
					jrParam.setField("IS_LAST_SELECTED"	, "1");
					jrParam.setField("IS_SCH_MTL"    	, "Y"); // 스케줄 단위 취소

					/**********************************************************
					* 1. 크레인스케줄 취소
					**********************************************************/
					EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
					JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });

					jrRtn = commUtils.addSndData(jrRtn, jrRst);
				}
			}

			jrRtn.setField("RTN_CD"	, "1");
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "CCommSeEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { coilDao.getYDY5L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	
    /**
     * 오퍼레이션명 : 차량 작업진행관리 - 크레인 권하시 호출
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public JDTORecord procY5CarWrkStatCtrCoil(JDTORecord rcvMsg) throws DAOException {
    	String mthdNm   = "차량 작업진행관리[CCoilL2RcvSeEJB.procY5CarWrkStatCtrCoil] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();	//전문 Return
			
			String ydCrnSchId   = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID" ));
			String ydWbookId    = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"   ));
			String sCarLdudGp   = commUtils.trim(rcvMsg.getFieldString("CAR_LDUD_GP"   )); //U 권하위치 차량 L 권상위치 차량
			String ydDnWrLoc    = commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LOC"  ));
			String ydUpWrLoc    = commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LOC"  ));
			String sStlAppearGp = commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP" ));
			String sStlNo       = commUtils.trim(rcvMsg.getFieldString("STL_NO"        ));
			String ydSchCd      = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"     ));
			
			String sModifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //수정자(Backup Only)
			String sPI_YD       = commUtils.nvl(rcvMsg.getFieldString("PI_YD"),"*");			
			// PIDEV			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "CCoilL2RcvSeEJBBean => procY5CarWrkStatCtrCoil", "APPPI0", sPI_YD, "*");
			
//			if("PIDEV".equals("PIDEV")) {
				jrRtn = procY5CarWrkStatCtrCoil_PIDEV(rcvMsg);
				return jrRtn;
//			}
//			
//			JDTORecord jrCarMv  = commUtils.getParam(logId, mthdNm, sModifier);
//        	jrCarMv.setField("YD_CRN_SCH_ID" , ydCrnSchId  );
//        	jrCarMv.setField("YD_WBOOK_ID"   , ydWbookId   );
//        	jrCarMv.setField("CAR_LDUD_GP"   , sCarLdudGp  );
//        	jrCarMv.setField("YD_DN_WR_LOC"  , ydDnWrLoc   );
//        	jrCarMv.setField("YD_UP_WR_LOC"  , ydUpWrLoc   );
//        	jrCarMv.setField("STL_NO"        , sStlNo      );
//        	jrCarMv.setField("STL_APPEAR_GP" , sStlAppearGp); //재료외형구분 E;소재 , Y:제품
//        	
//        	jrCarMv.setField("YD_STK_COL_GP" , ydDnWrLoc.substring(0, 6));
//        	
//
//        	String ydCarSchId          = "";
//        	String ydCarUseGp          = "";
//        	String sTransEquipmentType = "";
//        	
//        	/***********************************
//        	 * 상차
//        	 ***********************************/
//        	if ("U".equals(sCarLdudGp)) {
//        		commUtils.printLog(logId, "상차", "SL");
//        		
//	        	/*************************************
//				 * 상차완료 유무 체크
//				 *************************************/
//	        	/*
//				SELECT YD_CAR_SCH_ID
//				     , YD_CAR_USE_GP 
//				     , CAR_NO
//				     , TRANS_ORD_DATE
//				     , TRANS_ORD_SEQNO
//				     , DEST_TEL_NO
//				     , YD_STK_COL_GP
//				     , WLOC_CD
//				     , YD_PNT_CD
//				     , YD_CAR_PROG_STAT  -- 차량진행
//				     , BAY_WRK_CNT       -- 해당상차도 작업대상
//				     , BAY_WRK_END_CNT   -- 해당 상차도 작업완료
//				     , CASE WHEN BAY_WRK_CNT <= BAY_WRK_END_CNT THEN 'Y' ELSE'N' END AS CAR_LD_CMPL_YN
//				     , TRANS_EQUIPMENT_TYPE    
//				     , CMBN_CARLD_YN 
//				  FROM
//				       (
//				        SELECT TS.YD_CAR_SCH_ID
//				             , TS.YD_CAR_USE_GP
//				             , TS.CAR_NO
//				             , TS.TRANS_ORD_DATE
//				             , TS.TRANS_ORD_SEQNO
//				             , TS.DEST_TEL_NO
//				             , TS.YD_CAR_PROG_STAT
//				             , TS.TRANS_EQUIPMENT_TYPE
//				             , TS.CMBN_CARLD_YN
//				             , SC.YD_STK_COL_GP
//				             , SC.WLOC_CD
//				             , SC.YD_PNT_CD
//				             -- 해당상차도 작업대상 건수   
//				             , CASE WHEN WB.YD_CAR_USE_GP = 'L' THEN 
//				                    -- 구내운송
//				                         (SELECT COUNT(DISTINCT(A.STL_NO)) 
//				                            FROM TB_YD_STOCK  A
//				                               , TB_YD_STKLYR B 
//				                           WHERE A.STL_NO               = B.STL_NO
//				                             AND A.CAR_FRTOMOVE_WORD_NO = TS.FRTOMOVE_WORD_NO
//				                             AND SUBSTR(B.YD_STK_COL_GP,1,2) = SUBSTR(SC.YD_STK_COL_GP,1,2))
//				                             
//				                    ELSE  
//				                     -- 출하
//				                        (SELECT COUNT(DISTINCT(B.STL_NO)) 
//				                           FROM TB_YD_STOCK    B
//				                              , TB_YD_CARPOINT C
//				                              , TB_YD_STKLYR   D
//				                          WHERE B.TRANS_ORD_DATE  = TS.TRANS_ORD_DATE
//				                            AND B.TRANS_ORD_SEQNO = TS.TRANS_ORD_SEQNO
//				                            AND C.YD_STK_COL_GP   = SC.YD_STK_COL_GP
//				                            AND C.DEL_YN    = 'N'
//				                            AND B.STL_NO    = D.STL_NO
//				                            AND C.YD_BAY_GP = SUBSTR(D.YD_STK_COL_GP, 2, 1)
//				                            AND SUBSTR(B.YD_STK_COL_GP, 3, 2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
//				                        )
//				                     END  
//				               AS BAY_WRK_CNT    
//				               
//				             -- 해당상차도 작업완료 건수                 
//				             , CASE WHEN WB.YD_CAR_USE_GP = 'L' THEN 
//				                    -- 구내운송
//				                         (SELECT COUNT(DISTINCT(A.STL_NO))
//				                            FROM TB_YD_STKLYR A
//				                           WHERE ((SUBSTR(A.YD_STK_COL_GP,3,2) IN ('PT') AND YD_STK_LYR_MTL_STAT = 'C'))
//				                             AND SUBSTR(A.YD_STK_COL_GP,1,2) = SUBSTR(SC.YD_STK_COL_GP,1,2)
//				                             AND A.STL_NO IN (SELECT B.STL_NO
//				                                                FROM TB_YD_STOCK B
//				                                               WHERE B.CAR_FRTOMOVE_WORD_NO = TS.FRTOMOVE_WORD_NO))              
//				                    ELSE 
//				                    -- 출하
//				                         (SELECT COUNT(DISTINCT(A.COIL_NO))
//				                            FROM TB_PT_COILCOMM A
//				                           WHERE SUBSTR(A.YD_STR_LOC,3,2) IN ('PT') 
//				                             AND A.COIL_NO  IN (SELECT B.STL_NO
//				                                                  FROM TB_YD_STOCK    B
//				                                                     , TB_YD_CARPOINT C
//				                                                     , TB_YD_STKLYR   D
//				                                                 WHERE B.TRANS_ORD_DATE  = TS.TRANS_ORD_DATE
//				                                                   AND B.TRANS_ORD_SEQNO = TS.TRANS_ORD_SEQNO
//				                                                   AND C.YD_STK_COL_GP   = SC.YD_STK_COL_GP
//				                                                   AND C.DEL_YN    = 'N'
//				                                                   AND B.STL_NO    = D.STL_NO
//				                                                   AND C.YD_BAY_GP = SUBSTR(D.YD_STK_COL_GP, 2, 1)
//				                                                   AND SUBSTR(B.YD_STK_COL_GP, 3, 2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
//				                                                 )
//				                         )   
//				                    END  
//				               AS BAY_WRK_END_CNT   
//				          FROM TB_YD_STKCOL SC
//				             , TB_YD_CARSCH TS
//				             , (SELECT TRN_EQP_CD , CAR_NO , CARD_NO ,YD_CAR_USE_GP, SUBSTR(YD_DN_WO_LOC,1,6) AS YD_DN_WO_LOC
//				                  FROM TB_YD_CRNSCH  A
//				                     , TB_YD_WRKBOOK B
//				                 WHERE A.YD_WBOOK_ID   = B.YD_WBOOK_ID
//				                   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
//				               ) WB
//				         WHERE SC.YD_STK_COL_GP = WB.YD_DN_WO_LOC
//				           AND SC.YD_STK_COL_GP = :V_YD_STK_COL_GP
//				           AND ((WB.YD_CAR_USE_GP = 'L' AND WB.TRN_EQP_CD = SC.TRN_EQP_CD) --구내운송
//				             OR (WB.YD_CAR_USE_GP = 'G' AND WB.CARD_NO    = SC.CARD_NO AND WB.CAR_NO=SC.CAR_NO)) --출하차량
//				           AND ((WB.YD_CAR_USE_GP = 'L' AND WB.TRN_EQP_CD = TS.TRN_EQP_CD) --구내운송
//				             OR (WB.YD_CAR_USE_GP = 'G' AND WB.CARD_NO    = TS.CARD_NO AND WB.CAR_NO=TS.CAR_NO)) --출하차량  
//				           AND TS.DEL_YN = 'N'
//				         ORDER BY  TS.YD_CAR_SCH_ID DESC  
//				       ) 
//				 WHERE ROWNUM <= 1   
//	        	 */
//	        	JDTORecordSet jsCrnSch = commDao.select(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarSchLd", logId, mthdNm, "상차완료 여부 조회");
//	        	
//	        	String sCarLdCmplYn        = "N";
//	        	        	
//	        	if (jsCrnSch.size() > 0) {
//	        		sCarLdCmplYn        = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("CAR_LD_CMPL_YN"      )); //상차완료여부
//	        		ydCarSchId          = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CAR_SCH_ID"       )); 	               
//	        		ydCarUseGp          = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CAR_USE_GP"       )); 	               
//	        		sTransEquipmentType = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("TRANS_EQUIPMENT_TYPE")); //운송장비TYPE P:PDA
//	        	}
//	        	
//	        	jrCarMv.setField("YD_CARUD_WRK_BOOK_ID"   , ydWbookId);
//	        	jrCarMv.setField("YD_CARLD_WRK_BOOK_ID"   , ydWbookId);
//	        	
//	        	commUtils.printLog(logId, "상차완료 유무 : " + sCarLdCmplYn, "SL");
//	        	
//	    		/*
//	    		 * 제품이송시에 검수확인 필요
//	    		 */
//	    		String sApp803 = coilDao.ApplyYn(logId, mthdNm, "APP803", "J", "*");
//		    	
//	    		commUtils.printLog(logId, "제품이송 검수 등록 여부 : " + sApp803, "SL");
//	    		/* ***************************
//	    		 * 검수 테이블 저장
//	    		 * ***************************/
//	    		if ("G".equals(ydCarUseGp) && "P".equals(sTransEquipmentType) && "Y".equals(sApp803)) { 
//	    			
//	    			jrCarMv = commUtils.getParam(logId, mthdNm, sModifier);
//	    			jrCarMv.setField("STL_NO",   sStlNo);
//	    			/*
//					INSERT INTO TB_YD_EXAMINATIONCHKLIST (
//					  TRANS_ORD_DATE
//					, TRANS_ORD_SEQNO
//					, STL_NO
//					, YD_GP
//					, CAR_NO
//					, CARD_NO
//					, GATE_NM
//					, BAY_GP
//					, YD_CARPNT_CD
//					, YD_CAR_UPP_LOC_CD
//					)
//					SELECT *
//					  FROM (
//					        SELECT D.TRANS_ORD_DATE
//					             , D.TRANS_ORD_SEQNO
//					             , D.STL_NO
//					             , NVL(A.YD_GP, D.YD_AIM_YD_GP) 
//					             , D.CAR_NO 
//					             , D.CARD_NO
//					             , A.GATE_NAME
//					             , A.BAY_GP 
//					             , NVL(G.CARLD_PNT_CD, A.YD_CARPNT_CD) AS CARLD_PNT_CD
//					             , D.YD_CAR_UPP_LOC_CD
//					         FROM (SELECT YD_GP
//					                    , YD_BAY_GP AS BAY_GP
//					                    , YD_STK_COL_GP AS STACK_COL_GP
//					                    , YD_CARPNT_DESC AS GATE_NAME
//					                    , CARD_NO
//					                    , YD_CARPNT_CD
//					                 FROM TB_YD_CARPOINT
//					                WHERE YD_GP IN ('J','H')
//					                  AND DEL_YN = 'N'
//					              ) A
//					            , TB_YD_STOCK  D
//					            , TB_DM_TRANSWORDGOODS @DL_SMDB G
//					        WHERE D.CARD_NO = A.CARD_NO(+)    
//					          AND D.STL_NO  = :V_STL_NO
//					          AND D.TRANS_ORD_DATE  = G.TRANS_WORD_DATE(+)
//					          AND D.TRANS_ORD_SEQNO = G.TRANS_WORD_SEQNO(+)
//					          AND D.STL_NO = G.GOODS_NO(+) 
//					          AND NOT EXISTS (
//					                          SELECT 1
//					                            FROM TB_YD_EXAMINATIONCHKLIST K
//					                           WHERE K.TRANS_ORD_DATE  = D.TRANS_ORD_DATE
//					                             AND K.TRANS_ORD_SEQNO = D.TRANS_ORD_SEQNO
//					                             AND K.STL_NO = D.STL_NO)
//					          AND ROWNUM <= 1
//					       ) A
//					 WHERE TRANS_ORD_DATE IS NOT NULL
//	    			 */
//	    			commDao.insert(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insExaminationChkList", logId, mthdNm, "검수DATA등록");
//	    		}
//	    		
//	    		commUtils.printLog(logId, "상차완료 여부 : " + sCarLdCmplYn, "SL");
//        		
//        		/******************
//        		 * 상차 완료
//        		 ******************/
//        		if ("Y".equals(sCarLdCmplYn)) {
//        			
//        			jrCarMv = commUtils.getParam(logId, mthdNm, sModifier);
//        			jrCarMv.setField("YD_CAR_PROG_STAT"    , "5"      );
//	    			jrCarMv.setField("YD_EQP_WRK_STAT"     , "L"      );
//	    			jrCarMv.setField("YD_CAR_SCH_ID"       , ydCarSchId);
//	    			jrCarMv.setField("YD_CARLD_CMPL_DT"    , commUtils.getDateTime14());
//	    			/*
//					UPDATE TB_YD_CARSCH
//					   SET MOD_DDTT = SYSDATE
//					     , MODIFIER = :V_MODIFIER
//					     , YD_CAR_PROG_STAT = NVL(:V_YD_CAR_PROG_STAT, YD_CAR_PROG_STAT)
//					     , YD_CARLD_ST_DT   = DECODE(NVL(:V_YD_CARLD_ST_DT  ,'NULL'),'NULL',YD_CARLD_ST_DT  ,SYSDATE)
//					     , YD_CARLD_CMPL_DT = DECODE(NVL(:V_YD_CARLD_CMPL_DT,'NULL'),'NULL',YD_CARLD_CMPL_DT,SYSDATE)
//					     , YD_CARUD_ST_DT   = DECODE(NVL(:V_YD_CARUD_ST_DT  ,'NULL'),'NULL',YD_CARUD_ST_DT  ,SYSDATE)
//					     , YD_CARUD_CMPL_DT = DECODE(NVL(:V_YD_CARUD_CMPL_DT,'NULL'),'NULL',YD_CARUD_CMPL_DT,SYSDATE)
//					     , YD_EQP_WRK_STAT  = NVL(:V_YD_EQP_WRK_STAT, YD_EQP_WRK_STAT)
//					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID 
//	    			 */
//	    			commDao.update(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updMvCarSchCmpl", logId, mthdNm, "상차완료 등록");
//        			
//	    			if ("L".equals(ydCarUseGp)) {	//구내운송
//	    				
//	    				/************************************
//	    				 * 상차작업완료 송신YDTSJ008
//	    				 ************************************/
//	    				JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
//	    				jrYdMsg.setField("JMS_TC_CD"    , "YDTSJ008");
//	    				jrYdMsg.setField("YD_SCH_CD"    , ydSchCd   );
//	    	        	jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
//
//	    				jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDTSJ008", jrYdMsg));
//	    				
//	    				/* **************************
//	    				 * AB재료정보생성
//	    				 * **************************/
//	    				/*
//						SELECT STL_NO
//						     , MATL_FTMV_WO_DT AS TRS_INDI_DT -- 이송지시
//						     , SPOS_WLOC_CD
//						     , ARR_WLOC_CD
//						     , MTL_UGNT_GP AS URGENT_FRTOMOVE_WORD_GP -- 긴급구분
//						  FROM TB_TS_MATL_FTMV_WO 
//						 WHERE STL_NO            = :V_STL_NO
//						   AND TS_MATL_FTMV_STAT_GP     = '1'
//						   AND MATL_FTMV_WO_NML_HD_YN   = 'Y'
//            			 */
//            			JDTORecordSet jsWbook = commDao.select(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getTsMtlArrWloc", logId, mthdNm, "작업예약조회");
//            			String sArrWlocCd = "";
//            			if (jsWbook.size() > 0) {
//            				sArrWlocCd = commUtils.trim(jsWbook.getRecord(0).getFieldString("ARR_WLOC_CD")); 
//            			}
//            			
//            			if ("D3Y41".equals(sArrWlocCd) || "D3Y42".equals(sArrWlocCd)) { //1열연
//            				/*
//	    					MERGE INTO TB_YM_STOCK A
//	    					USING (
//	    					        SELECT B.STL_NO    AS STOCK_ID
//	    					             , '2'         AS STOCK_STAT
//	    					             , 'EC'        AS STOCK_MOVE_TERM
//	    					             , 'N'         AS DEL_YN
//	    					             , :V_MODIFIER AS MODIFIER
//	    					             , SYSDATE     AS MOD_DDTT
//	    					             , A.TRANS_ORD_DATE || A.TRANS_ORD_SEQNO AS TRANS_WORD_NO
//	    					             , (SELECT DECODE(STL_APPEAR_GP, 'Y', 'CG', 'CM')
//	    					                  FROM TB_PT_COILCOMM
//	    					                 WHERE COIL_NO = B.STL_NO
//	    					               ) AS STOCK_ITEM
//	    					          FROM TB_YD_CARSCH A
//	    					             , TB_YD_CARFTMVMTL B
//	    					             , TB_YD_STOCK C
//	    					         WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
//	    					           AND A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
//	    					           AND B.STL_NO        = C.STL_NO
//	    					           AND A.DEL_YN        = 'N'
//	    					      ) B
//	    					   ON ( A.STOCK_ID = B.STOCK_ID )
//	    					 WHEN MATCHED
//	    					         THEN UPDATE
//	    					          SET A.TRANS_WORD_NO = B.TRANS_WORD_NO
//	    					            , A.DEL_YN        = B.DEL_YN
//	    					            , A.MODIFIER      = B.MODIFIER
//	    					            , A.MOD_DDTT      = SYSDATE
//	    					 WHEN NOT MATCHED
//	    					 THEN INSERT(
//	    					              STOCK_ID
//	    					            , STOCK_ITEM
//	    					            , STOCK_STAT
//	    					            , STOCK_MOVE_TERM
//	    					            , TRANS_WORD_NO
//	    					            , DEL_YN
//	    					            , REGISTER
//	    					            , REG_DDTT
//	    					            , MODIFIER
//	    					            , MOD_DDTT
//	    					            )
//	    					      VALUES(
//	    					              B.STOCK_ID
//	    					            , B.STOCK_ITEM
//	    					            , B.STOCK_STAT
//	    					            , B.STOCK_MOVE_TERM
//	    					            , B.TRANS_WORD_NO
//	    					            , B.DEL_YN
//	    					            , B.MODIFIER
//	    					            , B.MOD_DDTT
//	    					            , B.MODIFIER
//	    					            , B.MOD_DDTT
//	    					            )
//	    					*/
//	    			    	commDao.update(jrYdMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYmStockCoilMerge", logId, mthdNm, "1열연 저장품정보 저장");
//            			}
//	    					
//            			if ("D2Y44".equals(sArrWlocCd) || "D2Y45".equals(sArrWlocCd)) {	 //박판
//	    					/*
//	    					MERGE INTO TB_YF_STOCK A
//	    					USING (
//	    					        SELECT B.STL_NO
//	    					             , (SELECT DECODE(C.STL_APPEAR_GP, 'Y', 'CG', 'CM')
//	    					                  FROM TB_PT_COILCOMM
//	    					                 WHERE COIL_NO = B.STL_NO
//	    					                )               AS STOCK_ITEM
//	    					             , C.TRANS_ORD_DATE
//	    					             , C.TRANS_ORD_SEQNO
//	    					             , '2'              AS YD_MTL_STAT
//	    					             , 'EC'             AS STOCK_MOVE_TERM
//	    					             , 'N'              AS DEL_YN
//	    					             , :V_MODIFIER      AS MODIFIER
//	    					             , SYSDATE          AS MOD_DDTT
//	    					          FROM TB_YD_CARSCH     A
//	    					             , TB_YD_CARFTMVMTL B
//	    					             , TB_YD_STOCK      C
//	    					         WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
//	    					           AND A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
//	    					           AND B.STL_NO        = C.STL_NO
//	    	                           AND B.DEL_YN        = 'N'
//	    					      ) B
//	    					   ON ( A.STL_NO = B.STL_NO )
//	    					 WHEN MATCHED
//	    					      THEN UPDATE
//	    					              SET A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
//	    					                , A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
//	    					                , A.MODIFIER        = B.MODIFIER
//	    					                , A.MOD_DDTT        = B.MOD_DDTT
//	    					 WHEN NOT MATCHED
//	    					      THEN INSERT (
//	    					                    STL_NO
//	    					                  , STOCK_ITEM
//	    					                  , TRANS_ORD_DATE
//	    					                  , TRANS_ORD_SEQNO
//	    					                  , YD_MTL_STAT
//	    					                  , STOCK_MOVE_TERM
//	    					                  , DEL_YN
//	    					                  , REGISTER
//	    					                  , REG_DDTT
//	    					                  , MODIFIER
//	    					                  , MOD_DDTT
//	    					                  )
//	    					           VALUES (
//	    					                    B.STL_NO
//	    					                  , B.STOCK_ITEM
//	    					                  , B.TRANS_ORD_DATE
//	    					                  , B.TRANS_ORD_SEQNO
//	    					                  , B.YD_MTL_STAT
//	    					                  , B.STOCK_MOVE_TERM
//	    					                  , B.DEL_YN
//	    					                  , B.MODIFIER
//	    					                  , B.MOD_DDTT
//	    					                  , B.MODIFIER
//	    					                  , B.MOD_DDTT
//	    					                  )
//	    					*/
//	    			    	commDao.update(jrYdMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYfStockCoilMerge", logId, mthdNm, "박판열연 저장품정보 저장");
//            			}
//	    			
//	    			} else if ("P".equals(sTransEquipmentType)) {					//출하PDA
//	    				
//    					/***********************************
//    					 * 코일제품이송 상차완료 전송PDA
//    					 ***********************************/
//    					commUtils.printLog(logId, "[YDDMR073]코일제품이송 상차완료 전문 송신", "SL");
//    					JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
//    					sndL2Msg.setField("JMS_TC_CD"    , "YDDMR073"); 
//    					sndL2Msg.setField("YD_CAR_SCH_ID", ydCarSchId);
//						
//    					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR073", sndL2Msg));
//    				
//    					String sApp810 = coilDao.ApplyYn(logId, mthdNm, "APP810","J","*"); //
//    					commUtils.printLog(logId, "공냉재 소재통로 검수완료전문 자동 송신 : " + sApp810, "SL");
//    					
//    					if ("Y".equals(sApp810)) {
//        					/***************************************
//    						 * 공냉재 소재통로 이송시 검수완료 전문 송신 
//    						 ***************************************/
//        					/*
//    			    		SELECT CS.TRANS_ORD_DATE  AS TRANS_WORD_DATE
//    						     , CS.TRANS_ORD_SEQNO AS TRANS_WORD_SEQNO
//    						     , CS.CAR_NO
//    						     , ST.STL_NO          AS GOODS_NO
//    						     , ST.CR_FRTOMOVE_GP
//    						     , COUNT(*) OVER (PARTITION BY CS.TRANS_ORD_DATE, CS.TRANS_ORD_SEQNO) AS GOODS_NO_CNT
//    						     , '' AS GOODS_CHK_AB_CD
//    						     , '' AS LABEL_REISSUE_YN
//    						     , ST.YD_CAR_UPP_LOC_CD
//    						  FROM TB_PT_COILCOMM  CC
//    						     , TB_YD_CARSCH    CS
//    						     , TB_YD_STOCK     ST
//    						 WHERE CC.COIL_NO         = ST.STL_NO
//    						   AND CS.TRANS_ORD_DATE  = ST.TRANS_ORD_DATE  
//    						   AND CS.TRANS_ORD_SEQNO = ST.TRANS_ORD_SEQNO 
//    						   AND CS.YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
//    						   AND CS.YD_CARLD_STOP_LOC LIKE 'J_PT03' --소재통로
//    						   AND CS.DEL_YN = 'N'
//    						   AND (SUBSTR(CC.NEXT_PROC,2,1) IN ('A')  
//    						        OR EXISTS (SELECT 1
//    						              FROM TB_HR_C_SHEARWOWR SR
//    						             WHERE SR.HR_PLNT_GP = 'C'
//    						               AND SR.WORK_STAT  = '*'
//    						               AND SR.WORD_PROC  LIKE '%A'
//    						               AND SR.COIL_NO = ST.STL_NO
//    						               AND SR.RECEIPT_HOLD_SCRAP_CAUSE_GP IN ('I','B')
//    						               AND SR.STEP_NO = (SELECT MAX(STEP_NO)
//    						                                   FROM TB_HR_C_SHEARWOWR
//    						                                   WHERE COIL_NO = SR.COIL_NO) 
//    						                )
//    						        )   
//    			    		*/
//    						JDTORecordSet jsAirClList = commDao.select(sndL2Msg, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getAirclMtlYn", logId, mthdNm, "검수완료전문데이터");
//    						
//    						if (jsAirClList.size() > 0) {
//        			    		
//        						JDTORecord jrTcInfo = JDTORecordFactory.getInstance().create();
//        		    			jrTcInfo.setField("JMS_TC_CD"			, "YDDMR074");
//        		    			jrTcInfo.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
//        		    			jrTcInfo.setField("TC_CODE"			    , "YDDMR074");
//        		    			jrTcInfo.setField("TC_CREATE_DDTT"	    , commUtils.getDateTime14());
//        		    			jrTcInfo.setField("TRANS_WORD_DATE"		, jsAirClList.getRecord(0).getFieldString("TRANS_WORD_DATE"));
//        		    			jrTcInfo.setField("TRANS_WORD_SEQNO"	, jsAirClList.getRecord(0).getFieldString("TRANS_WORD_SEQNO"));
//        		    			jrTcInfo.setField("CARLD_CHK_DONE_DATE"	, commUtils.getDate8()); // yyyyMMdd
//        		    			jrTcInfo.setField("CARLD_CHK_DONE_TIME"	, commUtils.getTime6()); // HHmmss
//        		    			jrTcInfo.setField("CAR_NO"				, jsAirClList.getRecord(0).getFieldString("CAR_NO"));
//
//        						JDTORecord jrTcList = JDTORecordFactory.getInstance().create();
//        						for (int Loop_i = 1; Loop_i <= jsAirClList.size(); Loop_i++) {
//        							jsAirClList.absolute(Loop_i);
//        			    			jrTcList = jsAirClList.getRecord();
//        			    			
//        			    			if( Loop_i == 1 )
//        			    				jrTcInfo.setField("GOODS_NO_CNT", commUtils.nvl(jrTcList.getFieldString("GOODS_NO_CNT"), "0"));
//    	    			    			jrTcInfo.setField("GOODS_NO"		+ Loop_i, commUtils.trim(jrTcList.getFieldString("GOODS_NO")));
//    	    			    			jrTcInfo.setField("GOODS_CHK_AB_CD"	+ Loop_i, commUtils.trim(jrTcList.getFieldString("GOODS_CHK_AB_CD")));
//    	    			    			jrTcInfo.setField("LABEL_REISSUE_YN"+ Loop_i, commUtils.trim(jrTcList.getFieldString("LABEL_REISSUE_YN")));
//    	    			    			jrTcInfo.setField("GDS_CARLD_LOC"	+ Loop_i, commUtils.trim(jrTcList.getFieldString("YD_CAR_UPP_LOC_CD")));
//        						}
//
//        						// 마지막 CR_FRTOMOVE_GP 값
//        						jrTcInfo.setField("CR_FRTOMOVE_GP", commUtils.trim(jrTcList.getFieldString("CR_FRTOMOVE_GP")));
//        						jrRtn = commUtils.addSndData(jrRtn, jrTcInfo);
//        					}
//    					} // end app
//	    			}
//        			
//	    			/**********************************
//	    			 * 출하관리
//	    			 **********************************/
//	    			if ("G".equals(ydCarUseGp)) {
//	    				
//	    				jrCarMv = commUtils.getParam(logId, mthdNm, sModifier);
//	    				jrCarMv.setField("YD_CAR_SCH_ID", ydCarSchId);
//	    				jrCarMv.setField("COIL_NO"      , sStlNo    );
//	    				jrCarMv.setField("STL_NO"       , sStlNo    );
//	    				
//	    				/****************************
//	    				 *  임가공 대상재 조회
//	    				 ****************************/
//	    				/*
//						SELECT COIL_NO AS STL_NO 
//						  FROM TB_DM_COILFRTOMOVEWORDDETAIL @DL_SMDB
//						 WHERE 1 = 1
//						   AND COIL_NO = :V_COIL_NO
//						   AND DEL_YN  = 'N'
//						   AND FRTOMOVE_WORD_SEQNO >= 700000
//	    				 */
//	    				JDTORecordSet jsRent = commDao.select(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getRentprocFtmvWoTrgt", logId, mthdNm, "임가공대상재 조회");
//	    				
//	    				if (jsRent.size() > 0) {
//	    					commUtils.printLog(logId, "임가공대상재 존재", "SL");
//
//	    					/* *********************
//    		            	 * 복수상차 처리로직
//    		            	 * *********************/
//    						JDTORecord jrCmbnCarldYn = this.procCmbnCarldYn(jrCarMv);
//    						jrRtn = commUtils.addSndData(jrRtn, jrCmbnCarldYn);
//    						
//    						String sRtn = jrCmbnCarldYn.getFieldString("RTN_CD");
//    						
//    						commUtils.printLog(logId, "복수상차 처리 로직 정상 여부 : " + sRtn, "SL");
//    						
//    						if ("2".equals(sRtn)) { //조합상차 해당없을 경우
//    							/*****************************
//        						 * 임가공이송상하차완료
//        						 *****************************/
//        						commUtils.printLog(logId, "[YDDMR022]임가공이송상하차완료 전문 송신", "SL");
//        						JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
//    	    					sndL2Msg.setField("JMS_TC_CD"     , "YDDMR022"); 
//    	    					sndL2Msg.setField("YD_CAR_SCH_ID" , ydCarSchId);
//    							
//    	    					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR022", sndL2Msg));
//    	    					
//    	    					String sAPP819_YN = coilDao.ApplyYn(logId, mthdNm, "APP819", "J", "*"); //임가공 권하 맵Clear
//    	    					
//    	    					if ("N".equals(sAPP819_YN)) {
//    		    					//차량 자동출발 처리
//    		    					JDTORecordSet jsCarSch = commDao.select(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCarsch", logId, mthdNm, "차량스케줄 조회");
//    		    					if (jsCarSch.size() > 0) {
//    		    					
//    		    						jsCarSch.first();
//    		    						
//    		    						/**********************************************************
//    		    						* 코일출하차량 출발처리 - 맵비활성화
//    		    						**********************************************************/
//    		    						JDTORecord jrYdDmMsg = commUtils.getParam(logId, mthdNm, sModifier);
//    									jrYdDmMsg.setField("CARD_NO"        , commUtils.trim(jsCarSch.getRecord().getFieldString("CARD_NO"        )));
//    									jrYdDmMsg.setField("CAR_NO"         , commUtils.trim(jsCarSch.getRecord().getFieldString("CAR_NO"         )));			
//    									jrYdDmMsg.setField("SPOS_WLOC_CD"   , commUtils.trim(jsCarSch.getRecord().getFieldString("SPOS_WLOC_CD"   )));
//    									jrYdDmMsg.setField("SPOS_YD_PNT_CD" , commUtils.trim(jsCarSch.getRecord().getFieldString("YD_PNT_CD1"     )));
//    									jrYdDmMsg.setField("TRANS_ORD_DT"   , commUtils.trim(jsCarSch.getRecord().getFieldString("TRANS_ORD_DATE" )));
//    									jrYdDmMsg.setField("TRANS_ORD_SEQNO", commUtils.trim(jsCarSch.getRecord().getFieldString("TRANS_ORD_SEQNO")));
//
//    									EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
//    									JDTORecord jrMsg = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { jrYdDmMsg });
//    									
//    									jrRtn = commUtils.addSndData(jrRtn, jrMsg);
//    		    					}	    						
//    	    					}
//    						}
//
//    					} else {
//    						// 출하차량 상차완료 인 경우(제품)
//    						commUtils.printLog(logId, "출하PDA 여부 : " +sTransEquipmentType  , "SL");
//	    					if (!"P".equals(sTransEquipmentType)) { //출하PDA
//
//	    						/* *********************
//	    		            	 * 복수상차 처리로직
//	    		            	 * *********************/
//	    						JDTORecord jrCmbnCarldYn = this.procCmbnCarldYn(jrCarMv);
//	    						jrRtn = commUtils.addSndData(jrRtn, jrCmbnCarldYn);
//	    						
//	    						String sRtn = jrCmbnCarldYn.getFieldString("RTN_CD");
//	    						
//	    						commUtils.printLog(logId, "복수상차 처리 로직 정상 여부 : " + sRtn, "SL");
//	    						
//	    						if ("2".equals(sRtn)) { //조합상차 해당없을 경우
//	    							/**************************************
//	    							 * 코일출하상차 완료
//	    							 **************************************/
//	    							commUtils.printLog(logId, "[YDDMR015]코일출하상차 완료 전문 송신", "SL");
//		    						JDTORecord jrYdDmMsg = commUtils.getParam(logId, mthdNm, sModifier);
//		    						jrYdDmMsg.setField("JMS_TC_CD"     , "YDDMR015"); 
//		    						jrYdDmMsg.setField("YD_CAR_SCH_ID" , ydCarSchId);
//									
//			    					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR015", jrYdDmMsg));
//	    							
//	    						}
//	    					} // 출하PDA
//	    				} //if (jsRent.size() > 0) {
//	    			} // if ("G".equals(ydCarUseGp)) { //출하관리
//        		} //if ("Y".equals(sSangchChk)) {
//        	} //상차
//		
//        	/***********************************
//        	 * 하차
//        	 ***********************************/
//        	if ("L".equals(sCarLdudGp)) {
//        		commUtils.printLog(logId, "하차", "SL");
//        		
//        		String sCarUdCmplYn = "N";
//        		/*
//				SELECT TS.YD_CAR_SCH_ID
//				     , DECODE((SELECT COUNT(*) 
//				                 FROM TB_YD_CARFTMVMTL 
//				                WHERE YD_CAR_SCH_ID = TS.YD_CAR_SCH_ID
//				                  AND DEL_YN = 'N'),1,'Y','N') AS CAR_UD_CMPL_YN --차량하차완료여부(권상실적에서 이송재료 삭제)
//				     , TS.CAR_NO
//				     , TS.ARR_WLOC_CD AS WLOC_CD
//				     , TS.YD_PNT_CD3  AS YD_PNT_CD
//				     , TS.TRANS_ORD_DATE
//				     , TS.TRANS_ORD_SEQNO
//				     , TS.TRANS_EQUIPMENT_TYPE  
//				     , TS.YD_CAR_USE_GP
//				     , TM.STL_NO
//				     , TS.YD_EQP_WRK_STAT
//				  FROM TB_YD_CARSCH     TS
//				     , TB_YD_CARFTMVMTL TM
//				     , TB_YD_CRNSCH     CS
//				 WHERE TS.YD_CAR_SCH_ID = TM.YD_CAR_SCH_ID
//				   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
//				   AND CS.YD_CRN_GRAB_USE_RULE_ID = TS.YD_CAR_SCH_ID
//    			 */
//    			JDTORecordSet jsCarUd = commDao.select(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarSchUd", logId, mthdNm, "하차완료 체크");
//    			if (jsCarUd.size() <= 0) {
//    				commUtils.printLog(logId, "차량스케줄 정보가 존재하지 않습니다.", "SL");
//    			} else {
//	        		jsCarUd.first();
//	        		sCarUdCmplYn        = commUtils.trim(jsCarUd.getRecord().getFieldString("CAR_UD_CMPL_YN")); //하차완료여부
//    				ydCarSchId          = commUtils.trim(jsCarUd.getRecord().getFieldString("YD_CAR_SCH_ID" )); 	               
//	        		ydCarUseGp          = commUtils.trim(jsCarUd.getRecord().getFieldString("YD_CAR_USE_GP" ));
//	        		sTransEquipmentType = commUtils.trim(jsCarUd.getRecord().getFieldString("TRANS_EQUIPMENT_TYPE"));
//    			}
//        		
//        		commUtils.printLog(logId, "하차작업 구내운송 차량여부 : " +ydCarUseGp + "소재이송여부 " + sStlAppearGp , "SL");
//        		
//            	/***********************************
//            	 * 코일이송일품상차실적 송신
//            	 ***********************************/
//    			commUtils.printLog(logId, "코일제품이송 여부 : " + sTransEquipmentType, "SL");
//
//            	if ("P".equals(sTransEquipmentType)) {
//    	        	JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
//    	        	jrYdMsg.setField("JMS_TC_CD"    , "YDDMR072");
//    	        	jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
//    				jrYdMsg.setField("STL_NO"		, sStlNo    );
//    				jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR072", jrYdMsg));
//            	}
//        		
//        		//구내운송 차량이고 소재이송인 경우에만 처리함
//        		if ("L".equals(ydCarUseGp) && !"Y".equals(sStlAppearGp)) {
//        			
//        			jrCarMv = commUtils.getParam(logId, mthdNm, sModifier);
//    				jrCarMv.setField("STL_NO" , sStlNo);
//    				/*
//					SELECT STL_NO
//					     , TRANSWORD_SEQNO
//					     , SPOS_WLOC_CD
//					     , ARR_WLOC_CD
//					  FROM USRPTA.TB_PT_STLFRTOMOVE A
//					 WHERE STL_NO = :V_STL_NO
//					   AND A.FRTOMOVE_STAT_CD  IN('1','3')
//					   AND A.TRANSWORD_SEQNO = (SELECT /*+ INDEX_DESC(B PK_PT_STLFRTOMOVE)
//					                                   MAX(TRANSWORD_SEQNO)
//					                              FROM TB_PT_STLFRTOMOVE B
//					                             WHERE A.STL_NO = B.STL_NO
//					                               AND FRTOMOVE_STAT_CD NOT IN ('Z','C')
//					                               AND ROWNUM <= 1
//					                            )
//    				 */
//    				JDTORecordSet jsPtStl = commDao.select(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getPtStlFrtoMove", logId, mthdNm, "이송지시 조회");
//    				
//    				if (jsPtStl.size() <= 0) {
//    					commUtils.printLog(logId, "이송지시테이블에 재료정보["+sStlNo+"] 존재하지 않음", "SL");
//    				} else {
//    					
//    					jsPtStl.first();
//    					JDTORecord jrPtStl = jsPtStl.getRecord();
//    					jrPtStl.setField("YD_MTL_PLN_STR_TO_LOC_CD", ydDnWrLoc);
//    					jrPtStl.setField("FRTOMOVE_STAT_CD"        , "*");
//    					/*
//    					UPDATE USRPTA.TB_PT_STLFRTOMOVE
//						   SET FRTOMOVE_DONE_DATE       = SYSDATE-0.00003
//						     , FTMV_HDS_DD              = TO_CHAR(SYSDATE - (6 / 24), 'YYYYMMDD')
//						     , YD_MTL_PLN_STR_TO_LOC_CD = :V_YD_MTL_PLN_STR_TO_LOC_CD
//						     , FRTOMOVE_STAT_CD         = :V_FRTOMOVE_STAT_CD
//						     , MODIFIER = :V_MODIFIER
//						     , MOD_DDTT = SYSDATE
//						 WHERE STL_NO          = :V_STL_NO
//						   AND TRANSWORD_SEQNO = :V_TRANSWORD_SEQNO
//    					 */
//        				commDao.update(jrPtStl, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updPtStlFrtoMove2", logId, mthdNm, "이송지시 테이블 수정");
//    					
//    				}
//    				
//    				/**************************************
//					 * 코일소재이송완료실적
//					 **************************************/
//    				commUtils.printLog(logId, "[YDPTJ002]코일소재이송완료실적 전문 송신", "SL");
//					
//    				JDTORecord jrYdPtMsg = commUtils.getParam(logId, mthdNm, sModifier);
//    				jrYdPtMsg.setField("JMS_TC_CD"     , "YDPTJ002"); 
//    				jrYdPtMsg.setField("COIL_NO"       , sStlNo    );
//    				jrYdPtMsg.setField("YD_CAR_SCH_ID" , ydCarSchId);
//					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDPTJ002", jrYdPtMsg));
//        		}
//        		
//        	} //하차
//			
//			commUtils.printLog(logId, mthdNm, "S-");
//			
//			jrRtn.setField("YD_CAR_SCH_ID", ydCarSchId);
//			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 	
	

	/**
	 *      [A] 오퍼레이션명 :  복수상차
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procCmbnCarldYn(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "복수상차[CCoilL2RcvSeEJB.procCmbnCarldYn] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
			

			String sStlNo         = commUtils.trim(rcvMsg.getFieldString("STL_NO"     ));
			String sModifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
//PIDEV_S :병행가동용:PI_YD
			String sPI_YD         = commUtils.nvl(rcvMsg.getFieldString("PI_YD"),"*");			
			// PIDEV
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "CCoilCarMvSeEJBBean => procCmbnCarldYn", "APPPI0", sPI_YD, "*");
			
//			if("PIDEV".equals("PIDEV")) {
				
				jrRtn = this.procCmbnCarldYn_PIDEV(rcvMsg);
				return jrRtn;
//			}

//			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);			
//			/********************************
//			 * 차량스케줄 여부 확인
//			 ********************************/
//			jrParam.setField("STL_NO", sStlNo);
//			jrParam.setField("YD_GP" , "J"   );
//			
//			/*
//			SELECT A.*
//			  FROM TB_YD_CARSCH A
//			     , (
//			        SELECT TRANS_ORD_DATE,TRANS_ORD_SEQNO
//			          FROM USRYDA.TB_YD_STOCK
//			         WHERE STL_NO= :V_STL_NO
//			         UNION
//			        SELECT TRANS_ORD_DATE,TRANS_ORD_SEQNO
//			          FROM USRYFA.TB_YF_STOCK
//			         WHERE STL_NO= :V_STL_NO
//			         UNION         			         
//			        SELECT NVL(TRANS_ORD_DATE2,SUBSTR(TRANS_WORD_NO,1,8))
//			             , NVL(TRANS_ORD_SEQNO2,SUBSTR(TRANS_WORD_NO,9))
//			          FROM USRYMA.TB_YM_STOCK
//			         WHERE STOCK_ID =:V_STL_NO
//			       ) B
//			 WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
//			   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
//			   AND DEL_YN   = 'N'
//			 ORDER BY A.YD_CAR_SCH_ID DESC
//			 */
//			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarwbookid", logId, mthdNm, "차량스케줄 조회");
//				
//			if (jsCarSch.size() <= 0) {
//				commUtils.printLog(logId, "차량스케줄이 존재하지 않습니다.", "S-");
//				jrRtn.setField("RTN_CD", "0");
//				return jrRtn;
//			}
//
//			String sCmbnCarldYn   = commUtils.nvl (jsCarSch.getRecord(0).getFieldString("CMBN_CARLD_YN"    ), "N");
//			String sWorkGp        = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_WRK_GP"    ));
//			String sTelNO         = commUtils.trim(jsCarSch.getRecord(0).getFieldString("TEL_NO"           ));
//			String sCarNo         = commUtils.trim(jsCarSch.getRecord(0).getFieldString("CAR_NO"           ));
//			String sCardNo        = commUtils.trim(jsCarSch.getRecord(0).getFieldString("CARD_NO"          ));
//			String sCarKind       = commUtils.trim(jsCarSch.getRecord(0).getFieldString("CAR_KIND"         ));
////			String sWaitkArrDdtt  = commUtils.trim(jsCarSch.getRecord(0).getFieldString("WAIT_ARR_DDTT"    ));
////			String sWaitArrGp     = commUtils.trim(jsCarSch.getRecord(0).getFieldString("WAIT_ARR_GP"      ));
//			String sPosWlocCd     = commUtils.trim(jsCarSch.getRecord(0).getFieldString("SPOS_WLOC_CD"     ));
//			String ydPntCd1       = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_PNT_CD1"       ));
//			String ydStkColGp     = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CARLD_STOP_LOC"));
//			String sTransOrdDate  = commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_DATE"   ));
//			String sTransOrdSeqno = commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_SEQNO"  ));
//			String sDriverName    = commUtils.trim(jsCarSch.getRecord(0).getFieldString("DRIVER_NAME"      ));
//			
//			/******************************
//			 * 조합상차 여부 확인
//			 ******************************/
//			//조합상차(시작:S , 종료: E ,  단일상차: N )
//			commUtils.printLog(logId, "조합상차 여부 : " +sCmbnCarldYn  , "SL");
//
//			if (!"S".equals(sCmbnCarldYn)) {
//				commUtils.printLog(logId, "복수상차가 아닙니다.", "S-");
//				jrRtn.setField("RTN_CD", "2");
//				return jrRtn;
//			}
//			
//			/*********************************
//			 * 저장품 종료
//			 *********************************/
//			jrParam.setField("TRANS_ORD_DATE" , sTransOrdDate);
//			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
//			jrParam.setField("YD_STK_COL_GP"  , ydStkColGp   );
//			jrParam.setField("DEL_YN"         , "Y"          );
//			jrParam.setField("STL_PROG_CD"    , "M"          );
//			jrParam.setField("YD_AIM_RT_GP"   , "M2"         );
//			
//			/*
//			UPDATE TB_YD_STOCK  
//			   SET DEL_YN       = :V_DEL_YN  
//			     , STL_PROG_CD  = :V_STL_PROG_CD  
//			     , YD_AIM_RT_GP = :V_YD_AIM_RT_GP  
//			     , MODIFIER     = :V_MODIFIER  
//			     , MOD_DDTT     = SYSDATE  
//			 WHERE STL_NO IN (  
//			                  SELECT A.STL_NO  
//			                    FROM TB_YD_STOCK   A  
//			                       , TB_YD_STKLYR B  
//			                   WHERE A.STL_NO = B.STL_NO  
//			                     AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE  
//			                     AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO  
//			                     AND B.YD_STK_COL_GP LIKE SUBSTR(:V_YD_STK_COL_GP,1,2)||'%'  
//			                     AND SUBSTR(B.YD_STK_COL_GP,3,2) IN('PT','TR','TT')
//			                 ) 
//			 */
//			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStockTrnsOrd", logId, mthdNm, "저장품 종료");
//			
//			/**********************************************************
//			* 코일출하차량 출발처리 - 맵비활성화
//			**********************************************************/
//			JDTORecord jrYdDmMsg = commUtils.getParam(logId, mthdNm, sModifier);
////			jrYdDmMsg.setField("JMS_TC_CD"      , "DMYDR040"   );	//전문코드
//			jrYdDmMsg.setField("CARD_NO"        , sCardNo      );
//			jrYdDmMsg.setField("CAR_NO"         , sCarNo       );			
//			jrYdDmMsg.setField("SPOS_WLOC_CD"   , sPosWlocCd   );
//			jrYdDmMsg.setField("SPOS_YD_PNT_CD" , ydPntCd1     );
//			jrYdDmMsg.setField("TRANS_ORD_DT"   , sTransOrdDate);
//			jrYdDmMsg.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
//
//			EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
//			JDTORecord jrMsg = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { jrYdDmMsg });
//			
//			jrRtn = commUtils.addSndData(jrRtn, jrMsg);
//			
//			/**********************************************************
//			* 저장품제원 : 코일야드L2로 송신(YDY5L002)
//			**********************************************************/
//			JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
//			sndL2Msg.setField("JMS_TC_CD"      , "YDY5L002");
//			sndL2Msg.setField("YD_INFO_SYNC_CD", "3"       );
//			sndL2Msg.setField("YD_STK_COL_GP"  , ydStkColGp);
//			sndL2Msg.setField("YD_STK_BED_NO"  , ""        );
//			
//			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L002", sndL2Msg));
//			
//			/*************************
//			 * 복수 창고 구분
//			 *************************/
//			/*
//			WITH TEMP_TABLE AS (
//			SELECT A.TRANS_ORD_DATE
//			     , A.TRANS_ORD_SEQNO
//			     , A.YD_GP
//			  FROM (
//			        SELECT B.YD_GP
//			             , A.STL_NO
//			             , A.TRANS_ORD_DATE
//			             , A.TRANS_ORD_SEQNO
//			          FROM USRYDA.TB_YD_STOCK A
//			             , TB_PT_COILCOMM B
//			         WHERE A.STL_NO = B.COIL_NO 
//			           AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
//			           AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
//			         UNION ALL
//			        SELECT B.YD_GP
//			             , A.STL_NO
//			             , A.TRANS_ORD_DATE
//			             , A.TRANS_ORD_SEQNO
//			          FROM USRYFA.TB_YF_STOCK A
//			             , TB_PT_COILCOMM B
//			         WHERE A.STL_NO = B.COIL_NO 
//			           AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
//			           AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
//			         UNION ALL         			         
//			        SELECT B.YD_GP 
//			             , A.STOCK_ID
//			             , A.TRANS_ORD_DATE2
//			             , A.TRANS_ORD_SEQNO2
//			          FROM USRYMA.TB_YM_STOCK A
//			             , TB_PT_COILCOMM B
//			         WHERE A.STOCK_ID = B.COIL_NO 
//			           AND A.TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE
//			           AND A.TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
//			       ) A
//			 GROUP BY  A.TRANS_ORD_DATE ,A.TRANS_ORD_SEQNO, A.YD_GP
//			)
//			SELECT A.TRANS_ORD_DATE
//			     , A.TRANS_ORD_SEQNO
//			     , COUNT(YD_GP) AS CNT
//			     , (SELECT B.YD_GP 
//			          FROM TEMP_TABLE B
//			         WHERE B.YD_GP <> :V_YD_GP) AS NEXT_YD_GP
//			  FROM TEMP_TABLE A
//			 GROUP BY A.TRANS_ORD_DATE
//			        , A.TRANS_ORD_SEQNO
//			 HAVING COUNT(YD_GP)>1          
//			 */
//			JDTORecordSet jsCmplGp = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarYdCmbnCarldGP", logId, mthdNm, "복수창고 구분");
//			
//			if (jsCmplGp.size() <= 0) {
//				commUtils.printLog(logId, "복수창고가 아닌경우(복수동)", "SL");
//				/*
//				WITH TEMP_TABLE AS (
//				SELECT :V_YD_GP AS YD_GP 
//				     , :V_TRANS_ORD_DATE  AS TRANS_ORD_DATE 
//				     , :V_TRANS_ORD_SEQNO AS TRANS_ORD_SEQNO
//				  FROM DUAL
//				)
//				SELECT COUNT(*) AS CHK
//				  FROM (
//				        SELECT SUBSTR(YD_CARPNT_CD,2,2) 
//				          FROM (
//				                SELECT A.STL_NO
//				                     , B.YD_STK_COL_GP
//				                     , SUBSTR(B.YD_STK_COL_GP,1,1) AS YD_GP
//				                     , SUBSTR(B.YD_STK_COL_GP,3,2) AS SPAN_GP
//				                     , SUBSTR(B.YD_STK_COL_GP,2,1) AS BAY_GP
//				                  FROM USRYDA.TB_YD_STOCK  A
//				                     , USRYDA.TB_YD_STKLYR B
//				                     , TEMP_TABLE C
//				                 WHERE A.STL_NO = B.STL_NO
//				                   AND A.TRANS_ORD_DATE  = C.TRANS_ORD_DATE
//				                   AND A.TRANS_ORD_SEQNO = C.TRANS_ORD_SEQNO
//				                   AND C.YD_GP ='J'
//				               ) A
//				             , USRYDA.TB_YD_CARPOINT B
//				         WHERE A.YD_GP  = B.YD_GP
//				           AND B.DEL_YN = 'N'
//				           AND A.SPAN_GP BETWEEN B.YD_SPAN_FROM AND B.YD_SPAN_TO
//				           AND A.BAY_GP = B.YD_BAY_GP
//				           AND A.STL_NO <> :V_STL_NO
//				         GROUP BY SUBSTR(YD_CARPNT_CD,2,2)
//				       ) A 
//				 WHERE 1 = 1        
//				 */
//				JDTORecordSet jsChkCmpl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarschCarNoCardNoTransNoCHK", logId, mthdNm, "복수창고 구분");
//				
//				String sChk = "";
//				if (jsChkCmpl.size() > 0) {
//					sChk = jsChkCmpl.getRecord(0).getFieldString("CHK");
//				}
//				
//				/************************
//				 * 입동지시 호출
//				 ************************/
//				JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
//				jrYdMsg.setField("JMS_TC_CD"            , "DMYDR061"    );
//				jrYdMsg.setField("JMS_TC_CREATE_DDTT"   , commUtils.getDateTime14());
//				jrYdMsg.setField("TC_CODE"		        , "DMYDR061"    );
//				jrYdMsg.setField("TC_CREATE_DDTT"       , commUtils.getDateTime14());
//				jrYdMsg.setField("YD_GP"		        , "J");					
//				jrYdMsg.setField("WORK_GP"		        , sWorkGp);
//				jrYdMsg.setField("TEL_NO"		        , sTelNO);
//				jrYdMsg.setField("TRANS_ORD_DT"		    , sTransOrdDate);
//				jrYdMsg.setField("TRANS_ORD_SEQNO" 		, sTransOrdSeqno);
//				jrYdMsg.setField("CAR_NO"				, sCarNo );
//				jrYdMsg.setField("CARD_NO"				, sCardNo);
//				jrYdMsg.setField("CAR_KIND"				, sCarKind);
//				jrYdMsg.setField("WAIT_ARR_DDTT"		, commUtils.getDateTime14());
//				jrYdMsg.setField("WAIT_ARR_GP"			, "B");
//				jrYdMsg.setField("DRIVER_NAME"			, sDriverName);
//				
//				if ("0".equals(sChk) || "1".equals(sChk)) { //복수동 존재 수량 체크
//					jrYdMsg.setField("CMBN_CARLD_YN", "E");
//				}else {                               
//					jrYdMsg.setField("CMBN_CARLD_YN", "S");
//				}
//				
//				String sAPP821 = coilDao.ApplyYn(logId, mthdNm, "APP821", "J", "*"); //복수동 대기장도착 전문송신 여부
//				
//				if ("Y".equals(sAPP821)) {
//					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
//				} else {
//					EJBConnector ejbConn1 = new EJBConnector("default", "CCoilL3RcvSeEJB", this);
//					JDTORecord jrArrived = (JDTORecord)ejbConn1.trx("rcvDMYDR061", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
//					
//					jrRtn = commUtils.addSndData(jrRtn, jrArrived);
//				}
//				
//			} else {
//				
//				commUtils.printLog(logId, "복수창고인 경우", "SL");
//				
//				String sNextYdGp = jsCmplGp.getRecord(0).getFieldString("NEXT_YD_GP");
//				jrParam.setField("YD_GP", sNextYdGp);
//				
//				/*
//				SELECT C.*
//				  FROM USRYDA.TB_YD_STOCK A
//				     , USRYDA.TB_YD_STKLYR B
//				     , USRYDA.TB_YD_CARPOINT C
//				 WHERE A.STL_NO = B.STL_NO
//				   AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
//				   AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
//				   AND SUBSTR(B.YD_STK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
//				   AND SUBSTR(B.YD_STK_COL_GP,1,1) = C.YD_GP
//				   AND 'J' = :V_YD_GP
//				   AND C.DEL_YN = 'N'
//				 UNION ALL
//				 SELECT C.*
//				  FROM USRYMA.TB_YM_STOCK A
//				     , USRYMA.TB_YM_STACKLAYER B
//				     , USRYDA.TB_YD_CARPOINT C
//				 WHERE A.STOCK_ID = B.STOCK_ID 
//				   AND A.TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE
//				   AND A.TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
//				   AND SUBSTR(B.STACK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
//				   AND SUBSTR(B.STACK_COL_GP,1,1) = C.YD_GP
//				   AND :V_YD_GP IN('3')
//				 UNION ALL
//				 SELECT C.*
//				  FROM USRYFA.TB_YF_STOCK    A
//				     , USRYFA.TB_YF_STKLYR   B
//				     , USRYDA.TB_YD_CARPOINT C
//				 WHERE A.STL_NO = B.STL_NO 
//				   AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
//				   AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
//				   AND SUBSTR(B.YD_STK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
//				   AND SUBSTR(B.YD_STK_COL_GP,1,1) = C.YD_GP
//				   AND :V_YD_GP IN('1')   
//				 */
//				JDTORecordSet jsNextYd = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarYdCmbnCarSch", logId, mthdNm, "다음 복수창고");
//				
//				if (jsNextYd.size() <= 0) {
//					commUtils.printLog(logId, "다음 창고 도착가능 포인트가 존재하지 않습니다.", "S-");
//					jrRtn.setField("RTN_CD", "0");
//					return jrRtn;
//				}
//				
//				/*******************************
//				 * 차량입동지시 
//				 ********************************/
//				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//				jrYdMsg.setField("JMS_TC_CD"           , "YDDMR028"    );
//				jrYdMsg.setField("JMS_TC_CREATE_DDTT"  , commUtils.getDateTime14());
//				jrYdMsg.setField("TC_CODE"		       , "YDDMR028"    );
//				jrYdMsg.setField("TC_CREATE_DDTT"      , commUtils.getDateTime14());
//			    jrYdMsg.setField("TRANS_WORD_DATE"     , sTransOrdDate );
//			    jrYdMsg.setField("TRANS_WORD_SEQNO"    , sTransOrdSeqno);
//			    jrYdMsg.setField("CARD_NO"             , sCardNo       );
//			    jrYdMsg.setField("CAR_NO"              , sCarNo        );
//			    jrYdMsg.setField("WLOC_CD"             , jsNextYd.getRecord(0).getFieldString("WLOC_CD"));
//			    jrYdMsg.setField("YD_PNT_CD"           , jsNextYd.getRecord(0).getFieldString("YD_PNT_CD"));
//				
//			    jrYdMsg.setField("LOAN_PULLOUT_ABLE_YN","Y"            );
//			    jrYdMsg.setField("BAYIN_DDTT"          , commUtils.getDateTime14()); //입동일시
//
//			    jrYdMsg.setField("YD_CARPNT_CD"        , jsNextYd.getRecord(0).getFieldString("YD_CARPNT_CD"));
//			    
//			    jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
//			}
//			
//			commUtils.printLog(logId, mthdNm, "S-");
//
//			jrRtn.setField("RTN_CD", "1");
//			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
    
    /**
     * 오퍼레이션명 : 차량동간이적기능(신)
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return intRtnVal
     * @throws DAOException
     */
    public JDTORecord traillerMoveSch(JDTORecord rcvMsg) throws DAOException {
    	String mthdNm   = "차량동간이적기능[CCoilL2RcvSeEJB.traillerMoveSch] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");
			
			JDTORecord jrRtn     = JDTORecordFactory.getInstance().create();	//전문 Return
			String rtnCd			= "";
			String rtnMsg			= "";
			
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       ));
			String sStlNo        = commUtils.trim(rcvMsg.getFieldString("STL_NO"          ));
			String ydDnWrLoc     = commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LOC"    ));
			String ydWbookId     = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"     )); 
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   ));
			String sModifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			
			boolean bCarMvYn = coilDao.chkCarMv(logId, mthdNm, ydSchCd); // 차량동간이적 여부
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("STL_NO"     	, sStlNo);
			jrParam.setField("YD_SCH_CD"  	, ydSchCd);
			jrParam.setField("YD_WBOOK_ID"	, ydWbookId);
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
			
			//주작업인 경우에만 작업 한다.(to위치가 차량위치가 아니고 차량이적상차 작업 인 경우)
			if (!"PT".equals(ydDnWrLoc.substring(2, 4))
			&&  !"TR".equals(ydDnWrLoc.substring(2, 4))
			&& ("U".equals(ydSchCd.substring(6, 7)))	
			) {
				commUtils.printLog(logId, "차량동간이적 작업 - 보조작업 skip", "S-");
				return jrRtn;
			}
			
			if (bCarMvYn) { //차량동간이적이면
				
        	} else {
				commUtils.printLog(logId, "차량이적 아님", "S-");
				return jrRtn;
        	}
			
			String sChk = "";
			
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCarSchLdByStlNo
			SELECT B.YD_CAR_SCH_ID 
			  FROM TB_YD_STOCK A
			     , TB_YD_CARSCH B 
			 WHERE STL_NO = :V_STL_NO 
			   AND A.CAR_FRTOMOVE_WORD_NO = B.TRANS_ORD_DATE||B.TRANS_ORD_SEQNO
			   AND A.COIL_CAR_NO          = B.CAR_NO
			   AND B.DEL_YN = 'N'
			*/	   
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCarSchLdByStlNo", logId, mthdNm, "차량스케쥴 조회");
			
			if(jsCarSch.size() < 1) {
				commUtils.printLog(logId, "차량동간이적 작업 - 차량스케쥴 검색오류발생", "S-");
				return jrRtn;
			}
			String ydCarSchId = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
			
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);

			/*************************************
			 * 상차 작업
			 *************************************/

			if ("U".equals(ydSchCd.substring(6, 7))) {

				jrParam.setField("YD_DN_WR_LOC"     , ydDnWrLoc     );
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insY5YDL009CarMtlCarMove
				--크레인권하실적 차량이송재료 등록 
				INSERT INTO TB_YD_CARFTMVMTL (
				       YD_CAR_SCH_ID
				     , STL_NO   
				     , REGISTER     
				     , REG_DDTT     
				     , MODIFIER     
				     , MOD_DDTT 
				     , DEL_YN       
				     , YD_STK_BED_NO
				     , YD_STK_LYR_NO
				     , HCR_GP
				     , STL_PROG_CD
				     , YD_MTL_ITEM
				     , YD_ROUTE_GP
				)
				SELECT :V_YD_CAR_SCH_ID
				     , CM.STL_NO
				     , :V_MODIFIER     
				     , SYSDATE         
				     , :V_MODIFIER     
				     , SYSDATE         
				     , 'N'             
				     , NVL(SUBSTR(NVL(:V_YD_DN_WR_LOC, YD_DN_WO_LOC),-2),'01') AS YD_STK_BED_NO
				     , '001' AS YD_STK_LYR_NO
				     , CM.HCR_GP
				     , CM.STL_PROG_CD
				     , CM.YD_MTL_ITEM
				     , CM.YD_ROUTE_GP
				  FROM TB_YD_CRNWRKMTL CM
				     , TB_YD_STOCK     ST
				     , TB_YD_CRNSCH    CR
				 WHERE CM.STL_NO        = ST.STL_NO
				   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND CM.YD_CRN_SCH_ID = CR.YD_CRN_SCH_ID

		    	 */
		    	commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insY5YDL009CarMtlCarMove", logId, mthdNm, "차량이송재료 등록");
				
		    	/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getY5YDL009CarSchLd
		    	WITH TO_DIR_TBL AS
		    	(
		    	SELECT DIST_SHIPASSIGN_GP  AS TO_DIR
		    	  FROM TB_YD_WRKBOOK 
		    	  WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID AND ROWNUM = 1
		    	)
		    	SELECT B.YD_CAR_SCH_ID
		    	     , A.SAILNO  
		    	     --완료여부
		    	     , CASE WHEN A.SAILNO = (SELECT COUNT(*) FROM TB_YD_CARFTMVMTL WHERE DEL_YN = 'N' AND YD_CAR_SCH_ID = B.YD_CAR_SCH_ID) THEN 'Y'
		    	            -- 해당동에 상차 할 물량이 없는 경우도 완료임
		    	            WHEN (SELECT COUNT(*)
		    				        FROM TB_YD_WRKBOOK 
		    				       WHERE CAR_NO    =  B.CAR_NO
		    				         AND DEL_YN    = 'N' 
		    				         AND YD_BAY_GP = SUBSTR(:V_YD_SCH_CD,2,1)
		    				         AND SUBSTR(YD_SCH_CD,7,1) = 'U'
		    				         AND ROWNUM =1
		    				       )  = 0 THEN  'Y'
		    	            ELSE 'N' END UP_END_YN
		    	     --목적동       
		    	     , A.YD_AIM_BAY_GP       
		    	     , B.CAR_NO
		    	     , B.CARD_NO
		    	     , B.TRANS_ORD_DATE
		    	     , B.TRANS_ORD_SEQNO
		    	     , B.SPOS_WLOC_CD
		    	     , B.YD_PNT_CD1 AS  SPOS_YD_PNT_CD
		    	     , (SELECT YD_CARPNT_CD 
		    	          FROM TB_YD_CARPOINT 
		    	         WHERE WLOC_CD = B.SPOS_WLOC_CD AND YD_PNT_CD = B.YD_PNT_CD1 AND DEL_YN = 'N')   AS YD_CARPNT_CD
		    	     -- 하차스케쥴코드
		    	     , CASE WHEN SUBSTR(:V_YD_SCH_CD,8,1) = 'H' THEN 
		    	                 'J' || A.YD_AIM_BAY_GP || 'TR1' || C.TO_DIR ||'LH' 
		    	            ELSE 'J' || A.YD_AIM_BAY_GP || 'TR1' || C.TO_DIR ||'LM' 
		    	            END AS TO_YD_SCH_CD
		    	     -- 하차도착위치 (작업예약에 방향이 있음)
		    	     , (SELECT MIN(YD_STK_COL_GP)  
		    	          FROM TB_YD_CARPOINT 
		    	         WHERE DEL_YN = 'N'
		    	           AND YD_GP  = 'J'
		    	           AND YD_CAR_USETYPE_GP = 'MT'
		    	           AND CASE WHEN SUBSTR(YD_STK_COL_GP,6,1) IN ('1','2') THEN '1'
		    	                    WHEN SUBSTR(YD_STK_COL_GP,6,1) IN ('3')     THEN '3'
		    	                    WHEN SUBSTR(YD_STK_COL_GP,6,1) IN ('4','5') THEN '2' 
		    	                    END = C.TO_DIR
		    	           AND SUBSTR(YD_STK_COL_GP,2,1) = A.YD_AIM_BAY_GP ) AS YD_CARUD_STOP_LOC
		    	  FROM TB_YD_STOCK  A
		    	     , TB_YD_CARSCH B 
		    	     , TO_DIR_TBL   C 
		    	 WHERE STL_NO = :V_STL_NO 
		    	   AND A.CAR_FRTOMOVE_WORD_NO = B.TRANS_ORD_DATE||B.TRANS_ORD_SEQNO
		    	   AND A.COIL_CAR_NO          = B.CAR_NO
		    	   AND B.YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID
    			*/
    			JDTORecordSet jsCarUpSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getY5YDL009CarSchLd", logId, mthdNm, "상차 차량스케줄 조회 "); 
    	    	
    			if (jsCarUpSch.size() > 0) {
    				JDTORecord jrCarUpSch = jsCarUpSch.getRecord(0);

    				String carLdCmplYn 		= commUtils.trim(jrCarUpSch.getFieldString("UP_END_YN")); 		//차량상차완료여부
    				String sCarNo 			= commUtils.trim(jrCarUpSch.getFieldString("CAR_NO")); 
    				String sCardNo 			= commUtils.trim(jrCarUpSch.getFieldString("CARD_NO")); 
    				String sToYdSchCd 		= commUtils.trim(jrCarUpSch.getFieldString("TO_YD_SCH_CD")); 	    //TO스케쥴 코드
    				String ydAimBayGp 		= commUtils.trim(jrCarUpSch.getFieldString("YD_AIM_BAY_GP")); 	    
    				String sTransOrdDate 	= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_DATE")); 	    
    				String sTransOrdSeqNo 	= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_SEQNO")); 	    
    				String WlocCd 			= commUtils.trim(jrCarUpSch.getFieldString("SPOS_WLOC_CD")); 	    
    				String ydPndCd 			= commUtils.trim(jrCarUpSch.getFieldString("SPOS_YD_PNT_CD")); 	
    				String ydCarPntCd 	    = commUtils.trim(jrCarUpSch.getFieldString("YD_CARPNT_CD")); 	
    				String ydCarupStopLoc   = commUtils.trim(jrCarUpSch.getFieldString("YD_CARUD_STOP_LOC")); 	
    				String sUpEndYn         = commUtils.trim(jrCarUpSch.getFieldString("UP_END_YN"));   // 상차 완료 여부
    				

    				commUtils.printLog(logId, mthdNm+  "★★★★★  권하차량스케쥴:" + ydCarSchId + " 차량상차완료:" + carLdCmplYn+ " ★★★★", "SL");
    				
    				
    				//차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
    				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
    				if ("Y".equals(carLdCmplYn)) {              //해당동만 완료이면 차량상태 완료처리함
    					jrParam.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
    					jrParam.setField("YD_EQP_WRK_STAT" , "L");
					} else {
						jrParam.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
    					jrParam.setField("YD_EQP_WRK_STAT" , "U");
					}
    				jrParam.setField("YD_STK_COL_GP" 	, ydDnWrLoc.substring(0, 6) ); 
    				jrParam.setField("WR_DT" 			, commUtils.getDateTime14() ); 
    				jrParam.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
    				jrParam.setField("YD_WBOOK_ID"	    , ydWbookId);
    				
    				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL009CarSchLdCarMove 
    				--크레인권하실적 상차 차량스케줄 수정
    				UPDATE TB_YD_CARSCH TS
    				   SET TS.MODIFIER             = :V_MODIFIER
    				     , TS.MOD_DDTT             = SYSDATE
    				     , TS.YD_EQP_WRK_STAT      = :V_YD_EQP_WRK_STAT --영차
    				     , TS.YD_EQP_WRK_SH        = (SELECT COUNT(*) 
    				                                    FROM TB_YD_CARFTMVMTL 
    				                                   WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID)
    				     , TS.YD_EQP_WRK_WT        = (SELECT SUM(COIL_WT) 
    				                                    FROM TB_YD_CARFTMVMTL A
    				                                       , TB_PT_COILCOMM   B
    				                                   WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
    				                                     AND A.STL_NO        = B.COIL_NO
    				                                   
    				                                   )
    				     , TS.YD_PNT_CD3           = '0000'
    				     , TS.YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
    				     , TS.YD_CARLD_STOP_LOC    = :V_YD_STK_COL_GP
    				     , TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT,NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE))
    				     , TS.YD_CARLD_CMPL_DT     = DECODE(:V_YD_CAR_PROG_STAT,'5',NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE),NULL)
    				     , TS.YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT
    				WHERE TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
    				*/    
    				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL009CarSchLdCarMove", logId, mthdNm, " 상차 차량스케줄 수정 ");
					commUtils.printLog(logId, mthdNm+  "상차완료 여부: " + carLdCmplYn, "SL");
					
					if ("Y".equals(carLdCmplYn)) {

						JDTORecord jrWbookSearch = JDTORecordFactory.getInstance().create();
						
						/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarftmvmtlByCarSchId 
						SELECT YD_CAR_SCH_ID
						     , STL_NO
						  FROM TB_YD_CARFTMVMTL
						 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
						*/   
						JDTORecordSet jsWbookSearch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarftmvmtlByCarSchId", logId, mthdNm, "목적동 대상재 조회"); 
				    	
						if (jsWbookSearch.size() > 0) {
							for(int Loop_i = 1; Loop_i <= jsWbookSearch.size() ; Loop_i++) {
								jsWbookSearch.absolute(Loop_i);
								jrWbookSearch = jsWbookSearch.getRecord();
								
								//작업예약 등록 호출
		    					JDTORecord jrInRec = commUtils.getParam(logId, mthdNm, sModifier);
		    					jrInRec.setField("YD_SCH_CD"			, sToYdSchCd);//스케줄코드
		    					jrInRec.setField("STL_SH"				, "1");  //LINE_IN 재료매수
		    					jrInRec.setField("STL_NO1"				, commUtils.trim(jrWbookSearch.getFieldString("STL_NO")));
		    					jrInRec.setField("YD_TO_LOC_DCSN_MTD"	, "S");								
		    					jrInRec.setField("YD_UP_COLL_SEQ1"		, "1");  //권상모음순서
		    					jrInRec.setField("YD_USER_ID"			, sModifier);   
		    					jrInRec.setField("CARD_NO"				, sCarNo);  //차량번호
		    					jrInRec.setField("CAR_NO"				, sCarNo);  //차량번호
		    					jrInRec.setField("YD_AIM_YD_GP"		    , "J");  
		    					jrInRec.setField("YD_AIM_BAY_GP"		, ydAimBayGp);  
		    					jrInRec.setField("YD_CAR_USE_GP"		, CConstant.YD_CAR_USE_GP_DM);
		    					
		    	    			EJBConnector ejbConn = new EJBConnector("default", "CCoilWrkBookSeEJB", this);
		    	    			JDTORecord jrWbook = (JDTORecord)ejbConn.trx("insWrkBookTx", new Class[] { JDTORecord.class }, new Object[] { jrInRec });
							}
						}
    	    			
    					
		    			// 마지막 작업 마치고 차량 정보 초기화 호출
						JDTORecord jrSnd  = commUtils.getParam(logId, mthdNm, sModifier);
						jrSnd.setField("TRANS_ORD_DATE"		, sTransOrdDate);
						jrSnd.setField("TRANS_ORD_SEQNO"	, sTransOrdSeqNo);
						jrSnd.setField("CAR_NO"				, sCarNo);
						jrSnd.setField("CARD_NO"			, sCardNo);
						jrSnd.setField("SPOS_WLOC_CD"		, WlocCd);
						jrSnd.setField("SPOS_YD_PNT_CD"		, ydPndCd);
						jrSnd.setField("YD_CARPNT_CD"		, ydCarPntCd);
						jrSnd.setField("WRK_GP"				, "U");  //상차작업

						jrRtn = commUtils.addSndData(jrRtn, this.procYDOutCarLevWr(jrSnd));	
						
						
						/**********************************************************
						* Crane스케줄 호출
						*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
						*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
						**********************************************************/
						jrParam.setField("YD_STK_COL_GP" 	, ydCarupStopLoc ); 
						/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCarpointByFrmYn 
						SELECT YD_FRM_YN
						  FROM USRYDA.TB_YD_CARPOINT
						 WHERE 1=1 
						   AND YD_STK_COL_GP = :V_YD_STK_COL_GP 
						   AND YD_GP  = 'J'
						   AND DEL_YN = 'N'
						*/	   
						JDTORecordSet jsCarpontFrm = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCarpointByFrmYn", logId, mthdNm, "형상 비형상 판단");
						if(jsCarpontFrm.size() > 0) {
							JDTORecord jrFrm = jsCarpontFrm.getRecord(0);
							String ydFrmYn = commUtils.trim(jrFrm.getFieldString("YD_FRM_YN"));  
						
							// 형상여부
							if ("N".equals(ydFrmYn)) {
								// 하차지에 형상이 없으므로 동간이적 차량 도착처리 전문 송신 
								JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
								jrYdMsg.setField("JMS_TC_CD"		, "Y5YDL018" );
								jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
								jrYdMsg.setField("PT_LOAD_LOC"		, ydCarupStopLoc);
								jrYdMsg.setField("CAR_NO"			, sCardNo );
								jrYdMsg.setField("CAR_UPDN_GP"		, "2");  //하차
								jrYdMsg.setField("BACKUP_YN"		, "Y");  
								
								jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);				
							}	
						}
    				}	
				}					
    		} else {
//    			//하차작업
    			
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// 차량 이송재료 삭제
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    			jrParam.setField("DEL_YN"		, "Y");
				/* com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarftmvmtlDelYn 
				UPDATE TB_YD_CARFTMVMTL
				   SET DEL_YN        = :V_DEL_YN
				     , MODIFIER      = :V_MODIFIER
				     , MOD_DDTT      = SYSDATE
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				   AND STL_NO        = :V_STL_NO  
                */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarftmvmtlDelYn", logId, mthdNm, "차량스케줄재료 삭제");    			
    			
				
    			// 하차 작업
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getY5YDL009CarSchUd 
				SELECT B.YD_CAR_SCH_ID
				     , A.SAILNO  
				     --하차완료여부:차량재료가 없는 경우
				     , CASE WHEN 0        = (SELECT COUNT(*) 
				                               FROM TB_YD_CARFTMVMTL 
				                              WHERE DEL_YN = 'N' 
				                                AND YD_CAR_SCH_ID = B.YD_CAR_SCH_ID) THEN 'Y'
				--            WHEN A.SAILNO = (SELECT COUNT(*) 
				--                               FROM TB_YD_CARFTMVMTL 
				--                              WHERE DEL_YN = 'Y' 
				--                                AND YD_CAR_SCH_ID = B.YD_CAR_SCH_ID) THEN 'Y'
				            ELSE 'N' END DN_END_YN
				     --목적동       
				     , A.YD_AIM_BAY_GP       
				     , B.CAR_NO
				     , B.CARD_NO
				     , B.TRANS_ORD_DATE
				     , B.TRANS_ORD_SEQNO
				     , B.ARR_WLOC_CD    AS WLOC_CD
				     , B.YD_PNT_CD3     AS YD_PNT_CD
				     , (SELECT YD_CARPNT_CD 
				          FROM TB_YD_CARPOINT 
				         WHERE WLOC_CD = B.ARR_WLOC_CD AND YD_PNT_CD = B.YD_PNT_CD3 AND DEL_YN = 'N')   AS YD_CARPNT_CD
				  FROM TB_YD_STOCK A
				     , TB_YD_CARSCH B 
				 WHERE STL_NO = :V_STL_NO 
				   AND A.CAR_FRTOMOVE_WORD_NO = B.TRANS_ORD_DATE||B.TRANS_ORD_SEQNO
				   AND A.COIL_CAR_NO          = B.CAR_NO
				   AND B.YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID
    			*/
    			JDTORecordSet jsCarUpSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getY5YDL009CarSchUd", logId, mthdNm, "하차 차량스케줄 조회 "); 
    	    	
    			if (jsCarUpSch.size() > 0) {
    				JDTORecord jrCarUpSch = jsCarUpSch.getRecord(0);

    				String carDnCmplYn 		= commUtils.trim(jrCarUpSch.getFieldString("DN_END_YN")); 		//차량하차완료여부
    				String sCarNo 			= commUtils.trim(jrCarUpSch.getFieldString("CAR_NO")); 
    				String sCardNo 			= commUtils.trim(jrCarUpSch.getFieldString("CARD_NO")); 
    				String ydCarPntCd		= commUtils.trim(jrCarUpSch.getFieldString("YD_CARPNT_CD")); 
    				String sTransOrdDate	= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_DATE"));
    				String sTransOrdSeqNo	= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_SEQNO")); 
    				String sWlocCd 			= commUtils.trim(jrCarUpSch.getFieldString("WLOC_CD")); 
    				String ydPndCd 			= commUtils.trim(jrCarUpSch.getFieldString("YD_PNT_CD")); 					
    				
    				
    				String ydSchPriorNew = "";
    				String ydEqpIdNew    = "";
    				commUtils.printLog(logId, mthdNm+  "★★★★★  권하차량스케쥴:" + ydCarSchId + " 차량하차완료:" + carDnCmplYn+ " ★★★★", "SL");
    				
    				//차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
    				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
    				
    				if ("Y".equals(carDnCmplYn)) {              //해당동만 완료이면 차량상태 완료처리함
    					jrParam.setField("YD_CAR_PROG_STAT", "E"); //야드차량진행상태(하차완료)
					} else {
						jrParam.setField("YD_CAR_PROG_STAT", "D"); //야드차량진행상태(하차개시)
					}
    				jrParam.setField("YD_STK_COL_GP" 	, ydDnWrLoc.substring(0, 6) ); 
    				jrParam.setField("WR_DT" 			, commUtils.getDateTime14() ); 
    				jrParam.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
    				
    				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL009CarSchDn 
    				--크레인권하실적 하차 차량스케줄 수정
    				UPDATE USRYDA.TB_YD_CARSCH 
    				   SET MODIFIER             = :V_MODIFIER
    				     , MOD_DDTT             = SYSDATE
    				     , YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT   
    				     , YD_EQP_WRK_SH        = (SELECT COUNT(*) 
    				                                 FROM TB_YD_CARFTMVMTL 
    				                                WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID)
    				     , YD_EQP_WRK_WT        = (SELECT SUM(COIL_WT) 
    				                                 FROM TB_YD_CARFTMVMTL A
    				                                    , USRPTA.TB_PT_COILCOMM   B
    				                                WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
    				                                  AND A.STL_NO        = B.COIL_NO
    				                                   
    				                                   )
    				     , YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
    				  --   , YD_CARUD_STOP_LOC    = :V_YD_STK_COL_GP
    				     , YD_CARUD_ST_DT       = NVL(YD_CARUD_ST_DT,NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE))
    				     , YD_CARUD_CMPL_DT     = DECODE(:V_YD_CAR_PROG_STAT,'E',NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE),NULL)
    				 WHERE YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID  
    				*/    
    				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL009CarSchDn", logId, mthdNm, " 하차 차량스케줄 수정 ");
					commUtils.printLog(logId, mthdNm+  "하차지 완료 여부: " + carDnCmplYn, "SL");

    				if ("Y".equals(carDnCmplYn)) {

    					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updateYdStockTranwordnoNewDown 
    					UPDATE TB_YD_STOCK
    					   SET MODIFIER  = :V_MODIFIER
    					     , MOD_DDTT  = SYSDATE
    					     , SCARFING_YN  = NULL
    					     , COIL_CAR_NO  = NULL
    					     , COIL_CAR_LOTID_YN    = NULL
    						 , SAILNO               = NULL
    					     , CAR_FRTOMOVE_WORD_NO = NULL
    					 WHERE STL_NO IN (
    					                  SELECT STL_NO
    					                    FROM TB_YD_CARFTMVMTL
    					                   WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
    					                 )
    	                  )
    	    			 */
    	    			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updateYdStockTranwordnoNewDown", logId, mthdNm, "초기화 재료 조회");
    					
    					
    					// 마지막 작업 마치고 차량 정보 초기화 호출
						JDTORecord jrSnd = commUtils.getParam(logId, mthdNm, sModifier);
						jrSnd.setField("TRANS_ORD_DATE"		, sTransOrdDate);
						jrSnd.setField("TRANS_ORD_SEQNO"	, sTransOrdSeqNo);
						jrSnd.setField("CAR_NO"				, sCarNo);
						jrSnd.setField("CARD_NO"			, sCardNo);
						jrSnd.setField("SPOS_WLOC_CD"		, sWlocCd);
						jrSnd.setField("SPOS_YD_PNT_CD"		, ydPndCd);
						jrSnd.setField("YD_CARPNT_CD"		, ydCarPntCd);
						jrSnd.setField("WRK_GP"				, "D");  //하차작업

						jrRtn = commUtils.addSndData(jrRtn, this.procYDOutCarLevWr(jrSnd));	
						
						
						/**********************************************************
						* 상차지 작업 예약 호출 Crane스케줄 호출
						*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
						*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
						**********************************************************/
						/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCarPntFrmYnToUp 
						SELECT AA.* 
						     , (SELECT NVL(YD_FRM_YN,'N')
						          FROM USRYDA.TB_YD_CARPOINT 
						         WHERE YD_STK_COL_GP = AA.YD_STK_COL_GP
						           AND DEL_YN = 'N')  AS YD_FRM_YN 
						  FROM (
						        SELECT A.YD_WBOOK_ID   
						             , A.YD_SCH_CD 
						            -- , CTS_RELAY_SADDLE AS PT_LOC
						             , (SELECT YD_STK_COL_GP
						                  FROM USRYDA.TB_YD_CARPOINT
						                 WHERE YD_CAR_USETYPE_GP = 'MT'
						                   AND YD_GP     = SUBSTR(A.YD_SCH_CD,1,1)
						                   AND YD_BAY_GP = SUBSTR(A.YD_SCH_CD,2,1)
						                   AND SUBSTR(YD_CARPNT_CD,2,1) = SUBSTR(A.YD_SCH_CD,6,1)
						                   AND ROWNUM = 1
						               ) AS YD_STK_COL_GP       
						          FROM TB_YD_WRKBOOK A
						             , TB_YD_WRKBOOKMTL B
						             , TB_YD_STOCK C
						         WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
						           AND B.STL_NO = C.STL_NO
						           AND A.DEL_YN = 'N'
						           AND B.DEL_YN = 'N'
						           AND A.CARD_NO = :V_CARD_NO
						           AND SUBSTR(A.YD_SCH_CD, 7,1) = 'U'  --상차
						        ORDER BY A.YD_WBOOK_ID  
						       ) AA
						*/

						//PIDEV
//						String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "CCoilL2RcvSeEJBBean => 차량동간이적기능[CCoilL2RcvSeEJB.traillerMoveSch]", "APPPI0", "J", "*");

						jrParam.setField("CAR_NO" 	    , sCarNo);
						
//						if("N".equals(sApplyYnPI)) {
//							jrParam.setField("CARD_NO" 	    , sCardNo);	
//						}
						
						JDTORecordSet jsPntFrm = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCarPntFrmYnToUp_PIDEV", logId, mthdNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 ");
						if (jsPntFrm.size() > 0) {
							String ydFrmYn = commUtils.trim(jsPntFrm.getRecord(0).getFieldString("YD_FRM_YN"));
							// 형상여부
							if ("N".equals(ydFrmYn)) {
								// 상차지에 형상이 없으므로 동간이적 차량 도착처리 전문 송신 
								JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
								jrYdMsg.setField("JMS_TC_CD"		, "Y5YDL018" );
								jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
								jrYdMsg.setField("PT_LOAD_LOC"		, commUtils.trim(jsPntFrm.getRecord(0).getFieldString("YD_STK_COL_GP")));
								jrYdMsg.setField("CAR_NO"			, sCardNo );
								jrYdMsg.setField("CAR_UPDN_GP"		, "1");  //상차
								jrYdMsg.setField("BACKUP_YN"		, "Y");  
								
								jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);				
							}
						}
		    		}
				}	   			
    		}
			
			jrRtn.setField("YD_CAR_SCH_ID", ydCarSchId); //이력등록에 필요
			
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 

    
	/**
	 *      [A] 오퍼레이션명 : 동간이적차량 출발 처리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procYDOutCarLevWr(JDTORecord rcvMsg)throws JDTOException  {
		String mthdNm = "동간이적차량 출발 처리 [CCoilJspSeEJB.procYDOutCarLevWr] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();        
		String sLogMsg    = "";
		
	    try{
			commUtils.printLog(logId, mthdNm, "S+");	
			
			String szMsg	   = "";		
		    String ydCarDiffYn = "N"; //위치 동일차량여부	
		    
		    String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String transOrdDate = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DATE"));  //운송지시일자
	    	String transOrdSeqNo= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송지시순번
	    	String ydCarNo  	= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));          //차량번호
	    	String ydCardNo     = commUtils.trim(rcvMsg.getFieldString("CARD_NO"));         //카드번호
	    	String sposWlocCd   = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"));    //발지개소코드
	    	String sposYdPntCd  = commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"));  //발지포인트코드
	    	String sModifier  	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
	    	String sWrkGp   	= commUtils.trim(rcvMsg.getFieldString("WRK_GP"));      //'U' 상차작업 ,'D' 하차작업
	    	
	    	// PIDEV_S :병행가동용:PI_YD
	    	String sPI_YD   	= commUtils.trim(rcvMsg.getFieldString("PI_YD")); 
	    	String sPI_YD1   	= commUtils.trim(rcvMsg.getFieldString("PI_YD1"));
	    	
	    	if ("".equals(sModifier)) { sModifier = msgId; }
	    	
	    	if (transOrdDate.equals("")) {
	    		sLogMsg = "운송지시일자가 없습니다.";
				commUtils.printLog(logId, sLogMsg, "SL");
				throw new DAOException("TRANS_ORD_DATE Error");
	    	}
	    	
	    	if (transOrdSeqNo.equals("")) {
	    		sLogMsg = "운송지시순번이 없습니다.";
				commUtils.printLog(logId, sLogMsg, "SL");
				throw new DAOException("TRANS_ORD_SEQNO Error");
	    	}
	    	
	    	if (ydCarNo.equals("")) {
	    		sLogMsg = "차량번호가 없습니다.";
				commUtils.printLog(logId, sLogMsg, "SL");
				throw new DAOException("CAR_NO Error");
	    	}
	    	
	    	if (sposWlocCd.equals("")) {
	    		sLogMsg = "발지개소코드가 없습니다.";
				commUtils.printLog(logId, sLogMsg, "SL");
	    	}
	    	
	    	if (sposYdPntCd.equals("")) {
	    		sLogMsg = "발지포인트코드가 없습니다.";
				commUtils.printLog(logId, sLogMsg, "SL");
				throw new DAOException("SPOS_YD_PNT_CD Error");
	    	}
	    	
			
	    	//발지위치정보로 출발위치 Clear
	    	//열정보 Clear 업데이트 후 리턴값이 1이상이면 베드 단정보도 Clear
	    	//업데이트값이 없다면 그냥 종료
	    	//발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)
	    	JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("WLOC_CD",   sposWlocCd);
			jrParam.setField("YD_PNT_CD", sposYdPntCd);
	    	
			/* com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStkcolWLocCdandPntCd 
			SELECT A.YD_STK_COL_GP
			     , A.YD_LOC_GP
			     , A.YD_BAY_GP
			     , A.YD_EQP_GP
			     , A.YD_STK_COL_NO
			     , B.YD_STK_COL_ACT_STAT
			     , A.YD_STK_COL_RULE_XAXIS
			     , A.YD_STK_COL_RULE_YAXIS
			     , A.YD_STK_COL_W
			     , A.YD_STK_COL_L
			     , A.YD_CAR_USE_GP
			     , B.TRN_EQP_CD
			     , B.CAR_NO
			     , B.CARD_NO
			     , A.WLOC_CD
			     , A.YD_PNT_CD
			     , B.YD_CARPNT_CD
			  FROM TB_YD_STKCOL   A
			     , TB_YD_CARPOINT B
			 WHERE B.YD_STK_COL_GP = A.YD_STK_COL_GP
			   AND A.WLOC_CD       = :V_WLOC_CD
			   AND A.YD_PNT_CD     = :V_YD_PNT_CD
			   AND A.DEL_YN        = 'N'
			   AND B.DEL_YN        = 'N'
	    	 */	    	
	    	JDTORecordSet jsStkCol = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStkcolWLocCdandPntCd", logId, mthdNm, "적치열 조회"); 

	    	if (jsStkCol == null || jsStkCol.size() <= 0) {
	    		sLogMsg = mthdNm + "발지개소["+sposWlocCd+"] 및 포인트 코드["+sposYdPntCd+"]가 타공정코드가 아니고 대기장입니다.";
				commUtils.printLog(logId, sLogMsg, "SL");
				return jrRtn ;
				
	    	} else {
	    		
		    	//열구분을 조회(도착지)
		    	String ydCarldLevLoc    = commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_GP")); 
		    	String ydCarPntCdChk 	= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_CARPNT_CD")); 
		    	String ydCarNoChk 	    = commUtils.trim(jsStkCol.getRecord(0).getFieldString("CAR_NO"));
		    	String ydStkColActStat  = commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));
		    	
		    	if (!ydCarNoChk.equals(ydCarNo)) {
		 		   /**********************************************************
					* 다른 차량이 존재 하는 경우 
					**********************************************************/
		    		szMsg="["+mthdNm+"] 해당 위치에 다른 차량이 존재 합니다. 포인트 차량:"+ydCarNoChk + "  취소대상 차량:"+ ydCarNo;
		    		commUtils.printLog(logId, szMsg, "SL");	
		    		ydCarDiffYn = "Y";
		    	} else {
		    		/**********************************************************
					* 동일차량존재 
					**********************************************************/
		    		
		    		//---------------------------------------------------------------------------------------
    				JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
					
    				sndL2Msg.setField("YD_INFO_SYNC_CD"		, "4"                         ); //야드정보동기화코드
    				sndL2Msg.setField("MSG_GP"				, "I"                         ); //전문구분
    				sndL2Msg.setField("YD_STK_COL_GP"    	, ydCarldLevLoc);
    				sndL2Msg.setField("YD_STK_BED_NO"    	, "01");
					sndL2Msg.setField("YD_CAR_USE_GP"    	, "G"); //L:구내운송, G:출하차량
					sndL2Msg.setField("CAR_NO"  			, ydCarNo); //차량번호
					sndL2Msg.setField("CARD_NO"  			, ydCardNo); //카드번호
					sndL2Msg.setField("YD_CAR_ARRSTRT_STAT" , "S"); //A:도착, S:출발
					sndL2Msg.setField("YD_CAR_AIM_YD_GP"	, "J"); 
					
					if("U".equals(sWrkGp)) {
						//상차완료 후 출발
						sndL2Msg.setField("YD_EQP_WRK_STAT"  	, "L"); //U:공차, L:영차
					} else {
						//하차완료 후 출발
						sndL2Msg.setField("YD_EQP_WRK_STAT"  	, "U"); //U:공차, L:영차
					}
    	 
    				//전송 Data 생성
    				jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L001_CarInfo", sndL2Msg));		
    				szMsg="[" + mthdNm + "] 저장위치 : 코일야드L2 로 송신 열구분[" + ydCarldLevLoc + "] - 저장위치 제원 : 코일야드L2 로 송신 호출 "+jrRtn.size();
    				commUtils.printLog(logId, szMsg, "SL");
		    		//---------------------------------------------------------------------------------------
		    		
		    		
		    		//사용불가로 되어있으면 상태값을 바꾸지 않는다
		    		if (!"N".equals(ydStkColActStat)) {
		    			ydStkColActStat = "C";
		    		}		    		
		    		/********************************
					 * 적치열 상태 비활성화등록
					 *******************************/
		    		sLogMsg = mthdNm + "출발야드의 적치열["+ydCarldLevLoc+"]을 비활성상태로 변경처리 시작 ";
					commUtils.printLog(logId, sLogMsg, "SL");
					
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("YD_STK_COL_GP"		, ydCarldLevLoc);
			    	jrParam.setField("YD_STK_COL_ACT_STAT"	, ydStkColActStat);
			    	jrParam.setField("YD_CAR_USE_GP"		, "");
			    	jrParam.setField("TRN_EQP_CD"			, "");
			    	jrParam.setField("CAR_NO"				, "");
			    	jrParam.setField("CARD_NO"				, "");
			    	/* com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStkcolActYn
			    	UPDATE TB_YD_STKCOL
			    	   SET MODIFIER            = :V_MODIFIER
			    	     , MOD_DDTT            = SYSDATE
			    	     , YD_CAR_USE_GP       = :V_YD_CAR_USE_GP
			    	     , TRN_EQP_CD          = :V_TRN_EQP_CD
			    	     , CAR_NO              = :V_CAR_NO
			    	     , CARD_NO             = :V_CARD_NO
			    	     , YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
			    	 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			    	 */
			    	int intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStkcolActYn", logId, mthdNm, "TB_YD_STKCOL 등록");
					if (intRtnVal <= 0) {

						sLogMsg = mthdNm + "적치열[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, sLogMsg, "SL");
						throw new DAOException(sLogMsg);
					}

					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					 EJBConnector ejbConn3 = new EJBConnector("default","CCoilCarMvSeEJB",this);
				     ejbConn3.trx("YdCarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class},
					     	             new Object[]{"B","","",ydCarldLevLoc,"","",ydStkColActStat,logId,mthdNm,sModifier});	
				     
					/********************************
					 * 적치베드 상태 비활성화등록
					 *******************************/
				     jrParam.setField("YD_STK_BED_WT_MAX"  , CConstant.YD_STK_BED_WT_MAX_DEFAULT);
				     jrParam.setField("YD_STK_BED_ACT_STAT", "C");
    			    	
				     /* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkBedInit
				     UPDATE TB_YD_STKBED
				        SET MODIFIER  = :V_MODIFIER
				          , MOD_DDTT  = SYSDATE
				          , YD_STK_BED_WT_MAX   = :V_YD_STK_BED_WT_MAX
				          , YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
				      WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkBedInit", logId, mthdNm, "적치베드 비활성화");
					/********************************
					 * 적치단 상태 비활성화등록
					 *******************************/
					//야드적치단활성상태
					jrParam.setField("STL_NO"             , "" );
					jrParam.setField("YD_STK_LYR_ACT_STAT", "C");
					jrParam.setField("YD_STK_LYR_MTL_STAT", "E");
					/*
					UPDATE TB_YD_STKLYR            
					   SET YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
					     , STL_NO              = :V_STL_NO
					     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
					     , MODIFIER            = :V_MODIFIER
					     , MOD_DDTT            = SYSDATE
					 WHERE YD_STK_COL_GP  = :V_YD_STK_COL_GP
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStklyrYdStkColGp", logId, mthdNm, "적치단활성상태 수정");
    				if (intRtnVal <= 0) {

    					sLogMsg = mthdNm + " 적치단[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, sLogMsg, "SL");
						throw new DAOException(sLogMsg);

					}
		    	}
		    	
		    	if ("D".equals(sWrkGp)) {
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					// 하차 작업인 경우 차량스케줄 삭제
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			    	jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			    	jrParam.setField("TRANS_ORD_DATE"	, transOrdDate);
			    	jrParam.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);
			    	jrParam.setField("CAR_NO" 			, ydCarNo);		
			    	
					//PIDEV
//					String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
					
//					if("N".equals(sApplyYnPI)) {
//				    	jrParam.setField("CARD_NO"			, ydCardNo);						
//					}
					
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschTransDTSeq2 
					SELECT *
					  FROM (
					        SELECT *
					          FROM TB_YD_CARSCH
					         WHERE CAR_NO       LIKE :V_CAR_NO||'%'
					           AND CARD_NO         = :V_CARD_NO
					           AND TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
					           AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
					         ORDER BY YD_CAR_SCH_ID DESC
					       ) A
					 WHERE ROWNUM <= 1   
			    	*/
			    	JDTORecordSet jsCarResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschTransDTSeq2_PIDEV", logId, mthdNm, "차량스케줄  조회"); 
			    	
			    	
			    	if (jsCarResult.size() <= 0 ) {
			    		sLogMsg = "차량스케쥴 조회 오류 + ("+transOrdDate+", "+ ydCarNo + ", " + ydCardNo + ", 'G')";
						commUtils.printLog(logId, sLogMsg, "SL");
						return jrRtn ;
			    		
			    	} else {
			    
			    		jsCarResult.first();
						JDTORecord jrCarResult = jsCarResult.getRecord();
						String ydCarSchId = commUtils.trim(jrCarResult.getFieldString("YD_CAR_SCH_ID"     ));
						
						jrParam.setField("YD_CAR_SCH_ID" , ydCarSchId);
						jrParam.setField("DEL_YN" , "Y");
	
						/* com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarSchDelYn 
						UPDATE TB_YD_CARSCH
						   SET DEL_YN        = :V_DEL_YN
						     , MODIFIER      = :V_MODIFIER
						     , MOD_DDTT      = SYSDATE
						 WHERE YD_CAR_SCH_ID = :YD_CAR_SCH_ID
						 */
								    		
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarSchDelYn", logId, mthdNm, "TB_YD_CARSCH 차량 스케줄삭제");
			    	}
		    	}	
		    	
		    	commUtils.printLog(logId, "ydCarDiffYn:" + ydCarDiffYn, "SL");
		    	commUtils.printLog(logId, "입동포인트:" + ydCarPntCdChk, "SL");
		    	
				if ("N".equals(ydCarDiffYn)) {
					/****************************************************************************************************
					 * 차량도착위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구한다.
					 ***************************************************************************************************/
					jrParam.setField("YD_CARPNT_CD" , ydCarPntCdChk);
					commUtils.printLog(logId, "차량도착위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구한다.:", "SL");
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschNext 
					SELECT A.YD_CAR_SCH_ID
					     , A.CAR_NO 
					     , B.YD_STK_COL_GP    AS PT_LOAD_LOC
					     , CASE WHEN A.YD_EQP_WRK_STAT = 'U' THEN '1'
					            ELSE '2' END  AS CAR_UPDN_GP
					  FROM TB_YD_CARSCH A
					     , (SELECT * 
					          FROM USRYDA.TB_YD_CARPOINT 
					         WHERE DEL_YN = 'N'
					           AND YD_CARPNT_CD = :V_YD_CARPNT_CD
					       ) B    
					 WHERE 1=1
					   AND A.DEL_YN = 'N'
					   AND A.YD_CAR_WRK_GP = 'G'
					   AND CASE WHEN A.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN A.YD_CARLD_STOP_LOC 
					            ELSE A.YD_CARUD_STOP_LOC END = B.YD_STK_COL_GP
					 ORDER BY YD_BAYIN_WO_SEQ
					        , YD_CAR_SCH_ID 
			    	*/
			    	JDTORecordSet jsCarNext = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschNext", logId, mthdNm, "해당포인트 다음차량스케줄조회"); 
			    	if (jsCarNext.size() > 0 ) {
			    		JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setField("JMS_TC_CD"		, "Y5YDL018" );
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
						jrYdMsg.setField("PT_LOAD_LOC"		, commUtils.trim(jsCarNext.getRecord(0).getFieldString("PT_LOAD_LOC")));
						jrYdMsg.setField("CAR_NO"			, commUtils.trim(jsCarNext.getRecord(0).getFieldString("CAR_NO")) );
						jrYdMsg.setField("CAR_UPDN_GP"		, commUtils.trim(jsCarNext.getRecord(0).getFieldString("CAR_UPDN_GP")) ); 
						jrYdMsg.setField("BACKUP_YN"		, "N");
						
						// PIDEV_S :병행가동용:PI_YD
						jrYdMsg.setField("PI_YD"		, sPI_YD);
						jrYdMsg.setField("PI_YD1"		, sPI_YD1);
												
						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
			    	}
		    	}//end of if
	    	}
    	
		} catch (Exception e) {
			sLogMsg="동간이적출발실적 처리 Error:" +e.getMessage();
			commUtils.printLog(logId, sLogMsg, "SL");
			throw new DAOException(e);
		}

		return jrRtn;
	}// end of procYDOutCarLevWr()	
			
	/**
	 *      [A] 오퍼레이션명 : 저장위치제원요구(Y5YDL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL001(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "Y5저장위치제원요구[CCoilL2RcvSeEJB.rcvY5YDL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			JDTORecord jrRtn = null;
			
			//수신 항목 값
			String msgId		= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd	= commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp			= commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp		= commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); //야드동구분
			String ydEqpGp		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); //야드설비구분
			String ydStkColNo	= commUtils.trim(rcvMsg.getFieldString("YD_STK_COL_NO"  )); //야드적치열번호
			String ydStkBedNo	= commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"  )); //야드적치Bed번호
			
			String sModifier	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"       )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			JDTORecord jrParam	= commUtils.getParam(logId, mthdNm, sModifier);
			String sMsg			= "";

			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydInfoSyncCd)) {
				throw new Exception("야드정보동기화코드(YD_INFO_SYNC_CD) 없음");
			} else if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			} else if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
				throw new Exception("야드동구분(YD_BAY_GP) 없음");
			} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
				throw new Exception("야드설비구분(YD_EQP_GP) 없음");
			} else if ("".equals(ydEqpGp)) {
				throw new Exception("야드설비구분(YD_EQP_GP) 없음");
			} else if ("".equals(ydBayGp)) {
				throw new Exception("야드동구분(YD_BAY_GP) 없음");
			}

			if ("D".equals(ydInfoSyncCd) && "".equals(ydStkBedNo)) {
				throw new Exception("스크랩구역구분(YD_STK_BED_NO) 없음");
			}
			
			if ("C".equals(ydInfoSyncCd) && ("".equals(ydStkColNo) || "".equals(ydStkBedNo))) {
				// 크레인작업폭 ydStkColNo+ydStkBedNo 필수값
				throw new Exception("크레인 작업폭 값 없음");
			}
			
			/**********************************************************
			 * 1. YD_INFO_SYNC_CD = 'D' 스크랩 삭제
			 **********************************************************/
			if ("D".equals(ydInfoSyncCd)) {
    			sMsg = "YD_INFO_SYNC_CD["+ ydInfoSyncCd +"] YD_GP["+ ydGp +"] YD_BAY_GP["+ ydBayGp +"] YD_EQP_GP["+ ydEqpGp +"] YD_STK_COL_NO["+ ydStkColNo +"] YD_STK_BED_NO["+ ydStkBedNo +"] 스크랩 삭제";
	    		commUtils.printLog(logId, sMsg, "SL");

	    		String ydStkColGp = ydGp + ydBayGp + ydEqpGp + ydStkColNo;
	    		
	    		String sAreaGp = ydStkBedNo.substring(0, 1);
	    		
	    		if( "A".equals(sAreaGp) || "B".equals(sAreaGp) || "C".equals(sAreaGp) || "D".equals(sAreaGp) ) {
		    		jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("YD_STK_COL_GP", ydStkColGp);					// 스크랩 적치열
		    		jrParam.setField("AREA_GP"		, sAreaGp);	// A영역/B영역
					
		    		/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updClearScrapStockByCol
		    		UPDATE TB_YD_STOCK
		    		   SET DEL_YN   = 'Y'
		    		     , MODIFIER = :V_MODIFIER
		    		     , MOD_DDTT = SYSDATE
		    		 WHERE DEL_YN = 'N'
		    		   AND STL_NO IN (
		    		                    SELECT A.STL_NO
		    		                      FROM TB_YD_STKLYR A
		    		                         , (
		    		                            SELECT AA.YD_STK_COL_GP
		    		                                 , AA.YD_STK_BED_NO
		    		                                 , AA.YD_STK_LYR_NO
		    		                              FROM TB_YD_STKLYR AA
		    		                                 , TB_YD_RULE   BB
		    		                             WHERE AA.YD_STK_COL_GP = BB.CD_GP
		    		                               AND AA.YD_STK_BED_NO = BB.ITEM
		    		                               AND BB.REPR_CD_GP = 'SCRAP'
		    		                               AND BB.ITEM1     IN ('A', 'B', 'C', 'D')
		    		                               AND BB.CD_GP   LIKE :V_YD_STK_COL_GP
		    		                               AND BB.ITEM1   LIKE :V_AREA_GP
		    		                             UNION ALL
		    		                             --B구역일경우 전BED 2단 스크랩 삭제
		    		                            SELECT MIN(CD_GP) AS YD_STK_COL_GP
		    		                                 , LPAD(TO_NUMBER(MIN(ITEM))-1, 2, '0') AS YD_STK_BED_NO
		    		                                 , '002' AS YD_STK_LYR_NO
		    		                              FROM TB_YD_RULE
		    		                             WHERE REPR_CD_GP = 'SCRAP'
		    		                               AND CD_GP   LIKE :V_YD_STK_COL_GP
		    		                               AND ITEM1      = :V_AREA_GP
		    		                               AND :V_AREA_GP IN ('B','C','D')
		    		                           ) B
		    		                     WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
		    		                       AND A.YD_STK_BED_NO = B.YD_STK_BED_NO
		    		                       AND A.YD_STK_LYR_NO = B.YD_STK_LYR_NO
		    		                       AND A.DEL_YN        = 'N'
		    		                       AND A.STL_NO IS NOT NULL
		    		                 )
					*/
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updClearScrapStockByCol", logId, mthdNm, "저장품 스크랩 종료");
					
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updClearScrapLyrByCol
					UPDATE TB_YD_STKLYR
					   SET STL_NO              = NULL
					     , YD_STK_LYR_MTL_STAT = 'E'
					     , MODIFIER            = :V_MODIFIER
					     , MOD_DDTT            = SYSDATE
					 WHERE YD_STK_COL_GP LIKE 'J_SC%'
					   AND YD_STK_LYR_MTL_STAT = 'C'
					   AND YD_STK_COL_GP || YD_STK_BED_NO || YD_STK_LYR_NO IN (
					                                            SELECT A.YD_STK_COL_GP || A.YD_STK_BED_NO || A.YD_STK_LYR_NO
					                                              FROM TB_YD_STKLYR A
					                                                 , (
					                                                    SELECT AA.YD_STK_COL_GP
					                                                         , AA.YD_STK_BED_NO
					                                                         , AA.YD_STK_LYR_NO
					                                                      FROM TB_YD_STKLYR AA
					                                                         , TB_YD_RULE   BB
					                                                     WHERE AA.YD_STK_COL_GP = BB.CD_GP
					                                                       AND AA.YD_STK_BED_NO = BB.ITEM
					                                                       AND BB.REPR_CD_GP = 'SCRAP'
					                                                       AND BB.ITEM1     IN ('A', 'B', 'C', 'D')
					                                                       AND BB.CD_GP   LIKE :V_YD_STK_COL_GP
					                                                       AND BB.ITEM1   LIKE :V_AREA_GP
					                                                     UNION ALL
					                                                     --B구역일경우 전BED 2단 스크랩 삭제
					                                                    SELECT MIN(CD_GP) AS YD_STK_COL_GP
					                                                         , LPAD(TO_NUMBER(MIN(ITEM))-1, 2, '0') AS YD_STK_BED_NO
					                                                         , '002' AS YD_STK_LYR_NO
					                                                      FROM TB_YD_RULE
					                                                     WHERE REPR_CD_GP = 'SCRAP'
					                                                       AND CD_GP   LIKE :V_YD_STK_COL_GP
					                                                       AND ITEM1      = :V_AREA_GP
					                                                       AND :V_AREA_GP IN ('B','C','D')
					                                                   ) B
					                                             WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
					                                               AND A.YD_STK_BED_NO = B.YD_STK_BED_NO
					                                               AND A.YD_STK_LYR_NO = B.YD_STK_LYR_NO
					                                               AND A.DEL_YN        = 'N'
					                                               AND A.STL_NO IS NOT NULL
					                                         )
					*/
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updClearScrapLyrByCol", logId, mthdNm, "TB_YD_STKLYR 스크랩 삭제");
	    		} else {
	    			
		    		jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("YD_STK_COL_GP", ydStkColGp);
					jrParam.setField("YD_STK_BED_NO", ydStkBedNo);

					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updClearScrapStockByBed
					UPDATE TB_YD_STOCK
					   SET MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					     , DEL_YN   = 'Y'
					 WHERE DEL_YN   = 'N'
					   AND STL_NO IN (SELECT STL_NO
					                    FROM TB_YD_STKLYR
					                   WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
					                     AND 'Y' = CASE WHEN YD_STK_BED_NO = :V_YD_STK_BED_NO THEN 'Y'
					                                    WHEN YD_STK_BED_NO = LPAD(TO_NUMBER(:V_YD_STK_BED_NO)-1, 2, '0')
					                                         AND YD_STK_LYR_NO = '002'        THEN 'Y'
					                                    ELSE 'N'
					                               END
					                     AND DEL_YN = 'N'
					                     AND YD_STK_LYR_MTL_STAT = 'C'
					                 )
	    			*/
	    			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updClearScrapStockByBed", logId, mthdNm, "TB_YD_STOCK 스크랩 삭제");
	    			
	    			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updClearScrapLyrByBed
	    			UPDATE TB_YD_STKLYR
	    			   SET MODIFIER            = :V_MODIFIER
	    			     , MOD_DDTT            = SYSDATE
	    			     , STL_NO              = NULL
	    			     , YD_STK_LYR_MTL_STAT = 'E'
	    			 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
	    			   AND 'Y' = CASE WHEN YD_STK_BED_NO = :V_YD_STK_BED_NO THEN 'Y'
	    			                  WHEN YD_STK_BED_NO = LPAD(TO_NUMBER(:V_YD_STK_BED_NO)-1, 2, '0')
	    			                   AND YD_STK_LYR_NO = '002'        THEN 'Y'
	    			                  ELSE 'N'
	    			             END
	    			   AND DEL_YN              = 'N'
	    			   AND YD_STK_LYR_MTL_STAT = 'C'
	    			*/
	    			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updClearScrapLyrByBed", logId, mthdNm, "TB_YD_STKLYR 스크랩 삭제");
	    		}
			} else if( "T".equals(ydInfoSyncCd) ) {
				
				/****************************
				* 2. 정정보급존 자동이적 ON/OFF
				*****************************/
    			sMsg = "정정보급존 자동이적:YD_INFO_SYNC_CD["+ ydInfoSyncCd +"] YD_GP["+ ydGp +"] YD_BAY_GP["+ ydBayGp +"] YD_EQP_GP["+ ydEqpGp +"] YD_STK_COL_NO["+ ydStkColNo +"] YD_STK_BED_NO["+ ydStkBedNo +"]";
	    		commUtils.printLog(logId, sMsg, "SL");
	    		
	    		if( ydStkBedNo.length() < 1 ) {
	    			throw new Exception("보급존자동이적ON/OFF(YD_STK_BED_NO) 없음");
	    		} else if( !"Y".equals(ydStkBedNo) && !"N".equals(ydStkBedNo) ) {
	    			throw new Exception("보급존자동이적ON/OFF(YD_STK_BED_NO) 데이터에러["+ ydStkBedNo +"]");
	    		}
	    		
	    		jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	    		jrParam.setField("REPR_CD_GP"	, "APP103");
	    		jrParam.setField("YD_BAY_GP"	, ydBayGp);
	    		jrParam.setField("FLAG"			, ydStkBedNo);
	    		
	    		/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updSupplyZoneMoveYnByYdBayGp
	    		UPDATE TB_YD_RULE
	    		   SET ITEM1      = :V_FLAG
	    		     , MODIFIER   = :V_MODIFIER
	    		     , MOD_DDTT   = SYSDATE
	    		 WHERE REPR_CD_GP = :V_REPR_CD_GP
	    		   AND CD_GP      = :V_YD_BAY_GP
	    		   AND DEL_YN     = 'N'
	    		*/
	    		commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updSupplyZoneMoveYnByYdBayGp", logId, mthdNm, "정정보급존 동별 자동이적 ON/OFF");
	    		
	    		/**************************************
	    		 * ON 으로 업데이트시 정정보급존 자동이적 기동
	    		 **************************************/
	    		if( "Y".equals(ydStkBedNo) ) {
	    			
					jrParam.setField("YD_BAY_GP",	ydBayGp);
					jrParam.setField("IF_SEND",		"Y");		// 명령선택기동
					jrParam.setField("SINGLE_SEND",	"N");       // 한두개 적치된 열의 코일을 이동
					/***********************************************************
					*  정정보급존 자동이적
					***********************************************************/
					EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
					JDTORecord jrSupplyZoneMove = (JDTORecord)ejbConn.trx("procSupplyZoneMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });

					String rtnCd	= commUtils.nvl(jrSupplyZoneMove.getFieldString("RTN_CD"), "0");
					String rtnMsg	= commUtils.nvl(jrSupplyZoneMove.getFieldString("RTN_MSG"), "");
					
		    		commUtils.printLog(logId, "RTN_CD["+ rtnCd +"] RTN_MSG["+ rtnMsg +"]", "SL");
	    			
					//전송 Data 생성
					jrRtn = commUtils.addSndData(jrRtn, jrSupplyZoneMove);
	    		}
			} else if ("C".equals(ydInfoSyncCd)) {
				/**
				 *  크레인 그랩 폭
				 */
				int nCrW = Integer.parseInt(ydStkColNo + ydStkBedNo);
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);

				jrParam.setField("DTL_ITEM4", nCrW + ""); //크레인폭
				jrParam.setField("REPR_CD_GP", "APP840");
				jrParam.setField("CD_GP"     , "J"     );
				jrParam.setField("ITEM"      , "*"     );
				
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdRuleMgt", logId, mthdNm, "크레인 그랩 기준 변경");
				
			} else {

				/**********************************************************
				* 3. 저장위치제원(YDY5L001) 전문 생성
				**********************************************************/
				JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
	
				sndL2Msg.setField("YD_INFO_SYNC_CD"	, ydInfoSyncCd                         ); //야드정보동기화코드
				sndL2Msg.setField("YD_STK_COL_GP"  	, ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
				sndL2Msg.setField("YD_STK_BED_NO"  	, ydStkBedNo                           ); //야드적치Bed번호
	
				//전송 Data 생성
				jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L001", sndL2Msg));
			}

			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} //Y5YDL001
	
	/**
	 *      [A] 오퍼레이션명 : 저장품제원요구(Y5YDL002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL002(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "Y5저장품제원요구[CCoilL2RcvSeEJB.rcvY5YDL002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값 
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); //야드동구분
			String ydEqpGp      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); //야드설비구분
			String ydStkColNo   = commUtils.trim(rcvMsg.getFieldString("YD_STK_COL_NO"  )); //야드적치열번호
			String ydStkBedNo   = commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"  )); //야드적치Bed번호
			String stlNo        = commUtils.trim(rcvMsg.getFieldString("STL_NO"		    )); //재료번호
			
			String sModifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"       )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydInfoSyncCd)) {
				throw new Exception("야드정보동기화코드(YD_INFO_SYNC_CD) 없음");
			}	
			if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			}

			if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
				//저장위치별
				if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
					throw new Exception("야드동구분(YD_BAY_GP) 없음");
				} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
					throw new Exception("야드설비구분(YD_EQP_GP) 없음");
				}
			} else {
				//재료별
				if ("".equals(stlNo)) {
					throw new Exception("재료번호(STL_NO) 없음");
				}
			}

			/**********************************************************
			* 2. 저장품제원(YDY5L002) 전문 생성
			**********************************************************/
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);	//Log ID
			sndL2Msg.setResultMsg(mthdNm);	//Log Method Name

			sndL2Msg.setField("YD_INFO_SYNC_CD"	, ydInfoSyncCd                         ); //야드정보동기화코드
			sndL2Msg.setField("MSG_GP"			, "I"                         		   ); //전문구분
			sndL2Msg.setField("YD_STK_COL_GP"  	, ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			sndL2Msg.setField("YD_STK_BED_NO"  	, ydStkBedNo                           ); //야드적치Bed번호
			sndL2Msg.setField("YD_GP"          	, ydGp                                 ); //야드구분
			sndL2Msg.setField("STL_NO"       	, stlNo                                ); //재료번호

			JDTORecord jrRtn = null;
			//전송Data 생성
//			if (stlNo.startsWith("S") || "SC".equals(ydEqpGp)) {
//				jrRtn = commUtils.addSndData(coilDao.getMsgL2("YDY5L002_SCRAP", sndL2Msg));
//			} else {
				jrRtn = commUtils.addSndData(coilDao.getMsgL2("YDY5L002", sndL2Msg));
//			}

			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} //Y5YDL002	
	
	
	/**
	 *      [A] 오퍼레이션명 : 설비운전모드전환(Y5YDL003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL003(JDTORecord rcvMsg) throws DAOException {
		String mthdNm 	= "설비운전모드전환[CCoilL2RcvSeEJB.Y5YDL003] < " + rcvMsg.getResultMsg();
		String logId 		= rcvMsg.getResultCode();
		JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();
		JDTORecord resMsg 	= commUtils.getParam(logId, mthdNm, "Y5YDL003"); //크레인작업실적응답 전문 생성용
		boolean resYn 		= false;	//크레인작업실적응답 전문 전송여부

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId           = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId         = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" ));  // 1: On-Line, "2": Off-Line, "4": 일시정지, "5": 비상정지
			String ydEqpWrkMode2   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE2")); // A:무인, R:리모컨, E:정비, M:유인
			
			String sModifier       = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			//크레인작업실적응답 전문 전송여부를 Check
			if (sModifier.equals(msgId)) {
				//Backup 이 아니고 L2에서 인터페이스 수신된 경우만 응답 전문 전송여부 Check 
				if (ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4))) {
					resYn = true;
				}
			}
			
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE
			String sBR_GP     = ""; 	//ON_LINE/OFF_LINE  PARAM
			
			//크레인작업실적응답 전문 생성용
			resMsg = commUtils.getParam(logId, mthdNm, sModifier);
			resMsg.setField("YD_EQP_ID"       , ydEqpId       ); //야드설비ID
			resMsg.setField("YD_L2_WR_GP"     , "M"           ); //야드L2실적구분
			resMsg.setField("YD_L3_HD_RS_CD"  , "BR99"        ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:설비운전모드전환 수신처리"); //야드L3MESSAGE(Error)
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "BR01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "BR02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydEqpWrkMode)) {
				ydL3HdRsCd = "BR03";
				ydL3Msg    = "오류:운전모드 없음";
			} else if ("".equals(ydEqpWrkMode)) {
				ydL3HdRsCd = "BR04";
				ydL3Msg    = "오류:운전모드2 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
		
			jrParam.setField("YD_EQP_ID"	, ydEqpId        ); //야드설비ID
			
			/**********************************************************
			* 1. 설비모드 Check
			**********************************************************/
	        if (ydEqpWrkMode.equals(CConstant.YD_EQP_WRK_MODE_1)) { //1: On-Line
	        	
	        	jrParam.setField("YD_EQP_WRK_MODE"  	, ydEqpWrkMode); 	
	        	jrParam.setField("YD_EQP_AUTO_CRN_MODE" , ydEqpWrkMode); 	// 1: 정상
	        	
	        } else if (ydEqpWrkMode.equals(CConstant.YD_EQP_WRK_MODE_2)) {	//2: Off-Line
	        	
	        	jrParam.setField("YD_EQP_WRK_MODE"  	, ydEqpWrkMode); 	
	        	
	        } else { // 일시정지 ,비상정지
	        	
	        	jrParam.setField("YD_EQP_AUTO_CRN_MODE" , ydEqpWrkMode); 	// 4: 일시정지, 5:비상정지
	        	
	        }
	        
        	jrParam.setField("YD_EQP_WRK_MODE2"  		, ydEqpWrkMode2); 	// A:무인, R:리모컨, E:정비, M:유인
	        
        	/**********************************************************
			* 2. 설비상태 체크
			**********************************************************/
        	/*
			SELECT *
			  FROM TB_YD_EQP
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND DEL_YN = 'N'
        	 */
			JDTORecordSet jsEqp = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdEqp", logId, mthdNm, "설비상태 체크");
			
			String sOld_YdEqpWrkMode  = jsEqp.getRecord(0).getFieldString("YD_EQP_WRK_MODE" );
			String sOld_YdEqpWrkMode2 = jsEqp.getRecord(0).getFieldString("YD_EQP_WRK_MODE2");
			String sReSchYn           = "Y"; //리스케줄 여부
			
			if (ydEqpWrkMode.equals(sOld_YdEqpWrkMode)) {
				if (!ydEqpWrkMode2.equals(sOld_YdEqpWrkMode2)) {
					//설비상태는 이전과 같고 설비타입만 변경된경우 리스케줄링을 하지 않는다
					sReSchYn = "N";
				}	
			}
        	
        	/**********************************************************
			* 3. 설비상태 수정
			**********************************************************/
        	/*
        	--설비 상태 Mode 수정 
			UPDATE TB_YD_EQP
			   SET MODIFIER             = :V_MODIFIER
			      ,MOD_DDTT             = SYSDATE
			      ,YD_EQP_WRK_MODE      = nvl(:V_YD_EQP_WRK_MODE,YD_EQP_WRK_MODE)
			      ,YD_EQP_AUTO_CRN_MODE = nvl(:V_YD_EQP_AUTO_CRN_MODE,YD_EQP_AUTO_CRN_MODE)
			      ,YD_EQP_WRK_MODE2     = nvl(:V_YD_EQP_WRK_MODE2,YD_EQP_WRK_MODE2)
			 WHERE YD_EQP_ID   = :V_YD_EQP_ID
			   AND DEL_YN      = 'N'
        	*/	   
			//commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updStatEqpMode", logId, mthdNm, "설비상태 MODE 수정");
			commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updStatEqpMode", logId, mthdNm, "설비상태 MODE 수정");
			
			/**********************************************************
			* 6. 크레인작업실적응답 전문 전송(YDY5L005)
			**********************************************************/
			if (resYn) {
				commUtils.printLog(logId,"확인:"+resYn, "SL");
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
			}

			/**********************************************************
			* 7. 크레인 모드 변경시 
			*   - ON-LINE  : 스케줄요구호출 (작업지시전송)
			*   - OFF-LINE : 크레인 변경 처리
			**********************************************************/
			if ("CR".equals(ydEqpId.substring(2, 4))) {
			
		        if (ydEqpWrkMode.equals(CConstant.YD_EQP_WRK_MODE_1)) { //1: On-Line
		        	
		        	
					jrParam.setField("YD_EQP_ID", ydEqpId); //야드설비ID

					JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdEqp", logId, mthdNm, "설비상태조회");

					if ( jsChk.size() > 0) {
						String sYD_EQP_WRK_MODE = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE"));
						String ydLocGp			= commUtils.trim(jsChk.getRecord(0).getFieldString("YD_LOC_GP"));
						if (!sYD_EQP_WRK_MODE.equals(ydEqpWrkMode)) {
			        		sBR_GP = "R"; //복구  	
			        	}
						
						if ("CR".equals(ydEqpId.substring(2, 4)) && "1".equals(sYD_EQP_WRK_MODE)) {//on-line일때만 리스케줄
							
							if ("H".equals(ydLocGp) && "Y".equals(sReSchYn)) {  // 모드 변경시에는 리스케줄 안함 20220519
								
								//크레인 리스케줄
								jrParam.setField("JMS_TC_CD" , msgId); //수신 전문 I/F ID
								jrParam.setField("BR_GP"     , sBR_GP);
								jrParam.setField("RE_SCH_YN" , sReSchYn); 
//								jrRtn = commUtils.addSndData(jrRtn, this.trtCrnReschH(jrParam));
							} else {
								
								//크레인 리스케줄
								jrParam.setField("JMS_TC_CD", msgId); //수신 전문 I/F ID
								jrParam.setField("BR_GP" , sBR_GP);
								jrRtn = commUtils.addSndData(jrRtn, this.trtCrnResch(jrParam));
							}
						}
						
						String sYD_EQP_STAT = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT"));
						
						commUtils.printLog(logId, "크레인모드 변경후 명령선택기동", "SL");
						if (!"B".equals(sYD_EQP_STAT)) {
							
							/***********************************************************
							 * 크레인의 다음 스케줄 명령 선택 기동 - 운전모드변경 명령선택 기동 호출
							 ***********************************************************/
							//야드설비상태가 대기이면 명령선택전문 전송
							JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
							jrYdMsg.setField("JMS_TC_CD"         , "Y5YDL007"               ); //JMSTC코드
							jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
							jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
							jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태
							
							jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
						}
					}
		        	
		        } else if (ydEqpWrkMode.equals(CConstant.YD_EQP_WRK_MODE_2)) {	//2: Off-Line
		        	
					/*********************************************
					 * 크레인변경 처리 
					 ********************************************/
		        	/* 내부전문 또는 메소드 호출 선택*/
		        	jrParam.setField("YD_EQP_ID"	, ydEqpId        ); //야드설비ID
		        	jrRtn = commUtils.addSndData(jrRtn, this.offLineChgnCrn(jrParam));
		        }
			}
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "CCommSeEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { coilDao.getYDY5L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} // Y5YDL003
	
	
	/**
	 *      [A] 오퍼레이션명 : 설비고장복구실적(Y5YDL004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL004(JDTORecord rcvMsg) throws DAOException {
		String mthdNm 	= "설비고장복구실적[CCoilL2RcvSeEJB.Y5YDL004] < " + rcvMsg.getResultMsg();
		String logId 		= rcvMsg.getResultCode();
		JDTORecord resMsg 	= commUtils.getParam(logId, mthdNm, "Y5YDL004"); //크레인작업실적응답 전문 생성용
		boolean resYn 		= false;	//크레인작업실적응답 전문 전송여부

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId           = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId         = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"          )); //야드설비ID
			String ydEqpStat       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_STAT"        )); //야드설비상태(B:고장, N:정상, R:복구, P:롤이송 등)
			String ydEqpPauseCode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_PAUSE_CODE"  )); //야드설비휴지코드
			String ydEqpTrblRcvrDt = commUtils.trim(rcvMsg.getFieldString("YD_EQP_TRBL_RCVR_DT")); //야드설비고장복구일시
			
			String sModifier       = commUtils.trim(rcvMsg.getFieldString("MODIFIER"         )); //수정자(Backup Only)
			String brGp            = ""; //고장복구구분
			if ("".equals(sModifier)) { sModifier = msgId; }

			//크레인작업실적응답 전문 전송여부를 Check
			if (sModifier.equals(msgId)) {
				//Backup 이 아니고 L2에서 인터페이스 수신된 경우만 응답 전문 전송여부 Check 
				if (ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4))) {
					resYn = true;
				}
			}
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE
			
			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(mthdNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId	); //야드설비ID

			resMsg.setField("YD_L2_WR_GP"     , ydEqpStat); //야드L2실적 그대로 응답
			resMsg.setField("YD_L3_HD_RS_CD"  , "BR99"        ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:설비고장복구실적 수신처리"); //야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "BR01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "BR02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydEqpStat)) {
				ydL3HdRsCd = "BR03";
				ydL3Msg    = "오류:야드설비상태 없음";
			} else if ("".equals(ydEqpPauseCode) && "B".equals(ydEqpStat)) {
				ydL3HdRsCd = "BR04";
				ydL3Msg    = "오류:설비휴지코드 없음";
			} else if ("".equals(ydEqpTrblRcvrDt) && ("B".equals(ydEqpStat) || "N".equals(ydEqpStat) || "R".equals(ydEqpStat)|| "P".equals(ydEqpStat))) {
				ydL3HdRsCd = "BR05";
				ydL3Msg    = "오류:고장복구일시 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE

				jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
				jrRtn.setField("RTN_CD" 		, ydL3HdRsCd);	
				jrRtn.setField("RTN_MSG"		, ydL3Msg);
				return jrRtn;
			}
			
			
			/**********************************************************
			* 2. 설비상태 Check
			**********************************************************/
			if ("B".equals(ydEqpStat)||"P".equals(ydEqpStat)) {
				brGp = ydEqpStat; //고장
				if ("".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "B000";
				}
			} else if ("N".equals(ydEqpStat) || "R".equals(ydEqpStat)) { // 정상 또는 복구
				brGp = "R"; //복구
				if ("".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "0000";
					ydEqpStat = "N";
				}

				if ("CR".equals(ydEqpId.substring(2, 4))) {
					ydEqpStat = "W";
				} else {
					ydEqpStat = "N";
				}
			}
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);

			jrParam.setField("YD_EQP_ID"          , ydEqpId        ); //야드설비ID
			jrParam.setField("YD_EQP_PAUSE_CODE"  , ydEqpPauseCode ); //야드설비휴지코드
			jrParam.setField("YD_EQP_PAUSE_OCC_DT", ydEqpTrblRcvrDt); //야드설비휴지발생일시
			jrParam.setField("YD_EQP_STAT"        , ydEqpStat      ); //야드설비상태
			jrParam.setField("BR_GP"              , brGp           ); //고장복구구분

			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getStatEqp
			--설비상태조회
			SELECT YD_EQP_STAT          --야드설비상태
			     , YD_EQP_WRK_MODE      --야드설비작업Mode
			     , YD_EQP_WRK_MODE2     --야드설비작업Mode2
			     , YD_EQP_AUTO_CRN_MODE --무인크레인상태
			     , YD_LOC_GP
			  FROM TB_YD_EQP EQ
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND DEL_YN    = 'N'
			*/
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getStatEqp", logId, mthdNm, "설비상태조회");

			if (jsChk == null || jsChk.size() == 0) {
				//설비 Table 존재유무 Check
				ydL3HdRsCd = "BR11";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 정보 없음";
			} else if (ydEqpStat.equals(commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT")))) {
				//설비 Table 설비상태 Check
				ydL3HdRsCd = "BR12";
				ydL3Msg = "오류:현재 설비상태[" + ydEqpStat + "]와 동일";
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE

				jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
				jrRtn.setField("RTN_CD" 		, ydL3HdRsCd);	
				jrRtn.setField("RTN_MSG"		, ydL3Msg);
				
				return jrRtn;
			}
	        
        	/**********************************************************
			* 3. 설비상태 수정
			**********************************************************/
        	/* 
			UPDATE TB_YD_EQP
			   SET MODIFIER    = :V_MODIFIER
			     , MOD_DDTT    = SYSDATE
			     , YD_EQP_STAT = CASE WHEN YD_EQP_ID LIKE 'J_TC%' AND YD_EQP_STAT IN ('A') AND :V_YD_EQP_STAT = 'N'  THEN YD_EQP_STAT 
			                          ELSE :V_YD_EQP_STAT END
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND DEL_YN    = 'N'
        	*/	   
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updStatEqp", logId, mthdNm, "설비상태  수정");

			/**********************************************************
			* 4. 설비휴지 등록
			**********************************************************/
			if (!"".equals(brGp)) {
				/*
				MERGE INTO TB_YD_EQPPAUSE EP USING (
				SELECT DECODE(DD.NEW_YN,'Y',TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||'XXXXXX' ,EP.YD_EQP_PAUSE_OCCR_SEQ) AS YD_EQP_PAUSE_OCCR_SEQ
				      ,DD.YD_EQP_ID
				      ,DD.MODIFIER
				      ,SYSDATE AS MOD_DDTT
				      ,'N'     AS DEL_YN
				      ,DECODE(DD.NEW_YN,'Y',DD.YD_EQP_PAUSE_CODE) AS YD_EQP_PAUSE_CODE
				      ,DECODE(DD.NEW_YN,'Y',SF_YD_WRK_DUTY (DD.YD_EQP_PAUSE_OCC_DT)) AS YD_EQP_PAUSE_OCC_WRK_DUTY
				      ,DECODE(DD.NEW_YN,'Y',SF_YD_WRK_PARTY(DD.YD_EQP_PAUSE_OCC_DT)) AS YD_EQP_PAUSE_OCC_WRK_PARTY
				      ,DECODE(DD.NEW_YN,'Y',DD.YD_EQP_PAUSE_OCC_DT) AS YD_EQP_PAUSE_OCC_DT
				      ,DECODE(DD.NEW_YN,'N',DD.YD_EQP_PAUSE_OCC_DT) AS YD_EQP_PAUSE_END_DT
				      ,DECODE(DD.NEW_YN,'N',SF_YD_WRK_DUTY (DD.YD_EQP_PAUSE_OCC_DT))  AS YD_EQP_PAUSE_END_WRK_DUTY
				      ,DECODE(DD.NEW_YN,'N',SF_YD_WRK_PARTY(DD.YD_EQP_PAUSE_OCC_DT)) AS YD_EQP_PAUSE_END_WRK_PARTY
				      ,DECODE(DD.NEW_YN,'N',ROUND((DD.YD_EQP_PAUSE_OCC_DT - EP.YD_EQP_PAUSE_OCC_DT) * 1440)) AS YD_EQP_PAUSE_PASS_HR
				  FROM TB_YD_EQPPAUSE EP
				      ,(
				        SELECT A.*
				              ,MAX(B.YD_EQP_PAUSE_OCCR_SEQ) AS YD_EQP_PAUSE_OCCR_SEQ
				              ,CASE WHEN A.BR_GP = 'B' OR  MAX(B.YD_EQP_PAUSE_OCCR_SEQ) IS NULL THEN 'Y' ELSE 'N' END AS NEW_YN --신규여부
				              ,CASE WHEN A.BR_GP = 'R' AND MAX(B.YD_EQP_PAUSE_OCCR_SEQ) IS NULL THEN 'N' ELSE 'Y' END AS REG_YN --등록여부
				          FROM (
				                SELECT :V_YD_EQP_ID           AS YD_EQP_ID
				                      ,:V_MODIFIER            AS MODIFIER
				                      ,:V_YD_EQP_PAUSE_CODE   AS YD_EQP_PAUSE_CODE
				                      ,TO_DATE(:V_YD_EQP_PAUSE_OCC_DT, 'YYYYMMDDHH24MISS') AS YD_EQP_PAUSE_OCC_DT
				                      ,:V_BR_GP               AS BR_GP --고장복구구분
				                  FROM DUAL
				               ) A
				              ,TB_YD_EQPPAUSE B
				         WHERE A.YD_EQP_ID = B.YD_EQP_ID(+)
				       ) DD
				 WHERE DD.YD_EQP_ID = EP.YD_EQP_ID(+)
				   AND DD.YD_EQP_PAUSE_OCCR_SEQ = EP.YD_EQP_PAUSE_OCCR_SEQ(+)
				   AND DD.REG_YN = 'Y'
				) DD ON (EP.YD_EQP_PAUSE_OCCR_SEQ = DD.YD_EQP_PAUSE_OCCR_SEQ AND EP.YD_EQP_ID = DD.YD_EQP_ID)
				--복구 수신시 UPDATE
				WHEN MATCHED THEN
				UPDATE SET
				  EP.MODIFIER = DD.MODIFIER
				, EP.MOD_DDTT = DD.MOD_DDTT
				, EP.YD_EQP_PAUSE_END_DT        = DD.YD_EQP_PAUSE_END_DT
				, EP.YD_EQP_PAUSE_END_WRK_DUTY  = DD.YD_EQP_PAUSE_END_WRK_DUTY
				, EP.YD_EQP_PAUSE_END_WRK_PARTY = DD.YD_EQP_PAUSE_END_WRK_PARTY
				, EP.YD_EQP_PAUSE_PASS_HR       = DD.YD_EQP_PAUSE_PASS_HR
				--고장 수신시 INSERT
				WHEN NOT MATCHED THEN
				INSERT (
				  EP.YD_EQP_PAUSE_OCCR_SEQ
				, EP.YD_EQP_ID
				, EP.REGISTER
				, EP.REG_DDTT
				, EP.MODIFIER
				, EP.MOD_DDTT
				, EP.DEL_YN
				, EP.YD_EQP_PAUSE_CODE
				, EP.YD_EQP_PAUSE_OCC_WRK_DUTY
				, EP.YD_EQP_PAUSE_OCC_WRK_PARTY
				, EP.YD_EQP_PAUSE_OCC_DT
				) VALUES (
				  TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') || TO_CHAR(USRYDA.YD_EQPPAUSE_SEQ.NEXTVAL,'FM000000') 
				, DD.YD_EQP_ID
				, DD.MODIFIER
				, DD.MOD_DDTT
				, DD.MODIFIER
				, DD.MOD_DDTT
				, DD.DEL_YN
				, DD.YD_EQP_PAUSE_CODE
				, DD.YD_EQP_PAUSE_OCC_WRK_DUTY
				, DD.YD_EQP_PAUSE_OCC_WRK_PARTY
				, DD.YD_EQP_PAUSE_OCC_DT
				) 
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpPause", logId, mthdNm, "설비휴지 등록");
			}
			
			/**********************************************************
			* 5. 크레인 리스케줄 
			*  - 고장복구구분 [R:복구 리스케줄, B:고장 리스케줄]
			*  - 작업예약 야드스케쥴우선순위 수정
			*  - 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			*  - 대기상태인 야드설비ID에 해당하는 크레인작업지시 전문 추가
			**********************************************************/
			String sYD_EQP_WRK_MODE	= commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE"));
			String ydLocGp			= commUtils.trim(jsChk.getRecord(0).getFieldString("YD_LOC_GP"));
			if ("CR".equals(ydEqpId.substring(2, 4)) && "1".equals(sYD_EQP_WRK_MODE)) {//on-line일때만 리스케줄
				//크레인 리스케줄
				jrParam.setField("JMS_TC_CD", msgId); //수신 전문 I/F ID
				if ("B".equals(brGp)) {
					jrRtn = commUtils.addSndData(jrRtn, this.offLineChgnCrn(jrParam));
				} else {
					
					if( "H".equals(ydLocGp) ) {
						jrRtn = commUtils.addSndData(jrRtn, this.trtCrnReschH(jrParam));
					}
				}
			}

			/**********************************************************
			* 6. 크레인작업실적응답 전문 전송(YDY5L005)
			**********************************************************/
			if (resYn) {
				commUtils.printLog(logId,"확인:"+resYn, "SL");
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
				jrRtn.setField("RTN_CD" 			, "1"); //화면 백업용
				jrRtn.setField("RTN_MSG"			, "전문송신 성공");
			}

			commUtils.printLog(logId, mthdNm, "S-");

			jrRtn.setField("RTN_CD" 		, "1");	
			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "CCommSeEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { coilDao.getYDY5L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
		
	
	/**
	 *      [A] 오퍼레이션명 : 대차이동실적(Y5YDL011) - 개발중
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL011(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "대차이동실적[CCoilL2RcvSeEJB.rcvY5YDL011] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		JDTORecord resMsg = commUtils.getParam(logId, mthdNm, "Y5YDL011"); //크레인작업실적응답 전문 생성용
		boolean resYn 	  = false;	//크레인작업실적응답 전문 전송여부
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId             = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId           = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydBayGp           = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"       )); //야드동구분
			String ydMoveGp          = commUtils.trim(rcvMsg.getFieldString("YD_MOVE_GP"      )); //야드대차이동구분 S: 출발,  M: 이동 중,  E: 도착
			String ydTcarCurrBay     = commUtils.trim(rcvMsg.getFieldString("YD_TCAR_CURR_BAY")); //현재동
			String ydTcarAimBay      = commUtils.trim(rcvMsg.getFieldString("YD_TCAR_AIM_BAY" )); //목적동
			String ydTcarMoveDir     = commUtils.trim(rcvMsg.getFieldString("YD_TCAR_MOVE_DIR")); //대차이동방향 F : FORWARD B : BACKWARD
			
			String sModifier         = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; resYn = true;}
			
			String sMsg              = "";          
			String ydGp              = "J";
			String ydEqpStat         = "";
			
			String ydEqpWrkStat         = ""; //야드설비작업상태
			String ydTcarSchId          = ""; //대차스케줄
			String ydCarldStopLoc       = ""; //야드상차정지위치
			String ydCarldSchReqGp      = ""; //야드상차스케줄요청구분
			String ydCarldWrkBookId     = ""; //야드상차작업약ID
			
			String ydCarudSchReqGp      = ""; //야드하차스케줄요청구분
			String ydCarudStopLoc       = ""; //야드하차정지위치
			String ydCarProgStat        = ""; //야드차량진행상태
//			String ydAimBayGp           = ""; //하차동
			String sCrnSchSendYn        = "N";

			String ydLocGp              = "";
			String sAPP834_YN           = coilDao.ApplyYn(logId, mthdNm, "APP834", "J", "*"); // 대차L2백업시 스케줄 기동여부
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);

			//응답용 전문 set
            resMsg.setField("MODIFIER"        , sModifier    );	//수정자
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_L2_WR_GP"     , "T"          ); //야드L2실적구분(대차)
			resMsg.setField("YD_L3_HD_RS_CD"  , "E099"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:대차이동실적 수신처리"); //야드L3MESSAGE(Error)
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			} else if ("".equals(ydMoveGp)) {
				throw new Exception("대차이동구분(YD_MOVE_GP) 없음");
			} else if ("".equals(ydTcarCurrBay)) {
				throw new Exception("현재동(YD_TCAR_CURR_BAY) 없음");
			} else if ("".equals(ydTcarAimBay)) {
				throw new Exception("목적동(YD_TCAR_AIM_BAY) 없음");
			} else if ("".equals(ydTcarMoveDir)) {
				throw new Exception("이동방향값(YD_TCAR_MOVE_DIR) 없음");
			}

			if (!"S".equals(ydMoveGp) && !"E".equals(ydMoveGp)) {
				sMsg =  "대차이동구분[" + ydMoveGp + "]이 'S' 또는 'E'가 아니므로 종료";
				commUtils.printLog(logId, sMsg, "S-");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				return jrRtn;
			}
			
			String ydStkColGp  = ydGp + ydBayGp + ydEqpId.substring(2, 6); // 현재동
			
			JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
			jrYdMsg.setField("JMS_TC_CD"		 , "YDYDJ552"); //
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
			
			/**********************************************************
			* 1. 대차스케줄상태check
			**********************************************************/
			jrParam.setField("YD_EQP_ID", ydEqpId);
			
			/*
			SELECT *
			  FROM TB_YD_EQP
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND DEL_YN    = 'N'
			 */
			JDTORecordSet jsYdEqp = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdEqp", logId, mthdNm, "대차 설비 조회");
			
			if (jsYdEqp.size() < 0) {
				sMsg = "해당 설비ID ["+ydEqpId+"] 존재하지 않음";
				commUtils.printLog(logId, sMsg, "S-");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				return jrRtn;
			}
			
			ydLocGp   = commUtils.trim(jsYdEqp.getRecord(0).getFieldString("YD_LOC_GP")); //소재, 제품대차 구분
			
			ydEqpStat = commUtils.trim(jsYdEqp.getRecord(0).getFieldString("YD_EQP_STAT"));
			
			/*
			SELECT *
			  FROM TB_YD_TCARSCH
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND DEL_YN    = 'N'
			 */
			JDTORecordSet jsTcarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdTcarSchByYdEqpId", logId, mthdNm, "대차스케줄조회");
			
			if (jsTcarSch.size() > 0) {
				ydEqpWrkStat     = jsTcarSch.getRecord(0).getFieldString("YD_EQP_WRK_STAT"     );  //야드설비작업상태      
				ydTcarSchId      = jsTcarSch.getRecord(0).getFieldString("YD_TCAR_SCH_ID"      );  //대차스케줄         
				ydCarldStopLoc   = jsTcarSch.getRecord(0).getFieldString("YD_CARLD_STOP_LOC"   );  //야드상차정지위치      
				ydCarldSchReqGp  = jsTcarSch.getRecord(0).getFieldString("YD_CARLD_SCH_REQ_GP" );  //야드상차스케줄요청구분   
				ydCarldWrkBookId = jsTcarSch.getRecord(0).getFieldString("YD_CARLD_WRK_BOOK_ID");  //야드상차작업약ID     
				ydCarudSchReqGp  = jsTcarSch.getRecord(0).getFieldString("YD_CARUD_SCH_REQ_GP" );  //야드하차스케줄요청구분                   
				ydCarudStopLoc   = jsTcarSch.getRecord(0).getFieldString("YD_CARUD_STOP_LOC"   );  //야드하차정지위치      
				ydCarProgStat    = jsTcarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT"    );  //야드차량진행상태      
			} else {                                                                             
				sMsg = "해당 설비ID ["+ydEqpId+"]에 해당하는 대차스케줄 존재하지 않음";
				commUtils.printLog(logId, sMsg, "S-");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				if (resYn) {
					resMsg.setField("YD_L3_HD_RS_CD", "E001"); //야드L3처리결과코드(정상)
					resMsg.setField("YD_L3_MSG"     , "대차스케줄 존재하지 않음"); //야드L3MESSAGE
					jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
				}
				return jrRtn;
			}
			
			jrParam.setField("YD_TCAR_SCH_ID"      , ydTcarSchId);
//?			jrParam.setField("YD_CARLD_STOP_LOC"   , ydCarldStopLoc);
//?			jrParam.setField("YD_CARLD_WRK_BOOK_ID", ydCarldWrkBookId);
//?			jrParam.setField("YD_CARUD_SCH_REQ_GP" , ydCarudSchReqGp);
//?			jrParam.setField("YD_CARUD_STOP_LOC"   , ydCarudStopLoc  );
//?			jrParam.setField("YD_CAR_PROG_STAT"    , ydCarProgStat  );

			commUtils.printLog(logId, "sAPP834_YN : " + sAPP834_YN , "SL");
			commUtils.printLog(logId, "ydLocGp : " + ydLocGp , "SL");
			commUtils.printLog(logId, "ydTcarMoveDir : " + ydTcarMoveDir , "SL");
			
			if ("Y".equals(sAPP834_YN) && "J".equals(ydLocGp) && "X".equals(ydTcarMoveDir)) {
				//대차이동방향이 X일경우 백업. 제품대품대차일 경우에만 처리
			} else
			if ("E".equals(ydMoveGp) && "A".equals(ydEqpStat)) { //공대차 도착
				sMsg = "해당 설비ID ["+ydEqpId+"] 상태가 이미 도착 상태입니다. ";
				commUtils.printLog(logId, sMsg, "S-");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				if (resYn) {
					resMsg.setField("YD_L3_HD_RS_CD", "E002"); //야드L3처리결과코드(정상)
					resMsg.setField("YD_L3_MSG"     , "이미 도착 상태"); //야드L3MESSAGE
					jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
				}
				return jrRtn;
			}
			
			/**********************************************************
			* 1. 설비 상태 수정 
			**********************************************************/
			sMsg = "해당 설비ID ["+ydEqpId+"] " + "ydMoveGp:" +ydMoveGp ;
			commUtils.printLog(logId, sMsg, "SL");
			
			if ("S".equals(ydMoveGp) || "M".equals(ydMoveGp)) {
				ydEqpStat = "M"; //이동
			} else if ("E".equals(ydMoveGp)) {
				ydEqpStat = "A"; //도착
			}

			jrParam.setField("YD_EQP_ID"     , ydEqpId  );
			jrParam.setField("YD_CURR_BAY_GP", ydBayGp  );
			jrParam.setField("YD_EQP_STAT"	 , ydEqpStat); 
			
			/*
			UPDATE TB_YD_EQP
			   SET YD_CURR_BAY_GP = :V_YD_CURR_BAY_GP
			     , YD_EQP_STAT    = :V_YD_EQP_STAT
			     , MODIFIER       = :V_MODIFIER
			     , MOD_DDTT       = SYSDATE
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			 */
			commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdEqpStat", logId, mthdNm, "설비상태 수정");
			

			/**********************************************************
			* 1. 공차/영차 출발 
			**********************************************************/

			sMsg = "해당 설비ID ["+ydEqpId+"] " + "ydMoveGp:" +ydMoveGp + " ydEqpWrkStat: " + ydEqpWrkStat ;
			commUtils.printLog(logId, sMsg, "SL");
			
			if ("S".equals(ydMoveGp)) { //출발
				
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_TCAR_SCH_ID"      , ydTcarSchId);
				
				if ("U".equals(ydEqpWrkStat)) { //공차출발
					
					sMsg = "해당 설비ID ["+ydEqpId+"] " + "공차출발" ;
					commUtils.printLog(logId, sMsg, "SL");

					jrParam.setField("YD_CAR_PROG_STAT"    , "1");
					jrParam.setField("YD_CARLD_LEV_LOC"    , ydStkColGp);
					jrParam.setField("YD_CARLD_STOP_LOC"   , ydStkColGp);
					jrParam.setField("YD_CARLD_LEV_DT"     , commUtils.getDateTime14());
					
				} 
				
				if ("L".equals(ydEqpWrkStat)) { //영차출발
					sMsg = "해당 설비ID ["+ydEqpId+"] " + "영차출발" ;
					commUtils.printLog(logId, sMsg, "SL");
					
					jrParam.setField("YD_TCAR_SCH_ID"   , ydTcarSchId);	
					jrParam.setField("YD_CAR_PROG_STAT" , "A");
					jrParam.setField("YD_CARUD_LEV_DT"  , commUtils.getDateTime14());
				}
				
				/* 
				UPDATE TB_YD_TCARSCH
				   SET MODIFIER          = :V_MODIFIER
				     , MOD_DDTT          = SYSDATE
				     , YD_CAR_PROG_STAT  = :V_YD_CAR_PROG_STAT
				     , YD_CARLD_LEV_LOC  = NVL(:V_YD_CARLD_LEV_LOC , YD_CARLD_LEV_LOC)
				     , YD_CARLD_STOP_LOC = NVL(:V_YD_CARLD_STOP_LOC, YD_CARLD_STOP_LOC)
				     , YD_CARLD_LEV_DT   = NVL(TO_DATE(:V_YD_CARLD_LEV_DT, 'YYYYMMDDHH24MISS'), YD_CARLD_LEV_DT)
				     , YD_CARUD_LEV_DT   = NVL(TO_DATE(:V_YD_CARUD_LEV_DT, 'YYYYMMDDHH24MISS'), YD_CARUD_LEV_DT)
				 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updTcarSchProgStatS", logId, mthdNm, "대차진행상태 수정(출발)");
				
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				//출발위치 BED상태 비활성화
				jrParam.setField("YD_STK_COL_GP"      , ydGp + "_" + ydEqpId.substring(2, 6));
				jrParam.setField("YD_STK_BED_ACT_STAT", "C"); //비활성화
				/*
				UPDATE TB_YD_STKBED
				   SET MODIFIER             = :V_MODIFIER
				     , MOD_DDTT             = SYSDATE
				     , YD_STK_BED_ACT_STAT  = :V_YD_STK_BED_ACT_STAT
				 WHERE YD_STK_COL_GP     LIKE :V_YD_STK_COL_GP
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkBedTcarS", logId, mthdNm, "적치Bed활성상태 수정");
				
				//야드적치단활성상태
				jrParam.setField("STL_NO"             , "" );
				jrParam.setField("YD_STK_LYR_ACT_STAT", "C");
				jrParam.setField("YD_STK_LYR_MTL_STAT", "E");
				/*
				UPDATE TB_YD_STKLYR            
				   SET MODIFIER            = :V_MODIFIER
				     , MOD_DDTT            = SYSDATE
				     , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
				     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
				     , STL_NO              = :V_STL_NO
				 WHERE YD_STK_COL_GP    LIKE :V_YD_STK_COL_GP
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkLyrTcarS", logId, mthdNm, "적치단활성상태 수정");
				
				if ("L".equals(ydEqpWrkStat)) { //영차출발
					
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("YD_TCAR_SCH_ID"      , ydTcarSchId);
					
					/*********************************************
					 *  크레인 하차 예약 등록 여부 CHECK 하여 없으면 생성
					 *********************************************/
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getY5YDL011WbTCar
					SELECT YD_TCAR_SCH_ID
					     , STL_NO
					     , YD_SCH_CD
					     , (SELECT YD_WRK_CRN_PRIOR FROM TB_YD_SCHRULE WHERE YD_SCH_CD = A.YD_SCH_CD) AS YD_WRK_CRN_PRIOR
					     , WB_CNT
					     , YD_TO_LOC_GUIDE
					  FROM (SELECT A.YD_TCAR_SCH_ID
					             , B.STL_NO
					             -- 대차하차나 동간입고를 대표로 대차하차
					             , A.YD_CARUD_STOP_LOC||CASE WHEN C.YD_LOC_GP = 'H' THEN 'LH' ELSE 'LM' END AS YD_SCH_CD
					             --하차 스케쥴 유무검색
					             , (SELECT COUNT(*)
					                  FROM TB_YD_WRKBOOK    WB
					                     , TB_YD_WRKBOOKMTL WM
					                 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
					                   AND WB.DEL_YN = 'N'
					                   AND WM.DEL_YN = 'N'
					                   AND SUBSTR(WB.YD_SCH_CD,1,6) = A.YD_CARUD_STOP_LOC
					                   AND 'Y' = CASE WHEN C.YD_LOC_GP = 'H' AND SUBSTR(WB.YD_SCH_CD,7,2)   = 'LH'       THEN 'Y'
					                                  WHEN C.YD_LOC_GP = 'J' AND SUBSTR(WB.YD_SCH_CD,7,2) IN ('LM','MM') THEN 'Y'
					                                  ELSE 'N' END
					                   AND WM.STL_NO = B.STL_NO
					               ) AS WB_CNT
					             , (SELECT YD_TO_LOC_GUIDE
					                  FROM TB_YD_WRKBOOK
					                 WHERE YD_WBOOK_ID = (SELECT MAX(WB.YD_WBOOK_ID)
					                                        FROM TB_YD_WRKBOOK    WB
					                                           , TB_YD_WRKBOOKMTL WM
					                                       WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
					                                         AND WB.DEL_YN = 'Y'
					                                         AND WM.DEL_YN = 'Y'
					                                         AND SUBSTR(WB.YD_SCH_CD,1,6) = A.YD_CARLD_STOP_LOC
					                                         AND 'Y' = CASE WHEN C.YD_LOC_GP = 'H' AND SUBSTR(WB.YD_SCH_CD,7,2)   = 'UH'       THEN 'Y'
					                                                        WHEN C.YD_LOC_GP = 'J' AND SUBSTR(WB.YD_SCH_CD,7,2) IN ('UM','MM') THEN 'Y'
					                                                        ELSE 'N' END
					                                         AND WM.STL_NO = B.STL_NO
					                                     )
					               ) AS YD_TO_LOC_GUIDE
					          FROM TB_YD_TCARSCH     A
					             , TB_YD_TCARFTMVMTL B
					             , TB_YD_EQP         C
					         WHERE A.YD_TCAR_SCH_ID = B.YD_TCAR_SCH_ID
					           AND A.DEL_YN = 'N'
					           AND B.DEL_YN = 'N'
					           AND A.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
					           AND A.YD_EQP_ID      = C.YD_EQP_ID
					       ) A
					 WHERE WB_CNT = 0
					*/	   
					JDTORecordSet jsDnTc = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getY5YDL011WbTCar", logId, mthdNm, "권하대상재!!!");
					String ydNewWbookId = ""; 
					if (jsDnTc.size() > 0 ) {
						
						JDTORecord jrParamTc = JDTORecordFactory.getInstance().create();
						JDTORecord jrDnTc 	 = JDTORecordFactory.getInstance().create();
						for(int nIdx=0; nIdx < jsDnTc.size(); nIdx++) {
							
							jrParamTc = JDTORecordFactory.getInstance().create();
							jrDnTc    = JDTORecordFactory.getInstance().create();
							//대차스케줄 처리 (영대차출발지시)
							//야드하차작업예약ID 생성
							String ydCarudWrkBookId = coilDao.getSeqId(logId, mthdNm, "WrkBook");
							
							//작업예약 등록
							jrDnTc = jsDnTc.getRecord(nIdx);
							
							if (nIdx == 0 ) {
								ydNewWbookId = ydCarudWrkBookId;
							}
							jrParamTc = commUtils.getParam(logId, mthdNm, sModifier);
							jrParamTc.setField("YD_CARUD_WRK_BOOK_ID", ydCarudWrkBookId); //야드하차작업예약ID
							jrParamTc.setField("YD_WBOOK_ID"         , ydCarudWrkBookId ); 
							jrParamTc.setField("YD_SCH_CD"           , commUtils.trim(jrDnTc.getFieldString("YD_SCH_CD"))); //스케쥴 코드
							jrParamTc.setField("YD_WRK_CRN_PRIOR"    , commUtils.trim(jrDnTc.getFieldString("YD_WRK_CRN_PRIOR"))); //대차설비id
							jrParamTc.setField("YD_EQP_ID"           , ydEqpId       	); //대차설비id
							jrParamTc.setField("STL_NO" 			 , commUtils.trim(jrDnTc.getFieldString("STL_NO"))); 
							jrParamTc.setField("YD_TO_LOC_GUIDE" 	 , commUtils.trim(jrDnTc.getFieldString("YD_TO_LOC_GUIDE"))); 
												        
							/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insY5YDL011WbTCarIns
							INSERT INTO TB_YD_WRKBOOK WB
							       (WB.YD_WBOOK_ID         , WB.REGISTER              , WB.REG_DDTT              ,
							        WB.DEL_YN              , WB.YD_GP                 , WB.YD_BAY_GP             , WB.YD_SCH_CD   , WB.YD_SCH_PRIOR ,
							        WB.YD_SCH_PROG_STAT    , WB.YD_SCH_ST_GP          , WB.YD_SCH_REQ_GP         , WB.YD_AIM_YD_GP, WB.YD_AIM_BAY_GP,
							        WB.YD_TO_LOC_DCSN_MTD  , WB.YD_TO_LOC_GUIDE       , WB.YD_WRK_PLAN_TCAR)
							VALUES (:V_YD_CARUD_WRK_BOOK_ID, :V_MODIFIER              ,  SYSDATE                 ,
							        'N'                    , SUBSTR(:V_YD_SCH_CD,1,1) , SUBSTR(:V_YD_SCH_CD,2,1) , :V_YD_SCH_CD   , :V_YD_WRK_CRN_PRIOR ,
							        'W'                    , 'O'                      , '1'                      ,SUBSTR(:V_YD_SCH_CD,1,1) , SUBSTR(:V_YD_SCH_CD,2,1),
							        NULL                   , :V_YD_TO_LOC_GUIDE       , :V_YD_EQP_ID)
							*/        
							commDao.update(jrParamTc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insY5YDL011WbTCarIns", logId, mthdNm, "작업예약 등록");
	
							//작업예약재료 등록
							/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insY5YDL011WbMtlTCarIns 
							INSERT INTO TB_YD_WRKBOOKMTL WM
							       (WM.YD_WBOOK_ID          , WM.STL_NO         , WM.REGISTER       , WM.REG_DDTT    ,
							        WM.MODIFIER             , WM.MOD_DDTT       , WM.DEL_YN         , WM.YD_STK_COL_GP,
							        WM.YD_STK_BED_NO        , WM.YD_STK_LYR_NO  , WM.YD_UP_COLL_SEQ)
							VALUES (:V_YD_CARUD_WRK_BOOK_ID , :V_STL_NO         , :V_MODIFIER       , SYSDATE        ,
							        :V_MODIFIER             , SYSDATE           , 'N'               , :V_YD_STK_COL_GP,
							        :V_YD_STK_BED_NO        , :V_YD_STK_LYR_NO  , :V_YD_UP_COLL_SEQ)      
							*/        
							jrParamTc.setField("YD_STK_COL_GP" 	, ydEqpId); 
							jrParamTc.setField("YD_STK_BED_NO" 	, "01");    // 특별히 의미 없음
							jrParamTc.setField("YD_STK_LYR_NO"  , "001");   // 특별히 의미 없음
							jrParamTc.setField("YD_UP_COLL_SEQ" , "1"); 
							commDao.update(jrParamTc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insY5YDL011WbMtlTCarIns", logId, mthdNm, "작업예약재료 등록");							        
						}
						
						/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL011WbTCar
						UPDATE TB_YD_TCARSCH
						   SET MODIFIER             = :V_MODIFIER
						     , MOD_DDTT             = SYSDATE
						     , YD_CARUD_WRK_BOOK_ID = :V_YD_CARUD_WRK_BOOK_ID
						 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
						   
						 */	 
						jrParam.setField("YD_CARUD_WRK_BOOK_ID", ydNewWbookId); //신야드대차스케쥴ID
						jrParam.setField("YD_TCAR_SCH_ID", ydTcarSchId);
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL011WbTCar", logId, mthdNm, "대차스케줄상태 수정");
					
					}						
				}
			} 
			
			/**********************************************************
			* 2. 공차 도착 ydCarldSchReqGp
			**********************************************************/
			if ("E".equals(ydMoveGp) && "U".equals(ydEqpWrkStat)) { //공대차 도착
				sMsg = "해당 설비ID ["+ydEqpId+"] " + "공차도착" ;
				commUtils.printLog(logId, sMsg, "SL");
				
				ydCarldStopLoc = "J" + ydBayGp + ydEqpId.substring(2, 6);  //상차위치
				
	    		String sAPP027_YN = coilDao.ApplyYn(logId, mthdNm, "APP027", "J", "*");
				commUtils.printLog(logId,  "==========[[[ APP027 입고대차 도착시 스케쥴초기화 : "+ sAPP027_YN +" ]]]============", "SL");

				if( "Y".equals(sAPP027_YN) ) {
					/************************************************
					 * 입고대차 도착시 동간입고 스케쥴 일반야드 TO위치 초기화
					 ************************************************/
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getRcptCrnSchInfo
					SELECT B.STL_NO
					     , A.*
					  FROM TB_YD_CRNSCH    A
					     , TB_YD_CRNWRKMTL B
					     , TB_YD_EQP       C
					 WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
					   AND A.YD_SCH_CD     = C.YD_GP || C.YD_CURR_BAY_GP || SUBSTR(C.YD_EQP_ID, 3, 4) ||'MM'
					   AND C.YD_EQP_ID     = :V_YD_EQP_ID
					   AND A.DEL_YN        = 'N'
					   AND B.DEL_YN        = 'N'
					   AND C.DEL_YN        = 'N'
					   AND A.YD_WRK_PROG_STAT = 'W'
					   AND C.RCPT_TCAR_USE_YN = 'Y'
					   AND C.RCPT_TCAR_BAY    = C.YD_CURR_BAY_GP
					   AND SUBSTR(A.YD_DN_WO_LOC, 0, 6) <> C.YD_GP || C.YD_CURR_BAY_GP || SUBSTR(C.YD_EQP_ID, 3, 4)
					 ORDER BY A.YD_CRN_SCH_ID
					*/
					JDTORecordSet jsRcptCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getRcptCrnSchInfo", logId, mthdNm, "동간입고 TO위치야드 조회");
					
					sMsg = "입고대차 도착시 동간입고 스케쥴 일반야드 TO위치 초기화";
					commUtils.printLog(logId, sMsg, "SL");

					for(int i=0; i<jsRcptCrnSch.size(); i++) {
						
						String ydCrnSchId	= commUtils.trim(jsRcptCrnSch.getRecord(i).getFieldString("YD_CRN_SCH_ID"));
						String sStlNo		= commUtils.trim(jsRcptCrnSch.getRecord(i).getFieldString("STL_NO"));
						String ydDnWoLoc	= commUtils.trim(jsRcptCrnSch.getRecord(i).getFieldString("YD_DN_WO_LOC"));
						String ydDnWoLayer	= commUtils.trim(jsRcptCrnSch.getRecord(i).getFieldString("YD_DN_WO_LAYER"));
						

						sMsg = (i+1)+".YD_CRN_SCH_ID["+ ydCrnSchId +"] YD_DN_WO_LOC["+ ydDnWoLoc +"-"+ ydDnWoLayer +"] STL_NO["+ sStlNo +"]";
						commUtils.printLog(logId, sMsg, "SL");
						
						JDTORecord jdRcptCrnSch = commUtils.getParam(logId, mthdNm, sModifier);
						
						/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLoc
						UPDATE TB_YD_CRNSCH
						   SET MODIFIER = :V_MODIFIER
						      ,MOD_DDTT = SYSDATE
						      ,YD_DN_WO_LOC             = NVL(:V_YD_DN_WO_LOC           ,YD_DN_WO_LOC)
						      ,YD_DN_WO_LAYER           = NVL(:V_YD_DN_WO_LAYER         ,YD_DN_WO_LAYER)
						      ...
						 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						*/
						jdRcptCrnSch.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
						jdRcptCrnSch.setField("YD_DN_WO_LOC"	, ydCarldStopLoc +"00");
						jdRcptCrnSch.setField("YD_DN_WO_LAYER"	, "001");
						commDao.update(jdRcptCrnSch, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLoc", logId, mthdNm, "동간입고 권하 크레인스케쥴 수정");
						
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
						jdRcptCrnSch.setField("YD_STK_COL_GP"		, ydDnWoLoc.substring(0, 6));
					    jdRcptCrnSch.setField("YD_STK_BED_NO"		, ydDnWoLoc.substring(6, 8));
					    jdRcptCrnSch.setField("YD_STK_LYR_NO"		, ydDnWoLayer);
					    jdRcptCrnSch.setField("STL_NO"				, "");
					    jdRcptCrnSch.setField("YD_STK_LYR_MTL_STAT"	, "E");
						commDao.update(jdRcptCrnSch, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyr", logId, mthdNm, "동간입고 권하위치 TB_YD_STKLYR 수정");
					}
				}
				
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_TCAR_SCH_ID"      , ydTcarSchId);
				jrParam.setField("YD_CARLD_STOP_LOC"   , ydCarldStopLoc);  //상차위치
				
				sMsg = "해당 설비ID ["+ydEqpId+"] " + "ydCarldWrkBookId:" +ydCarldWrkBookId ;
				commUtils.printLog(logId, sMsg, "SL");
// sjh				
//				if ("".equals(ydCarldWrkBookId)) {
//					//작업예약이 없는 이유는 작업자가 지정한 동으로 이동했거나 홈동으로 이동한 경우이기때문에
//					//상차도착으로 처리하지 않는다.
//					jrParam.setField("YD_CAR_PROG_STAT"   , "0");
//					
//				} else {
					
					//도착동 우선순위 빠른 작업예약 조회
				/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdWrkbookYdWrkPlanTcar 
				WITH TEMP_TBL AS
				(
				-- 대상재중 첫번재 작업예약
				SELECT * 
				  FROM (
				        SELECT A.YD_WBOOK_ID
				             , A.YD_AIM_BAY_GP
				          FROM TB_YD_WRKBOOK A
				             , TB_YD_EQP  D   
				         WHERE A.YD_WRK_PLAN_TCAR = D.YD_EQP_ID
				           AND A.YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
				           AND ( A.YD_AIM_BAY_GP = NVL(D.YD_TCAR_WRK_ABLE_BAY1,'*') 
				                  OR A.YD_AIM_BAY_GP = NVL(D.YD_TCAR_WRK_ABLE_BAY2,'*')
				                  OR A.YD_AIM_BAY_GP = NVL(D.YD_TCAR_WRK_ABLE_BAY3,'*')
				                  OR A.YD_AIM_BAY_GP = NVL(D.YD_TCAR_WRK_ABLE_BAY4,'*')
				                  OR A.YD_AIM_BAY_GP = NVL(D.YD_TCAR_WRK_ABLE_BAY5,'*')
				                  OR A.YD_AIM_BAY_GP = NVL(D.YD_TCAR_WRK_ABLE_BAY6,'*')
				                  OR A.YD_AIM_BAY_GP = NVL(D.YD_TCAR_WRK_ABLE_BAY7,'*')
				                  OR A.YD_AIM_BAY_GP = NVL(D.YD_TCAR_WRK_ABLE_BAY8,'*')
				               )
				           AND A.YD_WBOOK_ID NOT IN (
				                                SELECT A.YD_WBOOK_ID
				                                  FROM (
				                                       SELECT YD_CARLD_WRK_BOOK_ID AS YD_WBOOK_ID
				                                         FROM TB_YD_TCARSCH
				                                        WHERE YD_CARLD_WRK_BOOK_ID IS NOT NULL
				                                          AND DEL_YN = 'N'
				                                       UNION ALL
				                                       SELECT YD_CARUD_WRK_BOOK_ID AS YD_WBOOK_ID
				                                         FROM TB_YD_TCARSCH
				                                        WHERE YD_CARUD_WRK_BOOK_ID IS NOT NULL
				                                          AND DEL_YN = 'N'
				                                       UNION ALL
				                                       SELECT YD_WBOOK_ID
				                                         FROM TB_YD_CRNSCH
				                                        WHERE YD_GP IN ('J')
				                                          AND DEL_YN = 'N'
				                                       ) A
				                                     , USRYDA.TB_YD_WRKBOOKMTL B
				                                     , USRYDA.TB_YD_CRNWRKMTL C
				                                 WHERE A.YD_WBOOK_ID=B.YD_WBOOK_ID
				                                   AND B.STL_NO= C.STL_NO(+)
				                                   AND B.DEL_YN='N'
				                                   AND C.DEL_YN='N'
				                                 GROUP BY A.YD_WBOOK_ID
				                )
				           AND A.YD_SCH_CD LIKE 'J' || nvl(:V_YD_BAY_GP,'_') || 'TC%'
				           AND A.DEL_YN = 'N'
				           AND SUBSTR(A.YD_SCH_CD,7,2) NOT IN ('LM','LH')
				         ORDER BY A.YD_SCH_PRIOR ASC, A.YD_WBOOK_ID ASC 
				      )
				      WHERE ROWNUM = 1   
				)
				SELECT * FROM 
				(
				SELECT * 
				  FROM (
				        SELECT A.* 
				             , SUM(A.COIL_WT)   OVER(ORDER BY A.YD_SCH_PRIOR ASC, A.YD_WBOOK_ID ASC) LOW_SUM
				             , COUNT(A.STL_NO)  OVER(ORDER BY A.YD_SCH_PRIOR ASC, A.YD_WBOOK_ID ASC) LOW_CNT
				          FROM (
				                SELECT A.YD_WBOOK_ID  
				                     , A.YD_GP        
				                     , A.YD_BAY_GP    
				                     , A.YD_SCH_CD    
				                     , A.YD_SCH_PRIOR  
				                     , A.YD_SCH_PROG_STAT
				                     , A.YD_SCH_ST_GP  
				                     , A.YD_SCH_REQ_GP 
				                     , A.YD_AIM_YD_GP 
				                     , A.YD_AIM_BAY_GP
				                     , A.YD_CTS_RELAY_YN  
				                     , A.YD_CTS_RELAY_BAY_GP
				                     , A.YD_TO_LOC_DCSN_MTD
				                     , A.YD_TO_LOC_GUIDE 
				                     , A.YD_WRK_PLAN_TCAR
				                     , A.YD_CAR_USE_GP
				                     , A.TRN_EQP_CD
				                     , A.CAR_NO 
				                     , A.CARD_NO
				                     , B.STL_NO
				                     , C.COIL_WT
				                     , D.STK_BED_MAX_QNTY
				                     , D.STK_BED_MAX_WT
				                  FROM TB_YD_WRKBOOK    A
				                     , TB_YD_WRKBOOKMTL B
				                     , TB_PT_COILCOMM   C
				                     , TB_YD_EQP        D
				                 WHERE A.YD_WBOOK_ID  = B.YD_WBOOK_ID 
				                   AND B.STL_NO = C.COIL_NO
				                   AND A.YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
				                   AND A.YD_WRK_PLAN_TCAR = D.YD_EQP_ID
				                   AND A.YD_WBOOK_ID NOT IN (
				                                        SELECT A.YD_WBOOK_ID
				                                          FROM (
				                                               SELECT YD_CARLD_WRK_BOOK_ID AS YD_WBOOK_ID
				                                                 FROM TB_YD_TCARSCH
				                                                WHERE YD_CARLD_WRK_BOOK_ID IS NOT NULL
				                                                  AND DEL_YN = 'N'
				                                               UNION ALL
				                                               SELECT YD_CARUD_WRK_BOOK_ID AS YD_WBOOK_ID
				                                                 FROM TB_YD_TCARSCH
				                                                WHERE YD_CARUD_WRK_BOOK_ID IS NOT NULL
				                                                  AND DEL_YN = 'N'
				                                               UNION ALL
				                                               SELECT YD_WBOOK_ID
				                                                 FROM TB_YD_CRNSCH
				                                                 WHERE YD_GP IN ('J')
				                                                   AND DEL_YN = 'N'
				                                               ) A
				                                             , USRYDA.TB_YD_WRKBOOKMTL B
				                                             , USRYDA.TB_YD_CRNWRKMTL  C
				                                         WHERE A.YD_WBOOK_ID=B.YD_WBOOK_ID
				                                           AND B.STL_NO= C.STL_NO(+)
				                                           AND B.DEL_YN='N'
				                                           AND C.DEL_YN='N'
				                                         GROUP BY A.YD_WBOOK_ID
				                        )
				                   AND A.YD_SCH_CD LIKE 'J' || nvl(:V_YD_BAY_GP,'_') || 'TC%'
				                   AND A.DEL_YN = 'N'
				                   AND B.DEL_YN = 'N'
				                   AND SUBSTR(A.YD_SCH_CD,7,2) NOT IN ('LM','LH')
				                ) A
				              , TEMP_TBL B
				          WHERE 1 = 1 
				                -- 첫선택된 대상재와 동일 목적동
				            AND A.YD_AIM_BAY_GP = B.YD_AIM_BAY_GP                  
				          ORDER BY A.YD_SCH_PRIOR ASC, A.YD_WBOOK_ID ASC  
				      )  A
				   WHERE 1 = 1
				     AND LOW_SUM           < STK_BED_MAX_WT -- 중량
				     AND STK_BED_MAX_QNTY >= LOW_CNT -- 매수
				     AND NOT EXISTS(SELECT 1 
				                      FROM USRYDA.TB_YD_CRNSCH C 
				                     WHERE C.DEL_YN='N' 
				                       AND C.YD_GP     = A.YD_GP
				                       AND C.YD_BAY_GP = A.YD_BAY_GP
				                       AND C.YD_DN_WO_LOC LIKE 'J'||'_TC%'
				                       AND SUBSTR(C.YD_SCH_CD,2,5) = SUBSTR(A.YD_WRK_PLAN_TCAR,2,5)
				                    )   
				     ORDER BY YD_WBOOK_ID   
				) WHERE ROWNUM = 1     
				   */  

					jrParam.setField("YD_WRK_PLAN_TCAR", ydEqpId); //작업계획 대차
					jrParam.setField("YD_BAY_GP"       , ydBayGp);
					JDTORecordSet jsTcarWbook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdWrkbookYdWrkPlanTcar", logId, mthdNm, "작업예약조회");
					
					if (jsTcarWbook.size() > 0) {
						
						for (int i = 1; i <= jsTcarWbook.size(); i++) {
							jsTcarWbook.absolute(i);
							
							// 작업예약을  대차스케쥴에 Match
							if (i == 1) {
								ydCarldWrkBookId = jsTcarWbook.getRecord().getFieldString("YD_WBOOK_ID"); //야드작업예약ID
								ydCarudStopLoc   = "J" + jsTcarWbook.getRecord().getFieldString("YD_AIM_BAY_GP") + ydEqpId.substring(2, 6);
							}
							jrYdMsg.setField("YD_WBOOK_ID"+i     , jsTcarWbook.getRecord().getFieldString("YD_WBOOK_ID"    )); //야드작업예약ID
							jrYdMsg.setField("SCH_CNT" , ""+i); //작업예약 개수
								
						}

						//크레인 스케쥴 기동 여부
						sCrnSchSendYn = "Y";
						
						//해당재료로 상차도착 처리
						jrParam.setField("YD_CAR_PROG_STAT"    , "2");
						jrParam.setField("YD_CARLD_WRK_BOOK_ID", ydCarldWrkBookId);  //상차작업예약
						jrParam.setField("YD_CARUD_STOP_LOC"   , ydCarudStopLoc);
						
					} else {
						jrParam.setField("YD_CAR_PROG_STAT"    , "0");
						jrParam.setField("YD_CARLD_WRK_BOOK_ID", "");
					}
//				}

				/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdTcarschStat 
    			UPDATE TB_YD_TCARSCH
    			   SET MODIFIER = :V_MODIFIER
    			     , MOD_DDTT = SYSDATE
    			     , YD_CARLD_SCH_REQ_GP  = NVL(:V_YD_CARLD_SCH_REQ_GP  , YD_CARLD_SCH_REQ_GP)  --상차 Sch 구분
    			     , YD_CARUD_SCH_REQ_GP  = NVL(:V_YD_CARUD_SCH_REQ_GP  , YD_CARUD_SCH_REQ_GP)  
    			     , YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_CARLD_WRK_BOOK_ID , YD_CARLD_WRK_BOOK_ID) --상차 작업예약
    			     , YD_CARLD_STOP_LOC    = NVL(:V_YD_CARLD_STOP_LOC    , YD_CARLD_STOP_LOC  -- 상차정지위치
    			     , YD_CARUD_STOP_LOC    = NVL(:V_YD_CARUD_STOP_LOC    , YD_CARUD_STOP_LOC) -- 하차정지위치
    			     , YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT
    			 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
    			*/
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdTcarschStat", logId, mthdNm, "대차진행상태 수정(도착1)");

				//상차정지위치 베드 활성화
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_STK_COL_GP"      , ydCarldStopLoc); 
				jrParam.setField("YD_STK_BED_ACT_STAT", "L"); //활성화
				/*
				UPDATE TB_YD_STKBED
				   SET YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
				     , MODIFIER            = :V_MODIFIER
				     , MOD_DDTT            = SYSDATE
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND DEL_YN        = 'N'   
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkBedActStat", logId, mthdNm, "적치Bed활성상태 수정");
				
				jrParam.setField("STL_NO"             , "" );
				jrParam.setField("YD_STK_LYR_ACT_STAT", "E");
				jrParam.setField("YD_STK_LYR_MTL_STAT", "E");
				/*
				UPDATE TB_YD_STKLYR            
				   SET YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
				     , STL_NO              = :V_STL_NO
				     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
				     , MODIFIER            = :V_MODIFIER
				     , MOD_DDTT            = SYSDATE
				 WHERE YD_STK_COL_GP  = :V_YD_STK_COL_GP
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStklyrYdStkColGp", logId, mthdNm, "상차정지위치 CLEAR");
				
				
				sMsg = "해당 설비ID ["+ydEqpId+"] " + "ydCarldSchReqGp:" +ydCarldSchReqGp ;
				commUtils.printLog(logId, sMsg, "SL");
								
			} 			
			
			/**********************************************************
		 	 * 4. 영차 도착 
			 **********************************************************/
		
			if ("E".equals(ydMoveGp) && "L".equals(ydEqpWrkStat)) { //영대차 도착
//				nRet = 4;
				sMsg = "해당 설비ID ["+ydEqpId+"] " + "영차도착" ;
				commUtils.printLog(logId, sMsg, "SL");

				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_TCAR_SCH_ID"      , ydTcarSchId);
				jrParam.setField("YD_CAR_PROG_STAT"    , "B");
				
				/*********************************************
				 *  크레인 하차 예약 등록 여부 CHECK 하여 없으면 생성
				 *********************************************/
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getY5YDL011WbTCar
				SELECT YD_TCAR_SCH_ID 
				     , STL_NO
				     , YD_SCH_CD
				     , (SELECT YD_WRK_CRN_PRIOR FROM TB_YD_SCHRULE WHERE YD_SCH_CD = A.YD_SCH_CD) AS YD_WRK_CRN_PRIOR
				     , WB_CNT
				  FROM
				        (  
				        SELECT A.YD_TCAR_SCH_ID
				             , B.STL_NO
				             -- 대차하차나 동간입고를 대표로 대차하차
				             , A.YD_CARUD_STOP_LOC||CASE WHEN C.YD_LOC_GP = 'H' THEN 'LH' ELSE 'LM' END AS YD_SCH_CD
				             --하차 스케쥴 유무검색
				             , (SELECT COUNT(*) 
				                  FROM TB_YD_WRKBOOK WB
				                     , TB_YD_WRKBOOKMTL WM
				                 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID  
				                   AND WB.DEL_YN = 'N'
				                   AND WM.DEL_YN = 'N'
				                   AND SUBSTR(WB.YD_SCH_CD,1,6) = A.YD_CARUD_STOP_LOC
				                   AND 'Y' = CASE WHEN C.YD_LOC_GP = 'H' AND SUBSTR(WB.YD_SCH_CD,7,2) = 'LH'         THEN 'Y'
				                                  WHEN C.YD_LOC_GP = 'J' AND SUBSTR(WB.YD_SCH_CD,7,2) IN ('LM','MM') THEN 'Y'
				                                  ELSE 'N' END
				                   AND WM.STL_NO    = B.STL_NO ) AS WB_CNT  
				          FROM TB_YD_TCARSCH     A
				             , TB_YD_TCARFTMVMTL B
				             , TB_YD_EQP         C
				         WHERE A.YD_TCAR_SCH_ID = B.YD_TCAR_SCH_ID
				           AND A.DEL_YN = 'N'
				           AND B.DEL_YN = 'N'
				           AND A.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
				           AND A.YD_EQP_ID      = C.YD_EQP_ID
				        ) A 
				 WHERE WB_CNT = 0 
				*/	   
				JDTORecordSet jsDnTc = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getY5YDL011WbTCar", logId, mthdNm, "하차스케쥴 편성!!!");
				String ydNewWbookId = ""; 
				if (jsDnTc.size() > 0 ) {
					
					JDTORecord jrParamTc = JDTORecordFactory.getInstance().create();
					JDTORecord jrDnTc 	 = JDTORecordFactory.getInstance().create();
					for(int nIdx=0; nIdx < jsDnTc.size(); nIdx++) {
						
						jrParamTc = JDTORecordFactory.getInstance().create();
						jrDnTc    = JDTORecordFactory.getInstance().create();
						//대차스케줄 처리 (영대차출발지시)
						//야드하차작업예약ID 생성
						String ydCarudWrkBookId = coilDao.getSeqId(logId, mthdNm, "WrkBook");
						
						//작업예약 등록
						jrDnTc = jsDnTc.getRecord(nIdx);
						
						if (nIdx == 0 ) {
							ydNewWbookId = ydCarudWrkBookId;
						}
						jrParamTc = commUtils.getParam(logId, mthdNm, sModifier);
						jrParamTc.setField("YD_CARUD_WRK_BOOK_ID", ydCarudWrkBookId); //야드하차작업예약ID
						jrParamTc.setField("YD_WBOOK_ID"         , ydCarudWrkBookId ); 
						jrParamTc.setField("YD_SCH_CD"           , commUtils.trim(jrDnTc.getFieldString("YD_SCH_CD"))); //스케쥴 코드
						jrParamTc.setField("YD_WRK_CRN_PRIOR"    , commUtils.trim(jrDnTc.getFieldString("YD_WRK_CRN_PRIOR"))); //대차설비id
						jrParamTc.setField("YD_EQP_ID"           , ydEqpId       	); //대차설비id
						jrParamTc.setField("STL_NO" 			 , commUtils.trim(jrDnTc.getFieldString("STL_NO"))); 
											        
						/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insY5YDL011WbTCarIns 
						INSERT INTO TB_YD_WRKBOOK WB
						       (WB.YD_WBOOK_ID         , WB.REGISTER              , WB.REG_DDTT              ,
						        WB.DEL_YN              , WB.YD_GP                 , WB.YD_BAY_GP             , WB.YD_SCH_CD   , WB.YD_SCH_PRIOR ,
						        WB.YD_SCH_PROG_STAT    , WB.YD_SCH_ST_GP          , WB.YD_SCH_REQ_GP         , WB.YD_AIM_YD_GP, WB.YD_AIM_BAY_GP,
						        WB.YD_TO_LOC_DCSN_MTD  , WB.YD_TO_LOC_GUIDE       , WB.YD_WRK_PLAN_TCAR)
						VALUES (:V_YD_CARUD_WRK_BOOK_ID, :V_MODIFIER              ,  SYSDATE                 ,
						        'N'                    , SUBSTR(:V_YD_SCH_CD,1,1) , SUBSTR(:V_YD_SCH_CD,2,1) , :V_YD_SCH_CD   , :V_YD_WRK_CRN_PRIOR ,
						        'W'                    , 'O'                      , '1'                      ,SUBSTR(:V_YD_SCH_CD,1,1) , SUBSTR(:V_YD_SCH_CD,2,1),
						        NULL                   , NULL                     , :V_YD_EQP_ID)
						*/        
						commDao.update(jrParamTc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insY5YDL011WbTCarIns", logId, mthdNm, "작업예약 등록");

						//작업예약재료 등록
						/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insY5YDL011WbMtlTCarIns 
						INSERT INTO TB_YD_WRKBOOKMTL WM
						       (WM.YD_WBOOK_ID          , WM.STL_NO         , WM.REGISTER       , WM.REG_DDTT    ,
						        WM.MODIFIER             , WM.MOD_DDTT       , WM.DEL_YN         , WM.YD_STK_COL_GP,
						        WM.YD_STK_BED_NO        , WM.YD_STK_LYR_NO  , WM.YD_UP_COLL_SEQ)
						VALUES (:V_YD_CARUD_WRK_BOOK_ID , :V_STL_NO         , :V_MODIFIER       , SYSDATE        ,
						        :V_MODIFIER             , SYSDATE           , 'N'               , :V_YD_STK_COL_GP,
						        :V_YD_STK_BED_NO        , :V_YD_STK_LYR_NO  , :V_YD_UP_COLL_SEQ)      
						*/        
						jrParamTc.setField("YD_STK_COL_GP" 	, ydEqpId); 
						jrParamTc.setField("YD_STK_BED_NO" 	, "01");    // 특별히 의미 없음
						jrParamTc.setField("YD_STK_LYR_NO"  , "001");   // 특별히 의미 없음
						jrParamTc.setField("YD_UP_COLL_SEQ" , "1"); 
						commDao.update(jrParamTc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insY5YDL011WbMtlTCarIns", logId, mthdNm, "작업예약재료 등록");							        
					}
					
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL011WbTCar
					UPDATE TB_YD_TCARSCH
					   SET MODIFIER             = :V_MODIFIER
					     , MOD_DDTT             = SYSDATE
					     , YD_CARUD_WRK_BOOK_ID = :V_YD_CARUD_WRK_BOOK_ID
					 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
					   
					 */	 
					jrParam.setField("YD_CARUD_WRK_BOOK_ID", ydNewWbookId); //신야드대차스케쥴ID
					jrParam.setField("YD_TCAR_SCH_ID", ydTcarSchId);
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updY5YDL011WbTCar", logId, mthdNm, "대차스케줄상태 수정");
				
				}					
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdTcarschStat 
    			UPDATE TB_YD_TCARSCH
    			   SET MODIFIER = :V_MODIFIER
    			     , MOD_DDTT = SYSDATE
    			     , YD_CARLD_SCH_REQ_GP  = NVL(:V_YD_CARLD_SCH_REQ_GP  , YD_CARLD_SCH_REQ_GP)  --상차 Sch 구분
    			     , YD_CARUD_SCH_REQ_GP  = NVL(:V_YD_CARUD_SCH_REQ_GP  , YD_CARUD_SCH_REQ_GP)  
    			     , YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_CARLD_WRK_BOOK_ID , YD_CARLD_WRK_BOOK_ID) --상차 작업예약
    			     , YD_CARLD_STOP_LOC    = NVL(:V_YD_CARLD_STOP_LOC    , YD_CARLD_STOP_LOC  -- 상차정지위치
    			     , YD_CARUD_STOP_LOC    = NVL(:V_YD_CARUD_STOP_LOC    , YD_CARUD_STOP_LOC) -- 하차정지위치
    			     , YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT
    			 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
    			*/
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdTcarschStat", logId, mthdNm, "대차진행상태 수정(영차)");

				sMsg = "해당 설비ID ["+ydEqpId+"] " + "ydCarldSchReqGp:" +ydCarldSchReqGp ;
				commUtils.printLog(logId, sMsg, "SL");
				
				sMsg = "해당 설비ID ["+ydEqpId+"] " + "ydCarudSchReqGp:" +ydCarudSchReqGp + " ydCarProgStat: " + ydCarProgStat ;
				commUtils.printLog(logId, sMsg, "SL");
				
				//스케줄 요청 구분 영대차도착
				if ("3".equals(ydCarudSchReqGp)) {
					
					if (!"A".equals(ydCarProgStat)) { // 영차 출발
						
						//출발지(상차지) 초기화
						jrParam.setField("YD_STK_COL_GP"      , ydCarldStopLoc);
						jrParam.setField("YD_STK_BED_ACT_STAT", "C"); //
						/*
						UPDATE TB_YD_STKBED
						   SET YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
						     , MODIFIER            = :V_MODIFIER
						     , MOD_DDTT            = SYSDATE
						 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
						   AND DEL_YN        = 'N'   
						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkBedActStat", logId, mthdNm, "적치Bed활성상태 수정");
						
						jrParam.setField("STL_NO"             , "" );
						jrParam.setField("YD_STK_LYR_ACT_STAT", "C");
						jrParam.setField("YD_STK_LYR_MTL_STAT", "E");
						/*
						UPDATE TB_YD_STKLYR            
						   SET YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
						     , STL_NO              = :V_STL_NO
						     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
						     , MODIFIER            = :V_MODIFIER
						     , MOD_DDTT            = SYSDATE
						 WHERE YD_STK_COL_GP  = :V_YD_STK_COL_GP
						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStklyrYdStkColGp", logId, mthdNm, "상차정지위치 CLEAR");
					}
					
					jrParam.setField("YD_STK_COL_GP"      , ydCarudStopLoc);
					jrParam.setField("YD_STK_BED_ACT_STAT", "L"); //
					/*
					UPDATE TB_YD_STKBED
					   SET YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
					     , MODIFIER            = :V_MODIFIER
					     , MOD_DDTT            = SYSDATE
					 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
					   AND DEL_YN        = 'N'   
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkBedActStat", logId, mthdNm, "적치Bed활성상태 수정");
					
					//야드적치단활성상태
					jrParam.setField("STL_NO"             , "" );
					jrParam.setField("YD_STK_LYR_ACT_STAT", "C");
					jrParam.setField("YD_STK_LYR_MTL_STAT", "E");
					/*
					UPDATE TB_YD_STKLYR            
					   SET YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
					     , STL_NO              = :V_STL_NO
					     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
					     , MODIFIER            = :V_MODIFIER
					     , MOD_DDTT            = SYSDATE
					 WHERE YD_STK_COL_GP  = :V_YD_STK_COL_GP
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStklyrYdStkColGp", logId, mthdNm, "적치단활성상태 수정");
					
					/******************************
					 * 대차 도착 수신시 도착동 체크
					 ******************************/
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getTCarArrivedSchId
					SELECT *
					  FROM (SELECT A.YD_TCAR_SCH_ID
					             , A.STL_NO
					             , A.YD_STK_BED_NO
					             , A.YD_STK_LYR_NO
					             , A.STL_PROG_CD
					             , A.YD_MTL_ITEM
					             , A.YD_ROUTE_GP
					             , B.YD_WBOOK_ID
					             , D.YD_CARUD_STOP_LOC
					             , C.YD_SCH_CD
					             , C.YD_WRK_PLAN_CRN
					          FROM TB_YD_TCARFTMVMTL A
					             , TB_YD_WRKBOOKMTL  B
					             , TB_YD_WRKBOOK     C
					             , TB_YD_TCARSCH     D
					         WHERE A.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
					           AND A.STL_NO         = B.STL_NO
					           AND B.YD_WBOOK_ID    = C.YD_WBOOK_ID
					           AND A.YD_TCAR_SCH_ID = D.YD_TCAR_SCH_ID
					           AND C.YD_BAY_GP      = SUBSTR(D.YD_CARUD_STOP_LOC,2,1)
					           AND C.YD_BAY_GP      = :V_YD_BAY_GP
					           AND A.DEL_YN         = 'N'
					           AND B.DEL_YN         = 'N'
					       )
					 WHERE STL_NO NOT IN (SELECT STL_NO
					                        FROM TB_YD_CRNWRKMTL
					                       WHERE DEL_YN = 'N')
					 ORDER BY YD_STK_BED_NO, YD_STK_LYR_NO
					*/
					jrParam.setField("YD_BAY_GP", ydBayGp );
					JDTORecordSet jsTcarMtl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getTCarArrivedSchId", logId, mthdNm, "하차작업예약 조회");
					
					for (int i = 1; i <= jsTcarMtl.size(); i++) {
						jsTcarMtl.absolute(i);
						
						jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			    		jrParam.setField("YD_STK_COL_GP",       ydCarudStopLoc);
			    		jrParam.setField("YD_STK_BED_NO",       jsTcarMtl.getRecord().getFieldString("YD_STK_BED_NO"));
			    		jrParam.setField("YD_STK_LYR_NO",       "001");
			    		jrParam.setField("STL_NO",              jsTcarMtl.getRecord().getFieldString("STL_NO"));
			    		jrParam.setField("YD_STK_LYR_MTL_STAT", "C"); //적치중
			    		jrParam.setField("YD_STK_LYR_ACT_STAT", "E"); //적치가능
			    		/*
						UPDATE TB_YD_STKLYR            
						   SET YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
						     , STL_NO              = :V_STL_NO
						     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
						     , MODIFIER            = :V_MODIFIER
						     , MOD_DDTT            = SYSDATE
						 WHERE YD_STK_COL_GP  = :V_YD_STK_COL_GP  
						   AND YD_STK_BED_NO  = :V_YD_STK_BED_NO
						   AND YD_STK_LYR_NO  = :V_YD_STK_LYR_NO 
			    		 */
			    		commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStklyrActStat", logId, mthdNm, "적치단 수정");
			    		
			    		//하차크레인 스케줄 호출
						jrYdMsg.setField("YD_WBOOK_ID"+i , jsTcarMtl.getRecord().getFieldString("YD_WBOOK_ID"));
						jrYdMsg.setField("SCH_CNT"       , ""+i); //작업예약 개수
					}
					if (jsTcarMtl.size() > 0 ) {
						//크레인 스케쥴 기동 여부
						sCrnSchSendYn = "Y";
					}
				}
			} 
			
			/**********************************************************
			 * 5. 대차 이동실적 
			 **********************************************************/
			if ("M".equals(ydMoveGp)) {//대차이동실적 처리 완료 YD_MOVE_GP : M
				jrRtn.setField("RTN_CD"	, "1");
				jrRtn.setField("RTN_MSG", "대차이동실적 전문수신완료");
				if (resYn) {
					resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
					resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
					jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
				}
				return jrRtn;
			}
			
			/**********************************************************
			* 6. 저장위치제원 L2전문 송신
			**********************************************************/
			jrParam.setField("YD_INFO_SYNC_CD" , "3"       ); //야드정보동기화코드(Bed)  1:동,2:SPAN,3:열,4:BED
			jrParam.setField("YD_STK_COL_GP"   , ydStkColGp); //야드적치열구분(현재동)
			jrParam.setField("YD_STK_BED_NO"   , "01"      ); //야드적치Bed

			//전송Data 조회
			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L001", jrParam));
			
			/**********************************************************
			* 99. 대차이동실적 응답 전송
			**********************************************************/
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
			}
	
			sMsg = "스케쥴 기동 여부 " +sCrnSchSendYn  ;
			commUtils.printLog(logId, sMsg, "SL");
			
			//스케쥴 기동 
			if ("Y".equals(sCrnSchSendYn) ) {
			
				EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this);
				JDTORecord jrCrnSchMsg = (JDTORecord)ejbConn.trx("procYDYDJ552", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
				jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
			}			
			
			jrRtn.setField("RTN_CD"	, "1");
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "CCommSeEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { coilDao.getYDY5L005(resMsg)});
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}		
	
	
	/**
	 *      [A] 오퍼레이션명 : 강제권상요구(Y5YDL012)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL012(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "강제권상요구[CCoilL2RcvSeEJB.rcvY5YDL012] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		JDTORecord resMsg = commUtils.getParam(logId, mthdNm, "Y5YDL012"); //크레인작업실적응답 전문 생성용
		JDTORecord jrRtn  = JDTORecordFactory.getInstance().create(); //전문 Return
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			String sMsg			= "";

			//수신 항목 값
			String msgId		= commUtils.getMsgId(rcvMsg);								// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     ));	// 야드설비ID
			String ydUpWrLoc	= commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LOC"  ));	// 야드권상실적위치
			String ydUpWrLayer	= commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LAYER"));	// 야드권상실적단
			int    ydCrX		= Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_CRN_XAXIS"),"0")); // 야드크레인X축
			int    ydCrY		= Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_CRN_YAXIS"),"0")); // 야드크레인Y축
			int    ydCrZ		= Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_CRN_ZAXIS"),"0")); // 야드크레인Z축

			String sModifier	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"     ));	// 수정자(Backup Only)
			

			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);
			resMsg.setResultMsg(mthdNm);
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); // 야드설비ID
			resMsg.setField("YD_L2_WR_GP"     , "J"          ); // 야드L2실적구분(지시요구) U:권상실적, D:권하실적, F: 강제권하, R:고장,M:모드변경, E: 비상조업실적, J : 지시요구
			resMsg.setField("YD_L3_HD_RS_CD"  , "JR99"       ); // 야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:크레인작업지시요구 수신처리"); // 야드L3MESSAGE(Error)

			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			}

			if ("".equals(ydUpWrLoc)) {
				if (ydCrX == 0) {
					
					sMsg = "정보이상 이상 ["+ ydEqpId +"] : 위치값 없음";
					commUtils.printLog(logId, sMsg, "SL");
					
					resMsg.setField("YD_L3_MSG", sMsg);
					jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
					
					commUtils.printLog(logId, mthdNm, "S-");
					return jrRtn;
				}
			}

			if ("".equals(ydUpWrLoc) || ydUpWrLoc.length() != 8) {
				
				sMsg = "에러:정보이상권상:" + ydUpWrLoc;
				commUtils.printLog(logId, sMsg, "SL");

				resMsg.setField("YD_L3_HD_RS_CD"  , "8888"); //야드L3처리결과코드(Error)
				resMsg.setField("YD_L3_MSG", sMsg);
				jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg)); 

				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
			}

			String ydStkColGp = ydUpWrLoc.substring(0, 6); //
        	String ydStkBedGp = ydUpWrLoc.substring(6, 8);
			String ydBayGp    = ydUpWrLoc.substring(1, 2);
			String sEqpIdGp   = ydEqpId.substring(5, 6); //설비번호

			/**********************************************************
			* 조회 및 등록
			**********************************************************/
        	//조회 및 등록용
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, commUtils.trim(sModifier));
			jrParam.setField("YD_EQP_ID"    	, ydEqpId    ); //야드설비ID
			jrParam.setField("YD_STK_COL_GP"  	, ydStkColGp );
			jrParam.setField("YD_STK_BED_NO"  	, ydStkBedGp );
			jrParam.setField("YD_STK_LYR_NO"    , ydUpWrLayer);
			jrParam.setField("YD_CRN_XAXIS"    	, ""+ydCrX   );
			jrParam.setField("YD_CRN_YAXIS"    	, ""+ydCrY   );
			jrParam.setField("YD_CRN_ZAXIS"    	, ""+ydCrZ   );

			JDTORecord jrChk = coilDao.chkEqpStat(jrParam);

			String ydL3HdRsCd = commUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));// 기존 C:'8888')
			String ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));
			
			resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); // 야드L3처리결과코드

			if (!"".equals(ydL3Msg)) {
				
				sMsg = ydL3Msg +"에러:설비상태가 이적할 수 없음";
				commUtils.printLog(logId, sMsg, "SL");
				resMsg.setField("YD_L3_MSG", sMsg);
				
				jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
				
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
			}
			
			/*******************************************
			 * 1. 설비 상태 체크
			 *******************************************/
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getStatEqpCrnSch
			--설비상태조회 
			SELECT YD_EQP_STAT
			     , CASE WHEN YD_EQP_STAT = '1' 
			            THEN (SELECT YD_CRN_SCH_ID
			                    FROM (
			                            SELECT YD_CRN_SCH_ID
			                              FROM TB_YD_CRNSCH                                      
			                             WHERE YD_EQP_ID = :V_YD_EQP_ID                   
			                               AND YD_WRK_PROG_STAT IN ('1','2','3')
			                               AND DEL_YN = 'N'
			                             ORDER BY YD_WRK_PROG_STAT DESC
			                         ) 
			                    WHERE ROWNUM <= 1
			                 )
			             ELSE '' END AS YD_CRN_SCH_ID                 
			  FROM TB_YD_EQP EQ
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND DEL_YN    = 'N' 
			*/
			JDTORecordSet jsEqpSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getStatEqpCrnSch", logId, mthdNm, "설비상태 조회");

			String ydEqpStat  = commUtils.trim(jsEqpSch.getRecord(0).getFieldString("YD_EQP_STAT"));   //설비 상태
			String ydCrnSchId = commUtils.trim(jsEqpSch.getRecord(0).getFieldString("YD_CRN_SCH_ID")); //작업 id
			
			if( !"W".equals(ydEqpStat) && !"1".equals(ydEqpStat) ) {
				
				sMsg = "에러:설비상태가 이적할수 없습니다.";
				commUtils.printLog(logId, sMsg, "SL");
				resMsg.setField("YD_L3_MSG", sMsg);

				jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
				
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
			}
			
			/*******************************************
			 * 2. 권상위치 좌표 체크
			 *******************************************/
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStklyrXYZStk
			SELECT A.STL_NO 
			     , A.YD_STK_COL_GP
			     , A.YD_STK_BED_NO
			     , A.YD_STK_LYR_NO
			  FROM TB_YD_STKLYR A
			     , TB_YD_STKBED B
			 WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP 
			   AND A.YD_STK_BED_NO = B.YD_STK_BED_NO 
			   AND A.YD_STK_COL_GP = :V_YD_STK_COL_GP
			   AND A.YD_STK_BED_NO = :V_YD_STK_BED_NO 
			   AND A.YD_STK_LYR_NO = :V_YD_STK_LYR_NO
			   AND A.YD_STK_LYR_ACT_STAT = 'E'
			   AND A.YD_STK_LYR_MTL_STAT = 'C'
			   AND A.DEL_YN = 'N'
			   AND B.DEL_YN = 'N'
			   AND SUBSTR(A.YD_STK_COL_GP,1,1) IN ('H','J')
			   AND :V_YD_CRN_XAXIS  BETWEEN A.YD_STK_LYR_XAXIS  - B.YD_STK_BED_XAXIS_TOL AND A.YD_STK_LYR_XAXIS  + B.YD_STK_BED_XAXIS_TOL
			   AND :V_YD_CRN_YAXIS  BETWEEN A.YD_STK_LYR_YAXIS  - B.YD_STK_BED_YAXIS_TOL AND A.YD_STK_LYR_YAXIS  + B.YD_STK_BED_YAXIS_TOL
		    */
			JDTORecordSet jsMtl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStklyrXYZStk", logId, mthdNm, "위치정보 조회");
			String ydWbookId = "";
			String ydSchCd   = "";

    		if ( jsMtl.size() <= 0 ) {
				
				sMsg = "에러:지시정보가 예약되어 있습니다.";
				commUtils.printLog(logId, sMsg, "SL");
				resMsg.setField("YD_L3_MSG", sMsg);

				jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
				
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
				
    		} else if ( jsMtl.size() > 1) {
    			
				sMsg = "에러:1개 이상의 좌표가 존재합니다.";
				commUtils.printLog(logId, sMsg, "SL");
				resMsg.setField("YD_L3_MSG", sMsg);

				jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
				
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
    		}
    		
			/*******************************************
			 * 3. 권상지시요구 작업예약 생성
			 *******************************************/
    		ydSchCd = "J"+ ydBayGp +"YD"+ sEqpIdGp +"2MH";
    		JDTORecord jrWrkBook = commUtils.getParam(logId, mthdNm, sModifier);
    		jrWrkBook.setField("YD_SCH_CD"	, ydSchCd);// 자동이적
    		jrWrkBook.setField("STL_SH"		, "1"    );
    		jrWrkBook.setField("STL_NO1"	, commUtils.trim(jsMtl.getRecord(0).getFieldString("STL_NO"))); //재료번호

			EJBConnector ejbConn = new EJBConnector("default", "CCoilWrkBookSeEJB", this);
			JDTORecord outRecord = (JDTORecord)ejbConn.trx("insWrkBookTx", new Class[] { JDTORecord.class }, new Object[] { jrWrkBook });
			
			ydWbookId = commUtils.trim(outRecord.getFieldString("YD_WBOOK_ID"));

			if ( "".equals(ydWbookId) ) {
				
				sMsg = "에러:작업예약 생성 실패";
				commUtils.printLog(logId, sMsg, "SL");
				resMsg.setField("YD_L3_MSG", sMsg);

				jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
				
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
			}

			/*******************************************
			 * 4. 현재 권상지시 스케쥴 초기화 처리
			 *******************************************/
    		if( "1".equals(ydEqpStat) && !"".equals(ydCrnSchId) ) {
				JDTORecord jrInPara = commUtils.getParam(logId, mthdNm, commUtils.trim(sModifier));
				jrInPara.setField("YD_WRK_PROG_STAT"	, "W");
				jrInPara.setField("YD_CRN_SCH_ID"		, ydCrnSchId);

				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnsch
				UPDATE TB_YD_CRNSCH
				   SET MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				     , YD_WRK_PROG_STAT = :V_YD_WRK_PROG_STAT
				     , YD_SCH_ST_GP     = NVL(:V_YD_SCH_ST_GP,YD_SCH_ST_GP)
				     , YD_WORD_DT       = NVL(TO_DATE(:V_YD_WORD_DT,'YYYYMMDDHH24MISS'),YD_WORD_DT)
				 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				*/
				commDao.update(jrInPara, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnsch", logId, mthdNm, "기존 SCH 상태 'W'로 수정");

				/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.updStatEqp
				UPDATE TB_YD_EQP
				   SET MODIFIER    = :V_MODIFIER
				     , MOD_DDTT    = SYSDATE
				     , YD_EQP_STAT = :V_YD_EQP_STAT
				 WHERE YD_EQP_ID   = :V_YD_EQP_ID
				   AND DEL_YN      = 'N'
        		*/
				jrInPara.setField("YD_EQP_ID"	, ydEqpId); //야드설비ID
				jrInPara.setField("YD_EQP_STAT"	, "W");
				//더이상 크레인 스케줄에 작업이 없을 경우 설비Table에 설비상태를 'W' 명령선택대기(idle)로 변경한다.
        		commDao.update(jrInPara, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.updStatEqp", logId, mthdNm, "설비 상태:'W'로 수정");

        		/******************************************
        		 * 20.10.27 권하위치가 대차위치면 권하위치 초기화
        		 ******************************************/
        		/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCrnsch
        		SELECT YD_EQP_ID                        AS YD_EQP_ID
        		     , YD_CRN_SCH_ID                    AS YD_CRN_SCH_ID
        		     ......
        		  FROM TB_YD_CRNSCH
        		 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
        		*/
        		JDTORecordSet jrCrnSchChk = commDao.select(jrInPara, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCrnsch", logId, mthdNm, "현작업 권하위치 대차인지 체크");

        		if( jrCrnSchChk.size() > 0 ) {

        			// 권하위치
        			String ydDnWoLoc = commUtils.trim(jrCrnSchChk.getRecord(0).getFieldString("YD_DN_WO_LOC"));

        			commUtils.printLog(logId, "기존스케쥴 권하위치["+ ydDnWoLoc +"]", "SL");
        			
        			if( ydDnWoLoc.length() == 8 ) {
	        			if( "TC".equals(ydDnWoLoc.substring(2,4)) && !"00".equals(ydDnWoLoc.substring(6,8))) {
	
	        				/************************************
	        				 * 1.크레인스케쥴 대차권하위치 00번지
	        				 ************************************/
	        				commUtils.printLog(logId, "크레인스케쥴 대차 권하위치 초기화", "SL");
							// 이전 크레인스케쥴 권하위치 대차 00 번지로 변경
							/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLoc
							UPDATE TB_YD_CRNSCH
							   SET MODIFIER = :V_MODIFIER
							      ,MOD_DDTT = SYSDATE
							      ,YD_DN_WO_LOC             = NVL(:V_YD_DN_WO_LOC           ,YD_DN_WO_LOC)
							      ,YD_DN_WO_LAYER           = NVL(:V_YD_DN_WO_LAYER         ,YD_DN_WO_LAYER)
							      ...
							 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
							*/
	        				jrInPara.setField("YD_DN_WO_LOC"	, ydDnWoLoc.substring(0, 6) + "00");
	        				jrInPara.setField("YD_DN_WO_LAYER"	, "001");
							commDao.update(jrInPara, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLoc", logId, mthdNm,  "기존스케쥴 대차권하위치 초기화");
	
	        				/************************************
	        				 * 2.대차 권하위치 초기화
	        				 ************************************/
							commUtils.printLog(logId, "대차위치 코일정보 초기화", "SL");
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
							jrInPara.setField("YD_STK_COL_GP"		, ydDnWoLoc.substring(0, 6));
						    jrInPara.setField("YD_STK_BED_NO"		, ydDnWoLoc.substring(6, 8));
						    jrInPara.setField("YD_STK_LYR_NO"		, "001");
						    jrInPara.setField("STL_NO"				, "");
						    jrInPara.setField("YD_STK_LYR_MTL_STAT"	, "E");
							commDao.update(jrInPara, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyr", logId, mthdNm, "작업중인 대차권하위치 초기화");
	        			}
        			}
        		}
    		}

			/**********************************************************
			* 5. 크레인스케줄 전문 호출
			**********************************************************/
			JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);

			jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); // 야드작업예약ID
			jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); // 야드스케쥴코드
			jrYdMsg.setField("YD_EQP_ID"    , ydEqpId  );
			jrYdMsg.setField("YD_SCH_ST_GP" , "O"      ); // 야드스케쥴기동구분
			jrYdMsg.setField("YD_SCH_REQ_GP", "L"      ); // 야드스케쥴요청구분(인출)

			jrRtn = commUtils.addSndData(jrRtn, coilDao.getCrnSchMsg(jrYdMsg));

			commUtils.printLog(logId, mthdNm, "S-");

			jrRtn.setField("RTN_CD"	, "1");
			return jrRtn;

		} catch (Exception e) {
			try {
				//chito : 정상SET후  ERROR 발생한 경우								
				if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
					resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
					resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
				}
				
				//크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "CCommSeEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { coilDao.getYDY5L005(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 작업현황요구(Y5YDL013)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL013(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "작업현황요구[CoilL2RcvSeEJB.rcvY5YDL013] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = commUtils.getParam(logId, mthdNm, "Y5YDL013");	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId     	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     //설비ID
			String sModifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID 이상 [" + ydEqpId + "]");
			}

			/**********************************************************
			* 2. 작업현황응답(YDY5L007)
			**********************************************************/
			//크레인스케줄기동여부 'Y'이고 작업예약ID가 있으면 크레인스케줄 전송
			JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);

			sndL2Msg.setField("YD_EQP_ID"   , ydEqpId);  					//이동구분 
			//작업현황응답정보 송신
			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L007", sndL2Msg));
								
	
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업가능응답(Y5YDL015)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL015(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "크레인작업가능응답[CCoilL2RcvSeEJB.rcvY5YDL015] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = commUtils.getParam(logId, mthdNm, "Y5YDL015");	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값ydEqpId
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId     	 = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"          )); //설비ID
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"   )); //야드작업진행상태(5:권하위치변경)만 올라옴
			String ydSchCd   	 = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"          )); //야드스케쥴코드
			String ydCrnSchId  	 = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"      )); //야드크레인스케쥴ID
			String sReqYn  		 = commUtils.trim(rcvMsg.getFieldString("REQ_YN"             )); //유무응답
			String sReqMsg       = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_REQ_MSG")); //메시지
			String sErrCd        = commUtils.trim(rcvMsg.getFieldString("ERR_CD"             )); //에러코드
			String ydCrnSchIdOld = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID_OLD"  )); //이전 스케줄ID

			String sModifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); 		//수정자(Backup Only)
			
			String sMsgGp       = commUtils.trim(rcvMsg.getFieldString("MSG_GP"     )); 		//전문구분I(신규), U(수정), D(취소,삭제), R(재 전송)	

			String ydChgStlNo 		= "";
			String ydChgDnWoLoc	    = "";
			String ydChgDnWoLayer	= "";
			String ydBefDnWoLoc		= "";
			String ydBefDnWoLayer	= "";
			String ydWbookId 		= "";
			String ydWrkProgStst	= "";	
			String ydUpWrLoc	    = "";	
			String ydL2RequestStat	= "";
			String ydSchPrior       = ""; //스케줄 우선순위
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if (coilDao.chkAutoCrn(logId, mthdNm, ydEqpId)) {
				commUtils.printLog(logId, ydEqpId + "무인 크레인입니다.", "SL");
				jrRtn = this.rcvY5YDL015Auto(rcvMsg);
				return jrRtn;
			}  else {
				commUtils.printLog(logId, ydEqpId + "무인 크레인이 아닙니다", "SL");
			}
			
		
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_EQP_ID"         		, ydEqpId);  					
			jrParam.setField("YD_CRN_SCH_ID"      		, ydCrnSchId);  					
			jrParam.setField("YD_WRK_PROG_STAT"    		, ydWrkProgStat);  					
			jrParam.setField("YD_WRK_PROG_REQ_MSG"   	, sReqMsg);  					

			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCrnSchLocLog
			SELECT YD_CRN_SCH_ID
			     , YD_EQP_ID
			     , YD_DN_WO_LOC_TO
			     , STL_NO_TEMP
			     , STK_LYR_NO_TEMP
			     , YD_DN_WO_LOC
			     , YD_DN_WO_LAYER
			     , YD_WBOOK_ID
			     , YD_WRK_PROG_STAT
			     , YD_SCH_CD
			     , YD_UP_WR_LOC
			     , YD_L2_REQUEST_STAT
			     , YD_SCH_PRIOR
			  FROM TB_YD_CRNSCH
			 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			*/   
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCrnSchLocLog", logId, mthdNm, "대상작업 조회");
			if (jsCrnSch.size() == 0) {
				throw new Exception("크레인 스케쥴 번호가 없습니다..! [" + ydCrnSchId + "]");
			} else {
				ydWbookId 		= jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID"       );     
				ydSchCd 		= jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD"         );       
				ydWrkProgStst	= jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"  );
				ydUpWrLoc	    = jsCrnSch.getRecord(0).getFieldString("YD_UP_WR_LOC"      ); //권상위치
				
				ydChgStlNo   	= jsCrnSch.getRecord(0).getFieldString("STL_NO_TEMP"       ); //변경할 코일번호
				ydChgDnWoLoc	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC_TO"   ); //변경후저장위치
				ydChgDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("STK_LYR_NO_TEMP"   ); //변경후저장위치
				
				ydBefDnWoLoc 	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC"      ); //변경전저장위치
				ydBefDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LAYER"    ); //변경전저장위치
				ydL2RequestStat	= jsCrnSch.getRecord(0).getFieldString("YD_L2_REQUEST_STAT"); //야드L2요구상태
				
				ydSchPrior      = jsCrnSch.getRecord(0).getFieldString("YD_SCH_PRIOR"      );    //스케줄우선순위
			} 
			
			/**********************************************************
			 * 스케줄 트랜잭션 문제로 인해 권하지시위치가 공백인 경우
			 * 권하위치 업데이트 후 다시 지시 내려감으로 종료처리
			 **********************************************************/
			if ("".equals(ydBefDnWoLoc)) {
				commUtils.printLog(logId, "권하지시 위치 없음!! 스케줄생성 트랜잭션 Error", "SL");
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
			}
			
			/**************************************
			 * L2 응답메시지 UPDATE 
			 **************************************/
			/*
			UPDATE TB_YD_CRNSCH  
			   SET MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE
			     , YD_WRK_PROG_REQ_MSG = :V_YD_WRK_PROG_REQ_MSG
			 WHERE YD_CRN_SCH_ID       = :V_YD_CRN_SCH_ID
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnSchProgReqMsg", logId, mthdNm,  "L2 응답메시지 UPDATE");
				
			/**********************************************************
			* 2. 권하위치 변경 요청 결과
			**********************************************************/
			commUtils.printLog(logId, "YD_WRK_PROG_STAT["+ ydWrkProgStat +"] YD_L2_REQUEST_STAT["+ ydL2RequestStat +"] YD_DN_WO_LOC_TO["+ ydChgDnWoLoc +"]", "SL");
			
			if (("1".equals(ydWrkProgStat) || "5".equals(ydWrkProgStat)) && "5".equals(ydL2RequestStat)) {
				
				commUtils.printLog(logId, "권하위치 변경 ["+ ydWrkProgStat +"] 응답", "SL");
						
				// 응답전문 N 일때(작업 불가메세지)
				if ("N".equals(sReqYn)) {
					commUtils.printLog(logId, mthdNm + "권하위치 변경 불가일 경우", "S-");
					return jrRtn;
				}
				
				String ydChgStkColGp = ydChgDnWoLoc.substring(0, 6); 
				String ydChgStkBedNo = ydChgDnWoLoc.substring(6, 8);
				String ydBefStkColGp = ""; 
				String ydBefStkBedNo = "";
				
				if (ydBefDnWoLoc.length() == 8 && (!"XX010101".equals(ydBefDnWoLoc))) {	
					ydBefStkColGp = ydBefDnWoLoc.substring(0, 6); 
					ydBefStkBedNo = ydBefDnWoLoc.substring(6, 8);
				}
				
				/**********************************************************
				*  변경전 정보 수정
				*  기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다.
				**********************************************************/
				JDTORecord jrOldLoc = commUtils.getParam(logId, mthdNm, sModifier);
				
				if (ydBefDnWoLoc.length() == 8 && (!"XX010101".equals(ydBefDnWoLoc))) {	
					String compSTL_NO  = "";
					jrOldLoc = commUtils.getParam(logId, mthdNm, sModifier);
					jrOldLoc.setField("YD_STK_COL_GP"   , ydBefStkColGp);	
					jrOldLoc.setField("YD_STK_BED_NO"   , ydBefStkBedNo);	
					jrOldLoc.setField("YD_STK_LYR_NO"   , ydBefDnWoLayer);	
					/* 
					SELECT *
					  FROM TB_YD_STKLYR
					 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
					   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
					   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
					*/   
					JDTORecordSet jsBefStkLay = commDao.select(jrOldLoc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStkLyrByPk", logId, mthdNm, "기존 적재위치 조회");				
					if (jsBefStkLay.size() == 0) {
						throw new Exception("저장위치 이상.! [" + ydCrnSchId + "]");
					} else {
						compSTL_NO = jsBefStkLay.getRecord(0).getFieldString("STL_NO");
						// 기존 변경 재료 와 수정할 재료가 동일한지 비교
						if (ydChgStlNo.equals(compSTL_NO)) {
					
							// 기존 권하지시위치 정보 Clear
							jrOldLoc.setField("STL_NO"              , "");
							jrOldLoc.setField("YD_STK_LYR_ACT_STAT"	, "E");
							jrOldLoc.setField("YD_STK_LYR_MTL_STAT" , "E");
							/*
							UPDATE TB_YD_STKLYR            
							   SET MOD_DDTT     = SYSDATE             
							     , MODIFIER     = :V_MODIFIER             
							     , YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT, YD_STK_LYR_ACT_STAT)
							     , YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT, YD_STK_LYR_MTL_STAT)
							     , STL_NO              = :V_STL_NO
							 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
							   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
							   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO    
					    	 */
							commDao.update(jrOldLoc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkLyrYdStkColBedGp", logId, mthdNm, "기존위치 Clear");
						}
					}
				}	
	
				/**********************************************************
				* 3. 신규 위치 SET
				**********************************************************/		
				JDTORecord jrNewLoc = commUtils.getParam(logId, mthdNm, sModifier);
				
				// 신규 저장위치 CHECK
				jrNewLoc.setField("YD_STK_COL_GP" 	, ydChgStkColGp);	
				jrNewLoc.setField("YD_STK_BED_NO" 	, ydChgStkBedNo);	
				jrNewLoc.setField("YD_STK_LYR_NO" 	, ydChgDnWoLayer);	
				/*
				SELECT *
				  FROM TB_YD_STKLYR
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
				   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
				*/
				JDTORecordSet jsChgStkLay = commDao.select(jrNewLoc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStkLyrByPk", logId, mthdNm, "신규 적재위치 조회");				
				
				if (jsChgStkLay.size() == 0) {
					throw new Exception("저장위치 이상.! [" + ydCrnSchId + "]");
				}

				// 신규위치에 정보를 Setting
				jrNewLoc.setField("STL_NO"             , ydChgStlNo    );
				jrNewLoc.setField("YD_STK_LYR_ACT_STAT", "E"           );
				jrNewLoc.setField("YD_STK_LYR_MTL_STAT", "D"           );
				/*
				UPDATE TB_YD_STKLYR            
				   SET MOD_DDTT     = SYSDATE             
				     , MODIFIER     = :V_MODIFIER             
				     , YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT, YD_STK_LYR_ACT_STAT)
				     , YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT, YD_STK_LYR_MTL_STAT)
				     , STL_NO              = :V_STL_NO
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
				   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO  
		    	 */
				commDao.update(jrNewLoc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkLyrYdStkColBedGp", logId, mthdNm, "신규위치 등록");				

				// 신규 좌표 정보 READ
				jrNewLoc.setField("YD_CRN_SCH_ID"	        , ydCrnSchId);
				jrNewLoc.setField("YD_DN_WO_LOC"	     	, ydChgDnWoLoc);	
				jrNewLoc.setField("YD_DN_WO_LAYER"	        , ydChgDnWoLayer);

				if ("".equals(ydUpWrLoc)) {
					jrNewLoc.setField("YD_WRK_PROG_STAT"	, "1");
				} else {
					jrNewLoc.setField("YD_WRK_PROG_STAT"	, "2");
				}
				/* 
				MERGE INTO TB_YD_CRNSCH CS USING (
				SELECT SL.YD_STK_LYR_XAXIS      AS YD_DN_WO_LOC_XAXIS
				     , SB.YD_STK_BED_XAXIS_TOL  AS YD_DN_WO_XAXIS_GAP_MAX
				     , SB.YD_STK_BED_XAXIS_TOL  AS YD_DN_WO_XAXIS_GAP_MIN
				     , SL.YD_STK_LYR_YAXIS      AS YD_DN_WO_LOC_YAXIS
				     , SB.YD_STK_BED_YAXIS_TOL  AS YD_DN_WO_YAXIS_GAP_MAX 
				     , SB.YD_STK_BED_YAXIS_TOL  AS YD_DN_WO_YAXIS_GAP_MIN 
				     , SL.YD_STK_LYR_ZAXIS      AS YD_DN_WO_LOC_ZAXIS
				     , SB.YD_STK_BED_ZAXIS_TOL  AS YD_DN_WO_ZAXIS_GAP_MAX 
				     , SB.YD_STK_BED_ZAXIS_TOL  AS YD_DN_WO_ZAXIS_GAP_MIN 
				     , SC.ROTATION_ANGLE        AS DOWN_ROTATION_ANGLE
				     , :V_YD_CRN_SCH_ID         AS YD_CRN_SCH_ID
				  FROM TB_YD_STKCOL     SC
				     , TB_YD_STKBED     SB
				     , TB_YD_STKLYR     SL
				 WHERE SC.YD_STK_COL_GP = SB.YD_STK_COL_GP
				   AND SB.YD_STK_COL_GP = SL.YD_STK_COL_GP
				   AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO
				   AND SL.YD_STK_COL_GP = SUBSTR(:V_YD_DN_WO_LOC,1,6)
				   AND SL.YD_STK_BED_NO = SUBSTR(:V_YD_DN_WO_LOC,7,2)
				   AND SL.YD_STK_LYR_NO = :V_YD_DN_WO_LAYER
				       
				) DD ON (CS.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID)
				
				WHEN MATCHED THEN UPDATE SET
				       MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				     , YD_DN_WO_LOC             = NVL(:V_YD_DN_WO_LOC           ,YD_DN_WO_LOC)
				     , YD_DN_WO_LAYER           = NVL(:V_YD_DN_WO_LAYER         ,YD_DN_WO_LAYER)
				     , YD_DN_WO_LOC_XAXIS       = NVL(DD.YD_DN_WO_LOC_XAXIS     ,YD_DN_WO_LOC_XAXIS)
				     , YD_DN_WO_XAXIS_GAP_MAX   = NVL(DD.YD_DN_WO_XAXIS_GAP_MAX ,YD_DN_WO_XAXIS_GAP_MAX)
				     , YD_DN_WO_XAXIS_GAP_MIN   = NVL(DD.YD_DN_WO_XAXIS_GAP_MIN ,YD_DN_WO_XAXIS_GAP_MIN)
				     , YD_DN_WO_LOC_YAXIS       = NVL(DD.YD_DN_WO_LOC_YAXIS     ,YD_DN_WO_LOC_YAXIS)
				     , YD_DN_WO_YAXIS_GAP_MAX   = NVL(DD.YD_DN_WO_YAXIS_GAP_MAX ,YD_DN_WO_YAXIS_GAP_MAX)
				     , YD_DN_WO_YAXIS_GAP_MIN   = NVL(DD.YD_DN_WO_YAXIS_GAP_MIN ,YD_DN_WO_YAXIS_GAP_MIN)
				     , YD_DN_WO_LOC_ZAXIS       = NVL(DD.YD_DN_WO_LOC_ZAXIS     ,YD_DN_WO_LOC_ZAXIS)
				     , YD_DN_WO_ZAXIS_GAP_MAX   = NVL(DD.YD_DN_WO_ZAXIS_GAP_MAX ,YD_DN_WO_ZAXIS_GAP_MAX)
				     , YD_DN_WO_ZAXIS_GAP_MIN   = NVL(DD.YD_DN_WO_ZAXIS_GAP_MIN ,YD_DN_WO_ZAXIS_GAP_MIN)
				     , YD_L2_REQUEST_STAT       = :V_YD_L2_REQUEST_STAT 
				     , YD_DN_WO_LOC_TO          = :V_YD_DN_WO_LOC_TO
				     , YD_WRK_PROG_STAT         = :V_YD_WRK_PROG_STAT
				     , YD_WORD_DT               = SYSDATE
				     , DOWN_ROTATION_ANGLE      = NVL(DD.DOWN_ROTATION_ANGLE    ,DOWN_ROTATION_ANGLE)
				 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				 */				
				commDao.update(jrNewLoc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnWrk", logId, mthdNm, "TB_YD_CRNSCH 등록");		
				
				//권상지시 or 권상완료
	    		if ( "1".equals(ydWrkProgStst) || "2".equals(ydWrkProgStst)) { //권상지시 or 권상완료
	    			JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
	    			jrYdMsg.setField("JMS_TC_CD"         	, "Y5YDL007"); 					//JMSTC코드
	    			jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); 	//JMSTC생성일시
	    			jrYdMsg.setField("YD_EQP_ID"        	, ydEqpId);  					//야드설비ID
	    			jrYdMsg.setField("YD_WRK_PROG_STAT"     , ydWrkProgStst); 				//야드작업진행상태
	    			jrYdMsg.setField("YD_SCH_CD"        	, ydSchCd);						//야드스케쥴코드
	    			jrYdMsg.setField("YD_CRN_SCH_ID"        , ydCrnSchId); 					//야드크레인스케쥴ID
	    			
	    			//크레인작업지시요구 전문을 추가
					jrRtn = commUtils.addSndData(jrRtn, this.rcvY5YDL007(jrYdMsg));
				}
	    		
	    		//------------------------------------------------------------------------
	    		// 본래 권하위치가 차량 또는 대차에서 일반야드로 권하위치를 변경하는경우
	    		//------------------------------------------------------------------------
	    		String szChgEqpGp    = ydChgDnWoLoc.substring(2,4); 
	    		String szBefEqpGp    = "";
	    		// 기존 권하위치 
	    		if (ydBefDnWoLoc.length() >= 6) {
	    			szBefEqpGp = ydBefDnWoLoc.substring(2,4);
	    		}
	    		
	    		// 기존 설비구분이 차량/대차이고 , 신규 설비 구분이 그 차량/대차 작업이 아닌경우  
	    		// 작업예약 ID를 Clear  한다.
	    		if ("TC".equals(szBefEqpGp) || "PT".equals(szBefEqpGp) || "TR".equals(szBefEqpGp)) {
	    			if (!szChgEqpGp.equals(szBefEqpGp)) {

	    				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
	    				
	    				String szULGp  = ydSchCd.substring(6,7);  //상차구분
	    				String szCarGp = ydSchCd.substring(2,4);
	    				
	    				//스케줄 코드가 차량/대차 인경우 구분
	    				if ("TC".equals(szCarGp)) {
	    	 				if ("U".equals(szULGp)) {  

	    	 					/* 
	    	 					--대차스케줄 작업예약ID 삭제 
								UPDATE TB_YD_TCARSCH
								   SET MODIFIER              = :V_MODIFIER
								     , MOD_DDTT              = SYSDATE
								     , YD_CARLD_WRK_BOOK_ID  = NULL
								 WHERE DEL_YN                = 'N'
								   AND YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
	    	    				*/   
	    	    				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updTcarSchWbDelLd", logId, mthdNm, "TB_YD_TCARSCH");				

	    					} else if ("L".equals(szULGp)) {
	    						//하차인경우 작업예약 정보 삭제
	    						/* 
								UPDATE TB_YD_TCARSCH
								   SET MODIFIER              = :V_MODIFIER
								     , MOD_DDTT              = SYSDATE
								     , YD_CARUD_WRK_BOOK_ID  = NULL
								 WHERE DEL_YN                = 'N'
								   AND YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
	    	    				*/   
	    	    				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updTcarSchWbDelUd", logId, mthdNm, "TB_YD_TCARSCH");				
	    						
	    					}
	    					
	    				} else if ("PT".equals(szCarGp) || "TR".equals(szCarGp)) {
	    					// 차량인 경우 		
	    					if ("U".equals(szULGp)) {
	    						
	    						/* 
								UPDATE TB_YD_CARSCH
								   SET MODIFIER              = :V_MODIFIER
								     , MOD_DDTT              = SYSDATE
								     , YD_CARLD_WRK_BOOK_ID  = NULL
								 WHERE DEL_YN                = 'N'
								   AND YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
	    						 */  
	    						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCarSchWbDelLd", logId, mthdNm, "TB_YD_CARSCH");				
	    						
	    					} else if ("L".equals(szULGp)) {
	    						//하차인경우 작업예약 정보 삭제
	    						/* 
								UPDATE TB_YD_CARSCH
								   SET MODIFIER              = :V_MODIFIER
								     , MOD_DDTT              = SYSDATE
								     , YD_CARUD_WRK_BOOK_ID  = NULL
								 WHERE DEL_YN                = 'N'
								   AND YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
	    						*/   
	    						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCarSchWbDelUd", logId, mthdNm, "TB_YD_CARSCH");				
	    					}
	    				}
	    			}	
	    		}
	    		
			} else {
				
				String sGenLineOff = "N";  //일반입고 여부
				JDTORecord jrParamSet = commUtils.getParam(logId, mthdNm, sModifier);
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getLineOffStartUpYn  
				SELECT YD_SCH_CD
				  FROM TB_YD_SCHRULE
				 WHERE YD_SCH_CD = :V_YD_SCH_CD
				   AND DEL_YN = 'N'
				   AND YD_SCH_CD IN ('JHKD01LM','JGFD01LM','JEKD01LM','JCKD01LM','JBKD01LM')
				*/
				jrParamSet.setField("YD_SCH_CD" 	    , ydSchCd);
				jrParamSet.setField("YD_CRN_SCH_ID" 	, ydCrnSchId);
				JDTORecordSet jsCrnSchChk = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getLineOffStartUpYn", logId, mthdNm, "일반작업 여부");
    	    	
				if (jsCrnSchChk.size() > 0) {
					sGenLineOff = "Y";
				}
				/**********************************************************
				* 2. 권하위치 변경이 아닌 경우
				**********************************************************/						
				if ("Y".equals(sReqYn)) {
					commUtils.printLog(logId, "유인 응답 가능 : MSG_GP["+ sMsgGp +"] REQ_YN["+ sReqYn +"] YD_WRK_PROG_STAT["+ ydWrkProgStat +"]", "SL");
					
					// 크레인 호기 변경일때 이전크레인 취소 전문 수신시 선택상태 업데이트 하므로 막음
					if( !"D".equals(sMsgGp) ) {
						/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnSchProgStatMsg
						UPDATE TB_YD_CRNSCH  A
						   SET MODIFIER            = :V_MODIFIER
						     , MOD_DDTT            = SYSDATE
						     , YD_WRK_PROG_STAT    = CASE WHEN YD_WRK_PROG_STAT IN ('S', '1') THEN :V_YD_WRK_PROG_STAT
						                                  ELSE YD_WRK_PROG_STAT
						                             END
						     , YD_WORD_DT          = CASE WHEN :V_YD_WRK_PROG_STAT = 'W'         THEN NULL
						                                  WHEN :V_YD_WRK_PROG_STAT IN ('S', '1') THEN SYSDATE
						                                  ELSE YD_WORD_DT
						                             END
						 WHERE YD_CRN_SCH_ID       = :V_YD_CRN_SCH_ID
						   AND DEL_YN              = 'N'
						*/
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnSchProgStatMsg", logId, mthdNm,  "TB_YD_CRNSCH");
					} 
					
					/******************************************************************
					 * 입고대차 작업 응답 Y 일 때 다른 입고 작업 권하번지 00으로 변경 
					 ****************************************************************/
					if ("TC".equals(ydSchCd.substring(2, 4)) && "MM".equals(ydSchCd.substring(6, 8))) { //동간입고 스케줄
						/*
						UPDATE TB_YD_CRNSCH
						   SET MODIFIER       = :V_MODIFIER
						     , MOD_DDTT       = SYSDATE
						     , YD_DN_WO_LOC   = SUBSTR(YD_DN_WO_LOC, 0, 6)||'00'
						     , YD_DN_WO_LAYER = '001'
						 WHERE YD_GP     = 'J'
						   AND DEL_YN    = 'N'
						   AND YD_SCH_CD = (SELECT YD_SCH_CD FROM TB_YD_CRNSCH WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
						   AND YD_CRN_SCH_ID != :V_YD_CRN_SCH_ID
						   AND SUBSTR(YD_DN_WO_LOC, 7, 2) != '00' 
						   AND SUBSTR(YD_DN_WO_LOC, 3, 2)  = 'TC' 
						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updTcMmDnLocBed00", logId, mthdNm, "동간입고 권하위치번지 00초기화");
						
						//권하지시 위치가 일반야드일 경우 야드맵 정리
						/*
						UPDATE TB_YD_STKLYR
						   SET MODIFIER            = :V_MODIFIER
						     , MOD_DDTT            = SYSDATE
						     , STL_NO              = '' 
						     , YD_STK_LYR_MTL_STAT = 'E'
						 WHERE 1 = 1
						   AND YD_STK_LYR_MTL_STAT = 'D'
						   AND (YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO) NOT IN (
						                                                            SELECT SUBSTR(YD_DN_WO_LOC, 1, 6) AS YD_STK_COL_GP
						                                                                 , SUBSTR(YD_DN_WO_LOC, 7, 2) AS YD_STK_BED_NO
						                                                                 , YD_DN_WO_LAYER             AS YD_STK_LYR_NO
						                                                              FROM TB_YD_CRNSCH
						                                                             WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						                                                            )
						   AND STL_NO              = (SELECT STL_NO
						                                FROM TB_YD_CRNWRKMTL
						                               WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						                             )
						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updDnWoLocClear", logId, mthdNm, "권하위치 초기화");
					}
					/******************************************************************
					 * 일반재 입고 line off 시 처리  
					 ****************************************************************/
					// 일반입고 'XX' 처리
					if( "Y".equals(sGenLineOff) ) {
						/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnSchClear 
						UPDATE TB_YD_CRNSCH
						   SET MODIFIER = :V_MODIFIER
						     , MOD_DDTT = SYSDATE
						     , YD_DN_WO_LOC   = 'XX010101'
						     , YD_DN_WO_LAYER = ''
						 WHERE DEL_YN  = 'N'
						   AND YD_CRN_SCH_ID != :V_YD_CRN_SCH_ID 
						   AND YD_SCH_CD      = :V_YD_SCH_CD
						   AND YD_DN_WO_LOC  != 'XX010101'
						   AND YD_DN_WO_LOC  IS NOT NULL       
						 */
						jrParamSet.setField("YD_SCH_CD" 	    , ydSchCd);
						jrParamSet.setField("YD_CRN_SCH_ID" 	, ydCrnSchId);
						commDao.update(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnSchClear", logId, mthdNm, "일반입고 권하위치번지 'XX'초기화");
						
						/* 이전 스케줄이 동간입고일 경우 권하위치 초기화 
						UPDATE TB_YD_CRNSCH
						   SET MODIFIER       = :V_MODIFIER
						     , MOD_DDTT       = SYSDATE
						     , YD_DN_WO_LOC   = SUBSTR(YD_DN_WO_LOC, 0, 6)||'00'
						     , YD_DN_WO_LAYER = '001'
						 WHERE YD_GP     = 'J'
						   AND DEL_YN    = 'N'
						   AND YD_SCH_CD = (SELECT YD_SCH_CD FROM TB_YD_CRNSCH WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
						   AND SUBSTR(YD_DN_WO_LOC, 7, 2) != '00' 
						   AND YD_SCH_CD LIKE 'J_TC__MM'
						 */
						jrParamSet.setField("YD_CRN_SCH_ID" 	, ydCrnSchIdOld);
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updTcMmDnLocBed002", logId, mthdNm, "이전동간입고 권하위치번지 00초기화");						
					}
				} else if ("N".equals(sReqYn)) {
					commUtils.printLog(logId, "유인 응답 불가 : REQ_YN["+ sReqYn +"] YD_WRK_PROG_STAT["+ ydWrkProgStat +"]", "SL");
					
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					 
					if (sReqMsg.length() >= 4) {
						
						if ("E001".equals(sErrCd)) {
							
							//작업지시가 내려와 이미 코일을 집었는데 다음작업지시가 내려온 경우
							//다음작업지시를 수행할 수 없는 상황 (차상국은 이전 작업지시를 그대로 실행)
							//L3 는 'N' 응답에 'E001' 메세지 받은 크레인 스케줄 ID의 작업진행상태를  'W'로 변경한다.
							commUtils.printLog(logId, mthdNm + "E001 불가 일경우", "SL");
							
							/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnSchProgStatMsg
							UPDATE TB_YD_CRNSCH  A
							   SET MODIFIER            = :V_MODIFIER
							     , MOD_DDTT            = SYSDATE
							     , YD_WRK_PROG_STAT    = CASE WHEN YD_WRK_PROG_STAT IN ('S', '1') THEN :V_YD_WRK_PROG_STAT
							                                  ELSE YD_WRK_PROG_STAT
							                             END
							     , YD_WORD_DT          = CASE WHEN :V_YD_WRK_PROG_STAT = 'W'         THEN NULL
							                                  WHEN :V_YD_WRK_PROG_STAT IN ('S', '1') THEN SYSDATE
							                                  ELSE YD_WORD_DT
							                             END
							 WHERE YD_CRN_SCH_ID       = :V_YD_CRN_SCH_ID
							   AND DEL_YN              = 'N'
							*/
							jrParam.setField("YD_WRK_PROG_STAT"	, "W");
							jrParam.setField("YD_CRN_SCH_ID"   	, ydCrnSchId);
							commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnSchProgStatMsg", logId, mthdNm,  "취소된 스케줄 작업진행상태 변경");
							
							/**********************************************************
							 * 권하위치 대차일때 권하번지 '00', TB_YD_STKLYR 초기화
							***********************************************************/
							// 권하위치 대차, 00번지가 아닐때
							if( "TC".equals(ydBefDnWoLoc.substring(2, 4)) && !"00".equals(ydBefDnWoLoc.substring(6, 8)) ) {

								// 이전 크레인스케쥴 권하위치 대차 00 번지로 변경
								/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLoc
								UPDATE TB_YD_CRNSCH
								   SET MODIFIER = :V_MODIFIER
								      ,MOD_DDTT = SYSDATE
								      ,YD_DN_WO_LOC             = NVL(:V_YD_DN_WO_LOC           ,YD_DN_WO_LOC)
								      ,YD_DN_WO_LAYER           = NVL(:V_YD_DN_WO_LAYER         ,YD_DN_WO_LAYER)
								      ...
								 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
								*/
							    jrParam.setField("YD_DN_WO_LOC"		, ydBefDnWoLoc.substring(0, 6) + "00");
							    jrParam.setField("YD_DN_WO_LAYER"	, "001");
								commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLoc", logId, mthdNm,  "취소된 스케쥴 대차권하위치 초기화");

								/**************************************************
								 * - 취소된 작업 대차권하위치이면 TB_YD_STKLYR 원복
								 **************************************************/

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
							    jrParam.setField("YD_STK_COL_GP"		, ydBefDnWoLoc.substring(0, 6));
							    jrParam.setField("YD_STK_BED_NO"		, ydBefDnWoLoc.substring(6, 8));
							    jrParam.setField("YD_STK_LYR_NO"		, "001");
							    jrParam.setField("STL_NO"				, "");
							    jrParam.setField("YD_STK_LYR_MTL_STAT"	, "E");
								commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyr", logId, mthdNm, "취소된 스케쥴 대차권하위치 초기화");
							}
							
							/**************************************************
							 * 일반입고 작업시작
							 **************************************************/
							if( "Y".equals(sGenLineOff) ) {
								
								// 이전 크레인스케쥴 권하위치 XXXX 번지로 변경
								/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLocDn 
								UPDATE TB_YD_CRNSCH C
								   SET MODIFIER = :V_MODIFIER
								      ,MOD_DDTT = SYSDATE
								      ,YD_DN_WO_LOC             = :V_YD_DN_WO_LOC  
								      ,YD_DN_WO_LAYER           = :V_YD_DN_WO_LAYER
								      ,YD_CRN_GRAB_USE_RULE_ID  = ( SELECT B.YD_CAR_SCH_ID
								                                      FROM TB_YD_WRKBOOK A
								                                         , TB_YD_CARSCH B
								                                     WHERE B.DEL_YN='N'
								                                       AND (B.CAR_NO=A.CAR_NO OR B.TRN_EQP_CD = A.TRN_EQP_CD )
								                                       AND A.YD_WBOOK_ID =  (SELECT B1.YD_WBOOK_ID 
								                                                               FROM TB_YD_CRNSCH B1
								                                                              WHERE B1.YD_CRN_SCH_ID = C.YD_CRN_SCH_ID )
								                                       AND ROWNUM <= 1)
								 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
								*/
								jrParamSet.setField("YD_CRN_SCH_ID" 	, ydCrnSchId);
								jrParamSet.setField("YD_DN_WO_LOC"		, "XX010101");
								jrParamSet.setField("YD_DN_WO_LAYER"	, "");
								commDao.update(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLocDn", logId, mthdNm,  "취소된 스케쥴 일반입고 권하위치 초기화");

								/**************************************************
								 * - 취소된 작업 일반입고 권하위치이면 TB_YD_STKLYR 원복
								 **************************************************/

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
								jrParamSet.setField("YD_STK_COL_GP"			, ydBefDnWoLoc.substring(0, 6));
								jrParamSet.setField("YD_STK_BED_NO"			, ydBefDnWoLoc.substring(6, 8));
								jrParamSet.setField("YD_STK_LYR_NO"			, ydBefDnWoLayer);
								jrParamSet.setField("STL_NO"				, "");
								jrParamSet.setField("YD_STK_LYR_MTL_STAT"	, "E");
								commDao.update(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyr", logId, mthdNm, "취소된 스케쥴 일반입고 권하위치 초기화");					
								
							}
							
							if (!"".equals(ydCrnSchIdOld)) {

								//이전 스케줄ID의 상태를 '1'로 변경한다.
								/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnSchProgStatMsg
								UPDATE TB_YD_CRNSCH  A
								   SET MODIFIER            = :V_MODIFIER
								     , MOD_DDTT            = SYSDATE
								     , YD_WRK_PROG_STAT    = CASE WHEN YD_WRK_PROG_STAT IN ('S', '1') THEN :V_YD_WRK_PROG_STAT
								                                  ELSE YD_WRK_PROG_STAT
								                             END
								     , YD_WORD_DT          = CASE WHEN :V_YD_WRK_PROG_STAT = 'W'         THEN NULL
								                                  WHEN :V_YD_WRK_PROG_STAT IN ('S', '1') THEN SYSDATE
								                                  ELSE YD_WORD_DT
								                             END
								 WHERE YD_CRN_SCH_ID       = :V_YD_CRN_SCH_ID
								   AND DEL_YN              = 'N'
								*/
								jrParam.setField("YD_WRK_PROG_STAT"	, "1");
								jrParam.setField("YD_CRN_SCH_ID"   	, ydCrnSchIdOld);
								commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnSchProgStatMsg", logId, mthdNm,  "이전 크레인스케줄 작업진행상태  변경");

								/**********************************************************
								 * Line-Off 긴급작업 취소, 이전 Line-Off 작업 복구
								***********************************************************/
								/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCrnsch
								SELECT YD_EQP_ID                        AS YD_EQP_ID
								     , YD_CRN_SCH_ID                    AS YD_CRN_SCH_ID
								       ...
								     , YD_DN_WO_LOC                     AS YD_DN_WO_LOC
								     , YD_DN_WO_LAYER                   AS YD_DN_WO_LAYER
								       ...
								  FROM TB_YD_CRNSCH
								 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
								*/
								JDTORecordSet jsCrnWrkOld = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCrnsch", logId, mthdNm, "이전 크레인스케쥴 조회");
								if( jsCrnWrkOld.size() > 0 ) {
									JDTORecord jrCrnSchOld = jsCrnWrkOld.getRecord(0);

									// 종료 되지 않은 크레인 스케쥴
									if( "N".equals(jrCrnSchOld.getField("DEL_YN"))) {

										String ydDnWoLocOld = commUtils.trim(jsCrnWrkOld.getRecord(0).getFieldString("YD_DN_WO_LOC"));
										String ydDnWoLayerOld = commUtils.trim(jsCrnWrkOld.getRecord(0).getFieldString("YD_DN_WO_LAYER"));

										/**************************************************
										 * - 이전 작업 대차권하위치이면 TB_YD_STKLYR 원복
										 **************************************************/
										if( "TC".equals(ydDnWoLocOld.substring(2, 4)) && !"00".equals(ydDnWoLocOld.substring(6, 8))) {

											/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getStockIdByCrnSchId
											SELECT CM.STL_NO
											  FROM TB_YD_CRNSCH    CS
											     , TB_YD_CRNWRKMTL CM
											 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
											   AND CS.DEL_YN = 'N'
											   AND CM.DEL_YN = 'N'
											   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
											 ORDER BY CM.YD_STK_LYR_NO
											*/
											JDTORecordSet jsCrnWrkMtl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getStockIdByCrnSchId", logId, mthdNm, "이전 크레인스케쥴 코일번호 조회");

											if( jsCrnWrkMtl.size() > 0 ) {

												JDTORecord jrCrnWrkMtl = jsCrnWrkMtl.getRecord(0);
												String sStlNoOld = commUtils.trim(jrCrnWrkMtl.getFieldString("STL_NO"));

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
											    jrParam.setField("STL_NO"				, sStlNoOld);
											    jrParam.setField("YD_STK_LYR_MTL_STAT"	, "D");
												commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyr", logId, mthdNm, "작업중인 Line-Off 대차권하위치 초기화");
											}
										}
										
										/**************************************************
										 * - 이전 작업 일반입고 권하위치이면 TB_YD_STKLYR 원복
										 **************************************************/
										if( "Y".equals(sGenLineOff) ) {

											/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getStockIdByCrnSchId
											SELECT CM.STL_NO
											  FROM TB_YD_CRNSCH    CS
											     , TB_YD_CRNWRKMTL CM
											 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
											   AND CS.DEL_YN = 'N'
											   AND CM.DEL_YN = 'N'
											   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
											 ORDER BY CM.YD_STK_LYR_NO
											*/
											jrParamSet.setField("YD_CRN_SCH_ID"   	, ydCrnSchIdOld);
											JDTORecordSet jsCrnWrkMtl1 = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getStockIdByCrnSchId", logId, mthdNm, "이전 크레인스케쥴 코일번호 조회");

											if( jsCrnWrkMtl1.size() > 0 ) {

												JDTORecord jrCrnWrkMtl1 = jsCrnWrkMtl1.getRecord(0);
												String sStlNoOld = commUtils.trim(jrCrnWrkMtl1.getFieldString("STL_NO"));

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
												jrParamSet.setField("STL_NO"				, sStlNoOld);
												jrParamSet.setField("YD_STK_LYR_MTL_STAT"	, "D");
												commDao.update(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyr", logId, mthdNm, "작업중인 Line-Off 대차권하위치 초기화");
											}
										}
										
									}
								}
							}									
						}
					}
				}
			}
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업가능응답(rcvY5YDL015Auto)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL015Auto(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "크레인작업가능응답[CCoilL2RcvSeEJB.rcvY5YDL015Auto] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();

		JDTORecord jrRtn = commUtils.getParam(logId, mthdNm, "Y5YDL015");	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값ydEqpId
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId     	 = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"          )); //설비ID
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"   )); //야드작업진행상태(5:권하위치변경)만 올라옴
			String ydSchCd   	 = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"          )); //야드스케쥴코드
			String ydCrnSchId  	 = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"      )); //야드크레인스케쥴ID
			String sReqYn  		 = commUtils.trim(rcvMsg.getFieldString("REQ_YN"             )); //유무응답
			String sReqMsg       = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_REQ_MSG")); //메시지
			String sErrCd        = commUtils.trim(rcvMsg.getFieldString("ERR_CD"             )); //에러코드
			String ydCrnSchIdOld = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID_OLD"  )); //이전 스케줄ID

			String sModifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); 		//수정자(Backup Only)

			String sMsgGp       = commUtils.trim(rcvMsg.getFieldString("MSG_GP"     )); 		//전문구분I(신규), U(수정), D(취소,삭제), R(재 전송)

			String ydChgStlNo 		= "";
			String ydChgDnWoLoc	    = "";
			String ydChgDnWoLayer	= "";
			String ydBefDnWoLoc		= "";
			String ydBefDnWoLayer	= "";
			String ydWbookId 		= "";
			String ydWrkProgStst	= "";
			String ydUpWrLoc	    = "";
			String ydL2RequestStat	= "";
			String ydSchPrior       = ""; //스케줄 우선순위

			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;

			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if (!coilDao.chkAutoCrn(logId, mthdNm, ydEqpId)) {
				commUtils.printLog(logId, ydEqpId + "무인 크레인이 아닙니다", "SL");

				return jrRtn;
			}

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_EQP_ID"         		, ydEqpId);
			jrParam.setField("YD_CRN_SCH_ID"      		, ydCrnSchId);
			jrParam.setField("YD_WRK_PROG_STAT"    		, ydWrkProgStat);
			jrParam.setField("YD_WRK_PROG_REQ_MSG"   	, sReqMsg);

			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCrnSchLocLog
			SELECT YD_CRN_SCH_ID
			     , YD_EQP_ID
			     , YD_DN_WO_LOC_TO
			     , STL_NO_TEMP
			     , STK_LYR_NO_TEMP
			     , YD_DN_WO_LOC
			     , YD_DN_WO_LAYER
			     , YD_WBOOK_ID
			     , YD_WRK_PROG_STAT
			     , YD_SCH_CD
			     , YD_UP_WR_LOC
			     , YD_L2_REQUEST_STAT
			     , YD_SCH_PRIOR
			  FROM TB_YD_CRNSCH
			 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			*/
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCrnSchLocLog", logId, mthdNm, "대상작업 조회");
			if (jsCrnSch.size() == 0) {
				throw new Exception("크레인 스케쥴 번호가 없습니다..! [" + ydCrnSchId + "]");
			} else {
				ydWbookId 		= jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID"       );
				ydSchCd 		= jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD"         );
				ydWrkProgStst	= jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"  );
				ydUpWrLoc	    = jsCrnSch.getRecord(0).getFieldString("YD_UP_WR_LOC"      ); //권상위치

				ydChgStlNo   	= jsCrnSch.getRecord(0).getFieldString("STL_NO_TEMP"       ); //변경할 코일번호
				ydChgDnWoLoc	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC_TO"   ); //변경후저장위치
				ydChgDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("STK_LYR_NO_TEMP"   ); //변경후저장위치

				ydBefDnWoLoc 	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC"      ); //변경전저장위치
				ydBefDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LAYER"    ); //변경전저장위치
				ydL2RequestStat	= jsCrnSch.getRecord(0).getFieldString("YD_L2_REQUEST_STAT"); //야드L2요구상태

				ydSchPrior      = jsCrnSch.getRecord(0).getFieldString("YD_SCH_PRIOR"      );    //스케줄우선순위
			}

			/**********************************************************
			 * 스케줄 트랜잭션 문제로 인해 권하지시위치가 공백인 경우
			 * 권하위치 업데이트 후 다시 지시 내려감으로 종료처리			 
			 **********************************************************/
			if ("".equals(ydBefDnWoLoc)) {
				commUtils.printLog(logId, "권하지시 위치 없음!! 스케줄생성 트랜잭션 Error", "SL");
				commUtils.printLog(logId, mthdNm, "S-");
				
				/********************
				 * 스케줄 취소 
				 ********************/
				if ("Y".equals(sReqYn) && "D".equals(sMsgGp)) {
					String sCrnSchYdEqpId = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_EQP_ID"));
					
					commUtils.printLog(logId, "전문수신 설비코드["+ ydEqpId +"] 크레인스케쥴 설비코드["+ sCrnSchYdEqpId +"]", "SL");
					
					if( ydEqpId.equals(sCrnSchYdEqpId) ) {
					
						commUtils.printLog(logId, "스케쥴취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");

						jrParam.setField("YD_WBOOK_ID"  	, ydWbookId );
						jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
						jrParam.setField("YD_EQP_ID"    	, ydEqpId   );
						jrParam.setField("YD_SCH_CD"    	, ydSchCd   );
						jrParam.setField("YD_L2_RETURN_FLAG", "Y"       );
						jrParam.setField("IS_LAST_SELECTED"	, "1");
						jrParam.setField("IS_SCH_MTL"    	, "Y"); // 스케줄 단위 취소

						/**********************************************************
						* 1. 크레인스케줄 취소
						**********************************************************/
						EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
						JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });

						jrRtn = commUtils.addSndData(jrRtn, jrRst);

						commUtils.printLog(logId, "ydL2RequestStat [ " + ydL2RequestStat + " - 작업취소  ]", "SL");

						if ("X".equals(ydL2RequestStat)) {   // 화면에서 작업 취소 임 : 작업예약 삭제
							/**********************************************************
							* 작업예약 취소
							**********************************************************/
							EJBConnector ejbConn1 = new EJBConnector("default", "CCoilJspSeEJB", this);
							jrRst = (JDTORecord)ejbConn1.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });

							jrRtn = commUtils.addSndData(jrRtn, jrRst);
						}
					}
				}
				return jrRtn;
			}

			/*****************************************
			 * 코드가 E009 : 스케줄 삭제 
			 ******************************************/
//			String sAPP003_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "003");
//			if("Y".equals(sAPP003_YN)){
//				
//				if ("E009".equals(sErrCd) && "N".equals(sReqYn)) {
//					commUtils.printLog(logId, "권하지시 위치 2단에 코일 있음", "SL");
//					commUtils.printLog(logId, mthdNm, "S-");
//					
//					/********************
//					 * 스케줄 취소 
//					 ********************/
//					
//						String sCrnSchYdEqpId = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_EQP_ID"));
//						
//						commUtils.printLog(logId,"전문수신 코드["+ sErrCd +"] 전문수신 설비코드["+ ydEqpId +"] 크레인스케쥴 설비코드["+ sCrnSchYdEqpId +"]", "SL");
//						
//						if( ydEqpId.equals(sCrnSchYdEqpId) ) {
//						
//							commUtils.printLog(logId, "스케쥴취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");
//	
//							jrParam.setField("YD_WBOOK_ID"  	, ydWbookId );
//							jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
//							jrParam.setField("YD_EQP_ID"    	, ydEqpId   );
//							jrParam.setField("YD_SCH_CD"    	, ydSchCd   );
//							jrParam.setField("YD_L2_RETURN_FLAG", "Y"       );
//							jrParam.setField("IS_LAST_SELECTED"	, "1");
//							jrParam.setField("IS_SCH_MTL"    	, "Y"); // 스케줄 단위 취소
//	
//							/**********************************************************
//							* 1. 크레인스케줄 취소
//							**********************************************************/
//							EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
//							JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
//	
//							jrRtn = commUtils.addSndData(jrRtn, jrRst);
//	
//							commUtils.printLog(logId, "ydL2RequestStat [ " + ydL2RequestStat + " - 작업취소  ]", "SL");
//	
//							
//								/**********************************************************
//								* 작업예약 취소
//								**********************************************************/
//								EJBConnector ejbConn1 = new EJBConnector("default", "CCoilJspSeEJB", this);
//								jrRst = (JDTORecord)ejbConn1.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
//	
//								jrRtn = commUtils.addSndData(jrRtn, jrRst);
//								
//								
//						}
//					
//						return jrRtn;
//				}
//			}
			/**************************************
			 * L2 응답메시지 UPDATE
			 **************************************/
			/*
			UPDATE TB_YD_CRNSCH
			   SET MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE
			     , YD_WRK_PROG_REQ_MSG = :V_YD_WRK_PROG_REQ_MSG
			 WHERE YD_CRN_SCH_ID       = :V_YD_CRN_SCH_ID
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnSchProgReqMsg", logId, mthdNm,  "TB_YD_CRNSCH");

			/**********************************************************
			* 2. 권하위치 변경 요청 결과
			**********************************************************/
			
			commUtils.printLog(logId, "YD_WRK_PROG_STAT["+ ydWrkProgStat +"] YD_L2_REQUEST_STAT["+ ydL2RequestStat +"] YD_DN_WO_LOC_TO["+ ydChgDnWoLoc +"]", "SL");
			if (("1".equals(ydWrkProgStat) || "5".equals(ydWrkProgStat)) && "5".equals(ydL2RequestStat)) {

				// 응답전문 N 일때(작업 불가메세지)
				if ("N".equals(sReqYn)) {
					commUtils.printLog(logId, mthdNm + "권하위치 변경 불가일 경우", "S-");
					return jrRtn;
				}

				String ydChgStkColGp = ydChgDnWoLoc.substring(0, 6);
				String ydChgStkBedNo = ydChgDnWoLoc.substring(6, 8);
				String ydBefStkColGp = "";
				String ydBefStkBedNo = "";

				if (ydBefDnWoLoc.length() == 8 && (!"XX010101".equals(ydBefDnWoLoc))) {
					ydBefStkColGp = ydBefDnWoLoc.substring(0, 6);
					ydBefStkBedNo = ydBefDnWoLoc.substring(6, 8);
				}

				/**********************************************************
				*  변경전 정보 수정
				*  기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다.
				**********************************************************/
				JDTORecord jrOldLoc = commUtils.getParam(logId, mthdNm, sModifier);

				if (ydBefDnWoLoc.length() == 8 && (!"XX010101".equals(ydBefDnWoLoc))) {
					String compSTL_NO  = "";
					jrOldLoc = commUtils.getParam(logId, mthdNm, sModifier);
					jrOldLoc.setField("YD_STK_COL_GP"   , ydBefStkColGp);
					jrOldLoc.setField("YD_STK_BED_NO"   , ydBefStkBedNo);
					jrOldLoc.setField("YD_STK_LYR_NO"   , ydBefDnWoLayer);
					/*
					SELECT *
					  FROM TB_YD_STKLYR
					 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
					   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
					   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
					*/
					JDTORecordSet jsBefStkLay = commDao.select(jrOldLoc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStkLyrByPk", logId, mthdNm, "기존 적재위치 조회");
					if (jsBefStkLay.size() == 0) {
						throw new Exception("저장위치 이상.! [" + ydCrnSchId + "]");
					} else {
						compSTL_NO = jsBefStkLay.getRecord(0).getFieldString("STL_NO");
						// 기존 변경 재료 와 수정할 재료가 동일한지 비교
						if (ydChgStlNo.equals(compSTL_NO)) {

							// 기존 권하지시위치 정보 Clear
							jrOldLoc.setField("STL_NO"              , "");
							jrOldLoc.setField("YD_STK_LYR_ACT_STAT"	, "E");
							jrOldLoc.setField("YD_STK_LYR_MTL_STAT" , "E");
							/*
							UPDATE TB_YD_STKLYR
							   SET MOD_DDTT     = SYSDATE
							     , MODIFIER     = :V_MODIFIER
							     , YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT, YD_STK_LYR_ACT_STAT)
							     , YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT, YD_STK_LYR_MTL_STAT)
							     , STL_NO              = :V_STL_NO
							 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
							   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
							   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
					    	 */
							commDao.update(jrOldLoc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkLyrYdStkColBedGp", logId, mthdNm, "기존위치 Clear");
						}
					}
				}

				/**********************************************************
				* 3. 신규 위치 SET
				**********************************************************/
				JDTORecord jrNewLoc = commUtils.getParam(logId, mthdNm, sModifier);

				// 신규 저장위치 CHECK
				jrNewLoc.setField("YD_STK_COL_GP" 	, ydChgStkColGp);
				jrNewLoc.setField("YD_STK_BED_NO" 	, ydChgStkBedNo);
				jrNewLoc.setField("YD_STK_LYR_NO" 	, ydChgDnWoLayer);
				/*
				SELECT *
				  FROM TB_YD_STKLYR
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
				   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
				*/
				JDTORecordSet jsChgStkLay = commDao.select(jrNewLoc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStkLyrByPk", logId, mthdNm, "신규 적재위치 조회");

				if (jsChgStkLay.size() == 0) {
					throw new Exception("저장위치 이상.! [" + ydCrnSchId + "]");
				}

				// 신규위치에 정보를 Setting
				jrNewLoc.setField("YD_STK_COL_GP"      , ydChgStkColGp );
				jrNewLoc.setField("YD_STK_BED_NO"      , ydChgStkBedNo );
				jrNewLoc.setField("YD_STK_LYR_NO"      , ydChgDnWoLayer);
				jrNewLoc.setField("STL_NO"             , ydChgStlNo    );
				jrNewLoc.setField("YD_STK_LYR_ACT_STAT", "E"           );
				jrNewLoc.setField("YD_STK_LYR_MTL_STAT", "D"           );
				/*
				UPDATE TB_YD_STKLYR
				   SET MOD_DDTT     = SYSDATE
				     , MODIFIER     = :V_MODIFIER
				     , YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT, YD_STK_LYR_ACT_STAT)
				     , YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT, YD_STK_LYR_MTL_STAT)
				     , STL_NO              = :V_STL_NO
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
				   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
		    	 */
				commDao.update(jrNewLoc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkLyrYdStkColBedGp", logId, mthdNm, "신규위치 등록");

				// 신규 좌표 정보 READ
				jrNewLoc.setField("YD_CRN_SCH_ID"	        , ydCrnSchId);
				jrNewLoc.setField("YD_DN_WO_LOC"	     	, ydChgDnWoLoc);
				jrNewLoc.setField("YD_DN_WO_LAYER"	        , ydChgDnWoLayer);


				if ("".equals(ydUpWrLoc)) {
					jrNewLoc.setField("YD_WRK_PROG_STAT"	, "1");
				} else {
					jrNewLoc.setField("YD_WRK_PROG_STAT"	, "2");
				}
				/*
				MERGE INTO TB_YD_CRNSCH CS USING (
				SELECT SL.YD_STK_LYR_XAXIS      AS YD_DN_WO_LOC_XAXIS
				     , SB.YD_STK_BED_XAXIS_TOL  AS YD_DN_WO_XAXIS_GAP_MAX
				     , SB.YD_STK_BED_XAXIS_TOL  AS YD_DN_WO_XAXIS_GAP_MIN
				     , SL.YD_STK_LYR_YAXIS      AS YD_DN_WO_LOC_YAXIS
				     , SB.YD_STK_BED_YAXIS_TOL  AS YD_DN_WO_YAXIS_GAP_MAX
				     , SB.YD_STK_BED_YAXIS_TOL  AS YD_DN_WO_YAXIS_GAP_MIN
				     , SL.YD_STK_LYR_ZAXIS      AS YD_DN_WO_LOC_ZAXIS
				     , SB.YD_STK_BED_ZAXIS_TOL  AS YD_DN_WO_ZAXIS_GAP_MAX
				     , SB.YD_STK_BED_ZAXIS_TOL  AS YD_DN_WO_ZAXIS_GAP_MIN
				     , SC.ROTATION_ANGLE        AS DOWN_ROTATION_ANGLE
				     , :V_YD_CRN_SCH_ID         AS YD_CRN_SCH_ID
				  FROM TB_YD_STKCOL     SC
				     , TB_YD_STKBED     SB
				     , TB_YD_STKLYR     SL
				 WHERE SC.YD_STK_COL_GP = SB.YD_STK_COL_GP
				   AND SB.YD_STK_COL_GP = SL.YD_STK_COL_GP
				   AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO
				   AND SL.YD_STK_COL_GP = SUBSTR(:V_YD_DN_WO_LOC,1,6)
				   AND SL.YD_STK_BED_NO = SUBSTR(:V_YD_DN_WO_LOC,7,2)
				   AND SL.YD_STK_LYR_NO = :V_YD_DN_WO_LAYER

				) DD ON (CS.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID)

				WHEN MATCHED THEN UPDATE SET
				       MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				     , YD_DN_WO_LOC             = NVL(:V_YD_DN_WO_LOC           ,YD_DN_WO_LOC)
				     , YD_DN_WO_LAYER           = NVL(:V_YD_DN_WO_LAYER         ,YD_DN_WO_LAYER)
				     , YD_DN_WO_LOC_XAXIS       = NVL(DD.YD_DN_WO_LOC_XAXIS     ,YD_DN_WO_LOC_XAXIS)
				     , YD_DN_WO_XAXIS_GAP_MAX   = NVL(DD.YD_DN_WO_XAXIS_GAP_MAX ,YD_DN_WO_XAXIS_GAP_MAX)
				     , YD_DN_WO_XAXIS_GAP_MIN   = NVL(DD.YD_DN_WO_XAXIS_GAP_MIN ,YD_DN_WO_XAXIS_GAP_MIN)
				     , YD_DN_WO_LOC_YAXIS       = NVL(DD.YD_DN_WO_LOC_YAXIS     ,YD_DN_WO_LOC_YAXIS)
				     , YD_DN_WO_YAXIS_GAP_MAX   = NVL(DD.YD_DN_WO_YAXIS_GAP_MAX ,YD_DN_WO_YAXIS_GAP_MAX)
				     , YD_DN_WO_YAXIS_GAP_MIN   = NVL(DD.YD_DN_WO_YAXIS_GAP_MIN ,YD_DN_WO_YAXIS_GAP_MIN)
				     , YD_DN_WO_LOC_ZAXIS       = NVL(DD.YD_DN_WO_LOC_ZAXIS     ,YD_DN_WO_LOC_ZAXIS)
				     , YD_DN_WO_ZAXIS_GAP_MAX   = NVL(DD.YD_DN_WO_ZAXIS_GAP_MAX ,YD_DN_WO_ZAXIS_GAP_MAX)
				     , YD_DN_WO_ZAXIS_GAP_MIN   = NVL(DD.YD_DN_WO_ZAXIS_GAP_MIN ,YD_DN_WO_ZAXIS_GAP_MIN)
				     , YD_L2_REQUEST_STAT       = :V_YD_L2_REQUEST_STAT
				     , YD_DN_WO_LOC_TO          = :V_YD_DN_WO_LOC_TO
				     , YD_WRK_PROG_STAT         = :V_YD_WRK_PROG_STAT
				     , YD_WORD_DT               = SYSDATE
				     , DOWN_ROTATION_ANGLE      = NVL(DD.DOWN_ROTATION_ANGLE    ,DOWN_ROTATION_ANGLE)
				 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				 */
				commDao.update(jrNewLoc, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnWrk", logId, mthdNm, "TB_YD_CRNSCH 등록");
				
				/* 20.12.17 추가 *************************/
		    	JDTORecord jrParam2 = commUtils.getParam(logId, mthdNm, sModifier);
		    	jrParam2.setField("YD_EQP_ID", ydEqpId);
		    	/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdEqp 
		    	SELECT *
		    	  FROM TB_YD_EQP
		    	 WHERE YD_EQP_ID = :V_YD_EQP_ID
		    	   AND DEL_YN = 'N'
		    	*/
		    	JDTORecordSet jsYdEqpInfo = commDao.select(jrParam2, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdEqp", logId, mthdNm, "크레인정보 조회");
		    	
		    	String ydLocGp = "";
		    	if( jsYdEqpInfo.size() > 0 ) {
		    		ydLocGp = commUtils.trim(jsYdEqpInfo.getRecord(0).getFieldString("YD_LOC_GP"));
		    	}
				/****************************************/
		    	
		    	if( !"H".equals(ydLocGp) ) {
					//권하위치 변경후 작업지시정보를  재전송 (Manual 작업지시보내는 방법으로  선택 인경우만 전송한다)
					//권상지시 or 권상완료
		    		
		    		if ( "1".equals(ydWrkProgStst) || "2".equals(ydWrkProgStst)) { //권상지시 or 권상완료
		    			JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
		    			jrYdMsg.setField("JMS_TC_CD"         	, "Y5YDL007"); 					//JMSTC코드
		    			jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); 	//JMSTC생성일시
		    			jrYdMsg.setField("YD_EQP_ID"        	, ydEqpId);  					//야드설비ID
		    			jrYdMsg.setField("YD_WRK_PROG_STAT"     , ydWrkProgStst); 				//야드작업진행상태
		    			jrYdMsg.setField("YD_SCH_CD"        	, ydSchCd);						//야드스케쥴코드
		    			jrYdMsg.setField("YD_CRN_SCH_ID"        , ydCrnSchId); 					//야드크레인스케쥴ID
	
		    			//크레인작업지시요구 전문을 추가
						jrRtn = commUtils.addSndData(jrRtn, this.rcvY5YDL007(jrYdMsg));
					}
		    	}

	    		//------------------------------------------------------------------------
	    		// 본래 권하위치가 차량 또는 대차에서 일반야드로 권하위치를 변경하는경우
	    		//------------------------------------------------------------------------
	    		String szChgEqpGp    = ydChgDnWoLoc.substring(2,4);
	    		String szBefEqpGp    = "";
	    		// 기존 권하위치
	    		if (ydBefDnWoLoc.length() >= 6) {
	    			szBefEqpGp = ydBefDnWoLoc.substring(2,4);
	    		}

	    		// 기존 설비구분이 차량/대차이고 , 신규 설비 구분이 그 차량/대차 작업이 아닌경우
	    		// 작업예약 ID를 Clear  한다.
	    		if ("TC".equals(szBefEqpGp) || "PT".equals(szBefEqpGp) || "TR".equals(szBefEqpGp)) {
	    			if (!szChgEqpGp.equals(szBefEqpGp)) {

	    				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );

	    				String szULGp  = ydSchCd.substring(6,7);  //상차구분
	    				String szCarGp = ydSchCd.substring(2,4);

	    				//스케줄 코드가 차량/대차 인경우 구분
	    				if ("TC".equals(szCarGp)) {
	    	 				if ("U".equals(szULGp)) {

	    	 					/*
	    	 					--대차스케줄 작업예약ID 삭제
								UPDATE TB_YD_TCARSCH
								   SET MODIFIER              = :V_MODIFIER
								     , MOD_DDTT              = SYSDATE
								     , YD_CARLD_WRK_BOOK_ID  = NULL
								 WHERE DEL_YN                = 'N'
								   AND YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
	    	    				*/
	    	    				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updTcarSchWbDelLd", logId, mthdNm, "TB_YD_TCARSCH");

	    					} else if ("L".equals(szULGp)) {
	    						//하차인경우 작업예약 정보 삭제
	    						/*
								UPDATE TB_YD_TCARSCH
								   SET MODIFIER              = :V_MODIFIER
								     , MOD_DDTT              = SYSDATE
								     , YD_CARUD_WRK_BOOK_ID  = NULL
								 WHERE DEL_YN                = 'N'
								   AND YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
	    	    				*/
	    	    				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updTcarSchWbDelUd", logId, mthdNm, "TB_YD_TCARSCH");

	    					}

	    				} else if ("PT".equals(szCarGp) || "TR".equals(szCarGp)) {
	    					// 차량인 경우
	    					if ("U".equals(szULGp)) {

	    						/*
								UPDATE TB_YD_CARSCH
								   SET MODIFIER              = :V_MODIFIER
								     , MOD_DDTT              = SYSDATE
								     , YD_CARLD_WRK_BOOK_ID  = NULL
								 WHERE DEL_YN                = 'N'
								   AND YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
	    						 */
	    						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCarSchWbDelLd", logId, mthdNm, "TB_YD_CARSCH");

	    					} else if ("L".equals(szULGp)) {
	    						//하차인경우 작업예약 정보 삭제
	    						/*
								UPDATE TB_YD_CARSCH
								   SET MODIFIER              = :V_MODIFIER
								     , MOD_DDTT              = SYSDATE
								     , YD_CARUD_WRK_BOOK_ID  = NULL
								 WHERE DEL_YN                = 'N'
								   AND YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
	    						*/
	    						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCarSchWbDelUd", logId, mthdNm, "TB_YD_CARSCH");
	    					}
	    				}
	    			}
	    		}
			} else {
				
				String sGenLineOff = "N";  //일반입고 여부
				JDTORecord jrParamSet = commUtils.getParam(logId, mthdNm, sModifier);
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getLineOffStartUpYn  
				SELECT YD_SCH_CD
				  FROM TB_YD_SCHRULE
				 WHERE YD_SCH_CD = :V_YD_SCH_CD
				   AND DEL_YN = 'N'
				   AND YD_SCH_CD IN ('JHKD01LM','JGFD01LM','JEKD01LM','JCKD01LM','JBKD01LM')
				*/
				jrParamSet.setField("YD_SCH_CD" 	    , ydSchCd);
				jrParamSet.setField("YD_CRN_SCH_ID" 	, ydCrnSchId);
				JDTORecordSet jsCrnSchChk = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getLineOffStartUpYn", logId, mthdNm, "일반작업 여부");
    	    	
				if (jsCrnSchChk.size() > 0) {
					sGenLineOff = "Y";
				}
				/**********************************************************
				* 2. 권하위치 변경이 아닌 경우
				**********************************************************/
				if ("Y".equals(sReqYn)) {
					
					commUtils.printLog(logId, "무인 응답 가능 : MSG_GP["+ sMsgGp +"] REQ_YN["+ sReqYn +"] YD_WRK_PROG_STAT["+ ydWrkProgStat +"]", "SL");
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnSchProgStatMsg
					UPDATE TB_YD_CRNSCH  A
					   SET MODIFIER            = :V_MODIFIER
					     , MOD_DDTT            = SYSDATE
					     , YD_WRK_PROG_STAT    = CASE WHEN YD_WRK_PROG_STAT IN ('S', '1') THEN :V_YD_WRK_PROG_STAT
					                                  ELSE YD_WRK_PROG_STAT
					                             END
					     , YD_WORD_DT          = CASE WHEN :V_YD_WRK_PROG_STAT = 'W'         THEN NULL
					                                  WHEN :V_YD_WRK_PROG_STAT IN ('S', '1') THEN SYSDATE
					                                  ELSE YD_WORD_DT
					                             END
					 WHERE YD_CRN_SCH_ID       = :V_YD_CRN_SCH_ID
					   AND DEL_YN              = 'N'
					*/
					jrParam.setField("YD_WORD_DT", "1");
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnSchProgStatMsg", logId, mthdNm,  "TB_YD_CRNSCH");

					/********************
					 * 스케줄 취소 
					 ********************/
					if ("Y".equals(sReqYn) && "D".equals(sMsgGp)) {
						String sCrnSchYdEqpId = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_EQP_ID"));
						
						commUtils.printLog(logId, "전문수신 설비코드["+ ydEqpId +"] 크레인스케쥴 설비코드["+ sCrnSchYdEqpId +"]", "SL");
						
						if( ydEqpId.equals(sCrnSchYdEqpId) ) {
						
							commUtils.printLog(logId, "스케쥴취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");
	
							jrParam.setField("YD_WBOOK_ID"  	, ydWbookId );
							jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
							jrParam.setField("YD_EQP_ID"    	, ydEqpId   );
							jrParam.setField("YD_SCH_CD"    	, ydSchCd   );
							jrParam.setField("YD_L2_RETURN_FLAG", "Y"       );
							jrParam.setField("IS_LAST_SELECTED"	, "1");
							jrParam.setField("IS_SCH_MTL"    	, "Y"); // 스케줄 단위 취소
	
							/**********************************************************
							* 1. 크레인스케줄 취소
							**********************************************************/
							EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
							JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
	
							jrRtn = commUtils.addSndData(jrRtn, jrRst);
	
							commUtils.printLog(logId, "ydL2RequestStat [ " + ydL2RequestStat + " - 작업취소  ]", "SL");
	
							if ("X".equals(ydL2RequestStat)) {   // 화면에서 작업 취소 임 : 작업예약 삭제
								/**********************************************************
								* 작업예약 취소
								**********************************************************/
								EJBConnector ejbConn1 = new EJBConnector("default", "CCoilJspSeEJB", this);
								jrRst = (JDTORecord)ejbConn1.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
	
								jrRtn = commUtils.addSndData(jrRtn, jrRst);
							}
						}
					}

					/******************************************************************
					 * 입고대차 작업 응답 Y 일 때 다른 입고 작업 권하번지 00으로 변경 
					 ****************************************************************/
					if ("TC".equals(ydSchCd.substring(2, 4)) && "MM".equals(ydSchCd.substring(6, 8))) { //동간입고 스케줄
					
						/*
						UPDATE TB_YD_CRNSCH
						   SET MODIFIER       = :V_MODIFIER
						     , MOD_DDTT       = SYSDATE
						     , YD_DN_WO_LOC   = SUBSTR(YD_DN_WO_LOC, 0, 6)||'00'
						     , YD_DN_WO_LAYER = '001'
						 WHERE YD_GP     = 'J'
						   AND DEL_YN    = 'N'
						   AND YD_SCH_CD = (SELECT YD_SCH_CD FROM TB_YD_CRNSCH WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
						   AND YD_CRN_SCH_ID != :V_YD_CRN_SCH_ID
						   AND SUBSTR(YD_DN_WO_LOC, 7, 2) != '00' 
						   AND SUBSTR(YD_DN_WO_LOC, 3, 2)  = 'TC' 
						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updTcMmDnLocBed00", logId, mthdNm, "동간입고 권하위치번지 00초기화");
						
						//권하지시 위치가 일반야드일 경우 야드맵 정리
						/*
						UPDATE TB_YD_STKLYR
						   SET MODIFIER            = :V_MODIFIER
						     , MOD_DDTT            = SYSDATE
						     , STL_NO              = '' 
						     , YD_STK_LYR_MTL_STAT = 'E'
						 WHERE 1 = 1
						   AND YD_STK_LYR_MTL_STAT = 'D'
						   AND (YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO) NOT IN (
						                                                            SELECT SUBSTR(YD_DN_WO_LOC, 1, 6) AS YD_STK_COL_GP
						                                                                 , SUBSTR(YD_DN_WO_LOC, 7, 2) AS YD_STK_BED_NO
						                                                                 , YD_DN_WO_LAYER             AS YD_STK_LYR_NO
						                                                              FROM TB_YD_CRNSCH
						                                                             WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						                                                            )
						   AND STL_NO              = (SELECT STL_NO
						                                FROM TB_YD_CRNWRKMTL
						                               WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						                             )
						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updDnWoLocClear", logId, mthdNm, "권하위치 초기화");
					}
					
					/******************************************************************
					 * 일반재 입고 line off 시 처리  
					 ****************************************************************/
					// 일반입고 'XX' 처리
					if( "Y".equals(sGenLineOff) ) {
						/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnSchClear 
						UPDATE TB_YD_CRNSCH
						   SET MODIFIER = :V_MODIFIER
						     , MOD_DDTT = SYSDATE
						     , YD_DN_WO_LOC   = 'XX010101'
						     , YD_DN_WO_LAYER = ''
						 WHERE DEL_YN  = 'N'
						   AND YD_CRN_SCH_ID != :V_YD_CRN_SCH_ID 
						   AND YD_SCH_CD      = :V_YD_SCH_CD
						   AND YD_DN_WO_LOC  != 'XX010101'
						   AND YD_DN_WO_LOC  IS NOT NULL       
						 */
						jrParamSet.setField("YD_SCH_CD" 	    , ydSchCd);
						jrParamSet.setField("YD_CRN_SCH_ID" 	, ydCrnSchId);
						commDao.update(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnSchClear", logId, mthdNm, "일반입고 권하위치번지 'XX'초기화");
						
						/* 이전 스케줄이 동간입고일 경우 권하위치 초기화 
						UPDATE TB_YD_CRNSCH
						   SET MODIFIER       = :V_MODIFIER
						     , MOD_DDTT       = SYSDATE
						     , YD_DN_WO_LOC   = SUBSTR(YD_DN_WO_LOC, 0, 6)||'00'
						     , YD_DN_WO_LAYER = '001'
						 WHERE YD_GP     = 'J'
						   AND DEL_YN    = 'N'
						   AND YD_SCH_CD = (SELECT YD_SCH_CD FROM TB_YD_CRNSCH WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
						   AND SUBSTR(YD_DN_WO_LOC, 7, 2) != '00' 
						   AND YD_SCH_CD LIKE 'J_TC__MM'
						 */
						jrParamSet.setField("YD_CRN_SCH_ID" 	, ydCrnSchIdOld);
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updTcMmDnLocBed002", logId, mthdNm, "이전동간입고 권하위치번지 00초기화");
					}
				 } else if ("N".equals(sReqYn)) {
					commUtils.printLog(logId, "무인 응답 불가 : REQ_YN["+ sReqYn +"] YD_WRK_PROG_STAT["+ ydWrkProgStat +"]", "SL");

					if ("E001".equals(sErrCd)) {

						//작업지시가 내려와 이미 코일을 집었는데 다음작업지시가 내려온 경우
						//다음작업지시를 수행할 수 없는 상황 (차상국은 이전 작업지시를 그대로 실행)
						//L3 는 'N' 응답에 'E001' 메세지 받은 크레인 스케줄 ID의 작업진행상태를  'W'로 변경한다.
						commUtils.printLog(logId, mthdNm + "E001 불가 일경우", "SL");
						
						/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnSchProgStatMsg
						UPDATE TB_YD_CRNSCH  A
						   SET MODIFIER            = :V_MODIFIER
						     , MOD_DDTT            = SYSDATE
						     , YD_WRK_PROG_STAT    = CASE WHEN YD_WRK_PROG_STAT IN ('S', '1') THEN :V_YD_WRK_PROG_STAT
						                                  ELSE YD_WRK_PROG_STAT
						                             END
						     , YD_WORD_DT          = CASE WHEN :V_YD_WRK_PROG_STAT = 'W'         THEN NULL
						                                  WHEN :V_YD_WRK_PROG_STAT IN ('S', '1') THEN SYSDATE
						                                  ELSE YD_WORD_DT
						                             END
						 WHERE YD_CRN_SCH_ID       = :V_YD_CRN_SCH_ID
						   AND DEL_YN              = 'N'
						*/
						jrParam.setField("YD_WRK_PROG_STAT"	, "W");
						jrParam.setField("YD_CRN_SCH_ID"   	, ydCrnSchId);
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnSchProgStatMsg", logId, mthdNm,  "취소된 스케줄 작업진행상태 변경");
						
						/**********************************************************
						 * 권하위치 대차일때 권하번지 '00', TB_YD_STKLYR 초기화
						***********************************************************/
						// 권하위치 대차, 00번지가 아닐때
						if( "TC".equals(ydBefDnWoLoc.substring(2, 4)) && !"00".equals(ydBefDnWoLoc.substring(6, 8)) ) {

							// 이전 크레인스케쥴 권하위치 대차 00 번지로 변경
							/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLoc
							UPDATE TB_YD_CRNSCH
							   SET MODIFIER = :V_MODIFIER
							      ,MOD_DDTT = SYSDATE
							      ,YD_DN_WO_LOC             = NVL(:V_YD_DN_WO_LOC           ,YD_DN_WO_LOC)
							      ,YD_DN_WO_LAYER           = NVL(:V_YD_DN_WO_LAYER         ,YD_DN_WO_LAYER)
							      ...
							 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
							*/
						    jrParam.setField("YD_DN_WO_LOC"		, ydBefDnWoLoc.substring(0, 6) + "00");
						    jrParam.setField("YD_DN_WO_LAYER"	, "001");
							commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLoc", logId, mthdNm,  "취소된 스케쥴 대차권하위치 초기화");

							/**************************************************
							 * - 취소된 작업 대차권하위치이면 TB_YD_STKLYR 원복
							 **************************************************/

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
						    jrParam.setField("YD_STK_COL_GP"		, ydBefDnWoLoc.substring(0, 6));
						    jrParam.setField("YD_STK_BED_NO"		, ydBefDnWoLoc.substring(6, 8));
						    jrParam.setField("YD_STK_LYR_NO"		, "001");
						    jrParam.setField("STL_NO"				, "");
						    jrParam.setField("YD_STK_LYR_MTL_STAT"	, "E");
							commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyr", logId, mthdNm, "취소된 스케쥴 대차권하위치 초기화");
						}
						/**************************************************
						 * 일반입고 작업시작
						 **************************************************/
						if( "Y".equals(sGenLineOff) ) {
							
							// 이전 크레인스케쥴 권하위치 XXXX 번지로 변경
							/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLocDn 
							UPDATE TB_YD_CRNSCH C
							   SET MODIFIER = :V_MODIFIER
							      ,MOD_DDTT = SYSDATE
							      ,YD_DN_WO_LOC             = :V_YD_DN_WO_LOC  
							      ,YD_DN_WO_LAYER           = :V_YD_DN_WO_LAYER
							      ,YD_CRN_GRAB_USE_RULE_ID  = ( SELECT B.YD_CAR_SCH_ID
							                                      FROM TB_YD_WRKBOOK A
							                                         , TB_YD_CARSCH B
							                                     WHERE B.DEL_YN='N'
							                                       AND (B.CAR_NO=A.CAR_NO OR B.TRN_EQP_CD = A.TRN_EQP_CD )
							                                       AND A.YD_WBOOK_ID =  (SELECT B1.YD_WBOOK_ID 
							                                                               FROM TB_YD_CRNSCH B1
							                                                              WHERE B1.YD_CRN_SCH_ID = C.YD_CRN_SCH_ID )
							                                       AND ROWNUM <= 1)
							 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
							*/
							jrParamSet.setField("YD_CRN_SCH_ID" 	, ydCrnSchId);
							jrParamSet.setField("YD_DN_WO_LOC"		, "XX010101");
							jrParamSet.setField("YD_DN_WO_LAYER"	, "");
							commDao.update(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdCrnWrkLocDn", logId, mthdNm,  "취소된 스케쥴 일반입고 권하위치 초기화");

							/**************************************************
							 * - 취소된 작업 일반입고 권하위치이면 TB_YD_STKLYR 원복
							 **************************************************/

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
							jrParamSet.setField("YD_STK_COL_GP"			, ydBefDnWoLoc.substring(0, 6));
							jrParamSet.setField("YD_STK_BED_NO"			, ydBefDnWoLoc.substring(6, 8));
							jrParamSet.setField("YD_STK_LYR_NO"			, ydBefDnWoLayer);
							jrParamSet.setField("STL_NO"				, "");
							jrParamSet.setField("YD_STK_LYR_MTL_STAT"	, "E");
							commDao.update(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyr", logId, mthdNm, "취소된 스케쥴 일반입고 권하위치 초기화");					
							
						}
						if (!"".equals(ydCrnSchIdOld)) {

							//이전 스케줄ID의 상태를 '1'로 변경한다.
							/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnSchProgStatMsg
							UPDATE TB_YD_CRNSCH  A
							   SET MODIFIER            = :V_MODIFIER
							     , MOD_DDTT            = SYSDATE
							     , YD_WRK_PROG_STAT    = CASE WHEN YD_WRK_PROG_STAT IN ('S', '1') THEN :V_YD_WRK_PROG_STAT
							                                  ELSE YD_WRK_PROG_STAT
							                             END
							     , YD_WORD_DT          = CASE WHEN :V_YD_WRK_PROG_STAT = 'W'         THEN NULL
							                                  WHEN :V_YD_WRK_PROG_STAT IN ('S', '1') THEN SYSDATE
							                                  ELSE YD_WORD_DT
							                             END
							 WHERE YD_CRN_SCH_ID       = :V_YD_CRN_SCH_ID
							   AND DEL_YN              = 'N'
							*/
							jrParam.setField("YD_WRK_PROG_STAT"	, "1");
							jrParam.setField("YD_CRN_SCH_ID"   	, ydCrnSchIdOld);
							commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnSchProgStatMsg", logId, mthdNm,  "이전 크레인스케줄 작업진행상태  변경");
							
							/**********************************************************
							 * Line-Off 긴급작업 취소, 이전 Line-Off 작업 복구
							***********************************************************/
							/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCrnsch
							SELECT YD_EQP_ID                        AS YD_EQP_ID
							     , YD_CRN_SCH_ID                    AS YD_CRN_SCH_ID
							       ...
							     , YD_DN_WO_LOC                     AS YD_DN_WO_LOC
							     , YD_DN_WO_LAYER                   AS YD_DN_WO_LAYER
							       ...
							  FROM TB_YD_CRNSCH
							 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
							*/
							JDTORecordSet jsCrnWrkOld = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCrnsch", logId, mthdNm, "이전 크레인스케쥴 조회");
							if( jsCrnWrkOld.size() > 0 ) {
								JDTORecord jrCrnSchOld = jsCrnWrkOld.getRecord(0);

								// 종료 되지 않은 크레인 스케쥴
								if( "N".equals(jrCrnSchOld.getField("DEL_YN"))) {

									String ydDnWoLocOld = commUtils.trim(jsCrnWrkOld.getRecord(0).getFieldString("YD_DN_WO_LOC"));
									String ydDnWoLayerOld = commUtils.trim(jsCrnWrkOld.getRecord(0).getFieldString("YD_DN_WO_LAYER"));
									/**************************************************
									 * - 이전 작업 대차권하위치이면 TB_YD_STKLYR 원복
									 **************************************************/
									if( "TC".equals(ydDnWoLocOld.substring(2, 4)) && !"00".equals(ydDnWoLocOld.substring(6, 8))) {

										/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getStockIdByCrnSchId
										SELECT CM.STL_NO
										  FROM TB_YD_CRNSCH    CS
										     , TB_YD_CRNWRKMTL CM
										 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
										   AND CS.DEL_YN = 'N'
										   AND CM.DEL_YN = 'N'
										   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
										 ORDER BY CM.YD_STK_LYR_NO
										*/
										JDTORecordSet jsCrnWrkMtl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getStockIdByCrnSchId", logId, mthdNm, "이전 크레인스케쥴 코일번호 조회");

										if( jsCrnWrkMtl.size() > 0 ) {

											JDTORecord jrCrnWrkMtl = jsCrnWrkMtl.getRecord(0);
											String sStlNoOld = commUtils.trim(jrCrnWrkMtl.getFieldString("STL_NO"));

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
										    jrParam.setField("STL_NO"				, sStlNoOld);
										    jrParam.setField("YD_STK_LYR_MTL_STAT"	, "D");
											commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyr", logId, mthdNm, "작업중인 Line-Off 대차권하위치 초기화");
										}
									}
									/**************************************************
									 * - 이전 작업 일반입고 권하위치이면 TB_YD_STKLYR 원복
									 **************************************************/
									if( "Y".equals(sGenLineOff) ) {

										/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getStockIdByCrnSchId
										SELECT CM.STL_NO
										  FROM TB_YD_CRNSCH    CS
										     , TB_YD_CRNWRKMTL CM
										 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
										   AND CS.DEL_YN = 'N'
										   AND CM.DEL_YN = 'N'
										   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
										 ORDER BY CM.YD_STK_LYR_NO
										*/
										jrParamSet.setField("YD_CRN_SCH_ID"   	, ydCrnSchIdOld);
										JDTORecordSet jsCrnWrkMtl1 = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getStockIdByCrnSchId", logId, mthdNm, "이전 크레인스케쥴 코일번호 조회");

										if( jsCrnWrkMtl1.size() > 0 ) {

											JDTORecord jrCrnWrkMtl1 = jsCrnWrkMtl1.getRecord(0);
											String sStlNoOld = commUtils.trim(jrCrnWrkMtl1.getFieldString("STL_NO"));

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
											jrParamSet.setField("STL_NO"				, sStlNoOld);
											jrParamSet.setField("YD_STK_LYR_MTL_STAT"	, "D");
											commDao.update(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyr", logId, mthdNm, "작업중인 Line-Off 대차권하위치 초기화");
										}
									}									
								}
							}
						}
					}
				}
			}			

			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 스케줄작업요구(Y5YDL014)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL014(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "스케줄작업요구[CCoilL2RcvSeEJB.rcvY5YDL014] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = commUtils.getParam(logId, mthdNm, "Y5YDL014");	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값ydEqpId
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId     	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     //설비ID
//			String ydSchCd     	= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));     //SCHEDULE 코드
//			String ydCrnSchId  	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); //SCHEDULEID
			String ydSchFlag   	= commUtils.trim(rcvMsg.getFieldString("YD_SCH_FLAG"));   //요구스케쥴구분 
			
			String sModifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;

			String ydNewWbookId 	= "";
			String ydNewSchCd 		= "";
			String ydNewSchPrior	= "";
			String ydNewCrnSchId 	= "";

			JDTORecord jrParam	= commUtils.getParam(logId, mthdNm, sModifier);
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID 이상 [" + ydEqpId + "]");
			} else if (ydSchFlag.length() == 0) {
				throw new Exception("요구스케쥴구분 이상 [" + ydEqpId + "]");
			}
			
			
			/**********************************************************
			* 2. 작업대상 조회
			**********************************************************/
			jrParam.setField("YD_EQP_ID"     , ydEqpId);  					
			jrParam.setField("YD_SCH_FLAG"   , ydSchFlag);
			
			//A : 수입, B : 보급, C : 동내이적, D : 반입, E : HYSCO출하, F : 이송, K : 입측SCRAP추출, L : 출측SCRAP추출  					
			
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCrnSchDmd
			SELECT *
			  FROM (
			        SELECT
			               (CASE WHEN A.YD_SCH_CD    LIKE 'J_CV01LH'     THEN 'A'--수입
			                     WHEN (B.CD_CONTENTS LIKE '%보급%' OR B.CD_CONTENTS LIKE '%TakeIn%') AND (B.CD_CONTENTS NOT LIKE '%재작%' ) THEN 'B'--보급
			                     WHEN A.YD_SCH_CD    LIKE 'J_TC%'        THEN 'C'--대차
			                     WHEN B.CD_CONTENTS  LIKE '%반입%'        THEN 'D'--반입
			                     WHEN A.YD_SCH_CD    LIKE 'J_PT0_UH'     THEN 'F'--이송
			                     WHEN B.CD_CONTENTS  LIKE '%입측TakeOut%' THEN 'G'--입측추출
			                     WHEN B.CD_CONTENTS  LIKE '%출측TakeOut%' OR (B.CD_CONTENTS LIKE '%추출%' AND B.CD_CONTENTS NOT LIKE '%스크%') THEN 'H'--출측추출
			                     WHEN B.CD_CONTENTS  LIKE '%차공정%'      THEN 'I'--차공정
			                     WHEN B.CD_CONTENTS  LIKE '%차량이적%'     THEN 'J'--차량이적
			                     WHEN B.CD_CONTENTS  LIKE '%스크%'        THEN 'L' --출측SCRAP추출
			                     WHEN B.CD_CONTENTS  LIKE '%스크%'        THEN 'K' --입측SCRAP추출
			                     WHEN B.CD_CONTENTS  LIKE '%재작%'        THEN 'M'--재작보급
			                     WHEN B.CD_CONTENTS  LIKE '%동내이적%'    THEN 'T'--동내이적
			                     WHEN A.YD_SCH_CD    LIKE 'J_YD03MH'      THEN 'S'--공냉재이적
			                     WHEN A.YD_SCH_CD    LIKE 'J_PT2%'        THEN 'N'--공냉재이송
			                     WHEN A.YD_SCH_CD    LIKE 'J_YD_2MH'      THEN 'O' --자동이적
			                 END) AS YD_SCH_FLAG
			             , A.YD_SCH_CD
			             , '1'            AS YD_SCH_PRIOR
			             , YD_CRN_SCH_ID
			             , YD_WBOOK_ID
			             , CASE WHEN A.YD_SCH_CD LIKE 'J_CV01LH' THEN -A.YD_WBOOK_ID
			                    ELSE TO_NUMBER(A.YD_WBOOK_ID)
			                END AS YD_WBOOK_ID2
			          FROM TB_YD_CRNSCH  A
			             , TB_YD_SCHRULE B
			             , TB_YD_EQP     C
			         WHERE A.YD_SCH_CD  = B.YD_SCH_CD
			           AND B.YD_WRK_CRN = C.YD_EQP_ID
			           AND A.DEL_YN     = 'N'
			           AND A.YD_GP      = 'J'
			           AND (CASE C.YD_EQP_STAT WHEN 'B' THEN B.YD_ALT_CRN ELSE A.YD_EQP_ID END) = :V_YD_EQP_ID
			           AND 'Y' = CASE WHEN SUBSTR(A.YD_SCH_CD,8,1) = 'H' THEN 'Y'
			                          WHEN A.YD_SCH_CD LIKE 'J_PT2%'   THEN 'Y'--20220106 소재장 1통로 공냉재 이송 추가
			                          ELSE 'N'
			                     END
			       ) A
			 WHERE YD_SCH_FLAG = :V_YD_SCH_FLAG
			 ORDER BY YD_SCH_PRIOR
			          -- 스크랩추출 지시 순서
			        , CASE WHEN A.YD_SCH_CD IN ('JHKD05LH', 'JEKD05LH', 'JCKD05LH', 'JBKD05LH')
			                    THEN ROW_NUMBER() OVER(ORDER BY YD_CRN_SCH_ID DESC)
			               ELSE 0
			          END
			        , A.YD_WBOOK_ID
			*/ 
			
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCrnSchDmd", logId, mthdNm, "요구정보 조회");
			if (jsCrnSch.size() == 0) {
				commUtils.printLog(logId,  "요구에 해당하는 정보 없음[" + ydEqpId + "] 스케줄플래그 : ["+ydSchFlag+"]", "S-");
				
				/**********************************************
				 * 1HFL/4HFL 보급 크레인스케줄 기동
				 * 202302 YYS
				 **********************************************/
	            String sAPP009_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","009"); //공냉재 자동입고
				commUtils.printLog(logId, "스케줄작업요구시 보급 예약 크레인스케줄 기동여부 : " + sAPP009_YN, "SL");
				if ("Y".equals(sAPP009_YN)) {
					
					/*					 
					 SELECT AA.YD_SCH_CD
						     , AA.YD_WBOOK_ID
						     , AA.YD_EQP_ID
						  FROM (
						        SELECT A.YD_SCH_CD
						             , A.YD_WBOOK_ID
						             , (SELECT BB.YD_WRK_CRN FROM TB_YD_SCHRULE BB WHERE BB.YD_SCH_CD = A.YD_SCH_CD) AS YD_EQP_ID
						             , ROW_NUMBER() OVER (PARTITION BY YD_SCH_CD  ORDER BY A.YD_SCH_CD , A.YD_WBOOK_ID ) AS RANK_NO
						          FROM TB_YD_WRKBOOK A
						         WHERE A.DEL_YN    = 'N'
						           AND A.YD_GP     = 'J'
						           AND NOT EXISTS(SELECT 1 FROM USRYDA.TB_YD_CRNSCH B 
						                           WHERE B.DEL_YN = 'N'
						                             AND B.YD_SCH_CD   = A.YD_SCH_CD 
						                             AND B.YD_WBOOK_ID = A.YD_WBOOK_ID)
						           AND (A.YD_SCH_CD = 'JCFE01UH' OR A.YD_SCH_CD = 'JGFE01UH' OR A.YD_SCH_CD LIKE 'J_KE01UH' ) --#1HFL보급 JCFE01UH JGFE01UH
						         
						       ) AA
						 WHERE 1=1 --ROWNUM <= 1
						 AND AA.RANK_NO = 1 -- 예약재료 전부 기동시에는 주석처리
						 AND AA.YD_EQP_ID = :V_YD_EQP_ID 
					 */
					JDTORecordSet jsHFLRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdWrkbookHFL14Chk", logId, mthdNm, "보급예약(HFL/SPM) 조회");	
					
					//보급요청일때, 텔레스코프 교정기 보급/추출 포함
					if (jsHFLRst.size() > 0 && ("B".equals(ydSchFlag) || "U".equals(ydSchFlag))) {
						
	 
						commUtils.printLog(logId, "HFL/텔레스코프 보급 크레인 스케줄 기동", "SL");
						
						JDTORecord jrCrnSchMsg = commUtils.getParam(logId, mthdNm, sModifier);
						jrCrnSchMsg.setField("JMS_TC_CD"         , "YDYDJ551"); 
						jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
						jrCrnSchMsg.setField("YD_SCH_CD"         , jsHFLRst.getRecord(0).getFieldString("YD_SCH_CD"  )); 
						jrCrnSchMsg.setField("YD_WBOOK_ID"       , jsHFLRst.getRecord(0).getFieldString("YD_WBOOK_ID"));
						jrCrnSchMsg.setField("YD_EQP_ID"         , jsHFLRst.getRecord(0).getFieldString("YD_EQP_ID"  ));
						
						EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this);	    				  
    					jrCrnSchMsg = (JDTORecord)ejbConn.trx("procYDYDJ551", new Class[] { JDTORecord.class }, new Object[] { jrCrnSchMsg });
	    				   
						/**********************************************************
						* 5. 명령선택 기동
						**********************************************************/

						//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
						JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
						jrYdMsg.setField("JMS_TC_CD"            , "Y5YDL007");	//명령선택기동
						jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); 	//JMSTC생성일시
						jrYdMsg.setField("YD_EQP_ID"        	, ydEqpId);  					//야드설비ID
//						jrYdMsg.setField("YD_WRK_PROG_STAT"     , ydWrkProgStst); 				//야드작업진행상태
						jrYdMsg.setField("YD_SCH_CD"        	, jsHFLRst.getRecord(0).getFieldString("YD_SCH_CD"  ));					//야드스케쥴코드
//						jrYdMsg.setField("YD_CRN_SCH_ID"        , ydNewCrnSchId); 				//야드크레인스케쥴ID
						
						jrRtn = commUtils.addSndData(jrRtn, this.rcvY5YDL007(jrYdMsg));
						
						return jrRtn;
						 
						
					} else {
						commUtils.printLog(logId, "HFL/텔레스코프 보급 작업예약 없음", "SL");
						
						// 텔레스코프 추출 크레인 스케줄 기동
						if("P".equals(ydSchFlag)){
							commUtils.printLog(logId, "텔레스코프 추출 크레인 스케줄 기동", "SL");
							
							JDTORecordSet jsTELRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdTelesOutStlNoChk", logId, mthdNm, "텔레스코프 추출대상 조회");	
							
							if (jsTELRst.size() > 0 ){
								
								String sStlNo 	= jsTELRst.getRecord(0).getFieldString("COIL_NO");
								
								JDTORecord jrCrnSchMsg = commUtils.getParam(logId, mthdNm, sModifier);
								jrCrnSchMsg.setField("JMS_TC_CD"         	, "HRYDJ009"); 
								jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
								jrCrnSchMsg.setField("TREAT_GP"         	, "3"); //처리구분 1:보급, 2:보급취소, 3:추출, 4:Take-Out, 5:Take-In
								jrCrnSchMsg.setField("STL_NO"         		, sStlNo);	//재료번호
								jrCrnSchMsg.setField("EQP_GP"         		, "T1-02");	//설비구분 - 추출(JFTD0101)
								jrCrnSchMsg.setField("YD_EQP_ID"         	, ydEqpId );
						 
								EJBConnector ejbConn = new EJBConnector("default", "CCoilL3RcvSeEJB", this);	    				  
		    					jrCrnSchMsg = (JDTORecord)ejbConn.trx("rcvHRYDJ009", new Class[] { JDTORecord.class }, new Object[] { jrCrnSchMsg });
		    				   
		    					
		    					ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this);	    				  
		    					jrCrnSchMsg = (JDTORecord)ejbConn.trx("procYDYDJ551", new Class[] { JDTORecord.class }, new Object[] { jrCrnSchMsg });
			    				
		    					
		    					//jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
							}
							
							/**********************************************************
							* 5. 명령선택 기동
							**********************************************************/
	
							//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
							JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
							jrYdMsg.setField("JMS_TC_CD"            , "Y5YDL007");	//명령선택기동
							jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); 	//JMSTC생성일시
							jrYdMsg.setField("YD_EQP_ID"        	, ydEqpId);  					//야드설비ID 
							jrYdMsg.setField("YD_SCH_CD"        	, "JFTD01LH");					//야드스케쥴코드 
							
							jrRtn = commUtils.addSndData(jrRtn, this.rcvY5YDL007(jrYdMsg));
							
							return jrRtn;
						}
					}
				}
				
			} else {
				// 요구스케쥴구분
				ydNewCrnSchId 	= jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID");  // 신규 작업
				ydNewWbookId 	= jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID"  );  // 신규 작업
				ydNewSchCd 		= jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD"    );  // 신규 작업
				ydNewSchPrior	= jsCrnSch.getRecord(0).getFieldString("YD_SCH_PRIOR" );  // 신규 작업
			}


			jrParam.setField("YD_SCH_CD"     , ydNewSchCd);	
			jrParam.setField("YD_WBOOK_ID"   , ydNewWbookId);	
			jrParam.setField("YD_SCH_PRIOR"  , ydNewSchPrior);	
			jrParam.setField("YD_CRN_SCH_ID" , ydNewCrnSchId);
			
			/*   
			SELECT YD_CRN_SCH_ID
			  FROM (
			        SELECT YD_CRN_SCH_ID
			             , COUNT(*)  AS CRN_WRK_CNT
			          FROM TB_YD_CRNSCH
			         WHERE YD_EQP_ID = :V_YD_EQP_ID
			           AND YD_WRK_PROG_STAT IN ('1', 'S')
			           AND DEL_YN = 'N'   
			         GROUP BY YD_CRN_SCH_ID  
			        )
			 WHERE CRN_WRK_CNT = 1            
			 */
			JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCrnWrkMgtPriorWrk1", logId, mthdNm, "기존크레인 작업 조회");
			if (jsCrn == null || jsCrn.size() <= 0) {
				/**********************************************************
				* 3.1 기존 작업이 없음  신규 작업만 하면 됨  
				**********************************************************/
			
			} else {
				
				// 기존 작업 
			    String ydCrnSchIdWrk = commUtils.trim(jsCrn.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
			    
				/**********************************************************
				* 3.2 기존 작업 정리 - 대기상태로
				**********************************************************/
			    /* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnWrkMgtPriorWrkNext1
			    --기존스케즐 초기화
			    UPDATE TB_YD_CRNSCH A
			       SET MODIFIER         = :V_MODIFIER
			         , MOD_DDTT         = SYSDATE
			         , YD_WRK_PROG_STAT = 'W'
			         , YD_WORD_DT       = NULL
			         , YD_SCH_PRIOR     = DECODE(YD_SCH_PRIOR, '0', '0' -- 긴급작업의 우선순위 변경 안함
			                                                 , (SELECT YD_WRK_CRN_PRIOR
			                                                      FROM TB_YD_SCHRULE B
			                                                     WHERE B.YD_SCH_CD = A.YD_SCH_CD))
			     WHERE YD_WBOOK_ID IN(SELECT YD_WBOOK_ID
			                            FROM TB_YD_CRNSCH C
			                           WHERE C.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
			       AND DEL_YN = 'N'
			    */
			    jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchIdWrk);
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnWrkMgtPriorWrkNext1", logId, mthdNm,  "TB_YD_CRNSCH");						    
				
			}
			
			/**********************************************************
			* 4. 신규작업 우선순위 변경
			**********************************************************/
			//신규 작업예약 Table 우선순위 Update
	    	/* 
			UPDATE TB_YD_WRKBOOK
			   SET MODIFIER     = :V_MODIFIER
			     , MOD_DDTT     = SYSDATE
			     , YD_SCH_PRIOR = NVL(TO_NUMBER(:V_YD_SCH_PRIOR),1)
			 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
			   AND DEL_YN       = 'N'
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updWrkBookPrior1", logId, mthdNm, "TB_YM_WRKBOOK");				

			//신규 작업 우선순위 변경
			/* 
			UPDATE TB_YD_CRNSCH
			   SET MODIFIER      = :V_MODIFIER
			     , MOD_DDTT      = SYSDATE
			     , YD_SCH_PRIOR  = NVL(TO_NUMBER(:V_YD_SCH_PRIOR),1)
			     , YD_EQP_ID     = NVL(:V_YD_EQP_ID, YD_EQP_ID)
			 WHERE YD_WBOOK_ID   = :V_YD_WBOOK_ID
			   AND YD_WRK_PROG_STAT IN ('1','W','S')
			   AND DEL_YN = 'N'  
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnWrkMgt1", logId, mthdNm,  "TB_YM_CRNSCH");
			
			/**********************************************************
			* 5. 명령선택 기동
			**********************************************************/

			//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
			jrYdMsg.setField("JMS_TC_CD"            , "Y5YDL007");	//명령선택기동
			jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); 	//JMSTC생성일시
			jrYdMsg.setField("YD_EQP_ID"        	, ydEqpId);  					//야드설비ID
//			jrYdMsg.setField("YD_WRK_PROG_STAT"     , ydWrkProgStst); 				//야드작업진행상태
			jrYdMsg.setField("YD_SCH_CD"        	, ydNewSchCd);					//야드스케쥴코드
//			jrYdMsg.setField("YD_CRN_SCH_ID"        , ydNewCrnSchId); 				//야드크레인스케쥴ID
			
			jrRtn = commUtils.addSndData(jrRtn, this.rcvY5YDL007(jrYdMsg));
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 차량작업예정정보요구(Y5YDL016)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL016(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "차량작업예정정보요구[CCoilL2RcvSeEJB.rcvY5YDL016] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = commUtils.getParam(logId, mthdNm, "Y5YDL016");	//전문 Return
		
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값ydEqpId
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydLoadLoc   	= commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"));     	//상차도 위치
			
			String sModifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;
			
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if (ydLoadLoc.length() < 6) {
				commUtils.printLog(logId, mthdNm + "상차도 길이 < 6 :" +ydLoadLoc , "SL");
				//throw new Exception("상차도 위치 [" + ydLoadLoc + "]");
			} 
			
			String sCarNo = "";
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("PT_LOAD_LOC"      , ydLoadLoc);
			
			/**********************************************************
			* 1. 차량번호 조회 
			**********************************************************/
			/*
			SELECT NVL(CAR_NO ,TRN_EQP_CD) AS CAR_NO
			  FROM TB_YD_CARPOINT
			 WHERE DEL_YN = 'N'
			   AND YD_STK_COL_GP = :V_PT_LOAD_LOC 
			 */
			JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdGetCarNoByLoc", logId, mthdNm, "차량번호 조회");

			if (jsRst.size() == 0) {
				commUtils.printLog(logId, "상차도 정보 없음 :" +ydLoadLoc , "SL");
			} else {	
			
				sCarNo = jsRst.getRecord(0).getFieldString("CAR_NO");
				if ("".equals(sCarNo)) {
					commUtils.printLog(logId, "해당위치 차량정보 없음  :" +ydLoadLoc , "SL");
				}
			} 
			
			/**********************************************************
			 * 차량작업 예정정보 송신 YDY5L008
			 **********************************************************/
	    	JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
	    	sndL2Msg.setField("JMS_TC_CD"       , "YDY5L008");
			sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
			sndL2Msg.setField("SEARCH_FLAG"     , "1"       ); //1:상차도, 2:차량스케쥴 ID
//			sndL2Msg.setField("YD_CAR_SCH_ID"   , ydCarSchId); //차량스케줄
			sndL2Msg.setField("PT_LOAD_LOC"     , ydLoadLoc);
				
			jrRtn = commUtils.addSndData(jrRtn, coilDao.procCarPlanInfo(sndL2Msg));	 //전송 Data 생성
			
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 상차도 작업불가(Y5YDL017)
	 *      - 소재 수입 1단적치 모드
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL017(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "상차도 작업불가[CCoilL2RcvSeEJB.rcvY5YDL017] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sPtLoadLoc   = commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC")); //상차도위치
			String sUseYn       = commUtils.trim(rcvMsg.getFieldString("USE_YN"     )); //상차도 사용유무 Y:가능, N:불가	

			String sModifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자(Backup Only)
			
			// 결속장 인터락 상태 수신 시
			if("JFFE02".equals(sPtLoadLoc) || "JDFE03".equals(sPtLoadLoc) || "JBFE05".equals(sPtLoadLoc) ){
				//=============================================================
				// Log 테이블 등록 (결속장 인터락 상태)
				//============================================================= 				
				commUtils.printLog(logId,  "[열연 코일야드L2] 결속장 인터락 상태 수신[" + sPtLoadLoc + "]", "SL");
				
				if (sPtLoadLoc.length() != 6) {
					throw new Exception("상차도 위치 PT_LOAD_LOC 가 6자리가 아닙니다!! [" + sPtLoadLoc + "]");
				}
				
				if (!"Y".equals(sUseYn) && !"N".equals(sUseYn)) {
					throw new Exception("상차도 사용유무 USE_YN 이 'Y','N' 이 아닌 값이 들어 왔습니다!! [" + sUseYn + "]");
				}
				JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_STK_COL_GP"    , sPtLoadLoc);
				jrParam.setField("MATL_SUP_MTD_GP"  , sUseYn);
		        
				/*
				UPDATE USRYDA.TB_YD_STKCOL
				   SET MATL_SUP_MTD_GP = :V_MATL_SUP_MTD_GP
				     , MOD_DDTT = SYSDATE
				     , MODIFIER = 'Y5YDL017'
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
				*/ 
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.Y5YDL017UpdYdConvLock", logId, mthdNm, "저장위치 MATL_SUP_MTD_GP UPDATE");		
				
				// CLOSE 인 경우 해당 스케쥴에 대한 작업 예약이 있는 경우 기동처리 함
				if ("N".equals(sUseYn)) { // 작업가능 N 작업불가 Y
					/* 
					SELECT YD_WBOOK_ID
					     , YD_SCH_PRIOR
					     , CRN_SCH_CNT
					     , RANK_CNT
					  FROM (
					        SELECT A.YD_WBOOK_ID
					             , A.YD_SCH_PRIOR
					             , A.YD_SCH_CD
					             , (SELECT COUNT(*) FROM TB_YD_CRNSCH WHERE DEL_YN = 'N' AND YD_GP = 'J' AND YD_SCH_CD = A.YD_SCH_CD ) AS CRN_SCH_CNT
					             ,  ROW_NUMBER() OVER(PARTITION BY A.YD_SCH_CD ORDER BY A.YD_SCH_CD, A.YD_SCH_PRIOR, A.YD_WBOOK_ID )   AS RANK_CNT
					          FROM TB_YD_WRKBOOK A
					         WHERE A.YD_GP  = 'J'
					           AND A.DEL_YN = 'N'
					           AND 'Y' = CASE WHEN :V_YD_STK_COL_GP = 'JFFE02' AND A.YD_SCH_CD IN ('JFFE01UH','JFFD01LM','JFYD04MM') THEN 'Y'
					                          WHEN :V_YD_STK_COL_GP = 'JDFE03' AND A.YD_SCH_CD IN ('JDFE01UH','JDFD01LM','JDYD04MM') THEN 'Y'
					                          WHEN :V_YD_STK_COL_GP = 'JBFE05' AND A.YD_SCH_CD IN ('JBFE01UH','JBFD01LM','JBYD04MM') THEN 'Y'
					                          ELSE 'N' END
					       )
					 WHERE CRN_SCH_CNT = 0
					   AND RANK_CNT = 1
					   AND 'Y' = CASE WHEN SUBSTR(YD_SCH_CD, 3, 6) = 'FE01UH'
					                   AND 1 > (SELECT COUNT(*)
					                              FROM TB_YD_STKLYR
					                             WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
					                               AND YD_STK_BED_NO      <> '00'
					                               AND YD_STK_LYR_ACT_STAT = 'E'
					                               AND YD_STK_LYR_MTL_STAT = 'E'
					                               AND DEL_YN              = 'N'
					                           ) THEN 'N'
					                  ELSE 'Y' END
					 */
					JDTORecordSet jsWbook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdWrkbookSchSta", logId, mthdNm, "작업예약 조회");
					
					if (jsWbook.size() <= 0) {
						return jrRtn;
					}
					
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					for (int i = 1; i <= jsWbook.size(); i++) {
						jsWbook.absolute(i);
						
						jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setField("JMS_TC_CD"		 , "YDYDJ551"); //
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
						jrYdMsg.setField("YD_WBOOK_ID"       , jsWbook.getRecord().getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"         , ""); //야드스케쥴코드
						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
					}
				}
			} else if( sPtLoadLoc.indexOf("CV01") > -1 ) {
				/***************************************************
				 * 소재 수입적치 1단 모드
				 ***************************************************/
				if (sPtLoadLoc.length() != 6) {
					throw new Exception("수입 위치 PT_LOAD_LOC 가 6자리가 아닙니다!! [" + sPtLoadLoc + "]");
				}
				
				if (!"Y".equals(sUseYn) && !"N".equals(sUseYn)) {
					throw new Exception("1단사용여부 사용유무 USE_YN 이 'Y','N' 이 아닌 값이 들어 왔습니다!! [" + sUseYn + "]");
				}
				
				String ydBayGp   = sPtLoadLoc.substring(1,2);
				JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("REPR_CD_GP"	, "APP020");
				jrParam.setField("CD_GP"		, "J");
				jrParam.setField("ITEM"			, ydBayGp);
				jrParam.setField("ITEM1"		, sUseYn);
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdRuleMgt
				UPDATE TB_YD_RULE
				   SET ITEM1       = :V_ITEM1
				     , ITEM_VALUE1 = :V_ITEM_VALUE1
				     , ITEM2       = :V_ITEM2
				     , ITEM_VALUE2 = :V_ITEM_VALUE2
				     , DTL_ITEM1   = :V_DTL_ITEM1
				     , DTL_ITEM2   = :V_DTL_ITEM2
				     , DTL_ITEM3   = :V_DTL_ITEM3
				     , DTL_ITEM4   = :V_DTL_ITEM4
				     , DTL_ITEM5   = :V_DTL_ITEM5
				     , DTL_ITEM6   = :V_DTL_ITEM6
				     , DTL_ITEM7   = :V_DTL_ITEM7
				     , DTL_ITEM8   = :V_DTL_ITEM8
				     , DTL_ITEM9   = :V_DTL_ITEM9
				     , DTL_ITEM10  = :V_DTL_ITEM10
				     , MOD_DDTT    = SYSDATE
				     , MODIFIER    = :V_MODIFIER
				 WHERE REPR_CD_GP  = :V_REPR_CD_GP
				   AND CD_GP       = :V_CD_GP
				   AND ITEM        = :V_ITEM
				*/
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdRuleMgt", logId, mthdNm, "수입 1단적치 모드 수정");
				
			} else {
				//=============================================================
				// Log 테이블 등록 ( 차량작업 예정정보 요구)
				//=============================================================
				
				if ("".equals(sModifier)) { sModifier = msgId; }
				mthdNm = msgId.substring(0, 2) + mthdNm;
	
				/**********************************************************
				* 0. 수신 항목 값 Check
				**********************************************************/
				if (sPtLoadLoc.length() != 6) {
					throw new Exception("상차도 위치 PT_LOAD_LOC 가 6자리가 아닙니다!! [" + sPtLoadLoc + "]");
				}
				if (!"PT".equals(sPtLoadLoc.substring(2,4))) {
					throw new Exception("상차도 위치 SECT_GP가 'PT'가 아닙니다!! [" + sPtLoadLoc + "]");
				}
				if (!"Y".equals(sUseYn) && !"N".equals(sUseYn)) {
					throw new Exception("상차도 사용유무 USE_YN 이 'Y','N' 이 아닌 값이 들어 왔습니다!! [" + sUseYn + "]");
				}
	
				JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	
				jrParam.setField("PT_LOAD_LOC", sPtLoadLoc);
				if ("Y".equals(sUseYn)) {
					jrParam.setField("YD_STK_COL_ACT_STAT", "L");//야드적치열활성상태 L:적치가능
				} else {
					jrParam.setField("YD_STK_COL_ACT_STAT", "N");//야드적치열활성상태 N:사용불가
				}
				
				/**********************************************************
				* 0. 차량포인트 적치열활성상태 수정
				**********************************************************/
				
				// 차량포인트 적치열활성상태 UPDATE
				/* 
				UPDATE  TB_YD_CARPOINT
				   SET  MODIFIER = :V_MODIFIER
				       ,MOD_DDTT = SYSDATE
				       ,YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
				 WHERE  YD_STK_COL_GP = :V_YD_STK_COL_GP    
				*/
				jrParam.setField("YD_STK_COL_GP"	, sPtLoadLoc); //상차도위치
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCarPointActStat", logId, mthdNm, "차량포인트 적치열활성상태 UPDATE");				
				
			}
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 차량동간이적도착실적(Y5YDL018)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL018(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "차량동간이적도착실적[CCoilL2RcvSeEJB.rcvY5YDL018] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = commUtils.getParam(logId, mthdNm, "Y5YDL018");	//전문 Return
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sPtLoadLoc   = commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC")); //상차도명 JAPT03
			String sCarNo       = commUtils.trim(rcvMsg.getFieldString("CAR_NO"));      //차량번호
			String sCarUpdnGp   = commUtils.trim(rcvMsg.getFieldString("CAR_UPDN_GP")); //작업구분 1: 상차, 2: 하차	
			String sModifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));    //수정자(Backup Only)
			String sBackUpYn    = commUtils.nvl (rcvMsg.getFieldString("BACKUP_YN"),"N"); //권하시 비형상일 경우 "Y"
			String ydCarSchId   = commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID")); //대기중인 차량이 들어온 경우
			
			// PIDEV_S :병행가동용:PI_YD
			String sPI_YD   = commUtils.trim(rcvMsg.getFieldString("PI_YD")); //대기중인 차량이 들어온 경우
			String sPI_YD1   = commUtils.trim(rcvMsg.getFieldString("PI_YD1")); //대기중인 차량이 들어온 경우
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;
			String sMsg         = "";
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			JDTORecordSet jsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");	
			JDTORecord    jrWbook = JDTORecordFactory.getInstance().create();
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sPtLoadLoc)) {
				sMsg = "상차도 위치 이상 [" + sPtLoadLoc + "]";
				jrRtn.setField("RTN_CD"         , "0");
				jrRtn.setField("RTN_MSG"        , sMsg);
				return jrRtn;
			} else if ("".equals(sCarNo)) {
				sMsg = "차량번호 이상 [" + sCarNo + "]";
				jrRtn.setField("RTN_CD"         , "0");
				jrRtn.setField("RTN_MSG"        , sMsg);
				return jrRtn;
			} else if ("".equals(sCarUpdnGp)) {
				sMsg = "차량상하차 구분 이상 [" + sCarUpdnGp + "]";
				jrRtn.setField("RTN_CD"         , "0");
				jrRtn.setField("RTN_MSG"        , sMsg);
				return jrRtn;
			}
			
			jrParam.setField("PT_LOAD_LOC", sPtLoadLoc);
			jrParam.setField("CAR_NO"     , sCarNo    );
			jrParam.setField("CAR_UPDN_GP", sCarUpdnGp);

			/**********************************************************
			* 1. 차량포인트 정보 조회
			**********************************************************/
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdGetCarNoTypeByLoc 
			SELECT A.*
			     -- 상차이거나 형상이 없으면 스케쥴 기동/ 하차이면 형상 수신후 기동  
			     , CASE WHEN YD_FRM_YN      = 'N' THEN 'Y' 
			            WHEN :V_CAR_UPDN_GP = '1' THEN 'Y' 
			            ELSE 'N' END SCH_STA_YN
			  FROM
			(
			SELECT A.YD_CARPNT_CD AS YD_CARPNT_CD
			     , A.CAR_NO  
			     , A.YD_CAR_USETYPE_GP 
			     , A.YD_STK_COL_ACT_STAT 
			     , A.WLOC_CD
			     , A.YD_PNT_CD
			     , (SELECT YD_LOC_GP 
			          FROM TB_YD_STKCOL 
			         WHERE YD_STK_COL_GP = A.YD_STK_COL_GP) AS YD_LOC_GP
			     , (SELECT YD_WBOOK_ID 
			          FROM TB_YD_WRKBOOK 
			         WHERE CAR_NO    =  :V_CAR_NO
			           AND DEL_YN    = 'N' 
			           AND YD_BAY_GP = SUBSTR(:V_PT_LOAD_LOC,2,1) 
			           AND DECODE(SUBSTR(YD_SCH_CD,7,1) , 'U', '1' , 'L', '2' ) = :V_CAR_UPDN_GP
			           AND YD_GP = 'J'
			           AND ROWNUM =1
			       ) AS CHK_WBOOK
			     , YD_FRM_YN
			  FROM TB_YD_CARPOINT A
			 WHERE DEL_YN = 'N'
			   AND YD_STK_COL_GP = :V_PT_LOAD_LOC 
			) A  
			 */
			JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdGetCarNoTypeByLoc", logId, mthdNm, "포인트 체크");
			
			if (jsRst.size() != 1) {
				sMsg = sPtLoadLoc+"개소코드 이상"; 
				commUtils.printLog(logId, sMsg, "S-");
				jrRtn.setField("RTN_CD"         , "0");
				jrRtn.setField("RTN_MSG"        , sMsg);
				return jrRtn;
			}
			
			String sCarNoGet       = commUtils.trim(jsRst.getRecord(0).getFieldString("CAR_NO"             ));
			String ydCarUsetypeGp  = commUtils.trim(jsRst.getRecord(0).getFieldString("YD_CAR_USETYPE_GP"  )); 
			String ydCarpntCd      = commUtils.trim(jsRst.getRecord(0).getFieldString("YD_CARPNT_CD"       ));
			String ydStkColActStat = commUtils.trim(jsRst.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));
			String sWlocCd         = commUtils.trim(jsRst.getRecord(0).getFieldString("WLOC_CD"            ));
			String ydPntCd         = commUtils.trim(jsRst.getRecord(0).getFieldString("YD_PNT_CD"          ));
			String ydLocGp         = commUtils.trim(jsRst.getRecord(0).getFieldString("YD_LOC_GP"          ));
			String ydFrmYn         = commUtils.trim(jsRst.getRecord(0).getFieldString("YD_FRM_YN"          ));
			String sSchStaYn       = commUtils.trim(jsRst.getRecord(0).getFieldString("SCH_STA_YN"          ));
			
			commUtils.printLog(logId, "소재제품 구분:" + ydLocGp , "SL");
			
			String sCarStat = "";
			/// 권하위치에  형상이 없는 경우  차량이 없는 경우 와 있는 경우 별도 작업처리 함 
			if (("Y".equals(sBackUpYn)) && (!"".equals(sCarNoGet))) { 
				sMsg = sPtLoadLoc+"동 개소에 다른 차량이 있는 경우 임. 현재차량["+sCarNoGet+"]"; 
				commUtils.printLog(logId, sMsg , "SL");
				sCarStat = "1";
			} else {
				sCarStat = "2";
			}
			
			sMsg = "작업구분["+sCarStat+"]"; 
			commUtils.printLog(logId, sMsg, "SL");
			JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
			if ("2".equals(sCarStat)) {
				
				if (!"".equals(sCarNoGet)) {
					sMsg = sPtLoadLoc+"동 개소지 야드포인트 사용불가. 현재차량["+sCarNoGet+"]"; 
					commUtils.printLog(logId, sMsg, "S-");
					jrRtn.setField("RTN_CD"         , "0");
					jrRtn.setField("RTN_MSG"        , sMsg);
					return jrRtn;
				}
				
				if (!"C".equals(ydStkColActStat)) {
					sMsg = sPtLoadLoc + "동 개소지의 야드포인트가 사용불가. ["+ydStkColActStat+"]";
					commUtils.printLog(logId, sMsg, "S-");
					jrRtn.setField("RTN_CD"         , "0");
					jrRtn.setField("RTN_MSG"        , sMsg);
					return jrRtn;
				}
				
				if (!"MT".equals(ydCarUsetypeGp)) {
					sMsg = sPtLoadLoc + "동 개소지의 야드포인트가 사용불가. 현재 포인트 타입 ["+ydCarUsetypeGp+"]";
					commUtils.printLog(logId, sMsg, "S-");
					jrRtn.setField("RTN_CD"         , "0");
					jrRtn.setField("RTN_MSG"        , sMsg);
					return jrRtn;
				}
				
				sMsg = sPtLoadLoc + "동 개소지의 야드포인트가 사용 가능.";
				commUtils.printLog(logId, sMsg, "SL");
				
			
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("CAR_NO"          , sCarNo);
				jrParam.setField("CARD_NO"         , sCarNo);
				jrParam.setField("YD_MAKECARPNT_CD", ydCarpntCd);
				jrParam.setField("YD_STK_COL_GP"   , sPtLoadLoc);
				
				//차량 POINT TABLE 점유
				coilDao.procUpdYdTransOrdChangeNEW(jrParam);
				
			    //YD 저장위치 맵 활성화
				coilDao.procYdLayerOpen(jrParam);			    	
			
			
			
	    		//---------------------------------------------------------------------------------------
				sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
				sndL2Msg.setField("YD_INFO_SYNC_CD"		, "4" ); //야드정보동기화코드
				sndL2Msg.setField("MSG_GP"				, "I" ); //전문구분
				sndL2Msg.setField("YD_STK_COL_GP"    	, sPtLoadLoc);
				sndL2Msg.setField("YD_STK_BED_NO"    	, "01");
				sndL2Msg.setField("YD_CAR_USE_GP"    	, "G"); //L:구내운송, G:출하차량
				sndL2Msg.setField("CAR_NO"  			, sCarNo); //차량번호
				sndL2Msg.setField("CARD_NO"  			, sCarNo); //카드번호
				sndL2Msg.setField("YD_CAR_ARRSTRT_STAT" , "A"); //A:도착, S:출발
				sndL2Msg.setField("YD_CAR_AIM_YD_GP"	, "J"); 
				
				if("U".equals(sCarUpdnGp)) {
					//상차도착
					sndL2Msg.setField("YD_EQP_WRK_STAT"  	, "U"); //U:공차, L:영차
				} else {
					//하차도착
					sndL2Msg.setField("YD_EQP_WRK_STAT"  	, "L"); //U:공차, L:영차
				}
	 
				//전송 Data 생성
				jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L001_CarInfo", sndL2Msg));		
				sMsg="[" + mthdNm + "] 저장위치 : 코일야드L2 로 송신 열구분[" + sPtLoadLoc + "] - 저장위치 제원 : 코일야드L2 로 송신 호출 "+jrRtn.size();
				commUtils.printLog(logId, sMsg, "SL");			
			}	
			/*
			 * sCarUpdnGp = 1 상차 작업
			 * 1. 차량 스케줄 생성
			 * 2. 작업예약 선택
			 * 3. 차량재료 등록
			 * 4. 차량 예정정보 발송
			 * 5. 크레인스케줄 
			 */
			/*****************
			 *  상차 작업 
			 ********************/
			
			
			String ydSchCd = ""; //상하차 구분 스케줄 코드
			String sTransOrdDate  = "";
			String sTransOrdSeqno = "";
			/******************
			 * 상차인 경우
			 *****************/
		    if ("1".equals(sCarUpdnGp)) {
		    	commUtils.printLog(logId, "<<<<<<<<<<<<<<<상차인 경우>>>>>>>>>>>>>>>>>>>>>", "SL");	
		    	if("H".equals(ydLocGp)) {
			    	if (("4".equals(sPtLoadLoc.substring(5, 6)))||("5".equals(sPtLoadLoc.substring(5, 6)))) {
						ydSchCd = sPtLoadLoc.subSequence(0,2) + "TR12UH";  //2통로
					} else if ("3".equals(sPtLoadLoc.substring(5, 6)) ) {
						ydSchCd = sPtLoadLoc.subSequence(0,2) + "TR13UH";  //3통로 
					} else {
						ydSchCd = sPtLoadLoc.subSequence(0,2) + "TR11UH";  //1통로
					}
		    	} else {
		    		if (("4".equals(sPtLoadLoc.substring(5, 6)))||("5".equals(sPtLoadLoc.substring(5, 6)))) {
						ydSchCd = sPtLoadLoc.subSequence(0,2) + "TR12UM";  //2통로
		    		} else if ("3".equals(sPtLoadLoc.substring(5, 6)) ) {
						ydSchCd = sPtLoadLoc.subSequence(0,2) + "TR13UM";  //3통로 
					} else {
						ydSchCd = sPtLoadLoc.subSequence(0,2) + "TR11UM";  //1통로
					}
		    	}
		    	commUtils.printLog(logId, "대상스케쥴 코드:" + ydSchCd, "SL");
		    	jrParam.setField("YD_SCH_CD"      , ydSchCd);
				
		    	/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdWrkbookBySchCdUp 
		    	SELECT A.YD_WBOOK_ID
		    	     , (SELECT YD_AIM_BAY_GP
		    	          FROM TB_YD_WRKBOOKMTL WM
		    	             , TB_YD_STOCK ST
		    	         WHERE WM.YD_WBOOK_ID = A.YD_WBOOK_ID    
		    	           AND WM.STL_NO = ST.STL_NO    
		    	           AND WM.DEL_YN = 'N'
		    	         GROUP BY ST.YD_AIM_BAY_GP ) AS YD_AIM_BAY_GP  --목표동 
		    	  FROM TB_YD_WRKBOOK A
		    	 WHERE 1=1
		    	   AND A.YD_SCH_CD  = :V_YD_SCH_CD
		    	   AND A.CAR_NO     = :V_CAR_NO
		    	   AND A.DEL_YN = 'N'
		    	   AND NOT EXISTS (
		    	                   SELECT *
		    	                     FROM USRYDA.TB_YD_CRNSCH B
		    	                        , TB_YD_WRKBOOK C 
		    	                    WHERE B.YD_WBOOK_ID = C.YD_WBOOK_ID
		    	                      AND B.YD_GP = SUBSTR(A.YD_SCH_CD,1,1)
		    	                      AND B.YD_SCH_CD LIKE 'J_TR1__'
		    	                      AND C.CAR_NO = A.CAR_NO
		    	                      AND B.DEL_YN = 'N'
		    	                      AND C.DEL_YN = 'N'
		    	                  )
		    	 ORDER BY A.YD_WBOOK_ID
				 */
		    	jsWbook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdWrkbookBySchCdUp", logId, mthdNm, "작업예약 조회");
				
				if (jsWbook.size() <= 0) {
					sMsg = "대상재 없음";
					commUtils.printLog(logId, sMsg, "S-");
					jrRtn.setField("RTN_CD"    , "0");
					jrRtn.setField("RTN_MSG"   , sMsg);
					return jrRtn;
				}

				String ydWbookId  = jsWbook.getRecord(0).getFieldString("YD_WBOOK_ID");
				String ydAimBayGp = jsWbook.getRecord(0).getFieldString("YD_AIM_BAY_GP");  //목표동
//				String ydLocGp    = ydSchCd.substring(7, 8);
				
				jrParam.setField("YD_LOC_GP"       , ydLocGp);
				jrParam.setField("STA_BAY_GP"      , ydSchCd.substring(1, 2));
				jrParam.setField("END_BAY_GP"      , ydAimBayGp);
				
				/*
				SELECT CASE WHEN SUBSTR(:V_YD_SCH_CD,2,1) IN ('B','C') AND SUBSTR(:V_YD_SCH_CD,6,1) IN ('2')  THEN 'DJY1E'
				            ELSE 'DJY22' END AS ARR_WLOC_CD
				  FROM DUAL
				 */
				JDTORecordSet jsCarWlocCd = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarWlocCd", logId, mthdNm, "작업개소조회");
				
				if (jsCarWlocCd.size() <= 0) {
					sMsg = "대상 개소코드 없음";
					commUtils.printLog(logId, sMsg, "S-");
					jrRtn.setField("RTN_CD"    , "0");
					jrRtn.setField("RTN_MSG"   , sMsg);
					return jrRtn;
				}
				
				String sArrWlocCd    = jsCarWlocCd.getRecord(0).getFieldString("ARR_WLOC_CD"  );

				if (!"".equals(ydCarSchId)) {
					/* com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarschArrStatWb 
					UPDATE TB_YD_CARSCH
					   SET MODIFIER      = :V_MODIFIER
					     , MOD_DDTT      = SYSDATE
					     , YD_CARUD_ARR_DT      = NVL(TO_DATE(:V_YD_CARUD_ARR_DT, 'YYYYMMDDHH24MISS'), YD_CARUD_ARR_DT)
					     , YD_CARLD_ARR_DT      = NVL(TO_DATE(:V_YD_CARLD_ARR_DT, 'YYYYMMDDHH24MISS'), YD_CARLD_ARR_DT)
					     , SPOS_WLOC_CD         = NVL(:V_SPOS_WLOC_CD         , SPOS_WLOC_CD        )
					     , YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_CARLD_WRK_BOOK_ID , YD_CARLD_WRK_BOOK_ID)
					     , YD_CARUD_WRK_BOOK_ID = NVL(:V_YD_CARUD_WRK_BOOK_ID , YD_CARUD_WRK_BOOK_ID)
					     , YD_CAR_PROG_STAT     = NVL(:V_YD_CAR_PROG_STAT     , YD_CAR_PROG_STAT    )  
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID   
					*/ 
					jrParam.setField("YD_CARLD_ARR_DT"  , commUtils.getDateTime14());
					jrParam.setField("YD_CAR_PROG_STAT"	, "2");	
					jrParam.setField("YD_CAR_SCH_ID"	, ydCarSchId);	

					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarschArrStatWb", logId, mthdNm, "TB_YD_CARSCH 차량 상차도착");
					
					
				} else {
					//운송지시일자, 순번 생성  (988001 처럼 앞에  988를 붙인다.)
					/*
					SELECT A.TRANS_ORD_DATE
					     , A.TRANS_ORD_SEQNO + 1 AS TRANS_ORD_SEQNO
					  FROM (
					        SELECT TO_CHAR(SYSDATE,'YYYYMMDD') AS TRANS_ORD_DATE
					             , NVL(MAX(TRANS_ORD_SEQNO),988000) AS TRANS_ORD_SEQNO
					          FROM TB_YD_CARSCH
					         WHERE TRANS_ORD_DATE = TO_CHAR(SYSDATE,'YYYYMMDD')
					           AND TRANS_ORD_SEQNO >  988000
					           AND TRANS_ORD_SEQNO <=  988999
					           AND TRANS_ORD_DATE = TO_CHAR(SYSDATE,'YYYYMMDD')
					       ) A
					 WHERE 1 = 1       
					 */
					JDTORecordSet jsTrans = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getRetnTransOrdNoByCar", logId, mthdNm, "작업예약 조회");
					
					sTransOrdDate  = jsTrans.getRecord(0).getFieldString("TRANS_ORD_DATE" );
					sTransOrdSeqno = jsTrans.getRecord(0).getFieldString("TRANS_ORD_SEQNO");
			    	
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					//차량 스케쥴 생성
					ydCarSchId = coilDao.getSeqId(logId, mthdNm, "CarSch");
					
					jrParam.setField("YD_CAR_SCH_ID"       , ydCarSchId                       );
					jrParam.setField("YD_EQP_WRK_STAT"     , "U"                              ); //야드설비작업상태
					jrParam.setField("YD_EQP_ID"           , CConstant.YD_TS_CAR_EQP_ID       ); //야드설비ID
					jrParam.setField("CAR_NO"              , sCarNo                           ); //운송장비코드
					jrParam.setField("CAR_KIND"            , "TR"                             ); //차량종류
					jrParam.setField("CARD_NO"             , sCarNo                           ); //운송장비코드
					jrParam.setField("YD_CAR_USE_GP"       , CConstant.YD_CAR_USE_GP_DM       ); //차량사용구분
					jrParam.setField("SPOS_WLOC_CD"        , sWlocCd                          ); //발지개소코드
					jrParam.setField("ARR_WLOC_CD"         , sArrWlocCd                       ); //착지개소코드
					jrParam.setField("YD_CARLD_LEV_LOC"    , sPtLoadLoc                       ); //야드상차출발위치
					jrParam.setField("YD_CARLD_LEV_DT"     , commUtils.getDateTime14()        ); //상차출발일시
					jrParam.setField("YD_BAYIN_WO_SEQ"     , CConstant.YD_BAYIN_WO_SEQ_DEFAULT); //입동지시순번 - 기본값으로 설정(9)
	//				jrParam.setField("YD_CAR_PROG_STAT"    , "2"                              ); //상차도착
					if ("2".equals(sCarStat)) {
						jrParam.setField("YD_CAR_PROG_STAT", "2"                         ); //상차도착
					} else {
						jrParam.setField("YD_CAR_PROG_STAT", "1"                         ); //상차출발
					}
					
					jrParam.setField("YD_CARLD_STOP_LOC"   , sPtLoadLoc                       ); //야드상차정지위치 (직상차 제외)
					
					jrParam.setField("YD_CARLD_ARR_DT"     , commUtils.getDateTime14()        );
					jrParam.setField("TRANS_ORD_DATE"      , sTransOrdDate                    );
					jrParam.setField("TRANS_ORD_SEQNO"     , sTransOrdSeqno                   );
					jrParam.setField("YD_CARLD_WRK_BOOK_ID", ydWbookId                        );// 상차 작업예약ID
					jrParam.setField("YD_PNT_CD1"          , ydPntCd                          );	
					jrParam.setField("YD_CAR_WRK_GP"       , "G"                              );// 차량동간이적	
					
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insYdCarsch", logId, mthdNm, "차량스케줄 생성");
				} 	
				
				
				
				if ("2".equals(sCarStat)) {
					jrParam.setField("YD_SCH_CD"      , ydSchCd);
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getSailNoWrkbookBySchCd 
					SELECT A.* 
					     , B.SAILNO
					  FROM TB_YD_WRKBOOK A
					     , TB_YD_STOCK B
					     , TB_YD_WRKBOOKMTL C
					 WHERE 1 = 1
					   AND A.YD_WBOOK_ID = C.YD_WBOOK_ID 
					   AND B.STL_NO      = C.STL_NO
					   AND A.YD_SCH_CD   = :V_YD_SCH_CD
					   AND A.CAR_NO      = :V_CAR_NO
					   AND A.DEL_YN ='N'
					   AND B.DEL_YN ='N'
					   AND C.DEL_YN ='N'
					   AND NOT EXISTS (
					                   SELECT *
					                     FROM USRYDA.TB_YD_CRNSCH B
					                        , TB_YD_WRKBOOK C 
					                    WHERE B.YD_WBOOK_ID = C.YD_WBOOK_ID
					                      AND B.YD_GP = SUBSTR(A.YD_SCH_CD,1,1)
					                      AND B.YD_SCH_CD LIKE '__TR1___'
					                      AND C.CAR_NO =  A.CAR_NO
					                      AND B.DEL_YN = 'N'
					                      AND C.DEL_YN = 'N'
					                  )
					 ORDER BY A.YD_WBOOK_ID
					        , B.STL_NO
					 */
					JDTORecordSet jsSailNo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getSailNoWrkbookBySchCd", logId, mthdNm, "작업예약 조회");
					if (jsSailNo.size() <= 0) {
						sMsg = "상차가능 개수 오류";
						commUtils.printLog(logId, sMsg, "S-");
						jrRtn.setField("RTN_CD"         , "0");
						jrRtn.setField("RTN_MSG"        , sMsg);
						return jrRtn;
					}
					
					String sSailNo = commUtils.nvl(jsSailNo.getRecord(0).getFieldString("SAILNO"), "0");
					
					//상차가능 수량이 더 많을때는 남은 재료카운트로 for 문
					if (Integer.parseInt(sSailNo) > jsWbook.size()) {
						sSailNo = "" + jsWbook.size();
					}
	
					commUtils.printLog(logId, "차량상차가능 매수 " + sSailNo, "SL");
					
					commUtils.printLog(logId, "매수:" + sSailNo, "SL");
					
					//PIDEV
//					String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
					
					jrParam.setField("CAR_NO" , sCarNo);
					
//					if("N".equals(sApplyYnPI)) {
//						jrParam.setField("CARD_NO", sCarNo);
//					}					
					
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschCarNoCardNo
					SELECT *
					  FROM (
					        SELECT A.*
					          FROM TB_YD_CARSCH A
					         WHERE CAR_NO  = :V_CAR_NO
					           AND CARD_NO = :V_CARD_NO
					           AND DEL_YN  = 'N'
					         ORDER BY YD_CAR_SCH_ID DESC
					       )
					 WHERE ROWNUM <= 1
					 */
					
					// PIDEV_S :병행가동용:PI_YD
					jrParam.setField("PI_YD"	, sPI_YD);
					jrParam.setField("PI_YD1"	, sPI_YD1);
					
					JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschCarNoCardNo_PIDEV", logId, mthdNm, "차량스케줄 조회");
					if (jsCarSch.size() <= 0) {
						sMsg = "차량스케줄 조회 오류";
						commUtils.printLog(logId, sMsg, "S-");
						jrRtn.setField("RTN_CD"    , "0");
						jrRtn.setField("RTN_MSG"   , sMsg);
						return jrRtn;
					}
										
					JDTORecordSet jsWbMtl = JDTORecordFactory.getInstance().createRecordSet("");
	
					// 상차 가능 매수만큼 반복 
					for (int i = 1; i <= Integer.parseInt(sSailNo); ++i) {
						jsSailNo.absolute(i);
						jrParam.setField("YD_WBOOK_ID", jsSailNo.getRecord().getFieldString("YD_WBOOK_ID"));
						/*
						SELECT A.YD_WBOOK_ID AS YD_WBOOK_ID
						     , D.STL_NO AS  STL_NO
						     , D.LOC
						     , A.DEL_YN AS DEL_YN
						     , A.YD_GP AS YD_GP
						     , A.YD_BAY_GP AS YD_BAY_GP
						     , A.YD_SCH_CD AS YD_SCH_CD
						     , A.YD_SCH_PRIOR AS YD_SCH_PRIOR
						     , A.YD_SCH_PROG_STAT AS YD_SCH_PROG_STAT
						     , A.YD_SCH_ST_GP AS YD_SCH_ST_GP
						     , A.YD_SCH_REQ_GP AS YD_SCH_REQ_GP
						     , A.YD_AIM_YD_GP AS YD_AIM_YD_GP
						     , A.YD_AIM_BAY_GP AS YD_AIM_BAY_GP
						     , A.YD_CTS_RELAY_YN AS YD_CTS_RELAY_YN
						     , A.YD_CTS_RELAY_BAY_GP AS YD_CTS_RELAY_BAY_GP
						     , A.YD_TO_LOC_DCSN_MTD AS YD_TO_LOC_DCSN_MTD
						     , A.YD_TO_LOC_GUIDE AS YD_TO_LOC_GUIDE
						     , A.YD_WRK_PLAN_TCAR AS YD_WRK_PLAN_TCAR
						     , A.YD_CAR_USE_GP AS YD_CAR_USE_GP
						     , A.TRN_EQP_CD AS TRN_EQP_CD
						     , A.CAR_NO AS CAR_NO
						     , D.CARD_NO AS CARD_NO
						     , D.CUST_CD AS CUST_CD
						     , D.DEST_CD AS DEST_CD
						     , D.DEST_TEL_NO AS DEST_TEL_NO
						     , D.DIST_SHIPASSIGN_GP AS DIST_SHIPASSIGN_GP
						  FROM TB_YD_WRKBOOK A
						     , (SELECT B.CUST_CD AS CUST_CD
						             , B.DEST_CD AS DEST_CD
						             , B.DEST_TEL_NO AS DEST_TEL_NO
						             , B.DIST_SHIPASSIGN_GP AS DIST_SHIPASSIGN_GP
						             , B.CARD_NO AS CARD_NO
						             , B.YD_AIM_YD_GP AS YD_AIM_YD_GP
						             , B.STL_NO AS STL_NO
						             , C.YD_WBOOK_ID AS YD_WBOOK_ID
						             , C.LOC
						          FROM TB_YD_STOCK B
						             , (SELECT YD_WBOOK_ID   AS YD_WBOOK_ID
						                     , STL_NO        AS STL_NO
						                     , YD_STK_BED_NO AS LOC
						                  FROM TB_YD_WRKBOOKMTL
						                 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID)C
						         WHERE B.STL_NO = C.STL_NO ) D
						 WHERE A.YD_WBOOK_ID =D.YD_WBOOK_ID
						 */
						jsWbMtl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdWbookYdStockYdWbookMtlBookId", logId, mthdNm, "작업예약 조회");
						
						jrParam.setField("STL_NO"       		, jsWbMtl.getRecord(0).getFieldString("STL_NO"));
						jrParam.setField("CAR_FRTOMOVE_WORD_NO" , sTransOrdDate+sTransOrdSeqno);
						jrParam.setField("YD_CAR_UPP_LOC_CD"    , "0"+i);
						
						/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updCarLotId 
						UPDATE TB_YD_STOCK 
						   SET MOD_DDTT             = SYSDATE
						     , MODIFIER             = :V_MODIFIER 
						     , CAR_FRTOMOVE_WORD_NO = :V_CAR_FRTOMOVE_WORD_NO
						     , COIL_CAR_NO          = :V_CAR_NO
						     , CARD_NO              = :V_CARD_NO
						     , YD_CAR_UPP_LOC_CD    = :V_YD_CAR_UPP_LOC_CD
						 WHERE STL_NO  = :V_STL_NO 
						 */
						commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updCarLotId", logId, mthdNm, "동간이송 LOT_ID 등록");
					}
					
					/**********************************************************
					 * 차량작업 예정정보 송신 YDY5L008
					 **********************************************************/
			    	sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
			    	sndL2Msg.setField("JMS_TC_CD"       , "YDY5L008");
					sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
					sndL2Msg.setField("SEARCH_FLAG"     , "2"       ); //1:상차도, 2:차량스케쥴 ID
					sndL2Msg.setField("CAR_WRK_GP"      , "G"       ); //G:차량동간이적
					sndL2Msg.setField("YD_CAR_SCH_ID"   , ydCarSchId); //차량스케줄
					sndL2Msg.setField("PT_LOAD_LOC"     , sPtLoadLoc);
					
//					// PIDEV_S :병행가동용:PI_YD
//					sndL2Msg.setField("PI_YD"			, sPI_YD);
//					sndL2Msg.setField("PI_YD1"			, sPI_YD1);
					
					jrRtn = commUtils.addSndData(jrRtn, coilDao.procCarPlanInfo(sndL2Msg));	 //전송 Data 생성 //TODO 해당 메소드 2군데 있음
					
					/**********************************************************
					 * 상차 크레인스케줄 기동
					 **********************************************************/
					commUtils.printLog(logId, "jsSailNo.size():" + jsSailNo.size(), "SL");	
					commUtils.printLog(logId, "sSchStaYn:" +sSchStaYn, "SL");	
					if ((jsSailNo.size() > 0) && ("Y".equals(sSchStaYn)))  {
						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setField("JMS_TC_CD"			, "YDYDJ552");  //
						jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()  ); //JMSTC생성일시
						jrYdMsg.setField("MODIFIER"				, sModifier);
						
						
						for (int i = 1; i <= Integer.parseInt(sSailNo); i++) {
							jsSailNo.absolute(i);
							jrYdMsg.setField("YD_WBOOK_ID"+i     , jsSailNo.getRecord().getFieldString("YD_WBOOK_ID"    )); //야드작업예약ID
							jrYdMsg.setField("SCH_CNT" , ""+i); //작업예약 개수
						}
						
						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);

					}
				}
		    } else {//sCarUpdnGp = 2 하차 작업
		    	
		    	/************************************************
				 *   하차 작업
				 *   - 타동에 작업예약이 있는지 CHECK
				 *     - 있으면 ERROR
				 *************************************************/		    	
		    	
		    	commUtils.printLog(logId, "<<<<<<<<<<<<<<<하차인 경우>>>>>>>>>>>>>>>>>>>>>", "SL");	
		    	if("H".equals(ydLocGp)) {
			    	if (("4".equals(sPtLoadLoc.substring(5, 6)))||("5".equals(sPtLoadLoc.substring(5, 6)))) {
						ydSchCd = sPtLoadLoc.subSequence(0,2) + "TR12LH";  //2통로
					} else if ("3".equals(sPtLoadLoc.substring(5, 6)) ) {
						ydSchCd = sPtLoadLoc.subSequence(0,2) + "TR13LH";  //3통로 
					} else {
						ydSchCd = sPtLoadLoc.subSequence(0,2) + "TR11LH";  //1통로
					}
		    	} else {
			    	if(("4".equals(sPtLoadLoc.substring(5, 6)))||("5".equals(sPtLoadLoc.substring(5, 6)))) {
						ydSchCd = sPtLoadLoc.subSequence(0,2) + "TR12LM";  //2통로
					} else if ("3".equals(sPtLoadLoc.substring(5, 6)) ) {
						ydSchCd = sPtLoadLoc.subSequence(0,2) + "TR13LM";  //3통로 
					} else {
						ydSchCd = sPtLoadLoc.subSequence(0,2) + "TR11LM";  //1통로
					}
		    	}
		    	commUtils.printLog(logId, "대상스케쥴 코드:" + ydSchCd, "SL");

				//ydSchCd = ydLoadLoc.substring(0 , 2)+"PT07LM";
				jrParam.setField("YD_SCH_CD"        , ydSchCd);
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdWrkbookBySchCdChk 
				SELECT YD_CAR_SCH_ID
				     , STL_NO 
				     , YD_AIM_BAY_GP
				     , WB_CNT
				     , YD_SCH_CD
				     , DECODE(ERR_CNT,0,'N','Y') AS ERR_GP
				  FROM ( 
				        SELECT B.YD_CAR_SCH_ID
				             , B.STL_NO
				             -- 해당차량이 다른위치에 하차작업예약이 있으면 ERR
				             , (SELECT COUNT(*) 
				                  FROM TB_YD_WRKBOOK C
				                 WHERE C.YD_SCH_CD LIKE 'J_TR1_L_%'
				                   AND C.YD_SCH_CD <> :V_YD_SCH_CD
				                   AND C.CAR_NO = A.CAR_NO  
				                   AND C.DEL_YN = 'N') AS ERR_CNT
				             , (SELECT COUNT(*) 
				                  FROM TB_YD_WRKBOOK C
				                     , TB_YD_WRKBOOKMTL D
				                 WHERE C.YD_WBOOK_ID = D.YD_WBOOK_ID
				                   AND C.YD_SCH_CD = :V_YD_SCH_CD
				                   AND D.STL_NO = B.STL_NO
				                   AND C.CAR_NO = A.CAR_NO   
				                   AND C.DEL_YN = 'N'
				                   AND D.DEL_YN = 'N') WB_CNT
				             , (SELECT YD_AIM_BAY_GP FROM TB_YD_STOCK WHERE STL_NO = B.STL_NO )  AS YD_AIM_BAY_GP     
				             , :V_YD_SCH_CD  AS YD_SCH_CD
				          FROM TB_YD_CARSCH A
				             , TB_YD_CARFTMVMTL B
				         WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				           AND A.CAR_NO = :V_CAR_NO
				           AND A.DEL_YN = 'N'
				           AND B.DEL_YN = 'N'
				        )   
				*/	   
				
				JDTORecordSet jsWbookChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdWrkbookBySchCdChk", logId, mthdNm, "작업예약정보조회");
				if (jsWbookChk.size() == 0) {
					commUtils.printLog(logId, "차량재료 정보가 없습니다.", "S-");
					jrRtn.setField("RTN_CD"    , "0");
					jrRtn.setField("RTN_MSG"   , "차량재료 정보가 없습니다" );
					return jrRtn;
				}	
				
				int iWbCnt = 0;
				JDTORecord jrWbookChk = commUtils.getParam(logId, mthdNm, sModifier);
				for(int Loop_i = 1; Loop_i <= jsWbookChk.size() ; Loop_i++) {
					jsWbookChk.absolute(Loop_i);
					jrWbookChk = jsWbookChk.getRecord();
					
					if("Y".equals(commUtils.trim(jrWbookChk.getFieldString("ERR_GP")))) {
						
						commUtils.printLog(logId, "타동에 하차 작업이 있습니다..", "S-");
						jrRtn.setField("RTN_CD"    , "0");
						jrRtn.setField("RTN_MSG"   , "타동에 하차 작업이 있습니다" );
						return jrRtn;
					}
					
					iWbCnt = Integer.parseInt(commUtils.nvl(jrWbookChk.getFieldString("WB_CNT"),"0"));
					commUtils.printLog(logId, "iWbCnt." + iWbCnt, "SL");
					if (iWbCnt == 0) {
					 /***********************************
					  * 작업예약 생성 및 작업예약 재료 생성
					  *********************************/
						commUtils.printLog(logId, "작업예약생성..", "SL");
						//작업예약 등록 호출
    					JDTORecord jrInRec = commUtils.getParam(logId, mthdNm, sModifier);
    					jrInRec.setField("YD_SCH_CD"			, ydSchCd);//스케줄코드
    					jrInRec.setField("STL_SH"				, "1");  //LINE_IN 재료매수
    					jrInRec.setField("STL_NO1"				, commUtils.trim(jrWbookChk.getFieldString("STL_NO")));
    					jrInRec.setField("YD_TO_LOC_DCSN_MTD"	, "S");								
    					jrInRec.setField("YD_UP_COLL_SEQ1"		, "1");  //권상모음순서
    					jrInRec.setField("YD_USER_ID"			, sModifier);   
    					jrInRec.setField("CARD_NO"				, sCarNo);  //차량번호
    					jrInRec.setField("CAR_NO"				, sCarNo);  //차량번호
    					jrInRec.setField("YD_AIM_YD_GP"		    , "J");  
    					jrInRec.setField("YD_AIM_BAY_GP"		, commUtils.trim(jrWbookChk.getFieldString("YD_AIM_BAY_GP")));
    					jrInRec.setField("YD_CAR_USE_GP"		, CConstant.YD_CAR_USE_GP_DM);
    					
    	    			EJBConnector ejbConn = new EJBConnector("default", "CCoilWrkBookSeEJB", this);
    	    			ejbConn.trx("insWrkBookTx", new Class[] { JDTORecord.class }, new Object[] { jrInRec });						
					}
					
				}
				
				
//				2.2 생성한 스케줄코드로 작업 예약 select
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdWrkbookBySchCdDn 
				SELECT KK.*
				     , (SELECT YD_STK_BED_NO 
				          FROM TB_YD_CARFTMVMTL 
				         WHERE YD_CAR_SCH_ID = KK.YD_CAR_SCH_ID 
				           AND STL_NO = KK.STL_NO) AS YD_STK_BED_NO
				  FROM (
				        SELECT A.* 
				             , B.STL_NO
				             , ( SELECT YD_CAR_SCH_ID 
				                   FROM TB_YD_CARSCH 
				                  WHERE DEL_YN = 'N' 
				                    AND TRANS_ORD_DATE||TRANS_ORD_SEQNO = B.CAR_FRTOMOVE_WORD_NO
				                    AND CAR_NO          = B.COIL_CAR_NO ) AS YD_CAR_SCH_ID
				          FROM TB_YD_WRKBOOK A
				             , TB_YD_STOCK B
				             , TB_YD_WRKBOOKMTL C
				         WHERE 1=1
				           AND A.YD_WBOOK_ID = C.YD_WBOOK_ID 
				           AND B.STL_NO      = C.STL_NO
				           AND A.YD_SCH_CD   = :V_YD_SCH_CD
				           AND B.COIL_CAR_NO = :V_CAR_NO
				           AND A.DEL_YN='N'
				           AND B.DEL_YN='N'
				           AND C.DEL_YN='N'
				           AND A.YD_WBOOK_ID NOT IN (
				                                   SELECT C1.YD_WBOOK_ID
				                                     FROM USRYDA.TB_YD_CRNSCH B1
				                                        , TB_YD_WRKBOOK C1 
				                                    WHERE B1.YD_WBOOK_ID = C1.YD_WBOOK_ID
				                                      AND B1.YD_GP = SUBSTR(A.YD_SCH_CD,1,1)
				                                      AND B1.YD_SCH_CD LIKE '__TR1___'
				                                      AND C1.CAR_NO =  A.CAR_NO
				                                      AND B1.DEL_YN = 'N'
				                                      AND C1.DEL_YN = 'N'
				                                 )
				        ORDER BY A.YD_WBOOK_ID
				) KK
				*/
				jsWbook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdWrkbookBySchCdDn", logId, mthdNm, "작업예약정보조회"); 				
				if (jsWbook.size() == 0) {
					commUtils.printLog(logId, "작업예약정보 없음 ", "S-");
					jrRtn.setField("RTN_CD"    , "0");
					jrRtn.setField("RTN_MSG"   , "작업예약정보 없음" );
					return jrRtn;
					
				} 
			
				commUtils.printLog(logId, mthdNm + "작업예약갯수:" +jsWbook.size() , "SL");
				ydCarSchId  = jsWbook.getRecord(0).getFieldString("YD_CAR_SCH_ID" );

				/***** 하차 작업 처리   ***************/
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);	
				jrParam.setField("YD_CAR_SCH_ID"		, ydCarSchId);
				jrParam.setField("YD_CARUD_ARR_DT"		, commUtils.getDateTime14());
//				jrParam.setField("YD_CAR_PROG_STAT"		, "B" );		//하차도착상태
				if ("2".equals(sCarStat)) {
					jrParam.setField("YD_CAR_PROG_STAT", "B" ); //하차도착
				} else {
					jrParam.setField("YD_CAR_PROG_STAT", "A" ); //하차출발
				}
				jrParam.setField("ARR_WLOC_CD"			, sWlocCd);		
				jrParam.setField("YD_PNT_CD3"			, ydPntCd);
				jrParam.setField("YD_CARUD_STOP_LOC"	, sPtLoadLoc);	
				jrParam.setField("YD_EQP_WRK_STAT"		, "L");			
				/* com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarschArrStatDn 
				UPDATE TB_YD_CARSCH
				   SET MODIFIER         = :V_MODIFIER
				     , MOD_DDTT          = SYSDATE
				     , YD_CAR_PROG_STAT  = :V_YD_CAR_PROG_STAT  
				     , YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC   
				     , YD_CARUD_ARR_DT   = TO_DATE(:V_YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS') 
				     , ARR_WLOC_CD       = :V_ARR_WLOC_CD
				     , YD_PNT_CD3        = :V_YD_PNT_CD3
				     , YD_EQP_WRK_STAT   = :V_YD_EQP_WRK_STAT  
				     
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID 
				*/ 
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarschArrStatDn", logId, mthdNm, "TB_YD_CARSCH 차량 하차도착");
				if ("2".equals(sCarStat)) {
					JDTORecord jrInTemp  = commUtils.getParam(logId, mthdNm, sModifier);	
					// 하차 가능 매수  수만큼 루프
					for(int Loop_i = 1; Loop_i <= jsWbook.size() ; Loop_i++) {
						jsWbook.absolute(Loop_i);
						jrWbook = jsWbook.getRecord();
						
						// 하차인 경우: 저장위치에 재료 정보 SET
						
						jrInTemp.setField("YD_STK_COL_GP"   	, sPtLoadLoc);
						jrInTemp.setField("YD_STK_BED_NO"   	, jrWbook.getFieldString("YD_STK_BED_NO"));
						jrInTemp.setField("YD_STK_LYR_NO" 		, "001");
						jrInTemp.setField("STL_NO"       		, jrWbook.getFieldString("STL_NO"));
						jrInTemp.setField("YD_STK_LYR_ACT_STAT" , "E");
						jrInTemp.setField("YD_STK_LYR_MTL_STAT" , "C");
				    	
				    	/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkLyrYdStkColBedGp 
				    	UPDATE TB_YD_STKLYR            
				    	   SET MOD_DDTT     = SYSDATE             
				    	     , MODIFIER     = :V_MODIFIER             
				    	     , YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT, YD_STK_LYR_ACT_STAT)
				    	     , YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT, YD_STK_LYR_MTL_STAT)
				    	     , STL_NO              = :V_STL_NO
				    	 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
				    	   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
				    	   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO  
				    	 */
						commDao.update(jrInTemp, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkLyrYdStkColBedGp", logId, mthdNm, "TB_YD_STKLYR 등록");
					}				
					
					/**********************************************************
					 * 차량작업 예정정보 송신 YDY5L008
					 **********************************************************/
			    	sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
			    	sndL2Msg.setField("JMS_TC_CD"       , "YDY5L008");
					sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
					sndL2Msg.setField("SEARCH_FLAG"     , "2"       ); //1:상차도, 2:차량스케쥴 ID
					sndL2Msg.setField("YD_CAR_SCH_ID"   , ydCarSchId); //차량스케줄
					sndL2Msg.setField("CAR_WRK_GP"      , "G"       ); //G:차량동간이적 추가
					sndL2Msg.setField("PT_LOAD_LOC"     , sPtLoadLoc); //
					
					jrRtn = commUtils.addSndData(jrRtn, coilDao.procCarPlanInfo(sndL2Msg));	 //전송 Data 생성 //TODO 해당 메소드 2군데 있음
					
					/**********************************************************
					 * 크레인스케줄 기동
					 *  - 형상있는 곳은 형상 완료 전문(신규전문 수신시 스케줄 기동)
					 *  - 형상없는 곳은 기존 로직 그대로 실행
					 **********************************************************/
					commUtils.printLog(logId, "jsWbook.size():" + jsWbook.size(), "SL");	
					commUtils.printLog(logId, "sSchStaYn:" +sSchStaYn, "SL");						
					commUtils.printLog(logId, "형상이 있는 곳에는 형상정보가 올라와야지 스케쥴 기동처리 됨", "SL");						
					if ((jsWbook.size() > 0) && ("Y".equals(sSchStaYn))) {
						
						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setField("JMS_TC_CD"			, "YDYDJ552");  //
						jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
						jrYdMsg.setField("MODIFIER"				, sModifier);
						
						for (int i = 1; i <= jsWbook.size(); i++) {
							jsWbook.absolute(i);
							jrYdMsg.setField("YD_WBOOK_ID"+i     , jsWbook.getRecord().getFieldString("YD_WBOOK_ID"    )); //야드작업예약ID
							jrYdMsg.setField("SCH_CNT" , ""+i); //작업예약 개수
						}
						
						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
					}
				}
		    }
	
			commUtils.printLog(logId, mthdNm, "S-");

			jrRtn.setField("RTN_CD"	, "1");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

		
	
	/**
	 *      [A] 오퍼레이션명 : 크레인 비상조업실적(Y5YDL019)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException 
	*/
	public JDTORecord rcvY5YDL019(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "크레인 비상조업실적[CoilSpecRegSeEJB.rcvY5YDL019] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = commUtils.getParam(logId, mthdNm, "Y5YDL019");	//전문 Return
		
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값
			String msgId            = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId          = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //야드설비ID(6)
			String ydUpLoc          = commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LOC" )); //권상위치(11)
			String ydDnLoc          = commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LOC" )); //권하위치(11) 
			String sStlNo           = commUtils.trim(rcvMsg.getFieldString("STL_NO"       )); //재료번호 
			String ydUpCmplDt       = commUtils.trim(rcvMsg.getFieldString("YD_UP_CMPL_DT")); //야드권상완료일시
			String ydDnCmplDt       = commUtils.trim(rcvMsg.getFieldString("YD_DN_CMPL_DT")); //야드권하완료일시
			
			String sModifier        = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			/**********************************************************
			* 0. 수신항목 Check
			**********************************************************/
			if (!"CR".equals(ydEqpId.substring(2,4))) {
				throw new Exception("야드설비ID(YD_EQP_ID) 설비 구분이 'CR'이 아닙니다!! [" + ydEqpId + "]");
			}
			if (ydUpLoc.length() != 11) {
				throw new Exception("권상위치(YD_UP_LOC)가 11자리가 아닙니다!! [" + ydUpLoc + "]");
			}
			if (ydDnLoc.length() != 11) {
				throw new Exception("권하위치(YD_DN_LOC)가 11자리가 아닙니다!! [" + ydDnLoc + "]");
			}
			if ("".equals(sStlNo)) {
				throw new Exception("STL_NO 에 빈 값이 들어왔습니다!! [" + sStlNo + "]");
			}
	 
			if (ydUpCmplDt.length() != 14) {
				throw new Exception("야드권상완료일시(YD_UP_CMPL_DT)가 14자리가 아닙니다!! [" + ydUpCmplDt + "]");
			}
			if (ydDnCmplDt.length() != 14) {
				throw new Exception("야드권하완료일시(YD_DN_CMPL_DT)가 14자리가 아닙니다!! [" + ydDnCmplDt + "]");
			}
  
			JDTORecord jrParam	= commUtils.getParam(logId, mthdNm, sModifier); 
			
			//Crane스케줄ID생성 
			String sCrnSchId = coilDao.getSeqId(logId, mthdNm, "CrnSch"); //비상조업실적용 1개의 Crane스케줄ID 사용
	 
			/**********************************************************
			* 1. STL_NO 로 적치단 Clear 하기
			**********************************************************/
			/*
			UPDATE TB_YD_STKLYR
			   SET MODIFIER       = :V_MODIFIER
			     , MOD_DDTT       = SYSDATE
			     , STL_NO         = ''
			     , YD_STK_LYR_MTL_STAT = 'E'
			 WHERE STL_NO = :V_STL_NO
			   AND YD_STK_COL_GP LIKE :V_YD_GP || '%'  
			*/
			jrParam.setField("STL_NO"	, sStlNo);
			jrParam.setField("YD_GP"	, "J");	
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.clrStkLyr", logId, mthdNm, "STL_NO 로 적치단 Clear");
			
			
			/**********************************************************
			* 2. 권하위치에  STL_NO 를 적치중으로 설정한다.
			**********************************************************/
			/*
			UPDATE TB_YD_STKLYR
			   SET MODIFIER       = :V_MODIFIER
			     , MOD_DDTT       = SYSDATE
			     , STL_NO         = :V_STL_NO
			     , YD_STK_LYR_MTL_STAT = 'C'
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
			   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
			  */
			jrParam.setField("STL_NO"			, sStlNo);
			jrParam.setField("YD_STK_COL_GP"	, ydDnLoc.substring(0,6));	
			jrParam.setField("YD_STK_BED_NO"	, ydDnLoc.substring(6,8));	
			jrParam.setField("YD_STK_LYR_NO"	, ydDnLoc.substring(8,11));
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.setStakLyr", logId, mthdNm, "권하위치에  STL_NO 를 적치중으로 설정 ");

			
			/**********************************************************
			* 3. 코일공통 수정
			**********************************************************/  
			jrParam.setField("STL_NO"   , sStlNo ); //권하 코일번호
			jrParam.setField("YD_LOC"   , ydDnLoc.substring(0,8)+ydDnLoc.substring(9,11)); //야드권하실적위치
			
			/*
			UPDATE TB_PT_COILCOMM
			   SET (  
			         YD_GP                 -- 야드구분
			       , YD_BAY_GP             -- 동
			       , YD_EQP_GP             -- SPAN
			       , YD_STK_COL_NO         -- 적치열번지
			       , YD_STK_BED_NO         -- 적치번지
			       , YD_STK_LYR_NO         -- 적치단
			       , YD_STR_LOC            -- 현 저장위치코드
			       , YD_STR_LOC_HIS1       -- 전 저장위치코드
			       , YD_STR_LOC_HIS2       -- 전전 저장위치코드
			       ) =
			       (
			        SELECT 
			               SUBSTR(P_YD_LOC,1,1) AS YD_GP         -- 야드구분
			             , SUBSTR(P_YD_LOC,2,1) AS YD_BAY_GP     -- 동
			             , SUBSTR(P_YD_LOC,3,2) AS YD_EQP_GP     -- SPAN
			             , SUBSTR(P_YD_LOC,5,2) AS YD_STK_COL_NO -- 적치열번지
			             , SUBSTR(P_YD_LOC,7,2) AS YD_STK_BED_NO -- 적치번지
			             , SUBSTR(P_YD_LOC,9,3) AS YD_STK_LYR_NO -- 적치단
			             , P_YD_LOC        AS YD_STR_LOC         -- 현 저장위치코드   
			             , YD_STR_LOC      AS YD_STR_LOC_HIS1    -- 전현 저장위치코드
			             , YD_STR_LOC_HIS1 AS YD_STR_LOC_HIS2    -- 전전현 저장위치코드
			          FROM TB_PT_COILCOMM
			             ,(SELECT :V_YD_LOC AS P_YD_LOC FROM DUAL) 
			         WHERE COIL_NO = :V_STL_NO
			     )
			 WHERE COIL_NO = :V_STL_NO
			 */
			commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updateCoilCommonLocInfo", logId, mthdNm, "TB_PT_COILCOMM 저장위치 수정");
			
			/**********************************************************
			* 4. 저장품 비상조업 수정
			**********************************************************/
			/* 
			UPDATE TB_YD_STOCK
			   SET MODIFIER       = :V_MODIFIER
			     , MOD_DDTT       = SYSDATE
			     , URGENT_DIST_YN ='Y' --비상조업구분
			 WHERE STL_NO = :V_STL_NO   
			 */ 
			jrParam.setField("STL_NO"	, sStlNo);
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStockUrgentDistYn", logId, mthdNm, "저장품 URGENT_DIST_YN 수정 ");
			
			/**********************************************************
			* 5. 작업이력 등록
			**********************************************************/
			/* 
			INSERT INTO TB_YD_WRKHIST (
			  YD_WRK_HIST_ID
			, YD_CRN_SCH_ID
			, YD_SCH_CD
			, STL_NO
			, YD_EQP_ID
			, YD_UP_WR_LOC
			, YD_UP_WO_LAYER
			, YD_UP_CMPL_DT
			, YD_DN_WR_LOC
			, YD_DN_WR_LAYER
			, YD_DN_CMPL_DT
			, REGISTER
			, REG_DDTT
			, MODIFIER
			, MOD_DDTT
			, DEL_YN
			, YD_GP
			) VALUES (
			  CONCAT(TO_CHAR(SYSDATE-1,'YYYYMMDDHH24MI'),LPAD(SUBSTR((SELECT MAX(YD_WRK_HIST_ID) FROM TB_YD_WRKHIST),13,6)+1, 6, '0')) 
			, :V_YD_CRN_SCH_ID
			, SUBSTR(:V_YD_EQP_ID,1,2)||'CV01LH'
			, :V_STL_NO
			, :V_YD_EQP_ID
			, :V_YD_UP_WR_LOC
			, :V_YD_UP_WO_LAYER
			, TO_DATE(:V_YD_UP_CMPL_DT,'YYYYMMDDHH24MISS')
			, :V_YD_DN_WR_LOC
			, :V_YD_DN_WR_LAYER
			, TO_DATE(:V_YD_DN_CMPL_DT,'YYYYMMDDHH24MISS')
			, :V_MODIFIER
			, SYSDATE
			, :V_MODIFIER
			, SYSDATE
			, 'N'
			, :V_YD_GP
			)
			*/
			jrParam.setField("YD_CRN_SCH_ID"		, sCrnSchId              );	
			jrParam.setField("STL_NO"				, sStlNo                 );
			jrParam.setField("YD_EQP_ID"			, ydEqpId                );	
			jrParam.setField("YD_UP_WR_LOC"   		, ydUpLoc.substring(0, 8)); //야드권하실적위치
			jrParam.setField("YD_UP_WO_LAYER"   	, ydUpLoc.substring(8,11)); //야드권하실적위치
			jrParam.setField("YD_UP_CMPL_DT"		, ydUpCmplDt             );
			jrParam.setField("YD_DN_WR_LOC"   		, ydDnLoc.substring(0, 8)); //야드권하실적위치
			jrParam.setField("YD_DN_WR_LAYER"  		, ydDnLoc.substring(8,11)); //야드권하실적위치
			jrParam.setField("YD_DN_CMPL_DT"		, ydDnCmplDt             );	
			jrParam.setField("YD_GP"				, "J"                    );	
			commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insWrkHist", logId, mthdNm, "비상조업용 작업이력 등록 ");
 
		  
			/**********************************************************
			* 5. 저장품제원 : 코일야드L2로 송신(YDY5L002)
			**********************************************************/
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setField("JMS_TC_CD"      , "YDY5L002");
			sndL2Msg.setField("YD_INFO_SYNC_CD", "5"       );    // 5:지정저장품
			sndL2Msg.setField("STL_NO"         , sStlNo    );
			sndL2Msg.setField("YD_STK_COL_GP"  , ""        );
			sndL2Msg.setField("YD_STK_BED_NO"  , ""        );
			
			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L002", sndL2Msg));
			
			/**************************************************************
			* 6. 열연수입완료실적 : 조업L3 송신(YDHRJ007) 21.09.16 (조업L3 요청)
			***************************************************************/
			if( "CV".equals(ydUpLoc.substring(2, 4)) ) {
				
				JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier); 
				jrYdMsg.setField("JMS_TC_CD"			, "YDHRJ007");
				jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
				jrYdMsg.setField("YD_GP"				, "J");		// 야드구분 추가	 
				jrYdMsg.setField("STL_NO"				, sStlNo);	// 재료번호
				
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
		    	commUtils.printLog(logId, "열연수입완료실적(YDHRJ007) 송신", "SL");
			}
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일 SPM1 TrackIng 정보(Y5YDL020)
	 *		
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL020(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일 SPM1 TrackIng 정보[CCoilL2RcvSeEJB.rcvY5YDL020] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String sECC06   = commUtils.trim(rcvMsg.getFieldString("ECC06"));
			String sECC05   = commUtils.trim(rcvMsg.getFieldString("ECC05"));
			String sECC04   = commUtils.trim(rcvMsg.getFieldString("ECC04"));
			String sECC03   = commUtils.trim(rcvMsg.getFieldString("ECC03"));
			String sECC02   = commUtils.trim(rcvMsg.getFieldString("ECC02"));
			String sECC01   = commUtils.trim(rcvMsg.getFieldString("ECC01"));
			String sENT     = commUtils.trim(rcvMsg.getFieldString("ENT"  ));
			String sPOR     = commUtils.trim(rcvMsg.getFieldString("POR"  ));
			String sMILL    = commUtils.trim(rcvMsg.getFieldString("MILL" ));
			String sTR      = commUtils.trim(rcvMsg.getFieldString("TR"   ));
			String sEXIT    = commUtils.trim(rcvMsg.getFieldString("EXIT "));
			String sDCC01   = commUtils.trim(rcvMsg.getFieldString("DCC01"));
			String sDCC02   = commUtils.trim(rcvMsg.getFieldString("DCC02"));
			String sDCC03   = commUtils.trim(rcvMsg.getFieldString("DCC03"));
			String sDCC04   = commUtils.trim(rcvMsg.getFieldString("DCC04"));
			String sDCC05   = commUtils.trim(rcvMsg.getFieldString("DCC05"));
			String sDCC06   = commUtils.trim(rcvMsg.getFieldString("DCC06"));
			String sDCC07   = commUtils.trim(rcvMsg.getFieldString("DCC07"));
			String sDCC08   = commUtils.trim(rcvMsg.getFieldString("DCC08"));
			String sDCC09   = commUtils.trim(rcvMsg.getFieldString("DCC09"));
			String sDCC10   = commUtils.trim(rcvMsg.getFieldString("DCC10"));
			String sDCC11   = commUtils.trim(rcvMsg.getFieldString("DCC11"));
			String sSHC     = commUtils.trim(rcvMsg.getFieldString("SHC"  ));

			
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;

			/**********************************************************
			* 1. 조업 TRACKING UPDATE
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("EQP_CD"   ,"SPM1");
			
			jrParam.setField("STLNO1"   ,sECC06);
			jrParam.setField("STLNO2"   ,sECC05); 
			jrParam.setField("STLNO3"   ,sECC04); 
			jrParam.setField("STLNO4"   ,sECC03); 
			jrParam.setField("STLNO5"   ,sECC02); 
			jrParam.setField("STLNO6"   ,sECC01); 
			jrParam.setField("STLNO7"   ,sENT  ); 
			jrParam.setField("STLNO8"   ,sPOR  ); 
			jrParam.setField("STLNO9"   ,sMILL ); 
			jrParam.setField("STLNO10"  ,sTR   ); 
			jrParam.setField("STLNO11"  ,sEXIT ); 
			jrParam.setField("STLNO12"  ,sDCC01); 
			jrParam.setField("STLNO13"  ,sDCC02); 
			jrParam.setField("STLNO14"  ,sDCC03); 
			jrParam.setField("STLNO15"  ,sDCC04); 
			jrParam.setField("STLNO16"  ,sDCC05); 
			jrParam.setField("STLNO17"  ,sDCC06); 
			jrParam.setField("STLNO18"  ,sDCC07); 
			jrParam.setField("STLNO19"  ,sDCC08); 
			jrParam.setField("STLNO20"  ,sDCC09); 
			jrParam.setField("STLNO21"  ,sDCC10); 
			jrParam.setField("STLNO22"  ,sDCC11); 
			jrParam.setField("STLNO23"  ,sSHC  ); 

			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpTrackingSPMHFL", logId, mthdNm, "코일 SPM1 TrackIng정보 수정");

				
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일 SPM2 TrackIng 정보(Y5YDML021)
	 *		
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL021(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일 SPM2 TrackIng 정보[CCoilL2RcvSeEJB.rcvY5YDL021] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String sECC06   = commUtils.trim(rcvMsg.getFieldString("ECC06"));  
			String sECC05   = commUtils.trim(rcvMsg.getFieldString("ECC05"));  
			String sECC04   = commUtils.trim(rcvMsg.getFieldString("ECC04"));  
			String sECC03   = commUtils.trim(rcvMsg.getFieldString("ECC03"));  
			String sECC02   = commUtils.trim(rcvMsg.getFieldString("ECC02"));  
			String sECC01   = commUtils.trim(rcvMsg.getFieldString("ECC01"));  
			String sENT     = commUtils.trim(rcvMsg.getFieldString("ENT"  ));  
			String sPOR     = commUtils.trim(rcvMsg.getFieldString("POR"  ));  
			String sMILL    = commUtils.trim(rcvMsg.getFieldString("MILL" ));  
			String sTR      = commUtils.trim(rcvMsg.getFieldString("TR"   ));  
			String sEXIT    = commUtils.trim(rcvMsg.getFieldString("EXIT "));  
			String sDCC01   = commUtils.trim(rcvMsg.getFieldString("DCC01"));  
			String sDCC02   = commUtils.trim(rcvMsg.getFieldString("DCC02"));  
			String sDCC03   = commUtils.trim(rcvMsg.getFieldString("DCC03"));  
			String sDCC04   = commUtils.trim(rcvMsg.getFieldString("DCC04"));  
			String sDCC05   = commUtils.trim(rcvMsg.getFieldString("DCC05"));  
			String sDCC06   = commUtils.trim(rcvMsg.getFieldString("DCC06"));  
			String sDCC07   = commUtils.trim(rcvMsg.getFieldString("DCC07"));  
			String sDCC08   = commUtils.trim(rcvMsg.getFieldString("DCC08"));  
			String sDCC09   = commUtils.trim(rcvMsg.getFieldString("DCC09"));  
			String sDCC10   = commUtils.trim(rcvMsg.getFieldString("DCC10"));  
			String sDCC11   = commUtils.trim(rcvMsg.getFieldString("DCC11"));  
			String sDSHC    = commUtils.trim(rcvMsg.getFieldString("DSHC"  )); 
			
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;

			/**********************************************************
			* 1. 조업 TRACKING UPDATE
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("EQP_CD"   ,"SPM2");
			
			jrParam.setField("STLNO1"   ,sECC06);
			jrParam.setField("STLNO2"   ,sECC05); 
			jrParam.setField("STLNO3"   ,sECC04); 
			jrParam.setField("STLNO4"   ,sECC03); 
			jrParam.setField("STLNO5"   ,sECC02); 
			jrParam.setField("STLNO6"   ,sECC01); 
			jrParam.setField("STLNO7"   ,sENT  ); 
			jrParam.setField("STLNO8"   ,sPOR  ); 
			jrParam.setField("STLNO9"   ,sMILL ); 
			jrParam.setField("STLNO10"  ,sTR   ); 
			jrParam.setField("STLNO11"  ,sEXIT ); 
			jrParam.setField("STLNO12"  ,sDCC01); 
			jrParam.setField("STLNO13"  ,sDCC02); 
			jrParam.setField("STLNO14"  ,sDCC03); 
			jrParam.setField("STLNO15"  ,sDCC04); 
			jrParam.setField("STLNO16"  ,sDCC05); 
			jrParam.setField("STLNO17"  ,sDCC06); 
			jrParam.setField("STLNO18"  ,sDCC07); 
			jrParam.setField("STLNO19"  ,sDCC08); 
			jrParam.setField("STLNO20"  ,sDCC09); 
			jrParam.setField("STLNO21"  ,sDCC10); 
			jrParam.setField("STLNO22"  ,sDCC11); 
			jrParam.setField("STLNO23"  ,sDSHC ); 

			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpTrackingSPMHFL", logId, mthdNm, "코일 SPM2 TrackIng정보 수정");

				
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일 SPM3 TrackIng 정보(Y5YDML022)
	 *		
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL022(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일 SPM3 TrackIng 정보[CCoilL2RcvSeEJB.rcvY5YDL022] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String sECC11   = commUtils.trim(rcvMsg.getFieldString("ECC11"));
			String sECC10   = commUtils.trim(rcvMsg.getFieldString("ECC10"));
			String sECC09   = commUtils.trim(rcvMsg.getFieldString("ECC09"));
			String sECC08   = commUtils.trim(rcvMsg.getFieldString("ECC08"));
			String sECC07   = commUtils.trim(rcvMsg.getFieldString("ECC07"));
			String sECC06   = commUtils.trim(rcvMsg.getFieldString("ECC06"));
			String sESHC    = commUtils.trim(rcvMsg.getFieldString("ESHC" ));
			String sPS      = commUtils.trim(rcvMsg.getFieldString("PS"   ));
			String sECC05   = commUtils.trim(rcvMsg.getFieldString("ECC05"));
			String sECC04   = commUtils.trim(rcvMsg.getFieldString("ECC04"));
			String sECC03   = commUtils.trim(rcvMsg.getFieldString("ECC03"));
			String sECC02   = commUtils.trim(rcvMsg.getFieldString("ECC02"));
			String sECC01   = commUtils.trim(rcvMsg.getFieldString("ECC01"));
			String sENT     = commUtils.trim(rcvMsg.getFieldString("ENT"  ));
			String sCR      = commUtils.trim(rcvMsg.getFieldString("CR"   ));
			String sPOR     = commUtils.trim(rcvMsg.getFieldString("POR"  ));
			String sMILL    = commUtils.trim(rcvMsg.getFieldString("MILL" ));
			String sTR      = commUtils.trim(rcvMsg.getFieldString("TR"   ));
			String sEXIT    = commUtils.trim(rcvMsg.getFieldString("EXIT" ));
			String sDCC01   = commUtils.trim(rcvMsg.getFieldString("DCC01"));
			String sDCC02   = commUtils.trim(rcvMsg.getFieldString("DCC02"));
			String sDCC03   = commUtils.trim(rcvMsg.getFieldString("DCC03"));
			String sDCC04   = commUtils.trim(rcvMsg.getFieldString("DCC04"));
			String sDCC05   = commUtils.trim(rcvMsg.getFieldString("DCC05"));
			String sDCC06   = commUtils.trim(rcvMsg.getFieldString("DCC06"));
			String sDCC07   = commUtils.trim(rcvMsg.getFieldString("DCC07"));
			String sDCC08   = commUtils.trim(rcvMsg.getFieldString("DCC08"));
			String sDCC09   = commUtils.trim(rcvMsg.getFieldString("DCC09"));
			String sDCC10   = commUtils.trim(rcvMsg.getFieldString("DCC10"));
			String sDCC11   = commUtils.trim(rcvMsg.getFieldString("DCC11"));
			String sDSHC    = commUtils.trim(rcvMsg.getFieldString("DSHC" ));

			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;

			/**********************************************************
			* 1. 조업 TRACKING UPDATE
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("EQP_CD"   ,"SPM3");
			
			jrParam.setField("STLNO1"   ,sECC11);
			jrParam.setField("STLNO2"   ,sECC10); 
			jrParam.setField("STLNO3"   ,sECC09); 
			jrParam.setField("STLNO4"   ,sECC08); 
			jrParam.setField("STLNO5"   ,sECC07); 
			jrParam.setField("STLNO6"   ,sECC06); 
			jrParam.setField("STLNO7"   ,sESHC ); 
			jrParam.setField("STLNO8"   ,sPS   ); 
			jrParam.setField("STLNO9"   ,sECC05); 
			jrParam.setField("STLNO10"  ,sECC04); 
			jrParam.setField("STLNO11"  ,sECC03); 
			jrParam.setField("STLNO12"  ,sECC02); 
			jrParam.setField("STLNO13"  ,sECC01); 
			jrParam.setField("STLNO14"  ,sENT  ); 
			jrParam.setField("STLNO15"  ,sCR   ); 
			jrParam.setField("STLNO16"  ,sPOR  ); 
			jrParam.setField("STLNO17"  ,sMILL ); 
			jrParam.setField("STLNO18"  ,sTR   ); 
			jrParam.setField("STLNO19"  ,sEXIT ); 
			jrParam.setField("STLNO20"  ,sDCC01); 
			jrParam.setField("STLNO21"  ,sDCC02); 
			jrParam.setField("STLNO22"  ,sDCC03); 
			jrParam.setField("STLNO23"  ,sDCC04);
			jrParam.setField("STLNO24"  ,sDCC05); 
			jrParam.setField("STLNO25"  ,sDCC06); 
			jrParam.setField("STLNO26"  ,sDCC07); 
			jrParam.setField("STLNO27"  ,sDCC08); 
			jrParam.setField("STLNO28"  ,sDCC09); 
			jrParam.setField("STLNO29"  ,sDCC10); 
			jrParam.setField("STLNO30"  ,sDCC11); 
			jrParam.setField("STLNO31"  ,sDSHC );

			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpTrackingSPMHFL", logId, mthdNm, "코일 SPM3 TrackIng정보 수정");

				
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일 SPM4 TrackIng 정보(Y5YDML023)
	 *		
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL023(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일 SPM4 TrackIng 정보[CCoilL2RcvSeEJB.rcvY5YDL023] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			                
			String sECC11   = commUtils.trim(rcvMsg.getFieldString("ECC11"));
			String sECC10   = commUtils.trim(rcvMsg.getFieldString("ECC10"));
			String sECC09   = commUtils.trim(rcvMsg.getFieldString("ECC09"));
			String sECC08   = commUtils.trim(rcvMsg.getFieldString("ECC08"));
			String sECC07   = commUtils.trim(rcvMsg.getFieldString("ECC07"));
			String sECC06   = commUtils.trim(rcvMsg.getFieldString("ECC06"));
			String sESHC    = commUtils.trim(rcvMsg.getFieldString("ESHC" ));
			String sPS      = commUtils.trim(rcvMsg.getFieldString("PS"   ));
			String sECC05   = commUtils.trim(rcvMsg.getFieldString("ECC05"));
			String sECC04   = commUtils.trim(rcvMsg.getFieldString("ECC04"));
			String sECC03   = commUtils.trim(rcvMsg.getFieldString("ECC03"));
			String sECC02   = commUtils.trim(rcvMsg.getFieldString("ECC02"));
			String sECC01   = commUtils.trim(rcvMsg.getFieldString("ECC01"));
			String sENT     = commUtils.trim(rcvMsg.getFieldString("ENT"  ));
			String sCR      = commUtils.trim(rcvMsg.getFieldString("CR"   ));
			String sPOR     = commUtils.trim(rcvMsg.getFieldString("POR"  ));
			String sMILL    = commUtils.trim(rcvMsg.getFieldString("MILL" ));
			String sTR      = commUtils.trim(rcvMsg.getFieldString("TR"   ));
			String sEXIT    = commUtils.trim(rcvMsg.getFieldString("EXIT" ));
			String sDCC01   = commUtils.trim(rcvMsg.getFieldString("DCC01"));
			String sDCC02   = commUtils.trim(rcvMsg.getFieldString("DCC02"));
			String sDCC03   = commUtils.trim(rcvMsg.getFieldString("DCC03"));
			String sDCC04   = commUtils.trim(rcvMsg.getFieldString("DCC04"));
			String sDCC05   = commUtils.trim(rcvMsg.getFieldString("DCC05"));
			String sDCC06   = commUtils.trim(rcvMsg.getFieldString("DCC06"));
			String sDCC07   = commUtils.trim(rcvMsg.getFieldString("DCC07"));
			String sDCC08   = commUtils.trim(rcvMsg.getFieldString("DCC08"));
			String sDCC09   = commUtils.trim(rcvMsg.getFieldString("DCC09"));
			String sDCC10   = commUtils.trim(rcvMsg.getFieldString("DCC10"));
			String sDCC11   = commUtils.trim(rcvMsg.getFieldString("DCC11"));
			String sDSHC    = commUtils.trim(rcvMsg.getFieldString("DSHC" ));

			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;

			/**********************************************************
			* 1. 조업 TRACKING UPDATE
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("EQP_CD"   ,"SPM4");
			
			jrParam.setField("STLNO1"   ,sECC11);
			jrParam.setField("STLNO2"   ,sECC10); 
			jrParam.setField("STLNO3"   ,sECC09); 
			jrParam.setField("STLNO4"   ,sECC08); 
			jrParam.setField("STLNO5"   ,sECC07); 
			jrParam.setField("STLNO6"   ,sECC06); 
			jrParam.setField("STLNO7"   ,sESHC ); 
			jrParam.setField("STLNO8"   ,sPS   ); 
			jrParam.setField("STLNO9"   ,sECC05); 
			jrParam.setField("STLNO10"  ,sECC04); 
			jrParam.setField("STLNO11"  ,sECC03); 
			jrParam.setField("STLNO12"  ,sECC02); 
			jrParam.setField("STLNO13"  ,sECC01); 
			jrParam.setField("STLNO14"  ,sENT  ); 
			jrParam.setField("STLNO15"  ,sCR   ); 
			jrParam.setField("STLNO16"  ,sPOR  ); 
			jrParam.setField("STLNO17"  ,sMILL ); 
			jrParam.setField("STLNO18"  ,sTR   ); 
			jrParam.setField("STLNO19"  ,sEXIT ); 
			jrParam.setField("STLNO20"  ,sDCC01); 
			jrParam.setField("STLNO21"  ,sDCC02); 
			jrParam.setField("STLNO22"  ,sDCC03); 
			jrParam.setField("STLNO23"  ,sDCC04);
			jrParam.setField("STLNO24"  ,sDCC05); 
			jrParam.setField("STLNO25"  ,sDCC06); 
			jrParam.setField("STLNO26"  ,sDCC07); 
			jrParam.setField("STLNO27"  ,sDCC08); 
			jrParam.setField("STLNO28"  ,sDCC09); 
			jrParam.setField("STLNO29"  ,sDCC10); 
			jrParam.setField("STLNO30"  ,sDCC11); 
			jrParam.setField("STLNO31"  ,sDSHC );

			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpTrackingSPMHFL", logId, mthdNm, "코일 SPM4 TrackIng정보 수정");

				
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일 SPM5 TrackIng 정보(Y5YDML024)
	 *		
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL024(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일 SPM5 TrackIng 정보[CCoilL2RcvSeEJB.rcvY5YDL024] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			                
			String sECC08    = commUtils.trim(rcvMsg.getFieldString("ECC08"));
			String sECC07    = commUtils.trim(rcvMsg.getFieldString("ECC07"));
			String sECC06    = commUtils.trim(rcvMsg.getFieldString("ECC06"));
			String sENT      = commUtils.trim(rcvMsg.getFieldString("ENT"));
			String sPOR      = commUtils.trim(rcvMsg.getFieldString("POR"));
			String sMILL     = commUtils.trim(rcvMsg.getFieldString("MILL"));
			String sTR       = commUtils.trim(rcvMsg.getFieldString("TR"));
			String sEXIT     = commUtils.trim(rcvMsg.getFieldString("EXIT"));
			String sECC05    = commUtils.trim(rcvMsg.getFieldString("ECC05"));
			String sECC04    = commUtils.trim(rcvMsg.getFieldString("ECC04"));
			String sECC03    = commUtils.trim(rcvMsg.getFieldString("ECC03"));
			String sECC02    = commUtils.trim(rcvMsg.getFieldString("ECC02"));
			String sECC01    = commUtils.trim(rcvMsg.getFieldString("ECC01"));
			String sTC       = commUtils.trim(rcvMsg.getFieldString("TC"));
			String sPS       = commUtils.trim(rcvMsg.getFieldString("PS"));
			String sDCC01    = commUtils.trim(rcvMsg.getFieldString("DCC01"));
			String sDCC02    = commUtils.trim(rcvMsg.getFieldString("DCC02"));
			String sDCC03    = commUtils.trim(rcvMsg.getFieldString("DCC03"));
			String sDCC04    = commUtils.trim(rcvMsg.getFieldString("DCC04"));
			String sDCC05    = commUtils.trim(rcvMsg.getFieldString("DCC05"));
			String sDCC06    = commUtils.trim(rcvMsg.getFieldString("DCC06"));
			String sDCC07    = commUtils.trim(rcvMsg.getFieldString("DCC07"));
			String sDCC08    = commUtils.trim(rcvMsg.getFieldString("DCC08"));
			String sDCC09    = commUtils.trim(rcvMsg.getFieldString("DCC09"));
			String sDCC10    = commUtils.trim(rcvMsg.getFieldString("DCC10"));
			String sDCC11    = commUtils.trim(rcvMsg.getFieldString("DCC11"));

			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;

			/**********************************************************
			* 1. 조업 TRACKING UPDATE
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("EQP_CD"   ,"SPM5");
			
			jrParam.setField("STLNO1"   , ""    );
			jrParam.setField("STLNO2"   , ""    );
			jrParam.setField("STLNO3"   , ""    );
			jrParam.setField("STLNO4"   , sECC08);
			jrParam.setField("STLNO5"   , sECC07);
			jrParam.setField("STLNO6"   , sECC06);
			
			String sAPP812_YN = coilDao.ApplyYn(logId, mthdNm, "APP812", "J", "*");
			
			if ("Y".equals(sAPP812_YN)) {
				//TO-BE
				jrParam.setField("STLNO7"   , sECC03);
				jrParam.setField("STLNO8"   , sECC02);
				jrParam.setField("STLNO9"   , sECC01);
				jrParam.setField("STLNO10"  , sTC   );
				jrParam.setField("STLNO11"  , ""    );
				jrParam.setField("STLNO12"  , sPS   );
				jrParam.setField("STLNO13"  , sENT  );
				jrParam.setField("STLNO14"  , sPOR  );
				jrParam.setField("STLNO15"  , sMILL );
				jrParam.setField("STLNO16"  , sTR   );
				jrParam.setField("STLNO17"  , sEXIT );
				jrParam.setField("STLNO18"  , sECC05);
				jrParam.setField("STLNO19"  , sECC04);
			} else {
				// AS-IS
				jrParam.setField("STLNO7"   , sENT  );//5
				jrParam.setField("STLNO8"   , sPOR  );//4
				jrParam.setField("STLNO9"   , sMILL );//3
				jrParam.setField("STLNO10"  , sTR   );//2
				jrParam.setField("STLNO11"  , ""    );//
				jrParam.setField("STLNO12"  , sEXIT );//1
				jrParam.setField("STLNO13"  , sECC05);//TC
				jrParam.setField("STLNO14"  , sECC04);//PS
				jrParam.setField("STLNO15"  , sECC03);//ENT
				jrParam.setField("STLNO16"  , sECC02);//POR
				jrParam.setField("STLNO17"  , sECC01);//MILL
				jrParam.setField("STLNO18"  , sTC   );//TR
				jrParam.setField("STLNO19"  , sPS   );//EXIT
			}
			
			jrParam.setField("STLNO20"  , ""    );
			jrParam.setField("STLNO21"  , ""    );
			jrParam.setField("STLNO22"  , ""    );
			jrParam.setField("STLNO23"  , ""    );
			jrParam.setField("STLNO24"  , sDCC01);
			jrParam.setField("STLNO25"  , sDCC02);
			jrParam.setField("STLNO26"  , sDCC03);
			jrParam.setField("STLNO27"  , sDCC04);
			jrParam.setField("STLNO28"  , sDCC05);
			jrParam.setField("STLNO29"  , sDCC06);
			jrParam.setField("STLNO30"  , sDCC07);
			jrParam.setField("STLNO31"  , sDCC08);
			jrParam.setField("STLNO32"  , sDCC09);
			jrParam.setField("STLNO33"  , sDCC10);
			jrParam.setField("STLNO34"  , sDCC11);

			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpTrackingSPMHFL", logId, mthdNm, "코일 SPM5 TrackIng정보 수정");

				
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	

	/**
	 *      [A] 오퍼레이션명 : 코일 HFL1 TrackIng 정보(Y5YDML025)
	 *		
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL025(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일 HFL1 TrackIng 정보[CCoilL2RcvSeEJB.rcvY5YDL025] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String sECC06   = commUtils.trim(rcvMsg.getFieldString("ECC06"));
			String sECC05   = commUtils.trim(rcvMsg.getFieldString("ECC05"));
			String sECC04   = commUtils.trim(rcvMsg.getFieldString("ECC04"));
			String sECC03   = commUtils.trim(rcvMsg.getFieldString("ECC03"));
			String sECC02   = commUtils.trim(rcvMsg.getFieldString("ECC02"));
			String sECC01   = commUtils.trim(rcvMsg.getFieldString("ECC01"));
			String sPOR     = commUtils.trim(rcvMsg.getFieldString("POR"  ));
			String sDCC01   = commUtils.trim(rcvMsg.getFieldString("DCC01"));
			String sDCC02   = commUtils.trim(rcvMsg.getFieldString("DCC02"));
			String sDCC03   = commUtils.trim(rcvMsg.getFieldString("DCC03"));
			String sDCC04   = commUtils.trim(rcvMsg.getFieldString("DCC04"));
			String sDCC05   = commUtils.trim(rcvMsg.getFieldString("DCC05"));
			String sDCC06   = commUtils.trim(rcvMsg.getFieldString("DCC06"));
			String sDCC07   = commUtils.trim(rcvMsg.getFieldString("DCC07"));
			String sDCC08   = commUtils.trim(rcvMsg.getFieldString("DCC08"));
			String sDCC09   = commUtils.trim(rcvMsg.getFieldString("DCC09"));
			String sDCC10   = commUtils.trim(rcvMsg.getFieldString("DCC10"));
			String sDCC11   = commUtils.trim(rcvMsg.getFieldString("DCC11"));
			String sSHC1    = commUtils.trim(rcvMsg.getFieldString("SHC1" ));
			String sSHC2    = commUtils.trim(rcvMsg.getFieldString("SHC2" ));
			String sSHC3    = commUtils.trim(rcvMsg.getFieldString("SHC3" ));
			String sCC      = commUtils.trim(rcvMsg.getFieldString("CC"   ));

			
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;

			/**********************************************************
			* 1. 조업 TRACKING UPDATE
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("EQP_CD"   ,"HFL1");
			
			jrParam.setField("STLNO1"   , sECC06);
			jrParam.setField("STLNO2"   , sECC05); 
			jrParam.setField("STLNO3"   , sECC04); 
			jrParam.setField("STLNO4"   , sECC03); 
			jrParam.setField("STLNO5"   , sECC02); 
			jrParam.setField("STLNO6"   , sECC01); 
			jrParam.setField("STLNO7"   , sPOR  ); 
			jrParam.setField("STLNO8"   , sDCC01); 
			jrParam.setField("STLNO9"   , sDCC02); 
			jrParam.setField("STLNO10"  , sDCC03); 
			jrParam.setField("STLNO11"  , sDCC04); 
			jrParam.setField("STLNO12"  , sDCC05); 
			jrParam.setField("STLNO13"  , sDCC06); 
			jrParam.setField("STLNO14"  , sDCC07); 
			jrParam.setField("STLNO15"  , sDCC08); 
			jrParam.setField("STLNO16"  , sDCC09); 
			jrParam.setField("STLNO17"  , sDCC10); 
			jrParam.setField("STLNO18"  , sDCC11); 
			jrParam.setField("STLNO19"  , sSHC1 ); 
			jrParam.setField("STLNO20"  , sSHC2 ); 
			jrParam.setField("STLNO21"  , sSHC3 ); 
			jrParam.setField("STLNO22"  , sCC   ); 

			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpTrackingSPMHFL", logId, mthdNm, "코일 HFL1 Tracking정보 수정");
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 코일 HFL4 TrackIng 정보(Y5YDML026)
	 *		
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL026(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일 HFL4 TrackIng 정보[CCoilL2RcvSeEJB.rcvY5YDL026] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String sECC04   = commUtils.trim(rcvMsg.getFieldString("ECC04"));
			String sECC03   = commUtils.trim(rcvMsg.getFieldString("ECC03"));
			String sECC02   = commUtils.trim(rcvMsg.getFieldString("ECC02"));
			String sECC01   = commUtils.trim(rcvMsg.getFieldString("ECC01"));
			String sDCC01   = commUtils.trim(rcvMsg.getFieldString("DCC01"));
			String sDCC02   = commUtils.trim(rcvMsg.getFieldString("DCC02"));
			String sDCC03   = commUtils.trim(rcvMsg.getFieldString("DCC03"));
			String sDCC04   = commUtils.trim(rcvMsg.getFieldString("DCC04"));
			String sDCC05   = commUtils.trim(rcvMsg.getFieldString("DCC05"));
			String sDCC06   = commUtils.trim(rcvMsg.getFieldString("DCC06"));
			String sDCC07   = commUtils.trim(rcvMsg.getFieldString("DCC07"));
			String sDCC08   = commUtils.trim(rcvMsg.getFieldString("DCC08"));
			String sDCC09   = commUtils.trim(rcvMsg.getFieldString("DCC09"));
			
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;

			/**********************************************************
			* 1. 조업 TRACKING UPDATE
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("EQP_CD"   ,"HFL4");
			
			jrParam.setField("STLNO1"   ,sECC04);
			jrParam.setField("STLNO2"   ,sECC03); 
			jrParam.setField("STLNO3"   ,sECC02); 
			jrParam.setField("STLNO4"   ,sECC01); 
			jrParam.setField("STLNO5"   ,sDCC01); 
			jrParam.setField("STLNO6"   ,sDCC02); 
			jrParam.setField("STLNO7"   ,sDCC03); 
			jrParam.setField("STLNO8"   ,sDCC04); 
			jrParam.setField("STLNO9"   ,sDCC05); 
			jrParam.setField("STLNO10"  ,sDCC06); 
			jrParam.setField("STLNO11"  ,sDCC07); 
			jrParam.setField("STLNO12"  ,sDCC08); 
			jrParam.setField("STLNO13"  ,sDCC09); 

			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpTrackingSPMHFL", logId, mthdNm, "코일 HFL4 Tracking정보 수정");

			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일 컨베이어 TrackIng 정보1(Y5YDML027)
	 *		
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL027(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일 컨베이어ABC TrackIng 정보[CCoilL2RcvSeEJB.rcvY5YDL027] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String sCVC_01  = commUtils.trim(rcvMsg.getFieldString("CVC-01"));
			String sCVC_02  = commUtils.trim(rcvMsg.getFieldString("CVC-02"));
			String sCVC_03  = commUtils.trim(rcvMsg.getFieldString("CVC-03"));
			String sCVC_04  = commUtils.trim(rcvMsg.getFieldString("CVC-04"));
			String sCVC_05  = commUtils.trim(rcvMsg.getFieldString("CVC-05"));
			String sCVC_06  = commUtils.trim(rcvMsg.getFieldString("CVC-06"));
			String sCVC_07  = commUtils.trim(rcvMsg.getFieldString("CVC-07"));
			String sCVC_08  = commUtils.trim(rcvMsg.getFieldString("CVC-08"));
			String sCVC_09  = commUtils.trim(rcvMsg.getFieldString("CVC-09"));
			String sCVC_10  = commUtils.trim(rcvMsg.getFieldString("CVC-10"));
			String sCVC_11  = commUtils.trim(rcvMsg.getFieldString("CVC-11"));
			String sCVC_12  = commUtils.trim(rcvMsg.getFieldString("CVC-12"));
			String sCVB_01  = commUtils.trim(rcvMsg.getFieldString("CVB-01"));
			String sCVB_02  = commUtils.trim(rcvMsg.getFieldString("CVB-02"));
			String sCVB_03  = commUtils.trim(rcvMsg.getFieldString("CVB-03"));
			String sCVB_04  = commUtils.trim(rcvMsg.getFieldString("CVB-04"));
			String sCVB_05  = commUtils.trim(rcvMsg.getFieldString("CVB-05"));
			String sCVB_06  = commUtils.trim(rcvMsg.getFieldString("CVB-06"));
			String sCVB_07  = commUtils.trim(rcvMsg.getFieldString("CVB-07"));
			String sCVB_08  = commUtils.trim(rcvMsg.getFieldString("CVB-08"));
			String sCVB_09  = commUtils.trim(rcvMsg.getFieldString("CVB-09"));
			String sCVB_10  = commUtils.trim(rcvMsg.getFieldString("CVB-10"));
			String sCVB_11  = commUtils.trim(rcvMsg.getFieldString("CVB-11"));
			String sCVB_12  = commUtils.trim(rcvMsg.getFieldString("CVB-12"));
			String sCVA_01  = commUtils.trim(rcvMsg.getFieldString("CVA-01"));
			String sCVA_02  = commUtils.trim(rcvMsg.getFieldString("CVA-02"));
			String sCVA_03  = commUtils.trim(rcvMsg.getFieldString("CVA-03"));
			String sCVA_04  = commUtils.trim(rcvMsg.getFieldString("CVA-04"));
			String sCVA_05  = commUtils.trim(rcvMsg.getFieldString("CVA-05"));
			String sCVA_06  = commUtils.trim(rcvMsg.getFieldString("CVA-06"));
			String sCVA_07  = commUtils.trim(rcvMsg.getFieldString("CVA-07"));
			String sCVA_08  = commUtils.trim(rcvMsg.getFieldString("CVA-08"));
			String sCVA_09  = commUtils.trim(rcvMsg.getFieldString("CVA-09"));
			String sCVA_10  = commUtils.trim(rcvMsg.getFieldString("CVA-10"));
			String sCVA_11  = commUtils.trim(rcvMsg.getFieldString("CVA-11"));
			String sCVA_12  = commUtils.trim(rcvMsg.getFieldString("CVA-12"));
			
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;

			/**********************************************************
			* 1. 조업 TRACKING UPDATE
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("EQP_CD"   ,"CVC");
			jrParam.setField("STLNO1"   , sCVC_01);
			jrParam.setField("STLNO2"   , sCVC_02); 
			jrParam.setField("STLNO3"   , sCVC_03); 
			jrParam.setField("STLNO4"   , sCVC_04); 
			jrParam.setField("STLNO5"   , sCVC_05); 
			jrParam.setField("STLNO6"   , sCVC_06); 
			jrParam.setField("STLNO7"   , sCVC_07); 
			jrParam.setField("STLNO8"   , sCVC_08); 
			jrParam.setField("STLNO9"   , sCVC_09); 
			jrParam.setField("STLNO10"  , sCVC_10); 
			jrParam.setField("STLNO11"  , sCVC_11); 
			jrParam.setField("STLNO12"  , sCVC_12);
			
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpTrackingCV", logId, mthdNm, "코일 컨베이어C Tracking정보 수정");
			
			jrParam.setField("EQP_CD"   ,"CVB");
			jrParam.setField("STLNO1"   , sCVB_01);
			jrParam.setField("STLNO2"   , sCVB_02); 
			jrParam.setField("STLNO3"   , sCVB_03); 
			jrParam.setField("STLNO4"   , sCVB_04); 
			jrParam.setField("STLNO5"   , sCVB_05); 
			jrParam.setField("STLNO6"   , sCVB_06); 
			jrParam.setField("STLNO7"   , sCVB_07); 
			jrParam.setField("STLNO8"   , sCVB_08); 
			jrParam.setField("STLNO9"   , sCVB_09); 
			jrParam.setField("STLNO10"  , sCVB_10); 
			jrParam.setField("STLNO11"  , sCVB_11); 
			jrParam.setField("STLNO12"  , sCVB_12);
			
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpTrackingCV", logId, mthdNm, "코일 컨베이어B Tracking정보 수정");
			
			jrParam.setField("EQP_CD"   ,"CVA");
			jrParam.setField("STLNO1"   , sCVA_01);
			jrParam.setField("STLNO2"   , sCVA_02); 
			jrParam.setField("STLNO3"   , sCVA_03); 
			jrParam.setField("STLNO4"   , sCVA_04); 
			jrParam.setField("STLNO5"   , sCVA_05); 
			jrParam.setField("STLNO6"   , sCVA_06); 
			jrParam.setField("STLNO7"   , sCVA_07); 
			jrParam.setField("STLNO8"   , sCVA_08); 
			jrParam.setField("STLNO9"   , sCVA_09); 
			jrParam.setField("STLNO10"  , sCVA_10); 
			jrParam.setField("STLNO11"  , sCVA_11); 
			jrParam.setField("STLNO12"  , sCVA_12); 

			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpTrackingCV", logId, mthdNm, "코일 컨베이어A Tracking정보 수정");

				
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일 컨베이어 TrackIng 정보2(Y5YDML028)
	 *		
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL028(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일 컨베이어DEFGH TrackIng 정보[CCoilL2RcvSeEJB.rcvY5YDL028] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String sWBF_01  = commUtils.trim(rcvMsg.getFieldString("WBF-01"));
			String sWBF_02  = commUtils.trim(rcvMsg.getFieldString("WBF-02"));
			String sWBF_03  = commUtils.trim(rcvMsg.getFieldString("WBF-03"));
			String sWBF_04  = commUtils.trim(rcvMsg.getFieldString("WBF-04"));
			String sWBF_05  = commUtils.trim(rcvMsg.getFieldString("WBF-05"));
			String sWBF_06  = commUtils.trim(rcvMsg.getFieldString("WBF-06"));
			String sWBF_07  = commUtils.trim(rcvMsg.getFieldString("WBF-07"));
			String sWBF_08  = commUtils.trim(rcvMsg.getFieldString("WBF-08"));
			String sWBF_09  = commUtils.trim(rcvMsg.getFieldString("WBF-09"));
			String sWBF_10  = commUtils.trim(rcvMsg.getFieldString("WBF-10"));
			String sWBF_11  = commUtils.trim(rcvMsg.getFieldString("WBF-11"));
			String sWBF_12  = commUtils.trim(rcvMsg.getFieldString("WBF-12"));
			String sCVH_01  = commUtils.trim(rcvMsg.getFieldString("CVH-01"));
			String sCVH_02  = commUtils.trim(rcvMsg.getFieldString("CVH-02"));
			String sCVH_03  = commUtils.trim(rcvMsg.getFieldString("CVH-03"));
			String sCVH_04  = commUtils.trim(rcvMsg.getFieldString("CVH-04"));
			String sCVH_05  = commUtils.trim(rcvMsg.getFieldString("CVH-05"));
			String sCVH_06  = commUtils.trim(rcvMsg.getFieldString("CVH-06"));
			String sCVH_07  = commUtils.trim(rcvMsg.getFieldString("CVH-07"));
			String sCVH_08  = commUtils.trim(rcvMsg.getFieldString("CVH-08"));
			String sCVH_09  = commUtils.trim(rcvMsg.getFieldString("CVH-09"));
			String sCVH_10  = commUtils.trim(rcvMsg.getFieldString("CVH-10"));
			String sCVH_11  = commUtils.trim(rcvMsg.getFieldString("CVH-11"));
			String sCVH_12  = commUtils.trim(rcvMsg.getFieldString("CVH-12"));
			String sCVG_01  = commUtils.trim(rcvMsg.getFieldString("CVG-01"));
			String sCVG_02  = commUtils.trim(rcvMsg.getFieldString("CVG-02"));
			String sCVG_03  = commUtils.trim(rcvMsg.getFieldString("CVG-03"));
			String sCVG_04  = commUtils.trim(rcvMsg.getFieldString("CVG-04"));
			String sCVG_05  = commUtils.trim(rcvMsg.getFieldString("CVG-05"));
			String sCVG_06  = commUtils.trim(rcvMsg.getFieldString("CVG-06"));
			String sCVG_07  = commUtils.trim(rcvMsg.getFieldString("CVG-07"));
			String sCVG_08  = commUtils.trim(rcvMsg.getFieldString("CVG-08"));
			String sCVG_09  = commUtils.trim(rcvMsg.getFieldString("CVG-09"));
			String sCVG_10  = commUtils.trim(rcvMsg.getFieldString("CVG-10"));
			String sCVG_11  = commUtils.trim(rcvMsg.getFieldString("CVG-11"));
			String sCVG_12  = commUtils.trim(rcvMsg.getFieldString("CVG-12"));
			String sCVE_01  = commUtils.trim(rcvMsg.getFieldString("CVE-01"));
			String sCVE_02  = commUtils.trim(rcvMsg.getFieldString("CVE-02"));
			String sCVE_03  = commUtils.trim(rcvMsg.getFieldString("CVE-03"));
			String sCVE_04  = commUtils.trim(rcvMsg.getFieldString("CVE-04"));
			String sCVE_05  = commUtils.trim(rcvMsg.getFieldString("CVE-05"));
			String sCVE_06  = commUtils.trim(rcvMsg.getFieldString("CVE-06"));
			String sCVE_07  = commUtils.trim(rcvMsg.getFieldString("CVE-07"));
			String sCVE_08  = commUtils.trim(rcvMsg.getFieldString("CVE-08"));
			String sCVE_09  = commUtils.trim(rcvMsg.getFieldString("CVE-09"));
			String sCVE_10  = commUtils.trim(rcvMsg.getFieldString("CVE-10"));
			String sCVE_11  = commUtils.trim(rcvMsg.getFieldString("CVE-11"));
			String sCVE_12  = commUtils.trim(rcvMsg.getFieldString("CVE-12"));
			String sCVD_01  = commUtils.trim(rcvMsg.getFieldString("CVD-01"));
			String sCVD_02  = commUtils.trim(rcvMsg.getFieldString("CVD-02"));
			String sCVD_03  = commUtils.trim(rcvMsg.getFieldString("CVD-03"));
			String sCVD_04  = commUtils.trim(rcvMsg.getFieldString("CVD-04"));
			String sCVD_05  = commUtils.trim(rcvMsg.getFieldString("CVD-05"));
			String sCVD_06  = commUtils.trim(rcvMsg.getFieldString("CVD-06"));
			String sCVD_07  = commUtils.trim(rcvMsg.getFieldString("CVD-07"));
			String sCVD_08  = commUtils.trim(rcvMsg.getFieldString("CVD-08"));
			String sCVD_09  = commUtils.trim(rcvMsg.getFieldString("CVD-09"));
			String sCVD_10  = commUtils.trim(rcvMsg.getFieldString("CVD-10"));
			String sCVD_11  = commUtils.trim(rcvMsg.getFieldString("CVD-11"));
			String sCVD_12  = commUtils.trim(rcvMsg.getFieldString("CVD-12"));
			
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;

			/**********************************************************
			* 1. 조업 TRACKING UPDATE
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("EQP_CD"   ,"WBF");
			jrParam.setField("STLNO1"   , sWBF_01);
			jrParam.setField("STLNO2"   , sWBF_02); 
			jrParam.setField("STLNO3"   , sWBF_03); 
			jrParam.setField("STLNO4"   , sWBF_04); 
			jrParam.setField("STLNO5"   , sWBF_05); 
			jrParam.setField("STLNO6"   , sWBF_06); 
			jrParam.setField("STLNO7"   , sWBF_07); 
			jrParam.setField("STLNO8"   , sWBF_08); 
			jrParam.setField("STLNO9"   , sWBF_09); 
			jrParam.setField("STLNO10"  , sWBF_10); 
			jrParam.setField("STLNO11"  , sWBF_11); 
			jrParam.setField("STLNO12"  , sWBF_12);
			
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpTrackingCV", logId, mthdNm, "코일 컨베이어F Tracking정보 수정");
			
			jrParam.setField("EQP_CD"   ,"CVH");
			jrParam.setField("STLNO1"   , sCVH_01);
			jrParam.setField("STLNO2"   , sCVH_02); 
			jrParam.setField("STLNO3"   , sCVH_03); 
			jrParam.setField("STLNO4"   , sCVH_04); 
			jrParam.setField("STLNO5"   , sCVH_05); 
			jrParam.setField("STLNO6"   , sCVH_06); 
			jrParam.setField("STLNO7"   , sCVH_07); 
			jrParam.setField("STLNO8"   , sCVH_08); 
			jrParam.setField("STLNO9"   , sCVH_09); 
			jrParam.setField("STLNO10"  , sCVH_10); 
			jrParam.setField("STLNO11"  , sCVH_11); 
			jrParam.setField("STLNO12"  , sCVH_12);
			
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpTrackingCV", logId, mthdNm, "코일 컨베이어H Tracking정보 수정");
			
			jrParam.setField("EQP_CD"   ,"CVG");
			jrParam.setField("STLNO1"   , sCVG_01);
			jrParam.setField("STLNO2"   , sCVG_02); 
			jrParam.setField("STLNO3"   , sCVG_03); 
			jrParam.setField("STLNO4"   , sCVG_04); 
			jrParam.setField("STLNO5"   , sCVG_05); 
			jrParam.setField("STLNO6"   , sCVG_06); 
			jrParam.setField("STLNO7"   , sCVG_07); 
			jrParam.setField("STLNO8"   , sCVG_08); 
			jrParam.setField("STLNO9"   , sCVG_09); 
			jrParam.setField("STLNO10"  , sCVG_10); 
			jrParam.setField("STLNO11"  , sCVG_11); 
			jrParam.setField("STLNO12"  , sCVG_12); 

			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpTrackingCV", logId, mthdNm, "코일 컨베이어G Tracking정보 수정");
			
			jrParam.setField("EQP_CD"   ,"CVE");
			jrParam.setField("STLNO1"   , sCVE_01);
			jrParam.setField("STLNO2"   , sCVE_02); 
			jrParam.setField("STLNO3"   , sCVE_03); 
			jrParam.setField("STLNO4"   , sCVE_04); 
			jrParam.setField("STLNO5"   , sCVE_05); 
			jrParam.setField("STLNO6"   , sCVE_06); 
			jrParam.setField("STLNO7"   , sCVE_07); 
			jrParam.setField("STLNO8"   , sCVE_08); 
			jrParam.setField("STLNO9"   , sCVE_09); 
			jrParam.setField("STLNO10"  , sCVE_10); 
			jrParam.setField("STLNO11"  , sCVE_11); 
			jrParam.setField("STLNO12"  , sCVE_12);
			
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpTrackingCV", logId, mthdNm, "코일 컨베이어E Tracking정보 수정");
			
			jrParam.setField("EQP_CD"   ,"CVD");
			jrParam.setField("STLNO1"   , sCVD_01);
			jrParam.setField("STLNO2"   , sCVD_02); 
			jrParam.setField("STLNO3"   , sCVD_03); 
			jrParam.setField("STLNO4"   , sCVD_04); 
			jrParam.setField("STLNO5"   , sCVD_05); 
			jrParam.setField("STLNO6"   , sCVD_06); 
			jrParam.setField("STLNO7"   , sCVD_07); 
			jrParam.setField("STLNO8"   , sCVD_08); 
			jrParam.setField("STLNO9"   , sCVD_09); 
			jrParam.setField("STLNO10"  , sCVD_10); 
			jrParam.setField("STLNO11"  , sCVD_11); 
			jrParam.setField("STLNO12"  , sCVD_12); 

			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEqpTrackingCV", logId, mthdNm, "코일 컨베이어D Tracking정보 수정");

				
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 분동코일작업지시요구(Y5YDL029)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL029(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "CICD분동코일작업지시요구[CCoilL2RcvSeEJB.rcvY5YDL029] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = commUtils.getParam(logId, mthdNm, "Y5YDL029");	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId          = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sCoilNo        = commUtils.trim(rcvMsg.getFieldString("COIL_NO"               )); //코일번호               
			String ydUpWoLoc      = commUtils.trim(rcvMsg.getFieldString("YD_UP_WO_LOC"          )); //야드권상지시위치
			String ydUpWoLayer    = commUtils.trim(rcvMsg.getFieldString("YD_UP_WO_LAYER"        )); //야드권상지시단
			String ydUpWoLocXaxis = commUtils.trim(rcvMsg.getFieldString("YD_UP_WO_LOC_XAXIS"    )); //야드권상지시X축
			String ydUpWoLocYaxis = commUtils.trim(rcvMsg.getFieldString("YD_UP_WO_LOC_YAXIS"    )); //야드권상지시Y축
			String ydUpWoLocZaxis = commUtils.trim(rcvMsg.getFieldString("YD_UP_WO_LOC_ZAXIS"    )); //야드권상지시Z축
			// 2025.12.22 RITM1543970 야드권상지시Z축부호 -> 미사용 항목 재활용 : 권상회전 각도 1
			String sYD_UP_WO_LOC_ZAXIS_SYM = commUtils.trim(rcvMsg.getFieldString("YD_UP_WO_LOC_ZAXIS_SYM")); //야드권상지시Z축부호
			String ydDnWoLoc      = commUtils.trim(rcvMsg.getFieldString("YD_DN_WO_LOC"          )); //야드권하지시위치
			String ydDnWoLayer    = commUtils.trim(rcvMsg.getFieldString("YD_DN_WO_LAYER"        )); //야드권하지시단
			String ydDnWoLocXaxis = commUtils.trim(rcvMsg.getFieldString("YD_DN_WO_LOC_XAXIS"    )); //야드권하지시X축
			String ydDnWoLocYaxis = commUtils.trim(rcvMsg.getFieldString("YD_DN_WO_LOC_YAXIS"    )); //야드권하지시Y축
			String ydDnWoLocZaxis = commUtils.trim(rcvMsg.getFieldString("YD_DN_WO_LOC_ZAXIS"    )); //야드권하지시Z축
			// 2025.12.22 RITM1543970 야드권하지시Z축부호 -> 미사용 항목 재활용 : 권하회전 각도 3
			String sYD_DN_WO_LOC_ZAXIS_SYM = commUtils.trim(rcvMsg.getFieldString("YD_DN_WO_LOC_ZAXIS_SYM")); //야드권하지시Z축부호
			String ydStlWt        = commUtils.trim(rcvMsg.getFieldString("YD_STL_WT"             )); //야드재료중량
			String ydStlW_T       = commUtils.trim(rcvMsg.getFieldString("YD_STL_T"              )); //야드재료두께
			String ydStlW_W       = commUtils.trim(rcvMsg.getFieldString("YD_STL_W"              )); //야드재료폭
			String sCoilOutDia    = commUtils.trim(rcvMsg.getFieldString("COIL_OUTDIA"           )); //Coil외경
			String sCoilInDia     = commUtils.trim(rcvMsg.getFieldString("COIL_INDIA"            )); //Coil내경
			String sModifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }

			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sCoilNo)) {
				throw new Exception("분동코일ID 이상");
			} 
			if (ydUpWoLoc.length() != 8) {
				throw new Exception("권상논리좌표 이상");
			}
			
			// 2025.12.22 RITM1543970 분동코일이 아닌 경우 회전지시로 인식 ( 권상위치 = 권하위치 )
			if (!"B".equals(sCoilNo.substring(0, 1))) {
				ydDnWoLoc      = ydUpWoLoc; //야드권하지시위치
				ydDnWoLayer    = ydUpWoLayer; //야드권하지시단
				ydDnWoLocXaxis = ydUpWoLocXaxis; //야드권하지시X축
				ydDnWoLocYaxis = ydUpWoLocYaxis; //야드권하지시Y축
				ydDnWoLocZaxis = ydUpWoLocZaxis; //야드권하지시Z축
				sYD_UP_WO_LOC_ZAXIS_SYM = "1";
				sYD_DN_WO_LOC_ZAXIS_SYM = "3";
			}
			
			
			String ydGp    = ydUpWoLoc.substring(0, 1);
			String ydBayGp = ydUpWoLoc.substring(1, 2);
//			ydUpWoLayer    = ydUpWoLayer.substring(0, 3); 
//			ydDnWoLayer    = ydDnWoLayer.substring(0, 3);

			String ydSchCd = "J" + ydBayGp + "YD99MH"; //TODO 추후 분동코일 이적 스케줄 바꿔야 함
	

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("STL_NO"         , sCoilNo     ); //재료번호
			jrParam.setField("YD_SCH_CD"      , ydSchCd     ); //분동코일 이적
			jrParam.setField("DTL_ITEM1"      , sCoilNo     );
			jrParam.setField("DTL_ITEM2"      , ydStlWt     );
			jrParam.setField("DTL_ITEM3"      , ydStlW_T    );
			jrParam.setField("DTL_ITEM4"      , ydStlW_W    );
			jrParam.setField("DTL_ITEM5"      , sCoilOutDia );
			jrParam.setField("DTL_ITEM6"      , sCoilInDia  );

			
			/*********************************************************
			 * 1.크레인 선택
			 **********************************************************/
			/*
			SELECT SR.YD_WRK_CRN
			     , SR.YD_WRK_CRN_PRIOR
			     , E1.YD_EQP_STAT      AS WRK_WPROG_STAT       --작업크레인 야드설비상태 B
			     , E1.YD_EQP_WRK_MODE  AS WRK_WORK_MODE        --작업크레인 야드설비작업Mode  1 ONLINE 2 OFFLINE   
			     , SR.YD_ALT_CRN
			     , E2.YD_EQP_STAT      AS ALT_WPROG_STAT       --대체크레인 야드설비상태
			     , E2.YD_EQP_WRK_MODE  AS ALT_WORK_MODE        --대체크레인 야드설비작업Mode     
			  FROM TB_YD_SCHRULE  SR
			     , TB_YD_EQP      E1
			     , TB_YD_EQP      E2
			 WHERE YD_SCH_CD     = :V_YD_SCH_CD
			   AND SR.YD_WRK_CRN = E1.YD_EQP_ID(+)
			   AND SR.YD_ALT_CRN = E2.YD_EQP_ID(+)
			   AND SR.DEL_YN     = 'N'
			   AND E1.DEL_YN(+)  = 'N'
			   AND E2.DEL_YN(+)  = 'N'
			 */
			JDTORecordSet jrEqpInfo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getWrkCrnForBC", logId, mthdNm, "크레인 조회");
			if (jrEqpInfo.size() <= 0) {
				throw new Exception("크레인 정보 이상");
			}
			
			String ydWrkCrnPrior   = jrEqpInfo.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR");//작업우선순위
			String ydWrkCrn        = jrEqpInfo.getRecord(0).getFieldString("YD_WRK_CRN"    ); //
			String sWrkWprogStat   = jrEqpInfo.getRecord(0).getFieldString("WRK_WPROG_STAT"); //
			String sWrkWorkMode    = jrEqpInfo.getRecord(0).getFieldString("WRK_WORK_MODE" ); //
			String ydAltCrn        = jrEqpInfo.getRecord(0).getFieldString("YD_ALT_CRN"    ); //
			String sAltWprogStat   = jrEqpInfo.getRecord(0).getFieldString("ALT_WPROG_STAT"); //
			String saltWorkMode    = jrEqpInfo.getRecord(0).getFieldString("ALT_WORK_MODE" ); //
			String ydEqpId         = ydWrkCrn;
			String sEQP_STAT       = sWrkWprogStat;
			
			if ("B".equals(sWrkWprogStat) || "2".equals(sWrkWorkMode)) {
				if (!"".equals(ydAltCrn) && !"B".equals(sAltWprogStat) && !"2".equals(saltWorkMode)) {
					ydEqpId   = ydAltCrn;
					sEQP_STAT = sAltWprogStat;
				}
			}
			
			/**********************************************************
			* 동일분동코일 작업스케줄 있을 시에 스케줄 생성안함
			**********************************************************/
			/*
			SELECT CS.*
			  FROM TB_YD_CRNSCH    CS
			     , TB_YD_CRNWRKMTL CM
			 WHERE 1 = 1
			   AND CS.YD_SCH_CD     = :V_YD_SCH_CD
			   AND CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
			   AND CS.DEL_YN = 'N'
			   AND CM.DEL_YN = 'N'
			   AND CM.STL_NO = :V_STL_NO
			 */
			JDTORecordSet jsBDSchlist = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getBDSchList", logId, mthdNm, "분동스케줄 조회");
			if (jsBDSchlist.size() > 0) {
				throw new Exception("해당 분동코일["+sCoilNo+"] 스케줄 존재함");
			}
			
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updBDCoilInfo 
			MERGE INTO TB_YD_RULE R USING (
			SELECT :V_DTL_ITM1 AS DTL_ITEM1  -- COIL_NO
			     , :V_DTL_ITM2 AS DTL_ITEM2
			     , :V_DTL_ITM3 AS DTL_ITEM3
			     , :V_DTL_ITM4 AS DTL_ITEM4
			     , :V_DTL_ITM5 AS DTL_ITEM5
			     , :V_DTL_ITM6 AS DTL_ITEM6
			     , 'BDCOIL'    AS REPR_CD_GP
			     , 'J'         AS CD_GP
			     , (SELECT COUNT(*) + 1 FROM TB_YD_RULE WHERE REPR_CD_GP = 'BDCOIL' AND CD_GP = 'J') AS SEQ 
			  FROM DUAL
			 ) DD ON (R.DTL_ITEM1 = DD.DTL_ITEM1 AND R.REPR_CD_GP = DD.REPR_CD_GP AND R.CD_GP = DD.CD_GP)
			 WHEN NOT MATCHED THEN
			    INSERT (
			             REPR_CD_GP   , CD_GP        , ITEM         , REPR_CD_CONTENTS
			           , DTL_ITEM1    , DTL_ITEM2    , DTL_ITEM3    , DTL_ITEM4    , DTL_ITEM5   , DTL_ITEM6)
			    VALUES ( DD.REPR_CD_GP, DD.CD_GP     , DD.SEQ       , '분동코일'
			           , DD.DTL_ITEM1 , DD.DTL_ITEM2 , DD.DTL_ITEM3 , DD.DTL_ITEM4 , DD.DTL_ITEM5, DD.DTL_ITEM6)
			 WHEN MATCHED THEN UPDATE SET
			      R.MODIFIER = 'Y5YDL029'
			    , R.MOD_DDTT = SYSDATE
			    , R.DTL_ITEM2 = DD.DTL_ITEM2
			    , R.DTL_ITEM3 = DD.DTL_ITEM3
			    , R.DTL_ITEM4 = DD.DTL_ITEM4
			    , R.DTL_ITEM5 = DD.DTL_ITEM5    
			    , R.DTL_ITEM6 = DD.DTL_ITEM6
			 */
			// 2025.12.22 RITM1543970 분동코일이 아닌 경우 회전지시로 인식
			// 분동코일인 경우에만 분동코일 정보 수정
			if ("B".equals(sCoilNo.substring(0, 1))) {
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updBDCoilInfo", logId, mthdNm, "분동코일 정보 수정");
			}
			
			
			/**********************************************************
			* 2. 작업예약 생성
			**********************************************************/
			String ydWbookId = coilDao.getSeqId(logId, mthdNm, "WrkBook");
			
			/**********************************************************
			*  3.크레인 스케줄 등록
			**********************************************************/
			JDTORecord jrCrnSch = commUtils.getParam(logId, mthdNm, sModifier);

			String ydCrnSchId = coilDao.getSeqId(logId, mthdNm, "CrnSch");

			jrCrnSch.setField("YD_CRN_SCH_ID"         , ydCrnSchId);
			jrCrnSch.setField("REGISTER"              , sModifier);
			jrCrnSch.setField("YD_WBOOK_ID"           , ydWbookId);
			jrCrnSch.setField("YD_EQP_ID"             , ydEqpId);
			jrCrnSch.setField("YD_GP"                 , ydUpWoLoc.substring(0,1));
			jrCrnSch.setField("YD_BAY_GP"             , ydUpWoLoc.substring(1,2));
			jrCrnSch.setField("YD_SCH_CD"             , ydSchCd);    
			jrCrnSch.setField("YD_SCH_PRIOR"          , ydWrkCrnPrior);
			jrCrnSch.setField("YD_WRK_PROG_STAT"      , "W"); //스케즐 생성후 작업지시 전송
			jrCrnSch.setField("YD_MAIN_WRK_MTL_SH"    , "1");
			jrCrnSch.setField("YD_TO_LOC_GUIDE"       , ydDnWoLoc);
			jrCrnSch.setField("YD_TO_LOC_DCSN_MTD"    , "S"); // S W
			jrCrnSch.setField("YD_EQP_WRK_SH"         , "1");
			jrCrnSch.setField("YD_EQP_WRK_MAX_L"      , "0");

			jrCrnSch.setField("YD_UP_WO_LOC"          , ydUpWoLoc);
			jrCrnSch.setField("YD_UP_WO_LAYER"        , ydUpWoLayer);
			jrCrnSch.setField("YD_UP_WO_LOC_XAXIS"    , ydUpWoLocXaxis); 
			jrCrnSch.setField("YD_UP_WO_XAXIS_GAP_MAX", "1000");              
			jrCrnSch.setField("YD_UP_WO_XAXIS_GAP_MIN", "1000");              
			jrCrnSch.setField("YD_UP_WO_LOC_YAXIS"    , ydUpWoLocYaxis); 
			jrCrnSch.setField("YD_UP_WO_YAXIS_GAP_MAX", "1000");              
			jrCrnSch.setField("YD_UP_WO_YAXIS_GAP_MIN", "1000");              
			jrCrnSch.setField("YD_UP_WO_LOC_ZAXIS"    , ydUpWoLocZaxis); 
			jrCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MAX", "100");               
			jrCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MIN", "100");               
			
			jrCrnSch.setField("YD_DN_WO_LOC"          , ydDnWoLoc);
			jrCrnSch.setField("YD_DN_WO_LAYER"        , ydDnWoLayer);
			jrCrnSch.setField("YD_DN_WO_LOC_XAXIS"    , ydDnWoLocXaxis); 
			jrCrnSch.setField("YD_DN_WO_XAXIS_GAP_MAX", "1000");
			jrCrnSch.setField("YD_DN_WO_XAXIS_GAP_MIN", "1000");
			jrCrnSch.setField("YD_DN_WO_LOC_YAXIS"    , ydDnWoLocYaxis);
			jrCrnSch.setField("YD_DN_WO_YAXIS_GAP_MAX", "1000");
			jrCrnSch.setField("YD_DN_WO_YAXIS_GAP_MIN", "1000");
			jrCrnSch.setField("YD_DN_WO_LOC_ZAXIS"    , ydDnWoLocZaxis);
			jrCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MAX", "100");
			jrCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MIN", "100");
			// 2025.12.22 RITM1543970 분동코일이 아닌 경우 회전지시로 인식 ( 권상각도1, 권하각도3 )
			if ("B".equals(sCoilNo.substring(0, 1))) { // 분동코일
				jrCrnSch.setField("UP_ROTATION_ANGLE"     , "0");
				jrCrnSch.setField("DOWN_ROTATION_ANGLE"   , "0");
			} else { // 회전지시
				jrCrnSch.setField("UP_ROTATION_ANGLE"     , "1");
				jrCrnSch.setField("DOWN_ROTATION_ANGLE"   , "3");
			}
			
			
			String szLogMsg = "";
			int intRtnVal = commDao.insert(jrCrnSch, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insYdCrnsch", logId, mthdNm, "CRNSCH 생성");
			if (intRtnVal < 1) {
				szLogMsg = "["+ mthdNm +"]크레인 스케줄 등록중  Error!! ErrorCode: " + intRtnVal;
				throw new Exception(szLogMsg);
			}
			
			/**********************************************************
			* 4.크레인 스케줄 작업재료 등록
			**********************************************************/			
			jrCrnSch = commUtils.getParam(logId, mthdNm, sModifier);

			jrCrnSch.setField("YD_CRN_SCH_ID" , ydCrnSchId);
			jrCrnSch.setField("REGISTER"      , sModifier);
			jrCrnSch.setField("YD_AID_WRK_YN" , "N"); //주작업
			jrCrnSch.setField("STL_NO"        , sCoilNo);
			jrCrnSch.setField("YD_STK_LYR_NO" , ydUpWoLayer);

			intRtnVal = commDao.insert(jrCrnSch, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insYdCrnWrkMtl", logId, mthdNm, "CRNWRKMTL 생성");
			if (intRtnVal <= 0) {
				szLogMsg = "["+ mthdNm +"] 크레인 스케줄 작업재료 등록중 실패: " + intRtnVal;
				throw new Exception(szLogMsg);
			}
			
			/**********************************************************
			* 5.크레인작업지시 호출
  			**********************************************************/
			commUtils.printLog(logId, "분동코일 작업지시 전문 송신", "SL");

			JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);

			jrYdMsg.setField("JMS_TC_CD"         , "YDY5L004"               ); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("YD_CRN_SCH_ID"     , ydCrnSchId           ); //
			jrYdMsg.setField("MSG_GP"            , "I"           ); //전문구분
			jrYdMsg.setField("COIL_OUTDIA"       , sCoilOutDia); //외경
			jrYdMsg.setField("COIL_INDIA"        , sCoilInDia );  //내경
			jrYdMsg.setField("YD_STL_WT"         , ydStlWt  );
			jrYdMsg.setField("YD_STL_T"          , ydStlW_T   );
			jrYdMsg.setField("YD_STL_W"          , ydStlW_W   );
			jrYdMsg.setField("STL_NO"            , sCoilNo    );			
			
			String sAPP003_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","003"); 
			commUtils.printLog(logId, "분동코일 명령선택 기동으로 변경 : " + sAPP003_YN, "SL");
			
			if ("Y".equals(sAPP003_YN)) {
	           /* 
    			// jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
    			jrYdMsg.setField("JMS_TC_CD"         	, "Y5YDL007"); 					//JMSTC코드
    			//jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); 	//JMSTC생성일시
    			jrYdMsg.setField("YD_EQP_ID"        	, ydEqpId);  					//야드설비ID
    			jrYdMsg.setField("YD_SCH_CD"        	, ydSchCd);						//야드스케쥴코드
    			//jrYdMsg.setField("YD_CRN_SCH_ID"        , ydCrnSchId); 					//야드크레인스케쥴ID
    			
    			//크레인작업지시요구 전문을 추가
				jrRtn = commUtils.addSndData(jrRtn, this.rcvY5YDL007(jrYdMsg));
				*/
				/*
				  com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCrnWrkInfo 
					SELECT YD_CRN_SCH_ID
					  FROM TB_YD_CRNSCH
					 WHERE YD_EQP_ID = :V_YD_EQP_ID
					   AND YD_WRK_PROG_STAT IN ('1','2', '3','4','S')
					   AND DEL_YN = 'N' 
				 */
				jrYdMsg.setField("YD_EQP_ID"             , ydEqpId);
				JDTORecordSet jrEqpWrkInfo = commDao.select(jrYdMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCrnWrkInfo", logId, mthdNm, "크레인작업정보 조회");
				if (jrEqpWrkInfo.size() <= 0) {
					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L004WC", jrYdMsg));
				}
			}else{
				jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L004WC", jrYdMsg));				
			}
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 차량형상 완료 정보(Y5YDL030)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL030(JDTORecord rcvMsg) throws DAOException { 
		String mthdNm = "차량형상 완료 정보[CCoilL2RcvSeEJB.rcvY5YDL030] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = commUtils.getParam(logId, mthdNm, "Y5YDL030");	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId     = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //차상위치
			String sCarYn    = commUtils.trim(rcvMsg.getFieldString("CAR_YN"   )); //0 차량없음 1차량있음
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자(Backup Only)
			
			JDTORecordSet jsCarWbookId = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				throw new Exception("수신한 설비코드가 없음");
			} else if (!"0".equals(sCarYn) && !"1".equals(sCarYn)) {
				throw new Exception("차량유무 코드 이상");
			} else if (ydEqpId.length() != 6) {
				throw new Exception("설비코드 이상");
			}
			commUtils.printLog(logId, "YD_EQP_ID = " + ydEqpId, "SL");
			commUtils.printLog(logId, "CAR_YN    = " + sCarYn   , "SL");

			if (!"PT".equals(ydEqpId.substring(2, 4))) {
				throw new Exception("설비코드 이상");
			}

			/**********************************************************
			* 2. 차량형상완료여부 표시
			**********************************************************/
			//차량형상 완료 표시
			jrParam.setField("YD_CTS_RELAY_YN" , "Y");	//형상완료로 인한 스케줄 기동
			jrParam.setField("YD_STK_COL_GP"   , ydEqpId);
			
			//PIDEV
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");

			/*
			UPDATE TB_YD_WRKBOOK
			   SET YD_CTS_RELAY_YN = :V_YD_CTS_RELAY_YN
			     , MODIFIER = :V_MODIFIER
				 , MOD_DDTT = SYSDATE
			 WHERE YD_WBOOK_ID IN(SELECT WB.YD_WBOOK_ID
			                        FROM TB_YD_WRKBOOK     WB
							           , TB_YD_WRKBOOKMTL  WM
			                           , (SELECT TRN_EQP_CD, CAR_NO, CARD_NO
			                                FROM TB_YD_CARPOINT
			                               WHERE (YD_CARPNT_CD LIKE 'H%'
			                                   OR YD_CARPNT_CD LIKE 'J%') --YD_CARPNT_CD는 바꾸지 않고 YD_STK_COL_GP만 H->J로 바뀜
			                                 AND YD_STK_COL_GP = :V_YD_STK_COL_GP
			                                 AND DEL_YN = 'N'
			                           ) CP
			                       WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
			                         AND WB.DEL_YN = 'N'
			                         AND WM.DEL_YN = 'N'
			                         AND ((WB.YD_CAR_USE_GP = 'L' AND WB.TRN_EQP_CD = CP.TRN_EQP_CD)
			                          OR  (WB.YD_CAR_USE_GP = 'G' AND WB.CAR_NO = CP.CAR_NO  AND WB.CARD_NO = CP.CARD_NO))
			                     )
			   AND SUBSTR(YD_SCH_CD, 7, 1) != 'U' --상차는 스케줄 미리 기동                     
			 */
			
			// PIDEV
//			if("Y".equals(sApplyYnPI)) {
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCarFrm_PIDEV", logId, mthdNm, "차량형상완료 표시");
//			} else { 
//				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCarFrm", logId, mthdNm, "차량형상완료 표시");	
//			}
			
			/**********************************************************
			 * 차량형상완료시각 저장
			 **********************************************************/
			/*
			UPDATE TB_YD_CARSCH
			   SET MODIFIER        = :V_MODIFIER
			     , MOD_DDTT        = SYSDATE
			     , CAR_FRM_CMPL_DT = SYSDATE
			 WHERE DEL_YN = 'N'    
			   AND CAR_NO IN (SELECT CAR_NO
			                   FROM TB_YD_CARPOINT
			                  WHERE YD_GP = 'J'
			                    AND YD_STK_COL_GP = :V_YD_STK_COL_GP
			                    AND DEL_YN = 'N'
			                 )
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCarFrmDt", logId, mthdNm, "차량형상완료 표시");
			
			/**********************************************************
			* 3. 차량형상 완료시 스케줄 기동
			**********************************************************/
			/* 
			SELECT WB.YD_SCH_CD
			     , WB.YD_WBOOK_ID
			     , WM.YD_STK_COL_GP
			     , WM.YD_STK_LYR_NO
			     --차량동간이적 상차인지CHECK : 형상정보완료(Y5YDL030) 수신시 필요
			     , CASE WHEN YD_SCH_CD LIKE 'J_TR1_U_' THEN 'Y'
			            ELSE 'N' END CAR_MOVE_GP
			  FROM TB_YD_WRKBOOK     WB
			     , TB_YD_WRKBOOKMTL  WM
			     , (SELECT *
			          FROM TB_YD_CARPOINT
			         WHERE (YD_CARPNT_CD LIKE 'H%'
			             OR YD_CARPNT_CD LIKE 'J%') --YD_CARPNT_CD는 바꾸지 않고 YD_STK_COL_GP만 H->J로 바뀜
			           AND YD_STK_COL_GP = :V_YD_STK_COL_GP
			       ) CP
			 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
			   AND WB.DEL_YN      = 'N'
			   AND WM.DEL_YN      = 'N'
			   AND WB.YD_GP       = CP.YD_GP
			   AND WB.YD_BAY_GP   = CP.YD_BAY_GP   
			   AND ((WB.YD_CAR_USE_GP = 'L' AND WB.TRN_EQP_CD = CP.TRN_EQP_CD)
			     OR (WB.YD_CAR_USE_GP = 'G' AND WB.CAR_NO = CP.CAR_NO  AND WB.CARD_NO    = CP.CARD_NO))
			   AND WB.YD_WBOOK_ID NOT IN (SELECT CS.YD_WBOOK_ID 
			                                FROM TB_YD_CRNSCH    CS
			                                   , TB_YD_CRNWRKMTL CM
			                               WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
			                                 AND CS.DEL_YN = 'N'
			                                 AND CM.DEL_YN = 'N')
			   AND NVL(WB.SCH_CNCL_YN, 'N') NOT IN ('Y') --사용자 취소된 작업예약은 기동시키지 않는다. 20210302 LHJ
			 ORDER BY WM.YD_STK_LYR_NO DESC
			        , WB.YD_WBOOK_ID
 			 */
			jsCarWbookId = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCarWrkList_PIDEV", logId, mthdNm, "작업예약 조회");					
			
			if (jsCarWbookId.size() == 0) {
				return jrRtn;
			}
			
			String sCarMoveGp = jsCarWbookId.getRecord(0).getFieldString("CAR_MOVE_GP");
			/*************************************
			 * 차량동간이적인 경우 상차인 경우 미리 기동 되어 있음
			 * - RETURN
			 *************************************/
			if ("Y".equals(sCarMoveGp)) {
				return jrRtn;
			}
	
			JDTORecord jrCrnSchMsg = commUtils.getParam(logId, mthdNm, sModifier);
			jrCrnSchMsg.setField("YD_SCH_CD", ""); //야드스케쥴코드
			jrCrnSchMsg.setField("YD_EQP_ID", ""); //야드설비ID

			//적치된 대상 스케줄 호출
			EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this);
			
			for (int i = 0; i < jsCarWbookId.size(); i++) {
				jrParam.setField("YD_WBOOK_ID", jsCarWbookId.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
				jrCrnSchMsg = (JDTORecord)ejbConn.trx("procYDYDJ551", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			}
			
			jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
				
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 스크랩 차단기 정보(Y5YDL031)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL031(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "스크랩차량 차단기 정보[CCoilL2RcvSeEJB.rcvY5YDL031] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			String sAppYn = coilDao.ApplyYn(logId, mthdNm, "APP320", "J", "*");
			commUtils.printLog(logId,  "==========[[[ APP320 스크랩차량 진출입 : "+ sAppYn +" ]]]============", "SL");
			if( "N".equals(sAppYn) ) {
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
			}

			//수신 항목 값
			String msgId		= commUtils.getMsgId(rcvMsg);							// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"	));	// 야드설비ID
			String sCarYn		= commUtils.trim(rcvMsg.getFieldString("CAR_YN"	));		// 차량유무(0:없음, 1:있음)
			String sModifier	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"	));	// 수정자(Backup Only)

			if ("".equals(sModifier)) { sModifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sCarYn)) {
				throw new Exception("수신한 설비코드가 없음");
			} else if (!"0".equals(sCarYn) && !"1".equals(sCarYn)) {
				throw new Exception("차량유무 코드 이상");
			}

			String ydBayGp = ydEqpId.substring(1,2);
			commUtils.printLog(logId, "YD_STK_COL_GP["+ ydEqpId +"] YD_BAY_GP["+ ydBayGp +"] CAR_YN["+ sCarYn +"]", "SL");

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("REPR_CD_GP"	, "APP031");	// 스크랩차량 진입여부
			jrParam.setField("YD_EQP_ID"	, ydEqpId);		// 차량포인트
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getScrpCarEntYn
			SELECT ITEM_VALUE1 AS YD_EQP_ID
			     , DTL_ITEM1   AS CAR_YN
			  FROM TB_YD_RULE
			 WHERE REPR_CD_GP  = :V_REPR_CD_GP
			   AND ITEM_VALUE1 = :V_YD_EQP_ID
			   AND DEL_YN      = 'N'
			*/
			JDTORecordSet jsPtInfo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getScrpCarEntYn", logId, mthdNm, "스크랩차량 진입여부 조회");

			if (jsPtInfo.size() == 0) {
				throw new Exception("수신한 차량포인트["+ ydEqpId +"]가 없습니다.");
			}

			String sValue = commUtils.trim(jsPtInfo.getRecord(0).getFieldString("CAR_YN"));

			if( sValue.equals(sCarYn) ) {
				commUtils.printLog(logId, "동일 차량유무정보 수신["+ sValue +"/"+ sCarYn +"]", "SL");
				return jrRtn;
			}

			jrParam.setField("CAR_YN", sCarYn);	// 스크랩차량 진입여부
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEquipCarYn
			UPDATE TB_YD_RULE
			   SET DTL_ITEM1   = :V_CAR_YN
			     , MODIFIER    = :V_MODIFIER
			     , MOD_DDTT    = SYSDATE
			 WHERE REPR_CD_GP  = :V_REPR_CD_GP
			   AND ITEM_VALUE1 = :V_YD_EQP_ID
			   AND DEL_YN      = 'N'
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updEquipCarYn", logId, mthdNm, "스크랩차량 진입여부 수정");

			/**********************************
			 * 차량진입 여부
			 **********************************/
			if( "1".equals(sCarYn) ) {
				/***********************************
				 * 3열 -> 1,2열 이적스케쥴 삭제
				 ***********************************/
				String ydSchCd = "J"+ ydBayGp +"SC01MH";
				jrParam.setField("YD_SCH_CD", ydSchCd); // 스크랩이적 스케쥴코드

				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getScrapMvWrkInfo
				SELECT A.YD_WBOOK_ID
				     , A.YD_SCH_CD
				     , B.YD_CRN_SCH_ID
				     , NVL(B.YD_WRK_PROG_STAT, 'W') AS YD_WRK_PROG_STAT
				  FROM TB_YD_WRKBOOK A
				     , TB_YD_CRNSCH  B
				 WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID(+)
				   AND A.DEL_YN      = 'N'
				   AND B.DEL_YN(+)   = 'N'
				   AND A.YD_SCH_CD   = :V_YD_SCH_CD
				 ORDER BY B.YD_WRK_PROG_STAT DESC, A.YD_WBOOK_ID DESC
				*/
				JDTORecordSet jsScrapWrk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getScrapMvWrkInfo", logId, mthdNm, "스크랩이적 작업예약 조회");

				for(int i=0; i<jsScrapWrk.size(); i++) {

					String ydWbookId		= commUtils.trim(jsScrapWrk.getRecord(i).getFieldString("YD_WBOOK_ID"));
					String ydCrnSchId		= commUtils.trim(jsScrapWrk.getRecord(i).getFieldString("YD_CRN_SCH_ID"));
					String ydWrkProgStat	= commUtils.trim(jsScrapWrk.getRecord(i).getFieldString("YD_WRK_PROG_STAT"));

					// 지시대기 작업 삭제
					if( "W".equals(ydWrkProgStat) ) {

						// 크레인스케쥴 삭제
						if( !"".equals(ydCrnSchId) ) {

							jrParam = commUtils.getParam(logId, mthdNm, sModifier);
							jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);

							EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
							JDTORecord jrCrnSchCncl = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
							String rtnCd	= commUtils.nvl(jrCrnSchCncl.getFieldString("RTN_CD"), "0");
							String rtnMsg	= commUtils.nvl(jrCrnSchCncl.getFieldString("RTN_MSG"), "");
							commUtils.printLog(logId, (i+1)+". YD_WBOOK_ID["+ ydWbookId +"] YD_CRN_SCH_ID["+ ydCrnSchId +"] YD_WRK_PROG_STAT["+ ydWrkProgStat +"] 스케쥴취소 RTN_CD["+ rtnCd +"] RTN_MSG["+ rtnMsg +"]", "SL");
						}

						// 작업예약 삭제
						if( !"".equals(ydWbookId) ) {

							jrParam = commUtils.getParam(logId, mthdNm, sModifier);
							jrParam.setField("YD_WBOOK_ID"	, ydWbookId);

							EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
							JDTORecord jrCrnWrkCncl = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
							String rtnCd	= commUtils.nvl(jrCrnWrkCncl.getFieldString("RTN_CD"), "0");
							String rtnMsg	= commUtils.nvl(jrCrnWrkCncl.getFieldString("RTN_MSG"), "");
							commUtils.printLog(logId, (i+1)+". YD_WBOOK_ID["+ ydWbookId +"] YD_WRK_PROG_STAT["+ ydWrkProgStat +"] 작업예약취소 RTN_CD["+ rtnCd +"] RTN_MSG["+ rtnMsg +"]", "SL");
						}
					}
				}
				
				/***********************************************************************************************
				 * B/C 동 스크랩차량 들어왔을때 수입 권하위치 가능 지역으로 변경 로직 추가
				 * 일반 차량 작업인지 판단후 스크랩 차량일때만 변경 해줌.
				 * YYS 202302 
				 * 쿼리에서 차량진입시 상태값 체크하여 30스판 이하는 권하위치로 검색돼지 않게 처리한 상태임.
				 * 체크후 적용여부 정리.
				 ***********************************************************************************************/
//				String sAPP010_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","010"); 
//				commUtils.printLog(logId, "B/C 동 스크랩차량 들어왔을때 수입 권하위치 가능 지역으로 변경 : " + sAPP010_YN, "SL");
//				
//				if(("B".equals(ydBayGp) || "C".equals(ydBayGp)) && "Y".equals(sAPP010_YN)){
//
//					jrParam.setField("PT_LOAD_LOC"	, ydEqpId);		// 차량포인트
//					/*
//					SELECT NVL(CAR_NO ,TRN_EQP_CD) AS CAR_NO
//					  FROM TB_YD_CARPOINT
//					 WHERE DEL_YN = 'N'
//					   AND YD_STK_COL_GP = :V_PT_LOAD_LOC 
//					 */
//					JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdGetCarNoByLoc", logId, mthdNm, "차량번호 조회");
//					boolean checkYn 	  =  false;//차량작업으로 변경 하지 않음
//					String ydSchCdCheck   = "J"+ ydBayGp +"CV01LH";
//					if (jsRst.size() == 0) {
//						commUtils.printLog(logId, "상차도 정보 없음 :" +ydEqpId , "SL");
//						checkYn 	  =  true; //차량작업이 없고 스크랩 차량작업임
//					} else {	
//					
//						String sCarNo = jsRst.getRecord(0).getFieldString("CAR_NO");
//						if ("".equals(sCarNo)) {
//							commUtils.printLog(logId, "해당위치 차량정보 없음  :" +ydEqpId , "SL");
//							checkYn 	  =  true;
//						}
//					}
//					
//					if (checkYn) {
//						//권하위치 재 검색후
//						//권하위치 변경 등록
//						jrParam.setField("YD_SCH_CD"	, ydSchCdCheck);
//						/*
//						/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCvWrkInfo
//							SELECT A.YD_WBOOK_ID
//							     , A.YD_SCH_CD
//							     , B.YD_EQP_ID
//							     , B.YD_CRN_SCH_ID
//							     , B.YD_WRK_PROG_STAT
//							     , B.YD_DN_WO_LOC
//							     , B.YD_DN_WO_LAYER
//							  FROM TB_YD_WRKBOOK A
//							     , TB_YD_CRNSCH  B
//							 WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
//							   AND A.DEL_YN   = 'N'
//							   AND B.DEL_YN   = 'N'
//							   AND B.YD_DN_WO_LOC <> 'XX010101'
//							   AND B.YD_WRK_PROG_STAT IN ('1','2')
//							   AND SUBSTR(B.YD_DN_WO_LOC,3,2) < 31
//							   AND A.YD_SCH_CD   = :V_YD_SCH_CD
//						 */
//						JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCvWrkInfo", logId, mthdNm, "차량번호 조회");
//						
//						if(jsCrnSch.size() > 0){
//							// 작업예약 조회
//		        			String ydWbookId		= commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID"));
//		        			String ydEqpIdCv		= commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_EQP_ID"));
//		        			String ydCrnSchId		= commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
//		        			
//		        			String ydStlNo   		= commUtils.trim(jsCrnSch.getRecord(0).getFieldString("STL_NO"));
//		        			//String ydEqpIdCv		= commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_EQP_ID"));
//		        			//String ydCrnSchId		= commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
//		        			
//		        			commUtils.printLog(logId, "권하위치 변경 작업 시작 >> 작업예약아이디 : ["+ydWbookId+"] 크레인 :"+ydEqpIdCv, "SL");
//		        			
//		        			JDTORecord jrParamSet	= commUtils.getParam(logId, mthdNm, sModifier);
//		        			jrParamSet.setField("YD_WBOOK_ID"	, ydWbookId);
//		        			jrParamSet.setField("YD_EQP_ID"		, ydEqpIdCv);
//	
//		                	/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdWrkbook 
//		                	SELECT YD_WBOOK_ID      AS YD_WBOOK_ID
//		                		 ...
//		                	     , (SELECT STL_NO 
//		                	          FROM TB_YD_WRKBOOKMTL 
//		                	         WHERE YD_WBOOK_ID = A.YD_WBOOK_ID 
//		                	           AND DEL_YN = 'N' AND ROWNUM = 1) AS STL_NO
//		                	     , (SELECT ITEM1 
//		                	          FROM TB_YD_RULE
//		                	         WHERE REPR_CD_GP = 'APP010'
//		                	           AND DEL_YN = 'N') AS SCHLOG_YN
//		                	  FROM TB_YD_WRKBOOK A
//		                	 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
//		        			 */
//		                	JDTORecordSet jsWbook = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdWrkbook", logId, mthdNm, "작업예약 조회(대차상차)"); 
//		                	String sMsg = "";
//		        	    	if (jsWbook.size() < 1) {
//		        	    		sMsg = "오류:작업 예약 조회 시 오류 ydWbookId: " + ydWbookId;
//		        				
//		        				commUtils.printLog(logId, sMsg, "SL");
//		        				
//		        				jrRtn.setField("RTN_CD" , "0");	
//		            			jrRtn.setField("RTN_MSG", sMsg);
//		            			return jrRtn;
//		        			}
//		        	    	
//		        	    	/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnSchByWBookId
//		        	    	SELECT A.YD_EQP_ID               AS YD_EQP_ID
//		        	    		   ...
//		        	    	  FROM TB_YD_EQP  A                                               
//		        	    	     , TB_YD_CRNSCH B                                               
//		        	    	     , TB_YD_CRNWRKMTL C                                               
//		        	    	     , USRPTA.TB_PT_COILCOMM  D  
//		        	    	 WHERE B.YD_EQP_ID      = A.YD_EQP_ID  
//		        	    	   AND B.YD_CRN_SCH_ID  = C.YD_CRN_SCH_ID  
//		        	    	   AND C.STL_NO         = D.COIL_NO
//		        	    	   AND B.YD_WBOOK_ID    = :V_YD_WBOOK_ID
//		        	    	   AND B.YD_EQP_ID      = :V_YD_EQP_ID                         
//		        	    	   AND B.DEL_YN = 'N'
//		        	    	   AND C.DEL_YN = 'N'
//		        	    	 ORDER BY B.YD_CRN_SCH_ID
//		        	    	*/
//		        	    	JDTORecordSet jsCrnSchTCar = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnSchByWBookId", logId, mthdNm, "크레인스케줄 조회(대차상차)"); 
//		        	    	
//		        	    	if (jsCrnSchTCar.size() < 1) {
//		        	    		sMsg = "오류:크레인스케쥴 조회 시 오류 ydWbookId: " + ydWbookId;
//		        				commUtils.printLog(logId, sMsg, "SL");
//		        				
//		        				jrRtn.setField("RTN_CD" , "0");	
//		            			jrRtn.setField("RTN_MSG", sMsg);
//		            			return jrRtn;
//		        			}
//		        	    	
//		        	    	jsWbook.first();
//		        	    	jsCrnSchTCar.first();
//		        	    	
//		        	    	JDTORecord jrWbook = jsWbook.getRecord();
//		        	    	JDTORecord jrCrnSchTCar = jsCrnSchTCar.getRecord();
//		        	    	jrWbook.setField("RE_TOLOC_YN", "Y");//권하위치 재설정 사용
//	        				/********************************************
//	        				 * 수입 - 주작업TO위치
//	        				 ********************************************/
//	        				EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this);
//	        				JDTORecord jrSchRtn = (JDTORecord)ejbConn.trx("toLocPrimaryWorkCV", new Class[] { String.class, String.class, JDTORecord.class, JDTORecord.class }, new Object[] {logId, mthdNm, jrWbook, jrCrnSchTCar });
//	        				
//							String rtnCd = commUtils.nvl(jrSchRtn.getFieldString("RTN_CD"), "0");
//							String rtnMg = commUtils.nvl(jrSchRtn.getFieldString("RTN_MSG"), "");
//							
//							String dnWoLoc   = commUtils.nvl(jrSchRtn.getFieldString("YD_DN_WO_LOC"), "");
//							String dnWoLayer = commUtils.nvl(jrSchRtn.getFieldString("YD_DN_WO_LAYER"), "");
//							
//							
//							if( "0".equals(rtnCd) ) {
//				            	// TO위치 결정 실패시 XX010101 업데이트 처리
//		        				
//			        			 dnWoLoc   = "XX010101";
//								 dnWoLayer = "001";
//				            }else{ // 권하위치 재설정
//				            	
//
//								// Parameter Grid Setting
//								GridData gdParam = new GridData();
//								
//								gdParam.addParam("YD_USER_ID", sModifier);
//								gdParam.addParam("YD_LOC_GP" , "H");
//								gdParam.createHeader("CHECK", OperateGridData.t_checkbox);
//								gdParam.createHeader("YD_CRN_SCH_ID" , "T" );
//								gdParam.createHeader("STL_NO"        , "T" );
//								gdParam.createHeader("YD_DN_WO_LOC"  , "T" );
//								gdParam.createHeader("YD_DN_WO_LAYER", "T" );
//								
//								 
//								gdParam.getHeader("CHECK").addValue("1", "");
//								gdParam.getHeader("YD_CRN_SCH_ID").addValue(ydCrnSchId, "");
//								gdParam.getHeader("STL_NO").addValue(ydStlNo, "");
//								gdParam.getHeader("YD_DN_WO_LOC").addValue(dnWoLoc, "");
//								gdParam.getHeader("YD_DN_WO_LAYER").addValue(dnWoLayer, "");
//								
//								commUtils.printLog(logId, "권하위치 재설정 :dnWoLoc ["+dnWoLoc+"]", "SL");
//								commUtils.printLog(logId, "권하위치 재설정 :ydStlNo ["+ydStlNo+"]", "SL");
//								commUtils.printLog(logId, "권하위치 재설정 :ydCrnSchId ["+ydCrnSchId+"]", "SL");
//								commUtils.printLog(logId, "권하위치 재설정 :dnWoLayer ["+dnWoLayer+"]", "SL");
//								
//								gdParam.setNavigateValue(mthdNm); // 상위 Method 명
//								gdParam.setIPAddress(logId);      // Logging 을 위한 ID
//				            	 ejbConn = new EJBConnector("default", "CCoilJspFaEJB", this);
//				    			 jrRtn = (JDTORecord)ejbConn.trx("updToPosFixCoilH", new Class[] { GridData.class }, new Object[] { gdParam });
//				    			 
//				    			  rtnCd = commUtils.nvl(jrSchRtn.getFieldString("RTN_CD"), "0");
//								  rtnMg = commUtils.nvl(jrSchRtn.getFieldString("RTN_MSG"), "");
//								  commUtils.printLog(logId, "권하위치 재설정 완료 메시지 :>>>>>>> "+rtnMg, "SL");
//								  
//			            			
//				            }
//							
//
//						}
//						
//					}
//						
//				}
				
			} else {
				/***********************************
				 * 3열 -> 1,2열 이적스케쥴 생성
				 ***********************************/

				jrParam.setField("YD_BAY_GP", ydBayGp);
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getBufferScrapInfo
				SELECT A.YD_STK_COL_GP
				     , A.YD_STK_BED_NO
				     , A.YD_STK_LYR_NO
				     , A.YD_STK_LYR_MTL_STAT
				     , A.STL_NO
				     , SUBSTR(YD_STK_COL_GP, 0, 4)|| '01MH' AS YD_SCH_CD
				  FROM TB_YD_STKLYR A
				 WHERE DEL_YN = 'N'
				   AND YD_STK_LYR_MTL_STAT         = 'C'
				   AND SUBSTR(YD_STK_COL_GP, 2, 1) = :V_YD_BAY_GP
				   AND 'Y' = CASE WHEN SUBSTR(YD_STK_COL_GP, 2, 1) = 'H'
				                   AND YD_STK_COL_GP = 'JHSC03'              THEN 'Y'
				                  WHEN SUBSTR(YD_STK_COL_GP, 2, 1) = 'E'
				                   AND YD_STK_COL_GP = 'JESC03'              THEN 'Y'
				                  WHEN SUBSTR(YD_STK_COL_GP, 2, 1) = 'C'
				                   AND YD_STK_COL_GP = 'JCSC03'              THEN 'Y'
				                  WHEN SUBSTR(YD_STK_COL_GP, 2, 1) = 'B'
				                   AND YD_STK_COL_GP IN ('JBSC03', 'JBSC04') THEN 'Y'
				                  WHEN SUBSTR(YD_STK_COL_GP, 2, 1) = 'A'
				                   AND YD_STK_COL_GP = 'JASC03'              THEN 'Y'
				                  ELSE 'N'
				             END
				   ----- 작업예약 제외 ---------
				   AND NOT EXISTS (SELECT 1
				                     FROM TB_YD_WRKBOOK    WB
				                        , TB_YD_WRKBOOKMTL WM
				                    WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                      AND WB.YD_GP       = 'J'
				                      AND WB.DEL_YN      = 'N'
				                      AND WM.DEL_YN      = 'N'
				                      AND WM.STL_NO      = A.STL_NO
				                  )
				 ORDER BY A.YD_STK_LYR_NO DESC, A.YD_STK_COL_GP, A.YD_STK_BED_NO
				*/
				JDTORecordSet jsScrap = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getBufferScrapInfo", logId, mthdNm, "스크랩이적 대상재 조회");

				for(int i=0; i<jsScrap.size(); i++) {

					String ydSchCd		= commUtils.trim(jsScrap.getRecord(i).getFieldString("YD_SCH_CD"));
					String sStlNo		= commUtils.trim(jsScrap.getRecord(i).getFieldString("STL_NO"));
					String ydStkColGp	= commUtils.trim(jsScrap.getRecord(i).getFieldString("YD_STK_COL_GP"));
					String ydStkBedNo	= commUtils.trim(jsScrap.getRecord(i).getFieldString("YD_STK_BED_NO"));
					String ydStkLyrNo	= commUtils.trim(jsScrap.getRecord(i).getFieldString("YD_STK_LYR_NO"));
					String ydStkLoc		= ydStkColGp+ydStkBedNo+"-"+ydStkLyrNo;

					String sMsg = "["+ (i+1) +"/"+ jsScrap.size() +"] 스케쥴코드["+ ydSchCd +"] 저장위치["+ ydStkLoc +"] 코일번호["+ sStlNo +"]";
					commUtils.printLog(logId, sMsg, "SL");

					/*********************************
		    		 * 1. 작업예약 등록
		    		 *********************************/
					jrParam	= commUtils.getParam(logId, mthdNm, sModifier);

					jrParam.setField("YD_SCH_CD"			, ydSchCd	);
					jrParam.setField("STL_SH"				, "1"		);
					jrParam.setField("STL_NO1"				, sStlNo	);
					jrParam.setField("YD_TO_LOC_DCSN_MTD"	, "F"		);
					jrParam.setField("YD_AIM_YD_GP"			, "J"		);
					jrParam.setField("YD_AIM_BAY_GP"		, ydBayGp	);
 
					EJBConnector ejbConn = new EJBConnector("default", "CCoilWrkBookSeEJB", this); 
					JDTORecord jrWrkBook = (JDTORecord)ejbConn.trx("insWrkBookTx", new Class[] { JDTORecord.class }, new Object[] { jrParam });

					String rtnCd		 = commUtils.nvl(jrWrkBook.getFieldString("RTN_CD"), "0");
					String rtnMsg		 = commUtils.nvl(jrWrkBook.getFieldString("RTN_MSG"), "");
					String ydWbookId	 = commUtils.nvl(jrWrkBook.getFieldString("YD_WBOOK_ID"), "");

					if( !"1".equals(rtnCd) ) {
						sMsg = "작업예약생성 실패 : YD_STK_COL_GP["+ ydStkLoc +"] STL_NO["+ sStlNo +"] RTN_MSG:"+ rtnMsg;
			    		commUtils.printLog(logId, sMsg, "SL");

			    		jrRtn.setField("RTN_CD"	, "0");
			    		jrRtn.setField("RTN_MSG", sMsg);

			    		commUtils.printLog(logId, mthdNm, "S-");
						return jrRtn;
					}

					sMsg = " >> ["+ (i+1) +"/"+ jsScrap.size() +"] 작업에약ID["+ ydWbookId +"]";
					commUtils.printLog(logId, sMsg, "SL");

					/*********************************
		    		 * 스케쥴 1개 기동처리
		    		 *********************************/
					if( i == 0 ) {

						jrParam = commUtils.getParam(logId, mthdNm, sModifier);
						jrParam.setField("YD_SCH_CD", ydSchCd);//스케줄코드
						/*
						SELECT YD_WBOOK_ID
						     , YD_SCH_CD
						  FROM TB_YD_CRNSCH
						 WHERE DEL_YN = 'N'
						   AND YD_SCH_CD = :V_YD_SCH_CD
						*/
						JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getWorkTbRefNONESchCd", logId, mthdNm, "기동된 크레인스케쥴 조회");

						if( jsWrkBook.size() < 1 ) {

							jrParam = commUtils.getParam(logId, mthdNm, sModifier);
							jrParam.setField("YD_SCH_CD"	, ydSchCd  );
							jrParam.setField("YD_WBOOK_ID"	, ydWbookId);

							EJBConnector ejbConnS = new EJBConnector("default" , "CCoilJspSeEJB" , this);
							JDTORecord jrRst = (JDTORecord)ejbConnS.trx("trxRunSchedule" , new Class[]{JDTORecord.class} , new Object[]{ jrParam });

							jrRtn = commUtils.addSndData(jrRtn, jrRst);

			    			sMsg = " >> 스케쥴 기동처리 - rtnCd["+ rtnCd +"] rtnMsg["+ rtnMsg +"] 작업예약ID["+ ydWbookId +"]";
				    		commUtils.printLog(logId, sMsg, "SL");
				    	}
					}
				}
			}

			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
    /**
	 *      [A] 오퍼레이션명 : 크레인주행금지구간(Y5YDL032)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
    public JDTORecord rcvY5YDL032(JDTORecord rcvMsg) throws DAOException {
    	String mthdNm   = "크레인주행금지구간[CCoilL2RcvSeEJB.rcvY5YDL032] < " + rcvMsg.getResultMsg();
    	String logId      = rcvMsg.getResultCode();
    	JDTORecord resMsg = commUtils.getParam(logId, mthdNm, "Y5YDL032"); //크레인작업실적응답 전문 생성용
		boolean resYn 	  = true;
    	
    	try {
    		commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
    		//Data수신 항목 값
			String msgId      		   = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String startEndGp		   = commUtils.trim(rcvMsg.getFieldString("START_END_GP")); //시작종료구분
			String ydGp 			   = commUtils.trim(rcvMsg.getFieldString("YD_GP"				   )); //야드구분
			String bayGp 			   = commUtils.trim(rcvMsg.getFieldString("BAY_GP"                 )); //야드동구분
			String travlProhFromloc    = commUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_FROMLOC"     )); //야드주행금지FROM위치
			String travlProhToloc 	   = commUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_TOLOC"       )); //야드주행금지TO위치
			String travlProhFromxaxis  = commUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_FROMXAXIS"   )); //야드주행금지FROM위치X축
			String travlProhToxaxis    = commUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_TOXAXIS"     )); //야드주행금지TO위치X축
			
			String sModifier 		   = commUtils.trim(rcvMsg.getFieldString("MODIFIER"               )); //수정자
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;
			
			JDTORecord jrRtn   = JDTORecordFactory.getInstance().create();	//전문 Return
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(startEndGp)) {
				throw new Exception("시작종료구분(START_END_GP) 없음");
			} else if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			} else if ("".equals(bayGp)) {
				throw new Exception("야드동구분(BAY_GP) 없음");
			} else if ("".equals(travlProhFromloc)) {
				throw new Exception("야드주행금지FROM위치(TRAVL_PROH_FROMLOC) 없음");
			} else if ("".equals(travlProhToloc)) {
				throw new Exception("야드주행금지TO위치(TRAVL_PROH_TOLOC) 없음");
			} else if ("".equals(travlProhFromxaxis)) {
				throw new Exception("야드주행금지FROM위치X축(TRAVL_PROH_FROMXAXIS) 없음");
			} else if ("".equals(travlProhToxaxis)) {
				throw new Exception("야드주행금지TO위치X축(TRAVL_PROH_TOXAXIS) 없음");
			}
			
			/**********************************************************
			* 2. 크레인주행금지구간 SEQ SELECT
			**********************************************************/
			jrParam.setField("YD_GP"             , ydGp            );
			jrParam.setField("BAY_GP"            , bayGp           );
			jrParam.setField("TRAVL_PROH_FROMLOC", travlProhFromloc);
			jrParam.setField("TRAVL_PROH_TOLOC"  , travlProhToloc  );
			
			/*
			SELECT YD_CRANE_TRAVL_PROH_SEQ 
			  FROM TB_YD_CRANE_TRAVL_PROH
			 WHERE DEL_YN = 'N'
			   AND YD_GP  = :V_YD_GP
			   AND BAY_GP = :V_BAY_GP
			   AND TRAVL_PROH_FROMLOC = :V_TRAVL_PROH_FROMLOC
			   AND TRAVL_PROH_TOLOC   = :V_TRAVL_PROH_TOLOC
			*/
			
			JDTORecordSet jrList = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCrnTravlProh", logId, mthdNm, "크레인주행금지구간 조회");	
			
			String sSeq = "";
			
			if (jrList.size() > 0) {
				sSeq = jrList.getRecord(0).getFieldString("YD_CRANE_TRAVL_PROH_SEQ");
			}
			
			jrParam.setField("YD_CRANE_TRAVL_PROH_SEQ", sSeq    );
			jrParam.setField("TRAVL_PROH_FROMXAXIS"   , travlProhFromxaxis);
			jrParam.setField("TRAVL_PROH_TOXAXIS"     , travlProhToxaxis  );
			
			if ("S".equals(startEndGp)) {
				jrParam.setField("DEL_YN", "N"); 
			} else if ("E".equals(startEndGp)) {
				jrParam.setField("DEL_YN", "Y");
			}
				
			/*
			MERGE INTO TB_YD_CRANE_TRAVL_PROH USING DUAL 
			  ON ( 
			      YD_CRANE_TRAVL_PROH_SEQ = :V_YD_CRANE_TRAVL_PROH_SEQ
			  AND YD_GP                   = :V_YD_GP
			  AND BAY_GP                  = :V_BAY_GP
			  AND TRAVL_PROH_FROMXAXIS    = :V_TRAVL_PROH_FROMXAXIS
			  AND TRAVL_PROH_TOXAXIS      = :V_TRAVL_PROH_TOXAXIS
			     )
			WHEN MATCHED THEN
			UPDATE 
			   SET DEL_YN   = :V_DEL_YN
			     , MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE                 
			WHEN NOT MATCHED THEN
			INSERT (
			      YD_CRANE_TRAVL_PROH_SEQ
			    , YD_GP
			    , BAY_GP
			    , TRAVL_PROH_FROMLOC
			    , TRAVL_PROH_TOLOC
			    , TRAVL_PROH_FROMXAXIS
			    , TRAVL_PROH_TOXAXIS
			    , DEL_YN
			    , REGISTER
			    , REG_DDTT
			    , MODIFIER
			    , MOD_DDTT
			) VALUES (
			     (SELECT NVL(MAX(TO_NUMBER(YD_CRANE_TRAVL_PROH_SEQ)),0)+1 
			        FROM TB_YD_CRANE_TRAVL_PROH)
			    , :V_YD_GP
			    , :V_BAY_GP
			    , :V_TRAVL_PROH_FROMLOC
			    , :V_TRAVL_PROH_TOLOC
			    , :V_TRAVL_PROH_FROMXAXIS
			    , :V_TRAVL_PROH_TOXAXIS
			    , :V_DEL_YN
			    , :V_MODIFIER
			    , SYSDATE
			    , :V_MODIFIER
			    , SYSDATE
			)
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnTravlProh", logId, mthdNm, "크레인주행금지구간 수정 및 등록");
			
			/**********************************************************
			* 3. 응답 전문 전송(YDY5L005)
			**********************************************************/
			if (resYn) {
				resMsg.setResultCode(logId);				//Log ID
				resMsg.setField("YD_L3_HD_RS_CD", "0000");	//야드L3처리결과코드(정상)
				resMsg.setField("BAY_GP"        , bayGp);
				resMsg.setField("YD_EQP_ID"     , "J"+bayGp+"CR"+bayGp+"1"); // 대표크레인

				jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(resMsg));
			}
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
			
		} catch (Exception e) {
			if (resYn) {
				try {
					resMsg.setResultCode(logId);
					
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "CCommSeEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { coilDao.getYDY5L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
    }   	 
    
    
    /**
	 *      [A] 오퍼레이션명 : 코일 야드 크레인 위치정보(Y5YDL033)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY5YDL033(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일 야드 크레인 위치정보[CCoilL2RcvSeEJB.rcvY5YDL033] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값
			String msgId     = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			for (int Loop_i = 1; Loop_i <= 24; Loop_i++) {
				
				String ydEqpId = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" + Loop_i ));
				
				if ("".equals(ydEqpId)) {
					break;
				}

				jrParam.setField("YD_EQP_ID"     	, ydEqpId );  //YD_EQP_ID 
				jrParam.setField("CRN_WRK_PROC_STAT", commUtils.trim(rcvMsg.getFieldString("CRN_WRK_PROC_STAT"+Loop_i )) );  //CRN_WRK_PROC_STAT
				jrParam.setField("CURR_XAXIS"     	, commUtils.trim(rcvMsg.getFieldString("CURR_XAXIS"+Loop_i )) );  //CURR_XAXIS
				jrParam.setField("FROM_XAXIS"     	, commUtils.trim(rcvMsg.getFieldString("FROM_XAXIS"+Loop_i )) );  //FROM_XAXIS 
				jrParam.setField("TO_XAXIS"     	, commUtils.trim(rcvMsg.getFieldString("TO_XAXIS"+Loop_i )) );  //TO_XAXIS
				
				/*
				MERGE INTO TB_YD_CRNLOC CRN USING (
				SELECT :V_YD_EQP_ID         AS YD_EQP_ID
				     , :V_CRN_WRK_PROC_STAT AS CRN_WRK_PROC_STAT
				     , :V_CURR_XAXIS        AS CURR_XAXIS
				     , :V_FROM_XAXIS        AS FROM_XAXIS
				     , :V_TO_XAXIS          AS TO_XAXIS
				     , :V_MODIFIER          AS MODIFIER
				     , SYSDATE              AS MOD_DDTT
				  FROM DUAL  
				) DD ON (CRN.YD_EQP_ID = DD.YD_EQP_ID)
				WHEN NOT MATCHED THEN
				INSERT (CRN.YD_GP               , CRN.YD_BAY_GP             , CRN.YD_EQP_ID , 
				        CRN.CRN_WRK_PROC_STAT   , CRN.CURR_XAXIS            , CRN.FROM_XAXIS,
				        CRN.TO_XAXIS            , CRN.REGISTER              , CRN.REG_DDTT  ,  
				        CRN.MODIFIER            , CRN.MOD_DDTT)
				VALUES (SUBSTR(DD.YD_EQP_ID,1,1), SUBSTR(DD.YD_EQP_ID,2,1)  , DD.YD_EQP_ID  ,
				        DD.CRN_WRK_PROC_STAT    , DD.CURR_XAXIS             , DD.FROM_XAXIS , 
				        DD.TO_XAXIS             , DD.MODIFIER               , DD.MOD_DDTT   ,
				        DD.MODIFIER             , DD.MOD_DDTT)
				WHEN MATCHED THEN UPDATE SET
				     CRN.MODIFIER           = DD.MODIFIER
				   , CRN.MOD_DDTT           = DD.MOD_DDTT
				   , CRN.CRN_WRK_PROC_STAT  = DD.CRN_WRK_PROC_STAT
				   , CRN.CURR_XAXIS         = DD.CURR_XAXIS
				   , CRN.FROM_XAXIS         = DD.FROM_XAXIS
				   , CRN.TO_XAXIS           = DD.TO_XAXIS
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnLoc", logId, mthdNm, "코일 야드 크레인 위치 정보 등록");
			}
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
     * 오퍼레이션명 : C열연코일야드L2 크레인작업지시 (Y5YDL007) procY5CrnWrkOrdReq
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param rcvMsg
     * @return JDTORecord
     * @throws JDTOException
     */
    public JDTORecord rcvY5YDL007(JDTORecord rcvMsg)throws JDTOException  {
    	String mthdNm = "크레인작업지시[CCoilL2RcvSeEJB.rcvY5YDL007] < " + rcvMsg.getResultMsg();
    	String logId  = rcvMsg.getResultCode();
    	
    	JDTORecord jrSndMsg = commUtils.getParam(logId, mthdNm, "Y5YDL007");   //응답전문 
    	JDTORecord jrRtn  	= JDTORecordFactory.getInstance().create();   //Return
    	String sMsg			= "";
        
    	try{
        	
        	commUtils.printLog(logId, mthdNm , "S+");
        	commUtils.printParam(logId, rcvMsg);
        	
        	//=============================================================
        	// Log 테이블 등록  
			//=============================================================
        	String msgId         	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydCmdChk      	= commUtils.nvl (rcvMsg.getFieldString("YD_CMD_CHK")  , "N"); // 긴급작업 일 경우('Y'), 화면 크레인상태관리 - 명령선택기동할 경우 ('Y')
			String rcvYdWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       	= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydSchCdDn       	= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //권하시야드스케쥴코드
			
			String ydCrnSchId		= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
        	// 스케쥴생성MAIN에서 발생함 :  'W'상태일때 호출한 CRN작업지시ID
        	String ydCrnSchIdRe		= commUtils.nvl(rcvMsg.getFieldString("YD_CRN_SCH_ID_RE"), "");
        	String tcarLdCmplYn		= commUtils.nvl(rcvMsg.getFieldString("TCAR_LD_CMPL_YN"),  "N"); // 야드L2 영대차출발지시 송신여부

        	String sAUTO_YN         = commUtils.trim(rcvMsg.getFieldString("AUTO_YN")); // 무인크레인 여부 - 일시정지 권하위치변경, 일시정지 스케줄 삭제에 쓰임
        	String sYD_LOC_GP       = commUtils.trim(rcvMsg.getFieldString("YD_LOC_GP")); //
        	
        	// PIDEV_S :병행가동용:PI_YD
        	String sPiYd      		= commUtils.trim(rcvMsg.getFieldString("PI_YD")); // 
        	String sPiYd1       	= commUtils.trim(rcvMsg.getFieldString("PI_YD1")); // 
        	
			String sModifier     	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			String rtnXXLogIn = "N"; //XX LOG 생성여부 판단
			String ydCVChk      	= commUtils.nvl (rcvMsg.getFieldString("YD_CV_CHK")  , "N"); // 수입권하위치XX 일 경우다시 호출('Y')
			
			String ydXXCV      = "N";
			String ydXXCHK      = "Y";
			
			//TO위치가 XX 인경우 다음 작업 지시 전송을 위한 목적
			String ydToXXChk    	= commUtils.nvl (rcvMsg.getFieldString("YD_TOXX_CHK")  , "N"); 
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			commUtils.printLog(logId, "크레인설비["+ydEqpId+"] 상태 체크 완료 -◈◈◈ 야드 작업 진행상태 : " + rcvYdWrkProgStat , "SL");
			
			//크레인작업실적응답 전문 생성용
			jrSndMsg = commUtils.getParam(logId, mthdNm, sModifier);
			jrSndMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			jrSndMsg.setField("YD_WRK_PROG_STAT", rcvYdWrkProgStat); //야드작업진행상태
			jrSndMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrSndMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			jrSndMsg.setField("YD_L2_WR_GP"     , "J"          ); //야드L2실적구분(지시요구)
			jrSndMsg.setField("YD_L3_HD_RS_CD"  , "JR99"       ); //야드L3처리결과코드(Error)
			jrSndMsg.setField("YD_L3_MSG"       , "오류:크레인작업지시요구 수신처리"); //야드L3MESSAGE(Error)
			
			/**********************************************************
			* 1. 설비상태 Check
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_EQP_ID"    , ydEqpId  ); //크레인 정보
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("YD_CV_CHK"    , ydCVChk); //수입권하위치XX 일 경우다시 호출
			
			JDTORecord jrChk = coilDao.chkEqpStat(jrParam);        	
			String ydL3HdRsCd 		 = commUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));
			String ydL3Msg    		 = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));
			String sEqpYdWrkProgStat = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT"   ));
			if (!"".equals(ydL3Msg)) {
				jrSndMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				jrSndMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
//				throw new Exception(ydL3Msg);
				sMsg = "오류:크레인 상태 check: " + ydL3Msg;
				commUtils.printLog(logId, sMsg, "SL");
				
				jrRtn.setField("RTN_CD" , "0");	
    			jrRtn.setField("RTN_MSG", sMsg);
    			return jrRtn;
			}       	
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCrnSchY5YDL007
			SELECT *
			  FROM
			       ( SELECT CS.YD_CRN_SCH_ID
			              , CS.YD_WRK_PROG_STAT
			              , CS.YD_WBOOK_ID
			              , CS.YD_UP_WO_LOC
			            --  , CS.YD_DN_WO_LOC
			            --  대차 도착시 명령선택을 위해 
			            -- , CASE WHEN CS.YD_SCH_CD LIKE 'J_TC0_L%' THEN 'XX010101' ELSE CS.YD_DN_WO_LOC END  AS YD_DN_WO_LOC
			              , CASE WHEN CS.YD_DN_WO_LOC IS NULL AND CS.YD_SCH_CD LIKE 'J_TC0_L%' THEN 'XX010101' 
			                     WHEN CS.YD_DN_WO_LOC IS NULL AND CS.YD_SCH_CD LIKE 'J_TC0_MM' THEN SUBSTR(CS.YD_SCH_CD,1,6) ||'00' 
			                     ELSE CS.YD_DN_WO_LOC END  AS YD_DN_WO_LOC
			              , CS.YD_SCH_CD
			              --작업상태
			              , DECODE(CS.YD_WRK_PROG_STAT,'W','0','S','1', CS.YD_WRK_PROG_STAT) AS YD_WRK_PROG_STAT_PRIOR
			              --우선순위
			              , CASE WHEN CS.YD_SCH_PRIOR = 0                                                    THEN 0
			                     WHEN CS.YD_CRN_GRAB_USE_RULE_ID = (SELECT YD_CRN_GRAB_USE_RULE_ID
			                                                          FROM TB_YD_CRNSCH
			                                                         WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			                                                           AND SUBSTR(YD_SCH_CD,3,2) IN('PT','TR'))     THEN 1  --차량만
			                     ELSE 2  END AS SEQ2 -- 이전
			              , CS.YD_SCH_PRIOR
			              , (SELECT CAR_NO FROM  TB_YD_WRKBOOK WB WHERE WB.YD_WBOOK_ID = CS.YD_WBOOK_ID) AS CAR_NO
			              , CM.STL_NO
			              , (SELECT YD_STK_BED_NO FROM TB_YD_EQPTRACKING WHERE STL_NO = CM.STL_NO AND ROWNUM = 1) AS YD_STK_BED_NO
			              , COUNT(CS.YD_CRN_SCH_ID) OVER (PARTITION BY CASE WHEN YD_SCH_CD = 'JATC01MM' THEN 'JAKD01LM'
			                                                                WHEN YD_SCH_CD = 'JATC02MM' THEN 'JAKD01LM'
			
			                                                        --WHEN YD_SCH_CD = 'JBTC01MM' THEN 'JBFD01LM'
			                                                        --WHEN YD_SCH_CD = 'JBTC02MM' THEN 'JBFD01LM'
			                                                        WHEN YD_SCH_CD = 'JBTC05MM' THEN 'JBKD01LM'
			
			                                                        WHEN YD_SCH_CD = 'JCTC01MM' THEN 'JCFD01LM'
			                                                        WHEN YD_SCH_CD = 'JCTC02MM' THEN 'JCFD01LM'
			                                                        WHEN YD_SCH_CD = 'JCTC05MM' THEN 'JCKD01LM'
			
			                                                        WHEN YD_SCH_CD = 'JETC01MM' THEN 'JEKD01LM'
			                                                        WHEN YD_SCH_CD = 'JETC02MM' THEN 'JEKD01LM'
			
			                                                        WHEN YD_SCH_CD = 'JGTC01MM' THEN 'JGFD01LM'
			                                                        WHEN YD_SCH_CD = 'JGTC02MM' THEN 'JGFD01LM'
			
			                                                        WHEN YD_SCH_CD = 'JHTC01MM' THEN 'JHKD01LM'
			                                                        WHEN YD_SCH_CD = 'JHTC02MM' THEN 'JHKD01LM'
			                                                        ELSE CS.YD_SCH_CD END) AS SCH_CNT
			           FROM TB_YD_CRNSCH    CS
			              , TB_YD_CRNWRKMTL CM
			          WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
			            AND CS.YD_GP  = 'J'
			            AND CS.DEL_YN = 'N'
			            AND CM.DEL_YN = 'N'
			            AND CS.YD_EQP_ID =  :V_YD_EQP_ID
			       ) A
			  ORDER BY YD_WRK_PROG_STAT_PRIOR DESC
			         , SEQ2 --차량단위 우선처리
			         , CASE WHEN CAR_NO LIKE 'GT%' THEN YD_SCH_PRIOR+1 ELSE YD_SCH_PRIOR END
			           -- 입고 3건 이상일때(결속장 제외) 입고 우선처리
			         , CASE WHEN YD_SCH_CD IN ('JATC01MM','JATC02MM','JAKD01LM',
			                                   'JBTC05MM','JBKD01LM',
			                                   'JCTC01MM','JCTC02MM','JCTC05MM','JCFD01LM','JCKD01LM',
			                                   'JETC01MM','JETC02MM','JEKD01LM',
			                                   'JGTC01MM','JGTC02MM','JGFD01LM',
			                                   'JHTC01MM','JHTC02MM','JHKD01LM')
			
			
			
			                 AND SCH_CNT >= 3
			                     THEN 1
			                ELSE 2 END
			         , CASE WHEN SUBSTR(YD_SCH_CD,3,2) IN ('TR','PT','TT') AND SUBSTR(YD_SCH_CD,0,1) IN ('J','H') THEN SUBSTR(YD_SCH_CD,4,1) ELSE '' END
			         -- SPM5, HFL4 추출은 뒤쪽 먼저
			         , CASE WHEN SUBSTR(YD_UP_WO_LOC, 0, 6) IN ('JAKD05', 'JCFD04') THEN TO_NUMBER(YD_STK_BED_NO) * -1
			                ELSE TO_NUMBER(YD_STK_BED_NO) END
			         , YD_CRN_SCH_ID
			*/
			JDTORecordSet jsCrnSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			//TO위치가 XX 인경우 다음 작업 지시 전송을 위한 목적 2024.01.29 CHITO		 
			String sAPP023_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "023");
			commUtils.printLog(logId,  "==========[[[ APP001 TO위치가 XX 인경우 다음 작업 지시 전송 : "+ sAPP023_YN +" ]]]============", "SL");
			if ("Y".equals(sAPP023_YN)) {

					if("Y".equals(ydToXXChk)){
						jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCrnSchY5YDL007TOXX", logId, mthdNm, "크레인스케줄 조회TOXX");				
					}else{
						jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCrnSchY5YDL007", logId, mthdNm, "크레인스케줄 조회");
					}
			}
			
			commUtils.printLog(logId, "크레인설비["+ydEqpId+"] 크레인 스케쥴 수 : " + jsCrnSch.size() , "SL");
		    
			if( jsCrnSch.size() > 0 ) {
				String ydWrkProgStat = "";
			    /**********************************************************
				* 2.1 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송
				**********************************************************/
				ydCrnSchId    = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				ydWrkProgStat = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
				ydSchCd       = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD"));

				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID

				commUtils.printLog(logId, "크레인설비["+ydEqpId+"] 크레인 스케쥴 : " + ydCrnSchId + "/긴급재 여부:" + ydCmdChk + "작업상태:" + ydWrkProgStat , "SL");
				
			    /***********************************************************
				*  야드 작업 진행상태가 S,1,2,3,5인 이고 긴급작업이 아닌경우 (작업지시를 재요구 하는 경우 사용한다.)
				***********************************************************/ 		    
	        	if (("S".equals(ydWrkProgStat)||"1".equals(ydWrkProgStat)||"2".equals(ydWrkProgStat)||"3".equals(ydWrkProgStat)||"5".equals(ydWrkProgStat)) 
	        		&& "N".equals(ydCmdChk)) {
	        		commUtils.printLog(logId, "야드 작업 진행상태가 S,1,2,3 이고 긴급작업이 아닌 경우 전문 재전송처리", "SL");
	        		
	        		/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdWorkDt 
	        		UPDATE TB_YD_CRNSCH
	        		   SET MODIFIER         = :V_MODIFIER
	        		     , MOD_DDTT         = SYSDATE
	        		     , YD_WORD_DT       = SYSDATE
	        		 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
	        		   AND DEL_YN           = 'N'
	        		   AND YD_WRK_PROG_STAT IN ('S', '1')			 
					*/
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdWorkDt", logId, mthdNm, "스케쥴 수정");

					// 분동코일 작업지시 요구 처리
					if( ydSchCd.indexOf("YD99MH") > -1 ) {
						
						jrParam.setField("MSG_GP", "U"); //전문구분 - 재지시
						jrParam.setField("STL_NO", commUtils.trim(jsCrnSch.getRecord(0).getFieldString("STL_NO"))); //분동코일 번호
						
						// PIDEV_S :병행가동용:PI_YD
						jrParam.setField("PI_YD", sPiYd);
						jrParam.setField("PI_YD1", sPiYd1);
						
						jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L004WC", jrParam));
						
					} else {
					
						String sAPP814_YN = coilDao.ApplyYn(logId, mthdNm, "APP814", "H", "S5");
						if ("Y".equals(sAPP814_YN) && "Y".equals(sAUTO_YN) && "H".equals(sYD_LOC_GP) && ydSchCd.endsWith("H")) {
							jrParam.setField("YD_CRN_SCH_RMD_CNT", "S5"  ); //S5 일시정지 후 권하위치 변경
						}
						
						//현재크레인이 작업중인 스케줄의 값을 다시 재전송한다.
						jrParam.setField("MSG_GP", "U"); //전문구분 - 재지시
						
						// PIDEV_S :병행가동용:PI_YD
						jrParam.setField("PI_YD", sPiYd);
						jrParam.setField("PI_YD1", sPiYd1);						
						
		            	jrRtn = commUtils.addSndData(coilDao.getMsgL2("YDY5L004", jrParam));
					}
	            	
				} else {
					/**********************************************************
					* 2.1.2 대기[W] 이면 다음 작업지시 전송
					**********************************************************/
					//설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "1"); //권상작업지시
					/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.updStatEqp
	        		UPDATE TB_YD_EQP 
	        		   SET MODIFIER    = :V_MODIFIER
	        		     , MOD_DDTT    = SYSDATE
	        		     , YD_EQP_STAT = :V_YD_EQP_STAT
	        		 WHERE YD_EQP_ID   = :V_YD_EQP_ID
	        		   AND DEL_YN      = 'N'
	        		*/
	        		commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.updStatEqp", logId, mthdNm, "설비TABLE 설비상태 수정(W)");	
	        		
	        		//크레인스케줄 야드작업진행상태 수정
					jrParam.setField("YD_WRK_PROG_STAT"	 , "W"); //대기  
					
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnSchW 
					UPDATE TB_YD_CRNSCH A
					   SET YD_WRK_PROG_STAT =:V_YD_WRK_PROG_STAT
					     , YD_WORD_DT       = NULL
					     , MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					     , YD_SCH_PRIOR     = (SELECT YD_WRK_CRN_PRIOR 
					                             FROM USRYDA.TB_YD_SCHRULE B
					                            WHERE B.YD_SCH_CD = A.YD_SCH_CD)
					 WHERE DEL_YN         = 'N'
					   AND YD_GP          = 'J'
					   AND YD_EQP_ID      = :V_YD_EQP_ID
					   AND YD_CRN_SCH_ID <> :V_YD_CRN_SCH_ID
					   AND YD_WRK_PROG_STAT != 'W'
					*/	   
	        		commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCrnSchW", logId, mthdNm, "해당크레인 야드작업진행상태 초기화");
	        		

	        		//크레인스케줄 야드작업진행상태 수정
					jrParam.setField("YD_WRK_PROG_STAT"	 , "S"); //선택지시
					jrParam.setField("YD_L2_REQUEST_STAT", "1"); 
					
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updStatCrnSchWrkProg 
					--크레인스케줄 작업진행상태 수정
					UPDATE TB_YD_CRNSCH
					   SET MODIFIER           = :V_MODIFIER
					     , MOD_DDTT           = SYSDATE
					     , YD_WRK_PROG_STAT   = :V_YD_WRK_PROG_STAT
					     , YD_WORD_DT         = DECODE(:V_YD_WRK_PROG_STAT,'S',SYSDATE,'1',SYSDATE,'W',NULL,YD_WORD_DT)
					     , YD_L2_REQUEST_STAT = NVL(:V_YD_L2_REQUEST_STAT,YD_L2_REQUEST_STAT) 
					 WHERE YD_CRN_SCH_ID      = :V_YD_CRN_SCH_ID
					   AND DEL_YN             = 'N'
					*/	   
	        		commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updStatCrnSchWrkProg", logId, mthdNm, "크레인스케줄 야드작업진행상태 수정");
	        		
					/**********************************************************
					* 2.1.3 대차상차 작업 권하번지 확정
					*  - 동간이적
					*  - 동간입고
					**********************************************************/
					commUtils.printLog(logId, "신로직", "SL");
	        		
	        		String ydDnWoLoc = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC"));
	        		commUtils.printLog(logId, "대차 권하위치 체크:"+ ydDnWoLoc, "SL");
	        		if( "TC".equals(ydSchCd.substring(2,4)) && "00".equals(ydDnWoLoc.substring(6,8)) ) {
	        			commUtils.printLog(logId, "신로직1", "SL");
	        			// 작업예약 조회
	        			String ydWbookId		= commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID"));
	        			commUtils.printLog(logId, "신로직2", "SL");
	        			JDTORecord jrParamSet	= commUtils.getParam(logId, mthdNm, sModifier);
	        			jrParamSet.setField("YD_WBOOK_ID"	, ydWbookId);
	        			jrParamSet.setField("YD_EQP_ID"		, ydEqpId);

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
	                	JDTORecordSet jsWbook = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdWrkbook", logId, mthdNm, "작업예약 조회(대차상차)"); 
	        	    	
	        	    	if (jsWbook.size() < 1) {
	        	    		sMsg = "오류:작업 예약 조회 시 오류 ydWbookId: " + ydWbookId;
	        				
	        				commUtils.printLog(logId, sMsg, "SL");
	        				
	        				jrRtn.setField("RTN_CD" , "0");	
	            			jrRtn.setField("RTN_MSG", sMsg);
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
	        	    	JDTORecordSet jsCrnSchTCar = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnSchByWBookId", logId, mthdNm, "크레인스케줄 조회(대차상차)"); 
	        	    	
	        	    	if (jsCrnSchTCar.size() < 1) {
	        	    		sMsg = "오류:크레인스케쥴 조회 시 오류 ydWbookId: " + ydWbookId;
	        				commUtils.printLog(logId, sMsg, "SL");
	        				
	        				jrRtn.setField("RTN_CD" , "0");	
	            			jrRtn.setField("RTN_MSG", sMsg);
	            			return jrRtn;
	        			}
	        	    	
	        	    	jsWbook.first();
	        	    	jsCrnSchTCar.first();
	        	    	
	        	    	JDTORecord jrWbook = jsWbook.getRecord();
	        	    	JDTORecord jrCrnSchTCar = jsCrnSchTCar.getRecord();
	        	    	
	        	    	JDTORecord jrSchRtn = JDTORecordFactory.getInstance().create();
	        	    	String rtnCd = "0";
	        	    	String rtnMg = "";
	        	    	
//	        	    	String sAPP839_YN = coilDao.ApplyYn(logId, mthdNm, "APP839", "J", "*"); // 제품 동간이적 스케줄 로직분리
//	        			
//	        	    	if ("Y".equals(sAPP839_YN) && "UM".equals(ydSchCd.substring(6,8))) {
//	        	    		//제품 동간이적 상차는 스케줄 기동시에 To위치가 정해진다
//	        	    	} else
	        			if ("U".equals(ydSchCd.substring(6,7))) {
	        				
	        				/********************************************
	        				 * 동간이적 - 사용자지정작업
	        				 ********************************************/
	        				String ydWrkPlanTcar	= commUtils.trim(jrWbook.getFieldString("YD_WRK_PLAN_TCAR"));	// 대차설비코드
	        				String ydAimBayGp		= commUtils.trim(jrWbook.getFieldString("YD_AIM_BAY_GP"));		// 목적동
	        				
	        				jrParamSet = commUtils.getParam(logId, mthdNm, sModifier);
		        			jrParamSet.setField("YD_EQP_ID"		, ydWrkPlanTcar);	// 지정대차 설비코드
		        			jrParamSet.setField("YD_AIM_BAY_GP"	, ydAimBayGp);		// 목적동
	            			/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getTcBayWrk
	            			-- 대차 목적동에 대차작업 여부 
	            			SELECT A.YD_EQP_ID
	            			     , A.YD_CURR_BAY_GP
	            			     , (SELECT COUNT(*) 
	            			          FROM TB_YD_STKLYR 
	            			         WHERE SUBSTR(YD_STK_COL_GP,1,4) = SUBSTR(A.YD_EQP_ID,1,1)||B.YD_AIM_BAY_GP ||'TC' 
	            			           AND DEL_YN = 'N'
	            			           AND STL_NO IS NOT NULL
	            			       ) --상차된 경우
	            			       + 
	            			--       (SELECT DECODE(COUNT(*),1,0,9) 
	            			       (SELECT COUNT(*)
	            			          FROM TB_YD_TCARSCH 
	            			         WHERE DEL_YN = 'N'
	            			           AND YD_EQP_ID = A.YD_EQP_ID
	            			           AND YD_CAR_PROG_STAT IN('0','2')
	            			       ) -- 대차스케쥴 상태대기
	            			       + 
	            			       (SELECT COUNT(*)
	            			          FROM TB_YD_CRNSCH 
	            			         WHERE DEL_YN = 'N'
	            			           AND YD_GP  = SUBSTR(A.YD_EQP_ID,1,1)
	            			           AND SUBSTR(YD_DN_WO_LOC,1,4) = SUBSTR(A.YD_EQP_ID,1,1)||B.YD_AIM_BAY_GP ||'TC'
	            			       ) -- 크레인작업지시 편성여부
	            			       
	            			       AS WK_CNT
	            			  FROM TB_YD_EQP A
	            			     , (SELECT :V_YD_AIM_BAY_GP AS YD_AIM_BAY_GP
	            			          FROM DUAL) B
	            			 WHERE A.YD_EQP_ID = :V_YD_EQP_ID
	            			   AND A.YD_EQP_STAT <> 'B'
	            			*/
							//대차정보 조회
		        			JDTORecordSet jsTcarInfo = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getTcBayWrk", logId, mthdNm, "대차정보조회");

							if (jsTcarInfo.size() > 0) {

								JDTORecord jrTCarInfo = jsTcarInfo.getRecord(0);
								String ydTcarCurrBayGp  = commUtils.trim(jrTCarInfo.getFieldString("YD_CURR_BAY_GP" )); // 현재동
								                                 //작업위치
								jrParamSet.setField("YD_STK_COL_GP", ydWrkPlanTcar.substring(0,1) + ydSchCd.substring(1,2) + ydWrkPlanTcar.substring(2,6));

								commUtils.printLog(logId,  "현 대차위치(ydTcarCurrBayGp):" + ydTcarCurrBayGp, "SL");

								if (ydSchCd.substring(1,2).equals(ydTcarCurrBayGp)) {
									
									String sStlNo = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
									
									//대차위치가  같은동에 있는 경우
									commUtils.printLog(logId,  " : 대차 위치가  작업동과 같은동에 있는 경우", "SL");
			        				commUtils.printLog(logId,  " TOSQL"+ydCrnSchId+ " 권상재료["+sStlNo +"] 의 적치가능한 베드 조회 시작", "SL");
			        				/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkBedBybedTc
			        				-- 캐빈방향에 따라 ORDER BY 변경
			        				WITH PARM_TBL AS (
			        				    SELECT :V_YD_STK_COL_GP AS YD_STK_COL_GP FROM DUAL
			        				)
			        				SELECT YD_STK_COL_GP
			        				     , YD_STK_BED_NO
			        				     , YD_STK_LYR_NO
			        				     , YD_MTL_SH
			        				  FROM (SELECT SB.YD_STK_COL_GP
			        				             , SB.YD_STK_BED_NO
			        				             , SL.YD_STK_LYR_NO
			        				             , COUNT(SL.STL_NO) AS YD_MTL_SH
			        				          FROM TB_YD_STKBED SB
			        				             , TB_YD_STKLYR SL
			        				             , PARM_TBL PT
			        				         WHERE SB.YD_STK_COL_GP = PT.YD_STK_COL_GP
			        				           AND SB.YD_STK_COL_GP = SL.YD_STK_COL_GP
			        				           AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO
			        				           AND SB.DEL_YN        = 'N'
			        				           AND SL.YD_STK_LYR_MTL_STAT = 'E' --적치가능
			        				--           AND SUBSTR(SB.YD_STK_COL_GP,2,1) IN ('D', 'E', 'F', 'G', 'H')
			        				         GROUP BY SB.YD_STK_COL_GP, SB.YD_STK_BED_NO, SL.YD_STK_LYR_NO
			        				         ORDER BY SB.YD_STK_COL_GP
			        				                , (CASE WHEN SB.YD_STK_BED_NO='01' THEN 1
			        				                        WHEN SB.YD_STK_BED_NO='02' THEN 3
			        				                        ELSE 2 END)
			        				                , SL.YD_STK_LYR_NO
			        				       )
			        				 WHERE YD_MTL_SH = 0
			        				   AND ROWNUM    = 1
				    				*/
			        				JDTORecordSet jsTcarBed = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdStkBedBybedTc", logId, mthdNm, "대차 조회");

				            		if (jsTcarBed.size() <= 0) {
				        				commUtils.printLog(logId, "대차 BED READ 실패!", "SL");
				            		} else {
				            			JDTORecord jrTcarBed  = jsTcarBed.getRecord(0);

				    					String ydStkColGp	= commUtils.trim(jrTcarBed.getFieldString("YD_STK_COL_GP"));//대차정지위치 적치열
				    					String ydStkBedNo	= commUtils.trim(jrTcarBed.getFieldString("YD_STK_BED_NO"));//대차정지위치 적치베드
				    					String ydStkLyrNo	= commUtils.trim(jrTcarBed.getFieldString("YD_STK_LYR_NO"));//대차정지위치 적치단

				    					JDTORecord jrChkTcLocAble = commUtils.getParam(logId, mthdNm, sModifier);
				    					jrChkTcLocAble.setField("YD_WBOOK_ID" , ydWbookId);
				    					jrChkTcLocAble.setField("YD_SCH_CD"   , ydSchCd);

				    					//대차위 상차가능 중량 및 매수
				    					boolean bTcLocAbleYn = coilDao.chkTcLocAble(jrChkTcLocAble);
				    					if (bTcLocAbleYn) {
				    						
				    						// 대차상차위치로 YD_TO_LOC_GUIDE 수정
				    						jrWbook.setField("YD_TO_LOC_GUIDE", ydStkColGp + ydStkBedNo + ydStkLyrNo);

					        				EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this);
					        				jrSchRtn = (JDTORecord)ejbConn.trx("toLocUser", new Class[] { String.class, String.class, JDTORecord.class, JDTORecord.class }
					        				                                                          , new Object[] {logId, mthdNm, jrWbook, jrCrnSchTCar });

											rtnCd	= commUtils.nvl(jrSchRtn.getFieldString("RTN_CD"), "0");
								            rtnMg	= commUtils.nvl(jrSchRtn.getFieldString("RTN_MSG"), "");
								            if("0".equals(rtnCd)){
								            	rtnXXLogIn = "Y";
								            }
								            
				    					}
				            		}
								}
							}
	        			} else if( "MM".equals(ydSchCd.substring(6,8)) ) {
	        				
	        				/********************************************
	        				 * 동간입고 - 주작업TO위치
	        				 ********************************************/
	        				EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this);
	        				jrSchRtn = (JDTORecord)ejbConn.trx("toLocPrimaryWork", new Class[] { String.class, String.class, JDTORecord.class, JDTORecord.class }, new Object[] {logId, mthdNm, jrWbook, jrCrnSchTCar });
	        				
							rtnCd	= commUtils.nvl(jrSchRtn.getFieldString("RTN_CD"), "0");
				            rtnMg	= commUtils.nvl(jrSchRtn.getFieldString("RTN_MSG"), "");
				            
				            if("0".equals(rtnCd)){
				            	rtnXXLogIn = "Y";
				            }
				            // 권하완료시 출발지시송신여부 & 주작업TO위치 성공
				            if( "N".equals(tcarLdCmplYn) && !"0".equals(rtnCd) ) {
				            	
				            	// 대차 이동지시 송신처리
					            String sTcMoveYn = commUtils.nvl(jrSchRtn.getFieldString("TC_MOVE_YN"), "N");
					            commUtils.printLog(logId, "대차이동지시 송신 여부(sTcMoveYn) :" + sTcMoveYn, "SL");
					            
								if ("Y".equals(sTcMoveYn)) {
	
									JDTORecord jrTcSnd = commUtils.getParam(logId, mthdNm, sModifier);
									jrTcSnd.setField("YD_SCH_CD", ydSchCd);
	
									/***********************************************************
									*  제품은 중량오버인 경우 대차 자동 출발
									***********************************************************/
									ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
									JDTORecord jrTCarSts = (JDTORecord)ejbConn.trx("procTcarStsSetTcarD", new Class[] { JDTORecord.class }, new Object[] { jrTcSnd });
									
									jrRtn = commUtils.addSndData(jrRtn, jrTCarSts);
								}
				            }
	        			}
	        			
	        			if( "0".equals(rtnCd) ) {
			            	// TO위치 결정 실패시 XX010101 업데이트 처리
	        				commUtils.printLog(logId, rtnMg, "SL");
	        				
	        				jrParamSet	= commUtils.getParam(logId, mthdNm, sModifier);
		        			jrParamSet.setField("YD_CRN_SCH_ID" , ydCrnSchId);
		        			jrParamSet.setField("YD_DN_WO_LOC"  , "XX010101");
	    					
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
						    int intRtnVal = commDao.update(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updCrnWrkMgtDnLoc", logId, mthdNm, "크레인스케줄 갱신");
						    /********************************************************
							 * 권하위치 XX 일때 로그테이블에 등록
							 ********************************************************/
						    JDTORecord jrInXXLog 	= commUtils.getParam(logId, mthdNm, sModifier);
							jrInXXLog.setField("YD_WBOOK_ID" , ydWbookId);
							jrInXXLog.setField("YD_SCH_CD"   , ydSchCd);
							jrInXXLog.setField("YD_EQP_ID"   , ydEqpId);	
							jrInXXLog.setField("STL_NO"      , commUtils.trim(jrWbook.getFieldString("STL_NO")));		
							jrInXXLog.setField("YD_CRN_SCH_ID"     , ydCrnSchId);
							jrInXXLog.setField("YD_LOC_ABLECHECK"  , "rcvY5YDL007_1"); 								
							jrInXXLog.setField("YD_CRNSCHLOC_NOTE"		, "S");
							if("Y".equals(rtnXXLogIn) ){
								EJBConnector SchXXLog = new EJBConnector("default", "CCoilSchSeEJB", this);
								SchXXLog.trx( "insSchXXLog" , new Class[] { JDTORecord.class }, new Object[] { jrInXXLog });
							}
							if (intRtnVal <= 0) {
								sMsg = "크레인스케줄 To위치 Default값 등록 실패!!";
			    				commUtils.printLog(logId, sMsg, "SL");
			    				jrRtn.setField("RTN_CD"		, "0");
			    				jrRtn.setField("RTN_MSG"	, sMsg);
			        			return jrRtn;
							}
			            }
	        		}

	        		/***********************************************************
					*  수입 XX를 명령선택 기동
					***********************************************************/
					if( "CV".equals(ydSchCd.substring(2,4)) ) {
						if("XX010101".equals(ydDnWoLoc)) {
	        				
							// 작업예약 조회
		        			String ydWbookId		= commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID"));
		        			commUtils.printLog(logId, "신로직2", "SL");
		        			JDTORecord jrParamSet	= commUtils.getParam(logId, mthdNm, sModifier);
		        			jrParamSet.setField("YD_WBOOK_ID"	, ydWbookId);
		        			jrParamSet.setField("YD_EQP_ID"		, ydEqpId);
	
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
		                	JDTORecordSet jsWbook = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdWrkbook", logId, mthdNm, "작업예약 조회(대차상차)"); 
		        	    	
		        	    	if (jsWbook.size() < 1) {
		        	    		sMsg = "오류:작업 예약 조회 시 오류 ydWbookId: " + ydWbookId;
		        				
		        				commUtils.printLog(logId, sMsg, "SL");
		        				
		        				jrRtn.setField("RTN_CD" , "0");	
		            			jrRtn.setField("RTN_MSG", sMsg);
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
		        	    	JDTORecordSet jsCrnSchTCar = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdCrnSchByWBookId", logId, mthdNm, "크레인스케줄 조회(대차상차)"); 
		        	    	
		        	    	if (jsCrnSchTCar.size() < 1) {
		        	    		sMsg = "오류:크레인스케쥴 조회 시 오류 ydWbookId: " + ydWbookId;
		        				commUtils.printLog(logId, sMsg, "SL");
		        				
		        				jrRtn.setField("RTN_CD" , "0");	
		            			jrRtn.setField("RTN_MSG", sMsg);
		            			return jrRtn;
		        			}
		        	    	
		        	    	jsWbook.first();
		        	    	jsCrnSchTCar.first();
		        	    	
		        	    	JDTORecord jrWbook = jsWbook.getRecord();
		        	    	JDTORecord jrCrnSchTCar = jsCrnSchTCar.getRecord();
		        	    	jrWbook.setField("RE_TOLOC_YN", "N");//권하위치 재설정 사용여부
	        				/********************************************
	        				 * 수입 - 주작업TO위치
	        				 ********************************************/
	        				EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this);
	        				JDTORecord jrSchRtn = (JDTORecord)ejbConn.trx("toLocPrimaryWorkCV", new Class[] { String.class, String.class, JDTORecord.class, JDTORecord.class }, new Object[] {logId, mthdNm, jrWbook, jrCrnSchTCar });
	        				
							String rtnCd = commUtils.nvl(jrSchRtn.getFieldString("RTN_CD"), "0");
							String rtnMg = commUtils.nvl(jrSchRtn.getFieldString("RTN_MSG"), "");
							
							if( "0".equals(rtnCd) ) {
				            	// TO위치 결정 실패시 XX010101 업데이트 처리
		        				commUtils.printLog(logId, rtnMg, "SL");
		        				
		        				jrParamSet	= commUtils.getParam(logId, mthdNm, sModifier);
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
							    /********************************************************
								 * 권하위치 XX 일때 로그테이블에 등록
								 ********************************************************/
								    JDTORecord jrInXXLog 	= commUtils.getParam(logId, mthdNm, sModifier);
									jrInXXLog.setField("YD_WBOOK_ID" , ydWbookId);
									jrInXXLog.setField("YD_SCH_CD"   , ydSchCd);
									jrInXXLog.setField("YD_EQP_ID"   , ydEqpId);	
									jrInXXLog.setField("STL_NO"      , commUtils.trim(jrWbook.getFieldString("STL_NO")));		
									jrInXXLog.setField("YD_CRN_SCH_ID"     , ydCrnSchId);
									jrInXXLog.setField("YD_LOC_ABLECHECK"  , "rcvY5YDL007_CV"); 
									jrInXXLog.setField("YD_CRNSCHLOC_NOTE"		, "S");
									EJBConnector SchXXLog = new EJBConnector("default", "CCoilSchSeEJB", this);
									SchXXLog.trx( "insSchXXLog" , new Class[] { JDTORecord.class }, new Object[] { jrInXXLog });
									
									if (intRtnVal <= 0) {
										sMsg = "크레인스케줄 To위치 Default값 등록 실패!!";
					    				commUtils.printLog(logId, sMsg, "SL");
					    				jrRtn.setField("RTN_CD"		, "0");
					    				jrRtn.setField("RTN_MSG"	, sMsg);
					        			return jrRtn;
									}	 
									
									sAPP023_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "023");
									commUtils.printLog(logId,  "==========[[[ APP001 TO위치가 XX 인경우 다음 작업 지시 전송2 : "+ sAPP023_YN +" SCH_CD :"+ydSchCd+" ]]]============", "SL");
									if ("Y".equals(sAPP023_YN) && "CV".equals(ydSchCd.substring(2,4))) {
										
										jrSndMsg.setField("YD_L3_HD_RS_CD", "8888" ); //야드L3처리결과코드
										jrSndMsg.setField("YD_L3_MSG"     , "코일번호[" + commUtils.trim(jrWbook.getFieldString("STL_NO")) + "] to위치 없음"); //야드L3MESSAGE 
										
										//크레인작업실적응답 전문 전송
										EJBConnector resConn = new EJBConnector("default", "CCommSeEJB", this);
										resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { coilDao.getYDY5L005(jrSndMsg) });
										
										commUtils.printLog(logId, "크레인 스케쥴 to위치 없음 전송2> "+ ydCrnSchId, "SL");
										
										//TO위치가 XX면  명령선택전문 전송 하여 다른 작업 지시를 전송 한다.
										JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
										jrYdMsg.setField("JMS_TC_CD"         , "Y5YDL007"               ); //JMSTC코드
										jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
										jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
										jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태 
										jrYdMsg.setField("YD_TOXX_CHK"  	 , "Y"		             	); //TO위치가 XX 결정된 경우 다른 크레인 작업 호출 
										
										 
										jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
										
										commUtils.printLog(logId, mthdNm, "S-");
										jrRtn.setField("RTN_CD" 		, "1");
										return jrRtn;
									}else{
										ydXXCV      = "Y"; // 명령선택 재실행.지시 삭제후			
									}
				            }							
						}
					}

					/***********************************************************
					*  제품입고 를 명령선택 기동
					***********************************************************/
					JDTORecord jrParamSet	= commUtils.getParam(logId, mthdNm, sModifier);
					// 작업예약 조회
        			String ydWbookId		= commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID"));
        			jrParamSet.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
        			jrParamSet.setField("YD_WBOOK_ID"	, ydWbookId);
					jrParamSet.setField("YD_SCH_CD"	    , ydSchCd);
        			jrParamSet.setField("YD_EQP_ID"		, ydEqpId);
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getToLocStartUpYn  
					SELECT YD_SCH_CD
					  FROM TB_YD_SCHRULE
					 WHERE YD_SCH_CD = :V_YD_SCH_CD
					   AND DEL_YN = 'N'
					   AND YD_SCH_CD IN ('JAKD01LM','JBKD01LM','JCKD01LM','JHKD01LM','JEKD01LM'
					                    ,'JCFD01LM','JGFD01LM')
					*/
					JDTORecordSet jsSchStartUp = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getToLocStartUpYn", logId, mthdNm, "TO위치재기동 조회");
					if (jsSchStartUp.size() > 0) {
						
						if("XX010101".equals(ydDnWoLoc)) {
	        				
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
		                	JDTORecordSet jsWbook = commDao.select(jrParamSet, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getYdWrkbook", logId, mthdNm, "작업예약 조회(대차상차)"); 
		        	    	
		        	    	if (jsWbook.size() < 1) {
		        	    		sMsg = "오류:작업 예약 조회 시 오류 ydWbookId: " + ydWbookId;
		        				
		        				commUtils.printLog(logId, sMsg, "SL");
		        				
		        				jrRtn.setField("RTN_CD" , "0");	
		            			jrRtn.setField("RTN_MSG", sMsg);
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
		        	    		sMsg = "오류:크레인스케쥴 조회 시 오류 ydWbookId: " + ydWbookId;
		        				commUtils.printLog(logId, sMsg, "SL");
		        				
		        				jrRtn.setField("RTN_CD" , "0");	
		            			jrRtn.setField("RTN_MSG", sMsg);
		            			return jrRtn;
		        			}
		        	    	
		        	    	jsWbook.first();
		        	    	jsCrnSchKd.first();
		        	    	
		        	    	JDTORecord jrWbook = jsWbook.getRecord();
		        	    	JDTORecord jrCrnSchKd = jsCrnSchKd.getRecord();
		        	    	
	        				/********************************************
	        				 * 일반입고 - 주작업TO위치
	        				 ********************************************/
	        				EJBConnector ejbConn = new EJBConnector("default","CCoilSchSeEJB", this);
	        				JDTORecord jrSchRtn = (JDTORecord)ejbConn.trx("toLocPrimaryWorkKD", new Class[] { String.class, String.class, JDTORecord.class, JDTORecord.class }, new Object[] {logId, mthdNm, jrWbook, jrCrnSchKd });
	        				
							String rtnCd = commUtils.nvl(jrSchRtn.getFieldString("RTN_CD"), "0");
							String rtnMg = commUtils.nvl(jrSchRtn.getFieldString("RTN_MSG"), "");
							
							if( "0".equals(rtnCd) ) {
				            	// TO위치 결정 실패시 XX010101 업데이트 처리
		        				commUtils.printLog(logId, rtnMg, "SL");
		        				
		        				jrParamSet	= commUtils.getParam(logId, mthdNm, sModifier);
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
							    	/********************************************************
									 * 권하위치 XX 일때 로그테이블에 등록
									 ********************************************************/
								    JDTORecord jrInXXLog 	= commUtils.getParam(logId, mthdNm, sModifier);
									jrInXXLog.setField("YD_WBOOK_ID" , ydWbookId);
									jrInXXLog.setField("YD_SCH_CD"   , ydSchCd);
									jrInXXLog.setField("YD_EQP_ID"   , ydEqpId);	
									jrInXXLog.setField("STL_NO"      , commUtils.trim(jrWbook.getFieldString("STL_NO")));		
									jrInXXLog.setField("YD_CRN_SCH_ID"     , ydCrnSchId);
									jrInXXLog.setField("YD_LOC_ABLECHECK"  , "rcvY5YDL007_KD"); 								
									jrInXXLog.setField("YD_CRNSCHLOC_NOTE"		, "S");
									EJBConnector SchXXLog = new EJBConnector("default", "CCoilSchSeEJB", this);
									SchXXLog.trx( "insSchXXLog" , new Class[] { JDTORecord.class }, new Object[] { jrInXXLog });
									
								if (intRtnVal <= 0) {
									sMsg = "크레인스케줄 To위치 Default값 등록 실패!!";
				    				commUtils.printLog(logId, sMsg, "SL");
				    				jrRtn.setField("RTN_CD"		, "0");
				    				jrRtn.setField("RTN_MSG"	, sMsg);
				        			return jrRtn;
								}
				            }
						}
					}
					/******************************************************
					 * 수입에서 권하위치x 될때 보급 / 이적 / 이송작업 있을시 자동으로 변경 돼게 수정.
					 * 20230829 YYS
					 *******************************************************/
					/**********************************************************
					* 5.크레인작업지시 호출
		  			**********************************************************/					
					 
					if ("Y".equals(ydXXCV)) {//수입 권하 xx 일때
						
						commUtils.printLog(logId, "수입 XX 후 다른 작업지시 전문 송신", "SL");

//						JDTORecord jrYdCVMsg = commUtils.getParam(logId, mthdNm, "XXREACT");
//
//		        		/***********************************************************
//						 * 크레인의 다음 스케줄 명령 선택 기동 - 운전모드변경 명령선택 기동 호출
//						 ***********************************************************/
//						//야드설비상태가 대기이면 명령선택전문 전송
//		        		jrYdCVMsg.setField("JMS_TC_CD"         , "Y5YDL007"               ); //JMSTC코드
//		        		jrYdCVMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
//		        		jrYdCVMsg.setField("YD_EQP_ID"         , ydEqpId               ); //야드설비ID
//		        		jrYdCVMsg.setField("YD_WRK_PROG_STAT"  , "W"             ); //야드작업진행상태
//		        		jrYdCVMsg.setField("YD_CV_CHK"         , "Y"             ); //수입권하XX 일때 명령선택기동 호출
//		        		jrYdCVMsg.setField("YD_SCH_CD"         , ydSchCd         );
//						
//						
//							//설비의 야드설비상태 수정 원위치
//							jrParam.setField("YD_EQP_STAT", "W"); //작업지시
//							/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.updStatEqp
//			        		UPDATE TB_YD_EQP 
//			        		   SET MODIFIER    = :V_MODIFIER
//			        		     , MOD_DDTT    = SYSDATE
//			        		     , YD_EQP_STAT = :V_YD_EQP_STAT
//			        		 WHERE YD_EQP_ID   = :V_YD_EQP_ID
//			        		   AND DEL_YN      = 'N'
//			        		*/
//			        		commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.updStatEqp", logId, mthdNm, "설비TABLE 설비상태 수정(W)");	
//			        		
//			        		//크레인스케줄 야드작업진행상태 수정
//							jrParam.setField("YD_WRK_PROG_STAT"	 , "W"); //선택대기
//							
//							/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updStatCrnSchWrkProg 
//							--크레인스케줄 작업진행상태 수정
//							UPDATE TB_YD_CRNSCH
//							   SET MODIFIER           = :V_MODIFIER
//							     , MOD_DDTT           = SYSDATE
//							     , YD_WRK_PROG_STAT   = :V_YD_WRK_PROG_STAT
//							     , YD_WORD_DT         = DECODE(:V_YD_WRK_PROG_STAT,'S',SYSDATE,'1',SYSDATE,'W',NULL,YD_WORD_DT)
//							     , YD_L2_REQUEST_STAT = NVL(:V_YD_L2_REQUEST_STAT,YD_L2_REQUEST_STAT) 
//							 WHERE YD_CRN_SCH_ID      = :V_YD_CRN_SCH_ID
//							   AND DEL_YN             = 'N'
//							*/	   
//			        		commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updStatCrnSchWrkProg", logId, mthdNm, "크레인스케줄 야드작업진행상태 수정");
//			        		
//							
//			        	 	//명령선택기동.	
//		                    //EJBConnector  ejbConn = new EJBConnector("default", "CCoilL2RcvSeEJB", this);
//							//JDTORecord jrRtnL007 = (JDTORecord)ejbConn.trx("rcvY5YDL007", new Class[] { JDTORecord.class }, new Object[] { jrYdCVMsg });
//							
//							//jrRtn = commUtils.addSndData(jrRtn,  jrRtnL007 );		
//							jrRtn = commUtils.addSndData(jrRtn, jrYdCVMsg);
					}else{
					
					  // 작업지시 전문 송신
	        		   jrParam.setField("MSG_GP", "I"); //전문구분 - 신규
	        		   jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L004", jrParam));
	        		   
	        		   ydXXCHK ="N";
					}
				}
	        	
	        	/**********************************************************
				* 2.2.1 작업지시 to위치  없음 전송
				**********************************************************/
	        	
	        	String sAPP016_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","016"); 
				commUtils.printLog(logId, "작업지시 to위치  없음 전송 : " + sAPP016_YN, "SL");
				if ("Y".equals(sAPP016_YN) &&  "Y".equals(ydXXCHK)) {//수입 권하 xx 일때
	        	 
		        	jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchId);
		        
		        	JDTORecordSet jsCrnsch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCrnsch", logId, mthdNm, "크레인 스케쥴 조회");
	
	        		if (jsCrnsch.size() > 0) {
	    				    				
	    				JDTORecord jrCrnsch  = jsCrnsch.getRecord(0);
						String ydDnWoLoc	= commUtils.trim(jrCrnsch.getFieldString("YD_DN_WO_LOC"));//to위치 
						String ydStlNo	= commUtils.trim(jrCrnsch.getFieldString("STL_NO"));//to위치 
						
						commUtils.printLog(logId, "크레인 스케쥴 조회 완료> "+ ydDnWoLoc, "SL");
						
						if("XX010101".equals(ydDnWoLoc)){
						
							jrSndMsg.setField("YD_L3_HD_RS_CD", "8888" ); //야드L3처리결과코드
							jrSndMsg.setField("YD_L3_MSG"     , "코일번호[" + ydStlNo + "] to위치 없음"); //야드L3MESSAGE 
							
							//크레인작업실적응답 전문 전송
							EJBConnector resConn = new EJBConnector("default", "CCommSeEJB", this);
							resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { coilDao.getYDY5L005(jrSndMsg) });
							
							commUtils.printLog(logId, "크레인 스케쥴 to위치 없음 전송> "+ ydCrnSchId, "SL");
						}
	        		}  
	        	}
        									
	        	
			} else {
				
				
				/**********************************************************
				* 2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
				*    2.1 권상지시[1], 권상완료[2], 권하지시[3] 이면  작업지시 없음 전송
				*        - [W] 이면 : 작업 예약 검색 안함  
				*    2.2 권하완료[4] 이면 스케줄을 생성
				**********************************************************/
				if ("W".equals(rcvYdWrkProgStat) ||"1".equals(rcvYdWrkProgStat) || "2".equals(rcvYdWrkProgStat) || "3".equals(rcvYdWrkProgStat)) {
					/**********************************************************
					* 2.2.1 작업지시 없음 전송
					**********************************************************/
					jrSndMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					jrSndMsg.setField("YD_L3_MSG"     , "크레인[" + ydEqpId + "-" + rcvYdWrkProgStat + "] 작업지시 없음"); //야드L3MESSAGE
					
					jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(jrSndMsg));

					commUtils.printLog(logId, "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + ydEqpId + " : " + rcvYdWrkProgStat + " - " + ydCrnSchId + " ]", "SL");
				} else {
					
					/**********************************************************
					* 2.2.2 권하완료[4]시 작업 예약 검색후 스케쥴 기동 
					**********************************************************/
					//크레인작업지시가 없으면 설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "W"); //대기(Wait)

					/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.updStatEqp
	        		UPDATE TB_YD_EQP
	        		   SET MODIFIER    = :V_MODIFIER
	        		     , MOD_DDTT    = SYSDATE
	        		     , YD_EQP_STAT = :V_YD_EQP_STAT
	        		 WHERE YD_EQP_ID   = :V_YD_EQP_ID
	        		   AND DEL_YN      = 'N'
	        		*/	   
	        		commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.updStatEqp", logId, mthdNm, "설비상태 수정");
	        		
	    			//작업예약 조회
	        		/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getWorkTbRefNONECoil
	        		SELECT *
	        		  FROM (SELECT A.*
	        		             , ROW_NUMBER() OVER(PARTITION BY YD_SCH_CD ORDER BY ROWNUM) AS RANK_CNT
	        		             , CASE WHEN SUBSTR(A.YD_SCH_CD,8,1) = 'H' THEN
	        		                    CASE WHEN SUBSTR(A.YD_SCH_CD,3,2) = 'TC' AND SUBSTR(A.YD_SCH_CD,7,1) IN ('U') 
	        		                              THEN CASE WHEN SUBSTR(A.YD_SCH_CD,2,1) <> A.YD_CURR_BAY_GP            THEN 'Y' 
	        		                                        WHEN A.YD_EQP_STAT <> 'A'                                   THEN 'Y' 
	        		                                        WHEN A.TC_CNT + T_CRN_CNT + 1          <= A.STK_BED_MAX_QNTY 
	        		                                         AND A.TC_WGT + T_CRN_WGT + A.COIL_WT  <= A.STK_BED_MAX_WT  THEN 'N'
	        		                                   ELSE 'Y' END 
	        		                         ELSE 'N' END  
	        		               ELSE             
	        		                    CASE WHEN SUBSTR(A.YD_SCH_CD,3,2) = 'TC' AND SUBSTR(A.YD_SCH_CD,7,1) IN ('U') 
	        		                              THEN CASE WHEN SUBSTR(A.YD_SCH_CD,2,1) <> A.YD_CURR_BAY_GP            THEN 'Y' 
	        		                                        WHEN A.YD_EQP_STAT <> 'A'                                   THEN 'Y' 
	        		                                        WHEN A.TC_CNT + 1          <= A.STK_BED_MAX_QNTY 
	        		                                         AND A.TC_WGT + A.COIL_WT  <= A.STK_BED_MAX_WT  THEN 'N'
	        		                                   ELSE 'Y' END 
	        		                         ELSE 'N' END  
	        		               END AS TC_CHK
	        		          FROM (SELECT WB.YD_WBOOK_ID
	        		                     , WB.YD_SCH_CD
	        		                     , WB.YD_SCH_PRIOR
	        		                     , WB.YD_WRK_PLAN_TCAR
	        		                     , WB.CAR_NO
	        		                     , EQ.STK_BED_MAX_QNTY
	        		                     , EQ.STK_BED_MAX_WT
	        		                     , EQ.YD_CURR_BAY_GP
	        		                     , EQ.YD_EQP_ID
	        		                     , EQ.YD_EQP_STAT
	        		                     , CC.COIL_WT
	        		                       -- 대차상차인 경우 현재위치:중량 및 매수 CHECK
	        		--                     , (SELECT CASE WHEN SUBSTR(WB.YD_SCH_CD,2,1) <> EQ.YD_CURR_BAY_GP THEN 'N'
	        		--                                    WHEN SUBSTR(WB.YD_SCH_CD,3,2) = 'TC' AND SUBSTR(WB.YD_SCH_CD,7,1) IN ('U')
	        		--                                         THEN (SELECT CASE WHEN NVL(COUNT(*),0)+1 <= EQ.STK_BED_MAX_QNTY
	        		--                                                            AND NVL(SUM(C.COIL_WT),0) + CC.COIL_WT  <= EQ.STK_BED_MAX_WT  THEN 'N'
	        		--                                                      ELSE 'Y' END
	        		--                                                 FROM TB_YD_STKLYR   A
	        		--                                                    , TB_PT_COILCOMM C
	        		--                                                WHERE A.STL_NO = C.COIL_NO
	        		--                                                  AND A.YD_STK_COL_GP = 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
	        		--                                                  AND A.STL_NO IS NOT NULL
	        		--                                                  AND A.DEL_YN = 'N'
	        		--                                                  AND A.YD_STK_LYR_MTL_STAT = 'C')
	        		--                               ELSE 'N' END
	        		--                          FROM DUAL) AS TC_CHK
	        		                     , (SELECT COUNT(*)
	        		                          FROM TB_YD_STKLYR   A 
	        		                             , TB_PT_COILCOMM C 
	        		                         WHERE A.STL_NO = C.COIL_NO 
	        		                           AND A.YD_STK_COL_GP = 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4) 
	        		                           AND A.STL_NO IS NOT NULL 
	        		                           AND A.DEL_YN = 'N' 
	        		                           AND A.YD_STK_LYR_MTL_STAT IN ('C') 
	        		                       ) TC_CNT                          
	        		                     , (SELECT NVL(SUM(C.COIL_WT),0)
	        		                          FROM TB_YD_STKLYR   A 
	        		                             , TB_PT_COILCOMM C 
	        		                         WHERE A.STL_NO = C.COIL_NO 
	        		                           AND A.YD_STK_COL_GP = 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4) 
	        		                           AND A.STL_NO IS NOT NULL 
	        		                           AND A.DEL_YN = 'N' 
	        		                           AND A.YD_STK_LYR_MTL_STAT IN ('C') 
	        		                       ) TC_WGT                          
	        		                     , (SELECT NVL(COUNT(STL_NO),0)
	        		                          FROM TB_YD_CRNSCH   A 
	        		                             , TB_YD_CRNWRKMTL B 
	        		                             , TB_PT_COILCOMM C 
	        		                         WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID 
	        		                           AND A.DEL_YN = 'N'
	        		                           AND B.DEL_YN = 'N'
	        		                           AND B.STL_NO = C.COIL_NO 
	        		                           AND A.YD_SCH_CD = WB.YD_SCH_CD 
	        		                           AND A.YD_EQP_ID <> :V_YD_EQP_ID 
	        		                           AND SUBSTR(A.YD_DN_WO_LOC,1,6) = 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4) 
	        		                       ) T_CRN_CNT                          
	        		                     , (SELECT NVL(SUM(C.COIL_WT),0)
	        		                          FROM TB_YD_CRNSCH   A 
	        		                             , TB_YD_CRNWRKMTL B 
	        		                             , TB_PT_COILCOMM C 
	        		                         WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID 
	        		                           AND A.DEL_YN = 'N'
	        		                           AND B.DEL_YN = 'N'
	        		                           AND B.STL_NO = C.COIL_NO 
	        		                           AND A.YD_SCH_CD = WB.YD_SCH_CD 
	        		                           AND A.YD_EQP_ID <> :V_YD_EQP_ID 
	        		                           AND SUBSTR(A.YD_DN_WO_LOC,1,6) = 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
	        		                       ) T_CRN_WGT   
	        		                  FROM TB_YD_WRKBOOK    WB
	        		                     , TB_YD_WRKBOOKMTL WM
	        		                     , TB_YD_SCHRULE    SR
	        		                     , TB_YD_EQP        EQ
	        		                     , TB_PT_COILCOMM   CC
	        		                 WHERE WB.YD_WBOOK_ID      = WM.YD_WBOOK_ID
	        		                   AND WM.STL_NO           = CC.COIL_NO
	        		                   AND WB.YD_WRK_PLAN_TCAR = EQ.YD_EQP_ID(+)
	        		                   AND WB.DEL_YN           = 'N'
	        		                   AND SR.DEL_YN           = 'N'
	        		                   AND SR.YD_SCH_PROH_EXN  = 'N'
	        		                   AND WB.YD_SCH_CD        = SR.YD_SCH_CD
	        		                   AND SR.YD_WRK_CRN       = :V_YD_EQP_ID
	        		                   AND WB.TRN_EQP_CD IS NULL --구내운송제외
	        		                   AND WB.CAR_NO     IS NULL --차량제외
	        		                   -- 크레인 편성된 작업예약 제외
	        		                   AND WB.YD_WBOOK_ID NOT IN (SELECT NVL(YD_WBOOK_ID,'*')
	        		                                                FROM TB_YD_CRNSCH
	        		                                               WHERE DEL_YN = 'N'
	        		                                               GROUP BY YD_WBOOK_ID)
	        		                   -- SPM/HFL 설비 작업예약 제외
	        		                   AND 'Y' = CASE WHEN WB.YD_SCH_CD LIKE 'J_KE01UH' THEN 'N' -- SPM 보급
	        		                                  WHEN WB.YD_SCH_CD LIKE 'JCFE01UH' THEN 'N' -- 4HFL보급
	        		                                  WHEN WB.YD_SCH_CD LIKE 'JGFE01UH' THEN 'N' -- 1HFL보급
	        		                                  WHEN WB.YD_SCH_CD LIKE 'J_KD%'    THEN 'N' -- SPM 출측
	        		                                  WHEN WB.YD_SCH_CD LIKE 'JGFD%'    THEN 'N' -- HFL1 출측
	        		                                  WHEN WB.YD_SCH_CD LIKE 'JCFD%'    THEN 'N' -- HFL4 출측
	        		                                  ELSE 'Y'
	        		                             END

	        		                   -- 지포장위치에 적치할 곳이 없으면 제외
	        		                   AND 'Y' = CASE WHEN WB.YD_SCH_CD IN ('JBGF01UH','JCGF01UH','JEGF01UH','JHGF01UH')
	        		                                       AND (SELECT COUNT(*)
	        		                                              FROM TB_YD_STKLYR     A
	        		                                                 , TB_YD_SCHRULE    B
	        		                                                 , TB_YD_LOCSRCHRNG C
	        		                                             WHERE B.YD_SCH_CD = C.YD_SCH_CD
	        		                                               AND A.DEL_YN              = 'N'
	        		                                               AND B.DEL_YN              = 'N'
	        		                                               AND C.DEL_YN              = 'N'
	        		                                               AND A.YD_STK_LYR_ACT_STAT = 'E'
	        		                                               AND A.STL_NO           IS NULL
	        		                                               AND B.CD_CONTENTS      LIKE '%지포장%'
	        		                                               AND A.YD_STK_COL_GP    LIKE '%GF%'
	        		                                               AND B.YD_SCH_CD        LIKE 'J%'
	        		                                               AND A.YD_STK_COL_GP    LIKE 'J'|| SUBSTR(WB.YD_SCH_CD,2,1) ||'%'  --> 입력 값 (야드 동)
	        		                                           ) = 0 THEN 'N'
	        		                                  ELSE 'Y' END
	        		                   -- HFL(결속장) 보급가능 위치 체크하여 스케쥴기동
	        		                   AND 'Y' = CASE WHEN WB.YD_SCH_CD IN ('JBFE01UH', 'JDFE01UH', 'JFFE01UH')
	        		                                       AND (SELECT COUNT(*)
	        		                                              FROM TB_YD_STKLYR B
	        		                                             WHERE B.YD_STK_COL_GP = DECODE(WB.YD_SCH_CD, 'JFFE01UH', 'JFFE02'
	        		                                                                                        , 'JDFE01UH', 'JDFE03'
	        		                                                                                        , 'JBFE01UH', 'JBFE05')
	        		                                               AND B.STL_NO IS NULL
	        		                                               AND B.YD_STK_LYR_ACT_STAT = 'E'
	        		                                               AND B.YD_STK_BED_NO <> '00') = 0 THEN 'N'
	        		                                  ELSE 'Y' END
	        		                   -- 차량동간이적(동일차량이 상하차작업을 하고 있으면 상하차작업 선택 불가)
	        		                   AND NOT EXISTS(SELECT 1
	        		                                    FROM USRYDA.TB_YD_CRNSCH B
	        		                                       , TB_YD_WRKBOOK       C
	        		                                   WHERE B.YD_WBOOK_ID = C.YD_WBOOK_ID
	        		                                     AND C.CAR_NO      = WB.CAR_NO
	        		                                     AND B.DEL_YN      = 'N'
	        		                                     AND C.DEL_YN      = 'N'
	        		                                     AND B.YD_WRK_PROG_STAT IN ('S','W','1','2','3')
	        		                                     AND SUBSTR(B.YD_SCH_CD,1,1) = SUBSTR(WB.YD_SCH_CD,1,1)
	        		                                     AND B.YD_SCH_CD  LIKE 'J_TR_0%'
	        		                                     AND WB.YD_SCH_CD LIKE 'J_TR_0%'
	        		                                     AND 'Y' = CASE WHEN SUBSTR(B.YD_SCH_CD,1,1) = 'U' AND SUBSTR(WB.YD_SCH_CD,1,1) = 'L' THEN 'N'
	        		                                                    WHEN SUBSTR(B.YD_SCH_CD,1,1) = 'L' AND SUBSTR(WB.YD_SCH_CD,1,1) = 'U' THEN 'N'
	        		                                                    ELSE 'Y' END
	        		                                  )
	        		                    --스케줄 취소한 작업예약 제외 20201029 LHJ
	        		                    AND 'Y' = CASE WHEN (SELECT ITEM1 FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP815' AND ITEM = 'J') = 'Y' 
	        		                                    AND SUBSTR(WB.YD_SCH_CD, 8, 1) != 'H'
	        		                                    AND WB.SCH_CNCL_YN = 'Y'
	        		                                   THEN 'N'
	        		                                   WHEN (SELECT ITEM1 FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP815' AND ITEM = 'H') = 'Y' 
	        		                                    AND SUBSTR(WB.YD_SCH_CD, 8, 1) = 'H'
	        		                                    AND WB.SCH_CNCL_YN = 'Y'
	        		                                   THEN 'N'
	        		                              ELSE 'Y' END
	        		                    -- 결속장 MATL_SUP_MTD_GP ='Y' LOCK 상태 스케쥴 금지SJH
	        		                    AND 'Y' = CASE WHEN WB.YD_SCH_CD IN ('JBFD01LM','JBTC01MM','JBTC02MM','JBYD04MM','JDFD01LM','JDTC01MM','JDTC02MM','JDYD04MM') THEN
	        		                                   CASE WHEN (SELECT NVL(MAX(MATL_SUP_MTD_GP),'N') 
	        		                                                FROM TB_YD_STKCOL 
	        		                                               WHERE YD_STK_COL_GP 
	        		                                                   = CASE WHEN WB.YD_SCH_CD IN ('JBFD01LM','JBTC01MM','JBTC02MM','JBYD04MM') THEN 'JBFE05'
	        		                                                          WHEN WB.YD_SCH_CD IN ('JDFD01LM','JDTC01MM','JDTC02MM','JDYD04MM') THEN 'JDFE03'
	        		                                                          ELSE 'XXXXXX' END) = 'Y' THEN 'N'
	        		                                        ELSE 'Y' END
	        		                              ELSE 'Y' END 
	        		                    -- 크래들롤 보급작업 제외
	        		                    AND 'Y' = CASE WHEN WB.YD_SCH_CD IN ('JDCD01UH','JFCD01UH') THEN 'N'
	        		                                   ELSE 'Y'
	        		                              END
	        		                 ORDER BY WB.YD_SCH_PRIOR ASC
	        		                        , (CASE WHEN WB.YD_SCH_CD LIKE 'J%GF01UH' THEN
	        		                                  (SELECT HANDSCARFING_YN
	        		                                     FROM TB_YD_STOCK
	        		                                    WHERE STL_NO = (SELECT STL_NO
	        		                                                      FROM USRYDA.TB_YD_WRKBOOKMTL
	        		                                                     WHERE YD_WBOOK_ID = WB.YD_WBOOK_ID )
	        		                                  )
	        		                             ELSE '' END) ASC
	        		                        , WB.YD_WBOOK_ID ASC
	        		                ) A
	        		       )
	        		 WHERE 1=1
	        		   AND RANK_CNT = 1
	        		   AND TC_CHK   = 'N'
	    			*/
	        		JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getWorkTbRefNONECoil", logId, mthdNm, "작업예약 조회");

					//작업예약이 없으면 작업 지시 없음 전송
					/**********************************************************
					* 작업예약 대상재 없음 
					**********************************************************/

					if (jsWrkBook.size() < 1) {
						
						jrSndMsg.setField("YD_WRK_PROG_STAT", ""); //야드작업진행상태
						jrSndMsg.setField("YD_SCH_CD"       , ""); //야드스케쥴코드
						jrSndMsg.setField("YD_CRN_SCH_ID"   , ""); //야드크레인스케쥴ID
						
						ydL3Msg = "다음 크레인작업지시 없음";
						jrSndMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
						jrSndMsg.setField("YD_L3_MSG"     , "크레인[" + ydEqpId + "-" + rcvYdWrkProgStat + "] 작업지시 없음"); //야드L3MESSAGE
						
						jrRtn = commUtils.addSndData(jrRtn, coilDao.getYDY5L005(jrSndMsg));

						commUtils.printLog(logId, "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + ydEqpId + " : " + rcvYdWrkProgStat + " - " + ydCrnSchId + " ]", "SL");

						commUtils.printLog(logId, mthdNm, "S-");
						jrRtn.setField("RTN_CD" 		, "1");
						return jrRtn;
						
					} else {
						/**********************************************************
						* 작업예약 대상재 있음
						**********************************************************/
						
                     // 대상재 있음
	 	        		String ydSchCdWb		= commUtils.nvl(jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"), "");
	 	        		String ydWbookIdWb		= commUtils.nvl(jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID"), "");

	    	    		jrSndMsg = commUtils.getParam(logId, mthdNm, sModifier);
	    	    		jrSndMsg.setField("JMS_TC_CD"	, "YDYDJ551");
	    	    		jrSndMsg.setField("YD_EQP_ID"   , ydEqpId   ); //야드설비ID
	    	    		jrSndMsg.setField("YD_SCH_CD"   , ydSchCdWb   ); //야드스케쥴코드
	    				jrSndMsg.setField("YD_WBOOK_ID"	, ydWbookIdWb);

	    				jrRtn = commUtils.addSndData(jrRtn, jrSndMsg);
	    				
					}
				}
			}

			
			commUtils.printLog(logId, mthdNm, "S-");
			jrRtn.setField("RTN_CD" 		, "1");
			return jrRtn;
		} catch (Exception e) {
			try {
				//chito : 정상SET후  ERROR 발생한 경우								
				if( "0000".equals(commUtils.trim(jrSndMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
					jrSndMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
					jrSndMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
				}
				
				//크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "CCommSeEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { coilDao.getYDY5L005(jrSndMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
    }
    
	/**	
	 *      [A] 오퍼레이션명 : 대차작업 스케쥴 기동(YDYDJ555)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYDYDJ555(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "대차작업 스케쥴 기동 [CCoilL2RcvSeEJB.rcvYDYDJ555] < " + rcvMsg.getResultMsg();

		String logId		= rcvMsg.getResultCode();
		JDTORecord jrRtn	= JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {

			commUtils.printLog(logId, mthdNm, "S+");

	    	//수신항목 변수 저장
			String ydEqpId	 = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));	//설비번호(크레인번호)
			String ydSchCd	 = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));	//스케쥴

			String msgId	 = commUtils.nvl(commUtils.getMsgId(rcvMsg),"YDYDJ555");	// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	// 수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			//변경자 설정 (insert,update 문에서 사용)
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier); 	
			jrParam.setField("YD_EQP_ID"    , ydEqpId  ); //크레인 정보
			jrParam.setField("YD_SCH_CD"    , ydSchCd  ); //크레인 스케쥴
			
			String sAPP030_YN = coilDao.ApplyYn(logId, mthdNm, "APP030", "J", "*");
			commUtils.printLog(logId, "sAPP030_YN:"+ sAPP030_YN, "SL");
			
			if ("N".equals(sAPP030_YN)) {
				//작업예약 조회
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getWorkTbRefNONECoilTc 
				SELECT *
				  FROM (SELECT A.*
				             , ROW_NUMBER() OVER(PARTITION BY YD_SCH_CD ORDER BY ROWNUM) AS RANK_CNT
				          FROM (SELECT WB.YD_WBOOK_ID
				                     , WB.YD_SCH_CD
				                     , WB.YD_SCH_PRIOR
				                     , WB.YD_WRK_PLAN_TCAR
				                     , WB.CAR_NO
				                     , EQ.STK_BED_MAX_QNTY
				                     , EQ.STK_BED_MAX_WT
				                     , EQ.YD_CURR_BAY_GP
				                     , CC.COIL_WT
				                       -- 대차상차인 경우 현재위치:중량 및 매수 CHECK
				                     , (SELECT CASE WHEN SUBSTR(WB.YD_SCH_CD,2,1) <> EQ.YD_CURR_BAY_GP THEN 'N'
				                                    WHEN SUBSTR(WB.YD_SCH_CD,3,2) = 'TC' AND SUBSTR(WB.YD_SCH_CD,7,1) IN ('U')
				                                         THEN (SELECT CASE WHEN NVL(COUNT(*),0)+1 <= EQ.STK_BED_MAX_QNTY
				                                                            AND NVL(SUM(C.COIL_WT),0) + CC.COIL_WT  <= EQ.STK_BED_MAX_WT  THEN 'N'
				                                                      ELSE 'Y' END
				                                                 FROM TB_YD_STKLYR   A
				                                                    , TB_PT_COILCOMM C
				                                                WHERE A.STL_NO = C.COIL_NO
				                                                  AND A.YD_STK_COL_GP = 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
				                                                  AND A.STL_NO IS NOT NULL
				                                                  AND A.DEL_YN = 'N'
				                                                  AND A.YD_STK_LYR_MTL_STAT = 'C')
				                               ELSE 'N' END
				                          FROM DUAL) AS TC_CHK
				                  FROM TB_YD_WRKBOOK    WB
				                     , TB_YD_WRKBOOKMTL WM
				                     , TB_YD_SCHRULE    SR
				                     , TB_YD_EQP        EQ
				                     , TB_PT_COILCOMM   CC
				                 WHERE WB.YD_WBOOK_ID      = WM.YD_WBOOK_ID
				                   AND WM.STL_NO           = CC.COIL_NO
				                   AND WB.YD_WRK_PLAN_TCAR = EQ.YD_EQP_ID(+)
				                   AND WB.DEL_YN           = 'N'
				                   AND SR.DEL_YN           = 'N'
				                   AND SR.YD_SCH_PROH_EXN  = 'N'
				                   AND WB.YD_SCH_CD        = SR.YD_SCH_CD
				                   AND SR.YD_WRK_CRN       = :V_YD_EQP_ID
				                   -- 크레인 편성된 작업예약 제외
				                   AND WB.YD_WBOOK_ID NOT IN (SELECT NVL(YD_WBOOK_ID,'*')
				                                                FROM TB_YD_CRNSCH
				                                               WHERE DEL_YN = 'N'
				                                               GROUP BY YD_WBOOK_ID)
				                   AND WB.YD_SCH_CD LIKE 'J_TC__UH'
				                 ORDER BY WB.YD_SCH_PRIOR ASC
				                        , WB.YD_WBOOK_ID ASC
				                ) A
				       )
				 WHERE 1=1
				   AND RANK_CNT = 1
				   AND TC_CHK   = 'N'
				*/
	    		JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getWorkTbRefNONECoilTc", logId, mthdNm, "작업예약 조회");

				//작업예약이 없으면 작업 지시 없음 전송
				/**********************************************************
				* 작업예약 대상재 없음 
				**********************************************************/
	
				if (jsWrkBook.size() > 0) {
					
					/**********************************************************
					* 작업예약 대상재 있음
					**********************************************************/
					
	             // 대상재 있음
	        		String ydSchCdWb		= commUtils.nvl(jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"), "");
	        		String ydWbookIdWb		= commUtils.nvl(jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID"), "");
	
	        		JDTORecord jrSndMsg = commUtils.getParam(logId, mthdNm, sModifier);
		    		jrSndMsg.setField("JMS_TC_CD"	, "YDYDJ551");
		    		jrSndMsg.setField("YD_EQP_ID"   , ydEqpId   ); //야드설비ID
		    		jrSndMsg.setField("YD_SCH_CD"   , ydSchCdWb   ); //야드스케쥴코드
					jrSndMsg.setField("YD_WBOOK_ID"	, ydWbookIdWb);
	
					jrRtn = commUtils.addSndData(jrRtn, jrSndMsg);
					
				}
			} else {
				JDTORecordSet jsWrkBook = JDTORecordFactory.getInstance().createRecordSet("Temp");
				commUtils.printLog(logId, "ydSchCd:"+ ydSchCd, "SL");
				
				if ("TC".equals(ydSchCd.substring(2, 4)) && "UH".equals(ydSchCd.substring(6, 8))) {
					//작업예약 조회
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getWorkTbRefNONECoilTc 
					SELECT *
					  FROM (SELECT A.*
					             , ROW_NUMBER() OVER(PARTITION BY YD_SCH_CD ORDER BY ROWNUM) AS RANK_CNT
					             , CASE WHEN SUBSTR(A.YD_SCH_CD,2,1) <> A.YD_CURR_BAY_GP THEN 'Y'
					                    WHEN SUBSTR(A.YD_SCH_CD,3,2) = 'TC' AND SUBSTR(A.YD_SCH_CD,7,1) IN ('U')
					                         THEN CASE WHEN A.TC_CNT + T_CRN_CNT + 1          <= A.STK_BED_MAX_QNTY
					                                    AND A.TC_WGT + T_CRN_WGT + A.COIL_WT  <= A.STK_BED_MAX_WT  THEN 'N'
					                         ELSE 'Y' END
					               ELSE 'N' END  AS TC_CHK
					          FROM (SELECT WB.YD_WBOOK_ID
					                     , WB.YD_SCH_CD
					                     , WB.YD_SCH_PRIOR
					                     , WB.YD_WRK_PLAN_TCAR
					                     , 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)  AS TC_LOC
					                     , (SELECT COUNT(*)
					                          FROM TB_YD_STKLYR   A
					                             , TB_PT_COILCOMM C
					                         WHERE A.STL_NO = C.COIL_NO
					                           AND A.YD_STK_COL_GP = 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
					                           AND A.STL_NO IS NOT NULL
					                           AND A.DEL_YN = 'N'
					                           AND A.YD_STK_LYR_MTL_STAT IN ('C')
					                       ) TC_CNT
					                     , (SELECT NVL(SUM(C.COIL_WT),0)
					                          FROM TB_YD_STKLYR   A
					                             , TB_PT_COILCOMM C
					                         WHERE A.STL_NO = C.COIL_NO
					                           AND A.YD_STK_COL_GP = 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
					                           AND A.STL_NO IS NOT NULL
					                           AND A.DEL_YN = 'N'
					                           AND A.YD_STK_LYR_MTL_STAT IN ('C')
					                       ) TC_WGT
					                     , (SELECT NVL(COUNT(STL_NO),0)
					                          FROM TB_YD_CRNSCH   A
					                             , TB_YD_CRNWRKMTL B
					                             , TB_PT_COILCOMM C
					                         WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
					                           AND A.DEL_YN = 'N'
					                           AND B.DEL_YN = 'N'
					                           AND B.STL_NO = C.COIL_NO
					                           AND A.YD_SCH_CD = WB.YD_SCH_CD
					                           AND A.YD_EQP_ID <> :V_YD_EQP_ID
					                           AND SUBSTR(A.YD_DN_WO_LOC,1,6) = 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
					                       ) T_CRN_CNT
					                     , (SELECT NVL(SUM(C.COIL_WT),0)
					                          FROM TB_YD_CRNSCH   A
					                             , TB_YD_CRNWRKMTL B
					                             , TB_PT_COILCOMM C
					                         WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
					                           AND A.DEL_YN = 'N'
					                           AND B.DEL_YN = 'N'
					                           AND B.STL_NO = C.COIL_NO
					                           AND A.YD_SCH_CD = WB.YD_SCH_CD
					                           AND A.YD_EQP_ID <> :V_YD_EQP_ID
					                           AND SUBSTR(A.YD_DN_WO_LOC,1,6) = 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
					                       ) T_CRN_WGT
					                     , WB.CAR_NO
					                     , EQ.STK_BED_MAX_QNTY
					                     , EQ.STK_BED_MAX_WT
					                     , EQ.YD_CURR_BAY_GP
					                     , CC.COIL_WT
					                       -- 대차상차인 경우 현재위치:중량 및 매수 CHECK
					--                     , (SELECT CASE WHEN SUBSTR(WB.YD_SCH_CD,2,1) <> EQ.YD_CURR_BAY_GP THEN 'Y'
					--                                    WHEN SUBSTR(WB.YD_SCH_CD,3,2) = 'TC' AND SUBSTR(WB.YD_SCH_CD,7,1) IN ('U')
					--                                         THEN (SELECT CASE WHEN NVL(COUNT(*),0)+1 <= EQ.STK_BED_MAX_QNTY
					--                                                            AND NVL(SUM(C.COIL_WT),0) + CC.COIL_WT  <= EQ.STK_BED_MAX_WT  THEN 'N'
					--                                                      ELSE 'Y' END
					--                                                 FROM TB_YD_STKLYR   A
					--                                                    , TB_PT_COILCOMM C
					--                                                WHERE A.STL_NO = C.COIL_NO
					--                                                  AND A.YD_STK_COL_GP = 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
					--                                                  AND A.STL_NO IS NOT NULL
					--                                                  AND A.DEL_YN = 'N'
					--                                                  AND A.YD_STK_LYR_MTL_STAT IN ('C'))
					--                               ELSE 'N' END
					--                          FROM DUAL) AS TC_CHK
					                  FROM TB_YD_WRKBOOK    WB
					                     , TB_YD_WRKBOOKMTL WM
					                     , TB_YD_SCHRULE    SR
					                     , TB_YD_EQP        EQ
					                     , TB_PT_COILCOMM   CC
					                 WHERE WB.YD_WBOOK_ID      = WM.YD_WBOOK_ID
					                   AND WM.STL_NO           = CC.COIL_NO
					                   AND WB.YD_WRK_PLAN_TCAR = EQ.YD_EQP_ID(+)
					                   AND WB.DEL_YN           = 'N'
					                   AND SR.DEL_YN           = 'N'
					                   AND SR.YD_SCH_PROH_EXN  = 'N'
					                   AND WB.YD_SCH_CD        = SR.YD_SCH_CD
					                   AND SR.YD_WRK_CRN       = :V_YD_EQP_ID
					                   -- 크레인 편성된 작업예약 제외
					                   AND WB.YD_WBOOK_ID NOT IN (SELECT NVL(YD_WBOOK_ID,'*')
					                                                FROM TB_YD_CRNSCH
					                                               WHERE DEL_YN = 'N'
					                                               GROUP BY YD_WBOOK_ID)
					                   AND WB.YD_SCH_CD LIKE 'J_TC__UH'
					                 ORDER BY WB.YD_SCH_PRIOR ASC
					                        , WB.YD_WBOOK_ID ASC
					                ) A
					       )
					 WHERE 1=1
					   AND RANK_CNT = 1
					   AND TC_CHK   = 'N'
					   AND 'Y' = CASE WHEN (SELECT COUNT(*)
					                          FROM TB_YD_CRNSCH CS
					                         WHERE CS.DEL_YN = 'N'
					                           AND CS.YD_SCH_CD LIKE 'J_TC__UH'
					                           AND CS.YD_EQP_ID = :V_YD_EQP_ID
					                       ) > 0 THEN 'N'
					                  ELSE 'Y' END
					*/
		    		jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getWorkTbRefNONECoilTc", logId, mthdNm, "대차작업예약 조회");
		    		
				} else if ("TC".equals(ydSchCd.substring(2, 4)) 
						 && ("UM".equals(ydSchCd.substring(6, 8)) || "MM".equals(ydSchCd.substring(6, 8)))
						 ) {
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getWorkTbRefNONECoilTcJ 
					SELECT *
					  FROM (SELECT A.*
					             , ROW_NUMBER() OVER(PARTITION BY YD_SCH_CD ORDER BY ROWNUM) AS RANK_CNT
					             , CASE WHEN SUBSTR(A.YD_SCH_CD,2,1) <> A.YD_CURR_BAY_GP THEN 'Y' 
					                    WHEN SUBSTR(A.YD_SCH_CD,3,2) = 'TC' AND SUBSTR(A.YD_SCH_CD,7,1) IN ('U') 
					                    THEN CASE WHEN A.TC_CNT + 1          <= A.STK_BED_MAX_QNTY 
					                               AND A.TC_WGT + A.COIL_WT  <= A.STK_BED_MAX_WT  THEN 'N'
					                              ELSE 'Y' END 
					                         ELSE 'N' END AS TC_CHK 
					          FROM (SELECT WB.YD_WBOOK_ID
					                     , WB.YD_SCH_CD
					                     , WB.YD_SCH_PRIOR
					                     , WB.YD_WRK_PLAN_TCAR
					                     , WB.CAR_NO
					                     , EQ.STK_BED_MAX_QNTY
					                     , EQ.STK_BED_MAX_WT
					                     , EQ.YD_CURR_BAY_GP
					                     , CC.COIL_WT
					                     , (SELECT COUNT(*)
					                          FROM TB_YD_STKLYR   A 
					                             , TB_PT_COILCOMM C 
					                         WHERE A.STL_NO = C.COIL_NO 
					                           AND A.YD_STK_COL_GP = 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4) 
					                           AND A.STL_NO IS NOT NULL 
					                           AND A.DEL_YN = 'N' 
					                           AND A.YD_STK_LYR_MTL_STAT IN ('C') 
					                       ) TC_CNT                          
					                     , (SELECT NVL(SUM(C.COIL_WT),0)
					                          FROM TB_YD_STKLYR   A 
					                             , TB_PT_COILCOMM C 
					                         WHERE A.STL_NO = C.COIL_NO 
					                           AND A.YD_STK_COL_GP = 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4) 
					                           AND A.STL_NO IS NOT NULL 
					                           AND A.DEL_YN = 'N' 
					                           AND A.YD_STK_LYR_MTL_STAT IN ('C') 
					                       ) TC_WGT                          
					                     , (SELECT NVL(COUNT(STL_NO),0)
					                          FROM TB_YD_CRNSCH   A 
					                             , TB_YD_CRNWRKMTL B 
					                             , TB_PT_COILCOMM C 
					                         WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID 
					                           AND A.DEL_YN = 'N'
					                           AND B.DEL_YN = 'N'
					                           AND B.STL_NO = C.COIL_NO 
					                           AND A.YD_SCH_CD = WB.YD_SCH_CD 
					                           AND A.YD_EQP_ID <> :V_YD_EQP_ID 
					                           AND SUBSTR(A.YD_DN_WO_LOC,1,6) = 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4) 
					                       ) T_CRN_CNT                          
					                     , (SELECT NVL(SUM(C.COIL_WT),0)
					                          FROM TB_YD_CRNSCH   A 
					                             , TB_YD_CRNWRKMTL B 
					                             , TB_PT_COILCOMM C 
					                         WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID 
					                           AND A.DEL_YN = 'N'
					                           AND B.DEL_YN = 'N'
					                           AND B.STL_NO = C.COIL_NO 
					                           AND A.YD_SCH_CD = WB.YD_SCH_CD 
					                           AND A.YD_EQP_ID <> :V_YD_EQP_ID 
					                           AND SUBSTR(A.YD_DN_WO_LOC,1,6) = 'J'||SUBSTR(WB.YD_SCH_CD,2,1)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
					                       ) T_CRN_WGT   
					                  FROM TB_YD_WRKBOOK    WB
					                     , TB_YD_WRKBOOKMTL WM
					                     , TB_YD_SCHRULE    SR
					                     , TB_YD_EQP        EQ
					                     , TB_PT_COILCOMM   CC
					                 WHERE WB.YD_WBOOK_ID      = WM.YD_WBOOK_ID
					                   AND WM.STL_NO           = CC.COIL_NO
					                   AND WB.YD_WRK_PLAN_TCAR = EQ.YD_EQP_ID(+)
					                   AND WB.DEL_YN           = 'N'
					                   AND SR.DEL_YN           = 'N'
					                   AND SR.YD_SCH_PROH_EXN  = 'N'
					                   AND WB.YD_SCH_CD        = SR.YD_SCH_CD
					                   AND SR.YD_WRK_CRN       = :V_YD_EQP_ID
					                   AND WB.TRN_EQP_CD IS NULL --구내운송제외
					                   AND WB.CAR_NO     IS NULL --차량제외
					                   -- 크레인 편성된 작업예약 제외
					                   AND WB.YD_WBOOK_ID NOT IN (SELECT NVL(YD_WBOOK_ID,'*')
					                                                FROM TB_YD_CRNSCH
					                                               WHERE DEL_YN = 'N'
					                                               GROUP BY YD_WBOOK_ID)
					                   -- 크레인 편성된 작업예약 제외
					                   AND WB.YD_WBOOK_ID NOT IN (SELECT NVL(YD_WBOOK_ID,'*')
					                                                FROM TB_YD_CRNSCH
					                                               WHERE DEL_YN = 'N'
					                                               GROUP BY YD_WBOOK_ID)
					                   AND WB.YD_SCH_CD LIKE 'J_TC__UM'
					                 ORDER BY WB.YD_SCH_PRIOR ASC
					                        , WB.YD_WBOOK_ID ASC
					                ) A
					       )
					 WHERE 1=1
					   AND RANK_CNT = 1
					   AND TC_CHK   = 'N'
					   AND 'Y' = CASE WHEN (SELECT COUNT(*)
					                          FROM TB_YD_CRNSCH CS
					                         WHERE CS.DEL_YN = 'N'
					                           AND CS.YD_SCH_CD LIKE 'J_TC__UM'
					                           AND CS.YD_EQP_ID = :V_YD_EQP_ID
					                       ) > 0 THEN 'N'
					                  ELSE 'Y' END 
					
					*/
		    		jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getWorkTbRefNONECoilTcJ", logId, mthdNm, "대차작업예약 조회");
				} // 동내이적 스케쥴 
				else if( "YD01MH".equals(ydSchCd.substring(2, 8)) ||"YD02MH".equals(ydSchCd.substring(2, 8)) || "YD51MH".equals(ydSchCd.substring(2, 8)) ) {
					
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getWorkTbRefNONECoilYd
					SELECT *
					  FROM (SELECT ROW_NUMBER() OVER(PARTITION BY YD_SCH_CD ORDER BY ROWNUM) AS RANK_CNT
					             , A.*
					          FROM (SELECT WB.YD_WBOOK_ID
					                     , WB.YD_SCH_CD
					                     , WB.YD_SCH_PRIOR
					                     , CS.YD_CRN_SCH_ID
					                  FROM TB_YD_WRKBOOK WB
					                     , TB_YD_SCHRULE SR
					                     , TB_YD_CRNSCH  CS
					                 WHERE WB.YD_SCH_CD       = SR.YD_SCH_CD
					                   AND WB.YD_WBOOK_ID     = CS.YD_WBOOK_ID(+)
					                   AND WB.DEL_YN          = 'N'
					                   AND SR.DEL_YN          = 'N'
					                   AND CS.DEL_YN(+)       = 'N'
					                   AND SR.YD_SCH_PROH_EXN = 'N'
					                   AND WB.YD_SCH_CD       = :V_YD_SCH_CD
					                 ORDER BY WB.YD_WBOOK_ID
					               ) A
					       )
					 WHERE RANK_CNT       = 1
					   AND YD_CRN_SCH_ID IS NULL
					*/
					jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getWorkTbRefNONECoilYd", logId, mthdNm, "동내이적예약 조회");
				}
				
				//작업예약이 없으면 작업 지시 없음 전송
				/**********************************************************
				* 작업예약 대상재 없음 
				**********************************************************/
	
				if (jsWrkBook.size() > 0) {
					
					/**********************************************************
					* 작업예약 대상재 있음
					**********************************************************/
					
	             // 대상재 있음
	        		String ydSchCdWb		= commUtils.nvl(jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"), "");
	        		String ydWbookIdWb		= commUtils.nvl(jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID"), "");
	
	        		JDTORecord jrSndMsg = commUtils.getParam(logId, mthdNm, sModifier);
		    		jrSndMsg.setField("JMS_TC_CD"	, "YDYDJ551");
		    		jrSndMsg.setField("YD_EQP_ID"   , ydEqpId   ); //야드설비ID
		    		jrSndMsg.setField("YD_SCH_CD"   , ydSchCdWb   ); //야드스케쥴코드
					jrSndMsg.setField("YD_WBOOK_ID"	, ydWbookIdWb);
	
					jrRtn = commUtils.addSndData(jrRtn, jrSndMsg);
					
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
     * 오퍼레이션명 : 차량 작업진행관리 - 크레인 권하시 호출
     * 신 PIDEV
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public JDTORecord procY5CarWrkStatCtrCoil_PIDEV(JDTORecord rcvMsg) throws DAOException {
    	String mthdNm   = "차량 작업진행관리[CCoilL2RcvSeEJB.procY5CarWrkStatCtrCoil_PIDEV] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			JDTORecord jrRtn    = JDTORecordFactory.getInstance().create();	//전문 Return
			
			String ydCrnSchId   = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID" ));
			String ydWbookId    = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"   ));
			String sCarLdudGp   = commUtils.trim(rcvMsg.getFieldString("CAR_LDUD_GP"   )); //U 권하위치 차량 L 권상위치 차량
			String ydDnWrLoc    = commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LOC"  ));
			String ydUpWrLoc    = commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LOC"  ));
			String sStlAppearGp = commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP" ));
			String sStlNo       = commUtils.trim(rcvMsg.getFieldString("STL_NO"        ));
			String ydSchCd      = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"     ));
			
			String sModifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //수정자(Backup Only)
//PIDEV_S :병행가동용:PI_YD
			String sPI_YD       = commUtils.nvl(rcvMsg.getFieldString("PI_YD"),"*");			
			JDTORecord jrCarMv  = commUtils.getParam(logId, mthdNm, sModifier);

			jrCarMv.setField("YD_CRN_SCH_ID" , ydCrnSchId  );
        	jrCarMv.setField("YD_WBOOK_ID"   , ydWbookId   );
        	jrCarMv.setField("CAR_LDUD_GP"   , sCarLdudGp  );
        	jrCarMv.setField("YD_DN_WR_LOC"  , ydDnWrLoc   );
        	jrCarMv.setField("YD_UP_WR_LOC"  , ydUpWrLoc   );
        	jrCarMv.setField("STL_NO"        , sStlNo      );
        	jrCarMv.setField("STL_APPEAR_GP" , sStlAppearGp); //재료외형구분 E;소재 , Y:제품
        	
        	jrCarMv.setField("YD_STK_COL_GP" , ydDnWrLoc.substring(0, 6));
        	
        	String ydCarSchId          = "";
        	String ydCarUseGp          = "";
        	String sTransEquipmentType = "";
        	
        	String sAutoCheck          = "N";
        	String sPI0001 = coilDao.ApplyYn(logId, mthdNm, "PI0001","J","*"); //
        	/***********************************
        	 * 상차
        	 ***********************************/
        	if ("U".equals(sCarLdudGp)) {
        		commUtils.printLog(logId, "상차_PIDEV", "SL");
        		
	        	/*************************************
				 * 상차완료 유무 체크
				 *************************************/
	        	/*
				SELECT YD_CAR_SCH_ID
				     , YD_CAR_USE_GP 
				     , CAR_NO
				     , TRANS_ORD_DATE
				     , TRANS_ORD_SEQNO
				     , DEST_TEL_NO
				     , YD_STK_COL_GP
				     , WLOC_CD
				     , YD_PNT_CD
				     , YD_CAR_PROG_STAT  -- 차량진행
				     , BAY_WRK_CNT       -- 해당상차도 작업대상
				     , BAY_WRK_END_CNT   -- 해당 상차도 작업완료
				     , CASE WHEN BAY_WRK_CNT <= BAY_WRK_END_CNT THEN 'Y' ELSE'N' END AS CAR_LD_CMPL_YN
				     , TRANS_EQUIPMENT_TYPE    
				     , CMBN_CARLD_YN 
				  FROM
				       (
				        SELECT TS.YD_CAR_SCH_ID
				             , TS.YD_CAR_USE_GP
				             , TS.CAR_NO
				             , TS.TRANS_ORD_DATE
				             , TS.TRANS_ORD_SEQNO
				             , TS.DEST_TEL_NO
				             , TS.YD_CAR_PROG_STAT
				             , TS.TRANS_EQUIPMENT_TYPE
				             , TS.CMBN_CARLD_YN
				             , SC.YD_STK_COL_GP
				             , SC.WLOC_CD
				             , SC.YD_PNT_CD
				             -- 해당상차도 작업대상 건수   
				             , CASE WHEN WB.YD_CAR_USE_GP = 'L' THEN 
				                    -- 구내운송
				                         (SELECT COUNT(DISTINCT(A.STL_NO)) 
				                            FROM TB_YD_STOCK  A
				                               , TB_YD_STKLYR B 
				                           WHERE A.STL_NO               = B.STL_NO
				                             AND A.CAR_FRTOMOVE_WORD_NO = TS.FRTOMOVE_WORD_NO
				                             AND SUBSTR(B.YD_STK_COL_GP,1,2) = SUBSTR(SC.YD_STK_COL_GP,1,2))
				                             
				                    ELSE  
				                     -- 출하
				                        (SELECT COUNT(DISTINCT(B.STL_NO)) 
				                           FROM TB_YD_STOCK    B
				                              , TB_YD_CARPOINT C
				                              , TB_YD_STKLYR   D
				                          WHERE B.TRANS_ORD_DATE  = TS.TRANS_ORD_DATE
				                            AND B.TRANS_ORD_SEQNO = TS.TRANS_ORD_SEQNO
				                            AND C.YD_STK_COL_GP   = SC.YD_STK_COL_GP
				                            AND C.DEL_YN    = 'N'
				                            AND B.STL_NO    = D.STL_NO
				                            AND C.YD_BAY_GP = SUBSTR(D.YD_STK_COL_GP, 2, 1)
				                            AND SUBSTR(B.YD_STK_COL_GP, 3, 2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
				                        )
				                     END  
				               AS BAY_WRK_CNT    
				               
				             -- 해당상차도 작업완료 건수                 
				             , CASE WHEN WB.YD_CAR_USE_GP = 'L' THEN 
				                    -- 구내운송
				                         (SELECT COUNT(DISTINCT(A.STL_NO))
				                            FROM TB_YD_STKLYR A
				                           WHERE ((SUBSTR(A.YD_STK_COL_GP,3,2) IN ('PT') AND YD_STK_LYR_MTL_STAT = 'C'))
				                             AND SUBSTR(A.YD_STK_COL_GP,1,2) = SUBSTR(SC.YD_STK_COL_GP,1,2)
				                             AND A.STL_NO IN (SELECT B.STL_NO
				                                                FROM TB_YD_STOCK B
				                                               WHERE B.CAR_FRTOMOVE_WORD_NO = TS.FRTOMOVE_WORD_NO))              
				                    ELSE 
				                    -- 출하
				                         (SELECT COUNT(DISTINCT(A.COIL_NO))
				                            FROM TB_PT_COILCOMM A
				                           WHERE SUBSTR(A.YD_STR_LOC,3,2) IN ('PT') 
				                             AND A.COIL_NO  IN (SELECT B.STL_NO
				                                                  FROM TB_YD_STOCK    B
				                                                     , TB_YD_CARPOINT C
				                                                     , TB_YD_STKLYR   D
				                                                 WHERE B.TRANS_ORD_DATE  = TS.TRANS_ORD_DATE
				                                                   AND B.TRANS_ORD_SEQNO = TS.TRANS_ORD_SEQNO
				                                                   AND C.YD_STK_COL_GP   = SC.YD_STK_COL_GP
				                                                   AND C.DEL_YN    = 'N'
				                                                   AND B.STL_NO    = D.STL_NO
				                                                   AND C.YD_BAY_GP = SUBSTR(D.YD_STK_COL_GP, 2, 1)
				                                                   AND SUBSTR(B.YD_STK_COL_GP, 3, 2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
				                                                 )
				                         )   
				                    END  
				               AS BAY_WRK_END_CNT   
				          FROM TB_YD_STKCOL SC
				             , TB_YD_CARSCH TS
				             , (SELECT TRN_EQP_CD , CAR_NO , CARD_NO ,YD_CAR_USE_GP, SUBSTR(YD_DN_WO_LOC,1,6) AS YD_DN_WO_LOC
				                  FROM TB_YD_CRNSCH  A
				                     , TB_YD_WRKBOOK B
				                 WHERE A.YD_WBOOK_ID   = B.YD_WBOOK_ID
				                   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
				               ) WB
				         WHERE SC.YD_STK_COL_GP = WB.YD_DN_WO_LOC
				           AND SC.YD_STK_COL_GP = :V_YD_STK_COL_GP
				           AND ((WB.YD_CAR_USE_GP = 'L' AND WB.TRN_EQP_CD = SC.TRN_EQP_CD) --구내운송
				             OR (WB.YD_CAR_USE_GP = 'G' AND WB.CARD_NO    = SC.CARD_NO AND WB.CAR_NO=SC.CAR_NO)) --출하차량
				           AND ((WB.YD_CAR_USE_GP = 'L' AND WB.TRN_EQP_CD = TS.TRN_EQP_CD) --구내운송
				             OR (WB.YD_CAR_USE_GP = 'G' AND WB.CARD_NO    = TS.CARD_NO AND WB.CAR_NO=TS.CAR_NO)) --출하차량  
				           AND TS.DEL_YN = 'N'
				         ORDER BY  TS.YD_CAR_SCH_ID DESC  
				       ) 
				 WHERE ROWNUM <= 1   
	        	 */
        		//PIDEV_S :병행가동용:PI_YD
        		jrCarMv.setField("PI_YD",    	sPI_YD);        		
	        	JDTORecordSet jsCrnSch = commDao.select(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarSchLd_PIDEV", logId, mthdNm, "상차완료 여부 조회");
	        	
	        	String sCarLdCmplYn        = "N";
	        	        	
	        	if (jsCrnSch.size() > 0) {
	        		sCarLdCmplYn        = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("CAR_LD_CMPL_YN"      )); //상차완료여부
	        		ydCarSchId          = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CAR_SCH_ID"       )); 	               
	        		ydCarUseGp          = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CAR_USE_GP"       )); 	               
	        		sTransEquipmentType = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("TRANS_EQUIPMENT_TYPE")); //이송 P:PDA
	        	}
	        	
	        	jrCarMv.setField("YD_CARUD_WRK_BOOK_ID"   , ydWbookId);
	        	jrCarMv.setField("YD_CARLD_WRK_BOOK_ID"   , ydWbookId);
	        	
	        	commUtils.printLog(logId, "상차완료 유무 : " + sCarLdCmplYn, "SL");
	        	
	    		/*
	    		 * 제품이송시에 검수확인 필요
	    		 */
	    		String sApp803 = coilDao.ApplyYn(logId, mthdNm, "APP803", "J", "*");
		    	
	    		commUtils.printLog(logId, "제품이송 검수 등록 여부 : " + sApp803, "SL");
	    		commUtils.printLog(logId, "ydCarUseGp : " + ydCarUseGp, "SL");
	    		commUtils.printLog(logId, "sTransEquipmentType : " + sTransEquipmentType, "SL");
	    		commUtils.printLog(logId, "sApp803 : " + sApp803, "SL");
	    		
	    		/* ***************************
	    		 * 검수 테이블 저장
	    		 * ***************************/
	    		if ("G".equals(ydCarUseGp) && "P".equals(sTransEquipmentType) && "Y".equals(sApp803)) { 
	    			
	    			jrCarMv = commUtils.getParam(logId, mthdNm, sModifier);
	    			jrCarMv.setField("STL_NO",   sStlNo);
	    			/*
					INSERT INTO TB_YD_EXAMINATIONCHKLIST (
					  TRANS_ORD_DATE
					, TRANS_ORD_SEQNO
					, STL_NO
					, YD_GP
					, CAR_NO
					, CARD_NO
					, GATE_NM
					, BAY_GP
					, YD_CARPNT_CD
					, YD_CAR_UPP_LOC_CD
					)
					SELECT *
					  FROM (
					        SELECT D.TRANS_ORD_DATE
					             , D.TRANS_ORD_SEQNO
					             , D.STL_NO
					             , NVL(A.YD_GP, D.YD_AIM_YD_GP) 
					             , D.CAR_NO 
					             , D.CARD_NO
					             , A.GATE_NAME
					             , A.BAY_GP 
					             , NVL(G.CARLD_PNT_CD, A.YD_CARPNT_CD) AS CARLD_PNT_CD
					             , D.YD_CAR_UPP_LOC_CD
					         FROM (SELECT YD_GP
					                    , YD_BAY_GP AS BAY_GP
					                    , YD_STK_COL_GP AS STACK_COL_GP
					                    , YD_CARPNT_DESC AS GATE_NAME
					                    , CARD_NO
					                    , YD_CARPNT_CD
					                 FROM TB_YD_CARPOINT
					                WHERE YD_GP IN ('J','H')
					                  AND DEL_YN = 'N'
					              ) A
					            , TB_YD_STOCK  D
					            , TB_DM_TRANSWORDGOODS @DL_SMDB G
					        WHERE D.CARD_NO = A.CARD_NO(+)    
					          AND D.STL_NO  = :V_STL_NO
					          AND D.TRANS_ORD_DATE  = G.TRANS_WORD_DATE(+)
					          AND D.TRANS_ORD_SEQNO = G.TRANS_WORD_SEQNO(+)
					          AND D.STL_NO = G.GOODS_NO(+) 
					          AND NOT EXISTS (
					                          SELECT 1
					                            FROM TB_YD_EXAMINATIONCHKLIST K
					                           WHERE K.TRANS_ORD_DATE  = D.TRANS_ORD_DATE
					                             AND K.TRANS_ORD_SEQNO = D.TRANS_ORD_SEQNO
					                             AND K.STL_NO = D.STL_NO)
					          AND ROWNUM <= 1
					       ) A
					 WHERE TRANS_ORD_DATE IS NOT NULL
	    			 */
//	    			commDao.insert(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insExaminationChkList", logId, mthdNm, "검수DATA등록");
//PIDEV
	    			commDao.insert(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insExaminationChkList_PIDEV", logId, mthdNm, "검수DATA등록");
	    		}
	    		
	    		commUtils.printLog(logId, "상차완료 여부 : " + sCarLdCmplYn, "SL");
        		
        		/******************
        		 * 상차 완료
        		 ******************/
        		if ("Y".equals(sCarLdCmplYn)) {
        			
        			jrCarMv = commUtils.getParam(logId, mthdNm, sModifier);
        			jrCarMv.setField("YD_CAR_PROG_STAT"    , "5"      );
	    			jrCarMv.setField("YD_EQP_WRK_STAT"     , "L"      );
	    			jrCarMv.setField("YD_CAR_SCH_ID"       , ydCarSchId);
	    			jrCarMv.setField("YD_CARLD_CMPL_DT"    , commUtils.getDateTime14());
	    			/*
					UPDATE TB_YD_CARSCH
					   SET MOD_DDTT = SYSDATE
					     , MODIFIER = :V_MODIFIER
					     , YD_CAR_PROG_STAT = NVL(:V_YD_CAR_PROG_STAT, YD_CAR_PROG_STAT)
					     , YD_CARLD_ST_DT   = DECODE(NVL(:V_YD_CARLD_ST_DT  ,'NULL'),'NULL',YD_CARLD_ST_DT  ,SYSDATE)
					     , YD_CARLD_CMPL_DT = DECODE(NVL(:V_YD_CARLD_CMPL_DT,'NULL'),'NULL',YD_CARLD_CMPL_DT,SYSDATE)
					     , YD_CARUD_ST_DT   = DECODE(NVL(:V_YD_CARUD_ST_DT  ,'NULL'),'NULL',YD_CARUD_ST_DT  ,SYSDATE)
					     , YD_CARUD_CMPL_DT = DECODE(NVL(:V_YD_CARUD_CMPL_DT,'NULL'),'NULL',YD_CARUD_CMPL_DT,SYSDATE)
					     , YD_EQP_WRK_STAT  = NVL(:V_YD_EQP_WRK_STAT, YD_EQP_WRK_STAT)
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID 
	    			 */
	    			commDao.update(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updMvCarSchCmpl", logId, mthdNm, "상차완료 등록");
        			
	    			if ("L".equals(ydCarUseGp)) {	//구내운송
	    				
	    				/************************************
	    				 * 상차작업완료 송신YDTSJ008
	    				 ************************************/
	    				JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
	    				jrYdMsg.setField("JMS_TC_CD"    , "YDTSJ008");
	    				jrYdMsg.setField("YD_SCH_CD"    , ydSchCd   );
	    	        	jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);

	    				jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDTSJ008", jrYdMsg));
	    				
	    				/* **************************
	    				 * AB재료정보생성
	    				 * **************************/
	    				/*
						SELECT STL_NO
						     , MATL_FTMV_WO_DT AS TRS_INDI_DT -- 이송지시
						     , SPOS_WLOC_CD
						     , ARR_WLOC_CD
						     , MTL_UGNT_GP AS URGENT_FRTOMOVE_WORD_GP -- 긴급구분
						  FROM TB_TS_MATL_FTMV_WO 
						 WHERE STL_NO            = :V_STL_NO
						   AND TS_MATL_FTMV_STAT_GP     = '1'
						   AND MATL_FTMV_WO_NML_HD_YN   = 'Y'
            			 */
            			JDTORecordSet jsWbook = commDao.select(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getTsMtlArrWloc", logId, mthdNm, "작업예약조회");
            			String sArrWlocCd = "";
            			if (jsWbook.size() > 0) {
            				sArrWlocCd = commUtils.trim(jsWbook.getRecord(0).getFieldString("ARR_WLOC_CD")); 
            			}
// PIDEV
            			commUtils.printLog(logId, "sArrWlocCd : " + sArrWlocCd, "SL");
            			
            			if ("D3Y41".equals(sArrWlocCd) || "D3Y42".equals(sArrWlocCd)) { //1열연
            				/*
	    					MERGE INTO TB_YM_STOCK A
	    					USING (
	    					        SELECT B.STL_NO    AS STOCK_ID
	    					             , '2'         AS STOCK_STAT
	    					             , 'EC'        AS STOCK_MOVE_TERM
	    					             , 'N'         AS DEL_YN
	    					             , :V_MODIFIER AS MODIFIER
	    					             , SYSDATE     AS MOD_DDTT
	    					             , A.TRANS_ORD_DATE || A.TRANS_ORD_SEQNO AS TRANS_WORD_NO
	    					             , (SELECT DECODE(STL_APPEAR_GP, 'Y', 'CG', 'CM')
	    					                  FROM TB_PT_COILCOMM
	    					                 WHERE COIL_NO = B.STL_NO
	    					               ) AS STOCK_ITEM
	    					          FROM TB_YD_CARSCH A
	    					             , TB_YD_CARFTMVMTL B
	    					             , TB_YD_STOCK C
	    					         WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
	    					           AND A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	    					           AND B.STL_NO        = C.STL_NO
	    					           AND A.DEL_YN        = 'N'
	    					      ) B
	    					   ON ( A.STOCK_ID = B.STOCK_ID )
	    					 WHEN MATCHED
	    					         THEN UPDATE
	    					          SET A.TRANS_WORD_NO = B.TRANS_WORD_NO
	    					            , A.DEL_YN        = B.DEL_YN
	    					            , A.MODIFIER      = B.MODIFIER
	    					            , A.MOD_DDTT      = SYSDATE
	    					 WHEN NOT MATCHED
	    					 THEN INSERT(
	    					              STOCK_ID
	    					            , STOCK_ITEM
	    					            , STOCK_STAT
	    					            , STOCK_MOVE_TERM
	    					            , TRANS_WORD_NO
	    					            , DEL_YN
	    					            , REGISTER
	    					            , REG_DDTT
	    					            , MODIFIER
	    					            , MOD_DDTT
	    					            )
	    					      VALUES(
	    					              B.STOCK_ID
	    					            , B.STOCK_ITEM
	    					            , B.STOCK_STAT
	    					            , B.STOCK_MOVE_TERM
	    					            , B.TRANS_WORD_NO
	    					            , B.DEL_YN
	    					            , B.MODIFIER
	    					            , B.MOD_DDTT
	    					            , B.MODIFIER
	    					            , B.MOD_DDTT
	    					            )
	    					*/
	    			    	commDao.update(jrYdMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYmStockCoilMerge", logId, mthdNm, "1열연 저장품정보 저장");
            			}
	    					
            			if ("D2Y44".equals(sArrWlocCd) || "D2Y45".equals(sArrWlocCd)) {	 //박판
	    					/*
	    					MERGE INTO TB_YF_STOCK A
	    					USING (
	    					        SELECT B.STL_NO
	    					             , (SELECT DECODE(C.STL_APPEAR_GP, 'Y', 'CG', 'CM')
	    					                  FROM TB_PT_COILCOMM
	    					                 WHERE COIL_NO = B.STL_NO
	    					                )               AS STOCK_ITEM
	    					             , C.TRANS_ORD_DATE
	    					             , C.TRANS_ORD_SEQNO
	    					             , '2'              AS YD_MTL_STAT
	    					             , 'EC'             AS STOCK_MOVE_TERM
	    					             , 'N'              AS DEL_YN
	    					             , :V_MODIFIER      AS MODIFIER
	    					             , SYSDATE          AS MOD_DDTT
	    					          FROM TB_YD_CARSCH     A
	    					             , TB_YD_CARFTMVMTL B
	    					             , TB_YD_STOCK      C
	    					         WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	    					           AND A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
	    					           AND B.STL_NO        = C.STL_NO
	    	                           AND B.DEL_YN        = 'N'
	    					      ) B
	    					   ON ( A.STL_NO = B.STL_NO )
	    					 WHEN MATCHED
	    					      THEN UPDATE
	    					              SET A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
	    					                , A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
	    					                , A.MODIFIER        = B.MODIFIER
	    					                , A.MOD_DDTT        = B.MOD_DDTT
	    					 WHEN NOT MATCHED
	    					      THEN INSERT (
	    					                    STL_NO
	    					                  , STOCK_ITEM
	    					                  , TRANS_ORD_DATE
	    					                  , TRANS_ORD_SEQNO
	    					                  , YD_MTL_STAT
	    					                  , STOCK_MOVE_TERM
	    					                  , DEL_YN
	    					                  , REGISTER
	    					                  , REG_DDTT
	    					                  , MODIFIER
	    					                  , MOD_DDTT
	    					                  )
	    					           VALUES (
	    					                    B.STL_NO
	    					                  , B.STOCK_ITEM
	    					                  , B.TRANS_ORD_DATE
	    					                  , B.TRANS_ORD_SEQNO
	    					                  , B.YD_MTL_STAT
	    					                  , B.STOCK_MOVE_TERM
	    					                  , B.DEL_YN
	    					                  , B.MODIFIER
	    					                  , B.MOD_DDTT
	    					                  , B.MODIFIER
	    					                  , B.MOD_DDTT
	    					                  )
	    					*/
	    			    	commDao.update(jrYdMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYfStockCoilMerge", logId, mthdNm, "박판열연 저장품정보 저장");
            			}
	    			
	    			} else if ("P".equals(sTransEquipmentType)) {					//출하PDA
	    				
    					/***********************************
    					 * 코일제품이송 상차완료 전송PDA
    					 ***********************************/
    					commUtils.printLog(logId, "[YDDMR073]코일제품이송 상차완료 전문 송신", "SL");
    					JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
    					
//PIDEV
//    					sndL2Msg.setField("JMS_TC_CD"    , "YDDMR073"); 
//    					sndL2Msg.setField("YD_CAR_SCH_ID", ydCarSchId);
//						
//    					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR073", sndL2Msg));	
    					
//						sndL2Msg.setField("MQ_TC_CD"     , "M10YDLMJ1091"); 
						sndL2Msg.setField("YD_CAR_SCH_ID" , ydCarSchId);
//						
//    					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("M10YDLMJ1091B", sndL2Msg));		    							
						
    					String sApp810 = coilDao.ApplyYn(logId, mthdNm, "APP810","J","*"); //
    					commUtils.printLog(logId, "공냉재 소재통로 검수완료전문 자동 송신 : " + sApp810, "SL");
    					
    					if ("Y".equals(sApp810)) {
    						
    						if ("Y".equals(sPI0001)) {
    					    	sAutoCheck = "Y";
						  	} else {
		    					/***************************************
								 * 공냉재 소재통로 이송시 검수완료 전문 송신 
								 ***************************************/
		    					/*
					    		SELECT CS.TRANS_ORD_DATE  AS TRANS_WORD_DATE
								     , CS.TRANS_ORD_SEQNO AS TRANS_WORD_SEQNO
								     , CS.CAR_NO
								     , ST.STL_NO          AS GOODS_NO
								     , ST.CR_FRTOMOVE_GP
								     , COUNT(*) OVER (PARTITION BY CS.TRANS_ORD_DATE, CS.TRANS_ORD_SEQNO) AS GOODS_NO_CNT
								     , '' AS GOODS_CHK_AB_CD
								     , '' AS LABEL_REISSUE_YN
								     , ST.YD_CAR_UPP_LOC_CD
								  FROM TB_PT_COILCOMM  CC
								     , TB_YD_CARSCH    CS
								     , TB_YD_STOCK     ST
								 WHERE CC.COIL_NO         = ST.STL_NO
								   AND CS.TRANS_ORD_DATE  = ST.TRANS_ORD_DATE  
								   AND CS.TRANS_ORD_SEQNO = ST.TRANS_ORD_SEQNO 
								   AND CS.YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
								   AND CS.YD_CARLD_STOP_LOC LIKE 'J_PT03' --소재통로
								   AND CS.DEL_YN = 'N'
								   AND (SUBSTR(CC.NEXT_PROC,2,1) IN ('A')  
								        OR EXISTS (SELECT 1
								              FROM TB_HR_C_SHEARWOWR SR
								             WHERE SR.HR_PLNT_GP = 'C'
								               AND SR.WORK_STAT  = '*'
								               AND SR.WORD_PROC  LIKE '%A'
								               AND SR.COIL_NO = ST.STL_NO
								               AND SR.RECEIPT_HOLD_SCRAP_CAUSE_GP IN ('I','B')
								               AND SR.STEP_NO = (SELECT MAX(STEP_NO)
								                                   FROM TB_HR_C_SHEARWOWR
								                                   WHERE COIL_NO = SR.COIL_NO) 
								                )
								        )   
					    		*/
								JDTORecordSet jsAirClList = commDao.select(sndL2Msg, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getAirclMtlYn", logId, mthdNm, "검수완료전문데이터");
								
								if (jsAirClList.size() > 0) {
		    			    		
		    						JDTORecord jrTcInfo = JDTORecordFactory.getInstance().create();
		    						
		//PIDEV   						
		//        		    			jrTcInfo.setField("JMS_TC_CD"			, "YDDMR074");
		//        		    			jrTcInfo.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
		//        		    			jrTcInfo.setField("TC_CODE"			    , "YDDMR074");
		//        		    			jrTcInfo.setField("TC_CREATE_DDTT"	    , commUtils.getDateTime14());
		//        		    			jrTcInfo.setField("TRANS_WORD_DATE"		, jsAirClList.getRecord(0).getFieldString("TRANS_WORD_DATE"));
		//        		    			jrTcInfo.setField("TRANS_WORD_SEQNO"	, jsAirClList.getRecord(0).getFieldString("TRANS_WORD_SEQNO"));
		//        		    			jrTcInfo.setField("CARLD_CHK_DONE_DATE"	, commUtils.getDate8()); // yyyyMMdd
		//        		    			jrTcInfo.setField("CARLD_CHK_DONE_TIME"	, commUtils.getTime6()); // HHmmss
		//        		    			jrTcInfo.setField("CAR_NO"				, jsAirClList.getRecord(0).getFieldString("CAR_NO"));
		    							
		    		    			jrTcInfo.setField("MQ_TC_CD"			, "M10YDLMJ1101");
		    		    			jrTcInfo.setField("MQ_TC_CREATE_DDTT"	, commUtils.getDateTime14());
		    		    			jrTcInfo.setField("TRN_REQ_DATE"		, jsAirClList.getRecord(0).getFieldString("TRANS_WORD_DATE"));
		    		    			jrTcInfo.setField("TRN_REQ_SEQ"			, jsAirClList.getRecord(0).getFieldString("TRANS_WORD_SEQNO"));
		    		    			jrTcInfo.setField("CAR_NO"				, jsAirClList.getRecord(0).getFieldString("CAR_NO"));
		    		    			
		    		    			jrTcInfo.setField("YD_GP"				, "J");
		    		    			jrTcInfo.setField("DIST_GOODS_GP"		, "H");
		    		    			jrTcInfo.setField("SCH_YN"				, "Y");
		    		    			
		    		    			jrTcInfo.setField("CARLD_CHK_DONE_DATE"	, commUtils.getDate8()); // yyyyMMdd
		    		    			jrTcInfo.setField("CARLD_CHK_DONE_TIME"	, commUtils.getTime6()); // HHmmss
		    							
		    						JDTORecord jrTcList = JDTORecordFactory.getInstance().create();
		    						for (int Loop_i = 1; Loop_i <= jsAirClList.size(); Loop_i++) {
		    							jsAirClList.absolute(Loop_i);
		    			    			jrTcList = jsAirClList.getRecord();
		    			    			
		    			    			if( Loop_i == 1 )
		    			    				jrTcInfo.setField("GOODS_NO_CNT", commUtils.nvl(jrTcList.getFieldString("GOODS_NO_CNT"), "0"));
			    			    			jrTcInfo.setField("GOODS_NO"		+ Loop_i, commUtils.trim(jrTcList.getFieldString("GOODS_NO")));
			    			    			jrTcInfo.setField("GOODS_CHK_AB_CD"	+ Loop_i, commUtils.trim(jrTcList.getFieldString("GOODS_CHK_AB_CD")));
			    			    			jrTcInfo.setField("LABEL_REISSUE_YN"+ Loop_i, commUtils.trim(jrTcList.getFieldString("LABEL_REISSUE_YN")));
			    			    			jrTcInfo.setField("GDS_CARLD_LOC"	+ Loop_i, commUtils.trim(jrTcList.getFieldString("YD_CAR_UPP_LOC_CD")));
		    						}
		
		    						// 마지막 CR_FRTOMOVE_GP 값
		    						jrTcInfo.setField("CR_FRTOMOVE_GP", commUtils.trim(jrTcList.getFieldString("CR_FRTOMOVE_GP")));
		    						jrRtn = commUtils.addSndData(jrRtn, jrTcInfo);
		    					}
							} 
    					}// end app
	    			}
        			
	    			/**********************************
	    			 * 출하관리
	    			 **********************************/
	    			if ("G".equals(ydCarUseGp)) {
	    				
	    				jrCarMv = commUtils.getParam(logId, mthdNm, sModifier);
	    				jrCarMv.setField("YD_CAR_SCH_ID", ydCarSchId);
	    				jrCarMv.setField("COIL_NO"      , sStlNo    );
	    				jrCarMv.setField("STL_NO"       , sStlNo    );
	    				
	    				/****************************
	    				 *  임가공 대상재 조회
	    				 ****************************/
	    				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getRentprocFtmvWoTrgt_PIDEV
	    				--임가공 이송지시 대상
	    				SELECT 
	    					   A.GOODS_NO AS STL_NO 
	    				  FROM 
	    				  	   TB_LM_P_TRN_D A
	    				     , TB_LM_P_TRN_M B 
	    				 WHERE 
	    				 	   A.TRN_REQ_DATE = B.TRN_REQ_DATE
	    				   AND A.TRN_REQ_SEQ  = B.TRN_REQ_SEQ
	    				   AND A.GOODS_NO = :V_COIL_NO
	    				   AND A.DEL_YN  = 'N'
	    				--   AND TRN_REQ_SEQ >= 700000
	    				   AND B.TRN_FRTOMOVE_GP = '23'
	    				 */
//PIDEV_S :병행가동용:PI_YD
	    				jrCarMv.setField("PI_YD",    	sPI_YD);	    				
	    				JDTORecordSet jsRent = commDao.select(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getRentprocFtmvWoTrgt_PIDEV", logId, mthdNm, "임가공대상재 조회");
	    				
	    				if (jsRent.size() > 0) {
	    					commUtils.printLog(logId, "임가공대상재 존재", "SL");

	    					/* *********************
    		            	 * 임가공 복수상차 처리로직
    		            	 * *********************/
//PIDEV_S :병행가동용:PI_YD
		    				jrCarMv.setField("PI_YD",    	sPI_YD);	    					
    						JDTORecord jrCmbnCarldYn = this.procCmbnCarldYn(jrCarMv);
    						jrRtn = commUtils.addSndData(jrRtn, jrCmbnCarldYn);
    						
    						String sRtn = jrCmbnCarldYn.getFieldString("RTN_CD");
    						
    						commUtils.printLog(logId, "임가공 복수상차 처리 로직 정상 여부 : " + sRtn, "SL");
    						
    						if ("2".equals(sRtn)) { //조합상차 해당없을 경우
    							/*****************************
        						 * 임가공이송상차완료
        						 *****************************/
        						commUtils.printLog(logId, "[YDDMR022]임가공이송상차완료 전문 송신", "SL");
        						JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
        						
//PIDEV
//    	    					sndL2Msg.setField("JMS_TC_CD"     , "YDDMR022"); 
//    	    					sndL2Msg.setField("YD_CAR_SCH_ID" , ydCarSchId);
//	    					
//    	    					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR022", sndL2Msg));
    							sndL2Msg.setField("MQ_TC_CD"     , "M10YDLMJ1095"); //O
	    						sndL2Msg.setField("YD_CAR_SCH_ID" , ydCarSchId);
								
		    					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("M10YDLMJ1095A", sndL2Msg));		    							
    							
    	    					
    	    					String sAPP819_YN = coilDao.ApplyYn(logId, mthdNm, "APP819", "J", "*"); //임가공 권하 맵Clear
    	    					
    	    					if ("N".equals(sAPP819_YN)) {
    		    					//차량 자동출발 처리
    		    					JDTORecordSet jsCarSch = commDao.select(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCarsch", logId, mthdNm, "차량스케줄 조회");
    		    					if (jsCarSch.size() > 0) {
    		    					
    		    						jsCarSch.first();
    		    						
    		    						/**********************************************************
    		    						* 코일출하차량 출발처리 - 맵비활성화
    		    						**********************************************************/
    		    						JDTORecord jrYdDmMsg = commUtils.getParam(logId, mthdNm, sModifier);
    									jrYdDmMsg.setField("CARD_NO"        , commUtils.trim(jsCarSch.getRecord().getFieldString("CARD_NO"        )));
    									jrYdDmMsg.setField("CAR_NO"         , commUtils.trim(jsCarSch.getRecord().getFieldString("CAR_NO"         )));			
    									jrYdDmMsg.setField("SPOS_WLOC_CD"   , commUtils.trim(jsCarSch.getRecord().getFieldString("SPOS_WLOC_CD"   )));
    									jrYdDmMsg.setField("SPOS_YD_PNT_CD" , commUtils.trim(jsCarSch.getRecord().getFieldString("YD_PNT_CD1"     )));
    									jrYdDmMsg.setField("TRANS_ORD_DT"   , commUtils.trim(jsCarSch.getRecord().getFieldString("TRANS_ORD_DATE" )));
    									jrYdDmMsg.setField("TRANS_ORD_SEQNO", commUtils.trim(jsCarSch.getRecord().getFieldString("TRANS_ORD_SEQNO")));
//PIDEV_S :병행가동용:PI_YD
    									jrYdDmMsg.setField("PI_YD",    	sPI_YD);
    									EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
    									JDTORecord jrMsg = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { jrYdDmMsg });
    									
    									jrRtn = commUtils.addSndData(jrRtn, jrMsg);
    		    					}	    						
    	    					}
    						}

    					} else {
    						// 출하차량 상차완료 인 경우(제품)
    						commUtils.printLog(logId, "출하PDA 여부 : " +sTransEquipmentType  , "SL");
	    					if (!"P".equals(sTransEquipmentType)) { //출하PDA

	    						/* *********************
	    		            	 * 일반제품 복수상차 처리로직
	    		            	 * *********************/
//PIDEV_S :병행가동용:PI_YD
			    				jrCarMv.setField("PI_YD",    	sPI_YD);		    						
	    						JDTORecord jrCmbnCarldYn = this.procCmbnCarldYn(jrCarMv);
	    						jrRtn = commUtils.addSndData(jrRtn, jrCmbnCarldYn);
	    						
	    						String sRtn = jrCmbnCarldYn.getFieldString("RTN_CD");
	    						
	    						commUtils.printLog(logId, "일반제품 복수상차 처리 로직 정상 여부 : " + sRtn, "SL");
	    						
	    						if ("2".equals(sRtn)) { //조합상차 해당없을 경우
	    							/**************************************
	    							 * 코일출하상차 완료
	    							 **************************************/
	    							commUtils.printLog(logId, "[YDDMR015]일반제품 코일출하상차 완료 전문 송신", "SL");
		    						JDTORecord jrYdDmMsg = commUtils.getParam(logId, mthdNm, sModifier);
		    						
//PIDEV	
//		    						jrYdDmMsg.setField("JMS_TC_CD"     , "YDDMR015"); 
//		    						jrYdDmMsg.setField("YD_CAR_SCH_ID" , ydCarSchId);
//			    					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR015", jrYdDmMsg));		    							

		    						jrYdDmMsg.setField("MQ_TC_CD"     , "M10YDLMJ1091"); 
		    						jrYdDmMsg.setField("YD_CAR_SCH_ID" , ydCarSchId);
									
			    					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("M10YDLMJ1091A", jrYdDmMsg));		    							
	    						}
	    					} 
//PIDEV 추가  일반 이송도 복수동 추가처리 함 
    						/**********************
    		            	 * 제품이송 복수상차 처리로직
    		            	 **********************/
	    					
	    					else if ("P".equals(sTransEquipmentType)) { //출하PDA
	    						
//PIDEV_S :병행가동용:PI_YD
			    				jrCarMv.setField("PI_YD",    	sPI_YD);	    						
	    						JDTORecord jrCmbnCarldYn = this.procCmbnCarldYn(jrCarMv);
	    						jrRtn = commUtils.addSndData(jrRtn, jrCmbnCarldYn);
	    						
	    						String sRtn = jrCmbnCarldYn.getFieldString("RTN_CD");
	    						
	    						commUtils.printLog(logId, "제품이송 복수상차 처리 로직 정상 여부 : " + sRtn, "SL");
	    						
	    						if ("2".equals(sRtn)) { //조합상차 해당없을 경우
	    							/**************************************
	    							 * 제품이송 완료처리
	    							 **************************************/
	    							commUtils.printLog(logId, "제품이송 완료 전문 송신", "SL");
		    						JDTORecord jrYdDmMsg = commUtils.getParam(logId, mthdNm, sModifier);
		    						
//PIDEV	

		    						jrYdDmMsg.setField("MQ_TC_CD"     , "M10YDLMJ1091"); 
		    						jrYdDmMsg.setField("YD_CAR_SCH_ID" , ydCarSchId);
		    						
		        					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("M10YDLMJ1091B", jrYdDmMsg));					    					
			    					
	    						}	    						
	    						
	    					}
	    				} 
	    			} 
        		} 
        	} //상차
			/***************************************
			 * 공냉재 소재통로 이송시 검수완료 전문 송신 
			 ***************************************/
			if ("Y".equals(sPI0001)) {    

   				commUtils.printLog(logId, "공냉재 소재통로 검수완료전문 자동검수 이동처리 : " + sPI0001, "SL");     
				if ("Y".equals(sAutoCheck)) {
				
					JDTORecord sndL2Msg1 = commUtils.getParam(logId, mthdNm, sModifier);
					sndL2Msg1.setField("YD_CAR_SCH_ID" , ydCarSchId);			  
					/***************************************
					 * 공냉재 소재통로 이송시 검수완료 전문 송신 
					 ***************************************/
					/*
		    		SELECT CS.TRANS_ORD_DATE  AS TRANS_WORD_DATE
					     , CS.TRANS_ORD_SEQNO AS TRANS_WORD_SEQNO
					     , CS.CAR_NO
					     , ST.STL_NO          AS GOODS_NO
					     , ST.CR_FRTOMOVE_GP
					     , COUNT(*) OVER (PARTITION BY CS.TRANS_ORD_DATE, CS.TRANS_ORD_SEQNO) AS GOODS_NO_CNT
					     , '' AS GOODS_CHK_AB_CD
					     , '' AS LABEL_REISSUE_YN
					     , ST.YD_CAR_UPP_LOC_CD
					  FROM TB_PT_COILCOMM  CC
					     , TB_YD_CARSCH    CS
					     , TB_YD_STOCK     ST
					 WHERE CC.COIL_NO         = ST.STL_NO
					   AND CS.TRANS_ORD_DATE  = ST.TRANS_ORD_DATE  
					   AND CS.TRANS_ORD_SEQNO = ST.TRANS_ORD_SEQNO 
					   AND CS.YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
					   AND CS.YD_CARLD_STOP_LOC LIKE 'J_PT03' --소재통로
					   AND CS.DEL_YN = 'N'
					   AND (SUBSTR(CC.NEXT_PROC,2,1) IN ('A')  
					        OR EXISTS (SELECT 1
					              FROM TB_HR_C_SHEARWOWR SR
					             WHERE SR.HR_PLNT_GP = 'C'
					               AND SR.WORK_STAT  = '*'
					               AND SR.WORD_PROC  LIKE '%A'
					               AND SR.COIL_NO = ST.STL_NO
					               AND SR.RECEIPT_HOLD_SCRAP_CAUSE_GP IN ('I','B')
					               AND SR.STEP_NO = (SELECT MAX(STEP_NO)
					                                   FROM TB_HR_C_SHEARWOWR
					                                   WHERE COIL_NO = SR.COIL_NO) 
					                )
					        )   
		    		*/
					JDTORecordSet jsAirClList = commDao.select(sndL2Msg1, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getAirclMtlYn", logId, mthdNm, "검수완료전문데이터");
					
					if (jsAirClList.size() > 0) {
			    		
						JDTORecord jrTcInfo = JDTORecordFactory.getInstance().create();
						
		    			jrTcInfo.setField("MQ_TC_CD"			, "M10YDLMJ1101");
		    			jrTcInfo.setField("MQ_TC_CREATE_DDTT"	, commUtils.getDateTime14());
		    			jrTcInfo.setField("TRN_REQ_DATE"		, jsAirClList.getRecord(0).getFieldString("TRANS_WORD_DATE"));
		    			jrTcInfo.setField("TRN_REQ_SEQ"			, jsAirClList.getRecord(0).getFieldString("TRANS_WORD_SEQNO"));
		    			jrTcInfo.setField("CAR_NO"				, jsAirClList.getRecord(0).getFieldString("CAR_NO"));
		    			
		    			jrTcInfo.setField("YD_GP"				, "J");
		    			jrTcInfo.setField("DIST_GOODS_GP"		, "H");
		    			jrTcInfo.setField("SCH_YN"				, "Y");
		    			
		    			jrTcInfo.setField("CARLD_CHK_DONE_DATE"	, commUtils.getDate8()); // yyyyMMdd
		    			jrTcInfo.setField("CARLD_CHK_DONE_TIME"	, commUtils.getTime6()); // HHmmss
							
						JDTORecord jrTcList = JDTORecordFactory.getInstance().create();
						for (int Loop_i = 1; Loop_i <= jsAirClList.size(); Loop_i++) {
							jsAirClList.absolute(Loop_i);
			    			jrTcList = jsAirClList.getRecord();
			    			
			    			if( Loop_i == 1 )
			    				jrTcInfo.setField("GOODS_NO_CNT", commUtils.nvl(jrTcList.getFieldString("GOODS_NO_CNT"), "0"));
				    			jrTcInfo.setField("GOODS_NO"		+ Loop_i, commUtils.trim(jrTcList.getFieldString("GOODS_NO")));
				    			jrTcInfo.setField("GOODS_CHK_AB_CD"	+ Loop_i, commUtils.trim(jrTcList.getFieldString("GOODS_CHK_AB_CD")));
				    			jrTcInfo.setField("LABEL_REISSUE_YN"+ Loop_i, commUtils.trim(jrTcList.getFieldString("LABEL_REISSUE_YN")));
				    			jrTcInfo.setField("GDS_CARLD_LOC"	+ Loop_i, commUtils.trim(jrTcList.getFieldString("YD_CAR_UPP_LOC_CD")));
						}
	
						// 마지막 CR_FRTOMOVE_GP 값
						jrTcInfo.setField("CR_FRTOMOVE_GP", commUtils.trim(jrTcList.getFieldString("CR_FRTOMOVE_GP")));
						jrRtn = commUtils.addSndData(jrRtn, jrTcInfo);
					}
				} // end app
			}
        	/***********************************
        	 * 하차
        	 ***********************************/
        	if ("L".equals(sCarLdudGp)) {
        		commUtils.printLog(logId, "하차", "SL");
        		
        		String sCarUdCmplYn = "N";
        		/*
				SELECT TS.YD_CAR_SCH_ID
				     , DECODE((SELECT COUNT(*) 
				                 FROM TB_YD_CARFTMVMTL 
				                WHERE YD_CAR_SCH_ID = TS.YD_CAR_SCH_ID
				                  AND DEL_YN = 'N'),1,'Y','N') AS CAR_UD_CMPL_YN --차량하차완료여부(권상실적에서 이송재료 삭제)
				     , TS.CAR_NO
				     , TS.ARR_WLOC_CD AS WLOC_CD
				     , TS.YD_PNT_CD3  AS YD_PNT_CD
				     , TS.TRANS_ORD_DATE
				     , TS.TRANS_ORD_SEQNO
				     , TS.TRANS_EQUIPMENT_TYPE  
				     , TS.YD_CAR_USE_GP
				     , TM.STL_NO
				     , TS.YD_EQP_WRK_STAT
				  FROM TB_YD_CARSCH     TS
				     , TB_YD_CARFTMVMTL TM
				     , TB_YD_CRNSCH     CS
				 WHERE TS.YD_CAR_SCH_ID = TM.YD_CAR_SCH_ID
				   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND CS.YD_CRN_GRAB_USE_RULE_ID = TS.YD_CAR_SCH_ID
    			 */
    			JDTORecordSet jsCarUd = commDao.select(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarSchUd", logId, mthdNm, "하차완료 체크");
    			if (jsCarUd.size() <= 0) {
    				commUtils.printLog(logId, "차량스케줄 정보가 존재하지 않습니다.", "SL");
    			} else {
	        		jsCarUd.first();
	        		sCarUdCmplYn        = commUtils.trim(jsCarUd.getRecord().getFieldString("CAR_UD_CMPL_YN")); //하차완료여부
    				ydCarSchId          = commUtils.trim(jsCarUd.getRecord().getFieldString("YD_CAR_SCH_ID" )); 	               
	        		ydCarUseGp          = commUtils.trim(jsCarUd.getRecord().getFieldString("YD_CAR_USE_GP" ));
	        		sTransEquipmentType = commUtils.trim(jsCarUd.getRecord().getFieldString("TRANS_EQUIPMENT_TYPE"));
    			}
        		
        		commUtils.printLog(logId, "하차작업 구내운송 차량여부 : " +ydCarUseGp + "소재이송여부 " + sStlAppearGp , "SL");
        		
            	/***********************************
            	 * 코일이송일품상차실적 송신
            	 ***********************************/
    			commUtils.printLog(logId, "코일제품이송 여부 : " + sTransEquipmentType, "SL");

            	if ("P".equals(sTransEquipmentType)) {
    	        	JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
    	        	jrYdMsg.setField("JMS_TC_CD"    , "YDDMR072");
    	        	jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
    				jrYdMsg.setField("STL_NO"		, sStlNo    );
    				
//PIDEV
//					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDDMR072", jrYdMsg));	
					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("M10YDLMJ1081B", jrYdMsg));											
            	}
        		
        		//구내운송 차량이고 소재이송인 경우에만 처리함
        		if ("L".equals(ydCarUseGp) && !"Y".equals(sStlAppearGp)) {
        			
        			jrCarMv = commUtils.getParam(logId, mthdNm, sModifier);
    				jrCarMv.setField("STL_NO" , sStlNo);
    				/*
					SELECT STL_NO
					     , TRANSWORD_SEQNO
					     , SPOS_WLOC_CD
					     , ARR_WLOC_CD
					  FROM USRPTA.TB_PT_STLFRTOMOVE A
					 WHERE STL_NO = :V_STL_NO
					   AND A.FRTOMOVE_STAT_CD  IN('1','3')
					   AND A.TRANSWORD_SEQNO = (SELECT /*+ INDEX_DESC(B PK_PT_STLFRTOMOVE)
					                                   MAX(TRANSWORD_SEQNO)
					                              FROM TB_PT_STLFRTOMOVE B
					                             WHERE A.STL_NO = B.STL_NO
					                               AND FRTOMOVE_STAT_CD NOT IN ('Z','C')
					                               AND ROWNUM <= 1
					                            )
    				 */
    				JDTORecordSet jsPtStl = commDao.select(jrCarMv, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getPtStlFrtoMove", logId, mthdNm, "이송지시 조회");
    				
    				if (jsPtStl.size() <= 0) {
    					commUtils.printLog(logId, "이송지시테이블에 재료정보["+sStlNo+"] 존재하지 않음", "SL");
    				} else {
    					
    					jsPtStl.first();
    					JDTORecord jrPtStl = jsPtStl.getRecord();
    					jrPtStl.setField("YD_MTL_PLN_STR_TO_LOC_CD", ydDnWrLoc);
    					jrPtStl.setField("FRTOMOVE_STAT_CD"        , "*");
    					/*
    					UPDATE USRPTA.TB_PT_STLFRTOMOVE
						   SET FRTOMOVE_DONE_DATE       = SYSDATE-0.00003
						     , FTMV_HDS_DD              = TO_CHAR(SYSDATE - (6 / 24), 'YYYYMMDD')
						     , YD_MTL_PLN_STR_TO_LOC_CD = :V_YD_MTL_PLN_STR_TO_LOC_CD
						     , FRTOMOVE_STAT_CD         = :V_FRTOMOVE_STAT_CD
						     , MODIFIER = :V_MODIFIER
						     , MOD_DDTT = SYSDATE
						 WHERE STL_NO          = :V_STL_NO
						   AND TRANSWORD_SEQNO = :V_TRANSWORD_SEQNO
    					 */
        				commDao.update(jrPtStl, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updPtStlFrtoMove2", logId, mthdNm, "이송지시 테이블 수정");
    					
    				}
    				
    				/**************************************
					 * 코일소재이송완료실적
					 **************************************/
    				commUtils.printLog(logId, "[YDPTJ002]코일소재이송완료실적 전문 송신", "SL");
					
    				JDTORecord jrYdPtMsg = commUtils.getParam(logId, mthdNm, sModifier);
    				jrYdPtMsg.setField("JMS_TC_CD"     , "YDPTJ002"); 
    				jrYdPtMsg.setField("COIL_NO"       , sStlNo    );
    				jrYdPtMsg.setField("YD_CAR_SCH_ID" , ydCarSchId);
					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDPTJ002", jrYdPtMsg));
        		}
        		
        	} //하차
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			jrRtn.setField("YD_CAR_SCH_ID", ydCarSchId);
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 	
	/**
	 *      [A] 오퍼레이션명 :  복수상차
	 *      PIDEV
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procCmbnCarldYn_PIDEV(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "복수상차[CCoilL2RcvSeEJB.procCmbnCarldYn_PIDEV] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
			
			String sStlNo         = commUtils.trim(rcvMsg.getFieldString("STL_NO"     ));
			String sModifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			//PIDEV_S :병행가동용:PI_YD
			String sPI_YD         = commUtils.nvl(rcvMsg.getFieldString("PI_YD"),"*");				
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			/********************************
			 * 차량스케줄 여부 확인
			 ********************************/
			jrParam.setField("STL_NO", sStlNo);
			jrParam.setField("YD_GP" , "J"   );
			
			/*
			SELECT A.*
			  FROM TB_YD_CARSCH A
			     , (
			        SELECT TRANS_ORD_DATE,TRANS_ORD_SEQNO
			          FROM USRYDA.TB_YD_STOCK
			         WHERE STL_NO= :V_STL_NO
			         UNION
			        SELECT TRANS_ORD_DATE,TRANS_ORD_SEQNO
			          FROM USRYFA.TB_YF_STOCK
			         WHERE STL_NO= :V_STL_NO
			         UNION         			         
			        SELECT NVL(TRANS_ORD_DATE2,SUBSTR(TRANS_WORD_NO,1,8))
			             , NVL(TRANS_ORD_SEQNO2,SUBSTR(TRANS_WORD_NO,9))
			          FROM USRYMA.TB_YM_STOCK
			         WHERE STOCK_ID =:V_STL_NO
			       ) B
			 WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
			   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
			   AND DEL_YN   = 'N'
			 ORDER BY A.YD_CAR_SCH_ID DESC
			 */
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarwbookid", logId, mthdNm, "차량스케줄 조회");
				
			if (jsCarSch.size() <= 0) {
				commUtils.printLog(logId, "차량스케줄이 존재하지 않습니다.", "S-");
				jrRtn.setField("RTN_CD", "0");
				return jrRtn;
			}

			String sCmbnCarldYn   = commUtils.nvl (jsCarSch.getRecord(0).getFieldString("CMBN_CARLD_YN"    ), "N");
			String sWorkGp        = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_WRK_GP"    ));
			String sTelNO         = commUtils.trim(jsCarSch.getRecord(0).getFieldString("TEL_NO"           ));
			String sCarNo         = commUtils.trim(jsCarSch.getRecord(0).getFieldString("CAR_NO"           ));
			String sCardNo        = commUtils.trim(jsCarSch.getRecord(0).getFieldString("CARD_NO"          ));
			String sCarKind       = commUtils.trim(jsCarSch.getRecord(0).getFieldString("CAR_KIND"         ));
			String sPosWlocCd     = commUtils.trim(jsCarSch.getRecord(0).getFieldString("SPOS_WLOC_CD"     ));
			String ydPntCd1       = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_PNT_CD1"       ));
			String ydStkColGp     = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CARLD_STOP_LOC"));
			String sTransOrdDate  = commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_DATE"   ));
			String sTransOrdSeqno = commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_SEQNO"  ));
			String sDriverName    = commUtils.trim(jsCarSch.getRecord(0).getFieldString("DRIVER_NAME"      ));
			
			/******************************
			 * 조합상차 여부 확인
			 ******************************/
			//조합상차(시작:S , 종료: E ,  단일상차: N )
			commUtils.printLog(logId, "조합상차 여부 : " + sCmbnCarldYn  , "SL");

			if (!"S".equals(sCmbnCarldYn)) {
				commUtils.printLog(logId, "복수상차가 아닙니다.", "S-");
				jrRtn.setField("RTN_CD", "2");
				return jrRtn;
			}
			
			/*********************************
			 * 저장품 종료
			 *********************************/
			jrParam.setField("TRANS_ORD_DATE" , sTransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
			jrParam.setField("YD_STK_COL_GP"  , ydStkColGp   );
			jrParam.setField("DEL_YN"         , "Y"          );
			jrParam.setField("STL_PROG_CD"    , "M"          );
			jrParam.setField("YD_AIM_RT_GP"   , "M2"         );
			
			/*
			UPDATE TB_YD_STOCK  
			   SET DEL_YN       = :V_DEL_YN  
			     , STL_PROG_CD  = :V_STL_PROG_CD  
			     , YD_AIM_RT_GP = :V_YD_AIM_RT_GP  
			     , MODIFIER     = :V_MODIFIER  
			     , MOD_DDTT     = SYSDATE  
			 WHERE STL_NO IN (  
			                  SELECT A.STL_NO  
			                    FROM TB_YD_STOCK   A  
			                       , TB_YD_STKLYR B  
			                   WHERE A.STL_NO = B.STL_NO  
			                     AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE  
			                     AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO  
			                     AND B.YD_STK_COL_GP LIKE SUBSTR(:V_YD_STK_COL_GP,1,2)||'%'  
			                     AND SUBSTR(B.YD_STK_COL_GP,3,2) IN('PT','TR','TT')
			                 ) 
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStockTrnsOrd", logId, mthdNm, "저장품 종료");
			
			/**********************************************************
			* 코일출하차량 출발처리 - 맵비활성화
			**********************************************************/
			JDTORecord jrYdDmMsg = commUtils.getParam(logId, mthdNm, sModifier);
			jrYdDmMsg.setField("CARD_NO"        , sCardNo      );
			jrYdDmMsg.setField("CAR_NO"         , sCarNo       );			
			jrYdDmMsg.setField("SPOS_WLOC_CD"   , sPosWlocCd   );
			jrYdDmMsg.setField("SPOS_YD_PNT_CD" , ydPntCd1     );
			jrYdDmMsg.setField("TRANS_ORD_DT"   , sTransOrdDate);
			jrYdDmMsg.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
//PIDEV_S :병행가동용:PI_YD
			jrYdDmMsg.setField("PI_YD",    	sPI_YD);	
			EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
			JDTORecord jrMsg = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { jrYdDmMsg });
			
			jrRtn = commUtils.addSndData(jrRtn, jrMsg);
			
			/**********************************************************
			* 저장품제원 : 코일야드L2로 송신(YDY5L002)
			**********************************************************/
			JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
			sndL2Msg.setField("JMS_TC_CD"      , "YDY5L002");
			sndL2Msg.setField("YD_INFO_SYNC_CD", "3"       );
			sndL2Msg.setField("YD_STK_COL_GP"  , ydStkColGp);
			sndL2Msg.setField("YD_STK_BED_NO"  , ""        );
//PIDEV_S :병행가동용:PI_YD
			sndL2Msg.setField("PI_YD",    	sPI_YD);			
			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L002", sndL2Msg));
			
			/*************************
			 * 복수 창고 구분
			 *************************/
			/*
			WITH TEMP_TABLE AS (
			SELECT A.TRANS_ORD_DATE
			     , A.TRANS_ORD_SEQNO
			     , A.YD_GP
			  FROM (
			        SELECT B.YD_GP
			             , A.STL_NO
			             , A.TRANS_ORD_DATE
			             , A.TRANS_ORD_SEQNO
			          FROM USRYDA.TB_YD_STOCK A
			             , TB_PT_COILCOMM B
			         WHERE A.STL_NO = B.COIL_NO 
			           AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
			           AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			         UNION ALL
			        SELECT B.YD_GP
			             , A.STL_NO
			             , A.TRANS_ORD_DATE
			             , A.TRANS_ORD_SEQNO
			          FROM USRYFA.TB_YF_STOCK A
			             , TB_PT_COILCOMM B
			         WHERE A.STL_NO = B.COIL_NO 
			           AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
			           AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			         UNION ALL         			         
			        SELECT B.YD_GP 
			             , A.STOCK_ID
			             , A.TRANS_ORD_DATE2
			             , A.TRANS_ORD_SEQNO2
			          FROM USRYMA.TB_YM_STOCK A
			             , TB_PT_COILCOMM B
			         WHERE A.STOCK_ID = B.COIL_NO 
			           AND A.TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE
			           AND A.TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
			       ) A
			 GROUP BY  A.TRANS_ORD_DATE ,A.TRANS_ORD_SEQNO, A.YD_GP
			)
			SELECT A.TRANS_ORD_DATE
			     , A.TRANS_ORD_SEQNO
			     , COUNT(YD_GP) AS CNT
			     , (SELECT B.YD_GP 
			          FROM TEMP_TABLE B
			         WHERE B.YD_GP <> :V_YD_GP) AS NEXT_YD_GP
			  FROM TEMP_TABLE A
			 GROUP BY A.TRANS_ORD_DATE
			        , A.TRANS_ORD_SEQNO
			 HAVING COUNT(YD_GP)>1          
			 */
			JDTORecordSet jsCmplGp = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarYdCmbnCarldGP", logId, mthdNm, "복수창고 구분");
			
			if (jsCmplGp.size() <= 0) {
				commUtils.printLog(logId, "복수창고가 아닌경우(복수동)", "SL");
				/************************
				 * 일반 출하 복수동 
				 ************************/				
				
				if (!"9".equals(sWorkGp)) {
					
					/*
					WITH TEMP_TABLE AS (
					SELECT :V_YD_GP AS YD_GP 
					     , :V_TRANS_ORD_DATE  AS TRANS_ORD_DATE 
					     , :V_TRANS_ORD_SEQNO AS TRANS_ORD_SEQNO
					  FROM DUAL
					)
					SELECT COUNT(*) AS CHK
					  FROM (
					        SELECT SUBSTR(YD_CARPNT_CD,2,2) 
					          FROM (
					                SELECT A.STL_NO
					                     , B.YD_STK_COL_GP
					                     , SUBSTR(B.YD_STK_COL_GP,1,1) AS YD_GP
					                     , SUBSTR(B.YD_STK_COL_GP,3,2) AS SPAN_GP
					                     , SUBSTR(B.YD_STK_COL_GP,2,1) AS BAY_GP
					                  FROM USRYDA.TB_YD_STOCK  A
					                     , USRYDA.TB_YD_STKLYR B
					                     , TEMP_TABLE C
					                 WHERE A.STL_NO = B.STL_NO
					                   AND A.TRANS_ORD_DATE  = C.TRANS_ORD_DATE
					                   AND A.TRANS_ORD_SEQNO = C.TRANS_ORD_SEQNO
					                   AND C.YD_GP ='J'
					               ) A
					             , USRYDA.TB_YD_CARPOINT B
					         WHERE A.YD_GP  = B.YD_GP
					           AND B.DEL_YN = 'N'
					           AND A.SPAN_GP BETWEEN B.YD_SPAN_FROM AND B.YD_SPAN_TO
					           AND A.BAY_GP = B.YD_BAY_GP
					           AND A.STL_NO <> :V_STL_NO
					         GROUP BY SUBSTR(YD_CARPNT_CD,2,2)
					       ) A 
					 WHERE 1 = 1        
					 */
					JDTORecordSet jsChkCmpl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarschCarNoCardNoTransNoCHK", logId, mthdNm, "복수창고 구분");
					
					String sChk = "";
					if (jsChkCmpl.size() > 0) {
						sChk = jsChkCmpl.getRecord(0).getFieldString("CHK");
					}
					commUtils.printLog(logId, "복수동 Chk : " + sChk , "SL");
					
					/************************
					 * 입동지시 호출
					 ************************/
	//				JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
	//				jrYdMsg.setField("JMS_TC_CD"            , "DMYDR061"    );
	//				jrYdMsg.setField("JMS_TC_CREATE_DDTT"   , commUtils.getDateTime14());
	//				jrYdMsg.setField("TC_CODE"		        , "DMYDR061"    );
	//				jrYdMsg.setField("TC_CREATE_DDTT"       , commUtils.getDateTime14());
	//				jrYdMsg.setField("YD_GP"		        , "J");					
	//				jrYdMsg.setField("WORK_GP"		        , sWorkGp);
	//				jrYdMsg.setField("TEL_NO"		        , sTelNO);
	//				jrYdMsg.setField("TRANS_ORD_DT"		    , sTransOrdDate);
	//				jrYdMsg.setField("TRANS_ORD_SEQNO" 		, sTransOrdSeqno);
	//				jrYdMsg.setField("CAR_NO"				, sCarNo );
	//				jrYdMsg.setField("CARD_NO"				, sCardNo);
	//				jrYdMsg.setField("CAR_KIND"				, sCarKind);
	//				jrYdMsg.setField("WAIT_ARR_DDTT"		, commUtils.getDateTime14());
	//				jrYdMsg.setField("WAIT_ARR_GP"			, "B");
	//				jrYdMsg.setField("DRIVER_NAME"			, sDriverName);
	//				
	//				if ("0".equals(sChk) || "1".equals(sChk)) { //복수동 존재 수량 체크
	//					jrYdMsg.setField("CMBN_CARLD_YN", "E");
	//				}else {                               
	//					jrYdMsg.setField("CMBN_CARLD_YN", "S");
	//				}
	//				
	//				String sAPP821 = coilDao.ApplyYn(logId, mthdNm, "APP821", "J", "*"); //복수동 대기장도착 전문송신 여부
	//				
	//				if ("Y".equals(sAPP821)) {
	//					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
	//				} else {
	//					EJBConnector ejbConn1 = new EJBConnector("default", "CCoilL3RcvSeEJB", this);
	//					JDTORecord jrArrived = (JDTORecord)ejbConn1.trx("rcvDMYDR061", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
	//					
	//					jrRtn = commUtils.addSndData(jrRtn, jrArrived);
	//				}				
					// 일반 출하 차량
					JDTORecord jrYdMsg  = JDTORecordFactory.getInstance().create();
					jrYdMsg.setField("MQ_TC_CD"             , "M10LMYDJ1041");
					jrYdMsg.setField("MQ_TC_CREATE_DDTT"    , commUtils.getDateTime14());
					jrYdMsg.setField("YD_GP"		        , "J");
					jrYdMsg.setField("WORK_GP"		        , sWorkGp);
					jrYdMsg.setField("TEL_NO"		        , sTelNO);
					jrYdMsg.setField("DRIVER_NAME"			, sDriverName);
					jrYdMsg.setField("TRN_REQ_DATE"		    , sTransOrdDate);
					jrYdMsg.setField("TRN_REQ_SEQ" 		    , sTransOrdSeqno);
					jrYdMsg.setField("CAR_KIND"				, sCarKind);
					jrYdMsg.setField("CAR_NO"				, sCarNo );
					jrYdMsg.setField("WAIT_ARR_DDTT"		, commUtils.getDateTime14());
					jrYdMsg.setField("WAIT_ARR_GP"			, "B");
					jrYdMsg.setField("DIST_GOODS_GP"		, "H");
					jrYdMsg.setField("YD_SND_YN"			, "Y");
					
					if ("0".equals(sChk) || "1".equals(sChk)) { //복수동 존재 수량 체크
						jrYdMsg.setField("CMBN_CARLD_YN", "E");
					}else {                               
						jrYdMsg.setField("CMBN_CARLD_YN", "S");
					}
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
					
				} else {

					/************************
					 * 이송 출하 복수동 
					 ************************/						
					/* com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarschCarNoCardNoTransNoCHK9_PIDEV 
					WITH TEMP_TABLE AS (
					SELECT :V_YD_GP AS YD_GP 
					     , :V_TRANS_ORD_DATE  AS TRANS_ORD_DATE 
					     , :V_TRANS_ORD_SEQNO AS TRANS_ORD_SEQNO
					  FROM DUAL
					)
					SELECT COUNT(*) OVER() AS CHK,YD_CARPNT_CD, STL_NO 
					  FROM (
					        SELECT MIN(YD_CARPNT_CD) AS YD_CARPNT_CD,STL_NO
					          FROM (
					                SELECT A.STL_NO
					                     , B.YD_STK_COL_GP
					                     , SUBSTR(B.YD_STK_COL_GP,1,1) AS YD_GP
					                     , SUBSTR(B.YD_STK_COL_GP,3,2) AS SPAN_GP
					                     , SUBSTR(B.YD_STK_COL_GP,2,1) AS BAY_GP
					                  FROM TB_YD_STOCK  A
					                     , TB_YD_STKLYR B
					                     , TEMP_TABLE   C
					                 WHERE A.STL_NO = B.STL_NO
					                   AND A.TRANS_ORD_DATE  = C.TRANS_ORD_DATE
					                   AND A.TRANS_ORD_SEQNO = C.TRANS_ORD_SEQNO
					                   AND C.YD_GP ='J'
					               ) A
					             , TB_YD_CARPOINT B
					         WHERE A.YD_GP  = B.YD_GP
					           AND B.DEL_YN = 'N'
					           AND A.SPAN_GP BETWEEN B.YD_SPAN_FROM AND B.YD_SPAN_TO
					           AND A.BAY_GP  = B.YD_BAY_GP
					           AND B.YD_CAR_USETYPE_GP IN ('TR','TO')
					           AND A.STL_NO <> :V_STL_NO
					         GROUP BY STL_NO
					       ) A 
					 WHERE 1 = 1  
					 */
					JDTORecordSet jsChkCmpl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarschCarNoCardNoTransNoCHK9_PIDEV", logId, mthdNm, "이송복수동 구분");
					
					String sChk = "";
					if (jsChkCmpl.size() > 0) {
						sChk = jsChkCmpl.getRecord(0).getFieldString("CHK");
						
						commUtils.printLog(logId, "이송 출하 복수동  Chk : " + sChk , "SL");
						
						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
						
						jrYdMsg.setField("MQ_TC_CD"             , "M10LMYDJ1041");
						jrYdMsg.setField("MQ_TC_CREATE_DDTT"    , commUtils.getDateTime14());
						jrYdMsg.setField("YD_GP"		        , "J");
						jrYdMsg.setField("WORK_GP"		        , sWorkGp);
						jrYdMsg.setField("TEL_NO"		        , sTelNO);
						jrYdMsg.setField("DRIVER_NAME"			, sDriverName);
						jrYdMsg.setField("TRN_REQ_DATE"		    , sTransOrdDate);
						jrYdMsg.setField("TRN_REQ_SEQ" 		    , sTransOrdSeqno);
						jrYdMsg.setField("CANCEL_YN"		    , "N");
						jrYdMsg.setField("CAR_KIND"				, sCarKind);
						jrYdMsg.setField("CAR_NO"				, sCarNo );
						jrYdMsg.setField("YD_EQP_WRK_SH"		, sChk);
						jrYdMsg.setField("CARLD_PNT_CD"			, jsChkCmpl.getRecord(0).getFieldString("YD_CARPNT_CD") );
						
						for (int i = 1; i <= jsChkCmpl.size(); ++i) {
							jsChkCmpl.absolute(i);
							jrYdMsg.setField("STL_NO"+i        , commUtils.trim(jsChkCmpl.getRecord().getFieldString("STL_NO"    )));
//							jrYdMsg.setField("GDS_CARLD_LOC"+i , commUtils.trim(jsChkCmpl.getRecord().getFieldString("GDS_CARLD_LOC")));
						}

						jrYdMsg.setField("WAIT_ARR_DDTT"		, commUtils.getDateTime14());
						jrYdMsg.setField("WAIT_ARR_GP"			, "B");
						jrYdMsg.setField("DIST_GOODS_GP"		, "H");

						jrYdMsg.setField("YD_SND_YN"			, "Y");
						
						if ("0".equals(sChk) || "1".equals(sChk)) { //복수동 존재 수량 체크
							jrYdMsg.setField("CMBN_CARLD_YN", "E");
						}else {                               
							jrYdMsg.setField("CMBN_CARLD_YN", "S");
						}

						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
					}
					
				}
			} else {
				
				commUtils.printLog(logId, "복수창고인 경우", "SL");
				
				String sNextYdGp = jsCmplGp.getRecord(0).getFieldString("NEXT_YD_GP");
				jrParam.setField("YD_GP", sNextYdGp);
				
				/*
				SELECT C.*
				  FROM USRYDA.TB_YD_STOCK A
				     , USRYDA.TB_YD_STKLYR B
				     , USRYDA.TB_YD_CARPOINT C
				 WHERE A.STL_NO = B.STL_NO
				   AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
				   AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				   AND SUBSTR(B.YD_STK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
				   AND SUBSTR(B.YD_STK_COL_GP,1,1) = C.YD_GP
				   AND 'J' = :V_YD_GP
				   AND C.DEL_YN = 'N'
				 UNION ALL
				 SELECT C.*
				  FROM USRYMA.TB_YM_STOCK A
				     , USRYMA.TB_YM_STACKLAYER B
				     , USRYDA.TB_YD_CARPOINT C
				 WHERE A.STOCK_ID = B.STOCK_ID 
				   AND A.TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE
				   AND A.TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
				   AND SUBSTR(B.STACK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
				   AND SUBSTR(B.STACK_COL_GP,1,1) = C.YD_GP
				   AND :V_YD_GP IN('3')
				 UNION ALL
				 SELECT C.*
				  FROM USRYFA.TB_YF_STOCK    A
				     , USRYFA.TB_YF_STKLYR   B
				     , USRYDA.TB_YD_CARPOINT C
				 WHERE A.STL_NO = B.STL_NO 
				   AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
				   AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				   AND SUBSTR(B.YD_STK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
				   AND SUBSTR(B.YD_STK_COL_GP,1,1) = C.YD_GP
				   AND :V_YD_GP IN('1')   
				 */
				JDTORecordSet jsNextYd = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarYdCmbnCarSch", logId, mthdNm, "다음 복수창고");
				
				if (jsNextYd.size() <= 0) {
					commUtils.printLog(logId, "다음 창고 도착가능 포인트가 존재하지 않습니다.", "S-");
					jrRtn.setField("RTN_CD", "0");
					return jrRtn;
				}
				
				/*******************************
				 * 차량입동지시 
				 ********************************/
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				
// PIDEV
//				jrYdMsg.setField("JMS_TC_CD"           , "YDDMR028"    );
//				jrYdMsg.setField("JMS_TC_CREATE_DDTT"  , commUtils.getDateTime14());
//				jrYdMsg.setField("TC_CODE"		       , "YDDMR028"    );
//				jrYdMsg.setField("TC_CREATE_DDTT"      , commUtils.getDateTime14());
//			    jrYdMsg.setField("TRANS_WORD_DATE"     , sTransOrdDate );
//			    jrYdMsg.setField("TRANS_WORD_SEQNO"    , sTransOrdSeqno);
//			    jrYdMsg.setField("CARD_NO"             , sCardNo       );
//			    jrYdMsg.setField("CAR_NO"              , sCarNo        );
//			    jrYdMsg.setField("WLOC_CD"             , jsNextYd.getRecord(0).getFieldString("WLOC_CD"));
//			    jrYdMsg.setField("YD_PNT_CD"           , jsNextYd.getRecord(0).getFieldString("YD_PNT_CD"));
//				
//			    jrYdMsg.setField("LOAN_PULLOUT_ABLE_YN","Y"            );
//			    jrYdMsg.setField("BAYIN_DDTT"          , commUtils.getDateTime14()); //입동일시
//
//			    jrYdMsg.setField("YD_CARPNT_CD"        , jsNextYd.getRecord(0).getFieldString("YD_CARPNT_CD"));				
					
				jrYdMsg.setField("MQ_TC_CD"		        , "M10YDLMJ1061"			);	
				jrYdMsg.setField("MQ_TC_CREATE_DDTT"    , commUtils.getDateTime14() ); //JMSTC생성일시
				
			    jrYdMsg.setField("TRN_REQ_DATE"         , sTransOrdDate 			); // 운송지시일자
			    jrYdMsg.setField("TRN_REQ_SEQ"    	    , sTransOrdSeqno			); // 운송의뢰순번
			    
			    jrYdMsg.setField("CAR_NO"               , sCarNo        			); // 차량번호
			    jrYdMsg.setField("YD_GP"                , "J"	       				); // 야드구분
			    jrYdMsg.setField("DIST_GOODS_GP"        , "H"	      				); // 출하제품구분
			    
			    jrYdMsg.setField("SCH_YN"               , "N"						); // 스케줄여부
			    jrYdMsg.setField("BAYIN_DDTT"           , commUtils.getDateTime14() ); // 입동일시
			    jrYdMsg.setField("WLOC_CD"              , jsNextYd.getRecord(0).getFieldString("WLOC_CD"));
			    jrYdMsg.setField("YD_PNT_CD"            , jsNextYd.getRecord(0).getFieldString("YD_PNT_CD"));
			    jrYdMsg.setField("LOAN_PULLOUT_ABLE_YN" , "N"            			); // 차입인출가능여부					
			    jrYdMsg.setField("YD_CARPNT_CD"         , jsNextYd.getRecord(0).getFieldString("YD_CARPNT_CD"));
			    
			    jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
			}
			
			
			commUtils.printLog(logId, mthdNm, "S-");

			jrRtn.setField("RTN_CD", "1");
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 
	
}  