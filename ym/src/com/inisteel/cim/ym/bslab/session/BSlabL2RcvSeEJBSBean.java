/**
 * @(#)BSlabL2RcvSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      B열연 SLAB 야드 L2 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bslab.session;

import java.util.ArrayList;
import java.util.List;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.util.YmConstant;
import com.inisteel.cim.ym.bslab.dao.BSlabDAO;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonDB;
import com.inisteel.cim.ym.common.YmCommonUtil;
//import com.inisteel.cim.ym.common.YmCommonConst;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;


/**
 *      [A] 클래스명 : B열연 SLAB 야드 L2수신 처리 
 *  
 * @ejb.bean name="BSlabL2RcvSeEJB" jndi-name="BSlabL2RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/
 
public class BSlabL2RcvSeEJBSBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();
	private BSlabDAO bSlabDao = new BSlabDAO();
	private BSlabComm bSlabComm = new BSlabComm(); 
	private YmComm ymComm = new YmComm();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	/***************************************************************************
	 * 
	 **************************************************************************/
	/**
	 *      [A] 오퍼레이션명 : 고장 및 작업 모드 변경시 Crane Reschedule 처리 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnResch(JDTORecord jrParam) throws DAOException {
		String methodNm = "고장 및 작업 모드 변경시 크레인리스케줄[BSlabL2RcvSeEJB.trtCrnResch] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		try {
			commUtils.printLog(logId, methodNm, "S+");
			jrParam.setResultMsg(methodNm);	//Log Method Name
			JDTORecord jrRtn = null;	//크레인작업지시 전문 Return

			String modifier 	= commUtils.trim(jrParam.getFieldString("MODIFIER")); //수신 전문 I/F ID
			String ydEqpId		= commUtils.trim(jrParam.getFieldString("YD_EQP_ID"));    //설비id
			String sBrGp 		= commUtils.trim(jrParam.getFieldString("BR_GP"));    //고장 복구
			String ydEqpWrkMode2= commUtils.nvl (jrParam.getFieldString("YD_EQP_WRK_MODE2"),"M");  //작업모드 2
			
			JDTORecordSet jsCrnSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			if("R".equals(sBrGp)) {
				//복구,ON_LINE 인 경우
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnReschCrnSchR 
				--크레인리스케줄 크레인스케줄 검색 
				SELECT CS.YD_CRN_SCH_ID
				     , CS.YD_SCH_CD
				     , ST.YD_SCH_PRIOR
				     , ST.YD_EQP_ID
				     , CS.YD_WRK_PROG_STAT
				  FROM TB_YM_CRNSCH   CS 
				     , (SELECT SR.YD_SCH_CD
				             , SR.YD_WRK_CRN_PRIOR  AS YD_SCH_PRIOR
				             , SR.YD_WRK_CRN        AS YD_EQP_ID
				          FROM TB_YM_SCHEDULERULE SR
				         WHERE SR.YD_GP         = SUBSTR(:V_YD_EQP_ID,1,1)
				           AND SR.YD_BAY_GP     = SUBSTR(:V_YD_EQP_ID,2,1)
				           AND (SR.YD_WRK_CRN    = :V_YD_EQP_ID) 
				           AND SR.DEL_YN        = 'N'
				        ) ST
				 WHERE CS.DEL_YN     = 'N'       
				   AND CS.YD_SCH_CD  = ST.YD_SCH_CD 
				   AND CS.YD_WRK_PROG_STAT IN ( 'W' )
				   AND 1 = CASE WHEN SUBSTR(CS.YD_UP_WO_LOC,3,2) = 'TC' OR  SUBSTR(CS.YD_DN_WO_LOC,3,2) = 'TC' THEN
				                CASE WHEN SUBSTR(CS.YD_UP_WO_LOC,5,1) IN ( SELECT SUBSTR(DTL_ITM2,6,1) 
				                                                             FROM TB_YM_RULE
				                                                            WHERE REPR_CD_GP= 'YM2005'
				                                                              AND DTL_ITM1  = ST.YD_EQP_ID ) THEN 1
				                     WHEN SUBSTR(CS.YD_DN_WO_LOC,5,1) IN ( SELECT SUBSTR(DTL_ITM2,6,1)
				                                                             FROM TB_YM_RULE
				                                                            WHERE REPR_CD_GP= 'YM2005'
				                                                              AND DTL_ITM1  = ST.YD_EQP_ID ) THEN 1
				                     ELSE 0 END                                     
				           ELSE 1 END 
				 ORDER BY DECODE(YD_WRK_PROG_STAT,'W',-1,'S',0,YD_WRK_PROG_STAT) DESC                                           
				 */					
				jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnReschCrnSchR", logId, methodNm, "설비별 크레인스케줄 조회");
				JDTORecord    jrCrnSch = JDTORecordFactory.getInstance().create();
				if (jsCrnSch.size() > 0) {
					for(int Loop_i = 1; Loop_i <= jsCrnSch.size(); Loop_i++) {
						jsCrnSch.absolute(Loop_i);  
						jrCrnSch = jsCrnSch.getRecord();
						//크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
						/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCrnReschCrnSch
						--크레인리스케줄 크레인스케줄 수정 
						UPDATE TB_YM_CRNSCH
						   SET MODIFIER      = :V_MODIFIER
						     , MOD_DDTT      = SYSDATE
						     , YD_SCH_PRIOR  = :V_YD_SCH_PRIOR
						     , YD_EQP_ID     = :V_YD_EQP_ID 
						 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						 */	    
						JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
						jrParam1.setField("MODIFIER"     , modifier );
						jrParam1.setField("YD_SCH_PRIOR" , commUtils.trim(jrCrnSch.getFieldString("YD_SCH_PRIOR" )));
						jrParam1.setField("YD_EQP_ID"    , commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"    )));
						jrParam1.setField("YD_CRN_SCH_ID", commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID")));
						
						commDao.update(jrParam1, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCrnReschCrnSch", logId, methodNm, "크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정");
					}
				}			
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name

				String msgId = commUtils.trim(jrParam.getFieldString("MSG_ID")); //수신 전문 I/F ID
				msgId = msgId.substring(0, 2);
				
				jrYdMsg.setField("JMS_TC_CD"         , "A8YML007"); //JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("MODIFIER"          , modifier  ); //수정자
				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId   ); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"       ); //야드작업진행상태
				
				//크레인작업지시요구 전문을 추가
				jrRtn = commUtils.addSndData(jrRtn, this.rcvA8YML007(jrYdMsg));
			} else {
				//고장,OFF_LINE인 경우
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnReschCrnSchB 
				--크레인리스케줄 크레인스케줄 검색(고장) 
				--상대크레인으로 작업 변경
				SELECT CS.YD_CRN_SCH_ID
				     , CS.YD_SCH_CD             -- 상대크레인 스케쥴
				     , CS.YD_WRK_PROG_STAT      -- 작업상태 
				     , ST.ALT_YD_SCH_PRIOR      -- 상대우선순위
				     , ST.ALT_YD_EQP_ID         -- 상대크레인
				     , (SELECT YD_EQP_WRK_MODE2 FROM TB_YM_EQUIP WHERE EQUIP_GP = CS.YD_EQP_ID) AS YD_EQP_AUTO_CRN_MODE

				  FROM TB_YM_CRNSCH   CS 
				     , (SELECT SR.YD_SCH_CD
				             , SR.YD_WRK_CRN_PRIOR  AS ALT_YD_SCH_PRIOR
				             , SR.YD_ALT_CRN        AS ALT_YD_EQP_ID
				          FROM TB_YM_SCHEDULERULE SR
				         WHERE SR.YD_GP         = SUBSTR(:V_YD_EQP_ID,1,1)
				           AND SR.YD_BAY_GP     = SUBSTR(:V_YD_EQP_ID,2,1)
				           AND SR.YD_WRK_CRN    = :V_YD_EQP_ID 
				           AND SR.DEL_YN        = 'N'
				        ) ST
				 WHERE CS.DEL_YN     = 'N'       
				   AND CS.YD_SCH_CD  = ST.YD_SCH_CD 
				   AND CS.YD_WRK_PROG_STAT IN ( 'W', 'S', '1' )
				   AND 1 = CASE WHEN SUBSTR(CS.YD_UP_WO_LOC,3,2) = 'TC' OR  SUBSTR(CS.YD_DN_WO_LOC,3,2) = 'TC' THEN
				                CASE WHEN SUBSTR(CS.YD_UP_WO_LOC,5,1) IN ( SELECT SUBSTR(DTL_ITM2,6,1) 
				                                                             FROM TB_YM_RULE
				                                                            WHERE REPR_CD_GP= 'YM2005'
				                                                              AND DTL_ITM1  = ST.ALT_YD_EQP_ID ) THEN 1
				                     WHEN SUBSTR(CS.YD_DN_WO_LOC,5,1) IN ( SELECT SUBSTR(DTL_ITM2,6,1)
				                                                             FROM TB_YM_RULE
				                                                            WHERE REPR_CD_GP= 'YM2005'
				                                                              AND DTL_ITM1  = ST.ALT_YD_EQP_ID ) THEN 1
				                     ELSE 0 END                                     
				           ELSE 1 END 
				   AND CS.YD_EQP_ID = :V_YD_EQP_ID       
				 ORDER BY DECODE(YD_WRK_PROG_STAT,'W',-1,'S',0,YD_WRK_PROG_STAT) DESC                     				
				 */	
				jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnReschCrnSchB", logId, methodNm, "설비별 크레인스케줄 조회");
				JDTORecord    jrCrnSch = JDTORecordFactory.getInstance().create();
				if (jsCrnSch.size() > 0) {
					for(int Loop_i = 1; Loop_i <= jsCrnSch.size(); Loop_i++) {
						jsCrnSch.absolute(Loop_i);  
						jrCrnSch = jsCrnSch.getRecord();
						ydEqpId = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"    ));
						
						if(Loop_i == 1 && !"A".equals(ydEqpWrkMode2)) {
							//선택된 정보 취소
							if(commUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT")).equals("1")||commUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT")).equals("S")) {
								
								JDTORecord tcRecord = JDTORecordFactory.getInstance().create();
								tcRecord.setField("YD_CRN_SCH_ID"   , commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID")));
								tcRecord.setField("MSG_GP"          , "D");
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L004", tcRecord));
								
							}
						}					
						//크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
						/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCrnReschCrnSch
						--크레인리스케줄 크레인스케줄 수정 
						UPDATE TB_YM_CRNSCH
						   SET MODIFIER      = :V_MODIFIER
						     , MOD_DDTT      = SYSDATE
						     , YD_SCH_PRIOR  = :V_YD_SCH_PRIOR
						     , YD_EQP_ID     = :V_YD_EQP_ID 
						 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						 */	    
						JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
						jrParam1.setField("MODIFIER"     , modifier );
						jrParam1.setField("YD_SCH_PRIOR" , commUtils.trim(jrCrnSch.getFieldString("YD_SCH_PRIOR" )));
						jrParam1.setField("YD_EQP_ID"    , ydEqpId);
						jrParam1.setField("YD_CRN_SCH_ID", commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID")));
						
						commDao.update(jrParam1, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCrnReschCrnSch", logId, methodNm, "크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정");
						
					}
					//상대크레인 작업 상태를 check 하여 작업이 없으면  명령선택 기동
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnReSchByYdEqpId
					SELECT *
					  FROM TB_YM_CRNSCH
					 WHERE DEL_YN = 'N'
					   AND YD_EQP_ID = :V_YD_EQP_ID 
					   AND YD_WRK_PROG_STAT NOT IN ('W')
					*/
					jrParam.setField("YD_EQP_ID"    , ydEqpId);
					JDTORecordSet jsYdEqpId = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnReSchByYdEqpId", logId, methodNm, "설비별 크레인스케줄 조회");
					if (jsYdEqpId.size() == 0) {
						
						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name

						String msgId = commUtils.trim(jrParam.getFieldString("MSG_ID")); //수신 전문 I/F ID
						msgId = msgId.substring(0, 2);
						jrYdMsg.setField("JMS_TC_CD"         , "A8YML007"); //JMSTC코드
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
						jrYdMsg.setField("MODIFIER"          , modifier  ); //수정자
						jrYdMsg.setField("YD_EQP_ID"         , commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"    ))); //야드설비ID
						jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"       ); //야드작업진행상태
						
						//크레인작업지시요구 전문을 추가
						jrRtn = commUtils.addSndData(jrRtn, this.rcvA8YML007(jrYdMsg));
					}		
					
				}
			}	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of Crane Reschedule 처리 
	
	/**
	 *      [A] 오퍼레이션명 : MSLAB,SLAB 공통 Table 저장위치를 UPDATE한다
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean UpdSlabComLoc(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "주편공통,SLAB공통 update[BSlabL2RcvSeEJB.UpdSlabComLoc] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		String sSLAB_GP = "";
		//V_YD_LOC     -- 현 저장위치코드   
		//V_STOCK_ID
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID

			//SLAB번호로 VW_YD_SLABCOMM에서 SLAB(S)인지 주편(M)인지 판단할 수 있는 SLAB_GP 를 구한다.
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSLAB_GP 
			SELECT SLAB_GP
			  FROM VW_YD_SLABCOMM
			 WHERE SLAB_NO = :V_STOCK_ID*/
			JDTORecordSet rsResult = commDao.select(rcvMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSLAB_GP", logId, methodNm, "SLAB_GP 구하기");

			if (rsResult == null || rsResult.size() == 0) {
				return false;
			} else  {
				sSLAB_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("SLAB_GP"));
			}
			
			if("M".equals(sSLAB_GP)) {
				
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updMSlabCommLocInfo
				UPDATE TB_PT_MSLABCOMM
				   SET (  
				         YD_GP,                  -- 야드구분
				         YD_BAY_GP,              -- 동
				         YD_EQP_GP,              -- SPAN
				         YD_STK_COL_NO,          -- 적치열번지
				         YD_STK_BED_NO,          -- 적치번지
				         YD_STK_LYR_NO,          -- 적치단
				         YD_STR_LOC,             -- 현 저장위치코드
				         YD_STR_LOC_HIS1,         -- 전 저장위치코드
				         YD_STR_LOC_HIS2         -- 전전 저장위치코드
				       ) =
				       (
				        SELECT 
				            substr(:V_YD_LOC,1,1),-- 야드구분
				            substr(:V_YD_LOC,2,1),-- 동
				            substr(:V_YD_LOC,3,2),-- SPAN
				            substr(:V_YD_LOC,5,2),-- 적치열번지
				            substr(:V_YD_LOC,7,2),-- 적치번지
				            substr(:V_YD_LOC,9,2),-- 적치단
				            :V_YD_LOC,                       -- 현 저장위치코드   
				            YD_STR_LOC,                 -- 전현 저장위치코드
				            YD_STR_LOC_HIS1             -- 전전현 저장위치코드
				        FROM TB_PT_MSLABCOMM
				        WHERE MSLAB_NO = :V_STOCK_ID
				       )
				WHERE MSLAB_NO = :V_STOCK_ID */
				commDao.update(rcvMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updMSlabCommLocInfo", logId, methodNm, "주편공통 수정");
				
//			} else if("S".equals(sSLAB_GP)) {
			} else {
				
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSlabCommLocInfo 
				UPDATE TB_PT_SLABCOMM
				   SET (  
				         YD_GP,                  -- 야드구분
				         YD_BAY_GP,              -- 동
				         YD_EQP_GP,              -- SPAN
				         YD_STK_COL_NO,          -- 적치열번지
				         YD_STK_BED_NO,          -- 적치번지
				         YD_STK_LYR_NO,          -- 적치단
				         YD_STR_LOC,             -- 현 저장위치코드
				         YD_STR_LOC_HIS1,         -- 전 저장위치코드
				         YD_STR_LOC_HIS2         -- 전전 저장위치코드
				       ) =
				       (
				        SELECT 
				            substr(:V_YD_LOC,1,1),-- 야드구분
				            substr(:V_YD_LOC,2,1),-- 동
				            substr(:V_YD_LOC,3,2),-- SPAN
				            substr(:V_YD_LOC,5,2),-- 적치열번지
				            substr(:V_YD_LOC,7,2),-- 적치번지
				            substr(:V_YD_LOC,9,2),-- 적치단
				            :V_YD_LOC,                       -- 현 저장위치코드   
				            YD_STR_LOC,                 -- 전현 저장위치코드
				            YD_STR_LOC_HIS1             -- 전전현 저장위치코드
				        FROM TB_PT_SLABCOMM
				        WHERE SLAB_NO = :V_STOCK_ID
				       )
				WHERE SLAB_NO = :V_STOCK_ID */
				commDao.update(rcvMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSlabCommLocInfo", logId, methodNm, "SLAB공통 수정");
			}
			
        	
			commUtils.printLog(logId, methodNm, "S-");

		} catch(Exception e) {
			
		}
		return true;
	} // end of MSLAB,SLAB 공통 Table 저장위치를 UPDATE한다
	
	/**
	 *      [A] 오퍼레이션명 : SLAB 공통 Table 저장위치를 UPDATE (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean updSlabCommLocInfoTx(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "SLAB공통 저장위치 update[BSlabL2RcvSeEJB.updSlabCommLocInfoTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			// YD_LOC : 야드구분(1)+동(1)+SPAN(1)+적치열(2)+Bed(2)+적치단(2)
			// STOCK_ID : SLAB번호 or 주편번호
		
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSlabCommLocInfo 
			UPDATE TB_PT_SLABCOMM
			   SET (  
			         YD_GP,                  -- 야드구분
			         YD_BAY_GP,              -- 동
			         YD_EQP_GP,              -- SPAN
			         YD_STK_COL_NO,          -- 적치열번지
			         YD_STK_BED_NO,          -- 적치번지
			         YD_STK_LYR_NO,          -- 적치단
			         YD_STR_LOC,             -- 현 저장위치코드
			         YD_STR_LOC_HIS1,         -- 전 저장위치코드
			         YD_STR_LOC_HIS2         -- 전전 저장위치코드
			       ) =
			       (
			        SELECT 
			            substr(:V_YD_LOC,1,1),-- 야드구분
			            substr(:V_YD_LOC,2,1),-- 동
			            substr(:V_YD_LOC,3,2),-- SPAN
			            substr(:V_YD_LOC,5,2),-- 적치열번지
			            substr(:V_YD_LOC,7,2),-- 적치번지
			            substr(:V_YD_LOC,9,2),-- 적치단
			            :V_YD_LOC,                       -- 현 저장위치코드   
			            YD_STR_LOC,                 -- 전현 저장위치코드
			            YD_STR_LOC_HIS1             -- 전전현 저장위치코드
			        FROM TB_PT_SLABCOMM
			        WHERE SLAB_NO = :V_STOCK_ID
			       )
			WHERE SLAB_NO = :V_STOCK_ID */
			commDao.update(rcvMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSlabCommLocInfo", logId, methodNm, "SLAB공통 저장위치 수정");
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	} // end of SLAB 공통 Table 저장위치를 UPDATE (Transaction 분리)

	/**
	 *      [A] 오퍼레이션명 : SLAB 공통 Table 진도코드를 UPDATE (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean updSlabCommCurrProgCdTx(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "SLAB공통 진도코드 update[BSlabL2RcvSeEJB.updSlabCommCurrProgCdTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			// CURR_PROG_CD : 진도코드
			// SLAB_NO : SLAB번호 or 주편번호
		
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateMatlFtmvWlrstSlabNEW 
			UPDATE TB_PT_SLABCOMM
			   SET FNL_REG_PGM = 'ydCurrProgcdS'
			      ,CURR_PROG_CD_REG_PGM = 'ydCurrProgcdS'
			      ,CURR_PROG_REG_DDTT = SYSDATE
			      ,CURR_PROG_CD = :V_CURR_PROG_CD
			      ,BEFO_PROG_CD_REG_PGM = CURR_PROG_CD_REG_PGM
			      ,BEFO_PROG_REG_DDTT = CURR_PROG_REG_DDTT
			      ,BEFO_PROG_CD = CURR_PROG_CD
			      ,BEFOBEFO_PROG_CD_REG_PGM = BEFO_PROG_CD_REG_PGM
			      ,BEFOBEFO_PROG_REG_DDTT = BEFO_PROG_REG_DDTT
			      ,BEFOBEFO_PROG_CD = BEFO_PROG_CD
			      ,MODIFIER = 'SYSTEM'
			      ,MOD_DDTT = SYSDATE
			      ,MATL_TKOV_DT = SYSDATE
			 WHERE SLAB_NO = :V_SLAB_NO    */
			commDao.update(rcvMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateMatlFtmvWlrstSlabNEW", logId, methodNm, "SLAB공통 진도코드 수정");
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	} // end of SLAB 공통 Table 저장위치를 UPDATE (Transaction 분리)
	
	/**
	 *      [A] 오퍼레이션명 : MSLAB공통 Table 저장위치를 UPDATE (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean updMSlabCommLocInfoTx(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "주편공통 저장위치 update[BSlabL2RcvSeEJB.updMSlabCommLocInfoTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			// YD_LOC : 야드구분(1)+동(1)+SPAN(1)+적치열(2)+Bed(2)+적치단(2)
			// STOCK_ID : SLAB번호 or 주편번호
			
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updMSlabCommLocInfo
			UPDATE TB_PT_MSLABCOMM
			   SET (  
			         YD_GP,                  -- 야드구분
			         YD_BAY_GP,              -- 동
			         YD_EQP_GP,              -- SPAN
			         YD_STK_COL_NO,          -- 적치열번지
			         YD_STK_BED_NO,          -- 적치번지
			         YD_STK_LYR_NO,          -- 적치단
			         YD_STR_LOC,             -- 현 저장위치코드
			         YD_STR_LOC_HIS1,         -- 전 저장위치코드
			         YD_STR_LOC_HIS2         -- 전전 저장위치코드
			       ) =
			       (
			        SELECT 
			            substr(:V_YD_LOC,1,1),-- 야드구분
			            substr(:V_YD_LOC,2,1),-- 동
			            substr(:V_YD_LOC,3,2),-- SPAN
			            substr(:V_YD_LOC,5,2),-- 적치열번지
			            substr(:V_YD_LOC,7,2),-- 적치번지
			            substr(:V_YD_LOC,9,2),-- 적치단
			            :V_YD_LOC,                       -- 현 저장위치코드   
			            YD_STR_LOC,                 -- 전현 저장위치코드
			            YD_STR_LOC_HIS1             -- 전전현 저장위치코드
			        FROM TB_PT_MSLABCOMM
			        WHERE MSLAB_NO = :V_STOCK_ID
			       )
			WHERE MSLAB_NO = :V_STOCK_ID */
			commDao.update(rcvMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updMSlabCommLocInfo", logId, methodNm, "주편공통 저장위치 수정");
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	} // end of MSLAB공통 Table 저장위치를 UPDATE (Transaction 분리)

	/**
	 *      [A] 오퍼레이션명 : MSLAB공통 Table 진도코드를 UPDATE (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean updMSlabCommCurrProgCdTx(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "주편공통 진도코드 update[BSlabL2RcvSeEJB.updMSlabCommCurrProgCdTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			// CURR_PROG_CD : 진도코드
			// MSLAB_NO : SLAB번호 or 주편번호
			
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateMatlFtmvWlrstMSlabNEW
			UPDATE TB_PT_MSLABCOMM
			SET FNL_REG_PGM = 'ydCurrProgcd',
			    CURR_PROG_CD_REG_PGM ='ydCurrProgcd',   -- 현재진도코드 PGM
			    CURR_PROG_REG_DDTT =sysdate,     -- 현재진도코드등록일시       
			    CURR_PROG_CD = :V_CURR_PROG_CD ,
			    BEFO_PROG_CD_REG_PGM=CURR_PROG_CD_REG_PGM,   -- 전 진도코드 PGM
			    BEFO_PROG_REG_DDTT= CURR_PROG_REG_DDTT,     -- 전 진도코드등록일시
			    BEFO_PROG_CD =CURR_PROG_CD,           -- 전 진도코드
			    BEFOBEFO_PROG_CD_REG_PGM =BEFO_PROG_CD_REG_PGM ,
			    BEFOBEFO_PROG_REG_DDTT =BEFO_PROG_REG_DDTT ,
			    BEFOBEFO_PROG_CD = BEFO_PROG_CD,
			    MODIFIER ='SYSTEM',               -- 수정자
			    MOD_DDTT =sysdate,
			    MATL_TKOV_DT =sysdate
			WHERE MSLAB_NO =:V_MSLAB_NO  */
			commDao.update(rcvMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateMatlFtmvWlrstMSlabNEW", logId, methodNm, "주편공통 진도코드 수정");
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	} // end of MSLAB공통 Table 진도코드를 UPDATE (Transaction 분리)
	
	
	/**
	 *      [A] 오퍼레이션명 : MSLAB공통 Table 보온뱅크(BK)추출시간  UPDATE (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean updMSlabCommBkTimeEndTx(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "주편공통 보온뱅크(BK)추출시간 update[BSlabL2RcvSeEJB.updMSlabCommBkTimeEndTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			// YD_CRN_SCH_ID : Crane 스케줄ID
			
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateMslabCommonSubEndInfo
			UPDATE TB_PT_MSLABCOMMSUB A
			 SET  BK_STK_END_TIME=nvl(BK_STK_END_TIME,SYSDATE)
			     , CCSLAB_CL_MTD_GP=(CASE WHEN CCSLAB_CL_MTD_GP ='C' AND (SYSDATE - A.BK_STK_START_TIME) * 24 < 96 AND (SYSDATE - A.BK_STK_START_TIME) * 24 >= 72 THEN 'G'
			                              WHEN CCSLAB_CL_MTD_GP ='D' AND (SYSDATE - A.BK_STK_START_TIME) * 24 < 72 AND (SYSDATE - A.BK_STK_START_TIME) * 24 >= 48 THEN 'G' 
			                              WHEN CCSLAB_CL_MTD_GP ='E' AND (SYSDATE - A.BK_STK_START_TIME) * 24 < 48 AND (SYSDATE - A.BK_STK_START_TIME) * 24 >= 24 THEN 'H'
			                              WHEN CCSLAB_CL_MTD_GP ='F' AND (SYSDATE - A.BK_STK_START_TIME) * 24 < 24 AND (SYSDATE - A.BK_STK_START_TIME) * 24 >= 0 THEN 'I'
			                              WHEN CCSLAB_CL_MTD_GP IN ('C', 'D', 'E', 'F') AND (SYSDATE - A.BK_STK_START_TIME) * 24 >= 96 THEN 'C'
			                              WHEN CCSLAB_CL_MTD_GP IN ('C', 'D', 'E', 'F') AND (SYSDATE - A.BK_STK_START_TIME) * 24 >= 72 THEN 'D'
			                              WHEN CCSLAB_CL_MTD_GP IN ('C', 'D', 'E', 'F') AND (SYSDATE - A.BK_STK_START_TIME) * 24 >= 48 THEN 'E'
			                              WHEN CCSLAB_CL_MTD_GP IN ('C', 'D', 'E', 'F') AND (SYSDATE - A.BK_STK_START_TIME) * 24 >= 24 THEN 'F'                              
			                         ELSE CCSLAB_CL_MTD_GP
			                         END) 
			WHERE MSLAB_NO IN (
			   SELECT STOCK_ID FROM TB_YM_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			) */
			commDao.update(rcvMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateMslabCommonSubEndInfo", logId, methodNm, "주편공통 보온뱅크(BK)추출시간 수정");
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	} // end of MSLAB공통 Table 보온뱅크(BK)추출시간  UPDATE (Transaction 분리)
	
	/**
	 *      [A] 오퍼레이션명 : MSLAB공통 Table 보온뱅크(BK)장입시간  UPDATE (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean updMSlabCommBkTimeStartTx(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "주편공통 보온뱅크(BK)장입시간 update[BSlabL2RcvSeEJB.updMSlabCommBkTimeStartTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			// YD_CRN_SCH_ID : Crane 스케줄ID
			
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateMslabCommonSubInfo
			UPDATE TB_PT_MSLABCOMMSUB A
			 SET BK_STK_YN='Y'
			   , BK_STK_START_TIME=nvl(BK_STK_START_TIME,SYSDATE)
			   , CCSLAB_CL_MTD_GP =(CASE WHEN CCSLAB_CL_MTD_GP IN ('G', 'H', 'I', 'J','N') THEN 'F' 
			                        ELSE CCSLAB_CL_MTD_GP
			                        END)
			WHERE MSLAB_NO IN (
			    SELECT STOCK_ID FROM TB_YM_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			) */
			commDao.update(rcvMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateMslabCommonSubInfo", logId, methodNm, "주편공통 보온뱅크(BK)장입시간 수정");
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	} // end of MSLAB공통 Table 보온뱅크(BK)장입시간  UPDATE (Transaction 분리)


	/**
	 *      [A] 오퍼레이션명 : SLAB공통 Table 보온뱅크(BK)장입시간  UPDATE (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean updSlabCommBkTimeStartTx(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "SLAB공통 보온뱅크(BK)장입시간 update[BSlabL2RcvSeEJB.updSlabCommBkTimeStartTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			// YD_CRN_SCH_ID : Crane 스케줄ID
			
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateSlabCommonSubInfo 
			UPDATE TB_PT_SLABCOMMSUB
			 SET BK_STK_YN='Y' 
			WHERE SLAB_NO IN (
			    SELECT STOCK_ID FROM TB_YM_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			) */
			commDao.update(rcvMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateSlabCommonSubInfo", logId, methodNm, "SLAB공통 보온뱅크(BK)장입시간 수정");
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	} // end of SLAB공통 Table 보온뱅크(BK)장입시간  UPDATE (Transaction 분리)
	
	/**
	 *      [A] 오퍼레이션명 : 작업예약 INSERT (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean insWrkBookTx(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "작업예약 INSERT (Transaction 분리) [BSlabL2RcvSeEJB.insWrkBookTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			commDao.insert(rcvMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insWrkBook", logId, methodNm, "작업예약 INSERT (Transaction 분리)");
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	} 

	/**
	 *      [A] 오퍼레이션명 : 작업예약재료 INSERT (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean insWrkBookMtlTx(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "작업예약재료 INSERT (Transaction 분리) [BSlabL2RcvSeEJB.insWrkBookMtlTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			commDao.insert(rcvMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료 INSERT (Transaction 분리)");
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	} 
		
	/**
	 *      [A] 오퍼레이션명 : 적치단(TB_YM_STACKLAYER) INSERT (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean insStackLayerTx(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "적치단(TB_YM_STACKLAYER) (Transaction 분리) [BSlabL2RcvSeEJB.insStackLayerTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			commDao.insert(rcvMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.setStackLayer", logId, methodNm, "적치단(TB_YM_STACKLAYER) INSERT (Transaction 분리)");
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	} 	
	
	/**
	 *      [A] 오퍼레이션명 : INSERT,UPDATE (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean execQueryIdTx(JDTORecord rcvMsg, String queryId) throws DAOException {
		String methodNm = "Transaction 분리 수행 [BSlabL2RcvSeEJB.execQueryIdTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			commDao.update(rcvMsg, queryId, logId, methodNm, "Transaction 분리 수행");
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}     	
	
	/**
	 *      [A] 오퍼레이션명 : 슬라브  Line Off Request 정보 수신(CF1PB11)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCF1PB11(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "슬라브  Line Off Request 정보 수신[BSlabL2RcvSeEJB.rcvCF1PB11] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = null;	//전문 Return
		
		try{
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sSlabNo      = commUtils.trim(rcvMsg.getFieldString("SLABNo"));
			String sShift       = commUtils.trim(rcvMsg.getFieldString("Shift" )); // 근
			String sGroup       = commUtils.trim(rcvMsg.getFieldString("Group")); // 조	
			String sSlabID      = commUtils.trim(rcvMsg.getFieldString("SLAB구분"));
			String sDestChange  = commUtils.trim(rcvMsg.getFieldString("DestinationChange"));
			String sLocation  	= commUtils.trim(rcvMsg.getFieldString("Location"));
			String sPosition 	= commUtils.trim(rcvMsg.getFieldString("Position"));
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			/**********************************************************
			* 1. 수신한 Slab를 해당위치와, STACK_LAYER_STAT 상태를  'C' Update 한다. (적치중)
			**********************************************************/
			String sHZ_COL_GP   = "";
			String sHZ_BED_GP   = "";
			String sHZ_LAYER_GP = YmConstant.STACK_LAYER_GP_01;
			
			if (sLocation.equals(YmConstant.LOCATION_1)){
				sHZ_COL_GP = YmConstant.STACK_COL_GP_2AHB01;
			}else if (sLocation.equals(YmConstant.LOCATION_2)){
				sHZ_COL_GP = YmConstant.STACK_COL_GP_2BHB02;
			}else if (sLocation.equals(YmConstant.LOCATION_3)){
				sHZ_COL_GP = YmConstant.STACK_COL_GP_2CHB03;
			}else if (sLocation.equals(YmConstant.LOCATION_4)){
				sHZ_COL_GP = YmConstant.STACK_COL_GP_2ACT02;
			}	
			
			if (sPosition.equals("0") ){
				sHZ_BED_GP = YmConstant.STACK_BED_GP_01;
			}else{
				sHZ_BED_GP = "0" + sPosition;
			}

			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STOCK_ID"	        , sSlabNo); //저장품ID
			jrParam.setField("STACK_LAYER_STAT"	, YmConstant.STACK_LAYER_STAT_C); //LAYER 상태 적치중
			jrParam.setField("MODIFIER"	        , modifier); //modifier
			jrParam.setField("STACK_COL_GP"	    , sHZ_COL_GP); //열
			jrParam.setField("STACK_BED_GP"	    , sHZ_BED_GP); //배드
			jrParam.setField("STACK_LAYER_GP"	, sHZ_LAYER_GP); //단
			

			 /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateHZStackLayer  
				 UPDATE TB_YM_STACKLAYER 
				    SET  STOCK_ID         = :V_STOCK_ID
				       , STACK_LAYER_STAT = :V_STACK_LAYER_STAT
				       , MODIFIER         = :V_MODIFIER
				       , MOD_DDTT         = SYSDATE				       
				 WHERE STACK_COL_GP       = :V_STACK_COL_GP
				     AND STACK_BED_GP     = :V_STACK_BED_GP
				     AND STACK_LAYER_GP   = :V_STACK_LAYER_GP
			*/
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateHZStackLayer", logId, methodNm, "적치단 상태업데이트");
	
			
			/**********************************************************
			* 2. 수신한 SLABNo가 저장품(TB_YM_STOCK) Table 에 존재 하는지 점검
			* 저장품 Table에 작업예약_ID가 존재한다면 Error
			**********************************************************/		

			/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectStockIdNoDel  
			SELECT A.STOCK_ID
			     , A.WBOOK_ID
			     , A.STOCK_MOVE_TERM 
			     , B.YD_WBOOK_ID
			  FROM TB_YM_STOCK A
			      ,TB_YM_WRKBOOKMTL B
			 WHERE A.STOCK_ID = :V_STOCK_ID
			   AND A.STOCK_ID = B.STOCK_ID(+)
			   AND A.DEL_YN    = 'N'
			   AND B.DEL_YN(+) = 'N' 
			*/ 
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectStockIdNoDel", logId, methodNm, "작업예약 등록여부");

			if (jsChk == null || jsChk.size() <= 0) {
				throw new Exception("TB_YM_STOCK(YM_저장품) 에 존재하지 않는 SLAB_NO 입니다!");
			} else {
				String sWBOOK_ID = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_WBOOK_ID"));
				if(!"".equals(sWBOOK_ID)) {
					throw new Exception("작업예약 ID:"+sWBOOK_ID+" 가 등록된 SLAB_NO"+sSlabNo+"에 존재합니다!");
				}
			}		
			
			/**********************************************************
			* 3. 수신한 SLABNo가 적치단(TB_YM_STACKLAYER) 테이블에 존재하는지 점검 
			* 존재하지 않는다면  Error
			**********************************************************/					
			/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectStackColGp 
			-- STACK_COL_GP(적치열:첫번째자리 야드구분, 두번째 자리 동구분), STACK_LAYER_STAT(적치단 상태 C:적치중)
			SELECT  STACK_COL_GP
			      , SUBSTR(STACK_COL_GP,1,1) STACK_COL_GP1
			      , SUBSTR(STACK_COL_GP,2,1) STACK_COL_GP2
			      , STACK_BED_GP
			      , STACK_LAYER_STAT
			      , STACK_LAYER_GP
			      , (CASE            
			            WHEN (--정정 실적 처리된 항목 중 최근 실적이 재작업인 경우 추출.
			             SELECT COUNT(*)
			              FROM TB_HR_C_SHEARWOWR SR
			             WHERE  SR.STEP_NO = (SELECT /*+ INDEX_DESC(A PK_HR_C_SHEARWOWR)*/
			/*                                         MAX(STEP_NO)
			                                   FROM TB_HR_C_SHEARWOWR A
			                                  WHERE COIL_NO = SR.COIL_NO
			                                   AND  WORK_STAT = '*'
			                                   AND ROWNUM<=1 
			                                  )
			              AND  SR.COIL_NO=A.STOCK_ID  
			              AND  SR.RECEIPT_HOLD_SCRAP_CAUSE_GP IN('J','Q')
			              )=1 THEN 'J'   
			               WHEN (SELECT COUNT(*) FROM TB_PT_COILCOMM B
			                    WHERE B.COIL_NO=A.STOCK_ID
			                     AND ((B.REMAIN_PROC1='1K' AND B.REMAIN_PROC2='1Q')
			                         OR B.REMAIN_PROC1='1Q')
			                    --AND B.COIL_T>2.9
			                     )=1
			              THEN 'Q'
			              ELSE 'X' END) AS CHK
			  FROM TB_YM_STACKLAYER  A
			WHERE STOCK_ID = :V_STOCK_ID			
			*/

			JDTORecordSet jsChk1 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectStackColGp", logId, methodNm, "단정보등록여부");

			if (jsChk1 == null || jsChk1.size() <= 0) {
				throw new Exception("등록된 SLAB_NO:"+sSlabNo+"가 적치단(TB_YM_STACKLAYER) Table에 존재하지 않습니다. Error");
			}  
			
			String stackCol  = commUtils.trim(jsChk1.getRecord(0).getFieldString("STACK_COL_GP"));
			String stackBed = commUtils.trim(jsChk1.getRecord(0).getFieldString("STACK_BED_GP"));
			String stackBay = commUtils.trim(jsChk1.getRecord(0).getFieldString("STACK_COL_GP2"));
			String stackLayer = commUtils.trim(jsChk1.getRecord(0).getFieldString("STACK_LAYER_GP"));
			String stackStat = commUtils.trim(jsChk1.getRecord(0).getFieldString("STACK_LAYER_STAT"));


			
			/*************************************** 
			 * 	4.작업예약이 생성 
			 **************************************/					
  			//작업예약,작업재료 등록		
 
			jrParam.setField("STL_NO"           , sSlabNo); //재료번호
			jrParam.setField("STACK_COL_GP"     , stackCol); 
			jrParam.setField("STACK_BED_GP"     , stackBed); 
			jrParam.setField("STACK_LAYER_GP"   , stackLayer); 
			String ydSchCd = stackBay.equals("A") ? YmConstant.YD_SCH_CD_2AHB01LM : YmConstant.YD_SCH_CD_2CHB01LM ;
			jrParam.setField("YD_SCH_CD"        ,  ydSchCd );// Slab H/B Line Off
			jrParam.setField("MODIFIER"         , modifier); //수정자
//			jrOutTemp.setField("YD_TO_LOC_GUIDE"  , toLocGuide    ); //TO위치가이드
			
			String ydWbookId = ymComm.procWkBookInsert(jrParam);
			
			if(ydWbookId.equals(YmConstant.RETN_CD_FAILURE)){
				throw new Exception("작업예약ID 생성 실패"); 				
			}			
			
		
			/*************************************** 
			 * 	5.저장품 Table(TB_YM_STOCK)에 WBOOK_ID및 이동조건을 Update 한다
			 **************************************/	
    		
    		JDTORecord jrRtnProg = ymComm.getSlabCurrProgCd(jrParam);
    		String sStockMoveTerm = commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"));
    		
			jrParam.setField("WBOOK_ID"     , ydWbookId); 
			jrParam.setField("STOCK_MOVE_TERM"     , sStockMoveTerm); 
 
			/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateYdStockStockId
    		-- 저장품 Table(TB_YM_STOCK)에 STOCK_MOVE_TERM 를 Update 한다.
    		UPDATE TB_YM_STOCK 
    		   SET STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
			     , MODIFIER         = :V_MODIFIER
			     , MOD_DDTT         = SYSDATE		    		     
    		 WHERE STOCK_ID = :V_STOCK_ID    	
	    	*/

			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateYdStockStockId", logId, methodNm, " 저장품TB_YM_STOCK  STOCK_MOVE_TERM를 Update ");
			

			/**********************************************************
			* 6.크레인스케줄 전문 호출
			**********************************************************/
			jrParam.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			jrParam.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드

				
			//Slab Schedule EJB Call
			EJBConnector ejbConn = new EJBConnector("default","BSlabSchSeEJB",this);
			jrRtn = (JDTORecord)ejbConn.trx("procYMYMJ202",new Class[]{JDTORecord.class},new Object[]{ jrParam });			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 슬라브  Line Off Request 정보 수신(CF1PB11)
	
	/**
	 *      [A] 오퍼레이션명 : #4 CTC Slab Loading Result(CF1PB16)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCF1PB16(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "#4 CTC Slab Loading Result[BSlabL2RcvSeEJB.rcvCF1PB16] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = null;	//전문 Return
		
		try{
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sSlabNo      = commUtils.trim(rcvMsg.getFieldString("SLAB_NO"));


			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			/**********************************************************
			* 1. 수신한 SlabNo로  슬라브 공통 정보를 읽어  두께 폭을  가져온다.
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name	
			jrParam.setField("MODIFIER"         , modifier); //수정자
			jrParam.setField("SLAB_NO"      , sSlabNo); //재료번호
		    /*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectSlabInfo
	          WITH TEMP AS (
	              SELECT :V_SLAB_NO AS SLAB_NO
	              FROM    DUAL
	          )
	          SELECT   C.SLAB_NO                       --SLAB번호
	                  ,C.SLAB_NO AS STL_NO             --SLAB번호
	                  ,TO_CHAR(E.REFUR_CHG_ABLE_DT, 'YYYYMMDDHH24MISS') AS MILL_PLAN_DDTT   
	          	 	  ,C.STL_APPEAR_GP
	          		  ,C.CURR_PROG_CD
	                  ,E.REFUR_CHG_LOT_NO AS LOT_NO    -- 가열로장입Lot번호
	                  ,E.LOT_IN_SLAB_PRIOR             -- Lot내Slab순위
	                  ,E.YD_CHG_NO                     -- 야드장입순위
	                  ,C.ORD_YEOJAE_GP                 --주문여재구분
	                  ,C.ORD_NO
	                  ,C.ORD_DTL
	                  ,C.SLAB_WO_RT_CD
	                  ,C.ORD_HCR_GP
	                  ,C.HCR_GP
	                  ,C.SCARFING_YN      
	                  ,NVL(C.ORD_NO, '') || NVL(C.ORD_DTL, '') AS PRODUC_NO --제작번호행번
	                  ,C.SLAB_T                        --두께
	                  ,C.SLAB_W                        --폭
	                  ,C.SLAB_LEN                      --길이
	                  ,C.SLAB_WT                       --중량
	                  ,C.COIL_NO                       --예정COILNO
	                  ,C.STACK_LOT_NO AS STACK_LOT_CD  --산적 LOT CODE
	                  ,D.BUY_SLAB_NO                   --구입슬라브번호
	                  ,A.STOCK_ID
	                  ,A.WBOOK_ID
	                  ,A.STACK_LOT_NO AS STACK_LOT
	                  ,A.STOCK_MOVE_TERM
	                  ,B.STOCK_ID  AS LAYER_STOCK_ID
	          FROM    TEMP
	                  ,TB_YM_STOCK         A
	                  ,TB_YM_STACKLAYER    B
	                  ,VW_YD_SLABCOMM      C
	                  ,TB_QM_BUYSLABINFO   D
	                  ,TB_CT_L_HRMILLWO    E
	          WHERE   TEMP.SLAB_NO = C.SLAB_NO(+)
	          AND     TEMP.SLAB_NO = D.MSLAB_NO(+)
	          AND     TEMP.SLAB_NO = A.STOCK_ID(+)
	          AND     TEMP.SLAB_NO = B.STOCK_ID(+)
	          AND     TEMP.SLAB_NO = E.STL_NO(+) 
			*/ 
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectSlabInfo", logId, methodNm, "SLAB공통정보");
	
			String sSlab_WT = commUtils.nvl(jsChk.getRecord(0).getFieldString("SLAB_WT"),"0");
			String sSlab_T  = commUtils.nvl(jsChk.getRecord(0).getFieldString("SLAB_T"),"0");

			/**********************************************************
			* 2. 단정보를 조회 (설비 W/B) 배드와 단정보를 가져온다 
			**********************************************************/
			jrParam.setField("STOCK_ID"         , sSlabNo);  
			jrParam.setField("STACK_COL_GP"      , YmConstant.STACK_COL_GP_2CWB01); //적치열(2CWB01)
			
			/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectWBSTACKLAYER 
			SELECT STACK_BED_GP
			      ,STACK_LAYER_GP 
			 FROM TB_YM_STACKLAYER
			WHERE STOCK_ID       = :V_STOCK_ID
			  AND STACK_COL_GP   = :V_STACK_COL_GP
			*/ 
			JDTORecordSet jsChk1 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectWBSTACKLAYER", logId, methodNm, "단정보조회 기준적치열(2CWB01)");
	
			if (jsChk1 == null || jsChk1.size() <= 0) {
				commUtils.printLog(logId, methodNm +"W/B에 Slab: "+sSlabNo +" 가 존재하지 않습니다.", "SL");
				return jrRtn;
			}  
			
			String sTmpBedGp    = commUtils.trim(jsChk1.getRecord(0).getFieldString("STACK_BED_GP"));
			String sTmpLayerGp  = commUtils.trim(jsChk1.getRecord(0).getFieldString("STACK_LAYER_GP"));		
			
			
			/**********************************************************
			* 3. 배드정보를 조회 (설비 W/B) 적치대 가용량 두께 수량  정보를 가져온다.
			**********************************************************/
			jrParam.setField("STACK_BED_GP"      , sTmpBedGp); //적치대
			
			/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectWBSTACKER 
			SELECT STACK_BED_QNTY_MAX
			      ,STACK_BED_WT_MAX
			      ,STACK_BED_HIGH_MAX
			      ,STACK_BED_QNTY_CURR
			      ,STACK_BED_WT_CURR
			      ,STACK_BED_HIGH_CURR
			      ,STACK_BED_ABLE_QNTY
			      ,STACK_BED_ABLE_WT
			      ,STACK_BED_ABLE_HIGH
			  FROM TB_YM_STACKER
			 WHERE STACK_COL_GP   = :V_STACK_COL_GP
			   AND STACK_BED_GP   = :V_STACK_BED_GP
			*/ 
			
			JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectWBSTACKER", logId, methodNm, "배드정보조회 기준적치열(2CWB01)");
	

			
			String sTmpQntyMax    = commUtils.nvl(jsChk2.getRecord(0).getFieldString("STACK_BED_QNTY_MAX"),"0");
			String sTmpWtMax      = commUtils.nvl(jsChk2.getRecord(0).getFieldString("STACK_BED_WT_MAX"),"0");
			String sTmpHighMax    = commUtils.nvl(jsChk2.getRecord(0).getFieldString("STACK_BED_HIGH_MAX"),"0");
			String sTmpQntyCurr   = commUtils.nvl(jsChk2.getRecord(0).getFieldString("STACK_BED_QNTY_CURR"),"0");
			String sTmpWtCurr     = commUtils.nvl(jsChk2.getRecord(0).getFieldString("STACK_BED_WT_CURR"),"0");
			String sTmpHighCurr   = commUtils.nvl(jsChk2.getRecord(0).getFieldString("STACK_BED_HIGH_CURR"),"0");
			String sTmpAbleQnty   = commUtils.nvl(jsChk2.getRecord(0).getFieldString("STACK_BED_ABLE_QNTY"),"0");
			String sTmpAbleWt     = commUtils.nvl(jsChk2.getRecord(0).getFieldString("STACK_BED_ABLE_WT"),"0");
			String sTmpAbleHigh   = commUtils.nvl(jsChk2.getRecord(0).getFieldString("STACK_BED_ABLE_HIGH"),"0");
		
			
			/**********************************************************
			* 4. 배드정보의  중량 두께와	 슬라브 공통의  중량 두께 비교	차이분을 구한다
			**********************************************************/			
			/*  
			 *	STACK_BED_QNTY_CURR	=STACK_BED_QNTY_CURR-1, 
			 *	STACK_BED_WT_CURR	-(Slab 공통 중량), 
			 *	STACK_BED_HIGH_CURR	-(Slab 공통 두께)
			 */
			int intTmpQntyCurr = 0;
			int intTmpWtCurr   = 0;
			int intTmpHighCurr = 0;
			
			intTmpQntyCurr = Integer.parseInt(sTmpQntyCurr);
			intTmpWtCurr   = Integer.parseInt(sTmpWtCurr); 
			intTmpHighCurr = Integer.parseInt(sTmpHighCurr);
			
			intTmpQntyCurr = intTmpQntyCurr - 1; 
			intTmpWtCurr   = intTmpWtCurr   - Integer.parseInt(sSlab_WT);
			intTmpHighCurr = intTmpHighCurr - Integer.parseInt(sSlab_T);
			
			if(intTmpQntyCurr 	< 0) intTmpQntyCurr = 0;
			if(intTmpWtCurr 	< 0) intTmpWtCurr 	= 0;
			if(intTmpHighCurr 	< 0) intTmpHighCurr = 0;		
			
			
			/**********************************************************
			* 5. 해당 적치단 정보 초기화
			**********************************************************/						
			jrParam.setField("STACK_LAYER_ACTIVE_STAT"      , YmConstant.STACK_LAYER_ACTIVE_STAT_E); //적치단활성상태 적치가능
			jrParam.setField("STACK_LAYER_STAT"             , YmConstant.STACK_LAYER_STAT_E); //적치단 상태 적치가능
			jrParam.setField("STACK_LAYER_GP"               , sTmpLayerGp); //적치단
			
			/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateWBSTACKLAYER
			UPDATE  TB_YM_STACKLAYER 
			   SET  STOCK_ID=NULL
			     ,  STACK_LAYER_ACTIVE_STAT= :V_STACK_LAYER_ACTIVE_STAT
			     ,  STACK_LAYER_STAT= :V_STACK_LAYER_STAT
				 ,  MODIFIER         = :V_MODIFIER
			     ,  MOD_DDTT         = SYSDATE	     
			 WHERE  STACK_COL_GP   = :V_STACK_COL_GP
			   AND  STACK_BED_GP    = :V_STACK_BED_GP
			   AND  STACK_LAYER_GP = :V_STACK_LAYER_GP
			*/
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateWBSTACKLAYER", logId, methodNm, "적치단 상태업데이트");
	
			
			/**********************************************************
			* 6. 해당 STACKER INFO 셋팅.
			**********************************************************/		
			jrParam.setField("STACK_BED_QNTY_CURR"      , intTmpQntyCurr+""); //적치배드수량 현재
			jrParam.setField("STACK_BED_WT_CURR"        , intTmpWtCurr+""); //적치배드중량현재
			jrParam.setField("STACK_BED_HIGH_CURR"      , intTmpHighCurr+""); //적치배드높이 현재 
			
			/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateSlabCurrSet 
			UPDATE TB_YM_STACKER
			   SET STACK_BED_QNTY_CURR = :V_STACK_BED_QNTY_CURR
			     , STACK_BED_WT_CURR = :V_STACK_BED_WT_CURR
			     , STACK_BED_HIGH_CURR = :V_STACK_BED_HIGH_CURR
				 , MODIFIER         = :V_MODIFIER
			     , MOD_DDTT         = SYSDATE	     			     
			WHERE STACK_COL_GP   = :V_STACK_COL_GP
			  AND STACK_BED_GP   = :V_STACK_BED_GP
			*/
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateSlabCurrSet", logId, methodNm, "적치대정보업데이트");
			
			/**********************************************************
			*7. Slab 공통에 있는 현재 위치 항목을 Clear
			**********************************************************/		
			jrParam.setField("YD_GP"      , YmConstant.YD_GP_2); //야드구분		
			jrParam.setField("YD_BAY_GP"      , ""); //동구분	
			jrParam.setField("YD_EQP_GP"      , ""); //설비구분
			jrParam.setField("YD_STK_COL_NO"  , ""); //야드적치열
			jrParam.setField("YD_STK_BED_NO"  , ""); //야드적치배드	
			jrParam.setField("YD_STK_LYR_NO"  , ""); //야드적치단	
			jrParam.setField("YD_STR_LOC"     , ""); //야드저장위치

	
			  /*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateStoreLocOfSlabComm
				UPDATE  TB_PT_SLABCOMM
				SET        YD_GP         = :V_YD_GP
				          ,YD_BAY_GP     = :V_YD_BAY_GP 
				          ,YD_EQP_GP     = :V_YD_EQP_GP
				          ,YD_STK_COL_NO = :V_YD_STK_COL_NO
				          ,YD_STK_BED_NO = :V_YD_STK_BED_NO
				          ,YD_STK_LYR_NO = :V_YD_STK_LYR_NO
				          ,YD_STR_LOC    = :V_YD_STR_LOC
				          ,YD_STR_LOC_HIS1 = YD_STR_LOC
				          ,YD_STR_LOC_HIS2 =YD_STR_LOC_HIS1
					      ,MODIFIER         = :V_MODIFIER
					      ,MOD_DDTT         = SYSDATE	
				WHERE    SLAB_NO = :V_SLAB_NO  		
			*/
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateStoreLocOfSlabComm", logId, methodNm, "Slab공통업데이트");			

			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of #4 CTC Slab Loading Result(CF1PB16)
	
	/**
	 *      [A] 오퍼레이션명 : SLAB W/B Information Request 정보(CF1PB27)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCF1PB27(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "SLAB W/B Information Request 정보[BSlabL2RcvSeEJB.rcvCF1PB27] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = null;	//전문 Return
		
		try{
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();

			jrParam.setField("STACK_COL_GP"          ,YmConstant.STACK_COL_GP_2CWB01); //w/b 적치열
			jrParam.setField("STACK_BED_GP04"        ,YmConstant.STACK_BED_GP_04); //04배드 
			jrParam.setField("STACK_BED_GP05"        ,YmConstant.STACK_BED_GP_05); //05배드 
			
			
			jrRtn = commUtils.addSndData(commDao.getMsgL2("CF1BP14", jrParam));
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of SLAB W/B Information Request 정보(CF1PB27)

	/**
	 *      [A] 오퍼레이션명 : 저장위치제원요구(A8YML001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvA8YML001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "A8저장위치제원요구[BSlabL2RcvSeEJB.rcvA8YML001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "저장위치제원요구(A8YML001) 수신 ", rcvMsg);
						
			//수신 항목 값
			//String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = commUtils.trim(rcvMsg.getFieldString("BAY_GP"      	)); //야드동구분
			String ydEqpGp      = commUtils.trim(rcvMsg.getFieldString("SECT_GP"      	)); //야드설비구분
			String ydStkColNo   = commUtils.trim(rcvMsg.getFieldString("COL_GP"  		)); //야드적치열번호
			String ydStkBedNo   = commUtils.trim(rcvMsg.getFieldString("STACK_BED_GP"  	)); //야드적치Bed번호
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydInfoSyncCd)) {
				throw new Exception("야드정보동기화코드(YD_INFO_SYNC_CD) 없음");
			} else if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			} else if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
				throw new Exception("야드동구분(BAY_GP) 없음");
			} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
				throw new Exception("야드설비구분(SECT_GP) 없음");
			}

			/**********************************************************
			* 2. 저장위치제원(YMA8L001) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD"	, ydInfoSyncCd                         ); //야드정보동기화코드
			jrParam.setField("STACK_COL_GP"  	, ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			jrParam.setField("STACK_BED_GP"  	, ydStkBedNo                           ); //야드적치Bed번호

			//전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA8L001", jrParam));
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 저장위치제원요구(A8YML001)
	
	/**
	 *      [A] 오퍼레이션명 : 저장품제원요구(A8YML002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvA8YML002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "A8저장품제원요구[BSlabL2RcvSeEJB.rcvA8YML002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "저장품제원요구(A8YML002) 수신 ", rcvMsg);
			
			//수신 항목 값
			//String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = commUtils.trim(rcvMsg.getFieldString("BAY_GP"      	)); //야드동구분
			String ydEqpGp      = commUtils.trim(rcvMsg.getFieldString("SECT_GP"      	)); //야드설비구분
			String ydStkColNo   = commUtils.trim(rcvMsg.getFieldString("COL_GP"  		)); //야드적치열번호
			String ydStkBedNo   = commUtils.trim(rcvMsg.getFieldString("STACK_BED_GP"  	)); //야드적치Bed번호
			String stlNo        = commUtils.trim(rcvMsg.getFieldString("STOCK_ID"		)); //재료번호

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
					throw new Exception("재료번호(SSTL_NO) 없음");
				}
			}

			/**********************************************************
			* 2. 저장품제원(YMA8L002) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD"	, ydInfoSyncCd                         ); //야드정보동기화코드
			jrParam.setField("MSG_GP"			, "I"                         		   ); //전문구분
			jrParam.setField("STACK_COL_GP"  	, ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			jrParam.setField("STACK_BED_GP"  	, ydStkBedNo                           ); //야드적치Bed번호
			jrParam.setField("YD_GP"          	, ydGp                                 ); //야드구분
			jrParam.setField("STOCK_ID"       	, stlNo                                ); //재료번호
			
			
			//STACK_COL_GP 위치가 존재하는지 체크 해서 throw 시켜라
			
			

			//전송Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA8L002", jrParam));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 저장품제원요구(A8YML002)
	
	/**
	 *      [A] 오퍼레이션명 : 설비운전모드전환(A8YML003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML003(JDTORecord rcvMsg) throws DAOException {
		String methodNm 	= "설비운전모드전환[BSlabL2RcvSeEJB.A8YML003] < " + rcvMsg.getResultMsg();
		String logId 		= rcvMsg.getResultCode();
		JDTORecord resMsg 	= JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn 		= false;	//크레인작업실적응답 전문 전송여부
		//boolean resYn 		= true;	//크레인작업실적응답 전문 전송여부 - test용

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "설비운전모드전환(A8YML003) 수신 ", rcvMsg);

			//수신 항목 값
			String msgId           = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId         = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //야드설비ID
			String ydEqpWrkMode    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE"));  // 1: On-Line(정상), "2": Off-Line, "4": 일시정지, "5": 비상정지
			String ydEqpWrkMode2   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE2")); // A:무인, R:리모컨, E:정비, M:유인
			String modifier        = commUtils.trim(rcvMsg.getFieldString("MODIFIER"         )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			//크레인작업실적응답 전문 전송여부를 Check
			if(modifier.equals(msgId)) {
				//Backup 이 아니고 L2에서 인터페이스 수신된 경우만 응답 전문 전송여부 Check 
				if (ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4))) {
					resYn = true;
				}
			}
			
			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE
			String sBR_GP     = ""; 	//ON_LINE/OFF_LINE  PARAM
			
			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId	); //야드설비ID
			resMsg.setField("YD_L2_WR_GP"     , "M"		); //야드L2실적구분 M:운전모드전환
			resMsg.setField("YD_L3_HD_RS_CD"  , "BR99"        ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:설비운전모드전환 수신처리"); //야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
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
			// 무인은 ON-OFF 없음	
			} else if ("".equals(ydEqpWrkMode)) {
				ydL3HdRsCd = "BR04";
				ydL3Msg    = "오류:운전모드2 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			String ydEqpWrkModeL3 = "";
			
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"	, ydEqpId        ); //야드설비ID
			jrParam.setField("MODIFIER"     , modifier       ); //수정자

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStatEqp
			--설비상태조회 
			SELECT WPROG_STAT     AS YD_EQP_STAT
			     , WORK_MODE      AS YD_EQP_WRK_MODE
				 , STACK_MAX_QNTY	                  --적재 최대 수량
				 , STACK_MAX_WT		                  --적재 최대 중량
			  FROM TB_YM_EQUIP EQ
			 WHERE EQUIP_GP = :V_YD_EQP_ID
			   AND DEL_YN    = 'N' 
			*/	   
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStatEqp", logId, methodNm, "설비상태조회");			
			if ( jsChk.size() > 0) {
				ydEqpWrkModeL3 = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE"));
			}				
			
			/**********************************************************
			* 2. 설비모드 Check
			**********************************************************/
			//Walking Beam일경우 압연모드 변경 0,2 => 대기 / 1,3 => 사용
			if ("WB".equals(ydEqpId.substring(2, 4))){
				
				String sOLD_WORK_MODE = "";
				
				//변경하기전에 WORK_MODE 값 읽어오고 UPDATE 후 WORK_MODE 읽어와서 대기 --> 선작업 일때만 WB보급요청 실행해야 한다.
				jrParam.setField("EQUIP_GP"    , "2CWB01"); //W/B
				/*
				 SELECT HMI_STAT ,WORK_MODE
	   			   FROM TB_YM_EQUIP
	              WHERE EQUIP_GP   =  :V_EQUIP_GP
				*/
				JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectEquipInfo", logId, methodNm, "설비상태조회-변경전 선작업상태");
				if(rsResult.size() > 0) {
					sOLD_WORK_MODE = rsResult.getRecord(0).getFieldString("WORK_MODE");
				}
				
				
				//압연모드 수신여부
				String sAPP020_YN = ymComm.BCoilApplyYn("YM2020","2","2CWB01");
				if("Y".equals(sAPP020_YN)){
					jrParam.setField("YD_EQP_WRK_MODE"  	, ydEqpWrkMode);
					jrParam.setField("YD_EQP_WRK_MODE2"  	, ydEqpWrkMode2);
				}
				
				jrParam.setField("EQUIP_STAT"  			, ydEqpWrkMode);
				
				/**********************************************************
				* 3. 설비상태 수정
				**********************************************************/
				/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqpModeWb
				UPDATE TB_YM_EQUIP
				   SET MODIFIER  = :V_MODIFIER
				      ,MOD_DDTT  = SYSDATE
				      ,WORK_MODE = CASE WHEN :V_YD_EQP_WRK_MODE = '0' THEN '2' --대기  0:OFF(수동)
				                        WHEN :V_YD_EQP_WRK_MODE = '1' THEN '1' --실행  1:MPC
				                        WHEN :V_YD_EQP_WRK_MODE = '2' THEN '1' --실행  2:TIMER
				                        WHEN :V_YD_EQP_WRK_MODE = '3' THEN '1' --실행  3:TABLE
				                        ELSE WORK_MODE END 
				      ,YD_EQP_WRK_MODE2  = nvl(:V_YD_EQP_WRK_MODE2,YD_EQP_WRK_MODE2)
				      ,EQUIP_STAT = nvl(:V_EQUIP_STAT ,EQUIP_STAT )
				 WHERE EQUIP_GP  = :V_YD_EQP_ID
				   AND DEL_YN    = 'N'
	        	*/	   
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqpModeWb", logId, methodNm, "Walking Beam설비상태 MODE 수정");
				
				if("Y".equals(sAPP020_YN)){
					
					
					//설비상태가 선작업 실행 일때 
					//강제로 워킹빔보급 수신으 처리해서 워킹빕 보급 스케줄을 생성한다.
					
					jrParam.setField("EQUIP_GP"    , "2CWB01"); //W/B
					/*
					 SELECT HMI_STAT ,WORK_MODE
		   			   FROM TB_YM_EQUIP
		              WHERE EQUIP_GP   =  :V_EQUIP_GP
					*/
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectEquipInfo", logId, methodNm, "설비상태조회-변경 후 선작업 실행상태");
					if(rsResult.size() > 0) {
						if("1".equals(rsResult.getRecord(0).getFieldString("WORK_MODE")) && "2".equals(sOLD_WORK_MODE)) {
							//WORK_MODE 가  2(대기) --> 1(선작업 실행) 일 때 W/B 보급 실행한다.
					
							//선작업지시 존재여부 체크
							jrParam.setField("YD_SCH_CD" 		, "2CWB01UM"); 
							jrParam.setField("YD_CRN_SCH_ID"	, "");
							/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch
							--권상,권하시 해당 크레인스케줄 id를 제외한 동일 스케줄 작업이 만들어져 있는지 조회
							SELECT CS.YD_CRN_SCH_ID
							      ,CS.YD_SCH_CD
							      ,CS.YD_UP_WO_LOC
							      ,CS.YD_DN_WO_LOC
							      ,CM.STOCK_ID
							      ,CM.YD_AID_WRK_YN
							FROM   TB_YM_CRNSCH CS
							      ,TB_YM_CRNWRKMTL CM
							WHERE  CS.DEL_YN = 'N'
							  AND  CM.DEL_YN = 'N'
							  AND  CS.YD_SCH_CD LIKE :V_YD_SCH_CD ||'%'
							  AND  CS.YD_CRN_SCH_ID NOT IN (NVL(:V_YD_CRN_SCH_ID,'1'))
							  AND  CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
							  AND  CM.YD_AID_WRK_YN = 'N'
							 */
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch", logId, methodNm, "선작업지시 존재여부 체크");
							
							if(rsResult.size() == 0) {
							
								jrParam.setField("YD_SCH_CD", "2CWB01UM");
								rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "C동WB보급 스케줄기준 조회");
								if(rsResult.size() > 0) {
									
									jrParam.setField("YD_WRK_PLAN_CRN", rsResult.getRecord(0).getFieldString("YD_WRK_CRN"));
									jrParam.setField("JMS_TC_CD", "A8YML020" );
									
									jrRtn = commUtils.addSndData(jrRtn, this.rcvA8YML020(jrParam));
									
								} 
							}
						}
					}
					
				}
				
				
			}else{
				if(ydEqpWrkMode.equals(YmConstant.YD_EQP_WRK_MODE_1)) { 		//1: On-Line
		        	
		        	jrParam.setField("YD_EQP_WRK_MODE"  	, ydEqpWrkMode); 	
		        	jrParam.setField("YD_EQP_AUTO_CRN_MODE" , ydEqpWrkMode); 
		        	if(!ydEqpWrkModeL3.equals(ydEqpWrkMode)) {
		        		sBR_GP = "R"; //복구  	
		        	}
		        	
		        } else if(ydEqpWrkMode.equals(YmConstant.YD_EQP_WRK_MODE_2)) {	//2: Off-Line
		        	
			       	jrParam.setField("YD_EQP_WRK_MODE"  	, ydEqpWrkMode); 
		        	if(!ydEqpWrkModeL3.equals(ydEqpWrkMode)) {
			        	sBR_GP = "B"; //고장과 동일하게 처리
		        	}
		        	
		        } else { // 일시정지 ,비상정지
		        	
		        	jrParam.setField("YD_EQP_AUTO_CRN_MODE" , ydEqpWrkMode); 	// 4: 일시정지, 5:비상정지
		        	
		        }
			
	        	jrParam.setField("YD_EQP_WRK_MODE2"  		, ydEqpWrkMode2); 	// A:무인, R:리모컨, E:정비, M:유인
	        	/**********************************************************
				* 3. 설비상태 수정
				**********************************************************/
	        	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqpMode
	        	--설비 상태 Mode 수정 
	        	UPDATE TB_YM_EQUIP
	        	   SET MODIFIER             = :V_MODIFIER
	        	      ,MOD_DDTT             = SYSDATE
	        	      ,WORK_MODE            = nvl(:V_YD_EQP_WRK_MODE,WORK_MODE)
	        	      ,YD_EQP_AUTO_CRN_MODE = nvl(:V_YD_EQP_AUTO_CRN_MODE,YD_EQP_AUTO_CRN_MODE)
	        	      ,YD_EQP_WRK_MODE2     = nvl(:V_YD_EQP_WRK_MODE2,YD_EQP_WRK_MODE2)
	        	 WHERE EQUIP_GP    = :V_YD_EQP_ID
	        	   AND DEL_YN      = 'N'
	        	*/	   
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqpMode", logId, methodNm, "설비상태 MODE 수정");
			}
	        

			/**********************************************************
			* 5. 크레인 리스케줄
			*  - 고장복구구분 [R:복구 리스케줄, B:고장 리스케줄]
			*  - 작업예약 야드스케쥴우선순위 수정
			*  - 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			*  - 대기상태인 야드설비ID에 해당하는 크레인작업지시 전문 추가
			**********************************************************/
			if ("CR".equals(ydEqpId.substring(2, 4))&&("R".equals(sBR_GP)||"B".equals(sBR_GP))) {
				//크레인 리스케줄
				jrParam.setField("MSG_ID", msgId); //수신 전문 I/F ID
				jrParam.setField("BR_GP" , sBR_GP); 
				jrRtn = this.trtCrnResch(jrParam);
			}
			
			/**********************************************************
			* 6. 크레인작업실적응답 전문 전송(YMA8L005)
			**********************************************************/
			if (resYn) {
				commUtils.printLog(logId,"확인:"+resYn, "SL");
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
			}

	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YmCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { bSlabComm.getYMA8L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 설비운전모드전환(A8YML003)
	
	
	/**
	 *      [A] 오퍼레이션명 : 설비고장복구실적(A8YML004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML004(JDTORecord rcvMsg) throws DAOException {
		String methodNm 	= "설비고장복구실적[BSlabL2RcvSeEJB.A8YML004] < " + rcvMsg.getResultMsg();
		String logId 		= rcvMsg.getResultCode();
		JDTORecord resMsg 	= JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		//boolean resYn 		= false;	//크레인작업실적응답 전문 전송여부
		boolean resYn 		= true;	//크레인작업실적응답 전문 전송여부 - test용

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "설비고장복구실적(A8YML004) 수신 ", rcvMsg);

			//수신 항목 값
			String msgId           = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId         = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"          )); //야드설비ID
			String ydEqpStat       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_STAT"        )); //야드설비상태(B:고장, N:정상, R:복구 등)
			String ydEqpPauseCode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_PAUSE_CODE"  )); //야드설비휴지코드
			String ydEqpTrblRcvrDt = commUtils.trim(rcvMsg.getFieldString("YD_EQP_TRBL_RCVR_DT")); //야드설비고장복구일시
			String modifier        = commUtils.trim(rcvMsg.getFieldString("MODIFIER"         )); //수정자(Backup Only)
			String brGp            = ""; //고장복구구분
			if ("".equals(modifier)) { modifier = msgId; }

			//크레인작업실적응답 전문 전송여부를 Check
			if(modifier.equals(msgId)) {
				//Backup 이 아니고 L2에서 인터페이스 수신된 경우만 응답 전문 전송여부 Check 
				if (ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4))) {
					resYn = true;
				}
			}
			
			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE
			
			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId	); //야드설비ID
			resMsg.setField("YD_L2_WR_GP"     , ydEqpStat	); //야드L2실적구분 R:고장복구실적
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
			} else if ("".equals(ydEqpTrblRcvrDt) && ("B".equals(ydEqpStat) || "N".equals(ydEqpStat) || "R".equals(ydEqpStat))) {
				ydL3HdRsCd = "BR05";
				ydL3Msg    = "오류:고장복구일시 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			
			/**********************************************************
			* 2. 설비상태 Check
			**********************************************************/
			if ("B".equals(ydEqpStat)) {
				brGp = "B"; //고장
				if ("".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "B000";
				}
			} else if ("N".equals(ydEqpStat) || "R".equals(ydEqpStat)) {
				brGp = "R"; //복구
				if ("".equals(ydEqpPauseCode)) {
					//ydEqpPauseCode = "R000";
					ydEqpPauseCode = "0000";
					ydEqpStat = "N";
				}

				if ("CR".equals(ydEqpId.substring(2, 4))) {
					ydEqpStat = "W";
				} else {
					ydEqpStat = "N";
				}
			}
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"          , ydEqpId        ); //야드설비ID
			jrParam.setField("YD_EQP_PAUSE_CODE"  , ydEqpPauseCode ); //야드설비휴지코드
			jrParam.setField("YD_EQP_PAUSE_OCC_DT", ydEqpTrblRcvrDt); //야드설비휴지발생일시
			jrParam.setField("YD_EQP_STAT"        , ydEqpStat      ); //야드설비상태
			jrParam.setField("BR_GP"              , brGp           ); //고장복구구분
			jrParam.setField("MODIFIER"           , modifier       ); //수정자

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStatEqp
			--설비상태조회 
			SELECT WPROG_STAT     AS YD_EQP_STAT
			     , WORK_MODE      AS YD_EQP_WRK_MODE
				 , STACK_MAX_QNTY	                  --적재 최대 수량
				 , STACK_MAX_WT		                  --적재 최대 중량
			     , CURR_STOP_LOC
			  FROM TB_YM_EQUIP EQ
			 WHERE EQUIP_GP = :V_YD_EQP_ID
			   AND DEL_YN    = 'N'    */ 
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStatEqp", logId, methodNm, "설비상태조회");

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
				throw new Exception(ydL3Msg);
			}
	        
        	if("B".equals(ydEqpStat) || "R".equals(ydEqpStat) || "N".equals(ydEqpStat) || "W".equals(ydEqpStat)) {
        		//설비상태가 고장(B),복구(R),정상(N) 일경우만
	        	/**********************************************************
				* 3. 설비상태 수정
				**********************************************************/
	        	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp
				--설비 상태 수정 
				UPDATE TB_YM_EQUIP
				   SET MODIFIER    = :V_MODIFIER
				      ,MOD_DDTT    = SYSDATE
				      ,WPROG_STAT = :V_YD_EQP_STAT
				 WHERE EQUIP_GP    = :V_YD_EQP_ID
				   AND DEL_YN      = 'N'
	        	*/	   
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp", logId, methodNm, "설비상태  수정");
	
				/**********************************************************
				* 4. 설비휴지 등록
				**********************************************************/
				if (!"".equals(brGp)) {
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updEqpPause", logId, methodNm, "설비휴지 등록");
				}
        	}
			
			/**********************************************************
			* 5. 크레인 리스케줄
			*  - 고장복구구분 [R:복구 리스케줄, B:고장 리스케줄]
			*  - 작업예약 야드스케쥴우선순위 수정
			*  - 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			*  - 대기상태인 야드설비ID에 해당하는 크레인작업지시 전문 추가
			**********************************************************/
			String sYD_EQP_WRK_MODE = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE"));
			if ("CR".equals(ydEqpId.substring(2, 4)) && "1".equals(sYD_EQP_WRK_MODE)) {//on-line일때만 리스케줄
				//크레인 리스케줄
				jrParam.setField("MSG_ID", msgId); //수신 전문 I/F ID
				jrRtn = this.trtCrnResch(jrParam);
			}
			
			/**********************************************************
			* STE 설비 고장시 크레인 비상스케쥴 작업지시 호출 
			**********************************************************/
			// STE #1
			if ("2AST01".equals(ydEqpId) && "B".equals(ydEqpStat)) {
				
				//CTC#2 3번 번지 최상단 Slab 정보를 작업예약한다.
				String sYD_SCH_CD = "2AHB02UM"; //A동 STE 비상보급
				String sYD_TO_LOC_GUIDE = "2ACT010101"; //sYD_TO_LOC_GUIDE = "2ACT010101";
				
				//선작업지시 존재여부 체크
				jrParam.setField("YD_SCH_CD" 		, sYD_SCH_CD); 
				jrParam.setField("YD_CRN_SCH_ID"	, "1");
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch
				--권상,권하시 해당 크레인스케줄 id를 제외한 동일 스케줄 작업이 만들어져 있는지 조회
				SELECT CS.YD_CRN_SCH_ID
				      ,CS.YD_SCH_CD
				      ,CS.YD_UP_WO_LOC
				      ,CS.YD_DN_WO_LOC
				      ,CM.STOCK_ID
				      ,CM.YD_AID_WRK_YN
				FROM   TB_YM_CRNSCH CS
				      ,TB_YM_CRNWRKMTL CM
				WHERE  CS.DEL_YN = 'N'
				  AND  CM.DEL_YN = 'N'
				  AND  CS.YD_SCH_CD LIKE :V_YD_SCH_CD ||'%'
				  AND  CS.YD_CRN_SCH_ID NOT IN (NVL(:V_YD_CRN_SCH_ID,'1'))
				  AND  CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
				  AND  CM.YD_AID_WRK_YN = 'N'
				 */
				JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch", logId, methodNm, "선작업지시 존재여부 체크");
				
				if(rsResult.size() == 0) {
				
				
					jrParam.setField("STACK_COL_GP"		, "2ACT02"); 
					jrParam.setField("STACK_BED_GP"		, "03");
					/*
					SELECT *
					  FROM TB_YM_STACKLAYER
					 WHERE STACK_COL_GP	= :V_STACK_COL_GP
					   AND STACK_BED_GP	= :V_STACK_BED_GP 
					   AND STOCK_ID IS NOT NULL
					 ORDER BY STACK_LAYER_GP DESC
					 */
					JDTORecordSet rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStackLayerInfoWithBedL", logId, methodNm, "CTC#2 03Bed 조회 ");
	
					boolean bFLAG = true;
					
					if(rsResult2.size() < 2) {
						commUtils.printLog(logId, "=======:::: STE고장 비상스케쥴 = CTC#2 3번 번지 02단 정보 없음! 비상스케쥴 수행안함 ", "SL");
						bFLAG = false;
					}
				
					if (bFLAG) {
						
						//스케줄코드로 스케줄기준Table조회
						jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
				    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
						SELECT YD_SCH_CD
						     , YD_WRK_CRN
						     , YD_WRK_CRN_PRIOR
						  FROM TB_YM_SCHEDULERULE
						 WHERE DEL_YN = 'N'
						   AND YD_SCH_CD = :V_YD_SCH_CD
						*/   
						JDTORecordSet rsResult3 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
						
						if(rsResult3.size() > 0) {
							String sYD_SCH_PRIOR = rsResult3.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
							
							//작업예약ID생성
							String sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
						
							/**********************************************************
							* 2-1. 작업예약(TB_YM_WRKBOOK) 생성
							**********************************************************/
							jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
							jrParam.setField("YD_GP"			, YmConstant.YD_GP_2);
							jrParam.setField("YD_BAY_GP"		, sYD_SCH_CD.substring(1,2));
							jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD); //야드스케쥴코드
							jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
							jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
							jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
							jrParam.setField("YD_TO_LOC_GUIDE"	, sYD_TO_LOC_GUIDE); //야드To위치Guide
							jrParam.setField("YD_TO_LOC_DCSN_MTD", "S"); //야드TO위치결정방법
							
							commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
							
							/**********************************************************
							* 2-2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
							**********************************************************/
							jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
							jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult2.getRecord(0).getFieldString("STOCK_ID")));
							jrParam.setField("STACK_COL_GP"		, commUtils.trim(rsResult2.getRecord(0).getFieldString("STACK_COL_GP")));
							jrParam.setField("STACK_BED_GP"		, commUtils.trim(rsResult2.getRecord(0).getFieldString("STACK_BED_GP")));
							jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(rsResult2.getRecord(0).getFieldString("STACK_LAYER_GP")));
							
							commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
							
							/**********************************************************
							* 2-3. TB_YM_STOCK의 저장품 이동조건 등록
							**********************************************************/
							jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult2.getRecord(0).getFieldString("STOCK_ID")));
							jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_FS ); //압연작업대기
							
							commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 저장품 이동 조건 UPDATE");
							
							//스케줄 메인 호출
							JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
							jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
							jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
							jrCrnSchMsg.setField("YD_WBOOK_ID"  		, sYD_WBOOK_ID); //작업예약ID
							jrCrnSchMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
							jrCrnSchMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
							jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);		
						}
					} //bFLAG
				} else {
					commUtils.printLog(logId, "=======:::: A동 STE 비상보급 스페줄이 존재합니다. " , "SL");					
				}
			}
			
			// STE #3 (PICKUP CRANE)
			if ("2CST03".equals(ydEqpId) && "S".equals(ydEqpStat)) { //S: 비상보급시작
				
				//STE#3 호기가 고장이면  W/B보급 스케줄 주작업 크래인을 C1으로 변경한다.
				
				String sYD_WRK_CRN = "";
				
				//스케줄코드로 스케줄기준Table조회
				jrParam.setField("YD_SCH_CD", "2CWB01UM"); //C동 WB보급
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
				SELECT YD_SCH_CD
				     , YD_WRK_CRN
				     , YD_WRK_CRN_PRIOR
				  FROM TB_YM_SCHEDULERULE
				 WHERE DEL_YN = 'N'
				   AND YD_SCH_CD = :V_YD_SCH_CD
				*/   
				JDTORecordSet rsResult4 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
				
				if(rsResult4.size() > 0) {
					sYD_WRK_CRN = rsResult4.getRecord(0).getFieldString("YD_WRK_CRN");
				}
				
				if(!"2CCRC1".equals(sYD_WRK_CRN)) {
					jrParam.setField("YD_MULTI_WRK_YN"	, "N" );
					jrParam.setField("YD_WRK_CRN"		, "2CCRC1");    //주작업크레인 C1
					jrParam.setField("YD_ALT_CRN"		, "2CCRC2");    //보조작업크레인 C2
					jrParam.setField("YD_SCH_CD"		, "2CWB01UM");  //W/B 보급 스케줄
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSchRuleMultiYn
					UPDATE TB_YM_SCHEDULERULE
					   SET MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,YD_MULTI_WRK_YN = NVL(:V_YD_MULTI_WRK_YN, YD_MULTI_WRK_YN)
					      ,YD_WRK_CRN = :V_YD_WRK_CRN
					      ,YD_ALT_CRN = :V_YD_ALT_CRN
					 WHERE YD_SCH_CD = :V_YD_SCH_CD  */
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSchRuleMultiYn", logId, methodNm, "스케줄 기준 야드멀티작업여부 설정");
				}
				
				
				//W/B 05번 번지 최상단 Slab 정보를 작업예약한다.
				String sYD_SCH_CD = "2CHB02UM"; //C동 STE 비상보급
				String sYD_TO_LOC_GUIDE = "2CCT040101"; //sYD_TO_LOC_GUIDE = "2CCT040101";
				
				//선작업지시 존재여부 체크
				jrParam.setField("YD_SCH_CD" 		, sYD_SCH_CD); 
				jrParam.setField("YD_CRN_SCH_ID"	, "1");
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch
				--권상,권하시 해당 크레인스케줄 id를 제외한 동일 스케줄 작업이 만들어져 있는지 조회
				SELECT CS.YD_CRN_SCH_ID
				      ,CS.YD_SCH_CD
				      ,CS.YD_UP_WO_LOC
				      ,CS.YD_DN_WO_LOC
				      ,CM.STOCK_ID
				      ,CM.YD_AID_WRK_YN
				FROM   TB_YM_CRNSCH CS
				      ,TB_YM_CRNWRKMTL CM
				WHERE  CS.DEL_YN = 'N'
				  AND  CM.DEL_YN = 'N'
				  AND  CS.YD_SCH_CD LIKE :V_YD_SCH_CD ||'%'
				  AND  CS.YD_CRN_SCH_ID NOT IN (NVL(:V_YD_CRN_SCH_ID,'1'))
				  AND  CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
				  AND  CM.YD_AID_WRK_YN = 'N'
				 */
				JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch", logId, methodNm, "선작업지시 존재여부 체크");
				
				if(rsResult.size() == 0) {
				
					//--------------------------------------------------------------------------------
					//C동 STE 비상보급  시작
					//--------------------------------------------------------------------------------
					//적치단 테이블에서 W/B 05Bed 정보를 읽어 온다.
					jrParam.setField("STACK_COL_GP"		, "2CWB01"); 
					jrParam.setField("STACK_BED_GP"		, "05");
					/*
					SELECT *
					  FROM TB_YM_STACKLAYER
					 WHERE STACK_COL_GP	= :V_STACK_COL_GP
					   AND STACK_BED_GP	= :V_STACK_BED_GP 
					   AND STOCK_ID IS NOT NULL
					 ORDER BY STACK_LAYER_GP DESC
					 */
					JDTORecordSet rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStackLayerInfoWithBedL", logId, methodNm, "W/B 05Bed 조회 ");
	
					boolean bFLAG = true;
					String sSTOCK_ID = "";
					String sSTACK_LAYER_STAT = "";
					for (int ii = 0; ii <  rsResult2.size(); ++ii ) {
						sSTOCK_ID         = rsResult2.getRecord(ii).getFieldString("STOCK_ID");
						sSTACK_LAYER_STAT = rsResult2.getRecord(ii).getFieldString("STACK_LAYER_STAT");
					}
					
					if ("".equals(sSTOCK_ID)) {
						//"STE고장 비상스케쥴 = W/B 5번 번지 저장품정보 존재안함");
						commUtils.printLog(logId, "=======:::: STE고장 비상스케쥴 = W/B 5번 번지 저장품정보 존재안함. " , "SL");
						bFLAG = false;
					}
					
					if (!"C".equals(sSTACK_LAYER_STAT)) {
						//"STE고장 비상스케쥴 = W/B 5번 번지 저장품정보 작업등록상태 ="+sLayerStat);
						commUtils.printLog(logId, "=======:::: STE고장 비상스케쥴 = W/B 5번 번지 저장품정보 작업등록상태 = " + sSTACK_LAYER_STAT, "SL");
						bFLAG = false;
					}
				
					if (bFLAG) {
						//CTC4 트랙킹 관리가 안되기 때문에 크레인 작업지시 생성 전에 Clear
						jrParam.setField("STACK_COL_GP"		, "2CCT04"); 
						jrParam.setField("STACK_BED_GP"		, "01");
						jrParam.setField("STACK_LAYER_GP"	, "01");
						jrParam.setField("STACK_LAYER_STAT" , "E");
						jrParam.setField("STOCK_ID"         , "");
						/*
						UPDATE TB_YM_STACKLAYER
						   SET STOCK_ID			= :V_STOCK_ID
							 , STACK_LAYER_STAT	= :V_STACK_LAYER_STAT
							 , MODIFIER         = 'SYSTEM'
						 	 , MOD_DDTT         = SYSDATE     
						 WHERE STACK_COL_GP   = :V_STACK_COL_GP 
						   AND STACK_BED_GP   = :V_STACK_BED_GP 
						   AND STACK_LAYER_GP = :V_STACK_LAYER_GP 
						 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat", logId, methodNm, "CTC4 Clear");
						
						
						
						//스케줄코드로 스케줄기준Table조회
						jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
				    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
						SELECT YD_SCH_CD
						     , YD_WRK_CRN
						     , YD_WRK_CRN_PRIOR
						  FROM TB_YM_SCHEDULERULE
						 WHERE DEL_YN = 'N'
						   AND YD_SCH_CD = :V_YD_SCH_CD
						*/   
						JDTORecordSet rsResult3 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
						
						if(rsResult3.size() > 0) {
							String sYD_SCH_PRIOR = rsResult3.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
							
							//작업예약ID생성
							String sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
						
							/**********************************************************
							* 2-1. 작업예약(TB_YM_WRKBOOK) 생성
							**********************************************************/
							jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
							jrParam.setField("YD_GP"			, YmConstant.YD_GP_2);
							jrParam.setField("YD_BAY_GP"		, sYD_SCH_CD.substring(1,2));
							jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD); //야드스케쥴코드
							jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
							jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
							jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
							jrParam.setField("YD_TO_LOC_GUIDE"	, sYD_TO_LOC_GUIDE); //야드To위치Guide
							jrParam.setField("YD_TO_LOC_DCSN_MTD", "S"); //야드TO위치결정방법
							
							commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
							
							/**********************************************************
							* 2-2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
							**********************************************************/
							jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
							jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult2.getRecord(0).getFieldString("STOCK_ID")));
							jrParam.setField("STACK_COL_GP"		, commUtils.trim(rsResult2.getRecord(0).getFieldString("STACK_COL_GP")));
							jrParam.setField("STACK_BED_GP"		, commUtils.trim(rsResult2.getRecord(0).getFieldString("STACK_BED_GP")));
							jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(rsResult2.getRecord(0).getFieldString("STACK_LAYER_GP")));
							
							commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
							
							/**********************************************************
							* 2-3. TB_YM_STOCK의 저장품 이동조건 등록
							**********************************************************/
							jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult2.getRecord(0).getFieldString("STOCK_ID")));
							jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_FS ); //압연작업대기
							
							commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 저장품 이동 조건 UPDATE");
							
							//스케줄 메인 호출
							JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
							jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
							jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
							jrCrnSchMsg.setField("YD_WBOOK_ID"  		, sYD_WBOOK_ID); //작업예약ID
							jrCrnSchMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
							jrCrnSchMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
							jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);		
						}
					} //bFLAG
				} else {
					commUtils.printLog(logId, "=======:::: C동 STE 비상보급 스페줄이 존재합니다. " , "SL");					
				}
			} else if ("2CST03".equals(ydEqpId) && "T".equals(ydEqpStat)) { //T : 비상보급종료 
				
				//STE#3 호기가 복구되면  W/B보급 스케줄 주작업 크래인을 C2로 변경한다.
				
				String sYD_WRK_CRN = "";
				
				//스케줄코드로 스케줄기준Table조회
				jrParam.setField("YD_SCH_CD", "2CWB01UM"); //C동 WB보급
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
				SELECT YD_SCH_CD
				     , YD_WRK_CRN
				     , YD_WRK_CRN_PRIOR
				  FROM TB_YM_SCHEDULERULE
				 WHERE DEL_YN = 'N'
				   AND YD_SCH_CD = :V_YD_SCH_CD
				*/   
				JDTORecordSet rsResult4 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
				
				if(rsResult4.size() > 0) {
					sYD_WRK_CRN = rsResult4.getRecord(0).getFieldString("YD_WRK_CRN");
				}
				
				if(!"2CCRC2".equals(sYD_WRK_CRN)) {
					jrParam.setField("YD_MULTI_WRK_YN"	, "N" );
					jrParam.setField("YD_WRK_CRN"		, "2CCRC2");
					jrParam.setField("YD_ALT_CRN"		, "2CCRC1");
					jrParam.setField("YD_SCH_CD"		, "2CWB01UM");
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSchRuleMultiYn
					UPDATE TB_YM_SCHEDULERULE
					   SET MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,YD_MULTI_WRK_YN = NVL(:V_YD_MULTI_WRK_YN, YD_MULTI_WRK_YN)
					      ,YD_WRK_CRN = :V_YD_WRK_CRN
					      ,YD_ALT_CRN = :V_YD_ALT_CRN
					 WHERE YD_SCH_CD = :V_YD_SCH_CD  */
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSchRuleMultiYn", logId, methodNm, "스케줄 기준 야드멀티작업여부 설정");
				}
			}
			
			
			if ("2CWB01".equals(ydEqpId) && "B".equals(ydEqpStat)) {
				
				//WB 고장시 스케줄사용여부 OFF 처리, 선작업지시 대기 로 설정
				
				jrParam.setField("HMI_STAT"		, "C"); //C:OFF, O:ON
				jrParam.setField("EQUIP_GP"		, "2CWB01"); 
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEquipHmiStat", logId, methodNm, "B열연 SLAB 설비 스케줄사용여부 변경");
				
				jrParam.setField("WORK_MODE"	, "2");
				jrParam.setField("EQUIP_GP"		, "2CWB01"); 
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEquipBefWork", logId, methodNm, "B열연 SLAB 선작업지시사용여부 변경");
				
			} 
			
			if ("2DFS12".equals(ydEqpId)) {
				//형상12 가 고장일 경우 차량포인트 2DPT01, 2DPT02 를 사용 금지 시킨다.
				
				jrParam.setField("MODIFIER"			, modifier		); //수정자
				if("B".equals(ydEqpStat)) {
					jrParam.setField("YD_STK_COL_ACT_STAT"	, "N"); //야드적치열활성상태 N:사용불가
				} else {
					jrParam.setField("YD_STK_COL_ACT_STAT"	, "L"); //야드적치열활성상태 L:적치가능
				}
				// 차량포인트 적치열활성상태 UPDATE
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarPointActStat 
				UPDATE  TB_YD_CARPOINT
				   SET  MODIFIER = :V_MODIFIER
				       ,MOD_DDTT = SYSDATE
				       ,YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
				 WHERE  YD_STK_COL_GP = :V_YD_STK_COL_GP     */
				jrParam.setField("YD_STK_COL_GP"	, "2DPT01"	); //상차도위치
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarPointActStat", logId, methodNm, "차량포인트 2DPT01 적치열활성상태 UPDATE ");
				
				jrParam.setField("YD_STK_COL_GP"	, "2DPT02"	); //상차도위치
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarPointActStat", logId, methodNm, "차량포인트 2DPT02 적치열활성상태 UPDATE ");
			}
			
			if ("2DFS34".equals(ydEqpId)) {
				//형상34 가 고장일 경우 차량포인트 2DPT03, 2DPT04 를 사용 금지 시킨다.
				
				jrParam.setField("MODIFIER"			, modifier		); //수정자
				if("B".equals(ydEqpStat)) {
					jrParam.setField("YD_STK_COL_ACT_STAT"	, "N"); //야드적치열활성상태 N:사용불가
				} else {
					jrParam.setField("YD_STK_COL_ACT_STAT"	, "L"); //야드적치열활성상태 L:적치가능
				}
				// 차량포인트 적치열활성상태 UPDATE
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarPointActStat 
				UPDATE  TB_YD_CARPOINT
				   SET  MODIFIER = :V_MODIFIER
				       ,MOD_DDTT = SYSDATE
				       ,YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
				 WHERE  YD_STK_COL_GP = :V_YD_STK_COL_GP     */
				jrParam.setField("YD_STK_COL_GP"	, "2DPT03"	); //상차도위치
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarPointActStat", logId, methodNm, "차량포인트 2DPT03 적치열활성상태 UPDATE ");
				
				jrParam.setField("YD_STK_COL_GP"	, "2DPT04"	); //상차도위치
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarPointActStat", logId, methodNm, "차량포인트 2DPT04 적치열활성상태 UPDATE ");
			}
			
			/********************************************************** 
			* 6. 크레인작업실적응답 전문 전송(YMA8L005)
			**********************************************************/
			if (resYn) {
				commUtils.printLog(logId,"확인:"+resYn, "SL");
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YmCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { bSlabComm.getYMA8L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 설비고장복구실적(A8YML004)
	
	/**
	 *      [A] 오퍼레이션명 : B열연SLAB크레인작업지시요구(A8YML007)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvA8YML007(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "B열연SLAB크레인작업지시요구[BSlabL2RcvSeEJB.rcvA8YML007] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용

		try {
			commUtils.printLog(logId, methodNm, "S+"); 
			
			//수신 항목 값 
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");

			JDTORecord jrRtn  = JDTORecordFactory.getInstance().create();	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "J"          ); //야드L2실적구분(지시요구)
			resMsg.setField("YD_L3_HD_RS_CD"  , "JR99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:크레인작업지시요구 수신처리"); //야드L3MESSAGE(Error)

			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"    , ydEqpId   ); //야드설비ID
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			/**********************************************************
			* 1. 설비상태 Check
			**********************************************************/
			JDTORecord jrChk = ymComm.chkEqpStat(jrParam);

			ydL3HdRsCd = commUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));
			ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
				return jrRtn;
			}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
// SLAB 고도화 - START			
			
			if("D3".equals(ydEqpId.substring(4,6))) {
			
				String sAPP100_D3_ADV_YN = commUtils.trim(rcvMsg.getFieldString("APP100_D3_ADV_YN"));
				if("".equals(sAPP100_D3_ADV_YN)) {
					sAPP100_D3_ADV_YN = ymComm.BCoilApplyYn("APP100","2","D3_ADV_YN");
				}
				
				commUtils.printLog(logId,  "==========[[[ SLAB D동 D3 고도화 적용여부 :" + sAPP100_D3_ADV_YN + " ]]]============", "SL");
			
				if("Y".equals(sAPP100_D3_ADV_YN)) {
					
					jrRtn = this.rcvA8YML007D3Adv(rcvMsg);
					return jrRtn;
					
				}
				
			} else if("C1".equals(ydEqpId.substring(4,6))) {
				
				String sAPP100_C1_ADV_YN = commUtils.trim(rcvMsg.getFieldString("APP100_C1_ADV_YN"));
				if("".equals(sAPP100_C1_ADV_YN)) {
					sAPP100_C1_ADV_YN = ymComm.BCoilApplyYn("APP100","2","C1_ADV_YN");
				}
				
				commUtils.printLog(logId,  "==========[[[ SLAB C동 C1 고도화 적용여부 :" + sAPP100_C1_ADV_YN + " ]]]============", "SL");
			
				if("Y".equals(sAPP100_C1_ADV_YN)) {
					
					jrRtn = this.rcvA8YML007C1Adv(rcvMsg);
					return jrRtn;
					
				}
				
			} else if("C2".equals(ydEqpId.substring(4,6))) {
					
				String sAPP100_C2_ADV_YN = commUtils.trim(rcvMsg.getFieldString("APP100_C2_ADV_YN"));
				if("".equals(sAPP100_C2_ADV_YN)) {
					sAPP100_C2_ADV_YN = ymComm.BCoilApplyYn("APP100","2","C2_ADV_YN");
				}
				
				commUtils.printLog(logId,  "==========[[[ SLAB C동 C2 고도화 적용여부 :" + sAPP100_C2_ADV_YN + " ]]]============", "SL");
			
				if("Y".equals(sAPP100_C2_ADV_YN)) {
					
					jrRtn = this.rcvA8YML007C2Adv(rcvMsg);
					return jrRtn;
					
				}
				
				String sAPP100_WB_ADV_YN = commUtils.trim(rcvMsg.getFieldString("APP100_WB_ADV_YN"));
				if("".equals(sAPP100_WB_ADV_YN)) {
					sAPP100_WB_ADV_YN = ymComm.BCoilApplyYn("APP100","2","WB_ADV_YN");
				}
				
				commUtils.printLog(logId,  "==========[[[ SLAB C동 WB 고도화 적용여부 :" + sAPP100_WB_ADV_YN + " ]]]============", "SL");
			
				if("Y".equals(sAPP100_WB_ADV_YN)) {
					
					jrRtn = this.rcvA8YML007C2Adv(rcvMsg);
					return jrRtn;
					
				}
			}
			
			
// SLAB 고도화 - END			
///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
						
			/**********************************************************
			* 2. 크레인스케줄 조회
			*    2.1 크레인스케줄이 존재하면 전송
			*    2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
			**********************************************************/
			/* 크레인작업지시요구 크레인스케줄 조회  */
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchA8YML007 
			SELECT YD_CRN_SCH_ID
			     , YD_WRK_PROG_STAT
			     , YD_UP_WO_LOC 
			     , YD_DN_WO_LOC 
			     , YD_SCH_CD
			     , YD_SCH_PRIOR
			     , SEQ1
			     , YD_SCH_ST_GP
			     , YD_WRK_PLAN_CRN
			     , YD_WRK_PLAN_CRN2
			     , OTHER_CRN_CNT 
			     , OTHER_BRA_CNT
			     -- 이송하차이고 
			     -- 상대편 크레인 작업이 없고 
			     -- 스케쥴 기동구분이 'N'(멀티) 인 경우
			     -- 동일작업이 2건이면 멀티작업가능
			     , CASE WHEN SUBSTR(YD_SCH_CD,3,6) = 'PT02LM' 
			             AND OTHER_CRN_CNT = 0 
			             AND OTHER_BRA_CNT = 0
			             AND YD_SCH_ST_GP  = 'N'
			             AND YD_SCH_CD = LEAD(YD_SCH_CD) OVER (ORDER BY SEQ1 DESC, YD_SCH_PRIOR, YD_CRN_SCH_ID) THEN 'Y'
			            ELSE 'N' END MULTI_YN
			  FROM
			       (
			         SELECT CS.YD_CRN_SCH_ID
			              , CS.YD_WRK_PROG_STAT
			              , CS.YD_UP_WO_LOC 
			              , CS.YD_DN_WO_LOC 
			              , CS.YD_SCH_CD
			              , CS.YD_SCH_PRIOR
			              , DECODE(CS.YD_WRK_PROG_STAT,'W','0','S','1', CS.YD_WRK_PROG_STAT) AS SEQ1
			              , WB.YD_SCH_ST_GP
			              , WB.YD_WRK_PLAN_CRN
			              , WB.YD_WRK_PLAN_CRN2
			              --상대크레인 작업 건수
			              , (SELECT COUNT(*) 
			                   FROM TB_YM_CRNSCH 
			                  WHERE DEL_YN = 'N'
			                    AND YD_EQP_ID = WB.YD_WRK_PLAN_CRN2) AS OTHER_CRN_CNT
			              , (SELECT COUNT(*)
			                   FROM TB_YM_EQUIP 
			                  WHERE EQUIP_GP = WB.YD_WRK_PLAN_CRN2
			                    AND (WPROG_STAT = 'B' OR WORK_MODE = '2')) AS OTHER_BRA_CNT -- 고장여부
			           FROM TB_YM_CRNSCH CS
			              , TB_YM_WRKBOOK WB
			          WHERE CS.YD_WBOOK_ID = WB.YD_WBOOK_ID
			            AND CS.DEL_YN = 'N'
			            AND WB.DEL_YN = 'N'
			            AND CS.YD_EQP_ID =  :V_YD_EQP_ID 
			            --권상위치 상단에 타크레인 존재여부 
			            AND 1 = CASE WHEN  (SELECT COUNT(*) 
			                                  FROM TB_YM_CRNSCH 
			                                 WHERE DEL_YN = 'N' 
			                                   AND YD_UP_WO_LOC   = CS.YD_UP_WO_LOC 
			                                   AND TO_NUMBER(YD_UP_WO_LAYER)  > TO_NUMBER(CS.YD_UP_WO_LAYER) 
			                                   AND YD_EQP_ID <> :V_YD_EQP_ID) = 0 THEN 1
			                         ELSE 0 END          
			            --권하위치 하단에 타크레인 존재여부 
			            AND 1 = CASE WHEN  (SELECT COUNT(*) 
			                                  FROM TB_YM_CRNSCH 
			                                 WHERE DEL_YN = 'N' 
			                                   AND YD_DN_WO_LOC   = CS.YD_DN_WO_LOC 
			                                   AND TO_NUMBER(YD_DN_WO_LAYER)  < TO_NUMBER(CS.YD_DN_WO_LAYER) 
			                                   AND YD_EQP_ID <> :V_YD_EQP_ID) = 0 THEN 1
			                         ELSE 0 END          
			          ORDER BY SEQ1 DESC, YD_SCH_PRIOR, YD_CRN_SCH_ID
			       )    
			 WHERE ROWNUM <= 1
			 */
			JDTORecordSet jsSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchA8YML007", logId, methodNm, "B열연SLAB크레인스케줄 조회");

			if (jsSch.size() > 0) {
				/**********************************************************
				* 2.1 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송
				**********************************************************/
				ydCrnSchId    = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				ydWrkProgStat = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
				String sYD_SCH_CD = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_SCH_CD"));

				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID

				if ("S".equals(ydWrkProgStat)||"1".equals(ydWrkProgStat) ||"2".equals(ydWrkProgStat) ||"3".equals(ydWrkProgStat)||"5".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.1.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 재지시 전송
					**********************************************************/
					jrParam.setField("MSG_GP", "U"); //전문구분 - 재지시
					/*
					UPDATE TB_YM_CRNSCH
					   SET MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					     , YD_WORD_DT       = SYSDATE
					 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
					   AND DEL_YN           = 'N'					 
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdWorkDt");					

					//크레인작업지시(YMA8L004) 전문 재전송
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L004", jrParam));

					commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
				} else {
					/**********************************************************
					* 2.1.2 대기[W] 이면 다음 작업지시 전송
					**********************************************************/

					jrParam.setField("MSG_GP", "I"); //전문구분 - 신규
					jrParam.setField("YD_EQP_STAT", "1"); //권상작업지시

					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp 
					--설비 상태 수정 
					UPDATE TB_YM_EQUIP
					   SET MODIFIER    = :V_MODIFIER
					      ,MOD_DDTT    = SYSDATE
					      ,WPROG_STAT = :V_YD_EQP_STAT
					 WHERE EQUIP_GP    = :V_YD_EQP_ID
					   AND DEL_YN      = 'N'
					*/	   
	        		commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp", logId, methodNm, "B열연SLAB설비상태 수정");

	        		//크레인스케줄 야드작업진행상태 수정
					jrParam.setField("YD_WRK_PROG_STAT"	 , "S"); //선택지시
					jrParam.setField("YD_L2_REQUEST_STAT", "1");
					
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatCrnSchWrkProg 
					--크레인스케줄 작업진행상태 수정
					UPDATE TB_YM_CRNSCH
					   SET MODIFIER         = :V_MODIFIER
					      ,MOD_DDTT         = SYSDATE
					      ,YD_WRK_PROG_STAT = :V_YD_WRK_PROG_STAT
					      ,YD_WORD_DT       = DECODE(:V_YD_WRK_PROG_STAT,'S',SYSDATE,'1',SYSDATE,'W',NULL,YD_WORD_DT)
					      ,YD_L2_REQUEST_STAT = NVL(:V_YD_L2_REQUEST_STAT,YD_L2_REQUEST_STAT) 
					 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
					   AND DEL_YN           = 'N' 
					*/
	        		commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatCrnSchWrkProg", logId, methodNm, "B열연SLAB크레인스케줄 야드작업진행상태 수정");
					//크레인작업지시(YMA8L004) 전문 생성
					
//SJH					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L004", jrParam));
	        		JDTORecordSet jsRtn1 = commDao.getMsgL2("YMA8L004", jrParam);
					jrRtn = commUtils.addSndData(jrRtn , jsRtn1);
					
					//크레인작업지시 Z값 갱신 처리
	        		if(jsRtn1.size() > 0 ) {
	        		//	this.procCrnsSchZaxis(jrParam);
	        			jrParam.setField("YD_UP_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_UP_WO_LOC_ZAXIS"));	
	        			jrParam.setField("YD_DN_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_DN_WO_LOC_ZAXIS"));	
	        			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS 
	    				--크레인스케줄 z값 갱신
	    				UPDATE TB_YM_CRNSCH
	    				   SET YD_UP_WO_LOC_ZAXIS = :V_YD_UP_WO_LOC_ZAXIS
	    				     , YD_DN_WO_LOC_ZAXIS = :V_YD_DN_WO_LOC_ZAXIS
	    				 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
	    				   AND DEL_YN           = 'N' 
	    				*/
	            		commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS", logId, methodNm, "SLAB크레인스케줄 Z값 수정");
	        			        			
	        		}
	        		
					///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
					// SLAB 고도화 - D3 자동긴급작업(S1) 적용여부  START			
	        		
	        		//D2 이송하차지시 일때 D3가 이미 이송하차 작업지시를 받고(권상지시) 권상전이라면  D3에게 D3 작업지시를 긴급작업(S1)으로 재 전송한다.
	        		if("D2".equals(ydEqpId.substring(4,6)) && "2DPT02LM".equals(sYD_SCH_CD)) {
	        		
						String sAPP100_D3_S1_YN = ymComm.BCoilApplyYn("APP100","2","D3_S1_YN");  
						commUtils.printLog(logId,  "==========[[[ SLAB D3 자동긴급작업(S1) 적용여부 :" + sAPP100_D3_S1_YN + " ]]]============", "SL");
					
						if("Y".equals(sAPP100_D3_S1_YN)) {
							
							/**********************************************************
							* 크레인작업지시요구 전문 조회 - 일시정지 긴급작업
							**********************************************************/
							String sAPP030 = ymComm.BCoilApplyYn("APP030","2","S1");
							
							if ("Y".equals(sAPP030)) {
								
								jrParam.setField("YD_EQP_ID"    , "2DCRD3"   ); //야드설비ID
								JDTORecordSet jsSchD3 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchA8YML007", logId, methodNm, "B열연SLAB D3크레인스케줄 조회");
								
								if(jsSchD3.size() > 0) {
									
									String ydCrnSchIdD3    = commUtils.trim(jsSchD3.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
									String ydSchCdD3	   = commUtils.trim(jsSchD3.getRecord(0).getFieldString("YD_SCH_CD"   ));
									String ydWrkProgStatD3 = commUtils.trim(jsSchD3.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
									
									
									if("2DPT02LM".equals(ydSchCdD3) && "1".equals(ydWrkProgStatD3)) {

										//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
										JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
						                
						               	jrYdMsg.setField("JMS_TC_CD"         , YmConstant.YMA8L004); //크레인작업지시요구
										jrYdMsg.setField("YD_CRN_SCH_ID"     , ydCrnSchIdD3         ); //야드크레인스케쥴ID
										jrYdMsg.setField("MSG_GP"            , "I"   ); //전문구분
										jrYdMsg.setField("YD_CRN_SCH_RMD_CNT", "S1"  ); //S1 일시정지 후 긴급작업
	
										jrRtn = commUtils.addSndData(jrRtn , commDao.getMsgL2("YMA8L004", jrYdMsg));
									}

								}
							}
							
						}
	        			
	        		}
					// SLAB 고도화 - D3 자동긴급작업(S1) 적용여부 END			
					///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	        		
					commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
				}
			} else {
				/**********************************************************
				* 2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
				*    2.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 Error 처리
				*    2.2 권하완료[4] 이면 스케줄을 생성
				*    2.3 명령선택대기[W] 이면 응답 전문을 전송 -> 2.2로 통합
				**********************************************************/
				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.2.1 재지시요구 시
					**********************************************************/
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , "크레인[" + ydEqpId + "-" + ydWrkProgStat + "] 작업지시 없음"); //야드L3MESSAGE
					
					jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));

					commUtils.printLog(logId, "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + ydEqpId + " : " + ydWrkProgStat + " - " + ydCrnSchId + " ]", "SL");
				} else {
					/**********************************************************
					* 2.2.2 대기상태[W], 권하완료[4] 지시요구
					**********************************************************/
					//크레인작업지시가 없으면 설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "W"); //대기(Wait)

	        		commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp", logId, methodNm, "B열연SLAB설비상태 수정");
	        		

	        		JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

	    			//작업예약 조회
					 /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWbIdA8YML007 
					SELECT *
					  FROM (
					        SELECT A.YD_WBOOK_ID
					             , A.YD_SCH_CD
					             , A.YD_SCH_CD_CNT
					             , A.YD_SCH_CD_YN
					             , A.YD_WRK_PLAN_TCAR
					             , A.YD_WRK_PLAN_CRN
					          FROM (
					                SELECT YD_WBOOK_ID
					                     , YD_SCH_CD
					                     , SUM(1) OVER(PARTITION BY YD_SCH_CD) AS YD_SCH_CD_CNT 
					                     , 'Y'  AS YD_SCH_CD_YN 
					                     , YD_WRK_PLAN_TCAR
					                     , YD_WRK_PLAN_CRN
					                     , SCH_CNCL_YN
					                     , (SELECT YD_SCH_AUTO_ST_YN 
					                          FROM TB_YM_SCHEDULERULE 
					                         WHERE YD_SCH_CD = WB.YD_SCH_CD 
					                           AND DEL_YN = 'N') AS YD_SCH_AUTO_ST_YN --스케줄별 자동기동여부 DEFAULT Y
					                  FROM TB_YM_WRKBOOK WB
					                 WHERE DEL_YN = 'N'
					                   --AND YD_WRK_PLAN_TCAR IS NULL
					                   --대차상차 인 경우 설비TABLE 적재가능 매수 보다 크거나 같으면 선택불가
					                   --대차 스케줄 과 목적동이 틀리면 선택불가 
					                   AND 1 =  CASE WHEN SUBSTR(YD_SCH_CD,3,2) = 'TC' AND SUBSTR(WB.YD_SCH_CD,7,1) = 'U' THEN 
					                                 CASE WHEN (SELECT COUNT(*) 
					                                              FROM TB_YM_STACKLAYER 
					                                             WHERE STACK_COL_GP = SUBSTR(WB.YD_SCH_CD,1,2)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
					                                               AND STOCK_ID IS NOT NULL) >= (SELECT STACK_MAX_QNTY FROM TB_YM_EQUIP WHERE EQUIP_GP = WB.YD_WRK_PLAN_TCAR )
					                                      THEN '2'
					                                      WHEN (SELECT NVL(SUBSTR(YD_CARUD_STOP_LOC,2,1),  WB.YD_AIM_BAY_GP )
					                                              FROM TB_YM_TCARSCH 
					                                             WHERE DEL_YN = 'N'
					                                               AND YD_EQP_ID = WB.YD_WRK_PLAN_TCAR) <> WB.YD_AIM_BAY_GP 
					                                      THEN '2'    
					                                      WHEN (SELECT SUBSTR(CURR_STOP_LOC,2,1)
					                                              FROM TB_YM_EQUIP 
					                                             WHERE DEL_YN = 'N'
					                                               AND EQUIP_GP = WB.YD_WRK_PLAN_TCAR) <> SUBSTR(WB.YD_SCH_CD,2,1)
					                                      THEN '2'
					                                      -- 대차 출발시 문제발생     
					                                      WHEN (SELECT COUNT(*) 
					                                              FROM TB_YM_STACKER 
					                                             WHERE STACK_COL_GP = SUBSTR(WB.YD_SCH_CD,1,2)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
					                                               AND STACK_BED_ACTIVE_STAT NOT IN ('L')) > 0   
					                                      THEN '2'
					                                      -- 대차 상차 로직
					                                      ELSE '1' END
					                                 ELSE '1' END    
					                   -- 대차에 관련된 스케쥴 기동시 동간작업기준과 틀리면 기동처리 안함                 
					                   AND 1 =  CASE WHEN (SELECT COUNT(*) 
					                                         FROM TB_YM_WRKBOOK WB1
					                                            , (SELECT EQUIP_GP
					                                                    , SUBSTR(CARLOAD_STOP_LOC,1,2)  ||SUBSTR(CARLD_SCH_CD,3,6) AS UP_SCH
					                                                    , SUBSTR(CARUNLOAD_STOP_LOC,1,2)||SUBSTR(CARUD_SCH_CD,3,6) AS DN_SCH
					                                                 FROM TB_YM_EQUIP WHERE EQUIP_GP LIKE '2XTC0%')  EQ1 
					                                            , (SELECT *
					                                                 FROM USRYMA.TB_YM_RULE 
					                                                WHERE REPR_CD_GP = 'YM2007'
					                                                  AND CD_GP = '2'
					                                                  AND DEL_YN = 'N') RULE
					                                        WHERE WB1.YD_WBOOK_ID = WB.YD_WBOOK_ID
					                                          AND WB1.DEL_YN = 'N'
					                                          AND WB1.YD_WRK_PLAN_TCAR = EQ1.EQUIP_GP
					                                          AND SUBSTR(WB1.YD_SCH_CD,3,6) = SUBSTR(DTL_ITM1(+),3,6) 
					                                          AND 'Y' = CASE WHEN YD_WRK_PLAN_TCAR IS NOT NULL AND SUBSTR(ITEM,1,1) = 'L' AND YD_SCH_CD != UP_SCH THEN  'Y'
					                                                         WHEN YD_WRK_PLAN_TCAR IS NOT NULL AND SUBSTR(ITEM,1,1) = 'U' AND YD_SCH_CD != DN_SCH THEN  'Y'
					                                                         ELSE 'N' END  ) > 0
					                                 THEN '2'                                                      
					                                 ELSE '1' END     
					                                  
					                   AND TRN_EQP_CD       IS NULL
					                   AND YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
					                                             FROM TB_YM_CRNSCH
					                                            WHERE DEL_YN = 'N')
					                   AND (YD_WRK_PLAN_CRN = :V_YD_EQP_ID 
					                         OR YD_SCH_CD IN
					                            (SELECT YD_SCH_CD
					                               FROM TB_YM_SCHEDULERULE
					                              WHERE (YD_WRK_CRN = :V_YD_EQP_ID )
					                                AND DEL_YN          = 'N')
					                       )
					                   AND NVL(YD_WRK_PLAN_CRN, :V_YD_EQP_ID ) = :V_YD_EQP_ID
					                  ORDER BY (CASE WHEN YD_WRK_PLAN_CRN=:V_YD_EQP_ID  THEN 1 ELSE 2 END ),YD_SCH_PRIOR, YD_WBOOK_ID     
					               ) A
					         WHERE 1=1
					           AND YD_SCH_CD_YN = 'Y'        
					           AND YD_SCH_AUTO_ST_YN = 'Y'
					       )
					   WHERE ROWNUM = 1
					*/
	        		JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWbIdA8YML007", logId, methodNm, "B열연SLAB작업예약 조회");

					//작업예약이 있으면 크레인스케줄호출
					if (jsWrkBook.size() > 0) {
						ydL3Msg = "크레인스케줄 호출";

						jrYdMsg.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"    , jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
						jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
						jrYdMsg.setField("MODIFIER"   , modifier); //수정자
							
						// 크레인스케줄 기동
						jrRtn = ymComm.getCrnSchMsg(jrYdMsg);
					} else {
						ydL3Msg = "다음 크레인작업지시 없음";
					}
					
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
					resMsg.setField("YD_CRN_SCH_ID" , ""     ); 
					
					jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
					
					commUtils.printLog(logId, "B열연SLAB크레인작업지시요구(다음지시) " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			try {
				//PIDEV_F : 정상SET후  ERROR 발생한 경우							
				if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {							
					resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)						
					resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)						
				}							

				//크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "YmCommEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { bSlabComm.getYMA8L005(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of B열연SLAB크레인작업지시요구(A8YML007)

	
	/**
	 *      [A] 오퍼레이션명 : 크레인권상실적(A8YML008)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML008(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인권상실적[BSlabL2RcvSeEJB.rcvA8YML008] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
	    JDTORecord jrParam	 = JDTORecordFactory.getInstance().create(); //Query 실행시 파라메터 전달용 JDTORecord 
		
		//송신용 
		JDTORecord jrYdMsg;
	    
		JDTORecord resMsg 	= JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn 		= true;	//크레인작업실적응답 전문 전송여부

		String ydL3HdRsCd = "";		//야드L3처리결과코드
		String ydL3Msg    = ""; 	//야드L3MESSAGE
		
		String ydWbookId  = ""; //야드작업예약ID
		String ydDnWoLoc  = ""; //야드권하지시위치
		String ydCarSchId  = ""; //차량스케줄ID
		
		String slabRemYn = ""; //상단 슬라브 존재유무
		
		JDTORecordSet jsChk = null;
		
		String sD3Flag = ""; //D동이송하차 D2+D3 작업으로 D2권상후 D3 작업지시 전송한 경우 "1"로 셋팅함.
		String sYD_EQP_WRK_SH = "0"; //C동 장입대상재 자동 동간이적 편성을 위한 변수
		
		try {

			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "크레인권상실적(A8YML008) 수신 ", rcvMsg);

			//수신 항목 값
			String msgId	 	 = commUtils.nvl(commUtils.getMsgId(rcvMsg),"A8YML008"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydUpWrLoc     = commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LOC"    )); //야드권상실적위치
			String ydUpWrLayer   = commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LAYER"  )); //야드권상실적단
			
			if (ydUpWrLayer.length() == 3) {
				//L2 수신 (L2에서 3자리로 전송)
				ydUpWrLayer = ydUpWrLayer.substring(1,3); //"002" --> "02"
			}
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축			
			String ydEqpWrkMode2 = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE2")); //야드설비작업모드2(A:무인자동,R:리모컨,E:정비,M:유인)
			String modifier 	 = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)

			if ("".equals(modifier)) { modifier = msgId; }
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
			
			//크레인작업실적응답 전문 생성용
			resMsg 	= JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "U"          ); //야드L2실적구분(권상실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권상실적 수신처리"); //야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
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
			} else if ("".equals(ydUpWrLoc)) {
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
			
			/**********************************************************
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStatCrnSch  
			--B열연 SLAB 크레인스케줄상태 조회 
			SELECT CS.* 
			     , (SELECT YD_EQP_WRK_MODE2 FROM USRYMA.TB_YM_EQUIP WHERE EQUIP_GP = CS.YD_EQP_ID) AS DB_YD_EQP_WRK_MODE2
			  FROM (
			        SELECT YD_WBOOK_ID
			              ,YD_EQP_ID
			              ,YD_SCH_CD
			              ,YD_WRK_PROG_STAT
			              ,YD_DN_WO_LOC
			              ,YD_UP_WR_LOC
			          FROM TB_YM_CRNSCH A
			         WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			           AND DEL_YN        = 'N'
			        ) CS  */
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStatCrnSch", logId, methodNm, "B열연 SLAB 크레인스케줄상태 조회");

			if (jsCrnSch.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "UP11";
				ydL3Msg = "오류:크레인스케쥴 DB정보 없음";
			} else {
				//크레인스케쥴 Table 야드작업진행상태 Check
				JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
				ydWbookId       = commUtils.trim(jrCrnSch.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
				ydDnWoLoc       = commUtils.nvl(jrCrnSch.getFieldString("YD_DN_WO_LOC"),"XX010101"); //야드권하지시위치
				String tmpStat  = commUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"       )); //야드설비ID
				String tmpMode2 = commUtils.trim(jrCrnSch.getFieldString("DB_YD_EQP_WRK_MODE2" )); //DB 작업모드2
				sYD_EQP_WRK_SH  = commUtils.nvl(jrCrnSch.getFieldString("YD_EQP_WRK_SH"), "0"); //야드설비작업매수

				//크레인작업실적응답 전송
				if ("2".equals(tmpStat)) {//인터페이스오류로인해 재수신받았을경우 응답전문재송신
					resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
					resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
					jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
					
					return jrRtn;
				}else if (!"1".equals(tmpStat) && !"W".equals(tmpStat) && !"S".equals(tmpStat)) {  //2018.06.19 권상 시 대기상태에서도 처리가 되도록 수정

					ydL3HdRsCd = "UP12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {

					ydL3HdRsCd = "UP13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}

				// 실적에서 MODE2 가 안올라 오는 경우 DB에 있는 DATA SETUP
				if(ydEqpWrkMode2.equals("")) {
					ydEqpWrkMode2 = tmpMode2;
				}
			}
			
			if (!"".equals(ydL3Msg)) {

				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			/**********************************************************
			* 3. 권상위치 단 Check
			**********************************************************/
			//2018.06.19 권상지시위치 바로 윗 단에 슬라브 존재하는지 확인
			jrParam.setField("STACK_COL_GP", ydUpWrLoc.substring(0,6)); //적치열구분
			jrParam.setField("STACK_BED_GP", ydUpWrLoc.substring(6,8)); //적치베드구분
			jrParam.setField("STACK_LAYER_GP", ydUpWrLayer); //적치단구분
			
			/*com.inisteel.cim.ym.bslab.dao.BSlabDAO.getInfoSlabRemain  권상위치 단 체크(슬라브 존재 여부 체크)
				SELECT DECODE(STOCK_ID,NULL,'N','Y') AS SLAB_REMAIN_YN
				FROM TB_YM_STACKLAYER
				WHERE STACK_COL_GP = :V_STACK_COL_GP
				AND STACK_BED_GP = :V_STACK_BED_GP
				AND STACK_LAYER_GP = :V_STACK_LAYER_GP + 1  
			 */
			JDTORecordSet jsSlabRm = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getInfoSlabRemain", logId, methodNm, "B열연 SLAB 권상위치 단 체크");
			
			if (jsSlabRm.size() > 0) {
				//크레인스케쥴 Table 야드작업진행상태 Check
				JDTORecord jrSlabRm = jsSlabRm.getRecord(0);
				slabRemYn       = commUtils.trim(jrSlabRm.getFieldString("SLAB_REMAIN_YN"     )); //슬라브존재유무
				
				if ("Y".equals(slabRemYn)) {
					ydL3HdRsCd = "UP12";
					ydL3Msg = "오류:권상실적단 위에 슬라브 존재";
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			commUtils.printLog(logId, " ★★★★   SLAB 권상처리시작 > 크레인 스케쥴 ID:"+ ydCrnSchId, "S+");

			String currDt = commUtils.getDateTime14(); //현재시각
			jrParam.setField("YD_WBOOK_ID" , ydWbookId  ); //수정자
			jrParam.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드크레인스케쥴ID
			
			/**********************************************************
			* 4. 권상실적위치가 대차(대차 하차작업)
			* 4.1 대차 하차 정보 등록
			*   - 대차이송재료 삭제
			*   - 대차스케줄 삭제 : 하차완료 시
			**********************************************************/
			if ("TC".equals(ydUpWrLoc.substring(2, 4))) {

				//대차하차스케쥴 조회
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getAxYML008TcarSchUd 
				--크레인권상실적 하차 대차스케줄 조회 
				SELECT TS.YD_TCAR_SCH_ID
				      ,CASE WHEN NVL((SELECT COUNT(*)
				                        FROM TB_YM_TCARFTMVMTL TM
				                       WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
				                         AND TM.DEL_YN = 'N'),0) -
				                 NVL((SELECT COUNT(*)
				                        FROM TB_YM_CRNWRKMTL CM
				                       WHERE CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                         AND CM.DEL_YN = 'N'),0) > 0 THEN 'N' ELSE 'Y'
				       END AS TCAR_UD_CMPL_YN --대차하차완료여부
				  FROM TB_YM_TCARSCH TS
				 WHERE TS.YD_CARUD_STOP_LOC = SUBSTR(:V_YD_UP_WR_LOC,1,6)
				   AND TS.DEL_YN = 'N'
				*/	   
				JDTORecordSet jsTC = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getAxYML008TcarSchUd", logId, methodNm, "대차하차스케쥴 조회");
				
				if (jsTC.size() > 0) {
				
					JDTORecord jrTC = jsTC.getRecord(0);
					String ydTcarSchId  = commUtils.trim(jrTC.getFieldString("YD_TCAR_SCH_ID" )); //야드대차스케쥴ID
					String tcarUdCmplYn = commUtils.trim(jrTC.getFieldString("TCAR_UD_CMPL_YN")); //대차하차완료여부

					commUtils.printLog(logId, methodNm+  "★★★★★  권상대차스케쥴:" + ydTcarSchId +  " 대차하차완료여부:" + tcarUdCmplYn +" ★★★★", "SL");
					
					if ("Y".equals(tcarUdCmplYn)) {
						//하차완료이면 대차스케줄 삭제 후 공대차출발지시 처리
					
						if (resYn) {
							//실패시 응답메세지 설정
							resMsg.setField("YD_L3_HD_RS_CD", "UP21"                    ); //야드L3처리결과코드
							resMsg.setField("YD_L3_MSG"     , "오류:대차 하차완료처리 실패"); //야드L3MESSAGE
						}
						
						//하차완료(공대차출발지시) 처리
						jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setResultCode(logId);							//Log ID
						jrYdMsg.setResultMsg(methodNm);							//Log Method Name
						jrYdMsg.setField("CRANE_ID"      	, ydEqpId); 		//야드설비ID(CRANE)
						jrYdMsg.setField("YD_TCAR_SCH_ID"	, ydTcarSchId); 	//야드대차스케쥴ID
						jrYdMsg.setField("YD_CARUD_STOP_LOC", ydUpWrLoc.substring(0, 6)); //권상위치 -> 하차완료위치
						jrYdMsg.setField("YD_EQP_ID"     	, ydUpWrLoc.substring(0, 1) + "XTC0" + ydUpWrLoc.substring(4, 5)); //야드설비ID(대차)
						jrYdMsg.setField("MODIFIER"    		, modifier   ); 	//수정자

						jrRtn = commUtils.addSndData(jrRtn, ymComm.trtTcarSchUdCmpl_Slab(jrYdMsg));
						
					} else {
						
						//하차완료가 아니면 크레인 권상재료 만큼 대차이송재료 삭제 후 대차작업실적 전송
						jrParam.setField("YD_TCAR_SCH_ID"   , ydTcarSchId              ); //야드대차스케쥴ID
						jrParam.setField("YD_CARUD_STOP_LOC", ydUpWrLoc.substring(0, 6)); //야드하차정지위치
						jrParam.setField("YD_CARUD_WRK_CRN" , ydEqpId                  ); //야드하차작업크레인

						//대차스케줄(하차) 수정
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML005TcarSchUd
						--크레인권상실적 하차 대차스케줄 수정 
						UPDATE TB_YM_TCARSCH
						   SET MODIFIER          = :V_MODIFIER
						      ,MOD_DDTT          = SYSDATE
						      ,YD_CAR_PROG_STAT  = 'D' --하차개시
						      ,YD_CARUD_ST_DT    = NVL(YD_CARUD_ST_DT,SYSDATE)
						      ,YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC
						      ,YD_CARUD_WRK_CRN  = :V_YD_CARUD_WRK_CRN
						 WHERE YD_TCAR_SCH_ID    = :V_YD_TCAR_SCH_ID
						   AND DEL_YN            = 'N'
						*/	   
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML005TcarSchUd", logId, methodNm, "대차스케줄(하차) 수정");

						//대차이송재료 삭제
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML005TcarMtlDel 
						--크레인권상실적 대차이송재료 삭제  
						MERGE INTO TB_YM_TCARFTMVMTL TM USING (
						SELECT :V_YD_TCAR_SCH_ID AS YD_TCAR_SCH_ID
						     , STOCK_ID
						  FROM TB_YM_CRNWRKMTL CM
						 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						   AND DEL_YN        = 'N'
						) DD ON (TM.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID AND TM.STOCK_ID = DD.STOCK_ID)
						WHEN MATCHED THEN UPDATE SET
							 TM.MODIFIER = :V_MODIFIER
						    ,TM.MOD_DDTT = SYSDATE
						    ,TM.DEL_YN   = 'Y'
						*/   
						    	
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML005TcarMtlDel", logId, methodNm, "대차이송재료 삭제");
						
					}
				}				
				
			}
			
			/**********************************************************
			* 5. 권상실적위치가 차량(하차)
			*    차량스케줄 야드차량진행상태가 하차도착(B) 또는 하차검수(C) 이고
			*  	   야드차량사용구분이 구내운송(L) 이면
			* 5.1 구내운송 소재차량하차개시(YSTSJ009) 전송
			* 5.2 차량이송재료 삭제
			* 5.3 차량스케줄 수정
			*   - 야드차량진행상태(D:하차개시), 야드설비작업매수 등 수정
			**********************************************************/
			if ("PT".equals(ydUpWrLoc.substring(2, 4))) {
				
				jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchId); //야드크레인스케쥴ID
				jrParam.setField("YD_WBOOK_ID"   , ydWbookId ); 
				jrParam.setField("WR_DT" 		 , currDt ); 
				
				//차량스케줄  야드하차작업예약ID 등록 (차량스케줄에 야드하차작업예약ID 없을 경우)

				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getAxYML008CarSchUdWbId  
				SELECT YD_CAR_SCH_ID
				     , YD_WBOOK_ID
				     , YD_CAR_USE_GP
				     , TRANS_EQUIPMENT_TYPE 
				     , YD_CAR_PROG_STAT
				     , YD_CAR_WRK_GP        -- 야드차량작업구분
				     , CAR_KIND
				  FROM (SELECT TS.YD_CAR_SCH_ID
				              ,WM.YD_WBOOK_ID
				              ,TS.YD_CAR_USE_GP
				              ,TS.TRANS_EQUIPMENT_TYPE 
				              ,TS.YD_CAR_PROG_STAT 
				              ,TS.YD_CAR_WRK_GP 
				              ,TS.CAR_KIND
				          FROM TB_YD_CARSCH     TS
				              ,TB_YD_CARFTMVMTL TM
				              ,TB_YM_WRKBOOKMTL WM
				         WHERE WM.STOCK_ID      = TM.STL_NO
				           AND TM.YD_CAR_SCH_ID = TS.YD_CAR_SCH_ID
				           AND WM.YD_WBOOK_ID   = :V_YD_WBOOK_ID
				           AND WM.DEL_YN        = 'N'
				           AND TM.DEL_YN        = 'N'
				           AND TS.DEL_YN        = 'N'
				         ORDER BY TS.YD_CAR_SCH_ID)
				 WHERE ROWNUM = 1
				*/ 
				JDTORecordSet jsCarChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getAxYML008CarSchUdWbId", logId, methodNm, "차량스케줄상태 조회");

				if (jsCarChk.size() == 0) {
					//크레인스케쥴 Table 존재유무 Check
					ydL3HdRsCd = "UP11";
					ydL3Msg = "오류:차량스케쥴 DB정보 없음";
				} else {
					
					//크레인스케쥴 Table 야드작업진행상태 Check
					ydCarSchId  		= commUtils.trim(jsCarChk.getRecord(0).getFieldString("YD_CAR_SCH_ID"     	)); //야드작업예약ID
					String ydCarUseGp 	= commUtils.trim(jsCarChk.getRecord(0).getFieldString("YD_CAR_USE_GP"     	)); //차량작업구분
					String equipType 	= commUtils.trim(jsCarChk.getRecord(0).getFieldString("TRANS_EQUIPMENT_TYPE")); //운송장비 TYPE P:PDA
					String ydCarProgStat= commUtils.trim(jsCarChk.getRecord(0).getFieldString("YD_CAR_PROG_STAT"    )); //차량진행상태
					String ydCarWrkGp	= commUtils.trim(jsCarChk.getRecord(0).getFieldString("YD_CAR_WRK_GP"     	)); //차량작업구분
					String ydCarKind	= commUtils.trim(jsCarChk.getRecord(0).getFieldString("CAR_KIND"     		)); //차량종류
					
					// 하차검수 및 하차도착인 경우
					if ( ydCarProgStat.endsWith("C") ||ydCarProgStat.endsWith("B")){
		    			if ( ydCarUseGp.equals("L") ) {
		    				
		    				String sYDTSJ009_YN = "Y";
		    				
		    				/**********************************************************
		    				* YDTSJ009 SLAB 하차개시 권상시 전송여부 체크
		    				**********************************************************/
		    				jrParam.setField("REPR_CD_GP"	, "YM2003");
		    				jrParam.setField("CD_GP"		, "2");
		    				jrParam.setField("ITEM"			, "YDTSJ009");
		    				jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "야드 기준 조회"); 
		    				
		    				if(jsChk.size() > 0) {
		    					sYDTSJ009_YN = jsChk.getRecord(0).getFieldString("DTL_ITM1");
		    				}
		    				
		    				if("Y".equals(sYDTSJ009_YN)) { 
			    				//구내운송 소재차량하차개시
			    				jrYdMsg = JDTORecordFactory.getInstance().create();
			    				jrYdMsg.setResultCode(logId);	//Log ID
			    				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			    				jrYdMsg.setField("WR_DT" 		, commUtils.getDateTime14()  ); 
			    				jrYdMsg.setField("STACK_COL_GP"	, ydUpWrLoc.substring(0, 6)     );
	
			    				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ009", jrYdMsg));
		    				}
		    			} 
					}	
						
					jrParam.setField("YD_CAR_SCH_ID" , ydCarSchId     ); 
					
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML008CarSchUdWbId 
					UPDATE TB_YD_CARSCH TS 
					   SET TS.MODIFIER             = :V_MODIFIER
					     , TS.MOD_DDTT             = SYSDATE
					     , TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML008CarSchUdWbId", logId, methodNm, "차량스케줄(하차) 수정");
	
					
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML008CarMtlDel 
					UPDATE TB_YD_CARFTMVMTL TM
					   SET TM.MODIFIER = :V_MODIFIER
					     , TM.MOD_DDTT = SYSDATE
					     , TM.DEL_YN   = 'Y'
					 WHERE TM.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					   AND TM.STOCK_ID  IN (SELECT B.STOCK_ID
					                          FROM TB_YM_CRNSCH A
					                             , TB_YM_CRNWRKMTL B
					                         WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID    
					                           AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					                           AND A.DEL_YN        = 'N'
					                           AND A.DEL_YN        = 'N')
					*/    	
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML008CarMtlDel", logId, methodNm, "차량이송재료 삭제");
					
					
					//회송테이블 완료 처리
					/* com.inisteel.cim.ym.bslab.dao.BSalbDAO.uptRetHtHistCmplDt 
					-- 회송이력 테이블 완료일자 셋팅
					UPDATE TB_YD_RETHTHIST
					   SET MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,YD_RETHT_CMPL_DT = SYSDATE
					      ,YD_RETHT_STAT_CD = '3'
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					   AND STOCK_ID IN (SELECT B.STOCK_ID
										  FROM TB_YM_CRNSCH A
					                         , TB_YM_CRNWRKMTL B
					                     WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID    
					                       AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					                       AND A.DEL_YN        = 'N'
					                       AND A.DEL_YN        = 'N'
					                       ) */
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSalbDAO.uptRetHtHistCmplDt", logId, methodNm, "회송테이블 완료 처리");
					
    				
    				/**********************************************************
    				* YDTSJ010 SLAB 하차완료 권상시 전송여부 체크
    				**********************************************************/
    				String sYDTSJ010_YN = "Y";
    				
    				jrParam.setField("REPR_CD_GP"	, "YM2003");
    				jrParam.setField("CD_GP"		, "2");
    				jrParam.setField("ITEM"			, "YDTSJ010");
    				jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "야드 기준 조회"); 
    				
    				if(jsChk.size() > 0) {
    					sYDTSJ010_YN = jsChk.getRecord(0).getFieldString("DTL_ITM1");
    				}
    				
    				if("Y".equals(sYDTSJ010_YN)) { 
    					
    					/* 하차완료 추가 
    					 */					
    					
    					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getA8YML008HaChaEndChk
    					 SELECT CASE WHEN CNT <= 1     THEN 'Y'  
    							     ELSE 'N' END                         AS HACHA_CHK
    					      , (SELECT YD_EQP_WRK_STAT 
    					           FROM TB_YD_CARSCH 
    					          WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID) AS YD_EQP_WRK_STAT
    					  FROM (
    					        SELECT COUNT(1)  AS CNT
    					          FROM TB_YD_CARSCH A
    					             , TB_YD_CARFTMVMTL B
    					         WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
    					           AND A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
    					           AND A.DEL_YN = 'N'
    					           AND B.DEL_YN = 'N'
    					       ) A   
    					 */
    					JDTORecordSet jsCarChk2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getA8YML008HaChaEndChk", logId, methodNm, "차량스케줄상태 조회");
    					
    					if (jsCarChk2.size() == 0) {
    						//크레인스케쥴 Table 존재유무 Check
    						ydL3HdRsCd = "UP11";
    						ydL3Msg = "오류:차량스케쥴 DB정보 없음";
    						commUtils.printLog(logId, methodNm + ydL3Msg, "SL");
    					} else {
    						
    						String CarHaChaEnd 	= commUtils.trim(jsCarChk2.getRecord(0).getFieldString("HACHA_CHK"     )); //하차완료 구분
    					
    						/***
    						 *  하차완료인 경우
    						 */
    						if ("Y".equals(CarHaChaEnd)){
    							
    							JDTORecord recPara = JDTORecordFactory.getInstance().create();
    							recPara.setResultCode(logId);	//Log ID
    							recPara.setResultMsg(methodNm);	//Log Method Name
    							recPara.setField("MODIFIER" 		, modifier     ); 
    							recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
    							recPara.setField("WR_DT" 			, currDt ); 

    							//하차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
    							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYDL009CarSchUd 
    							--크레인권하실적 하차 차량스케줄 수정 
    							UPDATE TB_YD_CARSCH
    							   SET MODIFIER         = :V_MODIFIER
    								  ,MOD_DDTT         = SYSDATE
    							      ,YD_EQP_WRK_STAT  = 'U' --공차
    							      ,YD_CAR_PROG_STAT = 'E' --하차완료
    							      ,YD_CARUD_CMPL_DT = NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
    							 WHERE YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
    							   AND DEL_YN           = 'N'
    							*/    
    							commDao.update(recPara, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYDL009CarSchUd", logId, methodNm, "하차 차량스케줄 수정  ");
    							
    			    			//하차작업완료 송신 YDTSJ010
    							if ( ydCarUseGp.equals("L") ) {
    								//하차작업완료 송신 YDTSJ010
    			    				jrYdMsg = JDTORecordFactory.getInstance().create();
    			    				jrYdMsg.setResultCode(logId);	//Log ID
    			    				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
    			    				jrYdMsg.setField("WR_DT"	        , currDt);
    			    				jrYdMsg.setField("YD_CAR_SCH_ID"	, ydCarSchId);
    			    				
    		        				//구내운송 소재차량하차완료
    								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ010_BSLAB", jrYdMsg));	
    							}
    						}
    					}
    				} //end of if("Y".equals(sYDTSJ010_YN)) {
				}
				
				//하차 차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드하차개시일시 수정
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getA8YML008CarSchUdSchId
				SELECT TS.YD_CAR_SCH_ID
				     , COUNT(ST.STOCK_ID)        AS YD_EQP_WRK_SH
				     , NVL(SUM(CC.SLAB_WT),0)    AS YD_EQP_WRK_WT
				     , CASE WHEN MIN(TS.YD_CAR_PROG_STAT) IN ('B','C') --하차도착,검수
				            THEN 'D'                                   --하차개시
				       ELSE MIN(TS.YD_CAR_PROG_STAT) END AS YD_CAR_PROG_STAT
				     , CASE WHEN MIN(TS.YD_CAR_PROG_STAT) IN ('B','C') --하차도착,검수
				            THEN NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
				       ELSE MIN(TS.YD_CARUD_ST_DT) END AS YD_CARUD_ST_DT
				  FROM TB_YD_CARSCH     TS
				     , TB_YD_CARFTMVMTL TM
				     , TB_YM_STOCK      ST
				     , VW_YD_SLABCOMM   CC
				 WHERE TS.YD_CAR_SCH_ID        = TM.YD_CAR_SCH_ID
				   AND TM.STL_NO               = ST.STOCK_ID
				   AND TM.STL_NO               = CC.SLAB_NO
				   AND TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				   AND TS.DEL_YN               = 'N'
				   AND TM.DEL_YN               = 'Y'
				 GROUP BY TS.YD_CAR_SCH_ID  
				 */
				JDTORecordSet jsCarSchChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getA8YML008CarSchUdSchId", logId, methodNm, "차량스케줄정보 조회");

				if (jsCarSchChk.size() == 0) {
					//크레인스케쥴 Table 존재유무 Check
					ydL3HdRsCd = "UP11";
					ydL3Msg = "오류:크레인스케쥴 DB정보 없음";
				} else {
					//크레인스케쥴 Table 야드작업진행상태 Check
					String ydEqpWrkSh  	= commUtils.trim(jsCarSchChk.getRecord(0).getFieldString("YD_EQP_WRK_SH")); 	//야드설비작업매수
					String ydEqpWrkWt  	= commUtils.trim(jsCarSchChk.getRecord(0).getFieldString("YD_EQP_WRK_WT"));     //야드설비작업중량
					String ydCarProgStat= commUtils.trim(jsCarSchChk.getRecord(0).getFieldString("YD_CAR_PROG_STAT")); 	//야드차량진행상태
					String ydCarupStDt  = commUtils.trim(jsCarSchChk.getRecord(0).getFieldString("YD_CARUD_ST_DT")); 	//야드하차개시일시
					
					jrParam.setField("YD_EQP_WRK_SH" 	, ydEqpWrkSh   ); 
					jrParam.setField("YD_EQP_WRK_WT" 	, ydEqpWrkWt   ); 
					jrParam.setField("YD_CAR_PROG_STAT" , ydCarProgStat); 
					jrParam.setField("YD_CARUD_ST_DT" 	, ydCarupStDt  ); 
					
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML008CarSchUdSchId 
					UPDATE USRYDA.TB_YD_CARSCH TS
					   SET TS.MODIFIER         = :V_MODIFIER
					     , TS.MOD_DDTT         = SYSDATE
					     , TS.YD_EQP_WRK_SH    = :V_YD_EQP_WRK_SH
					     , TS.YD_EQP_WRK_WT    = :V_YD_EQP_WRK_WT
					     , TS.YD_CAR_PROG_STAT = :V_YD_CAR_PROG_STAT
					     , TS.YD_CARUD_ST_DT   = :V_YD_CARUD_ST_DT
					 WHERE TS.YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID   
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML008CarSchUdSchId", logId, methodNm, "차량스케줄(하차) 수정");
				}	    
			}
			
			/**********************************************************
			* 6. 권하지시위치가 차량(상차)
			*    차량스케줄 야드차량진행상태가 상차도착(2) 또는 상차검수(3) 이면
			* 6.1 구내운송 소재차량상차개시(YMTSJ007) 전송
			*   - 야드차량사용구분이 구내운송(L)
			* 6.2 출하관리출하상차개시(YMDSJ006) 전송
			*   - 야드차량사용구분이 출하차량(G)
			* 6.3 차량스케줄 수정
			*   - 야드설비작업상태(U:공차), 야드차량진행상태(4:상차개시) 등 수정
			**********************************************************/
			if ("PT".equals(ydDnWoLoc.substring(2, 4))) {
				
				//차량하차스케줄 정보 조회
				JDTORecord recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" 	 , ydWbookId); 
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId); 
				recPara.setField("YD_EQP_ID" 	 , ydEqpId); 
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getAxYML008CarSchLd_PIDEV
				--크레인권상실적 상차 차량스케줄 조회 
				SELECT TS.YD_CAR_SCH_ID               --야드차량스케쥴ID
				      ,TS.YD_CAR_USE_GP               --야드차량사용구분
				      ,TS.TRN_EQP_CD                  --운송장비코드
				      ,TS.SPOS_WLOC_CD                --발지개소코드
				      ,SC.YD_PNT_CD AS SPOS_YD_PNT_CD --발지야드포인트코드
				      ,TS.CARD_NO                     --카드번호
				      ,TS.CAR_NO                      --차량번호
				      ,TS.TRANS_ORD_DATE              --운송작업지시일자
				      ,TS.TRANS_ORD_SEQNO             --운송작업지시순번
				      ,TS.CMBN_CARLD_YN               --복수동
				      ,TS.TRANS_EQUIPMENT_TYPE
				      ,TS.YD_CAR_PROG_STAT 
				  FROM TB_YM_CRNSCH   CS
				      ,TB_YM_STACKCOL SC
				      ,TB_YD_CARSCH   TS
				      ,(SELECT WM.YD_WBOOK_ID
				              ,WM.YD_AIM_YD_GP
				              ,MV.ARR_WLOC_CD
				          FROM TB_PT_STLFRTOMOVE MV
				              ,(SELECT WB.YD_WBOOK_ID
				                      ,WB.YD_AIM_YD_GP
				                      ,WM.STOCK_ID
				                  FROM TB_YM_WRKBOOK    WB
				                      ,TB_YM_WRKBOOKMTL WM
				                 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                   AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				                   AND WM.DEL_YN = 'N'
				                   AND ROWNUM = 1) WM
				         WHERE MV.STL_NO = WM.STOCK_ID
				           AND MV.TRANSWORD_SEQNO = (SELECT MAX(MM.TRANSWORD_SEQNO)
				                                       FROM TB_PT_STLFRTOMOVE MM
				                                      WHERE MM.STL_NO = MV.STL_NO)) MV
				 WHERE CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND CS.YD_WBOOK_ID   = MV.YD_WBOOK_ID(+)
				   AND SC.STACK_COL_GP = SUBSTR(CS.YD_DN_WO_LOC,1,6)
				   AND SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
				   AND ((SC.YD_CAR_USE_GP = 'L' AND SC.TRN_EQP_CD = TS.TRN_EQP_CD) --구내운송
				     OR (SC.YD_CAR_USE_GP = 'G' AND SC.CAR_NO = TS.CAR_NO)) --출하차량
				--   AND TS.YD_CAR_PROG_STAT IN ('2','3') --상차도착,검수
				   AND TS.DEL_YN = 'N'

				   */
				JDTORecordSet jsCarSch = commDao.select(recPara, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getAxYML008CarSchLd_PIDEV", logId, methodNm, "차량상차스케줄 정보 "); 
		    	
				if (jsCarSch.size() > 0) {
					
					JDTORecord jrCarSch = jsCarSch.getRecord(0);
					
					ydCarSchId			= commUtils.trim(jrCarSch.getFieldString("YD_CAR_SCH_ID")); 	//야드차량스케쥴
					String ydCarProgStat= commUtils.trim(jrCarSch.getFieldString("YD_CAR_PROG_STAT")); 	//차량진행상태
					String cmbnCarldYn  = commUtils.trim(jrCarSch.getFieldString("CMBN_CARLD_YN"));     //복수동
					String equipType 	= commUtils.trim(jrCarSch.getFieldString("TRANS_EQUIPMENT_TYPE")); //운송장비 TYPE P:PDA
					String arrWlocCd 	= commUtils.trim(jrCarSch.getFieldString("ARR_WLOC_CD"  	));
					String trnEqpCd 	= commUtils.trim(jrCarSch.getFieldString("TRN_EQP_CD"    	)); //운송장비코드
					String sposWlocCd 	= commUtils.trim(jrCarSch.getFieldString("SPOS_WLOC_CD"  	)); //발지개소코드
					String sposYdPntCd 	= commUtils.trim(jrCarSch.getFieldString("SPOS_YD_PNT_CD"	)); //발지야드포인트코드
					String ydCarUseGp 	= commUtils.trim(jrCarSch.getFieldString("YD_CAR_USE_GP"	)); //차량용도

					
					recPara.setField("YD_CAR_SCH_ID" , ydCarSchId);
					
					if ( (ydCarProgStat.equals("3") || ydCarProgStat.equals("2")) ) {
						
						/***
						 * 상차개시 처리
						 */
						jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name

						//전송할 전문에 추가
						recPara.setField("MODIFIER" 	, modifier     ); 
						recPara.setField("ARR_WLOC_CD"  , arrWlocCd); //착지개소코드
						recPara.setField("WR_DT" 		, commUtils.getDateTime14()     ); 
						//상차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
						
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML008CarSchLd 
						--크레인권상실적 상차 차량스케줄 수정 
						UPDATE TB_YD_CARSCH
						   SET MODIFIER             = :V_MODIFIER
							  ,MOD_DDTT             = SYSDATE
						      ,YD_EQP_WRK_STAT      = 'U' --공차
						      ,YD_CAR_PROG_STAT     = '4' --상차개시
						      ,ARR_WLOC_CD          = :V_ARR_WLOC_CD
						      ,YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
						      ,YD_CARLD_ST_DT       = NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
						 WHERE YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID
						   AND DEL_YN               = 'N'
						*/
						commDao.update(recPara, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML008CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");	
						
						if ("L".equals(ydCarUseGp)) {
							//구내운송 소재차량상차개시  
							jrYdMsg.setField("JMS_TC_CD"         , "YDTSJ007"	); //JMSTC코드
							jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    	); //JMSTC생성일시
							jrYdMsg.setField("TRN_EQP_CD"        , trnEqpCd  	); //운송장비코드
							jrYdMsg.setField("SPOS_WLOC_CD"      , sposWlocCd	); //발지개소코드
							jrYdMsg.setField("SPOS_YD_PNT_CD"    , sposYdPntCd	); //발지야드포인트코드
							jrYdMsg.setField("ARR_WLOC_CD"       , arrWlocCd	); //착지개소코드
							jrYdMsg.setField("TRN_WRK_ST_DT"     , currDt    	); //운송작업시작일시
							
							jrRtn = commUtils.addSndData(jrRtn	 , jrYdMsg);
						}	
					} 
				}
			}
			
			/**********************************************************
			* 7. 권상위치가 보온뱅크(BK)이고 권하지시위치가 보온뱅크(BK)가 아니면
			*    보온뱅크 추출시간 UPDATE
			**********************************************************/
			if ("BK".equals(ydUpWrLoc.substring(2, 4)) && !"BK".equals(ydDnWoLoc.substring(2, 4))) {
				
				bSlabComm.updMSlabCommBkTimeEnd(rcvMsg); //야드크레인스케줄ID 전달
			}
			
			/**********************************************************
			* 8. 설비, 크레인스케쥴, 적치단, 적치Bed 수정
			* 8.1 설비 야드설비상태(권상완료:2) 수정
			* 8.2 적치단
			*   - 크레인 재료정보 등록
			*   - 권상위치 재료정보 삭제
			* 8.3 크레인스케쥴 권상실적 수정
			**********************************************************/
			//Crane 설비상태 권상완료(2)으로 수정
			jrParam.setField("YD_EQP_ID"       	, ydEqpId     ); //야드설비ID
			jrParam.setField("YD_EQP_STAT"     	, "2"         ); //야드설비상태(권상완료:2)
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp 
			--설비 상태 수정 
			UPDATE TB_YM_EQUIP
			   SET MODIFIER    = :V_MODIFIER
			      ,MOD_DDTT    = SYSDATE
			      ,WPROG_STAT = :V_YD_EQP_STAT
			 WHERE EQUIP_GP    = :V_YD_EQP_ID
			   AND DEL_YN      = 'N'
			*/	   
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp", logId, methodNm, "설비상태 권상완료(2)으로 수정");
			
			//야드권상작업수행구분
			String ydUpWrkActGp = ydEqpWrkMode;

			if ("0".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "M"; //Manual
			} else if ("1".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "A"; //Auto
			} else if ("9".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "B"; //Backup
			}
			
			//크레인스케쥴 수정
			jrParam.setField("YD_UP_CMPL_DT"   	, currDt      ); //야드권상완료일시
			jrParam.setField("YD_UP_WR_LOC"    	, ydUpWrLoc   ); //야드권상실적위치
			jrParam.setField("YD_UP_WR_LAYER"  	, ydUpWrLayer ); //야드권상실적단
			jrParam.setField("YD_UP_WRK_ACT_GP"	, ydUpWrkActGp); //야드권상작업수행구분
			jrParam.setField("YD_UP_WR_XAXIS"  	, ydCrnXaxis  ); //야드권상실적X축
			jrParam.setField("YD_UP_WR_YAXIS"  	, ydCrnYaxis  ); //야드권상실적Y축
			jrParam.setField("YD_UP_WR_ZAXIS"  	, ydCrnZaxis  ); //야드권상실적Z축
			jrParam.setField("STACK_COL_GP"     , ydUpWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("YD_UP_WRK_MODE2"  , ydEqpWrkMode2); //자동모드
			jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId); //야드크레인스케쥴ID
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML008CrnSch
			-- 크레인권상실적 크레인스케줄 수정 
			UPDATE TB_YM_CRNSCH
			   SET MODIFIER         = :V_MODIFIER
			      ,MOD_DDTT         = SYSDATE
			      ,YD_WRK_PROG_STAT = '2' --권상완료
			      ,YD_UP_CMPL_DT    = TO_DATE(:V_YD_UP_CMPL_DT,'YYYYMMDDHH24MISS')
			      ,YD_UP_WR_LOC     = :V_YD_UP_WR_LOC
			      ,YD_UP_WR_LAYER   = :V_YD_UP_WR_LAYER
			      ,YD_UP_WRK_ACT_GP = :V_YD_UP_WRK_ACT_GP
			      ,YD_UP_WR_XAXIS   = TO_NUMBER(:V_YD_UP_WR_XAXIS)
			      ,YD_UP_WR_YAXIS   = TO_NUMBER(:V_YD_UP_WR_YAXIS)
			      ,YD_UP_WR_ZAXIS   = TO_NUMBER(:V_YD_UP_WR_ZAXIS)
			      ,YD_UP_WRK_MODE2  = :V_YD_UP_WRK_MODE2
			 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML008CrnSch", logId, methodNm, "크레인스케쥴 수정");
			
			//적치단 - 권상위치 수정
			if ("PT".equals(ydUpWrLoc.substring(2, 4))) {
				//권상위치가 차량 포인트 일 경우
				jrParam.setField("STOCK_ID", ""); //저장품ID
				jrParam.setField("STACK_LAYER_STAT", "E"); //적치단 상태
				jrParam.setField("STACK_LAYER_ACTIVE_STAT", "C"); //적치단 활성상태
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrStackLayerActiveStat  
				--크레인권상실적 적치단(권상위치) + 적치단 활성상태 수정  
				UPDATE TB_YM_STACKLAYER 
				   SET MODIFIER            = :V_MODIFIER
				     , MOD_DDTT            = SYSDATE
				     , STOCK_ID            = :V_STOCK_ID
				     , STACK_LAYER_STAT    = :V_STACK_LAYER_STAT
				     , STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT
				 WHERE STOCK_ID IN (SELECT STOCK_ID FROM TB_YM_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)    
				   AND STACK_LAYER_STAT    IN ('C', 'U') */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrStackLayerActiveStat", logId, methodNm, "적치단 - 권상위치 수정 - 차량 포인트 일 경우");
				
			} else {
				//일반 야드 일 경우
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML008UpStkLyr  
				--크레인권상실적 적치단(권상위치)  
				UPDATE TB_YM_STACKLAYER 
				   SET MODIFIER            = :V_MODIFIER
				     , MOD_DDTT            = SYSDATE
				     , STOCK_ID            = NULL
				     , STACK_LAYER_STAT = 'E'
				 WHERE STOCK_ID IN (SELECT STOCK_ID FROM TB_YM_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)    
				   AND STACK_LAYER_STAT    IN ('C', 'U') */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML008UpStkLyr", logId, methodNm, "적치단 - 권상위치 수정 - 일반 야드 일 경우 ");
			}
			
			//권상위치가 WB 인 경우 (Take Out) - Tracking Table Clear
			if ("WB".equals(ydUpWrLoc.substring(2, 4))) {
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updTrackingWBClear 
				UPDATE TB_YM_EQPTRACKING
				   SET MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,STL_NO   = ''
				 WHERE YD_GP    = '2'
				   AND PROC_GP  = 'W'
				   AND STL_NO IN (SELECT STOCK_ID FROM TB_YM_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID) */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updTrackingWBClear", logId, methodNm, "WB Tracking 테이블 권상위치 Clear ");
			}

			//권상위치가 CTC 인 경우 (Take Out) - Tracking Table Clear
			if ("CT".equals(ydUpWrLoc.substring(2, 4))) {
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updTrackingCTCClear 
				UPDATE TB_YM_EQPTRACKING
				   SET MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,STL_NO   = ''
				 WHERE YD_GP    = '2'
				   AND PROC_GP  = 'C'
				   AND STL_NO IN (SELECT STOCK_ID FROM TB_YM_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID) */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updTrackingCTCClear", logId, methodNm, "CTC Tracking 테이블 권상위치 Clear ");
			}
			
			//적치단 - 크레인적치단 수정
			jrParam.setField("YD_EQP_ID"	, ydEqpId); 
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); 
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrCrnLoc
			--크레인권상실적 적치단(크레인,권상실적) 수정
			MERGE INTO TB_YM_STACKLAYER SL USING (

			SELECT A.* 
			       , TRIM(TO_CHAR(ROWNUM,'FM00'))  AS STACK_LAYER_GP
			  FROM (
			            SELECT :V_YD_EQP_ID     AS STACK_COL_GP 
			                 , '01'             AS STACK_BED_GP
			                 , STACK_LAYER_GP   AS STACK_LAYER_GP_FROM
			                 , A.STOCK_ID
			                 , 'C'              AS STACK_LAYER_STAT
			              FROM TB_YM_CRNWRKMTL A
			                 , TB_YM_CRNSCH    B
			             WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			               AND A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
			               AND A.DEL_YN = 'N'
			               AND B.DEL_YN = 'N'
			             ORDER BY STACK_LAYER_GP
			       )   A  

			) DD ON (SL.STACK_COL_GP = DD.STACK_COL_GP AND SL.STACK_BED_GP = DD.STACK_BED_GP AND SL.STACK_LAYER_GP = DD.STACK_LAYER_GP)
			WHEN MATCHED THEN UPDATE SET
				 SL.MODIFIER         = :V_MODIFIER
			    ,SL.MOD_DDTT         = SYSDATE
			    ,SL.STOCK_ID         = DD.STOCK_ID
			    ,SL.STACK_LAYER_STAT = DD.STACK_LAYER_STAT */
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrCrnLoc", logId, methodNm, "크레인권상실적 적치단(크레인,권상실적) 수정 ");

			///**********************************************************
			//* 8. 장입 보급 순서 초기화
			//* 8.1 W/B보급, CTC보급 스케줄에서 권하위치가 W/B,CTC일 경우
			//*   - STOCK의 CHARGE_LOT_NO를 Clear
			//*   - CTS_RELAY_SADDLE 에 장입순번 설정
			//*   - 생산통제 장입진행실적 송신 - YDCTJ032 
			//**********************************************************/
			//if(("2CWB01UM".equals(ydSchCd)&&"WB".equals(ydDnWoLoc.substring(2,4))) ||   //C동 W/B보급
			//   ("2ACT01UM".equals(ydSchCd)&&"CT".equals(ydDnWoLoc.substring(2,4))) ||   //A동 CTC보급
			//   ("2CCT01UM".equals(ydSchCd)&&"CT".equals(ydDnWoLoc.substring(2,4))) ) {  //C동 CTC보급
			//	
			//	//저장품 - CTS_RELAY_SADDLE 에 장입순번 설정 후 STOCK의 CHARGE_LOT_NO를 Clear
			//	jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); 
			//	/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockChargeLotNoClear 
			//	--CHARGE_LOT_NO 항목 CLEAR
			//	MERGE INTO TB_YM_STOCK ST USING (
            //
			//	    SELECT A.STOCK_ID
			//	          ,C.CHARGE_LOT_NO
			//	          ,C.CTS_RELAY_SADDLE
			//	      FROM TB_YM_CRNWRKMTL A
			//	         , TB_YM_CRNSCH    B
			//	         , TB_YM_STOCK     C
			//	     WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			//	       AND A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
			//	       AND A.DEL_YN = 'N'
			//	       AND B.DEL_YN = 'N'
			//	       AND A.STOCK_ID = C.STOCK_ID
			//	       
			//	) DD ON (ST.STOCK_ID = DD.STOCK_ID )
			//	WHEN MATCHED THEN UPDATE SET
			//		 ST.MODIFIER         = :V_MODIFIER
			//	    ,ST.MOD_DDTT         = SYSDATE
			//	    ,ST.CHARGE_LOT_NO    = ''
			//	    ,ST.CTS_RELAY_SADDLE = DD.CHARGE_LOT_NO */
			//	commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockChargeLotNoClear", logId, methodNm, "저장품 - CTS_RELAY_SADDLE 에 장입순번 설정 후 STOCK의 CHARGE_LOT_NO를 Clear ");
			//	
			//	//생산통제 장입진행실적 송신 - YDCTJ032 
			//	jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); 
			//	jrParam.setField("CHG_SUP_PROG_STAT", YmConstant.TC_YMPC020); //장입보급진행상태
			//	
			//	jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDCTJ032", jrParam));
			//} 
		
			//이송하차인 경우
			if ("PT02LM".equals(ydSchCd.substring(2, 8))) {
				//차량하차스케줄 정보 조회
				JDTORecord recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId); 
				recPara.setField("YD_WBOOK_ID" 	 , ydWbookId); 
				recPara.setField("YD_EQP_ID" 	 , ydEqpId); 
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnSchMultiCheck 

				SELECT A.YD_CRN_SCH_ID
				     , A.YD_WRK_PROG_STAT
				     , A.YD_UP_WO_LOC 
				     , A.YD_DN_WO_LOC 
				     , A.YD_SCH_CD
				     , A.YD_SCH_PRIOR
				     , A.SEQ1
				     , A.YD_SCH_ST_GP
				     , A.YD_WRK_PLAN_CRN
				     , A.YD_WRK_PLAN_CRN2
				     , A.OTHER_CRN_CNT 
				     , A.OTHER_BRA_CNT
				     -- 이송하차이고 
				     -- 건수가 2건이고 
				     -- 현재상태도 멀티작업이면
				     -- 상대편 크레인 작업이 없고 
				     -- 스케쥴 기동구분이 'N'(멀티) 인 경우
				     -- 동일작업이 2건이면 멀티작업가능
				     , CASE WHEN SUBSTR(A.YD_SCH_CD,3,6) = 'PT02LM' 
				             AND SUM(1) OVER () = 2 
				             AND (SELECT YD_MULTI_WRK_YN
				                    FROM USRYMA.TB_YM_SCHEDULERULE
				                   WHERE YD_SCH_CD = A.YD_SCH_CD 
				                     AND ROWNUM = 1) = 'Y'
				             AND A.OTHER_CRN_CNT  = 0 
				             AND A.OTHER_BRA_CNT  = 0
				             AND A.YD_SCH_ST_GP   = 'N'
				             AND A.YD_UP_WO_LOC   = LEAD(A.YD_UP_WO_LOC) OVER (ORDER BY A.SEQ1 DESC, A.YD_SCH_PRIOR, A.YD_CRN_SCH_ID) 
				             AND A.YD_SCH_CD      = LEAD(A.YD_SCH_CD)    OVER (ORDER BY A.SEQ1 DESC, A.YD_SCH_PRIOR, A.YD_CRN_SCH_ID) THEN 'Y'
				            ELSE 'N' END MULTI_YN
				  FROM
				       ( SELECT CS.YD_CRN_SCH_ID
				              , CS.YD_WRK_PROG_STAT
				              , CS.YD_UP_WO_LOC 
				              , CS.YD_DN_WO_LOC 
				              , CS.YD_SCH_CD
				              , CS.YD_SCH_PRIOR
				              , DECODE(CS.YD_WRK_PROG_STAT,'W','0','S','1', CS.YD_WRK_PROG_STAT) AS SEQ1
				              , WB.YD_SCH_ST_GP
				              , WB.YD_WRK_PLAN_CRN
				              , WB.YD_WRK_PLAN_CRN2
				              --상대크레인 작업 건수
				              , (SELECT COUNT(*) 
				                   FROM TB_YM_CRNSCH 
				                  WHERE DEL_YN = 'N'
				                    AND YD_EQP_ID = WB.YD_WRK_PLAN_CRN2)       AS OTHER_CRN_CNT
				              , (SELECT COUNT(*)
				                   FROM TB_YM_EQUIP 
				                  WHERE EQUIP_GP = WB.YD_WRK_PLAN_CRN2
				                    AND (WPROG_STAT = 'B' OR WORK_MODE = '2')) AS OTHER_BRA_CNT -- 고장여부
				           FROM TB_YM_CRNSCH CS
				              , TB_YM_WRKBOOK WB
				          WHERE CS.YD_WBOOK_ID = WB.YD_WBOOK_ID
				            AND CS.DEL_YN = 'N'
				            AND WB.DEL_YN = 'N'
				            AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				            AND CS.YD_EQP_ID   = :V_YD_EQP_ID 
				          ORDER BY SEQ1 DESC, YD_SCH_PRIOR, YD_CRN_SCH_ID
				       ) A   
				 WHERE ROWNUM <= 2
				*/ 
				JDTORecordSet jsCarMulti = commDao.select(recPara, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnSchMultiCheck", logId, methodNm, "차량상차스케줄 정보 "); 
		    	
				if (jsCarMulti.size() == 2) {

					/**********************************************************
					* 멀티여부 CHECK
					**********************************************************/	

					String multiYn      	= commUtils.trim(jsCarMulti.getRecord(0).getFieldString("MULTI_YN"));
					String otherYdEqpId 	= commUtils.trim(jsCarMulti.getRecord(0).getFieldString("YD_WRK_PLAN_CRN2")); //상대크레인
					String otherYdCrnSchId 	= commUtils.trim(jsCarMulti.getRecord(1).getFieldString("YD_CRN_SCH_ID"));    //작업지시

					commUtils.printLog(logId, "멀티작업여부 [" + multiYn + " ]", "SL");					
					if(multiYn.equals("Y")){
						/**********************************************************
						* 멀티트레인 설비상태 Check
						* 멀티크레인이 고장 이나 off-line 일경우 편성 안함
						**********************************************************/
						//DAO Parameter - Log ID, Method, 수정자 Set
						JDTORecord jrEqpParam = commUtils.getParam(logId, methodNm, "");

						jrEqpParam.setField("YD_EQP_ID", otherYdEqpId); //야드설비ID

						JDTORecord jrChk = ymComm.chkEqpStat(jrEqpParam);

						String ydEqpL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));
						commUtils.printLog(logId,  "==========[[[ SLAB 멀티크레인 상태 여부:" + ydEqpL3Msg + " ]]]============", "SL");
						
						if ("".equals(ydEqpL3Msg)) {
							
							recPara.setField("MSG_GP"		, "I"); 			//전문구분 - 신규
							recPara.setField("YD_EQP_STAT"	, "1"); 			//권상작업지시
							recPara.setField("YD_EQP_ID"	, otherYdEqpId); 	//상대크레인
							recPara.setField("YD_CRN_SCH_ID", otherYdCrnSchId); //상대크레인작업지시
		
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp 
							--설비 상태 수정 
							UPDATE TB_YM_EQUIP
							   SET MODIFIER    = :V_MODIFIER
							      ,MOD_DDTT    = SYSDATE
							      ,WPROG_STAT = :V_YD_EQP_STAT
							 WHERE EQUIP_GP    = :V_YD_EQP_ID
							   AND DEL_YN      = 'N'
							*/	   
			        		commDao.update(recPara, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp", logId, methodNm, "B열연SLAB설비상태 수정");
		
			        		//크레인스케줄 야드작업진행상태 수정
			        		recPara.setField("YD_WRK_PROG_STAT"	 , "S"); //선택지시
			        		recPara.setField("YD_L2_REQUEST_STAT", "1");
							
							/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStatCrnSchWrkProg 
							--크레인스케줄 작업진행상태 수정
							UPDATE TB_YM_CRNSCH
							   SET MODIFIER         = :V_MODIFIER
							      ,MOD_DDTT         = SYSDATE
							      ,YD_WRK_PROG_STAT = :V_YD_WRK_PROG_STAT
							      ,YD_WORD_DT       = DECODE(:V_YD_WRK_PROG_STAT,'S',SYSDATE,'1',SYSDATE,'W',NULL,YD_WORD_DT)
							      ,YD_L2_REQUEST_STAT = NVL(:V_YD_L2_REQUEST_STAT,YD_L2_REQUEST_STAT) 
							      ,YD_EQP_ID        = :V_YD_EQP_ID
							 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
							   AND DEL_YN           = 'N'
							*/
			        		commDao.update(recPara, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStatCrnSchWrkProg", logId, methodNm, "B열연SLAB크레인스케줄 야드작업진행상태 수정");
							
			        		JDTORecord jrYdMsg1 = commUtils.getParam(logId, methodNm, "A8YML007");
	
							jrYdMsg1.setField("JMS_TC_CD"       , YmConstant.A8YML007);	//크레인작업지시요구
							jrYdMsg1.setField("YD_EQP_ID"       , otherYdEqpId);	//야드설비ID
							jrYdMsg1.setField("YD_WRK_PROG_STAT", "4"         );	//야드작업진행상태(권하완료)
							jrYdMsg1.setField("YD_CRN_SCH_ID"   , otherYdCrnSchId);	//야드크레인스케쥴ID
	
							EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
							JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("rcvA8YML007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg1 });
							jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
							commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 멀티작업지시 전송 [ " + otherYdEqpId + " : " + ydWrkProgStat +  " - " + otherYdCrnSchId + " ]", "SL");
							
							sD3Flag = "1"; //D동이송하차 D2+D3 작업으로 D2권상후 D3 작업지시 전송한 경우 "1"로 셋팅함.
						}//"".equals(ydEqpL3Msg) end
					}//multiYn.equals("Y")
				} 
			}
			
			/**********************************************************
			* 권상시 하단에 동일 작업 대기에 걸려 HOLDING 된 크레인 기동처리
			**********************************************************/	
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getEqpStatChk 
			SELECT YD_EQP_ID
			     , YD_WRK_PROG_STAT
			     , YD_SCH_CD
			     , CASE WHEN YD_WRK_PROG_STAT = 'W' THEN 'Y'
			            ELSE 'N' END SCH_YN
			  FROM (
			        SELECT A.YD_EQP_ID
			             , A.YD_WRK_PROG_STAT
			             , A.YD_SCH_CD
			          FROM TB_YM_CRNSCH A 
			             , (  
			                SELECT A.YD_EQP_ID
			                  FROM TB_YM_CRNSCH A
			                 WHERE A.YD_UP_WO_LOC = :V_YD_UP_WR_LOC
			                   AND A.DEL_YN = 'N'
			                   AND A.YD_EQP_ID <> :V_YD_EQP_ID
			                   AND ROWNUM = 1
			               ) B
			         WHERE DEL_YN = 'N'
			           AND A.YD_EQP_ID = B.YD_EQP_ID
			         ORDER BY CASE WHEN YD_WRK_PROG_STAT = 'W' THEN -1  
			                       WHEN YD_WRK_PROG_STAT = 'S' THEN  0  
			                       ELSE TO_NUMBER(YD_WRK_PROG_STAT) END DESC
			       )
			 WHERE ROWNUM  = 1      
   
			*/ 
			JDTORecordSet jsEqpStatChk = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getEqpStatChk", logId, methodNm, "설비 상태(대기) 검색 ");
			if(jsEqpStatChk.size() > 0 ) {
				String ydEqpIdStatW  = commUtils.trim(jsEqpStatChk.getRecord(0).getFieldString("YD_EQP_ID")); //대기 상태 크레인 
				String ydSchYn       = commUtils.trim(jsEqpStatChk.getRecord(0).getFieldString("SCH_YN"));    //명령선택 여부
				String ydSchCdStatW  = commUtils.trim(jsEqpStatChk.getRecord(0).getFieldString("YD_SCH_CD")); //야드스케쥴코드
	

				if("Y".equals(ydSchYn)) {
					/**********************************************************
					* HOLDING 된 크레인 기동처리상태 Check
					* HOLDING 된 크레인이 고장 이나 off-line 일경우 자동편성 안함
					**********************************************************/
					//DAO Parameter - Log ID, Method, 수정자 Set
					JDTORecord jrEqpParam = commUtils.getParam(logId, methodNm, "");

					jrEqpParam.setField("YD_EQP_ID", ydEqpIdStatW); //야드설비ID

					JDTORecord jrChk = ymComm.chkEqpStat(jrEqpParam);

					String ydEqpL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));
					commUtils.printLog(logId,  "==========[[[ SLAB HOLDING 된 크레인 상태 여부:" + ydEqpL3Msg + " ]]]============", "SL");
					
					if ("".equals(ydEqpL3Msg)) {
						JDTORecord jrYdMsg1 = JDTORecordFactory.getInstance().create();
						jrYdMsg1.setResultCode(logId);	//Log ID
						jrYdMsg1.setResultMsg(methodNm);	//Log Method Name

						jrYdMsg1.setField("JMS_TC_CD"			, "A8YML007"  ); //JMSTC코드
						jrYdMsg1.setField("YD_EQP_ID"       	, ydEqpIdStatW); //야드설비ID
						jrYdMsg1.setField("YD_WRK_PROG_STAT"	, "4"      	  ); //야드작업진행상태(권하완료)
						jrYdMsg1.setField("YD_SCH_CD"       	, ydSchCdStatW); //야드스케쥴코드
						jrYdMsg1.setField("MODIFIER"        	, modifier    ); //수정자
						jrRtn = commUtils.addSndData(jrRtn, this.rcvA8YML007(jrYdMsg1));
					}
					
				}
				
				
			}
			
			
///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
// SLAB 고도화 - START			
			
			if("2DPT02LM".equals(ydSchCd)) { //D동 이송하차
				if("D2".equals(ydEqpId.substring(4,6)) && "".equals(sD3Flag)) {
					
					String sAPP100_D3_ADV_TYP = ymComm.BCoilApplyYn("APP100","2","D3_ADV_TYP");
					
					if("N".equals(sAPP100_D3_ADV_TYP)) {
				
						String sAPP100_D3_ADV_YN = ymComm.BCoilApplyYn("APP100","2","D3_ADV_YN");  
						
						commUtils.printLog(logId,  "==========[[[ SLAB D동 D3 고도화 적용여부 :" + sAPP100_D3_ADV_YN + " ]]]============", "SL");
					
						if("Y".equals(sAPP100_D3_ADV_YN)) {
							
							/**********************************************************
							* D3 설비상태 Check
							* D3크레인이 고장 이나 off-line 일경우 자동편성 안함
							**********************************************************/
							//DAO Parameter - Log ID, Method, 수정자 Set
							JDTORecord jrEqpParam = commUtils.getParam(logId, methodNm, "");
	
							jrEqpParam.setField("YD_EQP_ID", "2DCRD3"); //야드설비ID
	
							JDTORecord jrChk = ymComm.chkEqpStat(jrEqpParam);
	
							String ydEqpL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));
							commUtils.printLog(logId,  "==========[[[ SLAB D동 D3 고도화 적용시 D3크레인 상태 여부:" + ydEqpL3Msg + " ]]]============", "SL");
							
							
							if ("".equals(ydEqpL3Msg)) {
								
								JDTORecord jrYdMsg1 = JDTORecordFactory.getInstance().create();
								jrYdMsg1.setResultCode(logId);	//Log ID
								jrYdMsg1.setResultMsg(methodNm);	//Log Method Name
		
								jrYdMsg1.setField("JMS_TC_CD"			, "A8YML007"  ); //JMSTC코드
								jrYdMsg1.setField("YD_EQP_ID"       	, "2DCRD3"); //야드설비ID D동 3호기
								jrYdMsg1.setField("YD_WRK_PROG_STAT"	, "4"      	  ); //야드작업진행상태(권하완료)
								jrYdMsg1.setField("YD_SCH_CD"       	, ""); //야드스케쥴코드
								jrYdMsg1.setField("MODIFIER"        	, modifier    ); //수정자
								jrYdMsg1.setField("APP100_D3_ADV_YN"    , "SKIP"); // 고도화 로직 SIKP 하고 일반 로직 수행지정
								jrRtn = commUtils.addSndData(jrRtn, this.rcvA8YML007(jrYdMsg1));
							}
						}
					}
				}
			} else if("2CWB01UM".equals(ydSchCd)) { //C동 W/B 보급
				if("01".equals(ydUpWrLayer) || ("02".equals(ydUpWrLayer) && "2".equals(sYD_EQP_WRK_SH)) ) {
					
					String sAPP103_2_ADV_YN = ymComm.BCoilApplyYn("APP103","2","1_ADV_YN");  
					
					commUtils.printLog(logId,  "==========[[[ SLAB 장입대상재 동간이적 자동 편성여부 :" + sAPP103_2_ADV_YN + " ]]]============", "SL");
					
					if("Y".equals(sAPP103_2_ADV_YN)) {
						
						JDTORecord jrYMYMJ207 = JDTORecordFactory.getInstance().create();
						jrYMYMJ207.setResultCode(logId);	//Log ID
						jrYMYMJ207.setResultMsg(methodNm);	//Log Method Name
						
						jrYMYMJ207.setField("JMS_TC_CD"				, "YMYMJ207");
						jrYMYMJ207.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
						jrYMYMJ207.setField("YD_CRN_SCH_ID"			, ydCrnSchId); 
						jrRtn = commUtils.addSndData(jrRtn, jrYMYMJ207);	
					}
				}
//				if("C2".equals(ydEqpId.substring(4,6))) {
//					
//					String sAPP100_C1_ADV_YN = ymComm.BCoilApplyYn("APP100","2","C1_ADV_YN");  
//					
//					commUtils.printLog(logId,  "==========[[[ SLAB C동 C1 고도화 적용여부 :" + sAPP100_C1_ADV_YN + " ]]]============", "SL");
//				
//					if("Y".equals(sAPP100_C1_ADV_YN)) {
//						
//						JDTORecord jrYdMsg1 = JDTORecordFactory.getInstance().create();
//						jrYdMsg1.setResultCode(logId);	//Log ID
//						jrYdMsg1.setResultMsg(methodNm);	//Log Method Name
//
//						jrYdMsg1.setField("JMS_TC_CD"			, "A8YML007"  ); //JMSTC코드
//						jrYdMsg1.setField("YD_EQP_ID"       	, "2CCRC1"); //야드설비ID D동 3호기
//						jrYdMsg1.setField("YD_WRK_PROG_STAT"	, "4"      	  ); //야드작업진행상태(권하완료)
//						jrYdMsg1.setField("YD_SCH_CD"       	, ""); //야드스케쥴코드
//						jrYdMsg1.setField("MODIFIER"        	, modifier    ); //수정자
//						jrYdMsg1.setField("APP100_C1_ADV_YN"    , ""); // 고도화 로직 수행
//						jrRtn = commUtils.addSndData(jrRtn, this.rcvA8YML007(jrYdMsg1));
//					}
//				}
				
			}
			
// SLAB 고도화 - END			
///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
			
			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
			}

			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (Exception e) {
			if (resYn) {
				try {
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YmCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { bSlabComm.getYMA8L005(resMsg)});
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
			
	/**
	 *      [A] 오퍼레이션명 : 크레인권하실적(A8YML009)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML009(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인권하실적[BSlabL2RcvSeEJB.rcvA8YML009] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
	    JDTORecord jrParam	 = JDTORecordFactory.getInstance().create(); //Query 실행시 파라메터 전달용 JDTORecord 
		
	    JDTORecordSet rsResult 	= null;
	    JDTORecordSet jsChk = null;
	    JDTORecord jrChk = null;
	    
		//송신용 
		JDTORecord jrYdMsg;
	    
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecord resMsg 	= JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn 		= true;	//크레인작업실적응답 전문 전송여부

		String ydL3HdRsCd = "";		//야드L3처리결과코드
		String ydL3Msg    = ""; 	//야드L3MESSAGE
		
		String ydWbookId     = ""; //야드작업예약ID
		String ydUpWrLoc     = ""; //야드권상실적위치
		String ydEqpWrkSh    = ""; //작업매수
		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "크레인권하실적(A8YML009) 수신 ", rcvMsg);

			//수신 항목 값
			String msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"A8YML009"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태(4:권하완료, 5:강제권하)
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydDnWrLoc     = commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LOC"    )); //야드권하실적위치
			String ydDnWrLayer   = commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LAYER"  )); //야드권하실적단
			if (ydDnWrLayer.length() == 3) {
				//L2 수신 (L2에서 3자리로 전송)
				ydDnWrLayer = ydDnWrLayer.substring(1,3); //"002" --> "02"
			}
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축	
			String ydEqpWrkMode2 = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE2")); //야드설비작업모드2(A:무인자동,R:리모컨,E:정비,M:유인)
			
			String modifier 	 = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
			String tcarLdCmplYn  = "" ;                                                      //대차작업완료여부
			
			commUtils.printLog("", "■■■ YD_EQP_ID        = " + ydEqpId      , "[info]");
			commUtils.printLog("", "■■■ YD_EQP_WRK_MODE  = " + ydEqpWrkMode , "[info]");
			commUtils.printLog("", "■■■ YD_WRK_PROG_STAT = " + ydWrkProgStat, "[info]");
			commUtils.printLog("", "■■■ YD_SCH_CD        = " + ydSchCd      , "[info]");
			commUtils.printLog("", "■■■ YD_CRN_SCH_ID    = " + ydCrnSchId   , "[info]");
			commUtils.printLog("", "■■■ YD_DN_WR_LOC     = " + ydDnWrLoc    , "[info]");
			commUtils.printLog("", "■■■ YD_DN_WR_LAYER   = " + ydDnWrLayer  , "[info]");
			commUtils.printLog("", "■■■ YD_CRN_XAXIS     = " + ydCrnXaxis   , "[info]");
			commUtils.printLog("", "■■■ YD_CRN_YAXIS     = " + ydCrnYaxis   , "[info]");
			commUtils.printLog("", "■■■ YD_CRN_ZAXIS     = " + ydCrnZaxis   , "[info]");
			commUtils.printLog("", "■■■ YD_EQP_WRK_MODE2 = " + ydEqpWrkMode2, "[info]");
			 		  
			if ("".equals(modifier)) { modifier = msgId; }
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
			
			//크레인작업실적응답 전문 생성용
			resMsg 	= JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "D"          ); //야드L2실적구분(권상실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권하실적 수신처리"); //야드L3MESSAGE(Error)
			
			
			/**********************************************************
			* 1. 베드 L2 실좌표 값으로 설정 작업(1,2단인 경우)
			**********************************************************/
			String sAPP110_YN = ymComm.BCoilApplyYn("APP110","2",ydEqpId.substring(1 , 2));   
			commUtils.printLog(logId,  "실좌표적용여부:" + sAPP110_YN, "SL");	
			if(sAPP110_YN.equals("Y") ) {
			
				//1단,2단 인경우 지시 실 좌표 기준으로 X,Y,Z 값 변경
				if("01".equals(ydDnWrLayer) || "02".equals(ydDnWrLayer)){
					commUtils.printLog("", "■■■> YD_DN_WR_LOC     = " + ydDnWrLoc    , "[info]");
					commUtils.printLog("", "■■■> YD_DN_WR_LAYER   = " + ydDnWrLayer  , "[info]");
					commUtils.printLog("", "■■■> YD_CRN_XAXIS     = " + ydCrnXaxis   , "[info]");
					commUtils.printLog("", "■■■> YD_CRN_YAXIS     = " + ydCrnYaxis   , "[info]");
					commUtils.printLog("", "■■■> YD_CRN_ZAXIS     = " + ydCrnZaxis   , "[info]");
					
					jrParam.setField("YD_STK_BED_XAXIS_REAL"    	, ydCrnXaxis	);
					jrParam.setField("YD_STK_BED_YAXIS_REAL"    	, ydCrnYaxis	);
					jrParam.setField("YD_STK_BED_ZAXIS_REAL"		, ydCrnZaxis	);
					jrParam.setField("STACK_COL_GP" 				, ydDnWrLoc.substring(0, 6)); //야드적치열구분
					jrParam.setField("STACK_BED_GP" 				, ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
					
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updXYZByBedchange
					UPDATE TB_YM_STACKER
					   SET 
					       MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,YD_STK_BED_XAXIS_REAL = :V_YD_STK_BED_XAXIS_REAL
					      ,YD_STK_BED_YAXIS_REAL = :V_YD_STK_BED_YAXIS_REAL
					      ,YD_STK_BED_ZAXIS_REAL = :V_YD_STK_BED_ZAXIS_REAL
					 WHERE  STACK_COL_GP = :V_STACK_COL_GP
					   AND  STACK_BED_GP = :V_STACK_BED_GP */
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updXYZByBedchange", logId, methodNm, "베드 실좌표 설정");
					
				}
			}

			// SJH 				
			/**********************************************************
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStatCrnSch  
			--B열연 SLAB 크레인스케줄상태 조회 
			SELECT CS.* 
			     , (SELECT YD_EQP_WRK_MODE2 FROM USRYMA.TB_YM_EQUIP WHERE EQUIP_GP = CS.YD_EQP_ID) AS DB_YD_EQP_WRK_MODE2
			  FROM (
			        SELECT YD_WBOOK_ID
			             , YD_EQP_ID
			             , YD_SCH_CD
			             , YD_WRK_PROG_STAT
			             , YD_DN_WO_LOC
			             , YD_UP_WR_LOC
			             , YD_EQP_WRK_SH
			          FROM TB_YM_CRNSCH A
			         WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			           AND DEL_YN        = 'N'
			        ) CS  */
			JDTORecordSet jsCrnSch1 = commDao.select(resMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStatCrnSch", logId, methodNm, "B열연 SLAB 크레인스케줄상태 조회");

			if (jsCrnSch1.size() == 0) {
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCrnSchLocLog 
				SELECT YD_CRN_SCH_ID
				     , YD_DN_WO_LOC_TO
				     , STL_NO_TEMP
				     , STK_LYR_NO_TEMP
				     , YD_DN_WO_LOC
				     , YD_DN_WO_LAYER
				     , YD_WBOOK_ID
				     , YD_WRK_PROG_STAT
				     , YD_SCH_CD
				     , YD_EQP_ID
				     , YD_UP_WR_LOC
				     , YD_L2_REQUEST_STAT
				     , YD_SCH_PRIOR
				     ,YD_TO_LOC_DCSN_MTD
				  FROM TB_YM_CRNSCH
				 WHERE YD_EQP_ID = :V_YD_EQP_ID
				   AND YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				*/   
				
				
				JDTORecordSet jsDelCrnSch = commDao.select(resMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCrnSchLocLog", logId, methodNm, "대상작업 조회");
				
				if (jsDelCrnSch.size() == 0) {
					//크레인스케쥴 Table 존재유무 Check
					ydL3HdRsCd = "UP11";
					ydL3Msg = "오류:크레인스케쥴 DB정보 없음";
				}else{

					JDTORecord jrDelCrnSch = null;
					jrDelCrnSch 	= jsDelCrnSch.getRecord(0);
					String tmpStat  = commUtils.trim(jrDelCrnSch.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
					
					//인터페이스오류로인해 재수신받았을경우 응답전문재송신
					if("4".equals(tmpStat)){
						resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
						resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
						jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
						
						return jrRtn;
					}
				}
			} else {
				ydEqpWrkSh      = commUtils.trim(jsCrnSch1.getRecord(0).getFieldString("YD_EQP_WRK_SH"    )); //크레인작업매수
			}
		
			if(ydEqpWrkSh.equals("2") ) {
				ydDnWrLayer = commUtils.stringPlusInt(ydDnWrLayer,-1);
				commUtils.printLog("", "■■■ YD_DN_WR_LAYER   = " + ydDnWrLayer  , "[info]");
			}
			commUtils.printLog(logId, " ydDnWrLayer" + ydDnWrLayer, "S+");
			
			
			
			boolean chgDnWrLayer = false; //권하위치 적치단 변경여부			
			
			//-----------------------------------------------------------------------------------------------------------------------------------------
			//권하위치 DB상의 권하 Max단 체크
			
			jrParam.setField("STACK_COL_GP" 	, ydDnWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("STACK_BED_GP" 	, ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMaxLayer
			--권하위치 DB상의 권하 Max단 체크
			SELECT LPAD(NVL(MAX(TO_NUMBER(STACK_LAYER_GP)),0)+1,2,'0') AS STACK_LAYER_GP
			  FROM TB_YM_STACKLAYER A
			 WHERE STACK_COL_GP = :V_STACK_COL_GP
			   AND STACK_BED_GP = :V_STACK_BED_GP
			   AND STACK_LAYER_STAT IN ('L','C') */
			jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMaxLayer", logId, methodNm, "현재정보  조회");
			
			if (jsChk.size() > 0) {
				jrChk = jsChk.getRecord(0);
				String tbDnWrLayer = commUtils.trim(jrChk.getFieldString("STACK_LAYER_GP"));
				
				if (!ydDnWrLayer.equals(tbDnWrLayer)) {
					commUtils.printLog(logId, "권하위치[" + ydDnWrLoc + "] 적치단 변경 : " + ydDnWrLayer + " -> " + tbDnWrLayer, "SL");
					chgDnWrLayer = true;
					ydDnWrLayer  = tbDnWrLayer;
				}
			}
			//-----------------------------------------------------------------------------------------------------------------------------------------			
			
			
			
			
			
			
			jrParam.setField("YD_CRN_XAXIS"	, ydCrnXaxis); //야드크레인스케쥴ID
			jrParam.setField("YD_CRN_YAXIS" , ydCrnYaxis); //수정자			
			jrParam.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			jrParam.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			jrParam.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrParam.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			jrParam.setField("YD_DN_WR_LOC"    , ydDnWrLoc    ); //권하지시위치-논리좌표(8자리)
			jrParam.setField("STACK_LAYER_GP"  , ydDnWrLayer  ); //권하지시단  -논리좌표(3자리)
			
			//크레인 권하작업 SLAB 번호와 권하실적 단 조회 (1매,2매)
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnSchStockIdAndDnWrLayer
			--크레인 스케줄작업 SLAB 번호와 권하실적 단 구하기
			SELECT A.* 
			       , TRIM(TO_CHAR(TO_NUMBER(DNWR_STACK_LAYER_GP) + (ROWNUM-1),'FM00')) AS STACK_LAYER_GP
			       , (SELECT NVL(CHARGE_LOT_NO,CTS_RELAY_SADDLE)  FROM TB_YM_STOCK WHERE STOCK_ID = A.STOCK_ID) AS CHARGE_LOT_NO 
			  FROM (
			            SELECT :V_STACK_LAYER_GP   AS DNWR_STACK_LAYER_GP
			                 , STACK_LAYER_GP      AS STACK_LAYER_GP_FROM
			                 , A.STOCK_ID
			                 , 'C'                 AS STACK_LAYER_STAT
			                 , A.YD_AID_WRK_YN
			              FROM TB_YM_CRNWRKMTL A
			                 , TB_YM_CRNSCH    B
			             WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			               AND A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
			               AND A.DEL_YN = 'N'
			               AND B.DEL_YN = 'N'
			             ORDER BY STACK_LAYER_GP
			       )   A   */
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnSchStockIdAndDnWrLayer", logId, methodNm, "크레인 권하작업 SLAB 번호와 권하실적 단 조회"); 
			
			/***************************
			 ** 1. 강제 권하시 
			 ***************************/
			if ("5".equals(ydWrkProgStat)) {
				
				resMsg.setField("YD_L2_WR_GP"     , "F"); //야드L2실적구분 (강제권하)
				
				JDTORecordSet jsCrnLoc = null;
				
				if ("".equals(ydDnWrLoc)) { 
					commUtils.printLog(logId, "■■■수신 논리좌표 미존재시 X,Y 좌표로 논리 적치열,BED 값을 구해온다", "[info]");
					jrParam.setField("STACK_COL_GP"			,ydEqpId.substring(0,2)); //야드구분 + 동구분
					jrParam.setField("STACK_LAYER_X_AXIS"	,ydCrnXaxis);
					jrParam.setField("STACK_LAYER_Y_AXIS"	,ydCrnYaxis);
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getXYLogicalInfo
					SELECT  A.STACK_COL_GP
					       ,A.STACK_BED_GP
					       ,A.STACK_LAYER_GP
					       ,A.STACK_LAYER_X_AXIS
					       ,A.STACK_LAYER_Y_AXIS
					       ,A.STACK_LAYER_Z_AXIS
					  FROM  TB_YM_STACKLAYER A
					       ,TB_YM_STACKER B
					 WHERE  A.STACK_COL_GP LIKE :V_STACK_COL_GP || '%'
					   AND  A.STACK_COL_GP = B.STACK_COL_GP  
					   AND  A.STACK_BED_GP = B.STACK_BED_GP  
					   AND  A.STACK_LAYER_GP = '01'
					   AND  A.DEL_YN = 'N' 
					   AND  B.DEL_YN = 'N' 
					   AND  A.STACK_LAYER_X_AXIS between :V_STACK_LAYER_X_AXIS - NVL(B.YD_STK_BED_XAXIS_TOL,100) and :V_STACK_LAYER_X_AXIS + NVL(B.YD_STK_BED_XAXIS_TOL,100)
					   AND  A.STACK_LAYER_Y_AXIS between :V_STACK_LAYER_Y_AXIS - NVL(B.YD_STK_BED_YAXIS_TOL,100) and :V_STACK_LAYER_Y_AXIS + NVL(B.YD_STK_BED_YAXIS_TOL,100)  */
					jsCrnLoc = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getXYLogicalInfo", logId, methodNm, "좌표에 해당하는 논리 값 READ 조회");
					
					if (jsCrnLoc.size() != 1) {
						resMsg.setField("YD_L3_HD_RS_CD", "E002");
						resMsg.setField("YD_L3_MSG"     , "물리좌표 적치불가["+ydCrnXaxis+" "+ydCrnYaxis+"]"); //해당 물리좌표 적치불가시 수신받은 물리좌표 리턴
						
						//크레인작업실적응답 전문 전송
						jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
						
						return jrRtn;//  강제권하시 응답전문 송신 후 종료
					}
					
					ydDnWrLoc = jsCrnLoc.getRecord(0).getFieldString("STACK_COL_GP") 
					          + jsCrnLoc.getRecord(0).getFieldString("STACK_BED_GP");
				}  
				
				//SLAB BED에 권상예약, 적치중, 적치가능 COUNT 정보 조회
				jrParam.setField("STACK_COL_GP" 	, ydDnWrLoc.substring(0, 6)); //야드적치열구분
				jrParam.setField("STACK_BED_GP" 	, ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
				/* 
				SELECT (   
				            SELECT COUNT(*) AS U_CNT 
				              FROM TB_YM_STACKLAYER
				             WHERE STACK_COL_GP = :V_STACK_COL_GP
				               AND STACK_BED_GP = :V_STACK_BED_GP
				               AND STACK_LAYER_STAT IN ('U')
				       ) AS U_CNT --권상작업예약COUNT
				      ,(
				            SELECT COUNT(*) AS E_CNT 
				              FROM TB_YM_STACKLAYER
				             WHERE STACK_COL_GP = :V_STACK_COL_GP
				               AND STACK_BED_GP = :V_STACK_BED_GP
				               AND STACK_LAYER_ACTIVE_STAT IN ('E')
				               AND STACK_LAYER_STAT IN ('E')
				       ) AS E_CNT --적치가능COUNT
				      ,(
				            SELECT COUNT(*) AS E_CNT 
				              FROM TB_YM_STACKLAYER
				             WHERE STACK_COL_GP = :V_STACK_COL_GP
				               AND STACK_BED_GP = :V_STACK_BED_GP
				               AND STACK_LAYER_ACTIVE_STAT IN ('E')
				               AND STACK_LAYER_STAT IN ('C')
				       ) AS C_CNT --적치중 COUNT
				  FROM DUAL  */
				jsCrnLoc = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCntInfoBed", logId, methodNm, "논리주소에 해당하는 BED의 적치가능 COUNT 조회");
				
				if(!"0".equals(jsCrnLoc.getRecord(0).getFieldString("U_CNT"))) {
					resMsg.setField("YD_L3_HD_RS_CD", "E000");
					resMsg.setField("YD_L3_MSG" , "권상작업예약존재 ["+ydDnWrLoc+"]");
				} else {
					
					int e_cnt = jsCrnLoc.getRecord(0).getFieldInt("E_CNT");
					if( e_cnt == 0 || rsResult.size() > e_cnt) {
						resMsg.setField("YD_L3_HD_RS_CD", "E000");
						resMsg.setField("YD_L3_MSG" , "적치불가 ["+ydDnWrLoc+"]");
					}
				}
				
				//크레인작업실적응답 전문 전송
				jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
				
				return jrRtn;//  강제권하시 응답전문 송신 후 종료
			}
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "DN01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "DN02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydCrnSchId)) {
				ydL3HdRsCd = "DN03";
				ydL3Msg    = "오류:크레인스케쥴ID 없음";
			} else if ("".equals(ydDnWrLoc)) {
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 없음";
			} else if ("".equals(ydDnWrLayer)) {
				ydL3HdRsCd = "DN05";
				ydL3Msg    = "오류:권하실적단 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			/**********************************************************
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStatCrnSch  
			--B열연 SLAB 크레인스케줄상태 조회 
			SELECT CS.* 
			     , (SELECT YD_EQP_WRK_MODE2 FROM USRYMA.TB_YM_EQUIP WHERE EQUIP_GP = CS.YD_EQP_ID) AS DB_YD_EQP_WRK_MODE2
			  FROM (
			        SELECT YD_WBOOK_ID
			             , YD_EQP_ID
			             , YD_SCH_CD
			             , YD_WRK_PROG_STAT
			             , YD_DN_WO_LOC
			             , YD_UP_WR_LOC
			             , YD_EQP_WRK_SH
			          FROM TB_YM_CRNSCH A
			         WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			           AND DEL_YN        = 'N'
			        ) CS  */
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStatCrnSch", logId, methodNm, "B열연 SLAB 크레인스케줄상태 조회");

			if (jsCrnSch.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "UP11";
				ydL3Msg = "오류:크레인스케쥴 DB정보 없음";
			} else {
				//크레인스케쥴 Table 야드작업진행상태 Check
				JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
				ydWbookId       = commUtils.trim(jrCrnSch.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
				ydUpWrLoc       = commUtils.trim(jrCrnSch.getFieldString("YD_UP_WR_LOC"    )); //야드권상실적위치
				ydEqpWrkSh      = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_WRK_SH"    )); //크레인작업매수
				String tmpStat  = commUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"       )); //야드설비ID
				String tmpMode2 = commUtils.trim(jrCrnSch.getFieldString("DB_YD_EQP_WRK_MODE2" )); //DB 작업모드2
				
				if (!"2".equals(tmpStat) && !"3".equals(tmpStat)) {
					ydL3HdRsCd = "DN12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "DN13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
				
				// 실적에서 MODE2 가 안올라 오는 경우 DB에 있는 DATA SETUP
				if(ydEqpWrkMode2.equals("")) {
					ydEqpWrkMode2 = tmpMode2;
				}
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			commUtils.printLog(logId, " ★★★★   SLAB 권하처리시작 > 크레인 스케쥴 ID:"+ ydCrnSchId, "S+");

			//야드권하작업수행구분
			String ydDnWrkActGp = ydEqpWrkMode;
			
			if ("0".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "M"; //Manual
			} else if ("1".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "A"; //Auto
			} else if ("9".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "B"; //Backup
			}
			
			String currDt = commUtils.getDateTime14(); //현재시각
			
			/**********************************************************
			* 4. 권하실적위치가 대차(상차)
			* 4.1 대차 상차 정보 등록
			*   - 작업예약 야드작업계획대차 수정
			*   - 대차이송재료 등록
			* 4.2 대차 하차스케줄 생성
			*   - 작업예약 등록
			*   - 작업예약재료 등록
			* 4.3 대차스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
			* 4.4 L2 전송 전문
			**********************************************************/
			if ("TC".equals(ydDnWrLoc.substring(2, 4))) {

				
				jrParam.setField("YD_WBOOK_ID"   	    , ydWbookId);   	                        //작업예약번호
				//야드작업계획대차
				jrParam.setField("YD_WRK_PLAN_TCAR"   	,  "2XTC0" + ydDnWrLoc.substring(4,5));   	//계획대차번호
				commUtils.printLog(logId, methodNm+  "상차 권하 대차스케쥴:" + ydSchCd , "SL");

				// 대차스케줄 초기정보 하차 정지위치
				/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarunloadStopLoc_before
				  SELECT CARUNLOAD_STOP_LOC
				   FROM   TB_YM_EQUIP 
				   WHERE EQUIP_GP = :V_YD_WRK_PLAN_TCAR --야드작업 계획대차업데이트
				 */
				JDTORecordSet jsLookUnLoadStopLoc = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarunloadStopLoc_before", logId, methodNm, "하차정지위치조회");
				String YdCarudStopLoc = commUtils.trim(jsLookUnLoadStopLoc.getRecord(0).getFieldString("CARUNLOAD_STOP_LOC")) ;    
             
				if("PT02LM".equals(ydSchCd.substring(2))){ //2APT02LM

				
	
				//이송하차 대차상차 스케줄일경우 야드작업 계획대차가 안들어 가는 이유로  후속작업 을 위하여
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateWrkBkLevWoSlab_before1 /
					UPDATE TB_YM_WRKBOOK
					   SET MODIFIER             = :V_MODIFIER
					     , MOD_DDTT             = SYSDATE
					     , YD_AIM_BAY_GP        = :V_YD_AIM_BAY_GP    --다음대차의 목표동
					     , YD_WRK_PLAN_TCAR     = :V_YD_WRK_PLAN_TCAR --야드작업 계획대차업데이트
					 WHERE YD_WBOOK_ID          = :V_YD_WBOOK_ID
				 */	 
				jrParam.setField("YD_AIM_BAY_GP"   	,  YdCarudStopLoc.substring(1,2));   	//목적동		
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateWrkBkLevWoSlab_before1", logId, methodNm, "작업예약정보  야드작업 계획대차업데이트 수정");
								
				
                }
				
				
				// 대차스케줄 초기정보 상차작업예약id
				/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarldWrkBookId_before
				  SELECT YD_CARLD_WRK_BOOK_ID
				   FROM   TB_YM_TCARSCH 
				   WHERE YD_EQP_ID = :V_YD_WRK_PLAN_TCAR --야드작업 계획대차업데이트
				    AND DEL_YN                 = 'N'				   
				 */
				String ChkCarldWrkBookId ="";
				JDTORecordSet jsLookLoadWrkBookId = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarldWrkBookId_before", logId, methodNm, "대차스케줄의 상차작업예약id 점검");
				ChkCarldWrkBookId = commUtils.trim(jsLookLoadWrkBookId.getRecord(0).getFieldString("YD_CARLD_WRK_BOOK_ID")) ;  
				
				
				
			   if("".equals(ChkCarldWrkBookId))	{
				//대차스케줄 초기정보 없는경우 대비하여 대차스케줄에 상차예약ID를 세팅한다.
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateTcshbefore2 
				UPDATE TB_YM_TCARSCH
				   SET MODIFIER              = :V_MODIFIER
				      ,MOD_DDTT              = SYSDATE
				      ,YD_CARLD_WRK_BOOK_ID  = :V_YD_CARLD_WRK_BOOK_ID
				      ,YD_CARLD_STOP_LOC     = :V_YD_CARLD_STOP_LOC
				      ,YD_CARUD_STOP_LOC     = :V_YD_CARUD_STOP_LOC
				WHERE YD_EQP_ID              = :V_YD_WRK_PLAN_TCAR
				  AND DEL_YN                 = 'N'
				*/
			        jrParam.setField("YD_CARLD_WRK_BOOK_ID"       , ydWbookId          );       //하차치면서 상차인 예약 id
				   //jrParam.setField("YD_CARLD_LEV_LOC"          , NextCurrStopLoc       );    // 
				    jrParam.setField("YD_CARLD_STOP_LOC"          , ydDnWrLoc.substring(0, 6)       ); //상차정지위치
				    jrParam.setField("YD_CARUD_STOP_LOC"          , YdCarudStopLoc     ); //하차정지위치
				   // jrParam.setField("YD_WRK_PLAN_TCAR"         , "2XTC0" + ydDnWrLoc.substring(4,5)  ); //전대차설비id
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateTcshbefore2", logId, methodNm, "대차대대차 이송건 초기 대차스케줄에 상차예약ID를 세팅");										
			   }	
                // 대차상차완료
				//야드작업계획대차
				    //jrParam.setField("YD_WRK_PLAN_TCAR", "2XTC0" + ydDnWrLoc.substring(4,5));
				jrParam.setField("YD_CARLD_WRK_CRN", ydEqpId);
				jrParam.setField("YD_WBOOK_ID"     , ydWbookId);
				jrParam.setField("YD_DN_WR_LOC"    , ydDnWrLoc);
				jrParam.setField("YD_EQP_WRK_SH"   , ydEqpWrkSh);
     
				//대차상차스케쥴 조회
				 /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getA8YML009TCarSchLd  
				-- 상차완료 기준 
				-- 기준매수보다 현재 대차상차매수가 작은 경우 
				   --다음 작업매수가 없으면 상차완료
				   --다음 작업매수가 있으면 현재매수+대차상차완료매수가 기준매수보다 크면 상차완료 
				   --장비 횟수 기준  추가   				
				  SELECT YD_TCAR_SCH_ID 
				    , TC_MAX_QNTY
				    , CASE WHEN TC_TIMES != '1' THEN
				                    CASE WHEN TC_MAX_QNTY > TC_LYR_SH 
				                                THEN CASE WHEN TC_CRN_SH = 0  THEN 'Y'
				                                          WHEN TC_MAX_QNTY < TC_LYR_SH + TC_CRN_SH   THEN 'Y'
				                                          ELSE 'N' END
				                          ELSE 'Y' 
				                          END 
				                   ELSE 'Y' END 
				                   AS TCAR_LD_CMPL_YN --대차상차완료여부
				    , TC_CRN_SH               -- 작업예약에 남은 매수
				    , TC_LYR_SH               -- 현재대차적치매수
				    , YD_EQP_ID
				    , YD_CARUD_STOP_LOC
				    , EQ.YD_TCAR_WRK_ABLE_BAY6
				 FROM  
				      (
				       SELECT TS.YD_TCAR_SCH_ID
				            , TS.YD_EQP_ID
				            , (SELECT COUNT(*) + NVL(TO_NUMBER(:V_YD_EQP_WRK_SH),0) --현재 권하 실적 
				                 FROM TB_YM_STACKLAYER
				                WHERE STACK_COL_GP = SUBSTR(:V_YD_DN_WR_LOC,1,6)
				                  AND STOCK_ID IS NOT NULL
				                  AND DEL_YN = 'N'
				                  AND STACK_LAYER_STAT = 'C' ) 
				              AS TC_LYR_SH --현재대차적치매수
				                  
				            , NVL((SELECT YD_EQP_WRK_SH
				                     FROM (
				                           SELECT * 
				                             FROM TB_YM_CRNSCH
				                            WHERE YD_SCH_CD LIKE SUBSTR(:V_YD_DN_WR_LOC,1,2)|| 'TC__UM'
				                              AND DEL_YN = 'N'
				                              AND YD_CRN_SCH_ID <> :V_YD_CRN_SCH_ID
				                            ORDER BY YD_CRN_SCH_ID  
				                      )
				                 WHERE ROWNUM <= 1 ),0)       
				              AS TC_CRN_SH --다음작업 대차작업 수
				                  
				            , EQ.STACK_MAX_QNTY  AS TC_MAX_QNTY --설비기준 매수 
				            , EQ.CTS_RELAY_YN    AS TC_TIMES    --설비기준 횟수

				           -- , NVL(TS.YD_CARUD_STOP_LOC,EQ.CARUNLOAD_STOP_LOC) AS YD_CARUD_STOP_LOC  --야드하차정지위치
				            ,( SELECT ITEM
									       FROM USRYMA.TB_YM_RULE RL 
									          WHERE RL.REPR_CD_GP = 'TCAR01'
									            AND RL.CD_GP    = '2'
									            AND RL.DEL_YN   = 'N'
									            AND RL.DTL_ITM1 = TS.YD_EQP_ID
									            AND RL.DTL_ITM2 = BK.YD_AIM_BAY_GP ) AS YD_CARUD_STOP_LOC --작업예약 기준 하차지 정지 위치    
				         FROM TB_YM_TCARSCH TS
				             ,TB_YM_EQUIP   EQ 
				             ,TB_YM_WRKBOOK BK 
				        WHERE TS.YD_EQP_ID = :V_YD_WRK_PLAN_TCAR
				         AND  TS.YD_CARLD_WRK_BOOK_ID = BK.YD_WBOOK_ID
				         AND  TS.DEL_YN    = 'N'
				         AND  TS.YD_EQP_ID = EQ.EQUIP_GP )*/
				JDTORecordSet jsTc = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getA8YML009TCarSchLd", logId, methodNm, "대차상차스케쥴 조회 조회!!!");
				
				if (jsTc.size() > 0) {
					JDTORecord jrTc = jsTc.getRecord(0);

					jrParam.setField("YD_TCAR_SCH_ID", commUtils.trim(jrTc.getFieldString("YD_TCAR_SCH_ID"))); //야드대차스케쥴ID(이력등록시에도 사용)
					
					//작업예약 야드작업계획대차 수정
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA7YML009WbTCar 
					// 크레인권하실적 작업예약 대차 수정
					UPDATE TB_YM_WRKBOOK
					   SET MODIFIER         = :V_MODIFIER
						  ,MOD_DDTT         = SYSDATE
					      ,YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
					 WHERE YD_WBOOK_ID      = :V_YD_WBOOK_ID
					*/ 
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA7YML009WbTCar", logId, methodNm, "작업예약 야드작업계획대차 수정");
					
					//대차이송재료 등록
					//코일은 BED 증가  SLAB는 단 증가
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA8YML009TCarMtlIns  
					MERGE INTO TB_YM_TCARFTMVMTL TM USING (
					SELECT :V_YD_TCAR_SCH_ID AS YD_TCAR_SCH_ID
					     , CM.STOCK_ID
					     , :V_MODIFIER       AS MODIFIER
					     , SYSDATE           AS MOD_DDTT
					     , 'N'               AS DEL_YN
					     , substr(:V_YD_DN_WR_LOC,7,2) AS STACK_BED_GP
					     -- 2매 2회 작업시 각각 크레인스케줄이 틀릴경우 단 정의를 위함
					     , CASE WHEN ( SELECT  COUNT(*) 
					                   FROM   TB_YM_TCARFTMVMTL 
					                  WHERE   YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID)  = 0 THEN  
					                  TO_CHAR(ROW_NUMBER() OVER( ORDER BY CM.STACK_LAYER_GP) ,'FM00')         
					            ELSE  TO_CHAR(( SELECT  COUNT(*) 
					                             FROM   TB_YM_TCARFTMVMTL 
					                            WHERE    YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID) +1,'FM00') END   AS STACK_LAYER_GP
					      , CC.HCR_GP
					      , CC.CURR_PROG_CD  AS STL_PROG_CD
					      , ST.STOCK_ITEM    AS YD_MTL_ITEM
					  FROM TB_YM_CRNWRKMTL CM
					     , TB_YM_STOCK     ST
					     , VW_YD_SLABCOMM  CC  
					 WHERE CM.STOCK_ID        = ST.STOCK_ID
					   AND CM.STOCK_ID        = CC.SLAB_NO
					   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					   AND CM.DEL_YN        = 'Y'
					 ORDER BY CM.STACK_LAYER_GP
					) DD ON (TM.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID AND TM.STOCK_ID = DD.STOCK_ID)
					WHEN NOT MATCHED THEN
					INSERT (TM.YD_TCAR_SCH_ID, TM.STOCK_ID     , TM.REGISTER   , TM.REG_DDTT     ,
					        TM.MODIFIER      , TM.MOD_DDTT     , TM.DEL_YN     , TM.STACK_BED_GP,
					        TM.STACK_LAYER_GP, TM.HCR_GP       , TM.STL_PROG_CD, TM.YD_MTL_ITEM   )
					VALUES (DD.YD_TCAR_SCH_ID, DD.STOCK_ID     , DD.MODIFIER   , DD.MOD_DDTT     ,
					        DD.MODIFIER      , DD.MOD_DDTT     , DD.DEL_YN     , DD.STACK_BED_GP,
					        DD.STACK_LAYER_GP, DD.HCR_GP       , DD.STL_PROG_CD, DD.YD_MTL_ITEM   )
					 */         
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA8YML009TCarMtlIns", logId, methodNm, "대차이송재료 등록");

					tcarLdCmplYn         = commUtils.trim(jrTc.getFieldString("TCAR_LD_CMPL_YN"));    	//대차상차완료여부
					String tcarUdStopLoc = commUtils.trim(jrTc.getFieldString("YD_CARUD_STOP_LOC"));  	//하차 TO위치
					String tcarEqpId 	 = commUtils.trim(jrTc.getFieldString("YD_EQP_ID"));  			//대차설비ID
					String YdTcarWrkAbleBay6    = commUtils.trim(jrTc.getFieldString("YD_TCAR_WRK_ABLE_BAY6")) ; //선작업지시 상차지 이지만 하차지에 실행할 경우 TAG
					String CarudSchCd 	 = commUtils.trim(jrTc.getFieldString("CARUD_SCH_CD")); 					
//					String TC_LYR_SH 	 = commUtils.trim(jrTc.getFieldString("TC_LYR_SH"));  			//상차매수
					String ydTcWrkBookId = "";

					

					
					
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarYML009UpStkLyr 
					--크레인권상실적  대차 적치단(권하위치) 선 CLEAR
					UPDATE TB_YM_STACKLAYER 
					   SET MODIFIER                = :V_MODIFIER
					     , MOD_DDTT                = SYSDATE
					     , STACK_LAYER_STAT        = 'E'
					     , STACK_LAYER_ACTIVE_STAT ='E'
					 WHERE STOCK_ID IS NULL     
					   AND STACK_COL_GP = SUBSTR(:V_YD_DN_WR_LOC,1,6)					
					

					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarYML009UpStkLyr", logId, methodNm, " 대차 적치단(권하위치) 선 CLEAR");					
					*/ 
					
					commUtils.printLog(logId, methodNm+  "★★★★★  권하대차스케쥴:" + commUtils.trim(jrTc.getFieldString("YD_TCAR_SCH_ID")) 
							 +  " 대차상차완료여부:" + tcarLdCmplYn +  " 하차 위치 :" + tcarUdStopLoc +" ★★★★", "SL");
					
					jrParam.setField("YD_CARUD_STOP_LOC", tcarUdStopLoc);                               //TO위치
					if ("N".equals(tcarLdCmplYn)) {
						//상차완료가 아니면
						jrParam.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					} else {
						
						//상차완료이면
						jrParam.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
						
						/*
						 com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getA7YSL009WbTCarIns
						SELECT TM.STOCK_ID
						      ,WB.STACK_COL_GP
						      ,TM.STACK_BED_GP
						      ,TM.STACK_LAYER_GP
						      ,COUNT(*) OVER () - ROW_NUMBER() OVER (ORDER BY STACK_LAYER_GP) + 1 AS YD_UP_COLL_SEQ
						  FROM TB_YM_TCARFTMVMTL TM
						      ,(SELECT WB.YD_GP||WB.YD_AIM_BAY_GP||SUBSTR(WB.YD_WRK_PLAN_TCAR,3) AS STACK_COL_GP
						          FROM TB_YM_WRKBOOK WB
						         WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID) WB
						 WHERE TM.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
						   AND TM.DEL_YN         = 'N'
							   
						JDTORecordSet jsDnTc = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getA7YSL009WbTCarIns", logId, methodNm, "권하대상재!!!");
						if (jsDnTc.size() > 0 ){
							JDTORecord jrParamTc = JDTORecordFactory.getInstance().create();
							JDTORecord jrDnTc 	 = JDTORecordFactory.getInstance().create(); 
							String ydCarudWrkBookId = commDao.getSeqId(logId, methodNm, "WrkBook");
							for(int nIdx=0; nIdx < jsDnTc.size(); nIdx++) {
								
								jrParamTc = JDTORecordFactory.getInstance().create();
								jrDnTc    = JDTORecordFactory.getInstance().create();
								//대차스케줄 처리 (영대차출발지시)
								//야드하차작업예약ID 생성
								//String ydCarudWrkBookId = commDao.getSeqId(logId, methodNm, "WrkBook");
								
								jrParamTc.setField("YD_CARUD_WRK_BOOK_ID", ydCarudWrkBookId); //야드하차작업예약ID
								jrParamTc.setField("YD_SCH_ST_GP"        , ydDnWrkActGp    ); //야드스케쥴기동구분
								jrParamTc.setField("YD_EQP_ID"           , tcarEqpId       ); //대차설비id
								jrParamTc.setField("MODIFIER"            , modifier       ); 
								jrParamTc.setField("YD_WBOOK_ID"         , ydWbookId       ); 
								jrParamTc.setField("YD_CARUD_STOP_LOC"   , tcarUdStopLoc       ); 
								
								jrDnTc = jsDnTc.getRecord(nIdx);
								if(nIdx == 0) {
									ydTcWrkBookId = ydCarudWrkBookId;
									//작업예약 등록
							        
									 
									 com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA8YSL009WbTCarIns  
									MERGE INTO TB_YM_WRKBOOK WB USING (
									       SELECT :V_YD_CARUD_WRK_BOOK_ID AS YD_WBOOK_ID      --야드작업예약ID
									     , :V_MODIFIER             AS MODIFIER         --수정자
									     , SYSDATE                 AS MOD_DDTT         --수정일시
									     , 'N'                     AS DEL_YN           --삭제유무
									     , WB.YD_GP                                    --야드구분
									     , WB.YD_BAY_GP                                --야드동구분
									     , WB.YD_SCH_CD                                --야드스케쥴코드
									     , (SELECT SR.YD_WRK_CRN_PRIOR
									          FROM TB_YM_SCHEDULERULE SR
									         WHERE SR.YD_SCH_CD = WB.YD_SCH_CD) 
									                               AS YD_SCH_PRIOR     --야드스케쥴우선순위
									     , 'W'                     AS YD_SCH_PROG_STAT --야드스케쥴진행상태(스케줄수행대기)
									     , :V_YD_SCH_ST_GP         AS YD_SCH_ST_GP     --야드스케쥴기동구분
									     , '1'                     AS YD_SCH_REQ_GP    --야드스케쥴요청구분(대차상차완료)
									     , WB.YD_TO_LOC_DCSN_MTD                       --야드To위치결정방법
									     , WB.YD_TO_LOC_GUIDE                          --야드To위치Guide
									     , WB.YD_WRK_PLAN_TCAR                         --야드작업계획대차
									  FROM (SELECT WB.YD_GP
									             , SUBSTR(:V_YD_CARUD_STOP_LOC,2,1)       AS YD_BAY_GP
									             , CASE WHEN WB.YD_AIM_BAY_GP NOT IN ('A','C') THEN 
									                         '2'||WB.YD_AIM_BAY_GP||DECODE( SUBSTR(:V_YD_CARUD_STOP_LOC,5,1) , '2','TC22LM','TC11LM') 
									                    ELSE '2'||WB.YD_AIM_BAY_GP|| SUBSTR(EQ.CARUD_SCH_CD,3)   
									                    END  AS YD_SCH_CD 
									                   
									             , CASE WHEN WB.YD_TO_LOC_DCSN_MTD = 'F' AND WB.YD_AIM_BAY_GP != SUBSTR(WB.YD_TO_LOC_GUIDE,2,1)
									                    THEN 'S' ELSE WB.YD_TO_LOC_DCSN_MTD END 
									               AS YD_TO_LOC_DCSN_MTD
									             , CASE WHEN WB.YD_TO_LOC_DCSN_MTD = 'F' AND WB.YD_AIM_BAY_GP != SUBSTR(WB.YD_TO_LOC_GUIDE,2,1)
									                    THEN ''  ELSE WB.YD_TO_LOC_GUIDE    END 
									               AS YD_TO_LOC_GUIDE
									             , WB.YD_WRK_PLAN_TCAR
									          FROM TB_YM_WRKBOOK WB
									              ,TB_YM_EQUIP   EQ             
									         WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
									           AND WB.YD_WRK_PLAN_TCAR = EQ.EQUIP_GP(+)) WB 
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
									
									commDao.update(jrParamTc, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA8YSL009WbTCarIns", logId, methodNm, "작업예약 등록");
								}
								

		
								//작업예약재료 등록
								 com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA7YML009WbMtlTCarIns 
								INSERT INTO TB_YM_WRKBOOKMTL WM
								       (WM.YD_WBOOK_ID          , WM.STOCK_ID       , WM.REGISTER       , WM.REG_DDTT    ,
								        WM.MODIFIER             , WM.MOD_DDTT       , WM.DEL_YN         , WM.STACK_COL_GP,
								        WM.STACK_BED_GP         , WM.STACK_LAYER_GP , WM.YD_UP_COLL_SEQ)
								VALUES (:V_YD_CARUD_WRK_BOOK_ID , :V_STOCK_ID       , :V_MODIFIER       , SYSDATE        ,
								        :V_MODIFIER             , SYSDATE           , 'N'               , :V_STACK_COL_GP,
								        :V_STACK_BED_GP         , :V_STACK_LAYER_GP , :V_YD_UP_COLL_SEQ);        
								        
								jrParamTc.setField("STOCK_ID" 		, commUtils.trim(jrDnTc.getFieldString("STOCK_ID"))); 
								jrParamTc.setField("STACK_COL_GP" 	, commUtils.trim(jrDnTc.getFieldString("STACK_COL_GP"))); 
								jrParamTc.setField("STACK_BED_GP" 	, commUtils.trim(jrDnTc.getFieldString("STACK_BED_GP"))); 
								jrParamTc.setField("STACK_LAYER_GP" , commUtils.trim(jrDnTc.getFieldString("STACK_LAYER_GP"))); 
								jrParamTc.setField("YD_UP_COLL_SEQ" , commUtils.trim(jrDnTc.getFieldString("YD_UP_COLL_SEQ"))); 
								
								
								commDao.update(jrParamTc, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA7YML009WbMtlTCarIns", logId, methodNm, "작업예약재료 등록");						        
							}//end for
						}
						
					*/}
					jrParam.setField("YD_CARUD_WRK_BOOK_ID", ydTcWrkBookId); //야드하차작업예약ID
					jrParam.setField("STACK_COL_GP" 	, ydDnWrLoc.substring(0, 6)); //야드적치열구분
					//대차스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA8YML009TcarSchLd  
					-- 대차스케줄 수정
					MERGE INTO TB_YM_TCARSCH TS USING (
					SELECT TM.YD_TCAR_SCH_ID
					     , :V_MODIFIER              AS MODIFIER
					     , :V_YD_CAR_PROG_STAT      AS YD_CAR_PROG_STAT
					     , TM.YD_EQP_WRK_SH
					     , TM.YD_EQP_WRK_WT
					     , :V_YD_WBOOK_ID           AS YD_CARLD_WRK_BOOK_ID
					     , :V_STACK_COL_GP          AS YD_CARLD_STOP_LOC
					     , :V_YD_CARLD_WRK_CRN      AS YD_CARLD_WRK_CRN
					     , NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE) AS WR_DT
					     , WB.YD_WBOOK_ID           AS YD_CARUD_WRK_BOOK_ID 
					     , NVL(:V_YD_CARUD_STOP_LOC,YD_CARUD_STOP_LOC)     AS YD_CARUD_STOP_LOC 
					  FROM TB_YM_WRKBOOK WB
					      ,(SELECT TM.YD_TCAR_SCH_ID
					              , YD_CARUD_STOP_LOC      AS YD_CARUD_STOP_LOC 
					              ,COUNT(*)                AS YD_EQP_WRK_SH
					              ,SUM(ST.SLAB_WT)         AS YD_EQP_WRK_WT
					              ,:V_YD_CARUD_WRK_BOOK_ID AS YD_CARUD_WRK_BOOK_ID
					          FROM TB_YM_TCARSCH TS
					              ,TB_YM_TCARFTMVMTL TM
					              ,VW_YD_SLABCOMM    ST
					         WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
					           AND TM.STOCK_ID       = ST.SLAB_NO
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
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA8YML009TcarSchLd", logId, methodNm, "대차스케줄 수정");
					
					//L2 영대차출발지시
					if ("Y".equals(tcarLdCmplYn)) {
												
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L006", jrParam));
					}
					
					//하차스케줄이 대차상차스케줄인경우
					if(((CarudSchCd.substring(2,4))+(CarudSchCd.substring(6))).equals("TCUM")&& "Y".equals(tcarLdCmplYn)){  //2XTC11UM
						 //다음 가능한 대차 조회 하여 공대차 출발 지시를 내린다.
						 //다음을 영대차 하차출발시 하여야할지 ?(스케줄이 잘못된경우 현재 상차완료부터 시작함)
						 /*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarToTcarMove
						 SELECT A.EQUIP_GP
						       ,CASE WHEN  :V_EQUIP_GP='2XTC03' THEN DECODE(((COUNT(*) OVER() )),1,'Y',DECODE(A.EQUIP_GP,'2XTC02','Y','N'))
						             WHEN  :V_EQUIP_GP='2XTC02' THEN DECODE(((COUNT(*) OVER() )),1,'Y',DECODE(A.EQUIP_GP,'2XTC03','Y','N'))  
						             WHEN  :V_EQUIP_GP='2XTC01' THEN DECODE(((COUNT(*) OVER() )),1,'Y',DECODE(A.EQUIP_GP,'2XTC02','Y','N'))  
						             ELSE '' END AS YN_TAG
						      ,COUNT(*) OVER(  ) RN
						       ,A.CURR_STOP_LOC  
						       ,A.CARLOAD_STOP_LOC
						       ,A.CARUNLOAD_STOP_LOC
						       ,SUBSTR(A.CARLOAD_STOP_LOC,2,1) AIMBAY 
						  FROM TB_YM_EQUIP A
						      ,TB_YM_TCARSCH B
						  WHERE A.EQUIP_GP !=  :V_EQUIP_GP  
						    AND A.DEL_YN ='N'
						    AND B.DEL_YN ='N'
						    AND A.EQUIP_GP = B.YD_EQP_ID
						    AND A.YD_GP ='2'
						    AND B.YD_EQP_WRK_STAT ='U' --공차
						    AND B.YD_CAR_PROG_STAT ='0' --상차대기						
						*/
						
					    jrParam.setField("EQUIP_GP"           , "2XTC0" + ydDnWrLoc.substring(4,5)      ); //전대차설비id
					
					    
						JDTORecordSet jsLookTc = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarToTcarMove", logId, methodNm, "대하차스케줄이 상차인 다음 가능한 대차 조회");
						
						if (jsLookTc.size() > 0) {
							//throw new Exception("대차대대차 이송 가능한 대차가 존재하지 않습니다.");
						
							 String Tcar       = commUtils.trim(jsLookTc.getRecord(0).getFieldString("EQUIP_GP")) ;                  //다음대차
							 String AimBay     = commUtils.trim(jsLookTc.getRecord(0).getFieldString("AIMBAY")) ;                    //다음대차상차동
							 jrParam.setField("YD_EQP_ID"          , Tcar       );   //다음대차설비id
							 jrParam.setField("YD_BAY_GP"          , AimBay       ); //다음대차상차동					 
							 jrRtn = commUtils.addSndData(jrRtn, ymComm.trtTcarStartUnload_Slab(jrParam));
						}
						
					}					
				}				


			}

			/**********************************************************
			* 5. 권하실적위치가 차량(상차)
			* 5.1 차량이송재료 등록
			* 5.2 차량스케줄 야드차량진행상태, 야드설비작업상태(L:영차), 야드설비작업매수 등 수정
			*   - 직상차(차량 스케줄코드가 아님) : 야드차량진행상태(4:상차개시)
			*   - 상차완료(차량 스케줄코드가 아님) : 야드차량진행상태(5:상차완료)
			*   - 야드차량사용구분이 구내운송(L)이고 상차완료이면
			*     . 마지막 크레인스케줄 이면
			* 5.3 공통 처리 : 야드차량사용구분이 구내운송(L)이고 상차완료이면
			* 5.3.2 소재이송지시 야드재료예정저장From위치코드, 이송상차일자 수정
			* 5.3.3 저장품 목표야드, 목표동, 목표행선 등을 수정
			* 5.4 야드차량사용구분이 출하차량(G)
			* 5.4.1 출하관리 일품출하상차실적(YSDSJ007) 전송
			* 5.4.2 출하관리 출하상차완료(YSDSJ008) 전송
			*     - 상차완료(마지막 크레인스케줄)이면
			**********************************************************/
			//차량상차완료여부(소재이송지시 수정 및 공통Table 소재이송일시 수정)
			String carLdCmplYn 		= "N";
			String ydCarUseGp  		= "";
			String ydCarSchId  		= "";
			String CarNo  			= "";
			String TransOrdDate  	= "";
			String TransOrdSeqNo  	= "";
			String WlocCd  			= "";
			String ydPndCd  		= "";
			String drvTelNo  		= "";   // 기사전화번호
			String ydCarProgStat  	= "";   // 차량진행상태
			String equipType 	    = "";   // 냉연이송 구분
			String cmbnCarldyn 	    = "";   // 복수상차
			
			if ("PT".equals(ydDnWrLoc.substring(2, 4))) {
				
				//상차 차량스케줄  조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId     ); 
				recPara.setField("STACK_COL_GP"  , ydDnWrLoc.substring(0, 6) ); 
				recPara.setField("YD_EQP_ID"     , ydEqpId); 
				
				/*  com.inisteel.cim.ym.bslab.dao.BSlabDAO.getA8YML009CarSchLd
				SELECT YD_CAR_SCH_ID
				     , YD_CAR_USE_GP 
				     , CAR_NO
				     , BAY_WRK_CNT -- 해당상차도 작업대상
				     , BAY_WRK_END_CNT -- 해당 상차도 작업완료
				     , TRANS_ORD_DATE
				     , TRANS_ORD_SEQNO
				     , DEST_TEL_NO
				     , STACK_COL_GP
				     , WLOC_CD
				     , YD_PNT_CD
				     , YD_CAR_PROG_STAT -- 차량진행
				     , BAY_WRK_CNT
				     , BAY_WRK_END_CNT
				     --, CASE WHEN BAY_WRK_CNT <= BAY_WRK_END_CNT THEN 'Y' ELSE'N' END AS CAR_LD_CMPL_YN      --해당상차도 [1123]경기99사1123
				     , CASE WHEN BAY_WRK_END_CNT > 0 THEN 'N' ELSE 'Y' END AS CAR_LD_CMPL_YN
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
				             , SC.STACK_COL_GP
				             , SC.WLOC_CD
				             , SC.YD_PNT_CD
				         -- 해당상차도 작업대상 건수   
				            ,  CASE WHEN SC.YD_CAR_USE_GP = 'L' THEN 
				                    -- 구내운송
				                         (SELECT COUNT(DISTINCT(A.STOCK_ID)) 
				                            FROM TB_YM_STOCK A
				                               , TB_YM_STACKLAYER B 
				                           WHERE A.STOCK_ID         = B.STOCK_ID
				                             AND A.FRTOMOVE_WORD_NO = TS.FRTOMOVE_WORD_NO
				                             AND SUBSTR(B.STACK_COL_GP,1,2) = SUBSTR(SC.STACK_COL_GP,1,2))
				                    ELSE  
				                     -- 출하
				                        (SELECT COUNT(DISTINCT(B.STOCK_ID)) 
				                           FROM TB_YM_STOCK B
				                              , TB_YD_CARPOINT C
				                          WHERE B.TRANS_ORD_DATE2  = TS.TRANS_ORD_DATE
				                            AND B.TRANS_ORD_SEQNO2 = TS.TRANS_ORD_SEQNO
				                            AND SUBSTR(B.EXPECT_STACK_LOC,3,2) BETWEEN YD_SPAN_FROM  AND YD_SPAN_TO
				                            AND SUBSTR(B.EXPECT_STACK_LOC,2,1) = SUBSTR(C.YD_STK_COL_GP,2,1)
				                            AND C.YD_STK_COL_GP = SC.STACK_COL_GP)
				                      
				                     END  
				               AS BAY_WRK_CNT    
				               
				         -- 해당상차도 작업완료 건수                 
				             , CASE WHEN SC.YD_CAR_USE_GP = 'L' THEN 
				                    -- 구내운송
				                         (SELECT COUNT(DISTINCT(A.STOCK_ID))
				                            FROM TB_YM_STACKLAYER A
				                           WHERE ((SUBSTR(A.STACK_COL_GP,3,2) NOT IN ('PT','CR') AND STACK_LAYER_STAT IN ('C','U')) )
				                             AND SUBSTR(A.STACK_COL_GP,1,2) = SUBSTR(SC.STACK_COL_GP,1,2)
				                             AND A.STOCK_ID IN (SELECT B.STOCK_ID
				                                                  FROM TB_YM_STOCK B
				                                                 WHERE B.FRTOMOVE_WORD_NO = TS.FRTOMOVE_WORD_NO))              
				                    ELSE 
				                    -- 출하
				                         (SELECT COUNT(DISTINCT(A.COIL_NO)) + 1
				                            FROM TB_PT_COILCOMM A
				                           WHERE SUBSTR(A.YD_STR_LOC,3,2) IN ('PT') 
				                             AND A.COIL_NO  IN (SELECT B.STOCK_ID
				                                                  FROM TB_YM_STOCK B
				                                                     , TB_YD_CARPOINT C
				                                                 WHERE B.TRANS_ORD_DATE2  = TS.TRANS_ORD_DATE
				                                                   AND B.TRANS_ORD_SEQNO2 = TS.TRANS_ORD_SEQNO
				                                                   AND SUBSTR(B.EXPECT_STACK_LOC,3,2) BETWEEN YD_SPAN_FROM  AND YD_SPAN_TO
				                                                   AND SUBSTR(B.EXPECT_STACK_LOC,2,1) = SUBSTR(C.YD_STK_COL_GP,2,1)
				                                                   AND C.YD_STK_COL_GP = SC.STACK_COL_GP))   
				                    END  
				               AS BAY_WRK_END_CNT   
				          FROM TB_YM_STACKCOL SC
				             , USRYDA.TB_YD_CARSCH TS
				         WHERE SC.STACK_COL_GP = :V_STACK_COL_GP
				           AND SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
				           AND ((SC.YD_CAR_USE_GP = 'L' AND SC.TRN_EQP_CD = TS.TRN_EQP_CD) --구내운송
				             OR (SC.YD_CAR_USE_GP = 'G' AND SC.CAR_NO = TS.CAR_NO)) --출하차량
				           AND TS.DEL_YN = 'N'
				        )  
				*/
				JDTORecordSet jsCarUpSch = commDao.select(recPara, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getA8YML009CarSchLd", logId, methodNm, "상차 차량스케줄 조회 "); 
		    	
				if (jsCarUpSch.size() > 0) {
					JDTORecord jrCarUpSch = jsCarUpSch.getRecord(0);

					ydCarSchId  	= commUtils.trim(jrCarUpSch.getFieldString("YD_CAR_SCH_ID"));
					CarNo 			= commUtils.trim(jrCarUpSch.getFieldString("CAR_NO")); 
					TransOrdDate	= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_DATE"));
					TransOrdSeqNo 	= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_SEQNO")); 
					WlocCd 			= commUtils.trim(jrCarUpSch.getFieldString("WLOC_CD")); 
					ydPndCd 		= commUtils.trim(jrCarUpSch.getFieldString("YD_PNT_CD")); 					
					ydCarUseGp 		= commUtils.trim(jrCarUpSch.getFieldString("YD_CAR_USE_GP")); 		//야드차량사용구분
					drvTelNo 		= commUtils.trim(jrCarUpSch.getFieldString("DEST_TEL_NO")); 		//목적지 전화번호
					carLdCmplYn 	= commUtils.trim(jrCarUpSch.getFieldString("CAR_LD_CMPL_YN")); 		//차량상차완료여부
					ydCarProgStat 	= commUtils.trim(jrCarUpSch.getFieldString("YD_CAR_PROG_STAT")); 	//차량진행상태
					equipType 		= commUtils.trim(jrCarUpSch.getFieldString("TRANS_EQUIPMENT_TYPE")); //운송장비 TYPE P:PDA					
					cmbnCarldyn 	= commUtils.trim(jrCarUpSch.getFieldString("CMBN_CARLD_YN")); 	     //복수상차					
					
					commUtils.printLog(logId, methodNm+  "★★★★★  권하차량스케쥴:" + ydCarSchId + " 차량상차완료:" + carLdCmplYn+ " ★★★★", "SL");
					
					//차량이송재료(TB_YM_CARFTMVMTL) 상차 등록
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CAR_SCH_ID" , ydCarSchId ); 
					recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
					recPara.setField("MODIFIER" 	 , modifier     ); 
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.insA8YML009CarMtlIns
					--B열연 SLAB 크레인권하실적 차량이송재료 등록 

					INSERT INTO USRYDA.TB_YD_CARFTMVMTL ( 
					       YD_CAR_SCH_ID
					     , STL_NO   
					     , REGISTER     
					     , REG_DDTT     
					     , MODIFIER     
					     , MOD_DDTT 
					     , DEL_YN       
					     , YD_STK_BED_NO
					     , YD_STK_LYR_NO   
					) 
					SELECT YD_CAR_SCH_ID
					       ,STOCK_ID
					       ,REGISTER
					       ,REG_DDTT
					       ,MODIFIER
					       ,MOD_DDTT
					       ,DEL_YN
					       ,YD_STK_BED_NO
					       ,TO_CHAR(CNT + ROWNUM ,'FM00') AS YD_STK_LYR_NO
					  FROM  (

					             SELECT :V_YD_CAR_SCH_ID AS YD_CAR_SCH_ID
					                  , CM.STOCK_ID
					                  , :V_MODIFIER AS REGISTER    
					                  , SYSDATE  AS REG_DDTT      
					                  , :V_MODIFIER AS MODIFIER    
					                  , SYSDATE  AS MOD_DDTT     
					                  , 'N' AS DEL_YN            
					                  , NVL(SUBSTR(YD_DN_WO_LOC,-2),'01') AS YD_STK_BED_NO
					                  , (SELECT COUNT(*)
					                       FROM TB_YD_CARFTMVMTL
					                      WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					                        AND DEL_YN         = 'N'
					                    ) AS CNT
					                  , ROWNUM 
					              FROM TB_YM_CRNWRKMTL CM
					                  ,TB_YM_STOCK     ST
					                  ,TB_YM_CRNSCH CR
					             WHERE CM.STOCK_ID      = ST.STOCK_ID
					               AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					               AND CM.YD_CRN_SCH_ID = CR.YD_CRN_SCH_ID
					               AND CM.DEL_YN        = 'N'
					             ORDER BY CM.STACK_LAYER_GP

					        ) AA    	 */
					commDao.update(recPara, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insA8YML009CarMtlIns", logId, methodNm, "B열연 SLAB 상차 이송재료 등록 ");
					
					
					//차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setResultCode(logId);	//Log ID
					recPara.setResultMsg(methodNm);	//Log Method Name
					
					//if ("Y".equals(carLdCmplYn)) {              //해당동만 완료이면 차량상태 완료처리함
					//	recPara.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
					//} else {
						recPara.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					//}
					recPara.setField("YD_WBOOK_ID" 		, ydWbookId ); 
					recPara.setField("STACK_COL_GP" 	, ydDnWrLoc.substring(0, 6) ); 
					recPara.setField("WR_DT" 			, currDt ); 
					recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
					recPara.setField("MODIFIER"         , modifier     ); 
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA8YML009CarSchLd
					--B열연 SLAB 크레인권하실적 상차 차량스케줄 수정
					UPDATE USRYDA.TB_YD_CARSCH TS
					   SET TS.MODIFIER             = :V_MODIFIER
					     , TS.MOD_DDTT             = SYSDATE
					     , TS.YD_EQP_WRK_STAT      = 'L' --영차
					     , TS.YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT   
					     , TS.YD_EQP_WRK_SH        = (SELECT COUNT(*) 
					                                    FROM TB_YD_CARFTMVMTL 
					                                   WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID)
					     , TS.YD_EQP_WRK_WT        = (SELECT SUM(SLAB_WT) 
					                                    FROM TB_YD_CARFTMVMTL A
					                                       , VW_YD_SLABCOMM   B
					                                   WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					                                     AND A.STL_NO        = B.SLAB_NO
					                                   
					                                   )
					     , TS.YD_PNT_CD3           = '0000'
					     , TS.YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_WBOOK_ID,TS.YD_CARLD_WRK_BOOK_ID)
					     , TS.YD_CARLD_STOP_LOC    = :V_STACK_COL_GP
					     , TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT,NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE))
					     , TS.YD_CARLD_CMPL_DT     = DECODE(:V_YD_CAR_PROG_STAT,'5',NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE),NULL)
					     , TS.ARR_WLOC_CD          = NVL(:V_ARR_WLOC_CD,TS.ARR_WLOC_CD)
					   
					WHERE TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
					 */
					commDao.update(recPara, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA8YML009CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");

					if ("Y".equals(carLdCmplYn)) {

			    		/**********************************************************
						* 상차완료처리
						**********************************************************/
						if ("L".equals(ydCarUseGp)) {
				    		/**********************************************************
							* 구내운송 상차완료처리
							**********************************************************/
							//jrYdMsg			= JDTORecordFactory.getInstance().create();
	    					//jrYdMsg = JDTORecordFactory.getInstance().create();
							//jrYdMsg.setResultCode(logId);	//Log ID
							//jrYdMsg.setResultMsg(methodNm);	//Log Method Name
							//jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId); 
							//jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ008", jrYdMsg));
							
		    				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
		    				sndL2Msg.setResultCode(logId);	//Log ID
		    				sndL2Msg.setResultMsg(methodNm);	//Log Method Name
		    				sndL2Msg.setField("YD_INFO_SYNC_CD"	, "3"                         ); //야드정보동기화코드
		    				sndL2Msg.setField("MSG_GP"			, "I"                         ); //전문구분
		    				sndL2Msg.setField("STACK_COL_GP"    , ydDnWrLoc.substring(0, 6));
		    				sndL2Msg.setField("STACK_BED_GP"    , "");
		    				commUtils.printParam(logId, sndL2Msg);
		    	 
		    					//전송 Data 생성
		    				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L001", sndL2Msg));		

		    				commUtils.printLog(logId, "[" + methodNm + "] 저장위치제원 : 슬라브야드L2 로 송신 열구분[" + ydDnWrLoc.substring(0, 6) + "] - 저장위치제원 : 코일야드L2 로 송신 호출 성공"+jrRtn.size(), "SL");
						
						}
					}
				}
			}
			
			/**********************************************************
			* 6. 권상실적위치가 차량(하차)
			*    마지막 스케줄이면(차량이송재료 없음)
			* 6.1 차량스케줄 야드차량진행상태(E:하차완료), 야드설비작업상태(U:공차) 등 수정
			* 6.4 구내운송 소재차량하차완료 송신(YSTSJ010) 전송
			* 6.5 권하실적재료 적치단 수정 후 등록하여야 하므로 하단부로 이동
			*   - 소재이송지시 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드 수정
			**********************************************************/
			//차량하차여부(공통Table 소재인수일시 수정 송신)
			String carUdCmplYn = "N";
			
			if ("PT".equals(ydUpWrLoc.substring(2, 4)) && !"PT".equals(ydDnWrLoc.substring(2, 4))) {

				
				//야드하차작업예약ID 차량하차스케줄 정보 조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STACK_COL_GP"  , ydUpWrLoc.substring(0, 6) ); 
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId);
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getAxYDL009CarSchUd
				--크레인권하실적 하차 차량스케줄 조회
				SELECT TS.YD_CAR_SCH_ID
				     , DECODE((SELECT COUNT(*) 
				                 FROM TB_YD_CARFTMVMTL 
				                WHERE YD_CAR_SCH_ID = TS.YD_CAR_SCH_ID
				                  AND DEL_YN = 'N'),0,'Y','N') AS CAR_UD_CMPL_YN --차량하차완료여부(권상실적에서 이송재료 삭제)
				     , TS.CAR_NO
				     , TS.ARR_WLOC_CD AS WLOC_CD
				     , TS.YD_PNT_CD3  AS YD_PNT_CD
				     , TS.TRANS_ORD_DATE
				     , TS.TRANS_ORD_SEQNO
				     , TS.TRANS_EQUIPMENT_TYPE  
				     , TS.YD_CAR_USE_GP
				     , TM.STL_NO
				  FROM TB_YD_CARSCH     TS
				     , TB_YD_CARFTMVMTL TM
				     , TB_YM_CRNSCH     CS
				 WHERE TS.YD_CAR_SCH_ID = TM.YD_CAR_SCH_ID
				   AND TS.DEL_YN = 'N'
				   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND CS.YD_CRN_GRAB_USE_RULE_ID = TS.YD_CAR_SCH_ID
				*/
				JDTORecordSet jsCarDnSch = commDao.select(recPara, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getAxYDL009CarSchUd", logId, methodNm, "하차 차량스케줄 조회 "); 
		    	
				if (jsCarDnSch.size() > 0) {
					JDTORecord jrCarDnSch = jsCarDnSch.getRecord(0);
					
					CarNo 			= commUtils.trim(jrCarDnSch.getFieldString("CAR_NO")); 
					TransOrdDate	= commUtils.trim(jrCarDnSch.getFieldString("TRANS_ORD_DATE"));
					TransOrdSeqNo 	= commUtils.trim(jrCarDnSch.getFieldString("TRANS_ORD_SEQNO")); 
					WlocCd 			= commUtils.trim(jrCarDnSch.getFieldString("WLOC_CD")); 
					ydPndCd 		= commUtils.trim(jrCarDnSch.getFieldString("YD_PNT_CD")); 					
					ydCarSchId  	= commUtils.trim(jrCarDnSch.getFieldString("YD_CAR_SCH_ID"));
					carUdCmplYn 	= commUtils.trim(jrCarDnSch.getFieldString("CAR_UD_CMPL_YN")); //차량하차완료여부
					equipType 		= commUtils.trim(jrCarDnSch.getFieldString("TRANS_EQUIPMENT_TYPE")); //운송장비 TYPE P:PDA
					ydCarUseGp 		= commUtils.trim(jrCarDnSch.getFieldString("YD_CAR_USE_GP")); 		//야드차량사용구분
					
					commUtils.printLog(logId, methodNm+  "★★★★★  권하차량스케쥴:" + ydCarSchId + " 차량하차완료여부:" + carUdCmplYn + " ★★★★", "SL");
					
					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(jrCarDnSch.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
					

					
						
					//차량하차완료이면
					if ("Y".equals(carUdCmplYn)) {
						
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setResultCode(logId);	//Log ID
						recPara.setResultMsg(methodNm);	//Log Method Name
						recPara.setField("MODIFIER" 		, modifier     ); 
						recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
						recPara.setField("WR_DT" 			, currDt ); 

						//하차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYDL009CarSchUd 
						--크레인권하실적 하차 차량스케줄 수정 
						UPDATE TB_YD_CARSCH 
						   SET MODIFIER         = :V_MODIFIER
							  ,MOD_DDTT         = SYSDATE
						      ,YD_EQP_WRK_STAT  = 'U' --공차
						      ,YD_CAR_PROG_STAT = 'E' --하차완료
						      ,YD_CARUD_CMPL_DT = NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
						 WHERE YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
						   AND DEL_YN           = 'N'
						*/    
						commDao.update(recPara, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYDL009CarSchUd", logId, methodNm, "하차 차량스케줄 수정  ");
						
	    				
	    				/**********************************************************
	    				* YDTSJ010 SLAB 하차완료 권상시 전송여부 체크
	    				**********************************************************/
	    				String sYDTSJ010_YN = "N";
	    				
	    				jrParam.setField("REPR_CD_GP"	, "YM2003");
	    				jrParam.setField("CD_GP"		, "2");
	    				jrParam.setField("ITEM"			, "YDTSJ010");
	    				jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "야드 기준 조회"); 
	    				
	    				if(jsChk.size() > 0) {
	    					sYDTSJ010_YN = jsChk.getRecord(0).getFieldString("DTL_ITM1");
	    				}
	    				
	    				if("N".equals(sYDTSJ010_YN)) { 
							
			    			//하차작업완료 송신 YDTSJ010
		    				jrYdMsg = JDTORecordFactory.getInstance().create();
		    				jrYdMsg.setResultCode(logId);	//Log ID
		    				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
		    				jrYdMsg.setField("WR_DT"	        , currDt);
		    				jrYdMsg.setField("YD_CAR_SCH_ID"	, ydCarSchId);
		    				
	        				//구내운송 소재차량하차완료
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ010_BSLAB", jrYdMsg));	
	    				}
						
						
	    				JDTORecord sndL2Msg2 = JDTORecordFactory.getInstance().create();
	    				sndL2Msg2.setResultCode(logId);	//Log ID
	    				sndL2Msg2.setResultMsg(methodNm);	//Log Method Name
	    				sndL2Msg2.setField("YD_INFO_SYNC_CD"	, "4"                      ); //야드정보동기화코드
	    				sndL2Msg2.setField("MSG_GP"				, "I"                      ); //전문구분
	    				sndL2Msg2.setField("STACK_COL_GP"    	, ydUpWrLoc.substring(0, 6));
	    				sndL2Msg2.setField("STACK_BED_GP"    	, "01"					   );
	    				commUtils.printParam(logId, sndL2Msg2);
	    	 
	    				//전송 Data 생성
	    				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L001", sndL2Msg2));		

					}
				} else {
					//야드하차작업예약ID가 없는 경우 권상실적수신에서 작업예약 재료번호로 차량하차스케줄ID를 조회하여 작업예약ID 등록
					commUtils.printLog(logId, "권하실적 하차 차량스케줄이 없습니다.", "SL");
				}
			}
			
			/**********************************************************
			* 7. 설비, 적치단 , 크레인스케쥴, 저장품, 작업예약재료, 작업예약 수정
			* 7.1 설비 야드설비상태(권하완료) 수정
			* 7.2 적치단
			*   - 크레인 재료정보 삭제
			*   - 권하위치 재료정보 수정
			*   - 권하위치외 같은 재료번호로 등록된 적치단 수정(권하분리 재료 제외)
			* 7.3 크레인스케쥴
			*   - 크레인작업재료 삭제
			*   - 크레인스케쥴 권하실적 수정 및 삭제
			* 7.4 작업예약 마지막 크레인스케쥴 이면
			*   - 작업예약재료 삭제
			*   - 작업예약 수정 및 삭제
			**********************************************************/
			
			//Crane 설비상태 권하완료(4)으로 수정
			jrParam.setField("YD_EQP_ID"    	, ydEqpId); //야드설비ID(크레인)
			jrParam.setField("YD_EQP_STAT"  	, "4"    ); //야드설비상태(권하완료)
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp 
			--설비 상태 수정 
			UPDATE TB_YM_EQUIP
			   SET MODIFIER    = :V_MODIFIER
			      ,MOD_DDTT    = SYSDATE
			      ,WPROG_STAT = :V_YD_EQP_STAT
			 WHERE EQUIP_GP    = :V_YD_EQP_ID
			   AND DEL_YN      = 'N'
			*/	   
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp", logId, methodNm, "설비상태 권상완료(4)으로 수정");

			//권하한 SLAB가 존재하는 적치단을 모두 Clear 한다.
			jrParam.setField("YD_CRN_SCH_ID"   , ydCrnSchId); //야드크레인스케쥴ID
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.clrUpDnWrkMtl 
			UPDATE TB_YM_STACKLAYER
			   SET MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			     , STOCK_ID            = NULL
			     , STACK_LAYER_STAT = 'E'
			 WHERE 1=1 --STACK_LAYER_STAT IN ('U','D')
			   AND STOCK_ID  IN (
				            SELECT STOCK_ID 
				              FROM TB_YM_CRNWRKMTL
				             WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			                    )
			   AND SUBSTR(STACK_COL_GP,1,1) IN ('2','3')
			*/
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.clrUpDnWrkMtl", logId, methodNm, "권하한 SLAB가 존재하는 적치단을 모두 Clear 한다. ");
			
//			boolean chgDnWrLayer = false; //권하위치 적치단 변경여부
			
				
			//-----------------------------------------------------------------------------------------------------------------------------------------
			//권하위치 DB상의 권하 Max단 체크
//			
//			jrParam.setField("STACK_COL_GP" 	, ydDnWrLoc.substring(0, 6)); //야드적치열구분
//			jrParam.setField("STACK_BED_GP" 	, ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
//			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMaxLayer
//			--권하위치 DB상의 권하 Max단 체크
//			SELECT LPAD(NVL(MAX(TO_NUMBER(STACK_LAYER_GP)),0)+1,2,'0') AS STACK_LAYER_GP
//			  FROM TB_YM_STACKLAYER A
//			 WHERE STACK_COL_GP = :V_STACK_COL_GP
//			   AND STACK_BED_GP = :V_STACK_BED_GP
//			   AND STACK_LAYER_STAT IN ('L','C') */
//			jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMaxLayer", logId, methodNm, "현재정보  조회");
//			
//			if (jsChk.size() > 0) {
//				jrChk = jsChk.getRecord(0);
//				String tbDnWrLayer = commUtils.trim(jrChk.getFieldString("STACK_LAYER_GP"));
//				
//				if (!ydDnWrLayer.equals(tbDnWrLayer)) {
//					commUtils.printLog(logId, "권하위치[" + ydDnWrLoc + "] 적치단 변경 : " + ydDnWrLayer + " -> " + tbDnWrLayer, "SL");
//					chgDnWrLayer = true;
//					ydDnWrLayer  = tbDnWrLayer;
//				}
//			}
			//-----------------------------------------------------------------------------------------------------------------------------------------
			
			//권하위치 적치단에 SLAB 적치시킨다.
			jrParam.setField("STACK_COL_GP" 	, ydDnWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("STACK_BED_GP" 	, ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
			jrParam.setField("STACK_LAYER_GP" 	, ydDnWrLayer); //야드적치단
			jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId); //야드크레인스케쥴ID
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrDnWrLoc  
			--크레인권하실적 적치단 수정
			MERGE INTO TB_YM_STACKLAYER SL USING (

			SELECT  A.* 
			       
			         ,  (SELECT TO_CHAR(TO_NUMBER(MIN(STACK_LAYER_GP)  ) +(RN-1) ,'FM00')  
			                              FROM TB_YM_STACKLAYER
			                             WHERE STACK_COL_GP = :V_STACK_COL_GP
			                              AND  STACK_BED_GP = :V_STACK_BED_GP
			                              AND  STACK_LAYER_ACTIVE_STAT ='E'
			                              AND  STACK_LAYER_STAT ='E')  AS STACK_LAYER_GP
			          
			  FROM (
			            SELECT :V_STACK_COL_GP     AS STACK_COL_GP 
			                 , :V_STACK_BED_GP     AS STACK_BED_GP
			                 , :V_STACK_LAYER_GP   AS DNWR_STACK_LAYER_GP
			                 , STACK_LAYER_GP      AS STACK_LAYER_GP_FROM
			                 , A.STOCK_ID
			                 , 'C'                 AS STACK_LAYER_STAT
			                 , ROW_NUMBER() OVER(  ORDER BY STACK_LAYER_GP) RN
			              FROM TB_YM_CRNWRKMTL A
			                 , TB_YM_CRNSCH    B
			             WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			               AND A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
			               AND A.DEL_YN = 'N'
			               AND B.DEL_YN = 'N'
			             ORDER BY STACK_LAYER_GP
			       )   A  

			) DD ON (SL.STACK_COL_GP = DD.STACK_COL_GP AND SL.STACK_BED_GP = DD.STACK_BED_GP AND SL.STACK_LAYER_GP = DD.STACK_LAYER_GP)
			WHEN MATCHED THEN UPDATE SET
				 SL.MODIFIER         = :V_MODIFIER 
			    ,SL.MOD_DDTT         = SYSDATE
			    ,SL.STOCK_ID         = DD.STOCK_ID
			    ,SL.STACK_LAYER_STAT = DD.STACK_LAYER_STAT*/		
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrDnWrLoc", logId, methodNm, "크레인권하실적 적치단 수정 ");

			//---------------------------------------------------------------------------
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치
			resMsg.setField("YD_DN_WR_LOC", ydDnWrLoc); //야드권하실적위치

			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
			}
			//---------------------------------------------------------------------------
			
			if ("WB".equals(ydDnWrLoc.substring(2, 4))) {
				//권하위치가 W/B 이면 Tracking table 변경 (화면 표시용)
				
				//Tracking 테이블 W/B 01Bed 01단이 비어 있지 않고 단 정보 W/B 01BED 01단과 정보가 다를 경우 Shift
				jrParam.setField("YD_GP"	, "2");
				jrParam.setField("PROC_GP"	, "W");
				jrParam.setField("EQUIP_GP"	, "WB11");
				jrParam.setField("COL_GP"	, "2CWB01");
				jrParam.setField("BED_GP"	, "01");
				jrParam.setField("LYR_GP"	, "01");
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getChkTrackingAndLayerInfo
				SELECT (
				            SELECT STL_NO
				              FROM TB_YM_EQPTRACKING
				             WHERE YD_GP = :V_YD_GP
				               AND PROC_GP = :V_PROC_GP
				               AND EQUIP_GP = :V_EQUIP_GP
				       ) T_STL_NO
				      ,(
				            SELECT STOCK_ID 
				              FROM TB_YM_STACKLAYER
				             WHERE STACK_COL_GP = :V_COL_GP
				               AND STACK_BED_GP = :V_BED_GP
				               AND STACK_LAYER_GP = :V_LYR_GP
				       ) L_STOCK_ID
				  FROM DUAL   */ 
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getChkTrackingAndLayerInfo", logId, methodNm, "Tracking 정보와 적치단 정보 비교"); 
				
				if(jsChk.size() > 0) {
					if(!"".equals(jsChk.getRecord(0).getFieldString("T_STL_NO")) && !jsChk.getRecord(0).getFieldString("T_STL_NO").equals(jsChk.getRecord(0).getFieldString("L_STOCK_ID"))) {
						
						/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEqpTrackingWShift
						MERGE INTO TB_YM_EQPTRACKING ET USING (

						    SELECT A.YD_GP
						          ,A.PROC_GP
						          ,A.EQUIP_GP
						          ,A.STL_NO
						          ,'WB'||DECODE(TO_BED_NO,6,1,TO_BED_NO)||SUBSTR(A.EQUIP_GP,4,1) AS TO_EQUIP_GP
						          ,DECODE(TO_BED_NO,6,'',A.STL_NO) AS TO_STL_NO 
						      FROM (
						                SELECT YD_GP
						                      ,PROC_GP
						                      ,EQUIP_GP
						                      ,STL_NO
						                      ,(TO_NUMBER(SUBSTR(EQUIP_GP,3,1))+1) AS TO_BED_NO
						                  FROM TB_YM_EQPTRACKING
						                 WHERE YD_GP = '2'
						                   AND PROC_GP = 'W'
						                 ORDER BY SORT_SEQ  
						           ) A
						  
						) DD ON (ET.YD_GP = DD.YD_GP AND ET.PROC_GP = DD.PROC_GP AND ET.EQUIP_GP = DD.TO_EQUIP_GP )
						WHEN MATCHED THEN UPDATE SET
						     STL_NO     = DD.TO_STL_NO
						   , MOD_DDTT   = SYSDATE
						   , MODIFIER   = :V_MODIFIER	 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEqpTrackingWShift", logId, methodNm, "WB Tracking Table 정보 Shift 처리 ");
					}
				}
				
				//적치단 정보를 Tracking Table 설정 (WB 01Bed만)
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updWBTrackingByLyrInfo
				MERGE INTO TB_YM_EQPTRACKING ET USING (

				    SELECT '2' AS YD_GP
				          ,'W' AS PROC_GP
				          ,'WB1' || TO_NUMBER(STACK_LAYER_GP) AS EQUIP_GP
				          , STOCK_ID AS STL_NO
				    FROM   TB_YM_STACKLAYER
				    WHERE  STACK_COL_GP = '2CWB01'
				    AND    STACK_BED_GP = '01'

				  
				) DD ON (ET.YD_GP = DD.YD_GP AND ET.PROC_GP = DD.PROC_GP AND ET.EQUIP_GP = DD.EQUIP_GP )
				WHEN MATCHED THEN UPDATE SET
				     STL_NO     = DD.STL_NO
				   , MOD_DDTT   = SYSDATE
				   , MODIFIER   = :V_MODIFIER			 */		
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updWBTrackingByLyrInfo", logId, methodNm, "적치단 WB 01 BED 정보를 Tracking 테이블에 update ");
				
				
				//Tracking Table 2CWB01 05BED 정보를 적치단에 설정
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updWBLyrInfoByTracking
				MERGE INTO TB_YM_STACKLAYER SL USING (

				    SELECT STACK_COL_GP
				          ,STACK_BED_GP
				          ,LOC_NO
				          ,STL_NO
				      FROM TB_YM_EQPTRACKING   
				     WHERE YD_GP = '2'
				       AND PROC_GP = 'W'
				       AND EQUIP_GP LIKE 'WB5%'

				) DD ON (SL.STACK_COL_GP = DD.STACK_COL_GP AND SL.STACK_BED_GP = DD.STACK_BED_GP AND SL.STACK_LAYER_GP = DD.LOC_NO )
				WHEN MATCHED THEN UPDATE SET
				     STOCK_ID   = DD.STL_NO
				   , MOD_DDTT   = SYSDATE
				   , MODIFIER   = :V_MODIFIER	 	 */ 			
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updWBLyrInfoByTracking", logId, methodNm, "Tracking 테이블에서 적치단 WB 05 BED 정보를  update ");
			}
			
			if ("2ACT02".equals(ydDnWrLoc.substring(0, 6))) {
				//권하위치가 2ACT02 이면 Tracking table 변경 (화면 표시용)
				
				//적치단 정보를 Tracking Table 설정 (2ACT02 01Bed만)
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCTCTrackingByLyrInfo
				MERGE INTO TB_YM_EQPTRACKING ET USING (

				    SELECT '2' AS YD_GP
				          ,'C' AS PROC_GP
				          ,'CTC21' || TO_NUMBER(STACK_LAYER_GP) AS EQUIP_GP
				          , STOCK_ID AS STL_NO
				    FROM   TB_YM_STACKLAYER
				    WHERE  STACK_COL_GP = '2ACT02'
				    AND    STACK_BED_GP = '01'

				  
				) DD ON (ET.YD_GP = DD.YD_GP AND ET.PROC_GP = DD.PROC_GP AND ET.EQUIP_GP = DD.EQUIP_GP )
				WHEN MATCHED THEN UPDATE SET
				     STL_NO     = DD.STL_NO
				   , MOD_DDTT   = SYSDATE
				   , MODIFIER   = :V_MODIFIER			 */		
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCTCTrackingByLyrInfo", logId, methodNm, "적치단 2ACT02 01 BED 정보를 Tracking 테이블에 update ");
			}
			
			/**********************************************************
			* 8. 장입 보급 순서 초기화 및 복구
			* 8.1 W/B보급, CTC보급 스케줄에서 권하위치가 W/B,CTC일 경우
			*   - STOCK의 CHARGE_LOT_NO를 Clear
			*   - CTS_RELAY_SADDLE 에 장입순번 설정
			*   - 생산통제 장입진행실적 송신 - YDCTJ032
			*   
			* 8.2 W/B TakeOut, CTC TakeOut 스케줄에서 권상위치가 W/B,CTC 이고
			*     권하위치가 W/B, CTC가 아니면 
			*   - 장입보급 순서 복구 기준이 Y 이면
			*   - CTS_RELAY_SADDLE 값이 있으면 CHARGE_LOT_NO 에 설정
			*   - CTS_RELAY_SADDLE 를 Clear
			**********************************************************/
			if(((!"WB".equals(ydUpWrLoc.substring(2,4))&&!"CT".equals(ydUpWrLoc.substring(2,4)))&&"WB".equals(ydDnWrLoc.substring(2,4))) ||  // 
			   ((!"WB".equals(ydUpWrLoc.substring(2,4))&&!"CT".equals(ydUpWrLoc.substring(2,4)))&&"CT".equals(ydDnWrLoc.substring(2,4))) ) {	 	
			
			//if(("2CWB01UM".equals(ydSchCd)&&"WB".equals(ydDnWrLoc.substring(2,4))) ||   //C동 W/B보급
			//   ("2ACT01UM".equals(ydSchCd)&&"CT".equals(ydDnWrLoc.substring(2,4))) ||   //A동 CTC보급
			//   ("2CCT01UM".equals(ydSchCd)&&"CT".equals(ydDnWrLoc.substring(2,4))) ) {  //C동 CTC보급
				
				//저장품 - CTS_RELAY_SADDLE 에 장입순번 설정 후 STOCK의 CHARGE_LOT_NO를 Clear
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); 
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockChargeLotNoClear 
				--CHARGE_LOT_NO 항목 CLEAR
				MERGE INTO TB_YM_STOCK ST USING (

				    SELECT A.STOCK_ID
				          ,C.CHARGE_LOT_NO
				          ,C.CTS_RELAY_SADDLE
				      FROM TB_YM_CRNWRKMTL A
				         , TB_YM_CRNSCH    B
				         , TB_YM_STOCK     C
				     WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				       AND A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
				       AND A.DEL_YN = 'N'
				       AND B.DEL_YN = 'N'
				       AND A.STOCK_ID = C.STOCK_ID
				       
				) DD ON (ST.STOCK_ID = DD.STOCK_ID )
				WHEN MATCHED THEN UPDATE SET
					 ST.MODIFIER         = :V_MODIFIER
				    ,ST.MOD_DDTT         = SYSDATE
				    ,ST.CHARGE_LOT_NO    = ''
				    ,ST.CTS_RELAY_SADDLE = DD.CHARGE_LOT_NO */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockChargeLotNoClear", logId, methodNm, "저장품 - CTS_RELAY_SADDLE 에 장입순번 설정 후 STOCK의 CHARGE_LOT_NO를 Clear ");
				
				//생산통제 장입진행실적 송신 - YDCTJ032 
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); 
				jrParam.setField("CHG_SUP_PROG_STAT", YmConstant.TC_YMPC020); //장입보급진행상태
				
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDCTJ032", jrParam));
			}			
			

			//-----------------------------------------------------------------------------------------------------------------------------------------
			//작업예약완료여부 체크
			String wbCmplYn = ""; 
			jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("YD_WBOOK_ID"  	, ydWbookId); //야드작업예약ID
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWbCmplYn
			--작업예약완료여부 체크
			SELECT (CASE WHEN COUNT(*)=1 THEN DECODE(MAX(CS.YD_CRN_SCH_ID),NULL,'Y',:V_YD_CRN_SCH_ID,'Y','N') 
			             WHEN COUNT(*)=0 THEN 'Y'
			             ELSE 'N' END) AS WB_CMPL_YN --작업예약 완료여부
			  FROM TB_YM_CRNSCH CS
			 WHERE CS.YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND CS.DEL_YN      = 'N' */
			jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWbCmplYn", logId, methodNm, "작업예약 완료여부 조회");
			
			if (jsChk.size() > 0) {

				jrChk = jsChk.getRecord(0);
				wbCmplYn = commUtils.trim(jrChk.getFieldString("WB_CMPL_YN"));
				commUtils.printLog(logId, "작업예약[" + ydWbookId + "] 완료여부 : " + wbCmplYn, "SL");
				
			} else {
				resMsg.setField("YD_L3_HD_RS_CD", "DN14"); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , "오류:작업예약 완료여부 조회 오류"); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			//-----------------------------------------------------------------------------------------------------------------------------------------
			
			
			if ("Y".equals(wbCmplYn)) { 
				//작업예약완료 이면..
				
				//작업예약재료 삭제
				jrParam.setField("YD_WBOOK_ID"  	, ydWbookId); //야드작업예약ID
				jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId); //야드크레인스케쥴ID
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYDL009WbMtlDel
				UPDATE TB_YM_WRKBOOKMTL
				   SET DEL_YN = 'Y'
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND STOCK_ID IN ( 
				                        SELECT STOCK_ID 
				                        FROM   TB_YM_CRNWRKMTL
				                        WHERE  YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                    ) */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYDL009WbMtlDel", logId, methodNm, "작업예약재료 삭제");
				
				//작업예약 수정 및 삭제
				jrParam.setField("YD_WBOOK_ID"  	, ydWbookId); //야드작업예약ID
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updAxYDL009WbDel
				--크레인권하실적 작업예약 삭제 
				UPDATE TB_YM_WRKBOOK
				   SET MODIFIER         = :V_MODIFIER
				      ,MOD_DDTT         = SYSDATE
				      ,DEL_YN           = 'Y'
				      ,YD_SCH_PROG_STAT = 'E' --End
				 WHERE YD_WBOOK_ID      = :V_YD_WBOOK_ID
				   AND DEL_YN           = 'N' */				
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updAxYDL009WbDel", logId, methodNm, "작업예약 수정 및 삭제");
				
			} else {
				//작업예약 미완료 이면..
				
				//크레인작업재료번호로 작업예약재료 삭제 (작업예약재료에 작업이 완료된 재료는 DEL_YN='Y' 함으로써 스케줄 취소 후 재 작업시 작업대상에서 제외시킨다.) 
				jrParam.setField("YD_WBOOK_ID"  	, ydWbookId); //야드작업예약ID
				jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId); //야드크레인스케쥴ID
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYDL009WbMtlDel
				UPDATE TB_YM_WRKBOOKMTL
				   SET DEL_YN = 'Y'
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND STOCK_ID IN ( 
				                        SELECT STOCK_ID 
				                        FROM   TB_YM_CRNWRKMTL
				                        WHERE  YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                    ) */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYDL009WbMtlDel", logId, methodNm, "작업예약재료 삭제");
			}			
			
			
			//크레인작업재료 삭제
			jrParam.setField("YD_CRN_SCH_ID"   , ydCrnSchId); //야드크레인스케쥴ID
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA7YDL009CrnMtl
			--크레인권하실적 크레인작업재료 삭제 -
			UPDATE TB_YM_CRNWRKMTL
			   SET MODIFIER      = :V_MODIFIER
				  ,MOD_DDTT      = SYSDATE
			      ,DEL_YN        = 'Y'
			 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			   AND DEL_YN        = 'N'
			*/	   
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA7YDL009CrnMtl", logId, methodNm, "크레인작업재료 삭제");
			
			//크레인스케쥴 권하실적 수정 및 삭제
			jrParam.setField("YD_DN_CMPL_DT"   , currDt      ); //야드권하완료일시
			jrParam.setField("YD_DN_WR_LOC"    , ydDnWrLoc   ); //야드권하실적위치
			jrParam.setField("YD_DN_WR_LAYER"  , ydDnWrLayer ); //야드권하실적단
			jrParam.setField("YD_DN_WRK_ACT_GP", ydDnWrkActGp); //야드권하작업수행구분
			jrParam.setField("YD_DN_WR_XAXIS"  , ydCrnXaxis  ); //야드권하실적X축
			jrParam.setField("YD_DN_WR_YAXIS"  , ydCrnYaxis  ); //야드권하실적Y축
			jrParam.setField("YD_DN_WR_ZAXIS"  , ydCrnZaxis  ); //야드권하실적Z축
			jrParam.setField("YD_DN_WRK_MODE2" , ydEqpWrkMode2 ); //모드 추가
			jrParam.setField("YD_CRN_SCH_ID"   , ydCrnSchId); //야드크레인스케쥴ID
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYDL009CrnSch
			-- 크레인권하실적 크레인스케줄 수정 
			UPDATE TB_YM_CRNSCH CS
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
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYDL009CrnSch", logId, methodNm, "크레인스케쥴 권하실적 수정");
			
			/**********************************************************
			* 8. 공통 Table, 저장품, 작업이력 등록 (순서 변경 안됨)
			* 8.1 
			*   - 크레인스케줄 재료를 대상
			*   - 권하실적위치로 저장위치 수정
			*   - 권상실적위치가 차량이면 현재진도코드 수정
			* 8.2 저장품 수정
			*   - 작업예약 재료를 대상
			*   - 작업예약이 삭제되었으면 작업예약ID, 스케줄코드 삭제
			*   - 현재진도코드가 저장품과 다르면 관련 항목(산적LotType 등) 수정
			*   - 저장위치가 저장품과 다르면 저장위치 수정
			* 8.3 작업이력 등록
			*   - 크레인스케줄 재료를 대상
			* 8.4 차량상차 또는 하차완료 시 차량이송재료 대상으로
			* 8.4.1 
			*     - 하차완료 시 차량이송재료 현재진도코드 수정 후
			* 8.4.1 소재이송지시 수정 : 권하실적재료 적치단 수정 후
			*     - 상차 : 이송상차일자, 야드재료예정저장From위치코드
			*     - 하차 : 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드
			* 8.4.2 
			*     - 상차 : 소재이송일시
			*     - 하차 : 소재인수일시
			**********************************************************/
			
			
			
			
			
			
			/**********************************************************
			*  권상위치가 보온뱅크(BK)가 아니고 권하위치가 보온뱅크(BK)이면
			*    보온뱅크 장입시간 UPDATE
			**********************************************************/
			if (!"BK".equals(ydUpWrLoc.substring(2, 4)) && "BK".equals(ydDnWrLoc.substring(2, 4))) {
				
				bSlabComm.updMSlabCommBkTimeStart(rcvMsg); //주편공통 보온뱅크(BK)장입시간  update
				
				bSlabComm.updSlabCommBkTimeStart(rcvMsg); //SLAB공통 Table 보온뱅크(BK)장입시간  UPDATE
			}

			String sSTOCK_ID;
			String sSTACK_LAYER_GP;
			String sYD_AID_WRK_YN;
			String sSTOCK_MOVE_TERM;
			String sCHARGE_LOT_NO;
				
			if(rsResult.size() > 0) {
				
				for(int ii = 0; ii < rsResult.size(); ii++) {
					
					sSTOCK_ID 		= commUtils.trim(rsResult.getRecord(ii).getFieldString("STOCK_ID"));
					sSTACK_LAYER_GP = commUtils.trim(rsResult.getRecord(ii).getFieldString("STACK_LAYER_GP"));
					sYD_AID_WRK_YN  = commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_AID_WRK_YN"));
					sCHARGE_LOT_NO  = commUtils.trim(rsResult.getRecord(ii).getFieldString("CHARGE_LOT_NO"));
					
					//주편공통 진행 상태가 진행중(2)인 경우 주편공통을 update 
					jrParam.setField("RECORD_PROG_STAT"	, "2"); 
					jrParam.setField("MSLAB_NO"			, sSTOCK_ID); 
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMSlabByRecordProgStat 
					SELECT MSLAB_NO
					  FROM TB_PT_MSLABCOMM
					 WHERE RECORD_PROG_STAT = :V_RECORD_PROG_STAT --진행중:2
					   AND MSLAB_NO= :V_MSLAB_NO */
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMSlabByRecordProgStat", logId, methodNm, "주편공통에 레코드상태가 진행중(2)인지 확인 "); 
					if(jsChk.size() > 0) {
						//주편공통의 LOC 정보를 Transaction 분리 하여 변경처리 한다.
						jrParam.setField("STOCK_ID"	, sSTOCK_ID);
						jrParam.setField("YD_LOC"	, ydDnWrLoc + sSTACK_LAYER_GP);
						bSlabComm.updMSlabCommLocInfo(jrParam);
					}
					
					//SLAB공통이 존재 하는 경우 SLAB공통 update 
					jrParam.setField("SLAB_NO"	, sSTOCK_ID); 
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabNoBySlabNo 
					SELECT SLAB_NO
					  FROM TB_PT_SLABCOMM
					 WHERE SLAB_NO= :V_SLAB_NO */
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabNoBySlabNo", logId, methodNm, "SLAB공통에 존재하는지 확인 "); 
					if(jsChk.size() > 0) {
						//SLAB공통의 LOC 정보를 Transaction 분리 하여 변경처리 한다.
						jrParam.setField("STOCK_ID"	, sSTOCK_ID);
						jrParam.setField("YD_LOC"	, ydDnWrLoc + sSTACK_LAYER_GP);
						bSlabComm.updSlabCommLocInfo(jrParam);
					}
					
					
					if("N".equals(sYD_AID_WRK_YN)) { //주 작업일 경우
						
						//저장품 CARUNLOAD_PUT_LOC = '' 수정 
						jrParam.setField("STOCK_ID"	, sSTOCK_ID); 
						jrParam.setField("YD_LOC"	, "");
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYDL009Stock 
						UPDATE TB_YM_STOCK
						   SET MODIFIER    = :V_MODIFIER
						     , MOD_DDTT    = SYSDATE
						     , CARUNLOAD_PUT_LOC = :V_YD_LOC
						 WHERE STOCK_ID    = :V_STOCK_ID
						*/ 
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYDL009Stock", logId, methodNm, "저장품 수정");
						
						//저장품 이동조건 설정 ----------------------------------------------------------------------start--
						sSTOCK_MOVE_TERM = "";
						
						if("TC".equals(ydDnWrLoc.substring(2, 4))) { 
//sjh							sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_TL;// 대차상차완료
//대차상차완료 없음							
						} else if("PT".equals(ydDnWrLoc.substring(2, 4))) {
//							sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_VL;// 차량상차완료
						} else if("SE".equals(ydDnWrLoc.substring(2, 4))) {
							sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_D1;// SCARFING 보급완료
						} else if("WB".equals(ydDnWrLoc.substring(2, 4))) {
							sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_F1;// W/B 보급완료
						
							//생산통제 장입진행실적 송신 
							jrRtn = commUtils.addSndData(jrRtn, bSlabComm.makeYDCTJ032(sSTOCK_ID, YmConstant.TC_YMPC030, logId ));

							//저장품제원(YMA8L002) 전문 생성
							jrParam.setField("MSG_GP"			, "U" 		); //전문구분(U:수정)
							jrParam.setField("YD_INFO_SYNC_CD"	, "5" 		); //야드정보동기화코드(지정저장품)
							jrParam.setField("STOCK_ID"		 	, sSTOCK_ID	);
							
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002", jrParam));
							
						} else if("CT".equals(ydDnWrLoc.substring(2, 4))) {
							sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_F2;// CTC 보급완료
						
							//생산통제 장입진행실적 송신 
							jrRtn = commUtils.addSndData(jrRtn, bSlabComm.makeYDCTJ032(sSTOCK_ID, YmConstant.TC_YMPC031, logId ));
							
							//저장품제원(YMA8L002) 전문 생성
							jrParam.setField("MSG_GP"			, "U" 		); //전문구분(U:수정)
							jrParam.setField("YD_INFO_SYNC_CD"	, "5" 		); //야드정보동기화코드(지정저장품)
							jrParam.setField("STOCK_ID"		 	, sSTOCK_ID	);
							
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002", jrParam));
							
						} else if("HB".equals(ydUpWrLoc.substring(2, 4))) {
							//권상위치가 HB 
							sSTOCK_MOVE_TERM = bSlabComm.getStockMoveTerm(sSTOCK_ID);
						} else if("HB".equals(ydDnWrLoc.substring(2, 4))) {
							//권하위치가 HB
							sSTOCK_MOVE_TERM = bSlabComm.getStockMoveTerm(sSTOCK_ID);
						}
						
						//if("PT02LM".equals(ydSchCd.substring(2,8))) { //이송하차 스케줄
						//	sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_C1;// 이송완료
						//}
						
						if(!"".equals(sSTOCK_MOVE_TERM)) {
							
							//TB_YM_STOCK의 STL_NO에 저장품 이동 조건 갱신
							jrParam.setField("STOCK_ID"			, sSTOCK_ID );
							jrParam.setField("STOCK_MOVE_TERM"	, sSTOCK_MOVE_TERM );
							
							bSlabDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 STL_NO에 저장품 이동 조건 갱신");
						}
						//----------------------------------------------------------------------------------------end--
						
						if(("WB".equals(ydUpWrLoc.substring(2,4))&&(!"WB".equals(ydDnWrLoc.substring(2,4))&&!"CT".equals(ydDnWrLoc.substring(2,4)))) || 
						   ("CT".equals(ydUpWrLoc.substring(2,4))&&(!"WB".equals(ydDnWrLoc.substring(2,4))&&!"CT".equals(ydDnWrLoc.substring(2,4)))) ) {
						
						//if(("2CWB01LM".equals(ydSchCd)&&"WB".equals(ydUpWrLoc.substring(2,4))&&!"WB".equals(ydDnWrLoc.substring(2,4))) || 	//C동 W/B TakeOut
						//   ("2CCT01LM".equals(ydSchCd)&&"CT".equals(ydUpWrLoc.substring(2,4))&&!"CT".equals(ydDnWrLoc.substring(2,4))) || 	//C동 CTC TakeOut
						//   ("2ACT01LM".equals(ydSchCd)&&"CT".equals(ydUpWrLoc.substring(2,4))&&!"CT".equals(ydDnWrLoc.substring(2,4))) ) {	//A동 CTC TakeOut
									
							//장입보급 순서 복구 기준이 Y 이면  장입 보급 순서 복구
							jrParam.setField("REPR_CD_GP"	, "YM3002");
							jrParam.setField("CD_GP"		, "2");
							JDTORecordSet rsYmRuleResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "야드 기준 조회"); 
							
							String sYM3002_RULE = "N";
							if(rsYmRuleResult.size() > 0) {
								sYM3002_RULE = rsYmRuleResult.getRecord(0).getFieldString("DTL_ITM1");
							}
							
							commUtils.printLog(logId, "=======:::: YM3002: " +  sYM3002_RULE , "SL");
							
			                if("Y".equals(sYM3002_RULE)) {

			                	/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockChargeLotNoRecovery
			                	UPDATE TB_YM_STOCK
			                	   SET CHARGE_LOT_NO = (SELECT LPAD(CTS_RELAY_SADDLE,6,'0') FROM TB_YM_STOCK WHERE STOCK_ID = :V_STOCK_ID)
			                	      ,CTS_RELAY_SADDLE = ''
			                	 WHERE STOCK_ID = :V_STOCK_ID  */
								jrParam.setField("STOCK_ID"			, sSTOCK_ID );
			    				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockChargeLotNoRecovery", logId, methodNm, "저장품 - CHARGE_LOT_NO 를 CTS_RELAY_SADDLE 값으로 복구 ");
			                }
						}
						
						
						//주작업이면서 장입번호가 있고 권상위치가 대차인 경우 생산통제 장입진행실적 송신
						if(!"".equals(sCHARGE_LOT_NO)) { 
							if("TC".equals(ydUpWrLoc.substring(2, 4))) {
								if(!"WB".equals(ydDnWrLoc.substring(2, 4)) && !"CT".equals(ydDnWrLoc.substring(2, 4))) {
									//생산통제 장입진행실적 송신 
									jrRtn = commUtils.addSndData(jrRtn, bSlabComm.makeYDCTJ032(sSTOCK_ID, YmConstant.TC_YMPC010, logId ));
								}
							}
						}
						
						//SLAB LINE OFF(구 SHLO) 완료시 CF1BP03 전송
						if("2AHB01LM".equals(ydSchCd) || "2CHB01LM".equals(ydSchCd)) {
							jrParam.setField("SLAB_NO"			, sSTOCK_ID );
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("CF1BP03", jrParam));
						}
						
						//CTC 보급(구 SCLI) 완료시 CF1BP12 전송
						if("2ACT01UM".equals(ydSchCd) || "2CCT01UM".equals(ydSchCd)) {
							jrParam.setField("SLAB_NO"			, sSTOCK_ID );
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("CF1BP12", jrParam));
						}
						
						//SCARFING 보급시 스카핑 작업지시 전송 
						if("2ESE01UM".equals(ydSchCd)) {
							jrParam.setField("SLAB_NO"			, sSTOCK_ID );
							/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectSlabMatirialInfo
							--스카핑 지시를 위한 정보 조회
							SELECT  SLAB_NO,
							        PLAN_SLAB_NO,               --예정 SLAB 번호
							        (CASE WHEN (SELECT C_WRSLT 
							                    FROM TB_PM_HEATWRSLTCOMM C     
							                    WHERE C.HEAT_NO=SLABCOMM.HEAT_NO           
							                     )>0.20         --//슬라브 고탄소재 인경우
							                THEN 'C' 
							                ELSE ''
							         END) AS BUY_SLAB_NO,       --구입 SLAB 번호(사용안함) 
							        ORD_NO,                     --주문 번호
							        ORD_DTL,                    --주문 행번
							        CC_PLNT_GP  AS PLANT_GP,    --공장구분
							        SLAB_T      AS SLAB_T,      --SLAB 두께
							        SLAB_W      AS SLAB_W,	    --SLAB 폭
							        SLAB_LEN    AS SLAB_LEN,	--SLAB 길이
							        CAL_SLAB_WT AS SLAB_WT,	    --SLAB 중량
							        CURR_PROG_CD,
							        HEAT_NO,
							        SPEC_ABBSYM,		        -- 규격약호
							        INGR_STAMP_GRADE,
							        REAGENT_PICK_TARGET_YN,     -- 시편채취유무
									REAGENTPICK_DONE_YN,        -- 시편완료유무
									SCARFING_YN,                -- Scarfing유무
									SCARFING_DONE_YN,           -- Scarfing완료유무
							        WO_MSLAB_RPR_MTD,	        -- Scarfing Pattern
									SCARFING_DEPTH,		        -- Scarfing 깊이
									'' AS INGR_C,				-- 성분C
									SLAB_WO_RT_CD,              -- SLAB지시행선코드
									ORD_HCR_GP,                 -- WCR/CCR 구분
									DECODE(ORD_HCR_GP,NULL,'0',
							               DECODE(LEAST(TRUNC((SYSDATE - nvl(SLAB_CREATE_DDTT,SLABCOMM.REG_DDTT))*24),DECODE(SCARFING_YN,'Y',24,12)),DECODE(SCARFING_YN,'Y',24,12),'0',NULL,'0','1')             
							        )AS TIMES,
							        DECODE(B.CHARGE_LOT_NO,null,'N','Y')CHARGE_LOT_NO
							        ,(SELECT MAX(SUBSTR(TRN_EQP_CD,2,2)) FROM USRYDA.TB_YD_CARFTMVMTL A
							            , USRYDA.TB_YD_CARSCH C
							           WHERE A.YD_CAR_SCH_ID=C.YD_CAR_SCH_ID
							             AND A.STL_NO=B.STOCK_ID
							             AND C.DEL_YN='N'
							             AND A.DEL_YN='N') AS TRN_EQP_CD
							        ,(CASE WHEN (SELECT 1
							                    FROM TB_PM_HEATWRSLTCOMM B         
							                   WHERE B.HEAT_NO=SLABCOMM.HEAT_NO
							                     AND B.C_WRSLT>=0.20)=1
							           AND SLABCOMM.SCARFING_DONE_YN='Y' THEN 'Y' ELSE 'N' END ) AS  C_WRSLT_CHK  
							      ,C3_SCARF_TRF_YN 
							      ,(SELECT C_WRSLT FROM TB_PM_HEATWRSLTCOMM WHERE HEAT_NO=SLABCOMM.HEAT_NO) AS C_WRSLT --카본함량
							FROM   (
							          SELECT A.*
							                ,B.BUY_SLAB_NO
							            FROM VW_YD_SLABCOMM A
							                ,TB_QM_BUYSLABINFO B
							           WHERE A.MSLAB_NO = B.MSLAB_NO(+)
							       ) SLABCOMM 
							      ,TB_YM_STOCK B
							WHERE  SLABCOMM.SLAB_NO = B.STOCK_ID
							AND    SLABCOMM.SLAB_NO = :V_SLAB_NO  */
							jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectSlabMatirialInfo", logId, methodNm, "스카핑 지시를 위한 정보 조회");
							if(jsChk.size()>0){
								JDTORecord sJr = bSlabComm.getRuleQMB518(commUtils.trim(jsChk.getRecord(0).getFieldString("WO_MSLAB_RPR_MTD"))); //Scarfing Pattern
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L009", sJr));
							}
						}
						
						if("PT02LM".equals(ydSchCd.substring(2,8))) { //이송하차 스케줄이면 이송하차완료처리
							
							//회송 이송하차는 이송하차완료처리를 하지 않는다.
							//회송테이블에 STOCK_ID 와 차량스케줄ID로  존재 한다면 이송하차 완료처리 하지 않는다.
							
							jrYdMsg			= JDTORecordFactory.getInstance().create();
	    					jrYdMsg = JDTORecordFactory.getInstance().create();
							jrYdMsg.setResultCode(logId);	//Log ID
							jrYdMsg.setResultMsg(methodNm);	//Log Method Name
							jrYdMsg.setField("STOCK_ID"			, sSTOCK_ID);
							jrYdMsg.setField("YD_CAR_SCH_ID"	, ydCarSchId);
							
							jsChk = commDao.select(jrYdMsg, "com.inisteel.cim.ym.bslab.dao.BSalbDAO.getRetHtHistByStock", logId, methodNm, "회송 재료인지 회송테이블 조회");
							
							if(jsChk.size() == 0) {
								
								//회송테이블에 존재 안할 경우만 이송하차완료처리를 수행한다.
								EJBConnector ejbConn = new EJBConnector("default", "YmCommSeEJB", this);
								JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procFtmvCmtl_SLAB", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
								jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
							} else {
								commUtils.printLog(logId, "■ ■ ■ STOCK_ID : " + sSTOCK_ID + " , YD_CAR_SCH_ID : " + ydCarSchId + " 는 회송 이송하차로 이송하차완료처리(YmCommSeEJB.procFtmvCmtl_SLAB)를 호출하지 않는다!! ■ ■ ■ ", "SL");
							}
							
							//이송하차 권하시 TB_YM_STOCK 의 좌표 정보를 Clear 한다.
							jrParam.setField("STOCK_ID"	, sSTOCK_ID); 
							/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockClearXYZaxis 
							UPDATE TB_YM_STOCK
							   SET MODIFIER = :V_MODIFIER
							      ,MOD_DDTT = SYSDATE
							      ,LOAD_LOC_CD = ''
							      ,WGT_CENTER_XAXIS = null
							      ,WGT_CENTER_YAXIS = null
							      ,WGT_CENTER_ZAXIS = null
							      ,CAU_CD = ''
							                               
							 WHERE STOCK_ID = :V_STOCK_ID */
							commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockClearXYZaxis", logId, methodNm, "이송하차 야드 권하처리시 스켄한 x,y,z 값을 STOCK 에서 clear");
							
						}
						
					}
				}
			}
			
			//작업이력 등록
			jrParam.setField("YD_CRN_SCH_ID"   , ydCrnSchId); 
			commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkHistSlab", logId, methodNm, "작업이력 등록");
			
			
			/**********************************************************
			* 9. 권하지시위치 단과 실적 단이 다르면 저장품제원 전문 전송(YMA8L002)
			**********************************************************/
			if (chgDnWrLayer) { //"5".equals(ydWrkProgStat) TODO 코일일때 강제권하일 때도 저장품제원 송신
				jrParam.setField("YD_INFO_SYNC_CD", "5"); //지정저장품
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002DnWr", jrParam));
			}
			
			/**********************************************************
			* 10. 권하위치 야드맵 전문 전송(YMA8L001)
			**********************************************************/
			jrParam.setField("YD_INFO_SYNC_CD"	, "4"); //1:동,2:SPAN,3:열,4:BED
			jrParam.setField("STACK_COL_GP" 	, ydDnWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("STACK_BED_GP" 	, ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L001", jrParam));
			
			
			/**********************************************************
			* 권하실적위치가 W/B 이면 선작업 여부 체크하여 장입요구 실행
			**********************************************************/
			
//			String sYM2011_2CWB01UM_YN = ymComm.BCoilApplyYn("YM2011","2","2CWB01UM");
//			
//			if("Y".equals(sYM2011_2CWB01UM_YN)) {
//				
//				if("2CWB01UM".equals(ydSchCd)) {
//					
//					jrParam.setField("EQUIP_GP"    , "2CWB01"); //W/B
//					/*
//					 SELECT HMI_STAT ,WORK_MODE
//		   			   FROM TB_YM_EQUIP
//		              WHERE EQUIP_GP   =  :V_EQUIP_GP
//					*/
//					jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectEquipInfo", logId, methodNm, "설비상태조회");
//					if(jsChk.size() > 0) {
//						if("1".equals(jsChk.getRecord(0).getFieldString("WORK_MODE"))) {
//							//W/B 보급 선작업 실행
//							jrParam.setField("JMS_TC_CD", "A8YML020" );
//							jrParam.setField("YD_WRK_PLAN_CRN", ydEqpId);
//							
//							commUtils.printLog(logId, "=======:::: 권하처리 스케줄코드 : " +  ydSchCd  + " WB장입(선작업)호출", "SL");
//							
//							jrRtn = commUtils.addSndData(jrRtn, this.rcvA8YML020(jrParam));
//						}
//					}	
//				} else {
//					
//					//WB장입 스케줄이 아니더라도 W/B보급크레인 이고 TB_YM_STACKLAYER 의 WB 01BED에
//					//적치된 또는 예약된 SLAB가 없으면 장입요청 호출
//					if("2C".equals(ydEqpId.substring(0,2))) {
//						
//						jrParam.setField("YD_SCH_CD"    , "2CWB01UM");
//						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule 
//						SELECT YD_SCH_CD
//						     , YD_WRK_CRN
//						     , YD_WRK_CRN_PRIOR
//						     , YD_MULTI_WRK_YN
//						  FROM TB_YM_SCHEDULERULE
//						 WHERE DEL_YN = 'N'
//						   AND YD_SCH_CD = :V_YD_SCH_CD */
//						jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케쥴기준조회");
//						
//						if(jsChk.size() > 0) {
//							if(ydEqpId.equals(jsChk.getRecord(0).getFieldString("YD_WRK_CRN"))) {
//								
//								commUtils.printLog(logId, "=======:::: 권하처리 스케줄코드 : " +  ydSchCd  + " 작업 크레인 : " + ydEqpId, "SL");
//								
//								jrParam.setField("STACK_COL_GP"    , "2CWB01");
//								jrParam.setField("STACK_BED_GP"    , "01");
//								/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPutStockIdByBedNo
//								SELECT  LYR.STACK_COL_GP
//								       ,LYR.STACK_BED_GP
//								       ,LYR.STACK_LAYER_GP
//								       ,LYR.STOCK_ID
//								       ,LYR.STACK_LAYER_STAT
//								       ,LYR.STACK_LAYER_ACTIVE_STAT
//								       ,(
//								            SELECT WBMTL.YD_WBOOK_ID
//								              FROM TB_YM_WRKBOOK    WB
//								                  ,TB_YM_WRKBOOKMTL WBMTL
//								             WHERE WB.YD_WBOOK_ID = WBMTL.YD_WBOOK_ID
//								               AND WB.DEL_YN = 'N'
//								               AND WBMTL.DEL_YN = 'N'
//								               AND WBMTL.STOCK_ID = LYR.STOCK_ID
//								        ) AS YD_WBOOK_ID       
//								  FROM  TB_YM_STACKLAYER LYR
//								 WHERE  LYR.STACK_COL_GP = :V_STACK_COL_GP
//								   AND  LYR.STACK_BED_GP = :V_STACK_BED_GP
//								   AND  LYR.STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT || '%'
//								   AND  LYR.STOCK_ID IS NOT NULL
//								  ORDER BY LYR.STACK_LAYER_GP DESC */
//								jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPutStockIdByBedNo", logId, methodNm, "WB 01Bed 조회");
//								
//								if(jsChk.size() == 0) {
//									
//									//W/B 보급 선작업 실행
//									jrParam.setField("JMS_TC_CD", "A8YML020" );
//									jrParam.setField("YD_WRK_PLAN_CRN", ydEqpId);
//									
//									commUtils.printLog(logId, "=======:::: 권하처리 스케줄코드 : WB 01 Bed가 빈 상태임으로  WB장입(선작업)호출", "SL");
//									
//									jrRtn = commUtils.addSndData(jrRtn, this.rcvA8YML020(jrParam));
//								}
//							}
//						}
//					}  
//				}
//			} 
			
			if ("WB".equals(ydDnWrLoc.substring(2, 4))) {

//				if("N".equals(sYM2011_2CWB01UM_YN)) {
					jrParam.setField("EQUIP_GP"    , "2CWB01"); //W/B
					/*
					 SELECT HMI_STAT ,WORK_MODE
		   			   FROM TB_YM_EQUIP
		              WHERE EQUIP_GP   =  :V_EQUIP_GP
					*/
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectEquipInfo", logId, methodNm, "설비상태조회");
					if(jsChk.size() > 0) {
						if("1".equals(jsChk.getRecord(0).getFieldString("WORK_MODE"))) {
							//W/B 보급 선작업 실행
							jrParam.setField("JMS_TC_CD", "A8YML020" );
							jrParam.setField("YD_WRK_PLAN_CRN", ydEqpId);
							
							jrRtn = commUtils.addSndData(jrRtn, this.rcvA8YML020(jrParam));
						}
					}	
//				}
				
				//WB의 01Bed 에 적치된('C') 슬라브를 지운다. 
				//적치단 WB 01BED 정보 Clear
				jrParam.setField("STACK_COL_GP"    	, "2CWB01"	);
				jrParam.setField("STACK_BED_GP"    	, "01"		);
				jrParam.setField("STACK_LAYER_STAT1", "C"		);
				jrParam.setField("STACK_LAYER_STAT2", "E"		);
				jrParam.setField("STOCK_ID"			, ""		);
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo
				UPDATE TB_YM_STACKLAYER
				   SET 
				       MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,STOCK_ID = :V_STOCK_ID
				      ,STACK_LAYER_STAT = :V_STACK_LAYER_STAT2
				 WHERE  STACK_COL_GP = :V_STACK_COL_GP
				   AND  STACK_BED_GP = :V_STACK_BED_GP
				   AND  STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT1 || '%' */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo", logId, methodNm, "적치단 WB 01BED 정보 Clear");
				
			}	
			
			
			/**********************************************************
			* 권하실적위치가 CTC 이면 선작업 여부 체크하여 장입요구 실행
			**********************************************************/
			if ("CT".equals(ydDnWrLoc.substring(2, 4)) ) {
				
				if("2CCT01UM".equals(ydSchCd) || "2ACT01UM".equals(ydSchCd)) { //CTC보급일 경우만
					
					if("2ACT01".equals(ydDnWrLoc.substring(0, 6))) {
						jrParam.setField("EQUIP_GP" , "2ACT01"); //CTC#1
						jrParam.setField("POSITION"	, "1");
//					} else if("2ACT02".equals(ydDnWrLoc.substring(0, 6))) {
//						jrParam.setField("EQUIP_GP" , "2ACT02"); //CTC#2
//						jrParam.setField("POSITION"	, "2");
					} else if("2CCT04".equals(ydDnWrLoc.substring(0, 6))) {
						jrParam.setField("EQUIP_GP" , "2CCT04"); //CTC#4
						jrParam.setField("POSITION"	, "4");
					}
				
					/*
					 SELECT HMI_STAT ,WORK_MODE
		   			   FROM TB_YM_EQUIP
		              WHERE EQUIP_GP   =  :V_EQUIP_GP
					*/
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectEquipInfo", logId, methodNm, "설비상태조회");
					if(jsChk.size() > 0) {
						if("1".equals(jsChk.getRecord(0).getFieldString("WORK_MODE")) || "O".equals(jsChk.getRecord(0).getFieldString("WORK_MODE"))) {
							//CTC 보급 선작업 실행
							jrParam.setField("JMS_TC_CD", "A8YML019" );
							jrParam.setField("YD_WRK_PLAN_CRN", ydEqpId);
							jrParam.setField("SLAB_NO", "");
							
							jrRtn = commUtils.addSndData(jrRtn, this.rcvA8YML019(jrParam));
						}
					}
				}
				
				if("2ACT02".equals(ydDnWrLoc.substring(0, 6))) {
					//CTC #2 의 01Bed 에 적치된('C') 슬라브를 지운다. 
					//적치단 WB 01BED 정보 Clear
					jrParam.setField("STACK_COL_GP"    	, "2ACT02"	);
					jrParam.setField("STACK_BED_GP"    	, "01"		);
					jrParam.setField("STACK_LAYER_STAT1", "C"		);
					jrParam.setField("STACK_LAYER_STAT2", "E"		);
					jrParam.setField("STOCK_ID"			, ""		);
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo
					UPDATE TB_YM_STACKLAYER
					   SET 
					       MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,STOCK_ID = :V_STOCK_ID
					      ,STACK_LAYER_STAT = :V_STACK_LAYER_STAT2
					 WHERE  STACK_COL_GP = :V_STACK_COL_GP
					   AND  STACK_BED_GP = :V_STACK_BED_GP
					   AND  STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT1 || '%' */
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo", logId, methodNm, "적치단 CTC#2 01BED 정보 Clear");
				}
				
				if("2CHB02UM".equals(ydSchCd)) {
					//C동 STE 비상 보급 일 경우
					
					//CTC#4의 01Bed 에 적치된('C') 슬라브를 지운다. 
					//적치단 WB 01BED 정보 Clear
					jrParam.setField("STACK_COL_GP"    	, "2CCT04"	);
					jrParam.setField("STACK_BED_GP"    	, "01"		);
					jrParam.setField("STACK_LAYER_STAT1", "C"		);
					jrParam.setField("STACK_LAYER_STAT2", "E"		);
					jrParam.setField("STOCK_ID"			, ""		);
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo
					UPDATE TB_YM_STACKLAYER
					   SET 
					       MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,STOCK_ID = :V_STOCK_ID
					      ,STACK_LAYER_STAT = :V_STACK_LAYER_STAT2
					 WHERE  STACK_COL_GP = :V_STACK_COL_GP
					   AND  STACK_BED_GP = :V_STACK_BED_GP
					   AND  STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT1 || '%' */
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo", logId, methodNm, "적치단 CTC#4 01BED 정보 Clear");
				}
				
				if("2AHB02UM".equals(ydSchCd)) {
					//A동 STE 비상 보급 일 경우
					
					//CTC#1의 01Bed 에 적치된('C') 슬라브를 지운다. 
					//적치단 WB 01BED 정보 Clear
					jrParam.setField("STACK_COL_GP"    	, "2ACT01"	);
					jrParam.setField("STACK_BED_GP"    	, "01"		);
					jrParam.setField("STACK_LAYER_STAT1", "C"		);
					jrParam.setField("STACK_LAYER_STAT2", "E"		);
					jrParam.setField("STOCK_ID"			, ""		);
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo
					UPDATE TB_YM_STACKLAYER
					   SET 
					       MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,STOCK_ID = :V_STOCK_ID
					      ,STACK_LAYER_STAT = :V_STACK_LAYER_STAT2
					 WHERE  STACK_COL_GP = :V_STACK_COL_GP
					   AND  STACK_BED_GP = :V_STACK_BED_GP
					   AND  STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT1 || '%' */
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo", logId, methodNm, "적치단 CTC#1 01BED 정보 Clear");
				}
			}
		
			if ("TC".equals(ydDnWrLoc.substring(2, 4)) && "Y".equals(tcarLdCmplYn)  ) {
				
				/******
				 * 대차 완료처리 현재동에서 크레인 스케줄이 남아 있으면 크레인 스케쥴 취소
				 * 대차상차인 크레인스케쥴 검색
				 *  
				 ******/ 						
				/******
				 * 현재동에서 크레인 스케줄이 남아 있으면 크레인 스케쥴 취소
				 * 대차상차인 크레인스케쥴 검색
				 *  
				 ****** 						
				
				/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getBeforeCrnSch
				SELECT  WB.YD_GP
				      , WB.YD_WBOOK_ID
				      , CR.YD_CRN_SCH_ID 
				      , WB.YD_WRK_PLAN_TCAR      
				      , CR.YD_SCH_CD  
				      , CM.STOCK_ID 
				   FROM TB_YM_WRKBOOK WB
				       ,TB_YM_CRNSCH  CR
				       ,TB_YM_CRNWRKMTL CM
				  WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				    AND WB.YD_SCH_CD   LIKE  '2_TC__UM'||'%'
				    AND WB.YD_SCH_CD   = CR.YD_SCH_CD
				    AND WB.DEL_YN      = 'N'
				    AND CR.DEL_YN      = 'N'
				    AND CR.YD_CRN_SCH_ID  = CM.YD_CRN_SCH_ID
				    AND CR.YD_CRN_SCH_ID  != :V_YD_CRN_SCH_ID */		
				
				JDTORecordSet jsCrnsch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getBeforeCrnSch", logId, methodNm, "크레인스케줄 조회");						
				 
				JDTORecord jrCrnSch 	= JDTORecordFactory.getInstance().create();
				
				JDTORecord jrParamTc1 = JDTORecordFactory.getInstance().create();

			    for(int Loop_i = 1; Loop_i <= jsCrnsch.size(); Loop_i++) {
			
	        		jsCrnsch.absolute(Loop_i);
	        		jrCrnSch  = jsCrnsch.getRecord();
				    	
				   
		        		jsCrnsch.absolute(Loop_i);
		        		jrCrnSch  = jsCrnsch.getRecord();

 
						jrParamTc1.setField("YD_WBOOK_ID"  	   , ydWbookId );
						jrParamTc1.setField("YD_CRN_SCH_ID"	   , commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID")));
						jrParamTc1.setField("YD_EQP_ID"    	   , ydEqpId   );
						jrParamTc1.setField("YD_SCH_CD"    	   , commUtils.trim(jrCrnSch.getFieldString("YD_SCH_CD")));
						jrParamTc1.setField("YD_L2_RETURN_FLAG", "Y"       );
						jrParamTc1.setField("IS_LAST_SELECTED" , "1"       );
						
						commUtils.printParam(logId, jrParamTc1);
				/**********************************************************
				* . 크레인스케줄 취소
				**********************************************************/
				EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
				JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParamTc1 });
				
				jrRtn = commUtils.addSndData(jrRtn, jrRst);
				 
			    }
			}						
			
		 
			String sAPP019_YN = ymComm.BCoilApplyYn("APP019","2","1");   
			commUtils.printLog(logId,  "동내이적 작업은 별도로  자동 기동:" + sAPP019_YN, "SL");	
			if(sAPP019_YN.equals("Y") ) {
				
				if("YD".equals(ydSchCd.substring(2, 4)) ) {
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdSchYn 
					SELECT CRN_SCH_CNT
					     , YD_WBOOK_ID  
					  FROM 
					(
					SELECT 1 AS TMP_KEY
					     , DECODE(COUNT(A.YD_CRN_SCH_ID),0,'N','Y') AS CRN_SCH_CNT
					  FROM TB_YM_CRNSCH A
					 WHERE A.DEL_YN = 'N'
					   AND A.YD_CRN_SCH_ID <> NVL(:V_YD_CRN_SCH_ID,1)
					   AND A.YD_SCH_CD LIKE '2_YD__MM'
					   AND A.YD_EQP_ID = :V_YD_EQP_ID 
					) A
					, 
					(
					SELECT 1 AS TMP_KEY
					     , YD_WBOOK_ID
					  FROM (
					        SELECT A.YD_WBOOK_ID
					             , A.YD_SCH_CD
					             , A.YD_SCH_CD_CNT
					             , A.YD_SCH_CD_YN
					             , A.YD_WRK_PLAN_TCAR
					             , A.YD_WRK_PLAN_CRN
					          FROM (
					                SELECT YD_WBOOK_ID
					                     , YD_SCH_CD
					                     , SUM(1) OVER(PARTITION BY YD_SCH_CD) AS YD_SCH_CD_CNT 
					                     , 'Y'  AS YD_SCH_CD_YN 
					                     , YD_WRK_PLAN_TCAR
					                     , YD_WRK_PLAN_CRN
					                     , SCH_CNCL_YN
					                     , (SELECT YD_SCH_AUTO_ST_YN 
					                          FROM TB_YM_SCHEDULERULE 
					                         WHERE YD_SCH_CD = WB.YD_SCH_CD 
					                           AND DEL_YN = 'N') AS YD_SCH_AUTO_ST_YN 
					                  FROM TB_YM_WRKBOOK WB
					                     , (SELECT :V_YD_EQP_ID  AS YD_EQP_ID FROM DUAL) PTBL
					                 WHERE WB.DEL_YN = 'N'
					                   AND WB.YD_SCH_CD LIKE '2_YD__MM'
					                   AND SUBSTR(WB.YD_SCH_CD,1,2) = SUBSTR(PTBL.YD_EQP_ID,1,2)
					                   AND '1' = CASE WHEN (SELECT COUNT(*)
					                                          FROM TB_YM_WRKBOOK A
					                                             , TB_YM_WRKBOOKMTL B
					                                             , TB_YM_STACKLAYER C
					                                             , TB_YM_CRNSCH   D
					                                         WHERE A.YD_WBOOK_ID  = B.YD_WBOOK_ID
					                                           AND A.YD_WBOOK_ID  = D.YD_WBOOK_ID
					                                           AND B.STOCK_ID     = C.STOCK_ID
					                                           AND C.STACK_COL_GP||C.STACK_BED_GP = D.YD_UP_WO_LOC
					                                           AND A.DEL_YN = 'N'
					                                           AND B.DEL_YN = 'N'
					                                           AND D.DEL_YN = 'N'
					                                           AND C.STACK_LAYER_GP >
					                                               (
					                                                SELECT NVL(MAX(C.STACK_LAYER_GP) ,'01')
					                                                  FROM TB_YM_WRKBOOK A
					                                                     , TB_YM_WRKBOOKMTL B
					                                                     , TB_YM_STACKLAYER C
					                                                 WHERE A.YD_WBOOK_ID  = B.YD_WBOOK_ID
					                                                   AND B.STOCK_ID     = C.STOCK_ID
					                                                   AND A.YD_WBOOK_ID  = WB.YD_WBOOK_ID
					                                               )                                                 
					                                           AND D.YD_DN_WO_LOC = 'XX010101'
					                                        ) > 0 
					                                  THEN '2'           
					                                  ELSE '1' END                                             
					                   AND TRN_EQP_CD       IS NULL
					                   AND YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
					                                             FROM TB_YM_CRNSCH
					                                            WHERE DEL_YN = 'N')
					                   AND YD_SCH_CD       IN (SELECT YD_SCH_CD
					                                             FROM TB_YM_SCHEDULERULE 
					                                            WHERE (YD_WRK_CRN = PTBL.YD_EQP_ID OR YD_ALT_CRN = PTBL.YD_EQP_ID)
					                                              AND DEL_YN = 'N')
					                   AND NVL((SELECT EQUIP_GP 
					                              FROM TB_YM_EQUIP A1
					                             WHERE A1.EQUIP_GP    = YD_WRK_PLAN_CRN
					                               AND A1.WORK_MODE   = '1'
					                               AND A1.WPROG_STAT <> 'B'), PTBL.YD_EQP_ID ) = PTBL.YD_EQP_ID
					                 ORDER BY (CASE WHEN NVL((SELECT EQUIP_GP 
					                                            FROM TB_YM_EQUIP A1
					                                           WHERE A1.EQUIP_GP = YD_WRK_PLAN_CRN
					                                             AND A1.WORK_MODE  = '1'
					                                             AND A1.WPROG_STAT <> 'B'), PTBL.YD_EQP_ID ) = PTBL.YD_EQP_ID THEN 1 ELSE 2 END )
					                        , YD_SCH_PRIOR
					                        , YD_WBOOK_ID     
					               ) A
					         WHERE 1=1
					           AND YD_SCH_CD_YN      = 'Y'        
					           AND YD_SCH_AUTO_ST_YN = 'Y'
					       )
					   WHERE ROWNUM = 1
					)B
					WHERE A.TMP_KEY = B.TMP_KEY(+)
	
					*/
					JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
					jrParam1.setField("YD_CRN_SCH_ID"   , ydCrnSchId); 
					jrParam1.setField("YD_EQP_ID"       , ydEqpId); 
					
					JDTORecordSet jsCrnsch1 = commDao.select(jrParam1, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdSchYn", logId, methodNm, "동내이적크레인스케줄 조회");						
					if(jsCrnsch1.size() > 0 ) {
						// 크레인 작업이 없고 동내이적 작업이 있으면 동내이적 스케쥴 기동처리
						String ydCrnSchCnt 	= jsCrnsch1.getRecord(0).getFieldString("CRN_SCH_CNT");
						String ydWbId 		= jsCrnsch1.getRecord(0).getFieldString("YD_WBOOK_ID");
						if("N".equals(ydCrnSchCnt)&&(!"".endsWith(ydWbId))) {
							
							ydL3Msg = "크레인스케줄 호출";
							JDTORecord jrYdMsg1 = JDTORecordFactory.getInstance().create();
							jrYdMsg1.setField("YD_WBOOK_ID"  , ydWbId  ); //야드작업예약ID
							jrYdMsg1.setField("YD_SCH_CD"    , ydSchCd ); //야드스케쥴코드
							jrYdMsg1.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
							jrYdMsg1.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
							jrYdMsg1.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
							jrYdMsg1.setField("MODIFIER"     , modifier); //수정자
								
							// 크레인스케줄 기동
							jrRtn = commUtils.addSndData(jrRtn,ymComm.getCrnSchMsg(jrYdMsg1));						
						}
					}
				}					
			}
			//resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치
			//resMsg.setField("YD_DN_WR_LOC", ydDnWrLoc); //야드권하실적위치

			////크레인작업실적응답 전송
			//if (resYn) {
			//	resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
			//	resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
			//	jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
			//}
			
			
			/**********************************************************
			* 11. 크레인작업지시요구 전문 호출(AxYDL007)
			**********************************************************/
			jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			jrYdMsg.setField("JMS_TC_CD"		, "A8YML007"); //JMSTC코드 
			jrYdMsg.setField("YD_EQP_ID"       	, ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT"	, "4"       ); //야드작업진행상태(권하완료)
			jrYdMsg.setField("YD_SCH_CD"       	, ydSchCd   ); //야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID"   	, ydCrnSchId); //야드크레인스케쥴ID
			jrYdMsg.setField("MODIFIER"        	, modifier  ); //수정자
			
			//크레인작업지시 요구을 추가
			jrRtn = commUtils.addSndData(jrRtn, this.rcvA8YML007(jrYdMsg));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}								

					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YmCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { bSlabComm.getYMA8L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 크레인 비상조업실적(A8YML010)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML010(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인 비상조업실적[BSlabL2RcvSeEJB.rcvA8YML010] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "크레인 비상조업실적(A8YML010) 수신 ", rcvMsg);

			//수신 항목 값
			String sYD_EQP_ID		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //야드설비ID
			String sYD_UP_LOC		= commUtils.trim(rcvMsg.getFieldString("YD_UP_LOC")); //권상위치
			String sYD_DN_LOC		= commUtils.trim(rcvMsg.getFieldString("YD_DN_LOC")); //권하위치
			String sYD_EQP_WRK_SH	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH")); //야드설비작업매수
			String sSLAB_NO1		= commUtils.trim(rcvMsg.getFieldString("SLAB_NO1")); //SLAB_NO1
			String sSLAB_NO2		= commUtils.trim(rcvMsg.getFieldString("SLAB_NO2")); //SLAB_NO1
			String sYD_UP_CMPL_DT   = commUtils.trim(rcvMsg.getFieldString("YD_UP_CMPL_DT")); //야드권상완료일시
			String sYD_DN_CMPL_DT   = commUtils.trim(rcvMsg.getFieldString("YD_DN_CMPL_DT")); //야드권하완료일시
			
			String msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"A8YML010"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			
			//수신항목 Check
			if(!"2".equals(sYD_EQP_ID.substring(0,1)) || !"CR".equals(sYD_EQP_ID.substring(2,4))) {
				throw new Exception("야드설비ID(YD_EQP_ID)가 야드구분이 '2'이 아니거나 설비 구분이 'CR'이 아닙니다!! [" + sYD_EQP_ID + "]");
			}
			if(sYD_UP_LOC.length() != 10) {
				throw new Exception("권상위치(YD_UP_LOC)가 10자리가 아닙니다!! [" + sYD_UP_LOC + "]");
			}
			if(sYD_DN_LOC.length() != 10) {
				throw new Exception("권하위치(YD_DN_LOC)가 10자리가 아닙니다!! [" + sYD_DN_LOC + "]");
			}
			if(!"2".equals(sYD_UP_LOC.substring(0,1))) {
				throw new Exception("권상위치(YD_UP_LOC)가 야드구분이 '2'이  아닙니다!! [" + sYD_UP_LOC + "]");
			}
			if(!"2".equals(sYD_DN_LOC.substring(0,1))) {
				throw new Exception("권상위치(YD_DN_LOC)가 야드구분이 '2'이  아닙니다!! [" + sYD_DN_LOC + "]");
			}
			if(!"01".equals(sYD_EQP_WRK_SH) && !"02".equals(sYD_EQP_WRK_SH)) {
				throw new Exception("야드설비작업매수 가 '01','02' 가 아닌 값이 들어 왔습니다!! [" + sYD_EQP_WRK_SH + "]");
			}
			if("".equals(sSLAB_NO1)) {
				throw new Exception("SLAB_NO1 에 빈 값이 들어왔습니다!! [" + sSLAB_NO1 + "]");
			}
			if("02".equals(sYD_EQP_WRK_SH)) {
				if("".equals(sSLAB_NO2)) {
					throw new Exception("SLAB_NO2 에 빈 값이 들어왔습니다!! [" +sSLAB_NO2 + "]");
				}
			}
			if(sYD_UP_CMPL_DT.length() != 14) {
				throw new Exception("야드권상완료일시(YD_UP_CMPL_DT)가 14자리가 아닙니다!! [" + sYD_UP_CMPL_DT + "]");
			}
			if(sYD_DN_CMPL_DT.length() != 14) {
				throw new Exception("야드권하완료일시(YD_DN_CMPL_DT)가 14자리가 아닙니다!! [" + sYD_DN_CMPL_DT + "]");
			}

			JDTORecordSet jsChk = null;
			String sSTOCK_ID;
			
			JDTORecord jrParam	= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			int iYD_EQP_WRK_SH = Integer.parseInt(sYD_EQP_WRK_SH);
			int iUP_STACK_LAYER_GP = Integer.parseInt(sYD_UP_LOC.substring(8,10));
			int iDN_STACK_LAYER_GP = Integer.parseInt(sYD_DN_LOC.substring(8,10));
			
			//Crane스케줄ID생성 
			String sCrnSchID = commDao.getSeqId(logId, methodNm, "CrnSch"); //비상조업실적용 1개의 Crane스케줄ID 사용
			
			for(int ii = 0; ii < iYD_EQP_WRK_SH; ii++) {
				
				/**********************************************************
				* 1. SLAB_NO 로 적치단 Clear 하기
				**********************************************************/
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.clearStackLayer 
				UPDATE TB_YM_STACKLAYER
				   SET STOCK_ID = ''
				      ,STACK_LAYER_STAT = 'E'
				      ,MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				WHERE  STOCK_ID = :V_STOCK_ID
				AND    STACK_COL_GP LIKE :V_YD_GP || '%' */
				jrParam.setField("MODIFIER"	, modifier		); //수정자
				jrParam.setField("STOCK_ID"	, commUtils.trim(rcvMsg.getFieldString("SLAB_NO"+(ii+1))));
				jrParam.setField("YD_GP"	, "2");	
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.clearStackLayer", logId, methodNm, "SLAB_NO 로 적치단 Clear");
				
				
				/**********************************************************
				* 2. 권하위치에  SLAB_NO 를 적치중으로 설정한다.
				**********************************************************/
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.setStackLayer
				UPDATE TB_YM_STACKLAYER
				   SET STOCK_ID = :V_STOCK_ID
				      ,STACK_LAYER_STAT = 'C'
				      ,MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				WHERE  STACK_COL_GP = :V_STACK_COL_GP
				  AND  STACK_BED_GP = :V_STACK_BED_GP
				  AND  STACK_LAYER_GP = :V_STACK_LAYER_GP  */
				jrParam.setField("MODIFIER"			, modifier		); //수정자
				jrParam.setField("STOCK_ID"			, commUtils.trim(rcvMsg.getFieldString("SLAB_NO"+(ii+1))));
				jrParam.setField("STACK_COL_GP"		, sYD_DN_LOC.substring(0,6));	
				jrParam.setField("STACK_BED_GP"		, sYD_DN_LOC.substring(6,8));	
				if(iDN_STACK_LAYER_GP < 10) {
					jrParam.setField("STACK_LAYER_GP"	, "0"+iDN_STACK_LAYER_GP);	
				} else {
					jrParam.setField("STACK_LAYER_GP"	, ""+iDN_STACK_LAYER_GP);	
				}
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.setStackLayer", logId, methodNm, "권하위치에  SLAB_NO 를 적치중으로 설정 ");

				
				/**********************************************************
				* 3. 주편공통,SLAB공통 수정 (별도 Transaction 으로 처리)
				**********************************************************/
				//jrParam.setField("MODIFIER"	, modifier	 ); //수정자
				//jrParam.setField("STOCK_ID"	, commUtils.trim(rcvMsg.getFieldString("SLAB_NO"+(ii+1))));
				//if(iDN_STACK_LAYER_GP < 10) {
				//	jrParam.setField("YD_LOC"   , sYD_DN_LOC.substring(0,8) + "0"+iDN_STACK_LAYER_GP ); //야드권하실적위치
				//} else {
				//	jrParam.setField("YD_LOC"   , sYD_DN_LOC.substring(0,8) + ""+iDN_STACK_LAYER_GP ); //야드권하실적위치
				//}
				//
				//EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				//ejbConn1.trx("UpdSlabComLoc", new Class[] { JDTORecord.class }, new Object[] { jrParam });

				
				sSTOCK_ID = commUtils.trim(rcvMsg.getFieldString("SLAB_NO"+(ii+1)));
				
				//주편공통 진행 상태가 진행중(2)인 경우 주편공통을 update 
				jrParam.setField("RECORD_PROG_STAT"	, "2"); 
				jrParam.setField("MSLAB_NO"			, sSTOCK_ID); 
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMSlabByRecordProgStat 
				SELECT MSLAB_NO
				  FROM TB_PT_MSLABCOMM
				 WHERE RECORD_PROG_STAT = :V_RECORD_PROG_STAT --진행중:2
				   AND MSLAB_NO= :V_MSLAB_NO */
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMSlabByRecordProgStat", logId, methodNm, "주편공통에 레코드상태가 진행중(2)인지 확인 "); 
				if(jsChk.size() > 0) {
					//주편공통의 LOC 정보를 Transaction 분리 하여 변경처리 한다.
					jrParam.setField("STOCK_ID"	, sSTOCK_ID);
					if(iDN_STACK_LAYER_GP < 10) {
						jrParam.setField("YD_LOC"   , sYD_DN_LOC.substring(0,8) + "0"+iDN_STACK_LAYER_GP ); //야드권하실적위치
					} else {
						jrParam.setField("YD_LOC"   , sYD_DN_LOC.substring(0,8) + ""+iDN_STACK_LAYER_GP ); //야드권하실적위치
					}
					bSlabComm.updMSlabCommLocInfo(jrParam);
				}
				
				//SLAB공통이 존재 하는 경우 SLAB공통 update 
				jrParam.setField("SLAB_NO"	, sSTOCK_ID); 
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabNoBySlabNo 
				SELECT SLAB_NO
				  FROM TB_PT_SLABCOMM
				 WHERE SLAB_NO= :V_SLAB_NO */
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabNoBySlabNo", logId, methodNm, "SLAB공통에 존재하는지 확인 "); 
				if(jsChk.size() > 0) {
					//SLAB공통의 LOC 정보를 Transaction 분리 하여 변경처리 한다.
					jrParam.setField("STOCK_ID"	, sSTOCK_ID);
					if(iDN_STACK_LAYER_GP < 10) {
						jrParam.setField("YD_LOC"   , sYD_DN_LOC.substring(0,8) + "0"+iDN_STACK_LAYER_GP ); //야드권하실적위치
					} else {
						jrParam.setField("YD_LOC"   , sYD_DN_LOC.substring(0,8) + ""+iDN_STACK_LAYER_GP ); //야드권하실적위치
					}
					bSlabComm.updSlabCommLocInfo(jrParam);
				}
				
				/**********************************************************
				* 4. 저장품 CARUNLOAD_PUT_LOC 수정
				**********************************************************/
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYDL009Stock 
				UPDATE TB_YM_STOCK
				   SET MODIFIER    = :V_MODIFIER
				     , MOD_DDTT    = SYSDATE
				     , CARUNLOAD_PUT_LOC = :V_YD_LOC
				 WHERE STOCK_ID    = :V_STOCK_ID 	*/
				jrParam.setField("MODIFIER"	, modifier	 ); //수정자
				if(iDN_STACK_LAYER_GP < 10) {
					jrParam.setField("YD_LOC"   , sYD_DN_LOC.substring(0,8) + "0"+iDN_STACK_LAYER_GP ); //야드권하실적위치
				} else {
					jrParam.setField("YD_LOC"   , sYD_DN_LOC.substring(0,8) + ""+iDN_STACK_LAYER_GP ); //야드권하실적위치
				}
				jrParam.setField("STOCK_ID"	, commUtils.trim(rcvMsg.getFieldString("SLAB_NO"+(ii+1))));
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYDL009Stock", logId, methodNm, "저장품 CARUNLOAD_PUT_LOC 수정 ");
				
				
				/**********************************************************
				* 5. 작업이력 등록
				**********************************************************/
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkHist_AxYML010
				INSERT INTO TB_YM_WRSLT (
				     CRANE_WRSLT_ID
				    ,SCH_ID
				    ,STOCK_ID
				    ,EQUIP_GP
				    ,CRANE_WRSLT_UP_LOC
				    ,CRANE_WRSLT_UP_DDTT
				    ,CRANE_WRSLT_PUT_LOC
				    ,CRANE_WRSLT_PUT_DDTT
				    ,REGISTER
				    ,REG_DDTT
				    ,MODIFIER
				    ,MOD_DDTT
				    ,DEL_YN
				    ,YD_GP
				) VALUES (
				     TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||YM_WRSLT_SEQ.NEXTVAL
				    ,:V_SCH_ID
				    ,:V_STOCK_ID
				    ,:V_EQUIP_GP
				    ,:V_CRANE_WRSLT_UP_LOC
				    ,:V_CRANE_WRSLT_UP_DDTT
				    ,:V_CRANE_WRSLT_PUT_LOC
				    ,:V_CRANE_WRSLT_PUT_DDTT
				    ,:V_MODIFIER
				    ,SYSDATE
				    ,:V_MODIFIER
				    ,SYSDATE
				    ,'N'
				    ,:V_YD_GP
				)  */
				jrParam.setField("SCH_ID"				, sCrnSchID);	
				jrParam.setField("STOCK_ID"				, commUtils.trim(rcvMsg.getFieldString("SLAB_NO"+(ii+1))));
				jrParam.setField("EQUIP_GP"				, sYD_EQP_ID);	
				if(iUP_STACK_LAYER_GP < 10) {
					jrParam.setField("CRANE_WRSLT_UP_LOC"   , sYD_UP_LOC.substring(0,8) + "0"+iUP_STACK_LAYER_GP ); //야드권하실적위치
				} else {
					jrParam.setField("CRANE_WRSLT_UP_LOC"   , sYD_UP_LOC.substring(0,8) + ""+iUP_STACK_LAYER_GP ); //야드권하실적위치
				}
				jrParam.setField("CRANE_WRSLT_UP_DDTT"	, sYD_UP_CMPL_DT);
				if(iDN_STACK_LAYER_GP < 10) {
					jrParam.setField("CRANE_WRSLT_PUT_LOC"   , sYD_DN_LOC.substring(0,8) + "0"+iDN_STACK_LAYER_GP ); //야드권하실적위치
				} else {
					jrParam.setField("CRANE_WRSLT_PUT_LOC"   , sYD_DN_LOC.substring(0,8) + ""+iDN_STACK_LAYER_GP ); //야드권하실적위치
				}
				jrParam.setField("CRANE_WRSLT_PUT_DDTT"	, sYD_DN_CMPL_DT);	
				jrParam.setField("MODIFIER"				, modifier); //수정자
				jrParam.setField("YD_GP"				, "2");	
				commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkHist_AxYML010", logId, methodNm, "비상조업용 작업이력 등록 ");

				//iUP_STACK_LAYER_GP++;
				//iDN_STACK_LAYER_GP++;
				
				//2017.11.03 테스트 후..
				iUP_STACK_LAYER_GP--;
				iDN_STACK_LAYER_GP--;
				
				/**********************************************************
				* 6. 저장품제원정보 (YMA8L002) 송신
				**********************************************************/
				jrParam.setField("TC_CD"          , "YMA7L002");
				jrParam.setField("MSG_GP"         , "I");
				jrParam.setField("YD_INFO_SYNC_CD", "5");
				jrParam.setField("STOCK_ID"       , sSTOCK_ID);
				
				//전송 Data 생성
				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L002", jrParam));
				
				
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 크레인 비상조업실적(A8YML010)
	
	/**
	 *      [A] 오퍼레이션명 : 대차이동실적(A8YML011)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML011(JDTORecord rcvMsg) throws DAOException {






		String methodNm = "대차이동실적[BSlabL2RcvSeEJB.rcvA8YML011] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )); //야드설비ID
			String ydTcMoveGp   = commUtils.trim(rcvMsg.getFieldString("YD_MOVE_GP"       )); //야드대차이동구분
			String TcCurrBay    = commUtils.trim(rcvMsg.getFieldString("YD_TCAR_CURR_BAY")); //현재동
			String TcAimBay     = commUtils.trim(rcvMsg.getFieldString("YD_TCAR_AIM_BAY" )); //목적동
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)

			String ydTcarSchId  = ""; // 대차 스케쥴 코드 
			
			String ydCarloadStopLoc   = "";
			String ydCarunloadStopLoc = "";
			String RollBackTag        = ""; 
			String NextTc             = "" ;                  //다음대차
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			} else if ("".equals(ydTcMoveGp)) {
				throw new Exception("대차이동구분(YD_MOVE_GP) 없음");
			} else if ("".equals(TcCurrBay)) {
				throw new Exception("현재동(YD_TCAR_CURR_BAY) 없음");
			}

			if (!"S".equals(ydTcMoveGp) && !"E".equals(ydTcMoveGp)) {
				commUtils.printLog(logId, "대차이동구분[" + ydTcMoveGp + "]이 'S' 또는 'E'가 아니므로 종료", "SL");
				commUtils.printLog(logId, methodNm, "S-");
				return null;
			}
			
			String ydStkColGp = TcCurrBay; // 
			
			/**********************************************************
			* 2. 설비 야드현재동구분, 대차스케줄 야드차량진행상태 수정
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_EQP_ID"      , ydEqpId     ); //야드설비ID
			jrParam.setField("YD_MOVE_GP"     , ydTcMoveGp  ); //야드대차이동구분
			jrParam.setField("STACK_COL_GP"   , ydStkColGp  ); //야드적치열구분(현재동)
			jrParam.setField("MODIFIER"       , modifier    ); //수정자
			//야드현재동구분
			if ("S".equals(ydTcMoveGp)) {
				jrParam.setField("YD_CURR_BAY_GP", "");
			} else {
				jrParam.setField("YD_CURR_BAY_GP", TcCurrBay);
			}

			/**********************************************************
			* 2.1 설비 야드현재동 수정
			**********************************************************/
			//설비 Table 야드현재동구분 수정
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdEqpCurrBay 
			UPDATE TB_YM_EQUIP
			   SET MODIFIER       = :V_MODIFIER
			      ,MOD_DDTT       = SYSDATE
			      ,CURR_STOP_LOC  = :V_STACK_COL_GP
			 WHERE EQUIP_GP       = :V_YD_EQP_ID
			*/ 
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdEqpCurrBay", logId, methodNm, "야드현재위치수정");
			

			/**********************************************************
			* 2.2 대차스케줄 야드차량진행상태 수정
			**********************************************************/
			JDTORecord jrChk = null;
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchByEqpid
			SELECT YD_TCAR_SCH_ID
			     , CASE WHEN :V_YD_MOVE_GP = 'S' THEN                   --야드대차이동구분(S:출발,E:도착)
			            CASE WHEN YD_EQP_WRK_STAT = 'U' THEN '1'        --공대차(상차)출발
			            ELSE 'A' END                                    --영대차(하차)출발
			       ELSE CASE WHEN YD_EQP_WRK_STAT = 'L' THEN 'B'        --영대차(하차)도착
			                 WHEN YD_CARLD_WRK_BOOK_ID IS NULL THEN '0' --공대차도착(상차대기)
			            ELSE '2' END                                    --공대차(상차)도착
			       END AS YD_CAR_PROG_STAT                              --야드차량진행상태
                , YD_CAR_PROG_STAT AS  BF_CAR_PROG_STAT                  --변경전 차량 진행상태			       
			  FROM TB_YM_TCARSCH
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND DEL_YN    = 'N'
			*/	   
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchByEqpid", logId, methodNm, "대차스케줄상태 조회");

		

			
			/**********************************************************
			* 3. 대차스케줄 조회
			**********************************************************/
			if (jsChk.size() > 0) {
				
				
				/**********************************************************
				* 3.0 대차스케줄 코드  세팅
				**********************************************************/
			 
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchForEqpid  

					SELECT NVL(C.YD_SCH_CD        ,C.YD_GP||C.YD_BAY_GP||SUBSTR(A.CARLD_SCH_CD,3) )      AS CARLD_SCH_CD        --상차지 스케줄 코드      
					     , NVL(B.YD_CARLD_STOP_LOC,A.CARLOAD_STOP_LOC)   AS CARLOAD_STOP_LOC    --상차지 정지위치 
					     , NVL(D.YD_SCH_CD        ,C.YD_GP||C.YD_AIM_BAY_GP||SUBSTR(A.CARUD_SCH_CD,3) )      AS CARUD_SCH_CD        --하차지 스케줄 코드      
					     , NVL(B.YD_CARUD_STOP_LOC,A.CARUNLOAD_STOP_LOC) AS CARUNLOAD_STOP_LOC   --하차지 정지위치 
					     , A.PALLET_NO             --선작업지시 1:실행 2:대기
					     , B.YD_CARLD_STOP_LOC     --대차기준 상차정지위치 
					     , B.YD_CARUD_STOP_LOC     --대차기준 하차정지위치
					     , C.YD_SCH_CD             --예약기준 상차작업예약스케줄
					     , D.YD_SCH_CD             --예약기준 하차작업예약스케줄
						 , A.CARUD_SCH_CD          AS EQ_CARUD_SCH_CD     --설비기준 하차지 스케줄코드
					     , A.CURR_STOP_LOC
					     , A.CARLOAD_STOP_LOC
					     , A.CARUNLOAD_STOP_LOC
					 FROM TB_YM_EQUIP A
					    , TB_YM_TCARSCH B
					    , TB_YM_WRKBOOK C
					    , TB_YM_WRKBOOK D
					 WHERE A.EQUIP_GP = :V_YD_EQP_ID
					   AND A.EQUIP_GP =  B.YD_EQP_ID
					   AND B.YD_CARLD_WRK_BOOK_ID = C.YD_WBOOK_ID(+)
					   AND B.YD_CARUD_WRK_BOOK_ID = D.YD_WBOOK_ID(+)
					   AND A.DEL_YN    = 'N' 
					   AND B.DEL_YN    = 'N'  
					   AND 'N'         =  C.DEL_YN(+)
					   AND 'N'         =  D.DEL_YN(+)
				*/	   
				JDTORecordSet jsChk7 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchForEqpid", logId, methodNm, "대차스케줄코드검색");	
				
				JDTORecord jrTcar7 = jsChk7.getRecord(0);
				String CarldSchCd     = commUtils.trim(jrTcar7.getFieldString("CARLD_SCH_CD"      )); //상차지 스케줄코드 
				String CarudSchCd     = commUtils.trim(jrTcar7.getFieldString("CARUD_SCH_CD"      )); //하차지 스케줄코드 
				String PreWrkWo       = commUtils.trim(jrTcar7.getFieldString("PALLET_NO"         )); //선작업지시 1:실행 2:대기 3:하차지 선작업실행
				jrParam.setField("CARLD_SCH_CD"      , CarldSchCd     ); //상차지 스케줄코드 
				jrParam.setField("CARUD_SCH_CD"      , CarudSchCd     ); //하차지 스케줄코드 
				String EqCarudSchCd            = commUtils.trim(jrTcar7.getFieldString("EQ_CARUD_SCH_CD" ));    //하차지 스케줄코드 장비
				String SetEqCarudSchCd         = TcCurrBay.substring(0, 2) + EqCarudSchCd.substring(2);         //영대차 도착시  기준정보 변경될때 사용 예약테이블 스케줄 변경용
				String SetEqCurrStopLoc        = commUtils.trim(jrTcar7.getFieldString("CURR_STOP_LOC" ));      //장비현재대차위치
				String SetEqCarloadStopLoc     = commUtils.trim(jrTcar7.getFieldString("CARLOAD_STOP_LOC" ));   //장비상차지위치
				String SetEqCarunloadStopLoc   = commUtils.trim(jrTcar7.getFieldString("CARUNLOAD_STOP_LOC" )); //장비하차지위치
				
				/**********************************************************
				* 3.1 대차스케줄이 있는 경우 
				**********************************************************/
				//대차스케쥴 정보 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydTcarSchId              =  commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"));  ////야드대차스케쥴ID
				String aFCarProgStat     =  commUtils.trim(jrChk.getFieldString("YD_CAR_PROG_STAT")) ;
				String bFCarProgStat     =  commUtils.trim(jrChk.getFieldString("BF_CAR_PROG_STAT")) ;
				
				
				jrParam.setField("YD_TCAR_SCH_ID"   , ydTcarSchId);   //대차SCH
				jrParam.setField("YD_CAR_PROG_STAT" , commUtils.trim(jrChk.getFieldString("YD_CAR_PROG_STAT"))); //야드차량진행상태
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarProgStat 
				UPDATE TB_YM_TCARSCH
				   SET MODIFIER          = :V_MODIFIER
				     , MOD_DDTT          = SYSDATE
				     , YD_CAR_PROG_STAT  = :V_YD_CAR_PROG_STAT --야드차량진행상태
				     , YD_CARLD_LEV_LOC  = DECODE(:V_YD_CAR_PROG_STAT,'1',:V_STACK_COL_GP ,YD_CARLD_LEV_LOC ) --야드상차출발위치
				     , YD_CARLD_LEV_DT   = DECODE(:V_YD_CAR_PROG_STAT,'1',SYSDATE         ,YD_CARLD_LEV_DT  ) --야드상차출발일시
				     , YD_CARLD_STOP_LOC = DECODE(:V_YD_CAR_PROG_STAT,'2',:V_STACK_COL_GP ,'0',:V_STACK_COL_GP,YD_CARLD_STOP_LOC) --야드상차정지위치
				     , YD_CARLD_ARR_DT   = DECODE(:V_YD_CAR_PROG_STAT,'2',SYSDATE         ,'0',SYSDATE        ,YD_CARLD_ARR_DT  ) --야드상차도착일시
				     , YD_CARUD_LEV_DT   = DECODE(:V_YD_CAR_PROG_STAT,'A',SYSDATE         ,YD_CARUD_LEV_DT  ) --야드하차출발일시
				     , YD_CARUD_STOP_LOC = DECODE(:V_YD_CAR_PROG_STAT,'B',:V_STACK_COL_GP ,YD_CARUD_STOP_LOC) --야드하차정지위치
				     , YD_CARUD_ARR_DT   = DECODE(:V_YD_CAR_PROG_STAT,'B',SYSDATE         ,YD_CARUD_ARR_DT  ) --야드하차도착일시
				 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
				 */	 
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarProgStat", logId, methodNm, "대차스케줄상태 수정");

				/**********************************************************
				* 3. 대차스케줄 조회
				**********************************************************/
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSch2 
					SELECT TS.YD_TCAR_SCH_ID
					      ,TS.YD_CAR_PROG_STAT
					      ,TS.YD_CARLD_WRK_BOOK_ID
					      ,TS.YD_CARUD_WRK_BOOK_ID
					      ,TS.YD_CARLD_STOP_LOC
					      ,(SELECT SB.STACK_BED_ACTIVE_STAT
					          FROM TB_YM_STACKER SB
					         WHERE SB.STACK_COL_GP = TS.YD_CARLD_STOP_LOC
					           AND SB.STACK_BED_GP = '01') AS YD_STK_BED_ACT_STAT_LD
					      ,TS.YD_CARUD_STOP_LOC
					      ,(SELECT SB.STACK_BED_ACTIVE_STAT
					          FROM TB_YM_STACKER SB
					         WHERE SB.STACK_COL_GP = TS.YD_CARUD_STOP_LOC
					           AND SB.STACK_BED_GP = '01') AS YD_STK_BED_ACT_STAT_UD
					      ,CASE WHEN TS.CRN_SCH_YN = 'Y' OR TS.YD_WBOOK_ID IS NULL THEN TS.CRN_SCH_YN
					      	    ELSE (SELECT DECODE(COUNT(*),0,'Y','N') --도착 시 크레인스케줄이 없으면 기동
					      	    	    FROM TB_YM_CRNSCH CS
					      	    	   WHERE CS.YD_WBOOK_ID = TS.YD_WBOOK_ID
					      	    	     AND CS.DEL_YN = 'N')
					       END CRN_SCH_YN
					      ,TS.YD_CARLD_WRK_CRN
					      ,TS.YD_CARUD_WRK_CRN         
					  FROM (SELECT TS.YD_TCAR_SCH_ID
					              ,TS.YD_CAR_PROG_STAT
					              ,TS.YD_CARLD_WRK_BOOK_ID
					              ,TS.YD_CARUD_WRK_BOOK_ID
					              ,NVL(TS.YD_CARLD_STOP_LOC , EQ.CARLOAD_STOP_LOC) AS YD_CARLD_STOP_LOC
					              ,NVL(TS.YD_CARUD_STOP_LOC,  EQ.CARUNLOAD_STOP_LOC) AS YD_CARUD_STOP_LOC
					              ,DECODE(TS.YD_CAR_PROG_STAT,'2','Y','B','Y','N') AS CRN_SCH_YN 
					              ,DECODE(TS.YD_CAR_PROG_STAT,'2',TS.YD_CARLD_WRK_BOOK_ID
					                                         ,'B',TS.YD_CARUD_WRK_BOOK_ID) AS YD_WBOOK_ID
					              ,TS.YD_CARLD_WRK_CRN
					              ,TS.YD_CARUD_WRK_CRN
					              ,TS.YD_EQP_ID
					          FROM TB_YM_TCARSCH TS
					              ,TB_YM_EQUIP EQ 
					         WHERE TS.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
					           AND TS.DEL_YN    = 'N'
					           AND TS.YD_EQP_ID =EQ.EQUIP_GP) TS
				 */          
				JDTORecordSet jsTcar = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSch2", logId, methodNm, "대차스케줄조회");


//				if (jsTcar == null || jsTcar.size() == 0) {
//					throw new Exception("대차스케줄[" + ydEqpId + "] 없음");
//				}

				JDTORecord jrTcar = jsTcar.getRecord(0);
				String ydCarProgStat     = commUtils.trim(jrTcar.getFieldString("YD_CAR_PROG_STAT"      )); //야드차량진행상태
				String ydCarldWrkbookId  = commUtils.trim(jrTcar.getFieldString("YD_CARLD_WRK_BOOK_ID"  )); //야드상차작업예약ID
				String ydCarudWrkbookId  = commUtils.trim(jrTcar.getFieldString("YD_CARUD_WRK_BOOK_ID"  )); //야드하차작업예약ID
				String ydCarldStopLoc    = commUtils.trim(jrTcar.getFieldString("YD_CARLD_STOP_LOC"     )); //야드상차정지위치
				String ydCarudStopLoc    = commUtils.trim(jrTcar.getFieldString("YD_CARUD_STOP_LOC"     )); //야드하차정지위치
				String ydStkBedActStatLd = commUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_LD")); //야드적치Bed활성상태(상차정지위치)
				String ydStkBedActStatUd = commUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_UD")); //야드적치Bed활성상태(하차정지위치)
				String crnSchYn          = commUtils.trim(jrTcar.getFieldString("CRN_SCH_YN"            )); //크레인스케줄기동여부
				String ydCarldWrkCrn     = commUtils.trim(jrTcar.getFieldString("YD_CARLD_WRK_CRN"      )); //상차작업크레인
				String ydCarudWrkCrn     = commUtils.trim(jrTcar.getFieldString("YD_CARUD_WRK_CRN"      )); //하차작업크레인
				String ydWbookId         = ""; //작업예약ID
				String ydSchReqGp        = ""; //야드스케쥴요청구분

				commUtils.printLog(logId, "대차[" + ydEqpId + "] 스케줄 >> 대차스케쥴ID:" + ydTcarSchId + ", 차량진행:" + ydCarProgStat + ", 크레인스케줄:" + crnSchYn
						                + ", 상차작업:" + ydCarldWrkbookId + "-" + ydCarldStopLoc + ", 하차작업:" + ydCarudWrkbookId + "-" + ydCarudStopLoc, "SL");
				
				/**********************************************************
				* 4. 적치Bed, 적치단 Table 상태 수정
				**********************************************************/
				//jrParam.setField("STACK_BED_GP" , "01"        ); //야드적치Bed번호
				jrParam.setField("YD_TCAR_SCH_ID", ydTcarSchId); //야드대차스케쥴ID
				
				if ("A".equals(ydCarProgStat)) {
					ydSchReqGp = "2"; //영대차출발 : 하차출발(A)

					/***
					 *  크레인 하차 예약 등록 여부 CHECK 하여 없으면 생성
					 */
					
/*					 com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getA8YML011WbTCar 
				    SELECT YD_TCAR_SCH_ID 
					     , STOCK_ID
					     , YD_SCH_CD
					     , (SELECT YD_WRK_CRN_PRIOR FROM TB_YM_SCHEDULERULE WHERE YD_SCH_CD = A.YD_SCH_CD) AS YD_WRK_CRN_PRIOR
					  FROM
					        (  
					        SELECT A.YD_TCAR_SCH_ID
					             , B.STOCK_ID 
					             , :V_CARUD_SCH_CD AS YD_SCH_CD
					             --하차 스케쥴 유무검색
					             , (SELECT COUNT(*) 
					                  FROM TB_YM_WRKBOOK WB
					                     , TB_YM_WRKBOOKMTL WM
					                 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID  
					                   AND WB.DEL_YN = 'N'
					                   AND WM.DEL_YN = 'N'
					                   AND WB.YD_SCH_CD = :V_CARUD_SCH_CD
					                   AND WM.STOCK_ID  = B.STOCK_ID ) AS WB_CNT  
					          FROM TB_YM_TCARSCH     A
					             , TB_YM_TCARFTMVMTL B
					         WHERE A.YD_TCAR_SCH_ID = B.YD_TCAR_SCH_ID
					           AND A.DEL_YN = 'N'
					           AND B.DEL_YN = 'N'
					           AND A.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
					          
					        ) A 
					 WHERE WB_CNT = 0  
						   
					JDTORecordSet jsDnTc = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getA8YML011WbTCar", logId, methodNm, "권하대상재!!!");
					String ydNewWbookId = ""; 
					if (jsDnTc.size() > 0 ){
						
						JDTORecord jrParamTc = JDTORecordFactory.getInstance().create();
						JDTORecord jrDnTc 	 = JDTORecordFactory.getInstance().create();
						
						String ydCarudWrkBookId = commDao.getSeqId(logId, methodNm, "WrkBook");
						
						for(int nIdx=0; nIdx < jsDnTc.size(); nIdx++) {
							
							jrParamTc = JDTORecordFactory.getInstance().create();
							jrDnTc    = JDTORecordFactory.getInstance().create();
							//대차스케줄 처리 (영대차출발지시)
							//야드하차작업예약ID 생성
							
							//작업예약 등록
							jrDnTc = jsDnTc.getRecord(nIdx);
						
							
							jrParamTc.setField("YD_CARUD_WRK_BOOK_ID", ydCarudWrkBookId); //야드하차작업예약ID
							jrParamTc.setField("MODIFIER"            , modifier       	); 
							jrParamTc.setField("YD_WBOOK_ID"         , ydCarudWrkBookId ); 
							jrParamTc.setField("YD_SCH_CD"           , commUtils.trim(jrDnTc.getFieldString("YD_SCH_CD"))); //스케쥴 코드
							jrParamTc.setField("YD_WRK_CRN_PRIOR"    , commUtils.trim(jrDnTc.getFieldString("YD_WRK_CRN_PRIOR"))); //대차설비id
							jrParamTc.setField("YD_EQP_ID"           , ydEqpId       	); //대차설비id
							jrParamTc.setField("STOCK_ID" 			 , commUtils.trim(jrDnTc.getFieldString("STOCK_ID"))); 
							
							if(nIdx == 0 ){
								//ydNewWbookId = ydCarudWrkBookId;
								 com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA7YML011WbTCarIns 
								INSERT INTO TB_YM_WRKBOOK WB
								       (WB.YD_WBOOK_ID         , WB.REGISTER              , WB.REG_DDTT              ,
								        WB.DEL_YN              , WB.YD_GP                 , WB.YD_BAY_GP             , WB.YD_SCH_CD   , WB.YD_SCH_PRIOR ,
								        WB.YD_SCH_PROG_STAT    , WB.YD_SCH_ST_GP          , WB.YD_SCH_REQ_GP         , WB.YD_AIM_YD_GP, WB.YD_AIM_BAY_GP,
								        WB.YD_TO_LOC_DCSN_MTD  , WB.YD_TO_LOC_GUIDE       , WB.YD_WRK_PLAN_TCAR)
								VALUES (:V_YD_CARUD_WRK_BOOK_ID, :V_MODIFIER              ,  SYSDATE                 ,
								        'N'                    , SUBSTR(:V_YD_SCH_CD,1,1) , SUBSTR(:V_YD_SCH_CD,2,1) , :V_YD_SCH_CD   , :V_YD_WRK_CRN_PRIOR ,
								        'W'                    , 'O'                      , '1'                      ,SUBSTR(:V_YD_SCH_CD,1,1) , SUBSTR(:V_YD_SCH_CD,2,1)    ,
								        NULL                   , NULL                     , :V_YD_EQP_ID)
								        
								commDao.update(jrParamTc, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA7YML011WbTCarIns", logId, methodNm, "작업예약 등록");								
							}
	
							//작업예약재료 등록
							 com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA7YML009WbMtlTCarIns 
							INSERT INTO TB_YM_WRKBOOKMTL WM
							       (WM.YD_WBOOK_ID          , WM.STOCK_ID       , WM.REGISTER       , WM.REG_DDTT    ,
							        WM.MODIFIER             , WM.MOD_DDTT       , WM.DEL_YN         , WM.STACK_COL_GP,
							        WM.STACK_BED_GP         , WM.STACK_LAYER_GP , WM.YD_UP_COLL_SEQ)
							VALUES (:V_YD_CARUD_WRK_BOOK_ID , :V_STOCK_ID       , :V_MODIFIER       , SYSDATE        ,
							        :V_MODIFIER             , SYSDATE           , 'N'               , :V_STACK_COL_GP,
							        :V_STACK_BED_GP         , :V_STACK_LAYER_GP , :V_YD_UP_COLL_SEQ);        
							        
							jrParamTc.setField("STACK_COL_GP" 	, ydEqpId); 
							jrParamTc.setField("STACK_BED_GP" 	, "01"); 
		                	jrParamTc.setField("STACK_LAYER_GP" , commUtils.format(nIdx+1, 2)); 
							jrParamTc.setField("YD_UP_COLL_SEQ" , "" + (nIdx+1)); 
							commDao.update(jrParamTc, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA7YML009WbMtlTCarIns", logId, methodNm, "작업예약재료 등록");							        
						}
						 com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarProgStatCarudWrkbook 
						UPDATE TB_YM_TCARSCH
						   SET MODIFIER             = :V_MODIFIER
						     , MOD_DDTT             = SYSDATE
						     , YD_CARUD_WRK_BOOK_ID = :V_YD_CARUD_WRK_BOOK_ID
						 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
						   
						 	 
						jrParam.setField("YD_CARUD_WRK_BOOK_ID", ydCarudWrkBookId); //신야드대차스케쥴ID
						jrParam.setField("YD_TCAR_SCH_ID", ydTcarSchId);
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarProgStatCarudWrkbook", logId, methodNm, "대차스케줄상태 수정");
					
					}		*/			
										
					
					//출발위치 적치단 재료 단정보를 대차재료정보 단에 업데이트 (
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStackLayer8
					SELECT STACK_LAYER_GP
					      ,STOCK_ID
					   FROM USRYMA.TB_YM_STACKLAYER
					  WHERE STACK_COL_GP = :V_STACK_COL_GP
					    AND STOCK_ID IS NOT NULL    
					*/	   
					JDTORecordSet jsChk8 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStackLayer8", logId, methodNm, "출발위치단정보 조회");						
					
					JDTORecord jrTcMtl = JDTORecordFactory.getInstance().create();
	    			JDTORecord jsLay   = JDTORecordFactory.getInstance().create();
		    		for(int Loop_i = 1; Loop_i <= jsChk8.size(); Loop_i++) {

		    			
		    			jsChk8.absolute(Loop_i);
		    			jrTcMtl = JDTORecordFactory.getInstance().create();
		    			jrTcMtl.setRecord(jsChk8.getRecord());
		    			
		    			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarRftMvMtl 
						UPDATE USRYMA.TB_YM_TCARFTMVMTL
						   SET MODIFIER             = :V_MODIFIER
						     , MOD_DDTT             = SYSDATE
						     , STACK_LAYER_GP       = :V_STACK_LAYER_GP
						 WHERE YD_TCAR_SCH_ID       = :V_YD_TCAR_SCH_ID
						   AND STOCK_ID             = :V_STOCK_ID
		    			 */	 
		    			 
		    			jsLay.setField("MODIFIER"		    , modifier); 
		    			jsLay.setField("STACK_LAYER_GP"		, jrTcMtl.getFieldString("STACK_LAYER_GP")); 
		    			jsLay.setField("YD_TCAR_SCH_ID"     , ydTcarSchId);
		    			jsLay.setField("STOCK_ID" 	        , jrTcMtl.getFieldString("STOCK_ID")); 

						commDao.update(jsLay, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarRftMvMtl", logId, methodNm, "대차스케줄재료 Layer 수정");	    			
					}	
										
					
					if(!aFCarProgStat.equals(bFCarProgStat) )	{   // 같은 정보 두번 수신 시 상태가 변경되지 않았는데  선작업 세팅된 정보가  제거 되는 현상방지 
						//영대차출발이면 출발위치 적치Bed 비활성화 처리
						jrParam.setField("STACK_COL_GP"         , ydCarldStopLoc); //야드적치열구분
						jrParam.setField("STACK_BED_ACTIVE_STAT", "C"       ); //야드적치Bed활성상태(비활성화)
						
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActByCol
						UPDATE TB_YM_STACKER
						   SET MODIFIER              = :V_MODIFIER
						      ,MOD_DDTT              = SYSDATE
						      ,STACK_BED_ACTIVE_STAT = :V_STACK_BED_ACTIVE_STAT
						 WHERE STACK_COL_GP          = :V_STACK_COL_GP
						   AND DEL_YN                = 'N'
						*/	   
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActByCol", logId, methodNm, "출발위치 Bed 비활성화");
						
						//출발위치 적치단 재료 삭제
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrClr2 
						UPDATE TB_YM_STACKLAYER
						   SET MODIFIER                = :V_MODIFIER
						      ,MOD_DDTT                = SYSDATE
						      ,STOCK_ID                = NULL
						      ,STACK_LAYER_ACTIVE_STAT = 'C'
						      ,STACK_LAYER_STAT        = 'E'
						 WHERE STACK_COL_GP    = :V_STACK_COL_GP
						   AND DEL_YN                  = 'N'
						*/	    
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrClr2", logId, methodNm, "출발위치 단 비활성화");
					}	
					if ( "1".equals(PreWrkWo)){  //선작업 구분이 실행시  영대차 하차출발시 NEXT스케줄을 호출한다.
						jrParam.setField("YD_WBOOK_ID"         , ydCarldWrkbookId); //상차지 작업예약 id
						
						 /*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getBookMtlPartDel
						 SELECT COUNT(*) AS CNT
						   FROM TB_YM_WRKBOOKMTL  
						   WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
						     AND DEL_YN ='N' 						
                        */
			            JDTORecordSet jsChk1 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getBookMtlPartDel", logId, methodNm, "작업예약 부분삭제시판단조회");
						
						String CNT = "";
		
						if (jsChk1.size() > 0) {
							 CNT = commUtils.trim(jsChk1.getRecord(0).getFieldString("CNT"));
						}
						 String QueryId  = "";
						 if("0".equals(CNT)) QueryId = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWo77" ;
							/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWo77 
							SELECT RL.ITEM        AS  CARLOAD_STOP_LOC   --상차정치위치
							      ,XB.YD_WBOOK_ID AS  YD_WBOOK_ID_NEXT   --다음예약
							      ,BO.YD_SCH_CD   AS  CARLD_SCH_CD       --상차지스케줄코드
							  FROM  (SELECT MIN(YD_WBOOK_ID  ) AS YD_WBOOK_ID
							                 ,MIN(YD_BAY_GP    ) AS YD_BAY_GP
							                 ,MIN(YD_AIM_BAY_GP) AS YD_AIM_BAY_GP
							                 ,MIN(YD_WRK_PLAN_TCAR) AS YD_WRK_PLAN_TCAR
							             FROM (SELECT YD_WBOOK_ID
							                         ,YD_BAY_GP
							                         ,YD_AIM_BAY_GP
							                         ,YD_WRK_PLAN_TCAR
							                     FROM TB_YM_WRKBOOK
							                    WHERE YD_WRK_PLAN_TCAR = :V_YD_EQP_ID
							                      AND YD_WBOOK_ID NOT IN
							                         (SELECT NVL(YD_CARLD_WRK_BOOK_ID,YD_CARUD_WRK_BOOK_ID) AS YD_WBOOK_ID
							                            FROM TB_YM_TCARSCH
							                           WHERE DEL_YN = 'N'
							                             AND (YD_CARLD_WRK_BOOK_ID IS NOT NULL	OR YD_CARUD_WRK_BOOK_ID IS NOT NULL))
							                      AND ((SUBSTR(YD_SCH_CD,1,2) <> (NVL(SUBSTR(YD_TO_LOC_GUIDE,1,2),SUBSTR(YD_SCH_CD,1,2))))
							                            OR 
							                           (YD_SCH_CD LIKE SUBSTR(YD_SCH_CD,1,2)|| 'TC__U%')
							                          )
							                      AND DEL_YN = 'N'
							                    ORDER BY  YD_WBOOK_ID)
							            WHERE ROWNUM = 1) XB
							            
							        , USRYMA.TB_YM_RULE RL
							        , USRYMA.TB_YM_WRKBOOK BO 
							         WHERE RL.REPR_CD_GP = 'TCAR01'
							           AND RL.CD_GP    = '2'
							           AND RL.DEL_YN   = 'N'
							           AND RL.DTL_ITM1 = XB.YD_WRK_PLAN_TCAR
							           AND RL.DTL_ITM2 = XB.YD_BAY_GP    
							           AND XB.YD_WBOOK_ID = BO.YD_WBOOK_ID
							*/ 
						 else  QueryId = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWo78" ;  
						 /*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWo78  
						 SELECT RL.ITEM        AS  CARLOAD_STOP_LOC   --상차정치위치
						       ,XB.YD_WBOOK_ID AS  YD_WBOOK_ID_NEXT   --다음예약
						       ,BO.YD_SCH_CD   AS  CARLD_SCH_CD       --상차지스케줄코드
						   FROM  (SELECT MIN(YD_WBOOK_ID  ) AS YD_WBOOK_ID
						                  ,MIN(YD_BAY_GP    ) AS YD_BAY_GP
						                  ,MIN(YD_AIM_BAY_GP) AS YD_AIM_BAY_GP
						                  ,MIN(YD_WRK_PLAN_TCAR) AS YD_WRK_PLAN_TCAR
						              FROM (SELECT YD_WBOOK_ID
						                          ,YD_BAY_GP
						                          ,YD_AIM_BAY_GP
						                          ,YD_WRK_PLAN_TCAR
						                      FROM TB_YM_WRKBOOK
						                     WHERE YD_WRK_PLAN_TCAR = :V_YD_EQP_ID

						                       AND ((SUBSTR(YD_SCH_CD,1,2) <> (NVL(SUBSTR(YD_TO_LOC_GUIDE,1,2),SUBSTR(YD_SCH_CD,1,2))))
						                             OR 
						                            (YD_SCH_CD LIKE SUBSTR(YD_SCH_CD,1,2)|| 'TC__U%')
						                           )
						                       AND DEL_YN = 'N'
						                     ORDER BY YD_SCH_PRIOR, YD_WBOOK_ID)
						             WHERE ROWNUM = 1) XB
						             
						         , USRYMA.TB_YM_RULE RL
						         , USRYMA.TB_YM_WRKBOOK BO 
						          WHERE RL.REPR_CD_GP = 'TCAR01'
						            AND RL.CD_GP    = '2'
						            AND RL.DEL_YN   = 'N'
						            AND RL.DTL_ITM1 = XB.YD_WRK_PLAN_TCAR
						            AND RL.DTL_ITM2 = XB.YD_BAY_GP    
						            AND XB.YD_WBOOK_ID = BO.YD_WBOOK_ID */						 
						JDTORecordSet jsChk77 = commDao.select(jrParam, QueryId, logId, methodNm, "대차스케쥴정보next상차정보 조회");

						if (jsChk77 != null && jsChk77.size() > 0) {
							JDTORecord jrChk77 = jsChk77.getRecord(0);
							ydWbookId = commUtils.trim(jrChk77.getFieldString("YD_WBOOK_ID_NEXT"));
						 
							crnSchYn ="Y" ;
							
			 				jrParam.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID	
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchForEqpid1 
		 
							SELECT
							     
							   ( SELECT ITEM
							       FROM USRYMA.TB_YM_RULE RL 
							          WHERE RL.REPR_CD_GP = 'TCAR01'
							            AND RL.CD_GP    = '2'
							            AND RL.DEL_YN   = 'N'
							            AND RL.DTL_ITM1 = :V_YD_EQP_ID
							            AND RL.DTL_ITM2 = A.YD_BAY_GP ) BO_CARLD_STOP_LOC --작업예약 기준 상차지 정지 위치 
							  ,( SELECT ITEM
							       FROM USRYMA.TB_YM_RULE RL 
							          WHERE RL.REPR_CD_GP = 'TCAR01'
							            AND RL.CD_GP    = '2'
							            AND RL.DEL_YN   = 'N'
							            AND RL.DTL_ITM1 = :V_YD_EQP_ID
							            AND RL.DTL_ITM2 = A.YD_AIM_BAY_GP ) BO_CARUD_STOP_LOC --작업예약 기준 상차지 정지 위치             
							               
							 FROM  TB_YM_WRKBOOK A 
							 WHERE 1=1
							   AND A.YD_WBOOK_ID = :V_YD_WBOOK_ID
							   AND A.DEL_YN    = 'N' 
							*/	   
							JDTORecordSet jsChk73 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchForEqpid1", logId, methodNm, "대차스케줄코드검색");	
							
							JDTORecord jrTcar73 = jsChk73.getRecord(0);
							
							ydCarloadStopLoc   = commUtils.trim(jrTcar73.getFieldString("BO_CARLD_STOP_LOC"));
							ydCarunloadStopLoc = commUtils.trim(jrTcar73.getFieldString("BO_CARUD_STOP_LOC"));
							jrParam.setField("NEXT_STOP_LOC_L"   , ydCarloadStopLoc);     //상차지 위치
							//jrParam.setField("NEXT_STOP_LOC_U"   , ydCarunloadStopLoc);   //하차지 위치

							//대차대기동및 상차지 수정 FOR 라스트권상 후 대차출발지시용
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdEqpCurrBay78 
								UPDATE TB_YM_EQUIP
								   SET MODIFIER           = :V_MODIFIER
								      ,MOD_DDTT           = SYSDATE
								      ,CARLOAD_STOP_LOC   = :V_NEXT_STOP_LOC_L
								      ,WAIT_STOP_LOC      = :V_NEXT_STOP_LOC_L
								WHERE EQUIP_GP            = :V_YD_EQP_ID 
							*/ 
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdEqpCurrBay78", logId, methodNm, "대차대기동및 상사지(only) 수정 FOR 라스트권상 후 대차출발지시용");
							
							
							//대차대기동및 상사지 수정 FOR next to 위치 기준 설정
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchCurrBay78 
								UPDATE TB_YM_TCARSCH
								   SET MODIFIER          = :V_MODIFIER
								      ,MOD_DDTT          = SYSDATE
								      ,YD_CARLD_LEV_LOC  = :V_NEXT_STOP_LOC_L
								      ,YD_CARLD_STOP_LOC = :V_NEXT_STOP_LOC_L
								WHERE YD_EQP_ID          = :V_YD_EQP_ID 
								  AND DEL_YN             = 'N'
							*/ 
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchCurrBay78", logId, methodNm, "To위치기준으로 활용하기위한 Next상차지정보업데이트");						
						}
						
					
							  
					} 


				} else if ("B".equals(ydCarProgStat)) {
					ydSchReqGp = "3"; //영대차도착 : 하차도착(B)

					//크레인스케줄기동여부 'Y'이면
					if ("Y".equals(crnSchYn)) {
						ydWbookId = ydCarudWrkbookId; //야드하차작업예약ID 
						
						//jrParam.setField("CARUD_SCH_CD"    , SetEqCarudSchCd);   //예약정보 스케줄 다시 장비기준으로 셋팅
						jrParam.setField("YD_WBOOK_ID"    , ydCarldWrkbookId );         
						
						/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmWrkBookUdSchCd
						UPDATE TB_YM_WRKBOOK
						   SET MODIFIER                = :V_MODIFIER
						      ,MOD_DDTT                = SYSDATE
						      ,YD_SCH_CD               = :V_YD_SCH_CD
						 WHERE YD_WBOOK_ID             = :V_YD_WBOOK_ID
						   AND DEL_YN                  = 'N'*/ 
						 
						   
						//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmWrkBookUdSchCd", logId, methodNm, "영대차도착 크레인구동전 예약스케줄변경"); 
						
						
						/***
						 *  크레인 하차 예약 등록 여부 CHECK 하여 없으면 생성
						 */
						

						 /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getA8YSL009WbTCarIns  
						SELECT TM.STOCK_ID
						      ,TS.YD_CARUD_STOP_LOC
						      ,TM.STACK_BED_GP
						      ,TM.STACK_LAYER_GP
						      ,COUNT(*) OVER () - ROW_NUMBER() OVER (ORDER BY STACK_LAYER_GP) + 1 AS YD_UP_COLL_SEQ
						
						  FROM TB_YM_TCARSCH TS
						      ,TB_YM_TCARFTMVMTL TM
						 WHERE TS.YD_TCAR_SCH_ID = TM.YD_TCAR_SCH_ID
						   AND TM.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
						   AND TM.DEL_YN         = 'N'
						   AND TS.YD_CARUD_WRK_BOOK_ID IS NULL
						  ORDER BY TM.STACK_LAYER_GP
						*/						
						
						JDTORecordSet jsDnTc = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getA8YSL009WbTCarIns", logId, methodNm, "하차권하대상재!!!");
						//String YdCarudWrkBookId = commUtils.trim(jsDnTc.getRecord(0).getFieldString("YD_CARUD_WRK_BOOK_ID")) ;
						String ydNewWbookId = ""; 
						if (jsDnTc.size() > 0 ){
							
							JDTORecord jrParamTc = JDTORecordFactory.getInstance().create();
							JDTORecord jrDnTc 	 = JDTORecordFactory.getInstance().create();
							
							jrParamTc.setField("YD_SCH_ST_GP"        , "O"     ); //야드스케쥴기동구분
							jrParamTc.setField("YD_EQP_ID"           , ydEqpId       ); //대차설비id
							jrParamTc.setField("MODIFIER"            , modifier       ); 
							jrParamTc.setField("YD_WBOOK_ID"         , ydCarldWrkbookId    ); 
							jrParamTc.setField("YD_CARUD_STOP_LOC"   , ydStkColGp       ); 
							
							//String NextTc               = "" ;                  //다음대차
							//String NextCurrStopLoc      = "" ;                  //다음대차현정지위치
							String NextCarloadStopLoc   = "" ;                  //다음대차상차정지위치
							String NextCarunloadStopLoc = "" ;                  //다음대차하차정지위치
							String ydCarudWrkBookId_new = "";
							String ydCarudWrkBookId_new_First = "";
							for(int nIdx=0; nIdx < jsDnTc.size(); nIdx++) {
								
								//jrParamTc = JDTORecordFactory.getInstance().create();
								jrDnTc    = JDTORecordFactory.getInstance().create();
								//대차스케줄 처리 (영대차출발지시)
								//야드하차작업예약ID 생성
								
								//하차지작업예약 등록
								jrDnTc = jsDnTc.getRecord(nIdx);
							
								ydCarudWrkBookId_new = commDao.getSeqId(logId, methodNm, "WrkBook");
								if(nIdx==0) ydCarudWrkBookId_new_First = ydCarudWrkBookId_new;
								jrParamTc.setField("YD_CARUD_WRK_BOOK_ID", ydCarudWrkBookId_new); //야드하차작업예약ID
								jrParamTc.setField("STOCK_ID" 		, commUtils.trim(jrDnTc.getFieldString("STOCK_ID"))); 
								
							//	if(nIdx == 0 ){
									 
									//하차지작업예약 등록
									 
									/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA8YSL009WbTCarIns  

									MERGE INTO TB_YM_WRKBOOK WB USING (
									       SELECT :V_YD_CARUD_WRK_BOOK_ID AS YD_WBOOK_ID      --야드작업예약ID
									     , :V_MODIFIER             AS MODIFIER         --수정자
									     , SYSDATE                 AS MOD_DDTT         --수정일시
									     , 'N'                     AS DEL_YN           --삭제유무
									     , WB.YD_GP                                    --야드구분
									     , WB.YD_BAY_GP                                --야드동구분
									     , WB.YD_SCH_CD                                --야드스케쥴코드
									     , (SELECT SR.YD_WRK_CRN_PRIOR
									          FROM TB_YM_SCHEDULERULE SR
									         WHERE SR.YD_SCH_CD = WB.YD_SCH_CD) 
									                               AS YD_SCH_PRIOR     --야드스케쥴우선순위
									     , 'W'                     AS YD_SCH_PROG_STAT --야드스케쥴진행상태(스케줄수행대기)
									     , :V_YD_SCH_ST_GP         AS YD_SCH_ST_GP     --야드스케쥴기동구분
									     , '1'                     AS YD_SCH_REQ_GP    --야드스케쥴요청구분(대차상차완료)
									     , WB.YD_TO_LOC_DCSN_MTD                       --야드To위치결정방법
									     , WB.YD_TO_LOC_GUIDE                          --야드To위치Guide
									     , WB.YD_WRK_PLAN_TCAR                         --야드작업계획대차
									  FROM (SELECT WB.YD_GP
									             , SUBSTR(:V_YD_CARUD_STOP_LOC,2,1)       AS YD_BAY_GP
									             , '2'||(:V_YD_CARUD_STOP_LOC,2,1)|| SUBSTR(EQ.CARUD_SCH_CD,3)  AS YD_SCH_CD 
									             , CASE WHEN WB.YD_TO_LOC_DCSN_MTD = 'F' AND WB.YD_AIM_BAY_GP != SUBSTR(WB.YD_TO_LOC_GUIDE,2,1)
									                    THEN 'S' ELSE WB.YD_TO_LOC_DCSN_MTD END 
									               AS YD_TO_LOC_DCSN_MTD
									             , CASE WHEN WB.YD_TO_LOC_DCSN_MTD = 'F' AND WB.YD_AIM_BAY_GP != SUBSTR(WB.YD_TO_LOC_GUIDE,2,1)
									                    THEN ''  ELSE WB.YD_TO_LOC_GUIDE    END 
									               AS YD_TO_LOC_GUIDE
									             , WB.YD_WRK_PLAN_TCAR
									          FROM TB_YM_WRKBOOK WB
									              ,TB_YM_EQUIP   EQ             
									         WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
									           AND WB.YD_WRK_PLAN_TCAR = EQ.EQUIP_GP(+)) WB 
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
									 commDao.update(jrParamTc, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA8YSL009WbTCarIns", logId, methodNm, "작업예약 등록");
									 
									 //하차 스케줄이 대차상차스케줄인경우  예약정보 보정작업
									 if("TCUM".equals(EqCarudSchCd.substring(2, 4)+EqCarudSchCd.substring(6))){
										 
										 commUtils.printLog(logId, "++++++++++++++++++++++++++++++ 하차 스케줄이 대차상차스케줄인경우  예약정보 보정작업 : " + ydEqpId + " ++++++++++++++++++++++++++++++", "SL");
										 
										 //같은동에 정차한 다음대차를 차는다.
										 /*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getEquipNearbyAmidBay
										 SELECT  *
										   FROM (
										   SELECT A.EQUIP_GP
										         ,CASE WHEN  :V_EQUIP_GP='2XTC03' THEN DECODE(((COUNT(*) OVER() )),1,'Y',DECODE(A.EQUIP_GP,'2XTC02','Y','N'))
										               WHEN  :V_EQUIP_GP='2XTC02' THEN DECODE(((COUNT(*) OVER() )),1,'Y',DECODE(A.EQUIP_GP,'2XTC03','Y','N'))  
										               WHEN  :V_EQUIP_GP='2XTC01' THEN DECODE(((COUNT(*) OVER() )),1,'Y',DECODE(A.EQUIP_GP,'2XTC02','Y','N'))  
										               ELSE '' END AS YN_TAG
										        ,COUNT(*) OVER(  ) RN
										         ,A.CURR_STOP_LOC  
										         ,A.CARLOAD_STOP_LOC
										         ,A.CARUNLOAD_STOP_LOC
										    FROM TB_YM_EQUIP A
										        ,TB_YM_TCARSCH B
										    WHERE A.EQUIP_GP !=  :V_EQUIP_GP  
										      AND A.DEL_YN ='N'
										      AND B.DEL_YN ='N'
										      AND A.EQUIP_GP = B.YD_EQP_ID
										      AND B.YD_EQP_WRK_STAT ='U' --공차
										      AND A.CURR_STOP_LOC IN 
										   
										  ( SELECT ITEM
										   FROM USRYMA.TB_YM_RULE RL 
										      WHERE RL.REPR_CD_GP = 'TCAR01'
										        AND RL.CD_GP    = '2'
										        AND RL.DEL_YN   = 'N'
										        AND RL.DTL_ITM1 != :V_EQUIP_GP  
										        
										        AND RL.DTL_ITM2 = :V_BAY) 
										 ) WHERE YN_TAG ='Y'*/
										 
										    //jrParam.setField("EQUIP_GP"           , ydEqpId       ); //전대차설비id
										    //jrParam.setField("BAY"                , ydStkColGp.substring(1, 2)       ); //전대차설비목표동
										    
											//JDTORecordSet jsLookTc = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getEquipNearbyAmidBay", logId, methodNm, "대하차스케줄이 상차인 대차찾기조회");
											
										 
											jrParam.setField("YD_CRN_SCH_ID"      , ""     ); 
											jrParam.setField("CARLOAD_STOP_LOC"   , TcCurrBay.substring(0,4)     ); 
											/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getTcCarLdStopLoc 
											SELECT CASE WHEN YD_EQP_WRK_SH = 2 THEN CARLOAD_STOP_LOC || '0102' 
											            ELSE CARLOAD_STOP_LOC || '0101'
											            END AS YD_DN_WO_LOC
											       ,EQUIP_GP
											       ,CARLOAD_STOP_LOC
											       ,CARUNLOAD_STOP_LOC
											       ,CARLD_SCH_CD
											       ,CARUD_SCH_CD
											FROM   (
											            SELECT EQUIP_GP
											                  ,CARLOAD_STOP_LOC
											                  ,CARUNLOAD_STOP_LOC
											                  ,CARLD_SCH_CD
											                  ,CARUD_SCH_CD
											                  ,(
											                     SELECT YD_EQP_WRK_SH
											                     FROM   TB_YM_CRNSCH
											                     WHERE  YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
											                   ) AS YD_EQP_WRK_SH
											            FROM   TB_YM_EQUIP
											            WHERE  YD_GP = '2'
											            AND    EQUIP_KIND = 'TC'
											            AND    DEL_YN = 'N'
											            AND    CARLOAD_STOP_LOC LIKE :V_CARLOAD_STOP_LOC || '%'

											       ) AA
											WHERE  ROWNUM = 1  */
											JDTORecordSet jsLookTc = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getTcCarLdStopLoc", logId, methodNm, "동간작업기준 조회");
											
											if (jsLookTc.size() >0) {
												//throw new Exception("같은동에 이적할 다른대차가 존재하지 않습니다.");
											    //아래는 존재하는 경우 처리임
												 NextTc               = commUtils.trim(jsLookTc.getRecord(0).getFieldString("EQUIP_GP")) ;                  //다음대차
												 //NextCurrStopLoc      = commUtils.trim(jsLookTc.getRecord(0).getFieldString("CURR_STOP_LOC")) ;             //다음대차현정지위치
												 NextCarloadStopLoc   = commUtils.trim(jsLookTc.getRecord(0).getFieldString("CARLOAD_STOP_LOC")) ;          //다음대차상차정지위치
												 NextCarunloadStopLoc = commUtils.trim(jsLookTc.getRecord(0).getFieldString("CARUNLOAD_STOP_LOC")) ;        //다음대차하차정지위치
											 
												
												//선행대차의 하차스케줄이  다음 대차의 상차 스케줄일경우 (대차대대차 이송건) 초기 없는경우 대비하여 대차스케줄에 상차예약ID를 세팅한다.
												/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateTcshbefore2
													UPDATE TB_YM_TCARSCH
													   SET MODIFIER              = :V_MODIFIER
													      ,MOD_DDTT              = SYSDATE
													      ,YD_CARLD_WRK_BOOK_ID  = :V_YD_CARLD_WRK_BOOK_ID
													      ,YD_CARLD_STOP_LOC     = :V_YD_CARLD_STOP_LOC
													      ,YD_CARUD_STOP_LOC     = :V_YD_CARUD_STOP_LOC
													WHERE YD_EQP_ID              = :V_YD_WRK_PLAN_TCAR
													  AND DEL_YN                 = 'N'
												*/ 
											    //jrParam.setField("YD_CARLD_WRK_BOOK_ID"       , ydCarudWrkBookId_new          );               //하차치면서 상차인 예약 id
											    jrParam.setField("YD_CARLD_WRK_BOOK_ID"       , ydCarudWrkBookId_new_First          );               //하차치면서 상차인 예약 id
											   // jrParam.setField("YD_CARLD_LEV_LOC"          , NextCurrStopLoc       ); // 
											    jrParam.setField("YD_CARLD_STOP_LOC"          , NextCarloadStopLoc       ); //하차정지위치
											    jrParam.setField("YD_CARUD_STOP_LOC"          , NextCarunloadStopLoc     ); //하차정지위치
											    jrParam.setField("YD_WRK_PLAN_TCAR"           , NextTc                   ); //전대차설비id
											    
												commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateTcshbefore2", logId, methodNm, "대차대대차 이송건 초기 대차스케줄에 상차예약ID를 세팅");										
	
											 
												
												//계획대차가 안들어 가는 이유로  후속작업 을 위하여
												/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateWrkBkLevWoSlab_before1 /
												UPDATE TB_YM_WRKBOOK
												   SET MODIFIER             = :V_MODIFIER
												     , MOD_DDTT             = SYSDATE
												     , YD_AIM_BAY_GP        = :V_YD_AIM_BAY_GP    --다음대차의 목표동
												     , YD_WRK_PLAN_TCAR     = :V_YD_WRK_PLAN_TCAR --야드작업 계획대차업데이트
												 WHERE YD_WBOOK_ID          = :V_YD_WBOOK_ID
												 */	 
												
												jrParam.setField("YD_WBOOK_ID"                , ydCarudWrkBookId_new          );               //하차치면서 상차인 예약 id
												jrParam.setField("YD_AIM_BAY_GP"              , NextCarunloadStopLoc.substring(1, 2)       ); // 다음대차의 상차스케줄이된 하차목표동
												commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateWrkBkLevWoSlab_before1", logId, methodNm, "작업예약정보  야드작업 계획대차업데이트 수정");
											
											}
									 }
									 
									//}
		
								//작업예약재료 등록
								/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA7YML009WbMtlTCarIns 
								INSERT INTO TB_YM_WRKBOOKMTL WM
								       (WM.YD_WBOOK_ID          , WM.STOCK_ID       , WM.REGISTER       , WM.REG_DDTT    ,
								        WM.MODIFIER             , WM.MOD_DDTT       , WM.DEL_YN         , WM.STACK_COL_GP,
								        WM.STACK_BED_GP         , WM.STACK_LAYER_GP , WM.YD_UP_COLL_SEQ)
								VALUES (:V_YD_CARUD_WRK_BOOK_ID , :V_STOCK_ID       , :V_MODIFIER       , SYSDATE        ,
								        :V_MODIFIER             , SYSDATE           , 'N'               , :V_STACK_COL_GP,
								        :V_STACK_BED_GP         , :V_STACK_LAYER_GP , :V_YD_UP_COLL_SEQ);        
								*/        
								jrParamTc.setField("STOCK_ID" 		, commUtils.trim(jrDnTc.getFieldString("STOCK_ID"))); 
								
								//if("TCUM".equals(EqCarudSchCd.substring(2, 4)+EqCarudSchCd.substring(6))){
									
								//	jrParamTc.setField("STACK_COL_GP" 	, NextCarunloadStopLoc); 
								//}else{
									
									jrParamTc.setField("STACK_COL_GP" 	, commUtils.trim(jrDnTc.getFieldString("YD_CARUD_STOP_LOC"))); 
								//}
								jrParamTc.setField("STACK_BED_GP" 	, commUtils.trim(jrDnTc.getFieldString("STACK_BED_GP"))); 
								jrParamTc.setField("STACK_LAYER_GP" , commUtils.trim(jrDnTc.getFieldString("STACK_LAYER_GP"))); 
								jrParamTc.setField("YD_UP_COLL_SEQ" , commUtils.trim(jrDnTc.getFieldString("YD_UP_COLL_SEQ"))); 
								
								commDao.update(jrParamTc, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA7YML009WbMtlTCarIns", logId, methodNm, "작업예약재료 등록");							        
							}
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarProgStatCarudWrkbook 
							UPDATE TB_YM_TCARSCH
							   SET MODIFIER             = :V_MODIFIER
							     , MOD_DDTT             = SYSDATE
							     , YD_CARUD_WRK_BOOK_ID = :V_YD_CARUD_WRK_BOOK_ID
							 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
							   
							 */	 
							jrParam.setField("YD_CARUD_WRK_BOOK_ID", ydCarudWrkBookId_new_First); //신야드대차스케쥴ID
							jrParam.setField("YD_TCAR_SCH_ID", ydTcarSchId);
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarProgStatCarudWrkbook", logId, methodNm, "대차스케줄상태 수정");
						
							
							ydWbookId = ydCarudWrkBookId_new ;
						}	
						
						
						
						
					}

					//영대차도착이고 적치Bed 비활성화 이면 활성화
					jrParam.setField("STACK_COL_GP", ydStkColGp); //적치Bed 야드적치열구분(현위치)

					if (!"L".equals(ydStkBedActStatUd)) {
						//현위치 적치Bed 활성화 처리
						jrParam.setField("STACK_BED_ACTIVE_STAT", "L"); //적치Bed 야드적치Bed활성상태(활성)
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActByCol
						UPDATE TB_YM_STACKER
						   SET MODIFIER              = :V_MODIFIER
						      ,MOD_DDTT              = SYSDATE
						      ,STACK_BED_ACTIVE_STAT = :V_STACK_BED_ACTIVE_STAT
						 WHERE STACK_COL_GP          = :V_STACK_COL_GP
						   AND DEL_YN                = 'N'
						*/	   
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActByCol", logId, methodNm, "도착위치 Bed 활성화");

					} 

					//도착지적치단 세팅 
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarMtl9 
					SELECT   YD_TCAR_SCH_ID
					       , STOCK_ID
					       , STACK_LAYER_GP
					   FROM TB_YM_TCARFTMVMTL
					  WHERE YD_TCAR_SCH_ID  = :V_YD_TCAR_SCH_ID
					      AND DEL_YN    = 'N'
                        ORDER BY  STACK_LAYER_GP					      
					*/	   
					JDTORecordSet jsChk9 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarMtl9", logId, methodNm, "대차스케줄재료정보 조회");

					
					JDTORecord jrTcMtl = JDTORecordFactory.getInstance().create();
	    			JDTORecord jsLay   = JDTORecordFactory.getInstance().create();
		    		for(int Loop_i = 1; Loop_i <= jsChk9.size(); Loop_i++) {

		    			
		    			jsChk9.absolute(Loop_i);
		    			jrTcMtl = JDTORecordFactory.getInstance().create();
		    			jrTcMtl.setRecord(jsChk9.getRecord());
		    			
		    			 
		    			jsLay.setField("MODIFIER"		    , modifier); 
		    			jsLay.setField("STOCK_ID" 	        , jrTcMtl.getFieldString("STOCK_ID")); 
		    			jsLay.setField("STACK_COL_GP"		, ydStkColGp); 
		    			jsLay.setField("STACK_LAYER_GP"		, jrTcMtl.getFieldString("STACK_LAYER_GP")); 

						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStkLyrTCarStl3  
						UPDATE TB_YM_STACKLAYER
						   SET MODIFIER                = :V_MODIFIER
						      ,MOD_DDTT                = SYSDATE
						      ,STACK_LAYER_ACTIVE_STAT = 'E'
						      ,STACK_LAYER_STAT        = 'C'
						      ,STOCK_ID                = :V_STOCK_ID
						 WHERE STACK_COL_GP            = :V_STACK_COL_GP
						   AND STACK_LAYER_GP          = :V_STACK_LAYER_GP
						   AND STACK_LAYER_ACTIVE_STAT IN('C','E')
						   AND STACK_LAYER_STAT        ='E'
						   AND DEL_YN                  ='N'
						*/
						commDao.update(jsLay, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStkLyrTCarStl3", logId, methodNm, "도착위치 단 활성화");
		
					  if(!(ydStkColGp.equals(ydCarldStopLoc)))	{
						//출발위치 LayerBed 비활성화 처리
						JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();			
						jrParam1.setResultCode(logId);	//Log ID
						jrParam1.setResultMsg(methodNm);	//Log Method Name
						jrParam1.setField("MODIFIER"       		 , modifier    ); //수정자
						jrParam1.setField("STACK_COL_GP"      	 , ydCarldStopLoc); //상차지야드적치열구분
						jrParam1.setField("STOCK_ID" 	        , jrTcMtl.getFieldString("STOCK_ID")); 
	 				
						//상차위치 적치단 재료 삭제
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrInActiveTC3 
						UPDATE TB_YM_STACKLAYER
						   SET MODIFIER                = :V_MODIFIER
						      ,MOD_DDTT                = SYSDATE
						      ,STOCK_ID                = NULL
						      ,STACK_LAYER_ACTIVE_STAT = 'C'
						      ,STACK_LAYER_STAT        = 'E'
						 WHERE STACK_COL_GP            = :V_STACK_COL_GP
						   AND STOCK_ID                = :V_STOCK_ID
						   AND DEL_YN                  = 'N'
						*/   
						commDao.update(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrInActiveTC3", logId, methodNm, "출발위치 단 비활성화");						
						
		    		}
						jrParam.setField("STOCK_ID_"+(Loop_i-1)			,commUtils.trim(jrTcMtl.getFieldString("STOCK_ID"))); //재료번호
						jrParam.setField("LOAD_LOC_CD_"+(Loop_i-1)		,commUtils.trim(jrTcMtl.getFieldString("STACK_LAYER_GP"))); //차량적재위치
						jrParam.setField("WORK_STATE_"+(Loop_i-1)		,commUtils.trim("0")); //작업상태
						jrParam.setField("WORK_COIL_MAX_CNT", "" + Loop_i);
					}	
	  	
			    	//전송 데이터 설정
			    	jrParam.setField("PT_LOAD_LOC"			, ydStkColGp); //하차동 위치
			    	jrParam.setField("CAR_NO"				, ydEqpId); //차량번호	
			    	jrParam.setField("CARD_NO"				, "0000"); //차량번호	
			    	jrParam.setField("PT_CLS"				, "TC"); //차량구분 "TT":TTcar, "TR":트레일러, "TC":대차
			    	jrParam.setField("WORK_CLS"				, "3"); //작업구분 1:출하입고,2:출하출고,3:구내입고,4:구내출고
					

					//차량예정정보 백업 송신  1번대차 C동 도착일경우 만 차량예정보 전송 --> C동 대차 형상 철거로 아래 로직 주석처리 함 2020.03.06
			    	//if(ydEqpId.equals("2XTC01") && (ydStkColGp.substring(1, 2)).equals("C"))  {
					//    jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L008BackUp", jrParam));
					//    
					//    jrParam.setField("ITEM"			, ydStkColGp.substring(1, 2)); //동
                    //    //형상관리 적용여부  
					//	/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTcarFormRule
					//	  SELECT DTL_ITM1
					//	  FROM USRYMA.TB_YM_RULE
					//	 WHERE 1=1
					//	  AND REPR_CD_GP ='YM2006' 
					//	  AND CD_GP      ='2'
					//	  AND DEL_YN     = 'N'
					//	  AND ITEM       = :V_ITEM
					//	*/
                    //
					//	JDTORecordSet jsTcRule = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTcarFormRule", logId, methodNm, "1번대차C동하차형상 적용조회");   								
					//	String ApplyYN = commUtils.trim(jsTcRule.getRecord(0).getFieldString("DTL_ITM1"));
                    //
					//	//대차 형상관리 적용여부따라 스케줄을  Slab 무게중심(형상인식)측정 정보(A8YML029) 에서 구동할지 판단
					//    if("Y".equals(ApplyYN)){
					//    	crnSchYn ="N";
					//    }
			    	//}
		    		
					//상차지 정보 Clear 추가
					//출발위치 적치Bed 비활성화 처리
					JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
					jrParam1.setResultCode(logId);	//Log ID
					jrParam1.setResultMsg(methodNm);	//Log Method Name
					jrParam1.setField("MODIFIER"       		 , modifier    ); //수정자
					jrParam1.setField("STACK_COL_GP"      	 , ydCarldStopLoc); //야드적치열구분
					jrParam1.setField("STACK_BED_ACTIVE_STAT", "C"       ); //야드적치Bed활성상태(비활성화)
					
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchStaErrChk 
					SELECT DECODE(COUNT(*),0, 'N','Y') AS CLEAR_YN 
					   FROM TB_YM_STACKLAYER
					 WHERE STACK_COL_GP = :V_STACK_COL_GP
					   AND STOCK_ID IS NOT NULL
					*/   
					JDTORecordSet jsTcar1 = commDao.select(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchStaErrChk", logId, methodNm, "대차스케줄조회");

					JDTORecord jr1Temp = null;
					String szCLEAR_YN  = "";
					if (jsTcar1 == null || jsTcar1.size() == 0) {
					} else {				

						jr1Temp = jsTcar1.getRecord(0);
						szCLEAR_YN		= commUtils.trim(jr1Temp.getFieldString("CLEAR_YN")); 
						//상차지 정보 Clear 추가
						if (szCLEAR_YN.equals("Y")) {

							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActByCol
							UPDATE TB_YM_STACKER
							   SET MODIFIER              = :V_MODIFIER
							      ,MOD_DDTT              = SYSDATE
							      ,STACK_BED_ACTIVE_STAT = :V_STACK_BED_ACTIVE_STAT
							 WHERE STACK_COL_GP          = :V_STACK_COL_GP
							   AND DEL_YN                = 'N'
							*/	   
							commDao.update(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActByCol", logId, methodNm, "출발위치 BED 비활성화");
							
/*							//상차위치 적치단 재료 삭제
							 com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrInActiveTC3 
							UPDATE TB_YM_STACKLAYER
							   SET MODIFIER                = :V_MODIFIER
							      ,MOD_DDTT                = SYSDATE
							      ,STOCK_ID                = NULL
							      ,STACK_LAYER_ACTIVE_STAT = 'C'
							      ,STACK_LAYER_STAT        = 'E'
							 WHERE STACK_COL_GP            = :V_STACK_COL_GP
							   AND SUBSTR(STACK_COL_GP,1,1)= '2'
							   AND SUBSTR(STACK_COL_GP,3,2)= 'TC'
							   AND DEL_YN                  = 'N'
							   AND STOCK_ID IS NULL
							   
							commDao.update(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrInActiveTC3", logId, methodNm, "출발위치 단 비활성화");*/
						}	
					}
					

					
				} else if ("1".equals(ydCarProgStat)) {
					ydSchReqGp = "5"; //공대차출발 : 상차출발(1)

					//공대차출발이면 출발위치 적치Bed 비활성화 처리
					jrParam.setField("STACK_COL_GP"          , ydStkColGp); //야드적치열구분
					jrParam.setField("STACK_BED_ACTIVE_STAT" , "C"       ); //야드적치Bed활성상태(비활성화)

					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActByCol
					UPDATE TB_YM_STACKER
					   SET MODIFIER              = :V_MODIFIER
					      ,MOD_DDTT              = SYSDATE
					      ,STACK_BED_ACTIVE_STAT = :V_STACK_BED_ACTIVE_STAT
					 WHERE STACK_COL_GP          = :V_STACK_COL_GP
					   AND DEL_YN                = 'N'
					*/	   
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActByCol", logId, methodNm, "출발위치 BED 비활성화");

					//출발위치 적치단 재료 삭제
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrInActiveTC 
					UPDATE TB_YM_STACKLAYER
					   SET MODIFIER                = :V_MODIFIER
					      ,MOD_DDTT                = SYSDATE
					      ,STOCK_ID                = NULL
					      ,STACK_LAYER_ACTIVE_STAT = 'C'
					      ,STACK_LAYER_STAT        = 'E'
					 WHERE STACK_COL_GP            = :V_STACK_COL_GP
					   AND SUBSTR(STACK_COL_GP,1,1)= '2'
					   AND SUBSTR(STACK_COL_GP,3,2)= 'TC'
					   AND DEL_YN                  = 'N'
					*/   
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrInActiveTC2", logId, methodNm, "출발위치 단 비활성화");

					

				} else {
					String StackLayerStat ="" ;
					String YdCarProgStat  ="" ;
					

					
					// 전제 크레인작업 지시 있는경우 공대차 상차도착,영대차 하차도착시  대차초기화 할때발생(공대차상차대기상태)  
					// 대차 Layer와 대차스케줄을  크레인 스케줄 기준으로 살린다.
					// 크레인스케줄기동여부 'N',  상차작업예약 없음 , 하차예약없음   
					
					if("N".equals(crnSchYn) && ("".equals(ydCarldWrkbookId) || "".equals(ydCarudWrkbookId) )){
						/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTbYmTcLayerRollBk               
          
							 SELECT B.STOCK_ID
							 , TO_CHAR( ROW_NUMBER() OVER(PARTITION BY  B.YD_CRN_SCH_ID  ORDER BY B.STACK_LAYER_GP,B.STOCK_ID  ) ,'FM00') AS STACK_LAYER_GP  
							 , B.STACK_LAYER_GP
							 , A.YD_SCH_CD
							 , B.YD_CRN_SCH_ID
							 , A.YD_BAY_GP
							 , C.YD_WBOOK_ID
							 , ( SELECT ITEM
							 FROM USRYMA.TB_YM_RULE RL 
							    WHERE RL.REPR_CD_GP = 'TCAR01'
							      AND RL.CD_GP    = '2'
							      AND RL.DEL_YN   = 'N'
							      AND RL.DTL_ITM1 = C.YD_WRK_PLAN_TCAR
							      AND RL.DTL_ITM2 = A.YD_BAY_GP ) AS STACK_COL_GP 
							 ,CASE WHEN NVL(C.YD_BAY_GP,'N')  = NVL(C.YD_AIM_BAY_GP,'N')
							       THEN 'U' -- 하차스케줄 구분
							       ELSE 'L' END AS LU --상차스케줄 구분
							 FROM TB_YM_CRNSCH  A
							     ,TB_YM_CRNWRKMTL B 
							     ,TB_YM_WRKBOOK C
							WHERE 1 = 1
							  AND A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
							  AND A.YD_WBOOK_ID   = C.YD_WBOOK_ID
							  AND C.YD_WRK_PLAN_TCAR = :V_YD_EQP_ID
							  AND A.DEL_YN = 'N'
							  AND B.DEL_YN = 'N'
							  AND C.DEL_YN  ='N'
					  */
						 JDTORecordSet jsChk91 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTbYmTcLayerRollBk", logId, methodNm, "대차Layer복원 조회");                     
						 
							JDTORecord jrTcLay = JDTORecordFactory.getInstance().create();
			    			JDTORecord tcLay   = JDTORecordFactory.getInstance().create();
				    		for(int Loop_i = 1; Loop_i <= jsChk91.size(); Loop_i++) {

				    			
				    			jsChk91.absolute(Loop_i);
				    			jrTcLay = JDTORecordFactory.getInstance().create();
				    			jrTcLay.setRecord(jsChk91.getRecord());
				    			String LU = commUtils.trim(jrTcLay.getFieldString("LU")) ;
				    			
				    			if("L".equals(LU)) {      //상차스케줄
									ydSchReqGp      ="6"; //공대차도착 : 상차도착(2) or 상차대기(0)
								    StackLayerStat  ="D"; //권하대기
								    YdCarProgStat   ="2"; //차량진행상태
				    			}else{
									ydSchReqGp      ="3"; //영대차하차도착 (야드스케줄요청구분)
								    StackLayerStat  ="U";  //권상대기
								    YdCarProgStat   ="B";
				    			}
				    			
				    			
				    			tcLay.setField("MODIFIER"		    , modifier); 
				    			tcLay.setField("STACK_LAYER_STAT"	, StackLayerStat); 
				    			tcLay.setField("STOCK_ID" 	        , jrTcLay.getFieldString("STOCK_ID")); 
				    			tcLay.setField("STACK_COL_GP"		, jrTcLay.getFieldString("STACK_COL_GP")); 
				    			tcLay.setField("STACK_LAYER_GP"		, jrTcLay.getFieldString("STACK_LAYER_GP")); 

				    			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStkLyrTCarStl5   
								UPDATE TB_YM_STACKLAYER
								   SET MODIFIER                = :V_MODIFIER
								      ,MOD_DDTT                = SYSDATE
								      ,STACK_LAYER_ACTIVE_STAT = 'E'
								      ,STACK_LAYER_STAT        = :V_STACK_LAYER_STAT
								      ,STOCK_ID                = :V_STOCK_ID
								 WHERE STACK_COL_GP            = :V_STACK_COL_GP
								   AND STACK_LAYER_GP          = :V_STACK_LAYER_GP
								   AND STOCK_ID                IS  NULL
								   AND DEL_YN                  ='N'
								*/
								commDao.update(tcLay, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStkLyrTCarStl5", logId, methodNm, "대차Layer복원"); 
		                        
							  //if(Loop_i == 1) {
								    RollBackTag ="R";
									tcLay.setField("MODIFIER"       		, modifier    ); 	                             //수정자
					    			tcLay.setField("YD_CAR_PROG_STAT"	    , YdCarProgStat); 			                     //야드차량진행상태  
					    			tcLay.setField("YD_TCAR_SCH_ID"   	    , ydTcarSchId);   	                             //대차SCH
									String QueryId ="" ;
									
										if("2".equals(YdCarProgStat)){
											tcLay.setField("YD_CARLD_WRK_BOOK_ID"   , jrTcLay.getFieldString("YD_WBOOK_ID")); 		 //상차 작업 예약
											
											/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarProgStatCarldWrkbook /
											UPDATE TB_YM_TCARSCH
											   SET MODIFIER             = :V_MODIFIER
											     , MOD_DDTT             = SYSDATE
											     , YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT --야드차량진행상태
											     , YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
											 WHERE YD_TCAR_SCH_ID       = :V_YD_TCAR_SCH_ID
											 */	
											QueryId ="com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarProgStatCarldWrkbook";
										}
										else {
											tcLay.setField("YD_CARUD_WRK_BOOK_ID"   , jrTcLay.getFieldString("YD_WBOOK_ID")); 		 //하차 작업 예약
											tcLay.setField("YD_CARUD_STOP_LOC"      , SetEqCarunloadStopLoc);
											/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarProgStatCarldWrkbook1  
											UPDATE TB_YM_TCARSCH
											   SET MODIFIER             = :V_MODIFIER
											     , MOD_DDTT             = SYSDATE
											     , YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT --야드차량진행상태
											     , YD_CARUD_WRK_BOOK_ID = :V_YD_CARUD_WRK_BOOK_ID
											     , YD_CARUD_STOP_LOC    = :V_YD_CARUD_STOP_LOC
											 WHERE YD_TCAR_SCH_ID       = :V_YD_TCAR_SCH_ID
											 */
											QueryId ="com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarProgStatCarldWrkbook1";
										}
 
									commDao.update(tcLay, QueryId, logId, methodNm, "대차스케줄상태 수정");
							  //}
								
								
		                   } 
					}
					//크레인스케줄기동여부 'Y'이면
					else if ("Y".equals(crnSchYn)) {
						ydWbookId = ydCarldWrkbookId; //야드상차작업예약ID
					} 
					else {
						/***************************
						 *  공대차 도착시 현재 대차작업이 없고 해당동 작업 지시가 있는 경우 
						 *  1.대차 상차 대기로 변경
						 */
						//이송하차 대차상차 스케줄일경우 야드작업 계획대차가 안들어 가는 이유로  후속작업 을 위하여 
						/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWoSlab_before 
						SELECT MIN(YD_WBOOK_ID) AS YD_WBOOK_ID
						 FROM (SELECT YD_WBOOK_ID
						             ,YD_BAY_GP
						             ,YD_AIM_BAY_GP
						         FROM TB_YM_WRKBOOK
						        WHERE YD_WBOOK_ID NOT IN
						                      (SELECT YD_CARLD_WRK_BOOK_ID AS YD_WBOOK_ID
						                         FROM TB_YM_TCARSCH
						                        WHERE DEL_YN = 'N'
						                          AND (YD_CARLD_WRK_BOOK_ID IS NOT NULL	))
						                   AND ((SUBSTR(YD_SCH_CD,1,2) <> (NVL(SUBSTR(YD_TO_LOC_GUIDE,1,2),SUBSTR(YD_SCH_CD,1,2))))
						                         OR 
						                        (YD_SCH_CD LIKE SUBSTR(YD_SCH_CD,1,2)|| 'PT__L%' )  --대차상차 스케줄에 이송하차                      
						                       )
						                   AND DEL_YN = 'N'
						                 ORDER BY YD_WBOOK_ID)
						         WHERE ROWNUM = 1
						*/         
                    	JDTORecordSet jsTcarNextBf = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWoSlab_before", logId, methodNm, "대차우선순위전이송하차대차상차있는지조회");
						
						String ydTcarNextWbookIdBf = commUtils.trim(jsTcarNextBf.getRecord(0).getFieldString("YD_WBOOK_ID")); 
                        if(!"".equals(ydTcarNextWbookIdBf))  {
							jrParam.setField("YD_WBOOK_ID"   	    , ydTcarNextWbookIdBf);   	//작업예약번호
							jrParam.setField("YD_WRK_PLAN_TCAR"   	, ydEqpId);   	            //대차번호
							
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateWrkBkLevWoSlab_before /
							UPDATE TB_YM_WRKBOOK
							   SET MODIFIER             = :V_MODIFIER
							     , MOD_DDTT             = SYSDATE
							     , YD_WRK_PLAN_TCAR     = :V_YD_WRK_PLAN_TCAR --야드작업 계획대차업데이트
							 WHERE YD_WBOOK_ID          = :V_YD_WBOOK_ID
							 */	 
							  
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateWrkBkLevWoSlab_before", logId, methodNm, "작업예약정보  야드작업 계획대차업데이트 수정");  						
						
                        }
						
						
						
						
	    	    		/*지정된 도착동,우선순위가 빠르고, 작업예약순서가 빠른 작업예약 조회*/
                        /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWoSlab  
                        -- 대차스케줄 공대차출발지시 조회 - 
                        SELECT TS.YD_TCAR_SCH_ID
                              ,EQ.WPROG_STAT                AS YD_EQP_STAT
                              ,EQ.WORK_MODE                 AS YD_EQP_WRK_MODE
                              ,NVL(SUBSTR(CURR_STOP_LOC,2,1),WB.YD_BAY_GP) 
                                                            AS YD_CURR_BAY_GP --이동중이면 상차동을 현재동으로
                              ,SUBSTR(EQ.WAIT_STOP_LOC,2,1) AS YD_HOME_BAY_GP
                              ,WB.YD_WBOOK_ID               AS YD_WBOOK_ID_CURR   --현재 대차스케줄 상차작업예약ID
                              ,WB.YD_BAY_GP                 AS YD_BAY_GP_CURR     --현재 대차스케줄 상차동
                              ,WB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_CURR --현재 대차스케줄 하차동
                              ,XB.YD_WBOOK_ID               AS YD_WBOOK_ID_NEXT   --다음 상차작업예약ID
                              ,XB.YD_BAY_GP                 AS YD_BAY_GP_NEXT     --다음 상차동
                              ,XB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_NEXT --다음 하차동
                              ,(SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END
                                  FROM TB_YM_TCARFTMVMTL TM
                                 WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
                                   AND TM.DEL_YN = 'N')     AS TC_MTL_YN
                        --      ,NVL(EQ.AUTO_TCAR_SCH_YN,'N') AS AUTO_TCAR_SCH_YN   --자동대차스케줄여부
                                ,CURR_STOP_LOC
                                ,EQ.WAIT_STOP_LOC
                                --,EQ.CARLOAD_STOP_LOC
                                --,EQ.CARUNLOAD_STOP_LOC
                                ,EQ.PALLET_NO
                                ,EQ.YD_TCAR_WRK_ABLE_BAY6
                                ,NVL(( SELECT ITEM
                                     FROM USRYMA.TB_YM_RULE RL 
                                    WHERE RL.REPR_CD_GP = 'TCAR01'
                                      AND RL.CD_GP    = '2'
                                      AND RL.DEL_YN   = 'N'
                                      AND RL.DTL_ITM1 = :V_YD_EQP_ID
                                      AND RL.DTL_ITM2 = WB.YD_BAY_GP ),EQ.CARLOAD_STOP_LOC) AS CARLOAD_STOP_LOC --작업예약 기준 상차지 정지 위치 
                                ,NVL(( SELECT ITEM
                                     FROM USRYMA.TB_YM_RULE RL 
                                    WHERE RL.REPR_CD_GP = 'TCAR01'
                                          AND RL.CD_GP    = '2'
                                          AND RL.DEL_YN   = 'N'
                                          AND RL.DTL_ITM1 = :V_YD_EQP_ID
                                          AND RL.DTL_ITM2 = WB.YD_AIM_BAY_GP ),CARUNLOAD_STOP_LOC) AS CARUNLOAD_STOP_LOC --작업예약 기준 상차지 정지 위치             
                          FROM TB_YM_EQUIP   EQ
                              ,TB_YM_TCARSCH TS
                              ,TB_YM_WRKBOOK WB
                              ,(SELECT MIN(YD_WBOOK_ID  ) AS YD_WBOOK_ID
                                      ,MIN(YD_BAY_GP    ) AS YD_BAY_GP
                                      ,MIN(YD_AIM_BAY_GP) AS YD_AIM_BAY_GP
                                  FROM (SELECT YD_WBOOK_ID
                                              ,YD_BAY_GP
                                              ,YD_AIM_BAY_GP
                                          FROM TB_YM_WRKBOOK
                                         WHERE YD_WRK_PLAN_TCAR = :V_YD_EQP_ID
                                           AND YD_WBOOK_ID NOT IN
                                              (SELECT NVL(YD_CARLD_WRK_BOOK_ID,YD_CARUD_WRK_BOOK_ID) AS YD_WBOOK_ID
                                                 FROM TB_YM_TCARSCH
                                                WHERE DEL_YN = 'N'
                                                  AND (YD_CARLD_WRK_BOOK_ID IS NOT NULL	OR YD_CARUD_WRK_BOOK_ID IS NOT NULL))
                        --                   AND YD_SCH_CD LIKE '__TC__U%'
                                           AND ((SUBSTR(YD_SCH_CD,1,2) <> (NVL(SUBSTR(YD_TO_LOC_GUIDE,1,2),SUBSTR(YD_SCH_CD,1,2))))
                                                 OR 
                                                (YD_SCH_CD LIKE SUBSTR(YD_SCH_CD,1,2)|| 'TC__U%')
                                                 OR
                                                (YD_SCH_CD LIKE SUBSTR(YD_SCH_CD,1,2)|| 'PT__L%' )  --대차상차 스케줄에 이송하차추가                        
                                               )
                                           AND DEL_YN = 'N'
                                         ORDER BY YD_WBOOK_ID)
                                 WHERE ROWNUM = 1) XB
                         WHERE EQ.EQUIP_GP             = TS.YD_EQP_ID(+)
                           AND 'N'                     = TS.DEL_YN(+)
                           AND TS.YD_CARLD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
                           AND 'N'                     = WB.DEL_YN(+)
                           AND EQ.EQUIP_GP             = :V_YD_EQP_ID
						 */	   
						JDTORecordSet jsTcarNext = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWoSlab", logId, methodNm, "대차우선순위조회");
						
						String ydTcarCurrWbookId = commUtils.trim(jsTcarNext.getRecord(0).getFieldString("YD_WBOOK_ID_CURR"));
						String ydTcarNextWbookId = commUtils.trim(jsTcarNext.getRecord(0).getFieldString("YD_WBOOK_ID_NEXT"));
				        // 공대차 도착시 현재 대차작업이 없을 경우 해당동 작업 지시가 있는 경우 
                        // - 대차 스케줄 상태변경 
						// - 크레인 스케쥴 기동
			            if((ydTcarNextWbookId.length() > 0 ) && (ydTcarCurrWbookId.equals(""))) {

			            	ydWbookId = ydTcarNextWbookId;
			            	crnSchYn = "Y";   // 스케쥴 기동 처리
			    			
			            	JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
			    			jrParam1.setResultCode(logId);	
			    			jrParam1.setResultMsg(methodNm);	
			    			jrParam1.setField("MODIFIER"       		, modifier    ); 	//수정자
			    			jrParam1.setField("YD_CAR_PROG_STAT"	, "2"); 			//야드차량진행상태
			    			jrParam1.setField("YD_CARLD_WRK_BOOK_ID", ydWbookId); 		//상차 작업 예약
							jrParam1.setField("YD_TCAR_SCH_ID"   	, ydTcarSchId);   	//대차SCH
							
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarProgStatCarldWrkbook /
							UPDATE TB_YM_TCARSCH
							   SET MODIFIER             = :V_MODIFIER
							     , MOD_DDTT             = SYSDATE
							     , YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT --야드차량진행상태
							     , YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
							 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
							 */	 
							commDao.update(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarProgStatCarldWrkbook", logId, methodNm, "대차스케줄상태 수정");
			            }
					}

					//공대차도착이고 적치Bed 비활성화 이면 활성화
					if (!"L".equals(ydStkBedActStatLd)) {
						//현위치 적치Bed 활성화 처리
						jrParam.setField("STACK_COL_GP"         , ydStkColGp); //야드적치열구분(현위치)
						jrParam.setField("STACK_BED_ACTIVE_STAT", "L"       ); //야드적치Bed활성상태(활성)
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActByCol
						UPDATE TB_YM_STACKER
						   SET MODIFIER              = :V_MODIFIER
						      ,MOD_DDTT              = SYSDATE
						      ,STACK_BED_ACTIVE_STAT = :V_STACK_BED_ACTIVE_STAT
						 WHERE STACK_COL_GP          = :V_STACK_COL_GP
						   AND DEL_YN                = 'N'
						*/	   
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActByCol", logId, methodNm, "도착위치 Bed 활성화");
						if ("N".equals(crnSchYn) && !("R".equals(RollBackTag))) {
							
							
							//적치단 수정
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrActiveTC2
							UPDATE TB_YM_STACKLAYER
							   SET MODIFIER                = :V_MODIFIER
							      ,MOD_DDTT                = SYSDATE
							      ,STOCK_ID                = NULL
							      ,STACK_LAYER_ACTIVE_STAT = 'E'
							      ,STACK_LAYER_STAT        = 'E'
							 WHERE STACK_COL_GP            = :V_STACK_COL_GP
							   AND SUBSTR(STACK_COL_GP,1,1)= '2'
							   AND SUBSTR(STACK_COL_GP,3,2)= 'TC'
							   AND DEL_YN                  = 'N'
							*/	   
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrActiveTC2", logId, methodNm, "도착위치 단 활성화");
							
						 }else{
							 RollBackTag ="";
							 
								//적치단 수정 선처리 된  스케줄(공대차 상차도착전  스케줄 생성된 건 제외)
								/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrActiveTC3
								UPDATE TB_YM_STACKLAYER
								   SET MODIFIER                = :V_MODIFIER
								      ,MOD_DDTT                = SYSDATE
								      ,STOCK_ID                = NULL
								      ,STACK_LAYER_ACTIVE_STAT = 'E'
								      ,STACK_LAYER_STAT        = 'E'
								 WHERE STACK_COL_GP            = :V_STACK_COL_GP
								   AND SUBSTR(STACK_COL_GP,1,1)= '2'
								   AND SUBSTR(STACK_COL_GP,3,2)= 'TC'
								   AND STACK_LAYER_STAT        != 'D'  --대차권하대기
								   AND DEL_YN                  = 'N'
								*/	   
								commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrActiveTC3", logId, methodNm, "도착위치 단 제한적 활성화");
								
								
								//적치단  레이어 STACK_LAYER_ACTIVE_STAT CLEAR
								/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrActiveTC4
								UPDATE TB_YM_STACKLAYER
								   SET MODIFIER                = :V_MODIFIER
								      ,MOD_DDTT                = SYSDATE
								      ,STACK_LAYER_ACTIVE_STAT = 'E'
								 WHERE STACK_COL_GP            = :V_STACK_COL_GP
								   AND SUBSTR(STACK_COL_GP,1,1)= '2'
								   AND SUBSTR(STACK_COL_GP,3,2)= 'TC'
								   AND DEL_YN                  = 'N'
								*/	   
								commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrActiveTC4", logId, methodNm, "도착위치 단 활성상태 활성화");
								
								
						 }

					}
				}

				/**********************************************************
				* 5. 야드저장위치제원(YMA8L001) 전문 조회
				**********************************************************/
				jrParam.setField("YD_INFO_SYNC_CD", "4"       ); //야드정보동기화코드(Bed)
				jrParam.setField("STACK_COL_GP"   , ydStkColGp); //야드적치열구분(현재동)
				jrParam.setField("STACK_BED_GP"   , "01"       ); //야드적치Bed

				//전송Data 조회
				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L001", jrParam));

				/**********************************************************
				* 6. 크레인스케줄 전송
				**********************************************************/
				commUtils.printLog(logId, " 크레인스케줄기동여부:" + crnSchYn + ", 크레인스케줄기동 wb:" + ydWbookId , "SL");
				
				//크레인스케줄기동여부 'Y'이고 작업예약ID가 있으면 크레인스케줄 전송
				if ("Y".equals(crnSchYn) && !"".equals(ydWbookId)) {
					
					if ((ydSchReqGp.equals("6"))) { //공대차 상차도착

						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name

						jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId ); //야드작업예약ID
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTcarWbookid 
						SELECT YD_WBOOK_ID FROM
						    (
						    SELECT CASE WHEN A.YD_WBOOK_ID = D.YD_WBOOK_ID THEN '1'
						                ELSE '2' END AS SEQ
						         , A.YD_WBOOK_ID
						         , A.YD_WRK_PLAN_TCAR
						         , C.STACK_LAYER_GP
						      FROM TB_YM_WRKBOOK A
						         , TB_YM_WRKBOOKMTL B   
						         , TB_YM_STACKLAYER C
						         , (SELECT * FROM TB_YM_WRKBOOK
						             WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID) D
						     WHERE A.YD_WBOOK_ID   = B.YD_WBOOK_ID  
						       AND B.STOCK_ID      = C.STOCK_ID  
						       AND A.YD_SCH_CD     = D.YD_SCH_CD  
						       AND A.YD_AIM_BAY_GP = D.YD_AIM_BAY_GP
						       AND C.STACK_LAYER_STAT = 'C' 
						       AND A.DEL_YN = 'N'
						       AND B.DEL_YN = 'N'
						     ORDER BY CASE WHEN A.YD_WBOOK_ID = D.YD_WBOOK_ID THEN '1'
						                ELSE '2' END 
						         , C.STACK_LAYER_GP DESC
						         , YD_WBOOK_ID   
						    ) A
						 WHERE ROWNUM <= ((SELECT STACK_MAX_QNTY FROM TB_YM_EQUIP WHERE EQUIP_GP = A.YD_WRK_PLAN_TCAR ) 
						                   -
						                  (SELECT COUNT(*) 
						                     FROM TB_YM_CRNSCH 
						                    WHERE YD_SCH_CD = A.YD_SCH_CD
						                      AND DEL_NO = 'N')  --현재기동된 SCH 갯수
						                  )  
						*/
					JDTORecordSet jsTcsch = commDao.select(jrYdMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTcarWbookid", logId, methodNm, "크레인스케줄 조회");   		
					JDTORecord jrTcsch = JDTORecordFactory.getInstance().create();
	    			JDTORecord jsMsg = JDTORecordFactory.getInstance().create();
	    			jsMsg.setResultCode(logId);	//Log ID
	    			jsMsg.setResultMsg(methodNm);	//Log Method Name
    			
		     
		              /* String ydSchCd = "";
					if ("1".equals(ydStkColGp.substring(4, 5))) {
						ydSchCd = ydStkColGp.substring(0, 2) + "TC11UM";
					} else if ("2".equals(ydStkColGp.substring(4, 5))) {
						ydSchCd = ydStkColGp.substring(0, 2) + "TC22UM";
					} else {
						ydSchCd = ydStkColGp.substring(0, 2) + "TC11UM";
					}*/
							
		    		//작업예약이 선작업으로 이미 스케줄 기동 된경우  다음 작업예약은 스케줄 기동하지 않음
					//jrYdMsg.setField("YD_SCH_CD"  , CarldSchCd); //  상차지스케줄
					jrYdMsg.setField("YD_EQP_ID"  , ydEqpId); //  대차번호

					//해당대차에 걸려있는 상차 스케줄이 있는지 판단
					/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTcarWbookid1                  
					 SELECT COUNT(*) AS CNT
					 FROM TB_YM_CRNSCH  A
					     ,TB_YM_WRKBOOK B 
					WHERE 1 = 1
					  AND A.YD_WBOOK_ID = B.YD_WBOOK_ID
					  AND B.YD_WRK_PLAN_TCAR = :V_YD_EQP_ID
					  AND A.DEL_YN = 'N'
					  AND B.DEL_YN = 'N'
					  AND A.YD_SCH_CD LIKE  '2_TC__UM%'
					  */
			            JDTORecordSet jsChk1 = commDao.select(jrYdMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTcarWbookid1", logId, methodNm, "설비상태조회");
						
						String CNT = "";
		
						if (jsChk1.size() > 0) {
							 CNT = commUtils.trim(jsChk1.getRecord(0).getFieldString("CNT"));
						}
			    			
			    			
			    			
			    		if(("0".equals(CNT)))	{
						    		for(int Loop_i = 1; Loop_i <= 1; Loop_i++) {
						    			jsTcsch.absolute(Loop_i);
						    			jrTcsch = JDTORecordFactory.getInstance().create();
						    			jrTcsch.setRecord(jsTcsch.getRecord());
						    			
						    			//크레인 스케줄 기동 YMYMJ202 호출
						    			jsMsg.setResultCode(logId);	//Log ID
						    			jsMsg.setResultMsg(methodNm);	//Log Method Name
						    			jsMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
						    			jsMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
						    			jsMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
						    			jsMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
						    			jsMsg.setField("YD_WBOOK_ID", jrTcsch.getFieldString("YD_WBOOK_ID")); //야드작업예약ID
									}	
					    		//	jsMsg.setField("SCH_CNT"				, ""+jsTcsch.size()); 
						    		jrRtn = commUtils.addSndData(jrRtn, jsMsg);			
			    		  }		
		    		   
					} else if ((ydSchReqGp.equals("3"))) {  //영대차도착 : 하차도착(B)

						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
						
	                     /* String ydSchCd = "";
						if ("1".equals(ydStkColGp.substring(4, 5))) {
							ydSchCd = ydStkColGp.substring(0, 2) + "TC11";
						} else if ("2".equals(ydStkColGp.substring(4, 5))) {
							ydSchCd = ydStkColGp.substring(0, 2) + "TC22";
						} else {
							ydSchCd = ydStkColGp.substring(0, 2) + "TC11";
						}*/
						
						jrYdMsg.setField("YD_SCH_CD"         , SetEqCarudSchCd); //  하차지스케줄
						if("".equals(NextTc))
						jrYdMsg.setField("YD_WRK_PLAN_TCAR"  , ydEqpId);         //  계획대차
						else 
							jrYdMsg.setField("YD_WRK_PLAN_TCAR"  , NextTc);         //  계획대차
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTcarWbookidLDSlab 
						SELECT YD_WBOOK_ID
						  FROM TB_YM_WRKBOOK
						 WHERE DEL_YN = 'N'
						      AND YD_SCH_CD = :V_YD_SCH_CD
						   AND YD_WBOOK_ID NOT IN ( SELECT YD_WBOOK_ID FROM TB_YM_CRNSCH WHERE DEL_YN = 'N')
						*/
						JDTORecordSet jsTcsch = commDao.select(jrYdMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTcarWbookidLDSlab", logId, methodNm, "크레인스케줄 조회");   		
						JDTORecord jrTcsch = JDTORecordFactory.getInstance().create();
		    			JDTORecord jsMsg = JDTORecordFactory.getInstance().create();
			    		for(int Loop_i = 1; Loop_i <= jsTcsch.size(); Loop_i++) {
			    			jsTcsch.absolute(Loop_i);
			    			jrTcsch = JDTORecordFactory.getInstance().create();
			    			jrTcsch.setRecord(jsTcsch.getRecord());
			    			//크레인 스케줄 기동 YMYMJ203 호출
			    			
			    			//jsMsg.setField("YD_WBOOK_ID"+(Loop_i),jrTcsch.getFieldString("YD_WBOOK_ID"));
							//jsMsg.setField("SCH_CNT"             , Integer.toString(Loop_i));
							
			    			//jsMsg.setField("JMS_TC_CD"			 , "YMYMJ203"); 
			    			jsMsg.setResultCode(logId);	//Log ID
			    			jsMsg.setResultMsg(methodNm);	//Log Method Name
			    			jsMsg.setField("JMS_TC_CD"			 , "YMYMJ202"); 
			    			jsMsg.setField("JMS_TC_CREATE_DDTT"	 , commUtils.getDateTime14()); //JMSTC생성일시				
			    			jsMsg.setField("YD_SCH_CD"  		 , ""); //야드스케쥴코드
			    			jsMsg.setField("YD_EQP_ID"  		 , ""); //야드설비ID
			    			jsMsg.setField("YD_WBOOK_ID"	    , jrTcsch.getFieldString("YD_WBOOK_ID")); //야드작업예약ID
			    			
							//크레인스케줄기동 전문
							EJBConnector sndConn = new EJBConnector("default", "BSlabSchSeEJB", this);
							JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("procYMYMJ202", new Class[] { JDTORecord.class }, new Object[] { jsMsg });
							jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
			    			
						}	

		    			
		    		//	if(jsTcsch.size() > 0 ) {
			    	//	  jrRtn = commUtils.addSndData(jrRtn, jsMsg);
		    		//	}	
					} else {
                 	   //JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
		
 				
						JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
						
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
						jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId       ); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_ST_GP" , "A"             ); //야드스케쥴기동구분(Auto)
						jrYdMsg.setField("YD_SCH_REQ_GP", ydSchReqGp      ); //야드스케쥴요청구분(영대차하차출발) 2

						//영대차 하차출발 일경우 하차지 정보와  다음 작업대상의 상차지 정보가 같으면  다음스케줄 구동하지 않고 하차지 마지막 권상후 크레인 스케줄기동
						if ((ydSchReqGp.equals("2")) && ydCarudStopLoc.equals(ydCarloadStopLoc)) {
							jrParam.setField("YD_TCAR_WRK_ABLE_BAY6", "X"   ); //
							// 영대차 하차출발 일경우 하차지 정보와  다음 작업대상의 상차지 정보가 같으면  다음스케줄 구동하지 않고 마지막 하차지권상 후 사용하기 위함 대체필드 사용
							// 마지막 하차지 하차권상후 크리어 하여야함
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdEqpCurrBay79 
								UPDATE TB_YM_EQUIP
								   SET MODIFIER                = :V_MODIFIER
								      ,MOD_DDTT                = SYSDATE
								      ,YD_TCAR_WRK_ABLE_BAY6   = :V_YD_TCAR_WRK_ABLE_BAY6
								WHERE EQUIP_GP                 = :V_YD_EQP_ID 
							*/ 
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdEqpCurrBay79", logId, methodNm, "하차지 라스트권상 후 스케줄기동을위함");							
							
						} else jrRtn = commUtils.addSndData(jrRtn, ymComm.getCrnSchMsg(jrYdMsg));

						
						
					}	
				}	
			}	


			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}		
	
	
	
	
	
	
	} // end of 대차이동실적(A8YML011)	
	
	/**
	 *      [A] 오퍼레이션명 : 작업현황요구(A8YML013)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML013(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "작업현황요구[BSlabL2RcvSeEJB.rcvA8YML013] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = null;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId     	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     //설비ID
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID 이상 [" + ydEqpId + "]");
			}


			/**********************************************************
			* 2. 작업현황응답(YMA8L007)
			**********************************************************/
			//크레인스케줄기동여부 'Y'이고 작업예약ID가 있으면 크레인스케줄 전송
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);	//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name

			sndL2Msg.setField("YD_EQP_ID"   , ydEqpId);  					//이동구분 
			//작업현황응답정보 송신
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L007", sndL2Msg));
								
	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 작업현황요구(A8YML013)
	
	
	/**
	 *      [A] 오퍼레이션명 : 스케줄작업요구(A8YML014)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML014(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "스케줄작업요구[BSlabL2RcvSeEJB.rcvA8YML014] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		JDTORecord jrRtn = null;	//전문 Return
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
//TODO
			//수신 항목 값ydEqpId
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId     	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     //설비ID
//			String ydSchCd     	= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));     //SCHEDULE 코드
//			String ydCrnSchId  	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); //SCHEDULEID
			String ydSchFlag   	= commUtils.trim(rcvMsg.getFieldString("YD_SCH_FLAG"));   //요구스케쥴구분
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)

			String ydNewWbookId 	= "";
			String ydNewSchCd 		= "";
			String ydNewSchPrior	= "";
			String ydNewCrnSchId 	= "";
			
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

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
			//크레인스케줄기동여부 'Y'이고 작업예약ID가 있으면 크레인스케줄 전송
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_EQP_ID"     , ydEqpId);  					
			jrParam.setField("YD_SCH_FLAG"   , ydSchFlag);  					
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchRequest1 
			SELECT *
			 FROM (
			        SELECT (CASE WHEN A.YD_SCH_CD LIKE '2_PT02UM'    THEN 'A'--이송상차       
			                     WHEN A.YD_SCH_CD LIKE '2ESE01UM'    THEN 'B'--스카핑보급
			                     
			                     WHEN A.YD_SCH_CD LIKE '2_WB01UM'    THEN 'D'--W/B 보급                            
			                     WHEN A.YD_SCH_CD LIKE '2_PT02_M'    THEN 'E'--이송하차       
			                     WHEN A.YD_SCH_CD LIKE '2ESE01LM'    THEN 'F'--스카핑추출
			                     
			                     WHEN A.YD_SCH_CD LIKE '2_YD__MM'    THEN 'H'--동내이적(1)    
			                     WHEN A.YD_SCH_CD LIKE '2_CT01UM'    THEN 'I'--CTC 보급       
			                     -- J절단장 보급
			                     WHEN A.YD_SCH_CD LIKE '2_HB01LM'    THEN 'L'--H/B LineOff    
			                     --M 절단장 추출
			                     --N 트레일러하차
			                     --O ET CAR 하차
			                     WHEN A.YD_SCH_CD LIKE '2_PT02_M'    THEN 'N'--이송상하차
			                     WHEN SUBSTR(YD_UP_WO_LOC, 3, 3) = 'TC1' THEN 'C'--대차하차(1)    
			                     WHEN SUBSTR(YD_DN_WO_LOC, 3, 3) = 'TC1' THEN 'C'--대차하차(1)    
			                     WHEN SUBSTR(YD_UP_WO_LOC, 3, 3) = 'TC2' THEN 'G'--대차하차(2)    
			                     WHEN SUBSTR(YD_DN_WO_LOC, 3, 3) = 'TC2' THEN 'G'--대차하차(2)    
			                     WHEN SUBSTR(YD_UP_WO_LOC, 3, 3) = 'TC3' THEN 'K'--대차하차(3)    
			                     WHEN SUBSTR(YD_DN_WO_LOC, 3, 3) = 'TC3' THEN 'K'--대차하차(4)    
			--                     WHEN A.YD_SCH_CD LIKE '2_HB02UM'    THEN 'L'--STE 비상보급     
			                 END) AS YD_SCH_FLAG
			             , B.YD_WRK_CRN 
			             , B.YD_ALT_CRN
			             , A.YD_SCH_CD
			             , A.YD_WBOOK_ID
			             , '0' AS YD_SCH_PRIOR
			             , A.YD_CRN_SCH_ID
			          FROM TB_YM_CRNSCH       A
			             , TB_YM_SCHEDULERULE B
			             , TB_YM_EQUIP        C
			         WHERE A.YD_SCH_CD  = B.YD_SCH_CD
			           AND B.YD_WRK_CRN = C.EQUIP_GP
			           AND A.DEL_YN='N'
			           AND (CASE C.WPROG_STAT WHEN 'B' THEN B.YD_ALT_CRN ELSE A.YD_EQP_ID END) = :V_YD_EQP_ID
			           AND A.YD_GP = '2'
			           AND '1' = CASE WHEN (SELECT COUNT(*)
			                                  FROM TB_YM_CRNSCH A1
			                                     , TB_YM_CRNWRKMTL B1
			                                     , TB_YM_STACKLAYER C1
			                                 WHERE A1.YD_CRN_SCH_ID  = B1.YD_CRN_SCH_ID
			                                   AND B1.STOCK_ID       = C1.STOCK_ID
			                                   AND C1.STACK_COL_GP||C1.STACK_BED_GP = A1.YD_UP_WO_LOC
			                                   AND A1.DEL_YN = 'N'
			                                   AND B1.DEL_YN = 'N'
			                                   AND C1.STACK_LAYER_GP >=
			                                       (
			                                        SELECT NVL(MAX(C2.STACK_LAYER_GP) ,'01')
			                                          FROM TB_YM_CRNSCH A2
			                                             , TB_YM_CRNWRKMTL B2
			                                             , TB_YM_STACKLAYER C2
			                                         WHERE A2.YD_CRN_SCH_ID  = B2.YD_CRN_SCH_ID
			                                           AND B2.STOCK_ID       = C2.STOCK_ID
			                                           AND C2.STACK_COL_GP||C2.STACK_BED_GP = A.YD_UP_WO_LOC
			                                       ) 
			                                   AND A1.YD_DN_WO_LOC = 'XX010101'
			                                ) > 0 
			                          THEN '2'           
			                          ELSE '1' END      
			       ) A
			 WHERE YD_SCH_FLAG = :V_YD_SCH_FLAG
			 ORDER BY YD_SCH_PRIOR, YD_WBOOK_ID 
			*/
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchRequest1", logId, methodNm, "요구정보 조회");
			if (jsCrnSch.size() == 0) {
				//throw new Exception("요구에 해당하는 정보 없음[" + ydEqpId + "]");
				resMsg.setField("YD_EQP_ID", ydEqpId);
				resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , "요구에 해당하는 정보 없음"); //야드L3MESSAGE
				resMsg.setField("YD_CRN_SCH_ID" , ""     ); 
				
				jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
				
				commUtils.printLog(logId, methodNm, "S-");

				return jrRtn;
				
			} else {
				// 요구스케쥴구분
				ydNewCrnSchId 	= jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID");  // 신규 작업
				ydNewWbookId 	= jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID");    // 신규 작업
				ydNewSchCd 		= jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD");      // 신규 작업
				ydNewSchPrior	= "1";   // 신규 작업
			}


			jrParam.setField("YD_SCH_CD"     , ydNewSchCd);	
			jrParam.setField("YD_WBOOK_ID"   , ydNewWbookId);	
			jrParam.setField("YD_SCH_PRIOR"  , ydNewSchPrior);	
			jrParam.setField("YD_CRN_SCH_ID" , ydNewCrnSchId);
			jrParam.setField("MODIFIER"      , modifier);	
			
			//기존 크레인 작업 조회
			// 기존작업지시
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnWrkMgtPriorWrk1 
			SELECT YD_CRN_SCH_ID 
			  FROM TB_YM_CRNSCH
			 WHERE YD_CRN_SCH_ID IN (
			        SELECT YD_CRN_SCH_ID
			          FROM (
			                SELECT YD_CRN_SCH_ID
			                     , COUNT(*)  AS CRN_WRK_CNT
			                  FROM TB_YM_CRNSCH
			                 WHERE YD_EQP_ID = :V_YD_EQP_ID
			                   AND YD_WRK_PROG_STAT IN ('1', 'S')
			                   AND DEL_YN = 'N' 
			                 GROUP BY YD_CRN_SCH_ID  
			                )
			         WHERE CRN_WRK_CNT = 1
			         )
			 AND YD_DN_WO_LOC <> 'XX010101'             
			 */
			JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnWrkMgtPriorWrk1", logId, methodNm, "기존크레인 작업 조회");
			if (jsCrn == null || jsCrn.size() <= 0) {
				/**********************************************************
				* 3.1 기존 작업이 없음  신규 작업만 하면 됨  
				**********************************************************/
			
			} else {
				
				// 기존 작업 
			    JDTORecord jrCrn = jsCrn.getRecord(0);
			    String ydCrnSchIdWrk   = commUtils.trim(jrCrn.getFieldString("YD_CRN_SCH_ID"   ));
			    
				/**********************************************************
				* 3.1 기존 작업 정리 
				* 3.2 신규 작업 처리 함  
				**********************************************************/
				
			    /**** 기존 작업 지시 정리 ***********/
				//크레인스케줄 Table 크레인ID, 우선순위 Update, 
			    /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtPriorWrkNext1 
			    UPDATE TB_YM_CRNSCH A
			       SET MODIFIER         = :V_MODIFIER
			         , MOD_DDTT         = SYSDATE
			         , YD_WRK_PROG_STAT = 'W'
			    	 , YD_WORD_DT       = NULL 
			    	 , YD_SCH_PRIOR     = (SELECT YD_WRK_CRN_PRIOR FROM USRYMA.TB_YM_SCHEDULERULE B
				                            WHERE B.YD_SCH_CD=A.YD_SCH_CD)
			     WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
			       AND DEL_YN = 'N'       
			     */   
			    jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchIdWrk);
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtPriorWrkNext1", logId, methodNm,  "TB_YM_CRNSCH");						    
				
			
				
				//신규 작업예약 Table 우선순위 Update
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updWrkBookPrior1 
		    	--작업예약 스케쥴우선순위 수정
		    	UPDATE TB_YM_WRKBOOK
		    	   SET MODIFIER     = :V_MODIFIER
		    	     , MOD_DDTT     = SYSDATE
		    	     , YD_SCH_PRIOR = NVL(TO_NUMBER(:V_YD_SCH_PRIOR),1)
		    	 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
		    	   AND DEL_YN       = 'N'
	
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updWrkBookPrior1", logId, methodNm, "TB_YM_WRKBOOK");				
	
				//신규 작업 우선순위 변경
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt1 
				UPDATE TB_YM_CRNSCH
				   SET MODIFIER     = :V_MODIFIER
				      ,MOD_DDTT     = SYSDATE
				      ,YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
				      ,YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
				      ,YD_WRK_PROG_STAT = 'S' 
				 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
				   AND YD_WRK_PROG_STAT IN ('1','W','S')
				   AND DEL_YN = 'N'
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt1", logId, methodNm,  "TB_YM_CRNSCH");
				
				/**********************************************************
				* 3.2 신  크레인작업지시 요구 처리
				**********************************************************/
	
				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
	
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
				jrYdMsg.setField("JMS_TC_CD"       , YmConstant.YMA8L004);	//크레인작업지시요구
				jrYdMsg.setField("MSG_GP"          , "I");	//야드설비ID
				jrYdMsg.setField("YD_CRN_SCH_ID"   , ydNewCrnSchId);	//야드크레인스케쥴ID
	
	//SJH			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L004", jrYdMsg));			
	    		JDTORecordSet jsRtn1 = commDao.getMsgL2("YMA8L004", jrYdMsg);
				jrRtn = commUtils.addSndData(jrRtn , jsRtn1);
				
				//크레인작업지시 Z값 갱신 처리
	    		if(jsRtn1.size() > 0 ) {
	    			jrYdMsg.setField("YD_UP_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_UP_WO_LOC_ZAXIS"));	
	    			jrYdMsg.setField("YD_DN_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_DN_WO_LOC_ZAXIS"));	
	    			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS 
					--크레인스케줄 z값 갱신
					UPDATE TB_YM_CRNSCH
					   SET YD_UP_WO_LOC_ZAXIS = :V_YD_UP_WO_LOC_ZAXIS
					     , YD_DN_WO_LOC_ZAXIS = :V_YD_DN_WO_LOC_ZAXIS
					 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
					   AND DEL_YN           = 'N' 
					*/
	        		commDao.update(jrYdMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS", logId, methodNm, "SLAB크레인스케줄 Z값 수정");
	//    			this.procCrnsSchZaxis(jrYdMsg);
	    		}			
			}
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 스케줄작업요구(A8YML014)
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업가능응답(A8YML015)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML015(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업가능응답[BSlabL2RcvSeEJB.rcvA8YML015] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값ydEqpId
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId     	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     		//설비ID
			String ydWrkProgStat= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"));    //야드작업진행상태(5:권하위치변경)만 올라옴
			String ydSchCd   	= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));    		//야드스케쥴코드
			String ydCrnSchId  	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); 		//야드크레인스케쥴ID
			String reqYn  		= commUtils.trim(rcvMsg.getFieldString("REQ_YN")); 				//유무응답
			String ReqMsg  		= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_REQ_MSG")); //메시지
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); 		//수정자(Backup Only)
			String headMsgGp    = commUtils.trim(rcvMsg.getFieldString("MSG_GP"     )); 		//head msg구분
			String ydChgStlNo 		= "";
			String ydChgDnWoLoc	    = ""; 
			String ydChgDnWoLayer	= "";
			String ydBefDnWoLoc		= "";
			String ydBefDnWoLayer	= "";
			String ydWbookId 		= "";
			String ydWrkProgStst	= "";	
			String ydUpWrLoc	    = "";	
			String ydL2RequestStat	= "";	
			String sYD_SCH_PRIOR    = ""; //스케줄 우선순위
			String autoYn           = "N";
			String ydToLocDcsnMtd   = "";// YD_TO_LOC_DCSN_MTD
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID 이상 [" + ydEqpId + "]");
			}
			/**********************************************************
			* 1. 수신 항목 값 Check
			*   무인 크레인이 이면 
			**********************************************************/
			if (ymComm.chkAutoCrn(ydEqpId) ){
				autoYn = "Y";
			} else {
				autoYn = "N";
			}
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"                 , modifier);	
			jrParam.setField("YD_EQP_ID"         		, ydEqpId);  					
			jrParam.setField("YD_CRN_SCH_ID"      		, ydCrnSchId);  					
			jrParam.setField("YD_WRK_PROG_STAT"    		, ydWrkProgStat);  					
			jrParam.setField("YD_WRK_PROG_REQ_MSG"   	, ReqMsg);  					

			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCrnSchLocLog 
			SELECT YD_CRN_SCH_ID
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
			  FROM TB_YM_CRNSCH
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			*/   
			
			
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCrnSchLocLog", logId, methodNm, "대상작업 조회");
			if (jsCrnSch.size() == 0) {
				commUtils.printLog("", "○○○크레인 스케쥴 번호가 없습니다..! [" + ydCrnSchId + "]", "[info]");
				return jrRtn;
			} else {
				ydCrnSchId 		= jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID");   
				ydWbookId 		= jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID");     
				ydSchCd 		= jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD");       
				ydWrkProgStst	= jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
				ydUpWrLoc	    = jsCrnSch.getRecord(0).getFieldString("YD_UP_WR_LOC");    //권상위치
				
				ydChgStlNo   	= jsCrnSch.getRecord(0).getFieldString("STL_NO_TEMP");     //변경할 재료번호
				ydChgDnWoLoc	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC_TO"); //변경후저장위치szStkPos
				ydChgDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("STK_LYR_NO_TEMP"); //변경후저장위치szStkLyrNo
				ydBefDnWoLoc 	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC");    //변경전저장위치szOldStkPos
				ydBefDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LAYER");  //변경전저장위치szOldStkLyrNo
				ydL2RequestStat	= jsCrnSch.getRecord(0).getFieldString("YD_L2_REQUEST_STAT");  //야드L2요구상태
				sYD_SCH_PRIOR   = jsCrnSch.getRecord(0).getFieldString("YD_SCH_PRIOR");    //스케줄우선순위
				ydToLocDcsnMtd  = jsCrnSch.getRecord(0).getFieldString("YD_TO_LOC_DCSN_MTD");//주 보조 
			} 
				
			if (("1".equals(ydWrkProgStat) || "5".equals(ydWrkProgStat)) && ydL2RequestStat.equals(YmConstant.YD_L2_REQUEST_STAT_5)) {
				/**********************************************************
				* 2. 권하위치 변경 요청 결과
				**********************************************************/		
				// 응답전문 N 일때(작업 불가메세지)
				if ("N".equals(reqYn)){
					/**********************************************************
					* 2.1 권하위치 변경  불가 일경우
					**********************************************************/		
					commUtils.printLog(logId, methodNm + "권하위치 변경 불가 일경우", "SL");
					//권하위치 변경 N응답시 메세지 update
				
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCrnSchProgStatMsgNo
					UPDATE TB_YM_CRNSCH  
					   SET YD_WRK_PROG_REQ_MSG = :V_YD_WRK_PROG_REQ_MSG
					 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH
					*/
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCrnSchProgStatMsgNo", logId, methodNm,  "TB_YM_CRNSCH");
					commUtils.printLog("", "○○○권하위치 변경 불가 [" + ydCrnSchId + "]", "[info]");
					return jrRtn;
					
				} else {

					/**********************************************************
					* 2.1 권하위치 변경 가능 일경우
					* 2.1 자동인 경우 에만  권하위치 변경처리 함
					**********************************************************/		
					if(autoYn.equals("N")) {
						return jrRtn;	
						
					} else {
						
						String ydChgStkColGp = ydChgDnWoLoc.substring(0, 6); 
						String ydChgStkBedNo = ydChgDnWoLoc.substring(6, 8);
						String ydBefStkColGp = ""; 
						String ydBefStkBedNo = "";
						if ( ydBefDnWoLoc.length() == 8 && (!ydBefDnWoLoc.equals("XX010101"))){	
							ydBefStkColGp = ydBefDnWoLoc.substring(0, 6); 
							ydBefStkBedNo = ydBefDnWoLoc.substring(6, 8);
						}
						
						commUtils.printLog(logId, methodNm + "전저장위치"+ ydBefDnWoLoc, "SL");
						
						/**********************************************************
						* 1. 적치 가능 여부 CHECK 
						**********************************************************/
						JDTORecord inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setResultCode(logId);	//Log ID
						inRecord.setResultMsg(methodNm);	//Log Method Name
						inRecord.setField("YD_CRN_SCH_ID"   , ydCrnSchId);  	
						inRecord.setField("STACK_COL_GP" 	, ydChgStkColGp);	
						inRecord.setField("STACK_BED_GP" 	, ydChgStkBedNo);	

						jsCrnSch = commDao.select(inRecord, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnSchDnWoLocCurLyr", logId, methodNm, "신규권하위치 조회");
						
						if (jsCrnSch.size() == 0) {
							commUtils.printLog(logId, methodNm + "저장위치 이상", "SL");
							throw new Exception("저장위치 이상.! [" + ydCrnSchId + "]");
						}
						
						JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
						
						ydChgDnWoLayer = commUtils.trim(jrCrnSch.getFieldString("STACK_LAYER_GP"));
						String sSTACK_LAYER_GP_SCH = commUtils.trim(jrCrnSch.getFieldString("STACK_LAYER_GP_SCH"));
						
						/**********************************************************
						* 1. 기존(전) 정보 수정
						*  기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다.
						**********************************************************/					
						//-----------------------------------------------------------------------
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setResultCode(logId);	//Log ID
						inRecord.setResultMsg(methodNm);	//Log Method Name
						inRecord.setField("MODIFIER"        , modifier);	
						inRecord.setField("STACK_COL_GP" 	, ydBefStkColGp);	
						inRecord.setField("STACK_BED_GP" 	, ydBefStkBedNo);	
						inRecord.setField("STACK_LAYER_GP" 	, ydBefDnWoLayer);	
						inRecord.setField("YD_CRN_SCH_ID" 	, ydCrnSchId);
						/*  
						SELECT STOCK_ID
						     , STACK_LAYER_STAT
						     , STACK_LAYER_ACTIVE_STAT
						     , STACK_LAYER_COMMENTS
						  FROM TB_YM_STACKLAYER
						 WHERE STACK_COL_GP   = :V_STACK_COL_GP
						   AND STACK_BED_GP   = :V_STACK_BED_GP 
						   AND STOCK_ID IN (SELECT STOCK_ID
						                      FROM TB_YM_CRNWRKMTL 
						                     WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						                       AND DEL_YN = 'N'
						                   )
						 ORDER BY STACK_LAYER_GP        
						*/   
						JDTORecordSet jsBefStkLay = commDao.select(inRecord, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStackLayerInfo", logId, methodNm, "신규 적재위치 조회");
						
						if ( ydBefDnWoLoc.length() == 8 && (!ydBefDnWoLoc.equals("XX010101"))){	
											
							if (jsBefStkLay.size() == 0) {
								commUtils.printLog(logId, methodNm + "저장위치 이상", "SL");
								return jrRtn;
							} else {
							
								// 기존 지시위치 에 쌓여 있는 정보 Clear
								inRecord.setField("STACK_LAYER_ACTIVE_STAT", "E");
								inRecord.setField("STACK_LAYER_STAT"       , "E");
						    	/*
								UPDATE TB_YM_STACKLAYER            
								   SET MOD_DDTT     = SYSDATE             
								     , MODIFIER     = :V_MODIFIER             
								     , STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT
								     , STOCK_ID                = NULL
								     , STACK_LAYER_STAT        = :V_STACK_LAYER_STAT
								 WHERE STACK_COL_GP   = :V_STACK_COL_GP
								   AND STACK_BED_GP   = :V_STACK_BED_GP
								   AND STOCK_ID IN (SELECT STOCK_ID
								                      FROM TB_YM_CRNWRKMTL 
								                     WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
								                       AND DEL_YN = 'N'
								                   )
						    	 */
								commDao.update(inRecord, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYdStkLyrYdStkColBedGp", logId, methodNm, "TB_YM_STACKLAYER 등록");
							}
						}	
			
						/**********************************************************
						* 3. 신규 위치 SET
						**********************************************************/					
						// 신규위치에 정보를 Setting
						inRecord = JDTORecordFactory.getInstance().create();
						int nSTACK_LAYER_GP = Integer.parseInt(ydChgDnWoLayer);
						JDTORecord jrBefStkLay = JDTORecordFactory.getInstance().create();
						
						for (int i = 1; i <= jsBefStkLay.size(); ++i) {
							jsBefStkLay.absolute(i);
							jrBefStkLay = jsBefStkLay.getRecord();
							inRecord.setField("MODIFIER"        		, modifier);	
							inRecord.setField("STACK_COL_GP" 			, ydChgStkColGp);	
							inRecord.setField("STACK_BED_GP" 			, ydChgStkBedNo);
							if (nSTACK_LAYER_GP < 10) {
								inRecord.setField("STACK_LAYER_GP" 		, "0"+nSTACK_LAYER_GP);
							} else {
								inRecord.setField("STACK_LAYER_GP" 		, "" +nSTACK_LAYER_GP);
							}
							inRecord.setField("STACK_LAYER_ACTIVE_STAT"	, "E");
							inRecord.setField("STACK_LAYER_STAT"		, "D");
							inRecord.setField("STOCK_ID"		, commUtils.trim(jrBefStkLay.getFieldString("STOCK_ID")));
					    	/*
							UPDATE TB_YM_STACKLAYER            
							   SET MOD_DDTT     = SYSDATE             
							     , MODIFIER     = :V_MODIFIER             
							     , STACK_LAYER_ACTIVE_STAT = NVL(:V_STACK_LAYER_ACTIVE_STAT, STACK_LAYER_ACTIVE_STAT)
							     , STOCK_ID                = NVL(:V_STOCK_ID               , STOCK_ID)
							     , STACK_LAYER_STAT        = NVL(:V_STACK_LAYER_STAT       , STACK_LAYER_STAT)
							 WHERE STACK_COL_GP   = :V_STACK_COL_GP
							   AND STACK_BED_GP   = :V_STACK_BED_GP
							   AND STACK_LAYER_GP = :V_STACK_LAYER_GP 
					    	 */
							commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColBedGp", logId, methodNm, "TB_YM_STACKLAYER 등록");				
							nSTACK_LAYER_GP++;
						}
						
						// 신규 좌표 정보 READ
						JDTORecord jrPara = JDTORecordFactory.getInstance().create();
						jrPara.setField("STACK_COL_GP" 	, ydChgStkColGp);	
						jrPara.setField("STACK_BED_GP" 	, ydChgStkBedNo);	
						jrPara.setField("STACK_LAYER_GP", sSTACK_LAYER_GP_SCH);	
						/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdStkbedLyr
						SELECT B.STACK_LAYER_X_AXIS 
						     , A.YD_STK_BED_XAXIS_TOL
							 , B.STACK_LAYER_Y_AXIS 
						     , A.YD_STK_BED_YAXIS_TOL
						     , B.STACK_LAYER_Z_AXIS 
						     , A.YD_STK_BED_ZAXIS_TOL
						  FROM TB_YM_STACKER A
						     , TB_YM_STACKLAYER B
						 WHERE A.STACK_COL_GP = B.STACK_COL_GP
						   AND A.STACK_BED_GP = B.STACK_BED_GP
						   AND A.STACK_COL_GP   = :V_STACK_COL_GP
						   AND A.STACK_BED_GP   = :V_STACK_BED_GP
						   AND B.STACK_LAYER_GP = :V_STACK_LAYER_GP
						*/   
						JDTORecordSet jsStkLayAxis = commDao.select(jrPara, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdStkbedLyr", logId, methodNm, "신규 좌표위치 조회");				
						if (jsStkLayAxis.size() == 0) {
							commUtils.printLog(logId, methodNm + "저장위치 이상", "SL");
							throw new Exception("저장위치 이상.! [" + ydCrnSchId + "]");
						} 
						jsStkLayAxis.absolute(1);
						JDTORecord jrStkLayAxis = JDTORecordFactory.getInstance().create();
						jrStkLayAxis.setRecord(jsStkLayAxis.getRecord());
						
						inRecord   = JDTORecordFactory.getInstance().create();
						inRecord.setField("MODIFIER"        		, modifier);	
						inRecord.setField("YD_CRN_SCH_ID"	        , ydCrnSchId);
						inRecord.setField("YD_DN_WO_LOC"	     	, ydChgDnWoLoc);	
						inRecord.setField("YD_DN_WO_LAYER"	        , sSTACK_LAYER_GP_SCH);
						inRecord.setField("YD_DN_WO_LOC_XAXIS"		, commUtils.trim(jrStkLayAxis.getFieldString("STACK_LAYER_X_AXIS")));
						inRecord.setField("YD_DN_WO_XAXIS_GAP_MAX"	, commUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_XAXIS_TOL")));
						inRecord.setField("YD_DN_WO_XAXIS_GAP_MIN"	, commUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_XAXIS_TOL")));
						inRecord.setField("YD_DN_WO_LOC_YAXIS"		, commUtils.trim(jrStkLayAxis.getFieldString("STACK_LAYER_Y_AXIS")));
						inRecord.setField("YD_DN_WO_YAXIS_GAP_MAX"	, commUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_YAXIS_TOL")));
						inRecord.setField("YD_DN_WO_YAXIS_GAP_MIN"	, commUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_YAXIS_TOL")));
						inRecord.setField("YD_DN_WO_LOC_ZAXIS"		, commUtils.trim(jrStkLayAxis.getFieldString("STACK_LAYER_X_AXIS")));
						inRecord.setField("YD_DN_WO_ZAXIS_GAP_MAX"	, commUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_ZAXIS_TOL")));
						inRecord.setField("YD_DN_WO_ZAXIS_GAP_MIN"	, commUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_ZAXIS_TOL")));
						inRecord.setField("YD_L2_REQUEST_STAT"	    , ""); 
						inRecord.setField("YD_DN_WO_LOC_TO"		    , ""); 
						inRecord.setField("DOWN_ROTATION_ANGLE"		, commUtils.trim(rcvMsg.getFieldString("ROTATION_ANGLE"))); //회전각도
						if ("".equals(ydUpWrLoc)){
							inRecord.setField("YD_WRK_PROG_STAT"	, "1");
						} else{
							inRecord.setField("YD_WRK_PROG_STAT"	, "2");
						}
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCrnWrk 
						UPDATE TB_YM_CRNSCH
						   SET MODIFIER = :V_MODIFIER
						      ,MOD_DDTT = SYSDATE
						      ,YD_DN_WO_LOC             = NVL(:V_YD_DN_WO_LOC           ,YD_DN_WO_LOC)
						      ,YD_DN_WO_LAYER           = NVL(:V_YD_DN_WO_LAYER         ,YD_DN_WO_LAYER)
						      ,YD_DN_WO_LOC_XAXIS       = NVL(:V_YD_DN_WO_LOC_XAXIS     ,YD_DN_WO_LOC_XAXIS)
						      ,YD_DN_WO_XAXIS_GAP_MAX   = NVL(:V_YD_DN_WO_XAXIS_GAP_MAX ,YD_DN_WO_XAXIS_GAP_MAX)
						      ,YD_DN_WO_XAXIS_GAP_MIN   = NVL(:V_YD_DN_WO_XAXIS_GAP_MIN ,YD_DN_WO_XAXIS_GAP_MIN)
						      ,YD_DN_WO_LOC_YAXIS       = NVL(:V_YD_DN_WO_LOC_YAXIS     ,YD_DN_WO_LOC_YAXIS)
						      ,YD_DN_WO_YAXIS_GAP_MAX   = NVL(:V_YD_DN_WO_YAXIS_GAP_MAX ,YD_DN_WO_YAXIS_GAP_MAX)
						      ,YD_DN_WO_YAXIS_GAP_MIN   = NVL(:V_YD_DN_WO_YAXIS_GAP_MIN ,YD_DN_WO_YAXIS_GAP_MIN)
						      ,YD_DN_WO_LOC_ZAXIS       = NVL(:V_YD_DN_WO_LOC_ZAXIS     ,YD_DN_WO_LOC_ZAXIS)
						      ,YD_DN_WO_ZAXIS_GAP_MAX   = NVL(:V_YD_DN_WO_ZAXIS_GAP_MAX ,YD_DN_WO_ZAXIS_GAP_MAX)
						      ,YD_DN_WO_ZAXIS_GAP_MIN   = NVL(:V_YD_DN_WO_ZAXIS_GAP_MIN ,YD_DN_WO_ZAXIS_GAP_MIN)
						      ,YD_L2_REQUEST_STAT       = :V_YD_L2_REQUEST_STAT 
						      ,YD_DN_WO_LOC_TO          = :V_YD_DN_WO_LOC_TO
						      ,YD_WRK_PROG_STAT         = :V_YD_WRK_PROG_STAT
						 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						 */				
						commDao.update(inRecord, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCrnWrk", logId, methodNm, "TB_YM_CRNSCH 등록");		
						
						//권하위치 변경후 작업지시정보를  재전송 (Manual 작업지시보내는 방법으로  선택 인경우만 전송한다)
						//권상지시 or 권상완료
						
			    		if ( ydWrkProgStst.equals("1") || ydWrkProgStst.equals("2")){ //권상지시 or 권상완료
			    			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			    			jrYdMsg.setResultCode(logId);	//Log ID
			    			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
		
			    			jrYdMsg.setField("JMS_TC_CD"         	, "A8YML007"); 					//JMSTC코드
			    			jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); 	//JMSTC생성일시
			    			jrYdMsg.setField("MODIFIER"        		, modifier);	                //수정자
			    			jrYdMsg.setField("YD_EQP_ID"        	, ydEqpId);  					//야드설비ID
			    			jrYdMsg.setField("YD_WRK_PROG_STAT"     , ydWrkProgStst); 				//야드작업진행상태
			    			jrYdMsg.setField("YD_SCH_CD"        	, ydSchCd);						//야드스케쥴코드
			    			jrYdMsg.setField("YD_CRN_SCH_ID"        , ydCrnSchId); 					//야드크레인스케쥴ID
			    			
			    			//크레인작업지시요구 전문을 추가
							jrRtn = commUtils.addSndData(jrRtn, this.rcvA8YML007(jrYdMsg));
						}
			    		
			    		//------------------------------------------------------------------------
			    		// 본래 권하위치가 차량 또는 대차에서 일반야드로 권하위치를 변경하는경우
			    		//------------------------------------------------------------------------
			    		String szChgEqpGp    = ydChgDnWoLoc.substring(2,4); 
			    		String szBefEqpGp    = "";
			    		// 기존 권하위치 
			    		if (ydBefDnWoLoc.length() >= 6){
			    			szBefEqpGp = ydBefDnWoLoc.substring(2,4);
			    		}
			    		
			    		// 기존 설비구분이 차량/대차이고 , 신규 설비 구분이 그 차량/대차 작업이 아닌경우  
			    		// 작업예약 ID를 Clear  한다.
			    		if (szBefEqpGp.equals("TC")||szBefEqpGp.equals("PT")){
			    			if (!szChgEqpGp.equals(szBefEqpGp)){
		
			    				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
			    				
			    				String szULGp  = ydSchCd.substring(6,7);  //상차구분
			    				String szCarGp = ydSchCd.substring(2,4);
			    				
			    				//스케줄 코드가 차량/대차 인경우 구분
			    				if(szCarGp.equals("TC")){
			    	 				if(szULGp.equals("U")){  
		
			    	 					/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCommTcarSchWbDelLd 
			    	 					--대차스케줄 작업예약ID 삭제 
			    	 					UPDATE USRYMA.TB_YM_TCARSCH
			    	 					   SET MODIFIER              = :V_MODIFIER
			    	 					      ,MOD_DDTT              = SYSDATE
			    	 					      ,YD_CARLD_WRK_BOOK_ID  = NULL
			    	 					 WHERE DEL_YN                = 'N'
			    	 					   AND YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
			    	    				*/   
			    	    				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCommTcarSchWbDelLd", logId, methodNm, "TB_YD_TCARSCH");				
		
			    					}else if(szULGp.equals("L")){
			    						//하차인경우 작업예약 정보 삭제
			    						/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCommTcarSchWbDelUd 
			    						--대차스케줄 작업예약ID 삭제 
			    						UPDATE USRYMA.TB_YM_TCARSCH
			    						   SET MODIFIER              = :V_MODIFIER
			    						      ,MOD_DDTT              = SYSDATE
			    						      ,YD_CARUD_WRK_BOOK_ID  =NULL
			    						 WHERE DEL_YN                = 'N'
			    						   AND YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
			    	    				*/   
			    	    				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCommTcarSchWbDelUd", logId, methodNm, "TB_YD_TCARSCH");				
			    						
			    					}
			    					
			    				}else if(szCarGp.equals("PT")){
			    					// 차량인 경우 		
			    					if(szULGp.equals("U")){
			    						
			    						/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCommCarSchWbDelLd 
			    						--  상차 차량 작업 예약 ID CLEAR
			    						UPDATE USRYDA.TB_YD_CARSCH
			    						   SET MODIFIER              = :V_MODIFIER
			    						      ,MOD_DDTT              = SYSDATE
			    						      ,YD_CARLD_WRK_BOOK_ID  = NULL
			    						 WHERE DEL_YN                = 'N'
			    						   AND YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
			    						 */  
			    						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCommCarSchWbDelLd", logId, methodNm, "TB_YD_CARSCH");				
			    						
			    					}else if(szULGp.equals("L")){
			    						//하차인경우 작업예약 정보 삭제
			    						
			    						/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCommCarSchWbDelUd 
			    						--  하차 차량 작업 예약 ID CLEAR
			    						UPDATE USRYDA.TB_YD_CARSCH
			    						   SET MODIFIER              = :V_MODIFIER
			    						      ,MOD_DDTT              = SYSDATE
			    						      ,YD_CARUD_WRK_BOOK_ID  = NULL
			    						 WHERE DEL_YN                = 'N'
			    						   AND YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
			    						*/   
			    						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCommCarSchWbDelUd", logId, methodNm, "TB_YD_CARSCH");				
			    					}
			    				}
			    			}
			    		}
			    		
						return jrRtn;
					}			
				}	
			} else {
				/**********************************************************
				* 2.권하위치 변경에 대한 요청이 아닌 경우
				*    - 스케줄 취소
				**********************************************************/						
				if ("Y".equals(reqYn)){
					
					
					commUtils.printLog(logId, methodNm + "가능인 경우", "SL");
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCrnSchProgStatMsg 
					UPDATE TB_YM_CRNSCH  A 
					   SET YD_WRK_PROG_STAT    =(CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN NVL(:V_YD_WRK_PROG_STAT,'1') ELSE YD_WRK_PROG_STAT END)
					     , YD_WRK_PROG_REQ_MSG =(CASE WHEN YD_WRK_PROG_REQ_MSG IS NULL THEN :V_YD_WRK_PROG_REQ_MSG ELSE YD_WRK_PROG_REQ_MSG END) 
					 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					
					*/
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCrnSchProgStatMsg", logId, methodNm,  "TB_YM_CRNSCH");
					
					commUtils.printLog(logId, "요청구분 [ " + reqYn + " - 작업취소 " + headMsgGp + " ]", "SL");
					
					if ("D".equals(headMsgGp) && autoYn.equals("Y")  ){
						commUtils.printLog(logId, "스케쥴취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");
						
						jrParam.setField("YD_WBOOK_ID"  	, ydWbookId );
						jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
						jrParam.setField("YD_EQP_ID"    	, ydEqpId   );
						jrParam.setField("YD_SCH_CD"    	, ydSchCd   );
						jrParam.setField("YD_L2_RETURN_FLAG", "Y"       );
						jrParam.setField("IS_LAST_SELECTED"	, "1");
						/**********************************************************
						* 1. 크레인스케줄 취소
						**********************************************************/
						EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
						JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
						
						jrRtn = commUtils.addSndData(jrRtn, jrRst);
						
						/**********************************************************
						* 이송하차일 경우 작업예약 취소
						**********************************************************/
//						if ("PT02LM".equals(ydSchCd.substring(2, 8))){
//							EJBConnector ejbConn1 = new EJBConnector("default", "BSlabJspSeEJB", this);
//							JDTORecord jrRst1 = (JDTORecord)ejbConn1.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
//							jrRtn = commUtils.addSndData(jrRtn, jrRst1);
//						}

						commUtils.printLog(logId, "ydL2RequestStat [ " + ydL2RequestStat + " - 작업취소  ]", "SL");
						
						if ("X".equals(ydL2RequestStat)){   		// 화면에서 작업 취소 임 : 작업예약 삭제
							/**********************************************************
							* 2. 작업예약 취소
							**********************************************************/
							EJBConnector ejbConn1 = new EJBConnector("default", "BSlabJspSeEJB", this);
							jrRst = (JDTORecord)ejbConn1.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
							
							jrRtn = commUtils.addSndData(jrRtn, jrRst);
				
							// 스케쥴 취소+작업예약 취소 인 경우 
							/**********************************************************
							* 11. 크레인작업지시요구 전문 호출(AxYDL007)
							**********************************************************/
							JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
							jrYdMsg.setResultCode(logId);	//Log ID
							jrYdMsg.setResultMsg(methodNm);	//Log Method Name

							jrYdMsg.setField("JMS_TC_CD"		, "A8YML007"); //JMSTC코드
							jrYdMsg.setField("YD_EQP_ID"       	, ydEqpId   ); //야드설비ID
							jrYdMsg.setField("YD_WRK_PROG_STAT"	, "4"       ); //야드작업진행상태(권하완료)
							jrYdMsg.setField("YD_SCH_CD"       	, ydSchCd   ); //야드스케쥴코드
							jrYdMsg.setField("MODIFIER"        	, modifier  ); //수정자
							
							//크레인작업지시 요구을 추가
							jrRtn = commUtils.addSndData(jrRtn, this.rcvA8YML007(jrYdMsg));
						
						}	
					}
					
					/***************************
					 * 일시정지-긴급작업(S1)
					 ***************************/
					String sAPP030 = ymComm.BCoilApplyYn("APP030","3","S1");
					commUtils.printLog(logId, "[A8YML015] 일시정지-긴급작업(S1) 응답 시작", "[INFO]");
					
					if ("Y".equals(sAPP030)) {
						if ("Y".equals(reqYn) && "0".equals(sYD_SCH_PRIOR)) {
							// 해당 스케줄 1로 변경
							/*
							UPDATE TB_YM_CRNSCH
							   SET MODIFIER     = :V_MODIFIER
							      ,MOD_DDTT     = SYSDATE
							      ,YD_WRK_PROG_STAT =:V_YD_WRK_PROG_STAT
							      ,YD_WORD_DT   = SYSDATE 
							 WHERE YD_CRN_SCH_ID  = :V_YD_CRN_SCH_ID
							   AND DEL_YN = 'N'
							 */
							jrParam.setField("YD_WRK_PROG_STAT"	, "1"       ); // 긴급작업 응답이 Y로 왔으므로 선택(1)
							jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt1Auto");
							
							// 동일크레인의 해당 스케줄외 W로 변경 
						    /*  
							UPDATE TB_YM_CRNSCH A
							   SET YD_WRK_PROG_STAT =:V_YD_WRK_PROG_STAT
							     , YD_WORD_DT       = NULL
							     , MODIFIER         = :V_MODIFIER
							     , MOD_DDTT         = SYSDATE
							     , YD_SCH_PRIOR     = (SELECT YD_WRK_CRN_PRIOR 
							                             FROM USRYMA.TB_YM_SCHEDULERULE B
													    WHERE B.YD_SCH_CD = A.YD_SCH_CD)
							 WHERE DEL_YN         = 'N'
							   AND YD_EQP_ID      = :V_YD_EQP_ID
							   AND YD_CRN_SCH_ID != :V_YD_CRN_SCH_ID 
						     */
							jrParam.setField("YD_WRK_PROG_STAT"	, "W"       );  
							jrParam.setField("YD_EQP_ID"    	, ydEqpId   );
							commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCrnSchPriorW");
						}
					}
					commUtils.printLog(logId, "[A8YML015] 일시정지-긴급작업(S1) 응답 끝", "[INFO]");

					
					/**************************************************
					 * D3 이송하차 고도화 
					 *   - D2 이송하차 크레인작업지시 응답 Y 받은후 D3 크레인에게 작업지시 보내기 
					 **************************************************/

					commUtils.printLog(logId, "[A8YML015] D3 이송하차 고도화  시작", "[INFO]");
					
					if("2DPT02LM".equals(ydSchCd)) { //D동 이송하차
					
						if("D2".equals(ydEqpId.substring(4,6))) {
							
							String sAPP100_D3_ADV_TYP = ymComm.BCoilApplyYn("APP100","2","D3_ADV_TYP");
							
							if("Y".equals(sAPP100_D3_ADV_TYP)) {
						
								String sAPP100_D3_ADV_YN = ymComm.BCoilApplyYn("APP100","2","D3_ADV_YN");  
								
								commUtils.printLog(logId,  "==========[[[ SLAB D동 D3 고도화 적용여부 :" + sAPP100_D3_ADV_YN + " ]]]============", "SL");
							
								if("Y".equals(sAPP100_D3_ADV_YN)) {
									
									/**********************************************************
									* D3 설비상태 Check
									* D3크레인이 고장 이나 off-line 일경우 자동편성 안함
									**********************************************************/
									//DAO Parameter - Log ID, Method, 수정자 Set
									JDTORecord jrEqpParam = commUtils.getParam(logId, methodNm, "");
			
									jrEqpParam.setField("YD_EQP_ID", "2DCRD3"); //야드설비ID
			
									JDTORecord jrChk = ymComm.chkEqpStat(jrEqpParam);
			
									String ydEqpL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));
									commUtils.printLog(logId,  "==========[[[ SLAB D동 D3 고도화 적용시 D3크레인 상태 여부:" + ydEqpL3Msg + " ]]]============", "SL");
									
									
									if ("".equals(ydEqpL3Msg)) {
										
										JDTORecord jrYdMsg1 = JDTORecordFactory.getInstance().create();
										jrYdMsg1.setResultCode(logId);	//Log ID
										jrYdMsg1.setResultMsg(methodNm);	//Log Method Name
				
										jrYdMsg1.setField("JMS_TC_CD"			, "A8YML007"  ); //JMSTC코드
										jrYdMsg1.setField("YD_EQP_ID"       	, "2DCRD3"); //야드설비ID D동 3호기
										jrYdMsg1.setField("YD_WRK_PROG_STAT"	, "4"      	  ); //야드작업진행상태(권하완료)
										jrYdMsg1.setField("YD_SCH_CD"       	, ""); //야드스케쥴코드
										jrYdMsg1.setField("MODIFIER"        	, modifier    ); //수정자
										jrYdMsg1.setField("APP100_D3_ADV_YN"    , "SKIP"); // 고도화 로직 SIKP 하고 일반 로직 수행지정
										jrRtn = commUtils.addSndData(jrRtn, this.rcvA8YML007(jrYdMsg1));
									}
								}
							}
							
						}
					}
					
					commUtils.printLog(logId, "[A8YML015] D3 이송하차 고도화  끝", "[INFO]");
					
				
				 } else if ("N".equals(reqYn)){
					
					commUtils.printLog(logId, methodNm + "불가 일경우", "SL");
					//권하위치 변경 N응답시 메세지 update
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCrnSchProgStatMsgNo
					UPDATE TB_YM_CRNSCH  
					   SET YD_WRK_PROG_REQ_MSG = :V_YD_WRK_PROG_REQ_MSG
					 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH
					*/
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCrnSchProgStatMsgNo", logId, methodNm,  "TB_YM_CRNSCH");
					
					if(ReqMsg.length()>=4) {
						if("E001".equals(ReqMsg.substring(0,4))) {
							//작업지시가 내려와 이미 슬라브를 집었는데 다음작업지시가 내려온 경우
							//다음작업지시를 수행할 수 없는 상황 (차상국은 이전 작업지시를 그대로 실행)
							//L3 는 'N' 응답에 'E001' 메세지 받은 크레인 스케줄 ID의 작업진행상태를  'W'로 변경한다.
							commUtils.printLog(logId, methodNm + "E001 불가 일경우", "SL");
							
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCrnSchYdWrkProgStat 
							UPDATE TB_YM_CRNSCH
							   SET YD_WRK_PROG_STAT = (CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN NVL(:V_YD_WRK_PROG_STAT,'1') ELSE YD_WRK_PROG_STAT END)
							 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID */
							jrParam.setField("YD_WRK_PROG_STAT"	, "W");  					
							jrParam.setField("YD_CRN_SCH_ID"   	, ydCrnSchId);  					
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCrnSchYdWrkProgStat", logId, methodNm,  "크레인 스케줄 작업진행상태 변경");
							

							if(ReqMsg.length()>=23) {
								
								//이전 스케줄ID의 상태를 '1'로 변경한다.
								String befCrnSchId = ReqMsg.substring(5,23);
								  
								/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCrnSchYdWrkProgStat 
								UPDATE TB_YM_CRNSCH
								   SET YD_WRK_PROG_STAT = (CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN NVL(:V_YD_WRK_PROG_STAT,'1') ELSE YD_WRK_PROG_STAT END)
								 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID */
								jrParam.setField("YD_WRK_PROG_STAT"	, "1");  					
								jrParam.setField("YD_CRN_SCH_ID"   	, befCrnSchId);  					
								commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCrnSchYdWrkProgStat", logId, methodNm,  "이전 크레인 스케줄 작업진행상태  변경");
							}									
						}//E001
						else if("E100".equals(ReqMsg.substring(0,4))){
							//작업지시가 내려와 이미 슬라브를 집었는데 통서포팅블럭 동작 이상인경우
							//SLAB 작업을 할수없는 상화
							//L3 는 'N' 응답에 'E100' 메세지 받은 크레인 스케줄 ID의 작업진행상태를  'W'로 변경한다.
							commUtils.printLog(logId, methodNm + "E100 불가 일경우", "SL");
							
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCrnSchYdWrkProgStat 
							UPDATE TB_YM_CRNSCH
							   SET YD_WRK_PROG_STAT = (CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN NVL(:V_YD_WRK_PROG_STAT,'1') ELSE YD_WRK_PROG_STAT END)
							 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID */
							jrParam.setField("YD_WRK_PROG_STAT"	, "W");  					
							jrParam.setField("YD_CRN_SCH_ID"   	, ydCrnSchId);  					
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCrnSchYdWrkProgStat", logId, methodNm,  "크레인 스케줄 작업진행상태 변경");
							
							String sAPP100_E100_YN = ymComm.BCoilApplyYn("APP100","2","E100_YN");  
							
							if("Y".equals(sAPP100_E100_YN)){
								
								commUtils.printLog(logId, "스케쥴취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");
								
								jrParam.setField("YD_WBOOK_ID"  	, ydWbookId );
								jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
								jrParam.setField("YD_EQP_ID"    	, ydEqpId   );
								jrParam.setField("YD_SCH_CD"    	, ydSchCd   );
								jrParam.setField("YD_L2_RETURN_FLAG", "Y"       );
								jrParam.setField("IS_LAST_SELECTED"	, "1");
								jrParam.setField("CNCL_BY_WHO"		, "E100"); //이송하차 크레인스케줄 취소시 작업얘약도 함께 삭제하는데 E100 으로 크레인스케줄 취소시는 작업예약 삭제을 하지 않는다.
								
								
								//스케줄 삭제전 Grouping 작업예약ID 정보 조회
								/*com.inisteel.cim.ym.bslab.dao.BSlabDAO.getGrpSlabWrkBookMtl
								SELECT A.YD_WBOOK_ID
								      ,A.YD_GP
								      ,A.YD_BAY_GP
								      ,A.YD_SCH_CD
								      ,A.YD_SCH_PRIOR
								      ,A.YD_SCH_PROG_STAT
								      ,A.YD_TO_LOC_DCSN_MTD
								      ,A.YD_TO_LOC_GUIDE
								      ,A.YD_WRK_PLAN_CRN
								      ,B.STOCK_ID
								      ,B.STACK_COL_GP
								      ,B.STACK_BED_GP
								      ,B.STACK_LAYER_GP
								      ,B.YD_UP_COLL_SEQ
								      ,B.YD_TAKE_OUT_CD
								      ,(SELECT CASE WHEN SUBSTR(YD_DN_WO_LOC,1,2) = 'XX' THEN ''
								               ELSE YD_DN_WO_LOC END AS YD_TO_LOC_GUIDE_FNL
								         FROM TB_YM_CRNSCH C
								             ,TB_YM_CRNWRKMTL D
								        WHERE C.YD_WBOOK_ID = A.YD_WBOOK_ID
								          AND C.YD_CRN_SCH_ID = D.YD_CRN_SCH_ID
								          AND D.STOCK_ID = B.STOCK_ID
								          AND C.DEL_YN = 'N'
								          AND D.DEL_YN = 'N'
								          AND D.YD_AID_WRK_YN = 'N') AS YD_TO_LOC_GUIDE_FNL
								 FROM  TB_YM_WRKBOOK A, TB_YM_WRKBOOKMTL B
								WHERE  A.YD_WBOOK_ID = B.YD_WBOOK_ID
								  AND  A.YD_WBOOK_ID = :V_YD_WBOOK_ID
								 */
								JDTORecordSet rsGrpResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getGrpSlabWrkBookMtl", logId, methodNm, "Grouping TB_YM_WRKBOOK 작업취소 대상재 조회");
								
								
								/**********************************************************
								* 1. 크레인스케줄 취소
								**********************************************************/
								EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
								JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
								
								jrRtn = commUtils.addSndData(jrRtn, jrRst);
								
								String oldWbookId = "";
								String sYD_TO_LOC_GUIDE_FNL = "";
								
								//대차상차 스케줄 주작업
								if( ydToLocDcsnMtd.equals("S") && (ydSchCd.substring(2,4)+ydSchCd.substring(6)).equals("TCUM")){
									
									//DAO Parameter - Log ID, Method, 수정자 Set
									JDTORecord jrTcParam = JDTORecordFactory.getInstance().create();
									jrTcParam.setResultCode(logId);	//Log ID
									jrTcParam.setResultMsg(methodNm);	//Log Method Name
	
									jrTcParam.setField("MODIFIER" , "E100");
									
									for (int i = 0; i < rsGrpResult.size(); ++i) {
										//Grouping 전 작업예약ID 조회
										if(!"".equals(rsGrpResult.getRecord(i).getFieldString("YD_TAKE_OUT_CD"))){
											oldWbookId = rsGrpResult.getRecord(i).getFieldString("YD_TAKE_OUT_CD");
										}
									}
									//스케줄 취소후 분리된 작업예약중 상단재료의 스케줄 실행
									sYD_TO_LOC_GUIDE_FNL = rsGrpResult.getRecord(0).getFieldString("YD_TO_LOC_GUIDE_FNL");
									
									//하단작업예약 update
									jrTcParam.setField("DIST_SHIPASSIGN_GP"  , ""); //1매 스케줄기동여부
									jrTcParam.setField("YD_WBOOK_ID", oldWbookId); //야드작업예약ID
									jrTcParam.setField("YD_TO_LOC_GUIDE_FNL"  , ""); //취소하기전 권하위치
									
									//작업예약 화면에서 작업예약 1매씩 실행여부 Update
									/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDistShipassWrkBook 
										UPDATE TB_YM_WRKBOOK
										   SET DIST_SHIPASSIGN_GP = :V_DIST_SHIPASSIGN_GP
										      ,MODIFIER    = :V_MODIFIER
										      ,MOD_DDTT    = SYSDATE
										      ,YD_TO_LOC_GUIDE_FNL = NVL(:V_YD_TO_LOC_GUIDE_FNL,YD_TO_LOC_GUIDE_FNL)
										WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
									 */
									commDao.update(jrTcParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDistShipassWrkBook", logId, methodNm, "작업예약 1매씩 실행여부 Update");
									
	
									//상단작업예약 1매스케줄 기동
									jrTcParam.setField("JMS_TC_CD", "YMYMJ202"); //야드작업예약ID
									jrTcParam.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());	//현재시각				
									jrTcParam.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
									jrTcParam.setField("YD_EQP_ID"  , ""); //야드설비ID
									jrTcParam.setField("DIST_SHIPASSIGN_GP"  , "1"); //1매 스케줄기동여부
									jrTcParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
									jrTcParam.setField("YD_TO_LOC_GUIDE_FNL"  , sYD_TO_LOC_GUIDE_FNL); //취소하기전 권하위치
									
									//작업예약 화면에서 작업예약 1매씩 실행여부 Update
									/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDistShipassWrkBook 
										UPDATE TB_YM_WRKBOOK
										   SET DIST_SHIPASSIGN_GP = :V_DIST_SHIPASSIGN_GP
										      ,MODIFIER    = :V_MODIFIER
										      ,MOD_DDTT    = SYSDATE
										      ,YD_TO_LOC_GUIDE_FNL = NVL(:V_YD_TO_LOC_GUIDE_FNL,YD_TO_LOC_GUIDE_FNL)
										WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
									 */
									commDao.update(jrTcParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDistShipassWrkBook", logId, methodNm, "작업예약 1매씩 실행여부 Update");
									
									//크레인스케줄기동 전문
									EJBConnector sndConn = new EJBConnector("default", "BSlabSchSeEJB", this);
									JDTORecord jrRtn2 = (JDTORecord)sndConn.trx("procYMYMJ202", new Class[] { JDTORecord.class }, new Object[] { jrTcParam });
									jrRtn = commUtils.addSndData(jrRtn, jrRtn2);
									
								// W/B보급 주작업인 경우
								}else if(ydToLocDcsnMtd.equals("S") && ydSchCd.equals("2CWB01UM")){
									
									//하단적치중인 재료의 작업예약 삭제
									String oldStockId = rsGrpResult.getRecord(1).getFieldString("STOCK_ID");
									
									jrParam.setField("STOCK_ID"  	, oldStockId );
									//grouping 작업예약재료 삭제
									/*com.inisteel.cim.ym.bslab.dao.BSlabDAO.delGrpCnclYnWrkBookMtl
									DELETE TB_YM_WRKBOOKMTL
									 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
									   AND STOCK_ID = :V_STOCK_ID
									 */
									commDao.delete(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.delGrpCnclYnWrkBookMtl", logId, methodNm, "Grouping TB_YM_WRKBOOKMTL 삭제");
									
									sYD_TO_LOC_GUIDE_FNL = rsGrpResult.getRecord(0).getFieldString("YD_TO_LOC_GUIDE_FNL");
									
									//DAO Parameter - Log ID, Method, 수정자 Set
									JDTORecord jrTcParam = JDTORecordFactory.getInstance().create();
									jrTcParam.setResultCode(logId);	//Log ID
									jrTcParam.setResultMsg(methodNm);	//Log Method Name
	
									jrTcParam.setField("MODIFIER" , "E100");	
									
									jrTcParam.setField("JMS_TC_CD", "YMYMJ202"); //야드작업예약ID
									jrTcParam.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());	//현재시각				
									jrTcParam.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
									jrTcParam.setField("YD_EQP_ID"  , ""); //야드설비ID
									jrTcParam.setField("DIST_SHIPASSIGN_GP"  , "1"); //1매 스케줄기동여부
									jrTcParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
									
									jrTcParam.setField("YD_TO_LOC_GUIDE_FNL"  , sYD_TO_LOC_GUIDE_FNL); //취소하기전 권하위치
									
									//작업예약 화면에서 작업예약 1매씩 실행여부 Update
									/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDistShipassWrkBook 
										UPDATE TB_YM_WRKBOOK
										   SET DIST_SHIPASSIGN_GP = :V_DIST_SHIPASSIGN_GP
										      ,MODIFIER    = :V_MODIFIER
										      ,MOD_DDTT    = SYSDATE
										      ,YD_TO_LOC_GUIDE_FNL = NVL(:V_YD_TO_LOC_GUIDE_FNL,YD_TO_LOC_GUIDE_FNL)
										WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
									 */
									commDao.update(jrTcParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDistShipassWrkBook", logId, methodNm, "작업예약 1매씩 실행여부 Update");
									
									//크레인스케줄기동 전문
									EJBConnector sndConn = new EJBConnector("default", "BSlabSchSeEJB", this);
									JDTORecord jrRtn2 = (JDTORecord)sndConn.trx("procYMYMJ202", new Class[] { JDTORecord.class }, new Object[] { jrTcParam });
									jrRtn = commUtils.addSndData(jrRtn, jrRtn2);
									
								}else{
									for (int i = 0; i < rsGrpResult.size(); ++i) {
										//Grouping 전 작업예약ID 조회
										if(!"".equals(rsGrpResult.getRecord(i).getFieldString("YD_TAKE_OUT_CD"))){
											oldWbookId = rsGrpResult.getRecord(i).getFieldString("YD_TAKE_OUT_CD");
										}
										sYD_TO_LOC_GUIDE_FNL = rsGrpResult.getRecord(i).getFieldString("YD_TO_LOC_GUIDE_FNL");
										
										//DAO Parameter - Log ID, Method, 수정자 Set
										JDTORecord jrTcParam = JDTORecordFactory.getInstance().create();
										jrTcParam.setResultCode(logId);	//Log ID
										jrTcParam.setResultMsg(methodNm);	//Log Method Name
		
										jrTcParam.setField("MODIFIER" , "E100");	
										
										jrTcParam.setField("JMS_TC_CD", "YMYMJ202"); //야드작업예약ID
										jrTcParam.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());	//현재시각				
										jrTcParam.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
										jrTcParam.setField("YD_EQP_ID"  , ""); //야드설비ID
										jrTcParam.setField("DIST_SHIPASSIGN_GP"  , "1"); //1매 스케줄기동여부
										
										//주작업예약 먼저 실행
										if(i == 0){
											jrTcParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
										}else{										
											jrTcParam.setField("YD_WBOOK_ID", oldWbookId); //Grouping 야드작업예약ID
										}
										
										jrTcParam.setField("YD_TO_LOC_GUIDE_FNL"  , sYD_TO_LOC_GUIDE_FNL); //취소하기전 권하위치
										
										//작업예약 화면에서 작업예약 1매씩 실행여부 Update
										/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDistShipassWrkBook 
											UPDATE TB_YM_WRKBOOK
											   SET DIST_SHIPASSIGN_GP = :V_DIST_SHIPASSIGN_GP
											      ,MODIFIER    = :V_MODIFIER
											      ,MOD_DDTT    = SYSDATE
											      ,YD_TO_LOC_GUIDE_FNL = NVL(:V_YD_TO_LOC_GUIDE_FNL,YD_TO_LOC_GUIDE_FNL)
											WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
										 */
										commDao.update(jrTcParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDistShipassWrkBook", logId, methodNm, "작업예약 1매씩 실행여부 Update");
										
										//크레인스케줄기동 전문
										EJBConnector sndConn = new EJBConnector("default", "BSlabSchSeEJB", this);
										JDTORecord jrRtn2 = (JDTORecord)sndConn.trx("procYMYMJ202", new Class[] { JDTORecord.class }, new Object[] { jrTcParam });
										jrRtn = commUtils.addSndData(jrRtn, jrRtn2);
									}//for
								}//else
								
								
							}//sAPP100_E100_YN
						}//E100
					}
					return jrRtn;
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 크레인작업가능응답(A8YML015)
	
	/**
	 *      [A] 오퍼레이션명 : 차량작업예정정보요구(A8YML016)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException 
	*/
	public JDTORecord rcvA8YML016(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "차량작업예정정보요구[BSlabL2RcvSeEJB.rcvA8YML016] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값ydEqpId
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydLoadLoc   	= commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"));     	//상차도 위치
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); 		//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			String CarNo = "";
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydLoadLoc.length() < 6) {
				commUtils.printLog(logId, methodNm + "상차도 길이 < 6 :" +ydLoadLoc , "SL");
				//throw new Exception("상차도 위치 [" + ydLoadLoc + "]");
			} 

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"                 , modifier);	
			jrParam.setField("PT_LOAD_LOC"      		, ydLoadLoc);  					
			jrParam.setField("SEARCH_FLAG"      		, "1");  //1:상차도, 2:차량스케쥴 ID					
			jrParam.setField("YD_GP"      		        , "2");  //야드구분
			
			if("PT".equals(ydLoadLoc.substring(2,4))) {
			
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdGetCarNoByLoc 
				SELECT NVL(CAR_NO ,TRN_EQP_CD) AS CAR_NO
				  FROM TB_YD_CARPOINT
				 WHERE DEL_YN = 'N'
				   AND YD_STK_COL_GP = :V_PT_LOAD_LOC 
				*/   
				//차량위치조회
				JDTORecordSet jrCarPoint = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdGetCarNoByLoc", logId, methodNm, "차량위치조회"); 
				if (jrCarPoint.size() == 0) {
					commUtils.printLog(logId, methodNm + "상차도 정보 없음 :" +ydLoadLoc , "SL");
					//return jrRtn;
				} else {	
				
					CarNo = jrCarPoint.getRecord(0).getFieldString("CAR_NO");
					
					if (CarNo.equals("")) {
						commUtils.printLog(logId, methodNm + "해당위치 차량정보 없음  :" +ydLoadLoc , "SL");
						//return jrRtn;
					}
				}
			
			} else {
				jrParam.setField("SEARCH_FLAG"      		, "5");  //5:대차					
			}

			/**********************************************************
			* 차량작업 예정정보 송신 (YMA8L008)
			**********************************************************/
			jrRtn = commUtils.addSndData(jrRtn, ymComm.procCarPlanInfo_Slab(jrParam));
			
			/**********************************************************
			* 저장위치제원정보 송신 (YMA8L001)
			**********************************************************/
			jrParam.setField("YD_INFO_SYNC_CD"	, "4"           ); //야드정보동기화코드
			jrParam.setField("MSG_GP"			, "I"           ); //전문구분
			jrParam.setField("STACK_COL_GP"    	, ydLoadLoc		);
			jrParam.setField("STACK_BED_GP"    	, "01"			);
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L001", jrParam));		
			
			/**********************************************************
			* 저장품제원(YMA8L002) 
			**********************************************************/
			jrParam.setField("YD_INFO_SYNC_CD"	, "4"			); //야드정보동기화코드
			jrParam.setField("MSG_GP"			, "I"			); //전문구분
			jrParam.setField("STACK_COL_GP"  	, ydLoadLoc		); //야드적치열구분
			jrParam.setField("STACK_BED_GP"  	, "01"			); //야드적치Bed번호
			jrParam.setField("YD_GP"          	, "2"			); //야드구분
			jrParam.setField("STOCK_ID"       	, ""			); //재료번호
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L002", jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 차량작업예정정보요구(A8YML016)
	
	/**
	 *      [A] 오퍼레이션명 : 상차도 작업불가(A8YML017)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML017(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "상차도 작업불가[BSlabL2RcvSeEJB.rcvA8YML017] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "상차도 작업불가(A8YML017) 수신 ", rcvMsg);

			//수신 항목 값
			String sPT_LOAD_LOC = commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC")); //상차도위치(6)
			String sUSE_YN		= commUtils.trim(rcvMsg.getFieldString("USE_YN")); //Y:사용가능, N:사용불가
			String msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"A8YML017"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			
			//수신항목 Check
			if(sPT_LOAD_LOC.length() != 6) {
				throw new Exception("상차도 위치 PT_LOAD_LOC 가 6자리가 아닙니다!! [" + sPT_LOAD_LOC + "]");
			}
			if(!"2".equals(sPT_LOAD_LOC.substring(0,1)) || !"PT".equals(sPT_LOAD_LOC.substring(2,4))) {
				throw new Exception("상차도 위치 PT_LOAD_LOC 가 야드구분이 '2'이 아니거나 SECT_GP가 'PT'가 아닙니다!! [" + sPT_LOAD_LOC + "]");
			}
			if(!"Y".equals(sUSE_YN) && !"N".equals(sUSE_YN)) {
				throw new Exception("상차도 사용유무 USE_YN 이 'Y','N' 이 아닌 값이 들어 왔습니다!! [" + sUSE_YN + "]");
			}
			
			JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
			
			
			// 차량포인트 적치열활성상태 UPDATE
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarPointActStat 
			UPDATE  TB_YD_CARPOINT
			   SET  MODIFIER = :V_MODIFIER
			       ,MOD_DDTT = SYSDATE
			       ,YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
			 WHERE  YD_STK_COL_GP = :V_YD_STK_COL_GP     */
			jrParam.setField("MODIFIER"			, modifier		); //수정자
			jrParam.setField("YD_STK_COL_GP"	, sPT_LOAD_LOC	); //상차도위치
			if("Y".equals(sUSE_YN)) {
				jrParam.setField("YD_STK_COL_ACT_STAT"	, "C"); //야드적치열활성상태 C:적치가능
				
				JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdCarPnt", logId, methodNm, "차량포인트 조회");
				if(rsResult.size() <= 0) {
					throw new Exception("대상 차량 Point 가 리스트에 없습니다!!");
				}
				String sTRN_EQP_CD = rsResult.getRecord(0).getFieldString("TRN_EQP_CD");
				String sYD_CAR_PROG_STAT = rsResult.getRecord(0).getFieldString("YD_CAR_PROG_STAT");
				
				if(!"".equals(sTRN_EQP_CD)) {
					if("1".equals(sYD_CAR_PROG_STAT)||"A".equals(sYD_CAR_PROG_STAT)) {
						jrParam.setField("YD_STK_COL_ACT_STAT"	, "R"); //야드적치열활성상태 R:예약중
					} else {
						jrParam.setField("YD_STK_COL_ACT_STAT"	, "L"); //야드적치열활성상태 L:사용중
					}
				} 				
				
			} else {
				jrParam.setField("YD_STK_COL_ACT_STAT"	, "N"); //야드적치열활성상태 N:사용불가
			}
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarPointActStat", logId, methodNm, "차량포인트 적치열활성상태 UPDATE ");
			
			//저장위치제원정보 (YMA8L001) 송신
			JDTORecord recInTemp  = JDTORecordFactory.getInstance().create();
			recInTemp.setResultCode(logId);	    //Log ID
			recInTemp.setResultMsg(methodNm);	//Log Method Name
			recInTemp.setField("YD_INFO_SYNC_CD", "4");						//1:동,2:SPAN,3:열,4:BED
			recInTemp.setField("MSG_GP"			, "I"                         ); //전문구분
			recInTemp.setField("YD_GP"			, sPT_LOAD_LOC.substring(0, 1));
			recInTemp.setField("STACK_COL_GP"	, sPT_LOAD_LOC);
			recInTemp.setField("STK_BED_GP" 	, "01");
			
			//전송 Data 생성
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L001", recInTemp));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 상차도 작업불가(A8YML017)
	
	/**
	 *      [A] 오퍼레이션명 : 차량동간이적 도착(A8YML018)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML018(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "차량동간이적 도착[BSlabL2RcvSeEJB.rcvA8YML018] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");

			jrRtn = this.procA8YML018(rcvMsg);

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}    
	
	/**
	 *      [A] 오퍼레이션명 : 차량동간이적 도착(A8YML018)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procA8YML018(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "차량동간이적 도착[BSlabL2RcvSeEJB.procA8YML018] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값ydEqpId
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydLoadLoc	= commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"));     //상차도 위치
			String CarNo   		= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));     		//차량번호
			String CarUoDnGp  	= commUtils.trim(rcvMsg.getFieldString("CAR_UPDN_GP"));		//차량상하차 구분 (1:상차,2:하차)
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); 	//수정자(Backup Only)
			int mtlCnt           = 0;      
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydLoadLoc.length() == 0) {
				throw new Exception("상차도 위치 이상 [" + ydLoadLoc + "]");
			} else if (CarNo.length() == 0) {
				throw new Exception("차량번호 이상 [" + ydLoadLoc + "]");
			} else if (CarUoDnGp.length() == 0) {
				throw new Exception("차량상하차 구분 이상 [" + ydLoadLoc + "]");
			} 
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"                 , modifier);	
			jrParam.setField("PT_LOAD_LOC"      		, ydLoadLoc);  					
			jrParam.setField("CAR_UPDN_GP"      		, CarUoDnGp);  					

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdGetCarNoTypeByLoc
			WITH TEMP_TABLE AS (
			SELECT :V_PT_LOAD_LOC AS V_PT_LOAD_LOC
			     , :V_CAR_UPDN_GP AS V_CAR_UPDN_GP
			  FROM DUAL
			)
			SELECT YD_CARPNT_CD AS YD_CARPNT_CD
			     , NVL(CAR_NO ,(CASE WHEN YD_STK_COL_ACT_STAT='C' 
			                         THEN '' 
			                         ELSE (SELECT CAR_NO 
			                                 FROM TB_YD_CARSCH 
			                                WHERE CAR_NO IN ('9999','9998','9997','9996','9995') 
			                                  AND DEL_YN = 'N' 
			                                  AND ROWNUM=1) END)) 
			                           AS CAR_NO
			     , YD_CAR_USETYPE_GP   AS YD_CAR_USETYPE_GP
			     , YD_STK_COL_ACT_STAT AS YD_STK_COL_ACT_STAT
			     , (SELECT YD_WBOOK_ID 
			          FROM TB_YM_WRKBOOK 
			             , TEMP_TABLE
			         WHERE CAR_NO IN ('9999','9998','9997','9996','9995') 
			           AND DEL_YN = 'N' 
			           AND YD_BAY_GP = SUBSTR(V_PT_LOAD_LOC,2,1) 
			           AND DECODE(SUBSTR(YD_SCH_CD,6,2) , '3L', '2'
			                                            , '3U', '1'
			                                            , '7L', '2'
			                                            , '7U', '1') = V_CAR_UPDN_GP
			           AND ROWNUM =1
			        )  AS CHK_WBOOK
			    , WLOC_CD
			    , YD_PNT_CD
			 FROM TB_YD_CARPOINT
			WHERE DEL_YN = 'N'
			  AND YD_STK_COL_GP = :V_PT_LOAD_LOC    
			*/
		   
			JDTORecordSet jrCarPoint = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdGetCarNoTypeByLoc", logId, methodNm, "차량포인트정보조회"); 
			if (jrCarPoint.size() == 0) {
				commUtils.printLog(logId, methodNm + "해당위치 포인트정보 없음  :" +ydLoadLoc , "SL");
				throw new Exception("해당위치 포인트정보 없음  [" + ydLoadLoc + "]");
				
			} 
			JDTORecord jsCarPoint = JDTORecordFactory.getInstance().create();	//전문 Return
			
			jrCarPoint.first();
			jsCarPoint.setRecord(jrCarPoint.getRecord());
			
			String sCarNo 			= commUtils.trim(jsCarPoint.getFieldString("CAR_NO"));               //szCAR_NO_GET
			String ydCarUseTypeGp 	= commUtils.trim(jsCarPoint.getFieldString("YD_CAR_USETYPE_GP"));    //szYD_CAR_USETYPE_GP
			String ydCarPntCd 		= commUtils.trim(jsCarPoint.getFieldString("YD_CARPNT_CD"));         //szYD_CARPNT_CD
			String ydStkColActStat 	= commUtils.trim(jsCarPoint.getFieldString("YD_STK_COL_ACT_STAT"));  //szYD_STK_COL_ACT_STAT
			String chkWBook 		= commUtils.trim(jsCarPoint.getFieldString("CHK_WBOOK"));            //szCHK_WBOOK
			String WlocCd 		    = commUtils.trim(jsCarPoint.getFieldString("WLOC_CD"));              //개소코드
			String ydPntCd 		    = commUtils.trim(jsCarPoint.getFieldString("YD_PNT_CD"));            //포인트
			if (!"".equals(sCarNo)){
				commUtils.printLog(logId, methodNm + "해당위치  야드포인트가 사용불가. 현재차량 ["+sCarNo+"]" , "SL");
				throw new Exception("해당위치  야드포인트가 사용불가. 현재차량 ["+sCarNo+"]");
			}	
			if (!"C".equals(ydStkColActStat)){
				commUtils.printLog(logId, methodNm + "해당위치  야드포인트가 사용불가." , "SL");
				throw new Exception("해당위치  야드포인트가 사용불가. 현재차량 ["+sCarNo+"]");
			}	
			if ("".equals(chkWBook)){
				commUtils.printLog(logId, methodNm + "해당위치  해당 작업예약이 없습니다." , "SL");
				throw new Exception("해당위치  해당 작업예약이 없습니다 현재차량 ["+sCarNo+"]");
			}	
			if (!"MT".equals(ydCarUseTypeGp)){
				commUtils.printLog(logId, methodNm + "해당위치  현재 포인트 타입 ["+ydCarUseTypeGp+"]" , "SL");
				throw new Exception("해당위치  현재 포인트 타입 ["+ydCarUseTypeGp+"] 현재차량 ["+sCarNo+"]");
    		}

			// 해당 포인트 점유
			jrParam.setField("YD_STK_COL_ACT_STAT" 	, "L");  			
			jrParam.setField("TRN_EQP_CD"      		, "");  			
			jrParam.setField("CAR_NO"      		    , CarNo);  			
			jrParam.setField("CARD_NO"      		, CarNo);  			
			jrParam.setField("YD_CAR_USE_GP"        , "G");
			jrParam.setField("STACK_COL_GP"      	, ydLoadLoc);  			
			jrParam.setField("YD_STK_COL_GP"      	, ydLoadLoc);  			
			jrParam.setField("YD_MAKECARPNT_CD"     , ydCarPntCd);  			
			
		    //차량 POINT TABLE 점유V_YD_STK_COL_ACT_STA
			/*  com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarpoint 
			UPDATE TB_YD_CARPOINT
			   SET YD_STK_COL_ACT_STAT=:V_YD_STK_COL_ACT_STAT
			     , TRN_EQP_CD =:V_TRN_EQP_CD
			     , CAR_NO =:V_CAR_NO
			     , CARD_NO =:V_CARD_NO
			     , MOD_DDTT=SYSDATE
			     , MODIFIER='포인트변경'
			 WHERE YD_STK_COL_GP=:V_YD_STK_COL_GP
			*/
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarpoint", logId, methodNm, "Car-Point 등록");

			/**********************************************************
			* 2. YD 저장위치 맵 활성화
			**********************************************************/			

	    	/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYmStackcol
	    	UPDATE TB_YM_STACKCOL 
	    	SET  
	    		 MOD_DDTT = SYSDATE             
	    		,MODIFIER = :V_MODIFIER   
	    		,STACK_COL_GP = :V_YD_STK_COL_GP       
	    		,STACK_COL_ACTIVE_STAT = :V_YD_STK_COL_ACT_STAT 
	    		,YD_CAR_USE_GP = :V_YD_CAR_USE_GP      
	    		,TRN_EQP_CD = :V_TRN_EQP_CD           
	    		,CAR_NO = :V_CAR_NO              
	    	WHERE STACK_COL_GP = :V_YD_STK_COL_GP
	    	*/
	    	
	    	commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYmStackcol", logId, methodNm, "TB_YM_STACKCOL 수정");
			
			/**********************************************************
			* 3. 적치베드 테이블에 활성상태 처리 
			**********************************************************/			

    		jrParam.setField("STACK_BED_WT_MAX", YmConstant.YD_STK_BED_WT_MAX_DEFAULT);
    		jrParam.setField("STACK_BED_ACTIVE_STAT", "L");
    		
    		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkbedYdStkColGp */

    		/*UPDATE USRYMA.TB_YM_STACKER
			   SET MOD_DDTT     = SYSDATE             
				 , MODIFIER     = :V_MODIFIER             
				 , STACK_BED_ACTIVE_STAT  = NVL(:V_STACK_BED_ACTIVE_STAT,STACK_BED_ACTIVE_STAT)
			     , STACK_BED_WT_MAX  = NVL(:V_STACK_BED_WT_MAX,STACK_BED_WT_MAX )
			  WHERE STACK_COL_GP  = :V_STACK_COL_GP	*/	
    		
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YM_STACKER 활성상태수정(E)");
			
			String ydSchCd   	 = "";
			String ydWbookId 	 = "";
			String transOrdDate  = "";
			String transOrdSeqNo = "";
			String ydCarSchId 	 = ""; 
			JDTORecordSet jsWBook = JDTORecordFactory.getInstance().createRecordSet("temp");
			JDTORecord jrWBook = JDTORecordFactory.getInstance().create();	
			if ("1".equals(CarUoDnGp)){ // 상차
				
				/**********************************************************
				* 4 상차작업
				*  4.1 차량도착위치  TB_YM_STACKLAYER CLEAR
				 * 4.2 차량 스케줄 생성
				 * 3. 차량재료 등록
				 * 4. 차량 예정정보 발송
				 * 5. 크레인스케줄 
				**********************************************************/			
				
	    		jrParam.setField("STACK_LAYER_ACTIVE_STAT", "E");
	    		jrParam.setField("STL_NO"				  , "");
	    		jrParam.setField("STACK_LAYER_STAT"       , "E");
	    		
	    		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColGpClear */
	
	    		/*UPDATE USRYMA.TB_YM_STACKLAYER           
				   SET MOD_DDTT     = SYSDATE             
					 , MODIFIER     = :V_MODIFIER             
					 , STACK_LAYER_ACTIVE_STAT  = :V_STACK_LAYER_ACTIVE_STAT 
				     , STOCK_ID  = null
				     , STACK_LAYER_STAT  = :V_STACK_LAYER_STAT
				 WHERE STACK_COL_GP = :V_STACK_COL_GP*/	
						    		
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YM_STACKLAYER 차량 적치단 정보 활성화(E)");
				
				/**********************************************************
				* 4.2 차량 스케줄 생성
				**********************************************************/
	
				ydSchCd  = "2"+ydLoadLoc.substring(1,2) + "PT03UM";
				
				jrParam.setField("YD_SCH_CD"        , ydSchCd);
				
//				2.2 생성한 스케줄코드로 작업 예약 select
				/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdWrkbookBySchCd
				SELECT A.* 
				       ,B.CTS_RELAY_BAY
				  FROM TB_YM_WRKBOOK A
				       , TB_YM_STOCK B
				       , TB_YM_WRKBOOKMTL C
				 WHERE 1=1
				   AND A.YD_WBOOK_ID = C.YD_WBOOK_ID 
				   AND B.STOCK_ID    = C.STOCK_ID
				   AND A.YD_SCH_CD   = :V_YD_SCH_CD
				   AND B.CAR_CARD_NO = :V_CAR_NO
				AND A.DEL_YN='N'
				AND B.DEL_YN='N'
				AND C.DEL_YN='N'
				AND NOT EXISTS(
				        SELECT *
				         FROM USRYMA.TB_YM_CRNSCH B
				        WHERE B.YD_GP= SUBSTR(A.YD_SCH_CD,1,1)
				          AND B.YD_SCH_CD LIKE '__PT__UM'
				          AND SUBSTR(B.YD_SCH_CD,6,3)=SUBSTR(A.YD_SCH_CD,6,3)
				          AND B.DEL_YN='N'
				)
				ORDER BY A.YD_WBOOK_ID,B.STOCK_ID
				*/
				jsWBook = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdWrkbookBySchCd", logId, methodNm, "작업예약정보조회"); 
				if (jsWBook.size() == 0) {
					m_ctx.setRollbackOnly();
					commUtils.printLog(logId, methodNm + "작업예약정보 없음 " +ydLoadLoc , "SL");
					throw new Exception("작업예약정보 없음  현재차량 ["+sCarNo+"]");
					
				} 
				
				jsWBook.first();
				jrWBook.setRecord(jsWBook.getRecord());
				
				mtlCnt = Integer.parseInt(commUtils.trim(jrWBook.getFieldString("CTS_RELAY_BAY"  )));
				JDTORecord jsTransSeq = JDTORecordFactory.getInstance().create();
				//운송지시일자, 순번 생성 
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getRetnTransOrdNoByCar 
				--AB 차량동간이적 운송지시일자, 순번 생성 
				SELECT A.TRANS_ORD_DATE
				     , A.TRANS_ORD_SEQNO + 1 AS TRANS_ORD_SEQNO
				  FROM (
				         SELECT TO_CHAR(SYSDATE,'YYYYMMDD') AS TRANS_ORD_DATE
				              , NVL(MAX(TRANS_ORD_SEQNO),988700) AS TRANS_ORD_SEQNO
				           FROM TB_YD_CARSCH
				          WHERE TRANS_ORD_DATE = TO_CHAR(SYSDATE,'YYYYMMDD')
				            AND TRANS_ORD_SEQNO >  988700
				            AND TRANS_ORD_SEQNO <=  988999
				        ) A
				 */       
				JDTORecordSet jrTransSeq = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getRetnTransOrdNoByCar", logId, methodNm, "운송지시정보조회"); 
				if (jrTransSeq.size() == 0) {
					commUtils.printLog(logId, methodNm + "운송지시일자,순번  생성시  오류발생 " +ydLoadLoc , "SL");
					throw new Exception("운송지시일자,순번  생성시  오류발생 " +ydLoadLoc + " 현재차량 ["+sCarNo+"]");
					
				} 
				jrTransSeq.first();
				jsTransSeq		= jrTransSeq.getRecord();
				
				transOrdDate  = commUtils.trim(jsTransSeq.getFieldString("TRANS_ORD_DATE"));
				transOrdSeqNo = commUtils.trim(jsTransSeq.getFieldString("TRANS_ORD_SEQNO"));

				ydCarSchId = commDao.getSeqId(logId, methodNm, "CarSch");				
				
				JDTORecord jrInParam = JDTORecordFactory.getInstance().create();
				jrInParam.setField("YD_CAR_SCH_ID"			, ydCarSchId); //야드차량스케쥴ID
				jrInParam.setField("MODIFIER"				, modifier);
				jrInParam.setField("YD_EQP_ID"				, YmConstant.YD_DM_CAR_EQP_ID);				//야드설비ID 
				jrInParam.setField("YD_CAR_USE_GP"			, YmConstant.YD_CAR_USE_GP_DM);				//차량사용구분('G': 출하?)
				jrInParam.setField("CAR_NO"					, CarNo);							 		//운송장비코드
				jrInParam.setField("CAR_KIND"				, "TR");							 		//차량종류
				jrInParam.setField("YD_EQP_WRK_STAT"		, "U");										//야드설비작업상태
				
				jrInParam.setField("CARD_NO"				, CarNo);							 		//운송장비코드
				jrInParam.setField("SPOS_WLOC_CD"			, WlocCd);									//발지개소코드
				jrInParam.setField("ARR_WLOC_CD"			, WlocCd);									//착지개소코드
				
				jrInParam.setField("YD_CARLD_LEV_LOC"		, ydLoadLoc);								//야드상차출발위치
				jrInParam.setField("YD_PNT_CD1"				, ydPntCd);
				jrInParam.setField("YD_CARLD_WRK_BOOK_ID"	, ydWbookId);     			 				//상차 작업예약ID
				jrInParam.setField("YD_CARLD_STOP_LOC"		, ydLoadLoc);								//야드상차정지위치 (직상차 제외)

				jrInParam.setField("YD_CAR_PROG_STAT"		, YmConstant.YD_CAR_PROG_STAT_2);			//상차출발상태
				jrInParam.setField("TRANS_ORD_DATE"			, transOrdDate);							//운송지시번호
				jrInParam.setField("TRANS_ORD_SEQNO"		, transOrdSeqNo);							//운송지시번호
				jrInParam.setField("YD_BAYIN_WO_SEQ"		, "9");										//입동지시순번 - 기본값으로 설정(9)
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarSch 
				INSERT INTO TB_YD_CARSCH(

				       YD_CAR_SCH_ID
				     , REGISTER
				     , REG_DDTT
				     , MODIFIER
				     , MOD_DDTT
				     , DEL_YN
				     , YD_EQP_ID
				     , YD_CAR_USE_GP
				     , CAR_NO
				     , CAR_KIND
				     , YD_EQP_WRK_STAT      --U:상차, L:하차
				     
				     , SPOS_WLOC_CD
				     , ARR_WLOC_CD
				     , YD_CARLD_LEV_LOC     --야드상차출발위치
				     , YD_CARLD_LEV_DT      --야드상차출발일시
				     , YD_CARLD_PNT_WO_DT   --야드상차포인트지시일시
				     , YD_PNT_CD1           --야드상차포인트(4자리)
				     , YD_CARLD_WRK_BOOK_ID --야드상차작업예약ID
				     , YD_CARLD_STOP_LOC    --야드상차정지위치(적치열6자리)
				     , YD_CARLD_ARR_DT      --야드상차도착일시

				     , YD_CARUD_LEV_DT      --야드하차출발일시
				     , YD_CARUD_PNT_WO_DT   --야드하차Point지시일시
				     , YD_PNT_CD3           --야드하차포인트(4자리)
				     , YD_CARUD_WRK_BOOK_ID --야드하차작업예약ID
				     , YD_CARUD_STOP_LOC    --야드하차정지위치(적치열6자리)
				     , YD_CARUD_ARR_DT      --야드하차도착일시
				     , YD_CAR_PROG_STAT     --야드차량진행상태
				     , TRANS_ORD_DATE
				     , TRANS_ORD_SEQNO 
				     , YD_BAYIN_WO_SEQ
				    
				) SELECT :V_YD_CAR_SCH_ID
				       , :V_MODIFIER
				       , SYSDATE
				       , :V_MODIFIER
				       , SYSDATE
				       , 'N'
				       , :V_YD_EQP_ID
				       , :V_YD_CAR_USE_GP
				       , :V_CAR_NO
				       , :V_CAR_KIND
				       , :V_YD_EQP_WRK_STAT
				       , :V_SPOS_WLOC_CD
				       , :V_ARR_WLOC_CD
				       -- 상차정보
				       , DECODE(:V_YD_EQP_WRK_STAT,'U',:V_YD_CARLD_LEV_LOC,NULL) --야드상차출발위치
				       , DECODE(:V_YD_EQP_WRK_STAT,'U',SYSDATE,NULL)
				       , DECODE(:V_YD_EQP_WRK_STAT,'U',SYSDATE,NULL)
				       , DECODE(:V_YD_EQP_WRK_STAT,'U',:V_YD_PNT_CD1,NULL)
				       , DECODE(:V_YD_EQP_WRK_STAT,'U',:V_YD_CARLD_WRK_BOOK_ID,NULL)
				       , DECODE(:V_YD_EQP_WRK_STAT,'U',:V_YD_CARLD_STOP_LOC,NULL)
				       , DECODE(:V_YD_EQP_WRK_STAT,'U',SYSDATE,NULL)   --야드상차도착일시
				       --하차정보
				       , DECODE(:V_YD_EQP_WRK_STAT,'L',SYSDATE,NULL)
				       , DECODE(:V_YD_EQP_WRK_STAT,'L',SYSDATE,NULL)
				       , DECODE(:V_YD_EQP_WRK_STAT,'L',:V_YD_PNT_CD3,NULL)
				       , DECODE(:V_YD_EQP_WRK_STAT,'L',:V_YD_CARUD_WRK_BOOK_ID,NULL)
				       , DECODE(:V_YD_EQP_WRK_STAT,'L',:V_YD_CARUD_STOP_LOC,NULL)
				       , DECODE(:V_YD_EQP_WRK_STAT,'L',SYSDATE,NULL)   --야드하차도착일시
				       , :V_YD_CAR_PROG_STAT
				       , :V_TRANS_ORD_DATE
				       , :V_TRANS_ORD_SEQNO 
				       , :V_YD_BAYIN_WO_SEQ
				    FROM DUAL

			*/	    
				
				commDao.insert(jrInParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarSch", logId, methodNm, "차량스케쥴 상차도착 으로 INSERT ");	
				
				if(mtlCnt > jsWBook.size() ){
					mtlCnt = jsWBook.size();	
				}
				/**********************************************************
				* 4.3 차량 스케쥴 재료 등록
				**********************************************************/				
				// 상차 가능 매수  수만큼 루프
				for(int Loop_i = 1; Loop_i <= mtlCnt ; Loop_i++) {
					jsWBook.absolute(Loop_i);
					jrWBook = jsWBook.getRecord();
						
					//------------------------------------------------------------------------------------------------------
					// 차량이송재료 등록
					//------------------------------------------------------------------------------------------------------
					JDTORecord inRecord = JDTORecordFactory.getInstance().create();
					inRecord.setField("YD_CAR_SCH_ID"		, ydCarSchId);//스케줄코드
					inRecord.setField("STL_NO"				, jrWBook.getFieldString("STOCK_ID"));
					inRecord.setField("YD_STK_BED_NO"		, "01");
					inRecord.setField("YD_STK_LYR_NO"		, "00"+Loop_i);
					inRecord.setField("MODIFIER"			, modifier);
				
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarSchmtl
					INSERT INTO TB_YD_CARFTMVMTL(
					       YD_CAR_SCH_ID
					     , STL_NO
					     , REGISTER
					     , REG_DDTT
					     , MODIFIER
					     , MOD_DDTT
					     , DEL_YN
					--     , YD_CAR_UPP_LOC_CD
					     , YD_STK_BED_NO
					     , YD_STK_LYR_NO
					--     , HCR_GP
					--     , STL_PROG_CD
					--     , YD_MTL_ITEM
					--     , YD_ROUTE_GP
					     ) 
					VALUES ( 
					       :V_YD_CAR_SCH_ID
					     , :V_STL_NO
					     , :V_MODIFIER
					     , SYSDATE
					     , :V_MODIFIER
					     , SYSDATE
					     , 'N'
					     , :V_YD_STK_BED_NO
					     , :V_YD_STK_LYR_NO
					)
				   */	    
					
					commDao.insert(inRecord, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarSchmtl", logId, methodNm, "차량재료 스케쥴 INSERT ");
					
					/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStockTransOrdNo 
					UPDATE TB_YM_STOCK
					   SET MODIFIER   = :V_MODIFIER
					     , MOD_DDTT   = SYSDATE 
					     , TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE2
					     , TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO2
					WHERE STOCK_ID = :V_STOCK_ID
					*/
					
					inRecord.setField("STOCK_ID"		 	, jrWBook.getFieldString("STOCK_ID"));
					inRecord.setField("TRANS_ORD_DATE2"		, transOrdDate);
					inRecord.setField("TRANS_ORD_SEQNO2"	, transOrdSeqNo);
					commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStockTransOrdNo", logId, methodNm, "저장품운송지시번호 변경");
				}				
			} else {
				
				/**********************************************************
				* 4 하차작업
				*  1. 차량 스케줄 생성
				 * 2. 작업예약 선택
				 * 3. 차량재료 등록
				 * 4. 차량 예정정보 발송
				 * 5. 크레인스케줄 
				**********************************************************/						
				//ydSchCd = ydLoadLoc.substring(0 , 2)+"PT07LM";
				ydSchCd  = "2"+ydLoadLoc.substring(1,2) + "PT03LM";
				jrParam.setField("YD_SCH_CD"        , ydSchCd);
				
//				2.2 생성된 스케줄코드로 작업 예약 select
				/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdWrkbookBySchCd2
				SELECT A.* 
				     , B.CTS_RELAY_BAY
				     , B.STOCK_ID 
				  FROM TB_YM_WRKBOOK A
				     , TB_YM_STOCK B
				     , TB_YM_WRKBOOKMTL C
				 WHERE 1=1
				   AND A.YD_WBOOK_ID = C.YD_WBOOK_ID 
				   AND B.STOCK_ID    = C.STOCK_ID
				   AND A.YD_SCH_CD   = :V_YD_SCH_CD
				   AND B.CAR_CARD_NO = :V_CAR_NO
				   AND A.DEL_YN='N'
				   AND B.DEL_YN='N'
				   AND C.DEL_YN='N'
				   AND NOT EXISTS(
				        SELECT *
				         FROM USRYMA.TB_YM_CRNSCH B
				        WHERE B.YD_GP= SUBSTR(A.YD_SCH_CD,1,1)
				          AND B.YD_SCH_CD LIKE '__PT__LM'
				          AND SUBSTR(B.YD_SCH_CD,6,3)=SUBSTR(A.YD_SCH_CD,6,3)
				          AND B.DEL_YN='N'
				)
				ORDER BY A.YD_WBOOK_ID,B.STOCK_ID
				*/
				jsWBook = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdWrkbookBySchCd2", logId, methodNm, "작업예약정보조회"); 
				if (jsWBook.size() == 0) {
					commUtils.printLog(logId, methodNm + "작업예약정보 없음 " +ydLoadLoc , "SL");
					throw new Exception("작업예약정보 없음  현재차량 ["+sCarNo+"]");
					
				} 
				
				jsWBook.first();
				jrWBook.setRecord(jsWBook.getRecord());
				mtlCnt = Integer.parseInt(commUtils.trim(jrWBook.getFieldString("CTS_RELAY_BAY"  )));
				JDTORecord jsTransSeq = JDTORecordFactory.getInstance().create();
				//운송지시일자, 순번 생성 
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getRetnTransOrdNoByCar 
				--AB 차량동간이적 운송지시일자, 순번 생성 
				SELECT A.TRANS_ORD_DATE
				     , A.TRANS_ORD_SEQNO + 1 AS TRANS_ORD_SEQNO
				  FROM (
				         SELECT TO_CHAR(SYSDATE,'YYYYMMDD') AS TRANS_ORD_DATE
				              , NVL(MAX(TRANS_ORD_SEQNO),988700) AS TRANS_ORD_SEQNO
				           FROM TB_YD_CARSCH
				          WHERE TRANS_ORD_DATE = TO_CHAR(SYSDATE,'YYYYMMDD')
				            AND TRANS_ORD_SEQNO >  988700
				            AND TRANS_ORD_SEQNO <=  988999
				        ) A
				 */       
				JDTORecordSet jrTransSeq = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getRetnTransOrdNoByCar", logId, methodNm, "운송지시정보조회"); 
				if (jrTransSeq.size() == 0) {
					commUtils.printLog(logId, methodNm + "운송지시일자,순번  생성시  오류발생 " +ydLoadLoc , "SL");
					throw new Exception("운송지시일자,순번  생성시  오류발생 " +ydLoadLoc + " 현재차량 ["+sCarNo+"]");
					
				} 
				jrTransSeq.first();
				jsTransSeq		= jrTransSeq.getRecord();
				
				transOrdDate  = commUtils.trim(jsTransSeq.getFieldString("TRANS_ORD_DATE"));
				transOrdSeqNo = commUtils.trim(jsTransSeq.getFieldString("TRANS_ORD_SEQNO"));
	
				ydCarSchId = commDao.getSeqId(logId, methodNm, "CarSch");
				
				/**********************************************************
				* 4.2 차량 스케줄 생성
				**********************************************************/
				
				/***** 하차 작업 처리   ***************/
				JDTORecord jrInParam = JDTORecordFactory.getInstance().create();
				jrInParam.setField("YD_CAR_SCH_ID"			, ydCarSchId); //야드차량스케쥴ID
				jrInParam.setField("MODIFIER"				, modifier);
				jrInParam.setField("YD_EQP_ID"				, YmConstant.YD_DM_CAR_EQP_ID);				//야드설비ID 
				jrInParam.setField("YD_CAR_USE_GP"			, YmConstant.YD_CAR_USE_GP_DM);				//차량사용구분('G': 출하?)
				jrInParam.setField("CAR_NO"					, CarNo);							 		//운송장비코드
				jrInParam.setField("CAR_KIND"				, "TR");							 		//차량종류
				jrInParam.setField("YD_EQP_WRK_STAT"		, "L");										//야드설비작업상태
				
				jrInParam.setField("CARD_NO"				, CarNo);							 		//운송장비코드
				jrInParam.setField("SPOS_WLOC_CD"			, WlocCd);									//발지개소코드
				jrInParam.setField("ARR_WLOC_CD"			, WlocCd);									//착지개소코드
				
				jrInParam.setField("V_YD_CARUD_STOP_LOC"	, ydLoadLoc);								//야드하차출발위치
				jrInParam.setField("YD_PNT_CD3"				, ydPntCd);
				jrInParam.setField("YD_CARUD_WRK_BOOK_ID"	, ydWbookId);     			 				//하차 작업예약ID

				jrInParam.setField("YD_CAR_PROG_STAT"		, YmConstant.YD_CAR_PROG_STAT_B);			//하차상태
				jrInParam.setField("TRANS_ORD_DATE"			, transOrdDate);							//운송지시번호
				jrInParam.setField("TRANS_ORD_SEQNO"		, transOrdSeqNo);							//운송지시번호
				jrInParam.setField("YD_BAYIN_WO_SEQ"		, "9");										//입동지시순번 - 기본값으로 설정(9)
				
				commDao.insert(jrInParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarSch", logId, methodNm, "차량스케쥴 하차도착 으로 INSERT ");
	
				if(mtlCnt > jsWBook.size() ){
					mtlCnt = jsWBook.size();	
				}
				/**********************************************************
				* 3. 차량 스케쥴 재료 등록
				**********************************************************/				
				// 상차 가능 매수  수만큼 루프
				for(int Loop_i = 1; Loop_i <= mtlCnt ; Loop_i++) {
					jsWBook.absolute(Loop_i);
					jrWBook = jsWBook.getRecord();
						
					//------------------------------------------------------------------------------------------------------
					// 차량이송재료 등록
					//------------------------------------------------------------------------------------------------------
					JDTORecord inRecord = JDTORecordFactory.getInstance().create();
					inRecord.setField("YD_CAR_SCH_ID"		, ydCarSchId);//스케줄코드
					inRecord.setField("STL_NO"				, jrWBook.getFieldString("STOCK_ID"));
					inRecord.setField("YD_STK_BED_NO"		, "01");
					inRecord.setField("YD_STK_LYR_NO"		, "00"+Loop_i);
					inRecord.setField("MODIFIER"			, modifier);
				
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarSchmtl
					INSERT INTO TB_YD_CARFTMVMTL(
					       YD_CAR_SCH_ID
					     , STL_NO
					     , REGISTER
					     , REG_DDTT
					     , MODIFIER
					     , MOD_DDTT
					     , DEL_YN
					--     , YD_CAR_UPP_LOC_CD
					     , YD_STK_BED_NO
					     , YD_STK_LYR_NO
					--     , HCR_GP
					--     , STL_PROG_CD
					--     , YD_MTL_ITEM
					--     , YD_ROUTE_GP
					     ) 
					VALUES ( 
					       :V_YD_CAR_SCH_ID
					     , :V_STL_NO
					     , :V_MODIFIER
					     , SYSDATE
					     , :V_MODIFIER
					     , SYSDATE
					     , 'N'
					     , :V_YD_STK_BED_NO
					     , :V_YD_STK_LYR_NO
					)
				   */	    
					
					commDao.insert(inRecord, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarSchmtl", logId, methodNm, "차량재료 스케쥴 INSERT ");
					
					/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStockTransOrdNo
					UPDATE TB_YM_STOCK
					   SET MODIFIER   = :V_MODIFIER
					     , MOD_DDTT   = SYSDATE 
					     , TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE2
					     , TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO2
					WHERE STOCK_ID = :V_STOCK_ID
					*/
					
					inRecord.setField("STOCK_ID"		 	, jrWBook.getFieldString("STOCK_ID"));
					inRecord.setField("TRANS_ORD_DATE2"		, transOrdDate);
					inRecord.setField("TRANS_ORD_SEQNO2"	, transOrdSeqNo);
					commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStockTransOrdNo", logId, methodNm, "저장품운송지시번호 변경");
				
					// 하차인 경우: 저장위치에 재료 정보 SET
					JDTORecord recInTemp  = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("STACK_COL_GP"   		, ydLoadLoc);
			    	recInTemp.setField("STACK_BED_GP"   		, "01");
			    	recInTemp.setField("STACK_LAYER_GP" 		, jsWBook.getFieldString("STACK_LAYER_GP"));
			    	recInTemp.setField("STOCK_ID"       		, jsWBook.getFieldString("STOCK_ID"));
			    	recInTemp.setField("STACK_LAYER_ACTIVE_STAT", "E");
			    	recInTemp.setField("STACK_LAYER_STAT"       , "C");
			    	recInTemp.setField("MODIFIER"      			, modifier);
			    	
			    	/*
					UPDATE TB_YM_STACKLAYER            
					   SET MOD_DDTT     = SYSDATE             
					     , MODIFIER     = :V_MODIFIER             
					     , STACK_LAYER_ACTIVE_STAT = NVL(:V_STACK_LAYER_ACTIVE_STAT, STACK_LAYER_ACTIVE_STAT)
					     , STOCK_ID                = NVL(:V_STOCK_ID               , STOCK_ID)
					     , STACK_LAYER_STAT        = NVL(:V_STACK_LAYER_STAT       , STACK_LAYER_STAT)
					 WHERE STACK_COL_GP   = :V_STACK_COL_GP
					   AND STACK_BED_GP   = :V_STACK_BED_GP
					   AND STACK_LAYER_GP = :V_STACK_LAYER_GP 
			    	 */
					commDao.update(recInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColBedGp", logId, methodNm, "TB_YM_STACKLAYER 등록");
				}		
			}
			//차량예정정보 백업 송신
			//jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L008", jrParam));
			jrParam.setField("YD_CAR_SCH_ID"			, ydCarSchId); //야드차량스케쥴ID
			jrParam.setField("SEARCH_FLAG"      		, "2");  //1:상차도, 2:차량스케쥴 ID	
			jrRtn = commUtils.addSndData(jrRtn, ymComm.procCarPlanInfo(jrParam));
			
			/**********************************************************
			* 2.2 크레인스케줄 전문 호출
			**********************************************************/
			//------------------------------------------------------------------------------------------------------
			// 크레인스케줄기동
			//------------------------------------------------------------------------------------------------------

			jsWBook.first();
			jrWBook.setRecord(jsWBook.getRecord());			
			
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			jrYdMsg.setField("YD_WBOOK_ID"  , jrWBook.getFieldString("YD_WBOOK_ID")); //야드작업예약ID
			jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
			jrYdMsg.setField("YD_SCH_ST_GP" , "O"      ); //야드스케쥴기동구분
			jrYdMsg.setField("YD_SCH_REQ_GP", "L"      ); //야드스케쥴요청구분(인출)
			jrYdMsg.setField("MODIFIER"     , modifier ); //수정자

			jrRtn = commUtils.addSndData(jrRtn, ymComm.getCrnSchMsg(jrYdMsg));	

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 차량동간이적 도착(A8YML018)	
	
	/**		
	 *      [A] 오퍼레이션명 : Mill Slab loading Request(A8YML019) CTC보급요구
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML019(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "CTC 보급요구[BSlabL2RcvSeEJB.rcvA8YML019] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
	    JDTORecord jrParam	 = JDTORecordFactory.getInstance().create(); //Query 실행시 파라메터 전달용 JDTORecord 
	    JDTORecordSet rsResult    	= null;
	    JDTORecordSet rsResult2    	= null;
	    
	    String sYD_WRK_PLAN_CRN; //야드작업계획크레인
	    String sYD_CRN_SCH_ID; //야드크레인스케쥴ID
	    String sYD_SCH_CD; //야드스케쥴코드
		String sYD_TO_LOC_GUIDE = null;
	    
	    int iMaxRec = 1; //1매 작업
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "CTC보급요구(A8YML019) 수신 ", rcvMsg);
			
			//수신 항목 값
			String msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"A8YML019"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sSlabNo	    = commUtils.trim(rcvMsg.getFieldString("SLAB_NO"));  //SLABNo
			String sPosition  	= commUtils.trim(rcvMsg.getFieldString("POSITION")); //Position
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)

			if ("".equals(modifier)) { modifier = msgId; }
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명

			sYD_WRK_PLAN_CRN 	= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PLAN_CRN")); //화면단에서 실행할 때 전달됨, 전문에는 없는 항목임
			sYD_CRN_SCH_ID 		= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); //선작업 실행할 때 전달됨, 전문에는 없는 항목임
			sYD_SCH_CD 			= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD")); //선작업 실행할 때 전달됨, 전문에는 없는 항목임

			//스케줄코드, To위치 Guide 
			if("1".equals(sPosition)) {
				sYD_SCH_CD = "2ACT01UM";
			} else if("2".equals(sPosition)) { 
				sYD_SCH_CD = "2ACT01UM";
			} else if("4".equals(sPosition)) {
				sYD_SCH_CD = "2CCT01UM";
			}							
			
			double dmaxStackWt  = 53200;
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sPosition)) {
				
				if("".equals(sYD_CRN_SCH_ID)) {
					throw new Exception("POSITION 없음["+ sPosition + "]");
				} else {
					commUtils.printLog(logId, methodNm + " POSITION 없음["+ sPosition + "]" , "SL");
					return jrRtn;
				}
			}
			
			/******************************************************************
			* 2. TB_YM_EQUIP테이블의 HMI_STAT CHECK 
			*    Off : C 이면 보급요청이 들어와도 Skip함
			******************************************************************/
			if("1".equals(sPosition)) {
				jrParam.setField("EQUIP_GP"    , "2ACT01"); //CTC1
//			} else if("2".equals(sPosition)) { 
//				jrParam.setField("EQUIP_GP"    , "2ACT02"); //CTC2
			} else if("4".equals(sPosition)) {
				jrParam.setField("EQUIP_GP"    , "2CCT04"); //CTC4
			} else {
				
				if("".equals(sYD_CRN_SCH_ID)) {
					//throw new Exception("POSITION 값이 '1','2','4' 가 아닙니다! " + sPosition);
					throw new Exception("POSITION 값이 '1','4' 가 아닙니다! " + sPosition);
				} else {
					//commUtils.printLog(logId, methodNm + " POSITION 값이 '1','2','4' 가 아닙니다! " , "SL");
					commUtils.printLog(logId, methodNm + " POSITION 값이 '1','4' 가 아닙니다! " , "SL");
					return jrRtn;
				}				
			}
			/*
			 SELECT HMI_STAT ,WORK_MODE
   			   FROM TB_YM_EQUIP
              WHERE EQUIP_GP   =  :V_EQUIP_GP
			*/
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectEquipInfo", logId, methodNm, "설비상태조회");
			if(rsResult.size() > 0) {
				if("C".equals(rsResult.getRecord(0).getFieldString("HMI_STAT"))) {
					if("".equals(sYD_CRN_SCH_ID)) {
						throw new Exception(jrParam.getFieldString("EQUIP_GP")+" 스케줄 사용여부가 OFF로 되어있습니다!");
					} else {
						commUtils.printLog(logId, methodNm + jrParam.getFieldString("EQUIP_GP")+" 스케줄 사용여부가 OFF로 되어있습니다!" , "SL");
						return jrRtn;
					}					
				}
				dmaxStackWt = Double.parseDouble(commUtils.nvl(rsResult.getRecord(0).getFieldString("STACK_MAX_WT" ),"53200"));
			}			
			
			commUtils.printLog(logId, "[[[ CTC보급요구 STACK_MAX_WT : " + dmaxStackWt + " ]]]", "SL");
			
			//ctc#4 보급일 경우 WB에 재료번호가 남아 있으면 보급 스케줄이 생성되면 안된다.
			if("4".equals(sPosition)) {
			
				jrParam.setField("YD_GP" 	, YmConstant.YD_GP_2); 
				jrParam.setField("PROC_GP" 	, "W");
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWbTrackingStockList
				SELECT 
				       STACK_COL_GP
				      ,STACK_BED_GP
				      ,LOC_NO AS STACK_LAYER_GP
				      ,STL_NO AS STOCK_ID
				  FROM TB_YM_EQPTRACKING
				 WHERE YD_GP = :V_YD_GP
				   AND PROC_GP = :V_PROC_GP
				   AND STL_NO IS NOT NULL
				  ORDER BY STACK_COL_GP, STACK_BED_GP DESC, STACK_LAYER_GP DESC   */ 
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWbTrackingStockList", logId, methodNm, "WB트래킹정보조회");
				if(rsResult.size() > 0) {
					
					if("".equals(sYD_CRN_SCH_ID)) {
						throw new Exception("#4CTC보급요구=>WB 상에 재료가 존재합니다!!");
					} else {
						commUtils.printLog(logId, methodNm + " #4CTC보급요구=>WB 상에 재료가 존재합니다!!" , "SL");
						return jrRtn;
					}
				}				
			}

			/*************************************************************************************
			* 3. CTC 맵 정보 Clear (POSTION 별 STACKLAYER 의 STOCK_ID = '', STACK_LAYER_STAT ='E')    																				  
			*************************************************************************************/
			if("1".equals(sPosition)) {
				jrParam.setField("STACK_COL_GP"    , "2ACT01"); //CTC1
//			} else if("2".equals(sPosition)) { 
//				jrParam.setField("STACK_COL_GP"    , "2ACT02"); //CTC2
			} else if("4".equals(sPosition)) {
				jrParam.setField("STACK_COL_GP"    , "2CCT04"); //CTC4
			}			
			jrParam.setField("STACK_BED_GP"		, "01"); 
			jrParam.setField("STACK_LAYER_STAT1", "C");
			jrParam.setField("STACK_LAYER_STAT2", "E"); 
			jrParam.setField("STOCK_ID", ""); 
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo 
			UPDATE TB_YM_STACKLAYER
			   SET 
			       MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,STOCK_ID = :V_STOCK_ID
			      ,STACK_LAYER_STAT = :V_STACK_LAYER_STAT2
			 WHERE  STACK_COL_GP = :V_STACK_COL_GP
			   AND  STACK_BED_GP = :V_STACK_BED_GP
			   AND  STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT1 || '%' */
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo", logId, methodNm, "CTC 맵 정보 Clear");

			
			
			if("1".equals(sPosition)) {
				jrParam.setField("EQUIP_GP"    , "2ACT01"); //CTC1
//			} else if("2".equals(sPosition)) { 
//				jrParam.setField("EQUIP_GP"    , "2ACT02"); //CTC2
			} else if("4".equals(sPosition)) {
				jrParam.setField("EQUIP_GP"    , "2CCT04"); //CTC4
			}	
			/*
			 SELECT HMI_STAT ,WORK_MODE
   			   FROM TB_YM_EQUIP
              WHERE EQUIP_GP   =  :V_EQUIP_GP
			*/
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectEquipInfo", logId, methodNm, "설비상태조회");
			if(rsResult.size() > 0) {
				if("1".equals(rsResult.getRecord(0).getFieldString("WORK_MODE")) || "O".equals(rsResult.getRecord(0).getFieldString("WORK_MODE"))) {
					//WORK_MODE 1:선작업 실행, 2:대기 (구 O:선작업실행, C:대기)
					if("".equals(sYD_CRN_SCH_ID) &&"".equals(sYD_WRK_PLAN_CRN)) {
						//L2에서 전송한 전문 A8YML019 임 
						//선작업 실행일 경우는 L2에서 수신된 A8YML019를 처리 하지 않고 여기서 종료 함
						commUtils.printLog(logId, methodNm + jrParam.getFieldString("EQUIP_GP")+" 선작업 실행으로 되어있습니다! L2에서 수신된  A8YML019를 처리하지 않습니다!" , "SL");
						return jrRtn;
					}
				}
			}	
			
			
			/*******************************************************************************************
			* 4. 각 동 야드와 대차에 있는 SLAB 중에 보급대상 장입 Lot 에 해당하면서 작업예약 되어 있지 않으면 작업예약 생성
			* (현재 장입대상 순번이고 대차 또는 야드에 적치중인 SLAB)
			*******************************************************************************************/
			boolean isPreWork = false;
//			if("2".equals(sPosition)) { 
//				
//				//선작업지시 존재여부 체크 
//				//1.1	CTC보급 주크레인 정보가져오기
//				//1.2	주크레인이 CTC보급 스케쥴코드에 해당하는 스케쥴 정보 가져오기 없으면 IF문 빠져나옴
//				//1.3	스케쥴정보 상태 체크(UP지시,PUT지시)
//				//적치단 권하위치에 slab번호 , 상태 D UPDATE
//				//스케줄 권하위치를 2ACT02 01 로 UPDATE
//				//isPreWork = ture;
//			}
			
			
			
			//선작업지시 존재여부 체크
			jrParam.setField("YD_SCH_CD" 		, sYD_SCH_CD); 
			jrParam.setField("YD_CRN_SCH_ID"	, sYD_CRN_SCH_ID);
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch
			--권상,권하시 해당 크레인스케줄 id를 제외한 동일 스케줄 작업이 만들어져 있는지 조회
			SELECT CS.YD_CRN_SCH_ID
			      ,CS.YD_SCH_CD
			      ,CS.YD_UP_WO_LOC
			      ,CS.YD_DN_WO_LOC
			      ,CM.STOCK_ID
			      ,CM.YD_AID_WRK_YN
			FROM   TB_YM_CRNSCH CS
			      ,TB_YM_CRNWRKMTL CM
			WHERE  CS.DEL_YN = 'N'
			  AND  CM.DEL_YN = 'N'
			  AND  CS.YD_SCH_CD LIKE :V_YD_SCH_CD ||'%'
			  AND  CS.YD_CRN_SCH_ID NOT IN (NVL(:V_YD_CRN_SCH_ID,'1'))
			  AND  CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
			  AND  CM.YD_AID_WRK_YN = 'N'
			 */
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch", logId, methodNm, "선작업지시 존재여부 체크");
			
			if(rsResult.size() == 0) {
				
				if("".equals(sSlabNo)) {
					
					//장입 대상재 조회
					jrParam.setField("YD_GP" , YmConstant.YD_GP_2); 
					if("4".equals(sPosition)) {
						jrParam.setField("BAY_GP", YmConstant.BAY_GP_C);
					} else {
						jrParam.setField("BAY_GP", YmConstant.BAY_GP_A);
					}
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectWBSlabSearch_09 
					--장입대상순번이고, 대차, 야드에 적치중인 SLAB 조회
					SELECT STACKCOL.STACK_COL_GP
					      ,LAYER.STACK_BED_GP
					      ,LAYER.STACK_LAYER_GP
					      ,STOCK.STOCK_ID
					      ,STOCK.CHARGE_LOT_NO
					      ,(
					            SELECT WBMTL.YD_WBOOK_ID
					              FROM TB_YM_WRKBOOK    WB
					                  ,TB_YM_WRKBOOKMTL WBMTL
					             WHERE WB.YD_WBOOK_ID = WBMTL.YD_WBOOK_ID
					               AND WB.DEL_YN = 'N'
					               AND WBMTL.DEL_YN = 'N'
					               AND WB.YD_SCH_CD IN ('2ACT01UM', '2CCT01UM') --A동 CTC 보급, C동 CTC 보급, C동 W/B 보급
					               AND WB.YD_WBOOK_ID NOT IN (
					                      
					                        SELECT YD_WBOOK_ID 
					                          FROM TB_YM_CRNSCH 
					                         WHERE DEL_YN = 'N'
					                          AND  YD_SCH_CD IN ('2ACT01UM', '2CCT01UM') 
					              
					                   ) 
					               AND WBMTL.STOCK_ID = STOCK.STOCK_ID
					       ) AS YD_WBOOK_ID
					      ,SLABCOMM.SLAB_WT
					      ,SLABCOMM.SLAB_W
					       
					  FROM TB_YM_STACKCOL   STACKCOL
					      ,TB_YM_STACKLAYER LAYER
					      ,TB_YM_STOCK      STOCK
					      ,VW_YD_SLABCOMM   SLABCOMM
					      
					 WHERE STACKCOL.YD_GP = :V_YD_GP
					   AND STACKCOL.BAY_GP = :V_BAY_GP
					   AND STACKCOL.SECT_GP IN ('01','02','TC')
					   AND STACKCOL.STACK_COL_GP = LAYER.STACK_COL_GP
					   AND LAYER.STACK_LAYER_STAT IN ('C')
					   AND LAYER.STOCK_ID = STOCK.STOCK_ID
					   AND STOCK.CHARGE_LOT_NO IS NOT NULL
					   AND STOCK.STOCK_ID = SLABCOMM.SLAB_NO(+)
					   
					 ORDER BY CHARGE_LOT_NO 
							 ,STACK_COL_GP DESC 
							 ,STACK_BED_GP DESC 
							 ,STACK_LAYER_GP DESC   */
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectWBSlabSearch_09", logId, methodNm, "장입대상재조회");
					if(rsResult.size() <= 0) {
						if("".equals(sYD_CRN_SCH_ID)) {
							throw new Exception("CTC보급요구=>SLAB 장입 대상재 존재 안함.");
						} else {
							commUtils.printLog(logId, "CTC보급요구=>SLAB 장입 대상재 존재 안함." , "SL");
							return jrRtn;
						}						
					}
					
//					if("2".equals(sPosition)) {
//
//						//적치단 테이블에서 2ACT02열 01Bed 2단 정보를 읽어 온다.
//						jrParam.setField("STACK_COL_GP", "2ACT02"); 
//						jrParam.setField("STACK_BED_GP", "01"); 
//						jrParam.setField("STACK_LAYER_GP", "02"); 
//						/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStockIdByLoc 
//						SELECT  STOCK_ID
//						       ,STACK_LAYER_STAT
//						       ,STACK_LAYER_ACTIVE_STAT
//						       ,TRIM(TO_CHAR(STACK_BED_GP + 1,'00')) AS NEXT_BED_GP
//						  FROM  TB_YM_STACKLAYER
//						 WHERE  STACK_COL_GP = :V_STACK_COL_GP
//						   AND  STACK_BED_GP = :V_STACK_BED_GP
//						   AND  STACK_LAYER_GP LIKE :V_STACK_LAYER_GP || '%' */
//						rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStockIdByLoc", logId, methodNm, "W/B 01Bed 2단 정보 읽어오기 "); 
//						if(rsResult2.size() > 0) {
//							if("C".equals(rsResult2.getRecord(0).getFieldString("STACK_LAYER_ACTIVE_STAT"))) {
//								//2ACT02열 01Bed 2단 이 비활성(C)이면 1단만 사용함으로 작업매수 1로 설정
//								iMaxRec = 1;
//							} else {
//								iMaxRec = 2;
//							}
//						} 			
//						
//					} else {
						iMaxRec = 1;
//					}
					
				} else {
					//파라메터로 SLAB_NO가 전달 되었을 때... 무조건 1매 작업
					jrParam.setField("STOCK_ID"	, sSlabNo);
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYmStockInfo 
					SELECT A.*
					  FROM TB_YM_STOCK A
					 WHERE STOCK_ID = :V_STOCK_ID */
					JDTORecordSet rsResult3 = bSlabDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYmStockInfo", logId, methodNm, "SLAB_NO로 TB_YM_STOCK를 조회");
					
					if(rsResult3.size() <= 0) {
						if("".equals(sYD_CRN_SCH_ID)) {
							throw new Exception("CTC보급요구=>SLAB 장입 대상재 존재 안함.");
						} else {
							commUtils.printLog(logId, "CTC보급요구=>SLAB 장입 대상재 존재 안함." , "SL");
							return jrRtn;
						}						
					}
					
					iMaxRec = 1;
				}
				

				

				if(iMaxRec == 2 && rsResult.size() > 1) {
					//작업매수 2매 일경우 
					// - 적치열, 적치Bed 가 동일하고, 장입순번이 같거나
					//   장입순번 값이 적은 값이 상단에 있고 , 하단에 다음 장입순번이 있으면 2매 작업 가능
					// - 위 조건에 충족하더라도 2매 작업중량이 > 53200 이거나 폭차이가 > 20.0 이면 1매만 작업
					//  폭이 2000 이상이면 1매 작업
					if(rsResult.getRecord(0).getFieldString("STACK_COL_GP").equals(rsResult.getRecord(1).getFieldString("STACK_COL_GP")) 
					   && rsResult.getRecord(0).getFieldString("STACK_BED_GP").equals(rsResult.getRecord(1).getFieldString("STACK_BED_GP"))) { //적치열, 적치Bed 가 동일
						
						double dUpperSlabWt = Double.parseDouble(commUtils.nvl(rsResult.getRecord(0).getFieldString("SLAB_WT"),"0")); //상단 Slab 중량
						double dLowerSlabWt = Double.parseDouble(commUtils.nvl(rsResult.getRecord(1).getFieldString("SLAB_WT"),"0")); //하단 Slab 중량
						commUtils.printLog(logId, "=======:::: 상단 SLAB 중량 : " +  dUpperSlabWt , "SL");
						commUtils.printLog(logId, "=======:::: 하단 SLAB 중량 : " +  dLowerSlabWt , "SL");
						
						double dUpperSlabW = Double.parseDouble(commUtils.nvl(rsResult.getRecord(0).getFieldString("SLAB_W"),"0")); //상단 Slab 폭
						double dLowerSlabW = Double.parseDouble(commUtils.nvl(rsResult.getRecord(1).getFieldString("SLAB_W"),"0")); //하단 Slab 폭
						commUtils.printLog(logId, "=======:::: 상단 SLAB 폭 : " +  dUpperSlabW , "SL");
						commUtils.printLog(logId, "=======:::: 하단 SLAB 폭 : " +  dLowerSlabW , "SL");
						
						double dRuleSlabW = Double.parseDouble(commUtils.nvl(rsResult.getRecord(0).getFieldString("WID_DIF"),"0")); //폭기준
						commUtils.printLog(logId, "=======:::: 폭기준 : " +  dRuleSlabW , "SL");
						
						
						if(dUpperSlabWt + dLowerSlabWt > dmaxStackWt) {
							iMaxRec = 1;
							commUtils.printLog(logId, "=======>>>> dUpperSlabWt + dLowerSlabWt > 53200 에 의해 iMaxRec = " +  iMaxRec , "SL");
						} else if(dUpperSlabW - dLowerSlabW > dRuleSlabW) {
							iMaxRec = 1;
							commUtils.printLog(logId, "=======>>>> dUpperSlabW - dLowerSlabW > " + dRuleSlabW + " 에 의해 iMaxRec = " +  iMaxRec , "SL");
						} else if(dUpperSlabW >= 2000) {
							iMaxRec = 1;
							commUtils.printLog(logId, "=======>>>> dUpperSlabW >= 2000 에 의해 iMaxRec = " +  iMaxRec , "SL");
						} else if(dLowerSlabW >= 2000) {
							iMaxRec = 1;
							commUtils.printLog(logId, "=======>>>> dLowerSlabW >= 2000 에 의해 iMaxRec = " +  iMaxRec , "SL");
						} else {
							
							int iUpperSlabChargeLotNo = Integer.parseInt(commUtils.nvl(rsResult.getRecord(0).getFieldString("CHARGE_LOT_NO"),"0")); //상단 Slab 장입순번
							int iLowerSlabCHargeLotNo = Integer.parseInt(commUtils.nvl(rsResult.getRecord(1).getFieldString("CHARGE_LOT_NO"),"0")); //하단 Slab 장입순번
							
							commUtils.printLog(logId, "=======|||| 상단 SLAB 장입순위 : " +  iUpperSlabChargeLotNo , "SL");
							commUtils.printLog(logId, "=======|||| 하단 SLAB 장입순위 : " +  iLowerSlabCHargeLotNo , "SL");
							
							if(iUpperSlabChargeLotNo == iLowerSlabCHargeLotNo) { 
								//장입순번이 같음
								iMaxRec = 2;
								commUtils.printLog(logId, "=======>>>> 상단,하단 장입순위 같음  iMaxRec = " +  iMaxRec , "SL");
								
							} else { 
								//장입순번이 다름
								
								if(iUpperSlabChargeLotNo + 1 == iLowerSlabCHargeLotNo ) {
									//장입순번이 상단 하단 연속으로 되어 있으면 2매 작업 가능
									iMaxRec = 2;
									commUtils.printLog(logId, "=======>>>> 상단,하단 장입순위 다르지만 연속으로 되어 있음  iMaxRec = " +  iMaxRec , "SL");
								} else {
									iMaxRec = 1;
								}
							}
							
							if(iMaxRec == 2) {
								//장입순번이 같거나 상단 하단 연속으로 되어 있더라도 
								//위치(열,BED)가 틀리거나 단이 연속으로 되어 있지 않을 경우 1매 작업
								
								//동일BED 체크
								String dUpperBedGp = rsResult.getRecord(0).getFieldString("STACK_COL_GP") + rsResult.getRecord(0).getFieldString("STACK_BED_GP"); //상단 BED
								String dLowerBedGp = rsResult.getRecord(1).getFieldString("STACK_COL_GP") + rsResult.getRecord(1).getFieldString("STACK_BED_GP"); //하단 BED
								commUtils.printLog(logId, "=======:::: 상단 열+BED : " +  dUpperBedGp , "SL");
								commUtils.printLog(logId, "=======:::: 하단 열+BED : " +  dLowerBedGp , "SL");
								
								//연속단 체크 
								int iUpperLayerGp = Integer.parseInt(commUtils.nvl(rsResult.getRecord(0).getFieldString("STACK_LAYER_GP"),"0")); //상단 Layer_Gp
								int iLowerLayerGp = Integer.parseInt(commUtils.nvl(rsResult.getRecord(1).getFieldString("STACK_LAYER_GP"),"0")); //하단 Layer_Gp
								commUtils.printLog(logId, "=======:::: 상단 Layer_Gp : " +  iUpperLayerGp , "SL");
								commUtils.printLog(logId, "=======:::: 하단 Layer_Gp : " +  iLowerLayerGp , "SL");
								
								//동일BED 체크
								if(dUpperBedGp.equals(dLowerBedGp)) {
									
									if(iUpperLayerGp == iLowerLayerGp + 1) {
										iMaxRec = 2;
									} else {
										iMaxRec = 1;
										commUtils.printLog(logId, "=======>>>> 상단,하단 단이 연속이 아님  iMaxRec = " +  iMaxRec , "SL");
									}
									
								} else {
									iMaxRec = 1;
									commUtils.printLog(logId, "=======>>>> 상단,하단 위치(열,BED)가 다름#2  iMaxRec = " +  iMaxRec , "SL");
								}
							}
						}
						
					} else {
						//1매만 작업
						iMaxRec = 1;
						commUtils.printLog(logId, "=======>>>> 상단,하단 위치(열,BED)가 다름#1  iMaxRec = " +  iMaxRec , "SL");
					}
					
				} else {
					//1매만 작업
					iMaxRec = 1;
				} 
				
				String sYD_SCH_PRIOR = null;
				String sYD_WBOOK_ID = rsResult.getRecord(0).getFieldString("YD_WBOOK_ID");
				
				if("".equals(sYD_WBOOK_ID)) {
					
					//스케줄코드, To위치 Guide 
					if("1".equals(sPosition)) {
						sYD_SCH_CD = "2ACT01UM";
						sYD_TO_LOC_GUIDE = "2ACT010101"; //sYD_TO_LOC_GUIDE = "2ACT010101";
//					} else if("2".equals(sPosition)) { 
//						sYD_SCH_CD = "2ACT01UM";
//						sYD_TO_LOC_GUIDE = "2ACT020101"; //sYD_TO_LOC_GUIDE = "2ACT020101";
					} else if("4".equals(sPosition)) {
						sYD_SCH_CD = "2CCT01UM";
						sYD_TO_LOC_GUIDE = "2CCT040101"; //sYD_TO_LOC_GUIDE = "2CCT040101";
					}							
				
					//스케줄코드로 스케줄기준Table조회
					jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
			    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
					SELECT YD_SCH_CD
					     , YD_WRK_CRN
					     , YD_WRK_CRN_PRIOR
					  FROM TB_YM_SCHEDULERULE
					 WHERE DEL_YN = 'N'
					   AND YD_SCH_CD = :V_YD_SCH_CD
					*/   
					rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
			    	
					if (rsResult2 != null && rsResult2.size() > 0) {
						sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
						sYD_WRK_PLAN_CRN = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN");
					} else {
						if("".equals(sYD_CRN_SCH_ID)) {
							throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sYD_SCH_CD + "]");
						} else {
							commUtils.printLog(logId, "B열연 코일 스케쥴 코드 이상 : [" + sYD_SCH_CD + "]" , "SL");
							return jrRtn;
						}
					}			
					
					//작업예약ID생성
					sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
				
					/**********************************************************
					* 2-1. 작업예약(TB_YM_WRKBOOK) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
					jrParam.setField("YD_GP"			, YmConstant.YD_GP_2);
					jrParam.setField("YD_BAY_GP"		, sYD_SCH_CD.substring(1,2));
					jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD); //야드스케쥴코드
					jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
					jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
					jrParam.setField("YD_TO_LOC_GUIDE"	, sYD_TO_LOC_GUIDE); //야드To위치Guide
					if(!"".equals(sYD_WRK_PLAN_CRN)) {
						jrParam.setField("YD_WRK_PLAN_CRN"	, sYD_WRK_PLAN_CRN); //야드작업계획크레인
					}
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
					
					/**********************************************************
					* 2-2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
					**********************************************************/
					for(int ii = 0; ii < iMaxRec; ii++) {
						jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
						jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STOCK_ID")));
						jrParam.setField("STACK_COL_GP"		, commUtils.trim(rsResult.getRecord(ii).getFieldString("STACK_COL_GP")));
						jrParam.setField("STACK_BED_GP"		, commUtils.trim(rsResult.getRecord(ii).getFieldString("STACK_BED_GP")));
						jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(rsResult.getRecord(ii).getFieldString("STACK_LAYER_GP")));
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
						
						/**********************************************************
						* 2-3. TB_YM_STOCK의 저장품 이동조건 등록
						**********************************************************/
						jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STOCK_ID")));
						jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_FS ); //압연작업대기
						
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 저장품 이동 조건 UPDATE");
					}
				}
				
				//스케줄 메인 호출
				JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
				jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
				jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
				jrCrnSchMsg.setField("YD_WBOOK_ID"  		, sYD_WBOOK_ID); //작업예약ID
				jrCrnSchMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
				jrCrnSchMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
				jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);		
				
				//Slab Schedule EJB Call
				//jrParam.setField("JMS_TC_CD"    , "YMYMJ202"); 
				//jrParam.setField("YD_WBOOK_ID"  , sYD_WBOOK_ID); //야드작업예약ID
				//jrParam.setField("YD_SCH_CD"    , ""  ); //야드스케쥴코드
				//jrParam.setField("YD_EQP_ID"    , ""  ); //야드설비ID
				
				
				//EJBConnector ejbConn = new EJBConnector("default","BSlabSchSeEJB",this);
				//jrRtn = (JDTORecord)ejbConn.trx("procYMYMJ202",new Class[]{JDTORecord.class},new Object[]{ jrParam });			
				
			} else {
				//선작업 스케줄이 있을 경우에
				
			}
			
			commUtils.printLog(logId, methodNm, "S-");
		
			return jrRtn;
		}catch(DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of Mill Slab loading Request(A8YML019) CTC보급요구
	
	/**
	 *      [A] 오퍼레이션명 : W/B 장입요구(A8YML020)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML020(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "W/B 장입요구[BSlabL2RcvSeEJB.rcvA8YML020] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn 		= JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam		= JDTORecordFactory.getInstance().create(); //Query 실행시 파라메터 전달용 JDTORecord 
	    JDTORecordSet rsResult  = null;
	    JDTORecordSet rsResult2 = null;
	    
	    int iMaxRec = 1; //1매 작업
		
	    String msgId;
	    String modifier;
	    String sYD_WRK_PLAN_CRN; //야드작업계획크레인
	    String sYD_CRN_SCH_ID; //야드크레인스케쥴ID
	    String sYD_SCH_CD; //야드스케쥴코드
		
	    try{
			commUtils.printLog(logId, methodNm, "S+");
	    	
			msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"A8YML020"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
			
			sYD_WRK_PLAN_CRN 	= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PLAN_CRN")); //화면단에서 실행할 때 전달됨, 전문에는 없는 항목임
			sYD_CRN_SCH_ID 		= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); //선작업 실행할 때 전달됨, 전문에는 없는 항목임
			sYD_SCH_CD 			= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD")); //선작업 실행할 때 전달됨, 전문에는 없는 항목임

			if("".equals(sYD_SCH_CD)) {
				sYD_SCH_CD = "2CWB01UM";
			}
			
			double dmaxStackWt  = 53200;
			
			String sYD_WRK_CRN = "2CCRC2";
			
			/******************************************************************
			* 1. C동 W/B 보급 작업 크레인 을 TB_YM_SCHEDULERULE 에서 가져 온다.
			******************************************************************/
			jrParam.setField("YD_SCH_CD"    , sYD_SCH_CD);  //"2CWB01UM"
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule
			SELECT YD_SCH_CD
			     , YD_WRK_CRN
			     , YD_WRK_CRN_PRIOR
			     , YD_MULTI_WRK_YN
			  FROM TB_YM_SCHEDULERULE
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = :V_YD_SCH_CD  */ 
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄기준 조회");
			if(rsResult.size() > 0) {
				sYD_WRK_CRN = rsResult.getRecord(0).getFieldString("YD_WRK_CRN"); //C1,C2,C3 중 하나
			} else {
				throw new Exception("TB_YM_SCHEDULERULE(스케줄기준)테이블에서 W/B 보급 스케줄 정보를 찾지 못했습니다!!");
			}
			/******************************************************************
			* 2. 해당 크레인의 STACK_MAX_WT 적채 최대 중량을 TB_YM_EQUIP 에서 가져온다.
			******************************************************************/
			jrParam.setField("EQUIP_GP"    , sYD_WRK_CRN); //W/B
			/*
			 SELECT STACK_MAX_WT
   			   FROM TB_YM_EQUIP
              WHERE EQUIP_GP   =  :V_EQUIP_GP
			*/
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectEquipInfo", logId, methodNm, "설비테이블조회");
			if(rsResult.size() > 0) {
				dmaxStackWt = Double.parseDouble(commUtils.nvl(rsResult.getRecord(0).getFieldString("STACK_MAX_WT" ),"53200"));
			}
			
			commUtils.printLog(logId, "[[[ W/B 장입 크레인 : " + sYD_WRK_CRN + " , 장입 중량 STACK_MAX_WT : " + dmaxStackWt + " ]]]", "SL");
			
			//적치단 WB 01BED 정보 Clear
			jrParam.setField("STACK_COL_GP"    	, "2CWB01"	);
			jrParam.setField("STACK_BED_GP"    	, "01"		);
			jrParam.setField("STACK_LAYER_STAT1", "C"		);
			jrParam.setField("STACK_LAYER_STAT2", "E"		);
			jrParam.setField("STOCK_ID"			, ""		);
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo
			UPDATE TB_YM_STACKLAYER
			   SET 
			       MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,STOCK_ID = :V_STOCK_ID
			      ,STACK_LAYER_STAT = :V_STACK_LAYER_STAT2
			 WHERE  STACK_COL_GP = :V_STACK_COL_GP
			   AND  STACK_BED_GP = :V_STACK_BED_GP
			   AND  STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT1 || '%' */
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo", logId, methodNm, "적치단 WB 01BED 정보 Clear");
			
			
			/******************************************************************
			* 3. TB_YM_EQUIP테이블의 HMI_STAT , WORK_MODE CHECK 
			*    - HMI_STAT  : 스케줄사용여부(C:사용안함,O:스케줄사용)
			*    - WORK_MODE : 선작업지시실행여부(C:선작업실행안함,O:선작업실행)
			******************************************************************/
			jrParam.setField("EQUIP_GP"    , "2CWB01"); //W/B
			/*
			 SELECT HMI_STAT ,WORK_MODE
   			   FROM TB_YM_EQUIP
              WHERE EQUIP_GP   =  :V_EQUIP_GP
			*/
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectEquipInfo", logId, methodNm, "W/B 설비상태조회");
			if(rsResult.size() > 0) {
				
				if("C".equals(rsResult.getRecord(0).getFieldString("HMI_STAT"))) {
					if("".equals(sYD_CRN_SCH_ID)) {
						throw new Exception(jrParam.getFieldString("EQUIP_GP")+" 스케줄 사용여부가 OFF로 되어있습니다!");
					} else {
						commUtils.printLog(logId, methodNm + jrParam.getFieldString("EQUIP_GP")+" 스케줄 사용여부가 OFF로 되어있습니다!" , "SL");
						return jrRtn;
					}
				}
				
				if("1".equals(rsResult.getRecord(0).getFieldString("WORK_MODE"))) {
					//WORK_MODE 1:선작업 실행, 2:대기
					if("".equals(sYD_CRN_SCH_ID) &&"".equals(sYD_WRK_PLAN_CRN)) {
						//L2에서 전송한 전문 A8YML020 임 
						//선작업 실행일 경우는 L2에서 수신된 A8YML020을 처리 하지 않고 여기서 종료 함
						commUtils.printLog(logId, methodNm + jrParam.getFieldString("EQUIP_GP")+" 선작업 실행으로 되어있습니다! L2에서 수신된  A8YML020를 처리하지 않습니다!" , "SL");
						return jrRtn;
					}
				}
			}	
			
			
			//선작업지시 존재여부 체크
			jrParam.setField("YD_SCH_CD" 		, sYD_SCH_CD); 
			jrParam.setField("YD_CRN_SCH_ID"	, sYD_CRN_SCH_ID);
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch
			--권상,권하시 해당 크레인스케줄 id를 제외한 동일 스케줄 작업이 만들어져 있는지 조회
			SELECT CS.YD_CRN_SCH_ID
			      ,CS.YD_SCH_CD
			      ,CS.YD_UP_WO_LOC
			      ,CS.YD_DN_WO_LOC
			      ,CM.STOCK_ID
			      ,CM.YD_AID_WRK_YN
			FROM   TB_YM_CRNSCH CS
			      ,TB_YM_CRNWRKMTL CM
			WHERE  CS.DEL_YN = 'N'
			  AND  CM.DEL_YN = 'N'
			  AND  CS.YD_SCH_CD LIKE :V_YD_SCH_CD ||'%'
			  AND  CS.YD_CRN_SCH_ID NOT IN (NVL(:V_YD_CRN_SCH_ID,'1'))
			  AND  CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
			  AND  CM.YD_AID_WRK_YN = 'N'
			 */
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch", logId, methodNm, "선작업지시 존재여부 체크");
			
			if(rsResult.size() == 0) {
				
				//장입 대상재 조회
				jrParam.setField("YD_GP" , YmConstant.YD_GP_2); 
				jrParam.setField("BAY_GP", YmConstant.BAY_GP_C);
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectWBSlabSearch_09_WB
				--W/B 장입대상순번이고, 대차, 야드에 적치중인 SLAB 조회
				SELECT STACKCOL.STACK_COL_GP
				      ,LAYER.STACK_BED_GP
				      ,LAYER.STACK_LAYER_GP
				      ,STOCK.STOCK_ID
				      ,STOCK.CHARGE_LOT_NO
				      ,(
				            SELECT MIN(WBMTL.YD_WBOOK_ID)
				              FROM TB_YM_WRKBOOK    WB
				                  ,TB_YM_WRKBOOKMTL WBMTL
				             WHERE WB.YD_WBOOK_ID = WBMTL.YD_WBOOK_ID
				               AND WB.DEL_YN = 'N'
				               AND WBMTL.DEL_YN = 'N'
				               AND WB.YD_SCH_CD IN ('2CWB01UM') --C동 W/B 보급
				               AND WB.YD_WBOOK_ID NOT IN (
				                      
				                        SELECT YD_WBOOK_ID 
				                          FROM TB_YM_CRNSCH 
				                         WHERE DEL_YN = 'N'
				                          AND  YD_SCH_CD IN ('2CWB01UM') 
				              
				                   ) 
				               AND WBMTL.STOCK_ID = STOCK.STOCK_ID
				       ) AS YD_WBOOK_ID
				      ,SLABCOMM.SLAB_WT
				      ,SLABCOMM.SLAB_W
				      , (SELECT DTL_ITM1
				           FROM USRYMA.TB_YM_RULE
				          WHERE REPR_CD_GP = 'YM102'
				            AND ITEM = 'WB'
				       ) AS WID_DIF 
				  FROM TB_YM_STACKCOL   STACKCOL
				      ,TB_YM_STACKLAYER LAYER
				      ,TB_YM_STOCK      STOCK
				      ,VW_YD_SLABCOMM   SLABCOMM
				      
				 WHERE STACKCOL.YD_GP = :V_YD_GP
				   AND STACKCOL.BAY_GP = :V_BAY_GP
				   AND STACKCOL.SECT_GP IN ('01','02','TC')
				   AND STACKCOL.STACK_COL_GP = LAYER.STACK_COL_GP
				   AND LAYER.STACK_LAYER_STAT IN ('C')
				   AND LAYER.STOCK_ID = STOCK.STOCK_ID
				   AND STOCK.CHARGE_LOT_NO IS NOT NULL
				   AND STOCK.STOCK_ID = SLABCOMM.SLAB_NO(+)
				   
				 ORDER BY CHARGE_LOT_NO 
						 ,STACK_COL_GP DESC 
						 ,STACK_BED_GP DESC 
						 ,STACK_LAYER_GP DESC   
				 */
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectWBSlabSearch_09_WB", logId, methodNm, "장입대상재조회");
				if(rsResult.size() <= 0) {
					if("".equals(sYD_CRN_SCH_ID)) {
						throw new Exception("W/B 장입요구=>SLAB 장입 대상재 존재 안함.");
					} else {
						commUtils.printLog(logId, "W/B 장입요구=>SLAB 장입 대상재 존재 안함." , "SL");
						return jrRtn;
					}
				}

				/////////////////////////////////////////////////////////////////////////////////////////////////////////
				//대차에 실려 이동중인 장입번호 점검하여  현재 장입대상재보다 빠른면    Hold한다.Start	
			     int iCharLotNo     = Integer.parseInt(commUtils.nvl(rsResult.getRecord(0).getFieldString("CHARGE_LOT_NO"),"0")); //장입번호
			     String CurrStockId = commUtils.trim(rsResult.getRecord(0).getFieldString("STOCK_ID"))  ;
			     
			     /*com.inisteel.cim.ym.bslab.dao.BSlabDAO.getTcarFastChareLotNo
			      SELECT  T2.STOCK_ID
			             ,TO_NUMBER(NVL(T3.CHARGE_LOT_NO,99999999)) AS CHARGE_LOT_NO
			        FROM TB_YM_TCARSCH     T1  
			           , TB_YM_TCARFTMVMTL T2  
			           , TB_YM_STOCK       T3
			        WHERE 1=1 
			          AND T1.YD_TCAR_SCH_ID   = T2.YD_TCAR_SCH_ID 
			          AND T1.DEL_YN           =  'N' 
			          AND T2.DEL_YN           =  'N'               
			          AND T1.YD_CAR_PROG_STAT =  'A'  --하차출발 
			          AND T3.DEL_YN           =  'N'
			          AND T3.STOCK_ID         = T2.STOCK_ID
				*/		
			     JDTORecordSet	rsFastChNo = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getTcarFastChareLotNo", logId, methodNm, "대차이동중 현재장입보다 빠른장입재료찾기 "); 
						
				if(rsFastChNo.size() > 0) {
					for(int ii= 0; ii < rsFastChNo.size() ; ii++) {
						
						int iCharLotNo_Tc = Integer.parseInt(commUtils.nvl(rsFastChNo.getRecord(ii).getFieldString("CHARGE_LOT_NO"),"99999999")); //대차에 실려 이동중인 장입번호
						String StockId    = commUtils.trim(rsFastChNo.getRecord(ii).getFieldString("STOCK_ID")) ;
						  
						if(iCharLotNo > iCharLotNo_Tc ) {
							if("".equals(sYD_CRN_SCH_ID)) {
								throw new Exception("대차이동중인장입대상재:"+StockId +" 장입번호 :"+ iCharLotNo_Tc +"가 현재장입재:"+CurrStockId+"장입번호:" + iCharLotNo+"보다빠릅니다.");
							} else {
								commUtils.printLog(logId, "대차이동중인장입대상재:"+StockId +" 장입번호:"+ iCharLotNo_Tc +"현재장입재:"+CurrStockId+"장입번호:" + iCharLotNo, "SL");
								return jrRtn;
							}
						}
								 
					}
				}				
				//대차에 실려 이동중인 장입번호 점검하여  현재 장입대상재보다 빠른면    Hold한다.End	
				/////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				//적치단 테이블에서 2CWB01 01Bed 2단 정보를 읽어 온다.
				jrParam.setField("STACK_COL_GP", "2CWB01"); 
				jrParam.setField("STACK_BED_GP", "01"); 
				jrParam.setField("STACK_LAYER_GP", "02"); 
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStockIdByLoc 
				SELECT  STOCK_ID
				       ,STACK_LAYER_STAT
				       ,STACK_LAYER_ACTIVE_STAT
				       ,TRIM(TO_CHAR(STACK_BED_GP + 1,'00')) AS NEXT_BED_GP
				  FROM  TB_YM_STACKLAYER
				 WHERE  STACK_COL_GP = :V_STACK_COL_GP
				   AND  STACK_BED_GP = :V_STACK_BED_GP
				   AND  STACK_LAYER_GP LIKE :V_STACK_LAYER_GP || '%' */
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStockIdByLoc", logId, methodNm, "W/B 01Bed 2단 정보 읽어오기 "); 
				if(rsResult2.size() > 0) {
					if("C".equals(rsResult2.getRecord(0).getFieldString("STACK_LAYER_ACTIVE_STAT"))) {
						//01Bed 2단 이 비활성(C)이면 1단만 사용함으로 작업매수 1로 설정
						iMaxRec = 1;
					} else {
						iMaxRec = 2;
					}
				}
				
				commUtils.printLog(logId, "=======>>>> 2CWB01열 01Bed 02단 활성상태 : " +  rsResult2.getRecord(0).getFieldString("STACK_LAYER_ACTIVE_STAT") + ", iMaxRec = " + iMaxRec , "SL");
				
				if(iMaxRec == 2 && rsResult.size() > 1) {
					//작업매수 2매 일경우 
					// - 적치열, 적치Bed 가 동일하고, 장입순번이 같거나
					//   장입순번 값이 적은 값이 상단에 있고 , 하단에 다음 장입순번이 있으면 2매 작업 가능
					// - 위 조건에 충족하더라도 2매 작업중량이 > 장입크레인의 작업가능중량 (C1,C2:60000, C3:53200) 이거나 폭차이가 > 20.0 이면 1매만 작업
					//  폭이 2000 이상이면 1매 작업
					if(rsResult.getRecord(0).getFieldString("STACK_COL_GP").equals(rsResult.getRecord(1).getFieldString("STACK_COL_GP")) 
					   && rsResult.getRecord(0).getFieldString("STACK_BED_GP").equals(rsResult.getRecord(1).getFieldString("STACK_BED_GP"))) { //적치열, 적치Bed 가 동일
						
						double dUpperSlabWt = Double.parseDouble(commUtils.nvl(rsResult.getRecord(0).getFieldString("SLAB_WT"),"0")); //상단 Slab 중량
						double dLowerSlabWt = Double.parseDouble(commUtils.nvl(rsResult.getRecord(1).getFieldString("SLAB_WT"),"0")); //하단 Slab 중량
						commUtils.printLog(logId, "=======:::: 상단 SLAB 중량 : " +  dUpperSlabWt , "SL");
						commUtils.printLog(logId, "=======:::: 하단 SLAB 중량 : " +  dLowerSlabWt , "SL");
						
						double dUpperSlabW = Double.parseDouble(commUtils.nvl(rsResult.getRecord(0).getFieldString("SLAB_W"),"0")); //상단 Slab 폭
						double dLowerSlabW = Double.parseDouble(commUtils.nvl(rsResult.getRecord(1).getFieldString("SLAB_W"),"0")); //하단 Slab 폭
						commUtils.printLog(logId, "=======:::: 상단 SLAB 폭 : " +  dUpperSlabW , "SL");
						commUtils.printLog(logId, "=======:::: 하단 SLAB 폭 : " +  dLowerSlabW , "SL");

						double dRuleSlabW = Double.parseDouble(commUtils.nvl(rsResult.getRecord(0).getFieldString("WID_DIF"),"50")); //상판폭 - 하단 폭 폭차이 기준
						commUtils.printLog(logId, "=======:::: 상단폭-하단폭 폭차이 기준 : " +  dRuleSlabW , "SL");

						double dRuleMaxW = Double.parseDouble(commUtils.nvl(rsResult.getRecord(0).getFieldString("YM102_MAX_W"),"1750")); //2매작업 기준 폭
						commUtils.printLog(logId, "=======:::: 2매작업 기준 폭 : " +  dRuleMaxW , "SL");

						
						if(dUpperSlabWt + dLowerSlabWt > dmaxStackWt) {
							iMaxRec = 1;
							commUtils.printLog(logId, "=======>>>> dUpperSlabWt + dLowerSlabWt > " + dmaxStackWt + " 에 의해 iMaxRec = " +  iMaxRec , "SL");
						} else if(dUpperSlabW - dLowerSlabW > dRuleSlabW) {
							iMaxRec = 1;
							commUtils.printLog(logId, "=======>>>> dUpperSlabW - dLowerSlabW > " + dRuleSlabW + " 에 의해 iMaxRec = " +  iMaxRec , "SL");
						} else if(dUpperSlabW >= dRuleMaxW) {
							iMaxRec = 1;
							commUtils.printLog(logId, "=======>>>> dUpperSlabW >= " + dRuleMaxW + " 에 의해 iMaxRec = " +  iMaxRec , "SL");
						} else if(dLowerSlabW >= dRuleMaxW) {
							iMaxRec = 1;
							commUtils.printLog(logId, "=======>>>> dLowerSlabW >= " + dRuleMaxW + " 에 의해 iMaxRec = " +  iMaxRec , "SL");
						} else {
							
							int iUpperSlabChargeLotNo = Integer.parseInt(commUtils.nvl(rsResult.getRecord(0).getFieldString("CHARGE_LOT_NO"),"0")); //상단 Slab 장입순번
							int iLowerSlabCHargeLotNo = Integer.parseInt(commUtils.nvl(rsResult.getRecord(1).getFieldString("CHARGE_LOT_NO"),"0")); //하단 Slab 장입순번
							
							commUtils.printLog(logId, "=======|||| 상단 SLAB 장입순위 : " +  iUpperSlabChargeLotNo , "SL");
							commUtils.printLog(logId, "=======|||| 하단 SLAB 장입순위 : " +  iLowerSlabCHargeLotNo , "SL");
							
							if(iUpperSlabChargeLotNo == iLowerSlabCHargeLotNo) { 
								//장입순번이 같음
								iMaxRec = 2;
								commUtils.printLog(logId, "=======>>>> 상단,하단 장입순위 같음  iMaxRec = " +  iMaxRec , "SL");
								
							} else { 
								//장입순번이 다름
								
								if(iUpperSlabChargeLotNo + 1 == iLowerSlabCHargeLotNo ) {
									//장입순번이 상단 하단 연속으로 되어 있으면 2매 작업 가능
									iMaxRec = 2;
									commUtils.printLog(logId, "=======>>>> 상단,하단 장입순위 다르지만 연속으로 되어 있음  iMaxRec = " +  iMaxRec , "SL");
								} else {
									iMaxRec = 1;
								}
							}
							
							
							if(iMaxRec == 2) {
								//장입순번이 같거나 상단 하단 연속으로 되어 있더라도 
								//위치(열,BED)가 틀리거나 단이 연속으로 되어 있지 않을 경우 1매 작업
								
								//동일BED 체크
								String dUpperBedGp = rsResult.getRecord(0).getFieldString("STACK_COL_GP") + rsResult.getRecord(0).getFieldString("STACK_BED_GP"); //상단 BED
								String dLowerBedGp = rsResult.getRecord(1).getFieldString("STACK_COL_GP") + rsResult.getRecord(1).getFieldString("STACK_BED_GP"); //하단 BED
								commUtils.printLog(logId, "=======:::: 상단 열+BED : " +  dUpperBedGp , "SL");
								commUtils.printLog(logId, "=======:::: 하단 열+BED : " +  dLowerBedGp , "SL");
								
								//연속단 체크 
								int iUpperLayerGp = Integer.parseInt(commUtils.nvl(rsResult.getRecord(0).getFieldString("STACK_LAYER_GP"),"0")); //상단 Layer_Gp
								int iLowerLayerGp = Integer.parseInt(commUtils.nvl(rsResult.getRecord(1).getFieldString("STACK_LAYER_GP"),"0")); //하단 Layer_Gp
								commUtils.printLog(logId, "=======:::: 상단 Layer_Gp : " +  iUpperLayerGp , "SL");
								commUtils.printLog(logId, "=======:::: 하단 Layer_Gp : " +  iLowerLayerGp , "SL");
								
								//동일BED 체크
								if(dUpperBedGp.equals(dLowerBedGp)) {
									
									if(iUpperLayerGp == iLowerLayerGp + 1) {
										iMaxRec = 2;
									} else {
										iMaxRec = 1;
										commUtils.printLog(logId, "=======>>>> 상단,하단 단이 연속이 아님  iMaxRec = " +  iMaxRec , "SL");
									}
									
								} else {
									iMaxRec = 1;
									commUtils.printLog(logId, "=======>>>> 상단,하단 위치(열,BED)가 다름  iMaxRec = " +  iMaxRec , "SL");
								}
							}
							
							
							//if(iMaxRec == 2) {
							//	//상위 조건이 충족 되더라도 변형재열재(두께 200mm 이하)는  1매 작업
							//	double dUpperSlabT = Double.parseDouble(commUtils.nvl(rsResult.getRecord(0).getFieldString("SLAB_T"),"0")); //상단 Slab 두께
							//	double dLowerSlabT = Double.parseDouble(commUtils.nvl(rsResult.getRecord(1).getFieldString("SLAB_T"),"0")); //하단 Slab 두께
							//	commUtils.printLog(logId, "=======:::: 상단 SLAB 두께 : " +  dUpperSlabT , "SL");
							//	commUtils.printLog(logId, "=======:::: 하단 SLAB 두께 : " +  dLowerSlabT , "SL");
							//	
							//	if(dUpperSlabT <= 200 && dUpperSlabT > 0) {
							//		iMaxRec = 1;
							//		commUtils.printLog(logId, "=======>>>> dUpperSlabT <= 200 에 의해 iMaxRec = " +  iMaxRec , "SL");
							//	}
							//	if(dLowerSlabT <= 200 && dLowerSlabT > 0) {
							//		iMaxRec = 1;
							//		commUtils.printLog(logId, "=======>>>> dLowerSlabT <= 200 에 의해 iMaxRec = " +  iMaxRec , "SL");
							//	}
							//}
							
							if(iMaxRec == 2) {
								//상위 조건이 충족 되더라도 변형재열재(SLABCOMM.REHEAT_SLAB_GP 가 '2')는  1매 작업
								String sUpperReheatSlabGp = commUtils.nvl(rsResult.getRecord(0).getFieldString("REHEAT_SLAB_GP"),""); //상단 재열재구분
								String dLowerReheatSlabGp = commUtils.nvl(rsResult.getRecord(1).getFieldString("REHEAT_SLAB_GP"),""); //하단 재열재구분
								commUtils.printLog(logId, "=======:::: 상단 재열재구분 : " +  sUpperReheatSlabGp , "SL");
								commUtils.printLog(logId, "=======:::: 하단 재열재구분 : " +  dLowerReheatSlabGp , "SL");
								
								if("2".equals(sUpperReheatSlabGp)) {
									iMaxRec = 1;
									commUtils.printLog(logId, "=======>>>> sUpperReheatSlabGp == '2' 에 의해 iMaxRec = " +  iMaxRec , "SL");
								}
								if("2".equals(dLowerReheatSlabGp)) {
									iMaxRec = 1;
									commUtils.printLog(logId, "=======>>>> dLowerReheatSlabGp == '2' 에 의해 iMaxRec = " +  iMaxRec , "SL");
								}
							}
							
							
							if(iMaxRec == 2) {
								//상위 조건이 충족 되더라도 밴딩재(STOCK.YD_RULE_PL_RS_GP 가 'Y')는  1매 작업
								String sUpperYdRulePlRsGp = commUtils.nvl(rsResult.getRecord(0).getFieldString("YD_RULE_PL_RS_GP"),""); //상단 Bending재구분
								String dLowerYdRulePlRsGp = commUtils.nvl(rsResult.getRecord(1).getFieldString("YD_RULE_PL_RS_GP"),""); //하단 Bending재구분
								commUtils.printLog(logId, "=======:::: 상단 Bending재구분 : " +  sUpperYdRulePlRsGp , "SL");
								commUtils.printLog(logId, "=======:::: 하단 Bending재구분 : " +  dLowerYdRulePlRsGp , "SL");
								
								if("Y".equals(sUpperYdRulePlRsGp)) {
									iMaxRec = 1;
									commUtils.printLog(logId, "=======>>>> sUpperYdRulePlRsGp == 'Y' 에 의해 iMaxRec = " +  iMaxRec , "SL");
								}
								if("Y".equals(dLowerYdRulePlRsGp)) {
									iMaxRec = 1;
									commUtils.printLog(logId, "=======>>>> dLowerYdRulePlRsGp == 'Y' 에 의해 iMaxRec = " +  iMaxRec , "SL");
								}
							}
							
						}
						
					} else {
						//1매만 작업
						iMaxRec = 1;
					}
				
				} else {
					//1매만 작업
					iMaxRec = 1;
				} 

				commUtils.printLog(logId, "=======>>>> WB장입 작업 매수 : " +  iMaxRec , "SL");
				
				
				/*********************************************************************************************************
				*  B열연 크레인자동화시스템구축_SLAB야드안정화 
				*  요구사항 37번 - WB 장입시 다른 스케줄 작업예약에 있으면 작업예약 삭제
				*  요구자 : 정종균 과장, 송정현 부장
				*  요청일 : 2017-11-02
				*  수정일 : 2017-11-05
				*********************************************************************************************************/
				String ydWbookId = "";
				for(int ii = 0; ii < iMaxRec; ii++) {
					if(!"".equals(commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_WBOOK_ID2")))) {
						if("".equals(commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_CRN_SCH_ID")))) {
							//작업예약 삭제처리
							ydWbookId = commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_WBOOK_ID2"));
							
							jrParam.setField("YD_WBOOK_ID", ydWbookId);
							
							/**********************************************************
							* 1. 크레인스케줄 존재여부 Check
							**********************************************************/
							/*
							SELECT YD_CRN_SCH_ID
							  FROM TB_YM_CRNSCH
							 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
							   AND DEL_YN      = 'N'
							 */
							JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCommWbCrnSch", logId, methodNm, "크레인작업지시read");
							if (jsCrnSch != null && jsCrnSch.size() > 0) {	
								
								if("".equals(sYD_CRN_SCH_ID)) {
									throw new Exception("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 " + jsCrnSch.size() + " 건 존재합니다.");
								} else {
									commUtils.printLog(logId, "작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 " + jsCrnSch.size() + " 건 존재합니다." , "SL");
									return jrRtn;
								}
						    }
							
							/**********************************************************
							* 3. 차량/대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
							**********************************************************/
							//차량스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
							/*
							UPDATE USRYDA.TB_YD_CARSCH
							   SET MODIFIER              = :V_MODIFIER
							      ,MOD_DDTT              = SYSDATE
							      ,YD_CARLD_WRK_BOOK_ID  = DECODE(YD_CARLD_WRK_BOOK_ID,:V_YD_WBOOK_ID,NULL,YD_CARLD_WRK_BOOK_ID)
							      ,YD_CARUD_WRK_BOOK_ID  = DECODE(YD_CARUD_WRK_BOOK_ID,:V_YD_WBOOK_ID,NULL,YD_CARUD_WRK_BOOK_ID)
							 WHERE DEL_YN                = 'N'
							   AND (YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID OR YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID)
							 */
							commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCommCarSchWbDel", logId, methodNm, "TB_YD_CARSCH");				
						
							//대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
							/*
							UPDATE USRYDA.TB_YD_TCARSCH
							   SET MODIFIER              = :V_MODIFIER
							      ,MOD_DDTT              = SYSDATE
							      ,YD_CARLD_WRK_BOOK_ID  = DECODE(YD_CARLD_WRK_BOOK_ID,:V_YD_WBOOK_ID,NULL,YD_CARLD_WRK_BOOK_ID)
							      ,YD_CARUD_WRK_BOOK_ID  = DECODE(YD_CARUD_WRK_BOOK_ID,:V_YD_WBOOK_ID,NULL,YD_CARUD_WRK_BOOK_ID)
							 WHERE DEL_YN                = 'N'
							   AND (YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID OR YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID)
							 */
							commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCommTcarSchWbDel", logId, methodNm, "TB_YM_TCARSCH");				

						    /**********************************************************
							* 4. 작업예약/재료 삭제
							**********************************************************/
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
							
							
						} else {
							
							if("".equals(sYD_CRN_SCH_ID)) {
								throw new Exception("장입대상 SLAB_NO : " + commUtils.trim(rsResult.getRecord(ii).getFieldString("STOCK_ID")) 
										+ " 가 크레인 스케줄 : " + commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_CRN_SCH_ID")) + "에 잡혀 있습니다!!");
							} else {
								commUtils.printLog(logId, "장입대상 SLAB_NO : " + commUtils.trim(rsResult.getRecord(ii).getFieldString("STOCK_ID")) 
										+ " 가 크레인 스케줄 : " + commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_CRN_SCH_ID")) + "에 잡혀 있습니다!!" , "SL");
								return jrRtn;
							}							
						}
					}
				}
				/*********************************************************************************************************
				*********************************************************************************************************/				
				
				
				String sYD_TO_LOC_GUIDE = null;
				String sYD_SCH_PRIOR = null;
				String sYD_WBOOK_ID = rsResult.getRecord(0).getFieldString("YD_WBOOK_ID");
				
				if("".equals(sYD_WBOOK_ID)) {
					
					//스케줄코드, To위치 Guide 
					sYD_SCH_CD = "2CWB01UM";
					//sYD_TO_LOC_GUIDE = "2CWB010101";
					sYD_TO_LOC_GUIDE = "";
				
					//스케줄코드로 스케줄기준Table조회
					jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
			    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
					SELECT YD_SCH_CD
					     , YD_WRK_CRN
					     , YD_WRK_CRN_PRIOR
					  FROM TB_YM_SCHEDULERULE
					 WHERE DEL_YN = 'N'
					   AND YD_SCH_CD = :V_YD_SCH_CD
					*/   
					rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
			    	
					if (rsResult2 != null && rsResult2.size() > 0) {
						sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
						sYD_WRK_PLAN_CRN = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN");
					} else {
						if("".equals(sYD_CRN_SCH_ID)) {
							throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sYD_SCH_CD + "]");
						} else {
							commUtils.printLog(logId, "B열연 코일 스케쥴 코드 이상 : [" + sYD_SCH_CD + "]" , "SL");
							return jrRtn;
						}
					}			
					
					//작업예약ID생성
					sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
				
					/**********************************************************
					* 2-1. 작업예약(TB_YM_WRKBOOK) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
					jrParam.setField("YD_GP"			, YmConstant.YD_GP_2);
					jrParam.setField("YD_BAY_GP"		, sYD_SCH_CD.substring(1,2));
					jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD); //야드스케쥴코드
					jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
					jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
					jrParam.setField("YD_TO_LOC_GUIDE"	, sYD_TO_LOC_GUIDE); //야드To위치Guide
					if(!"".equals(sYD_WRK_PLAN_CRN)) {
						jrParam.setField("YD_WRK_PLAN_CRN"	, sYD_WRK_PLAN_CRN); //야드작업계획크레인
					}
					
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
					
					/**********************************************************
					* 2-2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
					**********************************************************/
					for(int ii = 0; ii < iMaxRec; ii++) {
						jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
						jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STOCK_ID")));
						jrParam.setField("STACK_COL_GP"		, commUtils.trim(rsResult.getRecord(ii).getFieldString("STACK_COL_GP")));
						jrParam.setField("STACK_BED_GP"		, commUtils.trim(rsResult.getRecord(ii).getFieldString("STACK_BED_GP")));
						jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(rsResult.getRecord(ii).getFieldString("STACK_LAYER_GP")));
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
						
						/**********************************************************
						* 2-3. TB_YM_STOCK의 저장품 이동조건 등록
						**********************************************************/
						jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STOCK_ID")));
						jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_FS ); //압연작업대기
						
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 저장품 이동 조건 UPDATE");
					}
				}
				
				//스케줄 메인 호출
				JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
				jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
				jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
				jrCrnSchMsg.setField("YD_WBOOK_ID"  		, sYD_WBOOK_ID); //작업예약ID
				jrCrnSchMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
				jrCrnSchMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
				jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);		
				
				//Slab Schedule EJB Call
				//jrParam.setField("JMS_TC_CD"    , "YMYMJ202"); 
				//jrParam.setField("YD_WBOOK_ID"  , sYD_WBOOK_ID); //야드작업예약ID
				//jrParam.setField("YD_SCH_CD"    , ""  ); //야드스케쥴코드
				//jrParam.setField("YD_EQP_ID"    , ""  ); //야드설비ID
				
				
				//EJBConnector ejbConn = new EJBConnector("default","BSlabSchSeEJB",this);
				//jrRtn = (JDTORecord)ejbConn.trx("procYMYMJ202",new Class[]{JDTORecord.class},new Object[]{ jrParam });			
				
			} else {
				//선작업 스케줄이 있을 경우에
				
			}
			

			commUtils.printLog(logId, methodNm, "S-");
			
	    	return jrRtn;
	    	
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of W/B 장입요구(A8YML020)


	
	/**
	 *      [A] 오퍼레이션명 : 슬라브 CTC Tracking 정보(A8YML021)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML021(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "슬라브 CTC Tracking 정보(A8YML021) << " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			
			String SLAB_NO111   = commUtils.trim(rcvMsg.getFieldString("SLAB_NO111" ));
			String SLAB_NO_STE  = commUtils.trim(rcvMsg.getFieldString("SLAB_NO_STE" ));
			String SLAB_NO232   = commUtils.trim(rcvMsg.getFieldString("SLAB_NO232" ));
			String SLAB_NO231   = commUtils.trim(rcvMsg.getFieldString("SLAB_NO231" ));
			String SLAB_NO222   = commUtils.trim(rcvMsg.getFieldString("SLAB_NO222" ));
			String SLAB_NO221   = commUtils.trim(rcvMsg.getFieldString("SLAB_NO221" ));
			String SLAB_NO212   = commUtils.trim(rcvMsg.getFieldString("SLAB_NO212" ));
			String SLAB_NO211   = commUtils.trim(rcvMsg.getFieldString("SLAB_NO211" ));
			
			String sYD_WBOOK_ID = "";
			
			if ("".equals(modifier)) { modifier = msgId; }
			
			String L3Hmi         = commUtils.nvl(rcvMsg.getFieldString("L3_HMI"),"N");         //백업화면 기동 여부

			if(L3Hmi.equals("N")) {   // L2전문수신으로 실행된 경우

				/**********************************************************
				* 1. 조업 TRACKING UPDATE
				**********************************************************/
				
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
				jrYdMsg.setField("MODIFIER"     , modifier);					//수정자
				jrYdMsg.setField("STLNO1"     	, SLAB_NO111 );  //SLAB_NO111
				jrYdMsg.setField("STLNO2"     	, SLAB_NO_STE ); //SLAB_NO_STE
				jrYdMsg.setField("STLNO3"     	, SLAB_NO232 );  //SLAB_NO232
				jrYdMsg.setField("STLNO4"     	, SLAB_NO231 );  //SLAB_NO231
				jrYdMsg.setField("STLNO5"     	, SLAB_NO222 );  //SLAB_NO222
				jrYdMsg.setField("STLNO6"     	, SLAB_NO221 );  //SLAB_NO221
				jrYdMsg.setField("STLNO7"     	, SLAB_NO212 );  //SLAB_NO212
				jrYdMsg.setField("STLNO8"     	, SLAB_NO211 );  //SLAB_NO211
	
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updEqpTrackingC 
				MERGE INTO TB_YM_EQPTRACKING ET USING (
				SELECT A.STL_NO 
				     , B.STL_NO           AS STL_NO_NEW
				     , B.SORT_SEQ
				  FROM TB_YM_EQPTRACKING A
				     , (
				        SELECT :V_STLNO1 AS STL_NO ,  1  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO2 AS STL_NO ,  2  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO3 AS STL_NO ,  3  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO4 AS STL_NO ,  4  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO5 AS STL_NO ,  5  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO6 AS STL_NO ,  6  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO7 AS STL_NO ,  7  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO8 AS STL_NO ,  8  AS SORT_SEQ  FROM DUAL  
				     ) B
				WHERE A.YD_GP     = '2'
				  AND A.PROC_GP   = 'C'
				  AND A.EQUIP_GP  LIKE 'CT%'
				  AND A.SORT_SEQ  = B.SORT_SEQ
				  
				) DD ON (ET.YD_GP = '2' AND ET.PROC_GP = 'C' AND ET.EQUIP_GP LIKE 'CT%' AND  ET.SORT_SEQ = DD.SORT_SEQ)
				WHEN MATCHED THEN UPDATE SET
				     STL_NO     = STL_NO_NEW
				   , MOD_DDTT   = SYSDATE
				   , MODIFIER   = :V_MODIFIER
				*/   
				commDao.update(jrYdMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updEqpTrackingC", logId, methodNm, "CTC수정");
				
				
				//Tracking Table 2ACT02 03BED 정보를 적치단에 설정
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCTLyrInfoByTracking
				MERGE INTO TB_YM_STACKLAYER SL USING (
	
				    SELECT STACK_COL_GP
				          ,STACK_BED_GP
				          ,LOC_NO
				          ,STL_NO
				      FROM TB_YM_EQPTRACKING   
				     WHERE YD_GP = '2'
				       AND PROC_GP = 'C'
				       AND EQUIP_GP LIKE 'CTC23%'
	
				) DD ON (SL.STACK_COL_GP = DD.STACK_COL_GP AND SL.STACK_BED_GP = DD.STACK_BED_GP AND SL.STACK_LAYER_GP = DD.LOC_NO )
				WHEN MATCHED THEN UPDATE SET
				     STOCK_ID   = DD.STL_NO
				   , STACK_LAYER_STAT = DECODE(NVL(DD.STL_NO,''),'','E','C')
				   , MOD_DDTT   = SYSDATE
				   , MODIFIER   = :V_MODIFIER  */ 
				commDao.update(jrYdMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCTLyrInfoByTracking", logId, methodNm, "Tracking 테이블에서 적치단  2ACT02 03BED 정보를  update ");
			}
			
			
			/**********************************************************
			* 11. STE 고장에 따른 크레인 작업 지시 호출
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"     , modifier);					//수정자
			jrParam.setField("YD_EQP_ID"	, "2AST01"); 
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStatEqp", logId, methodNm, "설비상태조회");
			if(jsChk.size()>0) {
				if("B".equals(commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT")))) { 
					
					//STE 고장!
					
					//CTC#2 3번 번지 최상단 Slab 정보를 작업예약한다.
					String sYD_SCH_CD = "2AHB02UM"; //A동 STE 비상보급
					String sYD_TO_LOC_GUIDE = "2ACT010101"; //sYD_TO_LOC_GUIDE = "2ACT010101";
					
					//선작업지시 존재여부 체크
					jrParam.setField("YD_SCH_CD" 		, sYD_SCH_CD); 
					jrParam.setField("YD_CRN_SCH_ID"	, "1");
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch
					--권상,권하시 해당 크레인스케줄 id를 제외한 동일 스케줄 작업이 만들어져 있는지 조회
					SELECT CS.YD_CRN_SCH_ID
					      ,CS.YD_SCH_CD
					      ,CS.YD_UP_WO_LOC
					      ,CS.YD_DN_WO_LOC
					      ,CM.STOCK_ID
					      ,CM.YD_AID_WRK_YN
					FROM   TB_YM_CRNSCH CS
					      ,TB_YM_CRNWRKMTL CM
					WHERE  CS.DEL_YN = 'N'
					  AND  CM.DEL_YN = 'N'
					  AND  CS.YD_SCH_CD LIKE :V_YD_SCH_CD ||'%'
					  AND  CS.YD_CRN_SCH_ID NOT IN (NVL(:V_YD_CRN_SCH_ID,'1'))
					  AND  CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					  AND  CM.YD_AID_WRK_YN = 'N'
					 */
					JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch", logId, methodNm, "선작업지시 존재여부 체크");
					
					if(rsResult.size() == 0) {
					
					
						jrParam.setField("STACK_COL_GP"		, "2ACT02"); 
						jrParam.setField("STACK_BED_GP"		, "03");
						jrParam.setField("STACK_LAYER_STAT"	, "C");
						/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPutStockIdByBedNo
						SELECT  LYR.STACK_COL_GP
						       ,LYR.STACK_BED_GP
						       ,LYR.STACK_LAYER_GP
						       ,LYR.STOCK_ID
						       ,LYR.STACK_LAYER_STAT
						       ,LYR.STACK_LAYER_ACTIVE_STAT
						       ,(
						            SELECT WBMTL.YD_WBOOK_ID
						              FROM TB_YM_WRKBOOK    WB
						                  ,TB_YM_WRKBOOKMTL WBMTL
						             WHERE WB.YD_WBOOK_ID = WBMTL.YD_WBOOK_ID
						               AND WB.DEL_YN = 'N'
						               AND WBMTL.DEL_YN = 'N'
						               AND WBMTL.STOCK_ID = LYR.STOCK_ID
						        ) AS YD_WBOOK_ID       
						  FROM  TB_YM_STACKLAYER LYR
						 WHERE  LYR.STACK_COL_GP = :V_STACK_COL_GP
						   AND  LYR.STACK_BED_GP = :V_STACK_BED_GP
						   AND  LYR.STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT || '%'
						   AND  LYR.STOCK_ID IS NOT NULL
						  ORDER BY LYR.STACK_LAYER_GP DESC  */
						JDTORecordSet rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPutStockIdByBedNo", logId, methodNm, "CTC#2 03Bed 조회 ");
		
						boolean bFLAG = true;
						
						if(rsResult2.size() < 2) {
							commUtils.printLog(logId, "=======:::: STE고장 비상스케쥴 = CTC#2 3번 번지 02단 정보 없음! 비상스케쥴 수행안함 ", "SL");
							bFLAG = false;
						} else {
							sYD_WBOOK_ID = rsResult2.getRecord(0).getFieldString("YD_WBOOK_ID");
							if(!"".equals(sYD_WBOOK_ID)) {
								commUtils.printLog(logId, "=======:::: STE고장 비상스케쥴 = CTC#2 3번 번지 02단 SLAB 작업예약 존재함! 비상스케쥴 수행안함 ", "SL");
								bFLAG = false;
							}
						}
						
						if (bFLAG) {
							
							//CTC1  크레인 작업지시 생성 전에 Clear
							jrParam.setField("STOCK_ID", ""); 
							jrParam.setField("STACK_LAYER_STAT2", "E"); 
							jrParam.setField("STACK_COL_GP"		, "2ACT01"); 
							jrParam.setField("STACK_BED_GP"		, "01"); 
							jrParam.setField("STACK_LAYER_STAT1", "");
							/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo 
							UPDATE TB_YM_STACKLAYER
							   SET 
							       MODIFIER = :V_MODIFIER
							      ,MOD_DDTT = SYSDATE
							      ,STOCK_ID = :V_STOCK_ID
							      ,STACK_LAYER_STAT = :V_STACK_LAYER_STAT2
							 WHERE  STACK_COL_GP = :V_STACK_COL_GP
							   AND  STACK_BED_GP = :V_STACK_BED_GP
							   AND  STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT1 || '%' */
							commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo", logId, methodNm, "CTC1 Clear");
							
							
							//스케줄코드로 스케줄기준Table조회
							jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
					    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
							SELECT YD_SCH_CD
							     , YD_WRK_CRN
							     , YD_WRK_CRN_PRIOR
							  FROM TB_YM_SCHEDULERULE
							 WHERE DEL_YN = 'N'
							   AND YD_SCH_CD = :V_YD_SCH_CD
							*/   
							JDTORecordSet rsResult3 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
							
							if(rsResult3.size() > 0) {
								String sYD_SCH_PRIOR = rsResult3.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
								
								//작업예약ID생성
								sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
							
								/**********************************************************
								* 2-1. 작업예약(TB_YM_WRKBOOK) 생성
								**********************************************************/
								jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
								jrParam.setField("YD_GP"			, YmConstant.YD_GP_2);
								jrParam.setField("YD_BAY_GP"		, sYD_SCH_CD.substring(1,2));
								jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD); //야드스케쥴코드
								jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
								jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
								jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
								jrParam.setField("YD_TO_LOC_GUIDE"	, sYD_TO_LOC_GUIDE); //야드To위치Guide
								jrParam.setField("YD_TO_LOC_DCSN_MTD", "S"); //야드TO위치결정방법
								
								commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
								
								/**********************************************************
								* 2-2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
								**********************************************************/
								jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
								jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult2.getRecord(0).getFieldString("STOCK_ID")));
								jrParam.setField("STACK_COL_GP"		, commUtils.trim(rsResult2.getRecord(0).getFieldString("STACK_COL_GP")));
								jrParam.setField("STACK_BED_GP"		, commUtils.trim(rsResult2.getRecord(0).getFieldString("STACK_BED_GP")));
								jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(rsResult2.getRecord(0).getFieldString("STACK_LAYER_GP")));
								
								commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
								
								/**********************************************************
								* 2-3. TB_YM_STOCK의 저장품 이동조건 등록
								**********************************************************/
								jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult2.getRecord(0).getFieldString("STOCK_ID")));
								jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_FS ); //압연작업대기
								
								commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 저장품 이동 조건 UPDATE");
								
								//스케줄 메인 호출
								JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
								jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
								jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
								jrCrnSchMsg.setField("YD_WBOOK_ID"  		, sYD_WBOOK_ID); //작업예약ID
								jrCrnSchMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
								jrCrnSchMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
								jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);		
							}
						} //bFLAG
					} else {
						commUtils.printLog(logId, "=======:::: A동 STE 비상보급 스페줄이 존재합니다. " , "SL");					
					}
					
					
					
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 슬라브 CTC Tracking 정보(A8YML021)	

	/**		
	 *      [A] 오퍼레이션명 : 자동이적요구(A8YML027) 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML027(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "자동이적요구[BSlabL2RcvSeEJB.rcvA8YML027] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
	    JDTORecord jrParam	 = JDTORecordFactory.getInstance().create(); //Query 실행시 파라메터 전달용 JDTORecord 
	    JDTORecordSet rsResult    	= null;
	    JDTORecordSet rsResult2    	= null;
	    
	    int iMaxRec = 1; //1매 작업
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "자동이적요구(A8YML027) 수신 ", rcvMsg);
			
			//수신 항목 값
			String msgId			= commUtils.nvl(commUtils.getMsgId(rcvMsg),"A8YML027"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sYD_EQP_ID   	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));  //야드설비ID
			String sYD_EQP_WRK_SH  	= commUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"2"); //야드설비작업매수
			String sYD_CRN_XAXIS	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS")); //야드크레인X축
			String sYD_CRN_YAXIS	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS")); //야드크레인Y축
			String modifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
			if ("".equals(modifier)) { modifier = msgId; }
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sYD_EQP_ID)) {
				throw new Exception("YD_EQP_ID 없음["+ sYD_EQP_ID + "]");
			}
			if ("".equals(sYD_CRN_XAXIS)) {
				throw new Exception("YD_CRN_XAXIS 없음["+ sYD_CRN_XAXIS + "]");
			}
			if ("".equals(sYD_CRN_YAXIS)) {
				throw new Exception("YD_CRN_YAXIS 없음["+ sYD_CRN_YAXIS + "]");
			}
			
			/******************************************************************
			* 2. X,Y 물리 좌표값으로 논리위치(적치열,Bed)를 조회한다.
			******************************************************************/
			jrParam.setField("STACK_COL_GP"    , sYD_EQP_ID.substring(0,2)); //야드구분 + 동
			jrParam.setField("STACK_LAYER_X_AXIS"    , sYD_CRN_XAXIS); //야드크레인X축
			jrParam.setField("STACK_LAYER_Y_AXIS"    , sYD_CRN_YAXIS); //야드크레인Y축
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getXYLogicalInfo
			SELECT  A.STACK_COL_GP
			       ,A.STACK_BED_GP
			       ,A.STACK_LAYER_GP
			       ,A.STACK_LAYER_X_AXIS
			       ,A.STACK_LAYER_Y_AXIS
			       ,A.STACK_LAYER_Z_AXIS
			  FROM  TB_YM_STACKLAYER A
			       ,TB_YM_STACKER B
			 WHERE  A.STACK_COL_GP LIKE :V_STACK_COL_GP || '%'
			   AND  A.STACK_COL_GP = B.STACK_COL_GP  
			   AND  A.STACK_BED_GP = B.STACK_BED_GP  
			   AND  A.STACK_LAYER_GP = '01'
			   AND  A.DEL_YN = 'N' 
			   AND  B.DEL_YN = 'N' 
			   AND  A.STACK_LAYER_X_AXIS between :V_STACK_LAYER_X_AXIS - NVL(B.YD_STK_BED_XAXIS_TOL,100) and :V_STACK_LAYER_X_AXIS + NVL(B.YD_STK_BED_XAXIS_TOL,100)
			   AND  A.STACK_LAYER_Y_AXIS between :V_STACK_LAYER_Y_AXIS - NVL(B.YD_STK_BED_YAXIS_TOL,100) and :V_STACK_LAYER_Y_AXIS + NVL(B.YD_STK_BED_YAXIS_TOL,100)  */
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getXYLogicalInfo", logId, methodNm, "X,Y 물리 좌표값으로 논리위치(적치열,Bed)를 조회한다.");
			
			if(rsResult.size() < 1) {
				throw new Exception(" 논리 적치위치가 존재하지 않습니다!");
			}					
			if(rsResult.size() > 1) {
				throw new Exception(" 논리 적치위치가 여러건 존재합니다! 건수:"+rsResult.size());
			}					
			
			/******************************************************************
			* 2. 논리위치(적치열,Bed)에 적치중인 SLAB번호를 상단부터 조회한다.
			******************************************************************/
			String sSTACK_COL_GP = rsResult.getRecord(0).getFieldString("STACK_COL_GP");
			String sSTACK_BED_GP = rsResult.getRecord(0).getFieldString("STACK_BED_GP");
			
			jrParam.setField("STACK_COL_GP"		, sSTACK_COL_GP); 
			jrParam.setField("STACK_BED_GP"		, sSTACK_BED_GP); 
			jrParam.setField("STACK_LAYER_STAT"	, "C");
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPutStockIdByBedNo
			SELECT  LYR.STACK_COL_GP
			       ,LYR.STACK_BED_GP
			       ,LYR.STACK_LAYER_GP
			       ,LYR.STOCK_ID
			       ,LYR.STACK_LAYER_STAT
			       ,LYR.STACK_LAYER_ACTIVE_STAT
			       ,(
			            SELECT WBMTL.YD_WBOOK_ID
			              FROM TB_YM_WRKBOOK    WB
			                  ,TB_YM_WRKBOOKMTL WBMTL
			             WHERE WB.YD_WBOOK_ID = WBMTL.YD_WBOOK_ID
			               AND WB.DEL_YN = 'N'
			               AND WBMTL.DEL_YN = 'N'
			               AND WBMTL.STOCK_ID = LYR.STOCK_ID
			        ) AS YD_WBOOK_ID       
			  FROM  TB_YM_STACKLAYER LYR
			 WHERE  LYR.STACK_COL_GP = :V_STACK_COL_GP
			   AND  LYR.STACK_BED_GP = :V_STACK_BED_GP
			   AND  LYR.STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT || '%'
			   AND  LYR.STOCK_ID IS NOT NULL
			  ORDER BY LYR.STACK_LAYER_GP DESC  */
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPutStockIdByBedNo", logId, methodNm, "논리위치(적치열,Bed)에 적치중인 SLAB번호를 상단부터 조회한다. "); 
			
			
			/******************************************************************
			* 3. 크레인 정보(작업매수), 설비상태 가져오기
			******************************************************************/
			jrParam.setField("EQUIP_GP"    , sYD_EQP_ID); 
			/*
			 SELECT WPROG_STAT ,STACK_MAX_QNTY
   			   FROM TB_YM_EQUIP
              WHERE EQUIP_GP   =  :V_EQUIP_GP
			*/
			rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectEquipInfo", logId, methodNm, "설비상태조회");
			if(rsResult2.size() < 1) {
				throw new Exception(" 크레인 정보가  존재하지 않습니다! " + sYD_EQP_ID);
			}				
			
			String sWPROG_STAT 		= rsResult2.getRecord(0).getFieldString("WPROG_STAT"); //작업진행 상태
			
			if(YmConstant.WORK_PROG_STAT_3.equals(sWPROG_STAT)) { //권하지시상태
				throw new Exception(" 권하 지시 상태에서는 자동이적을 편성할 수 없습니다!! WPROG_STAT : " + sWPROG_STAT);
			}
			
			String sSTACK_MAX_QNTY	= rsResult2.getRecord(0).getFieldString("STACK_MAX_QNTY"); //적재 최대 수량
			
			int iYD_EQP_WRK_SH = Integer.parseInt(sYD_EQP_WRK_SH);   //요구수량
			int iSTACK_MAX_QNTY = Integer.parseInt(sSTACK_MAX_QNTY); //최대 적채가능 수량
			
			iMaxRec = iYD_EQP_WRK_SH;
			
			if(iMaxRec > iSTACK_MAX_QNTY) {
				iMaxRec = iSTACK_MAX_QNTY;
			}
			
			if(iMaxRec > rsResult.size()) {
				iMaxRec = rsResult.size();
			}
			
			/******************************************************************
			* 4. 작업예약 생성, 크레인스케줄 호출
			******************************************************************/
			String sYD_SCH_CD = null;
			String sYD_TO_LOC_GUIDE = null;
			String sYD_SCH_PRIOR = null;
			String sYD_WBOOK_ID = null;
			String sSTACK_LAYER_STAT = null;
			String sSTOCK_ID = null;
			
			for(int ii = 0; ii < iMaxRec; ii++) {
				
				sYD_WBOOK_ID = rsResult.getRecord(ii).getFieldString("YD_WBOOK_ID");
				sSTACK_LAYER_STAT = rsResult.getRecord(ii).getFieldString("STACK_LAYER_STAT");
				sSTOCK_ID = rsResult.getRecord(ii).getFieldString("STOCK_ID");
				
				if(YmConstant.STACK_LAYER_STAT_U.equals(sSTACK_LAYER_STAT)) {
					throw new Exception(" SLAB_NO : " + sSTOCK_ID + " 가 권상대기[U] 상태입니다.");
				} else if(YmConstant.STACK_LAYER_STAT_D.equals(sSTACK_LAYER_STAT)) {
					throw new Exception(" SLAB_NO : " + sSTOCK_ID + " 가 권하대기[D] 상태입니다.");
				} else if(!"".equals(sYD_WBOOK_ID)) {
					throw new Exception(" SLAB_NO : " + sSTOCK_ID + " 가 작업예약ID : " + sYD_WBOOK_ID + " 에  걸려 있는 상태입니다.");
				}
			}
			
			//스케줄코드, To위치 Guide 
			sYD_SCH_CD =  sYD_EQP_ID.substring(0,2)+"YD" + sYD_EQP_ID.substring(5) + "1MM"; //2AYD11MM, 2AYD21MM, 2BYD11MM, 2CYD11MM, 2CYD21MM, 2DYD11MM, 2DYD21MM, 2EYD11MM, 2EYD21MM
			sYD_TO_LOC_GUIDE = sYD_EQP_ID.substring(0,2); //2A, 2B, 2C, 2D, 2E
		
			//스케줄코드로 스케줄기준Table조회
			jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
	    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
			SELECT YD_SCH_CD
			     , YD_WRK_CRN
			     , YD_WRK_CRN_PRIOR
			  FROM TB_YM_SCHEDULERULE
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = :V_YD_SCH_CD
			*/   
			rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
	    	
			if (rsResult2 != null && rsResult2.size() > 0) {
				sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			} else {
				throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sYD_SCH_CD + "]");
			}			
			
			//작업예약ID생성
			sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
		
			/**********************************************************
			* 2-1. 작업예약(TB_YM_WRKBOOK) 생성
			**********************************************************/
			jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
			jrParam.setField("YD_GP"			, YmConstant.YD_GP_2);
			jrParam.setField("YD_BAY_GP"		, sYD_SCH_CD.substring(1,2));
			jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD); //야드스케쥴코드
			jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
			jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
			jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
			jrParam.setField("YD_TO_LOC_GUIDE"	, sYD_TO_LOC_GUIDE); //야드To위치Guide
			
			commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
			
			/**********************************************************
			* 2-2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
			**********************************************************/
			for(int ii = 0; ii < iMaxRec; ii++) {
				jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
				jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STOCK_ID")));
				jrParam.setField("STACK_COL_GP"		, commUtils.trim(rsResult.getRecord(ii).getFieldString("STACK_COL_GP")));
				jrParam.setField("STACK_BED_GP"		, commUtils.trim(rsResult.getRecord(ii).getFieldString("STACK_BED_GP")));
				jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(rsResult.getRecord(ii).getFieldString("STACK_LAYER_GP")));
				
				commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
				
				/**********************************************************
				* 2-3. TB_YM_STOCK의  저장품 이동조건 등록
				**********************************************************/
				jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STOCK_ID")));
				jrParam.setField("STOCK_MOVE_TERM"	, bSlabComm.getStockMoveTerm(commUtils.trim(rsResult.getRecord(ii).getFieldString("STOCK_ID")))); //진도코드에 따른 저장품 이동조건
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 저장품 이동 조건 UPDATE");
			}
			
			//스케줄 메인 호출
			//JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
			//jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
			//jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
			//jrCrnSchMsg.setField("YD_WBOOK_ID"  		, sYD_WBOOK_ID); //작업예약ID
			//jrCrnSchMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
			//jrCrnSchMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
			//jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);		
			
			//Slab Schedule EJB Call
			jrParam.setField("JMS_TC_CD"    , "YMYMJ202"); 
			jrParam.setField("YD_WBOOK_ID"  , sYD_WBOOK_ID); //야드작업예약ID
			jrParam.setField("YD_SCH_CD"    , ""  ); //야드스케쥴코드
			jrParam.setField("YD_EQP_ID"    , ""  ); //야드설비ID
			
			EJBConnector ejbConn = new EJBConnector("default","BSlabSchSeEJB",this);
			jrRtn = (JDTORecord)ejbConn.trx("procYMYMJ202",new Class[]{JDTORecord.class},new Object[]{ jrParam });			
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
			
		}catch(DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 자동이적요구(A8YML027)
	
	/**
	 *      [A] 오퍼레이션명 : 슬라브 W/B Tracking 정보(A8YML021)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML028(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "슬라브 W/B Tracking 정보(A8YML028) < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "슬라브 W/B Tracking정보(rcvA8YML028) 수신 ", rcvMsg);

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			
			String WB51_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB51_SLAB_NO"));
			String WB52_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB52_SLAB_NO"));
			String WB53_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB53_SLAB_NO"));
			String WB54_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB54_SLAB_NO"));
			String WB41_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB41_SLAB_NO"));
			String WB42_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB42_SLAB_NO"));
			String WB43_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB43_SLAB_NO"));
			String WB44_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB44_SLAB_NO"));
			String WB31_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB31_SLAB_NO"));
			String WB32_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB32_SLAB_NO"));
			String WB33_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB33_SLAB_NO"));
			String WB34_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB34_SLAB_NO"));
			String WB21_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB21_SLAB_NO"));
			String WB22_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB22_SLAB_NO"));
			String WB23_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB23_SLAB_NO"));
			String WB24_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB24_SLAB_NO"));
			String WB11_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB11_SLAB_NO"));
			String WB12_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB12_SLAB_NO"));
			String WB13_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB13_SLAB_NO"));
			String WB14_SLAB_NO   = commUtils.trim(rcvMsg.getFieldString("WB14_SLAB_NO"));		

			
			
			if ("".equals(modifier)) { modifier = msgId; }
			
			String L3Hmi         = commUtils.nvl(rcvMsg.getFieldString("L3_HMI"),"N");         //백업화면 기동 여부

			if(L3Hmi.equals("N")) {   // L2전문수신으로 실행된 경우
				
				/**********************************************************
				* 1. 조업 TRACKING UPDATE
				**********************************************************/
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
				jrYdMsg.setField("MODIFIER"     , modifier);					//수정자
				jrYdMsg.setField("STLNO1"     	, WB51_SLAB_NO);  //WB51_SLAB_NO
				jrYdMsg.setField("STLNO2"     	, WB52_SLAB_NO);  //WB52_SLAB_NO
				jrYdMsg.setField("STLNO3"     	, WB53_SLAB_NO);  //WB53_SLAB_NO
				jrYdMsg.setField("STLNO4"     	, WB54_SLAB_NO);  //WB54_SLAB_NO
				jrYdMsg.setField("STLNO5"     	, WB41_SLAB_NO);  //WB41_SLAB_NO
				jrYdMsg.setField("STLNO6"     	, WB42_SLAB_NO);  //WB42_SLAB_NO
				jrYdMsg.setField("STLNO7"     	, WB43_SLAB_NO);  //WB43_SLAB_NO
				jrYdMsg.setField("STLNO8"     	, WB44_SLAB_NO);  //WB44_SLAB_NO	
				jrYdMsg.setField("STLNO9"     	, WB31_SLAB_NO);  //WB31_SLAB_NO
				jrYdMsg.setField("STLNO10"     	, WB32_SLAB_NO);  //WB32_SLAB_NO
				jrYdMsg.setField("STLNO11"     	, WB33_SLAB_NO);  //WB33_SLAB_NO
				jrYdMsg.setField("STLNO12"     	, WB34_SLAB_NO);  //WB34_SLAB_NO	
				jrYdMsg.setField("STLNO13"     	, WB21_SLAB_NO);  //WB21_SLAB_NO
				jrYdMsg.setField("STLNO14"     	, WB22_SLAB_NO);  //WB22_SLAB_NO
				jrYdMsg.setField("STLNO15"     	, WB23_SLAB_NO);  //WB23_SLAB_NO
				jrYdMsg.setField("STLNO16"     	, WB24_SLAB_NO);  //WB24_SLAB_NO	
				jrYdMsg.setField("STLNO17"     	, WB11_SLAB_NO);  //WB11_SLAB_NO
				jrYdMsg.setField("STLNO18"     	, WB12_SLAB_NO);  //WB12_SLAB_NO
				jrYdMsg.setField("STLNO19"     	, WB13_SLAB_NO);  //WB13_SLAB_NO
				jrYdMsg.setField("STLNO20"     	, WB14_SLAB_NO);  //WB14_SLAB_NO		
	
				commUtils.printParam(logId + "슬라브 W/B Tracking정보(rcvA8YML028) 수신 ", jrYdMsg);
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updEqpTrackingW 
				MERGE INTO TB_YM_EQPTRACKING ET USING (
				SELECT A.STL_NO 
				     , B.STL_NO           AS STL_NO_NEW
				     , B.SORT_SEQ
				  FROM TB_YM_EQPTRACKING A
				     , (
				        SELECT :V_STLNO1  AS STL_NO ,  1  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO2  AS STL_NO ,  2  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO3  AS STL_NO ,  3  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO4  AS STL_NO ,  4  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO5  AS STL_NO ,  5  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO6  AS STL_NO ,  6  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO7  AS STL_NO ,  7  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO8  AS STL_NO ,  8  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO9  AS STL_NO ,  9  AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO10 AS STL_NO ,  10 AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO11 AS STL_NO ,  11 AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO12 AS STL_NO ,  12 AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO13 AS STL_NO ,  13 AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO14 AS STL_NO ,  14 AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO15 AS STL_NO ,  15 AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO16 AS STL_NO ,  16 AS SORT_SEQ  FROM DUAL UNION ALL	
				        SELECT :V_STLNO17 AS STL_NO ,  17 AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO18 AS STL_NO ,  18 AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO19 AS STL_NO ,  19 AS SORT_SEQ  FROM DUAL UNION ALL
				        SELECT :V_STLNO20 AS STL_NO ,  20 AS SORT_SEQ  FROM DUAL 
				     ) B
				WHERE A.YD_GP     = '2'
				  AND A.PROC_GP   = 'W'
				  AND A.EQUIP_GP  LIKE 'WB%'
				  AND A.SORT_SEQ  = B.SORT_SEQ
				  
				) DD ON (ET.YD_GP = '2' AND ET.PROC_GP = 'W' AND ET.EQUIP_GP LIKE 'WB%' AND  ET.SORT_SEQ = DD.SORT_SEQ)
				WHEN MATCHED THEN UPDATE SET
				     STL_NO     = STL_NO_NEW
				   , MOD_DDTT   = SYSDATE
				   , MODIFIER   = :V_MODIFIER
				*/   
				commDao.update(jrYdMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updEqpTrackingW", logId, methodNm, "W/B수정");
				
				
				
				//Tracking Table 2CWB01 05BED 정보를 적치단에 설정
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updWBLyrInfoByTracking
				MERGE INTO TB_YM_STACKLAYER SL USING (
	
				    SELECT STACK_COL_GP
				          ,STACK_BED_GP
				          ,LOC_NO
				          ,STL_NO
				      FROM TB_YM_EQPTRACKING   
				     WHERE YD_GP = '2'
				       AND PROC_GP = 'W'
				       AND EQUIP_GP LIKE 'WB5%'
	
				) DD ON (SL.STACK_COL_GP = DD.STACK_COL_GP AND SL.STACK_BED_GP = DD.STACK_BED_GP AND SL.STACK_LAYER_GP = DD.LOC_NO )
				WHEN MATCHED THEN UPDATE SET
				     STOCK_ID   = DD.STL_NO
				   , STACK_LAYER_STAT = DECODE(NVL(DD.STL_NO,''),'','E','C')
				   , MOD_DDTT   = SYSDATE
				   , MODIFIER   = :V_MODIFIER	  */ 
				commDao.update(jrYdMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updWBLyrInfoByTracking", logId, methodNm, "Tracking 테이블에서 적치단 WB 05 BED 정보를  update ");
				
				
				//=============================================================================================================================================
//				String sYM2011_2CWB01UM28_YN = ymComm.BCoilApplyYn("YM2011","2","2CWB01UM28");
//				
//				if("Y".equals(sYM2011_2CWB01UM28_YN)) {
//					
//					jrParam.setField("EQUIP_GP"    , "2CWB01"); //W/B
//					/*
//					 SELECT HMI_STAT ,WORK_MODE
//		   			   FROM TB_YM_EQUIP
//		              WHERE EQUIP_GP   =  :V_EQUIP_GP
//					*/
//					JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectEquipInfo", logId, methodNm, "설비상태조회");
//					if(jsChk.size() > 0) {
//						if("1".equals(jsChk.getRecord(0).getFieldString("WORK_MODE"))) { //선작업 실행일 경우
//							
//							jrParam.setField("STACK_COL_GP"    , "2CWB01");
//							jrParam.setField("STACK_BED_GP"    , "01");
//							/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPutStockIdByBedNo
//							SELECT  LYR.STACK_COL_GP
//							       ,LYR.STACK_BED_GP
//							       ,LYR.STACK_LAYER_GP
//							       ,LYR.STOCK_ID
//							       ,LYR.STACK_LAYER_STAT
//							       ,LYR.STACK_LAYER_ACTIVE_STAT
//							       ,(
//							            SELECT WBMTL.YD_WBOOK_ID
//							              FROM TB_YM_WRKBOOK    WB
//							                  ,TB_YM_WRKBOOKMTL WBMTL
//							             WHERE WB.YD_WBOOK_ID = WBMTL.YD_WBOOK_ID
//							               AND WB.DEL_YN = 'N'
//							               AND WBMTL.DEL_YN = 'N'
//							               AND WBMTL.STOCK_ID = LYR.STOCK_ID
//							        ) AS YD_WBOOK_ID       
//							  FROM  TB_YM_STACKLAYER LYR
//							 WHERE  LYR.STACK_COL_GP = :V_STACK_COL_GP
//							   AND  LYR.STACK_BED_GP = :V_STACK_BED_GP
//							   AND  LYR.STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT || '%'
//							   AND  LYR.STOCK_ID IS NOT NULL
//							  ORDER BY LYR.STACK_LAYER_GP DESC */
//							jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPutStockIdByBedNo", logId, methodNm, "WB 01Bed 조회");
//							
//							if(jsChk.size() == 0) {
//								
//								//선작업 실행이면서 워킹빔 01 BED가  비여 있으면 
//								//워킹빔 보급 스케줄이 존재하는지 체크한다.
//								jrParam.setField("YD_SCH_CD" 		, "2CWB01UM"); 
//								jrParam.setField("YD_CRN_SCH_ID"	, "");
//								/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch
//								--권상,권하시 해당 크레인스케줄 id를 제외한 동일 스케줄 작업이 만들어져 있는지 조회
//								SELECT CS.YD_CRN_SCH_ID
//								      ,CS.YD_SCH_CD
//								      ,CS.YD_UP_WO_LOC
//								      ,CS.YD_DN_WO_LOC
//								      ,CM.STOCK_ID
//								      ,CM.YD_AID_WRK_YN
//								FROM   TB_YM_CRNSCH CS
//								      ,TB_YM_CRNWRKMTL CM
//								WHERE  CS.DEL_YN = 'N'
//								  AND  CM.DEL_YN = 'N'
//								  AND  CS.YD_SCH_CD LIKE :V_YD_SCH_CD ||'%'
//								  AND  CS.YD_CRN_SCH_ID NOT IN (NVL(:V_YD_CRN_SCH_ID,'1'))
//								  AND  CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
//								  AND  CM.YD_AID_WRK_YN = 'N'
//								 */
//								jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch", logId, methodNm, "선작업지시 존재여부 체크");
//								
//								if(jsChk.size() == 0) {
//								
//									jrParam.setField("YD_SCH_CD"    , "2CWB01UM");
//									/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule 
//									SELECT YD_SCH_CD
//									     , YD_WRK_CRN
//									     , YD_WRK_CRN_PRIOR
//									     , YD_MULTI_WRK_YN
//									  FROM TB_YM_SCHEDULERULE
//									 WHERE DEL_YN = 'N'
//									   AND YD_SCH_CD = :V_YD_SCH_CD */
//									jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케쥴기준조회");
//									
//									//W/B 보급 선작업 실행
//									jrParam.setResultCode(logId);	//Log ID
//									jrParam.setResultMsg(methodNm);	//Log Method Name
//									jrParam.setField("JMS_TC_CD", "A8YML020" );
//									jrParam.setField("YD_WRK_PLAN_CRN", jsChk.getRecord(0).getFieldString("YD_WRK_CRN"));
//									
//									commUtils.printLog(logId, "=======:::: WB 트래킹정보 수신시  선작업 실행인데 워킹빔 보급 스케줄 없고  WB 01 Bed가 빈 상태임으로  WB장입(선작업)호출", "SL");
//									
//									jrRtn = commUtils.addSndData(jrRtn, this.rcvA8YML020(jrParam));
//								
//								}
//							}
//							
//						}
//					}	
//					
//				}
				//=============================================================================================================================================
				
				
			}
			
			/**********************************************************
			* 11. STE 고장에 따른 크레인 작업 지시 호출
			**********************************************************/
			//JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"     , modifier);					//수정자
			jrParam.setField("YD_EQP_ID"	, "2CST03"); 
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStatEqp", logId, methodNm, "설비상태조회");
			if(jsChk.size()>0) {
				if("B".equals(commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT")))) { 
					
					//STE 고장!
					
					////선작업지시 존재여부 체크
					//jrParam.setField("YD_SCH_CD" 		, "2CHB02UM"); 
					//jrParam.setField("YD_CRN_SCH_ID"	, "");
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch
					--권상,권하시 해당 크레인스케줄 id를 제외한 동일 스케줄 작업이 만들어져 있는지 조회
					SELECT CS.YD_CRN_SCH_ID
					      ,CS.YD_SCH_CD
					      ,CS.YD_UP_WO_LOC
					      ,CS.YD_DN_WO_LOC
					      ,CM.STOCK_ID
					      ,CM.YD_AID_WRK_YN
					FROM   TB_YM_CRNSCH CS
					      ,TB_YM_CRNWRKMTL CM
					WHERE  CS.DEL_YN = 'N'
					  AND  CM.DEL_YN = 'N'
					  AND  CS.YD_SCH_CD LIKE :V_YD_SCH_CD ||'%'
					  AND  CS.YD_CRN_SCH_ID NOT IN (NVL(:V_YD_CRN_SCH_ID,'1'))
					  AND  CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					  AND  CM.YD_AID_WRK_YN = 'N'
					 */
					//JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch", logId, methodNm, "선작업지시 존재여부 체크");			
					
					//if(rsResult.size() == 0) {
						
						//선작업지시가 없을 경우
						
						//적치단 테이블에서 정보를 읽어 온다.
						jrParam.setField("STACK_COL_GP"		, "2CWB01"); 
						jrParam.setField("STACK_BED_GP"		, "05"); 
						jrParam.setField("STACK_LAYER_STAT"	, "C");
						/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPutStockIdByBedNo
						SELECT  LYR.STACK_COL_GP
						       ,LYR.STACK_BED_GP
						       ,LYR.STACK_LAYER_GP
						       ,LYR.STOCK_ID
						       ,LYR.STACK_LAYER_STAT
						       ,LYR.STACK_LAYER_ACTIVE_STAT
						       ,(
						            SELECT WBMTL.YD_WBOOK_ID
						              FROM TB_YM_WRKBOOK    WB
						                  ,TB_YM_WRKBOOKMTL WBMTL
						             WHERE WB.YD_WBOOK_ID = WBMTL.YD_WBOOK_ID
						               AND WB.DEL_YN = 'N'
						               AND WBMTL.DEL_YN = 'N'
						               AND WBMTL.STOCK_ID = LYR.STOCK_ID
						        ) AS YD_WBOOK_ID       
						  FROM  TB_YM_STACKLAYER LYR
						 WHERE  LYR.STACK_COL_GP = :V_STACK_COL_GP
						   AND  LYR.STACK_BED_GP = :V_STACK_BED_GP
						   AND  LYR.STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT || '%'
						   AND  LYR.STOCK_ID IS NOT NULL
						  ORDER BY LYR.STACK_LAYER_GP DESC  */
						JDTORecordSet rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPutStockIdByBedNo", logId, methodNm, "W/B 05Bed 조회 "); 
						if(rsResult2.size() > 0) {
							
							String sYD_WBOOK_ID = rsResult2.getRecord(0).getFieldString("YD_WBOOK_ID");
							
							if("".equals(sYD_WBOOK_ID)) {
							
								//CTC4 트랙킹 관리가 안되기 때문에 크레인 작업지시 생성 전에 Clear
								jrParam.setField("STOCK_ID", ""); 
								jrParam.setField("STACK_LAYER_STAT2", "E"); 
								jrParam.setField("STACK_COL_GP"		, "2CCT04"); 
								jrParam.setField("STACK_BED_GP"		, "01"); 
								jrParam.setField("STACK_LAYER_STAT1", "");
								/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo 
								UPDATE TB_YM_STACKLAYER
								   SET 
								       MODIFIER = :V_MODIFIER
								      ,MOD_DDTT = SYSDATE
								      ,STOCK_ID = :V_STOCK_ID
								      ,STACK_LAYER_STAT = :V_STACK_LAYER_STAT2
								 WHERE  STACK_COL_GP = :V_STACK_COL_GP
								   AND  STACK_BED_GP = :V_STACK_BED_GP
								   AND  STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT1 || '%' */
								commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo", logId, methodNm, "CTC4 Clear");
								
								String sYD_SCH_CD = "2CHB02UM"; //C동 STE 비상보급
								String sYD_TO_LOC_GUIDE = "2CCT040101"; //sYD_TO_LOC_GUIDE = "2CCT040101";
								
								
								//CTC4 고장시 비상보급 TO위치 L4 테이블 (2CLT04) 지정 여부
								String sAPP100_ZY001_YN = ymComm.BCoilApplyYn("APP100","2","ZY001_YN");
								
								if("Y".equals(sAPP100_ZY001_YN)) {
									
									//CTC#4 고장여부 확인
									jrParam.setField("EQUIP_GP"  , "2CCT04"); 
									
									JDTORecordSet rsCtc4Stat = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectEquipInfo", logId, methodNm, "CTC#4 고장여부 확인");

									if(rsCtc4Stat.size() > 0) {
										if("B".equals(rsCtc4Stat.getRecord(0).getFieldString("WPROG_STAT"))) {
											commUtils.printLog(logId, "=======:::: CTC#4 고장 = TO위치가이드를 2CLT04 로 변경  " , "SL");
											sYD_TO_LOC_GUIDE = "2CLT040101"; //CTC#4 고장일 경우 TO위치 L4 테이블
										}
									}
								}
								
								
								//스케줄코드로 스케줄기준Table조회
								jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
						    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
								SELECT YD_SCH_CD
								     , YD_WRK_CRN
								     , YD_WRK_CRN_PRIOR
								  FROM TB_YM_SCHEDULERULE
								 WHERE DEL_YN = 'N'
								   AND YD_SCH_CD = :V_YD_SCH_CD
								*/   
								JDTORecordSet rsResult3 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
								
								if(rsResult3.size() > 0) {
									String sYD_SCH_PRIOR = rsResult3.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
									
									if(rsResult2.size() > 1) {
										//W/B 05Bed 에  2단 적치 된 상태 일경우 1단에 적치된 SLAB는 STE비상보급 작업예약만 생성해 놓고 다음 Tracking 정보 (2단 작업후) 수신시
										// 만들어진 작업예약이 수행되게 된다.
										
										//작업예약ID생성
										sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
									
										/**********************************************************
										* 2-1. 작업예약(TB_YM_WRKBOOK) 생성
										**********************************************************/
										jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
										jrParam.setField("YD_GP"			, YmConstant.YD_GP_2);
										jrParam.setField("YD_BAY_GP"		, sYD_SCH_CD.substring(1,2));
										jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD); //야드스케쥴코드
										jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
										jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
										jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
										jrParam.setField("YD_TO_LOC_GUIDE"	, sYD_TO_LOC_GUIDE); //야드To위치Guide
										
										commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
										
										/**********************************************************
										* 2-2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
										**********************************************************/
										
										jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
										jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult2.getRecord(1).getFieldString("STOCK_ID")));
										jrParam.setField("STACK_COL_GP"		, commUtils.trim(rsResult2.getRecord(1).getFieldString("STACK_COL_GP")));
										jrParam.setField("STACK_BED_GP"		, commUtils.trim(rsResult2.getRecord(1).getFieldString("STACK_BED_GP")));
										jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(rsResult2.getRecord(1).getFieldString("STACK_LAYER_GP")));
										
										commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
										
										/**********************************************************
										* 2-3. TB_YM_STOCK의 저장품 이동조건 등록
										**********************************************************/
										jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult2.getRecord(1).getFieldString("STOCK_ID")));
										jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_FS ); //압연작업대기
										
										commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 저장품 이동 조건 UPDATE");
										
										
									}
									
									//작업예약ID생성
									sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
								
									/**********************************************************
									* 2-1. 작업예약(TB_YM_WRKBOOK) 생성
									**********************************************************/
									jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
									jrParam.setField("YD_GP"			, YmConstant.YD_GP_2);
									jrParam.setField("YD_BAY_GP"		, sYD_SCH_CD.substring(1,2));
									jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD); //야드스케쥴코드
									jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
									jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
									jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
									jrParam.setField("YD_TO_LOC_GUIDE"	, sYD_TO_LOC_GUIDE); //야드To위치Guide
									
									commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
									
									/**********************************************************
									* 2-2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
									**********************************************************/
									
									jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
									jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult2.getRecord(0).getFieldString("STOCK_ID")));
									jrParam.setField("STACK_COL_GP"		, commUtils.trim(rsResult2.getRecord(0).getFieldString("STACK_COL_GP")));
									jrParam.setField("STACK_BED_GP"		, commUtils.trim(rsResult2.getRecord(0).getFieldString("STACK_BED_GP")));
									jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(rsResult2.getRecord(0).getFieldString("STACK_LAYER_GP")));
									
									commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
									
									/**********************************************************
									* 2-3. TB_YM_STOCK의 저장품 이동조건 등록
									**********************************************************/
									jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult2.getRecord(0).getFieldString("STOCK_ID")));
									jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_FS ); //압연작업대기
									
									commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 저장품 이동 조건 UPDATE");
									
								}
							}
							
							//스케줄 메인 호출
							JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
							jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
							jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
							jrCrnSchMsg.setField("YD_WBOOK_ID"  		, sYD_WBOOK_ID); //작업예약ID
							jrCrnSchMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
							jrCrnSchMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
							jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);		
						}
						
					//}
					
				}
			}
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 슬라브 W/B Tracking 정보(A8YML028)

	/**
	 *      [A] 오퍼레이션명 : Slab 무게중심(형상인식)측정 정보(A8YML029)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML029(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Slab 무게중심(형상인식)측정 정보[BSlabL2RcvSeEJB.rcvA8YML029] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam	 = JDTORecordFactory.getInstance().create(); //Query 실행시 파라메터 전달용 JDTORecord 
	    JDTORecordSet rsResult    	= null;
	    JDTORecordSet rsResult2    	= null;
		
	    String msgId;
	    String modifier;
		
	    try{
			commUtils.printLog(logId, methodNm, "S+");
	    	
			String sPT_LOAD_LOC		= commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"));     //상차도 위치
			String sCAR_NO   		= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));     		//차량번호
			String sYD_EQP_WRK_SH	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH")); //야드설비작업매수
			String sCAU_CD			= commUtils.trim(rcvMsg.getFieldString("CAU_CD"));			//형상원인코드
			msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"A8YML029"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
			
			int iYD_EQP_WRK_SH = Integer.parseInt(sYD_EQP_WRK_SH);
			
			String sSLAB_NO;			//SLAB번호
			String sLOAD_LOC_CD;		//차량적재위치
			String sWGT_CENTER_XAXIS;	//야드크레인X축(mm)
			String sWGT_CENTER_YAXIS;	//야드크레인Y축(mm)
			String sWGT_CENTER_ZAXIS;	//야드크레인Z축(mm)
			String sBENDING_GP;			//BENDING 구분(+:상,-:하)
			String sBENDING_AXIS;		//BENDING 량(mm)
			String sYD_STK_COL_DIR_GP;	//야드적치열방향구분(+:좌,-:우)
			String sYD_STK_COL_DEG;		//재료적치각도(소재의 뒤츨린 각도)
			
			String sNO;
			
			String [] SLAB_NO_IN_CAR;	//SLAB 번호 배열 (이송하차시, 스캔 슬라브번호와 정보상 슬라브번호 확인용)
			
			String s_YD_CAR_SCH_ID = "";
			String sFRTOMOVE_WORD_NO = ""; //이송작업지시 번호
			
			if(iYD_EQP_WRK_SH == 0) {
				
				sSLAB_NO = commUtils.trim(rcvMsg.getFieldString("SLAB_NO1"));
				
				if(sPT_LOAD_LOC.equals(sSLAB_NO)) {
					//이송상차 형상인식 스켄 값 
					commUtils.printLog(logId, "::::::>>>> 이송상차 형상 XYZ 값 수신 : " + sPT_LOAD_LOC + " , 차량번호 : " + sCAR_NO , "SL");
					commUtils.printLog(logId, methodNm, "S-");

					sWGT_CENTER_XAXIS 	= commUtils.trim(rcvMsg.getFieldString("WGT_CENTER_XAXIS1"));  //"WGT_CENTER_XAXIS"+sNO
					sWGT_CENTER_YAXIS 	= commUtils.trim(rcvMsg.getFieldString("WGT_CENTER_YAXIS1"));  //"WGT_CENTER_YAXIS"+sNO
					sWGT_CENTER_ZAXIS 	= commUtils.trim(rcvMsg.getFieldString("WGT_CENTER_ZAXIS1"));  //"WGT_CENTER_ZAXIS"+sNO
					
					jrParam.setField("REPR_CD_GP"	, "YM2019" );
					jrParam.setField("CD_GP"		, "2" );
					jrParam.setField("ITEM"			, sPT_LOAD_LOC);
					jrParam.setField("DTL_ITM1"		, sWGT_CENTER_XAXIS);
					jrParam.setField("DTL_ITM2"		, sWGT_CENTER_YAXIS);
					jrParam.setField("DTL_ITM3"		, sWGT_CENTER_ZAXIS);
					jrParam.setField("DTL_ITM4"		, sCAU_CD);
					jrParam.setField("DTL_ITM5"		, "Y");

					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYmRuleNvl", logId, methodNm, "TB_YM_RULE YM2019 수정 - 이송상차용 형상 XYZ 값");
					
				}
				
				commUtils.printLog(logId, methodNm, "S-");
				
		    	return sndRecord;
			}
			//운송장비코드로 이송재료 조회 
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getListFrtostlList
			SELECT  STL_NO
			       ,A.YD_STK_BED_NO
			       ,SUBSTR(A.YD_STK_LYR_NO,2,2) AS STK_LYR
			       ,A.YD_CAR_SCH_ID
			       ,(SELECT CS.YD_CARUD_WRK_BOOK_ID FROM TB_YD_CARSCH CS WHERE CS.YD_CAR_SCH_ID = A.YD_CAR_SCH_ID) AS YD_CARUD_WRK_BOOK_ID
			       ,(SELECT CS.FRTOMOVE_WORD_NO FROM TB_YD_CARSCH CS WHERE CS.YD_CAR_SCH_ID = A.YD_CAR_SCH_ID) AS FRTOMOVE_WORD_NO
			  FROM TB_YD_CARFTMVMTL A 
			 WHERE A.YD_CAR_SCH_ID = (SELECT MAX(A.YD_CAR_SCH_ID)
			                            FROM TB_YD_CARSCH A
			                           WHERE A.TRN_EQP_CD = :V_TRN_EQP_CD  
			                             AND A.DEL_YN = 'N')
			   AND A.DEL_YN = 'N'
			 ORDER BY A.YD_STK_LYR_NO  */
			jrParam.setField("TRN_EQP_CD"	, sCAR_NO); 
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getListFrtostlList", logId, methodNm, "운송장비코드로 이송재료 조회");
			if(rsResult.size() <= 0) {
				throw new Exception("영차(F) 도착처리 대상재가 존재 안함");
			} else {
				SLAB_NO_IN_CAR = new String[rsResult.size()];
				for(int i=0; i<rsResult.size(); i++){
					SLAB_NO_IN_CAR[i] = rsResult.getRecord(i).getFieldString("STL_NO");
				}
				s_YD_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
				//wbook_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CARUD_WRK_BOOK_ID"));
				sFRTOMOVE_WORD_NO = commUtils.trim(rsResult.getRecord(0).getFieldString("FRTOMOVE_WORD_NO"));
			}
					
			String sSLAB_NOS ="";	   //SLAB번호들
			for(int ii = 1; ii <= iYD_EQP_WRK_SH; ii++) {

				sNO = commUtils.getLPadZero(Integer.toString(ii), 1); //"01", "02", "03" ... "14", "15"
				
				sSLAB_NO 		    = commUtils.trim(rcvMsg.getFieldString("SLAB_NO"          +ii));  //"SLAB_NO"+sNO
				sLOAD_LOC_CD 		= commUtils.trim(rcvMsg.getFieldString("LOAD_LOC_CD"      +ii));  //"LOAD_LOC_CD"+sNO
				sWGT_CENTER_XAXIS 	= commUtils.trim(rcvMsg.getFieldString("WGT_CENTER_XAXIS" +ii));  //"WGT_CENTER_XAXIS"+sNO
				sWGT_CENTER_YAXIS 	= commUtils.trim(rcvMsg.getFieldString("WGT_CENTER_YAXIS" +ii));  //"WGT_CENTER_YAXIS"+sNO
				sWGT_CENTER_ZAXIS 	= commUtils.trim(rcvMsg.getFieldString("WGT_CENTER_ZAXIS" +ii));  //"WGT_CENTER_ZAXIS"+sNO
				sBENDING_GP		    = commUtils.trim(rcvMsg.getFieldString("BENDING_GP"       +ii));  //"BENDING_GP"+sNO
				sBENDING_AXIS		= commUtils.trim(rcvMsg.getFieldString("BENDING_AXIS"     +ii));  //"BENDING_AXIS"+sNO
				sYD_STK_COL_DIR_GP	= commUtils.trim(rcvMsg.getFieldString("YD_STK_COL_DIR_GP"+ii));  //"YD_STK_COL_DIR_GP"+sNO
				sYD_STK_COL_DEG		= commUtils.trim(rcvMsg.getFieldString("YD_STK_COL_DEG"   +ii));  //"YD_STK_COL_DEG"+sNO
				
				//2023.02.20 열연1부 김기훈주임님 요청. 차량에 상차되어있지 않은 슬라브 스캔시 작업지시 생성되지 않게끔.
				boolean isExist = false;
				for(int jj=0; jj<SLAB_NO_IN_CAR.length; jj++){
					if(sSLAB_NO.equals(SLAB_NO_IN_CAR[jj])){
						isExist = true;
					}
				}
				if(!isExist){
					throw new Exception("차량번호["+sCAR_NO+ "]에 상차되어있지 않은 슬라브["+sSLAB_NO+"]가 스캔되었습니다.");
				}
				
				jrParam.setField("STOCK_ID"			, sSLAB_NO); 
				jrParam.setField("LOAD_LOC_CD"		, sLOAD_LOC_CD); 
				jrParam.setField("WGT_CENTER_XAXIS"	, sWGT_CENTER_XAXIS); 
				jrParam.setField("WGT_CENTER_YAXIS"	, sWGT_CENTER_YAXIS); 
				jrParam.setField("WGT_CENTER_ZAXIS"	, sWGT_CENTER_ZAXIS); 
				jrParam.setField("BENDING_GP"		, sBENDING_GP); 
				jrParam.setField("BENDING_AXIS"		, sBENDING_AXIS); 
				jrParam.setField("YD_STK_COL_DIR_GP", sYD_STK_COL_DIR_GP); 
				jrParam.setField("YD_STK_COL_DEG"	, sYD_STK_COL_DEG); 
				jrParam.setField("CAU_CD"			, sCAU_CD);
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockByA8YML029
				UPDATE TB_YM_STOCK
				   SET MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,LOAD_LOC_CD = :V_LOAD_LOC_CD
				      ,WGT_CENTER_XAXIS = :V_WGT_CENTER_XAXIS
				      ,WGT_CENTER_YAXIS = :V_WGT_CENTER_YAXIS
				      ,WGT_CENTER_ZAXIS = :V_WGT_CENTER_ZAXIS
				      ,BENDING_GP = :V_BENDING_GP
				      ,BENDING_AXIS = :V_BENDING_AXIS
				      ,YD_STK_COL_DIR_GP = :V_YD_STK_COL_DIR_GP
				      ,YD_STK_COL_DEG = :V_YD_STK_COL_DEG
				      ,YD_RULE_PL_RS_GP = CASE WHEN TO_NUMBER(NVL(:V_BENDING_AXIS,0)) >= (SELECT NVL(DTL_ITM1,0)
				                                                                                FROM USRYMA.TB_YM_RULE 
				                                                                               WHERE CD_GP = '2' 
				                                                                                 AND REPR_CD_GP LIKE'YM2009'
				                                                                                 AND DEL_YN = 'N') THEN 'Y'
				                               ELSE '' END 
				      ,CAU_CD = NVL(:V_CAU_CD,'0000');
				 WHERE STOCK_ID = :V_STOCK_ID  */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockByA8YML029", logId, methodNm, "slab 무게중심(형상인식)측정 정보 수신처리 (tb_ym_stock 관련 항목 update)");
				
				if(ii > 0){
					sSLAB_NOS += ",";
				}
				sSLAB_NOS += sSLAB_NO;
			}
			
			
			if(sPT_LOAD_LOC.length() < 6) {
				throw new Exception("상차도 위치 [" + sPT_LOAD_LOC + "] 가 6자리가 아닙니다!!");
			}
			
			if("4003".equals(sCAU_CD) || "4007".equals(sCAU_CD)) {
				
				commUtils.printLog(logId, "::::::>>>> 형상원인코드 : " + sCAU_CD + " 로 자동스케줄 생성이 안됩니다!!!!!! ", "SL");
				commUtils.printLog(logId, methodNm, "S-");
				
		    	return sndRecord;
			}
			
			if("2DPT".equals(sPT_LOAD_LOC.substring(0,4))||"2APT".equals(sPT_LOAD_LOC.substring(0,4))||"2EPT".equals(sPT_LOAD_LOC.substring(0,4))) {
				//상차도 위치가 D,A,E 동 차량 포인트 일 경우...
				
				//차량진행상태가 'A':하차출발, 'B'하차 도착일 경우만 실행
				String sYD_CAR_PROG_STAT = "";	//차량진행상태	
				jrParam.setField("YD_STK_COL_GP"	, sPT_LOAD_LOC);
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdCarPnt", logId, methodNm, "상차도로 차량진행상태 조회"); 
				if(rsResult2.size() > 0) {
					sYD_CAR_PROG_STAT = commUtils.trim(rsResult2.getRecord(0).getFieldString("YD_CAR_PROG_STAT"));
				}
				
				if("1".equals(sYD_CAR_PROG_STAT) 
						|| "2".equals(sYD_CAR_PROG_STAT) 
						|| "3".equals(sYD_CAR_PROG_STAT) 
						|| "4".equals(sYD_CAR_PROG_STAT) 
						|| "5".equals(sYD_CAR_PROG_STAT)
						|| "".equals(sYD_CAR_PROG_STAT)) {
					//이송상차 일 경우 이하 로직 처리 안함
					commUtils.printLog(logId, "::::::>>>> 차량진행상태 :" + sYD_CAR_PROG_STAT, "SL");
					commUtils.printLog(logId, methodNm, "S-");
			    	return sndRecord;
			    	
				} else {
					//이송하차 일 경우
					
					//////////////////////////////////////////////////////////////////
					//
					// 형상인식시스템에서 보낸 재료적치각도가 기준에 있는 허용각도 보다 클 경우
					// 작업예약을 생성하지 않는다.
					//
					//////////////////////////////////////////////////////////////////
					String sYM2008_ALW_DEG = "";	//허용각도 기준값	
					//int    iCntOverDeg = 0; 		//허용각도 이상의 SLAB 갯수
					jrParam.setField("REPR_CD_GP"	, "YM2008");
					jrParam.setField("CD_GP"		, "2");
					jrParam.setField("ITEM"			, "DEG");
					rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "야드 기준 조회"); 
					
					if(rsResult2.size() > 0) {
						sYM2008_ALW_DEG = rsResult2.getRecord(0).getFieldString("DTL_ITM1");
					}				
					
					
					//==================================================================================================================================
					String szTRN_EQP_CD;
					String sStkClo;
					String s_STACK_YD_GP;
					String s_STACK_BAY_GP;
					//String s_YD_CAR_SCH_ID = "";
					String sSchCode;
					String wbook_ID = "";
					String sYD_SCH_PRIOR;
					//String sFRTOMOVE_WORD_NO = ""; //이송작업지시 번호
					String sSCH_AUTO_RUN_MODE = "M";
					String sYD_WRK_CRN = ""; 
					String sYD_MULTI_WRK_YN = ""; //야드멀티작업여부
					String szARR_YD_PNT_CD = "";
					String sSTOCK_ID = "";
					String sSTACK_BED_X_AXIS = "";
					int iFS_XAXIS = 0;
					int iL3_XAXIS = 0;
					
					int iWbook_ID_Cnt = 0;
					
					szTRN_EQP_CD = sCAR_NO; 
					sStkClo = sPT_LOAD_LOC;
					s_STACK_YD_GP	= sStkClo.substring(0, 1);
					s_STACK_BAY_GP  = sStkClo.substring(1, 2);
					
					szARR_YD_PNT_CD = "1" + s_STACK_BAY_GP +  sStkClo.substring(4,6);
					
					
					//운송장비코드로 이송재료 조회 
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getListFrtostlList
					SELECT  STL_NO
					       ,A.YD_STK_BED_NO
					       ,SUBSTR(A.YD_STK_LYR_NO,2,2) AS STK_LYR
					       ,A.YD_CAR_SCH_ID
					       ,(SELECT CS.YD_CARUD_WRK_BOOK_ID FROM TB_YD_CARSCH CS WHERE CS.YD_CAR_SCH_ID = A.YD_CAR_SCH_ID) AS YD_CARUD_WRK_BOOK_ID
					       ,(SELECT CS.FRTOMOVE_WORD_NO FROM TB_YD_CARSCH CS WHERE CS.YD_CAR_SCH_ID = A.YD_CAR_SCH_ID) AS FRTOMOVE_WORD_NO
					  FROM TB_YD_CARFTMVMTL A 
					 WHERE A.YD_CAR_SCH_ID = (SELECT MAX(A.YD_CAR_SCH_ID)
					                            FROM TB_YD_CARSCH A
					                           WHERE A.TRN_EQP_CD = :V_TRN_EQP_CD  
					                             AND A.DEL_YN = 'N')
					   AND A.DEL_YN = 'N'
					 ORDER BY A.YD_STK_LYR_NO  */
					/*jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD); 
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getListFrtostlList", logId, methodNm, "운송장비코드로 이송재료 조회");
					if(rsResult.size() <= 0) {
						throw new Exception("영차(F) 도착처리 대상재가 존재 안함");
					} else {
						s_YD_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
						//wbook_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CARUD_WRK_BOOK_ID"));
						sFRTOMOVE_WORD_NO = commUtils.trim(rsResult.getRecord(0).getFieldString("FRTOMOVE_WORD_NO"));
					}*/
					
					
					
					//스케줄코드 생성  - 이송하차(L)
					sSchCode = s_STACK_YD_GP + s_STACK_BAY_GP + "PT02LM";
					
					//스케줄코드로 스케줄기준Table조회
					jrParam.setField("YD_SCH_CD", sSchCode);
			    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
					SELECT YD_SCH_CD
					     , YD_WRK_CRN
					     , YD_WRK_CRN_PRIOR
					     , YD_MULTI_WRK_YN
					  FROM TB_YM_SCHEDULERULE
					 WHERE DEL_YN = 'N'
					   AND YD_SCH_CD = :V_YD_SCH_CD
					*/   
					rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
			    	
					if (rsResult2 != null && rsResult2.size() > 0) {
						sYD_WRK_CRN = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN"); //야드작업크레인
						sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
						sYD_MULTI_WRK_YN = rsResult2.getRecord(0).getFieldString("YD_MULTI_WRK_YN"); //야드멀티작업여부
					} else {
						throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sSchCode + "]");
					}			
					
					
	
						
	
						
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					//------------------------------------------------------------------------------------------------------------------------
					//작업예약 슬라브 단위로 생성
					
					//이송 작업지시 번호 가져오기
					if("".equals(sFRTOMOVE_WORD_NO)) {
						sFRTOMOVE_WORD_NO = commDao.getSeqId(logId, methodNm, "FtMvWo"); 
					}
					
					
					if("Y".equals(sYD_MULTI_WRK_YN)) {
						jrParam.setField("YD_SCH_ST_GP"		, "N"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
						if("A".equals(s_STACK_BAY_GP)) {
							if("2ACRA1".equals(sYD_WRK_CRN)) {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2ACRA1"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2ACRA2"); //야드작업계획크레인2
							} else {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2ACRA2"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2ACRA1"); //야드작업계획크레인2
							}
						} else if("D".equals(s_STACK_BAY_GP)) {
							if("2DCRD3".equals(sYD_WRK_CRN)) {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2DCRD3"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2DCRD2"); //야드작업계획크레인2
							} else {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2DCRD2"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2DCRD3"); //야드작업계획크레인2
							}
						} else {
							jrParam.setField("YD_WRK_PLAN_CRN"	, sYD_WRK_CRN); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"	, ""); //야드작업계획크레인2
						}
						
					} else {
						jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
						jrParam.setField("YD_WRK_PLAN_CRN"	, sYD_WRK_CRN); //야드작업계획크레인
						jrParam.setField("YD_WRK_PLAN_CRN2"	, ""); //야드작업계획크레인2
					}
					
					
					iWbook_ID_Cnt = 0;
					JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
					
					//하차대상 갯수 만큼 Looping...
					//for(int ii= 0; ii < rsResult.size() ; ii++) {
					for(int ii=(rsResult.size()-1) ; ii >= 0 ; ii--) { //상단에서 하단 순으로 작업
						
						//SLAB번호로 이송하차 작업예약이 존재하는지 확인
						//STOCK에 X좌표가 0 이 아닌지 확인 --> X좌표가 0이면 BREAK (하단 재료 작업예약을 만들지 않는다.)
						sSTOCK_ID = commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO"));
						
						jrParam.setField("YD_SCH_ID"	, sSchCode);
						jrParam.setField("STOCK_ID"		, sSTOCK_ID);
						jrParam.setField("STACK_COL_GP"	, sPT_LOAD_LOC);
						rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getFsStockXyz", logId, methodNm, "스캔정보 조회"); 
						
						if(rsResult2.size() > 0) {
							wbook_ID = commUtils.trim(rsResult2.getRecord(0).getFieldString("YD_WBOOK_ID"));
							iFS_XAXIS = Integer.parseInt(commUtils.nvl(rsResult2.getRecord(0).getFieldString("WGT_CENTER_XAXIS"),"0")); //형상시스템에서  받은 값
							iL3_XAXIS = Integer.parseInt(commUtils.nvl(rsResult2.getRecord(0).getFieldString("STACK_BED_X_AXIS"),"0")); //L3 상차도 중심 값
						}
						
						if(iFS_XAXIS == 0) break; //SCAN이 안되었다면 종료한다.			
						
						if("".equals(wbook_ID)) {
						
							//작업예약ID생성
							wbook_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
							
							iWbook_ID_Cnt++;
							
							jrCrnSchMsg.setField("YD_WBOOK_ID"+(iWbook_ID_Cnt),wbook_ID);
							jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(iWbook_ID_Cnt));
							
							/**********************************************************
							* 1-1. 작업예약(TB_YM_WRKBOOK) 생성
							**********************************************************/
							jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
							jrParam.setField("YD_GP"			, s_STACK_YD_GP);
							jrParam.setField("YD_BAY_GP"		, s_STACK_BAY_GP);
							jrParam.setField("YD_SCH_CD"		, sSchCode); //야드스케쥴코드
							jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
							jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
							jrParam.setField("YD_SCH_REQ_GP"	, "C"); //야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
							jrParam.setField("TRN_EQP_CD"		, szTRN_EQP_CD); //운송장비코드
							jrParam.setField("YD_CAR_USE_GP"	, "L"); //야드차량사용구분 L:구내운송
							
							//commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
							bSlabComm.insWrkBook(jrParam);
							
							
							/**********************************************************
							* 작업예약재료(TB_YM_WRKBOOKMTL) 생성
							**********************************************************/
							jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
							jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
							jrParam.setField("STACK_COL_GP"		, sStkClo);
							jrParam.setField("STACK_BED_GP"		, "01");
							jrParam.setField("STACK_LAYER_GP"	, commUtils.format(ii+1, 2));
							
							//commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
							bSlabComm.insWrkBookMtl(jrParam);
							
							/**********************************************************
							* TB_YM_STOCK의이송작업지시번호(TRANS_WORD_NO) 등록
							**********************************************************/
							jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
							//jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_CS ); //이송대기
							jrParam.setField("STOCK_MOVE_TERM"	, "" ); //영차출발수신시 지정됨.. 여기서는 변경하지 않고 이전 값을 그대로 설정함
							jrParam.setField("FRTOMOVE_WORD_NO"	, sFRTOMOVE_WORD_NO); //이송작업지시번호
							/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockTransWordNo
							UPDATE TB_YM_STOCK
							   SET MODIFIER   = :V_MODIFIER
							     , MOD_DDTT   = SYSDATE 
							     , STOCK_MOVE_TERM = NVL(:V_STOCK_MOVE_TERM,STOCK_MOVE_TERM)
							     , FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO
							WHERE STOCK_ID = :V_STOCK_ID */
							commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockTransWordNo", logId, methodNm, "TB_YM_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록");
						}
					}
					
					
					if(iWbook_ID_Cnt > 0) { //생성된 작업예약이 있을 경우만 
						/**********************************************************
						* Pallet조회 (B)에서 지정한 크레인스케줄 생성 모드 확인
						*  - Auto 이면 크레인 스케줄을 호출 한다.
						**********************************************************/
						jrParam.setField("REPR_CD_GP"	, "YM2002");
						jrParam.setField("CD_GP"		, s_STACK_YD_GP);
						jrParam.setField("ITEM"			, s_STACK_BAY_GP);
						rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "야드 기준 조회"); 
						
						if(rsResult2.size() > 0) {
							sSCH_AUTO_RUN_MODE = rsResult2.getRecord(0).getFieldString("DTL_ITM1");
						}
						
						//if("A".equals(sSCH_AUTO_RUN_MODE) && "0000".equals(sCAU_CD)) { //Auto 모드일 경우만 스케줄 호출 + 형상 원인코드가 정상('0000')일 경우만 호출
						if("A".equals(sSCH_AUTO_RUN_MODE) ) { //Auto 모드일 경우만 스케줄 호출 
							
							//크레인 스케줄 기동 YMYMJ203 호출
							jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ203"); 
							jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시	
							jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
							jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
							
							sndRecord = commUtils.addSndData(sndRecord, jrCrnSchMsg);
						}
					}
					
					//------------------------------------------------------------------------------------------------------------------------
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
						
						
						
					
					
					
					
					
					
					
					
					
					
					
					/**********************************************************
					* 1-5. 차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)
					**********************************************************/
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt5 
					UPDATE TB_YD_CARSCH
					  SET  MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC
					      ,YD_PNT_CD3 = :V_YD_PNT_CD3
					      ,YD_CARUD_ARR_DT = SYSDATE
					      ,YD_CAR_PROG_STAT = 'B' -- 하차도착
					      ,YD_CARUD_WRK_BOOK_ID= :V_YD_CARUD_WRK_BOOK_ID
					      ,FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO
					WHERE TRN_EQP_CD = :V_TRN_EQP_CD
					AND DEL_YN = 'N' */
					jrParam.setField("YD_CARUD_STOP_LOC"	, sStkClo);
					jrParam.setField("YD_PNT_CD3"			, szARR_YD_PNT_CD);
					jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, wbook_ID);
					jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
					jrParam.setField("FRTOMOVE_WORD_NO"		, sFRTOMOVE_WORD_NO); //이송작업지시번호
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt5", logId, methodNm, "차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)");
					
				} 

			} else if("2CTC".equals(sPT_LOAD_LOC.substring(0,4))) {
				//상차도 위치가 C동 대차 포인트 일 경우...	
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("EQUIP_GP"  , "2XTC01"); //  1번대차
				jrYdMsg.setField("YD_BAY_GP" , "C");      //  C동
				jrYdMsg.setField("SLAB_NO" , sSLAB_NOS);      // 해당스라브
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTcarWbookidLDFormSlab 
				SELECT A.YD_WBOOK_ID
				      ,C.STOCK_ID
				  FROM TB_YM_WRKBOOK A
				      ,TB_YM_TCARSCH B
				      ,TB_YM_WRKBOOKMTL C
				 WHERE A.DEL_YN = 'N'
				   AND B.DEL_YN  = 'N'
				   AND C.DEL_YN  = 'N'   
				   AND A.YD_WRK_PLAN_TCAR =B.YD_EQP_ID
				   AND A.YD_WBOOK_ID =B.YD_CARUD_WRK_BOOK_ID --하차스케줄ID 
				   AND A.YD_WBOOK_ID = C.YD_WBOOK_ID
				   AND B.YD_EQP_ID   =:V_EQUIP_GP   
				   AND A.YD_BAY_GP   =:V_YD_BAY_GP 
				   AND A.YD_WBOOK_ID NOT IN ( SELECT YD_WBOOK_ID FROM TB_YM_CRNSCH WHERE DEL_YN = 'N')
				   AND (:V_SLAB_NO IS NOT NULL AND C.STOCK_ID IN (
				                                    SELECT  SUBSTR (AA, INSTR (AA, ',', 1, LEVEL) + 1,
				                                              INSTR (AA, ',', 1, LEVEL + 1) - INSTR (AA, ',', 1, LEVEL)
				                                               - 1
				                                           ) STOCK_ID
				                                        FROM (SELECT ',' || :V_SLAB_NO || ',' AA FROM DUAL)
				                                      CONNECT BY LEVEL <= LENGTH (AA) - LENGTH (REPLACE (AA, ',')) - 1
				                                   )
				                               )
				   ORDER BY A.YD_WBOOK_ID
				   */ 
				JDTORecordSet jsTcsch = commDao.select(jrYdMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTcarWbookidLDFormSlab", logId, methodNm, "형상관리적용후크레인스케줄 조회");   		
			 	JDTORecord jsMsg = JDTORecordFactory.getInstance().create();
	     

    			if(jsTcsch.size() > 0 ) {

	    			String Ydwbookid = commUtils.trim(jsTcsch.getRecord(0).getFieldString("YD_WBOOK_ID"));
	    			
	    			//크레인 스케줄 기동 YMYMJ202 호출
	    			jsMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
	    			jsMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
	    			jsMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
	    			jsMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
	    			jsMsg.setField("YD_WBOOK_ID"	  , Ydwbookid); //야드작업예약ID
	    			
    				sndRecord = commUtils.addSndData(sndRecord, jsMsg);
    			}	
			}
				
			
			commUtils.printLog(logId, methodNm, "S-");
			
	    	return sndRecord;
	    	
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of Slab 무게중심(형상인식)측정 정보(A8YML029) NEW

	
	/**
	 *      [A] 오퍼레이션명 : ZAXIS 값 갱신 처리 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public boolean procCrnsSchZaxis(JDTORecord jrParam) throws DAOException {
		String methodNm = "ZAXIS 값 갱신 처리 [BSlabL2RcvSeEJB.procCrnsSchZaxis] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			jrParam.setResultMsg(methodNm);	//Log Method Name

			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getZAXIS 
			SELECT /*+ INDEX(B PK_YM_STACKLAYER *)/
			       CUR_INFO.YD_EQP_ID                AS YD_EQP_ID                 --야드설비ID
			     , CUR_INFO.YD_SCH_CD                AS YD_SCH_CD                 --야드스케쥴코드
			     , CUR_INFO.YD_CRN_SCH_ID            AS YD_CRN_SCH_ID             --야드크레인스케쥴ID
			     , CASE WHEN CUR_INFO.YD_EQP_WRK_SH = 1 THEN CUR_INFO.UP_SUM_SLAB_T1               - (CUR_INFO.YD_MTL_T1 * 0.6)
			            WHEN CUR_INFO.YD_EQP_WRK_SH = 2 THEN CUR_INFO.UP_SUM_SLAB_T1 - CUR_INFO.YD_MTL_T1 - (CUR_INFO.YD_MTL_T2 * 0.6)
			            END                          AS YD_UP_WO_LOC_ZAXIS
			     ,  CASE WHEN CUR_INFO.YD_EQP_WRK_SH = 1 THEN CUR_INFO.DN_SUM_SLAB_T1 + (CUR_INFO.YD_MTL_T1 * 0.3)
			             WHEN CUR_INFO.YD_EQP_WRK_SH = 2 THEN CUR_INFO.DN_SUM_SLAB_T1 + (CUR_INFO.YD_MTL_T2 * 0.3)
			             END  AS YD_DN_WO_LOC_ZAXIS
			  FROM  -- 대상작업지시
			       (
			        SELECT A.*
			             , (SELECT nvl(SUM(SLAB_T),0)
			                  FROM TB_YM_STACKLAYER SL
			                     , VW_YD_SLABCOMM   SC 
			                 WHERE SL.STACK_COL_GP = SUBSTR(A.YD_UP_WO_LOC,1,6)
			                   AND SL.STACK_BED_GP = SUBSTR(A.YD_UP_WO_LOC,7,2)
			                   AND SL.STOCK_ID = SC.SLAB_NO 
			                   AND SL.STACK_LAYER_STAT IN ('C','U')
			               ) + 
			               (SELECT NVL(STACK_BED_Z_AXIS,0)
			                  FROM TB_YM_STACKER SK
			                 WHERE SK.STACK_COL_GP = SUBSTR(A.YD_UP_WO_LOC,1,6)
			                   AND SK.STACK_BED_GP = SUBSTR(A.YD_UP_WO_LOC,7,2)
			                   AND ROWNUM = 1
			               ) 
			               AS UP_SUM_SLAB_T1----
			             , (SELECT nvl(SUM(SLAB_T),0)
			                  FROM TB_YM_STACKLAYER SL
			                     , VW_YD_SLABCOMM   SC 
			                 WHERE SL.STACK_COL_GP = SUBSTR(A.YD_DN_WO_LOC,1,6)
			                   AND SL.STACK_BED_GP = SUBSTR(A.YD_DN_WO_LOC,7,2) 
			                   AND SL.STOCK_ID = SC.SLAB_NO 
			                   AND SL.STACK_LAYER_STAT IN ('C')
			               ) + 
			                --기준 위치
			               (SELECT NVL(STACK_BED_Z_AXIS,0)
			                  FROM TB_YM_STACKER SK
			                 WHERE SK.STACK_COL_GP = SUBSTR(A.YD_DN_WO_LOC,1,6)
			                   AND SK.STACK_BED_GP = SUBSTR(A.YD_DN_WO_LOC,7,2)
			                   AND ROWNUM = 1
			               ) 
			               AS DN_SUM_SLAB_T1----
			          FROM (
			                SELECT A.*
			                     , B.STOCK_ID                                               AS STOCK_ID1
			                     , C.SLAB_WT                                                AS YD_MTL_WT1
			                     , C.SLAB_T                                                 AS YD_MTL_T1
			                     , C.SLAB_W                                                 AS YD_MTL_W1
			                     , C.SLAB_LEN                                               AS YD_MTL_L1

			                     , LEAD(B.STOCK_ID)        OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS STOCK_ID2
			                     , LEAD(C.SLAB_WT)         OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS YD_MTL_WT2
			                     , LEAD(C.SLAB_T)          OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS YD_MTL_T2
			                     , LEAD(C.SLAB_W)          OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS YD_MTL_W2
			                     , LEAD(C.SLAB_LEN)        OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS YD_MTL_L2
			                     , ROW_NUMBER() OVER(PARTITION BY A.YD_WBOOK_ID, A.YD_CRN_SCH_ID ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC) AS RN
			                  FROM TB_YM_CRNSCH    A
			                     , TB_YM_CRNWRKMTL B
			                     , (SELECT A.*
			                          FROM VW_YD_SLABCOMM A
			                       )  C
			                     , TB_YM_STOCK     ST 
			                 WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID 
			                   AND B.STOCK_ID      = C.SLAB_NO
			                   AND A.DEL_YN        = 'N'
			                   AND B.STOCK_ID      = ST.STOCK_ID
			                   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			               ) A
			         WHERE RN = 1
			       ) CUR_INFO   
			WHERE CUR_INFO.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			  AND CUR_INFO.YD_WRK_PROG_STAT IN ('W', 'S', '1', '2', '3')
			 */	
			
			JDTORecordSet jsZaxis = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getZAXIS", logId, methodNm, "SLAB크레인스케줄 Z값조회");

			if (jsZaxis.size() > 0) {
				/**********************************************************
				* 2.1 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송
				**********************************************************/
				String ydUpWoLocZaxis	= commUtils.trim(jsZaxis.getRecord(0).getFieldString("YD_UP_WO_LOC_ZAXIS"   ));
				String ydDnWoLocZaxis	= commUtils.trim(jsZaxis.getRecord(0).getFieldString("YD_DN_WO_LOC_ZAXIS"   ));
			
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS 
				--크레인스케줄 z값 갱신
				UPDATE TB_YM_CRNSCH
				   SET YD_UP_WO_LOC_ZAXIS = :V_YD_UP_WO_LOC_ZAXIS
				     , YD_DN_WO_LOC_ZAXIS = :V_YD_DN_WO_LOC_ZAXIS
				 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
				   AND DEL_YN           = 'N' 
				*/
				jrParam.setField("YD_UP_WO_LOC_ZAXIS", ydUpWoLocZaxis); 
				jrParam.setField("YD_DN_WO_LOC_ZAXIS", ydDnWoLocZaxis);
        		commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS", logId, methodNm, "SLAB크레인스케줄 Z값 수정");
			}	
			
			commUtils.printLog(logId, methodNm, "S-");

			return true;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of 
	
	/**
	 *      [A] 오퍼레이션명 : B열연 SLAB HOME 이동지시 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/	
	public JDTORecord makeHomePosMvWo(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "B열연 SLAB HOME 이동지시[BSlabL2RcvSeEJB.makeHomePosMvWo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = null;	//크레인작업지시 전문 Return
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			rcvMsg.setResultMsg(methodNm);	//Log Method Name
			
			String sYD_EQP_ID = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));
			String sMV_GP = commUtils.trim(rcvMsg.getFieldString("MV_GP"));
			String sHOME_PO_NO = commUtils.nvl(rcvMsg.getFieldString("HOME_PO_NO"), "1");
			
			//TB_YM_RULE 테이블에서 해당 크레인의 홈포지션 X,Y 값을 읽어 온다.
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("REPR_CD_GP"	, "APP105");
			jrParam.setField("CD_GP"		, sYD_EQP_ID);
			jrParam.setField("ITEM"			, sHOME_PO_NO);
			JDTORecordSet rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "야드 기준 조회"); 
			
			if(rsResult2.size() > 0) {
				
				String sX_AXIS = commUtils.nvl(rsResult2.getRecord(0).getFieldString("DTL_ITM1"),"0");
				String sY_AXIS = commUtils.nvl(rsResult2.getRecord(0).getFieldString("DTL_ITM2"),"0");
				
				if("0".equals(sX_AXIS) || "0".equals(sY_AXIS)) {
					
					commUtils.printLog(logId, ">>>> B열연 SLAB HOME 이동지시 X,Y 좌표 조회 오류!!!", "SL");
					
				} else {
					
					JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
					sndL2Msg.setResultCode(logId);	//Log ID
					sndL2Msg.setResultMsg(methodNm);	//Log Method Name
					sndL2Msg.setField("YD_EQP_ID"		, sYD_EQP_ID  ); 
					sndL2Msg.setField("MV_GP"			, sMV_GP      ); 
					sndL2Msg.setField("YD_WO_LOC_XAXIS" , sX_AXIS);
					sndL2Msg.setField("YD_WO_LOC_YAXIS" , sY_AXIS);				
		 
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L010", sndL2Msg));	 //전송 Data 생성	
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of makeHomePosMvWo 처리
	
	
	/**
	 *      [A] 오퍼레이션명 : B열연SLAB크레인작업지시요구 - D3 고도화(A8YML007D3Adv)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvA8YML007D3Adv(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "B열연SLAB크레인작업지시요구 - D3 고도화[BSlabL2RcvSeEJB.rcvA8YML007D3Adv] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용

		try {
			commUtils.printLog(logId, methodNm, "S+"); 
			
			JDTORecord sYMYMJ204 = null;
			
			//수신 항목 값 
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 - D3 고도화 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");

			JDTORecord jrRtn  = JDTORecordFactory.getInstance().create();	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "J"          ); //야드L2실적구분(지시요구)
			resMsg.setField("YD_L3_HD_RS_CD"  , "JR99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:크레인작업지시요구 수신처리"); //야드L3MESSAGE(Error)

			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"    , ydEqpId   ); //야드설비ID
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			/**********************************************************
			* 1. 설비상태 Check
			**********************************************************/
			JDTORecord jrChk = ymComm.chkEqpStat(jrParam);

			ydL3HdRsCd = commUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));
			ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			
			/**********************************************************
			* 2. 크레인스케줄 조회
			*    2.1 크레인스케줄이 존재하면 전송
			*    2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
			**********************************************************/
			/* 크레인작업지시요구 크레인스케줄 조회  */
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchA8YML007 
			SELECT YD_CRN_SCH_ID
			     , YD_WRK_PROG_STAT
			     , YD_UP_WO_LOC 
			     , YD_DN_WO_LOC 
			     , YD_SCH_CD
			     , YD_SCH_PRIOR
			     , SEQ1
			     , YD_SCH_ST_GP
			     , YD_WRK_PLAN_CRN
			     , YD_WRK_PLAN_CRN2
			     , OTHER_CRN_CNT 
			     , OTHER_BRA_CNT
			     -- 이송하차이고 
			     -- 상대편 크레인 작업이 없고 
			     -- 스케쥴 기동구분이 'N'(멀티) 인 경우
			     -- 동일작업이 2건이면 멀티작업가능
			     , CASE WHEN SUBSTR(YD_SCH_CD,3,6) = 'PT02LM' 
			             AND OTHER_CRN_CNT = 0 
			             AND OTHER_BRA_CNT = 0
			             AND YD_SCH_ST_GP  = 'N'
			             AND YD_SCH_CD = LEAD(YD_SCH_CD) OVER (ORDER BY SEQ1 DESC, YD_SCH_PRIOR, YD_CRN_SCH_ID) THEN 'Y'
			            ELSE 'N' END MULTI_YN
			  FROM
			       (
			         SELECT CS.YD_CRN_SCH_ID
			              , CS.YD_WRK_PROG_STAT
			              , CS.YD_UP_WO_LOC 
			              , CS.YD_DN_WO_LOC 
			              , CS.YD_SCH_CD
			              , CS.YD_SCH_PRIOR
			              , DECODE(CS.YD_WRK_PROG_STAT,'W','0','S','1', CS.YD_WRK_PROG_STAT) AS SEQ1
			              , WB.YD_SCH_ST_GP
			              , WB.YD_WRK_PLAN_CRN
			              , WB.YD_WRK_PLAN_CRN2
			              --상대크레인 작업 건수
			              , (SELECT COUNT(*) 
			                   FROM TB_YM_CRNSCH 
			                  WHERE DEL_YN = 'N'
			                    AND YD_EQP_ID = WB.YD_WRK_PLAN_CRN2) AS OTHER_CRN_CNT
			              , (SELECT COUNT(*)
			                   FROM TB_YM_EQUIP 
			                  WHERE EQUIP_GP = WB.YD_WRK_PLAN_CRN2
			                    AND (WPROG_STAT = 'B' OR WORK_MODE = '2')) AS OTHER_BRA_CNT -- 고장여부
			           FROM TB_YM_CRNSCH CS
			              , TB_YM_WRKBOOK WB
			          WHERE CS.YD_WBOOK_ID = WB.YD_WBOOK_ID
			            AND CS.DEL_YN = 'N'
			            AND WB.DEL_YN = 'N'
			            AND CS.YD_EQP_ID =  :V_YD_EQP_ID 
			            --권상위치 상단에 타크레인 존재여부 
			            AND 1 = CASE WHEN  (SELECT COUNT(*) 
			                                  FROM TB_YM_CRNSCH 
			                                 WHERE DEL_YN = 'N' 
			                                   AND YD_UP_WO_LOC   = CS.YD_UP_WO_LOC 
			                                   AND TO_NUMBER(YD_UP_WO_LAYER)  > TO_NUMBER(CS.YD_UP_WO_LAYER) 
			                                   AND YD_EQP_ID <> :V_YD_EQP_ID) = 0 THEN 1
			                         ELSE 0 END          
			            --권하위치 하단에 타크레인 존재여부 
			            AND 1 = CASE WHEN  (SELECT COUNT(*) 
			                                  FROM TB_YM_CRNSCH 
			                                 WHERE DEL_YN = 'N' 
			                                   AND YD_DN_WO_LOC   = CS.YD_DN_WO_LOC 
			                                   AND TO_NUMBER(YD_DN_WO_LAYER)  < TO_NUMBER(CS.YD_DN_WO_LAYER) 
			                                   AND YD_EQP_ID <> :V_YD_EQP_ID) = 0 THEN 1
			                         ELSE 0 END          
			          ORDER BY SEQ1 DESC, YD_SCH_PRIOR, YD_CRN_SCH_ID
			       )    
			 WHERE ROWNUM <= 1
			 */
			JDTORecordSet jsSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchA8YML007", logId, methodNm, "B열연SLAB D3 크레인스케줄 조회");

			if (jsSch.size() > 0) {
				/**********************************************************
				* 2.1 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송
				**********************************************************/
				ydCrnSchId    = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				ydWrkProgStat = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));

				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID

				if ("S".equals(ydWrkProgStat)||"1".equals(ydWrkProgStat) ||"2".equals(ydWrkProgStat) ||"3".equals(ydWrkProgStat)||"5".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.1.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 재지시 전송
					**********************************************************/
					jrParam.setField("MSG_GP", "U"); //전문구분 - 재지시
					/*
					UPDATE TB_YM_CRNSCH
					   SET MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					     , YD_WORD_DT       = SYSDATE
					 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
					   AND DEL_YN           = 'N'					 
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdWorkDt");					

					//크레인작업지시(YMA8L004) 전문 재전송
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L004", jrParam));

					commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
				} else {
					
					
///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
// SLAB 고도화 - START			
					
//     D3 스케줄이 이송하차일 경우
//        D2 스케줄이 존재하고 
//           D2 스케줄이 이송하차 이고 작업진행상태가  W,S,1 이면 작업지시를 전송하지 않고 HOME 포지션으로 이동한다.
//			 D2 스케줄이 이송상차 이고 작업진행상태가  W,S,1 이 아니고 권하위치가 PT 이면 HOME 포지션으로 이동한다.

					JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
					
					String sD3_GOTO_HOMEPO_YN = "N";
					
					String sD3_YD_SCH_CD =  commUtils.trim(jsSch.getRecord(0).getFieldString("YD_SCH_CD"));
					
					if("2DPT02LM".equals(sD3_YD_SCH_CD)  || "2DPT02UM".equals(sD3_YD_SCH_CD) ) { //이송하차  또는 이송상차인 경우

						jrParam2.setResultCode(logId);	//Log ID
						jrParam2.setResultMsg(methodNm);	//Log Method Name
						jrParam2.setField("YD_EQP_ID"    , "2DCRD2"   ); //야드설비ID

						JDTORecordSet jsSch2 = commDao.select(jrParam2, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchA8YML007", logId, methodNm, "B열연SLAB D2 크레인스케줄 조회");
						
						if (jsSch2.size() > 0) {

							String sD2_YD_SCH_CD 		= commUtils.trim(jsSch2.getRecord(0).getFieldString("YD_SCH_CD"));
							String sD2_YD_WRK_PROG_STAT = commUtils.trim(jsSch2.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
							String sD2_YD_DN_WO_LOC		= commUtils.trim(jsSch2.getRecord(0).getFieldString("YD_DN_WO_LOC"));
							
							if("2DPT02LM".equals(sD2_YD_SCH_CD)) { //이송하차
								if("2".equals(sD2_YD_WRK_PROG_STAT)) {
									//D동 하차포인트 중 최상단에 D2호기 이송하차 작업이 있으면 --> 작업지시 내리지 않고, HOME 포지션으로 이동한다.
									jrParam2.setField("STACK_COL_GP" , "2DPT"   ); //D동 포인트
									jrParam2.setField("YD_EQP_ID"    , "2DCRD2"   ); //야드설비ID
									jrParam2.setField("YD_SCH_CD"    , "2DPT02LM"   ); //스케줄코드
									jsSch2 = commDao.select(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPtSch", logId, methodNm, "D동 하차포인트 하차스케줄 조회");
									
									if(jsSch2.size() > 0) {
										sD3_GOTO_HOMEPO_YN = "Y";
									}
								}
							} else if("2DPT02UM".equals(sD2_YD_SCH_CD)) { //이송상차
								if("2".equals(sD2_YD_WRK_PROG_STAT)) {
									//권하위치가 2DPTxx 이면 --> 작업지시 내리지 않고 HOME 포지션으로 이동한다.
									if(sD2_YD_DN_WO_LOC.length() >= 4) {
										if("2DPT".equals(sD2_YD_DN_WO_LOC.substring(0,4))) {
											sD3_GOTO_HOMEPO_YN = "Y";
										}
									}
								}
							}
						}
					}
					
					if("Y".equals(sD3_GOTO_HOMEPO_YN)) {
						
						
						String sAPP100_D3_HOME_PO_YN = ymComm.BCoilApplyYn("APP100","2","D3_HOME_PO");  
						
						commUtils.printLog(logId,  "==========[[[ SLAB D3 홈포지션 이동지시 생성 여부 :" + sAPP100_D3_HOME_PO_YN + " ]]]============", "SL");
					
						if("Y".equals(sAPP100_D3_HOME_PO_YN)) {
							
							jrParam2.setResultCode(logId);	//Log ID
							jrParam2.setResultMsg(methodNm);	//Log Method Name
							jrParam2.setField("YD_EQP_ID"   , "2DCRD3"   ); //야드설비ID
							jrParam2.setField("MV_GP"    	, "1"   ); 
							jrParam2.setField("HOME_PO_NO" 	, "1"   );
							
							JDTORecord jrRtn1 = this.makeHomePosMvWo(jrParam2);
							
							if(jrRtn1 == null) {
							
			 					ydL3Msg = "타크레인작업으로인한대기";
			 					
			 					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
								resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
								resMsg.setField("YD_CRN_SCH_ID" , ""     ); 
								
								jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
								
								commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 - D3 고도화 " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");
							
								sYMYMJ204 = bSlabComm.makeYMYMJ204("D3_ADV", "D3 이송하차 작업대기", "2", ydCrnSchId, logId); 
								jrRtn = commUtils.addSndData(jrRtn, sYMYMJ204);
								
							} else {
								
								jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
								
								sYMYMJ204 = bSlabComm.makeYMYMJ204("D3_HOME_PO", "D3 홈포지션 이동", "3", ydCrnSchId, logId);
								jrRtn = commUtils.addSndData(jrRtn, sYMYMJ204);
								
							}
							
						} else {
						
							//D3에게 이송하차 작업지시를 내릴 때 D2가 이송하차 권상 
		 					ydL3Msg = "타크레인작업으로인한대기";
	
		 					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
							resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
							resMsg.setField("YD_CRN_SCH_ID" , ""     ); 
							
							jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
							
							commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 - D3 고도화 " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");
						
							sYMYMJ204 = bSlabComm.makeYMYMJ204("D3_ADV", "D3 이송하차 작업대기", "1", ydCrnSchId, logId); 
							jrRtn = commUtils.addSndData(jrRtn, sYMYMJ204);							
						}
						
						commUtils.printLog(logId, methodNm, "S-");

						return jrRtn;
					}
					
// SLAB 고도화 - END			
///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
					
					
					/**********************************************************
					* 2.1.2 대기[W] 이면 다음 작업지시 전송
					**********************************************************/

					jrParam.setField("MSG_GP", "I"); //전문구분 - 신규
					jrParam.setField("YD_EQP_STAT", "1"); //권상작업지시

					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp 
					--설비 상태 수정 
					UPDATE TB_YM_EQUIP
					   SET MODIFIER    = :V_MODIFIER
					      ,MOD_DDTT    = SYSDATE
					      ,WPROG_STAT = :V_YD_EQP_STAT
					 WHERE EQUIP_GP    = :V_YD_EQP_ID
					   AND DEL_YN      = 'N'
					*/	   
	        		commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp", logId, methodNm, "B열연SLAB설비상태 수정");

	        		//크레인스케줄 야드작업진행상태 수정
					jrParam.setField("YD_WRK_PROG_STAT"	 , "S"); //선택지시
					jrParam.setField("YD_L2_REQUEST_STAT", "1");
					
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatCrnSchWrkProg 
					--크레인스케줄 작업진행상태 수정
					UPDATE TB_YM_CRNSCH
					   SET MODIFIER         = :V_MODIFIER
					      ,MOD_DDTT         = SYSDATE
					      ,YD_WRK_PROG_STAT = :V_YD_WRK_PROG_STAT
					      ,YD_WORD_DT       = DECODE(:V_YD_WRK_PROG_STAT,'S',SYSDATE,'1',SYSDATE,'W',NULL,YD_WORD_DT)
					      ,YD_L2_REQUEST_STAT = NVL(:V_YD_L2_REQUEST_STAT,YD_L2_REQUEST_STAT) 
					 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
					   AND DEL_YN           = 'N' 
					*/
	        		commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatCrnSchWrkProg", logId, methodNm, "B열연SLAB크레인스케줄 야드작업진행상태 수정");
					//크레인작업지시(YMA8L004) 전문 생성
					
//SJH					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L004", jrParam));
	        		JDTORecordSet jsRtn1 = commDao.getMsgL2("YMA8L004", jrParam);
					jrRtn = commUtils.addSndData(jrRtn , jsRtn1);
					
					//크레인작업지시 Z값 갱신 처리
	        		if(jsRtn1.size() > 0 ) {
	        		//	this.procCrnsSchZaxis(jrParam);
	        			jrParam.setField("YD_UP_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_UP_WO_LOC_ZAXIS"));	
	        			jrParam.setField("YD_DN_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_DN_WO_LOC_ZAXIS"));	
	        			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS 
	    				--크레인스케줄 z값 갱신
	    				UPDATE TB_YM_CRNSCH
	    				   SET YD_UP_WO_LOC_ZAXIS = :V_YD_UP_WO_LOC_ZAXIS
	    				     , YD_DN_WO_LOC_ZAXIS = :V_YD_DN_WO_LOC_ZAXIS
	    				 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
	    				   AND DEL_YN           = 'N' 
	    				*/
	            		commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS", logId, methodNm, "SLAB크레인스케줄 Z값 수정");
	        			        			
	        		}
	        		
					commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
				}
			} else {
				/**********************************************************
				* 2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
				*    2.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 Error 처리
				*    2.2 권하완료[4] 이면 스케줄을 생성
				*    2.3 명령선택대기[W] 이면 응답 전문을 전송 -> 2.2로 통합
				**********************************************************/
				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.2.1 재지시요구 시
					**********************************************************/
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , "크레인[" + ydEqpId + "-" + ydWrkProgStat + "] 작업지시 없음"); //야드L3MESSAGE
					
					jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));

					commUtils.printLog(logId, "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + ydEqpId + " : " + ydWrkProgStat + " - " + ydCrnSchId + " ]", "SL");
				} else {
					/**********************************************************
					* 2.2.2 대기상태[W], 권하완료[4] 지시요구
					**********************************************************/
					//크레인작업지시가 없으면 설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "W"); //대기(Wait)

	        		commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp", logId, methodNm, "B열연SLAB설비상태 수정");
	        		

	        		JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

	    			//작업예약 조회
					 /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWbIdA8YML007 
					SELECT *
					  FROM (
					        SELECT A.YD_WBOOK_ID
					             , A.YD_SCH_CD
					             , A.YD_SCH_CD_CNT
					             , A.YD_SCH_CD_YN
					             , A.YD_WRK_PLAN_TCAR
					             , A.YD_WRK_PLAN_CRN
					          FROM (
					                SELECT YD_WBOOK_ID
					                     , YD_SCH_CD
					                     , SUM(1) OVER(PARTITION BY YD_SCH_CD) AS YD_SCH_CD_CNT 
					                     , 'Y'  AS YD_SCH_CD_YN 
					                     , YD_WRK_PLAN_TCAR
					                     , YD_WRK_PLAN_CRN
					                     , SCH_CNCL_YN
					                     , (SELECT YD_SCH_AUTO_ST_YN 
					                          FROM TB_YM_SCHEDULERULE 
					                         WHERE YD_SCH_CD = WB.YD_SCH_CD 
					                           AND DEL_YN = 'N') AS YD_SCH_AUTO_ST_YN --스케줄별 자동기동여부 DEFAULT Y
					                  FROM TB_YM_WRKBOOK WB
					                 WHERE DEL_YN = 'N'
					                   --AND YD_WRK_PLAN_TCAR IS NULL
					                   --대차상차 인 경우 설비TABLE 적재가능 매수 보다 크거나 같으면 선택불가
					                   --대차 스케줄 과 목적동이 틀리면 선택불가 
					                   AND 1 =  CASE WHEN SUBSTR(YD_SCH_CD,3,2) = 'TC' AND SUBSTR(WB.YD_SCH_CD,7,1) = 'U' THEN 
					                                 CASE WHEN (SELECT COUNT(*) 
					                                              FROM TB_YM_STACKLAYER 
					                                             WHERE STACK_COL_GP = SUBSTR(WB.YD_SCH_CD,1,2)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
					                                               AND STOCK_ID IS NOT NULL) >= (SELECT STACK_MAX_QNTY FROM TB_YM_EQUIP WHERE EQUIP_GP = WB.YD_WRK_PLAN_TCAR )
					                                      THEN '2'
					                                      WHEN (SELECT NVL(SUBSTR(YD_CARUD_STOP_LOC,2,1),  WB.YD_AIM_BAY_GP )
					                                              FROM TB_YM_TCARSCH 
					                                             WHERE DEL_YN = 'N'
					                                               AND YD_EQP_ID = WB.YD_WRK_PLAN_TCAR) <> WB.YD_AIM_BAY_GP 
					                                      THEN '2'    
					                                      WHEN (SELECT SUBSTR(CURR_STOP_LOC,2,1)
					                                              FROM TB_YM_EQUIP 
					                                             WHERE DEL_YN = 'N'
					                                               AND EQUIP_GP = WB.YD_WRK_PLAN_TCAR) <> SUBSTR(WB.YD_SCH_CD,2,1)
					                                      THEN '2'
					                                      -- 대차 출발시 문제발생     
					                                      WHEN (SELECT COUNT(*) 
					                                              FROM TB_YM_STACKER 
					                                             WHERE STACK_COL_GP = SUBSTR(WB.YD_SCH_CD,1,2)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
					                                               AND STACK_BED_ACTIVE_STAT NOT IN ('L')) > 0   
					                                      THEN '2'
					                                      -- 대차 상차 로직
					                                      ELSE '1' END
					                                 ELSE '1' END    
					                   -- 대차에 관련된 스케쥴 기동시 동간작업기준과 틀리면 기동처리 안함                 
					                   AND 1 =  CASE WHEN (SELECT COUNT(*) 
					                                         FROM TB_YM_WRKBOOK WB1
					                                            , (SELECT EQUIP_GP
					                                                    , SUBSTR(CARLOAD_STOP_LOC,1,2)  ||SUBSTR(CARLD_SCH_CD,3,6) AS UP_SCH
					                                                    , SUBSTR(CARUNLOAD_STOP_LOC,1,2)||SUBSTR(CARUD_SCH_CD,3,6) AS DN_SCH
					                                                 FROM TB_YM_EQUIP WHERE EQUIP_GP LIKE '2XTC0%')  EQ1 
					                                            , (SELECT *
					                                                 FROM USRYMA.TB_YM_RULE 
					                                                WHERE REPR_CD_GP = 'YM2007'
					                                                  AND CD_GP = '2'
					                                                  AND DEL_YN = 'N') RULE
					                                        WHERE WB1.YD_WBOOK_ID = WB.YD_WBOOK_ID
					                                          AND WB1.DEL_YN = 'N'
					                                          AND WB1.YD_WRK_PLAN_TCAR = EQ1.EQUIP_GP
					                                          AND SUBSTR(WB1.YD_SCH_CD,3,6) = SUBSTR(DTL_ITM1(+),3,6) 
					                                          AND 'Y' = CASE WHEN YD_WRK_PLAN_TCAR IS NOT NULL AND SUBSTR(ITEM,1,1) = 'L' AND YD_SCH_CD != UP_SCH THEN  'Y'
					                                                         WHEN YD_WRK_PLAN_TCAR IS NOT NULL AND SUBSTR(ITEM,1,1) = 'U' AND YD_SCH_CD != DN_SCH THEN  'Y'
					                                                         ELSE 'N' END  ) > 0
					                                 THEN '2'                                                      
					                                 ELSE '1' END     
					                                  
					                   AND TRN_EQP_CD       IS NULL
					                   AND YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
					                                             FROM TB_YM_CRNSCH
					                                            WHERE DEL_YN = 'N')
					                   AND (YD_WRK_PLAN_CRN = :V_YD_EQP_ID 
					                         OR YD_SCH_CD IN
					                            (SELECT YD_SCH_CD
					                               FROM TB_YM_SCHEDULERULE
					                              WHERE (YD_WRK_CRN = :V_YD_EQP_ID )
					                                AND DEL_YN          = 'N')
					                       )
					                   AND NVL(YD_WRK_PLAN_CRN, :V_YD_EQP_ID ) = :V_YD_EQP_ID
					                  ORDER BY (CASE WHEN YD_WRK_PLAN_CRN=:V_YD_EQP_ID  THEN 1 ELSE 2 END ),YD_SCH_PRIOR, YD_WBOOK_ID     
					               ) A
					         WHERE 1=1
					           AND YD_SCH_CD_YN = 'Y'        
					           AND YD_SCH_AUTO_ST_YN = 'Y'
					       )
					   WHERE ROWNUM = 1
					*/
	        		JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWbIdA8YML007", logId, methodNm, "B열연SLAB작업예약 조회");

					//작업예약이 있으면 크레인스케줄호출
					if (jsWrkBook.size() > 0) {
						ydL3Msg = "크레인스케줄 호출";

						jrYdMsg.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"    , jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
						jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
						jrYdMsg.setField("MODIFIER"   , modifier); //수정자
							
						// 크레인스케줄 기동
						jrRtn = ymComm.getCrnSchMsg(jrYdMsg);
					} else {
						ydL3Msg = "다음 크레인작업지시 없음";
					}
					
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
					resMsg.setField("YD_CRN_SCH_ID" , ""     ); 
					
					jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
					
					commUtils.printLog(logId, "B열연SLAB크레인작업지시요구(다음지시) " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			try {
				//PIDEV_F : 정상SET후  ERROR 발생한 경우							
				if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {							
					resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)						
					resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)						
				}							

				//크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "YmCommEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { bSlabComm.getYMA8L005(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of B열연SLAB크레인작업지시요구 - D3 고도화 (A8YML007D3Adv)
	 

	/**
	 *      [A] 오퍼레이션명 : B열연SLAB크레인작업지시요구 - C1 고도화(A8YML007C1Adv)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvA8YML007C1Adv(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "B열연SLAB크레인작업지시요구 - C1 고도화[BSlabL2RcvSeEJB.rcvA8YML007C1Adv] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		
		try {
			commUtils.printLog(logId, methodNm, "S+"); 
			
			JDTORecord sYMYMJ204 = null;

			//수신 항목 값 
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 - C1 고도화 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");

			JDTORecord jrRtn  = JDTORecordFactory.getInstance().create();	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE
			
			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "J"          ); //야드L2실적구분(지시요구)
			resMsg.setField("YD_L3_HD_RS_CD"  , "JR99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:크레인작업지시요구 수신처리"); //야드L3MESSAGE(Error)

			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"    , ydEqpId   ); //야드설비ID
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			/**********************************************************
			* 1. 설비상태 Check
			**********************************************************/
			JDTORecord jrChk = ymComm.chkEqpStat(jrParam);

			ydL3HdRsCd = commUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));
			ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			
			/**********************************************************
			* 2. 크레인스케줄 조회
			*    2.1 크레인스케줄이 존재하면 전송
			*    2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
			**********************************************************/
			/* 크레인작업지시요구 크레인스케줄 조회  */
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchA8YML007 
			SELECT YD_CRN_SCH_ID
			     , YD_WRK_PROG_STAT
			     , YD_UP_WO_LOC 
			     , YD_DN_WO_LOC 
			     , YD_SCH_CD
			     , YD_SCH_PRIOR
			     , SEQ1
			     , YD_SCH_ST_GP
			     , YD_WRK_PLAN_CRN
			     , YD_WRK_PLAN_CRN2
			     , OTHER_CRN_CNT 
			     , OTHER_BRA_CNT
			     -- 이송하차이고 
			     -- 상대편 크레인 작업이 없고 
			     -- 스케쥴 기동구분이 'N'(멀티) 인 경우
			     -- 동일작업이 2건이면 멀티작업가능
			     , CASE WHEN SUBSTR(YD_SCH_CD,3,6) = 'PT02LM' 
			             AND OTHER_CRN_CNT = 0 
			             AND OTHER_BRA_CNT = 0
			             AND YD_SCH_ST_GP  = 'N'
			             AND YD_SCH_CD = LEAD(YD_SCH_CD) OVER (ORDER BY SEQ1 DESC, YD_SCH_PRIOR, YD_CRN_SCH_ID) THEN 'Y'
			            ELSE 'N' END MULTI_YN
			  FROM
			       (
			         SELECT CS.YD_CRN_SCH_ID
			              , CS.YD_WRK_PROG_STAT
			              , CS.YD_UP_WO_LOC 
			              , CS.YD_DN_WO_LOC 
			              , CS.YD_SCH_CD
			              , CS.YD_SCH_PRIOR
			              , DECODE(CS.YD_WRK_PROG_STAT,'W','0','S','1', CS.YD_WRK_PROG_STAT) AS SEQ1
			              , WB.YD_SCH_ST_GP
			              , WB.YD_WRK_PLAN_CRN
			              , WB.YD_WRK_PLAN_CRN2
			              --상대크레인 작업 건수
			              , (SELECT COUNT(*) 
			                   FROM TB_YM_CRNSCH 
			                  WHERE DEL_YN = 'N'
			                    AND YD_EQP_ID = WB.YD_WRK_PLAN_CRN2) AS OTHER_CRN_CNT
			              , (SELECT COUNT(*)
			                   FROM TB_YM_EQUIP 
			                  WHERE EQUIP_GP = WB.YD_WRK_PLAN_CRN2
			                    AND (WPROG_STAT = 'B' OR WORK_MODE = '2')) AS OTHER_BRA_CNT -- 고장여부
			           FROM TB_YM_CRNSCH CS
			              , TB_YM_WRKBOOK WB
			          WHERE CS.YD_WBOOK_ID = WB.YD_WBOOK_ID
			            AND CS.DEL_YN = 'N'
			            AND WB.DEL_YN = 'N'
			            AND CS.YD_EQP_ID =  :V_YD_EQP_ID 
			            --권상위치 상단에 타크레인 존재여부 
			            AND 1 = CASE WHEN  (SELECT COUNT(*) 
			                                  FROM TB_YM_CRNSCH 
			                                 WHERE DEL_YN = 'N' 
			                                   AND YD_UP_WO_LOC   = CS.YD_UP_WO_LOC 
			                                   AND TO_NUMBER(YD_UP_WO_LAYER)  > TO_NUMBER(CS.YD_UP_WO_LAYER) 
			                                   AND YD_EQP_ID <> :V_YD_EQP_ID) = 0 THEN 1
			                         ELSE 0 END          
			            --권하위치 하단에 타크레인 존재여부 
			            AND 1 = CASE WHEN  (SELECT COUNT(*) 
			                                  FROM TB_YM_CRNSCH 
			                                 WHERE DEL_YN = 'N' 
			                                   AND YD_DN_WO_LOC   = CS.YD_DN_WO_LOC 
			                                   AND TO_NUMBER(YD_DN_WO_LAYER)  < TO_NUMBER(CS.YD_DN_WO_LAYER) 
			                                   AND YD_EQP_ID <> :V_YD_EQP_ID) = 0 THEN 1
			                         ELSE 0 END          
			          ORDER BY SEQ1 DESC, YD_SCH_PRIOR, YD_CRN_SCH_ID
			       )    
			 WHERE ROWNUM <= 1
			 */
			JDTORecordSet jsSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchA8YML007Adv", logId, methodNm, "B열연SLAB C1 크레인스케줄 조회");

			if (jsSch.size() > 0) {
				/**********************************************************
				* 2.1 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송
				**********************************************************/
				ydCrnSchId    = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				ydWrkProgStat = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));

				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID

				if ("S".equals(ydWrkProgStat)||"1".equals(ydWrkProgStat) ||"2".equals(ydWrkProgStat) ||"3".equals(ydWrkProgStat)||"5".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.1.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 재지시 전송
					**********************************************************/
					jrParam.setField("MSG_GP", "U"); //전문구분 - 재지시
					/*
					UPDATE TB_YM_CRNSCH
					   SET MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					     , YD_WORD_DT       = SYSDATE
					 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
					   AND DEL_YN           = 'N'					 
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdWorkDt");					

					//크레인작업지시(YMA8L004) 전문 재전송
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L004", jrParam));

					commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
				} else {
					
					
///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
// SLAB 고도화 - START			
					
//     C1 스케줄이 대차하차일 경우

					JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
					
					String sC1_YD_SCH_CD =  commUtils.trim(jsSch.getRecord(0).getFieldString("YD_SCH_CD"));
					String sC1_YD_UP_WO_LOC = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_UP_WO_LOC"));
					String sC1_YD_DN_WO_LOC = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_DN_WO_LOC"));
					
					if("2CTC11LM".equals(sC1_YD_SCH_CD) && "2CTC1".equals(sC1_YD_UP_WO_LOC.substring(0,5)) ) { //C동 대차하차(1) 이고  권상위치가 1번 대차

						jrParam2.setResultCode(logId);	//Log ID
						jrParam2.setResultMsg(methodNm);	//Log Method Name
						jrParam2.setField("YD_EQP_ID"    , "2CCRC2"   ); //야드설비ID

						JDTORecordSet jsSch2 = commDao.select(jrParam2, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchA8YML007", logId, methodNm, "B열연SLAB C2 크레인스케줄 조회");
						
						if (jsSch2.size() > 0) {

							String sC2_YD_SCH_CD 		= commUtils.trim(jsSch2.getRecord(0).getFieldString("YD_SCH_CD"));
							String sC2_YD_WRK_PROG_STAT = commUtils.trim(jsSch2.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
							String sC2_YD_UP_WO_LOC		= commUtils.trim(jsSch2.getRecord(0).getFieldString("YD_UP_WO_LOC"));
							
							if("2CWB01UM".equals(sC2_YD_SCH_CD)||"2CYD21MM".equals(sC2_YD_SCH_CD)) {
								//C2가 WB보급, 동내이적 스케줄이면
								if("W".equals(sC2_YD_WRK_PROG_STAT)||"S".equals(sC2_YD_WRK_PROG_STAT)||"1".equals(sC2_YD_WRK_PROG_STAT)) {
									//C2가 권상전이고 권상위치가 간섭구간일때 다른 작업이 있으면 지시변경
									if(  !"2C020203".equals(sC2_YD_UP_WO_LOC)
									  && !"2C020204".equals(sC2_YD_UP_WO_LOC)
									  && !"2C020205".equals(sC2_YD_UP_WO_LOC)
									  && !"2C020206".equals(sC2_YD_UP_WO_LOC)
									  && !"2C020207".equals(sC2_YD_UP_WO_LOC)
									  && !"2C020208".equals(sC2_YD_UP_WO_LOC) ) {
										
										//C2 크레인이 권상전이고 권상위치가  2SPAN 3,4,5,6,7,8 BED 가 아니면 1번대차 대차하차작업을 C1에게 주면 간섭이 발생함
										// C1 크레인에 다른 스케줄이 존재한다면 다른 스케줄을 전송하도록 함
											
										if(jsSch.size() > 1) {
										
											ydCrnSchId    = commUtils.trim(jsSch.getRecord(1).getFieldString("YD_CRN_SCH_ID"   ));
											ydWrkProgStat = commUtils.trim(jsSch.getRecord(1).getFieldString("YD_WRK_PROG_STAT"));
											
											jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
											
											sC1_YD_SCH_CD =  commUtils.trim(jsSch.getRecord(1).getFieldString("YD_SCH_CD"));
											sC1_YD_UP_WO_LOC = commUtils.trim(jsSch.getRecord(1).getFieldString("YD_UP_WO_LOC"));
											
											sYMYMJ204 = bSlabComm.makeYMYMJ204("C1_ADV", "대차1 간섭으로 작업지시변경("+sC1_YD_UP_WO_LOC+")", "1", sC2_YD_WRK_PROG_STAT + "," + sC2_YD_UP_WO_LOC + "," + jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID") + "->" + ydCrnSchId, logId); 
											
										} else {
											
						 					//ydL3Msg = "타크레인작업으로인한대기";
						 					
						 					//resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
											//resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
											//resMsg.setField("YD_CRN_SCH_ID" , ""     ); 
											
											//jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
											
											//commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 - C1 고도화 " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");
											
											//sYMYMJ204 = bSlabComm.makeYMYMJ204("C1_ADV", "C1 작업대기", "2", ydCrnSchId, logId);
											//jrRtn = commUtils.addSndData(jrRtn, sYMYMJ204);
											
											//commUtils.printLog(logId, methodNm, "S-");
	
											//return jrRtn;
										}
									}
								} else {
									//C2가 권상후이고 다음작업이 간섭구간일때 다른 대차 있으면 지시변경
									
									jrParam2.setField("YD_GP"    , "2"   ); //야드구분
									jrParam2.setField("BAY_GP"   , "C"   ); //동
									
									jsSch2 = commDao.select(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectWBSlabSearch_09_WB", logId, methodNm, "B열연SLAB C2 다음 장입 스케줄 조회");
									
									if (jsSch2.size() > 0) {
										
										sC2_YD_UP_WO_LOC = commUtils.trim(jsSch2.getRecord(0).getFieldString("STACK_COL_GP")) 
										                 + commUtils.trim(jsSch2.getRecord(0).getFieldString("STACK_BED_GP"));
										
										if(  !"2C020203".equals(sC2_YD_UP_WO_LOC)
										  && !"2C020204".equals(sC2_YD_UP_WO_LOC)
										  && !"2C020205".equals(sC2_YD_UP_WO_LOC)
										  && !"2C020206".equals(sC2_YD_UP_WO_LOC)
										  && !"2C020207".equals(sC2_YD_UP_WO_LOC)
										  && !"2C020208".equals(sC2_YD_UP_WO_LOC) ) {

											if(jsSch.size() > 1) {
												
												ydCrnSchId    = commUtils.trim(jsSch.getRecord(1).getFieldString("YD_CRN_SCH_ID"   ));
												ydWrkProgStat = commUtils.trim(jsSch.getRecord(1).getFieldString("YD_WRK_PROG_STAT"));
												
												jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
												
												sC1_YD_SCH_CD =  commUtils.trim(jsSch.getRecord(1).getFieldString("YD_SCH_CD"));
												sC1_YD_UP_WO_LOC = commUtils.trim(jsSch.getRecord(1).getFieldString("YD_UP_WO_LOC"));
												
												sYMYMJ204 = bSlabComm.makeYMYMJ204("C1_ADV", "대차1 간섭으로 작업지시변경("+sC1_YD_UP_WO_LOC+")", "2", sC2_YD_WRK_PROG_STAT + "," + sC2_YD_UP_WO_LOC + "," + jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID") + "->" + ydCrnSchId, logId);
											} 
										}
									}
								}
							}
						}
						//C1에게 1번대차 작업지시를 준다.
					}
					
					if("2CTC11LM".equals(sC1_YD_SCH_CD) && "2CTC2".equals(sC1_YD_UP_WO_LOC.substring(0,5))) { //C동 대차하차(1) 이고  권상위치가 2번 대차
						
						jrParam2.setResultCode(logId);	//Log ID
						jrParam2.setResultMsg(methodNm);	//Log Method Name
						jrParam2.setField("YD_EQP_ID"    , "2CCRC2"   ); //야드설비ID

						JDTORecordSet jsSch2 = commDao.select(jrParam2, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchA8YML007", logId, methodNm, "B열연SLAB C2 크레인스케줄 조회");
						
						if (jsSch2.size() > 0) {

							String sC2_YD_SCH_CD 		= commUtils.trim(jsSch2.getRecord(0).getFieldString("YD_SCH_CD"));
							String sC2_YD_WRK_PROG_STAT = commUtils.trim(jsSch2.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
							String sC2_YD_UP_WO_LOC		= commUtils.trim(jsSch2.getRecord(0).getFieldString("YD_UP_WO_LOC"));
							
							if("2CWB01UM".equals(sC2_YD_SCH_CD)||"2CYD21MM".equals(sC2_YD_SCH_CD)) {
								//C2가 WB보급,동내이적 스케줄이면
								if("W".equals(sC2_YD_WRK_PROG_STAT)||"S".equals(sC2_YD_WRK_PROG_STAT)||"1".equals(sC2_YD_WRK_PROG_STAT)) {
									//C2가 권상전이면 권상위치를 체크
									if(  "2C010201".equals(sC2_YD_UP_WO_LOC)
									  || "2C010202".equals(sC2_YD_UP_WO_LOC)
									  || "2C010203".equals(sC2_YD_UP_WO_LOC)
									  || "2C010204".equals(sC2_YD_UP_WO_LOC)
									  || "2C010205".equals(sC2_YD_UP_WO_LOC)
									  || "2C010206".equals(sC2_YD_UP_WO_LOC)
									  || "2C010207".equals(sC2_YD_UP_WO_LOC) ) {
										
										//C2 크레인이 권상전이고 권상위치가  1SPAN 1,2,3,4,5,6,7 BED 이면 2번대차 대차하차작업을 C1에게 주면 C2와 갑섭이 발생함
										//C1 크레인에 3번대차 작업이 존재한다면 3번대차 스케줄을 전송하도록 함
											
										if(jsSch.size() > 1) {
										
											for(int ii = 1; ii < jsSch.size(); ii++) {
												
												sC1_YD_SCH_CD =  commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_SCH_CD"));
												sC1_YD_UP_WO_LOC = commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_UP_WO_LOC"));
													
												if("2CTC11LM".equals(sC1_YD_SCH_CD) && "2CTC3".equals(sC1_YD_UP_WO_LOC.substring(0,5))) {
													//3번 대차하차 작업을 지시한다.
	
													ydCrnSchId    = commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_CRN_SCH_ID"   ));
													ydWrkProgStat = commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_WRK_PROG_STAT"));
													
													jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
													
													sYMYMJ204 = bSlabComm.makeYMYMJ204("C1_ADV", "대차2 간섭으로 작업지시변경("+sC1_YD_UP_WO_LOC+")", "3", sC2_YD_WRK_PROG_STAT + "," + sC2_YD_UP_WO_LOC + "," + jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID") + "->" + ydCrnSchId, logId);
												}
												
											}
											
										} else  {
											
						 					//ydL3Msg = "타크레인작업으로인한대기";
						 					
						 					//resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
											//resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
											//resMsg.setField("YD_CRN_SCH_ID" , ""     ); 
											
											//jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
											
											//commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 - C1 고도화 " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");
											
											//sYMYMJ204 = bSlabComm.makeYMYMJ204("C1_ADV", "C1 작업대기", "4", ydCrnSchId, logId);
											//jrRtn = commUtils.addSndData(jrRtn, sYMYMJ204);
											
											//commUtils.printLog(logId, methodNm, "S-");
	
											//return jrRtn;
										}
									} else if(  "2C020203".equals(sC2_YD_UP_WO_LOC)
											 || "2C020204".equals(sC2_YD_UP_WO_LOC)
											 || "2C020205".equals(sC2_YD_UP_WO_LOC)
											 || "2C020206".equals(sC2_YD_UP_WO_LOC)
											 || "2C020207".equals(sC2_YD_UP_WO_LOC)
											 || "2C020208".equals(sC2_YD_UP_WO_LOC) ) {
										
										//C2 크레인이 권상전이고 권상위치가  2SPAN 3,4,5,6,7,8 BED 이면 C1과 C2가 갑섭이 발생하지 않음으로
										//C1 크레인에 1번대차 작업이 존재한다면 1번대차 스케줄을 우선 전송하도록 함
										
										if(jsSch.size() > 1) {
											
											for(int ii = 1; ii < jsSch.size(); ii++) {
												
												sC1_YD_SCH_CD =  commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_SCH_CD"));
												sC1_YD_UP_WO_LOC = commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_UP_WO_LOC"));
													
												if("2CTC11LM".equals(sC1_YD_SCH_CD) && "2CTC1".equals(sC1_YD_UP_WO_LOC.substring(0,5))) {
													//1번 대차하차 작업을 지시한다.
	
													ydCrnSchId    = commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_CRN_SCH_ID"   ));
													ydWrkProgStat = commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_WRK_PROG_STAT"));
													
													jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
													
													sYMYMJ204 = bSlabComm.makeYMYMJ204("C1_ADV", "대차2 -> 대차1 우선실행", "4", sC2_YD_WRK_PROG_STAT + "," + sC2_YD_UP_WO_LOC + "," + jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID") + "->" + ydCrnSchId, logId);
												}
											}
										}
									}
								} else {
									//C2가 권상후이고 다음작업이 간섭구간이 아닐때 1번대차 작업이 있으면 1번 대차로 지시변경
									
									jrParam2.setField("YD_GP"    , "2"   ); //야드구분
									jrParam2.setField("BAY_GP"   , "C"   ); //동
									
									jsSch2 = commDao.select(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectWBSlabSearch_09_WB", logId, methodNm, "B열연SLAB C2 다음 장입 스케줄 조회");
									
									if (jsSch2.size() > 0) {
										
										sC2_YD_UP_WO_LOC = commUtils.trim(jsSch2.getRecord(0).getFieldString("STACK_COL_GP")) 
										                 + commUtils.trim(jsSch2.getRecord(0).getFieldString("STACK_BED_GP"));
										
										if( "2C020203".equals(sC2_YD_UP_WO_LOC)
										 || "2C020204".equals(sC2_YD_UP_WO_LOC)
										 || "2C020205".equals(sC2_YD_UP_WO_LOC)
										 || "2C020206".equals(sC2_YD_UP_WO_LOC)
										 || "2C020207".equals(sC2_YD_UP_WO_LOC)
										 || "2C020208".equals(sC2_YD_UP_WO_LOC) ) {

											if(jsSch.size() > 1) {
												
												for(int ii = 1; ii < jsSch.size(); ii++) {
													
													sC1_YD_SCH_CD =  commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_SCH_CD"));
													sC1_YD_UP_WO_LOC = commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_UP_WO_LOC"));
														
													if("2CTC11LM".equals(sC1_YD_SCH_CD) && "2CTC1".equals(sC1_YD_UP_WO_LOC.substring(0,5))) {
														//1번 대차하차 작업을 지시한다.
		
														ydCrnSchId    = commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_CRN_SCH_ID"   ));
														ydWrkProgStat = commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_WRK_PROG_STAT"));
														
														jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
														
														sYMYMJ204 = bSlabComm.makeYMYMJ204("C1_ADV", "대차2 -> 대차1 우선실행", "5", sC2_YD_WRK_PROG_STAT + "," + sC2_YD_UP_WO_LOC + "," + jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID") + "->" + ydCrnSchId, logId);
													}
												}
											}
										}
									}
								}
							}
						}	
						
					} else if("2CTC11LM".equals(sC1_YD_SCH_CD) && "2CTC3".equals(sC1_YD_UP_WO_LOC.substring(0,5))) { //C동 대차하차(1) 이고  권상위치가 3번 대차
					
						jrParam2.setResultCode(logId);	//Log ID
						jrParam2.setResultMsg(methodNm);	//Log Method Name
						jrParam2.setField("YD_EQP_ID"    , "2CCRC2"   ); //야드설비ID

						JDTORecordSet jsSch2 = commDao.select(jrParam2, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchA8YML007", logId, methodNm, "B열연SLAB C2 크레인스케줄 조회");
						
						if (jsSch2.size() > 0) {

							String sC2_YD_SCH_CD 		= commUtils.trim(jsSch2.getRecord(0).getFieldString("YD_SCH_CD"));
							String sC2_YD_WRK_PROG_STAT = commUtils.trim(jsSch2.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
							String sC2_YD_UP_WO_LOC		= commUtils.trim(jsSch2.getRecord(0).getFieldString("YD_UP_WO_LOC"));
							
							if("2CWB01UM".equals(sC2_YD_SCH_CD)||"2CYD21MM".equals(sC2_YD_SCH_CD)) {
								//C2가 WB보급,동내이적 스케줄이면
								if("W".equals(sC2_YD_WRK_PROG_STAT)||"S".equals(sC2_YD_WRK_PROG_STAT)||"1".equals(sC2_YD_WRK_PROG_STAT)) {
									//C2가 권상전이면 권상위치를 체크
									if( "2C020203".equals(sC2_YD_UP_WO_LOC)
									 || "2C020204".equals(sC2_YD_UP_WO_LOC)
									 || "2C020205".equals(sC2_YD_UP_WO_LOC)
									 || "2C020206".equals(sC2_YD_UP_WO_LOC)
									 || "2C020207".equals(sC2_YD_UP_WO_LOC)
									 || "2C020208".equals(sC2_YD_UP_WO_LOC) ) {
										
										//C2 크레인이 권상전이고 권상위치가  2SPAN 3,4,5,6,7,8 BED 이면 C1과 C2가 갑섭이 발생하지 않음으로
										//C1 크레인에 1번대차 작업이 존재한다면 1번대차 스케줄을 우선 전송하도록 함
										
										if(jsSch.size() > 1) {
											
											for(int ii = 1; ii < jsSch.size(); ii++) {
												
												sC1_YD_SCH_CD =  commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_SCH_CD"));
												sC1_YD_UP_WO_LOC = commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_UP_WO_LOC"));
													
												if("2CTC11LM".equals(sC1_YD_SCH_CD) && "2CTC1".equals(sC1_YD_UP_WO_LOC.substring(0,5))) {
													//1번 대차하차 작업을 지시한다.
	
													ydCrnSchId    = commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_CRN_SCH_ID"   ));
													ydWrkProgStat = commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_WRK_PROG_STAT"));
													
													jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
													
													sYMYMJ204 = bSlabComm.makeYMYMJ204("C1_ADV", "대차3 -> 대차1 우선실행", "6", sC2_YD_WRK_PROG_STAT + "," + sC2_YD_UP_WO_LOC + "," + jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID") + "->" + ydCrnSchId, logId);
												}
											}
										}
									}
								} else {
									//C2가 권상후이고 다음작업이 간섭구간이 아닐때 1번대차 작업이 있으면 1번 대차로 지시변경
									
									jrParam2.setField("YD_GP"    , "2"   ); //야드구분
									jrParam2.setField("BAY_GP"   , "C"   ); //동
									
									jsSch2 = commDao.select(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectWBSlabSearch_09_WB", logId, methodNm, "B열연SLAB C2 다음 장입 스케줄 조회");
									
									if (jsSch2.size() > 0) {
										
										sC2_YD_UP_WO_LOC = commUtils.trim(jsSch2.getRecord(0).getFieldString("STACK_COL_GP")) 
										                 + commUtils.trim(jsSch2.getRecord(0).getFieldString("STACK_BED_GP"));
										
										if( "2C020203".equals(sC2_YD_UP_WO_LOC)
										 || "2C020204".equals(sC2_YD_UP_WO_LOC)
										 || "2C020205".equals(sC2_YD_UP_WO_LOC)
										 || "2C020206".equals(sC2_YD_UP_WO_LOC)
										 || "2C020207".equals(sC2_YD_UP_WO_LOC)
										 || "2C020208".equals(sC2_YD_UP_WO_LOC) ) {

											if(jsSch.size() > 1) {
												
												for(int ii = 1; ii < jsSch.size(); ii++) {
													
													sC1_YD_SCH_CD =  commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_SCH_CD"));
													sC1_YD_UP_WO_LOC = commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_UP_WO_LOC"));
														
													if("2CTC11LM".equals(sC1_YD_SCH_CD) && "2CTC1".equals(sC1_YD_UP_WO_LOC.substring(0,5))) {
														//1번 대차하차 작업을 지시한다.
		
														ydCrnSchId    = commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_CRN_SCH_ID"   ));
														ydWrkProgStat = commUtils.trim(jsSch.getRecord(ii).getFieldString("YD_WRK_PROG_STAT"));
														
														jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
														
														sYMYMJ204 = bSlabComm.makeYMYMJ204("C1_ADV", "대차3 -> 대차1 우선실행", "7", sC2_YD_WRK_PROG_STAT + "," + sC2_YD_UP_WO_LOC + "," + jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID") + "->" + ydCrnSchId, logId);
													}
												}
											}
										}
									}
								}
							}
						}
					}
					
// SLAB 고도화 - END			
///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
					
					
					/**********************************************************
					* 2.1.2 대기[W] 이면 다음 작업지시 전송
					**********************************************************/

					jrParam.setField("MSG_GP", "I"); //전문구분 - 신규
					jrParam.setField("YD_EQP_STAT", "1"); //권상작업지시

					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp 
					--설비 상태 수정 
					UPDATE TB_YM_EQUIP
					   SET MODIFIER    = :V_MODIFIER
					      ,MOD_DDTT    = SYSDATE
					      ,WPROG_STAT = :V_YD_EQP_STAT
					 WHERE EQUIP_GP    = :V_YD_EQP_ID
					   AND DEL_YN      = 'N'
					*/	   
	        		commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp", logId, methodNm, "B열연SLAB설비상태 수정");

	        		//크레인스케줄 야드작업진행상태 수정
					jrParam.setField("YD_WRK_PROG_STAT"	 , "S"); //선택지시
					jrParam.setField("YD_L2_REQUEST_STAT", "1");
					
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatCrnSchWrkProg 
					--크레인스케줄 작업진행상태 수정
					UPDATE TB_YM_CRNSCH
					   SET MODIFIER         = :V_MODIFIER
					      ,MOD_DDTT         = SYSDATE
					      ,YD_WRK_PROG_STAT = :V_YD_WRK_PROG_STAT
					      ,YD_WORD_DT       = DECODE(:V_YD_WRK_PROG_STAT,'S',SYSDATE,'1',SYSDATE,'W',NULL,YD_WORD_DT)
					      ,YD_L2_REQUEST_STAT = NVL(:V_YD_L2_REQUEST_STAT,YD_L2_REQUEST_STAT) 
					 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
					   AND DEL_YN           = 'N' 
					*/
	        		commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatCrnSchWrkProg", logId, methodNm, "B열연SLAB크레인스케줄 야드작업진행상태 수정");
					//크레인작업지시(YMA8L004) 전문 생성
					
//SJH					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L004", jrParam));
	        		JDTORecordSet jsRtn1 = commDao.getMsgL2("YMA8L004", jrParam);
					jrRtn = commUtils.addSndData(jrRtn , jsRtn1);
					
					//크레인작업지시 Z값 갱신 처리
	        		if(jsRtn1.size() > 0 ) {
	        		//	this.procCrnsSchZaxis(jrParam);
	        			jrParam.setField("YD_UP_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_UP_WO_LOC_ZAXIS"));	
	        			jrParam.setField("YD_DN_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_DN_WO_LOC_ZAXIS"));	
	        			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS 
	    				--크레인스케줄 z값 갱신
	    				UPDATE TB_YM_CRNSCH
	    				   SET YD_UP_WO_LOC_ZAXIS = :V_YD_UP_WO_LOC_ZAXIS
	    				     , YD_DN_WO_LOC_ZAXIS = :V_YD_DN_WO_LOC_ZAXIS
	    				 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
	    				   AND DEL_YN           = 'N' 
	    				*/
	            		commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS", logId, methodNm, "SLAB크레인스케줄 Z값 수정");
	        			        			
	        		}
	        		
					commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
				}	
				
			} else {
				/**********************************************************
				* 2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
				*    2.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 Error 처리
				*    2.2 권하완료[4] 이면 스케줄을 생성
				*    2.3 명령선택대기[W] 이면 응답 전문을 전송 -> 2.2로 통합
				**********************************************************/
				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.2.1 재지시요구 시
					**********************************************************/
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , "크레인[" + ydEqpId + "-" + ydWrkProgStat + "] 작업지시 없음"); //야드L3MESSAGE
					
					jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));

					commUtils.printLog(logId, "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + ydEqpId + " : " + ydWrkProgStat + " - " + ydCrnSchId + " ]", "SL");
				} else {
					/**********************************************************
					* 2.2.2 대기상태[W], 권하완료[4] 지시요구
					**********************************************************/
					//크레인작업지시가 없으면 설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "W"); //대기(Wait)

	        		commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp", logId, methodNm, "B열연SLAB설비상태 수정");
	        		

	        		JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

	    			//작업예약 조회
					 /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWbIdA8YML007 
					SELECT *
					  FROM (
					        SELECT A.YD_WBOOK_ID
					             , A.YD_SCH_CD
					             , A.YD_SCH_CD_CNT
					             , A.YD_SCH_CD_YN
					             , A.YD_WRK_PLAN_TCAR
					             , A.YD_WRK_PLAN_CRN
					          FROM (
					                SELECT YD_WBOOK_ID
					                     , YD_SCH_CD
					                     , SUM(1) OVER(PARTITION BY YD_SCH_CD) AS YD_SCH_CD_CNT 
					                     , 'Y'  AS YD_SCH_CD_YN 
					                     , YD_WRK_PLAN_TCAR
					                     , YD_WRK_PLAN_CRN
					                     , SCH_CNCL_YN
					                     , (SELECT YD_SCH_AUTO_ST_YN 
					                          FROM TB_YM_SCHEDULERULE 
					                         WHERE YD_SCH_CD = WB.YD_SCH_CD 
					                           AND DEL_YN = 'N') AS YD_SCH_AUTO_ST_YN --스케줄별 자동기동여부 DEFAULT Y
					                  FROM TB_YM_WRKBOOK WB
					                 WHERE DEL_YN = 'N'
					                   --AND YD_WRK_PLAN_TCAR IS NULL
					                   --대차상차 인 경우 설비TABLE 적재가능 매수 보다 크거나 같으면 선택불가
					                   --대차 스케줄 과 목적동이 틀리면 선택불가 
					                   AND 1 =  CASE WHEN SUBSTR(YD_SCH_CD,3,2) = 'TC' AND SUBSTR(WB.YD_SCH_CD,7,1) = 'U' THEN 
					                                 CASE WHEN (SELECT COUNT(*) 
					                                              FROM TB_YM_STACKLAYER 
					                                             WHERE STACK_COL_GP = SUBSTR(WB.YD_SCH_CD,1,2)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
					                                               AND STOCK_ID IS NOT NULL) >= (SELECT STACK_MAX_QNTY FROM TB_YM_EQUIP WHERE EQUIP_GP = WB.YD_WRK_PLAN_TCAR )
					                                      THEN '2'
					                                      WHEN (SELECT NVL(SUBSTR(YD_CARUD_STOP_LOC,2,1),  WB.YD_AIM_BAY_GP )
					                                              FROM TB_YM_TCARSCH 
					                                             WHERE DEL_YN = 'N'
					                                               AND YD_EQP_ID = WB.YD_WRK_PLAN_TCAR) <> WB.YD_AIM_BAY_GP 
					                                      THEN '2'    
					                                      WHEN (SELECT SUBSTR(CURR_STOP_LOC,2,1)
					                                              FROM TB_YM_EQUIP 
					                                             WHERE DEL_YN = 'N'
					                                               AND EQUIP_GP = WB.YD_WRK_PLAN_TCAR) <> SUBSTR(WB.YD_SCH_CD,2,1)
					                                      THEN '2'
					                                      -- 대차 출발시 문제발생     
					                                      WHEN (SELECT COUNT(*) 
					                                              FROM TB_YM_STACKER 
					                                             WHERE STACK_COL_GP = SUBSTR(WB.YD_SCH_CD,1,2)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
					                                               AND STACK_BED_ACTIVE_STAT NOT IN ('L')) > 0   
					                                      THEN '2'
					                                      -- 대차 상차 로직
					                                      ELSE '1' END
					                                 ELSE '1' END    
					                   -- 대차에 관련된 스케쥴 기동시 동간작업기준과 틀리면 기동처리 안함                 
					                   AND 1 =  CASE WHEN (SELECT COUNT(*) 
					                                         FROM TB_YM_WRKBOOK WB1
					                                            , (SELECT EQUIP_GP
					                                                    , SUBSTR(CARLOAD_STOP_LOC,1,2)  ||SUBSTR(CARLD_SCH_CD,3,6) AS UP_SCH
					                                                    , SUBSTR(CARUNLOAD_STOP_LOC,1,2)||SUBSTR(CARUD_SCH_CD,3,6) AS DN_SCH
					                                                 FROM TB_YM_EQUIP WHERE EQUIP_GP LIKE '2XTC0%')  EQ1 
					                                            , (SELECT *
					                                                 FROM USRYMA.TB_YM_RULE 
					                                                WHERE REPR_CD_GP = 'YM2007'
					                                                  AND CD_GP = '2'
					                                                  AND DEL_YN = 'N') RULE
					                                        WHERE WB1.YD_WBOOK_ID = WB.YD_WBOOK_ID
					                                          AND WB1.DEL_YN = 'N'
					                                          AND WB1.YD_WRK_PLAN_TCAR = EQ1.EQUIP_GP
					                                          AND SUBSTR(WB1.YD_SCH_CD,3,6) = SUBSTR(DTL_ITM1(+),3,6) 
					                                          AND 'Y' = CASE WHEN YD_WRK_PLAN_TCAR IS NOT NULL AND SUBSTR(ITEM,1,1) = 'L' AND YD_SCH_CD != UP_SCH THEN  'Y'
					                                                         WHEN YD_WRK_PLAN_TCAR IS NOT NULL AND SUBSTR(ITEM,1,1) = 'U' AND YD_SCH_CD != DN_SCH THEN  'Y'
					                                                         ELSE 'N' END  ) > 0
					                                 THEN '2'                                                      
					                                 ELSE '1' END     
					                                  
					                   AND TRN_EQP_CD       IS NULL
					                   AND YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
					                                             FROM TB_YM_CRNSCH
					                                            WHERE DEL_YN = 'N')
					                   AND (YD_WRK_PLAN_CRN = :V_YD_EQP_ID 
					                         OR YD_SCH_CD IN
					                            (SELECT YD_SCH_CD
					                               FROM TB_YM_SCHEDULERULE
					                              WHERE (YD_WRK_CRN = :V_YD_EQP_ID )
					                                AND DEL_YN          = 'N')
					                       )
					                   AND NVL(YD_WRK_PLAN_CRN, :V_YD_EQP_ID ) = :V_YD_EQP_ID
					                  ORDER BY (CASE WHEN YD_WRK_PLAN_CRN=:V_YD_EQP_ID  THEN 1 ELSE 2 END ),YD_SCH_PRIOR, YD_WBOOK_ID     
					               ) A
					         WHERE 1=1
					           AND YD_SCH_CD_YN = 'Y'        
					           AND YD_SCH_AUTO_ST_YN = 'Y'
					       )
					   WHERE ROWNUM = 1
					*/
	        		JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWbIdA8YML007", logId, methodNm, "B열연SLAB작업예약 조회");

					//작업예약이 있으면 크레인스케줄호출
					if (jsWrkBook.size() > 0) {
						ydL3Msg = "크레인스케줄 호출";

						jrYdMsg.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"    , jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
						jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
						jrYdMsg.setField("MODIFIER"   , modifier); //수정자
							
						// 크레인스케줄 기동
						jrRtn = ymComm.getCrnSchMsg(jrYdMsg);
					} else {
						ydL3Msg = "다음 크레인작업지시 없음";
						
///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
// SLAB 고도화 - C1 홈포지션 결정 START			
									
						if("C1".equals(ydEqpId.substring(4,6))) {
						
							String sAPP100_C1_HOME_PO_YN = ymComm.BCoilApplyYn("APP100","2","C1_HOME_PO");  
							commUtils.printLog(logId,  "==========[[[ SLAB C1 홈포지션 이동지시 생성 여부 :" + sAPP100_C1_HOME_PO_YN + " ]]]============", "SL");
						
							if("Y".equals(sAPP100_C1_HOME_PO_YN)) {
								
								jrParam.setField("YD_SCH_CD", "2CWB01UM");
								JDTORecordSet jsChk = commDao.select(jrParam,"com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "C1 홈포지션 결정 - W/B보급크레인 조회");
								
								if(jsChk.size()>0) {
									
									if("2CCRC2".equals(jsChk.getRecord(0).getFieldString("YD_WRK_CRN"))) {
										//W/B보급크레인이 C2 일때
										
										JDTORecord jrRtn1 = null;
										String sC2UpWoLoc = "";
										String sC2WrkProgStat = "";
										String[] sTC_STAT = new String[3];
										String[] sTC_L_BAY = new String[3];
										String[] sTC_U_BAY = new String[3];
										String[] sTC_EQUIP_STAT = new String[3];
										String[] sTC_ARR_LEFT_SEC = new String[3];
										
										
										jrParam.setField("YD_GP"		, "2");
										jrParam.setField("YD_EQP_ID"	, "2CCRC2");
										jrParam.setField("YD_SCH_CD"	, "2CWB01UM");
										jrParam.setField("PAGE_NO"		, "1");
										jrParam.setField("PAGE_SIZE"	, "100");
										
										jsChk = commDao.select(jrParam,"com.inisteel.cim.ym.bslab.dao.BSlabDAO.getbtCrnWrkMgtjmNew", logId, methodNm, "C2 크레인작업관리 스케줄조회");
										if(jsChk.size()>0) {
											sC2UpWoLoc 		= commUtils.trim(jsChk.getRecord(0).getFieldString("YD_UP_WO_LOC"));
											sC2WrkProgStat 	= commUtils.trim(jsChk.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
											commUtils.printLog(logId,  "C2권상위치:"+sC2UpWoLoc+", C2작업진행상태:"+sC2WrkProgStat, "SL");
											
											if("W".equals(sC2WrkProgStat)||"S".equals(sC2WrkProgStat)||"1".equals(sC2WrkProgStat)) {
												if(sC2UpWoLoc.length() >= 5) {
													if("2CTC2".equals(sC2UpWoLoc.substring(0,5)) || "2C01".equals(sC2UpWoLoc.substring(0,4))) {
														//권상위치가 대차2 이거나 01Span 이면 3번대차 HOME 위치로 이동지시
														
														jrParam.setField("YD_EQP_ID"    , "2CCRC1"   ); //야드설비ID
														jrParam.setField("MV_GP"    	, "1"   ); 
														jrParam.setField("HOME_PO_NO" 	, "2XTC03"   );
														jrRtn1 = this.makeHomePosMvWo(jrParam);
														
														if(jrRtn1 != null) {
															jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
															sYMYMJ204 = bSlabComm.makeYMYMJ204("C1_HOME_PO", "C1 홈포지션 이동 -> " + jrParam.getFieldString("HOME_PO_NO"), "1", sC2UpWoLoc, logId);
															jrRtn = commUtils.addSndData(jrRtn, sYMYMJ204);
															commUtils.printLog(logId, methodNm, "S-");
															return jrRtn;
														}
													}
												}
											}
										}
										
										jrParam.setField("EQUIP_GP"		, "");
										jsChk = commDao.select(jrParam,"com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getC1HomePosDcsnTcInfo", logId, methodNm, "C1 홈포지션 결정 대차 정보 조회");
										
										if(jsChk.size() == 3) {
											
											String sGUBUN = "";
											
											//차량진행상태
											sTC_STAT[0] = jsChk.getRecord(0).getFieldString("YD_CAR_PROG_STAT");
											sTC_STAT[1] = jsChk.getRecord(1).getFieldString("YD_CAR_PROG_STAT");
											sTC_STAT[2] = jsChk.getRecord(2).getFieldString("YD_CAR_PROG_STAT");
											//설비상태
											sTC_EQUIP_STAT[0] = jsChk.getRecord(0).getFieldString("EQUIP_STAT");
											sTC_EQUIP_STAT[1] = jsChk.getRecord(1).getFieldString("EQUIP_STAT");
											sTC_EQUIP_STAT[2] = jsChk.getRecord(2).getFieldString("EQUIP_STAT");
											//상차동
											sTC_L_BAY[0] = jsChk.getRecord(0).getFieldString("L_BAY");
											sTC_L_BAY[1] = jsChk.getRecord(1).getFieldString("L_BAY");
											sTC_L_BAY[2] = jsChk.getRecord(2).getFieldString("L_BAY");
											//하차동
											sTC_U_BAY[0] = jsChk.getRecord(0).getFieldString("U_BAY");
											sTC_U_BAY[1] = jsChk.getRecord(1).getFieldString("U_BAY");
											sTC_U_BAY[2] = jsChk.getRecord(2).getFieldString("U_BAY");
											//하차줄발일시
											sTC_ARR_LEFT_SEC[0] = jsChk.getRecord(0).getFieldString("ARR_LEFT_SEC");
											sTC_ARR_LEFT_SEC[1] = jsChk.getRecord(1).getFieldString("ARR_LEFT_SEC");
											sTC_ARR_LEFT_SEC[2] = jsChk.getRecord(2).getFieldString("ARR_LEFT_SEC");
											
											if("O".equals(sTC_EQUIP_STAT[1]) && "O".equals(sTC_EQUIP_STAT[2])) {
												
												if(("A".equals(sTC_STAT[1]) && "C".equals(sTC_U_BAY[1])) && !("A".equals(sTC_STAT[2]) && "C".equals(sTC_U_BAY[2])) ) {
													//2번대차만 C동으로 하차출발한 상태
													sGUBUN = "2번대차만 C동으로 하차출발한 상태";
													jrParam.setField("YD_EQP_ID"    , "2CCRC1"   ); //야드설비ID
													jrParam.setField("MV_GP"    	, "1"   ); 
													jrParam.setField("HOME_PO_NO" 	, "2XTC02"   );
													jrRtn1 = this.makeHomePosMvWo(jrParam);
												} else if(!("A".equals(sTC_STAT[1]) && "C".equals(sTC_U_BAY[1])) && ("A".equals(sTC_STAT[2]) && "C".equals(sTC_U_BAY[2])) ) { 
													//3번대차만 C동으로 하차출발한 상태
													sGUBUN = "3번대차만 C동으로 하차출발한 상태";
													jrParam.setField("YD_EQP_ID"    , "2CCRC1"   ); //야드설비ID
													jrParam.setField("MV_GP"    	, "1"   ); 
													jrParam.setField("HOME_PO_NO" 	, "2XTC03"   );
													jrRtn1 = this.makeHomePosMvWo(jrParam);
												} else if(("A".equals(sTC_STAT[1]) && "C".equals(sTC_U_BAY[1])) && ("A".equals(sTC_STAT[2]) && "C".equals(sTC_U_BAY[2])) ) {
													//2번대차,3번대차 모두 C동으로 하차출발한 상태
													jrParam.setField("YD_EQP_ID"    , "2CCRC1"   ); //야드설비ID
													jrParam.setField("MV_GP"    	, "1"   );
													
													double dTc2ArrLeftSec = Double.parseDouble(commUtils.nvl(sTC_ARR_LEFT_SEC[1],"999999999")); 
													double dTc3ArrLeftSec = Double.parseDouble(commUtils.nvl(sTC_ARR_LEFT_SEC[2],"999999999")); 
														
													if(dTc2ArrLeftSec < dTc3ArrLeftSec) {
														//C동에 2번 대차가 먼저 도착 예정
														sGUBUN = "C동에 2번 대차가 먼저 도착 예정";
														jrParam.setField("YD_EQP_ID"    , "2CCRC1"   ); //야드설비ID
														jrParam.setField("MV_GP"    	, "1"   ); 
														jrParam.setField("HOME_PO_NO" 	, "2XTC02"   );
														jrRtn1 = this.makeHomePosMvWo(jrParam);
													} else {
														sGUBUN = "C동에 3번 대차가 먼저 도착 예정";
														jrParam.setField("YD_EQP_ID"    , "2CCRC1"   ); //야드설비ID
														jrParam.setField("MV_GP"    	, "1"   ); 
														jrParam.setField("HOME_PO_NO" 	, "2XTC03"   );
														jrRtn1 = this.makeHomePosMvWo(jrParam);
													}
												} else {
													
												}
												
											} else if("O".equals(sTC_EQUIP_STAT[1]) && "C".equals(sTC_EQUIP_STAT[2])) {
												//3번대차 고장
												if(("A".equals(sTC_STAT[1]) && "C".equals(sTC_U_BAY[1]))) {
													//2번대차 하차출발
													sGUBUN = "3번대차 고장 2번대차 하차출발 ";
													jrParam.setField("YD_EQP_ID"    , "2CCRC1"   ); //야드설비ID
													jrParam.setField("MV_GP"    	, "1"   ); 
													jrParam.setField("HOME_PO_NO" 	, "2XTC02"   );
													jrRtn1 = this.makeHomePosMvWo(jrParam);
												}
												
											} else if("C".equals(sTC_EQUIP_STAT[1]) && "O".equals(sTC_EQUIP_STAT[2])) {
												//2번대차 고장
												if(("A".equals(sTC_STAT[2]) && "C".equals(sTC_U_BAY[2]))) {
													//3번대차 하차출발
													sGUBUN = "2번대차 고장 3번대차 하차출발 ";
													jrParam.setField("YD_EQP_ID"    , "2CCRC1"   ); //야드설비ID
													jrParam.setField("MV_GP"    	, "1"   ); 
													jrParam.setField("HOME_PO_NO" 	, "2XTC03"   );
													jrRtn1 = this.makeHomePosMvWo(jrParam);
												}
											}
											
											if(jrRtn1 != null) {
												jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
												sYMYMJ204 = bSlabComm.makeYMYMJ204("C1_HOME_PO", "C1 홈포지션 이동 -> " + jrParam.getFieldString("HOME_PO_NO"), "2", sGUBUN, logId);
												jrRtn = commUtils.addSndData(jrRtn, sYMYMJ204);
												commUtils.printLog(logId, methodNm, "S-");
												return jrRtn;
											}
											
											//--------------------------------------------------------------------------------------
											// 대차출발 정보로 HOME포지션 이동지시를 정할 수 없을 때
											String sTC2_STAT_RANK = "";
											String sTC3_STAT_RANK = "";
											
											jrParam.setField("L_BAY"		, sTC_L_BAY[1]); //대차2호기 상차동
											jrParam.setField("TC_HOGI"		, "2"); //대차2호기
											jsChk = commDao.select(jrParam,"com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getC1HomePosDcsnCrnSchInfo", logId, methodNm, "C1 홈포지션 결정 크레인 스케줄 정보 조회");
											if(jsChk.size()>0) {
												sTC2_STAT_RANK = commUtils.trim(jsChk.getRecord(0).getFieldString("STAT_RANK"));
											}
											
											jrParam.setField("L_BAY"		, sTC_L_BAY[2]); //대차3호기 상차동
											jrParam.setField("TC_HOGI"		, "3"); //대차2호기
											jsChk = commDao.select(jrParam,"com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getC1HomePosDcsnCrnSchInfo", logId, methodNm, "C1 홈포지션 결정 크레인 스케줄 정보 조회");
											if(jsChk.size()>0) {
												sTC3_STAT_RANK = commUtils.trim(jsChk.getRecord(0).getFieldString("STAT_RANK"));
											}
											
											if(!"".equals(sTC2_STAT_RANK) && "".equals(sTC3_STAT_RANK)) {
												//2번대차 스케줄이 있으면
												sGUBUN = "2번대차 스케줄이 존재 ";
												jrParam.setField("YD_EQP_ID"    , "2CCRC1"   ); //야드설비ID
												jrParam.setField("MV_GP"    	, "1"   ); 
												jrParam.setField("HOME_PO_NO" 	, "2XTC02"   );
												jrRtn1 = this.makeHomePosMvWo(jrParam);
											} else if("".equals(sTC2_STAT_RANK) && !"".equals(sTC3_STAT_RANK)) {
												//3번대차 스케줄이 있으면
												sGUBUN = "3번대차 스케줄이 존재 ";
												jrParam.setField("YD_EQP_ID"    , "2CCRC1"   ); //야드설비ID
												jrParam.setField("MV_GP"    	, "1"   ); 
												jrParam.setField("HOME_PO_NO" 	, "2XTC03"   );
												jrRtn1 = this.makeHomePosMvWo(jrParam);
											} if(!"".equals(sTC2_STAT_RANK) && !"".equals(sTC3_STAT_RANK)) {
												//2번,3번대차 스케줄이 모두 존재하면
												int iTc2StatRank = Integer.parseInt(commUtils.nvl(sTC2_STAT_RANK,"99")); 
												int iTc3StatRank = Integer.parseInt(commUtils.nvl(sTC3_STAT_RANK,"99")); 
												if(iTc2StatRank < iTc3StatRank) {
													sGUBUN = "TC2_RANK:"+iTc2StatRank+",TC3_RANK:"+iTc3StatRank;
													jrParam.setField("YD_EQP_ID"    , "2CCRC1"   ); //야드설비ID
													jrParam.setField("MV_GP"    	, "1"   ); 
													jrParam.setField("HOME_PO_NO" 	, "2XTC02"   );
													jrRtn1 = this.makeHomePosMvWo(jrParam);
												} else {
													sGUBUN = "TC2_RANK:"+iTc2StatRank+",TC3_RANK:"+iTc3StatRank;
													jrParam.setField("YD_EQP_ID"    , "2CCRC1"   ); //야드설비ID
													jrParam.setField("MV_GP"    	, "1"   ); 
													jrParam.setField("HOME_PO_NO" 	, "2XTC03"   );
													jrRtn1 = this.makeHomePosMvWo(jrParam);
												}
											} 
											
											if(jrRtn1 != null) {
												jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
												sYMYMJ204 = bSlabComm.makeYMYMJ204("C1_HOME_PO", "C1 홈포지션 이동 -> " + jrParam.getFieldString("HOME_PO_NO"), "3", sGUBUN, logId);
												jrRtn = commUtils.addSndData(jrRtn, sYMYMJ204);
												commUtils.printLog(logId, methodNm, "S-");
												return jrRtn;
											}
										}
										
										jrParam.setField("YD_EQP_ID"    , "2CCRC1"   ); //야드설비ID
										jrParam.setField("MV_GP"    	, "1"   ); 
										jrParam.setField("HOME_PO_NO" 	, "2XTC03"   );
										jrRtn1 = this.makeHomePosMvWo(jrParam);
										
										if(jrRtn1 != null) {
											jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
											sYMYMJ204 = bSlabComm.makeYMYMJ204("C1_HOME_PO", "C1 홈포지션 이동 -> " + jrParam.getFieldString("HOME_PO_NO"), "4", ""+jsChk.size(), logId);
											jrRtn = commUtils.addSndData(jrRtn, sYMYMJ204);
											commUtils.printLog(logId, methodNm, "S-");
											return jrRtn;
										}
										
									} //<--W/B보급크레인이 C2 일때 if 문 
								}
							}
						}
									
// SLAB 고도화 - C1 홈포지션 결정 END			
///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
						
					}
					
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
					resMsg.setField("YD_CRN_SCH_ID" , ""     ); 
					
					jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
					
					commUtils.printLog(logId, "B열연SLAB크레인작업지시요구(다음지시) " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");
				}
			}
			
			if(sYMYMJ204 != null) {
				jrRtn = commUtils.addSndData(jrRtn, sYMYMJ204);
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			try {
				//PIDEV_F : 정상SET후  ERROR 발생한 경우								
				if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
					resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
					resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
				}								

				//크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "YmCommEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { bSlabComm.getYMA8L005(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of B열연SLAB크레인작업지시요구 - C2 고도화 (A8YML007C2Adv)
	 
	/**
	 *      [A] 오퍼레이션명 : B열연SLAB크레인작업지시요구 - C2 고도화(A8YML007C2Adv)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvA8YML007C2Adv(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "B열연SLAB크레인작업지시요구 - C2 고도화[BSlabL2RcvSeEJB.rcvA8YML007C2Adv] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용

		try {
			commUtils.printLog(logId, methodNm, "S+"); 
			
			JDTORecord sYMYMJ204 = null;
			
			//수신 항목 값 
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 - C2 고도화 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");

			JDTORecord jrRtn  = JDTORecordFactory.getInstance().create();	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE
			
			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "J"          ); //야드L2실적구분(지시요구)
			resMsg.setField("YD_L3_HD_RS_CD"  , "JR99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:크레인작업지시요구 수신처리"); //야드L3MESSAGE(Error)

			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"    , ydEqpId   ); //야드설비ID
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			/**********************************************************
			* 1. 설비상태 Check
			**********************************************************/
			JDTORecord jrChk = ymComm.chkEqpStat(jrParam);

			ydL3HdRsCd = commUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));
			ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			
			/**********************************************************
			* 2. 크레인스케줄 조회
			*    2.1 크레인스케줄이 존재하면 전송
			*    2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
			**********************************************************/
			/* 크레인작업지시요구 크레인스케줄 조회  */
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchA8YML007 
			SELECT YD_CRN_SCH_ID
			     , YD_WRK_PROG_STAT
			     , YD_UP_WO_LOC 
			     , YD_DN_WO_LOC 
			     , YD_SCH_CD
			     , YD_SCH_PRIOR
			     , SEQ1
			     , YD_SCH_ST_GP
			     , YD_WRK_PLAN_CRN
			     , YD_WRK_PLAN_CRN2
			     , OTHER_CRN_CNT 
			     , OTHER_BRA_CNT
			     -- 이송하차이고 
			     -- 상대편 크레인 작업이 없고 
			     -- 스케쥴 기동구분이 'N'(멀티) 인 경우
			     -- 동일작업이 2건이면 멀티작업가능
			     , CASE WHEN SUBSTR(YD_SCH_CD,3,6) = 'PT02LM' 
			             AND OTHER_CRN_CNT = 0 
			             AND OTHER_BRA_CNT = 0
			             AND YD_SCH_ST_GP  = 'N'
			             AND YD_SCH_CD = LEAD(YD_SCH_CD) OVER (ORDER BY SEQ1 DESC, YD_SCH_PRIOR, YD_CRN_SCH_ID) THEN 'Y'
			            ELSE 'N' END MULTI_YN
			  FROM
			       (
			         SELECT CS.YD_CRN_SCH_ID
			              , CS.YD_WRK_PROG_STAT
			              , CS.YD_UP_WO_LOC 
			              , CS.YD_DN_WO_LOC 
			              , CS.YD_SCH_CD
			              , CS.YD_SCH_PRIOR
			              , DECODE(CS.YD_WRK_PROG_STAT,'W','0','S','1', CS.YD_WRK_PROG_STAT) AS SEQ1
			              , WB.YD_SCH_ST_GP
			              , WB.YD_WRK_PLAN_CRN
			              , WB.YD_WRK_PLAN_CRN2
			              --상대크레인 작업 건수
			              , (SELECT COUNT(*) 
			                   FROM TB_YM_CRNSCH 
			                  WHERE DEL_YN = 'N'
			                    AND YD_EQP_ID = WB.YD_WRK_PLAN_CRN2) AS OTHER_CRN_CNT
			              , (SELECT COUNT(*)
			                   FROM TB_YM_EQUIP 
			                  WHERE EQUIP_GP = WB.YD_WRK_PLAN_CRN2
			                    AND (WPROG_STAT = 'B' OR WORK_MODE = '2')) AS OTHER_BRA_CNT -- 고장여부
			           FROM TB_YM_CRNSCH CS
			              , TB_YM_WRKBOOK WB
			          WHERE CS.YD_WBOOK_ID = WB.YD_WBOOK_ID
			            AND CS.DEL_YN = 'N'
			            AND WB.DEL_YN = 'N'
			            AND CS.YD_EQP_ID =  :V_YD_EQP_ID 
			            --권상위치 상단에 타크레인 존재여부 
			            AND 1 = CASE WHEN  (SELECT COUNT(*) 
			                                  FROM TB_YM_CRNSCH 
			                                 WHERE DEL_YN = 'N' 
			                                   AND YD_UP_WO_LOC   = CS.YD_UP_WO_LOC 
			                                   AND TO_NUMBER(YD_UP_WO_LAYER)  > TO_NUMBER(CS.YD_UP_WO_LAYER) 
			                                   AND YD_EQP_ID <> :V_YD_EQP_ID) = 0 THEN 1
			                         ELSE 0 END          
			            --권하위치 하단에 타크레인 존재여부 
			            AND 1 = CASE WHEN  (SELECT COUNT(*) 
			                                  FROM TB_YM_CRNSCH 
			                                 WHERE DEL_YN = 'N' 
			                                   AND YD_DN_WO_LOC   = CS.YD_DN_WO_LOC 
			                                   AND TO_NUMBER(YD_DN_WO_LAYER)  < TO_NUMBER(CS.YD_DN_WO_LAYER) 
			                                   AND YD_EQP_ID <> :V_YD_EQP_ID) = 0 THEN 1
			                         ELSE 0 END          
			          ORDER BY SEQ1 DESC, YD_SCH_PRIOR, YD_CRN_SCH_ID
			       )    
			 WHERE ROWNUM <= 1
			 */
			JDTORecordSet jsSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchA8YML007Adv", logId, methodNm, "B열연SLAB C2 크레인스케줄 조회");

			if (jsSch.size() > 0) {
				/**********************************************************
				* 2.1 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송
				**********************************************************/
				ydCrnSchId    = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				ydWrkProgStat = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
				ydSchCd		  = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_SCH_CD"));

				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID

				if ("S".equals(ydWrkProgStat)||"1".equals(ydWrkProgStat) ||"2".equals(ydWrkProgStat) ||"3".equals(ydWrkProgStat)||"5".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.1.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 재지시 전송
					**********************************************************/
					jrParam.setField("MSG_GP", "U"); //전문구분 - 재지시
					/*
					UPDATE TB_YM_CRNSCH
					   SET MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					     , YD_WORD_DT       = SYSDATE
					 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
					   AND DEL_YN           = 'N'					 
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdWorkDt");					

					//크레인작업지시(YMA8L004) 전문 재전송
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L004", jrParam));

					commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
				} else {
					

					String sAPP100_C2_ADV_YN = ymComm.BCoilApplyYn("APP100","2","C2_ADV_YN");
					
					if("Y".equals(sAPP100_C2_ADV_YN)) {
///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
// SLAB 고도화 - START			
					
//     C2 스케줄이 WB보급일 경우
						
						String sC2_YD_SCH_CD =  commUtils.trim(jsSch.getRecord(0).getFieldString("YD_SCH_CD"));
						
						if("2CWB01UM".equals(sC2_YD_SCH_CD)) { //C동 WB보급일 경우
							
							JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
							jrParam2.setResultCode(logId);	//Log ID
							jrParam2.setResultMsg(methodNm);	//Log Method Name
							
							JDTORecordSet jsSch2 = commDao.select(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWbCtcCnt", logId, methodNm, "WB1,4,5,CTC4 SLAB 적치상태 CHECK");
							if (jsSch2.size() > 0) {
								
								if("Y".equals(jsSch2.getRecord(0).getFieldString("FLAG"))) {
									// CTC4에 SLAB 있고 WB5에 2매 WB4에 2매 WB1에 SLAB가 적치되어 있으면 C2 대차작업 가능
									
									jsSch2 = commDao.select(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getTC1LMCrnSch", logId, methodNm, "C동 1번대차하차 스케줄 조회");
									if (jsSch2.size() > 0) {
										//1번 대차 하차 스케줄의 상태가 'W'이이고 권하위치가 01스판 12열이거나  02 SPAN 이면
										
										//1번 대차 하차스케줄의 작업 크레인을 C2로 변경하고 C2에게 작업지시
										String ydCrnSchId2    = commUtils.trim(jsSch2.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
										String ydWrkProgStat2 = commUtils.trim(jsSch2.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
										
										jrParam2.setField("MODIFIER"		, "C2_ADV");
										jrParam2.setField("YD_EQP_ID"		, "2CCRC2");
										jrParam2.setField("YD_SCH_CD"		, "2CTC22LM");
										jrParam2.setField("YD_CRN_SCH_ID"	, ydCrnSchId2);
										
										if(commDao.update(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCrnSchEqpIdSchCd", logId, methodNm, "크레인스케줄 변경 - 작업크레인, 스케줄코드") > 0) {
											
											jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId2); 
											
											sYMYMJ204 = bSlabComm.makeYMYMJ204("C2_ADV", "C2 대차지원 ", "1", ydCrnSchId + "->" + ydCrnSchId2, logId);
											jrRtn = commUtils.addSndData(jrRtn, sYMYMJ204);
											
											ydCrnSchId    = ydCrnSchId2;
											ydWrkProgStat = ydWrkProgStat2;
											ydSchCd		  = "2CTC22LM";
										}
									}
								}
							}
							//C2에게 WB보급 작업지시를 준다.
						} 
					
// SLAB 고도화 - END			
///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
					}
					
					/**********************************************************
					* 2.1.2 대기[W] 이면 다음 작업지시 전송
					**********************************************************/

					jrParam.setField("MSG_GP", "I"); //전문구분 - 신규
					jrParam.setField("YD_EQP_STAT", "1"); //권상작업지시

					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp 
					--설비 상태 수정 
					UPDATE TB_YM_EQUIP
					   SET MODIFIER    = :V_MODIFIER
					      ,MOD_DDTT    = SYSDATE
					      ,WPROG_STAT = :V_YD_EQP_STAT
					 WHERE EQUIP_GP    = :V_YD_EQP_ID
					   AND DEL_YN      = 'N'
					*/	   
	        		commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp", logId, methodNm, "B열연SLAB설비상태 수정");

	        		//크레인스케줄 야드작업진행상태 수정
					jrParam.setField("YD_WRK_PROG_STAT"	 , "S"); //선택지시
					jrParam.setField("YD_L2_REQUEST_STAT", "1");
					
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatCrnSchWrkProg 
					--크레인스케줄 작업진행상태 수정
					UPDATE TB_YM_CRNSCH
					   SET MODIFIER         = :V_MODIFIER
					      ,MOD_DDTT         = SYSDATE
					      ,YD_WRK_PROG_STAT = :V_YD_WRK_PROG_STAT
					      ,YD_WORD_DT       = DECODE(:V_YD_WRK_PROG_STAT,'S',SYSDATE,'1',SYSDATE,'W',NULL,YD_WORD_DT)
					      ,YD_L2_REQUEST_STAT = NVL(:V_YD_L2_REQUEST_STAT,YD_L2_REQUEST_STAT) 
					 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
					   AND DEL_YN           = 'N' 
					*/
	        		commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatCrnSchWrkProg", logId, methodNm, "B열연SLAB크레인스케줄 야드작업진행상태 수정");
	        		
	        		String sAPP100_WB_ADV_YN = ymComm.BCoilApplyYn("APP100","2","WB_ADV_YN");
	        		
	        		if("Y".equals(sAPP100_WB_ADV_YN)) {
///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
// SLAB 고도화 - START			
//	        		    int iWrkSh = Integer.parseInt(commUtils.nvl(jsSch.getRecord(0).getFieldString("YD_EQP_WRK_SH"),"0")); //설비작업매수
//	        		    String sYD_UP_WO_LOC = commUtils.nvl(jsSch.getRecord(0).getFieldString("YD_UP_WO_LOC"),"XX010101"); //권상위치
//	        		    String sYD_DN_WO_LOC = commUtils.nvl(jsSch.getRecord(0).getFieldString("YD_DN_WO_LOC"),"XX010101"); //권하위치
//	        		    String sCHARGE_LOT_NO = commUtils.nvl(jsSch.getRecord(0).getFieldString("CHARGE_LOT_NO"),""); //장입순번
//	        		    
//	        		    if("2CWB01UM".equals(ydSchCd) && "2CWB0101".equals(sYD_DN_WO_LOC) && iWrkSh == 1 
//	        		    		&& ("0204".equals(sYD_UP_WO_LOC.substring(4,8)) || "0205".equals(sYD_UP_WO_LOC.substring(4,8)) 
//	        		    				|| "0206".equals(sYD_UP_WO_LOC.substring(4,8)) || "0207".equals(sYD_UP_WO_LOC.substring(4,8)) || "0208".equals(sYD_UP_WO_LOC.substring(4,8))  ) )  { 
//	        		    	//C동 WB보급 이면서 권하위치가 WB01 Bed이고 작업매수가 1 일 경우 + 권상위치가 02 span 04,05,06,07,08 일경우 
//	        			
//							JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
//							jrParam2.setResultCode(logId);	//Log ID
//							jrParam2.setResultMsg(methodNm);	//Log Method Name
//							
//							JDTORecordSet jsSch2 = commDao.select(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWbCtcCnt", logId, methodNm, "WB1,4,5,CTC4 SLAB 적치상태 CHECK");
//							if (jsSch2.size() > 0) {
//
//								String sFlag2 = jsSch2.getRecord(0).getFieldString("FLAG2");
//								String sWB01_STOCK_ID = jsSch2.getRecord(0).getFieldString("WB01_STOCK_ID");
//								
//								commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 - C2 고도화   sFlag2 :  " + sFlag2 + " , WB01_STOCK_ID : " + sWB01_STOCK_ID , "SL");
//								
//								if("Y".equals(sFlag2)) {
//									//2회 2매작업이 가능한 WB 상태인 경우 
//									jrParam2.setField("STOCK_ID" , sWB01_STOCK_ID); 
//									jsSch2 = commDao.select(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCmplCrnSch", logId, methodNm, "이전 장입완료 CRANE 스케줄 조회");
//
//									if (jsSch2.size() > 0) {
//										
//										if("1".equals(jsSch2.getRecord(0).getFieldString("YD_EQP_WRK_SH")) && sCHARGE_LOT_NO.equals(jsSch2.getRecord(0).getFieldString("CTS_RELAY_SADDLE"))) {
//											//이전 장입완료 스케줄이 1매 작업이고 장입순번이 같을 때
//											
//											//CRANE 작업지시 권하 단을 2단으로 변경
//											jrParam2.setField("MODIFIER"		, "WB_ADV");
//											jrParam2.setField("YD_DN_WO_LAYER"	, "02");
//											jrParam2.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
//											commDao.update(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCrnSchDnWoLayer", logId, methodNm, "CRANE 스케줄 권하지시 단 수정");
//											
//											//적치단(TB_YM_STACKLAYER) 수정
//											//01단에 이전 장입완료 스케줄 1매작업 SLAB 를 적치('C') 시킨다. 
//											jrParam2.setField("MODIFIER"				, "WB_ADV");
//											jrParam2.setField("STOCK_ID"				, jsSch2.getRecord(0).getFieldString("STOCK_ID"));
//											jrParam2.setField("STACK_LAYER_STAT"		, "C");
//											jrParam2.setField("STACK_LAYER_ACTIVE_STAT"	, "E");
//											jrParam2.setField("STACK_COL_GP"			, "2CWB01");
//											jrParam2.setField("STACK_BED_GP"			, "01");
//											jrParam2.setField("STACK_LAYER_GP"			, "01");
//											
//											commDao.update(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByLoc", logId, methodNm, "01 적치단(TB_YM_STACKLAYER) 수정");
//											
//											//02단에 현 장입 스케줄 1매작업 SLAB 를 권하예정('D') 시킨다. 
//											jrParam2.setField("MODIFIER"				, "WB_ADV");
//											jrParam2.setField("STOCK_ID"				, jsSch.getRecord(0).getFieldString("STOCK_ID"));
//											jrParam2.setField("STACK_LAYER_STAT"		, "D");
//											jrParam2.setField("STACK_LAYER_ACTIVE_STAT"	, "E");
//											jrParam2.setField("STACK_COL_GP"			, "2CWB01");
//											jrParam2.setField("STACK_BED_GP"			, "01");
//											jrParam2.setField("STACK_LAYER_GP"			, "02");
//											
//											commDao.update(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByLoc", logId, methodNm, "02 적치단(TB_YM_STACKLAYER) 수정");
//											
//											jrParam.setField("LAST_WORK_ORD_GP", "L"); //WB Lock 걸기
//											
//											
//											sYMYMJ204 = bSlabComm.makeYMYMJ204("WB_ADV", "WB장입 2매 2회작업 ", "1", ydCrnSchId , logId);
//											jrRtn = commUtils.addSndData(jrRtn, sYMYMJ204);
//										}
//									}
//								}
//							}
//	        			}
	        			
	        			int iWrkSh = Integer.parseInt(commUtils.nvl(jsSch.getRecord(0).getFieldString("YD_EQP_WRK_SH"),"0")); //설비작업매수
	        			String sYD_UP_WO_LOC = commUtils.nvl(jsSch.getRecord(0).getFieldString("YD_UP_WO_LOC"),"XX010101"); //권상위치
	        			String sYD_DN_WO_LOC = commUtils.nvl(jsSch.getRecord(0).getFieldString("YD_DN_WO_LOC"),"XX010101"); //권하위치
	        			String sCHARGE_LOT_NO = commUtils.nvl(jsSch.getRecord(0).getFieldString("CHARGE_LOT_NO"),""); //장입순번
	        			
	        			//if("2CWB01UM".equals(ydSchCd) && "2CWB0101".equals(sYD_DN_WO_LOC) && iWrkSh == 1 &&        					
        		    	//   ("0203".equals(sYD_UP_WO_LOC.substring(4,8)) || "0204".equals(sYD_UP_WO_LOC.substring(4,8)) || "0205".equals(sYD_UP_WO_LOC.substring(4,8)) 
   		    			//	|| "0206".equals(sYD_UP_WO_LOC.substring(4,8)) || "0207".equals(sYD_UP_WO_LOC.substring(4,8)) || "0208".equals(sYD_UP_WO_LOC.substring(4,8))  ) )  { 

	        			if("2CWB01UM".equals(ydSchCd) && "2CWB0101".equals(sYD_DN_WO_LOC) && iWrkSh == 1) {         					
	        			
							JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
							jrParam2.setResultCode(logId);	//Log ID
							jrParam2.setResultMsg(methodNm);	//Log Method Name
							
							JDTORecordSet jsCnt = commDao.select(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWbCtcCnt", logId, methodNm, "WB1,4,5,CTC4 SLAB 적치상태 CHECK");
							if (jsCnt.size() > 0) {
								
								String sFlag2 = jsCnt.getRecord(0).getFieldString("FLAG2");
								String sWB01_STOCK_ID = jsCnt.getRecord(0).getFieldString("WB01_STOCK_ID");
								String sWB01_SH = jsCnt.getRecord(0).getFieldString("WB01_SH");
								
								commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 - C2 고도화   sFlag2 :  " + sFlag2 + " , WB01_STOCK_ID : " + sWB01_STOCK_ID + " , WB01_SH : " + sWB01_SH , "SL");
								
								if("2".equals(sWB01_SH)) { //WB 01 BED 활성상태가  2매 작업 가능일때
									
									jrParam2.setField("STOCK_ID" , sWB01_STOCK_ID); 
									JDTORecordSet jsSch2 = commDao.select(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCmplCrnSch", logId, methodNm, "이전 장입완료 CRANE 스케줄 조회");
									
									if (jsSch2.size() > 0) {
										
										String sWrkSh = jsSch2.getRecord(0).getFieldString("YD_EQP_WRK_SH");
										String sRegister = jsSch2.getRecord(0).getFieldString("REGISTER");
										
										if("1".equals(sWrkSh)) {
											
											if("L".equals(sRegister)) {
												
												//CRANE 작업지시 권하 단을 2단으로 변경
												jrParam2.setField("MODIFIER"		, "WB_ADV");
												jrParam2.setField("YD_DN_WO_LAYER"	, "02");
												jrParam2.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
												commDao.update(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCrnSchDnWoLayer", logId, methodNm, "CRANE 스케줄 권하지시 단 수정");
												
												//적치단(TB_YM_STACKLAYER) 수정
												//01단에 이전 장입완료 스케줄 1매작업 SLAB 를 적치('C') 시킨다. 
												jrParam2.setField("MODIFIER"				, "WB_ADV");
												jrParam2.setField("STOCK_ID"				, jsSch2.getRecord(0).getFieldString("STOCK_ID"));
												jrParam2.setField("STACK_LAYER_STAT"		, "C");
												jrParam2.setField("STACK_LAYER_ACTIVE_STAT"	, "E");
												jrParam2.setField("STACK_COL_GP"			, "2CWB01");
												jrParam2.setField("STACK_BED_GP"			, "01");
												jrParam2.setField("STACK_LAYER_GP"			, "01");
												
												commDao.update(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByLoc", logId, methodNm, "01 적치단(TB_YM_STACKLAYER) 수정");
												
												//02단에 현 장입 스케줄 1매작업 SLAB 를 권하예정('D') 시킨다. 
												jrParam2.setField("MODIFIER"				, "WB_ADV");
												jrParam2.setField("STOCK_ID"				, jsSch.getRecord(0).getFieldString("STOCK_ID"));
												jrParam2.setField("STACK_LAYER_STAT"		, "D");
												jrParam2.setField("STACK_LAYER_ACTIVE_STAT"	, "E");
												jrParam2.setField("STACK_COL_GP"			, "2CWB01");
												jrParam2.setField("STACK_BED_GP"			, "01");
												jrParam2.setField("STACK_LAYER_GP"			, "02");
												
												commDao.update(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByLoc", logId, methodNm, "02 적치단(TB_YM_STACKLAYER) 수정");
												
												jrParam.setField("LAST_WORK_ORD_GP", "E"); //WB Lock 해제
												
												
												sYMYMJ204 = bSlabComm.makeYMYMJ204("WB_ADV", "WB장입 2매 2회작업", "E", ydCrnSchId , logId);
												jrRtn = commUtils.addSndData(jrRtn, sYMYMJ204);
												
												
											} else {
												
												if("Y".equals(sFlag2)) {
													//2회 2매작업이 가능한 WB 상태인 경우 
													//다음장입예정 슬라브가 1매작업인지 확인
												
													jrParam2.setField("YD_GP"    , "2"   ); //야드구분
													jrParam2.setField("BAY_GP"   , "C"   ); //동
													
													jsSch2 = commDao.select(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectWBSlabSearch_09_WB", logId, methodNm, "B열연SLAB C2 다음 장입 스케줄 조회");
													
													if (jsSch2.size() > 1) {
												
														int iMaxRec = 2; 
														
														commUtils.printLog(logId, "=======:::: 다음장입대상 SLAB 번호1 : " +  jsSch2.getRecord(0).getFieldString("STOCK_ID") , "SL");
														commUtils.printLog(logId, "=======:::: 다음장입대상 SLAB 번호2 : " +  jsSch2.getRecord(1).getFieldString("STOCK_ID") , "SL");
														
														if(jsSch2.getRecord(0).getFieldString("STACK_COL_GP").equals(jsSch2.getRecord(1).getFieldString("STACK_COL_GP")) 
														   && jsSch2.getRecord(0).getFieldString("STACK_BED_GP").equals(jsSch2.getRecord(1).getFieldString("STACK_BED_GP"))) { //적치열, 적치Bed 가 동일
															
															double dUpperSlabWt = Double.parseDouble(commUtils.nvl(jsSch2.getRecord(0).getFieldString("SLAB_WT"),"0")); //상단 Slab 중량
															double dLowerSlabWt = Double.parseDouble(commUtils.nvl(jsSch2.getRecord(1).getFieldString("SLAB_WT"),"0")); //하단 Slab 중량
															commUtils.printLog(logId, "=======:::: 상단 SLAB 중량 : " +  dUpperSlabWt , "SL");
															commUtils.printLog(logId, "=======:::: 하단 SLAB 중량 : " +  dLowerSlabWt , "SL");
															
															double dUpperSlabW = Double.parseDouble(commUtils.nvl(jsSch2.getRecord(0).getFieldString("SLAB_W"),"0")); //상단 Slab 폭
															double dLowerSlabW = Double.parseDouble(commUtils.nvl(jsSch2.getRecord(1).getFieldString("SLAB_W"),"0")); //하단 Slab 폭
															commUtils.printLog(logId, "=======:::: 상단 SLAB 폭 : " +  dUpperSlabW , "SL");
															commUtils.printLog(logId, "=======:::: 하단 SLAB 폭 : " +  dLowerSlabW , "SL");

															double dRuleSlabW = Double.parseDouble(commUtils.nvl(jsSch2.getRecord(0).getFieldString("WID_DIF"),"0")); //폭기준
															double dmaxStackWt = Double.parseDouble(commUtils.nvl(jsCnt.getRecord(0).getFieldString("STACK_MAX_WT"),"0")); //중량기준
															commUtils.printLog(logId, "=======:::: 폭기준 : " +  dRuleSlabW , "SL");
															commUtils.printLog(logId, "=======:::: 중량기준 : " +  dmaxStackWt , "SL");
															
															if(dUpperSlabWt + dLowerSlabWt > dmaxStackWt) {
																iMaxRec = 1;
																commUtils.printLog(logId, "=======>>>> dUpperSlabWt + dLowerSlabWt > 53200 에 의해 iMaxRec = " +  iMaxRec , "SL");
															} else if(dUpperSlabW - dLowerSlabW > dRuleSlabW) {
																iMaxRec = 1;
																commUtils.printLog(logId, "=======>>>> dUpperSlabW - dLowerSlabW > " + dRuleSlabW + " 에 의해 iMaxRec = " +  iMaxRec , "SL");
															} else if(dUpperSlabW >= 2000) {
																iMaxRec = 1;
																commUtils.printLog(logId, "=======>>>> dUpperSlabW >= 2000 에 의해 iMaxRec = " +  iMaxRec , "SL");
															} else if(dLowerSlabW >= 2000) {
																iMaxRec = 1;
																commUtils.printLog(logId, "=======>>>> dLowerSlabW >= 2000 에 의해 iMaxRec = " +  iMaxRec , "SL");
															} else {
																
																int iUpperSlabChargeLotNo = Integer.parseInt(commUtils.nvl(jsSch2.getRecord(0).getFieldString("CHARGE_LOT_NO"),"0")); //상단 Slab 장입순번
																int iLowerSlabCHargeLotNo = Integer.parseInt(commUtils.nvl(jsSch2.getRecord(1).getFieldString("CHARGE_LOT_NO"),"0")); //하단 Slab 장입순번
																commUtils.printLog(logId, "=======|||| 상단 SLAB 장입순위 : " +  iUpperSlabChargeLotNo , "SL");
																commUtils.printLog(logId, "=======|||| 하단 SLAB 장입순위 : " +  iLowerSlabCHargeLotNo , "SL");
																
																if(iUpperSlabChargeLotNo == iLowerSlabCHargeLotNo) { 
																	//장입순번이 같음
																	iMaxRec = 2;
																	commUtils.printLog(logId, "=======>>>> 상단,하단 장입순위 같음  iMaxRec = " +  iMaxRec , "SL");
																	
																} else { 
																	//장입순번이 다름
																	
																	if(iUpperSlabChargeLotNo + 1 == iLowerSlabCHargeLotNo ) {
																		//장입순번이 상단 하단 연속으로 되어 있으면 2매 작업 가능
																		iMaxRec = 2;
																		commUtils.printLog(logId, "=======>>>> 상단,하단 장입순위 다르지만 연속으로 되어 있음  iMaxRec = " +  iMaxRec , "SL");
																	} else {
																		iMaxRec = 1;
																	}
																}
																
																if(iMaxRec == 2) {
																	//장입순번이 같거나 상단 하단 연속으로 되어 있더라도 
																	//위치(열,BED)가 틀리거나 단이 연속으로 되어 있지 않을 경우 1매 작업
																	
																	//동일BED 체크
																	String dUpperBedGp = jsSch2.getRecord(0).getFieldString("STACK_COL_GP") + jsSch2.getRecord(0).getFieldString("STACK_BED_GP"); //상단 BED
																	String dLowerBedGp = jsSch2.getRecord(1).getFieldString("STACK_COL_GP") + jsSch2.getRecord(1).getFieldString("STACK_BED_GP"); //하단 BED
																	commUtils.printLog(logId, "=======:::: 상단 열+BED : " +  dUpperBedGp , "SL");
																	commUtils.printLog(logId, "=======:::: 하단 열+BED : " +  dLowerBedGp , "SL");
																	
																	//연속단 체크 
																	int iUpperLayerGp = Integer.parseInt(commUtils.nvl(jsSch2.getRecord(0).getFieldString("STACK_LAYER_GP"),"0")); //상단 Layer_Gp
																	int iLowerLayerGp = Integer.parseInt(commUtils.nvl(jsSch2.getRecord(1).getFieldString("STACK_LAYER_GP"),"0")); //하단 Layer_Gp
																	commUtils.printLog(logId, "=======:::: 상단 Layer_Gp : " +  iUpperLayerGp , "SL");
																	commUtils.printLog(logId, "=======:::: 하단 Layer_Gp : " +  iLowerLayerGp , "SL");
																	
																	//동일BED 체크
																	if(dUpperBedGp.equals(dLowerBedGp)) {
																		
																		if(iUpperLayerGp == iLowerLayerGp + 1) {
																			iMaxRec = 2;
																		} else {
																			iMaxRec = 1;
																			commUtils.printLog(logId, "=======>>>> 상단,하단 단이 연속이 아님  iMaxRec = " +  iMaxRec , "SL");
																		}
																		
																	} else {
																		iMaxRec = 1;
																		commUtils.printLog(logId, "=======>>>> 상단,하단 위치(열,BED)가 다름  iMaxRec = " +  iMaxRec , "SL");
																	}
																}
																
																if(iMaxRec == 2) {
																	//상위 조건이 충족 되더라도 변형재열재(SLABCOMM.REHEAT_SLAB_GP 가 '2')는  1매 작업
																	String sUpperReheatSlabGp = commUtils.nvl(jsSch2.getRecord(0).getFieldString("REHEAT_SLAB_GP"),""); //상단 재열재구분
																	String dLowerReheatSlabGp = commUtils.nvl(jsSch2.getRecord(1).getFieldString("REHEAT_SLAB_GP"),""); //하단 재열재구분
																	commUtils.printLog(logId, "=======:::: 상단 재열재구분 : " +  sUpperReheatSlabGp , "SL");
																	commUtils.printLog(logId, "=======:::: 하단 재열재구분 : " +  dLowerReheatSlabGp , "SL");
																	
																	if("2".equals(sUpperReheatSlabGp)) {
																		iMaxRec = 1;
																		commUtils.printLog(logId, "=======>>>> sUpperReheatSlabGp == '2' 에 의해 iMaxRec = " +  iMaxRec , "SL");
																	}
																	if("2".equals(dLowerReheatSlabGp)) {
																		iMaxRec = 1;
																		commUtils.printLog(logId, "=======>>>> dLowerReheatSlabGp == '2' 에 의해 iMaxRec = " +  iMaxRec , "SL");
																	}
																}
																
																if(iMaxRec == 2) {
																	//상위 조건이 충족 되더라도 밴딩재(STOCK.YD_RULE_PL_RS_GP 가 'Y')는  1매 작업
																	String sUpperYdRulePlRsGp = commUtils.nvl(jsSch2.getRecord(0).getFieldString("YD_RULE_PL_RS_GP"),""); //상단 Bending재구분
																	String dLowerYdRulePlRsGp = commUtils.nvl(jsSch2.getRecord(1).getFieldString("YD_RULE_PL_RS_GP"),""); //하단 Bending재구분
																	commUtils.printLog(logId, "=======:::: 상단 Bending재구분 : " +  sUpperYdRulePlRsGp , "SL");
																	commUtils.printLog(logId, "=======:::: 하단 Bending재구분 : " +  dLowerYdRulePlRsGp , "SL");
																	
																	if("Y".equals(sUpperYdRulePlRsGp)) {
																		iMaxRec = 1;
																		commUtils.printLog(logId, "=======>>>> sUpperYdRulePlRsGp == 'Y' 에 의해 iMaxRec = " +  iMaxRec , "SL");
																	}
																	if("Y".equals(dLowerYdRulePlRsGp)) {
																		iMaxRec = 1;
																		commUtils.printLog(logId, "=======>>>> dLowerYdRulePlRsGp == 'Y' 에 의해 iMaxRec = " +  iMaxRec , "SL");
																	}
																}
															}
															
															
														} else {
															iMaxRec = 1;
														}
														commUtils.printLog(logId, "=======>>>> WB장입 작업 매수 : " +  iMaxRec , "SL");
														
														
														if(iMaxRec == 1) {
															//다음 장입재가 1 매작업이고
															
															int iChargeLotNo1 = Integer.parseInt(commUtils.nvl(sCHARGE_LOT_NO,"0"));
															int iChargeLotNo2 = Integer.parseInt(commUtils.nvl(jsSch2.getRecord(0).getFieldString("CHARGE_LOT_NO"),"0"));
															
															commUtils.printLog(logId, "=======:::: 크레인 스케줄 장입번호 : " +  iChargeLotNo1 , "SL");
															commUtils.printLog(logId, "=======:::: 다음 장입재 장입번호 : " +  iChargeLotNo2 , "SL");
															
															if(iChargeLotNo1 == iChargeLotNo2) {
																//장입순번이 다음 장입재와 같고
																
																String sYD_UP_WO_LOC2 = jsSch2.getRecord(0).getFieldString("STACK_COL_GP") + jsSch2.getRecord(0).getFieldString("STACK_BED_GP"); //권상위치
																
																commUtils.printLog(logId, "=======:::: 다음 장입재 권상위치 : " +  sYD_UP_WO_LOC2 , "SL");
																
																if("0203".equals(sYD_UP_WO_LOC2.substring(4,8)) || "0204".equals(sYD_UP_WO_LOC2.substring(4,8)) || "0205".equals(sYD_UP_WO_LOC2.substring(4,8)) 
											   		    		|| "0206".equals(sYD_UP_WO_LOC2.substring(4,8)) || "0207".equals(sYD_UP_WO_LOC2.substring(4,8)) || "0208".equals(sYD_UP_WO_LOC2.substring(4,8))) {
																	//다음 장입재 권상위치가 C동 02스판 03열 이상이면 
																	
																	//크레인 스케줄 REGISTER 를 'L' 로 설정한다. 
																	jrParam2.setField("REGISTER"		, "L");
																	jrParam2.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
																	
																	if(commDao.update(jrParam2, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCrnSchWbAdv", logId, methodNm, "크레인스케줄 변경 - WB LOCK 설정 표시") > 0) { 
																	
																		jrParam.setField("LAST_WORK_ORD_GP", "L"); //WB Lock 설정
																
																		sYMYMJ204 = bSlabComm.makeYMYMJ204("WB_ADV", "WB장입 2매 2회작업", "L", ydCrnSchId , logId);
																		jrRtn = commUtils.addSndData(jrRtn, sYMYMJ204);
																	}
																}
															}
														}
													}
												}
											}
										} 
									}
								} //if("2".equals(sWB01_SH)) { 
							}
	        			}
	        			
// SLAB 고도화 - END			
///////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	        		}	        		
	        		
					//크레인작업지시(YMA8L004) 전문 생성
	        		JDTORecordSet jsRtn1 = commDao.getMsgL2("YMA8L004", jrParam);
					jrRtn = commUtils.addSndData(jrRtn , jsRtn1);
					
					//크레인작업지시 Z값 갱신 처리
	        		if(jsRtn1.size() > 0 ) {
	        		//	this.procCrnsSchZaxis(jrParam);
	        			jrParam.setField("YD_UP_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_UP_WO_LOC_ZAXIS"));	
	        			jrParam.setField("YD_DN_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_DN_WO_LOC_ZAXIS"));	
	        			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS 
	    				--크레인스케줄 z값 갱신
	    				UPDATE TB_YM_CRNSCH
	    				   SET YD_UP_WO_LOC_ZAXIS = :V_YD_UP_WO_LOC_ZAXIS
	    				     , YD_DN_WO_LOC_ZAXIS = :V_YD_DN_WO_LOC_ZAXIS
	    				 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
	    				   AND DEL_YN           = 'N' 
	    				*/
	            		commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS", logId, methodNm, "SLAB크레인스케줄 Z값 수정");
	        			        			
	        		}
	        		
					commUtils.printLog(logId, "B열연SLAB크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
				}	
				
			} else {
				/**********************************************************
				* 2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
				*    2.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 Error 처리
				*    2.2 권하완료[4] 이면 스케줄을 생성
				*    2.3 명령선택대기[W] 이면 응답 전문을 전송 -> 2.2로 통합
				**********************************************************/
				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.2.1 재지시요구 시
					**********************************************************/
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , "크레인[" + ydEqpId + "-" + ydWrkProgStat + "] 작업지시 없음"); //야드L3MESSAGE
					
					jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));

					commUtils.printLog(logId, "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + ydEqpId + " : " + ydWrkProgStat + " - " + ydCrnSchId + " ]", "SL");
				} else {
					/**********************************************************
					* 2.2.2 대기상태[W], 권하완료[4] 지시요구
					**********************************************************/
					//크레인작업지시가 없으면 설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "W"); //대기(Wait)

	        		commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatEqp", logId, methodNm, "B열연SLAB설비상태 수정");
	        		

	        		JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

	    			//작업예약 조회
					 /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWbIdA8YML007 
					SELECT *
					  FROM (
					        SELECT A.YD_WBOOK_ID
					             , A.YD_SCH_CD
					             , A.YD_SCH_CD_CNT
					             , A.YD_SCH_CD_YN
					             , A.YD_WRK_PLAN_TCAR
					             , A.YD_WRK_PLAN_CRN
					          FROM (
					                SELECT YD_WBOOK_ID
					                     , YD_SCH_CD
					                     , SUM(1) OVER(PARTITION BY YD_SCH_CD) AS YD_SCH_CD_CNT 
					                     , 'Y'  AS YD_SCH_CD_YN 
					                     , YD_WRK_PLAN_TCAR
					                     , YD_WRK_PLAN_CRN
					                     , SCH_CNCL_YN
					                     , (SELECT YD_SCH_AUTO_ST_YN 
					                          FROM TB_YM_SCHEDULERULE 
					                         WHERE YD_SCH_CD = WB.YD_SCH_CD 
					                           AND DEL_YN = 'N') AS YD_SCH_AUTO_ST_YN --스케줄별 자동기동여부 DEFAULT Y
					                  FROM TB_YM_WRKBOOK WB
					                 WHERE DEL_YN = 'N'
					                   --AND YD_WRK_PLAN_TCAR IS NULL
					                   --대차상차 인 경우 설비TABLE 적재가능 매수 보다 크거나 같으면 선택불가
					                   --대차 스케줄 과 목적동이 틀리면 선택불가 
					                   AND 1 =  CASE WHEN SUBSTR(YD_SCH_CD,3,2) = 'TC' AND SUBSTR(WB.YD_SCH_CD,7,1) = 'U' THEN 
					                                 CASE WHEN (SELECT COUNT(*) 
					                                              FROM TB_YM_STACKLAYER 
					                                             WHERE STACK_COL_GP = SUBSTR(WB.YD_SCH_CD,1,2)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
					                                               AND STOCK_ID IS NOT NULL) >= (SELECT STACK_MAX_QNTY FROM TB_YM_EQUIP WHERE EQUIP_GP = WB.YD_WRK_PLAN_TCAR )
					                                      THEN '2'
					                                      WHEN (SELECT NVL(SUBSTR(YD_CARUD_STOP_LOC,2,1),  WB.YD_AIM_BAY_GP )
					                                              FROM TB_YM_TCARSCH 
					                                             WHERE DEL_YN = 'N'
					                                               AND YD_EQP_ID = WB.YD_WRK_PLAN_TCAR) <> WB.YD_AIM_BAY_GP 
					                                      THEN '2'    
					                                      WHEN (SELECT SUBSTR(CURR_STOP_LOC,2,1)
					                                              FROM TB_YM_EQUIP 
					                                             WHERE DEL_YN = 'N'
					                                               AND EQUIP_GP = WB.YD_WRK_PLAN_TCAR) <> SUBSTR(WB.YD_SCH_CD,2,1)
					                                      THEN '2'
					                                      -- 대차 출발시 문제발생     
					                                      WHEN (SELECT COUNT(*) 
					                                              FROM TB_YM_STACKER 
					                                             WHERE STACK_COL_GP = SUBSTR(WB.YD_SCH_CD,1,2)||SUBSTR(WB.YD_WRK_PLAN_TCAR,3,4)
					                                               AND STACK_BED_ACTIVE_STAT NOT IN ('L')) > 0   
					                                      THEN '2'
					                                      -- 대차 상차 로직
					                                      ELSE '1' END
					                                 ELSE '1' END    
					                   -- 대차에 관련된 스케쥴 기동시 동간작업기준과 틀리면 기동처리 안함                 
					                   AND 1 =  CASE WHEN (SELECT COUNT(*) 
					                                         FROM TB_YM_WRKBOOK WB1
					                                            , (SELECT EQUIP_GP
					                                                    , SUBSTR(CARLOAD_STOP_LOC,1,2)  ||SUBSTR(CARLD_SCH_CD,3,6) AS UP_SCH
					                                                    , SUBSTR(CARUNLOAD_STOP_LOC,1,2)||SUBSTR(CARUD_SCH_CD,3,6) AS DN_SCH
					                                                 FROM TB_YM_EQUIP WHERE EQUIP_GP LIKE '2XTC0%')  EQ1 
					                                            , (SELECT *
					                                                 FROM USRYMA.TB_YM_RULE 
					                                                WHERE REPR_CD_GP = 'YM2007'
					                                                  AND CD_GP = '2'
					                                                  AND DEL_YN = 'N') RULE
					                                        WHERE WB1.YD_WBOOK_ID = WB.YD_WBOOK_ID
					                                          AND WB1.DEL_YN = 'N'
					                                          AND WB1.YD_WRK_PLAN_TCAR = EQ1.EQUIP_GP
					                                          AND SUBSTR(WB1.YD_SCH_CD,3,6) = SUBSTR(DTL_ITM1(+),3,6) 
					                                          AND 'Y' = CASE WHEN YD_WRK_PLAN_TCAR IS NOT NULL AND SUBSTR(ITEM,1,1) = 'L' AND YD_SCH_CD != UP_SCH THEN  'Y'
					                                                         WHEN YD_WRK_PLAN_TCAR IS NOT NULL AND SUBSTR(ITEM,1,1) = 'U' AND YD_SCH_CD != DN_SCH THEN  'Y'
					                                                         ELSE 'N' END  ) > 0
					                                 THEN '2'                                                      
					                                 ELSE '1' END     
					                                  
					                   AND TRN_EQP_CD       IS NULL
					                   AND YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
					                                             FROM TB_YM_CRNSCH
					                                            WHERE DEL_YN = 'N')
					                   AND (YD_WRK_PLAN_CRN = :V_YD_EQP_ID 
					                         OR YD_SCH_CD IN
					                            (SELECT YD_SCH_CD
					                               FROM TB_YM_SCHEDULERULE
					                              WHERE (YD_WRK_CRN = :V_YD_EQP_ID )
					                                AND DEL_YN          = 'N')
					                       )
					                   AND NVL(YD_WRK_PLAN_CRN, :V_YD_EQP_ID ) = :V_YD_EQP_ID
					                  ORDER BY (CASE WHEN YD_WRK_PLAN_CRN=:V_YD_EQP_ID  THEN 1 ELSE 2 END ),YD_SCH_PRIOR, YD_WBOOK_ID     
					               ) A
					         WHERE 1=1
					           AND YD_SCH_CD_YN = 'Y'        
					           AND YD_SCH_AUTO_ST_YN = 'Y'
					       )
					   WHERE ROWNUM = 1
					*/
	        		JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWbIdA8YML007", logId, methodNm, "B열연SLAB작업예약 조회");

					//작업예약이 있으면 크레인스케줄호출
					if (jsWrkBook.size() > 0) {
						ydL3Msg = "크레인스케줄 호출";

						jrYdMsg.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"    , jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
						jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
						jrYdMsg.setField("MODIFIER"   , modifier); //수정자
							
						// 크레인스케줄 기동
						jrRtn = ymComm.getCrnSchMsg(jrYdMsg);
					} else {
						ydL3Msg = "다음 크레인작업지시 없음";
					}
					
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
					resMsg.setField("YD_CRN_SCH_ID" , ""     ); 
					
					jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
					
					commUtils.printLog(logId, "B열연SLAB크레인작업지시요구(다음지시) " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			try {
				//PIDEV_F : 정상SET후  ERROR 발생한 경우							
				if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {							
					resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)						
					resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)						
				}							

				//크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "YmCommEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { bSlabComm.getYMA8L005(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of B열연SLAB크레인작업지시요구 - C2 고도화 (A8YML007C2Adv)
	 
	
	
	//2018년 2월 8일 목요일 - 크레인주행금지구간 전문(A8YML030)
	/**
	 *      [A] 오퍼레이션명 : 크레인주행금지구간(A8YML030)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
    public JDTORecord rcvA8YML030(JDTORecord rcvMsg) throws DAOException {
    	String methodNm = "A8크레인주행금지구간[BSlabL2RcvSeEJB.rcvA8YML030] < " + rcvMsg.getResultMsg();
    	String logId = rcvMsg.getResultCode();
    	JDTORecord resMsg 	= JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn 		= true;
    	
    	try{
    		commUtils.printLog(logId, methodNm, "S+");
			
			//JDTORecord jrRtn = null;
    		
    		//Data수신 항목 값
			String msgId      		   = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String StartEndGp		   = commUtils.trim(rcvMsg.getFieldString("START_END_GP")); //시작종료구분
			String ydGp 			   = commUtils.trim(rcvMsg.getFieldString("YD_GP"				   )); //야드구분
			String bayGp 			   = commUtils.trim(rcvMsg.getFieldString("BAY_GP"                 )); //야드동구분
			String TravlProhFromloc    = commUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_FROMLOC"     )); //야드주행금지FROM위치
			String TravlProhToloc 	   = commUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_TOLOC"       )); //야드주행금지TO위치
			String TravlProhFromxaxis  = commUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_FROMXAXIS"   )); //야드주행금지FROM위치X축
			String TravlProhToxaxis    = commUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_TOXAXIS"     )); //야드주행금지TO위치X축
			
			//String ymCraneTravlProhSeq = commUtils.trim(rcvMsg.getFieldString("YM_CRANE_TRAVL_PROH_SEQ"				   )); 
			//String delyn 			   = commUtils.trim(rcvMsg.getFieldString("DEL_YN"				   )); //삭제처리유무
			//String register 		   = commUtils.trim(rcvMsg.getFieldString("REGISTER"			   )); //등록자
			String modifier 		   = commUtils.trim(rcvMsg.getFieldString("MODIFIER"               )); //수정자
			if ("".equals(modifier)) { modifier = msgId; }
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			JDTORecord jrRtn = null;	//전문 Return
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if("".equals(StartEndGp)){
				throw new Exception("시작종료구분(START_END_GP) 없음");
			}else if("".equals(ydGp)){
				throw new Exception("야드구분(YD_GP) 없음");
			}else if("".equals(bayGp)){
				throw new Exception("야드동구분(BAY_GP) 없음");
			}else if("".equals(TravlProhFromloc)){
				throw new Exception("야드주행금지FROM위치(TRAVL_PROH_FROMLOC) 없음");
			}else if("".equals(TravlProhToloc)){
				throw new Exception("야드주행금지TO위치(TRAVL_PROH_TOLOC) 없음");
			}else if("".equals(TravlProhFromxaxis)){
				throw new Exception("야드주행금지FROM위치X축(TRAVL_PROH_FROMXAXIS) 없음");
			}else if("".equals(TravlProhToxaxis)){
				throw new Exception("야드주행금지TO위치X축(TRAVL_PROH_TOXAXIS) 없음");
			}
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			/**********************************************************
			* 2. 크레인주행금지구간(A8YML030) SEQ SELECT
			**********************************************************/
			jrParam.setField("YD_GP", ydGp                             );
			jrParam.setField("BAY_GP", bayGp                           );
			jrParam.setField("TRAVL_PROH_FROMLOC", TravlProhFromloc    );
			jrParam.setField("TRAVL_PROH_TOLOC", TravlProhToloc        );
			
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getbSlabCRANETRAVLPROHList
			  SELECT YM_CRANE_TRAVL_PROH_SEQ 
			    FROM TB_YM_CRANE_TRAVL_PROH
			   WHERE DEL_YN = 'N'
			     AND YD_GP = :V_YD_GP
			     AND BAY_GP =  :V_BAY_GP
			     AND TRAVL_PROH_FROMLOC =  :V_TRAVL_PROH_FROMLOC
			     AND TRAVL_PROH_TOLOC =  :V_TRAVL_PROH_TOLOC */
			
			JDTORecordSet jrList = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getbSlabCRANETRAVLPROHList", logId, methodNm, "크레인주행금지구간 조회");	
			
			String sSeq = "";
			
			if(jrList.size() > 0){
				sSeq = jrList.getRecord(0).getFieldString("YM_CRANE_TRAVL_PROH_SEQ");
			}
			
			jrParam.setField("YM_CRANE_TRAVL_PROH_SEQ", sSeq    );
			
			jrParam.setField("TRAVL_PROH_FROMXAXIS", TravlProhFromxaxis);
			jrParam.setField("TRAVL_PROH_TOXAXIS", TravlProhToxaxis    );
			
			if("S".equals(StartEndGp)){
				jrParam.setField("DEL_YN", "N"    );
			}else if("E".equals(StartEndGp)){
				jrParam.setField("DEL_YN", "Y"    );
			}
			jrParam.setField("REGISTER", "A8YML030");
			jrParam.setField("MODIFIER", "A8YML030");
				
			/*com.inisteel.cim.ym.bslab.dao.BSlabDAO.updbSlabCRANETRAVLPROH
				MERGE INTO TB_YM_CRANE_TRAVL_PROH USING DUAL ON ( 
				            YM_CRANE_TRAVL_PROH_SEQ = :V_YM_CRANE_TRAVL_PROH_SEQ
				        AND YD_GP = :V_YD_GP
				        AND BAY_GP  = :V_BAY_GP
					    AND TRAVL_PROH_FROMXAXIS  = :V_TRAVL_PROH_FROMXAXIS
						AND TRAVL_PROH_TOXAXIS = :V_TRAVL_PROH_TOXAXIS)
					  WHEN MATCHED THEN
				    UPDATE SET DEL_YN = :V_DEL_YN,
				               MODIFIER = :V_MODIFIER,
					           MOD_DDTT = SYSDATE                 
					  WHEN NOT MATCHED THEN
					INSERT(YM_CRANE_TRAVL_PROH_SEQ,
					       YD_GP,
					       BAY_GP,
					       TRAVL_PROH_FROMLOC,
					       TRAVL_PROH_TOLOC,
					       TRAVL_PROH_FROMXAXIS,
					       TRAVL_PROH_TOXAXIS,
					       DEL_YN,
					       REGISTER,
					       REG_DDTT,
				           MODIFIER,
				           MOD_DDTT)
					VALUES(USRYMA.YM_CRANE_TRAVL_PROH_SEQ.NEXTVAL,
						   :V_YD_GP,
						   :V_BAY_GP,
						   :V_TRAVL_PROH_FROMLOC,
						   :V_TRAVL_PROH_TOLOC,
						   :V_TRAVL_PROH_FROMXAXIS,
						   :V_TRAVL_PROH_TOXAXIS,
						   :V_DEL_YN,
						   :V_REGISTER,
						   SYSDATE)  */
				
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updbSlabCRANETRAVLPROH", logId, methodNm, "크레인주행금지구간 수정 및 등록");

			/**********************************************************
			* 3. 크레인작업실적응답 전문 전송(YMA8L005)
			**********************************************************/
			commUtils.printLog(logId,"확인:"+resYn, "SL");
			if (resYn) {
				commUtils.printLog(logId,"확인:"+resYn, "SL");
				resMsg.setResultCode(logId);				//Log ID
				resMsg.setField("YD_L3_HD_RS_CD", "0000");	//야드L3처리결과코드(정상)
				resMsg.setField("BAY_GP", bayGp);
				
				resMsg.setField("A", "2ACRA1"); //A동 대표크레인
				resMsg.setField("B", "2BCRB1"); //B동 대표크레인
				resMsg.setField("C", "2CCRC1"); //C동 대표크레인
				resMsg.setField("D", "2DCRD1"); //D동 대표크레인
				resMsg.setField("E", "2ECRE1"); //E동 대표크레인
				
				jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005_recv(resMsg));
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					resMsg.setResultCode(logId);
					//resMsg.setField("YD_L3_HD_RS_CD", "9999"); //야드L3처리결과코드(오류)
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YmCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] {bSlabComm.getYMA8L005_recv(resMsg)});
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    }//End rcvA8YML030  
	
	/**
	 *      [A] 오퍼레이션명 : 슬라브 야드 크레인 위치정보(A8YML031)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvA8YML031(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "슬라브 야드 크레인 위치정보[BSlabL2RcvSeEJB.rcvA8YML031] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = null;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			for(int Loop_i = 1; Loop_i <= 24; Loop_i++) {
				
				String ydEqpId = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"+Loop_i ));
				if("".equals(ydEqpId)){
					break;
				}
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name

				jrYdMsg.setField("MODIFIER"     		, modifier);					//수정자
				jrYdMsg.setField("YD_EQP_ID"     		, ydEqpId );  //YD_EQP_ID 
				jrYdMsg.setField("CRN_WRK_PROC_STAT"    , commUtils.trim(rcvMsg.getFieldString("CRN_WRK_PROC_STAT"+Loop_i )) );  //CRN_WRK_PROC_STAT
				jrYdMsg.setField("CURR_XAXIS"     		, commUtils.trim(rcvMsg.getFieldString("CURR_XAXIS"+Loop_i )) );  //CURR_XAXIS
				jrYdMsg.setField("FROM_XAXIS"     		, commUtils.trim(rcvMsg.getFieldString("FROM_XAXIS"+Loop_i )) );  //FROM_XAXIS 
				jrYdMsg.setField("TO_XAXIS"     		, commUtils.trim(rcvMsg.getFieldString("TO_XAXIS"+Loop_i )) );  //TO_XAXIS
				
				/*1열연 야드 크레인 위치정보 등록*/
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmCrnLoc
				MERGE INTO USRYMA.TB_YM_CRNLOC CRN USING (
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
				    ,CRN.MOD_DDTT           = DD.MOD_DDTT
				    ,CRN.CRN_WRK_PROC_STAT  = DD.CRN_WRK_PROC_STAT
				    ,CRN.CURR_XAXIS         = DD.CURR_XAXIS
				    ,CRN.FROM_XAXIS         = DECODE(NVL(DD.FROM_XAXIS,'00000000'),'00000000',CRN.CURR_XAXIS,DD.FROM_XAXIS)
				    ,CRN.TO_XAXIS           = DD.TO_XAXIS
				 */
				commDao.update(jrYdMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmCrnLoc", logId, methodNm, "슬라브 야드 크레인 위치 정보 등록");
				
				
				if("2ECRE3".equals(ydEqpId)) {
					//E3 가 2E0116 열을 지나 갈때 TURN 작업 자동 해제
					
					jrYdMsg.setField("YD_EQP_ID"     		, ydEqpId );  //YD_EQP_ID 
					
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmCrnLoc 
					SELECT YD_GP
					      ,YD_BAY_GP
					      ,YD_EQP_ID
					      ,CRN_WRK_PROC_STAT
					      ,CURR_XAXIS
					      ,FROM_XAXIS
					      ,TO_XAXIS
					      ,CASE WHEN CURR_XAXIS > FROM_XAXIS THEN '+'
					            WHEN CURR_XAXIS < FROM_XAXIS THEN '-'
					            ELSE '=' END AS CRN_DIR
					      ,(SELECT EQUIP_STAT FROM TB_YM_EQUIP WHERE EQUIP_GP = YD_EQP_ID) AS EQUIP_STAT
					FROM   TB_YM_CRNLOC
					WHERE  YD_EQP_ID LIKE :V_YD_EQP_ID || '%'
					*/
					JDTORecordSet jrList = commDao.select(jrYdMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmCrnLoc", logId, methodNm, "크레인 위치 정보 조회");	
					
					String sCRN_DIR = "";
					String sEQUIP_STAT = "";
					int iCURR_XAXIS = 0;
					int iSTACK_LAYER_X_AXIS = 0;
					
					if(jrList.size() > 0){
						sCRN_DIR = jrList.getRecord(0).getFieldString("CRN_DIR");
						sEQUIP_STAT = jrList.getRecord(0).getFieldString("EQUIP_STAT");
						iCURR_XAXIS = jrList.getRecord(0).getFieldInt("CURR_XAXIS");
						
						if("T".equals(sEQUIP_STAT)) {
							//핸드스카핑장 뒤집기 작업 (TURN작업) 일 경우
							
							jrYdMsg.setField("STACK_COL_GP" , "2E0116" );  
							
							JDTORecordSet jrList2 = commDao.select(jrYdMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.bslabYdNEqpXyaxisSet.getSlabYmStackBed", logId, methodNm, "특정 열 좌표 정보 조회");
							
							if(jrList2.size() > 0){
							
								iSTACK_LAYER_X_AXIS = jrList2.getRecord(0).getFieldInt("STACK_LAYER_X_AXIS");
								
								commUtils.printLog(logId,"[[TURN 작업 시작 후 핸드스카핑장 진입여부  좌표 확인]]  iCURR_XAXIS : "+iCURR_XAXIS + " , " + iSTACK_LAYER_X_AXIS , "SL");
							
								if(iCURR_XAXIS < iSTACK_LAYER_X_AXIS ) {
									//핸드스카핑장 영역으로 들어온 경우
								
								
									//TB_YM_EQUIP 테이블 EQUIP_STAT 에 'U' UPDATE
									jrYdMsg.setField("EQUIP_STAT"		, "U");
									jrYdMsg.setField("YD_EQP_ID"		, ydEqpId);
									
									commDao.update(jrYdMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updEquipStat", logId, methodNm, "TB_YM_EQUIP 수정");
								
								}
							}
							
						} else if("U".equals(sEQUIP_STAT)) {
							//T -> U 로 된 경우
							if("+".equals(sCRN_DIR)) {
								//01스판 쪽에서 02 스판쪽 (상차포인트쪽 방향) 방향으로 이동 중일 경우

								jrYdMsg.setField("STACK_COL_GP" , "2E0116" );  
								
								JDTORecordSet jrList2 = commDao.select(jrYdMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.bslabYdNEqpXyaxisSet.getSlabYmStackBed", logId, methodNm, "특정 열 좌표 정보 조회");
								
								if(jrList2.size() > 0){
								
									iSTACK_LAYER_X_AXIS = jrList2.getRecord(0).getFieldInt("STACK_LAYER_X_AXIS");
									
									commUtils.printLog(logId,"[[좌표 확인]]  iCURR_XAXIS : "+iCURR_XAXIS + " , " + iSTACK_LAYER_X_AXIS , "SL");
									
									if(iCURR_XAXIS >= iSTACK_LAYER_X_AXIS ) {
										
										JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
										sndL2Msg.setResultCode(logId);	//Log ID
										sndL2Msg.setResultMsg(methodNm);	//Log Method Name
										sndL2Msg.setField("YD_EQP_ID"		, ydEqpId  ); 
										sndL2Msg.setField("MV_GP"			, "R"      ); 
										sndL2Msg.setField("YD_WO_LOC_XAXIS" , "");
										sndL2Msg.setField("YD_WO_LOC_YAXIS" , "");				
							 
										jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L010", sndL2Msg));	 //전송 Data 생성
										
										//TB_YM_EQUIP 테이블 EQUIP_STAT 에 'R' UPDATE
										jrYdMsg.setField("EQUIP_STAT"		, "R");
										jrYdMsg.setField("YD_EQP_ID"		, ydEqpId);
										
										commDao.update(jrYdMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updEquipStat", logId, methodNm, "TB_YM_EQUIP 수정");
										
									}
								}
							}
						}
					}
				}
			}
			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
}	
