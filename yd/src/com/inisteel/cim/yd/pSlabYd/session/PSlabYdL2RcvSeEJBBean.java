/**
 * @(#)PSlabYdL2RcvSeEJBBean
 *
 * @version          V1.00
 * @author           염용선
 * @date             2020/05/06
 *
 * @description      Slab야드 L2수신 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2020/05/06   염용선      염용선      최초 등록
 */
package com.inisteel.cim.yd.pSlabYd.session;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.StringUtils;
import com.inisteel.cim.yd.pSlabYd.dao.PSlabYdCommDAO;
import com.inisteel.cim.yd.pSlabCommon.util.PSlabYdConstant;
import com.inisteel.cim.yd.pSlabCommon.util.PSlabYdUtils;
import com.inisteel.cim.yd.pSlabYd.session.PSlabYdComm;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
/**
 *      [A] 클래스명 : Slab야드 L2수신 처리
 *
 * @ejb.bean name="PSlabYdL2RcvSeEJB" jndi-name="PSlabYdL2RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class PSlabYdL2RcvSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private PSlabYdUtils  slabUtils = new PSlabYdUtils();
	private PSlabYdComm    slabComm = new PSlabYdComm();
	private PSlabYdCommDAO  commDao = new PSlabYdCommDAO();
	
	//PIDEV		
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();	
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	


	/***************************************************************************
	 * C연주Slab야드L2(Y1), 후판Slab야드L2(Y3), 항만야드L2(E7)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 저장위치제원요구(Y3YDL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY3YDL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "저장위치제원요구[PSlabYdL2RcvSeEJB.rcvY3YDL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = slabUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = slabUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = slabUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); //야드동구분
			String ydEqpGp      = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); //야드설비구분
			String ydStkColNo   = slabUtils.trim(rcvMsg.getFieldString("YD_STK_COL_NO"  )); //야드적치열번호
			String ydStkBedNo   = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"  )); //야드적치Bed번호
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"D".equals(ydGp)) {
				slabUtils.printLog(logId, ydGp+" >> 야드구분(후판슬라브야드) 관계없음", "SL");
				throw new Exception(ydGp+" >> 야드구분(후판슬라브야드) 관계없음");
			} else if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
				slabUtils.printLog(logId, "야드동구분(YD_BAY_GP) 없음", "SL");
				throw new Exception("야드동구분(YD_BAY_GP) 없음");
			} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
				slabUtils.printLog(logId, "야드설비구분(YD_EQP_GP) 없음", "SL");
				throw new Exception("야드설비구분(YD_EQP_GP) 없음");
			}

			/**********************************************************
			* 2. 저장위치제원( YDY3L001) 전문 조회
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD", ydInfoSyncCd                         ); //야드정보동기화코드
			jrParam.setField("YD_STK_COL_GP"  , ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO"  , ydStkBedNo                           ); //야드적치Bed번호
			jrParam.setField("YD_EQP_WRK_STAT_GUBUN" , "");
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY3L001", jrParam));
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	


	/**
	 *      [A] 오퍼레이션명 : 저장품제원요구(Y3YDL002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY3YDL002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "저장품제원요구[PSlabYdL2RcvSeEJB.rcvY3YDL002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = slabUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = slabUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = slabUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); //야드동구분
			String ydEqpGp      = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); //야드설비구분
			String ydStkColNo   = slabUtils.trim(rcvMsg.getFieldString("YD_STK_COL_NO"  )); //야드적치열번호
			String ydStkBedNo   = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"  )); //야드적치Bed번호
			String stlNo        = slabUtils.trim(rcvMsg.getFieldString("STL_NO"         )); //재료번호
			methodNm = msgId.substring(0, 2) + methodNm;

			slabUtils.printLog(logId, "msgId="+msgId, "SL");
			slabUtils.printLog(logId, "ydInfoSyncCd="+ydInfoSyncCd, "SL");
			slabUtils.printLog(logId, "ydGp="+ydGp, "SL");
			slabUtils.printLog(logId, "ydBayGp="+ydBayGp, "SL");
			slabUtils.printLog(logId, "ydEqpGp="+ydEqpGp, "SL");
			slabUtils.printLog(logId, "ydStkColNo="+ydStkColNo, "SL");
			slabUtils.printLog(logId, "ydStkBedNo="+ydStkBedNo, "SL");
			slabUtils.printLog(logId, "stlNo="+stlNo, "SL");
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydGp)) {
				slabUtils.printLog(logId, "야드구분(YD_GP) 없음", "SL");
				throw new Exception("야드구분(YD_GP) 없음");
			}
//			if ("".equals(ydEqpGp)) {	// [5]지정저장품 으로 올 때 스판구분 안들어옴 > 주석처리
//				slabUtils.printLog(logId, "야드 스판 구분(ydEqpGp) 없음", "SL");
//				throw new Exception("야드 스판 구분(ydEqpGp) 없음");
//			}
			if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
				//저장위치별
				if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
					slabUtils.printLog(logId, "야드동구분(YD_BAY_GP) 없음", "SL");
					throw new Exception("야드동구분(YD_BAY_GP) 없음");
				} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
					slabUtils.printLog(logId, "야드설비구분(YD_EQP_GP) 없음", "SL");
					throw new Exception("야드설비구분(YD_EQP_GP) 없음");
				}
			} else {
				//재료별
				if ("".equals(stlNo)) {
					slabUtils.printLog(logId, "재료번호(STL_NO) 없음", "SL");
					throw new Exception("재료번호(STL_NO) 없음");
				}
			}

			/**********************************************************
			* 2. 저장품제원(YDY3L002) 전문 조회
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD", ydInfoSyncCd                         ); //야드정보동기화코드
			jrParam.setField("YD_STK_COL_GP"  , ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO"  , ydStkBedNo                           ); //야드적치Bed번호
			jrParam.setField("YD_GP"          , ydGp                                 ); //야드구분
			jrParam.setField("STL_NO"         , stlNo                                ); //재료번호

			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY3L002", jrParam));
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	


	/**
	 *      [A] 오퍼레이션명 : Crane Reschedule 처리 (Y1YDL003, Y3YDL003, Y1YDL004, Y3YDL004)
	 *      염용선 2020-06-26
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnResch(JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인리스케줄[PSlabYdL2RcvSeEJB.trtCrnResch] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String ydUserId   = slabUtils.trim(jrParam.getFieldString("MODIFIER"        )); //수정자(Backup Only)
		//JDTORecord resMsg = slabUtils.getParam(logId, methodNm, ydUserId); //크레인작업실적응답 전문 생성용
        
		try {
			slabUtils.printLog(logId, methodNm, "SL+");
			//jrParam.setResultMsg(methodNm);	//Log Method Name

			JDTORecord jrRtn = slabUtils.getParam(logId, methodNm, ydUserId);;	//크레인작업지시 전문 Return

			//작업예약 야드스케쥴우선순위 수정
			/*
			 * 크레인리스케줄 작업예약 수정 
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
				    ,WB.YD_SCH_PRIOR = DD.YD_SCH_PRIOR

			 */
			 commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updCrnReschWrkBook", logId, methodNm, "작업예약(TB_YD_WRKBOOK) 수정");				
				
			//크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			/*
			 * 크레인리스케줄 크레인스케줄 수정 
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
				) DD ON (CS.YD_SCH_CD = DD.YD_SCH_CD AND CS.YD_WRK_PROG_STAT = 'W' AND CS.DEL_YN = 'N')
				WHEN MATCHED THEN UPDATE SET
					 CS.MODIFIER     = DD.MODIFIER
				    ,CS.MOD_DDTT     = SYSDATE
				    ,CS.YD_SCH_PRIOR = DD.YD_SCH_PRIOR
				    ,CS.YD_EQP_ID    = DECODE(DD.YD_EQP_ID,NULL,CS.YD_EQP_ID,DD.YD_EQP_ID)
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updCrnReschCrnSch", logId, methodNm, "크레인스케줄(TB_YD_CRNSCH) 수정");				
			
			//크레인작업지시 대상 설비 조회
			/*
			 * 크레인리스케줄 작업지시설비 조회
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
				 WHERE EQ.YD_EQP_ID       = SR.YD_EQP_ID
				   AND EQ.DEL_YN          = 'N'
				   AND EQ.YD_EQP_STAT     = 'W'
				   AND EQ.YD_EQP_WRK_MODE = '1'
			 */
            JDTORecordSet jsWoEqp = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getCrnReschWoEqp", logId, methodNm, "작업지시설비 조회");
    		
			
			int schCnt = jsWoEqp.size();

			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, ydUserId);				
			 
				jrYdMsg.setField("JMS_TC_CD"         , "Y3YDL007"         ); //JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
				
				for (int ii = 0; ii < schCnt; ii++) {
					jrYdMsg.setField("YD_EQP_ID"       , jsWoEqp.getRecord(ii).getFieldString("YD_EQP_ID")); //야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT", "W"                                              ); //야드작업진행상태
					slabUtils.printLog(logId, jsWoEqp.getRecord(ii).getFieldString("YD_EQP_ID"), "SL2=========================================");	
					//크레인작업지시 전문을 추가
					
					JDTORecord jrGetYdMsg = this.rcvY3YDL007(jrYdMsg);
					String rtnCd	 = slabUtils.nvl(jrGetYdMsg.getFieldString("RTN_CD"), "0");
					String rtnMsg	 = slabUtils.nvl(jrGetYdMsg.getFieldString("RTN_MSG"), "");
					slabUtils.printLog(logId,  " =======rtnCd:"+ rtnCd, "FL");
					slabUtils.printLog(logId,  " =======rtnMsg:"+ rtnMsg, "FL");
					// ROLLBACK 시 전문 발생
					if (!"1".equals(rtnCd)) {
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", rtnMsg);
						return jrRtn;
					}
					jrRtn = slabUtils.addSndData(jrRtn,jrGetYdMsg);
				}
			
			slabUtils.printLog(logId, methodNm, "SL-");
			jrRtn.setField("RTN_CD"	, "1");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 설비운전모드전환(Y1YDL003, Y3YDL003, C3YDL009)
	 *      염용선  2020-06-29
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비운전모드전환[PSlabYdL2RcvSeEJB.rcvY3YDL003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		boolean resYn = false;	//크레인작업실적응답 전문 전송여부
		String ydUserId   = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
		JDTORecord resMsg = slabUtils.getParam(logId, methodNm, ydUserId); //크레인작업실적응답 전문 생성용
       
		JDTORecord jrRtn  = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			slabUtils.printLog(logId, methodNm, "SL+");
			
			
			//수신 항목 값
			String msgId        = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId      = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"      )); //야드설비ID
			String ydEqpWrkMode = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE")); //야드설비작업Mode(1:On-Line, 0:Off-Line , 4:일시정지)
			String ydEqpWrkMode2 = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE2")); //야드설비작업Mode("A" : 자동 ,"R":리모컨, "E":정비 , "M":유인)
			String brGp       = ""; //고장복구구분
			String ydCrnSchId = "";
			String ydSchCd    = "";
			if ("".equals(ydUserId)) { ydUserId = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			//크레인작업실적응답 전문 전송여부를 Check 
			if (msgId.startsWith("Y3") ||(ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4)))) {
				resYn = true;
			}

			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setField("YD_EQP_ID"       , ydEqpId     ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydEqpWrkMode); //야드작업진행상태(야드설비작업Mode)
			resMsg.setField("YD_L2_WR_GP"     , "M"         ); //야드L2실적구분(운전모드변경)
			resMsg.setField("YD_L3_HD_RS_CD"  , "EM99"      ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:설비운전모드전환 수신처리"); //야드L3MESSAGE(Error)
			

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "EM01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "EM02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydEqpWrkMode)) {
				ydL3HdRsCd = "EM03";
				ydL3Msg    = "오류:설비작업Mode 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);				
			}

		    if(resYn && "CR".equals(ydEqpId.substring(2, 4))){//L2 에서 송신일때만 체크 2021-06-06 YYS
			    if ("".equals(ydEqpWrkMode2)) {
					ydL3Msg    = "오류:설비작업Mode2 없음(크레인 유인/무인)";
					throw new Exception(ydL3Msg);	
			    }
	         }		
		    slabUtils.printLog(logId, "-----> ydEqpWrkMode (ydEqpWrkMode : "+ydEqpWrkMode+" )","SL");

			if ("1".equals(ydEqpWrkMode) || "4".equals(ydEqpWrkMode) || "5".equals(ydEqpWrkMode)  ) {
				brGp = "R";	//복구
			} else {
				brGp = "B";	//고장
				//ydEqpWrkMode = "0";
			}
			slabUtils.printLog(logId, "-----> brGp (brGp : "+brGp+" )","SL");

			/**********************************************************
			* 2. 설비작업Mode Check
			**********************************************************/
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, ydUserId);
			jrParam.setField("YD_EQP_ID"      , ydEqpId     ); //야드설비ID
			jrParam.setField("YD_EQP_WRK_MODE", ydEqpWrkMode); //야드설비작업Mode
			jrParam.setField("BR_GP"          , brGp        ); //고장복구구분
			jrParam.setField("YD_EQP_WRK_MODE2"  		, ydEqpWrkMode2); 	// A:무인, R:리모컨, E:정비, M:유인
			//JDTORecordSet jsChk = commDao.getStat("Eqp", jrParam);
			/*설비상태조회
			SELECT YD_EQP_STAT
			      , YD_EQP_WRK_MODE
			      , YD_EQP_WRK_MODE2
			  FROM TB_YD_EQP EQ
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND DEL_YN    = 'N'
		    */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getStatEqp", logId, methodNm, "설비상태 조회");
		    String ydEqpWrkModeBefore = "";	// 변경전
		    int workCnt = 0;
			if (jsChk == null || jsChk.size() == 0) {
				//설비 Table 존재유무 Check
				ydL3HdRsCd = "EM11";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 정보 없음";
			}
			
			else{
				//설비 Table 설비작업Mode Check
				//ydEqpWrkMode2.equals(slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE2")))
				ydEqpWrkModeBefore = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE"));
				workCnt = Integer.parseInt(slabUtils.trim(jsChk.getRecord(0).getFieldString("WORK_CNT")));
			}
			
			slabUtils.printLog(logId, "----->설비의 현재 작업지시 내려간 CRNSCH 수 : "+workCnt,"SL");
			
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
           
			
			/**********************************************************
			* 3. 설비의 야드설비작업Mode 수정
			**********************************************************/
			jrParam.setField("YD_EQP_STAT"  	, ""); 
            if ("1".equals(ydEqpWrkMode)) { //1: On-Line
	        	
	        	jrParam.setField("YD_EQP_WRK_MODE"  	, ydEqpWrkMode); 	
	        	jrParam.setField("YD_EQP_AUTO_CRN_MODE" , ydEqpWrkMode); 	// 1: 정상
	        	
	        
	        	//2023.08.02 후판 박태훈 계장 요청. 크레인 일시정지 해제시, 대기상태로 돌아가 지시가 있는데도 새로운 작업지시가 먼저 기동되는 문제 
	        	jrParam.setField("YD_EQP_STAT"  	, workCnt >0 ? "1" : "W"); 
	        } else if ("0".equals(ydEqpWrkMode)) {	//0: Off-Line
	        	
	        	jrParam.setField("YD_EQP_WRK_MODE"  	, ydEqpWrkMode); 	
	        	jrParam.setField("YD_EQP_AUTO_CRN_MODE" , "");
	        } else { // 일시정지 ,비상정지
	        	jrParam.setField("YD_EQP_WRK_MODE"  	, ydEqpWrkMode); 
	        	////yys
	        	jrParam.setField("YD_EQP_AUTO_CRN_MODE" , ydEqpWrkMode); 	// 4: 일시정지, 5:비상정지
	        	
	        }
            //L2 에서 송신일때만 체크 2021-06-06 YYS
            jrParam.setField("YD_EQP_WRK_MODE2"  		, ydEqpWrkMode2); 	// A:무인, R:리모컨, E:정비, M:유인
			//쿼리 변경 무인 크레인 적용 2021-06-07 YYS
			/*
			 *설비 작업Mode 수정 
				UPDATE TB_YD_EQP
				   SET MODIFIER        = :V_MODIFIER
				      ,MOD_DDTT         = SYSDATE
				      ,YD_EQP_AUTO_CRN_MODE       = nvl(:V_YD_EQP_AUTO_CRN_MODE,YD_EQP_AUTO_CRN_MODE)
				      ,YD_EQP_WRK_MODE       = nvl(:V_YD_EQP_WRK_MODE,YD_EQP_WRK_MODE)
				      ,YD_EQP_WRK_MODE2     = nvl(:V_YD_EQP_WRK_MODE2,YD_EQP_WRK_MODE2)
				      ,YD_EQP_STAT                = nvl(:V_YD_EQP_STAT,YD_EQP_STAT)
				 WHERE  YD_EQP_ID       = :V_YD_EQP_ID
				             AND DEL_YN      = 'N'
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updStatEqpMode", logId, methodNm, "설비(TB_YD_EQP) 야드설비작업Mode 수정");				
				
			
			String ydGp = ydEqpId.substring(0, 1);
			
			slabUtils.printLog(logId, "-----> 설비의 야드설비작업Mode 수정 완료 (야드구분 : "+ydGp+" )","SL");

			/**********************************************************
			* 4. 크레인 리스케줄
			*  - 고장복구구분 [R:복구 리스케줄, B:고장 리스케줄]
			*  - 작업예약 야드스케쥴우선순위 수정
			*  - 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			*  - 대기상태인 야드설비ID에 해당하는 크레인작업지시 전문 추가
			**********************************************************/
//			if ("CR".equals(ydEqpId.substring(2, 4))) {
//				//크레인 리스케줄
//				jrParam.setField("MSG_ID", msgId); //수신 전문 I/F ID
//				jrRtn = this.trtCrnResch(jrParam);
//			}
			
			/**********************************************************
			* 7. 크레인 모드 변경시 
			*   - ON-LINE  : 스케줄요구호출 (작업지시전송)
			*   - OFF-LINE : 크레인 변경 처리
			**********************************************************/
			if ("CR".equals(ydEqpId.substring(2, 4))) {
			
		        if ("1".equals(ydEqpWrkMode) ) { //1: On-Line
		        	
		        	
					jrParam.setField("YD_EQP_ID", ydEqpId); //야드설비ID
					/*
					 * 
					 * 
					SELECT YD_EQP_ID
					     , YD_GP
					     , YD_BAY_GP
					     , YD_EQP_GP
					     , YD_EQP_NO
					     , YD_WRK_ALW_XAXIS_TO
					     , YD_EQP_NAME
					     , YD_EQP_STAT
					     , YD_EQP_WRK_MODE
					     , YD_WRK_ALW_XAXIS_FR
					     , YD_WRK_ALW_YAXIS_FR
					     , YD_WRK_ALW_YAXIS_TO
					     , YD_WRK_ALW_ZAXIS_FR
					     , YD_WRK_ALW_ZAXIS_TO
					     , YD_CRN_TRAVL_OFFSET
					     , YD_CRN_GRAB_TP
					     , YD_CRN_TRAVS_OFFSET
					     , YD_L2_HMI_STAT
					     , YD_CTS_RELAY_YN
					     , YD_CTS_RELAY_BAY_GP
					     , YD_CRN_GRAB1_ACT_STAT
					     , YD_CRN_GRAB2_ACT_STAT
					     , YD_WRK_ABLE_XAXIS_FR
					     , YD_WRK_ABLE_XAXIS_TO
					     , YD_WRK_ABLE_YAXIS_FR
					     , YD_WRK_ABLE_YAXIS_TO
					     , YD_WRK_ABLE_ZAXIS_FR
					     , YD_WRK_ABLE_ZAXIS_TO
					     , YD_CURR_BAY_GP
					     , YD_HOME_BAY_GP
					     , YD_TCAR_WRK_ABLE_BAY1
					     , YD_TCAR_WRK_ABLE_BAY2
					     , YD_TCAR_WRK_ABLE_BAY3
					     , YD_TCAR_WRK_ABLE_BAY4
					     , YD_TCAR_WRK_ABLE_BAY5
					     , YD_CRN_CURR_XAXIS
					     , YD_CRN_CURR_YAXIS
					     , YD_CRN_USE_SEQ
					     , YD_CRN_CONT_CARASGN_CNT
					     , YD_CRN_CONT_CARASGN_WR
					     , RCPT_TCAR_USE_YN
					     , RCPT_TCAR_BAY
					     , RCPT_TCAR_AIM_BAY_GP
					     , YD_TCAR_WRK_ABLE_BAY6
					     , YD_TCAR_WRK_ABLE_BAY7
					     , YD_TCAR_WRK_ABLE_BAY8
					     , YD_EQP_WRK_MODE2
					     , YD_EQP_AUTO_CRN_MODE
					     , YD_LOC_GP
					  FROM TB_YD_EQP
					 WHERE YD_EQP_ID = :V_YD_EQP_ID
					   AND DEL_YN    = 'N'
					 */
					JDTORecordSet jsEqpChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getYdEqp", logId, methodNm, "설비상태조회");

					if ( jsChk.size() > 0) {
						String sYD_EQP_WRK_MODE = slabUtils.trim(jsEqpChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE"));
						String ydLocGp			= slabUtils.trim(jsEqpChk.getRecord(0).getFieldString("YD_LOC_GP"));
						if (!sYD_EQP_WRK_MODE.equals(ydEqpWrkMode)) {
							brGp = "R"; //복구  	
			        	}
						slabUtils.printLog(logId, "[sYD_EQP_WRK_MODE]"+sYD_EQP_WRK_MODE+"[ydEqpWrkMode]"+ydEqpWrkMode+"[brGp]"+brGp, "SL");
						if ("CR".equals(ydEqpId.substring(2, 4)) 
								&& "0".equals(ydEqpWrkModeBefore) // 이전모드가 off-line 이고
								&& "1".equals(ydEqpWrkMode)     // 변경될 모드가 on-line 이며
								&& "A".equals(ydEqpWrkMode2) ) {//무인일때만 리스케줄
								//크레인 리스케줄
								jrParam.setField("MSG_ID", msgId); //수신 전문 I/F ID
								jrParam.setField("BR_GP" , brGp);
								jrRtn = this.trtCrnResch(jrParam);
								slabUtils.printLog(logId, ">>>>>>>>>>>크레인 trtCrnResch[BR_GP] : "+brGp, "SL");
								
						}
						
						String sYD_EQP_STAT = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT"));
						
						slabUtils.printLog(logId, "■■■크레인모드 변경후 명령선택기동", "SL");
						/*
						SELECT A.YD_CRN_SCH_ID
	            	     , A.YD_WBOOK_ID
	            	     , A.YD_WRK_PROG_STAT
	            	     , A.YD_DN_WO_LOC
	            	     , A.YD_SCH_CD
	            	     , A.YD_EQP_ID
	            	  FROM TB_YD_CRNSCH A
		            	 WHERE A.DEL_YN = 'N'
		                   AND A.YD_EQP_ID = :V_YD_EQP_ID
		                   AND A.YD_WRK_PROG_STAT = '1'
		                 ORDER BY YD_CRN_SCH_ID
	                   */
						//JDTORecordSet jsCraSchChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getCrnSchId", logId, methodNm, "스케줄아이디조회");
						
//////////////////////////////////////////
						 String APPLY_YN37   = commDao.PSlabApplyYn("APPLY_YN37");
			//////////////////////////////////////////
				
				     if("Y".equals(APPLY_YN37)){

			        
						
						if (!"B".equals(sYD_EQP_STAT)) {
							 //ydCrnSchId = slabUtils.trim(jsCraSchChk.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
							 //ydSchCd    = slabUtils.trim(jsCraSchChk.getRecord(0).getFieldString("YD_SCH_CD"));
							/***********************************************************
							 * 크레인의 다음 스케줄 명령 선택 기동 - 운전모드변경 명령선택 기동 호출
							 ***********************************************************/
							//야드설비상태가 대기이면 명령선택전문 전송
							JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, ydUserId);
							jrYdMsg.setField("JMS_TC_CD"         , "Y3YDL007"               ); //JMSTC코드
							jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
							jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
							jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태
							
							//jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   ); //야드스케쥴코드
							//jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId); //야드크레인스케쥴ID
							jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);
							
							//크레인작업지시 전문을 추가
//			    			JDTORecord jrGetYdMsg = this.rcvY3YDL007(jrYdMsg);
//			    			String rtnCd	 = slabUtils.nvl(jrGetYdMsg.getFieldString("RTN_CD"), "0");
//			    			String rtnMsg	 = slabUtils.nvl(jrGetYdMsg.getFieldString("RTN_MSG"), "");
//			    			slabUtils.printLog(logId,  " =======rtnCd:"+ rtnCd, "FL");
//			    			slabUtils.printLog(logId,  " =======rtnMsg:"+ rtnMsg, "FL");
//			    			// ROLLBACK 시 전문 발생
//			    			if (!"1".equals(rtnCd)) {
//			    				throw new Exception(rtnMsg);
//			    			}
//			    			
//			    			jrRtn = slabUtils.addSndData(jrRtn, jrGetYdMsg);
			    			
						}
				     }
					}
		        	
		        } else if ("0".equals(ydEqpWrkMode)) {	//0: Off-Line
		        	
					/*********************************************
					 * 크레인변경 처리 
					 ********************************************/
		        	/* 내부전문 또는 메소드 호출 선택*/
		        	jrParam.setField("YD_EQP_ID"	, ydEqpId        ); //야드설비ID
		        	jrRtn = slabUtils.addSndData(jrRtn, this.offLineChgnCrn(jrParam));
		        }
			}
			
			/**********************************************************
			* 5. 크레인작업실적응답 전문 전송(YDY3L005)
			**********************************************************/
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상 이지만 설비전환은 상태코드 null 로 보내기 위함 : 2021-06-08 YYS)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				
				jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY3L005(resMsg));
			}

			slabUtils.printLog(logId, methodNm, "SL-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "작업Mode 변경이 완료되었습니다.");
			return jrRtn;
			
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(slabUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "PSlabYdCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { slabComm.getYDY3L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
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
		String mthdNm = "OFF-LINE 크레인 변경 처리[PSlabYdL2RcvSeE.offLineChgnCrn] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecordSet rsResult    	= null;
		
		JDTORecord jrRtn = null;	//전문 Return
		
		try{
			slabUtils.printLog(logId, mthdNm, "S+");
			slabUtils.printParam(logId, rcvMsg);

			String sModifier	= slabUtils.nvl(rcvMsg.getFieldString("MODIFIER"),"OFFLINE");
			String ydEqpId		= slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //야드설비ID
			String ydGp			= "D";
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
					ydBayGp = ydEqpId.substring(1,2);
				}
			}
			
			JDTORecord jrParam = slabUtils.getParam(logId, mthdNm, sModifier);

			//해당 Crane 호기에 걸려 있는 스케줄 정보 조회
			jrParam.setField("YD_GP"		, ydGp); //야드구분
			jrParam.setField("YD_BAY_GP"	, ydBayGp); //동구분
			jrParam.setField("YD_EQP_ID"	, ydEqpId); //크레인번호
			rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getCrnWrk", logId, mthdNm, "크레인스케줄조회");
			
			if (rsResult.size() <= 0) {
				slabUtils.printLog(logId, "해당 Crane 호기에 걸려 있는 스케줄 정보 없음! "+ydEqpId, "[]");
				return jrRtn;
			}
			
			String[] arrYdWbookId = new String[rsResult.size()];
			
			for (int ii = 0; ii < rsResult.size(); ii++) {
			
				ydWbookId  = slabUtils.trim(rsResult.getRecord(ii).getFieldString("YD_WBOOK_ID"));
				ydCrnSchId = slabUtils.trim(rsResult.getRecord(ii).getFieldString("YD_CRN_SCH_ID"));
			
			    //작업할 야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (slabUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }  //해당 값이 있는지를 Check
				
				arrYdWbookId[ii] = ydWbookId;

				/**********************************************************
				* 1. 크레인스케줄, 스케줄기준, 설비정보 Check
				* 1.1 크레인스케줄의 스케줄ID 및 설비상태 Check
				* 1.2 크레인스케줄 설비ID로 스케줄기준의 주 및 대체 크레인설비ID와 비교하여 변경 크레인설비ID와 순위를 Set
				* 1.3 변경 할 크레인 정보를 Check
				**********************************************************/
				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				
				/*
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
				JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getCraneChange", logId, mthdNm, "크레인변경 조회");

			    if (jsCrn == null || jsCrn.size() <= 0) {
					continue;
			    }
				
			    JDTORecord jrCrn = jsCrn.getRecord(0);
				
			    ydWrkProgStat   = slabUtils.trim(jrCrn.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				ydEqpId         = slabUtils.trim(jrCrn.getFieldString("YD_EQP_ID"		)); //야드설비ID
				chgYdEqpId      = slabUtils.trim(jrCrn.getFieldString("CHG_YD_EQP_ID"	)); //변경 야드설비ID
				chgYdSchPrior   = slabUtils.trim(jrCrn.getFieldString("CHG_YD_SCH_PRIOR")); //변경 야드스케쥴우선순위
				chgYdEqpStat    = slabUtils.trim(jrCrn.getFieldString("CHG_YD_EQP_STAT"	)); //변경 야드설비상태
				ydLocGp			= slabUtils.trim(jrCrn.getFieldString("YD_LOC_GP"		)); //소재/제품 야드구분
			
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

				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updWrkBookPrior", logId, mthdNm, "작업예약 Table 우선순위 Update");				
				
				// 지시 내려간 스케줄은 상태 유지
				if ("1".equals(ydWrkProgStat) || "S".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.1  OFF-LINE 크레인의 작업지시 취소 전문 송신
					**********************************************************/
						slabUtils.printLog(logId, "OFF-LINE 크레인의 작업지시 취소 전문 송신 "+mthdNm, "SL");
						jrParam.setField("MSG_GP", "D"); //전문구분(취소)
						jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY3L004", jrParam));
					
						slabUtils.printLog(logId, "제품크레인 작업지시 취소 전문 없음 "+mthdNm, "SL");
					
				}
				
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
						commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updCrnWrkMgtW", logId, mthdNm,  "크레인스케줄 Table 크레인ID, 우선순위 Update");					
				
				
			}
			
			slabUtils.printLog(logId, "변경된 크레인의 설비상태: "+ chgYdEqpStat + " " + mthdNm, "SL");
			
			if (!"".equals(chgYdEqpId) && !chgYdEqpId.equals(ydEqpId)) {
				//변경된 크레인 상태 w이면 명령선택기동 EQP
				if ("W".equals(chgYdEqpStat)) {
					
					/***********************************************************
					 * 크레인의 다음 스케줄 명령 선택 기동 - 운전모드변경 명령선택 기동 호출
					 ***********************************************************/
					//야드설비상태가 대기이면 명령선택전문 전송
					JDTORecord jrYdMsg = slabUtils.getParam(logId, mthdNm, sModifier);
					jrYdMsg.setField("JMS_TC_CD"         , "Y3YDL007"               ); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , chgYdEqpId               ); //야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT"  , chgYdEqpStat             ); //야드작업진행상태
					
					jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);
				}
			}
			
			slabUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	

	/**
	 *      [A] 오퍼레이션명 : 설비고장복구실적(Y1YDL004, Y3YDL004, C3YDL008)
	 *      염용선 2020-06-26
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비고장복구실적[PSlabYdL2RcvSeEJB.rcvY3YDL004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		String ydUserId   = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
		JDTORecord resMsg = slabUtils.getParam(logId, methodNm, ydUserId); //크레인작업실적응답 전문 생성용
        //Return Value
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); 

		boolean resYn = false;	//크레인작업실적응답 전문 전송여부

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId           = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId         = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"          )); //야드설비ID
			String ydEqpStat       = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_STAT"        )); //야드설비상태(B:고장, N:정상, R:복구 등)
			String ydEqpPauseCode  = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_PAUSE_CODE"  )); //야드설비휴지코드
			String ydEqpTrblRcvrDt = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_TRBL_RCVR_DT")); //야드설비고장복구일시
			String brGp            = ""; //고장복구구분
			if ("".equals(ydUserId)) { ydUserId = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			//크레인작업실적응답 전문 전송여부를 Check
			if ( msgId.startsWith("Y3") ||	(ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4)))) {
				resYn = true;
			}

			
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setField("YD_EQP_ID"       , ydEqpId       ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydEqpStat     ); //야드작업진행상태(야드설비상태)
			resMsg.setField("YD_SCH_CD"       , ydEqpPauseCode); //야드스케쥴코드(야드설비휴지코드)
			resMsg.setField("YD_L2_WR_GP"     , "R"           ); //야드L2실적구분(고장복구실적)
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
			slabUtils.printLog(logId, ydL3HdRsCd+"==>>>>"+ydL3Msg, "SL");
			
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
				if ("0000".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "B000";
				}
			} else if ("N".equals(ydEqpStat) || "R".equals(ydEqpStat)) {
				brGp = "R"; //복구
				if ("0000".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "R000";
				}

				if ("CR".equals(ydEqpId.substring(2, 4))) {
					ydEqpStat = "W";
				} else {
					ydEqpStat = "N";
				}
			}

			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, ydUserId);
			jrParam.setField("YD_EQP_ID"          , ydEqpId        ); //야드설비ID
			jrParam.setField("YD_EQP_PAUSE_CODE"  , ydEqpPauseCode ); //야드설비휴지코드
			jrParam.setField("YD_EQP_PAUSE_OCC_DT", ydEqpTrblRcvrDt); //야드설비휴지발생일시
			jrParam.setField("YD_EQP_STAT"        , ydEqpStat      ); //야드설비상태
			jrParam.setField("BR_GP"              , brGp           ); //고장복구구분
			
			//JDTORecordSet jsChk = commDao.getStat("Eqp", jrParam);
			/*설비상태조회
			SELECT YD_EQP_STAT
			      ,DECODE(YD_EQP_WRK_MODE,'1','1','0') AS YD_EQP_WRK_MODE
			  FROM TB_YD_EQP EQ
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND DEL_YN    = 'N'
		    */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getStatEqp", logId, methodNm, "설비상태 조회");
			
			if (jsChk == null || jsChk.size() == 0) {
				//설비 Table 존재유무 Check
				ydL3HdRsCd = "BR11";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 정보 없음";
			} else if (ydEqpStat.equals(slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT")))) {
				//설비 Table 설비상태 Check
				ydL3HdRsCd = "BR12";
				ydL3Msg = "오류:현재 설비상태[" + ydEqpStat + "]와 동일";
			}
			slabUtils.printLog(logId, ydL3HdRsCd+"==>>>>"+ydL3Msg, "SL");	
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 3. 설비상태 수정
			**********************************************************/
			//commDao.updStat("Eqp", jrParam);
			/*
			 *설비 상태 수정
				UPDATE TB_YD_EQP
				   SET MODIFIER    = :V_MODIFIER
				      ,MOD_DDTT    = SYSDATE
				      ,YD_EQP_STAT = :V_YD_EQP_STAT
				 WHERE YD_EQP_ID   = :V_YD_EQP_ID
				   AND DEL_YN      = 'N'
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatEqp", logId, methodNm, "설비(TB_YD_EQP) 야드설비상태 수정");				
			
			//크레인정보 Flex Server로 전송
			//더이상 flex 사용하지 않음
			//slabComm.sndToFlexData(resMsg);

			/**********************************************************
			* 4. 설비휴지 등록
			**********************************************************/
			if (!"".equals(brGp)) {
				/*
				 * 설비고장복구실적 설비휴지 등록 
					MERGE INTO TB_YD_EQPPAUSE EP USING (
					SELECT DECODE(DD.NEW_YN,'Y',TO_CHAR(NVL(TO_NUMBER(EP.YD_EQP_PAUSE_OCCR_SEQ),0) + 1,'FM000000000000000000')
					                           ,EP.YD_EQP_PAUSE_OCCR_SEQ) AS YD_EQP_PAUSE_OCCR_SEQ
					      ,DD.YD_EQP_ID
					      ,DD.MODIFIER
					      ,SYSDATE AS MOD_DDTT
					      ,'N'     AS DEL_YN
					      ,DECODE(DD.NEW_YN,'Y',DD.YD_EQP_PAUSE_CODE                                           ) AS YD_EQP_PAUSE_CODE
					      ,DECODE(DD.NEW_YN,'Y',SF_YD_WRK_DUTY(DD.YD_EQP_PAUSE_OCC_DT)                         ) AS YD_EQP_PAUSE_OCC_WRK_DUTY
					      ,DECODE(DD.NEW_YN,'Y',SF_YD_WRK_PARTY(DD.YD_EQP_PAUSE_OCC_DT)                        ) AS YD_EQP_PAUSE_OCC_WRK_PARTY
					      ,DECODE(DD.NEW_YN,'Y',DD.YD_EQP_PAUSE_OCC_DT                                         ) AS YD_EQP_PAUSE_OCC_DT
					      ,DECODE(DD.NEW_YN,'N',DD.YD_EQP_PAUSE_OCC_DT                                         ) AS YD_EQP_PAUSE_END_DT
					      ,DECODE(DD.NEW_YN,'N',SF_YD_WRK_DUTY(DD.YD_EQP_PAUSE_OCC_DT)                         ) AS YD_EQP_PAUSE_END_WRK_DUTY
					      ,DECODE(DD.NEW_YN,'N',SF_YD_WRK_PARTY(DD.YD_EQP_PAUSE_OCC_DT)                        ) AS YD_EQP_PAUSE_END_WRK_PARTY
					      ,DECODE(DD.NEW_YN,'N',ROUND((DD.YD_EQP_PAUSE_OCC_DT - EP.YD_EQP_PAUSE_OCC_DT) * 1440)) AS YD_EQP_PAUSE_PASS_HR
					  FROM TB_YD_EQPPAUSE EP
					      ,(SELECT DD.*
					              ,MAX(EP.YD_EQP_PAUSE_OCCR_SEQ) AS YD_EQP_PAUSE_OCCR_SEQ
					              ,CASE WHEN DD.BR_GP = 'B' OR  MAX(EP.YD_EQP_PAUSE_OCCR_SEQ) IS NULL THEN 'Y' ELSE 'N' END AS NEW_YN --신규여부
					              ,CASE WHEN DD.BR_GP = 'R' AND MAX(EP.YD_EQP_PAUSE_OCCR_SEQ) IS NULL THEN 'N' ELSE 'Y' END AS REG_YN --등록여부
					          FROM TB_YD_EQPPAUSE EP
					              ,(SELECT :V_YD_EQP_ID         AS YD_EQP_ID
					                      ,:V_MODIFIER          AS MODIFIER
					                      ,:V_YD_EQP_PAUSE_CODE AS YD_EQP_PAUSE_CODE
					                      ,TO_DATE(:V_YD_EQP_PAUSE_OCC_DT,'YYYYMMDDHH24MISS') AS YD_EQP_PAUSE_OCC_DT
					                      ,:V_BR_GP             AS BR_GP --고장복구구분
					                  FROM DUAL) DD
					         WHERE DD.YD_EQP_ID = EP.YD_EQP_ID(+)) DD
					 WHERE DD.YD_EQP_ID = EP.YD_EQP_ID(+)
					   AND DD.YD_EQP_PAUSE_OCCR_SEQ = EP.YD_EQP_PAUSE_OCCR_SEQ(+)
					   AND DD.REG_YN = 'Y'
					) DD ON (EP.YD_EQP_PAUSE_OCCR_SEQ = DD.YD_EQP_PAUSE_OCCR_SEQ AND EP.YD_EQP_ID = DD.YD_EQP_ID)
					WHEN MATCHED THEN UPDATE SET
						    EP.MODIFIER                   = DD.MODIFIER
					       ,EP.MOD_DDTT                   = DD.MOD_DDTT
					       ,EP.YD_EQP_PAUSE_END_DT        = DD.YD_EQP_PAUSE_END_DT
					       ,EP.YD_EQP_PAUSE_END_WRK_DUTY  = DD.YD_EQP_PAUSE_END_WRK_DUTY
					       ,EP.YD_EQP_PAUSE_END_WRK_PARTY = DD.YD_EQP_PAUSE_END_WRK_PARTY
					       ,EP.YD_EQP_PAUSE_PASS_HR       = DD.YD_EQP_PAUSE_PASS_HR
					WHEN NOT MATCHED THEN
					INSERT (EP.YD_EQP_PAUSE_OCCR_SEQ, EP.YD_EQP_ID, EP.REGISTER, EP.REG_DDTT,
					        EP.MODIFIER, EP.MOD_DDTT, EP.DEL_YN, EP.YD_EQP_PAUSE_CODE,
					        EP.YD_EQP_PAUSE_OCC_WRK_DUTY, EP.YD_EQP_PAUSE_OCC_WRK_PARTY, EP.YD_EQP_PAUSE_OCC_DT)
					VALUES (DD.YD_EQP_PAUSE_OCCR_SEQ, DD.YD_EQP_ID, DD.MODIFIER, DD.MOD_DDTT,
					        DD.MODIFIER, DD.MOD_DDTT, DD.DEL_YN, DD.YD_EQP_PAUSE_CODE,
					        DD.YD_EQP_PAUSE_OCC_WRK_DUTY, DD.YD_EQP_PAUSE_OCC_WRK_PARTY, DD.YD_EQP_PAUSE_OCC_DT)

				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL004EqpPause", logId, methodNm, "설비휴지(TB_YD_EQPPAUSE) 등록");				
				
			}
			
			/**********************************************************
			* 5. 크레인 리스케줄
			*  - 고장복구구분 [R:복구 리스케줄, B:고장 리스케줄]
			*  - 작업예약 야드스케쥴우선순위 수정
			*  - 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			*  - 대기상태인 야드설비ID에 해당하는 크레인작업지시 전문 추가
			**********************************************************/
			if ("CR".equals(ydEqpId.substring(2, 4))) {
				//해당 크레인 스케줄 상태가 권상지시(1)일 경우 명령선택대기(W)로 변경
				if ("B".equals(ydEqpStat)) {
					/*
					 * 설비고장복구실적 크레인스케줄(대기) 수정 
						UPDATE TB_YD_CRNSCH
						   SET MODIFIER         = :V_MODIFIER
							  ,MOD_DDTT         = SYSDATE
						      ,YD_WRK_PROG_STAT = 'W' --명령선택대기
						      ,YD_WORD_DT       = NULL
						 WHERE YD_EQP_ID        = :V_YD_EQP_ID
						   AND YD_WRK_PROG_STAT = '1' --권상지시
						   AND DEL_YN           = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL004CrnSchW", logId, methodNm, "크레인스케줄(TB_YD_CRNSCH) 진행상태(대기) 수정");				
					
				}
				//크레인 리스케줄
				if(!"DBCRB2".equals(ydEqpId)) {
					jrParam.setField("MSG_ID", msgId); //수신 전문 I/F ID
					jrRtn = this.trtCrnResch(jrParam);
				}
			}
			
			/**********************************************************
			* 6. 크레인작업실적응답 전문 전송(YDY3L005)
			**********************************************************/
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY3L005(resMsg));
			}

			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "설비고장복구실적 정상 처리");
			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(slabUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "PSlabYdCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { slabComm.getYDY3L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	

	/**
	 *      [A] 오퍼레이션명 : 크레인현재위치(Y1YDL005, Y3YDL005)
	 *      염용선 2020-10-29
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
//	public JDTORecord rcvY3YDL005(JDTORecord rcvMsg) throws DAOException {
//		String methodNm = "크레인현재위치[PSlabYdL2RcvSeEJB.rcvY3YDL005] < " + rcvMsg.getResultMsg();
//		String logId = rcvMsg.getResultCode();
//
//		try {
//			slabUtils.printLog(logId, methodNm, "S+");
//
//			String msgId = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			methodNm = msgId.substring(0, 2) + methodNm;
//
//			String ydEqpId    = ""; //야드설비ID
//			String ydCrnXaxis = ""; //야드크레인X축
//			String ydCrnYaxis = ""; //야드크레인Y축
//			String ydGp       = "A"; //야드구분
//
//			if (msgId.startsWith("Y3")) {
//				ydGp = "D"; //후판슬라브야드
//			}
//
//			HashMap hmData = new HashMap(); //전송할 Data
//
//			hmData.put("MSG_GP", "C" ); //크레인위치
//			hmData.put("YD_GP" , ydGp); //야드구분
//
//			for (int ii = 1; ii <= 20; ii++) {
//				ydEqpId    = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    + ii));
//				ydCrnXaxis = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS" + ii));
//				ydCrnYaxis = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS" + ii));
//
//				if (ydEqpId.length() != 6 || "".equals(ydCrnXaxis) || "".equals(ydCrnYaxis)) {
//					continue;
//				}
//
//				hmData.put("YD_EQP_ID" + ii, ydEqpId                );
//				hmData.put("YD_POS_X"  + ii, new Integer(ydCrnXaxis));
//				hmData.put("YD_POS_Y"  + ii, new Integer(ydCrnYaxis));
//			}
//			/**
//			 * flex 사용하지 않음
//			 * 주석 처리 
//			 * 염용선
//			 */
//			//slabComm.sndToFlex("yd_monitor" + ydGp, hmData);
//
//			slabUtils.printLog(logId, methodNm, "S-");
//
//			return null;
//		} catch (DAOException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
//		}
//	}

	
	

	/**
	 *      [A] 오퍼레이션명 : 크레인작업계획요구(Y1YDL006, Y3YDL006)
	 *      염용선 2020-10-29
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY3YDL006(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업계획요구[PSlabYdL2RcvSeEJB.rcvY3YDL006] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			
			 //Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); 
			
            String ydUserId = "";
			//수신 항목 값
			String msgId        = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = slabUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ptopPlntGp   = ""; //조업공장구분
			if ("".equals(ydUserId)) { ydUserId = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydInfoSyncCd)) {
				throw new Exception("야드정보동기화코드(YD_INFO_SYNC_CD) 없음");
			}

			
			if ("P".equals(ydInfoSyncCd)) {
				ptopPlntGp = "PA";	//1후판
			} else if ("Q".equals(ydInfoSyncCd)) {
				ptopPlntGp = "PB";	//2후판
			} else {
				throw new Exception("야드정보동기화코드(YD_INFO_SYNC_CD) 이상 [" + ydInfoSyncCd + "]");
			}
		
			/**********************************************************
			* 2. 크레인작업계획(YDY3L003) 전문 조회
			**********************************************************/
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, ydUserId);
			jrParam.setField("PTOP_PLNT_GP", ptopPlntGp); //조업공장구분

			JDTORecordSet jsL003 = commDao.getMsgL2("YDY3L003", jrParam);
			if(jsL003.size() <= 0){
				throw new Exception("조업공장구분에 맞는 데이터 없음 [" + ptopPlntGp + "]");
			}
			//전송Data 조회
			jrRtn = slabUtils.addSndData(jrRtn,jsL003);

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	

	/**
	 *      [A] 오퍼레이션명 : 크레인작업지시요구(Y3YDL007)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY3YDL007(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업지시요구[PSlabYdL2RcvSeEJB.rcvY3YDL007] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		String sModifier   = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
		
		JDTORecord resMsg = slabUtils.getParam(logId, methodNm, sModifier); //크레인작업실적응답 전문 생성용
		JDTORecord jrRtn  = slabUtils.getParam(logId, methodNm, sModifier);
		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printParam(logId + " 크레인작업지시요구 시작 : Param", rcvMsg);
			
			//수신 항목 값
			String msgId         = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydWrkProgStat = slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String toBedGubun    = slabUtils.trim(rcvMsg.getFieldString("TO_BED_GUBUN"    )); //to위치변경일경우 5
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			slabUtils.printLog(logId, "후판슬라브 START 크레인작업지시요구 : ["+ydCrnSchId+"]", "S+");
			slabUtils.printLog(logId, "크레인작업지시요구 [ " + ydEqpId + " :야드작업진행상태(YD_WRK_PROG_STAT)>> " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
			
			if("X".equals(ydWrkProgStat)){
				/* 
				 *  4561 이상 슬라브
				 * 후판장척재슬라브 R/T베드 장입요구
				 */
				slabUtils.printLog(logId, "후판장척재슬라브 R/T베드 장입요구 [ " + ydEqpId + " : " + ydWrkProgStat  + " ]", "SL");
				
				/**********************************************************
				* 2. Take-In 재료번호 Check
				**********************************************************/
				//조회 및 등록용
				JDTORecord jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name

				jrParam.setField("YD_STK_COL_GP", "DART01"   ); //야드적치열구분
				jrParam.setField("YD_STK_BED_NO", "05"); 		  //야드적치Bed번호
				jrParam.setField("YD_STK_LYR_NO", "001"); 	  //야드적치단번호

				/*
				 *  Take-In완료 Bed정보 조회
					SELECT SL.YD_STK_COL_GP
					      ,SL.YD_STK_BED_NO
					      ,SL.YD_STK_LYR_NO
					      ,SL.STL_NO
					      ,(SELECT SC.SLAB_GP
					          FROM VW_YD_SLABCOMM SC
					         WHERE SC.SLAB_NO = SL.STL_NO) AS SLAB_GP
					      ,(SELECT BR.YD_STR_LOC
					          FROM VW_YD_YDB033 BR  --Slab보급설비기준
					         WHERE BR.YD_STK_COL_GP = SL.YD_STK_COL_GP) AS YD_STR_LOC --야드저장위치(공통 수정용)
					      ,NVL(SR.TI_SUP_YN    ,'Y') AS TI_SUP_YN     --NULL이면 스카핑/2차절단이므로 자동보급
					      ,NVL(SR.TI_PRE_SUP_YN,'N') AS TI_PRE_SUP_YN --NULL이면 선보급 안함
					  FROM TB_YD_STKLYR SL
					      ,(SELECT REPR_CD_GP                           AS YD_STK_COL_GP
					              ,NVL(MIN(DECODE(ITEM,'1',ITEM1)),'N') AS TI_SUP_YN     --TakeIn공Bed보급요구여부
					              ,NVL(MIN(DECODE(ITEM,'2',ITEM1)),'N') AS TI_PRE_SUP_YN --TakeInBed1매선보급요구여부
					          FROM TB_YD_RULE
					         WHERE REPR_CD_GP = :V_YD_STK_COL_GP
					           AND CD_GP = '*'
					         GROUP BY REPR_CD_GP) SR
					 WHERE SL.YD_STK_COL_GP = SR.YD_STK_COL_GP(+)
					   AND SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
					   AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
					   AND SL.YD_STK_LYR_NO = :V_YD_STK_LYR_NO

				 */
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getC3YDL005Bed", logId, methodNm, "Bed정보 조회");
	    	
				String carryInReqYn = "";
				
				if (jsChk != null && jsChk.size() > 0) {
					JDTORecord jrChk = jsChk.getRecord(0);

					//Carry-In요구 결정
					String tiSupYn    = slabUtils.trim(jrChk.getFieldString("TI_SUP_YN"    )); //TakeIn공Bed보급요구여부

					if ("Y".equals(tiSupYn)){
						carryInReqYn = "Y"; //Carry-In요구구분
					}
				} else {					 
					throw new Exception("적치단[DART01/05/001] 정보가 없습니다.");
				}
				
				/**********************************************************
				* 2. 적치Bed 재료번호, 재료상태  Update
				**********************************************************/
				jrParam.setField("MODIFIER"           , sModifier); //수정자
				jrParam.setField("STL_NO"             , ""      ); //재료번호
				jrParam.setField("YD_STK_LYR_MTL_STAT", "E"     ); //야드적치단재료상태(적치가능)

				//적치Bed Table Update
				//commDao.updSlabYd("StkLyrStlNo", jrParam);
				/*
				 *적치단 재료번호 수정 
					UPDATE TB_YD_STKLYR
					   SET MODIFIER            = :V_MODIFIER
					      ,MOD_DDTT            = SYSDATE
					      ,STL_NO 
					             = :V_STL_NO
					      ,YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
					 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
					   AND YD_STK_BED_NO       = :V_YD_STK_BED_NO
					   AND YD_STK_LYR_NO       = :V_YD_STK_LYR_NO
					   AND DEL_YN              = 'N'
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updSlabYdStkLyrStlNo", logId, methodNm, "적치단(TB_YD_STKLYR) 재료번호 수정");				
				
				
				//Carry-In요구구분이 'Y'이면
				slabUtils.printLog(logId, "-----> Carry-In요구구분 : " + carryInReqYn, "SL");
				
				if ("Y".equals(carryInReqYn)) {
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

					//설비보급요구 전문
					//설비보급요구 전문
					slabUtils.printLog(logId, "-----> 설비보급요구 처리 (YDYDJ421)", "SL");
					/*
					 * YDYDJ420 >> YDYDJ421 내부전문 코드 변경
					 * 염용선 2020-11-10
					 */
					jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ421"               ); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , "DART01"                 ); //야드설비ID
					jrYdMsg.setField("YD_STK_BED_NO"     , "05"              		); //야드적치Bed번호
					jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); //야드스케쥴기동구분(Auto)
					jrYdMsg.setField("MODIFIER"        , sModifier                 ); //수정자
					
					//전송할 전문에 추가
					jrRtn = slabUtils.addSndData(jrRtn,jrYdMsg);
				}
				
				slabUtils.printLog(logId, "-----> 설비보급요구 처리중 (YDYDJ421) ", "SL");
				 
				jrRtn.setField("RTN_CD" , "1");
				jrRtn.setField("RTN_MSG", "설비보급요구 처리(YDYDJ421).");
				return jrRtn;
			}
			
			
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
			jrParam.setField("MODIFIER"     , sModifier  ); //수정자

			/**********************************************************
			* 1. 설비상태 Check
			**********************************************************/
			JDTORecord jrChk = slabComm.chkEqpStat(jrParam);

			ydL3HdRsCd = slabUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));
			ydL3Msg    = slabUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
				
			}
			
			slabUtils.printLog(logId, "-----> 설비상태 Check 완료", "SL");

			/**********************************************************
			* 2. 크레인스케줄 조회
			*    2.1 크레인스케줄이 존재하면 전송
			*    2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
			**********************************************************/
			/*
			 * 크레인작업지시요구 크레인스케줄 조회 
				SELECT YD_CRN_SCH_ID
				      ,YD_WRK_PROG_STAT
				  FROM (SELECT CS.YD_CRN_SCH_ID
				              ,CS.YD_WRK_PROG_STAT
				              ,DECODE(CS.YD_WRK_PROG_STAT,'W','0',CS.YD_WRK_PROG_STAT) AS SEQ1
				              ,DECODE(CS.YD_WBOOK_ID,CT.YD_WBOOK_ID,0,1)               AS SEQ2
				              ,DECODE(CS.YD_WBOOK_ID,CT.YD_WBOOK_ID,0,CS.YD_SCH_PRIOR) AS YD_SCH_PRIOR
				          FROM TB_YD_CRNSCH CS
				              ,(SELECT MIN(YD_WBOOK_ID) AS YD_WBOOK_ID
				                  FROM TB_YD_CRNSCH
				                 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID) CT
				         WHERE CS.DEL_YN = 'N'
				           AND CS.YD_EQP_ID = :V_YD_EQP_ID
				         ORDER BY SEQ1 DESC, SEQ2, YD_SCH_PRIOR, CS.YD_CRN_SCH_ID)
				 WHERE ROWNUM = 1
			 */
			JDTORecordSet jsSch = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL007CS", logId, methodNm, "크레인스케줄 조회");
			
			/*
			 * ydWrkProgStat  = C 이면 작업보류 상태 이므로 해당 크레인에 대기상태의 스케줄이 있으면 선택 하고 없으면 대기중인 작업예약을 기동 시킨다.
			 * YYS 2022-01-13
			 */
			/////////////////////////////////////////////////////////////////////////////////////////
//			String ydCrnSchIdFr    = "";
//			String ydWrkProgStatFr = "DD";
//			String ydCrnSchIdSe    = "";
//			String ydWrkProgStatSe = "DD";
//			JDTORecordSet jsGetSchList =  JDTORecordFactory.getInstance().createRecordSet("");
//			if (jsSch.size() > 0) {
//				ydCrnSchIdFr    = slabUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
//				ydWrkProgStatFr = slabUtils.trim(jsSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
//                if("C".equals(ydWrkProgStatFr)){//스케줄 보류 상태 이면
//					
//					 jsGetSchList = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL007CS2", logId, methodNm, "크레인스케줄 조회");
//					if (jsGetSchList.size() > 0) {
//						ydCrnSchIdFr    = slabUtils.trim(jsGetSchList.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
//						ydWrkProgStatFr = slabUtils.trim(jsGetSchList.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
//					}else{
//						 ydCrnSchIdFr    = "";
//						 ydWrkProgStatFr = "DD";
//					}
//				}
//				
//			}
			/////////////////////&& !"DD".equals(ydWrkProgStatFr)////////////////////////////////////////////////////////////////////
			if (jsSch.size() > 0 ) {
				/**********************************************************
				* 2.1 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송
				**********************************************************/
				ydCrnSchId    = slabUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				ydWrkProgStat = slabUtils.trim(jsSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));

//				if(!"DD".equals(ydWrkProgStatFr)){
//					ydCrnSchId    = ydCrnSchIdFr;
//					ydWrkProgStat = ydWrkProgStatFr;
//
//				}
//				
				slabUtils.printLog(logId, " ydCrnSchId ["+ydCrnSchId+"], ydWrkProgStat ["+ydWrkProgStat+"]", "SL");
				
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID

				if ("S".equals(ydWrkProgStat)
						|| "1".equals(ydWrkProgStat) 
						|| "2".equals(ydWrkProgStat) 
						|| "3".equals(ydWrkProgStat)
						|| "5".equals(ydWrkProgStat)
						) {
					/**********************************************************
					* 2.1.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 재지시 전송
					**********************************************************/
					slabUtils.printLog(logId, "-----> 권상,권하 재지시 처리:" + ydWrkProgStat, "SL");
					jrParam.setField("MSG_GP", "U"); //전문구분 - 재지시
					
					slabUtils.printLog(logId, "야드 작업 진행상태가 S,1,2,3  전문 재전송처리", "SL");
	        		
	        		/*  
	        		UPDATE TB_YD_CRNSCH
	        		   SET MODIFIER         = :V_MODIFIER
	        		     , MOD_DDTT         = SYSDATE
	        		     , YD_WORD_DT       = SYSDATE
	        		 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
	        		   AND DEL_YN           = 'N'
	        		   AND YD_WRK_PROG_STAT IN ('S', '1')			 
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdWorkDt", logId, methodNm, "스케쥴 수정");

					
	            	
				} else {
					
					//설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "1"); //권상작업지시
					/*
	        		UPDATE TB_YD_EQP 
	        		   SET MODIFIER    = :V_MODIFIER
	        		     , MOD_DDTT    = SYSDATE
	        		     , YD_EQP_STAT = :V_YD_EQP_STAT
	        		 WHERE YD_EQP_ID   = :V_YD_EQP_ID
	        		   AND DEL_YN      = 'N'
	        		*/
	        		commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatEqp", logId, methodNm, "설비TABLE 설비상태 수정(W)");	
	        		
	        		//크레인스케줄 야드작업진행상태 수정
					jrParam.setField("YD_WRK_PROG_STAT"	 , "W"); //대기  
					
					/*  
					UPDATE TB_YD_CRNSCH A
					   SET YD_WRK_PROG_STAT =:V_YD_WRK_PROG_STAT
					     , YD_WORD_DT       = NULL
					     , MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					     , YD_SCH_PRIOR     = (SELECT YD_WRK_CRN_PRIOR 
					                             FROM USRYDA.TB_YD_SCHRULE B
					                            WHERE B.YD_SCH_CD = A.YD_SCH_CD)
					 WHERE DEL_YN         = 'N'
					   AND YD_GP          = 'D'
					   AND YD_EQP_ID      = :V_YD_EQP_ID
					   AND YD_CRN_SCH_ID <> :V_YD_CRN_SCH_ID
					   AND YD_WRK_PROG_STAT != 'W'
					*/	   
	        		commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updCrnSchW", logId, methodNm, "해당크레인 야드작업진행상태 초기화");
	        		
	        		

	        		//크레인스케줄 야드작업진행상태 수정
					jrParam.setField("YD_WRK_PROG_STAT"	 , "S"); //선택지시
					jrParam.setField("YD_L2_REQUEST_STAT", "1"); 
					
					/*  
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
	        		commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatCrnSchWrkProg", logId, methodNm, "크레인스케줄 야드작업진행상태 수정");
	        		
					/**********************************************************
					* 2.1.2 대기[W] 이면 다음 작업지시 전송
					**********************************************************/
					slabUtils.printLog(logId, "-----> 대기 : 다음 작업지시 처리 [" + ydWrkProgStat + "]", "SL");
					jrParam.setField("MSG_GP", "I"); //전문구분 - 신규

										
	        		
					//크레인스케줄 권상지시단 수정
					/*
					 * 크레인작업지시요구 크레인스케줄 권상지시단 수정
						MERGE INTO TB_YD_CRNSCH CS USING (
						SELECT CS.YD_CRN_SCH_ID
						      ,MIN(CS.YD_STK_LYR_NO)          AS YD_UP_WO_LAYER
						      ,NVL(ROUND(SUM(ST.YD_MTL_T)),0) AS YD_UP_WO_LOC_ZAXIS
						  FROM TB_YD_STKLYR SL
						      ,TB_YD_STOCK  ST
						      ,(SELECT CS.YD_CRN_SCH_ID
						              ,CS.YD_UP_WO_LAYER
						              ,SL.YD_STK_COL_GP
						              ,SL.YD_STK_BED_NO
						              ,SL.YD_STK_LYR_NO
						              ,ROW_NUMBER() OVER (ORDER BY CM.YD_STK_LYR_NO) AS RN
						          FROM TB_YD_CRNSCH    CS
						              ,TB_YD_CRNWRKMTL CM
						              ,TB_YD_STKLYR    SL
						         WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
						           AND SL.YD_STK_COL_GP = SUBSTR(CS.YD_UP_WO_LOC,1,6)
						           AND SL.YD_STK_BED_NO = SUBSTR(CS.YD_UP_WO_LOC,7,2)
						           AND SL.STL_NO        = CM.STL_NO
						           AND SL.YD_STK_LYR_MTL_STAT IN ('C','U')
						           AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						           AND CM.DEL_YN        = 'N') CS
						 WHERE CS.YD_UP_WO_LAYER != CS.YD_STK_LYR_NO
						   AND CS.RN              = 1
						   AND SL.YD_STK_COL_GP   = CS.YD_STK_COL_GP
						   AND SL.YD_STK_BED_NO   = CS.YD_STK_BED_NO
						   AND SL.YD_STK_LYR_NO   < CS.YD_STK_LYR_NO
						   AND SL.STL_NO          = ST.STL_NO
						 GROUP BY CS.YD_CRN_SCH_ID
						) DD ON (CS.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID)
						WHEN MATCHED THEN UPDATE SET
						     CS.MODIFIER           = :V_MODIFIER
						    ,CS.MOD_DDTT           = SYSDATE
						    --,CS.YD_UP_WO_LAYER     = DD.YD_UP_WO_LAYER
						    ,CS.YD_UP_WO_LOC_ZAXIS = DD.YD_UP_WO_LOC_ZAXIS

					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL007CS", logId, methodNm, "크레인스케줄(TB_YD_CRNSCH) 권상지시단 수정");				
					
				}
				
				/**********************************************************
				* 권상 권하 시 크레인 동선안에서 최고 슬라브 적치높이및 다음 작업 지시 슬라브적치 최고 높이 계산
				* 전문 추가 항목 값 계산
				* 2020-11-26
				* 염용선
				* 1.현재작업 동선상 적치슬라브 최고 높이
				* 2.다음작업 동선상 적치슬라브 최고 높이
				**********************************************************/
				/**********************************************************
				 * 1.현재작업 동선상 적치슬라브 최고 높이
				**********************************************************/
				//권하위치변경시 야드작업진행상태를 5 로 셋팅하여 지시전문 송신
				//YYS 2021-05-27
				if("5".equals(toBedGubun) || "6".equals(toBedGubun)){//6:권하위치변경이면서 대기중인 (W) 스케줄
					jrParam.setField("YD_WRK_PROG_STAT2"	 , toBedGubun); //to위치변경일 경우 셋팅
				}else{
					if("5".equals(ydWrkProgStat)){
						jrParam.setField("YD_WRK_PROG_STAT2"	 , "6");//권하위치변경과 구분 하기 위해
					}
				}
				String slabMaxH = slabComm.getSlabMaxH("", jrParam,  logId ,  methodNm , sModifier);
				
				jrParam.setField("YD_WRK_MAX_SLAB_H"    , slabMaxH                 ); //야드작업최대SLAB높이
					
				/**********************************************************
				 * 2.다음작업 동선상 적치슬라브 최고 높이
				**********************************************************/
				String slabMaxHNext = slabComm.getSlabMaxH("NEXT", jrParam,  logId ,  methodNm , sModifier);
				
				jrParam.setField("YD_WRK_MAX_SLAB_H_NEXT"    , slabMaxHNext                 ); //야드작업최대SLAB높이
				slabUtils.printLog(logId, "slabMaxH:"+slabMaxH+">>>>slabMaxHNext:"+slabMaxHNext, "S-------------------");
				slabUtils.printLog(logId, "-----> 크레인작업지시 추출" , "S-------------------");
				
				JDTORecordSet jsL004 = commDao.getMsgL2("YDY3L004", jrParam);	
				
				
				//크레인작업지시 Z값 갱신 처리
				if(jsL004.size() > 0 ) {
	    		
	    			jrParam.setField("YD_UP_WO_LOC_ZAXIS"   , jsL004.getRecord(0).getFieldString("YD_UP_WO_LOC_ZAXIS"));	
	    			jrParam.setField("YD_DN_WO_LOC_ZAXIS"   , jsL004.getRecord(0).getFieldString("YD_DN_WO_LOC_ZAXIS"));	
	    			/* --크레인스케줄 z값 갱신
					UPDATE TB_YD_CRNSCH
					   SET YD_UP_WO_LOC_ZAXIS = :V_YD_UP_WO_LOC_ZAXIS
					     , YD_DN_WO_LOC_ZAXIS = :V_YD_DN_WO_LOC_ZAXIS
					 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
					   AND DEL_YN           = 'N' 
					*/
	        		commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updZAXIS", logId, methodNm, "SLAB크레인스케줄 Z값 수정");
	    			        			
	    		}
	    		
	    		jrRtn = slabUtils.addSndData(jrRtn,jsL004);
	    		
				slabUtils.printLog(logId, "크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
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
					jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY3L005(resMsg));

					slabUtils.printLog(logId, "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + ydEqpId + " : " + ydWrkProgStat + " - " + ydCrnSchId + " ]", "SL");
				} else {
					/**********************************************************
					* 2.2.2 대기상태[W], 권하완료[4] 지시요구
					**********************************************************/
					//크레인작업지시가 없으면 설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "W"); //대기(Wait)

	        		//commDao.updStat("Eqp", jrParam);
	        		/*
					 *설비 상태 수정
						UPDATE TB_YD_EQP
						   SET MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,YD_EQP_STAT = :V_YD_EQP_STAT
						 WHERE YD_EQP_ID   = :V_YD_EQP_ID
						   AND DEL_YN      = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatEqp", logId, methodNm, "설비(TB_YD_EQP) 야드설비상태 수정");				
					
	        		JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

					
	    			//작업예약 조회
	        		/*
					 * 크레인작업지시요구 작업예약 조회 
						
					WITH TEMP AS (
					    SELECT :V_YD_EQP_ID AS V_YD_EQP_ID FROM DUAL
					)
					SELECT YD_WBOOK_ID
					      ,YD_SCH_CD
					      ,YD_EQP_ID
					      ,YD_SCH_PRIOR
					      ,YD_EQP_SEQ
					      ,YD_WRK_PLAN_CRN
					  FROM (
					        SELECT YD_WBOOK_ID
					              ,YD_SCH_CD
					              ,(CASE WHEN B.V_YD_EQP_ID=nvl(A.YD_WRK_PLAN_CRN,B.V_YD_EQP_ID)  THEN 2 ELSE 3 END) AS YD_EQP_SEQ
					              ,nvl(A.YD_WRK_PLAN_CRN,B.V_YD_EQP_ID) as YD_EQP_ID
					              ,YD_SCH_PRIOR
					              ,A.YD_WRK_PLAN_CRN
					          FROM TB_YD_WRKBOOK A, TEMP B
					         WHERE DEL_YN = 'N'
					           AND YD_GP = 'D'
					           AND YD_WRK_PLAN_TCAR IS NULL
					           AND TRN_EQP_CD IS NULL
					           AND CAR_NO IS NULL
					           AND YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID FROM TB_YD_CRNSCH WHERE DEL_YN = 'N')
					
					           -- 2021.06.11 진행반 요청 : 다음 진행할 예약작업은 스케쥴코드에 설정된 작업/대체 크레인 제외하고, 지정->주 크레인 순으로 대상 조회
					           AND (
					                -- YD_WRK_PLAN_CRN (지정크레인)이 존재하면...지정크레인만 비교
					                (YD_WRK_PLAN_CRN IS NOT NULL AND YD_WRK_PLAN_CRN = B.V_YD_EQP_ID)
					                OR
					                -- YD_WRK_PLAN_CRN (지정크레인)이 존재하지 않으면...스케쥴코드의 작업크레인 비교
					                (YD_WRK_PLAN_CRN IS NULL AND YD_SCH_CD IN (
					                                                             SELECT YD_SCH_CD 
					                                                               FROM (
					                                                                     SELECT YD_SCH_CD  
					                                                                           ,(CASE WHEN (SELECT YD_EQP_STAT FROM TB_YD_EQP D WHERE D.YD_EQP_ID=C.YD_WRK_CRN) = 'B'
					                                                                                  THEN YD_ALT_CRN ELSE YD_WRK_CRN END) AS YD_WRK_CRN  
					                                                                       FROM TB_YD_SCHRULE C  
					                                                                      WHERE YD_SCH_PROH_EXN = 'N'
					                                                                        AND DEL_YN = 'N'
					                                                                    )  
					                                                              WHERE YD_WRK_CRN = :V_YD_EQP_ID
					                                                          ))
					               )
					
					         ORDER BY YD_SCH_PRIOR, YD_EQP_SEQ, YD_WBOOK_ID
					    )
					    WHERE ROWNUM = 1
    
					 */
					JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvDAO.getY3YDL007WB", logId, methodNm, "작업예약 조회");
					
					//slabUtils.printLog(logId, "-----> 대기상태[W], 권하완료[4] 지시요구 : 작업예약 추출");
					slabUtils.printLog(logId, "-----> 대기상태[W], 권하완료[4] 지시요구 : 작업예약 추출", "SL");
					//작업예약이 있으면 크레인스케줄 없으면 슬라브자동준비작업요구 호출
					if (jsWrkBook.size() > 0) {
						ydL3Msg = "크레인스케줄 호출";

						jrYdMsg.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"    , jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
						jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
						jrYdMsg.setField("MODIFIER"   , sModifier); //수정자

						slabUtils.printLog(logId, "-----> 대기상태[W], 권하완료[4] 지시요구 : 크레인스케줄 호출","SL");
						
						jrRtn = slabComm.getCrnSchMsg(jrYdMsg);
						
						String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
						String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
						slabUtils.printLog(logId, methodNm, "S-");
						slabUtils.printLog(logId, rtnCd+"===RETURN VALUE CHECK========"+rtnMsg, "SL");
						if ("0".equals(rtnCd)) {
							//작업지시 등 전송할 전문이 있으면 받아서 전송
							// 스케줄 기동이 안돼면 작업 종결 처리 를 위해 전문 송신
							// 2021-12-03 yys
							ydL3Msg = "다음 크레인작업지시 없음";
							resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
							resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
							jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY3L005(resMsg));
							
							jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", rtnMsg);
							return jrRtn;
						}
						//resMsg.setField("YD_L3_HD_RS_CD", "8888" ); //야드L3처리결과코드
					} else {
						/**********************************************************
						* 장입준비작업요구는 사용하지 않음
						***********************************************************
						
						**********************************************************/
						ydL3Msg = "다음 크레인작업지시 없음";
						resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
						resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
						jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY3L005(resMsg));
					}

					
					

					slabUtils.printLog(logId, "크레인작업지시요구(다음지시) " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");
				}
			}
			slabUtils.printLog(logId, "후판슬라브 END 크레인작업지시요구 : ["+ydCrnSchId+"]", "S+");
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "크레인작업지시요구 완료되었습니다.");
			return jrRtn;
		} catch (Exception e) {
			
			try {
				//chito : 정상SET후  ERROR 발생한 경우								
				if( "0000".equals(slabUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
					resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
					resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
				}
				
				//크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { slabComm.getYDY3L005(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	


	/**
	 *      [A] 오퍼레이션명 : 크레인권상실적(Y3YDL008)  
	 *      염용선 2020-06-29
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL008(JDTORecord rcvMsg) throws DAOException {
		String methodNm   = "크레인권상실적[PSlabYdL2RcvSeEJB.rcvY3YDL008] < " + rcvMsg.getResultMsg();
		String logId      = rcvMsg.getResultCode();
		String ydUserId   = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //수정자(Backup Only)
		
		JDTORecord resMsg = slabUtils.getParam(logId, methodNm, ydUserId); //크레인작업실적응답 전문 생성용
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydEqpWrkMode2 = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE2")); //A:무인, R:리모컨, M:유인 ,E:정비			
			String ydWrkProgStat = slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydUpWrLoc     = slabUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LOC"    )); //야드권상실적위치
			String ydUpWrLayer   = slabUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LAYER"  )); //야드권상실적단
			String ydCrnXaxis    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축			
			String ydWbookId     = ""; //야드작업예약ID
			String ydDnWoLoc     = ""; //야드권하지시위치
			if ("".equals(ydUserId)) { ydUserId = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			//크레인작업실적응답 전문 전송여부를 Check -> Backup시에도 응답전문 전송
			//if (!msgId.equals(ydUserId) || msgId.startsWith("YDYD")) {
			//	resYn = false;
			//}
			slabUtils.printLog(logId, "후판슬라브-권상실적 : ["+ydCrnSchId+"]", "S+");
			slabUtils.printLog(logId, "L2>>>L3 후판슬라브-야드권상실적단 : ["+ydUpWrLayer+"]", "S+");
			slabUtils.printLog(logId, "L2>>>L3 후판슬라브-야드권상실적위치 : ["+ydUpWrLoc+"]", "S+");
			JDTORecord jrRtn = slabUtils.getParam(logId, methodNm, ydUserId);	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "U"          ); //야드L2실적구분(권상실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)

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
			/**********************************************************
			* 1.1. 수신 항목 값 Check : 크레인 상태
			**********************************************************/
			
			slabUtils.printLog(logId, "-----> 수신 항목 값 Check 완료","SL");

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			if ("".equals(ydEqpWrkMode2)) {
				ydL3Msg    = "오류:설비작업Mode2 없음(크레인 유인/무인)";
				throw new Exception(ydL3Msg);
			}
			
			/**********************************************************
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, ydUserId);
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			
			JDTORecord jrChk = slabUtils.getParam(logId, methodNm, ydUserId);
			//JDTORecordSet jsChk = commDao.getStat("CrnSch", jrParam);
			/*
			 * 크레인스케줄상태 조회 
				SELECT YD_WBOOK_ID
				      ,YD_EQP_ID
				      ,YD_SCH_CD
				      ,YD_WRK_PROG_STAT
				      ,YD_DN_WO_LOC
				      ,YD_UP_WR_LOC
				      ,(SELECT YD_EQP_WRK_MODE2 FROM TB_YD_EQP WHERE YD_EQP_ID = TB_YD_CRNSCH.YD_EQP_ID) AS DB_YD_EQP_WRK_MODE2
				  FROM TB_YD_CRNSCH
				 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND DEL_YN        = 'N'
			 */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getStatCrnSch", logId, methodNm, "크레인스케줄상태");
    		
			if (jsChk.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "UP11";
				ydL3Msg = "오류:크레인스케쥴 DB정보 없음";
			} else {
				//크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = slabUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
				ydDnWoLoc       = slabUtils.trim(jrChk.getFieldString("YD_DN_WO_LOC"    )); //야드권하지시위치
				String tmpStat  = slabUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = slabUtils.trim(jrChk.getFieldString("YD_EQP_ID"       )); //야드설비ID
				String tmpMode2 = slabUtils.trim(jrChk.getFieldString("DB_YD_EQP_WRK_MODE2")); //DB 작업모드2
				if (!"1".equals(tmpStat) && !"W".equals(tmpStat)) {
					ydL3HdRsCd = "UP12";
					if("2".equals(tmpStat)){
						return null;
					}
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
			jrParam.setField("YD_UP_WRK_MODE2", ydEqpWrkMode2);
			jrParam.setField("YD_DN_WRK_MODE2", ydEqpWrkMode2);
			/**********************************************************
			* 2-1. 권상단 상단에 스케쥴없이 재료가 적치되어 있는 경우( 권상처리 에러) Check
			**********************************************************/
			jrParam.setField("YD_STK_COL_GP", ydUpWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO", ydUpWrLoc.substring(6, 8)); //야드적치Bed번호
			jrParam.setField("YD_UP_WR_LAYER"  , ydUpWrLayer ); //야드권상실적단
			
				/*
				 * 
				     권상단 상단에 스케쥴없이 재료가 적치되어 있는 경우( 권상처리 에러)
					SELECT 'Y' AS CHK 
					FROM TB_YD_STKLYR A 
					WHERE A.STL_NO IS NOT NULL 
					AND A.YD_STK_COL_GP=:V_YD_STK_COL_GP --'AA0302' 
					AND A.YD_STK_BED_NO=:V_YD_STK_BED_NO --'02'
					AND YD_STK_LYR_NO  >:V_YD_UP_WR_LAYER --'005'
					AND YD_STK_LYR_MTL_STAT='C'
				 */
			JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getStatDan", logId, methodNm, "단적치상태");
    		
			if (jsChk2.size() > 0) {
				//상단에 스케쥴 없는 재료  존재유무 Check
				ydL3HdRsCd = "UP14";
				//ydL3Msg = "오류:스케쥴 없이 더미재가 상단에 존재";
				ydL3Msg = "오류:("+ydUpWrLoc+")슬라브가 상단에 존재";
				
				
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY3L005(resMsg));
			}
			//********************************************************/
			
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				
				slabUtils.printLog(logId, "["+ methodNm +"] "+ ydL3Msg,"SL");
				throw new Exception(ydL3Msg);
				
			}
			
			slabUtils.printLog(logId, "-----> 크레인스케쥴ID Check 완료","SL");
			
			/**********************************************************
			* 3. 전송 전문 조회
			* 3.1 생산통제 장입진행실적(후판:YDCTJ031)			
			**********************************************************/
			String currDt = slabUtils.getDateTime14(); //현재시각

			jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
			jrParam.setField("WR_DT"      , currDt   ); //실적일시

			//생산통제 장입진행실적(권상권하)
			jrParam.setField("UP_DN_GP"     , "U"                      ); //권상권하구분(권상)
			jrParam.setField("YD_STK_COL_GP", ydDnWoLoc.substring(0, 6)); //야드적치열구분
			jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDCTJ031UD", jrParam));

			jrParam.setField("YD_STK_COL_GP", ydUpWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO", ydUpWrLoc.substring(6, 8)); //야드적치Bed번호

 
			/**********************************************************
			* 4. 권상실적위치가 대차(하차)
			* 4.1 대차 하차 정보 등록
			*   - 대차이송재료 삭제
			*   -  대차작업실적(YDY3L007) 전송 : 후판Slab야드L2 전문 없음
			*   - 대차스케줄 삭제 : 하차완료 시
			* - 공대차출발지시 :  후판Slab야드L2 대차출발지시(YDY3L006) 
			**********************************************************/
			if ("TC".equals(ydUpWrLoc.substring(2, 4))) {
				//대차하차스케쥴 조회
				/*
				 * 크레인권상실적 하차 대차스케줄 조회 
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
				 WHERE TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				   AND TS.DEL_YN = 'N'
				 */
				jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL008TcarSchUd", logId, methodNm, "하차 대차스케줄 조회");
	    		
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);
					String ydTcarSchId  = slabUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID" )); //야드대차스케쥴ID
					String tcarUdCmplYn = slabUtils.trim(jrChk.getFieldString("TCAR_UD_CMPL_YN")); //대차하차완료여부

					if ("Y".equals(tcarUdCmplYn)) {
						//하차완료이면 대차스케줄 삭제 후 공대차출발지시 처리
						if (resYn) {
							resMsg.setField("YD_L3_HD_RS_CD", "UP21"                    ); //야드L3처리결과코드
							resMsg.setField("YD_L3_MSG"     , "오류:대차 하차완료처리 실패"); //야드L3MESSAGE
						}

						//하차완료(공대차출발지시) 처리
						JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, ydUserId);
						
						jrYdMsg.setField("YD_EQP_ID"     , ydUpWrLoc.substring(0, 1) + "X" + ydUpWrLoc.substring(2, 6)); //야드설비ID(대차)
						jrYdMsg.setField("YD_TCAR_SCH_ID", ydTcarSchId); //야드대차스케쥴ID
						
						JDTORecord jrTcarSchUdCmpl =slabComm.trtTcarSchUdCmpl(jrYdMsg);
						String rtnCd	 = slabUtils.nvl(jrTcarSchUdCmpl.getFieldString("RTN_CD"), "0");
						String rtnMsg	 = slabUtils.nvl(jrTcarSchUdCmpl.getFieldString("RTN_MSG"), "");
						
						if ("0".equals(rtnCd)) {
							jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", rtnMsg);
							return jrRtn;
						}
						
						jrRtn = slabUtils.addSndData(jrRtn, jrTcarSchUdCmpl);
					} else {
						//하차완료가 아니면 크레인 권상재료 만큼 대차이송재료 삭제 후 대차작업실적 전송
						jrParam.setField("YD_TCAR_SCH_ID"   , ydTcarSchId              ); //야드대차스케쥴ID
						jrParam.setField("YD_CARUD_STOP_LOC", ydUpWrLoc.substring(0, 6)); //야드하차정지위치
						jrParam.setField("YD_CARUD_WRK_CRN" , ydEqpId                  ); //야드하차작업크레인

						//대차스케줄(하차) 수정
						/*
						 * 
						 *크레인권상실적 하차 대차스케줄 수정 
						UPDATE TB_YD_TCARSCH
						   SET MODIFIER          = :V_MODIFIER
						      ,MOD_DDTT          = SYSDATE
						      ,YD_CAR_PROG_STAT  = 'D' --하차개시
						      ,YD_CARUD_ST_DT    = NVL(YD_CARUD_ST_DT,SYSDATE)
						      ,YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC
						      ,YD_CARUD_WRK_CRN  = :V_YD_CARUD_WRK_CRN
						 WHERE YD_TCAR_SCH_ID    = :V_YD_TCAR_SCH_ID
						   AND DEL_YN            = 'N'
						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL008TcarSchUd", logId, methodNm, "대차스케줄(TB_YD_TCARSCH) 하차정보 수정");				
						
						//대차이송재료 삭제
						/*
						 * 크레인권상실적 대차이송재료 삭제 
							MERGE INTO TB_YD_TCARFTMVMTL TM USING (
							SELECT :V_YD_TCAR_SCH_ID AS YD_TCAR_SCH_ID
							      ,STL_NO
							  FROM TB_YD_CRNWRKMTL CM
							 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
							   AND DEL_YN        = 'N'
							) DD ON (TM.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID AND TM.STL_NO = DD.STL_NO)
							WHEN MATCHED THEN UPDATE SET
								 TM.MODIFIER = :V_MODIFIER
							    ,TM.MOD_DDTT = SYSDATE
							    ,TM.DEL_YN   = 'Y'
						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL008TcarMtlDel", logId, methodNm, "대차이송재료(TB_YD_TCARFTMVMTL) 하차정보 삭제");				
					}
				}
			}

			/**********************************************************
			* 5. 권상실적위치가 차량(하차)
			*    차량스케줄 야드차량진행상태가 하차도착(B) 또는 하차검수(C) 이고
			*  	   야드차량사용구분이 구내운송(L) 이면
			* 5.1 구내운송 소재차량하차개시(YDTSJ009) 전송
			* 5.2 차량이송재료 삭제
			* 5.3 차량스케줄 수정
			*   - 야드차량진행상태(D:하차개시), 야드설비작업매수 등 수정
			**********************************************************/
			if("PT".equals(ydUpWrLoc.substring(2, 4))) {
				slabUtils.printLog(logId, "-----> 권상실적위치가 차량(하차) 처리준비","SL");
				//차량스케줄  야드하차작업예약ID 등록 (차량스케줄에 야드하차작업예약ID 없을 경우)
				/*
				 * 크레인권상실적 하차 차량스케줄 작업예약ID 수정
					MERGE INTO TB_YD_CARSCH TS USING (
					SELECT YD_CAR_SCH_ID
					      ,YD_WBOOK_ID
					  FROM (SELECT TS.YD_CAR_SCH_ID
					              ,WM.YD_WBOOK_ID
					          FROM TB_YD_CARSCH     TS
					              ,TB_YD_CARFTMVMTL TM
					              ,TB_YD_WRKBOOKMTL WM
					         WHERE WM.STL_NO        = TM.STL_NO
					           AND TM.YD_CAR_SCH_ID = TS.YD_CAR_SCH_ID
					           AND WM.YD_WBOOK_ID   = :V_YD_WBOOK_ID
					           AND WM.DEL_YN        = 'N'
					           AND TM.DEL_YN        = 'N'
					           AND TS.DEL_YN        = 'N'
					           AND NOT EXISTS (SELECT YD_CAR_SCH_ID
					                             FROM TB_YD_CARSCH
					                            WHERE YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
					                              AND DEL_YN               = 'N')
					         ORDER BY TS.YD_CAR_SCH_ID)
					 WHERE ROWNUM = 1
					) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
					WHEN MATCHED THEN UPDATE SET
					     TS.MODIFIER             = :V_MODIFIER
					    ,TS.MOD_DDTT             = SYSDATE
					    ,TS.YD_CARUD_WRK_BOOK_ID = DD.YD_WBOOK_ID
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL008CarSchUdWbId", logId, methodNm, "차량스케줄(TB_YD_CARSCH) 작업예약ID 수정");				
				
				//구내운송 소재차량하차개시
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ009", jrParam));

				//차량이송재료 삭제
				/*
				 * 크레인권상실적 하차 차량이송재료 삭제 
					MERGE INTO TB_YD_CARFTMVMTL TM USING (
					SELECT TS.YD_CAR_SCH_ID
					      ,CM.STL_NO
					  FROM TB_YD_CRNSCH    CS
					      ,TB_YD_CRNWRKMTL CM
					      ,TB_YD_CARSCH    TS
					 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					   AND CS.YD_WBOOK_ID   = TS.YD_CARUD_WRK_BOOK_ID
					   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					   AND CM.DEL_YN        = 'N'
					   AND TS.DEL_YN        = 'N'
					) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.STL_NO = DD.STL_NO)
					WHEN MATCHED THEN UPDATE SET
						 TM.MODIFIER = :V_MODIFIER
					    ,TM.MOD_DDTT = SYSDATE
					    ,TM.DEL_YN   = 'Y'
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL008CarMtlDel", logId, methodNm, "차량이송재료(TB_YD_CARFTMVMTL) 하차정보 삭제");				
				
				
				//하차 차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드하차개시일시 수정
				/*
				 * 크레인권상실적 하차 차량스케줄 수정
					MERGE INTO TB_YD_CARSCH TS USING (
					SELECT TS.YD_CAR_SCH_ID
					      ,COUNT(ST.STL_NO)         AS YD_EQP_WRK_SH
					      ,NVL(SUM(ST.YD_MTL_WT),0) AS YD_EQP_WRK_WT
					      ,CASE WHEN MIN(TS.YD_CAR_PROG_STAT) IN ('B','C') --하차도착,검수
					            THEN 'D'                                   --하차개시
					       ELSE MIN(TS.YD_CAR_PROG_STAT) END AS YD_CAR_PROG_STAT
					      ,CASE WHEN MIN(TS.YD_CAR_PROG_STAT) IN ('B','C') --하차도착,검수
					            THEN NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
					       ELSE MIN(TS.YD_CARUD_ST_DT) END AS YD_CARUD_ST_DT
					  FROM TB_YD_CARSCH     TS
					      ,TB_YD_CARFTMVMTL TM
					      ,TB_YD_STOCK      ST
					 WHERE TS.YD_CAR_SCH_ID        = TM.YD_CAR_SCH_ID(+)
					   AND TM.STL_NO               = ST.STL_NO(+)
					   AND TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
					   AND TS.DEL_YN               = 'N'
					   AND TM.DEL_YN(+)            = 'N'
					 GROUP BY TS.YD_CAR_SCH_ID
					) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
					WHEN MATCHED THEN UPDATE SET
						 TS.MODIFIER         = :V_MODIFIER
					    ,TS.MOD_DDTT         = SYSDATE
					    ,TS.YD_EQP_WRK_SH    = DD.YD_EQP_WRK_SH
					    ,TS.YD_EQP_WRK_WT    = DD.YD_EQP_WRK_WT
					    ,TS.YD_CAR_PROG_STAT = DD.YD_CAR_PROG_STAT
					    ,TS.YD_CARUD_ST_DT   = DD.YD_CARUD_ST_DT
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL008CarSchUd", logId, methodNm, "차량스케줄(TB_YD_CARSCH) 하차정보 수정");				
				
				//차량 회송처리기능. 권상시마다 update 
				jrParam.setField("YD_CRN_SCH_ID"  , ydCrnSchId );
				/*
				 * 회송완료처리  

					UPDATE USRYDA.TB_YD_RETHTHIST
					SET MODIFIER = :V_MODIFIER
					   ,MOD_DDTT = SYSDATE
					   ,YD_RETHT_CMPL_DT = SYSDATE
					   ,YD_RETHT_STAT_CD = '3'
					WHERE STL_NO IN (
					    SELECT STL_NO
					    FROM TB_YD_CRNSCH CS
					        ,TB_YD_CRNWRKMTL CM
					    WHERE 1=1
					      AND CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					      AND CS.YD_CRN_SCH_ID = :YD_CRN_SCH_ID)
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL008RethtHist", logId, methodNm, "회송이력(TB_YD_RETHTHIST) 수정");				
				
				slabUtils.printLog(logId, "-----> 권상실적위치가 차량(하차) 처리완료","SL");
			}
			
			/**********************************************************
			* 6. 권하지시위치가 차량(상차)
			*    차량스케줄 야드차량진행상태가 상차도착(2) 또는 상차검수(3) 이면
			* 6.1 구내운송 소재차량상차개시(YDTSJ007) 전송
			*   - 야드차량사용구분이 구내운송(L)
			
			* 6.3 차량스케줄 수정
			*   - 야드설비작업상태(U:공차), 야드차량진행상태(4:상차개시) 등 수정
			**********************************************************/
			if ("PT".equals(ydDnWoLoc.substring(2, 4))) {
				/* 크레인권상실적 상차 차량스케줄 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL008CarSchLd
				SELECT TS.YD_CAR_SCH_ID               --야드차량스케쥴ID
				      ,TS.YD_CAR_USE_GP               --야드차량사용구분
				      ,TS.TRN_EQP_CD                  --운송장비코드
				      ,TS.SPOS_WLOC_CD                --발지개소코드
				      ,SC.YD_PNT_CD AS SPOS_YD_PNT_CD --발지야드포인트코드
				      ,DECODE(SC.YD_CAR_USE_GP,'L',
				       NVL(MV.ARR_WLOC_CD,SF_SLAB_YD_ARR_WLOC_CD(MV.YD_AIM_YD_GP))) AS ARR_WLOC_CD --착지개소코드
				      ,TS.CARD_NO                     --카드번호
				      ,TS.CAR_NO                      --차량번호
				      ,TS.TRANS_ORD_DATE              --운송작업지시일자
				      ,TS.TRANS_ORD_SEQNO             --운송작업지시순번
				  FROM TB_YD_CRNSCH  CS
				      ,TB_YD_STKCOL  SC
				      ,TB_YD_CARSCH  TS
				      ,(SELECT WM.YD_WBOOK_ID
				              ,WM.YD_AIM_YD_GP
				              ,MV.ARR_WLOC_CD
				          FROM TB_PT_STLFRTOMOVE MV
				              ,(SELECT WB.YD_WBOOK_ID
				                      ,WB.YD_AIM_YD_GP
				                      ,WM.STL_NO 
				                  FROM TB_YD_WRKBOOK    WB
				                      ,TB_YD_WRKBOOKMTL WM
				                 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                   AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				                   --AND WM.DEL_YN = 'N'
				                   AND ROWNUM = 1) WM
				         WHERE MV.STL_NO = WM.STL_NO
				           AND MV.TRANSWORD_SEQNO = (SELECT MAX(MM.TRANSWORD_SEQNO)
				                                       FROM TB_PT_STLFRTOMOVE MM
				                                      WHERE MM.STL_NO = MV.STL_NO)) MV
				 WHERE CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND CS.YD_WBOOK_ID   = MV.YD_WBOOK_ID(+)
				   AND SC.YD_STK_COL_GP = SUBSTR(CS.YD_DN_WO_LOC,1,6)
				   AND SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
				   -- 2021.07.19 일반이적으로 작업 시 상차작업예약ID가 생기지 않으므로 조건에서 제외 => 오류로 인한 동일 차량의 실적이 다 건 발생 대비 발지 개소코드 조건 추가함
				   --AND ((SC.YD_CAR_USE_GP = 'L' AND SC.TRN_EQP_CD = TS.TRN_EQP_CD AND TS.YD_CARLD_WRK_BOOK_ID=MV.YD_WBOOK_ID ) --구내운송
				   AND ((SC.YD_CAR_USE_GP = 'L' AND SC.TRN_EQP_CD = TS.TRN_EQP_CD AND TS.SPOS_WLOC_CD IN ( 'DKY21', 'DWY22') ) --구내운송
				     OR (SC.YD_CAR_USE_GP = 'G' AND SC.CARD_NO = TS.CARD_NO AND SC.CAR_NO = TS.CAR_NO)) --출하차량
				   AND TS.YD_CAR_PROG_STAT IN ('2','3') --상차도착,검수
				   AND TS.DEL_YN = 'N'
				 */
				jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL008CarSchLd", logId, methodNm, "상차 차량스케줄 조회");
	    		
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);
				
					//상차개시 전문
					JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, ydUserId);
					if("L".equals(jrChk.getFieldString("YD_CAR_USE_GP"))) {
						slabUtils.printLog(logId, "-----> 구내운송 소재차량상차개시 전문추출","SL");
						//구내운송 소재차량상차개시
						jrYdMsg.setField("JMS_TC_CD"         , "YDTSJ007"); //JMSTC코드
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    ); //JMSTC생성일시
						jrYdMsg.setField("TRN_EQP_CD"        , slabUtils.trim(jrChk.getFieldString("TRN_EQP_CD"    ))); //운송장비코드
						jrYdMsg.setField("SPOS_WLOC_CD"      , slabUtils.trim(jrChk.getFieldString("SPOS_WLOC_CD"  ))); //발지개소코드
						jrYdMsg.setField("SPOS_YD_PNT_CD"    , slabUtils.trim(jrChk.getFieldString("SPOS_YD_PNT_CD"))); //발지야드포인트코드
						jrYdMsg.setField("ARR_WLOC_CD"       , slabUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"   ))); //착지개소코드
						jrYdMsg.setField("TRN_WRK_ST_DT"     , currDt    ); //운송작업시작일시
					}
					
					//전송할 전문에 추가
					jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);

					jrParam.setField("YD_CAR_SCH_ID", slabUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID
					jrParam.setField("ARR_WLOC_CD"  , slabUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"  ))); //착지개소코드

					//상차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
					/*
					 * 크레인권상실적 상차 차량스케줄 수정 
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
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL008CarSchLd", logId, methodNm, "상차 차량스케줄(TB_YD_CARSCH) 수정");				
					
				}
			}

			/**********************************************************
			* 7. 설비, 크레인스케쥴, 적치단, 적치Bed 수정
			* 7.1 설비 야드설비상태(권상중) 수정
			* 7.2 적치단
			*   - 크레인 재료정보 등록
			*   - 권상위치 재료정보 삭제
			* 7.3 적치Bed 야드적치Bed입출고상태(완산Bed->입출고가능) 수정
			* 7.4 크레인스케쥴 권상실적 수정
			**********************************************************/
			//야드권상작업수행구분
			String ydUpWrkActGp = ydEqpWrkMode;

			if ("0".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "M"; //Manual
			} else if("1".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "A"; //Auto
			} else if("4".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "B"; //Backup
			}

			//설비
			jrParam.setField("YD_EQP_ID"       , ydEqpId     ); //야드설비ID
			jrParam.setField("YD_EQP_STAT"     , "2"         ); //야드설비상태(권상중)
			//크레인스케쥴
			jrParam.setField("YD_UP_CMPL_DT"   , currDt      ); //야드권상완료일시
			jrParam.setField("YD_UP_WR_LOC"    , ydUpWrLoc   ); //야드권상실적위치
			jrParam.setField("YD_UP_WR_LAYER"  , ydUpWrLayer ); //야드권상실적단
			jrParam.setField("YD_UP_WRK_ACT_GP", ydUpWrkActGp); //야드권상작업수행구분
			jrParam.setField("YD_UP_WR_XAXIS"  , ydCrnXaxis  ); //야드권상실적X축
			jrParam.setField("YD_UP_WR_YAXIS"  , ydCrnYaxis  ); //야드권상실적Y축
			jrParam.setField("YD_UP_WR_ZAXIS"  , ydCrnZaxis  ); //야드권상실적Z축

			//설비(야드설비상태) 수정
			/*
			 *설비 상태 수정
				UPDATE TB_YD_EQP
				   SET MODIFIER    = :V_MODIFIER
				      ,MOD_DDTT    = SYSDATE
				      ,YD_EQP_STAT = :V_YD_EQP_STAT
				 WHERE YD_EQP_ID   = :V_YD_EQP_ID
				   AND DEL_YN      = 'N'
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatEqp", logId, methodNm, "설비(TB_YD_EQP) 야드설비상태 수정");				
			
			//적치단(크레인 및 권상위치) 수정
			/*
			 * 크레인권상실적 적치단(크레인,권상실적) 수정 - 
				MERGE INTO TB_YD_STKLYR SL USING (
				--크레인위치
				SELECT :V_YD_EQP_ID AS YD_STK_COL_GP
				      ,'01' AS YD_STK_BED_NO
				      ,YD_STK_LYR_NO
				      ,STL_NO
				      ,'C'  AS YD_STK_LYR_MTL_STAT --적치중
				  FROM TB_YD_CRNWRKMTL
				 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND DEL_YN        = 'N'
				 UNION ALL
				--권상실적위치
				SELECT SL.YD_STK_COL_GP
				      ,SL.YD_STK_BED_NO
				      ,SL.YD_STK_LYR_NO
				      ,NULL AS STL_NO
				      ,'E'  AS YD_STK_LYR_MTL_STAT --적치가능
				  FROM TB_YD_CRNWRKMTL CM
				      ,TB_YD_STKLYR    SL
				 WHERE CM.STL_NO        = SL.STL_NO
				   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
				   AND CM.DEL_YN        = 'N'
				) DD ON (SL.YD_STK_COL_GP = DD.YD_STK_COL_GP AND SL.YD_STK_BED_NO = DD.YD_STK_BED_NO AND SL.YD_STK_LYR_NO = DD.YD_STK_LYR_NO)
				WHEN MATCHED THEN UPDATE SET
					 SL.MODIFIER            = :V_MODIFIER
				    ,SL.MOD_DDTT            = SYSDATE
				    ,SL.STL_NO              = DD.STL_NO
				    ,SL.YD_STK_LYR_MTL_STAT = DD.YD_STK_LYR_MTL_STAT
			 */
			
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL008StkLyr", logId, methodNm, "적치단(TB_YD_STKLYR) 크레인 및 권상위치 수정");				
			
			//적치Bed(완산Bed->입출고가능) 수정
			/*
			 * 크레인권상실적 적치Bed(완산) 수정 
				UPDATE TB_YD_STKBED
				   SET MODIFIER             = :V_MODIFIER
					  ,MOD_DDTT             = SYSDATE
				      ,YD_STK_BED_WHIO_STAT = 'E' --입출고가능
				 WHERE YD_STK_COL_GP        = :V_YD_STK_COL_GP
				   AND YD_STK_BED_NO        = :V_YD_STK_BED_NO
				   AND YD_STK_BED_WHIO_STAT = 'F' --완산Bed
				   AND DEL_YN               = 'N'
			 */
			
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL008StkBedF", logId, methodNm, "적치Bed(TB_YD_STKBED) 완산여부 수정");				
			
			//크레인스케쥴 수정
			/*
			 * 크레인권상실적 크레인스케줄 수정 
				UPDATE TB_YD_CRNSCH
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
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL008CrnSch", logId, methodNm, "크레인스케줄(TB_YD_CRNSCH) 수정");				
			

			slabUtils.printLog(logId, "-----> 설비, 크레인스케쥴, 적치단, 적치Bed 수정 처리완료","SL");

			/**********************************************************
			* 8. 재열재인출이면 Bed재료 Shift후 Carry-Out요구
			*  - 적치중인 재료만 있을경우(작업예약,권상대기,권하대기 없음)
			**********************************************************/
			if ("PS".equals(ydUpWrLoc.substring(2, 4))) {
				String carryOutYn = "N"; //Carry-Out가능여부
				String ydStkColGp = ydUpWrLoc.substring(0, 6); //야드적치열구분

				//Bed상태 조회
				jrParam.setField("STL_NO"       , ""        ); //재료번호(적치시)
				jrParam.setField("YD_STK_COL_GP", ydStkColGp); //야드적치열구분

				/*
				 * 재열재Bed상태조회 
					SELECT CASE WHEN MTL_SH = MTL_SH_C --적치재료가 전부 적치중(권상대기,권하대기,작업예약 없음)
					            THEN 'Y' ELSE 'N' END AS CARRY_OUT_YN --Carry-Out가능여부(권상실적 처리시)
					      ,CASE WHEN MAX_BED_NO > MTL_SH   --적치재료가 있고 중간에 공Bed가 있음
					             AND MTL_SH     = MTL_SH_C --Carry-Out가능시
					            THEN 'Y' ELSE 'N' END AS SHIFT_YN --Shift필요여부(재열재추가시)
					      ,TO_CHAR(CASE WHEN MAX_BED_NO > MTL_SH
					                     AND MTL_SH     = MTL_SH_C
					                    THEN MTL_SH            --Shift후
					                    ELSE NVL(MAX_BED_NO,0) --현재MaxBed
					                END + 1,'FM00')   AS YD_STK_BED_NO --재열재추가시Bed
					      ,(SELECT YD_STK_COL_GP||YD_STK_BED_NO||'-'||YD_STK_LYR_NO
					          FROM TB_YD_STKLYR
					         WHERE STL_NO = :V_STL_NO
					           AND ROWNUM = 1) AS YD_STR_LOC --기등록저장위치
					      ,(SELECT COUNT(*)
					          FROM TB_YD_STKBED
					         WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
					           AND DEL_YN = 'N')      AS BED_CNT --재열재인출Bed수
					  FROM (SELECT TO_NUMBER(MAX(SL.YD_STK_BED_NO)) AS MAX_BED_NO
					              ,COUNT(SL.STL_NO)                 AS MTL_SH
					              ,SUM(DECODE(SL.YD_STK_LYR_MTL_STAT,'C',1)) -
					               SUM((SELECT COUNT(*)
					                     FROM TB_YD_WRKBOOKMTL WM
					                    WHERE WM.STL_NO = SL.STL_NO
					                      AND WM.DEL_YN = 'N'))     AS MTL_SH_C
					          FROM TB_YD_STKLYR SL
					         WHERE SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
					           AND SL.STL_NO IS NOT NULL)
				 */
				jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getStatRehtBed", logId, methodNm, "재열재Bed상태");
	    		
				if (jsChk.size() > 0) {
					carryOutYn = slabUtils.trim(jsChk.getRecord(0).getFieldString("CARRY_OUT_YN")); //Carry-Out가능여부
				}

				if ("Y".equals(carryOutYn)) {
					//적치단 재료 Shift(권상 했으니 공Bed가 발생했을 것이므로)
					/*
					 * 적치단 재열재 Shift 
						MERGE INTO TB_YD_STKLYR SL USING (
						SELECT SL.YD_STK_COL_GP
						      ,SL.YD_STK_BED_NO
						      ,SL.YD_STK_LYR_NO
						      ,SM.STL_NO
						      ,DECODE(SM.STL_NO,NULL,'E','C') AS YD_STK_LYR_MTL_STAT
						  FROM TB_YD_STKLYR SL
						      ,(SELECT YD_STK_COL_GP
						              ,TO_CHAR(ROW_NUMBER() OVER (ORDER BY YD_STK_BED_NO),'FM00') AS YD_STK_BED_NO
						              ,YD_STK_LYR_NO
						              ,STL_NO
						          FROM TB_YD_STKLYR SL
						         WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
						           AND STL_NO IS NOT NULL) SM
						 WHERE SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
						   AND SL.YD_STK_BED_NO = SM.YD_STK_BED_NO(+)
						   AND SL.YD_STK_LYR_NO = SM.YD_STK_LYR_NO(+)
						) DD ON (SL.YD_STK_COL_GP = DD.YD_STK_COL_GP AND SL.YD_STK_BED_NO = DD.YD_STK_BED_NO AND SL.YD_STK_LYR_NO = DD.YD_STK_LYR_NO)
						WHEN MATCHED THEN UPDATE SET
						     SL.MODIFIER            = :V_MODIFIER
						    ,SL.MOD_DDTT            = SYSDATE
						    ,SL.STL_NO              = DD.STL_NO
						    ,SL.YD_STK_LYR_MTL_STAT = DD.YD_STK_LYR_MTL_STAT
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updSlabYdStkLyrShift", logId, methodNm, "적치단(TB_YD_STKLYR) 재열재 Shift");				
					
					JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, ydUserId);
	
					//설비인출요구
					jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ411"); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , ydStkColGp); //야드설비ID
					jrYdMsg.setField("YD_STK_BED_NO"     , "01"      ); //야드적치Bed번호
					jrYdMsg.setField("YD_SCH_ST_GP"      , "A"       ); //야드스케쥴기동구분(Auto)
					
					//전송할 전문에 추가
					jrRtn = slabUtils.addSndData(jrRtn,jrYdMsg);
				}
			}

			
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치	
			
			
			/**********************************************************
			* 10. 후판 슬라브야드의 경우 권상시 크레인스케줄 기동
			* 10.1 현재 크레인스케줄이 작업예약의 마지막 작업인지 검사
			* 10.2 다음 작업예약에 대해 크레인 스케줄 기동
			**********************************************************/
			
			if("D".equals(ydUpWrLoc.substring(0, 1))){
				slabUtils.printLog(logId, "후판슬라브야드 작업예약의 마지막 크레인스케줄 권상완료시 다음 작업예약 호출 혹은 크레인스케줄 재지시","SL");
				
				/*
				 * WITH TEMP_PARAM AS (
					    SELECT :V_YD_WBOOK_ID AS V_YD_WBOOK_ID 
					          ,:V_YD_CRN_SCH_ID AS V_YD_CRN_SCH_ID 
					          ,:V_YD_EQP_ID AS V_YD_EQP_ID 
					    FROM DUAL
					
					)
					SELECT YD_CRN_SCH_ID, YD_WBOOK_ID
					  FROM TB_YD_CRNSCH CS
					      ,TEMP_PARAM A
					 WHERE 1=1
					   AND CS.YD_WBOOK_ID = A.V_YD_WBOOK_ID
					   AND CS.DEL_YN ='N'
					   AND CS.YD_CRN_SCH_ID > A.V_YD_CRN_SCH_ID

				 */
				jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL008EndCrnSchYN", logId, methodNm, "작업예약의 마지막 크레인스케줄여부 조회");
	    		
				if (jsChk.size() > 0) {
					slabUtils.printLog(logId, ydEqpId+" 장비의 현 작업예약의 다음 작업할 크레인 스케줄 존재","SL");
				}
				else{
					//작업예약의 마지막 스케줄일시, 장비ID 로 다른 작업예약 크레인스케줄이 있다면 Y1YDL007 호출
					//아닐시 다음 작업예약 크레인스케줄 기동.
					
					/*
					 * 
					 	WITH TEMP_PARAM AS (
						    SELECT :V_YD_WBOOK_ID AS V_YD_WBOOK_ID 
						          ,:V_YD_CRN_SCH_ID AS V_YD_CRN_SCH_ID 
						          ,:V_YD_EQP_ID AS V_YD_EQP_ID 
						    FROM DUAL
						
						)
						
						SELECT YD_CRN_SCH_ID, YD_WBOOK_ID
						  FROM TB_YD_CRNSCH CS
						      ,TEMP_PARAM A
						 WHERE 1=1
						   AND CS.YD_GP = SUBSTR(A.V_YD_EQP_ID,0,1)
						   AND CS.DEL_YN = 'N'
						   AND CS.YD_WBOOK_ID <> A.V_YD_WBOOK_ID
						   AND CS.YD_EQP_ID = A.V_YD_EQP_ID
					 */
					jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL008OtherCrnSchYN", logId, methodNm, "다른 작업예약 크레인 스케줄 기동 여부");
		    		
					//현 작업예약 이외의 기동된 크레인 스케줄 존재
					if(jsChk.size() >0 ){
						slabUtils.printLog(logId,  ydEqpId+" 장비의 다음 작업예약의 크레인스케줄 있으므로, 재지시 처리","SL");
						slabUtils.printLog(logId,  ydSchCd+" ydSchCd","SL");
						slabUtils.printLog(logId,  ydCrnSchId+" ydCrnSchId, 재지시 처리","SL");
						
						//서버 적용여부 : 권상실적 받을때 크레인작업지시요구 전문 호출
							/**********************************************************
							* 12. 크레인작업지시요구 전문 호출(Y3YDL007 )
							**********************************************************/

						
					}
					
					//현 작업예약 이외의 기동된 크레인 스케줄 존재 X
					else {
						slabUtils.printLog(logId,  ydEqpId+" 장비의 다음 작업예약의 크레인스케줄 기동","SL");
						
						JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, ydUserId);
						
						
						/*
						 * 크레인작업지시요구 작업예약 조회 
							 WITH TEMP AS (  
							 SELECT :V_YD_EQP_ID AS V_YD_EQP_ID FROM DUAL  
							 )  
							 SELECT YD_WBOOK_ID  
							      , YD_SCH_CD  
							      , YD_EQP_ID
							      , YD_SCH_PRIOR
							      , YD_EQP_SEQ
							      , YD_WRK_PLAN_CRN
							 FROM (SELECT YD_WBOOK_ID  
							             , YD_SCH_CD  
							             , (CASE WHEN B.V_YD_EQP_ID=nvl(A.YD_WRK_PLAN_CRN,B.V_YD_EQP_ID)  THEN 2 ELSE 3 END) AS YD_EQP_SEQ  
							             , nvl(A.YD_WRK_PLAN_CRN,B.V_YD_EQP_ID) as YD_EQP_ID
							             , YD_SCH_PRIOR
							             , A.YD_WRK_PLAN_CRN
							         FROM TB_YD_WRKBOOK A, TEMP B  
							         WHERE DEL_YN = 'N'  
							             AND YD_WRK_PLAN_TCAR IS NULL  
							             AND TRN_EQP_CD IS NULL  
							             AND CAR_NO IS NULL  
							             AND YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID  
							                                     FROM TB_YD_CRNSCH  
							                                     WHERE DEL_YN = 'N')  
							             AND (YD_WRK_PLAN_CRN = B.V_YD_EQP_ID  
							             OR YD_SCH_CD IN  
							                             ( SELECT YD_SCH_CD FROM (  
							                                                 SELECT YD_SCH_CD  
							                                                     , (CASE WHEN (SELECT YD_EQP_STAT FROM TB_YD_EQP D WHERE D.YD_EQP_ID=C.YD_WRK_CRN)='B'  
							                                                     THEN YD_ALT_CRN ELSE YD_WRK_CRN END) AS YD_WRK_CRN  
							                                                 FROM TB_YD_SCHRULE C  
							                                                 WHERE YD_SCH_PROH_EXN = 'N'  
							                                                 AND DEL_YN = 'N'  
							                                                 )  
							                             WHERE YD_WRK_CRN = :V_YD_EQP_ID  
							                             )  
							             )  
							         ORDER BY YD_SCH_PRIOR, YD_EQP_SEQ , YD_WBOOK_ID  
							 )  
							 WHERE ROWNUM = 1 

						 */
						JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvDAO.getY3YDL007WB", logId, methodNm, "작업예약 조회");
						
						//작업예약이 있으면 크레인스케줄 호출
						if (jsWrkBook.size() > 0) {
							slabUtils.printLog(logId, ydEqpId+" 장비의 현 작업예약의 다음 작업할 작업예약 :"+jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID"),"SL");
							ydL3Msg = "크레인스케줄 호출";

							jrYdMsg.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
							jrYdMsg.setField("YD_SCH_CD"    , jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
							jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
							jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
							jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
							
							slabUtils.printLog(logId,  "-----> 대기상태[W], 권하완료[4] 지시요구 : 크레인스케줄 호출","SL");
							jrRtn = slabUtils.addSndData(jrRtn, slabComm.getCrnSchMsg(jrYdMsg));
							//jrRtn = slabComm.getCrnSchMsg(jrYdMsg);
							resMsg.setField("YD_L3_HD_RS_CD", "8888" ); //야드L3처리결과코드
						}else{
							slabUtils.printLog(logId, ydEqpId+" 장비의 다음 작업예약 존재 X","SL");
						}
					
					
					}
					
					
				}
			}
			
			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY3L005(resMsg));
			}
			slabUtils.printLog(logId, "후판슬라브 END 크레인권상실적 : ["+ydCrnSchId+"]", "S-");
			//권상후 해당크레인 다음  작업예약없으면 압연지시제 더미작업예약 생성
			//   2021-11-03 YYS
			//////////////////////////////////////////////////////////
			
//////////////////////////////////////////
			 String APPLY_YN38   = commDao.PSlabApplyYn("APPLY_YN38");
			 String APPLY_YN40   = commDao.PSlabApplyYn("APPLY_YN40"); // 지시재 준비작업 자동 처리 여부
//////////////////////////////////////////
	
		    
		    	 
		    	 
		    	   /* 크레인별 더미 자동처리 여부 조회
					SELECT   NVL(DTL_ITEM2,'N')  AS  DTL_ITEM2 -- A1 지시제 더미 자동처리 여부 Y:자동 예약처리 N:수동작업
					        ,NVL(DTL_ITEM3,'N')  AS  DTL_ITEM3 -- A2
					        ,NVL(DTL_ITEM4,'N')  AS  DTL_ITEM4 -- A3
					        ,NVL(DTL_ITEM5,'N')  AS  DTL_ITEM5 -- B1
					        ,NVL(DTL_ITEM6,'N')  AS  DTL_ITEM6 -- B2
					        ,NVL(DTL_ITEM7,'N')  AS  DTL_ITEM7 -- 이적준비작업 자동여부 Y:자동 , N:수동
					        ,NVL(DTL_ITEM8,'')   AS  DTL_ITEM8 -- 이적 스케줄코드
					  FROM TB_YD_RULE
					   WHERE REPR_CD_GP = 'DYD400'
					      AND CD_GP = 'D'
					      AND ITEM ='1'
					*/
					JDTORecordSet jsRuleItem = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvDAO.getRuleItem", logId, methodNm, "Dummy 처리 자동 유무 조회");
					String dtlItem2   = "N";
					String dtlItem3   = "N";
					String dtlItem4   = "N";
					String dtlItem5   = "N";
					String dtlItem6   = "N";
					String dtlItem7   = "N";
					String dtlItem8   = "N";
					String dtlItem9   = "N";
					String dtlItem10   = "";
					
					if (jsRuleItem.size() > 0) {//예약작업이 있으면
						dtlItem2   = slabUtils.trim(jsRuleItem.getRecord(0).getFieldString("DTL_ITEM2")); //A1
						dtlItem3   = slabUtils.trim(jsRuleItem.getRecord(0).getFieldString("DTL_ITEM3")); //A2 
						dtlItem4   = slabUtils.trim(jsRuleItem.getRecord(0).getFieldString("DTL_ITEM4")); //A3
						dtlItem5   = slabUtils.trim(jsRuleItem.getRecord(0).getFieldString("DTL_ITEM5")); //B1
						dtlItem6   = slabUtils.trim(jsRuleItem.getRecord(0).getFieldString("DTL_ITEM6")); //B2
						dtlItem7   = slabUtils.trim(jsRuleItem.getRecord(0).getFieldString("DTL_ITEM7")); //준비작업 자동여부(A)
						dtlItem8   = slabUtils.trim(jsRuleItem.getRecord(0).getFieldString("DTL_ITEM8")); //준비작업 스케줄코드(1후판)
						dtlItem9   = slabUtils.trim(jsRuleItem.getRecord(0).getFieldString("DTL_ITEM9")); //준비작업 자동여부(B)
						dtlItem10   = slabUtils.trim(jsRuleItem.getRecord(0).getFieldString("DTL_ITEM10")); //준비작업 스케줄코드(2후판)
		            }
					
		    	    String ydDongGubun = ydEqpId.substring(0, 2);
					String ydCraneGubun = ydEqpId.substring(4, 6);
					String ydSpan       = "DD";
					String ydParam      = "DDDD";
					slabUtils.printLog(logId, "ydEqpId : "+ydEqpId, "SL");
					slabUtils.printLog(logId, "권상위치 : "+ydUpWrLoc, "SL");
					slabUtils.printLog(logId, "ydDongGubun : "+ydDongGubun, "SL");
					slabUtils.printLog(logId, "ydCraneGubun : "+ydCraneGubun, "SL");
					if("A3".equals(ydCraneGubun) && "Y".equals(dtlItem4)){
						ydSpan = "04";
						ydParam = ydDongGubun+ydSpan;
					}else if ("B2".equals(ydCraneGubun) && "Y".equals(dtlItem6)){
						ydSpan = "04";
						ydParam = ydDongGubun+ydSpan;
					}else if("B1".equals(ydCraneGubun) && "Y".equals(dtlItem5)){
						ydSpan = "03";
						ydParam = ydDongGubun+ydSpan;
					}else if("A2".equals(ydCraneGubun) && "Y".equals(dtlItem3)){
						ydSpan = "03";//02
						ydParam = ydDongGubun+ydSpan;
					}else if("A1".equals(ydCraneGubun) && "Y".equals(dtlItem2)){
						ydSpan = "01";						
						ydParam = ydDongGubun+"02";
					}
					
					//대상재 자동이적
					if("Y".equals(APPLY_YN40)){
						if( ("Y".equals(dtlItem7) && "A2".equals(ydCraneGubun)) || ("Y".equals(dtlItem9) && "B1".equals(ydCraneGubun)) ){
							jrParam.setField("YD_EQP_ID" , ydEqpId  );
							JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvDAO.getY3YDL007WBDummyAuto", logId, methodNm, "작업예약 조회");
										            		
							  if (jsWrkBook.size() > 2) {//예약작업이 있으면
								  slabUtils.printLog(logId, "-----> 작업예약 존재함.", "SL");
				              }else{
				            	  
									
				            	    jrParam.setField("CHK"         , "1"    );
				            	    if("DA".equals(ydDongGubun)){
				            	    	jrParam.setField("YD_STR_LOC"  , ydDongGubun+"04" );
										jrParam.setField("YD_STR_LOC2" , ydDongGubun+"03" );
				            	    }else{//2후판은 3스판만 해당
				            	    	jrParam.setField("YD_STR_LOC"  , ydDongGubun+"03" );
										jrParam.setField("YD_STR_LOC2" , ydDongGubun+"03" );
				            	    }
									
									jrParam.setField("DUMMY_YN"    , "N"    );
									
									JDTORecordSet  jsStrLoc = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getStrLoc", logId, methodNm, "이적베드 조회");
									if (jsStrLoc.size() > 0) { 
										String strLoc   = slabUtils.trim(jsStrLoc.getRecord(0).getFieldString("YD_STR_LOC"));
										jrParam.setField("YD_STK_COL_GP"   , strLoc.substring(0, 6));
										jrParam.setField("YD_STK_BED_NO"   , strLoc.substring(6, 8));
										if("DA".equals(ydDongGubun)){
											jrParam.setField("YD_SCH_CD"       , dtlItem8        ); //스케쥴코드
					            	    }else{//2후판은 3스판만 해당
					            	    	jrParam.setField("YD_SCH_CD"       , dtlItem10        ); //스케쥴코드
					            	    }
										
										JDTORecordSet  jsMillWoInq = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getMillWoInqPrSCHAuto2", logId, methodNm, "지시제(더미제외)조회");
					            					    				
								  	    if (jsMillWoInq.size() > 0) {
					    					//작업예약등록	    					
					    					EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
					    					JDTORecord jrMrMove = (JDTORecord)ejbConn.trx("insMvstkWrkBook"	, new Class[] { JDTORecord.class, JDTORecordSet.class } , new Object[] { jrParam, jsMillWoInq });
					    					
					    				}
									}else{
										
									}
				            	} 
						}
					}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
					//더미 자동이적
					if("Y".equals(APPLY_YN38)){
				       if(("Y".equals(dtlItem2)
				    		|| "Y".equals(dtlItem3)
				    		|| "Y".equals(dtlItem4)
				    		|| "Y".equals(dtlItem5)
				    		|| "Y".equals(dtlItem6))
				    		&& !"DDDD".equals(ydParam)){
				    	
					    JDTORecordSet jsMsg = JDTORecordFactory.getInstance().createRecordSet("");	//크레인스케줄 정리를 위함
					    jrParam.setField("YD_EQP_ID" , ydEqpId  );
					    /*
					     * 작업예약 건수 (스케줄 진행중인 예약 포함) 조회후 2건 이상 있을경우 패스
					     * 
								WITH WB AS (
								SELECT WB.*
								  FROM (SELECT ROWNUM AS RNUM
								             , WB.*
								          FROM (SELECT WB.YD_WBOOK_ID
								                      ,WB.REG_DDTT
								                      ,WB.DEL_YN
								                      ,WB.YD_GP
								                      ,WB.YD_BAY_GP
								                      ,WB.YD_SCH_CD
								                      ,WB.YD_SCH_PRIOR
								                      ,WB.YD_TO_LOC_GUIDE
								                      ,WB.YD_WRK_PLAN_CRN
								                      ,WB.YD_WRK_PLAN_TCAR
								                      ,WB.TRN_EQP_CD
								                      ,COUNT(*) OVER() AS TOTALCOUNT
								                  FROM TB_YD_WRKBOOK WB
								                 WHERE WB.DEL_YN = 'N'
								                   AND WB.YD_GP  = 'D'                   
								                ) WB         
								   ) WB
								 
								)
								SELECT * FROM 
								(
								SELECT WB.YD_WBOOK_ID      
								      ,NVL(CS.YD_EQP_ID,SR.YD_WRK_CRN) AS YD_WRK_CRN
								      ,NVL(WB.YD_WRK_PLAN_CRN , NVL(CS.YD_EQP_ID,SR.YD_WRK_CRN) ) YD_WRK_PLAN_CRN
								     
								  FROM TB_YD_SCHRULE SR, WB
								      ,(SELECT WB.YD_WBOOK_ID
								              ,MIN(CS.YD_CRN_SCH_ID)    AS YD_CRN_SCH_ID
								              ,MIN(CS.YD_EQP_ID)        AS YD_EQP_ID
								              ,MIN(CS.YD_WRK_PROG_STAT) AS YD_WRK_PROG_STAT
								              ,NULLIF(TO_CHAR(COUNT(*)),'0') AS CRN_SCH_CNT
								          FROM TB_YD_CRNSCH CS, WB
								         WHERE CS.YD_WBOOK_ID = WB.YD_WBOOK_ID
								           AND CS.DEL_YN      = 'N'
								         GROUP BY WB.YD_WBOOK_ID) CS
								 WHERE WB.YD_SCH_CD   = SR.YD_SCH_CD
								   AND WB.YD_WBOOK_ID = CS.YD_WBOOK_ID(+)
								  ) BB
								  WHERE 1=1
								   AND BB.YD_WRK_PLAN_CRN = :V_YD_EQP_ID
					     */
						JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvDAO.getY3YDL007WBDummyAuto", logId, methodNm, "작업예약 조회");
						
						  if (jsWrkBook.size() > 2) {//예약작업이 있으면
							  slabUtils.printLog(logId, "-----> 작업예약 존재함.", "SL");
			              }else{
			            	
			            	
			            	
							jrParam.setField("CHK"         , "1"     );
							jrParam.setField("YD_STR_LOC"  , ydDongGubun+ydSpan );
							jrParam.setField("YD_STR_LOC2" , ydParam    );
							jrParam.setField("DUMMY_YN"    , "Y"     );
							
							JDTORecordSet  jsStrLoc = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getStrLoc2", logId, methodNm, "이적베드 조회");
							if (jsStrLoc.size() > 0) { 
								String strLoc   = slabUtils.trim(jsStrLoc.getRecord(0).getFieldString("YD_STR_LOC"));
								
								jrParam.setField("YD_STR_LOC_BED"   , strLoc);
								
							/*
							 * 각 스판별 압연지시재 더미 조회
							 * 
							   
							 */
				            	JDTORecordSet jsDummy = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getMillWoInqPrSCHAuto", logId, methodNm, "지시제 더미조회");
				            	if (jsDummy.size() > 0) {
				            		JDTORecord jrDummy = slabUtils.getParam(logId, methodNm, ydUserId);
				            		
									int ydSlabT = Integer.parseInt(slabUtils.nvl(jsDummy.getRecord(0).getFieldString("SLAB_T"),"0"));
									int ydSlabLen = Integer.parseInt(slabUtils.nvl(jsDummy.getRecord(0).getFieldString("SLAB_LEN"),"0"));
									
				            		String ydStrLoc   = slabUtils.trim(jsDummy.getRecord(0).getFieldString("YD_STR_LOC"   ));		//야드저장위치
				            		String ydStkLyrNo = slabUtils.trim(jsDummy.getRecord(0).getFieldString("YD_STK_LYR_NO"));		//야드적치단번호
				    				
				            		//2022.05.12 후판 펀치리스트 27 300T 더미이적 스케줄 강제하는 부분 삭제.
				            		/*if(ydSlabT > 251 && "DA".equals(ydStrLoc.substring(0, 2))){	
				    					if("04".equals(ydStrLoc.substring(2, 4))){
				    						ydSchCd    = "DAT204MM";	//야드스케쥴코드
				    					}else{
				    						ydSchCd    = "DAT203MM";	//야드스케쥴코드
				    					}
				    						    					
				    				}else{
				    					if(ydSlabT < 200 && "DB".equals(ydStrLoc.substring(0, 2))){	
				    						ydSchCd    = "DBS103MM"; // 사이징재 코드
				    					}else{
				    						ydSchCd    = ydStrLoc.substring(0, 2) + "YD" + ydStrLoc.substring(2, 4) + "MM";	//야드스케쥴코드
				    					}
				    					
				    				}*/
				    				
				    				if(ydSlabT < 200 && "DB".equals(ydStrLoc.substring(0, 2))){	
			    						ydSchCd    = "DBS103MM"; // 사이징재 코드
			    					}else{
			    						ydSchCd    = ydStrLoc.substring(0, 2) + "YD" + ydStrLoc.substring(2, 4) + "MM";	//야드스케쥴코드
			    					}
				    				
				    				
				    				jrParam.setField("YD_STK_COL_GP", ydStrLoc.substring(0, 6)); //야드적치열구분
				    				jrParam.setField("YD_STK_BED_NO", ydStrLoc.substring(6, 8)); //야드적치Bed번호
				    				jrParam.setField("YD_STK_LYR_NO", ydStkLyrNo              ); //야드적치단번호
				    				
				    				/*
				    				 * SELECT LR.YD_SCH_CD, LB.YD_STK_COL_GP, LB.YD_STK_BED_NO
										  FROM TB_YD_LOCSRCHRNG LR
										      ,TB_YD_LOCSRCHBED LB
										 WHERE LR.YD_LOC_SRCH_RNG_REG_SNO = LB.YD_LOC_SRCH_RNG_REG_SNO
										   AND LB.YD_STK_COL_GP = :V_YD_STK_COL_GP
										   AND LB.YD_STK_BED_NO = :V_YD_STK_BED_NO
										   AND LR.YD_SCH_CD   = 'DAL199LM'
										   AND LR.YD_ROUTE_GP = 'DD'
										   AND LR.DEL_YN      = 'N'
										   AND LB.DEL_YN      = 'N'
										   AND LB.DEL_YN ='Y' --2022.05.12 후판 펀치리스트 장척재 강제 해제 (MAX길이 반영으로 인해) 추후 코드 삭제
				    				*/	   
				    				//JDTORecordSet jsLongLen = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getLongLen", logId, methodNm, "장척재 베드조회");
					            	if ("DA02".equals(ydStrLoc.substring(0, 4)) && "05".equals(ydStrLoc.substring(6, 8))) {//권상위치가 장척재 관리 베드이면 스케줄코드를 장척재 코드로 셋팅
					            		JDTORecordSet jsLongLen = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getLongLen", logId, methodNm, "장척재 베드조회");
						            	if (jsLongLen.size() > 0){
					            				ydSchCd = slabUtils.trim(jsLongLen.getRecord(0).getFieldString("YD_SCH_CD"   )); //"DAL199LM" //장척재베드 벗어나지 못하게 함
					            		}
					            	}
				    				
				    				jrParam.setField("YD_SCH_CD"    , ydSchCd                 ); //야드스케쥴코드
				    				
					    				if("PU".equals(ydStrLoc.substring(2, 4)) 
					    					||"TC".equals(ydStrLoc.substring(2, 4))
					    					||"RT".equals(ydStrLoc.substring(2, 4)) 
					    					||"PT".equals(ydStrLoc.substring(2, 4))
					    					||"CR".equals(ydStrLoc.substring(2, 4))	){
					    					slabUtils.printLog(logId, "-----> 장입/입고 베드 및 대차의 더미는 제외함.", "SL");
					    				}else{
						    				//작업예약 대상재료 조회
						    				/*
						    				
														  SELECT SL.STL_NO
														      ,SL.YD_STK_COL_GP
														      ,SL.YD_STK_BED_NO
														      ,SL.YD_STK_LYR_NO
														      ,ST.CAL_SLAB_WT         AS YD_MTL_WT
														      ,ST.REAL_MEASURE_SLAB_T AS YD_MTL_T
														      ,ST.REAL_MEASURE_SLAB_W AS YD_MTL_W
														  FROM TB_YD_STKLYR SL
														      -- ,TB_YD_STOCK  ST
														      ,(
														            SELECT ST.*
														                 , SC.SLAB_LEN               -- 길이
														                 , SC.REAL_MEASURE_SLAB_LEN  -- 실측길이
														                 , SC.REAL_MEASURE_SLAB_T    -- 실측두께
														                 , SC.REAL_MEASURE_SLAB_W    -- 실측폭
														                 , SC.CAL_SLAB_WT            -- 계산중량
														              FROM USRYDA.TB_YD_STOCK    ST -- YD_저장품
														                 , USRPTA.TB_PT_SLABCOMM SC -- SLAB공통
														             WHERE ST.STL_NO = SC.SLAB_NO
														      ) ST
														 WHERE SL.STL_NO        = ST.STL_NO
														   AND SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
														   AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
														   AND SL.YD_STK_LYR_NO > :V_YD_STK_LYR_NO
														   AND SL.YD_STK_LYR_MTL_STAT = 'C'
														 ORDER BY SL.YD_STK_LYR_NO DESC
														 
															    				 
															    				 
											      SELECT SL.STL_NO
										              ,SL.YD_STK_COL_GP
										              ,SL.YD_STK_BED_NO
										              ,SL.YD_STK_LYR_NO
										              ,ST.YD_MTL_WT
										              ,ST.YD_MTL_T
										              ,ST.YD_MTL_W
										          FROM TB_YD_STKLYR SL
										              ,TB_YD_STOCK  ST
										         WHERE SL.STL_NO        = ST.STL_NO
										           AND SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
										           AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
										           AND SL.YD_STK_LYR_NO = :V_YD_STK_LYR_NO
										           AND SL.YD_STK_LYR_MTL_STAT = 'C'
										           AND SL.YD_STK_COL_GP||SL.YD_STK_BED_NO NOT IN (SELECT YD_STK_COL_GP||YD_STK_BED_NO FROM TB_YD_WRKBOOKMTL  WHERE 1=1
										               AND YD_STK_COL_GP = :V_YD_STK_COL_GP
										               AND YD_STK_BED_NO = :V_YD_STK_BED_NO
										               AND DEL_YN = 'N'
										               GROUP BY YD_STK_COL_GP||YD_STK_BED_NO)
										         ORDER BY SL.YD_STK_LYR_NO DESC
						    				 */
						    				JDTORecordSet jsWbMtl = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getMvStkWrkBookRegDmMtl", logId, methodNm, "Dummy이적재료조회");
						    	    						
						    				if (jsWbMtl.size() > 0) {
						    					//작업예약등록	
						    					if ("DAL199LM".equals(ydSchCd) || "DBS103MM".equals(ydSchCd)){//권상베드 주변 20개 검색 하지 않고 장척재 권하위치로 보내기 위함
						    						
						    					}else{
						    						jrParam.setField("TRT_GP"    , "DM"                 );// 더미이적 확인
						    					}
						    					EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
						    					jrDummy = (JDTORecord)ejbConn.trx("insMvstkWrkBook"	, new Class[] { JDTORecord.class, JDTORecordSet.class } , new Object[] { jrParam, jsWbMtl });
						    					
						    				}
				    				     }
				    				
				            	    }
							 }
			               }
				      }
		       }
			//////////////////////////////////////////////////////////
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "크레인권상실적");
			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(slabUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "PSlabYdCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { slabComm.getYDY3L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	

	
	

               
	
	/**
	 *      [A] 오퍼레이션명 : 크레인권하실적(Y3YDL009)
	 *      2020-11-19
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL009(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인권하실적[PSlabYdL2RcvSeEJB.rcvY3YDL009] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		String sModifier   = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //수정자(Backup Only)
		
		JDTORecord jrRtn  = slabUtils.getParam(logId, methodNm, sModifier); 	//전문 Return
		JDTORecord resMsg = slabUtils.getParam(logId, methodNm, sModifier); //크레인작업실적응답 전문 생성용
		JDTORecord recInTemp = slabUtils.getParam(logId, methodNm, sModifier);	
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부
		
		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:off-line, 1:on-line, 4:일시정지)
			String ydEqpWrkMode2  = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE2" )); //"A" : 자동 ,"R":리모컨, "E":정비 , "M":유인
			String ydWrkProgStat = slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태(4:권하완료, 5:강제권하)
			String ydSchCd       = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydDnWrLoc     = slabUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LOC"    )); //야드권하실적위치
			String ydDnWrLayer   = slabUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LAYER"  )); //야드권하실적단
			String ydCrnXaxis    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축			
			//String modifier      = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"      )); //수정자(Backup Only)
			String ydWbookId     = ""; //야드작업예약ID
			String ydUpWrLoc     = ""; //야드권상실적위치
			if ("".equals(sModifier)) { sModifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			//크레인작업실적응답 전문 전송여부를 Check -> Backup시에도 응답전문 전송
			//if (!msgId.equals(modifier) || msgId.startsWith("YDYD")) {
			//	resYn = false;
			//}
			slabUtils.printLog(logId, "후판슬라브-권하실적 : ["+ydCrnSchId+"] " + ydEqpId, "S+");
			slabUtils.printLog(logId, "야드설비ID : "+ydEqpId+">>>>야드설비작업Mode : "+ ydEqpWrkMode+">>>>>야드스케쥴코드 : "+ ydSchCd+">>>>>야드크레인스케쥴ID : "+ ydCrnSchId+">>>>>야드권하실적위치 : "+ ydDnWrLoc, "SL");
			
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L3_HD_RS_CD"  , "DN99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권하실적 수신처리"); //야드L3MESSAGE(Error)
			if ("4".equals(ydWrkProgStat)) {
				resMsg.setField("YD_L2_WR_GP", "D"); //야드L2실적구분(권하실적)
			} else {
				resMsg.setField("YD_L2_WR_GP", "F"); //야드L2실적구분(강제권하)
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
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			if ("".equals(ydEqpWrkMode2)) {
				ydL3Msg    = "오류:설비작업Mode2 값이 없음(크레인 유인/무인)";
				throw new Exception(ydL3Msg);
			}
			
			slabUtils.printLog(logId, "-----> 수신 항목 값 Check 완료","SL");

			/**********************************************************
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			
			JDTORecord jrChk = null;
			//JDTORecordSet jsChk = commDao.getStat("CrnSch", jrParam);
			/*
			 * 크레인스케줄상태 조회 
				SELECT YD_WBOOK_ID
				      ,YD_EQP_ID
				      ,YD_SCH_CD
				      ,YD_WRK_PROG_STAT
				      ,YD_DN_WO_LOC
				      ,YD_UP_WR_LOC
				  FROM TB_YD_CRNSCH
				 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND DEL_YN        = 'N'
			 */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getStatCrnSch", logId, methodNm, "크레인스케줄상태");
    
			if (jsChk.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "DN11";
				ydL3Msg = "오류:크레인스케쥴ID DB정보 없음";
			} else {
				//크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = slabUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
				ydSchCd         = slabUtils.trim(jrChk.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
				ydUpWrLoc       = slabUtils.trim(jrChk.getFieldString("YD_UP_WR_LOC"    )); //야드권상실적위치
				String tmpStat  = slabUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = slabUtils.trim(jrChk.getFieldString("YD_EQP_ID"       )); //야드설비ID
				if (!"2".equals(tmpStat) && !"3".equals(tmpStat)) { //2:권상완료, 3:권하지시
					ydL3HdRsCd = "DN12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "DN13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				//throw new Exception(ydL3Msg);
				slabUtils.printLog(logId, "["+ methodNm +"] "+ ydL3Msg,"SL");
				return jrRtn;
			}

			//조회 Parameter
			jrParam.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			jrParam.setField("YD_STK_COL_GP", ydDnWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO", ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
			//설비
			jrParam.setField("YD_EQP_ID"  , ydEqpId); //야드설비ID(크레인)
			jrParam.setField("YD_EQP_STAT", "4"    ); //야드설비상태(권하완료)
			
			jrParam.setField("YD_UP_WRK_MODE2" , ydEqpWrkMode2); //모드 추가
			jrParam.setField("YD_DN_WRK_MODE2" , ydEqpWrkMode2); //모드 추가
			//실제 야드권하실적단 및 기타 정보 조회
			String wbCmplYn = ""; //작업예약완료여부
			boolean chgDnWrLayer = false; //권하위치 적치단 변경여부

			/*
			 * 크레인권하실적 현재정보 조회 
				SELECT 
				       --TO_CHAR(SUM(CASE WHEN SL.YD_STK_LYR_MTL_STAT IN ('C','U') THEN 1 ELSE 0 END) + 1,'FM000') AS YD_DN_WR_LAYER
				       MIN((SELECT TO_CHAR(NVL(MAX(YD_STK_LYR_NO),0) + 1, 'FM000')  FROM TB_YD_STKLYR WHERE YD_STK_COL_GP = SB.YD_STK_COL_GP AND YD_STK_BED_NO = SB.YD_STK_BED_NO AND YD_STK_LYR_MTL_STAT IN ('C', 'U'))) AS YD_DN_WR_LAYER
				      ,MIN(SB.YD_STK_BED_XAXIS) AS YD_DN_WR_XAXIS
				      ,MIN(SB.YD_STK_BED_YAXIS) AS YD_DN_WR_YAXIS
				      ,SUM(CASE WHEN SL.YD_STK_LYR_MTL_STAT IN ('C','U') THEN ST.YD_MTL_T ELSE 0 END) AS YD_DN_WR_ZAXIS
				      ,MIN((SELECT DECODE(MAX(CS.YD_CRN_SCH_ID),NULL,'Y',:V_YD_CRN_SCH_ID,'Y','N')
				              FROM TB_YD_CRNSCH CS
				             WHERE CS.YD_WBOOK_ID = :V_YD_WBOOK_ID
				               AND CS.DEL_YN      = 'N')) AS WB_CMPL_YN --작업예약 완료여부
				  FROM TB_YD_STKBED SB
				      ,TB_YD_STKLYR SL
				      ,TB_YD_STOCK  ST
				 WHERE SB.YD_STK_COL_GP = SL.YD_STK_COL_GP(+)
				   AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO(+)
				   AND SL.STL_NO        = ST.STL_NO(+)
				   AND SB.YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND SB.YD_STK_BED_NO = :V_YD_STK_BED_NO
			 */
			jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL009Curr", logId, methodNm, "현재정보  조회");
    	
			if (jsChk.size() > 0) {
				jrChk = jsChk.getRecord(0);
				wbCmplYn = slabUtils.trim(jrChk.getFieldString("WB_CMPL_YN"));
				String tbDnWrLayer = slabUtils.trim(jrChk.getFieldString("YD_DN_WR_LAYER"));
				slabUtils.printLog(logId, "권하위치[" + ydDnWrLoc + "] 적치단 변경 : " + ydDnWrLayer + " -> " + tbDnWrLayer, "SL");
				
				slabUtils.printLog(logId, "작업예약[" + ydWbookId + "] 완료여부 : " + wbCmplYn, "SL");
				if (!ydDnWrLayer.equals(tbDnWrLayer)) {
					chgDnWrLayer = true;
					ydDnWrLayer  = tbDnWrLayer;
					ydCrnZaxis   = "";
				}
				
				if ("".equals(ydCrnXaxis) || "".equals(ydCrnYaxis) || "".equals(ydCrnZaxis)) {
					ydCrnXaxis = slabUtils.trim(jrChk.getFieldString("YD_DN_WR_XAXIS"));
					ydCrnYaxis = slabUtils.trim(jrChk.getFieldString("YD_DN_WR_YAXIS"));
					ydCrnZaxis = slabUtils.trim(jrChk.getFieldString("YD_DN_WR_ZAXIS"));
				}
			} else {
				resMsg.setField("YD_L3_HD_RS_CD", "DN14"); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , "오류:권하위치 DB정보 없음"); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			//야드권하작업수행구분
			String ydDnWrkActGp = ydEqpWrkMode;
			
			if ("0".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "M"; //Manual
			} else if("1".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "A"; //Auto
			} else if("4".equals(ydEqpWrkMode)) {//일시정지 일때
				ydDnWrkActGp = "B"; //Backup
			}
			
			slabUtils.printLog(logId, "-----> 크레인스케쥴ID Check 완료","SL");

			
			/**********************************************************
			* 3. 전문 전송
			* 3.1 생산통제 장입진행실적(후판:YDCTJ031UD)			
			**********************************************************/
			String currDt = slabUtils.getDateTime14(); //현재시각
			
			//크레인스케쥴
			jrParam.setField("YD_DN_CMPL_DT"   , currDt      ); //야드권하완료일시
			jrParam.setField("YD_DN_WR_LOC"    , ydDnWrLoc   ); //야드권하실적위치
			jrParam.setField("YD_DN_WR_LAYER"  , ydDnWrLayer ); //야드권하실적단
			jrParam.setField("YD_DN_WRK_ACT_GP", ydDnWrkActGp); //야드권하작업수행구분
			jrParam.setField("YD_DN_WR_XAXIS"  , ydCrnXaxis  ); //야드권하실적X축
			jrParam.setField("YD_DN_WR_YAXIS"  , ydCrnYaxis  ); //야드권하실적Y축
			jrParam.setField("YD_DN_WR_ZAXIS"  , ydCrnZaxis  ); //야드권하실적Z축
			jrParam.setField("WR_DT"           , currDt      ); //실적일시
			jrParam.setField("UP_DN_GP"        , "D"         ); //권상권하구분(권하)

			//생산통제 장입진행실적(권상권하)
			jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDCTJ031UD", jrParam));

			
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
			*   - 대차작업실적(YDY3L007) 전송 : 후판Slab야드L2 전문 없음
			*   -  후판Slab야드L2 대차출발지시(YDY3L006) 전송 : 상차완료 시
			**********************************************************/
			slabUtils.printLog(logId, ydDnWrLoc+"-----> ydDnWrLoc","SL");
			if ("TC".equals(ydDnWrLoc.substring(2, 4))) {
				/***********************
				 * 대차 하차전에 상태 체크
				 *********************/
				
				//야드작업계획대차
				jrParam.setField("YD_WRK_PLAN_TCAR", ydDnWrLoc.substring(0, 1) + "X" + ydDnWrLoc.substring(2, 6));

				//대차상차스케쥴 조회
				/*
				 * 크레인권하실적 상차 대차스케줄 조회
					SELECT TS.YD_TCAR_SCH_ID
					      ,CASE WHEN WB.TC_SCH_YN = 'N' THEN 'Y' ELSE
					            CASE WHEN NVL(WB.WRKBOOKMTL_SH,0) - NVL(WB.CRNWRKMTL_SH,0) -
					                      NVL((SELECT COUNT(*)
					                             FROM TB_YD_TCARFTMVMTL TM
					                            WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
					                              AND TM.DEL_YN = 'N'),0) > 0 THEN 'N' ELSE 'Y' END
					       END AS TCAR_LD_CMPL_YN --대차상차완료여부
					  FROM TB_YD_TCARSCH TS
					      ,(SELECT DECODE(SUBSTR(WB.YD_SCH_CD,3,2),'TC','Y','N') AS TC_SCH_YN --대차스케줄여부
					              ,(SELECT COUNT(*)
					                  FROM TB_YD_WRKBOOKMTL WM
					                 WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
					                   AND WM.DEL_YN = 'N') AS WRKBOOKMTL_SH
					              ,(SELECT COUNT(*)
					                  FROM TB_YD_CRNWRKMTL CM
					                 WHERE CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					                   AND CM.DEL_YN = 'N') AS CRNWRKMTL_SH
					          FROM TB_YD_WRKBOOK WB
					         WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID) WB
					 WHERE TS.YD_EQP_ID = :V_YD_WRK_PLAN_TCAR
					   AND TS.DEL_YN    = 'N'
				 */
				jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL009TcarSchLd", logId, methodNm, "상차 대차스케줄  조회");
	    		
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					jrParam.setField("YD_TCAR_SCH_ID", slabUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"))); //야드대차스케쥴ID(이력등록시에도 사용)
					String tcarLdCmplYn = slabUtils.trim(jrChk.getFieldString("TCAR_LD_CMPL_YN")); //대차상차완료여부

					//작업예약 야드작업계획대차 수정
					/*
					 * 크레인권하실적 작업예약 대차 수정
						UPDATE TB_YD_WRKBOOK
						   SET MODIFIER         = :V_MODIFIER
							  ,MOD_DDTT         = SYSDATE
						      ,YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
						 WHERE YD_WBOOK_ID      = :V_YD_WBOOK_ID
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL009WbTcar", logId, methodNm, "작업예약(TB_YD_WRKBOOK) 대차 수정");				
					
					//대차이송재료 등록
					/*
					 * 크레인권하실적 대차이송재료 등록
						MERGE INTO TB_YD_TCARFTMVMTL TM USING (
						SELECT :V_YD_TCAR_SCH_ID AS YD_TCAR_SCH_ID
						      ,CM.STL_NO
						      ,:V_MODIFIER       AS MODIFIER
						      ,SYSDATE           AS MOD_DDTT
						      ,'N'               AS DEL_YN
						      ,'01'              AS YD_STK_BED_NO
						      ,TO_CHAR((SELECT COUNT(*)
						                  FROM TB_YD_TCARFTMVMTL
						                 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
						                   AND DEL_YN         = 'N') + TO_NUMBER(CM.YD_STK_LYR_NO),'FM000') AS YD_STK_LYR_NO
						      ,ST.HCR_GP
						      ,ST.STL_PROG_CD
						      ,ST.YD_MTL_ITEM
						      ,ST.YD_AIM_RT_GP   AS YD_ROUTE_GP
						  FROM TB_YD_CRNWRKMTL CM
						      ,TB_YD_STOCK     ST
						 WHERE CM.STL_NO        = ST.STL_NO
						   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						   AND CM.DEL_YN        = 'N'
						) DD ON (TM.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID AND TM.STL_NO = DD.STL_NO)
						WHEN NOT MATCHED THEN
						INSERT (TM.YD_TCAR_SCH_ID, TM.STL_NO  , TM.REGISTER   , TM.REG_DDTT     ,
						        TM.MODIFIER      , TM.MOD_DDTT, TM.DEL_YN     , TM.YD_STK_BED_NO,
						        TM.YD_STK_LYR_NO , TM.HCR_GP  , TM.STL_PROG_CD, TM.YD_MTL_ITEM  , TM.YD_ROUTE_GP)
						VALUES (DD.YD_TCAR_SCH_ID, DD.STL_NO  , DD.MODIFIER   , DD.MOD_DDTT     ,
						        DD.MODIFIER      , DD.MOD_DDTT, DD.DEL_YN     , DD.YD_STK_BED_NO,
						        DD.YD_STK_LYR_NO , DD.HCR_GP  , DD.STL_PROG_CD, DD.YD_MTL_ITEM  , DD.YD_ROUTE_GP)
					 */
					commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL009TcarMtlIns", logId, methodNm, "대차이송재료(TB_YD_TCARFTMVMTL) 상차 등록");				
					
					if ("N".equals(tcarLdCmplYn)) {
						//상차완료가 아니면
						jrParam.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					} else {
						//상차완료이면
						jrParam.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)

						//대차스케줄 처리 (영대차출발지시)
						//야드하차작업예약ID 생성
						String ydCarudWrkBookId = commDao.getSeqId(logId, methodNm, "WrkBook");
						
						if ("".equals(ydCarudWrkBookId)) {
							ydL3Msg = "오류:대차작업예약ID 생성 실패";
							resMsg.setField("YD_L3_HD_RS_CD", "DN21" ); //야드L3처리결과코드
							resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
							throw new Exception(ydL3Msg);
						}

						//작업예약 등록
						jrParam.setField("YD_CARUD_WRK_BOOK_ID", ydCarudWrkBookId); //야드하차작업예약ID
						jrParam.setField("YD_SCH_ST_GP"        , ydDnWrkActGp    ); //야드스케쥴기동구분

						/*
						 * 크레인권하실적 대차 작업예약 등록 
							MERGE INTO TB_YD_WRKBOOK WB USING (
							SELECT :V_YD_CARUD_WRK_BOOK_ID AS YD_WBOOK_ID      --야드작업예약ID
							      ,:V_MODIFIER             AS MODIFIER         --수정자
							      ,SYSDATE                 AS MOD_DDTT         --수정일시
							      ,'N'                     AS DEL_YN           --삭제유무
							      ,WB.YD_GP                                    --야드구분
							      ,WB.YD_BAY_GP                                --야드동구분
							      ,WB.YD_SCH_CD                                --야드스케쥴코드
							      ,(SELECT SR.YD_WRK_CRN_PRIOR
							          FROM TB_YD_SCHRULE SR
							         WHERE SR.YD_SCH_CD = WB.YD_SCH_CD) AS YD_SCH_PRIOR --야드스케쥴우선순위
							      ,'W'                     AS YD_SCH_PROG_STAT --야드스케쥴진행상태(스케줄수행대기)
							      ,:V_YD_SCH_ST_GP         AS YD_SCH_ST_GP     --야드스케쥴기동구분
							      ,'1'                     AS YD_SCH_REQ_GP    --야드스케쥴요청구분(대차상차완료)
							      ,WB.YD_TO_LOC_DCSN_MTD                       --야드To위치결정방법
							      ,WB.YD_TO_LOC_GUIDE                          --야드To위치Guide
							      ,WB.YD_WRK_PLAN_TCAR                         --야드작업계획대차
							  FROM (SELECT WB.YD_GP
							              ,WB.YD_AIM_BAY_GP AS YD_BAY_GP
							              ,WB.YD_GP||WB.YD_AIM_BAY_GP||SUBSTR(WB.YD_WRK_PLAN_TCAR,3)||'LM' AS YD_SCH_CD
							              ,CASE WHEN WB.YD_TO_LOC_DCSN_MTD = 'F' AND WB.YD_AIM_BAY_GP != SUBSTR(WB.YD_TO_LOC_GUIDE,2,1)
							                    THEN 'S' ELSE WB.YD_TO_LOC_DCSN_MTD END AS YD_TO_LOC_DCSN_MTD
							              ,CASE WHEN WB.YD_TO_LOC_DCSN_MTD = 'F' AND WB.YD_AIM_BAY_GP != SUBSTR(WB.YD_TO_LOC_GUIDE,2,1)
							                    THEN ''  ELSE WB.YD_TO_LOC_GUIDE    END AS YD_TO_LOC_GUIDE
							              ,WB.YD_WRK_PLAN_TCAR
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
						commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL009WbTcarIns", logId, methodNm, "작업예약 등록(TB_YD_WRKBOOK) 대차하차 등록");				
						
						//작업예약재료 등록
						/*
						 * 크레인권하실적 대차 작업예약재료 등록 
							MERGE INTO TB_YD_WRKBOOKMTL WM USING (
							SELECT :V_YD_CARUD_WRK_BOOK_ID AS YD_WBOOK_ID
							      ,TM.STL_NO
							      ,:V_MODIFIER             AS MODIFIER
							      ,SYSDATE                 AS MOD_DDTT
							      ,'N'                     AS DEL_YN
							      ,WB.YD_STK_COL_GP
							      ,TM.YD_STK_BED_NO
							      ,TM.YD_STK_LYR_NO
							      ,COUNT(*) OVER () - ROW_NUMBER() OVER (ORDER BY YD_STK_LYR_NO) + 1 AS YD_UP_COLL_SEQ
							  FROM TB_YD_TCARFTMVMTL TM
							      ,(SELECT WB.YD_GP||WB.YD_AIM_BAY_GP||SUBSTR(WB.YD_WRK_PLAN_TCAR,3) AS YD_STK_COL_GP
							          FROM TB_YD_WRKBOOK WB
							         WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID) WB
							 WHERE TM.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
							   AND TM.DEL_YN         = 'N'
							) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.STL_NO = DD.STL_NO)
							WHEN NOT MATCHED THEN
							INSERT (WM.YD_WBOOK_ID  , WM.STL_NO       , WM.REGISTER      , WM.REG_DDTT     ,
							        WM.MODIFIER     , WM.MOD_DDTT     , WM.DEL_YN        , WM.YD_STK_COL_GP,
							        WM.YD_STK_BED_NO, WM.YD_STK_LYR_NO, WM.YD_UP_COLL_SEQ)
							VALUES (DD.YD_WBOOK_ID  , DD.STL_NO       , DD.MODIFIER      , DD.MOD_DDTT     ,
							        DD.MODIFIER     , DD.MOD_DDTT     , DD.DEL_YN        , DD.YD_STK_COL_GP,
							        DD.YD_STK_BED_NO, DD.YD_STK_LYR_NO, DD.YD_UP_COLL_SEQ)
						 */
						commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL009WbMtlTcarIns", logId, methodNm, "작업예약재료(TB_YD_WRKBOOKMTL) 대차하차 등록");				
						
					}

					//대차스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
					/*
					 * 크레인권하실적 상차 대차스케줄 수정 
						MERGE INTO TB_YD_TCARSCH TS USING (
						SELECT TM.YD_TCAR_SCH_ID
						      ,:V_MODIFIER              AS MODIFIER
						      ,:V_YD_CAR_PROG_STAT      AS YD_CAR_PROG_STAT
						      ,TM.YD_EQP_WRK_SH
						      ,TM.YD_EQP_WRK_WT
						      ,:V_YD_WBOOK_ID           AS YD_CARLD_WRK_BOOK_ID
						      ,:V_YD_STK_COL_GP         AS YD_CARLD_STOP_LOC
						      ,:V_YD_CARLD_WRK_CRN      AS YD_CARLD_WRK_CRN
						      ,NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE) AS WR_DT
						      ,WB.YD_WBOOK_ID           AS YD_CARUD_WRK_BOOK_ID
						      ,SUBSTR(WB.YD_SCH_CD,1,6) AS YD_CARUD_STOP_LOC
						  FROM TB_YD_WRKBOOK WB
						      ,(SELECT TM.YD_TCAR_SCH_ID
						              ,COUNT(*)                AS YD_EQP_WRK_SH
						              ,SUM(ST.YD_MTL_WT)       AS YD_EQP_WRK_WT
						              ,:V_YD_CARUD_WRK_BOOK_ID AS YD_CARUD_WRK_BOOK_ID
						          FROM TB_YD_TCARFTMVMTL TM
						              ,TB_YD_STOCK       ST
						         WHERE TM.STL_NO         = ST.STL_NO
						           AND TM.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
						           AND TM.DEL_YN         = 'N'
						         GROUP BY TM.YD_TCAR_SCH_ID) TM
						  WHERE TM.YD_CARUD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
						) DD ON (TS.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID)
						WHEN MATCHED THEN UPDATE SET
							 TS.MODIFIER             = DD.MODIFIER
						    ,TS.MOD_DDTT             = SYSDATE
						    ,TS.YD_EQP_WRK_STAT      = 'L' --영차
						    ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT
						    ,TS.YD_EQP_WRK_SH        = DD.YD_EQP_WRK_SH
						    ,TS.YD_EQP_WRK_WT        = DD.YD_EQP_WRK_WT
						    ,TS.YD_CARLD_WRK_BOOK_ID = DD.YD_CARLD_WRK_BOOK_ID
						    ,TS.YD_CARLD_STOP_LOC    = DD.YD_CARLD_STOP_LOC
						    ,TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT,DD.WR_DT)
						    ,TS.YD_CARLD_CMPL_DT     = DECODE(DD.YD_CAR_PROG_STAT,'5',DD.WR_DT,NULL)
						    ,TS.YD_CARLD_WRK_CRN     = DD.YD_CARLD_WRK_CRN
						    ,TS.YD_CARUD_WRK_BOOK_ID = DD.YD_CARUD_WRK_BOOK_ID
						    ,TS.YD_CARUD_STOP_LOC    = DD.YD_CARUD_STOP_LOC
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL009TcarSchLd", logId, methodNm, "대차스케줄(TB_YD_TCARSCH) 상차 수정");				
					
					// 후판Slab야드L2 영대차출발지시
					if ("Y".equals(tcarLdCmplYn)) {
						jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L006", jrParam));
					}						
				}
			
			}
			//2023.01.13. MES_PI 수정사항. 2후판재 1후판 추출 -> 2후판 대차 작업시에도 이송실적 연계를 위한 추가////////////////////////////////////////////////
			/**********************************************************
			* 4-1. 권상실적위치가 대차(하차)
			* 4-1.1 생산통계 이송실적전문 전송. 
	
			**********************************************************/
			slabUtils.printLog(logId, ydUpWrLoc+"-----> ydUpWrLoc","SL");
			if (ydUpWrLoc.length() > 4 && "TC".equals(ydUpWrLoc.substring(2, 4))) {
//				String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "슬라브 이동실적", "APPPI1", "*", "*");
//				if("Y".equals(sApplyYnPI)) {
					String fromLoc    = "";//발지코드
					String toLoc      = "";//착지코드
					String hdsIniDate = "";//계상일자
					
					//A동 대차하차시 S200(2후판) -> S100(1후판) 이송실적
					if("DATC".equals(ydUpWrLoc.substring(0,4))){
						fromLoc = "S200";
						toLoc = "S100";
					}
					//B동 대차하차시 S100(1후판) -> S200(2후판) 이송실적
					else if("DBTC".equals(ydUpWrLoc.substring(0,4))){
						fromLoc = "S100";
						toLoc = "S200";
					}
					hdsIniDate = slabUtils.getDefaultHdsDate();
					
					jrParam.setField("FROM_LOC"          , fromLoc                    ); //상차위치
					jrParam.setField("TO_LOC"            , toLoc                     ); //하차위치
					jrParam.setField("MODIFIER"          ,sModifier             	); //실행프로그램
					jrParam.setField("PGM_ID"            , "rcvY3YDL009"             ); //실행프로그램
					jrParam.setField("ERP_HDS_DD"        , hdsIniDate                ); //계상일자
					
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDSSJ001", jrParam));						
//				}
			}
			//2023.01.13. MES_PI 수정사항. 2후판재 1후판 추출 -> 2후판 대차 작업시에도 이송실적 연계를 위한 추가END////////////////////////////////////////////////
			
			
			String sDYD201_YN28 = "";
			/* com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYnDualSql
			SELECT 'N' AS APPLY_YN28 -- 권하실적처리시 작업지시 송신 여부 
			  FROM DUAL
			*/  
			JDTORecordSet jsApplyYNChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYnDualSql", logId, methodNm, "열정보 Read"); 

			if (jsApplyYNChk.size() > 0) {
				sDYD201_YN28    = slabUtils.trim(jsApplyYNChk.getRecord(0).getFieldString("APPLY_YN28"));
			}

			/**********************************************************
			* 5. 권하실적위치가 차량(상차)
			* 5.1 차량이송재료 등록
			* 5.2 차량스케줄 야드차량진행상태, 야드설비작업상태(L:영차), 야드설비작업매수 등 수정
			*   - 직상차(차량 스케줄코드가 아님) : 야드차량진행상태(4:상차개시)
			*   - 상차완료(차량 스케줄코드가 아님) : 야드차량진행상태(5:상차완료)
			*   - 야드차량사용구분이 구내운송(L)이고 상차완료이면
			*     . 권상실적위치가 ADPUP1 이고 차량 상차매수 4매가 안되면 Skip
			*     . 권상실적위치가 ADPUP1 이고 차량 상차매수 4매 이상이거나
			*     . 마지막 크레인스케줄 이면
			* 5.3 공통 처리 : 야드차량사용구분이 구내운송(L)이고 상차완료이면
			* 5.3.1 주편, Slab 공통 Table 소재이송일시 수정
			* 5.3.2 소재이송지시 야드재료예정저장From위치코드, 이송상차일자 수정
			* 5.3.3 저장품 목표야드, 목표동, 목표행선 등을 수정
			* 5.4 야드차량사용구분이 출하차량(G)
			
			**********************************************************/
			//차량상차완료여부(소재이송지시 수정 및 공통Table 소재이송일시 수정)
			String carLdCmplYn = "N";

			if("PT".equals(ydDnWrLoc.substring(2, 4))) {
				//차량상차스케줄 정보 조회
				/*크레인권하실적 상차 차량스케줄 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL009CarSchLd*/
				/*
				 * SELECT *
				 FROM (
				SELECT TS.YD_CAR_SCH_ID
				      ,TS.YD_CAR_USE_GP
				      ,CASE WHEN TS.YD_CAR_USE_GP = 'L' AND CS.DIR_LD_YN = 'Y' THEN
				            CASE WHEN NVL(CS.CRNWRKMTL_SH,0) +
				                      NVL((SELECT COUNT(*)
				                             FROM TB_YD_CARFTMVMTL TM
				                            WHERE TM.YD_CAR_SCH_ID = TS.YD_CAR_SCH_ID
				                              AND TM.DEL_YN = 'N'),0) >= 4 THEN 'Y'
				            ELSE 'N' END
				            
				            WHEN YD_SCH_CD like 'A_YD__MM' THEN 'N'--이적상차시 상차완료여부 알 수 없다(연주야드 상차완료TC자동전송 방지)
				       ELSE CS.CAR_LD_CMPL_YN
				       END AS CAR_LD_CMPL_YN --차량상차완료여부
				       ,TS.TRN_EQP_CD
				  FROM TB_YD_STKCOL SC
				      ,TB_YD_CARSCH TS
				      ,(SELECT CS.YD_CRN_SCH_ID
				              ,(SELECT DECODE(MAX(CA.YD_CRN_SCH_ID),NULL,'Y',CS.YD_CRN_SCH_ID,'Y','N')
				                  FROM TB_YD_CRNSCH CA
				                 WHERE CA.YD_WBOOK_ID = CS.YD_WBOOK_ID
				                   AND CA.DEL_YN      = 'N'
				                   AND CA.YD_DN_WO_LOC LIKE '__PT%') AS CAR_LD_CMPL_YN
				              ,CASE WHEN CS.YD_UP_WR_LOC LIKE 'ADPUP1%' THEN 'Y' ELSE 'N' END AS DIR_LD_YN --직상차여부
				              ,(SELECT COUNT(*)
				                  FROM TB_YD_CRNWRKMTL CM
				                 WHERE CM.YD_CRN_SCH_ID = CS.YD_CRN_SCH_ID
				                   AND CM.DEL_YN = 'N') AS CRNWRKMTL_SH
				              ,YD_SCH_CD
				          FROM TB_YD_CRNSCH CS
				         WHERE CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID) CS
				 WHERE SC.YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
				   AND ((SC.YD_CAR_USE_GP = 'L' AND SC.TRN_EQP_CD = TS.TRN_EQP_CD) --구내운송
				     OR (SC.YD_CAR_USE_GP = 'G' AND SC.CARD_NO = TS.CARD_NO AND SC.CAR_NO = TS.CAR_NO)) --출하차량
				   AND TS.DEL_YN = 'N'
				ORDER BY YD_CAR_SCH_ID DESC
				) A
				WHERE ROWNUM<=1 
				*/
				
				jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL009CarSchLd", logId, methodNm, "상차 차량스케줄  조회");
				
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					jrParam.setField("YD_CAR_SCH_ID", slabUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
					String ydCarUseGp = slabUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP")); //야드차량사용구분
					carLdCmplYn = slabUtils.trim(jrChk.getFieldString("CAR_LD_CMPL_YN")); //차량상차완료여부
					String trnEqpCd = slabUtils.trim(jrChk.getFieldString("TRN_EQP_CD")); //장비번호
					
					//차량이송재료 등록
					/* com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL009CarMtlIns
					MERGE INTO TB_YD_CARFTMVMTL TM USING (
					SELECT :V_YD_CAR_SCH_ID AS YD_CAR_SCH_ID
					      ,CM.STL_NO
					      ,:V_MODIFIER      AS MODIFIER
					      ,SYSDATE          AS MOD_DDTT
					      ,'N'              AS DEL_YN
					--      ,'01'             AS YD_STK_BED_NO
					--      ,TO_CHAR((SELECT COUNT(*)
					--                  FROM TB_YD_CARFTMVMTL
					--                 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					--                   AND DEL_YN         = 'N') + TO_NUMBER(CM.YD_STK_LYR_NO),'FM000') AS YD_STK_LYR_NO
					      ,SUBSTR(:V_YD_DN_WR_LOC,7,2) AS YD_STK_BED_NO
					      ,TO_CHAR((SELECT COUNT(*)
					                  FROM TB_YD_CARFTMVMTL
					                 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					                   AND YD_STK_BED_NO = SUBSTR(:V_YD_DN_WR_LOC,7,2)
					                   AND DEL_YN         = 'N') + TO_NUMBER(CM.YD_STK_LYR_NO),'FM000') AS YD_STK_LYR_NO

					      ,ST.HCR_GP
					      ,ST.STL_PROG_CD
					      ,ST.YD_MTL_ITEM
					      ,ST.YD_AIM_RT_GP  AS YD_ROUTE_GP
					  FROM TB_YD_CRNWRKMTL CM
					      ,TB_YD_STOCK     ST
					 WHERE CM.STL_NO        = ST.STL_NO
					   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					   AND CM.DEL_YN        = 'N'
					) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.STL_NO = DD.STL_NO)
					WHEN NOT MATCHED THEN
					INSERT (TM.YD_CAR_SCH_ID, TM.STL_NO  , TM.REGISTER   , TM.REG_DDTT     ,
					        TM.MODIFIER     , TM.MOD_DDTT, TM.DEL_YN     , TM.YD_STK_BED_NO,
					        TM.YD_STK_LYR_NO, TM.HCR_GP  , TM.STL_PROG_CD, TM.YD_MTL_ITEM  , TM.YD_ROUTE_GP)
					VALUES (DD.YD_CAR_SCH_ID, DD.STL_NO  , DD.MODIFIER   , DD.MOD_DDTT     ,
					        DD.MODIFIER     , DD.MOD_DDTT, DD.DEL_YN     , DD.YD_STK_BED_NO,
					        DD.YD_STK_LYR_NO, DD.HCR_GP  , DD.STL_PROG_CD, DD.YD_MTL_ITEM  , DD.YD_ROUTE_GP)
					 */
					commDao.insertTx(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL009CarMtlIns", logId, methodNm, "차량이송재료(TB_YD_CARFTMVMTL) 상차 등록");				
					
					if(PSlabYdConstant.APPLY_YN.equals(sDYD201_YN28) ) { //변경후 
						//변경_start 2021-07-21
						jrParam.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
						/*크레인권하실적 상차 차량스케줄 수정 */
						/*
						 * MERGE INTO TB_YD_CARSCH TS USING (					 
						SELECT TM.YD_CAR_SCH_ID
						      ,:V_YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT
						      ,COUNT(*)            AS YD_EQP_WRK_SH
						      ,SUM(ST.YD_MTL_WT)   AS YD_EQP_WRK_WT
						      ,:V_YD_WBOOK_ID      AS YD_WBOOK_ID
						      ,:V_YD_STK_COL_GP    AS YD_CARLD_STOP_LOC
						      ,NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE) AS WR_DT
						      ,SF_SLAB_YD_ARR_WLOC_CD(MIN(ST.YD_AIM_YD_GP)) AS ARR_WLOC_CD
						  FROM TB_YD_CARFTMVMTL TM
						      ,TB_YD_STOCK      ST
						 WHERE TM.STL_NO        = ST.STL_NO
						   AND TM.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
						   AND TM.DEL_YN        = 'N'
						 GROUP BY TM.YD_CAR_SCH_ID
						) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
						WHEN MATCHED THEN UPDATE SET
						     TS.MODIFIER             = :V_MODIFIER
						    ,TS.MOD_DDTT             = SYSDATE
						    ,TS.YD_EQP_WRK_STAT      = 'L' --영차
						    ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT
						    ,TS.YD_EQP_WRK_SH        = DD.YD_EQP_WRK_SH
						    ,TS.YD_EQP_WRK_WT        = DD.YD_EQP_WRK_WT
						    ,TS.ARR_WLOC_CD          = NVL(TS.ARR_WLOC_CD,DD.ARR_WLOC_CD)
						    ,TS.YD_CARLD_WRK_BOOK_ID = DD.YD_WBOOK_ID
						    ,TS.YD_CARLD_STOP_LOC    = DD.YD_CARLD_STOP_LOC
						    ,TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT,DD.WR_DT)
						    ,TS.YD_CARLD_CMPL_DT     = DECODE(DD.YD_CAR_PROG_STAT,'5',DD.WR_DT,NULL)
						*/
						commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL009CarSchLd", logId, methodNm, "차량스케줄(TB_YD_CARSCH) 상차 수정");				
						
						/*
						//구내운송 상차완료 TC 자동전송
						//YDTSJ008	소재차량상차완료
						if ("L".equals(ydCarUseGp) && "D".equals(ydDnWrLoc.substring(0, 1)) && "Y".equals(carLdCmplYn)) { 
							
							recInTemp.setField("MSG_ID",        "YDTSJ008");
			    			recInTemp.setField("YD_CAR_SCH_ID", slabUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); 
			    			//ydDelegate.sendMsg(recInTemp);
			    			// 소재차량상차완료 처리는 차량관리 화면에서 처리 하기로 함
			    			// 2021-01-07 염용선
			    			jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ008", recInTemp));
			    			
						}
						*/
	//변경_end 2021-07-21	
					} else {		//기존로직 

						//차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
						if ("Y".equals(carLdCmplYn)) {
							jrParam.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
							jrParam.setField("TRN_EQP_CD"		, trnEqpCd );
							
							//----------추가: 상차처리시 차상위치가 2Bed인 경우 ---------------------------------
							// --크레인 스케줄에 해당 예약 ID 로  --> 예약 정보의 예약 스케줄ID 조회 --> 해당 스케줄ID로  조회
							//
							/* com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getYdPrepschPricnt  
							SELECT YD_PREP_SCH_ID, WB.YD_WBOOK_ID, WB.TRN_EQP_CD
							  FROM TB_YD_CARSCH CS
							     ,(SELECT WB.YD_WBOOK_ID, WB.TRN_EQP_CD
							         FROM USRYDA.TB_YD_WRKBOOK    WB
							        WHERE WB.DEL_YN = 'N'
							      ) WB
							     , TB_YD_PREPSCH PS 
							  WHERE CS.DEL_YN = 'N'
							    AND CS.TRN_EQP_CD  = WB.TRN_EQP_CD 
							    AND CS.TRN_EQP_CD  = :V_TRN_EQP_CD     
							    AND WB.YD_WBOOK_ID = PS.YD_WBOOK_ID 
							    AND WB.YD_WBOOK_ID NOT IN   --PS.YD_WBOOK_ID 
							         ( SELECT YD_WBOOK_ID   FROM TB_YD_CRNSCH
							           WHERE DEL_YN = 'N'   AND YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID )
							 */       
							JDTORecordSet outRecSet = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getYdPrepschPricnt", logId, methodNm, "조회");
							if (outRecSet.size() > 0 ) {
								jrParam.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시-ii)
								 
								carLdCmplYn = "";
							}
							//--------------------------------------------------------------------
						} else {
							jrParam.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
						}


					}
					
				}
			}
			
			/**********************************************************
			* 6. 권상실적위치가 차량(하차)
			*    마지막 스케줄이면(차량이송재료 없음)
			* 6.1 차량스케줄 야드차량진행상태(E:하차완료), 야드설비작업상태(U:공차) 등 수정
			* 6.2 생산통제 이송하차실적(YDCTJ034) 전송
			*   - 재료외형구분이 Slab이고 Slab지시행선이 PA,PB 이고 재열재구분이 1,2
			* 6.3 후판조업 후판재열재슬라브적치실적(1후판:YDPRJ003, 2후판:YDPPJ003) 전송
			*   - 권상실적위치가 후판Slab야드이고 발지개소코드가 후판-극후물 냉각대(DKY23,DWY23) 
			* 6.4 구내운송 소재차량하차완료 송신(YDTSJ010) 전송
			* 6.5 권하실적재료 적치단 수정 후 등록하여야 하므로 하단부로 이동
			*   - 소재이송지시 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드 수정
			* 6.6 공통 Table 현재진도코드 변경 후 작업대상은 하단부로 이동
			*   - 주편, Slab 공통 Table 소재인수일시 수정
			*   - 진행관리 Slab이송완료실적(YDPTJ001) 전송
			**********************************************************/
			//차량하차여부(공통Table 소재인수일시 수정 및 진행관리 Slab이송완료실적 송신)
			String carUdCmplYn = "N";
			String trnEqpCd    = "";

			if ("PT".equals(ydUpWrLoc.substring(2, 4)) && !"PT".equals(ydDnWrLoc.substring(2, 4))) {
				//야드하차작업예약ID 차량하차스케줄 정보 조회
				/*--크레인권하실적 하차 차량스케줄 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL009CarSchUd
				SELECT TS.YD_CAR_SCH_ID
				      ,DECODE(COUNT(TM.STL_NO),0,'Y','N') AS CAR_UD_CMPL_YN --차량하차완료여부(권상실적에서 이송재료 삭제)
      				  ,MAX(TRN_EQP_CD) TRN_EQP_CD
				  FROM TB_YD_CARSCH     TS
				      ,TB_YD_CARFTMVMTL TM
				 WHERE TS.YD_CAR_SCH_ID        = TM.YD_CAR_SCH_ID(+)
				   AND TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				   AND TS.DEL_YN               = 'N'
				   AND TM.DEL_YN(+)            = 'N'
				 GROUP BY TS.YD_CAR_SCH_ID
				 */
				jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL009CarSchUd", logId, methodNm, "하차 차량스케줄  조회");
				
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					jrParam.setField("YD_CAR_SCH_ID", slabUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
					carUdCmplYn = slabUtils.trim(jrChk.getFieldString("CAR_UD_CMPL_YN")); //차량하차완료여부
					trnEqpCd    = slabUtils.trim(jrChk.getFieldString("TRN_EQP_CD"));
					//차량하차완료이면
					if ("Y".equals(carUdCmplYn)) {
						//하차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
						/*
						 * --크레인권하실적 하차 차량스케줄 수정 - 
						
						UPDATE TB_YD_CARSCH
						   SET MODIFIER         = :V_MODIFIER
							  ,MOD_DDTT         = SYSDATE
						      ,YD_EQP_WRK_STAT  = 'U' --공차
						      ,YD_CAR_PROG_STAT = 'E' --하차완료
						      ,YD_CARUD_CMPL_DT = NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
						 WHERE YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
						   AND DEL_YN           = 'N'
						*/
						commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updY3YDL009CarSchUd", logId, methodNm, "차량스케줄(TB_YD_CARSCH) 상차 수정");				
						
						//생산통제 이송하차실적
						jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDCTJ034", jrParam));
						//구내운송 소재차량하차완료
						jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ010", jrParam));
						//후판조업 후판재열재슬라브적치실적
						jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDPRJ003", jrParam));
					}
				} else {
					//야드하차작업예약ID가 없는 경우 권상실적수신에서 작업예약 재료번호로 차량하차스케줄ID를 조회하여 작업예약ID 등록
					slabUtils.printLog(logId, "권하실적 하차 차량스케줄이 없습니다.", "SL");
				} 
				
				String sDYD201_YN29 = "";
				/* com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYnDualSql
						SELECT 'Y' AS APPLY_YN29         FROM DUAL
				 */  
				jsApplyYNChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYnDualSql", logId, methodNm, "적용여부정보 Read"); 
				if (jsApplyYNChk.size() > 0) {
					sDYD201_YN29    = slabUtils.trim(jsApplyYNChk.getRecord(0).getFieldString("APPLY_YN29"));
				}
				//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// 다음 작업예약  존재 여부 확인후 있으면 다음 스케줄 기동 처리    2021-07-22 
				// 현재  구내운송차량번호가 같은 작업예약건이 있으면 기동 처리  (단, 다음 크레인작업이 없는 경우 )   
				//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				if(PSlabYdConstant.APPLY_YN.equals(sDYD201_YN29)) {
					
					slabUtils.printLog(logId, "++(하차시)다음 크레인작업이 없는 경우  다음 작업예약건의 스케줄 기동 처리+++++++", "SL");
					String ydWbookId2 = "";
					/*다음 작업예약건-com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL009WrkbookId 
							 WITH WBOOKID AS (
							 SELECT YD_WBOOK_ID
							   FROM TB_YD_WRKBOOK
							  WHERE DEL_YN  = 'N'
							    AND YD_GP        = 'D'
							    AND TRN_EQP_CD   = :V_TRN_EQP_CD 
							    AND YD_WBOOK_ID <> :V_YD_WBOOK_ID 
							 )
							 SELECT * 
							   FROM WBOOKID
							  WHERE YD_WBOOK_ID NOT IN  (
							        SELECT YD_WBOOK_ID 
							        FROM TB_YD_CRNSCH
							        WHERE DEL_YN  = 'N'
							        AND YD_WBOOK_ID IN (SELECT YD_WBOOK_ID FROM WBOOKID )
							 )*/					
					jrParam.setField("TRN_EQP_CD"		, trnEqpCd );
					jrParam.setField("TRN_EQP_CD"		, trnEqpCd );
					jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL009WrkbookId", logId, methodNm, "작업예약중다음크레인작업건");
					
					if(jsChk.size() > 0) {
						ydWbookId2    = slabUtils.trim(jsApplyYNChk.getRecord(0).getFieldString("YD_WBOOK_ID"));
						
						JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
						slabUtils.printLog(logId,  "크레인 스케쥴 호출 : YDYDJ401>>"+ ydSchCd +">"+ ydWbookId2 +" (크레인:" + ydEqpId+")", "SL");   
						
						//크레인 스케줄 기동  호출
						jrCrnSchMsg.setField("JMS_TC_CD"			, "YDYDJ401"); 
						jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, slabUtils.getDateTime14()); 	//JMSTC생성일시	
						jrCrnSchMsg.setField("YD_WBOOK_ID"  		, ydWbookId2); 					//작업예약ID
						jrCrnSchMsg.setField("YD_SCH_CD"  			, ydSchCd);  					//야드스케쥴코드
						jrCrnSchMsg.setField("YD_EQP_ID"  			, ydEqpId);  					//야드설비ID (예)DACRA1
						jrCrnSchMsg.setField("EJB_CALL_YN"			, "Y");							//EJBCall여부(신 크레인스케줄)
						
						jrRtn = slabUtils.addSndData(jrRtn, jrCrnSchMsg);
					}
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
			//설비(야드설비상태) 수정
			/*
			 *설비 상태 수정
				UPDATE TB_YD_EQP
				   SET MODIFIER    = :V_MODIFIER
				      ,MOD_DDTT    = SYSDATE
				      ,YD_EQP_STAT = :V_YD_EQP_STAT
				 WHERE YD_EQP_ID   = :V_YD_EQP_ID
				   AND DEL_YN      = 'N'
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatEqp", logId, methodNm, "설비(TB_YD_EQP) 야드설비상태 수정");				
			
			//적치단(크레인 및 권하위치) 수정
			/*
			 * --크레인권하실적 적치단(크레인,권하실적,권하지시) 수정 
				MERGE INTO TB_YD_STKLYR SL USING (
				--권하실적위치
				WITH CM AS (
				SELECT CS.YD_STK_COL_GP
				      ,CS.YD_STK_BED_NO
				      ,TO_CHAR(TO_NUMBER(CM.YD_STK_LYR_NO) + CS.YD_DN_WR_LAYER,'FM000') AS YD_STK_LYR_NO
				      ,CM.STL_NO
				      --작업예약의 크레인스케줄 중, 완료되지 않은 CRNSCH에 해당 재료 있으면 적치상태 권상대기
				      ,DECODE((SELECT COUNT(*)
				                  FROM TB_YD_CRNSCH CC
				                      ,TB_YD_CRNWRKMTL CN
				                 WHERE 1=1
				                   AND CC.YD_WBOOK_ID = CS.YD_WBOOK_ID
				                   AND CC.DEL_YN ='N'
				                   AND CC.YD_CRN_SCH_ID = CN.YD_CRN_SCH_ID
				                   AND CN.STL_NO = CM.STL_NO
				                   AND CC.YD_CRN_SCH_ID > CS.YD_CRN_SCH_ID),0,'C','U') AS YD_STK_LYR_MTL_STAT
				  FROM TB_YD_CRNWRKMTL CM
				      ,(SELECT DD.YD_STK_COL_GP
				              ,DD.YD_STK_BED_NO
				              ,DD.YD_DN_WR_LAYER
				              ,DD.YD_CRN_SCH_ID
				              ,CS.YD_CRN_SCH_ID AS YD_CRN_SCH_ID_NEXT
				              ,ROW_NUMBER() OVER (ORDER BY CS.YD_CRN_SCH_ID) AS RN
				              ,CS.YD_WBOOK_ID
				          FROM TB_YD_CRNSCH CS
				              ,(SELECT :V_YD_STK_COL_GP  AS YD_STK_COL_GP
				                      ,:V_YD_STK_BED_NO  AS YD_STK_BED_NO
				                      ,TO_NUMBER(:V_YD_DN_WR_LAYER) - 1 AS YD_DN_WR_LAYER
				                      ,:V_YD_CRN_SCH_ID  AS YD_CRN_SCH_ID
				                      ,:V_YD_WBOOK_ID    AS YD_WBOOK_ID
				                  FROM DUAL) DD
				         WHERE CS.YD_WBOOK_ID(+)   = DD.YD_WBOOK_ID
				           AND CS.YD_CRN_SCH_ID(+) > DD.YD_CRN_SCH_ID
				           AND CS.DEL_YN(+)        = 'N') CS
				 WHERE CM.YD_CRN_SCH_ID = CS.YD_CRN_SCH_ID
				   AND CM.DEL_YN = 'N'
				   AND CS.RN = 1
				)
				SELECT *
				  FROM (SELECT DD.*
				              ,ROW_NUMBER() OVER (PARTITION BY DD.YD_STK_COL_GP, DD.YD_STK_BED_NO, DD.YD_STK_LYR_NO ORDER BY DD.NO) AS RN
				          FROM (--크레인위치
				                SELECT YD_STK_COL_GP
				                      ,YD_STK_BED_NO
				                      ,YD_STK_LYR_NO
				                      ,NULL AS STL_NO
				                      ,'E'  AS YD_STK_LYR_MTL_STAT --적치가능
				                      ,1    AS NO
				                  FROM TB_YD_STKLYR
				                 WHERE YD_STK_COL_GP = :V_YD_EQP_ID
				                   AND YD_STK_BED_NO = '01'
				                   AND STL_NO IS NOT NULL
				                 UNION ALL
				                --권하실적위치
				                SELECT YD_STK_COL_GP
				                      ,YD_STK_BED_NO
				                      ,YD_STK_LYR_NO
				                      ,(CASE WHEN YD_STK_COL_GP='DART01' THEN NULL ELSE STL_NO END) AS STL_NO
				                      ,(CASE WHEN YD_STK_COL_GP='DART01' THEN 'E' ELSE YD_STK_LYR_MTL_STAT END) AS YD_STK_LYR_MTL_STAT 
				                      ,2    AS NO
				                  FROM CM
				                 UNION ALL
				                --권하지시위치(실적위치와 다를 경우 등)
				                SELECT SL.YD_STK_COL_GP
				                      ,SL.YD_STK_BED_NO
				                      ,SL.YD_STK_LYR_NO
				                      ,NULL AS STL_NO
				                      ,'E'  AS YD_STK_LYR_MTL_STAT --적치가능
				                      ,3    AS NO
				                  FROM CM
				                      ,TB_YD_STKLYR SL
				                 WHERE CM.STL_NO = SL.STL_NO
				                   AND CM.YD_STK_LYR_MTL_STAT IN ('C','D') --적치중,권하대기(권상대기 제외)
				                   AND SL.YD_STK_COL_GP NOT LIKE '__CR%'   --크레인제외
				                   AND (SL.YD_STK_COL_GP != CM.YD_STK_COL_GP
				                     OR SL.YD_STK_BED_NO != CM.YD_STK_BED_NO
				                     OR SL.YD_STK_LYR_NO != CM.YD_STK_LYR_NO)) DD)
				 WHERE RN = 1 --지시단과 실적단이 다를 경우 저장위치가 2개이상이되므로 추가
				) DD ON (SL.YD_STK_COL_GP = DD.YD_STK_COL_GP AND SL.YD_STK_BED_NO = DD.YD_STK_BED_NO AND SL.YD_STK_LYR_NO = DD.YD_STK_LYR_NO)
				WHEN MATCHED THEN UPDATE SET
				     SL.MODIFIER            = :V_MODIFIER
				    ,SL.MOD_DDTT            = SYSDATE
				    ,SL.STL_NO              = DD.STL_NO
				    ,SL.YD_STK_LYR_MTL_STAT = DD.YD_STK_LYR_MTL_STAT
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009StkLyr", logId, methodNm, "적치단(TB_YD_STKLYR) 권하위치 수정");				
			
			//크레인작업재료 삭제
			/*
			 * --크레인권하실적 크레인작업재료 삭제 - 
				UPDATE TB_YD_CRNWRKMTL
				   SET MODIFIER      = :V_MODIFIER
					  ,MOD_DDTT      = SYSDATE
				      ,DEL_YN        = 'Y'
				 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND DEL_YN        = 'N'
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009CrnMtl", logId, methodNm, "크레인작업재료(TB_YD_CRNWRKMTL) 삭제");				
			
			//크레인스케쥴 권하실적 수정 및 삭제
			/*
			 * --크레인권하실적 크레인스케줄 수정 - 
				MERGE INTO TB_YD_CRNSCH CS USING (
				SELECT :V_MODIFIER                                  AS MODIFIER
				      ,TO_DATE(:V_YD_DN_CMPL_DT,'YYYYMMDDHH24MISS') AS YD_DN_CMPL_DT
				      ,:V_YD_DN_WR_LOC                              AS YD_DN_WR_LOC
				      ,:V_YD_DN_WR_LAYER                            AS YD_DN_WR_LAYER
				      ,:V_YD_DN_WRK_ACT_GP                          AS YD_DN_WRK_ACT_GP
				      ,TO_NUMBER(:V_YD_DN_WR_XAXIS)                 AS YD_DN_WR_XAXIS
				      ,TO_NUMBER(:V_YD_DN_WR_YAXIS)                 AS YD_DN_WR_YAXIS
				      ,TO_NUMBER(:V_YD_DN_WR_ZAXIS)                 AS YD_DN_WR_ZAXIS
				      ,:V_YD_CRN_SCH_ID                             AS YD_CRN_SCH_ID
				      ,:V_YD_DN_WRK_MODE2                           AS YD_DN_WRK_MODE2 
				  FROM DUAL
				) DD ON (CS.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID)
				WHEN MATCHED THEN UPDATE SET
				     CS.MODIFIER         = DD.MODIFIER
				    ,CS.MOD_DDTT         = SYSDATE
				    ,CS.DEL_YN           = 'Y'
				    ,CS.YD_WRK_PROG_STAT = '4' --권하완료
				    ,CS.YD_WRK_HDS_DD    = SF_YD_WRK_HDS_DD(DD.YD_DN_CMPL_DT)
				    ,CS.YD_WRK_DUTY      = SF_YD_WRK_DUTY(DD.YD_DN_CMPL_DT)
				    ,CS.YD_WRK_PARTY     = SF_YD_WRK_PARTY(DD.YD_DN_CMPL_DT)
				    ,CS.YD_DN_CMPL_DT    = DD.YD_DN_CMPL_DT
				    ,CS.YD_DN_WR_LOC     = DD.YD_DN_WR_LOC
				    ,CS.YD_DN_WR_LAYER   = DD.YD_DN_WR_LAYER
				    ,CS.YD_DN_WRK_ACT_GP = DD.YD_DN_WRK_ACT_GP
				    ,CS.YD_DN_WR_XAXIS   = DD.YD_DN_WR_XAXIS
				    ,CS.YD_DN_WR_YAXIS   = DD.YD_DN_WR_YAXIS
				    ,CS.YD_DN_WR_ZAXIS   = DD.YD_DN_WR_ZAXIS
				    ,CS.YD_DN_WRK_MODE2  = DD.YD_DN_WRK_MODE2
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009CrnSch", logId, methodNm, "크레인스케줄(TB_YD_CRNSCH) 수정");				
			
			//작업예약완료 이면
			if ("Y".equals(wbCmplYn)) {
				//작업예약재료 삭제
				/*
				 * --크레인권하실적 작업예약재료 삭제 - 
					UPDATE TB_YD_WRKBOOKMTL
					   SET MODIFIER    = :V_MODIFIER
						  ,MOD_DDTT    = SYSDATE
					      ,DEL_YN      = 'Y'
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND DEL_YN      = 'N'
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009WbMtlDel", logId, methodNm, "작업예약재료(TB_YD_WRKBOOKMTL) 삭제");				
				
				//작업예약 수정 및 삭제
				/*
				 * --크레인권하실적 작업예약 삭제 - 
					UPDATE TB_YD_WRKBOOK
					   SET MODIFIER         = :V_MODIFIER
						  ,MOD_DDTT         = SYSDATE
					      ,DEL_YN           = 'Y'
					      ,YD_SCH_PROG_STAT = 'E' --End
					 WHERE YD_WBOOK_ID      = :V_YD_WBOOK_ID
					   AND DEL_YN           = 'N'
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009WbDel", logId, methodNm, "작업예약(TB_YD_WRKBOOK) 삭제");				
				
			}
			
			/**********************************************************
			* 8. 공통 Table, 저장품, 작업이력 등록 (순서 변경 안됨)
			* 8.1 주편 및 Slab 공통 Table 수정
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
			* 8.4.1 진행관리 Slab이송완료실적(YDPTJ001) 전송
			*     - 하차완료 시 차량이송재료 현재진도코드 수정 후
			* 8.4.1 소재이송지시 수정 : 권하실적재료 적치단 수정 후
			*     - 상차 : 이송상차일자, 야드재료예정저장From위치코드
			*     - 하차 : 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드
			* 8.4.2 주편 및 Slab 공통 Table 수정
			*     - 상차 : 소재이송일시
			*     - 하차 : 소재인수일시
			**********************************************************/
			//주편공통 수정
			/*
				-- com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getY3YDL009MslabComm
				-- 권하실적 후 주편 위치정보 갱신대상 조회
				SELECT CM.STL_NO
				      ,CS.YD_GP
				      ,CS.YD_BAY_GP
				      ,SUBSTR(CS.YD_DN_WR_LOC,3,2) AS YD_EQP_GP
				      ,SUBSTR(CS.YD_DN_WR_LOC,5,2) AS YD_STK_COL_NO
				      ,SUBSTR(CS.YD_DN_WR_LOC,7,2) AS YD_STK_BED_NO
				      ,TO_CHAR(TO_NUMBER(CM.YD_STK_LYR_NO) + TO_NUMBER(CS.YD_DN_WR_LAYER) - 1,'FM000') AS YD_STK_LYR_NO
				      ,CS.YD_DN_WR_LOC||TO_CHAR(TO_NUMBER(CM.YD_STK_LYR_NO) + TO_NUMBER(CS.YD_DN_WR_LAYER) - 1,'FM00') AS YD_STR_LOC
				      ,CS.YD_DN_CMPL_DT AS WR_DT
				      ,TO_CHAR(CS.YD_DN_CMPL_DT     ,'YYYYMMDD') AS RECEIPT_DATE
				      ,TO_CHAR(CS.YD_DN_CMPL_DT     ,'HH24MISS') AS RECEIPT_TIME
				      ,TO_CHAR(CS.YD_DN_CMPL_DT-6/24,'YYYYMMDD') AS RECEIPT_INI_DATE
				      ,(SELECT MIN(TS.YD_CAR_USE_GP)
				          FROM TB_YD_CARSCH TS
				         WHERE TS.YD_CARLD_WRK_BOOK_ID = CS.YD_WBOOK_ID
				           AND TS.DEL_YN = 'N') AS YD_CAR_USE_GP
				      ,substr(CM.STL_NO,1,1) as STL_CHK     
				  FROM TB_YD_CRNSCH    CS
				      ,TB_YD_CRNWRKMTL CM
				      ,TB_PT_MSLABCOMM SC
				 WHERE CS.YD_CRN_SCH_ID    = CM.YD_CRN_SCH_ID
				   AND CM.STL_NO           = SC.MSLAB_NO
				   AND SC.RECORD_PROG_STAT = '2' --진행
				   AND CS.YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
			 */
			JDTORecordSet	jsSchMslab = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getY3YDL009MslabComm", logId, methodNm, "하차 차량스케줄  조회");
			JDTORecord		jrSchMslab = null;
			
			slabUtils.printLog(logId, "[주편 수정대상 건] jsSchMslab.size() : " + jsSchMslab.size(), "SL");
			if(jsSchMslab.size() > 0){
				for(int ii = 0 ; ii < jsSchMslab.size() ; ii++ ){
					jrSchMslab = jsSchMslab.getRecord(ii);
					jrSchMslab.setField("MODIFIER", sModifier);
					
					/*
					 	-- com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009MslabComm2
						-- 후판Slab 권하실적처리 -> 주편 현재위치 수정
						UPDATE USRPTA.TB_PT_MSLABCOMM SET
						      MODIFIER         = :V_MODIFIER
						    , MOD_DDTT         = SYSDATE
						    , FNL_REG_PGM      = 'rcvY3YDL009'    -- 크레인권하실적(Y3YDL009)
						    , YD_GP            = 'D'              -- 후판Slab 야드
						    , YD_BAY_GP        = :V_YD_BAY_GP
						    , YD_EQP_GP        = :V_YD_EQP_GP
						    , YD_STK_COL_NO    = :V_YD_STK_COL_NO
						    , YD_STK_BED_NO    = :V_YD_STK_BED_NO
						    , YD_STK_LYR_NO    = :V_YD_STK_LYR_NO
						    , YD_STR_LOC       = :V_YD_STR_LOC
						    , YD_STR_LOC_HIS1  = DECODE(YD_STR_LOC, :V_YD_STR_LOC, YD_STR_LOC_HIS1, YD_STR_LOC     )
						    , YD_STR_LOC_HIS2  = DECODE(YD_STR_LOC, :V_YD_STR_LOC, YD_STR_LOC_HIS2, YD_STR_LOC_HIS1)
						    , RECEIPT_DATE     = (CASE :V_STL_CHK WHEN 'G' THEN RECEIPT_DATE ELSE DECODE(YD_GP, :V_YD_GP, RECEIPT_DATE, :V_RECEIPT_DATE    ) END) -- 입고일기준:권하일(출하 제외)
						    , RECEIPT_TIME     = (CASE :V_STL_CHK WHEN 'G' THEN RECEIPT_TIME ELSE DECODE(YD_GP, :V_YD_GP, RECEIPT_TIME, :V_RECEIPT_TIME    ) END) -- 입고시각기준:권하시각(출하 제외)
						    , RECEIPT_INI_DATE = (CASE :V_STL_CHK WHEN 'G' THEN RECEIPT_INI_DATE ELSE DECODE(YD_GP, :V_YD_GP,RECEIPT_INI_DATE, :V_RECEIPT_INI_DATE) END)
						WHERE MSLAB_NO = :V_STL_NO
					 */
					commDao.updateTx(jrSchMslab, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009MslabComm2", logId, methodNm, "주편공통(TB_PT_MSLABCOMM) 수정");	
				}
			}
			
			
			//Slab공통 수정
			/*
				-- com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getY3YDL009SlabComm
				-- 권하실적 후 Slab 위치정보 갱신대상 조회
				SELECT CM.STL_NO
				      ,CS.YD_GP
				      ,CS.YD_BAY_GP
				      ,SUBSTR(CS.YD_DN_WR_LOC,3,2) AS YD_EQP_GP
				      ,SUBSTR(CS.YD_DN_WR_LOC,5,2) AS YD_STK_COL_NO
				      ,SUBSTR(CS.YD_DN_WR_LOC,7,2) AS YD_STK_BED_NO
				      ,TO_CHAR(TO_NUMBER(CM.YD_STK_LYR_NO) + TO_NUMBER(CS.YD_DN_WR_LAYER) - 1,'FM000') AS YD_STK_LYR_NO
				      ,CS.YD_DN_WR_LOC||TO_CHAR(TO_NUMBER(CM.YD_STK_LYR_NO) + TO_NUMBER(CS.YD_DN_WR_LAYER) - 1,'FM00') AS YD_STR_LOC
				      ,CS.YD_DN_CMPL_DT AS WR_DT
				      ,TO_CHAR(CS.YD_DN_CMPL_DT     ,'YYYYMMDD') AS RECEIPT_DATE
				      ,TO_CHAR(CS.YD_DN_CMPL_DT     ,'HH24MISS') AS RECEIPT_TIME
				      ,TO_CHAR(CS.YD_DN_CMPL_DT-6/24,'YYYYMMDD') AS RECEIPT_INI_DATE
				      ,(SELECT MIN(TS.YD_CAR_USE_GP)
				          FROM TB_YD_CARSCH TS
				         WHERE TS.YD_CARLD_WRK_BOOK_ID = CS.YD_WBOOK_ID
				           AND TS.DEL_YN = 'N') AS YD_CAR_USE_GP
				  FROM TB_YD_CRNSCH    CS
				      ,TB_YD_CRNWRKMTL CM
				      ,TB_PT_SLABCOMM  SC
				 WHERE CS.YD_CRN_SCH_ID    = CM.YD_CRN_SCH_ID
				   AND CM.STL_NO           = SC.SLAB_NO
				   AND SC.RECORD_PROG_STAT = '2' --진행
				   AND CS.YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
			 */
			JDTORecordSet	jsSchSlab = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getY3YDL009SlabComm", logId, methodNm, "하차 차량스케줄  조회");
			JDTORecord		jrSchSlab = null;
			
			slabUtils.printLog(logId, "[Slab 수정대상 건] jsSchSlab.size() : " + jsSchSlab.size(), "SL");
			if(jsSchSlab.size() > 0){
				for(int ii = 0 ; ii < jsSchSlab.size() ; ii++ ){
					jrSchSlab = jsSchSlab.getRecord(ii);
					jrSchSlab.setField("MODIFIER", sModifier);
					
					/*
					 	-- com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009SlabComm2
						-- 후판Slab 권하실적처리 -> Slab 현재위치 수정
						UPDATE USRPTA.TB_PT_SLABCOMM SC SET
						      MODIFIER         = :V_MODIFIER
						    , MOD_DDTT         = SYSDATE
						    , FNL_REG_PGM      = 'rcvY1YDL009' --권하실적수신
						    , YD_GP            = :V_YD_GP
						    , YD_BAY_GP        = :V_YD_BAY_GP
						    , YD_EQP_GP        = :V_YD_EQP_GP
						    , YD_STK_COL_NO    = :V_YD_STK_COL_NO
						    , YD_STK_BED_NO    = :V_YD_STK_BED_NO
						    , YD_STK_LYR_NO    = :V_YD_STK_LYR_NO
						    , YD_STR_LOC       = :V_YD_STR_LOC
						    , YD_STR_LOC_HIS1  = DECODE(SC.YD_STR_LOC,:V_YD_STR_LOC,SC.YD_STR_LOC_HIS1,SC.YD_STR_LOC     )
						    , YD_STR_LOC_HIS2  = DECODE(SC.YD_STR_LOC,:V_YD_STR_LOC,SC.YD_STR_LOC_HIS2,SC.YD_STR_LOC_HIS1)
						    , RECEIPT_DATE     = DECODE(SC.YD_GP,:V_YD_GP,SC.RECEIPT_DATE    ,:V_RECEIPT_DATE    )
						    , RECEIPT_TIME     = DECODE(SC.YD_GP,:V_YD_GP,SC.RECEIPT_TIME    ,:V_RECEIPT_TIME    )
						    , RECEIPT_INI_DATE = DECODE(SC.YD_GP,:V_YD_GP,SC.RECEIPT_INI_DATE,:V_RECEIPT_INI_DATE)
						WHERE SC.SLAB_NO = :V_STL_NO
					 */
					commDao.updateTx(jrSchSlab, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009SlabComm2", logId, methodNm, "주편공통(TB_PT_MSLABCOMM) 수정");	
				}
			}
			//차량하차작업인 경우 재료공통 진도 변경
			if ("PT".equals(ydUpWrLoc.substring(2, 4)) && !"PT".equals(ydDnWrLoc.substring(2, 4))) {
				//주편공통 재료진도 변경
				/*
				 * --크레인권하실적 주편공통진도 조회 - 
				SELECT STL_NO
				      ,CURR_PROG_REG_DDTT
				      ,CURR_PROG_CD_NEW AS CURR_PROG_CD
				      ,'['||STL_NO||' : '||CURR_PROG_CD||' -> '||CURR_PROG_CD_NEW||' << '||LOG_MSG||']' AS LOG_MSG
				  FROM (SELECT CM.STL_NO
				              ,TO_CHAR(CS.YD_DN_CMPL_DT,'YYYYMMDDHH24MISS') AS CURR_PROG_REG_DDTT
				              ,SC.CURR_PROG_CD
				              ,USRPMA.IHSF_PM_주편SLAB진도찾기(SC.STL_APPEAR_GP,CM.STL_NO) AS CURR_PROG_CD_NEW
				              ,'STL_APPEAR_GP:'     ||SC.STL_APPEAR_GP   ||
				               ', YD_GP:'           ||SC.YD_GP           ||
				               ', SLAB_WO_RT_CD:'   ||SC.SLAB_WO_RT_CD   ||
				               ', ORD_YEOJAE_GP:'   ||SC.ORD_YEOJAE_GP   ||
				               ', SCARFING_YN:'     ||SC.SCARFING_YN     ||
				               ', SCARFING_DONE_YN:'||SC.SCARFING_DONE_YN AS LOG_MSG
				          FROM TB_YD_CRNSCH    CS
				              ,TB_YD_CRNWRKMTL CM
				              ,TB_PT_MSLABCOMM SC
				         WHERE CS.YD_CRN_SCH_ID    = CM.YD_CRN_SCH_ID
				           AND CM.STL_NO           = SC.MSLAB_NO
				           AND SC.RECORD_PROG_STAT = '2' --진행
				           AND CS.YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
				           AND CM.STL_NO NOT IN
				                (SELECT STL_NO
				                 FROM TB_YD_RETHTHIST
				                 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				                 --AND YD_RETHT_STAT_CD = '1'
				                )
				       )
				 WHERE CURR_PROG_CD != CURR_PROG_CD_NEW
				 */
				jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getY3YDL009MslabCommProg", logId, methodNm, "주편공통진도  조회");
				
				for (int ii = 0; ii < jsChk.size(); ii++) {
					jrChk = jsChk.getRecord(ii);
					
					slabUtils.printLog(logId, "주편공통 재료진도 변경 " + slabUtils.trim(jrChk.getFieldString("LOG_MSG")), "SL");

					jrParam.setField("CURR_PROG_REG_DDTT", slabUtils.trim(jrChk.getFieldString("CURR_PROG_REG_DDTT"))); //현재진도등록일시
					jrParam.setField("CURR_PROG_CD"      , slabUtils.trim(jrChk.getFieldString("CURR_PROG_CD"      ))); //현재진도코드
					jrParam.setField("STL_NO"            , slabUtils.trim(jrChk.getFieldString("STL_NO"            ))); //재료번호

					/*
					 * 크레인권하실적 주편공통진도 수정  
						UPDATE TB_PT_MSLABCOMM
						   SET CURR_PROG_CD_REG_PGM     = 'rcvY3YDL009' --권하실적수신
						      ,CURR_PROG_REG_DDTT       = NVL(SYSDATE,TO_DATE(:V_CURR_PROG_REG_DDTT,'YYYYMMDDHH24MISS'))
						      ,CURR_PROG_CD             = :V_CURR_PROG_CD
						      ,BEFO_PROG_CD_REG_PGM     = CURR_PROG_CD_REG_PGM
						      ,BEFO_PROG_REG_DDTT       = CURR_PROG_REG_DDTT
						      ,BEFO_PROG_CD             = CURR_PROG_CD
						      ,BEFOBEFO_PROG_CD_REG_PGM = BEFO_PROG_CD_REG_PGM
						      ,BEFOBEFO_PROG_REG_DDTT   = BEFO_PROG_REG_DDTT
						      ,BEFOBEFO_PROG_CD         = BEFO_PROG_CD
						 WHERE MSLAB_NO                 = :V_STL_NO
					 */
					commDao.updateTx(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009MslabCommProg", logId, methodNm, "주편공통(TB_PT_MSLABCOMM) 진도 수정");				
					
					//진행관리 Slab이송완료실적(진도변경) 전송
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDPTJ001Mslab", jrParam));
				}

				//Slab공통 재료진도 변경
				/*
				 * --크레인권하실적 Slab공통진도 조회 - 
				SELECT STL_NO
				      ,CURR_PROG_REG_DDTT
				      ,CURR_PROG_CD_NEW AS CURR_PROG_CD
				      ,'['||STL_NO||' : '||CURR_PROG_CD||' -> '||CURR_PROG_CD_NEW||' << '||LOG_MSG||']' AS LOG_MSG
				  FROM (SELECT CM.STL_NO
				              ,TO_CHAR(CS.YD_DN_CMPL_DT,'YYYYMMDDHH24MISS') AS CURR_PROG_REG_DDTT
				              ,SC.CURR_PROG_CD
				              ,USRPMA.IHSF_PM_주편SLAB진도찾기(SC.STL_APPEAR_GP,CM.STL_NO) AS CURR_PROG_CD_NEW
				              ,'STL_APPEAR_GP:'     ||SC.STL_APPEAR_GP   ||
				               ', YD_GP:'           ||SC.YD_GP           ||
				               ', SLAB_WO_RT_CD:'   ||SC.SLAB_WO_RT_CD   ||
				               ', ORD_YEOJAE_GP:'   ||SC.ORD_YEOJAE_GP   ||
				               ', SCARFING_YN:'     ||SC.SCARFING_YN     ||
				               ', SCARFING_DONE_YN:'||SC.SCARFING_DONE_YN AS LOG_MSG
				          FROM TB_YD_CRNSCH    CS
				              ,TB_YD_CRNWRKMTL CM
				              ,TB_PT_SLABCOMM  SC
				         WHERE CS.YD_CRN_SCH_ID    = CM.YD_CRN_SCH_ID
				           AND CM.STL_NO           = SC.SLAB_NO
				           AND SC.RECORD_PROG_STAT = '2' --진행
				           AND CS.YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
				           AND CM.STL_NO NOT IN
				                (SELECT STL_NO
				                 FROM TB_YD_RETHTHIST
				                 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				                 --AND YD_RETHT_STAT_CD = '1'
				                )        
				        )
				 WHERE CURR_PROG_CD != CURR_PROG_CD_NEW
				 */
				jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getY3YDL009SlabCommProg", logId, methodNm, "Slab공통진도  조회");
				
				for (int ii = 0; ii < jsChk.size(); ii++) {
					jrChk = jsChk.getRecord(ii);

					slabUtils.printLog(logId, "Slab공통 재료진도 변경 " + slabUtils.trim(jrChk.getFieldString("LOG_MSG")), "SL");

					jrParam.setField("CURR_PROG_REG_DDTT", slabUtils.trim(jrChk.getFieldString("CURR_PROG_REG_DDTT"))); //현재진도등록일시
					jrParam.setField("CURR_PROG_CD"      , slabUtils.trim(jrChk.getFieldString("CURR_PROG_CD"      ))); //현재진도코드
					jrParam.setField("STL_NO"            , slabUtils.trim(jrChk.getFieldString("STL_NO"            ))); //재료번호

					/*
					 * --크레인권하실적 Slab공통진도 수정 - 
					UPDATE TB_PT_SLABCOMM
					   SET CURR_PROG_CD_REG_PGM     = 'rcvY3YDL009' --권하실적수신
					      ,CURR_PROG_REG_DDTT       = NVL(SYSDATE,TO_DATE(:V_CURR_PROG_REG_DDTT,'YYYYMMDDHH24MISS'))
					      ,CURR_PROG_CD             = :V_CURR_PROG_CD
					      ,BEFO_PROG_CD_REG_PGM     = CURR_PROG_CD_REG_PGM
					      ,BEFO_PROG_REG_DDTT       = CURR_PROG_REG_DDTT
					      ,BEFO_PROG_CD             = CURR_PROG_CD
					      ,BEFOBEFO_PROG_CD_REG_PGM = BEFO_PROG_CD_REG_PGM
					      ,BEFOBEFO_PROG_REG_DDTT   = BEFO_PROG_REG_DDTT
					      ,BEFOBEFO_PROG_CD         = BEFO_PROG_CD
					 WHERE SLAB_NO                  = :V_STL_NO
					 */
					commDao.updateTx(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009SlabCommProg", logId, methodNm, "Slab공통(TB_PT_SLABCOMM) 진도 수정");				
					
					//진행관리 Slab이송완료실적(진도변경) 전송
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDPTJ001Slab", jrParam));
				}
			}
			
			
			//저장품 수정
			/*
			 * --크레인권하실적 저장품 수정 - 
				MERGE INTO TB_YD_STOCK ST USING (
				SELECT SC.*
				      ,DECODE(YD_AIM_RT_GP2,'E2',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EL' ELSE 'E2' END),
				                            'E4',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EM' ELSE 'E4' END),
				                            'E5',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EN' ELSE 'E5' END),
				              YD_AIM_RT_GP2) AS YD_AIM_RT_GP
				 FROM (
				        SELECT SC.STL_NO
				              ,SC.CURR_PROG_CD AS STL_PROG_CD --재료진도코드
				              ,SC.ORD_YEOJAE_GP               --주문여재구분
				              ,SF_SLAB_YD_AIM_RT_GP(SC.SLAB_WO_RT_CD, SC.CURR_PROG_CD     ,
				                                    SC.SCARFING_YN  , SC.SCARFING_DONE_YN , SC.HCR_GP,
				                                    SC.ORD_YEOJAE_GP, SC.STL_APPEAR_GP ,
				                                     (CASE WHEN SC.MSLAB_RPR_MC_GP='G' AND SC.SCARFING_DONE_YN='N' AND SC.SCARFING_YN='Y' THEN 'AG' --그라인더 필수재
				                                           WHEN SC.BK_STK_END_TIME IS NULL THEN (SELECT BANK_WORK_RT     --보온뱅크재(AE,AF)
				                                                                              FROM VW_YD_F_MSLABWO A
				                                                                             WHERE A.PLN_MSLAB_NO=SC.PLAN_SLAB_NO
				                                                                               AND ROWNUM<=1
				                                                                            )
				                                     ELSE ''
				                                     END )
				                                    )   AS YD_AIM_RT_GP2  --야드목표행선구분
				              ,SF_SLAB_YD_AIM_YD_GP (SC.YD_GP, SC.CURR_PROG_CD, SC.ARR_WLOC_CD) AS YD_AIM_YD_GP  --야드목표야드구분
				              ,SF_SLAB_YD_AIM_BAY_GP(SC.YD_GP, SC.SLAB_WO_RT_CD, SC.CURR_PROG_CD, SC.ARR_WLOC_CD,
				                                     SC.SCARFING_YN  , SC.SCARFING_DONE_YN, SC.HCR_GP,
				                                     SC.ORD_YEOJAE_GP, SC.STL_APPEAR_GP   )     AS YD_AIM_BAY_GP --야드목표동구분
				              ,SF_SLAB_YD_STK_LOT_TP(SC.SLAB_WO_RT_CD, SC.CURR_PROG_CD    ,
				                                     SC.SCARFING_YN  , SC.SCARFING_DONE_YN, SC.HCR_GP,
				                                     SC.ORD_YEOJAE_GP, SC.STL_APPEAR_GP   )     AS YD_STK_LOT_TP --야드산적LotType
				              ,SF_SLAB_YD_STK_LOT_CD(SC.SLAB_WO_RT_CD, SC.CURR_PROG_CD    ,
				                                     SC.SCARFING_YN  , SC.SCARFING_DONE_YN, SC.HCR_GP,
				                                     SC.ORD_YEOJAE_GP, SC.STL_APPEAR_GP   ,
				                                     OC.PROD_DUE_DATE, SC.STACK_LOT_NO    ,
				                                     SC.YD_CHG_NO    , SC.ARR_WLOC_CD     )     AS YD_STK_LOT_CD --야드산적Lot코드
				              ,SC.STL_APPEAR_GP    --재료외형구분
				              ,SC.SLAB_WO_RT_CD    --Slab지시행선코드
				              ,SC.HCR_GP           --HCR구분
				              ,SC.SCARFING_YN      --Scarfing여부
				              ,SC.SCARFING_DONE_YN --Scarfing완료유무
				              ,SC.YD_STK_COL_GP    --야드적치열구분
				              ,SC.YD_STK_BED_NO    --야드적치Bed번호
				              ,SC.YD_STK_LYR_NO    --야드적치단번호
				              ,SC.DEL_YN           --작업예약삭제여부
				              ,DECODE(SC.YD_AIM_RT_GP,'AE','Y','AF','Y',SC.PRG_YN) AS PRG_YN  --재료진도변경여부
				              ,SC.LOC_YN           --저장위치변경여부
				              ,(CASE WHEN YD_SCH_CD IN('MBPU01LM','MBYDPUMM') THEN 'Y' ELSE 'N' END) AS CHK
				              ,SC.ARR_WLOC_CD    --착지개소코드
				          FROM TB_PT_OSCOMM OC
				              ,(SELECT 'M' AS SLAB_GP
				                      ,SC.MSLAB_NO AS STL_NO
				                      ,SC.SLAB_WO_RT_CD
				                      ,SC.CURR_PROG_CD
				                      ,SC.SCARFING_YN
				                      ,SC.SCARFING_DONE_YN
				                      ,SC.WR_HCR_GP AS HCR_GP
				                      ,SC.ORD_YEOJAE_GP
				                      ,SC.STL_APPEAR_GP
				                      ,SC.STACK_LOT_NO
				                      ,SUBSTR(SC.YD_STR_LOC,1,6) AS YD_STK_COL_GP
				                      ,SUBSTR(SC.YD_STR_LOC,7,2) AS YD_STK_BED_NO
				                      ,SC.YD_STK_LYR_NO
				                      ,SUBSTR(SC.YD_STR_LOC,1,1) AS YD_GP
				                      ,ST.ARR_WLOC_CD
				                      ,ST.YD_CHG_NO
				                      ,CASE WHEN ST.YD_WBOOK_ID IS NOT NULL AND WM.DEL_YN = 'Y'
				                            THEN 'Y' ELSE 'N' END AS DEL_YN
				                      ,DECODE(ST.STL_PROG_CD,SC.CURR_PROG_CD,'N','Y') AS PRG_YN
				                      ,CASE WHEN ST.YD_STK_COL_GP != SUBSTR(SC.YD_STR_LOC,1,6)
				                              OR ST.YD_STK_BED_NO != SUBSTR(SC.YD_STR_LOC,7,2)
				                              OR ST.YD_STK_LYR_NO != SC.YD_STK_LYR_NO THEN 'Y'
				                       ELSE 'N' END AS LOC_YN
				                      ,SC.ORD_NO
				                      ,SC.ORD_DTL
				                      ,SC.PLN_MSLAB_NO AS PLAN_SLAB_NO
				                      ,ST.YD_AIM_RT_GP
				                      ,MS.MSLAB_RPR_MC_GP 
				                      ,MS.BK_STK_END_TIME
				                      ,WK.YD_SCH_CD
				                  FROM TB_YD_WRKBOOKMTL WM
				                      ,TB_YD_WRKBOOK WK
				                      ,TB_YD_STOCK      ST
				                      ,TB_PT_MSLABCOMM  SC
				                      ,TB_PT_MSLABCOMMSUB MS
				                 WHERE WM.STL_NO      = ST.STL_NO
				                   AND SC.MSLAB_NO     = MS.MSLAB_NO(+)
				                   AND WM.STL_NO      = SC.MSLAB_NO
				                   AND WM.YD_WBOOK_ID=WK.YD_WBOOK_ID
				                   AND SC.RECORD_PROG_STAT = '2' --진행
				                   AND WM.YD_WBOOK_ID = :V_YD_WBOOK_ID
				                 UNION ALL
				                SELECT 'S' AS SLAB_GP
				                      ,SC.SLAB_NO AS STL_NO
				                      ,SC.SLAB_WO_RT_CD
				                      ,SC.CURR_PROG_CD
				                      ,SC.SCARFING_YN
				                      ,SC.SCARFING_DONE_YN
				                      ,SC.HCR_GP
				                      ,SC.ORD_YEOJAE_GP
				                      ,SC.STL_APPEAR_GP
				                      ,SC.STACK_LOT_NO
				                      ,SUBSTR(SC.YD_STR_LOC,1,6) AS YD_STK_COL_GP
				                      ,SUBSTR(SC.YD_STR_LOC,7,2) AS YD_STK_BED_NO
				                      ,SC.YD_STK_LYR_NO
				                      ,SUBSTR(SC.YD_STR_LOC,1,1) AS YD_GP
				                      ,ST.ARR_WLOC_CD
				                      ,ST.YD_CHG_NO
				                      ,CASE WHEN ST.YD_WBOOK_ID IS NOT NULL AND WM.DEL_YN = 'Y'
				                            THEN 'Y' ELSE 'N' END AS DEL_YN
				                      ,DECODE(ST.STL_PROG_CD,SC.CURR_PROG_CD,'N','Y') AS PRG_YN
				                      ,CASE WHEN ST.YD_STK_COL_GP != SUBSTR(SC.YD_STR_LOC,1,6)
				                              OR ST.YD_STK_BED_NO != SUBSTR(SC.YD_STR_LOC,7,2)
				                              OR ST.YD_STK_LYR_NO != SC.YD_STK_LYR_NO THEN 'Y'
				                       ELSE 'N' END AS LOC_YN
				                      ,SC.ORD_NO
				                      ,SC.ORD_DTL
				                      ,SC.PLAN_SLAB_NO
				                      ,ST.YD_AIM_RT_GP
				                      ,NULL AS MSLAB_RPR_MC_GP 
				                      ,NULL
				                      ,WK.YD_SCH_CD
				                  FROM TB_YD_WRKBOOKMTL WM
				                      ,TB_YD_WRKBOOK WK
				                      ,TB_YD_STOCK      ST
				                      ,TB_PT_SLABCOMM   SC
				                 WHERE WM.STL_NO      = ST.STL_NO
				                   AND WM.STL_NO      = SC.SLAB_NO
				                   AND WM.YD_WBOOK_ID=WK.YD_WBOOK_ID
				                   AND SC.RECORD_PROG_STAT = '2' --진행
				                   AND WM.YD_WBOOK_ID = :V_YD_WBOOK_ID) SC
				          WHERE SC.ORD_NO  = OC.ORD_NO(+)
				            AND SC.ORD_DTL = OC.ORD_DTL(+)
				          ) SC
				      WHERE (SC.DEL_YN = 'Y' OR SC.PRG_YN = 'Y' OR SC.LOC_YN = 'Y' OR CHK ='Y')
				) DD ON (ST.STL_NO = DD.STL_NO)
				WHEN MATCHED THEN UPDATE SET
					 ST.MODIFIER         = :V_MODIFIER
				    ,ST.MOD_DDTT         = SYSDATE
				    ,ST.YD_WBOOK_ID      = DECODE(DD.DEL_YN,'Y',NULL               ,ST.YD_WBOOK_ID     )
				    ,ST.YD_SCH_CD        = DECODE(DD.DEL_YN,'Y',NULL               ,ST.YD_SCH_CD       )
				    ,ST.STL_PROG_CD      = DECODE(DD.PRG_YN,'Y',DD.STL_PROG_CD     ,ST.STL_PROG_CD     )
				    ,ST.ORD_YEOJAE_GP    = DECODE(DD.PRG_YN,'Y',DD.ORD_YEOJAE_GP   ,ST.ORD_YEOJAE_GP   )
				    ,ST.YD_AIM_RT_GP     = DECODE(DD.PRG_YN,'Y',DD.YD_AIM_RT_GP    ,ST.YD_AIM_RT_GP    )
				    ,ST.YD_AIM_YD_GP     = DECODE(DD.PRG_YN,'Y',DD.YD_AIM_YD_GP    ,ST.YD_AIM_YD_GP    )
				    ,ST.YD_AIM_BAY_GP    = DECODE(DD.PRG_YN,'Y',DD.YD_AIM_BAY_GP   ,ST.YD_AIM_BAY_GP   )
				    ,ST.YD_STK_LOT_TP    = DECODE(DD.PRG_YN,'Y',DD.YD_STK_LOT_TP   ,ST.YD_STK_LOT_TP   )
				    ,ST.YD_STK_LOT_CD    = DECODE(DD.PRG_YN,'Y',DD.YD_STK_LOT_CD   ,ST.YD_STK_LOT_CD   )
				    ,ST.STL_APPEAR_GP    = DECODE(DD.PRG_YN,'Y',DD.STL_APPEAR_GP   ,ST.STL_APPEAR_GP   )
				    ,ST.SLAB_WO_RT_CD    = DECODE(DD.PRG_YN,'Y',DD.SLAB_WO_RT_CD   ,ST.SLAB_WO_RT_CD   )
				    ,ST.HCR_GP           = DECODE(DD.PRG_YN,'Y',DD.HCR_GP          ,ST.HCR_GP          )
				    ,ST.SCARFING_YN      = DECODE(DD.PRG_YN,'Y',DD.SCARFING_YN     ,ST.SCARFING_YN     )
				    ,ST.SCARFING_DONE_YN = DECODE(DD.PRG_YN,'Y',DD.SCARFING_DONE_YN,ST.SCARFING_DONE_YN)
				    ,ST.YD_STK_COL_GP    = DECODE(DD.LOC_YN,'Y',DD.YD_STK_COL_GP   ,ST.YD_STK_COL_GP   )
				    ,ST.YD_STK_BED_NO    = DECODE(DD.LOC_YN,'Y',DD.YD_STK_BED_NO   ,ST.YD_STK_BED_NO   )
				    ,ST.YD_STK_LYR_NO    = DECODE(DD.LOC_YN,'Y',DD.YD_STK_LYR_NO   ,ST.YD_STK_LYR_NO   )
				    ,ST.YD_RULE_PL_RS_GP = DECODE(DD.CHK,'Y',NULL               ,ST.YD_RULE_PL_RS_GP   )
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009Stock", logId, methodNm, "저장품(TB_YD_STOCK) 수정");				
			
			//작업이력 등록
			/*
			 * --작업이력 등록 - 
				MERGE INTO TB_YD_WRKHIST WH USING (
				SELECT HI.HIST_ID_DT||TO_CHAR(HI.HIST_ID_NO + ROWNUM,'FM000000') AS YD_WRK_HIST_ID --야드작업이력ID
				      ,:V_MODIFIER AS MODIFIER    --수정자
				      ,SYSDATE     AS MOD_DDTT    --수정일시
				      ,'N'         AS DEL_YN      --삭제유무
				      ,CS.YD_GP                   --야드구분
				      ,CM.STL_NO                  --재료번호
				      ,DECODE(CT.YD_TCAR_SCH_ID,NULL,SUBSTR(CS.YD_SCH_CD,7,1),'M') AS YD_GNT_GP --야드수불구분
				      ,ST.BUY_SLAB_NO             --구입Slab번호
				      ,ST.STL_APPEAR_GP           --재료외형구분
				      ,ST.ITEMNAME_CD             --품명코드
				      ,ST.ORD_YEOJAE_GP           --주문여재구분
				      ,ST.ORD_NO                  --주문번호
				      ,ST.ORD_DTL                 --주문행번
				      ,ST.STLKIND_CD              --강종코드
				      ,ST.SPEC_ABBSYM             --규격약호
				      ,ST.ORD_GP                  --수주구분
				      ,ST.CUST_CD                 --고객코드
				      ,ST.DEST_CD                 --목적지코드
				      ,ST.DEMANDER_CD             --수요가코드
				      ,ST.DEST_TEL_NO             --목적지전화번호
				      ,ST.STL_PROG_CD             --재료진도코드
				      ,ST.GOODS_GRADE             --제품등급
				      ,ST.YD_MTL_W_GP             --야드재료폭구분
				      ,ST.YD_MTL_T_GP             --야드재료두께구분
				      ,ST.YD_MTL_L_GP             --야드재료길이구분
				      ,ST.YD_MTL_T                --야드재료두께
				      ,ST.YD_MTL_W                --야드재료폭
				      ,ST.YD_MTL_L                --야드재료길이
				      ,ST.YD_MTL_WT               --야드재료중량
				      ,ST.YD_COIL_OUTDIA_GRP_GP   --야드코일외경군구분
				      ,ST.COIL_INDIA              --Coil내경
				      ,ST.COIL_OUTDIA             --Coil외경
				      ,ST.SLAB_WO_RT_CD           --Slab지시행선코드
				      ,ST.ORD_HCR_GP              --설계HCR구분
				      ,ST.HCR_GP                  --HCR구분
				      ,ST.SCARFING_YN             --Scarfing여부
				      ,ST.SCARFING_DONE_YN        --Scarfing완료유무
				      ,ST.REHEAT_SLAB_GP          --재열재구분
				      ,ST.ROLL_UNIT_GP            --Roll단위구분
				      ,ST.ROLL_UNIT_NAME          --Roll단위명
				      ,ST.REFUR_CHG_LOT_NO        --가열로장입Lot번호
				      ,ST.REFUR_CHG_PLN_SERNO     --가열로장입예정일련번호
				      ,ST.YD_CONVEYOR_BRANCH_CD   --야드Conveyor분기코드
				      ,ST.HYSCO_TRANS_GP          --HYSCO운송구분
				      ,ST.COOL_METHOD             --권취Coil냉각방법
				      ,ST.COOL_DONE_GP            --냉각완료구분
				      ,ST.RENTPROC_CD             --임가공사코드
				      ,ST.DIST_DUE_DATE           --출하기한일
				      ,ST.YD_PILING_CD            --야드Piling코드
				      ,ST.YD_BOOK_OUT_LOC         --야드BookOut위치
				      ,ST.PL_RCPT_LN_GP           --후판입고Line구분
				      ,ST.FRTOMOVE_ORD_DATE       --이송지시일자
				      ,ST.URGENT_FRTOMOVE_WORD_GP --긴급이송작업지시구분
				      ,CT.SPOS_WLOC_CD            --발지개소코드
				      ,CT.ARR_WLOC_CD             --착지개소코드
				      ,ST.YD_AIM_RT_GP            --야드목표행선구분
				      ,ST.YD_AIM_BAY_GP           --야드목표동구분
				      ,ST.YD_STK_LOT_TP           --야드산적LotType
				      ,ST.YD_STK_LOT_CD           --야드산적Lot코드
				      ,ST.TRANS_ORD_DATE          --운송지시일자
				      ,ST.PL_L2_TRK_NO            --후판L2제품번호
				      ,ST.DIST_SHIPASSIGN_GP      --출하배선지시구분
				      ,ST.EXPORT_SHIP_SET_NO      --수출재배선번호
				      ,ST.SHIPASSIGN_WORD_DATE    --배선작업지시일자
				      ,ST.SHIPASSIGN_WORD_SEQNO   --배선작업지시순번
				      ,ST.SHIP_CD                 --선박코드
				      ,ST.SHIP_NAME               --선박명
				      ,ST.RSHP_HOLD_NO            --선박Hold번호
				      ,ST.BERTH_NO                --선석번호
				      ,ST.SAILNO                  --선박항차
				      ,CT.YD_CAR_USE_GP           --야드차량사용구분
				      ,CT.CAR_NO                  --차량번호
				      ,CT.TRN_EQP_CD              --운송장비코드
				      ,CT.CAR_KIND                --차량종류
				      ,CT.TRANS_EQUIPMENT_TYPE    --운송장비Type
				      ,CT.CARD_NO                 --카드번호
				      ,CT.YD_CAR_SCH_ID           --야드차량스케쥴ID
				      ,CT.YD_TCAR_SCH_ID          --야드대차스케쥴ID
				      ,CS.YD_WBOOK_ID             --야드작업예약ID
				      ,CS.YD_CRN_SCH_ID           --야드크레인스케쥴ID
				      ,CS.YD_SCH_CD               --야드스케쥴코드
				      ,CS.YD_SCH_ST_GP            --야드스케쥴기동구분
				      ,CS.YD_SCH_REQ_GP           --야드스케쥴요청구분
				      ,CS.YD_SCH_PRIOR            --야드스케쥴우선순위
				      ,CS.YD_WBOOK_DT             --야드작업예약일시
				      ,CM.YD_AID_WRK_YN           --야드보조작업여부
				      ,CM.YD_TO_LOC_DCSN_MTD      --야드To위치결정방법
				      ,CS.YD_TO_LOC_GUIDE         --야드To위치Guide
				      ,CS.YD_SCH_DT               --야드스케쥴일시
				      ,CS.YD_WORD_DT              --야드작업지시일시
				      ,CS.YD_UP_WO_LOC            --야드권상지시위치
				      ,TO_CHAR(TO_NUMBER(CS.YD_UP_WO_LAYER) + TO_NUMBER(CM.YD_STK_LYR_NO) - 1,'FM000') AS YD_UP_WO_LAYER --야드권상지시단
				      ,CS.YD_UP_WR_LOC            --야드권상실적위치
				      ,TO_CHAR(TO_NUMBER(CS.YD_UP_WR_LAYER) + TO_NUMBER(CM.YD_STK_LYR_NO) - 1,'FM000') AS YD_UP_WR_LAYER --야드권상실적단
				      ,CS.YD_UP_WRK_ACT_GP        --야드권상작업수행구분
				      ,CS.YD_UP_CMPL_DT           --야드권상완료일시
				      ,CS.YD_DN_WO_LOC            --야드권하지시위치
				      ,TO_CHAR(TO_NUMBER(CS.YD_DN_WO_LAYER) + TO_NUMBER(CM.YD_STK_LYR_NO) - 1,'FM000') AS YD_DN_WO_LAYER --야드권하지시단
				      ,CS.YD_DN_WR_LOC            --야드권하실적위치
				      ,TO_CHAR(TO_NUMBER(CS.YD_DN_WR_LAYER) + TO_NUMBER(CM.YD_STK_LYR_NO) - 1,'FM000') AS YD_DN_WR_LAYER --야드권하실적단
				      ,CS.YD_DN_WRK_ACT_GP        --야드권하작업수행구분
				      ,CS.YD_DN_CMPL_DT           --야드권하완료일시
				      ,CS.YD_WRK_HDS_DD           --야드작업계상일자
				      ,CS.YD_WRK_DUTY             --야드작업근
				      ,CS.YD_WRK_PARTY            --야드작업조
				      ,CT.YD_CARLD_LEV_LOC        --야드상차출발위치
				      ,CT.YD_CARLD_LEV_DT         --야드상차출발일시
				      ,CT.YD_CARLD_PNT_WO_DT      --야드상차Point지시일시
				      ,CT.YD_PNT_CD1              --야드포인트코드1
				      ,CT.YD_PNT_CD2              --야드포인트코드2
				      ,CT.YD_CARLD_WRK_BOOK_ID    --야드상차작업예약ID
				      ,CT.YD_CARLD_SCH_REQ_GP     --야드상차스케쥴요청구분
				      ,CT.YD_CARLD_STOP_LOC       --야드상차정지위치
				      ,CT.YD_CARLD_ARR_DT         --야드상차도착일시
				      ,CT.YD_CARLD_ST_DT          --야드상차개시일시
				      ,CT.YD_CARLD_CMPL_DT        --야드상차완료일시
				      ,CT.YD_CARLD_WRK_ACT_GP     --야드상차작업수행구분
				      ,CT.YD_CARLD_CHK_DT         --야드상차검수일시
				      ,CT.YD_CARUD_LEV_DT         --야드하차출발일시
				      ,CT.YD_CARUD_PNT_WO_DT      --야드하차Point지시일시
				      ,CT.YD_PNT_CD3              --야드포인트코드3
				      ,CT.YD_PNT_CD4              --야드포인트코드4
				      ,CT.YD_CARUD_WRK_BOOK_ID    --야드하차작업예약ID
				      ,CT.YD_CARUD_STOP_LOC       --야드하차정지위치
				      ,CT.YD_CARUD_SCH_REQ_GP     --야드하차스케쥴요청구분
				      ,CT.YD_CARUD_ARR_DT         --야드하차도착일시
				      ,CT.YD_CARUD_CHK_DT         --야드하차검수일시
				      ,CT.YD_CARUD_ST_DT          --야드하차개시일시
				      ,CT.YD_CARUD_CMPL_DT        --야드하차완료일시
				      ,CT.YD_CARUD_WRK_ACT_GP     --야드하차작업수행구분
				      ,CT.YD_TRN_WRK_DELY_CD      --야드운송작업지연코드
				      ,CS.YD_EQP_ID               --야드설비ID
				  FROM TB_YD_CRNSCH    CS
				      ,TB_YD_CRNWRKMTL CM
				      ,TB_YD_STOCK     ST
				      ,(SELECT ROWNUM AS RN
				              ,CT.*
				          FROM (SELECT SPOS_WLOC_CD
				                      ,ARR_WLOC_CD
				                      ,YD_CAR_USE_GP
				                      ,CAR_NO
				                      ,TRN_EQP_CD
				                      ,CAR_KIND
				                      ,TRANS_EQUIPMENT_TYPE
				                      ,CARD_NO
				                      ,YD_CAR_SCH_ID
				                      ,NULL AS YD_TCAR_SCH_ID
				                      ,YD_CARLD_LEV_LOC
				                      ,YD_CARLD_LEV_DT
				                      ,YD_CARLD_PNT_WO_DT
				                      ,YD_PNT_CD1
				                      ,YD_PNT_CD2
				                      ,YD_CARLD_WRK_BOOK_ID
				                      ,YD_CARLD_SCH_REQ_GP
				                      ,YD_CARLD_STOP_LOC
				                      ,YD_CARLD_ARR_DT
				                      ,YD_CARLD_ST_DT
				                      ,YD_CARLD_CMPL_DT
				                      ,YD_CARLD_WRK_ACT_GP
				                      ,YD_CARLD_CHK_DT
				                      ,YD_CARUD_LEV_DT
				                      ,YD_CARUD_PNT_WO_DT
				                      ,YD_PNT_CD3
				                      ,YD_PNT_CD4
				                      ,YD_CARUD_WRK_BOOK_ID
				                      ,YD_CARUD_STOP_LOC
				                      ,YD_CARUD_SCH_REQ_GP
				                      ,YD_CARUD_ARR_DT
				                      ,YD_CARUD_CHK_DT
				                      ,YD_CARUD_ST_DT
				                      ,YD_CARUD_CMPL_DT
				                      ,YD_CARUD_WRK_ACT_GP
				                      ,YD_TRN_WRK_DELY_CD
				                  FROM TB_YD_CARSCH
				                 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				                 UNION ALL
				                SELECT NULL AS SPOS_WLOC_CD
				                      ,NULL AS ARR_WLOC_CD
				                      ,NULL AS YD_CAR_USE_GP
				                      ,NULL AS CAR_NO
				                      ,NULL AS TRN_EQP_CD
				                      ,NULL AS CAR_KIND
				                      ,NULL AS TRANS_EQUIPMENT_TYPE
				                      ,NULL AS CARD_NO
				                      ,NULL AS YD_CAR_SCH_ID
				                      ,YD_TCAR_SCH_ID
				                      ,YD_CARLD_LEV_LOC
				                      ,YD_CARLD_LEV_DT
				                      ,NULL AS YD_CARLD_PNT_WO_DT
				                      ,NULL AS YD_PNT_CD1
				                      ,NULL AS YD_PNT_CD2
				                      ,YD_CARLD_WRK_BOOK_ID
				                      ,YD_CARLD_SCH_REQ_GP
				                      ,YD_CARLD_STOP_LOC
				                      ,YD_CARLD_ARR_DT
				                      ,YD_CARLD_ST_DT
				                      ,YD_CARLD_CMPL_DT
				                      ,YD_CARLD_WRK_ACT_GP
				                      ,NULL AS YD_CARLD_CHK_DT
				                      ,YD_CARUD_LEV_DT
				                      ,NULL AS YD_CARUD_PNT_WO_DT
				                      ,NULL AS YD_PNT_CD3
				                      ,NULL AS YD_PNT_CD4
				                      ,YD_CARUD_WRK_BOOK_ID
				                      ,YD_CARUD_STOP_LOC
				                      ,YD_CARUD_SCH_REQ_GP
				                      ,YD_CARUD_ARR_DT
				                      ,NULL AS YD_CARUD_CHK_DT
				                      ,YD_CARUD_ST_DT
				                      ,YD_CARUD_CMPL_DT
				                      ,YD_CARUD_WRK_ACT_GP
				                      ,NULL AS YD_TRN_WRK_DELY_CD
				                  FROM TB_YD_TCARSCH
				                 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID) CT
				         WHERE ROWNUM = 1) CT
				      ,(SELECT 1 AS RN
				              ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')                AS HIST_ID_DT
				              ,NVL(TO_NUMBER(SUBSTR(MAX(YD_WRK_HIST_ID),13)),0) AS HIST_ID_NO
				          FROM TB_YD_WRKHIST
				         WHERE YD_WRK_HIST_ID LIKE TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||'%') HI
				 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
				   AND CM.STL_NO        = ST.STL_NO
				   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND HI.RN            = CT.RN(+)
				) DD ON (WH.YD_WRK_HIST_ID = DD.YD_WRK_HIST_ID)
				WHEN NOT MATCHED THEN
				INSERT (
				 WH.YD_WRK_HIST_ID      , WH.REGISTER             , WH.REG_DDTT            , WH.MODIFIER           , WH.MOD_DDTT               ,
				 WH.DEL_YN              , WH.YD_GP                , WH.STL_NO              , WH.YD_GNT_GP          , WH.BUY_SLAB_NO            ,
				 WH.STL_APPEAR_GP       , WH.ITEMNAME_CD          , WH.ORD_YEOJAE_GP       , WH.ORD_NO             , WH.ORD_DTL                ,
				 WH.STLKIND_CD          , WH.SPEC_ABBSYM          , WH.ORD_GP              , WH.CUST_CD            , WH.DEST_CD                ,
				 WH.DEMANDER_CD         , WH.DEST_TEL_NO          , WH.STL_PROG_CD         , WH.GOODS_GRADE        , WH.YD_MTL_W_GP            ,
				 WH.YD_MTL_T_GP         , WH.YD_MTL_L_GP          , WH.YD_MTL_T            , WH.YD_MTL_W           , WH.YD_MTL_L               ,
				 WH.YD_MTL_WT           , WH.YD_COIL_OUTDIA_GRP_GP, WH.COIL_INDIA          , WH.COIL_OUTDIA        , WH.SLAB_WO_RT_CD          ,
				 WH.ORD_HCR_GP          , WH.HCR_GP               , WH.SCARFING_YN         , WH.SCARFING_DONE_YN   , WH.REHEAT_SLAB_GP         ,
				 WH.ROLL_UNIT_GP        , WH.ROLL_UNIT_NAME       , WH.REFUR_CHG_LOT_NO    , WH.REFUR_CHG_PLN_SERNO, WH.YD_CONVEYOR_BRANCH_CD  ,
				 WH.HYSCO_TRANS_GP      , WH.COOL_METHOD          , WH.COOL_DONE_GP        , WH.RENTPROC_CD        , WH.DIST_DUE_DATE          ,
				 WH.YD_PILING_CD        , WH.YD_BOOK_OUT_LOC      , WH.PL_RCPT_LN_GP       , WH.FRTOMOVE_ORD_DATE  , WH.URGENT_FRTOMOVE_WORD_GP,
				 WH.SPOS_WLOC_CD        , WH.ARR_WLOC_CD          , WH.YD_AIM_RT_GP        , WH.YD_AIM_BAY_GP      , WH.YD_STK_LOT_TP          ,
				 WH.YD_STK_LOT_CD       , WH.TRANS_ORD_DATE       , WH.PL_L2_TRK_NO        , WH.DIST_SHIPASSIGN_GP , WH.EXPORT_SHIP_SET_NO     ,
				 WH.SHIPASSIGN_WORD_DATE, WH.SHIPASSIGN_WORD_SEQNO, WH.SHIP_CD             , WH.SHIP_NAME          , WH.RSHP_HOLD_NO           ,
				 WH.BERTH_NO            , WH.SAILNO               , WH.YD_CAR_USE_GP       , WH.CAR_NO             , WH.TRN_EQP_CD             ,
				 WH.CAR_KIND            , WH.TRANS_EQUIPMENT_TYPE , WH.CARD_NO             , WH.YD_CAR_SCH_ID      , WH.YD_TCAR_SCH_ID         ,
				 WH.YD_WBOOK_ID         , WH.YD_CRN_SCH_ID        , WH.YD_SCH_CD           , WH.YD_SCH_ST_GP       , WH.YD_SCH_REQ_GP          ,
				 WH.YD_SCH_PRIOR        , WH.YD_WBOOK_DT          , WH.YD_AID_WRK_YN       , WH.YD_TO_LOC_DCSN_MTD , WH.YD_TO_LOC_GUIDE        ,
				 WH.YD_SCH_DT           , WH.YD_WORD_DT           , WH.YD_UP_WO_LOC        , WH.YD_UP_WO_LAYER     , WH.YD_UP_WR_LOC           ,
				 WH.YD_UP_WR_LAYER      , WH.YD_UP_WRK_ACT_GP     , WH.YD_UP_CMPL_DT       , WH.YD_DN_WO_LOC       , WH.YD_DN_WO_LAYER         ,
				 WH.YD_DN_WR_LOC        , WH.YD_DN_WR_LAYER       , WH.YD_DN_WRK_ACT_GP    , WH.YD_DN_CMPL_DT      , WH.YD_WRK_HDS_DD          ,
				 WH.YD_WRK_DUTY         , WH.YD_WRK_PARTY         , WH.YD_CARLD_LEV_LOC    , WH.YD_CARLD_LEV_DT    , WH.YD_CARLD_PNT_WO_DT     ,
				 WH.YD_PNT_CD1          , WH.YD_PNT_CD2           , WH.YD_CARLD_WRK_BOOK_ID, WH.YD_CARLD_SCH_REQ_GP, WH.YD_CARLD_STOP_LOC      ,
				 WH.YD_CARLD_ARR_DT     , WH.YD_CARLD_ST_DT       , WH.YD_CARLD_CMPL_DT    , WH.YD_CARLD_WRK_ACT_GP, WH.YD_CARLD_CHK_DT        ,
				 WH.YD_CARUD_LEV_DT     , WH.YD_CARUD_PNT_WO_DT   , WH.YD_PNT_CD3          , WH.YD_PNT_CD4         , WH.YD_CARUD_WRK_BOOK_ID   ,
				 WH.YD_CARUD_STOP_LOC   , WH.YD_CARUD_SCH_REQ_GP  , WH.YD_CARUD_ARR_DT     , WH.YD_CARUD_CHK_DT    , WH.YD_CARUD_ST_DT         ,
				 WH.YD_CARUD_CMPL_DT    , WH.YD_CARUD_WRK_ACT_GP  , WH.YD_TRN_WRK_DELY_CD  , WH.YD_EQP_ID
				) VALUES (
				 DD.YD_WRK_HIST_ID      , DD.MODIFIER             , DD.MOD_DDTT            , DD.MODIFIER           , DD.MOD_DDTT               ,
				 DD.DEL_YN              , DD.YD_GP                , DD.STL_NO              , DD.YD_GNT_GP          , DD.BUY_SLAB_NO            ,
				 DD.STL_APPEAR_GP       , DD.ITEMNAME_CD          , DD.ORD_YEOJAE_GP       , DD.ORD_NO             , DD.ORD_DTL                ,
				 DD.STLKIND_CD          , DD.SPEC_ABBSYM          , DD.ORD_GP              , DD.CUST_CD            , DD.DEST_CD                ,
				 DD.DEMANDER_CD         , DD.DEST_TEL_NO          , DD.STL_PROG_CD         , DD.GOODS_GRADE        , DD.YD_MTL_W_GP            ,
				 DD.YD_MTL_T_GP         , DD.YD_MTL_L_GP          , DD.YD_MTL_T            , DD.YD_MTL_W           , DD.YD_MTL_L               ,
				 DD.YD_MTL_WT           , DD.YD_COIL_OUTDIA_GRP_GP, DD.COIL_INDIA          , DD.COIL_OUTDIA        , DD.SLAB_WO_RT_CD          ,
				 DD.ORD_HCR_GP          , DD.HCR_GP               , DD.SCARFING_YN         , DD.SCARFING_DONE_YN   , DD.REHEAT_SLAB_GP         ,
				 DD.ROLL_UNIT_GP        , DD.ROLL_UNIT_NAME       , DD.REFUR_CHG_LOT_NO    , DD.REFUR_CHG_PLN_SERNO, DD.YD_CONVEYOR_BRANCH_CD  ,
				 DD.HYSCO_TRANS_GP      , DD.COOL_METHOD          , DD.COOL_DONE_GP        , DD.RENTPROC_CD        , DD.DIST_DUE_DATE          ,
				 DD.YD_PILING_CD        , DD.YD_BOOK_OUT_LOC      , DD.PL_RCPT_LN_GP       , DD.FRTOMOVE_ORD_DATE  , DD.URGENT_FRTOMOVE_WORD_GP,
				 DD.SPOS_WLOC_CD        , DD.ARR_WLOC_CD          , DD.YD_AIM_RT_GP        , DD.YD_AIM_BAY_GP      , DD.YD_STK_LOT_TP          ,
				 DD.YD_STK_LOT_CD       , DD.TRANS_ORD_DATE       , DD.PL_L2_TRK_NO        , DD.DIST_SHIPASSIGN_GP , DD.EXPORT_SHIP_SET_NO     ,
				 DD.SHIPASSIGN_WORD_DATE, DD.SHIPASSIGN_WORD_SEQNO, DD.SHIP_CD             , DD.SHIP_NAME          , DD.RSHP_HOLD_NO           ,
				 DD.BERTH_NO            , DD.SAILNO               , DD.YD_CAR_USE_GP       , DD.CAR_NO             , DD.TRN_EQP_CD             ,
				 DD.CAR_KIND            , DD.TRANS_EQUIPMENT_TYPE , DD.CARD_NO             , DD.YD_CAR_SCH_ID      , DD.YD_TCAR_SCH_ID         ,
				 DD.YD_WBOOK_ID         , DD.YD_CRN_SCH_ID        , DD.YD_SCH_CD           , DD.YD_SCH_ST_GP       , DD.YD_SCH_REQ_GP          ,
				 DD.YD_SCH_PRIOR        , DD.YD_WBOOK_DT          , DD.YD_AID_WRK_YN       , DD.YD_TO_LOC_DCSN_MTD , DD.YD_TO_LOC_GUIDE        ,
				 DD.YD_SCH_DT           , DD.YD_WORD_DT           , DD.YD_UP_WO_LOC        , DD.YD_UP_WO_LAYER     , DD.YD_UP_WR_LOC           ,
				 DD.YD_UP_WR_LAYER      , DD.YD_UP_WRK_ACT_GP     , DD.YD_UP_CMPL_DT       , DD.YD_DN_WO_LOC       , DD.YD_DN_WO_LAYER         ,
				 DD.YD_DN_WR_LOC        , DD.YD_DN_WR_LAYER       , DD.YD_DN_WRK_ACT_GP    , DD.YD_DN_CMPL_DT      , DD.YD_WRK_HDS_DD          ,
				 DD.YD_WRK_DUTY         , DD.YD_WRK_PARTY         , DD.YD_CARLD_LEV_LOC    , DD.YD_CARLD_LEV_DT    , DD.YD_CARLD_PNT_WO_DT     ,
				 DD.YD_PNT_CD1          , DD.YD_PNT_CD2           , DD.YD_CARLD_WRK_BOOK_ID, DD.YD_CARLD_SCH_REQ_GP, DD.YD_CARLD_STOP_LOC      ,
				 DD.YD_CARLD_ARR_DT     , DD.YD_CARLD_ST_DT       , DD.YD_CARLD_CMPL_DT    , DD.YD_CARLD_WRK_ACT_GP, DD.YD_CARLD_CHK_DT        ,
				 DD.YD_CARUD_LEV_DT     , DD.YD_CARUD_PNT_WO_DT   , DD.YD_PNT_CD3          , DD.YD_PNT_CD4         , DD.YD_CARUD_WRK_BOOK_ID   ,
				 DD.YD_CARUD_STOP_LOC   , DD.YD_CARUD_SCH_REQ_GP  , DD.YD_CARUD_ARR_DT     , DD.YD_CARUD_CHK_DT    , DD.YD_CARUD_ST_DT         ,
				 DD.YD_CARUD_CMPL_DT    , DD.YD_CARUD_WRK_ACT_GP  , DD.YD_TRN_WRK_DELY_CD  , DD.YD_EQP_ID
				)
			 */
			commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.insSlabYdWrkHist", logId, methodNm, "작업이력(TB_YD_WRKHIST) 등록");				
			

			//차량상차 또는 하차완료 시
			if ("Y".equals(carLdCmplYn) || "Y".equals(carUdCmplYn)) {
				//소재이송지시 수정
				if ("Y".equals(carLdCmplYn)) {
					//차량상차완료 시
					jrParam.setField("LD_UD_GP", "L"); //상하차구분(상차)
					/*
					 * --크레인권하실적 상차 소재이송지시 수정 
						MERGE INTO TB_PT_STLFRTOMOVE FM USING (
						SELECT FM.STL_NO
						      ,FM.TRANSWORD_SEQNO
						      ,NVL(SUBSTR(:V_WR_DT,1,8),TO_CHAR(SYSDATE,'YYYYMMDD')) AS FRTOMOVE_CARLOAD_DATE
						      ,TM.YD_MTL_PLN_STR_FR_LOC_CD
						  FROM TB_PT_STLFRTOMOVE FM
						      ,(SELECT TM.STL_NO
						              ,SC.YD_STR_LOC_HIS1 AS YD_MTL_PLN_STR_FR_LOC_CD
						          FROM TB_YD_CARFTMVMTL TM
						              ,TB_PT_MSLABCOMM  SC
						         WHERE SC.MSLAB_NO         = TM.STL_NO
						           AND SC.RECORD_PROG_STAT = '2' --진행
						           AND TM.YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
						         UNION ALL
						        SELECT TM.STL_NO
						              ,SC.YD_STR_LOC_HIS1 AS YD_MTL_PLN_STR_FR_LOC_CD
						          FROM TB_YD_CARFTMVMTL TM
						              ,TB_PT_SLABCOMM   SC
						         WHERE SC.SLAB_NO          = TM.STL_NO
						           AND SC.RECORD_PROG_STAT = '2' --진행
						           AND TM.YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID) TM
						 WHERE TM.STL_NO = FM.STL_NO
						   AND FM.FRTOMOVE_STAT_CD IN ('1','3')
						   AND FM.TRANSWORD_SEQNO =
						       (SELECT MAX(MX.TRANSWORD_SEQNO)
						          FROM TB_PT_STLFRTOMOVE MX
						         WHERE MX.STL_NO = FM.STL_NO
						           AND MX.FRTOMOVE_STAT_CD NOT IN ('Z','C'))
						) DD ON (FM.STL_NO = DD.STL_NO AND FM.TRANSWORD_SEQNO = DD.TRANSWORD_SEQNO)
						WHEN MATCHED THEN UPDATE SET
							 FM.MODIFIER                 = :V_MODIFIER
						    ,FM.MOD_DDTT                 = SYSDATE
						    ,FM.FRTOMOVE_CARLOAD_DATE    = DD.FRTOMOVE_CARLOAD_DATE
						    ,FM.YD_MTL_PLN_STR_FR_LOC_CD = DD.YD_MTL_PLN_STR_FR_LOC_CD

					 */
					commDao.updateTx(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009StlMoveLd", logId, methodNm, "소재이송지시(TB_PT_STLFRTOMOVE) 상차 수정");				
					
				} else {
					//차량하차완료 시
					jrParam.setField("LD_UD_GP", "U"); //상하차구분(하차)
					/*
					 * --크레인권하실적 하차 소재이송지시 수정
						MERGE INTO TB_PT_STLFRTOMOVE FM USING (
						SELECT FM.STL_NO
						      ,FM.TRANSWORD_SEQNO
						      ,NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE) AS WR_DT
						      ,(SELECT SL.YD_STK_COL_GP||SL.YD_STK_BED_NO||SUBSTR(SL.YD_STK_LYR_NO,2,2)
						          FROM TB_YD_STKLYR SL
						         WHERE SL.STL_NO = TM.STL_NO
						           AND SL.YD_STK_LYR_MTL_STAT = 'C') AS YD_MTL_PLN_STR_TO_LOC_CD
						  FROM TB_YD_CARFTMVMTL  TM
						      ,TB_PT_STLFRTOMOVE FM
						 WHERE TM.STL_NO        = FM.STL_NO
						   AND TM.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
						   AND FM.FRTOMOVE_STAT_CD IN ('1','3')
						   AND FM.TRANSWORD_SEQNO =
						       (SELECT MAX(MX.TRANSWORD_SEQNO)
						          FROM TB_PT_STLFRTOMOVE MX
						         WHERE MX.STL_NO = FM.STL_NO
						           AND MX.FRTOMOVE_STAT_CD NOT IN ('Z','C'))
						   AND FM.STL_NO NOT IN
						            (SELECT STL_NO
						             FROM TB_YD_RETHTHIST
						             WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
						             --AND YD_RETHT_STAT_CD = '1'
						            )
						) DD ON (FM.STL_NO = DD.STL_NO AND FM.TRANSWORD_SEQNO = DD.TRANSWORD_SEQNO)
						WHEN MATCHED THEN UPDATE SET
							 FM.MODIFIER                 = :V_MODIFIER
						    ,FM.MOD_DDTT                 = SYSDATE
						    ,FM.FRTOMOVE_DONE_DATE       = DD.WR_DT
						    ,FM.FTMV_HDS_DD              = TO_CHAR(DD.WR_DT - 6 / 24,'YYYYMMDD')
						    ,FM.YD_MTL_PLN_STR_TO_LOC_CD = DD.YD_MTL_PLN_STR_TO_LOC_CD
						    ,FM.FRTOMOVE_STAT_CD         = '*'

					 */
					commDao.updateTx(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009StlMoveUd", logId, methodNm, "소재이송지시(TB_PT_STLFRTOMOVE) 하차 수정");				
					
					
				}

				//주편공통 수정
				/*
				 * --크레인권하실적 상하차 주편공통 수정 - 
					MERGE INTO TB_PT_MSLABCOMM SC USING (
					SELECT TM.STL_NO
					      ,:V_LD_UD_GP AS LD_UD_GP
					      ,TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS') AS WR_DT
					  FROM TB_YD_CARFTMVMTL TM
					      ,TB_PT_MSLABCOMM  SC
					 WHERE TM.STL_NO           = SC.MSLAB_NO
					   AND SC.RECORD_PROG_STAT = '2' --진행
					   AND TM.YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
					) DD ON (SC.MSLAB_NO = DD.STL_NO)
					WHEN MATCHED THEN UPDATE SET
					     SC.MODIFIER     = :V_MODIFIER
					    ,SC.MOD_DDTT     = SYSDATE
					    ,SC.FNL_REG_PGM  = 'rcvY3YDL009' --권하실적수신
					    ,SC.MATL_FTMV_DT = DECODE(DD.LD_UD_GP,'L',DD.WR_DT,SC.MATL_FTMV_DT)
					    ,SC.MATL_TKOV_DT = DECODE(DD.LD_UD_GP,'U',DD.WR_DT,SC.MATL_TKOV_DT)

				 */
				commDao.updateTx(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009MslabCommCar", logId, methodNm, "주편공통(TB_PT_MSLABCOMM) 상하차 수정");				
				
				//Slab공통 수정
				/*
				 * --크레인권하실적 상하차 Slab공통 수정 
					MERGE INTO TB_PT_MSLABCOMM SC USING (
					SELECT TM.STL_NO
					      ,:V_LD_UD_GP AS LD_UD_GP
					      ,TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS') AS WR_DT
					  FROM TB_YD_CARFTMVMTL TM
					      ,TB_PT_SLABCOMM   SC
					 WHERE TM.STL_NO           = SC.SLAB_NO
					   AND SC.RECORD_PROG_STAT = '2' --진행
					   AND TM.YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
					) DD ON (SC.MSLAB_NO = DD.STL_NO)
					WHEN MATCHED THEN UPDATE SET
					     SC.MODIFIER     = :V_MODIFIER
					    ,SC.MOD_DDTT     = SYSDATE
					    ,SC.FNL_REG_PGM  = 'rcvY3YDL009' --권하실적수신
					    ,SC.MATL_FTMV_DT = DECODE(DD.LD_UD_GP,'L',DD.WR_DT,SC.MATL_FTMV_DT)
					    ,SC.MATL_TKOV_DT = DECODE(DD.LD_UD_GP,'U',DD.WR_DT,SC.MATL_TKOV_DT)


				 */
				commDao.updateTx(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009SlabCommCar", logId, methodNm, "Slab공통(TB_PT_SLABCOMM) 상하차 수정");				
				
			}
			
			/**********************************************************
			* 9. 권하지시위치 단과 실적 단이 다르면 저장품제원 전문 전송( YDY3L002)			
			**********************************************************/
			slabUtils.printLog(logId, "권하지시위치 단과 실적 단이 다르면 YDY3L002DnWr [" + chgDnWrLayer + "] " , "SL");
			
//			String sDYD400_YN11 = slabComm.ApplyYn("DYD400", "D", "11");
//			slabUtils.printLog(logId, sDYD400_YN11+" >>크레인스케줄이면 EJB Call 적용여부[Y:적용 / N:미적용]============================", "S+");
//			if ("Y".equals(sDYD400_YN11)){
//				if ("DAPU01".equals(ydDnWrLoc.substring(0, 6)) 
//						|| "DAPU03".equals(ydDnWrLoc.substring(0, 6)) 
//						|| "DBPU05".equals(ydDnWrLoc.substring(0, 6))) { // Dpiler 보급베드일 경우엔 저장품제원 재전송
//					slabUtils.printLog(logId, ydDnWrLoc.substring(0, 6)+" >> Depiler 보급베드에서 저장품제원 전송[권하지시단 L2와 싱크 맞추기 위해]============================", "S+");
//					//위에서 보내는 저장품제원 전문은 로그 확인하여 판단후 삭제 여부 결정 
//					// YYS 2021-05-25
//					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L002DnWr", jrParam));	
//				}
//				
//				if (chgDnWrLayer) {
//					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L002DnWr", jrParam));				
//				}
//			}
			
			/**********************************************************
			* 10. 대차초기화 및 크레인작업실적응답 전문 전송( YDY3L005)
			**********************************************************/
			//크레인, 권상실적위치 및 권하실적위치 재료정보 Flex Server로 전송
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치
			resMsg.setField("YD_DN_WR_LOC", ydDnWrLoc); //야드권하실적위치
			
			if ("TC".equals(ydUpWrLoc.substring(2, 4)) && !"TC".equals(ydDnWrLoc.substring(2, 4)) ) {
				//대차 초기화 진행
				jrParam.setField("YD_WRK_PLAN_TCAR", "DXTC01"); //야드권하실적위치
				/*
				 * SELECT WB.YD_SCH_CD
				      ,WB.YD_SCH_PRIOR
				      ,WB.YD_BAY_GP     AS YD_LD_BAY_GP --상차동
				      ,WB.YD_AIM_BAY_GP AS YD_UD_BAY_GP --하차동
				      ,WM.WB_MTL_SH
				      ,WM.WB_MTL_WT
				      ,WB.YD_WBOOK_ID
				      ,WB.YD_WRK_PLAN_TCAR
				      ,(SELECT SR.CD_CONTENTS
				          FROM TB_YD_SCHRULE SR
				         WHERE SR.YD_SCH_CD = WB.YD_SCH_CD) AS YD_SCH_CD_NM
				  FROM TB_YD_WRKBOOK WB
				      ,(SELECT WB.YD_WBOOK_ID
				              ,COUNT(*)        AS WB_MTL_SH
				              ,SUM(SC.SLAB_WT) AS WB_MTL_WT
				              ,(SELECT COUNT(*)
				                  FROM TB_YD_CRNSCH CS
				                 WHERE CS.YD_WBOOK_ID = WB.YD_WBOOK_ID
				                   AND CS.YD_SCH_CD LIKE '______L_'
				                   AND CS.YD_WRK_PROG_STAT = '2'
				                   AND CS.DEL_YN           = 'N') AS CRN_SCH_CNT
				          FROM TB_YD_WRKBOOK    WB
				              ,TB_YD_WRKBOOKMTL WM
				              ,VW_YD_SLABCOMM   SC
				         WHERE WM.YD_WBOOK_ID      = WB.YD_WBOOK_ID
				           AND SC.SLAB_NO          = WM.STL_NO
				           AND WB.YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
				           AND WB.DEL_YN           = 'N'
				           AND WM.DEL_YN           = 'N'
				         GROUP BY WB.YD_WBOOK_ID) WM
				 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				   AND WM.CRN_SCH_CNT < 1
				 ORDER BY SUBSTR(WB.YD_SCH_CD,7,1), WB.YD_SCH_PRIOR, WB.YD_WBOOK_ID
				 */
				jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getTcarSchMgtWB", logId, methodNm, "대상  조회");
				if (jsChk.size() == 0) {
					jrParam.setField("YD_EQP_ID", "DXTC"); //야드권하실적위치
					/*--대차스케줄관리 대차정보조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getTcarSchMgtTC
					WITH TS AS (
					SELECT EQ.YD_EQP_ID
					      ,EQ.YD_CURR_BAY_GP
					      ,EQ.YD_HOME_BAY_GP
					      ,EQ.YD_EQP_STAT
					      ,EQ.YD_EQP_WRK_MODE
					      ,NVL(EQ.RCPT_TCAR_USE_YN,'N') AS AUTO_TCAR_SCH_YN --자동대차스케줄여부
					      ,TS.YD_CAR_PROG_STAT
					      ,TS.YD_CARLD_STOP_LOC
					      ,TS.YD_CARUD_STOP_LOC
					      ,TS.YD_EQP_WRK_SH
					      ,TS.YD_EQP_WRK_WT
					      ,CASE WHEN TS.YD_CAR_PROG_STAT BETWEEN '0' AND '4' THEN YD_CARLD_WRK_BOOK_ID
					            WHEN TS.YD_CAR_PROG_STAT BETWEEN '5' AND 'D' THEN YD_CARUD_WRK_BOOK_ID
					        END AS YD_WBOOK_ID
					      ,TS.YD_TCAR_SCH_ID
					  FROM TB_YD_EQP     EQ
					      ,TB_YD_TCARSCH TS
					 WHERE EQ.YD_EQP_ID = TS.YD_EQP_ID(+)
					   AND 'N'          = TS.DEL_YN(+)
					   AND EQ.YD_EQP_ID LIKE :V_YD_EQP_ID||'%'
					)
					SELECT TS.YD_EQP_ID
					      ,TS.YD_CURR_BAY_GP
					      ,TS.YD_HOME_BAY_GP
					      ,DECODE(TS.YD_EQP_STAT,'B','B'   ,'N'   ) AS YD_EQP_STAT
					      ,DECODE(TS.YD_EQP_STAT,'B','고장','정상') AS YD_EQP_STAT_NM
					      ,TS.YD_EQP_WRK_MODE
					      ,DECODE(TS.YD_EQP_WRK_MODE,'1','On-Line','Off-Line') AS YD_EQP_WRK_MODE_NM
					      ,TS.AUTO_TCAR_SCH_YN
					      ,TS.YD_CAR_PROG_STAT
					      ,(SELECT BR.CD_MNNG
					          FROM BRE.VW_CM_CODES BR
					         WHERE BR.CD_EN_ID  = 'YD_CAR_PROG_STAT'
					           AND BR.CD_CAT_ID = 'HS0000'
					           AND BR.CD_VAL    = TS.YD_CAR_PROG_STAT) AS YD_CAR_PROG_STAT_NM
					      ,SUBSTR(TS.YD_CARLD_STOP_LOC,2,1) AS YD_LD_BAY_GP --상차동
					      ,SUBSTR(TS.YD_CARUD_STOP_LOC,2,1) AS YD_UD_BAY_GP --하차동
					      ,NULLIF(WB.WB_MTL_SH,0)           AS WB_MTL_SH
					      ,NULLIF(WB.WB_MTL_WT,0)           AS WB_MTL_WT
					      ,NULLIF(TM.TC_MTL_SH,0)           AS TC_MTL_SH
					      ,NULLIF(TM.TC_MTL_WT,0)           AS TC_MTL_WT
					      ,WB.YD_SCH_CD
					      ,(SELECT SR.CD_CONTENTS
					          FROM TB_YD_SCHRULE SR
					         WHERE SR.YD_SCH_CD = WB.YD_SCH_CD) AS YD_SCH_CD_NM
					      ,TS.YD_WBOOK_ID
					      ,TS.YD_TCAR_SCH_ID
					  FROM TS
					      ,(SELECT WB.YD_WBOOK_ID
					              ,MIN(WB.YD_SCH_CD) AS YD_SCH_CD
					              ,COUNT(SC.SLAB_NO) AS WB_MTL_SH
					              ,SUM(SC.SLAB_WT)   AS WB_MTL_WT
					          FROM TB_YD_WRKBOOK    WB
					              ,TB_YD_WRKBOOKMTL WM
					              ,VW_YD_SLABCOMM   SC
					         WHERE WB.YD_WBOOK_ID IN (SELECT YD_WBOOK_ID FROM TS WHERE YD_WBOOK_ID IS NOT NULL)
					           AND WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
					           AND WM.STL_NO      = SC.SLAB_NO
					           AND WB.DEL_YN      = 'N'
					           AND WM.DEL_YN      = 'N'
					         GROUP BY WB.YD_WBOOK_ID) WB
					      ,(SELECT TS.YD_TCAR_SCH_ID
					              ,COUNT(SC.SLAB_NO) AS TC_MTL_SH
					              ,SUM(SC.SLAB_WT)   AS TC_MTL_WT
					          FROM TB_YD_TCARFTMVMTL TM, TS
					              ,VW_YD_SLABCOMM    SC
					         WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
					           AND TM.STL_NO         = SC.SLAB_NO
					           AND TM.DEL_YN         = 'N'
					         GROUP BY TS.YD_TCAR_SCH_ID) TM
					 WHERE TS.YD_TCAR_SCH_ID = TM.YD_TCAR_SCH_ID(+)
					   AND TS.YD_WBOOK_ID    = WB.YD_WBOOK_ID(+)
					 ORDER BY TS.YD_EQP_ID
				  */
					jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getTcarSchMgtTC", logId, methodNm, "대차정보");
					if (jsChk.size() == 1) {
						
						jrChk = jsChk.getRecord(0);

						//jrParam.setField("YD_CAR_SCH_ID", slabUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
						String ydCurrBayGp = slabUtils.trim(jrChk.getFieldString("YD_CURR_BAY_GP")); //차량하차완료여부
						String ydEqpIdTi = slabUtils.trim(jrChk.getFieldString("YD_EQP_ID"));
						
						GridData gridReq = new GridData();
						gridReq.addParam("userid", sModifier);
						gridReq.createHeader("CHECK", OperateGridData.t_checkbox);
						gridReq.createHeader("YD_CURR_BAY_GP", ydCurrBayGp);
						gridReq.createHeader("YD_EQP_ID", ydEqpIdTi);						
						
						gridReq.getHeader("CHECK").addValue("1", "");
						gridReq.getHeader("YD_CURR_BAY_GP").addValue(ydCurrBayGp, "");
						gridReq.getHeader("YD_EQP_ID").addValue(ydEqpIdTi, "");
						gridReq.setNavigateValue(methodNm); // 상위 Method 명
						gridReq.setIPAddress(logId);      // Logging 을 위한 ID
						
						JDTORecord jrRtn2  = slabUtils.getParam(logId, methodNm, sModifier); 	//전문 Return
						
						
						EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
						jrRtn2 = (JDTORecord)ejbConn.trx("trtTcarSchMgtTI", new Class[] { GridData.class }, new Object[] { gridReq });
						
						String rtnCd	 = slabUtils.nvl(jrRtn2.getFieldString("RTN_CD"), "0");
						String rtnMsg	 = slabUtils.nvl(jrRtn2.getFieldString("RTN_MSG"), "");
						slabUtils.printLog(logId, "trtTcarSchMgtTI[대차초기화 : =======rtnCd:" + rtnCd, "SL");
						slabUtils.printLog(logId, "trtTcarSchMgtTI[대차초기화 : =======rtnMsg:"+ rtnMsg, "SL");
						
						jrRtn = slabUtils.addSndData(jrRtn, jrRtn2);						
					}
				}
			}
			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY3L005(resMsg));
			}
			
			 String APPLY_YN43   = commDao.PSlabApplyYn("APPLY_YN43");
				//////////////////////////////////////////
				// 보류/해제 시 적용 YYS	
//			 if("Y".equals(APPLY_YN43)){
//				JDTORecordSet jsGetSchList = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getY3YDL007CS2", logId, methodNm, "크레인스케줄 조회");
//				if (jsGetSchList.size() > 0) {
//					ydCrnSchId    = slabUtils.trim(jsGetSchList.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
//					ydWrkProgStat = slabUtils.trim(jsGetSchList.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
//				}
//			 }
			 
			/**********************************************************
			* 13. 크레인작업지시요구 전문 호출( Y3YDL007)
			**********************************************************/
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			jrYdMsg.setField("JMS_TC_CD"       , "Y3YDL007"); //JMSTC코드 후판슬라브 적용
			jrYdMsg.setField("YD_EQP_ID"       , ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       ); //야드작업진행상태(권하완료)
			jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   ); //야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId); //야드크레인스케쥴ID
			jrYdMsg.setField("MODIFIER"        , sModifier  ); //수정자
			
			//크레인작업지시 전문을 추가
			JDTORecord jrGetYdMsg = this.rcvY3YDL007(jrYdMsg);
			String rtnCd	 = slabUtils.nvl(jrGetYdMsg.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrGetYdMsg.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId,  " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId,  " =======rtnMsg:"+ rtnMsg, "FL");
			
			slabUtils.printLog(logId, "YD_STK_COL_GP : "+ydDnWrLoc.substring(0, 6), "SL>>>>>>>>>>>>>>>>>>");
			slabUtils.printLog(logId, "YD_EQP_ID : "+ydEqpId, "SL>>>>>>>>>>>>>>>>>");
			
			jrRtn = slabUtils.addSndData(jrRtn, jrGetYdMsg);
			slabUtils.printLog(logId, "후판슬라브 END 크레인권하실적 : ["+ydCrnSchId+"]", "S+");
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "크레인권하실적 처리완료");
			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(slabUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "PSlabYdCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { slabComm.getYDY3L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인비상조업(Y3YDL010)
	 *      2020-11-19
	 *      염용선 비상조업처리는 하지않음
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	
	public JDTORecord rcvY3YDL010(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인비상조업실적[SlabYdL2RcvSeEJB.rcvY3YDL010] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printLog(logId, "삭제된 전문입니다.", "SL");
			slabUtils.printLog(logId, methodNm, "S-");
			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	



	/**
	 *      [A] 오퍼레이션명 : Y3수불구변경요구(Y3YDL011)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL011(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y3수불구변경요구[PSlabYdL2RcvSeEJB.rcvY3YDL011] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId  = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" )); //야드설비ID
			String ydUserId = slabUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(ydUserId)) { ydUserId = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			}

			/**********************************************************
			* 2. 적치Bed Table에 Update할 대상을 검색
			**********************************************************/
			int trtCnt = 0;	//처리건수
			int bedCnt = 6;	//Bed건수

			String ydStkBedActStat[] = new String[bedCnt]; //야드적치Bed활성상태

			//Bed건수 만큼 Set
			for (int ii = 0; ii < bedCnt; ii++) {
				ydStkBedActStat[ii] = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_USG_GP" + (ii + 1)));
				if (!"".equals(ydStkBedActStat[ii])) {
					trtCnt++;
				}
			}

			if (trtCnt <= 0) {
				throw new Exception("야드적치Bed활성상태(YD_STK_BED_USG_GP) 이상");
			}

			/**********************************************************
			* 3. 적치Bed Table에 활성상태를 Update
			**********************************************************/
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, ydUserId);
			
			
			for (int ii = 0; ii < bedCnt; ii++) {
				if (!"".equals(ydStkBedActStat[ii])) {
					String bedActStat = ydStkBedActStat[ii];
					
					jrParam.setField("YD_STK_BED_USG_GP"   , bedActStat); //야드적치열구분(현위치)--수입구('S'), 불출구('B'), Close('C')
					jrParam.setField("YD_STK_COL_GP"       , ydEqpId    ); //야드적치Bed활성상태(활성)
					jrParam.setField("YD_STK_BED_NO"       ,"0" + (ii + 1)); //야드적치열구분(현위치)
					
					/*
					 * 적치Bed 활성상태 수정 
						UPDATE TB_YD_STKBED
						   SET MODIFIER      = :V_MODIFIER
						      ,MOD_DDTT       = SYSDATE
						      ,YD_STK_BED_USG_GP = :V_YD_STK_BED_USG_GP
						 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
						   AND YD_STK_BED_NO         = :V_YD_STK_BED_NO
						   AND DEL_YN              = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatStkBedAct", logId, methodNm, "적치Bed(TB_YD_STKBED) 야드적치Bed활성상태 수정");
					
				}
			}

			
			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}




	/**
	 *      [A] 오퍼레이션명 : Take-Out완료(Y3YDL012 )
	 *      염용선 2020-09-09
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL012(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Take-Out완료[PSlabYdL2RcvSeEJB.rcvY3YDL012] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )); //야드설비ID
			String ydStkBedNo    = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"    )); //야드적치Bed번호
			String ydStkBedStlSh = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH")); //야드적치Bed재료매수
			String carryOutReqYn = slabUtils.trim(rcvMsg.getFieldString("CARRY_OUT_REQ_GP" )); //Carry-Out요구구분
			String stlNoBed      = slabUtils.trim(rcvMsg.getFieldString("STL_NO"));	           //처리되는 재료번호
			String sModifier     = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"       )); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			JDTORecord jrRtn     = slabUtils.getParam(logId, methodNm, sModifier);
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			} else if ("".equals(ydStkBedNo)) {
				throw new Exception("적치Bed번호(YD_STK_BED_NO) 없음");
			} else if ("".equals(ydStkBedStlSh)) {
				throw new Exception("적치Bed재료매수(YD_STK_BED_STL_SH) 없음");
			}
			
			boolean isNumer = StringUtils.isNumeric(ydStkBedNo);
			if (!isNumer) {
				throw new Exception("적치Bed번호(YD_STK_BED_NO)["+ydStkBedNo+"] 문자열 ");
			}
			
			
			/**********************************************************
			* 협폭재일경우 OVER CRAN 작업지시 생성
			**********************************************************/
			
			int cnt =6 ;			
			
			/**********************************************************
			* 2. 적치Bed 재료번호, 재료상태  및 저장품 Update
			**********************************************************/
			int lyrNo = 0;							//단번호
			int mtlSh = 0;							//Bed재료매수
			String[][] bedMtl = new String[cnt][2];	//Bed재료정보

			for (int ii = 0; ii < cnt; ii++) {
				for (int jj = 0; jj < 2; jj++) {
					bedMtl[ii][jj] = "";
				}
			}

			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
			
			jrParam.setField("YD_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO"      , ydStkBedNo); //야드적치Bed번호
			jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); //야드적치단재료상태(적치중)
			
			//기 등록된 Bed 재료정보 조회 - 이미 재료정보가 생성되어 있으면 다시 등록 안 함
			/*
			 * --Take-Out완료 Bed재료 조회 
				SELECT SL.YD_STK_LYR_NO
				      ,SL.STL_NO
				      ,ST.YD_AIM_BAY_GP
				  FROM TB_YD_STKLYR SL
				      ,TB_YD_STOCK  ST
				 WHERE SL.STL_NO        = ST.STL_NO(+)
				   AND SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
				   AND SL.STL_NO IS NOT NULL
				 ORDER BY SL.YD_STK_LYR_NO

			 */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getC3YDL004BedMtl", logId, methodNm, "Bed재료 조회");
	    		
			if (jsChk != null && jsChk.size() > 0) {
				mtlSh = jsChk.size();
				for (int ii = 0; ii < mtlSh; ii++) {
					lyrNo = Integer.parseInt(slabUtils.nvl(jsChk.getRecord(ii).getFieldString("YD_STK_LYR_NO"),"0"));	//야드적치단번호
					if (lyrNo > 0 && lyrNo < cnt) {
						bedMtl[lyrNo][0] = slabUtils.trim(jsChk.getRecord(ii).getFieldString("STL_NO"       ));	//재료번호
						bedMtl[lyrNo][1] = slabUtils.trim(jsChk.getRecord(ii).getFieldString("YD_AIM_BAY_GP"));	//야드목표동구분
					}
				}
			}
			
			
			String stlNo = "";	//재료번호
			mtlSh = Integer.parseInt(ydStkBedStlSh); //야드적치Bed재료매수
			
			//적치Bed 정보 Set
			for (int ii = 1; ii <= mtlSh; ii++) {
				stlNo = slabUtils.trim(rcvMsg.getFieldString("STL_NO" + ii));	//재료번호
				if ("".equals(stlNo)) {
					throw new Exception("재료번호(STL_NO" + ii + ") 없음");
				}
				
				jrParam.setField("STL_NO"       , stlNo    ); //재료번호
				jrParam.setField("YD_STK_LYR_NO", "00" + ii); //야드적치단번호
				jrParam.setField("YD_STK_LYR_NO2", "0" + ii); //야드적치단번호2
				
				slabUtils.printLog(logId, "TB_YD_STKLYR >> 재료번호 : "+bedMtl[ii][0], "SL");
				slabUtils.printLog(logId, "입력받은  >> 재료번호 : "+stlNo, "SL");
				
				//기 등록된 적치단에 재료번호가 없거나 다르면
				if (!stlNo.equals(bedMtl[ii][0])) {
					//20250124 픽업베드 DART 인출 -> DAPU 인출 정보가 와 중복적치현상 발생. 연주슬라브야드와 같이, 
					//이전위치 탐색하여, RT거나 다른 PU 일 경우  CLEAR
					
					jrParam.setField("STL_NO"       , stlNo    ); //재료번호
					JDTORecordSet chkResult = commDao.select(jrParam, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO", logId, methodNm, "재료 저장위치 검색");
					
					if(chkResult != null && chkResult.size() > 0) {
						String curYdStkColGp = chkResult.getRecord(0).getFieldString("YD_STK_COL_GP");
						String curYdStkBedNo = chkResult.getRecord(0).getFieldString("YD_STK_BED_NO");
						String curYdStkLyrNo = chkResult.getRecord(0).getFieldString("YD_STK_LYR_NO");
						
						slabUtils.printLog(logId, "["+stlNo+"] 기존 적치정보 적치열 ["+curYdStkColGp+"] 적치베드 ["+curYdStkBedNo+"] 적치단 ["+curYdStkLyrNo+"]", "SL");
						
						String curYd = curYdStkColGp.length()>=6 ? curYdStkColGp.substring(0,1) : "";
						String curEqp = curYdStkColGp.length()>=6 ? curYdStkColGp.substring(2,4) : "";
						String yd_gp  = ydEqpId.substring(0,1);//전문의 적치야드
						
						if( ydEqpId.equals(curYdStkColGp) &&
								ydStkBedNo.equals(curYdStkBedNo) &&
								("00" + (ii + 1)).equals(curYdStkLyrNo)){
								slabUtils.printLog(logId, "["+stlNo+"] 기존 적치정보와 take-out 실적정보 동일하므로 clear 하지 않음", "SL"); 
							}
							//적치야드가 기존과 같으며, 적치정보가 PU,PI 경우에만 기존 정보 clear (ora- 에러 중복적치 현상 방지)  
							else if(yd_gp.equals(curYd) && (curEqp.equals("PU") || curEqp.equals("RT") || curEqp.equals("PS") ) ){
								slabUtils.printLog(logId, "["+stlNo+"] 기존 적치정보와 take-out 실적정보 다르므로, 기존 정보 clear", "SL"); 
								
								JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
								jrParam2 = slabUtils.getParam(logId, methodNm, sModifier);
								jrParam2.setField("MODIFIER"				,sModifier);
								jrParam2.setField("STL_NO"				,"");
								jrParam2.setField("YD_STK_LYR_MTL_STAT"	,"E");
								jrParam2.setField("YD_STK_COL_GP"		,curYdStkColGp);
								jrParam2.setField("YD_STK_BED_NO"		,curYdStkBedNo);
								jrParam2.setField("YD_STK_LYR_NO"		,curYdStkLyrNo);
								
								commDao.update(jrParam2, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updSlabYdStkLyrStlNo", logId, methodNm, "적치단(TB_YD_STKLYR) 재료번호 수정");	
							
							}

							else{
								slabUtils.printLog(logId, "["+stlNo+"] 기존 적치정보가 같은야드 픽업베드가 아니므로, 처리 CLEAR 안함", "SL");
							}

					}
					else {
						slabUtils.printLog(logId, "["+stlNo+"] 기존 적치정보 존재하지 않음", "SL");
					}
					//적치단 Table Update
					/*
					 *적치단 재료번호 수정 
						UPDATE TB_YD_STKLYR
						   SET MODIFIER            = :V_MODIFIER
						      ,MOD_DDTT            = SYSDATE
						      ,STL_NO              = :V_STL_NO
						      ,YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
						 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
						   AND YD_STK_BED_NO       = :V_YD_STK_BED_NO
						   AND YD_STK_LYR_NO       = :V_YD_STK_LYR_NO
						   AND DEL_YN              = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updSlabYdStkLyrStlNo", logId, methodNm, "적치단(TB_YD_STKLYR) 재료번호 수정");				
					
					//슬라브공통 Table Update
					/*
					 * --Take-Out완료 Slab공통 저장위치 수정 

						MERGE INTO TB_PT_SLABCOMM SC USING (
						SELECT STL_NO
						      ,SUBSTR(YD_STR_LOC,1,1)      AS YD_GP
						      ,SUBSTR(YD_STR_LOC,2,1)      AS YD_BAY_GP
						      ,SUBSTR(YD_STR_LOC,3,2)      AS YD_EQP_GP
						      ,SUBSTR(YD_STR_LOC,5,2)      AS YD_STK_COL_NO
						      ,SUBSTR(YD_STR_LOC,7,2)      AS YD_STK_BED_NO
						      ,'0'||SUBSTR(YD_STR_LOC,9,2) AS YD_STK_LYR_NO
						      ,YD_STR_LOC
						  FROM (SELECT :V_STL_NO     AS STL_NO
						              , :V_YD_STK_COL_GP || :V_YD_STK_BED_NO || :V_YD_STK_LYR_NO2 AS YD_STR_LOC
						          FROM DUAL)
								  
						) DD ON (SC.SLAB_NO = DD.STL_NO AND SC.RECORD_PROG_STAT = '1')
						WHEN MATCHED THEN UPDATE SET
						     SC.MODIFIER         = :V_MODIFIER
						    ,SC.MOD_DDTT         = SYSDATE
						    ,SC.FNL_REG_PGM      = 'rcvY3YDL012'
						    ,SC.YD_GP            = DD.YD_GP
						    ,SC.YD_BAY_GP        = DD.YD_BAY_GP
						    ,SC.YD_EQP_GP        = DD.YD_EQP_GP
						    ,SC.YD_STK_COL_NO    = DD.YD_STK_COL_NO
						    ,SC.YD_STK_BED_NO    = DD.YD_STK_BED_NO
						    ,SC.YD_STK_LYR_NO    = DD.YD_STK_LYR_NO
						    ,SC.YD_STR_LOC       = DD.YD_STR_LOC
						    ,SC.YD_STR_LOC_HIS1  = SC.YD_STR_LOC
						    ,SC.YD_STR_LOC_HIS2  = SC.YD_STR_LOC_HIS1
					 */
					commDao.updateTx(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updSlabCommLyr", logId, methodNm, "슬라브공통(TB_PT_SLABCOMM) 현재저장위치 수정");				
					
					//저장품 Table 산적LotType 등 Upsert
					/*
					 * --저장품등록(재료번호) 
						MERGE INTO TB_YD_STOCK ST USING (
						SELECT SC.*
						      ,DECODE(SC.YD_AIM_RT_GP2,'E2',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EL' ELSE 'E2' END),
						                            'E4',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EM' ELSE 'E4' END),
						                            'E5',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EN' ELSE 'E5' END),
						              SC.YD_AIM_RT_GP2) AS YD_AIM_RT_GP
						FROM
						    (SELECT SC.SLAB_NO                   AS STL_NO           --재료번호
						          ,:V_MODIFIER                  AS MODIFIER         --수정자
						          ,SYSDATE                      AS MOD_DDTT         --수정일시
						          ,'N'                          AS DEL_YN           --삭제유무
						          ,SC.PTOP_PLNT_GP                                  --조업공장구분
						          ,SF_SLAB_YD_MTL_ITEM(SC.STL_APPEAR_GP, SC.SLAB_WO_RT_CD) AS YD_MTL_ITEM --야드재료품목
						          ,SC.ITEMNAME_CD                                   --품명코드
						          ,'2'                          AS YD_MTL_STAT      --야드재료상태(현물)
						          ,SC.CURR_PROG_CD              AS STL_PROG_CD      --재료진도코드
						          ,SC.ORD_YEOJAE_GP                                 --주문여재구분
						          ,SC.ORD_NO                                        --주문번호
						          ,SC.ORD_DTL                                       --주문행번
						          ,SF_SLAB_YD_AIM_RT_GP (SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , SC.SCARFING_YN     ,
						                                 SC.SCARFING_DONE_YN, SC.HCR_GP          , SC.ORD_YEOJAE_GP   ,
						                                 SC.STL_APPEAR_GP   ,
						                                 (CASE WHEN SC.MSLAB_RPR_MC_GP='G' AND SC.SCARFING_DONE_YN='N' AND SC.SCARFING_YN='Y' THEN 'AG' ELSE (SELECT BANK_WORK_RT     
						                                                                               FROM VW_YD_F_MSLABWO A
						                                                                              WHERE A.PLN_MSLAB_NO=SC.PLAN_SLAB_NO
						                                                                                   AND ROWNUM<=1
						                                                                              )
						                                      END )
						                                 ) AS YD_AIM_RT_GP2  --야드목표행선구분
						          ,SF_SLAB_YD_AIM_YD_GP (NVL(SL.YD_GP,CASE WHEN LENGTH(SC.SLAB_NO) > 9 THEN 'D' ELSE
						                                                   CASE WHEN FT.ARR_WLOC_CD = 'C3S01' THEN 'M'
						                                                        ELSE 'A' END
						                                              END),
						                                 SC.CURR_PROG_CD    , FT.ARR_WLOC_CD                          ) AS YD_AIM_YD_GP  --야드목표야드구분
						          ,SF_SLAB_YD_AIM_BAY_GP(NVL(SL.YD_GP,CASE WHEN LENGTH(SC.SLAB_NO) > 9 THEN 'D' ELSE 
						                                                   CASE WHEN FT.ARR_WLOC_CD = 'C3S01' THEN 'M'
						                                                        ELSE 'A' END
						                                              END),
						                                 SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , FT.ARR_WLOC_CD     ,
						                                 SC.SCARFING_YN     , SC.SCARFING_DONE_YN, SC.HCR_GP          ,
						                                 SC.ORD_YEOJAE_GP   , SC.STL_APPEAR_GP                        ) AS YD_AIM_BAY_GP --야드목표동구분
						          ,SF_SLAB_YD_STK_LOT_TP(SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , SC.SCARFING_YN     ,
						                                 SC.SCARFING_DONE_YN, SC.HCR_GP          , SC.ORD_YEOJAE_GP   ,
						                                 SC.STL_APPEAR_GP                                             ) AS YD_STK_LOT_TP --야드산적LotType
						          ,SF_SLAB_YD_STK_LOT_CD(SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , SC.SCARFING_YN     ,
						                                 SC.SCARFING_DONE_YN, SC.HCR_GP          , SC.ORD_YEOJAE_GP   ,
						                                 SC.STL_APPEAR_GP   , OC.PROD_DUE_DATE   , SC.STACK_LOT_NO    ,
						                                 MO.YD_CHG_NO       , FT.ARR_WLOC_CD                          ) AS YD_STK_LOT_CD --야드산적Lot코드
						          ,SC.STL_APPEAR_GP                                 --재료외형구분
						          ,SC.PLNT_PROC_CD                                  --공장공정코드
						          ,SC.OVERALL_STAMP_GRADE                           --종합판정등급
						          ,SC.SLAB_T                    AS YD_MTL_T         --야드재료두께
						          ,SC.SLAB_W                    AS YD_MTL_W         --야드재료폭
						          ,SC.SLAB_LEN                  AS YD_MTL_L         --야드재료길이
						          ,SC.SLAB_WT                   AS YD_MTL_WT        --야드재료중량
						          ,SC.SLAB_WO_RT_CD                                 --Slab지시행선코드
						          ,SC.ORD_HCR_GP                                    --설계HCR구분
						          ,SC.HCR_GP                                        --HCR구분
						          ,NVL(SC.SCARFING_YN     ,'N') AS SCARFING_YN      --Scarfing여부
						          ,NVL(SC.SCARFING_DONE_YN,'N') AS SCARFING_DONE_YN --Scarfing완료유무
						          ,SC.HANDSCARFING_YN                               --HandScarfing유무
						          ,SC.WO_MSLAB_RPR_MTD                              --지시주편손질방법
						          ,SC.REHEAT_SLAB_GP                                --재열재구분
						          ,MO.ROLL_UNIT_GP                                  --Roll단위구분
						          ,MO.ROLL_UNIT_NAME                                --Roll단위명
						          ,MO.REFUR_CHG_LOT_NO                              --가열로장입Lot번호
						          ,MO.REFUR_CHG_PLN_SERNO                           --가열로장입예정일련번호
						          ,OC.ORD_GP                                        --수주구분
						          ,OC.CUST_CD                                       --고객코드
						          ,OC.DEST_CD                                       --목적지코드
						          ,SC.DEMANDER_CD                                   --수요가코드
						          ,OC.GOODS_GRADE                                   --제품등급
						          ,OC.YD_RCPT_STR_LOC                               --야드입고저장위치
						          ,OC.DIST_DUE_DATE                                 --출하기한일
						          ,OC.EXPORT_SHIP_SET_NO                            --수출재배선번호
						          ,OC.DELIVER_TERM_CD                               --인도조건코드
						          ,OC.DETAIL_ARR_CD                                 --상세착지코드
						          ,HC.STLKIND_CD                                    --강종코드
						          ,SC.SPEC_ABBSYM                                   --규격약호
						          ,SC.CCM_NO                    AS CC_CCM_NO        --연주CCM번호
						          ,SC.PARENT_SLAB_NO            AS MMATL_FEE_NO     --모재료번호
						          ,FT.WO_CAR_PLNT_PROC_CD                           --지시차공장공정코드
						          ,FT.ORD_BEFO_PROG_CD                              --지시전진도코드
						          ,FT.ARR_WLOC_CD                                   --착지개소코드
						          ,FT.URGENT_FRTOMOVE_WORD_GP                       --긴급이송작업지시구분
						          ,SC.SCARFING_DEPTH                                --Scarfing깊이
						          ,MO.YD_CHG_NO                                     --야드장입순위
						          ,MO.PL_MPL_NO                                     --후판날판번호
						          ,SL.YD_STK_COL_GP                                 --야드적치열구분
						          ,SL.YD_STK_BED_NO                                 --야드적치Bed번호
						          ,SL.YD_STK_LYR_NO                                 --야드적치단번호
						      FROM VW_YD_SLABCOMM SC
						          ,TB_PT_HEATCOMM HC
						          ,TB_PT_OSCOMM   OC
						          ,(SELECT STL_NO
						                  ,YD_STK_COL_GP
						                  ,YD_STK_BED_NO
						                  ,YD_STK_LYR_NO
						                  ,SUBSTR(YD_STK_COL_GP,1,1) AS YD_GP
						              FROM TB_YD_STKLYR
						             WHERE STL_NO = :V_STL_NO
						               AND YD_STK_LYR_MTL_STAT IN ('C','U')) SL
						          ,(SELECT STL_NO
						                  ,WO_CAR_PLNT_PROC_CD
						                  ,ORD_BEFO_PROG_CD
						                  ,ARR_WLOC_CD
						                  ,URGENT_FRTOMOVE_WORD_GP
						              FROM TB_PT_STLFRTOMOVE FT
						             WHERE STL_NO = :V_STL_NO
						               AND TRANSWORD_SEQNO = (SELECT MAX(MS.TRANSWORD_SEQNO)
						                                        FROM TB_PT_STLFRTOMOVE MS
						                                       WHERE MS.STL_NO = FT.STL_NO
						                                         AND MS.FRTOMOVE_STAT_CD IN ('1','3'))) FT --(이송지시확정,야드수신완료)
						          ,(SELECT STL_NO
						                  ,SUBSTR(ROLL_UNIT_NAME,7,1) AS ROLL_UNIT_GP
						                  ,ROLL_UNIT_NAME
						                  ,REFUR_CHG_LOT_NO
						                  ,REFUR_CHG_PLN_SERNO
						                  ,YD_CHG_NO
						                  ,NULL AS PL_MPL_NO
						              FROM USRCTA.TB_CT_L_HRMILLWO
						             WHERE STL_NO = :V_STL_NO
						               AND CT_MILL_SPEC_WRK_STAT_GP >= '3'
						             UNION ALL
						            SELECT STL_NO
						                  ,SUBSTR(ROLL_UNIT_NAME,7,1) AS ROLL_UNIT_GP
						                  ,ROLL_UNIT_NAME
						                  ,REFUR_CHG_LOT_NO
						                  ,REFUR_CHG_PLN_SERNO
						                  ,YD_CHG_NO
						                  ,PL_MPL_NO
						              FROM USRCTA.TB_CT_N_PLMPLWO
						             WHERE STL_NO = :V_STL_NO
						               AND CT_MILL_SPEC_WRK_STAT_GP >= '3') MO
						     WHERE SC.SLAB_NO = SL.STL_NO(+)
						       AND SC.HEAT_NO = HC.HEAT_NO(+)
						       AND SC.ORD_NO  = OC.ORD_NO(+)
						       AND SC.ORD_DTL = OC.ORD_DTL(+)
						       AND SC.SLAB_NO = FT.STL_NO(+)
						       AND SC.SLAB_NO = MO.STL_NO(+)
						       AND SC.SLAB_NO = :V_STL_NO
						       AND ROWNUM     = 1
						    ) SC
						) DD ON (ST.STL_NO = DD.STL_NO)
						WHEN MATCHED THEN UPDATE SET
							    ST.MODIFIER                = DD.MODIFIER
						       ,ST.MOD_DDTT                = DD.MOD_DDTT
						       ,ST.DEL_YN                  = DD.DEL_YN
						       ,ST.PTOP_PLNT_GP            = DD.PTOP_PLNT_GP
						       ,ST.YD_MTL_ITEM             = DD.YD_MTL_ITEM
						       ,ST.ITEMNAME_CD             = DD.ITEMNAME_CD
						       ,ST.YD_MTL_STAT             = DD.YD_MTL_STAT
						       ,ST.STL_PROG_CD             = DD.STL_PROG_CD
						       ,ST.ORD_YEOJAE_GP           = DD.ORD_YEOJAE_GP
						       ,ST.ORD_NO                  = DD.ORD_NO
						       ,ST.ORD_DTL                 = DD.ORD_DTL
						       ,ST.YD_AIM_RT_GP            = DD.YD_AIM_RT_GP
						       ,ST.YD_AIM_YD_GP            = DD.YD_AIM_YD_GP
						       ,ST.YD_AIM_BAY_GP           = DD.YD_AIM_BAY_GP
						       ,ST.YD_STK_LOT_TP           = DD.YD_STK_LOT_TP
						       ,ST.YD_STK_LOT_CD           = DD.YD_STK_LOT_CD
						       ,ST.STL_APPEAR_GP           = DD.STL_APPEAR_GP
						       ,ST.PLNT_PROC_CD            = DD.PLNT_PROC_CD
						       ,ST.OVERALL_STAMP_GRADE     = DD.OVERALL_STAMP_GRADE
						       ,ST.YD_MTL_T                = DD.YD_MTL_T
						       ,ST.YD_MTL_W                = DD.YD_MTL_W
						       ,ST.YD_MTL_L                = DD.YD_MTL_L
						       ,ST.YD_MTL_WT               = DD.YD_MTL_WT
						       ,ST.SLAB_WO_RT_CD           = DD.SLAB_WO_RT_CD
						       ,ST.ORD_HCR_GP              = DD.ORD_HCR_GP
						       ,ST.HCR_GP                  = DD.HCR_GP
						       ,ST.SCARFING_YN             = DD.SCARFING_YN
						       ,ST.SCARFING_DONE_YN        = DD.SCARFING_DONE_YN
						       ,ST.HANDSCARFING_YN         = DD.HANDSCARFING_YN
						       ,ST.WO_MSLAB_RPR_MTD        = DD.WO_MSLAB_RPR_MTD
						       ,ST.REHEAT_SLAB_GP          = DD.REHEAT_SLAB_GP
						       ,ST.ROLL_UNIT_GP            = DD.ROLL_UNIT_GP
						       ,ST.ROLL_UNIT_NAME          = DD.ROLL_UNIT_NAME
						       ,ST.REFUR_CHG_LOT_NO        = DD.REFUR_CHG_LOT_NO
						       ,ST.REFUR_CHG_PLN_SERNO     = DD.REFUR_CHG_PLN_SERNO
						       ,ST.ORD_GP                  = DD.ORD_GP
						       ,ST.CUST_CD                 = DD.CUST_CD
						       ,ST.DEST_CD                 = DD.DEST_CD
						       ,ST.DEMANDER_CD             = DD.DEMANDER_CD
						       ,ST.GOODS_GRADE             = DD.GOODS_GRADE
						       ,ST.YD_RCPT_STR_LOC         = DD.YD_RCPT_STR_LOC
						       ,ST.DIST_DUE_DATE           = DD.DIST_DUE_DATE
						       ,ST.EXPORT_SHIP_SET_NO      = DD.EXPORT_SHIP_SET_NO
						       ,ST.DELIVER_TERM_CD         = DD.DELIVER_TERM_CD
						       ,ST.DETAIL_ARR_CD           = DD.DETAIL_ARR_CD
						       ,ST.STLKIND_CD              = DD.STLKIND_CD
						       ,ST.SPEC_ABBSYM             = DD.SPEC_ABBSYM
						       ,ST.CC_CCM_NO               = DD.CC_CCM_NO
						       ,ST.MMATL_FEE_NO            = DD.MMATL_FEE_NO
						       ,ST.WO_CAR_PLNT_PROC_CD     = DD.WO_CAR_PLNT_PROC_CD
						       ,ST.ORD_BEFO_PROG_CD        = DD.ORD_BEFO_PROG_CD
						       ,ST.ARR_WLOC_CD             = DD.ARR_WLOC_CD
						       ,ST.URGENT_FRTOMOVE_WORD_GP = DD.URGENT_FRTOMOVE_WORD_GP
						       ,ST.SCARFING_DEPTH          = DD.SCARFING_DEPTH
						       ,ST.YD_CHG_NO               = DD.YD_CHG_NO
						       ,ST.PL_MPL_NO               = DD.PL_MPL_NO
						       ,ST.YD_STK_COL_GP           = DD.YD_STK_COL_GP
						       ,ST.YD_STK_BED_NO           = DD.YD_STK_BED_NO
						       ,ST.YD_STK_LYR_NO           = DD.YD_STK_LYR_NO
						WHEN NOT MATCHED THEN
						INSERT (ST.STL_NO                 , ST.REGISTER           , ST.REG_DDTT           , ST.MODIFIER        , ST.MOD_DDTT     ,
						        ST.DEL_YN                 , ST.PTOP_PLNT_GP       , ST.YD_MTL_ITEM        , ST.ITEMNAME_CD     , ST.YD_MTL_STAT  ,
						        ST.STL_PROG_CD            , ST.ORD_YEOJAE_GP      , ST.ORD_NO             , ST.ORD_DTL         , ST.YD_AIM_RT_GP ,
						        ST.YD_AIM_YD_GP           , ST.YD_AIM_BAY_GP      , ST.YD_STK_LOT_TP      , ST.YD_STK_LOT_CD   , ST.STL_APPEAR_GP,
						        ST.PLNT_PROC_CD           , ST.OVERALL_STAMP_GRADE, ST.YD_MTL_T           , ST.YD_MTL_W        , ST.YD_MTL_L     ,
						        ST.YD_MTL_WT              , ST.SLAB_WO_RT_CD      , ST.ORD_HCR_GP         , ST.HCR_GP          , ST.SCARFING_YN  ,
						        ST.SCARFING_DONE_YN       , ST.HANDSCARFING_YN    , ST.WO_MSLAB_RPR_MTD   , ST.REHEAT_SLAB_GP  , ST.ROLL_UNIT_GP ,
						        ST.ROLL_UNIT_NAME         , ST.REFUR_CHG_LOT_NO   , ST.REFUR_CHG_PLN_SERNO, ST.ORD_GP          , ST.CUST_CD      ,
						        ST.DEST_CD                , ST.DEMANDER_CD        , ST.GOODS_GRADE        , ST.YD_RCPT_STR_LOC , ST.DIST_DUE_DATE,
						        ST.EXPORT_SHIP_SET_NO     , ST.DELIVER_TERM_CD    , ST.DETAIL_ARR_CD      , ST.STLKIND_CD      , ST.SPEC_ABBSYM  ,
						        ST.CC_CCM_NO              , ST.MMATL_FEE_NO       , ST.WO_CAR_PLNT_PROC_CD, ST.ORD_BEFO_PROG_CD, ST.ARR_WLOC_CD  ,
						        ST.URGENT_FRTOMOVE_WORD_GP, ST.SCARFING_DEPTH     , ST.YD_CHG_NO          , ST.PL_MPL_NO       , ST.YD_STK_COL_GP,
						        ST.YD_STK_BED_NO          , ST.YD_STK_LYR_NO)
						VALUES (DD.STL_NO                 , DD.MODIFIER           , DD.MOD_DDTT           , DD.MODIFIER        , DD.MOD_DDTT     ,
						        DD.DEL_YN                 , DD.PTOP_PLNT_GP       , DD.YD_MTL_ITEM        , DD.ITEMNAME_CD     , DD.YD_MTL_STAT  ,
						        DD.STL_PROG_CD            , DD.ORD_YEOJAE_GP      , DD.ORD_NO             , DD.ORD_DTL         , DD.YD_AIM_RT_GP ,
						        DD.YD_AIM_YD_GP           , DD.YD_AIM_BAY_GP      , DD.YD_STK_LOT_TP      , DD.YD_STK_LOT_CD   , DD.STL_APPEAR_GP,
						        DD.PLNT_PROC_CD           , DD.OVERALL_STAMP_GRADE, DD.YD_MTL_T           , DD.YD_MTL_W        , DD.YD_MTL_L     ,
						        DD.YD_MTL_WT              , DD.SLAB_WO_RT_CD      , DD.ORD_HCR_GP         , DD.HCR_GP          , DD.SCARFING_YN  ,
						        DD.SCARFING_DONE_YN       , DD.HANDSCARFING_YN    , DD.WO_MSLAB_RPR_MTD   , DD.REHEAT_SLAB_GP  , DD.ROLL_UNIT_GP ,
						        DD.ROLL_UNIT_NAME         , DD.REFUR_CHG_LOT_NO   , DD.REFUR_CHG_PLN_SERNO, DD.ORD_GP          , DD.CUST_CD      ,
						        DD.DEST_CD                , DD.DEMANDER_CD        , DD.GOODS_GRADE        , DD.YD_RCPT_STR_LOC , DD.DIST_DUE_DATE,
						        DD.EXPORT_SHIP_SET_NO     , DD.DELIVER_TERM_CD    , DD.DETAIL_ARR_CD      , DD.STLKIND_CD      , DD.SPEC_ABBSYM  ,
						        DD.CC_CCM_NO              , DD.MMATL_FEE_NO       , DD.WO_CAR_PLNT_PROC_CD, DD.ORD_BEFO_PROG_CD, DD.ARR_WLOC_CD  ,
						        DD.URGENT_FRTOMOVE_WORD_GP, DD.SCARFING_DEPTH     , DD.YD_CHG_NO          , DD.PL_MPL_NO       , DD.YD_STK_COL_GP,
						        DD.YD_STK_BED_NO          , DD.YD_STK_LYR_NO)
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.insSlabYdStock", logId, methodNm, "저장품(TB_YD_STOCK) 등록");				
					
				} else if ("".equals(bedMtl[ii][1])) {
					//기 등록된 야드목표동구분이 없으면 저장품 Table에 등록되지 않은 것으로 간주하여 저장품 정보 등록
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.insSlabYdStock", logId, methodNm, "저장품(TB_YD_STOCK) 등록");				
					
				}
			}
			
			
			/**********************************************************
			* 3. Carry-Out 처리
			**********************************************************/
			//Carry-Out요구구분이 'Y'이면
			//협폭재 및 장척재(슬라브 길이 4560 이상)일때 오버크레인으로 작업 지시 내려감
			// Y:일반(piler고장시) S:협폭재 L:장척재
			slabUtils.printLog(logId, "carryOutReqYn : "+carryOutReqYn+", ydEqpId : "+ydEqpId+", ydStkBedNo : "+ydStkBedNo, "SL");
			if ("Y".equals(carryOutReqYn)||"S".equals(carryOutReqYn)||"L".equals(carryOutReqYn)) {
				JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, sModifier);
				
				//설비인출요구
				jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ411"               ); //JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
				jrYdMsg.setField("YD_STK_BED_NO"     , ydStkBedNo               ); //야드적치Bed번호
				jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); //야드스케쥴기동구분(Auto)
				jrYdMsg.setField("YD_TO_LOC_12"      , carryOutReqYn            ); //장척재 일때 권하위치 지정 베드 셋팅
				
				
				//전송할 전문에 추가
				jrRtn = slabUtils.addSndData(jrRtn,jrYdMsg);
			}

			/**********************************************************
			* 4. 후판Slab이상재 등록 처리
			*    - 후판슬라브 A동 #1 픽업설비만 해당(- 평량기가 #1 R/T에만 설치) 
			**********************************************************/
			if("DAPU02".equals(ydEqpId)){
				 
				jrParam.setField("SLAB_NO", slabUtils.trim(rcvMsg.getFieldString("STL_NO"))); //Slab번호
				
				/*
				 * --Take-Out완료 후판Slab이상재 조회 
				SELECT SLAB_NO        --Slab번호
				      ,AB_OCCR_RSN_CD --이상재관리코드
				      ,SLAB_WO_RT_CD  --Slab지시행선
				      ,CAL_SLAB_WT    --이론중량
				      ,A1_SLAB_WT     --평량중량
				      ,SLAB_WT_DIFF   --차이
				      ,ILBAN_U        --일반재초과
				      ,ILBAN_D        --일반재미달 
				      ,SCARF_U        --스카핑초과
				      ,SCARF_D        --스카핑미달
				      ,SF_YN          --스카핑재여부
				  FROM (SELECT SLAB_NO
				              ,SLAB_WO_RT_CD
				              ,CAL_SLAB_WT
				              ,A1_SLAB_WT
				              ,SLAB_WT_DIFF
				              ,CAL_SLAB_WT *  0.04 AS ILBAN_U
				              ,CAL_SLAB_WT * -0.01 AS ILBAN_D
				              ,CAL_SLAB_WT *  0.05 AS SCARF_U
				              ,0                   AS SCARF_D
				              ,SF_YN
				              ,CASE WHEN SF_YN = 'Y' THEN --스카핑재
				              	         CASE WHEN SLAB_WT_DIFF < 0                   THEN 'K05' --중량미달
				              	              WHEN SLAB_WT_DIFF > CAL_SLAB_WT *  0.05 THEN 'K06' --중량초과
				              	         END
				              	    ELSE                  --일반재
				              	         CASE WHEN SLAB_WT_DIFF < CAL_SLAB_WT * -0.01 THEN 'K05' --중량미달
				              	              WHEN SLAB_WT_DIFF > CAL_SLAB_WT *  0.04 THEN 'K06' --중량초과
				              	         END
				               END AS AB_OCCR_RSN_CD
				          FROM (SELECT SLAB_NO
				                      ,SLAB_WO_RT_CD
				                      ,CASE WHEN SCARFING_YN = 'Y' AND SCARFING_DONE_YN = 'Y' THEN 'Y' ELSE 'N' END AS SF_YN --스카핑재여부
				                      ,CAL_SLAB_WT
				                      ,A1_SLAB_WT
				                      ,A1_SLAB_WT - CAL_SLAB_WT AS SLAB_WT_DIFF
				                  FROM TB_PT_SLABCOMM
				                 WHERE SLAB_NO = :V_SLAB_NO
				                   AND A1_SLAB_WT > 0)
				        ) --2차절단 평량실적
				 WHERE AB_OCCR_RSN_CD IS NOT NULL
				 */
				
				JDTORecordSet jsAbjchk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getC3YDL004AbMtl", logId, methodNm, "후판Slab이상재 조회");
			    	
				
				JDTORecord jrAbHist = null;
				
				if (jsAbjchk != null && jsAbjchk.size() > 0) {
					
					String sSlabNo      = slabUtils.trim(jsAbjchk.getRecord(0).getFieldString("SLAB_NO")); 			//Slab번호
					String sAbOccrRsnCd = slabUtils.trim(jsAbjchk.getRecord(0).getFieldString("AB_OCCR_RSN_CD")); 	//이상재관리코드
					String sSlabWoRtCd  = slabUtils.trim(jsAbjchk.getRecord(0).getFieldString("SLAB_WO_RT_CD"));  	//지시행선
					String sCalSlabWt   = slabUtils.trim(jsAbjchk.getRecord(0).getFieldString("CAL_SLAB_WT"));  	//이론중량
					String sA1SlabWt    = slabUtils.trim(jsAbjchk.getRecord(0).getFieldString("A1_SLAB_WT"));  		//평량중량
					String sSlabWtDiff  = slabUtils.trim(jsAbjchk.getRecord(0).getFieldString("SLAB_WT_DIFF"));  	//차이
					String sIlbanU      = slabUtils.trim(jsAbjchk.getRecord(0).getFieldString("ILBAN_U"));  		//일반재초과
					String sIlbanD      = slabUtils.trim(jsAbjchk.getRecord(0).getFieldString("ILBAN_D"));  		//일반재미달
					String sScarfU      = slabUtils.trim(jsAbjchk.getRecord(0).getFieldString("SCARF_U"));  		//스카핑초과
					String sScarfD      = slabUtils.trim(jsAbjchk.getRecord(0).getFieldString("SCARF_D"));  		//스카핑미달
					String sSfYn        = slabUtils.trim(jsAbjchk.getRecord(0).getFieldString("SF_YN"));  			//스카핑여부					
					String sSndbkGpEtc  = ""; //SNDBK_GP_ETC
					
					sSndbkGpEtc  = "슬라브번호="+sSlabNo+"/";
					sSndbkGpEtc += "이상재코드="+sAbOccrRsnCd+"/";
					sSndbkGpEtc += "이론중량	="+sCalSlabWt+"/";
					sSndbkGpEtc += "평량중량	="+sA1SlabWt+"/";
					sSndbkGpEtc += "차이		="+sSlabWtDiff+"/";
					sSndbkGpEtc += "일반재초과="+sIlbanU+"/";
					sSndbkGpEtc += "일반재미달="+sIlbanD+"/";
					sSndbkGpEtc += "스카핑초과="+sScarfU+"/";
					sSndbkGpEtc += "스카핑미달="+sScarfD+"/";
					sSndbkGpEtc += "스카핑여부="+sSfYn+"/";
					
					jrParam.setField("AB_OCCR_RSN_CD"     , sAbOccrRsnCd);		 //이상재관리코드
					jrParam.setField("YD_ABMTL_ASGN_DD"   , slabUtils.getDate8()); //이상재지정일
					jrParam.setField("SNDBK_GP_ETC"   	, sSndbkGpEtc); 		 //이상재로그
					 
					//저장품 Table Update - V_SLAB_NO/V_AB_OCCR_RSN_CD/V_YD_ABMTL_ASGN_DD/V_SNDBK_GP_ETC
					/*
					 * --후판Slab이상재 등록 
					UPDATE TB_YD_STOCK
					SET YD_ABMTL_RSN_CD  = :V_AB_OCCR_RSN_CD,
					    YD_ABMTL_ASGN_DD = :V_YD_ABMTL_ASGN_DD,
					    SNDBK_GP_ETC     = :V_SNDBK_GP_ETC
					WHERE STL_NO         = :V_SLAB_NO
					 */
						
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updSlabYdStockAbMtl", logId, methodNm, "저장품(TB_YD_STOCK) 이상재코드 수정");				
						
					 /*
					  * 이상재 이력등록 
					  * 이상재 화면처리 등록과 동일한 메소드 사용
					  */
						jrAbHist 	= slabUtils.getParam(logId, methodNm, sModifier);
						jrAbHist.setField("STL_NO"				, sSlabNo);
						jrAbHist.setField("YD_ABMTL_RSN_CD"	, sAbOccrRsnCd);
						jrAbHist.setField("YD_ABMTL_HD_MTD_CD"	, "");
						jrAbHist.setField("YD_ABMTL_GRD"		, "");
						jrAbHist.setField("YD_ABMTL_REM"		, "");
						jrAbHist.setField("YD_ABMTL_ASGN_DD"	, slabUtils.getDate8());
						jrAbHist.setField("REGISTER"			, sModifier);
						
						/*
						 * INSERT INTO TB_YD_ABSLAB_HIST
							( STL_NO
							, STEP_NO
							, SEQ_ID
							, REGISTER
							, REG_DDTT
							, MODIFIER
							, MOD_DDTT
							, DEL_YN
							, YD_ABMTL_RSN_CD
							, YD_ABMTL_HD_MTD_CD
							, YD_ABMTL_GRD
							, YD_ABMTL_REM
							, YD_ABMTL_ASGN_DD
							)
							VALUES (
							  :V_STL_NO
							, (SELECT NVL(MAX(SUBSTR(STEP_NO,1,2)),0) + 1 
							     FROM TB_YD_ABSLAB_HIST 
								WHERE STL_NO = :V_STL_NO)
							, YD_PILING_CD_HIST_SEQ.NEXTVAL
							, :V_REGISTER
							, SYSDATE
							, :V_REGISTER
							, SYSDATE
							, 'N'
							, :V_YD_ABMTL_RSN_CD
							, :V_YD_ABMTL_HD_MTD_CD
							, :V_YD_ABMTL_GRD
							, :V_YD_ABMTL_REM
							, :V_YD_ABMTL_ASGN_DD
							)
						 */
					    commDao.update(jrAbHist, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.insYdAbSlabHist", logId, methodNm, "저장품(TB_YD_STOCK) 이상재코드 수정");				
						
					JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, sModifier);
	
					//설비인출요구
					jrYdMsg.setField("JMS_TC_CD"         , "YDCTJ035"               ); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("SLAB_NO"         	, sSlabNo                  ); //재료번호
					jrYdMsg.setField("PTOP_PLNT_GP"     , sSlabWoRtCd              ); //지시행선
					jrYdMsg.setField("AB_OCCR_RSN_CD"   , sAbOccrRsnCd             ); //이상재코드
					jrYdMsg.setField("REGISTER"        	, sModifier                ); //등록자
					jrYdMsg.setField("REG_DDTT"         , slabUtils.getDateTime14()); //등록일시
					jrYdMsg.setField("PROCESS_GP"       , "1"                 	   ); //등록코드(1)
					 
					//전송할 전문에 추가
					jrRtn = slabUtils.addSndData(jrRtn,jrYdMsg);
					
				}
			}
			
			
			//PIDEV	
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "슬라브 이동실적", "APPPI1", "*", "*");
//			if("Y".equals(sApplyYnPI)) {
				//MES_PI YYS 20220829 ////////////////////////////////////////////////
				JDTORecord jrYdMesPiMsg = slabUtils.getParam(logId, methodNm, sModifier);
				String toLoc      = "";//착지코드
				String hdsIniDate = "";//계상일자
				
				//221114 장척재 RT take-out -> 야드 carry-out 에 따라, DA,DB 까지만 확인
				if("DA".equals(ydEqpId.substring(0,2))){
					toLoc = "S100";//1후판재
				}
				else if("DB".equals(ydEqpId.substring(0,2))){
					toLoc = "S200";//1후판재
				}
				
				/*
				if("DAPU02".equals(ydEqpId) || "DAPU04".equals(ydEqpId)){
					toLoc = "S100";//1후판재
				}else if("DBPU06".equals(ydEqpId)){
					toLoc = "S200";//2후판재
				}*/
				hdsIniDate = slabUtils.getDefaultHdsDate();
				
				jrYdMesPiMsg.setField("JMS_TC_CD"         , "YDSSJ001"                ); //슬라브 이송실적
				jrYdMesPiMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14() ); //JMSTC생성일시
				jrYdMesPiMsg.setField("STL_NO"         	  , stlNoBed                  ); //재료번호
				jrYdMesPiMsg.setField("FROM_LOC"          , "S811"                    ); //상차위치
				jrYdMesPiMsg.setField("TO_LOC"            , toLoc                     ); //하차위치
				jrYdMesPiMsg.setField("PGM_ID"            , "rcvY3YDL012"             ); //실행프로그램
				jrYdMesPiMsg.setField("ERP_HDS_DD"        , hdsIniDate                ); //계상일자
				
				//전송할 전문에 추가
				jrRtn = slabUtils.addSndData(jrRtn,jrYdMesPiMsg);
						
//			}
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "Take-Out완료 되었습니다.");	
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	 
	
	
	
	/**
	 *      [A] 오퍼레이션명 : Take-In완료(Y3YDL013)
	 *      염용선 2020-09-04
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL013(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Take-In완료[PSlabYdL2RcvSeEJB.rcvY3YDL013] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = null;
		
		// 에러처리 필요 변수 추가 -> 에러발생 시 TB_YD_STOCK.TEMP9 (임시항목)에 'Y' 저장한다.
		String sErrYn = "N";												// 에러발생여부
		JDTORecord jrErrYnParam = JDTORecordFactory.getInstance().create();	// 에러발생 시 Parameter 값 저장용
		
		String msgId         = ""; //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
		String ydEqpId       = ""; //야드설비ID
		String ydStkBedNo    = ""; //야드적치Bed번호
		String ydStkBedStlSh = ""; //야드적치Bed재료매수
		String takeInStlNo   = ""; //재료번호(Take-In)
		String sModifier     = ""; //수정자(Backup Only)
		
		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값
			msgId         = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			ydEqpId       = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )); //야드설비ID
			ydStkBedNo    = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"    )); //야드적치Bed번호
			ydStkBedStlSh = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH")); //야드적치Bed재료매수
			takeInStlNo   = slabUtils.trim(rcvMsg.getFieldString("STL_NO"           )); //재료번호(Take-In)
			sModifier     = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"       )); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			jrRtn  = slabUtils.getParam(logId, methodNm, sModifier);
			
			slabUtils.printLog(logId, methodNm, "▶ 수신 항목 필수 값 Check.");
			slabUtils.printLog(logId, methodNm, "▷ ydEqpId(야드설비ID)				: " + ydEqpId		);
			slabUtils.printLog(logId, methodNm, "▷ ydStkBedNo(야드적치Bed번호)		: " + ydStkBedNo	);
			slabUtils.printLog(logId, methodNm, "▷  ydStkBedStlSh(야드적치Bed재료매수)	: " + ydStkBedStlSh	);
			slabUtils.printLog(logId, methodNm, "▷  takeInStlNo(Take-In 재료번호)	: " + takeInStlNo	);
			
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				sErrYn = "Y";	// 에러발생여부
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			} else if ("".equals(ydStkBedNo)) {
				sErrYn = "Y";	// 에러발생여부
				throw new Exception("적치Bed번호(YD_STK_BED_NO) 없음");
			} else if ("".equals(ydStkBedStlSh)) {
				sErrYn = "Y";	// 에러발생여부
				throw new Exception("적치Bed재료매수(YD_STK_BED_STL_SH) 없음");
			} else if ("".equals(takeInStlNo)) {
				sErrYn = "Y";	// 에러발생여부
				throw new Exception("Take-In재료번호(STL_NO) 없음");
			}
			slabUtils.printLog(logId, "야드적치Bed재료매수 - ydStkBedStlSh : "+ydStkBedStlSh, "S+");
			String ydStkLyrNo   = "00" + (Integer.parseInt(ydStkBedStlSh) + 1); //Take-In 야드적치단번호
			String slabGp       = "";	//Slab구분(S:Slab, M:주편)
			String ydStrLoc     = "";	//야드저장위치(공통 수정용)
			String carryInReqYn = "N";	//Carry-In요구구분
			
            //////////////////////////////////////////
		      String APPLY_YN41   = commDao.PSlabApplyYn("APPLY_YN41");
			//////////////////////////////////////////
			/**********************************************************
			* 2. Take-In 재료번호 Check
			**********************************************************/
			//조회 및 등록용
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
			
			jrParam.setField("YD_STK_COL_GP", ydEqpId   ); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO", ydStkBedNo); //야드적치Bed번호
			jrParam.setField("YD_STK_LYR_NO", ydStkLyrNo); //야드적치단번호
			slabUtils.printLog(logId, "※RULE정보:"+ ydEqpId +"(확인)", "SL");
		
			/*
			 *  Take-In완료 Bed정보 조회
				SELECT SL.YD_STK_COL_GP
				      ,SL.YD_STK_BED_NO
				      ,SL.YD_STK_LYR_NO
				      ,SL.STL_NO
				      ,(SELECT SC.SLAB_GP
				          FROM VW_YD_SLABCOMM SC
				         WHERE SC.SLAB_NO = SL.STL_NO) AS SLAB_GP
				      ,(SELECT BR.YD_STR_LOC
				          FROM VW_YD_YDB033 BR  --Slab보급설비기준
				         WHERE BR.YD_STK_COL_GP = SL.YD_STK_COL_GP) AS YD_STR_LOC --야드저장위치(공통 수정용)
				      ,NVL(SR.TI_SUP_YN    ,'Y') AS TI_SUP_YN     --NULL이면 스카핑/2차절단이므로 자동보급
				      ,NVL(SR.TI_PRE_SUP_YN,'N') AS TI_PRE_SUP_YN --NULL이면 선보급 안함
				  FROM TB_YD_STKLYR SL
				      ,(SELECT REPR_CD_GP                           AS YD_STK_COL_GP
				              ,NVL(MIN(DECODE(ITEM,'1',ITEM1)),'N') AS TI_SUP_YN     --TakeIn공Bed보급요구여부
				              ,NVL(MIN(DECODE(ITEM,'2',ITEM1)),'N') AS TI_PRE_SUP_YN --TakeInBed1매선보급요구여부
				          FROM TB_YD_RULE
				         WHERE REPR_CD_GP = :V_YD_STK_COL_GP
				           AND CD_GP = '*'
				         GROUP BY REPR_CD_GP) SR
				 WHERE SL.YD_STK_COL_GP = SR.YD_STK_COL_GP(+)
				   AND SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
				   AND SL.YD_STK_LYR_NO = :V_YD_STK_LYR_NO

			 */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getC3YDL005Bed", logId, methodNm, "Bed정보 조회");
    		
			if (jsChk != null && jsChk.size() > 0) {
				JDTORecord jrChk = jsChk.getRecord(0);
				
				String stlNo = slabUtils.trim(jrChk.getFieldString("STL_NO")); //적치Bed 재료번호
				if(!"DART01".equals(ydEqpId)){
					if (!takeInStlNo.equals(stlNo)) {
						sErrYn = "Y";	// 에러발생여부
						throw new Exception("Take-In 재료번호[" + takeInStlNo + "]와 적치Bed 야드적치단번호(보급슬라브갯수 +1)[" + ydStkLyrNo + "]가 서로 다릅니다.");
					}
				}
				slabGp   = slabUtils.trim(jrChk.getFieldString("SLAB_GP"   )); //Slab구분(공통 수정용)
				ydStrLoc = slabUtils.trim(jrChk.getFieldString("YD_STR_LOC")); //야드저장위치(공통 수정용)

				//Carry-In요구 결정
				String tiSupYn    = slabUtils.trim(jrChk.getFieldString("TI_SUP_YN"    )); //TakeIn공Bed보급요구여부
				String tiPreSupYn = slabUtils.trim(jrChk.getFieldString("TI_PRE_SUP_YN")); //TakeInBed1매선보급요구여부
                
				//#1 Scafer에서 Hand스카핑장으로 Take-In 되는 경우는 Carry-In요구를 하지 않는다.
				if ("Y".equals(tiSupYn) ) {
					//공Bed 이거나 Bed에 1매 있고 선보급요구여부가 "Y"이면
                    if("Y".equals(APPLY_YN41)){		         
					   //if("DAPU01".equals(ydEqpId) || "DAPU03".equals(ydEqpId)) {//1후판 장입베드 비어있는 베드 우선 공급으로 적용
						                                                         // YYS 2022-01-21
						   carryInReqYn = "Y"; //Carry-In요구구분		
						
					}else{
						if ("001".equals(ydStkLyrNo) || ("002".equals(ydStkLyrNo) && "Y".equals(tiPreSupYn))) { 
							carryInReqYn = "Y"; //Carry-In요구구분
						}
					}
					
					
				}
			} else {
				sErrYn = "Y";	// 에러발생여부
				throw new Exception("적치단[" + ydEqpId + "-" + ydStkBedNo + "-" + ydStkLyrNo + "] 정보가 없습니다.");
			}
			slabUtils.printLog(logId, "설비 : " + ydEqpId, "SL");	
			/**********************************************************
			* 2. 적치Bed 재료번호, 재료상태  Update
			**********************************************************/
			jrParam.setField("STL_NO"             , ""      ); //재료번호
			jrParam.setField("YD_STK_LYR_MTL_STAT", "E"     ); //야드적치단재료상태(적치가능)

			//적치Bed Table Update
			/*
			 *적치단 재료번호 수정 
				UPDATE TB_YD_STKLYR
				   SET MODIFIER            = :V_MODIFIER
				      ,MOD_DDTT            = SYSDATE
				      ,STL_NO              = :V_STL_NO
				      ,YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
				 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
				   AND YD_STK_BED_NO       = :V_YD_STK_BED_NO
				   AND YD_STK_LYR_NO       = :V_YD_STK_LYR_NO
				   AND DEL_YN              = 'N'
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updSlabYdStkLyrStlNo", logId, methodNm, "적치단(TB_YD_STKLYR) 재료번호 수정");				
			
						
			
			/**********************************************************
			* 3. 송신 전문 조회
			* 3.1 생산통제 장입진행실적(Take-In) : (후판:YDCTJ031)
			* 3.2 Slab야드 야드저장위치제원 :  YDY3L001
			* 3.3 보급요구처리 : Slab보급설비기준(YDB033)에 정의 됨
			**********************************************************/
			//생산통제 장입진행실적(Take-In) (YDCTJ031)
			jrParam.setField("STL_NO", takeInStlNo); //재료번호
			jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL3("YDCTJ031TI", jrParam));
			
			//Slab야드 저장위치제원( YDY3L001)
			jrParam.setField("YD_INFO_SYNC_CD", "4"); //야드정보동기화코드(Bed)
			jrParam.setField("YD_EQP_WRK_STAT_GUBUN" , "");
			jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L001", jrParam));
			
            
			if("Y".equals(APPLY_YN41)){	
			 JDTORecord jrYdBedMsg = slabUtils.getParam(logId, methodNm, sModifier);
                // 항상 빈 베드 체크 - 1레이어만 확인
			    //2023.01.13 ydEqpId.substring(0,4) -> ydEqpId 로 수정. 1픽업 자동 3픽업 수동인데, 3픽업에 자동보급되는현상 수정
			    jrYdBedMsg.setField("YD_STK_COL_GP", ydEqpId);
			    slabUtils.printLog(logId, "-----> 설비보급요구처리 (YD_STK_COL_GP) : "+ydEqpId, "SL");
			    /*
			     * SELECT * FROM   TB_YD_STKLYR
			          WHERE 1=1
			           AND YD_STK_COL_GP IN ('DAPU01','DAPU03','DBPU05')
			           AND YD_STK_COL_GP LIKE  :V_YD_STK_COL_GP || '%'
			           AND STL_NO IS NULL
			           AND YD_STK_LYR_MTL_STAT = 'E'
                       AND DEL_YN = 'N'
			           AND YD_STK_LYR_NO = '001'
			         ORDER BY YD_STK_COL_GP DESC ,YD_STK_BED_NO DESC
			     */
			     jsChk = commDao.select(jrYdBedMsg, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getBed", logId, methodNm, "Bed정보 조회");
	    		
				if (jsChk != null && jsChk.size() > 0) {
					JDTORecord jrChk = jsChk.getRecord(0);
					
					ydEqpId = slabUtils.trim(jrChk.getFieldString("YD_STK_COL_GP")); //적치Bed 
					ydStkBedNo = slabUtils.trim(jrChk.getFieldString("YD_STK_BED_NO")); //적치Bed
				}else{
					carryInReqYn = "N";
				}
			}
			
			//Carry-In요구구분이 'Y'이면
			slabUtils.printLog(logId, "-----> Carry-In요구구분 : " + carryInReqYn, "SL");
			if ("Y".equals(carryInReqYn)) {
				JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, sModifier);
				    
				//설비보급요구 전문
				slabUtils.printLog(logId, "-----> 설비보급요구 처리 (YDYDJ421) ", "SL");
				/*
				 * YDYDJ420 >> YDYDJ421 내부전문 코드 변경
				 * 염용선 2020-11-10
				 */
				jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ421"               ); // YDYDJ420 JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
				jrYdMsg.setField("YD_STK_BED_NO"     , ydStkBedNo               ); //야드적치Bed번호
				jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); //야드스케쥴기동구분(Auto)
				jrYdMsg.setField("MODIFIER"          , sModifier                ); //수정자
				
				//전송할 전문에 추가
				jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);
			}
			slabUtils.printLog(logId, "-----> 설비보급요구 처리중 (YDYDJ421) " , "SL");

			/**********************************************************
			* 4. 공통Table 야드저장위치  Update
			**********************************************************/
			//야드저장위치 값이 있으면
			if (!"".equals(ydStrLoc)) {
				jrParam.setField("YD_STR_LOC", ydStrLoc); //야드저장위치
				if ("M".equals(slabGp)) {
					//주편공통
					commDao.updateTx(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updC3YDL005MslabComm", logId, methodNm, "주편공통(TB_PT_MSLABCOMM) 저장위치 수정");				
					
				} else {
					
					//Slab 공통
					/*
					 * --Take-In 완료 Slab 공통 저장위치 수정
						MERGE INTO TB_PT_SLABCOMM SC USING (
						SELECT STL_NO
						      ,SUBSTR(YD_STR_LOC,1,1)      AS YD_GP
						      ,SUBSTR(YD_STR_LOC,2,1)      AS YD_BAY_GP
						      ,SUBSTR(YD_STR_LOC,3,2)      AS YD_EQP_GP
						      ,SUBSTR(YD_STR_LOC,5,2)      AS YD_STK_COL_NO
						      ,SUBSTR(YD_STR_LOC,7,2)      AS YD_STK_BED_NO
						      ,'0'||SUBSTR(YD_STR_LOC,9,2) AS YD_STK_LYR_NO
						      ,YD_STR_LOC
						  FROM (SELECT :V_STL_NO     AS STL_NO
						              ,:V_YD_STR_LOC AS YD_STR_LOC
						          FROM DUAL)
						) DD ON (SC.SLAB_NO = DD.STL_NO AND SC.RECORD_PROG_STAT = '2')
						WHEN MATCHED THEN UPDATE SET
						     SC.MODIFIER         = :V_MODIFIER
						    ,SC.MOD_DDTT         = SYSDATE
						    ,SC.FNL_REG_PGM      = 'rcvY3YDL013'
						    ,SC.YD_GP            = DD.YD_GP
						    ,SC.YD_BAY_GP        = DD.YD_BAY_GP
						    ,SC.YD_EQP_GP        = DD.YD_EQP_GP
						    ,SC.YD_STK_COL_NO    = DD.YD_STK_COL_NO
						    ,SC.YD_STK_BED_NO    = DD.YD_STK_BED_NO
						    ,SC.YD_STK_LYR_NO    = DD.YD_STK_LYR_NO
						    ,SC.YD_STR_LOC       = DD.YD_STR_LOC
						    ,SC.YD_STR_LOC_HIS1  = SC.YD_STR_LOC
						    ,SC.YD_STR_LOC_HIS2  = SC.YD_STR_LOC_HIS1

					 */
					commDao.updateTx(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updC3YDL005SlabComm", logId, methodNm, "Slab공통(TB_PT_SLABCOMM) 저장위치 수정");				
					
				}
			}
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "Take-In완료 되었습니다.");	
			slabUtils.printLog(logId, methodNm, ">>>>> 리턴 전!");
			return jrRtn;
		} catch (DAOException e) {
			sErrYn = "Y";	// 에러발생여부
			throw e;
		} catch (Exception e) {
			sErrYn = "Y";	// 에러발생여부
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		} finally {
			try {
				
				String sDYD500_YN_01 = slabComm.ApplyYn("DYD500", "D", "1"); // Take In 처리(YDY3L001) 후 에러 발생 유무 저장 적용여부
				slabUtils.printLog(logId, methodNm, "▷ Take In 처리(YDY3L001) 후 에러 발생 유무 저장 적용여부 : " + sDYD500_YN_01);
				
				if("Y".equals(sDYD500_YN_01)){	// Take In 처리(YDY3L001) 후 에러 발생 유무 저장 적용여부
					slabUtils.printLog(logId, methodNm, "▶ Take In 처리 후 에러 발생 유무를 체크합니다.");
					slabUtils.printLog(logId, methodNm, "▷ 에러발생유무 : "+ sErrYn);
					
					// 에러 발생 시 TB_YD_STOCK.TEMP9(임시) 항목에 'Y'로 SET한다.
					if("Y".equals(sErrYn)) {	// 에러 발생 시 처리
						
						jrErrYnParam.setField("ERR_YN"		, sErrYn  		); 	// 에러여부
						jrErrYnParam.setField("MODIFIER"	, sModifier		); 	// 수정자(인터페이스Id)
						jrErrYnParam.setField("STL_NO"		, takeInStlNo 	);  // Take-In 재료번호
						
						slabUtils.printParam(logId, jrErrYnParam);
						/*
						 -- com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updStockErrYn 
						 -- Take-In완료(장입) 처리 시 에러가 발생하면 TEMP9(임시) 항목에 'Y'로 SET한다. 
							UPDATE USRYDA.TB_YD_STOCK STK -- YD_저장품
							   SET STK.TEMP9    = :V_ERR_YN   -- 에러여부
							     , STK.MODIFIER = :V_MODIFIER -- 수정자
							     , STK.MOD_DDTT = SYSDATE
							 WHERE STK.STL_NO   = :V_STL_NO
						*/
						// 별도 트랜젝션으로 UPDATE 처리
						commDao.updateTx(jrErrYnParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updStockErrYn", logId, methodNm, "저장품(TB_YD_STOCK.TEMP9) >> Take-In완료(장입) 처리 시 에러 발생유무 UPDATE.");
						slabUtils.printLog(logId, methodNm, "S-");
					
					} else{
						slabUtils.printLog(logId, methodNm, "▶ 발생된 에러가 없습니다.");
					}
				}else{
					// pass.
					slabUtils.printLog(logId, methodNm, "▶ Take In 처리(YDY3L001) 후 에러 발생 유무 저장 적용대상이 아닙니다.");
				}
				
			} catch (Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}			
		}
	}


	


	/**
	 *      [A] 오퍼레이션명 : 대차이동실적(Y3YDL014)
	 *      염용선 2020-08-25
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL014(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차이동실적-도착[PSlabYdL2RcvSeEJB.rcvY3YDL014] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			//Return Value
			JDTORecord jrRtn  = slabUtils.getParam(logId, methodNm, "");
			//수신 항목 값
			String msgId        = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId      = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"      )); //야드설비ID
			String ydTcarMoveGp = slabUtils.trim(rcvMsg.getFieldString("YD_TCAR_MOVE_GP")); //야드대차이동구분
			String ydBayGp1     = slabUtils.trim(rcvMsg.getFieldString("YD_BAY_GP1"     )); //야드동구분1
			String ydUserId     = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			if ("".equals(ydUserId)) { ydUserId = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			slabUtils.printLog(logId, "YD_EQP_ID(야드설비ID):"+ydEqpId+">>>YD_TCAR_MOVE_GP(야드대차이동구분):"+ ydTcarMoveGp+">>>YD_BAY_GP1(야드동구분1):"+ ydBayGp1 , "SL");
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			} else if ("".equals(ydTcarMoveGp)) {
				throw new Exception("대차이동구분(YD_TCAR_MOVE_GP) 없음");
			} else if ("".equals(ydBayGp1)) {
				throw new Exception("현재동(YD_BAY_GP1) 없음");
			}

			if (!"S".equals(ydTcarMoveGp) && !"E".equals(ydTcarMoveGp)) {
				slabUtils.printLog(logId, "대차이동구분[" + ydTcarMoveGp + "]이 'S' 또는 'E'가 아니므로 종료", "SL");
				return null;
			}

			String ydStkColGp = ydEqpId.substring(0, 1) + ydBayGp1 + ydEqpId.substring(2); //야드적치열구분(현재동)
			slabUtils.printLog(logId, "YD_STK_COL_GP(야드적치열구분(현재동)):"+ydStkColGp , "SL");
			
			/**********************************************************
			* 2. 설비 야드현재동구분, 대차스케줄 야드차량진행상태 수정
			**********************************************************/
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm,ydUserId);
			
			jrParam.setField("YD_EQP_ID"      , ydEqpId     ); //야드설비ID
			jrParam.setField("YD_TCAR_MOVE_GP", ydTcarMoveGp); //야드대차이동구분   S:출발, M:이동중, E:도착
			jrParam.setField("YD_STK_COL_GP"  , ydStkColGp  ); //야드적치열구분(현재동)
			
			slabUtils.printLog(logId, "YD_STK_COL_GP : "+ydStkColGp, "SL>>>>>>>>>>>>>>>>>>");
			slabUtils.printLog(logId, "YD_EQP_ID : "+ydEqpId, "SL>>>>>>>>>>>>>>>>>");
			slabUtils.printLog(logId, "야드대차이동구분[ydTcarMoveGp] : "+ydTcarMoveGp, "SL>>>>>>>>>>>>>>>>>>");
			
			//야드현재동구분
			if ("S".equals(ydTcarMoveGp)) {
				jrParam.setField("YD_CURR_BAY_GP", "");
			} else {
				jrParam.setField("YD_CURR_BAY_GP", ydBayGp1);
			}
			
			slabUtils.printLog(logId, "야드동구분1[ydBayGp1] : "+ydBayGp1, "SL>>>>>>>>>>>>>>>>>>");
			
			
			//설비 Table 야드현재동구분 수정
			/*
			 * --설비 현재동 수정 
				UPDATE TB_YD_EQP
				   SET MODIFIER       = :V_MODIFIER
				      ,MOD_DDTT       = SYSDATE
				      ,YD_CURR_BAY_GP = :V_YD_CURR_BAY_GP
				 WHERE YD_EQP_ID      = :V_YD_EQP_ID

			 */
			
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updSlabYdEqpCurrBay", logId, methodNm, "설비(TB_YD_EQP) 야드현재동구분 수정");
			
			
			//대차스케줄 야드차량진행상태 수정
			/*
			 * --대차이동실적 대차스케줄 수정
				MERGE INTO TB_YD_TCARSCH TS USING (
				SELECT YD_TCAR_SCH_ID
				      ,CASE WHEN :V_YD_TCAR_MOVE_GP = 'S' THEN              --야드대차이동구분(S:출발,E:도착)
				            CASE WHEN YD_EQP_WRK_STAT = 'U' THEN '1'        --공대차(상차)출발
				            ELSE 'A' END                                    --영대차(하차)출발
				       ELSE CASE WHEN YD_EQP_WRK_STAT = 'L' THEN 'B'        --영대차(하차)도착
				                 WHEN YD_CARLD_WRK_BOOK_ID IS NULL THEN '0' --공대차도착(상차대기)
				            ELSE '2' END                                    --공대차(상차)도착
				       END AS YD_CAR_PROG_STAT                              --야드차량진행상태
				      ,:V_YD_STK_COL_GP AS YD_STK_COL_GP                    --실적발생위치
				  FROM TB_YD_TCARSCH
				 WHERE YD_EQP_ID = :V_YD_EQP_ID
				   AND DEL_YN    = 'N'
				) DD ON (TS.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID)
				WHEN MATCHED THEN UPDATE SET
					 TS.MODIFIER         = :V_MODIFIER
				    ,TS.MOD_DDTT         = SYSDATE
				    ,TS.YD_CAR_PROG_STAT = DD.YD_CAR_PROG_STAT --야드차량진행상태
				    ,TS.YD_CARLD_LEV_LOC  = DECODE(DD.YD_CAR_PROG_STAT,'1',DD.YD_STK_COL_GP,TS.YD_CARLD_LEV_LOC ) --야드상차출발위치
				    ,TS.YD_CARLD_LEV_DT   = DECODE(DD.YD_CAR_PROG_STAT,'1',SYSDATE         ,TS.YD_CARLD_LEV_DT  ) --야드상차출발일시
				    ,TS.YD_CARLD_STOP_LOC = DECODE(DD.YD_CAR_PROG_STAT,'2',DD.YD_STK_COL_GP,'0',DD.YD_STK_COL_GP,TS.YD_CARLD_STOP_LOC) --야드상차정지위치
				    ,TS.YD_CARLD_ARR_DT   = DECODE(DD.YD_CAR_PROG_STAT,'2',SYSDATE         ,'0',SYSDATE         ,TS.YD_CARLD_ARR_DT  ) --야드상차도착일시
				    ,TS.YD_CARUD_LEV_DT   = DECODE(DD.YD_CAR_PROG_STAT,'A',SYSDATE         ,TS.YD_CARUD_LEV_DT  ) --야드하차출발일시
				    ,TS.YD_CARUD_STOP_LOC = DECODE(DD.YD_CAR_PROG_STAT,'B',DD.YD_STK_COL_GP,TS.YD_CARUD_STOP_LOC) --야드하차정지위치
				    ,TS.YD_CARUD_ARR_DT   = DECODE(DD.YD_CAR_PROG_STAT,'B',SYSDATE         ,TS.YD_CARUD_ARR_DT  ) --야드하차도착일시

			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updC3YDL007TcarSch", logId, methodNm, "대차스케줄(TB_YD_STKLYR) 차량진행상태 수정");
			
			/**********************************************************
			* 3. 대차스케줄 조회
			**********************************************************/
			/*
			 * --대차이동실적 대차스케줄 조회 
				SELECT TS.YD_TCAR_SCH_ID
				      ,TS.YD_CAR_PROG_STAT
				      ,TS.YD_CARLD_WRK_BOOK_ID
				      ,TS.YD_CARUD_WRK_BOOK_ID
				      ,TS.YD_CARLD_STOP_LOC
				      ,(SELECT SB.YD_STK_BED_ACT_STAT
				          FROM TB_YD_STKBED SB
				         WHERE SB.YD_STK_COL_GP = TS.YD_CARLD_STOP_LOC
				           AND SB.YD_STK_BED_NO = '01') AS YD_STK_BED_ACT_STAT_LD
				      ,TS.YD_CARUD_STOP_LOC
				      ,(SELECT SB.YD_STK_BED_ACT_STAT
				          FROM TB_YD_STKBED SB
				         WHERE SB.YD_STK_COL_GP = TS.YD_CARUD_STOP_LOC
				           AND SB.YD_STK_BED_NO = '01') AS YD_STK_BED_ACT_STAT_UD
				      ,CASE WHEN TS.CRN_SCH_YN = 'Y' OR TS.YD_WBOOK_ID IS NULL THEN TS.CRN_SCH_YN
				      	    ELSE (SELECT DECODE(COUNT(*),0,'Y','N') --도착 시 크레인스케줄이 없으면 기동
				      	    	    FROM TB_YD_CRNSCH CS
				      	    	   WHERE CS.YD_WBOOK_ID = TS.YD_WBOOK_ID
				      	    	     AND CS.DEL_YN = 'N')
				       END CRN_SCH_YN
				  FROM (SELECT TS.YD_TCAR_SCH_ID
				              ,TS.YD_CAR_PROG_STAT
				              ,TS.YD_CARLD_WRK_BOOK_ID
				              ,TS.YD_CARUD_WRK_BOOK_ID
				              ,TS.YD_CARLD_STOP_LOC
				              ,TS.YD_CARUD_STOP_LOC
				              ,NVL((SELECT BR.DTL_ITEM1 FROM TB_YD_RULE BR
									   WHERE 1=1
									     AND BR.REPR_CD_GP = 'DYD400'
									     AND BR.CD_GP      = 'D'
									     AND BR.ITEM_VALUE1= TS.YD_EQP_ID
									     AND BR.ITEM       = TS.YD_CAR_PROG_STAT),'N') AS CRN_SCH_YN 
				              ,DECODE(TS.YD_CAR_PROG_STAT,'2',TS.YD_CARLD_WRK_BOOK_ID
				                                         ,'B',TS.YD_CARUD_WRK_BOOK_ID) AS YD_WBOOK_ID
				          FROM TB_YD_TCARSCH TS
				         WHERE TS.YD_EQP_ID = :V_YD_EQP_ID
				           AND TS.DEL_YN    = 'N') TS

			 */
			JDTORecordSet jsTcar = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvDAO.getC3YDL007TcarSch", logId, methodNm, "대차스케줄조회");
			
			if (jsTcar == null || jsTcar.size() == 0) {
				throw new Exception("대차스케줄[" + ydEqpId + "] 없음");
			}

			JDTORecord jrTcar = jsTcar.getRecord(0);
			String ydTcarSchId       = slabUtils.trim(jrTcar.getFieldString("YD_TCAR_SCH_ID"        )); //야드대차스케쥴ID
			String ydCarProgStat     = slabUtils.trim(jrTcar.getFieldString("YD_CAR_PROG_STAT"      )); //야드차량진행상태
			String ydCarldWrkbookId  = slabUtils.trim(jrTcar.getFieldString("YD_CARLD_WRK_BOOK_ID"  )); //야드상차작업예약ID
			String ydCarudWrkbookId  = slabUtils.trim(jrTcar.getFieldString("YD_CARUD_WRK_BOOK_ID"  )); //야드하차작업예약ID
			String ydCarldStopLoc    = slabUtils.trim(jrTcar.getFieldString("YD_CARLD_STOP_LOC"     )); //야드상차정지위치
			String ydCarudStopLoc    = slabUtils.trim(jrTcar.getFieldString("YD_CARUD_STOP_LOC"     )); //야드하차정지위치
			String ydStkBedActStatLd = slabUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_LD")); //야드적치Bed활성상태(상차정지위치)
			String ydStkBedActStatUd = slabUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_UD")); //야드적치Bed활성상태(하차정지위치)
			//업무기준(DYD400) 야드스케쥴기동구분(Y:기동,N:기동안함) : 출발 또는 도착 중 하나 이상은 반드시 'Y'로 되어 있어야 함
			//대차 출발시 스케줄 실행
			String crnSchYn          = slabUtils.trim(jrTcar.getFieldString("CRN_SCH_YN"            )); //크레인스케줄기동여부
			String ydWbookId         = ""; //작업예약ID
			String ydSchReqGp        = ""; //야드스케쥴요청구분

			slabUtils.printLog(logId, "대차[" + ydEqpId + "] 스케줄 >> 대차스케쥴ID:" + ydTcarSchId + ", 차량진행:" + ydCarProgStat + ", 크레인스케줄:" + crnSchYn
					                + ", 상차작업:" + ydCarldWrkbookId + "-" + ydCarldStopLoc + ", 하차작업:" + ydCarudWrkbookId + "-" + ydCarudStopLoc, "SL");
			
			/**********************************************************
			* 4. 적치Bed, 적치단 Table 상태 수정
			**********************************************************/
			jrParam.setField("YD_STK_BED_NO" , "01"       ); //야드적치Bed번호
			jrParam.setField("YD_TCAR_SCH_ID", ydTcarSchId); //야드대차스케쥴ID
			//jrParam.setField("YD_CRN_SCH_ID", ydTcarSchId); //야드대차스케쥴ID
			if ("A".equals(ydCarProgStat)) {
				ydSchReqGp = "2"; //영대차출발 : 하차출발(A)

				//영대차출발이면 출발위치 적치Bed 비활성화 처리
				jrParam.setField("YD_STK_COL_GP"      , ydStkColGp); //야드적치열구분
				jrParam.setField("YD_STK_BED_ACT_STAT", "C"       ); //야드적치Bed활성상태(비활성화)
				/*
				 * 적치Bed 활성상태 수정 
					UPDATE TB_YD_STKBED
					   SET MODIFIER      = :V_MODIFIER
					      ,MOD_DDTT       = SYSDATE
					      ,YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
					 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
					   AND YD_STK_BED_NO         = :V_YD_STK_BED_NO
					   AND DEL_YN              = 'N'
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatStkBedAct", logId, methodNm, "적치Bed(TB_YD_STKBED) 야드적치Bed활성상태 수정");
				
				//출발위치 적치단 재료 삭제
				/*
				 * --적치단 재료번호 삭제 - com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdStkLyrClr
					UPDATE TB_YD_STKLYR
					   SET MODIFIER            = :V_MODIFIER
					      ,MOD_DDTT            = SYSDATE
					      ,STL_NO              = NULL
					      ,YD_STK_LYR_ACT_STAT = 'E'
					      ,YD_STK_LYR_MTL_STAT = 'E'
					 WHERE YD_STK_COL_GP    LIKE :V_YD_STK_COL_GP
					   AND YD_STK_BED_NO    LIKE :V_YD_STK_BED_NO||'%'
					   AND DEL_YN              = 'N'

				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updSlabYdStkLyrClr", logId, methodNm, "적치단(TB_YD_STKLYR) 재료번호 삭제");
				
				//크레인스케줄기동여부 'Y'이면
				if ("Y".equals(crnSchYn)) {
					ydWbookId = ydCarudWrkbookId; //야드하차작업예약ID

					//하차위치 적치Bed 활성화 처리
					jrParam.setField("YD_STK_COL_GP"      , ydCarudStopLoc); //적치Bed 야드적치열구분(하차정지위치)
					jrParam.setField("YD_STK_BED_ACT_STAT", "L"           ); //적치Bed 야드적치Bed활성상태(활성)
					/*
					 * 적치Bed 활성상태 수정 
						UPDATE TB_YD_STKBED
						   SET MODIFIER      = :V_MODIFIER
						      ,MOD_DDTT       = SYSDATE
						      ,YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
						 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
						   AND YD_STK_BED_NO         = :V_YD_STK_BED_NO
						   AND DEL_YN              = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatStkBedAct", logId, methodNm, "적치Bed(TB_YD_STKBED) 야드적치Bed활성상태 수정");
					
					//하차위치 적치단 재료번호 등록
					/*
					 * --대차이동실적 적치단 재료번호 등록 
						MERGE INTO TB_YD_STKLYR SL USING (
						SELECT SL.YD_STK_COL_GP
						      ,SL.YD_STK_BED_NO
						      ,SL.YD_STK_LYR_NO
						      ,'E'                            AS YD_STK_LYR_ACT_STAT --적치가능
						      ,DECODE(TM.STL_NO,NULL,'E','C') AS YD_STK_LYR_MTL_STAT --적치가능,적치중
						      ,TM.STL_NO
						  FROM TB_YD_STKLYR      SL
						      ,TB_YD_TCARFTMVMTL TM
						 WHERE SL.YD_STK_COL_GP  = :V_YD_STK_COL_GP
						   AND SL.YD_STK_BED_NO  = :V_YD_STK_BED_NO
						   AND SL.YD_STK_LYR_NO  = TM.YD_STK_LYR_NO(+)
						   AND :V_YD_TCAR_SCH_ID = TM.YD_TCAR_SCH_ID(+)
						   AND 'N'               = TM.DEL_YN(+)
						   AND NOT EXISTS(SELECT 1 FROM USRYDA.TB_YD_CRNWRKMTL CR WHERE CR.STL_NO=TM.STL_NO AND CR.DEL_YN='N') 
						) DD ON (SL.YD_STK_COL_GP = DD.YD_STK_COL_GP AND SL.YD_STK_BED_NO = DD.YD_STK_BED_NO AND SL.YD_STK_LYR_NO = DD.YD_STK_LYR_NO)
						WHEN MATCHED THEN UPDATE SET
							 SL.MODIFIER            = :V_MODIFIER
						    ,SL.MOD_DDTT            = SYSDATE
						    ,SL.YD_STK_LYR_ACT_STAT = DD.YD_STK_LYR_ACT_STAT
						    ,SL.YD_STK_LYR_MTL_STAT = DD.YD_STK_LYR_MTL_STAT
						    ,SL.STL_NO              = DD.STL_NO
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updC3YDL007StkLyrStl", logId, methodNm, "적치단(TB_YD_STKLYR) 재료번호 등록");
					
					//설비 Table 야드현재동구분 미리 수정
					jrParam.setField("YD_CURR_BAY_GP", ydCarudStopLoc.substring(1, 2));
					/*
					 * --설비 현재동 수정 
						UPDATE TB_YD_EQP
						   SET MODIFIER       = :V_MODIFIER
						      ,MOD_DDTT       = SYSDATE
						      ,YD_CURR_BAY_GP = :V_YD_CURR_BAY_GP
						 WHERE YD_EQP_ID      = :V_YD_EQP_ID

					 */
					
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updSlabYdEqpCurrBay", logId, methodNm, "설비(TB_YD_EQP) 야드현재동구분 수정");
					
					
					
				}
				
				
				
			} else if ("B".equals(ydCarProgStat)) {
				ydSchReqGp = "3"; //영대차도착 : 하차도착(B)

				//크레인스케줄기동여부 'Y'이면
				if ("Y".equals(crnSchYn)) {
					ydWbookId = ydCarudWrkbookId; //야드하차작업예약ID
				}

				//영대차도착이고 적치Bed 비활성화 이면 활성화
				jrParam.setField("YD_STK_COL_GP", ydStkColGp); //적치Bed 야드적치열구분(현위치)

				if (!"L".equals(ydStkBedActStatUd)) {
					//현위치 적치Bed 활성화 처리
					jrParam.setField("YD_STK_BED_ACT_STAT", "L"); //적치Bed 야드적치Bed활성상태(활성)
					/*
					 * 적치Bed 활성상태 수정 
						UPDATE TB_YD_STKBED
						   SET MODIFIER      = :V_MODIFIER
						      ,MOD_DDTT       = SYSDATE
						      ,YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
						 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
						   AND YD_STK_BED_NO         = :V_YD_STK_BED_NO
						   AND DEL_YN              = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatStkBedAct", logId, methodNm, "적치Bed(TB_YD_STKBED) 야드적치Bed활성상태 수정");
					
				}

				//하차위치 적치단 재료번호 등록 -> 혹시 정보가 맞지 않을 수도 있으므로 무조건 Update
				/*
				 * --대차이동실적 적치단 재료번호 등록 
					MERGE INTO TB_YD_STKLYR SL USING (
					SELECT SL.YD_STK_COL_GP
					      ,SL.YD_STK_BED_NO
					      ,SL.YD_STK_LYR_NO
					      ,'E'                            AS YD_STK_LYR_ACT_STAT --적치가능
					      ,DECODE(TM.STL_NO,NULL,'E','C') AS YD_STK_LYR_MTL_STAT --적치가능,적치중
					      ,TM.STL_NO
					  FROM TB_YD_STKLYR      SL
					      ,TB_YD_TCARFTMVMTL TM
					 WHERE SL.YD_STK_COL_GP  = :V_YD_STK_COL_GP
					   AND SL.YD_STK_BED_NO  = :V_YD_STK_BED_NO
					   AND SL.YD_STK_LYR_NO  = TM.YD_STK_LYR_NO(+)
					   AND :V_YD_TCAR_SCH_ID = TM.YD_TCAR_SCH_ID(+)
					   AND 'N'               = TM.DEL_YN(+)
					   AND NOT EXISTS(SELECT 1 FROM USRYDA.TB_YD_CRNWRKMTL CR WHERE CR.STL_NO=TM.STL_NO AND CR.DEL_YN='N') 
					) DD ON (SL.YD_STK_COL_GP = DD.YD_STK_COL_GP AND SL.YD_STK_BED_NO = DD.YD_STK_BED_NO AND SL.YD_STK_LYR_NO = DD.YD_STK_LYR_NO)
					WHEN MATCHED THEN UPDATE SET
						 SL.MODIFIER            = :V_MODIFIER
					    ,SL.MOD_DDTT            = SYSDATE
					    ,SL.YD_STK_LYR_ACT_STAT = DD.YD_STK_LYR_ACT_STAT
					    ,SL.YD_STK_LYR_MTL_STAT = DD.YD_STK_LYR_MTL_STAT
					    ,SL.STL_NO              = DD.STL_NO
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updC3YDL007StkLyrStl", logId, methodNm, "적치단(TB_YD_STKLYR) 재료번호 등록");
				
				
			} else if ("1".equals(ydCarProgStat)) {
				ydSchReqGp = "5"; //공대차출발 : 상차출발(1)

				//공대차출발이면 출발위치 적치Bed 비활성화 처리
				jrParam.setField("YD_STK_COL_GP"      , ydStkColGp); //야드적치열구분
				jrParam.setField("YD_STK_BED_ACT_STAT", "C"       ); //야드적치Bed활성상태(비활성화)
				/*
				 * 적치Bed 활성상태 수정 
					UPDATE TB_YD_STKBED
					   SET MODIFIER      = :V_MODIFIER
					      ,MOD_DDTT       = SYSDATE
					      ,YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
					 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
					   AND YD_STK_BED_NO         = :V_YD_STK_BED_NO
					   AND DEL_YN              = 'N'
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatStkBedAct", logId, methodNm, "적치Bed(TB_YD_STKBED) 야드적치Bed활성상태 수정");
				

				//크레인스케줄기동여부 'Y'이면
				if ("Y".equals(crnSchYn)) {
					ydWbookId = ydCarldWrkbookId; //야드상차작업예약ID

					//상차위치 적치Bed 활성화 처리
					jrParam.setField("YD_STK_COL_GP"      , ydCarldStopLoc); //야드적치열구분(상차정지위치)
					jrParam.setField("YD_STK_BED_ACT_STAT", "L"           ); //적치Bed 야드적치Bed활성상태(활성)
					/*
					 * 적치Bed 활성상태 수정 
						UPDATE TB_YD_STKBED
						   SET MODIFIER      = :V_MODIFIER
						      ,MOD_DDTT       = SYSDATE
						      ,YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
						 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
						   AND YD_STK_BED_NO         = :V_YD_STK_BED_NO
						   AND DEL_YN              = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatStkBedAct", logId, methodNm, "적치Bed(TB_YD_STKBED) 야드적치Bed활성상태 수정");
					
				
					//설비 Table 야드현재동구분 미리 수정
					jrParam.setField("YD_CURR_BAY_GP", ydCarldStopLoc.substring(1, 2));
					/*
					 * --설비 현재동 수정 
						UPDATE TB_YD_EQP
						   SET MODIFIER       = :V_MODIFIER
						      ,MOD_DDTT       = SYSDATE
						      ,YD_CURR_BAY_GP = :V_YD_CURR_BAY_GP
						 WHERE YD_EQP_ID      = :V_YD_EQP_ID

					 */
					
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updSlabYdEqpCurrBay", logId, methodNm, "설비(TB_YD_EQP) 야드현재동구분 수정");
					
				}
			} else {
				ydSchReqGp = "6"; //공대차도착 : 상차도착(2) or 상차대기(0)

				//크레인스케줄기동여부 'Y'이면
				if ("Y".equals(crnSchYn)) {
					ydWbookId = ydCarldWrkbookId; //야드상차작업예약ID
				}

				//공대차도착이고 적치Bed 비활성화 이면 활성화
				if (!"L".equals(ydStkBedActStatLd)) {
					//현위치 적치Bed 활성화 처리
					jrParam.setField("YD_STK_COL_GP"      , ydStkColGp); //야드적치열구분(현위치)
					jrParam.setField("YD_STK_BED_ACT_STAT", "L"       ); //야드적치Bed활성상태(활성)
					/*
					 * 적치Bed 활성상태 수정 
						UPDATE TB_YD_STKBED
						   SET MODIFIER      = :V_MODIFIER
						      ,MOD_DDTT       = SYSDATE
						      ,YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
						 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
						   AND YD_STK_BED_NO         = :V_YD_STK_BED_NO
						   AND DEL_YN              = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatStkBedAct", logId, methodNm, "적치Bed(TB_YD_STKBED) 야드적치Bed활성상태 수정");
					
					
				}
			}

			
			/**********************************************************
			* 5. 야드저장위치제원(YDY3L001) 전문 조회
			**********************************************************/
			jrParam.setField("YD_INFO_SYNC_CD", "4"       ); //야드정보동기화코드(Bed)
			jrParam.setField("YD_STK_COL_GP"  , ydStkColGp); //야드적치열구분(현재동)
			jrParam.setField("YD_EQP_WRK_STAT_GUBUN" , "");
			//전송Data 조회
			jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY3L001", jrParam));

			/**********************************************************
			* 6. 크레인스케줄(YDYDJ400 >> YDYDJ401로 코드 변경) 전송
			**********************************************************/
			//크레인스케줄기동여부 'Y'이고 작업예약ID가 있으면 크레인스케줄 전송
			if ("Y".equals(crnSchYn) && !"".equals(ydWbookId)) {
				JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm,ydUserId);
				
				jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId ); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_ST_GP" , "A"       ); //야드스케쥴기동구분(Auto)
				jrYdMsg.setField("YD_SCH_REQ_GP", ydSchReqGp); //야드스케쥴요청구분
				
				
			     jrRtn = slabUtils.addSndData(jrRtn, slabComm.getCrnSchMsg(jrYdMsg));
		   
			}
			
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "대차이동실적");	
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	
	
	/**
	 *      [A] 오퍼레이션명 : L2픽업크레인 지시정보(Y3YDL015)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL015(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "L2픽업크레인 지시정보[PSlabYdL2RcvSeEJB.rcvY3YDL015] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlNo  	= slabUtils.trim(rcvMsg.getFieldString("STL_NO" )); //재료번호
			String cancelYN = slabUtils.trim(rcvMsg.getFieldString("CANCEL_YN")); //취소유무
			String ydUserId = slabUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			String ydStkColGp = "";
			String ydStkBedNo = "";
			if ("".equals(ydUserId)) { ydUserId = msgId; }

			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (stlNo.length() > 11) {
				throw new Exception("재료번호(STL_NO) 이상 [" + stlNo + "]");
			}

			
			/**********************************************************
			* 2. 해당 슬라브가 적치되어있는 Bed 검색
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STL_NO"    , stlNo                 ); //재료번호
			
			/*
			 * 저장 위치 정보 2 
				SELECT YD_STK_COL_GP
				      ,YD_STK_BED_NO
				FROM TB_YD_STKLYR
				WHERE STL_NO = :V_STL_NO
			 */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getStrLocInfo2", logId, methodNm, "저장 위치 정보 2");
			
			if (jsChk.size() > 0) {
				ydStkColGp  = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_STK_COL_GP" ));
				ydStkBedNo = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_STK_BED_NO"));
			} else {
				throw new Exception("슬라브 저장 위치 이상");
			}

			
			/**********************************************************
			* 3. 2후판 Pickup Bed Table에 Flag 초기화
			**********************************************************/
			jrParam.setField("YD_STK_COL_GP", "DBPU05"); //적치열

			//해당 베드 Flag 초기화
			/*
			 *  적치Bed(TB_YD_STKBED) Flag 초기화  
				UPDATE TB_YD_STKBED
				SET YD_COIL_OUTDIA_GRP_GP = NULL
				   ,MODIFIER = :V_MODIFIER
				   ,MOD_DDTT = SYSDATE
				WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updY3YDL015BC", logId, methodNm, "적치Bed(TB_YD_STKBED) Flag 초기화");				
			
			
			
			/**********************************************************
			* 4. 슬라브가 적치되어있는 베드에 Flag update
			**********************************************************/
			jrParam.setField("YD_STK_COL_GP", ydStkColGp); //적치열
			jrParam.setField("YD_STK_BED_NO", ydStkBedNo); //적치Bed
			jrParam.setField("MODIFIER"		, "Y3YDL015"); //L2픽업크레인 지시정보 수신

			//해당 베드 Flag 초기화
			if("N".equals(cancelYN)) {
				/*
				 * 적치Bed(TB_YD_STKBED) Flag 등록  
					UPDATE TB_YD_STKBED
					SET YD_COIL_OUTDIA_GRP_GP = 'Y'
					   ,MODIFIER = :V_MODIFIER
					   ,MOD_DDTT = SYSDATE
					WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
					AND YD_STK_BED_NO = :V_YD_STK_BED_NO
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updY3YDL015BU", logId, methodNm, "적치Bed(TB_YD_STKBED) Flag 등록");				
			}

			
			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업가능응답(Y3YDL016)
	 *      염용선 2020-12-04 개발 진행중----------------------------------------------
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL016(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업가능응답[PSlabYdL2RcvSeEJB.rcvY3YDL016] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printParam(logId, rcvMsg);
			//수신 항목 값ydEqpId
			String msgId        = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId     	= slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     		//설비ID
			String ydWrkProgStat= slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"));    //야드작업진행상태(5:권하위치변경)만 올라옴
			String ydSchCd   	= slabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));    		//야드스케쥴코드
			String ydCrnSchId  	= slabUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); 		//야드크레인스케쥴ID
			String reqYn  		= slabUtils.trim(rcvMsg.getFieldString("REQ_YN")); 				//유무응답
			String ReqMsg  		= slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_REQ_MSG")); //메시지
			String sModifier    = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); 		//수정자(Backup Only)
			String headMsgGp    = slabUtils.trim(rcvMsg.getFieldString("MSG_GP"     )); 		//head msg구분
			String ydErrorCd    = slabUtils.trim(rcvMsg.getFieldString("ERROR_CD"  )); // 에러코드
			 
			slabUtils.printLog(logId, "msgId="+msgId, "SL");
			slabUtils.printLog(logId, "ydEqpId="+ydEqpId, "SL");
			slabUtils.printLog(logId, "ydWrkProgStat="+ydWrkProgStat, "SL");
			slabUtils.printLog(logId, "ydSchCd="+ydSchCd, "SL");
			slabUtils.printLog(logId, "ydCrnSchId="+ydCrnSchId, "SL");
			slabUtils.printLog(logId, "reqYn="+reqYn, "SL");
			slabUtils.printLog(logId, "ReqMsg="+ReqMsg, "SL");
			//slabUtils.printLog(logId, "ydCrnSchIdOld="+ydCrnSchIdOld, "SL");
			
			String ydChgStlNo 		= "";
			String ydChgDnWoLoc	    = ""; 
			String ydChgDnWoLayer	= "";
			String ydBefDnWoLoc		= "";
			String ydBefDnWoLayer	= "";
			String ydWbookId 		= "";
			String ydWrkProgStst	= "";	
			String ydUpWrLoc	    = "";
			String ydUpWoLoc        = "";
			String ydL2RequestStat	= "";	
			String ydSchPrior       = ""; //스케줄 우선순위
			String autoYn           = "N";
			String ydMode2          = "";
			if ("".equals(sModifier)) { sModifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;			
			
//			else if (ydEqpWrkMode2.equals(slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE2")))) {
//				//설비 Table 설비작업Mode Check
//				ydL3HdRsCd = "EM12";
//				ydL3Msg = "오류:현재 설비작업Mode2와 동일";
//			}
			
			
				
			if (ydEqpId.length() < 6) {
				slabUtils.printLog(logId, ydEqpId + "설비ID 이상.", "SL2");				
				throw new Exception("설비ID 이상 [" + ydEqpId + "]");
			}
			/**********************************************************
			* 1. 수신 항목 값 Check
			*   무인 크레인이 이면 
			**********************************************************/
			if (slabComm.chkAutoCrn(logId,sModifier,ydEqpId) ){
				autoYn = "Y";
			} else {
				autoYn = "N";
			}
			
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
			jrParam.setField("YD_EQP_ID"         		, ydEqpId);  					
			jrParam.setField("YD_CRN_SCH_ID"      		, ydCrnSchId);  					
			jrParam.setField("YD_WRK_PROG_STAT"    		, ydWrkProgStat);  					
			jrParam.setField("YD_WRK_PROG_REQ_MSG"   	, ReqMsg);  					

			slabUtils.printLog(logId, "크레인 CHECK : [" + autoYn + "]", "SL2");	
			/* 
			  ///////////////////////////////////////// 
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
			     , YD_UP_WO_LOC
			     , YD_L2_REQUEST_STAT
			     , YD_SCH_PRIOR
			  FROM TB_YD_CRNSCH
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			*/   
			
			
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdCrnSchLocLog", logId, methodNm, "대상작업 조회");
			if (jsCrnSch.size() == 0) {
				slabUtils.printLog(logId, "크레인 스케쥴 번호가 없습니다..! [" + ydCrnSchId + "]", "SL2");
				throw new Exception("크레인 스케쥴 번호가 없습니다..! [" + ydCrnSchId + "]");
			} else {
				ydCrnSchId 		= jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID");   
				ydWbookId 		= jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID");     
				ydSchCd 		= jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD");       
				ydWrkProgStst	= jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
				ydUpWrLoc	    = jsCrnSch.getRecord(0).getFieldString("YD_UP_WR_LOC");    //권상위치
				ydUpWoLoc	    = jsCrnSch.getRecord(0).getFieldString("YD_UP_WO_LOC");    //권상위치
				ydChgStlNo   	= jsCrnSch.getRecord(0).getFieldString("STL_NO_TEMP");     //변경할 재료번호
				ydChgDnWoLoc	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC_TO"); //변경후저장위치szStkPos
				ydChgDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("STK_LYR_NO_TEMP"); //변경후저장위치szStkLyrNo
				ydBefDnWoLoc 	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC");    //변경전저장위치szOldStkPos
				ydBefDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LAYER");  //변경전저장위치szOldStkLyrNo
				ydL2RequestStat	= jsCrnSch.getRecord(0).getFieldString("YD_L2_REQUEST_STAT");  //야드L2요구상태
				ydSchPrior      = jsCrnSch.getRecord(0).getFieldString("YD_SCH_PRIOR");    //스케줄우선순위
			} 
			/* UPDATE TB_YD_CRNSCH  
			   SET  MODIFIER = :V_MODIFIER
			          , MOD_DDTT = SYSDATE
			          , YD_WRK_PROG_REQ_MSG = :V_YD_WRK_PROG_REQ_MSG
			 WHERE YD_CRN_SCH_ID       = :V_YD_CRN_SCH_ID
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdCrnSchProgStatMsgNo", logId, methodNm,  "TB_YD_CRNSCH");	
			slabUtils.printLog(logId, "==ydL2RequestStat=="+ydL2RequestStat+"=ydWrkProgStat="+ydWrkProgStat + "권하위치 변경 불가 일경우", "SL");
			
			//if (("1".equals(ydWrkProgStat) || "5".equals(ydWrkProgStat)) ) {//'권하위치변경'
				if (("1".equals(ydWrkProgStat) || "5".equals(ydWrkProgStat)) && "5".equals(ydL2RequestStat)) {
				/**********************************************************
				* 2. 권하위치 변경 요청 결과
				**********************************************************/		
				// 응답전문 N 일때(작업 불가메세지)
				if ("N".equals(reqYn)){
					/**********************************************************
					* 2.1 권하위치 변경  불가 일경우
					**********************************************************/		
					slabUtils.printLog(logId, methodNm + "권하위치 변경 불가 일경우", "SL");
					//권하위치 변경 N응답시 메세지 update
					slabUtils.printLog("", "○○○권하위치 변경 불가 [" + ydCrnSchId + "]", "[info]");
					return jrRtn;
					
				} else {
					slabUtils.printLog(logId, "autoYn : "+autoYn+ "|| ydBefDnWoLoc  : "+ydBefDnWoLoc , "SL");
					/**********************************************************
					* 2.1 권하위치 변경 가능 일경우
					* 2.1 자동인 경우 에만  권하위치 변경처리 함
					**********************************************************/		
		
					
					
					if("N".equals(autoYn)) {
						return jrRtn;	
						
					} else {
						
						/******************************************************
						 * 권하위치변경일경우엔 TB_YD_CRNSCH 수정하지 않음
						 */
						 if(!"5".equals(ydWrkProgStat)) {						
							/*
							UPDATE TB_YD_CRNSCH  A 
							   SET YD_WRK_PROG_STAT    =(CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN NVL(:V_YD_WRK_PROG_STAT,'1') ELSE YD_WRK_PROG_STAT END)
							     , YD_WRK_PROG_REQ_MSG =(CASE WHEN YD_WRK_PROG_REQ_MSG IS NULL THEN :V_YD_WRK_PROG_REQ_MSG ELSE YD_WRK_PROG_REQ_MSG END) 
							 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
							
							*/
							commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdCrnSchProgStatMsg", logId, methodNm,  "TB_YD_CRNSCH");
							
						   }
						
						
						String ydChgStkColGp = ydChgDnWoLoc.substring(0, 6); 
						String ydChgStkBedNo = ydChgDnWoLoc.substring(6, 8);
						String ydBefStkColGp = ""; 
						String ydBefStkBedNo = "";
						if ( ydBefDnWoLoc.length() == 8 && (!ydBefDnWoLoc.equals("XX010101"))){	
							ydBefStkColGp = ydBefDnWoLoc.substring(0, 6); 
							ydBefStkBedNo = ydBefDnWoLoc.substring(6, 8);
						}
						
						slabUtils.printLog(logId, methodNm + "전저장위치"+ ydBefDnWoLoc, "SL");
						
						/**********************************************************
						* 1. 크레인 정보 Read
						**********************************************************/
						JDTORecord inRecord = slabUtils.getParam(logId, methodNm, sModifier);						
						inRecord.setField("YD_CRN_SCH_ID"   , ydCrnSchId);  	
						inRecord.setField("YD_STK_COL_GP" 	, ydChgStkColGp);	
						inRecord.setField("YD_STK_BED_NO" 	, ydChgStkBedNo);	
						/*
						 * SELECT YD_STK_COL_GP  
							     , YD_STK_BED_NO  
							     , CASE WHEN YD_MTL_SH = 2 THEN  LPAD(TO_NUMBER(YD_STK_LYR_NO) + 1,3,'0')
							            ELSE LPAD(TO_NUMBER(YD_STK_LYR_NO),3,'0') END YD_STK_LYR_NO_SCH
							     , YD_STK_LYR_NO 
							     , YD_DN_WO_LOC_XAXIS
							     , YD_DN_WO_LOC_YAXIS
							     , YD_DN_WO_LOC_ZAXIS
							     , YD_DN_WO_LOC_OLD
							     , YD_DN_WO_LAYER_OLD
							     , (SELECT CASE WHEN SUM(DECODE(YD_STK_LYR_MTL_STAT,'U',1,0)) > 0 THEN 'UP' --권상대기 있음
							                    WHEN SUM(DECODE(YD_STK_LYR_MTL_STAT,'D',1,0)) > 0 THEN 'DW' --권하대기 있음
							                    WHEN SUM(DECODE(YD_STK_LYR_MTL_STAT,'E',1,0)) > 0 THEN 'ET' --아랫단 공백있음
							               ELSE '' END 
							          FROM TB_YD_STKLYR 
							         WHERE YD_STK_COL_GP = A.YD_STK_COL_GP 
							           AND YD_STK_BED_NO = A.YD_STK_BED_NO      
							           AND YD_STK_LYR_NO < A.YD_STK_LYR_NO   
							       ) AS DL_LOC_CHK_RST  
							     , YD_STK_BED_XAXIS_TOL
							     , YD_STK_BED_YAXIS_TOL
							     , YD_STK_BED_ZAXIS_TOL     
							     , (SELECT CASE WHEN SUM(DECODE(YD_STK_LYR_MTL_STAT,'U',1,0)) > 0 THEN 'UP'
							                    WHEN SUM(DECODE(YD_STK_LYR_MTL_STAT,'D',1,0)) > 0 THEN 'DW' --권하대기 있음
							               ELSE '' END 
							          FROM TB_YD_STKLYR
							         WHERE YD_STK_COL_GP||YD_STK_BED_NO = A.YD_DN_WO_LOC_OLD 
							           AND YD_STK_LYR_NO > A.YD_DN_WO_LAYER_OLD   
							       ) AS OLD_LOC_CHK_RST
							  FROM
							       (  
							        SELECT A.YD_STK_COL_GP  
							             , A.YD_STK_BED_NO  
							             , B.YD_STK_LYR_NO
							             , MIN(A.YD_STK_BED_XAXIS_TOL)          AS YD_STK_BED_XAXIS_TOL
							             , MIN(A.YD_STK_BED_YAXIS_TOL)          AS YD_STK_BED_YAXIS_TOL
							             , MIN(A.YD_STK_BED_ZAXIS_TOL)          AS YD_STK_BED_ZAXIS_TOL
							             , MIN(B.YD_STK_LYR_XAXIS)            AS YD_DN_WO_LOC_XAXIS
							             , MIN(B.YD_STK_LYR_YAXIS)            AS YD_DN_WO_LOC_YAXIS
							             , MIN(B.YD_STK_LYR_ZAXIS)            AS YD_DN_WO_LOC_ZAXIS
							             , MIN(CM.YD_DN_WO_LOC)                 AS YD_DN_WO_LOC_OLD
							             , MIN(CM.YD_DN_WO_LAYER)               AS YD_DN_WO_LAYER_OLD
							             , MIN(CM.YD_MTL_SH)                    AS YD_MTL_SH
							          FROM USRYDA.TB_YD_STKBED A
							             , USRYDA.TB_YD_STKLYR B
							             --이전위치
							             , (SELECT CM.YD_DN_WO_LOC
							                      ,CM.YD_DN_WO_LAYER       
							                      ,CM.YD_MTL_SH
							                      ,CM.YD_MTL_WT
							                      ,CM.YD_MTL_T
							                  FROM (SELECT CS.YD_CRN_SCH_ID
							                              ,MIN(CS.YD_WBOOK_ID   ) AS YD_WBOOK_ID
							                              ,MIN(CS.YD_DN_WO_LOC  ) AS YD_DN_WO_LOC
							                              ,MIN(CS.YD_DN_WO_LAYER) AS YD_DN_WO_LAYER
							                              ,COUNT(*)               AS YD_MTL_SH
							                              ,SUM(CC.SLAB_WT  )      AS YD_MTL_WT
							                              ,SUM(CC.SLAB_T   )      AS YD_MTL_T
							                          FROM TB_YD_CRNSCH    CS
							                              ,TB_YD_CRNWRKMTL CM
							                              ,TB_YD_STOCK     ST
							                              ,VW_YD_SLABCOMM  CC
							                         WHERE CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
							                           AND CM.YD_CRN_SCH_ID = CS.YD_CRN_SCH_ID
							                           AND CM.STL_NO = ST.STL_NO
							                           AND CM.STL_NO = CC.SLAB_NO
							                           AND CM.DEL_YN = 'N'
							                         GROUP BY CS.YD_CRN_SCH_ID) CM) CM
							         WHERE A.YD_STK_COL_GP  = SUBSTR(:V_YD_STK_COL_GP, 0, 6)
							           AND A.YD_STK_COL_GP  = B.YD_STK_COL_GP
							           AND A.YD_STK_BED_NO  = B.YD_STK_BED_NO
							           AND A.YD_STK_BED_NO  = :V_YD_STK_BED_NO
							           AND B.YD_STK_LYR_NO  = (SELECT NVL(MIN(YD_STK_LYR_NO) , '001')
							                                     FROM TB_YD_STKLYR 
							                                    WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
							                                      AND YD_STK_BED_NO = :V_YD_STK_BED_NO
							                                      AND YD_STK_LYR_ACT_STAT = 'E'
							                                      AND YD_STK_LYR_MTL_STAT = 'E'
							                                      AND YD_STK_LYR_NO < (SELECT NVL(MIN(YD_STK_LYR_NO), '20')
							                                                              FROM TB_YD_STKLYR 
							                                                             WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
							                                                               AND YD_STK_BED_NO = :V_YD_STK_BED_NO
							                                                               AND YD_STK_LYR_ACT_STAT IN ('C', 'N')
							                                                           )
							                                   )
							           AND A.DEL_YN = 'N'
							           AND A.YD_STK_BED_ACT_STAT IN ('L', 'O')
							           AND B.YD_STK_LYR_ACT_STAT = 'E'
							           AND B.YD_STK_LYR_MTL_STAT = 'E'
							           AND B.STL_NO IS NULL
							        GROUP BY A.YD_STK_COL_GP,A.YD_STK_BED_NO,B.YD_STK_LYR_NO   
							       ) A
							 WHERE 1 = 1  
						 */
						 jsCrnSch = commDao.select(inRecord, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getCrnSchDnWoLocCurLyr", logId, methodNm, "신규권하위치 조회");
						
						if (jsCrnSch.size() == 0) {
							slabUtils.printLog(logId, methodNm + "저장위치 이상", "SL");
							throw new Exception("저장위치 이상.! [" + ydCrnSchId + "]");
						}
						
						JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
						
						ydChgDnWoLayer 				= slabUtils.trim(jrCrnSch.getFieldString("YD_STK_LYR_NO"));
						String sSTACK_LAYER_GP_SCH  = slabUtils.trim(jrCrnSch.getFieldString("YD_STK_LYR_NO_SCH"));
						
						/**********************************************************
						* 1. 기존(전) 정보 수정
						*  기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다.
						**********************************************************/					
						//-----------------------------------------------------------------------
						inRecord.setField("YD_STK_COL_GP" 	, ydBefStkColGp);	
						inRecord.setField("YD_STK_BED_NO" 	, ydBefStkBedNo);	
						inRecord.setField("YD_STK_LYR_NO" 	, ydBefDnWoLayer);	
						inRecord.setField("YD_CRN_SCH_ID" 	, ydCrnSchId);
						/*  
						SELECT STL_NO
						     , YD_STK_LYR_MTL_STAT
						     , YD_STK_LYR_ACT_STAT 
						   FROM TB_YD_STKLYR
						 WHERE YD_STK_COL_GP   = :V_YD_STK_COL_GP
						   AND YD_STK_BED_NO     = :V_YD_STK_BED_NO 
						   AND STL_NO IN (SELECT STOCK_ID
						                      FROM TB_YD_CRNWRKMTL 
						                     WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						                       AND DEL_YN = 'N'
						                   )
						 ORDER BY YD_STK_LYR_NO    
						*/   
						JDTORecordSet jsBefStkLay = commDao.select(inRecord, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getStackLayerInfo", logId, methodNm, "신규 적재위치 조회");
						
						if ( ydBefDnWoLoc.length() == 8 && (!"XX010101".equals(ydBefDnWoLoc))){	
											
							if (jsBefStkLay.size() == 0) {
								slabUtils.printLog(logId, methodNm + "저장위치 이상", "SL");
								return jrRtn;
							} else {
							
								// 기존 권하지시위치 정보 Clear
								inRecord.setField("STL_NO"              , "");
								inRecord.setField("YD_STK_LYR_ACT_STAT"	, "E");
								inRecord.setField("YD_STK_LYR_MTL_STAT" , "E");
						    	/*
								UPDATE TB_YD_STKLYR            
									   SET MOD_DDTT     = SYSDATE             
									     , MODIFIER     = :V_MODIFIER             
									     , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
									     , STL_NO              = NULL
									     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
									 WHERE YD_STK_COL_GP   = :V_YD_STK_COL_GP
									   AND YD_STK_BED_NO   = :V_YD_STK_BED_NO
									   AND STL_NO IN (SELECT STL_NO
									                      FROM TB_YD_CRNWRKMTL 
									                     WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
									                       AND DEL_YN = 'N'
									                   )
						    	 */
								commDao.update(inRecord, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdStkLyrYdStkColBedGp", logId, methodNm, "TB_YD_SKLYR 등록");
							}
						}	
			
						/**********************************************************
						* 3. 신규 위치 SET
						**********************************************************/					
						// 신규위치에 정보를 Setting
						//inRecord = JDTORecordFactory.getInstance().create();
						int nSTACK_LAYER_GP = Integer.parseInt(ydChgDnWoLayer);
						JDTORecord jrBefStkLay = JDTORecordFactory.getInstance().create();
						
						for (int i = 1; i <= jsBefStkLay.size(); ++i) {
							jsBefStkLay.absolute(i);
							jrBefStkLay = jsBefStkLay.getRecord();
							//inRecord.setField("MODIFIER"        		, sModifier);	
							inRecord.setField("YD_STK_COL_GP" 			, ydChgStkColGp);	
							inRecord.setField("YD_STK_BED_NO" 			, ydChgStkBedNo);
							if (nSTACK_LAYER_GP < 10) {
								inRecord.setField("YD_STK_LYR_NO" 		, "00"+nSTACK_LAYER_GP);
							} else {
								inRecord.setField("YD_STK_LYR_NO" 		, "" +nSTACK_LAYER_GP);
							}
							inRecord.setField("YD_STK_LYR_ACT_STAT"	, "E");
							inRecord.setField("YD_STK_LYR_MTL_STAT"	, "D");
							inRecord.setField("STL_NO"		, slabUtils.trim(jrBefStkLay.getFieldString("STL_NO")));
							slabUtils.printLog(logId, jrBefStkLay.getFieldString("STL_NO") + "::STL_NO", "SL");
							slabUtils.printLog(logId, nSTACK_LAYER_GP + "::YD_STK_LYR_NO", "SL");
					    	/*
							/* 
								 UPDATE TB_YD_STKLYR            
								   SET MOD_DDTT     = SYSDATE             
								     , MODIFIER     = :V_MODIFIER             
								     , YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT, YD_STK_LYR_ACT_STAT)
								     , STL_NO              = :V_STL_NO
								     , YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT , YD_STK_LYR_MTL_STAT)
								 WHERE YD_STK_COL_GP   = :V_YD_STK_COL_GP
								   AND YD_STK_BED_NO   = :V_YD_STK_BED_NO
								   AND YD_STK_LYR_NO   = :V_YD_STK_LYR_NO 
					    	 */
							commDao.update(inRecord, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdStkLyrYdStkColBedGp2", logId, methodNm, "TB_YD_STKLYR 등록");				
							nSTACK_LAYER_GP++;
						}
						
						// 신규 좌표 정보 READ
						JDTORecord jrPara = JDTORecordFactory.getInstance().create();
						jrPara.setField("YD_STK_COL_GP" 	, ydChgStkColGp);	
						jrPara.setField("YD_STK_BED_NO" 	, ydChgStkBedNo);	
						jrPara.setField("YD_STK_LYR_NO", sSTACK_LAYER_GP_SCH);	
						/*
						SELECT SL.YD_STK_LYR_XAXIS 
						     , SB.YD_STK_BED_XAXIS_TOL
						     , SL.YD_STK_LYR_YAXIS 
						     , SB.YD_STK_BED_YAXIS_TOL
						     , SL.YD_STK_LYR_ZAXIS 
						     , SB.YD_STK_BED_ZAXIS_TOL
						     , SC.ROTATION_ANGLE
						  FROM TB_YD_STKCOL SC
						     , TB_YD_STKBED SB
						     , TB_YD_STKLYR SL
						 WHERE SC.YD_STK_COL_GP = SB.YD_STK_COL_GP
						   AND SB.YD_STK_COL_GP = SL.YD_STK_COL_GP
						   AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO
						   AND SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
						   AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
						   AND SL.YD_STK_LYR_NO = :V_YD_STK_LYR_NO
						*/   
						JDTORecordSet jsStkLayAxis = commDao.select(jrPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdStkbedLyr", logId, methodNm, "신규 좌표위치 조회");				
						if (jsStkLayAxis.size() == 0) {
							slabUtils.printLog(logId, methodNm + "저장위치 이상", "SL");
							throw new Exception("저장위치 이상.! [" + ydCrnSchId + "]");
						} 
						jsStkLayAxis.absolute(1);
						JDTORecord jrStkLayAxis = JDTORecordFactory.getInstance().create();
						jrStkLayAxis.setRecord(jsStkLayAxis.getRecord());
						
						inRecord   = slabUtils.getParam(logId, methodNm, sModifier);	
						inRecord.setField("YD_CRN_SCH_ID"	        , ydCrnSchId);
						inRecord.setField("YD_DN_WO_LOC"	     	, ydChgDnWoLoc);	
						inRecord.setField("YD_DN_WO_LAYER"	        , sSTACK_LAYER_GP_SCH);
						inRecord.setField("YD_DN_WO_LOC_XAXIS"		, slabUtils.trim(jrStkLayAxis.getFieldString("YD_STK_LYR_XAXIS")));
						inRecord.setField("YD_DN_WO_XAXIS_GAP_MAX"	, slabUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_XAXIS_TOL")));
						inRecord.setField("YD_DN_WO_XAXIS_GAP_MIN"	, slabUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_XAXIS_TOL")));
						inRecord.setField("YD_DN_WO_LOC_YAXIS"		, slabUtils.trim(jrStkLayAxis.getFieldString("YD_STK_LYR_YAXIS")));
						inRecord.setField("YD_DN_WO_YAXIS_GAP_MAX"	, slabUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_YAXIS_TOL")));
						inRecord.setField("YD_DN_WO_YAXIS_GAP_MIN"	, slabUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_YAXIS_TOL")));
						inRecord.setField("YD_DN_WO_LOC_ZAXIS"		, slabUtils.trim(jrStkLayAxis.getFieldString("YD_STK_LYR_ZAXIS")));
						inRecord.setField("YD_DN_WO_ZAXIS_GAP_MAX"	, slabUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_ZAXIS_TOL")));
						inRecord.setField("YD_DN_WO_ZAXIS_GAP_MIN"	, slabUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_ZAXIS_TOL")));
						inRecord.setField("YD_L2_REQUEST_STAT"	    , ""); 
						inRecord.setField("YD_DN_WO_LOC_TO"		    , ""); 
						inRecord.setField("DOWN_ROTATION_ANGLE"		, slabUtils.trim(rcvMsg.getFieldString("ROTATION_ANGLE"))); //회전각도
						if ("".equals(ydUpWrLoc)){
							inRecord.setField("YD_WRK_PROG_STAT"	, "1");
						} else{
							inRecord.setField("YD_WRK_PROG_STAT"	, "2");
						}
						/* 
						MERGE INTO TB_YD_CRNSCH CS USING (
						    SELECT SL.YD_STK_LYR_XAXIS    AS YD_DN_WO_LOC_XAXIS
						         , SB.YD_STK_BED_XAXIS_TOL  AS YD_DN_WO_XAXIS_GAP_MAX
						         , SB.YD_STK_BED_XAXIS_TOL  AS YD_DN_WO_XAXIS_GAP_MIN
						         , SL.YD_STK_LYR_YAXIS    AS YD_DN_WO_LOC_YAXIS
						         , SB.YD_STK_BED_YAXIS_TOL  AS YD_DN_WO_YAXIS_GAP_MAX 
						         , SB.YD_STK_BED_YAXIS_TOL  AS YD_DN_WO_YAXIS_GAP_MIN 
						         , SL.YD_STK_LYR_ZAXIS    AS YD_DN_WO_LOC_ZAXIS
						         , SB.YD_STK_BED_ZAXIS_TOL  AS YD_DN_WO_ZAXIS_GAP_MAX 
						         , SB.YD_STK_BED_ZAXIS_TOL  AS YD_DN_WO_ZAXIS_GAP_MIN 
						         , SC.ROTATION_ANGLE        AS DOWN_ROTATION_ANGLE
						         , :V_YD_CRN_SCH_ID         AS YD_CRN_SCH_ID
						      FROM  TB_YD_STKCOL SC
						          , TB_YD_STKBED SB
						          , TB_YD_STKLYR SL
						     WHERE SC.YD_STK_COL_GP = SB.YD_STK_COL_GP
						       AND SB.YD_STK_COL_GP = SL.YD_STK_COL_GP
						       AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO
						       AND SL.YD_STK_COL_GP   = SUBSTR(:V_YD_DN_WO_LOC,1,6)
						       AND SL.YD_STK_BED_NO   = SUBSTR(:V_YD_DN_WO_LOC,7,2)
						       AND SL.YD_STK_LYR_NO = :V_YD_DN_WO_LAYER
						       
						) DD ON (CS.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID)
						
						WHEN MATCHED THEN UPDATE SET
						       MODIFIER = :V_MODIFIER
						      ,MOD_DDTT = SYSDATE
						      ,YD_DN_WO_LOC             = NVL(:V_YD_DN_WO_LOC           ,YD_DN_WO_LOC)
						      ,YD_DN_WO_LAYER           = NVL(:V_YD_DN_WO_LAYER         ,YD_DN_WO_LAYER)
						      ,YD_DN_WO_LOC_XAXIS       = NVL(DD.YD_DN_WO_LOC_XAXIS     ,YD_DN_WO_LOC_XAXIS)
						      ,YD_DN_WO_XAXIS_GAP_MAX   = NVL(DD.YD_DN_WO_XAXIS_GAP_MAX ,YD_DN_WO_XAXIS_GAP_MAX)
						      ,YD_DN_WO_XAXIS_GAP_MIN   = NVL(DD.YD_DN_WO_XAXIS_GAP_MIN ,YD_DN_WO_XAXIS_GAP_MIN)
						      ,YD_DN_WO_LOC_YAXIS       = NVL(DD.YD_DN_WO_LOC_YAXIS     ,YD_DN_WO_LOC_YAXIS)
						      ,YD_DN_WO_YAXIS_GAP_MAX   = NVL(DD.YD_DN_WO_YAXIS_GAP_MAX ,YD_DN_WO_YAXIS_GAP_MAX)
						      ,YD_DN_WO_YAXIS_GAP_MIN   = NVL(DD.YD_DN_WO_YAXIS_GAP_MIN ,YD_DN_WO_YAXIS_GAP_MIN)
						      ,YD_DN_WO_LOC_ZAXIS       = NVL(DD.YD_DN_WO_LOC_ZAXIS     ,YD_DN_WO_LOC_ZAXIS)
						      ,YD_DN_WO_ZAXIS_GAP_MAX   = NVL(DD.YD_DN_WO_ZAXIS_GAP_MAX ,YD_DN_WO_ZAXIS_GAP_MAX)
						      ,YD_DN_WO_ZAXIS_GAP_MIN   = NVL(DD.YD_DN_WO_ZAXIS_GAP_MIN ,YD_DN_WO_ZAXIS_GAP_MIN)
						      ,YD_L2_REQUEST_STAT       = :V_YD_L2_REQUEST_STAT 
						      ,YD_DN_WO_LOC_TO          = :V_YD_DN_WO_LOC_TO
						      ,YD_WRK_PROG_STAT         = :V_YD_WRK_PROG_STAT
						      ,YD_WORD_DT               = SYSDATE
						      ,DOWN_ROTATION_ANGLE      = NVL(DD.DOWN_ROTATION_ANGLE    ,DOWN_ROTATION_ANGLE)
						 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						 */				
						commDao.update(inRecord, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdCrnWrk", logId, methodNm, "TB_YD_CRNSCH 등록");		
						
						//권하위치 변경후 작업지시정보를  재전송 (Manual 작업지시보내는 방법으로  선택 인경우만 전송한다)
						//권상지시 or 권상완료
					
			    		if ( "1".equals(ydWrkProgStst) || "2".equals(ydWrkProgStst)){ //권상지시 or 권상완료
			    			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, sModifier);	
			    			jrYdMsg.setField("JMS_TC_CD"         	, "Y3YDL007"); 					//JMSTC코드
			    			jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, slabUtils.getDateTime14()); 	//JMSTC생성일시
			    			jrYdMsg.setField("YD_EQP_ID"        	, ydEqpId);  					//야드설비ID
			    			jrYdMsg.setField("YD_WRK_PROG_STAT"     , ydWrkProgStst); 				//야드작업진행상태
			    			jrYdMsg.setField("YD_SCH_CD"        	, ydSchCd);						//야드스케쥴코드
			    			jrYdMsg.setField("YD_CRN_SCH_ID"        , ydCrnSchId); 					//야드크레인스케쥴ID
			    			
			    			
			    			//크레인작업지시 전문을 추가
			    			JDTORecord jrGetYdMsg = this.rcvY3YDL007(jrYdMsg);
			    			String rtnCd	 = slabUtils.nvl(jrGetYdMsg.getFieldString("RTN_CD"), "0");
			    			String rtnMsg	 = slabUtils.nvl(jrGetYdMsg.getFieldString("RTN_MSG"), "");
			    			slabUtils.printLog(logId,  " =======rtnCd:"+ rtnCd, "FL");
			    			slabUtils.printLog(logId,  " =======rtnMsg:"+ rtnMsg, "FL");
			    			// ROLLBACK 시 전문 발생
			    			if (!"1".equals(rtnCd)) {
			    				jrRtn.setField("RTN_CD"	, "0");
			    				jrRtn.setField("RTN_MSG", rtnMsg);
			    				return jrRtn;
			    			}
			    			
			    			//크레인작업지시요구 전문을 추가
							jrRtn = slabUtils.addSndData(jrRtn, jrGetYdMsg);
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
			    		if ("TC".equals(szBefEqpGp)||"PT".equals(szBefEqpGp)){
			    			if (!szChgEqpGp.equals(szBefEqpGp)){
		
			    				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
			    				
			    				String szULGp  = ydSchCd.substring(6,7);  //상차구분
			    				String szCarGp = ydSchCd.substring(2,4);
			    				
			    				//스케줄 코드가 차량/대차 인경우 구분
			    				if("TC".equals(szCarGp)){
			    	 				if("U".equals(szULGp)){  
		
			    	 					/*--대차스케줄 작업예약ID 삭제 
			    	 					UPDATE USRYDA.TB_YD_TCARSCH
										   SET MODIFIER              = :V_MODIFIER
										      ,MOD_DDTT              = SYSDATE
										      ,YD_CARLD_WRK_BOOK_ID  = NULL
										 WHERE DEL_YN                = 'N'
										   AND YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
			    	    				*/   
			    	    				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updCommTcarSchWbDelLd", logId, methodNm, "TB_YD_TCARSCH");				
		
			    					}else if("L".equals(szULGp)){
			    						//하차인경우 작업예약 정보 삭제
			    						/*--대차스케줄 작업예약ID 삭제 
			    						UPDATE USRYDA.TB_YD_TCARSCH
			    						   SET MODIFIER              = :V_MODIFIER
			    						      ,MOD_DDTT              = SYSDATE
			    						      ,YD_CARUD_WRK_BOOK_ID  =NULL
			    						 WHERE DEL_YN                = 'N'
			    						   AND YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
			    	    				*/   
			    	    				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updCommTcarSchWbDelUd", logId, methodNm, "TB_YD_TCARSCH");				
			    	    				
			    					}
			    					
			    				}else if("PT".equals(szCarGp)){
			    					// 차량인 경우 		
			    					if("U".equals(szULGp)){
			    						
			    						/* com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updCommCarSchWbDelLd
			    						--  상차 차량 작업 예약 ID CLEAR
			    						UPDATE USRYDA.TB_YD_CARSCH
			    						   SET MODIFIER              = :V_MODIFIER
			    						      ,MOD_DDTT              = SYSDATE
			    						      ,YD_CARLD_WRK_BOOK_ID  = NULL
			    						 WHERE DEL_YN                = 'N'
			    						   AND YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
			    						 */  
			    						commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updCommCarSchWbDelLd", logId, methodNm, "TB_YD_CARSCH");				
			    						
			    					}else if("L".equals(szULGp)){
			    						//하차인경우 작업예약 정보 삭제
			    						
			    						/* com.inisteel.cim.yd.pslabyd.session.PSlabYdJspSeEJB.updCommCarSchWbDelUd 
			    						--  하차 차량 작업 예약 ID CLEAR
			    						UPDATE USRYDA.TB_YD_CARSCH
			    						   SET MODIFIER              = :V_MODIFIER
			    						      ,MOD_DDTT              = SYSDATE
			    						      ,YD_CARUD_WRK_BOOK_ID  = NULL
			    						 WHERE DEL_YN                = 'N'
			    						   AND YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
			    						*/   
			    						commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updCommCarSchWbDelUd", logId, methodNm, "TB_YD_CARSCH");				
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
					
					
					slabUtils.printLog(logId, methodNm + "가능인 경우", "SL");
					/*
					UPDATE TB_YD_CRNSCH  A 
					   SET YD_WRK_PROG_STAT    =(CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN NVL(:V_YD_WRK_PROG_STAT,'1') ELSE YD_WRK_PROG_STAT END)
					     , YD_WRK_PROG_REQ_MSG =(CASE WHEN YD_WRK_PROG_REQ_MSG IS NULL THEN :V_YD_WRK_PROG_REQ_MSG ELSE YD_WRK_PROG_REQ_MSG END) 
					 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					
					*/
					 if(!"5".equals(ydWrkProgStat)) {		
					    commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdCrnSchProgStatMsg", logId, methodNm,  "TB_YD_CRNSCH");
					 }
					slabUtils.printLog(logId, "요청구분 [ " + reqYn + " - 작업취소 " + headMsgGp + " ]", "SL");
				
					if ("D".equals(headMsgGp) && "Y".equals(autoYn) ){
						slabUtils.printLog(logId, "[DYD400]스케쥴취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");
					
						
						/**********************************************************
						* 이송하차일 경우 작업예약 취소
						**********************************************************/
//						if ("PT02LM".equals(ydSchCd.substring(2, 8))){
//							EJBConnector ejbConn1 = new EJBConnector("default", "BSlabJspSeEJB", this);
//							JDTORecord jrRst1 = (JDTORecord)ejbConn1.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
//							jrRtn = slabUtils.addSndData(jrRtn, jrRst1);
//						}

						slabUtils.printLog(logId, "ydL2RequestStat [ " + ydL2RequestStat + " - 작업취소  ]", "SL");
						
						if ("X".equals(ydL2RequestStat)){   		// 화면에서 작업 취소 임 : 작업예약 삭제
							/**********************************************************
							* 2. 작업예약 취소
							**********************************************************/
							EJBConnector ejbConn1 = new EJBConnector("default", "PSlabYdJspSeEJB", this);
							JDTORecord jrBookCncl = (JDTORecord)ejbConn1.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
							
							String rtnCd	 = slabUtils.nvl(jrBookCncl.getFieldString("RTN_CD"), "0");
							String rtnMsg	 = slabUtils.nvl(jrBookCncl.getFieldString("RTN_MSG"), "");
							 if(!"1".equals(rtnCd)){
									jrRtn.setField("RTN_CD"	, "0");
									jrRtn.setField("RTN_MSG", rtnMsg);
									return jrRtn;
								}
							jrRtn = slabUtils.addSndData(jrRtn, jrBookCncl);
				
							// 스케쥴 취소+작업예약 취소 인 경우 
							/**********************************************************
							* 11. 크레인작업지시요구 전문 호출(Y3YDL007)
							**********************************************************/
							JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
							jrYdMsg.setResultCode(logId);	//Log ID
							jrYdMsg.setResultMsg(methodNm);	//Log Method Name

							jrYdMsg.setField("JMS_TC_CD"		, "Y3YDL007"); //JMSTC코드
							jrYdMsg.setField("YD_EQP_ID"       	, ydEqpId   ); //야드설비ID
							jrYdMsg.setField("YD_WRK_PROG_STAT"	, "4"       ); //야드작업진행상태(권하완료)
							jrYdMsg.setField("YD_SCH_CD"       	, ydSchCd   ); //야드스케쥴코드
							jrYdMsg.setField("MODIFIER"        	, sModifier  ); //수정자
							
							JDTORecord jrGetYdMsg = this.rcvY3YDL007(jrYdMsg);
			    			 rtnCd	 = slabUtils.nvl(jrGetYdMsg.getFieldString("RTN_CD"), "0");
			    			 rtnMsg	 = slabUtils.nvl(jrGetYdMsg.getFieldString("RTN_MSG"), "");
			    			slabUtils.printLog(logId,  " =======rtnCd:"+ rtnCd, "FL");
			    			slabUtils.printLog(logId,  " =======rtnMsg:"+ rtnMsg, "FL");
			    			
			    			
							//크레인작업지시 요구을 추가
							jrRtn = slabUtils.addSndData(jrRtn, jrGetYdMsg);
						
						}	
					}
					
					/***************************
					 * 일시정지-긴급작업(S1)
					 ***************************/
				
				 } else if ("N".equals(reqYn)){
					
					 slabUtils.printLog(logId, methodNm + "불가 일경우", "SL");
					
					if(ReqMsg.length()>=4) {
						if("E001".equals(ReqMsg.substring(0,4))) {
							//작업지시가 내려와 이미 슬라브를 집었는데 다음작업지시가 내려온 경우
							//다음작업지시를 수행할 수 없는 상황 (차상국은 이전 작업지시를 그대로 실행)
							//L3 는 'N' 응답에 'E001' 메세지 받은 크레인 스케줄 ID의 작업진행상태를  'W'로 변경한다.
							slabUtils.printLog(logId, methodNm + "E001 불가 일경우", "SL");
							
							/* 
							UPDATE TB_YD_CRNSCH
							   SET YD_WRK_PROG_STAT = (CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN NVL(:V_YD_WRK_PROG_STAT,'1') ELSE YD_WRK_PROG_STAT END)
							 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID */
							jrParam.setField("YD_WRK_PROG_STAT"	, "W");  					
							jrParam.setField("YD_CRN_SCH_ID"   	, ydCrnSchId);  					
							commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdCrnSchYdWrkProgStat", logId, methodNm,  "크레인 스케줄 작업진행상태 변경");
							

							if(ReqMsg.length()>=23) {
								
								//이전 스케줄ID의 상태를 '1'로 변경한다.
								String befCrnSchId = ReqMsg.substring(5,23);
								  
								/* 
								UPDATE TB_YD_CRNSCH
								   SET YD_WRK_PROG_STAT = (CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN NVL(:V_YD_WRK_PROG_STAT,'1') ELSE YD_WRK_PROG_STAT END)
								 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID */
								jrParam.setField("YD_WRK_PROG_STAT"	, "1");  					
								jrParam.setField("YD_CRN_SCH_ID"   	, befCrnSchId);  					
								commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdCrnSchYdWrkProgStat", logId, methodNm,  "이전 크레인 스케줄 작업진행상태  변경");
							}									
						}
					}
					return jrRtn;
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
	
	

	/**
	 *      [A] 오퍼레이션명 : 차량형상 완료여부(Y3YDL017)                           
	 *      PYS   2020.12.10    						    	    
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL017(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "후판슬라브차량형상 완료여부[PSlabYdL2RcvSeEJB.rcvY3YDL017] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
	    try{
	    	slabUtils.printLog(logId, methodNm, "S+");
	    	slabUtils.printLog(logId, "▶ 후판슬라브차량형상 완료여부 START!", "SL");
	    	
			String sPtLoadLoc	= slabUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"));     //상차도 위치
			String sTrnEqpCd   	= slabUtils.trim(rcvMsg.getFieldString("CAR_NO"));     		//차량번호
			String sYdEqpWrkSh	= slabUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH")); //야드설비작업매수
			String msgId		= slabUtils.nvl(slabUtils.getMsgId(rcvMsg),"Y3YDL017"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sModifier 	= slabUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			String ydFrmYn      = "";
			
			slabUtils.printLog(logId, "▶ PARAMETER 확인.", "SL");
			slabUtils.printLog(logId, "▷ 상차도 위치(sPtLoadLoc)		: "	+ sPtLoadLoc	, "SL");
			slabUtils.printLog(logId, "▷ 차량번호(sTrnEqpCd)		: "	+ sTrnEqpCd		, "SL");
			slabUtils.printLog(logId, "▷ 야드설비작업매수(sYdEqpWrkSh)	: "	+ sYdEqpWrkSh	, "SL");
			slabUtils.printLog(logId, "▷ 수신 전문 I/F ID(msgId)		: "	+ msgId			, "SL");
			slabUtils.printLog(logId, "▷ 수정자(sModifier)			: "	+ sModifier		, "SL");
			slabUtils.printLog(logId, "▷ 형상사용유무(ydFrmYn)		: "	+ ydFrmYn		, "SL");
			slabUtils.printLog(logId, "▶ PARAMETER 확인종료.", "SL");
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			JDTORecord jrRtn    = slabUtils.getParam(logId, methodNm, sModifier);
			JDTORecord jrParam  = slabUtils.getParam(logId, methodNm, sModifier);
			
			int iYdEqpWrkSh = Integer.parseInt(sYdEqpWrkSh);
			
			String sStlNo          = "";    //SLAB번호
			String sLoadLocCd	   = "";	//차량적재위치
			String sWgtCenterXaxis = "";	//야드크레인X축(mm)
			String sWgtCenterYaxis = "";	//야드크레인Y축(mm)-중간4분의2지점 
			String sLoadLocCd2	   = "";	//차량적재위치            -1/4
			String sWgtCenterYaxis2 = "";	//야드크레인Y축(mm)-앞4분의1지점 
			String sLoadLocCd3	   = "";	//차량적재위치            -3/4
			String sWgtCenterYaxis3 = "";	//야드크레인Y축(mm)-앞4분의3지점
			String sWgtCenterZaxis = "";	//야드크레인Z축(mm)
			String sBendingGp	   = "";	//BENDING 구분(+:상,-:하)
			String sBendingAxis	   = "";	//BENDING 량(mm)
			String sYdStkColDirGp  = "";	//야드적치열방향구분(+:좌,-:우)
			String sYdStkColDeg	   = "";	//재료적치각도(소재의 뒤츨린 각도)
			String szYdAimBayGp    = "";    //야드목표동구분 
			
			String carLocCd       = "";    // 앞자지 0:차상중간,1:차상1베드 2:차상2베드
			String ydWbookId = "";
			String ydSchCd = "";
			if(sPtLoadLoc.length() < 6) {
				throw new Exception("상차도 위치 [" + sPtLoadLoc + "] 가 6자리가 아닙니다!!");
			} 
			
			slabUtils.printLog(logId, "▶ 차량포인트 정보를 조회합니다.", "SL");
			slabUtils.printLog(logId, "▷ 차량포인트(적치열)	: "	+ sPtLoadLoc , "SL");
			
			jrParam.setField("YD_STK_COL_GP", sPtLoadLoc);
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdPntByStkColGp (상전문이 들어 왔지만 형상 적용 여부가 "N"이라면 처리 안함.)
				SELECT YD_CARPNT_CD
				      ,YD_STK_COL_ACT_STAT
				      ,YD_CAR_USETYPE_GP
				      ,YD_GP
				      ,YD_BAY_GP
				      ,YD_STK_COL_GP
				      ,TRN_EQP_CD
				      ,CAR_NO
				      ,CARD_NO
				      ,WLOC_CD
				      ,YD_PNT_CD
				      ,YD_CARPNT_DESC
				      ,YD_SPAN_FROM
				      ,YD_SPAN_TO
				      ,YD_FRM_YN
				  FROM TB_YD_CARPOINT  
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP 
			 */
			JDTORecordSet jsYdRule = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdPntByStkColGp", logId, methodNm, "야드적치열구분으로 차량포인트 정보 조회");
			if (jsYdRule.size() > 0) {
				ydFrmYn = slabUtils.nvl(jsYdRule.getRecord(0).getFieldString("YD_FRM_YN"),"N");
				
				slabUtils.printLog(logId, "▶ 차량포인트 조회완료.", "SL");
				slabUtils.printLog(logId, "▷ 형상사용유무(YD_FRM_YN)	: "	+ ydFrmYn , "SL");
				
			} else {
				throw new Exception("해당 상차도 위치 [" + sPtLoadLoc + "] 가 존재하지 않습니다.!!");
			}
			
			slabUtils.printLog(logId, "▶ 상차작업 형상정보인지 확인합니다. ===> 야드설비작업매수가 0이면 상차로 판단. ", "SL");
			slabUtils.printLog(logId, "▷ 야드설비작업매수(sYdEqpWrkSh) : "	+ iYdEqpWrkSh	, "SL");
			if(iYdEqpWrkSh == 0) {	// 상차일 때 처리 -> 상차 시 작업매수가 0으로 넘어온다.
				
				slabUtils.printLog(logId, "▶ 상차작업 형상정보이므로 RULE 테이블에 저장합니다.(TB_YD_RULE - 'DYD006')", "SL");
				slabUtils.printLog(logId, "▷ 형상사용유무(YD_FRM_YN) 재확인 : "	+ ydFrmYn , "SL");
				
				/**********************************
				 * 상차이면서 LOT가 편성되어 있지 않는 경우 
				 **********************************/
				//sStlNo = slabUtils.trim(rcvMsg.getFieldString("SLAB_NO1"));
				
				//if(sPtLoadLoc.equals(sStlNo)) {
					if("Y".equals(ydFrmYn)) {
						slabUtils.printLog(logId, "▶ 형상정보를 사용하므로 TB_YD_RULE테이블에 형상정보를 UPDATE 합니다.", "SL");
						
						//이송상차 형상인식 스켄 값 
						slabUtils.printLog(logId, "::::::>>>> 이송상차 형상 XYZ 값 수신 : " + sPtLoadLoc + " , 차량번호 : " + sTrnEqpCd , "SL");
						slabUtils.printLog(logId, methodNm, "SL");
						
						sLoadLocCd          = slabUtils.trim(rcvMsg.getFieldString("LOAD_LOC_CD1"));
						sWgtCenterXaxis 	= slabUtils.trim(rcvMsg.getFieldString("WGT_CENTER_XAXIS1"));  //"WGT_CENTER_XAXIS"+sNO
						sWgtCenterYaxis 	= slabUtils.trim(rcvMsg.getFieldString("WGT_CENTER_YAXIS1"));  //"WGT_CENTER_YAXIS"+sNO
						sWgtCenterZaxis 	= slabUtils.trim(rcvMsg.getFieldString("WGT_CENTER_ZAXIS1"));  //"WGT_CENTER_ZAXIS"+sNO
						
						sLoadLocCd2         = slabUtils.trim(rcvMsg.getFieldString("LOAD_LOC_CD2"));
						sWgtCenterYaxis2 	= slabUtils.trim(rcvMsg.getFieldString("WGT_CENTER_YAXIS2"));  //"WGT_CENTER_YAXIS"+sNO

						sLoadLocCd3         = slabUtils.trim(rcvMsg.getFieldString("LOAD_LOC_CD3"));       //요청시 "SLAB_NO3" 로 변경 가능 : 현재 SLAB_NO3 는  DBPT01 형태로 넘어 올 가능성..
						sWgtCenterYaxis3 	= slabUtils.trim(rcvMsg.getFieldString("WGT_CENTER_YAXIS3"));  //"WGT_CENTER_YAXIS"+sNO
						
						
						slabUtils.printLog(logId, "▷ 차량적재위치1[" + sLoadLocCd	+ "] X축 값 : "	+ sWgtCenterXaxis	
											    + "▷ Y축 값 : "	+ sWgtCenterYaxis + "▷ Z축 값 : "	+ sWgtCenterZaxis , "SL");
						slabUtils.printLog(logId, "▷ 차량적재위치2[" + sLoadLocCd2	+ "] Y축(2) 값 : "	+ sWgtCenterYaxis2	, "SL");
						slabUtils.printLog(logId, "▷ 차량적재위치3[" + sLoadLocCd3	+ "] Y축(3) 값 : "	+ sWgtCenterYaxis3	, "SL");
						
						jrParam.setField("REPR_CD_GP"	, "DYD006" );
						jrParam.setField("ITEM"			, "D" );
						jrParam.setField("CD_GP"		, sPtLoadLoc);			//"DBPT01"
						jrParam.setField("DTL_ITEM1"		, sWgtCenterXaxis);
						jrParam.setField("DTL_ITEM2"		, sWgtCenterYaxis);
						jrParam.setField("DTL_ITEM3"		, sWgtCenterZaxis);
						jrParam.setField("DTL_ITEM4"		, "");//String sCAU_CD			= commUtils.trim(rcvMsg.getFieldString("CAU_CD"));			//형상원인코드
						jrParam.setField("DTL_ITEM5"		, "Y");

						jrParam.setField("DTL_ITEM6"		, sLoadLocCd2);			
						jrParam.setField("DTL_ITEM7"		, sWgtCenterYaxis2);
						jrParam.setField("DTL_ITEM8"		, sLoadLocCd3);
						jrParam.setField("DTL_ITEM9"		, sWgtCenterYaxis3);
						
						/*
						UPDATE TB_YD_RULE
						   SET DTL_ITEM1  = NVL(:V_DTL_ITEM1 ,DTL_ITEM1)
						      ,DTL_ITEM2  = NVL(:V_DTL_ITEM2 ,DTL_ITEM2)
						      ,DTL_ITEM3  = NVL(:V_DTL_ITEM3 ,DTL_ITEM3)
						      ,DTL_ITEM4  = NVL(:V_DTL_ITEM4 ,DTL_ITEM4)
						      ,DTL_ITEM5  = NVL(:V_DTL_ITEM5 ,DTL_ITEM5)
						      ,DTL_ITEM6  = NVL(:V_DTL_ITEM6 ,DTL_ITEM6)
						      ,DTL_ITEM7  = NVL(:V_DTL_ITEM7 ,DTL_ITEM7)
						      ,DTL_ITEM8  = NVL(:V_DTL_ITEM8 ,DTL_ITEM8)
						      ,DTL_ITEM9  = NVL(:V_DTL_ITEM9 ,DTL_ITEM9)
						      ,DTL_ITEM10 = NVL(:V_DTL_ITEM10,DTL_ITEM10)
						      ,MODIFIER  = :V_MODIFIER
						      ,MOD_DDTT  = SYSDATE
						 WHERE REPR_CD_GP = :V_REPR_CD_GP
						   AND CD_GP = :V_CD_GP
						   AND ITEM = :V_ITEM
						*/
						commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updYdRuleNvl", logId, methodNm, "TB_YD_RULE DYD006 수정 - 이송상차용 형상 XYZ 값");

						jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd);
						/* --크레인스케줄 기동 사전점검--
						SELECT   YD_CARLD_WRK_BOOK_ID 
						FROM TB_YD_CARSCH 
						WHERE DEL_YN  = 'N'
						AND TRN_EQP_CD  = :V_TRN_EQP_CD 
						AND ROWNUM = 1 
						 */   
						JDTORecordSet jsSchRule = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getYdCarschYdCarLdWrkBookId", logId, methodNm, "예약번호 조회"); 
						String ydCarldWrkBookId = "";
						ydSchCd          = sPtLoadLoc+"UM"; //상차
						if (jsSchRule.size() > 0) {
							ydCarldWrkBookId      = jsSchRule.getRecord(0).getFieldString("YD_CARLD_WRK_BOOK_ID"); 
						}
						if(!"".equals(ydCarldWrkBookId)) {
							//상차 형상 수신된 경우 : 예약작업 확인후  크레인스케줄 기동 처리 
							
							JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
							slabUtils.printLog(logId,  "크레인 스케쥴 호출 : YDYDJ401>>"+ ydSchCd +">"+ ydCarldWrkBookId  , "SL");   
							
							//크레인 스케줄 기동  호출
							jrCrnSchMsg.setField("JMS_TC_CD"			, "YDYDJ401"); 
							jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, slabUtils.getDateTime14()); //JMSTC생성일시	
							jrCrnSchMsg.setField("YD_WBOOK_ID"  		, ydCarldWrkBookId); //작업예약ID
							jrCrnSchMsg.setField("YD_SCH_CD"  			, ydSchCd);  //야드스케쥴코드
							jrCrnSchMsg.setField("YD_EQP_ID"  			, "");  //야드설비ID (예)DACRA1
							jrCrnSchMsg.setField("EJB_CALL_YN"			, "Y");	//EJBCall여부(신 크레인스케줄)
							
							jrRtn = slabUtils.addSndData(jrRtn, jrCrnSchMsg);
						}
					}
				//}
				
				slabUtils.printLog(logId, "▶ 후판슬라브차량 상차형상 완료여부 END!", "SL");
				slabUtils.printLog(logId, methodNm, "S-");
				
		    	return jrRtn;
			}
			//영차인경우 형상정보 수신 전 재료의 형상정보 clear
			else if(iYdEqpWrkSh > 0){
				String ydStkBedNo = "";
				slabUtils.printLog(logId, "▶ [" + sTrnEqpCd + "]하차차량의 형상정보 clear.", "SL");
				slabUtils.printLog(logId, "▶ [" + sTrnEqpCd + "]하차차량의 Bed정보를 확인합니다.", "SL");
				
				//2bed 하차 진입 여부 판정   
				jrParam.setField("TRN_EQP_CD"	, sTrnEqpCd); 
				/*  #com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarFtmvMtlBed2List
				 *  
					SELECT B.YD_CAR_SCH_ID 
					     , B.YD_STK_BED_NO 
					  FROM TB_YD_CARSCH     A
					     , TB_YD_CARFTMVMTL B
					 WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID 
					   AND A.TRN_EQP_CD    = :V_TRN_EQP_CD
					   AND A.DEL_YN = 'N'  
					   AND B.DEL_YN = 'N'
					 GROUP BY B.YD_CAR_SCH_ID,B.YD_STK_BED_NO
					 ORDER BY B.YD_CAR_SCH_ID, B.YD_STK_BED_NO DESC
				 */
				JDTORecordSet jsBed2 = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarFtmvMtlBed2List", logId, methodNm, "이송재료 2Bed여부 조회");
				slabUtils.printLog(logId, "▷ [" + sTrnEqpCd + "]차량의 Bed 갯수 : " + jsBed2.size(), "SL");
				
				for(int kk= 0; kk < jsBed2.size() ; kk++) {
					//--YD_STK_BED_NO
					ydStkBedNo = slabUtils.nvl(jsBed2.getRecord(kk).getFieldString("YD_STK_BED_NO"),"01"); 
					
					//1.운송작업영공구분이 F:영차 인 경우 처리
					slabUtils.printLog(logId, "▶ 1.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 하차대상재료 조회..", "SL");
					
					//운송장비코드로 이송재료 조회
					/* com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getListFrtostlList
					SELECT  A.STL_NO
					       ,A.YD_STK_BED_NO
					       ,SUBSTR(A.YD_STK_LYR_NO,2,2) AS STK_LYR
					       ,A.YD_CAR_SCH_ID
					       ,(SELECT CS.YD_CARUD_WRK_BOOK_ID FROM TB_YD_CARSCH CS WHERE CS.YD_CAR_SCH_ID = A.YD_CAR_SCH_ID) AS YD_CARUD_WRK_BOOK_ID
					       ,(SELECT CS.FRTOMOVE_WORD_NO     FROM TB_YD_CARSCH CS WHERE CS.YD_CAR_SCH_ID = A.YD_CAR_SCH_ID) AS FRTOMOVE_WORD_NO
					       
					       , DECODE(A.YD_CAR_UPP_LOC_CD, '', DECODE(B.YD_CARUD_STOP_LOC,'',B.YD_CARLD_STOP_LOC,B.YD_CARUD_STOP_LOC), A.YD_CAR_UPP_LOC_CD)  as YD_STK_COL_GP
					       , A.YD_STK_BED_NO   AS YD_STK_BED_NO
					       , A.YD_STK_LYR_NO   AS YD_STK_LYR_NO
					  FROM TB_YD_CARFTMVMTL A 
					      ,TB_YD_CARSCH     B 
					 WHERE A.YD_CAR_SCH_ID = (SELECT MAX(A.YD_CAR_SCH_ID)
					                            FROM TB_YD_CARSCH A
					                           WHERE A.TRN_EQP_CD = :V_TRN_EQP_CD  
					                             AND A.DEL_YN = 'N')
					   AND A.YD_CAR_SCH_ID =  B.YD_CAR_SCH_ID
					   AND A.DEL_YN = 'N'
					   AND B.DEL_YN = 'N' 
					   AND A.YD_STK_BED_NO LIKE NVL(:V_YD_STK_BED_NO,'%')
					 ORDER BY A.YD_STK_BED_NO,A.YD_STK_LYR_NO  DESC
					 */
					jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd); 
					jrParam.setField("YD_STK_BED_NO"	, ydStkBedNo); 
					
					JDTORecordSet jsCarMtl = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getListFrtostlList", logId, methodNm, "운송장비코드로 하차대상재료 조회");
					
					slabUtils.printLog(logId, "▷ 1.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 하차대상 재료 갯수 : " + jsCarMtl.size(), "SL");
					
					if (jsCarMtl.size() <= 0) {
						slabUtils.printLog(logId, "▶ 1.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED에 하차대상 재료가 없습니다.. ERROR 처리.", "SL");
						msgId = "하차대상재료 없음";
						throw new Exception(msgId);
					} /*else {
						ydCarSchId = slabUtils.trim(jsCarMtl.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
						slabUtils.printLog(logId, "▷ 하차 차량스케줄 ID : " + ydCarSchId, "SL");
					}*/
					
					//하차대상 갯수 만큼 Looping...
					for(int ii= 0; ii < jsCarMtl.size() ; ii++) {
						slabUtils.printLog(logId, "▶ 1.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 " + (ii+1) + "번 째 재료 형상정보 초기화를 시작합니다.", "SL");
						
						//slabUtils.printLog(logId, "▶ 1. TB_YD_STKLYR(적치단) 테이블의 재료작업상태를 작업중으로 변경합니다.", "SL");
						slabUtils.printLog(logId, "▷ 재료번호	: " + jsCarMtl.getRecord(ii).getFieldString("STL_NO")		, "SL");
						slabUtils.printLog(logId, "▷ 적치열	    : " + sPtLoadLoc + ">" +jsCarMtl.getRecord(ii).getFieldString("YD_STK_COL_GP")	, "SL");
						slabUtils.printLog(logId, "▷ 적치BED	: " + jsCarMtl.getRecord(ii).getFieldString("YD_STK_BED_NO"), "SL");
						slabUtils.printLog(logId, "▷ 적치단    	: " + jsCarMtl.getRecord(ii).getFieldString("YD_STK_LYR_NO"), "SL");
						
						
						slabUtils.printLog(logId, "▶ 1. TB_YD_STOCK(저장품) 테이블의 형상정보를 CLEAR합니다.", "SL");
						// 저장품  형상자료 CLEAR  
						jrParam.setField("LOAD_LOC_CD"		, ""); 
						jrParam.setField("WGT_CENTER_XAXIS"	, ""); 
						jrParam.setField("WGT_CENTER_YAXIS"	, ""); 
						jrParam.setField("WGT_CENTER_ZAXIS"	, ""); 
						jrParam.setField("BENDING_GP"		, ""); 
						jrParam.setField("BENDING_AXIS"		, ""); 
						jrParam.setField("YD_STK_COL_DIR_GP", ""); 
						jrParam.setField("YD_STK_COL_DEG"	, ""); 
						jrParam.setField("CAU_CD"			, "_");
						
						jrParam.setField("STL_NO"			, jsCarMtl.getRecord(ii).getFieldString("STL_NO"));
						/*   
							UPDATE TB_YD_STOCK
							   SET MODIFIER = :V_MODIFIER
							      ,MOD_DDTT = SYSDATE
							      ,LOAD_LOC_CD       = :V_LOAD_LOC_CD
							      ,CAU_CD            = NVL(:V_CAU_CD,'0000')  --형상원인코드
							      ,WGT_CENTER_XAXIS  = :V_WGT_CENTER_XAXIS   --야드크레인X축(mm)
							      ,WGT_CENTER_YAXIS  = :V_WGT_CENTER_YAXIS
							      ,WGT_CENTER_ZAXIS  = :V_WGT_CENTER_ZAXIS
							      ,BENDING_GP        = :V_BENDING_GP         --BENDING 구분(+:상,-:하)
							      ,BENDING_AXIS      = :V_BENDING_AXIS       --BENDING 량(mm)
							      ,YD_STK_COL_DIR_GP = :V_YD_STK_COL_DIR_GP  --야드적치열방향구분(+:좌,-:우)
							      ,YD_STK_COL_DEG    = :V_YD_STK_COL_DEG     --재료적치각도(소재의 뒤틀린 각도)
							      ,YD_RULE_PL_RS_GP = CASE WHEN TO_NUMBER(NVL(:V_BENDING_AXIS,0)) >= (SELECT NVL(DTL_ITEM1,0)
							                                                                                FROM USRYDA.TB_YD_RULE 
							                                                                               WHERE 1=1 --ITEM = 'D' 
							                                                                                 AND REPR_CD_GP ='DYD004'
							                                                                                 AND DEL_YN = 'N') THEN 'Y'
							                               ELSE '' END 
							WHERE STL_NO = :V_STL_NO
						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStockByScanXyz", logId, methodNm, "STOCK에서 기존  SCAN 좌표값 초기 화  (tb_yd_stock 관련 항목 update)");
						slabUtils.printLog(logId, "▶ 1.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 하차재료 형상정보 CLEAR END.", "SL");
					}
				}
		
			} //차량작업재료의 형상정보 clear end
			
			/*******************************************************************
			 * 기 형성되었던 작업예약 clear -- 2022.04.20 수정 / 후판 punchlist
			 **********************************************************************/	
			ydWbookId = "";
			/* com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarWrkList
			SELECT WB.YD_SCH_CD
			     , WB.YD_WBOOK_ID
			  FROM TB_YD_WRKBOOK     WB
			     , (SELECT *
			          FROM TB_YD_CARPOINT
			         WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			       ) CP
			 WHERE WB.DEL_YN      = 'N'
			   AND WB.YD_GP       = CP.YD_GP
			   AND WB.YD_BAY_GP   = CP.YD_BAY_GP   
			   AND WB.TRN_EQP_CD  = CP.TRN_EQP_CD
			   AND WB.YD_WBOOK_ID NOT IN (SELECT CS.YD_WBOOK_ID 
			                                FROM TB_YD_CRNSCH    CS
			                                   , TB_YD_CRNWRKMTL CM
			                               WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
			                                 AND CS.DEL_YN = 'N'
			                                 AND CM.DEL_YN = 'N')
			 ORDER BY WB.YD_WBOOK_ID
 			 */
			JDTORecordSet jsCarWbookId = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarWrkList", logId, methodNm, "작업예약 조회");					
			
			if (jsCarWbookId.size() > 0) {
				for(int ii = 0; ii < jsCarWbookId.size(); ii++) {
					ydWbookId = jsCarWbookId.getRecord(ii).getFieldString("YD_WBOOK_ID"); 
					ydSchCd   = jsCarWbookId.getRecord(ii).getFieldString("YD_SCH_CD"); 
					// 하차작업예약 취소
			    	jrParam.setField("YD_WBOOK_ID"	, ydWbookId	);	// 작업예약ID
			    	jrParam.setField("YD_SCH_CD"	, ydSchCd   );	// 스케쥴코드
			    	
					//--작업예약삭제--
					EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
					jrParam = (JDTORecord)ejbConn.trx("trtWrkBookCncl"	, new Class[] { JDTORecord.class } , new Object[] { jrParam });
					
				}
			}
			
			
			
			/*******************************************************************
			 * 예정정보가 정상적으로 내려 간 경우 (상차이면서 LOT가 편성되어 있지 않는 경우 )
			 **********************************************************************/			
			for(int ii = 1; ii <= iYdEqpWrkSh; ii++) {

				sStlNo   		= slabUtils.trim(rcvMsg.getFieldString("SLAB_NO"          +ii));  //"SLAB_NO"+sNO
				sLoadLocCd 		= slabUtils.trim(rcvMsg.getFieldString("LOAD_LOC_CD"      +ii));  //"LOAD_LOC_CD"+sNO
				sWgtCenterXaxis = slabUtils.trim(rcvMsg.getFieldString("WGT_CENTER_XAXIS" +ii));  //"WGT_CENTER_XAXIS"+sNO
				sWgtCenterYaxis = slabUtils.trim(rcvMsg.getFieldString("WGT_CENTER_YAXIS" +ii));  //"WGT_CENTER_YAXIS"+sNO
				sWgtCenterZaxis = slabUtils.trim(rcvMsg.getFieldString("WGT_CENTER_ZAXIS" +ii));  //"WGT_CENTER_ZAXIS"+sNO
				sBendingGp		= slabUtils.trim(rcvMsg.getFieldString("BENDING_GP"       +ii));  //"BENDING_GP"+sNO
				sBendingAxis	= slabUtils.trim(rcvMsg.getFieldString("BENDING_AXIS"     +ii));  //"BENDING_AXIS"+sNO
				sYdStkColDirGp	= slabUtils.trim(rcvMsg.getFieldString("YD_STK_COL_DIR_GP"+ii));  //"YD_STK_COL_DIR_GP"+sNO
				sYdStkColDeg	= slabUtils.trim(rcvMsg.getFieldString("YD_STK_COL_DEG"   +ii));  //"YD_STK_COL_DEG"+sNO
                /*
                 * 벤딩값 및 뒤틀림각도 값이 기준치 이상이면 스케줄 기동 안함
                 * 처리로직 삽입 예정
                 * 
                 */
				jrParam.setField("BENDING_GP"		, sBendingGp); //벤딩값
				jrParam.setField("CAR_NO"			, sTrnEqpCd );
				jrParam.setField("WGT_CENTER_XAXIS"	, sWgtCenterXaxis); 
				jrParam.setField("WGT_CENTER_YAXIS"	, sWgtCenterYaxis); 
				jrParam.setField("WGT_CENTER_ZAXIS"	, sWgtCenterZaxis); 
				jrParam.setField("STL_NO"			, sStlNo); 
				jrParam.setField("LOAD_LOC_CD"		, sLoadLocCd); 
				jrParam.setField("BENDING_AXIS"		, sBendingAxis); 
				jrParam.setField("YD_STK_COL_DIR_GP", sYdStkColDirGp); 
				jrParam.setField("YD_STK_COL_DEG"	, sYdStkColDeg); //뒤틀림 각도
				jrParam.setField("CAU_CD"			, "");
				/* 형상처리내용 임시저장
				com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStockBendReg02
				
				UPDATE USRYDA.TB_YD_STOCK
				   SET YD_RULE_PL_RS_GP      = CASE WHEN TO_NUMBER(NVL(:V_BENDING_AXIS,0)) >= (SELECT NVL(DTL_ITEM1,0)
				                                                                                FROM USRYDA.TB_YD_RULE 
				                                                                               WHERE ITEM = 'D' 
				                                                                                 AND REPR_CD_GP ='DYD004'
				                                                                                 AND DEL_YN = 'N') THEN 'Y'
				                               ELSE '' END            --야드기준복수결과구분
				      , CAR_NO  		     = :V_CAR_NO              
				      , WGT_CENTER_XAXIS	 = :V_WGT_CENTER_XAXIS    --야드크레인X축(mm)
				      , WGT_CENTER_YAXIS	 = :V_WGT_CENTER_YAXIS    --야드크레인Y축(mm)
				      , WGT_CENTER_ZAXIS	 = :V_WGT_CENTER_ZAXIS    --야드크레인Z축(mm)
				      , BENDING_GP	         = :V_BENDING_GP	      --BENDING 구분(+:상,-:하)
				      , BENDING_AXIS	     = :V_BENDING_AXIS   	  --BENDING 량(mm)
				      , YD_STK_COL_DIR_GP	 = :V_YD_STK_COL_DIR_GP   --야드적치열방향구분(+:좌,-:우)
				      , YD_STK_COL_DEG	     = :V_YD_STK_COL_DEG   	  --재료적치각도(소재의 뒤츨린 각도)
				      , CAU_CD	        	 = :V_CAU_CD 	          --형상원인코드
				      , MODIFIER 		     = :V_MODIFIER
				      , MOD_DDTT 		     = SYSDATE
				WHERE STL_NO  = :V_STL_NO 
				*/
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStockBendReg02", logId, methodNm, "저장품 등록");

				if(ii == 1){
					//추가
					/*
					UPDATE TB_YD_RULE
					   SET DTL_ITEM1  = NVL(:V_DTL_ITEM1 ,DTL_ITEM1)
					      ,DTL_ITEM2  = NVL(:V_DTL_ITEM2 ,DTL_ITEM2)
					      ,DTL_ITEM3  = NVL(:V_DTL_ITEM3 ,DTL_ITEM3)
					      ,DTL_ITEM4  = NVL(:V_DTL_ITEM4 ,DTL_ITEM4)
					      ,DTL_ITEM5  = NVL(:V_DTL_ITEM5 ,DTL_ITEM5)
					      ,DTL_ITEM6  = NVL(:V_DTL_ITEM6 ,DTL_ITEM6)
					      ,DTL_ITEM7  = NVL(:V_DTL_ITEM7 ,DTL_ITEM7)
					      ,DTL_ITEM8  = NVL(:V_DTL_ITEM8 ,DTL_ITEM8)
					      ,DTL_ITEM9  = NVL(:V_DTL_ITEM9 ,DTL_ITEM9)
					      ,DTL_ITEM10 = NVL(:V_DTL_ITEM10,DTL_ITEM10)
					      ,MODIFIER  = :V_MODIFIER
					      ,MOD_DDTT  = SYSDATE
					 WHERE REPR_CD_GP = :V_REPR_CD_GP
					   AND CD_GP = :V_CD_GP
					   AND ITEM = :V_ITEM
					 */
					jrParam.setField("REPR_CD_GP"	, "DYD006" );
					jrParam.setField("ITEM"			, "D" );
					jrParam.setField("CD_GP"		, sPtLoadLoc);			//"DBPT01"
					jrParam.setField("DTL_ITEM1"	, sWgtCenterXaxis);
					jrParam.setField("DTL_ITEM2"	, "");
					jrParam.setField("DTL_ITEM3"	, sWgtCenterZaxis);
					jrParam.setField("DTL_ITEM4"	, "");  //String sCAU_CD			= commUtils.trim(rcvMsg.getFieldString("CAU_CD"));			//형상원인코드
					jrParam.setField("DTL_ITEM5"	, "Y");	//형상 스캔 유무	
					jrParam.setField("DTL_ITEM7"	, "");
					if( sLoadLocCd.length() == 2 ){
						carLocCd = sLoadLocCd.substring(0,1);
						if("1".equals(carLocCd) || "0".equals(carLocCd)){//차 중간:0/앞베드:1
							jrParam.setField("DTL_ITEM2"	, sWgtCenterYaxis);							
						}
					}else{
						throw new Exception("차량적치위치(비정상)[" + sLoadLocCd + "]!!");
					}
				}
				
				if( sLoadLocCd.length() == 2 ){//차 뒷베드:2
					carLocCd = sLoadLocCd.substring(0,1);
					if("2".equals(carLocCd)){
						jrParam.setField("DTL_ITEM7"	, sWgtCenterYaxis);						
					}
				}
				
			}
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updYdRuleNvl", logId, methodNm, "TB_YD_RULE DYD006 수정 - 이송상차용 형상 XYZ 값");
			/**********************************************************
			* 3. 차량형상 완료시 스케줄 기동
			**********************************************************/
			ydWbookId = "";
			/* com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarWrkList
			SELECT WB.YD_SCH_CD
			     , WB.YD_WBOOK_ID
			  FROM TB_YD_WRKBOOK     WB
			     , (SELECT *
			          FROM TB_YD_CARPOINT
			         WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			       ) CP
			 WHERE WB.DEL_YN      = 'N'
			   AND WB.YD_GP       = CP.YD_GP
			   AND WB.YD_BAY_GP   = CP.YD_BAY_GP   
			   AND WB.TRN_EQP_CD  = CP.TRN_EQP_CD
			   AND WB.YD_WBOOK_ID NOT IN (SELECT CS.YD_WBOOK_ID 
			                                FROM TB_YD_CRNSCH    CS
			                                   , TB_YD_CRNWRKMTL CM
			                               WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
			                                 AND CS.DEL_YN = 'N'
			                                 AND CM.DEL_YN = 'N')
			 ORDER BY WB.YD_WBOOK_ID
 			 */
			jsCarWbookId = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarWrkList", logId, methodNm, "작업예약 조회");					
			
			if (jsCarWbookId.size() > 0) {
				ydWbookId = jsCarWbookId.getRecord(0).getFieldString("YD_WBOOK_ID"); 
			} else {
				String ydStkBedNo = "";
				slabUtils.printLog(logId, "▶ [" + sTrnEqpCd + "]하차차량의 작업예약 없는 경우 형상정보를 바탕으로 작업예약 생성.", "SL");
				slabUtils.printLog(logId, "▶ [" + sTrnEqpCd + "]하차차량의 Bed정보를 확인합니다.", "SL");
				
				//2bed 하차 진입 여부 판정   
				jrParam.setField("TRN_EQP_CD"	, sTrnEqpCd); 
				/*  #com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarFtmvMtlBed2List
				 *  
					SELECT B.YD_CAR_SCH_ID 
					     , B.YD_STK_BED_NO 
					  FROM TB_YD_CARSCH     A
					     , TB_YD_CARFTMVMTL B
					 WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID 
					   AND A.TRN_EQP_CD    = :V_TRN_EQP_CD
					   AND A.DEL_YN = 'N'  
					   AND B.DEL_YN = 'N'
					 GROUP BY B.YD_CAR_SCH_ID,B.YD_STK_BED_NO
					 ORDER BY B.YD_CAR_SCH_ID, B.YD_STK_BED_NO DESC
				 */
				JDTORecordSet jsBed2 = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarFtmvMtlBed2List", logId, methodNm, "이송재료 2Bed여부 조회");
				slabUtils.printLog(logId, "▷ [" + sTrnEqpCd + "]차량의 Bed 갯수 : " + jsBed2.size(), "SL");
				//하차차량 형상 수신 시 작업예약 없는 경우 형상정보 기준으로 작업예약 생성.
				if(iYdEqpWrkSh>0){
					for(int kk= 0; kk < jsBed2.size() ; kk++) {
						//--YD_STK_BED_NO
						ydStkBedNo = slabUtils.nvl(jsBed2.getRecord(kk).getFieldString("YD_STK_BED_NO"),"01"); 
						
						slabUtils.printLog(logId, "▶ 1.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 하차대상재료 조회 중..", "SL");
						
						//운송장비코드로 이송재료 조회(형상좌표 있는 재료만 select)
						/* com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getListFrtostlList
							SELECT  A.STL_NO
							       ,A.YD_STK_BED_NO
							       ,SUBSTR(A.YD_STK_LYR_NO,2,2) AS STK_LYR
							       ,A.YD_CAR_SCH_ID
							       ,(SELECT CS.YD_CARUD_WRK_BOOK_ID FROM TB_YD_CARSCH CS WHERE CS.YD_CAR_SCH_ID = A.YD_CAR_SCH_ID) AS YD_CARUD_WRK_BOOK_ID
							       ,(SELECT CS.FRTOMOVE_WORD_NO     FROM TB_YD_CARSCH CS WHERE CS.YD_CAR_SCH_ID = A.YD_CAR_SCH_ID) AS FRTOMOVE_WORD_NO
							       
							       , DECODE(A.YD_CAR_UPP_LOC_CD, '', DECODE(B.YD_CARUD_STOP_LOC,'',B.YD_CARLD_STOP_LOC,B.YD_CARUD_STOP_LOC), A.YD_CAR_UPP_LOC_CD)  as YD_STK_COL_GP
							       , A.YD_STK_BED_NO   AS YD_STK_BED_NO
							       , A.YD_STK_LYR_NO   AS YD_STK_LYR_NO
							  FROM TB_YD_CARFTMVMTL A 
							      ,TB_YD_CARSCH     B
							      ,TB_YD_STOCK      C
							 WHERE A.YD_CAR_SCH_ID = (SELECT MAX(A.YD_CAR_SCH_ID)
							                            FROM TB_YD_CARSCH A
							                           WHERE A.TRN_EQP_CD = :V_TRN_EQP_CD  
							                             AND A.DEL_YN = 'N')
							   AND A.YD_CAR_SCH_ID =  B.YD_CAR_SCH_ID
							   AND A.STL_NO = C.STL_NO
							   AND A.DEL_YN = 'N'
							   AND B.DEL_YN = 'N' 
							   AND C.DEL_YN = 'N'
							   AND A.YD_STK_BED_NO LIKE NVL(:V_YD_STK_BED_NO,'%')
							   AND C.WGT_CENTER_XAXIS IS NOT NULL
							   
							 ORDER BY A.YD_STK_BED_NO,A.YD_STK_LYR_NO  DESC
						 */
						jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd); 
						jrParam.setField("YD_STK_BED_NO"	, ydStkBedNo); 
						
						
						JDTORecordSet jsCarMtl = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getListFrtostlListFrm", logId, methodNm, "운송장비코드로 하차대상재료 조회");
						
						slabUtils.printLog(logId, "▷ 1.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 하차대상 재료 갯수 : " + jsCarMtl.size(), "SL");
						
						if (jsCarMtl.size() <= 0) {
							slabUtils.printLog(logId, "▶ 1.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED에 하차대상 재료가 없습니다.. skip 처리.", "SL");
							msgId = "영차(F) 도착처리 대상재가 존재 안함";
							continue;
							//throw new Exception(msgId);
						} 

						slabUtils.printLog(logId, "▶ 2.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 하차재료 작업예약 생성 START.", "SL");
						slabUtils.printLog(logId, "▶ 2. 크레인스케줄코드를 생성합니다.", "SL");
						
						//스케줄코드 생성  - 이송하차(L)
						ydSchCd = sPtLoadLoc+"LM";
						String ydWrkCrn ="";
						String ydSchPrior ="";
						String sYDYDJ401_CALL_YN ="";
						slabUtils.printLog(logId, "▶ 2. 생성된 크레인스케줄코드의 스케줄기준을 조회합니다.", "SL");
						slabUtils.printLog(logId, "▷  생성된 크레인스케줄코드 : " + ydSchCd, "SL");
						
						//스케줄코드로 스케줄기준Table조회
						jrParam.setField("YD_SCH_CD", ydSchCd);
						/* # com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdSchrule

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
							      
							      -- 2021.11.29 추가 => 차량도착(하차작업) 사용 => 무인작업 중일 때 스케쥴 기동불가(형상없이 기동되면 위험)
							      ,(
							        SELECT DECODE(EQP.YD_EQP_WRK_MODE2,'R','Y'        -- [R] 리모컨
							                                          ,'M','Y','N')   -- [M] 매뉴얼
							          FROM TB_YD_EQP EQP
							         WHERE EQP.YD_EQP_ID = YD_WRK_CRN
							       ) AS YDYDJ401_CALL_YN
							   FROM TB_YD_SCHRULE
							 WHERE YD_SCH_CD =  :V_YD_SCH_CD
						 */   
						JDTORecordSet jsSchRule = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
						
						if (jsSchRule != null && jsSchRule.size() > 0) {
							ydWrkCrn      		= jsSchRule.getRecord(0).getFieldString("YD_WRK_CRN"); 			//야드작업크레인
							ydSchPrior    		= jsSchRule.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); 	//야드스케쥴우선순위
							sYDYDJ401_CALL_YN	= jsSchRule.getRecord(0).getFieldString("YDYDJ401_CALL_YN"); 	//하차작업 스케쥴 기동여부 > Y/N 값만 나옴(NULL 없음) > 무인크레인이면 스케쥴 기동하면 안되기 때문에 'N'으로 나옴
							slabUtils.printLog(logId, "▷  sYDYDJ401_CALL_YN : " + sYDYDJ401_CALL_YN, "SL");
							
						} else {
							slabUtils.printLog(logId, "▶ 2. 생성된 크레인스케줄코드의 스케줄기준이 존재하지 않습니다. ERROR 처리", "SL");
							throw new Exception("후판슬라브 스케줄 코드 이상 : [" + ydSchCd + "]");
						}			
						
						/********************************
						 * 1. 작업예약 생성
						 * 2. TB_YD_STOCK의이송작업지시번호(TRANS_WORD_NO) 등록
					 	/*********************************/
						slabUtils.printLog(logId, "▶ 2.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 하차재료 작업예약을 생성합니다.", "SL");
						slabUtils.printLog(logId, "▷   운송장비코드	: " + sTrnEqpCd	, "SL");
						slabUtils.printLog(logId, "▷   크레인스케줄코드	: " + ydSchCd	, "SL");
						slabUtils.printLog(logId, "▷   야드작업계획크레인	: " + ydWrkCrn	, "SL");
						slabUtils.printLog(logId, "▷   야드구분		: " + sPtLoadLoc.substring(0,1)		, "SL");
						slabUtils.printLog(logId, "▷   목표동		: " + sPtLoadLoc.substring(1,2)	, "SL");
						slabUtils.printLog(logId, "▷   야드스케쥴우선순위	: " + ydSchPrior, "SL");
						slabUtils.printLog(logId, "▷   적치열		: " + sPtLoadLoc, "SL");
						
						jrParam.setField("YD_SCH_ST_GP"				, "A"); 		//야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
						jrParam.setField("YD_WRK_PLAN_CRN"			, ydWrkCrn); 	//야드작업계획크레인
						jrParam.setField("YD_WRK_PLAN_CRN2"			, ""); 			//야드작업계획크레인2
						
						//ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
						
						jrParam.setField("YD_SCH_CD"        , ydSchCd); 	//야드스케쥴코드
						jrParam.setField("YD_AIM_BAY_GP"    , sPtLoadLoc.substring(1,2)  ); 	//야드목표동구분 ydAimBayGp
						jrParam.setField("YD_TO_LOC_GUIDE"  , ""); 			//야드To위치Guide 
						jrParam.setField("YD_WRK_PLAN_TCAR" , ""); 			//야드작업계획대차 ydWrkPlanTcar
						jrParam.setField("YD_WRK_PLAN_CRN"  , "");      	//야드작업크레인 ydWrkCrn
						jrParam.setField("YD_SCH_PRIOR"	    , "");      	//야드크레인작업순위 schPrior
						
						//jrParam.setField("YD_WBOOK_ID"		, ydWbookId);
						jrParam.setField("YD_GP"			, sPtLoadLoc.substring(0,1));
						jrParam.setField("YD_SCH_PRIOR"		, ydSchPrior); //야드스케쥴우선순위
						jrParam.setField("YD_SCH_PROG_STAT"	, "W"); 		//야드스케쥴진행상태(W:스케줄수행대기)
						jrParam.setField("YD_SCH_REQ_GP"	, "C"); 		//야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
						jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd); 	//운송장비코드
						jrParam.setField("YD_CAR_USE_GP"	, "L"); 		//야드차량사용구분 L:구내운송
						jrParam.setField("YD_STK_COL_GP"   	, sPtLoadLoc);	//"DBPT02"
						
						//--작업예약등록--
						EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
						jrParam = (JDTORecord)ejbConn.trx("insMvstkWrkBook"	, new Class[] { JDTORecord.class, JDTORecordSet.class } , new Object[] { jrParam, jsCarMtl });
						
						ydWbookId = slabUtils.trim(jrParam.getFieldString("YD_WBOOK_ID"));
						slabUtils.printLog(logId, "▶ 2.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 하차재료 작업예약이 생성되었습니다.", "SL");
						slabUtils.printLog(logId, "▷ 생성된 작업예약번호 : " + ydWbookId, "SL");
						
						String rtnCd	 = slabUtils.nvl(jrParam.getFieldString("RTN_CD"), "0");
						String rtnMsg	 = slabUtils.nvl(jrParam.getFieldString("RTN_MSG"), "");
						slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "SL");
						slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "SL");
						// ROLLBACK 시 전문 발생
						if ("0".equals(rtnCd)) {
							jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", "작업예약 생성 오류 : "+rtnMsg);
							return jrRtn;
						}
						
					}	//2Bed 하차 처리 (end)
					
					//작업예약 생성 후 작업예약id 정보 select
					/* com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarWrkList
					SELECT WB.YD_SCH_CD
					     , WB.YD_WBOOK_ID
					  FROM TB_YD_WRKBOOK     WB
					     , (SELECT *
					          FROM TB_YD_CARPOINT
					         WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
					       ) CP
					 WHERE WB.DEL_YN      = 'N'
					   AND WB.YD_GP       = CP.YD_GP
					   AND WB.YD_BAY_GP   = CP.YD_BAY_GP   
					   AND WB.TRN_EQP_CD  = CP.TRN_EQP_CD
					   AND WB.YD_WBOOK_ID NOT IN (SELECT CS.YD_WBOOK_ID 
					                                FROM TB_YD_CRNSCH    CS
					                                   , TB_YD_CRNWRKMTL CM
					                               WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					                                 AND CS.DEL_YN = 'N'
					                                 AND CM.DEL_YN = 'N')
					 ORDER BY WB.YD_WBOOK_ID
		 			 */
					jrParam.setField("YD_STK_COL_GP", sPtLoadLoc);
					jsCarWbookId = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarWrkList", logId, methodNm, "작업예약 조회");					
					
					if (jsCarWbookId.size() > 0) {
						ydWbookId = jsCarWbookId.getRecord(0).getFieldString("YD_WBOOK_ID"); 
					}
					else{
						jrRtn.setField("RTN_CD"	, "1");
						jrRtn.setField("RTN_MSG", "기동할 스케쥴 없음.");
						return jrRtn;
					}
					
					
				}
				else{
					jrRtn.setField("RTN_CD"	, "1");
					jrRtn.setField("RTN_MSG", "기동할 스케쥴 없음.");
					return jrRtn;
				}
			}
			
//			JDTORecord jrCrnSchMsg = slabUtils.getParam(logId, methodNm, sModifier);
			
			//크레인 스케줄 기동  호출
//			jrCrnSchMsg.setField("JMS_TC_CD"			, "YDYDJ401"); 
//			jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, slabUtils.getDateTime14()); //JMSTC생성일시	
//			jrCrnSchMsg.setField("YD_WBOOK_ID"  		, ydWbookId); //작업예약ID
//			jrCrnSchMsg.setField("YD_SCH_CD"  			, "");  //야드스케쥴코드
//			jrCrnSchMsg.setField("YD_EQP_ID"  			, "");  //야드설비ID (예)DACRA1
//			jrCrnSchMsg.setField("EJB_CALL_YN"			, "Y");	//EJBCall여부(신 크레인스케줄)
//			
//			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdSchSeEJB", this);
//			JDTORecord jrSchParam = (JDTORecord)ejbConn.trx("rcvYDYDJ401", new Class[] { JDTORecord.class }, new Object[] { jrCrnSchMsg });
//			String rtnCd	 = slabUtils.nvl(jrSchParam.getFieldString("RTN_CD"), "0");
//			String rtnMsg	 = slabUtils.nvl(jrSchParam.getFieldString("RTN_MSG"), "");
//			if(!"1".equals(rtnCd)) {
//				slabUtils.printLog(logId, "======================", "SL"); 
//				slabUtils.printLog(logId, "스케줄기동(비정상)" +rtnMsg, "SL"); 
//				slabUtils.printLog(logId, "======================", "SL"); 
//				throw new Exception("스케줄기동(비정상)[" + rtnMsg + "]!!");
//			} else {
//				jrRtn = slabUtils.addSndData(jrRtn, jrSchParam);
//			}		
/* 2bed 모두 스케쥴 가동 요청 시 아래 코드 주석해제 후 
   com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarWrkList 쿼리에서
   ORDER BY WB.YD_WBOOK_ID -> 'ORDER BY WB.YD_WBOOK_ID DESC' 로 변경 하면 된다.
 */
			for(int ii = 0; ii < jsCarWbookId.size(); ii++) {
				JDTORecord jrCrnSchMsg = slabUtils.getParam(logId, methodNm, sModifier);
				slabUtils.printLog(logId,  "크레인 스케쥴 호출 : YDYDJ401"  , "SL");   
				//크레인 스케줄 기동  호출
				jrCrnSchMsg.setField("JMS_TC_CD"			, "YDYDJ401"); 
				jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, slabUtils.getDateTime14()); //JMSTC생성일시	
				jrCrnSchMsg.setField("YD_WBOOK_ID"  		, jsCarWbookId.getRecord(ii).getFieldString("YD_WBOOK_ID")); //작업예약ID
				jrCrnSchMsg.setField("YD_SCH_CD"  			, "");  //야드스케쥴코드                                : ydSchCd
				jrCrnSchMsg.setField("YD_EQP_ID"  			, "");  //야드설비ID (예)DACRA1    : ydEqpId
				jrCrnSchMsg.setField("EJB_CALL_YN"			, "Y");	//EJBCall여부(신 크레인스케줄)   

				slabUtils.printLog(logId, "예약id["+ii+"]"+ jsCarWbookId.getRecord(ii).getFieldString("YD_WBOOK_ID"), "SL"); 
				// 변경  
				jrRtn = slabUtils.addSndData(jrRtn, jrCrnSchMsg);

				/*
				EJBConnector ejbConn = new EJBConnector("default", "PSlabYdSchSeEJB", this);
				JDTORecord jrSchParam = (JDTORecord)ejbConn.trx("rcvYDYDJ401", new Class[] { JDTORecord.class }, new Object[] { jrCrnSchMsg });
				String rtnCd	 = slabUtils.nvl(jrSchParam.getFieldString("RTN_CD"), "0");
				String rtnMsg	 = slabUtils.nvl(jrSchParam.getFieldString("RTN_MSG"), "");
				if(!"1".equals(rtnCd)) {
					slabUtils.printLog(logId, "======================", "SL"); 
					slabUtils.printLog(logId, "스케줄기동(비정상)" +rtnMsg, "SL"); 
					slabUtils.printLog(logId, "======================", "SL"); 
					throw new Exception("스케줄기동(비정상)[" + rtnMsg + "]!!");
				} else {
					jrRtn = slabUtils.addSndData(jrRtn, jrSchParam);
				}
				*/
				
			}	
			slabUtils.printLog(logId, methodNm, "S-");
			
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "정상처리되었습니다.");
	    	return jrRtn;
		
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 상차도 작업 불가(Y3YDL018)
	 *      PYS   2020.12.10
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL018(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "HSC상차도 작업 불가[PSlabYdL2RcvSeEJB.rcvY3YDL018] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			String sModifier    = rcvMsg.getFieldString("MODIFIER" ); 					//수정자(MODIFIER)
			JDTORecord jrRtn    = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
			JDTORecord jrParam  = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set
			
			slabUtils.printLog(logId,  "logId:"+ logId, "SL");
			slabUtils.printLog(logId, methodNm, "■■■■■■ 상차도 작업 불가[PSlabYdL2RcvSeEJB.rcvY3YDL018] Start. ■■■■■■");
			
			//수신 항목 값
			String msgId		= slabUtils.nvl(slabUtils.getMsgId(rcvMsg),"Y3YDL018"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sPT_LOAD_LOC = rcvMsg.getFieldString("PT_LOAD_LOC");					//상차도위치(6)
			String sUSE_YN		= rcvMsg.getFieldString("USE_YN");						//Y:사용가능, N:사용불가
			
			slabUtils.printLog(logId, methodNm, "■■■■■+ 수신 PARAM 체크 +■■■■■■■■■■■■■■■■■■■■■■■■■"	);
			slabUtils.printLog(logId, methodNm, "■ MSG_ID(전문ID)				:" + msgId			);
			slabUtils.printLog(logId, methodNm, "■ PT_LOAD_LOC(상차도 위치)		:" + sPT_LOAD_LOC	);
			slabUtils.printLog(logId, methodNm, "■ USE_YN(상차도 사용유무)			:" + sUSE_YN		);
			slabUtils.printLog(logId, methodNm, "■ MODIFIER(수정자 Backup Only)	:" + sModifier		);
			slabUtils.printLog(logId, methodNm, "■■■■■- 수신 PARAM 체크 -■■■■■■■■■■■■■■■■■■■■■■■■■"	);
			
			if ("".equals(sModifier)) { sModifier = msgId; }				//수정자(Backup Only)
			
			//수신항목 Check
			if(sPT_LOAD_LOC.length() != 6) {
				slabUtils.printLog(logId, methodNm, "+ 수신항목 에러체크 : 상차도 위치 6자리가 아닌 case");
				throw new Exception("상차도 위치 PT_LOAD_LOC 가 6자리가 아닙니다!! [" + sPT_LOAD_LOC + "]");
			}
			if(!"D".equals(sPT_LOAD_LOC.substring(0,1)) || !"PT".equals(sPT_LOAD_LOC.substring(2,4))) {
				slabUtils.printLog(logId, methodNm, "+ 수신항목 에러체크 : 상차도 위치 PT_LOAD_LOC 가 야드구분이 'D(1,2후판 슬라브야드)'가 아니거나 SECT_GP가 'PT'가 아닌 case");
				throw new Exception("상차도 위치 PT_LOAD_LOC 가 야드구분이 'D'가 아니거나 SECT_GP가 'PT'가 아닙니다!! [" + sPT_LOAD_LOC + "]");
			}
			if(!"Y".equals(sUSE_YN) && !"N".equals(sUSE_YN)) {
				slabUtils.printLog(logId, methodNm, "+ 수신항목 에러체크 : 상차도 사용유무 USE_YN 이 'Y','N' 이 아닌 값이 들어온 case");
				throw new Exception("상차도 사용유무 USE_YN 이 'Y','N' 이 아닌 값이 들어 왔습니다!! [" + sUSE_YN + "]");
			}
			
			jrParam.setField("YD_STK_COL_GP"	, sPT_LOAD_LOC	); //상차도위치
			
			slabUtils.printLog(logId, methodNm, "■ sUSE_YN(상차도 사용유부) :" + sUSE_YN);
			if("Y".equals(sUSE_YN)) {
				jrParam.setField("YD_STK_COL_ACT_STAT"	, "C"); //야드적치열활성상태 C:적치가능
				
				// 차량 포인트 조회
				JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdCarPnt", logId, methodNm, "차량포인트 조회");
				
				if(rsResult.size() <= 0) {
					slabUtils.printLog(logId, methodNm, "+ ERROR 처리: 대상 차량 Point가 없음.");
					throw new Exception("대상 차량 Point 가 리스트에 없습니다!!");
				}
				
				String sTRN_EQP_CD = rsResult.getRecord(0).getFieldString("TRN_EQP_CD");				// 운송장비코드
				String sYD_CAR_PROG_STAT = rsResult.getRecord(0).getFieldString("YD_CAR_PROG_STAT");	// 야드차량진행상태
				
				slabUtils.printLog(logId, methodNm, "■ sTRN_EQP_CD(운송장비코드)				:" + sTRN_EQP_CD		);
				slabUtils.printLog(logId, methodNm, "■ sYD_CAR_PROG_STAT(야드차량진행상태 위치)	:" + sYD_CAR_PROG_STAT	);
				
				if(!"".equals(sTRN_EQP_CD)) {
					if("1".equals(sYD_CAR_PROG_STAT)||"A".equals(sYD_CAR_PROG_STAT)) {			// 차량진행상태가 [1]상차출발, [A]하차출발 일때
						jrParam.setField("YD_STK_COL_ACT_STAT"	, "R"); //야드적치열활성상태 R:예약중
					} else {
						jrParam.setField("YD_STK_COL_ACT_STAT"	, "L"); //야드적치열활성상태 L:사용중
					}
				} 				
			} else {
				jrParam.setField("YD_STK_COL_ACT_STAT"	, "N"); //야드적치열활성상태 N:사용불가
			}
			
			/**********************************************************
			 * 적치열 활성상태 UPDATE.
			 **********************************************************/
			slabUtils.printLog(logId, methodNm, "■■■■■+ 적치열 활성상태 UPDATE START +■■■■■■■■■■■■■■■■■■■■■■■■■"	);
			slabUtils.printLog(logId, methodNm, "■ MODIFIER(수정자)					: " + jrParam.getFieldString("MODIFIER")			);
			slabUtils.printLog(logId, methodNm, "■ YD_STK_COL_ACT_STAT(적치열 활성상태)	: " + jrParam.getFieldString("YD_STK_COL_ACT_STAT")	);
			slabUtils.printLog(logId, methodNm, "■ YD_STK_COL_GP(상차도 위치)			: " + jrParam.getFieldString("YD_STK_COL_GP")		);
			/*
			-- com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updYdCarPointActStat
			-- 차량포인트 적치열활성상태 UPDATE
			UPDATE  TB_YD_CARPOINT
			   SET  MODIFIER = :V_MODIFIER
			       ,MOD_DDTT = SYSDATE
			       ,YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
			 WHERE  YD_STK_COL_GP = :V_YD_STK_COL_GP
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updYdCarPointActStat", logId, methodNm, "차량포인트 적치열활성상태 UPDATE ");
			slabUtils.printLog(logId, methodNm, "■■■■■+ 적치열 활성상태 UPDATE END   +■■■■■■■■■■■■■■■■■■■■■■■■■"	);
			
			
			/**********************************************************
			 * 저장위치제원정보 송신 (YDY3L001)
			 **********************************************************/
			slabUtils.printLog(logId, methodNm, "■■■■■+ 저장위치제원정보 송신 (YDY3L001)PARAM SET START +■■■■■■■■■"	);
			jrParam.setField("YD_INFO_SYNC_CD"		, "4"           	); // 야드정보동기화코드 [1]동, [2]SPAN, [3]:열, [4]: Bed
			jrParam.setField("MSG_GP"				, "I"           	); // 전문구분
			jrParam.setField("YD_STK_COL_GP"    	, sPT_LOAD_LOC		); // 상차도위치
			jrParam.setField("YD_STK_BED_NO"    	, "01"				);
			jrParam.setField("YD_EQP_WRK_STAT_GUBUN" , "");
			jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY3L001", jrParam));
			slabUtils.printLog(logId, methodNm, "■■■■■+ 저장위치제원정보 송신 (YDY3L001)PARAM SET END   +■■■■■■■■■"	);
			
			slabUtils.printLog(logId, methodNm, "■■■■■ 상차도 작업 불가[PSlabYdL2RcvSeEJB.rcvY3YDL018] End. ■■■■");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 차량작업 예정정보 요구(Y3YDL019)				
	 *      
	 *      YDY3L008 번 리턴
	 *      박영수   2020.11.23    									
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	

	public JDTORecord rcvY3YDL019(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "차량작업예정정보요구[PSlabYdL2RcvSeEJB.rcvY3YDL019] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			
			slabUtils.printLog(logId, methodNm, "S+");
			String ydCarSchId 		= "";
			slabUtils.printParam(logId, rcvMsg, "S+");
			
			//수신 항목 값ydEqpId
			String msgId        = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydLoadLoc   	= slabUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"));     	//상차도 위치
			String modifier     = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); 		//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			String CarNo = "";
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydLoadLoc.length() < 6) {
				slabUtils.printLog(logId, methodNm + "상차도 길이 < 6 :" +ydLoadLoc , "SL");
				throw new Exception("상차도 위치 길이가 6자리보다 작습니다. [" + ydLoadLoc + "]");
			} 

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"                 , modifier);	
			jrParam.setField("PT_LOAD_LOC"      		, ydLoadLoc);  					
			jrParam.setField("YD_STK_COL_GP"      		, ydLoadLoc);   
			jrParam.setField("SEARCH_FLAG"      		, "1");  //1:상차도, 2:차량스케쥴 ID					
			jrParam.setField("YD_GP"      		        , "D");  //야드구분
			
			/* 상차도 위치에 존재하는  스케줄id
			SELECT YD_CAR_SCH_ID 
			FROM TB_YD_CARSCH 
			WHERE DEL_YN = 'N' 
			AND  TRN_EQP_CD = (SELECT TRN_EQP_CD
			                     FROM TB_YD_CARPOINT 
			                    WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP 
			                      AND DEL_YN = 'N')
			*/
			jrParam.setField("YD_STK_COL_GP"      		, ydLoadLoc);   
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJ.getListtrnEqpsch", logId, methodNm, "차량스케줄 ,작업예약ID 조회");
			if (jsCarSch.size() > 0) {
				ydCarSchId = slabUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
			} else {	
				slabUtils.printLog(logId, methodNm + "해당위치 차량작업정보 없음  :" +ydLoadLoc , "SL");
				//throw new Exception("해당위치 차량작업정보 없음. [" + ydLoadLoc + "]");
			}
			
			
			/**********************************************************
			* 차량작업 예정정보 송신 (Y3YDL008)
			**********************************************************/
			//jrRtn = slabUtils.addSndData(jrRtn, slabComm.procCarPlanInfo_Slab(jrParam));
			jrParam		= slabUtils.getParam(logId, methodNm, modifier);
			jrParam.setField("SEARCH_FLAG" 			, "1"			); //1:상차도, 2:차량스케쥴 ID	
			jrParam.setField("PT_LOAD_LOC" 			, ydLoadLoc		); //상차도 위치
			jrParam.setField("YD_CAR_SCH_ID"		, ydCarSchId	); //야드차량스케쥴ID  (추가사항)
			
			slabUtils.printLog(logId, "*상차도 위치:"+ ydLoadLoc+ " 야드차량스케쥴ID" + ydCarSchId, "SL");    
			jrRtn = slabUtils.addSndData(jrRtn, slabComm.procCarPlanInfo_Slab(jrParam));  

			/**********************************************************
			* 저장위치제원정보 송신 (YDY3L001)
			**********************************************************/
			jrParam.setField("YD_INFO_SYNC_CD"	, "4"           ); //야드정보동기화코드
			jrParam.setField("MSG_GP"			, "I"           ); //전문구분
			jrParam.setField("YD_STK_COL_GP"    	, ydLoadLoc		);
			jrParam.setField("YD_STK_BED_NO"    	, ""			);
			jrParam.setField("YD_EQP_WRK_STAT_GUBUN" , "");
			slabUtils.printLog(logId, "*상차도 위치(YDY3L001):"+ ydLoadLoc, "SL");   
			jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY3L001", jrParam));		
			
			/**********************************************************
			* 저장품제원(YDY3L002) 
			**********************************************************/
			jrParam.setField("YD_INFO_SYNC_CD"	, "4"			); //야드정보동기화코드
			jrParam.setField("MSG_GP"			, "I"			); //전문구분
			jrParam.setField("YD_STK_COL_GP"  	, ydLoadLoc		); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO"  	, ""			); //야드적치Bed번호
			jrParam.setField("YD_GP"          	, "D"			); //야드구분
			jrParam.setField("STL_NO"       	, ""			); //재료번호
			
			slabUtils.printLog(logId, "*상차도 위치(YDY3L002):"+ ydLoadLoc, "SL");
			jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY3L002", jrParam));
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 
	
	
	 /**
	 *      [A] 오퍼레이션명 : 슬라브 야드 크레인 위치정보(Y3YDL020)
	 *      염용선 2021-01-15
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY3YDL020(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "슬라브 야드 크레인 위치정보[PSlabYdL2RcvSeEJB.rcvY3YDL020] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			slabUtils.printLog(logId, mthdNm, "S+");
			slabUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값
			String msgId     = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sModifier = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;
			
			JDTORecord jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
			
			for (int Loop_i = 1; Loop_i <= 24; Loop_i++) {
				
				String ydEqpId = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" + Loop_i ));
				
				if ("".equals(ydEqpId)) {
					break;
				}

				jrParam.setField("YD_EQP_ID"     	, ydEqpId );  //YD_EQP_ID 
				jrParam.setField("CRN_WRK_PROC_STAT", slabUtils.trim(rcvMsg.getFieldString("CRN_WRK_PROC_STAT"+Loop_i )) );  //CRN_WRK_PROC_STAT
				jrParam.setField("CURR_XAXIS"     	, slabUtils.trim(rcvMsg.getFieldString("CURR_XAXIS"+Loop_i )) );  //CURR_XAXIS
				jrParam.setField("FROM_XAXIS"     	, slabUtils.trim(rcvMsg.getFieldString("FROM_XAXIS"+Loop_i )) );  //FROM_XAXIS 
				jrParam.setField("TO_XAXIS"     	, slabUtils.trim(rcvMsg.getFieldString("TO_XAXIS"+Loop_i )) );  //TO_XAXIS
				
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
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdCrnLoc", logId, mthdNm, "슬라브 야드 크레인 위치 정보 등록");
			}
			
			slabUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : scarfing장 작업유무(Y3YDL021)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY3YDL021(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "scarfing장 작업유무[PSlabYdL2RcvSeEJB.rcvY3YDL021] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			
			slabUtils.printLog(logId, mthdNm, "S+");
			
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인주행금지구간(Y3YDL030)
	 *      2021-03-17 염용선
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
    public JDTORecord rcvY3YDL030(JDTORecord rcvMsg) throws DAOException {
    	String methodNm = "Y3크레인주행금지구간[PSlabYdL2RcvSeEJB.rcvY3YDL030] < " + rcvMsg.getResultMsg();
    	String logId = rcvMsg.getResultCode();
    	JDTORecord resMsg 	= JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn 		= true;
    	
    	try{
    		slabUtils.printLog(logId, methodNm, "S+");
			
			//JDTORecord jrRtn = null;
    		
    		//Data수신 항목 값
			String msgId      		   = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String StartEndGp		   = slabUtils.trim(rcvMsg.getFieldString("START_END_GP")); //시작종료구분
			String ydGp 			   = slabUtils.trim(rcvMsg.getFieldString("YD_GP"				   )); //야드구분
			String bayGp 			   = slabUtils.trim(rcvMsg.getFieldString("BAY_GP"                 )); //야드동구분
			String TravlProhFromloc    = slabUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_FROMLOC"     )); //야드주행금지FROM위치
			String TravlProhToloc 	   = slabUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_TOLOC"       )); //야드주행금지TO위치
			String TravlProhFromxaxis  = slabUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_FROMXAXIS"   )); //야드주행금지FROM위치X축
			String TravlProhToxaxis    = slabUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_TOXAXIS"     )); //야드주행금지TO위치X축
			
			//String ymCraneTravlProhSeq = slabUtils.trim(rcvMsg.getFieldString("YM_CRANE_TRAVL_PROH_SEQ"				   )); 
			//String delyn 			   = slabUtils.trim(rcvMsg.getFieldString("DEL_YN"				   )); //삭제처리유무
			//String register 		   = slabUtils.trim(rcvMsg.getFieldString("REGISTER"			   )); //등록자
			String modifier 		   = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"               )); //수정자
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
			* 2. 크레인주행금지구간(Y3YDL030) SEQ SELECT
			**********************************************************/
			jrParam.setField("YD_GP", ydGp                             );
			jrParam.setField("BAY_GP", bayGp                           );
			jrParam.setField("TRAVL_PROH_FROMLOC", TravlProhFromloc    );
			jrParam.setField("TRAVL_PROH_TOLOC", TravlProhToloc        );
			
			/* 
			  SELECT YD_CRANE_TRAVL_PROH_SEQ 
				  FROM USRYDA.TB_YD_CRANE_TRAVL_PROH
				 WHERE DEL_YN = 'N'
				     AND YD_GP = :V_YD_GP
				     AND BAY_GP =  :V_BAY_GP
				     AND TRAVL_PROH_FROMLOC =  :V_TRAVL_PROH_FROMLOC
				     AND TRAVL_PROH_TOLOC =  :V_TRAVL_PROH_TOLOC
		   */
			
			JDTORecordSet jrList = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getbSlabCRANETRAVLPROHList", logId, methodNm, "크레인주행금지구간 조회");	
			
			String sSeq = "";
			
			if(jrList.size() > 0){
				sSeq = jrList.getRecord(0).getFieldString("YD_CRANE_TRAVL_PROH_SEQ");
			}
			
			jrParam.setField("YD_CRANE_TRAVL_PROH_SEQ", sSeq    );
			
			jrParam.setField("TRAVL_PROH_FROMXAXIS", TravlProhFromxaxis);
			jrParam.setField("TRAVL_PROH_TOXAXIS", TravlProhToxaxis    );
			
			if("S".equals(StartEndGp)){
				jrParam.setField("DEL_YN", "N"    );
			}else if("E".equals(StartEndGp)){
				jrParam.setField("DEL_YN", "Y"    );
			}
			jrParam.setField("REGISTER", "Y3YDL030");
			jrParam.setField("MODIFIER", "Y3YDL030");
				
			/*
			 * MERGE INTO TB_YD_CRANE_TRAVL_PROH USING DUAL ON ( 
                         YD_CRANE_TRAVL_PROH_SEQ = :V_YD_CRANE_TRAVL_PROH_SEQ
		        AND YD_GP = :V_YD_GP
		        AND BAY_GP  = :V_BAY_GP
			    AND TRAVL_PROH_FROMXAXIS  = :V_TRAVL_PROH_FROMXAXIS
				AND TRAVL_PROH_TOXAXIS = :V_TRAVL_PROH_TOXAXIS)
			  WHEN MATCHED THEN
		    UPDATE SET DEL_YN = :V_DEL_YN,
		                        MODIFIER = :V_MODIFIER,
			         MOD_DDTT = SYSDATE                 
			  WHEN NOT MATCHED THEN
			INSERT(YD_CRANE_TRAVL_PROH_SEQ,
			       YD_GP,
			       BAY_GP,
			       TRAVL_PROH_FROMLOC,
			       TRAVL_PROH_TOLOC,
			       TRAVL_PROH_FROMXAXIS,
			       TRAVL_PROH_TOXAXIS,
			       DEL_YN,
			       REGISTER,
			       REG_DDTT)
			VALUES((SELECT nvl(MAX(to_number(YD_CRANE_TRAVL_PROH_SEQ)),0)+1 FROM TB_YD_CRANE_TRAVL_PROH),
				   :V_YD_GP,
				   :V_BAY_GP,
				   :V_TRAVL_PROH_FROMLOC,
				   :V_TRAVL_PROH_TOLOC,
				   :V_TRAVL_PROH_FROMXAXIS,
				   :V_TRAVL_PROH_TOXAXIS,
				    :V_DEL_YN,
				   :V_REGISTER,
				   SYSDATE)
		   */
				
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.updbSlabCRANETRAVLPROH", logId, methodNm, "크레인주행금지구간 수정 및 등록");

			/**********************************************************
			* 3. 크레인작업실적응답 전문 전송(YDY3L005)
			**********************************************************/
			slabUtils.printLog(logId,"확인:"+resYn, "SL");
			if (resYn) {
				slabUtils.printLog(logId,"확인:"+resYn, "SL");
				resMsg.setResultCode(logId);				//Log ID
				resMsg.setField("YD_L3_HD_RS_CD", "0000");	//야드L3처리결과코드(정상)
				resMsg.setField("BAY_GP", bayGp);
				
				resMsg.setField("A", "DACRA1"); //A동 대표크레인
				resMsg.setField("B", "DBCRB1"); //B동 대표크레인
				jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY3L005(resMsg));
			}
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					resMsg.setResultCode(logId);
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(slabUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "PSlabYdCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] {slabComm.getYDY3L005(resMsg)});
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
    }
	
}
