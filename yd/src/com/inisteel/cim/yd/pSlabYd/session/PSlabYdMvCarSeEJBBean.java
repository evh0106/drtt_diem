/**
 * @(#)PSlabYdMvCarSeEJBBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2020/12/10
 *
 * @description      Slab야드 Car_move 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              	요청자       	수정자      	내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2020/12/10   	정종균     	 PYS      	최초 등록                       [참고]PSlabYdMvCarSeEJBSBean
 */
package com.inisteel.cim.yd.pSlabYd.session;

import java.util.ArrayList;
import java.util.Vector;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.rule.GetBreRule1;
import com.inisteel.cim.yd.common.rule.GetBreRule2;
import com.inisteel.cim.yd.pSlabCommon.util.PSlabYdUtils;
import com.inisteel.cim.yd.pSlabYd.dao.PSlabYdCommDAO;
import com.inisteel.cim.yd.pSlabCommon.util.PSlabYdConstant;

/**
 *      [A] 클래스명 : Slab야드 _Move_Car 처리
 *
 * @ejb.bean name="PSlabYdMvCarSeEJB" jndi-name="PSlabYdMvCarSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic. 
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class PSlabYdMvCarSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private PSlabYdUtils 	slabUtils 	= new PSlabYdUtils();
	private PSlabYdComm   	slabComm 	= new PSlabYdComm();
	private PSlabYdCommDAO 	commDao 	= new PSlabYdCommDAO();
	
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/***************************************************************************
	 * 후판 슬라브> 차량 관리  
	 **************************************************************************/
	/*  ydEjbCon.trx("PSlabYdCommEJB", "rcvInterface", inRecord);			/CarMvHdSeEJBBean (MAIN)
		TSYDJ002^	소재차량도착Point요구		CarMvHdFaEJB	rcvMatlCarArrPntReq /YmCommCarMvSeEJB	rcvTSYDJ002	/CCoilCarMvSeEJB	rcvTSYDJ002(2열연)
		TSYDJ003^	소재차량도착				CarMvHdFaEJB	rcvMatlCarArr       /YmCommCarMvSeEJB	rcvTSYDJ003 /CCoilCarMvSeEJB	rcvTSYDJ003
		TSYDJ004^	소재차량출발				CarMvHdFaEJB	rcvMatlCarLev       /YmCommCarMvSeEJB	rcvTSYDJ004 /CCoilCarMvSeEJB	rcvTSYDJ004
		YDTSJ007	소재차량상차개시			YDTSJ008	소재차량상차완료							
		YDTSJ009	소재차량하차개시			YDTSJ010	소재차량하차완료
		YDTSJ011	소재차량Point지시		YDTSJ012	소재차량Point개폐
		YMYMJ662	출하 차량 입동지시 요구		YmCommCarMvSeEJB	rcvYMYMJ662 (대상외)
	 */
	
	/**
	 * [A] 오퍼레이션명 : 소재차량출발(TSYDJ004)   					(참고)CCoilCarMvSeEJB: procMatlCarLev 
	 * 2021.02.02 pys
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvTSYDJ004(JDTORecord rcvMsg) throws DAOException  {
		String mthdNm 	= "후판슬라브소재차량출발[PSlabYdMvCarSeEJB.rcvTSYDJ004] < " + rcvMsg.getResultMsg();
		String logId  	= rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, mthdNm, "S+");
			slabUtils.printParam(logId + "후판슬라브야드 소재차량출발 수신 ", rcvMsg);
			slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발(TSYDJ004) 전문 수신처리 START!", "SL");
			
	    	//수신항목 변수 저장
			String msgId                = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sTrnEqpCd      		= slabUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"             )); //운송장비코드
			String sSposWlocCd      	= slabUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"           )); //발지개소코드
			String sSposYdPntCd     	= slabUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"         )); //발지야드포인트코드
			String sArrWlocCd      		= slabUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD"            )); //착지개소코드
			String sArrYdPntCd      	= slabUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD"          )); //착지야드포인트코드
			String sTrnWrkFullvoidGp	= slabUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP"    )); //운송작업영공구분 E:공차 F:영차
			String sTrnEqpStkCapa   	= slabUtils.nvl (rcvMsg.getFieldString("TRN_EQP_STK_CAPA"), "0"	);  //운송장비적재능력
			String ydWoCnclYn           = slabUtils.trim(rcvMsg.getFieldString("YD_WO_CNCL_YN"          )); //야드지시 취소여부
			String sModifier 			= slabUtils.trim(rcvMsg.getFieldString("MODIFIER"               )); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }	
			if ("0".equals(sTrnEqpStkCapa)) {
				sTrnEqpStkCapa = PSlabYdConstant.YD_CAR_BED_WT_MAX_DEFAULT;
			}
			
			slabUtils.printLog(logId, "▷ 출발차량 운송장비코드(sTrnEqpCd)			: "+ sTrnEqpCd			, "SL");
			slabUtils.printLog(logId, "▷ 출발차량 영공구분(sTrnWrkFullvoidGp)	: "+ sTrnWrkFullvoidGp	, "SL");

			boolean bIsCarSchYN     	= false;  //차량스케쥴 존재 여부
			String sMsg            	 	= "";
			String ydCarStopLoc         = ""; 
			//String ydPntCd	        = "";
			String queryId 				= "";
			String sCurrDate        	= slabUtils.getDateTime14();
			
			JDTORecord jrParam      	= slabUtils.getParam(logId, mthdNm, sModifier);
			JDTORecord jrRtn 			= slabUtils.getParam(logId, mthdNm, sModifier);;
			/****************************************
			 * 야드지시취소 
			 ****************************************/
			slabUtils.printLog(logId, "1. 야드지시취소", "SL");
			slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발-[" + sTrnEqpCd + "] 차량의 출발취소 전문인지 확인합니다.", "SL");
			slabUtils.printLog(logId, "▷ 출발지시취소여부(ydWoCnclYn) : "+ ydWoCnclYn			, "SL");
			if ("Y".equals(ydWoCnclYn)) {
				slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발-[" + sTrnEqpCd + "] 차량의 출발취소작업을 진행합니다.", "SL");
				
				JDTORecord jrCncl = this.procInitCarinfo(rcvMsg);	
				
				jrRtn = slabUtils.addSndData(jrRtn, jrCncl);
				
				return jrRtn;
				
			} else {
				slabUtils.printLog(logId, "▷ 출발지시취소여부확인(ydWoCnclYn) : "+ ydWoCnclYn + "(대상 아님)"	, "SL");
			}
	    	/***************************************
	    	 * 운송장비코드로 차량스케줄 조회
	    	 ***************************************/
			slabUtils.printLog(logId, "2. 차량스케줄 조회", "SL");
			slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발-[" + sTrnEqpCd + "] 차량의 밸리데이션 체크를 진행합니다.", "SL");
			slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발-[" + sTrnEqpCd + "] 차량의 밸리데이션 체크를 위한 차량 스케줄 정보를 조회합니다.", "SL");
			jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd);
			/* com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdCarschByTrnEqpCd  
				SELECT *
				  FROM (
				        SELECT    YD_CAR_SCH_ID
				                , DEL_YN
				                , YD_EQP_ID
				                , YD_CAR_USE_GP
				                , CAR_NO
				                , TRN_EQP_CD
				                , CAR_KIND
				                , TRANS_EQUIPMENT_TYPE
				                , YD_EQP_WRK_STAT
				                , YD_WRK_PROG_STAT
				                , YD_EQP_WRK_SH
				                , YD_EQP_WRK_WT
				                , YD_STK_BED_TP
				                , SPOS_WLOC_CD
				                , ARR_WLOC_CD
				                , YD_CARLD_LEV_LOC
				                , YD_CARLD_LEV_DT
				                , YD_CARLD_PNT_WO_DT
				                , YD_PNT_CD1
				                , YD_PNT_CD2
				                , YD_CARLD_WRK_BOOK_ID
				                , YD_CARLD_SCH_REQ_GP
				                , YD_CARLD_STOP_LOC
				                , YD_CARLD_ARR_DT
				                , YD_CARLD_ST_DT
				                , YD_CARLD_CMPL_DT
				                , YD_CARLD_WRK_ACT_GP
				                , YD_CARLD_CHK_DT
				                , YD_CARUD_LEV_DT
				                , YD_CARUD_PNT_WO_DT
				                , NVL(YD_PNT_CD3, '0000') AS YD_PNT_CD3
				                , YD_PNT_CD4
				                , YD_CARUD_WRK_BOOK_ID
				                , YD_CARUD_STOP_LOC
				                , YD_CARUD_SCH_REQ_GP
				                , YD_CARUD_ARR_DT
				                , YD_CARUD_CHK_DT
				                , YD_CARUD_ST_DT
				                , YD_CARUD_CMPL_DT
				                , YD_CARUD_WRK_ACT_GP
				                , YD_TRN_WRK_DELY_CD
				                , CARD_NO
				                , YD_CAR_PROG_STAT
				                , FRTOMOVE_PLANT_GP
				                , PROC_TO
				                , RENTPROC_CD
				                , YD_FRTOMOVE_YD_GP
				                , YD_FRTOMOVE_BAY_GP
				                , URGENT_FRTOMOVE_WORD_GP
				                , DEST_TEL_NO
				                , YD_DLVRDD_RULE_DD
				                , SHIPASSIGN_WORD_DATE
				                , SHIPASSIGN_WORD_SEQNO
				                , SHIP_CD
				                , SHIP_NAME
				                , RSHP_HOLD_NO
				                , BERTH_NO
				                , SAILNO
				                , YD_CAR_WRK_GP
				                , TRANS_ORD_DATE
				                , TRANS_ORD_SEQNO
				                , YD_BAYIN_WO_SEQ
				                , YD_CAR_RCPT_CHK_YN
				                , YD_CAR_ISSUE_CHK_YN
				                , YD_CAR_RCPT_CHECKER
				                , YD_CAR_ISSUE_CHECKER
				                , IF_SEQ_NO
				                , TEL_NO
				                , CMBN_CARLD_YN
				                , WAIT_ARR_DDTT
				                , WAIT_ARR_GP
				                , YD_MSG_NM
				                , FRTOMOVE_WORD_NO
				                , DRIVER_NAME
				                , DECODE(A.YD_CARLD_STOP_LOC,'',A.YD_CARUD_STOP_LOC,A.YD_CARLD_STOP_LOC) AS YD_CAR_STOP_LOC
				          FROM TB_YD_CARSCH A
				         WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				           AND DEL_YN     ='N'
				         ORDER BY YD_CAR_SCH_ID DESC
				                , YD_CARUD_CMPL_DT DESC
				       ) A
				 WHERE ROWNUM <= 1
				 
			 */
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdCarschByTrnEqpCd";
			JDTORecordSet jsCarSch = commDao.select(jrParam, queryId, logId, mthdNm, "운송장비코드로 차량스케줄 조회");
			
			slabUtils.printLog(logId, "▷ 차량스케줄 조회 건수 : "+ jsCarSch.size(), "SL");
			if (jsCarSch.size() > 1) {
				slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발-[" + sTrnEqpCd + "] 차량의 차량스케줄이 여러건이 존재합니다", "SL");
				throw new DAOException("스케줄 조회 시 오류발생 - 운송장비코드로 차량스케줄이 여러건["+jsCarSch.size()+"]이 존재합니다");
			}
			
 
			/********************************************
			 * 차량스케줄이 존재하는 경우 업무 로직 진행
			 ********************************************/
			String ydCarSchId        = "";
			String ydCarProgStat     = "";
			String sCarSchSposWlocCd = "";
			String ydCarldWrkBookId  = "";
			
			if (jsCarSch.size() == 1) {
				slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발-3. 차량스케줄이 존재하는 경우", "SL");
				bIsCarSchYN = true;
				
				// 차량스케줄 정보 Set
				ydCarSchId       = slabUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID"       )); //차량스케줄ID
				ydCarProgStat    = slabUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT"    )); //차량진행상태
				sCarSchSposWlocCd= slabUtils.trim(jsCarSch.getRecord(0).getFieldString("SPOS_WLOC_CD"        )); //발지개소코드
				ydCarldWrkBookId = slabUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CARLD_WRK_BOOK_ID")); //야드상차작업예약ID
				ydCarStopLoc     = slabUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_STOP_LOC"	 )); //상하차pOINT
				
				slabUtils.printLog(logId, "▷ 차량진행상태    : " + ydCarProgStat    , "SL");
				slabUtils.printLog(logId, "▷ 상하차 여부 [E]상차[F]하차 : " + sTrnWrkFullvoidGp, "SL");
				
				if ("1".equals(ydCarProgStat) && "E".equals(sTrnWrkFullvoidGp)) {  //상차출발이고 공차인 경우
					slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발-3.1 상차출발인 경우 발지개소와 착지개소가 같은 지 체크합니다.", "SL");
					/**************************************************
		    		 * 공차작업
		    		 **************************************************/
					slabUtils.printLog(logId, "▷ sArrWlocCd[착지개소]        :"+sArrWlocCd        , "SL");
					slabUtils.printLog(logId, "▷ sCarSchSposWlocCd[발지개소] :"+sCarSchSposWlocCd , "SL");
					// 중복 상차 출발 전문 수신 case 
					if (sArrWlocCd.equals(sCarSchSposWlocCd)) {
						slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발-3.1.1 착지와 발지가 동일합니다.", "SL");
						
						if (sSposYdPntCd.equals(PSlabYdConstant.YD_WAIT_PNT_CD) ) { //1Z99 (대기장포인트코드)
							slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발-3.1.1.1 발지포인트 1Z99(대기장)입니다.", "SL");
							//------------------------------------------------------------------------------------------------------------
							//	전문의 착지개소코드와 차량스케줄의 발지개소코드가 같고 전문의 발지개소POINT가 대기장[1Z99]에서 공차출발 시는 
							//	중복 상차출발이므로 업무종료
							//	==> 차량정지POINT요구 지시 모듈 호출 후 업무종료
							//------------------------------------------------------------------------------------------------------------
							
							/********************************************
							 * 소재차량도착Point요구 모듈 호출 TSYDJ002
							 ********************************************/
							JDTORecord inParam = slabUtils.getParam(logId, mthdNm, sModifier);
							inParam.setField("TRN_EQP_CD"			, sTrnEqpCd                );	
							inParam.setField("WLOC_CD"				, sCarSchSposWlocCd        );			
							inParam.setField("TRN_WRK_FULLVOID_GP"	, sTrnWrkFullvoidGp        );			
							inParam.setField("PNT_DMD_DT"			, slabUtils.getDateTime14());			
							
							slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발-대기장에서 공차(상차) 차량 출발 시 소재차량도착Point요구(TSYDJ002) I/F 호출 전문 Set", "SL");
							JDTORecord jrYdMsg = this.rcvTSYDJ002(inParam);
							jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);		//전문 확인 

							sMsg = "대기장[1Z99]에서 공차출발 시 소재차량도착Point요구 모듈 호출 완료";
							slabUtils.printLog(logId, sMsg, "S-");
							return jrRtn;
							
							//------------------------------------------------------------------------------------------------------------
						} else {
							// IF 수신시 착지 하고 차량스케쥴 발지가 동일 한 경우  -->					( 업무종료 )
							slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발-착지개소와 발지개소가 같으므로 프로그램을 종료합니다.", "SL");
							sMsg = "(전문)착지와 차량스케줄 발지가 동일한  경우. 종료" + sArrWlocCd;
							slabUtils.printLog(logId, sMsg, "S-");
							throw new Exception(sMsg);
						}
					} else {
						/***********************************************************
						 * 차량스케줄 삭제처리, 작업예약삭제, 준비스케줄복구 후 다음 로직 처리
						 ***********************************************************/
						jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
						jrParam.setField("YD_WBOOK_ID"	, ydCarldWrkBookId);
						slabUtils.printLog(logId, "▶ 상차지 정보 CLEAR(차량스케줄 삭제처리, 작업예약삭제, 준비스케줄복구) 시작", "SL");
						
						jrRtn = slabUtils.addSndData(jrRtn, this.procMatlCarCancel(jrParam));
						
						slabUtils.printLog(logId, "▶ 상차지 정보 CLEAR(차량스케줄 삭제처리, 작업예약삭제, 준비스케줄복구) 완료", "SL");
						bIsCarSchYN = false;
					}
				}  
			}  
			
			//차량스케줄이 존재 하는 경우(하차완료 후 나가는 차량 / 하차작업을 위해 출발한 차량 / 상차완료 후 나가는 차량)
			slabUtils.printLog(logId, "▷ 하차이면서 차량스케줄 존재여부(bIsCarSchYN) : "+bIsCarSchYN, "SL");
			if (bIsCarSchYN) {	// 차량스케쥴존재유무 Flag
				
				slabUtils.printLog(logId, "▷ [" + sTrnEqpCd + "]차량의 차량진행상태 : " + ydCarProgStat, "SL");
				// 차량진행상태 [E] : 하차완료
				if ("E".equals(ydCarProgStat)) {  // 하차완료 후 출발 = 차량스케쥴이 있으면서 하차인 case.
					slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발-[" + sTrnEqpCd + "] 차량은 하차완료 후 나가는 차입니다.", "SL");
					
					/***********************************************************
					 * 하차완료인 경우 차량스케줄 삭제처리
					 ***********************************************************/
					slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발-차량스케쥴을 완료처리(DEL_YN='Y') 합니다.", "SL");
					slabUtils.printLog(logId, "▷ 차량스케쥴ID : " + ydCarSchId    , "SL");
					
					jrParam.setField("YD_CAR_SCH_ID", ydCarSchId	);
					jrParam.setField("DEL_YN"		, "Y"			);
					/* 
					UPDATE TB_YD_CARFTMVMTL
					   SET DEL_YN   = 'Y'
					     , MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updDelCarWrMgtSchMtl";
					commDao.update(jrParam, queryId, logId, mthdNm, "차량작업재료 삭제");
					
					
					/*
					UPDATE TB_YD_CARSCH
					   SET MODIFIER  = :V_MODIFIER
					     , MOD_DDTT  = SYSDATE
					     , DEL_YN    = :V_DEL_YN
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdCarSchReverseYN";
					commDao.update(jrParam, queryId, logId, mthdNm, "차량스케줄 삭제");
					
					bIsCarSchYN = false;
				
				
				} else if (PSlabYdConstant.YD_CARLD_ARR.equals(ydCarProgStat)) {  //(2)상차도착 일 경우 차량스케쥴 삭제 처리
					slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발-[" + sTrnEqpCd + "] 차량은 상차차량 입니다.", "SL");
					/****************************************************
					 * 공차도착인 차량스케줄이면 차량스케줄을 삭제 
					 * - 공차도착한 차량에 대해 공차출발 실적을 수신 시는 차량스케줄 삭제
					 ****************************************************/
					slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량출발-차량스케쥴을 완료처리(DEL_YN='Y') 합니다.", "SL");
					slabUtils.printLog(logId, "▷ 차량스케쥴ID : " + ydCarSchId    , "SL");
					jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
					jrParam.setField("DEL_YN"		, "Y");
					/*
					UPDATE TB_YD_CARFTMVMTL
					   SET DEL_YN   = 'Y'
					     , MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updDelCarWrMgtSchMtl";
					commDao.update(jrParam, queryId, logId, mthdNm, "차량작업재료 수정");
					
					/*************************************************
					 * 차량스케줄 삭제
					 *************************************************/					
					/*
					UPDATE TB_YD_CARSCH
					   SET MODIFIER  = :V_MODIFIER
					     , MOD_DDTT  = SYSDATE
					     , DEL_YN    = :V_DEL_YN
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdCarSchReverseYN";
					commDao.update(jrParam, queryId, logId, mthdNm, "차량스케줄 삭제");
					
					bIsCarSchYN = false;

				}
			} 
			
	    	/* ****************************************************************
	    	 *	발지개소코드, 발지개소Point를 야드저장위치로 변환(출발지 위치)
	    	 * ****************************************************************/
			slabUtils.printLog(logId, "5. 발지개소코드, 발지Point로 출발지 조회", "SL");
			jrParam.setField("WLOC_CD"   , sSposWlocCd);
			jrParam.setField("YD_PNT_CD" , sSposYdPntCd);
			/*
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
			//
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdStkcolWLocCdandPntCd";
			JDTORecordSet jsStkCol = commDao.select(jrParam, queryId, logId, mthdNm, "적치열 조회");

			/* ***********************************************************
			 * 다른 야드에서 출발한 경우에는 적치열 베드 단정보를 초기화 한다.
			 * ***********************************************************/
			slabUtils.printLog(logId, "6. 다른 야드에서 출발한 경우에는 적치열 베드 단정보를 초기화", "SL");
			
			String ydCarldStopLoc = ""; 
			String sColTrnEqpCd   = ""; 
			
	    	if (jsStkCol.size() > 0) {
	    		
	    		jsStkCol.absolute(1);
		    	JDTORecord jrStkCol = jsStkCol.getRecord();
		    	
		    	ydCarldStopLoc = slabUtils.trim(jrStkCol.getFieldString("YD_STK_COL_GP")); //열구분을 조회(출발지)
		    	sColTrnEqpCd   = slabUtils.trim(jrStkCol.getFieldString("TRN_EQP_CD"   )); //적치열에 존재하는 운송장비코드
		    	
				sMsg= "출발야드의 적치열["+ydCarldStopLoc+"]의 운송장비코드["+sColTrnEqpCd+"]와 전문의 운송장비코드["+sTrnEqpCd+"] 비교 ";
				slabUtils.printLog(logId, sMsg , "SL");
				
				//------------------------------------------------------------------------------------------------------------
				//	적치열의 운송장비코드와 전문의 운송장비코드가 일치하는 경우에만 맵 Clear 시작
				//------------------------------------------------------------------------------------------------------------
				 
				
				if (sColTrnEqpCd.equals(sTrnEqpCd)) {
					
					sMsg= "출발야드의 적치열["+ydCarldStopLoc+"]의 운송장비코드["+sColTrnEqpCd+"]와 전문의 운송장비코드["+sTrnEqpCd+"]가 같으므로 맵 Clear 시작 ";
					slabUtils.printLog(logId, sMsg , "SL");
					/****************************************************
					 * 적치열 비활성       (COL)
					 * 적치포인트 비활성 (YDPOINT)
					 * 적치BED
					 * 적치단
					 ****************************************************/
			    	jrParam.setField("YD_STK_COL_GP", ydCarldStopLoc);
					
					/*
					UPDATE TB_YD_STKCOL
					   SET YD_STK_COL_ACT_STAT = DECODE(YD_STK_COL_ACT_STAT, 'N', 'N', 'C')
					     , TRN_EQP_CD       = ''
					     , YD_CAR_USE_GP    = ''
					     , CAR_NO           = ''
					     , CARD_NO          = ''
					     , MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					 WHERE YD_STK_COL_GP    = :V_YD_STK_COL_GP
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updCarWrMgtStkcol";
					commDao.update(jrParam, queryId, logId, mthdNm, "TB_YD_STKCOL 초기화");
					
				    //저장위치로 초기화 하는 경우(구내운송)
					/* 
					UPDATE TB_YD_CARPOINT
					   SET TRN_EQP_CD = NULL
					     , YD_STK_COL_ACT_STAT = DECODE(CARD_NO,NULL,(DECODE(YD_STK_COL_ACT_STAT,'N','N',:V_STAT)),YD_STK_COL_ACT_STAT)
					     , MOD_DDTT = SYSDATE
					     , MODIFIER = :V_MODIFIER
					 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP 
					   AND DEL_YN = 'N'
					*/ 
					jrParam.setField("STAT"  			, "C"); 
			    	jrParam.setField("YD_STK_COL_GP"	, ydCarldStopLoc);
			    	
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.carpointstackcolgpupdateCT";
			    	commDao.update(jrParam, queryId, logId, mthdNm, "TB_YD_CARPOINT 초기화");
					
			    	
			    	long lngYdWrkAlwWt = Long.parseLong(sTrnEqpStkCapa)	;
			    	slabUtils.printLog(logId, "이송차량의적재가능중량maxMax값:"+ lngYdWrkAlwWt+ " 초기값:" +PSlabYdConstant.YD_CAR_BED_WT_MAX_DEFAULT, "SL");
			    	
			    	jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
				    jrParam.setField("YD_STK_COL_GP"      , ydCarldStopLoc);
			    	//jrParam.setField("YD_STK_BED_WT_MAX"  , PSlabYdConstant.YD_CAR_BED_WT_MAX_DEFAULT); 
			    	jrParam.setField("YD_STK_BED_ACT_STAT", "C");
			    	/*
					UPDATE TB_YD_STKBED
					   SET YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
					     , YD_STK_BED_WT_MAX   = nvl(to_number(:V_YD_STK_BED_WT_MAX), YD_STK_BED_WT_MAX) // 수정
					     , MOD_DDTT            = SYSDATE
					     , MODIFIER            = :V_MODIFIER
					 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
					*/
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updStkbedActStat";
					commDao.update(jrParam, queryId, logId, mthdNm, "TB_YD_STKBED 비활성화등록");

				    
					jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
			    	jrParam.setField("YD_STK_COL_GP"		, ydCarldStopLoc);
			    	jrParam.setField("YD_STK_LYR_ACT_STAT"	, "C");	
			    	jrParam.setField("STL_NO"				, "");
			    	jrParam.setField("YD_STK_LYR_MTL_STAT"	, "E");
			    	
			    	/*
			    	UPDATE TB_YD_STKLYR
			    	   SET YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
			    	     , STL_NO              = :V_STL_NO
			    	     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
			    	     , MODIFIER            = :V_MODIFIER
			    	     , MOD_DDTT            = SYSDATE
			    	WHERE YD_STK_COL_GP        = :V_YD_STK_COL_GP
			    	*/
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updStklyrYdStkColGp";
			    	commDao.update(jrParam, queryId, logId, mthdNm, "TB_YD_STKLYR 초기화");
					
			    	/**********************************************************
					* 업무기준 : 차량 출발 시 저장위치 제원 야드L2로 전송
					**********************************************************/
					JDTORecord jrL2Msg = slabUtils.getParam(logId, mthdNm, sModifier);
					
					
					jrL2Msg.setField("YD_INFO_SYNC_CD" , "4"); // 1:동,2:SPAN,3:열,4:BED
			    	jrL2Msg.setField("YD_GP"           , ydCarldStopLoc.substring(0, 1));
			    	jrL2Msg.setField("YD_STK_COL_GP"   , ydCarldStopLoc);
			    	jrL2Msg.setField("YD_STK_BED_NO"   , "01");

			    	if ("E".equals(sTrnWrkFullvoidGp)) {
			    		jrL2Msg.setField("YD_CAR_PROG_STAT"	, PSlabYdConstant.YD_CAR_PROG_STAT_1);
			    		jrL2Msg.setField("YD_EQP_WRK_STAT" 	, "L");
			    		sMsg = "공차출발시 시 저장위치 제원 야드L2로 전송";
			    	} else {
			    		jrL2Msg.setField("YD_CAR_PROG_STAT"	, PSlabYdConstant.YD_CAR_PROG_STAT_A);
			    		jrL2Msg.setField("YD_EQP_WRK_STAT" 	, "L");
			    		sMsg = "영차출발시 시 저장위치 제원 야드L2로 전송";
			    	}
			    	
			    	jrL2Msg.setField("YD_CAR_ARRSTRT_STAT" 	, "S"        ); //A:도착, S:출발
			    	jrL2Msg.setField("TRN_EQP_CD"  	       	, sTrnEqpCd  );
			    	jrL2Msg.setField("YD_CAR_USE_GP"       	, "L"        );//구내운송
			    	jrL2Msg.setField("CAR_NO"       		, ""         );
			    	jrL2Msg.setField("CARD_NO"       		, ""         );
			    	
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L001_CarInfo", jrL2Msg)); //전문
					
					slabUtils.printLog(logId, sMsg, "SL");
					
					/********************************************************
					 * 이송차량 영차/공차 출발시 해당 위치대기차량 입동지시 요구
					 ********************************************************/
					/*
						SELECT A.*
						     ,(SELECT YD_CARPNT_CD FROM TB_YD_CARPOINT         
						        WHERE YD_GP = 'D'
						          AND DEL_YN = 'N'
						          AND YD_STK_COL_GP = :V_YD_STK_COL_GP
						          AND ROWNUM = 1
						      ) YD_CARPNT_CD
						  FROM (
						        SELECT A.*
						             , 'E' AS TRN_WRK_FULLVOID_GP
						             , SPOS_WLOC_CD AS WLOC_CD
						          FROM TB_YD_CARSCH A
						         WHERE DEL_YN = 'N'
						           AND YD_CAR_PROG_STAT  = '1' 
						           AND SPOS_WLOC_CD IN ('DKY21', 'DWY22') 
						           AND YD_CARLD_STOP_LOC LIKE SUBSTR(:V_YD_STK_COL_GP,1,4) ||'%'
						         UNION ALL  
						        SELECT A.*
						             , 'F' AS TRN_WRK_FULLVOID_GP
						             , ARR_WLOC_CD AS WLOC_CD
						          FROM TB_YD_CARSCH A
						         WHERE DEL_YN = 'N'
						           AND YD_CAR_PROG_STAT = 'A' 
						           AND ARR_WLOC_CD  IN ('DKY21', 'DWY22')
						           AND YD_CARUD_STOP_LOC LIKE  SUBSTR(:V_YD_STK_COL_GP,1,4) ||'%'
						       ) A
						 ORDER BY YD_BAYIN_WO_SEQ  
						        , YD_CAR_SCH_ID 
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getTsNextTrnEqp";
			    	JDTORecordSet jsCarSchNext = commDao.select(jrParam, queryId, logId, mthdNm, "차량스케줄 조회");
			    	if (jsCarSchNext.size() > 0) {
			    		
			    		String ydCarUseGp = jsCarSchNext.getRecord(0).getFieldString("YD_CAR_USE_GP"); // G출하 L구내운송
			    		
		    			if ("L".equals(ydCarUseGp)) {
			    			String sTrnEqpCdNext         = jsCarSchNext.getRecord(0).getFieldString("TRN_EQP_CD");
				    		String sTrnWrkFullvoidGpNext = jsCarSchNext.getRecord(0).getFieldString("TRN_WRK_FULLVOID_GP");
				    		String sWlocCdNext           = jsCarSchNext.getRecord(0).getFieldString("WLOC_CD");
				    		
				    		jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
							jrParam.setField("TRN_EQP_CD"			, sTrnEqpCdNext        );	
							jrParam.setField("WLOC_CD"				, sWlocCdNext          );	
							jrParam.setField("TRN_WRK_FULLVOID_GP"	, sTrnWrkFullvoidGpNext);	
							jrParam.setField("PNT_DMD_DT"			, sCurrDate  );			
							
							JDTORecord jrYdMsg = this.rcvTSYDJ002(jrParam);
							jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);
			    		} 
			    	}			
				} else {
					sMsg = "출발야드의 적치열["+ydCarldStopLoc+"]의 운송장비코드["+sColTrnEqpCd+"]와 전문의 운송장비코드["+sTrnEqpCd+"]가 다르므로 맵 Clear 안함 ";
					slabUtils.printLog(logId, sMsg , "SL");
				}
				
	    	} else if ( jsStkCol.size() == 0 ) {
	    		
				if (bIsCarSchYN) {
					
					//해당야드로 출발을 한 후 대기장에 도착 후 대기장에서 출발인 경우에는 업무종료 처리
					sMsg = "발지개소코드["+sSposWlocCd+"], 발지포인트코드["+sSposYdPntCd+"] 확인 처리 ";
					slabUtils.printLog(logId, sMsg , "SL");
					
					if ( sCarSchSposWlocCd.equals(sSposWlocCd) && PSlabYdConstant.YD_WAIT_PNT_CD.equals(sSposYdPntCd) ) { //대기장포인트코드
						//발지개소코드가 이미존재하는 차량스케줄의 발지개소코드와 같고 발지포인트코드가 대기장 포인트코드인 경우에는 업무종료처리
						sMsg= "발지개소코드["+sSposWlocCd+"]가 이미존재하는 차량스케줄의 발지개소코드["+sCarSchSposWlocCd+"]와 같고 발지포인트코드["+sSposYdPntCd+"]가 대기장 포인트코드인 경우에는 업무종료처리합니다.";
						slabUtils.printLog(logId, sMsg , "S-");
						return jrRtn;
					}
				}
	    	}
	    	
			//상차LOT편성 및 작업예약등록시 파라미터값
			jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CAR_USE_GP"    , "L"                      );
	    	jrParam.setField("YD_EQP_ID"        , "XXPT01"                 );
	    	jrParam.setField("TRN_EQP_CD"       , sTrnEqpCd                );
	    	jrParam.setField("WLOC_CD"          , sArrWlocCd               );
	    	jrParam.setField("PNT_DMD_DT"       , slabUtils.getDateTime14());
	    	jrParam.setField("SPOS_YD_PNT_CD"   , sSposYdPntCd             );
	    	jrParam.setField("SPOS_WLOC_CD"     , sSposWlocCd              );

	    	/********************************************
	    	// 2. 후판Slab에서 일관제철로 이송인 경우     : 상차후 출발일때  
	    	********************************************/
	    	String sWorkGp 				= "";
	    	
    		if ("E".equals(sTrnWrkFullvoidGp) || "F".equals(sTrnWrkFullvoidGp) ) { 
    			
    			//이송상하차 후 출발착처리시  해당 포인트를 초기화 한다..
    			jrParam.setField("REPR_CD_GP"	, "DYD006" );
    			jrParam.setField("CD_GP"		, ydCarStopLoc  );    //"DBPT01" 
    			jrParam.setField("ITEM"			, "D");
    			jrParam.setField("DTL_ITEM4"	, "");
    			jrParam.setField("DTL_ITEM5"	, "N");
    			jrParam.setField("DTL_ITEM1"	, "0");
    			jrParam.setField("DTL_ITEM2"	, "0");
    			jrParam.setField("DTL_ITEM3"	, "0");
    			jrParam.setField("DTL_ITEM6"	, "0");	
    			jrParam.setField("DTL_ITEM7"	, "0");
    			jrParam.setField("DTL_ITEM8"	, "0");
    			jrParam.setField("DTL_ITEM9"	, "0");
    			jrParam.setField("DTL_ITEM10"	, "");
    			/*
				UPDATE TB_YD_RULE
				   SET DTL_ITEM1 = NVL(:V_DTL_ITEM1,DTL_ITEM1)
				      ,DTL_ITEM2 = NVL(:V_DTL_ITEM2,DTL_ITEM2)
				      ,DTL_ITEM3 = NVL(:V_DTL_ITEM3,DTL_ITEM3)
				      ,DTL_ITEM4 = NVL(:V_DTL_ITEM4,DTL_ITEM4)
				      ,DTL_ITEM5 = NVL(:V_DTL_ITEM5,DTL_ITEM5)
				      ,DTL_ITEM6 = NVL(:V_DTL_ITEM6,DTL_ITEM6)
				      ,DTL_ITEM7 = NVL(:V_DTL_ITEM7,DTL_ITEM7)
				      ,DTL_ITEM8 = NVL(:V_DTL_ITEM8,DTL_ITEM8)
				      ,DTL_ITEM9 = NVL(:V_DTL_ITEM9,DTL_ITEM9)
				      ,DTL_ITEM10 = NVL(:V_DTL_ITEM10,DTL_ITEM10)
				      ,MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				 WHERE REPR_CD_GP = :V_REPR_CD_GP
				   AND CD_GP = :V_CD_GP
				   AND ITEM = :V_ITEM
    			 */                      
    			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdRuleNvl", logId, mthdNm, "TB_YD_RULE DYD006 수정 - 초기화");
    		}
    		
	    	//상차  완료후 출발  
	    	if("F".equals(sTrnWrkFullvoidGp)  ) {
	    		if ("DWY22".equals(sSposWlocCd) || "DKY21".equals(sSposWlocCd) ) {
	    			sWorkGp	= "Y"; 
	    		}
	    	} else {  //하차후 타공정에 상차 하러 가는  경우    2021.05.20 PYS 
	    		if ("DWY22".equals(sSposWlocCd) || "DKY21".equals(sSposWlocCd) ) {
    				sWorkGp	= "Y"; 
	    		}
	    		
	    	}
	    	
	    	if("Y".equals(sWorkGp)) {
	    		
	    		/**********************************************************
	    		 * 상차출발 완료후 타공정 후속 작업 처리
	    		 **********************************************************/
	    		slabUtils.printLog(logId, "<<기존 프로세스(procMatlCarLev) 처리 시작>>", "SL");
    			EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
    			ejbConn.trx("procMatlCarLev", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
	    		slabUtils.printLog(logId, "<<기존 프로세스(procMatlCarLev) 처리 종료>>", "SL");

 
	    	} else {	
	    		
	    		/********************************************
	    		 * 7. 공 영차시 처리 로직
	    		 ********************************************/


	    		if ("E".equals(sTrnWrkFullvoidGp)) { //공차
	    			slabUtils.printLog(logId, "7.1. 공차 처리", "SL");
	    			jrParam.setField("YD_CARLD_LEV_LOC"       , ydCarldStopLoc); //야드상차출발위치
	    			jrParam.setField("TRN_EQP_STK_CAPA"       , sTrnEqpStkCapa);
	    			

	    			/*************************************
	    			 * 차량스케쥴 생성 호출
	    			 *************************************/
	    			JDTORecord jrRst = this.chkWlocCdLotComp(jrParam); 
	    			jrRtn = slabUtils.addSndData(jrRtn, jrRst);
	    			slabUtils.printLog(logId, "7.1. 공차 처리(E)", "SL");
	    			
	    		} else if ("F".equals(sTrnWrkFullvoidGp)) { //영차
	    			slabUtils.printLog(logId, "7.2. 영차 처리", "SL");
	    			if ( jsCarSch.size() <= 0 ) {
	    				throw new DAOException("운송장비코드["+sTrnEqpCd+"]로 차량스케줄 조회 Error");
	    			}
	    			
	    			slabUtils.printLog(logId, "7.2.1 하차재료 조회", "SL");
	    			jrParam.setField("YD_WBOOK_ID"            , ydCarldWrkBookId);
	    			jrParam.setField("YD_CAR_SCH_ID"          , ydCarSchId      );
	    			
	    			/*
	    		SELECT A.YD_CAR_SCH_ID                          AS YD_CAR_SCH_ID
	    		     , A.STL_NO                                 AS STL_NO
	    		     , A.REGISTER                               AS REGISTER
	    		     , TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
	    		     , A.MODIFIER                               AS MODIFIER
	    		     , TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
	    		     , A.DEL_YN                                 AS DEL_YN
	    		     , A.YD_CAR_UPP_LOC_CD                      AS YD_CAR_UPP_LOC_CD
	    		     , A.YD_STK_BED_NO                          AS YD_STK_BED_NO
	    		     , A.YD_STK_LYR_NO                          AS YD_STK_LYR_NO
	    		     , A.HCR_GP                                 AS HCR_GP
	    		     , A.STL_PROG_CD                            AS STL_PROG_CD
	    		     , A.YD_MTL_ITEM                            AS YD_MTL_ITEM
	    		     , A.YD_ROUTE_GP                            AS YD_ROUTE_GP
	    		     , DECODE(C.ARR_WLOC_CD
	    		            , 'DHY21', 'A'
	    		            , 'C3S01', 'M'
	    		            , 'D3Y43', '2'
	    		            , 'D2Y43', '0'
	    		            , 'DJY25', 'S'
	    		            , 'DWY22', 'D'
	    		            , B.YD_AIM_YD_GP) AS YD_AIM_YD_GP
	    		       --//Tong 크레인 사용 금지재 -> A동 입고
				      ,(CASE WHEN C.ARR_WLOC_CD='DHY21' AND (SELECT 'Y' FROM USRYDA.VW_YD_SLABHSM C
				                    WHERE C.HSM_GP='X'
				                      AND C.SLAB_NO=B.STL_NO)='Y' THEN 'A' 
				             --통합-> 연주 후판재 스카핑재 C동 논스카핑재 A동 --연주요청
				             WHEN C.SPOS_WLOC_CD = 'DJY25' AND C.ARR_WLOC_CD ='DHY21' AND B.SLAB_WO_RT_CD LIKE 'P%' AND B.SCARFING_YN = 'Y' AND B.SCARFING_DONE_YN = 'N' THEN 'C'
				             WHEN C.SPOS_WLOC_CD = 'DJY25' AND C.ARR_WLOC_CD ='DHY21' AND B.SLAB_WO_RT_CD LIKE 'P%' THEN 'A'
				             WHEN C.ARR_WLOC_CD='DHY21' AND B.YD_AIM_YD_GP = 'A' AND MAX(TEMP9) OVER(PARTITION BY A.YD_CAR_SCH_ID) = 'Y' THEN 'B'
				             WHEN C.ARR_WLOC_CD = 'C3S01' AND C.REGISTER='runTsRetHt' THEN 'B'--항만야드 회송차량 B동 포인트지시
				             WHEN C.REGISTER='runTsRetHt' THEN 'A' --//반송인 경우 A동 하차
				             WHEN C.ARR_WLOC_CD='C3S01' THEN 'A' --//C3#스카핑 A동 하차
				             WHEN C.ARR_WLOC_CD='DHY21' AND B.YD_AIM_BAY_GP NOT IN ('A','B','C','D') THEN 'A'  --//연주에서 다른 동이 나오는 경우 A동으로 하차
							 WHEN C.SPOS_WLOC_CD ='C3S01' AND C.ARR_WLOC_CD = 'DHY21' AND (SELECT SC.ORD_YEOJAE_GP FROM TB_PT_SLABCOMM SC WHERE SC.SLAB_NO = A.STL_NO) = '1' THEN 'B'
				        ELSE B.YD_AIM_BAY_GP 
				        END) AS YD_AIM_BAY_GP
	    		     , B.YD_RCPT_PLN_STR_LOC
	    		     , B.YD_RCPT_PLN_STR_LOC1
	    		     , B.YD_RCPT_PLN_STR_LOC2
	    		     , (SELECT ARR_YD_PNT_CD
	    		          FROM USRTSA.TB_TS_MATL_FTMV_WO C
	    		         WHERE C.TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO) FROM TB_TS_MATL_FTMV_WO D WHERE D.STL_NO=C.STL_NO)
	    		           AND C.STL_NO=A.STL_NO ) AS ARR_YD_PNT_CD
	    		     , C.YD_CAR_PROG_STAT
	    		  FROM TB_YD_CARFTMVMTL A
	    		     , TB_YD_STOCK      B
	    		     , TB_YD_CARSCH     C
	    		  WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	    		    AND A.YD_CAR_SCH_ID = C.YD_CAR_SCH_ID
	    		    AND A.STL_NO        = B.STL_NO(+)
	    		    AND A.DEL_YN        = 'N'
	    		  ORDER BY YD_STK_BED_NO, YD_STK_LYR_NO DESC
	    			 */
	    			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdCarftmvmtlID";
	    			JDTORecordSet jsCarMtl = commDao.select(jrParam, queryId, logId, mthdNm, "이송재료 조회");
	    			
	    			if ( jsCarMtl.size() <= 0 ) {
	    				throw new DAOException("이송재료 조회 Error");
	    			}
	    			
	    			/*********************************************************
	    			 * 저장품 등록
	    			 *********************************************************/	    		 
	    			for (int i = 1; i <= jsCarMtl.size(); ++i) {
	    				
	    				jsCarMtl.absolute(i);
	    				String sStlNo = jsCarMtl.getRecord().getFieldString("STL_NO");
	    				
	    				jrParam.setField("STL_NO"		, sStlNo );  
	    				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.insSlabYdStock", logId, mthdNm, "저장품 데이터 등록");
	    				
	    			} //end for
	    			
	    			

	    			/*****************************************
	    			 * 착지가  2후판Slab 일경우 하차출발(A)로 변경
	    			 *****************************************/
	    			if ("DWY22".equals(sArrWlocCd) || "DKY21".equals(sArrWlocCd)) {
	    				slabUtils.printLog(logId, "7.2.4 착지 2열연 상태코드 변경(A 하차출발)", "SL");
	    				jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
	    				jrParam.setField("YD_CAR_SCH_ID"		, ydCarSchId);
	    				jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, ""        );
	    				jrParam.setField("YD_CAR_PROG_STAT"		, "A" 		); //하차 출발
	    				jrParam.setField("YD_CARUD_LEV_DT"		, sCurrDate ); //하차출발일시
	    				jrParam.setField("YD_PNT_CD1"		    , sSposYdPntCd ); //발지POINT 
	    				jrParam.setField("YD_PNT_CD3"		    , sArrYdPntCd  ); //착지POINT 
	    				/* 
						UPDATE TB_YD_CARSCH
						   SET MODIFIER = :V_MODIFIER
						      ,MOD_DDTT = SYSDATE
						      ,YD_CARUD_WRK_BOOK_ID = :V_YD_CARUD_WRK_BOOK_ID
						      ,YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT
						      ,YD_CARUD_LEV_DT      = TO_DATE(:V_YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')
						      ,YD_PNT_CD1           = DECODE(YD_PNT_CD1,'', :V_YD_PNT_CD1, YD_PNT_CD1)
						      ,YD_PNT_CD3           = DECODE(YD_PNT_CD3,'', :V_YD_PNT_CD3, YD_PNT_CD3)
						 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
 	    				 */ 
	    				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdCarsch";
	    				commDao.update(jrParam, queryId, logId, mthdNm, "차량스케쥴수정");
	    				
	    				/*****************************************
	    				 * 소재차량도착Point요구 - 영차
	    				 *****************************************/
	    				slabUtils.printLog(logId, "8. 소재차량도착 point요구", "SL");
	    				
	    				jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
	    				jrParam.setField("TRN_EQP_CD"			, sTrnEqpCd  );	
	    				jrParam.setField("WLOC_CD"				, sArrWlocCd );			
	    				jrParam.setField("TRN_WRK_FULLVOID_GP"	, "F"        );			
	    				jrParam.setField("PNT_DMD_DT"			, sCurrDate  );			
	    				
	    				JDTORecord jrYdMsg = this.rcvTSYDJ002(jrParam);
	    				jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);	
	    			} 
	    			slabUtils.printLog(logId, "7.2. 영차 처리(F)", "SL");
	    			
	    		} else {
	    			throw new Exception("운송작업영공구분코드 Error");
	    		}
	    	}
			
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "처리완료");

			slabUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	

	/**
	 * 	[A] 오퍼레이션명 : 상차지 정보 CLEAR
	 *  2021.02.02 pys
	 * 	@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *  @param JDTORecord rcvMsg
	 *  @return JDTORecord
	 *  @throws DAOException
	*/
	public JDTORecord procMatlCarCancel(JDTORecord rcvMsg)throws DAOException  {
		String mthdNm = "후판슬라브야드 상차지 정보 CLEAR[PSlabYdMvCarSeEJB.procMatlCarCancel] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
	    
	    try{
			slabUtils.printLog(logId, mthdNm, "S+");
			slabUtils.printParam(logId, rcvMsg);
			
			String sTrnEqpCd  		= slabUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 	  //운송장비코드
			String ydCarSchId 		= slabUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID")); //차량스케줄ID 				
			String ydCarldWrkBookId = slabUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"));   //작업예약ID
			String sModifier  		= slabUtils.trim(rcvMsg.getFieldString("MODIFIER")); 	  //수정자(Backup Only)
			
			String sMsg      = "";

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			JDTORecord jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd);			
				
			/*************************************************
			 * 적치열 삭제처리
			 *************************************************/	
			/* 
			UPDATE USRYDA.TB_YD_STKCOL
			SET TRN_EQP_CD    = NULL
			  , YD_CAR_USE_GP = NULL
			  , MOD_DDTT      = SYSDATE 
			  , MODIFIER      = V_MODIFIER
			WHERE TRN_EQP_CD  = :V_TRN_EQP_CD
			*/ 
			String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdStkcolTrnEqpCdToNull";
			commDao.update(jrParam, queryId, logId, mthdNm, "열CLEAR");
			
			/****************************************************
			 * 차량작업재료 조회
			 ****************************************************/
		    jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
			/*
			UPDATE TB_YD_CARFTMVMTL
			   SET DEL_YN   = 'Y'
			     , MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			 */
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updDelCarWrMgtSchMtl";
			commDao.update(jrParam, queryId, logId, mthdNm, "차량작업재료 수정");
			
			/*************************************************
			 * 차량스케줄 삭제
			 *************************************************/					
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
			jrParam.setField("DEL_YN"		, "Y");
			jrParam.setField("YD_WBOOK_ID"	, ydCarldWrkBookId);
			/* 
			UPDATE TB_YD_CARSCH
			   SET MODIFIER  = :V_MODIFIER
			     , MOD_DDTT  = SYSDATE
			     , DEL_YN    = :V_DEL_YN
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			 */
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdCarSchReverseYN";
			commDao.update(jrParam, queryId, logId, mthdNm, "차량스케줄 삭제");
			
			/*************************************************
			 * 작업예약 삭제
			 *************************************************/
			/*
			UPDATE TB_YD_WRKBOOKMTL  
			   SET MOD_DDTT = SYSDATE
			     , MODIFIER = :V_MODIFIER
			     , DEL_YN   = :V_DEL_YN
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID	
			 */
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.delWrkbookMtl";
			commDao.update(jrParam, queryId, logId, mthdNm, "작업예약 재료 삭제");
			
			/*
			UPDATE TB_YD_WRKBOOK
			   SET MOD_DDTT = SYSDATE
			     , MODIFIER = :V_MODIFIER
			     , DEL_YN   = :V_DEL_YN
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			 */
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.delYdWrkbook";
			commDao.update(jrParam, queryId, logId, mthdNm, "작업예약 삭제");
			//-
			/*************************************************
			 * 저장품에서 작업예약ID와 스케줄코드 Clear시킴
			 *************************************************/
			/* # com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdStockDelYdWBookId
			--차량동간이적
			UPDATE USRYDA.TB_YD_STOCK
			   SET YD_WBOOK_ID = NULL
			     , YD_SCH_CD   = NULL 
			     , TRANS_ORD_DATE  = (CASE WHEN LENGTH(CAR_NO) = 4 THEN NULL ELSE TRANS_ORD_DATE END)
			     , TRANS_ORD_SEQNO = (CASE WHEN LENGTH(CAR_NO) = 4 THEN NULL ELSE TRANS_ORD_SEQNO END)
			     , CARD_NO         = (CASE WHEN LENGTH(CAR_NO) = 4 THEN NULL ELSE CARD_NO END)
			     , SAILNO          = (CASE WHEN LENGTH(CAR_NO) = 4 THEN NULL ELSE SAILNO END)
			     , SCARFING_YN     = (CASE WHEN LENGTH(CAR_NO) = 4 THEN NULL ELSE SCARFING_YN END)
			     , YD_CAR_UPP_LOC_CD= (CASE WHEN LENGTH(CAR_NO) = 4 THEN NULL ELSE YD_CAR_UPP_LOC_CD END)
			     , COIL_CAR_NO     = NULL 
			     , COIL_CAR_LOTID_YN     = NULL 
			     , CAR_FRTOMOVE_WORD_NO  = NULL 
			 WHERE STL_NO IN (SELECT STL_NO 
			                    FROM TB_YD_WRKBOOKMTL 
				               WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID)
			 */
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdStockDelYdWBookId";
			commDao.update(jrParam, queryId, logId, mthdNm, "저장품 초기화");
			
			/**************************************************
    		 * 1. 준비스케쥴 복구
    		 **************************************************/
			/** 1.1 준비스케쥴 검색**/
			/* 
			SELECT YD_PREP_SCH_ID
			     , YD_SCH_CD
			     , DEL_YN
			     , YD_GP
			     , YD_PREP_WK_ST
			     , YD_TO_LOC_DCSN_MTD
			     , YD_TO_LOC_GUIDE
			     , ARR_WLOC_CD
			     , YD_AIM_BAY_GP
			     , YD_CARASGN_SEQ
			     , YD_EQP_WRK_SH
			     , YD_WRK_PLAN_CRN
			     , YD_WBOOK_ID
			  FROM TB_YD_PREPSCH
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			*/
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdPrepschByYdWbookId";
			JDTORecordSet jsPrepSch = commDao.select(jrParam, queryId, logId, mthdNm, "준비스케줄검색");
		
			sMsg = "작업예약ID["+ ydCarldWrkBookId +"] 준비스케쥴 검색 jsPrepSch.size() = "+ jsPrepSch.size();
			slabUtils.printLog(logId, sMsg, "SL");
			
			if ( jsPrepSch.size() > 0 ) {
				
				/** 1.1 준비스케쥴 재료정보 복구 */
				jsPrepSch.first();
				String ydPrepSchId = slabUtils.trim(jsPrepSch.getRecord().getFieldString("YD_PREP_SCH_ID"));
				
				jrParam.setField("YD_PREP_SCH_ID"	, ydPrepSchId);
				jrParam.setField("DEL_YN"			, "N");
				
				/*
				UPDATE TB_YD_PREPMTL
				   SET MODIFIER       = :V_MODIFIER
				     , MOD_DDTT       = SYSDATE
				     , DEL_YN         = :V_DEL_YN
				 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
				*/
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.delYdPrepmtlByPrepSchId1";
				commDao.update(jrParam, queryId, logId, mthdNm, "준비스케줄재료 복구");
				
	    		/** 2-2. 준비스케쥴 복구 (작업예약 null 처리) */
				/* 
				UPDATE TB_YD_PREPSCH
				   SET DEL_YN         = :V_DEL_YN
				     , YD_WBOOK_ID    = :V_YD_WBOOK_ID
				     , MODIFIER       = :V_MODIFIER
				     , MOD_DDTT       = SYSDATE
				 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
				*/
				jrParam.setField("YD_WBOOK_ID", "");
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.delYdPrepsch1";
				commDao.update(jrParam, queryId, logId, mthdNm, "준비스케줄 복구");
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
	 * 오퍼레이션명 : 초기화 처리  (CarinfoResetC)  --취소전문에 따른 후속 조치                   
	 * 2021.02.02 pys 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord procInitCarinfo(JDTORecord rcvMsg) throws DAOException  {
		String mthdNm   = "초기화 처리 [PSlabYdMvCarSeEJB.procInitCarinfo] < " + rcvMsg.getResultMsg();
		String logId    = rcvMsg.getResultCode();
		
		try {
			slabUtils.printLog(logId, mthdNm, "S+");
			slabUtils.printParam(logId, rcvMsg);

			String msgId      = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sTrnEqpCd  = slabUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"   ));
			String sModifier  = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			JDTORecord jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRN_EQP_CD", sTrnEqpCd);
			
			/* 운송장비코드로 차량스케줄 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdCarschByTrnEqpCd
			SELECT *
			  FROM (
			        SELECT    YD_CAR_SCH_ID
			                , DEL_YN
			                , YD_EQP_ID
			                , YD_CAR_USE_GP
			                , CAR_NO
			                , TRN_EQP_CD
			                , CAR_KIND
			                , TRANS_EQUIPMENT_TYPE
			                , YD_EQP_WRK_STAT
			                , YD_WRK_PROG_STAT
			                , YD_EQP_WRK_SH
			                , YD_EQP_WRK_WT
			                , YD_STK_BED_TP
			                , SPOS_WLOC_CD
			                , ARR_WLOC_CD
			                , YD_CARLD_LEV_LOC
			                , YD_CARLD_LEV_DT
			                , YD_CARLD_PNT_WO_DT
			                , YD_PNT_CD1
			                , YD_PNT_CD2
			                , YD_CARLD_WRK_BOOK_ID
			                , YD_CARLD_SCH_REQ_GP
			                , YD_CARLD_STOP_LOC
			                , YD_CARLD_ARR_DT
			                , YD_CARLD_ST_DT
			                , YD_CARLD_CMPL_DT
			                , YD_CARLD_WRK_ACT_GP
			                , YD_CARLD_CHK_DT
			                , YD_CARUD_LEV_DT
			                , YD_CARUD_PNT_WO_DT
			                , NVL(YD_PNT_CD3, '0000') AS YD_PNT_CD3
			                , YD_PNT_CD4
			                , YD_CARUD_WRK_BOOK_ID
			                , YD_CARUD_STOP_LOC
			                , YD_CARUD_SCH_REQ_GP
			                , YD_CARUD_ARR_DT
			                , YD_CARUD_CHK_DT
			                , YD_CARUD_ST_DT
			                , YD_CARUD_CMPL_DT
			                , YD_CARUD_WRK_ACT_GP
			                , YD_TRN_WRK_DELY_CD
			                , CARD_NO
			                , YD_CAR_PROG_STAT
			                , FRTOMOVE_PLANT_GP
			                , PROC_TO
			                , RENTPROC_CD
			                , YD_FRTOMOVE_YD_GP
			                , YD_FRTOMOVE_BAY_GP
			                , URGENT_FRTOMOVE_WORD_GP
			                , DEST_TEL_NO
			                , YD_DLVRDD_RULE_DD
			                , SHIPASSIGN_WORD_DATE
			                , SHIPASSIGN_WORD_SEQNO
			                , SHIP_CD
			                , SHIP_NAME
			                , RSHP_HOLD_NO
			                , BERTH_NO
			                , SAILNO
			                , YD_CAR_WRK_GP
			                , TRANS_ORD_DATE
			                , TRANS_ORD_SEQNO
			                , YD_BAYIN_WO_SEQ
			                , YD_CAR_RCPT_CHK_YN
			                , YD_CAR_ISSUE_CHK_YN
			                , YD_CAR_RCPT_CHECKER
			                , YD_CAR_ISSUE_CHECKER
			                , IF_SEQ_NO
			                , TEL_NO
			                , CMBN_CARLD_YN
			                , WAIT_ARR_DDTT
			                , WAIT_ARR_GP
			                , YD_MSG_NM
			                , FRTOMOVE_WORD_NO
			                , DRIVER_NAME
			          FROM TB_YD_CARSCH A
			         WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			           AND DEL_YN     ='N'
			         ORDER BY YD_CAR_SCH_ID DESC
			                , YD_CARUD_CMPL_DT DESC
			       ) A
			 WHERE ROWNUM <= 1
			 */
			String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdCarschByTrnEqpCd";
			JDTORecordSet jsCarSch = commDao.select(jrParam, queryId, logId, mthdNm, "차량스케줄 조회");
			
			if (jsCarSch.size() == 0) {
				slabUtils.printLog(logId, "차량스케줄이 없습니다.", "S-");
				return jrRtn;
			}
			
			String ydCarSchId = jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID");
		
			/* #차량스케줄에 따른 이송재료 검색--  com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarWrMgt  
			SELECT A.TRN_EQP_CD
			     , A.YD_CAR_USE_GP
			     , A.YD_CAR_SCH_ID
			     , CASE WHEN A.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN A.YD_CARLD_STOP_LOC ELSE '' END YD_CARLD_STOP_LOC
			     , CASE WHEN A.YD_CAR_PROG_STAT IN ('A','B','C','D','E') THEN A.YD_CARUD_STOP_LOC ELSE '' END YD_CARUD_STOP_LOC
			     , NVL(C.YD_WBOOK_ID,(CASE WHEN A.YD_CAR_PROG_STAT IN ('1','2','3','4','5')
			        THEN (SELECT MAX(YD_WBOOK_ID) FROM USRYDA.TB_YD_WRKBOOKMTL WHERE DEL_YN='N' AND YD_WBOOK_ID = A.YD_CARLD_WRK_BOOK_ID  ) ELSE '' END)
			           ) AS  YD_CARLD_YD_WBOOK_ID
			     , NVL(D.YD_WBOOK_ID,(CASE WHEN A.YD_CAR_PROG_STAT IN ('A','B','C','D','E')
			        THEN (SELECT MAX(YD_WBOOK_ID) FROM USRYDA.TB_YD_WRKBOOKMTL WHERE DEL_YN='N' AND YD_WBOOK_ID = A.YD_CARUD_WRK_BOOK_ID  ) ELSE '' END)
			           ) AS  YD_CARUD_YD_WBOOK_ID
			     , A.YD_CAR_PROG_STAT
			     , (SELECT MAX(YD_CRN_SCH_ID) FROM  USRYDA.TB_YD_CRNSCH WHERE DEL_YN='N' AND YD_WBOOK_ID = C.YD_WBOOK_ID  ) AS YD_CRN_SCH_ID
			     , (SELECT YD_PREP_SCH_ID
			          FROM USRYDA.TB_YD_PREPSCH
			         WHERE YD_WBOOK_ID = C.YD_WBOOK_ID
			           AND ROWNUM = 1) YD_PREP_SCH_ID
			     , NVL(C.YD_SCH_CD,D.YD_SCH_CD ) AS YD_SCH_CD
			     , B.STL_NO
			     , decode(C.YD_WBOOK_ID,'',D.YD_WBOOK_ID,C.YD_WBOOK_ID) AS YD_WBOOK_ID
			     , A.CAR_NO
			     , A.CARD_NO
			  FROM USRYDA.TB_YD_CARSCH A
			     , USRYDA.TB_YD_CARFTMVMTL B
			     , (
			        SELECT A1.*
			          FROM USRYDA.TB_YD_WRKBOOK  A1
			             , USRYDA.TB_YD_WRKBOOKMTL   A2
			         WHERE A1.YD_WBOOK_ID = A2.YD_WBOOK_ID(+)
			           AND A1.DEL_YN = 'N'
			           AND A2.DEL_YN = 'N'
			           AND A1.YD_GP  = 'D'
			       ) C
			     , (
			        SELECT A1.*
			          FROM USRYDA.TB_YD_WRKBOOK  A1
			             , USRYDA.TB_YD_WRKBOOKMTL   A2
			         WHERE A1.YD_WBOOK_ID = A2.YD_WBOOK_ID(+)
			           AND A1.DEL_YN = 'N'
			           AND A2.DEL_YN = 'N'
			           AND A1.YD_GP  = 'D'
			       ) D
			 WHERE A.YD_CAR_SCH_ID        = B.YD_CAR_SCH_ID(+)
			   AND A.YD_CARLD_WRK_BOOK_ID = C.YD_WBOOK_ID(+)
			   AND A.YD_CARUD_WRK_BOOK_ID = D.YD_WBOOK_ID(+)
			   AND A.YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID  -- 차량스케줄 ID
			   AND A.DEL_YN               = 'N'
			   AND B.DEL_YN(+)            = 'N'
	   
			*/
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarWrMgt";
		    JDTORecordSet jsSchInfo = commDao.select(jrParam, queryId, logId, mthdNm, "차량스케쥴검색");
		    
		    if( jsSchInfo.size() < 1 ) {
		    	slabUtils.printLog(logId, "차량스케줄정보가 없습니다.", "S-");
				return jrRtn;
		    }
		    
		    jsSchInfo.first();

		    JDTORecord jrCarSchInfo = jsSchInfo.getRecord();
		    
		    String ydCrnSchId 		= jrCarSchInfo.getFieldString("YD_CRN_SCH_ID");
		    slabUtils.printLog(logId, "##크레인작업지시 여부:"+ ydCrnSchId , "SL"); 
		    String ydCarLdYdWbookId = jrCarSchInfo.getFieldString("YD_CARLD_YD_WBOOK_ID");
		    String ydCarUdYdWbookId = jrCarSchInfo.getFieldString("YD_CARUD_YD_WBOOK_ID");
		    String ydCarLdStopLoc 	= jrCarSchInfo.getFieldString("YD_CARLD_STOP_LOC");
		    String ydCarUdStopLoc 	= jrCarSchInfo.getFieldString("YD_CARUD_STOP_LOC");
		    String ydCarProgStat 	= jrCarSchInfo.getFieldString("YD_CAR_PROG_STAT");
		    String ydPrepSchId 		= jrCarSchInfo.getFieldString("YD_PREP_SCH_ID");
		    String ydSchCd			= jrCarSchInfo.getFieldString("YD_SCH_CD");
		    String sCarNo           = jrCarSchInfo.getFieldString("CAR_NO");
		    String sCardNo          = jrCarSchInfo.getFieldString("CARD_NO");
		    
		    boolean bCarMvYn = false;  // 차량동간이적 여부
		    
	    	/**
	    	 *  차량 현재 크레인작업지시 있을 때
	    	 */
		    if (!"".equals(ydCrnSchId)) {
		    	jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
		    	jrParam.setField("YD_SCH_CD"	, ydSchCd);
		    	jrParam.setField("DEL_YN"		, "Y");
		    	
		    	if("".equals(ydCarLdYdWbookId ) ){
		    		jrParam.setField("YD_WBOOK_ID", ydCarUdYdWbookId);
		    	} else {
		    		jrParam.setField("YD_WBOOK_ID", ydCarLdYdWbookId);
		    	}

		    	/**********************************************************
				* 1. 크레인스케줄 취소      -(차량이송재료 기준) 
				**********************************************************/
				EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
				JDTORecord jrCancelWrk = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
		    	String sRtnCd	= slabUtils.nvl(jrCancelWrk.getFieldString("RTN_CD"), "0");
		    	String sRtnMsg	= slabUtils.trim(jrCancelWrk.getFieldString("RTN_MSG"));
		    	
		    	if (!"1".equals(sRtnCd)) {
					slabUtils.printLog(logId, sRtnMsg, "S-");
					
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sRtnMsg);
					return jrRtn;
		    	}
		    	jrRtn = slabUtils.addSndData(jrRtn, jrCancelWrk);
		    	
		    	if (bCarMvYn) { //차량동간이적이면
			    	slabUtils.printLog(logId, "차량동간이적 시 작업예약 삭제 생략", "SL");
		    	} else {
		    		
	 				/*
					 * 크레인작업관리 작업취소 조회                  --크레인스케줄에 예약id 기준 
						SELECT YD_CRN_SCH_ID
						      ,YD_WRK_PROG_STAT
						      ,YD_EQP_ID
						      ,YD_SCH_CD
						      ,MODIFIER
						  FROM TB_YD_CRNSCH
						 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
						   AND DEL_YN      = 'N'
						 ORDER BY YD_CRN_SCH_ID
					 */
	                JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCrnWrkMgtWCSch", logId, mthdNm, "작업취소 스케줄정보 조회");

				    if ( jsCrn.size() > 0) {
				    
				      	jrParam.setField("YD_CRN_SCH_ID", slabUtils.trim(jsCrn.getRecord().getFieldString("YD_CRN_SCH_ID"  )));
				      	jrParam.setField("YD_EQP_ID"    , slabUtils.trim(jsCrn.getRecord().getFieldString("YD_EQP_ID"  ))   );
				      	jrParam.setField("YD_SCH_CD"    , slabUtils.trim(jsCrn.getRecord().getFieldString("YD_SCH_CD"  ))   );
				      	//jrParam.setField("YD_WBOOK_ID", slabUtils.trim(jsCrn.getRecord().getFieldString("YD_WBOOK_ID"  )) );
				    }

		    		/**
		    		 * 작업예약 삭제
		    		 */
		    		EJBConnector ejbConn1 = new EJBConnector("default", "PSlabYdJspSeEJB", this);
					JDTORecord jrDelWbk = (JDTORecord)ejbConn1.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
		    		
			    	sRtnCd	= slabUtils.trim(jrDelWbk.getFieldString("RTN_CD"));
			    	sRtnMsg	= slabUtils.trim(jrDelWbk.getFieldString("RTN_MSG"));
			    	
			    	if ( "0".equals(sRtnCd) ) {
			    		slabUtils.printLog(logId, sRtnMsg, "SL");
			    		
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", sRtnMsg);
						return jrRtn;
			    	}
		    	}
		    } else {

		    	//--크레인 작업 지시가 없는경우   : 예약ID 기준으로  
		    	String ydCarYdWbookId = "";

		    	if("".equals(ydCarLdYdWbookId ) ){
		    		jrParam.setField("YD_WBOOK_ID", ydCarUdYdWbookId);
		    		ydCarYdWbookId                = ydCarUdYdWbookId;
		    	} else {
		    		jrParam.setField("YD_WBOOK_ID", ydCarLdYdWbookId);
		    		ydCarYdWbookId                = ydCarLdYdWbookId;
		    	}
		    	
				/*
				 * 크레인작업관리 작업취소 조회 
					SELECT YD_CRN_SCH_ID
					      ,YD_WRK_PROG_STAT
					      ,YD_EQP_ID
					      ,YD_SCH_CD
					      ,MODIFIER
					  FROM TB_YD_CRNSCH
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND DEL_YN      = 'N'  --Y
					 ORDER BY YD_CRN_SCH_ID
				 */
				JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCrnWrkMgtWCSch", logId, mthdNm, "작업취소 스케줄정보 조회");
    		
			    if ( jsCrn.size() > 0) {
			      	jrParam.setField("YD_CRN_SCH_ID", slabUtils.trim(jsCrn.getRecord().getFieldString("YD_CRN_SCH_ID"  )));
			      	jrParam.setField("YD_EQP_ID"    , slabUtils.trim(jsCrn.getRecord().getFieldString("YD_EQP_ID"  ))   );
			      	jrParam.setField("YD_SCH_CD"    , slabUtils.trim(jsCrn.getRecord().getFieldString("YD_SCH_CD"  ))   );
			      	
		      		// 작업예약취소 호출
		      		jrParam.setField("YD_WBOOK_ID"	, ydCarYdWbookId);
		      		
		      		EJBConnector ejbConn1 = new EJBConnector("default", "PSlabYdJspSeEJB", this);
		      		JDTORecord jrWbkResult = (JDTORecord)ejbConn1.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
		      		
		      		String sRtnCd	= slabUtils.nvl(jrWbkResult.getFieldString("RTN_CD"), "0");
		      		String sRtnMsg	= slabUtils.trim(jrWbkResult.getFieldString("RTN_MSG"));
		      		
		      		if( !"1".equals(sRtnCd) ) {
		      			slabUtils.printLog(logId, sRtnMsg, "SL");
		      			
		      			jrRtn.setField("RTN_CD"  , "0");
		      			jrRtn.setField("RTN_MSG" , sRtnMsg);
		      			return jrRtn;
		      		} 
		      		
		      		String sCraneWrSndYn = slabUtils.trim(jrWbkResult.getFieldString("CRANE_WR_SND_YN"));
		      		slabUtils.printLog(logId, "CRANE_WR_SND_YN:"+sCraneWrSndYn, "SL");
		      		
		      		if ("Y".equals(sCraneWrSndYn)) {
		      			String sEqpId		= slabUtils.trim(jrWbkResult.getFieldString("YD_EQP_ID"));
		      			String sWrkProgStat	= slabUtils.trim(jrWbkResult.getFieldString("YD_WRK_PROG_STAT"));
		      			String sYdSchCd 	= slabUtils.trim(jrWbkResult.getFieldString("YD_SCH_CD"));
		      			
				    	slabUtils.printLog(logId, "YD_EQP_ID        : ["+ sEqpId +"]"		, "SL");
				    	slabUtils.printLog(logId, "YD_WRK_PROG_STAT : ["+ sWrkProgStat +"]"	, "SL");
				    	slabUtils.printLog(logId, "YD_SCH_CD        : ["+ sYdSchCd +"]"		, "SL");

		      			//jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
		      			jrParam.setField("JMS_TC_CD"        , "Y3YDL007"); //크레인작업지시요구
		      			jrParam.setField("YD_WRK_PROG_STAT" , sWrkProgStat);
		      			jrParam.setField("YD_SCH_CD"        , sYdSchCd);
		      			jrParam.setField("YD_EQP_ID"        , sEqpId);
		      			
		      			// 크레인작업지시요구  : procY5CrnWrkOrdReq -> rcvY5YDL007 변경  --> (후판) rcvY3YDL007
		      			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdL2RcvSeEJB", this);
		      			JDTORecord jrRtnParam = (JDTORecord)ejbConn.trx("rcvY3YDL007", new Class[] { JDTORecord.class }, new Object[] { jrParam });
		      			
		      			jrRtn = slabUtils.addSndData(jrRtn, jrRtnParam );
		      		}
			    }
		    }
		 
		    /*
		     *  차량스케쥴 취소
		     */
		    if (!"".equals(ydCarSchId)) {
		    	
		    	jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
		    	jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
				
				/**********************************************************
				* 스케쥴 삭제 Flag
				**********************************************************/
				/*
				UPDATE TB_YD_CARFTMVMTL
				   SET DEL_YN   = 'Y'
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updDelCarWrMgtSchMtl";
				commDao.update(jrParam, queryId, logId, mthdNm, "TB_YD_CARFTMVMTL 삭제");
				
				/*
				UPDATE TB_YD_CARSCH
				   SET DEL_YN   = 'Y'
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updDelCarWrMgtSch";
				commDao.update(jrParam, queryId, logId, mthdNm, "TB_YD_CARSCH 삭제");
				
				String sCarStopLoc = "";
				slabUtils.printLog(logId, "진행상태:"+ ydCarProgStat + " Ld:" +ydCarLdStopLoc + " Ud:"+ydCarUdStopLoc , "SL");
				
				// 대차차량인 경우 생략
				if ( !"1".equals(ydCarProgStat) &&
					 !"A".equals(ydCarProgStat) ) {
					
					if ( "2".equals(ydCarProgStat) ||
						 "3".equals(ydCarProgStat) ||
						 "4".equals(ydCarProgStat) ||
						 "5".equals(ydCarProgStat) ) {
						sCarStopLoc = ydCarLdStopLoc;
					} else {
						sCarStopLoc = ydCarUdStopLoc;
					}
					
					jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
			    	jrParam.setField("STAT"			, "C");
			    	jrParam.setField("YD_STK_COL_GP", sCarStopLoc);
					/*
					UPDATE TB_YD_CARPOINT
					   SET CARD_NO       = NULL
					     , CAR_NO        = NULL
					     , TRN_EQP_CD    = NULL
					     , YD_STK_COL_ACT_STAT = NVL2(TRN_EQP_CD, YD_STK_COL_ACT_STAT, :V_STAT)
					     , MODIFIER      = :V_MODIFIER
					     , MOD_DDTT      = SYSDATE
					 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
					*/
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updCarPointStkColGpC";
					commDao.update(jrParam, queryId, logId, mthdNm, "차량POINT 차량정보 초기화");
				}
		    }
			
			// 상차_도착이후 
			if ( "2".equals(ydCarProgStat) ||  
				 "3".equals(ydCarProgStat) ||
				 "4".equals(ydCarProgStat) ||
				 "5".equals(ydCarProgStat) ) {
				if (!"".equals(ydCarLdStopLoc)) {
					
			    	jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
			    	jrParam.setField("YD_STK_COL_GP", ydCarLdStopLoc);
					/*
					UPDATE TB_YD_STKCOL
					   SET YD_STK_COL_ACT_STAT = DECODE(YD_STK_COL_ACT_STAT, 'N', 'N', 'C')
					     , TRN_EQP_CD       = ''
					     , YD_CAR_USE_GP    = ''
					     , CAR_NO           = ''
					     , CARD_NO          = ''
					     , MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					 WHERE YD_STK_COL_GP    = :V_YD_STK_COL_GP
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updCarWrMgtStkcol";
					commDao.update(jrParam, queryId, logId, mthdNm, "TB_YD_STKCOL 초기화");
					 
					// 저장위치 비활성화등록
			    	jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
			    	jrParam.setField("YD_STK_BED_WT_MAX"  , PSlabYdConstant.YD_CAR_BED_WT_MAX_DEFAULT); // 100000
			    	jrParam.setField("YD_STK_COL_GP"	  , ydCarLdStopLoc);
			    	jrParam.setField("YD_STK_BED_ACT_STAT", "C");
			    	
			    	/*
					UPDATE TB_YD_STKBED
					   SET YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
					     , YD_STK_BED_WT_MAX   = nvl(to_number(:V_YD_STK_BED_WT_MAX), 0)
					     , MOD_DDTT            = SYSDATE
					     , MODIFIER            = :V_MODIFIER
					 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP					 
			    	 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updStkbedActStat";
					commDao.update(jrParam, queryId, logId, mthdNm, "TB_YD_STKBED 비활성화등록");
					
										
					// 적치단 비활성화
					jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
			    	jrParam.setField("YD_STK_COL_GP"		, ydCarLdStopLoc);
			    	jrParam.setField("YD_STK_LYR_ACT_STAT"	, "C");
			    	jrParam.setField("STL_NO"				, "");
			    	jrParam.setField("YD_STK_LYR_MTL_STAT"	, "E");
			    	
			    	/*
			    	UPDATE TB_YD_STKLYR
			    	   SET YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
			    	     , STL_NO              = :V_STL_NO
			    	     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
			    	     , MODIFIER            = :V_MODIFIER
			    	     , MOD_DDTT            = SYSDATE
			    	WHERE YD_STK_COL_GP        = :V_YD_STK_COL_GP
			    	*/
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updStklyrYdStkColGp";
			    	commDao.update(jrParam, queryId, logId, mthdNm, "TB_YD_STKLYR 초기화");
						
					/**********************************************************
					* 업무기준 : 차량 출발 시 저장위치 제원 야드L2로 전송
					**********************************************************/
					JDTORecord sndL2Msg = slabUtils.getParam(logId, mthdNm, sModifier);
					
    				sndL2Msg.setField("YD_INFO_SYNC_CD"			, "4"                         ); //야드정보동기화코드
    				sndL2Msg.setField("MSG_GP"					, "I"                         ); //전문구분
    				sndL2Msg.setField("YD_STK_COL_GP"    		, ydCarLdStopLoc);
    				sndL2Msg.setField("YD_STK_BED_NO"    		, "01"  );
					if ("".equals(sTrnEqpCd)) {
						sndL2Msg.setField("YD_CAR_USE_GP"    	, "G"   ); //L:구내운송, G:출하차량
						sndL2Msg.setField("CAR_NO"  			, sCarNo); //차량번호
						sndL2Msg.setField("CARD_NO"  			, sCardNo); //카드번호
					} else {
						sndL2Msg.setField("YD_CAR_USE_GP"    	, "L"   );   //L:구내운송, G:출하차량
						sndL2Msg.setField("TRN_EQP_CD"  		, sTrnEqpCd);	
					}
					sndL2Msg.setField("YD_CAR_ARRSTRT_STAT" 	, "S"); //A:도착, S:출발
					sndL2Msg.setField("YD_EQP_WRK_STAT"     	, "L");
					
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L001_CarInfo", sndL2Msg));
					
				}
			} else if ( "B".equals(ydCarProgStat) ||
					    "C".equals(ydCarProgStat) ||
					    "D".equals(ydCarProgStat) ||
					    "E".equals(ydCarProgStat) ) {
				
				if ( !"".equals(ydCarUdStopLoc) ) {
					jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
			    	jrParam.setField("YD_STK_COL_GP", ydCarUdStopLoc);
					
					/*
					UPDATE TB_YD_STKCOL
					   SET YD_STK_COL_ACT_STAT = DECODE(YD_STK_COL_ACT_STAT, 'N', 'N', 'C')
					     , TRN_EQP_CD       = ''
					     , YD_CAR_USE_GP    = ''
					     , CAR_NO           = ''
					     , CARD_NO          = ''
					     , MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					 WHERE YD_STK_COL_GP    = :V_YD_STK_COL_GP
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updCarWrMgtStkcol";
					commDao.update(jrParam, queryId, logId, mthdNm, "TB_YD_STKCOL 초기화");
					
					// 저장위치 비활성화등록
			    	jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
			    	jrParam.setField("YD_STK_BED_WT_MAX"  , PSlabYdConstant.YD_CAR_BED_WT_MAX_DEFAULT); // 100000
			    	jrParam.setField("YD_STK_COL_GP"      , ydCarUdStopLoc);
			    	jrParam.setField("YD_STK_BED_ACT_STAT", "C");
			    	/*
					UPDATE TB_YD_STKBED
					   SET YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
					     , YD_STK_BED_WT_MAX   = nvl(to_number(:V_YD_STK_BED_WT_MAX), 0)
					     , MOD_DDTT            = SYSDATE
					     , MODIFIER            = :V_MODIFIER
					 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
			    	 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updStkbedActStat";
					commDao.update(jrParam, queryId, logId, mthdNm, "TB_YD_STKBED 비활성화등록");
					
					// 적치단 비활성화
			    	jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
			    	jrParam.setField("YD_STK_COL_GP"		, ydCarUdStopLoc);
			    	jrParam.setField("YD_STK_LYR_ACT_STAT"	, "C");
			    	jrParam.setField("STL_NO"				, "");
			    	jrParam.setField("YD_STK_LYR_MTL_STAT"	, "E");
			    	/*
			    	UPDATE TB_YD_STKLYR
			    	   SET YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
			    	     , STL_NO              = :V_STL_NO
			    	     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
			    	     , MODIFIER            = :V_MODIFIER
			    	     , MOD_DDTT            = SYSDATE
			    	WHERE YD_STK_COL_GP        = :V_YD_STK_COL_GP
			    	*/
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updStklyrYdStkColGp";
			    	commDao.update(jrParam, queryId, logId, mthdNm, "TB_YD_STKLYR 초기화");
										
					/**********************************************************
					* 업무기준 : 차량 출발 시 저장위치 제원 야드L2로 전송
					**********************************************************/
					JDTORecord sndL2Msg = slabUtils.getParam(logId, mthdNm, sModifier);
					
    				sndL2Msg.setField("YD_INFO_SYNC_CD"		, "4"                         ); //야드정보동기화코드
    				sndL2Msg.setField("MSG_GP"				, "I"                         ); //전문구분
    				sndL2Msg.setField("YD_STK_COL_GP"    	, ydCarUdStopLoc);
    				sndL2Msg.setField("YD_STK_BED_NO"    	, "01"  );
					if ("".equals(sTrnEqpCd)) {
						sndL2Msg.setField("YD_CAR_USE_GP"    	, "G"   ); //L:구내운송, G:출하차량
						sndL2Msg.setField("CAR_NO"  			, sCarNo); //차량번호
						sndL2Msg.setField("CARD_NO"  			, sCardNo); //카드번호
					} else {
						sndL2Msg.setField("YD_CAR_USE_GP"    	, "L"   ); //L:구내운송, G:출하차량  (후판은 구내운송만 해당) 
						sndL2Msg.setField("TRN_EQP_CD"  		, sTrnEqpCd);	
					}
					sndL2Msg.setField("YD_CAR_ARRSTRT_STAT" , "S"); //A:도착, S:출발
					sndL2Msg.setField("YD_EQP_WRK_STAT"     , "L");
					
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L001_CarInfo", sndL2Msg));
				}
			}
			
			if ( !"".equals(ydPrepSchId) ) {
		    	jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
		    	jrParam.setField("YD_PREP_SCH_ID", ydPrepSchId);
		    	
		    	/*
		    	UPDATE TB_YD_PREPMTL
		    	   SET DEL_YN   = 'N'
		    	     , MODIFIER = :V_MODIFIER
		    	     , MOD_DDTT = SYSDATE
		    	 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
		    	*/
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updCarWrMgtPrepSchMtl";
		    	commDao.update(jrParam, queryId, logId, mthdNm, "TB_YD_PREPMTL 준비재료 복원");
		    	
		    	/* # 동일상차야드준비스케줄ID 초기화  - com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updPriYdPrepSchId
				UPDATE TB_YD_PREPSCH
				   SET PRI_YD_PREP_SCH_ID = NULL
				     , MODIFIER           = :V_MODIFIER
				     , MOD_DDTT           = SYSDATE
				 WHERE YD_GP  = 'D' -- 후판슬라브야드 대상
				   AND DEL_YN = 'N' -- 종료된 LOT는 제외
				   AND PRI_YD_PREP_SCH_ID LIKE :V_YD_PREP_SCH_ID || '%'
		    	 */
		    	queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updPriYdPrepSchId";
		    	commDao.update(jrParam, queryId, logId, mthdNm, "TB_YD_PREPSCH 동일상차야드준비스케줄ID 초기화");
		    	
		    	/*
		    	UPDATE TB_YD_PREPSCH
				   SET DEL_YN             = 'N'
				     , PRI_YD_PREP_SCH_ID = NULL           -- 동일상차야드준비스케쥴ID
				     , MODIFIER           = :V_MODIFIER
				     , MOD_DDTT           = SYSDATE
				 WHERE YD_PREP_SCH_ID     = :V_YD_PREP_SCH_ID
		    	*/
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updCarWrMgtPrepSch";
		    	commDao.update(jrParam, queryId, logId, mthdNm, "TB_YD_PREPSCH 준비스케줄 복원");
		    	
			}
			slabUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 소재차량도착Point요구(TSYDJ002)               (참고)TsInfoRegSBean.procMatlCarArrPntRequest ==> procMatlCarArrPntReq
	 * 2021.02.09 pys                                          
	 * 야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvTSYDJ002(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "슬라브소재차량도착Point요구[PSlabYdMvCarSeEJB.rcvTSYDJ002] < " + rcvMsg.getResultMsg(); 
		String logId = rcvMsg.getResultCode();
		
	    try{
	    	slabUtils.printLog(logId, methodNm, "S+");
	    	String msgId        	= slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID  -"TSYDJ002"
	    	
	    	String szMsg           		= "";
	    	String szTrnEqpCd    		= "";	//운송장비코드      (8)
	    	String szWlocCd       		= "";	//개소코드            (5)
	    	String szTrnWrkFullvoidGp   = "";	//운송작업영공구분 (1)
	    	String szYdMsgNm			= "";
	    	String sStackBayGp 			= "";
	    	String sYdPntCd				= "0000";
	    	String ydStkColActStat      = "";

			String sModifier 		= slabUtils.trim(rcvMsg.getFieldString("MODIFIER")); 			//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }	

			JDTORecord jrRtn  		= slabUtils.getParam(logId, methodNm, sModifier);
			JDTORecord jrParam  	= slabUtils.getParam(logId, methodNm, sModifier);   
			JDTORecord sndRecord	= slabUtils.getParam(logId, methodNm, sModifier);

			slabUtils.printParam(logId+"<<<<소재차량도착Point요구[TSYDJ002] 전문수신>>>>", rcvMsg);
			/*-------------------------------------------------------------------------------------------
			 *  * 소재차량도착Point요구  (TSYDJ002) 
			 *	1. 차량스케줄이 없는 경우 종료
			 *	2. 적치열 가용 포인트 조회
			 *	3. 차량 스케줄에 포인트 정보 갱신 
			 *	4. 소재차량 포인트 지시 송신(YDTSJ011)
			 *--------------------------------------------------------------------------------------------*/
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			szTrnEqpCd      		= slabUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 	//운송장비코드
			szWlocCd      			= slabUtils.trim(rcvMsg.getFieldString("WLOC_CD")); 	//개소코드
//			szPntDmdDt 				= slabUtils.trim(rcvMsg.getFieldString("PNT_DMD_DT"));  //포인트요구일시
			szTrnWrkFullvoidGp  	= slabUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분[상하차구분 E:공차상태(상차작업),F:영차(하차작업)]
			
			slabUtils.printLog(logId, "(수신 항목 값)장비:"+ szTrnEqpCd +" 개소:"+szWlocCd +" 운송작업영공구분:"+szTrnWrkFullvoidGp, "SL");
			
			//운송장비코드길이가 3자리 이상인지 확인 (substring 전 에러 체크)
			if (szTrnEqpCd.length() < 3 ) {
				szMsg = "운송장비코드 오류 [" + szTrnEqpCd + "] 운송장비 코드구분(PT/TR)정보가 없습니다.";
				slabUtils.printLog(logId, szMsg, "SL");

				sndRecord.setField("RTN_CD"	, "0");
				sndRecord.setField("RTN_MSG", szMsg);
				return sndRecord ;
			}
//	    	szTrnEqpGp = szTrnEqpCd.substring(1, 3);//PT/TR 구분

	    	/**********************************************************
			* 1. 개소코드가 정합성 체크 : 후판slab 여부 
			**********************************************************/
	    	if (!this.getSlabLocationInfo_02(szWlocCd)) {
				//throw new Exception("개소코드 오류 [" + szWLOC_CD + "]는 후판Slab 개소코드가 아닙니다!");
				szMsg = "개소코드 오류 [" + szWlocCd + "]는 후판SLAB 개소코드가 아닙니다!" ;
				slabUtils.printLog(logId, szMsg, "SL");
				
				sndRecord.setField("RTN_CD"	, "0");
				sndRecord.setField("RTN_MSG", szMsg);
				return sndRecord ;
			}			
	    	
	    	/**********************************************************
			* 2. 운송장비코드로 차량스케줄 조회
			**********************************************************/
			jrParam.setField("TRN_EQP_CD", szTrnEqpCd);
	    	/* # com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdCarschByTrnEqpCd
			SELECT *
			  FROM (
			        SELECT    YD_CAR_SCH_ID
			                , DEL_YN
			                , YD_EQP_ID
			                , YD_CAR_USE_GP
			                , CAR_NO
			                , TRN_EQP_CD
			                , CAR_KIND
			                , TRANS_EQUIPMENT_TYPE
			                , YD_EQP_WRK_STAT
			                , YD_WRK_PROG_STAT
			                , YD_EQP_WRK_SH
			                , YD_EQP_WRK_WT
			                , YD_STK_BED_TP
			                , SPOS_WLOC_CD
			                , ARR_WLOC_CD
			                , YD_CARLD_LEV_LOC
			                , YD_CARLD_LEV_DT
			                , YD_CARLD_PNT_WO_DT
			                , YD_PNT_CD1
			                , YD_PNT_CD2
			                , YD_CARLD_WRK_BOOK_ID
			                , YD_CARLD_SCH_REQ_GP
			                , YD_CARLD_STOP_LOC
			                , YD_CARLD_ARR_DT
			                , YD_CARLD_ST_DT
			                , YD_CARLD_CMPL_DT
			                , YD_CARLD_WRK_ACT_GP
			                , YD_CARLD_CHK_DT
			                , YD_CARUD_LEV_DT
			                , YD_CARUD_PNT_WO_DT
			                , NVL(YD_PNT_CD3, '0000') AS YD_PNT_CD3
			                , YD_PNT_CD4
			                , YD_CARUD_WRK_BOOK_ID
			                , YD_CARUD_STOP_LOC
			                , YD_CARUD_SCH_REQ_GP
			                , YD_CARUD_ARR_DT
			                , YD_CARUD_CHK_DT
			                , YD_CARUD_ST_DT
			                , YD_CARUD_CMPL_DT
			                , YD_CARUD_WRK_ACT_GP
			                , YD_TRN_WRK_DELY_CD
			                , CARD_NO
			                , YD_CAR_PROG_STAT
			                , FRTOMOVE_PLANT_GP
			                , PROC_TO
			                , RENTPROC_CD
			                , YD_FRTOMOVE_YD_GP
			                , YD_FRTOMOVE_BAY_GP
			                , URGENT_FRTOMOVE_WORD_GP
			                , DEST_TEL_NO
			                , YD_DLVRDD_RULE_DD
			                , SHIPASSIGN_WORD_DATE
			                , SHIPASSIGN_WORD_SEQNO
			                , SHIP_CD
			                , SHIP_NAME
			                , RSHP_HOLD_NO
			                , BERTH_NO
			                , SAILNO
			                , YD_CAR_WRK_GP
			                , TRANS_ORD_DATE
			                , TRANS_ORD_SEQNO
			                , YD_BAYIN_WO_SEQ
			                , YD_CAR_RCPT_CHK_YN
			                , YD_CAR_ISSUE_CHK_YN
			                , YD_CAR_RCPT_CHECKER
			                , YD_CAR_ISSUE_CHECKER
			                , IF_SEQ_NO
			                , TEL_NO
			                , CMBN_CARLD_YN
			                , WAIT_ARR_DDTT
			                , WAIT_ARR_GP
			                , YD_MSG_NM
			                , FRTOMOVE_WORD_NO
			                , DRIVER_NAME
			          FROM TB_YD_CARSCH A
			         WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			           AND DEL_YN     ='N'
			         ORDER BY YD_CAR_SCH_ID DESC
			                , YD_CARUD_CMPL_DT DESC
			       ) A
			 WHERE ROWNUM <= 1
	    	 */
			String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdCarschByTrnEqpCd";
	    	JDTORecordSet jsCarSch = commDao.select(jrParam, queryId, logId, methodNm, "차량스케줄 조회");
	    	
	    	if (jsCarSch.size() <= 0) {
	    		/**********************************************************
				* 2-1. 차량스케줄이 존재하지 않으면 종료
				**********************************************************/
	    		szMsg = "해당 차량 "+ szTrnEqpCd +"에 차량스케줄이 없습니다.";
				sndRecord.setField("RTN_CD"	, "0");
				sndRecord.setField("RTN_MSG", szMsg);
				return jrRtn ;
	    	}
    		//String ydCarSchId    = jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID");
    		//String ydCarProgStat = jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT");
    		
	    	String ydStkColGp    = "";
			//***************************************************************************	
			// 3.장비코드와 개소코드로 적치열을 조회하여 포인트 지시가 이미 전송된 건인지 체크 : [ 포인트 재 요구 시] 상차예약정지위치 검색 
			// 3-1.기처리된 건이면  다음 point 전송   예) DAPT01 -->  DAPT02로  배정    
	    	//***************************************************************************	
	    	
			jrParam.setField("WLOC_CD"		, szWlocCd);
			jrParam.setField("CAR_CARD_NO"	, szTrnEqpCd);
			/*  
			    SELECT  YD_STK_COL_GP 
			           ,YD_PNT_CD
			           ,TRN_EQP_CD
			           ,YD_STK_COL_ACT_STAT
			           ,YD_GP
			           ,YD_BAY_GP
			    FROM    TB_YD_CARPOINT 
			    WHERE   WLOC_CD    = :V_WLOC_CD
			    AND     TRN_EQP_CD = :V_CAR_CARD_NO
			    ORDER BY YD_STK_COL_GP
			 */
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getListloadStoppoint2";
			JDTORecordSet loadPointList = commDao.select(jrParam, queryId, logId, methodNm, "장비코드로 포인트 재 요구 시 상차예약정지위치 검색");
			if (loadPointList.size() > 0) {	// 해당 차량의 point 지시가 기 처리되었는지 확인
				ydStkColActStat = slabUtils.trim(loadPointList.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));
				if(PSlabYdConstant.YD_STK_COL_ACT_STAT_R.equals(ydStkColActStat)) {				// 이미 예약중으로 잡혀있는 상태에서 point 지시 요구가 내려온 case
					szMsg="해당 차량"+szTrnEqpCd+"은  "+szWlocCd+ "에 이미 예약이 잡혀 있습니다. (다른 차량으로 포인트 지시 재송신 요청)";
					slabUtils.printLog(logId, szMsg, "SL");
					
					ydStkColGp  = slabUtils.trim(loadPointList.getRecord(0).getFieldString("YD_STK_COL_GP"));		//DBPT01
					sStackBayGp = slabUtils.trim(loadPointList.getRecord(0).getFieldString("YD_BAY_GP"));			//"B"
					sYdPntCd 	= slabUtils.nvl (loadPointList.getRecord(0).getFieldString("YD_PNT_CD"),"0000");	    //1B01
					slabUtils.printLog(logId, "YD_STK_COL_ACT_STAT:"+ydStkColActStat +"STK_COL_GP:"+ ydStkColGp 
							                 +"YD_BAY_GP:"+ sStackBayGp +"YD_PNT_CD:"+ sYdPntCd, "SL");
					
					slabUtils.printLog(logId, methodNm, "S-");
					sndRecord.setField("RTN_CD"	, "0");
					sndRecord.setField("RTN_MSG", szMsg);
					return sndRecord;
				} else if(PSlabYdConstant.YD_STK_COL_ACT_STAT_L.equals(ydStkColActStat)) {		// 차량이 도착상태에서 point 지시 요구가 내려온 case
					szMsg="["+methodNm+"] 이미 도착한 상태인 경우   (" + PSlabYdConstant.YD_STK_COL_ACT_STAT_L + ")";
					slabUtils.printLog(logId, szMsg, "SL");
					
					slabUtils.printLog(logId, methodNm, "S-");
					sndRecord.setField("RTN_CD"	, "0");
					sndRecord.setField("RTN_MSG", szMsg);
					return sndRecord;
				} else if(PSlabYdConstant.YD_STK_COL_ACT_STAT_C.equals(ydStkColActStat)) { 		// 해당 point가 사용가능일때(사용자가 차량화면에서 [point개폐]기능으로 바꾼 case)
					//야드적치열활성상태 : "C" -> 차량에 입동지시I/F만 다시 내려준다.
					slabUtils.printLog(logId, "YD_STK_COL_ACT_STAT:"+ydStkColActStat +"[c]", "SL");
					
				} else {																		// 해당 point가 사용불가일때(사용자가 차량화면에서 [point개폐]기능으로 바꾼 case)
					szMsg ="["+methodNm+"] 야드적치열활성상태 : " + ydStkColActStat+ "(  N:사용불가)";
					slabUtils.printLog(logId, szMsg, "SL");
					
					slabUtils.printLog(logId, methodNm, "S-");
					sndRecord.setField("RTN_CD"	, "0");
					sndRecord.setField("RTN_MSG", szMsg);
					return sndRecord;
				}
				
			} else {
					szMsg="["+methodNm+"] 영공구분:"+ szTrnWrkFullvoidGp+ " <E:공차상태(상차작업),F:영차(하차작업)> "+ "개소코드:"+szWlocCd;
					slabUtils.printLog(logId, szMsg, "SL");
					
					/**************************************
					 * 가용 포인트 검색
					 **************************************/
					jrParam.setField("WLOC_CD"		, szWlocCd);					
	
					/* #com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getListloadStoppoint02 
						SELECT YD_STK_COL_GP  
						     , YD_PNT_CD
						  FROM TB_YD_STKCOL 
						 WHERE DEL_YN              = 'N'
						   AND WLOC_CD             = :V_WLOC_CD
						   AND YD_STK_COL_ACT_STAT = 'C' 
						   AND TRN_EQP_CD   IS NULL 
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getListloadStoppoint02";
					JDTORecordSet rsResult = commDao.select(jrParam, queryId, logId, methodNm, "SLAB야드 상차정지위치 조회");
					if (rsResult.size() < 1) {
						/**************************************
						 * 가용 포인트가 없는 경우
						 * YDTSJ011 전문 송신후 종료
						 **************************************/
					
						szMsg="["+methodNm+"] 하차정지위치 검색 - 검색대상재 동에 있는 차량위치정보 찾지 못함 ";
						slabUtils.printLog(logId, szMsg, "SL");
						
						/* #com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getListloadStoppoint03 
						SELECT YD_STK_COL_GP  
						     , YD_PNT_CD
						     , TRN_EQP_CD
						  FROM TB_YD_STKCOL 
						 WHERE DEL_YN    = 'N'
						   AND WLOC_CD   = :V_WLOC_CD
						  ORDER BY CASE WHEN TRN_EQP_CD IS NOT NULL     THEN 1 
						           ELSE 2 END
						         , CASE WHEN YD_STK_COL_ACT_STAT <> 'N' THEN 1
						           ELSE 2 END
						         , YD_STK_COL_GP
						 */
						queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getListloadStoppoint03";
						JDTORecordSet jrResult = commDao.select(jrParam, queryId, logId, methodNm, "SLAB야드 상차정지위치 조회");
						if (jrResult.size() > 0) {
							ydStkColGp = slabUtils.trim(jrResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
						} else {
							if("DWY22".equals(szWlocCd)) {
								ydStkColGp = "DBPT01";
							} else {
								ydStkColGp = "DAPT01";
							}	
						}
						
						if("E".equals(szTrnWrkFullvoidGp)) {
							jrParam.setField("YD_CARLD_STOP_LOC", ydStkColGp		); //야드하차정지위치
						} else {
							jrParam.setField("YD_CARUD_STOP_LOC", ydStkColGp		); //야드하차정지위치
						}	
							
						/* #com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updCarSchLdUdByTrnEqpCd02
						UPDATE  TB_YD_CARSCH A
						   SET  MODIFIER             = :V_MODIFIER
						       ,MOD_DDTT             = SYSDATE
						       ,YD_CARLD_STOP_LOC    = NVL(:V_YD_CARLD_STOP_LOC    ,YD_CARLD_STOP_LOC   )
						       ,YD_CARUD_STOP_LOC    = NVL(:V_YD_CARUD_STOP_LOC    ,YD_CARUD_STOP_LOC   )
						WHERE   TRN_EQP_CD           = :V_TRN_EQP_CD
						  AND   DEL_YN               = 'N'
						  AND   A.YD_CAR_SCH_ID      = (
						                              SELECT MAX(YD_CAR_SCH_ID) 
						                                FROM TB_YD_CARSCH B
						                               WHERE A.TRN_EQP_CD=B.TRN_EQP_CD
						                                 AND B.DEL_YN='N'
						                             )
						
	                     */
						queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updCarSchLdUdByTrnEqpCd02";
						commDao.update(jrParam, queryId, logId, methodNm, "차량스케줄 상차출발로 UPDATE(2)");
						

						
						//0000 포인트지시 전송
						sndRecord = slabUtils.addSndData(sndRecord, this.makeYDTSJ011(szTrnEqpCd, szWlocCd, "0000", szYdMsgNm, sStackBayGp, logId));
						
						slabUtils.printLog(logId, methodNm, "S-");
						sndRecord.setField("RTN_CD"	, "0");
						sndRecord.setField("RTN_MSG", szMsg);
						return sndRecord;
					}
					ydStkColGp = slabUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
					sYdPntCd   = slabUtils.nvl (rsResult.getRecord(0).getFieldString("YD_PNT_CD"),"0000");
					szMsg="["+methodNm+"] 상하차정지위치 검색 - 검색대상재 동에 있는 차량위치정보 검색 결과 : " + ydStkColGp + "," + sYdPntCd;
					slabUtils.printLog(logId, szMsg, "SL");
					
	    		    // 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
					if("F".equals(szTrnWrkFullvoidGp)) {
						jrParam.setField("YD_STK_COL_ACT_STAT"	, "R"); //적재 상태 =U:하차작업상태  , L:상차작업중 , S:작업등록상태 , W:대기상태
					} else {
						jrParam.setField("YD_STK_COL_ACT_STAT"	, "R"); //R:예약중 
					}
					jrParam.setField("TRN_EQP_CD"			, szTrnEqpCd);
					jrParam.setField("CAR_CARD_NO"			, szTrnEqpCd); 	
					jrParam.setField("YD_CAR_USE_GP"		, "L"); //구내운송  
					jrParam.setField("YD_STK_COL_GP"		, ydStkColGp);
					/*
					UPDATE TB_YD_STKCOL
					   SET YD_STK_COL_ACT_STAT = :V_STACK_STAT
					      ,TRN_EQP_CD    = DECODE(:V_YD_CAR_USE_GP, 'L',  :V_TRN_EQP_CD, '')  --※수정 
					      ,YD_CAR_USE_GP =        :V_YD_CAR_USE_GP 
					      ,MODIFIER      = :V_MODIFIER
					      ,MOD_DDTT      = SYSDATE
					 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
    					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updateEquipcolStat2";
					commDao.update(jrParam, queryId, logId, methodNm, "TB_YD_STKCOL 예약정보등록");

					//차량포인트 통합관리
					this.YdCarPointinforeg(	 "3"	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
											,""		,szTrnEqpCd	,ydStkColGp	,"",""	
											,"R"	,logId, sModifier);
					//**********************************************************************************	
					
					if("F".equals(szTrnWrkFullvoidGp)) {
						jrParam.setField("YD_CAR_PROG_STAT"	, PSlabYdConstant.YD_CAR_PROG_STAT_A ); //차량진행상태 (A:하차출발)
						jrParam.setField("YD_EQP_WRK_STAT"	, "L"			    ); //야드설비작업상태 (L:영차)
						jrParam.setField("ARR_WLOC_CD"		, szWlocCd		    ); //착지개소코드
						jrParam.setField("YD_CARUD_STOP_LOC", ydStkColGp	    ); //야드상차정지위치
						jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, ""			); //야드상차작업예약ID
					} else {
						jrParam.setField("YD_CAR_PROG_STAT"	, PSlabYdConstant.YD_CAR_PROG_STAT_1 ); //차량진행상태 (1:상차출발)
						jrParam.setField("YD_EQP_WRK_STAT"	, "U"				); //야드설비작업상태 (U:공차)
						jrParam.setField("SPOS_WLOC_CD"		, szWlocCd			); //발지개소코드(상차지)
						jrParam.setField("YD_CARLD_STOP_LOC", ydStkColGp		); //야드하차정지위치
						jrParam.setField("YD_CARLD_WRK_BOOK_ID"	, ""			); //야드상차작업예약ID
					}
					jrParam.setField("YD_CAR_USE_GP"		, "L"				); //야드차량사용구분 (L:구내운송, G:출하차량 )
					jrParam.setField("YD_PNT_CD"			, sYdPntCd			); //야드상차포인트코드(발지)
					jrParam.setField("TRN_EQP_CD"			, szTrnEqpCd		); //운송장비코드
					
					/* #com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updCarSchLdUdByTrnEqpCd
					UPDATE  TB_YD_CARSCH A
					   SET  MODIFIER             = :V_MODIFIER
					       ,MOD_DDTT             = SYSDATE
					       ,YD_CAR_PROG_STAT     = NVL(:V_YD_CAR_PROG_STAT     ,YD_CAR_PROG_STAT    )
					       ,YD_EQP_WRK_STAT      = NVL(:V_YD_EQP_WRK_STAT      ,YD_EQP_WRK_STAT     )
					       ,ARR_WLOC_CD          = NVL(:V_ARR_WLOC_CD          ,ARR_WLOC_CD         )
					       ,YD_CARUD_STOP_LOC    = NVL(:V_YD_CARUD_STOP_LOC    ,YD_CARUD_STOP_LOC   )
					       ,YD_CARUD_LEV_DT      = DECODE(:V_YD_CARUD_LEV_DT   , YD_CARUD_LEV_DT    )
					       ,YD_CARUD_PNT_WO_DT   = DECODE(:V_YD_CARUD_PNT_WO_DT, YD_CARUD_PNT_WO_DT )
					       ,YD_PNT_CD3           = DECODE(:V_YD_EQP_WRK_STAT   , 'L', :V_YD_PNT_CD, '' )  --영차
					       ,SPOS_WLOC_CD         = NVL(:V_SPOS_WLOC_CD         ,SPOS_WLOC_CD        )
					       ,YD_CARLD_STOP_LOC    = NVL(:V_YD_CARLD_STOP_LOC    ,YD_CARLD_STOP_LOC   )
					       ,YD_CARLD_LEV_DT      = DECODE(:V_YD_CARLD_LEV_DT   , YD_CARLD_LEV_DT   )
					       ,YD_CARLD_PNT_WO_DT   = DECODE(:V_YD_EQP_WRK_STAT   , 'U', SYSDATE, ''   )
					       ,YD_PNT_CD1           = DECODE(:V_YD_EQP_WRK_STAT   , 'U', :V_YD_PNT_CD, '')  --공차 
					       ,YD_CAR_USE_GP        = NVL(:V_YD_CAR_USE_GP        ,YD_CAR_USE_GP       )
					WHERE   TRN_EQP_CD           = :V_TRN_EQP_CD
					  AND   DEL_YN               = 'N'
					  AND   A.YD_CAR_SCH_ID      = (
					                              SELECT MAX(YD_CAR_SCH_ID) 
					                                FROM TB_YD_CARSCH B
					                               WHERE A.TRN_EQP_CD=B.TRN_EQP_CD
					                                 AND B.DEL_YN='N'
					                             )
                     */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updCarSchLdUdByTrnEqpCd";
					commDao.update(jrParam, queryId, logId, methodNm, "차량스케줄 상차출발로 UPDATE ");
			}
			
			//포인트지시 전송
			sndRecord = slabUtils.addSndData(sndRecord, this.makeYDTSJ011(szTrnEqpCd, szWlocCd, sYdPntCd, szYdMsgNm, sStackBayGp, logId));
			
			slabUtils.printLog(logId, methodNm, "S-");
			return sndRecord;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of rcvTSYDJ002()

	
	/**
	 * [A] 오퍼레이션명 : 포인트지시(YDTSJ011) 전문 생성 (목적동 포함)
	 * 야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord makeYDTSJ011(	  String szTrnEqpCd
									, String szWlocCd
									, String szYdPntCd
									, String szYdMsgNm
									, String szYdBayGp 
									, String logId)throws DAOException  {
		String methodNm 			= "포인트지시(YDTSJ011)전문 생성[PSlabYdMvCarSeEJB.makeYDTSJ011] " ; //gdReq.getNavigateValue()
		
	    try{
	    	slabUtils.printLog(logId, methodNm, "S+");
			
			//포인트지시 메세지 전송
	    	JDTORecord jrTemp			= JDTORecordFactory.getInstance().create();
			jrTemp.setResultCode(logId);	//Log ID
			jrTemp.setResultMsg(methodNm);	//Log Method Name
			jrTemp.setField("JMS_TC_CD"				, "YDTSJ011");					//소재차량 POINT 지시
			jrTemp.setField("JMS_TC_CREATE_DDTT"	, slabUtils.getDateTime14()); 	//JMSTC생성일시
			//--------------------------------------------------------------------------------------------
			jrTemp.setField("TRN_EQP_CD"			, szTrnEqpCd);					//운송장비코드
			jrTemp.setField("WLOC_CD"				, szWlocCd);					//개소코드
			jrTemp.setField("YD_PNT_CD"				, slabUtils.nvl(szYdPntCd,"0000")); //1B01 (현위치의 동구분 + 통로구분(Zone) + Point-NO)
			jrTemp.setField("PNT_WO_GP"				, "A");							//포인트지시구분       (1)
			jrTemp.setField("PNT_WO_DT"				, slabUtils.getDateTime14());   //포인트지시DLFWK(14)
			if("".equals(szYdBayGp)) {
				jrTemp.setField("YD_BAY_GP"			, slabUtils.nvl(szYdPntCd.substring(1,2),"")); //목적동 추가
			} else {
				jrTemp.setField("YD_BAY_GP"			, slabUtils.nvl(szYdBayGp,"")); //목적동 추가
			}
			//--------------------------------------------------------------------------------------------
			jrTemp.setField("YD_MSG_NM"				, szYdMsgNm);
			jrTemp.setField("TRN_WRK_MTL_GP"		, "S"); //운송작업재료구분 (C:C제품,H:열연C소재,S:SLAB,L:냉연C소재)
			
			slabUtils.printLog(logId, methodNm, "S-");
			return jrTemp;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} // 


	/**
	 * 오퍼레이션명 : 차량스케쥴 생성   :: [상차LOT 편성은 수기 처리, 상차도 등 상태 코드 변경 사항만 반영함.]  
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord chkWlocCdLotComp(JDTORecord rcvMsg)throws JDTOException  {
		String mthdNm = "상차Lot편성[PSlabYdMvCarSeEJB.chkWlocCdLotComp] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
	    try {
	    	slabUtils.printLog(logId, mthdNm, "S+");
	    	slabUtils.printParam(logId, rcvMsg);
	    	
	    	String sTrnEqpCd            = slabUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"             ));
	    	String sWlocCd              = slabUtils.trim(rcvMsg.getFieldString("WLOC_CD"                ));
	    	String ydCarldLevLoc        = slabUtils.trim(rcvMsg.getFieldString("YD_CARLD_LEV_LOC"       ));
	    	
	    	String sModifier            = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
	    	String sMsg                 = "";
	    	
	    	JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
	    	JDTORecord jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
	    		
	    	if ("DWY22".equals(sWlocCd) || "DKY21".equals(sWlocCd) ) {

	    		//상차LOT편성 호출
			    	sMsg = "[상차Lot편성 호출처리] 개소코드["+sWlocCd+"]에 차량스케줄 확인";
			    	slabUtils.printLog(logId, sMsg, "SL");

					String sYdCarSchId = commDao.getSeqId(logId, mthdNm, "CarSch");

			    	jrParam.setField("YD_CAR_SCH_ID"   , sYdCarSchId);
					jrParam.setField("YD_EQP_WRK_STAT" , "U"                       );		//야드설비작업상태
					jrParam.setField("YD_EQP_ID"       , PSlabYdConstant.YD_TS_CAR_EQP_ID); //야드설비ID: XXPT01
					jrParam.setField("TRN_EQP_CD"      , sTrnEqpCd                 );		//운송장비코드
					jrParam.setField("YD_CAR_USE_GP"   , PSlabYdConstant.YD_CAR_USE_GP_TS);	//차량사용구분:L
					jrParam.setField("SPOS_WLOC_CD"    , sWlocCd                   );		//발지개소코드
					jrParam.setField("YD_CARLD_LEV_LOC", ydCarldLevLoc             );		//야드상차출발위치
					jrParam.setField("YD_CARLD_LEV_DT" , slabUtils.getDateTime14() );		//상차출발일시
					jrParam.setField("YD_BAYIN_WO_SEQ" , PSlabYdConstant.YD_BAYIN_WO_SEQ_DEFAULT);//입동지시순번 - 기본값으로 설정(9)
					jrParam.setField("YD_CAR_PROG_STAT", "1"                       );		//상차출발상태
					
					//차량 스케줄 INSERT  (TB_YD_CARSCH )
					/*
                     com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.insYdCarsch 
					INSERT INTO USRYDA.TB_YD_CARSCH
					(	   YD_CAR_SCH_ID
					     , REGISTER
					     , REG_DDTT
					     , MODIFIER
					     , MOD_DDTT
					     , DEL_YN
					     , YD_EQP_ID
					     , YD_CAR_USE_GP
					     , CAR_NO
					     , TRN_EQP_CD
					     , CAR_KIND
					     , YD_EQP_WRK_STAT
					     , SPOS_WLOC_CD
					     , ARR_WLOC_CD
					     , YD_CARLD_LEV_LOC
					     , YD_CARLD_LEV_DT
					     , YD_CARUD_LEV_DT
					     , YD_PNT_CD1
					     , YD_PNT_CD3
					     , YD_CARLD_STOP_LOC
					     , YD_CARUD_STOP_LOC
					     , CARD_NO
					     , YD_CAR_PROG_STAT
					     , YD_CAR_WRK_GP
					     , TRANS_ORD_DATE
					     , TRANS_ORD_SEQNO
					     , YD_BAYIN_WO_SEQ
					     , TEL_NO
					     , CMBN_CARLD_YN
					     , WAIT_ARR_DDTT
					     , WAIT_ARR_GP
					     , TRANS_EQUIPMENT_TYPE
					     , FRTOMOVE_WORD_NO
					     , DRIVER_NAME
					) VALUES (
					       :V_YD_CAR_SCH_ID
					     , :V_MODIFIER
					     , SYSDATE
					     , :V_MODIFIER
					     , SYSDATE
					     , 'N'
					     , :V_YD_EQP_ID
					     , :V_YD_CAR_USE_GP
					     , :V_CAR_NO
					     , :V_TRN_EQP_CD
					     , :V_CAR_KIND
					     , :V_YD_EQP_WRK_STAT
					     , :V_SPOS_WLOC_CD
					     , :V_ARR_WLOC_CD              --
					     , :V_YD_CARLD_LEV_LOC
					     , TO_DATE(:V_YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')
					     , TO_DATE(:V_YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')
					     , NVL(:V_YD_PNT_CD1,'0000')
					     , NVL(:V_YD_PNT_CD3,'0000')
					     , :V_YD_CARLD_STOP_LOC
					     , :V_YD_CARUD_STOP_LOC        --
					     , :V_CARD_NO
					     , :V_YD_CAR_PROG_STAT
					     , :V_YD_CAR_WRK_GP
					     , :V_TRANS_ORD_DATE
					     , :V_TRANS_ORD_SEQNO
					     , :V_YD_BAYIN_WO_SEQ
					     , :V_TEL_NO
					     , :V_CMBN_CARLD_YN
					     , :V_WAIT_ARR_DDTT
					     , :V_WAIT_ARR_GP     
					     , :V_TRANS_EQUIPMENT_TYPE
					     , :V_FRTOMOVE_WORD_NO
					     , :V_DRIVER_NAME
					)
					 */
					String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.insYdCarsch";
					commDao.insert(jrParam, queryId, logId, mthdNm, "차량스케줄 생성");
					
					//차량도착Point요구모듈호출
					jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("TRN_EQP_CD"         , sTrnEqpCd                );		//운송장비코드
					jrParam.setField("WLOC_CD"            , sWlocCd                  );
					jrParam.setField("TRN_WRK_FULLVOID_GP", "E"                      );
					jrParam.setField("PNT_DMD_DT"         , slabUtils.getDateTime14());
					slabUtils.printLog(logId, "[#002]WLOC_CD:"+sWlocCd+ " TRN_EQP_CD:"+sTrnEqpCd, "SL");
					JDTORecord jrTsYd = this.rcvTSYDJ002(jrParam);
					jrRtn = slabUtils.addSndData(jrRtn, jrTsYd);
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
	 * [A] 오퍼레이션명 : 후판 Slab야드 소재차량도착(TSYDJ003) 수신처리
	 * 야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvTSYDJ003(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "슬라브소재차량도착수신처리[PSlabYdMvCarSeEJB.rcvTSYDJ003] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
	    try{
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량도착(TSYDJ003) 수신처리 START!", "SL");
			slabUtils.printParam(logId + "후판Slab_소재차량_도착_수신처리 ", rcvMsg);
			
	    	//수신항목 변수 저장
			String sMsg     = "";
			String sTrnEqpCd      	 = slabUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 		//운송장비코드
			String sTrnWrkFullvoidGp = slabUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP"));//운송작업영공구분
			String sArrWlocCd      	 = slabUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 		//착지개소코드
			String sArrYdPntCd       = slabUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 		//착지야드포인트코드
			String msgId		     = slabUtils.nvl (slabUtils.getMsgId(rcvMsg),"TSYDJ003"); 		//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier 	     = slabUtils.trim(rcvMsg.getFieldString("MODIFIER")); 			//수정자(Backup Only)
 			String sTrnEqpStkCapa    = slabUtils.nvl (rcvMsg.getFieldString("TRN_EQP_STK_CAPA"), PSlabYdConstant.YD_CAR_BED_WT_MAX_DEFAULT);
			if ("".equals(modifier)) { modifier = msgId; }
			
			String ydStkColGp  	     = "";
			String ydGp	       	     = "";
			String ydBayGp     	     = "";
			String ydCarSchId  	     = "";
			String ydSchCd	   	     = "";
			String ydWbookId   	     = "", ydWbookId1   = ""; /*하차1번*/
			String ydEqpWrkStat	     = "";
			String ydFrmYn    	     = "N";
			String sYDYDJ401_CALL_YN = "Y"; /* 무인작업 중일 때 스케쥴 기동불가(형상없이 스케줄 기동되면 위험) */
			
			slabUtils.printLog(logId, "▶ 필수 파라미터 확인", "SL");
			slabUtils.printLog(logId, "▷ 운송장비코드		: "	+ sTrnEqpCd			, "SL");
			slabUtils.printLog(logId, "▷ 착지개소코드		: "	+ sArrWlocCd 		, "SL");
			slabUtils.printLog(logId, "▷ 착지야드포인트코드	: "	+ sArrYdPntCd 	    , "SL");
			slabUtils.printLog(logId, "▷ 운송작업영공구분	: "	+ sTrnWrkFullvoidGp , "SL");
			slabUtils.printLog(logId, "▷  msgId		: "	+ msgId				, "SL");
			
			JDTORecord jrRtn  		= slabUtils.getParam(logId, methodNm, modifier);
			JDTORecord jrParam		= slabUtils.getParam(logId, methodNm, modifier);	//Query 실행시 파라메터 전달용 JDTORecord 
			
	    	//착지개소코드가 후판Slab 개소코드인지 검사한다.
			slabUtils.printLog(logId, "▶ 착지개소코드가 후판Slab 개소코드인지  확인합니다.", "SL");
			slabUtils.printLog(logId, "▷ 착지개소코드 :" + sArrWlocCd, "SL");
			if (!getSlabLocationInfo_02(sArrWlocCd)) {
	    		//throw new Exception("개소코드 오류 [" + szArrWlocCd + "]는 후판Slab 개소코드가 아닙니다!");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "개소코드 오류 [" + sArrWlocCd + "]는 후판Slab 개소코드가 아닙니다!");
				return 	jrRtn;
			}			


	    	//도착 포인트코드가 대기장(1Z99)인 경우 도착처리 안함
			slabUtils.printLog(logId, "▶ 도착 포인트코드가 대기장(1Z99)인 경우 도착처리로 지정하지 않습니다.", "SL");
			slabUtils.printLog(logId, "▷ 착지야드포인트코드 : " + sArrYdPntCd, "SL");
	    	if ("1Z99".equals(sArrYdPntCd)) {
				//throw new Exception("도착포인트코드가 [1Z99]대기장으로 도착처리 안함.");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "도착포인트코드가 [1Z99]대기장으로 도착처리 안함.");
				return 	jrRtn;
	    	}
	    	
	    	slabUtils.printLog(logId, "▶ 도착 포인트(적치열) 상태가 사용중인지 확인합니다.", "SL");
	    	slabUtils.printLog(logId, "▷ 착지야드포인트코드	: "	+ sArrYdPntCd, "SL");
	    	
	    	jrParam.setField("WLOC_CD"		, sArrWlocCd ); 
	    	jrParam.setField("YD_PNT_CD"	, sArrYdPntCd ); //	    	
	    	
	    	//TB_YD_STKCOL 에서 사용중 "L" 상태이면 도착 처리 불가 
	    	slabUtils.printLog(logId, "YD_STK_COL_GP" + sArrWlocCd, "SL");
	    	/* 
	    	com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getListStkColgp
	    
	    	SELECT YD_STK_COL_GP
	    	     , YD_STK_COL_ACT_STAT --L:사용중  
	    	     , TRN_EQP_CD 
	    	  FROM TB_YD_STKCOL
	    	 WHERE WLOC_CD = :V_WLOC_CD
	    	   AND YD_PNT_CD = :V_YD_PNT_CD 
	    	--   AND YD_EQP_GP = 'PT'
	    	*/	
	    	
	    	JDTORecordSet jsStkCol = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getListStkColgp", logId, methodNm, "차량포인트 체크  "); 
			if (jsStkCol.size() <= 0) {
				sMsg = "착지개소코드 [" + sArrWlocCd + "] 는  없는 위치입니다.";
				throw new Exception("착지개소코드 [" + sArrWlocCd + "] 는 없는 위치입니다.");	

			} else {
				ydStkColGp			= slabUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_GP"));

				String sYdStkColActStat2 = slabUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"  ));
				String sTrnEqpCd2        = slabUtils.trim(jsStkCol.getRecord(0).getFieldString("TRN_EQP_CD"  ));
				if(PSlabYdConstant.YD_STK_COL_ACT_STAT_L.equals(sYdStkColActStat2) ) {
					if(!sTrnEqpCd2.equals(sTrnEqpCd )) {   //동일차량이 아니면 
						throw new Exception("착지개소 [" + sArrWlocCd + "]에 이미 "+ sTrnEqpCd2+ "장비가 점유하고 있습니다.");	
					} else {
						throw new Exception("착지개소 [" + sArrWlocCd + "]는 이미 사용중인 상태 입니다.");
						// 재작업에 대한 사항 
					}
				}
				if(!"".equals(ydStkColGp)) {
					ydGp	= ydStkColGp.substring(0, 1);
					ydBayGp  = ydStkColGp.substring(1, 2);
					slabUtils.printLog(logId, "YD_STK_COL_GP:"+ydStkColGp + " Bay_Gp:" +  ydBayGp , "SL"); 
				} else {
					slabUtils.printLog(logId, "YD_STK_COL_GP:"+ydStkColGp +"(data확인!) ", "SL"); 
				}
				slabUtils.printLog(logId, "▶ 도착 포인트(적치열) 상태 이상없음. ", "SL");
			}
			
			slabUtils.printLog(logId, "▶ 도착 포인트(차량포인트) 상태가 사용중인지 확인합니다.", "SL");
			slabUtils.printLog(logId, "▷ 착지 개소코드		: "	+ sArrWlocCd 		, "SL");
	    	slabUtils.printLog(logId, "▷ 착지 야드포인트코드	: "	+ sArrYdPntCd		, "SL");
	    	/* 차량포인트(TB_YD_CARPOINT)를 개소코드와 야드포인트로 조회하여 도착처리 운송장비 코드와 동일한지 체크한다.      
	    	 * 
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
	    	      ,YD_SPAN_FROM
	    	      ,YD_SPAN_TO
	    	      ,YD_STK_COL_GP2
	    	      ,YD_FRM_YN
	    	FROM   TB_YD_CARPOINT
	    	WHERE  WLOC_CD   = :V_WLOC_CD
	    	AND    YD_PNT_CD = :V_YD_PNT_CD 
	    	*/
			jrParam.setField("WLOC_CD"	, sArrWlocCd); 	
			jrParam.setField("YD_PNT_CD", sArrYdPntCd); 
			
			String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarPointChk";
			JDTORecordSet jsCarPnt = commDao.select(jrParam, queryId, logId, methodNm, "차량포인트 체크  "); 
			if (jsCarPnt.size() <= 0) {
				sMsg = "착지개소코드 [" + sArrWlocCd + "], 착지야드포인트코드 [" + sArrYdPntCd + "] 는 차량포인트에 없는 위치입니다.";
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				return 	jrRtn;
				//throw new Exception(sMsg);	
			} else {
				slabUtils.printLog(logId, "YD_STK_COL_ACT_STAT:"+ jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT") , "SL");

				String sTrnEqpCdPnt        	= jsCarPnt.getRecord(0).getFieldString("TRN_EQP_CD");	// 운송장비코드
				ydFrmYn        				= jsCarPnt.getRecord(0).getFieldString("YD_FRM_YN");	// 야드형상여부
				
				slabUtils.printLog(logId, "▶ 활성상태 코드 값 [C]Close(비활성화)[L]활성 [N]불가 [R]예약", "SL");
				slabUtils.printLog(logId, "▷ 차량포인트 활성상태 : " + jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"), "SL");
				
				if ("R".equals(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"))) {
					
					slabUtils.printLog(logId, "▶ 도착 차량포인트("+ sArrYdPntCd + ")가 현재 예약중입니다. 예약중인 차량과 동일 차량인지 확인합니다.", "SL");
					slabUtils.printLog(logId, "▷ 예약중인 차량 운송장비코드 : " + sTrnEqpCdPnt	, "SL");
					slabUtils.printLog(logId, "▷ 도착처리 차량 운송장비코드 : " + sTrnEqpCd		, "SL");
					
					//예약일 경우 입력받은 운송장비코드와 동일한지 체크
					if(!sTrnEqpCd.equals(sTrnEqpCdPnt)) {
						msgId = "착지개소코드 [" + sArrWlocCd + "], 착지야드포인트코드 [" + sArrYdPntCd + "] 는 " + sTrnEqpCdPnt  + " 로 예약되어 있는데 " + sTrnEqpCd + " 로 도착처리 수신되었습니다.";
						slabUtils.printLog(logId, ""+msgId, "SL");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", msgId);
						return 	jrRtn;
					}

				} else if ("L".equals(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"))) {
					//사용중일경우 에러 처리
					sMsg = "착지개소코드 [" + sArrWlocCd + "], 착지야드포인트코드 [" + sArrYdPntCd + "] 는  이미 " + jsCarPnt.getRecord(0).getFieldString("TRN_EQP_CD") + " 가 입동되어 있는 위치입니다.(L)";
					slabUtils.printLog(logId, ""+sMsg, "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sMsg);
					return 	jrRtn;
				} else if ("N".equals(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"))) {
					//사용금지일경우 에러 처리
					sMsg = "착지개소코드 [" + sArrWlocCd + "], 착지야드포인트코드 [" + sArrYdPntCd + "] 는  사용금지 상태입니다.";
					slabUtils.printLog(logId, ""+sMsg, "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sMsg);
					return 	jrRtn;
				}
				
				slabUtils.printLog(logId, sTrnEqpCd +"--(이송차량도착전문수신_적재가능max중량)-->"+ sTrnEqpStkCapa , "SL"); 	
				jrParam = slabUtils.getParam(logId, methodNm, modifier);
				jrParam.setField("YD_STK_COL_GP"      , ydStkColGp		);
				jrParam.setField("YD_STK_BED_WT_MAX"  , sTrnEqpStkCapa	); 
				jrParam.setField("YD_STK_BED_ACT_STAT", "L"				);
				/*
				UPDATE TB_YD_STKBED
				   SET YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
				     , YD_STK_BED_WT_MAX   = nvl(to_number(:V_YD_STK_BED_WT_MAX), 0)
				     , MOD_DDTT            = SYSDATE
				     , MODIFIER            = :V_MODIFIER
				 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
				 */
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updStkbedActStat";
				commDao.update(jrParam, queryId, logId, methodNm, "BED 활성화처리 및 차량도착정보적재중량MAX값등록");
				
			}
			
			slabUtils.printLog(logId, "▶ 도착 포인트(차량) 상태 이상없음.", "SL");
			slabUtils.printLog(logId, "▶ 적치열과 차량포인트 테이블을 초기화 합니다.", "SL");
			
			//차량위치 예정정보 삭제 정리 -예약으로 잡혀있는정보 Clear  (예)02에 에정처리후 01로 도착 된 경우 
	    	/*         
			UPDATE TB_YD_STKCOL
			   SET CARD_NO             = ''
			     , CAR_NO              = '' 
			     , YD_CAR_USE_GP       = ''
			     , TRN_EQP_CD          = ''
			     , YD_STK_COL_ACT_STAT = DECODE(YD_STK_COL_ACT_STAT, 'R', :V_YD_STK_COL_ACT_STAT, YD_STK_COL_ACT_STAT)
			     , MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			 WHERE TRN_EQP_CD          = :V_TRN_EQP_CD   
			   AND DEL_YN              = 'N' 
			   AND YD_GP               = 'D'
   			 */
			jrParam.setField("TRN_EQP_CD"			, sTrnEqpCd); //운송장비코드
			jrParam.setField("YD_STK_COL_ACT_STAT"	, "C"); 	
			
			slabUtils.printLog(logId, "▶ 적치열 테이블(TB_YD_STKCOL)을 초기화 합니다.", "SL");
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updStackStatByTrnEqpCd03"; 
			commDao.update(jrParam, queryId, logId, methodNm, "차량위치 예정정보 삭제 정리 -예약으로 잡혀있는정보 Clear ");
			
			//차량 포인트 예약으로 잡혀있는정보 Clear
			/* 
			UPDATE TB_YD_CARPOINT
			   SET YD_STK_COL_ACT_STAT = DECODE(YD_STK_COL_ACT_STAT, 'R', 'C', YD_STK_COL_ACT_STAT)
			      ,TRN_EQP_CD = ''
			      ,MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			   AND YD_GP = 'D'
			   AND DEL_YN = 'N'
			*/
			jrParam.setField("TRN_EQP_CD",sTrnEqpCd);  //운송장비코드
			
			slabUtils.printLog(logId, "▶ 차량포인트 테이블(TB_YD_CARPOINT)을 초기화 합니다.", "SL");
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updPlnInfoReSet";
			commDao.update(jrParam, queryId, logId, methodNm, "차량 포인트 예약으로 잡혀있는정보 Clear ");
			
			slabUtils.printLog(logId, "▶ 적치열(" + sArrYdPntCd + ")을 점유 처리합니다.", "SL");
			//차량저장위치 점유	  
			jrParam.setField("YD_CAR_USE_GP", "L"); //L:구내운송차량 , G:출하차량  	--야드차량사용구분
			jrParam.setField("TRN_EQP_CD"	, sTrnEqpCd); 
			jrParam.setField("CAR_NO"		, ""); 
			jrParam.setField("CARD_NO"		, ""); 
			jrParam.setField("YD_STK_COL_ACT_STAT", "L");   //사용중 처리 
			jrParam.setField("WLOC_CD"		, sArrWlocCd); 	
			jrParam.setField("YD_PNT_CD"	, sArrYdPntCd); 

			/* 
				UPDATE  TB_YD_STKCOL
				   SET  YD_CAR_USE_GP 	= :V_YD_CAR_USE_GP
				       ,TRN_EQP_CD 		= :V_TRN_EQP_CD
				       ,CAR_NO 			= :V_CAR_NO
				       ,CARD_NO 		= :V_CARD_NO
				       ,YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT  
				 WHERE  WLOC_CD 		= :V_WLOC_CD
				   AND  YD_PNT_CD 		= :V_YD_PNT_CD
			       AND  YD_EQP_GP 		= 'PT' 
			 */ 	
			queryId  = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updateLayerstat_01";
			commDao.update(jrParam, queryId, logId, methodNm, "차량저장위치 점유 ");
			
			slabUtils.printLog(logId, "▶ 차량포인트(" + sArrYdPntCd + ")를 점유 처리합니다. >> 차량포인트통합관리 호출", "SL");
			//차량포인트통합관리 (1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			this.YdCarPointinforeg(	 "4"
									,""
									,sTrnEqpCd
									,""
									,sArrWlocCd
									,sArrYdPntCd
									,"L"
									, logId, methodNm);
			
			//운송장비코드로 TB_YD_CARFTMVMTL(차량이송재료)에 하차출발로 예약되어 있는 재료 수 구하기
			jrParam.setField("TRN_EQP_CD"	, sTrnEqpCd); 
			
			slabUtils.printLog(logId, "▶ 적치단 테이블(TB_YD_STKLYR)의 활성상태를 '적치가능'으로 변경 합니다.", "SL");
			/* 
			SELECT  COUNT(B.STL_NO) AS QTY
			  FROM  TB_YD_CARSCH 		A
			       ,TB_YD_CARFTMVMTL 	B
			 WHERE  A.DEL_YN 			= 'N'
			   AND  A.TRN_EQP_CD 		= :V_TRN_EQP_CD
			   AND  A.YD_CAR_PROG_STAT 	= 'A' -- 차량진행상태 A:하차출발
			   AND  A.YD_CAR_SCH_ID 	= B.YD_CAR_SCH_ID
			 */ 
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getListstlQty";
			JDTORecordSet jsCarMtlCnt = commDao.select(jrParam, queryId, logId, methodNm, "운송장비코드로 TB_YD_CARFTMVMTL(차량이송재료)에 하차출발로 예약되어 있는 재료 수 구하기  ");
			String szQTY = slabUtils.trim(jsCarMtlCnt.getRecord(0).getFieldString("QTY"));
			
			slabUtils.printLog(logId, "▷ 차량에 실린 재료(Slab) 갯수 : " + szQTY, "SL");
			
			//해당 야드포인트이 적치단에서 QTY 이하 단의  활성상태를 변경한다.
			jrParam.setField("YD_STK_LYR_ACT_STAT"		, "E"); //적치가능
			jrParam.setField("QTY"						, "6"); 
			jrParam.setField("WLOC_CD"					, sArrWlocCd); 	
			jrParam.setField("YD_PNT_CD"				, sArrYdPntCd); 
			if ("0".equals(szQTY)) {
				jrParam.setField("QTY"					, "6"); 
			} else {
				slabUtils.printLog(logId, "하차출발건수"+szQTY, "SL");
				jrParam.setField("QTY"					, szQTY); 
			}
			slabUtils.printLog(logId, "TB_YD_STKCOL "+sArrWlocCd + " PNT_CD:" +sArrYdPntCd, "SL");
			/* 
				UPDATE TB_YD_STKLYR
				   SET YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT 
				 WHERE YD_STK_COL_GP = (SELECT YD_STK_COL_GP	    	 		
				                         FROM  TB_YD_STKCOL
				                        WHERE  WLOC_CD 		= :V_WLOC_CD
				                          AND  YD_PNT_CD 	= :V_YD_PNT_CD
				                          AND  YD_EQP_GP 	= 'PT'
				                      )
				   AND TO_NUMBER(YD_STK_LYR_NO) <= :V_QTY  
			 */
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updateLayerstat_Qty";
			commDao.update(jrParam, queryId, logId, methodNm, "수정"); 
			
			JDTORecordSet jsCarMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");

			String ydWrkCrn      	= "";
			String ydSchPrior    	= "";
			//String ydPrepSchId		= "";
			String sLotYn 			= "N"; 
			String ydEqpId      	= "";
			//boolean ydydj401 = false;
			
			slabUtils.printLog(logId, "▷ 운송작업영공구분[F]영차[E]공차	:"+ sTrnWrkFullvoidGp, "SL");
			slabUtils.printLog(logId, "▷ 차량형상인식 사용여부			:"+ ydFrmYn, "SL");
			
			if ("F".equals(sTrnWrkFullvoidGp)) {
				slabUtils.printLog(logId, "▶ [" + sTrnEqpCd + "]차량은 하차차량입니다.", "SL");
				
				/*****************
				 *   영차인 경우
				 *****************/
				ydEqpWrkStat = "L"; //영차
				
				String ydStkBedNo = "";
				
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
 				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarFtmvMtlBed2List";
				JDTORecordSet jsBed2 = commDao.select(jrParam, queryId, logId, methodNm, "이송재료 2Bed여부 조회");
				/**********************************************************
				* 1-5. 차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)
				**********************************************************/
				
				slabUtils.printLog(logId, "▶3.[" + sTrnEqpCd + "]하차차량의 차량스케줄 정보를 UPDATE 합니다.", "SL");
				slabUtils.printLog(logId, "▷   차량작업위치	: " + ydStkColGp	, "SL");
				slabUtils.printLog(logId, "▷   차량포인트		: " + sArrYdPntCd	, "SL");
				slabUtils.printLog(logId, "▷   하차작업예약ID	: " + ydWbookId		, "SL");
				slabUtils.printLog(logId, "▷   운송장비코드	: " + sTrnEqpCd		, "SL");
				
				jrParam.setField("YD_CARUD_STOP_LOC"	, ydStkColGp);
				jrParam.setField("YD_PNT_CD3"			, sArrYdPntCd);
				jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, ydWbookId);
				jrParam.setField("TRN_EQP_CD"			, sTrnEqpCd);
				/* 
				UPDATE TB_YD_CARSCH
				  SET  MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC
				      ,YD_PNT_CD3 = :V_YD_PNT_CD3
				      ,YD_CARUD_ARR_DT = SYSDATE
				      ,YD_CAR_PROG_STAT = 'B' -- 하차도착
				      ,YD_CARUD_WRK_BOOK_ID= :V_YD_CARUD_WRK_BOOK_ID
				WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				AND DEL_YN = 'N'
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updateArrDt5", logId, methodNm, "차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)");
				//윗쪽으로 이동 사유: DBPT01로 차량 출발후 DBPT02로 도착하면 작업예약시 FROM 값이 다르게 표시되고 있음. -CARSCH 값의 정합성을 위해 이동함.   
				
				slabUtils.printLog(logId, "▷ [" + sTrnEqpCd + "]차량의 Bed 갯수 : " + jsBed2.size(), "SL");
				for(int kk= 0; kk < jsBed2.size() ; kk++) {
					//--YD_STK_BED_NO
					ydStkBedNo = slabUtils.nvl(jsBed2.getRecord(kk).getFieldString("YD_STK_BED_NO"),"01"); 
					
					//1.운송작업영공구분이 F:영차 인 경우 처리
					slabUtils.printLog(logId, "▶ [[[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 하차처리 작업을 시작합니다.", "SL");
					slabUtils.printLog(logId, "▶ 1.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 하차재료 상태변경 START.", "SL");
					slabUtils.printLog(logId, "▶ 1.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 하차대상재료 조회 중..", "SL");
					
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
					
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getListFrtostlList";
					jsCarMtl = commDao.select(jrParam, queryId, logId, methodNm, "운송장비코드로 하차대상재료 조회");
					
					slabUtils.printLog(logId, "▷ 1.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 하차대상 재료 갯수 : " + jsCarMtl.size(), "SL");
					
					if (jsCarMtl.size() <= 0) {
						slabUtils.printLog(logId, "▶ 1.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED에 하차대상 재료가 없습니다.. ERROR 처리.", "SL");
						msgId = "영차(F) 도착처리 대상재가 존재 안함";
						throw new Exception(msgId);
					} else {
						ydCarSchId = slabUtils.trim(jsCarMtl.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
						slabUtils.printLog(logId, "▷ 하차 차량스케줄 ID : " + ydCarSchId, "SL");
					}
					
					//하차대상 갯수 만큼 Looping...
					for(int ii= 0; ii < jsCarMtl.size() ; ii++) {
						slabUtils.printLog(logId, "▶ 1.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 " + ii + "번 째 재료상태변경을 시작합니다.", "SL");
						
						slabUtils.printLog(logId, "▶ 1. TB_YD_STKLYR(적치단) 테이블의 재료작업상태를 작업중으로 변경합니다.", "SL");
						slabUtils.printLog(logId, "▷ 재료번호	: " + jsCarMtl.getRecord(ii).getFieldString("STL_NO")		, "SL");
						slabUtils.printLog(logId, "▷ 적치열	    : " + ydStkColGp + ">" +jsCarMtl.getRecord(ii).getFieldString("YD_STK_COL_GP")	, "SL");
						slabUtils.printLog(logId, "▷ 적치BED	: " + jsCarMtl.getRecord(ii).getFieldString("YD_STK_BED_NO"), "SL");
						slabUtils.printLog(logId, "▷ 적치단    	: " + jsCarMtl.getRecord(ii).getFieldString("YD_STK_LYR_NO"), "SL");
						
						/**********************************************************
						 * 영차도착 포인트 적치단(TB_YD_STKLYR)에 SLAB정보 생성하기
						 **********************************************************/
						jrParam.setField("STL_NO"				, slabUtils.trim(jsCarMtl.getRecord(ii).getFieldString("STL_NO")));
						jrParam.setField("YD_STK_COL_GP"		, ydStkColGp);
						jrParam.setField("YD_STK_BED_NO"		, slabUtils.trim(jsCarMtl.getRecord(ii).getFieldString("YD_STK_BED_NO")));
						jrParam.setField("YD_STK_LYR_NO"		, slabUtils.trim(jsCarMtl.getRecord(ii).getFieldString("YD_STK_LYR_NO"))); //	//slabUtils.format(ii+1, 3));
						jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"		);	// 적치중
						
						/*
						UPDATE TB_YD_STKLYR
						   SET STL_NO = :V_STL_NO
						      ,YD_STK_LYR_ACT_STAT  = NVL(:V_YD_STK_LYR_ACT_STAT, YD_STK_LYR_ACT_STAT)
						      ,YD_STK_LYR_MTL_STAT  = NVL(:V_YD_STK_LYR_MTL_STAT, YD_STK_LYR_MTL_STAT)
						      ,MODIFIER = :V_MODIFIER
						      ,MOD_DDTT = SYSDATE
						WHERE  YD_STK_COL_GP = :V_YD_STK_COL_GP
						  AND  YD_STK_BED_NO = :V_YD_STK_BED_NO  						  
						 */
						queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.setStackLayer";
						commDao.update(jrParam, queryId, logId, methodNm, "영차도착 포인트 적치단(TB_YD_STKLYR)에 SLAB정보 생성하기");
						
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
						queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStockByScanXyz";
						commDao.update(jrParam, queryId, logId, methodNm, "STOCK에서 기존  SCAN 좌표값 초기 화  (tb_yd_stock 관련 항목 update)");
						slabUtils.printLog(logId, "▶ 1.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 하차재료 상태변경 END.", "SL");
					}
					
					slabUtils.printLog(logId, "▶ 2.[" + sTrnEqpCd + "]하차차량 [" + ydStkBedNo + "]BED의 하차재료 작업예약 생성 START.", "SL");
					slabUtils.printLog(logId, "▶ 2. 크레인스케줄코드를 생성합니다.", "SL");
					
					//스케줄코드 생성  - 이송하차(L)
					ydSchCd = ydStkColGp+"LM";
					
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
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdSchrule";
					JDTORecordSet jsSchRule = commDao.select(jrParam, queryId, logId, methodNm, "스케줄 기준 조회"); 
					
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
					slabUtils.printLog(logId, "▷   야드구분		: " + ydGp		, "SL");
					slabUtils.printLog(logId, "▷   목표동		: " + ydBayGp	, "SL");
					slabUtils.printLog(logId, "▷   야드스케쥴우선순위	: " + ydSchPrior, "SL");
					slabUtils.printLog(logId, "▷   적치열		: " + ydStkColGp, "SL");
					
					jrParam.setField("YD_SCH_ST_GP"				, "A"); 		//야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
					jrParam.setField("YD_WRK_PLAN_CRN"			, ydWrkCrn); 	//야드작업계획크레인
					jrParam.setField("YD_WRK_PLAN_CRN2"			, ""); 			//야드작업계획크레인2
					
					//ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
					
					jrParam.setField("YD_SCH_CD"        , ydSchCd); 	//야드스케쥴코드
					jrParam.setField("YD_AIM_BAY_GP"    , ydBayGp  ); 	//야드목표동구분 ydAimBayGp
					jrParam.setField("YD_TO_LOC_GUIDE"  , ""); 			//야드To위치Guide 
					jrParam.setField("YD_WRK_PLAN_TCAR" , ""); 			//야드작업계획대차 ydWrkPlanTcar
					jrParam.setField("YD_WRK_PLAN_CRN"  , "");      	//야드작업크레인 ydWrkCrn
					jrParam.setField("YD_SCH_PRIOR"	    , "");      	//야드크레인작업순위 schPrior
					
					//jrParam.setField("YD_WBOOK_ID"		, ydWbookId);
					jrParam.setField("YD_GP"			, ydGp);
					jrParam.setField("YD_SCH_PRIOR"		, ydSchPrior); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT"	, "W"); 		//야드스케쥴진행상태(W:스케줄수행대기)
					jrParam.setField("YD_SCH_REQ_GP"	, "C"); 		//야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
					jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd); 	//운송장비코드
					jrParam.setField("YD_CAR_USE_GP"	, "L"); 		//야드차량사용구분 L:구내운송
					jrParam.setField("YD_STK_COL_GP"   	, ydStkColGp);	//"DBPT02"
					
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
					
					if ( kk == 0) {
						ydWbookId1 = ydWbookId;
					}
				}	//2Bed 하차 처리 (end)
				
//-- (E)--				
			} else if ("E".equals(sTrnWrkFullvoidGp)) {
				
				/*****************
				 *   공차인 경우
				 *****************/
				slabUtils.printLog(logId, "▶ [" + sTrnEqpCd + "]차량은 상차차량입니다.", "SL");
				
				ydEqpWrkStat = "U"; //공차

				sMsg="운송작업영공구분이 _E:공차_ 인 경우 처리 < " + methodNm;
				slabUtils.printLog(logId, sMsg, "SL");
				
				/**********************************************************
				* 2-4. 차량스케쥴 update(도착시간, 실제도착위치 업데이트처리)
				**********************************************************/
				slabUtils.printLog(logId, "▶ 1.[" + sTrnEqpCd + "]차량의 도착정보를 TB_YD_CARSCH(차량스케줄)에 UPDATE 합니다.", "SL");
				slabUtils.printLog(logId, "▷   운송장비코드	: " + sTrnEqpCd		, "SL");
				slabUtils.printLog(logId, "▷   차량도착위치	: " + ydStkColGp	, "SL");
				
				jrParam.setField("YD_CARLD_STOP_LOC"	, ydStkColGp);
				jrParam.setField("TRN_EQP_CD"			, sTrnEqpCd);
				/* 
				UPDATE TB_YD_CARSCH
				  SET  MODIFIER 		= :V_MODIFIER
				      ,MOD_DDTT 		= SYSDATE
				      ,YD_CARLD_STOP_LOC= :V_YD_CARLD_STOP_LOC      
				      ,YD_CARLD_ARR_DT  = SYSDATE
				      ,YD_CAR_PROG_STAT = '2' --상차도착
				WHERE TRN_EQP_CD 		= :V_TRN_EQP_CD
				AND DEL_YN = 'N' 
				*/
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updateArrDt_1";
				commDao.update(jrParam, queryId, logId, methodNm, "차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)");
				
				slabUtils.printLog(logId, "▶ 2.[" + sTrnEqpCd + "]차량의 작업예약을 생성합니다.", "SL");
				slabUtils.printLog(logId, "▶ 2.[" + sTrnEqpCd + "]차량으로 TB_YD_CARSCH(차량스케줄)에 등록된 작업예약이 있는지 확인합니다.", "SL");
				
				//차량스케줄 ,작업예약ID 조회 
				jrParam.setField("TRN_EQP_CD"	, sTrnEqpCd);
				/* com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getListtrnEqpschL_1
				SELECT YD_CARLD_WRK_BOOK_ID AS WBOOK_ID
				      ,YD_CAR_SCH_ID
				  FROM TB_YD_CARSCH
				 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				   AND YD_CAR_PROG_STAT = '2'
				   AND DEL_YN = 'N'
				*/
				JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getListtrnEqpschL_1", logId, methodNm, "차량스케줄 ,작업예약ID 조회");
				
				slabUtils.printLog(logId, "▷   차량스케줄 건수 : " + jsCarSch.size()	, "SL");
				if (jsCarSch.size() > 0) {
					ydCarSchId = slabUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
					ydWbookId  = slabUtils.trim(jsCarSch.getRecord(0).getFieldString("WBOOK_ID"));
					
					slabUtils.printLog(logId, "▷   차량스케줄 ID		: " + ydCarSchId	, "SL");
					slabUtils.printLog(logId, "▷   기 등록된 작업예약ID	: " + ydWbookId		, "SL");
					
					if (!"".equals(ydWbookId)) {
						slabUtils.printLog(logId, "▶ 2.[" + sTrnEqpCd + "]상차차량의 기 등록된 작업예약이 있으므로 작업예약생성 PASS.", "SL");
					} else {
						slabUtils.printLog(logId, "▶ 2.[" + sTrnEqpCd + "]상차차량의 기 등록된 작업예약이 없으므로 작업예약을 생성합니다.", "SL");
						
		    			//이미 등록된 차량이송준비스케줄을 조회
		    			slabUtils.printLog(logId, "차량이송준비스케줄을 조회 후 작업예약 등록 시작", "SL");
		    			slabUtils.printLog(logId, "▷  지개소코드			: " + sArrWlocCd	, "SL");
		    			slabUtils.printLog(logId, "▷   운송장비코드		: " + sTrnEqpCd		, "SL");
		    			slabUtils.printLog(logId, "▷   적치열(도착포인트)	: " + ydStkColGp	, "SL");
		    			
						//작업예약 신규 생성***********************************************************
		    			jrParam.setField("WLOC_CD"      	, sArrWlocCd		);
		    			jrParam.setField("TRN_EQP_CD"   	, sTrnEqpCd			);
		    			jrParam.setField("YD_STK_COL_GP"	, ydStkColGp		);
		    			jrParam.setField("TRN_EQP_STK_CAPA"	, sTrnEqpStkCapa	);	// 차량 상차허용가능 중량
		    			
		    			//상차 작업 예약 자동 등록 처리 
		    			slabUtils.printLog(logId, "▶ 2.[" + sTrnEqpCd + "]상차차량의 상차 작업 예약 자동 등록 처리 호출", "SL");
						JDTORecord jrWbook 	= this.procYdWbookForCarLd(jrParam);
						sLotYn				= slabUtils.nvl(jrWbook.getFieldString("LOT_YN"), "0");
						String rtnMsg	 	= slabUtils.nvl(jrWbook.getFieldString("RTN_MSG"), "");
						
						slabUtils.printLog(logId, "▷   상차 LOT 편성 자동match 여부 : " + sLotYn	, "SL");
						if("N".equals(sLotYn)) {
							slabUtils.printLog(logId, "▶ 2.[" + sTrnEqpCd + "]상차차량의 상차 LOT 편성 자동match 오류:"+ rtnMsg, "SL");
						}
					    //--작업예약  자동생성 완료  
						ydWbookId 	= slabUtils.nvl(jrWbook.getFieldString("YD_WBOOK_ID"), "");
						ydWbookId1 	= slabUtils.nvl(jrWbook.getFieldString("YD_WBOOK_ID2"), "");	//*상차
						ydSchCd	  	= slabUtils.nvl(jrWbook.getFieldString("YD_SCH_CD"), "0");
						ydEqpId 	= slabUtils.nvl(jrWbook.getFieldString("YD_EQP_ID"), "0");
						//ydPrepSchId 		= slabUtils.nvl(jrWbook.getFieldString("YD_PREP_SCH_ID"), "0");
						
						slabUtils.printLog(logId, "▶ 2.[" + sTrnEqpCd + "]상차차량의 차량이송준비스케줄을 조회 후 작업예약 등록 끝", "SL");
						
						jrRtn.setField("YD_WBOOK_ID", ydWbookId);
						jrRtn.setField("YD_SCH_CD"  , ydSchCd  );
					}
					//----------------------------------------------------------------------------
				}
				
				//이송상차 도착처리시  해당 포인트를 초기화 한다..
				slabUtils.printLog(logId, "▶ 3.[" + sTrnEqpCd + "]상차차량의 형상정보(TB_YD_RULE DYD006)를 초기화합니다.", "SL");
				jrParam.setField("REPR_CD_GP"	, "DYD006" );
				jrParam.setField("CD_GP"		, ydStkColGp );    //"DBPT01" 
				jrParam.setField("ITEM"			, "D");
				jrParam.setField("DTL_ITEM4"	, "");
				jrParam.setField("DTL_ITEM5"	, "N");	//형상사용여부
				jrParam.setField("DTL_ITEM1"	, "0");
				jrParam.setField("DTL_ITEM2"	, "0");
				jrParam.setField("DTL_ITEM3"	, "0");
				jrParam.setField("DTL_ITEM6"	, "0");	
				jrParam.setField("DTL_ITEM7"	, "0");
				jrParam.setField("DTL_ITEM8"	, "0");
				jrParam.setField("DTL_ITEM9"	, "0");
				jrParam.setField("DTL_ITEM10"	, "");
				/*
				UPDATE TB_YD_RULE
				   SET DTL_ITEM1 = NVL(:V_DTL_ITEM1,DTL_ITEM1)
				      ,DTL_ITEM2 = NVL(:V_DTL_ITEM2,DTL_ITEM2)
				      ,DTL_ITEM3 = NVL(:V_DTL_ITEM3,DTL_ITEM3)
				      ,DTL_ITEM4 = NVL(:V_DTL_ITEM4,DTL_ITEM4)
				      ,DTL_ITEM5 = NVL(:V_DTL_ITEM5,DTL_ITEM5)
				      ,DTL_ITEM6 = NVL(:V_DTL_ITEM6,DTL_ITEM6)
				      ,DTL_ITEM7 = NVL(:V_DTL_ITEM7,DTL_ITEM7)
				      ,DTL_ITEM8 = NVL(:V_DTL_ITEM8,DTL_ITEM8)
				      ,DTL_ITEM9 = NVL(:V_DTL_ITEM9,DTL_ITEM9)
				      ,DTL_ITEM10 = NVL(:V_DTL_ITEM10,DTL_ITEM10)
				      ,MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				 WHERE REPR_CD_GP = :V_REPR_CD_GP
				   AND CD_GP = :V_CD_GP
				   AND ITEM = :V_ITEM
				 */                      
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdRuleNvl";
				commDao.update(jrParam, queryId, logId, methodNm, "TB_YD_RULE DYD006 수정 - 초기화");
			}

//--상하차 이후 공통작업--	 
			/**********************************************************
			 * 차량작업 예정정보 송신 (YDY3L008)    <-- Y3YDL019 (요구)
			 **********************************************************/
			slabUtils.printLog(logId, "▶ 4.[" + sTrnEqpCd + "]차량의 차량작업 예정정보 (YDY3L008) I/F 전문을 생성합니다.", "SL");
			jrParam		= slabUtils.getParam(logId, methodNm, modifier);
			slabUtils.printLog(logId, "---------------------------------------------------------------", "SL");   //상차도 위치 
			jrParam.setField("SEARCH_FLAG" 		    , "1"			); //1:상차도, 2:차량스케쥴 ID	
			jrParam.setField("PT_LOAD_LOC" 			, ydStkColGp	); //상차도 위치
			jrParam.setField("YD_CAR_SCH_ID"		, ydCarSchId	); //야드크레인스케쥴ID  (추가사항)
			jrParam.setField("TRN_EQP_CD"   		, sTrnEqpCd	    );
			
			jrRtn = slabUtils.addSndData(jrRtn, slabComm.procCarPlanInfo_Slab(jrParam));  
			
			/**********************************************************
			* 저장위치제원정보 송신 (YDY3L001)
			**********************************************************/
			slabUtils.printLog(logId, "▶ 4.[" + sTrnEqpCd + "]차량의 저장위치제원정보 송신 (YDY3L001) I/F 전문을 생성합니다.", "SL");
			//JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			jrParam		= slabUtils.getParam(logId, methodNm, modifier);

			jrParam.setField("YD_GP"				, "D"				);
			jrParam.setField("YD_STK_COL_NO"    	, ydStkColGp		);
			jrParam.setField("YD_CAR_ARRSTRT_STAT" 	, "A"				); //A:도착, S:출발
			jrParam.setField("YD_CAR_USE_GP"    	, "L"				); //L:구내운송, G:출하차량
			jrParam.setField("YD_EQP_WRK_STAT"  	, ydEqpWrkStat		); //U:공차, L:영차
			jrParam.setField("TRN_EQP_CD"  			, sTrnEqpCd		    ); //운송장비코드
			jrParam.setField("YD_CAR_AIM_YD_GP"		, ydGp		        );
			
			jrParam.setField("YD_INFO_SYNC_CD"		, "4"				); //야드정보동기화코드  야드정보동기화코드(1:동,2:SPAN,3:열,4:BED)
			jrParam.setField("YD_STK_COL_GP"		, ydStkColGp		);
			jrParam.setField("YD_STK_BED_NO"    	, ""				);
			jrParam.setField("YD_EQP_WRK_STAT_GUBUN" , "");
			jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY3L001", jrParam));
			
			slabUtils.printLog(logId, "▶ 5.[" + sTrnEqpCd + "]차량의 형상수신여부를 확인합니다.", "SL");
			slabUtils.printLog(logId, "▷   형상 사용 여부 : " + ydFrmYn	, "SL");
			if ("Y".equals(ydFrmYn)) {  //형상 사용 여부 
				
				slabUtils.printLog(logId, "▶ 5.[" + sTrnEqpCd + "]차량의 기 수신된 형상정보를 조회합니다.", "SL");
				/* com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdRuleNvl2 
				SELECT DECODE(DTL_ITEM5 ,'Y', 'Y','N') AS FRM_YN 
				FROM TB_YD_RULE 
				WHERE  REPR_CD_GP = 'DYD006'
				AND ITEM = 'D' AND CD_GP = :V_CD_GP
				*/
				jrParam.setField("CD_GP"		, ydStkColGp);
				JDTORecordSet jsFrmAcc =commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdRuleNvl2", logId, methodNm, "형상수신확인");
				
				slabUtils.printLog(logId, "▷   형상정보 조회건수 : " + jsFrmAcc.size(), "SL");
				if(jsFrmAcc.size() > 0) {
					
					slabUtils.printLog(logId, "▷   형상수신유무 : " + jsFrmAcc.getRecord(0).getFieldString("FRM_YN"), "SL");
					if("Y".equals( jsFrmAcc.getRecord(0).getFieldString("FRM_YN"))) {	//형상 수신 여부 
						ydFrmYn = "N";
					}
				}
			}
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
			 * 업무기준 : 저장품 제원 정보 L2로 전송   [YDY3L002]
			 *+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
			jrParam	= slabUtils.getParam(logId, methodNm, modifier);
			sMsg	= "["+methodNm+"] 저장품 제원 야드L2로 전송 시작..";
			slabUtils.printLog(logId, sMsg, "SL");
			 
			if ("F".equals(sTrnWrkFullvoidGp)) {
				/**********************************************************
				 * 저장품제원(YDY3L002) 송신 전문 생성     									(참고)YMA8L002 
				 **********************************************************/
				slabUtils.printLog(logId, "▶ 6.[" + sTrnEqpCd + "]차량은 하차차량이므로 저장품제원(YDY3L002) I/F 전문을 생성합니다.", "SL");
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("YD_INFO_SYNC_CD"		, "4"		); //야드정보동기화코드  (4:BED)
				jrParam.setField("MSG_GP"				, "I"		); //전문구분
				jrParam.setField("YD_STK_COL_GP"  		, ydStkColGp	); //야드적치열구분     
//2BED			jrParam.setField("YD_STK_BED_NO"  		, "01"		); //야드적치Bed번호
				jrParam.setField("YD_GP"          		, ydGp		); //야드구분
				//jrParam.setField("STL_NO"				, slabUtils.trim(jsCarMtl.getRecord(ii).getFieldString("STL_NO")));
				
				slabUtils.printLog(logId, ">>YD_GP:"+ydGp  , "SL");
				//후판 저장품제원 
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L002", jrParam));    
			}
			
			// sYDYDJ401_CALL_YN -> 작업크레인이 리모컨/매뉴얼(유인) 작업일 때  크레인 스케쥴 기동 처리('Y'로 Set)
			slabUtils.printLog(logId, "▷   sYDYDJ401_CALL_YN :"+sYDYDJ401_CALL_YN  , "SL");	// Y일 경우 크레인이 무인이므로 기동시키지 않는다.
			
			if ("N".equals(ydFrmYn) && "Y".equals(sYDYDJ401_CALL_YN)) {	// 형상이 사용중이지 않거나 크레인이 무인 작업 중이면 스케쥴을 기동시키지 않는다.
				slabUtils.printLog(logId, "▶ 5.[" + sTrnEqpCd + "]차량의 형상정보 있으므로 크레인스케줄 기동 I/F 전문을 생성합니다.", "SL");
				JDTORecord jrCrnSchMsg2 = JDTORecordFactory.getInstance().create();
				
				slabUtils.printLog(logId, "##스케줄기동 우선순위: 하차2Bed 우선## (1번)" + ydWbookId1 + " (2번)"+ ydWbookId, "SL");
				if(!"".equals(ydWbookId1) ) {
					slabUtils.printLog(logId,  "▶ 5.[" + sTrnEqpCd + "]차량의 크레인 스케쥴 호출(차상위치 다건) : YDYDJ401- 2BED >" + ydWbookId1 , "SL");   
					
					//크레인 스케줄 기동  호출
					jrCrnSchMsg2.setField("JMS_TC_CD"			, "YDYDJ401"); 
					jrCrnSchMsg2.setField("JMS_TC_CREATE_DDTT"	, slabUtils.getDateTime14()); //JMSTC생성일시	
					jrCrnSchMsg2.setField("YD_WBOOK_ID"  		, ydWbookId1); //작업예약ID
					jrCrnSchMsg2.setField("YD_SCH_CD"  			, ydSchCd);  //야드스케쥴코드
					jrCrnSchMsg2.setField("YD_EQP_ID"  			, ydEqpId);  //야드설비ID (예)DACRA1
					jrCrnSchMsg2.setField("EJB_CALL_YN"			, "Y");	//EJBCall여부(신 크레인스케줄)
					
					jrRtn = slabUtils.addSndData(jrRtn, jrCrnSchMsg2);
				}	
 
			}  
			
			
			slabUtils.printLog(logId, "▶ 후판슬라브야드 소재차량도착(TSYDJ003) 수신처리 END!", "SL");
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "정상처리되었습니다.");
			return jrRtn;
	    	
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of rcvTSYDJ003			


	/**
	 * [A] 오퍼레이션명 : 후판 개소코드 인지 체크						    
	 * 야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
	 * @param sWlocCd
	 * @return boolean 
	 */
	private boolean getSlabLocationInfo_02(String sWlocCd) {
		if (	PSlabYdConstant.WLOC_CD_A_PLATE_SLAB_YARD.equals(sWlocCd)   	//1후판슬라브야드(1후판-옥내 Yard)
			||  PSlabYdConstant.WLOC_CD_2_PLATE_SLAB_YARD.equals(sWlocCd)   	//2후판슬라브야드(2후판-옥내 Yard)
		   )	{
				return true;
		} else {
				return false;
		}
	}
	

	/**
	 * [A] 오퍼레이션명 : 구내운송 상차 차량도착시  LOT편성건을 확인하고 작업예약등록 처리  (차량상차작업예약등록 후 LOT편성 삭제처리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procYdWbookForCarLd(JDTORecord rcvMsg)throws DAOException  {
		String mthdNm = "(상차)준비스케줄 등록[PSlabYdMvCarSeEJB.procYdWbookForCarLd] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	    
	    try{
			slabUtils.printLog(logId, mthdNm, "S+");
			slabUtils.printParam(logId, rcvMsg);
			
			String sWlocCd    = slabUtils.trim(rcvMsg.getFieldString("WLOC_CD"      )); //개소코드
			String sTrnEqpCd  = slabUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"   )); //운송장비코드
			String ydStkColGp = slabUtils.trim(rcvMsg.getFieldString("YD_STK_COL_GP")); //야드적치열구분(D_PT0_)

			String ydPrepSchId1 = slabUtils.trim(rcvMsg.getFieldString("YD_PREP_SCH_ID1"));
			String ydPrepSchId2 = slabUtils.trim(rcvMsg.getFieldString("YD_PREP_SCH_ID2"));

			slabUtils.printLog(logId, "개소코드         : " + sWlocCd			, "SL");
			slabUtils.printLog(logId, "운송장비코드   : " + sTrnEqpCd		, "SL");
			slabUtils.printLog(logId, "야드적치열구분: " + ydStkColGp		, "SL");
			
			String sModifier  = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
			String sMsg 	= "";
			String ydWrkPlanCrn = "";
			String queryId 	= "";
			//boolean twoBed = false;
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			//파라메터 JDTORecord 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, mthdNm, sModifier);
			
			String ydBayGp = ydStkColGp.substring(1, 2);
			
			
			/***********************************
			 * 1. 도착동의 이송LOT편성 내역과 구내운송 차량 MATCH  
			 ***********************************/
			String ydSchCd   = "";
			String ydWbookId = "", ydWbookId2 = "";
			ydSchCd = ydStkColGp.substring(0, 4) +"__" + "UM";
			
			slabUtils.printLog(logId, "□□□ ydSchCd  :"+ydSchCd, "SL");
			
			//boolean bExist      = false;
			
			jrParam.setField("YD_SCH_CD", 		ydSchCd );
			jrParam.setField("YD_PREP_WK_ST",	"L");
			jrParam.setField("CAR_GP",			sTrnEqpCd.substring(1, 2));
			jrParam.setField("YD_WRK_PLAN_CRN", sTrnEqpCd);

			
			JDTORecordSet rsRst = null;
			
			slabUtils.printLog(logId, "▷  준비스케쥴id1(ydPrepSchId1) : " + ydPrepSchId1	, "SL");
			slabUtils.printLog(logId, "▷  준비스케쥴id2(ydPrepSchId2) : " + ydPrepSchId2	, "SL");
			
			if(!"".equals(ydPrepSchId1)) {  // 준비스케쥴ID가 파라미터로 넘어왔을 때 -> 차량작업관리 화면에서 수동으로 처리한 CASE

				if(!"".equals(ydPrepSchId1) &&  "".equals(ydPrepSchId2) ) {	// 1Bed 작업 이송LOT 조회
					slabUtils.printLog(logId, "YD_PREP_SCH_ID:"+ydPrepSchId1, "SL");
					jrParam.setField("YD_PREP_SCH_ID", ydPrepSchId1);
					
					/*
					SELECT A.STL_NO        AS STL_NO
					      ,A.YD_AIM_RT_GP  AS YD_AIM_RT_GP
					      ,A.YD_MTL_ITEM   AS YD_MTL_ITEM
					      ,A.YD_MTL_L      AS YD_MTL_L
					      ,A.YD_MTL_W      AS YD_MTL_W
					      ,A.YD_MTL_WT     AS YD_MTL_WT
					      ,B.YD_PREP_SCH_ID
					      ,B.YD_SCH_CD
					      ,B.YD_GP 
					      ,B.YD_PREP_WK_ST
					      ,B.YD_TO_LOC_DCSN_MTD
					      ,B.YD_TO_LOC_GUIDE
					      ,B.ARR_WLOC_CD
					      ,B.YD_AIM_YD_GP
					      ,B.YD_AIM_BAY_GP
					      ,B.YD_CARASGN_SEQ
					      ,B.YD_EQP_WRK_SH
					      ,B.YD_WRK_PLAN_CRN
					      ,B.YD_STK_COL_GP AS YD_STK_COL_GP
					      ,B.YD_STK_BED_NO AS YD_STK_BED_NO
					      ,B.YD_STK_LYR_NO AS YD_STK_LYR_NO
					      ,B.YD_STK_BED_NO_ROOT
					      ,B.PRI_YD_PREP_SCH_ID
					  FROM TB_YD_STOCK  A
					     , (
					        SELECT A.YD_PREP_SCH_ID
					             , A.YD_SCH_CD
					             , A.YD_GP 
					             , A.YD_PREP_WK_ST
					             , A.YD_TO_LOC_DCSN_MTD
					             , A.YD_TO_LOC_GUIDE
					             , A.ARR_WLOC_CD
					             , A.YD_AIM_YD_GP
					             , A.YD_AIM_BAY_GP
					             , A.YD_CARASGN_SEQ
					             , A.YD_EQP_WRK_SH
					             , A.YD_WRK_PLAN_CRN
					             , B.STL_NO
					             , B.YD_STK_COL_GP AS YD_STK_COL_GP
					             , B.YD_STK_BED_NO AS YD_STK_BED_NO
					             , B.YD_STK_LYR_NO AS YD_STK_LYR_NO
					             , YD_CAR_UPP_LOC_CD AS YD_STK_BED_NO_ROOT
					             , A.PRI_YD_PREP_SCH_ID
					          FROM ( 
					                SELECT *
					                  FROM (
					                  
					                        SELECT *
					                          FROM TB_YD_PREPSCH A
					                         WHERE YD_GP = 'D'
					                           AND YD_SCH_CD        LIKE :V_YD_SCH_CD || '%'
					                           AND YD_PREP_WK_ST    LIKE :V_YD_PREP_WK_ST || '%'
					                           AND NVL(CAR_GP, '*') LIKE :V_CAR_GP || '%'
					                           AND YD_PREP_SCH_ID   LIKE :V_YD_PREP_SCH_ID ||'%'    
					                           AND A.DEL_YN = 'N'
					                           AND A.PRI_YD_PREP_SCH_ID IS  NULL --순수1 Bed건
					                         ORDER BY YD_CARASGN_SEQ ASC
					                                , YD_PREP_SCH_ID ASC
					                       )
					               ) A
					             , TB_YD_PREPMTL B
					         WHERE A.YD_PREP_SCH_ID = B.YD_PREP_SCH_ID
					            AND B.DEL_YN = 'N'
					       ) B
					 WHERE A.STL_NO = B.STL_NO
					   AND A.DEL_YN = 'N'
					     
					 ORDER BY B.YD_PREP_SCH_ID  
					        , B.YD_STK_BED_NO_ROOT ASC
					        , B.YD_STK_COL_GP ASC
					        , B.YD_STK_BED_NO DESC
					        , B.YD_STK_LYR_NO DESC
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getYdStockNPrepSchByYdCrnCarGpFor";
					rsRst = commDao.select(jrParam, queryId, logId, mthdNm, "차량이송준비스케줄 조회(1Bed 대상)");
				} else { 	// 2Bed 작업 이송LOT ID조회
					slabUtils.printLog(logId, "YD_PREP_SCH_ID"+ ydPrepSchId1, "SL");
					jrParam.setField("YD_PREP_SCH_ID", ydPrepSchId1);
					
					/*
					SELECT A.STL_NO        AS STL_NO
					      ,A.YD_AIM_RT_GP  AS YD_AIM_RT_GP
					      ,A.YD_MTL_ITEM   AS YD_MTL_ITEM
					      ,A.YD_MTL_L      AS YD_MTL_L
					      ,A.YD_MTL_W      AS YD_MTL_W
					      ,A.YD_MTL_WT     AS YD_MTL_WT
					      ,B.YD_PREP_SCH_ID
					      ,B.YD_SCH_CD
					      ,B.YD_GP 
					      ,B.YD_PREP_WK_ST
					      ,B.YD_TO_LOC_DCSN_MTD
					      ,B.YD_TO_LOC_GUIDE
					      ,B.ARR_WLOC_CD
					      ,B.YD_AIM_YD_GP
					      ,B.YD_AIM_BAY_GP
					      ,B.YD_CARASGN_SEQ
					      ,B.YD_EQP_WRK_SH
					      ,B.YD_WRK_PLAN_CRN
					      ,B.YD_STK_COL_GP AS YD_STK_COL_GP
					      ,B.YD_STK_BED_NO AS YD_STK_BED_NO
					      ,B.YD_STK_LYR_NO AS YD_STK_LYR_NO
					      ,B.YD_STK_BED_NO_ROOT
					      ,B.PRI_YD_PREP_SCH_ID
					  FROM TB_YD_STOCK  A
					     , (
					        SELECT A.YD_PREP_SCH_ID
					             , A.YD_SCH_CD
					             , A.YD_GP 
					             , A.YD_PREP_WK_ST
					             , A.YD_TO_LOC_DCSN_MTD
					             , A.YD_TO_LOC_GUIDE
					             , A.ARR_WLOC_CD
					             , A.YD_AIM_YD_GP
					             , A.YD_AIM_BAY_GP
					             , A.YD_CARASGN_SEQ
					             , A.YD_EQP_WRK_SH
					             , A.YD_WRK_PLAN_CRN
					             , B.STL_NO
					             , B.YD_STK_COL_GP AS YD_STK_COL_GP
					             , B.YD_STK_BED_NO AS YD_STK_BED_NO
					             , B.YD_STK_LYR_NO AS YD_STK_LYR_NO
					             , YD_CAR_UPP_LOC_CD AS YD_STK_BED_NO_ROOT
					             , A.PRI_YD_PREP_SCH_ID
					          FROM ( 
					                SELECT *
					                  FROM (
					                  
					                        SELECT *
					                          FROM TB_YD_PREPSCH A
					                         WHERE YD_GP = 'D'
					                           AND YD_SCH_CD        LIKE TRIM(:V_YD_SCH_CD) || '%'
					                           AND YD_PREP_WK_ST    LIKE :V_YD_PREP_WK_ST || '%'
					                           AND NVL(CAR_GP, '*') LIKE :V_CAR_GP || '%'
					                           AND A.PRI_YD_PREP_SCH_ID IN (
					                             SELECT PRI_YD_PREP_SCH_ID 
					                               FROM TB_YD_PREPSCH 
					                              WHERE YD_PREP_SCH_ID LIKE :V_YD_PREP_SCH_ID ||'%'  )
					                           
					                           AND A.DEL_YN = 'N'
					                           AND A.PRI_YD_PREP_SCH_ID IS NOT NULL
					                         ORDER BY YD_CARASGN_SEQ ASC
					                                , YD_PREP_SCH_ID ASC
					                       )
					               ) A
					             , TB_YD_PREPMTL B
					         WHERE A.YD_PREP_SCH_ID = B.YD_PREP_SCH_ID
					            AND B.DEL_YN = 'N'
					       ) B
					 WHERE A.STL_NO = B.STL_NO
					   AND A.DEL_YN = 'N'
					     
					 ORDER BY B.YD_PREP_SCH_ID  
					        , B.YD_STK_BED_NO_ROOT ASC
					        , B.YD_STK_COL_GP ASC
					        , B.YD_STK_BED_NO DESC
					        , B.YD_STK_LYR_NO DESC
					*/
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getYdStockNPrepSchByYdCrnCarGpThr";
					rsRst = commDao.select(jrParam, queryId, logId, mthdNm, "차량이송준비스케줄 조회(2Bed 대상)");
				}	
			} else { 						// 준비스케쥴ID가 파라미터로 없을 때 -> 차량도착 시(도착전문[TSYDJ003] 수신)
				
				// 가장 먼저 편성된 이송LOT가 '1BED 작업'일 때 조회된다.
				/*
				SELECT A.STL_NO        AS STL_NO
				      ,A.YD_AIM_RT_GP  AS YD_AIM_RT_GP
				      ,A.YD_MTL_ITEM   AS YD_MTL_ITEM
				      ,A.YD_MTL_L      AS YD_MTL_L
				      ,A.YD_MTL_W      AS YD_MTL_W
				      ,A.YD_MTL_WT     AS YD_MTL_WT
				      ,B.YD_PREP_SCH_ID
				      ,B.YD_SCH_CD
				      ,B.YD_GP 
				      ,B.YD_PREP_WK_ST
				      ,B.YD_TO_LOC_DCSN_MTD
				      ,B.YD_TO_LOC_GUIDE
				      ,B.ARR_WLOC_CD
				      ,B.YD_AIM_YD_GP
				      ,B.YD_AIM_BAY_GP
				      ,B.YD_CARASGN_SEQ
				      ,B.YD_EQP_WRK_SH
				      ,B.YD_WRK_PLAN_CRN
				      ,B.YD_STK_COL_GP AS YD_STK_COL_GP
				      ,B.YD_STK_BED_NO AS YD_STK_BED_NO
				      ,B.YD_STK_LYR_NO AS YD_STK_LYR_NO
				      ,B.YD_STK_BED_NO_ROOT
				      ,B.PRI_YD_PREP_SCH_ID
				  FROM TB_YD_STOCK  A
				     , (
				        SELECT A.YD_PREP_SCH_ID
				             , A.YD_SCH_CD
				             , A.YD_GP 
				             , A.YD_PREP_WK_ST
				             , A.YD_TO_LOC_DCSN_MTD
				             , A.YD_TO_LOC_GUIDE
				             , A.ARR_WLOC_CD
				             , A.YD_AIM_YD_GP
				             , A.YD_AIM_BAY_GP
				             , A.YD_CARASGN_SEQ
				             , A.YD_EQP_WRK_SH
				             , A.YD_WRK_PLAN_CRN
				             , B.STL_NO
				             , B.YD_STK_COL_GP AS YD_STK_COL_GP
				             , B.YD_STK_BED_NO AS YD_STK_BED_NO
				             , B.YD_STK_LYR_NO AS YD_STK_LYR_NO
				             , '01' YD_STK_BED_NO_ROOT
				             , A.PRI_YD_PREP_SCH_ID
				          FROM TB_YD_PREPSCH A
				             , TB_YD_PREPMTL B
				         WHERE YD_GP            = 'D'
				           AND A.YD_PREP_SCH_ID = B.YD_PREP_SCH_ID
				           AND YD_SCH_CD        LIKE :V_YD_SCH_CD || '%'
				           AND YD_PREP_WK_ST    LIKE :V_YD_PREP_WK_ST || '%'
				           AND NVL(CAR_GP, '*') LIKE :V_CAR_GP || '%'
				           AND A.DEL_YN = 'N'
				           AND A.PRI_YD_PREP_SCH_ID IS NULL
				       ) B
				     , (
				        SELECT * FROM (
				        SELECT A.YD_PREP_SCH_ID
				          FROM TB_YD_PREPSCH A
				         WHERE YD_GP            = 'D'
				           AND A.YD_SCH_CD        LIKE :V_YD_SCH_CD || '%'
				           AND A.YD_PREP_WK_ST    LIKE :V_YD_PREP_WK_ST || '%'
				           AND NVL(A.CAR_GP, '*') LIKE :V_CAR_GP || '%'
				           AND A.DEL_YN = 'N'
				           ORDER BY YD_PREP_SCH_ID
				        ) 
				        WHERE ROWNUM = 1
				       ) C
				 WHERE A.STL_NO = B.STL_NO
				   AND B.YD_PREP_SCH_ID = C.YD_PREP_SCH_ID
				   AND A.DEL_YN = 'N'
				 ORDER BY B.YD_STK_COL_GP ASC
				        , B.YD_STK_BED_NO DESC
				        , B.YD_STK_LYR_NO DESC
				*/
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getYdStockNPrepSchByYdCrnCarGp";
				rsRst = commDao.select(jrParam, queryId, logId, mthdNm, "차량 도착 시 차량이송준비스케줄 조회(1Bed 대상)");
				
				if(rsRst.size() == 0) {	// 가장 먼저 편성된 이송LOT가 '2BED 작업'일 때 조회된다.
					/*
						SELECT A.STL_NO        AS STL_NO
						      ,A.YD_AIM_RT_GP  AS YD_AIM_RT_GP
						      ,A.YD_MTL_ITEM   AS YD_MTL_ITEM
						      ,A.YD_MTL_L      AS YD_MTL_L
						      ,A.YD_MTL_W      AS YD_MTL_W
						      ,A.YD_MTL_WT     AS YD_MTL_WT
						      ,B.YD_PREP_SCH_ID
						      ,B.YD_SCH_CD
						      ,B.YD_GP 
						      ,B.YD_PREP_WK_ST
						      ,B.YD_TO_LOC_DCSN_MTD
						      ,B.YD_TO_LOC_GUIDE
						      ,B.ARR_WLOC_CD
						      ,B.YD_AIM_YD_GP
						      ,B.YD_AIM_BAY_GP
						      ,B.YD_CARASGN_SEQ
						      ,B.YD_EQP_WRK_SH
						      ,B.YD_WRK_PLAN_CRN
						      ,B.YD_STK_COL_GP AS YD_STK_COL_GP
						      ,B.YD_STK_BED_NO AS YD_STK_BED_NO
						      ,B.YD_STK_LYR_NO AS YD_STK_LYR_NO
						      ,B.YD_STK_BED_NO_ROOT
						      ,B.PRI_YD_PREP_SCH_ID
						  FROM TB_YD_STOCK  A
						     , (
						        SELECT A.YD_PREP_SCH_ID
						             , A.YD_SCH_CD
						             , A.YD_GP 
						             , A.YD_PREP_WK_ST
						             , A.YD_TO_LOC_DCSN_MTD
						             , A.YD_TO_LOC_GUIDE
						             , A.ARR_WLOC_CD
						             , A.YD_AIM_YD_GP
						             , A.YD_AIM_BAY_GP
						             , A.YD_CARASGN_SEQ
						             , A.YD_EQP_WRK_SH
						             , A.YD_WRK_PLAN_CRN
						             , B.STL_NO
						             , B.YD_STK_COL_GP AS YD_STK_COL_GP
						             , B.YD_STK_BED_NO AS YD_STK_BED_NO
						             , B.YD_STK_LYR_NO AS YD_STK_LYR_NO
						             , YD_CAR_UPP_LOC_CD AS YD_STK_BED_NO_ROOT
						             , A.PRI_YD_PREP_SCH_ID
						          FROM ( 
						                SELECT *
						                  FROM (
						                  
						                        SELECT *
						                          FROM TB_YD_PREPSCH A
						                         WHERE YD_GP = 'D'
						                           AND YD_SCH_CD        LIKE :V_YD_SCH_CD || '%'
						                           AND YD_PREP_WK_ST    LIKE :V_YD_PREP_WK_ST || '%'
						                           AND NVL(CAR_GP, '*') LIKE :V_CAR_GP || '%'
						                           AND A.DEL_YN = 'N'
						                           AND A.PRI_YD_PREP_SCH_ID IS NOT NULL
						                         ORDER BY YD_CARASGN_SEQ ASC
						                                , YD_PREP_SCH_ID ASC
						                       )
						                  WHERE ROWNUM < 3
						               ) A
						             , TB_YD_PREPMTL B
						         WHERE A.YD_PREP_SCH_ID = B.YD_PREP_SCH_ID
						            AND B.DEL_YN = 'N'
						       ) B
						 WHERE A.STL_NO = B.STL_NO
						   AND A.DEL_YN = 'N'
						 ORDER BY B.YD_STK_BED_NO_ROOT ASC
						        , B.YD_STK_COL_GP ASC
						        , B.YD_STK_BED_NO DESC
						        , B.YD_STK_LYR_NO DESC
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getYdStockNPrepSchByYdCrnCarGpTwo";
					rsRst = commDao.select(jrParam, queryId, logId, mthdNm, "차량 도착 시 차량이송준비스케줄 조회(2Bed 대상)");
				}
			}
			
			if (rsRst.size() > 0 ) {
				
				String sAPPLY_YN36 		= ""; // (차량)구내운송상차차량적재MAX용량  
				/* com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYnDualSql
				SELECT 'Y' AS APPLY_YN36         FROM DUAL --(차량)상차차량 작업예약 생성 시 차량적재허용중량 체크유무 MJG
				*/  
				JDTORecordSet jsApplyYNChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYnDualSql", logId, mthdNm, "APPLY_YN36-차량적재허용중량 체크유무"); 
				if (jsApplyYNChk.size() > 0) {
					sAPPLY_YN36    = slabUtils.trim(jsApplyYNChk.getRecord(0).getFieldString("APPLY_YN36"));
				}
				
				
				slabUtils.printLog(logId, "▷  기준적용 확인(APPLY_YN36) :"+ sAPPLY_YN36, "SL");
				
				if("Y".equals(sAPPLY_YN36)){
					
					// 2021.09.08 민종근 추가 : 이송Lot 재료의 총 중량이 차량의 상차허용중량을 넘어가면 작업예약을 만들지 않는다.
					int iTRN_EQP_STK_CAPA	= Integer.parseInt(slabUtils.nvl(rcvMsg.getFieldString("TRN_EQP_STK_CAPA"), "1000000"));	// 운송장비적재능력
					int iYD_MTL_WT_SUM		= Integer.parseInt(rsRst.getRecord(0).getFieldString("YD_MTL_WT_SUM"));						// 이송LOT의 재료 중량 합계
					
					slabUtils.printLog(logId, "▷  차량허용중량(TRN_EQP_STK_CAPA) : "+ iTRN_EQP_STK_CAPA	, "SL");
					slabUtils.printLog(logId, "▷  이송LOT재료중량(YD_MTL_WT_SUM) : "+ iYD_MTL_WT_SUM		, "SL");
					
					if(iTRN_EQP_STK_CAPA < iYD_MTL_WT_SUM){
						slabUtils.printLog(logId, "상차LOT 중량이 운송장비적재능력을 초과합니다.", "SL");
						
						jrRtn.setField("LOT_YN"	, "N"							);
						jrRtn.setField("RTN_MSG", "상차LOT 중량이 운송장비적재능력을 초과합니다."	);
						return jrRtn;
					}
					
				}
				
		    	rsRst.first();
				String ydAimYdGp   		= slabUtils.trim(rsRst.getRecord().getFieldString("YD_AIM_YD_GP"  ));
				String ydAimBayGp  		= slabUtils.trim(rsRst.getRecord().getFieldString("YD_AIM_BAY_GP" ));
				String sPriYdPrepSchId 	= slabUtils.trim(rsRst.getRecord().getFieldString("PRI_YD_PREP_SCH_ID" ));
				String ydPrepSchId 		= "";    
				String ydStkBednoRoot2  = ""; 
				
				slabUtils.printLog(logId, "YD_PREP_SCH_ID:"+ ydPrepSchId, "SL");
				slabUtils.printLog(logId, "YD_AIM_YD_GP  :"+ ydAimYdGp  , "SL"); 
				slabUtils.printLog(logId, "YD_AIM_BAY_GP :"+ ydAimBayGp , "SL"); 
				
				/*------------------------------------------------------------------------------------
				 * 3.스케줄 기준 조회 
				 *------------------------------------------------------------------------------------ */
				ydSchCd = ydStkColGp + "UM";
				jrParam.setField("YD_SCH_CD"     , ydSchCd);
				/* # com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getYdSchrule
				SELECT A.*
				     , CASE WHEN WRK_EQUIP_STAT != 'B' THEN YD_WRK_CRN_PRIOR
				            WHEN WRK_EQUIP_STAT  = 'B' AND YD_ALT_CRN_YN = 'Y' THEN CASE WHEN ALT_EQUIP_STAT != 'B' THEN YD_ALT_CRN_PRIOR ELSE 99 END
				            ELSE 99
				        END AS YD_SCH_PRIOR
				     , CASE WHEN WRK_EQUIP_STAT != 'B' THEN YD_WRK_CRN
				            WHEN WRK_EQUIP_STAT  = 'B' AND YD_ALT_CRN_YN = 'Y' THEN CASE WHEN ALT_EQUIP_STAT != 'B' THEN YD_ALT_CRN ELSE '99' END
				            ELSE '99'
				        END AS YD_SCH_CRN
				  FROM (
				        SELECT YD_SCH_CD
				             , YD_GP
				             , YD_BAY_GP
				             , YD_SCH_RNG_CD
				             , YD_SCH_WHIO_GP
				             , YD_SCH_DIV_GP
				             , YD_SCH_RULE_ACT_STAT
				             , YD_WRK_CRN
				             , YD_WRK_CRN_PRIOR
				             , YD_ALT_CRN_YN
				             , YD_ALT_CRN
				             , YD_ALT_CRN_PRIOR
				             , CD_CONTENTS       
				             , YD_SCH_PROH_EXN   
				             , (SELECT YD_EQP_STAT FROM TB_YD_EQP WHERE YD_EQP_ID = SR.YD_WRK_CRN) AS WRK_EQUIP_STAT
				             , (SELECT YD_EQP_STAT FROM TB_YD_EQP WHERE YD_EQP_ID = SR.YD_ALT_CRN) AS ALT_EQUIP_STAT
				          FROM TB_YD_SCHRULE   SR
				         WHERE YD_SCH_CD = :V_YD_SCH_CD
				       ) A
				 WHERE 1 = 1
				 */
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getYdSchrule";
				JDTORecordSet jsSchrule = commDao.select(jrParam, queryId, logId, mthdNm, "스케줄 기준 조회");
		    	
				String ydSchPrior = "";
				if (jsSchrule.size() > 0) {
					ydSchPrior    = slabUtils.trim(jsSchrule.getRecord(0).getFieldString("YD_SCH_PRIOR"));		//--(작업가능)야드작업크레인우선순위
					ydWrkPlanCrn  = slabUtils.trim(jsSchrule.getRecord(0).getFieldString("YD_SCH_CRN"));
					slabUtils.printLog(logId, "YD_SCH_PRIOR:" + ydSchPrior, "SL");
					
					if ("99".equals(ydSchPrior)) {
						ydSchPrior = "";
						sMsg = "##해당 스케줄("+ ydSchCd +")에 적합한 작업가능한 크레인이 없습니다.";
						slabUtils.printLog(logId, sMsg, "SL");
						// 작업가능 크레인이 없는 상태이므로, Lot 편성 및 차량화면에서  match하여야 함
						jrRtn.setField("LOT_YN"	, "N");
				    	jrRtn.setField("RTN_CD"	, "0");
				    	jrRtn.setField("RTN_MSG", sMsg);
				    	return jrRtn;
					}
				}
				
				/*------------------------------------------------------------------------------------
				 * 4.작업예약 생성 처리 : 구내운송 차량이 도착하여  상차Lot 편성된 사항을 확인하고, 작업 예약을 생성 처리 하는 부분 
				 *------------------------------------------------------------------------------------ */
				slabUtils.printLog(logId, "4.작업예약 생성 처리", "SL"); 
				//String ydWbookId  	= "";
				String sStlNo 		= "";
				String sPtLoadLoc 	= "", sBedcomp = "";
				int    iWbookIdCnt = 0, kk = 0; 

				rsRst.first();
				for (int ii = 1; ii <= rsRst.size(); ++ii) {
					
					ydPrepSchId2     = slabUtils.trim(rsRst.getRecord(ii-1).getFieldString("YD_PREP_SCH_ID"));
					ydStkBednoRoot2  = slabUtils.trim(rsRst.getRecord(ii-1).getFieldString("YD_STK_BED_NO_ROOT" ));
	
					if("".equals(ydStkBednoRoot2)) {
						ydStkBednoRoot2 = "01";
					}
					slabUtils.printLog(logId, "YD_PREP_SCH_ID:"+ ydPrepSchId2 + " YD_STK_BED_NO_ROOT:" + ydStkBednoRoot2, "SL");
					
					iWbookIdCnt++;
					if(iWbookIdCnt == 1 || !sBedcomp.equals(ydStkBednoRoot2) ) {
	
						sBedcomp = ydStkBednoRoot2;
						kk=0;
						//작업예약ID생성
						ydWbookId = commDao.getSeqId(logId, mthdNm, "WrkBook");
						
						slabUtils.printLog(logId, "YD_SCH_CD"+ ydSchCd + "("+ ydWbookId + ")"+ ydStkBednoRoot2, "SL"); 
						/**********************************************************
						 * 4-1. 작업예약(TB_YD_WRKBOOK) 생성
						 **********************************************************/
						jrParam.setField("YD_WBOOK_ID"		, ydWbookId);
						jrParam.setField("YD_GP"			, "D");
						jrParam.setField("YD_BAY_GP"		, ydBayGp );  		// ydSchCd.substring(1, 2)
						jrParam.setField("YD_SCH_CD"		, ydSchCd); 		//야드스케쥴코드
						jrParam.setField("YD_SCH_PRIOR"		, ydSchPrior); 		//야드스케쥴우선순위    
						jrParam.setField("YD_SCH_PROG_STAT"	, "W"); 			//야드스케쥴진행상태(W:스케줄수행대기)
						jrParam.setField("YD_SCH_REQ_GP"	, "F"); 			//야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
						jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd); 		//운송장비코드
						jrParam.setField("YD_CAR_USE_GP"	, "L"); 			//야드차량사용구분 L:구내운송
						jrParam.setField("CAR_NO"			, "" );
						jrParam.setField("YD_AIM_BAY_GP"    , ydAimBayGp  );   	//야드목표동구분 ydAimBayGp
						jrParam.setField("YD_AIM_YD_GP"     , ydAimYdGp );
						jrParam.setField("YD_WRK_PLAN_CRN"  , ydWrkPlanCrn );//야드작업계획크레인  
						jrParam.setField("YD_TO_LOC_GUIDE"  , ydStkColGp+ydStkBednoRoot2  );
						
						slabUtils.printLog(logId, "YD_BAY_GP:"		+ ydBayGp	+ " YD_SCH_PRIOR:"	+ ydSchPrior, "SL");
						slabUtils.printLog(logId, "YD_AIM_BAY_GP:"	+ ydAimBayGp+ " YD_AIM_YD_GP:"	+ ydAimYdGp	, "SL");
						
						/*
							INSERT INTO TB_YD_WRKBOOK (
						       YD_WBOOK_ID        --야드작업예약ID
						     , YD_GP              --야드구분
						     , YD_BAY_GP          --야드동구분
						     , YD_SCH_CD          --야드스케쥴코드
						     , YD_SCH_PRIOR       --야드스케쥴우선순위
						     , YD_SCH_PROG_STAT   --야드스케쥴진행상태
						     , YD_SCH_ST_GP       --야드스케쥴기동구분
						     , YD_SCH_REQ_GP      --야드스케쥴요청구분
						     , YD_AIM_YD_GP       --야드목표야드구분
						     , YD_AIM_BAY_GP      --야드목표동구분
						     , YD_TO_LOC_DCSN_MTD --야드To위치결정방법
						     , YD_TO_LOC_GUIDE    --야드To위치Guide
						     , YD_WRK_PLAN_TCAR   --야드작업계획대차
						     , YD_CAR_USE_GP      --야드차량사용구분
						     , TRN_EQP_CD         --운송장비코드
						     , CAR_NO             --차량번호
						     , CARD_NO            --카드번호
						     , PTOP_PLNT_GP       --조업공장구분
						     , DEST_TEL_NO        --목적지전화번호
						     , DIST_SHIPASSIGN_GP --출하배선지시구분 
						     , YD_WRK_PLAN_CRN    --야드작업계획크레인
						     , REGISTER           --등록자
						     , REG_DDTT           --등록일시
						     , MODIFIER           --수정자
						     , MOD_DDTT           --수정일시
						     , DEL_YN             --삭제유무
							) VALUES (
						      :V_YD_WBOOK_ID
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
						     ,:V_PTOP_PLNT_GP
						     ,:V_DEST_TEL_NO
						     ,:V_DIST_SHIPASSIGN_GP
						     ,:V_YD_WRK_PLAN_CRN      
						     ,:V_MODIFIER
						     ,SYSDATE
						     ,:V_MODIFIER
						     ,SYSDATE
						     ,'N'
							)
						 */
						queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.insWrkBook";
						commDao.insert(jrParam, queryId, logId, mthdNm, "작업예약(TB_YD_WRKBOOK) 생성");
					}	
					if(iWbookIdCnt == 1 ) {
				    	ydPrepSchId     = slabUtils.trim(rsRst.getRecord(ii-1).getFieldString("YD_PREP_SCH_ID"));
				    	ydWbookId2      = ydWbookId;
					}
	
					/**********************************************************
					 * 4-2. 작업예약재료(TB_YD_WRKBOOKMTL) 생성
					 **********************************************************/
					sStlNo 			  = slabUtils.trim(rsRst.getRecord(ii-1).getFieldString("STL_NO"));
					slabUtils.printLog(logId, "["+ ii +"] " + sStlNo, "SL");
					sPtLoadLoc 		  = slabUtils.trim(rsRst.getRecord(ii-1).getFieldString("YD_STK_COL_GP"));
					String ydStkBedNo = slabUtils.trim(rsRst.getRecord(ii-1).getFieldString("YD_STK_BED_NO"));
					String ydStkLyrNo = slabUtils.trim(rsRst.getRecord(ii-1).getFieldString("YD_STK_LYR_NO"));
					
					jrParam.setField("YD_WBOOK_ID"		, ydWbookId);
					jrParam.setField("STL_NO"			, sStlNo);
					jrParam.setField("YD_STK_COL_GP"	, sPtLoadLoc);
					jrParam.setField("YD_STK_BED_NO"	, ydStkBedNo);
					jrParam.setField("YD_STK_LYR_NO"	, ydStkLyrNo); 					//야드적치단번호
					jrParam.setField("YD_UP_COLL_SEQ"   , slabUtils.format(kk+1, 2)); 	//야드권상모음순서
					kk++;
					/*
						INSERT INTO TB_YD_WRKBOOKMTL (
						  YD_WBOOK_ID       --야드작업예약ID
						 ,STL_NO            --재료번호
						 ,YD_STK_COL_GP     --야드적치열구분
						 ,YD_STK_BED_NO     --야드적치BED번호
						 ,YD_STK_LYR_NO     --야드적치단번호
						, YD_UP_COLL_SEQ
						 ,REGISTER          --등록자
						 ,REG_DDTT          --등록일시
						 ,MODIFIER          --수정자
						 ,MOD_DDTT          --수정일시
						 ,DEL_YN            --삭제유무
						) VALUES (
						  :V_YD_WBOOK_ID
						 ,:V_STL_NO
						 ,:V_YD_STK_COL_GP
						 ,:V_YD_STK_BED_NO
						 ,:V_YD_STK_LYR_NO
						 ,:V_YD_UP_COLL_SEQ
						 ,:V_MODIFIER
						 ,SYSDATE
						 ,:V_MODIFIER
						 ,SYSDATE
						 ,'N'
						)
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.insWrkBookMtl";
					commDao.insert(jrParam, queryId, logId, mthdNm, "작업예약재료(TB_YD_WRKBOOKMTL) 생성");
					
				}
				/**********************************************************
				 * 4-3. 차량스케쥴  : YD_CARLD_WRK_BOOK_ID에 상차작업예약id update
				 **********************************************************/
				jrParam.setField("TRN_EQP_CD"			, sTrnEqpCd);
				jrParam.setField("YD_CARLD_WRK_BOOK_ID"	, ydWbookId2); //twoBed 이면  (USRYDA.TB_YD_PREPSCH ※PRI_YD_PREP_SCH_ID )
				jrParam.setField("PRI_YD_PREP_SCH_ID"	, sPriYdPrepSchId);
				/* 
				UPDATE TB_YD_CARSCH
				  SET  MODIFIER            = :V_MODIFIER
				      ,MOD_DDTT            = SYSDATE
				      ,YD_CARLD_WRK_BOOK_ID= :V_YD_CARLD_WRK_BOOK_ID
				WHERE DEL_YN = 'N' 
				AND TRN_EQP_CD           = :V_TRN_EQP_CD
	
				 */			
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updCarschWbookid";
				commDao.update(jrParam, queryId, logId, mthdNm, "차량스케쥴(상차작업예약id UPDATE)");
				//++++++++++++++ 차량예약 생성 (끝) ++++++++++++++++++++++++++++++++++++++++++++++++++++++
				
				/********************************************************** 
				 * 5. Lot편성 생성 및 삭제
				 **********************************************************/
				slabUtils.printLog(logId, "5. Lot편성 내역  삭제  (YD_PREP_SCH_ID)"+ ydPrepSchId, "SL");
				
				if (!"".equals(ydPrepSchId)) {
					slabUtils.printLog(logId, "YD_PREP_SCH_ID:"+ydPrepSchId+ "YD_WBOOK_ID   :"+ydWbookId2, "SL");
					
					jrParam.setField("YD_PREP_SCH_ID", ydPrepSchId);
					jrParam.setField("YD_WBOOK_ID"   , ydWbookId2  );
					jrParam.setField("DEL_YN"        , "Y"        );
					/*
						UPDATE TB_YD_PREPMTL
						   SET MODIFIER       = :V_MODIFIER
						     , MOD_DDTT       = SYSDATE
						     , DEL_YN         = :V_DEL_YN
						 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.delYdPrepmtlByPrepSchId"; 
					commDao.update(jrParam, queryId, logId, mthdNm, "준비스케줄재료 삭제");
					
					/*
						UPDATE TB_YD_PREPSCH
						   SET DEL_YN         = :V_DEL_YN
						     , YD_WBOOK_ID    = :V_YD_WBOOK_ID
						     , MODIFIER       = :V_MODIFIER
						     , MOD_DDTT       = SYSDATE
						 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.delYdPrepsch";  
					commDao.update(jrParam, queryId, logId, mthdNm, "준비스케줄 삭제");
				}
	
				if (!"".equals(ydPrepSchId2)) {
					slabUtils.printLog(logId, "YD_PREP_SCH_ID(2):"+ydPrepSchId2+ "YD_WBOOK_ID(2)   :"+ydWbookId, "SL");
					
					jrParam.setField("YD_PREP_SCH_ID", ydPrepSchId2);
					jrParam.setField("YD_WBOOK_ID"   , ydWbookId  );
					jrParam.setField("DEL_YN"        , "Y"        );
					/*
						UPDATE TB_YD_PREPMTL
						   SET MODIFIER       = :V_MODIFIER
						     , MOD_DDTT       = SYSDATE
						     , DEL_YN         = :V_DEL_YN
						 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.delYdPrepmtlByPrepSchId"; 
					commDao.update(jrParam, queryId, logId, mthdNm, "준비스케줄재료 삭제(2)");
					 
					/*
						UPDATE TB_YD_PREPSCH
						   SET DEL_YN         = :V_DEL_YN
						     , YD_WBOOK_ID    = :V_YD_WBOOK_ID
						     , MODIFIER       = :V_MODIFIER
						     , MOD_DDTT       = SYSDATE
						 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.delYdPrepsch";  
					commDao.update(jrParam, queryId, logId, mthdNm, "준비스케줄 삭제(2)");
				}
				slabUtils.printLog(logId, "6.Lot편성 기준 예약작업 자동생성 완료" +" YD_WBOOK_ID:"+ydWbookId  +" YD_SCH_CD  :"+ydSchCd, "SL"); 
				
				jrRtn.setField("YD_WBOOK_ID"	, ydWbookId);
				jrRtn.setField("YD_WBOOK_ID2"	, ydWbookId2);
				jrRtn.setField("YD_SCH_CD"  	, ydSchCd  );
				jrRtn.setField("YD_EQP_ID"  	, ydWrkPlanCrn);
	
				jrRtn.setField("LOT_YN"	, "Y");
				jrRtn.setField("RTN_MSG", "처리완료");
			} else {
				jrRtn.setField("LOT_YN"	, "N");
				jrRtn.setField("RTN_MSG", "대상재 없음 ");
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
	 * [A] 오퍼레이션명 : 후판SLAB 예약 차량초기화 작업 (구내운송 예약 차량 초기화)   
	 * 야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public void initBookCarSch(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "후판Slab 예약 차량초기화 작업[PSlabYdMvCarSeEJB.initBookCarSch] < " + rcvMsg.getResultMsg();
		String logId    = rcvMsg.getResultCode();
		
	    try{
			slabUtils.printLog(logId, methodNm, "S+");
			String szTrnEqpCd			= "";
			String szTrnWrkFullvoidGp   = "";			//운송작업영공구분
			String szBackupYn			= "";
			String msgId				= "";
			String modifier				= "";

	    	//수신항목 변수 저장
			msgId    		= slabUtils.nvl(slabUtils.getMsgId(rcvMsg),"TSYDJ004"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			modifier 		= slabUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			szTrnEqpCd    	= slabUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); //운송장비코드
			szTrnWrkFullvoidGp   = slabUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			szBackupYn		= slabUtils.nvl(rcvMsg.getFieldString("BACKUP_YN"), "N"); //BACKUP 구분 (화면에서 강제초기화시 Y)
			//String szWlocCd	= slabUtils.nvl(rcvMsg.getFieldString("WLOC_CD"), ""); //개소코드
			
			JDTORecord jrParam  = slabUtils.getParam(logId, methodNm, modifier);
			if ("".equals(modifier)) { modifier = msgId; }
			
			//파라메터 JDTORecord 생성 - Log ID, Method, 수정자 Set
			jrParam = slabUtils.getParam(logId, methodNm, modifier);
			
			//초기화 대상 차량스케줄이 존재함..
		
			if ("E".equals(szTrnWrkFullvoidGp)||"Y".equals(szBackupYn)) {
					//운송작업영공구분
				
				//**********************************************************************************	
				//7. 차량위치(적치열) 정리 작업
				
				//차량위치 예정정보 삭제(상하차출발 위치) 정리
				jrParam.setField("CAR_CARD_NO", szTrnEqpCd); //운송장비코드
				/*
						UPDATE  TB_YD_STKCOL
						   SET  CARD_NO = ''
						       ,YD_STK_COL_ACT_STAT = ''
						       ,MODIFIER = :V_MODIFIER
						       ,MOD_DDTT = SYSDATE
						 WHERE  CARD_NO = :V_CAR_CARD_NO  --TRN_EQP_CD     
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updStackStatByTrnEqpCd", logId, methodNm, "적치열 차량위치 예정정보 삭제(상하차출발 위치)정리 ");
				
				//차량위치정보 삭제(상하차개시/완료/도착 위치) 정리
				jrParam.setField("TRN_EQP_CD", szTrnEqpCd); //운송장비코드
				/* 운송장비코드로 차량위치정보 삭제(상하차개시/완료/도착 위치) 정리
				 * 
					UPDATE  TB_YD_STKCOL
					   SET  TRN_EQP_CD = ''
					       ,YD_CAR_USE_GP = ''
					       ,MODIFIER = :V_MODIFIER
					       ,MOD_DDTT = SYSDATE
					WHERE TRN_EQP_CD = :V_TRN_EQP_CD 
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updCarUseGpByTrnEqpCd", logId, methodNm, "적치열 차량위치정보 삭제(상하차개시/완료/도착 위치)정리 ");

				//**********************************************************************************	
				//8. 차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				this.YdCarPointinforeg(  "1",""
										, szTrnEqpCd,"","",""
										, "C"
										, logId, methodNm);
			}
			
			slabUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 		


	/**
	 * [A] 오퍼레이션명 : 후판slab 차량초기화 작업 (구내운송 차량 초기화)
	 * 야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public void initCarSch(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "후판Slab 차량초기화 작업[PSlabYdMvCarSeEJB.initCarSch] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
	    try{
	    	slabUtils.printLog(logId, methodNm, "S+");
	    	String szMsg           		= "";
	    	String szTrnEqpCd			= "";
	    	String szTrnWrkFullvoidGp	= "";		//운송작업영공구분
	    	String szBackupYn			= "";
	    	String szWlocCd				= "";
	    	String msgId				= "";
	    	String modifier				= "";
	    	
	    	JDTORecordSet rsResult    	= null;
			
	    	//수신항목 변수 저장
			msgId    			= slabUtils.nvl(slabUtils.getMsgId(rcvMsg),"TSYDJ004"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			modifier 			= slabUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			szTrnEqpCd    		= slabUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); //운송장비코드
			szTrnWrkFullvoidGp  = slabUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			szBackupYn			= slabUtils.nvl(rcvMsg.getFieldString("BACKUP_YN"), "N"); //BACKUP 구분 (화면에서 강제초기화시 Y)
			szWlocCd			= slabUtils.nvl(rcvMsg.getFieldString("WLOC_CD"), ""); //개소코드
			
			if ("".equals(modifier)) { modifier = msgId; }
			
			//파라메터 JDTORecord 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);
			
			slabUtils.printLog(logId, "[initCarSch] szTrnWrkFullvoidGp:" + szTrnWrkFullvoidGp + "(F:영차,하차)" + " szBackupYn:" + szBackupYn 	, "SL");	//운송작업영공구분
			if( "E".equals(szTrnWrkFullvoidGp) ) {
				slabUtils.printLog(logId, "--초기화는 상차(E)+백업(Y)만 대상임--", "SL");
			}
			
			if ("E".equals(szTrnWrkFullvoidGp)||"Y".equals(szBackupYn)) {

				if ("Y".equals(szBackupYn)) {
					//**********************************************************************************	
					//3. 크레인 스케줄 편성 상태인지 체크
					jrParam.setField("TRN_EQP_CD", szTrnEqpCd); //운송장비코드
					/* # com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCrnSchByTrnEqpCd
					 * 
					SELECT  E.* --하차대상
					  FROM  TB_YD_CARSCH A
					       ,TB_YD_CARFTMVMTL B
					       ,TB_YD_WRKBOOK C 
					       ,TB_YD_WRKBOOKMTL D
					       ,TB_YD_CRNSCH E
					 WHERE  A.TRN_EQP_CD = :V_TRN_EQP_CD
					   AND  A.YD_CAR_SCH_ID=B.YD_CAR_SCH_ID
					   AND  A.DEL_YN = 'N'
					   AND  B.STL_NO = D.STL_NO
					   AND  D.DEL_YN = 'N'
					   AND  D.YD_WBOOK_ID = C.YD_WBOOK_ID
					   AND  C.DEL_YN = 'N'
					   AND  C.YD_WBOOK_ID = E.YD_WBOOK_ID
					   AND  E.DEL_YN = 'N'
					   AND  E.YD_SCH_CD LIKE SUBSTR(E.YD_SCH_CD,1,2)||'PT0_LM%' --하차스케줄
					
					UNION 
					
					SELECT  E.* --상차대상
					  FROM  TB_YD_CARSCH A
					       ,TB_YD_STOCK B
					       ,TB_YD_WRKBOOK C 
					       ,TB_YD_WRKBOOKMTL D
					       ,TB_YD_CRNSCH E
					 WHERE  A.TRN_EQP_CD = :V_TRN_EQP_CD
					   AND  A.FRTOMOVE_WORD_NO=B.CAR_FRTOMOVE_WORD_NO
					   AND  A.DEL_YN = 'N'
					   AND  B.STL_NO = D.STL_NO
					   AND  D.DEL_YN = 'N'
					   AND  D.YD_WBOOK_ID = C.YD_WBOOK_ID
					   AND  C.DEL_YN = 'N'
					   AND  C.YD_WBOOK_ID = E.YD_WBOOK_ID
					   AND  E.DEL_YN = 'N'
					   AND  E.YD_SCH_CD LIKE SUBSTR(E.YD_SCH_CD,1,2)||'PT0_UM%' --상차스케줄
					 */
					rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCrnSchByTrnEqpCd", logId, methodNm, "크레인 스케줄 편성 상태인지 체크");
					if (rsResult.size() > 0) {
						szMsg = " 이송대상제가 크레인 스케줄 편성상태 입니다!!! 크레인스케줄을 취소한 후에 초기화를 실행하세요. << " + methodNm;
						slabUtils.printLog(logId, szMsg, "SL");
						throw new Exception(szMsg);
					}
				
					//**********************************************************************************	
					//4. Layer(단) 저장품 상태를 'C:적치중'로 초기화  - ejb.transaction type="RequiresNew"
					jrParam.setField("TRN_EQP_CD"			, szTrnEqpCd); //운송장비코드
					jrParam.setField("YD_STK_LYR_ACT_STAT"	, "C"); //적치단 상태 C:적치중
					szMsg = " Layer(단) 저장품 상태를 'C:적치중'로 초기화  << " + methodNm;
					slabUtils.printLog(logId, szMsg, "SL");
					/*
						UPDATE TB_YD_STKLYR
						SET YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
						   ,MODIFIER = :V_MODIFIER
						   ,MOD_DDTT = SYSDATE
						WHERE STL_NO IN (
						                    SELECT 	--하차대상
						                         C.STL_NO
						                    FROM TB_YD_CARSCH A
						                        ,TB_YD_CARFTMVMTL B
						                        ,TB_YD_STKLYR C
						                    WHERE A.YD_CAR_SCH_ID=B.YD_CAR_SCH_ID
						                      AND B.STL_NO = C.STL_NO
						                      AND A.TRN_EQP_CD = :V_TRN_EQP_CD
						                      AND A.DEL_YN='N' 
						               UNION
						                    SELECT 	--상차대상
						                         C.STL_NO
						                    FROM TB_YD_CARSCH A
						                        ,TB_YD_STOCK B
						                        ,TB_YD_STKLYR C
						                    WHERE A.FRTOMOVE_WORD_NO=B.CAR_FRTOMOVE_WORD_NO
						                      AND B.STL_NO = C.STL_NO
						                      AND A.TRN_EQP_CD = :V_TRN_EQP_CD
						                      AND A.DEL_YN='N' 
						                  ) 
					 */
					String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updLayerStatByTrnEqpCd2";
					commDao.update(jrParam, queryId, logId, methodNm, "수정");

				
					//**********************************************************************************	
					//7. 차량위치(적치열) 정리 작업
					////////////////////////////////////////////////////////////////////////////////////////
					//발지 차량정보 삭제 하기 - 적치단 정리 - ejb.transaction type="RequiresNew"
					jrParam.setField("YD_STK_LYR_ACT_STAT"		, "C"			); //적치 단 활성 상태 C:비활성화
					jrParam.setField("WLOC_CD"	 				, szWlocCd		); //개소코드
					jrParam.setField("TRN_EQP_CD"				, szTrnEqpCd	); //운송장비코드
					jrParam.setField("MODIFIER"					, modifier		);
					szMsg = " 발지 차량정보 삭제 하기 - 적치단 정리  << " + methodNm;
					slabUtils.printLog(logId, szMsg, "SL");
					/*
					UPDATE TB_YD_STKLYR
					   SET STL_NO = ''
					      ,YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
					      ,YD_STK_LYR_MTL_STAT = 'E' 
					      ,MODIFIER         = :V_MODIFIER
					      ,MOD_DDTT         = SYSDATE
					 WHERE YD_STK_COL_GP  in (
					                         --개소코드와 차량번호로 적치열을 찾는다. 
					                         SELECT STACK_COL_GP
					                           FROM (
					                             SELECT / *+INDEX_DESC(A PK_YD_CARSCH)* /
					                                        (CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN YD_CARLD_STOP_LOC ELSE  YD_CARUD_STOP_LOC END) AS STACK_COL_GP
					                                        ,TRN_EQP_CD
					                                FROM USRYDA.TB_YD_CARSCH A
					                                WHERE NVL(CARD_NO,TRN_EQP_CD)=:V_TRN_EQP_CD 
					                                 AND YD_CAR_SCH_ID>=TO_CHAR(SYSDATE-1,'YYYYMMDD')                             
					                                 AND ROWNUM<=1      
					                             ) A
					                           WHERE EXISTS(SELECT 1 FROM USRYDA.TB_YD_STKCOL B 
					                                            WHERE B.TRN_EQP_CD=A.TRN_EQP_CD 
					                                              AND B.YD_STK_COL_GP=A.STACK_COL_GP)
					                      ) 
					*/
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updateLayerstat_02";
					commDao.update(jrParam, queryId, logId, methodNm, "수정");
										
				}
				
				//차량위치 예정정보 삭제(상하차출발 위치) 정리 - ejb.transaction type="RequiresNew"
				jrParam.setField("TRN_EQP_CD"			, szTrnEqpCd); //운송장비코드
				jrParam.setField("YD_STK_COL_ACT_STAT"	, "C");
				/* 
					UPDATE  TB_YD_STKCOL
					   SET  CARD_NO             = ''
					       ,CAR_NO              = ''
					       ,YD_STK_COL_ACT_STAT = NVL(:V_YD_STK_COL_ACT_STAT, YD_STK_COL_ACT_STAT)
					       ,MODIFIER            = :V_MODIFIER
					       ,MOD_DDTT            = SYSDATE
					 WHERE  TRN_EQP_CD          = :V_TRN_EQP_CD  --변경전:CAR_CARD_NO
					   AND  DEL_YN              = 'N'  
    			*/
				String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updStackStatByTrnEqpCd02";
				commDao.update(jrParam, queryId, logId, methodNm, "차량위치 예정정보 삭제(상하차출발 위치)정리 ");
				
				szMsg = " 차량위치 예정정보 삭제(상하차출발 위치)정리  << " + methodNm;
				slabUtils.printLog(logId, szMsg, "SL");
				
				//차량위치정보 삭제(상하차개시/완료/도착 위치) 정리 - ejb.transaction type="RequiresNew"
				jrParam.setField("TRN_EQP_CD"	, szTrnEqpCd); //운송장비코드
				jrParam.setField("STAT"			, "" 	    );	
				/*
				UPDATE  TB_YD_STKCOL
				   SET  TRN_EQP_CD = ''
				       ,YD_CAR_USE_GP = ''
				       ,MODIFIER = :V_MODIFIER
				       ,MOD_DDTT = SYSDATE
				WHERE TRN_EQP_CD = :V_TRN_EQP_CD 
				 */

				szMsg = " 차량위치정보 삭제(상하차개시/완료/도착 위치)정리  << " + methodNm;
				slabUtils.printLog(logId, szMsg, "SL");
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updCarUseGpByTrnEqpCd";
				commDao.update(jrParam, queryId, logId, methodNm, "차량위치정보 삭제(상하차개시/완료/도착 위치)정리 ");
				
				
				//설비코드로 초기화 하는 경우(구내운송)			 			
				//ejb.transaction type="RequiresNew"
				jrParam.setField("STAT", "C"); //적치형 활성상태
				jrParam.setField("TRN_EQP_CD", szTrnEqpCd); //운송장비코드
				
				szMsg = " 차량포인트통합관리  << " + methodNm;
				slabUtils.printLog(logId, szMsg, "SL");
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointtrneqpcdupdate
				UPDATE TB_YD_CARPOINT
				   SET TRN_EQP_CD=null
				     , YD_STK_COL_ACT_STAT=DECODE(CARD_NO,NULL,(DECODE(YD_STK_COL_ACT_STAT,'N','N',:V_STAT)),YD_STK_COL_ACT_STAT)
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointin'
				 WHERE TRN_EQP_CD=:V_TRN_EQP_CD
				   AND MOD_DDTT<>sysdate
				 */
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.carpointtrneqpcdupdate";
				commDao.update(jrParam, queryId, logId, methodNm, "수정 ");
				
				if ("Y".equals(szBackupYn)) {
					//**********************************************************************************	
					//5. 작업예약, 작업예약재료 삭제 
					jrParam.setField("TRN_EQP_CD"	, szTrnEqpCd); //운송장비코드
					/*
					SELECT  C.*
					  FROM  TB_YD_CARSCH A
					       ,TB_YD_CARFTMVMTL B
					       ,TB_YD_WRKBOOK C 
					       ,TB_YD_WRKBOOKMTL D
					 WHERE  A.TRN_EQP_CD = :V_TRN_EQP_CD
					   AND  A.YD_CAR_SCH_ID=B.YD_CAR_SCH_ID
					   AND  A.DEL_YN = 'N'
					   AND  B.STL_NO = D.STL_NO
					   AND  D.DEL_YN = 'N'
					   AND  D.YD_WBOOK_ID = C.YD_WBOOK_ID
					   AND  C.DEL_YN = 'N'
					   AND  C.YD_SCH_CD LIKE SUBSTR(C.YD_SCH_CD,1,2)||'PT0_LM%' --하차스케줄
					UNION
					SELECT  C.*
					  FROM  TB_YD_CARSCH A
					       ,TB_YD_STOCK B
					       ,TB_YD_WRKBOOK C 
					       ,TB_YD_WRKBOOKMTL D
					 WHERE  A.TRN_EQP_CD = :V_TRN_EQP_CD
					   AND  A.FRTOMOVE_WORD_NO=B.CAR_FRTOMOVE_WORD_NO
					   AND  A.DEL_YN = 'N'
					   AND  B.STL_NO = D.STL_NO
					   AND  D.DEL_YN = 'N'
					   AND  D.YD_WBOOK_ID = C.YD_WBOOK_ID
					   AND  C.DEL_YN = 'N'
					   AND  C.YD_SCH_CD LIKE SUBSTR(C.YD_SCH_CD,1,2)||'PT0_UM%' --상차스케줄
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getWrkBookByTrnEqpCd";
					rsResult = commDao.select(jrParam, queryId, logId, methodNm, "운송장비코드로 작업예약 유무 체크");
					if (rsResult.size() > 0) {
						szMsg = " 작업예약이 존재함으로 작업예약과 작업예약재료 를 삭제(DEL_YN='Y')처리  << " + methodNm;
						slabUtils.printLog(logId, szMsg, "SL");
						
						//작업예약(TB_YD_WRKBOOK) 삭제처리 (DEL_YN='Y')
						jrParam.setField("TRN_EQP_CD", szTrnEqpCd); //운송장비코드
						jrParam.setField("DEL_YN"	 , "Y"); //삭제유무 Y:삭제
						
						/*
						 * #com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updDelYnWrkBookMtlByTrnEqpCd2
						 * 
						UPDATE TB_YD_WRKBOOKMTL
						   SET DEL_YN = :V_DEL_YN
						      ,MODIFIER = :V_MODIFIER
						      ,MOD_DDTT = SYSDATE
						 WHERE YD_WBOOK_ID IN (
						                        SELECT  DISTINCT C.YD_WBOOK_ID --하차대상
						                          FROM  TB_YD_CARSCH A
						                               ,TB_YD_CARFTMVMTL B
						                               ,TB_YD_WRKBOOK C 
						                               ,TB_YD_WRKBOOKMTL D
						                         WHERE  A.TRN_EQP_CD = :V_TRN_EQP_CD
						                           AND  A.YD_CAR_SCH_ID=B.YD_CAR_SCH_ID
						                           AND  A.DEL_YN = 'N'
						                           AND  B.STL_NO = D.STL_NO
						                           AND  D.DEL_YN = 'N'
						                           AND  D.YD_WBOOK_ID = C.YD_WBOOK_ID
						                           --AND  C.DEL_YN = 'N' 
						                           AND  C.YD_SCH_CD LIKE SUBSTR(C.YD_SCH_CD,1,2)||'PT0_LM%' --하차스케줄
						                        UNION   
						                        SELECT  DISTINCT C.YD_WBOOK_ID --상차대상
						                          FROM  TB_YD_CARSCH A
						                               ,TB_YD_STOCK B
						                               ,TB_YD_WRKBOOK C 
						                               ,TB_YD_WRKBOOKMTL D
						                         WHERE  A.TRN_EQP_CD = :V_TRN_EQP_CD
						                           AND  A.FRTOMOVE_WORD_NO=B.CAR_FRTOMOVE_WORD_NO
						                           AND  A.DEL_YN = 'N'
						                           AND  B.STL_NO = D.STL_NO
						                           AND  D.DEL_YN = 'N'
						                           AND  D.YD_WBOOK_ID = C.YD_WBOOK_ID
						                           --AND  C.DEL_YN = 'N'
						                           AND  C.YD_SCH_CD LIKE SUBSTR(C.YD_SCH_CD,1,2)||'PT0_UM%' --상차스케줄
						                      )
					*/
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updDelYnWrkBookMtlByTrnEqpCd2";	
					commDao.update(jrParam, queryId, logId, methodNm, "작업예약재료 삭제(DEL_YN='Y')처리 ");
					}
				}
				
				//**********************************************************************************	
				//6. 차량스케줄, 차량이송재료 삭제 
				
				//차량이송재료(TB_YD_CARFTMVMTL) 삭제처리 (DEL_YN='Y')
				jrParam.setField("TRN_EQP_CD", szTrnEqpCd); //운송장비코드
				jrParam.setField("DEL_YN"	 , "Y"); //삭제유무 Y:삭제
				/*
					UPDATE TB_YD_CARFTMVMTL
					   SET DEL_YN 	= :V_DEL_YN
					      ,MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					 WHERE YD_CAR_SCH_ID IN (
					                         SELECT  DISTINCT YD_CAR_SCH_ID
					                           FROM  TB_YD_CARSCH
					                          WHERE  TRN_EQP_CD = :V_TRN_EQP_CD
					                            AND  DEL_YN = 'N'
					                        )                        
				 */
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updDelYnCarFtMvMtlByTrnEqpCd";
				commDao.update(jrParam, queryId, logId, methodNm, "변경1");
				
				//차량스케줄(TB_YD_CARSCH) 삭제처리 (DEL_YN='Y')
				jrParam.setField("TRN_EQP_CD", szTrnEqpCd); //운송장비코드
				jrParam.setField("DEL_YN"	 , "Y"); //삭제유무 Y:삭제
				/*
					UPDATE TB_YD_CARSCH
					   SET DEL_YN = :V_DEL_YN
					      ,MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					WHERE YD_CAR_SCH_ID IN (
					                         SELECT  YD_CAR_SCH_ID
					                           FROM  TB_YD_CARSCH
					                          WHERE  TRN_EQP_CD = :V_TRN_EQP_CD
					                            AND  DEL_YN = 'N'
					                        )
				 */
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updDelYnCarSchByTrnEqpCd";
				commDao.update(jrParam, queryId, logId, methodNm, "변경2 ");
			}

			slabUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of initCarSch()
	
	
	/**
	 * 오퍼레이션명 : 차량포인트 통합관리 								  
	 * 야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
 	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */     	
	public boolean YdCarPointinforeg( String chk 
									, String sCarNo
									, String sTrnEqpCd
									, String ydStkColGp
									, String szArrWlocCd 
									, String szArrYdPntCd
									, String sStat
									, String logId
									, String mthdNm
									)throws DAOException {
		String methodNm =  "[PSlabYdMvCarSeEJB.YdCarPointinforeg] < " ; //+ gdReq.getNavigateValue()
		String szMsg ="";
		boolean isSuccess = false;
		
		try{
			szMsg = "▨▨▨차량포인트 통합관리(START):통합작업 시작 ▨▨▨" + chk;
			slabUtils.printLog(logId, szMsg, "S+");

			szMsg = "▨▨▨(IN):"+chk+","+sCarNo+","+sTrnEqpCd+","+ydStkColGp+","+szArrWlocCd+","+szArrYdPntCd+","+sStat+"▨▨▨" ;
			slabUtils.printLog(logId, methodNm, "SL");
			int iSeq=0;
			String stkQueryId ="";
			
			JDTORecord recPara    = slabUtils.getParam(logId, methodNm, chk);
			 recPara.setField("STAT"			,sStat);
			 recPara.setField("TRN_EQP_CD"		,sTrnEqpCd);
			 recPara.setField("YD_STK_COL_GP"	,ydStkColGp);
			 recPara.setField("WLOC_CD"			,szArrWlocCd);
			 recPara.setField("YD_PNT_CD"		,szArrYdPntCd);
			 recPara.setField("CAR_NO"			,sCarNo);
			 
			/*구분 (chk) 
			 * = 1:설비코드로 초기화 하는 경우(구내운송)  , 		2:저장위치로 초기화 하는 경우(구내운송)
			 *   3:저장위치로 차량 포인트 예약 하는 경우(구내운송),  	4:개소코드,포인트로 차량 포인트 예약 하는 경우(구내운송), 
			 *   A:설비코드로 초기화 하는 경우(출하)	, 			B:저장위치로 초기화 하는 경우(출하)
			 *   C:저장위치로 차량 포인트 예약 하는 경우(출하), 		D:개소코드,포인트로 차량 포인트 예약 하는 경우(출하)
			 */
			
			if ("1".equals(chk)) {
				//설비코드로 초기화 하는 경우(구내운송)			 			
				/* 
				UPDATE TB_YD_CARPOINT
				   SET TRN_EQP_CD=null
				     , YD_STK_COL_ACT_STAT=DECODE(CARD_NO,NULL,(DECODE(YD_STK_COL_ACT_STAT,'N','N',:V_STAT)),YD_STK_COL_ACT_STAT)  --야드적치열활성상태
				 --  , YD_STK_COL_ACT_STAT=DECODE(CARD_NO,NULL,:V_STAT,YD_STK_COL_ACT_STAT)
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointin'
				 WHERE TRN_EQP_CD=:V_TRN_EQP_CD
				   AND DEL_YN = 'N'
				   AND MOD_DDTT<>sysdate
				*/
				stkQueryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.carpointtrneqpcdupdate";
				iSeq = commDao.update(recPara, stkQueryId, logId, methodNm, "수정1");
				
			}else if ("2".equals(chk)) {
				//저장위치로 초기화 하는 경우(구내운송)
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointstackcolgpupdateCT
				UPDATE USRYSA.TB_YD_CARPOINT
				   SET TRN_EQP_CD=null
				     , YD_STK_COL_ACT_STAT=DECODE(CARD_NO,NULL,:V_STAT,YD_STK_COL_ACT_STAT)
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointCT'
				 WHERE YD_STK_COL_GP=:V_YD_STK_COL_GP 
				   AND DEL_YN = 'N'
				*/ 
				recPara.setField("YD_STK_COL_GP",	sStat);

				stkQueryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.carpointstackcolgpupdateCT";
				iSeq = commDao.update(recPara, stkQueryId, logId, methodNm, "수정2");


			}else if ("3".equals(chk)) {
				//저장위치로 차량 포인트 예약 하는 경우(구내운송)
				 /* 
				UPDATE USRYSA.TB_YD_CARPOINT
				   SET YD_STK_COL_ACT_STAT=:V_STAT
				     , TRN_EQP_CD =:V_TRN_EQP_CD
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointCP'
				 WHERE YD_STK_COL_GP=:V_YD_STK_COL_GP			--야드적치열구분
				*/ 
				stkQueryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.carpointtrneqpcdupdateC";
				iSeq = commDao.update(recPara, stkQueryId, logId, methodNm, "수정");

			} else if ("4".equals(chk)) {
				//개소코드,포인트로 차량 포인트 예약 하는 경우(구내운송)
				 /* 
				UPDATE TB_YD_CARPOINT
				   SET YD_STK_COL_ACT_STAT  =:V_STAT
				     , TRN_EQP_CD           =:V_TRN_EQP_CD
				     , MOD_DDTT             =sysdate
				     , MODIFIER             ='CarPointin'
				 WHERE WLOC_CD              =:V_WLOC_CD			--개소코드
				   AND YD_PNT_CD            =:V_YD_PNT_CD		--야드차량포인트코드(*)
				*/   
				stkQueryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.carpointWlocpntupdate";
				iSeq = commDao.update(recPara, stkQueryId, logId, methodNm, "수정");

			} else if ("A".equals(chk)) {
				//설비코드로 초기화 하는 경우(출하)			 		
				/* 
				UPDATE USRYSA.TB_YD_CARPOINT
				  SET   CARD_NO=NULL
				    ,   CAR_NO=NULL
				    ,   YD_STK_COL_ACT_STAT=:V_STAT
				    ,   MOD_DDTT=sysdate
				    ,   MODIFIER='CarPointPT'
				 WHERE CARD_NO=:V_TRN_EQP_CD
				   AND CAR_NO  =:V_CAR_NO   
				*/   
				stkQueryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.carpointtrneqpcdupdatePT";
				iSeq = commDao.update(recPara, stkQueryId, logId, methodNm, "수정");

			}else if ("B".equals(chk)) {
				//저장위치로 초기화 하는 경우(출하)
				/* 
				UPDATE USRYSA.TB_YD_CARPOINT
				   SET CARD_NO=null
				     , CAR_NO=NULL
				     , YD_STK_COL_ACT_STAT=DECODE(TRN_EQP_CD,NULL,:V_STAT,YD_STK_COL_ACT_STAT)
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointC'
				 WHERE YD_STK_COL_GP=:V_YS_STK_COL_GP 
				*/ 
				stkQueryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.carpointstackcolgpupdateC";
				iSeq = commDao.update(recPara, stkQueryId, logId, methodNm, "수정");


			}else if ("C".equals(chk)) {
				//저장위치로 차량 포인트 예약 하는 경우(출하)
				/* 
	    		UPDATE USRYSA.TB_YD_CARPOINT
	    		   SET YD_STK_COL_ACT_STAT=:V_STAT
	    		     , CAR_NO  =:V_CAR_NO
	    		     , CARD_NO =:V_TRN_EQP_CD
	    		     , MOD_DDTT=sysdate
	    		     , MODIFIER='CarPointC'
	    		 WHERE YD_STK_COL_GP=:V_YD_STK_COL_GP
	    		*/ 
				stkQueryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.carpointtrneqpcdupdateC2";
				iSeq = commDao.update(recPara, stkQueryId, logId, methodNm, "수정");

			} else if ("D".equals(chk)) {
				 /* 
				UPDATE USRYSA.TB_YD_CARPOINT
				   SET YD_STK_COL_ACT_STAT=:V_STAT
				     , CAR_NO  =:V_CAR_NO
				     , CARD_NO = :V_TRN_EQP_CD
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointPT'
				 WHERE WLOC_CD=:V_WLOC_CD
				   AND YD_PNT_CD=:V_YD_PNT_CD
				 */  
				
				//개소코드,포인트로 차량 포인트 예약 하는 경우(출하)
				stkQueryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.carpointWlocpntupdatePT";
				iSeq = commDao.update(recPara, stkQueryId, logId, methodNm, "수정");
			}  
	    	szMsg =  "▨▨▨차량포인트 통합관리(END)COUNT:"+iSeq+"▨▨▨";
			isSuccess = true;
			slabUtils.printLog(logId, methodNm, "S-");	
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	    return isSuccess;
	}  

	
	/**
	 * 차량작업관리 상차LOT편성  (3-3.차량작업상세내역)         --수동[차량 도착후 2Bed상차Lot편성 ] 처리 반영--
	 * 야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
 	 * 	@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *  @param JDTORecord inDto
	 *  @return JDTORecord
	 *  @throws DAOException
	 */
	public JDTORecord insCarLdLot(JDTORecord [] inDto ) throws DAOException {
		/*
		 * 업무기준 : 1. 차량스케줄ID로 차량스케줄이 존재하는 조회
		 * 				1-1. 존재하지 않으면 오류처리
		 * 				1-2. 존재하면 기준적용/작업자지정인 지를 판단
		 * 					1-2-1. 작업자지정이면
		 * 						1-2-1-1. 스케줄기준 체크
		 * 						1-2-1-2. 기존작업예약이 존재하지 않으면 작업예약 등록
		 * 						1-2-1-3. 작업예약 재료 등록
		 * 						1-2-1-4. 통합야드인 경우의 준비스케줄을 이용하여 작업예약 등록인 경우에는 
		 * 								해당 준비스케줄의 작업예약ID와 DEL_YN 항목에 Y를 설정
		 * 					1-2-2. 기준적용이면
		 * 						1-2-2-1. 구내운송은 차량스펙을 조회하여 야드작업허용중량을 사용
		 * 						1-2-2-2. 이송대상재를 조회
		 * 						1-2-2-3. 스케줄기준 체크
		 * 						1-2-2-4. 작업예약 등록
		 * 						1-2-2-5. 작업예약재료 등록
		 * 					1-2-3. 상차출발인 경우 예약된 차량정지point를 삭제하고 차량정지point지시요구 모듈을 호출
		 */

	    String szMsg = "차량작업관리 화면 상차LOT편성[insCarLdLot] 시작";
	    String methodNm 				= "insCarLdLot";
	    String logId = "";
		
		try {
			logId 						= (String)inDto[0].getField("LOGID");
			String sModifier 			= (String)inDto[0].getField("USERID");
			slabUtils.printLog(logId,  szMsg, "S+");
			
			int    intRtnVal    		= 0;
			String szYdCarSchId 		= "";
			String szYdSchCd 			= "";
			String ydSchPrior 			= "";
			String szYdCarLotType 		= "";
			String szYdAimRtGp 			= "";
			
			String szYdWbookId 			= "";
			String szChkYdPrepSchId 	= "";
			String szTrnWrkFullvoidGp 	= "E"; 				//상차LOT편성(DxPT0xUM출고)
			String ydStkBedWtMax		= "";
			String ydStkColGp           = "";
			
			JDTORecord      recPara     = slabUtils.getParam(logId, methodNm, sModifier);
			JDTORecord 		jrRtn  		= slabUtils.getParam(logId, methodNm, sModifier);
			JDTORecord 		jrParam  	= slabUtils.getParam(logId, methodNm, sModifier);
			
			
			JDTORecordSet 	rsResult    = JDTORecordFactory.getInstance().createRecordSet("stock");

			//발지개소코드
			String szWlocCd   		= slabUtils.paraRecChkNull(inDto[0], "WLOC_CD3");			//DKY21: DWY22 
			String szCarPoint 		= slabUtils.paraRecChkNull(inDto[0], "CAR_POINT3");			// 
			String szTrnEqpCd       = slabUtils.paraRecChkNull(inDto[0], "TRN_EQP_CD"); 
			
			String ydPrepSchId1 	= slabUtils.paraRecChkNull(inDto[0], "YD_PREP_SCH_ID1");
			String ydPrepSchId2  	= slabUtils.paraRecChkNull(inDto[0], "YD_PREP_SCH_ID2");
			
			slabUtils.printLog(logId, "************발지개소코드:"+ szWlocCd , "SL");
			slabUtils.printLog(logId, "************차량Point:"+ szCarPoint, "SL");
			slabUtils.printLog(logId, "*************Lot편성 :"+ szCarPoint, "SL");
			
			szMsg = "차량작업관리 화면 상차LOT편성 - 발지개소코드["+szWlocCd+"]";
			slabUtils.printLog(logId, szMsg, "SL");
			if("".equals(szWlocCd)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "(필수)발지개소코드 오류 입니다.");
				return jrRtn;
			}
			String sDyd201Yn25 		= ""; // (차량)구내운송상차차량적재MAX용량  
			/* com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYnDualSql
			SELECT 'Y' AS APPLY_YN25         FROM DUAL
			*/  
			JDTORecordSet jsApplyYNChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYnDualSql", logId, methodNm, "열정보 Read"); 
			if (jsApplyYNChk.size() > 0) {
				sDyd201Yn25    = slabUtils.trim(jsApplyYNChk.getRecord(0).getFieldString("APPLY_YN25"));
			}

			//+++++++++++++++ 1. 차량스케줄을 먼저 조회해서 존재하는 지를 확인 ++++++++++++++++++++++
			szYdAimRtGp		= slabUtils.paraRecChkNull(inDto[0], "YD_AIM_RT_GP");
			slabUtils.printLog(logId, "szYdAimRtGp:"+ szYdAimRtGp, "SL"); 

			szYdCarSchId 	= slabUtils.nvl(inDto[0].getField("YD_CAR_SCH_ID"), "");	//tab2에서 받은 사항 
			szMsg = " 차량작업관리 화면 상차LOT편성 : 차량스케줄ID["+szYdCarSchId+"]로 조회 전";
			slabUtils.printLog(logId, szMsg, "SL");
			
			recPara.setField("YD_CAR_SCH_ID", szYdCarSchId);
		
			/* #com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdCarsch01
			 *  
				SELECT 
				    YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
				    ,REGISTER AS REGISTER
				    ,TO_CHAR(REG_DDTT,'YYYYMMDDHH24MISS') AS REG_DDTT
				    ,MODIFIER AS MODIFIER
				    ,TO_CHAR(MOD_DDTT,'YYYYMMDDHH24MISS') AS MOD_DDTT
				    ,DEL_YN AS DEL_YN
				    ,YD_EQP_ID AS YD_EQP_ID
				    ,YD_CAR_USE_GP AS YD_CAR_USE_GP
				    ,CAR_NO AS CAR_NO
				    ,TRN_EQP_CD AS TRN_EQP_CD
				    ,CAR_KIND AS CAR_KIND
				    ,TRANS_EQUIPMENT_TYPE AS TRANS_EQUIPMENT_TYPE 
				    ,YD_EQP_WRK_STAT  AS YD_EQP_WRK_STAT
				    ,YD_WRK_PROG_STAT  AS YD_WRK_PROG_STAT
				    ,NVL(YD_EQP_WRK_SH,'0')  AS YD_EQP_WRK_SH
				    ,YD_EQP_WRK_WT  AS YD_EQP_WRK_WT
				    ,YD_STK_BED_TP  AS YD_STK_BED_TP
				    ,SPOS_WLOC_CD  AS SPOS_WLOC_CD
				       ,(CASE WHEN ARR_WLOC_CD IS NULL THEN (SELECT 
				                                                    (SELECT / *+ INDEX(D PK_PT_STLFRTOMOVE)* / 
				                                                            D.ARR_WLOC_CD
				                                                        FROM TB_PT_STLFRTOMOVE D
				                                                       WHERE D.TRANSWORD_SEQNO=(SELECT/ *+ INDEX_DESC(D PK_PT_STLFRTOMOVE)* /
				                                                                            MAX(TRANSWORD_SEQNO) 
				                                                                         FROM TB_PT_STLFRTOMOVE K
				                                                                         WHERE D.STL_NO=K.STL_NO
				                                                                          AND ROWNUM<=1)
				                                                         AND B.STL_NO =D.STL_NO
				                                                         ) AS ARR_WLOC_CD
				                                             FROM  TB_YD_STKLYR B                                              
				                                              WHERE C.YD_CARLD_STOP_LOC=B.YD_STK_COL_GP     
				                                                AND ROWNUM<=1 ) 
				          ELSE ARR_WLOC_CD END) AS ARR_WLOC_CD
				    ,YD_CARLD_LEV_LOC  AS YD_CARLD_LEV_LOC
				    ,TO_CHAR(YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_LEV_DT
				    ,TO_CHAR(YD_CARLD_PNT_WO_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_PNT_WO_DT
				    ,YD_PNT_CD1 AS YD_PNT_CD1
				    ,YD_PNT_CD2 AS YD_PNT_CD2
				    ,YD_CARLD_WRK_BOOK_ID  AS YD_CARLD_WRK_BOOK_ID
				    ,YD_CARLD_SCH_REQ_GP  AS YD_CARLD_SCH_REQ_GP
				    ,YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
				    ,TO_CHAR(YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_ARR_DT
				    ,TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_ST_DT
				    ,TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_CMPL_DT
				    ,YD_CARLD_WRK_ACT_GP  AS YD_CARLD_WRK_ACT_GP
				    ,TO_CHAR(YD_CARLD_CHK_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_CHK_DT
				    ,TO_CHAR(YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_LEV_DT
				    ,TO_CHAR(YD_CARUD_PNT_WO_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_PNT_WO_DT
				    ,YD_PNT_CD3 AS YD_PNT_CD3
				    ,YD_PNT_CD4 AS YD_PNT_CD4
				    ,YD_CARUD_WRK_BOOK_ID  AS YD_CARUD_WRK_BOOK_ID
				    ,YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
				    ,YD_CARUD_SCH_REQ_GP  AS YD_CARUD_SCH_REQ_GP
				    ,TO_CHAR(YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_ARR_DT
				    ,TO_CHAR(YD_CARUD_CHK_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_CHK_DT
				    ,TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_ST_DT
				    ,TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_CMPL_DT
				    ,YD_CARUD_WRK_ACT_GP  AS YD_CARUD_WRK_ACT_GP
				    ,YD_TRN_WRK_DELY_CD  AS YD_TRN_WRK_DELY_CD
				    ,CARD_NO  AS CARD_NO
				    ,YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT
				    ,FRTOMOVE_PLANT_GP AS FRTOMOVE_PLANT_GP
				    ,PROC_TO AS PROC_TO
				    ,RENTPROC_CD AS RENTPROC_CD
				    ,YD_FRTOMOVE_YD_GP AS YD_FRTOMOVE_YD_GP
				    ,YD_FRTOMOVE_BAY_GP AS YD_FRTOMOVE_BAY_GP
				    ,URGENT_FRTOMOVE_WORD_GP AS URGENT_FRTOMOVE_WORD_GP
				    ,DEST_TEL_NO AS DEST_TEL_NO
				    ,YD_DLVRDD_RULE_DD AS YD_DLVRDD_RULE_DD
				    ,SHIPASSIGN_WORD_DATE AS SHIPASSIGN_WORD_DATE
				    ,SHIPASSIGN_WORD_SEQNO AS SHIPASSIGN_WORD_SEQNO
				    ,SHIP_CD AS SHIP_CD
				    ,SHIP_NAME AS SHIP_NAME
				    ,RSHP_HOLD_NO AS RSHP_HOLD_NO
				    ,BERTH_NO AS BERTH_NO
				    ,SAILNO AS SAILNO
				    ,YD_CAR_WRK_GP AS YD_CAR_WRK_GP
				    ,TRANS_ORD_DATE AS TRANS_ORD_DATE
				    ,TRANS_ORD_SEQNO AS TRANS_ORD_SEQNO
				    ,YD_BAYIN_WO_SEQ
				    ,YD_CAR_RCPT_CHK_YN
				    ,YD_CAR_ISSUE_CHK_YN
				    ,YD_CAR_RCPT_CHECKER
				    ,YD_CAR_ISSUE_CHECKER    
				    ,SUBSTR(YD_CARLD_STOP_LOC,1,1) AS YD_GP
				    ,(SELECT COUNT(*)
				     FROM TB_YD_CRNSCH
				     WHERE YD_WBOOK_ID = YD_CARLD_WRK_BOOK_ID
				     AND DEL_YN = 'N') YD_CRN_SCH_ID
				    ,(SELECT CASE WHEN YD_SCH_CD LIKE 'D_YD__MM' THEN 'Y'
				                  ELSE 'N' END AS M_WRK_GP
				        FROM TB_YD_WRKBOOK
				       WHERE YD_WBOOK_ID = (
				    SELECT CS.YD_CARLD_WRK_BOOK_ID
				    FROM TB_YD_CARSCH CS
				    WHERE 1=1 
				      AND CS.YD_CAR_SCH_ID = C.YD_CAR_SCH_ID
				      AND DEL_YN = 'N') 
				    ) AS M_WRK_GP
				    ,(SELECT CP.YD_FRM_YN FROM TB_YD_CARPOINT CP WHERE  CP.YD_STK_COL_GP = YD_CARLD_STOP_LOC ) AS YD_FRM_YN
				FROM TB_YD_CARSCH C
				WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			 */
			JDTORecordSet outRecSet = commDao.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdCarsch01", logId, methodNm, "조회");
			intRtnVal = outRecSet.size();
			if( intRtnVal == 0 ) {
				szMsg = "차량작업관리> 상차LOT편성 : 차량스케줄ID["+szYdCarSchId+"]이 존재하지 않습니다.";
				slabUtils.printLog(logId, szMsg, "SL");

				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg);
				return jrRtn; //RETN_CD_NOTEXIST;
			}else if( intRtnVal < 0 ) {
				szMsg = "차량작업관리> 상차LOT편성 : 차량스케줄ID["+szYdCarSchId+"]로 조회시 오류발생 : 반환값  - " + intRtnVal;
				slabUtils.printLog(logId, szMsg, "SL");

				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg);
				return jrRtn ;  
			}
			outRecSet.first();
			recPara = outRecSet.getRecord();
			
			//String szYdCarProgStat= slabUtils.nvl(recPara.getField("YD_CAR_PROG_STAT"), "");		//야드차량진행상태
			//String szCarNo 		= slabUtils.nvl(recPara.getField("CAR_NO"), "");		
			String szYdCarUseGp 	= slabUtils.nvl(recPara.getField("YD_CAR_USE_GP"), "");			//야드차량사용구분
			szTrnEqpCd 		        = slabUtils.nvl(recPara.getField("TRN_EQP_CD"), "");			//운송장비코드
			//String szArrYdPntCd 	= slabUtils.nvl(recPara.getField("YD_PNT_CD1"), "");			//야드포인트코드1
			String szArrWlocCd      = slabUtils.nvl(recPara.getField("ARR_WLOC_CD"), "");
			String szSposWlocCd     = slabUtils.nvl(recPara.getField("SPOS_WLOC_CD"), "");			//DKY21
			String ydFrmYn          = slabUtils.nvl(recPara.getField("YD_FRM_YN"), "");
			slabUtils.printLog(logId, "#szCarPoint"+szCarPoint+ " szSposWlocCd" + szSposWlocCd, "SL"); //DAPT01 
			
			slabUtils.printLog(logId, "CASE1. C연주슬라브야드(발지):" +szArrWlocCd +" (착지)" + szSposWlocCd, "SL");
			//
			szTrnWrkFullvoidGp = "U"; //출고=상차
			if("DBPT01".equals(szCarPoint) || "DBPT02".equals(szCarPoint) || "DAPT01".equals(szCarPoint) || "DAPT02".equals(szCarPoint) ){ 
				szYdSchCd = "D" + szCarPoint.substring(1, 2) + "PT0" + szCarPoint.substring(5, 6) + szTrnWrkFullvoidGp+"M"; 
			} else {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "처리불가입니다. 차량Point확인["+ szCarPoint +"]");
				return jrRtn;
			}
			slabUtils.printLog(logId, "변경 스케줄 코드는 :" + szYdSchCd, "SL");

			/*
				SELECT YD_STK_BED_WT_MAX 
				  FROM TB_YD_STKBED 
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP 
				   AND YD_STK_BED_NO = '01'  
			 */
			recPara.setField("YD_STK_COL_GP"	, szCarPoint);
			String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getYdStkbedStkBedWtMax";
			rsResult		= commDao.select(recPara, queryId, logId, szMsg, "구내운송 진입차량의 적재 능력");
			if (rsResult.size() > 0) {
				ydStkBedWtMax    = slabUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_BED_WT_MAX"));
			}

			recPara.setField("YD_WBOOK_ID"	, szYdWbookId);
			recPara.setField("YD_GP"		, szYdSchCd.substring(0,1));
			recPara.setField("YD_SCH_CD"	, szYdSchCd);
			recPara.setField("YD_BAY_GP"	, szYdSchCd.substring(1, 2));
			
	    	/////////////////////////////////////////////////////////////////////////////////// 출고 : U , 입고 : L , (이적 : M) 
	    	szMsg = "차량작업관리 화면 상차LOT편성 : 차량스케줄ID["+szYdCarSchId+"], 스케줄코드["+szYdSchCd+"], 운송장비코드["+szTrnEqpCd+"],  차량사용구분["+szYdCarUseGp+"]";
	    	slabUtils.printLog(logId, szMsg, "SL");

	    	szYdWbookId 	=  slabUtils.nvl(inDto[0].getField("YD_WBOOK_ID"), "");
	    	if(!"".equals(szYdWbookId)) {
	    		szMsg = "작업예약ID["+szYdWbookId+"] 가 없는 경우";
	    		slabUtils.printLog(logId, szMsg, "SL");
	    	}
	    	
	    	szChkYdPrepSchId	= slabUtils.paraRecChkNull(inDto[0], "PRI_YD_PREP_SCH_ID");
	    	szYdCarLotType    	= slabUtils.paraRecChkNull(inDto[0], "YD_CAR_LOT_TYPE");   	// M:작업자지정  , S:기준적용  
	    	
	    	if( !"".equals(szYdSchCd)) {
	    		/*
					SELECT A.*
					     , CASE WHEN WRK_EQUIP_STAT != 'B' THEN YD_WRK_CRN_PRIOR
					            WHEN WRK_EQUIP_STAT  = 'B' AND YD_ALT_CRN_YN = 'Y' THEN CASE WHEN ALT_EQUIP_STAT != 'B' THEN YD_ALT_CRN_PRIOR ELSE 99 END
					            ELSE 99
					        END AS YD_SCH_PRIOR
					     , CASE WHEN WRK_EQUIP_STAT != 'B' THEN YD_WRK_CRN
					            WHEN WRK_EQUIP_STAT  = 'B' AND YD_ALT_CRN_YN = 'Y' THEN CASE WHEN ALT_EQUIP_STAT != 'B' THEN YD_ALT_CRN ELSE '99' END
					            ELSE '99'
					        END AS YD_SCH_CRN
					  FROM (
					        SELECT YD_SCH_CD
					             , YD_GP
					             , YD_BAY_GP
					             , YD_SCH_RNG_CD
					             , YD_SCH_WHIO_GP
					             , YD_SCH_DIV_GP
					             , YD_SCH_RULE_ACT_STAT
					             , YD_WRK_CRN
					             , YD_WRK_CRN_PRIOR
					             , YD_ALT_CRN_YN
					             , YD_ALT_CRN
					             , YD_ALT_CRN_PRIOR
					             , CD_CONTENTS       
					             , YD_SCH_PROH_EXN   
					             , (SELECT YD_EQP_STAT FROM TB_YD_EQP WHERE YD_EQP_ID = SR.YD_WRK_CRN) AS WRK_EQUIP_STAT
					             , (SELECT YD_EQP_STAT FROM TB_YD_EQP WHERE YD_EQP_ID = SR.YD_ALT_CRN) AS ALT_EQUIP_STAT
					          FROM TB_YD_SCHRULE   SR
					         WHERE YD_SCH_CD = :V_YD_SCH_CD
					       ) A
					 WHERE 1 = 1
	    		 */
	    		recPara.setField("YD_SCH_CD"	, szYdSchCd);
	    		queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getYdSchrule";
	    		JDTORecordSet jsSchrule = commDao.select(recPara, queryId, logId, methodNm, "스케줄 기준 조회");
	    		//                                             .
	    		
	    		if (jsSchrule.size() > 0) {
	    			ydSchPrior    = slabUtils.trim(jsSchrule.getRecord(0).getFieldString("YD_SCH_PRIOR"));		//--(작업가능)야드작업크레인우선순위
	    			//ydWrkPlanCrn  = slabUtils.trim(jsSchrule.getRecord(0).getFieldString("YD_SCH_CRN"));
	    			//szYdSchPrior  = slabUtils.nvl(jsSchrule.getField("YD_SCH_PRIOR"), "");
	    			
	    			if ("99".equals(ydSchPrior)) {
	    				ydSchPrior = "";
	    				szMsg = "##해당 스케줄("+ szYdSchCd +")에 적합한 작업가능한 크레인이 없습니다.";
	    				slabUtils.printLog(logId, szMsg, "SL");
	    				// 작업가능 크레인이 없는 상태이므로, Lot 편성 및 차량화면에서  match하여야 함
	    				jrRtn.setField("LOT_YN"	, "N");
	    				jrRtn.setField("RTN_CD"	, "0");
	    				jrRtn.setField("RTN_MSG", szMsg);
	    				return jrRtn;
	    			}
	    		}
	    		slabUtils.printLog(logId, "YD_SCH_CD:"+ szYdSchCd+ " YD_SCH_PRIOR:" + ydSchPrior+"----스케줄기준 확인 끝", "SL");
	    	}
	    	szMsg = "차량작업관리 화면 상차LOT편성 - 작업자지정["+ szYdCarLotType+"] ----"+szChkYdPrepSchId+ "-----"+ ydStkBedWtMax;
	    	slabUtils.printLog(logId, szMsg, "SL");
	    	if(PSlabYdConstant.APPLY_YN.equals(sDyd201Yn25)) {
	    		if(Integer.parseInt(slabUtils.nvl(ydStkBedWtMax, "0")) > 0) {
	    			if( Integer.parseInt(slabUtils.nvl(ydStkBedWtMax, "0")) - 
	    					Integer.parseInt(slabUtils.nvl(inDto[0].getField("YD_MTL_WT_SUM"), "0")) < 0 )  {  //지정용량보다 Lot편성 용량이 크면 오류 처리  
	    				szMsg = "적재가능 중량"+ ydStkBedWtMax + "을 초과 하였음. ("+ slabUtils.nvl(inDto[0].getField("YD_MTL_WT_SUM"), "0")+ ")" ;
	    				jrRtn.setField("RTN_CD"	, "0");
	    				jrRtn.setField("RTN_MSG", szMsg);
	    				return jrRtn;
	    			}
	    		}
	    	}

	    	// ----반영 해야 하는 사항 : (자동)일때는 순서를 첫번째로 한정 하였음. 여기서는 화면에서 선택한 준비스케줄ID(동일상차포함)로 작업 예약을 생성---------  
	    	ydPrepSchId1 = slabUtils.paraRecChkNull(inDto[0], "YD_PREP_SCH_ID"); //처음것 
	    	
	    	for(int i = 0; i < inDto.length; i++ ) {
	    		if(!ydPrepSchId1.equals(  slabUtils.paraRecChkNull(inDto[i], "YD_PREP_SCH_ID") )) {
	    			ydPrepSchId2 =  slabUtils.paraRecChkNull(inDto[0], "YD_PREP_SCH_ID");
	    		}
	    	}
	    	jrParam.setField("WLOC_CD"			, szWlocCd);
	    	jrParam.setField("TRN_EQP_CD"		, szTrnEqpCd);
	    	//
	    	if(szYdSchCd.length() < 6) {
	    		slabUtils.printLog(logId, "스케줄코드 확인:" +szYdSchCd +"<<"+ ydStkColGp, "SL"); 
	    	}
	    	jrParam.setField("YD_STK_COL_GP"	, szYdSchCd.substring(0, 6));
	    	jrParam.setField("YD_PREP_SCH_ID1"	, ydPrepSchId1);
	    	jrParam.setField("YD_PREP_SCH_ID2"	, ydPrepSchId2);
	    	jrParam.setField("TRN_EQP_STK_CAPA"	, ""          );
	    	
	    	slabUtils.printLog(logId, szYdSchCd+ "> " +ydPrepSchId1+"=="+ydPrepSchId2, "SL");
	    	// ---------------((2021-07-28 등록 사항))--------------------------------------------------------------------------------
			//상차 작업 예약 자동 등록 처리 
			JDTORecord jrWbook 	= this.procYdWbookForCarLd(jrParam);
			String sLotYn		= slabUtils.nvl(jrWbook.getFieldString("LOT_YN"), "0");
			String rtnMsg	 	= slabUtils.nvl(jrWbook.getFieldString("RTN_MSG"), "");
			if("N".equals(sLotYn)) {
				slabUtils.printLog(logId, "상차 LOT 편성 자동match 오류:"+ rtnMsg, "SL");
			}
			String ydWbookId 	= slabUtils.nvl(jrWbook.getFieldString("YD_WBOOK_ID"), "");
			String ydWbookId2 	= slabUtils.nvl(jrWbook.getFieldString("YD_WBOOK_ID2"), "");
			String ydSchCd	  	= slabUtils.nvl(jrWbook.getFieldString("YD_SCH_CD"), "0");
			String ydEqpId 		= slabUtils.nvl(jrWbook.getFieldString("YD_EQP_ID"), "0");
			
			szMsg = "차량작업관리 화면> 상차LOT편성이 처리 되었습니다.";
			slabUtils.printLog(logId, szMsg, "SL");


			if ("Y".equals(ydFrmYn)) {  //형상 수신 여부  (구내운송 차량이 진입하고, LOT 편성을 하면서 형상까지 수신을 받은 경우는 스케줄까지 기동, 아니면 작업예약만 생성)  
				/* com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdRuleNvl2 
				SELECT DECODE(DTL_ITEM5 ,'Y', 'Y','N') AS FRM_YN 
				  FROM TB_YD_RULE 
			 	 WHERE REPR_CD_GP = 'DYD006'
				   AND ITEM = 'D' AND CD_GP = ::V_CD_GP
				*/
				jrParam.setField("CD_GP"		, szYdSchCd.substring(0, 6));
				JDTORecordSet jsFrmAcc =commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdRuleNvl2", logId, methodNm, "형상수신확인");
				if(jsFrmAcc.size() > 0) {
					if("Y".equals( jsFrmAcc.getRecord(0).getFieldString("FRM_YN"))) {
						ydFrmYn = "N";		//
					}
				}
			}

			if("N".equals(ydFrmYn) ) {		//상차는 1Bed를 먼저 상차 처리.
				
				JDTORecord jrCrnSchMsg1 = JDTORecordFactory.getInstance().create();
				slabUtils.printLog(logId,  "상차)크레인 스케줄 호출 : YDYDJ401-->" + ydWbookId , "SL");   
				
				if(!"".equals(ydWbookId) ) {
					
					//크레인 스케줄 기동  호출
					jrCrnSchMsg1.setField("JMS_TC_CD"			, "YDYDJ401"); 
					jrCrnSchMsg1.setField("JMS_TC_CREATE_DDTT"	, slabUtils.getDateTime14()); //JMSTC생성일시	
					jrCrnSchMsg1.setField("YD_WBOOK_ID"  		, ydWbookId); //작업예약ID
					jrCrnSchMsg1.setField("YD_SCH_CD"  			, ydSchCd);  //야드스케쥴코드
					jrCrnSchMsg1.setField("YD_EQP_ID"  			, ydEqpId);  //야드설비ID (예)DACRA1
					jrCrnSchMsg1.setField("EJB_CALL_YN"			, "Y");	//EJBCall여부(신 크레인스케줄)
					
					jrRtn = slabUtils.addSndData(jrRtn, jrCrnSchMsg1);
				}

				if(!"".equals(ydWbookId2) ) {
					slabUtils.printLog(logId,  "크레인 스케줄 호출(차상위치 다건:2Bed)-->" + ydWbookId2 , "SL");   
					jrCrnSchMsg1.setField("JMS_TC_CD"			, "YDYDJ401"); 
					jrCrnSchMsg1.setField("JMS_TC_CREATE_DDTT"	, slabUtils.getDateTime14()); //JMSTC생성일시	
					jrCrnSchMsg1.setField("YD_WBOOK_ID"  		, ydWbookId2); //작업예약ID
					jrCrnSchMsg1.setField("YD_SCH_CD"  			, ydSchCd);  //야드스케쥴코드
					jrCrnSchMsg1.setField("YD_EQP_ID"  			, ydEqpId);  //야드설비ID (예)DACRA1
					jrCrnSchMsg1.setField("EJB_CALL_YN"			, "Y");	//EJBCall여부(신 크레인스케줄)
					
					jrRtn = slabUtils.addSndData(jrRtn, jrCrnSchMsg1);
				}
			}
			
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", szMsg);
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}//end of insCarLdLot
	

	/**
	 *  크레인작업예약관리 - 작업예약 삭제												//--1차 수정분(2021-07-29)--
	 * 야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String delYdWrkbook(JDTORecord  inDto) throws DAOException {
	    String methodNm = "작업예약 삭제[PSlabYdMvCarSeEJB.delYdWrkbook] < " + inDto.getResultMsg();
		String logId    = inDto.getResultCode();
		String szMsg 	= "작업예약삭제"  ;

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			
			String		szRtnMsg				= "";
			String		szYdWbookId				= null;
			String		szYdEqpGp				= null;
			slabUtils.printLog(logId,  szMsg, "");

			String ydUserId      	= slabUtils.trim(inDto.getFieldString("MODIFIER"));
			JDTORecord recPara  	= slabUtils.getParam(logId, methodNm, ydUserId);
			//------------------------------------------------------------------------------------------------
			//	크레인스케줄이 존재하는 지 먼저 확인
			//------------------------------------------------------------------------------------------------
			szYdWbookId = slabUtils.paraRecChkNull(inDto, "YD_WBOOK_ID");
			szYdEqpGp   = slabUtils.paraRecChkNull(inDto, "YD_EQP_GP");

			recPara.setField("YD_WBOOK_ID", 		szYdWbookId);
			recPara.setField("YD_EQP_GP", 			szYdEqpGp	);

			String queryId 		= "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getYdCrnschByWrkbookIdDnWrLoc";
			JDTORecordSet rsResult	= commDao.select(recPara, queryId, logId, szMsg, "크레인스케줄이 존재하는 지 먼저 확인");
			/*
				SELECT YD_CRN_SCH_ID
				      ,REGISTER
				      ,REG_DDTT
				      ,MODIFIER
				      ,MOD_DDTT
				      ,DEL_YN
				      ,YD_WBOOK_ID
				      ,YD_EQP_ID
				      ,YD_GP
				      ,YD_BAY_GP
				      ,YD_SCH_CD
				      ,YD_SCH_ST_GP
				      ,YD_SCH_REQ_GP
				      ,YD_SCH_PRIOR
				      ,YD_EQP_WRK_STAT
				      ,YD_WRK_PROG_STAT
				      ,YD_WBOOK_DT
				      ,YD_SCH_DT
				      ,YD_WORD_DT
				      ,YD_UP_CMPL_DT
				      ,YD_DN_CMPL_DT
				      ,YD_WRK_HDS_DD
				      ,YD_WRK_DUTY
				      ,YD_WRK_PARTY
				      ,YD_MAIN_WRK_MTL_SH
				      ,YD_AID_WRK_MTL_SH
				      ,YD_AID_WRK_UPDN_GP
				      ,YD_TO_LOC_DCSN_MTD
				      ,YD_TO_LOC_GUIDE
				      ,YD_EQP_WRK_SH
				      ,YD_EQP_WRK_WT
				      ,YD_EQP_WRK_T
				      ,YD_EQP_WRK_MAX_W
				      ,YD_EQP_WRK_MAX_L
				      ,YD_CRN_SB_CTL_H
				      ,YD_CRN_GRAB_USE_RULE_ID
				      ,YD_UP_WO_LOC
				      ,YD_UP_WO_LAYER
				      ,YD_UP_WO_LOC_XAXIS
				      ,YD_UP_WO_XAXIS_GAP_MAX
				      ,YD_UP_WO_XAXIS_GAP_MIN
				      ,YD_UP_WO_LOC_YAXIS
				      ,YD_UP_WO_LOC_YAXIS1
				      ,YD_UP_WO_LOC_YAXIS2
				      ,YD_UP_WO_YAXIS_GAP_MAX
				      ,YD_UP_WO_YAXIS_GAP_MIN
				      ,YD_UP_WO_LOC_ZAXIS
				      ,YD_UP_WO_ZAXIS_GAP_MAX
				      ,YD_UP_WO_ZAXIS_GAP_MIN
				      ,YD_DN_WO_LOC
				      ,YD_DN_WO_LAYER
				      ,YD_DN_WO_LOC_XAXIS
				      ,YD_DN_WO_XAXIS_GAP_MAX
				      ,YD_DN_WO_XAXIS_GAP_MIN
				      ,YD_DN_WO_LOC_YAXIS
				      ,YD_DN_WO_LOC_YAXIS1
				      ,YD_DN_WO_LOC_YAXIS2
				      ,YD_DN_WO_YAXIS_GAP_MAX
				      ,YD_DN_WO_YAXIS_GAP_MIN
				      ,YD_DN_WO_LOC_ZAXIS
				      ,YD_DN_WO_ZAXIS_GAP_MAX
				      ,YD_DN_WO_ZAXIS_GAP_MIN
				      ,YD_UP_WR_LOC
				      ,YD_UP_WR_LAYER
				      ,YD_UP_WRK_ACT_GP
				      ,YD_UP_WR_XAXIS
				      ,YD_UP_WR_YAXIS
				      ,YD_UP_WR_YAXIS1
				      ,YD_UP_WR_YAXIS2
				      ,YD_UP_WR_ZAXIS
				      ,YD_DN_WR_LOC
				      ,YD_DN_WR_LAYER
				      ,YD_DN_WRK_ACT_GP
				      ,YD_DN_WR_XAXIS
				      ,YD_DN_WR_YAXIS
				      ,YD_DN_WR_YAXIS1
				      ,YD_DN_WR_YAXIS2
				      ,YD_DN_WR_ZAXIS
				  FROM TB_YD_CRNSCH
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				 ORDER BY YD_CRN_SCH_ID ASC
			 */

			if( rsResult.size() > 0 ) {
				szMsg = methodNm +"] 작업예약["+szYdWbookId+"]으로 크레인스케줄이 존재하므로 작업예약을 삭제하지 않습니다.";
				slabUtils.printLog(logId,  szMsg, "SL");
				return PSlabYdConstant.RETN_CD_FAILURE;
			
			}else if(rsResult.size() == 0){
				szMsg = methodNm +"] 작업예약["+szYdWbookId+"]으로 크레인스케줄이 존재하지 않으므로 작업예약 삭제 가능합니다.";
				slabUtils.printLog(logId,  szMsg, "SL");
			} else {
				szMsg = methodNm +"] 크레인 작업 테이블 정보 error입니다.";
				slabUtils.printLog(logId,  szMsg, "SL");
				return PSlabYdConstant.RETN_CD_FAILURE;
			}
			//------------------------------------------------------------------------------------------------
			szMsg = methodNm +"]  작업예약 clear";
			slabUtils.printLog(logId,  szMsg, "SL");
			
			szRtnMsg = this.delWBookBefoCarOrTCar(recPara);

			if( szRtnMsg.equals(PSlabYdConstant.RETN_CD_SUCCESS) ) {
				szMsg = methodNm +"] 작업예약["+szYdWbookId+"] 차량 스케줄 Clear성공 ";
				slabUtils.printLog(logId,  szMsg, "SL");

			}else if(szRtnMsg.equals(PSlabYdConstant.RETN_CD_FAILURE)){
				szMsg = methodNm +"] 작업예약["+szYdWbookId+"] 대차/차량 스케줄 Clear 실패 ";
				slabUtils.printLog(logId,  szMsg, "SL");

			}else{
				szMsg = methodNm +"] 작업예약["+szYdWbookId+"]  " + szRtnMsg ;
				slabUtils.printLog(logId,  szMsg, "SL");
			}

			//------------------------------------------------------------------------------------------------
			//	작업예약/재료 삭제                                                      
			//------------------------------------------------------------------------------------------------
			szRtnMsg = slabComm.delYdWrkbookNMtl(szYdWbookId, ydUserId, logId);

			if( szRtnMsg.equals(PSlabYdConstant.RETN_CD_SUCCESS) ) {
				szMsg = methodNm +"] 작업예약["+szYdWbookId+"]삭제 성공";
				slabUtils.printLog(logId,  szMsg, "SL");
			}else{
				szMsg = methodNm +"] 작업예약["+szYdWbookId+"]삭제 실패 - 메세지 : " + szRtnMsg;
				slabUtils.printLog(logId,  szMsg, "SL");
			}

			//------------------------------------------------------------------------------------------------
			szMsg = methodNm +"] 메소드 끝";
			slabUtils.printLog(logId,  szMsg, "S-");
		} catch (Exception e) {
			szMsg = methodNm +"] 예외발생 - 메세지 : " + e.getMessage();
			slabUtils.printLog(logId,  szMsg, "SL");
			throw new DAOException(PSlabYdConstant.RETN_CD_FAILURE);
		}
		
		return PSlabYdConstant.RETN_CD_SUCCESS;
	}

	
	/**
	 * 오퍼레이션명 : 차량 또는 대차 작업예약 ID 삭제 Module  
	 * 야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord (JDTORecord : YD_WBOOK_ID , MODIFIER)
	 * @return
	 * @throws JDTOException
	 */
	public String delWBookBefoCarOrTCar(JDTORecord msgRecord)throws JDTOException  {
		String methodNm = "차량 또는 대차 작업예약 ID 삭제 [PSlabYdMvCarSeEJB.delWBookBefoCarOrTCar] < " + msgRecord.getResultMsg();
		String logId    = msgRecord.getResultCode(); 
		String szMsg="";
		/*
		 *  요약 : 작업예약 삭제 전에 해당 작업에 물려 있는 차량스케줄 또는 대차 스케줄 작업예약을 
		 *  	   Clear 하기 위함  
		 *  
		 *  1. 취소할 작업예약 ID를 받는다.
		 *  2. 작업 예약 ID로 스케줄 코드를 조회한다.
		 *  3. 해당 작업예약 스케줄이 차량 대차 작업 일경우 상하차 작업을 확인하여 해당 스케줄의
		 *     상하차 작업 예약 ID를 Clear 한다. 
		 */
		try {
			szMsg = methodNm +"]"+ logId +"] 시작";
			//작업예약   레코드셋 생성
			//JDTORecordSet rsYdWBook =null;
			
			//파라미터 스크링 변수
			//String szOperationName 	= "차량/대차 작업예약 ID 삭제 Module";
			
			String szSchCd 		= "";
			String szCarGp 		= "";
			String szULGp 		= "";
			String szYdWBookId 	= "";
			String ydEqpGp 		= "";
			String szTrneqpCd 	= "";
			//리턴값
			int intRtnVal = 0;
			//체크 값
			msgRecord.getRequestUUID();
			slabUtils.printLog(logId,  szMsg, "S+");
			
			String userId = slabUtils.paraRecChkNull(msgRecord, "MODIFIER");
			/*
			 *  입력값 Check
			 */
			//ydUtils.displayRecord(szOperationName, msgRecord);		
			//String ydUserId      	= slabUtils.trim(inDto.getFieldString("MODIFIER"));
			JDTORecord recPara  	= slabUtils.getParam(logId, methodNm, userId);

			szYdWBookId = slabUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
			ydEqpGp     = slabUtils.paraRecChkNull(msgRecord, "YD_EQP_GP");
			
			recPara.setField("YD_WBOOK_ID", 		szYdWBookId);
			recPara.setField("YD_EQP_GP", 			ydEqpGp   	);
			slabUtils.printLog(logId, "input value)"+ ydEqpGp, "SL");

			if(szYdWBookId.trim().equals("")){
				
				szMsg = methodNm +"] 작업예약 ID가 존재하지 않습니다.";
				slabUtils.printLog(logId,  szMsg, "SL");
				
				return PSlabYdConstant.RETN_CD_EXIST;
			}
			
			/*
			 * 작업예약 ID정보로 작업예약 정보를 조회하여 스케줄 코드를 얻는다.
			 */
			
			//JDTORecordSet rsYdWBook = JDTORecordFactory.getInstance().createRecordSet("rsYdWBook");
			
			/*
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

			//intRtnVal = ydWrkbookDao.getYdWrkbook(msgRecord, rsYdWBook, 0);
			 */
			JDTORecordSet rsYdWBook = commDao.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdWrkbook", logId, methodNm, "작업예약 조회");
			//intRtnVal = rsYdWBook.size();
			if(rsYdWBook.size() < 0 ){
	
				szMsg = methodNm +"] 작업예약 조회 ERROR";
				slabUtils.printLog(logId,  szMsg, "SL");
				
				return PSlabYdConstant.RETN_CD_FAILURE;
			} else if(rsYdWBook.size() == 0){
				szMsg = methodNm +"] 작업예약 데이터가 없습니다.";
				slabUtils.printLog(logId,  szMsg, "SL");
				
				return PSlabYdConstant.RETN_CD_EXIST;
			}
			
			rsYdWBook.first();
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara = rsYdWBook.getRecord();
			
			szSchCd    = slabUtils.paraRecChkNull(recPara, "YD_SCH_CD"); 
			szTrneqpCd = slabUtils.paraRecChkNull(recPara, "TRN_EQP_CD"); 
			//szYdWBookId
			
			slabUtils.printLog(logId, "스케줄코드 :"+szSchCd + "<" + szSchCd.substring(6,7), "SL"); 
			
			if ( "".equals(szSchCd) || (szSchCd.length() != 8)){
				
				szMsg = methodNm +"] 스케줄 코드가 올바르지 않습니다.";
				slabUtils.printLog(logId,  szMsg, "SL");
				
				return PSlabYdConstant.RETN_CD_FAILURE;
			}else{
				
				szULGp = szSchCd.substring(6,7);//L
				
				//설비 구분을 입력 안 받은 경우 작업예약 ID로 스케줄 코드를 찾아 처리 함.
				if(ydEqpGp.equals(null) ||ydEqpGp.equals("") ){
					slabUtils.printLog(logId, szSchCd.substring(2,4)+")"+szSchCd, "SL");
					szCarGp = szSchCd.substring(2,4);
				}else{
					szCarGp = ydEqpGp ;
				}
				slabUtils.printLog(logId, "CarGp:"+ szCarGp +"<<" + szSchCd + ":"+ ydEqpGp, "SL");
				
				//inRec    = JDTORecordFactory.getInstance().create();
				
				//스케줄 코드가 차량 인경우 구분    (TR)
				if(szCarGp.equals(PSlabYdConstant.YD_EQP_GP_PALLET)
				|| szCarGp.equals(PSlabYdConstant.YD_EQP_GP_TRAILER)){
					// 차량인 경우 		
					if(PSlabYdConstant.YD_CRN_SCH_CD_UD.equals(szULGp)){	//U:상차 
						
						szMsg =  methodNm +"] 차량 작업 상차  예약정보 삭제 시작  <"+ szYdWBookId ;
						slabUtils.printLog(logId,  szMsg, "SL");
						
						recPara.setField("MODIFIER"				, userId);
						recPara.setField("YD_CARLD_WRK_BOOK_ID"	, szYdWBookId);
						
						/*
						  UPDATE TB_YD_CARSCH
						   SET  YD_CARLD_WRK_BOOK_ID = ''
						      , MODIFIER = :V_MODIFIER 
						      , MOD_DDTT = SYSDATE
						  WHERE YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
						 * 
						//intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(inRec, 4);
						 */
						intRtnVal =  commDao.update(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updYdCarschYdCarLdWrkBookId", logId, methodNm, "상차 차량 작업 예약 ID CLEAR");
						if(intRtnVal <0 ){
							szMsg = methodNm +"] 차량 작업 상차작업 삭제시 ERROR";
							slabUtils.printLog(logId, szMsg, "SL");
							return PSlabYdConstant.RETN_CD_FAILURE;
						} else if(intRtnVal ==0 ){
							szMsg = methodNm +"] 차량 작업 상차할 작업이 없습니다.";
							slabUtils.printLog(logId,  szMsg, "SL");
						}else{
							szMsg = methodNm +"] 차량 상차 작업예약 ID 삭제하였습니다.";
							slabUtils.printLog(logId,  szMsg, "SL");
						}
						
						//목표동 초기화 작업
						recPara    = JDTORecordFactory.getInstance().create();
						recPara.setField("MODIFIER"		, userId);
						recPara.setField("TRN_EQP_CD"	, szTrneqpCd);
						
						/*
						UPDATE USRYDA.TB_YD_STKCOL
						SET MODIFIER=:V_MODIFIER
						   , MOD_DDTT=SYSDATE
						   , YD_STKBED_USG_CD=NULL
						WHERE TRN_EQP_CD=:V_TRN_EQP_CD
						  AND YD_STK_COL_GP LIKE 'D_PT%'
						  
							//intRtnVal = YdStkColDao.updYdStkcolTrneqpCd(inRec,0);
						 */
						intRtnVal =  commDao.update(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updYdStkcolTrneqpCd", logId, methodNm, " 목표동 초기화 작업 CLEAR");

						slabUtils.printLog(logId, "목표동 초기화 작업"+ intRtnVal, "SL"); 
						
					}else if(szULGp.equals(PSlabYdConstant.YD_CRN_SCH_CD_LD)){  //L하차
						//하차인경우 작업예약 정보 삭제
						
						szMsg = methodNm +"] 차량 작업 하차  예약정보 삭제 시작";
						slabUtils.printLog(logId,  szMsg, "SL");
						
						recPara.setField("MODIFIER"				, userId );
						recPara.setField("YD_CARUD_WRK_BOOK_ID"	, szYdWBookId);

						//sIn = "S";
						/*
							UPDATE TB_YD_CARSCH
							   SET YD_CARUD_WRK_BOOK_ID = ''
							      , MODIFIER 			= :V_MODIFIER
							      , MOD_DDTT 			= SYSDATE
							 WHERE YD_CARUD_WRK_BOOK_ID = :V_YD_CARUD_WRK_BOOK_ID
							   AND DEL_YN = 'N'
						 */
						intRtnVal =  commDao.update(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updYdCarschYdCarUdWrkBookId", logId, methodNm, "하차 차량 작업 예약 ID CLEAR");
						
						if(intRtnVal <0 ){
							szMsg = methodNm +"] 차량 작업 하차작업 삭제시 ERROR";
							slabUtils.printLog(logId,  szMsg, "SL");
							return PSlabYdConstant.RETN_CD_FAILURE;
						}
						
						else if(intRtnVal == 0 ){
							szMsg = methodNm +"] 차량 작업 하차할 작업이 없습니다.";
							slabUtils.printLog(logId,  szMsg, "SL");
							
						}else{
							szMsg = methodNm +"] 차량 하차 작업예약 ID 삭제하였습니다.";
							slabUtils.printLog(logId,  szMsg, "SL");
						}
					}
				} else{
					szMsg = methodNm +"] 차량 작업이외 작업이므로  삭제할 필요가 없습니다.";
					slabUtils.printLog(logId,  szMsg, "SL");
				}
			}
			return PSlabYdConstant.RETN_CD_SUCCESS;
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}		
		
	}
	

	/**
	 * 오퍼레이션명 : (2-4)차량 작업 관리 화면 배차취소 -  배차처리 일괄 clear  	::		
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리 
	 * PYS  2020-12-24
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord canCarSch(GridData gdReq) throws DAOException {
		String methodNm = "배차처리 일괄 clear[PSlabYdMvCarSeEJB.canCarSch] < " +gdReq.getNavigateValue();
		String logId    = gdReq.getIPAddress();
		
		try {
			/************************************************************** 전체 로직 요약
			 화면에서 선택된 갯수만큼 Loop {
				 1. 차량스케쥴 및 삭제대상 작업예약 조회
					1-1. 차량스케쥴 및 삭제대상 작업예약 조회
				 	1-2. 크레인이 작업중이면 초기화 불가함 ->크레인스케줄이 있으면 에러처리 -> 작업예약삭제모듈서 체크함
				 	
				 2. 작업예약 삭제
				 	>> 작업예약삭제 별도 모듈 호출
				 3. 차량스케줄 삭제
				 	3-1. 차량작업재료 삭제
				 	3-2. 차량스케쥴 삭제
				 4. 야드맵 정보 Clear
				 	4-1. 해당 point에 다른 차량이 점거중인 지 확인(야드에 다른 차량이 진입 했지만 화면에 갱신이 안된 경우)
				 		>> 차량포인트에 초기화 대상 차량이 아닌 타 차량이 들어온 경우 포인트 clear 작업은 생략한다.
				 	4-2. 차량포인트(TB_YD_CARPOINT), 적치열(TB_YD_STKCOL) 테이블 Clear
				 	4-3. 저장위치제원 L2 전송
			 }
			 **********************************************************************/
			int    intRtnVal    		= 0;
			int    rowCnt				= 0; 
			String szMsg        		= "";		
			String sModifier			= "";
			String queryId				= "";
			
			// 화면에서 선택된 row의 값
			String ydCarSchId 			= "";	// 차량스케쥴 id
			
			// 차량스케줄 조회 후 저장용
			String sTrnEqpCd			= "";	// 차량스케줄의 차량번호
			String sCarStopLoc			= "";	// 차량정지위치
			
			slabUtils.printLog(logId, methodNm , "S+");
			sModifier     		 = slabUtils.trim(gdReq.getParam("userid" )); 			//수정자(MODIFIER)
			JDTORecord jrRtn     = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
			JDTORecord jrParam   = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set
			
			slabUtils.printLog(logId, "▶▷ 후판슬라브야드 차량작업 초기화 시작! ◁◀", "SL");
			
			rowCnt			= gdReq.getHeader("CHECK").getRowCount();	// 체크된 row 갯수
			
			for(int i = 0; i < rowCnt; i++ ) {	// 화면에서 선택된 갯수만큼 Loop
				slabUtils.printLog(logId, "▶ " + i + "번째 선택 행 초기화 시작", "SL");
				
				/*********************************************** 1. 차량스케쥴 및 삭제대상 작업예약 조회 *************************************************/
				slabUtils.printLog(logId, "▶ 1. 차량스케쥴 및 삭제대상 작업예약 조회", "SL");
				
				ydCarSchId		= slabUtils.trim(slabUtils.getValue(gdReq, "YD_CAR_SCH_ID"		, i));	// 차량스케쥴ID
				slabUtils.printLog(logId, "▷ 초기화 대상 차량스케줄ID : " + ydCarSchId, "SL");
				
				jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
				/*
				SELECT CS.TRN_EQP_CD                                                    -- 차량번호
				     , CS.YD_CAR_USE_GP                                                 -- 차량사용구분
				     , CS.YD_CAR_SCH_ID                                                 -- 차량스케쥴ID
				     , CASE WHEN CS.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN CS.YD_CARLD_STOP_LOC
				            WHEN CS.YD_CAR_PROG_STAT IN ('A','B','C','D','E') THEN CS.YD_CARUD_STOP_LOC
				            ELSE '' END                        AS YD_CAR_STOP_LOC       -- 차량정지위치
				     , CS.YD_CAR_PROG_STAT                     AS YD_CAR_PROG_STAT      -- 차량진행상태
				     , (SELECT MAX(YD_CRN_SCH_ID) 
				          FROM USRYDA.TB_YD_CRNSCH 
				         WHERE DEL_YN='N' 
				           AND YD_WBOOK_ID = WB.YD_WBOOK_ID  ) AS YD_CRN_SCH_ID         -- 작업중인 크레인스케쥴ID
				     , (SELECT YD_PREP_SCH_ID
				          FROM USRYDA.TB_YD_PREPSCH
				         WHERE YD_WBOOK_ID = WB.YD_WBOOK_ID
				           AND ROWNUM = 1                    ) AS YD_PREP_SCH_ID        -- 준비스케줄ID(이송LOT ID)
				     , WB.YD_SCH_CD                            AS YD_SCH_CD             -- 스케줄코드
				     , WB.YD_WBOOK_ID                          AS YD_WBOOK_ID           -- 작업예약ID
				     , WB.CAR_NO
				     , WB.CARD_NO
				  FROM USRYDA.TB_YD_CARSCH     CS -- 차량스케줄
				     , USRYDA.TB_YD_WRKBOOK    WB -- 작업예약
				 WHERE 1=1
				   AND CS.TRN_EQP_CD           = WB.TRN_EQP_CD(+)
				   AND CS.DEL_YN               = 'N'
				   AND WB.DEL_YN(+)            = 'N'
				   AND WB.YD_GP(+)             = 'D'
				   AND CS.YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID  -- 차량스케줄 ID
				*/
				slabUtils.printLog(logId, "▶ 해당 차량의 차량스케줄 및 작업예약ID 정보를 확인합니다.", "SL");
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarSchWrkId";
			    JDTORecordSet jsSchInfo = commDao.select(jrParam, queryId, logId, methodNm, "해당 차량의 차량스케줄 및 작업예약ID 조회");
			    
			    slabUtils.printLog(logId, "▷ 해당 차량의 차량스케줄 및 작업예약ID 조회 결과 건수 : " + jsSchInfo.size(), "SL");
			    if( jsSchInfo.size() < 1 ) {
			    	szMsg = "차량스케줄 정보가 없습니다. [" + ydCarSchId +"]";
			    	slabUtils.printLog(logId, szMsg, "SL");

					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", szMsg);
					return jrRtn;
			    }
			    
			    /*********************************************** 1. 차량스케쥴 및 삭제대상 작업예약 조회 End ********************************************/
			    /* 작업예약이 존재하면 작업예약 취소 모듈 호출 */
			    String sRtnCd	= "";
		    	String sRtnMsg  = "";
		    	
		    	slabUtils.printLog(logId, "▶  " + jsSchInfo.size() + "건의 작업예약을 삭제합니다.", "SL");
		    	
			    for(int ii = 0 ; ii < jsSchInfo.size() ; ii++){	// 선택된 차량스케쥴에 포함된 작업예약건수만큼 Loop
			    	
			    	/*********************************************** 2. 작업예약삭제 *************************************************/
			    	slabUtils.printLog(logId, "▶ 2. 작업예약삭제", "SL");
			    	
			    	JDTORecord jrCarSchInfo = jsSchInfo.getRecord(ii);
				    
			    	if(ii == 0){	// 1번만 실행
				    	sTrnEqpCd 				= jrCarSchInfo.getFieldString("TRN_EQP_CD"		);	// 차량번호
				    	sCarStopLoc				= jrCarSchInfo.getFieldString("YD_CAR_STOP_LOC"	);	// 차량정지위치
				    }
			    	
			    	String sYdWbookId		= jrCarSchInfo.getFieldString("YD_WBOOK_ID");			// 삭제대상 작업예약ID
			    	String sSchCd			= jrCarSchInfo.getFieldString("YD_SCH_CD");				// 삭제대상 스케쥴코드
			    	
			    	slabUtils.printLog(logId, "▶ " + ii + "번째 작업예약ID의 작예약업 취소를 호출합니다.", "SL");
			    	slabUtils.printLog(logId, "▷ " + ii + "번째 삭제대상 작업예약ID(" + sYdWbookId + ") / 스케쥴코드(" + sSchCd + ")", "SL");
			    	
			    	if(slabUtils.isEmpty(sYdWbookId) && slabUtils.isEmpty(sSchCd)){ // 작업예약정보가 있을때만 작업예약 취소처리 
			    		slabUtils.printLog(logId, "▶ 작업예약 정보가 없습니다. continue..", "SL");
			    		continue;
			    	}
			    	
			    	// 하차작업예약 취소
			    	jrParam.setField("YD_WBOOK_ID"	, sYdWbookId	);	// 작업예약ID
			    	jrParam.setField("YD_SCH_CD"	, sSchCd		);	// 스케쥴코드
			    	
			    	// PSlabYdJspSeEJB.trtWrkBookCncl >> 작업예약 취소처리
					EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
					JDTORecord jrWbkResult = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			    	sRtnCd	= slabUtils.nvl(jrWbkResult.getFieldString("RTN_CD"), "0");
			    	sRtnMsg	= slabUtils.trim(jrWbkResult.getFieldString("RTN_MSG"));
			    			
			    	if( !"1".equals(sRtnCd) ) {
			    		slabUtils.printLog(logId, "▶ " + ii + "번째 작업예약 취소시 ERROR 발생.", "SL");
			    		slabUtils.printLog(logId, sRtnMsg, "SL");

						jrRtn.setField("RTN_CD"  , "0");
						jrRtn.setField("RTN_MSG" , sRtnMsg);
						return jrRtn;
			    	}
			    	
			    	slabUtils.printLog(logId, "▶ 2. 작업예약삭제  End", "SL");
			    	/*********************************************** 2. 작업예약삭제  End *********************************************/
			    }
		    	
			    
		    	/*********************************************** 3. 차량스케줄 삭제 *************************************************/
			    slabUtils.printLog(logId, "▶ 3. 차량스케줄 삭제", "SL");
			    
			    slabUtils.printLog(logId, "▷ 삭제 대상 차량스케줄ID : " + ydCarSchId, "SL");
			    jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
				
				/* 
				UPDATE TB_YD_CARFTMVMTL
				   SET DEL_YN   = 'Y'
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				slabUtils.printLog(logId, "▶ 차량스케줄 작업재료를 삭제합니다." , "SL");
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updDelCarWrMgtSchMtl";
				intRtnVal = commDao.update(jrParam, queryId, logId, methodNm, "TB_YD_CARFTMVMTL 수정");
				slabUtils.printLog(logId, "▶ 차량SCH["+ ydCarSchId +"] 재료 삭제 "+ intRtnVal +"건" , "SL");
				
				/* 
				UPDATE TB_YD_CARSCH
				   SET DEL_YN   = 'Y'
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				slabUtils.printLog(logId, "▶ 차량스케줄ID를 삭제합니다." , "SL");
				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updDelCarWrMgtSch", logId, methodNm, "TB_YD_CARSCH 수정");
				slabUtils.printLog(logId, "▶ 차량SCH["+ ydCarSchId +"] 삭제 "+ intRtnVal +"건" , "SL");
				
				slabUtils.printLog(logId, "▶ 3. 차량스케줄 삭제  End", "SL");
				/*********************************************** 3. 차량스케줄 삭제  End *********************************************/
				
				
				/*********************************************** 4. 야드맵 정보 Clear *************************************************/
				slabUtils.printLog(logId, "▶ 4. 야드맵 정보 Clear", "SL");
				
		    	String tmpTrnEqpCd = "";	// 현재 점유중인 차량번호
		    	
		    	slabUtils.printLog(logId, "▷ 차량포인트 위치(YD_STK_COL_GP) :"+ sCarStopLoc, "SL");
		    	
		    	jrParam.setField("YD_STK_COL_GP", sCarStopLoc);
			    /*
				SELECT TRN_EQP_CD 
				  FROM  TB_YD_STKCOL 
				 WHERE  YD_STK_COL_GP = :V_YD_STK_COL_GP
			     */
		    	JDTORecordSet jsStkColInfo = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getYdStkcol", logId, methodNm, "차량포인트의 점유차량 검색");
		    	
		    	if(jsStkColInfo.size() > 0 ){
		    		tmpTrnEqpCd = jsStkColInfo.getRecord(0).getFieldString("TRN_EQP_CD");	// 현재 점유중인 차량번호
		    	}
		    	
		    	slabUtils.printLog(logId, "▶ 차량포인트에 초기화 대상 차량이 아닌 타 차량이 있는 경우 포인트 clear 작업은 생략한다." , "SL");
		    	slabUtils.printLog(logId, "▷ 차량스케쥴 차량	: " + sTrnEqpCd		, "SL");
	    		slabUtils.printLog(logId, "▷ 현재점유중인 차량	: " + tmpTrnEqpCd	, "SL");
	    		
	    		// 현재 차량과 carpoint에 있는 차량이 다르면  carpoint및 stkcol 은 변경금지
		    	if(!sTrnEqpCd.equals(tmpTrnEqpCd)) {	// 차량스케줄의 차량번호와 현재점유중인 차량번호가 다르면 새로운 차량이 들어온 것으로 본다.
		    		slabUtils.printLog(logId, "▶ 점유차량 확인 완료. 해당 위치에 차량이 없거나 다른 차량이 도착했습니다." , "SL");
		    	}else{
		    		slabUtils.printLog(logId, "▶ 새로 들어온 차량이 없습니다. 야드맵(차량포인트) 정보를 초기화 합니다." , "SL");
		    		
		    		// 차량포인트 초기화
		    		slabUtils.printLog(logId, "▶ 차량포인트(" + sCarStopLoc + ")를 초기화 합니다." , "SL");
		    		jrParam.setField("STAT"			, "C"			);	// [C]Close(비활성화) [L]활성 [N]불가
			    	jrParam.setField("YD_STK_COL_GP", sCarStopLoc	);
		    		/* 
			    	UPDATE TB_YD_CARPOINT
			    	   SET CARD_NO       = NULL
			    	     , CAR_NO        = NULL
			    	     , TRN_EQP_CD    = NULL
			    	     , YD_STK_COL_ACT_STAT = :V_STAT
			    	     , MODIFIER      = :V_MODIFIER
			    	     , MOD_DDTT      = SYSDATE
			    	 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			    	   AND DEL_YN = 'N'
		    		 */
			    	queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updCarPointStkColGpC";
			    	intRtnVal = commDao.update(jrParam, queryId, logId, methodNm, "차량POINT 차량정보 초기화");
		    		if(intRtnVal < 1){ slabUtils.printLog(logId, "▶ 차량포인트 초기화 실패.", "SL"); }
		    		
		    		
		    		// 적치열 초기화
		    		slabUtils.printLog(logId, "▶ 해당 적치열(" + sCarStopLoc + ")을 초기화 합니다."	, "SL");
		    		/* 
					UPDATE TB_YD_STKCOL
					   SET YD_STK_COL_ACT_STAT = DECODE(YD_STK_COL_ACT_STAT, 'N', 'N', 'C')
					     , TRN_EQP_CD       = ''
					     , YD_CAR_USE_GP    = ''
					     , CAR_NO           = ''
					     , CARD_NO          = ''
					     , MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					  -- ,YD_STK_COL_ACT_STAT = 'C'
					 WHERE YD_STK_COL_GP    = :V_YD_STK_COL_GP
		    		 */
		    		queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updCarWrMgtStkcol";
		    		intRtnVal = commDao.update(jrParam, queryId, logId, methodNm, "TB_YD_STKCOL 초기화");
		    		if(intRtnVal < 1){ slabUtils.printLog(logId, "▶ 적치열 초기화 실패.", "SL"); }
		    	
		    		
		    		// 적치Bed 초기화
		    		slabUtils.printLog(logId, "▶ 해당 적치열의 Bed(" + sCarStopLoc + ")를 비활성상태, BED중량 MAX기본값(" + PSlabYdConstant.YD_CAR_BED_WT_MAX_DEFAULT + ")으로 변경 합니다."	, "SL");
		    		// 저장위치 비활성화등록
		    		jrParam.setField("YD_STK_COL_GP"	  , sCarStopLoc														);
		    		jrParam.setField("YD_STK_BED_ACT_STAT", "C"																);
		    		jrParam.setField("YD_STK_BED_WT_MAX"  , slabUtils.nvl(PSlabYdConstant.YD_CAR_BED_WT_MAX_DEFAULT, "0")	); // Bed 최대중량  : 100000
		    		/*
					UPDATE TB_YD_STKBED
					   SET YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
					     , YD_STK_BED_WT_MAX   = :V_YD_STK_BED_WT_MAX
					     , MOD_DDTT            = SYSDATE
					     , MODIFIER            = :V_MODIFIER
					 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
		    		 */
		    		queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStkbedActStat";
		    		intRtnVal = commDao.update(jrParam, queryId, logId, methodNm, "TB_YD_STKBED 비활성화등록");
		    		if(intRtnVal < 1){ slabUtils.printLog(logId, "▶ 적치Bed 초기화 실패.", "SL"); }
		    		
		    		
		    		// 적치단 초기화
		    		slabUtils.printLog(logId, "▶ 적치열["+sCarStopLoc+"]의 적치단을 비활성상태로 변경합니다.", "SL");
		    		jrParam.setField("YD_STK_COL_GP"		, sCarStopLoc	);
		    		jrParam.setField("YD_STK_LYR_ACT_STAT"	, "C"			);	// 적치열 활성상태 : [C]Close(비활성화) [L]활성 [N]불가
		    		jrParam.setField("STL_NO"				, ""			);	// 재료번호
		    		jrParam.setField("YD_STK_LYR_MTL_STAT"	, "E"			);	// 재료상태 : [C]적치 중 [D]권하대기 [E]적치가능 [U]권상대기 [X]적치불가
		    		/*
			    	UPDATE TB_YD_STKLYR
			    	   SET YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
			    	     , STL_NO              = :V_STL_NO
			    	     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
			    	     , MODIFIER            = :V_MODIFIER
			    	     , MOD_DDTT            = SYSDATE
			    	WHERE YD_STK_COL_GP        = :V_YD_STK_COL_GP
		    		 */
		    		intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStklyrYdStkColGp", logId, methodNm, "TB_YD_STKLYR 초기화");
		    		if(intRtnVal < 1){ slabUtils.printLog(logId, "▶ 적치단 초기화 실패.", "SL"); }
		    		
		    		slabUtils.printLog(logId, "▶ 야드맵(차량포인트) 정보 초기화 작업이 완료되었습니다." , "SL");
		    		
					slabUtils.printLog(logId, "▶ 야드맵(열,BED,단) 정보가 변경되었으므로 L2에 저장위치제원(YDY3L001)-차량정보를 송신합니다." , "SL");
		    		/**********************************************************
	    			 * 변경된 야드맵 정보를 L2(YDY3L001:저장위치 제원)로 전송 -> 출발정보로 Set 후 전송한다.
	    			 **********************************************************/
	    			JDTORecord sndL2Msg = slabUtils.getParam(logId, methodNm, sModifier);
	    			
	    			sndL2Msg.setField("MSG_GP"				, "I"						); // 전문구분
	    			sndL2Msg.setField("YD_INFO_SYNC_CD"		, "4"						); // [1]동, [2]SPAN, [3]열, [4]Bed
	    			sndL2Msg.setField("YD_STK_COL_GP"    	, sCarStopLoc				); // 차량포인트
	    			sndL2Msg.setField("YD_STK_BED_NO"    	, "01"						); // L2에서는 2Bed 관리안함 -> '01'로 한번만 보내주면 된다.
	    			
	    			if ("".equals(sTrnEqpCd)) {
	    				sndL2Msg.setField("YD_CAR_USE_GP"    	, "G"   ); //L:구내운송, G:출하차량
	    				sndL2Msg.setField("CAR_NO"  			, ""	); //차량번호
	    				sndL2Msg.setField("CARD_NO"  			, ""	); //카드번호
	    			} else {
	    				sndL2Msg.setField("TRN_EQP_CD"  		, sTrnEqpCd);
	    			}
	    			
	    			sndL2Msg.setField("YD_CAR_ARRSTRT_STAT" , "S"						); // [A]도착, [S]출발
	    			sndL2Msg.setField("YD_EQP_WRK_STAT"     , "L"						); // 야드설비작업상태 [L]공차(상차), [U]영차(반입)
	    			
	    			jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L001_CarInfo", sndL2Msg));
	    			
	    			slabUtils.printLog(logId, "▶ 적치열["+sCarStopLoc+"]에 대한 야드저장위치제원 전문 송신 QUEUE 저장 완료.", "SL");
	    			slabUtils.printLog(logId, "▶ 4. 야드맵 정보 Clear End", "SL");
	    			/*********************************************** 4. 야드맵 정보 Clear End **********************************************/
		    	
		    	}
			}
			szMsg = "";
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", szMsg);
			
			slabUtils.printLog(logId, "▶ 해당 차량정보의 초기화 작업이 완료되었습니다." , "SL");
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 이송작업재료삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm = "이송작업재료삭제[BSlabJspFaEJB.delCarFtMvMtl] <" + gdReq.getNavigateValue() +"]";
		String logId    = gdReq.getIPAddress();
		
		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));
			//등록 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				//이송작업재료삭제
				slabUtils.printLog(logId, ii+"[STL_NO]"+slabUtils.getValue(gdReq, "STL_NO", ii) , "SL");
			
				jrParam.setField("YD_CAR_SCH_ID"	, slabUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("STL_NO"			, slabUtils.getValue(gdReq, "STL_NO", ii)); 
				
 				/* 이송작업재료삭제 -- com.inisteel.cim.ym.bcoil.dao.BCoilDAO.delCarFtMvMtl 
				UPDATE USRYDA.TB_YD_CARFTMVMTL
				   SET DEL_YN = 'Y'
					   ,MODIFIER  =  :V_MODIFIER 
					   ,MOD_DDTT =  SYSDATE
				WHERE  YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				AND    STL_NO = :V_STL_NO   
 			 	*/
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.delCarFtMvMtl", logId, methodNm, "이송작업재료삭제");
			}
			//차량 작업 상태,매수,작업완료시간 update
			/*  --이송차량스케줄 차량작업상태 수정
			 *  com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarSchWrkStSlab 
				
				UPDATE USRYDA.TB_YD_CARSCH TS
				   SET TS.MODIFIER             = :V_MODIFIER
				     , TS.MOD_DDTT             = SYSDATE
				     , TS.YD_EQP_WRK_STAT      = DECODE((SELECT COUNT(*) 
				                                    FROM TB_YD_CARFTMVMTL 
				                                   WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID),0,'U','L') --'L' : 영차 ,'U' : 공차
				     , TS.YD_EQP_WRK_SH        = (SELECT COUNT(*) 
				                                    FROM TB_YD_CARFTMVMTL 
				                                   WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID)
				     , TS.YD_EQP_WRK_WT        = (SELECT SUM(SLAB_WT) 
				                                    FROM TB_YD_CARFTMVMTL A
				                                       , VW_YD_SLABCOMM   B
				                                   WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				                                     AND A.STL_NO        = B.SLAB_NO
				                                   )
				     , TS.YD_PNT_CD3           = NVL(YD_PNT_CD3,'0000')
				WHERE TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID 
			 */
			jrParam.setField("YD_CAR_SCH_ID"	, slabUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")));
			String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updCarSchWrkStSlab";
			commDao.update(jrParam, queryId, logId, methodNm, "이송차량스케줄 차량작업상태 수정");
			
			slabUtils.printLog(logId, methodNm, "S-");

			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "정상처리되었습니다.");
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of delCarFtMvMtl

	/**
	 * 하차처리를 위한 차량스케줄 신규 생성 처리 
	 * 야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord mkUdCarSch(GridData gdReq) throws DAOException {
		String mthdNm = "하차백업생성[PSlabYdMvCarSeEJB.mkUdCarSch] < " + gdReq.getNavigateValue();
		String logId  = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, mthdNm, "S+");
			
			JDTORecord jrRtn   = slabUtils.getParam(logId, mthdNm, slabUtils.trim(gdReq.getParam("userid")));

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, mthdNm, slabUtils.trim(gdReq.getParam("userid")));
			
			String sYdCarSchId = commDao.getSeqId(logId, mthdNm, "CarSch");
			jrParam.setField("YD_CAR_SCH_ID"		, sYdCarSchId					); //야드차량스케쥴ID
			String szArrWlocCd = gdReq.getParam("ARR_WLOC_CD");
			String ydCarldStopLoc = "";
			if("DWY22".equals(szArrWlocCd)) {
				ydCarldStopLoc = "DBPT01";
			} else if("DKY21".equals(szArrWlocCd)) {
				ydCarldStopLoc = "DAPT01";
			}else {
				ydCarldStopLoc = "DBPT01";
			}
			
			jrParam.setField("YD_CAR_PROG_STAT"		, PSlabYdConstant.YD_CAR_PROG_STAT_5 ); //차량진행상태 (5:상차완료)
			jrParam.setField("YD_CAR_USE_GP"		, "L"							); //야드차량사용구분 (L:구내운송, G:출하차량 )
			jrParam.setField("YD_EQP_WRK_STAT"		, "L"							); //야드설비작업상태 (L:영차, U:공차)
			jrParam.setField("SPOS_WLOC_CD"			, gdReq.getParam("SPOS_WLOC_CD")); //발지개소코드(상차지)
			jrParam.setField("ARR_WLOC_CD"			, gdReq.getParam("ARR_WLOC_CD")	); //착지개소코드(하차지)
			jrParam.setField("YD_PNT_CD"			, ""							); //야드상차포인트코드(발지)
			//YD_PNT_CD를  이곳에서 정할수 없음.  (예) DWY22 =1B01, 1B02 / DJY25 = 1M01등 
			jrParam.setField("YD_CARLD_WRK_BOOK_ID"	, ""							); //야드상차작업예약ID
			jrParam.setField("YD_CARLD_STOP_LOC"	, ""							); //야드상차정지위치
			jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, ""							); 
			jrParam.setField("YD_CARUD_STOP_LOC"	, ydCarldStopLoc				); 
			jrParam.setField("TRN_EQP_CD"			, gdReq.getParam("TRN_EQP_CD")	); //운송장비코드
			
			/*
				INSERT INTO TB_YD_CARSCH
				(
				  YD_CAR_SCH_ID
				, REGISTER
				, REG_DDTT
				, MODIFIER
				, MOD_DDTT
				, DEL_YN
				, YD_EQP_ID
				, YD_CAR_USE_GP
				, TRN_EQP_CD
				, YD_EQP_WRK_STAT
				, SPOS_WLOC_CD
				, ARR_WLOC_CD
				, YD_CARLD_LEV_DT --야드상차출발일시
				, YD_CARLD_PNT_WO_DT --야드상차포인트지시일시
				, YD_PNT_CD1 --야드상차포인트(4자리)
				, YD_CARLD_WRK_BOOK_ID --야드상차작업예약ID
				, YD_CARLD_STOP_LOC --야드상차정지위치(적치열6자리)
				, YD_CAR_PROG_STAT --야드차량진행상태
				, YD_CARUD_WRK_BOOK_ID --야드상차작업예약ID
				, YD_CARUD_STOP_LOC --야드상차정지위치(적치열6자리)
				) 
				SELECT :V_YD_CAR_SCH_ID
				     , :V_MODIFIER
				     , SYSDATE
				     , :V_MODIFIER
				     , SYSDATE
				     , 'N'
				     , (SELECT YD_EQP_ID
				          FROM TB_YD_CARSPEC
				         WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				           AND DEL_YN = 'N'
				           AND ROWNUM <= 1
				       ) AS YD_EQP_ID
				     , :V_YD_CAR_USE_GP
				     , :V_TRN_EQP_CD
				     , :V_YD_EQP_WRK_STAT
				     , :V_SPOS_WLOC_CD
				     , :V_ARR_WLOC_CD
				     , SYSDATE
				     , SYSDATE
				     , :V_YD_PNT_CD
				     , NVL(:V_YD_CARLD_WRK_BOOK_ID,'')
				     , :V_YD_CARLD_STOP_LOC
				     , :V_YD_CAR_PROG_STAT
				     , NVL(:V_YD_CARUD_WRK_BOOK_ID,'')
				     , :V_YD_CARUD_STOP_LOC
				  FROM DUAL
			 */
			commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.insCarSchLd", logId, mthdNm, "차량스케쥴 상차출발로 INSERT");
			slabUtils.printLog(logId, "test", "SL");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "정상처리되었습니다.");
			slabUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}
	

	/**
	 *      [A] 오퍼레이션명 : 이송차량 실적처리 팝업 - 등록
	 * 		야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtMvCarStatSet2(GridData gdReq) throws DAOException {
		String methodNm = "이송차량 실적처리 팝업 - 등록 [PSlabYdMvCarSeEJB.trtMvCarStatSet2] < " + gdReq.getNavigateValue();
		String logId    = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			String sJmsTcCd 			= slabUtils.trim(gdReq.getParam("JMS_TC_CD"));
			String sTrnWrkFullvoidGp 	= slabUtils.trim(gdReq.getParam("TRN_WRK_FULLVOID_GP"));
			String sTrnEqpCd			= slabUtils.trim(gdReq.getParam("TRN_EQP_CD"));
			String sYdCarSchId			= slabUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"));
			String sSposWlocCd 			= slabUtils.trim(gdReq.getParam("SPOS_WLOC_CD"));
			String sYdPntCd1 			= slabUtils.trim(gdReq.getParam("YD_PNT_CD1"));
			String sYdCarldStopLoc 		= slabUtils.trim(gdReq.getParam("YD_CARLD_STOP_LOC"));
			String sArrWlocCd 			= slabUtils.trim(gdReq.getParam("ARR_WLOC_CD"));
			String sYdPntCd3 			= slabUtils.trim(gdReq.getParam("YD_PNT_CD3"));
			String sYdCarudStopLoc 		= slabUtils.trim(gdReq.getParam("YD_CARUD_STOP_LOC"));	
			String sToLoc 				= slabUtils.trim(gdReq.getParam("TO_LOC"));
			String sWlocCd 				= "";
			String sYdPntCd 			= "";
			String sYdCarldPntWoDt 		= "";
			String sYdCarudPntWoDt 		= "";

			String modifier = slabUtils.trim(gdReq.getParam("userid"      ));	//수정자
			String currDate = slabUtils.getDateTime14();						//현재시각

			if ("".equals(sTrnEqpCd)) {
				throw new Exception("운송장비코드가 없습니다.");
			} else {
				slabUtils.printLog(logId, "TRN_EQP_CD"+sTrnEqpCd, "SL");
			}

			//Return Value
			JDTORecord jrRtn = null;
			
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));
			
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, modifier);
			
			/*----------------------------------------------------------------
				01. 포인트 지시	(YDTSJ011)		
				02. 상차도착처리	(TSYDJ003)	03. 상차개시(YDTSJ007)	04. 상차완료(YDTSJ008)	05. 상차완료 후 출발처리 (TSYDJ004)
				06. 하차도착처리	(TSYDJ003)	07. 하차개시(YDTSJ009)	08. 하차완료(YDTSJ010)	09. 하차완료 후 출발처리 (TSYDJ004)
			 *----------------------------------------------------------------*/
			jrYdMsg.setField("JMS_TC_CD"         		, sJmsTcCd);
			slabUtils.printLog(logId,  "==JMS_TC_CD:"+ sJmsTcCd, "SL");
			
			if("TSYDJ003".equals(sJmsTcCd)) { //소재차량도착
				
				jrYdMsg.setField("TRN_EQP_CD"   	  		, sTrnEqpCd);
				
				if("F".equals(sTrnWrkFullvoidGp)) {			//하차도착
					jrYdMsg.setField("ARR_WLOC_CD"     	  		, sArrWlocCd);
					jrYdMsg.setField("ARR_YD_PNT_CD"   	  		, sYdPntCd3);
					jrYdMsg.setField("TRN_WRK_FULLVOID_GP"		, sTrnWrkFullvoidGp);
				} else if("E".equals(sTrnWrkFullvoidGp)) {	//상차도착
					jrYdMsg.setField("ARR_WLOC_CD"     	  		, sSposWlocCd);
					jrYdMsg.setField("ARR_YD_PNT_CD"   	  		, sYdPntCd1);
					jrYdMsg.setField("TRN_WRK_FULLVOID_GP"		, sTrnWrkFullvoidGp);
				}
				//EJBConnector sndConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);
				//jrRtn = (JDTORecord)sndConn.trx("rcvTSYDJ003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			
//				jrRtn = this.rcvTSYDJ003(jrYdMsg);
				jrRtn = null; 
				
				
			} else if("TSYDJ004".equals(sJmsTcCd)) { 			//소재차량출발
				
				jrYdMsg.setField("TRN_EQP_CD"   	  			, sTrnEqpCd);
				jrYdMsg.setField("TRN_WRK_FULLVOID_GP"			, sTrnWrkFullvoidGp);
				jrYdMsg.setField("TRN_EQP_STK_CAPA"		    	, "80000");
				
				slabUtils.printLog(logId, "영공(TrnWrkFullvoidGp):"+ sTrnWrkFullvoidGp +"<--E:상차,F:영차", "SL");
				if("F".equals(sTrnWrkFullvoidGp)) { 			//영차:하차하러 출발 
					jrYdMsg.setField("SPOS_WLOC_CD"     	  	, sSposWlocCd );
					jrYdMsg.setField("SPOS_YD_PNT_CD"   	  	, sYdPntCd1  );
					jrYdMsg.setField("ARR_WLOC_CD"     	  		, sArrWlocCd );
					jrYdMsg.setField("ARR_YD_PNT_CD"   	  		, sYdPntCd3);
					
					slabUtils.printLog(logId, "발(SPOS_WLOC_CD):"+sSposWlocCd, "SL");
					slabUtils.printLog(logId, "착(ARR_WLOC_CD) :"+sArrWlocCd, "SL");
					slabUtils.printLog(logId, " POINT  :" +sYdPntCd3, "SL");
					
				} else if("E".equals(sTrnWrkFullvoidGp)) { 	//(하차완료후 )출발처리로 착지개소를 DMY1P로 줌으로써 차량스케줄 완료처리를 한다.
					jrYdMsg.setField("SPOS_WLOC_CD"     	  	, sArrWlocCd);
					jrYdMsg.setField("SPOS_YD_PNT_CD"   	  	, sYdPntCd3);
					jrYdMsg.setField("ARR_WLOC_CD"     	  		, "DMY1P");
					jrYdMsg.setField("ARR_YD_PNT_CD"   	  		, "");
					
					slabUtils.printLog(logId, "발(SPOS_WLOC_CD) :"+sArrWlocCd, "SL");
					slabUtils.printLog(logId, "착(ARR_WLOC_CD)  :"+"DMY1P", "SL");
					slabUtils.printLog(logId, " POINT  :" +sYdPntCd3, "SL");
				}
				
				slabUtils.printLog(logId, " ", "SL");
				
				//EJBConnector sndConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);
				//jrRtn = (JDTORecord)sndConn.trx("rcvTSYDJ004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				jrRtn = this.rcvTSYDJ004(jrYdMsg);

			} else if("YDTSJ007".equals(sJmsTcCd)) { //소재차량상차개시
				
				//차량진행상태를 하차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT"		, PSlabYdConstant.YD_CAR_PROG_STAT_4 );  //상차개시
				jrParam.setField("YD_CARLD_ST_DT"		, currDate	);  //상차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARLD_CMPL_DT"		, ""		);  //상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_ST_DT"		, ""		);  //하차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT"		, ""		);  //하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID"		, sYdCarSchId); 
				
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updMvCarSchCmpl", logId, methodNm, "이송차량스케줄 상차개시로 수정");
				
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate		); //JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD"        , sTrnEqpCd	); //운송장비코드
				jrYdMsg.setField("SPOS_WLOC_CD"      , sSposWlocCd  ); //발지개소코드
				jrYdMsg.setField("SPOS_YD_PNT_CD"    , sYdPntCd1	); //발지야드포인트코드
				jrYdMsg.setField("ARR_WLOC_CD"       , sArrWlocCd	); //착지개소코드
				jrYdMsg.setField("TRN_WRK_ST_DT"     , currDate    	); //운송작업시작일시
				
				//전송할 전문에 추가
				jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);
				
			} else if("YDTSJ008".equals(sJmsTcCd)) { //소재차량상차완료
				
				//차량진행상태를 상차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT"		, PSlabYdConstant.YD_CAR_PROG_STAT_5 );  //상차완료
				jrParam.setField("YD_CARLD_ST_DT"		, ""		);  //상차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARLD_CMPL_DT"		, currDate	);  //상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_ST_DT"		, ""		);  //하차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT"		, ""		);  //하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID"		, sYdCarSchId); 
				
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updMvCarSchCmpl", logId, methodNm, "이송차량스케줄 상차완료로 수정");
				
				jrYdMsg.setField("YD_CAR_SCH_ID"     , sYdCarSchId); //차량스케줄ID
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ008", jrYdMsg));
				
				
			} else if("YDTSJ009".equals(sJmsTcCd)) { //소재차량하차개시
				
				//차량진행상태를 하차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT"		, PSlabYdConstant.YD_CAR_PROG_STAT_D	);  //하차개시
				jrParam.setField("YD_CARLD_ST_DT"		, ""		);  //상차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARLD_CMPL_DT"		, ""		);  //상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_ST_DT"		, currDate	);  //하차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT"		, ""		);  //하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID"		, sYdCarSchId); 
				
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updMvCarSchCmpl", logId, methodNm, "이송차량스케줄 하차개시로 수정");
				
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate    ); //JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD"        , sTrnEqpCd   ); //운송장비코드
				jrYdMsg.setField("ARR_WLOC_CD"       , sArrWlocCd  ); //착지개소코드
				jrYdMsg.setField("ARR_YD_PNT_CD"     , sYdPntCd3   ); //착지야드포인트코드
				jrYdMsg.setField("TRN_WRK_ST_DT"     , currDate    ); //운송작업시작일시
				
				//전송할 전문에 추가
				jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);
				
				
			} else if("YDTSJ010".equals(sJmsTcCd)) { //소재차량하차완료
				
				//차량진행상태를 하차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT"		, PSlabYdConstant.YD_CAR_PROG_STAT_E );  //하차완료
				jrParam.setField("YD_CARLD_ST_DT"		, ""		);  //상차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARLD_CMPL_DT"		, ""		);  //상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_ST_DT"		, ""		);  //하차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT"		, currDate	);  //하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID"		, sYdCarSchId); 
				
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updMvCarSchCmpl", logId, methodNm, "이송차량스케줄 하차완료로 수정");
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate    	); //JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD"        , sTrnEqpCd	); //운송장비코드
				jrYdMsg.setField("ARR_WLOC_CD"       , sArrWlocCd	); //착지개소코드
				jrYdMsg.setField("ARR_YD_PNT_CD"     , sYdPntCd3	); //착지야드포인트코드
				jrYdMsg.setField("TRN_WRK_ST_DT"     , currDate    	); //운송작업시작일시
				
				//전송할 전문에 추가
				jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);
				
				
			} else if("YDTSJ011".equals(sJmsTcCd)) { //소재차량Point지시

				//야드적치열구분으로 차량포인트 정보 조회
				jrParam.setField("YD_STK_COL_GP", sToLoc);
				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdPntByStkColGp
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
				JDTORecordSet jsCol = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdPntByStkColGp", logId, methodNm, "야드적치열구분으로 차량포인트 정보 조회");
				
				if(jsCol != null && jsCol.size() > 0) {
					sWlocCd		= slabUtils.trim(jsCol.getRecord(0).getFieldString("WLOC_CD"));
					sYdPntCd		= slabUtils.trim(jsCol.getRecord(0).getFieldString("YD_PNT_CD"));
					String sCAR_NO 	= slabUtils.trim(jsCol.getRecord(0).getFieldString("CAR_NO"));
					
					if("".equals(sWlocCd) || "".equals(sYdPntCd)) {
						
						throw new Exception(sToLoc + " 의 개소코드 또는 야드포인트에 NULL 값이 있습니다.");
					}
					
					if(!"".equals(slabUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")))) {
						
						throw new Exception(sToLoc + " 에 이미 " + slabUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")) + " 운송장비가 점유하고  있습니다.");
					}
					
					if(!"".equals(sCAR_NO)) {
						throw new Exception(sToLoc + " 에 이미 " + slabUtils.trim(jsCol.getRecord(0).getFieldString("CAR_NO")) + " 차량이 점유하고  있습니다.");
					}
					
				} else {
					
					throw new Exception(sToLoc + " 의 개소코드"+sWlocCd+"와 야드포인트"+sYdPntCd+"를 TB_YD_CARPOINT 에서 찾지 못했습니다.");
					
				}
				
				jrYdMsg.setField("JMS_TC_CD"         	, sJmsTcCd	); //"YDTSJ011"
				jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, currDate  	); //JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD"        	, sTrnEqpCd	); //운송장비코드
				jrYdMsg.setField("WLOC_CD"     	 		, sWlocCd		);
				jrYdMsg.setField("YD_PNT_CD"     		, sYdPntCd	); 
				jrYdMsg.setField("PNT_WO_GP"     		, "A"    		);
				jrYdMsg.setField("PNT_WO_DT"     		, currDate 		); 
				
				//전송할 전문에 추가
				jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);
				

				//차량스케줄의 개소코드, 야드포인트, 정지위치를 UDPATE 한다.
				if("E".equals(sTrnWrkFullvoidGp)) { 	//공차:상차
					sSposWlocCd 		= sWlocCd;
					sYdPntCd1			= sYdPntCd;	
					sYdCarldStopLoc 	= sToLoc;
					sYdCarldPntWoDt 	= currDate;
				} else { 								//영차:하차
					sArrWlocCd 			= sWlocCd;
					sYdPntCd3			= sYdPntCd;	
					sYdCarudStopLoc 	= sToLoc;
					sYdCarudPntWoDt     = currDate;
				}
				
				//이송차량스케줄 수정 
				jrParam.setField("YD_CAR_PROG_STAT"		, "");  //""이면 이전 상태 유지된다.
				jrParam.setField("SPOS_WLOC_CD"			, sSposWlocCd);
				jrParam.setField("YD_CARLD_PNT_WO_DT"	, sYdCarldPntWoDt);
				jrParam.setField("YD_PNT_CD1"			, sYdPntCd1);
				jrParam.setField("YD_CARLD_STOP_LOC"	, sYdCarldStopLoc); 
				jrParam.setField("ARR_WLOC_CD"			, sArrWlocCd);
				jrParam.setField("YD_CARUD_PNT_WO_DT"	, sYdCarudPntWoDt);
				jrParam.setField("YD_PNT_CD3"			, sYdPntCd3);
				jrParam.setField("YD_CARUD_STOP_LOC"	, sYdCarudStopLoc); 
				jrParam.setField("YD_CAR_SCH_ID"		, sYdCarSchId); 
				/* 이송차량스케줄 상하차 포인트지시 수정 -- com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updMvCarSchPntWo 
					UPDATE USRYDA.TB_YD_CARSCH
					SET    MOD_DDTT           = SYSDATE
					      ,MODIFIER           = :V_MODIFIER
					      ,YD_CAR_PROG_STAT   = NVL(:V_YD_CAR_PROG_STAT ,YD_CAR_PROG_STAT)
					      ,SPOS_WLOC_CD       = NVL(:V_SPOS_WLOC_CD     ,SPOS_WLOC_CD)
					      ,YD_CARLD_PNT_WO_DT = DECODE(NVL(:V_YD_CARLD_PNT_WO_DT,'NULL'),'NULL',YD_CARLD_PNT_WO_DT,SYSDATE)
					      ,YD_PNT_CD1         = NVL(:V_YD_PNT_CD1       ,YD_PNT_CD1)
					      ,YD_CARLD_STOP_LOC  = NVL(:V_YD_CARLD_STOP_LOC,YD_CARLD_STOP_LOC)
					      ,ARR_WLOC_CD        = NVL(:V_ARR_WLOC_CD      ,ARR_WLOC_CD)
					      ,YD_CARUD_PNT_WO_DT = DECODE(NVL(:V_YD_CARUD_PNT_WO_DT,'NULL'),'NULL',YD_CARUD_PNT_WO_DT,SYSDATE)
					      ,YD_PNT_CD3         = NVL(:V_YD_PNT_CD3       ,YD_PNT_CD3)
					      ,YD_CARUD_STOP_LOC  = NVL(:V_YD_CARUD_STOP_LOC,YD_CARUD_STOP_LOC)
					WHERE  YD_CAR_SCH_ID      = :V_YD_CAR_SCH_ID   
				*/			
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updMvCarSchPntWo", logId, methodNm, "이송차량스케줄 상하차 포인트지시 수정");
				
				//TB_YD_STKCOL 예약정보등록 
				jrParam.setField("YD_STK_COL_ACT_STAT"	, "L"); 
				jrParam.setField("CARD_NO"				, sTrnEqpCd);
				jrParam.setField("YD_STK_COL_GP"		, sToLoc);
				/*     
					UPDATE TB_YD_STKCOL
					   SET YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT --적재 상태
					      ,CARD_NO             = :V_CARD_NO             --차량 CARD 번호
					      ,MODIFIER            = :V_MODIFIER    
					      ,MOD_DDTT            = SYSDATE
					 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
				 */ 
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updateEquipcolStat", logId, methodNm, "TB_YD_STKCOL 예약정보등록");
				 
				//차량포인트 통합관리
				this.YdCarPointinforeg(	 "3"				//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
										,""					,sTrnEqpCd						,sToLoc
										,"",""				,"R"							,logId, modifier);
			}	

			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "정상처리되었습니다.");
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}

	} // end of trtMvCarStatSet2	
	
	

	/**
	 * 이송작업재료등록
	 * 야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm = "이송작업재료등록[PSlabYdMvCarSeEJB.updCarFtMvMtl] < " + gdReq.getNavigateValue();
		String logId    = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));
			
			String sModifier       		= slabUtils.trim(gdReq.getParam("userid" )); 			//수정자(MODIFIER)
			JDTORecord jrRtn       		= slabUtils.getParam(logId, methodNm, sModifier);		//Return Value

			String sSlabNo 				= ""; 
			String sStockMv				= "";
			String queryId				= "";

			//등록 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {

				sSlabNo = slabUtils.getValue(gdReq, "STL_NO", ii);
				slabUtils.printLog(logId, "STL_NO["+ ii+"]:", "SL");
				
				/**********************************************************
				* 2. VW_YD_SLABCOMM 에서  STL_NO로 필요정보 조회
				**********************************************************/
				jrParam.setField("SLAB_NO"	, sSlabNo);
				
				/* 
					SELECT A.SLAB_NO
					      ,A.PLAN_SLAB_NO 		--예정Slab번호
					      ,A.CURR_PROG_CD 		--진도코드   
					      ,A.WO_MSLAB_RPR_MTD 	--Scarfing Pattern
					  FROM VW_YD_SLABCOMM A
					 WHERE SLAB_NO = :V_SLAB_NO
				 */
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getInitSlabInfo";
				JDTORecordSet rsResult = commDao.select(jrParam, queryId, logId, methodNm, "VW_YD_SLABCOMM 에서  STL_NO로 필요정보 조회");
				
				if(rsResult.size() > 0) {
				//	String sCURR_PROG_CD 		= slabUtils.trim(rsResult.getRecord(0).getFieldString("CURR_PROG_CD"));     //현재진도코드
				//	String sWO_MSLAB_RPR_MTD	= slabUtils.trim(rsResult.getRecord(0).getFieldString("WO_MSLAB_RPR_MTD")); //Scarfing Pattern
				}
			 
				
				//TB_YD_STOCK 에 존재 하는지 확인
				jrParam.setField("STL_NO"	, sSlabNo);
				/*
					SELECT  A.STL_NO
					      , B.ORD_HCR_GP
					      , B.ORD_YEOJAE_GP
					      , (
					            SELECT  C.YD_WBOOK_ID
					              FROM  TB_YD_WRKBOOK C
					                  , TB_YD_WRKBOOKMTL D
					             WHERE  D.STL_NO = A.STL_NO
					               AND  D.YD_WBOOK_ID = C.YD_WBOOK_ID
					               AND  D.DEL_YN = 'N'
					               AND  C.DEL_YN = 'N'
					        ) AS YD_WBOOK_ID
					  FROM  TB_YD_STOCK A
					      , VW_YD_SLABCOMM B
					 WHERE  A.STL_NO = :V_STL_NO
					   AND  A.STL_NO = B.SLAB_NO(+)
				 */
				rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getStockInfoWcrGp", logId, methodNm, "TB_YD_STOCK에 존재하는지 체크");
				
				if(rsResult.size()<=0) {
					
					/**********************************************************
					* 1-1-2-1. TB_YD_STOCK에  STOCK_ID를 신규생성
					**********************************************************/
					jrParam.setField("STOCK_ID"			, sSlabNo );
					jrParam.setField("STOCK_MOVE_TERM"	, sStockMv  ); //저장품 이동 조건
					jrParam.setField("YD_AIM_RT_GP"     , "" );
					
					/*
					INSERT INTO TB_YD_STOCK (
					  STL_NO
					 ,REGISTER
					 ,REG_DDTT
					 ,MODIFIER
					 ,MOD_DDTT
					 ,DEL_YN
					) VALUES (
					  :V_STL_NO
					 ,:V_MODIFIER
					 ,SYSDATE
					 ,:V_MODIFIER
					 ,SYSDATE
					 ,'N'
					)
					 */
					commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.insStockInfo", logId, methodNm, "TB_YD_STOCK에 STOCK_ID를 신규생성");
					
					/**********************************************************
					* 1-1-2-2. 저장품제원정보(YDY3L002)  L2 송신
					**********************************************************/
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("MSG_ID"			, "YDY3L002");
					jrParam.setField("YD_INFO_SYNC_CD"	, "4"		 ); //야드정보동기화코드(A:생산실적)
					jrParam.setField("MSG_GP"			, "I"		 ); //정보구분(I:신규)

					jrParam.setField("STL_NO"			, sSlabNo ); //재료번호(SLAB번호)
					jrParam.setField("YD_STK_COL_GP"  	, "01");
					jrParam.setField("YD_STK_BED_NO"  	, "01");
					
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L002", jrParam));
					
				} else {
					/**********************************************************
					* 1-1-2-3. TB_YD_STOCK 에 STL_NO에 저장품 이동 조건 갱신
					**********************************************************/
					sSlabNo = slabUtils.trim(rsResult.getRecord(0).getFieldString("STL_NO")); //  
					jrParam.setField("STL_NO"			, sSlabNo );
					jrParam.setField("STOCK_MOVE_TERM"	, sStockMv  ); //저장품 이동 조건 
					/*  
					UPDATE TB_YD_STOCK 
					   SET 
					       YD_AIM_RT_GP    = NVL(:V_YD_AIM_RT_GP,'YG')   --야드목표행선구분
					      ,MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					 WHERE STL_NO   = :V_STL_NO
					*/					
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updStockMoveTerm", logId, methodNm, "TB_YD_STOCK의 STL_NO에 저장품 이동 조건 갱신");
				}
				
				//이송작업재료삭제
				jrParam.setField("YD_CAR_SCH_ID"	, slabUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("STL_NO"			, slabUtils.getValue(gdReq, "OLD_SSTL_NO", ii)); 
				//commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.delCarFtMvMtl", logId, methodNm, "이송작업재료삭제");
				
				slabUtils.printLog(logId, "BED"          +slabUtils.getValue(gdReq, "STACK_BED_GP", ii), "SL"); //임시값
				slabUtils.printLog(logId, "YD_STK_BED_NO"+slabUtils.getValue(gdReq, "YD_STK_BED_NO", ii), "SL");
				slabUtils.printLog(logId, "YD_STK_LYR_NO"+slabUtils.getValue(gdReq, "YD_STK_LYR_NO", ii), "SL");
				//이송작업재료등록
				jrParam.setField("STL_NO"			, sSlabNo); 
				jrParam.setField("YD_CAR_SCH_ID"	, slabUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("YD_STK_BED_NO"	, slabUtils.getValue(gdReq, "STACK_BED_GP", ii)); 
				jrParam.setField("YD_STK_LYR_NO"	, slabUtils.getValue(gdReq, "YD_STK_LYR_NO",ii)); 
				jrParam.setField("MODIFIER"	        , slabUtils.trim(gdReq.getParam("userid"))); 
				//jrParam.setField("YS_STK_SEQ_NO"	, commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii)); 
				
				slabUtils.printLog(logId, "--merge--", "SL");
				/* 
					MERGE INTO TB_YD_CARFTMVMTL TM USING (
					    SELECT STK.STL_NO
					          ,SLAB.HCR_GP                               AS HCR_GP
					          ,SLAB.RECORD_PROG_STAT                     AS STL_PROG_CD
					          ,NVL(:V_YD_STK_BED_NO '01'              )  AS YD_STK_BED_NO
					          ,NVL(:V_YD_STK_LYR_NO ,LAY.YD_STK_LYR_NO)  AS YD_STK_LYR_NO
					          ,:V_YD_CAR_SCH_ID                          AS YD_CAR_SCH_ID
					          ,:V_MODIFIER                               AS MODIFIER
					          ,SYSDATE                                   AS MOD_DDTT
					          ,'N'                                       AS DEL_YN
					    FROM   TB_YD_STOCK STK
					          ,(SELECT  STL_NO
					                   ,YD_STK_BED_NO
					                   ,YD_STK_LYR_NO 
					              FROM  USRYDA.TB_YD_STKLYR
					              WHERE STL_NO = :V_STL_NO
					                AND YD_STK_LYR_ACT_STAT IN ('C','U','E') ) LAY --(권상대기 적치중 STACK_LAYER_STAT)
					          ,VW_YD_SLABCOMM   SLAB    
					    WHERE  STK.STL_NO = :V_STL_NO
					      AND  STK.STL_NO = SLAB.SLAB_NO 
					      AND  STK.STL_NO = LAY.STL_NO(+)
					 
					) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.STL_NO = DD.STL_NO )    
					WHEN NOT MATCHED THEN
					INSERT (TM.YD_CAR_SCH_ID, TM.STL_NO
					      , TM.REGISTER     , TM.REG_DDTT       ,TM.MODIFIER, TM.MOD_DDTT, TM.DEL_YN
					      , TM.YD_STK_BED_NO, TM.YD_STK_LYR_NO  , TM.HCR_GP , TM.STL_PROG_CD)
					        
					VALUES (DD.YD_CAR_SCH_ID, DD.STL_NO
					      , DD.MODIFIER     , DD.MOD_DDTT     , DD.MODIFIER , DD.MOD_DDTT, DD.DEL_YN
					      , DD.YD_STK_BED_NO, DD.YD_STK_LYR_NO, DD.HCR_GP   , DD.STL_PROG_CD)
					WHEN MATCHED THEN
					UPDATE SET
					    TM.MODIFIER      = DD.MODIFIER
					   ,TM.MOD_DDTT      = DD.MOD_DDTT
					   ,TM.DEL_YN        = DD.DEL_YN
					   ,TM.YD_STK_BED_NO = DD.YD_STK_BED_NO
					   ,TM.YD_STK_LYR_NO = DD.YD_STK_LYR_NO
					   ,TM.HCR_GP        = DD.HCR_GP
					   ,TM.STL_PROG_CD   = DD.STL_PROG_CD
				*/
				commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updCarFtMvMtl", logId, methodNm, "이송작업재료등록");

				//--이송작업재료 위치변경--
				jrParam.setField("YD_CAR_SCH_ID"	, slabUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("STL_NO"			, sSlabNo 										); 
				jrParam.setField("YD_STK_BED_NO"	, slabUtils.getValue(gdReq, "YD_STK_BED_NO", ii));
				jrParam.setField("YD_STK_LYR_NO"	, slabUtils.getValue(gdReq, "YD_STK_LYR_NO", ii));
				/* 
				 UPDATE  TB_YD_CARFTMVMTL
				 SET YD_STK_BED_NO   = :V_YD_STK_BED_NO
				   , YD_STK_LYR_NO   = :V_YD_STK_LYR_NO
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				   AND STL_NO        = :V_STL_NO			
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updCarFtMvMtlBed", logId, methodNm, "이송작업재료위치변경");
			}
			

			slabUtils.printLog(logId, ""+ slabUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")) , "SL");
			//차량 작업 상태,매수,작업완료시간 update
			jrParam.setField("YD_CAR_SCH_ID"	, slabUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
			
			/*
			UPDATE USRYDA.TB_YD_CARSCH TS
			   SET TS.MODIFIER             = :V_MODIFIER
			     , TS.MOD_DDTT             = SYSDATE
			     , TS.YD_EQP_WRK_STAT      = DECODE((SELECT COUNT(*) 
			                                    FROM TB_YD_CARFTMVMTL 
			                                   WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID),0,'U','L') --'L' : 영차 ,'U' : 공차
			     , TS.YD_EQP_WRK_SH        = (SELECT COUNT(*) 
			                                    FROM TB_YD_CARFTMVMTL 
			                                   WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID)
			     , TS.YD_EQP_WRK_WT        = (SELECT SUM(SLAB_WT) 
			                                    FROM TB_YD_CARFTMVMTL A
			                                       , VW_YD_SLABCOMM   B
			                                   WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			                                     AND A.STL_NO        = B.SLAB_NO
			                                   )
			     , TS.YD_PNT_CD3           = NVL(YD_PNT_CD3,'0000')
			WHERE TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID 
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updCarSchWrkStSlab", logId, methodNm, "이송차량스케줄 차량작업상태 수정");
			
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "정상처리되었습니다.");
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updCarFtMvMtl
	

	/**
	 * 이송작업재료위치변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord chgCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm  = "이송작업재료위치변경[PSlabYdMvCarSeEJB.chgCarFtMvMtl] < " + gdReq.getNavigateValue();
		String logId     = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			//등록 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				//이송작업재료삭제
				jrParam.setField("YD_CAR_SCH_ID"	, slabUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("STL_NO"			, slabUtils.getValue(gdReq, "STL_NO", ii)); 
				/* 이송작업재료삭제 -- com.inisteel.cim.ym.bcoil.dao.BCoilDAO.delCarFtMvMtl 

				UPDATE USRYDA.TB_YD_CARFTMVMTL
				   SET DEL_YN = 'Y'
				   ,MODIFIER  =  :V_MODIFIER 
				   ,MOD_DDTT =  SYSDATE
				WHERE  YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				    AND   STL_NO = :V_STL_NO   
				*/
				commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.delCarFtMvMtl", logId, methodNm, "이송작업재료위치변경");
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				//이송작업재료등록
				slabUtils.printLog(logId, " STACK_BED_GP:"  +  slabUtils.getValue(gdReq, "STACK_BED_GP", ii)
						               +  " STACK_LAYER_GP:" +  slabUtils.getValue(gdReq, "YD_STK_LYR_NO",ii)      , "SL");
				
				jrParam.setField("YD_CAR_SCH_ID"	, slabUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("STL_NO"			, slabUtils.getValue(gdReq, "OLD_SSTL_NO", ii)); 
				jrParam.setField("STACK_BED_GP"  	, slabUtils.getValue(gdReq, "STACK_BED_GP", ii));   //
				jrParam.setField("STACK_LAYER_GP"	, slabUtils.getValue(gdReq, "YD_STK_LYR_NO",ii)); 
				jrParam.setField("MODIFIER"	        , slabUtils.trim(gdReq.getParam("userid"))); 
				//jrParam.setField("YS_STK_SEQ_NO"	, slabUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii)); 
				//jrParam.setField("YS_STK_SEQ_NO"	, slabUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii));
				
				/* 이송작업재료등록 -- com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCarFtMvMtl 
				MERGE INTO TB_YD_CARFTMVMTL TM USING (
				    SELECT STK.STL_NO  STOCK_ID
				          ,SLAB.HCR_GP
				          ,SLAB.RECORD_PROG_STAT AS STL_PROG_CD
				          ,NVL(:V_STACK_BED_GP  ,LAY.STACK_BED_GP)   AS STACK_BED_GP
				          ,NVL(:V_STACK_LAYER_GP,LAY.STACK_LAYER_GP) AS STACK_LAYER_GP
				          ,:V_YD_CAR_SCH_ID AS YD_CAR_SCH_ID
				          ,:V_MODIFIER AS MODIFIER
				          ,SYSDATE AS MOD_DDTT
				          ,LAY.STACK_LAYER_GP 
				          ,LAY.STACK_BED_GP
				          ,'N' AS DEL_YN
				    FROM   TB_YD_STOCK STK
				          ,(SELECT  STL_NO STOCK_ID
				                   ,YD_STK_BED_NO STACK_BED_GP
				                   ,YD_STK_LYR_NO STACK_LAYER_GP 
				              FROM  USRYDA.TB_YD_STKLYR
				              WHERE STL_NO = :V_STL_NO
				                AND YD_STK_LYR_MTL_STAT IN ('C','U') ) LAY  --권상대기 적치중
				          ,VW_YD_SLABCOMM   SLAB    
				    WHERE  STK.STL_NO = :V_STL_NO
				      AND  STK.STL_NO = SLAB.SLAB_NO (+)
				      AND  STK.STL_NO = LAY.STOCK_ID(+)
				 
				) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.STL_NO = DD.STOCK_ID )    
				WHEN NOT MATCHED THEN
				INSERT (TM.YD_CAR_SCH_ID, TM.STL_NO   , TM.REGISTER , TM.REG_DDTT,
				        TM.MODIFIER     , TM.MOD_DDTT , TM.DEL_YN   , TM.YD_STK_BED_NO,
				        TM.YD_STK_LYR_NO, TM.HCR_GP   , TM.STL_PROG_CD)
				VALUES (DD.YD_CAR_SCH_ID, DD.STOCK_ID , DD.MODIFIER , DD.MOD_DDTT,
				        DD.MODIFIER, DD.MOD_DDTT, DD.DEL_YN, DD.STACK_BED_GP,
				        DD.STACK_LAYER_GP, DD.HCR_GP, DD.STL_PROG_CD)
				WHEN MATCHED THEN
				UPDATE SET
				    TM.MODIFIER      = DD.MODIFIER
				   ,TM.MOD_DDTT      = DD.MOD_DDTT
				   ,TM.DEL_YN        = DD.DEL_YN
				   ,TM.YD_STK_BED_NO = DD.STACK_BED_GP
				   ,TM.YD_STK_LYR_NO = DD.STACK_LAYER_GP
				   ,TM.HCR_GP        = DD.HCR_GP
				   ,TM.STL_PROG_CD   = DD.STL_PROG_CD
				   */
				String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updCarFtMvMtl";
				commDao.insert(jrParam, queryId, logId, methodNm, "이송작업재료등록");
			}

			
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "정상처리되었습니다.");
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of chgCarFtMvMtl	
	
	
	
	

	/**  
	 * 
	 * [A] 오퍼레이션명 : 예약작업 생성 재처리 								(-차량 도착 상태에서 예약작업이 삭제 된 경우 재작업 처리를 할수 있음.)   
	 * 야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord uptReWbook(GridData gdReq)throws DAOException  {
		String methodNm = "예약작업재처리[PSlabYdMvCarSeEJB.uptReWbook] < "+ gdReq.getNavigateValue();
		String logId    = gdReq.getIPAddress();
	    String sMsg    	= "";
		
	    try{
			slabUtils.printLog(logId, methodNm, "S+");
	    	//변수 확인
			String sTrnWrkFullvoidGp= "F"; //운송작업_영공구분
			
			String ydCarStopLoc     = "";      //차량Point
			String sTrnEqpCd      	= ""; 		//운송장비코드
			String sArrWlocCd      	= ""; 		//착지개소코드
			String modifier 	    = "";      //수정자(Backup Only)
			String sSposWlocCd      = "";

			String ydGp	       		= "";
			String ydBayGp     		= "";
			String ydCarSchId  		= "";
			String ydSchCd	   		= "";
			String ydWbookId   		= "";
			String ydWbookId1  		= ""; /*첫번째 작업예약*/  
			String ydWrkCrn      	= "";
			String ydSchPrior    	= "";
			String ydMultiWrkYn 	= "";
			String ydStkBedNo 		= "";
			//String ydStkColGp  	= "";
			//String sFrtomoveWordNo= ""; 	//이송작업지시 번호
			//String ydEqpWrkStat 	= "";
			String 		ydFrmYn  	= "N";
			modifier = slabUtils.trim(gdReq.getParam("userid" )); 	
			
			JDTORecord jrRtn  		= slabUtils.getParam(logId, methodNm, modifier);
			JDTORecord jrParam		= slabUtils.getParam(logId, methodNm, modifier);
			JDTORecord  recInputPara= slabUtils.getParam(logId, methodNm, modifier); 
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			slabUtils.printLog(logId, "작업대상건수:"	+ rowCnt			, "SL");
			
			for(int i = 0; i < rowCnt; i++ ) {
				
				ydCarStopLoc   	 = slabUtils.trim(slabUtils.getValue(gdReq, "YD_CAR_STOP_LOC", i));
				sTrnEqpCd      	 = slabUtils.trim(slabUtils.getValue(gdReq, "TRN_EQP_CD"     , i));
				sArrWlocCd       = slabUtils.trim(slabUtils.getValue(gdReq, "ARR_WLOC_CD"    , i));
				sSposWlocCd    	 = slabUtils.trim(slabUtils.getValue(gdReq, "SPOS_WLOC_CD"   , i));
				modifier 	     = slabUtils.trim(slabUtils.getValue(gdReq, "YD_USER_ID"     , i));
				

				slabUtils.printLog(logId, "운송장비코드:"	+ sTrnEqpCd	  + "운송작업영공구분:"	    + sTrnWrkFullvoidGp   	
									   +  "착지개소코드:"	+ sArrWlocCd  + "<--" + sSposWlocCd	+ " 차량정지Point"  + ydCarStopLoc, "SL");
				//개소코드가 후판Slab 개소코드인지 검사한다.
				if (!getSlabLocationInfo_02(sArrWlocCd)) {
					sMsg = "개소코드 오류 [" + sArrWlocCd + "]는 후판Slab 개소코드가 아닙니다!";
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sMsg);
					return 	jrRtn;
				}			
				


				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdPntByStkColGp ( 형상 적용 여부가 "N"이라면 처리 안함.)
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
				jrParam.setField("YD_STK_COL_GP", ydCarStopLoc); 
				String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdPntByStkColGp";
				JDTORecordSet jsYdRule = commDao.select(jrParam, queryId, logId, methodNm, "야드적치열구분으로 차량포인트 정보 조회");
				
				if (jsYdRule.size() > 0) {
					ydFrmYn = slabUtils.nvl(jsYdRule.getRecord(0).getFieldString("YD_FRM_YN"),"N");
				} else if (jsYdRule.size() <= 0) {
					sMsg = "착지개소코드 [" + sArrWlocCd + "], 착지야드포인트코드 [" + ydCarStopLoc + "] 는 차량포인트에 없는 위치입니다.";
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sMsg);
					return 	jrRtn;
				}
				

                /* 예약 재처리 - com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJBBean.get2BedWbookid   
				SELECT B.YD_WBOOK_ID 
				FROM TB_YD_WRKBOOK    A
				   , TB_YD_WRKBOOKMTL B
				 WHERE A.DEL_YN = 'N'
				   AND A.YD_WBOOK_ID = B.YD_WBOOK_ID
				   AND A.TRN_EQP_CD  = :V_TRN_EQP_CD       
				   AND B.YD_STK_BED_NO LIKE :V_YD_STK_BED_NO ||'%'
				 */
				recInputPara.setField("TRN_EQP_CD"        , sTrnEqpCd 	 ); 
				recInputPara.setField("YD_STK_BED_NO"     , ""); 
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJBBean.get2BedWbookid";
				JDTORecordSet recordSet3 = commDao.select(recInputPara, queryId, logId, methodNm, "작업예약id");
				if(recordSet3.size() > 0) {
					sMsg = "작업예약건이 존재 합니다.  취소 후 재처리 하세요! ("+ sTrnEqpCd+")";
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sMsg);
					return 	jrRtn;
				}
				
				sMsg="*작업대상: 형상 사용 안하는 경우 ('N')에 하차 도착시 ---->"+"차량형상인식 사용여부("+ ydFrmYn+")";
				slabUtils.printLog(logId, sMsg, "SL");
				//--------차량 형상을 사전에 받은 경우 ---------------------------------------------
				if ("Y".equals(ydFrmYn)) {  //형상 수신 여부 
					/* com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdRuleNvl2 
					SELECT DECODE(DTL_ITEM5 ,'Y', 'Y','N') AS FRM_YN 
					FROM TB_YD_RULE 
					WHERE  REPR_CD_GP = 'DYD006'
					AND ITEM = 'D' AND CD_GP = :V_CD_GP
					 */
					jrParam.setField("CD_GP"		, ydCarStopLoc);
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdRuleNvl2";
					JDTORecordSet jsFrmAcc =commDao.select(jrParam, queryId, logId, methodNm, "형상수신확인");
					if(jsFrmAcc.size() > 0) {
						if("Y".equals( jsFrmAcc.getRecord(0).getFieldString("FRM_YN"))) {
							ydFrmYn = "N";
						}
					}
				}
				if ("Y".equals(ydFrmYn)) {
					sMsg = "형상 사용 안 하는 경우만 재처리 가능 합니다(또는 사전에 형상 수신된 경우) ";
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sMsg);
					return 	jrRtn;
				}
				
				//스케줄코드 생성  - 이송하차(L)
				ydSchCd = ydCarStopLoc+"LM";
				
				slabUtils.printLog(logId, "YD_SCH_CD:"+ydSchCd, "SL");
				//스케줄코드로 스케줄기준Table조회
				jrParam.setField("YD_SCH_CD", ydSchCd);
				/*
				SELECT A.*
				     , CASE WHEN WRK_EQUIP_STAT != 'B' THEN YD_WRK_CRN_PRIOR
				            WHEN WRK_EQUIP_STAT  = 'B' AND YD_ALT_CRN_YN = 'Y' THEN CASE WHEN ALT_EQUIP_STAT != 'B' THEN YD_ALT_CRN_PRIOR ELSE 99 END
				            ELSE 99
				        END AS YD_SCH_PRIOR
				     , CASE WHEN WRK_EQUIP_STAT != 'B' THEN YD_WRK_CRN
				            WHEN WRK_EQUIP_STAT  = 'B' AND YD_ALT_CRN_YN = 'Y' THEN CASE WHEN ALT_EQUIP_STAT != 'B' THEN YD_ALT_CRN ELSE '99' END
				            ELSE '99'
				        END AS YD_SCH_CRN
				  FROM (
				        SELECT YD_SCH_CD
				             , YD_GP
				             , YD_BAY_GP
				             , YD_SCH_RNG_CD
				             , YD_SCH_WHIO_GP
				             , YD_SCH_DIV_GP
				             , YD_SCH_RULE_ACT_STAT
				             , YD_WRK_CRN
				             , YD_WRK_CRN_PRIOR
				             , YD_ALT_CRN_YN
				             , YD_ALT_CRN
				             , YD_ALT_CRN_PRIOR
				             , CD_CONTENTS       
				             , YD_SCH_PROH_EXN   
				             , (SELECT YD_EQP_STAT FROM TB_YD_EQP WHERE YD_EQP_ID = SR.YD_WRK_CRN) AS WRK_EQUIP_STAT
				             , (SELECT YD_EQP_STAT FROM TB_YD_EQP WHERE YD_EQP_ID = SR.YD_ALT_CRN) AS ALT_EQUIP_STAT
				          FROM TB_YD_SCHRULE   SR
				         WHERE YD_SCH_CD = :V_YD_SCH_CD
				       ) A
				 WHERE 1 = 1
				 */
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getYdSchrule";
				JDTORecordSet jsSchRule = commDao.select(jrParam, queryId, logId, methodNm, "스케줄 기준 조회");
				
				if (jsSchRule != null && jsSchRule.size() > 0) {
					ydWrkCrn      	= jsSchRule.getRecord(0).getFieldString("YD_SCH_CRN"); 			//야드작업크레인
					ydSchPrior    	= jsSchRule.getRecord(0).getFieldString("YD_SCH_PRIOR"); 	//야드스케쥴우선순위
					//ydMultiWrkYn 	= slabUtils.nvl(jsSchRule.getRecord(0).getFieldString("YD_MULTI_WRK_YN"), "N"); 	//야드멀티작업여부
				} else {
					throw new Exception("후판슬라브 스케줄 코드 이상 : [" + ydSchCd + "]");
				}			

				
				//2bed 하차 진입 여부 판정   
				jrParam.setField("TRN_EQP_CD"	, sTrnEqpCd); 
				/* com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarFtmvMtlBed2List 
				SELECT B.YD_CAR_SCH_ID 
				     , B.YD_STK_BED_NO 
				  FROM TB_YD_CARSCH     A
				     , TB_YD_CARFTMVMTL B
				 WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID 
				   AND A.TRN_EQP_CD    = :V_TRN_EQP_CD
				   AND A.DEL_YN = 'N'  
				   AND B.DEL_YN = 'N'
				 GROUP BY B.YD_CAR_SCH_ID,B.YD_STK_BED_NO
				 ORDER BY B.YD_STK_BED_NO DESC
				 */
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCarFtmvMtlBed2List";
				JDTORecordSet jsBed2 = commDao.select(jrParam, queryId, logId, methodNm, "이송재료 2Bed여부 조회");

				for(int kk= 0; kk < jsBed2.size() ; kk++) {
					
					ydStkBedNo = slabUtils.nvl(jsBed2.getRecord(kk).getFieldString("YD_STK_BED_NO"),"01"); 
					
					//운송장비코드로 이송재료 조회 + 작업예약ID
					/*  com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getListFrtostlList  
					SELECT  A.STL_NO
					       ,A.YD_STK_BED_NO
					       ,SUBSTR(A.YD_STK_LYR_NO,2,2) AS STK_LYR
					       ,A.YD_CAR_SCH_ID
					       ,(SELECT CS.YD_CARUD_WRK_BOOK_ID FROM TB_YD_CARSCH CS WHERE CS.YD_CAR_SCH_ID = A.YD_CAR_SCH_ID) AS YD_CARUD_WRK_BOOK_ID
					       ,(SELECT CS.FRTOMOVE_WORD_NO     FROM TB_YD_CARSCH CS WHERE CS.YD_CAR_SCH_ID = A.YD_CAR_SCH_ID) AS FRTOMOVE_WORD_NO
					       
					       , DECODE(A.YD_CAR_UPP_LOC_CD, '', DECODE(B.YD_CARUD_STOP_LOC,'',B.YD_CARLD_STOP_LOC,B.YD_CARUD_STOP_LOC), A.YD_CAR_UPP_LOC_CD)  as YD_STK_COL_GP
					       , A.YD_STK_BED_NO      as YD_STK_BED_NO
					       , A.YD_STK_LYR_NO      as YD_STK_LYR_NO
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
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getListFrtostlList";
					JDTORecordSet jsCarMtl = commDao.select(jrParam, queryId, logId, methodNm, "운송장비코드로 이송재료 조회");
					
					if (jsCarMtl.size() <= 0) {
						sMsg = "영차(F) 도착처리 대상재가 존재 안함";
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", sMsg);
						return 	jrRtn;
					} else {
						ydCarSchId = slabUtils.trim(jsCarMtl.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
						
						slabUtils.printLog(logId, "YD_CAR_SCH_ID----->"+ydCarSchId, "SL");
					}
					//2Bed 대상 만큼 lOOping 
					for(int ii= 0; ii < jsCarMtl.size() ; ii++) {
						/**********************************************************
						 * 영차도착 포인트 적치단(TB_YD_STKLYR)에 SLAB정보 생성하기
						 **********************************************************/
						jrParam.setField("STL_NO"				, slabUtils.trim(jsCarMtl.getRecord(ii).getFieldString("STL_NO")));
						jrParam.setField("YD_STK_COL_GP"		, ydCarStopLoc);
						jrParam.setField("YD_STK_BED_NO"		, slabUtils.trim(jsCarMtl.getRecord(ii).getFieldString("YD_STK_BED_NO")));
						jrParam.setField("YD_STK_LYR_NO"		, slabUtils.trim(jsCarMtl.getRecord(ii).getFieldString("YD_STK_LYR_NO"))); //	//slabUtils.format(ii+1, 3));
						jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"		);
						//YD_STK_LYR_ACT_STAT
						/*
						UPDATE TB_YD_STKLYR
						   SET STL_NO = :V_STL_NO
						      ,YD_STK_LYR_ACT_STAT  = NVL(:V_YD_STK_LYR_ACT_STAT, YD_STK_LYR_ACT_STAT)
						      ,YD_STK_LYR_MTL_STAT  = NVL(:V_YD_STK_LYR_MTL_STAT, YD_STK_LYR_MTL_STAT)
						      ,MODIFIER = :V_MODIFIER
						      ,MOD_DDTT = SYSDATE
						WHERE  YD_STK_COL_GP = :V_YD_STK_COL_GP
						  AND  YD_STK_BED_NO = :V_YD_STK_BED_NO
						  AND  YD_STK_LYR_NO = :V_YD_STK_LYR_NO
  						  
						 */
						queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.setStackLayer";
						commDao.update(jrParam, queryId, logId, methodNm, "영차도착 포인트 적치단(TB_YD_STKLYR)에 SLAB정보 생성하기");

					}
					
					/********************************
					 * 1. 작업예약 생성
					 * 2. TB_YD_STOCK의이송작업지시번호(TRANS_WORD_NO) 등록
				 	/*********************************/
					if ("Y".equals(ydMultiWrkYn)) {
						jrParam.setField("YD_SCH_ST_GP"			, "N"); 		//야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
						
					} else {
						jrParam.setField("YD_SCH_ST_GP"			, "A"); 		//야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
						jrParam.setField("YD_WRK_PLAN_CRN"		, ydWrkCrn); 	//야드작업계획크레인
						jrParam.setField("YD_WRK_PLAN_CRN2"		, ""); 			//야드작업계획크레인2
					} 
					jrParam.setField("YD_SCH_ST_GP"				, "A"); 		//야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
					jrParam.setField("YD_WRK_PLAN_CRN"			, ydWrkCrn); 	//야드작업계획크레인
					jrParam.setField("YD_WRK_PLAN_CRN2"			, ""); 			//야드작업계획크레인2
					
					slabUtils.printLog(logId, "YD_SCH_ST_GP:"+jrParam.getField("YD_SCH_ST_GP"), "SL");
					slabUtils.printLog(logId, "YD_SCH_CD   :"+ydSchCd, "SL");
					
					//ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
					
					jrParam.setField("YD_SCH_CD"        , ydSchCd); 	//야드스케쥴코드
					jrParam.setField("YD_AIM_BAY_GP"    , ydBayGp  ); 	//야드목표동구분 ydAimBayGp
					jrParam.setField("YD_TO_LOC_GUIDE"  , ""); 			//야드To위치Guide 
					jrParam.setField("YD_WRK_PLAN_TCAR" , ""); 			//야드작업계획대차 ydWrkPlanTcar
					jrParam.setField("YD_WRK_PLAN_CRN"  , "");      	//야드작업크레인 ydWrkCrn
					jrParam.setField("YD_SCH_PRIOR"	    , "");      	//야드크레인작업순위 schPrior
					
					//jrParam.setField("YD_WBOOK_ID"		, ydWbookId);
					jrParam.setField("YD_GP"			, ydGp);
					jrParam.setField("YD_SCH_PRIOR"		, ydSchPrior); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT"	, "W"); 		//야드스케쥴진행상태(W:스케줄수행대기)
					jrParam.setField("YD_SCH_REQ_GP"	, "C"); 		//야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
					jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd); 	//운송장비코드
					jrParam.setField("YD_CAR_USE_GP"	, "L"); 		//야드차량사용구분 L:구내운송
					jrParam.setField("YD_STK_COL_GP"   	, ydCarStopLoc);//"DBPT02"
					
					//--작업예약등록--
					EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
					jrParam = (JDTORecord)ejbConn.trx("insMvstkWrkBook"	, new Class[] { JDTORecord.class, JDTORecordSet.class } , new Object[] { jrParam, jsCarMtl });

					String rtnCd	 = slabUtils.nvl(jrParam.getFieldString("RTN_CD"), "0");
					String rtnMsg	 = slabUtils.nvl(jrParam.getFieldString("RTN_MSG"), "");
		
					ydWbookId = slabUtils.trim(jrParam.getFieldString("YD_WBOOK_ID"));
					slabUtils.printLog(logId, "작업예약번호는:"+ydWbookId, "SL");

					slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "SL");
					slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "SL");
					// ROLLBACK 시 전문 발생
					if ("0".equals(rtnCd)) {
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", "작업예약 생성 오류"+ydSchCd);
						return jrRtn;
					}

					if ( kk == 0) {
						ydWbookId1 = ydWbookId;
					}
				}

				ydWbookId = slabUtils.nvl(jrParam.getFieldString("YD_WBOOK_ID"), "0");
				slabUtils.printLog(logId, "작업예약번호는:"+ydWbookId, "SL");
				/* com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updateArrDt51  
				UPDATE TB_YD_CARSCH
				  SET  MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,YD_CARUD_WRK_BOOK_ID= DECODE(:V_YD_CARUD_WRK_BOOK_ID, '', YD_CARUD_WRK_BOOK_ID, :V_YD_CARUD_WRK_BOOK_ID)
				WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				AND DEL_YN = 'N'
				 */	
				jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, ydWbookId); 	//작업예약번호
				jrParam.setField("TRN_EQP_CD"			, sTrnEqpCd); 	//운송장비코드
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updateArrDt51";
				commDao.update(jrParam, queryId, logId, methodNm, "차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)"); 
			}
			
			//건별로 작업 예약을 만들도록 처리 . 
			if(ydWbookId1.equals(ydWbookId)) ydWbookId = "";
			slabUtils.printLog(logId, "1)예약id-->"+ydWbookId1, "SL");
			slabUtils.printLog(logId, "2)예약id-->"+ydWbookId, "SL"); 
			//하차 (예약재처리)시  2Bed 부터 하차 처리  
			
			//JDTORecord jrCrnSchMsg1 = JDTORecordFactory.getInstance().create();
			JDTORecord jrCrnSchMsg2 = JDTORecordFactory.getInstance().create();
			if(!"".equals(ydWbookId1) ) {
				jrCrnSchMsg2 = JDTORecordFactory.getInstance().create();
				slabUtils.printLog(logId,  "크레인 스케쥴 호출(차상위치 다건) : YDYDJ401- 2BED -->" + ydWbookId1 , "SL");   
				
				//크레인 스케줄 기동  호출
				jrCrnSchMsg2.setField("JMS_TC_CD"			, "YDYDJ401"); 
				jrCrnSchMsg2.setField("JMS_TC_CREATE_DDTT"	, slabUtils.getDateTime14()); //JMSTC생성일시	
				jrCrnSchMsg2.setField("YD_WBOOK_ID"  		, ydWbookId1); //작업예약ID
				jrCrnSchMsg2.setField("YD_SCH_CD"  			, ydSchCd);  //야드스케쥴코드
				jrCrnSchMsg2.setField("YD_EQP_ID"  			, ydWrkCrn);  //야드설비ID (예)DACRA1
				jrCrnSchMsg2.setField("EJB_CALL_YN"			, "Y");	//EJBCall여부(신 크레인스케줄)
				
				jrRtn = slabUtils.addSndData(jrRtn, jrCrnSchMsg2);
			}	
			//---------------------------------------------------------------------
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "작업예약 재처리 성공");
			return jrRtn;
	    	
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	
		
	}
	

	/**  
	 * [A] 오퍼레이션명 : 예약작업 생성 재처리 -차량 도착 상태에서 예약작업이 삭제 된 경우 재작업 처리를 할수 있음.   
	 * 야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord makeReWbook(GridData gdReq)throws DAOException  {
		String methodNm = "건별하차(작업예약생성)[PSlabYdMvCarSeEJB.makeReWbook] < "+ gdReq.getNavigateValue();
		String logId    = gdReq.getIPAddress();
	    String sMsg     = "";
		
	    /*--------------------------------------------------------------------------------------
	     * 선택된 저장품별 작업예약 생성 처리   (하차 대상재에 한정함.)    ※상차는  이송Lot편성시   "동일상차처리"로 진행하고 있음. 
	     * -조회된 하차 대상재 중  건별로 작업 예약을 만들고, 스케줄 기동하여  하차 처리 하도록 처리.   (장점: 자동 스케줄로 생성된 경우 단위 작업 불가함. )
	     *--------------------------------------------------------------------------------------*/
	    try{
			slabUtils.printLog(logId, methodNm, "S+");
			
			String ydCarStopLoc     = "";      //차량Point
			String sTrnEqpCd      	= ""; 		//운송장비코드
			String sArrWlocCd      	= ""; 		//착지개소코드
			String modifier 	    = "";      //수정자(Backup Only)
			String ydGp	       		= "";
			String ydBayGp     		= "";
			String ydCarSchId  		= "";
			String ydSchCd	   		= "";
			String ydWbookId   		= "";
			String ydWrkCrn      	= "";
			String ydSchPrior    	= "";
			String ydMultiWrkYn 	= "";
			String szYdCarSchId  	= "";
			String szYdCarProgStat 	= "";
			String szStlNo 			= "";
			String queryId  		= "";
			sTrnEqpCd= slabUtils.trim(gdReq.getParam("TRN_EQP_CD" )); 	
			modifier = slabUtils.trim(gdReq.getParam("YD_USER_ID" )); 	
			
			JDTORecord jrRtn  		= slabUtils.getParam(logId, methodNm, modifier);
			JDTORecord jrParam		= slabUtils.getParam(logId, methodNm, modifier);
			JDTORecord recPara		= slabUtils.getParam(logId, methodNm, modifier);
			JDTORecord jrCrnSchMsg1 = slabUtils.getParam(logId, methodNm, modifier);
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			slabUtils.printLog(logId, "작업대상건수:"	+ rowCnt			, "SL");
			
			for(int i = 0; i < rowCnt; i++ ) {
				
				sTrnEqpCd   = slabUtils.trim(slabUtils.getValue(gdReq, "TRN_EQP_CD"     , i));
				szStlNo  	= slabUtils.trim(slabUtils.getValue(gdReq, "STL_NO"       , i));	
				szYdCarSchId= slabUtils.trim(slabUtils.getValue(gdReq, "YD_CAR_SCH_ID", i));
				
				ydCarStopLoc= slabUtils.trim(slabUtils.getValue(gdReq, "YD_CAR_STOP_LOC", i));
				sArrWlocCd  = slabUtils.trim(slabUtils.getValue(gdReq, "ARR_WLOC_CD"    , i));
				//sSposWlocCd = slabUtils.trim(slabUtils.getValue(gdReq, "SPOS_WLOC_CD"   , i));

				recPara.setField("YD_CAR_SCH_ID"			, szYdCarSchId);
				recPara.setField("STL_NO"					, szStlNo);
				
				// 해당 대상재가 예약에 등록된 사항이 있으면 ERR
				/*SELECT YD_WBOOK_ID
				  FROM USRYDA.TB_YD_WRKBOOKMTL 
				 WHERE DEL_YN = 'N'
				   AND STL_NO = :V_STL_NO
				*/   
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getYdWrkbookBystlNo";
				JDTORecordSet outRecSet = commDao.select(recPara, queryId, logId, methodNm, "작업예약재료 조회");
				if(outRecSet.size() > 0) {
					sMsg = "작업예약이 기등록 처리건이 있습니다. 해당건 취소후 처리 하세요!";
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sMsg);
					return 	jrRtn;
				}

				recPara.setField("MODIFIER"					, "makeReWbok");
				/*
				 UPDATE  TB_YD_CARFTMVMTL
				 SET MODIFIER = :V_MODIFIER
				   , MOD_DDTT = sysdate
				 WHERE DEL_YN = 'N'
				  AND YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				  AND STL_NO        = :V_STL_NO
				 */
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdCarftmvmtlModifier";
				commDao.update(recPara, queryId, logId, methodNm, "작업예약 대상건 선택");
				
				//--작업진행 상태 확인  
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdCarsch01";
				outRecSet = commDao.select(recPara, queryId, logId, methodNm, "차량스케줄조회");
				if(outRecSet.size() > 0) {
					szYdCarProgStat	= slabUtils.trim(outRecSet.getRecord(0).getFieldString("YD_CAR_PROG_STAT"    ));
					ydCarStopLoc 	= slabUtils.trim(outRecSet.getRecord(0).getFieldString("YD_CARUD_STOP_LOC"    ));
				}
			}
			
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdPntByStkColGp ( 형상 적용 여부가 "N"이라면 처리 안함.)
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
			jrParam.setField("YD_STK_COL_GP", ydCarStopLoc); 
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdPntByStkColGp";
			JDTORecordSet jsYdRule = commDao.select(jrParam, queryId, logId, methodNm, "야드적치열구분으로 차량포인트 정보 조회");
			
			if (jsYdRule.size() > 0) {
				//ydFrmYn = slabUtils.nvl(jsYdRule.getRecord(0).getFieldString("YD_FRM_YN"),"N");
			} else if (jsYdRule.size() <= 0) {
				sMsg = "착지개소코드 [" + sArrWlocCd + "], 착지야드포인트코드 [" + ydCarStopLoc + "] 는 차량포인트에 없는 위치입니다.";
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				return 	jrRtn;
			}
			
			if("A".equals(szYdCarProgStat) || "B".equals(szYdCarProgStat)) { } else{
				sMsg = "후판Slab 차량출/도착 상태에서만 가능한 작업 입니다!";
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				return 	jrRtn;
			}
		
			/* 대상건에 대한 작업 예약 생성  (사전작업:대상재 추출)
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
			 WHERE A.MODIFIER      = :V_MODIFY 
			   AND A.YD_CAR_SCH_ID =  B.YD_CAR_SCH_ID
			   AND A.DEL_YN = 'N'
			   AND B.DEL_YN = 'N' 
			 ORDER BY A.YD_STK_BED_NO,A.YD_STK_LYR_NO  DESC
			 */
			
			//운송장비코드로 이송재료 조회 + 작업예약ID
			//jrParam.setField("TRN_EQP_CD"				, sTrnEqpCd);
			//jrParam.setField("YD_STK_BED_NO"			, ydStkBedNo);
			jrParam.setField("MODIFY"					, "makeReWbok");
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getListReWbook";
			JDTORecordSet jsCarMtl = commDao.select(jrParam, queryId, logId, methodNm, "선택된 이송재료 조회");
			
			if (jsCarMtl.size() <= 0) {
				sMsg = "대상재가 선택처리 되지 않았습니다.";
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				return 	jrRtn;
			} else {
				ydCarSchId = slabUtils.trim(jsCarMtl.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
				
				slabUtils.printLog(logId, "YD_CAR_SCH_ID----->"+ydCarSchId, "SL");
			}
			

			//스케줄코드 생성  - 이송하차(L)
			ydSchCd = ydCarStopLoc+"LM";
			
			slabUtils.printLog(logId, "YD_SCH_CD:"+ydSchCd, "SL");
			//스케줄코드로 스케줄기준Table조회
			jrParam.setField("YD_SCH_CD", ydSchCd);
			/* com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdSchrule 

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
				 WHERE YD_SCH_CD =  :V_YD_SCH_CD 
			 */   
			JDTORecordSet jsSchRule = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
			
			if (jsSchRule != null && jsSchRule.size() > 0) {
				ydWrkCrn      	= jsSchRule.getRecord(0).getFieldString("YD_WRK_CRN"); 			//야드작업크레인
				ydSchPrior    	= jsSchRule.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); 	//야드스케쥴우선순위
				ydMultiWrkYn 	= slabUtils.nvl(jsSchRule.getRecord(0).getFieldString("YD_MULTI_WRK_YN"), "N"); 	//야드멀티작업여부
			} else {
				throw new Exception("후판슬라브 스케줄 코드 이상 : [" + ydSchCd + "]");
			}			
			
			//  작업예약 생성
			if ("Y".equals(ydMultiWrkYn)) {
				jrParam.setField("YD_SCH_ST_GP"			, "N"); 		//야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
				
			} else {
				jrParam.setField("YD_SCH_ST_GP"			, "A"); 		//야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
				jrParam.setField("YD_WRK_PLAN_CRN"		, ydWrkCrn); 	//야드작업계획크레인
				jrParam.setField("YD_WRK_PLAN_CRN2"		, ""); 			//야드작업계획크레인2
			} 
			jrParam.setField("YD_SCH_ST_GP"				, "A"); 		//야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
			jrParam.setField("YD_WRK_PLAN_CRN"			, ydWrkCrn); 	//야드작업계획크레인
			jrParam.setField("YD_WRK_PLAN_CRN2"			, ""); 			//야드작업계획크레인2
			
			slabUtils.printLog(logId, "YD_SCH_ST_GP:"+jrParam.getField("YD_SCH_ST_GP"), "SL");
			
			jrParam.setField("YD_SCH_CD"        , ydSchCd); 	//야드스케쥴코드
			jrParam.setField("YD_AIM_BAY_GP"    , ydBayGp  ); 	//야드목표동구분 ydAimBayGp
			jrParam.setField("YD_TO_LOC_GUIDE"  , ""); 			//야드To위치Guide 
			jrParam.setField("YD_WRK_PLAN_TCAR" , ""); 			//야드작업계획대차 ydWrkPlanTcar
			jrParam.setField("YD_WRK_PLAN_CRN"  , "");      	//야드작업크레인 ydWrkCrn
			jrParam.setField("YD_SCH_PRIOR"	    , "");      	//야드크레인작업순위 schPrior
			
			//jrParam.setField("YD_WBOOK_ID"		, ydWbookId);
			jrParam.setField("YD_GP"			, ydGp);
			jrParam.setField("YD_SCH_PRIOR"		, ydSchPrior); //야드스케쥴우선순위
			jrParam.setField("YD_SCH_PROG_STAT"	, "W"); 		//야드스케쥴진행상태(W:스케줄수행대기)
			jrParam.setField("YD_SCH_REQ_GP"	, "C"); 		//야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
			jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd); 	//운송장비코드
			jrParam.setField("YD_CAR_USE_GP"	, "L"); 		//야드차량사용구분 L:구내운송
			jrParam.setField("YD_STK_COL_GP"   	, ydCarStopLoc);//"DBPT02"
			
			//작업예약등록
			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			jrParam = (JDTORecord)ejbConn.trx("insMvstkWrkBook"	, new Class[] { JDTORecord.class, JDTORecordSet.class } , new Object[] { jrParam, jsCarMtl });

			String rtnCd	 = slabUtils.nvl(jrParam.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrParam.getFieldString("RTN_MSG"), "");

			ydWbookId = slabUtils.trim(jrParam.getFieldString("YD_WBOOK_ID"));
			slabUtils.printLog(logId, "작업예약번호는:"+ydWbookId, "SL");

			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "SL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "SL");
			// ROLLBACK 시 전문 발생
			if ("0".equals(rtnCd)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "작업예약 생성 오류"+ydSchCd);
				return jrRtn;
			}

			ydWbookId = slabUtils.nvl(jrParam.getFieldString("YD_WBOOK_ID"), "0");
			slabUtils.printLog(logId, "작업예약번호는:"+ydWbookId, "SL");
			/* com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updateArrDt51   (야드하차작업예약ID)
			UPDATE TB_YD_CARSCH
			  SET  MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,YD_CARUD_WRK_BOOK_ID= DECODE(:V_YD_CARUD_WRK_BOOK_ID, '', YD_CARUD_WRK_BOOK_ID, :V_YD_CARUD_WRK_BOOK_ID)
			WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			AND DEL_YN = 'N'
			 */	
			jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, ydWbookId); 	//작업예약번호
			jrParam.setField("TRN_EQP_CD"			, sTrnEqpCd); 	//운송장비코드
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updateArrDt51";
			commDao.update(jrParam, queryId, logId, methodNm, "차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)"); 
			
			// 작업예약 생성 건의 크레인 기동  처리  
			if(!"".equals(ydWbookId) ) {
				slabUtils.printLog(logId,  "크레인 스케쥴 호출(차상위치 다건) : YDYDJ401 -->" + ydWbookId , "SL");   
				jrCrnSchMsg1 = slabUtils.getParam(logId, methodNm, modifier);
				slabUtils.printLog(logId,  "크레인 스케쥴 호출 : YDYDJ401"  , "SL");   
				
				//크레인 스케줄 기동  호출
				jrCrnSchMsg1.setField("JMS_TC_CD"			, "YDYDJ401"); 
				jrCrnSchMsg1.setField("JMS_TC_CREATE_DDTT"	, slabUtils.getDateTime14()); //JMSTC생성일시	
				jrCrnSchMsg1.setField("YD_WBOOK_ID"  		, ydWbookId); //작업예약ID
				jrCrnSchMsg1.setField("YD_SCH_CD"  			, ydSchCd);  //야드스케쥴코드
				jrCrnSchMsg1.setField("YD_EQP_ID"  			, ydWrkCrn);  //야드설비ID (예)DACRA1
				jrCrnSchMsg1.setField("EJB_CALL_YN"			, "Y");	//EJBCall여부(신 크레인스케줄)
				
				jrRtn = slabUtils.addSndData(jrRtn, jrCrnSchMsg1);
			}
			
			//--(하차_작업예약 대상건) 작업 선택 사항 초기화 처리 
			
			recPara.setField("YD_CAR_SCH_ID"			, szYdCarSchId);
			recPara.setField("STL_NO"					, szStlNo);
			recPara.setField("MODIFIER"					, modifier);
			recPara.setField("MOFIFY" 					, "makeReWbok");
			/*
			 UPDATE  TB_YD_CARFTMVMTL
			 SET MODIFIER = :V_MODIFIER
			   , MOD_DDTT = sysdate
			 WHERE DEL_YN = 'N'
			   AND MODIFIER = :V_MOFIFY 
			 */
			queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdCarftmvmtlReModify";
			commDao.update(recPara, queryId, logId, methodNm, "작업예약 처리대상건 초기화");
			
			
			//---------------------------------------------------------------------
			slabUtils.printLog(logId, methodNm, "S-");
			sMsg = "재료별 작업에약 생성 처리가 완료 되었습니다.";
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", sMsg);
			return jrRtn;
	    	
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}
	

	/**				
	 * 오퍼레이션명 : (1-1 포인트개폐 처리 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리 
	 * PYS 2020-10-29    
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @throws DAOException
	 */
	public JDTORecord procSlabYdPntUnitCL(GridData inDto) throws DAOException {
		String methodNm = " 포인트개폐 처리[PSlabYdMvCarSeEJB.procSlabYdPntUnitCL] < " + inDto.getNavigateValue();
		String logId    = inDto.getIPAddress();
		String szMsg    = "";
		
		try {
			slabUtils.printLog(logId, methodNm, "S+");
			String rtnEcod						= "0";
			String szOperationName				= "포인트개폐처리";
			String szRtnMsg						= PSlabYdConstant.RETN_CD_SUCCESS;
			szMsg        						= "";
			String szYdStkColGp					= "";
			String szYdStkColActStat			= "";
			String szYdStkColActStatParam		= "";
			String szYdStkbedUsgCdParam			= "";
			String szTrnEqpCd             		= "";
			String szYdCarUseGp          		= "";
			String szCarNo          			= "";
			String szCardNo          			= "";
			String szYdCarpntCd					= "";	// 차량포인트코드
			String szYdFrmYn					= "";	// 야드 형상 구분 Y/N
			String szTcCode						= "";
			
			int     intRtnVal    				= 0;
			boolean isSendable					= true;
			
			String sModifier     = slabUtils.trim(inDto.getParam("userid" )); 			//수정자(MODIFIER)
			JDTORecord jrRtn     = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
			JDTORecord recPara   = slabUtils.getParam(logId, methodNm, sModifier);
			JDTORecord recTemp   = slabUtils.getParam(logId, methodNm, sModifier);
	
			int rowCnt = inDto.getHeader("CHECK").getRowCount(); //단건
			
			szMsg = "["+szOperationName+"] --------------- 메소드 시작 - 적치열건수["+rowCnt+"] ---------------";
			slabUtils.printLog(logId, szMsg, "SL");
			
			for(int x=0;x<rowCnt;x++){
				
				//----------------------------------------------------------------------------------------------
				//	적치열 조회
				//----------------------------------------------------------------------------------------------
				//String szYdGp			  		= inDto.getParam("YD_GP");
				szYdStkColGp 			= slabUtils.nvl(slabUtils.getValue(inDto, "YD_STK_COL_GP"    		,x), ""  ); 
				szYdStkColActStatParam  = slabUtils.nvl(slabUtils.getValue(inDto, "YD_STK_COL_ACT_STAT"    	,x), ""  ); //사용구분  C:사용가능, L:사용중, N:사용불가
				szYdFrmYn				= slabUtils.nvl(slabUtils.getValue(inDto, "YD_FRM_YN"    			,x), ""  ); //형상 구분 Y/N
				szYdCarpntCd			= slabUtils.nvl(slabUtils.getValue(inDto, "YD_CARPNT_CD"    		,x), ""  ); //차량포인트코드
				
				//입동대기차량 (1~5) :YD_BAYIN_WO_SEQ1 
				szYdStkbedUsgCdParam 	= slabUtils.nvl(slabUtils.getValue(inDto, "YD_STKBED_USG_CD"    	,x), ""  ); 
				szTrnEqpCd				= slabUtils.nvl(slabUtils.getValue(inDto, "TRN_EQP_CD"    			,x), ""  ); 
				
				slabUtils.printLog(logId, " YdStkColGp:"+ szYdStkColGp      + " YdStkColActStatParam:" + szYdStkColActStatParam 
						                + " YdStkbedUsgCdParam:" + szYdStkbedUsgCdParam + " TrnEqpCd:" + szTrnEqpCd, "SL");
				recPara.setField("YD_STK_COL_GP",   		szYdStkColGp);
				
				String sposWlocCd  		= slabUtils.nvl(slabUtils.getValue(inDto, "WLOC_CD"    		,x), ""  ); //발지개소코드
				String sposYdPntCd 		= ""; //발지야드포인트코드

				szMsg = "["+szOperationName+"] 적치열["+szYdStkColGp+"] 조회 시작 ";
				slabUtils.printLog(logId, szMsg, "SL");
				/*
				 * 
				 * SELECT 
							YD_STK_COL_GP                          AS YD_STK_COL_GP
							,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
							,REGISTER                              AS REGISTER
							,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
							,MODIFIER                              AS MODIFIER
							,DEL_YN                                AS DEL_YN
							,YD_GP                                 AS YD_GP
							,YD_BAY_GP                             AS YD_BAY_GP
							,YD_EQP_GP	                           AS YD_EQP_GP
							,YD_STK_COL_NO                         AS YD_STK_COL_NO
							,YD_STK_COL_ACT_STAT                   AS YD_STK_COL_ACT_STAT
							,YD_STK_COL_RULE_XAXIS                 AS YD_STK_COL_RULE_XAXIS
							,YD_STK_COL_RULE_YAXIS                 AS YD_STK_COL_RULE_YAXIS
						    ,YD_STK_COL_RULE_ZAXIS                 AS YD_STK_COL_RULE_ZAXIS
							,YD_STK_COL_W                          AS YD_STK_COL_W
							,YD_STK_COL_L                          AS YD_STK_COL_L
							,YD_CAR_USE_GP                         AS YD_CAR_USE_GP
							,TRN_EQP_CD                            AS TRN_EQP_CD
							,CAR_NO                                AS CAR_NO
							,CARD_NO                               AS CARD_NO
							,WLOC_CD                               AS WLOC_CD
							,YD_PNT_CD                             AS YD_PNT_CD
							,YD_STK_COL_W_GP                       AS YD_STK_COL_W_GP
							,YD_STK_COL_H_MAX                      AS YD_STK_COL_H_MAX
							,YD_STK_COL_BED_L_TP                   AS YD_STK_COL_BED_L_TP
						    ,YD_COIL_OUTDIA_GRP_GP                 AS YD_COIL_OUTDIA_GRP_GP 
						    ,YD_STKBED_USG_CD                      AS YD_STKBED_USG_CD     
						    ,YD_STK_SKID_GP                        AS YD_STK_SKID_GP       
						    ,YD_STK_COL_TOLOC_STAT                 AS YD_STK_COL_TOLOC_STAT
						 FROM TB_YD_STKCOL
						WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
						  AND DEL_YN        = 'N'
				 * 
				 */
				JDTORecordSet rsResult = commDao.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdStkcol", logId, methodNm, "적치열조회");
				
				if(rsResult.size() <= 0 ) {
					jrRtn.setField("RTN_CD"	, "0" );
					jrRtn.setField("RTN_MSG", "적치열 대상재[" +szYdStkColGp +"]가 존재하지 않습니다.");
					return jrRtn;
				}
				szMsg = "["+szOperationName+"] 적치열["+szYdStkColGp+"] 조회 완료 - 반환메세지 : " + szRtnMsg;
				slabUtils.printLog(logId, szMsg, "SL");
				
				rsResult.first();
				recTemp = rsResult.getRecord();
				
				szYdStkColActStat       = slabUtils.paraRecChkNull(recTemp, "YD_STK_COL_ACT_STAT");	//사용구분
				szCarNo       			= slabUtils.paraRecChkNull(recTemp, "CAR_NO");
				szCardNo       			= slabUtils.paraRecChkNull(recTemp, "CARD_NO");
				sposYdPntCd       		= slabUtils.paraRecChkNull(recTemp, "YD_PNT_CD");
				
				
				//----------------------------------------------------------------------------------------------
				//	적치열 수정
				//----------------------------------------------------------------------------------------------
				slabUtils.printLog(logId, "szYD_STKBED_USG_CD_PARAM:" + szYdStkbedUsgCdParam , "SL");
				slabUtils.printLog(logId, "szTRN_EQP_CD:"             + szTrnEqpCd           , "SL");
				
				/* 포인트개폐 시 차량사용구분, 운송장비코드, 차량번호, 카드번호 초기화한다. */
				//szYdCarUseGp	= "";	// 차량사용구분 -> 포인트 개폐 시 실제 차량이 있어도 데이터는 닦아준다.
				//szTrnEqpCd	= "";	// 운송장비코드 -> 포인트 개폐 시 실제 차량이 있어도 데이터는 닦아준다.
				//szCarNo		= "";	// 차량번호 -> 출하가 없으니 값이 없어야 함
				//szCardNo		= "";	// 카드번호 -> 출하가 없으니 값이 없어야 함
				
				recPara.setField("CAR_NO"				, szCarNo);
				recPara.setField("CARD_NO"				, szCardNo);
				
				recPara.setField("YD_STKBED_USG_CD"		, szYdStkbedUsgCdParam);
				recPara.setField("TRN_EQP_CD"			, szTrnEqpCd);
				recPara.setField("YD_CAR_USE_GP"		, szYdCarUseGp);
				
				recPara.setField("YD_STK_COL_ACT_STAT"  , szYdStkColActStatParam);		//
				//recPara.setField("MODIFIER"			, slabUtils.nvl(inDto[x].getFieldString("YD_USER_ID"),""));
				
				slabUtils.printLog(logId, "YD_STK_COL_ACT_STAT:"+ szYdStkColActStatParam, "SL");
				/*
				UPDATE TB_YD_STKCOL
				SET    MOD_DDTT             = SYSDATE  
				     , YD_STK_COL_ACT_STAT  = NVL(:V_YD_STK_COL_ACT_STAT ,YD_STK_COL_ACT_STAT)
				     , YD_CAR_USE_GP        = NVL(:V_YD_CAR_USE_GP       ,YD_CAR_USE_GP      )
				     , TRN_EQP_CD           = NVL(:V_TRN_EQP_CD          ,TRN_EQP_CD         )
				     , CAR_NO               = NVL(:V_CAR_NO              ,CAR_NO             )
				     , CARD_NO              = NVL(:V_CARD_NO             ,CARD_NO            )
				     , MODIFIER             = NVL(:V_MODIFIER            ,MODIFIER           ) 
				WHERE YD_STK_COL_GP         = :V_YD_STK_COL_GP
				  AND DEL_YN = 'N'
				*/
				intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdStkcol01", logId, methodNm, "적치열상태수정");
				if(intRtnVal <= 0 ) {
					jrRtn.setField("RTN_CD"	, "0" );
					jrRtn.setField("RTN_MSG", "parameter error!!! (처리 대상건이 없음.)");
					return jrRtn;
				}
				
				// 차량포인트 수정
				slabUtils.printLog(logId, "(후판일 경우에만 실행)["+szYdStkColGp.substring(0,1)+"] Yd_Stk_Col_Gp:" + szYdStkColGp, "SL");
				
				recPara.setField("YD_CARPNT_CD"			, szYdCarpntCd			); // 차량포인트
				recPara.setField("YD_STK_COL_ACT_STAT"	, szYdStkColActStatParam); // 적치열 활성상태
				recPara.setField("YD_FRM_YN"			, szYdFrmYn				); // 형상구분
				
				/*
				UPDATE TB_YD_CARPOINT
				   SET YD_STK_COL_ACT_STAT = NVL(:V_YD_STK_COL_ACT_STAT ,YD_STK_COL_ACT_STAT ) 
				     , TRN_EQP_CD          = NVL(:V_TRN_EQP_CD          ,TRN_EQP_CD          )
				     , CAR_NO              = NVL(:V_CAR_NO              ,CAR_NO              )
				     , CARD_NO             = NVL(:V_CARD_NO             ,CARD_NO             )
				     , YD_FRM_YN           = NVL(:V_YD_FRM_YN           ,YD_FRM_YN           )
				     , MODIFIER            = :V_MODIFIER
				 WHERE YD_CARPNT_CD        = :V_YD_CARPNT_CD
				   AND DEL_YN = 'N'

				 */
				intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.uptYdCarpoint", logId, methodNm, "후판수정");
				if(intRtnVal < 0) {
					szMsg = "수정대상건이 없습니다.";
					jrRtn.setField("RTN_CD"	, rtnEcod);
					jrRtn.setField("RTN_MSG", szMsg);
					return jrRtn;
				}
				
				slabUtils.printLog(logId, szMsg , "SL");
				
				//----------------------------------------------------------------------------------------------
				//	포인트개폐 전송
				//----------------------------------------------------------------------------------------------
				JDTORecord jrTS012 = JDTORecordFactory.getInstance().create();
				jrTS012.setField("JMS_TC_CD"    		, "YDTSJ012" ); 	//JMSTC코드
				jrTS012.setField("JMS_TC_CD"			, "YDTSJ012");		//TC CCODE : PSlabYdConstant.JMS_TC_CD_YDTSJ012
				jrTS012.setField("YD_STK_COL_GP"		, szYdStkColGp);	//야드적치열 구분
				jrTS012.setField("YD_GP"				, szYdStkColGp.substring(0,1));		//야드구분
				
				String curDateTime = slabUtils.getDateTime14();
				jrTS012.setField("PRSNT_LOC_WLOC_CD" 	, sposWlocCd ); 	//현위치개소코드     
				jrTS012.setField("YD_PNT_CD" 			, sposYdPntCd );    //야드포인트코드             
				jrTS012.setField("YD_PNT_OP_CL_TT" 		, curDateTime );    //야드포인트개폐시각       
				jrTS012.setField("JMS_TC_CREATE_DDTT"	, curDateTime); 	//JMSTC생성일시

				szMsg  = "(포인트개폐 전송)"+ "적치열:" + szYdStkColGp + " 현상태:" + szYdStkColActStat +  " 변경후(keyIn):" + szYdStkColActStatParam ;
				
				if(        PSlabYdConstant.YD_STK_COL_ACT_STAT_C.equals(szYdStkColActStatParam) 	//C:사용가능 
						|| PSlabYdConstant.YD_STK_COL_ACT_STAT_L.equals(szYdStkColActStatParam)		//L:사용중 
						|| "R".equals(szYdStkColActStatParam)  										//"R"
						)
				{
					if( "N".equals(szYdStkColActStat)) {			//C:비활성화, L:적치가능(활성), (N):사용불가 
						
						szMsg = "["+szOperationName+"] 적치열["+szYdStkColGp+"] - 변경된 야드적치열활성상태["+szYdStkColActStatParam+"]에 대한 포인트 OPEN 처리 전문 송신 ";
						slabUtils.printLog(logId, szMsg , "SL");
						
						jrTS012.setField("PNT_UNIT_CL_GP",	"Y");		//포인트개폐구분      
					}else{
						isSendable = false;
					}
				}else if("N".equals(szYdStkColActStatParam)){
					
					szMsg = "["+szOperationName+"] 적치열["+szYdStkColGp+"]에 대한 사용불가(포인트 CLOSE) 처리 전문 송신 ";
					slabUtils.printLog(logId, szMsg , "SL");
					
					jrTS012.setField("PNT_UNIT_CL_GP",		"N");
				}else{
					szMsg = "["+szOperationName+"] 포인트 개폐구분의 값이 없습니다 !!!" + "  예외조건:" + szYdStkColActStatParam;
					slabUtils.printLog(logId, szMsg , "SL");

					jrRtn.setField("RTN_CD"	, rtnEcod);
					jrRtn.setField("RTN_MSG", szMsg);
					return jrRtn;
				}
				slabUtils.printLog(logId, "PNT_UNIT_CL_GP:" +recPara.getField("PNT_UNIT_CL_GP"), "SL");
				
				if( isSendable ) {

					//Delegate 연결
					//slabUtils.addSndData(jrRtn, recPara);					//ydDelegate.sendMsg(recPara);
					jrRtn = slabUtils.addSndData(jrRtn, jrTS012);
					//
					szMsg = "["+szOperationName+"] 적치열["+szYdStkColGp+"]에 대한 포인트개폐 전문 송신 ";
					slabUtils.printLog(logId, szMsg , "SL");

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_INFO_SYNC_CD"	, 	"3");							//1:동,2:SPAN,3:열,4:BED
					recPara.setField("YD_GP"			, 	szYdStkColGp.substring(0, 1));
					recPara.setField("YD_STK_COL_GP"	, 	szYdStkColGp);
					recPara.setField("YD_EQP_WRK_STAT_GUBUN" , "");
					szTcCode = "YDY3L001";
					
					recPara.setField("EJB_MSG_ID", szTcCode);
					recPara.setField("YD_STK_BED_NO"  , "");  
					
					//ydDelegate.sendMsg(recL2Para);
					szMsg = "MSG_ID:" + recPara.getField("EJB_MSG_ID") + " JMS_TC_CD:" + recPara.getField("JMS_TC_CD"); 
					slabUtils.printLog(logId,  szMsg, "SL");
					
					//jrRtn = slabUtils.addSndData(jrRtn, recPara);
					jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY3L001", recPara));	
					
					szMsg = "["+szOperationName+"] 적치열["+szYdStkColGp+"]에 대한 야드저장위치제원 전문 송신 완료 ";
					slabUtils.printLog(logId, szMsg , "SL");
				}
			}//For_(end)
			
			szMsg = "["+szOperationName+"] --------------- 메소드 끝 --------------- ";
			slabUtils.printLog(logId, szMsg , "SL");
			slabUtils.printLog(logId, methodNm, "S-");

			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "정상 처리 되었습니다.");
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		} 
	}   

	
	/**
	 * 오퍼레이션명 : (1-2)입동순서 변경  (차량작업관리 TAB1.차량Point작업현황)
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리 
	 * PYS  2020-10-29
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public JDTORecord procSlabYdBayInWoSeqChang(GridData inDto) throws DAOException {
		String methodNm = "입동순서 변경 [PSlabYdMvCarSeEJB.procSlabYdBayInWoSeqChang] < " + inDto.getNavigateValue();
		String logId    = inDto.getIPAddress();
		String szMsg 	= "";
		
		try{
			szMsg = "#차량작업관리> 차량 POINT작업현황> " + methodNm;
			slabUtils.printLog(logId, szMsg , "S+");
			String sYdCarSchId 	 = "";
			String szYdBayinWoSeq= "";
			int    intRtnVal     = 0;
			String sModifier     = slabUtils.trim(inDto.getParam("userid" )); 			//수정자(MODIFIER)
			JDTORecord jrRtn     = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
			JDTORecord recPara   = slabUtils.getParam(logId, methodNm, sModifier);
			
			int rowCnt = inDto.getHeader("CHECK").getRowCount();
			slabUtils.printLog(logId, "대상건수:" +rowCnt  , "SL");
		
			for(int x=0;x<rowCnt;x++){
				slabUtils.printLog(logId, szMsg , "SL");
				
				for(int i=1;i<=5;i++){			//조회시 15개까지 처리 했으나, 화면상에 5개만 존재함. 
					sYdCarSchId    = slabUtils.nvl(inDto.getHeader("YD_CAR_SCH_ID"  +i).getValue(x),    "");
					szYdBayinWoSeq = slabUtils.nvl(inDto.getHeader("YD_BAYIN_WO_SEQ"+i).getValue(x),    "");
					
					szMsg = i + "> YD_CAR_SCH_ID:" + sYdCarSchId + "  <"  ;
					slabUtils.printLog(logId, szMsg, "SL");
					/*
					 * 화면상에서 순위변경된 사항만 입력 받음.  예)2번스케쥴 순위변경  --> 2번  차량번호에 스케쥴번호를 받아서   입동순서(YD_BAYIN_WO_SEQ)  update 처리 
					 * 
					 */
					if(!sYdCarSchId.equals("")){
						if(Integer.parseInt(szYdBayinWoSeq) > 0) {
							recPara.setField("YD_CAR_SCH_ID"	, sYdCarSchId);
							recPara.setField("YD_BAYIN_WO_SEQ"	, szYdBayinWoSeq);
							slabUtils.printLog(logId, i + "=" + sYdCarSchId + "==>" + szYdBayinWoSeq, "SL");//입동순서 변경사항 반영
							/*       
							UPDATE TB_YD_CARSCH
							     SET MOD_DDTT        = SYSDATE
							       , MODIFIER        = :V_MODIFIER
							       , YD_BAYIN_WO_SEQ = :V_YD_BAYIN_WO_SEQ		--야드입동지시순번
							WHERE YD_CAR_SCH_ID      = :V_YD_CAR_SCH_ID
							 *	
							//intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recPara,303);
							 */
							intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdCarschYdBayinWoSeq", logId, methodNm, "적치단 존재 여부");
							if(intRtnVal <= 0) {
								jrRtn.setField("RTN_CD"	, "0");
								jrRtn.setField("RTN_MSG", "대상건을 확인해 주세요!" );
								return jrRtn;
							}
						}
					}
				}	
			}
			
			szMsg = "차량작업관리 차량 POINT작업현황 입동순서 변경 작업을 처리하였습니다.";
			slabUtils.printLog(logId, szMsg , "S-");

			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", szMsg );
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}



	// 오퍼레이션명 : (포인트)point개폐처리    	                   		procSlabYdPntUnitCLCoil	
	// 오퍼레이션명 :(2) 목표행선및 적치배드의 양에 따라 작업예약재료들의 권상모음순서 정리   sortUpColSeqTwo
	// 오퍼레이션명 : BED 능력체크.                                 CheckBedSpec
	/**
	 * 오퍼레이션명 : (1-2)입동순서 변경  (차량작업관리 TAB1.차량Point작업현황)
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리 
	 * PYS  2020-10-29
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public JDTORecord procSlabYdGdsBayInWoSeqChang(GridData inDto) throws DAOException {
		String methodNm = inDto.getNavigateValue();
		String logId    = inDto.getIPAddress();
		
		try{
			String szMsg = "#차량작업관리> 차량 POINT작업현황> 입동순서 변경 작업 시작>";
			slabUtils.printLog(logId, szMsg , "S+");
			
		//	JDTORecord [] inRecord =  pslabUtils.genJDTORecordSet(inDto);
			String sYdCarSchId 			= "";
			String szYdBayinWoSeq 		= "";
			int    intRtnVal    		= 0;
			String sModifier     = slabUtils.trim(inDto.getParam("userid" )); 			//수정자(MODIFIER)
			JDTORecord jrRtn     = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
			JDTORecord recPara   = slabUtils.getParam(logId, methodNm, sModifier);
			
			
			int rowCnt = inDto.getHeader("CHECK").getRowCount();
			slabUtils.printLog(logId, "대상건수:" +rowCnt  , "SL");
		
			// 수정
			for(int x=0;x<rowCnt;x++){
				//String szYdCarSchId   = slabUtils.nvl(slabUtils.getValue(inDto, "YD_CAR_SCH_ID"    ,x), ""  ); 
				
				slabUtils.printLog(logId, szMsg , "SL");
				
				for(int i=1;i<=5;i++){			//조회시 15개까지 처리 했으나, 화면상에 5개만 존재함. 
					sYdCarSchId    = slabUtils.setDataDefault(inDto.getHeader("YD_CAR_SCH_ID"  +i).getValue(x),    "");
					szYdBayinWoSeq = slabUtils.setDataDefault(inDto.getHeader("YD_BAYIN_WO_SEQ"+i).getValue(x),    "");
					
					szMsg = i + "> YD_CAR_SCH_ID:" + sYdCarSchId + "  <"  ;
					slabUtils.printLog(logId, szMsg, "SL");
					/*
					 * 화면상에서 순위변경된 사항만 입력 받음.  예)2번스케쥴 순위변경  --> 2번  차량번호에 스케쥴번호를 받아서   입동순서(YD_BAYIN_WO_SEQ)  update 처리 
					 */
					if(!sYdCarSchId.equals("")){
						if(Integer.parseInt(szYdBayinWoSeq) > 0) {
							recPara.setField("YD_CAR_SCH_ID"	, sYdCarSchId);
							recPara.setField("YD_BAYIN_WO_SEQ"	, szYdBayinWoSeq);
							slabUtils.printLog(logId, i + "=" + sYdCarSchId + "==>" + szYdBayinWoSeq, "SL");//입동순서 변경사항 반영
							/* 
							UPDATE TB_YD_CARSCH
							     SET MOD_DDTT        = SYSDATE
							       , MODIFIER        = :V_MODIFIER
							       , YD_BAYIN_WO_SEQ = :V_YD_BAYIN_WO_SEQ		--야드입동지시순번
							WHERE YD_CAR_SCH_ID      = :V_YD_CAR_SCH_ID
							 */
							String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdCarschYdBayinWoSeq";
							intRtnVal = commDao.update(recPara, queryId, logId, methodNm, "적치단 존재 여부");
							if(intRtnVal <= 0) {
								jrRtn.setField("RTN_CD"	, "0");
								jrRtn.setField("RTN_MSG", "대상건을 확인해 주세요!" );
								return jrRtn;
							}
						}
					}
				}	
			}
			
			szMsg = "차량작업관리 차량 POINT작업현황 입동순서 변경 작업을 처리했습니다.";
			slabUtils.printLog(logId, szMsg , "S-");

			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", szMsg );
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	


	/**
	 * 차량작업관리> 차량작업상세내역 조회 (TAB-3)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getYdGdsCarWork(GridData gdReq) throws DAOException {
		String methodNm = "차량작업재료조회[PSlabYdMvCarSeEJB.getYdGdsCarWork] < " + gdReq.getNavigateValue();
		String logId    = gdReq.getIPAddress();
		
		try {
			slabUtils.printLog(logId, methodNm +"메소드 시작", "S+");
			slabUtils.printLog(logId, "▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨", "SL");
			slabUtils.printLog(logId, "차량작업관리 - 차량작업상세내역(TAB-3)조회 START. ", "SL");
			slabUtils.printLog(logId, "▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨", "SL");
			
			int intRtnVal 					= 0;
			String szMsg        			= "";		
			String szRtnMsg					= PSlabYdConstant.RETN_CD_SUCCESS;
			
			String szYD_CAR_SCH_ID			= "";
			String szYD_CAR_USE_GP			= "";
			String szTRN_EQP_CD				= "";
			String szCAR_NO					= "";
			String szCARD_NO				= "";
			String szYD_CAR_PROG_STAT		= "";
			String szDEL_YN					= "";
			String szSPOS_WLOC_CD			= "";
			String szYD_GP					= "";
			String szTRANS_ORD_DATE			= "";
			String szYD_CARLD_WRK_BOOK_ID   = "";
			
			GridData 		gdRet			= null;
			JDTORecordSet 	jsOutSet		= JDTORecordFactory.getInstance().createRecordSet("retTmp");	
			String     sModifier  = slabUtils.trim(gdReq.getParam("userid" )); 			//수정자(YD_USER_ID)
			String     sWlocCd    = slabUtils.trim(gdReq.getParam("WLOC_CD3")); 
			JDTORecord recPara    = slabUtils.getParam(logId, methodNm, sModifier);
			
			gdReq.addParam("PAGE_NO"	, "1");
			gdReq.addParam("PAGE_SIZE"	, "9999999");
			
			
			//UI로 반환 할 Grid data 를 생성 
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);

			//-----------------------------------------------------------------------------------------------------------------
			// 파라미터 확인
			//-----------------------------------------------------------------------------------------------------------------
			//ydUtils.displayRecord(szOperationName, inDto);
			//-----------------------------------------------------------------------------------------------------------------
			
			slabUtils.printLog(logId, "▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨", "SL");
			slabUtils.printLog(logId, "1. 차량스케줄 확인", "SL");
			slabUtils.printLog(logId, "▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨", "SL");
			
			//-----------------------------------------------------------------------------------------------------------------
			// 1. 차량스케줄 조회
			//-----------------------------------------------------------------------------------------------------------------
			szYD_CAR_SCH_ID = slabUtils.trim(gdReq.getParam("YD_CAR_SCH_ID" )) ;
			slabUtils.printLog(logId, "▣ 스케줄ID(YD_CAR_SCH_ID) : " + szYD_CAR_SCH_ID, "SL");
			
			
			slabUtils.printLog(logId, "▨▨▨ 1-1. 차량스케줄조회 ▨▨▨", "SL");
			/* # com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdCarsch01
			 * 
				SELECT 
				    YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
				    ,REGISTER AS REGISTER
				    ,TO_CHAR(REG_DDTT,'YYYYMMDDHH24MISS') AS REG_DDTT
				    ,MODIFIER AS MODIFIER
				    ,TO_CHAR(MOD_DDTT,'YYYYMMDDHH24MISS') AS MOD_DDTT
				    ,DEL_YN AS DEL_YN
				    ,YD_EQP_ID AS YD_EQP_ID
				    ,YD_CAR_USE_GP AS YD_CAR_USE_GP
				    ,CAR_NO AS CAR_NO
				    ,TRN_EQP_CD AS TRN_EQP_CD
				    ,CAR_KIND AS CAR_KIND
				    ,TRANS_EQUIPMENT_TYPE AS TRANS_EQUIPMENT_TYPE 
				    ,YD_EQP_WRK_STAT  AS YD_EQP_WRK_STAT
				    ,YD_WRK_PROG_STAT  AS YD_WRK_PROG_STAT
				    ,NVL(YD_EQP_WRK_SH,'0')  AS YD_EQP_WRK_SH
				    ,YD_EQP_WRK_WT  AS YD_EQP_WRK_WT
				    ,YD_STK_BED_TP  AS YD_STK_BED_TP
				    ,SPOS_WLOC_CD  AS SPOS_WLOC_CD
				       ,(CASE WHEN ARR_WLOC_CD IS NULL THEN (SELECT 
				                                                    (SELECT / *+ INDEX(D PK_PT_STLFRTOMOVE)* / 
				                                                            D.ARR_WLOC_CD
				                                                        FROM TB_PT_STLFRTOMOVE D
				                                                       WHERE D.TRANSWORD_SEQNO=(SELECT/ *+ INDEX_DESC(D PK_PT_STLFRTOMOVE)* /
				                                                                            MAX(TRANSWORD_SEQNO) 
				                                                                         FROM TB_PT_STLFRTOMOVE K
				                                                                         WHERE D.STL_NO=K.STL_NO
				                                                                          AND ROWNUM<=1)
				                                                         AND B.STL_NO =D.STL_NO
				                                                         ) AS ARR_WLOC_CD
				                                             FROM  TB_YD_STKLYR B                                              
				                                              WHERE C.YD_CARLD_STOP_LOC=B.YD_STK_COL_GP     
				                                                AND ROWNUM<=1 ) 
				          ELSE ARR_WLOC_CD END) AS ARR_WLOC_CD
				    ,YD_CARLD_LEV_LOC  AS YD_CARLD_LEV_LOC
				    ,TO_CHAR(YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_LEV_DT
				    ,TO_CHAR(YD_CARLD_PNT_WO_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_PNT_WO_DT
				    ,YD_PNT_CD1 AS YD_PNT_CD1
				    ,YD_PNT_CD2 AS YD_PNT_CD2
				    ,YD_CARLD_WRK_BOOK_ID  AS YD_CARLD_WRK_BOOK_ID
				    ,YD_CARLD_SCH_REQ_GP  AS YD_CARLD_SCH_REQ_GP
				    ,YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
				    ,TO_CHAR(YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_ARR_DT
				    ,TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_ST_DT
				    ,TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_CMPL_DT
				    ,YD_CARLD_WRK_ACT_GP  AS YD_CARLD_WRK_ACT_GP
				    ,TO_CHAR(YD_CARLD_CHK_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_CHK_DT
				    ,TO_CHAR(YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_LEV_DT
				    ,TO_CHAR(YD_CARUD_PNT_WO_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_PNT_WO_DT
				    ,YD_PNT_CD3 AS YD_PNT_CD3
				    ,YD_PNT_CD4 AS YD_PNT_CD4
				    ,YD_CARUD_WRK_BOOK_ID  AS YD_CARUD_WRK_BOOK_ID
				    ,YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
				    ,YD_CARUD_SCH_REQ_GP  AS YD_CARUD_SCH_REQ_GP
				    ,TO_CHAR(YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_ARR_DT
				    ,TO_CHAR(YD_CARUD_CHK_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_CHK_DT
				    ,TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_ST_DT
				    ,TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_CMPL_DT
				    ,YD_CARUD_WRK_ACT_GP  AS YD_CARUD_WRK_ACT_GP
				    ,YD_TRN_WRK_DELY_CD  AS YD_TRN_WRK_DELY_CD
				    ,CARD_NO  AS CARD_NO
				    ,YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT
				    ,FRTOMOVE_PLANT_GP AS FRTOMOVE_PLANT_GP
				    ,PROC_TO AS PROC_TO
				    ,RENTPROC_CD AS RENTPROC_CD
				    ,YD_FRTOMOVE_YD_GP AS YD_FRTOMOVE_YD_GP
				    ,YD_FRTOMOVE_BAY_GP AS YD_FRTOMOVE_BAY_GP
				    ,URGENT_FRTOMOVE_WORD_GP AS URGENT_FRTOMOVE_WORD_GP
				    ,DEST_TEL_NO AS DEST_TEL_NO
				    ,YD_DLVRDD_RULE_DD AS YD_DLVRDD_RULE_DD
				    ,SHIPASSIGN_WORD_DATE AS SHIPASSIGN_WORD_DATE
				    ,SHIPASSIGN_WORD_SEQNO AS SHIPASSIGN_WORD_SEQNO
				    ,SHIP_CD AS SHIP_CD
				    ,SHIP_NAME AS SHIP_NAME
				    ,RSHP_HOLD_NO AS RSHP_HOLD_NO
				    ,BERTH_NO AS BERTH_NO
				    ,SAILNO AS SAILNO
				    ,YD_CAR_WRK_GP AS YD_CAR_WRK_GP
				    ,TRANS_ORD_DATE AS TRANS_ORD_DATE
				    ,TRANS_ORD_SEQNO AS TRANS_ORD_SEQNO
				    ,YD_BAYIN_WO_SEQ
				    ,YD_CAR_RCPT_CHK_YN
				    ,YD_CAR_ISSUE_CHK_YN
				    ,YD_CAR_RCPT_CHECKER
				    ,YD_CAR_ISSUE_CHECKER    
				    ,SUBSTR(YD_CARLD_STOP_LOC,1,1) AS YD_GP
				    ,(select count(*)
				     from TB_YD_CRNSCH
				     where YD_WBOOK_ID = YD_CARLD_WRK_BOOK_ID
				     AND DEL_YN = 'N') YD_CRN_SCH_ID
				    ,(SELECT CASE WHEN YD_SCH_CD LIKE 'D_YD__MM' THEN 'Y'
				                  ELSE 'N' END AS M_WRK_GP
				        FROM TB_YD_WRKBOOK
				       WHERE YD_WBOOK_ID = (
				    SELECT CS.YD_CARLD_WRK_BOOK_ID
				    FROM TB_YD_CARSCH CS
				    WHERE 1=1 
				      AND CS.YD_CAR_SCH_ID = C.YD_CAR_SCH_ID
				      AND DEL_YN = 'N') 
				    ) AS M_WRK_GP
				   ,(SELECT CP.YD_FRM_YN FROM TB_YD_CARPOINT CP WHERE  CP.YD_STK_COL_GP = YD_CARLD_STOP_LOC ) AS YD_FRM_YN
				FROM TB_YD_CARSCH C
				WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
			 */
			recPara.setField("YD_CAR_SCH_ID"			, szYD_CAR_SCH_ID);
			JDTORecordSet outRecSet = commDao.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdCarsch01", logId, methodNm, "차량스케줄조회");
			
			
			slabUtils.printLog(logId, "▨▨▨ 1-2. 차량스케줄 에러체크 START. ▨▨▨", "SL");
			slabUtils.printLog(logId, "outRecSet.size() : " + outRecSet.size(), "SL");
			
			if(outRecSet.size() == 1) {	// 차량스케줄 ID가 정상조회 됐을 때(단 건 조회)
				szRtnMsg = PSlabYdConstant.RETN_CD_SUCCESS;
				szMsg="["+methodNm+"] 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 차량스케줄이 존재합니다.";
			}else{						// 차량스케줄 ID 조회결과가 이상 시 에러체크
				if(outRecSet.size() > 1) {
					szMsg="["+methodNm+"] 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 차량스케줄이 여러건["+intRtnVal+"] 존재합니다.";
					slabUtils.printLog(logId, "" + szMsg, "SL");
					szRtnMsg = PSlabYdConstant.RETN_CD_DUPLICATE;
				}else if(outRecSet.size() == 0) {
					szMsg="["+methodNm+"] 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 차량스케줄 조회 시 존재하지 않습니다.";
					slabUtils.printLog(logId, "" + szMsg, "SL");
					szRtnMsg = PSlabYdConstant.RETN_CD_NOTEXIST;
				}else if(outRecSet.size() == -2) {
					szMsg="["+methodNm+"] 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 차량스케줄 조회 시 : parameter error";
					slabUtils.printLog(logId, "" + szMsg, "SL");
					szRtnMsg = PSlabYdConstant.RETN_CD_NO_PARAM;
				}else if(outRecSet.size() < 0) {
					szMsg="["+methodNm+"] 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 차량스케줄 조회 오류발생 - 반환값 : " + intRtnVal;
					slabUtils.printLog(logId, "" + szMsg, "SL");
					slabUtils.printLog(logId, "" + szMsg, "SL");
					szRtnMsg = PSlabYdConstant.RETN_CD_FAILURE;
				}
				gdRtn = OperateGridData.cloneResponseGridData(gdReq);
				return slabUtils.jdtoRecordToGridData(gdRtn, jsOutSet.toList(), gdReq);
			}
			
			slabUtils.printLog(logId, "*** 결과메세지 : " + szMsg		, "SL");
			slabUtils.printLog(logId, "*** 리턴메세지 : " + szRtnMsg		, "SL");
			
			// 예기치 않은 결과 발생 시 에러를 발생한다.
			if(!szRtnMsg.equals(PSlabYdConstant.RETN_CD_SUCCESS)){
				szMsg = "["+methodNm+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 시 오류발생";
				slabUtils.printLog(logId, szMsg, "SL");	
				gdRtn = OperateGridData.cloneResponseGridData(gdReq);
				return slabUtils.jdtoRecordToGridData(gdRtn, jsOutSet.toList(), gdReq);
			}
			 
			
			//-----------------------------------------------------------------------------------------------------------------
			// 2. 차량작업내역 조회
			//-----------------------------------------------------------------------------------------------------------------
			slabUtils.printLog(logId, "▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨"	, "SL");
			slabUtils.printLog(logId, "2. 차량작업내역 조회"						, "SL");
			slabUtils.printLog(logId, "▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨"	, "SL");
			
			//-----------------------------------------------------------------------------------------------------------------
			// 2-1. 차량스케줄 삭제유무 확인
			//-----------------------------------------------------------------------------------------------------------------
			slabUtils.printLog(logId, "▨▨▨ 2-1. 차량스케줄 삭제유무 확인 ▨▨▨", "SL");
			outRecSet.first();
			recPara = outRecSet.getRecord();
			szDEL_YN = recPara.getField("DEL_YN").toString() ;
			
			slabUtils.printLog(logId, "*** 차량스케줄 종료유무(DEL_YN) : " + szDEL_YN, "SL");
			
			if(szDEL_YN.equals("Y")){		// 차량스케줄이 종료된 상태면 종료메세지 리턴
				szMsg = "[JSP Session - "+methodNm+"] 차량스케줄["+szYD_CAR_SCH_ID+"]이 삭제되었습니다.";
				slabUtils.printLog(logId, szMsg, "SL");
				gdRet = slabUtils.jdtoRecordToGridData(gdRtn, outRecSet.toList(), gdReq); 
				return gdRet;
			}
			
			// 차량스케줄이 삭제되지 않은 경우 차량작업내역 조회
			slabUtils.printLog(logId, "▨▨▨ 2-2. 차량 작업 내역 조회    ▨▨▨", "SL");
			szYD_GP					= PSlabYdConstant.YD_GP_A_PLATE_SLAB_YARD;						// 야드구분 -> [D]후판슬라브야드
			//szSPOS_WLOC_CD			= slabUtils.paraRecChkNull(recPara, "SPOS_WLOC_CD"			);	// 발지개소코드
			szYD_CAR_USE_GP 		= slabUtils.paraRecChkNull(recPara, "YD_CAR_USE_GP"			);	// 야드차량사용구분
			szTRN_EQP_CD 			= slabUtils.paraRecChkNull(recPara, "TRN_EQP_CD"			);	// 운송장비코드
			szCAR_NO 				= slabUtils.paraRecChkNull(recPara, "CAR_NO"				);
			szCARD_NO 				= slabUtils.paraRecChkNull(recPara, "CARD_NO"				);
			szYD_CAR_PROG_STAT		= slabUtils.paraRecChkNull(recPara, "YD_CAR_PROG_STAT"		);	// 야드차량진행상태
			szTRANS_ORD_DATE		= slabUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE"		);	// 운송지시일자
			szYD_CARLD_WRK_BOOK_ID	= slabUtils.paraRecChkNull(recPara, "YD_CARLD_WRK_BOOK_ID"	);	// 상차작업예약ID
			if("".equals(szSPOS_WLOC_CD) || "ALL".equals(szSPOS_WLOC_CD)) {
				szSPOS_WLOC_CD = sWlocCd;
			}
			// 차량상태 확인 -> 차량 상태 별로 조회쿼리 분기
			slabUtils.printLog(logId, ">> 차량 상태 조회", "SL");
			slabUtils.printLog(logId, "야드구분		 [YD_GP]                : " + szYD_GP				, "SL"); // 야드구분 -> [D]후판슬라브야드
			slabUtils.printLog(logId, "발지개소코드	 [SPOS_WLOC_CD]         : " + szSPOS_WLOC_CD		, "SL"); // 발지개소코드
			slabUtils.printLog(logId, "야드차량사용구분 [YD_CAR_USE_GP]        : " + szYD_CAR_USE_GP		, "SL"); // 야드차량사용구분
			slabUtils.printLog(logId, "운송장비코드	 [TRN_EQP_CD]           : " + szTRN_EQP_CD			, "SL"); // 운송장비코드
			slabUtils.printLog(logId, "야드차량진행상태 [YD_CAR_PROG_STAT]     : " + szYD_CAR_PROG_STAT	, "SL"); // 야드차량진행상태
			slabUtils.printLog(logId, "운송지시일자	 [TRANS_ORD_DATE]       : " + szTRANS_ORD_DATE		, "SL"); // 운송지시일자
			slabUtils.printLog(logId, "상차작업예약ID [YD_CARLD_WRK_BOOK_ID] : " + szYD_CARLD_WRK_BOOK_ID, "SL"); // 상차작업예약ID
			
			if( PSlabYdConstant.YD_CARLD_CMPL.equals(szYD_CAR_PROG_STAT) ) {		//상차완료	
				slabUtils.printLog(logId, ">> 차량상태 : 상차완료상태", "SL");
				//-----------------------------------------------------------------------------------------------------------------
				//	상차완료인 경우는 차량이송재료를 조회
				//-----------------------------------------------------------------------------------------------------------------
				szMsg = "[JSP Session - "+methodNm+"] 상차완료인 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량이송재료 조회 시작" + "<" + szYD_CAR_PROG_STAT +">";
				slabUtils.printLog(logId, szMsg, "SL");
				
				recPara.setField("YD_CAR_SCH_ID",		szYD_CAR_SCH_ID);
				
				/* 차량이송재료, 적치단, 저장품 조인
				 * SELECT COUNT(*) OVER() AS CNT ,
					       SUM(YD_MTL_WT) OVER() AS YD_MTL_WT_SUM ,
					       A.YD_CAR_SCH_ID ,
					       B.STL_NO ,
					       B.YD_STK_COL_GP ,
					       B.YD_STK_BED_NO ,
					       B.YD_STK_LYR_NO ,
					       B.YD_STK_COL_GP || B.YD_STK_BED_NO || B.YD_STK_LYR_NO AS YD_STR_LOC ,
					       C.ORD_NO ,
					       C.ORD_DTL ,
					       CASE WHEN C.ORD_NO || '-' || C.ORD_DTL = '-' THEN '' ELSE C.ORD_NO || '-' || C.ORD_DTL END AS ORD_NO_DTL ,
					       C.ORD_YEOJAE_GP ,
					       C.STL_PROG_CD ,
					       C.OVERALL_STAMP_GRADE ,
					       C.GOODS_GRADE ,
					       C.YD_MTL_T ,
					       C.YD_MTL_W ,
					       C.YD_MTL_L ,
					       C.YD_MTL_WT ,
					       C.YD_MTL_ITEM ,
					       SUBSTR(C.YD_MTL_ITEM, 1, 1) AS YD_MTL_ITEM_GP ,
					       TO_CHAR(C.YD_MTL_T, '9,999') || ' X ' || TO_CHAR(C.YD_MTL_W, '9,999') || ' X ' || TO_CHAR(DECODE(SUBSTR(C.YD_MTL_ITEM, 1, 1), 'C', C.COIL_OUTDIA, C.YD_MTL_L) , '99,999') AS GOODS_SIZE ,
					       C.YD_AIM_RT_GP ,
					       C.TRANS_ORD_DATE ,
					       C.TRANS_ORD_SEQNO ,
					       CASE WHEN C.TRANS_ORD_DATE || '-' || C.TRANS_ORD_SEQNO = '-' THEN '' ELSE C.TRANS_ORD_DATE || '-' || C.TRANS_ORD_SEQNO END  AS TRANS_ORD_DATE_SEQNO,
					       C.DEST_CD ,
					       C.CUST_CD ,
					       C.DEMANDER_CD ,
					       C.DEST_TEL_NO ,
					       C.DIST_SHIPASSIGN_GP ,
					       C.EXPORT_SHIP_SET_NO ,
					       C.SHIPASSIGN_WORD_DATE ,
					       C.SHIPASSIGN_WORD_SEQNO ,
					       C.SHIP_NAME ,
					       C.BERTH_NO ,
					       C.SAILNO ,
					       C.YD_AIM_YD_GP ,
					       C.YD_AIM_BAY_GP ,
					       C.HCR_GP,
					       D.ARR_WLOC_CD
					--스카핑여부,규격약호,구입슬라브추가 
					       ,
					       CASE
					         WHEN ( C.SCARFING_YN = 'Y'
					        AND    NVL(C.SCARFING_DONE_YN, 'N') = 'N') THEN '*'
					         ELSE ''
					       END AS SCARFING 
					       ,C.SPEC_ABBSYM  
					       ,(SELECT X.BUY_SLAB_NO
					        FROM   TB_QM_BUYSLABINFO X
					        WHERE  X.MSLAB_NO = C.STL_NO) AS GU_SLAB_NO
					FROM   USRYDA.TB_YD_CARFTMVMTL A,
					       USRYDA.TB_YD_STKLYR B,
					       USRYDA.TB_YD_STOCK C,
					       TB_PT_STLFRTOMOVE D
					WHERE  A.STL_NO = B.STL_NO
					AND    B.STL_NO = C.STL_NO
					AND    C.STL_NO = D.STL_NO
					AND    A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					AND    A.DEL_YN = 'N'
					AND    B.DEL_YN = 'N'
					AND    C.DEL_YN = 'N'
					AND    D.TRANSWORD_SEQNO = (select max(TRANSWORD_SEQNO)
					                            from TB_PT_STLFRTOMOVE
					                            where STL_NO = C.STL_NO)
					ORDER BY B.YD_STK_COL_GP ASC, B.YD_STK_BED_NO ASC, B.YD_STK_LYR_NO DESC
				 * 
				intRtnVal	= ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, outRecSet, 12);
				 */
				//
				String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdCarftmvmtlStockStkLyrByCarSchId";
				jsOutSet = commDao.select(recPara, queryId, logId, methodNm, "상차완료인 차량스케줄조회");

				intRtnVal = jsOutSet.size();
				
				szMsg = "[JSP Session - "+methodNm+"] 상차완료인 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량이송재료 조회 완료";
				slabUtils.printLog(logId, szMsg, "SL");
				//-----------------------------------------------------------------------------------------------------------------
			}else{
				slabUtils.printLog(logId, ">> 차량상태 : [" + szYD_CAR_PROG_STAT+"] 상차완료 이외  case", "SL");
				
				szMsg = "[JSP Session - "+methodNm+"] 대상재 조회 시작" ;
				slabUtils.printLog(logId, szMsg, "SL");
				
				recPara.setField("YD_CAR_USE_GP",		szYD_CAR_USE_GP);
				recPara.setField("TRN_EQP_CD",			szTRN_EQP_CD);
				recPara.setField("CAR_NO",				szCAR_NO);
				recPara.setField("CARD_NO",				szCARD_NO);
				
				slabUtils.printLog(logId, "발지 개소코드[SPOS_WLOC_CD]	:" + szSPOS_WLOC_CD, "SL");		// 상차지 개소코드
				slabUtils.printLog(logId, "야드차량사용구분[YD_CAR_USE_GP]	:" + szYD_CAR_USE_GP, "SL");	// [G]출하, [L]구내운송
				// 슬라브 야드의 경우 분리
				if(szSPOS_WLOC_CD.equals("DKY21") //1후판-옥내 Yard
				 ||szSPOS_WLOC_CD.equals("DWY22") //2후판-옥내 Yard
				 ||szSPOS_WLOC_CD.equals("DHY21") //연주-옥내 Yard
				 ||szSPOS_WLOC_CD.equals("DJY25") //부두-통합 Yard (비상야드추가)
				 ||szSPOS_WLOC_CD.equals("DYY15") //2냉연 비상야드
				 ||szSPOS_WLOC_CD.equals("BSY01") //서문 슬라브 야드(건설 제작장 부지)
				 ||szSPOS_WLOC_CD.equals("BSY02") //특)정정 옥외 슬라브 야드
				 ||szSPOS_WLOC_CD.equals("BSY03") //철분말 공장 슬라브 야드
				 ||szSPOS_WLOC_CD.equals("DKY23") //1후판-극후물 냉각대
				 ||szSPOS_WLOC_CD.equals("DWY23") //2후판-극후물 냉각대
				 ||szSPOS_WLOC_CD.equals("DJY24") //2열연-변형재열재 적치장
				){	
				//-------------------------------------------------------------------------------------	
				//	 * 이적상차인경우 기존 작업예약과 JOIN하여 보여주던 기능은 맞지 않음.
				//	 * 이적을 두번에 나눠서 하면 한 상차 작업에 작업예약이 2개
				//	 * 따라서, 구내운송에 상차완료 전상태이고, 상차 작업예약이 없거나, 스케줄 코드 검색하여 이적상차인경우 차량이송재료 table 에서 조회.
				//------------------------------------------------------------------------------------- 

					if("L".equals(szYD_CAR_USE_GP) && ( // 구내운송이고 상차인 경우 상차완료 전까지
							   PSlabYdConstant.YD_CARLD_LEV.equals(szYD_CAR_PROG_STAT)			//상차출발
							|| PSlabYdConstant.YD_CARLD_ARR.equals(szYD_CAR_PROG_STAT)			//상차도착
							|| PSlabYdConstant.YD_CARLD_CHK.equals(szYD_CAR_PROG_STAT)			//상차검수
							|| PSlabYdConstant.YD_CARLD_ST.equals (szYD_CAR_PROG_STAT))){		//상차개시
						
						slabUtils.printLog(logId, ">> 차량상태 : 구내운송이고 상차인 경우 상차완료 전까지(상차출발,상차도착,상차검수,상차개시)", "SL");
						slabUtils.printLog(logId, "상차작업예약ID(YD_CARLD_WRK_BOOK_ID):" + szYD_CARLD_WRK_BOOK_ID, "SL");
						if("".equals(szYD_CARLD_WRK_BOOK_ID)){
							slabUtils.printLog(logId, ">> 상차작업예약ID가 없으므로 차량작업재료로 조회", "SL");
							
							//차량작업재료로 조회
							recPara.setField("YD_CAR_SCH_ID",		szYD_CAR_SCH_ID);
							recPara.setField("TRN_EQP_CD",		    szTRN_EQP_CD);
							/*
								select COUNT(*) OVER() AS CNT
								      ,SUM(ST.YD_MTL_WT) OVER() AS YD_MTL_WT_SUM
								      ,CM.YD_CAR_SCH_ID
								      ,CM.STL_NO
								      ,SL.YD_STK_COL_GP
								      ,SL.YD_STK_BED_NO
								      ,SL.YD_STK_LYR_NO
								      ,SL.YD_STK_COL_GP || SL.YD_STK_BED_NO || SL.YD_STK_LYR_NO AS YD_STR_LOC
								      ,ST.ORD_NO
								      ,ST.ORD_DTL
								      ,CASE WHEN ST.ORD_NO || '-' || ST.ORD_DTL = '-' THEN '' ELSE ST.ORD_NO || '-' || ST.ORD_DTL END AS ORD_NO_DTL
								      ,ST.ORD_YEOJAE_GP
								      ,ST.STL_PROG_CD
								      ,ST.OVERALL_STAMP_GRADE
								      ,ST.GOODS_GRADE
								      ,ST.YD_MTL_T
								      ,ST.YD_MTL_W
								      ,ST.YD_MTL_L
								      ,ST.YD_MTL_WT
								      ,ST.YD_MTL_ITEM
								      ,SUBSTR(ST.YD_MTL_ITEM, 1, 1) AS YD_MTL_ITEM_GP
								      ,TO_CHAR(ST.YD_MTL_T, '9,999') || ' X ' || TO_CHAR(ST.YD_MTL_W, '9,999') || ' X ' || TO_CHAR(DECODE(SUBSTR(ST.YD_MTL_ITEM, 1, 1), 'C', ST.COIL_OUTDIA, ST.YD_MTL_L) , '99,999') AS GOODS_SIZE
								      ,ST.YD_AIM_RT_GP
								      ,ST.TRANS_ORD_DATE
								      ,ST.TRANS_ORD_SEQNO
								      ,CASE WHEN ST.TRANS_ORD_DATE || '-' || ST.TRANS_ORD_SEQNO = '-' THEN '' ELSE ST.TRANS_ORD_DATE || '-' || ST.TRANS_ORD_SEQNO END  AS TRANS_ORD_DATE_SEQNO
								      ,ST.DEST_CD
								      ,ST.CUST_CD
								      ,ST.DEMANDER_CD
								      ,ST.DEST_TEL_NO
								      ,ST.DIST_SHIPASSIGN_GP
								      ,ST.EXPORT_SHIP_SET_NO
								      ,ST.SHIPASSIGN_WORD_DATE
								      ,ST.SHIPASSIGN_WORD_SEQNO
								      ,ST.SHIP_NAME
								      ,ST.BERTH_NO
								      ,ST.SAILNO
								      ,ST.YD_AIM_YD_GP
								      ,ST.YD_AIM_BAY_GP
								      ,ST.HCR_GP
								--스카핑여부,규격약호,구입슬라브추가 
								       ,
								       CASE
								         WHEN ( ST.SCARFING_YN = 'Y'
								        AND    NVL(ST.SCARFING_DONE_YN, 'N') = 'N') THEN '*'
								         ELSE ''
								       END AS SCARFING ,
								       ST.SPEC_ABBSYM ,
								       (SELECT X.BUY_SLAB_NO
								        FROM   TB_QM_BUYSLABINFO X
								        WHERE  X.MSLAB_NO = ST.STL_NO) AS GU_SLAB_NO
								from TB_YD_CARSCH CS
								    ,TB_YD_CARFTMVMTL CM
								    ,TB_YD_STKLYR SL
								    ,TB_YD_STOCK ST
								where 1=1
								  and CS.YD_CAR_SCH_ID = CM.YD_CAR_SCH_ID
								  and CM.STL_NO = SL.STL_NO
								  and SL.STL_NO = ST.STL_NO
								  and CS.TRN_EQP_CD = :V_TRN_EQP_CD
								  and CS.DEL_YN = 'N'
								  and CM.DEL_YN = 'N'
								  and SL.DEL_YN = 'N'
							 */
							jsOutSet = commDao.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getWorkBookMtlStockByTrnEqpCdManual", logId, methodNm, "차량스케줄조회");
							
							szMsg = "[JSP Session - "+methodNm+"] 차량작업재료로 조회  완료_ YD_CARLD_WRK_BOOK_ID";
						}else{
							/* 2021-03-16 민종근
							 * 이적상차 관련 로직이 있었으나 필요없는 로직이므로 삭제 - PYS 부장님 확인
							 * ******************************************* */
							//intRtnVal = 0;	// 작업재료 결과 없음 처리함

							slabUtils.printLog(logId, "--szTRN_EQP_CD--"+szTRN_EQP_CD, "SL");
							/* 이적상차인지 검사
							 * SELECT CASE WHEN YD_SCH_CD LIKE 'A_YD__MM' THEN 'Y'
								            ELSE 'N' END AS M_WRK_GP
								  FROM TB_YD_WRKBOOK
								 WHERE YD_WBOOK_ID = (
								    SELECT YD_CARLD_WRK_BOOK_ID
								    FROM TB_YD_CARSCH
								    WHERE 1=1 
								      AND TRN_EQP_CD = :V_TRN_EQP_CD
								      AND DEL_YN     = 'N')
							 * 
									//intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, temp, 272);
							 */
							 slabUtils.printLog(logId, "이적상차인지 검사", "SL"); 
							 JDTORecordSet temp  = commDao.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getWorkBookManualYN", logId, methodNm, "이적상차인지 검사");
							 
							 intRtnVal = outRecSet.size();
							
							 if(outRecSet.size()>0){
								String manualYN = temp.getRecord(0).getFieldString("M_WRK_GP");
								szMsg = "[JSP Session - "+methodNm+"] 이적상차 여부 : "+ manualYN +" (getWorkBookManualYN)" ;
								slabUtils.printLog(logId, szMsg, "SL");
								
								if("Y".equals(manualYN)){
									recPara.setField("YD_CAR_SCH_ID",		szYD_CAR_SCH_ID);
									//getYdCarftmvmtl12
									/*
									 * select COUNT(*) OVER() AS CNT
										      ,SUM(ST.YD_MTL_WT) OVER() AS YD_MTL_WT_SUM
										      ,CM.YD_CAR_SCH_ID
										      ,CM.STL_NO
										      ,SL.YD_STK_COL_GP
										      ,SL.YD_STK_BED_NO
										      ,SL.YD_STK_LYR_NO
										      ,SL.YD_STK_COL_GP || SL.YD_STK_BED_NO || SL.YD_STK_LYR_NO AS YD_STR_LOC
										      ,ST.ORD_NO
										      ,ST.ORD_DTL
										      ,CASE WHEN ST.ORD_NO || '-' || ST.ORD_DTL = '-' THEN '' ELSE ST.ORD_NO || '-' || ST.ORD_DTL END AS ORD_NO_DTL
										      ,ST.ORD_YEOJAE_GP
										      ,ST.STL_PROG_CD
										      ,ST.OVERALL_STAMP_GRADE
										      ,ST.GOODS_GRADE
										      ,ST.YD_MTL_T
										      ,ST.YD_MTL_W
										      ,ST.YD_MTL_L
										      ,ST.YD_MTL_WT
										      ,ST.YD_MTL_ITEM
										      ,SUBSTR(ST.YD_MTL_ITEM, 1, 1) AS YD_MTL_ITEM_GP
										      ,TO_CHAR(ST.YD_MTL_T, '9,999') || ' X ' || TO_CHAR(ST.YD_MTL_W, '9,999') || ' X ' || TO_CHAR(DECODE(SUBSTR(ST.YD_MTL_ITEM, 1, 1), 'C', ST.COIL_OUTDIA, ST.YD_MTL_L) , '99,999') AS GOODS_SIZE
										      ,ST.YD_AIM_RT_GP
										      ,ST.TRANS_ORD_DATE
										      ,ST.TRANS_ORD_SEQNO
										      ,CASE WHEN ST.TRANS_ORD_DATE || '-' || ST.TRANS_ORD_SEQNO = '-' THEN '' ELSE ST.TRANS_ORD_DATE || '-' || ST.TRANS_ORD_SEQNO END  AS TRANS_ORD_DATE_SEQNO
										      ,ST.DEST_CD
										      ,ST.CUST_CD
										      ,ST.DEMANDER_CD
										      ,ST.DEST_TEL_NO
										      ,ST.DIST_SHIPASSIGN_GP
										      ,ST.EXPORT_SHIP_SET_NO
										      ,ST.SHIPASSIGN_WORD_DATE
										      ,ST.SHIPASSIGN_WORD_SEQNO
										      ,ST.SHIP_NAME
										      ,ST.BERTH_NO
										      ,ST.SAILNO
										      ,ST.YD_AIM_YD_GP
										      ,ST.YD_AIM_BAY_GP
										      ,ST.HCR_GP
										--스카핑여부,규격약호,구입슬라브추가 
										       ,
										       CASE
										         WHEN ( ST.SCARFING_YN = 'Y'
										        AND    NVL(ST.SCARFING_DONE_YN, 'N') = 'N') THEN '*'
										         ELSE ''
										       END AS SCARFING ,
										       ST.SPEC_ABBSYM ,
										       (SELECT X.BUY_SLAB_NO
										        FROM   TB_QM_BUYSLABINFO X
										        WHERE  X.MSLAB_NO = ST.STL_NO) AS GU_SLAB_NO
										from TB_YD_CARSCH CS
										    ,TB_YD_CARFTMVMTL CM
										    ,TB_YD_STKLYR SL
										    ,TB_YD_STOCK ST
										where 1=1
										  and CS.YD_CAR_SCH_ID = CM.YD_CAR_SCH_ID
										  and CM.STL_NO = SL.STL_NO
										  and SL.STL_NO = ST.STL_NO
										  and CS.TRN_EQP_CD = :V_TRN_EQP_CD
										  and CS.DEL_YN = 'N'
										  and CM.DEL_YN = 'N'
										  and SL.DEL_YN = 'N'
									 * 
										//intRtnVal	= ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 271);
									 */
									jsOutSet = commDao.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getWorkBookMtlStockByTrnEqpCdManual", logId, methodNm, "00");
									intRtnVal = jsOutSet.size();

								}
								else{
									/*
									 * SELECT COUNT(*) OVER() AS CNT ,
										       SUM(YD_MTL_WT) OVER() AS YD_MTL_WT_SUM ,
										       A.YD_WBOOK_ID ,
										       A.TRN_EQP_CD ,
										       A.CAR_NO ,
										       A.CARD_NO ,
										       A.YD_CAR_USE_GP ,
										       B.STL_NO ,
										       B.YD_STK_COL_GP ,
										       B.YD_STK_BED_NO ,
										       B.YD_STK_LYR_NO ,
										       B.YD_STK_COL_GP || B.YD_STK_BED_NO || B.YD_STK_LYR_NO AS YD_STR_LOC ,
										       B.YD_UP_COLL_SEQ ,
										       C.ORD_NO ,
										       C.ORD_DTL ,
										       CASE WHEN C.ORD_NO || '-' || C.ORD_DTL = '-' THEN '' ELSE C.ORD_NO || '-' || C.ORD_DTL END AS ORD_NO_DTL ,
										       C.ORD_YEOJAE_GP ,
										       C.STL_PROG_CD ,
										       C.OVERALL_STAMP_GRADE ,
										       C.GOODS_GRADE ,
										       C.YD_MTL_T ,
										       C.YD_MTL_W ,
										       C.YD_MTL_L ,
										       C.YD_MTL_WT ,
										       C.YD_MTL_ITEM ,
										       SUBSTR(C.YD_MTL_ITEM, 1, 1) AS YD_MTL_ITEM_GP ,
										       TO_CHAR(C.YD_MTL_T, '9,999') || ' X ' || TO_CHAR(C.YD_MTL_W, '9,999') || ' X ' || TO_CHAR(DECODE(SUBSTR(C.YD_MTL_ITEM, 1, 1), 'C', C.COIL_OUTDIA, C.YD_MTL_L) , '99,999') AS GOODS_SIZE ,
										       C.YD_AIM_RT_GP ,
										       C.TRANS_ORD_DATE ,
										       C.TRANS_ORD_SEQNO ,
										       C.TRANS_ORD_DATE || '-' || C.TRANS_ORD_SEQNO AS TRANS_ORD_DATE_SEQNO,
										       C.DEST_CD ,
										       C.CUST_CD ,
										       C.DEMANDER_CD ,
										       C.DEST_TEL_NO ,
										       C.DIST_SHIPASSIGN_GP ,
										       C.EXPORT_SHIP_SET_NO ,
										       C.SHIPASSIGN_WORD_DATE ,
										       C.SHIPASSIGN_WORD_SEQNO ,
										       C.SHIP_NAME ,
										       C.BERTH_NO ,
										       C.SAILNO ,
										       C.YD_AIM_YD_GP ,
										       C.YD_AIM_BAY_GP ,
										       C.HCR_GP
										--스카핑여부,규격약호,구입슬라브추가 
										       ,
										       CASE
										         WHEN ( C.SCARFING_YN = 'Y'
										        AND    NVL(C.SCARFING_DONE_YN, 'N') = 'N') THEN '*'
										         ELSE ''
										       END AS SCARFING ,
										       C.SPEC_ABBSYM ,
										       (SELECT X.BUY_SLAB_NO
										        FROM   TB_QM_BUYSLABINFO X
										        WHERE  X.MSLAB_NO = C.STL_NO) AS GU_SLAB_NO
										FROM   USRYDA.TB_YD_WRKBOOK A,
										       USRYDA.TB_YD_WRKBOOKMTL B,
										       USRYDA.TB_YD_STOCK C
										WHERE  A.YD_WBOOK_ID = B.YD_WBOOK_ID
										AND    B.STL_NO = C.STL_NO
										AND    ( ('L' = :V_YD_CAR_USE_GP
										                AND    A.TRN_EQP_CD = :V_TRN_EQP_CD
										                AND    A.YD_CAR_USE_GP = :V_YD_CAR_USE_GP)
										        OR     ( 'G' = :V_YD_CAR_USE_GP
										                AND    A.CAR_NO = :V_CAR_NO
										                AND    A.CARD_NO = :V_CARD_NO
										                AND    A.YD_CAR_USE_GP = :V_YD_CAR_USE_GP) )
										AND    A.DEL_YN ='N'
										AND    B.DEL_YN ='N'
										AND    C.DEL_YN ='N'
										ORDER BY YD_UP_COLL_SEQ ASC 
									 * 
										//intRtnVal  = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 26);
									 */	
									jsOutSet = commDao.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getWorkBookMtlStockByTrnEqpCd", logId, methodNm, "00");
									intRtnVal = jsOutSet.size();

									slabUtils.printLog(logId, ",,,,작업상세,,,,", "SL");
								}
							 } else {
									szMsg = "[JSP Session - "+methodNm+"] 이적상차인지 검사 오류.";
									slabUtils.printLog(logId, szMsg, "SL");	
									//
									gdRtn = OperateGridData.cloneResponseGridData(gdReq);
									return slabUtils.jdtoRecordToGridData(gdRtn, jsOutSet.toList(), gdReq);
							 }
						}		//--이적상차인 경우 -end- 
						
					}else{
						
						szMsg = "[JSP Session - "+methodNm+"] else 조건 [구내운송이고 상차인 경우 상차완료 전까지 (상차출발,상차도착,상차검수,상차개시)] ---아닌경우---  " ;
						slabUtils.printLog(logId, szMsg, "SL");

						/*
							SELECT COUNT(*) OVER()       AS CNT 
							      ,SUM(YD_MTL_WT) OVER() AS YD_MTL_WT_SUM
							      ,A.YD_WBOOK_ID 
							      ,A.TRN_EQP_CD 
							      ,A.CAR_NO 
							      ,A.CARD_NO 
							      ,A.YD_CAR_USE_GP 
							      ,B.STL_NO 
							      ,B.YD_STK_COL_GP 
							      ,B.YD_STK_BED_NO 
							      ,B.YD_STK_LYR_NO 
							      ,B.YD_STK_COL_GP || B.YD_STK_BED_NO || B.YD_STK_LYR_NO AS YD_STR_LOC 
							      ,B.YD_UP_COLL_SEQ 
							      ,C.ORD_NO 
							      ,C.ORD_DTL 
							      ,CASE WHEN C.ORD_NO || '-' || C.ORD_DTL = '-' THEN '' ELSE C.ORD_NO || '-' || C.ORD_DTL END AS ORD_NO_DTL 
							      ,C.ORD_YEOJAE_GP 
							      ,C.STL_PROG_CD 
							      ,C.OVERALL_STAMP_GRADE 
							      ,C.GOODS_GRADE 
							      ,C.YD_MTL_T 
							      ,C.YD_MTL_W 
							      ,C.YD_MTL_L 
							      ,C.YD_MTL_WT 
							      ,C.YD_MTL_ITEM 
							      ,SUBSTR(C.YD_MTL_ITEM, 1, 1) AS YD_MTL_ITEM_GP 
							      ,TO_CHAR(C.YD_MTL_T, '9,999') || ' X ' || TO_CHAR(C.YD_MTL_W, '9,999') || ' X ' || TO_CHAR(DECODE(SUBSTR(C.YD_MTL_ITEM, 1, 1), 'C', C.COIL_OUTDIA, C.YD_MTL_L) , '99,999') AS GOODS_SIZE 
							      ,C.YD_AIM_RT_GP 
							      ,C.TRANS_ORD_DATE 
							      ,C.TRANS_ORD_SEQNO 
							      ,C.TRANS_ORD_DATE || '-' || C.TRANS_ORD_SEQNO AS TRANS_ORD_DATE_SEQNO
							      ,C.DEST_CD 
							      ,C.CUST_CD 
							      ,C.DEMANDER_CD 
							      ,C.DEST_TEL_NO 
							      ,C.DIST_SHIPASSIGN_GP 
							      ,C.EXPORT_SHIP_SET_NO 
							      ,C.SHIPASSIGN_WORD_DATE 
							      ,C.SHIPASSIGN_WORD_SEQNO 
							      ,C.SHIP_NAME 
							      ,C.BERTH_NO 
							      ,C.SAILNO 
							      ,C.YD_AIM_YD_GP 
							      ,C.YD_AIM_BAY_GP 
							      ,C.HCR_GP
							--스카핑여부,규격약호,구입슬라브추가 
							      ,CASE WHEN ( C.SCARFING_YN = 'Y' AND NVL(C.SCARFING_DONE_YN, 'N') = 'N') THEN '*'  ELSE ''  END AS SCARFING 
							      ,C.SPEC_ABBSYM 
							      ,(SELECT X.BUY_SLAB_NO    FROM   TB_QM_BUYSLABINFO X     WHERE  X.MSLAB_NO = C.STL_NO) AS GU_SLAB_NO
							 FROM  USRYDA.TB_YD_WRKBOOK A
							      ,USRYDA.TB_YD_WRKBOOKMTL B
							      ,USRYDA.TB_YD_STOCK C
							WHERE  A.YD_WBOOK_ID = B.YD_WBOOK_ID
							AND    B.STL_NO      = C.STL_NO
							AND    (       ('L' = :V_YD_CAR_USE_GP
							                AND    A.TRN_EQP_CD    = :V_TRN_EQP_CD
							                AND    A.YD_CAR_USE_GP = :V_YD_CAR_USE_GP)
							        OR     ('G' = :V_YD_CAR_USE_GP
							                AND    A.CAR_NO        = :V_CAR_NO
							                AND    A.CARD_NO       = :V_CARD_NO
							                AND    A.YD_CAR_USE_GP = :V_YD_CAR_USE_GP) )
							AND    A.DEL_YN ='N'
							AND    B.DEL_YN ='N'
							AND    C.DEL_YN ='N'
							ORDER BY YD_UP_COLL_SEQ ASC 
						 */	
						jsOutSet = commDao.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getWorkBookMtlStockByTrnEqpCd", logId, methodNm, "00");
						intRtnVal = jsOutSet.size();
					}
				}//----
				
				szMsg = "[JSP Session - "+methodNm+"] 대상재 조회 완료";
				slabUtils.printLog(logId, szMsg, "SL");
			}
		
			if (intRtnVal <= 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session - "+methodNm+"] 작업재료 조회 오류발생 1 : 반환값 - " + intRtnVal;
					slabUtils.printLog(logId, szMsg, "SL");
					
				}else if (intRtnVal == 0) {
					szMsg = "[JSP Session - "+methodNm+"] 작업재료 조회 대상재가 존재하지 않음 : 반환값 - " + intRtnVal;
					slabUtils.printLog(logId, szMsg, "SL");							
				} else {
					szMsg = "[JSP Session - "+methodNm+"] 작업재료 조회 오류발생 2 : 반환값 - " + intRtnVal;
					slabUtils.printLog(logId, szMsg, "SL");							
				}
				
				gdRtn = OperateGridData.cloneResponseGridData(gdReq);
				return slabUtils.jdtoRecordToGridData(gdRtn, jsOutSet.toList(), gdReq);

			}
			
			szMsg = "["+methodNm+"] 작업재료 조회 성공 : 레코드 수 - " + intRtnVal;
			slabUtils.printLog(logId, szMsg, "SL");
			
			slabUtils.printLog(logId, methodNm , "S-");
			
			jsOutSet.first();
			//UI로 반환 할 Grid data 를 생성 
			gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jsOutSet.toList(), gdReq);
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 	



	/**
	 * [A] 오퍼레이션명: 이송LOT편성LIST-동일상차처리
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송Lot편List 
	 * PYS 2020-10-12    
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord uptSameYdPrepSch(GridData gdReq) throws DAOException {
		
		String methodNm 			= "후판슬라브야드 이송LOT편성LIST-동일상차처리[PSlabYdMvCarSeEJB.uptSameYdPrepSch] < " + gdReq.getNavigateValue();
		String logId 				= gdReq.getIPAddress();
		
		try{
			slabUtils.printLog(logId, "▶▷▶▷  "+ methodNm + " 시작 ◁◀◁◀", "SL");
			String rtnEcod          = "";
			String szMsg            = "";
			int    lCnt     		= 0;
			
			String sYdPrepSchId		= "";	// 야드준비스케쥴ID(KEY)
			String sPriYdPrepSchId	= "";	// 동일상차야드준비스케쥴ID
			
			
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printParam(logId, gdReq,   "S+");
			
			String sModifier   = slabUtils.trim(gdReq.getParam("userid" )); 			//수정자(MODIFIER)
			JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set
			
			slabUtils.printLog(logId, "▶  에러체크 시작 ◀", "SL");
			int rowCnt   = gdReq.getHeader("CHECK").getRowCount();

			if(rowCnt == 0) {
				szMsg  = "작업대상건이 없습니다. ";
				slabUtils.printLog(logId, szMsg, "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg);	
				return jrRtn;
			}
			slabUtils.printLog(logId, "▶  에러체크 이상없음. 완료 ◀", "SL");
			
			
			
			slabUtils.printLog(logId, "▶  신규 동일상차야드준비스케줄ID 채번 ◀", "SL");
			/* 동일상차야드준비스케줄ID 채번 - com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getPriYdPrepSchId
				SELECT TO_CHAR(SYSDATE,'YYYYMMDD') || TO_CHAR(USRYDA.YD_PRI_PREP_SCH_SEQ.NEXTVAL,'FM0000') AS SEQ_ID
				  FROM DUAL */
			sPriYdPrepSchId = commDao.getSeqId(logId, methodNm, "PriYdPrepSch");
			slabUtils.printLog(logId, "▶  신규 동일상차야드준비스케줄ID : " + sPriYdPrepSchId + "◀", "SL");
			
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				sYdPrepSchId = slabUtils.getValue(gdReq, "YD_PREP_SCH_ID"   ,ii);	// 화면에서 선택된 야드준비스케쥴ID(LOT번호)
				
				slabUtils.printLog(logId, "▶ 준비스케쥴ID			: " + sYdPrepSchId		, "SL");
				slabUtils.printLog(logId, "▶ 동일상차야드준비스케줄ID		: " + sPriYdPrepSchId	, "SL");
				slabUtils.printLog(logId, "▶ 야드차상위치코드			: " + (ii+1)				, "SL");
				
				jrParam.setField("PRI_YD_PREP_SCH_ID"	, sPriYdPrepSchId	);	// 동일상차야드준비스케쥴ID
				jrParam.setField("YD_CAR_UPP_LOC_CD"	, ""+(ii+1)			);	// 야드차상위치코드
				jrParam.setField("YD_PREP_SCH_ID"		, sYdPrepSchId		);	// YD_PREP_SCH_ID
				
				/* 동일상차LOT편성사전작업
				UPDATE USRYDA.TB_YD_PREPSCH -- YD_준비스케쥴
				   SET PRI_YD_PREP_SCH_ID  = :V_PRI_YD_PREP_SCH_ID               -- 동일상차야드준비스케쥴ID
				     , YD_CAR_UPP_LOC_CD   = LPAD(:V_YD_CAR_UPP_LOC_CD, 2, '0')  -- 야드차상위치코드
				     , MODIFIER            = :V_MODIFIER                         -- 수정자
				     , MOD_DDTT            = SYSDATE                             -- 수정일
				 WHERE YD_PREP_SCH_ID      = :V_YD_PREP_SCH_ID                   -- KEY:야드준비스케쥴ID
				 */
				lCnt = commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updYdPrepschidPri", logId, methodNm, "동일상차LOT편성사전작업");
				
				if (lCnt <= 0) {
					rtnEcod  = "0";
					szMsg = "처리대상건이 없습니다. ";
					slabUtils.printLog(logId, szMsg, "SL");
					jrRtn.setField("RTN_CD"	, rtnEcod);
					jrRtn.setField("RTN_MSG", szMsg);	
					return jrRtn;
				} else {
					rtnEcod  = "1";
					szMsg = "상차lot묶음작업이  정상처리되었습니다. ";
					slabUtils.printLog(logId, szMsg, "SL");
				}
			}
			
			slabUtils.printLog(logId, "▶ 작업결과:" +rtnEcod + ">" + szMsg, "S-");
			slabUtils.printLog(logId, "▶▷▶▷  후판슬라브야드 동일상차LOT편성 종료 ◁◀◁◀", "SL");
			
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, rtnEcod);
			jrRtn.setField("RTN_MSG", szMsg);	
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * [A] 오퍼레이션명: 이송LOT편성LIST-동일상차취소
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송Lot편List 
	 * PYS 2020-10-12    
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord uptCnclYdPrepSch(GridData gdReq) throws DAOException {
		
		String methodNm = "후판슬라브야드 이송LOT편성LIST-동일상차취소[PSlabYdMvCarSeEJB.uptSameYdPrepSch] < " + gdReq.getNavigateValue();
		String logId 	= gdReq.getIPAddress();
		
		try{
			slabUtils.printLog(logId, "▶▷▶▷  동일상차취소 시작 ◁◀◁◀", "SL");
			String rtnEcod          	= "";
			String szMsg            	= "";
			int    lCnt     			= 0;
			
			String sPRI_YD_PREP_SCH_ID	= "";	// 동일상차야드준비스케쥴ID(KEY)
			
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printParam(logId, gdReq,   "S+");
			
			String sModifier   = slabUtils.trim(gdReq.getParam("userid" )); 			//수정자(MODIFIER)
			JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set
			
			slabUtils.printLog(logId, "▶  에러체크 시작 ◀", "SL");
			int rowCnt   = gdReq.getHeader("CHECK").getRowCount();

			if(rowCnt == 0) {
				szMsg  = "작업대상건이 없습니다. ";
				slabUtils.printLog(logId, szMsg, "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg);	
				return jrRtn;
			}
			slabUtils.printLog(logId, "▶  에러체크 이상없음. 완료 ◀", "SL");
			
			// 선택된 row수 만큼 loop -> 화면에서 하나만 선택 가능하므로 실제 로직은 1회만 실행된다.
			for (int ii = 0; ii < rowCnt; ii++) {
				
				sPRI_YD_PREP_SCH_ID = slabUtils.getValue(gdReq, "PRI_YD_PREP_SCH_ID"   ,ii);	// 화면에서 선택된 LOT의 동일상차야드준비스케쥴ID
				slabUtils.printLog(logId, "▶ 초기화 대상 동일상차야드준비스케쥴ID	: " + sPRI_YD_PREP_SCH_ID		, "SL");
				
				jrParam.setField("PRI_YD_PREP_SCH_ID"	, sPRI_YD_PREP_SCH_ID	);	// 동일상차야드준비스케쥴ID
				
				/* 동일상차LOT편성 초기화
				UPDATE USRYDA.TB_YD_PREPSCH -- YD_준비스케쥴
				   SET PRI_YD_PREP_SCH_ID  = NULL                   -- 동일상차야드준비스케쥴ID
				     , YD_CAR_UPP_LOC_CD   = NULL                   -- 야드차상위치코드
				     , MODIFIER            = :V_MODIFIER            -- 수정자
				     , MOD_DDTT            = SYSDATE                -- 수정일
				 WHERE PRI_YD_PREP_SCH_ID  = :V_PRI_YD_PREP_SCH_ID  -- KEY:동일상차야드준비스케쥴ID */
				lCnt = commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updInitYdPrepschidPri", logId, methodNm, "동일상차LOT편성 초기화");
				
				if (lCnt <= 0) {
					rtnEcod  = "0";
					szMsg = "처리대상건이 없습니다. ";
					slabUtils.printLog(logId, szMsg, "SL");
					jrRtn.setField("RTN_CD"	, rtnEcod);
					jrRtn.setField("RTN_MSG", szMsg);	
					return jrRtn;
				} else {
					rtnEcod  = "1";
					szMsg = "동일상차취소 작업이  정상처리되었습니다. ";
					slabUtils.printLog(logId, szMsg, "SL");
				}
			}
			
			slabUtils.printLog(logId, "▶ 작업결과:" +rtnEcod + ">" + szMsg, "S-");
			slabUtils.printLog(logId, "▶▷▶▷  후판슬라브야드 동일상차취소 종료 ◁◀◁◀", "SL");
			
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, rtnEcod);
			jrRtn.setField("RTN_MSG", szMsg);	
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	   /**
		 * 상차위치 수정				4-1			
		 * PYS 2020-11-13
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return JDTORecordSet
		 * @throws DAOException
		 */
		public JDTORecord carLiftPosSet(GridData inDto) throws DAOException {
			String methodNm       	= "상차위치 수정[PSlabYdMvCarSeEJB.carLiftPosSet] < " + inDto.getNavigateValue();
			String logId    		= inDto.getIPAddress();
			
			/*--------------------------------------------
			 * 1. 작업예약 생성시 오류 처리 
			 * 2. 전문8번 발송 처리 
			 * 3. 예약 재처리 기동    :  
			 *--------------------------------------------*/
			try{
				String sModifier     = slabUtils.trim(inDto.getParam("YD_USER_ID" )); 		//수정자(MODIFIER)
				JDTORecord jrRtn     = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
				int nRtnVal  			= 0;
				String szLogMsg       	= "";
				String szRtnMsg 		= "";
				String lszStlNo 		= "";
				String lszYdStkBedNo 	= "";
				String lszYdStkLyrNo 	= "";
				String lszYdCarSchId 	= "";
				String lszYdCarstopLoc = "";
				//String szJobContinue    = "N"; //작업여부 
				//String ydStkColGp		= "";
				String ydWbookId 		= "";

				JDTORecord recInputPara   = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord recInputPara2  = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord recInputPara3  = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord jrParam        = slabUtils.getParam(logId, methodNm, sModifier);
				
				slabUtils.printLog(logId, methodNm, "S+");
				
				int rowCnt = inDto.getHeader("CHECK").getRowCount();
				slabUtils.printLog(logId, "작업자:" + sModifier + ", 대상건수:" + rowCnt, "SL");
				
				String sTrnEqpCd     = slabUtils.trim(inDto.getParam("TRN_EQP_CD" ));
				jrParam.setField("TRN_EQP_CD"	, sTrnEqpCd ); 
				/* com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getListtrnEqpschL_1
				SELECT YD_CARLD_WRK_BOOK_ID AS WBOOK_ID
				      ,YD_CAR_SCH_ID
				  FROM TB_YD_CARSCH
				 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				   AND YD_CAR_PROG_STAT = '2'
				   AND DEL_YN = 'N'
				*/
				JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getListtrnEqpschL_1", logId, methodNm, "차량스케줄 ,작업예약ID 조회");
				if (jsCarSch.size() > 0) {
					ydWbookId  = slabUtils.trim(jsCarSch.getRecord(0).getFieldString("WBOOK_ID"));
					if(!"".equals(ydWbookId)) {
						szRtnMsg = "크레인작업 진행건입니다. 작업예약 생성 불가!";
						slabUtils.printLog(logId, szRtnMsg, "SL");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", szRtnMsg);
						return jrRtn;
					}
				}	
				String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdCarschByTrnEqpCd";
				jsCarSch = commDao.select(jrParam, queryId, logId, methodNm, "차량스케줄 조회");
				if (jsCarSch.size() == 1) {
					lszYdCarstopLoc = jsCarSch.getRecord(0).getFieldString("YD_CARUD_STOP_LOC");
				}

				
				for (int x = 0; x < rowCnt; x++) {
				//recPara = inDto[x];
					lszStlNo 		 = slabUtils.nvl(slabUtils.getValue(inDto, "STL_NO"       , x),"");		//재료번호
					lszYdCarSchId 	 = slabUtils.nvl(slabUtils.getValue(inDto, "YD_CAR_SCH_ID", x),""); 	//차량스케줄 
					lszYdStkBedNo 	 = slabUtils.nvl(slabUtils.getValue(inDto, "YD_STK_BED_NO", x),""); 	//차상위치
					lszYdStkLyrNo 	 = slabUtils.nvl(slabUtils.getValue(inDto, "YD_STK_LYR_NO", x),""); 	//단 

					slabUtils.printLog(logId, "재료번호:" 		+ lszStlNo,        	"SL");
					slabUtils.printLog(logId, "차량스케줄:" 	+ lszYdCarSchId,   	"SL");
					slabUtils.printLog(logId, "차량정지위치:" 	+ lszYdCarstopLoc, 	"SL");  //HIDDEN
					slabUtils.printLog(logId, "차상위치:" 		+ lszYdStkBedNo
							            	+ " 단:" 		+ lszYdStkLyrNo,    "SL");
					slabUtils.printLog(logId, "차량번호:" 		+ sTrnEqpCd, 		"SL"); 
					if("".equals(lszYdCarSchId)){
						szRtnMsg = "차량 스케줄 재료정보가 존재하지 않습니다";
						slabUtils.printLog(logId, szRtnMsg, "SL");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", szRtnMsg);
						return jrRtn;
					}
				
				// "단 / 저장위치" 정보를 Check 한다.
					if("".equals(lszYdStkBedNo) || "".equals(lszYdStkLyrNo)){
						szRtnMsg = "단 / 저장위치 정보가 올바르지 않습니다";
						slabUtils.printLog(logId,  szRtnMsg, "SL");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", szRtnMsg);
						return jrRtn;
					}
					recInputPara =  JDTORecordFactory.getInstance().create();
					
					
					recInputPara.setField("YD_CAR_SCH_ID"	, lszYdCarSchId);
					recInputPara.setField("STL_NO"			, lszStlNo);
					recInputPara.setField("YD_STK_BED_NO"	, lszYdStkBedNo ); //차상위치  String.valueOf(Integer.parseInt(lszYdStkBedNo))
	 				recInputPara.setField("YD_STK_LYR_NO"	, lszYdStkLyrNo );
					recInputPara.setField("MODIFIER"     	, sModifier);
					recInputPara.setField("DEL_YN"       	, "N");
					/*SELECT * FROM TB_YD_CRNWRKMTL WHERE DEL_YN = 'N'  AND STL_NO = :V_STL_NO */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getCrnWrkMtlSlabnoGet";
					JDTORecordSet jrCrns = commDao.select(recInputPara, queryId, logId, methodNm, "크레인작업 진행여부 검증");
					if(jrCrns.size() > 0) {
						szRtnMsg = "대상재가 크레인 작업 진행중입니다. (차상위치 수정 불가)";
						slabUtils.printLog(logId,  szRtnMsg, "SL");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", szRtnMsg);
						return jrRtn;
					}
					/*
						UPDATE TB_YD_CARFTMVMTL
						      SET MODIFIER          = :V_MODIFIER
						         ,MOD_DDTT          = SYSDATE
						         ,DEL_YN            = :V_DEL_YN
						         ,YD_STK_BED_NO     = :V_YD_STK_BED_NO
						         ,YD_STK_LYR_NO     = :V_YD_STK_LYR_NO
						  WHERE YD_CAR_SCH_ID       = :V_YD_CAR_SCH_ID
						    AND STL_NO              = :V_STL_NO
						    
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdCarftmvmtl01";
					nRtnVal = commDao.update(recInputPara, queryId, logId, methodNm, "차량이송재료 UPDATE");
					
					if(nRtnVal<0){
						szRtnMsg = "YD_차량이송재료 변경 실패 (차상위치,단 확인)";
						slabUtils.printLog(logId,  szRtnMsg, "SL");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", szRtnMsg);
						return jrRtn;
						
					}else if(nRtnVal ==0){
						szRtnMsg = "YD_차량이송재료 UPDATE 된 항목이 없습니다";
						slabUtils.printLog(logId, szRtnMsg, "SL");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", szRtnMsg);
						return jrRtn;
					}
					
					//기존위치 삭제
					recInputPara3.setField("STL_NO", lszStlNo);
					slabUtils.printLog(logId, "기존위치 삭제:" + lszStlNo, "SL");
					
					/* YD_차상위치 단정보 삭제
					UPDATE TB_YD_STKLYR
					 SET STL_NO = ''
					    ,YD_STK_LYR_MTL_STAT = 'E' 
					    ,MODIFIER            =  :V_MODIFIER
					    ,MOD_DDTT          =  SYSDATE 
					WHERE STL_NO = :V_STL_NO    
					 */
					nRtnVal = commDao.update(recInputPara3, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updateCarLayer", logId, methodNm, "차상위치 단정보 삭제 ");
					if(nRtnVal > 0) {
						szRtnMsg = "YD_차상위치 단정보 삭제 성공  [ " + lszYdCarSchId +" ][" + lszStlNo +"]";
						slabUtils.printLog(logId, szRtnMsg, "SL");	
					} else {
						szRtnMsg = "YD_차상위치 단정보 삭제 실패 [저장품:"+ lszStlNo+"]";
						slabUtils.printLog(logId,  szRtnMsg, "SL");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", szRtnMsg);
					}
					
					//야드맵정보 수정
					//recInputPara2 =  JDTORecordFactory.getInstance().create();
					recInputPara2.setField("STL_NO"				, lszStlNo);
					recInputPara2.setField("YD_CARLD_STOP_LOC"	, lszYdCarstopLoc);
					recInputPara2.setField("YD_STK_BED_NO"		, slabUtils.stringPlusInt2(lszYdStkBedNo, 0)); //차상위치4
					recInputPara2.setField("YD_STK_LYR_NO"		, slabUtils.stringPlusInt (lszYdStkLyrNo, 0));
					slabUtils.printLog(logId, "[야드맵정보 수정]"+ lszStlNo + " -->" 
							+ lszYdCarstopLoc + "-" + lszYdStkBedNo + "-" + lszYdStkLyrNo, "SL");
					
					/*
						UPDATE TB_YD_STKLYR    
						   SET STL_NO        = :V_STL_NO
						     , YD_STK_LYR_MTL_STAT = 'C'
						 WHERE YD_STK_COL_GP = :V_YD_CARLD_STOP_LOC
						   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
						   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updateCarMapLayer";
					nRtnVal = commDao.update(recInputPara2, queryId, logId, methodNm, "야드맵정보삭제(초기화시)");

					szRtnMsg = "["+lszYdCarSchId+"]["	+ lszYdCarstopLoc                +"][" 
					         + " 차상위치:" 	+ String.valueOf(Integer.parseInt(lszYdStkBedNo))	//slabUtils.stringPlusInt2(lsz_YdStkBedNo, 0)
			            	 + " 단:" 		+ String.valueOf(Integer.parseInt(lszYdStkLyrNo));
					slabUtils.printLog(logId, szRtnMsg, "SL");							

				}
				
				/**********************************************************
				 * 차량작업 예정정보 송신 (YDY3L008)    <-- Y3YDL019 (요구)
				 **********************************************************/
				slabUtils.printLog(logId, "▶ 4.[" + sTrnEqpCd + "]차량의 차량작업 예정정보 (YDY3L008) I/F 전문을 생성합니다.", "SL");
				slabUtils.printLog(logId, "---------------------------------------------------------------", "SL");   //상차도 위치 
				jrParam.setField("SEARCH_FLAG" 		    , "1"				); //1:상차도, 2:차량스케쥴 ID	
				jrParam.setField("PT_LOAD_LOC" 			, lszYdCarstopLoc	); //상차도 위치
				jrParam.setField("YD_CAR_SCH_ID"		, ""				); //야드크레인스케쥴ID  (추가사항)
				jrParam.setField("TRN_EQP_CD"   		, ""		    	);
				
				jrRtn = slabUtils.addSndData(jrRtn, slabComm.procCarPlanInfo_Slab(jrParam)); 
				
				//if("Y".equals(szJobContinue)) { }

				//--
				szLogMsg = "[상차위치 수정] 정상 처리 되었습니다.";
				slabUtils.printLog(logId,  szLogMsg, "SL");
				jrRtn.setField("RTN_CD"	, "1");
				jrRtn.setField("RTN_MSG", szLogMsg);
				return jrRtn;
				
			} catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}
		}  
	   

		/**
		 * 이송대상재 저장품 수정			5-1
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws DAOException
		 */
		public JDTORecord updFrtoMoveMtlToStock (GridData inDto) throws DAOException {
			String methodNm = "이송대상재 저장품 수정[PSlabYdMvCarSeEJB.updFrtoMoveMtlToStock] < " + inDto.getNavigateValue();
			String logId    = inDto.getIPAddress();

			try {
				slabUtils.printLog(logId, methodNm, "S+");
				int       intRtnVal    	= 0;
				String    szMsg        	= null;
				String szStlNo 			= null;
				String szYdAimYdGp 		= null;
				String szYdAimBayGp 	= null;
				String szYdAimRtGp 		= null;
				String rtnEcod  		= "0";
				
				String sModifier   = slabUtils.trim(inDto.getParam("YD_USER_ID" )); 			//수정자(MODIFIER)
				JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
				JDTORecord recPara = slabUtils.getParam(logId, methodNm, sModifier);

				int rowCnt   = inDto.getHeader("CHECK").getRowCount();
				
				for(int x=0;x<rowCnt;x++){
					szStlNo      = slabUtils.nvl(slabUtils.getValue(inDto, "STL_NO"   		,x), "0"    );
					szYdAimYdGp  = slabUtils.nvl(slabUtils.getValue(inDto, "YD_AIM_YD_GP"   ,x), "0"    );
					szYdAimBayGp = slabUtils.nvl(slabUtils.getValue(inDto, "YD_AIM_BAY_GP"  ,x), "0"   	);
					szYdAimRtGp  = slabUtils.nvl(slabUtils.getValue(inDto, "YD_AIM_RT_GP"   ,x), "0"    );
					
					szMsg = "[JSP Session : " + methodNm + "] 재료번호[" + szStlNo + "], 목표야드[" + szYdAimYdGp + "], 목표동[" + szYdAimBayGp 
								   		   	            + "], 목표행선[" + szYdAimRtGp + "]";
					slabUtils.printLog(logId, szMsg, "SL");
					recPara.setField("STL_NO", 			szStlNo);
					recPara.setField("YD_AIM_YD_GP", 	szYdAimYdGp);
					recPara.setField("YD_AIM_BAY_GP", 	szYdAimBayGp);
					recPara.setField("YD_AIM_RT_GP", 	szYdAimRtGp);
					
					/*
					 * UPDATE TB_YD_STOCK 
						SET 
							 MODIFIER      = :V_MODIFIER
							,MOD_DDTT      = SYSDATE
							,YD_AIM_RT_GP  = :V_YD_AIM_RT_GP 
							,YD_AIM_YD_GP  = :V_YD_AIM_YD_GP 
							,YD_AIM_BAY_GP = :V_YD_AIM_BAY_GP
							--,YD_DLVRDD_RULE_DD = :V_YD_DLVRDD_RULE_DD
						WHERE STL_NO = :V_STL_NO
					 * 
					 * 		//ydStockDao.updYdStock(recPara, 0);
					 */
					intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdStock12", logId, methodNm, "이송대상재 저장품 수정");
					if( intRtnVal <= 0 ) {
						rtnEcod = "0";
						slabUtils.printLog(logId, szMsg + "수정시 오류발생 - 반환값 : " + intRtnVal, "SL");
					}else{
						rtnEcod = "1";
						slabUtils.printLog(logId, "수정 성공" +szMsg, "SL");
					}
				}
				szMsg = "[JSP Session : " + methodNm + "] 이송대상재 저장품 수정 성공";
				slabUtils.printLog(logId, szMsg, "SL");
				
				jrRtn.setField("RTN_CD"	, rtnEcod);
				jrRtn.setField("RTN_MSG", "이송대상재 저장품 수정처리 되었습니다.");	
				return jrRtn;
				
			} catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}
		}	
		

	    /**
		 * [A] 오퍼레이션명: 이송Lot편성List _ 삭제1
		 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송Lot편List 
		 * PYS 2020-10-12    
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inParam
		 * @return
		 * @throws DAOException
		 */
		public JDTORecord delYdPrepSch(GridData gdReq) throws DAOException {
			
			String methodNm 		= "이송Lot편성List_삭제1[PSlabYdMvCarSeEJB.delYdPrepSch] < " + gdReq.getNavigateValue();
			String logId 			= gdReq.getIPAddress();
			try{
				slabUtils.printLog(logId, methodNm, "S+");
				
				String szTrtGp			= "";
				String rtnEcod  		= "";
				String szMsg            = "";
				String szStlNo       	= "";
				
				//String szYdSchCd	    = "";
				String szYdPrepSchId    = "";
				String szYdAimBayGp     = "";
				String szYdCarasgnSeq   = "";
				String szYdWrkPlanCrn   = "";
				String szPackPrepSchId  = "", szPriYdPrepSchId = ""; 
				
				int    lCnt     		= 0;
				
				String sModifier   = slabUtils.trim(gdReq.getParam("userid" )); 			//수정자(MODIFIER)
				JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
				JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set
				//JDTORecord recInTemp = slabUtils.getParam(logId, methodNm, sModifier);
				int rowCnt   = gdReq.getHeader("CHECK").getRowCount();

				if(rowCnt == 0) {
					szMsg  = "작업대상건이 없습니다. ";
					slabUtils.printLog(logId, szMsg, "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", szMsg);	
					return jrRtn;
				} 
				for (int ii = 0; ii < rowCnt; ii++) {
					szYdPrepSchId			= slabUtils.nvl(slabUtils.getValue(gdReq, "YD_PREP_SCH_ID"   ,ii), ""    );
					szStlNo 				= slabUtils.nvl(slabUtils.getValue(gdReq, "STL_NO"           ,ii), ""    );
					
					slabUtils.printLog(logId, "YdPrepSchId:" +szYdPrepSchId + " StlNo:" + szStlNo , "SL");
					
					jrParam.setField("YD_PREP_SCH_ID"   , szYdPrepSchId);
					jrParam.setField("STL_NO"     		, szStlNo);
					
					//szYdSchCd	      = slabUtils.nvl(slabUtils.getValue(gdReq, "YD_SCH_CD"         ,ii), ""  );
					szYdPrepSchId     = slabUtils.nvl(slabUtils.getValue(gdReq, "YD_PREP_SCH_ID"    ,ii), ""  );
					szYdAimBayGp      = slabUtils.nvl(slabUtils.getValue(gdReq, "YD_AIM_BAY_GP"     ,ii), ""  );
					szYdCarasgnSeq    = slabUtils.nvl(slabUtils.getValue(gdReq, "YD_CARASGN_SEQ"    ,ii), ""  );
					szYdWrkPlanCrn    = slabUtils.nvl(slabUtils.getValue(gdReq, "YD_WRK_PLAN_CRN"   ,ii), ""  );
					szPackPrepSchId   = slabUtils.nvl(slabUtils.getValue(gdReq, "PACK_PREP_SCH_ID"  ,ii), ""  ); //동일차상위치 
					szPriYdPrepSchId  = slabUtils.nvl(slabUtils.getValue(gdReq, "PRI_YD_PREP_SCH_ID",ii), ""  );

					jrParam.setField("YD_PREP_SCH_ID"   , szYdPrepSchId);
					jrParam.setField("YD_AIM_BAY_GP"    , szYdAimBayGp);
					jrParam.setField("YD_CARASGN_SEQ"   , szYdCarasgnSeq);
					if( !szYdWrkPlanCrn.equals("") )	{
						//recPara.setField("YD_SCH_CD",   		szYD_SCH_CD.substring(0, 5) + szYD_WRK_PLAN_CRN.substring(5) + szYD_SCH_CD.substring(6));
						jrParam.setField("YD_WRK_PLAN_CRN",   	szYdWrkPlanCrn);
					}
					jrParam.setField("DEL_YN"           , "Y");
					jrParam.setField("REGISTER"         , sModifier);
					jrParam.setField("PRI_GB"			, szPackPrepSchId);
					jrParam.setField("PRI_YD_PREP_SCH_ID",szPriYdPrepSchId);

					
					if(!"".equals(szPriYdPrepSchId)  ) {
						/*
						UPDATE USRYDA.TB_YD_PREPSCH 
						 SET   MODIFIER           = :V_MODIFIER
						      ,MOD_DDTT           = SYSDATE
						      ,PRI_YD_PREP_SCH_ID = DECODE(:V_PRI_GB , '',  PRI_YD_PREP_SCH_ID, '')
						      ,YD_CAR_UPP_LOC_CD  = DECODE(:V_PRI_GB , '',  YD_CAR_UPP_LOC_CD, '')
						 WHERE PRI_YD_PREP_SCH_ID = :V_PRI_YD_PREP_SCH_ID
						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdPrepsch16", logId, methodNm, "2Bed상차Clear");
					}

					//delYdPrepSch  : 상단부 삭제 
					/*
					UPDATE USRYDA.TB_YD_PREPMTL
					 SET  DEL_YN = decode(:V_DEL_YN, 'Y', 'Y', 'N')
					   ,  REG_DDTT = sysdate 
					   ,  MODIFIER = :V_MODIFIER
					WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
					 */
					lCnt = commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.delYdPrepmtlByPrepSchId", logId, methodNm, "삭제");
					if (lCnt <= 0) {
						rtnEcod  = "0";
						szMsg = "삭제대상건이 없습니다.. ";
						slabUtils.printLog(logId, szMsg, "SL");
						jrRtn.setField("RTN_CD"	, rtnEcod);
						jrRtn.setField("RTN_MSG", szMsg);	
						return jrRtn;
					}
					
					/*
					JDTORecordSet jrPrepsch = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdPrepsch",  logId, methodNm, "상차묶음존재여부");
					jrParam.setField("PRI_YD_PREP_SCH_ID", "NULL");
					
					if(jrPrepsch.size() > 0) {
						jrPrepsch.first();
						recInTemp = jrPrepsch.getRecord();
						String sPriYdPrepSchId = slabUtils.paraRecChkNull(recInTemp,"PRI_YD_PREP_SCH_ID");
						if(!"".equals(sPriYdPrepSchId)) {
							jrParam.setField("DEL_YN" 			, "N");
							jrParam.setField("PRI_YD_PREP_SCH_ID"	, "NULL");
							jrParam.setField("YD_PREP_SCH_ID" 	, sPriYdPrepSchId.substring(0,18)    );

							commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.delYdPrepsch", logId, methodNm, "상차묶음정보초기화처리");
							
							jrParam.setField("YD_PREP_SCH_ID"   , szYdPrepSchId);
							jrParam.setField("DEL_YN" 			, "Y");
						}
					} 
					*/
					
					
					/*
					UPDATE  USRYDA.TB_YD_PREPSCH
					SET DEL_YN = DECODE(:V_DEL_YN, 'Y', 'Y', 'N')
					 , MODIFIER = :V_MODIFIER
					 , MOD_DDTT = sysdate
					 , PRI_YD_PREP_SCH_ID = DECODE(:V_PRI_YD_PREP_SCH_ID, 'NULL', '', PRI_YD_PREP_SCH_ID)
					WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
					 */
					
					lCnt = commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.delYdPrepsch", logId, methodNm, "삭제");

					if (lCnt <= 0) {
						rtnEcod  = "0";
						szMsg = "삭제대상건이 없습니다. ";
						slabUtils.printLog(logId, szMsg, "SL");
						jrRtn.setField("RTN_CD"	, rtnEcod);
						jrRtn.setField("RTN_MSG", szMsg);	
						return jrRtn;
						
					} else {
						rtnEcod  = "1";
						szMsg = "삭제작업  정상처리되었습니다. ";
						slabUtils.printLog(logId, szMsg, "SL");
					}
					
				}
				    
				slabUtils.printLog(logId, szTrtGp + ":" +rtnEcod + ">" + szMsg, "S-");
				
				slabUtils.printLog(logId, methodNm, "S-");
				jrRtn.setField("RTN_CD"	, rtnEcod);
				jrRtn.setField("RTN_MSG", szMsg);	
				return jrRtn;
				
			} catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}
		}
		
	    /**
		 * [A] 오퍼레이션명: 이송Lot편성List _ 수정 
		 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송Lot편List 
		 * PYS 2020-10-12    
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inParam
		 * @return
		 * @throws DAOException
		 */
		public JDTORecord uptYdPrepSch(GridData gdReq) throws DAOException {
			String methodNm 		= "이송Lot편성List_수정[PSlabYdMvCarSeEJB.uptYdPrepSch] < " + gdReq.getNavigateValue();
			String logId 			= gdReq.getIPAddress();
			try{
				slabUtils.printLog(logId, methodNm, "S+");
				
				String szTrtGp			= "";
				String rtnEcod  		= "";
				String szMsg            = "";
				String szStlNo       	= "";
				
				//String szYdSchCd	    = "";
				String szYdPrepSchId    = "";
				String szYdAimBayGp     = "";
				String szYdCarasgnSeq   = "";
				String szYdWrkPlanCrn   = "";
				String szPackPrepSchId  = "";
				String szPriYdPrepSchId = "";
				
				int    lCnt     		= 0;
				
				String sModifier   = slabUtils.trim(gdReq.getParam("userid" )); 			//수정자(MODIFIER)
				JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
				JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set
				
				int rowCnt   = gdReq.getHeader("CHECK").getRowCount();

				//szTrtGp = slabUtils.trim(gdReq.getParam("TRT_GP" ));
				
				if(rowCnt == 0) {
					szMsg  = "작업대상건이 없습니다. ";
					slabUtils.printLog(logId, szMsg, "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", szMsg);	
					return jrRtn;
				} 
				for (int ii = 0; ii < rowCnt; ii++) {
					szYdPrepSchId			= slabUtils.nvl(slabUtils.getValue(gdReq, "YD_PREP_SCH_ID"   ,ii), ""    );
					szStlNo 				= slabUtils.nvl(slabUtils.getValue(gdReq, "STL_NO"           ,ii), ""    );
					
					slabUtils.printLog(logId, "YdPrepSchId:" +szYdPrepSchId + " StlNo:" + szStlNo , "SL");
					
					jrParam.setField("YD_PREP_SCH_ID"   , szYdPrepSchId);
					jrParam.setField("STL_NO"     		, szStlNo);
					
					//szYdSchCd	      = slabUtils.nvl(slabUtils.getValue(gdReq, "YD_SCH_CD"         ,ii), ""  );
					szYdPrepSchId     = slabUtils.nvl(slabUtils.getValue(gdReq, "YD_PREP_SCH_ID"    ,ii), ""  );
					szYdAimBayGp      = slabUtils.nvl(slabUtils.getValue(gdReq, "YD_AIM_BAY_GP"     ,ii), ""  );
					szYdCarasgnSeq    = slabUtils.nvl(slabUtils.getValue(gdReq, "YD_CARASGN_SEQ"    ,ii), ""  );
					szYdWrkPlanCrn    = slabUtils.nvl(slabUtils.getValue(gdReq, "YD_WRK_PLAN_CRN"   ,ii), ""  );
					szPackPrepSchId   = slabUtils.nvl(slabUtils.getValue(gdReq, "PACK_PREP_SCH_ID"  ,ii), ""  ); //동일차상위치 
					szPriYdPrepSchId  = slabUtils.nvl(slabUtils.getValue(gdReq, "PRI_YD_PREP_SCH_ID",ii), ""  );
					
					jrParam.setField("YD_PREP_SCH_ID"   , szYdPrepSchId);
					jrParam.setField("YD_AIM_BAY_GP"    , szYdAimBayGp);
					jrParam.setField("YD_CARASGN_SEQ"   , szYdCarasgnSeq);
					if( !szYdWrkPlanCrn.equals("") )	{
						jrParam.setField("YD_WRK_PLAN_CRN", szYdWrkPlanCrn);
					} else {
						jrParam.setField("YD_WRK_PLAN_CRN", "");
					}
					
					jrParam.setField("DEL_YN"           , "N");
					jrParam.setField("REGISTER"         , sModifier);
					jrParam.setField("PRI_GB"			, szPackPrepSchId);
					jrParam.setField("PRI_YD_PREP_SCH_ID",szPriYdPrepSchId);
					slabUtils.printLog(logId, "PRI_YD_PREP_SCH_ID:"+ szPriYdPrepSchId + "+"+ szPackPrepSchId, "SL"); 
					
					if(!"".equals(szPriYdPrepSchId) && "".equals(szPackPrepSchId) ) {
						/*
						UPDATE USRYDA.TB_YD_PREPSCH
						 SET   MODIFIER           = :V_MODIFIER
						      ,MOD_DDTT           = SYSDATE
						      ,PRI_YD_PREP_SCH_ID = DECODE(:V_PRI_GB , '', '', PRI_YD_PREP_SCH_ID)
						      ,YD_CAR_UPP_LOC_CD  = DECODE(:V_PRI_GB , '',  '', YD_CAR_UPP_LOC_CD)
						 WHERE PRI_YD_PREP_SCH_ID = :V_PRI_YD_PREP_SCH_ID

						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdPrepsch15", logId, methodNm, "2Bed상차Clear");
					}
					
					/*
					UPDATE USRYDA.TB_YD_PREPSCH
					 SET   MODIFIER           = :V_MODIFIER
					      ,MOD_DDTT           = SYSDATE
					      ,YD_AIM_BAY_GP      = :V_YD_AIM_BAY_GP
					      ,YD_CARASGN_SEQ     = :V_YD_CARASGN_SEQ
					      ,YD_WRK_PLAN_CRN    = DECODE(:V_YD_WRK_PLAN_CRN, '',:V_YD_WRK_PLAN_CRN, YD_WRK_PLAN_CRN)
					 WHERE YD_PREP_SCH_ID     = :V_YD_PREP_SCH_ID
					 */
					lCnt = commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdPrepsch13", logId, methodNm, "수정");
					if (lCnt <= 0) {
						rtnEcod  = "0";
						szMsg = "수정대상건이 없습니다. ";
						slabUtils.printLog(logId, szMsg, "SL");
						jrRtn.setField("RTN_CD"	, rtnEcod);
						jrRtn.setField("RTN_MSG", szMsg);	
						return jrRtn;
					} else {
						rtnEcod  = "1";
						szMsg = "수정작업  정상처리되었습니다. ";
						slabUtils.printLog(logId, szMsg, "SL");
					}
				}
				    
				slabUtils.printLog(logId, szTrtGp + ":" +rtnEcod + ">" + szMsg, "S-");
				
				slabUtils.printLog(logId, methodNm, "S-");
				jrRtn.setField("RTN_CD"	, rtnEcod);
				jrRtn.setField("RTN_MSG", szMsg);	
				return jrRtn;
				
			} catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}
		}
		

	    /**
		 * [A] 오퍼레이션명: 이송Lot편List _ 삭제2 
		 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송Lot편List 
		 * PYS 2020-10-12    
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inParam
		 * @return
		 * @throws DAOException
		 */
		public JDTORecord delYdPrepMtl(GridData gdReq) throws DAOException {
			String methodNm 		= "이송Lot편List _ 삭제2[PSlabYdMvCarSeEJB.delYdPrepMtl] < " + gdReq.getNavigateValue();
			String logId 			= gdReq.getIPAddress();

			try{
				slabUtils.printLog(logId, methodNm, "S+");
				String szTrtGp			= "";
				String rtnEcod  		= "";
				String szMsg            = "";
				String szStlNo       	= "";
				
				String szYdPrepSchId    = "";
				
				int    lCnt     		= 0;
				
				String sModifier   = slabUtils.trim(gdReq.getParam("userid" )); 			//수정자(MODIFIER)
				JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
				JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord recInTemp = slabUtils.getParam(logId, methodNm, sModifier);
				
				int rowCnt   = gdReq.getHeader("CHECK").getRowCount();

				//szTrtGp = slabUtils.trim(gdReq.getParam("TRT_GP" ));
				
				slabUtils.printLog(logId, "TRT_GP:" + szTrtGp , "SL");
				if(rowCnt == 0) {
					szMsg  = "작업대상건이 없습니다. ";
					slabUtils.printLog(logId, szMsg, "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", szMsg);	
					return jrRtn;
				} 
				for (int ii = 0; ii < rowCnt; ii++) {
					szYdPrepSchId			= slabUtils.nvl(slabUtils.getValue(gdReq, "YD_PREP_SCH_ID"   ,ii), ""    );
					szStlNo 				= slabUtils.nvl(slabUtils.getValue(gdReq, "STL_NO"           ,ii), ""    );
					
					slabUtils.printLog(logId, "YdPrepSchId:" +szYdPrepSchId + " StlNo:" + szStlNo , "SL");
					
					jrParam.setField("YD_PREP_SCH_ID"   , szYdPrepSchId);
					jrParam.setField("STL_NO"     		, szStlNo);
					
					jrParam.setField("YD_PREP_SCH_ID"   , szYdPrepSchId);
					jrParam.setField("STL_NO"     		, szStlNo);

					/* 
						UPDATE  USRYDA.TB_YD_PREPMTL
						SET DEL_YN           = 'Y'
						   ,MODIFIER         = :V_MODIFIER
						   ,MOD_DDTT         = SYSDATE   
						WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
						  AND STL_NO         = :V_STL_NO
					*/
					lCnt = commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdPrepmtlStlByPrepSchId", logId, methodNm, "삭제");
					if (lCnt < 0) {
						rtnEcod  = "0";
						szMsg = "삭제대상건이 없습니다. ";
						slabUtils.printLog(logId, szMsg, "SL");
						jrRtn.setField("RTN_CD"	, rtnEcod);
						jrRtn.setField("RTN_MSG", szMsg);	
						return jrRtn;
					} else {
						rtnEcod  = "1";
						szMsg = "삭제작업  정상처리되었습니다. ";
						slabUtils.printLog(logId, szMsg, "SL");
					}
					
					//YD_EQP_WRK_SH "야드설비작업매수" 변경 
				}
				
				/*
					SELECT COUNT(*) AS YD_EQP_WRK_SH_CNT 
					FROM USRYDA.TB_YD_PREPMTL
					WHERE  1=1 
					AND YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
					AND DEL_YN         = 'N'
				*/	
				JDTORecordSet jrParam2 = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.selYdPrepmtlCnt",  logId, methodNm, "건수조회");
				if(jrParam2.size() >= 0) {
					jrParam2.first();
					recInTemp = jrParam2.getRecord();
					 
					jrParam.setField("YD_EQP_WRK_SH" 	, slabUtils.paraRecChkNull(recInTemp,"YD_EQP_WRK_SH_CNT")    );
					
					slabUtils.printLog(logId, "최종건수(YD_EQP_WRK_SH):" +slabUtils.paraRecChkNull(recInTemp,"YD_EQP_WRK_SH_CNT") , "SL");
					/*
					 *  com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdPrepsch12
						UPDATE TB_YD_PREPSCH
						   SET 
						       MODIFIER       = :V_MODIFIER
						      ,MOD_DDTT       = SYSDATE
						      ,YD_EQP_WRK_SH  = :V_YD_EQP_WRK_SH    -- 작업매수
						      ,YD_INV_SUM_WT  = (                   -- 집계중량
						                            SELECT SUM(SC.CAL_SLAB_WT) AS YD_INV_SUM_WT
						                              FROM USRPTA.TB_PT_SLABCOMM SC -- SLAB공통
						                             WHERE SC.SLAB_NO IN (
						                                                    SELECT STL_NO
						                                                      FROM TB_YD_PREPMTL MTL
						                                                     WHERE MTL.YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
						                                                       AND MTL.DEL_YN = 'N'
						                                                 )
						                          
						                        )
						      ,DEL_YN = DECODE(:V_YD_EQP_WRK_SH , 0, 'Y', 'N') -- 작업매수가 0이면 삭제 
						 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
					 */
					String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdPrepsch12";
					lCnt = commDao.update(jrParam, queryId, logId, methodNm, "변경(건수)");
					if(lCnt <= 0) {
						rtnEcod  = "0";
						szMsg    = "수정대상건이 없습니다. ";
						slabUtils.printLog(logId, szMsg, "SL");
						jrRtn.setField("RTN_CD"	, rtnEcod);
						jrRtn.setField("RTN_MSG", szMsg);	
						return jrRtn;
					} else {
						rtnEcod  = "1";
						szMsg = "삭제작업  정상처리되었습니다. ";
						slabUtils.printLog(logId, szMsg, "SL");
					}
				}
				slabUtils.printLog(logId, szTrtGp + ":" +rtnEcod + ">" + szMsg, "S-");
				
				slabUtils.printLog(logId, methodNm, "S-");
				jrRtn.setField("RTN_CD"	, rtnEcod);
				jrRtn.setField("RTN_MSG", szMsg);	
				return jrRtn;
				
			} catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}
		}
		

	    /**
		 * [A] 오퍼레이션명: 이송재료List _수정      (목표행선, 목표야드, 목표동)     							--ok
		 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송재료List 
		 * PYS 2020-10-15    
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inParam
		 * @return
		 * @throws DAOException
		 */
		public JDTORecord updSlabTotYdTransMtlList(GridData gdReq) throws DAOException {
			String methodNm = "이송재료List _수정[PSlabYdMvCarSeEJB.updSlabTotYdTransMtlList] < " + gdReq.getNavigateValue();
			String logId    = gdReq.getIPAddress();
			
			try{
				slabUtils.printLog(logId, methodNm, "S+");
				
				String szTrtGp	= "";
				String rtnEcod  = "";
				String szStlNo       = "";
				String szYdAimRtGp       = "";		//목표행선
				String szYdAimYdGp       = "";		//목표야드
				String szYdAimBayGp      = "";		//목표동
				String szMsg             = "";
				int    		lCnt         = 0;
				
				String sModifier   = slabUtils.trim(gdReq.getParam("YD_USER_ID" )); 			//수정자(MODIFIER)
				JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
				JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set
				int rowCnt   = gdReq.getHeader("CHECK").getRowCount();

				szTrtGp = slabUtils.trim(gdReq.getParam("TRT_GP" ));
				slabUtils.printLog(logId, "TRT_GP:" + szTrtGp , "SL");

				if(rowCnt <= 0 ) {
					szMsg = "대상건이 없습니다.";
					slabUtils.printLog(logId, methodNm, "S-");
					jrRtn.setField("RTN_CD"	, rtnEcod);
					jrRtn.setField("RTN_MSG", szMsg);	
				}

				for (int ii = 0; ii < rowCnt; ii++) {
					
					// 수정할 항목   				
					szStlNo 		  = slabUtils.nvl(slabUtils.getValue(gdReq, "STL_NO"           ,ii), ""    );
					szYdAimRtGp 	  = slabUtils.nvl(slabUtils.getValue(gdReq, "YD_AIM_RT_GP"     ,ii), ""    );  //목표행선
					szYdAimYdGp 	  = slabUtils.nvl(slabUtils.getValue(gdReq, "YD_AIM_YD_GP"     ,ii), ""    );  //목표야드
					szYdAimBayGp 	  = slabUtils.nvl(slabUtils.getValue(gdReq, "YD_AIM_BAY_GP"    ,ii), ""    );  //목표동
					
					jrParam.setField("STL_NO"     		, szStlNo);
					if("".equals(szStlNo)) {
						rtnEcod = "0";
						szMsg = "수정대상건이 없습니다. (코드 확인 요망.) ";
						jrRtn.setField("RTN_CD"	, rtnEcod);
						jrRtn.setField("RTN_MSG", szMsg);	
						return jrRtn;
					}
					// 수정; updSlabTotYdTransMtlList					// 등록: 자동-insYdPrepSch , 수동-insYdPrepSchByManual
					
					if("updSlabTotYdTransMtlList".equals(szTrtGp)   ) {
						jrParam.setField("YD_AIM_RT_GP", szYdAimRtGp);  //목표행선
						jrParam.setField("YD_AIM_YD_GP", szYdAimYdGp);  //목표야드
						jrParam.setField("YD_AIM_BAY_GP", szYdAimBayGp);  //목표동
						
						/*
							UPDATE TB_YD_STOCK 
							SET 
								 MODIFIER      = :V_MODIFIER
								,MOD_DDTT      = SYSDATE
								,YD_AIM_RT_GP  = :V_YD_AIM_RT_GP 
								,YD_AIM_YD_GP  = :V_YD_AIM_YD_GP 
								,YD_AIM_BAY_GP = :V_YD_AIM_BAY_GP
							WHERE STL_NO       = :V_STL_NO
						 */
						lCnt = commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdStock12", logId, methodNm, "수정");
						if (lCnt < 0) {
							rtnEcod = "0";
							szMsg = "수정대상건이 없습니다. ";
							jrRtn.setField("RTN_CD"	, rtnEcod);
							jrRtn.setField("RTN_MSG", szMsg);	
							return jrRtn;
						} else {
							rtnEcod = "1";
							szMsg = "수정작업  정상처리되었습니다. ";
						}
					}
				}  //++for 
				    
				slabUtils.printLog(logId, szTrtGp + ":" +rtnEcod + ">" + szMsg, "S-");
				
				slabUtils.printLog(logId, methodNm, "S-");
				jrRtn.setField("RTN_CD"	, rtnEcod);
				jrRtn.setField("RTN_MSG", szMsg);	
				return jrRtn;
				
			} catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}
		}

		/**
		 * 이송대상재를 준비스케줄에 등록 - 자동
		 * PYS  2020.12.03
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws DAOException
		 */
		public JDTORecord insYdPrepSch(GridData inDto) throws DAOException {
			String		methodNm = "이송대상재-준비스케줄등록(자동)[PSlabYdMvCarSeEJB.insYdPrepSch] <" + inDto.getNavigateValue();
			String 		logId  = inDto.getIPAddress();
			/*
			 * 업무기준 : 이송지시테이블, 저장품, 적치단, 슬라브공통, 주편공통, 준비스케줄, 준비재료, 작업예약, 작업예약재료
			 * 			 테이블을 조인해서 이송대상재를 조회해서 준비스케줄에 등록
			 * 			1. 자동/수동이송LOT편성인 지 판단
			 * 				1-1. 자동이송LOT편성이면
			 * 					1-1-1. 작업예약에 차량상차스케줄로 등록된 대상재는 제외
			 * 					1-1-2. 기존의 준비스케줄에 등록된 대상재는 제외
			 * 					1-1-3. 이송지시테이블의 이송상차일자[FRTOMOVE_CARLOAD_DATE]가 등록된 대상재는 제외
			 * 					1-1-3. 동별로 준비스케줄을 분리해서 등록되도록 처리
			 */

			try {
				String      szMsg        		= "";
				String      szOperationName	    = methodNm;
				//로컬변수 정의
				int			intFRTOMOVE_LOT_COUNT	= 0;
				int			intLOT_SH				= 0;
				int			intREAL_LOT_SH			= 0;
				String      szYdAimYdGp			= "";
				String      szYdAimBayGp 		= "";
				String 		szSposWlocCd		= "";
				String		szArrWlocCd			= "";
				String		szDateFrom			= "";
				String 		szDateTo			= "";
				String		szInOutGp			= "";
				String 		szYdGp				= "";
				String		szYdStkColGp		= "";
				String		szPrevYdStkColGp	= "";
				String		szYdPrepSchId		= "";
				String		szUserId			= "";
				String		szYdSchCd			= "";
				String		szStlNo				= "";
				String 		szYdStkBedNo		= "";
				String		szYdStkLyrNo 		= "";
				String		szYdDongGp			= "";
				String		szYdSpanGp			= "";
				String		szYdColGp			= "";
				String		szYdAimRtGp			= "";
				String		szCarGp				= "";
				String		szMakerName			= "";
				String		szRdDateAll			= "";
				String 		szViaGp				= "S";
				long		lngYD_MTL_WT		= 0;
				long		lngYD_MTL_WT_SUM	= 0;
				
				boolean		bRtnVal				= false;
				boolean	bIsLoopable				= true;
				int intRtnVal 					= 0;
				int intRowNo					= 0;
				int intLOOP_CNT					= 0;
				//----------------------------------------------------------------------------------------------
				//	파라미터 확인
				//----------------------------------------------------------------------------------------------
				String     sModifier = slabUtils.trim(inDto.getParam("YD_USER_ID" )); 		//수정자(MODIFIER)
				JDTORecord jrRtn     = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value

				JDTORecord recPara   = slabUtils.getParam(logId, methodNm, sModifier);		 
				JDTORecord recInTemp = slabUtils.getParam(logId, methodNm, sModifier);		 
				JDTORecord recTemp   = slabUtils.getParam(logId, methodNm, sModifier);		 
				JDTORecord jdtoRcd   = slabUtils.getParam(logId, methodNm, sModifier);		 
				JDTORecord recTemp2  = slabUtils.getParam(logId, methodNm, sModifier);		

				szMsg = "[JSP Session : "+szOperationName+"] -------------------- 메소드 시작 : 파라미터 확인 --------------------";
				slabUtils.printLog(logId,  szMsg, "S+");

				//ydUtils.displayRecord(szOperationName, inDto);

				szInOutGp 			= slabUtils.nvl(inDto.getParam("IN_OUT_GP"), "");			//입출고구분      =1.입고, 2.출고 
				szYdDongGp 			= slabUtils.nvl(inDto.getParam("YD_DONG_GP"), "");			//야드와 동구분  =DA,DB
				szYdSpanGp 			= slabUtils.nvl(inDto.getParam("YD_SPAN_GP"), "");			//스판구분
				szYdColGp 			= slabUtils.nvl(inDto.getParam("YD_COL_GP"), "");			//열구분

				szUserId 			= sModifier;

				szMsg = "" + logId + " 입출고구분" + szInOutGp+ " 야드와 동구분:" +szYdDongGp + " 스판구분:" + szYdSpanGp+ " 열구분:" + szYdColGp + " 입출구분:"+ szInOutGp;
				slabUtils.printLog(logId,  szMsg, "SL");

				//szYdStkColGp 		= "";

				if( szInOutGp.equals("1")) {					//입고 -- 실제적으로 호출되지 않음
					szSposWlocCd 		= slabUtils.nvl(inDto.getParam("ARR_WLOC_CD"), "");
					szArrWlocCd 		= slabUtils.nvl(inDto.getParam("SPOS_WLOC_CD"), "");
					//szYD_STK_COL_GP 	= szYD_DONG_GP + szYD_SPAN_GP + szYD_COL_GP;
					szYdStkColGp 		= "";
				}else{											//출고
					szSposWlocCd 		= slabUtils.nvl(inDto.getParam("SPOS_WLOC_CD"), "");	//발지개소코드
					szArrWlocCd 		= slabUtils.nvl(inDto.getParam("ARR_WLOC_CD"), "");		//착지개소코드
					szYdStkColGp 	    = "";                  //(변경전)  szYdDongGp + szYdSpanGp + szYdColGp 
				}
				szMsg = "" + logId + " *발지" + szSposWlocCd+ " 착지:" +szArrWlocCd   ;
				slabUtils.printLog(logId,  szMsg, "SL");
				
				szYdAimRtGp 			= slabUtils.nvl(inDto.getParam("YD_AIM_RT_GP"), "");		//목표행선
				szDateFrom   			= slabUtils.nvl(inDto.getParam("DATE_FROM"), "");			//시작일자
				szDateTo    			= slabUtils.nvl(inDto.getParam("DATE_TO"), "");				//종료일자
				//intFRTOMOVE_LOT_COUNT = ydDaoUtils.paraRecChkNullInt(inDto, "FRTOMOVE_LOT_COUNT");  
				//intLOT_SH   			= ydDaoUtils.paraRecChkNullInt(inDto, "LOT_SH");              
				szYdAimYdGp 			= slabUtils.nvl(inDto.getParam("YD_AIM_YD_GP"), "");		//목표야드
				szYdAimBayGp 			= slabUtils.nvl(inDto.getParam("YD_AIM_BAY_GP"), "");		//목표동
				szCarGp					= slabUtils.nvl(inDto.getParam("CAR_GP"), "");				//차량구분

				szMakerName 			= slabUtils.nvl(inDto.getParam("MAKER_NAME"), "");			//제조사 
				szYdGp 					= slabUtils.nvl(inDto.getParam("YD_GP"), "");				//야드구분

				slabUtils.printLog(logId, "CarGp:"+szCarGp + " YD_GP:"+szYdGp , "SL");
				//------------------------------------------------------------------------
				//	날짜를 전체로 조회할 것인 지를 판단하는 변수 추가
				//------------------------------------------------------------------------ :RD_DATE_ALL (없음)
				szRdDateAll			=  slabUtils.nvl(inDto.getParam("RD_DATE_ALL"), "");
				if( szRdDateAll.equals("Y")) {
					szDateFrom				= "00000000";
					szDateTo				= "99999999";
				}
				slabUtils.printLog(logId, szDateFrom+"~"+szDateTo, "SL");

				//------------------------------------------------------------------------
				//JDTORecordSet outRecSet           = JDTORecordFactory.getInstance().createRecordSet("YD");
				recPara        = JDTORecordFactory.getInstance().create();
			    recPara.setField("SPOS_WLOC_CD", 	szSposWlocCd);
			    recPara.setField("ARR_WLOC_CD",  	szArrWlocCd);
			    recPara.setField("YD_GP",  			szYdGp);
			    recPara.setField("DATE_FROM",    	szDateFrom);
			    recPara.setField("DATE_TO",      	szDateTo);
			    
			    //현재저장품위치가 s쪽에 위치 할경우가 있음.
			    slabUtils.printLog(logId, logId +"] YdStkColGp--> "+ szYdStkColGp, "SL");
			    recPara.setField("YD_STK_COL_GP",   szYdStkColGp);
			    recPara.setField("YD_AIM_RT_GP",    szYdAimRtGp);
			    recPara.setField("YD_AIM_YD_GP",    szYdAimYdGp);
			    recPara.setField("YD_AIM_BAY_GP",   szYdAimBayGp);
			    recPara.setField("MAKER_NAME",      szMakerName);
			    recPara.setField("YD_PREP_WK_ST",   "L");

			    //----------------------------------------------------------------------------------------------
				//	LOT편성할 대상재 조회
				//----------------------------------------------------------------------------------------------

				szMsg = "[JSP Session : "+szOperationName+"] LOT편성할 대상재 조회 시작 - 야드구분["+szYdGp+"]";
				slabUtils.printLog(logId,  szMsg, "SL");
			    /* # 이송지시된 이송재료 LIST - com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdFrtoMoveOrdMtlList

				 * SELECT COUNT(*)         OVER() AS TOTALCOUNT 
					     , SUM(A.YD_MTL_WT) OVER() AS TOT_SUM
					     , A.*
					FROM (
					  SELECT A.STL_NO
					    ,A.FRTOMOVE_STAT_CD
					    ,DECODE (A.FRTOMOVE_STAT_CD ,'*' , '작업완료'
					                                ,'0' , '공정등록'
					                                ,'1' , '공정확인'
					                                ,'2' , '출하확인'
					                                ,'C' , '지시취소'
					                                ,'3' , '야드수신' ) AS FRTOMOVE_STAT_CD_NAME
					                                
					     
					    ,A.SPOS_WLOC_CD
					    ,A.ARR_WLOC_CD
					    ,CASE WHEN A.FRTOMOVE_CARLOAD_DATE IS NOT NULL 
					    THEN SUBSTR(A.FRTOMOVE_CARLOAD_DATE,1,4)|| '.' || SUBSTR(A.FRTOMOVE_CARLOAD_DATE,5,2)|| '.'||SUBSTR(A.FRTOMOVE_CARLOAD_DATE,7,2)   
					    ELSE '' END AS FRTOMOVE_CARLOAD_DATE
					    ,SUBSTR(A.FRTOMOVE_WORD_DATE,1,4)|| '.' || SUBSTR(A.FRTOMOVE_WORD_DATE,5,2)|| '.'||SUBSTR(A.FRTOMOVE_WORD_DATE,7,2)   AS FRTOMOVE_WORD_DATE
					    ,TO_CHAR(A.FRTOMOVE_DONE_DATE,'YYYY.MM.DD') AS FRTOMOVE_DONE_DATE
					    ,B.YD_STK_COL_GP
					    ,B.YD_STK_BED_NO
					    ,B.YD_STK_LYR_NO
					    ,C.SLAB_WO_RT_CD
					    ,C.YD_MTL_WT
					    ,C.SPEC_ABBSYM
					    ,C.DEMANDER_CD
					    ,C.DEST_CD
					    ,C.YD_AIM_RT_GP
					    ,C.YD_AIM_YD_GP
					    ,C.YD_AIM_BAY_GP
					    ,C.SCARFING_YN 
					    ,C.SCARFING_DONE_YN
					    ,CASE WHEN C.SCARFING_YN = 'Y' AND NVL(C.SCARFING_DONE_YN, 'N') = 'N' THEN '스카핑' ELSE '' END AS SCARFING_GP
					    ,TO_CHAR(C.YD_MTL_T,'FM999.099') || ' X ' || TO_CHAR(C.YD_MTL_W,'FM9,999.0') || ' X ' || TO_CHAR(C.YD_MTL_L,'FM9,999,999') AS MTL_SIZE
					    ,D.SLAB_NO
					    ,DECODE(D.ORD_NO || '-' ||  D.ORD_DTL, '-', '') AS SLAB_COMM_ORD
					    ,DECODE(E.ORD_NO || '-' ||  E.ORD_DTL, '-', '') AS MSLAB_COMM_ORD
					    ,E.RECORD_PROG_STAT
					    ,DECODE(E.RECORD_PROG_STAT ,'3', D.ORD_YEOJAE_GP ,E.ORD_YEOJAE_GP)     AS  ORD_YEOJAE_GP
					    ,DECODE( DECODE(E.RECORD_PROG_STAT ,'3', D.ORD_YEOJAE_GP 
					                                          ,E.ORD_YEOJAE_GP) ,  '1', DECODE(E.RECORD_PROG_STAT , '3', DECODE(D.ORD_NO || '-' ||  D.ORD_DTL, '-', '')
					                                                                                                 , DECODE(E.ORD_NO || '-' ||  E.ORD_DTL, '-', '') )
					                                                            ,  '2', '' )  AS ORD
					    ,DECODE(E.RECORD_PROG_STAT ,'3', D.CURR_PROG_CD ,E.CURR_PROG_CD)  AS PROG_CD
					    ,CASE WHEN A.FRTOMOVE_STAT_CD IN ('3') THEN B.YD_STK_COL_GP || B.YD_STK_BED_NO ELSE SUBSTR(A.YD_MTL_PLN_STR_FR_LOC_CD,1,8) END AS POS1
					    ,CASE WHEN A.FRTOMOVE_STAT_CD IN ('3') THEN B.YD_STK_LYR_NO ELSE LPAD(SUBSTR(A.YD_MTL_PLN_STR_FR_LOC_CD,9,2), 3, '0') END AS DAN1
					    ,SUBSTR(A.YD_MTL_PLN_STR_TO_LOC_CD,1,8) AS POS2
					    ,SUBSTR(A.YD_MTL_PLN_STR_TO_LOC_CD,9,2) AS DAN2
					    , E.MSLAB_NO
					    , F.BUY_SLAB_NO AS GU_SALB_NO
					    , F.MAKER_NAME
					    , A.VIA_GP
					    FROM (SELECT *
					        FROM USRPTA.TB_PT_STLFRTOMOVE
					        WHERE (STL_NO, TRANSWORD_SEQNO) IN (
					            SELECT A.STL_NO
					            , MAX(A.TRANSWORD_SEQNO) AS TRANSWORD_SEQNO
					            FROM USRPTA.TB_PT_STLFRTOMOVE A
					            WHERE A.FRTOMOVE_STAT_CD IN ('3') 
					            AND A.FRTOMOVE_WORD_DATE BETWEEN :V_DATE_FROM AND :V_DATE_TO    
					            AND A.SPOS_WLOC_CD LIKE :V_SPOS_WLOC_CD || '%'
					            AND A.ARR_WLOC_CD LIKE :V_ARR_WLOC_CD || '%'
					            GROUP BY A.STL_NO ) 
					        ) A
					    , USRYDA.TB_YD_STKLYR B
					    , USRYDA.TB_YD_STOCK C
					    , USRPTA.TB_PT_SLABCOMM D
					    , USRPTA.TB_PT_MSLABCOMM E
					    , USRQMA.TB_QM_BUYSLABINFO F
					    WHERE A.STL_NO = B.STL_NO
					    AND A.STL_NO = C.STL_NO
					    AND A.STL_NO = D.SLAB_NO
					    AND D.MSLAB_NO = E.MSLAB_NO
					    AND A.STL_NO = F.MSLAB_NO(+)
					    AND B.YD_STK_COL_GP           LIKE :V_YD_STK_COL_GP || '%'
					    AND NVL(C.YD_AIM_RT_GP,  '*') LIKE :V_YD_AIM_RT_GP || '%'
					    AND NVL(C.YD_AIM_YD_GP,  '*') LIKE :V_YD_AIM_YD_GP || '%'
					    AND NVL(C.YD_AIM_BAY_GP, '*') LIKE :V_YD_AIM_BAY_GP || '%'
					    AND NVL(F.MAKER_NAME(+), '*') LIKE :V_MAKER_NAME || '%'
					    AND A.STL_NO NOT IN (
					        SELECT B.STL_NO
					        FROM USRYDA.TB_YD_PREPSCH A
					           , USRYDA.TB_YD_PREPMTL B
					        WHERE A.YD_PREP_SCH_ID = B.YD_PREP_SCH_ID
					        AND A.YD_GP = :V_YD_GP
					        AND A.YD_PREP_WK_ST = :V_YD_PREP_WK_ST
					        AND A.DEL_YN = 'N'
					        AND B.DEL_YN = 'N'
					    )
					    AND A.STL_NO NOT IN (
					        SELECT B.STL_NO
					        FROM USRYDA.TB_YD_WRKBOOK A
					           , USRYDA.TB_YD_WRKBOOKMTL B
					        WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
					        AND A.YD_GP         = :V_YD_GP
					        AND A.YD_SCH_CD LIKE :V_YD_GP || '_PT__UM'
					        AND A.DEL_YN        = 'N'
					        AND B.DEL_YN        = 'N'
					    )
					    AND B.DEL_YN = 'N'
					    AND C.DEL_YN = 'N'
					    ) A
					WHERE NVL(A.MAKER_NAME, '*') LIKE :V_MAKER_NAME || '%'
					ORDER BY A.YD_STK_COL_GP ASC, A.YD_STK_BED_NO DESC, A.YD_STK_LYR_NO DESC
					
				    //com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdFrtoMoveOrdMtlList
				    //intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 158);
				 */
				JDTORecordSet outRecSet = commDao.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdFrtoMoveOrdMtlList",  logId, methodNm, "이송지시된 이송재료 LIST조회");
				intRtnVal = outRecSet.size();
				szMsg = "[JSP Session : "+szOperationName+"] LOT편성할 대상재 조회 완료 - 반환값  : " + intRtnVal;
				slabUtils.printLog(logId,  szMsg, "SL");

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "[JSP Session : "+szOperationName+"] [1] LOT편성할 대상재 조회 시 오류발생 - 반환값 : " + intRtnVal;
						slabUtils.printLog(logId,  szMsg, "SL");
						
					} else {
						szMsg = "[JSP Session : "+szOperationName+"] [2] LOT편성할 대상재 조회 시 오류발생 - 반환값 : " + intRtnVal;
						slabUtils.printLog(logId,  szMsg, "SL");
					}
					//return outRecSet;
					
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", szMsg);	
					return jrRtn;		//YdConstant.RETN_CD_FAILURE
				} else 	if( intRtnVal == 0 ) {
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "대상건이 없습니다.");	
					return jrRtn;		//YdConstant.RETN_CD_NOTEXIST
				}
				//----------------------------------------------------------------------------------------------
				intFRTOMOVE_LOT_COUNT			= outRecSet.size();

				//----------------------------------------------------------------------------------------------
				//	BRE Rule에서 Pallet/Trailer에 따른 야드설비작업매수를 조회
				//----------------------------------------------------------------------------------------------

				//jdtoRcd = JDTORecordFactory.getInstance().create();
				slabUtils.printLog(logId, "차량구분:"+szCarGp, "SL");
				//BRE Rule에서 데이터 가져오기
				if("".equals(szCarGp )) {
					szCarGp = "T";
					szYdGp  = "D";
				}
				if( szCarGp.equals("P")||szCarGp.equals("Y") ) {
					if( szYdGp.equals(PSlabYdConstant.YD_GP_C_SLAB_YARD)) {				//C연주슬라브야드
						bRtnVal = GetBreRule1.getYDB193(jdtoRcd);
					}else if( szYdGp.equals(PSlabYdConstant.YD_GP_PORT_SLAB_YARD)) {	//항만슬라브야드 
						bRtnVal = GetBreRule1.getYDB193(jdtoRcd);
					}else if( szYdGp.equals(PSlabYdConstant.YD_GP_A_PLATE_SLAB_YARD)) {	//A후판슬라브야드
						bRtnVal = GetBreRule2.getYDB296(jdtoRcd);
					}
			    	if( bRtnVal ) {
			    		intLOT_SH = Integer.parseInt(""+jdtoRcd.getField("YD_EQP_WRK_SH"));
			    		szMsg = "[JSP Session : "+szOperationName+"] 야드구분["+szYdGp+"] - 차량구분["+szCarGp+"]에대한 야드설비작업매수 BRE Rule 조회 성공 - 야드설비작업매수["+intLOT_SH+"] ";
						slabUtils.printLog(logId, szMsg, "SL");
			    	}else{
			    		intLOT_SH = 6;
			    		szMsg = "[JSP Session : "+szOperationName+"] 야드구분["+szYdGp+"] - 차량구분["+szCarGp+"]에대한 야드설비작업매수 BRE Rule 조회 시 오류발생 - 야드설비작업매수 기본값[6] 사용";
			    		slabUtils.printLog(logId, szMsg, "SL");
			    	}
			    //szCAR_GP- T:100ton이하 , B:100ton이상 
				}else if( szCarGp.equals("T")  ) {
					if( szYdGp.equals(PSlabYdConstant.YD_GP_C_SLAB_YARD)) {				//C연주슬라브야드
						bRtnVal = GetBreRule1.getYDB194(jdtoRcd);
					}else if( szYdGp.equals(PSlabYdConstant.YD_GP_PORT_SLAB_YARD)) {	//항만슬라브야드 기능추가 - 
						bRtnVal = GetBreRule1.getYDB194(jdtoRcd);
					}else if( szYdGp.equals(PSlabYdConstant.YD_GP_A_PLATE_SLAB_YARD)) {	//A후판슬라브야드
						bRtnVal = GetBreRule2.getYDB297(jdtoRcd);
					}
					
			    	if( bRtnVal ) {
			    		intLOT_SH = slabUtils.paraRecChkNullInt(jdtoRcd, "YD_EQP_WRK_SH");
			    		szMsg = "[JSP Session : "+szOperationName+"] 야드구분["+szYdGp+"] - 차량구분["+szCarGp+"]에대한 야드설비작업매수 BRE Rule 조회 성공 - 야드설비작업매수["+intLOT_SH+"] ";
						slabUtils.printLog(logId,  szMsg, "SL");
			    	}else{
			    		intLOT_SH = 2;
			    		szMsg = "[JSP Session : "+szOperationName+"] 야드구분["+szYdGp+"] - 차량구분["+szCarGp+"]에대한 야드설비작업매수 BRE Rule 조회 시 오류발생 - 야드설비작업매수 기본값[2] 사용";
			    		slabUtils.printLog(logId,  szMsg, "SL");
			    	}
			   //szCAR_GP- T:100ton이하 , B:100ton이상 
				}else if(szCarGp.equals("B")){
					intLOT_SH = 3;
		    		szMsg = "[JSP Session : "+szOperationName+"] 야드구분["+szYdGp+"] - 차량구분["+szCarGp+"]에대한 야드설비작업매수 BRE Rule 조회 성공 - 야드설비작업매수["+intLOT_SH+"] ";
		    		slabUtils.printLog(logId,  szMsg, "SL");
				}

				//----------------------------------------------------------------------------------------------
				slabUtils.printLog(logId, intFRTOMOVE_LOT_COUNT % intLOT_SH + ">" + intFRTOMOVE_LOT_COUNT + "intLOT_SH:" +intLOT_SH, "SL");
				
				intLOOP_CNT				= intFRTOMOVE_LOT_COUNT	/ intLOT_SH;

				if( intFRTOMOVE_LOT_COUNT % intLOT_SH > 0 ) intLOOP_CNT++;

				intRowNo = 1;
				//for(int i = 1; i <= intFRTOMOVE_LOT_COUNT; i++ ) {
				for(int i = 1; i <= intLOOP_CNT; i++ ) {
					intREAL_LOT_SH 			= 0;
					lngYD_MTL_WT_SUM 		= 0;
					for(int j = 1; j <= intLOT_SH; j++ ) {
						szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 대상재 로우번호["+intRowNo+"] ";
						slabUtils.printLog(logId,  szMsg, "SL");
						if( outRecSet.size() < intRowNo ) {
							bIsLoopable = false;
							szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 대상재 로우번호["+intRowNo+"]가 대상재 건수["+outRecSet.size()+"]보다 큽니다. ";
							slabUtils.printLog(logId,  szMsg, "SL");
							break;
						}
						outRecSet.absolute(intRowNo);
						recPara = outRecSet.getRecord();

						if(szViaGp.equals("S")){
							szViaGp  			= slabUtils.paraRecChkNull(recPara, "VIA_GP");
						}

						szMsg = intRowNo+"::::"+ szViaGp+"VIA_GP:"+slabUtils.paraRecChkNull(recPara, "VIA_GP");
						slabUtils.printLog(logId,  szMsg, "SL");

						if(!szViaGp.equals(slabUtils.paraRecChkNull(recPara, "VIA_GP"))){

							szMsg = "[JSP Session : "+szOperationName+"] 경유대상재와 함께 이송LOT편성을 할 수 없습니다. ";
							slabUtils.printLog(logId,  szMsg, "SL");
							//this.m_ctx.setRollbackOnly();
							
							jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", szMsg);	
							return jrRtn;
						}

						intRowNo++;
					}
				}
				slabUtils.printLog(logId, "_준비스케줄 등록_" + intFRTOMOVE_LOT_COUNT +"/"+ intLOT_SH+ "(" + (intFRTOMOVE_LOT_COUNT % intLOT_SH) + ")", "SL");
				//----------------------------------------------------------------------------------------------
				//	준비스케줄 등록
				//----------------------------------------------------------------------------------------------

				intLOOP_CNT				= intFRTOMOVE_LOT_COUNT	/ intLOT_SH;

				if( intFRTOMOVE_LOT_COUNT % intLOT_SH > 0 ) intLOOP_CNT++;

				intRowNo = 1;
				//for(int i = 1; i <= intFRTOMOVE_LOT_COUNT; i++ ) {
				for(int i = 1; i <= intLOOP_CNT; i++ ) {
					intREAL_LOT_SH 			= 0;
					lngYD_MTL_WT_SUM 		= 0;
					for(int j = 1; j <= intLOT_SH; j++ ) {
						
						szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 대상재 로우번호["+intRowNo+"] ";
						slabUtils.printLog(logId,  szMsg, "SL");
						if( outRecSet.size() < intRowNo ) {
							bIsLoopable = false;
							szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 대상재 로우번호["+intRowNo+"]가 대상재 건수["+outRecSet.size()+"]보다 큽니다. ";
							slabUtils.printLog(logId,  szMsg, "SL");
							break;
						}
						outRecSet.absolute(intRowNo);
						recPara = outRecSet.getRecord();
						
						szStlNo  			= slabUtils.paraRecChkNull(recPara, "STL_NO");
						szYdStkColGp 		= slabUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
						szYdStkBedNo 		= slabUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
						szYdStkLyrNo 		= slabUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
						slabUtils.printLog(logId, "szStlNo:"+ szStlNo, "SL");
						
						if(!"".equals(szYdStkColGp)) {
							szYdGp 			= szYdStkColGp.substring(0, 1);
						} else {
							szYdGp     		= "DB";         
						}
						szYdAimBayGp 		= slabUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
						szYdAimYdGp 		= slabUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
						szArrWlocCd 		= slabUtils.paraRecChkNull(recPara, "ARR_WLOC_CD");
						lngYD_MTL_WT 		= slabUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");
						slabUtils.printLog(logId, "szYdAimBayGp:"+ szYdAimBayGp, "SL");


						lngYD_MTL_WT_SUM += lngYD_MTL_WT;

						szYdSchCd = szYdStkColGp.substring(0, 2) + "PT01UM";
						slabUtils.printLog(logId, j+">szYdSchCd:" + szYdSchCd, "SL");
						
						if( j == 1 ) {
							/* (준비스케쥴고유ID생성) 
							 * SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') ||  LPAD(YD_PREPSCH_SEQ.nextval,6,'0') AS YD_PREP_SCH_ID FROM DUAL
							 */
							JDTORecordSet reTemp = commDao.select(recTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdPrepschId", logId, methodNm, "조회");
							if(reTemp.size() > 0) {
								reTemp.first();
								recTemp2   = reTemp.getRecord();
								szYdPrepSchId = slabUtils.paraRecChkNull(recTemp2,"YD_PREP_SCH_ID") ;
							}
							//recTemp = JDTORecordFactory.getInstance().create();
							//준비스케줄 등록
							recTemp.setField("YD_PREP_SCH_ID", 			szYdPrepSchId);
							recTemp.setField("YD_SCH_CD", 				szYdSchCd);
							recTemp.setField("REGISTER", 				szUserId);
							recTemp.setField("YD_GP", 					szYdGp);
							recTemp.setField("YD_PREP_WK_ST", 			"L");
							recTemp.setField("ARR_WLOC_CD", 			szArrWlocCd);
							recTemp.setField("YD_AIM_YD_GP", 			szYdAimYdGp);
							recTemp.setField("YD_AIM_BAY_GP", 			szYdAimBayGp);
							recTemp.setField("YD_CARASGN_SEQ", 			PSlabYdConstant.YD_CARASGN_SEQ_AUTO_DEFAULT);
							//recTemp.setField("YD_EQP_WRK_SH", 		"" + intLOT_SH);
							recTemp.setField("CAR_GP", 					szCarGp);

							/* # com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.insYdPrepsch
								INSERT INTO TB_YD_PREPSCH    
								  ( 
								     YD_PREP_SCH_ID,
								     YD_SCH_CD,
								     REGISTER,
								     REG_DDTT,    
								     YD_GP,
								     YD_PREP_WK_ST,
								     YD_TO_LOC_DCSN_MTD,  --!!
								     YD_TO_LOC_GUIDE,     --!!
								     ARR_WLOC_CD,
								     YD_AIM_BAY_GP,
								     YD_CARASGN_SEQ,
								     YD_EQP_WRK_SH,
								     YD_WRK_PLAN_CRN,     --!!
								     YD_WBOOK_ID,         --!!
								     YD_AIM_YD_GP,
								     YD_INV_SUM_WT,       --!!
								     CAR_GP
								  )
								  VALUES (
								     :V_YD_PREP_SCH_ID
								   , :V_YD_SCH_CD
								   , :V_REGISTER
								   , SYSDATE
								   , :V_YD_GP
								   , :V_YD_PREP_WK_ST
								   , :V_YD_TO_LOC_DCSN_MTD
								   , :V_YD_TO_LOC_GUIDE
								   , :V_ARR_WLOC_CD
								   , :V_YD_AIM_BAY_GP
								   , :V_YD_CARASGN_SEQ
								   , :V_YD_EQP_WRK_SH
								   , :V_YD_WRK_PLAN_CRN
								   , :V_YD_WBOOK_ID
								   , :V_YD_AIM_YD_GP
								   , :V_YD_INV_SUM_WT
								   , :V_CAR_GP
								   )
							 */
							intRtnVal = commDao.insert(recTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.insYdPrepsch", logId, methodNm, "등록");
							if( intRtnVal < 0 ) {
								szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYdPrepSchId+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
								slabUtils.printLog(logId,  szMsg, "SL");
								
								jrRtn.setField("RTN_CD"	, "0");
								jrRtn.setField("RTN_MSG", szMsg);	
								return jrRtn;
							}else if( intRtnVal == 0 ) {
								szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYdPrepSchId+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
								slabUtils.printLog(logId,  szMsg, "SL");
								
								jrRtn.setField("RTN_CD"	, "0");
								jrRtn.setField("RTN_MSG", szMsg);	
								return jrRtn;

							}else{
								szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYdPrepSchId+"] 등록 성공 ";
								slabUtils.printLog(logId,  szMsg, "SL");
							}

						}else{
							if( !szYdStkColGp.substring(0, 2).equals(szPrevYdStkColGp.substring(0, 2)) ) {
								slabUtils.printLog(logId, "szYdStkColGp:" + szYdStkColGp+"=="+ szPrevYdStkColGp + ":szPrevYdStkColGp", "SL");
								break;
							}
						}
						//준비재료 등록
						slabUtils.printLog(logId, "--(준비재료 등록)TB_YD_PREPMTL insert--", "SL");
						//recTemp = JDTORecordFactory.getInstance().create();
						recTemp.setField("STL_NO", 				szStlNo);
						recTemp.setField("YD_PREP_SCH_ID", 		szYdPrepSchId);
						recTemp.setField("REGISTER", 			szUserId);
						recTemp.setField("YD_STK_COL_GP", 		szYdStkColGp);
						recTemp.setField("YD_STK_BED_NO", 		szYdStkBedNo);
						recTemp.setField("YD_STK_LYR_NO", 		szYdStkLyrNo);

						/* #자동,수동 모두 처리 - com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.insYdPrepmtl
							INSERT INTO TB_YD_PREPMTL
							       (
							        STL_NO
							        ,YD_PREP_SCH_ID
							        ,REGISTER
							        ,REG_DDTT
							        ,YD_STK_COL_GP
							        ,YD_STK_BED_NO
							        ,YD_STK_LYR_NO
							        ,FROMTOMOVE_PLN_DATE
							       )
							VALUES (         
							       :V_STL_NO
							       ,:V_YD_PREP_SCH_ID
							       ,:V_REGISTER
							       ,SYSDATE
							       ,:V_YD_STK_COL_GP
							       ,:V_YD_STK_BED_NO
							       ,:V_YD_STK_LYR_NO
							       ,:V_FROMTOMOVE_PLN_DATE
							       )
						 */
						intRtnVal = commDao.insert(recTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.insYdPrepmtl", logId, methodNm, "등록");

						if( intRtnVal < 0 ) {
							szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYdPrepSchId+"]의 ["+j+"]번재료["+szStlNo+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
							slabUtils.printLog(logId,  szMsg, "SL");
							jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", szMsg);	
							return jrRtn;
						}else if( intRtnVal == 0 ) {
							szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYdPrepSchId+"]의 ["+j+"]번재료["+szStlNo+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
							slabUtils.printLog(logId,  szMsg, "SL");
							jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", szMsg);	
							return jrRtn;
						}else{
							szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYdPrepSchId+"]의 ["+j+"]번재료["+szStlNo+"] 등록 성공 ";
							slabUtils.printLog(logId,  szMsg, "SL");
						}
						intREAL_LOT_SH++;
						szPrevYdStkColGp = szYdStkColGp;
						intRowNo++;
					}

					szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYdPrepSchId+"]의 매수["+intREAL_LOT_SH+"], 중량["+lngYD_MTL_WT_SUM+"] 수정 시작";
					slabUtils.printLog(logId,  szMsg, "SL");

					recInTemp = JDTORecordFactory.getInstance().create();
					
					//준비스케줄수정
					recInTemp.setField("YD_SCH_CD",				szYdSchCd);
					recInTemp.setField("YD_PREP_SCH_ID", 		szYdPrepSchId);
					recInTemp.setField("YD_EQP_WRK_SH", 	    String.valueOf(intREAL_LOT_SH));   	//야드설비작업매수
					recInTemp.setField("YD_INV_SUM_WT", 		String.valueOf(lngYD_MTL_WT_SUM));

					/*
					 *    UPDATE TB_YD_PREPSCH
						   SET YD_SCH_CD = :V_YD_SCH_CD
						      ,MODIFIER = :V_MODIFIER
						      ,MOD_DDTT = SYSDATE
						      ,YD_INV_SUM_WT = :V_YD_INV_SUM_WT
						      ,YD_EQP_WRK_SH   = :V_YD_EQP_WRK_SH   --:자동
						 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
					 */
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdPrepsch11", logId, methodNm, "수정");

					szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYdPrepSchId+"]의 매수["+intREAL_LOT_SH+"], 중량["+lngYD_MTL_WT_SUM+"] 수정 성공 - 반환값 : " + intRtnVal;
					slabUtils.printLog(logId,  szMsg, "SL");

					slabUtils.printLog(logId, "bIsLoopable:"+bIsLoopable , "SL");
					if( !bIsLoopable ) break;
				}
				
				//----------------------------------------------------------------------------------------------

				szMsg = "[JSP Session : "+szOperationName+"] -------------------- 메소드 끝 --------------------";
				slabUtils.printLog(logId,  szMsg, "SL");

				jrRtn.setField("RTN_CD"	, "1");
				jrRtn.setField("RTN_MSG", "정상처리되었습니다.");	
				return jrRtn;

			} catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}
		}

		/**
		 * 이송대상재를 준비스케줄에 등록 - 수동
		 * PYS 2020.12.03
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws DAOException
		 */
		public JDTORecord insYdPrepSchByManual(GridData inDto) throws DAOException {
			String 		methodNm 			= "이송대상재-준비스케줄등록(수동)[insYdPrepSchByManual] < " + inDto.getNavigateValue();
			String logId = inDto.getIPAddress(); 
			/*
			 * 업무기준 : 1. 그리드로 넘겨진 대상재들을 준비스케줄에 등록
			 * 				1-1. 준비스케줄 등록
			 * 				1-2. 준비재료 등록
			 */

			try {
				slabUtils.printLog(logId, methodNm, "S+");
				int intLotSh = inDto.getHeader("CHECK").getRowCount();
				int rowCnt   = intLotSh; 
				String      szMsg        		= "";
				String		szOperationName		= methodNm;
				//로컬변수 정의
				String		szYdAimYdGp			= "";
				String      szYdAimBayGp 		= "";
				String		szArrWlocCd			= "";
				String 		szYdGp				= "D";
				String		szYdStkColGp		= "";
				String		szYdPrepSchId		= "";
				String		szUserId			= "";
				String		szYdSchCd			= "";
				String		szStlNo				= "";
				String 		szYdStkBedNo		= "";
				String		szYdStkLyrNo 		= "";
				int 		intRtnVal 			= 0;
				long		lngYdMtlWt			= 0;
				long		lngYdMtlWtSum		= 0;
				
				String     sModifier = slabUtils.nvl(inDto.getParam("YD_USER_ID" ), ""); 		//수정자(MODIFIER)
				JDTORecord jrRtn     = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
				//JDTORecord jrParam   = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord recTemp2  = slabUtils.getParam(logId, methodNm, sModifier);
				JDTORecord recTemp   = slabUtils.getParam(logId, methodNm, sModifier);
				
				//String     sYdDongGp = slabUtils.nvl(inDto.getParam("YD_DONG_GP" ), "");
				szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 - 대상재건수["+ intLotSh+"]";
				slabUtils.printLog(logId, szMsg, "SL");

				szUserId 	 = slabUtils.trim(inDto.getParam("YD_USER_ID" ) );
				for(int i = 0; i < rowCnt; i++ ) {

					String szSposWlocCd = "";
					
					szStlNo  	 = slabUtils.nvl(slabUtils.getValue(inDto, "STL_NO"          ,i), "" );  //재료번호
					szYdStkColGp = slabUtils.nvl(slabUtils.getValue(inDto, "YD_STK_COL_GP"   ,i), "" );  
					szArrWlocCd  = slabUtils.nvl(slabUtils.getValue(inDto, "ARR_WLOC_CD"     ,i), "" );  //착지)개소 
					szSposWlocCd = slabUtils.nvl(slabUtils.getValue(inDto, "SPOS_WLOC_CD"    ,i), "" ); 
					
					if("".equals(szArrWlocCd)) {
						szArrWlocCd = PSlabYdConstant.WLOC_CD_B_PLATE_PLANT;
					}
					if("".equals(szSposWlocCd)) {
						szSposWlocCd = slabUtils.nvl(inDto.getParam("SPOS_WLOC_CD" ), "");
					}
					slabUtils.printLog(logId, szStlNo +"("+ i +")-->ARR_WLOC_CD:" + szArrWlocCd + " SPOS_WLOC_CD:" + szSposWlocCd, "SL"); 
					/*
					SELECT YD_STK_COL_GP 
					FROM TB_YD_STKCOL
					WHERE WLOC_CD = :V_WLOC_CD 
					AND DEL_YN = 'N'
					AND ROWNUM = 1
					 */
					recTemp.setField("WLOC_CD"	, szSposWlocCd);
					JDTORecordSet temp = commDao.select(recTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getLYdStkcolGp", logId, methodNm, "D_PT0_추출");
					if(temp.size()>0){
						szYdSchCd = temp.getRecord(0).getFieldString("YD_STK_COL_GP") + "UM";
					}

					slabUtils.printLog(logId, "YdSchCd:"+ szYdSchCd + " 재료번호:"+szStlNo + " YdGp:" + szYdGp + " 스케줄:" + szYdSchCd , "SL");
					if(i == 0) {
						//---------------------------------------------------------------------
						//준비스케줄 등록
						//---------------------------------------------------------------------
						/* (준비스케쥴고유ID생성) 
						 * SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') ||  LPAD(YD_PREPSCH_SEQ.nextval,6,'0') AS YD_PREP_SCH_ID FROM DUAL
							//szYD_PREP_SCH_ID = ydPrepSchDao.getYdPrepschId();
						 */
						JDTORecordSet reTemp = commDao.select(recTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdPrepschId", logId, methodNm, "조회");
						if(reTemp.size() > 0) {
							reTemp.first();
							recTemp2      = reTemp.getRecord();
							szYdPrepSchId = slabUtils.paraRecChkNull(recTemp2,"YD_PREP_SCH_ID") ;
							//szYD_PREP_SCH_ID = ydPrepSchDao.getYdPrepschId();
						}
						slabUtils.printLog(logId, " Yd_Prep_Sch_Id:"+szYdPrepSchId, "SL");
						//recTemp = JDTORecordFactory.getInstance().create();
					}
					
					recTemp.setField("YD_PREP_SCH_ID"	, szYdPrepSchId);
					recTemp.setField("YD_SCH_CD"		, szYdSchCd);
					recTemp.setField("REGISTER"			, szUserId);
					recTemp.setField("YD_GP"			, szYdGp);
					recTemp.setField("YD_PREP_WK_ST"	, "L");
					recTemp.setField("ARR_WLOC_CD"		, szArrWlocCd);
					recTemp.setField("YD_AIM_YD_GP"		, szYdAimYdGp);
					recTemp.setField("YD_AIM_BAY_GP"	, szYdAimBayGp);
					recTemp.setField("YD_CARASGN_SEQ"	, PSlabYdConstant.YD_CARASGN_SEQ_MAN_DEFAULT);
					recTemp.setField("YD_EQP_WRK_SH"	, "" + intLotSh);
					recTemp.setField("CAR_GP"			, slabUtils.trim(inDto.getParam("CAR_GP" ) ) );
					
					/*
					INSERT INTO TB_YD_PREPSCH    
					  ( 
					     YD_PREP_SCH_ID,
					     YD_SCH_CD,
					     REGISTER,
					     REG_DDTT,    
					     YD_GP,
					     YD_PREP_WK_ST,
					     YD_TO_LOC_DCSN_MTD,  
					     YD_TO_LOC_GUIDE,     
					     ARR_WLOC_CD,
					     YD_AIM_BAY_GP,
					     YD_CARASGN_SEQ,
					     YD_EQP_WRK_SH,
					     YD_WRK_PLAN_CRN,    
					     YD_WBOOK_ID,        
					     YD_AIM_YD_GP,
					     YD_INV_SUM_WT,      
					     CAR_GP
					  )
					  VALUES (
					     :V_YD_PREP_SCH_ID
					   , :V_YD_SCH_CD
					   , :V_REGISTER
					   , SYSDATE
					   , :V_YD_GP
					   , :V_YD_PREP_WK_ST
					   , :V_YD_TO_LOC_DCSN_MTD
					   , :V_YD_TO_LOC_GUIDE
					   , :V_ARR_WLOC_CD
					   , :V_YD_AIM_BAY_GP
					   , :V_YD_CARASGN_SEQ
					   , :V_YD_EQP_WRK_SH
					   , :V_YD_WRK_PLAN_CRN
					   , :V_YD_WBOOK_ID
					   , :V_YD_AIM_YD_GP
					   , :V_YD_INV_SUM_WT
					   , :V_CAR_GP
					   )
					 */
					if(i == 0) {
						intRtnVal = commDao.insert(recTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.insYdPrepsch", logId, methodNm, "등록");
						
						if( intRtnVal < 0 ) {
							szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYdPrepSchId+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
							slabUtils.printLog(logId,  szMsg, "SL");
							jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", szMsg);	
							return jrRtn;
							
						}else if( intRtnVal == 0 ) {
							szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYdPrepSchId+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
							slabUtils.printLog(logId,  szMsg, "SL");
							jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", szMsg);	
							return jrRtn;
							
						}else{
							szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYdPrepSchId+"] 등록 성공 ";
							slabUtils.printLog(logId,  szMsg, "SL");
						}
					}
					
					szStlNo  	 = slabUtils.nvl(slabUtils.getValue(inDto, "STL_NO"          ,i), "" ); 
					szYdStkColGp = slabUtils.nvl(slabUtils.getValue(inDto, "YD_STK_COL_GP"   ,i), "" ); 
					szYdStkBedNo = slabUtils.nvl(slabUtils.getValue(inDto, "YD_STK_BED_NO"   ,i), "" ); 
					szYdStkLyrNo = slabUtils.nvl(slabUtils.getValue(inDto, "YD_STK_LYR_NO"   ,i), "" ); 
					lngYdMtlWt	 = Long.parseLong(slabUtils.nvl(slabUtils.getValue(inDto, "YD_MTL_WT"   ,i), "" )); 

					lngYdMtlWtSum += lngYdMtlWt;

					//준비재료 등록
					recTemp.setField("STL_NO", szStlNo);
					recTemp.setField("YD_PREP_SCH_ID", szYdPrepSchId);
					recTemp.setField("REGISTER", szUserId);
					recTemp.setField("YD_STK_COL_GP", szYdStkColGp);
					recTemp.setField("YD_STK_BED_NO", szYdStkBedNo);
					recTemp.setField("YD_STK_LYR_NO", szYdStkLyrNo);

					//자동,수동 모두 처리 
					/*
						INSERT INTO TB_YD_PREPMTL
						       (
						         STL_NO
						        ,YD_PREP_SCH_ID
						        ,REGISTER
						        ,REG_DDTT
						        ,YD_STK_COL_GP
						        ,YD_STK_BED_NO
						        ,YD_STK_LYR_NO
						        ,FROMTOMOVE_PLN_DATE
						       )
						VALUES (         
						       :V_STL_NO
						       ,:V_YD_PREP_SCH_ID
						       ,:V_REGISTER
						       ,SYSDATE
						       ,:V_YD_STK_COL_GP
						       ,:V_YD_STK_BED_NO
						       ,:V_YD_STK_LYR_NO
						       ,:V_FROMTOMOVE_PLN_DATE
						       )
					 */
					intRtnVal = commDao.insert(recTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.insYdPrepmtl", logId, methodNm, "등록");

					if( intRtnVal < 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYdPrepSchId+"]의 ["+(i + 1)+"]번재료["+szStlNo+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
						slabUtils.printLog(logId,  szMsg, "SL");
					}else if( intRtnVal == 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYdPrepSchId+"]의 ["+(i + 1)+"]번재료["+szStlNo+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
						slabUtils.printLog(logId,  szMsg, "SL");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", szMsg);	
						return jrRtn;

					}else{
						szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYdPrepSchId+"]의 ["+(i + 1)+"]번재료["+szStlNo+"] 등록 성공 ";
						slabUtils.printLog(logId,  szMsg, "SL");
					}
				}

				//준비스케줄수정
				recTemp.setField("YD_PREP_SCH_ID", szYdPrepSchId);
				recTemp.setField("YD_INV_SUM_WT", "" + lngYdMtlWtSum);
				//recInTemp.setField("YD_EQP_WRK_SH", 	String.valueOf(intREAL_LOT_SH));   	//야드설비작업매수
				/*
				 *    UPDATE TB_YD_PREPSCH
					   SET YD_SCH_CD = :V_YD_SCH_CD
					      ,MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,YD_INV_SUM_WT = :V_YD_INV_SUM_WT
					      ,YD_EQP_WRK_SH   = :V_YD_EQP_WRK_SH   --:자동
					 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
				 */
				
				intRtnVal = commDao.insert(recTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdPrepsch10", logId, methodNm, "등록");

				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 등록 성공 - 메소드 끝";
				slabUtils.printLog(logId,  szMsg, "SL");
				
				slabUtils.printLog(logId, methodNm, "S-");
				
				jrRtn.setField("RTN_CD"	, "1");
				jrRtn.setField("RTN_MSG", "정상처리되었습니다.");	
				return jrRtn;
				
			} catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}
		} 


	    /**
		 * [A] 오퍼레이션명: 차량상차 정보조회 _수정   								             
		 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량상차정보조회 
		 * PYS 2020-10-19    
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inParam
		 * @return
		 * @throws DAOException
		 */
		public JDTORecord updpectionComplete(GridData gdReq) throws DAOException {
			String methodNm = "차량상차 정보조회 _수정 [PSlabYdMvCarSeEJB.updpectionComplete] < " +  gdReq.getNavigateValue();
			String logId = gdReq.getIPAddress();

			try{
				slabUtils.printLog(logId, methodNm, "S+");
				int    lCnt         		= 0;
				String szTrtGp	         	= "";
				String rtnEcod           	= "";
				String szMsg             	= "";
				String szYdCarUppLocCd      = "";
				
				String sModifier   = slabUtils.trim(gdReq.getParam("userid" )); 			//수정자(MODIFIER)
				JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
				JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set
				
				int rowCnt   = gdReq.getHeader("CHECK").getRowCount(); //단건  

				szTrtGp = slabUtils.trim(gdReq.getParam("TRT_GP" ));
				slabUtils.printLog(logId, "작업구분(TRT_GP):" + szTrtGp , "SL");
				/*
				 *  수정할 항목   				
				 */

				if(rowCnt <= 0) {
					rtnEcod = "0";
					szMsg = "선택된 작업대상건이 없습니다.";
					jrRtn.setField("RTN_CD"	, rtnEcod);
					jrRtn.setField("RTN_MSG", szMsg);
					return jrRtn;
				}
				for (int ii = 0; ii < rowCnt; ii++) {
					
					szYdCarUppLocCd = slabUtils.nvl(slabUtils.getValue(gdReq, "YD_CAR_UPP_LOC_CD"       ,ii), ""  );
					
					jrParam.setField("STL_NO" 				, slabUtils.nvl(slabUtils.getValue(gdReq, "STL_NO"    			,ii), ""  ));
					jrParam.setField("YD_CAR_UPP_LOC_CD" 	, slabUtils.nvl(slabUtils.getValue(gdReq, "YD_STK_BED_NO"       ,ii), ""  ));//차상위치
					jrParam.setField("YD_STK_BED_NO" 	    , slabUtils.nvl(slabUtils.getValue(gdReq, "YD_STK_BED_NO"       ,ii), ""  ));//차상위치
					jrParam.setField("YD_STK_LYR_NO" 	    , slabUtils.nvl(slabUtils.getValue(gdReq, "FIX_YD_STK_LYR_NO"   ,ii), ""  ));//차상단

					jrParam.setField("YD_WBOOK_ID" 			, slabUtils.nvl(slabUtils.getValue(gdReq, "YD_WBOOK_ID"			,ii), ""  ));
					jrParam.setField("YD_CAR_SCH_ID" 		, slabUtils.nvl(slabUtils.getValue(gdReq, "YD_CAR_SCH_ID"    	,ii), ""  ));
					jrParam.setField("DEL_YN"               , "N");
					//TB_YD_STOCK 에  YD_CAR_UPP_LOC_CD 변경 처리 
					
					if(!"".equals(szYdCarUppLocCd)) {
						
						/*  
						UPDATE TB_YD_STOCK 
						SET 
							 MODIFIER           = :V_MODIFIER
							,MOD_DDTT           = SYSDATE
						    ,YD_CAR_UPP_LOC_CD  = :V_YD_CAR_UPP_LOC_CD  --야드차상위치코드 
						    ,YD_STK_LYR_NO      = :V_YD_STK_LYR_NO      --야드적치단번호 
						WHERE STL_NO            = :V_STL_NO
						*/
						lCnt  = commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdStock11", logId, methodNm, "수정");
						if(lCnt > 0 ) {
							rtnEcod = "1";
							szMsg   = "수정되었습니다.";
						} else {
							rtnEcod = "0";
							szMsg = "작업대상건이 없습니다.";
							jrRtn.setField("RTN_CD"	, rtnEcod);
							jrRtn.setField("RTN_MSG", szMsg);
							return jrRtn;
						}
					}
					
					/*  
					UPDATE TB_YD_WRKBOOKMTL 
						SET   MODIFIER        = :V_MODIFIER
						     ,MOD_DDTT        = SYSDATE
						     ,YD_STK_LYR_NO   = :V_YD_STK_LYR_NO
						WHERE STL_NO          = :V_STL_NO
						  AND YD_WBOOK_ID     = :V_YD_WBOOK_ID
						  AND DEL_YN          = 'N'
					*/
					lCnt  = commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdWrkbookmtl10", logId, methodNm, "수정");
					if(lCnt > 0 ) {
						rtnEcod = "1";
						szMsg   = "수정되었습니다.";
					} else {
						rtnEcod = "0";
						szMsg = "작업대상건이 없습니다.";
						jrRtn.setField("RTN_CD"	, rtnEcod);
						jrRtn.setField("RTN_MSG", szMsg);
						//return jrRtn;
					}
					/*
						UPDATE TB_YD_CARFTMVMTL
						      SET MODIFIER          = :V_MODIFIER
						         ,MOD_DDTT          = SYSDATE
						         ,DEL_YN            = :V_DEL_YN
						         ,YD_STK_BED_NO     = :V_YD_STK_BED_NO
						         ,YD_STK_LYR_NO     = :V_YD_STK_LYR_NO
						  WHERE YD_CAR_SCH_ID       = :V_YD_CAR_SCH_ID
						    AND STL_NO              = :V_STL_NO
					 */
					lCnt  = commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdCarftmvmtl01", logId, methodNm, "수정");
					if(lCnt > 0 ) {
						rtnEcod = "1";
						szMsg   = "수정되었습니다.";
					} else {
						rtnEcod = "0";
						szMsg = "작업대상건이 없습니다.";
						jrRtn.setField("RTN_CD"	, rtnEcod);
						jrRtn.setField("RTN_MSG", szMsg);
						return jrRtn;
					}
				}
				
				slabUtils.printLog(logId, szTrtGp + ":" +rtnEcod + ">" + szMsg, "S-");
				
				slabUtils.printLog(logId, methodNm, "S-");
				jrRtn.setField("RTN_CD"	, rtnEcod);
				jrRtn.setField("RTN_MSG", szMsg);
				
				return jrRtn;
				
			} catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}
		}
		

		/**
		 * 오퍼레이션명 : (공통)야드저장위치를 진행관리의 공통테이블에  업데이트하는 메소드      (--내부처리모듈--)
		 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리 
		 * PYS  2020-10-29
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param msgRecord
		 * @param szFromMethod
		 * @return
		 */
	    public String setYdStrLocToPtComm (JDTORecord msgRecord, String szFromMethod , String logId) throws JDTOException {
	    	String methodNm = "야드저장위치수정(공통)[PSlabYdMvCarSeEJB.setYdStrLocToPtComm]"+ msgRecord.getResultMsg();
	    	/*
	    	 * 업무기준 :1. 재료품목으로 재료종류를 판단하여 재료공통을 조회
	    	 * 		 2. 재료공통에 현재 야드저장위치를 수정
	    	 * 파라미터 :
	    	 * 			1)	주편인경우         MSLAB_NO
	    	 * 			   	슬라브인경우     SLAB_NO
	    	 * 			 	후판제품인 경우 PLATE_NO
	    	 * 				코일인경우        COIL_NO
	    	 * 			2)	재료품목          YD_MTL_ITEM
	    	 * 			3)	YD_STK_COL_GP	: 적치열구분
	    	 * 			4)	YD_STK_BED_NO 	: 적치베드번호
	    	 * 			5)	YD_STK_LYR_NO	: 적치단번호
	    	 */
	    	String szOperationName				= "야드저장위치수정(공통)";
	    	String szRtnMsg						= PSlabYdConstant.RETN_CD_SUCCESS;
	    	String szMsg						= "" ;
	    	String szSTL_NO						= "";
	    	String szCurYdStrLoc				= "";
	    	String szYdStrLoc					= "" ;		//현재저장위치
	    	String szYdStrLocHis1				= "" ;		//이전저장위치
	    	String szYdMtlItem					= "" ;		//재료품목 정의
	    	//크레인스케줄의 정보				 
	    	String szYdGp						= "" ;
	    	String szYdBayGp					= "" ;
	    	String szYdEqpId					= "" ;
	    	String szYdStkColNo					= "" ;
	    	String szYdStkBedNo					= "" ;
	    	String szYdStkLyrNo					= "" ;
	    	
	    	String szYD_MTL_ITEM				= "";		//재료품목
	    	String szYD_MTL_ITEM_NM				= "";
	    	String szYD_STK_COL_GP				= "";
	    	String szYD_STK_BED_NO				= "";
	    	String szYD_STK_LYR_NO				= "";
	    	String queryId                      = "";
	    	
	    	int intRtnVal 						= -100 ;
	    	int intGp							= -1;
	    	//------------------------------------------------------------------------------------------------------
	    	//	파라미터 확인
	    	//------------------------------------------------------------------------------------------------------
	        szMsg = "["+szOperationName+"] 메소드 시작 - 파라미터 확인";
	        slabUtils.printLog(logId, szMsg, "SL");
	        szYD_MTL_ITEM   = slabUtils.paraRecChkNull(msgRecord, "YD_MTL_ITEM");
	        szYD_STK_COL_GP = slabUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
	        szYD_STK_BED_NO = slabUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
	        szYD_STK_LYR_NO = slabUtils.paraRecChkNull(msgRecord, "YD_STK_LYR_NO");
	        //ydUtils.displayRecord(szOperationName, msgRecord);
	        
			String sModifier     = slabUtils.paraRecChkNull(msgRecord, "userid");;

			JDTORecord getRecord = slabUtils.getParam(logId, methodNm, sModifier);
			JDTORecord recPara   = slabUtils.getParam(logId, methodNm, sModifier);
			JDTORecord setRecord = slabUtils.getParam(logId, methodNm, sModifier);
	        
	        if( szYD_MTL_ITEM.equals("") ) {
	        	szMsg = "["+szOperationName+"] 재료품목이 존재하지 않습니다.";
	            slabUtils.printLog(logId, szMsg, "SL");
	            return PSlabYdConstant.RETN_CD_NO_PARAM;
	        }
	        
	        if( szYD_STK_COL_GP.equals("") ) {
	        	szMsg = "["+szOperationName+"] 적치열정보["+szYD_STK_COL_GP+"]가 존재하지 않습니다.";
	            slabUtils.printLog(logId, szMsg, "SL");
	            return PSlabYdConstant.RETN_CD_NO_PARAM;
	        }
	        //------------------------------------------------------------------------------------------------------
	        
	        //------------------------------------------------------------------------------------------------------
	    	//재료품목으로 비교하고 품목에따라 맞는 공통테이블을 조회한다.
	        //------------------------------------------------------------------------------------------------------
			szYdMtlItem = szYD_MTL_ITEM.substring(0, 1) ;
			recPara = JDTORecordFactory.getInstance().create();
			
			slabUtils.printLog(logId, "<" + szYdMtlItem +"> YD_MTL_ITEM" + szYD_MTL_ITEM, "SL");
			
			if (szYdMtlItem.equals("P")) {
	    		szYD_MTL_ITEM_NM = "PLATE";
	            szSTL_NO = slabUtils.paraRecChkNull(msgRecord, "PLATE_NO");
	            intGp = 4;
	            //queryId = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECOMM_PIDEV"; //(old) TABLE:USRPTA.TB_PT_PLATECOMM
	            queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getPLATECOMM";
	            recPara.setField("PLATE_NO", szSTL_NO);
	            
			} else if (szYdMtlItem.equals("S")) {
	    		szYD_MTL_ITEM_NM = "슬라브";
	            szSTL_NO = slabUtils.paraRecChkNull(msgRecord, "SLAB_NO");
	            intGp = 2;
	            //queryId = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSLABCOMM"; //(old) TABLE:TB_PT_SLABCOMM
	            queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdCommDAO.getSLABCOMM";
	            recPara.setField("SLAB_NO", szSTL_NO);
			} else if(szYdMtlItem.equals("B")){
	    		//주편공통
	    		szYD_MTL_ITEM_NM = "주편";
	            szSTL_NO = slabUtils.paraRecChkNull(msgRecord, "MSLAB_NO");
	            intGp = 6;
	            //queryId = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMSLABCOMM"; //(old) TABLE:USRPTA.TB_PT_MSLABCOMM
	            queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getMSLABCOMM";
	            recPara.setField("MSLAB_NO", szSTL_NO);
	    	}
	    	
	    	szMsg = "["+szOperationName+"] 해당재료["+szSTL_NO+"]를 "+szYD_MTL_ITEM_NM+"공통테이블에서 조회 시작";
	        slabUtils.printLog(logId, szMsg, "SL");

	        JDTORecordSet getRecSet = commDao.select(recPara, queryId, logId, methodNm, "존재 여부조회");
	        intRtnVal = getRecSet.size();
	        //getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	    	//intRtnVal = ydStockDao.getYdStock(recPara, getRecSet, intGp);
	    	
	    	if( intRtnVal < 0 ) {
	    		szMsg = "["+szOperationName+"] 해당하는 재료["+szSTL_NO+"]가 "+szYD_MTL_ITEM_NM+"공통테이블 조회 시 오류발생 - 반환값 : " + intRtnVal;
	            slabUtils.printLog(logId, szMsg, "SL");
	            return PSlabYdConstant.RETN_CD_FAILURE;
	            
	    	}else if( intRtnVal == 0 ) {
	    		szMsg = "["+szOperationName+"] 해당하는 재료["+szSTL_NO+"]가 "+szYD_MTL_ITEM_NM+"공통테이블  조회 시  존재하지 않습니다.";
	            slabUtils.printLog(logId, szMsg, "SL");
	    		return PSlabYdConstant.RETN_CD_NOTEXIST;
	    	}
	    	
	    	szMsg = "["+szOperationName+"] 해당하는 재료["+szSTL_NO+"]가 "+szYD_MTL_ITEM_NM+"공통테이블에 존재합니다.";
	        slabUtils.printLog(logId, szMsg, "SL");
	    	
	    	getRecSet.first();
	    	getRecord 			= getRecSet.getRecord() ;
	    	szYdStrLoc 			= slabUtils.paraRecChkNull(getRecord, "YD_STR_LOC");
	    	szYdStrLocHis1 		= slabUtils.paraRecChkNull(getRecord, "YD_STR_LOC_HIS1");
	    	
	    	//------------------------------------------------------------------------------------------------------
	    	//	공통테이블 저장위치 수정
	    	//------------------------------------------------------------------------------------------------------
	    	szYdGp 				= szYD_STK_COL_GP.substring(0, 1); 
	    	szYdBayGp 			= szYD_STK_COL_GP.substring(1, 2);
	    	szYdEqpId 			= szYD_STK_COL_GP.substring(2, 4); 
	    	szYdStkColNo 		= szYD_STK_COL_GP.substring(4); 
	    	szYdStkBedNo 		= szYD_STK_BED_NO; 
	    	szYdStkLyrNo		= szYD_STK_LYR_NO;
	        //szYdDnWrLoc         = msgRecord.getFieldString("YD_DN_WR_LOC");
	        
	    	//setRecord 			= JDTORecordFactory.getInstance().create();
	    	setRecord.setField("YD_GP"				, szYdGp);
	    	setRecord.setField("YD_BAY_GP"			, szYdBayGp);
	    	setRecord.setField("YD_EQP_GP"			, szYdEqpId);
	    	setRecord.setField("YD_STK_COL_NO"		, szYdStkColNo);
	    	setRecord.setField("YD_STK_BED_NO"		, szYdStkBedNo);
	    	setRecord.setField("YD_STK_LYR_NO"		, szYdStkLyrNo);
	    	setRecord.setField("FNL_REG_PGM"		, szFromMethod);
	        
	    	//전저장위치 = szYdStrLoc,	 전전저장위치 = szYdStrLocHis1
	    	setRecord.setField("YD_STR_LOC_HIS1"	, szYdStrLoc) ;
	    	setRecord.setField("YD_STR_LOC_HIS2"	, szYdStrLocHis1) ;
	    	
	    	szMsg = "["+szOperationName+"] 수정 전 - 조회된 현 저장위치 : " + szYdStrLoc + " , 전 저장위치 : " + szYdStrLocHis1;
	        slabUtils.printLog(logId, "" + szMsg, "SL");
	        
	        slabUtils.printLog(logId, "" + szYdMtlItem, "SL");
	        if (szYdMtlItem.equals("P")) {			//PLATE공통 업데이트
	     		//PLATE공통테이블에 현저장위치 자리수가 잘못등록 (야드구분+동구분+설비구분+열번호+베드번호1자리+단번호3자리)
	     		if( szYdStkBedNo.equals("") ) {
	     			szCurYdStrLoc = szYdGp+szYdBayGp+szYdEqpId+szYdStkColNo;
	     		}else{
	     			szCurYdStrLoc = szYdGp+szYdBayGp+szYdEqpId+szYdStkColNo+szYdStkBedNo.substring(1,2)+szYdStkLyrNo;
	     		}
	    		setRecord.setField("PLATE_NO"		,   szSTL_NO); 
	    		setRecord.setField("YD_STR_LOC"		,   szCurYdStrLoc);
	    		intGp = 1;
	    		queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updPtPlatecommLOC";
	    		//queryId = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtPlatecommLOC"; //(변경전)
	     	}else
	        if (szYdMtlItem.equals("S")) {			//슬라브 공통 업데이트
	    		szCurYdStrLoc = szYdGp+szYdBayGp+szYdEqpId+szYdStkColNo+szYdStkBedNo.substring(1)+szYdStkLyrNo;
	    		setRecord.setField("SLAB_NO"		,   szSTL_NO); 
	    		setRecord.setField("YD_STR_LOC"		,   szCurYdStrLoc);
	    		intGp = 0;
	    		queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updPtSlabcommLOC";
	    		//queryId = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtSlabcommLOC";  //(변경전)
	        } 
	        if("P".equals(szYdMtlItem) || "S".equals(szYdMtlItem) ) {
	        } else {
	        	slabUtils.printLog(logId, "##코드 확인##" + szYdMtlItem, "SL");
	        }

	    	//intRtnVal = ydStockDao.updPtComm_LOC(setRecord, intGp);
	    	intRtnVal = commDao.update(recPara, queryId, logId, methodNm, "수정작업");
	    	
	    	if(intRtnVal < 0) {
	            szMsg = "["+szOperationName+"] 해당하는 재료["+szSTL_NO+"]를 "+szYD_MTL_ITEM_NM+"공통Table의 현저장위치["+szCurYdStrLoc+"], 전저장위치["+szYdStrLoc+"], 전전저장위치["+szYdStrLocHis1+"] 등록 시 오류발생 - 반환값 : " + intRtnVal;
	            slabUtils.printLog(logId, szMsg, "SL");
	            return PSlabYdConstant.RETN_CD_FAILURE;
			}else if( intRtnVal == 0 ) {
				szMsg = "["+szOperationName+"] 해당하는 재료["+szSTL_NO+"]를 "+szYD_MTL_ITEM_NM+"공통Table의 현저장위치["+szCurYdStrLoc+"], 전저장위치["+szYdStrLoc+"], 전전저장위치["+szYdStrLocHis1+"] 등록 시 재료가 존재하지 않습니다. - " + intRtnVal;
				slabUtils.printLog(logId, szMsg, "SL");
	            return PSlabYdConstant.RETN_CD_NOTEXIST;
			}
	    	
	    	szMsg = "["+szOperationName+"] 해당하는 재료["+szSTL_NO+"]를 "+szYD_MTL_ITEM_NM+"공통Table의 현저장위치["+szCurYdStrLoc+"], 전저장위치["+szYdStrLoc+"], 전전저장위치["+szYdStrLocHis1+"] 등록 성공 ";
	        slabUtils.printLog(logId, szMsg, "SL");
	      //------------------------------------------------------------------------------------------------------
	        return szRtnMsg ;
	    }
	   
	    
		/**
		 * 오퍼레이션명 : 하차완료이송지시수정
		 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리                                                               //--내부 처리 모듈--
		 * PYS  2020-10-29
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param szSTL_NO
		 * @param szYD_MTL_PLN_STR_TO_LOC_CD
		 * @return
		 * @throws JDTOException
		 */
		public String uptPtStlFrtoMoveWhenCarUnLoadCmpl(String szSTL_NO, String szYD_MTL_PLN_STR_TO_LOC_CD, String logId) throws JDTOException {
			String methodNm             = "하차완료이송지시수정"+ "[PSlabYdMvCarSeEJB.uptPtStlFrtoMoveWhenCarUnLoadCmpl]";
			try{	
				String szMsg				= null;
				int intRtnVal				= -100;
				
				JDTORecord recInTemp		= null;
				JDTORecord recOutTemp		= null;
				JDTORecordSet	rsResultTemp	= null;
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("STL_NO", szSTL_NO);
				
				//----------------------------------------------------------------------------------------------------------
				//	이송지시테이블 조회
				//----------------------------------------------------------------------------------------------------------
				szMsg="["+methodNm+"] 후판제품재료["+ szSTL_NO +"]로 이송지시테이블 조회 시작.";
				slabUtils.printLog(logId, szMsg, "SL");
				
				//rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
				/*  # com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getPtStlFrtoMove
				 *  
				SELECT STL_NO AS STL_NO
				     , TRANSWORD_SEQNO AS TRANSWORD_SEQNO
				     , SPOS_WLOC_CD AS SPOS_WLOC_CD
				     , ARR_WLOC_CD
			 	  FROM USRPTA.TB_PT_STLFRTOMOVE A
				 WHERE STL_NO = :V_STL_NO
				   AND A.FRTOMOVE_STAT_CD  IN('1','3')
				   AND A.TRANSWORD_SEQNO =(SELECT / *+ INDEX_DESC(B PK_PT_STLFRTOMOVE)* /
				                              MAX(TRANSWORD_SEQNO)
				                             FROM TB_PT_STLFRTOMOVE B
				                            WHERE A.STL_NO = B.STL_NO
				                              AND FRTOMOVE_STAT_CD NOT IN ('Z','C')
				                              AND ROWNUM<=1
				                            )
				 */
				String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getPtStlFrtoMove";
				rsResultTemp = commDao.select(recInTemp, queryId, logId, methodNm, "조회");
				if( intRtnVal <= 0 ) {
					szMsg="[" + methodNm + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 후판제품재료[" + szSTL_NO + "]가 존재하지 않습니다.";
					slabUtils.printLog(logId, szMsg, "SL");
					return PSlabYdConstant.RETN_CD_NOTEXIST;
				}
				
				szMsg="["+methodNm+"] 후판제품재료["+ szSTL_NO +"]로 이송지시테이블 조회 시 존재합니다.";
				slabUtils.printLog(logId, szMsg, "SL");
				//----------------------------------------------------------------------------------------------------------
				
				//----------------------------------------------------------------------------------------------------------
				// 이송지시테이블 업데이트 - 이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드
				//----------------------------------------------------------------------------------------------------------
				rsResultTemp.first();
				recOutTemp = rsResultTemp.getRecord();
				
				recOutTemp.setField("YD_MTL_PLN_STR_TO_LOC_CD", szYD_MTL_PLN_STR_TO_LOC_CD);
				//이송상태코드(이송완료)
				recOutTemp.setField("FRTOMOVE_STAT_CD", "*");
				
				szMsg="[" + methodNm + "]  진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]- 후판제품재료번호["
				+ szSTL_NO + "], 이송지시차수[" 
				+ slabUtils.paraRecChkNull(recOutTemp, "TRANSWORD_SEQNO") + "]에  이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드 업데이트 시작";
				slabUtils.printLog(logId, szMsg, "SL");
				
				/* 이송완료일자, 이송계상일자, 이송상태코드 업데이트 - com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updPtStlFrtoMove2

			  UPDATE USRPTA.TB_PT_STLFRTOMOVE
				SET FRTOMOVE_DONE_DATE      = SYSDATE-0.00003
				, FTMV_HDS_DD               = TO_CHAR(SYSDATE - (6 / 24), 'YYYYMMDD')
				, YD_MTL_PLN_STR_TO_LOC_CD  = :V_YD_MTL_PLN_STR_TO_LOC_CD
				, FRTOMOVE_STAT_CD          = :V_FRTOMOVE_STAT_CD
				, MODIFIER                  = 'ydCodeset'
				, MOD_DDTT                  = sysdate
				WHERE STL_NO                = :V_STL_NO
				AND TRANSWORD_SEQNO         = :V_TRANSWORD_SEQNO
				AND STL_NO NOT IN(
				       SELECT CM.STL_NO
				          FROM TB_YD_CARSCH CS
				             , TB_YD_CARFTMVMTL CM
				           WHERE CS.YD_CAR_SCH_ID = CM.YD_CAR_SCH_ID
				             AND CS.REGISTER = 'runTsRetHt'
				             AND CM.STL_NO   = :V_STL_NO
				             AND ROWNUM      = 1
				             AND CS.DEL_YN   = 'N')
				 */
				intRtnVal = commDao.update(recOutTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updPtStlFrtoMove2", logId, methodNm, "야드재료예정저장To위치코드 업데이트");
				
				if( intRtnVal <= 0 ) {
					szMsg="[" + methodNm + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에  이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드 업데이트 실패";
					slabUtils.printLog(logId, szMsg, "SL");
					return PSlabYdConstant.RETN_CD_FAILURE;
				}
				szMsg="[" + methodNm + "]  진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]- 후판제품재료번호[" + szSTL_NO + "], 이송지시차수[" 
				+ slabUtils.paraRecChkNull(recOutTemp, "TRANSWORD_SEQNO") 
				+ "]에  이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드 업데이트 성공";
				slabUtils.printLog(logId, szMsg, "SL");
				//----------------------------------------------------------------------------------------------------------
				return PSlabYdConstant.RETN_CD_SUCCESS;
				
			} catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}
		}


		/**
		 * 오퍼레이션명 : (3-2)차량작업관리 상차LOT편성 취소 : 후판
		 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리 
		 * PYS  2020-10-29
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return 
		 * @return 
		 * @return
		 * @throws DAOException
		 */
		public JDTORecord cancelCarLdLot(GridData inDto) throws DAOException {
			String methodNm = "차량작업관리 상차LOT편성 취소 [PSlabYdMvCarSeEJB.cancelCarLdLot] < " + inDto.getNavigateValue();
			String logId    = inDto.getIPAddress();
			/*
			 * 업무기준 : 	0. 스케줄존재유무 확인 후 스케줄이 기동상태면 에러처리
			 * 			1. 그리드에 선택된 작업예약재료를 삭제처리
			 * 			2. 작업예약재료가 모두 삭제되었으면
			 * 				2-1. 작업예약도 같이 삭제처리
			 * 				2-2. 차량스케줄의 상차작업예약ID를 NULL로 수정
			 * 					2-2-1. 작업 예정이거나 작업중인 동일상차작업이 남아있으면 동일상차작업예약ID를 차량스케줄의 상차작업예약ID로 update(남은 작업은 진행해야 하므로)
			 * 					2-2-2. 작업 예정이거나 작업중인 동일상차작업이 없으면  차량스케줄의 상차작업예약ID를 NULL로 수정
			 * 			  	2-3. 상차작업예약ID와 연관된 준비스케줄과 준비재료의 DEL_YN항목을 N으로 설정
			 * 				2-4. 예약된 차량정지POINT를 삭제처리
			 *
			 */
			
			try {
				slabUtils.printLog(logId, methodNm, "S+");
				slabUtils.printLog(logId, "◑ 후판슬라브야드 차량작업관리 상차LOT편성 취소 START!", "SL");
				
				//JDTO 변수정의
				JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
				//JDTORecordSet  outRecSet  = null;
				
				//기본 변수 정의
				String szMsg        		= "";		
				int    intRtnVal			= 0;
				
				//로컬변수 정의
				String szSTL_NO 			= "";
				String szCRUD 				= "";
				String ydWbookId 			= "";
				//String szYdCarSchId		= "";
				//String szYdPrepSchId		= "";
				
				int intRUDCnt 				= 0;
				
				String sModifier     = slabUtils.trim(inDto.getParam("userid" )); 			//수정자(MODIFIER)
				JDTORecord jrRtn     = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
				JDTORecord jrParam   = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set

				//szYdCarSchId = slabUtils.trim(inDto.getParam("YD_CAR_SCH_ID" ));
				
				//szMsg = "[JSP Session : "+methodNm+"] 메소드 시작 - 차량스케줄확인[" + szYdCarSchId + "]";
				//slabUtils.printLog(logId, ""+szMsg, "S+");
				
				int rowCnt = inDto.getHeader("CHECK").getRowCount(); //단건
				
				slabUtils.printLog(logId, "◑크레인스케줄 기동 확인중...", "SL");
				
				/**********************************************************************************
				 * 0. 스케쥴 존재여부 확인
				 **********************************************************************************/
				//작업예약ID 추출
				ArrayList alWbookId = new ArrayList();
				for(int i = 0; i < rowCnt; i++ ) {
					ydWbookId = slabUtils.nvl(slabUtils.getValue(inDto, "YD_WBOOK_ID", i), "");
					slabUtils.printLog(logId, i + "번째 재료의 ydWbookId ["+ i + "]" + ydWbookId, "SL");
					if (!alWbookId.contains(ydWbookId)) {
						slabUtils.printLog(logId, "▷ 크레인스케줄 검색대상 작업예약ID : "+ ydWbookId, "SL");
						alWbookId.add(ydWbookId);
					}
				}
				
				
				slabUtils.printLog(logId, "◑ " + alWbookId.size() + "건의 작업예약이 스케줄 기동이 되었는지 확인합니다.", "SL");
				//작업예약ID별 크레인스케쥴 존재여부 확인
				if (alWbookId.size() > 0) {
					for(int j = 0; j < alWbookId.size(); j++ ) {
						ydWbookId = (String)alWbookId.get(j);
						slabUtils.printLog(logId, "▷ " + j + "번째 작업예약ID : " + ydWbookId, "SL");
						
						//기본정보조회
						jrParam.setField("YD_WBOOK_ID", ydWbookId);
						/*
						 * 크레인작업관리 작업취소 조회 
							SELECT YD_CRN_SCH_ID
							      ,YD_WRK_PROG_STAT
							      ,YD_EQP_ID
							      ,YD_SCH_CD
							      ,MODIFIER
							  FROM TB_YD_CRNSCH
							 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
							   AND DEL_YN      = 'N'
							 ORDER BY YD_CRN_SCH_ID
						 */
		                JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCrnWrkMgtWCSch", logId, methodNm, "작업취소 스케줄정보 조회");
			    		
					    if (jsCrn != null && jsCrn.size() > 0) {
					    	slabUtils.printLog(logId, "◑작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 존재하여 취소가 불가능합니다.", "SL");
							jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", "작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 존재하여 취소가 불가능합니다.");
							return jrRtn;
					    }
					}
				}
				
				slabUtils.printLog(logId, "◑크레인스케줄 기동 확인완료. 기동된 크레인스케줄이 없으므로 작업LOT 삭제 시작합니다.", "SL");
				
				/**********************************************************************************
				 * 1. 그리드에 선택된 작업예약재료를 삭제처리
				 **********************************************************************************/
				//여러건의 로우를 처리 - 등록 및 수정
				for(int i = 0; i < rowCnt; i++ ) {
					szCRUD    = slabUtils.nvl(slabUtils.getValue(inDto, "CRUD"       , i), "");
					ydWbookId = slabUtils.nvl(slabUtils.getValue(inDto, "YD_WBOOK_ID", i), "");
					szSTL_NO  = slabUtils.nvl(slabUtils.getValue(inDto, "STL_NO"     , i), "");
					//szUser    = slabUtils.nvl(slabUtils.getValue(inDto, "YD_USER_ID" , i), "");
					
					szMsg = "◑[JSP Session : "+methodNm+"] ["+( i + 1 )+"]재료번호 - CRUD["+szCRUD+"], YD_WBOOK_ID["+ydWbookId+"], STL_NO["+szSTL_NO+"] 삭제 시작";
					slabUtils.printLog(logId, ""+szMsg, "SL");
					
					if( !szCRUD.equals("C") ) {
						recPara.setField("STL_NO"		, szSTL_NO	);
						recPara.setField("YD_WBOOK_ID"	, ydWbookId	);
						recPara.setField("DEL_YN"		, "Y"		);
						recPara.setField("MODIFIER"		, sModifier	);
						/*
						 * UPDATE TB_YD_WRKBOOKMTL
						      SET 
						          MODIFIER = :V_MODIFIER
						         ,MOD_DDTT = SYSDATE
						         ,DEL_YN = :V_DEL_YN 
						  WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID 
						    AND STL_NO = :V_STL_NO
						 */
						intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdWrkbookmtl", logId, methodNm, "수정");
						
						if( intRtnVal < 0 ) {
							szMsg = "[JSP Session : "+methodNm+"] 작업예약재료["+szSTL_NO+"] 삭제 시 오류발생 : 반환값 - " + intRtnVal;
							slabUtils.printLog(logId, ""+szMsg, "SL");
							
							jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", szMsg);
							return jrRtn;
						}else if( intRtnVal == 0 ) {
							szMsg = "[JSP Session : "+methodNm+"] 작업예약재료["+szSTL_NO+"] 삭제 시 해당 재료가 존재하지 않음 - " + intRtnVal;
							slabUtils.printLog(logId, ""+szMsg, "SL");
							
							jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", szMsg);
							return jrRtn;
						}else{
							szMsg = "[JSP Session : "+methodNm+"] 작업예약재료["+szSTL_NO+"] 삭제 성공 ";
							slabUtils.printLog(logId, ""+szMsg, "SL");
						}
						intRUDCnt++;
					}
				}
				slabUtils.printLog(logId, "◑작업예약 재료 삭제완료. 삭제된 재료 건 수(" + intRUDCnt + ")", "SL");
				
				/**********************************************************************************
				 * 2. 작업예약재료가 모두 삭제되었으면 작업예약 삭제처리
				 *   -> trtWrkBookCncl(작업예약취소처리) -> 해당 모듈에 차량스케줄과 준비스케줄 복구 등 동일상차(2Bed) 관련 로직이 적용되어 있음
				 **********************************************************************************/
				if( intRUDCnt > 0 ) {
					
					szMsg = "[JSP Session : "+methodNm+"] 작업예약[" + ydWbookId + "] 삭제 시작 ";
					slabUtils.printLog(logId, ""+szMsg, "SL");
					
					slabUtils.printLog(logId, "◑ 작업예약에 남아있는 재료 확인후 작업재료가 없으면 작업예약삭제처리 시작.", "SL");
					for(int j = 0; j < alWbookId.size(); j++ ) {	// alWbookId = 중복 제거된 작업예약ID ArrayList 
						ydWbookId = (String)alWbookId.get(j);
						
						slabUtils.printLog(logId, "▷ " + j + "번 째 작업예약ID : " + ydWbookId, "SL");
						
						jrParam.setField("YD_WBOOK_ID", ydWbookId);
						slabUtils.printLog(logId, "◑ " + j + "번 째 작업예약ID(" + ydWbookId +")의 작업재료 매수를 조회합니다.", "SL");
						/*
						-- 삭제대상 작업예약 조회
						SELECT WRK.YD_WBOOK_ID                        AS YD_WBOOK_ID -- 작업예약ID
						     , WRK.YD_SCH_CD                          AS YD_SCH_CD   -- 스케줄 코드
						     , MAX(WRM.STL_NO     )                   AS STL_NO      -- 삭제되지 않은 재료 중 1개
						    FROM USRYDA.TB_YD_WRKBOOK    WRK            -- 작업예약
						       , USRYDA.TB_YD_WRKBOOKMTL WRM            -- 작업예약재료
						    WHERE WRK.YD_WBOOK_ID = WRM.YD_WBOOK_ID(+)
						      AND WRK.YD_WBOOK_ID = :V_YD_WBOOK_ID
						      AND WRK.YD_GP     = 'D'
						      AND WRK.DEL_YN    = 'N'
						      AND WRM.DEL_YN(+) = 'N'                   -- 작업예약은 표기하되 삭제된 재료번호는 NULL로 조회(OUT JOIN)
						GROUP BY WRK.YD_WBOOK_ID, WRK.YD_SCH_CD
						*/
						JDTORecordSet wrkMtlRs = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdWrkbookmtlId", logId, methodNm, "작업예약 재료 확인");
						
						if(wrkMtlRs.size() > 0){
							slabUtils.printLog(logId, "◑ " + j + "번 째 작업예약(" + ydWbookId + ") 살아있음.", "SL");
							
							String tmpSTL_NO = wrkMtlRs.getRecord(0).getFieldString("STL_NO");	
							slabUtils.printLog(logId, "▷ 작업예약재료 : " + tmpSTL_NO, "SL");
							
							if(!"".equals(tmpSTL_NO)){
								slabUtils.printLog(logId, "◑ " + j + "번 째 작업예약(" + ydWbookId + ")의 작업대상재료가 존재하므로 삭제처리 하지 않습니다.", "SL");
							} else {
								slabUtils.printLog(logId, "◑ " + j + "번 째 작업예약(" + ydWbookId + ")은 대상재료가 존재하지 않으므로 삭제처리 합니다.", "SL");
								slabUtils.printLog(logId, "◑ " + j + "번 째 작업예약(" + ydWbookId + ")작업 취소를 호출합니다.", "SL");
								
								jrParam.setField("YD_WBOOK_ID"	, ydWbookId											);	// 작업예약ID
						    	jrParam.setField("YD_SCH_CD"	, wrkMtlRs.getRecord(0).getFieldString("YD_SCH_CD")	);	// 스케쥴코드
							    
						    	slabUtils.printLog(logId, "▷ YD_WBOOK_ID : " + jrParam.getFieldString("YD_WBOOK_ID"	), "SL");
						    	slabUtils.printLog(logId, "▷ YD_SCH_CD   : " + jrParam.getFieldString("YD_SCH_CD"	), "SL");
						    	
						    	//JDTORecord jrWbkResult = this.trtWrkBookCncl(jrParam);
					    		EJBConnector ejbConn1 = new EJBConnector("default", "PSlabYdJspSeEJB", this);
								JDTORecord jrWbkResult = (JDTORecord)ejbConn1.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });

								
						    	String sRtnCd	= slabUtils.nvl(jrWbkResult.getFieldString("RTN_CD"), "0");
						    	String sRtnMsg	= slabUtils.trim(jrWbkResult.getFieldString("RTN_MSG"));
						    	
						    	if( !"1".equals(sRtnCd) ) {
									slabUtils.printLog(logId, ""+sRtnMsg, "SL");
									
									jrRtn.setField("RTN_CD"  , "0");
									jrRtn.setField("RTN_MSG" , sRtnMsg);
									return jrRtn;
						    	}
							}
						}else{
							// 삭제대상 작업예약이 기 삭제됐으면 삭제처리 Pass.
							slabUtils.printLog(logId, "◑ " + j + "번 째 작업예약(" + ydWbookId + ")은 이미 삭제된 작업예약입니다.", "SL");
						}
						
					} // END loop
				}
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 취소 끝";
				slabUtils.printLog(logId, "◑ "+szMsg, "SL");
				
				//szRtnMsg = PSlabYdConstant.RETN_CD_SUCCESS;
				
				jrRtn.setField("RTN_CD"	, "1");
				jrRtn.setField("RTN_MSG", "◑정상 처리되었습니다.");
				return jrRtn;
			} catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}	
		}
		

		/**
		 * 차량스케줄삭제  
		 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송차량 백업 처리 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param GridData gdReq
		 * @return JDTORecord
		 * @throws DAOException
		*/
		public JDTORecord initBookCarSch(GridData gdReq) throws DAOException {
			String methodNm = "차량스케줄삭제[PSlabYdMvCarSeEJB.initBookCarSch] <" + gdReq.getNavigateValue() +"]";
			String logId    = gdReq.getIPAddress();

			try {
				slabUtils.printLog(logId, methodNm, "S+", gdReq);

				String ydCarSchId = ""; 
				String queryId    = "";
				String sModifier  = slabUtils.trim(gdReq.getParam("userid"));
				//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
				JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, sModifier);
				
				//등록 할  레코드 수
				int rowCnt = gdReq.getHeader("CHECK").getRowCount();
				
				for(int i = 0; i < rowCnt; i++ ) {
					ydCarSchId		= slabUtils.trim(slabUtils.getValue(gdReq, "YD_CAR_SCH_ID"		, i));
					
					jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
					jrParam.setField("DEL_YN"		, "Y");
					/*
					UPDATE TB_YD_CARFTMVMTL
					   SET DEL_YN   = 'Y'
					     , MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updDelCarWrMgtSchMtl";
					commDao.update(jrParam, queryId, logId, methodNm, "차량작업재료 삭제");
					
					/*
					UPDATE TB_YD_CARSCH
					   SET MODIFIER  = :V_MODIFIER
					     , MOD_DDTT  = SYSDATE
					     , DEL_YN    = :V_DEL_YN
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					 */
					queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdCarSchReverseYN";
					commDao.update(jrParam, queryId, logId, methodNm, "차량스케줄 삭제");
				}
				
				slabUtils.printLog(logId, methodNm, "S-");		 

				jrRtn.setField("RTN_CD"	, "1");
				jrRtn.setField("RTN_MSG", "정상처리되었습니다.");
				return jrRtn;
				
			} catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}
		}  
		
		
		/**
		 *      [A] 오퍼레이션명 : 크레인사양분리
		 *      염용선 2020-07-10
		 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 *      @param JDTORecord jrCrnSpec
		 *      @param JDTORecordSet jsWrkMtl
		 *      @return Vector
		 *      @throws DAOException
		*/
		public Vector setCrnSpecSpr(JDTORecord jrCrnSpec, JDTORecordSet jsWrkMtl, String ydSchid) throws DAOException {
			String methodNm = "크레인사양분리[PSlabYdJspSeEJB.setCrnSpecSpr] < " + jrCrnSpec.getResultMsg();
			String logId = jrCrnSpec.getResultCode();

			try {
				slabUtils.printLog(logId, methodNm, "S+");

				Vector vcLot = new Vector();	//크레인사양분리결과
				JDTORecord    jrRow = null;		//현재 Row
				JDTORecordSet jsLot = JDTORecordFactory.getInstance().createRecordSet("");	//Lot
				String crnSpecOvGp  = "";		//크레인사양초과구분

				//크레인사양분리
				int   mtlSh    = 0;		//재료매수
				int   mtlWt    = 0;		//재료중량
				float mtlT     = 0;		//재료두께
				float mtlW     = 0;		//재료폭
				int   mtlWtSum = 0;		//재료중량합
				float mtlTSum  = 0;		//재료두께합
				float mtlWMax  = 0;		//재료폭최대

				String Is_plate_StoA_Scarfing = "N"; 
				int rowCnt = jsWrkMtl.size();

				//String sDyd201Yn1 = slabComm.AppDydYn("DYD201", "D", "1", "하차차량_크레인용량제한_제외처리", "iWrkBook");  //
				String sDyd201Yn32	= ""; // 하차차량_크레인용량제한_제외처리
				/* com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYnDualSql
				SELECT 'Y' AS APPLY_YN32         FROM DUAL
				*/  
				JDTORecordSet jsApplyYN32Chk = commDao.select(jrCrnSpec, "com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYnDualSql", logId, methodNm, "Rule-하차차량_크레인용량제한_제외처리 Read"); 
				if (jsApplyYN32Chk.size() > 0) {
					sDyd201Yn32    = slabUtils.trim(jsApplyYN32Chk.getRecord(0).getFieldString("APPLY_YN32"));
				}
				
				if(PSlabYdConstant.APPLY_YN.equals(sDyd201Yn32)) {
					if(PSlabYdConstant.YD_EQP_GP_PALLET.equals(ydSchid.substring(2,4))) {
						sDyd201Yn32 = "Y";
						slabUtils.printLog(logId, ydSchid.substring(2,4)+">하차차량 크레인사양에 따른 작업예약분리 예외 처리", "SL");
					} else {
						sDyd201Yn32 = "N";
					}
				} else {
					sDyd201Yn32 = "N";
				}
				for (int ii = 0; ii < rowCnt; ii++) {
					jrRow = jsWrkMtl.getRecord(ii);
					
					mtlWt = Integer.parseInt(slabUtils.nvl(jrRow.getFieldString("YD_MTL_WT"),"0"));
					mtlT  = Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_T" ),"0"));
					mtlW  = Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_W" ),"0"));
					Is_plate_StoA_Scarfing = slabUtils.nvl(jrRow.getFieldString("PLATE_SIDE_SCARFING_YN" ),"N");
					if (ii > 0) {
						mtlSh++;
						mtlWtSum += mtlWt;
						mtlTSum  += mtlT;
						
						jrCrnSpec.setField("MTL_SH_SUM", String.valueOf(mtlSh   ));	//재료매수
						jrCrnSpec.setField("MTL_WT_SUM", String.valueOf(mtlWtSum));	//재료중량합
						jrCrnSpec.setField("MTL_T_SUM" , String.valueOf(mtlTSum ));	//재료두께합
						jrCrnSpec.setField("MTL_W"     , String.valueOf(mtlW    ));	//재료폭
						jrCrnSpec.setField("MTL_W_MAX" , String.valueOf(mtlWMax ));	//재료폭최대
						
						jrCrnSpec.setField("PLATE_SIDE_SCARFING_YN" , Is_plate_StoA_Scarfing);	//통합적치이력 있는 후판재 사이드스카핑 여부
						//크레인사양 초과 Check
						crnSpecOvGp = slabComm.chkCrnSpec(jrCrnSpec);
						slabUtils.printLog(logId, "크레인사양 초과 Check : "+crnSpecOvGp, "SL");
						if(!"Y".equals(jsApplyYN32Chk)) {		//구내운송차량은 작업예약생성시 크레인사양에 때른 제약에서 제외함.  
							if (!"".equals(crnSpecOvGp)) {
								//이전 Lot 추가
								vcLot.add(jsLot);
								
								jsLot = JDTORecordFactory.getInstance().createRecordSet("");
								mtlSh    = 1;
								mtlWtSum = mtlWt;
								mtlTSum  = mtlT;
								mtlWMax  = mtlW;
							}
						} 
					} else {
						mtlWtSum = mtlWt;
						mtlTSum  = mtlT;
						mtlWMax  = mtlW;
					}
					if (mtlW > mtlWMax) { mtlWMax = mtlW; }

					jsLot.addRecord(jrRow);
				}
				
				//마지막 Lot 추가
				vcLot.add(jsLot);
				
				slabUtils.printLog(logId, methodNm, "S-");

				return vcLot;
			} catch (DAOException e) {
				throw e;
			} catch (Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}
		}

		
		/**
		 * [A] 오퍼레이션명 : 구내운송 회송처리							  
		 * 야드관리 > 후판슬라브야드  > Monitoring > 차량작업관리 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param GridData gdReq
		 * @return JDTORecord
		 * @throws DAOException
		*/
		public JDTORecord runTsRetHt(GridData gdReq) throws DAOException {
			String methodNm = "슬라브구내운송 회송처리[PSlabYdMvCarSeEJB.runTsRetHt] < " + gdReq.getNavigateValue(); 
			String logId    = gdReq.getIPAddress();
			/*
			 * 회송 처리 1) 회송사유  전문(YDTSJ016) , 회송이력 테이블 (TB_YD_RETHTHIST) 등록
			 *       2)상차완료 전문 ( ) , 상차완료 상태 (5)처리
			 *       3)구내운송> 상차 완료후 출발 전문 수신 --> 정정공정으로 출발     
			 */

			try {
				String szLogMsg = ""+methodNm+"] 메소드 시작";
				slabUtils.printLog(logId,  szLogMsg, "S+");
				String currDate   = slabUtils.getDateTime14();	//현재시각
				
				//Return Value
				String sModifier 	 	= slabUtils.trim(gdReq.getParam("userid"));		//수정자(Backup Only)

				JDTORecord jrRtn    	= slabUtils.getParam(logId, methodNm, sModifier);
				JDTORecord jrParam 		= slabUtils.getParam(logId, methodNm, sModifier);
				//JDTORecord recInTemp 	= slabUtils.getParam(logId, methodNm, sModifier);
				
				//DAO Parameter - Log ID, Method, 수정자 Set
				
				String sRetHt_ID = "";		//회송이력ID
				
				sRetHt_ID = commDao.getSeqId(logId, methodNm, "RetHt"); 
				
				int rowCnt = gdReq.getHeader("CHECK").getRowCount();
				int iHsCnt = 0;
				int irowCnt = 0;
				String returnMsg 	= "", sMsg = "";
				String ydWbookId 	= "", sTrnEqpCd = "";
				
				String sSposWlocCd  = "";
				String sSposYdPntcd = "";
				String sArrWlocCd   = ""; 
				String sArrYdPntcd  = ""; 
				String ydCarStopLoc = "";
				String ydPntcd3 	= "";
				String ydCarudStopLoc = ""; 
				String sOldCarSchId = ""; 
				String sYdCarSchId = "",  sYdCarSchIdBef = "";
				
				slabUtils.printLog(logId, "ㅇㅁㅁㅁㅁㅁㅇ 회송 대상재  (이송하차 작업예약 삭제처리) 작업대상건수 : " + rowCnt, "SL");		 
				
				/**********************************************************
				* 1. 회송 대상재 이송하차 작업예약 삭제처리
				**********************************************************/
				for (int ii = 0; ii < rowCnt; ii++) {
					String iRim = gdReq.getHeader("CMPL_GP").getComboHiddenValues()[gdReq.getHeader("CMPL_GP").getSelectedIndex(ii)];
					
					if("HS".equals(iRim)) { //회송인 경우만 처리..
						slabUtils.printLog(logId, "회송인 경우만 처리.." + iRim, "SL");
						
						ydWbookId = slabUtils.nvl(slabUtils.getValue(gdReq, "YD_WBOOK_ID", ii),""); 
						sTrnEqpCd = slabUtils.nvl(slabUtils.getValue(gdReq, "TRN_EQP_CD" , ii),"");
						
						slabUtils.printLog(logId, ii+">예약id"+ ydWbookId + " 차량No"+ sTrnEqpCd, "SL");
						jrParam.setField("YD_WBOOK_ID"	, ydWbookId);
						jrParam.setField("YD_EQP_GP"	, sTrnEqpCd.substring(1,3));
						jrParam.setField("MODIFIER"		, sModifier);
						returnMsg = this.delYdWrkbook(jrParam);
						
						//크레인스케쥴이 있는 작업재료가 있는경우 회송처리 불가.
						if(!PSlabYdConstant.RETN_CD_SUCCESS.equals(returnMsg)){
							sMsg = "Error: "+ gdReq.getHeader("STL_NO").getValue(ii) +" 재료는 회송불가 (크레인작업예약이 있어 회송이 불가합니다. 작업예약을 삭제해주세요.)";
							slabUtils.printLog(logId,  sMsg , "SL");
							jrRtn.setField("RTN_CD" , "0");
							jrRtn.setField("RTN_MSG", sMsg);
							return jrRtn;
						}
						iHsCnt++;
					}
				}

				if(iHsCnt == 0) {
					throw new Exception("회송 대상재가 없습니다! 회송처리 비정상 종료");
				}
				slabUtils.printLog(logId, "ㅇㅁㅁㅁㅁㅁㅇ1.회송 대상재 이송하차 작업예약 삭제처리 종료 ", "SL");
				
				
				slabUtils.printLog(logId, "ㅇㅁㅁㅁㅁㅁㅇ1-1. 차량 스케줄 점검  " , "SL");
				/*
				SELECT *
				  FROM (
				        SELECT    YD_CAR_SCH_ID
				                , DEL_YN
				                , YD_EQP_ID
				                , YD_CAR_USE_GP
				                , CAR_NO
				                , TRN_EQP_CD
				                , CAR_KIND
				                , TRANS_EQUIPMENT_TYPE
				                , YD_EQP_WRK_STAT
				                , YD_WRK_PROG_STAT
				                , YD_EQP_WRK_SH
				                , YD_EQP_WRK_WT
				                , YD_STK_BED_TP
				                , SPOS_WLOC_CD
				                , ARR_WLOC_CD
				                , YD_CARLD_LEV_LOC
				                , YD_CARLD_LEV_DT
				                , YD_CARLD_PNT_WO_DT
				                , YD_PNT_CD1
				                , YD_PNT_CD2
				                , YD_CARLD_WRK_BOOK_ID
				                , YD_CARLD_SCH_REQ_GP
				                , YD_CARLD_STOP_LOC
				                , YD_CARLD_ARR_DT
				                , YD_CARLD_ST_DT
				                , YD_CARLD_CMPL_DT
				                , YD_CARLD_WRK_ACT_GP
				                , YD_CARLD_CHK_DT
				                , YD_CARUD_LEV_DT
				                , YD_CARUD_PNT_WO_DT
				                , NVL(YD_PNT_CD3, '0000') AS YD_PNT_CD3
				                , YD_PNT_CD4
				                , YD_CARUD_WRK_BOOK_ID
				                , YD_CARUD_STOP_LOC
				                , YD_CARUD_SCH_REQ_GP
				                , YD_CARUD_ARR_DT
				                , YD_CARUD_CHK_DT
				                , YD_CARUD_ST_DT
				                , YD_CARUD_CMPL_DT
				                , YD_CARUD_WRK_ACT_GP
				                , YD_TRN_WRK_DELY_CD
				                , CARD_NO
				                , YD_CAR_PROG_STAT
				                , FRTOMOVE_PLANT_GP
				                , PROC_TO
				                , RENTPROC_CD
				                , YD_FRTOMOVE_YD_GP
				                , YD_FRTOMOVE_BAY_GP
				                , URGENT_FRTOMOVE_WORD_GP
				                , DEST_TEL_NO
				                , YD_DLVRDD_RULE_DD
				                , SHIPASSIGN_WORD_DATE
				                , SHIPASSIGN_WORD_SEQNO
				                , SHIP_CD
				                , SHIP_NAME
				                , RSHP_HOLD_NO
				                , BERTH_NO
				                , SAILNO
				                , YD_CAR_WRK_GP
				                , TRANS_ORD_DATE
				                , TRANS_ORD_SEQNO
				                , YD_BAYIN_WO_SEQ
				                , YD_CAR_RCPT_CHK_YN
				                , YD_CAR_ISSUE_CHK_YN
				                , YD_CAR_RCPT_CHECKER
				                , YD_CAR_ISSUE_CHECKER
				                , IF_SEQ_NO
				                , TEL_NO
				                , CMBN_CARLD_YN
				                , WAIT_ARR_DDTT
				                , WAIT_ARR_GP
				                , YD_MSG_NM
				                , FRTOMOVE_WORD_NO
				                , DRIVER_NAME
				                , DECODE(A.YD_CARLD_STOP_LOC,'',A.YD_CARUD_STOP_LOC,A.YD_CARLD_STOP_LOC) AS YD_CAR_STOP_LOC
				          FROM TB_YD_CARSCH A
				         WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				           AND DEL_YN     ='N'
				         ORDER BY YD_CAR_SCH_ID DESC
				                , YD_CARUD_CMPL_DT DESC
				       ) A
				 WHERE ROWNUM <= 1
				 */
				jrParam.setField("TRN_EQP_CD", sTrnEqpCd);
				String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdCarschByTrnEqpCd";
				JDTORecordSet jsCarSch = commDao.select(jrParam, queryId, logId, methodNm, "차량스케줄 조회");
				if (jsCarSch.size() > 0) {
					ydCarStopLoc     = slabUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_STOP_LOC" )); 
					sSposWlocCd      = slabUtils.trim(jsCarSch.getRecord(0).getFieldString("SPOS_WLOC_CD"	 )); 
					sArrWlocCd       = slabUtils.trim(jsCarSch.getRecord(0).getFieldString("ARR_WLOC_CD"	 )); 
					//ydPntcd1       = slabUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_PNT_CD1"	 )); //E공차 
					ydPntcd3         = slabUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_PNT_CD3"	 )); //F영차
					ydCarudStopLoc   = slabUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CARUD_STOP_LOC"	 ));
					sOldCarSchId     = slabUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
				}
				
				
				/**********************************************************
				* 2. 기존 차량 스케줄  상차완료 처리
				**********************************************************/
				//2-1.신규 스케줄 생성 
				slabUtils.printLog(logId, "ㅇㅁㅁㅁㅁㅁ 3.신규 회송 차량 스케줄 생성 시작 ", "SL");
				
				sYdCarSchId = commDao.getSeqId(logId, methodNm, "CarSch");
				
				sYdCarSchIdBef = gdReq.getHeader("YD_CAR_SCH_ID").getValue(0);
				if(!sOldCarSchId.equals(sYdCarSchIdBef )){
					jrRtn.setField("RTN_CD" , "0");
					jrRtn.setField("RTN_MSG", "차량스케줄 오류:" + sOldCarSchId + "="+sYdCarSchIdBef);
				}
				jrParam.setField("YD_CAR_SCH_ID"		, sYdCarSchId	);
				jrParam.setField("OLD_YD_CAR_SCH_ID"	, sYdCarSchIdBef); //기존 차량스케줄ID
				jrParam.setField("REGISTER"				, "runTsRetHt"	); //Pallet조회(B)에서 회송 표시를 하기위해 REGISTER 에 "runTsRetHt"를 입력한다.
				/* -- 회송 차량스케줄코드 생성(2)   
				INSERT INTO TB_YD_CARSCH (
				       YD_CAR_SCH_ID
				     , REGISTER
				     , REG_DDTT
				     , MODIFIER
				     , MOD_DDTT
				     , DEL_YN
				     , YD_EQP_ID
				     , YD_CAR_USE_GP
				     , CAR_NO
				     , TRN_EQP_CD
				     , CAR_KIND
				     , YD_EQP_WRK_STAT      
				     , SPOS_WLOC_CD
				     , ARR_WLOC_CD
				     , YD_PNT_CD1           --야드상차포인트(4자리)
				     , YD_PNT_CD3
				     , YD_CARLD_STOP_LOC    --야드상차정지위치(적치열6자리)
				     , YD_CARLD_CMPL_DT     --야드상차완료일시
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
				       , YD_EQP_ID
				       , YD_CAR_USE_GP
				       , CAR_NO
				       , TRN_EQP_CD
				       , CAR_KIND
				       , 'U' -- YD_EQP_WRK_STAT      
				       , ARR_WLOC_CD
				       , SPOS_WLOC_CD       
				       , YD_PNT_CD3
				       , YD_PNT_CD1
				       , YD_CARUD_STOP_LOC 
				       , SYSDATE
				       , '5' -- 상차완료  
				       , TRANS_ORD_DATE
				       , TRANS_ORD_SEQNO 
				       , '9'
				    FROM TB_YD_CARSCH
				   WHERE YD_CAR_SCH_ID = :V_OLD_YD_CAR_SCH_ID
				   
				 */
				commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.insRetHtCarSch2", logId, methodNm, "회송 차량스케줄 INSERT");
				
				slabUtils.printLog(logId, "신규car스케줄: "+ sYdCarSchId + "(변경전)"+ sYdCarSchIdBef , "SL"); 
				slabUtils.printLog(logId, "처리대상건수:"+ rowCnt, "SL");
				
				
				for (int ii = 0; ii < rowCnt; ii++) {
					String iRim = gdReq.getHeader("CMPL_GP").getComboHiddenValues()[gdReq.getHeader("CMPL_GP").getSelectedIndex(ii)];
					
					slabUtils.printLog(logId, "CMPL_GP>"+ iRim, "SL");
					
					
					if("HS".equals(iRim)) { //회송인 경우만 처리..
						jrParam.setField("YD_CAR_SCH_ID"		, sYdCarSchId);
						jrParam.setField("STL_NO"				, gdReq.getHeader("STL_NO").getValue(ii)); 
						jrParam.setField("DEL_YN"				, "N");
						jrParam.setField("YD_STK_BED_NO"		, gdReq.getHeader("YD_STK_BED_NO").getValue(ii));
						jrParam.setField("YD_STK_LYR_NO"		, gdReq.getHeader("YD_STK_LYR_NO").getValue(ii));  //적치단
						
						slabUtils.printLog(logId, ">>"+ sYdCarSchId + "--"+ gdReq.getHeader("STL_NO").getValue(ii) + "-->" + gdReq.getHeader("YD_STK_LYR_NO").getValue(ii), "SL"); 
						/*
						INSERT INTO TB_YD_CARFTMVMTL(
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
						VALUES ( 
						       :V_YD_CAR_SCH_ID
						     , :V_STL_NO
						     , :V_MODIFIER
						     , SYSDATE
						     , :V_MODIFIER
						     , SYSDATE
						     , :V_DEL_YN
						     , :V_YD_STK_BED_NO
						     , :V_YD_STK_LYR_NO
						)
						 */
						commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.insCarFtMvMtl", logId, methodNm, "회송 차량스케줄 재료 INSERT");
					}
				}
				slabUtils.printLog(logId, "ㅇㅁㅁㅁㅁㅁ 3.신규 회송 차량 스케줄 생성 (종료) ", "");
				

				sSposWlocCd 	= gdReq.getHeader("SPOS_WLOC_CD").getValue(0);
				sSposYdPntcd 	= gdReq.getHeader("YD_PNT_CD1").getValue(0);
				sArrWlocCd 		= gdReq.getHeader("ARR_WLOC_CD").getValue(0);
				sArrYdPntcd 	= gdReq.getHeader("YD_PNT_CD3").getValue(0);
				
				slabUtils.printLog(logId, "SPOS_WLOC_CD:" + sSposWlocCd + "="+ sSposYdPntcd , "SL"); 
				slabUtils.printLog(logId, "ARR_WLOC_CD :" + sArrWlocCd  + "="+ sArrYdPntcd , "SL"); 
				
				/*-- 
				SELECT NVL(DTL_ITEM1, '') DTL_ITEM1
				FROM TB_YD_RULE A
				WHERE REPR_CD_GP = :V_REPR_CD_GP 
				  AND CD_GP      = :V_CD_GP 
				*/
				jrParam.setField("REPR_CD_GP"	, "DYD001");
				jrParam.setField("CD_GP"		, sSposWlocCd);
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYdRuleItem1";
				JDTORecordSet jrRule = commDao.select(jrParam, queryId, logId, methodNm, "DJY25 POINT 조회");
				if(jrRule.size() > 0  ) {
					jrRule.first();
					sArrYdPntcd = slabUtils.trim(jrRule.getRecord().getFieldString("DTL_ITEM1"));
				}
				
				if(!"".equals(sArrYdPntcd )) {
					/**********************************************************
					 * 3-1. 회송처리 완료후 타공정 후속 작업 처리
					 **********************************************************/
					jrParam.setField("JMS_TC_CD"			, "TSYDJ004"); 	//운송장비코드
					jrParam.setField("JMS_TC_CREATE_DDTT"	, currDate); 	//운송장비코드

					jrParam.setField("TRN_EQP_CD"			, sTrnEqpCd); 	//운송장비코드
					jrParam.setField("SPOS_WLOC_CD"			, sSposWlocCd); //발지개소코드
					jrParam.setField("SPOS_YD_PNT_CD"		, sSposYdPntcd);//발지야드포인트코드
					jrParam.setField("ARR_WLOC_CD"			, sArrWlocCd); 	//착지개소코드
					jrParam.setField("ARR_YD_PNT_CD"		, sArrYdPntcd); //착지야드포인트코드
					
					jrParam.setField("TRN_WRK_FULLVOID_GP"	, "E"); //운송작업영공구분 E:공차 F:영차
					jrParam.setField("TRN_EQP_STK_CAPA"		, "80000"); //운송장비적재능력
					jrParam.setField("YD_WO_CNCL_YN"		, ""); //야드지시 취소여부
					jrParam.setField("YD_RETHT_HIST_ID"		, sRetHt_ID);
				}
				
	    		/**********************************************************
	    		 * 3-2. 이송상하차 후 출발착처리시  해당 포인트를 초기화 한다..
	    		 **********************************************************/
	    		 
				jrParam.setField("REPR_CD_GP"	, "DYD006" );
				jrParam.setField("CD_GP"		, ydCarStopLoc  );   
				jrParam.setField("ITEM"			, "D");
				jrParam.setField("DTL_ITEM1"	, "0");
				jrParam.setField("DTL_ITEM2"	, "0");
				jrParam.setField("DTL_ITEM3"	, "0");
				jrParam.setField("DTL_ITEM4"	, "");
				jrParam.setField("DTL_ITEM5"	, "N");
				jrParam.setField("DTL_ITEM6"	, "0");	
				jrParam.setField("DTL_ITEM7"	, "0");
				jrParam.setField("DTL_ITEM8"	, "0");
				jrParam.setField("DTL_ITEM9"	, "0");
				jrParam.setField("DTL_ITEM10"	, "");
				/*
				UPDATE TB_YD_RULE
				   SET DTL_ITEM1 = NVL(:V_DTL_ITEM1,DTL_ITEM1)
				      ,DTL_ITEM2 = NVL(:V_DTL_ITEM2,DTL_ITEM2)
				      ,DTL_ITEM3 = NVL(:V_DTL_ITEM3,DTL_ITEM3)
				      ,DTL_ITEM4 = NVL(:V_DTL_ITEM4,DTL_ITEM4)
				      ,DTL_ITEM5 = NVL(:V_DTL_ITEM5,DTL_ITEM5)
				      ,DTL_ITEM6 = NVL(:V_DTL_ITEM6,DTL_ITEM6)
				      ,DTL_ITEM7 = NVL(:V_DTL_ITEM7,DTL_ITEM7)
				      ,DTL_ITEM8 = NVL(:V_DTL_ITEM8,DTL_ITEM8)
				      ,DTL_ITEM9 = NVL(:V_DTL_ITEM9,DTL_ITEM9)
				      ,DTL_ITEM10 = NVL(:V_DTL_ITEM10,DTL_ITEM10)
				      ,MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				 WHERE REPR_CD_GP = :V_REPR_CD_GP
				   AND CD_GP = :V_CD_GP
				   AND ITEM = :V_ITEM
				 */                      
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdRuleNvl", logId, methodNm, "TB_YD_RULE DYD006 수정 - 초기화");

				/****************************************************
				 * 적치열 비활성       (COL)
				 * 적치포인트 비활성 (YDPOINT)
				 * 적치BED
				 * 적치단
				 ****************************************************/
		    	jrParam.setField("YD_STK_COL_GP", ydCarStopLoc);
				  
		    	
				//-----------------------------------------------------------------------
				// 기존 하차 스케줄을 하차 완료 처리 해야 하고, 다시 상차 
				
				//2-3.기존 스케줄 종료처리   
				jrParam.setField("YD_CAR_SCH_ID", sOldCarSchId);
				/*
				UPDATE TB_YD_CARSCH
				   SET DEL_YN = 'Y'
				      ,MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.delCarschID", logId, methodNm, "기존 차량스케줄정보삭제");
				
				/*
				 * 
				UPDATE TB_YD_CARFTMVMTL
				   SET MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				     , DEL_YN   = 'Y'
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updDelYnCarSchMtl", logId, methodNm, "기존 차량스케줄(재료) 삭제");

				
				//2-1 기존 차량스케줄의 소재 이송지시에 상차날짜 초기화
				/*
				MERGE INTO TB_PT_STLFRTOMOVE FM USING (
				SELECT FM.STL_NO
				      ,FM.TRANSWORD_SEQNO
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
				) DD ON (FM.STL_NO = DD.STL_NO AND FM.TRANSWORD_SEQNO = DD.TRANSWORD_SEQNO)
				WHEN MATCHED THEN UPDATE SET
					 FM.MODIFIER                 = :V_MODIFIER
				    ,FM.MOD_DDTT                 = SYSDATE
				    ,FM.FRTOMOVE_CARLOAD_DATE    = NULL
				 */
				//   
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updStlFrToMoveRetHt", logId, methodNm, "기존 차량스케줄의 소재 이송지시에 상차날짜 초기화");
			
				slabUtils.printLog(logId, "ㅇㅁㅁㅁㅁㅁㅇ  ", "SL");

				/**********************************************************
				* 3. 상차 처리 완료 전문(YDTSJ008) 송신 .. 
				**********************************************************/
				/*  --2021.07.12 허책임. 8번전문  제외요청   
				//jrParam.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
				slabUtils.printLog(logId, "차량스케줄Id:"+ sYdCarSchId + " (old)"+gdReq.getHeader("YD_CAR_SCH_ID").getValue(0) , "SL"); 

				recInTemp.setField("MSG_ID",        "YDTSJ008");
    			recInTemp.setField("YD_CAR_SCH_ID", sYdCarSchId ); 
    			jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ008", recInTemp));
    			*/
				/**********************************************************
				* 4. 하차 형상 전문 수신 내역 Clear
				**********************************************************/
				slabUtils.printLog(logId, "ㅇㅁㅁㅁㅁㅁ 3.하차 형상 전문 수신 내역 Clear ", "SL");
				
				sSposWlocCd 	= gdReq.getHeader("SPOS_WLOC_CD").getValue(0);
				sSposYdPntcd 	= gdReq.getHeader("YD_PNT_CD1").getValue(0);
				sArrWlocCd 		= gdReq.getHeader("ARR_WLOC_CD").getValue(0);
				sArrYdPntcd 	= gdReq.getHeader("YD_PNT_CD3").getValue(0);
				
				slabUtils.printLog(logId, "SPOS_WLOC_CD:" + sSposWlocCd + "=(포인트)"+ sSposYdPntcd , "SL"); 
				slabUtils.printLog(logId, "ARR_WLOC_CD :" + sArrWlocCd  + "=(포인트)"+ sArrYdPntcd , "SL"); 
				
				jrParam.setField("REPR_CD_GP"	, "DYD006" );
				jrParam.setField("CD_GP"		, ydCarStopLoc  );   
				jrParam.setField("ITEM"			, "D");
				jrParam.setField("DTL_ITEM1"	, "0");
				jrParam.setField("DTL_ITEM2"	, "0");
				jrParam.setField("DTL_ITEM3"	, "0");
				jrParam.setField("DTL_ITEM4"	, "");
				jrParam.setField("DTL_ITEM5"	, "N");
				jrParam.setField("DTL_ITEM6"	, "0");	
				jrParam.setField("DTL_ITEM7"	, "0");
				jrParam.setField("DTL_ITEM8"	, "0");
				jrParam.setField("DTL_ITEM9"	, "0");
				jrParam.setField("DTL_ITEM10"	, "");
				/*
				UPDATE TB_YD_RULE
				   SET DTL_ITEM1 = NVL(:V_DTL_ITEM1,DTL_ITEM1)
				      ,DTL_ITEM2 = NVL(:V_DTL_ITEM2,DTL_ITEM2)
				      ,DTL_ITEM3 = NVL(:V_DTL_ITEM3,DTL_ITEM3)
				      ,DTL_ITEM4 = NVL(:V_DTL_ITEM4,DTL_ITEM4)
				      ,DTL_ITEM5 = NVL(:V_DTL_ITEM5,DTL_ITEM5)
				      ,DTL_ITEM6 = NVL(:V_DTL_ITEM6,DTL_ITEM6)
				      ,DTL_ITEM7 = NVL(:V_DTL_ITEM7,DTL_ITEM7)
				      ,DTL_ITEM8 = NVL(:V_DTL_ITEM8,DTL_ITEM8)
				      ,DTL_ITEM9 = NVL(:V_DTL_ITEM9,DTL_ITEM9)
				      ,DTL_ITEM10 = NVL(:V_DTL_ITEM10,DTL_ITEM10)
				      ,MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				 WHERE REPR_CD_GP = :V_REPR_CD_GP
				   AND CD_GP = :V_CD_GP
				   AND ITEM = :V_ITEM
				 */                      
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdRuleNvl", logId, methodNm, "TB_YD_RULE DYD006 수정 - 초기화");

				/****************************************************
				 * 적치열 비활성       (COL)
				 * 적치포인트 비활성 (YDPOINT)
				 * 적치BED
				 * 적치단
				 ****************************************************/
		    	jrParam.setField("YD_STK_COL_GP", ydCarStopLoc);
				
 				jrParam.setField("STAT"  			, "C"); 
		    	jrParam.setField("YD_STK_COL_GP"	, ydCarStopLoc);
		    	
			    
				/**********************************************************
				* 5. 회송이력 테이블 (TB_YD_RETHTHIST) 테이블 INSERT
				**********************************************************/
				slabUtils.printLog(logId, "ㅇㅁㅁㅁㅁㅁㅇ 5.회송이력 테이블 (TB_YD_RETHTHIST) 테이블 INSERT 시작  " + rowCnt, "");
				for (int ii = 0; ii < rowCnt; ii++) {
					String iRim = gdReq.getHeader("CMPL_GP").getComboHiddenValues()[gdReq.getHeader("CMPL_GP").getSelectedIndex(ii)];
					if("HS".equals(iRim)) { //회송인 경우만 처리..
						jrParam.setField("YD_RETHT_HIST_ID"		, sRetHt_ID);
						jrParam.setField("STL_NO"				, gdReq.getHeader("STL_NO").getValue(ii));
						jrParam.setField("YD_RETHT_EMPNO"		, sModifier);
						jrParam.setField("YD_RETHT_REQ_DT"		, currDate);
						jrParam.setField("YD_RETHT_RSN_CD"		, gdReq.getParam("RTNHT_RSN_CD"));
						jrParam.setField("YD_RETHT_RSN_CNTS"	, gdReq.getParam("RTNHT_RSN_MSG"));
						jrParam.setField("YD_RETHT_STAT_CD"		, "1");
						jrParam.setField("SPOS_WLOC_CD"			, gdReq.getHeader("SPOS_WLOC_CD").getValue(ii)); 
						jrParam.setField("ARR_WLOC_CD"			, gdReq.getHeader("ARR_WLOC_CD").getValue(ii)); 
						jrParam.setField("TRN_EQP_CD"			, gdReq.getHeader("TRN_EQP_CD").getValue(ii)); 
						jrParam.setField("YD_CAR_SCH_ID"		, sYdCarSchId /*gdReq.getHeader("YD_CAR_SCH_ID").getValue(ii)*/); 
						jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
						/*
						INSERT INTO TB_YD_RETHTHIST (
						     YD_RETHT_HIST_ID
						    ,STL_NO
						    ,YD_RETHT_EMPNO
						    ,YD_RETHT_REQ_DT
						    ,YD_RETHT_RSN_CD
						    ,YD_RETHT_RSN_CNTS
						    ,YD_RETHT_CMPL_DT
						    ,YD_RETHT_STAT_CD
						    ,SPOS_WLOC_CD
						    ,ARR_WLOC_CD
						    ,TRN_EQP_CD
						    ,YD_CAR_SCH_ID
						    ,REGISTER
						    ,REG_DDTT
						    ,MODIFIER
						    ,MOD_DDTT
						    ,DEL_YN
						) VALUES (
						     :V_YD_RETHT_HIST_ID
						    ,:V_STL_NO
						    ,:V_YD_RETHT_EMPNO
						    ,NVL(TO_DATE(:V_YD_RETHT_REQ_DT,'YYYYMMDDHH24MISS'),SYSDATE)
						    ,:V_YD_RETHT_RSN_CD
						    ,:V_YD_RETHT_RSN_CNTS
						    ,NULL
						    ,:V_YD_RETHT_STAT_CD
						    ,:V_SPOS_WLOC_CD
						    ,:V_ARR_WLOC_CD
						    ,:V_TRN_EQP_CD
						    ,:V_YD_CAR_SCH_ID
						    ,:V_MODIFIER
						    ,SYSDATE
						    ,:V_MODIFIER
						    ,SYSDATE
						    ,'N'
						)
						 */
						commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.insRetHtHist", logId, methodNm, "회송이력 테이블 INSERT");
					}
				}
				slabUtils.printLog(logId, "ㅇㅁㅁㅁㅁㅁ 5.회송이력 테이블 (TB_YD_RETHTHIST) 테이블 INSERT 종료 ", "");
				
				/**********************************************************
				* 6. 구내운송 회송하차 완료실적 전문 편집
				**********************************************************/
				slabUtils.printLog(logId, "ㅇㅁㅁㅁㅁㅁㅇ 6.구내운송 회송하차 완료실적 전문 편집 시작  " + sArrWlocCd, "");
				
				JDTORecord jrYDTSJ016 = JDTORecordFactory.getInstance().create();
				
				jrYDTSJ016.setField("JMS_TC_CD"				, "YDTSJ016"); 									//야드작업예약ID
				jrYDTSJ016.setField("JMS_TC_CREATE_DDTT"	, currDate); 									//JMSTC생성일시	
				
				jrYDTSJ016.setField("TRN_EQP_CD"			, gdReq.getHeader("TRN_EQP_CD").getValue(0)); 	//운송장비코드
				jrYDTSJ016.setField("ARR_WLOC_CD"			, sArrWlocCd); 									//착지개소코드
				jrYDTSJ016.setField("ARR_YD_PNT_CD"			, sArrYdPntcd ); 								//착지야드포인트코드
				jrYDTSJ016.setField("CARUD_CMPL_DT"			, currDate); 									//하차완료일시
				jrYDTSJ016.setField("CARLD_SH"				, gdReq.getHeader("CARLD_SH").getValue(0)); 	//상차매수
				
				if(rowCnt > 6) {
					slabUtils.printLog(logId, "ㅇㅁㅁㅁㅁㅁ 회송전문 6건까지 가능 ", "SL");
					//irowCnt = 5;					(회송대상 전체건 반영으로 변경)
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "회송 처리는 최대 6건까지 가능합니다.");
					//return jrRtn;
				}
				
				irowCnt = rowCnt;
				for (int ii = 0; ii < irowCnt; ii++) {
					String iRim = gdReq.getHeader("CMPL_GP").getComboHiddenValues()[gdReq.getHeader("CMPL_GP").getSelectedIndex(ii)];
					jrYDTSJ016.setField("STL_NO" + (ii+1)             , gdReq.getHeader("STL_NO").getValue(ii)); 		//재료번호n
					jrYDTSJ016.setField("RETHT_CARUD_CMPL_GP" + (ii+1), iRim); //회송하차완료구분n
					slabUtils.printLog(logId, "----------------"+iRim + " <"+ii+ ">" + gdReq.getHeader("STL_NO").getValue(ii), "SL");
				}
				
				jrRtn 	= slabUtils.addSndData(jrRtn, jrYDTSJ016);
				
				slabUtils.printLog(logId, "ㅇㅁㅁㅁㅁㅁ 5.구내운송 회송하차 완료실적 전문 편집 종료 ", "");
				
				slabUtils.printLog(logId, methodNm, "S-");

				jrRtn.setField("RTN_CD"	, "1");
				jrRtn.setField("RTN_MSG", "정상처리되었습니다.");

				return jrRtn;
				
			} catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}	
		} // end of runTsRetHt
		

		/**
		 * 오퍼레이션명 : (3-3)차량작업관리화면 상차완료처리
		 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리 
		 * 박영수  2020-10-29		2020-12-28
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws DAOException
		 */
		public JDTORecord complCarLdLot(GridData inDto) throws DAOException {
			String methodNm = "차량작업관리화면 상차완료처리[PSlabYdMvCarSeEJB.complCarLdLot] < " + inDto.getNavigateValue();
			String logId    = inDto.getIPAddress();
			String szMsg        	= "";
			
			// 화면에서 넘어온 Parameter
			String szYD_CAR_SCH_ID 	= "";	// 차량스케쥴ID
			String szTRN_EQP_CD     = "";	// 운송장비코드
			String szYD_STK_COL_GP	= "";	// 적치열포인트(Ex:DAPT01)
			
			int intRtnVal =  0;
			
			try {
				slabUtils.printLog(logId, methodNm, "S+");
				slabUtils.printLog(logId, "▶ 차량작업관리-상차완료처리 수동작업 START!", "SL");
				
				String sModifier       		= slabUtils.trim(inDto.getParam("userid" )); 			//수정자(MODIFIER)
				JDTORecord jrRtn       		= slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
				JDTORecord jrParam     		= slabUtils.getParam(logId, methodNm, sModifier);
				
				szYD_CAR_SCH_ID				= slabUtils.trim(inDto.getParam("YD_CAR_SCH_ID"	));	// 차량스케쥴ID
				szTRN_EQP_CD				= slabUtils.trim(inDto.getParam("TRN_EQP_CD"	));	// 운송장비코드
				szYD_STK_COL_GP				= slabUtils.trim(inDto.getParam("CAR_POINT3"	));	// 화염 3번탭 > 적치열포인트(Ex:DAPT01)
				
				slabUtils.printLog(logId, "▷ szYD_CAR_SCH_ID[차량스케쥴ID]	:"+szYD_CAR_SCH_ID	, "SL");
				slabUtils.printLog(logId, "▷ szTRN_EQP_CD[운송장비코드]		:"+szTRN_EQP_CD		, "SL");
				slabUtils.printLog(logId, "▷ szYD_STK_COL_GP[적치열포인트]	:"+szYD_STK_COL_GP	, "SL");
				
				
				// sDYD201_YN27 = 'N'일 경우 스케줄 존재유무를 제외한 에러체크 로직을 Passing 한다.(현업이 차량작업상태 상관없이 상차완료 처리 요청에 대한 대비)
				String sERROR_CHK_YN = "";	// 상차완료 처리 시 에러체크 적용 유무
				
				/* com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYnDualSql
				SELECT 'Y' AS APPLY_YN27  --(차량)상차완료처리 -> 차량작업상태 에러체크 유무 
				  FROM DUAL	*/
				JDTORecordSet jsApplyYNChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYnDualSql", logId, methodNm, "열정보 Read"); 
				if (jsApplyYNChk.size() > 0) {
					sERROR_CHK_YN    = slabUtils.trim(jsApplyYNChk.getRecord(0).getFieldString("APPLY_YN27")); // 차량작업상태 에러체크 유무
				}
				
				slabUtils.printLog(logId, "▷ sERROR_CHK_YN[상차완료 처리 시 에러체크 적용 유무]	:"+sERROR_CHK_YN	, "SL");
				
				
				/* 후판슬라브야드 차량스케쥴정보 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getCarSchInfo
				SELECT    CS.YD_CAR_SCH_ID               -- 야드차량스케쥴ID
				        , CS.REGISTER                    -- 등록자
				        , CS.REG_DDTT                    -- 등록일시
				        , CS.MODIFIER                    -- 수정자
				        , CS.MOD_DDTT                    -- 수정일시
				        , CS.DEL_YN                      -- 삭제유무
				        , CS.YD_EQP_ID                   -- 야드설비ID
				        , CS.YD_CAR_USE_GP               -- 야드차량사용구분
				        , CS.CAR_NO                      -- 차량번호
				        , CS.TRN_EQP_CD                  -- 운송장비코드
				        , CS.CAR_KIND                    -- 차량종류
				        , CS.TRANS_EQUIPMENT_TYPE        -- 운송장비Type
				        , CS.YD_EQP_WRK_STAT             -- 야드설비작업상태
				        , CS.YD_WRK_PROG_STAT            -- 야드작업진행상태
				        , CS.YD_EQP_WRK_SH               -- 야드설비작업매수
				        , CS.YD_EQP_WRK_WT               -- 야드설비작업중량
				        , CS.YD_STK_BED_TP               -- 야드적치BedType
				        , CS.SPOS_WLOC_CD                -- 발지개소코드
				        , CS.ARR_WLOC_CD                 -- 착지개소코드
				        , CS.YD_CARLD_LEV_LOC            -- 야드상차출발위치
				        , CS.YD_CARLD_LEV_DT             -- 야드상차출발일시
				        , CS.YD_CARLD_PNT_WO_DT          -- 야드상차Point지시일시
				        , CS.YD_PNT_CD1                  -- 야드포인트코드1
				        , CS.YD_PNT_CD2                  -- 야드포인트코드2
				        , CS.YD_CARLD_WRK_BOOK_ID        -- 야드상차작업예약ID
				        , CS.YD_CARLD_SCH_REQ_GP         -- 야드상차스케쥴요청구분
				        , CS.YD_CARLD_STOP_LOC           -- 야드상차정지위치
				        , CS.YD_CARLD_ARR_DT             -- 야드상차도착일시
				        , CS.YD_CARLD_ST_DT              -- 야드상차개시일시
				        , CS.YD_CARLD_CMPL_DT            -- 야드상차완료일시
				        , CS.YD_CARLD_WRK_ACT_GP         -- 야드상차작업수행구분
				        , CS.YD_CARLD_CHK_DT             -- 야드상차검수일시
				        , CS.YD_CARUD_LEV_DT             -- 야드하차출발일시
				        , CS.YD_CARUD_PNT_WO_DT          -- 야드하차Point지시일시
				        , CS.YD_PNT_CD3                  -- 야드포인트코드3
				        , CS.YD_PNT_CD4                  -- 야드포인트코드4
				        , CS.YD_CARUD_WRK_BOOK_ID        -- 야드하차작업예약ID
				        , CS.YD_CARUD_STOP_LOC           -- 야드하차정지위치
				        , CS.YD_CARUD_SCH_REQ_GP         -- 야드하차스케쥴요청구분
				        , CS.YD_CARUD_ARR_DT             -- 야드하차도착일시
				        , CS.YD_CARUD_CHK_DT             -- 야드하차검수일시
				        , CS.YD_CARUD_ST_DT              -- 야드하차개시일시
				        , CS.YD_CARUD_CMPL_DT            -- 야드하차완료일시
				        , CS.YD_CARUD_WRK_ACT_GP         -- 야드하차작업수행구분
				        , CS.YD_TRN_WRK_DELY_CD          -- 야드운송작업지연코드
				        , CS.CARD_NO                     -- 카드번호
				        , CS.YD_CAR_PROG_STAT            -- 야드차량진행상태
				        , CS.FRTOMOVE_PLANT_GP           -- 이송공장구분
				        , CS.PROC_TO                     -- 공정To
				        , CS.RENTPROC_CD                 -- 임가공사코드
				        , CS.YD_FRTOMOVE_YD_GP           -- 야드이송야드구분
				        , CS.YD_FRTOMOVE_BAY_GP          -- 야드이송동구분
				        , CS.URGENT_FRTOMOVE_WORD_GP     -- 긴급이송작업지시구분
				        , CS.DEST_TEL_NO                 -- 목적지전화번호
				        , CS.YD_DLVRDD_RULE_DD           -- 야드납기기준일
				        , CS.SHIPASSIGN_WORD_DATE        -- 배선작업지시일자
				        , CS.SHIPASSIGN_WORD_SEQNO       -- 배선작업지시순번
				        , CS.SHIP_CD                     -- 선박코드
				        , CS.SHIP_NAME                   -- 선박명
				        , CS.RSHP_HOLD_NO                -- 선박Hold번호
				        , CS.BERTH_NO                    -- 선석번호
				        , CS.SAILNO                      -- 선박항차
				        , CS.YD_CAR_WRK_GP               -- 야드차량작업구분
				        , CS.TRANS_ORD_DATE              -- 운송지시일자
				        , CS.TRANS_ORD_SEQNO             -- 운송지시순번
				        , CS.YD_BAYIN_WO_SEQ             -- 야드입동지시순번
				        , CS.YD_CAR_RCPT_CHK_YN          -- 야드차량입고검수여부
				        , CS.YD_CAR_ISSUE_CHK_YN         -- 야드차량출고검수여부
				        , CS.YD_CAR_RCPT_CHECKER         -- 야드차량입고검수자
				        , CS.YD_CAR_ISSUE_CHECKER        -- 야드차량출고검수자
				        , CS.IF_SEQ_NO                   -- 상차지시SEQ
				        , CS.TEL_NO                      -- 전화번호
				        , CS.CMBN_CARLD_YN               -- 조합상차유무
				        , CS.WAIT_ARR_DDTT               -- 도착시간
				        , CS.WAIT_ARR_GP                 -- 도착구분
				        , CS.YD_MSG_NM                   -- 야드Message명
				        , CS.FRTOMOVE_WORD_NO            -- 이송작업지시번호
				        , CS.DRIVER_NAME                 -- 운전기사명
				        , CS.CAR_REMODEL_YN              -- 차량개조여부
				        , CS.CAR_FRM_CMPL_DT             -- 차량형상완료일시
				  FROM USRYDA.TB_YD_CARSCH CS
				 WHERE CS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				slabUtils.printLog(logId, "▶ 1. 차량스케쥴ID로 차량스케쥴정보 조회", "SL");
				slabUtils.printLog(logId, "▷ PARAM[차량스케쥴ID]	:"+szYD_CAR_SCH_ID, "SL");
				
				jrParam.setField("YD_CAR_SCH_ID",   szYD_CAR_SCH_ID);
				JDTORecordSet carSchRsltSet = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getCarSchInfo", logId, methodNm, "차량스케쥴ID로 차량스케쥴정보 조회");
				
				// 차량스케쥴이 없으면 ERROR
				if(carSchRsltSet.size() <= 0){
					
					slabUtils.printLog(logId, "▶ 상차완료처리 대상 차량스케줄이 없으므로 ERROR.", "SL");
					szMsg = "해당 차량 작업은 종료되었습니다.";
					slabUtils.printLog(logId, szMsg, "SL");
					
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", szMsg);
					return jrRtn;
				
				}else{	// 차량스케쥴이 있으면 조회된 차량스케쥴로 상차완료처리 START.
					
					slabUtils.printLog(logId, "▶ 1-1. 차량스케쥴이 존재합니다.", "SL");
					slabUtils.printLog(logId, "▶ 차량스케쥴 에러체크 기준 적용", "SL");
					
					JDTORecord carSchRslt = carSchRsltSet.getRecord(0);
					
					String sYD_CAR_PROG_STAT = carSchRslt.getFieldString("YD_CAR_PROG_STAT");	// 야드차량진행상태
					
					slabUtils.printLog(logId, "▶ 차량진행상태 코드 값 [0]상차대기 [1]상차출발 [2]상차도착 [3]상차검수 [4]상차개시 [5]상차완료 [A]하차출발 [B]하차도착 [C]하차검수 [D]하차개시 [E]하차완료", "SL");
					slabUtils.printLog(logId, "▷ 해당 차량진행상태 : " + sYD_CAR_PROG_STAT, "SL");
					
					
					// 차량이 '상차개시' 외 다른 상태이면 상태에 맞는 에러처리를 한다.
					if(!PSlabYdConstant.YD_CARLD_ST.equals(sYD_CAR_PROG_STAT)){	// 상차개시 상태가 아니면
						
						slabUtils.printLog(logId, "▷ sERROR_CHK_YN[상차완료 처리 시 에러체크 적용 유무]	:"+sERROR_CHK_YN	, "SL");
						
						if("Y".equals(sERROR_CHK_YN)){	// 에러체크 적용 유무
							slabUtils.printLog(logId, "▶ 차량진행상태가 '상차개시' 상태가 아니므로 ERROR.", "SL");
							
							// 하차차량이면 ERROR
							if(PSlabYdConstant.YD_CARUD_LEV.equals(sYD_CAR_PROG_STAT)		||	// 하차출발
									PSlabYdConstant.YD_CARUD_ARR.equals(sYD_CAR_PROG_STAT)	||	// 하차도착
									PSlabYdConstant.YD_CARUD_CHK.equals(sYD_CAR_PROG_STAT)	||	// 하차검수
									PSlabYdConstant.YD_CARUD_ST.equals(sYD_CAR_PROG_STAT)	||	// 하차개시
									PSlabYdConstant.YD_CARUD_CMPL.equals(sYD_CAR_PROG_STAT)){	// 하차완료
								
								szMsg = "해당 차량은 [하차작업] 차량입니다.";
								slabUtils.printLog(logId, szMsg, "SL");
								
								jrRtn.setField("RTN_CD"	, "0");
								jrRtn.setField("RTN_MSG", szMsg);
								return jrRtn;
							
							// 상차완료 상태면 ERROR(다른 작업자가 완료처리 해버린 상태)
							}else if(PSlabYdConstant.YD_CARLD_CMPL.equals(sYD_CAR_PROG_STAT)){	// 상차완료
								szMsg = "이미 [상차완료]된 차량입니다.";
								slabUtils.printLog(logId, szMsg, "SL");
								
								jrRtn.setField("RTN_CD"	, "0");
								jrRtn.setField("RTN_MSG", szMsg);
								return jrRtn;
							
							// 그 외 상태  ERROR 처리
							}else{
								szMsg = "해당 차량 상태가 [상차개시]가 아닙니다.";
								slabUtils.printLog(logId, szMsg, "SL");
								
								jrRtn.setField("RTN_CD"	, "0");
								jrRtn.setField("RTN_MSG", szMsg);
								return jrRtn;
							}
						}
						
					
					}else{	// 상차개시 상태이면 정상적으로 상차완료 처리 로직을 진행 START.
						slabUtils.printLog(logId, "▶ 1-2. 차량스케쥴이 [상차개시]상태이므로 [상차완료] 처리를 시작합니다.", "SL");
						
						
						
						// to위치가 해당 차량포인트인 종료 안된 크레인스케줄 조회
						slabUtils.printLog(logId, "▶ 1-2-1. 사전체크1)to위치가 해당 차량포인트인 종료 안된 크레인스케줄이 있는지 확인합니다.", "SL");
						
						/* to위치 별 크레인스케줄 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getCrnSchInfoByToLoc
						SELECT    CS.YD_CRN_SCH_ID             -- 야드크레인스케쥴ID
						        , CS.REGISTER                  -- 등록자
						        , CS.REG_DDTT                  -- 등록일시
						        , CS.MODIFIER                  -- 수정자
						        , CS.MOD_DDTT                  -- 수정일시
						        , CS.DEL_YN                    -- 삭제유무
						        , CS.YD_WBOOK_ID               -- 야드작업예약ID
						        , CS.YD_EQP_ID                 -- 야드설비ID
						        , CS.YD_GP                     -- 야드구분
						        , CS.YD_BAY_GP                 -- 야드동구분
						        , CS.YD_SCH_CD                 -- 야드스케쥴코드
						        , CS.YD_SCH_ST_GP              -- 야드스케쥴기동구분
						        , CS.YD_SCH_REQ_GP             -- 야드스케쥴요청구분
						        , CS.YD_SCH_PRIOR              -- 야드스케쥴우선순위
						        , CS.YD_EQP_WRK_STAT           -- 야드설비작업상태
						        , CS.YD_WRK_PROG_STAT          -- 야드작업진행상태
						        , CS.YD_WBOOK_DT               -- 야드작업예약일시
						        , CS.YD_SCH_DT                 -- 야드스케쥴일시
						        , CS.YD_WORD_DT                -- 야드작업지시일시
						        , CS.YD_UP_CMPL_DT             -- 야드권상완료일시
						        , CS.YD_DN_CMPL_DT             -- 야드권하완료일시
						        , CS.YD_WRK_HDS_DD             -- 야드작업계상일자
						        , CS.YD_WRK_DUTY               -- 야드작업근
						        , CS.YD_WRK_PARTY              -- 야드작업조
						        , CS.YD_MAIN_WRK_MTL_SH        -- 야드주작업재료매수
						        , CS.YD_AID_WRK_MTL_SH         -- 야드보조작업재료매수
						        , CS.YD_AID_WRK_UPDN_GP        -- 야드보조작업상하구분
						        , CS.YD_TO_LOC_DCSN_MTD        -- 야드To위치결정방법
						        , CS.YD_TO_LOC_GUIDE           -- 야드To위치Guide
						        , CS.YD_EQP_WRK_SH             -- 야드설비작업매수
						        , CS.YD_EQP_WRK_WT             -- 야드설비작업중량
						        , CS.YD_EQP_WRK_T              -- 야드설비작업총두께
						        , CS.YD_EQP_WRK_MAX_W          -- 야드설비작업최대폭
						        , CS.YD_EQP_WRK_MAX_L          -- 야드설비작업최대길이
						        , CS.YD_CRN_SB_CTL_H           -- 야드크레인SB제어높이
						        , CS.YD_CRN_GRAB_USE_RULE_ID   -- 야드크레인Grab사용RuleID
						        , CS.YD_UP_WO_LOC              -- 야드권상지시위치
						        , CS.YD_UP_WO_LAYER            -- 야드권상지시단
						        , CS.YD_UP_WO_LOC_XAXIS        -- 야드권상지시X축
						        , CS.YD_UP_WO_XAXIS_GAP_MAX    -- 야드권상지시X축오차최대
						        , CS.YD_UP_WO_XAXIS_GAP_MIN    -- 야드권상지시X축오차최소
						        , CS.YD_UP_WO_LOC_YAXIS        -- 야드권상지시Y축
						        , CS.YD_UP_WO_LOC_YAXIS1       -- 야드권상지시Y축1
						        , CS.YD_UP_WO_LOC_YAXIS2       -- 야드권상지시Y축2
						        , CS.YD_UP_WO_YAXIS_GAP_MAX    -- 야드권상지시Y축오차최대
						        , CS.YD_UP_WO_YAXIS_GAP_MIN    -- 야드권상지시Y축오차최소
						        , CS.YD_UP_WO_LOC_ZAXIS        -- 야드권상지시Z축
						        , CS.YD_UP_WO_ZAXIS_GAP_MAX    -- 야드권상지시Z축오차최대
						        , CS.YD_UP_WO_ZAXIS_GAP_MIN    -- 야드권상지시Z축오차최소
						        , CS.YD_DN_WO_LOC              -- 야드권하지시위치
						        , CS.YD_DN_WO_LAYER            -- 야드권하지시단
						        , CS.YD_DN_WO_LOC_XAXIS        -- 야드권하지시X축
						        , CS.YD_DN_WO_XAXIS_GAP_MAX    -- 야드권하지시X축오차최대
						        , CS.YD_DN_WO_XAXIS_GAP_MIN    -- 야드권하지시X축오차최소
						        , CS.YD_DN_WO_LOC_YAXIS        -- 야드권하지시Y축
						        , CS.YD_DN_WO_LOC_YAXIS1       -- 야드권하지시Y축1
						        , CS.YD_DN_WO_LOC_YAXIS2       -- 야드권하지시Y축2
						        , CS.YD_DN_WO_YAXIS_GAP_MAX    -- 야드권하지시Y축오차최대
						        , CS.YD_DN_WO_YAXIS_GAP_MIN    -- 야드권하지시Y축오차최소
						        , CS.YD_DN_WO_LOC_ZAXIS        -- 야드권하지시Z축
						        , CS.YD_DN_WO_ZAXIS_GAP_MAX    -- 야드권하지시Z축오차최대
						        , CS.YD_DN_WO_ZAXIS_GAP_MIN    -- 야드권하지시Z축오차최소
						        , CS.YD_UP_WR_LOC              -- 야드권상실적위치
						        , CS.YD_UP_WR_LAYER            -- 야드권상실적단
						        , CS.YD_UP_WRK_ACT_GP          -- 야드권상작업수행구분
						        , CS.YD_UP_WR_XAXIS            -- 야드권상실적X축
						        , CS.YD_UP_WR_YAXIS            -- 야드권상실적Y축
						        , CS.YD_UP_WR_YAXIS1           -- 야드권상실적Y축1
						        , CS.YD_UP_WR_YAXIS2           -- 야드권상실적Y축2
						        , CS.YD_UP_WR_ZAXIS            -- 야드권상실적Z축
						        , CS.YD_DN_WR_LOC              -- 야드권하실적위치
						        , CS.YD_DN_WR_LAYER            -- 야드권하실적단
						        , CS.YD_DN_WRK_ACT_GP          -- 야드권하작업수행구분
						        , CS.YD_DN_WR_XAXIS            -- 야드권하실적X축
						        , CS.YD_DN_WR_YAXIS            -- 야드권하실적Y축
						        , CS.YD_DN_WR_YAXIS1           -- 야드권하실적Y축1
						        , CS.YD_DN_WR_YAXIS2           -- 야드권하실적Y축2
						        , CS.YD_DN_WR_ZAXIS            -- 야드권하실적Z축
						        , CS.UP_ROTATION_ANGLE         -- 권상위치회전각도
						        , CS.DOWN_ROTATION_ANGLE       -- 권하위치회전각도
						        , CS.YD_DN_WO_LOC_TO           -- 야드권하지시위치to
						        , CS.YD_L2_REQUEST_STAT        -- 야드L2요구상태
						        , CS.YD_UP_WRK_MODE2           -- 야드권상작업mode2
						        , CS.YD_DN_WRK_MODE2           -- 야드권작업mode2
						        , CS.STL_NO_TEMP               -- L2작업지시STL_NOTemp
						        , CS.STK_LYR_NO_TEMP           -- L2작업지시LayTemp
						        , CS.YD_WRK_PROG_REQ_MSG       -- 야드L2작업지시응답메세지
						  FROM TB_YD_CRNSCH CS -- YD_크레인스케쥴
						 WHERE 1=1
						   AND CS.YD_DN_WO_LOC LIKE :V_YD_STK_COL_GP || '%'
						   AND CS.DEL_YN = 'N' */
						jrParam.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
						slabUtils.printLog(logId, "▷ PARAM[적치열]	:"+szYD_STK_COL_GP, "SL");
						
						JDTORecordSet crnSchRsltSet = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getCrnSchInfoByToLoc", logId, methodNm, "to위치 별 크레인스케줄 조회");
						
						// 해당포인트에 이적 중인 크레인 스케쥴이 존재하면 ERROR(아직 작업이 안 끝난 것으로 본다)
						slabUtils.printLog(logId, "▷ 하차위치가 해당 차량포인트이고 종료안된 크레인스케줄 갯수 : " + crnSchRsltSet.size(), "SL");
						slabUtils.printLog(logId, "▷ sERROR_CHK_YN[상차완료 처리 시 에러체크 적용 유무]	:"+sERROR_CHK_YN	, "SL");
						
						if("Y".equals(sERROR_CHK_YN)){	// 에러체크 적용 유무
							if(crnSchRsltSet.size() > 0){
								szMsg = "해당차량의 종료되지 않은 상차 크레인스케쥴이 있습니다.";
								slabUtils.printLog(logId, szMsg, "SL");
								
								jrRtn.setField("RTN_CD"	, "0");
								jrRtn.setField("RTN_MSG", szMsg);
								return jrRtn;
							}
						}
						
						/* 2021.07.19 작업예약 에러체크 주석처리 -> 다른 차량이 대기 중일 때 미리 작업예약을 만들어두므로 작업예약은 해당위치로 에러체크를 걸면 안 된다.
						// to위치가이드가 차량포인트인 종료 안된 작업예약 조회
						slabUtils.printLog(logId, "▶ 1-2-2. 사전체크2)to위치가이드가 차량포인트인 종료 안된 작업예약이 있는지 확인합니다.", "SL");
						
						/* to위치가이드 별 작업예약 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getCrnSchInfoByToLoc
						SELECT    WB.YD_WBOOK_ID           -- 야드작업예약ID
						        , WB.REGISTER              -- 등록자
						        , WB.REG_DDTT              -- 등록일시
						        , WB.MODIFIER              -- 수정자
						        , WB.MOD_DDTT              -- 수정일시
						        , WB.DEL_YN                -- 삭제유무
						        , WB.YD_GP                 -- 야드구분
						        , WB.YD_BAY_GP             -- 야드동구분
						        , WB.YD_SCH_CD             -- 야드스케쥴코드
						        , WB.YD_SCH_PRIOR          -- 야드스케쥴우선순위
						        , WB.YD_SCH_PROG_STAT      -- 야드스케쥴진행상태
						        , WB.YD_SCH_ST_GP          -- 야드스케쥴기동구분
						        , WB.YD_SCH_REQ_GP         -- 야드스케쥴요청구분
						        , WB.YD_AIM_YD_GP          -- 야드목표야드구분
						        , WB.YD_AIM_BAY_GP         -- 야드목표동구분
						        , WB.YD_CTS_RELAY_YN       -- 야드CTS중계유무
						        , WB.YD_CTS_RELAY_BAY_GP   -- 야드CTS중계동구분
						        , WB.YD_TO_LOC_DCSN_MTD    -- 야드To위치결정방법
						        , WB.YD_TO_LOC_GUIDE       -- 야드To위치Guide
						        , WB.YD_WRK_PLAN_TCAR      -- 야드작업계획대차
						        , WB.YD_CAR_USE_GP         -- 야드차량사용구분
						        , WB.TRN_EQP_CD            -- 운송장비코드
						        , WB.CAR_NO                -- 차량번호
						        , WB.CARD_NO               -- 카드번호
						        , WB.PTOP_PLNT_GP          -- 조업공장구분
						        , WB.DEST_TEL_NO           -- 목적지전화번호
						        , WB.DIST_SHIPASSIGN_GP    -- 출하배선지시구분
						        , WB.YD_WRK_PLAN_CRN       -- 야드작업계획크레인
						        , WB.SCH_CNCL_YN           -- 스케줄취소여부
						  FROM TB_YD_WRKBOOK WB -- YD_작업예약
						 WHERE WB.YD_TO_LOC_GUIDE LIKE :V_YD_STK_COL_GP || '%'
						   AND WB.DEL_YN = 'N' * /
						JDTORecordSet wBookRsltSet = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getWbookInfoByToLoc", logId, methodNm, "to위치 별 작업예약 조회");
						
						// 해당포인트에 이적 중인 크레인 스케쥴이 존재하면 ERROR(아직 작업이 안 끝난 것으로 본다)
						slabUtils.printLog(logId, "▷ to위치가이드가 해당 차량포인트이고 종료안된 작업예약 갯수 : " + wBookRsltSet.size(), "SL");
						slabUtils.printLog(logId, "▷ sERROR_CHK_YN[상차완료 처리 시 에러체크 적용 유무]	:"+sERROR_CHK_YN	, "SL");
						
						if("Y".equals(sERROR_CHK_YN)){	// 에러체크 적용 유무
							if(wBookRsltSet.size() > 0){
								szMsg = "해당차량의 종료되지 않은 이적작업이 있습니다.";
								slabUtils.printLog(logId, szMsg, "SL");
								
								jrRtn.setField("RTN_CD"	, "0");
								jrRtn.setField("RTN_MSG", szMsg);
								return jrRtn;
							}
						}
						*/
						
						// 차량스케줄상태 ‘상차완료’ UPDATE
						slabUtils.printLog(logId, "▶1-2-3. 차량스케줄 ‘상차완료’ 상태로 UPDATE 합니다.", "SL");
						slabUtils.printLog(logId, "▷ PARAM[차량스케줄ID]	:"+szYD_CAR_SCH_ID, "SL");
						jrParam.setField("YD_CAR_SCH_ID",		szYD_CAR_SCH_ID);
						/*
							com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.UpdateCarSchProgStat
							UPDATE TB_YD_CARSCH
							   SET YD_CAR_PROG_STAT = '5'
							     , MOD_DDTT         = SYSDATE
							     , MODIFIER         = :V_MODIFIER
							 WHERE YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
						 */
						intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.UpdateCarSchProgStat", logId, methodNm, "차량스케줄상태 상차완료 변경");
						slabUtils.printLog(logId, "▶  " + intRtnVal + "건 UPDATE 되었습니다.", "SL");
						
						
						
						// PT_이송지시 테이블 ‘상차완료일’ UPDATE
						slabUtils.printLog(logId, "▶ 1-2-4. 소재이송지시(USRPTA.TB_PT_STLFRTOMOVE) 테이블에 '상차완료일'을 UPDATE 합니다.", "SL");
						
						String sWR_DT	= slabUtils.getDateTime14(); // 현재시각 yyyymmddhhmiss
						jrParam.setField("WR_DT", sWR_DT); //실적일시
						/* --크레인권하실적 상차 소재이송지시 수정 --
						 * com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009StlMoveLd
						 *       
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
						intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updY3YDL009StlMoveLd", logId, methodNm, "소재이송지시 상차수정");
						slabUtils.printLog(logId, "▶  " + intRtnVal + "건 UPDATE 되었습니다.", "SL");
						
						// 구내운송 송신전문  MSG 저장 - 소재차량상차완료(TSYDJ008)
						slabUtils.printLog(logId, "▶ 1-2-5. 구내운송으로 소재차량상차완료(TSYDJ008) 전문 송신 정보를 저장합니다.", "SL");
						
						slabUtils.printLog(logId, "▶ 소재차량상차완료(YDTSJ008) 전문 발송 MSG를 생성합니다.", "SL");
						jrParam.setField("MSG_ID",        "YDTSJ008");
						jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ008", jrParam));
						
						slabUtils.printLog(logId, "▶ 소재차량상차완료(YDTSJ008) 전문 발송 MSG 출력..", "SL");
						slabUtils.printParam(logId, jrRtn);
						
						
						
						slabUtils.printLog(logId, "▶  상차완료처리 로직이 정상 수행되었습니다.", "SL");
					
					}	// 상차개시 상태이면 정상적으로 상차완료 처리 로직을 진행 END.
					
				}	// 차량스케쥴이 있으면 조회된 차량스케쥴로 상차완료처리 END.
				
				
				szMsg = "";
				jrRtn.setField("RTN_CD"	, "1");
				jrRtn.setField("RTN_MSG", "상차완료처리 작업이 정상 처리 되었습니다.");
				return jrRtn;
				
			} catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}
		}
		
	
		
	/**
	 *  차량작업관리 상차LOT편성 - 운송장비(차량)에 이송LOT로 작업예약을 생성한다.
 	 * 	@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *  @param JDTORecord paramDto, JDTORecord [] listDto
	 *  @return JDTORecord
	 *  @throws DAOException
	 */
	public JDTORecord insCarLdLotNew(JDTORecord paramDto, JDTORecord[] listDtoSet) throws DAOException {
		/*******************************************************
		    - 필수 PARAMETER
		          차량스케쥴ID / 운송장비코드 / 발지개소코드 / 차량POINT
		    
		 	1. 차량스케줄 조회
		 	  1.1 차량스케줄이 없으면 ERROR처리
		 	
		 	2. 차량정보확인
		 	  2.1 차량이 '[2]상차도착' 상태인지 확인
		 	  2.2 상차작업예약이 이미 생성되었는지 확인
		 	  2.3 차량포인트, 적치열 조회 후 선택된 차량이 점유중인지 확인
		 	  
		 	3. 선택된 차량으로 기 생성된 작업예약 있는지 조회
		 	   3.1.1 작업예약생성
		 	   3.1.2 크레인스케줄 생성 - YDYDJ401 호출
		 *******************************************************/
		String logId                = "";
		String szMsg        		= "";		
		String methodNm 			= "insCarLdLotNew";
		String sModifier     		= "";
		
		// 화면에서 GridObj.SetParam으로 넘어온 파라미터는 변수명 앞에 param을 붙인다.
		String paramYD_CAR_SCH_ID		= "";	// 차량스케쥴ID
		String paramTRN_EQP_CD			= "";	// 운송장비코드
		String paramSPOS_WLOC_CD		= "";	// 발지개소코드
		String paramYD_STK_COL_GP		= "";	// 차량POINT의 적치열 예)'DAPT01', 'DBPT02' 등
		
		// 화면에서 그리드 row로 넘어온 파라미터는 변수명 앞에 list를 붙인다.
		String listYD_PREP_SCH_ID		= "";	// 준비스케쥴ID(이송 Lot)
		
		try {
			sModifier 		=	paramDto.getFieldString("USERID");
			logId 			=	paramDto.getFieldString("LOGID");
			
			szMsg = "후판 슬라브야드 차량작업관리 상차LOT편성[insCarLdLot] 시작"+ logId+ sModifier;
			slabUtils.printLog(logId,  szMsg, "S+");
			
			JDTORecord      recPara     = slabUtils.getParam(logId, methodNm, sModifier);	// 기준 조회용 파라미터 JDTORecord
			JDTORecord 		jrParam  	= slabUtils.getParam(logId, methodNm, sModifier);	// 작업예약생성용 파라미터 JDTORecord
			JDTORecord 		jrRtn  		= slabUtils.getParam(logId, methodNm, sModifier);	// return용
			
			JDTORecordSet 	rsResult    = JDTORecordFactory.getInstance().createRecordSet("stock");
			
			slabUtils.printLog(logId, "▶ 필수 PARAMETER CHECK.", "SL");
			
			paramYD_CAR_SCH_ID		= slabUtils.paraRecChkNull(paramDto, "YD_CAR_SCH_ID"	);	// 차량스케쥴ID
			paramTRN_EQP_CD			= slabUtils.paraRecChkNull(paramDto, "TRN_EQP_CD"		);	// 운송장비코드
			paramSPOS_WLOC_CD		= slabUtils.paraRecChkNull(paramDto, "SPOS_WLOC_CD"		);	// 발지개소코드
			paramYD_STK_COL_GP		= slabUtils.paraRecChkNull(paramDto, "YD_STK_COL_GP"	);	// 차량POINT의 적치열 예)'DAPT01', 'DBPT02' 등
			
			slabUtils.printLog(logId, "▷  paramYD_CAR_SCH_ID(차량스케쥴ID)		: " + paramYD_CAR_SCH_ID	, "SL");
			slabUtils.printLog(logId, "▷  paramTRN_EQP_CD(운송장비코드)			: " + paramTRN_EQP_CD		, "SL");
			slabUtils.printLog(logId, "▷  paramSPOS_WLOC_CD(발지개소코드)			: " + paramSPOS_WLOC_CD		, "SL");
			slabUtils.printLog(logId, "▷  paramYD_STK_COL_GP(차량POINT 적치열)	: " + paramYD_STK_COL_GP	, "SL");
			
			// 파라미터 확인
			if("".equals(slabUtils.nvl(paramYD_CAR_SCH_ID	, "")) ||
			   "".equals(slabUtils.nvl(paramTRN_EQP_CD		, "")) ||
			   "".equals(slabUtils.nvl(paramSPOS_WLOC_CD	, "")) ||
			   "".equals(slabUtils.nvl(paramYD_STK_COL_GP	, ""))){
				
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "상차LOT편성 작업을 진행할 필수 정보가 없습니다.");
				return jrRtn;
			}
			
			// 그리드 DATA 확인
			slabUtils.printLog(logId, "▷  선택된 Row 건수 : " + listDtoSet.length	, "SL"); // 화면에서 1건만 선택 가능하므로 1이 나와야 정상
			if(listDtoSet.length < 1){
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "선택된 이송LOT ID가 없습니다.");
				return jrRtn;
			}
			
			slabUtils.printLog(logId, "▶ 필수 PARAMETER CHECK 완료.", "SL");
			
			
			//+++++++++++++++ 1. 차량스케줄을 먼저 조회해서 존재하는 지를 확인 ++++++++++++++++++++++
			slabUtils.printLog(logId, "▶ 1. 차량스케줄 조회", "SL");
			slabUtils.printLog(logId, "▷  조회대상 차량스케쥴ID : " + paramYD_CAR_SCH_ID	, "SL");
			
			recPara.setField("YD_CAR_SCH_ID", paramYD_CAR_SCH_ID);
			/* 
				// com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdCarsch01
				SELECT 
				    YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
				    ,REGISTER AS REGISTER
				    ,TO_CHAR(REG_DDTT,'YYYYMMDDHH24MISS') AS REG_DDTT
				    ,MODIFIER AS MODIFIER
				    ,TO_CHAR(MOD_DDTT,'YYYYMMDDHH24MISS') AS MOD_DDTT
				    ,DEL_YN AS DEL_YN
				    ,YD_EQP_ID AS YD_EQP_ID
				    ,YD_CAR_USE_GP AS YD_CAR_USE_GP
				    ,CAR_NO AS CAR_NO
				    ,TRN_EQP_CD AS TRN_EQP_CD
				    ,CAR_KIND AS CAR_KIND
				    ,TRANS_EQUIPMENT_TYPE AS TRANS_EQUIPMENT_TYPE 
				    ,YD_EQP_WRK_STAT  AS YD_EQP_WRK_STAT
				    ,YD_WRK_PROG_STAT  AS YD_WRK_PROG_STAT
				    ,NVL(YD_EQP_WRK_SH,'0')  AS YD_EQP_WRK_SH
				    ,YD_EQP_WRK_WT  AS YD_EQP_WRK_WT
				    ,YD_STK_BED_TP  AS YD_STK_BED_TP
				    ,SPOS_WLOC_CD  AS SPOS_WLOC_CD
				       ,(CASE WHEN ARR_WLOC_CD IS NULL THEN (SELECT 
				                                                    (SELECT /*+ INDEX(D PK_PT_STLFRTOMOVE)/ 
				                                                            D.ARR_WLOC_CD
				                                                        FROM TB_PT_STLFRTOMOVE D
				                                                       WHERE D.TRANSWORD_SEQNO=(SELECT/+ INDEX_DESC(D PK_PT_STLFRTOMOVE)/
				                                                                            MAX(TRANSWORD_SEQNO) 
				                                                                         FROM TB_PT_STLFRTOMOVE K
				                                                                         WHERE D.STL_NO=K.STL_NO
				                                                                          AND ROWNUM<=1)
				                                                         AND B.STL_NO =D.STL_NO
				                                                         ) AS ARR_WLOC_CD
				                                             FROM  TB_YD_STKLYR B                                              
				                                              WHERE C.YD_CARLD_STOP_LOC=B.YD_STK_COL_GP     
				                                                AND ROWNUM<=1 ) 
				          ELSE ARR_WLOC_CD END) AS ARR_WLOC_CD
				    ,YD_CARLD_LEV_LOC  AS YD_CARLD_LEV_LOC
				    ,TO_CHAR(YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_LEV_DT
				    ,TO_CHAR(YD_CARLD_PNT_WO_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_PNT_WO_DT
				    ,YD_PNT_CD1 AS YD_PNT_CD1
				    ,YD_PNT_CD2 AS YD_PNT_CD2
				    ,YD_CARLD_WRK_BOOK_ID  AS YD_CARLD_WRK_BOOK_ID
				    ,YD_CARLD_SCH_REQ_GP  AS YD_CARLD_SCH_REQ_GP
				    ,YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
				    ,TO_CHAR(YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_ARR_DT
				    ,TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_ST_DT
				    ,TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_CMPL_DT
				    ,YD_CARLD_WRK_ACT_GP  AS YD_CARLD_WRK_ACT_GP
				    ,TO_CHAR(YD_CARLD_CHK_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_CHK_DT
				    ,TO_CHAR(YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_LEV_DT
				    ,TO_CHAR(YD_CARUD_PNT_WO_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_PNT_WO_DT
				    ,YD_PNT_CD3 AS YD_PNT_CD3
				    ,YD_PNT_CD4 AS YD_PNT_CD4
				    ,YD_CARUD_WRK_BOOK_ID  AS YD_CARUD_WRK_BOOK_ID
				    ,YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
				    ,YD_CARUD_SCH_REQ_GP  AS YD_CARUD_SCH_REQ_GP
				    ,TO_CHAR(YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_ARR_DT
				    ,TO_CHAR(YD_CARUD_CHK_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_CHK_DT
				    ,TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_ST_DT
				    ,TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_CMPL_DT
				    ,YD_CARUD_WRK_ACT_GP  AS YD_CARUD_WRK_ACT_GP
				    ,YD_TRN_WRK_DELY_CD  AS YD_TRN_WRK_DELY_CD
				    ,CARD_NO  AS CARD_NO
				    ,YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT
				    ,FRTOMOVE_PLANT_GP AS FRTOMOVE_PLANT_GP
				    ,PROC_TO AS PROC_TO
				    ,RENTPROC_CD AS RENTPROC_CD
				    ,YD_FRTOMOVE_YD_GP AS YD_FRTOMOVE_YD_GP
				    ,YD_FRTOMOVE_BAY_GP AS YD_FRTOMOVE_BAY_GP
				    ,URGENT_FRTOMOVE_WORD_GP AS URGENT_FRTOMOVE_WORD_GP
				    ,DEST_TEL_NO AS DEST_TEL_NO
				    ,YD_DLVRDD_RULE_DD AS YD_DLVRDD_RULE_DD
				    ,SHIPASSIGN_WORD_DATE AS SHIPASSIGN_WORD_DATE
				    ,SHIPASSIGN_WORD_SEQNO AS SHIPASSIGN_WORD_SEQNO
				    ,SHIP_CD AS SHIP_CD
				    ,SHIP_NAME AS SHIP_NAME
				    ,RSHP_HOLD_NO AS RSHP_HOLD_NO
				    ,BERTH_NO AS BERTH_NO
				    ,SAILNO AS SAILNO
				    ,YD_CAR_WRK_GP AS YD_CAR_WRK_GP
				    ,TRANS_ORD_DATE AS TRANS_ORD_DATE
				    ,TRANS_ORD_SEQNO AS TRANS_ORD_SEQNO
				    ,YD_BAYIN_WO_SEQ
				    ,YD_CAR_RCPT_CHK_YN
				    ,YD_CAR_ISSUE_CHK_YN
				    ,YD_CAR_RCPT_CHECKER
				    ,YD_CAR_ISSUE_CHECKER    
				    ,SUBSTR(YD_CARLD_STOP_LOC,1,1) AS YD_GP
				    ,(select count(*)
				     from TB_YD_CRNSCH
				     where YD_WBOOK_ID = YD_CARLD_WRK_BOOK_ID
				     AND DEL_YN = 'N') YD_CRN_SCH_ID
				    ,(SELECT CASE WHEN YD_SCH_CD LIKE 'D_YD__MM' THEN 'Y'
				                  ELSE 'N' END AS M_WRK_GP
				        FROM TB_YD_WRKBOOK
				       WHERE YD_WBOOK_ID = (
				    SELECT CS.YD_CARLD_WRK_BOOK_ID
				    FROM TB_YD_CARSCH CS
				    WHERE 1=1 
				      AND CS.YD_CAR_SCH_ID = C.YD_CAR_SCH_ID
				      AND DEL_YN = 'N') 
				    ) AS M_WRK_GP
				   ,(SELECT CP.YD_FRM_YN FROM TB_YD_CARPOINT CP WHERE  CP.YD_STK_COL_GP = YD_CARLD_STOP_LOC ) AS YD_FRM_YN
				FROM TB_YD_CARSCH C
				WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID 
			 */
			JDTORecordSet carSchRecSet = commDao.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdCarsch01", logId, methodNm, "차량스케쥴 조회");
			
			slabUtils.printLog(logId, "▶  1.1 차량스케줄이 없으면 ERROR처리", "SL");
			slabUtils.printLog(logId, "▷  차량스케줄 조회 건수 : " + carSchRecSet.size()	, "SL");
			
			JDTORecord carSchRecord = null;
			if( carSchRecSet.size() <= 0 ) {
				szMsg = "해당 차량의 차량스케줄이 존재하지 않습니다.";
				slabUtils.printLog(logId, szMsg, "SL");

				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg);
				return jrRtn;
			}else{
				slabUtils.printLog(logId, "▶  차량스케줄이 존재합니다.", "SL");
				carSchRecord = carSchRecSet.getRecord(0);
			}
			
		 	
			slabUtils.printLog(logId, "▶  2. 차량정보확인"					, "SL");
			slabUtils.printLog(logId, "▶  2.1 차량이 '[2]상차도착' 상태인지 확인"	, "SL");
			
			// 차량이 도착했는지 확인
			String sYD_CAR_PROG_STAT = slabUtils.nvl(carSchRecord.getField("YD_CAR_PROG_STAT"), "");	// 차량진행상태
			
			slabUtils.printLog(logId, "▷  차량진행상태(sYD_CAR_PROG_STAT) : " + sYD_CAR_PROG_STAT	, "SL");
			if(!sYD_CAR_PROG_STAT.equals(PSlabYdConstant.YD_CARLD_ARR)){	// 2:상차도착
				szMsg = "해당 차량이 '상차도착' 상태가 아닙니다.";
				slabUtils.printLog(logId, szMsg, "SL");

				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg);
				return jrRtn;
			}
			
			slabUtils.printLog(logId, "▶  2.2 상차작업예약이 이미 생성되었는지 확인", "SL");
			// 작업예약이 있는지 확인
			String sYD_CARLD_WRK_BOOK_ID = slabUtils.nvl(carSchRecord.getField("YD_CARLD_WRK_BOOK_ID"), "");	// 상차작업예약ID
			
			slabUtils.printLog(logId, "▷  상차작업예약ID(sYD_CARUD_WRK_BOOK_ID) : " + sYD_CARLD_WRK_BOOK_ID	, "SL");
			if(!"".equals(sYD_CARLD_WRK_BOOK_ID)){	// 작업예약ID가 있으면
				szMsg = "해당 차량은 이미 상차LOT편성이 완료된 생성된 차량입니다.(상차작업예약이 존재함)";
				slabUtils.printLog(logId, szMsg, "SL");

				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg);
				return jrRtn;
			}
			
			
			slabUtils.printLog(logId, "▶  2.3 차량포인트, 적치열 조회 후 선택된 차량이 점유중인지 확인", "SL");
			/*
			-- com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdStkcol
			SELECT
			        YD_STK_COL_GP                          AS YD_STK_COL_GP
			        ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
			        ,REGISTER                              AS REGISTER
			        ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
			        ,MODIFIER                              AS MODIFIER
			        ,DEL_YN                                AS DEL_YN
			        ,YD_GP                                 AS YD_GP
			        ,YD_BAY_GP                             AS YD_BAY_GP
			        ,YD_EQP_GP                                 AS YD_EQP_GP
			        ,YD_STK_COL_NO                         AS YD_STK_COL_NO
			        ,YD_STK_COL_ACT_STAT                   AS YD_STK_COL_ACT_STAT
			        ,YD_STK_COL_RULE_XAXIS                 AS YD_STK_COL_RULE_XAXIS
			        ,YD_STK_COL_RULE_YAXIS                 AS YD_STK_COL_RULE_YAXIS
			  ,YD_STK_COL_RULE_ZAXIS                 AS YD_STK_COL_RULE_ZAXIS
			        ,YD_STK_COL_W                          AS YD_STK_COL_W
			        ,YD_STK_COL_L                          AS YD_STK_COL_L
			        ,YD_CAR_USE_GP                         AS YD_CAR_USE_GP
			        ,TRN_EQP_CD                            AS TRN_EQP_CD
			        ,CAR_NO                                AS CAR_NO
			        ,CARD_NO                               AS CARD_NO
			        ,WLOC_CD                               AS WLOC_CD
			        ,YD_PNT_CD                             AS YD_PNT_CD
			        ,YD_STK_COL_W_GP                       AS YD_STK_COL_W_GP
			        ,YD_STK_COL_H_MAX                      AS YD_STK_COL_H_MAX
			        ,YD_STK_COL_BED_L_TP                   AS YD_STK_COL_BED_L_TP
			  ,YD_COIL_OUTDIA_GRP_GP                 AS YD_COIL_OUTDIA_GRP_GP
			  ,YD_STKBED_USG_CD                      AS YD_STKBED_USG_CD
			  ,YD_STK_SKID_GP                        AS YD_STK_SKID_GP
			  ,YD_STK_COL_TOLOC_STAT                 AS YD_STK_COL_TOLOC_STAT
			 FROM TB_YD_STKCOL
			WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			  AND DEL_YN        = 'N' */
			
			recPara.setField("YD_STK_COL_GP", paramYD_STK_COL_GP);
			JDTORecordSet stkColRecSet = commDao.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdStkcol", logId, methodNm, "적치열(차량POINT) 조회");
			slabUtils.printLog(logId, "▷  조회된 차량POINT 건수 : " + stkColRecSet.size(), "SL"); // 화면에서 1건만 선택 가능하므로 1이 나와야 정상
			
			JDTORecord stkColRecord = null;
			if( stkColRecSet.size() <= 0 ) {
				szMsg = "차량POINT정보가 존재하지 않습니다.";
				slabUtils.printLog(logId, szMsg, "SL");

				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg);
				return jrRtn;
			}else{
				stkColRecord = stkColRecSet.getRecord(0);
				
				// 화면에서 선택한 차량이 떠나고 다른 차량이 들어왔는 지 등 다른 차량이 작업중인지 확인한다.
				String sTRN_EQP_CD = slabUtils.nvl(stkColRecord.getField("TRN_EQP_CD"), ""); // 해당POINT의 점유중인 차량
				slabUtils.printLog(logId, "▷  화면에서 선택한 차량	: " + paramTRN_EQP_CD	, "SL");
				slabUtils.printLog(logId, "▷  현재 점유중인 차량	: " + sTRN_EQP_CD		, "SL");
				if(!paramTRN_EQP_CD.equals(sTRN_EQP_CD)){
					szMsg = "화면에서 선택한 차량과 점유중인 차량이 다릅니다.";
					slabUtils.printLog(logId, szMsg, "SL");

					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", szMsg);
					return jrRtn;
				}
			}
			
			
			//+++++++++++++++++++++++ 3. 작업예약 등록 +++++++++++++++++++++++
			slabUtils.printLog(logId, "▶  3.1 작업예약생성", "SL");
			
			slabUtils.printLog(logId, "▷  선택된 Row 건수 : " + listDtoSet.length	, "SL"); // 화면에서 1건만 선택 가능하므로 1이 나와야 정상
			JDTORecord listDto = listDtoSet[0];
			
			/********************************************************************************
			 	2Bed 상차 건 확인
			 	- 2021.07.26 상차는 유인작업만 가능하므로 2Bed 실적이 없어졌다.(공차는 레이저포인트가 없어 형상불가하다고 함)
			 	- 실 업무 시 1Bed로 상차작업만 있을 예정이며
			 	     아래 로직부터 2Bed 관련 로직은 추 후 2Bed로 작업이 추가될 수도 있으므로 2Bed도 가능하게 처리함
			********************************************************************************/
			
			slabUtils.printLog(logId, "▶  2Bed 상차인지 확인", "SL");
			listYD_PREP_SCH_ID = slabUtils.nvl(listDto.getField("YD_PREP_SCH_ID"), "");//준비스케쥴id
			slabUtils.printLog(logId, "▷  선택된 준비스케쥴ID(listYD_PREP_SCH_ID) : " + listYD_PREP_SCH_ID	, "SL"); // 화면에서 1건만 선택 가능하므로 1이 나와야 정상
			
			recPara.setField("YD_PREP_SCH_ID"	, listYD_PREP_SCH_ID);
			recPara.setField("YD_SCH_CD"		, ""				);
			
			/* 
				-- 동일상차LOT의 이송LotID 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdPrepsch2Bed
				SELECT MAX(PS.YD_PREP_SCH_ID) AS YD_PREP_SCH_ID2 -- 동일상차 준비스케쥴ID => 이 값이 나오면 2Bed 상차
				  FROM USRYDA.TB_YD_PREPSCH PS
				 WHERE PS.PRI_YD_PREP_SCH_ID = (
				                                    SELECT PRI_YD_PREP_SCH_ID
				                                      FROM USRYDA.TB_YD_PREPSCH
				                                     WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
				                               )
				   AND PS.YD_PREP_SCH_ID <> :V_YD_PREP_SCH_ID
				   AND PS.DEL_YN = 'N'
			 */
			//Lot 편성 건 중  2Bed는 1건으로 조회 -별도 쿼리 
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdPrepsch2Bed", logId, methodNm, "2Bed 준비스케줄 ID 조회");
			
			String sYD_PREP_SCH_ID1	= "";	//1Bed 준비스케쥴id
			String sYD_PREP_SCH_ID2 = "";	//2Bed 준비스케쥴id
			
			if( rsResult.size() <= 0) { 
				slabUtils.printLog(logId, "▶  조회결과 없음. 2Bed 상차인지 확인안됨. 1Bed 실적처리 합니다.", "SL");
				
				sYD_PREP_SCH_ID1 = listYD_PREP_SCH_ID;	// 1Bed 준비스케줄
				sYD_PREP_SCH_ID2 = "";					// 2Bed 준비스케줄
				
			}else{						// 위 쿼리에서 무조건 1row가 나오게 수정했으므로 ELSE만 탄다.
				rsResult.first();
				recPara = rsResult.getRecord();
				
				//2Bed 상차 여부 
				sYD_PREP_SCH_ID1      = listYD_PREP_SCH_ID;										// 1Bed 준비스케줄
				sYD_PREP_SCH_ID2      = slabUtils.paraRecChkNull(recPara, "YD_PREP_SCH_ID2");	// 2Bed 준비스케줄
			}
			
			slabUtils.printLog(logId, "▶  작업예약생성 PARAMETER 확인.", "SL");
			slabUtils.printLog(logId, "▶  준비스케쥴 별로 작업예약이 생성됩니다.", "SL");
			slabUtils.printLog(logId, "▷  준비스케쥴1 : " + sYD_PREP_SCH_ID1		, "SL");	// 1Bed 준비스케쥴
			slabUtils.printLog(logId, "▷  준비스케쥴2 : " + sYD_PREP_SCH_ID2		, "SL");	// 2Bed 준비스케쥴
			
			slabUtils.printLog(logId, "▷  개소코드	: " + paramSPOS_WLOC_CD		, "SL");	// 1Bed 개소코드
			slabUtils.printLog(logId, "▷  운송장비코드	: " + paramTRN_EQP_CD		, "SL");	// 1Bed 운송장비코드
			slabUtils.printLog(logId, "▷  차량포인트	: " + paramYD_STK_COL_GP	, "SL");	// 1Bed 차량포인트
			
			String sTRN_EQP_STK_CAPA = "";
			
			/* BED 적치허용중량 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getStkbedStkBedWtMax
			SELECT YD_STK_BED_WT_MAX
			  FROM TB_YD_STKBED
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			   AND YD_STK_BED_NO = '01'
			*/
			recPara.setField("YD_STK_COL_GP", paramYD_STK_COL_GP);
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getStkbedStkBedWtMax", logId, methodNm, "차량 적치허용중량 조회");
			if(rsResult.size() > 0){
				sTRN_EQP_STK_CAPA = slabUtils.paraRecChkNull(rsResult.getRecord(0), "YD_STK_BED_WT_MAX");
			}
			slabUtils.printLog(logId, "▷  차량적치허용중량	: " + sTRN_EQP_STK_CAPA	, "SL");	// 차량적치허용중량
			
			//작업예약 신규 생성***********************************************************
			jrParam.setField("WLOC_CD"      , paramSPOS_WLOC_CD			); 
			jrParam.setField("TRN_EQP_CD"   , paramTRN_EQP_CD			); 
			jrParam.setField("YD_STK_COL_GP", paramYD_STK_COL_GP		); // DAPT01, DBPT01..
			
			//대상 LOT편성 id가 지정된 경우 
			jrParam.setField("YD_PREP_SCH_ID1"	, sYD_PREP_SCH_ID1	);
			jrParam.setField("YD_PREP_SCH_ID2"	, sYD_PREP_SCH_ID2	); 
			jrParam.setField("TRN_EQP_STK_CAPA"	, sTRN_EQP_STK_CAPA	);	// 차량 상차허용가능 중량
			
			//상차 작업 예약 자동 등록 처리 
			JDTORecord jrWbook 	= this.procYdWbookForCarLd(jrParam);
			String sLotYn		= slabUtils.nvl(jrWbook.getFieldString("LOT_YN"), "0");
			String rtnMsg	 	= slabUtils.nvl(jrWbook.getFieldString("RTN_MSG"), "");
			
			if("N".equals(sLotYn)) {
				slabUtils.printLog(logId, "▶  작업예약생성을 실패했습니다."	, "SL");
				slabUtils.printLog(logId, "▷  오류메세지 : " + rtnMsg	, "SL");
			}
		    
			//작업예약  생성 완료
			String ydWbookId 	= slabUtils.nvl(jrWbook.getFieldString("YD_WBOOK_ID")	, ""	);	// 작업예약ID(1Bed)
			String ydWbookId2 	= slabUtils.nvl(jrWbook.getFieldString("YD_WBOOK_ID2")	, ""	);	// 작업예약ID(2Bed)
			String ydSchCd	  	= slabUtils.nvl(jrWbook.getFieldString("YD_SCH_CD")		, "0"	);	// 스케줄코드
			String ydEqpId 		= slabUtils.nvl(jrWbook.getFieldString("YD_EQP_ID")		, "0"	);	// 크레인 설비ID
			
			
			slabUtils.printLog(logId, "▶  3.2 크레인스케줄 생성 - YDYDJ401 호출", "SL");
			JDTORecord jrCrnSchMsg = null;
			
			if(!"".equals(ydWbookId) ) {
				slabUtils.printLog(logId, "▶   크레인스케줄 Msg생성(YDYDJ401)", "SL");
				slabUtils.printLog(logId, "▷  작업예약ID : " + ydWbookId	, "SL");
				
				jrCrnSchMsg = JDTORecordFactory.getInstance().create();
				
				//크레인 스케줄 기동  호출
				jrCrnSchMsg.setField("JMS_TC_CD"			, "YDYDJ401"				); 
				jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, slabUtils.getDateTime14()	); //JMSTC생성일시	
				jrCrnSchMsg.setField("YD_WBOOK_ID"  		, ydWbookId					); //작업예약ID
				jrCrnSchMsg.setField("YD_SCH_CD"  			, ydSchCd					); //야드스케쥴코드
				jrCrnSchMsg.setField("YD_EQP_ID"  			, ydEqpId					); //야드설비ID (예)DACRA1
				jrCrnSchMsg.setField("EJB_CALL_YN"			, "Y"						); //EJBCall여부(신 크레인스케줄)
				
				jrRtn = slabUtils.addSndData(jrRtn, jrCrnSchMsg);
				
			}
			
			// 2Bed는 작업예약생성까지만 진행 - 스케줄생성 안함
			/*if(!"".equals(ydWbookId2) ) {
				jrCrnSchMsg = JDTORecordFactory.getInstance().create();
				
				//크레인 스케줄 기동  호출
				jrCrnSchMsg.setField("JMS_TC_CD"			, "YDYDJ401"				); 
				jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, slabUtils.getDateTime14()	); //JMSTC생성일시	
				jrCrnSchMsg.setField("YD_WBOOK_ID"  		, ydWbookId2				); //작업예약ID
				jrCrnSchMsg.setField("YD_SCH_CD"  			, ydSchCd					); //야드스케쥴코드
				jrCrnSchMsg.setField("YD_EQP_ID"  			, ydEqpId					); //야드설비ID (예)DACRA1
				jrCrnSchMsg.setField("EJB_CALL_YN"			, "Y"						); //EJBCall여부(신 크레인스케줄)
				
				jrRtn = slabUtils.addSndData(jrRtn, jrCrnSchMsg);
			}*/
			
			szMsg = "상차LOT편성이 완료 되었습니다.";
			slabUtils.printLog(logId, methodNm, "S-");
			
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", szMsg);
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}//end of insCarLdLotNew
}