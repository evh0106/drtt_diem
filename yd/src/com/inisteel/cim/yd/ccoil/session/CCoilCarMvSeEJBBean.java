/**
 * @(#)CCoilCarMvSeEJBBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2019/05/02
 * 
 * @description      C열연 야드공통 차량이동 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2019/05/02   정종균  이현진      최초 등록
 * 
 */
package com.inisteel.cim.yd.ccoil.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yd.ccoil.dao.CCoilDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO;
import com.inisteel.cim.yd.ccommon.dao.CCommDAO;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.ccommon.util.CConstant;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;


/***
 *      [A] 클래스명 : YD 2열연 차량이동 처리
 *
 * @ejb.bean name="CCoilCarMvSeEJB" jndi-name="CCoilCarMvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/

public class CCoilCarMvSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private CCommUtils commUtils = new CCommUtils();
	private CCommDAO commDao = new CCommDAO();
	private CCoilDAO coilDao = new CCoilDAO();
	private SlabYdCommDAO slabCommDao = new SlabYdCommDAO();
	private SlabYdL2RcvDAO slabRcv2Dao = new SlabYdL2RcvDAO();
	private YdPICommDAO   ydPICommDAO   = new YdPICommDAO();
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * [A] 오퍼레이션명 : 2열연 개소코드 여부 체크    
	 * @param sWlocCd
	 * @return boolean (true: 2열연 개소코드)
	 */
	private boolean getYdLocationInfo(String sWlocCd) {
		
		if (
				CConstant.WLOC_CD_C_HR_COIL_MATL_YARD2.equals(sWlocCd)		//DJY22 //C열연 소재야드
			||	CConstant.WLOC_CD_C_HR_COIL_MATL_YARD3.equals(sWlocCd)		//DJY1E	//C열연 제품야드(B, C 2통로)
		  ) 
		{
			return true;
		} else {
			return false;
		}
	}
	
	
//	/**
//	 * [A] 오퍼레이션명 : 소재차량출발(TSYDJ004)   procMatlCarLev
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param JDTORecord rcvMsg
//	 * @return JDTORecord
//	 * @throws DAOException
//	*/
//	public JDTORecord rcvTSYDJ004_OLD(JDTORecord rcvMsg) throws DAOException  {
//		String mthdNm = "소재차량출발[CCoilCarMvSeEJB.rcvTSYDJ004] < " + rcvMsg.getResultMsg();
//		String logId  = rcvMsg.getResultCode();
//		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
//	    try {
//	    	 
//			commUtils.printLog(logId, mthdNm, "S+");
//			commUtils.printParam(logId + "소재차량출발 수신 ", rcvMsg);
//			
//			/******************************
//			 * 신규 구내운송 적용 여부
//			 ******************************/
////			String sAPP900 = coilDao.ApplyYn(logId, mthdNm, "APP900","J","*"); //구내운송 신모듈 적용 여부
////			
////			if ("Y".equals(sAPP900)) {
////				return this.rcvTSYDJ004New(rcvMsg);
////			}
//			
//	    	//수신항목 변수 저장
//			String msgId                = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String sTrnEqpCd      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"         )); //운송장비코드
//			String sSposWlocCd      	= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"       )); //발지개소코드
//			String sSposYdPntCd     	= commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"     )); //발지야드포인트코드
//			String sArrWlocCd      		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD"        )); //착지개소코드
////			String sArrYdPntCd      	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD"      )); //착지야드포인트코드
//			String sTrnWrkFullvoidGp	= commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분 E:공차 F:영차
//			String sTrnEqpStkCapa   	= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA"   )); //운송장비적재능력
//			
//			String ydWoCnclYn           = commUtils.trim(rcvMsg.getFieldString("YD_WO_CNCL_YN"      )); //야드지시 취소여부
//   			
//			String sModifier 			= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); 			//수정자(Backup Only)
//			if ("".equals(sModifier)) { sModifier = msgId; }	
//			
//			boolean bIsCarSchYN     = false;  //차량스케쥴 존재 여부
//			String sMsg             = "";
//			
//			String sCurrDate        = commUtils.getDateTime14();
//			
//			JDTORecord jrParam      = commUtils.getParam(logId, mthdNm, sModifier);
//			
//			/****************************************
//			 * 야드지시취소 
//			 ****************************************/
//			commUtils.printLog(logId, "1. 야드지시취소", "SL");
//			if ("Y".equals(ydWoCnclYn)) {
//				JDTORecord jrCncl = this.procInitCarinfo(rcvMsg);	
//				jrRtn = commUtils.addSndData(jrRtn, jrCncl);
//				
//				return jrRtn;
//			}
//			
//	    	/***************************************
//	    	 * 운송장비코드로 차량스케줄 조회
//	    	 ***************************************/
//			commUtils.printLog(logId, "2. 차량스케줄 조회", "SL");
//			jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd);
//			/*
//			SELECT *
//			  FROM (
//			        SELECT A.*
//			              ,(SELECT YD_STKBED_USG_CD
//			                  FROM TB_YD_STKCOL B
//			                 WHERE B.YD_STK_COL_GP = A.YD_CARLD_STOP_LOC
//			                   AND B.YD_STKBED_USG_CD IN ('A','D','E') --수출, 철송, 주문회
//			               ) AS NEW_DEST_BAY
//			          FROM TB_YD_CARSCH A
//			         WHERE TRN_EQP_CD = :V_TRN_EQP_CD
//			           AND DEL_YN     ='N'
//			         ORDER BY YD_CAR_SCH_ID DESC
//			                , YD_CARUD_CMPL_DT DESC
//			       ) A
//			 WHERE ROWNUM <= 1
//			 */
//			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarschByTrnEqpCd", logId, mthdNm, "차량스케줄 조회");
//			if (jsCarSch.size() > 1) {
//				commUtils.printLog(logId, "차량스케줄 조회 오류 ["+jsCarSch.size()+"건]", "S-");
//				throw new DAOException("스케줄 조회 시 오류발생 - 운송장비코드로 차량스케줄이 여러건["+jsCarSch.size()+"]이 존재합니다");
//			}
//			
//			/********************************************
//			 * 차량스케줄이 존재하는 경우 업무 로직 진행
//			 ********************************************/
//			String ydCarSchId        = "";
//			String ydCarProgStat     = "";
//			String sCarSchSposWlocCd = "";
//			String ydCarldWrkBookId  = "";
//			
//			if (jsCarSch.size() == 1) {
//				commUtils.printLog(logId, "3. 차량스케줄이 존재하는 경우", "SL");
//				bIsCarSchYN = true;
//				
//				/*
//				 * 차량스케줄 정보
//				 */
//				ydCarSchId       = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID"       )); //차량스케줄ID
//				ydCarProgStat    = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT"    )); //차량진행상태
//				sCarSchSposWlocCd= commUtils.trim(jsCarSch.getRecord(0).getFieldString("SPOS_WLOC_CD"        )); //발지개소코드
//				ydCarldWrkBookId = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CARLD_WRK_BOOK_ID")); //야드상차작업예약ID
//				
//				commUtils.printLog(logId, "▷ YD_CAR_PROG_STAT    : "+ydCarProgStat    , "SL");
//				commUtils.printLog(logId, "▷ TRN_WRK_FULLVOID_GP : "+sTrnWrkFullvoidGp, "SL");
//				
//				if ("1".equals(ydCarProgStat) && "E".equals(sTrnWrkFullvoidGp)) {  //상차출발이고 공차인 경우
//					commUtils.printLog(logId, "3.1 상차출발이고 공차인 경우", "SL");
//					/**************************************************
//		    		 * 공차작업
//		    		 **************************************************/
//					commUtils.printLog(logId, "▷ sArrWlocCd[착지개소]        :"+sArrWlocCd        , "SL");
//					commUtils.printLog(logId, "▷ sCarSchSposWlocCd[차량스케줄 발지개소] :"+sCarSchSposWlocCd , "SL");
//					
//					if (sArrWlocCd.equals(sCarSchSposWlocCd)) {
//						commUtils.printLog(logId, "3.1.1 착지와 차량스케쥴 발지가 동일 한 경우", "SL");
//						
//						if (sSposYdPntCd.equals(CConstant.YD_WAIT_PNT_CD) ) { //1Z99
//							commUtils.printLog(logId, "3.1.1.1 발지포인트 1Z99", "SL");
//							//------------------------------------------------------------------------------------------------------------
//							//	전문의 착지개소코드와 차량스케줄의 발지개소코드가 같고 전문의 발지개소POINT가 대기장[1Z99]에서 공차출발 시는 
//							//	중복 상차출발이므로 업무종료
//							//	==> 차량정지POINT요구 지시 모듈 호출 후 업무종료
//							//------------------------------------------------------------------------------------------------------------
//							
//							/********************************************
//							 * 소재차량도착Point요구 모듈 호출 TSYDJ002
//							 ********************************************/
//							JDTORecord inParam = commUtils.getParam(logId, mthdNm, sModifier);
//							inParam.setField("TRN_EQP_CD"			, sTrnEqpCd                );	
//							inParam.setField("WLOC_CD"				, sCarSchSposWlocCd        );			
//							inParam.setField("TRN_WRK_FULLVOID_GP"	, sTrnWrkFullvoidGp        );			
//							inParam.setField("PNT_DMD_DT"			, commUtils.getDateTime14());			
//							
//							JDTORecord jrYdMsg = this.rcvTSYDJ002(inParam);
//							jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
//
//							sMsg = "대기장[1Z99]에서 공차출발 시 소재차량도착Point요구 모듈 호출 완료";
//							commUtils.printLog(logId, sMsg, "S-");
//							return jrRtn;
//							
//							//------------------------------------------------------------------------------------------------------------
//						} else {
//							// IF 수신시 착지 하고 차량스케쥴 발지가 동일 한 경우
//							// 업무종료
//							commUtils.printLog(logId, "착지 하고 차량스케쥴 발지가 동일하지 않는 경우. 업무종료", "S-");
//							return jrRtn;
//						}
//					} else {
//						/***********************************************************
//						 * 차량스케줄 삭제처리, 작업예약삭제, 준비스케줄복구 후 다음 로직 처리
//						 ***********************************************************/
//						jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
//						jrParam.setField("YD_WBOOK_ID"	, ydCarldWrkBookId);
//						
//						jrRtn = commUtils.addSndData(jrRtn, this.procMatlCarCancel(jrParam));
//						bIsCarSchYN = false;
//					}
//				}  
//			} //if ( jsCarSch.size() == 1 ) {
//			
//			//차량스케줄이 존재 하는 경우
//			commUtils.printLog(logId, "▷bIsCarSchYN : "+bIsCarSchYN, "SL");
//			
//			if (bIsCarSchYN) {
//				
//				if ("E".equals(ydCarProgStat)) {  //하차완료
//					/***********************************************************
//					 * 하차완료인 경우 차량스케줄 삭제처리
//					 ***********************************************************/
//					commUtils.printLog(logId, "4.1. ydCarProgStat : E 하차완료", "SL");
//					jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
//					jrParam.setField("DEL_YN"		, "Y");
//					/*
//					UPDATE TB_YD_CARFTMVMTL
//					   SET DEL_YN   = 'Y'
//					     , MODIFIER = :V_MODIFIER
//					     , MOD_DDTT = SYSDATE
//					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
//					 */
//					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updDelCarWrMgtSchMtl", logId, mthdNm, "차량작업재료 삭제");
//					
//					/*
//					UPDATE TB_YD_CARSCH
//					   SET MODIFIER  = :V_MODIFIER
//					     , MOD_DDTT  = SYSDATE
//					     , DEL_YN    = :V_DEL_YN
//					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
//					 */
//					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCarSchReverseYN", logId, mthdNm, "차량스케줄 삭제");
//					
//					bIsCarSchYN = false;
//					
//				} else if ("2".equals(ydCarProgStat)) {  //상차도착
//					/****************************************************
//					 * 공차도착인 차량스케줄이면 차량스케줄을 삭제 
//					 * - 공차도착한 차량에 대해 공차출발 실적을 수신 시는 차량스케줄 삭제
//				     * - 이미 크레인스케줄이 편성된 경우에는 현업담당자가 삭제필요
//					 ****************************************************/
//					commUtils.printLog(logId, "4.2. ydCarProgStat : 2 상차도착", "SL");
//					jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
//					jrParam.setField("DEL_YN"		, "Y");
//					/*
//					UPDATE TB_YD_CARFTMVMTL
//					   SET DEL_YN   = 'Y'
//					     , MODIFIER = :V_MODIFIER
//					     , MOD_DDTT = SYSDATE
//					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
//					 */
//					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updDelCarWrMgtSchMtl", logId, mthdNm, "차량작업재료 수정");
//					
//					/*************************************************
//					 * 차량스케줄 삭제
//					 *************************************************/					
//					/*
//					UPDATE TB_YD_CARSCH
//					   SET MODIFIER  = :V_MODIFIER
//					     , MOD_DDTT  = SYSDATE
//					     , DEL_YN    = :V_DEL_YN
//					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
//					 */
//					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCarSchReverseYN", logId, mthdNm, "차량스케줄 삭제");
//					
//					bIsCarSchYN = false;
//
//				}
//			} //if (bIsCarSchYN) {
//			
//	    	/* ****************************************************************
//	    	 *	발지개소코드, 발지개소Point를 야드저장위치로 변환(출발지 위치)
//	    	 * ****************************************************************/
//			commUtils.printLog(logId, "5. 발지개소코드, 발지Point로 출발지 조회", "SL");
//			jrParam.setField("WLOC_CD"   , sSposWlocCd);
//			jrParam.setField("YD_PNT_CD" , sSposYdPntCd);
//			/*
//			SELECT A.YD_STK_COL_GP
//			     , A.YD_LOC_GP
//			     , A.YD_BAY_GP
//			     , A.YD_EQP_GP
//			     , A.YD_STK_COL_NO
//			     , B.YD_STK_COL_ACT_STAT
//			     , A.YD_STK_COL_RULE_XAXIS
//			     , A.YD_STK_COL_RULE_YAXIS
//			     , A.YD_STK_COL_W
//			     , A.YD_STK_COL_L
//			     , A.YD_CAR_USE_GP
//			     , B.TRN_EQP_CD
//			     , B.CAR_NO
//			     , B.CARD_NO
//			     , A.WLOC_CD
//			     , A.YD_PNT_CD
//			     , B.YD_CARPNT_CD
//			  FROM TB_YD_STKCOL   A
//			     , TB_YD_CARPOINT B
//			 WHERE B.YD_STK_COL_GP = A.YD_STK_COL_GP
//			   AND A.WLOC_CD       = :V_WLOC_CD
//			   AND A.YD_PNT_CD     = :V_YD_PNT_CD
//			   AND A.DEL_YN        = 'N'
//			   AND B.DEL_YN        = 'N'
//			*/
//			JDTORecordSet jsStkCol = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStkcolWLocCdandPntCd", logId, mthdNm, "적치열 조회");
//
//			/* ***********************************************************
//			 * 타공정으로 출발한 경우에는 적치열 베드 단정보를 초기화 한다. 
//			 * ***********************************************************/
//			commUtils.printLog(logId, "6. 타공정으로 출발한 경우에는 적치열 베드 단정보를 초기화", "SL");
//			
//			String ydCarldStopLoc = ""; 
//			String sColTrnEqpCd   = ""; 
//			
//	    	if (jsStkCol.size() > 0) {
//	    		
//	    		jsStkCol.absolute(1);
//		    	JDTORecord jrStkCol = jsStkCol.getRecord();
//		    	
//		    	ydCarldStopLoc = commUtils.trim(jrStkCol.getFieldString("YD_STK_COL_GP")); //열구분을 조회(출발지)
//		    	sColTrnEqpCd   = commUtils.trim(jrStkCol.getFieldString("TRN_EQP_CD"   )); //적치열에 존재하는 운송장비코드
//		    	
//				sMsg= "출발야드의 적치열["+ydCarldStopLoc+"]의 운송장비코드["+sColTrnEqpCd+"]와 전문의 운송장비코드["+sTrnEqpCd+"] 비교 ";
//				commUtils.printLog(logId, sMsg , "SL");
//				
//				//------------------------------------------------------------------------------------------------------------
//				//	적치열의 운송장비코드와 전문의 운송장비코드가 일치하는 경우에만 맵 Clear 시작
//				//------------------------------------------------------------------------------------------------------------
//				 
//				if (sColTrnEqpCd.equals(sTrnEqpCd)) {
//					
//					sMsg= "출발야드의 적치열["+ydCarldStopLoc+"]의 운송장비코드["+sColTrnEqpCd+"]와 전문의 운송장비코드["+sTrnEqpCd+"]가 같으므로 맵 Clear 시작 ";
//					commUtils.printLog(logId, sMsg , "SL");
//					/****************************************************
//					 * 적치열 비활성(COL)
//					 * 적치포인트 비활성(YDPOINT)
//					 * 적치BED
//					 * 적치단
//					 ****************************************************/
//			    	jrParam.setField("YD_STK_COL_GP", ydCarldStopLoc);
//					
//					/*
//					UPDATE TB_YD_STKCOL
//					   SET YD_STK_COL_ACT_STAT = DECODE(YD_STK_COL_ACT_STAT, 'N', 'N', 'C')
//					     , TRN_EQP_CD       = ''
//					     , YD_CAR_USE_GP    = ''
//					     , CAR_NO           = ''
//					     , CARD_NO          = ''
//					     , MODIFIER         = :V_MODIFIER
//					     , MOD_DDTT         = SYSDATE
//					 WHERE YD_STK_COL_GP    = :V_YD_STK_COL_GP
//					 */
//					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updCarWrMgtStkcol", logId, mthdNm, "TB_YD_STKCOL 초기화");
//					
//				    //저장위치로 초기화 하는 경우(구내운송)
//					/* 
//					UPDATE TB_YD_CARPOINT
//					   SET TRN_EQP_CD = NULL
//					     , YD_STK_COL_ACT_STAT = DECODE(CARD_NO,NULL,(DECODE(YD_STK_COL_ACT_STAT,'N','N',:V_STAT)),YD_STK_COL_ACT_STAT)
//					     , MOD_DDTT = SYSDATE
//					     , MODIFIER = :V_MODIFIER
//					 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP 
//					   AND DEL_YN = 'N'
//					*/ 
//					jrParam.setField("STAT"  			, "C"); 
//			    	jrParam.setField("YD_STK_COL_GP"	, ydCarldStopLoc);
//			    	
//			    	commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointstackcolgpupdateCT", logId, mthdNm, "TB_YD_CARPOINT 수정");
//					
//				    
//					
//				    jrParam = commUtils.getParam(logId, mthdNm, sModifier);
//				    jrParam.setField("YD_STK_COL_GP"      , ydCarldStopLoc);
//			    	jrParam.setField("YD_STK_BED_WT_MAX"  , CConstant.YD_STK_BED_WT_MAX_DEFAULT); // 300000
//			    	jrParam.setField("YD_STK_BED_ACT_STAT", "C");
//			    	
//			    	/*
//					UPDATE TB_YD_STKBED
//					   SET YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
//					     , YD_STK_BED_WT_MAX   = :V_YD_STK_BED_WT_MAX
//					     , MOD_DDTT            = SYSDATE
//					     , MODIFIER            = :V_MODIFIER
//					 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
//			    	 */
//					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updStkbedActStat", logId, mthdNm, "TB_YD_STKBED 비활성화등록");
//				    
//				    
//					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
//			    	jrParam.setField("YD_STK_COL_GP"		, ydCarldStopLoc);
//			    	jrParam.setField("YD_STK_LYR_ACT_STAT"	, "C");
//			    	jrParam.setField("STL_NO"				, "");
//			    	jrParam.setField("YD_STK_LYR_MTL_STAT"	, "E");
//			    	
//			    	/*
//			    	UPDATE TB_YD_STKLYR
//			    	   SET YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
//			    	     , STL_NO              = :V_STL_NO
//			    	     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
//			    	     , MODIFIER            = :V_MODIFIER
//			    	     , MOD_DDTT            = SYSDATE
//			    	WHERE YD_STK_COL_GP        = :V_YD_STK_COL_GP
//			    	*/
//			    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updStklyrYdStkColGp", logId, mthdNm, "TB_YD_STKLYR 초기화");
//					
//			    	/**********************************************************
//					* 업무기준 : 차량 출발 시 저장위치 제원 야드L2로 전송
//					**********************************************************/
//					JDTORecord jrL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
//
//					jrL2Msg.setField("YD_INFO_SYNC_CD" , "4"); // 1:동,2:SPAN,3:열,4:BED
//			    	jrL2Msg.setField("YD_GP"           , ydCarldStopLoc.substring(0, 1));
//			    	jrL2Msg.setField("YD_STK_COL_GP"   , ydCarldStopLoc);
//			    	jrL2Msg.setField("YD_STK_BED_NO"   , "01");
//
//			    	if ("E".equals(sTrnWrkFullvoidGp)) {
//			    		jrL2Msg.setField("YD_CAR_PROG_STAT", "1");
//			    		jrL2Msg.setField("YD_EQP_WRK_STAT" , "L");
//			    		sMsg = "공차출발시 시 저장위치 제원 야드L2로 전송";
//			    	} else {
//			    		jrL2Msg.setField("YD_CAR_PROG_STAT", "A");
//			    		jrL2Msg.setField("YD_EQP_WRK_STAT" , "L");
//			    		sMsg = "영차출발시 시 저장위치 제원 야드L2로 전송";
//			    	}
//			    	
//			    	jrL2Msg.setField("YD_CAR_ARRSTRT_STAT" , "S"        ); //A:도착, S:출발
//			    	jrL2Msg.setField("TRN_EQP_CD"  	       , sTrnEqpCd  );
//			    	jrL2Msg.setField("YD_CAR_USE_GP"       , "L"        );//구내운송
//
//					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L001_CarInfo", jrL2Msg));
//					
//					commUtils.printLog(logId, sMsg, "SL");
//					
//					/********************************************************
//					 * 이송차량 영차/공차 출발시 해당 위치대기차량 입동지시 요구
//					 ********************************************************/
//					/*
//					SELECT A.*
//					     ,(SELECT YD_CARPNT_CD FROM TB_YD_CARPOINT         
//					        WHERE YD_GP = 'J'
//					          AND DEL_YN = 'N'
//					          AND YD_STK_COL_GP = :V_YD_STK_COL_GP
//					          AND ROWNUM = 1
//					      ) YD_CARPNT_CD
//					  FROM (
//					        SELECT A.*
//					             , 'E' AS TRN_WRK_FULLVOID_GP
//					             , SPOS_WLOC_CD AS WLOC_CD
//					          FROM TB_YD_CARSCH A
//					         WHERE DEL_YN = 'N'
//					           AND YD_CAR_PROG_STAT  = '1' 
//					           AND SPOS_WLOC_CD IN ('DJY21', 'DJY22', 'DJY1E') 
//					           AND YD_CARLD_STOP_LOC = :V_YD_STK_COL_GP
//					         UNION ALL  
//					        SELECT A.*
//					             , 'F' AS TRN_WRK_FULLVOID_GP
//					             , ARR_WLOC_CD AS WLOC_CD
//					          FROM TB_YD_CARSCH A
//					         WHERE DEL_YN = 'N'
//					           AND YD_CAR_PROG_STAT = 'A' 
//					           AND ARR_WLOC_CD  IN ('DJY21', 'DJY22', 'DJY1E')
//					           AND YD_CARUD_STOP_LOC = :V_YD_STK_COL_GP
//					       ) A
//					 ORDER BY YD_BAYIN_WO_SEQ  
//					        , YD_CAR_SCH_ID
//					 */
//			    	JDTORecordSet jsCarSchNext = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getTsNextTrnEqp", logId, mthdNm, "차량스케줄 조회");
//			    	if (jsCarSchNext.size() > 0) {
//			    		
//			    		String ydCarUseGp = jsCarSchNext.getRecord(0).getFieldString("YD_CAR_USE_GP"); // G출하 L구내운송
//			    		
//			    		String sApp820 = coilDao.ApplyYn(logId, mthdNm, "APP820", "J", "*"); //입동지시 요구
//			    		if ("Y".equals(sApp820)) {
//			    			if ("L".equals(ydCarUseGp)) {
//				    			String sTrnEqpCdNext         = jsCarSchNext.getRecord(0).getFieldString("TRN_EQP_CD");
//					    		String sTrnWrkFullvoidGpNext = jsCarSchNext.getRecord(0).getFieldString("TRN_WRK_FULLVOID_GP");
//					    		String sWlocCdNext           = jsCarSchNext.getRecord(0).getFieldString("WLOC_CD");
//					    		
//					    		jrParam = commUtils.getParam(logId, mthdNm, sModifier);
//								jrParam.setField("TRN_EQP_CD"			, sTrnEqpCdNext        );	
//								jrParam.setField("WLOC_CD"				, sWlocCdNext          );	
//								jrParam.setField("TRN_WRK_FULLVOID_GP"	, sTrnWrkFullvoidGpNext);	
//								jrParam.setField("PNT_DMD_DT"			, sCurrDate  );			
//								
//								JDTORecord jrYdMsg = this.rcvTSYDJ002(jrParam);
//								jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
//				    		} 
//				    		
//				    		if ("G".equals(ydCarUseGp)) {
//
//				    			String ydCarPntCd = jsCarSchNext.getRecord(0).getFieldString("YD_CARPNT_CD");
//				    			
//				    			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
//								jrParam.setField("JMS_TC_CD"		 , "YDYDJ553");    
//								jrParam.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
//								jrParam.setField("YD_CARPNT_CD"	     , ydCarPntCd);	// 입동포인트
//								
//								JDTORecord jrCarBayInOrdReq = this.rcvYDYDJ553(jrParam);
//								jrRtn = commUtils.addSndData(jrRtn, jrCarBayInOrdReq);
//				    		}	
//			    		} else {
//			    			String sTrnEqpCdNext         = jsCarSchNext.getRecord(0).getFieldString("TRN_EQP_CD");
//				    		String sTrnWrkFullvoidGpNext = jsCarSchNext.getRecord(0).getFieldString("TRN_WRK_FULLVOID_GP");
//				    		String sWlocCdNext           = jsCarSchNext.getRecord(0).getFieldString("WLOC_CD");
//				    		
//				    		jrParam = commUtils.getParam(logId, mthdNm, sModifier);
//							jrParam.setField("TRN_EQP_CD"			, sTrnEqpCdNext        );	
//							jrParam.setField("WLOC_CD"				, sWlocCdNext          );	
//							jrParam.setField("TRN_WRK_FULLVOID_GP"	, sTrnWrkFullvoidGpNext);	
//							jrParam.setField("PNT_DMD_DT"			, sCurrDate  );			
//							
//							JDTORecord jrYdMsg = this.rcvTSYDJ002(jrParam);
//							jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
//			    		}
//			    	}
//					
//				
//				} else {
//					sMsg = "출발야드의 적치열["+ydCarldStopLoc+"]의 운송장비코드["+sColTrnEqpCd+"]와 전문의 운송장비코드["+sTrnEqpCd+"]가 다르므로 맵 Clear 안함 ";
//					commUtils.printLog(logId, sMsg , "SL");
//				}
//
//	    	} else if ( jsStkCol.size() == 0 ) {
//	    		
//				if (bIsCarSchYN) {
//					
//					//해당야드로 출발을 한 후 대기장에 도착 후 대기장에서 출발인 경우에는 업무종료 처리
//					sMsg = "발지개소코드["+sSposWlocCd+"], 발지포인트코드["+sSposYdPntCd+"] 확인 처리 ";
//					commUtils.printLog(logId, sMsg , "SL");
//					
//					if ( sCarSchSposWlocCd.equals(sSposWlocCd) && sSposYdPntCd.equals(CConstant.YD_WAIT_PNT_CD) ) { //대기장포인트코드
//						//발지개소코드가 이미존재하는 차량스케줄의 발지개소코드와 같고 발지포인트코드가 대기장 포인트코드인 경우에는 업무종료처리
//						sMsg= "발지개소코드["+sSposWlocCd+"]가 이미존재하는 차량스케줄의 발지개소코드["+sCarSchSposWlocCd+"]와 같고 발지포인트코드["+sSposYdPntCd+"]가 대기장 포인트코드인 경우에는 업무종료처리합니다.";
//						commUtils.printLog(logId, sMsg , "S-");
//						return jrRtn;
//					}
//				}
//	    	}
//	    	
//	
//			//상차LOT편성 및 작업예약등록시 파라미터값
//			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
//			jrParam.setField("YD_CAR_USE_GP"    , "L"                      );
//	    	jrParam.setField("YD_EQP_ID"        , "XXPT01"                 );
//	    	jrParam.setField("TRN_EQP_CD"       , sTrnEqpCd                );
//	    	jrParam.setField("WLOC_CD"          , sArrWlocCd               );
//	    	jrParam.setField("PNT_DMD_DT"       , commUtils.getDateTime14());
//	    	jrParam.setField("SPOS_YD_PNT_CD"   , sSposYdPntCd             );
//	    	jrParam.setField("SPOS_WLOC_CD"     , sSposWlocCd              );
//
//	    	/********************************************
//	    	 * 7. 공 영차시 처리 로직
//	    	 ********************************************/
//	    	if ("E".equals(sTrnWrkFullvoidGp)) { //공차
//	    		commUtils.printLog(logId, "7.1. 공차 처리", "SL");
//	    		jrParam.setField("SP_TRUCK_LOADING_LOC_TP", "S");
//	    		jrParam.setField("YD_CARLD_LEV_LOC"       , ydCarldStopLoc); //야드상차출발위치
//	    		jrParam.setField("TRN_EQP_STK_CAPA"       , sTrnEqpStkCapa);
//	    		
//	    		/*************************************
//	    		 * 상차 LOT편성 호출
//	    		 *************************************/
//	    		JDTORecord jrRst = this.chkWlocCdLotComp(jrParam); //상차 LOT편성
//	    		jrRtn = commUtils.addSndData(jrRtn, jrRst);
//	    		
//	    	} else if ("F".equals(sTrnWrkFullvoidGp)) { //영차
//	    		commUtils.printLog(logId, "7.2. 영차 처리", "SL");
//	    		if ( jsCarSch.size() <= 0 ) {
//					throw new DAOException("운송장비코드["+sTrnEqpCd+"]로 차량스케줄 조회 Error");
//				}
//	    		
//	    		commUtils.printLog(logId, "7.2.1 하차재료 조회", "SL");
//	    		jrParam.setField("SP_TRUCK_LOADING_LOC_TP", "H"             );
//	    		jrParam.setField("YD_WBOOK_ID"            , ydCarldWrkBookId);
//	    		jrParam.setField("YD_CAR_SCH_ID"          , ydCarSchId      );
//	    		
//	    		/*
//	    		SELECT A.YD_CAR_SCH_ID                          AS YD_CAR_SCH_ID
//	    		     , A.STL_NO                                 AS STL_NO
//	    		     , A.REGISTER                               AS REGISTER
//	    		     , TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
//	    		     , A.MODIFIER                               AS MODIFIER
//	    		     , TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
//	    		     , A.DEL_YN                                 AS DEL_YN
//	    		     , A.YD_CAR_UPP_LOC_CD                      AS YD_CAR_UPP_LOC_CD
//	    		     , A.YD_STK_BED_NO                          AS YD_STK_BED_NO
//	    		     , A.YD_STK_LYR_NO                          AS YD_STK_LYR_NO
//	    		     , A.HCR_GP                                 AS HCR_GP
//	    		     , A.STL_PROG_CD                            AS STL_PROG_CD
//	    		     , A.YD_MTL_ITEM                            AS YD_MTL_ITEM
//	    		     , A.YD_ROUTE_GP                            AS YD_ROUTE_GP
//	    		     , DECODE(C.ARR_WLOC_CD
//	    		            , 'DHY21', 'A'
//	    		            , 'C3S01', 'M'
//	    		            , 'D3Y43', '2'
//	    		            , 'D2Y43', '0'
//	    		            , 'DJY25', 'S'
//	    		            , B.YD_AIM_YD_GP) AS YD_AIM_YD_GP
//	    		       --//Tong 크레인 사용 금지재 -> A동 입고
//	    		     , (CASE WHEN C.ARR_WLOC_CD = 'DHY21'
//	    		              AND (SELECT 'Y' FROM USRYDA.VW_YD_SLABHSM C WHERE C.HSM_GP = 'X' AND C.SLAB_NO=B.STL_NO) = 'Y'
//	    		                  THEN 'A'
//	    		             WHEN C.ARR_WLOC_CD = 'DHY21' AND B.YD_AIM_YD_GP = 'A' AND MAX(TEMP9) OVER(PARTITION BY A.YD_CAR_SCH_ID) = 'Y'
//	    		                  THEN 'B'
//	    		             WHEN C.REGISTER = 'runTsRetHt'
//	    		                  THEN 'A' --//반송인 경우 A동 하차
//	    		             WHEN C.ARR_WLOC_CD = 'C3S01'
//	    		                  THEN 'A' --//C3#스카핑 A동 하차
//	    		             WHEN C.YD_CAR_USE_GP = 'L' AND C.ARR_WLOC_CD = 'DJY22' AND C.YD_CAR_PROG_STAT IN ('A', 'B')
//	    		                  THEN NVL(SUBSTR(YD_CARUD_STOP_LOC, 2, 1), B.YD_AIM_BAY_GP) -- 구내운송 하차 도착위치로 작업예약 생성
//	    		        ELSE B.YD_AIM_BAY_GP END
//	    		       ) AS YD_AIM_BAY_GP
//	    		     , B.YD_RCPT_PLN_STR_LOC
//	    		     , B.YD_RCPT_PLN_STR_LOC1
//	    		     , B.YD_RCPT_PLN_STR_LOC2
//	    		     , (SELECT ARR_YD_PNT_CD
//	    		          FROM USRTSA.TB_TS_MATL_FTMV_WO C
//	    		         WHERE C.TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO) FROM TB_TS_MATL_FTMV_WO D WHERE D.STL_NO=C.STL_NO)
//	    		           AND C.STL_NO=A.STL_NO ) AS ARR_YD_PNT_CD
//	    		     , C.YD_CAR_PROG_STAT
//	    		  FROM TB_YD_CARFTMVMTL A
//	    		     , TB_YD_STOCK      B
//	    		     , TB_YD_CARSCH     C
//	    		  WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
//	    		    AND A.YD_CAR_SCH_ID = C.YD_CAR_SCH_ID
//	    		    AND A.STL_NO        = B.STL_NO(+)
//	    		    AND A.DEL_YN        = 'N'
//	    		  ORDER BY YD_STK_BED_NO, YD_STK_LYR_NO DESC
//	    		*/
//	    		JDTORecordSet jsCarMtl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarftmvmtlID", logId, mthdNm, "이송재료 조회");
//	    		
//	    		if ( jsCarMtl.size() <= 0 ) {
//					throw new DAOException("이송재료 조회 Error");
//				}
//	    		
//	    		String sStlNo = "";
//	    		for (int i = 1; i <= jsCarMtl.size(); ++i) {
//	    			
//	    			jsCarMtl.absolute(i);
//	    			sStlNo = jsCarMtl.getRecord().getFieldString("STL_NO");
//	    			
//	    			/*********************************************************
//	    			 * 코일 저장품 등록
//	    			 *********************************************************/
//	    			commUtils.printLog(logId, "7.2.2 저장품 등록", "SL");
//	    			coilDao.stockProcCom(logId, mthdNm, sStlNo, 1);
//
//	    		} //end for
//
//	    		/*****************************************
//	    		 * 착지가 2열연 일경우 하차출발(A)로 변경
//	    		 *****************************************/
//	    		if (this.getYdLocationInfo(sArrWlocCd)) {
//	    			commUtils.printLog(logId, "7.2.4 착지 2열연 상태코드 변경(A 하차출발)", "SL");
//	    			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
//					jrParam.setField("YD_CAR_SCH_ID"		, ydCarSchId);
//					jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, ""        );
//					jrParam.setField("YD_CAR_PROG_STAT"		, "A"       ); //하차 출발
//					jrParam.setField("YD_CARUD_LEV_DT"		, sCurrDate ); //하차출발일시
//					
//					/* 
//					UPDATE TB_YD_CARSCH
//					   SET MODIFIER         = :V_MODIFIER
//					     , MOD_DDTT         = SYSDATE
//					     , YD_CARUD_WRK_BOOK_ID = :V_YD_CARUD_WRK_BOOK_ID
//					     , YD_CAR_PROG_STAT = :V_YD_CAR_PROG_STAT
//					     , YD_CARUD_LEV_DT  = :V_YD_CARUD_LEV_DT
//					 WHERE YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
//					*/ 
//					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarsch", logId, mthdNm, "차량스케쥴수정");
//					
//					/*****************************************
//		    		 * 소재차량도착Point요구 - 영차
//		    		 *****************************************/
//					commUtils.printLog(logId, "8. 소재차량도착 point요구", "SL");
//				
//					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
//					jrParam.setField("TRN_EQP_CD"			, sTrnEqpCd  );	
//					jrParam.setField("WLOC_CD"				, sArrWlocCd );			
//					jrParam.setField("TRN_WRK_FULLVOID_GP"	, "F"        );			
//					jrParam.setField("PNT_DMD_DT"			, sCurrDate  );			
//					
//					JDTORecord jrYdMsg = this.rcvTSYDJ002(jrParam);
//					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);	
//	    		} 
//	    		
//	    	} else {
//	    		throw new Exception("운송작업영공구분코드 Error");
//	    	}
//	    	
//			commUtils.printLog(logId, mthdNm, "S-");
//			return jrRtn;
//			
//		} catch (DAOException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
//		}
//	}	
	
	
	/**
	 * [A] 오퍼레이션명 : 소재차량출발(TSYDJ004)   procMatlCarLev
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvTSYDJ004(JDTORecord rcvMsg) throws DAOException  {
		String mthdNm = "소재차량출발[CCoilCarMvSeEJB.rcvTSYDJ004] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
	    try {
	    	 
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			/******************************
			 * 신규 구내운송 적용 여부
			 ******************************/
	    	//수신항목 변수 저장
			String msgId                = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sTrnEqpCd      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"         )); //운송장비코드
			String sSposWlocCd      	= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"       )); //발지개소코드
			String sSposYdPntCd     	= commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"     )); //발지야드포인트코드
			String sArrWlocCd      		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD"        )); //착지개소코드
//			String sArrYdPntCd      	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD"      )); //착지야드포인트코드
			String sTrnWrkFullvoidGp	= commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분 E:공차 F:영차
			String sTrnEqpStkCapa   	= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA"   )); //운송장비적재능력
			String ydWoCnclYn           = commUtils.trim(rcvMsg.getFieldString("YD_WO_CNCL_YN"      )); //야드지시 취소여부
   			
			String sModifier 			= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); 			//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }	
			
			boolean bIsCarSchYN     = false;  //차량스케쥴 존재 여부
			String sMsg             = "";
			
			String sCurrDate        = commUtils.getDateTime14();
			
			JDTORecord jrParam      = commUtils.getParam(logId, mthdNm, sModifier);
			
			/****************************************
			 * 야드지시취소 
			 ****************************************/
			commUtils.printLog(logId, "1. 야드지시취소", "SL");
			if ("Y".equals(ydWoCnclYn)) {
				JDTORecord jrCncl = this.procInitCarinfo(rcvMsg);	
				jrRtn = commUtils.addSndData(jrRtn, jrCncl);
				
				return jrRtn;
			}
			
	    	/***************************************
	    	 * 운송장비코드로 차량스케줄 조회
	    	 ***************************************/
			commUtils.printLog(logId, "2. 차량스케줄 조회", "SL");
			jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd);
			/*
			SELECT *
			  FROM (
			        SELECT A.*
			              ,(SELECT YD_STKBED_USG_CD
			                  FROM TB_YD_STKCOL B
			                 WHERE B.YD_STK_COL_GP = A.YD_CARLD_STOP_LOC
			                   AND B.YD_STKBED_USG_CD IN ('A','D','E') --수출, 철송, 주문회
			               ) AS NEW_DEST_BAY
			          FROM TB_YD_CARSCH A
			         WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			           AND DEL_YN     ='N'
			         ORDER BY YD_CAR_SCH_ID DESC
			                , YD_CARUD_CMPL_DT DESC
			       ) A
			 WHERE ROWNUM <= 1
			 */
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarschByTrnEqpCd", logId, mthdNm, "차량스케줄 조회");
			if (jsCarSch.size() > 1) {
				commUtils.printLog(logId, "차량스케줄 조회 오류 ["+jsCarSch.size()+"건]", "S-");
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
				commUtils.printLog(logId, "3. 차량스케줄이 존재하는 경우", "SL");
				bIsCarSchYN = true;
				
				ydCarSchId       = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID"       )); //차량스케줄ID
				ydCarProgStat    = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT"    )); //차량진행상태
				sCarSchSposWlocCd= commUtils.trim(jsCarSch.getRecord(0).getFieldString("SPOS_WLOC_CD"        )); //발지개소코드
				ydCarldWrkBookId = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CARLD_WRK_BOOK_ID")); //야드상차작업예약ID
				
				if ("1".equals(ydCarProgStat) && "E".equals(sTrnWrkFullvoidGp)) {  //상차출발이고 공차인 경우
					commUtils.printLog(logId, "3.1 상차출발이고 공차인 경우", "SL");
					/**************************************************
		    		 * 공차작업
		    		 **************************************************/
					if (sArrWlocCd.equals(sCarSchSposWlocCd)) {
						// IF 수신시 착지 하고 차량스케쥴 발지가 동일 한 경우 업무종료
						commUtils.printLog(logId, "착지 하고 차량스케쥴 발지가 동일하지 않는 경우. 업무종료", "S-");
						return jrRtn;
					} else {
						/***********************************************************
						 * 차량스케줄 삭제처리, 작업예약삭제, 준비스케줄복구 후 다음 로직 처리
						 ***********************************************************/
						jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
						jrParam.setField("YD_WBOOK_ID"	, ydCarldWrkBookId);
						
						jrRtn = commUtils.addSndData(jrRtn, this.procMatlCarCancel(jrParam));
						bIsCarSchYN = false;
					}
				}  
			} //if ( jsCarSch.size() == 1 ) {
			
			//차량스케줄이 존재 하는 경우
			commUtils.printLog(logId, "▷bIsCarSchYN : "+bIsCarSchYN, "SL");
			
			if (bIsCarSchYN) {
				
				if ("E".equals(ydCarProgStat)) {  //하차완료
					/***********************************************************
					 * 하차완료인 경우 차량스케줄 삭제처리
					 ***********************************************************/
					commUtils.printLog(logId, "4.1. ydCarProgStat : E 하차완료", "SL");
					jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
					jrParam.setField("DEL_YN"		, "Y");
					/*
					UPDATE TB_YD_CARFTMVMTL
					   SET DEL_YN   = 'Y'
					     , MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updDelCarWrMgtSchMtl", logId, mthdNm, "차량작업재료 삭제");
					
					/*
					UPDATE TB_YD_CARSCH
					   SET MODIFIER  = :V_MODIFIER
					     , MOD_DDTT  = SYSDATE
					     , DEL_YN    = :V_DEL_YN
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCarSchReverseYN", logId, mthdNm, "차량스케줄 삭제");
					
					bIsCarSchYN = false;
					
				} else if ("2".equals(ydCarProgStat)) {  //상차도착
					/****************************************************
					 * 공차도착인 차량스케줄이면 차량스케줄을 삭제 
					 * - 공차도착한 차량에 대해 공차출발 실적을 수신 시는 차량스케줄 삭제
				     * - 이미 크레인스케줄이 편성된 경우에는 현업담당자가 삭제필요
					 ****************************************************/
					commUtils.printLog(logId, "4.2. ydCarProgStat : 2 상차도착", "SL");
					jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
					jrParam.setField("DEL_YN"		, "Y");
					/*
					UPDATE TB_YD_CARFTMVMTL
					   SET DEL_YN   = 'Y'
					     , MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updDelCarWrMgtSchMtl", logId, mthdNm, "차량작업재료 수정");
					
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
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCarSchReverseYN", logId, mthdNm, "차량스케줄 삭제");
					
					bIsCarSchYN = false;
				}
			} //if (bIsCarSchYN) {
			
	    	/* ****************************************************************
	    	 *	발지개소코드, 발지개소Point를 야드저장위치로 변환(출발지 위치)
	    	 * ****************************************************************/
			commUtils.printLog(logId, "5. 발지개소코드, 발지Point로 출발지 조회", "SL");
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
			JDTORecordSet jsStkCol = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStkcolWLocCdandPntCd", logId, mthdNm, "적치열 조회");

			/* ***********************************************************
			 * 타공정으로 출발한 경우에는 적치열 베드 단정보를 초기화 한다. 
			 * ***********************************************************/
			commUtils.printLog(logId, "6. 타공정으로 출발한 경우에는 적치열 베드 단정보를 초기화", "SL");
			
			String ydCarldStopLoc = ""; 
			String sColTrnEqpCd   = ""; 
			
	    	if (jsStkCol.size() > 0) {
	    		
	    		jsStkCol.absolute(1);
		    	JDTORecord jrStkCol = jsStkCol.getRecord();
		    	
		    	ydCarldStopLoc = commUtils.trim(jrStkCol.getFieldString("YD_STK_COL_GP")); //열구분을 조회(출발지)
		    	sColTrnEqpCd   = commUtils.trim(jrStkCol.getFieldString("TRN_EQP_CD"   )); //적치열에 존재하는 운송장비코드
		    	
				sMsg= "출발야드의 적치열["+ydCarldStopLoc+"]의 운송장비코드["+sColTrnEqpCd+"]와 전문의 운송장비코드["+sTrnEqpCd+"] 비교 ";
				commUtils.printLog(logId, sMsg , "SL");
				
				//------------------------------------------------------------------------------------------------------------
				//	적치열의 운송장비코드와 전문의 운송장비코드가 일치하는 경우에만 맵 Clear 시작
				//------------------------------------------------------------------------------------------------------------
				 
				if (sColTrnEqpCd.equals(sTrnEqpCd)) {
					
					sMsg= "출발야드의 적치열["+ydCarldStopLoc+"]의 운송장비코드["+sColTrnEqpCd+"]와 전문의 운송장비코드["+sTrnEqpCd+"]가 같으므로 맵 Clear 시작 ";
					commUtils.printLog(logId, sMsg , "SL");
					/****************************************************
					 * 적치열 비활성(COL)
					 * 적치포인트 비활성(YDPOINT)
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
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updCarWrMgtStkcol", logId, mthdNm, "TB_YD_STKCOL 초기화");
					
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
			    	
			    	commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointstackcolgpupdateCT", logId, mthdNm, "TB_YD_CARPOINT 수정");
					
				    jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				    jrParam.setField("YD_STK_COL_GP"      , ydCarldStopLoc);
			    	jrParam.setField("YD_STK_BED_WT_MAX"  , CConstant.YD_STK_BED_WT_MAX_DEFAULT); // 300000
			    	jrParam.setField("YD_STK_BED_ACT_STAT", "C");
			    	
			    	/*
					UPDATE TB_YD_STKBED
					   SET YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
					     , YD_STK_BED_WT_MAX   = :V_YD_STK_BED_WT_MAX
					     , MOD_DDTT            = SYSDATE
					     , MODIFIER            = :V_MODIFIER
					 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
			    	 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updStkbedActStat", logId, mthdNm, "TB_YD_STKBED 비활성화등록");
				    
				    
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
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
			    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updStklyrYdStkColGp", logId, mthdNm, "TB_YD_STKLYR 초기화");
					
			    	/**********************************************************
					* 업무기준 : 차량 출발 시 저장위치 제원 야드L2로 전송
					**********************************************************/
					JDTORecord jrL2Msg = commUtils.getParam(logId, mthdNm, sModifier);

					jrL2Msg.setField("YD_INFO_SYNC_CD" , "4"); // 1:동,2:SPAN,3:열,4:BED
			    	jrL2Msg.setField("YD_GP"           , "J");
			    	jrL2Msg.setField("YD_STK_COL_GP"   , ydCarldStopLoc);
			    	jrL2Msg.setField("YD_STK_BED_NO"   , "01");

			    	if ("E".equals(sTrnWrkFullvoidGp)) {
			    		jrL2Msg.setField("YD_CAR_PROG_STAT", "1");
			    		jrL2Msg.setField("YD_EQP_WRK_STAT" , "L");
			    		sMsg = "공차출발시 시 저장위치 제원 야드L2로 전송";
			    	} else {
			    		jrL2Msg.setField("YD_CAR_PROG_STAT", "A");
			    		jrL2Msg.setField("YD_EQP_WRK_STAT" , "L");
			    		sMsg = "영차출발시 시 저장위치 제원 야드L2로 전송";
			    	}
			    	
			    	jrL2Msg.setField("YD_CAR_ARRSTRT_STAT" , "S"        ); //A:도착, S:출발
			    	jrL2Msg.setField("TRN_EQP_CD"  	       , sTrnEqpCd  );
			    	jrL2Msg.setField("YD_CAR_USE_GP"       , "L"        );//구내운송

					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L001_CarInfo", jrL2Msg));
					
					commUtils.printLog(logId, sMsg, "SL");
					
					/********************************************************
					 * 이송차량 영차/공차 출발시 해당 위치대기차량 입동지시 요구
					 ********************************************************/
					/*
					SELECT A.*
					     ,(SELECT YD_CARPNT_CD FROM TB_YD_CARPOINT         
					        WHERE YD_GP = 'J'
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
					           AND SPOS_WLOC_CD IN ('DJY21', 'DJY22', 'DJY1E') 
					           AND YD_CARLD_STOP_LOC = :V_YD_STK_COL_GP
					         UNION ALL  
					        SELECT A.*
					             , 'F' AS TRN_WRK_FULLVOID_GP
					             , ARR_WLOC_CD AS WLOC_CD
					          FROM TB_YD_CARSCH A
					         WHERE DEL_YN = 'N'
					           AND YD_CAR_PROG_STAT = 'A' 
					           AND ARR_WLOC_CD  IN ('DJY21', 'DJY22', 'DJY1E')
					           AND YD_CARUD_STOP_LOC = :V_YD_STK_COL_GP
					       ) A
					 ORDER BY YD_BAYIN_WO_SEQ  
					        , YD_CAR_SCH_ID
					 */
			    	JDTORecordSet jsCarSchNext = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getTsNextTrnEqp", logId, mthdNm, "차량스케줄 조회");
			    	if (jsCarSchNext.size() > 0) {
			    		
			    		String ydCarUseGp = jsCarSchNext.getRecord(0).getFieldString("YD_CAR_USE_GP"); // G출하 L구내운송
			    		
			    		String sApp838 = coilDao.ApplyYn(logId, mthdNm, "APP838", "J", "*"); //입동지시 요구 전문송신
			    		
			    			if ("L".equals(ydCarUseGp)) {
				    			String sTrnEqpCdNext         = jsCarSchNext.getRecord(0).getFieldString("TRN_EQP_CD");
					    		String sTrnWrkFullvoidGpNext = jsCarSchNext.getRecord(0).getFieldString("TRN_WRK_FULLVOID_GP");
					    		String sWlocCdNext           = jsCarSchNext.getRecord(0).getFieldString("WLOC_CD");
					    		
					    		jrParam = commUtils.getParam(logId, mthdNm, sModifier);
								jrParam.setField("TRN_EQP_CD"			, sTrnEqpCdNext        );	
								jrParam.setField("WLOC_CD"				, sWlocCdNext          );	
								jrParam.setField("TRN_WRK_FULLVOID_GP"	, sTrnWrkFullvoidGpNext);	
								jrParam.setField("PNT_DMD_DT"			, sCurrDate  );			
								
								JDTORecord jrYdMsg = this.rcvTSYDJ002(jrParam);
								jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				    		} 
				    		
				    		if ("G".equals(ydCarUseGp)) {

				    			String ydCarPntCd = jsCarSchNext.getRecord(0).getFieldString("YD_CARPNT_CD");
				    			
				    			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
								jrParam.setField("JMS_TC_CD"		 , "YDYDJ553");    
								jrParam.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
								jrParam.setField("YD_CARPNT_CD"	     , ydCarPntCd);	// 입동포인트
								
    	    					if ("Y".equals(sApp838)) {
    	    						jrRtn = commUtils.addSndData(jrRtn, jrParam);
    	    					} else {
    								JDTORecord jrCarBayInOrdReq = this.rcvYDYDJ553(jrParam);
    								jrRtn = commUtils.addSndData(jrRtn, jrCarBayInOrdReq);	
    	    					}
				    		}	
			    	}
				
				} else {
					sMsg = "출발야드의 적치열["+ydCarldStopLoc+"]의 운송장비코드["+sColTrnEqpCd+"]와 전문의 운송장비코드["+sTrnEqpCd+"]가 다르므로 맵 Clear 안함 ";
					commUtils.printLog(logId, sMsg , "SL");
				}

	    	} else if ( jsStkCol.size() == 0 ) {
	    		
				if (bIsCarSchYN) {
					
					//해당야드로 출발을 한 후 대기장에 도착 후 대기장에서 출발인 경우에는 업무종료 처리
					sMsg = "발지개소코드["+sSposWlocCd+"], 발지포인트코드["+sSposYdPntCd+"] 확인 처리 ";
					commUtils.printLog(logId, sMsg , "SL");
					
					if ( sCarSchSposWlocCd.equals(sSposWlocCd) && sSposYdPntCd.equals(CConstant.YD_WAIT_PNT_CD) ) { //대기장포인트코드
						//발지개소코드가 이미존재하는 차량스케줄의 발지개소코드와 같고 발지포인트코드가 대기장 포인트코드인 경우에는 업무종료처리
						sMsg= "발지개소코드["+sSposWlocCd+"]가 이미존재하는 차량스케줄의 발지개소코드["+sCarSchSposWlocCd+"]와 같고 발지포인트코드["+sSposYdPntCd+"]가 대기장 포인트코드인 경우에는 업무종료처리합니다.";
						commUtils.printLog(logId, sMsg , "S-");
						return jrRtn;
					}
				}
	    	}
	    	
	    	/********************************************
	    	 * 7. 공 영차시 처리 로직
	    	 ********************************************/
	    	if ("E".equals(sTrnWrkFullvoidGp)) { //공차
	    		commUtils.printLog(logId, "7.1. 공차 처리", "SL");
	    		
				//상차LOT편성 및 작업예약등록시 파라미터값
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				
				String ydCarSchIdNew   = coilDao.getSeqId(logId, mthdNm, "CarSch");
	    		String sFrtoMoveWordNo = coilDao.getSeqId(logId, mthdNm, "FtMvWo"); // 이송작업지시일자
	    		
	    		jrParam.setField("YD_CAR_SCH_ID"          , ydCarSchIdNew);
				jrParam.setField("YD_CAR_USE_GP"          , "L"                      );
		    	jrParam.setField("YD_EQP_ID"              , "XXPT01"                 );
		    	jrParam.setField("WLOC_CD"                , sArrWlocCd               );
		    	jrParam.setField("PNT_DMD_DT"             , sCurrDate                );
	    		jrParam.setField("SP_TRUCK_LOADING_LOC_TP", "S"                      ); //운송작업영공구분코드
//	    		jrParam.setField("YD_CARLD_LEV_LOC"       , ydCarldStopLoc           ); //야드상차출발위치
	    		jrParam.setField("TRN_EQP_STK_CAPA"       , sTrnEqpStkCapa           );
				jrParam.setField("YD_EQP_WRK_STAT"        , "U"                      );
				jrParam.setField("CAR_KIND"               , "PT"                     );
				jrParam.setField("YD_CAR_PROG_STAT"       , "1"                      ); //상차출발상태
				jrParam.setField("YD_CARLD_LEV_DT"        , sCurrDate                ); //상차출발일시
				jrParam.setField("FRTOMOVE_WORD_NO"       , sFrtoMoveWordNo          );
				jrParam.setField("YD_BAYIN_WO_SEQ"        , CConstant.YD_BAYIN_WO_SEQ_DEFAULT);
				jrParam.setField("YD_MTL_ITEM"            , "CM"                     );// 재료품목 CM : 소재 CG :제품
				
				/**********************************************************
				* 작업대상 재료 조회 - YDTSJ011의 YD_BAY_GP 세팅을 위한조회
				**********************************************************/
				jrParam.setField("SPOS_WLOC_CD"		      , sArrWlocCd               );
				jrParam.setField("TRN_EQP_CD"             , sTrnEqpCd                );
				
				JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStockYdStkLyrMatlLotC", logId, mthdNm, "작업대상 조회");
				
				if (jsStock.size() <= 0) {//재료 없으면 리턴
					commUtils.printLog(logId, "이송대상이 없습니다.", "SL");
					commUtils.printLog(logId, mthdNm, "S-");
					return jrRtn;
				}

				String sCarColGp  = commUtils.nvl(jsStock.getRecord(0).getFieldString("CAR_COL_GP"), "JXPT01"); //상차도 ex)JAPT01
				
				
				jrParam.setField("CAR_COL_GP"             , sCarColGp    );// 상차도 ex)JAPT01

				/***********************************************************
				 * PT 팔레트 차량 공차 출발시 적치 코일 기준 도착창고코드(ARR_WLOC_CD) 세팅
				 * - APP001/028 플래그 Y + 차량장비코드에 PT 포함 시에만 세팅
				 * - 적치 코일의 이송작업지시(TB_PT_STLFRTOMOVE) 기준 ARR_WLOC_CD 조회
				 * - 일반 차량 또는 플래그 N 시: ARR_WLOC_CD 세팅 안 함
				 ***********************************************************/
				String sAPP001_028_E_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "028");
				commUtils.printLog(logId, "CICD 2026.05.13 PT 팔레트 차량 공차 출발시 ARR_WLOC_CD 세팅 APP001/028=[" + sAPP001_028_E_YN + "]", "SL");
				if ("Y".equals(sAPP001_028_E_YN)) {
					boolean bIsPtCarIns = sTrnEqpCd != null && sTrnEqpCd.indexOf("PT") >= 0;
					commUtils.printLog(logId, "PT 팔레트 차량 여부 : " + (bIsPtCarIns ? "Y" : "N"), "SL");
					if (bIsPtCarIns) {
						commUtils.printLog(logId, "PT 팔레트 차량 - 적치 코일 기준 ARR_WLOC_CD 조회 후 세팅", "SL");
						JDTORecord jrParamArrWloc = commUtils.getParam(logId, mthdNm, sModifier);
						jrParamArrWloc.setField("TRN_EQP_CD", sTrnEqpCd);
						JDTORecordSet jsArrWloc = commDao.select(jrParamArrWloc, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getArrWlocCdByPtCar", logId, mthdNm, "PT 이송작업 ARR_WLOC_CD 세팅");
						String sArrWlocCdIns = sArrWlocCd; // 기본값: TSYDJ004 입력값 도착창고코드
						if (jsArrWloc.size() > 0) {
							sArrWlocCdIns = commUtils.nvl(jsArrWloc.getRecord(0).getFieldString("ARR_WLOC_CD"), sArrWlocCd);
							commUtils.printLog(logId, "PT 차량 ARR_WLOC_CD 세팅 결과 [" + sArrWlocCdIns + "]", "SL");
						} else {
							commUtils.printLog(logId, "PT 차량 적치 코일 미발견 - sArrWlocCd 기본값 [" + sArrWlocCd + "] 이용", "SL");
						}
						jrParam.setField("ARR_WLOC_CD", sArrWlocCdIns); // 도착창고코드 세팅
					}
				}

				// 차량스케줄 등록
				commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insYdCarsch", logId, mthdNm, "차량스케줄 생성");
				
				/***********************************************
		    	 * 소재차량Point지시 YDTSJ011
		    	 ***********************************************/
		    	JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
		    	jrYdMsg.setResultCode(logId);	//Logging 을 위한 ID
		    	jrYdMsg.setResultMsg(mthdNm);	//상위 Method 명
		    	
		    	jrYdMsg.setField("JMS_TC_CD"          , "YDTSJ011");
		    	jrYdMsg.setField("JMS_TC_CREATE_DDTT" , sCurrDate );
				jrYdMsg.setField("TRN_EQP_CD"         , sTrnEqpCd );
				jrYdMsg.setField("WLOC_CD"            , sArrWlocCd);
				jrYdMsg.setField("YD_PNT_CD"          , "0000"    );
				jrYdMsg.setField("PNT_WO_GP"          , "A"       );
				jrYdMsg.setField("PNT_WO_DT"          , sCurrDate );
				jrYdMsg.setField("YD_BAY_GP"          , sCarColGp.substring(1, 2));
				
				/**********************************************************
				 * 2열연 제품 2통로, 핫슬라브이송, PT 차량 여부 판단
				 *    - 차량코드에 'PT'가 포함된 경우 핫슬라브 이송 차량으로 처리
				 *    - PT 차량은 출발 즉시 도착 가능한 포인트 전송
				 *    - 활성화된 APP001/028 플래그 확인 후 PT 차량인 경우 적용
				 *    - jsStock에서 YD_PNT_CD 읽어서 YDTSJ011에 세팅
				 *********************************************************/
				commUtils.printLog(logId, "CICD 2026.05.12 PT 핫슬라브 차량 공차 출발시 실제 포인트 세팅", "SL");
				sAPP001_028_E_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "028");
				if ("Y".equals(sAPP001_028_E_YN)) {
					boolean bIsPtCarE = sTrnEqpCd != null && sTrnEqpCd.indexOf("PT") >= 0;
					commUtils.printLog(logId, "핫슬라브 팔레트 PT차량  여부 : " + (bIsPtCarE ? "Y (핫슬라브 이송차량)" : "N (일반차량)"), "SL");
					if (bIsPtCarE) {
						String sYdPntCdE = "0000";     // default
						if (jsStock.size() > 0) {
							sYdPntCdE = commUtils.nvl(jsStock.getRecord(0).getFieldString("YD_PNT_CD"), "0000");
						}
						jrYdMsg.setField("YD_PNT_CD", sYdPntCdE);
					}
				}
				
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
	    	}
	    	
	    	if ("F".equals(sTrnWrkFullvoidGp)) { //영차
	    		commUtils.printLog(logId, "7.2. 영차 처리", "SL");
	    		if (jsCarSch.size() <= 0) {
					throw new DAOException("운송장비코드["+sTrnEqpCd+"]로 차량스케줄 조회 Error");
				}
	    		
	    		commUtils.printLog(logId, "7.2.1 하차재료 조회", "SL");
	    		jrParam.setField("SP_TRUCK_LOADING_LOC_TP", "H"             );
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
	    		            , B.YD_AIM_YD_GP) AS YD_AIM_YD_GP
	    		       --//Tong 크레인 사용 금지재 -> A동 입고
	    		     , (CASE WHEN C.ARR_WLOC_CD = 'DHY21'
	    		              AND (SELECT 'Y' FROM USRYDA.VW_YD_SLABHSM C WHERE C.HSM_GP = 'X' AND C.SLAB_NO=B.STL_NO) = 'Y'
	    		                  THEN 'A'
	    		             WHEN C.ARR_WLOC_CD = 'DHY21' AND B.YD_AIM_YD_GP = 'A' AND MAX(TEMP9) OVER(PARTITION BY A.YD_CAR_SCH_ID) = 'Y'
	    		                  THEN 'B'
	    		             WHEN C.REGISTER = 'runTsRetHt'
	    		                  THEN 'A' --//반송인 경우 A동 하차
	    		             WHEN C.ARR_WLOC_CD = 'C3S01'
	    		                  THEN 'A' --//C3#스카핑 A동 하차
	    		             WHEN C.YD_CAR_USE_GP = 'L' AND C.ARR_WLOC_CD = 'DJY22' AND C.YD_CAR_PROG_STAT IN ('A', 'B')
	    		                  THEN NVL(SUBSTR(YD_CARUD_STOP_LOC, 2, 1), B.YD_AIM_BAY_GP) -- 구내운송 하차 도착위치로 작업예약 생성
	    		        ELSE B.YD_AIM_BAY_GP END
	    		       ) AS YD_AIM_BAY_GP
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
	    		JDTORecordSet jsCarMtl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarftmvmtlID", logId, mthdNm, "이송재료 조회");
	    		
	    		if ( jsCarMtl.size() <= 0 ) {
					throw new DAOException("이송재료 조회 Error");
				}
	    		
	    		String sStlNo = "";
	    		for (int i = 1; i <= jsCarMtl.size(); ++i) {
	    			
	    			jsCarMtl.absolute(i);
	    			sStlNo = jsCarMtl.getRecord().getFieldString("STL_NO");
	    			
	    			/*********************************************************
	    			 * 코일 저장품 등록
	    			 *********************************************************/
	    			commUtils.printLog(logId, "7.2.2 저장품 등록", "SL");
	    			coilDao.stockProcCom(logId, mthdNm, sStlNo, 1);

	    		} //end for

	    		/*****************************************
	    		 * 착지가 2열연 일경우 하차출발(A)로 변경
	    		 *****************************************/
	    		if (this.getYdLocationInfo(sArrWlocCd)) {
	    			commUtils.printLog(logId, "7.2.4 착지 2열연 상태코드 변경(A 하차출발)", "SL");
	    			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("YD_CAR_SCH_ID"		, ydCarSchId);
					jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, ""        );
					jrParam.setField("YD_CAR_PROG_STAT"		, "A"       ); //하차 출발
					jrParam.setField("YD_CARUD_LEV_DT"		, sCurrDate ); //하차출발일시
					
					/* 
					UPDATE TB_YD_CARSCH
					   SET MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					     , YD_CARUD_WRK_BOOK_ID = :V_YD_CARUD_WRK_BOOK_ID
					     , YD_CAR_PROG_STAT = :V_YD_CAR_PROG_STAT
					     , YD_CARUD_LEV_DT  = :V_YD_CARUD_LEV_DT
					 WHERE YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
					*/ 
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarsch", logId, mthdNm, "차량스케쥴수정");
					
					/*****************************************
		    		 * 소재차량도착Point요구 - 영차
		    		 *****************************************/
					commUtils.printLog(logId, "8. 소재차량도착 point요구", "SL");
					String ydStkColGp    = "JXPT03"; // 하차시 하차포인트가 없을 때를 위한 default 값
					String sEtrHouWaiSeq = "99";     // 대기차량 default
	    			/* 
					SELECT D.YD_STK_COL_GP 
					     , D.WLOC_CD
					     , D.YD_PNT_CD
					     ,(SELECT NVL(CAR_CNT, '99') AS ETR_HOU_WAI_SEQ
					         FROM USRYDA.VW_YD_CARPOINT   VC
					            , USRYDA.TB_YD_CARPOINT   TC
					        WHERE VC.YD_GP = 'J' 
					          AND VC.YD_CARPNT_CD = TC.YD_CARPNT_CD
					          AND TC.YD_BAY_GP    = SUBSTR(D.YD_STK_COL_GP, 2, 1)
					          AND TC.YD_CAR_USETYPE_GP IN ('TO','GT')
					          AND ROWNUM = 1
					      ) AS ETR_HOU_WAI_SEQ
					  FROM USRYDA.TB_YD_CARFTMVMTL   A
					     , USRTSA.TB_TS_MATL_FTMV_WO B
					     , USRYDA.TB_YD_STKCOL       D
					     , USRYDA.TB_YD_STOCK        ST     
					 WHERE A.STL_NO          = B.STL_NO
					   AND D.WLOC_CD         = B.ARR_WLOC_CD
					   AND B.TRN_WRK_MTL_GP  = 'H' --소재
					   AND A.YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
					   AND D.YD_STKBED_USG_CD IN('GT', 'TO')
					   AND B.TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO)
					                              FROM USRTSA.TB_TS_MATL_FTMV_WO C
					                             WHERE B.STL_NO         = C.STL_NO
					                               AND C.TRN_WRK_MTL_GP = 'H'
					                            )
					   AND D.DEL_YN          = 'N'	
					   AND A.STL_NO          = ST.STL_NO
					   AND ST.YD_AIM_BAY_GP  = D.YD_BAY_GP
					 GROUP BY D.YD_STK_COL_GP 
					        , D.WLOC_CD
					        , D.YD_PNT_CD
					 ORDER BY CASE WHEN SUBSTR(D.YD_STK_COL_GP,6,1) = '3'        THEN 1
					               WHEN SUBSTR(D.YD_STK_COL_GP,6,1) IN ('2','1') THEN 2
					               WHEN SUBSTR(D.YD_STK_COL_GP,6,1) IN ('4','5') THEN 3
					               ELSE 4 END                             
					 */
					JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getListloadStoppointGDh", logId, mthdNm, "차량포인트 조회");
					if (jsRst.size() > 0) {
			    		ydStkColGp    = commUtils.nvl(jsRst.getRecord(0).getFieldString("YD_STK_COL_GP"  ), "JXPT03");
			    		sEtrHouWaiSeq = commUtils.nvl(jsRst.getRecord(0).getFieldString("ETR_HOU_WAI_SEQ"), "99");
					} 
					
					/***********************************************
			    	 * 소재차량Point지시 YDTSJ011
			    	 ***********************************************/
			    	JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			    	jrYdMsg.setResultCode(logId);	//Logging 을 위한 ID
			    	jrYdMsg.setResultMsg(mthdNm);	//상위 Method 명
			    	
			    	jrYdMsg.setField("JMS_TC_CD"          , "YDTSJ011");
			    	jrYdMsg.setField("JMS_TC_CREATE_DDTT" , sCurrDate );
					jrYdMsg.setField("TRN_EQP_CD"         , sTrnEqpCd );
					jrYdMsg.setField("WLOC_CD"            , sArrWlocCd);
					jrYdMsg.setField("YD_PNT_CD"          , "0000"    );
					jrYdMsg.setField("PNT_WO_GP"          , "A"       );
					jrYdMsg.setField("PNT_WO_DT"          , sCurrDate );
					jrYdMsg.setField("YD_BAY_GP"          , ydStkColGp.substring(1, 2));
					jrYdMsg.setField("ETR_HOU_WAI_SEQ"    , sEtrHouWaiSeq);
					
					/**********************************************************
					 * 2열연 제품 2통로, 핫슬라브이송, PT 차량 여부 판단
					 *    - 차량코드에 'PT'가 포함된 경우 핫슬라브 이송 차량으로 처리
					 *    - PT 차량은 출발 즉시 도착 가능한 포인트 전송
					 **********************************************************/					
					commUtils.printLog(logId, "CICD 2026.05.06 PT 차량 영차 출발 처리 (핫슬라브 이송)", "SL");
					String sAPP001_028_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "028");
					if ("Y".equals(sAPP001_028_YN)) {
						boolean bIsPtCar = sTrnEqpCd != null && sTrnEqpCd.indexOf("PT") >= 0;
						commUtils.printLog(logId, "3. 핫슬라브 팔레트 PT차량  여부 : " + (bIsPtCar ? "Y (핫슬라브 이송차량)" : "N (일반차량)"), "SL");
						if (bIsPtCar) {
							String sYdPntCd = "0000";     // default
							if (jsRst.size() > 0) {
								sYdPntCd    = commUtils.nvl(jsRst.getRecord(0).getFieldString("YD_PNT_CD"  ), "0000");
							} 
							jrYdMsg.setField("YD_PNT_CD"          , sYdPntCd    );
						}
					}
					
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
	 *      [A] 오퍼레이션명 : 구내운송 목표동 조회
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String getMatlCarAimBayGp(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "소재이송 차량 목표동 조회[CCoilCarMvSeEJB.getMatlCarAimBayGp] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		String ydAimBayGp = "X";
		try {
			commUtils.printLog(logId, mthdNm, "S+"); 

			String sTrnEqpCd         = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"         )); //운송장비코드
			String sSposWlocCd       = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"       )); //발지개소코드
			String sTrnWrkFullvoidGp = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분 E:공차 F:영차
			String ydCarSchId        = commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"      ));
			
			String sModifier         = commUtils.trim(rcvMsg.getFieldString("MODIFIER"           )); //수정자(Backup Only)
			
			JDTORecord jrParam       = commUtils.getParam(logId, mthdNm, sModifier);

			String ydStkColGp = "";
			
			jrParam.setField("YD_CAR_SCH_ID"	, ydCarSchId);
			
			jrParam.setField("SPOS_WLOC_CD"		, sSposWlocCd);
			jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd);
			
			if ("".equals(sTrnWrkFullvoidGp)) {
				commUtils.printLog(logId, "운송작업영공구분 항목값이 없습니다.", "S-");
				return ydAimBayGp;
			}
			
			if ("E".equals(sTrnWrkFullvoidGp)) {
				/*
				WITH TEMP_TABLE AS (
				SELECT AA.*
				  FROM (
				        SELECT 
				               DECODE(A.MTL_UGNT_GP,'Y','1','N','2')       AS MTL_UGNT_SEQ--긴급재순위
				             , (CASE WHEN D7.CNT=0 THEN 0 ELSE D7.CNT END) AS CAR_POINT --차량갯수
				             , D1.NEXT_PROC                          --AS 구내차공정우선순위
				             , DECODE(A.GP,'CG',' ',D3.SCH_RULE_VAL) AS YD_NEXT_PROC--야드차공정우선순위
				             , A.YD_STK_COL_GP
				             , A.YD_STK_BED_NO
				             , A.YD_STK_LYR_NO
				             , A.GP                          AS YD_MTL_ITEM
				             , A.CUR_DONG                    --AS 제품현재동
				             , A.CG_AIM                      --AS 제품목표동
				             , A.CM_AIM                      -- 소재차공정
				             , A.STL_NO             , A.SPOS_WLOC_CD     -- 발지개소
				
				             , A.ARR_WLOC_CD      -- 착지개소
				             , A.HCR_GP           -- HCR구분
				             , A.YD_MTL_WT           -- 중량
				             , A.COIL_W  AS YD_MTL_W
				             , A.YD_AIM_RT_GP
				             , A.YD_AIM_YD_GP
				             , A.YD_AIM_BAY_GP 
				             , SUBSTR(A.YD_STK_COL_GP,1,1) AS YD_GP
				             , SUBSTR(A.YD_STK_COL_GP,2,1) AS YD_BAY_GP
				             , SUBSTR(A.YD_STK_COL_GP,3,2) AS YD_EQP_GP
				
				          FROM (
				                SELECT DECODE(C.STL_APPEAR_GP,'Y','CG','CM') AS GP
				                     , SUBSTR(A.YD_STK_COL_GP,1,1),SUBSTR(A.YD_STK_COL_GP,2,1)
				                     , DECODE(C.STL_APPEAR_GP,'Y',SUBSTR(B.ARR_YD_PNT_CD,2,1),'X') AS CG_AIM
				                     , DECODE(C.STL_APPEAR_GP,'Y','XX',C.NEXT_PROC) AS CM_AIM
				                     , SUBSTR(A.YD_STK_COL_GP,2,1) AS CUR_DONG
				                     , C.NEXT_PROC
				                     , B.TRN_WRK_MTL_GP
				                     , A.STL_NO
				                     , A.YD_STK_COL_GP
				                     , A.YD_STK_BED_NO
				                     , A.YD_STK_LYR_NO
				                     , B.SPOS_WLOC_CD      -- 발지개소
				                     , B.ARR_WLOC_CD       -- 착지개소
				                     , B.MTL_UGNT_GP       -- 긴급재구분
				                     , B.HCR_GP             -- HCR구분
				                     , C.COIL_WT       AS YD_MTL_WT       --중량
				                     , C.COIL_W
				                     , D.YD_AIM_RT_GP
				                     , D.YD_MTL_ITEM
				                     , D.YD_AIM_YD_GP
				                     , D.YD_AIM_BAY_GP
				                  FROM TB_YD_STKLYR       A
				                     , TB_TS_MATL_FTMV_WO B
				                     , TB_PT_COILCOMM     C
				                     , TB_YD_STOCK        D
				                 WHERE A.STL_NO  = B.STL_NO
				                   AND A.STL_NO  = C.COIL_NO
				                   AND C.COIL_NO = D.STL_NO
				                   AND A.YD_STK_LYR_MTL_STAT = 'C'
				                   AND C.CURR_PROG_CD LIKE (CASE WHEN B.TRN_WRK_MTL_GP = 'H' THEN 'E' ELSE '%' END)
				                   AND NOT EXISTS (SELECT STL_NO 
				                                     FROM TB_YD_WRKBOOKMTL K
				                                    WHERE K.STL_NO = A.STL_NO
				                                      AND DEL_YN   = 'N')
				                   AND ((SUBSTR(A.YD_STK_COL_GP,3,2) <> 'PT') AND (SUBSTR(A.YD_STK_COL_GP,3,2) <> 'TR'))
				                   AND B.SPOS_WLOC_CD  =    :V_SPOS_WLOC_CD
				                   AND B.TS_MATL_FTMV_STAT_GP     = '1'
				                   AND B.MATL_FTMV_WO_NML_HD_YN   = 'Y'
				                   AND A.YD_STK_COL_GP NOT LIKE '__CD__' --CR->CD 크래들롤
				                   AND B.TRN_WRK_MTL_GP LIKE ( SELECT NVL(TRN_WRK_MTL_GP,'H')
				                                                 FROM TB_TS_TRN_EQP_OPRN_PLN
				                                                WHERE SYSDATE BETWEEN CARASGN_ST_DT AND CARASGN_END_DT
				                                                  AND TRN_EQP_CD = :V_TRN_EQP_CD -- 'GTR26068' --'GTR21143'
				                                                  AND DEL_YN  ='N'
				                                                  AND ROWNUM <= 1
				                                             ) ||'%'
				                   AND A.DEL_YN = 'N'
				                   AND B.DEL_YN = 'N'
				               ) A 
				             , (
				                SELECT NEXT_PROC 
				                  FROM TB_TS_TRN_EQP_OPRN_PLN
				                 WHERE SYSDATE BETWEEN CARASGN_ST_DT AND CARASGN_END_DT
				                   AND TRN_EQP_CD =:V_TRN_EQP_CD -- 'GTR26068' --'GTR21143'
				                   AND DEL_YN='N'
				                   AND ROWNUM<=1
				               ) D1 --구내차공정우선순위
				             , (
				                SELECT SCH_RULE_VAL
				                     , SCH_CD 
				                  FROM TB_YM_STACKPRIORITY
				                 WHERE RULE_ID = 'YM08'
				                 UNION ALL
				                SELECT '1' 
				                     , 'XX' AS SCH_CD
				                  FROM DUAL
				               ) D3 -- 야드차공정우선순위
				             , (
				                SELECT SUBSTR(YD_STK_COL_GP,'2','1') AS DONG 
				                     , NVL(COUNT(CAR_NO),0)+ NVL(COUNT(TRN_EQP_CD),0) AS CNT
				                  FROM TB_YD_STKCOL 
				                 WHERE WLOC_CD = :V_SPOS_WLOC_CD
				                   AND YD_STK_COL_GP Like '__PT%'
				                 GROUP BY SUBSTR(YD_STK_COL_GP,'2','1')
				               ) D7 -- 차량위치체크
				         WHERE A.CM_AIM    = D3.SCH_CD
				           AND A.NEXT_PROC = D1.NEXT_PROC(+) 
				           AND A.CUR_DONG  = D7.DONG(+) 
				       ) AA
				 WHERE 1 = 1
				)
				, TEMP_TABLE2 AS (
				SELECT *
				  FROM TEMP_TABLE AA
				 WHERE AA.CAR_POINT IS NOT NULL
				 ORDER BY AA.NEXT_PROC
				       , AA.YD_STK_LYR_NO DESC
				       , AA.MTL_UGNT_SEQ
				       , AA.CAR_POINT
				       , (CASE WHEN YD_MTL_ITEM = 'CM' THEN 1 ELSE LENGTH(AA.STL_NO) END) 
				       , AA.YD_NEXT_PROC
				       , AA.YD_STK_COL_GP
				       , AA.YD_STK_BED_NO
				       , AA.YD_MTL_W
				) 
				SELECT AA.*
				     , NVL(BB.TONG, 3) AS TONG
				     , BB.YD_STK_COL_GP AS CAR_COL_GP
				
				  FROM TEMP_TABLE2 AA
				     , (
				        SELECT CASE WHEN SUBSTR(C.YD_STK_COL_GP,6,1) = '3'        THEN 3
				                    WHEN SUBSTR(C.YD_STK_COL_GP,6,1) IN ('2','1') THEN 1
				                    WHEN SUBSTR(C.YD_STK_COL_GP,6,1) IN ('4','5') THEN 2
				                    ELSE 3 END TONG
				             , C.YD_STK_COL_GP
				          FROM (SELECT A.YD_STK_COL_GP
				                     , (SELECT COUNT(*)
				                          FROM TB_YD_CARSCH CS
				                         WHERE CS.DEL_YN = 'N'
				                           AND CS.YD_CARLD_STOP_LOC = B.YD_STK_COL_GP
				                       ) AS CARLD_RANK
				                  FROM TB_YD_CARPOINT A
				                     , TB_YD_STKCOL   B  
				                 WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP   
				                   AND SUBSTR(A.YD_STK_COL_GP, 1, 1) = 'J'
				                   AND A.YD_STK_COL_ACT_STAT <> 'N'
				                   AND A.YD_CAR_USETYPE_GP IN ('TO', 'GT')
				                   AND B.YD_LOC_GP  = CASE WHEN (SELECT ITEM1 FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP002') = 'Y' THEN B.YD_LOC_GP
				                                           ELSE 'H' END
				                   AND A.DEL_YN = 'N'
				                   AND B.DEL_YN = 'N'
				                   AND A.WLOC_CD = :V_SPOS_WLOC_CD
				                 --통로 ORDER BY : 
				                 -- 1. 3통로가 열려 있으면 무조건 3통로    
				                 -- 2. 2통로가 열려 있으면 2통로 우선    
				                 -- 3. 1통로가 열려 있으면 1통로 우선    
				                 ORDER BY CASE WHEN SUBSTR(A.YD_STK_COL_GP,6,1) = '3'        THEN 1
				                               WHEN SUBSTR(A.YD_STK_COL_GP,6,1) IN ('2','1') THEN 2
				                               WHEN SUBSTR(A.YD_STK_COL_GP,6,1) IN ('4','5') THEN 3
				                               ELSE 4 END 
				                        , CARLD_RANK
				                 ) C 
				               , TEMP_TABLE2 AA
				           WHERE C.YD_STK_COL_GP LIKE SUBSTR(AA.YD_STK_COL_GP,1,2) ||'PT%'
				             AND ROWNUM = 1   
				       ) BB
				 WHERE SUBSTR(AA.YD_STK_COL_GP, 1, 2) = SUBSTR(BB.YD_STK_COL_GP(+) , 1, 2)
				   AND (CM_AIM, SUBSTR(AA.YD_STK_COL_GP,1,2)) = (SELECT CM_AIM, SUBSTR(YD_STK_COL_GP,1,2) 
				                                                   FROM TEMP_TABLE2
				                                                  WHERE ROWNUM <= 1 )
				   AND ROWNUM <= (SELECT ITEM1        --default 작업매수
				                    FROM TB_YD_RULE
				                   WHERE REPR_CD_GP = 'APP802'
				                     AND CD_GP      = 'J'
				                  )
				 ORDER BY AA.NEXT_PROC
				        , AA.MTL_UGNT_SEQ
				        , AA.CAR_POINT
				        , AA.YD_STK_LYR_NO DESC
				        , (CASE WHEN YD_MTL_ITEM = 'CM' THEN 1 ELSE LENGTH(AA.STL_NO) END)  
				        , AA.YD_NEXT_PROC
				        , AA.YD_STK_COL_GP
				        , AA.YD_STK_BED_NO
				        , AA.YD_MTL_W
				 */
				JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStockYdStkLyrMatlLotC", logId, mthdNm, "작업대상 조회");
				
				if (jsStock.size() > 0) {//재료 없으면 리턴
					ydStkColGp = commUtils.nvl(jsStock.getRecord(0).getFieldString("CAR_COL_GP"), "JXPT03"); //상차도 ex)JAPT01
				}
			}
			
			if ("F".equals(sTrnWrkFullvoidGp)) {
    			/* 
				SELECT D.YD_STK_COL_GP 
				     , D.WLOC_CD
				     , D.YD_PNT_CD
				  FROM USRYDA.TB_YD_CARFTMVMTL   A
				     , USRTSA.TB_TS_MATL_FTMV_WO B
				     , USRYDA.TB_YD_STKCOL       D
				     , USRYDA.TB_YD_STOCK        ST     
				 WHERE A.STL_NO          = B.STL_NO
				   AND D.WLOC_CD         = B.ARR_WLOC_CD
				   AND D.YD_LOC_GP       = 'H'
				   AND B.TRN_WRK_MTL_GP  = 'H' --소재
				   AND A.YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
				   AND D.YD_STKBED_USG_CD IN('GT', 'TO')
				   AND B.TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO)
				                              FROM USRTSA.TB_TS_MATL_FTMV_WO C
				                             WHERE B.STL_NO         = C.STL_NO
				                               AND C.TRN_WRK_MTL_GP = 'H'
				                            )
				   AND D.DEL_YN          = 'N'	
				   AND A.STL_NO          = ST.STL_NO
				   AND ST.YD_AIM_BAY_GP  = D.YD_BAY_GP
				 GROUP BY D.YD_STK_COL_GP 
				        , D.WLOC_CD
				        , D.YD_PNT_CD
				 ORDER BY CASE WHEN SUBSTR(D.YD_STK_COL_GP,6,1) = '3'        THEN 1
				               WHEN SUBSTR(D.YD_STK_COL_GP,6,1) IN ('2','1') THEN 2
				               WHEN SUBSTR(D.YD_STK_COL_GP,6,1) IN ('4','5') THEN 3
				               ELSE 4 END                          
				 */
				JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getListloadStoppointGDh", logId, mthdNm, "차량포인트 조회");
				if (jsRst.size() > 0) {
		    		ydStkColGp = commUtils.nvl(jsRst.getRecord(0).getFieldString("YD_STK_COL_GP"), "JXPT03");
				} 
			}
			ydAimBayGp = ydStkColGp.substring(1, 2);
			
			commUtils.printLog(logId, mthdNm, "S-");
			return ydAimBayGp;
			
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return ydAimBayGp;
		} catch (Exception e) {
			return ydAimBayGp;
		}
	}	
	
	
	/**
	 * [A] 오퍼레이션명 : 소재차량대기장 도착(TSYDJ005)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvTSYDJ005(JDTORecord rcvMsg) throws DAOException  {
		String mthdNm = "소재차량대기장 도착[CCoilCarMvSeEJB.rcvTSYDJ005] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
	    try {
	    	 
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
	    	//수신항목 변수 저장
			String msgId             = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID

//			String sCrPlntGp         = commUtils.trim(rcvMsg.getFieldString("CR_PLNT_GP"         )); // 냉연공장구분
			String sTrnEqpCd         = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"         )); // 운송장비코드
			String sArrWlocCd        = commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD"        )); // 착지개소코드
//			String sArrYdPntCd       = commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD"      )); // 착지야드포인트코드
			String sTrnWrkFullvoidGp = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); // 운송작업영공구분코드
//			String sTrnEqpStkCapa    = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA"   )); // 운송장비적재능력
			String sCarArrDt         = commUtils.trim(rcvMsg.getFieldString("CAR_ARR_DT"         )); // 차량도착일시
//			String sMsgGp            = commUtils.trim(rcvMsg.getFieldString("MSG_GP"             )); // MSG_GP

			String sModifier         = commUtils.trim(rcvMsg.getFieldString("MODIFIER"           )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }	
			
			String sMsg             = "";
			
			String sCurrDate        = commUtils.getDateTime14();
			
			JDTORecord jrParam      = commUtils.getParam(logId, mthdNm, sModifier);
			
	    	/***************************************
	    	 * 운송장비코드로 차량스케줄 조회
	    	 ***************************************/
			commUtils.printLog(logId, "1. 차량스케줄 조회", "SL");
			jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd);
			/*
			SELECT *
			  FROM (
			        SELECT A.*
			              ,(SELECT YD_STKBED_USG_CD
			                  FROM TB_YD_STKCOL B
			                 WHERE B.YD_STK_COL_GP = A.YD_CARLD_STOP_LOC
			                   AND B.YD_STKBED_USG_CD IN ('A','D','E') --수출, 철송, 주문회
			               ) AS NEW_DEST_BAY
			          FROM TB_YD_CARSCH A
			         WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			           AND DEL_YN     ='N'
			         ORDER BY YD_CAR_SCH_ID DESC
			                , YD_CARUD_CMPL_DT DESC
			       ) A
			 WHERE ROWNUM <= 1
			 */
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarschByTrnEqpCd", logId, mthdNm, "차량스케줄 조회");
			if (jsCarSch.size() > 1) {
				commUtils.printLog(logId, "차량스케줄 조회 오류 ["+jsCarSch.size()+"건]", "S-");
				throw new DAOException("스케줄 조회 시 오류발생 - 운송장비코드로 차량스케줄이 여러건["+jsCarSch.size()+"]이 존재합니다");
			}
			
			String ydCarSchId = jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID");
			jrParam.setField("YD_CAR_SCH_ID"		, ydCarSchId);
			
			/**********************************************************
			* 대기장 조회
			**********************************************************/
			String ydBayInPnt = "JXPT03"; //default 값
			
			if ("E".equals(sTrnWrkFullvoidGp)) {
				jrParam.setField("SPOS_WLOC_CD"		, sArrWlocCd);
				
				/*
				WITH TEMP_TABLE AS (
				SELECT AA.*
				  FROM (
				        SELECT 
				               DECODE(A.MTL_UGNT_GP,'Y','1','N','2')       AS MTL_UGNT_SEQ--긴급재순위
				             , (CASE WHEN D7.CNT=0 THEN 0 ELSE D7.CNT END) AS CAR_POINT --차량갯수
				             , D1.NEXT_PROC                          --AS 구내차공정우선순위
				             , DECODE(A.GP,'CG',' ',D3.SCH_RULE_VAL) AS YD_NEXT_PROC--야드차공정우선순위
				             , A.YD_STK_COL_GP
				             , A.YD_STK_BED_NO
				             , A.YD_STK_LYR_NO
				             , A.GP                          AS YD_MTL_ITEM
				             , A.CUR_DONG                    --AS 제품현재동
				             , A.CG_AIM                      --AS 제품목표동
				             , A.CM_AIM                      -- 소재차공정
				             , A.STL_NO             , A.SPOS_WLOC_CD     -- 발지개소
				
				             , A.ARR_WLOC_CD      -- 착지개소
				             , A.HCR_GP           -- HCR구분
				             , A.YD_MTL_WT           -- 중량
				             , A.COIL_W  AS YD_MTL_W
				             , A.YD_AIM_RT_GP
				             , A.YD_AIM_YD_GP
				             , A.YD_AIM_BAY_GP 
				             , SUBSTR(A.YD_STK_COL_GP,1,1) AS YD_GP
				             , SUBSTR(A.YD_STK_COL_GP,2,1) AS YD_BAY_GP
				             , SUBSTR(A.YD_STK_COL_GP,3,2) AS YD_EQP_GP
				
				          FROM (
				                SELECT DECODE(C.STL_APPEAR_GP,'Y','CG','CM') AS GP
				                     , SUBSTR(A.YD_STK_COL_GP,1,1),SUBSTR(A.YD_STK_COL_GP,2,1)
				                     , DECODE(C.STL_APPEAR_GP,'Y',SUBSTR(B.ARR_YD_PNT_CD,2,1),'X') AS CG_AIM
				                     , DECODE(C.STL_APPEAR_GP,'Y','XX',C.NEXT_PROC) AS CM_AIM
				                     , SUBSTR(A.YD_STK_COL_GP,2,1) AS CUR_DONG
				                     , C.NEXT_PROC
				                     , B.TRN_WRK_MTL_GP
				                     , A.STL_NO
				                     , A.YD_STK_COL_GP
				                     , A.YD_STK_BED_NO
				                     , A.YD_STK_LYR_NO
				                     , B.SPOS_WLOC_CD      -- 발지개소
				                     , B.ARR_WLOC_CD       -- 착지개소
				                     , B.MTL_UGNT_GP       -- 긴급재구분
				                     , B.HCR_GP             -- HCR구분
				                     , C.COIL_WT       AS YD_MTL_WT       --중량
				                     , C.COIL_W
				                     , D.YD_AIM_RT_GP
				                     , D.YD_MTL_ITEM
				                     , D.YD_AIM_YD_GP
				                     , D.YD_AIM_BAY_GP
				                  FROM TB_YD_STKLYR       A
				                     , TB_TS_MATL_FTMV_WO B
				                     , TB_PT_COILCOMM     C
				                     , TB_YD_STOCK        D
				                 WHERE A.STL_NO  = B.STL_NO
				                   AND A.STL_NO  = C.COIL_NO
				                   AND C.COIL_NO = D.STL_NO
				                   AND A.YD_STK_LYR_MTL_STAT = 'C'
				                   AND C.CURR_PROG_CD LIKE (CASE WHEN B.TRN_WRK_MTL_GP = 'H' THEN 'E' ELSE '%' END)
				                   AND NOT EXISTS (SELECT STL_NO 
				                                     FROM TB_YD_WRKBOOKMTL K
				                                    WHERE K.STL_NO = A.STL_NO
				                                      AND DEL_YN   = 'N')
				                   AND ((SUBSTR(A.YD_STK_COL_GP,3,2) <> 'PT') AND (SUBSTR(A.YD_STK_COL_GP,3,2) <> 'TR'))
				                   AND B.SPOS_WLOC_CD  =    :V_SPOS_WLOC_CD
				                   AND B.TS_MATL_FTMV_STAT_GP     = '1'
				                   AND B.MATL_FTMV_WO_NML_HD_YN   = 'Y'
				                   AND A.YD_STK_COL_GP NOT LIKE '__CD__' --CR->CD 크래들롤
				                   AND B.TRN_WRK_MTL_GP LIKE ( SELECT NVL(TRN_WRK_MTL_GP,'H')
				                                                 FROM TB_TS_TRN_EQP_OPRN_PLN
				                                                WHERE SYSDATE BETWEEN CARASGN_ST_DT AND CARASGN_END_DT
				                                                  AND TRN_EQP_CD = :V_TRN_EQP_CD -- 'GTR26068' --'GTR21143'
				                                                  AND DEL_YN  ='N'
				                                                  AND ROWNUM <= 1
				                                             ) ||'%'
				                   AND A.DEL_YN = 'N'
				                   AND B.DEL_YN = 'N'
				               ) A 
				             , (
				                SELECT NEXT_PROC 
				                  FROM TB_TS_TRN_EQP_OPRN_PLN
				                 WHERE SYSDATE BETWEEN CARASGN_ST_DT AND CARASGN_END_DT
				                   AND TRN_EQP_CD =:V_TRN_EQP_CD -- 'GTR26068' --'GTR21143'
				                   AND DEL_YN='N'
				                   AND ROWNUM<=1
				               ) D1 --구내차공정우선순위
				             , (
				                SELECT SCH_RULE_VAL
				                     , SCH_CD 
				                  FROM TB_YM_STACKPRIORITY
				                 WHERE RULE_ID = 'YM08'
				                 UNION ALL
				                SELECT '1' 
				                     , 'XX' AS SCH_CD
				                  FROM DUAL
				               ) D3 -- 야드차공정우선순위
				             , (
				                SELECT SUBSTR(YD_STK_COL_GP,'2','1') AS DONG 
				                     , NVL(COUNT(CAR_NO),0)+ NVL(COUNT(TRN_EQP_CD),0) AS CNT
				                  FROM TB_YD_STKCOL 
				                 WHERE WLOC_CD = :V_SPOS_WLOC_CD
				                   AND YD_STK_COL_GP Like '__PT%'
				                 GROUP BY SUBSTR(YD_STK_COL_GP,'2','1')
				               ) D7 -- 차량위치체크
				         WHERE A.CM_AIM    = D3.SCH_CD
				           AND A.NEXT_PROC = D1.NEXT_PROC(+) 
				           AND A.CUR_DONG  = D7.DONG(+) 
				       ) AA
				 WHERE 1 = 1
				)
				, TEMP_TABLE2 AS (
				SELECT *
				  FROM TEMP_TABLE AA
				 WHERE AA.CAR_POINT IS NOT NULL
				 ORDER BY AA.NEXT_PROC
				--       , (CASE WHEN AA.YD_STK_COL_GP LIKE 'HH%' THEN AA.YD_STK_LYR_NO ELSE '0' END) DESC
				       , AA.YD_STK_LYR_NO DESC
				       , AA.MTL_UGNT_SEQ
				       , AA.CAR_POINT
				--       , AA.YD_STK_LYR_NO DESC
				       , (CASE WHEN YD_MTL_ITEM = 'CM' THEN 1 ELSE LENGTH(AA.STL_NO) END) 
				       , AA.YD_NEXT_PROC
				       , AA.YD_STK_COL_GP
				       , AA.YD_STK_BED_NO
				       , AA.YD_MTL_W
				) 
				SELECT AA.*
				     , NVL(BB.TONG, 3) AS TONG
				     , BB.YD_STK_COL_GP AS CAR_COL_GP
				
				  FROM TEMP_TABLE2 AA
				     , (
				        SELECT CASE WHEN SUBSTR(C.YD_STK_COL_GP,6,1) = '3'        THEN 3
				                    WHEN SUBSTR(C.YD_STK_COL_GP,6,1) IN ('2','1') THEN 1
				                    WHEN SUBSTR(C.YD_STK_COL_GP,6,1) IN ('4','5') THEN 2
				                    ELSE 3 END TONG
				             , C.YD_STK_COL_GP
				          FROM (SELECT A.YD_STK_COL_GP
				                     , (SELECT COUNT(*)
				                          FROM TB_YD_CARSCH CS
				                         WHERE CS.DEL_YN = 'N'
				                           AND CS.YD_CARLD_STOP_LOC = B.YD_STK_COL_GP
				                       ) AS CARLD_RANK
				                  FROM TB_YD_CARPOINT A
				                     , TB_YD_STKCOL   B  
				                 WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP   
				                   AND SUBSTR(A.YD_STK_COL_GP, 1, 1) = 'J'
				                   AND A.YD_STK_COL_ACT_STAT <> 'N'
				                   AND A.YD_CAR_USETYPE_GP IN('TO', 'GT')
				--                   AND B.YD_LOC_GP = 'H'
				                   AND B.YD_LOC_GP  = CASE WHEN (SELECT ITEM1 FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP002') = 'Y' THEN B.YD_LOC_GP
				                                           ELSE 'H' END
				                   AND A.DEL_YN = 'N'
				                   AND B.DEL_YN = 'N'
				                 --통로 ORDER BY : 
				                 -- 1. 3통로가 열려 있으면 무조건 3통로    
				                 -- 2. 2통로가 열려 있으면 2통로 우선    
				                 -- 3. 1통로가 열려 있으면 1통로 우선    
				                 ORDER BY CASE WHEN SUBSTR(A.YD_STK_COL_GP,6,1) = '3'        THEN 1
				                               WHEN SUBSTR(A.YD_STK_COL_GP,6,1) IN ('2','1') THEN 2
				                               WHEN SUBSTR(A.YD_STK_COL_GP,6,1) IN ('4','5') THEN 3
				                               ELSE 4 END 
				                        , CARLD_RANK
				                 ) C 
				               , TEMP_TABLE2 AA
				           WHERE C.YD_STK_COL_GP LIKE SUBSTR(AA.YD_STK_COL_GP,1,2) ||'PT%'
				             AND ROWNUM = 1   
				       ) BB
				 WHERE SUBSTR(AA.YD_STK_COL_GP, 1, 2) = SUBSTR(BB.YD_STK_COL_GP(+) , 1, 2)
				   AND (CM_AIM, SUBSTR(AA.YD_STK_COL_GP,1,2)) = (SELECT CM_AIM, SUBSTR(YD_STK_COL_GP,1,2) 
				                                                   FROM TEMP_TABLE2
				                                                  WHERE ROWNUM <= 1 )
				   AND ROWNUM <= (SELECT ITEM1        --default 작업매수
				                    FROM TB_YD_RULE
				                   WHERE REPR_CD_GP = 'APP802'
				                     AND CD_GP      = 'J'
				                  )
				 ORDER BY AA.NEXT_PROC
				        , AA.MTL_UGNT_SEQ
				        , AA.CAR_POINT
				        , AA.YD_STK_LYR_NO DESC
				        , (CASE WHEN YD_MTL_ITEM = 'CM' THEN 1 ELSE LENGTH(AA.STL_NO) END)  
				        , AA.YD_NEXT_PROC
				        , AA.YD_STK_COL_GP
				        , AA.YD_STK_BED_NO
				        , AA.YD_MTL_W
				 */
				JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStockYdStkLyrMatlLotC", logId, mthdNm, "저장품 조회");
				
//				if (jsStock.size() <= 0) {//재료 없으면 리턴
//					sMsg = "이송대상이 없습니다." ;
//					commUtils.printLog(logId, sMsg, "SL");
//					commUtils.printLog(logId, mthdNm, "S-");
//					return jrRtn;
//				}
				
				if (jsStock.size() > 0) {
					ydBayInPnt  = commUtils.nvl(jsStock.getRecord(0).getFieldString("CAR_COL_GP"), "JXPT03"); //상차도 ex)JAPT01
					jrParam.setField("YD_CARLD_STOP_LOC", ydBayInPnt);	
				}
			}
			
			if ("F".equals(sTrnWrkFullvoidGp)) {
    			/* 
				SELECT D.YD_STK_COL_GP 
				     , D.WLOC_CD
				     , D.YD_PNT_CD
				  FROM USRYDA.TB_YD_CARFTMVMTL   A
				     , USRTSA.TB_TS_MATL_FTMV_WO B
				     , USRYDA.TB_YD_STKCOL       D
				     , USRYDA.TB_YD_STOCK        ST     
				 WHERE A.STL_NO          = B.STL_NO
				   AND D.WLOC_CD         = B.ARR_WLOC_CD
				   AND D.YD_LOC_GP       = 'H'
				   AND B.TRN_WRK_MTL_GP  = 'H' --소재
				   AND A.YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
				   AND D.YD_STKBED_USG_CD IN('GT', 'TO')
				   AND B.TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO)
				                              FROM USRTSA.TB_TS_MATL_FTMV_WO C
				                             WHERE B.STL_NO         = C.STL_NO
				                               AND C.TRN_WRK_MTL_GP = 'H'
				                            )
				   AND D.DEL_YN          = 'N'	
				   AND A.STL_NO          = ST.STL_NO
				   AND ST.YD_AIM_BAY_GP  = D.YD_BAY_GP
				 GROUP BY D.YD_STK_COL_GP 
				        , D.WLOC_CD
				        , D.YD_PNT_CD
				 ORDER BY CASE WHEN SUBSTR(D.YD_STK_COL_GP,6,1) = '3'        THEN 1
				               WHEN SUBSTR(D.YD_STK_COL_GP,6,1) IN ('2','1') THEN 2
				               WHEN SUBSTR(D.YD_STK_COL_GP,6,1) IN ('4','5') THEN 3
				               ELSE 4 END                          
				 */
				JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getListloadStoppointGDh", logId, mthdNm, "차량포인트 조회");
				if (jsRst.size() > 0) {
					ydBayInPnt = commUtils.nvl(jsRst.getRecord(0).getFieldString("YD_STK_COL_GP"), "JXPT03");
					jrParam.setField("YD_CARUD_STOP_LOC", ydBayInPnt);
				} 
			}
			
			/***************************************
	    	 * 차량스케줄 상하차 포인트 수정
	    	 ***************************************/
			jrParam.setField("WAIT_ARR_DDTT"		 , sCarArrDt);	//대기장도착시간
			/*
			UPDATE TB_YD_CARSCH
			   SET MOD_DDTT = SYSDATE
			     , MODIFIER = :V_MODIFIER
			     , SPOS_WLOC_CD       = NVL(:V_SPOS_WLOC_CD      , SPOS_WLOC_CD      )
			     , YD_CARLD_STOP_LOC  = NVL(:V_YD_CARLD_STOP_LOC , YD_CARLD_STOP_LOC )
			     , YD_CARLD_PNT_WO_DT = NVL(:V_YD_CARLD_PNT_WO_DT, YD_CARLD_PNT_WO_DT)
			     , YD_PNT_CD1         = NVL(:V_YD_PNT_CD1        , YD_PNT_CD1        )
			     , ARR_WLOC_CD        = NVL(:V_ARR_WLOC_CD       , ARR_WLOC_CD       )
			     , YD_CAR_PROG_STAT   = NVL(:V_YD_CAR_PROG_STAT  , YD_CAR_PROG_STAT  )
			     , YD_CARUD_LEV_DT    = NVL(:V_YD_CARUD_LEV_DT   , YD_CARUD_LEV_DT   )
			     , YD_CARUD_STOP_LOC  = NVL(:V_YD_CARUD_STOP_LOC , YD_CARUD_STOP_LOC )
			     , YD_CARUD_PNT_WO_DT = NVL(:V_YD_CARUD_PNT_WO_DT, YD_CARUD_PNT_WO_DT)
			     , YD_PNT_CD3         = NVL(:V_YD_PNT_CD3        , YD_PNT_CD3        )
			     , YD_EQP_WRK_STAT    = NVL(:V_YD_EQP_WRK_STAT   , YD_EQP_WRK_STAT   ) --상차U 하차 L로 세팅
			     , WAIT_ARR_DDTT      = NVL(:V_WAIT_ARR_DDTT     , WAIT_ARR_DDTT     ) --대기장 도착시간
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	    	 */
	    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCarSchTSYDJ002", logId, mthdNm, "차량스케줄 수정");
			
			/***************************************
	    	 * 소재차량도착Point요구
	    	 ***************************************/
			commUtils.printLog(logId, "2. 소재차량도착Point요구 모듈 호출", "SL");
    		jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRN_EQP_CD"			, sTrnEqpCd        );	
			jrParam.setField("WLOC_CD"				, sArrWlocCd       );	
			jrParam.setField("TRN_WRK_FULLVOID_GP"	, sTrnWrkFullvoidGp);	
			jrParam.setField("PNT_DMD_DT"			, sCurrDate        );			
			
			JDTORecord jrYdMsg = this.rcvTSYDJ002(jrParam);
			jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);


			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	

	
	/**
	 * 오퍼레이션명 : 초기화 처리  - CarinfoResetC
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord procInitCarinfo(JDTORecord rcvMsg) throws DAOException  {
		
		String mthdNm = "초기화 처리 [CCoilCarMvSeEJB.procInitCarinfo] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sTrnEqpCd  = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"   ));
			String sModifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRN_EQP_CD", sTrnEqpCd);
			
			/**
			 * 운송장비코드로 차량스케줄 조회
			 */
			/*
			SELECT *
			  FROM (
			        SELECT A.*
			              ,(SELECT YD_STKBED_USG_CD
			                  FROM TB_YD_STKCOL B
			                 WHERE B.YD_STK_COL_GP = A.YD_CARLD_STOP_LOC
			                   AND B.YD_STKBED_USG_CD IN ('A','D','E') --수출, 철송, 주문회
			               ) AS NEW_DEST_BAY
			          FROM TB_YD_CARSCH A
			         WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			           AND DEL_YN     ='N'
			         ORDER BY YD_CAR_SCH_ID DESC
			                , YD_CARUD_CMPL_DT DESC
			       ) A
			 WHERE ROWNUM <= 1
			 */
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarschByTrnEqpCd", logId, mthdNm, "차량스케줄 조회");
			
			if (jsCarSch.size() == 0) {
				commUtils.printLog(logId, "차량스케줄이 없습니다.", "S-");
				return jrRtn;
			}
			
			String ydCarSchId = jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID");
		
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
		
			/*
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
			     , C.YD_WBOOK_ID
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
			           AND A1.YD_GP  = 'J'
			       ) C
			     , (
			        SELECT A1.*
			          FROM USRYDA.TB_YD_WRKBOOK  A1
			             , USRYDA.TB_YD_WRKBOOKMTL   A2
			         WHERE A1.YD_WBOOK_ID = A2.YD_WBOOK_ID(+)
			           AND A1.DEL_YN = 'N'
			           AND A2.DEL_YN = 'N'
			           AND A1.YD_GP  = 'J'
			       ) D
			 WHERE A.YD_CAR_SCH_ID        = B.YD_CAR_SCH_ID(+)
			   AND A.YD_CARLD_WRK_BOOK_ID = C.YD_WBOOK_ID(+)
			   AND A.YD_CARUD_WRK_BOOK_ID = D.YD_WBOOK_ID(+)
			   AND A.YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID  -- 차량스케줄 ID
			   AND A.DEL_YN               = 'N'
			   AND B.DEL_YN(+)            = 'N'
			*/
		    JDTORecordSet jsSchInfo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getCarWrMgt", logId, mthdNm, "차량스케쥴검색");
		    
		    if( jsSchInfo.size() < 1 ) {
		    	commUtils.printLog(logId, "차량스케줄정보가 없습니다.", "S-");
				return jrRtn;
		    }
		    
		    jsSchInfo.first();

		    JDTORecord jrCarSchInfo = jsSchInfo.getRecord();
		    
//		    String sTrnEqpCd 		= jrCarSchInfo.getFieldString("TRN_EQP_CD");
		    String ydCrnSchId 		= jrCarSchInfo.getFieldString("YD_CRN_SCH_ID");
		    String ydCarLdYdWbookId = jrCarSchInfo.getFieldString("YD_CARLD_YD_WBOOK_ID");
		    String ydCarUdYdWbookId = jrCarSchInfo.getFieldString("YD_CARUD_YD_WBOOK_ID");
		    String ydCarLdStopLoc 	= jrCarSchInfo.getFieldString("YD_CARLD_STOP_LOC");
		    String ydCarUdStopLoc 	= jrCarSchInfo.getFieldString("YD_CARUD_STOP_LOC");
		    String ydCarProgStat 	= jrCarSchInfo.getFieldString("YD_CAR_PROG_STAT");
		    String ydPrepSchId 		= jrCarSchInfo.getFieldString("YD_PREP_SCH_ID");
		    String ydCarUseGp		= jrCarSchInfo.getFieldString("YD_CAR_USE_GP");
		    String ydSchCd			= jrCarSchInfo.getFieldString("YD_SCH_CD");
		    String sCarNo           = jrCarSchInfo.getFieldString("CAR_NO");
		    String sCardNo          = jrCarSchInfo.getFieldString("CARD_NO");
		    
		    boolean bCarMvYn = coilDao.chkCarMv(logId, mthdNm, ydSchCd); // 차량동간이적 여부
		    
	    	/**
	    	 *  차량 현재 크레인작업지시 있을 때
	    	 */
		    if (!"".equals(ydCrnSchId)) {
		    	jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
		    	jrParam.setField("YD_SCH_CD"	, ydSchCd);
		    	jrParam.setField("DEL_YN"		, "Y");
		    	
		    	/**********************************************************
				* 1. 크레인스케줄 취소
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
		    	
		    	jrRtn = commUtils.addSndData(jrRtn, jrCancelWrk);
		    	
		    	if (bCarMvYn) { //차량동간이적이면
			    	commUtils.printLog(logId, "차량동간이적 시 작업예약 삭제 생략", "SL");
		    	} else {
		    		
		    		/**
		    		 * 작업예약 삭제
		    		 */
		    		EJBConnector ejbConn1 = new EJBConnector("default", "CCoilJspSeEJB", this);
					JDTORecord jrDelWbk = (JDTORecord)ejbConn1.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
		    		
			    	sRtnCd	= commUtils.trim(jrDelWbk.getFieldString("RTN_CD"));
			    	sRtnMsg	= commUtils.trim(jrDelWbk.getFieldString("RTN_MSG"));
			    	
			    	if ( "0".equals(sRtnCd) ) {
			    		commUtils.printLog(logId, sRtnMsg, "SL");
			    		
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", sRtnMsg);
						return jrRtn;
			    	}
		    	}
		    } else {

		    	// 상차 예약작업 체크
		    	if ( !"".equals(ydCarLdYdWbookId) ) {
		    		if (bCarMvYn) { //차량동간이적이면
		    			commUtils.printLog(logId, "차량동간이적 시 작업예약 삭제 생략", "SL");
		    		}
		    		else {
			    		// 작업예약취소 호출
		    			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		    			jrParam.setField("YD_WBOOK_ID"	, ydCarLdYdWbookId);
		    			
		    			EJBConnector ejbConn1 = new EJBConnector("default", "CCoilJspSeEJB", this);
						JDTORecord jrWbkResult = (JDTORecord)ejbConn1.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
						
				    	String sRtnCd	= commUtils.nvl(jrWbkResult.getFieldString("RTN_CD"), "0");
				    	String sRtnMsg	= commUtils.trim(jrWbkResult.getFieldString("RTN_MSG"));
				    	
				    	if( !"1".equals(sRtnCd) ) {
				    		commUtils.printLog(logId, sRtnMsg, "SL");
				    		
							jrRtn.setField("RTN_CD"  , "0");
							jrRtn.setField("RTN_MSG" , sRtnMsg);
							return jrRtn;
				    	} 
				    	
			    		String sCraneWrSndYn = commUtils.trim(jrWbkResult.getFieldString("CRANE_WR_SND_YN"));
			    		
			    		if ("Y".equals(sCraneWrSndYn)) {
			    			String sEqpId		= commUtils.trim(jrWbkResult.getFieldString("YD_EQP_ID"));
			    			String sWrkProgStat	= commUtils.trim(jrWbkResult.getFieldString("YD_WRK_PROG_STAT"));
			    			String sYdSchCd 	= commUtils.trim(jrWbkResult.getFieldString("YD_SCH_CD"));
							
			    			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
							jrParam.setField("YD_EQP_ID"        , sEqpId);
							jrParam.setField("YD_WRK_PROG_STAT" , sWrkProgStat);
							jrParam.setField("YD_SCH_CD"        , sYdSchCd);
							
							// procY5CrnWrkOrdReq -> rcvY5YDL007 변경
							EJBConnector ejbConn = new EJBConnector("default", "CCoilL2RcvSeEJB", this);
							JDTORecord jrRtnParam = (JDTORecord)ejbConn.trx("rcvY5YDL007", new Class[] { JDTORecord.class }, new Object[] { jrParam });
							
							jrRtn = commUtils.addSndData(jrRtn, jrRtnParam );
								
				    	}
		    		}
		    	}
		    	
		    	// 하차 예약작업 체크
		    	if ( !"".equals(ydCarUdYdWbookId) ) {
		    		if (bCarMvYn) { //차량동간이적이면
		    			commUtils.printLog(logId, "차량동간이적 시 작업예약 삭제 생략", "SL");
		    		} else {
			    		// 작업예약취소 호출
		    			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		    			jrParam.setField("YD_WBOOK_ID"	, ydCarUdYdWbookId);
		    			
		    			EJBConnector ejbConn1 = new EJBConnector("default", "CCoilJspSeEJB", this);
						JDTORecord jrWbkResult = (JDTORecord)ejbConn1.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
		    			
				    	String sRtnCd	= commUtils.nvl(jrWbkResult.getFieldString("RTN_CD"), "0");
				    	String sRtnMsg	= commUtils.trim(jrWbkResult.getFieldString("RTN_MSG"));
				    	
				    	if( !"1".equals(sRtnCd) ) {
				    		commUtils.printLog(logId, sRtnMsg, "SL");
				    		
							jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", sRtnMsg);
							return jrRtn;
				    	}
				    	
			    		String sCraneWrSndYn = commUtils.trim(jrWbkResult.getFieldString("CRANE_WR_SND_YN"));
			    		
			    		if ("Y".equals(sCraneWrSndYn)) {
			    			String sEqpId		= commUtils.trim(jrWbkResult.getFieldString("YD_EQP_ID"));
			    			String sWrkProgStat	= commUtils.trim(jrWbkResult.getFieldString("YD_WRK_PROG_STAT"));
			    			String sYdSchCd 	= commUtils.trim(jrWbkResult.getFieldString("YD_SCH_CD"));
							
					    	commUtils.printLog(logId, "YD_EQP_ID        : ["+ sEqpId +"]"		, "SL");
					    	commUtils.printLog(logId, "YD_WRK_PROG_STAT : ["+ sWrkProgStat +"]"	, "SL");
					    	commUtils.printLog(logId, "YD_SCH_CD        : ["+ sYdSchCd +"]"		, "SL");
			    			
					    	// procY5CrnWrkOrdReq -> rcvY5YDL007 변경
							EJBConnector ejbConn = new EJBConnector("default", "CCoilL2RcvSeEJB", this);
							JDTORecord jrRtnParam = (JDTORecord)ejbConn.trx("rcvY5YDL007", new Class[] { JDTORecord.class }, new Object[] { jrParam });
							
							jrRtn = commUtils.addSndData(jrRtn, jrRtnParam );
							
			    		}
		    		}
		    	}
		    }
		    
		 
		    /*
		     *  차량스케쥴 취소
		     */
		    if (!"".equals(ydCarSchId)) {
		    	
		    	jrParam = commUtils.getParam(logId, mthdNm, sModifier);
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
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updDelCarWrMgtSchMtl", logId, mthdNm, "TB_YD_CARFTMVMTL 삭제");
				
				/*
				UPDATE TB_YD_CARSCH
				   SET DEL_YN   = 'Y'
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updDelCarWrMgtSch", logId, mthdNm, "TB_YD_CARSCH 삭제");
				
				String sCarStopLoc = "";
				
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
					
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
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
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updCarPointStkColGpC", logId, mthdNm, "차량POINT 차량정보 초기화");
					
				}
		    }
			
			// 상차, 하차 구분
			if ( "2".equals(ydCarProgStat) ||
				 "3".equals(ydCarProgStat) ||
				 "4".equals(ydCarProgStat) ||
				 "5".equals(ydCarProgStat) ) {
				if (!"".equals(ydCarLdStopLoc)) {
					
			    	jrParam = commUtils.getParam(logId, mthdNm, sModifier);
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
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updCarWrMgtStkcol", logId, mthdNm, "TB_YD_STKCOL 초기화");
					 
					// 저장위치 비활성화등록
			    	jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			    	jrParam.setField("YD_STK_BED_WT_MAX"  , CConstant.YD_STK_BED_WT_MAX_DEFAULT); // 300000
			    	jrParam.setField("YD_STK_COL_GP"	  , ydCarLdStopLoc);
			    	jrParam.setField("YD_STK_BED_ACT_STAT", "C");
			    	
			    	/*
					UPDATE TB_YD_STKBED
					   SET YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
					     , YD_STK_BED_WT_MAX   = :V_YD_STK_BED_WT_MAX
					     , MOD_DDTT            = SYSDATE
					     , MODIFIER            = :V_MODIFIER
					 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
			    	 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updStkbedActStat", logId, mthdNm, "TB_YD_STKBED 비활성화등록");
					
										
					// 적치단 비활성화
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
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
			    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updStklyrYdStkColGp", logId, mthdNm, "TB_YD_STKLYR 초기화");
						
					/**********************************************************
					* 업무기준 : 차량 출발 시 저장위치 제원 야드L2로 전송
					**********************************************************/
					JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
					
    				sndL2Msg.setField("YD_INFO_SYNC_CD"		, "4"                         ); //야드정보동기화코드
    				sndL2Msg.setField("MSG_GP"				, "I"                         ); //전문구분
    				sndL2Msg.setField("YD_STK_COL_GP"    	, ydCarLdStopLoc);
    				sndL2Msg.setField("YD_STK_BED_NO"    	, "01"  );
					if ("".equals(sTrnEqpCd)) {
						sndL2Msg.setField("YD_CAR_USE_GP"    	, "G"   ); //L:구내운송, G:출하차량
						sndL2Msg.setField("CAR_NO"  			, sCarNo); //차량번호
						sndL2Msg.setField("CARD_NO"  			, sCardNo); //카드번호
					} else {
						sndL2Msg.setField("TRN_EQP_CD"  		, sTrnEqpCd);	
					}
					sndL2Msg.setField("YD_CAR_ARRSTRT_STAT" , "S"); //A:도착, S:출발
					sndL2Msg.setField("YD_EQP_WRK_STAT"     , "L");
					
					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L001_CarInfo", sndL2Msg));
					
				}
			} else if ( "B".equals(ydCarProgStat) ||
					    "C".equals(ydCarProgStat) ||
					    "D".equals(ydCarProgStat) ||
					    "E".equals(ydCarProgStat) ) {
				
				if ( !"".equals(ydCarUdStopLoc) ) {
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
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
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updCarWrMgtStkcol", logId, mthdNm, "TB_YD_STKCOL 초기화");
					
					// 저장위치 비활성화등록
			    	jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			    	jrParam.setField("YD_STK_BED_WT_MAX"  , CConstant.YD_STK_BED_WT_MAX_DEFAULT); // 300000
			    	jrParam.setField("YD_STK_COL_GP"      , ydCarUdStopLoc);
			    	jrParam.setField("YD_STK_BED_ACT_STAT", "C");
			    	
			    	/*
					UPDATE TB_YD_STKBED
					   SET YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
					     , YD_STK_BED_WT_MAX   = :V_YD_STK_BED_WT_MAX
					     , MOD_DDTT            = SYSDATE
					     , MODIFIER            = :V_MODIFIER
					 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
			    	 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updStkbedActStat", logId, mthdNm, "TB_YD_STKBED 비활성화등록");
					
					// 적치단 비활성화
			    	jrParam = commUtils.getParam(logId, mthdNm, sModifier);
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
			    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updStklyrYdStkColGp", logId, mthdNm, "TB_YD_STKLYR 초기화");
										
					/**********************************************************
					* 업무기준 : 차량 출발 시 저장위치 제원 야드L2로 전송
					**********************************************************/
					JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
					
    				sndL2Msg.setField("YD_INFO_SYNC_CD"		, "4"                         ); //야드정보동기화코드
    				sndL2Msg.setField("MSG_GP"				, "I"                         ); //전문구분
    				sndL2Msg.setField("YD_STK_COL_GP"    	, ydCarUdStopLoc);
    				sndL2Msg.setField("YD_STK_BED_NO"    	, "01"  );
					if ("".equals(sTrnEqpCd)) {
						sndL2Msg.setField("YD_CAR_USE_GP"    	, "G"   ); //L:구내운송, G:출하차량
						sndL2Msg.setField("CAR_NO"  			, sCarNo); //차량번호
						sndL2Msg.setField("CARD_NO"  			, sCardNo); //카드번호
					} else {
						sndL2Msg.setField("TRN_EQP_CD"  		, sTrnEqpCd);	
					}
					sndL2Msg.setField("YD_CAR_ARRSTRT_STAT" , "S"); //A:도착, S:출발
					sndL2Msg.setField("YD_EQP_WRK_STAT"     , "L");
					
					jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L001_CarInfo", sndL2Msg));
					
				}
			}
			
			if ( !"".equals(ydPrepSchId) ) {
		    	jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		    	jrParam.setField("YD_PREP_SCH_ID", ydPrepSchId);
		    	
		    	/*
		    	UPDATE TB_YD_PREPMTL
		    	   SET DEL_YN   = 'N'
		    	     , MODIFIER = :V_MODIFIER
		    	     , MOD_DDTT = SYSDATE
		    	 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
		    	*/
		    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updCarWrMgtPrepSchMtl", logId, mthdNm, "TB_YD_PREPMTL 준비재료 복원");
		    	
		    	/*
		    	UPDATE TB_YD_PREPSCH
		    	   SET DEL_YN   = 'N'
		    	     , MODIFIER = :V_MODIFIER
		    	     , MOD_DDTT = SYSDATE
		    	 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
		    	*/

		    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updCarWrMgtPrepSch", logId, mthdNm, "TB_YD_PREPSCH 준비스케쥴 복원");
		    	
			}
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 오퍼레이션명 : 하차작업예약생성 - 구내운송
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord makeCarUdWrkBookAB(JDTORecord rcvMsg) throws DAOException  {
		String mthdNm = "하차작업예약생성[CCoilCarMvSeEJB.makeCarUdWrkBookAB] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
	    try {
	    	commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

	    	String ydCarSchId  = commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"));
	    	String sTrnEqpCd   = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"   ));
	    	String ydCarUseGp  = commUtils.trim(rcvMsg.getFieldString("YD_CAR_USE_GP"));
	    	String sArrWlocCd  = commUtils.trim(rcvMsg.getFieldString("WLOC_CD"      ));
	    	String sArrYdPntCd = commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); //착지
	    	
	    	String ydStkColGp  = commUtils.trim(rcvMsg.getFieldString("YD_CARUD_STOP_LOC")); //하차포인트
	    	String ydBayGp     = ydStkColGp.substring(1, 2); //동
	    	
	    	String sModifier   = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)

			String sMsg              = "";
	    	
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);

			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
			
			/* com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarftmvmtlID
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
			            , B.YD_AIM_YD_GP) AS YD_AIM_YD_GP
			       --//Tong 크레인 사용 금지재 -> A동 입고
			     , (CASE WHEN C.ARR_WLOC_CD = 'DHY21'
			              AND (SELECT 'Y' FROM USRYDA.VW_YD_SLABHSM C WHERE C.HSM_GP = 'X' AND C.SLAB_NO=B.STL_NO) = 'Y'
			                  THEN 'A'
			             WHEN C.ARR_WLOC_CD = 'DHY21' AND B.YD_AIM_YD_GP = 'A' AND MAX(TEMP9) OVER(PARTITION BY A.YD_CAR_SCH_ID) = 'Y'
			                  THEN 'B'
			             WHEN C.REGISTER = 'runTsRetHt'
			                  THEN 'A' --//반송인 경우 A동 하차
			             WHEN C.ARR_WLOC_CD = 'C3S01'
			                  THEN 'A' --//C3#스카핑 A동 하차
			             WHEN C.YD_CAR_USE_GP = 'L' AND C.ARR_WLOC_CD = 'DJY22' AND C.YD_CAR_PROG_STAT IN ('A', 'B')
			                  THEN NVL(SUBSTR(YD_CARUD_STOP_LOC, 2, 1), B.YD_AIM_BAY_GP) -- 구내운송 하차 도착위치로 작업예약 생성
			        ELSE B.YD_AIM_BAY_GP END
			       ) AS YD_AIM_BAY_GP
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
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarftmvmtlID", logId, mthdNm, "이송재료 조회");
			
			if (jsCarSch.size() <= 0) {
				sMsg = "차량이송재료 조회 실패!";
	    		commUtils.printLog(logId, sMsg, "SL");
	    		jrRtn.setField("RTN_CD"	, "0");	
	    		jrRtn.setField("RTN_MSG", sMsg);
	    		return jrRtn;
			}
			
			jsCarSch.first();
			JDTORecord jrCarSch = jsCarSch.getRecord();
	    	
	    	//야드구분 = 목표야드구분
	    	String ydAimYdGp   = commUtils.trim(jrCarSch.getFieldString("YD_AIM_YD_GP"));
	    	//야드동구분 = 목표동구분
	    	String ydAimBayGp  = commUtils.trim(jrCarSch.getFieldString("YD_AIM_BAY_GP"));
	    	//지시 도착포인트
//	    	String sArrYdPntCd = commUtils.trim(jrCarSch.getFieldString("ARR_YD_PNT_CD"));
	    	
	    	if ("".equals(sArrYdPntCd)) {
	    		sArrYdPntCd = "3";
	    	}
	    	
	    	/*********************************
			 * 스케줄 코드 생성
			 *********************************/
	    	String ydSchCd = "";
	    	
	    	/*
	    	 * 목적동 무시하고 강제 도착 또는 하차위치를 바꾸었을 때 필요함
	    	 */
	    	if (!ydBayGp.equals(ydAimBayGp)) {
	    		ydAimBayGp = ydBayGp;
	    	}
	    	
	    	jrParam.setField("YD_AIM_YD_GP" , ydAimYdGp);
	    	jrParam.setField("YD_AIM_BAY_GP", ydAimBayGp);
	    	jrParam.setField("ARR_WLOC_CD"  , sArrWlocCd);
	    	jrParam.setField("ARR_YD_PNT_CD", sArrYdPntCd);
	    	
	    	/*
			WITH PARAM AS (
			SELECT :V_YD_AIM_YD_GP  AS P_YD_AIM_YD_GP
			     , :V_YD_AIM_BAY_GP AS P_YD_AIM_BAY_GP
			     , :V_ARR_WLOC_CD   AS P_ARR_WLOC_CD
			     , SUBSTR(:V_ARR_YD_PNT_CD, 1, 1) AS P_ARR_YD_PNT_CD
			  FROM DUAL
			)
			SELECT CASE WHEN P_ARR_YD_PNT_CD = '2'   THEN 'J'||P_YD_AIM_BAY_GP||'PT02LH'  --2통로
			            WHEN P_ARR_YD_PNT_CD = '1'   THEN 'J'||P_YD_AIM_BAY_GP||'PT01LH'  --1통로
			            ELSE 'J'||P_YD_AIM_BAY_GP||'PT03LH'  --3통로
			        END AS YD_SCH_CD
			  FROM PARAM
	    	 */
	    	JDTORecordSet jsSchCd = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getSchCdFtmvLd", logId, mthdNm, "이송하차 스케줄 코드 생성");
	    	
	    	if (jsSchCd.size() <= 0) {
	    		sMsg = "차량이송하차 스케줄 코드 생성 실패!";
	    		commUtils.printLog(logId, sMsg, "SL");
	    		jrRtn.setField("RTN_CD"	, "0");	
	    		jrRtn.setField("RTN_MSG", sMsg);
	    		return jrRtn;
	    	}
	    	
	    	ydSchCd = jsSchCd.getRecord(0).getFieldString("YD_SCH_CD");
	    	
	    	//스케줄코드로 스케줄기준Table조회
	    	jrParam.setField("YD_SCH_CD", ydSchCd);
	    	
	    	/*
			SELECT YD_SCH_CD
			     , DEL_YN
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
			  FROM TB_YD_SCHRULE
			 WHERE 1 = 1 
			   AND YD_GP IN ('H','J')
			   AND YD_SCH_CD = :V_YD_SCH_CD
			   AND DEL_YN    = 'N'
			 */
			JDTORecordSet jsSchRule = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdSchRule", logId, mthdNm, "스케줄기준 조회");
	    	
			if (jsSchRule.size() <= 0) {
				sMsg = "스케줄코드(" + ydSchCd + ")에 대한 스케줄기준 데이터가 이상합니다.";
				commUtils.printLog(logId, sMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sMsg);	
				return jrRtn;
			}	
			
			jsSchRule.absolute(1);
			String ydWrkCrnPrior 	= commUtils.trim(jsSchRule.getRecord().getFieldString("YD_WRK_CRN_PRIOR")); //작업크레인우선순위
			String ydSchProhExn	  	= commUtils.trim(jsSchRule.getRecord().getFieldString("YD_SCH_PROH_EXN" )); //스케줄 금지 유무
			String ydWrkCrn       	= commUtils.trim(jsSchRule.getRecord().getFieldString("YD_WRK_CRN"      )); //작업크레인
			String ydAltCrnYn    	= commUtils.trim(jsSchRule.getRecord().getFieldString("YD_ALT_CRN_YN"   )); //대체크레인유무
			String ydAltCrn       	= commUtils.trim(jsSchRule.getRecord().getFieldString("YD_ALT_CRN"      )); //대체크레인
			String ydAltCrnPrior	= commUtils.trim(jsSchRule.getRecord().getFieldString("YD_ALT_CRN_PRIOR")); //대체크레인우선순위
	    	
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if ("Y".equals(ydSchProhExn)) {
				commUtils.printLog(logId, "스케줄 금지 유무가 '" + ydSchProhExn + "' 입니다", "SL");
				return jrRtn;
			}
			
			//작업크레인 설비 상태 체크
			String sRtnVal = coilDao.chkCrnStat(logId, mthdNm, ydWrkCrn);
			String ydEqpId = ""; 
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!sRtnVal.equals(CConstant.RETN_CD_SUCCESS)) {
				
				commUtils.printLog(logId, "작업크레인(" + ydWrkCrn + ") 사용 불가 상태입니다.", "SL");
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if ("Y".equals(ydAltCrnYn)) {
					
					commUtils.printLog(logId,  "대체크레인유무(" + ydAltCrnYn + "), 대체크레인이 없습니다.", "SL");
					ydEqpId = ydWrkCrn; //대체크레인이 없으면 주크레인 SET
//					return jrRtn; 
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				sRtnVal = coilDao.chkCrnStat(logId, mthdNm, ydAltCrn);
				
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!sRtnVal.equals(CConstant.RETN_CD_SUCCESS)) {
					
					commUtils.printLog(logId, "대체크레인(" + ydAltCrn + ") 사용 불가 상태입니다.", "S-");
					ydEqpId = ydWrkCrn; //대체크레인이 없으면 주크레인 SET
//					return jrRtn;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					ydEqpId = ydAltCrn;
					
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				ydEqpId = ydWrkCrn;
				
			}

			String ydWbookId = "";
			String ydWbookId1 = "";
	    	
	    	//작업예약항목SETTING
	    	JDTORecord jrWbook = commUtils.getParam(logId, mthdNm, sModifier);
	    	
			commUtils.printLog(logId, "작업예약 생성 완료", "SL");
    		
	    	//작업예약재료 등록
	    	for(int i = 1; i <= jsCarSch.size(); i++) {
	    		
	    		jsCarSch.absolute(i);
	    		ydWbookId = coilDao.getSeqId(logId, mthdNm, "WrkBook");
	    		
	    		if (i == 1) {
					ydWbookId1 = ydWbookId;
				}
	    		
		    	jrWbook.setField("YD_WBOOK_ID"    , ydWbookId);
		    	jrWbook.setField("YD_GP"          , "J");
		    	jrWbook.setField("YD_BAY_GP"      , ydAimBayGp);
		    	jrWbook.setField("YD_AIM_YD_GP"   , ydAimYdGp);
		    	jrWbook.setField("YD_AIM_BAY_GP"  , ydAimBayGp);
		    	jrWbook.setField("YD_SCH_PRIOR"   , ydWrkCrnPrior);
		    	jrWbook.setField("YD_SCH_CD"      , ydSchCd);
		    	jrWbook.setField("TRN_EQP_CD"     , sTrnEqpCd);
		    	jrWbook.setField("YD_CAR_USE_GP"  , ydCarUseGp);
		    	jrWbook.setField("YD_WRK_PLAN_CRN", ydEqpId);
		    	
	    		commDao.insert(jrWbook, "com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.insYdWrkbook", logId, mthdNm, "작업예약 생성(구내운송하차)");
	    		
	    		jrWbook.setField("YD_WBOOK_ID"		, ydWbookId);
	    		jrWbook.setField("YD_STK_COL_GP"    , ydStkColGp);
	    		jrWbook.setField("YD_STK_BED_NO"	, commUtils.trim(jsCarSch.getRecord().getFieldString("YD_STK_BED_NO"))); 
	    		jrWbook.setField("YD_STK_LYR_NO"	, commUtils.trim(jsCarSch.getRecord().getFieldString("YD_STK_LYR_NO"))); 
	    		jrWbook.setField("STL_NO"			, commUtils.trim(jsCarSch.getRecord().getFieldString("STL_NO"))); 
	    		jrWbook.setField("YD_UP_COLL_SEQ"	, "" + i);
	    		
	    		commDao.insert(jrWbook, "com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.insYdWrkbookMtl", logId, mthdNm, "작업예약재료 등록(구내운송하차)");
	    	}
	    	
//	    	jrParam = commUtils.getParam(logId, mthdNm, sModifier);
//			jrParam.setField("YD_CAR_SCH_ID"		, ydCarSchId);
//			jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, ydWbookId1);
//			jrParam.setField("YD_CAR_PROG_STAT"		, "A");
//			jrParam.setField("YD_CARUD_LEV_DT"		, commUtils.getDateTime14());  //하차출발일시
			
			/* 
			UPDATE TB_YD_CARSCH
			   SET MODIFIER             = :V_MODIFIER
			     , MOD_DDTT             = SYSDATE
			     , YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT
			     , YD_CARUD_LEV_DT      = :V_YD_CARUD_LEV_DT
			     , YD_CARUD_WRK_BOOK_ID = NVL(:V_YD_CARUD_WRK_BOOK_ID, YD_CARUD_WRK_BOOK_ID)
			     , YD_FRTOMOVE_BAY_GP   = NVL(:V_YD_FRTOMOVE_BAY_GP  , YD_FRTOMOVE_BAY_GP)
			 WHERE YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID
			*/ 
//			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarsch", logId, mthdNm, "차량스케쥴수정");	
	    	
	    	
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	
	/**
	 * 오퍼레이션명 : 상차Lot편성 호출 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord chkWlocCdLotComp(JDTORecord rcvMsg)throws JDTOException  {
		String mthdNm = "상차Lot편성[CCoilL2RcvSeEJB.chkWlocCdLotComp] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
	    
	    try {

	    	commUtils.printLog(logId, mthdNm, "S+");
	    	commUtils.printParam(logId, rcvMsg);
	    	
	    	String sTrnEqpCd            = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"             ));
	    	String sWlocCd              = commUtils.trim(rcvMsg.getFieldString("WLOC_CD"                ));
	    	String ydCarldLevLoc        = commUtils.trim(rcvMsg.getFieldString("YD_CARLD_LEV_LOC"       ));
	    	String sTrnEqpStkCapa       = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA"       ));
	    	String sSpTruckLoadingLocTp = commUtils.trim(rcvMsg.getFieldString("SP_TRUCK_LOADING_LOC_TP"));
	    	String sSposYdPntCd         = commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"         ));
	    	String sSposWlocCd          = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"           ));
	    	String sPntDmdDt            = commUtils.trim(rcvMsg.getFieldString("PNT_DMD_DT"             ));
//	    	String sIsEjbCall   		= commUtils.trim(rcvMsg.getFieldString("IS_EJB_CALL"            )); //(화면) 에서 전달됨
	    	
	    	String sModifier            = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
	    	String sMsg                 = "";
	    	
	    	String ydCarSchId           = "";
	    	
	    	JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	    		
    		
	    	/*******************
	    	 * 2열연 소재
	    	 *******************/
	    	if ("DJY21".equals(sWlocCd) || "DJY22".equals(sWlocCd) || "DJY1E".equals(sWlocCd)) {

	    		//차량LOT편성자동유무관리 기존 YDB499 -> APP499
	    		String ydAutoLot = coilDao.ApplyYn(logId, mthdNm, "APP499", "H", "*");
	    	
	    		commUtils.printLog(logId, "차량LOT편성자동유무관리 : " + ydAutoLot, "SL");
				//상차LOT편성 호출
				if ("Y".equals(ydAutoLot)) {
					
					ydCarSchId = coilDao.getSeqId(logId, mthdNm, "CarSch");
					jrParam.setField("YD_CAR_SCH_ID"          , ydCarSchId);
					jrParam.setField("TRN_EQP_CD"             , sTrnEqpCd);
					jrParam.setField("YD_EQP_ID"              , CConstant.YD_TS_CAR_EQP_ID); //야드설비ID: XXPT01
					jrParam.setField("YD_CAR_USE_GP"          , CConstant.YD_CAR_USE_GP_TS); //차량사용구분:L
					jrParam.setField("WLOC_CD"                , sWlocCd             ); //차량스케줄의 착지개소코드 - 상차야드
					jrParam.setField("SP_TRUCK_LOADING_LOC_TP", sSpTruckLoadingLocTp); //운송작업영공구분코드
					jrParam.setField("PNT_DMD_DT"             , sPntDmdDt           ); //포인트요구일시
					jrParam.setField("TRN_EQP_STK_CAPA"       , sTrnEqpStkCapa      );
					jrParam.setField("SPOS_WLOC_CD"           , sSposWlocCd         ); //발지개소코드
					jrParam.setField("SPOS_YD_PNT_CD"         , sSposYdPntCd        ); //발지포인트코드
					jrParam.setField("IS_EJB_CALL"            , "Y");
					
					JDTORecord jrYdMsg = this.procCHrCarLdWrkReq(jrParam);
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
					
				} else {
					
			    	sMsg = "[개소코드구분 및 상차Lot편성 호출처리] 소재창고 개소코드["+sWlocCd+"]이므로 차량스케줄 생성 시작";
			    	commUtils.printLog(logId, sMsg, "SL");

			    	ydCarSchId = coilDao.getSeqId(logId, mthdNm, "CarSch");
			    	
			    	jrParam.setField("YD_CAR_SCH_ID"   , ydCarSchId);
					jrParam.setField("YD_EQP_WRK_STAT" , "U"                       );		//야드설비작업상태
					jrParam.setField("YD_EQP_ID"       , CConstant.YD_TS_CAR_EQP_ID);		//야드설비ID: XXPT01
					jrParam.setField("TRN_EQP_CD"      , sTrnEqpCd                 );		//운송장비코드
					jrParam.setField("YD_CAR_USE_GP"   , CConstant.YD_CAR_USE_GP_TS);		//차량사용구분:L
					jrParam.setField("SPOS_WLOC_CD"    , sWlocCd                   );		//발지개소코드
					jrParam.setField("YD_CARLD_LEV_LOC", ydCarldLevLoc             );		//야드상차출발위치
					jrParam.setField("YD_CARLD_LEV_DT" , commUtils.getDateTime14() );		//상차출발일시
					jrParam.setField("YD_BAYIN_WO_SEQ" , CConstant.YD_BAYIN_WO_SEQ_DEFAULT);//입동지시순번 - 기본값으로 설정(9)
					jrParam.setField("YD_CAR_PROG_STAT", "1"                       );		//상차출발상태
					
					commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insYdCarsch", logId, mthdNm, "차량스케줄 생성");
					
					//차량도착Point요구모듈호출
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("TRN_EQP_CD"         , sTrnEqpCd                );		//운송장비코드
					jrParam.setField("WLOC_CD"            , sWlocCd                  );
					jrParam.setField("TRN_WRK_FULLVOID_GP", "E"                      );
					jrParam.setField("PNT_DMD_DT"         , commUtils.getDateTime14());
					JDTORecord jrTsYd = this.rcvTSYDJ002(jrParam);
					jrRtn = commUtils.addSndData(jrRtn, jrTsYd);
				}
	    	}
	    	/*******************
	    	 * 2열연 코일제품
	    	 *******************/
/*		    else if ("DJY1E".equals(sWlocCd)) {
		    	
		    	//차량LOT편성자동유무관리 기존 YDB499 -> APP499
		    	String ydAutoLot = coilDao.ApplyYn(logId, mthdNm, "APP499", "J", "*");
		    	
		    	if ("Y".equals(ydAutoLot)) {
					
					jrParam.setField("YD_CAR_SCH_ID"          , ydCarSchId);
					jrParam.setField("TRN_EQP_CD"             , sTrnEqpCd);
					jrParam.setField("YD_EQP_ID"              , CConstant.YD_TS_CAR_EQP_ID);				//야드설비ID: XXPT01
					jrParam.setField("YD_CAR_USE_GP"          , CConstant.YD_CAR_USE_GP_TS);				//차량사용구분:L
					jrParam.setField("WLOC_CD"                , sWlocCd);						//차량스케줄의 발지개소코드 - 상차야드
					jrParam.setField("SP_TRUCK_LOADING_LOC_TP", sSpTruckLoadingLocTp);
					jrParam.setField("PNT_DMD_DT"             , sPntDmdDt);					
					jrParam.setField("TRN_EQP_STK_CAPA"       , sTrnEqpStkCapa);	
					jrParam.setField("SPOS_WLOC_CD"           , sSposWlocCd);	
					jrParam.setField("SPOS_YD_PNT_CD"         , sSposYdPntCd);
					jrParam.setField("JMS_TC_CD"              , sJmsTcCd);
					
					JDTORecord jrYdMsg = this.procCHrCarLdWrkReq(jrParam);
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
					
				} else {
					
			    	sMsg = "[개소코드구분 및 상차Lot편성 호출처리] 코일제품 개소코드["+sWlocCd+"]이므로 차량스케줄 생성 시작";
			    	commUtils.printLog(logId, sMsg, "SL");

			    	ydCarSchId = coilDao.getSeqId(logId, mthdNm, "CarSch");
			    	
			    	jrParam.setField("YD_CAR_SCH_ID"   , ydCarSchId                );
					jrParam.setField("YD_EQP_WRK_STAT" , "U"                       );		//야드설비작업상태
					jrParam.setField("YD_EQP_ID"       , CConstant.YD_TS_CAR_EQP_ID);		//야드설비ID: XXPT01
					jrParam.setField("TRN_EQP_CD"      , sTrnEqpCd                 );		//운송장비코드
					jrParam.setField("YD_CAR_USE_GP"   , CConstant.YD_CAR_USE_GP_TS);		//차량사용구분:L
					jrParam.setField("SPOS_WLOC_CD"    , sWlocCd                   );		//발지개소코드
					jrParam.setField("YD_CARLD_LEV_LOC", ydCarldLevLoc             );		//야드상차출발위치
					jrParam.setField("YD_CARLD_LEV_DT" , commUtils.getDateTime14() );		//상차출발일시
					jrParam.setField("YD_BAYIN_WO_SEQ" , CConstant.YD_BAYIN_WO_SEQ_DEFAULT);//입동지시순번 - 기본값으로 설정(9)
					jrParam.setField("YD_CAR_PROG_STAT", "1"                       );		//상차출발상태
					
					commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insYdCarsch", logId, mthdNm, "차량스케줄 생성");
					
					//차량도착Point요구모듈호출
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("TRN_EQP_CD"         , sTrnEqpCd                );		//운송장비코드
					jrParam.setField("WLOC_CD"            , sWlocCd                  );
					jrParam.setField("TRN_WRK_FULLVOID_GP", "E"                      );
					jrParam.setField("PNT_DMD_DT"         , commUtils.getDateTime14());
					JDTORecord jrTsYd = this.rcvTSYDJ002(jrParam);
					jrRtn = commUtils.addSndData(jrRtn, jrTsYd);
				}
	    	}*/
	    	
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	

	
	/**
	 *      [A] 오퍼레이션명 : 2열연 차량상차작업요구(소재,제품)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procCHrCarLdWrkReq(JDTORecord rcvMsg)throws DAOException  {
		String mthdNm = "2열연차량상차작업요구[CCoilCarMvSeEJB.procCHrCarLdWrkReq] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
	    
	    try{

			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			/**********************************************************
			* 수신 항목 값 Check
			**********************************************************/
			String ydCarSchId           = commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"          )); //차량스케줄ID
			String sTrnEqpCd  		    = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"             )); //운송장비코드
			String ydEqpId   		    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"              )); //야드설비ID: XXPT01 				
			String ydCarUseGp           = commUtils.trim(rcvMsg.getFieldString("YD_CAR_USE_GP"          )); //차량사용구분:L
			String sWlocCd              = commUtils.trim(rcvMsg.getFieldString("WLOC_CD"                )); //차량스케줄의 발지개소코드 - 상차야드          
			String sSpTruckLoadingLocTp = commUtils.trim(rcvMsg.getFieldString("SP_TRUCK_LOADING_LOC_TP")); //운송작업영공구분코드                    
			String sPntDmdDt            = commUtils.trim(rcvMsg.getFieldString("PNT_DMD_DT"             )); //포인트요구일시                       
			String sTrnEqpStkCapa       = commUtils.nvl (rcvMsg.getFieldString("TRN_EQP_STK_CAPA"       ), "0");                                 
			String sSposWlocCd          = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"           )); //발지개소코드                        
			String sSposYdPntCd         = commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"         )); //발지포인트코드     
			
			String sFrtoMoveWordNo      = commUtils.trim(rcvMsg.getFieldString("FRTOMOVE_WORD_NO"       ));
			
			String sArrYn               = commUtils.nvl (rcvMsg.getFieldString("ARR_YN"                 ), "N"); //도착여부      
			String sBayInPnt            = commUtils.trim(rcvMsg.getFieldString("YD_CARLD_STOP_LOC"      ));      // 도착시에만 값이 들어옴
			
			String sModifier  		    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"         )); 	  //수정자(Backup Only)
			
			String sMsg      = "";
			
			
			if ("".equals(ydCarUseGp) || !"L".equals(ydCarUseGp)) {
				commUtils.printLog(logId, "차량사용구분 이상", "S-");
				return jrRtn;		
			}
			
			if ("".equals(sTrnEqpCd)) {
				commUtils.printLog(logId, "운송장비코드 이상", "S-");
				return jrRtn;
			}

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			String sBayInGp = "";
			
			if ("".equals(sBayInPnt)) {
				sBayInGp = "";
			} else {
				sBayInGp = sBayInPnt.substring(1, 2);
			}
			
			/**********************************************************
			* 1. 작업대상 재료 조회
			**********************************************************/
			commUtils.printLog(logId, "1. 작업대상 재료 조회", "SL");
			
			jrParam.setField("SPOS_WLOC_CD"		, sWlocCd  );
			jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd);
			jrParam.setField("YD_BAY_GP"		, sBayInGp );
			
			/*
			WITH TEMP_TABLE AS (
			SELECT AA.*
			  FROM (
			        SELECT 
			               DECODE(A.MTL_UGNT_GP,'Y','1','N','2')       AS MTL_UGNT_SEQ--긴급재순위
			             , (CASE WHEN D7.CNT=0 THEN 0 ELSE D7.CNT END) AS CAR_POINT --차량갯수
			             , D1.NEXT_PROC                          --AS 구내차공정우선순위
			             , DECODE(A.GP,'CG',' ',D3.SCH_RULE_VAL) AS YD_NEXT_PROC--야드차공정우선순위
			             , A.YD_STK_COL_GP
			             , A.YD_STK_BED_NO
			             , A.YD_STK_LYR_NO
			             , A.GP                          AS YD_MTL_ITEM
			             , A.CUR_DONG                    --AS 제품현재동
			             , A.CG_AIM                      --AS 제품목표동
			             , A.CM_AIM                      -- 소재차공정
			             , A.STL_NO             , A.SPOS_WLOC_CD     -- 발지개소
			
			             , A.ARR_WLOC_CD      -- 착지개소
			             , A.HCR_GP           -- HCR구분
			             , A.YD_MTL_WT           -- 중량
			             , A.COIL_W  AS YD_MTL_W
			             , A.YD_AIM_RT_GP
			             , A.YD_AIM_YD_GP
			             , A.YD_AIM_BAY_GP 
			             , SUBSTR(A.YD_STK_COL_GP,1,1) AS YD_GP
			             , SUBSTR(A.YD_STK_COL_GP,2,1) AS YD_BAY_GP
			             , SUBSTR(A.YD_STK_COL_GP,3,2) AS YD_EQP_GP
			
			          FROM (
			                SELECT DECODE(C.STL_APPEAR_GP,'Y','CG','CM') AS GP
			                     , SUBSTR(A.YD_STK_COL_GP,1,1),SUBSTR(A.YD_STK_COL_GP,2,1)
			                     , DECODE(C.STL_APPEAR_GP,'Y',SUBSTR(B.ARR_YD_PNT_CD,2,1),'X') AS CG_AIM
			                     , DECODE(C.STL_APPEAR_GP,'Y','XX',C.NEXT_PROC) AS CM_AIM
			                     , SUBSTR(A.YD_STK_COL_GP,2,1) AS CUR_DONG
			                     , C.NEXT_PROC
			                     , B.TRN_WRK_MTL_GP
			                     , A.STL_NO
			                     , A.YD_STK_COL_GP
			                     , A.YD_STK_BED_NO
			                     , A.YD_STK_LYR_NO
			                     , B.SPOS_WLOC_CD      -- 발지개소
			                     , B.ARR_WLOC_CD       -- 착지개소
			                     , B.MTL_UGNT_GP       -- 긴급재구분
			                     , B.HCR_GP             -- HCR구분
			                     , C.COIL_WT       AS YD_MTL_WT       --중량
			                     , C.COIL_W
			                     , D.YD_AIM_RT_GP
			                     , D.YD_MTL_ITEM
			                     , D.YD_AIM_YD_GP
			                     , D.YD_AIM_BAY_GP
			                  FROM TB_YD_STKLYR       A
			                     , TB_TS_MATL_FTMV_WO B
			                     , TB_PT_COILCOMM     C
			                     , TB_YD_STOCK        D
			                 WHERE A.STL_NO  = B.STL_NO
			                   AND A.STL_NO  = C.COIL_NO
			                   AND C.COIL_NO = D.STL_NO
			                   AND A.YD_STK_LYR_MTL_STAT = 'C'
			                   AND C.CURR_PROG_CD LIKE (CASE WHEN B.TRN_WRK_MTL_GP = 'H' THEN 'E' ELSE '%' END)
			                   AND NOT EXISTS (SELECT STL_NO 
			                                     FROM TB_YD_WRKBOOKMTL K
			                                    WHERE K.STL_NO = A.STL_NO
			                                      AND DEL_YN   = 'N')
			                   AND ((SUBSTR(A.YD_STK_COL_GP,3,2) <> 'PT') AND (SUBSTR(A.YD_STK_COL_GP,3,2) <> 'TR'))
			                   AND B.SPOS_WLOC_CD  =    :V_SPOS_WLOC_CD
			                   AND B.TS_MATL_FTMV_STAT_GP     = '1'
			                   AND B.MATL_FTMV_WO_NML_HD_YN   = 'Y'
			                   AND A.YD_STK_COL_GP NOT LIKE '__CD__' --CR->CD 크래들롤
			                   AND B.TRN_WRK_MTL_GP LIKE ( SELECT NVL(TRN_WRK_MTL_GP,'H')
			                                                 FROM TB_TS_TRN_EQP_OPRN_PLN
			                                                WHERE SYSDATE BETWEEN CARASGN_ST_DT AND CARASGN_END_DT
			                                                  AND TRN_EQP_CD = :V_TRN_EQP_CD -- 'GTR26068' --'GTR21143'
			                                                  AND DEL_YN  ='N'
			                                                  AND ROWNUM <= 1
			                                             ) ||'%'
			                   AND A.DEL_YN = 'N'
			                   AND B.DEL_YN = 'N'
			               ) A 
			             , (
			                SELECT NEXT_PROC 
			                  FROM TB_TS_TRN_EQP_OPRN_PLN
			                 WHERE SYSDATE BETWEEN CARASGN_ST_DT AND CARASGN_END_DT
			                   AND TRN_EQP_CD =:V_TRN_EQP_CD -- 'GTR26068' --'GTR21143'
			                   AND DEL_YN='N'
			                   AND ROWNUM<=1
			               ) D1 --구내차공정우선순위
			             , (
			                SELECT SCH_RULE_VAL
			                     , SCH_CD 
			                  FROM TB_YM_STACKPRIORITY
			                 WHERE RULE_ID = 'YM08'
			                 UNION ALL
			                SELECT '1' 
			                     , 'XX' AS SCH_CD
			                  FROM DUAL
			               ) D3 -- 야드차공정우선순위
			             , (
			                SELECT SUBSTR(YD_STK_COL_GP,'2','1') AS DONG 
			                     , NVL(COUNT(CAR_NO),0)+ NVL(COUNT(TRN_EQP_CD),0) AS CNT
			                  FROM TB_YD_STKCOL 
			                 WHERE WLOC_CD = :V_SPOS_WLOC_CD
			                   AND YD_STK_COL_GP Like '__PT%'
			                 GROUP BY SUBSTR(YD_STK_COL_GP,'2','1')
			               ) D7 -- 차량위치체크
			         WHERE A.CM_AIM    = D3.SCH_CD
			           AND A.NEXT_PROC = D1.NEXT_PROC(+) 
			           AND A.CUR_DONG  = D7.DONG(+) 
			       ) AA
			 WHERE 1 = 1
			)
			, TEMP_TABLE2 AS (
			SELECT *
			  FROM TEMP_TABLE AA
			 WHERE AA.CAR_POINT IS NOT NULL
			 ORDER BY AA.NEXT_PROC
			--       , (CASE WHEN AA.YD_STK_COL_GP LIKE 'HH%' THEN AA.YD_STK_LYR_NO ELSE '0' END) DESC
			       , AA.YD_STK_LYR_NO DESC
			       , AA.MTL_UGNT_SEQ
			       , AA.CAR_POINT
			--       , AA.YD_STK_LYR_NO DESC
			       , (CASE WHEN YD_MTL_ITEM = 'CM' THEN 1 ELSE LENGTH(AA.STL_NO) END) 
			       , AA.YD_NEXT_PROC
			       , AA.YD_STK_COL_GP
			       , AA.YD_STK_BED_NO
			       , AA.YD_MTL_W
			) 
			SELECT AA.*
			     , NVL(BB.TONG, 3) AS TONG
			     , BB.YD_STK_COL_GP AS CAR_COL_GP
			
			  FROM TEMP_TABLE2 AA
			     , (
			        SELECT CASE WHEN SUBSTR(C.YD_STK_COL_GP,6,1) = '3'        THEN 3
			                    WHEN SUBSTR(C.YD_STK_COL_GP,6,1) IN ('2','1') THEN 1
			                    WHEN SUBSTR(C.YD_STK_COL_GP,6,1) IN ('4','5') THEN 2
			                    ELSE 3 END TONG
			             , C.YD_STK_COL_GP
			          FROM (SELECT A.YD_STK_COL_GP
			                     , (SELECT COUNT(*)
			                          FROM TB_YD_CARSCH CS
			                         WHERE CS.DEL_YN = 'N'
			                           AND CS.YD_CARLD_STOP_LOC = B.YD_STK_COL_GP
			                       ) AS CARLD_RANK
			                  FROM TB_YD_CARPOINT A
			                     , TB_YD_STKCOL   B  
			                 WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP   
			                   AND SUBSTR(A.YD_STK_COL_GP, 1, 1) = 'J'
			                   AND A.YD_STK_COL_ACT_STAT <> 'N'
			                   AND A.YD_CAR_USETYPE_GP IN('TO', 'GT')
			--                   AND B.YD_LOC_GP = 'H'
			                   AND B.YD_LOC_GP  = CASE WHEN (SELECT ITEM1 FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP002') = 'Y' THEN B.YD_LOC_GP
			                                           ELSE 'H' END
			                   AND A.DEL_YN = 'N'
			                   AND B.DEL_YN = 'N'
			                 --통로 ORDER BY : 
			                 -- 1. 3통로가 열려 있으면 무조건 3통로    
			                 -- 2. 2통로가 열려 있으면 2통로 우선    
			                 -- 3. 1통로가 열려 있으면 1통로 우선    
			                 ORDER BY CASE WHEN SUBSTR(A.YD_STK_COL_GP,6,1) = '3'        THEN 1
			                               WHEN SUBSTR(A.YD_STK_COL_GP,6,1) IN ('2','1') THEN 2
			                               WHEN SUBSTR(A.YD_STK_COL_GP,6,1) IN ('4','5') THEN 3
			                               ELSE 4 END 
			                        , CARLD_RANK
			                 ) C 
			               , TEMP_TABLE2 AA
			           WHERE C.YD_STK_COL_GP LIKE SUBSTR(AA.YD_STK_COL_GP,1,2) ||'PT%'
			             AND ROWNUM = 1   
			       ) BB
			 WHERE SUBSTR(AA.YD_STK_COL_GP, 1, 2) = SUBSTR(BB.YD_STK_COL_GP(+) , 1, 2)
			   AND (CM_AIM, SUBSTR(AA.YD_STK_COL_GP,1,2)) = (SELECT CM_AIM, SUBSTR(YD_STK_COL_GP,1,2) 
			                                                   FROM TEMP_TABLE2
			                                                  WHERE ROWNUM <= 1 )
			   AND ROWNUM <= (SELECT ITEM1        --default 작업매수
			                    FROM TB_YD_RULE
			                   WHERE REPR_CD_GP = 'APP802'
			                     AND CD_GP      = 'J'
			                  )
			   AND AA.YD_BAY_GP LIKE :V_YD_BAY_GP || '%'
			 ORDER BY AA.NEXT_PROC
			        , AA.MTL_UGNT_SEQ
			        , AA.CAR_POINT
			        , AA.YD_STK_LYR_NO DESC
			        , (CASE WHEN YD_MTL_ITEM = 'CM' THEN 1 ELSE LENGTH(AA.STL_NO) END)  
			        , AA.YD_NEXT_PROC
			        , AA.YD_STK_COL_GP
			        , AA.YD_STK_BED_NO
			        , AA.YD_MTL_W
			 */
			JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStockYdStkLyrMatlLotC", logId, mthdNm, "저장품 조회");
			
			if (jsStock.size() <= 0) {//재료 없으면 리턴
				sMsg = "이송대상이 없습니다." ;
				commUtils.printLog(logId, sMsg, "SL");
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
			}

			String sTong      = commUtils.nvl(jsStock.getRecord(0).getFieldString("TONG"), "3"); //통로   ex)1
			String sCarColGp  = jsStock.getRecord(0).getFieldString("CAR_COL_GP"   ); //상차도 ex)JAPT01
			String ydStkColGp = jsStock.getRecord(0).getFieldString("YD_STK_COL_GP");
			String ydAimRtGp  = jsStock.getRecord(0).getFieldString("YD_AIM_RT_GP" );
			
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CAR_SCH_ID"          , ydCarSchId   );
			jrParam.setField("YD_EQP_ID"              , ydEqpId      );// 설비ID
			jrParam.setField("YD_CAR_USE_GP"          , ydCarUseGp   );// 차량사용구분
			jrParam.setField("TRN_EQP_CD"             , sTrnEqpCd    );// 운송장비코드
			jrParam.setField("WLOC_CD"                , sWlocCd      );// 개소코드
			jrParam.setField("PNT_DMD_DT"             , sPntDmdDt    );// 포인트요구일시
			jrParam.setField("SPOS_WLOC_CD"           , sSposWlocCd  );// 발지개소코드
			jrParam.setField("SPOS_YD_PNT_CD"         , sSposYdPntCd );// 발지포인트코드
			jrParam.setField("YD_MTL_ITEM"            , "CM"         );// 재료품목 CM : 소재 CG :제품
			jrParam.setField("CAR_COL_GP"             , sCarColGp    );// 상차도 ex)JAPT01
			
			/*
			 * 차량출발시에는 차량스케줄만 생성 후 종료
			 */
			if ("N".equals(sArrYn)) {	
				/**********************************************************
				* 차량스케줄 생성
				**********************************************************/
				commUtils.printLog(logId, "차량스케줄 생성", "SL");
				JDTORecord jrCarSch = this.mkY5CarSch(jrParam);
				jrRtn = commUtils.addSndData(jrRtn, jrCarSch);
				
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
			}
			
			/**********************************************************
			* 2. 작업매수 및 허용중량 조회
			**********************************************************/
			commUtils.printLog(logId, "2. 작업매수 및 허용중량 조회", "SL");
			JDTORecord jrRst   = JDTORecordFactory.getInstance().create();
			boolean bDefaultCarSpec = coilDao.getYdRule(logId, mthdNm, "APP802", "J", "*", jrRst);

			int iYdWrkAlwSh = 0;
			
			if (bDefaultCarSpec) {
				iYdWrkAlwSh = Integer.parseInt(jrRst.getFieldString("ITEM1"));//default 작업매수
				if ("0".equals(sTrnEqpStkCapa)) {
					sTrnEqpStkCapa = jrRst.getFieldString("ITEM_VALUE1"); //default 허용중량
				}
			}
			
			commUtils.printLog(logId, "작업매수, 중량 : "+ iYdWrkAlwSh+ ", "+ sTrnEqpStkCapa, "SL");
			
			/*
			 * 스케줄코드 생성
			 */
			String ydSchCd = ydStkColGp.substring(0, 2) + "PT0" + sTong + "UH"; //defalut 소재통로3

			commUtils.printLog(logId, "ydSchCd : " + ydSchCd, "SL");
			
			// 이송 재료 매수가 차량 이송 가능 매수보다 작으면 이송 재료 매수를 차량 이송 가능 매수에 대입한다.
			if (iYdWrkAlwSh > jsStock.size()) {
				iYdWrkAlwSh = jsStock.size();
			}
			commUtils.printLog(logId, "iYdWrkAlwSh : "+iYdWrkAlwSh, "SL");
			
			// 차량작업허용중량
			long lngYdWrkAlwWt = Long.parseLong(sTrnEqpStkCapa)	;
			long lngSumMtlWt   = 0;
			int  iYdCarldSh    = 0;

			jsStock.first();
			// 차량 이송 가능 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int i = 1; i <= iYdWrkAlwSh; ++i) {
				
				lngSumMtlWt = lngSumMtlWt + Long.parseLong(jsStock.getRecord().getFieldString("YD_MTL_WT"));
				commUtils.printLog(logId, "lngYdWrkAlwWt : "+lngYdWrkAlwWt, "SL");
				commUtils.printLog(logId, "lngSumMtlWt   : "+lngSumMtlWt  , "SL");
				
				// 차량작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				if (lngYdWrkAlwWt < lngSumMtlWt) {
					break;
				}

				// 차량이송재료매수
				iYdCarldSh = i;
				// 다음 레코드 추출
				jsStock.next();
			}
			
			jrParam.setField("YD_AIM_RT_GP"		      , ydAimRtGp);
			jrParam.setField("YD_SCH_CD"		      , ydSchCd             );// 스케줄코드
			jrParam.setField("YD_CARLD_SH"            , "" + iYdCarldSh     );// 상차 LOT 재료매수
			jrParam.setField("STL_SH"                 , "" + iYdCarldSh     );// 상차 LOT 재료매수 insWrkBook 호출하기위한 변수
			jrParam.setField("SP_TRUCK_LOADING_LOC_TP",	sSpTruckLoadingLocTp);// 운송작업영공구분코드
			
			/**********************************************************
			* 작업예약 생성
			**********************************************************/
			commUtils.printLog(logId, "작업예약 생성", "SL");
			
			String sWrkCrn     	= "";//야드작업크레인 		
			String ydSchPrior   = "";//야드작업크레인우선순위
			String ydWbookId    = "";
			
			/**********************************************************
			* 스케줄코드 Check
			**********************************************************/
			/*
			SELECT YD_SCH_CD
			     , DEL_YN
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
			  FROM TB_YD_SCHRULE
			 WHERE 1 = 1 
			   AND YD_GP IN ('H','J')
			   AND YD_SCH_CD = :V_YD_SCH_CD
			   AND DEL_YN    = 'N'
			 */
			JDTORecordSet jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdSchRule", logId, mthdNm, "스케줄기준 조회");

			//리턴값 메세지처리
			if (jsResult.size() <= 0) {
				sMsg = "스케줄코드(" + ydSchCd + ")에 대한 스케줄기준 데이터가 이상합니다.";
				commUtils.printLog(logId, sMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sMsg);	
				return jrRtn;
			}	
			
			//레코드 추출
			jsResult.absolute(1);
			
			String ydGp            	= commUtils.trim(jsResult.getRecord().getFieldString("YD_GP"           )); //야드구분
			String ydBayGp        	= commUtils.trim(jsResult.getRecord().getFieldString("YD_BAY_GP"       )); //동구분
			String ydSchProhExn	  	= commUtils.trim(jsResult.getRecord().getFieldString("YD_SCH_PROH_EXN" )); //스케줄 금지 유무
			String ydWrkCrn       	= commUtils.trim(jsResult.getRecord().getFieldString("YD_WRK_CRN"      )); //작업크레인
			String ydWrkCrnPrior 	= commUtils.trim(jsResult.getRecord().getFieldString("YD_WRK_CRN_PRIOR")); //작업크레인우선순위
			String ydAltCrnYn    	= commUtils.trim(jsResult.getRecord().getFieldString("YD_ALT_CRN_YN"   )); //대체크레인유무
			String ydAltCrn       	= commUtils.trim(jsResult.getRecord().getFieldString("YD_ALT_CRN"      )); //대체크레인
			String ydAltCrnPrior	= commUtils.trim(jsResult.getRecord().getFieldString("YD_ALT_CRN_PRIOR")); //대체크레인우선순위
			String ydAimYdGp	    = "";   
			String ydAimBayGp       = "";
			
			//스케줄 금지 유무가 "Y"이면 처리를 중지
			if ("Y".equals(ydSchProhExn)) {
				sMsg = "스케줄 금지 유무가 [Y] 입니다";
				commUtils.printLog(logId, sMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sMsg);
				return jrRtn;
			}
			
			/**********************************************************
			* 작업크레인 체크
			**********************************************************/
			
			//작업크레인 설비 상태 체크
			boolean blnRtnVal = coilDao.chkEqpStat(logId, mthdNm, ydWrkCrn);
			
			commUtils.printLog(logId, "blnRtnVal:" + blnRtnVal, "SL");
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {
				
				sMsg = "작업크레인(" + ydWrkCrn + ")이 사용 불가 상태입니다.";
				commUtils.printLog(logId, sMsg, "SL");
				
				//대체크레인의 유무 체크
				if (!"Y".equals(ydAltCrnYn)) {
					sMsg = "대체크레인유무(" + ydAltCrnYn + "), 대체크레인이 없습니다.";
					commUtils.printLog(logId, sMsg, "SL");
//					jrRtn.setField("RTN_CD" , "0");	
//					jrRtn.setField("RTN_MSG", sMsg);
//					return jrRtn;
					sWrkCrn    = ydWrkCrn;
					ydSchPrior = ydWrkCrnPrior;
				}
				
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = coilDao.chkEqpStat(logId, mthdNm, ydAltCrn);
				
				//대체크레인 사용여부
				if (!blnRtnVal) {
					sMsg = "대체크레인(" + ydAltCrn + ")이 사용 불가 상태입니다.";
					commUtils.printLog(logId, sMsg, "SL");
//					jrRtn.setField("RTN_CD" , "0");	
//					jrRtn.setField("RTN_MSG", sMsg);	
//					return jrRtn;
					sWrkCrn    = ydWrkCrn;
					ydSchPrior = ydWrkCrnPrior;
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					sWrkCrn    = ydAltCrn;
					ydSchPrior = ydAltCrnPrior;
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				sWrkCrn    = ydWrkCrn;
				ydSchPrior = ydWrkCrnPrior;
			}			
			
			String ydWbookId1 = ""; //대표작업예약ID
			for (int i = 1; i <= iYdCarldSh; ++i) {
				
				jsStock.absolute(i);
				
				ydWbookId = coilDao.getSeqId(logId, mthdNm, "WrkBook");
				
				if (i == 1) {
					ydWbookId1 = ydWbookId;
				}
				
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_WBOOK_ID"      , ydWbookId          );
				jrParam.setField("YD_GP"            , ydGp               );
				jrParam.setField("YD_BAY_GP"        , ydBayGp            );
				jrParam.setField("YD_SCH_CD"        , ydSchCd            );
				jrParam.setField("YD_SCH_PRIOR"     , ydSchPrior         );
				jrParam.setField("YD_CAR_USE_GP"    , "L"                );
				jrParam.setField("YD_AIM_YD_GP"     , ydAimYdGp          );
				jrParam.setField("YD_AIM_BAY_GP"    , ydAimBayGp         );
				jrParam.setField("TRN_EQP_CD"	    , sTrnEqpCd          );
				jrParam.setField("YD_WRK_PLAN_CRN"  , sWrkCrn            );
				jrParam.setField("YD_TO_LOC_GUIDE"  , sBayInPnt          );	//야드To위치Guide
				
				// 작업예약 INSERT
				commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.insYdWrkbook", logId, mthdNm, "작업예약 생성(상차작업예약)");
				
			 	/**********************************************************
				* 작업예약재료 생성
				**********************************************************/
				jsStock.absolute(i);

				// 조회항목 record 생성
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_WBOOK_ID"    , ydWbookId   );
				jrParam.setField("STL_NO"         , commUtils.trim(jsStock.getRecord().getFieldString("STL_NO"       )));			
				jrParam.setField("YD_STK_COL_GP"  , commUtils.trim(jsStock.getRecord().getFieldString("YD_STK_COL_GP"))); // 적치열구분					
				jrParam.setField("YD_STK_BED_NO"  , commUtils.trim(jsStock.getRecord().getFieldString("YD_STK_BED_NO"))); // 적치BED번호					
				jrParam.setField("YD_STK_LYR_NO"  , commUtils.trim(jsStock.getRecord().getFieldString("YD_STK_LYR_NO"))); // 적치단번호					
				jrParam.setField("YD_UP_COLL_SEQ" , "1"         ); // 권상모음순서

				// 작업예약재료 테이블에 등록한다.
				commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.insYdWrkbookMtl", logId, mthdNm, "작업예약재료 등록(상차작업예약)");
				
				/*****************************************
				 * 대상재 TB_YD_STOCK 작업예약 수정
				 *****************************************/
				jrParam.setField("CAR_FRTOMOVE_WORD_NO"  , sFrtoMoveWordNo);
				/*
				UPDATE TB_YD_STOCK
				   SET MODIFIER    = :V_MODIFIER
				     , MOD_DDTT    = SYSDATE
				     , YD_WBOOK_ID = :V_YD_WBOOK_ID
				     , CAR_FRTOMOVE_WORD_NO = :V_CAR_FRTOMOVE_WORD_NO
				 WHERE STL_NO = :V_STL_NO
				   AND DEL_YN = 'N'     
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdStockYdWbookId", logId, mthdNm, "저장품 수정");
			}
			
			/*****************************************
			 * 대표 상차작업예약id 등록
			 *****************************************/
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CAR_SCH_ID"        , ydCarSchId);
			jrParam.setField("YD_CARLD_WRK_BOOK_ID" , ydWbookId1);
			/*
			UPDATE TB_YD_CARSCH
			   SET MODIFIER      = :V_MODIFIER
			     , MOD_DDTT      = SYSDATE
			     , YD_CARLD_ARR_DT      = NVL(:V_YD_CARLD_ARR_DT      , YD_CARLD_ARR_DT     )
			     , YD_CARUD_ARR_DT      = NVL(:V_YD_CARUD_ARR_DT      , YD_CARUD_ARR_DT     )
			     , SPOS_WLOC_CD         = NVL(:V_SPOS_WLOC_CD         , SPOS_WLOC_CD        )
			     , YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_CARLD_WRK_BOOK_ID , YD_CARLD_WRK_BOOK_ID)
			     , YD_CARUD_WRK_BOOK_ID = NVL(:V_YD_CARUD_WRK_BOOK_ID , YD_CARUD_WRK_BOOK_ID)
			     , YD_CAR_PROG_STAT     = NVL(:V_YD_CAR_PROG_STAT     , YD_CAR_PROG_STAT    )  
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID   
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarschArrStatWb", logId, mthdNm, "대표작업예약Id 등록");
			
			
			//출하공차 도착시 저장위치 제원 송신
			if (!"L".equals(ydCarUseGp)) {
				/**********************************************************
				* 저장위치제원정보 송신 (YDY5L001)
				**********************************************************/
				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
				sndL2Msg.setField("YD_INFO_SYNC_CD"		, "4"		 ); //야드정보동기화코드
				sndL2Msg.setField("YD_STK_COL_GP"    	, ydStkColGp );
				sndL2Msg.setField("YD_STK_BED_NO"       , "01"       );
				sndL2Msg.setField("YD_CAR_ARRSTRT_STAT" , "A"        ); //A:도착, S:출발
				sndL2Msg.setField("YD_CAR_PROG_STAT"    , "2"        );
				sndL2Msg.setField("YD_EQP_WRK_STAT"  	, "U"        );
				sndL2Msg.setField("TRN_EQP_CD"  	    , sTrnEqpCd  );
				
				jrRtn = commUtils.addSndData(jrRtn,coilDao.getMsgL2("YDY5L001_CarInfo", sndL2Msg)); 
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
	 * 오퍼레이션명 : 2열연 차량스케줄생성 -- 구내운송(상차)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param rcvMsg
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord mkY5CarSch(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "차량스케줄생성[CCoilCarMvSeEJB.mkY5CarSch] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			String ydCarSchId           = commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID" )); //차량스케줄ID
			String ydEqpId              = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); // 설비ID         
			String ydCarUseGp           = commUtils.trim(rcvMsg.getFieldString("YD_CAR_USE_GP" )); // 차량사용구분       
			String sTrnEqpCd            = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"    )); // 운송장비코드       
			String sWlocCd              = commUtils.trim(rcvMsg.getFieldString("WLOC_CD"       )); // 착지개소코드         
			String sPntDmdDt            = commUtils.trim(rcvMsg.getFieldString("PNT_DMD_DT"    )); // 포인트요구일시      
			String sSposWlocCd          = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"  )); // 발지개소코드       
			String sSposYdPntCd         = commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD")); // 발지포인트코드      
			String sCarColGp            = commUtils.trim(rcvMsg.getFieldString("CAR_COL_GP"    )); //상차도 ex)JAPT01

			String sModifier  	     	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); 	  //수정자(Backup Only)
			
			JDTORecord jrParam  = commUtils.getParam(logId, mthdNm, sModifier);
			
			String sMsg = "";

			// 발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)
			jrParam.setField("WLOC_CD"   , sSposWlocCd );
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
			JDTORecordSet jsStkCol = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStkcolWLocCdandPntCd", logId, mthdNm, "적치열조회");
			
			String ydCarldLevLoc = "";
			if (jsStkCol.size() > 0) {
				ydCarldLevLoc = jsStkCol.getRecord(0).getFieldString("YD_STK_COL_GP");// 열구분을 조회(발지위치)
			} else {
				sMsg = "등록되지 않은 개소코드와 포인트코드입니다. ex) A,B열연 OR 대기장";
				commUtils.printLog(logId, sMsg, "SL");
				ydCarldLevLoc = "";
			}

			/**********************************************************
			* 차량스케줄Id 생성
			**********************************************************/
			//String ydCarSchId      = coilDao.getSeqId(logId, mthdNm, "CarSch");

			/**********************************************************
			* 이송작업지시일자 순번 생성
			**********************************************************/
			String sFrtoMoveWordNo = coilDao.getSeqId(logId, mthdNm, "FtMvWo");
			
			// 차량스케줄INSERT 항목
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CAR_SCH_ID"        , ydCarSchId);
			jrParam.setField("YD_EQP_WRK_STAT"      , "U");
			jrParam.setField("YD_EQP_ID"            , ydEqpId);
			jrParam.setField("TRN_EQP_CD"           , sTrnEqpCd);
			jrParam.setField("YD_CAR_USE_GP"        , ydCarUseGp);
			jrParam.setField("CAR_KIND"             , "PT");
			jrParam.setField("WLOC_CD"              , sWlocCd  );
			jrParam.setField("SPOS_WLOC_CD"         , sWlocCd  );
			jrParam.setField("YD_BAYIN_WO_SEQ"      , CConstant.YD_BAYIN_WO_SEQ_DEFAULT);
			jrParam.setField("YD_CAR_PROG_STAT"     , "1");// 상차출발상태
			jrParam.setField("YD_CARLD_LEV_DT"      , commUtils.getDateTime14());// 상차출발일시
			jrParam.setField("FRTOMOVE_WORD_NO"     , sFrtoMoveWordNo);
			jrParam.setField("YD_CARLD_LEV_LOC"     , ydCarldLevLoc);
			jrParam.setField("YD_CARLD_STOP_LOC"    , sCarColGp);

			// 차량스케줄 등록
			/*
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
			commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insYdCarsch", logId, mthdNm, "차량스케줄 생성");
			
			if ("L".equals(ydCarUseGp)) { // 구내운송
				// 소재차량정지Point요구 호출
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("TRN_EQP_CD"          , sTrnEqpCd);// 운송장비코드
				jrParam.setField("WLOC_CD"             , sWlocCd);// 개소코드
				jrParam.setField("TRN_WRK_FULLVOID_GP" , "E");// 운송작업영공구분코드
				jrParam.setField("PNT_DMD_DT"          , sPntDmdDt);// 포인트요구일시
				
				JDTORecord jrTsYd = this.rcvTSYDJ002(jrParam);
				jrRtn = commUtils.addSndData(jrRtn, jrTsYd);
				
				sMsg = "소재차량정지Point요구 호출 완료";
				commUtils.printLog(logId, sMsg, "SL");
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
	 *      [A] 오퍼레이션명 : 상차지 정보 CLEAR
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procMatlCarCancel(JDTORecord rcvMsg)throws DAOException  {
		String mthdNm = "상차지 정보 CLEAR[CCoilCarMvSeEJB.procMatlCarCancel] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
	    
	    try{

			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			String sTrnEqpCd  		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 	  //운송장비코드
			String ydCarSchId 		= commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID")); //차량스케줄ID 				
			String ydCarldWrkBookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"));   //작업예약ID
			String sModifier  		= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); 	  //수정자(Backup Only)
			
			String sMsg      = "";

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd);			
				
			/*************************************************
			 * 예약된 차량정지POINT 삭제처리
			 *************************************************/	
			/* 
			UPDATE TB_YD_STKCOL
			   SET TRN_EQP_CD       = null
			     , YD_CAR_USE_GP    = null
			 WHERE TRN_EQP_CD       = :V_TRN_EQP_CD
			*/ 
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStkcolTrnEqpCdToNull", logId, mthdNm, "열수정");
			
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
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updDelCarWrMgtSchMtl", logId, mthdNm, "차량작업재료 수정");
			
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
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCarSchReverseYN", logId, mthdNm, "차량스케줄 삭제");
			
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
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delWrkbookMtl", logId, mthdNm, "작업예약 재료 삭제");
			
			/*
			UPDATE TB_YD_WRKBOOK
			   SET MOD_DDTT = SYSDATE
			     , MODIFIER = :V_MODIFIER
			     , DEL_YN   = :V_DEL_YN
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delYdWrkbook", logId, mthdNm, "작업예약 삭제");
			
			/*************************************************
			 * 저장품에서 작업예약ID와 스케줄코드 Clear시킴
			 *************************************************/
			/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStockDelYdWBookId 
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
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStockDelYdWBookId", logId, mthdNm, "저장품 초기화");
			
			
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
			JDTORecordSet jsPrepSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdPrepschByYdWbookId", logId, mthdNm, "준비스케쥴검색");
		
			sMsg = "작업예약ID["+ ydCarldWrkBookId +"] 준비스케쥴 검색 jsPrepSch.size() = "+ jsPrepSch.size();
			commUtils.printLog(logId, sMsg, "SL");
			
			if ( jsPrepSch.size() > 0 ) {
				
				/** 1.1 준비스케쥴 재료정보 복구 */

				jsPrepSch.first();
				String ydPrepSchId = commUtils.trim(jsPrepSch.getRecord().getFieldString("YD_PREP_SCH_ID"));
				
				jrParam.setField("YD_PREP_SCH_ID"	, ydPrepSchId);
				jrParam.setField("DEL_YN"			, "N");
				
				/*
				UPDATE TB_YD_PREPMTL
				   SET MODIFIER       = :V_MODIFIER
				     , MOD_DDTT       = SYSDATE
				     , DEL_YN         = :V_DEL_YN
				 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
				*/
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delYdPrepmtlByPrepSchId1", logId, mthdNm, "준비스케쥴재료 복구");
				
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
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delYdPrepsch1", logId, mthdNm, "준비스케쥴 복구");
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
	 *  [A] 오퍼레이션명 : 소재차량도착Point요구(TSYDJ002) - procMatlCarArrPntReq
	 *
	 * 	@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *  @param JDTORecord rcvMsg
	 *  @return JDTORecord
	 *  @throws DAOException
	 *  @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvTSYDJ002(JDTORecord rcvMsg) throws DAOException  {
		String mthdNm = "소재차량도착Point요구[CCoilCarMvSeEJB.rcvTSYDJ002] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
	    
	    try {

			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			/******************************
			 * 신규 구내운송 적용 여부
			 ******************************/
	    	//수신항목
			String msgId             = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sTrnEqpCd         = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"         )); //운송장비코드
			String sWlocCd           = commUtils.trim(rcvMsg.getFieldString("WLOC_CD"            )); //착지개소코드
			String sTrnWrkFullvoidGp = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분[상하차구분 E:공차상태(상차작업),F:영차(하차작업)]
			String sPntDmdDt         = commUtils.trim(rcvMsg.getFieldString("PNT_DMD_DT"         )); //포인트요구일시 
			
			String sModifier 	     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)

			if ("".equals(sModifier)) { sModifier = msgId; }

			String sMsg   = "";
			
			String sCurrDate = commUtils.getDateTime14();
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //리턴용
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
	    	JDTORecord jrParam  = commUtils.getParam(logId, mthdNm, sModifier);
	    	jrParam.setField("TRN_EQP_CD"         , sTrnEqpCd        );
	    	jrParam.setField("WLOC_CD"            , sWlocCd          );
	    	jrParam.setField("TRN_WRK_FULLVOID_GP", sTrnWrkFullvoidGp);
	    	jrParam.setField("PNT_DMD_DT"         , sPntDmdDt        );

	    	/**********************************************************
			* 1. 개소코드가 2열연이 아닐경우 종료
			**********************************************************/
	    	if (!this.getYdLocationInfo(sWlocCd)) {
				sMsg = "개소코드 오류 [" + sWlocCd + "]는 2열연 개소코드가 아닙니다.!" ;
				commUtils.printLog(logId, sMsg, "S-");
				return jrRtn ;
			}			
	    	
	    	/**********************************************************
			* 2. 운송장비코드로 차량스케줄 조회
			**********************************************************/
	    	/*
			SELECT *
			  FROM (
			        SELECT A.*
			              ,(SELECT YD_STKBED_USG_CD
			                  FROM TB_YD_STKCOL B
			                 WHERE B.YD_STK_COL_GP = A.YD_CARLD_STOP_LOC
			                   AND B.YD_STKBED_USG_CD IN ('A','D','E') --수출, 철송, 주문회
			               ) AS NEW_DEST_BAY
			          FROM TB_YD_CARSCH A
			         WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			           AND DEL_YN     ='N'
			         ORDER BY YD_CAR_SCH_ID DESC
			                , YD_CARUD_CMPL_DT DESC
			       ) A
			 WHERE ROWNUM <= 1
	    	 */
	    	JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarschByTrnEqpCd", logId, mthdNm, "차량스케줄 조회");
	    	
	    	if (jsCarSch.size() <= 0) {
	    		/**********************************************************
				* 2-1. 존재하지 않으면 종료
				**********************************************************/
				commUtils.printLog(logId, "차량스케줄 조회 실패", "S-");
				return jrRtn ;
	    	}
	    	
	    	/**********************************************************
			* 2-2. 차량스케줄의 차량진행상태에 따른 야드포인트코드가 0000인지 판단
			**********************************************************/
    		String ydCarSchId    = jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID");
    		String ydCarProgStat = jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT");
    		
    		String ydCarStopLoc	 = "";
			String ydStkColGp    = "";
			String ydPntCd       = "";
			
			/*************************************
			 *  대기장 도착전 포인트 요구
			 *************************************/
			String ydCarldStopLoc = jsCarSch.getRecord(0).getFieldString("YD_CARLD_STOP_LOC"); //상차위치
			String ydCarudStopLoc = jsCarSch.getRecord(0).getFieldString("YD_CARUD_STOP_LOC"); //하차위치
			
			if (("E".equals(sTrnWrkFullvoidGp) && "".equals(ydCarldStopLoc))
			 || ("F".equals(sTrnWrkFullvoidGp) && "".equals(ydCarudStopLoc))		
			) {
				jrParam.setField("TRN_WRK_FULLVOID_GP", sTrnWrkFullvoidGp);
				jrParam.setField("YD_CAR_SCH_ID"	  , ydCarSchId);
				jrParam.setField("SPOS_WLOC_CD"		  , sWlocCd   );
				jrParam.setField("TRN_EQP_CD"         , sTrnEqpCd );
				String ydBayGp = this.getMatlCarAimBayGp(jrParam);
				
				/***********************************************
		    	 * 소재차량Point지시 YDTSJ011
		    	 ***********************************************/
		    	JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
		    	jrYdMsg.setResultCode(logId);	//Logging 을 위한 ID
		    	jrYdMsg.setResultMsg(mthdNm);	//상위 Method 명
		    	
		    	jrYdMsg.setField("JMS_TC_CD"          , "YDTSJ011");
		    	jrYdMsg.setField("JMS_TC_CREATE_DDTT" , sCurrDate );
				jrYdMsg.setField("TRN_EQP_CD"         , sTrnEqpCd );
				jrYdMsg.setField("WLOC_CD"            , sWlocCd);
				jrYdMsg.setField("YD_PNT_CD"          , "0000"    );
				jrYdMsg.setField("PNT_WO_GP"          , "A"       );
				jrYdMsg.setField("PNT_WO_DT"          , sCurrDate );
				jrYdMsg.setField("YD_BAY_GP"          , ydBayGp   );
				
				/**********************************************************
				 * 2열연 제품 2통로, 핫슬라브이송, PT 차량 여부 판단
				 *    - 차량코드에 'PT'가 포함된 경우 핫슬라브 이송 차량으로 처리
				 *    - PT 차량은 대기장 도착 없이 포인트 전송
				 **********************************************************/		
				commUtils.printLog(logId, "CICD 2026.05.06 PT 차량 포인트 요구 처리 (핫슬라브 이송)", "SL");
				String sAPP001_028_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "028");
				if ("Y".equals(sAPP001_028_YN)) {
					boolean bIsPtCar = sTrnEqpCd != null && sTrnEqpCd.indexOf("PT") >= 0;
					commUtils.printLog(logId, "3. 핫슬라브 팔레트 PT차량  여부 : " + (bIsPtCar ? "Y (핫슬라브 이송차량)" : "N (일반차량)"), "SL");
					if (bIsPtCar) {
						JDTORecord jrStopPntParam = commUtils.getParam(logId, mthdNm, sModifier);
						jrStopPntParam.setField("YD_CAR_SCH_ID", ydCarSchId);
						JDTORecordSet jsRstPt = commDao.select(jrStopPntParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getListloadStoppointGDh", logId, mthdNm, "핫슬라브 팔레트 PT차량  포인트 조회");
						String sYdPntCd = "0000";     // default
						if (jsRstPt.size() > 0) {							
							sYdPntCd    = commUtils.nvl(jsRstPt.getRecord(0).getFieldString("YD_PNT_CD"  ), "0000");							
						}
						jrYdMsg.setField("YD_PNT_CD"          , sYdPntCd    );
					}
				}			
				
			    jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
			    
			    commUtils.printLog(logId, mthdNm, "S-");
			    return jrRtn;
			}
			
			/*
			 * 상차출발
			 */
    		if ("1".equals(ydCarProgStat)) { 
    			ydCarStopLoc = jsCarSch.getRecord(0).getFieldString("YD_CARLD_STOP_LOC");
	    		ydStkColGp   = jsCarSch.getRecord(0).getFieldString("YD_CARLD_STOP_LOC");
	    		ydPntCd      = jsCarSch.getRecord(0).getFieldString("YD_PNT_CD1"       );
    		}
    		
    		/*
    		 * 하차출발
    		 */
    		if ("A".equals(ydCarProgStat)) { 
    			
    			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
    			
    			/* 
				SELECT D.YD_STK_COL_GP 
				     , D.WLOC_CD
				     , D.YD_PNT_CD
				  FROM USRYDA.TB_YD_CARFTMVMTL   A
				     , USRTSA.TB_TS_MATL_FTMV_WO B
				     , USRYDA.TB_YD_STKCOL       D
				     , USRYDA.TB_YD_STOCK        ST     
				 WHERE A.STL_NO          = B.STL_NO
				   AND D.WLOC_CD         = B.ARR_WLOC_CD
				   AND D.YD_LOC_GP       = 'H'
				   AND B.TRN_WRK_MTL_GP  = 'H' --소재
				   AND A.YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
				   AND D.YD_STKBED_USG_CD IN('GT', 'TO')
				   AND B.TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO)
				                              FROM USRTSA.TB_TS_MATL_FTMV_WO C
				                             WHERE B.STL_NO         = C.STL_NO
				                               AND C.TRN_WRK_MTL_GP = 'H'
				                            )
				   AND D.DEL_YN          = 'N'	
				   AND A.STL_NO          = ST.STL_NO
				   AND ST.YD_AIM_BAY_GP  = D.YD_BAY_GP
				 GROUP BY D.YD_STK_COL_GP 
				        , D.WLOC_CD
				        , D.YD_PNT_CD
				 ORDER BY CASE WHEN SUBSTR(D.YD_STK_COL_GP,6,1) = '3'        THEN 1
				               WHEN SUBSTR(D.YD_STK_COL_GP,6,1) IN ('2','1') THEN 2
				               WHEN SUBSTR(D.YD_STK_COL_GP,6,1) IN ('4','5') THEN 3
				               ELSE 4 END                          
				 */
				JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getListloadStoppointGDh", logId, mthdNm, "차량포인트 조회");
				if (jsRst.size() > 0) {
					ydCarStopLoc = jsRst.getRecord(0).getFieldString("YD_STK_COL_GP");
		    		ydStkColGp   = jsRst.getRecord(0).getFieldString("YD_STK_COL_GP");
		    		ydPntCd      = jsCarSch.getRecord(0).getFieldString("YD_PNT_CD3");
				} 
    		}
    		
    		commUtils.printLog(logId, "▷ydCarStopLoc : "+ydCarStopLoc, "SL");
    		commUtils.printLog(logId, "▷ydStkColGp   : "+ydStkColGp  , "SL");
    		commUtils.printLog(logId, "▷ydPntCd      : "+ydPntCd     , "SL");
    		
    		String ydSchCd       = "";
			String ydWrkBookId   = "";
			String ydAimRtGp     = "";
			String ydGp          = "";
			
			/**********************************************************
			* 2-2-2. 0000
			**********************************************************/
//			String sArrYn = "N"; //도착처리 여부
	    	if ("0000".equals(ydPntCd)) {
		    	/*
				 * 적치열구분이 존재하는 경우에만 가용 point를 조회 시작
				 */
		    	if (!"".equals(ydStkColGp)) {
		    		
		    		jrParam  = commUtils.getParam(logId, mthdNm, sModifier);
		    		
	    			jrParam.setField("YD_STK_COL_GP", ydStkColGp.substring(0,4));
		    		jrParam.setField("WLOC_CD"      , sWlocCd);
		    		
		    		/* 
					SELECT SC.YD_STK_COL_GP
					     , SC.YD_GP
					     , SC.YD_BAY_GP
					     , SC.YD_EQP_GP
					     , SC.YD_STK_COL_NO
					     , SC.YD_STK_COL_ACT_STAT
					     , SC.YD_STK_COL_RULE_YAXIS
					     , SC.YD_STK_COL_W
					     , SC.YD_STK_COL_L
					     , SC.YD_CAR_USE_GP
					     , SC.TRN_EQP_CD
					     , SC.CAR_NO
						 , SC.CARD_NO
					     , SC.WLOC_CD
					     , SC.YD_PNT_CD
					     , SC.YD_STK_COL_W_GP
					     , SC.YD_STK_COL_H_MAX
					     , SC.YD_STK_COL_BED_L_TP
					     , CP.YD_STK_COL_ACT_STAT AS CARPOINT_STAT
					  FROM TB_YD_STKCOL   SC
					     , TB_YD_CARPOINT CP
					 WHERE SC.YD_STK_COL_GP LIKE :V_YD_STK_COL_GP|| '%'
					   AND SC.WLOC_CD = :V_WLOC_CD 
					   AND SC.DEL_YN  = 'N' 
					   AND SC.YD_STK_COL_GP = CP.YD_STK_COL_GP
					   AND CP.YD_CAR_USETYPE_GP IN('GT', 'TO')
					   AND CP.DEL_YN  = 'N' 
					   AND SC.YD_LOC_GP  = CASE WHEN (SELECT ITEM1 FROM TB_YD_RULE WHERE REPR_CD_GP = 'APP002') = 'Y' THEN SC.YD_LOC_GP
					                                   ELSE 'H' END
					 ORDER BY CASE WHEN SUBSTR(SC.YD_STK_COL_GP,6,1) = '3'        THEN 1
					               WHEN SUBSTR(SC.YD_STK_COL_GP,6,1) IN ('2','1') THEN 2
					               WHEN SUBSTR(SC.YD_STK_COL_GP,6,1) IN ('4','5') THEN 3
					               ELSE 4 END  
		    		 */
		    		JDTORecordSet jsStkCol = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStkcolColGpLikeTong", logId, mthdNm, "적치열 조회");
		    		
		    		if (jsStkCol.size() <= 0) {
		    			commUtils.printLog(logId, "가용POINT 유무 판단을 위해 적치열 조회 실패", "S-");
		    			return jrRtn;
		    		}
		    		
		    		
		    		
		    		String ydStkColActStat = "";
		    		String sTrnEqpCdForCol = "";
		    		String sCarpointStat   = "";
		    				
		    		String sWlocCd1      = ""; 
		    		
		    		for (int ii = 1; ii <= jsStkCol.size(); ++ii) {
		    			jsStkCol.absolute(ii);

//		    			ydStkColWlocCd  = commUtils.trim(jsStkCol.getRecord().getFieldString("WLOC_CD"            ));
				    	ydPntCd         = commUtils.trim(jsStkCol.getRecord().getFieldString("YD_PNT_CD"          ));
				    	ydStkColGp      = commUtils.trim(jsStkCol.getRecord().getFieldString("YD_STK_COL_GP"      ));
				    	ydStkColActStat = commUtils.trim(jsStkCol.getRecord().getFieldString("YD_STK_COL_ACT_STAT"));
				    	sTrnEqpCdForCol = commUtils.trim(jsStkCol.getRecord().getFieldString("TRN_EQP_CD"         )); //적치열 운송코드
				    	sCarpointStat   = commUtils.trim(jsStkCol.getRecord().getFieldString("CARPOINT_STAT"      ));

				    	if (ii == 1) {
				    		sWlocCd1     = commUtils.trim(jsStkCol.getRecord().getFieldString("WLOC_CD"      ));
				    	}
		    			
				    	commUtils.printLog(logId, "ydPntCd        "+ydPntCd        , "SL");
				    	commUtils.printLog(logId, "ydStkColGp     "+ydStkColGp     , "SL");
				    	commUtils.printLog(logId, "ydStkColActStat"+ydStkColActStat, "SL");
				    	commUtils.printLog(logId, "sTrnEqpCdForCol"+sTrnEqpCdForCol, "SL");
				    	commUtils.printLog(logId, "sCarpointStat  "+sCarpointStat  , "SL");
				    	commUtils.printLog(logId, "sWlocCd1       "+sWlocCd1       , "SL");
				    	
				    	/*------------------------------------------------------------------------------------------------------
				    	 * 적치열의 활성상태가 비활성화[C]이고 차량 예약된 적치열이 아니면 
				    	 * 해당POINT에 운송장비코드로 미리 예약을 하고 구내운송으로 전송처리
				    	 ------------------------------------------------------------------------------------------------------*/
		    			if ("C".equals(sCarpointStat)) {
		    				if ("".equals(sTrnEqpCdForCol)) {
		    					//적치열에 해당 운송장비코드를 예약 등록
		    					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		    					jrParam.setField("TRN_EQP_CD"         , sTrnEqpCd );
		    					jrParam.setField("YD_STK_COL_GP"      , ydStkColGp);
	    						jrParam.setField("YD_STK_COL_ACT_STAT", "L"); // 도착
		    					
		    					/*
								UPDATE TB_YD_CARPOINT
								   SET MODIFIER = :V_MODIFIER
								     , MOD_DDTT = SYSDATE   
								     , YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
								     , TRN_EQP_CD          = :V_TRN_EQP_CD
								 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
		    					 */
		    					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updCarPointTrnEqpCd", logId, mthdNm, "TB_YD_CARPOINT 예약");
		    					commUtils.printLog(logId, "적치열구분 운송장비코드 예약 성공", "SL");
		    					break;
		    				} else {
		    					commUtils.printLog(logId, "적치열구분 운송장비코드로 예약되어 있음", "SL");
		    				}
		    			}
				    	
		    			/*------------------------------------------------------------------------------------------------------
				    	 * 적치열활성상태가 모두 활성인 경우 첫번째 위치를 등록하도록한다.          
				    	 *   ==> 대기장 포인트[0000] 코드를 전송하도록 변경 완료          
				    	 ------------------------------------------------------------------------------------------------------*/
				    	if (ii == jsStkCol.size()) {
				    		sWlocCd      = sWlocCd1;
				    		ydPntCd      = "0000";
				    	}
				    	
		    		} // end for
		    		
		    	} // end - if (!"".equals(ydStkColGp))
	    	} //if ("0000".equals(ydPntCd))
	    	
	    	commUtils.printLog(logId, "sWlocCd       "+sWlocCd       , "SL");
	    	commUtils.printLog(logId, "ydPntCd       "+ydPntCd       , "SL");
	    	commUtils.printLog(logId, "ydStkColGp    "+ydStkColGp    , "SL");
	    	
	    	jrParam  = commUtils.getParam(logId, mthdNm, sModifier);
	    	jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
	    	
	    	/*************************************************
	    	 * 차량스케줄 수정
	    	 *************************************************/
	    	jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
	    	
	    	if ("E".equals(sTrnWrkFullvoidGp)) {
	    		jrParam.setField("YD_CARLD_PNT_WO_DT",  commUtils.getDateTime14());	    
	    		jrParam.setField("YD_PNT_CD1"    	 , ydPntCd);
				jrParam.setField("YD_CARLD_STOP_LOC" , ydStkColGp);
				jrParam.setField("YD_EQP_WRK_STAT"   , "U");
	    	} else {
	    		jrParam.setField("YD_CARUD_PNT_WO_DT",  commUtils.getDateTime14());
	    		jrParam.setField("YD_PNT_CD3"    	 , ydPntCd);
				jrParam.setField("YD_CARUD_STOP_LOC" , ydStkColGp);
				jrParam.setField("YD_EQP_WRK_STAT"   , "L");
	    	}
	    	
	    	/*
			UPDATE TB_YD_CARSCH
			   SET MOD_DDTT = SYSDATE
			     , MODIFIER = :V_MODIFIER
			     , SPOS_WLOC_CD       = NVL(:V_SPOS_WLOC_CD      , SPOS_WLOC_CD      )
			     , YD_CARLD_STOP_LOC  = NVL(:V_YD_CARLD_STOP_LOC , YD_CARLD_STOP_LOC )
			     , YD_CARLD_PNT_WO_DT = NVL(:V_YD_CARLD_PNT_WO_DT, YD_CARLD_PNT_WO_DT)
			     , YD_PNT_CD1         = NVL(:V_YD_PNT_CD1        , YD_PNT_CD1        )
			     , ARR_WLOC_CD        = NVL(:V_ARR_WLOC_CD       , ARR_WLOC_CD       )
			     , YD_CAR_PROG_STAT   = NVL(:V_YD_CAR_PROG_STAT  , YD_CAR_PROG_STAT  )
			     , YD_CARUD_LEV_DT    = NVL(:V_YD_CARUD_LEV_DT   , YD_CARUD_LEV_DT   )
			     , YD_CARUD_STOP_LOC  = NVL(:V_YD_CARUD_STOP_LOC , YD_CARUD_STOP_LOC )
			     , YD_CARUD_PNT_WO_DT = NVL(:V_YD_CARUD_PNT_WO_DT, YD_CARUD_PNT_WO_DT)
			     , YD_PNT_CD3         = NVL(:V_YD_PNT_CD3        , YD_PNT_CD3        )
			     , YD_EQP_WRK_STAT    = NVL(:V_YD_EQP_WRK_STAT   , YD_EQP_WRK_STAT   ) --상차U 하차 L로 세팅
			     , WAIT_ARR_DDTT      = NVL(:V_WAIT_ARR_DDTT     , WAIT_ARR_DDTT     ) --대기장 도착시간
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	    	 */
	    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCarSchTSYDJ002", logId, mthdNm, "차량스케줄 수정");
	    	
	    	
	    	/***********************************************
	    	 * 소재차량Point지시 YDTSJ011
	    	 ***********************************************/
	    	JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
	    	jrYdMsg.setField("JMS_TC_CD"      , "YDTSJ011"    );
			jrYdMsg.setField("YD_CAR_SCH_ID"  , ydCarSchId    );
			jrYdMsg.setField("YD_SCH_CD"      , ydSchCd       );
			jrYdMsg.setField("YD_STK_COL_GP"  , ydStkColGp    );
			jrYdMsg.setField("PNT_WO_DT"      , commUtils.getDateTime14());
			
		    jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDTSJ011", jrYdMsg));
	    	
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}

	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량도착(TSYDJ003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYDJ003(JDTORecord rcvMsg)throws DAOException  {
		String mthdNm    = "소재차량도착[CCoilCarMvSeEJB.rcvTSYDJ003] < " + rcvMsg.getResultMsg();
		String logId     = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
	    try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			/******************************
			 * 신규 구내운송 적용 여부
			 ******************************/
//			String sAPP900 = coilDao.ApplyYn(logId, mthdNm, "APP900","J","*"); //구내운송 신모듈 적용 여부
			
	    	//수신항목 변수 저장
			String msgId             = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sTrnEqpCd         = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"         )); //운송장비코드
			String sArrWlocCd        = commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD"        )); //착지개소코드
			String sArrYdPntCd       = commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD"      )); //착지야드포인트코드
			String sTrnWrkFullvoidGp = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			String sTrnEqpStkCapa    = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA"   )); //운송장비적재능력
			String sCarArrDt         = commUtils.trim(rcvMsg.getFieldString("CAR_ARR_DT"         )); //차량도착일시
			String sMsgGp            = commUtils.trim(rcvMsg.getFieldString("MSG_GP"             )); //전문구분
			
			String sModifier         = commUtils.trim(rcvMsg.getFieldString("MODIFIER"           )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			String sMsg = "";
			 
			/**********************************************************
			 * 0. 수신 항목 값 체크
			 **********************************************************/
	    	if (!this.getYdLocationInfo(sArrWlocCd)) {
				sMsg = "개소코드 오류 [" + sArrWlocCd + "]는 2열연 개소코드가 아닙니다.!" ;
				commUtils.printLog(logId, sMsg, "S-");
				return jrRtn ;
			}
	    	
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);

			jrParam.setField("JMS_TC_CD"          , msgId            );
			jrParam.setField("TRN_EQP_CD"         , sTrnEqpCd        );
			jrParam.setField("ARR_WLOC_CD"        , sArrWlocCd       );
			jrParam.setField("ARR_YD_PNT_CD"      , sArrYdPntCd      );
			jrParam.setField("TRN_WRK_FULLVOID_GP", sTrnWrkFullvoidGp);
			jrParam.setField("TRN_EQP_STK_CAPA"   , sTrnEqpStkCapa   );
			jrParam.setField("CAR_ARR_DT"         , sCarArrDt        );
			jrParam.setField("MSG_GP"             , sMsgGp           );
			
			/**********************************************************
			 * 1. 운송장비코드로 차량스케줄 조회 
			 **********************************************************/
			commUtils.printLog(logId, "1. 운송장비코드로 차량스케줄 조회", "SL");
			/* 
			SELECT *
			  FROM (
			        SELECT A.*
			              ,(SELECT YD_STKBED_USG_CD
			                  FROM TB_YD_STKCOL B
			                 WHERE B.YD_STK_COL_GP = A.YD_CARLD_STOP_LOC
			                   AND B.YD_STKBED_USG_CD IN ('A','D','E') --수출, 철송, 주문회
			               ) AS NEW_DEST_BAY
			          FROM TB_YD_CARSCH A
			         WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			           AND DEL_YN     ='N'
			         ORDER BY YD_CAR_SCH_ID DESC
			                , YD_CARUD_CMPL_DT DESC
			       ) A
			 WHERE ROWNUM <= 1
			 */
			JDTORecordSet jsCarsch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarschByTrnEqpCd", logId, mthdNm, "차량스케줄 조회");
			if (jsCarsch.size() > 1) {
				commUtils.printLog(logId, "차량스케줄 조회 오류 ["+jsCarsch.size()+"건]", "S-");
				return jrRtn;
			}
			
			jsCarsch.first();
			JDTORecord jrCarSch = jsCarsch.getRecord();
			
			String ydCarSchId      = commUtils.trim(jrCarSch.getFieldString("YD_CAR_SCH_ID"    ));
			String ydCarProgStat   = commUtils.trim(jrCarSch.getFieldString("YD_CAR_PROG_STAT" ));
//			String ydCarldStopLoc  = commUtils.trim(jrCarSch.getFieldString("YD_CARLD_STOP_LOC"));
			String sFrtomoveWordNo = commUtils.trim(jrCarSch.getFieldString("FRTOMOVE_WORD_NO" )); //운송지시번호
			
	    	String ydPntCd         = commUtils.trim(jrCarSch.getFieldString("YD_PNT_CD1"       ));
	    	
	    	jrParam.setField("YD_CAR_SCH_ID"           , ydCarSchId     );
	    	jrParam.setField("YD_CAR_PROG_STAT"        , ydCarProgStat  );
//	    	jrParam.setField("YD_CARLD_STOP_LOC"       , ydCarldStopLoc );
	    	jrParam.setField("YD_PNT_CD1"              , ydPntCd        );
	    	jrParam.setField("FRTOMOVE_WORD_NO"        , sFrtomoveWordNo);
	    	
			//도착전문이 수정이 아니고 상차도착/하차도착인 경우에는 업무종료처리
	    	
			if (!"U".equals(sMsgGp)) {
				if ("2".equals(ydCarProgStat) || "B".equals(ydCarProgStat)) { //상차도착, 하차도착
					commUtils.printLog(logId, "운송장비코드 : "+ sTrnEqpCd    , "SL");
					commUtils.printLog(logId, "차량스케줄   : "+ ydCarSchId   , "SL");
					commUtils.printLog(logId, "차량진행상태 : "+ ydCarProgStat, "SL");
					commUtils.printLog(logId, "이미 도착처리된 상태입니다."   , "S-");
					return jrRtn;
				}
			}
			
	    	jrParam.setField("YD_CAR_USE_GP", "L"      );//L : 구내운송 , G : 출하차량
	    	jrParam.setField("YD_EQP_ID"    , "XXPT01" );
	    	jrParam.setField("TRN_EQP_CD"   , sTrnEqpCd );
			jrParam.setField("WLOC_CD"      , sArrWlocCd);
			
	    	/**********************************************************
			* 대기장 도착 전이면 도착처리 불가능
			**********************************************************/
			String sWaitArrDdtt = commUtils.nvl(jsCarsch.getRecord(0).getFieldString("WAIT_ARR_DDTT"), ""); //대기장도착시간
			
	    	/**********************************************************
			 * 2열연 제품 2통로, 핫슬라브이송, PT 차량 여부 판단
			 *   - 차량코드에 'PT'가 포함된 경우 핫슬라브 이송 차량으로 처리
			 *   - PT 차량은 대기장 도착 (TSYDJ005) 없이 바로 도착
			 *   APP001-J-028 플래그 Y + PT 차량이면 WAIT_ARR_DDTT 없을 때
			 *   현재시간으로 채워 체크를 통과시킴
			 **********************************************************/
			commUtils.printLog(logId, "CICD 2026.05.06 PT 차량 영차도착 처리 (핫슬라브 이송)", "SL");
			String sAPP001_028_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "028");
			if ("Y".equals(sAPP001_028_YN)) {
				boolean bIsPtCar = sTrnEqpCd != null && sTrnEqpCd.indexOf("PT") >= 0;
				commUtils.printLog(logId, "3. 핫슬라브 팔레트 PT차량  여부 : " + (bIsPtCar ? "Y (핫슬라브 이송차량)" : "N (일반차량)"), "SL");
				if (bIsPtCar && "".equals(sWaitArrDdtt)) {
				// PT 차량은 대기장 도삼이 없으므로 현재시간으로 대체하여 도삼처리 허용
					sWaitArrDdtt = commUtils.getDateTime14();
					commUtils.printLog(logId, "핫슬라브 팔레트 PT차량  - 대기장 도착시간 현재시간으로 대체[" + sWaitArrDdtt + "]", "SL");
				}
			}
			
			if ("".equals(sWaitArrDdtt)) {
				throw new DAOException("대기장 도착(TSYDJ005)전문 수신이 되지 않았습니다.");
			}
			
			/********************************************
			 * 2. 도착 상차도조회
			 ********************************************/
			commUtils.printLog(logId, "2. 도착 상차도조회", "SL");
			
			jrParam.setField("WLOC_CD"      , sArrWlocCd);
			jrParam.setField("YD_PNT_CD"    , sArrYdPntCd);
			
			/*
			SELECT *
			  FROM TB_YD_CARPOINT
			 WHERE DEL_YN    = 'N'
			   AND WLOC_CD   = :V_WLOC_CD
			   AND YD_PNT_CD = :V_YD_PNT_CD
			 */
			JDTORecordSet jsBayInPnt = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getCarBayInPnt", logId, mthdNm, "입동포인트 조회");
			if (jsBayInPnt.size() <= 0) {
				commUtils.printLog(logId, "도착차량포인트 이상"   , "S-");
				return jrRtn;
			}
				
			String sBayInPnt = jsBayInPnt.getRecord(0).getFieldString("YD_STK_COL_GP");
			
			if ("E".equals(sTrnWrkFullvoidGp)) {
				jrParam.setField("YD_PNT_CD1"    		, sArrYdPntCd);
				jrParam.setField("YD_CARLD_STOP_LOC"	, sBayInPnt);
			} else {
				jrParam.setField("YD_PNT_CD3"    		, sArrYdPntCd);
				jrParam.setField("YD_CARUD_STOP_LOC"	, sBayInPnt);
			}
			
			/*
			UPDATE TB_YD_CARSCH
			   SET MODIFIER      = :V_MODIFIER
			     , MOD_DDTT      = SYSDATE
			     , YD_CARLD_STOP_LOC = NVL(:V_YD_CARLD_STOP_LOC, YD_CARLD_STOP_LOC)
			     , YD_PNT_CD1        = NVL(:V_YD_PNT_CD1       , YD_PNT_CD1       )
			     , YD_CARUD_STOP_LOC = NVL(:V_YD_CARUD_STOP_LOC, YD_CARUD_STOP_LOC)
			     , YD_PNT_CD3        = NVL(:V_YD_PNT_CD3       , YD_PNT_CD3       )
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarschYdCoil2", logId, mthdNm, "상차도변경");
			
			/***************************************
			 * 2열연 제품 2통로, 핫슬라브이송, PT 차량 여부 판단 
			 * - APP001-J-028 관련 플래그처리
			 * - 차량코드에 'PT'가 포함된 경우 핫슬라브 이송 차량으로 처리
			 * - PT 차량(핫슬라브 이송) 영차 도착 처리
			 * - 차량코드에 PT 포함 시 하차 없이 차량 및 슬라브 포인트 유지
			 * - 하차개시, 하차완료 전문만 전송
			 ***************************************/
			commUtils.printLog(logId, "CICD 2026.05.06 PT 차량 영차도착 처리 (핫슬라브 이송)", "SL");
			// String sAPP001_028_YN = coilDao.ApplyYn(logId, mthdNm, "APP001", "J", "028");
			// sAPP001_028_YN, bIsPtCar 는 대기장 도착 체크 블록에서 이미 선언
			
			if ("Y".equals(sAPP001_028_YN)) {
				/**********************************************************
				 * 3. PT 차량 여부 판단
				 *    - 차량코드에 'PT'가 포함된 경우 핫슬라브 이송 차량으로 처리
				 *    - PT 차량은 도착 즉시 슬라브 적치 유지 (실제 하차작업 없음)
				 *    - 차량 스케줄은 완료처리, 차량출발 전문 올 때까지 포인트 유지
				 **********************************************************/
				boolean bIsPtCar = sTrnEqpCd != null && sTrnEqpCd.indexOf("PT") >= 0;
				commUtils.printLog(logId, "3. 핫슬라브 팔레트 PT차량  여부 : " + (bIsPtCar ? "Y (핫슬라브 이송차량 - 하차없이 적치유지)" : "N (일반차량)"), "SL");

				if (bIsPtCar) {
					if ("E".equals(sTrnWrkFullvoidGp)) { // E 공차 상차
						/**********************************************************
						 * [PT 차량 - 공차(상차) 도착 처리]
						 *  - E=공차: 상차 목적으로 소재 포인트에 비어서 도착
						 *  - 포인트 적치 슬라브 조회 후 상차개시/상차완료 전문 발송
						 *  - 차량/슬라브는 포인트에 그대로 유지
						 **********************************************************/
						commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 공차(상차) 도착 처리 시작", "SL");
						return this.procPtCarUDArrival(logId, mthdNm, sModifier, ydCarSchId, sBayInPnt, sArrYdPntCd, sTrnEqpCd, sTrnEqpStkCapa, jrRtn);
					} else { // F 영차 하차
						/**********************************************************
						 * [PT 차량 - 영차(하차) 도착 처리]
						 *  - 화물 실어 도착 → 이송실적 완료처리
						 *  - procPtCarArrival 기존 로직 유지
						 **********************************************************/
						commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 영차(하차) 도착 처리 시작", "SL");
						return this.procPtCarArrival(logId, mthdNm, sModifier, ydCarSchId, sBayInPnt, sTrnEqpCd, sTrnEqpStkCapa, jrRtn);
					}
				}

			}

	    	/**********************************************************
			 * 3. 작업예약 생성 
			 **********************************************************/
			commUtils.printLog(logId, "3. 작업예약 생성"   , "SL");
	    	if ("E".equals(sTrnWrkFullvoidGp)) {

	    		jrParam.setField("YD_CARLD_STOP_LOC", sBayInPnt);
	    		jrParam.setField("ARR_YN"           , "Y"      ); //도착여부 
	    		
				JDTORecord jrYdMsg = this.procCHrCarLdWrkReq(jrParam);
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				
	    	} else if ("F".equals(sTrnWrkFullvoidGp)) {
	    		
	    		jrParam.setField("YD_CARUD_STOP_LOC", sBayInPnt);
	    		JDTORecord jrYdMsg = this.makeCarUdWrkBookAB(jrParam);
	    		jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
	    	}
			
			
	    	/**********************************************************
			 * 3. 작업예약을 조회한다. 
			 *    (차량사용구분과 운송장비코드로 작업예약을 조회한다.) 
			 **********************************************************/
	    	/* 
			SELECT A.YD_WBOOK_ID
			     , A.YD_SCH_CD
			     , A.STL_NO
			     , A.YD_UP_COLL_SEQ
			     , A.YD_STK_COL_GP
			     , NVL(A.YD_STK_BED_NO,ROW_NO)  AS YD_STK_BED_NO
			     , A.YD_STK_LYR_NO
			  FROM (
			        SELECT B.YD_WBOOK_ID    AS YD_WBOOK_ID
			             , B.YD_SCH_CD
			             , A.STL_NO         AS STL_NO
			             , A.YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ
			             , A.YD_STK_COL_GP  AS YD_STK_COL_GP
			             , A.YD_STK_BED_NO  AS YD_STK_BED_NO
			             , A.YD_STK_LYR_NO  AS YD_STK_LYR_NO
			             , LPAD(ROWNUM,2,'0') AS ROW_NO
			          FROM TB_YD_WRKBOOKMTL A
			             ,(SELECT *
			                 FROM (SELECT WB.YD_WBOOK_ID
			                            , WB.YD_SCH_CD
			                         FROM TB_YD_WRKBOOK   WB
			                            , TB_YD_CARPOINT  CP 
			                        WHERE WB.DEL_YN = 'N'
			                          AND WB.YD_CAR_USE_GP = :V_YD_CAR_USE_GP
			                          AND WB.TRN_EQP_CD    = :V_TRN_EQP_CD
			                          --실제 입동한 동과 작업예약 동과 같은 작업예약 검색
			                          AND CP.YD_GP = 'J'
			                          AND WB.TRN_EQP_CD = CP.TRN_EQP_CD
			                          AND WB.YD_BAY_GP  = CP.YD_BAY_GP
			                          AND CP.DEL_YN     = 'N'
			                        ORDER BY YD_WBOOK_ID DESC
			                       ) C
			                 WHERE 1 = 1 
			                ) B
			          WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
			          ORDER BY YD_UP_COLL_SEQ DESC
			                 , YD_STK_COL_GP
			                 , YD_STK_BED_NO
			                 , YD_STK_LYR_NO
			       ) A
			 WHERE 1 = 1
	    	*/ 
	    	JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getWrkBookMtlbyCarUsrGpTrnEqpCd", logId, mthdNm, "작업예약 조회");
	    	
	    	if (jsWrkBook.size() == 0) {
				commUtils.printLog(logId, "직상차 도착이거나 대상재없이 공차도착인 경우 처리", "SL");
			}

	    	String ydWbookId  = ""; 
	    	String ydSchCd    = "";
	    	
	    	if (jsWrkBook.size() > 0) {
	    		//상차작업예약id 및 하차작업예약id로 등록하기 위해...
		    	ydWbookId = commUtils.trim(jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID"));
		    	ydSchCd   = commUtils.trim(jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  ));
	    	}
	    	
	    	/**********************************************************
			 * 4. 공차도착 및 영차도착 실적
			 **********************************************************/
	    	commUtils.printLog(logId, "4. 공차도착 및 영차도착 실적", "SL");
	    	if ("E".equals(sTrnWrkFullvoidGp)) {
	    		//공차도착
	    		jrParam.setField("YD_CARLD_WRK_BOOK_ID", ydWbookId);
	    		jrParam.setField("YD_SCH_CD"           , ydSchCd);
	    		
	    		jrRtn = commUtils.addSndData(jrRtn, this.procLDMatlCarArr(jrParam));

	    	} else {
	    		//영차도착
	    		jrParam.setField("YD_CARUD_WRK_BOOK_ID", ydWbookId);
	    		jrParam.setField("TRN_EQP_STK_CAPA"    , sTrnEqpStkCapa);

	    		jrRtn = commUtils.addSndData(jrRtn, this.procUDMatlCarArr(jrParam));//TODO
	    	} 
	    	
			/**********************************************************
			 * 99. 차량작업 예정정보 송신 YDY5L008
			 **********************************************************/
	    	commUtils.printLog(logId, "5. 차량예정정보 송신", "SL");
	    	JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
	    	sndL2Msg.setField("JMS_TC_CD"       , "YDY5L008");
			sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
			sndL2Msg.setField("SEARCH_FLAG"     , "2"       ); //1:상차도, 2:차량스케쥴 ID
			sndL2Msg.setField("YD_CAR_SCH_ID"   , ydCarSchId); //차량스케줄
			sndL2Msg.setField("PT_LOAD_LOC"     , sBayInPnt ); //상차도
			
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
	 *      [A] 오퍼레이션명 : PT 팔레트, 연주 - 2열연 핫슬라브 이송 차량도착처리 -- procPtCarArrival
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * 핫슬라브 팔레트 PT차량  도착 처리 (procPtCarArrival) < 소재차량도착 (rcvTSYDJ003)
	 *
	 * 핫슬라브 팔레트 PT차량 (이송실적 시스템) 도착 수신 시 전용 처리:
	 *   1. TB_YD_CARFTMVMTL 이송재료 목록 조회
	 *   2. 이송실적(TB_PT_STLFRTOMOVE) 완료처리
	 *   3. 주편공통(TB_PT_MSLABCOMM) 진행상태 갱신 및 PT 전문 발송
	 *   4. Slab공통(TB_PT_SLABCOMM) 진행상태 갱신 및 PT 전문 발송
	 *   5. 적치열(TB_YD_STKCOL) 이전 차량 클리어
	 *   6. 차량포인트(TB_YD_CARPOINT) 클리어 및 새 포인트 등록
	 *   7. 스판(TB_YD_STKBED) 활성화
	 *   8. 적치단(TB_YD_STKLYR) 업데이트
	 *   9. 주편공통(TB_PT_MSLABCOMM) 적치위치 갱신
	 *  10. Slab공통(TB_PT_SLABCOMM) 적치위치 갱신
	 *  11. YDTSJ009 (영차도착) 전문 구내운송시스템 송신
	 *  12. YDTSJ010 (작업완료) 전문 구내운송시스템 송신
	 *  13. 차량 스케줄 완료처리 및 DEL_YN 처리
	 *
	 * @param logId          로그 ID
	 * @param callerMthdNm  호출한 메소드명 (로그 흐름 추적용, mthdNm 전달)
	 * @param sModifier      수정자
	 * @param ydCarSchId     차량스케줄ID
	 * @param sBayInPnt      입고 적치열코드(포인트)
	 * @param sTrnEqpCd      운반설비코드
	 * @param sTrnEqpStkCapa 운반설비적재능력
	 * @param jrRtn          반환 레코드 (SND 데이터 누적)
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procPtCarArrival(String logId, String callerMthdNm, String sModifier, String ydCarSchId, String sBayInPnt, String sTrnEqpCd
			                                                       , String sTrnEqpStkCapa, JDTORecord  jrRtn) throws DAOException {
		String mthdNm = "핫슬라브이송PT차량도착처리[CCoilCarMvSeEJB.procPtCarArrival] < " + callerMthdNm;
		commUtils.printLog(logId, mthdNm, "S+");
		try {

			JDTORecord jrParam = null;

			/**********************************************************
			 * 1. TB_YD_CARFTMVMTL 에서 해당 차량스케줄의 재료번호 목록 조회
			 *      SELECT STL_NO, YD_STK_BED_NO, YD_STK_LYR_NO
			 *        FROM TB_YD_CARFTMVMTL
			 *       WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID AND DEL_YN = 'N'
			 **********************************************************/
			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 1. 차량이송재료(TB_YD_CARFTMVMTL) 조회", "SL");
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
			JDTORecordSet jsCarFtmvMtl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarftmvmtlBySchId", logId, mthdNm, "[핫슬라브 팔레트 PT차량 ] 이송재료 조회");

			if (jsCarFtmvMtl.size() <= 0) {
				commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 이송재료 없음 - 차량스케줄[" + ydCarSchId + "]", "S-");
				return jrRtn;
			}

			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 이송재료 수 : " + jsCarFtmvMtl.size(), "SL");

			/**********************************************************
			 * 2. 이송실적(TB_PT_STLFRTOMOVE) 완료처리
			 *      YD_MTL_PLN_STR_TO_LOC_CD = 입고 적치열코드(sBayInPnt)
			 *      FRTOMOVE_STAT_CD          = '*' (완료)
			 *      참조: procY5CarWrkStatCtrCoil_PIDEV 참조
			 **********************************************************/
			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 2. 이송실적 완료처리 시작", "SL");

			for (int ptIdx = 1; ptIdx <= jsCarFtmvMtl.size(); ptIdx++) {
				jsCarFtmvMtl.absolute(ptIdx);
				String sPtStlNo = commUtils.trim(jsCarFtmvMtl.getRecord().getFieldString("STL_NO"));

				if ("".equals(sPtStlNo)) {
					continue;
				}

				// 이송실적 조회 (미완료 최신 건)
				JDTORecord jrPtStlParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrPtStlParam.setField("STL_NO", sPtStlNo);
				JDTORecordSet jsPtStl = commDao.select(jrPtStlParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getPtStlFrtoMove", logId, mthdNm, "[핫슬라브 팔레트 PT차량 ] 이송실적 조회");

				if (jsPtStl.size() <= 0) {
					commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 이송실적 없음 - 재료번호[" + sPtStlNo + "]", "SL");
					continue;
				}

				// 이송실적 완료처리: 입고위치 및 완료상태 업데이트
				jsPtStl.first();
				JDTORecord jrPtStl = jsPtStl.getRecord();
				jrPtStl.setField("YD_MTL_PLN_STR_TO_LOC_CD", sBayInPnt); // 입고 적치열코드
				jrPtStl.setField("FRTOMOVE_STAT_CD"        , "*"       ); // 이송완료
				jrPtStl.setField("MODIFIER"                , sModifier );
				commDao.update(jrPtStl, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updPtStlFrtoMove2", logId, mthdNm, "[핫슬라브 팔레트 PT차량 ] 이송실적 테이블 완료처리");
				commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 이송실적 완료처리 - 재료번호[" + sPtStlNo + "], 입고위치[" + sBayInPnt + "]", "SL");
			}

			/**********************************************************
			 * 3. 주편공통(TB_PT_MSLABCOMM) 진행상태 갱신 및 PT 전문 발송
			 *         - 1에서 조회한 jsCarFtmvMtl STL_NO 별로 진행상태 갱신
			 *         - 크레인 연동 없이 재료번호로 직접 처리
			 *         - 참조: SlabYdL2RcvSeEJBBean.rcvY1YDL009 MslabCommProg 참조
			 ********************************************************/
			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 3. 주편공통 진행상태 갱신 시작", "SL");
			for (int mIdx = 1; mIdx <= jsCarFtmvMtl.size(); mIdx++) {
				jsCarFtmvMtl.absolute(mIdx);
				String sMslabStlNo = commUtils.trim(jsCarFtmvMtl.getRecord().getFieldString("STL_NO"));

				if ("".equals(sMslabStlNo)) {
					continue;
				}

				// 주편공통 진행상태 후보 조회
				JDTORecord jrMslabProgParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrMslabProgParam.setField("STL_NO", sMslabStlNo);
				JDTORecordSet jsMslabCommProg = commDao.select(jrMslabProgParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getMslabCommProgByStlNo", logId, mthdNm, "[핫슬라브 팔레트 PT차량 ] 주편공통 진행상태 후보 조회");

				for (int ii = 0; ii < jsMslabCommProg.size(); ii++) {
					JDTORecord jrMslabChk = jsMslabCommProg.getRecord(ii);

					commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 주편공통 진행상태 갱신 " + commUtils.trim(jrMslabChk.getFieldString("LOG_MSG")), "SL");

					JDTORecord jrMslabUpd = commUtils.getParam(logId, mthdNm, sModifier);
					// slab 관련 DAO 는 V_ 필요
					jrMslabUpd.setField("V_CURR_PROG_REG_DDTT", commUtils.trim(jrMslabChk.getFieldString("CURR_PROG_REG_DDTT"))); //진행상태등록일시
					jrMslabUpd.setField("V_CURR_PROG_CD"      , commUtils.trim(jrMslabChk.getFieldString("CURR_PROG_CD"      ))); //현재진행코드
					jrMslabUpd.setField("V_STL_NO"            , commUtils.trim(jrMslabChk.getFieldString("STL_NO"            ))); //재료번호

					slabRcv2Dao.updY1YDL009("MslabCommProg", jrMslabUpd);

					//구내운송 Slab이송완료전문(주편공통) 발송
					jrRtn = commUtils.addSndData(jrRtn, slabCommDao.getMsgL3("YDPTJ001Mslab", jrMslabUpd));
				}
			}

			/**********************************************************
			 * 4. Slab공통(TB_PT_SLABCOMM) 진행상태 갱신 및 PT 전문 발송
			 *         - 1에서 조회한 jsCarFtmvMtl STL_NO 별로 진행상태 갱신
			 *         - 크레인 연동 없이 재료번호로 직접 처리
			 *         - 참조: SlabYdL2RcvSeEJBBean.rcvY1YDL009 SlabCommProg 참조
			 ********************************************************/
			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 4. Slab공통 진행상태 갱신 시작", "SL");
			for (int sIdx = 1; sIdx <= jsCarFtmvMtl.size(); sIdx++) {
				jsCarFtmvMtl.absolute(sIdx);
				String sSlabStlNo = commUtils.trim(jsCarFtmvMtl.getRecord().getFieldString("STL_NO"));

				if ("".equals(sSlabStlNo)) {
					continue;
				}

				// Slab공통 진행상태 후보 조회
				JDTORecord jrSlabProgParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrSlabProgParam.setField("STL_NO", sSlabStlNo);
				JDTORecordSet jsSlabCommProg = commDao.select(jrSlabProgParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getSlabCommProgByStlNo", logId, mthdNm, "[핫슬라브 팔레트 PT차량 ] Slab공통 진행상태 후보 조회");

				for (int ii = 0; ii < jsSlabCommProg.size(); ii++) {
					JDTORecord jrSlabChk = jsSlabCommProg.getRecord(ii);

					commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] Slab공통 진행상태 갱신 " + commUtils.trim(jrSlabChk.getFieldString("LOG_MSG")), "SL");

					JDTORecord jrSlabUpd = commUtils.getParam(logId, mthdNm, sModifier);
					// slab 관련 DAO 는 V_ 필요
					jrSlabUpd.setField("V_CURR_PROG_REG_DDTT", commUtils.trim(jrSlabChk.getFieldString("CURR_PROG_REG_DDTT"))); //진행상태등록일시
					jrSlabUpd.setField("V_CURR_PROG_CD"      , commUtils.trim(jrSlabChk.getFieldString("CURR_PROG_CD"      ))); //현재진행코드
					jrSlabUpd.setField("V_STL_NO"            , commUtils.trim(jrSlabChk.getFieldString("STL_NO"            ))); //재료번호

					slabRcv2Dao.updY1YDL009("SlabCommProg", jrSlabUpd);

					//구내운송 Slab이송완료전문(Slab공통) 발송
					jrRtn = commUtils.addSndData(jrRtn, slabCommDao.getMsgL3("YDPTJ001Slab", jrSlabUpd));
				}
			}

			/**********************************************************
			 * 5. 적치열(TB_YD_STKCOL) 이전 처리
			 *      - 이전 다른 포인트에 연결된 차량 정보 클리어
			 *      - 새 포인트의 적치열 활성화 및 차량 정보 연결
			 *      - 참조: procUDMatlCarArr 4단계/5단계 참조
			 **********************************************************/
			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 5. 적치열 이전 차량 클리어", "SL");
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRN_EQP_CD", sTrnEqpCd);
			/*
			UPDATE TB_YD_STKCOL
			   SET TRN_EQP_CD    = NULL
			     , YD_CAR_USE_GP = NULL
			 WHERE TRN_EQP_CD    = :V_TRN_EQP_CD
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStkcolTrnEqpCdToNull", logId, mthdNm, "[핫슬라브 팔레트 PT차량 ] 적치열 이전 차량 클리어");

			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 5. 적치열 활성화 - 포인트[" + sBayInPnt + "]", "SL");
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_STK_COL_GP"      , sBayInPnt   );
			jrParam.setField("YD_CAR_USE_GP"      , "L"         );
			jrParam.setField("TRN_EQP_CD"          , sTrnEqpCd   );
			jrParam.setField("YD_STK_COL_ACT_STAT", "L"         ); // 활성화
			/*
			UPDATE TB_YD_STKCOL
			   SET YD_STK_COL_ACT_STAT = NVL(:V_YD_STK_COL_ACT_STAT, YD_STK_COL_ACT_STAT)
			     , YD_CAR_USE_GP       = NVL(:V_YD_CAR_USE_GP, YD_CAR_USE_GP)
			     , TRN_EQP_CD          = NVL(:V_TRN_EQP_CD, TRN_EQP_CD)
			     , MOD_DDTT            = SYSDATE
			     , MODIFIER            = :V_MODIFIER
			WHERE YD_STK_COL_GP        = :V_YD_STK_COL_GP
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStkcol01", logId, mthdNm, "[핫슬라브 팔레트 PT차량 ] 적치열 활성화");

			/**********************************************************
			 * 6. 차량포인트(TB_YD_CARPOINT) 처리
			 *      - 이전 포인트 정보 클리어 (차량코드 제거)
			 *      - 새 입고 포인트 등록
			 *      - 참조: procUDMatlCarArr YdCarPointinforeg("1"), ("3") 참조
			 **********************************************************/
			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 6. 차량포인트 클리어 및 새 포인트 등록", "SL");
			// 이전 포인트 클리어 (chk="1": 차량코드 제거 TRN_EQP_CD=NULL)
			this.YdCarPointinforeg("1", "", sTrnEqpCd, "", "", "", "C", logId, mthdNm, sModifier);
			// 새 입고 포인트 등록 (chk="3": 적치열코드 기반 포인트 등록)
			this.YdCarPointinforeg("3", "", sTrnEqpCd, sBayInPnt, "", "", "L", logId, mthdNm, sModifier);

			/**********************************************************
			 * 7. 스판(TB_YD_STKBED) 활성화
			 *      - 입고 적치열의 스판 활성화 상태로 갱신
			 *      - 참조: procUDMatlCarArr 6단계 참조
			 **********************************************************/
			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 7. 스판(TB_YD_STKBED) 활성화 - 포인트[" + sBayInPnt + "]", "SL");
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_STK_COL_GP"     , sBayInPnt      );
			jrParam.setField("YD_STK_BED_WT_MAX" , sTrnEqpStkCapa ); // 설비 적재능력
			jrParam.setField("YD_STK_BED_ACT_STAT", "L"           ); // 활성화
			/*
			UPDATE TB_YD_STKBED
			   SET YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
			     , YD_STK_BED_WT_MAX   = :V_YD_STK_BED_WT_MAX
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStkbedYdStkColGp", logId, mthdNm, "[핫슬라브 팔레트 PT차량 ] 스판 활성화");

			/**********************************************************
			 * 8. 적치단(TB_YD_STKLYR) 업데이트
			 *      - TB_YD_CARFTMVMTL 의 BED_NO/LYR_NO 기준으로 해당 적치단 정보 업데이트
			 *      - YD_STK_LYR_ACT_STAT = 'E' (점유됨), YD_STK_LYR_MTL_STAT = 'C' (적치중)
			 *      - 참조: procUDMatlCarArr 7단계 참조
			 *      - 일반처리는 작업장부(TB_YD_WRKBOOKMTL)에서 BED/LYR 획득
			 *        PT 차량은 TB_YD_CARFTMVMTL 에서 직접 획득
			 **********************************************************/
			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 8. 적치단(TB_YD_STKLYR) 업데이트 시작", "SL");

			for (int lyrIdx = 1; lyrIdx <= jsCarFtmvMtl.size(); lyrIdx++) {
				jsCarFtmvMtl.absolute(lyrIdx);
				String sLyrStlNo  = commUtils.trim(jsCarFtmvMtl.getRecord().getFieldString("STL_NO"         ));
				String sLyrBedNo  = commUtils.trim(jsCarFtmvMtl.getRecord().getFieldString("YD_STK_BED_NO"  ));
				String sLyrLyrNo  = commUtils.trim(jsCarFtmvMtl.getRecord().getFieldString("YD_STK_LYR_NO"  ));

				if ("".equals(sLyrStlNo) || "".equals(sLyrBedNo)) {
					continue;
				}

				// LYR_NO 없을 경우 기본값 "001" 사용
				if ("".equals(sLyrLyrNo)) {
					sLyrLyrNo = "001";
				}

				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_STK_COL_GP"      , sBayInPnt       ); // 입고 포인트
				jrParam.setField("YD_STK_BED_NO"      , sLyrBedNo       ); // 스판번호
				jrParam.setField("YD_STK_LYR_NO"      , sLyrLyrNo       ); // 층번호
				jrParam.setField("STL_NO"              , sLyrStlNo       ); // 재료번호
				jrParam.setField("YD_STK_LYR_ACT_STAT", "E"             ); // 점유됨
				jrParam.setField("YD_STK_LYR_MTL_STAT", "C"             ); // 적치중
				/*
				UPDATE TB_YD_STKLYR
				   SET YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT, YD_STK_LYR_ACT_STAT)
				     , YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT, YD_STK_LYR_MTL_STAT)
				     , STL_NO              = NVL(:V_STL_NO, STL_NO)
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
				   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
				*/
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdStkLyrYdStkColBedGp", logId, mthdNm, "[핫슬라브 팔레트 PT차량 ] 적치단 업데이트");
				commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 적치단 업데이트 - 재료[" + sLyrStlNo + "], BED[" + sLyrBedNo + "], LYR[" + sLyrLyrNo + "]", "SL");
			}

			/**********************************************************
			 * 9. 주편공통(TB_PT_MSLABCOMM) 적치위치 갱신
			 *         - 1에서 조회한 jsCarFtmvMtl STL_NO 별로 적치위치 갱신
			 *         - 크레인 연동 없이 재료번호로 직접 처리
			 *         - 참조: SlabYdL2RcvDAO.updY1YDL009MslabComm (크레인 연동)
			 ********************************************************/
			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 9. 주편공통 적치위치 갱신 시작", "SL");
			for (int mIdx = 1; mIdx <= jsCarFtmvMtl.size(); mIdx++) {
				jsCarFtmvMtl.absolute(mIdx);
				String sMslabStlNo2 = commUtils.trim(jsCarFtmvMtl.getRecord().getFieldString("STL_NO"         ));
				String sMslabColGp  = sBayInPnt; // 입고 포인트
				String sMslabBedNo  = commUtils.trim(jsCarFtmvMtl.getRecord().getFieldString("YD_STK_BED_NO"  ));
				String sMslabLyrNo  = commUtils.trim(jsCarFtmvMtl.getRecord().getFieldString("YD_STK_LYR_NO"  ));
				
				commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 주편공통 적치위치 갱신 - 재료[" + sMslabStlNo2 + "], COL[" + sMslabColGp + "], BED[" + sMslabBedNo + "], LYR[" + sMslabLyrNo + "]", "SL");

				if ("".equals(sMslabStlNo2) || "".equals(sMslabColGp)) {
					continue;
				}

				if ("".equals(sMslabLyrNo)) {
					sMslabLyrNo = "001";
				}

				JDTORecord jrMslabCommUpd = commUtils.getParam(logId, mthdNm, sModifier);
				jrMslabCommUpd.setField("STL_NO"         , sMslabStlNo2);
				jrMslabCommUpd.setField("YD_STK_COL_GP"  , sMslabColGp );
				jrMslabCommUpd.setField("YD_STK_BED_NO"  , sMslabBedNo );
				jrMslabCommUpd.setField("YD_STK_LYR_NO"  , sMslabLyrNo );

				commDao.update(jrMslabCommUpd, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updMslabCommByStlNo", logId, mthdNm, "[핫슬라브 팔레트 PT차량 ] 주편공통 적치위치 갱신");				
			}

			/**********************************************************
			 * 10. Slab공통(TB_PT_SLABCOMM) 적치위치 갱신
			 *         - 1에서 조회한 jsCarFtmvMtl STL_NO 별로 적치위치 갱신
			 *         - 크레인 연동 없이 재료번호로 직접 처리
			 *         - 참조: SlabYdL2RcvDAO.updY1YDL009SlabComm (크레인 연동)
			 ********************************************************/
			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 10. Slab공통 적치위치 갱신 시작", "SL");
			for (int sIdx = 1; sIdx <= jsCarFtmvMtl.size(); sIdx++) {
				jsCarFtmvMtl.absolute(sIdx);
				String sSlabStlNo2 = commUtils.trim(jsCarFtmvMtl.getRecord().getFieldString("STL_NO"         ));
				String sSlabColGp  = sBayInPnt; // 입고 포인트
				String sSlabBedNo  = commUtils.trim(jsCarFtmvMtl.getRecord().getFieldString("YD_STK_BED_NO"  ));
				String sSlabLyrNo  = commUtils.trim(jsCarFtmvMtl.getRecord().getFieldString("YD_STK_LYR_NO"  ));
				
				commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] Slab공통 적치위치 갱신 - 재료[" + sSlabStlNo2 + "], COL[" + sSlabColGp + "], BED[" + sSlabBedNo + "], LYR[" + sSlabLyrNo + "]", "SL");

				if ("".equals(sSlabStlNo2) || "".equals(sSlabColGp)) {
					continue;
				}

				if ("".equals(sSlabLyrNo)) {
					sSlabLyrNo = "001";
				}

				JDTORecord jrSlabCommUpd = commUtils.getParam(logId, mthdNm, sModifier);
				jrSlabCommUpd.setField("STL_NO"         , sSlabStlNo2);
				jrSlabCommUpd.setField("YD_STK_COL_GP"  , sSlabColGp );
				jrSlabCommUpd.setField("YD_STK_BED_NO"  , sSlabBedNo );
				jrSlabCommUpd.setField("YD_STK_LYR_NO"  , sSlabLyrNo );

				commDao.update(jrSlabCommUpd, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updSlabCommByStlNo", logId, mthdNm, "[핫슬라브 팔레트 PT차량 ] Slab공통 적치위치 갱신");
			}

			JDTORecord jrYdMsg = null; // YDTSJ009/010 전문 송신시 메시지 레코드

			/**********************************************************
			 * 11. YDTSJ009 (하차 개시) 전문 구내운송시스템 송신
			 *      - PT 차량 전용 크레인 없는 영차 도착 포인트 등록
			 *      - 구내운송 시스템에서 해당 차량의 영차 도착 처리 필요
			 *      - 이전 rcvY5YDL008 첫 전산 후 YDTSJ009 송신 방식 참조
			 *        (파라미터: YD_CAR_SCH_ID, YD_STK_COL_GP)
			 **********************************************************/
			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 11. YDTSJ009 하차 개시 전문 송신", "SL");
			jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setField("JMS_TC_CD"    , "YDTSJ009"  );
			jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId  );
			jrYdMsg.setField("YD_STK_COL_GP", sBayInPnt   ); // 입고 적치열코드(포인트)
			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDTSJ009", jrYdMsg));

			/**********************************************************
			 * 12. YDTSJ010 (하차 완료) 전문 구내운송시스템 송신
			 *      - PT 차량 전용 모든 작업완료 처리 (차량출발 포인트 등록)
			 *      - 이전 rcvY5YDL008 마지막 전산 후 YDTSJ010 송신 방식 참조
			 *        (파라미터: YD_CAR_SCH_ID, YD_SCH_CD, YD_GP)
			 *        TcYDTSJ010 처리: TB_YD_CARSCH.YD_CARUD_CMPL_DT 갱신
			 *        (13에서 updMvCarSchCmpl 로 YD_CARUD_CMPL_DT 이미 SET 됨)
			 **********************************************************/
			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 12. YDTSJ010 하차 완료 전문 송신", "SL");
			jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setField("JMS_TC_CD"    , "YDTSJ010"  );
			jrYdMsg.setField("YD_SCH_CD"    , ""           ); // TcYDTSJ010 이상시 파라미터
			jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId  );
			jrYdMsg.setField("YD_GP"        , "J"         ); // 2열연 야드
			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDTSJ010", jrYdMsg));

			/**********************************************************
			 * 13. 차량 스케줄 완료처리 및 DEL_YN 처리 (처리 마무리 단계)
			 *      - 구내운송 완료(TSYDJ004)에서 차량 스케줄 완료 후 포인트를
			 *        해당 포인트에 그대로 유지
			 *      - 스케줄 상태를 완료(D)로 업데이트하고 완료처리시 갱신
			 *      - TB_YD_CARSCH DEL_YN = 'Y' 처리
			 *      - TB_YD_CARFTMVMTL DEL_YN = 'Y' 처리 (이송재료)
			 **********************************************************/
			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 13. 차량스케줄 완료처리 (상태 D)", "SL");
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CAR_SCH_ID"   , ydCarSchId                );
			jrParam.setField("YD_CAR_PROG_STAT" , "D"                       ); // 작업완료 상태
			jrParam.setField("YD_CARUD_CMPL_DT" , commUtils.getDateTime14() ); // 작업완료처리시각
			/*
			UPDATE TB_YD_CARSCH
			   SET MOD_DDTT          = SYSDATE
			     , MODIFIER          = :V_MODIFIER
			     , YD_CAR_PROG_STAT  = NVL(:V_YD_CAR_PROG_STAT, YD_CAR_PROG_STAT)
			     , YD_CARUD_CMPL_DT  = DECODE(NVL(:V_YD_CARUD_CMPL_DT,'NULL'),'NULL',YD_CARUD_CMPL_DT,SYSDATE)
			 WHERE YD_CAR_SCH_ID     = :V_YD_CAR_SCH_ID
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updMvCarSchCmpl", logId, mthdNm, "[핫슬라브 팔레트 PT차량 ] 차량스케줄 완료처리");

			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 13. TB_YD_CARSCH DEL_YN='Y' 처리", "SL");
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
			jrParam.setField("DEL_YN"        , "Y"       );
			/*
			UPDATE TB_YD_CARSCH
			   SET DEL_YN     = :V_DEL_YN
			     , MODIFIER   = :V_MODIFIER
			     , MOD_DDTT   = SYSDATE
			 WHERE YD_CAR_SCH_ID = :YD_CAR_SCH_ID
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarSchDelYn", logId, mthdNm, "[핫슬라브 팔레트 PT차량 ] TB_YD_CARSCH DEL_YN=Y 처리");

			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 13. TB_YD_CARFTMVMTL DEL_YN='Y' 처리", "SL");
			for (int delIdx = 1; delIdx <= jsCarFtmvMtl.size(); delIdx++) {
				jsCarFtmvMtl.absolute(delIdx);
				String sDelStlNo = commUtils.trim(jsCarFtmvMtl.getRecord().getFieldString("STL_NO"));
				if ("".equals(sDelStlNo)) {
					continue;
				}
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
				jrParam.setField("STL_NO"        , sDelStlNo );
				jrParam.setField("DEL_YN"        , "Y"       );
				/*
				UPDATE TB_YD_CARFTMVMTL
				   SET DEL_YN     = :V_DEL_YN
				     , MODIFIER   = :V_MODIFIER
				     , MOD_DDTT   = SYSDATE
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				   AND STL_NO        = :V_STL_NO
				*/
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarftmvmtlDelYn", logId, mthdNm, "[핫슬라브 팔레트 PT차량 ] TB_YD_CARFTMVMTL DEL_YN=Y 처리");
				commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] TB_YD_CARFTMVMTL DEL_YN=Y - 재료번호[" + sDelStlNo + "]", "SL");
			}

			commUtils.printLog(logId, "[핫슬라브 팔레트 PT차량 ] 영차 도착 처리 완료 - 슬라브/차량은 포인트[" + sBayInPnt + "] 유지", "SL");
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 * 핫슬라브 팔레트 PT차량 공차(상차) 도착 처리 (procPtCarUDArrival) < 소재차량도착 (rcvTSYDJ003)
	 *
	 * 핫슬라브 팔레트 PT차량(E=공차) 상차 목적으로 소재 포인트에 도착 시 처리
	 *   1단계. 차량스케줄 상태 상차완료(5) 업데이트 (updYdCarschLdWbookId)
	 *   2단계. TB_YD_STKLYR 포인트 적치 슬라브 조회 (getYdStkLyrByColGpForUd)
	 *   3단계. 슬라브별 차량스케줄 재료 등록 (TB_YD_CARFTMVMTL INSERT - insCarFtmvMtlForPtUd)
	 *   4단계. 상차개시 전문 (YDTSJ007) 발송 - 작업스케줄ID(YD_CAR_SCH_ID) 기준 1회
	 *   5단계. 상차완료 전문 (YDTSJ008) 발송 - 작업스케줄ID(YD_CAR_SCH_ID) 기준 1회
	 *   * 차량/슬라브는 포인트에 그대로 유지 (DEL_YN 변경 없음)
	 *
	 * @param logId          로그 ID
	 * @param callerMthdNm   호출자 메소드명 (로그 흐름 파악용, mthdNm 사용)
	 * @param sModifier      수정자
	 * @param ydCarSchId     차량일정정보ID
	 * @param sBayInPnt      입동 위치코드(위치동, 예: JCPT05)
	 * @param sArrYdPntCd    도착 야드 포인트코드 (예: 2C02)
	 * @param sTrnEqpCd      운반설비코드
	 * @param sTrnEqpStkCapa 운반설비적치능력
	 * @param jrRtn          반환 레코드 (SND 데이터 연산)
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procPtCarUDArrival(String logId, String callerMthdNm, String sModifier, String ydCarSchId, String sBayInPnt, String sArrYdPntCd, String sTrnEqpCd
	                                                       , String sTrnEqpStkCapa, JDTORecord jrRtn) throws DAOException {
		String mthdNm = "핫슬라브 팔레트 PT차량 공차(상차)도착처리[CCoilCarMvSeEJB.procPtCarUDArrival] < " + callerMthdNm;
		commUtils.printLog(logId, mthdNm, "S+");
		try {

			JDTORecord jrParam = null;
			JDTORecord jrYdMsg = null;

			/**********************************************************
			 * 1. 차량스케줄 상차도착(5) 상태 업데이트
			 *   - YD_CAR_PROG_STAT    = '5' (상차완료)
			 *   - YD_CARLD_STOP_LOC   = sBayInPnt (입동포인트 = 상차위치동)
			 *   - YD_PNT_CD1          = sArrYdPntCd (도착야드포인트코드)
			 *   - YD_CARLD_ARR_DT     = 현재시각
			 *   - YD_CARLD_WRK_BOOK_ID = '' (작업지시없음)
			 *   - 차량/슬라브는 포인트에 그대로 유지 (DEL_YN 변경 없음)
			 **********************************************************/
			commUtils.printLog(logId, "[PT공차] 1. 차량스케줄 상차도착(2) 상태 업데이트", "SL");
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CAR_SCH_ID"       , ydCarSchId                ); // 차량일정ID
			jrParam.setField("YD_CARLD_WRK_BOOK_ID", ""                        ); // 작업지시없음
			jrParam.setField("YD_PNT_CD1"          , sArrYdPntCd              ); // 도착야드포인트코드
			jrParam.setField("YD_CAR_PROG_STAT"    , "5"                       ); // 상차완료
			jrParam.setField("YD_CARLD_STOP_LOC"   , sBayInPnt                 ); // 상차위치동(입동포인트)
			jrParam.setField("YD_CARLD_ARR_DT"     , commUtils.getDateTime14() ); // 상차도착시각
			/*
			UPDATE TB_YD_CARSCH
			   SET MODIFIER             = :V_MODIFIER
			     , MOD_DDTT             = SYSDATE
			     , YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
			     , YD_PNT_CD1           = :V_YD_PNT_CD1
			     , YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT
			     , YD_CARLD_STOP_LOC    = :V_YD_CARLD_STOP_LOC
			     , YD_CARLD_ARR_DT      = TO_DATE(:V_YD_CARLD_ARR_DT, 'YYYYMMDDHH24MISS')
			 WHERE YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarschLdWbookId", logId, mthdNm, "[PT공차] 차량스케줄 상차도착 상태 업데이트");

			/**********************************************************
			 * 2. 포인트(sBayInPnt) 기준 TB_YD_STKLYR 슬라브 조회 (1~4건)
			 *   - YD_STK_COL_GP = sBayInPnt (입동포인트 = 상차할 위치동)
			 *   - YD_STK_LYR_MTL_STAT = 'C' (적치 상태)
			 *   - 2단 적치 가능: YD_STK_BED_NO + YD_STK_LYR_NO 순서로 정렬
			 *   - 슬라브/량은 이동 없이 포인트에 그대로 유지
			 **********************************************************/
			commUtils.printLog(logId, "[PT공차] 2. 포인트 슬라브 조회 [" + sBayInPnt + "]", "SL");
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_STK_COL_GP", sBayInPnt);
			/*
			SELECT YD_STK_COL_GP
			     , YD_STK_BED_NO
			     , YD_STK_LYR_NO
			     , STL_NO
			  FROM TB_YD_STKLYR
			 WHERE YD_STK_COL_GP      = :V_YD_STK_COL_GP
			   AND YD_STK_LYR_MTL_STAT = 'C'
			   AND DEL_YN              = 'N'
			   AND STL_NO IS NOT NULL
			 ORDER BY YD_STK_BED_NO, YD_STK_LYR_NO
			*/
			JDTORecordSet jsStkLyr = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStkLyrByColGpForUd", logId, mthdNm, "[PT공차] 포인트 적치층 조회");

			if (jsStkLyr.size() <= 0) {
				commUtils.printLog(logId, "[PT공차] 포인트[" + sBayInPnt + "]  적치슬라브 없음 - 상차개시/완료 전문 발송 없이 종료", "SL");
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
			}
			commUtils.printLog(logId, "[PT공차] 포인트 적치 슬라브 수 : " + jsStkLyr.size(), "SL");

			/**********************************************************
			 * 3. 저장 슬라브별 차량스케줄 재료 등록 (TB_YD_CARFTMVMTL INSERT)
			 *   - jsStkLyr 루프: 슬라브별로 INSERT
			 *   - HCR_GP='C', STL_PROG_CD='E', YD_MTL_ITEM='BH', YD_ROUTE_GP='E4'
			 *   - YD_CAR_UPP_LOC_CD=NULL, YD_MSG_NM=NULL (크레인스케줄 없음)
			 *   - NOT EXISTS 조건으로 중복 등록 방지
			 **********************************************************/
			commUtils.printLog(logId, "[PT공차] 3. 슬라브별 차량스케줄 재료 등록 시작", "SL");
			for (int i = 0; i < jsStkLyr.size(); i++) {
				JDTORecord jrStkLyr = jsStkLyr.getRecord(i);
				String sStkStlNo = commUtils.trim(jrStkLyr.getFieldString("STL_NO"        ));
				String sStkBedNo = commUtils.trim(jrStkLyr.getFieldString("YD_STK_BED_NO" ));
				String sStkLyrNo = commUtils.trim(jrStkLyr.getFieldString("YD_STK_LYR_NO" ));
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_CAR_SCH_ID"  , ydCarSchId ); // 차량일정ID
				jrParam.setField("STL_NO"          , sStkStlNo  ); // 스틸당호
				jrParam.setField("YD_STK_BED_NO"   , sStkBedNo  ); // 스패번호
				jrParam.setField("YD_STK_LYR_NO"   , sStkLyrNo  ); // 단번호
				/*
				INSERT INTO TB_YD_CARFTMVMTL (
				    YD_CAR_SCH_ID, STL_NO, REGISTER, REG_DDTT, MODIFIER, MOD_DDTT, DEL_YN
				  , YD_CAR_UPP_LOC_CD, YD_STK_BED_NO, YD_STK_LYR_NO
				  , HCR_GP, STL_PROG_CD, YD_MTL_ITEM, YD_ROUTE_GP, YD_MSG_NM
				)
				SELECT :V_YD_CAR_SCH_ID
				     , :V_STL_NO
				     , :V_MODIFIER
				     , SYSDATE
				     , :V_MODIFIER
				     , SYSDATE
				     , 'N'
				     , NULL
				     , :V_YD_STK_BED_NO
				     , :V_YD_STK_LYR_NO
				     , 'C'
				     , 'E'
				     , 'BH'
				     , 'E4'
				     , NULL
				  FROM DUAL
				 WHERE NOT EXISTS (
				     SELECT 1
				       FROM TB_YD_CARFTMVMTL
				      WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				        AND STL_NO        = :V_STL_NO
				        AND DEL_YN        = 'N'
				 )
				*/
				commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.insCarFtmvMtlForPtUd", logId, mthdNm, "[PT공차] 슬라브 재료 등록");
				commUtils.printLog(logId, "[PT공차] 재료등록 완료 - 배드[" + sStkBedNo + "] 적치단[" + sStkLyrNo + "] 재료번호[" + sStkStlNo + "]", "SL");
			}
			commUtils.printLog(logId, "[PT공차] 3. 슬라브별 차량스케줄 재료 등록 완료 - 총 [" + jsStkLyr.size() + "]건", "SL");

			/**********************************************************
			 * 4. 상차개시 송신 (YDTSJ007) - YD_CAR_SCH_ID 기준 구내운송 상차개시
			 *   - TcYDTSJ007 쿼리: TB_YD_CARSCH + TB_YD_STKCOL JOIN
			 *   - 파라미터: YD_CAR_SCH_ID
			 **********************************************************/
			commUtils.printLog(logId, "[PT반출] 4. 상차개시(YDTSJ007) 송신", "SL");
			jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setField("JMS_TC_CD"    , "YDTSJ007");
			jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId); // 작업스케줄ID
			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDTSJ007", jrYdMsg));
			commUtils.printLog(logId, "[PT반출] 4. 상차개시(YDTSJ007) 송신 완료 - 작업스케줄ID[" + ydCarSchId + "]", "SL");

			/**********************************************************
			 * 5. 상차완료 송신 (YDTSJ008) - YD_CAR_SCH_ID 기준 구내운송 상차완료
			 *   - TcYDTSJ008 쿼리: TB_YD_CARSCH + TB_YD_CARFTMVMTL + TB_YD_STKCOL JOIN
			 *   - 파라미터: YD_CAR_SCH_ID
			 **********************************************************/
			commUtils.printLog(logId, "[PT반출] 5. 상차완료(YDTSJ008) 송신", "SL");
			jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setField("JMS_TC_CD"    , "YDTSJ008");
			jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId); // 작업스케줄ID
			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL3("YDTSJ008", jrYdMsg));
			commUtils.printLog(logId, "[PT반출] 5. 상차완료(YDTSJ008) 송신 완료 - 작업스케줄ID[" + ydCarSchId + "]", "SL");

			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 소재차량 공차도착 실적 -- procLDMatlCarArrForCoil
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procLDMatlCarArr(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "소재차량 공차도착 실적[CCoilCarMvSeEJB.procLDMatlCarArr] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			String sMsgGp            = commUtils.trim(rcvMsg.getFieldString("MSG_GP"              )); //전문구분
			String sArrWlocCd        = commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD"         )); //착지개소코드
			String sArrYdPntCd       = commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD"       )); //착지야드포인트코드
			String ydWbookId         = commUtils.trim(rcvMsg.getFieldString("YD_CARLD_WRK_BOOK_ID"));
	    	String ydSchCd           = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"           ));	    	
	    	String ydCarUseGp        = commUtils.trim(rcvMsg.getFieldString("YD_CAR_USE_GP"       ));
			String sTrnEqpCd         = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"          )); //운송장비코드
			String sTrnEqpStkCapa    = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA"    ));

			String ydCarSchId        = commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"       ));
	    	String ydPntCd           = commUtils.trim(rcvMsg.getFieldString("YD_PNT_CD1"          ));
	    	String ydCarldStopLoc    = commUtils.trim(rcvMsg.getFieldString("YD_CARLD_STOP_LOC"   )); //상차위치
	    	String ydCarProgStat     = commUtils.trim(rcvMsg.getFieldString("YD_CAR_PROG_STAT"    ));
	    	String sFrtomoveWordNo   = commUtils.trim(rcvMsg.getFieldString("FRTOMOVE_WORD_NO"    )); //운송지시번호
			
	    	String sModifier         = commUtils.trim(rcvMsg.getFieldString("MODIFIER"            )); //수정자(Backup Only)
			String sMsg              = "";
			
			int intRtnVal            = 0;
			
			boolean bIsReplacable	 = false;
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			/******************************
			 * 신규 구내운송 적용 여부
			 ******************************/
//			String sAPP900 = coilDao.ApplyYn(logId, mthdNm, "APP900","J","*"); //구내운송 신모듈 적용 여부
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);

			jrParam.setField("WLOC_CD"   , sArrWlocCd);
			jrParam.setField("YD_PNT_CD" , sArrYdPntCd);
			
			/**********************************************************
			* 1. 착지개소코드와 착지야드포인트코드로 적치열을 조회한다.
			**********************************************************/
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
	    	JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStkcolWLocCdandPntCd", logId, mthdNm, "적치열 조회");
	    	
	    	if (jsRst.size() <= 0) {
	    		sMsg = "착지개소코드, 포인트코드 적치열 조회 실패"; commUtils.printLog(logId, sMsg, "S-");
	    		jrRtn.setField("RTN_CD"	, "0");	
	    		jrRtn.setField("RTN_MSG", sMsg);
	    		return jrRtn;
	    	}
	    	
	    	//차량 정지위치 적치열구분
	    	String ydStkColGp      = commUtils.trim(jsRst.getRecord(0).getFieldString("YD_STK_COL_GP"      ));
	    	String ydStkColActStat = commUtils.trim(jsRst.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));
	    	String ydLocGp         = commUtils.trim(jsRst.getRecord(0).getFieldString("YD_LOC_GP"          )); //소재, 제품야드 구분
	    	String sTrnEqpCdOld    = commUtils.trim(jsRst.getRecord(0).getFieldString("TRN_EQP_CD"         ));
	    	
	    	if ("L".equals(ydStkColActStat)) {
	    		if (!sTrnEqpCdOld.equals(sTrnEqpCd)) {
	    			sMsg = "상차정지위치가 이미 활성상태입니다."; commUtils.printLog(logId, sMsg, "S-");
		    		jrRtn.setField("RTN_CD"	, "0");
		    		jrRtn.setField("RTN_MSG", sMsg);
		    		return jrRtn;
	    		} 
	    	}
	    	
	    	if ("N".equals(ydStkColActStat)) {
	    		sMsg = "상차정지위치가 사용 불가 상태입니다."; commUtils.printLog(logId, sMsg, "S-");
	    		jrRtn.setField("RTN_CD"	, "0");
	    		jrRtn.setField("RTN_MSG", sMsg);
	    		return jrRtn;
	    	}
	    	
	    	//도착전문이 수정인 경우/차량이 이미 도착한 경우
	    	if ("U".equals(sMsgGp) && "2".equals(ydCarProgStat)) {
	    		/*
	    		 * 업무기준 : 1. 이전에 도착한 동의 작업예약과 크레인스케줄 삭제처리
	    		 * 			 2. 이전에 도착한 차량정지POINT를 CLEAR
	    		 * 			 2. 현재 도착한 동의 빠른 준비스케줄 조회
	    		 * 			 3. 작업예약과 작업예약재료 등록
	    		 */
	    		//도착전문 수정시 동이 다른 경우 기존작업예약에 대한 크레인스케줄 존재여부 확인
	    		if (ydPntCd.length() > 2 && !ydPntCd.substring(0, 2).equals(sArrYdPntCd.substring(0, 2))) {
	    			
	    			commUtils.printLog(logId, "도착전문 수정시 동이 다른 경우 기존 작업예약에 대한 크레인스케줄 존재하는지 조회 시작.", "SL");
	    			
	    			if (!"".equals(ydWbookId)) {
	    				
	    				jrParam.setField("YD_WBOOK_ID", ydWbookId);
	    				/*
						SELECT *
						  FROM TB_YD_CRNSCH
						 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
						   AND DEL_YN='N'
						 ORDER BY YD_CRN_SCH_ID
	    				 */
	    				JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCrnschByWrkId", logId, mthdNm, "크레인스케줄 조회");
	    				if (jsCrnSch.size() > 0) {
	    					
	    					commUtils.printLog(logId, "도착전문 수정시 동이 다른 경우 기존 작업예약과 크레인스케줄 삭제", "SL");
	    					JDTORecord jrSchCncl = commUtils.getParam(logId, mthdNm, sModifier);
	    					jrSchCncl.setField("YD_CRN_SCH_ID", commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID")));
	    					jrSchCncl.setField("YD_SCH_CD"    , commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD")));
	    					jrSchCncl.setField("DEL_YN"       , "Y");
	    					
	    					EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
							JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrSchCncl });
	    					
							jrRtn = commUtils.addSndData(jrRtn, jrRst);
	    				}
	    				
	    				commUtils.printLog(logId, "도착전문 수정 시 동이 다른 경우 이전 도착동의 차량정지POINT를 CLEAR", "SL");
	    				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	    				jrParam.setField("YD_STK_COL_GP"      , ydStkColGp);
	    				jrParam.setField("YD_STK_COL_ACT_STAT", "C"       ); //비활성화
	    				
	    				//차량정지위치활성/비활성처리
						this.procCarPosActiveOrInActive(jrParam);
						
	    				
	    			} //if (!"".equals(ydWbookId)) {
	    			
	    			//신규작업예약 생성
	    			bIsReplacable = true;
	    			
	    		} else {
	    			commUtils.printLog(logId, "도착전문 수정시 동이 같고 작업예약이 존재하지 않으므로 업무 계속 진행", "SL");
	    			//신규 작업예약 생성하지 않기
	    			bIsReplacable = false;
	    		}
	    		
	    	}
	    	
	    	/**
	    	 * 실제 차량이 도착한 위치로 스케줄 코드로 새로 만들어서 등록한다. (생성때 등록한 스케줄코드와 틀릴 수 있기때문에...)
	    	 */
	    	if ("1".equals(ydCarProgStat)) {					//상차출발인 경우에만 처리
	    		commUtils.printLog(logId, "기존 등록된 작업예약이 존재하는 동과 차량이 도착한 현재동이 같은 지 비교", "SL");
	    		if (!"".equals(ydWbookId)) {
	    			if (ydSchCd.substring(0,2).equals(ydStkColGp.substring(0,2)) ) {
		    			//신규 작업예약 생성하지 않기
		    			bIsReplacable = false; 
	    			} else {
	    				//기존작업예약이 등록된 동과 차량이 도착한 현재동이 다른 경우
		    			//기존작업예약을 삭제처리하고 이미 등록된 차량이송준비스케줄을 조회	
	    				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	    				jrParam.setField("YD_WBOOK_ID", ydWbookId);
	    				jrParam.setField("DEL_YN"     , "Y"      );
	    				
	    				commUtils.printLog(logId, "기존에 등록된 작업예약이 존재하는 동과 차량이 도착한 현재동이 다르므로 기존작업예약["+ydWbookId+"]재료 삭제", "SL");
	    				/*
	    				UPDATE TB_YD_WRKBOOKMTL  
	    				   SET MOD_DDTT = SYSDATE
	    				     , MODIFIER = :V_MODIFIER
	    				     , DEL_YN   = :V_DEL_YN
	    				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID	
	    				 */
	    				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delWrkbookMtl", logId, mthdNm, "작업예약 재료 삭제");
	    				
	    				/*
	    				UPDATE TB_YD_WRKBOOK
	    				   SET MOD_DDTT = SYSDATE
	    				     , MODIFIER = :V_MODIFIER
	    				     , DEL_YN   = :V_DEL_YN
	    				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
	    				 */
	    				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delYdWrkbook", logId, mthdNm, "작업예약 삭제");

	    				//해당 작업예약과 관련된 준비스케줄을 회복
	    				this.restorePrepSch(jrParam);
	    				
	    				//신규 작업예약 생성하기
		    			bIsReplacable = true;
	    			}
    			} else {
    				//신규 작업예약 생성하기
	    			bIsReplacable = true;
    			}
	    		
	    		if (bIsReplacable) {
	    			//이미 등록된 차량이송준비스케줄을 조회
	    			commUtils.printLog(logId, "차량이송준비스케줄을 조회 후 작업예약 등록 시작", "SL");
					
					//작업예약 신규 생성***********************************************************
	    			jrParam.setField("WLOC_CD"      , sArrWlocCd);
	    			jrParam.setField("TRN_EQP_CD"   , sTrnEqpCd);
	    			jrParam.setField("YD_STK_COL_GP", ydStkColGp);
	    			jrParam.setField("YD_LOC_GP"    , ydLocGp   );
	    			
					JDTORecord jrWbook = this.procYdWbookForCarLd(jrParam);

					ydWbookId = commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));
					ydSchCd   = commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"  ));
					
					commUtils.printLog(logId, "차량이송준비스케줄을 조회 후 작업예약 등록 끝", "SL");
					
	    		}
	    	} //if ("1".equals(ydCarProgStat)) {
	    	
	    	//상차작업예약ID등록
	    	jrParam.setField("YD_CAR_SCH_ID"       , ydCarSchId );
	    	jrParam.setField("YD_CARLD_WRK_BOOK_ID", ydWbookId  );
	    	jrParam.setField("YD_PNT_CD1"          , sArrYdPntCd);
	    	jrParam.setField("YD_CAR_PROG_STAT"    , "2"        ); //상차도착
	    	jrParam.setField("YD_CARLD_STOP_LOC"   , ydStkColGp );	    	
	    	jrParam.setField("YD_CARLD_ARR_DT"     , commUtils.getDateTime14());
	    	
	    	/*
			UPDATE TB_YD_CARSCH
			   SET MODIFIER      = :V_MODIFIER
			     , MOD_DDTT      = SYSDATE
			     , YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
			     , YD_PNT_CD1           = :V_YD_PNT_CD1         
			     , YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT   
			     , YD_CARLD_STOP_LOC    = :V_YD_CARLD_STOP_LOC  
			     , YD_CARLD_ARR_DT      = TO_DATE(:V_YD_CARLD_ARR_DT, 'YYYYMMDDHH24MISS')    
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID     
	    	 */
	    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarschLdWbookId", logId, mthdNm, "작업예약 수정");
	    	
	    	/**
			 * 만약 다른 정지위치로 도착하는 경우에는 예약된 차량정지POINT를 CLEAR해 줄 필요가 있음
			 */
	    	jrParam.setField("TRN_EQP_CD"          , sTrnEqpCd);
	    	/*
			UPDATE TB_YD_STKCOL
			   SET MODIFIER      = :V_MODIFIER
			     , MOD_DDTT      = SYSDATE
			     , TRN_EQP_CD    = NULL
			     , YD_CAR_USE_GP = NULL
			 WHERE TRN_EQP_CD       = :V_TRN_EQP_CD
	    	 */
	    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStkcolTrnEqpCdToNull", logId, mthdNm, "운송장비코드 수정");
	    	
	    	commUtils.printLog(logId, "예약된 차량정지 포인트 삭제", "SL");
	    	
	    	//상차정지위치 Map Open(적치열, 적치베드, 적치단 활성화)
	    	jrParam.setField("YD_STK_COL_GP"      , ydStkColGp);
	    	jrParam.setField("YD_CAR_USE_GP"      , ydCarUseGp);
	    	jrParam.setField("TRN_EQP_CD"         , sTrnEqpCd );
//	    	jrParam.setField("YD_STKBED_USG_CD"   , ""        ); //목적동 초기화
	    	jrParam.setField("YD_STK_COL_ACT_STAT", "L"       ); //적치가능
	    	
	    	/*
			UPDATE TB_YD_STKCOL
			   SET MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			     , TRN_EQP_CD          = :V_TRN_EQP_CD
			     , YD_CAR_USE_GP       = :V_YD_CAR_USE_GP
			     , YD_STKBED_USG_CD    = :V_YD_STKBED_USG_CD
			     , YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
	    	 */
	    	intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStkcolCarStopLocOpen", logId, mthdNm, "상차정지위치 활성화");
	    	if (intRtnVal < 1) {
				throw new Exception("상차정지위치 상태 활성화 시 오류발생");
			}
	    	
	    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
	    	this.YdCarPointinforeg("1","",sTrnEqpCd,"","","","C",logId,mthdNm, sModifier);
		    
			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		    this.YdCarPointinforeg("3","",sTrnEqpCd,ydStkColGp,"","","L",logId,mthdNm, sModifier);
		    
		    
		    //적치베드 상태 활성화등록
		    jrParam.setField("YD_STK_BED_WT_MAX"  , sTrnEqpStkCapa);
			jrParam.setField("YD_STK_BED_ACT_STAT", "L");
		    /*
		    UPDATE TB_YD_STKBED
			   SET MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			     , YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
			     , YD_STK_BED_WT_MAX   = :V_YD_STK_BED_WT_MAX
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			 */
		    intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStkbedYdStkColGp", logId, mthdNm, "적치번지 활성화");
		    if (intRtnVal < 1) {
				throw new Exception("상차정지위치 적치베드 상태 활성화 시 오류발생");
			} 
		    
			//적치단 활성화 적치단활성상태는 'L' -> 'E'로 변경
		    jrParam.setField("YD_STK_COL_GP"		, ydStkColGp);
		    jrParam.setField("YD_STK_LYR_ACT_STAT"	, "E");
		    jrParam.setField("YD_STK_LYR_MTL_STAT"	, "E");
		    jrParam.setField("STL_NO"				, "");
		    /*
		    UPDATE TB_YD_STKLYR   
		       SET MODIFIER            = :V_MODIFIER
		         , MOD_DDTT            = SYSDATE
		         , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
		         , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
		         , STL_NO              = :V_STL_NO
		     WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
		    */
	    	intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStklyrYdStkColGp", logId, mthdNm, "적치단 활성화");
			if (intRtnVal < 1) {
				throw new Exception("상차정지위치 적치단 상태 활성화 시 오류발생");
			} 
		    
			/**********************************************************
			* 저장위치제원정보 송신 (YDY5L001)
			**********************************************************/
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setField("YD_INFO_SYNC_CD"		, "4"		 ); //야드정보동기화코드
			sndL2Msg.setField("YD_STK_COL_GP"    	, ydStkColGp );
			sndL2Msg.setField("YD_STK_BED_NO"	    , "01"       );
			sndL2Msg.setField("YD_CAR_PROG_STAT"    , "2"        );
			sndL2Msg.setField("YD_EQP_WRK_STAT"  	, "U"        ); 
			
			jrRtn = commUtils.addSndData(jrRtn,coilDao.getMsgL2("YDY5L001", sndL2Msg)); 

			/**********************************************************
			 * 크레인 스케줄 기동
			 **********************************************************/
			/* 
			SELECT NVL(YD_FRM_YN,'N') AS YD_FRM_YN
			  FROM USRYDA.TB_YD_CARPOINT
			 WHERE 1=1 
			   AND YD_STK_COL_GP = :V_YD_STK_COL_GP 
			   AND YD_GP  = 'J'
			   AND DEL_YN = 'N'
            */
			JDTORecordSet jsYdFrmYn = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCarpointByFrmYn", logId, mthdNm, "형상 비형상 판단");

			String ydFrmYn = commUtils.nvl(jsYdFrmYn.getRecord(0).getFieldString("YD_FRM_YN"), "N"); //차량형상여부

			commUtils.printLog(logId, "차량형상 존재여부 : " + ydFrmYn, "SL");

			
			jrParam.setField("FRTOMOVE_WORD_NO", sFrtomoveWordNo);
			/*
			SELECT WB.YD_WBOOK_ID
			     , SL.YD_STK_COL_GP
			     , SL.YD_STK_BED_NO
			     , SL.YD_STK_LYR_NO
			     , SL.STL_NO
			  FROM TB_YD_WRKBOOK    WB
			     , TB_YD_WRKBOOKMTL WM
			     , TB_YD_STOCK      ST
			     , TB_YD_STKLYR     SL
			 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
			   AND WM.STL_NO      = ST.STL_NO
			   AND ST.STL_NO      = SL.STL_NO
			   AND WB.DEL_YN = 'N'
			   AND WM.DEL_YN = 'N'
			   AND ST.DEL_YN = 'N'
			   AND  ((ST.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE AND ST.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO)
			       OR ST.CAR_FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO)
			   --결속장작업시 공냉재이송(1통로) 작업안함
			   AND 'Y' = (CASE WHEN WB.YD_SCH_CD IN ('JBPT21UM', 'JDPT21UM') 
			                    AND (SELECT MATL_SUP_MTD_GP 
			                           FROM TB_YD_STKCOL
			                          WHERE YD_STK_COL_GP LIKE 'J'||WB.YD_BAY_GP||'FE%') = 'Y'
			                   THEN 'N' --스케줄 기동안함
			                   ELSE 'Y' 
			               END)			       
			 ORDER BY SL.YD_STK_LYR_NO DESC 
			        , WB.YD_WBOOK_ID
			 */
			JDTORecordSet jsCarLdSeq = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getCarLoadSeq", logId, mthdNm, "스케줄기동 순서");
			
			if (jsCarLdSeq.size() > 0 && "N".equals(ydFrmYn)) {
			
				JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_SCH_CD", ""); //야드스케쥴코드
				jrParam.setField("YD_EQP_ID", ""); //야드설비ID

				//적치된 대상 스케줄 호출
				EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this);
				
				for (int i = 0; i < jsCarLdSeq.size(); i++) {
					jrParam.setField("YD_WBOOK_ID", jsCarLdSeq.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
					jrCrnSchMsg = (JDTORecord)ejbConn.trx("procYDYDJ551", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}
				
				jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
				commUtils.printLog(logId, "크레인스케줄 기동", "SL");
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
	 *      [A] 오퍼레이션명 :  준비스케줄원복
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String restorePrepSch(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "준비스케줄원복[CCoilCarMvSeEJB.restorePrepSch] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		String rtn    = "";
		try {
			commUtils.printLog(logId, mthdNm, "S+");

			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
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
			JDTORecordSet jsPrepSch = commDao.select(rcvMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdPrepschByYdWbookId", logId, mthdNm, "준비스케줄 조회");

			if (jsPrepSch.size() < 1) {
				return "준비스케줄 조회 실패";
			}
			
			String ydPrepSchId = commUtils.trim(rcvMsg.getFieldString("YD_PREP_SCH_ID"));
			
			jrParam.setField("YD_PREP_SCH_ID", ydPrepSchId);
			jrParam.setField("DEL_YN"        , "N"        );
			jrParam.setField("YD_WBOOK_ID"   , ""         );
			
			/*
			UPDATE TB_YD_PREPMTL
			   SET MODIFIER       = :V_MODIFIER
			     , MOD_DDTT       = SYSDATE
			     , DEL_YN         = :V_DEL_YN
			 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
			 */
			commDao.update(rcvMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delYdPrepmtlByPrepSchId1", logId, mthdNm, "준비스케줄재료 원복");
			
			/*
			UPDATE TB_YD_PREPSCH
			   SET DEL_YN         = :V_DEL_YN
			     , YD_WBOOK_ID    = :V_YD_WBOOK_ID
			     , MODIFIER       = :V_MODIFIER
			     , MOD_DDTT       = SYSDATE
			 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
			 */
			 commDao.update(rcvMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delYdPrepsch1", logId, mthdNm, "준비스케줄 원복");
	    	 
			commUtils.printLog(logId, mthdNm, "S-");

			return rtn;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return rtn;
		} catch (Exception e) {
			return rtn;
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 :  차량정지위치활성/비활성처리
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String procCarPosActiveOrInActive(JDTORecord rcvMsg) throws DAOException {

		String mthdNm = "차량정지위치활성/비활성처리[CCoilCarMvSeEJB.procCarPosActiveOrInActive] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		String rtn    = "";
		try {
			commUtils.printLog(logId, mthdNm, "S+");

			String ydStkColGp       = commUtils.nvl(rcvMsg.getFieldString("YD_STK_COL_GP"      ), "");
			String ydCarUseGp       = commUtils.nvl(rcvMsg.getFieldString("YD_CAR_USE_GP"      ), "");
			String sTrnEqpCd        = commUtils.nvl(rcvMsg.getFieldString("TRN_EQP_CD"         ), "");
			String sCarNo           = commUtils.nvl(rcvMsg.getFieldString("CAR_NO"             ), "");
			String sCardNo          = commUtils.nvl(rcvMsg.getFieldString("CARD_NO"            ), "");
			String sTrnEqpStkCapa   = commUtils.nvl(rcvMsg.getFieldString("TRN_EQP_STK_CAPA"   ), "");
			String ydStkColActStat  = commUtils.nvl(rcvMsg.getFieldString("YD_STK_COL_ACT_STAT"), "");
			String sModifier        = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)


			if ("".equals(ydStkColGp)) {
				return "적치열이 존재하지 않습니다.";
			}
			
			if ("L".equals(ydCarUseGp)) { //구내운송
				if ("L".equals(ydStkColActStat) && "".equals(sTrnEqpCd)) { //적치가능
					return "운송장비코드가 존재하지 않습니다.";
				}
			}
			
			if ("G".equals(ydCarUseGp)) { //출하
				if ("L".equals(ydStkColActStat) && ("".equals(sCarNo) || "".equals(sCardNo))) {
					return "차량번호 또는 카드번호가 존재하지 않습니다.";
				}
			}
			
			String ydStkBedActStat = "";
			String ydStkLyrActStat = "";
			
			if ("L".equals(ydStkColActStat)) { //적치가능
				ydStkBedActStat = "L";
				ydStkLyrActStat = "E";
			} else if ("C".equals(ydStkColActStat)) { //비활성화
				ydStkBedActStat = "C";
				ydStkLyrActStat = "C";
				sTrnEqpStkCapa 	= CConstant.YD_STK_BED_WT_MAX_DEFAULT;
				ydCarUseGp      = "";
			} else {
				return "적치열상태값은 사용할 수 없는 값입니다.";
			}
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("YD_STK_COL_GP"      , ydStkColGp     );
	    	jrParam.setField("YD_CAR_USE_GP"      , ydCarUseGp     );
	    	jrParam.setField("TRN_EQP_CD"         , sTrnEqpCd      );
	    	jrParam.setField("CAR_NO"             , sCarNo         );
	    	jrParam.setField("CARD_NO"            , sCardNo        );
	    	jrParam.setField("YD_STK_COL_ACT_STAT", ydStkColActStat);

	    	/* 
			UPDATE TB_YD_STKCOL
			   SET YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
			     , YD_CAR_USE_GP       = :V_YD_CAR_USE_GP
			     , TRN_EQP_CD          = :V_TRN_EQP_CD
			     , CAR_NO              = :V_CAR_NO
			     , CARD_NO             = :V_CARD_NO
			     , MOD_DDTT            = SYSDATE
			     , MODIFIER            = :V_MODIFIER
			 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
	    	 */
	    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStkcol05", logId, mthdNm, "적치열 수정"); 

	    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
	    	
	    	/*
			UPDATE TB_YD_CARPOINT
			   SET YD_STK_COL_ACT_STAT=:V_YD_STK_COL_ACT_STAT
			     , CAR_NO   = :V_CAR_NO
			     , CARD_NO  = :V_TRN_EQP_CD
			     , MOD_DDTT = SYSDATE
			     , MODIFIER = :V_MODIFIER
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP  
	    	 */
	    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointtrneqpcdupdateC2", logId, mthdNm, "차량포인트 수정");
	    	
	    	
	    	jrParam.setField("YD_STK_BED_WT_MAX"   , sTrnEqpStkCapa );
			jrParam.setField("YD_STK_BED_ACT_STAT" , ydStkBedActStat);
			/*
			UPDATE TB_YD_STKBED
			   SET MODIFIER  = :V_MODIFIER
			     , MOD_DDTT  = SYSDATE
			     , YD_STK_BED_WT_MAX   = :V_YD_STK_BED_WT_MAX
			     , YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkBedInit", logId, mthdNm, "적치BED 수정");
			
			
			
			jrParam.setField("YD_STK_LYR_ACT_STAT" , ydStkLyrActStat);
			jrParam.setField("STL_NO"              , "");
			jrParam.setField("YD_STK_LYR_MTL_STAT" , "E");
	    	/*
			UPDATE TB_YD_STKLYR
			   SET MODIFIER  = :V_MODIFIER
			     , MOD_DDTT  = SYSDATE
			     , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
			     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
			     , STL_NO              = :V_STL_NO
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
	    	 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStkLyrInit", logId, mthdNm, "적치단 수정");
	    	 
			commUtils.printLog(logId, mthdNm, "S-");

			return rtn;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return rtn;
		} catch (Exception e) {
			return rtn;
		}
	}		

	
	/**
	 * [A] 오퍼레이션명 : 차량상차작업등록 후 준비스케줄 삭제처리
	 * 기존 : String procYdWbookForCarLd(String szWLOC_CD,String szTRN_EQP_CD, String szYD_STK_COL_GP, JDTORecord recOut)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procYdWbookForCarLd(JDTORecord rcvMsg)throws DAOException  {
		String mthdNm = "준비스케줄 삭제[CCoilCarMvSeEJB.procYdWbookForCarLd] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	    
	    try{
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			String sWlocCd    = commUtils.trim(rcvMsg.getFieldString("WLOC_CD"      )); //개소코드
			String sTrnEqpCd  = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"   )); //운송장비코드
			String ydStkColGp = commUtils.trim(rcvMsg.getFieldString("YD_STK_COL_GP")); //
			String ydLocGp    = commUtils.trim(rcvMsg.getFieldString("YD_LOC_GP"    ));
			
			String sModifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
			String sMsg = "";
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			//파라메터 JDTORecord 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			String ydBayGp = ydStkColGp.substring(1, 2);
			jrParam.setField("YD_BAY_GP"  , ydBayGp);
			/***********************************
			 * 1. 크레인설비정보를 먼저 조회
			 ***********************************/
			/*
			SELECT A.YD_EQP_ID
			     , A.YD_EQP_NAME
			     , MAX(A.YD_GP)       AS YD_GP
			     , MAX(A.YD_EQP_GP)   AS YD_EQP_GP
			     , MAX(A.YD_EQP_NO)   AS YD_EQP_NO
			     , MAX(A.YD_EQP_STAT) AS YD_EQP_STAT
			     , MAX(A.YD_CURR_BAY_GP) AS YD_CURR_BAY_GP
			     , MAX(A.YD_HOME_BAY_GP) AS YD_HOME_BAY_GP
			     , MAX(A.YD_CRN_USE_SEQ) AS YD_CRN_USE_SEQ
			     , MAX(NVL(A.YD_CRN_CONT_CARASGN_CNT, 0)) AS YD_CRN_CONT_CARASGN_CNT
			     , MAX(NVL(A.YD_CRN_CONT_CARASGN_WR, 0)) AS YD_CRN_CONT_CARASGN_WR
			     , SUM(DECODE(SUBSTR(B.YD_SCH_CD, 2, 1), 'A', 1, 0)) AS A_FRTOMOVE_LOT_CNT
			     , SUM(DECODE(SUBSTR(B.YD_SCH_CD, 2, 1), 'B', 1, 0)) AS B_FRTOMOVE_LOT_CNT
			     , SUM(DECODE(SUBSTR(B.YD_SCH_CD, 2, 1), 'C', 1, 0)) AS C_FRTOMOVE_LOT_CNT
			     , SUM(DECODE(SUBSTR(B.YD_SCH_CD, 2, 1), 'D', 1, 0)) AS D_FRTOMOVE_LOT_CNT
			     , SUM(DECODE(SUBSTR(B.YD_SCH_CD, 2, 1), 'E', 1, 0)) AS E_FRTOMOVE_LOT_CNT
			     , SUM(DECODE(SUBSTR(B.YD_SCH_CD, 2, 1), 'F', 1, 0)) AS F_FRTOMOVE_LOT_CNT
			     , SUM(DECODE(SUBSTR(B.YD_SCH_CD, 2, 1), 'G', 1, 0)) AS G_FRTOMOVE_LOT_CNT
			     , SUM(DECODE(SUBSTR(B.YD_SCH_CD, 2, 1), 'H', 1, 0)) AS H_FRTOMOVE_LOT_CNT
			     , COUNT(B.YD_SCH_CD) AS FRTOMOVE_LOT_TOTAL
			  FROM TB_YD_EQP     A
			     , TB_YD_PREPSCH B
			 WHERE A.YD_EQP_ID = B.YD_WRK_PLAN_CRN(+)
			   AND A.YD_GP     = 'J'
			   AND A.YD_GP     = B.YD_GP(+)
			   AND A.YD_BAY_GP LIKE :V_YD_BAY_GP || '%'
			   AND A.YD_EQP_ID LIKE 'J_CR%'
			   AND A.DEL_YN    = 'N'
			   AND B.DEL_YN(+) = 'N'
			 GROUP BY A.YD_EQP_ID
			        , A.YD_EQP_NAME
			 ORDER BY YD_EQP_GP DESC
			        , A.YD_EQP_ID
			 */
			JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getCarAsgnStdByCrn", logId, mthdNm, "크레인조회");

			if (jsCrn.size() < 0) {
				throw new Exception("크레인 조회 실패");
			}
			
			String[] sCrnEqpId = new String[jsCrn.size()];
			
			/***********************************
			 * 2. 해당동의 차량상차 크레인스케줄정보를 조회
			 ***********************************/

			/*
			SELECT DISTINCT YD_EQP_ID
			  FROM TB_YD_CRNSCH
			 WHERE YD_SCH_CD LIKE 'J' || :V_YD_BAY_GP || 'PT__U_'
			   AND DEL_YN = 'N'
			 ORDER BY YD_EQP_ID ASC
			 */
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCrnschYdEqpForCarLd", logId, mthdNm, "크레인스케줄설비 조회");
			
			String ydEqpId = ""; //크레인
			boolean bEqual = false;
			int nRow = 0;
			
			if (jsCrnSch.size() > 0) {
				
				for (int i = 1; i <= jsCrn.size(); ++i) { //동별 크레인 대수
					jsCrn.absolute(i);
					ydEqpId = commUtils.trim(jsCrn.getRecord().getFieldString("YD_EQP_ID"));
					
					for (int j = 1; j <= jsCrnSch.size(); ++j) {
						jsCrnSch.absolute(j);
						
						if (jsCrnSch.getRecord().getFieldString("YD_EQP_ID").equals(ydEqpId)) {
							bEqual = true;
							break;
						}
					}
					
					if (bEqual) {
						bEqual = false;
					} else {
						sCrnEqpId[nRow] = ydEqpId;
						nRow++;
					}
				}
				
				for (int j = 1; j <= jsCrnSch.size(); ++j) {
					jsCrnSch.absolute(j);
					sCrnEqpId[nRow] = jsCrnSch.getRecord().getFieldString("YD_EQP_ID");
					nRow++;
				}
			} else if (jsCrnSch.size() == 0) {
				for (int i = 1; i <= jsCrn.size(); ++i) { //동별 크레인 대수
					jsCrn.absolute(i);
					sCrnEqpId[i-1] = commUtils.trim(jsCrn.getRecord().getFieldString("YD_EQP_ID"));
				}
			} else {
				throw new Exception("크레인 스케줄 조회 실패");
			}
			
			/***********************************
			 * 3. 도착동의 차량종류별(Pallet/Trailer) 빠른 준비스케줄조회
			 ***********************************/
			String ydSchCd   = "";
			String ydWbookId = "";
			commUtils.printLog(logId, "▶▶▶ydStkColGp:"+ydStkColGp, "SL");
			
			if ("J".equals(ydLocGp)) {
				if (   ("B".equals(ydBayGp) || "C".equals(ydBayGp)) 
					&& ("4".equals(ydStkColGp.substring(5, 6)) || "5".equals(ydStkColGp.substring(5, 6)))) {
					ydSchCd = ydStkColGp.substring(0, 2) + "PT52UM"; //이송2(제품2통로)
				} else {
					ydSchCd = ydStkColGp.substring(0, 2) + "PT02UM"; //이송출고(제품1통로)
				}
			} else {
				ydSchCd = ydStkColGp.substring(0, 2) + "PT";  
			}
			
			commUtils.printLog(logId, "□□□ydSchCd:"+ydSchCd, "SL");
			commUtils.printLog(logId, "□□□sTrnEqpCd:"+ydSchCd, "SL");
			
			jrParam.setField("YD_SCH_CD"        , ydSchCd     ); 
			jrParam.setField("YD_PREP_WK_ST"    , "L"         ); 
			jrParam.setField("CAR_GP"           , sTrnEqpCd.substring(1, 2));
			
			JDTORecordSet rsRst = JDTORecordFactory.getInstance().createRecordSet("");
			boolean bExist      = false;
			
			for (int i = 0; i < sCrnEqpId.length; ++i) {
				
				jrParam.setField("YD_WRK_PLAN_CRN", sCrnEqpId[i]);
				
				//이송 lot 편성대상 가져오기
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
				          FROM ( 
				                SELECT *
				                  FROM (
				                        SELECT *
				                          FROM TB_YD_PREPSCH A
				                         WHERE YD_GP = 'J'
				                           AND YD_SCH_CD        LIKE :V_YD_SCH_CD || '%'
				                           AND YD_PREP_WK_ST    LIKE :V_YD_PREP_WK_ST || '%'
				                           AND NVL(CAR_GP, '*') LIKE :V_CAR_GP || '%'
				                           AND A.DEL_YN = 'N'
				                         ORDER BY YD_CARASGN_SEQ ASC
				                                , YD_PREP_SCH_ID ASC
				                       )
				                  WHERE ROWNUM < 2
				               ) A
				             , TB_YD_PREPMTL B
				         WHERE A.YD_PREP_SCH_ID = B.YD_PREP_SCH_ID
				           AND B.DEL_YN = 'N'
				       ) B
				 WHERE A.STL_NO = B.STL_NO
				   AND A.DEL_YN = 'N'
				 ORDER BY B.YD_STK_COL_GP ASC
				        , B.YD_STK_BED_NO DESC
				        , B.YD_STK_LYR_NO DESC
				 */
				rsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStockNPrepSchByYdCrnCarGp", logId, mthdNm, "차량이송준비스케줄 조회");
				if (rsRst.size() > 0) {
					commUtils.printLog(logId, "이미 등록된 차량이송준비스케줄 조회", "SL");
					bExist = true;
					break;
				} else {
					commUtils.printLog(logId, "이미 등록된 차량이송준비스케줄 조회 대상 존재하지 않음", "SL");
				}
			} //end for
			
		    if (!bExist) {
		    	// 상차 Lot 편성 대상 재료 Select ###################################
		    	jrParam = JDTORecordFactory.getInstance().create();
		 	    jrParam.setField("SPOS_WLOC_CD", sWlocCd         ); 
		 	    jrParam.setField("TRN_EQP_CD"  , sTrnEqpCd       ); 
		 	    jrParam.setField("YD_BAY_CD"   , ydStkColGp.substring(0, 2)); 
		  
				// 저장품 테이블 조회
		 	    rsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStockYdStkLyrMatlLotC2", logId, mthdNm, "저장품 조회");
				
				if (rsRst.size() > 0) {					
					bExist = true;
		    	} 
		    }
			
		    if (!bExist) {
		    	commUtils.printLog(logId, "이미 등록된 차량이송준비스케줄을 조회 후 대상재가 존재하지 않습니다", "SL");
		    } else {
		    	rsRst.first();
		    	String ydPrepSchId = commUtils.trim(rsRst.getRecord().getFieldString("YD_PREP_SCH_ID"));
				String ydAimYdGp   = commUtils.trim(rsRst.getRecord().getFieldString("YD_AIM_YD_GP"  ));
				String ydAimBayGp  = commUtils.trim(rsRst.getRecord().getFieldString("YD_AIM_BAY_GP" ));
				
				if (!"".equals(ydPrepSchId)) {
					ydSchCd = commUtils.trim(rsRst.getRecord().getFieldString("YD_SCH_CD" ));
				} else {
					
					
					if ("J".equals(ydLocGp)) {
						if (("B".equals(ydBayGp) || "C".equals(ydBayGp)) 
								&& (ydStkColGp.substring(5, 6).equals("4") || ydStkColGp.substring(5, 6).equals("5"))) {
							ydSchCd = ydStkColGp.substring(0, 2) + "PT52UM"; //이송2(제품2통로);
						} else {
							ydSchCd = ydStkColGp.substring(0, 2) + "PT02UM"; //이송출고(제품1통로)
						}
					} else { //소재야드 인경우 20220727 CHITO 수정
						if (ydStkColGp.substring(5, 6).equals("3")) {
							ydSchCd = ydStkColGp.substring(0, 2) + "PT03UH"; //이송(3통로) 소재통로
						} else{
							ydSchCd = ydStkColGp.substring(0, 2) + "PT01UH"; //이송(1통로) 제품1통로
						}
					}
				}
				
				//스케줄 기준 조회
				jrParam.setField("YD_SCH_CD"     , ydSchCd);
				/*
				SELECT A.*
				     , CASE WHEN WRK_EQUIP_STAT != 'B' THEN YD_WRK_CRN_PRIOR
				            WHEN WRK_EQUIP_STAT  = 'B' AND YD_ALT_CRN_YN = 'Y' THEN CASE WHEN ALT_EQUIP_STAT != 'B' THEN YD_ALT_CRN_PRIOR ELSE 99 END
				            ELSE 99
				        END AS YD_SCH_PRIOR
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
				JDTORecordSet jsSchrule = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdSchrule", logId, mthdNm, "스케줄 기준 조회");
				String ydSchPrior = "";
				if (jsSchrule.size() > 0) {
					ydSchPrior = commUtils.trim(jsSchrule.getRecord(0).getFieldString("YD_SCH_PRIOR"));
					if ("99".equals(ydSchPrior)) {
						ydSchPrior = "";
						commUtils.printLog(logId, "작업가능한 크레인이 없습니다.", "SL");
					}
				}
				
				for (int i = 1; i <= rsRst.size(); ++i) {
					//작업예약 Id생성
					ydWbookId = coilDao.getSeqId(logId, mthdNm, "WrkBook");
					
					JDTORecord jrWbook = commUtils.getParam(logId, mthdNm, sModifier);
					jrWbook.setField("YD_WBOOK_ID"   , ydWbookId );
					jrWbook.setField("YD_GP"         , "J"       );
					jrWbook.setField("YD_BAY_GP"     , ydBayGp   );
					jrWbook.setField("YD_SCH_PRIOR"  , ydSchPrior);
					jrWbook.setField("YD_SCH_CD"     , ydSchCd   );
					jrWbook.setField("YD_CAR_USE_GP" , "L"       ); //구내운송
					jrWbook.setField("TRN_EQP_CD"    , sTrnEqpCd );
					jrWbook.setField("YD_AIM_YD_GP"  , ydAimYdGp );
					jrWbook.setField("YD_AIM_BAY_GP" , ydAimBayGp);
					
					commDao.insert(jrWbook, "com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.insYdWrkbook", logId, mthdNm, "작업예약 생성(준비스케쥴)");
				
					rsRst.absolute(i);
					
					jrWbook.setField("STL_NO"          , rsRst.getRecord().getFieldString("STL_NO"       ));
					jrWbook.setField("YD_STK_COL_GP"   , rsRst.getRecord().getFieldString("YD_STK_COL_GP"));
					jrWbook.setField("YD_STK_BED_NO"   , rsRst.getRecord().getFieldString("YD_STK_BED_NO"));
					jrWbook.setField("YD_STK_LYR_NO"   , rsRst.getRecord().getFieldString("YD_STK_LYR_NO"));
					jrWbook.setField("YD_UP_COLL_SEQ"  , "" + i );
					
					commDao.insert(jrWbook, "com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.insYdWrkbookMtl", logId, mthdNm, "작업예약재료 생성(준비스케쥴)");
				}
				
				if (!"".equals(ydPrepSchId)) {
					
					jrParam.setField("YD_PREP_SCH_ID", ydPrepSchId);
					jrParam.setField("YD_WBOOK_ID"   , ydWbookId  );
					jrParam.setField("DEL_YN"        , "Y"        );
					
					/*
					UPDATE TB_YD_PREPMTL
					   SET MODIFIER       = :V_MODIFIER
					     , MOD_DDTT       = SYSDATE
					     , DEL_YN         = :V_DEL_YN
					 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delYdPrepmtlByPrepSchId1", logId, mthdNm, "준비스케줄재료 삭제");
					/*
					UPDATE TB_YD_PREPSCH
					   SET DEL_YN         = :V_DEL_YN
					     , YD_WBOOK_ID    = :V_YD_WBOOK_ID
					     , MODIFIER       = :V_MODIFIER
					     , MOD_DDTT       = SYSDATE
					 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
					*/
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delYdPrepsch1", logId, mthdNm, "준비스케줄 삭제");
				}
		    }
			
			jrRtn.setField("YD_WBOOK_ID", ydWbookId);
			jrRtn.setField("YD_SCH_CD"  , ydSchCd  );

			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}		
	
	
	/**
	 * 오퍼레이션명 : 소재차량 영차도착 실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procUDMatlCarArr(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "소재차량 영차도착 실적[CCoilCarMvSeEJB.procUDMatlCarArr] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try{
			
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
	    	//----------------------------------------------------------------------------------------------------------
	    	//	파라미터 값 확인
	    	//----------------------------------------------------------------------------------------------------------
			String sMsg           = "";

			String sMsgGp         = commUtils.trim(rcvMsg.getFieldString("MSG_GP"              )); //전문구분
			String sArrWlocCd     = commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD"         )); //착지개소코드
			String sArrYdPntCd    = commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD"       )); //착지야드포인트코드
			String ydWbookId      = commUtils.trim(rcvMsg.getFieldString("YD_CARUD_WRK_BOOK_ID"));
//	    	String ydSchCd        = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"           ));	    	
			String ydEqpId        = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"           ));
	    	String ydCarUseGp     = commUtils.trim(rcvMsg.getFieldString("YD_CAR_USE_GP"       ));
			String sTrnEqpCd      = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"          )); //운송장비코드
			String sTrnEqpStkCapa = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA"    ));
			String ydCarSchId     = commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"    ));

//	    	String ydCarProgStat  = commUtils.trim(rcvMsg.getFieldString("YD_CAR_PROG_STAT" ));

	    	String sModifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"           )); //수정자(Backup Only)

	    	String sTrnWrkFullvoidGp = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP" )); //F영차

	    	//----------------------------------------------------------------------------------------------------------
	    	//	착지개소와 포인트코드로 적치열을 조회 후 차량정지위치 상태 체크
			//----------------------------------------------------------------------------------------------------------
	    	commUtils.printLog(logId, "1. 차량정지 위치 조회", "SL");
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		    jrParam.setField("WLOC_CD"		, sArrWlocCd);
		    jrParam.setField("YD_PNT_CD"	, sArrYdPntCd);
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
	    	JDTORecordSet jsStkCol = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStkcolWLocCdandPntCd", logId, mthdNm, "적치열 조회");
	    	if (jsStkCol.size() <= 0) {
				sMsg = "발지개소["+sArrWlocCd+"] 및 포인트 코드["+sArrYdPntCd+"] 적치열 조회 실패!";
	    		commUtils.printLog(logId, sMsg, "SL");
	    		jrRtn.setField("RTN_CD"	, "0");	
	    		return jrRtn;
	    	}
		
	    	
	    	jsStkCol.absolute(1);
			JDTORecord jrStkCol = jsStkCol.getRecord();
			
	    	//차량 정지위치 적치열구분
	    	
	    	String ydStkColGp 		= commUtils.trim(jrStkCol.getFieldString("YD_STK_COL_GP" ));
	    	String ydStkColActStat 	= commUtils.trim(jrStkCol.getFieldString("YD_STK_COL_ACT_STAT" ));

	    	if ("L".equals(ydStkColActStat) ) {
	    		commUtils.printLog(logId, "하차정지위치가 이미 활성상태입니다", "SL");
	    	}
	    	
	    	if ("N".equals(ydStkColActStat)) {
	    		sMsg = "하차정지위치가 사용 불가 상태입니다.";
	    		jrRtn.setField("RTN_CD"	, "0");
	    		jrRtn.setField("RTN_MSG", sMsg);
	    		return jrRtn;
	    	}

	    	//----------------------------------------------------------
			//	하차 작업예약 생성(하차동이 변경된 경우 )
			//----------------------------------------------------------
	    	commUtils.printLog(logId, "2. 하차작업예약이 없는 경우", "SL");
	    	if ("".equals(ydWbookId) && "F".equals(sTrnWrkFullvoidGp)) {
	    		
	    		commUtils.printLog(logId, "하차 작업예약 생성 시작(하차동이 변경된 경우)", "SL");
	    		jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			    jrParam.setField("YD_CAR_SCH_ID"    , ydCarSchId);
			    jrParam.setField("YD_EQP_ID"        , ydEqpId   );
		    	jrParam.setField("TRN_EQP_CD"       , sTrnEqpCd );
		    	jrParam.setField("YD_CAR_USE_GP"    , ydCarUseGp);
		    	jrParam.setField("WLOC_CD"          , sArrWlocCd);
		    	jrParam.setField("YD_CARUD_STOP_LOC", ydStkColGp);
			    
				jrRtn = commUtils.addSndData(jrRtn, this.makeCarUdWrkBookAB(jrParam)); 
				
				JDTORecordSet jsCarsch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCarsch", logId, mthdNm, "차량스케줄 조회");
				
				if (jsCarsch.size() <=0 ) {
					commUtils.printLog(logId, "차량스케줄 조회 오류", "S-");
					jrRtn.setField("RTN_CD"	, "0");
					return jrRtn;
				}
				
				ydWbookId = jsCarsch.getRecord(0).getFieldString("YD_CARUD_WRK_BOOK_ID");
				
	    	}
	    	
	    	//-------------------------------------------------------------------
	    	//	차량스케줄에 하차작업예약id, 차량진행상태(하차도착) 등록
	    	//-------------------------------------------------------------------
	    	commUtils.printLog(logId, "3. 차량스케줄 수정(B하차도착, 하차작업예약ID)", "SL");
			commUtils.printLog(logId, "차량스케줄["+ydCarSchId+"]에 하차작업예약["+ydWbookId+"], 착지개소코드["+sArrWlocCd+"], 차량진행상태[B], 하차정지위치["+ydStkColGp+"] 등록 시작", "SL");
			
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CAR_SCH_ID"		, ydCarSchId     );
			jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, ydWbookId      );
			jrParam.setField("ARR_WLOC_CD"			, sArrWlocCd     );
			jrParam.setField("YD_STK_COL_ACT_STAT"	, ydStkColActStat);
			jrParam.setField("YD_CAR_PROG_STAT"		, "B"            ); //하차도착
			jrParam.setField("YD_CARUD_STOP_LOC"	, ydStkColGp     ); //실제 차량이 들어온 하차정지위치를 재등록
			jrParam.setField("YD_CARUD_ARR_DT"		, commUtils.getDateTime14()); 

			/* 
			UPDATE TB_YD_CARSCH
			   SET MODIFIER             = :V_MODIFIER
			     , MOD_DDTT             = SYSDATE
			     , YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT
			     , YD_CARUD_LEV_DT      = TO_DATE(:V_YD_CARUD_LEV_DT, 'YYYYMMDDHH24MISS')
			     , YD_CARUD_WRK_BOOK_ID = NVL(:V_YD_CARUD_WRK_BOOK_ID, YD_CARUD_WRK_BOOK_ID)
			     , YD_FRTOMOVE_BAY_GP   = NVL(:V_YD_FRTOMOVE_BAY_GP  , YD_FRTOMOVE_BAY_GP)
			 WHERE YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID
			*/ 
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarsch", logId, mthdNm, "차량스케쥴수정");
			
			//----------------------------------------------------------------------------------------------------------
	    	//작업예약재료에 적치열정보를 등록한다. 
	    	//작업예약ID로 작업예약재료를 조회
			//----------------------------------------------------------------------------------------------------------
			jrParam.setField("YD_WBOOK_ID"			, ydWbookId );
			jrParam.setField("YD_STK_COL_GP"        , ydStkColGp);
			/*
			UPDATE TB_YD_WRKBOOKMTL
			   SET MODIFIER       = :V_MODIFIER
			     , MOD_DDTT       = SYSDATE
			     , YD_STK_COL_GP  = :V_YD_STK_COL_GP
			 WHERE STL_NO IN (SELECT STL_NO
			                    FROM TB_YD_WRKBOOKMTL A
			                   WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			                     AND DEL_YN = 'N'
			                 )
			   AND YD_WBOOK_ID = :V_YD_WBOOK_ID    
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdWrkbookmtlYdstkcolGp", logId, mthdNm, "재료수정");
			
	    	/* **************************************************************************************
			 * 만약 다른 정지위치로 도착하는 경우에는 예약된 차량정지POINT를 CLEAR해 줄 필요가 있음
			 * ***************************************************************************************/
			commUtils.printLog(logId, "4. 적치열 Clear 및 실제 입동 적치열 재점유", "SL");
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRN_EQP_CD"		, sTrnEqpCd);
			/* 
			UPDATE TB_YD_STKCOL
			   SET TRN_EQP_CD       = null
			     , YD_CAR_USE_GP    = null
			 WHERE TRN_EQP_CD       = :V_TRN_EQP_CD
			*/ 
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStkcolTrnEqpCdToNull", logId, mthdNm, "열수정");	 
		
			/* **************************************************************************************
	    	 * 하차정지위치 Map Open(적치열, 적치베드, 적치단 활성화)
			 * ***************************************************************************************/
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_STK_COL_GP"		, ydStkColGp);
			jrParam.setField("YD_CAR_USE_GP"		, ydCarUseGp);
			jrParam.setField("TRN_EQP_CD"			, sTrnEqpCd);
			jrParam.setField("YD_STKBED_USG_CD"		, "");
			jrParam.setField("YD_STK_COL_ACT_STAT"	, "L");
	    	
	    	/*
	    	UPDATE TB_YD_STKCOL
	    	   SET YD_STK_COL_ACT_STAT = NVL(:V_YD_STK_COL_ACT_STAT, YD_STK_COL_ACT_STAT)
	    	     , YD_CAR_USE_GP       = NVL(:V_YD_CAR_USE_GP, YD_CAR_USE_GP)
	    	     , TRN_EQP_CD          = NVL(:V_TRN_EQP_CD, TRN_EQP_CD)
	    	     , MOD_DDTT            = SYSDATE
	    	     , MODIFIER            = :V_MODIFIER
	    	WHERE YD_STK_COL_GP        = :V_YD_STK_COL_GP
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStkcol01", logId, mthdNm, "상차도 활성화");
			
			/* 
			 * 기존 차량포인트 삭제후 실제 입도한 차량포인트 점유
			 */
			commUtils.printLog(logId, "5. 차량포인트 Clear 및 재점유", "SL");
			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		    this.YdCarPointinforeg("1","",sTrnEqpCd,"","","","C",logId,mthdNm, sModifier);
		    
			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		    this.YdCarPointinforeg("3","",sTrnEqpCd,ydStkColGp,"","","L",logId,mthdNm, sModifier);
			
			//----------------------------------------------------------------------------------------------------------
			//적치베드 상태 활성화등록
			//----------------------------------------------------------------------------------------------------------
		    commUtils.printLog(logId, "6. 적치BED 활성화", "SL");
		    jrParam.setField("YD_STK_BED_WT_MAX"	, sTrnEqpStkCapa);
		    jrParam.setField("YD_STK_COL_GP"		, ydStkColGp);
		    jrParam.setField("YD_STK_BED_ACT_STAT"	, "L");
			
			/* 
			UPDATE TB_YD_STKBED
			   SET MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			     , YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
			     , YD_STK_BED_WT_MAX   = :V_YD_STK_BED_WT_MAX
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStkbedYdStkColGp", logId, mthdNm, "TB_YD_STKBED Set"); 
			
			
			//출발시 생성된 작업예약이 삭제된 경우 신규 작업예약에서 재료정보를 호출 함
			commUtils.printLog(logId, "7. 적치BED 활성화", "SL");
			/* 
	    	SELECT A.YD_WBOOK_ID
	    	     , A.YD_SCH_CD
	    	     , A.STL_NO
	    	     , A.YD_UP_COLL_SEQ
	    	     , A.YD_STK_COL_GP
	    	     , NVL(A.YD_STK_BED_NO,ROW_NO)  AS YD_STK_BED_NO
	    	     , A.YD_STK_LYR_NO
	    	  FROM (
	    	        SELECT B.YD_WBOOK_ID    AS YD_WBOOK_ID
	    	             , B.YD_SCH_CD
	    	             , A.STL_NO         AS STL_NO
	    	             , A.YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ
	    	             , A.YD_STK_COL_GP  AS YD_STK_COL_GP
	    	             , A.YD_STK_BED_NO  AS YD_STK_BED_NO
	    	             , A.YD_STK_LYR_NO  AS YD_STK_LYR_NO
	    	             , LPAD(ROWNUM,2,'0') AS ROW_NO
	    	          FROM TB_YD_WRKBOOKMTL A
	    	             ,(SELECT *
	    	                 FROM (SELECT YD_WBOOK_ID
	    	                            , YD_SCH_CD
	    	                         FROM TB_YD_WRKBOOK
	    	                        WHERE DEL_YN = 'N'
	    	                          AND YD_CAR_USE_GP = :V_YD_CAR_USE_GP
	    	                          AND TRN_EQP_CD    = :V_TRN_EQP_CD
	    	                        ORDER BY YD_WBOOK_ID DESC
	    	                       ) C
	    	                 WHERE ROWNUM <= 1
	    	                ) B
	    	          WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
	    	          ORDER BY YD_UP_COLL_SEQ DESC
	    	                 , YD_STK_COL_GP
	    	                 , YD_STK_BED_NO
	    	                 , YD_STK_LYR_NO
	    	       ) A
	    	 WHERE 1 = 1
	    	*/ 
	    	JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getWrkBookMtlbyCarUsrGpTrnEqpCd", logId, mthdNm, "작업예약 조회");
	    	
	    	//----------------------------------------------------------------------------------------------------------
			//적치단 활성화
			//----------------------------------------------------------------------------------------------------------
			for (int Loop_i = 1; Loop_i <= jsWrkBook.size(); Loop_i++) {
				jsWrkBook.absolute(Loop_i);

				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		    	jrParam.setField("YD_STK_COL_GP"		, ydStkColGp);
		    	jrParam.setField("YD_STK_BED_NO"		, commUtils.trim(jsWrkBook.getRecord().getFieldString("YD_STK_BED_NO"))); 
		    	jrParam.setField("YD_STK_LYR_NO"		, "001");
		    	jrParam.setField("STL_NO"				, commUtils.trim(jsWrkBook.getRecord().getFieldString("STL_NO"))); 
		    	jrParam.setField("YD_STK_LYR_ACT_STAT"	, "E");
		    	jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C");
		    	
		    	/* 
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
		    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.updYdStkLyrYdStkColBedGp", logId, mthdNm, "TB_YD_STKBED Set");
		    	
			}
			
			/**********************************************************
			* 저장위치제원정보 송신 (YDY5L001)
			**********************************************************/
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_INFO_SYNC_CD"		, "4"				); //야드정보동기화코드
			jrParam.setField("YD_STK_COL_GP"    	, ydStkColGp		);
			jrParam.setField("YD_STK_BED_NO"    	, "01"				);
			jrParam.setField("YD_CAR_ARRSTRT_STAT" 	, "A"				); //A:도착, S:출발
			jrParam.setField("YD_CAR_USE_GP"    	, "L"				); //L:구내운송, G:출하차량
			jrParam.setField("YD_EQP_WRK_STAT"  	, "B"            	); 
			jrParam.setField("TRN_EQP_CD"  			, sTrnEqpCd			); //운송장비코드
			jrRtn = commUtils.addSndData(jrRtn,coilDao.getMsgL2("YDY5L001_CarInfo", jrParam));		
	
			/**********************************************************
			* 저장품제원(YDY5L002) 전문 생성
			**********************************************************/
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_INFO_SYNC_CD"		, "3"				); //야드정보동기화코드 1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
			jrParam.setField("MSG_GP"				, "I"				); //전문구분
			jrParam.setField("YD_STK_COL_GP"  		, ydStkColGp		); //야드적치열구분
//			jrParam.setField("YD_STK_BED_NO"  	    , "01"				); //야드적치Bed번호
			jrRtn = commUtils.addSndData(jrRtn,coilDao.getMsgL2("YDY5L002", jrParam));
			
			/**********************************************************
			* 크레인 스케줄 호출
			**********************************************************/
			/* 
			SELECT NVL(YD_FRM_YN,'N') AS YD_FRM_YN
			  FROM USRYDA.TB_YD_CARPOINT
			 WHERE 1=1 
			   AND YD_STK_COL_GP = :V_YD_STK_COL_GP 
			   AND YD_GP  = 'J'
			   AND DEL_YN = 'N'
            */
			JDTORecordSet jsYdFrmYn = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCarpointByFrmYn", logId, mthdNm, "형상 비형상 판단");

			String ydFrmYn = commUtils.nvl(jsYdFrmYn.getRecord(0).getFieldString("YD_FRM_YN"), "N"); //차량형상여부

			commUtils.printLog(logId, "차량형상 존재여부 : " + ydFrmYn, "SL");
			
			if ("N".equals(ydFrmYn)) {
				JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_EQP_ID", ydEqpId); //야드설비ID

				//적치된 대상 스케줄 호출
				EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this);
				
				for (int i = 0; i < jsWrkBook.size(); i++) {
					jrParam.setField("YD_WBOOK_ID", jsWrkBook.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
					jrCrnSchMsg = (JDTORecord)ejbConn.trx("procYDYDJ551", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}
				
				jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
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
	 * [A] 오퍼레이션명 : 차량초기화 작업 (구내운송 차량 초기화)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public void initCarSch(JDTORecord rcvMsg)throws DAOException  {
		String mthdNm = "구내운송 차량초기화 작업[CCoilCarMvSeEJB.initCarSch] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	
	    
	    try{
			commUtils.printLog(logId, mthdNm, "S+");
			
	    	//수신항목 변수 저장
			String sTrnEqpCd    	= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 			//운송장비코드
			String sTrnWrkFullvoidGp= commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분  F 영차  E 공차
			String sBackUpYN		= commUtils.nvl (rcvMsg.getFieldString("BACKUP_YN" ), "N"); 	//BACKUP 구분 (화면에서 강제초기화시 Y)
			String sWlocCd			= commUtils.nvl (rcvMsg.getFieldString("WLOC_CD"   ), ""); 		//개소코드
			String sModifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"  )); 			//수정자(Backup Only)
			String sMsg             = "";
			
			JDTORecordSet rsResult  = null;
			
			//파라메터 JDTORecord 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			if ("E".equals(sTrnWrkFullvoidGp)||"Y".equals(sBackUpYN)) {

				if ("Y".equals(sBackUpYN)) {

					//크레인 스케줄 편성 상태인지 체크
					jrParam.setField("TRN_EQP_CD", sTrnEqpCd); //운송장비코드
					/*
					SELECT E.* --하차대상
					  FROM TB_YD_CARSCH     A
					     , TB_YD_CARFTMVMTL B
					     , TB_YD_WRKBOOK    C 
					     , TB_YD_WRKBOOKMTL D
					     , TB_YD_CRNSCH     E
					 WHERE A.TRN_EQP_CD    = :V_TRN_EQP_CD
					   AND A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
					   AND A.DEL_YN = 'N'
					   AND B.STL_NO = D.STL_NO
					   AND D.DEL_YN = 'N'
					   AND D.YD_WBOOK_ID = C.YD_WBOOK_ID
					   AND C.DEL_YN = 'N'
					   AND C.YD_WBOOK_ID = E.YD_WBOOK_ID
					   AND E.DEL_YN = 'N'
					   AND E.YD_SCH_CD LIKE SUBSTR(E.YD_SCH_CD,1,2)||'PT0_LM%' --하차스케줄
					 UNION 
					SELECT E.* --상차대상
					  FROM TB_YD_CARSCH     A
					     , TB_YD_STOCK      B
					     , TB_YD_WRKBOOK    C 
					     , TB_YD_WRKBOOKMTL D
					     , TB_YD_CRNSCH     E
					 WHERE A.TRN_EQP_CD = :V_TRN_EQP_CD
					   AND A.DEL_YN = 'N'
					   AND B.STL_NO = D.STL_NO
					   AND D.DEL_YN = 'N'
					   AND D.YD_WBOOK_ID = C.YD_WBOOK_ID
					   AND C.DEL_YN = 'N'
					   AND C.YD_WBOOK_ID = E.YD_WBOOK_ID
					   AND E.DEL_YN = 'N'
					   AND E.YD_SCH_CD LIKE SUBSTR(E.YD_SCH_CD,1,2)||'PT0_UM%' --상차스케줄
					 */
					rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getCrnSchByTrnEqpCd", logId, mthdNm, "크레인 스케줄 편성 상태인지 체크");
					if (rsResult.size() > 0) {
						sMsg = " 이송대상재가 크레인 스케줄 편성상태 입니다!!! 크레인스케줄을 취소한 후에 초기화를 실행하세요. << " + mthdNm;
						commUtils.printLog(logId, sMsg, "S-");
						throw new Exception(sMsg);
					}
				
					//Layer(단) 저장품 상태를 'C:적치중'로 초기화  - ejb.transaction type="RequiresNew"
					jrParam.setField("TRN_EQP_CD"		    , sTrnEqpCd); //운송장비코드
					jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"); //적치단 상태 C:적치중
					sMsg = " Layer(단) 저장품 상태를 'C:적치중'로 초기화  << " + mthdNm;
					commUtils.printLog(logId, sMsg, "SL");
					/*
					UPDATE TB_YD_STKLYR
					   SET YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
					     , MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					WHERE STL_NO IN (
					                 SELECT	C.STL_NO--하차대상
					                   FROM TB_YD_CARSCH A
					                      , TB_YD_CARFTMVMTL B
					                      , TB_YD_STKLYR C
					                  WHERE A.YD_CAR_SCH_ID=B.YD_CAR_SCH_ID
					                    AND B.STL_NO = C.STL_NO
					                    AND A.TRN_EQP_CD = :V_TRN_EQP_CD
					                    AND A.DEL_YN='N' 
					                  UNION
					                 SELECT C.STL_NO--상차대상
					                   FROM TB_YD_CARSCH A
					                      , TB_YD_STOCK  B
					                      , TB_YD_STKLYR C
					                  WHERE A.FRTOMOVE_WORD_NO = B.CAR_FRTOMOVE_WORD_NO
					                    AND B.STL_NO = C.STL_NO
					                    AND A.TRN_EQP_CD = :V_TRN_EQP_CD
					                    AND A.DEL_YN='N' 
					                ) 
					 */
					commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updLayerStatByTrnEqpCd", logId, mthdNm, "저장품 초기화");
				
				
					//차량위치(적치열) 정리 작업
					//발지 차량정보 삭제 하기 - 적치단 정리 - ejb.transaction type="RequiresNew"
					jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"			); //적치 단 활성 상태 C:비활성화
					jrParam.setField("WLOC_CD"	 			, sWlocCd		); //개소코드
					jrParam.setField("TRN_EQP_CD"			, sTrnEqpCd	); //운송장비코드
					sMsg = " 발지 차량정보 삭제 하기 - 적치단 정리  << " + mthdNm;
					commUtils.printLog(logId, sMsg, "SL");
					/*
					UPDATE TB_YD_STKLYR
					   SET STL_NO = ''
					     , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
					     , YD_STK_LYR_MTL_STAT = 'E' 
					     , MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					 WHERE YD_STK_COL_GP  IN (
					                         --개소코드와 차량번호로 적치열을 찾는다. 
					                         SELECT YD_STK_COL_GP
					                           FROM (
					                                 SELECT /*+INDEX_DESC(A PK_YD_CARSCH)
					                                        (CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN YD_CARLD_STOP_LOC ELSE  YD_CARUD_STOP_LOC END) AS YD_STK_COL_GP
					                                      , TRN_EQP_CD
					                                   FROM USRYDA.TB_YD_CARSCH A
					                                  WHERE NVL(CARD_NO,TRN_EQP_CD) = :V_TRN_EQP_CD 
					                                    AND YD_CAR_SCH_ID >= TO_CHAR(SYSDATE-1,'YYYYMMDD')                             
					                                    AND ROWNUM<=1      
					                                ) A
					                          WHERE EXISTS(SELECT 1 FROM TB_YD_STKCOL B 
					                                        WHERE B.TRN_EQP_CD    = A.TRN_EQP_CD 
					                                          AND B.YD_STK_COL_GP = A.YD_STK_COL_GP)
					                         ) 
					 */
					commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updateLayerstat_02");
					
				}
				
				//차량위치 예정정보 삭제(상하차출발 위치) 정리 - ejb.transaction type="RequiresNew"
				jrParam.setField("CAR_CARD_NO", sTrnEqpCd); //운송장비코드
				sMsg = " 차량위치 예정정보 삭제(상하차출발 위치)정리  << " + mthdNm;
				commUtils.printLog(logId, sMsg, "SL");
				commDao.updateTx(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd");
				
				//차량위치정보 삭제(상하차개시/완료/도착 위치) 정리 - ejb.transaction type="RequiresNew"
				jrParam.setField("TRN_EQP_CD", sTrnEqpCd); //운송장비코드
				sMsg = " 차량위치정보 삭제(상하차개시/완료/도착 위치)정리  << " + mthdNm;
				commUtils.printLog(logId, sMsg, "SL");
				/*
				UPDATE TB_YD_STKCOL
				   SET MODIFIER      = :V_MODIFIER
				     , MOD_DDTT      = SYSDATE
				     , TRN_EQP_CD    = NULL
				     , YD_CAR_USE_GP = NULL
				 WHERE TRN_EQP_CD    = :V_TRN_EQP_CD
				 */
				commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStkcolTrnEqpCdToNull");

				//설비코드로 초기화 하는 경우(구내운송)			 			
				jrParam.setField("STAT"      , "C"      ); //적치형 활성상태
				jrParam.setField("TRN_EQP_CD", sTrnEqpCd); //운송장비코드
				
				sMsg = " 차량포인트통합관리  << " + mthdNm;
				commUtils.printLog(logId, sMsg, "SL");
				/*
				UPDATE TB_YD_CARPOINT
				   SET TRN_EQP_CD = NULL
				     , YD_STK_COL_ACT_STAT = DECODE(CARD_NO,NULL,(DECODE(YD_STK_COL_ACT_STAT,'N','N',:V_STAT)),YD_STK_COL_ACT_STAT)
				     , MOD_DDTT = SYSDATE
				     , MODIFIER = :V_MODIFIER
				 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				   AND MOD_DDTT <> SYSDATE 
				*/
				commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointtrneqpcdupdate");
				
				
				if ("Y".equals(sBackUpYN)) {
					//작업예약, 작업예약재료 삭제 
					jrParam.setField("TRN_EQP_CD"	, sTrnEqpCd); //운송장비코드
					jrParam.setField("YD_CAR_USE_GP", "L"      ); //구내운송
					/* 
			    	SELECT A.YD_WBOOK_ID
			    	     , A.YD_SCH_CD
			    	     , A.STL_NO
			    	     , A.YD_UP_COLL_SEQ
			    	     , A.YD_STK_COL_GP
			    	     , NVL(A.YD_STK_BED_NO,ROW_NO)  AS YD_STK_BED_NO
			    	     , A.YD_STK_LYR_NO
			    	  FROM (
			    	        SELECT B.YD_WBOOK_ID    AS YD_WBOOK_ID
			    	             , B.YD_SCH_CD
			    	             , A.STL_NO         AS STL_NO
			    	             , A.YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ
			    	             , A.YD_STK_COL_GP  AS YD_STK_COL_GP
			    	             , A.YD_STK_BED_NO  AS YD_STK_BED_NO
			    	             , A.YD_STK_LYR_NO  AS YD_STK_LYR_NO
			    	             , LPAD(ROWNUM,2,'0') AS ROW_NO
			    	          FROM TB_YD_WRKBOOKMTL A
			    	             ,(SELECT *
			    	                 FROM (SELECT YD_WBOOK_ID
			    	                            , YD_SCH_CD
			    	                         FROM TB_YD_WRKBOOK
			    	                        WHERE DEL_YN = 'N'
			    	                          AND YD_CAR_USE_GP = :V_YD_CAR_USE_GP
			    	                          AND TRN_EQP_CD    = :V_TRN_EQP_CD
			    	                        ORDER BY YD_WBOOK_ID DESC
			    	                       ) C
			    	                 WHERE ROWNUM <= 1
			    	                ) B
			    	          WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
			    	          ORDER BY YD_UP_COLL_SEQ DESC
			    	                 , YD_STK_COL_GP
			    	                 , YD_STK_BED_NO
			    	                 , YD_STK_LYR_NO
			    	       ) A
			    	 WHERE 1 = 1
			    	*/ 
			    	JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getWrkBookMtlbyCarUsrGpTrnEqpCd", logId, mthdNm, "운송장비코드로 작업예약 조회");

					if (jsWrkBook.size() > 0) {
						sMsg = " 작업예약이 존재함으로 작업예약과 작업예약재료 를 삭제(DEL_YN='Y')처리  << " + mthdNm;
						commUtils.printLog(logId, sMsg, "SL");
						
						//작업예약(TB_YM_WRKBOOK) 삭제처리 (DEL_YN='Y')
						jrParam.setField("TRN_EQP_CD", sTrnEqpCd); //운송장비코드
						jrParam.setField("DEL_YN"	 , "Y"); //삭제유무 Y:삭제
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnWrkBookByTrnEqpCd", logId, mthdNm, "작업예약 삭제(DEL_YN='Y')처리 ");

						//작업예약재료(TB_YM_WRKBOOKMTL) 삭제처리 (DEL_YN='Y')
						jrParam.setField("TRN_EQP_CD", sTrnEqpCd); //운송장비코드
						jrParam.setField("DEL_YN"	 , "Y"); //삭제유무 Y:삭제
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnWrkBookMtlByTrnEqpCd", logId, mthdNm, "작업예약재료 삭제(DEL_YN='Y')처리 ");
					}
				}
				
				//차량스케줄, 차량이송재료 삭제 
				
				//차량이송재료(TB_YD_CARFTMVMTL) 삭제처리 (DEL_YN='Y')
				jrParam.setField("TRN_EQP_CD", sTrnEqpCd); //운송장비코드
				jrParam.setField("DEL_YN"	 , "Y"); //삭제유무 Y:삭제
				/*
				UPDATE TB_YD_CARFTMVMTL
				   SET DEL_YN = :V_DEL_YN
				      ,MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				 WHERE YD_CAR_SCH_ID IN (
				 
				                         SELECT  DISTINCT YD_CAR_SCH_ID
				                           FROM  TB_YD_CARSCH
				                          WHERE  TRN_EQP_CD = :V_TRN_EQP_CD
				                            AND  DEL_YN = 'N'
				                        )                        
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarFtMvMtlByTrnEqpCd", logId, mthdNm, "차량이송재료 삭제(DEL_YN='Y')처리 ");
				
				//차량스케줄(TB_YD_CARSCH) 삭제처리 (DEL_YN='Y')
				jrParam.setField("TRN_EQP_CD", sTrnEqpCd); //운송장비코드
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
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSchByTrnEqpCd", logId, mthdNm, "차량스케줄 삭제(DEL_YN='Y')처리 ");
			}

			commUtils.printLog(logId, mthdNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량출발취소(TSYDJ014) 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvTSYDJ014(JDTORecord rcvMsg)throws DAOException  {
		String mthdNm = "소재차량출발취소[CCoilCarMvSeEJB.rcvTSYDJ014] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
	    try {

	    	commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
	    	String msgId             = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sTrnEqpCd      	 = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"         )); //운송장비코드
			String sTrnWrkFullvoidGp = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분[상하차구분 E:공차상태(상차작업),F:영차(하차작업)]
			
	    	String sModifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			String sMsg      = "";
			String sCurrDate = commUtils.getDateTime14();
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRN_EQP_CD"	      , sTrnEqpCd        );
			jrParam.setField("TRN_WRK_FULLVOID_GP", sTrnWrkFullvoidGp);
			
			/*******************************
			 *  0. 기존 출발정보 유무 확인
			 *******************************/
			/*
			SELECT YD_CAR_SCH_ID
			     , SPOS_WLOC_CD
			     , YD_CARLD_WRK_BOOK_ID
			     , YD_CAR_PROG_STAT
			  FROM TB_YD_CARSCH
			 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			   AND DEL_YN     = 'N'
			   AND YD_CAR_PROG_STAT IN ('1','2') --1:상차출발,2:상차도착 
            */
			
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getListSposYNchk_E", logId, mthdNm, "기존 출발정보 유무 확인");
			
			if (jsCarSch.size() > 0) {
				
				String sSposWlocCd     = commUtils.trim(jsCarSch.getRecord(0).getFieldString("SPOS_WLOC_CD"));
				String sCarldWrkBookId = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CARLD_WRK_BOOK_ID"));
				String ydCarSchId      = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID"   ));
				String ydCarProgStat   = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT"));
				
				commUtils.printLog(logId,"=상차개소           = "+ sSposWlocCd    , "SL");
				commUtils.printLog(logId,"=취소할 작업예약 ID = "+ sCarldWrkBookId, "SL");
				commUtils.printLog(logId,"=취소할 차량작업 ID = "+ ydCarSchId     , "SL");
				commUtils.printLog(logId,"=취소할 차량상태    = "+ ydCarProgStat  , "SL");
				          
				/***********************************
				 *  1. 신규야드스케쥴 존재여부 체크
				 ***********************************/
				/* 
				SELECT *
				  FROM TB_YD_CRNSCH
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND DEL_YN='N'
				 ORDER BY YD_CRN_SCH_ID
				*/
				jrParam.setField("YD_WBOOK_ID"	, sCarldWrkBookId);
				JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCrnschByWrkId", logId, mthdNm, "크레인스케쥴 체크");
				if (rsResult.size() > 0) {
					sMsg = "신규야드스케쥴 존재여부 체크 결과 YD_CRN_SCH_ID : " 
					      + commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID")) 
					      + " 검색되어 차량출발취소 처리가 불가합니다.";
					commUtils.printLog(logId, sMsg, "S-");
							
					return jrRtn;				
				}
					
				/*******************************
				 *  2. 구내운송 차량 초기화
				 *   - 작업예약 삭제
				 *   - 야드맵 초기화
				 *   - 차량스케줄 삭제
				 *   - 카포인트 삭제
				 *******************************/
				commUtils.printLog(logId, "차량 초기화", "SL");
				
				jrParam.setField("YD_WBOOK_ID"  , sCarldWrkBookId);
				/*
				SELECT YD_TO_LOC_GUIDE AS YD_STK_COL_GP 
				     , YD_WBOOK_ID
				  FROM TB_YD_WRKBOOK
				 WHERE TRN_EQP_CD = (SELECT TRN_EQP_CD 
				                       FROM TB_YD_WRKBOOK
				                      WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				                        AND DEL_YN = 'N')
				   AND DEL_YN = 'N'  
				 */
				JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getWrkbookForTsCncl", logId, mthdNm, "작업예약 조회");

				String ydStkColGp = "";
				
				for (int i = 0; i < jsWrkBook.size(); ++i) {
					
					ydStkColGp = jsWrkBook.getRecord(0).getFieldString("YD_STK_COL_GP");
					
					jrParam.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(i).getFieldString("YD_WBOOK_ID"));
					jrParam.setField("DEL_YN"       , "Y"            );
					jrParam.setField("TRN_EQP_CD"   , sTrnEqpCd      );
					jrParam.setField("YD_CAR_SCH_ID", ydCarSchId     );
					/*
					UPDATE TB_YD_WRKBOOKMTL  
					   SET MOD_DDTT = SYSDATE
					     , MODIFIER = :V_MODIFIER
					     , DEL_YN   = :V_DEL_YN
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID	
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delWrkbookMtl", logId, mthdNm, "작업예약 재료 삭제");
					
					/*
					UPDATE TB_YD_WRKBOOK
					   SET MOD_DDTT = SYSDATE
					     , MODIFIER = :V_MODIFIER
					     , DEL_YN   = :V_DEL_YN
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.delYdWrkbook", logId, mthdNm, "작업예약 삭제");
				}
				
				/*
				UPDATE TB_YD_STKCOL
				   SET MOD_DDTT = SYSDATE 
				     , MODIFIER = :V_MODIFIER
				     , YD_STK_COL_ACT_STAT = 'C'
				     , YD_CAR_USE_GP       = ''
				     , TRN_EQP_CD          = ''
				 WHERE TRN_EQP_CD = :V_TRN_EQP_CD 
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.delYDStkBookLoc", logId, mthdNm, "예약위치정보삭제");
				
				/*
				UPDATE TB_YD_CARSCH
				   SET DEL_YN = 'Y'
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.delCarschID", logId, mthdNm, "차량스케줄정보삭제");
				
				/* 
				UPDATE TB_YD_CARPOINT
				   SET TRN_EQP_CD = NULL
				     , YD_STK_COL_ACT_STAT = DECODE(CARD_NO,NULL,(DECODE(YD_STK_COL_ACT_STAT,'N','N',:V_STAT)),YD_STK_COL_ACT_STAT)
				     , MOD_DDTT = SYSDATE
				     , MODIFIER = :V_MODIFIER
				 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				   AND MOD_DDTT <> SYSDATE 
				   AND DEL_YN = 'N'
				*/
				jrParam.setField("STAT"  		, "C"); 
		    	
		    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointtrneqpcdupdate", logId, mthdNm, "TB_YD_CARPOINT 수정");
				
				/********************************************************
				 * 3. 위치대기차량 입동지시 요구
				 ********************************************************/
		    	jrParam.setField("YD_STK_COL_GP", ydStkColGp);
				/*
				SELECT A.*
				     ,(SELECT YD_CARPNT_CD FROM TB_YD_CARPOINT         
				        WHERE YD_GP = 'J'
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
				           AND SPOS_WLOC_CD IN ('DJY21', 'DJY22', 'DJY1E') 
				           AND YD_CARLD_STOP_LOC = :V_YD_STK_COL_GP
				         UNION ALL  
				        SELECT A.*
				             , 'F' AS TRN_WRK_FULLVOID_GP
				             , ARR_WLOC_CD AS WLOC_CD
				          FROM TB_YD_CARSCH A
				         WHERE DEL_YN = 'N'
				           AND YD_CAR_PROG_STAT = 'A' 
				           AND ARR_WLOC_CD  IN ('DJY21', 'DJY22', 'DJY1E')
				           AND YD_CARUD_STOP_LOC = :V_YD_STK_COL_GP
				       ) A
				 ORDER BY YD_BAYIN_WO_SEQ  
				        , YD_CAR_SCH_ID
				 */
		    	JDTORecordSet jsCarSchNext = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getTsNextTrnEqp", logId, mthdNm, "차량스케줄 조회");
		    	if (jsCarSchNext.size() > 0) {
		    		
		     		String sTrnEqpCdNext         = jsCarSchNext.getRecord(0).getFieldString("TRN_EQP_CD");
		    		String sTrnWrkFullvoidGpNext = jsCarSchNext.getRecord(0).getFieldString("TRN_WRK_FULLVOID_GP");
		    		String sWlocCdNext           = jsCarSchNext.getRecord(0).getFieldString("WLOC_CD");
		    		
		    		jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("TRN_EQP_CD"			, sTrnEqpCdNext        );	
					jrParam.setField("WLOC_CD"				, sWlocCdNext          );	
					jrParam.setField("TRN_WRK_FULLVOID_GP"	, sTrnWrkFullvoidGpNext);	
					jrParam.setField("PNT_DMD_DT"			, sCurrDate  );			
					
					JDTORecord jrYdMsg = this.rcvTSYDJ002(jrParam);
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
		    	}
			
			} else {
				
				sMsg="기존 출발정보 없음[운송장비코드 : " + sTrnEqpCd + " ]" ;
				commUtils.printLog(logId, sMsg, "SL");
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
	 * 오퍼레이션명 : 차량포인트 통합관리 (기존형태 유지 yd와 동일)
 	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */     	
	public boolean YdCarPointinforeg(String chk 
			                       , String sCAR_NO
			                       , String sTRN_EQP_CD
			                       , String sYD_STK_COL_GP
			                       , String sARR_WLOC_CD 
							       , String sARR_YD_PNT_CD
							       , String sSTAT
							       , String logId
							       , String mthdNms
							       , String sModifier)throws DAOException {
		String mthdNm =  "[CCoilCarMvSeEJB.YdCarPointinforeg] < " + mthdNms;
		
		boolean isSuccess = false;
		String sMsg       = "";
		
		try{

			commUtils.printLog(logId, mthdNm, "S+");
			sMsg = "▣▣▣▣차량포인트 통합관리(START):"+chk+","+sCAR_NO+","+sTRN_EQP_CD+","+sYD_STK_COL_GP+","+sARR_WLOC_CD+","+sARR_YD_PNT_CD+","+sSTAT+"▣▣▣▣▣" ;
	  		
    		JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
	    	
			commUtils.printLog(logId, sMsg, "SL");
			if ("1".equals(chk)) {
				//설비코드로 초기화 하는 경우(구내운송)			 			
				/* 
				UPDATE TB_YD_CARPOINT
				   SET TRN_EQP_CD = NULL
				     , YD_STK_COL_ACT_STAT = DECODE(CARD_NO,NULL,(DECODE(YD_STK_COL_ACT_STAT,'N','N',:V_STAT)),YD_STK_COL_ACT_STAT)
				     , MOD_DDTT = SYSDATE
				     , MODIFIER = :V_MODIFIER
				 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				   AND MOD_DDTT <> SYSDATE 
				   AND DEL_YN = 'N'
				*/
				
				jrParam.setField("STAT"  		, sSTAT); 
		    	jrParam.setField("TRN_EQP_CD"	, sTRN_EQP_CD);
		    	
		    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointtrneqpcdupdate", logId, mthdNm, "TB_YD_CARPOINT 수정");
				
				
			}else if ("2".equals(chk)) {
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
				jrParam.setField("STAT"  			, sSTAT); 
		    	jrParam.setField("YD_STK_COL_GP"	, sYD_STK_COL_GP);
		    	
		    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointstackcolgpupdateCT", logId, mthdNm, "TB_YD_CARPOINT 수정");
				
			}else if ("3".equals(chk)) {
				//저장위치로 차량 포인트 예약 하는 경우(구내운송)
				/* 
				UPDATE TB_YD_CARPOINT
				   SET YD_STK_COL_ACT_STAT = :V_STAT
				     , TRN_EQP_CD = :V_TRN_EQP_CD
				     , MOD_DDTT = SYSDATE
				     , MODIFIER = :V_MODIFIER
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP   
				   AND DEL_YN = 'N'
				*/ 
				jrParam.setField("STAT"  			, sSTAT); 
		    	jrParam.setField("TRN_EQP_CD"		, sTRN_EQP_CD);
		    	jrParam.setField("YD_STK_COL_GP"	, sYD_STK_COL_GP);
		    	
		    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointtrneqpcdupdateC", logId, mthdNm, "TB_YD_CARPOINT 수정");

			} else if ("4".equals(chk)) {
				//개소코드,포인트로 차량 포인트 예약 하는 경우(구내운송)
				/*
				UPDATE TB_YD_CARPOINT
				   SET YD_STK_COL_ACT_STAT = :V_STAT
				     , TRN_EQP_CD = :V_TRN_EQP_CD
				     , MOD_DDTT   = SYSDATE
				     , MODIFIER   = :V_MODIFIER
				 WHERE WLOC_CD   = :V_WLOC_CD
				   AND YD_PNT_CD = :V_YD_PNT_CD    
				   AND DEL_YN    = 'N'
				*/   
				jrParam.setField("STAT"  			, sSTAT); 
		    	jrParam.setField("TRN_EQP_CD"		, sTRN_EQP_CD);
		    	jrParam.setField("WLOC_CD"			, sARR_WLOC_CD);
		    	jrParam.setField("YD_PNT_CD"		, sARR_YD_PNT_CD);
		    	
		    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointWlocpntupdate", logId, mthdNm, "TB_YD_CARPOINT 수정");

			} else if ("A".equals(chk)) {
				//설비코드로 초기화 하는 경우(출하)			 		
				/* 
				UPDATE TB_YD_CARPOINT
				   SET CARD_NO = NULL
				     , CAR_NO  = NULL
				     , YD_STK_COL_ACT_STAT = DECODE(YD_STK_COL_ACT_STAT, 'N', 'N',:V_STAT)
				     , MOD_DDTT = SYSDATE
				     , MODIFIER = :V_MODIFIER
				 WHERE CARD_NO = :V_TRN_EQP_CD   
				*/   
				jrParam.setField("STAT"  			, sSTAT); 
		    	jrParam.setField("TRN_EQP_CD"		, sTRN_EQP_CD);
		    	
		    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointtrneqpcdupdatePT", logId, mthdNm, "TB_YD_CARPOINT 수정");
				
			} else if ("B".equals(chk)) {
				//저장위치로 초기화 하는 경우(출하)
				/* 
				UPDATE TB_YD_CARPOINT
				   SET CARD_NO = NULL
				     , CAR_NO  = NULL
				     , YD_STK_COL_ACT_STAT=DECODE(TRN_EQP_CD,NULL,:V_STAT,YD_STK_COL_ACT_STAT)
				     , MOD_DDTT = SYSDATE
				     , MODIFIER = :V_MODIFIER
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP    
				   AND DEL_YN = 'N'
				*/ 
				jrParam.setField("STAT"  			, sSTAT); 
				jrParam.setField("YD_STK_COL_GP"	, sYD_STK_COL_GP);
		    	
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointstackcolgpupdateC", logId, mthdNm, "TB_YD_CARPOINT 수정");
				
			} else if ("C".equals(chk)) {
				//저장위치로 차량 포인트 예약 하는 경우(출하)
				/*
				UPDATE TB_YD_CARPOINT
				   SET YD_STK_COL_ACT_STAT=:V_STAT
				     , CAR_NO   = :V_CAR_NO
				     , CARD_NO  = :V_TRN_EQP_CD
				     , MOD_DDTT = SYSDATE
				     , MODIFIER = :V_MODIFIER
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND DEL_YN = 'N'
	    		*/ 
				jrParam.setField("YD_STK_COL_ACT_STAT", sSTAT); 
				jrParam.setField("CAR_NO"			  , sCAR_NO);
				jrParam.setField("TRN_EQP_CD"		  , sTRN_EQP_CD);
				jrParam.setField("YD_STK_COL_GP"	  , sYD_STK_COL_GP);
		    	
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointtrneqpcdupdateC2", logId, mthdNm, "TB_YD_CARPOINT 수정");
				
			} else if ("D".equals(chk)) {
				//개소코드,포인트로 차량 포인트 예약 하는 경우(출하)
				/*
				UPDATE TB_YD_CARPOINT
				   SET YD_STK_COL_ACT_STAT = :V_STAT
				     , CAR_NO   = :V_CAR_NO
				     , CARD_NO  = :V_TRN_EQP_CD
				     , MOD_DDTT = SYSDATE
				     , MODIFIER = :V_MODIFIER
				 WHERE WLOC_CD   = :V_WLOC_CD
				   AND YD_PNT_CD = :V_YD_PNT_CD    
				   AND DEL_YN    = 'N'
				 */  
				jrParam.setField("STAT"  			, sSTAT); 
				jrParam.setField("TRN_EQP_CD"		, sTRN_EQP_CD);
		    	
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointWlocpntupdatePT", logId, mthdNm, "TB_YD_CARPOINT 수정");

			}
	 
			isSuccess = true;
			commUtils.printLog(logId, mthdNm, "S-");	
	    } catch (DAOException daoe) {
	        throw daoe;
	    } catch (Exception e) {
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}		
	
	
	/**
	 * 출하차량 출발처리 - 맵비활성화 (DMYDR040)
	 * 강정선 19.11.14
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws JDTOException
	 */
	public JDTORecord procOutCarLevWr(JDTORecord rcvMsg)throws JDTOException  {
	    String mthdNm = "코일출하차량 출발처리 - 맵비활성화[CCoilCarMvSeEJB.procOutCarLevWr] < "+ rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();

		try{
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
		    
		    int intRtnVal		= 0;

		    String sCarNo			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));          // 차량번호
		    String sCardNo			= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));         // 카드번호
		    String sSposWlocCd		= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"));    // 발지개소코드
		    String sSposYdPntCd		= commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"));  // 발지포인트코드
		    String sTransOrdDate 	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));  	// 운송지시일자
		    String sTransOrdSeqno	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); // 운송지시순번
		    
		    String sMsgId           = commUtils.trim(rcvMsg.getFieldString("MSG_ID")); //DMYDR030 수신여부 판단
		    
			String sModifier 	    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
			//PIDEV_S :병행가동용:PI_YD
			String sPI_YD     = commUtils.nvl(rcvMsg.getFieldString("PI_YD"),"*");
			String sPI_YD1     = commUtils.nvl(rcvMsg.getFieldString("PI_YD1"),"*");
			
			JDTORecord jrRtn   = JDTORecordFactory.getInstance().create();
			
			String sMsg	= "";
		    
			/****************************************************
			 * 1. 필수 항목 체크
			 ****************************************************/
		    if ("".equals(sCarNo)) {
	    		sMsg = "차량번호가 없습니다.";
	    		commUtils.printLog(logId, sMsg, "SL");
	    		jrRtn.setField("RTN_CD"	, "0");	
	    		jrRtn.setField("RTN_MSG", sMsg);
	    		commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
		    }
//PIDEV		    
		    /*
		    if ("".equals(sCardNo)) {
	    		sMsg = "카드번호가 없습니다.";
	    		commUtils.printLog(logId, sMsg, "SL");
	    		jrRtn.setField("RTN_CD"	, "0");	
	    		jrRtn.setField("RTN_MSG", sMsg);
	    		commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
		    }
		    */
		    if ("".equals(sSposWlocCd)) {
	    		sMsg = "발지개소코드가 없습니다.";
	    		commUtils.printLog(logId, sMsg, "SL");
		    }
		    if ("".equals(sSposYdPntCd)) {
	    		sMsg = "발지포인트코드가 없습니다.";
	    		commUtils.printLog(logId, sMsg, "SL");
		    }
		    if ("".equals(sTransOrdDate)) {
	    		sMsg = "운송지시일자가 없습니다.";
	    		commUtils.printLog(logId, sMsg, "SL");
	    		jrRtn.setField("RTN_CD"	, "0");	
	    		jrRtn.setField("RTN_MSG", sMsg);
	    		commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
		    }
		    if ("".equals(sTransOrdSeqno)) {
	    		sMsg = "운송지시순번이 없습니다.";
	    		commUtils.printLog(logId, sMsg, "SL");
	    		jrRtn.setField("RTN_CD"	, "0");	
	    		jrRtn.setField("RTN_MSG", sMsg);
	    		commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
		    }

		    JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		    jrParam.setField("CAR_NO"         		, sCarNo		);
		    jrParam.setField("CARD_NO"        		, sCardNo		);
		    jrParam.setField("SPOS_WLOC_CD"   		, sSposWlocCd	);
		    jrParam.setField("SPOS_YD_PNT_CD" 		, sSposYdPntCd	);
		    jrParam.setField("TRANS_ORD_DATE" 		, sTransOrdDate );
		    jrParam.setField("TRANS_ORD_SEQNO"		, sTransOrdSeqno);
		    
		    jrParam.setField("WLOC_CD"		        , sSposWlocCd);
		    jrParam.setField("YD_PNT_CD"	        , sSposYdPntCd);
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
	    	JDTORecordSet jsStkCol = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStkcolWLocCdandPntCd", logId, mthdNm, "적치열 조회");
	    	if (jsStkCol.size() <= 0) {
				sMsg = "발지개소["+sSposWlocCd+"] 및 포인트 코드["+sSposYdPntCd+"] 적치열 조회 실패!";
	    		commUtils.printLog(logId, sMsg, "SL");
	    		jrRtn.setField("RTN_CD"	, "0");	
	    		jrRtn.setField("RTN_MSG", sMsg);
	    		commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
	    	}

	    	// 발지위치정보로 출발위치 Clear
	    	// 열정보 Clear 업데이트 후 리턴값이 1이상이면 베드 단정보도 Clear
	    	// 업데이트값이 없다면 그냥 종료
	    	// 발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)

	    	//열구분을 조회(도착지)
		    String sChkAnotherCar	= "N";
	    	String ydCarldLevLoc	= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_GP")); 
	    	String ydCarPntCd		= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_CARPNT_CD" )); 
	    	String sCarNoChk		= commUtils.trim(jsStkCol.getRecord(0).getFieldString("CAR_NO"       ));
	    	String ydStkColActStat	= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));
	    	String ydLocGp          = commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_LOC_GP")); //야드구분
	    	
	    	//다른 차량이 존재 하는 경우
	    	if (!sCarNoChk.equals(sCarNo)) {
	    		// LOG만 찍고 pass 처리

	    		sChkAnotherCar = "Y";
	    		sMsg = "해당 위치에 다른 차량이 존재 합니다. 포인트 차량["+sCarNoChk + "] 취소대상 차량["+ sCarNo +"]";
				commUtils.printLog(logId, sMsg, "SL");
	    	}

	    	//동일 차량이 존재 하는 경우 차량위치정보를 초기화 한다.
	    	if ("N".equals(sChkAnotherCar)) {

	    		if ("N".equals(ydStkColActStat)) {
	    			ydStkColActStat = "N";
	    		} else {
	    			ydStkColActStat = "C";
	    		}
	    		
				/****************************************************
				 * 2. TO위치 차량 존재시 삭제
				 ****************************************************/
	    		commUtils.printLog(logId, "2. TO위치 차량 존재시 삭제", "SL");
			    jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			    jrParam.setField("YD_STK_COL_GP"		, ydCarldLevLoc);
			    jrParam.setField("YD_STK_COL_ACT_STAT"	, ydStkColActStat);
			    jrParam.setField("YD_CAR_USE_GP"		, "");
			    jrParam.setField("TRN_EQP_CD"			, "");
			    jrParam.setField("CAR_NO"				, "");
			    jrParam.setField("CARD_NO"				, "");
	    		/*
	    		UPDATE TB_YD_STKCOL
	    		   SET YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
	    		     , YD_CAR_USE_GP       = :V_YD_CAR_USE_GP
	    		     , TRN_EQP_CD          = :V_TRN_EQP_CD
	    		     , CAR_NO              = :V_CAR_NO
	    		     , CARD_NO             = :V_CARD_NO
	    		     , MOD_DDTT            = SYSDATE
	    		     , MODIFIER            = :V_MODIFIER
	    		 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
	    		*/
		    	intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStkcol05", logId, mthdNm, "적치열 수정");
				if (intRtnVal < 1) {
					
					sMsg = "적치열 수정 실패! YD_STK_COL_GP["+ ydCarldLevLoc +"] YD_STK_COL_ACT_STAT["+ ydStkColActStat +"]";
					commUtils.printLog(logId, sMsg, "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sMsg);
					commUtils.printLog(logId, mthdNm, "S-");
					return jrRtn;
				}

				/****************************************************
				 * 3. 차량POINT 초기화
				 ****************************************************/
				commUtils.printLog(logId, "3. 차량POINT 초기화", "SL");
			    jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			    jrParam.setField("YD_STK_COL_GP", ydCarldLevLoc);
			    jrParam.setField("STAT"			, ydStkColActStat);

			    /*
			    UPDATE TB_YD_CARPOINT
			       SET CARD_NO       = NULL
			         , CAR_NO        = NULL
			         , YD_STK_COL_ACT_STAT = NVL2(TRN_EQP_CD, YD_STK_COL_ACT_STAT, :V_STAT)
			         , MODIFIER      = :V_MODIFIER
			         , MOD_DDTT      = SYSDATE
			     WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			    */
		    	intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updCarPointStkColGpC", logId, mthdNm, "차량POINT 초기화");
				if (intRtnVal < 1) {
					
					sMsg = "차량POINT 초기화 실패! YD_STK_COL_GP["+ ydCarldLevLoc +"] YD_STK_COL_ACT_STAT["+ ydStkColActStat +"]";
					commUtils.printLog(logId, sMsg, "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sMsg);
					commUtils.printLog(logId, mthdNm, "S-");
					return jrRtn;
				}
		    	
				/****************************************************
				 * 4. 차량적치BED 비활성화 등록
				 ****************************************************/
				commUtils.printLog(logId, "4. 차량적치BED 비활성화 등록", "SL");
			    jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			    jrParam.setField("YD_STK_COL_GP"		, ydCarldLevLoc);
			    jrParam.setField("YD_STK_BED_ACT_STAT"	, "C");

				/*
				UPDATE TB_YD_STKBED
				   SET MODIFIER            = :V_MODIFIER
				     , MOD_DDTT            = SYSDATE
				     , YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
				*/
		    	intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStkbedYdStkColGp", logId, mthdNm, "차량적치BED 비활성화");
				if (intRtnVal < 1) {
					
					sMsg = "차량 적치베드 정보 비활성화중 Error!!";
					commUtils.printLog(logId, sMsg, "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sMsg);
					commUtils.printLog(logId, mthdNm, "S-");
					return jrRtn;
				}

				/****************************************************
				 * 5. 차량적치단 비활성화 등록
				 ****************************************************/
				commUtils.printLog(logId, "5. 차량적치단 비활성화 등록", "SL");
			    jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			    jrParam.setField("YD_STK_COL_GP"		, ydCarldLevLoc);
			    jrParam.setField("YD_STK_LYR_ACT_STAT"	, "C");
			    jrParam.setField("YD_STK_LYR_MTL_STAT"	, "E");
			    jrParam.setField("STL_NO"				, "");
			    /*
			    UPDATE TB_YD_STKLYR   
			       SET MODIFIER            = :V_MODIFIER
			         , MOD_DDTT            = SYSDATE
			         , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
			         , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
			         , STL_NO              = :V_STL_NO
			     WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			    */
		    	intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStklyrYdStkColGp", logId, mthdNm, "차량적치단 비활성화");
				if (intRtnVal < 1) {
					
					sMsg = "차량 적치단 정보 비활성화중 Error!!";
					commUtils.printLog(logId, sMsg, "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sMsg);
					commUtils.printLog(logId, mthdNm, "S-");
					return jrRtn;
				}

				/****************************************************
				 * 6. 차량 출발시 저장위치 제원 L2 송신
				 ****************************************************/
			    jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			    jrParam.setField("YD_INFO_SYNC_CD"	  , "4"           );//1:동,2:SPAN,3:열,4:BED
			    jrParam.setField("YD_STK_COL_GP"	  , ydCarldLevLoc );
			    jrParam.setField("YD_STK_BED_NO"	  , "01"          );
			    jrParam.setField("YD_CAR_ARRSTRT_STAT", "S"           );//출발
			    jrParam.setField("YD_EQP_WRK_STAT"    , "L"           );
			    jrParam.setField("YD_CAR_USE_GP"      , "G"           );//출하
			    jrParam.setField("CAR_NO"             , sCarNo        );
			    jrParam.setField("CARD_NO"        	  , sCardNo		);
			    
				jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L001_CarInfo", jrParam));
				
				sMsg = "6. 저장위치 제원정보(YDY5L001) 송신 QUEUE 저장 완료.";
				commUtils.printLog(logId, sMsg, "SL");
	    	}
	    	
			/****************************************************
			 * 7. 차량스케쥴 조회
			 ****************************************************/
	    	commUtils.printLog(logId, "7. 차량스케쥴 조회", "SL");
			String sTmpCarNo	= "";
			if (sCarNo.startsWith("ET")) {
				sTmpCarNo = "ET";
			} else {
				sTmpCarNo = sCarNo;
			}
			
		    jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		    jrParam.setField("CAR_NO"			, sTmpCarNo);
		    jrParam.setField("CARD_NO"			, sCardNo);
		    jrParam.setField("TRANS_ORD_DATE"	, sTransOrdDate);
		    jrParam.setField("TRANS_ORD_SEQNO"	, sTransOrdSeqno);
		    
		    /*
		    SELECT *
		      FROM TB_YD_CARSCH
		     WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
		       AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
		       AND CAR_NO          = :V_CAR_NO
		       AND CARD_NO         = :V_CARD_NO
		       AND DEL_YN          = 'N'
		    */
//PIDEV_S :병행가동용:PI_YD
		    jrParam.setField("PI_YD",    	sPI_YD);			
	    	JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarschByTransDTSeq_PIDEV", logId, mthdNm, "차량스케쥴조회");
	    	if (jsCarSch.size() < 1) {
	    		
	    		// 차량스케쥴 없으면 정상으로 Return 처리함
	    		// 기존 CarMvHdSeEJB.procOutCarLevWr Line:13767
				sMsg = "차량스케쥴 조회 실패!! TRANS_ORD_DATE["+ sTransOrdDate +"] TRANS_ORD_SEQNO["+ sTransOrdSeqno +"] CAR_NO["+ sTmpCarNo +"] CAR_NO["+ sCarNo +"] ## 정상코드 Return";
	    		commUtils.printLog(logId, sMsg, "SL");
	    		jrRtn.setField("RTN_CD"	, "1");	
	    		jrRtn.setField("RTN_MSG", sMsg);
	    		commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
	    	}
	    	
		    String ydCarSchId   = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
		    String sCmbnCarldYn = commUtils.trim(jsCarSch.getRecord(0).getFieldString("CMBN_CARLD_YN"));
		    
			/****************************************************
			 * 8. 차량스케쥴 종료
			 ****************************************************/
		    commUtils.printLog(logId, "8. 차량스케쥴 종료", "SL");
		    jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		    jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
		    jrParam.setField("DEL_YN"		, "Y");
		    /*
			UPDATE TB_YD_CARSCH
			   SET DEL_YN        = :V_DEL_YN
			     , MODIFIER      = :V_MODIFIER
			     , MOD_DDTT      = SYSDATE
			 WHERE YD_CAR_SCH_ID = :YD_CAR_SCH_ID
		    */
	    	intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarSchDelYn", logId, mthdNm, "차량스케쥴 종료처리");
			if (intRtnVal < 1) {
				
				sMsg = "차량 스케쥴 업데이트 실패!! YD_CAR_SCH_ID["+ ydCarSchId +"]";
				commUtils.printLog(logId, sMsg, "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
			}

			/****************************************************
			 * 9. 차량작업재료 조회
			 ****************************************************/
			commUtils.printLog(logId, "9. 차량스케쥴 종료", "SL");
		    jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		    jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
		    /*
		    SELECT *
		      FROM (
		        SELECT YD_CAR_SCH_ID
		             , STL_NO
		             , YD_STK_BED_NO
		             , YD_STK_LYR_NO
		             , STL_PROG_CD
		             , YD_MTL_ITEM
		             , YD_ROUTE_GP
		             , SubSTR(YD_MTL_ITEM, 1, 1) AS YD_MTL_GP
		             , YD_CAR_UPP_LOC_CD
		             , NVL((SELECT MAX('Y')
		                      FROM TB_YD_RETHTHIST B
		                     WHERE B.STL_NO = A.STL_NO
		                       AND B.YD_RETHT_STAT_CD = '1'), 'N') AS RETHTHIST_CD
		         FROM TB_YD_CARFTMVMTL A
		        WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
		        ) A
		     WHERE ((RETHTHIST_CD = 'Y' AND YD_CAR_UPP_LOC_CD = '*') OR (RETHTHIST_CD = 'N' AND 1 = 1))
		     ORDER BY YD_STK_BED_NO, YD_STK_LYR_NO
			*/
	    	JDTORecordSet jsCarFtmvMtl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarftmvmtlBySchId", logId, mthdNm, "차량작업재료조회");
	    	if (jsCarFtmvMtl.size() < 1) {
	    		
	    		// 재료가 없어도 로그만 찍고 PASS 함
	    		// 기존 CarMvHdSeEJB.procOutCarLevWr Line:13803
				sMsg = "차량재료정보가 조회 실패!! YD_CAR_SCH_ID["+ ydCarSchId +"]";
	    		commUtils.printLog(logId, sMsg, "SL");
	    	} 
	    	
    		String sStlNo = "";
			/****************************************************
			 * 10. 차량 이송재료 종료
			 ****************************************************/
    		commUtils.printLog(logId, "10. 차량 이송재료 종료", "SL");
    		for (int i = 0; i < jsCarFtmvMtl.size(); i++) {
    			sStlNo = jsCarFtmvMtl.getRecord(i).getFieldString("STL_NO");
    			
    		    jrParam = commUtils.getParam(logId, mthdNm, sModifier);
    		    jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
    		    jrParam.setField("STL_NO"		, sStlNo);
    		    jrParam.setField("DEL_YN"		, "Y");
    		    
    		    /*
				UPDATE TB_YD_CARFTMVMTL
				   SET DEL_YN        = :V_DEL_YN
				     , MODIFIER      = :V_MODIFIER
				     , MOD_DDTT      = SYSDATE
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				   AND STL_NO        = :V_STL_NO
    		    */
    	    	intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarftmvmtlDelYn", logId, mthdNm, "차량 이송재료재료 종료처리");
    			if (intRtnVal < 1) {
    				// UPDATE 실패해도 PASS
    				sMsg = "차량 이송재료 업데이트 실패!! YD_CAR_SCH_ID["+ ydCarSchId +"] STL_NO["+ sStlNo +"]";
    				commUtils.printLog(logId, sMsg, "SL");
    				jrRtn.setField("RTN_CD"	, "0");
    				jrRtn.setField("RTN_MSG", sMsg);
    				commUtils.printLog(logId, mthdNm, "S-");
    				return jrRtn;
    			}
    		} //end for

    		String sApp838 = coilDao.ApplyYn(logId, mthdNm, "APP838", "J", "*"); //입동지시 요구 전문송신
    		 
    		if ("H".equals(ydLocGp)) {
    			jrParam.setField("YD_STK_COL_GP", ydCarldLevLoc);
        		/*
				SELECT A.*
				     ,(SELECT YD_CARPNT_CD FROM TB_YD_CARPOINT         
				        WHERE YD_GP = 'J'
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
				           AND SPOS_WLOC_CD IN ('DJY21', 'DJY22', 'DJY1E') 
				           AND YD_CARLD_STOP_LOC = :V_YD_STK_COL_GP
				         UNION ALL  
				        SELECT A.*
				             , 'F' AS TRN_WRK_FULLVOID_GP
				             , ARR_WLOC_CD AS WLOC_CD
				          FROM TB_YD_CARSCH A
				         WHERE DEL_YN = 'N'
				           AND YD_CAR_PROG_STAT = 'A' 
				           AND ARR_WLOC_CD  IN ('DJY21', 'DJY22', 'DJY1E')
				           AND YD_CARUD_STOP_LOC = :V_YD_STK_COL_GP
				       ) A
				 ORDER BY YD_BAYIN_WO_SEQ  
				        , YD_CAR_SCH_ID
    			 */
    	    	JDTORecordSet jsCarSchNext = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getTsNextTrnEqp", logId, mthdNm, "차량스케줄 조회");
    	    	if (jsCarSchNext.size() > 0) {
    	    		
    	    		String ydCarUseGp = jsCarSchNext.getRecord(0).getFieldString("YD_CAR_USE_GP"); // G출하 L구내운송
    	    		
    	    		if ("L".equals(ydCarUseGp)) {
    	    			String sTrnEqpCdNext         = jsCarSchNext.getRecord(0).getFieldString("TRN_EQP_CD");
    		    		String sTrnWrkFullvoidGpNext = jsCarSchNext.getRecord(0).getFieldString("TRN_WRK_FULLVOID_GP");
    		    		String sWlocCdNext           = jsCarSchNext.getRecord(0).getFieldString("WLOC_CD");
    		    		
    		    		jrParam = commUtils.getParam(logId, mthdNm, sModifier);
    					jrParam.setField("TRN_EQP_CD"			, sTrnEqpCdNext        );	
    					jrParam.setField("WLOC_CD"				, sWlocCdNext          );	
    					jrParam.setField("TRN_WRK_FULLVOID_GP"	, sTrnWrkFullvoidGpNext);	
    					jrParam.setField("PNT_DMD_DT"			, commUtils.getDateTime14());			
    					
    					JDTORecord jrYdMsg = this.rcvTSYDJ002(jrParam);
    					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
    					
    					jrRtn.setField("RTN_CD"			, "1");
    					jrRtn.setField("RTN_MSG"		, sMsg);

    					commUtils.printLog(logId, mthdNm, "S-");
    					return jrRtn;
    	    		} 
    	    		
    	    		if ("G".equals(ydCarUseGp)) {
    	    			if ("N".equals(sChkAnotherCar)) {
    	    				/**********************************************************
    	    				* 12. 입동대기차량 빠른순으로 입동지시 요구 (YDYDJ553)
    	    				*     return : 1. YDDMR028 입동대기
    	    				*              2. YDY5L008 차량작업 예정정보
    	    				**********************************************************/
    	    				if (!"".equals(ydCarPntCd)) {
    	    					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
    	    					jrParam.setField("JMS_TC_CD"		 , "YDYDJ553");          //차량입동지시 요구 기존:YDYDJ662
    	    					jrParam.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
    	    					jrParam.setField("YD_CARPNT_CD"	     , ydCarPntCd);	// 입동포인트
    	    					jrParam.setField("CAR_NO"		     , sTmpCarNo);
    	    					jrParam.setField("CARD_NO"		     , sCardNo);

    	    					//PIDEV_S :병행가동용:PI_YD
    	    					jrParam.setField("PI_YD"		     , sPI_YD);
    	    					jrParam.setField("PI_YD1"		     , sPI_YD1);
	
    	    					if ("Y".equals(sApp838)) {
    	    						jrRtn = commUtils.addSndData(jrRtn, jrParam);
    	    					} else {
    	    						JDTORecord jrCarBayInOrdReq = this.rcvYDYDJ553(jrParam); //기존 YDYDJ662
        	    					
        	    					commUtils.printParam(logId, jrCarBayInOrdReq);
        	    					
        	    					String rtnCd = jrCarBayInOrdReq.getFieldString("RTN_CD");
        	    					
        	    					if ("0".equals(rtnCd)) {
        	    						sMsg = jrCarBayInOrdReq.getFieldString("RTN_MSG");
        	    	    				commUtils.printLog(logId, sMsg, "SL");
        	    	    				jrRtn.setField("RTN_CD"	, "0");
        	    	    				jrRtn.setField("RTN_MSG", sMsg);
        	    	    				commUtils.printLog(logId, mthdNm, "S-");
        	    	    				return jrRtn;
        	    					}
        	    					jrRtn = commUtils.addSndData(jrRtn, jrCarBayInOrdReq);	
    	    					}
    	    					
    	    					commUtils.printLog(logId, "YDYDJ553 차량입동지시요구 완료!", "SL");
    	    				}
    	    	    	}
    	    			
    	    			commUtils.printLog(logId, "출하차량 출발처리 - 맵비활성화 완료", "SL");
    	    			
    	    			jrRtn.setField("RTN_CD"			, "1");
    	    			jrRtn.setField("RTN_MSG"		, sMsg);
    	    			commUtils.printLog(logId, mthdNm, "S-");
    	    			
    	    			return jrRtn;
    	    			
    	    		} // G
	    		} 
    		} //app820
    		
			if ("N".equals(sChkAnotherCar)) {
				/**********************************************************
				* 12. 입동대기차량 빠른순으로 입동지시 요구 (YDYDJ553)
				*     return : 1. YDDMR028 입동대기
				*              2. YDY5L008 차량작업 예정정보
				**********************************************************/
				if (!"".equals(ydCarPntCd)) {
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("JMS_TC_CD"		 , "YDYDJ553");          //차량입동지시 요구 기존:YDYDJ662
					jrParam.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrParam.setField("YD_CARPNT_CD"	     , ydCarPntCd);	// 입동포인트
					jrParam.setField("CAR_NO"		     , sTmpCarNo);
					jrParam.setField("CARD_NO"		     , sCardNo);
							
					if ("Y".equals(sApp838)) {
						jrRtn = commUtils.addSndData(jrRtn, jrParam);
					} else {
						JDTORecord jrCarBayInOrdReq = this.rcvYDYDJ553(jrParam); //기존 YDYDJ662
    					
    					commUtils.printParam(logId, jrCarBayInOrdReq);
    					
    					String rtnCd = jrCarBayInOrdReq.getFieldString("RTN_CD");
    					
    					if ("0".equals(rtnCd)) {
    						sMsg = jrCarBayInOrdReq.getFieldString("RTN_MSG");
    	    				commUtils.printLog(logId, sMsg, "SL");
    	    				jrRtn.setField("RTN_CD"	, "0");
    	    				jrRtn.setField("RTN_MSG", sMsg);
    	    				commUtils.printLog(logId, mthdNm, "S-");
    	    				return jrRtn;
    					}
    					jrRtn = commUtils.addSndData(jrRtn, jrCarBayInOrdReq);	
					}
					
					commUtils.printLog(logId, "YDYDJ553 차량입동지시요구 완료!", "SL");
				}
	    	}
			
			sMsg = "출하차량 출발처리 - 맵비활성화 완료";
			commUtils.printLog(logId, sMsg, "SL");
			
			jrRtn.setField("RTN_CD"			, "1");
			jrRtn.setField("RTN_MSG"		, sMsg);

			commUtils.printParam(logId, jrRtn);
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 오퍼레이션명 : 차량입동지시요구 (제품이송)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord procCarBayInOrdReqTr(JDTORecord rcvMsg)throws DAOException  {
		String mthdNm = "차량입동지시요구[CCoilCarMvSeEJB.procCarBayInOrdReqTr] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		try{
		 	commUtils.printLog(logId, mthdNm, "S+");	
		 	commUtils.printParam(logId, rcvMsg);
		 	
		 	String ydCarpntCd       = commUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD"  )); //입동포인트
		 	String ydCarSchId       = commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID" )); //차량스케줄ID 
		 	String sCrFrtomoveGp	= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP")); 
		 	
		 	String sModifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
		 	
		 	JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		 	String sMsg = ""; 
		 	
		 	String sCurrDate = commUtils.getDateTime14();
		 	
		 	/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydCarpntCd)) {
				sMsg = "파라미터 확인 : 차량포인트가 존재하지 않습니다.";
				commUtils.printLog(logId, sMsg, "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
			}

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			/**********************************************************
			* 1. 해당 포인트 입동유무 체크 (좌우 통로 같이 검색)
			**********************************************************/
			jrParam.setField("YD_CAR_SCH_ID" , ydCarSchId);
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCarsch", logId, mthdNm, "차량스케줄 조회");
			
			if (jsCarSch.size() <= 0) {
				commUtils.printLog(logId, "차량스케줄 조회 오류", "SL");
				jrRtn.setField("RTN_CD"	, "0");
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
			}
				
			String ydCarUseGp 	    = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_USE_GP"  ));
			String sCarKind         = commUtils.trim(jsCarSch.getRecord(0).getFieldString("CAR_KIND"       ));
			String sTransOrdDate  	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_DATE" ));
			String sTransOrdSeqno	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_SEQNO"));
			String sCarNo 			= commUtils.trim(jsCarSch.getRecord(0).getFieldString("CAR_NO"         ));
			String sCardNo          = commUtils.trim(jsCarSch.getRecord(0).getFieldString("CARD_NO"        ));
			String ydEqpWrkStat     = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_EQP_WRK_STAT"));
			
			if (!"G".equals(ydCarUseGp)) {
				return jrRtn;
			}
	
			jrParam.setField("YD_CARPNT_CD" , ydCarpntCd);
	
			/*
			SELECT *
			  FROM (
			        SELECT DECODE(YD_CARPNT_CD,:V_YD_CARPNT_CD,1,2) AS CHK
			             , YD_CARPNT_CD
			             , WLOC_CD
			             , YD_PNT_CD
			             , YD_CAR_USETYPE_GP
			             , YD_STK_COL_GP
			             , CAR_CNT
			          FROM USRYDA.VW_YD_CARPOINT
			         WHERE CAR_CNT <= DECODE(YD_CARPNT_CD, :V_YD_CARPNT_CD, 10, 1)
			           AND TRN_EQP_CD IS NULL 
			           AND CARD_NO IS NULL
			           AND YD_CARPNT_CD LIKE SUBSTR(:V_YD_CARPNT_CD,1,3)||'%'
			           AND YD_CAR_USETYPE_GP IN ('TR','RT','RA','TO')
			           AND YD_GP NOT IN ('1','3')
			         UNION ALL 
			        SELECT 1
			             , YD_CARPNT_CD
			             , WLOC_CD
			             , YD_PNT_CD
			             , YD_CAR_USETYPE_GP
			             , YD_STK_COL_GP
			             , 0
			          FROM USRYDA.TB_YD_CARPOINT
			         WHERE YD_CARPNT_CD = :V_YD_CARPNT_CD
			           AND YD_GP IN ('1','3')
			           AND YD_STK_COL_ACT_STAT <> 'N'
			       ) A
			 ORDER BY CHK
			 */
			jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarpointsch", logId, mthdNm, "입동유무 체크");
			
			String sLoanPulloutAbleYn = "";
			String sWlocCd            = "";
			String ydPntCd            = "";
			String ydCarUsetypeGp     = "";
			String ydStkColGp         = "";
			String ydCarpntCd2        = "";
			
			if (jsCarSch.size() > 0) {
				
				ydCarUsetypeGp  = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_USETYPE_GP")); 			//야드적치열활성상태
				sWlocCd         = commUtils.trim(jsCarSch.getRecord(0).getFieldString("WLOC_CD"          )); 
				ydPntCd         = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_PNT_CD"        )); 
				ydCarpntCd2     = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CARPNT_CD"     )); 
				ydStkColGp      = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_STK_COL_GP"    )); 
				
				//차량상 포인트가 트레일러인 경우 에만 입동처리 가능
				if ("PT".equals(ydCarUsetypeGp) || "TT".equals(ydCarUsetypeGp) || "AT".equals(ydCarUsetypeGp)) {
					return jrRtn;
				}
				
				//상차도 변경
				jrParam.setField("YD_CAR_SCH_ID"		, ydCarSchId);
				
				if ("U".equals(ydEqpWrkStat)) { //상차
					jrParam.setField("YD_PNT_CD1"    		, ydPntCd);
					jrParam.setField("YD_CARLD_STOP_LOC"	, ydStkColGp);
				} else {
					jrParam.setField("YD_PNT_CD3"    		, ydPntCd);
					jrParam.setField("YD_CARUD_STOP_LOC"	, ydStkColGp);
				}
				
				/*
				UPDATE TB_YD_CARSCH
				   SET MODIFIER      = :V_MODIFIER
				     , MOD_DDTT      = SYSDATE
				     , YD_CARLD_STOP_LOC = NVL(:V_YD_CARLD_STOP_LOC, YD_CARLD_STOP_LOC)
				     , YD_PNT_CD1        = NVL(:V_YD_PNT_CD1       , YD_PNT_CD1       )
				     , YD_CARUD_STOP_LOC = NVL(:V_YD_CARUD_STOP_LOC, YD_CARUD_STOP_LOC)
				     , YD_PNT_CD3        = NVL(:V_YD_PNT_CD3       , YD_PNT_CD3       )
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarschYdCoil2", logId, mthdNm, "상차도변경");
				
				sLoanPulloutAbleYn = "Y";
				
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_CARPNT_CD"	 , ydCarpntCd   );	// 입동포인트
				jrParam.setField("YD_CAR_SCH_ID" , ydCarSchId   );	// 차량스케줄ID
				jrParam.setField("CAR_KIND"		 , sCarKind     );
				jrParam.setField("CR_FRTOMOVE_GP", sCrFrtomoveGp);
				
				String ydLRYn = coilDao.ApplyYn(logId, mthdNm, "APP800", "J", "*");
				jrParam.setField("CHK_YN"		, ydLRYn     ); //좌우포인트 검색여부
				commUtils.printLog(logId, "좌우포인트 검색여부 : "+ ydLRYn, "SL");
				
				JDTORecord jrYdMsg = this.rcvYDYDJ553(jrParam); //입동지시요구
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				
				if ("2".equals(jrYdMsg.getFieldString("RTN_CD"))) {
					commUtils.printLog(logId, "입동지시 전문 송신(RTN_CD : 2)", "SL");
					commUtils.printLog(logId, mthdNm, "S-");
					return jrRtn;
				}
				
			} else {
				commUtils.printLog(logId, "입동이 불 가능 한 경우 (대기차량 있음 or 상차차량 있음", "SL");
				sLoanPulloutAbleYn = "N";
			}
			
			/**********************************************************
			* 코일이송차량입동지시PDA 
			**********************************************************/
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrParam.setField("TRANS_ORD_DATE"      , sTransOrdDate );
		    jrParam.setField("TRANS_ORD_SEQNO"     , sTransOrdSeqno);
			
// PIDEV
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "CCoilCarMvSeEJBBean => procCarBayInOrdReqTr", "APPPI0", "J", "*");			
			
//			if("Y".equals(sApplyYnPI)) {
				
				jrYdMsg.setField("MQ_TC_CD"		        , "M10YDLMJ1061"    );
				jrYdMsg.setField("MQ_TC_CREATE_DDTT"    , sCurrDate         ); //JMSTC생성일시
				jrYdMsg.setField("CR_FRTOMOVE_GP"       , sCrFrtomoveGp     );
				
			    jrYdMsg.setField("TRN_REQ_DATE" 	    , sTransOrdDate     );
			    jrYdMsg.setField("TRN_REQ_SEQ"     		, sTransOrdSeqno    );
			    jrYdMsg.setField("CAR_NO"               , sCarNo            );
			    
			    jrYdMsg.setField("YD_GP"      			, "J"			    );
			    jrYdMsg.setField("DIST_GOODS_GP"       	, "H"			    );
			    jrYdMsg.setField("SCH_YN"       		, "Y"    			);
			    
			    jrYdMsg.setField("BAYIN_DDTT"           , sCurrDate         );
			    // jrYdMsg.setField("CARD_NO"              , sCardNo           );
			    
			    jrYdMsg.setField("YD_CARPNT_CD"         , ydCarpntCd2       ); 
			    jrYdMsg.setField("LOAN_PULLOUT_ABLE_YN" , sLoanPulloutAbleYn);				
				
//			} else {
				
//			jrYdMsg.setField("JMS_TC_CD"            , "YDDMR070"        );
//			jrYdMsg.setField("JMS_TC_CREATE_DDTT"   , sCurrDate         ); //JMSTC생성일시
//			jrYdMsg.setField("TC_CODE"		        , "YDDMR070"        );
//			jrYdMsg.setField("TC_CREATE_DDTT"       , sCurrDate         ); //JMSTC생성일시
//			jrYdMsg.setField("CR_FRTOMOVE_GP"       , sCrFrtomoveGp     );
//		    jrYdMsg.setField("TRANS_WORD_DATE"      , sTransOrdDate     );
//		    jrYdMsg.setField("TRANS_WORD_SEQNO"     , sTransOrdSeqno    );
//		    jrYdMsg.setField("BAYIN_DDTT"           , sCurrDate         );
//		    jrYdMsg.setField("CARD_NO"              , sCardNo           );
//		    jrYdMsg.setField("CAR_NO"               , sCarNo            );
//		    jrYdMsg.setField("YD_CARPNT_CD"         , ydCarpntCd2       ); 
//		    jrYdMsg.setField("LOAN_PULLOUT_ABLE_YN" , sLoanPulloutAbleYn);
			
//			}
		    jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
			
			
			commUtils.printLog(logId, mthdNm, "S-");

			jrRtn.setField("RTN_CD"	, "1");
			return jrRtn;
	
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	
	}
	
	
	/**
	 * 오퍼레이션명 : 차량입동지시요구 (기존 procCarBayInOrdReqNEW YDYDJ662 -> YDYDJ553)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord rcvYDYDJ553(JDTORecord rcvMsg)throws DAOException  {

		String mthdNm = "차량입동지시요구[CCoilCarMvSeEJB.rcvYDYDJ553] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		try{
		 	commUtils.printLog(logId, mthdNm, "S+");	
		 	commUtils.printParam(logId, rcvMsg);
		 	
		 	String sCarKind		    = commUtils.nvl (rcvMsg.getFieldString("CAR_KIND"         ), "TR");
		 	String ydCarpntCd       = commUtils.nvl (rcvMsg.getFieldString("YD_CARPNT_CD"     ), ""  ); //입동포인트   - 필수항목 
		 	String ydCarSchId       = commUtils.nvl (rcvMsg.getFieldString("YD_CAR_SCH_ID"    ), ""  ); //차량스케줄ID - 선택항목 
		 	String sChkYn           = commUtils.nvl (rcvMsg.getFieldString("CHK_YN"           ), "Y" ); //입동tc 전송 유무 -> 반품회송시에는 N으로 해야함
		 	
		 	String sTransFrtomoveGp = commUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP"));  //1 운송지시 2 이송지시
		 	String sCrFrtomoveGp    = commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP"   ));  //냉연이송구분
		 	
		 	String sModifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"         ));       //수정자(Backup Only)

		 	// PIDEV
//		 	String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "CCoilCarMvSeEJBBean => rcvYDYDJ553", "APPPI0", "J", "*");
		 	
		 	String sPI_YD  = "";
//		 	if("Y".equals(sApplyYnPI)) {
		 		sPI_YD     = "J";
//		 	} else {
//		 		sPI_YD     = "*";
//		 	}
		 	
		 	if ("".equals(sModifier)) { sModifier = "YDYDJ553"; }
		 	
		 	JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		 	String sMsg      = "";
		 	String sCurrDate = commUtils.getDateTime14();
		 	
		 	/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydCarpntCd)) {
				sMsg = "파라미터 확인 : 입동포인트가 존재하지 않습니다.";
				commUtils.printLog(logId, sMsg, "S-");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", sMsg);
				return jrRtn;
			}

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			/**********************************************************
			* 1. 해당 포인트 입동유무 체크 (좌우 통로 같이 검색)
			**********************************************************/
			commUtils.printLog(logId, "1. 해당 포인트 입동유무 체크 (좌우 통로 같이 검색)", "SL");
			jrParam.setField("YD_CARPNT_CD"     , ydCarpntCd);
			jrParam.setField("CAR_KIND"         , sCarKind  );
			String ydPntGp = "J";
	         if(ydCarpntCd.startsWith("J3")){
	        	 ydPntGp = "H"; // 소재포인트
	         }
	         jrParam.setField("YD_PNT_GP"     , ydPntGp);
			///해당 포인트 입동대상 차량스케줄 조회
			JDTORecordSet jsRst = JDTORecordFactory.getInstance().createRecordSet("");
			if ("N".equals(sChkYn)) {
				/*
				WITH TEMP_TABLE AS (
				SELECT :V_YD_CARPNT_CD AS V_YD_CARPNT_CD
				     , :V_CAR_KIND     AS V_CAR_KIND
				     , (SELECT ITEM1
				          FROM USRYDA.TB_YD_RULE
				         WHERE REPR_CD_GP = 'J00002'
				           AND CD_GP = '*'
				           AND ITEM  = '*') AS PRE_SUP_YN
				  FROM DUAL
				)
				--//TR--------------------------------------
				SELECT *
				  FROM TB_YD_CARPOINT A
				     , TEMP_TABLE B
				 WHERE A.YD_CARPNT_CD = B.V_YD_CARPNT_CD
				   AND A.DEL_YN       = 'N'
				   AND A.YD_STK_COL_ACT_STAT = 'C' --사용가능
				   AND B.V_CAR_KIND   ='TR'
				   AND A.YD_CAR_USETYPE_GP IN('TR','RT','RA','TO') --트레일러
				   AND ((A.YD_CARPNT_CD LIKE 'J%' AND B.PRE_SUP_YN = 'N') OR ( A.YD_CARPNT_CD NOT LIKE 'J%' AND 1=1)) --차량입동제한 Y:사용 , N: 사용안함
				 UNION ALL
				--//TT--------------------------------------
				SELECT *
				  FROM TB_YD_CARPOINT A
				     , TEMP_TABLE B
				 WHERE A.YD_CARPNT_CD = B.V_YD_CARPNT_CD
				   AND A.DEL_YN       = 'N'
				   AND A.YD_STK_COL_ACT_STAT = 'C' --사용가능
				   AND B.V_CAR_KIND   = 'TT'
				   AND A.YD_CAR_USETYPE_GP IN('TT','AT','RT','TO') --TT_CAR
				   AND ((A.YD_CARPNT_CD LIKE 'J%' AND B.PRE_SUP_YN = 'N') OR ( A.YD_CARPNT_CD NOT LIKE 'J%' AND 1=1)) --차량입동제한 Y:사용 , N: 사용안함
				 UNION ALL
				--//PT--------------------------------------
				SELECT *
				  FROM TB_YD_CARPOINT A
				     , TEMP_TABLE B
				 WHERE A.YD_CARPNT_CD = B.V_YD_CARPNT_CD
				   AND A.DEL_YN       ='N'
				   AND A.YD_STK_COL_ACT_STAT = 'C' --사용가능
				   AND B.V_CAR_KIND   = 'PT'
				   AND A.YD_CAR_USETYPE_GP IN('PT', 'GT','AT','RA','TO') --PT
				   AND ((A.YD_CARPNT_CD LIKE 'J%' AND B.PRE_SUP_YN = 'N') OR ( A.YD_CARPNT_CD NOT LIKE 'J%' AND 1=1)) --차량입동제한 Y:사용 , N: 사용안함
				 
				 */
				jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarPointCHK2", logId, mthdNm, "차량포인트 조회");
			} else {
				/*
				WITH TEMP_TABLE AS (
				SELECT :V_YD_CARPNT_CD AS V_YD_CARPNT_CD
				     , :V_CAR_KIND   AS V_CAR_KIND
				     , ITEM1 AS PRE_SUP_YN
				     , CD_GP 
				  FROM TB_YD_RULE
				 WHERE REPR_CD_GP = 'J00002'
				)
				--//TR--------------------------------------
				SELECT *
				  FROM TB_YD_CARPOINT A
				     , TEMP_TABLE B
				 WHERE A.YD_CARPNT_CD LIKE SUBSTR(B.V_YD_CARPNT_CD,1,3) ||'%'
				   AND A.DEL_YN='N'
				   AND A.YD_STK_COL_ACT_STAT='C' --사용가능
				   AND B.V_CAR_KIND='TR'
				   AND A.YD_CAR_USETYPE_GP IN('TR', 'RT','RA','TO') --트레일러
				   AND (
				       (A.YD_CARPNT_CD like 'J%' AND (B.PRE_SUP_YN='N' AND CD_GP=(CASE WHEN LENGTH(CD_GP)=2 THEN YD_BAY_GP||SUBSTR(YD_CARPNT_CD,2,1) ELSE YD_BAY_GP END)) ) 
				        OR 
				       ( A.YD_CARPNT_CD NOT like 'J%' AND 1=1)
				      ) --차량입동제한 Y:사용 , N: 사용안함
				UNION ALL
				--//TT--------------------------------------
				SELECT *
				  FROM TB_YD_CARPOINT A
				     , TEMP_TABLE B
				 WHERE A.YD_CARPNT_CD LIKE SUBSTR(B.V_YD_CARPNT_CD,1,3) ||'%'
				   AND A.DEL_YN='N'
				   AND A.YD_STK_COL_ACT_STAT='C' --사용가능
				   AND B.V_CAR_KIND='TT'
				   AND A.YD_CAR_USETYPE_GP IN('TT', 'AT','RT','TO') --TT_CAR
				   AND ((A.YD_CARPNT_CD like 'J%' AND (B.PRE_SUP_YN='N' AND CD_GP=(CASE WHEN LENGTH(CD_GP)=2 THEN YD_BAY_GP||SUBSTR(YD_CARPNT_CD,2,1) ELSE YD_BAY_GP END)) ) 
				        OR 
				       ( A.YD_CARPNT_CD NOT like 'J%' AND 1=1)) --차량입동제한 Y:사용 , N: 사용안함
				 UNION ALL
				--//PT--------------------------------------
				SELECT *
				 FROM TB_YD_CARPOINT A
				    , TEMP_TABLE B
				 WHERE A.YD_CARPNT_CD LIKE SUBSTR(B.V_YD_CARPNT_CD,1,3) ||'%'
				   AND A.DEL_YN = 'N'
				   AND A.YD_STK_COL_ACT_STAT='C' --사용가능
				   AND B.V_CAR_KIND='PT'
				   AND A.YD_CAR_USETYPE_GP IN('PT', 'GT','AT','RA','TO') --PT 
				   AND ((A.YD_CARPNT_CD like 'J%' AND (B.PRE_SUP_YN='N' AND CD_GP=(CASE WHEN LENGTH(CD_GP)=2 THEN YD_BAY_GP||SUBSTR(YD_CARPNT_CD,2,1) ELSE YD_BAY_GP END)) ) 
				        OR 
				        ( A.YD_CARPNT_CD NOT like 'J%' AND 1=1)) --차량입동제한 Y:사용 , N: 사용안함
				 UNION ALL
				--//소재포인트------------------------------ 
				SELECT *
				  FROM TB_YD_CARPOINT A
				     , TEMP_TABLE B
				 WHERE A.YD_CARPNT_CD LIKE SUBSTR(B.V_YD_CARPNT_CD,1,3) ||'%'
				   AND A.DEL_YN='N'
				   AND B.V_YD_CARPNT_CD LIKE 'H%'
				   AND A.YD_STK_COL_ACT_STAT = 'C' --사용가능 
				   AND  ((A.YD_CARPNT_CD like 'J%' AND (B.PRE_SUP_YN='N' AND CD_GP=(CASE WHEN LENGTH(CD_GP)=2 THEN YD_BAY_GP||SUBSTR(YD_CARPNT_CD,2,1) ELSE YD_BAY_GP END)) ) 
				        OR 
				        ( A.YD_CARPNT_CD NOT like 'J%' AND 1=1)) --차량입동제한 Y:사용 , N: 사용안함
				 */
				if(ydCarpntCd.startsWith("J3")){
					jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarPointCHK_H", logId, mthdNm, "소재차량포인트 조회");
		         }else{
				    jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarPointCHK", logId, mthdNm, "출하차량포인트 조회");
		         }
			}
			
			if (jsRst.size() <= 0) {
				sMsg = "TB_YD_CARSCH [해당 포인트 ["+ydCarpntCd+"] 입동이 불가능 합니다.]";
				commUtils.printLog(logId, sMsg, "SL");
				
				/**********************************************************
				* 1.1 차량스케줄 조회
				**********************************************************/
				jrParam.setField("YD_CAR_SCH_ID"   , ydCarSchId    );
				
				JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCarsch", logId, mthdNm, "차량스케줄 조회");
				
				if (jsCarSch.size() > 0) {
					String sTransOrdDate  	   = commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_DATE"      ));
					String sTransOrdSeqno	   = commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_SEQNO"     ));
					String sCarNo 			   = commUtils.trim(jsCarSch.getRecord(0).getFieldString("CAR_NO"              ));
					String sCardNo             = commUtils.trim(jsCarSch.getRecord(0).getFieldString("CARD_NO"             ));
					String sTransEquipmentType = commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_EQUIPMENT_TYPE"));
					String sYdGp 			   = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_GP"));
					
					/**********************************************************
					* 1.1.1입동대기TC 전송
					**********************************************************/
					// PIDEV
//					if("Y".equals(sApplyYnPI)) {
						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
						jrParam.setField("TRANS_ORD_DATE"  , sTransOrdDate );
						jrParam.setField("TRANS_ORD_SEQNO" , sTransOrdSeqno);					
						
						jrYdMsg.setField("MQ_TC_CD"		        , "M10YDLMJ1061");
						jrYdMsg.setField("MQ_TC_CREATE_DDTT"    , sCurrDate     ); //JMSTC생성일시
						
						
						jrYdMsg.setField("TRN_REQ_DATE"         , sTransOrdDate ); // 운송지시일자
					    jrYdMsg.setField("TRN_REQ_SEQ"    	    , sTransOrdSeqno); // 운송의뢰순번

					    if ("P".equals(sTransEquipmentType)) {
							jrYdMsg.setField("SCH_YN"       , "Y");
						} else {
							jrYdMsg.setField("SCH_YN"       , "N");
						}
					    
					    jrYdMsg.setField("CAR_NO"               , sCarNo        ); // 차량번호
					    jrYdMsg.setField("YD_GP"                , sYdGp	       ); // 야드구분
					    jrYdMsg.setField("DIST_GOODS_GP"        , "H"	       ); // 출하제품구분
					    
					    jrYdMsg.setField("BAYIN_DDTT"           , sCurrDate     ); // 입동일시
					    jrYdMsg.setField("WLOC_CD"       	    , ""            ); // 개소코드 
					    jrYdMsg.setField("YD_PNT_CD"            , ""            ); // 야드포인트코드
					    jrYdMsg.setField("YD_CARPNT_CD"         , ""            ); 
					    jrYdMsg.setField("LOAN_PULLOUT_ABLE_YN" ,"N"            ); // 차입인출가능여부		
					    
					    jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
					    jrRtn.setField("RTN_CD"	, "2"); // 입동지시 전문 송신 : 2 EXIST
					    return jrRtn;   
//					} else {
//						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//						jrParam.setField("TRANS_ORD_DATE"  , sTransOrdDate );
//						jrParam.setField("TRANS_ORD_SEQNO" , sTransOrdSeqno);
//						
//						if ("P".equals(sTransEquipmentType)) {
//							jrYdMsg.setField("JMS_TC_CD"            , "YDDMR070");
//							jrYdMsg.setField("TC_CODE"		        , "YDDMR070");
//							jrYdMsg.setField("CR_FRTOMOVE_GP"       , sCrFrtomoveGp);
//						} else {
//							jrYdMsg.setField("JMS_TC_CD"            , "YDDMR028");
//							jrYdMsg.setField("TC_CODE"		        , "YDDMR028");
//							jrYdMsg.setField("WLOC_CD"              , ""  );
//						    jrYdMsg.setField("YD_PNT_CD"            , ""  );
//						}
//						
//						jrYdMsg.setField("TC_CREATE_DDTT"      , sCurrDate     ); //JMSTC생성일시
//						jrYdMsg.setField("JMS_TC_CREATE_DDTT"  , sCurrDate     ); //JMSTC생성일시
//					    jrYdMsg.setField("TRANS_WORD_DATE"     , sTransOrdDate );
//					    jrYdMsg.setField("TRANS_WORD_SEQNO"    , sTransOrdSeqno);
//					    jrYdMsg.setField("CARD_NO"             , sCardNo       );
//					    jrYdMsg.setField("CAR_NO"              , sCarNo        );
//					    jrYdMsg.setField("YD_CARPNT_CD"        , ""            ); 
//					    jrYdMsg.setField("LOAN_PULLOUT_ABLE_YN","N"            );
//					    jrYdMsg.setField("BAYIN_DDTT"          , sCurrDate     ); //입동일시
//						
//					    jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
//					    jrRtn.setField("RTN_CD"	, "2"); // 입동지시 전문 송신 : 2 EXIST
//					    return jrRtn;
//					}
				}
					
			} else {
				sMsg = "TB_YD_CARSCH [해당 포인트 ["+ydCarpntCd+"] 입동이 가능 합니다.]";
				commUtils.printLog(logId, sMsg, "SL");
	 
				String ydCarpntCdOld = ydCarpntCd;
						
				/**********************************************************
				* 1.2 해당 포인트 입동대상 차량스케줄 조회(좌우 통로 같이 검색)
				**********************************************************/
				jrParam.setField("YD_CARPNT_CD"		, ydCarpntCd);
				
				/*
				WITH TEMP_KEY AS (
				SELECT :V_YD_CARPNT_CD AS YD_CARPNT_CD
				  FROM DUAL
				)
				, TEMP_CARPOINT AS (
				SELECT A.*
				  FROM TB_YD_CARPOINT A
				     , TEMP_KEY B
				 WHERE A.YD_CARPNT_CD LIKE (CASE WHEN B.YD_CARPNT_CD LIKE 'J1A%' THEN B.YD_CARPNT_CD ELSE SUBSTR(B.YD_CARPNT_CD,1,3)||'%' END)
				)
				, TEMP_TABLE AS (
				SELECT B.YD_CAR_SCH_ID
				     , B.TRANS_ORD_DATE
				     , B.TRANS_ORD_SEQNO
				     , B.YD_GP
				     , B.YD_BAYIN_WO_SEQ
				     , B.CAR_KIND
				     , B.CAR_NO
				     , B.TRN_EQP_CD
				     , B.CARD_NO
				     , B.YD_CAR_WRK_GP
				     , B.TRANS_EQUIPMENT_TYPE
				     , B.YD_CARPNT_CD AS YD_CARPNT_CD2
				  FROM TEMP_CARPOINT A
				     , (
				        SELECT *
				          FROM (
				                SELECT A.YD_STK_COL_ACT_STAT
				                     , A.YD_STK_COL_GP
				                     , B.YD_CAR_SCH_ID
				                     , B.TRANS_ORD_DATE
				                     , B.TRANS_ORD_SEQNO
				                     , (CASE WHEN SPOS_WLOC_CD IN ('DJY1E') THEN 'J'
				                             WHEN SPOS_WLOC_CD IN ('D3Y41','D3Y42') THEN '3'
				                             WHEN SPOS_WLOC_CD IN ('D2Y44','D2Y45') THEN '1'
				                             WHEN SPOS_WLOC_CD IN ('DJY21','DJY22') THEN 'H'
				                        END) AS YD_GP
				                      , B.YD_BAYIN_WO_SEQ
				                      , B.CAR_KIND
				                      , B.CAR_NO
				                      , B.TRN_EQP_CD
				                      , B.CARD_NO
				                      , B.YD_CAR_WRK_GP
				                      , B.TRANS_EQUIPMENT_TYPE
				                      , A.YD_CARPNT_CD
				                   FROM TEMP_CARPOINT A
				                      , (SELECT *
				                           FROM TB_YD_CARSCH B
				                          WHERE B.DEL_YN = 'N'
				                            AND (B.YD_CAR_PROG_STAT =(CASE WHEN B.YD_CAR_PROG_STAT IN('A','B') THEN 'A' ELSE '1' END)  OR  B.YD_CAR_PROG_STAT='1')
				                            AND B.CAR_NO IS NOT NULL
				                            AND (B.CAR_NO NOT LIKE 'G%' OR  B.TRANS_ORD_SEQNO>=999000)  --//TT카 제외
				                        ) B
				                      , TEMP_KEY C
				                  WHERE A.YD_CARPNT_CD LIKE ( CASE WHEN  B.YD_CAR_PROG_STAT IN('A','B') AND B.IF_SEQ_NO =1 THEN C.YD_CARPNT_CD ELSE SUBSTR(C.YD_CARPNT_CD,1,3)||'%' END )
				                    AND A.YD_PNT_CD     = (CASE WHEN B.YD_CAR_PROG_STAT IN('A','B') THEN B.YD_PNT_CD3        ELSE B.YD_PNT_CD1        END)
				                    AND A.YD_STK_COL_GP = (CASE WHEN B.YD_CAR_PROG_STAT IN('A','B') THEN B.YD_CARUD_STOP_LOC ELSE B.YD_CARLD_STOP_LOC END)
				                  ORDER BY B.YD_BAYIN_WO_SEQ ,B.YD_CAR_SCH_ID
				               ) CC
				         WHERE ROWNUM <= 6   --대기차량 중 최대 6대까지만 입동 순위가 변경 가능 함.
				       ) B
				     , TEMP_KEY C
				 WHERE A.YD_CARPNT_CD LIKE (CASE WHEN C.YD_CARPNT_CD LIKE 'J1A%' THEN C.YD_CARPNT_CD ELSE SUBSTR(C.YD_CARPNT_CD,1,3)||'%' END)
				   AND A.YD_STK_COL_ACT_STAT NOT IN ('N','L')
				)
				, TEMP_TABLE2 AS (
				SELECT *
				  FROM (
				        SELECT *
				          FROM TEMP_TABLE C
				             , (SELECT A.YD_STK_COL_GP
				                     , A.YD_PNT_CD
				                     , A.WLOC_CD
				                     , A.YD_CARPNT_CD
				                     , A.YD_CAR_USETYPE_GP
				                  FROM TB_YD_CARPOINT A
				                     , TEMP_KEY B
				                 WHERE A.YD_CARPNT_CD LIKE (CASE WHEN B.YD_CARPNT_CD LIKE 'JA%' THEN B.YD_CARPNT_CD ELSE SUBSTR(B.YD_CARPNT_CD,1,3)||'%' END)
				                   AND A.YD_STK_COL_ACT_STAT NOT IN ('N','L')
				               ) D
				         WHERE  ( (C.CAR_KIND='TR' AND D.YD_CAR_USETYPE_GP IN('TR', 'RT','RA','TO'))
				                OR
				                (C.CAR_KIND='TR' AND C.CAR_NO IN ('0000','2222','3333','9999','7777','8888') AND D.YD_CAR_USETYPE_GP IN('MT')) --//차량동간이적
				                OR
				                (C.CAR_KIND='TT' AND D.YD_CAR_USETYPE_GP IN('TT', 'AT','RT','TO'))
				                 OR
				                 (C.CAR_KIND='PT' AND D.YD_CAR_USETYPE_GP IN('PT', 'GT','AT','RA','TO'))
				                 OR
				                 (C.YD_GP ='H' AND D.YD_CARPNT_CD=C.YD_CARPNT_CD2)
				                )
				
				         ORDER BY C.YD_BAYIN_WO_SEQ  --차량운선순위
				                , C.YD_CAR_SCH_ID
				       ) A
				 WHERE ROWNUM<=1
				)
				SELECT *
				  FROM (
				        SELECT CHK
				             , E.TRANS_ORD_DATE
				             , E.TRANS_ORD_SEQNO
				             , E.YD_CAR_SCH_ID
				             , E.YD_BAYIN_WO_SEQ
				             , E.CAR_KIND
				             , E.CAR_NO
				             , E.CARD_NO
				             , E.TRN_EQP_CD
				             , E.YD_CAR_WRK_GP
				             , A.YD_CARPNT_CD
				             , A.YD_STK_COL_GP
				             , A.YD_PNT_CD
				             , (SELECT YD_CARPNT_CD
				                  FROM USRYDA.TB_YD_CARPOINT
				                 WHERE YD_STK_COL_GP = (CASE WHEN E.YD_CAR_PROG_STAT IN('A','B') THEN E.YD_CARUD_STOP_LOC ELSE E.YD_CARLD_STOP_LOC END)
				                   AND DEL_YN = 'N'
				               ) AS OLD_YD_CARPNT_CD
				             , (CASE WHEN E.YD_CAR_PROG_STAT IN('A','B') THEN E.YD_CARUD_STOP_LOC ELSE E.YD_CARLD_STOP_LOC END)  AS OLD_YD_STK_COL_GP
				             , (CASE WHEN E.YD_CAR_PROG_STAT IN('A','B') THEN E.YD_PNT_CD3 ELSE E.YD_PNT_CD1 END) AS YD_PNT_CD1
				             , A.YD_STK_COL_GP AS YD_CARLD_STOP_LOC
				             , A.WLOC_CD
				             , E.TRANS_EQUIPMENT_TYPE
				             , E.YD_EQP_WRK_STAT
				          FROM (
				                --2열연
				                SELECT 1 AS CHK 
				                     , (--//왼쪽 상단 코일 대기차량
				                        SELECT E.YD_CAR_SCH_ID
				                          FROM VW_YD_STKLYERSEARCH C
				                             , TB_YD_STOCK D
				                             , TEMP_TABLE  E
				                         WHERE C.STL_NO      = A.STL_NO
				                           AND C.STL_NO_LEFT = D.STL_NO
				                           AND D.TRANS_ORD_DATE  = E.TRANS_ORD_DATE
				                           AND D.TRANS_ORD_SEQNO = E.TRANS_ORD_SEQNO
				                           AND C.YD_STK_COL_GP LIKE SUBSTR(B.YD_STK_COL_GP,1, 2) ||'%'
				                           AND ROWNUM <= 1
				                       ) AS YD_CAR_SCH_ID
				                     , B.YD_CARPNT_CD
				                     , B.YD_STK_COL_GP
				                     , B.YD_CAR_WRK_GP
				                     , B.YD_PNT_CD
				                     , B.WLOC_CD
				                  FROM TB_YD_STOCK  A
				                     , TEMP_TABLE2  B
				                 WHERE 'J' = B.YD_GP
				                   AND A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
				                   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
				
				                 UNION ALL
				
				                SELECT 2 AS CHK
				                     , ( --//오른쪽 상단 코일 대기차량
				                        SELECT E.YD_CAR_SCH_ID
				                          FROM VW_YD_STKLYERSEARCH C
				                             , TB_YD_STOCK D
				                             , TEMP_TABLE E
				                         WHERE C.STL_NO       = A.STL_NO
				                           AND C.STL_NO_RIGHT = D.STL_NO
				                           AND D.TRANS_ORD_DATE  = E.TRANS_ORD_DATE
				                           AND D.TRANS_ORD_SEQNO = E.TRANS_ORD_SEQNO
				                           AND C.YD_STK_COL_GP LIKE SUBSTR(B.YD_STK_COL_GP,1, 2) ||'%'
				                           AND ROWNUM <= 1
				                       ) AS YD_CAR_SCH_ID
				                     , B.YD_CARPNT_CD
				                     , B.YD_STK_COL_GP
				                     , B.YD_CAR_WRK_GP
				                     , B.YD_PNT_CD
				                     , B.WLOC_CD
				                  FROM TB_YD_STOCK A
				                     , TEMP_TABLE2 B
				                 WHERE 'J' = B.YD_GP
				                   AND A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
				                   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
				
				                 UNION ALL
				              --1열연
				                SELECT 3 AS CHK
				                     , (SELECT E.YD_CAR_SCH_ID
				                          FROM VW_YD_STKLYERSEARCH C
				                             , TB_YM_STOCK D
				                             , TEMP_TABLE E
				                         WHERE C.STL_NO      = A.STOCK_ID
				                           AND C.STL_NO_LEFT = D.STOCK_ID
				                           AND D.TRANS_ORD_DATE2  = E.TRANS_ORD_DATE
				                           AND D.TRANS_ORD_SEQNO2 = E.TRANS_ORD_SEQNO
				                           AND C.YD_STK_COL_GP LIKE SUBSTR(B.YD_STK_COL_GP,1, 2) ||'%'
				                           AND ROWNUM <= 1
				                       ) AS YD_CAR_SCH_ID
				                     , B.YD_CARPNT_CD 
				                     , B.YD_STK_COL_GP
				                     , B.YD_CAR_WRK_GP
				                     , B.YD_PNT_CD 
				                     , B.WLOC_CD
				                  FROM TB_YM_STOCK A
				                     , TEMP_TABLE2 B
				                 WHERE '3' = B.YD_GP
				                   AND A.TRANS_ORD_DATE2  = B.TRANS_ORD_DATE
				                   AND A.TRANS_ORD_SEQNO2 = B.TRANS_ORD_SEQNO
				                 UNION ALL
				                SELECT 4 AS CHK
				                     , (SELECT E.YD_CAR_SCH_ID
				                          FROM VW_YD_STKLYERSEARCH C
				                             , TB_YM_STOCK D
				                             , TEMP_TABLE E
				                         WHERE C.STL_NO       = A.STOCK_ID
				                           AND C.STL_NO_RIGHT = D.STOCK_ID
				                           AND D.TRANS_ORD_DATE2  = E.TRANS_ORD_DATE
				                           AND D.TRANS_ORD_SEQNO2 = E.TRANS_ORD_SEQNO
				                           AND C.YD_STK_COL_GP LIKE SUBSTR(B.YD_STK_COL_GP,1, 2) ||'%'
				                           AND ROWNUM <= 1
				                       ) AS YD_CAR_SCH_ID
				                     , B.YD_CARPNT_CD
				                     , B.YD_STK_COL_GP
				                     , B.YD_CAR_WRK_GP
				                     , B.YD_PNT_CD 
				                     , B.WLOC_CD
				                  FROM TB_YM_STOCK A
				                     , TEMP_TABLE2 B
				                 WHERE '3' = B.YD_GP
				                   AND A.TRANS_ORD_DATE2  = B.TRANS_ORD_DATE
				                   AND A.TRANS_ORD_SEQNO2 = B.TRANS_ORD_SEQNO
				                
				                UNION ALL   
				                --박판
				                SELECT 5 AS CHK 
				                     , (--//왼쪽 상단 코일 대기차량
				                        SELECT E.YD_CAR_SCH_ID
				                          FROM VW_YD_STKLYERSEARCH C
				                             , TB_YF_STOCK D
				                             , TEMP_TABLE  E
				                         WHERE C.STL_NO      = A.STL_NO
				                           AND C.STL_NO_LEFT = D.STL_NO
				                           AND D.TRANS_ORD_DATE  = E.TRANS_ORD_DATE
				                           AND D.TRANS_ORD_SEQNO = E.TRANS_ORD_SEQNO
				                           AND C.YD_STK_COL_GP LIKE SUBSTR(B.YD_STK_COL_GP,1, 2) ||'%'
				                           AND ROWNUM <= 1
				                       ) AS YD_CAR_SCH_ID
				                     , B.YD_CARPNT_CD
				                     , B.YD_STK_COL_GP
				                     , B.YD_CAR_WRK_GP
				                     , B.YD_PNT_CD
				                     , B.WLOC_CD
				                  FROM TB_YF_STOCK  A
				                     , TEMP_TABLE2  B
				                 WHERE '1' = B.YD_GP
				                   AND A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
				                   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
				
				                 UNION ALL
				
				                SELECT 6 AS CHK
				                     , ( --//오른쪽 상단 코일 대기차량
				                        SELECT E.YD_CAR_SCH_ID
				                          FROM VW_YD_STKLYERSEARCH C
				                             , TB_YF_STOCK D
				                             , TEMP_TABLE E
				                         WHERE C.STL_NO       = A.STL_NO
				                           AND C.STL_NO_RIGHT = D.STL_NO
				                           AND D.TRANS_ORD_DATE  = E.TRANS_ORD_DATE
				                           AND D.TRANS_ORD_SEQNO = E.TRANS_ORD_SEQNO
				                           AND C.YD_STK_COL_GP LIKE SUBSTR(B.YD_STK_COL_GP,1, 2) ||'%'
				                           AND ROWNUM <= 1
				                       ) AS YD_CAR_SCH_ID
				                     , B.YD_CARPNT_CD
				                     , B.YD_STK_COL_GP
				                     , B.YD_CAR_WRK_GP
				                     , B.YD_PNT_CD
				                     , B.WLOC_CD
				                  FROM TB_YF_STOCK A
				                     , TEMP_TABLE2 B
				                 WHERE '1' = B.YD_GP
				                   AND A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
				                   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO                   
				                   
				                 UNION ALL
				                 SELECT 7 AS CHK
				                      , YD_CAR_SCH_ID
				                      , YD_CARPNT_CD 
				                      , YD_STK_COL_GP
				                      , YD_CAR_WRK_GP
				                      , YD_PNT_CD 
				                      , WLOC_CD
				                   FROM TEMP_TABLE2
				               ) A
				             , TB_YD_CARSCH E
				         WHERE A.YD_CAR_SCH_ID IS NOT NULL
				           AND A.YD_CAR_SCH_ID = E.YD_CAR_SCH_ID
				         ORDER BY CHK  
				                , A.YD_CAR_SCH_ID
				       ) A
				 WHERE (A.CAR_NO NOT LIKE 'G%' OR  A.TRANS_ORD_SEQNO >= 999000)
				   AND ROWNUM <= 1
				 */
				JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarPointCarSchSelect", logId, mthdNm, "차량스케줄 조회");
				
				if (jsCarSch.size() <= 0) {
					sMsg = "TB_YD_CARSCH[해당 포인트 ["+ydCarpntCd+"] 입동대상 차량스케줄이 없습니다.]";
					commUtils.printLog(logId, sMsg, "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sMsg);
					commUtils.printLog(logId, mthdNm, "S-");
					return jrRtn;
				}
				
				String sNewYdCarSchId      = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID"       ), "");
			    String sNewCarKind         = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("CAR_KIND"            ), "TR");
			    String sNewCarNo           = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("CAR_NO"              ), "");
			    String sNewCardNo          = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("CARD_NO"             ), "");
			    String sNewTrnEqpCd        = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("TRN_EQP_CD"          ), "");     
			    String sNewTransOrdDate    = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_DATE"      ), "");
			    String sNewTransOrdSeqno   = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_SEQNO"     ), ""); 
			    String ydCarWrkGp          = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("YD_CAR_WRK_GP"       ), ""); 
			    //신규 포인트 차상 위치                                                                                           
			    String sNewYdCarpntCd      = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("YD_CARPNT_CD"        ), ""); 
			    String sNewYdStkColGp      = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("YD_STK_COL_GP"       ), ""); 
			    String sNewYdPntCd         = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("YD_PNT_CD"           ), "");
			    //기존 포인트 차상 위치 
			    String sOldYdCarpntCd      = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("OLD_YD_CARPNT_CD"    ), ""); 
			    String sOldYdStkColGp      = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("OLD_YD_STK_COL_GP"   ), ""); 
			    String sOldYdPntCd         = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("YD_PNT_CD1"          ), "");
			     
			    String sNewWlocCd          = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("WLOC_CD"             ), "");
			    String sTransEquipmentType = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("TRANS_EQUIPMENT_TYPE"), "");
			    String ydEqpWrkStat        = commUtils.nvl(jsCarSch.getRecord(0).getFieldString("YD_EQP_WRK_STAT"     ), "");
			     
			    String ydGp                = sNewYdStkColGp.substring(0, 1);
				
			    //------------------------------------------------------------------------------------------------------------
		    	//	1.차량포인트 입동 점유 , 2.차량상태 도착처리
		    	//------------------------------------------------------------------------------------------------------------
			    jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			    jrParam.setField("YD_STK_COL_ACT_STAT", "L"           );
			    jrParam.setField("CAR_NO"             , sNewCarNo     );
			    jrParam.setField("CARD_NO"            , sNewCardNo    );
			    jrParam.setField("TRN_EQP_CD"         , sNewTrnEqpCd  );         
			    jrParam.setField("YD_CAR_SCH_ID"      , sNewYdCarSchId);
			    jrParam.setField("SPOS_WLOC_CD"       , sNewWlocCd    ); //발지개소코드
			    
			    jrParam.setField("NEW_YD_CARPNT_CD"   , sNewYdCarpntCd);
			    jrParam.setField("OLD_YD_CARPNT_CD"   , sOldYdCarpntCd);
			    
			    jrParam.setField("YD_CARPNT_CD"       , sNewYdCarpntCd);
			    jrParam.setField("YD_CARLD_STOP_LOC"  , sNewYdStkColGp);
			    jrParam.setField("NEW_YD_PNT_CD"      , sNewYdPntCd   );
			    jrParam.setField("YD_CAR_WRK_GP"      , ydCarWrkGp    );
			    jrParam.setField("YD_EQP_WRK_STAT"    , ydEqpWrkStat  );
				
			    jrParam.setField("YD_MAKECARPNT_CD"   , sNewYdCarpntCd);
				
				//------------------------------------------------------------------------------------------------------------
			    //	도착포인트 동에 야드 적치 여부 체크
			    //------------------------------------------------------------------------------------------------------------
			    /*
				SELECT A.*
				  FROM TB_YD_CARSCH A
				     , TB_YD_STOCK  B
				     , TB_PT_COILCOMM C
				 WHERE A.DEL_YN = 'N'
				   AND A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
				   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
				   AND B.STL_NO    = C.COIL_NO
				   AND C.YD_BAY_GP = SUBSTR(A.YD_CARLD_STOP_LOC,2,1)
				   AND A.YD_CAR_PROG_STAT = '1'
				   AND C.YD_EQP_GP BETWEEN '00' AND '99'  --//야드에 적치
				   AND A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				   AND SUBSTR(A.YD_CARLD_STOP_LOC,1,1) IN('H','J')
				 UNION ALL
				SELECT A.*
				  FROM TB_YD_CARSCH     A
				     , TB_YM_STOCK      B
				     , TB_YM_STACKLAYER C
				 WHERE A.DEL_YN = 'N'
				   AND A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE2
				   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO2
				   AND B.STOCK_ID = C.STOCK_ID
				   AND C.STACK_LAYER_STAT IN('L','C','U')
				   AND SUBSTR(C.STACK_COL_GP,2,1) = SUBSTR(A.YD_CARLD_STOP_LOC,2,1)
				   AND A.YD_CAR_PROG_STAT = '1'
				   AND SUBSTR(C.STACK_COL_GP,3,2) BETWEEN '00' AND '99'  --//야드에 적치
				   AND A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				   AND SUBSTR(A.YD_CARLD_STOP_LOC,1,1) IN('3') 
				 UNION ALL
				SELECT A.*
				  FROM TB_YD_CARSCH A
				     , TB_YF_STOCK  B
				     , TB_YF_STKLYR C
				 WHERE A.DEL_YN = 'N'
				   AND A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
				   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
				   AND B.STL_NO = C.STL_NO
				   AND C.YD_STK_LYR_STAT IN('L','C','U')
				   AND SUBSTR(C.YD_STK_COL_GP,2,1) = SUBSTR(A.YD_CARLD_STOP_LOC,2,1)
				   AND A.YD_CAR_PROG_STAT = '1'
				   AND SUBSTR(C.YD_STK_COL_GP,3,2) BETWEEN '00' AND '99'  --//야드에 적치
				   AND A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				   AND SUBSTR(A.YD_CARLD_STOP_LOC,1,1) IN('1') 
				 UNION ALL
				SELECT A.*
				  FROM USRYDA.TB_YD_CARSCH A
				 WHERE A.YD_CAR_SCH_ID  = :V_YD_CAR_SCH_ID
				   AND A.TRANS_ORD_SEQNO >= '800000'
			     */
//PIDEV_S :병행가동용:PI_YD
			    jrParam.setField("PI_YD",    	sPI_YD);
			    jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCarStlYardInfoChk_PIDEV", logId, mthdNm, "차량스케줄 조회");
			    
			    if (jsCarSch.size() <= 0 && "U".equals(ydEqpWrkStat)) {
					sMsg = "TB_YD_CARSCH[해당 포인트 ["+sOldYdCarpntCd+"] 상차가능 대상이 존재하지 않습니다..]";
					commUtils.printLog(logId, sMsg, "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", sMsg);
					commUtils.printLog(logId, mthdNm, "S-");
					return jrRtn;
				}
			    
			    //------------------------------------------------------------------------------------------------------------
			    //	저장위치 맵 활성화
			    //------------------------------------------------------------------------------------------------------------
			    
			    //차량 POINT TABLE 점유
			    coilDao.procUpdYdTransOrdChangeNEW(jrParam); //new
			    
			    //차량 스케줄 POINT 변경 		
			    coilDao.procUpdYdTransOrdChange(jrParam);
			    
			    jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			    jrParam.setField("YD_STK_COL_GP"      , sNewYdStkColGp   );
			    jrParam.setField("CAR_NO"             , sNewCarNo        );
			    jrParam.setField("CARD_NO"            , sNewCardNo       );
			    jrParam.setField("TRN_EQP_CD"         , sNewTrnEqpCd     );         
			    jrParam.setField("YD_GP"              , ydGp             );
			    jrParam.setField("TRANS_WORD_DATE"	  , sNewTransOrdDate );
			    jrParam.setField("TRANS_WORD_SEQNO"   , sNewTransOrdSeqno);
			    
			    //야드맵 활성화
			    coilDao.procYdLayerOpen(jrParam); //new
			    
			    String ydWbookId = ""; 
		    	String ydEqpId   = "";
		    	String ydSchCd   = "";
		    	
			    if ("9".equals(ydCarWrkGp) && "TR".equals(sNewCarKind)) {
			    	//2냉연 TR은 크레인 작업예약은 도착전문을 받고서 처리 한다
			    	commUtils.printLog(logId, "ydCarWrkGp, sNewCarKind = "+ ydCarWrkGp + sNewCarKind, "SL");
			    } else {
			    	//------------------------------------------------------------------------------------------------------------
			    	//	크레인 작업예약 생성
			    	//------------------------------------------------------------------------------------------------------------
				    jrParam.setField("YD_BAY_GP"          , sNewYdCarpntCd.substring(2, 3));
				    jrParam.setField("YD_CARPNT_CD"       , sNewYdCarpntCd);
				    jrParam.setField("CAR_KIND"           , sCarKind);
					jrParam.setField("TRANS_FRTOMOVE_GP"  , sTransFrtomoveGp);
					//PIDEV_S :병행가동용:PI_YD
					jrParam.setField("PI_YD",    	sPI_YD);		
					JDTORecord jrWbook = this.procYdWbookInsert(jrParam);
					
					ydWbookId = commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));
					ydEqpId   = commUtils.trim(jrWbook.getFieldString("YD_EQP_ID"  ));
					ydSchCd   = commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"  ));
		
					//------------------------------------------------------------------------------------------------------------
			    	//	차량스케줄 도착상태 변경 처리
			    	//------------------------------------------------------------------------------------------------------------
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("YD_CAR_SCH_ID"       , sNewYdCarSchId);
					
					if ("U".equals(ydEqpWrkStat)) { //상차(공차)
						jrParam.setField("YD_CARLD_WRK_BOOK_ID", ydWbookId);
						jrParam.setField("YD_CARLD_ARR_DT"     , sCurrDate);
					} else {
						jrParam.setField("YD_CARUD_WRK_BOOK_ID", ydWbookId);
						jrParam.setField("YD_CARUD_ARR_DT"     , sCurrDate);
					}
					
					
					if ("P".equals(sTransEquipmentType) && "9".equals(ydCarWrkGp)) {
						jrParam.setField("YD_CAR_PROG_STAT", "B"); //하차도착상태
					} else {
						
						int iTranOrdSeqno = Integer.parseInt(sNewTransOrdSeqno);
						
						if (iTranOrdSeqno > 999000) {
							//반품, 회송, 부분하차
							jrParam.setField("YD_CAR_PROG_STAT"    , "B"      ); //하차도착상태
							jrParam.setField("YD_CARUD_WRK_BOOK_ID", ydWbookId); //야드하차작업예약ID
							jrParam.setField("YD_CARLD_WRK_BOOK_ID", ""       ); //상단에 설정한 상차작업예약ID clear
							jrParam.setField("YD_CARUD_ARR_DT"     , sCurrDate); //야드하차도착일시
						} else {
							jrParam.setField("YD_CAR_PROG_STAT"    , "2"); //상차도착상태
						}
					}
					
					jrParam.setField("SPOS_WLOC_CD", sNewWlocCd); //발지개소코드
					
					/*
					UPDATE TB_YD_CARSCH
					   SET MODIFIER      = :V_MODIFIER
					     , MOD_DDTT      = SYSDATE
					     , YD_CARLD_ARR_DT      = NVL(:V_YD_CARLD_ARR_DT      , YD_CARLD_ARR_DT     )
					     , YD_CARUD_ARR_DT      = NVL(:V_YD_CARUD_ARR_DT      , YD_CARUD_ARR_DT     )
					     , SPOS_WLOC_CD         = NVL(:V_SPOS_WLOC_CD         , SPOS_WLOC_CD        )
					     , YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_CARLD_WRK_BOOK_ID , YD_CARLD_WRK_BOOK_ID)
					     , YD_CARUD_WRK_BOOK_ID = NVL(:V_YD_CARUD_WRK_BOOK_ID , YD_CARUD_WRK_BOOK_ID)
					     , YD_CAR_PROG_STAT     = NVL(:V_YD_CAR_PROG_STAT     , YD_CAR_PROG_STAT    )  
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID   
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarschArrStatWb", logId, mthdNm, "차량스케줄 수정");
			    }
			    
			    
			    if ("9".equals(ydCarWrkGp) && "TR".equals(sNewCarKind)) {
			    	sChkYn = "H";	
			    } else {
			    	
			    	/**********************************************************
					 * 차량작업 예정정보 송신 YDY5L008
					 **********************************************************/
			    	JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
			    	sndL2Msg.setField("JMS_TC_CD"       , "YDY5L008");
					sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
					sndL2Msg.setField("SEARCH_FLAG"     , "2"       ); //1:상차도, 2:차량스케쥴 ID
					sndL2Msg.setField("YD_CAR_SCH_ID"   , sNewYdCarSchId); //차량스케줄
						
					jrRtn = commUtils.addSndData(jrRtn, coilDao.procCarPlanInfo(sndL2Msg));	 //전송 Data 생성
					
					/**********************************************************
					 * 차량형상이 없을 때 스케줄 기동
					 **********************************************************/
					/* 
					SELECT NVL(YD_FRM_YN,'N') AS YD_FRM_YN
					  FROM USRYDA.TB_YD_CARPOINT
					 WHERE 1=1 
					   AND YD_STK_COL_GP = :V_YD_STK_COL_GP 
					   AND YD_GP  = 'J'
					   AND DEL_YN = 'N'
		            */
					jrParam.setField("YD_STK_COL_GP", sNewYdStkColGp);
					JDTORecordSet jsYdFrmYn = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCarpointByFrmYn", logId, mthdNm, "형상 비형상 판단");

					String ydFrmYn = commUtils.nvl(jsYdFrmYn.getRecord(0).getFieldString("YD_FRM_YN"), "N"); //차량형상여부

					commUtils.printLog(logId, "차량형상 존재여부 : " + ydFrmYn, "SL");
					
					/*
					 * 차량형상이 없거나 상차(공차)일경우 스케줄 기동
					 */
					if ("N".equals(ydFrmYn) || "U".equals(ydEqpWrkStat)) { 
						
						String sApp832 = coilDao.ApplyYn(logId, mthdNm, "APP832", "J", "*"); //출하 대상코일 2단코일 스케줄 삭제
						
						jrParam.setField("TRANS_ORD_DATE"	 , sNewTransOrdDate );
					    jrParam.setField("TRANS_ORD_SEQNO"   , sNewTransOrdSeqno);
					    
					    if ("Y".equals(sApp832)) {
					    	/*
							SELECT CM.STL_NO
							     , CS.*
							  FROM (
							        SELECT CASE WHEN A.YD_STK_LYR_NO = '001' THEN NVL((SELECT STL_NO FROM TB_YD_STKLYR L
							                                                            WHERE L.YD_STK_COL_GP = A.YD_STK_COL_GP
							                                                              AND L.YD_STK_BED_NO = A.LEFT_BED
							                                                              AND L.YD_STK_LYR_NO = '002'), '') 
							                    ELSE '' END AS L_STL_NO
							             , CASE WHEN A.YD_STK_LYR_NO = '001' THEN NVL((SELECT STL_NO FROM TB_YD_STKLYR L
							                                                            WHERE L.YD_STK_COL_GP = A.YD_STK_COL_GP
							                                                              AND L.YD_STK_BED_NO = A.RIGHT_BED
							                                                              AND L.YD_STK_LYR_NO = '002'), '') 
							                    ELSE '' END AS R_STL_NO
							             , A.*
							          FROM (
							                SELECT DECODE(SL.YD_STK_LYR_NO, '001', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) - SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'),
							                                                '002', SL.YD_STK_BED_NO)                               AS LEFT_BED
							                     , DECODE(SL.YD_STK_LYR_NO, '001', SL.YD_STK_BED_NO,
							                                                '002', LPAD(TO_NUMBER(SL.YD_STK_BED_NO) + SF_YD_SKID_INTERVAL_GAP(SL.YD_STK_COL_GP), 2, '0'))
							                                                                                                       AS RIGHT_BED
							                     , SL.*
							                  FROM TB_YD_STOCK   ST
							                     , TB_YD_STKLYR  SL
							                 WHERE ST.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
							                   AND ST.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
							                   AND ST.STL_NO = SL.STL_NO
							               ) A
							         WHERE 1 = 1       
							       ) B
							     , TB_YD_CRNSCH    CS
							     , TB_YD_CRNWRKMTL CM
							 WHERE 1 = 1       
							   AND CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
							   AND CM.STL_NO IN(B.L_STL_NO, B.R_STL_NO)
							   AND  (CS.YD_SCH_CD LIKE 'J_YD04MM'         --공냉재 자동입고
							     OR  CS.YD_SCH_CD LIKE 'J_YD05MH')        --공냉재 자동이적
					    	 */
					    	JDTORecordSet js2SchList = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.get2DanAriClSchList", logId, mthdNm, "2단스케줄 조회");

					    	String ydWrkProgStat = "";
					    	String ydCrnSchId2   = "";
					    	
					    	if (js2SchList.size() > 0) {
					    		for (int i = 0; i < js2SchList.size(); ++i) {
					    			
						    		ydWrkProgStat = js2SchList.getRecord(i).getFieldString("YD_WRK_PROG_STAT");
						    		ydCrnSchId2   = js2SchList.getRecord(i).getFieldString("YD_CRN_SCH_ID"   );
						    		
						    		if ("W".equals(ydWrkProgStat)) {
					    				// 크레인 스케줄 삭제
					    				jrParam.setField("YD_CRN_SCH_ID"    , ydCrnSchId2);
										jrParam.setField("YD_L2_RETURN_FLAG", "Y");
					    				
										/**********************************************************
										* 크레인스케줄 취소
										**********************************************************/
										EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
										JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					    			}
						    	} //end for
					    	}
					    } //app832
					    
						/**********************************************************
						 * 크레인 스케줄 기동
						 **********************************************************/
						jrParam.setField("TRANS_ORD_DATE"	 , sNewTransOrdDate );
					    jrParam.setField("TRANS_ORD_SEQNO"   , sNewTransOrdSeqno);
						/*
						SELECT WB.YD_WBOOK_ID
						     , SL.YD_STK_COL_GP
						     , SL.YD_STK_BED_NO
						     , SL.YD_STK_LYR_NO
						     , SL.STL_NO
						  FROM TB_YD_WRKBOOK    WB
						     , TB_YD_WRKBOOKMTL WM
						     , TB_YD_STOCK      ST
						     , TB_YD_STKLYR     SL
						 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
						   AND WM.STL_NO      = ST.STL_NO
						   AND ST.STL_NO      = SL.STL_NO
						   AND WB.DEL_YN = 'N'
						   AND WM.DEL_YN = 'N'
						   AND ST.DEL_YN = 'N'
						   AND  ((ST.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE AND ST.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO)
						       OR ST.CAR_FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO)
						   --결속장작업시 공냉재이송(1통로) 작업안함
						   AND 'Y' = (CASE WHEN WB.YD_SCH_CD IN ('JBPT21UM', 'JDPT21UM') 
						                    AND (SELECT MATL_SUP_MTD_GP 
						                           FROM TB_YD_STKCOL
						                          WHERE YD_STK_COL_GP LIKE 'J'||WB.YD_BAY_GP||'FE%') = 'Y'
						                   THEN 'N' --스케줄 기동안함
						                   ELSE 'Y' 
						               END)						       
						 ORDER BY SL.YD_STK_LYR_NO DESC 
						        , WB.YD_WBOOK_ID
						 */
						JDTORecordSet jsCarLdSeq = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getCarLoadSeq", logId, mthdNm, "스케줄기동 순서");

						if (jsCarLdSeq.size() > 0 ) {
		    				
		    				JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier); 
							jrYdMsg.setField("JMS_TC_CD"		 , "YDYDJ552");  //
							jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
							jrYdMsg.setField("YD_SCH_CD"         , ""); //야드스케쥴코드
							jrYdMsg.setField("YD_EQP_ID"         , ""); //야드설비ID
							
							for (int i = 1; i <= jsCarLdSeq.size(); i++) {
								jsCarLdSeq.absolute(i);
								jrYdMsg.setField("YD_WBOOK_ID" + i, jsCarLdSeq.getRecord().getFieldString("YD_WBOOK_ID")); //야드작업예약ID
								jrYdMsg.setField("SCH_CNT"        , "" + i); //작업예약 개수
							}
							
							EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this);
							JDTORecord jrCrnSchMsg = (JDTORecord)ejbConn.trx("procYDYDJ552", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
							
							jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
							commUtils.printLog(logId, "크레인스케줄 기동", "SL");
		    			}							
						//---------------------------
					} //if ("N".equals(ydFrmYn)) {
			    	
			    	sChkYn = "Y";
			    }
				
	    		/**********************************************************
				 * 저장위치제원 송신 YDY5L001
				 **********************************************************/
			    commUtils.printLog(logId, "저장위치제원송신 YDY5L001", "SL");
			    JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
	    		sndL2Msg.setField("YD_INFO_SYNC_CD" , "4"); // 1:동,2:SPAN,3:열,4:BED
		    	sndL2Msg.setField("YD_GP"           , ydGp);
		    	sndL2Msg.setField("YD_STK_COL_GP"   , sNewYdStkColGp);
		    	sndL2Msg.setField("YD_STK_BED_NO"	, "01"          );
		    	if ("P".equals(sTransEquipmentType) && "9".equals(ydCarWrkGp)) {
		    		sndL2Msg.setField("YD_CAR_PROG_STAT", "B"); //하차도착상태
		    	} else {
		    		sndL2Msg.setField("YD_CAR_PROG_STAT", "2"); //상차도착상태
		    	}
		    	sndL2Msg.setField("YD_EQP_WRK_STAT" , "U");

				jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L001", sndL2Msg));
	    		
	    		/**********************************************************
				 * 입동지시 송신 YDDMR028 / YDDMR070
				 **********************************************************/
				commUtils.printLog(logId, "sChkYn : " + sChkYn, "SL");
				if ("Y".equals(sChkYn) || "H".equals(sChkYn)){
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("TRANS_ORD_DATE"      , sNewTransOrdDate );
					jrParam.setField("TRANS_ORD_SEQNO"     , sNewTransOrdSeqno);
					
// PIDEV
//					if("Y".equals(sApplyYnPI)) {
				    
						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create(); 
						jrYdMsg.setField("MQ_TC_CD"		        , "M10YDLMJ1061"    ); 
						jrYdMsg.setField("MQ_TC_CREATE_DDTT"    , sCurrDate     	); //JMSTC생성일시
					
						if ("P".equals(sTransEquipmentType)) {
							jrYdMsg.setField("SCH_YN"       , "Y");
						} else {
							jrYdMsg.setField("SCH_YN"       , "N");
						}
						
						jrYdMsg.setField("TRN_REQ_DATE"         , sNewTransOrdDate  ); // 운송지시일자
					    jrYdMsg.setField("TRN_REQ_SEQ"    	    , sNewTransOrdSeqno ); // 운송의뢰순번
					    
					    jrYdMsg.setField("CAR_NO"               , sNewCarNo         ); // 차량번호
					    jrYdMsg.setField("YD_GP"                , ydGp	       		); // 야드구분
					    jrYdMsg.setField("DIST_GOODS_GP"        , "H"	       		); // 출하제품구분
					    
					    jrYdMsg.setField("BAYIN_DDTT"           , sCurrDate     		); // 입동일시
					    jrYdMsg.setField("WLOC_CD"       	    , sNewWlocCd    		); // 개소코드 
					    jrYdMsg.setField("YD_PNT_CD"            , ""            		); // 야드포인트코드
					    jrYdMsg.setField("LOAN_PULLOUT_ABLE_YN" ,"Y"            		); // 차입인출가능여부
					    jrYdMsg.setField("YD_CARPNT_CD"         , sNewYdCarpntCd   ); 
					    
					    jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
					    
//					} else {
//						
//						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//						if ("P".equals(sTransEquipmentType)) {
//							jrYdMsg.setField("JMS_TC_CD"           , "YDDMR070");
//							jrYdMsg.setField("TC_CODE"			   , "YDDMR070");
//							/*
//							SELECT  *
//							  FROM TB_YD_STOCK
//							 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
//							   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
//							 */
//							JDTORecordSet jsStockLyr = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockTransOrdDate", logId, mthdNm, "재료 조회");
//							
//							if (jsStockLyr.size() > 0) {
//								jrYdMsg.setField("CR_FRTOMOVE_GP", commUtils.trim(jsStockLyr.getRecord(0).getFieldString("CR_FRTOMOVE_GP")));
//							} else {
//								jrYdMsg.setField("CR_FRTOMOVE_GP",  "");
//							}
//						} else {
//							jrYdMsg.setField("JMS_TC_CD"            , "YDDMR028"  );
//							jrYdMsg.setField("TC_CODE"			    , "YDDMR028"  );
//							jrYdMsg.setField("WLOC_CD"              , sNewWlocCd  );
//						    jrYdMsg.setField("YD_PNT_CD"            , sNewYdPntCd );
//						}
//						
//						jrYdMsg.setField("TC_CREATE_DDTT"		, sCurrDate        );
//						jrYdMsg.setField("JMS_TC_CREATE_DDTT"   , sCurrDate        ); //JMSTC생성일시
//					    jrYdMsg.setField("TRANS_WORD_DATE"      , sNewTransOrdDate );
//					    jrYdMsg.setField("TRANS_WORD_SEQNO"     , sNewTransOrdSeqno);
//					    jrYdMsg.setField("CARD_NO"              , sNewCardNo       );
//					    jrYdMsg.setField("CAR_NO"               , sNewCarNo        );
//					    jrYdMsg.setField("YD_CARPNT_CD"         , sNewYdCarpntCd   ); 
//					    jrYdMsg.setField("LOAN_PULLOUT_ABLE_YN" ,"Y"               );
//					    jrYdMsg.setField("BAYIN_DDTT"           , sCurrDate        ); //입동일시
//					    
//					    jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
//
//
//					} 
				    
				    jrParam.setField("YD_CAR_RCPT_CHK_YN"   ,"E"               ); //입동지시 송신여부
				    jrParam.setField("CARD_NO"              , sNewCardNo       );
				    jrParam.setField("CAR_NO"               , sNewCarNo        );
				    /*
					UPDATE TB_YD_CARSCH 
					   SET MODIFIER           = :V_MODIFIER
					     , MOD_DDTT           = SYSDATE
					     , YD_CAR_RCPT_CHK_YN = :V_YD_CAR_RCPT_CHK_YN
					     , YD_CARLD_PNT_WO_DT = CASE WHEN YD_EQP_WRK_STAT = 'L' THEN SYSDATE ELSE YD_CARLD_PNT_WO_DT END 
					     , YD_CARUD_PNT_WO_DT = CASE WHEN YD_EQP_WRK_STAT = 'U' THEN SYSDATE ELSE YD_CARUD_PNT_WO_DT END 
					 WHERE CAR_NO            = :V_CAR_NO
					   AND CARD_NO           = :V_CARD_NO
				     */
//				    if("Y".equals(sApplyYnPI)) {
				    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarschYdCarRcptChkYn_PIDEV", logId, mthdNm, "입동지시 송신여부");
//				    } else {
//				    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updYdCarschYdCarRcptChkYn", logId, mthdNm, "입동지시 송신여부");
//				    }
				    
				    commUtils.printLog(logId, mthdNm, "S-");
				    jrRtn.setField("RTN_CD"	, "2"); // 입동지시 전문 송신 : 2
				    return jrRtn;
				}
			}
			
			commUtils.printLog(logId, mthdNm, "S-");

			jrRtn.setField("RTN_CD"	, "1"); //입동지시 전문 미송신 : 1
			return jrRtn;
	
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	
	}
	
	
	/**
	 * 오퍼레이션명 : YD 작업예약생성
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
    public JDTORecord procYdWbookInsert(JDTORecord rcvMsg)throws DAOException  {
		String mthdNm = "작업예약생성[CCoilCarMvSeEJB.procYdWbookInsert] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			String sMsg             		= "";

			int intTRANS_ORD_SEQNO		    = 0;
			
            String sCarNo           = commUtils.trim(rcvMsg.getFieldString("CAR_NO"            ));
            String sCardNo          = commUtils.trim(rcvMsg.getFieldString("CARD_NO"           ));    
            String ydBayGp          = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"         ));    
            String ydGp             = commUtils.trim(rcvMsg.getFieldString("YD_GP"             ));   
            String sTransOrdDate    = commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_DATE"   ));
            String sTransOrdSeqno   = commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_SEQNO"  ));// 야드구분
            String ydCarpntCd       = commUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD"      )); 
            String sTransFrtomoveGp = commUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP" )); //1 운송 2 이송
		 	
		 	String sModifier        = commUtils.trim(rcvMsg.getFieldString("MODIFIER"          ));//수정자(Backup Only)
//PIDEV_S :병행가동용:PI_YD
			String sPI_YD     = commUtils.nvl(rcvMsg.getFieldString("PI_YD"),"*");		 	
		 	JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		 	
		 	JDTORecordSet jsResult  = JDTORecordFactory.getInstance().createRecordSet("");
		 	
		 	
		 	/**********************************************************
			* 위치구분조회
			**********************************************************/
		 	jrParam.setField("YD_CARPNT_CD", ydCarpntCd);
		 	/*
			SELECT SC.YD_LOC_GP
			     , SC.YD_STK_COL_GP
			  FROM TB_YD_CARPOINT  CP
			     , TB_YD_STKCOL    SC
			 WHERE CP.YD_STK_COL_GP = SC.YD_STK_COL_GP
			   AND CP.DEL_YN = 'N'
			   AND SC.DEL_YN = 'N'
			   AND CP.YD_CARPNT_CD = :V_YD_CARPNT_CD
		 	 */
		 	JDTORecordSet jsLocGp = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdLocGpBycarpointCd", logId, mthdNm, "위치구분 조회");
		 	String ydLocGp = "J";
		 	if (jsLocGp.size() > 0) {
		 		ydLocGp = commUtils.nvl(jsLocGp.getRecord(0).getFieldString("YD_LOC_GP"), "J");
		 	}
		 	
		 	/**********************************************************
			* 스케줄 코드 생성
			**********************************************************/
		 	String ydSchCd   = "";
		 	intTRANS_ORD_SEQNO = StringHelper.parseInt(sTransOrdSeqno, 0);
		 	
		 	jrParam.setField("TRANS_ORD_DATE"   , sTransOrdDate  );
		 	jrParam.setField("TRANS_ORD_SEQNO"  , sTransOrdSeqno  );
		 	jrParam.setField("TRANS_FRTOMOVE_GP", sTransFrtomoveGp);
		 	
		 	/* com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdSchCdDm
			--61
			WITH PARAM AS (
			SELECT P_TRANS_ORD_SEQNO
			     , SUBSTR(P_YD_CARPNT_CD, 2, 1) AS P_PASS_GP --통로
			     , SUBSTR(P_YD_CARPNT_CD, 3, 1) AS P_BAY_GP
			     , P_TRANS_FRTOMOVE_GP
			     , (SELECT CC.CURR_PROG_CD 
			          FROM TB_PT_COILCOMM  CC
			             , TB_YD_STOCK     ST
			         WHERE CC.COIL_NO = ST.STL_NO
			           AND CC.YD_GP = 'J'
			           AND ST.TRANS_ORD_DATE  = P_TRANS_ORD_DATE
			           AND ST.TRANS_ORD_SEQNO = P_TRANS_ORD_SEQNO
			           AND ROWNUM = 1
			        ) AS P_CURR_PROG_CD 
			  FROM (     
			        SELECT :V_TRANS_ORD_SEQNO   AS P_TRANS_ORD_SEQNO
			             , :V_YD_CARPNT_CD      AS P_YD_CARPNT_CD  
			             , :V_TRANS_FRTOMOVE_GP AS P_TRANS_FRTOMOVE_GP
			             , :V_TRANS_ORD_DATE    AS P_TRANS_ORD_DATE
			          FROM DUAL
			       )
			)
			SELECT CASE WHEN P_TRANS_ORD_SEQNO > 999000  AND P_PASS_GP = '3' THEN 'J'||P_BAY_GP||'PT43LH' --반품회송 소재통로는 소재스케줄코드
			            WHEN P_TRANS_ORD_SEQNO > 999000   THEN 'J'||P_BAY_GP||'PT4'||P_PASS_GP||'LM' --반품회송
			            WHEN P_TRANS_ORD_SEQNO >= 800000  THEN 'J'||P_BAY_GP||'PT3'||P_PASS_GP||'UM' --제품이송
			            WHEN P_TRANS_ORD_SEQNO BETWEEN 700000 AND 800000 --20210319 이송출고 
			              OR P_CURR_PROG_CD IN ('2','3','4','5','6','7','8','9') --20210317 허동수매니저 숫자진도코드는 전부 이송출고
			            THEN 'J'||P_BAY_GP||'PT0'||P_PASS_GP||'UM' --이송출고
			            ELSE 'J'||P_BAY_GP||'TR0'||P_PASS_GP||'UM' --
			        END AS YD_SCH_CD
			  FROM PARAM
		 	 */
//PIDEV_S :병행가동용:PI_YD
		 	jrParam.setField("PI_YD",    	sPI_YD);			 	
		 	JDTORecordSet jsYdSchCd = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdSchCdDm_PIDEV", logId, mthdNm, "스케줄코드 생성");
		 	if (jsYdSchCd.size() <= 0) {
		 		sMsg = "스케줄코드 생성 실패";
				commUtils.printLog(logId, sMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sMsg);	
				return jrRtn;
		 	}
		 	
		 	ydSchCd = jsYdSchCd.getRecord(0).getFieldString("YD_SCH_CD");
		 	
			/**********************************************************
			* 스케줄코드 Check
			**********************************************************/
			jrParam.setField("YD_SCH_CD", ydSchCd);
			/*
			SELECT YD_SCH_CD
			     , DEL_YN
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
			  FROM TB_YD_SCHRULE
			 WHERE 1 = 1 
			   AND YD_GP IN ('H','J')
			   AND YD_SCH_CD = :V_YD_SCH_CD
			   AND DEL_YN    = 'N'
			 */
			jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdSchRule", logId, mthdNm, "스케줄기준 조회");
			
			if (jsResult.size() <= 0) {
				sMsg = "스케줄코드(" + ydSchCd + ")에 대한 스케줄기준 데이터가 이상합니다.";
				commUtils.printLog(logId, sMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sMsg);	
				return jrRtn;
			}
			
			jsResult.absolute(1);
			
			String ydSchProhExn	  	= commUtils.trim(jsResult.getRecord().getFieldString("YD_SCH_PROH_EXN" )); //스케줄 금지 유무
			String ydWrkCrn       	= commUtils.trim(jsResult.getRecord().getFieldString("YD_WRK_CRN"      )); //작업크레인
			String ydWrkCrnPrior 	= commUtils.trim(jsResult.getRecord().getFieldString("YD_WRK_CRN_PRIOR")); //작업크레인우선순위
			String ydAltCrnYn    	= commUtils.trim(jsResult.getRecord().getFieldString("YD_ALT_CRN_YN"   )); //대체크레인유무
			String ydAltCrn       	= commUtils.trim(jsResult.getRecord().getFieldString("YD_ALT_CRN"      )); //대체크레인
			String ydAltCrnPrior	= commUtils.trim(jsResult.getRecord().getFieldString("YD_ALT_CRN_PRIOR")); //대체크레인우선순위
						
			//스케줄 금지 유무가 "Y"이면 처리를 중지
			if ("Y".equals(ydSchProhExn)) {
				sMsg = "스케줄 금지 유무가 [Y] 입니다";
				commUtils.printLog(logId, sMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sMsg);
				return jrRtn;
			}

			
			//작업크레인 설비 상태 체크
			boolean blnRtnVal = coilDao.chkEqpStat(logId, mthdNm, ydWrkCrn);
			
			commUtils.printLog(logId, "blnRtnVal:" + blnRtnVal, "SL");
			
			String sWrkCrn    = "";
		 	String ydSchPrior = "";
		 	
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {
				
				sMsg = "작업크레인(" + ydWrkCrn + ")이 사용 불가 상태입니다.";
				commUtils.printLog(logId, sMsg, "SL");
				
				//대체크레인의 유무 체크
				if (!"Y".equals(ydAltCrnYn)) {
					sMsg = "대체크레인유무(" + ydAltCrnYn + "), 대체크레인이 없습니다.";
					commUtils.printLog(logId, sMsg, "SL");
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", sMsg);
					return jrRtn;
				}
				
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = coilDao.chkEqpStat(logId, mthdNm, ydAltCrn);
				
				//대체크레인 사용여부
				if (!blnRtnVal) {
					sMsg = "대체크레인(" + ydAltCrn + ")이 사용 불가 상태입니다.";
					commUtils.printLog(logId, sMsg, "SL");
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", sMsg);	
					return jrRtn;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					sWrkCrn    = ydAltCrn;
					ydSchPrior = ydAltCrnPrior;
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				sWrkCrn    = ydWrkCrn;
				ydSchPrior = ydWrkCrnPrior;
			}
			
			/**********************************************************
			* 작업예약 존재 여부 CHECK
			**********************************************************/
			jrParam.setField("TRANS_ORD_DATE" , sTransOrdDate);
		 	jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
		 	
			/*
			SELECT A.STL_NO
			     , B.YD_WBOOK_ID
			 FROM TB_YD_STOCK      A
			    , TB_YD_WRKBOOKMTL B
			    , TB_YD_STKLYR     SL
			WHERE A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
			  AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			  AND A.STL_NO = B.STL_NO
			  AND B.DEL_YN = 'N'
			  --복수상차 트랜잭션 문제
			  AND A.STL_NO = SL.STL_NO
			  AND SL.YD_STK_LYR_MTL_STAT = 'C'
			  AND SUBSTR(SL.YD_STK_COL_GP, 3, 2) != 'CR'
		 	  */
		 	jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStockWbookcheck", logId, mthdNm, "작업예약 조회");
		 	if (jsResult.size() > 0) {
				sMsg = "이미 다른 작업예약이 존재 합니다  .!! ";
				commUtils.printLog(logId, sMsg, "SL");
				 
				for (int i = 0; i < jsResult.size(); i++) {

					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("YD_WBOOK_ID" , commUtils.trim(jsResult.getRecord(i).getFieldString("YD_WBOOK_ID")));
					
					//크레인 작업예약 삭제
					EJBConnector ejbConn = new EJBConnector("default" , "CCoilJspSeEJB" , this);
					JDTORecord jrWbookCncl = (JDTORecord)ejbConn.trx("trtWrkBookCncl" , new Class[]{JDTORecord.class} , new Object[]{ jrParam });
					
					jrRtn = commUtils.addSndData(jrRtn, jrWbookCncl);
				}
			}
			
			
		 	/**********************************************************
			* 작업예약 대상 조회
			**********************************************************/
		 	jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRANS_ORD_DATE" ,	sTransOrdDate );
			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
			jrParam.setField("YD_BAY_GP"      , ydBayGp       );
			jrParam.setField("YD_GP"          , ydGp          );
			jrParam.setField("YD_CARPNT_CD"   , ydCarpntCd    );
			jrParam.setField("CAR_NO"         , sCarNo        );
			
			String ydAimYdGp  = "";
			String ydAimBayGp = "";  
			
			if (intTRANS_ORD_SEQNO > 999000) { 
				//반품,회송,부분하차 일경우
				//차량 하차 위치(STKLYR)에 제품을 적치시킨다.
				/*
				MERGE INTO TB_YD_STKLYR TG USING (
				SELECT A.YD_CARUD_STOP_LOC AS YD_STK_COL_GP
				     , B.YD_STK_BED_NO
				     , '001' AS YD_STK_LYR_NO
				     , B.STL_NO
				  FROM TB_YD_CARSCH     A
				     , TB_YD_CARFTMVMTL B
				 WHERE A.CAR_NO          = :V_CAR_NO
				   AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
				   AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				   AND A.DEL_YN = 'N'
				   AND B.DEL_YN = 'N'
				   AND A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				) DD ON (TG.YD_STK_COL_GP = DD.YD_STK_COL_GP AND TG.YD_STK_BED_NO = DD.YD_STK_BED_NO AND TG.YD_STK_LYR_NO = DD.YD_STK_LYR_NO)
				WHEN MATCHED THEN UPDATE SET
				    TG.STL_NO              = DD.STL_NO
				   ,TG.YD_STK_LYR_MTL_STAT = 'C'
				   ,TG.MODIFIER            = :V_MODIFIER
				   ,TG.MOD_DDTT            = SYSDATE
				  
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.updCarftmvmtlToLyr", logId, mthdNm, "");

				ydAimYdGp	= ydGp;
				ydAimBayGp	= ydBayGp;
				
				//작업예약재료를 생성하기 위해 jsResult 에 하차 대상 재료를 조회한다.
				/*
				SELECT A.YD_CARUD_STOP_LOC AS YD_STK_COL_GP
				     , B.YD_STK_BED_NO
				     , '001' AS YD_STK_LYR_NO
				     , B.STL_NO
				  FROM TB_YD_CARSCH A
				     , TB_YD_CARFTMVMTL B
				 WHERE A.CAR_NO          = :V_CAR_NO
				   AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
				   AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				   AND A.DEL_YN          = 'N'
				   AND B.DEL_YN          = 'N'
				   AND A.YD_CAR_SCH_ID   = B.YD_CAR_SCH_ID
				 ORDER BY B.YD_STK_BED_NO
				 */
				jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getCarftmvmtl", logId, mthdNm, "하차대상재 조회");
				
			} else {
				
				/*
				WITH TEMP_TABLE AS (
				SELECT A.STL_NO
				     , SUBSTR(B.YD_STK_COL_GP,2,1) AS YD_BAY_GP
				     , A.YD_AIM_YD_GP
				     , A.YD_AIM_BAY_GP 
				     , B.YD_STK_COL_GP 
				     , B.YD_STK_BED_NO 
				     , B.YD_STK_LYR_NO
				     , A.TRANS_ORD_SEQNO
				     , A.YD_CAR_UPP_LOC_CD
				  FROM TB_YD_STOCK A
				     , TB_YD_STKLYR B
				     , TB_YD_CARPOINT C
				 WHERE A.STL_NO           = B.STL_NO
				   AND A.TRANS_ORD_DATE   = :V_TRANS_ORD_DATE
				   AND A.TRANS_ORD_SEQNO  = :V_TRANS_ORD_SEQNO
				   AND B.YD_STK_COL_GP LIKE :V_YD_GP ||:V_YD_BAY_GP ||'%'
				   AND C.YD_CARPNT_CD     = :V_YD_CARPNT_CD
				   AND ((SUBSTR(B.YD_STK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO )
				      OR SUBSTR(B.YD_STK_COL_GP,3,2) IN('80','01') )
				   AND B.DEL_YN = 'N'
                   AND C.DEL_YN = 'N'
				)
				SELECT *
				  FROM TEMP_TABLE A
				 WHERE (YD_STK_COL_GP,YD_STK_BED_NO,YD_STK_LYR_NO) = (SELECT YD_STK_COL_GP,YD_STK_BED_NO,YD_STK_LYR_NO
				                                                        FROM TEMP_TABLE B
				                                                       WHERE A.STL_NO = B.STL_NO 
				                                                         AND ROWNUM <= 1)
				  ORDER BY YD_STK_LYR_NO  DESC 
                         , YD_CAR_UPP_LOC_CD 
				 */
			 	jsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStockTransOrdDTWbook", logId, mthdNm, "");
			 	
			 	if (jsResult.size() <= 0) {
					sMsg = "운송지시 저장품대상이 없습니다.!! ";
					commUtils.printLog(logId, sMsg, "S-");
					throw new DAOException(sMsg);
				}
			 	
				ydAimYdGp	= commUtils.trim(jsResult.getRecord(0).getFieldString("YD_AIM_YD_GP"));
				ydAimBayGp	= commUtils.trim(jsResult.getRecord(0).getFieldString("YD_AIM_BAY_GP"));
			}
			

			/****************************
			 * 작업예약 생성
			 *****************************/
			String ydWbookId  = "";
			String ydWbookId1 = "";
			String sStlNo 	  = "";
			String ydStkColGp = "";
			String ydStkBedNo = "";
			String ydStkLyrNo = "";
			
			for (int j = 1; j <= jsResult.size(); j++) {

				ydWbookId = coilDao.getSeqId(logId, mthdNm, "WrkBook");
				
				if (j == 1) {
					ydWbookId1 = ydWbookId;
				}
				
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_WBOOK_ID"    , ydWbookId   );
				jrParam.setField("YD_GP"          , ydGp        );
				jrParam.setField("YD_BAY_GP"      , ydBayGp     );
				jrParam.setField("YD_SCH_CD"      , ydSchCd     );
				jrParam.setField("YD_SCH_PRIOR"   , ydSchPrior  );
				jrParam.setField("YD_CAR_USE_GP"  , "G"         );
				jrParam.setField("CAR_NO"         , sCarNo      );
				jrParam.setField("CARD_NO"        , sCardNo     );
				jrParam.setField("YD_AIM_YD_GP"   , ydAimYdGp   );
				jrParam.setField("YD_AIM_BAY_GP"  , ydAimBayGp  );
				
				// 작업예약 INSERT
				commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.insYdWrkbook", logId, mthdNm, "작업예약 생성(입동지시)");
				
				
			 	/**********************************************************
				* 작업예약재료 생성
				**********************************************************/
			
				jsResult.absolute(j);
				sStlNo 	   = commUtils.trim(jsResult.getRecord().getFieldString("STL_NO"));
				ydStkColGp = commUtils.trim(jsResult.getRecord().getFieldString("YD_STK_COL_GP"));
				ydStkBedNo = commUtils.trim(jsResult.getRecord().getFieldString("YD_STK_BED_NO"));
				ydStkLyrNo = commUtils.trim(jsResult.getRecord().getFieldString("YD_STK_LYR_NO"));

				// 조회항목 record 생성
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_WBOOK_ID"    , ydWbookId   );
				jrParam.setField("STL_NO"         , sStlNo 	    );			
				jrParam.setField("YD_STK_COL_GP"  , ydStkColGp  ); // 적치열구분					
				jrParam.setField("YD_STK_BED_NO"  , ydStkBedNo  ); // 적치BED번호					
				jrParam.setField("YD_STK_LYR_NO"  , ydStkLyrNo  ); // 적치단번호					
				jrParam.setField("YD_UP_COLL_SEQ" , "1"         ); // 권상모음순서

				// 작업예약재료 테이블에 등록한다.
				commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.insYdWrkbookMtl", logId, mthdNm, "작업예약재료 등록(입동지시)");
				
			}
				
			//출력 값
			jrRtn.setField("YD_WBOOK_ID", 	ydWbookId1); //대표작업예약 번호
			jrRtn.setField("YD_EQP_ID"  , 	sWrkCrn   );
			jrRtn.setField("YD_SCH_CD"  , 	ydSchCd   );
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	
	}
	
    
	/**
	 * 오퍼레이션명 : 출하차량도착실적처리 - 맵활성화
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procOutCarArrWr(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "출하영차도착 실적처리[CCoilCarMvSeEJB.procOutCarArrWr] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try{
			
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
	    	//----------------------------------------------------------------------------------------------------------
	    	//	파라미터 값 확인
	    	//----------------------------------------------------------------------------------------------------------
			String sMsg           = "";
			String sSposWlocCd    = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"   )); //발지개소코드
			String sSposYdPntCd   = commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD" )); //발지야드포인트코드
			String sCarNo         = commUtils.trim(rcvMsg.getFieldString("CAR_NO"         ));
			String sCardNo        = commUtils.trim(rcvMsg.getFieldString("CARD_NO"        ));
			String sTransOrdDate  = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DATE" ));
			String sTransOrdSeqno = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			
	    	String sModifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"           )); //수정자(Backup Only)

	    	//----------------------------------------------------------------------------------------------------------
	    	//	발지개소와 포인트코드로 적치열을 조회 후 차량정지위치 상태 체크
			//----------------------------------------------------------------------------------------------------------
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		    jrParam.setField("WLOC_CD"		, sSposWlocCd);
		    jrParam.setField("YD_PNT_CD"	, sSposYdPntCd);
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
	    	JDTORecordSet jsStkCol = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStkcolWLocCdandPntCd", logId, mthdNm, "적치열 조회");
	    	if (jsStkCol.size() <= 0) {
				sMsg = "발지개소["+sSposWlocCd+"] 및 포인트 코드["+sSposYdPntCd+"] 적치열 조회 실패!";
	    		commUtils.printLog(logId, sMsg, "S-");
	    		jrRtn.setField("RTN_CD"	, "0");	
	    		return jrRtn;
	    	}
		
	    	
	    	jsStkCol.absolute(1);
			JDTORecord jrStkCol = jsStkCol.getRecord();
			
	    	//차량 정지위치 적치열구분
			String ydCarldStopLoc = commUtils.trim(jrStkCol.getFieldString("YD_STK_COL_GP" ));
			
			jrParam.setField("YD_STK_COL_GP"      , ydCarldStopLoc );
			jrParam.setField("YD_STK_COL_ACT_STAT", "L"            );
	    	jrParam.setField("YD_CAR_USE_GP"      , "G"            );
	    	jrParam.setField("TRN_EQP_CD"         , ""             );
	    	jrParam.setField("CAR_NO"             , sCarNo         );
	    	jrParam.setField("CARD_NO"            , sCardNo        );
	    	
	    	/*
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
	    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStkcolActYn", logId, mthdNm, "적치열 수정");

	    	
	    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		    this.YdCarPointinforeg("C", sCarNo, sCardNo, ydCarldStopLoc, "", "", "L", logId, mthdNm, sModifier);
		   
		    
		    jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		    jrParam.setField("TRANS_ORD_DATE"  , sTransOrdDate );
	    	jrParam.setField("TRANS_ORD_SEQNO" , sTransOrdSeqno);
	    	jrParam.setField("CAR_NO"          , sCarNo        );
	    	jrParam.setField("CARD_NO"         , sCardNo       );
	    	jrParam.setField("YD_STK_COL_GP"   , ""            );
//PIDEV_S :병행가동용:PI_YD
	    	jrParam.setField("PI_YD",    	"J");		    	
	    	JDTORecordSet jsMtl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockYdStkLyrMtlCTransDTSeqCardNoDesc_PIDEV", logId, mthdNm, "");
	    	
	    	String ydMtlWtSum          = commUtils.trim(jsMtl.getRecord(0).getFieldString("YD_MTL_WT_SUM"       ));
			String ydMtlSh             = commUtils.trim(jsMtl.getRecord(0).getFieldString("YD_MTL_SH"           ));
			String sTransEquipmentType = commUtils.trim(jsMtl.getRecord(0).getFieldString("TRANS_EQUIPMENT_TYPE"));
			String ydCarProgStat 	   = commUtils.trim(jsMtl.getRecord(0).getFieldString("YD_CAR_PROG_STAT"    ));
	    	
			
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
//			jrParam.setField("YD_STK_BED_WT_MAX"  , "1 "+ ydMtlWtSum);
			jrParam.setField("YD_STK_COL_GP"      , ydCarldStopLoc );
			jrParam.setField("YD_STK_BED_ACT_STAT", "L");
		    
			/*
			UPDATE TB_YD_STKBED
			   SET MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			     , YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStkbedYdStkColGp", logId, mthdNm, "적치베드 활성화");
	    	
			//PDA하차 출하인 경우 생략
			if (("A".equals(ydCarProgStat) || "B".equals(ydCarProgStat)) && "P".equals(sTransEquipmentType)) {
				commUtils.printLog(logId, "PDA하차 출하인 경우 적치단 활성화 생략 (DMYDR073:에서 재료 update함", "SL");
			} else {
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_STK_COL_GP"      , ydCarldStopLoc);
				jrParam.setField("YD_STK_LYR_ACT_STAT", "E"           );
				jrParam.setField("STL_NO"             , ""            );
				jrParam.setField("YD_STK_LYR_MTL_STAT", "E"           );
		    	
				/*
				UPDATE TB_YD_STKLYR   
				   SET MODIFIER            = :V_MODIFIER
				     , MOD_DDTT            = SYSDATE
				     , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
				     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
				     , STL_NO              = :V_STL_NO
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStklyrYdStkColGp", logId, mthdNm, "적치단 활성화");
			}

			
			jrRtn = jrStkCol;
			jrRtn.setField("RTN_CD"	, "1");	
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}    

	
	/**
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * 오퍼레이션명 : 코일제품출하상차Lot편성(YDYDJ282) - DMYDR071, DMYDR074
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procCoilGdsDistCarLdComp(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일제품출하상차Lot편성[CCoilCarMvSeEJB.procCoilGdsDistCarLdComp] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try{
			
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			String sMsg           = "";
			commUtils.printLog(logId, "CICD 2026.02.05 소재통로 입동 프로세스 개선", "SL");
			
			String ydGp           = commUtils.trim(rcvMsg.getFieldString("YD_GP"            ));
			String sTransOrdDate  = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DATE"   ));
			String sTransOrdSeqno = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"  ));
			String sCarNo         = commUtils.trim(rcvMsg.getFieldString("CAR_NO"           ));
			String sCardNo        = commUtils.trim(rcvMsg.getFieldString("CARD_NO"          ));
			String sSposWlocCd    = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"     )); //발지개소코드
			String sSposYdPntCd   = commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"   )); //발지야드포인트코드
			String sCarKind       = commUtils.trim(rcvMsg.getFieldString("CAR_KIND"			)); // 차량구분 TT:TT CAR , T:Trailer
			String sWorkGp        = commUtils.trim(rcvMsg.getFieldString("WORK_GP"			));
			String ydCarldStopLoc = commUtils.trim(rcvMsg.getFieldString("YD_CARLD_STOP_LOC"));
//			String ydCarUseGp     = commUtils.trim(rcvMsg.getFieldString("YD_CAR_USE_GP"    ));
			String ydBayGp        = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"        ));
			String sCrFrtomoveGp  = commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP"   )); //11수출사내 41열연제품이송 63임가공 81열연소재이송
			
	    	String sModifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"         )); //수정자(Backup Only)
	    	
	    	if ("".equals(ydCarldStopLoc)) {
	    		sMsg = "상차정지위치가 없습니다.";
	    		commUtils.printLog(logId, sMsg, "SL");
				return jrRtn;
		    }
	    	if ("".equals(sCarNo)) {
	    		sMsg = "차량번호가 없습니다.";
	    		commUtils.printLog(logId, sMsg, "SL");
				return jrRtn;
		    }
// PIDEV
//		    if ("".equals(sCardNo)) {
//	    		sMsg = "카드번호가 없습니다.";
//	    		commUtils.printLog(logId, sMsg, "SL");
//				return jrRtn;
//		    }
		    if ("".equals(sSposWlocCd)) {
	    		sMsg = "발지개소코드가 없습니다.";
	    		commUtils.printLog(logId, sMsg, "SL");
				return jrRtn;
		    }
		    if ("".equals(sSposYdPntCd)) {
	    		sMsg = "발지포인트코드가 없습니다.";
	    		commUtils.printLog(logId, sMsg, "SL");
				return jrRtn;
		    }
		    if ("".equals(sTransOrdDate)) {
	    		sMsg = "운송지시일자가 없습니다.";
	    		commUtils.printLog(logId, sMsg, "SL");
				return jrRtn;
		    }
		    if ("".equals(sTransOrdSeqno)) {
	    		sMsg = "운송지시순번이 없습니다.";
	    		commUtils.printLog(logId, sMsg, "SL");
				return jrRtn;
		    }

		    /*************************
		     * 스케줄 코드 생성
		     *************************/
		    JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
		    		    
		    jrParam.setField("TRANS_ORD_DATE" 		, sTransOrdDate );
		    jrParam.setField("TRANS_ORD_SEQNO"		, sTransOrdSeqno);
		    
		    /*
			SELECT *
			  FROM TB_YD_CARSCH
			 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND DEL_YN = 'N'
		    */
	    	JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarschByTransDTSeq", logId, mthdNm, "차량스케쥴조회");
	    	String ydCarProgStat = "";
		    if (jsCarSch.size() > 0) {
		    	ydCarProgStat = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT"));
		    }
		    

		    String ydSchCd  = "";
		    String ydLocGp  = "";  
		
		    jrParam.setField("CR_FRTOMOVE_GP"   , sCrFrtomoveGp );
		    jrParam.setField("TRANS_ORD_DATE" 	, sTransOrdDate );
		    jrParam.setField("TRANS_ORD_SEQNO"	, sTransOrdSeqno);
		    jrParam.setField("SPOS_YD_PNT_CD"   , sSposYdPntCd  );
		    jrParam.setField("YD_CARLD_STOP_LOC", ydCarldStopLoc);
		    
		    /*
			WITH PARAM AS (
			SELECT CS.YD_CAR_PROG_STAT
			     , :V_SPOS_YD_PNT_CD    AS P_SPOS_YD_PNT_CD
			     , :V_YD_CARLD_STOP_LOC AS P_YD_CARLD_STOP_LOC
			     , :V_CR_FRTOMOVE_GP    AS P_CR_FRTOMOVE_GP
			     , CASE WHEN SUBSTR(CC.NEXT_PROC,2,1) IN ('A')  
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
			                          ) THEN 'Y'
			            ELSE 'N'
			        END  AS AIRCL_MTL_YN --공냉여부 판단
			     , CS.TRANS_ORD_SEQNO
			     , CC.CURR_PROG_CD			        
			     ,(SELECT YD_LOC_GP FROM TB_YD_STKCOL
			        WHERE YD_STK_COL_GP = CC.YD_GP||CC.YD_BAY_GP||CC.YD_EQP_GP||CC.YD_STK_COL_NO
			          AND DEL_YN = 'N'
			      ) AS YD_LOC_GP			        
			  FROM TB_PT_COILCOMM  CC
			     , TB_YD_CARSCH    CS
			     , TB_YD_STOCK     ST
			 WHERE CC.COIL_NO = ST.STL_NO
			   AND CS.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE  
			   AND CS.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO 
			   AND CS.TRANS_ORD_DATE  = ST.TRANS_ORD_DATE  
			   AND CS.TRANS_ORD_SEQNO = ST.TRANS_ORD_SEQNO 
			   AND CS.DEL_YN = 'N'
			)
			SELECT CASE WHEN P_CR_FRTOMOVE_GP IN ('63','11') THEN YD_GP_BAY||'TR0'||PASS_GP||LU_GP||'M' --임가공63 수출사내11 -> 출하
			            WHEN AIRCL_MTL_YN = 'Y'              THEN YD_GP_BAY||'PT2'||PASS_GP||LU_GP||'M' --공냉재이송
			            ELSE YD_GP_BAY||'PT0'||PASS_GP||LU_GP||'M' --이송출고
			        END AS YD_SCH_CD
			     , AIRCL_MTL_YN
			  FROM (
			        SELECT CASE WHEN YD_CAR_PROG_STAT IN ('1','2') THEN 'U'  --출고
			                    ELSE 'L'                                       --입고
			                END AS LU_GP  
			             , SUBSTR(P_SPOS_YD_PNT_CD, 1, 1)    AS PASS_GP
			             , SUBSTR(P_YD_CARLD_STOP_LOC, 1, 2) AS YD_GP_BAY
			             , AIRCL_MTL_YN
			            , P_CR_FRTOMOVE_GP
			          FROM PARAM 
			       )
			 WHERE 1 = 1
		     */
		  //PIDEV_S :병행가동용:PI_YD
		    jrParam.setField("PI_YD",    	"J");				    
		    JDTORecordSet jsYdSchCd = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getDistCarLdSchCd_PIDEV", logId, mthdNm, "스케줄 코드 생성");
		    
		    if (jsYdSchCd.size() <= 0) {
		    	sMsg = "스케줄코드 생성 실패";
				commUtils.printLog(logId, sMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sMsg);	
				return jrRtn;
		    }
		    
		    ydSchCd      = jsYdSchCd.getRecord(0).getFieldString("YD_SCH_CD");
		    ydLocGp      = jsYdSchCd.getRecord(0).getFieldString("YD_LOC_GP");
		    
		    commUtils.printLog(logId, "ydSchCd = " + ydSchCd, "SL");
		    
		    /*
		     * 해당 스케줄코드 작업우선순위 조회
		     */
		    jrParam.setField("YD_SCH_CD", ydSchCd);

			/*
			SELECT YD_SCH_CD
			     , DEL_YN
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
			  FROM TB_YD_SCHRULE
			 WHERE 1 = 1 
			   AND YD_GP IN ('H','J')
			   AND YD_SCH_CD = :V_YD_SCH_CD
			   AND DEL_YN    = 'N'
			 */
			JDTORecordSet jsSchCd = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdSchRule", logId, mthdNm, "스케줄기준 조회");

			//리턴값 메세지처리
			if (jsSchCd.size() <= 0) {
				sMsg = "스케줄코드(" + ydSchCd + ")에 대한 스케줄기준 데이터가 이상합니다.";
				commUtils.printLog(logId, sMsg, "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", sMsg);	
				return jrRtn;
			}	

			//b, c, d동 공냉재이송1통로는 소재장은 소재크레인이 작업
			String ydWrkPlanCrn = "";
			
			String sAPP839_YN = coilDao.ApplyYn(logId, mthdNm, "APP839", "J", "*"); // 공냉재 1통로시 소재장은 소재크레인
			
			if ("Y".equals(sAPP839_YN)) {
				if ("H".equals(ydLocGp) && ("JBPT21UM".equals(ydSchCd) || "JCPT21UM".equals(ydSchCd) || "JDPT21UM".equals(ydSchCd))) {
					ydWrkPlanCrn = commUtils.trim(jsSchCd.getRecord(0).getFieldString("YD_ALT_CRN"));
				}
			}
			
			String ydWrkCrnPrior 	= commUtils.trim(jsSchCd.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR")); //작업크레인우선순위

			
			JDTORecordSet jsMtl = JDTORecordFactory.getInstance().createRecordSet("YD");
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");	

			String sAppPI0010Yn = ydPICommDAO.ApplyYn("", mthdNm, "PI0010", "J", "*");
			// 상차 Lot 편성 대상 재료 Select
			jrParam.setField("YD_STK_COL_GP"      , ydCarldStopLoc.substring(0, 2));
			jrParam.setField("CAR_NO"          , sCarNo		);
//			if("N".equals(sApplyYnPI)) {
//			    jrParam.setField("CARD_NO"         , sCardNo		);				
//			}
			//PIDEV_S :병행가동용:PI_YD
			jrParam.setField("PI_YD",    	"J");

			if ("Y".equals(sAppPI0010Yn)) {
				if("U".equals(ydSchCd.substring(6, 7)) ) {// 상차
					commUtils.printLog(logId, "신상차___", "SL");
					jrParam.setField("YD_STK_CARPOINT"    , ydCarldStopLoc);
					jsMtl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockYdStkLyrMtlCTransDTSeqCardNoDescNew", logId, mthdNm, "신상차대상 조회");
				} else {
					commUtils.printLog(logId, "신하차__", "SL");
					jsMtl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockYdStkLyrMtlCTransDTSeqCardNoDesc_PIDEV", logId, mthdNm, "상차대상 조회");
				}
			} else {
				commUtils.printLog(logId, "기존", "SL");
				jsMtl = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockYdStkLyrMtlCTransDTSeqCardNoDesc_PIDEV", logId, mthdNm, "상차대상 조회");
			}	
			
		    String ydWbookId  = "";
		    String ydWbookId1 = ""; //대표 작업예약ID
		    String sStlNo     = "";
		    for (int i = 1; i <= jsMtl.size(); ++i) {
		    	
		    	jsMtl.absolute(i);

		    	sStlNo = jsMtl.getRecord().getFieldString("STL_NO"); // 재료번호
		    	
		    	String sApp822 = coilDao.ApplyYn(logId, mthdNm, "APP822", "J", "*"); //차량작업시 작업예약 삭제
		    	
		    	if ("Y".equals(sApp822)) {
		    		jrParam.setField("STL_NO", sStlNo); //재료번호
		    		
		    		/*
					SELECT B.STL_NO
					      ,B.YD_STK_LYR_NO      
					      ,A.*
					  FROM USRYDA.TB_YD_CRNSCH A
					      ,USRYDA.TB_YD_CRNWRKMTL B
					 WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
					   AND A.DEL_YN = 'N'
					   AND B.DEL_YN = 'N'
					   AND B.STL_NO = :V_STL_NO
		    		 */
		    		JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getCrnSchByStlNo", logId, mthdNm, "크레인스케줄 조회");
		    		
		    		if (jsCrnSch.size() > 0) {
		    			String ydWrkProgStat = jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
		    			String ydCrnSchId    = jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   );
		    			
		    			if ("W".equals(ydWrkProgStat)) {
		    				// 크레인 스케줄 삭제
		    				jrParam.setField("YD_CRN_SCH_ID"    , ydCrnSchId);
							jrParam.setField("DEL_YN"		    , "Y");
							jrParam.setField("YD_L2_RETURN_FLAG", "Y");
		    				
							/**********************************************************
							* 크레인스케줄 취소
							**********************************************************/
							EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
							JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
		    			}
		    		}
		    		
		    		/*
					UPDATE TB_YD_WRKBOOK
					   SET MODIFIER  = :V_MODIFIER
					     , MOD_DDTT  = SYSDATE
					     , DEL_YN    = 'Y'
					 WHERE YD_WBOOK_ID IN (SELECT WB.YD_WBOOK_ID 
					                         FROM TB_YD_WRKBOOK    WB
					                            , TB_YD_WRKBOOKMTL WM
					                        WHERE WB.DEL_YN = 'N'
					                          AND WM.DEL_YN = 'N'
					                          AND WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
					                          AND NOT EXISTS (SELECT 1
					                                            FROM TB_YD_CRNSCH CS
					                                           WHERE CS.YD_WBOOK_ID = WB.YD_WBOOK_ID
					                                             AND CS.DEL_YN = 'N')
					                          AND WM.STL_NO = :V_STL_NO
					                      )
		    		 */
		    		commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.delWrkbookByStlNo", logId, mthdNm, "작업예약 삭제");
		    		
		    		/*
					UPDATE TB_YD_WRKBOOKMTL
					   SET MODIFIER  = :V_MODIFIER
					     , MOD_DDTT  = SYSDATE
					     , DEL_YN    = 'Y'
					 WHERE YD_WBOOK_ID IN (SELECT WB.YD_WBOOK_ID 
					                         FROM TB_YD_WRKBOOK    WB
					                            , TB_YD_WRKBOOKMTL WM
					                        WHERE WB.DEL_YN = 'N'
					                          AND WM.DEL_YN = 'N'
					                          AND WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
					                          AND NOT EXISTS (SELECT 1
					                                            FROM TB_YD_CRNSCH CS
					                                           WHERE CS.YD_WBOOK_ID = WB.YD_WBOOK_ID
					                                             AND CS.DEL_YN = 'N')
					                          AND WM.STL_NO = :V_STL_NO
					                      )
		    		 */
		    		commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.delWrkbookMtlByStlNo", logId, mthdNm, "작업예약재료 삭제");
		    	}
		    	
				//작업예약 Id생성
				ydWbookId = coilDao.getSeqId(logId, mthdNm, "WrkBook");
		    	
				if (i == 1) {
					ydWbookId1 = ydWbookId;
				}
				// INSERT할 항목 SET
				jrParam.setField("YD_WBOOK_ID"      , ydWbookId    );
				jrParam.setField("YD_GP"            , ydGp         );
				jrParam.setField("YD_BAY_GP"        , ydBayGp      );
				jrParam.setField("YD_SCH_CD"        , ydSchCd      );
				jrParam.setField("YD_SCH_PRIOR"     , ydWrkCrnPrior);
				jrParam.setField("YD_CAR_USE_GP"    , "G"          ); //출하
				jrParam.setField("CAR_NO"           , sCarNo       );
				jrParam.setField("CARD_NO"          , sCardNo      );
				jrParam.setField("YD_AIM_YD_GP"     , commUtils.trim(jsMtl.getRecord().getFieldString("YD_AIM_YD_GP" )));
				jrParam.setField("YD_AIM_BAY_GP"    , commUtils.trim(jsMtl.getRecord().getFieldString("YD_AIM_BAY_GP")));
				jrParam.setField("YD_WRK_PLAN_CRN"  , ydWrkPlanCrn );
				
				// 작업예약 INSERT
				commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.insYdWrkbook", logId, mthdNm, "작업예약 생성(출하상차LOT편성)");
				
				jrParam.setField("YD_WBOOK_ID"      , ydWbookId  );
				jrParam.setField("STL_NO"           , jsMtl.getRecord().getFieldString("STL_NO")       ); //재료번호		    	
		    	jrParam.setField("YD_STK_COL_GP"    , jsMtl.getRecord().getFieldString("YD_STK_COL_GP"));
		    	jrParam.setField("YD_STK_BED_NO"    , jsMtl.getRecord().getFieldString("YD_STK_BED_NO"));
		    	jrParam.setField("YD_STK_LYR_NO"    , jsMtl.getRecord().getFieldString("YD_STK_LYR_NO"));
		    	jrParam.setField("YD_UP_COLL_SEQ"   , ""+i); //권상모음순서

				// 작업예약재료 테이블에 등록한다.
				commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilWrkBookSeEJB.insYdWrkbookMtl", logId, mthdNm, "작업예약재료 등록(출하상차LOT편성)");
		    }
		    
			
			/**********************************************************
			* 저장위치제원 L2전문 송신
			**********************************************************/
		    commUtils.printLog(logId, "ydCarProgStat : " + ydCarProgStat, "SL");
		    JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
		    if ("2".equals(ydCarProgStat) || "B".equals(ydCarProgStat)) {
		    	// 이미 차량도착상태 일 때는 보내지 않음
				sndL2Msg.setField("YD_INFO_SYNC_CD"	   , "4"           ); //야드정보동기화코드
				sndL2Msg.setField("YD_STK_COL_GP"  	   , ydCarldStopLoc); //야드적치열구분
				sndL2Msg.setField("YD_STK_BED_NO"	   , "01"          );
				sndL2Msg.setField("YD_CAR_ARRSTRT_STAT", "A"           ); //A도착 S출발
				sndL2Msg.setField("YD_CAR_PROG_STAT"   , "2"           );
				sndL2Msg.setField("YD_EQP_WRK_STAT"    , "U"           );
				sndL2Msg.setField("YD_CAR_USE_GP"      , "G"           );//출하
				sndL2Msg.setField("CAR_NO"             , sCarNo        );
				sndL2Msg.setField("CARD_NO"            , sCardNo       );
			    
				//전송 Data 생성
				jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L001_CarInfo", sndL2Msg));
		    }
			
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("CAR_NO"           , sCarNo        );	
			
			// PIDEV
//			if("N".equals(sApplyYnPI)) {
//				jrParam.setField("CARD_NO"          , sCardNo       );
//			}
			
			/*
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
			//PIDEV_S :병행가동용:PI_YD
			jrParam.setField("PI_YD",    	"J");			
			jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschCarNoCardNo_PIDEV", logId, mthdNm, "차량스케줄 조회");
			if (jsCarSch.size() <= 0) {
				commUtils.printLog(logId, "차량스케줄 존재하지 않음", "S-");
				return jrRtn;
			}
			
			String ydCarSchId   = jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID");
			String ydEqpWrkStat = jsCarSch.getRecord(0).getFieldString("YD_EQP_WRK_STAT");
			
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
			

			if ("L".equals(ydEqpWrkStat)) {
				jrParam.setField("YD_CAR_PROG_STAT"    , "B"           );//하차도착상태 
				jrParam.setField("YD_CARUD_WRK_BOOK_ID", ydWbookId1    );
				jrParam.setField("YD_CARUD_STOP_LOC"   , ydCarldStopLoc);
				jrParam.setField("YD_CARUD_ARR_DT"     , commUtils.getDateTime14());
				jrParam.setField("YD_PNT_CD3"          , sSposYdPntCd  );
			} else {
				jrParam.setField("YD_CAR_PROG_STAT"    , "2"           );//상차도착상태
				jrParam.setField("YD_CARLD_WRK_BOOK_ID", ydWbookId1    );
				jrParam.setField("YD_CARLD_STOP_LOC"   , ydCarldStopLoc);
				jrParam.setField("YD_CARLD_ARR_DT"     , commUtils.getDateTime14());
				jrParam.setField("YD_PNT_CD1"          , sSposYdPntCd);
			}
			
			if(!"".equals(sSposWlocCd)) {
				jrParam.setField("SPOS_WLOC_CD", sSposWlocCd);
			}
			
			/*
			UPDATE TB_YD_CARSCH
			   SET MOD_DDTT = SYSDATE
			     , MODIFIER = :V_MODIFIER
			     --상차
			     , SPOS_WLOC_CD         = NVL(:V_SPOS_WLOC_CD      , SPOS_WLOC_CD      )
			     , YD_CARLD_STOP_LOC    = NVL(:V_YD_CARLD_STOP_LOC , YD_CARLD_STOP_LOC )
			     , YD_CARLD_PNT_WO_DT   = NVL(TO_DATE(:V_YD_CARLD_PNT_WO_DT, 'YYYYMMDDHH24MISS'), YD_CARLD_PNT_WO_DT)
			     , YD_PNT_CD1           = NVL(:V_YD_PNT_CD1        , YD_PNT_CD1        )
			     , YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_CARLD_WRK_BOOK_ID, YD_CARLD_WRK_BOOK_ID)
			     --하차
			     , ARR_WLOC_CD          = NVL(:V_ARR_WLOC_CD       , ARR_WLOC_CD       )
			     , YD_CAR_PROG_STAT     = NVL(:V_YD_CAR_PROG_STAT  , YD_CAR_PROG_STAT  )
			     , YD_CARUD_LEV_DT      = NVL(:V_YD_CARUD_LEV_DT   , YD_CARUD_LEV_DT   )
			     , YD_CARUD_STOP_LOC    = NVL(:V_YD_CARUD_STOP_LOC , YD_CARUD_STOP_LOC )
			     , YD_CARUD_PNT_WO_DT   = NVL(TO_DATE(:V_YD_CARUD_PNT_WO_DT, 'YYYYMMDDHH24MISS'), YD_CARUD_PNT_WO_DT)
			     , YD_PNT_CD3           = NVL(:V_YD_PNT_CD3        , YD_PNT_CD3        )
			     , YD_CARUD_WRK_BOOK_ID = NVL(:V_YD_CARUD_WRK_BOOK_ID, YD_CARUD_WRK_BOOK_ID)
			     , YD_EQP_WRK_STAT      = NVL(:V_YD_EQP_WRK_STAT   , YD_EQP_WRK_STAT   ) --상차U 하차 L로 세팅
			     , WAIT_ARR_DDTT        = NVL(:V_WAIT_ARR_DDTT     , WAIT_ARR_DDTT     ) --대기장 도착시간
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	    	 */
	    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updCarSchTSYDJ002", logId, mthdNm, "차량스케줄 수정");
	    	
			
    		/**********************************************************
			 * 차량작업 예정정보 송신 YDY5L008
			 **********************************************************/
	    	sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
	    	sndL2Msg.setField("JMS_TC_CD"       , "YDY5L008");
			sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
			sndL2Msg.setField("SEARCH_FLAG"     , "2"       ); //1:상차도, 2:차량스케쥴 ID
			sndL2Msg.setField("YD_CAR_SCH_ID"   , ydCarSchId); //차량스케줄
				
			jrRtn = commUtils.addSndData(jrRtn, coilDao.procCarPlanInfo(sndL2Msg));	 //전송 Data 생성
	    	
			/**********************************************************
			 * 차량형상이 없을 때 스케줄 기동
			 **********************************************************/
			/* 
			SELECT NVL(YD_FRM_YN,'N') AS YD_FRM_YN
			  FROM USRYDA.TB_YD_CARPOINT
			 WHERE 1=1 
			   AND YD_STK_COL_GP = :V_YD_STK_COL_GP 
			   AND YD_GP  = 'J'
			   AND DEL_YN = 'N'
            */
			jrParam.setField("YD_STK_COL_GP", ydCarldStopLoc);
			JDTORecordSet jsYdFrmYn = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getYdCarpointByFrmYn", logId, mthdNm, "형상 비형상 판단");

			String ydFrmYn = commUtils.nvl(jsYdFrmYn.getRecord(0).getFieldString("YD_FRM_YN"), "N");

			commUtils.printLog(logId, "차량형상 존재여부 : " + ydFrmYn, "SL");
			
			/**********************************************************
			 * 작업예약 호출
			 **********************************************************/
			/* 
			SELECT WB.YD_SCH_CD
			     , WB.YD_WBOOK_ID
			     , WM.YD_STK_COL_GP
			     , WM.YD_STK_LYR_NO
			     --차량동간이적 상차인지CHECK : 형상정보완료(Y5YDL030) 수신시 필요
			     , CASE WHEN YD_SCH_CD LIKE 'J_TR1_U_' THEN 'Y'
			            ELSE 'N' END CAR_MOVE_GP
			     --차량도착전문보다 스캔완료 전문이 먼저왔을 때 스케줄 기동을 위하여 필요
			     , WB.YD_CTS_RELAY_YN 
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
			     OR (WB.YD_CAR_USE_GP = 'G' AND WB.CAR_NO = CP.CAR_NO  AND WB.CARD_NO = CP.CARD_NO))
			   AND WB.YD_WBOOK_ID NOT IN (SELECT CS.YD_WBOOK_ID 
			                                FROM TB_YD_CRNSCH    CS
			                                   , TB_YD_CRNWRKMTL CM
			                               WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
			                                 AND CS.DEL_YN = 'N'
			                                 AND CM.DEL_YN = 'N')
			 ORDER BY WM.YD_STK_LYR_NO DESC
			        , WB.YD_WBOOK_ID
 			 */ 

			//PIDEV_S :병행가동용:PI_YD
			jrParam.setField("PI_YD",    	"J");					
			JDTORecordSet jsCarWbookId = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getCarWrkList_PIDEV", logId, mthdNm, "작업예약 조회");					
			
			if (jsCarWbookId.size() == 0) {
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
			}
			
			/********************************************************
			 * 도착전문보다 형상스캔 완료정보가 먼저 왔을 경우 스케줄 기동
			 ********************************************************/
			String sFrmCompYn = commUtils.nvl(jsCarWbookId.getRecord(0).getFieldString("YD_CTS_RELAY_YN"), "N");
			commUtils.printLog(logId, "차량형상 측정여부 : " + sFrmCompYn, "SL");
			
			String sApp823 = coilDao.ApplyYn(logId, mthdNm, "APP823", "J", "*"); //차량하차시 스케줄 자동기동여부
    		if ("Y".equals(sApp823)) {

				if (jsCarWbookId.size() > 0 ) {
    				
    				JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier); 
					jrYdMsg.setField("JMS_TC_CD"		 , "YDYDJ552");  //
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_SCH_CD"         , ""); //야드스케쥴코드
					jrYdMsg.setField("YD_EQP_ID"         , ""); //야드설비ID
					
					for (int i = 1; i <= jsCarWbookId.size(); i++) {
						jsCarWbookId.absolute(i);
						jrYdMsg.setField("YD_WBOOK_ID" + i, jsCarWbookId.getRecord().getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrYdMsg.setField("SCH_CNT"        , "" + i); //작업예약 개수
					}
					
					EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this);
					JDTORecord jrCrnSchMsg = (JDTORecord)ejbConn.trx("procYDYDJ552", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
					
					jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
					commUtils.printLog(logId, "크레인스케줄 기동", "SL");
    			}
				//---------------------------------------				
    		} else {
    			// 2026.02.05 소재통로 입동 프로세스 개선
    			if ("Y".equals(ydFrmYn) 
    				&& ( "JAPT03".equals(ydCarldStopLoc)
    					 || "JBPT03".equals(ydCarldStopLoc)
    					 || "JCPT03".equals(ydCarldStopLoc)
    					 || "JDPT03".equals(ydCarldStopLoc)
    					 || "JEPT03".equals(ydCarldStopLoc)
    					 || "JFPT01".equals(ydCarldStopLoc)
    					 || "JGPT01".equals(ydCarldStopLoc)
    					 || "JHPT03".equals(ydCarldStopLoc)
    					  )
    				) {	
    				ydEqpWrkStat = "";
    			} // 2026.02.05 소재통로 입동 프로세스 개선
    			
    			if ("N".equals(ydFrmYn) || "U".equals(ydEqpWrkStat)) {
    				JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
    				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
    				jrParam.setField("YD_SCH_CD", ""); //야드스케쥴코드
    				jrParam.setField("YD_EQP_ID", ""); //야드설비ID
    				
    				/* ***************************
    				 * 적치된 대상 크레인스케줄 호출 
    				 * ***************************/
    				EJBConnector ejbConn = new EJBConnector("default", "CCoilSchSeEJB", this);
    				
    				for (int i = 0; i < jsCarWbookId.size(); i++) {
    					jrParam.setField("YD_WBOOK_ID", jsCarWbookId.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
    					jrCrnSchMsg = (JDTORecord)ejbConn.trx("procYDYDJ551", new Class[] { JDTORecord.class }, new Object[] { jrParam });
    				}
    				
    				jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
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
	
} 
