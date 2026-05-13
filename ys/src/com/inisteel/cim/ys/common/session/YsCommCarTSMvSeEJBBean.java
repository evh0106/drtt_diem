/**
 * @(#)YsCommCarTSMvSeEJBBean.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2025/09/11
 * 
 * @description		이클래스는 차량이동처리 Session EJB클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.01  2025/09/11        
 * 차량이동처리 Session EJB
 * rcvTSYSJ002 					: 소재차량도착Point요구(TSYSJ002)
 * rcvTSYSJ003 					: 소재차량도착(TSYSJ003)
 * rcvTSYSJ004 					: 소재차량출발(TSYSJ004)
 * rcvTSYSJ005 					: 서문스크랩상차실적(TSYSJ005)
 * procLDMatlCarArr 			: 소재차량 공차도착 실적
 * procUDMatlCarArr 			: 소재차량 영차도착 실적
 * procCallCrnSch 				: 크레인스케줄호출
 * procInsWrkBookCarUd 			: 하차작업예약생성
 * procInsCarSch 			    : 차량스케쥴 편성 및 상차Lot편성 호출 
 * YsCarPointinforeg2 			: 차량 포인트 통합관리 YD 와 동일
 * rcvTSYSJ014 					: 공차출발취소(TSYSJ014) 2025.08.27 신규 작성
 * rcvTSYSJ006 					: 소재차량대기장 도착(TSYSJ006) 2025.08.27 신규 작성
 * updPbStlFrToMoveUp			: 이송지시 상차 완료 UPDATE
 * updPbStlFrToMoveDown			: 이송지시 하차 완료 UPDATE
 * delPrepSch					: TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료) 종료
 * proc42GateCarArr				: 남42문 소재차량 공차도착 실적
 * carLdCmplYn					: 상차 완료 기준 조회
 * ApplyRuleItem				: RULE ITEM RETURN
 * procL2MatlCarArr				: L2 -> L3 구내운송 영차 도착 처리
 */	

package com.inisteel.cim.ys.common.session;


import com.inisteel.cim.cm.message.MessageSenderAuto;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ys.message.MessageSenderTalk;
import com.inisteel.cim.ysPI.dao.YsPiDAO;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;
import com.inisteel.cim.ys.gds.session.GdsYsComm; 

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;

import com.inisteel.cim.ys.common.util.YsQueryIFCar;


//GridData 사용
import com.inisteel.cim.ys.cbt.session.CbtYsJspSeEJBBean; 
import com.inisteel.cim.ys.ebt.session.EbtYsJspSeEJBBean; 
//2026.01.29 소형 야드 추가
import com.inisteel.cim.ys.sbr.session.SbrYsJspSeEJBBean; 
import xlib.cmc.GridData;

/**
 * 차량이동처리 Session EJB
 *
 * @ejb.bean name="YsCommCarTSMvSeEJB" jndi-name="YsCommCarTSMvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
 */
public class YsCommCarTSMvSeEJBBean extends BaseSessionBean implements YsQueryIFCar { 
	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	private GdsYsComm gdsYsComm = new GdsYsComm();
	
	private CbtYsJspSeEJBBean	CbtYsJsp   	= new CbtYsJspSeEJBBean();  
	private EbtYsJspSeEJBBean	EbtYsJsp   	= new EbtYsJspSeEJBBean();  
// 2026.01.29 소형 야드 추가
	private SbrYsJspSeEJBBean	SbrYsJsp   	= new SbrYsJspSeEJBBean();  
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 *      [A] 오퍼레이션명 : 소재차량도착Point요구(TSYSJ002) YSYSJ901 호출시 기동
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYSJ002(JDTORecord rcvMsg)throws DAOException  {

		/*
		 * 전문내용 : TSYSJ002(소재차량도착Point요구)
		 * TRN_EQP_CD                    	운송장비코드			CHAR	8		구내운송 차량 제원 등록 NO.	
		 * WLOC_CD							개소코드			CHAR	5		작업 개소(상차지,하차지)
		 * TRN_WRK_FULLVOID_GP				운송작업영공구분		CHAR	1		F(영차) / E(공차)
		 * PNT_DMD_DT						포인트요구일시		CHAR 	14
		 */
		
		String methodNm = "소재차량도착Point요구[YsCommCarTSMvSeEJBBean.rcvTSYSJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		JDTORecordSet 	rsResult    = null;
		JDTORecord	  	sndRecord	= JDTORecordFactory.getInstance().create();
		JDTORecord    	recInTemp   = null;
		JDTORecord    	recInTemp1  = null;
		JDTORecord    	recOutTemp  = null;
		JDTORecord    	recOutTemp1 = null;
		JDTORecord    	recSndPnt   = null;


	    int intRtnVal 				= 0 ;
	    
	    String szMsg           		= "";
	    String szOperationName		= "소재차량도착Point요구";
	    String szMethodName     	= "procMatlCarLev";
	    
	    String szYS_STK_COL_GP 		= "";
	    String srTRN_EQP_CD    		= "";					// 운송장비코드
	    String srWLOC_CD       		= "";					// 개소코드
	    String szYD_CAR_SCH_ID 		= "";
	    String szYD_PNT_CD     		= "";
	    String szYD_CAR_PROG_STAT	= "";
	    String srTRN_WRK_FULLVOID_GP= "";					// 운송작업영공구분
	    String szYD_STK_COL_ACT_STAT= "";
	    String szLAST_TRN_EQP_CD_LOC= "";
	    String szCOL_WLOC_CD 		= "";		    
	    String szSPOS_WLOC_CD 		= ""; 					// 각강체크를 위함 - WC
	    
        String msgId = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
        
        if(msgId==null || msgId.equals("")){
        	return sndRecord;
        }

        szMsg = "[" + szOperationName + "] 전문수신 : TCCODE=" + msgId ;
        
	    try{

			commUtils.printLog(logId, methodNm, "S+");
			
	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");

			commUtils.printParam(logId + "소재차량도착Point요구 수신 ", rcvMsg);
			
	    	//운송장비코드, 개소코드, 상하차구분코드, 포인트요구일시
			srTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")			);
			srWLOC_CD      			= commUtils.trim(rcvMsg.getFieldString("WLOC_CD")				); 
			srTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")	); 
  
			//포인트지시 메세지 전송 준비
			//포인트 결정 하면 됨
			recSndPnt = JDTORecordFactory.getInstance().create();

			recSndPnt.setResultCode(logId);											// Log ID
			recSndPnt.setResultMsg(methodNm);										// Log Method Name
			recSndPnt.setField("JMS_TC_CD"			, "YSTSJ011"				);
			recSndPnt.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()	); 	// JMSTC생성일시
			recSndPnt.setField("TRN_EQP_CD"			, srTRN_EQP_CD				);
			recSndPnt.setField("WLOC_CD"			, srWLOC_CD					);
			recSndPnt.setField("PNT_WO_GP"			, "A"						);
			recSndPnt.setField("PNT_WO_DT"			, commUtils.getDateTime14()	);
/*			
			recSndPnt.setField("YD_PNT_CD"			, "0000");               
			sndRecord = commUtils.addSndData(sndRecord,recSndPnt);
*/			
// 2026.01.29 야드 구분 사용 부분 없음 막음			
//			String szYD_GP = commUtils.getWlocToYdGp(srWLOC_CD);
			
			/**********************************************************
			* 1. 운송장비코드로 차량스케줄을 조회한다.
			**********************************************************/			
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp 	= JDTORecordFactory.getInstance().create();

	    	recInTemp.setField("TRN_EQP_CD", srTRN_EQP_CD);
	    	
	    	
	    	// 운송장비코드로 차량스케쥴 조회
			rsResult = commDao.select(recInTemp, getYdCarschDaoTrnEqpCd, logId, methodNm, "차량스케줄을 조회"); 

			if (rsResult == null || rsResult.size() <= 0) {
				szMsg = "운송장비코드[" + srTRN_EQP_CD + "]로 차량스케줄 없음 ---> '0000' 포인트 송신";
				commUtils.printLog(logId, szMsg, "SL");
				
				recSndPnt.setField("YD_PNT_CD"			, "0000");
// 2025.08.19 YD_MSG_NM(야드메시지) 추가	
				recSndPnt.setField("YD_MSG_NM"			, szMsg);
				
				return sndRecord = commUtils.addSndData(sndRecord,recSndPnt);
				
			} else if (rsResult.size() > 1) {
				szMsg = "차량스케줄 조회 시 오류발생 - 운송장비코드로 차량스케줄이 여러건[" + rsResult.size() + "]이 존재합니다.---> '0000' 포인트 송신";
				commUtils.printLog(logId, szMsg, "SL");

				recSndPnt.setField("YD_PNT_CD"			, "0000");               
// 2025.08.19 YD_MSG_NM(야드메시지) 추가	
				recSndPnt.setField("YD_MSG_NM"			, szMsg);

				return sndRecord = commUtils.addSndData(sndRecord,recSndPnt);
				
			}

			/**********************************************************
			* 2. 상차출발 / 하차출발 검사
			**********************************************************/		
			rsResult.absolute(1);			
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord());
			
			szYD_CAR_SCH_ID      	= commUtils.trim(recOutTemp.getFieldString("YD_CAR_SCH_ID")); 
			szYD_CAR_PROG_STAT    	= commUtils.trim(recOutTemp.getFieldString("YD_CAR_PROG_STAT")); 
			szLAST_TRN_EQP_CD_LOC	= commUtils.trim(recOutTemp.getFieldString("LAST_TRN_EQP_CD_LOC")); 
	    	
			// 각강체크를 위함 - WC
	        szSPOS_WLOC_CD          = commUtils.trim(recOutTemp.getFieldString("SPOS_WLOC_CD")); 

			szMsg = "[" + methodNm + "] szYD_CAR_PROG_STAT [" + szYD_CAR_PROG_STAT + "]";
			commUtils.printLog(logId, szMsg, "******");
	        
	    	if( szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARLD_LEV) ) {
				//상차출발
	    		szYS_STK_COL_GP     = commUtils.trim(recOutTemp.getFieldString("YD_CARLD_STOP_LOC")); 
	    		szYD_PNT_CD      	= commUtils.trim(recOutTemp.getFieldString("YD_PNT_CD1")); 

	    	} else if( szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARUD_LEV) ) {
	    		//하차출발
	    		szYS_STK_COL_GP     = commUtils.trim(recOutTemp.getFieldString("YD_CARUD_STOP_LOC")); 
	    		szYD_PNT_CD      	= commUtils.trim(recOutTemp.getFieldString("YD_PNT_CD3")); 

	    	}  else {
	    		//상차출발/하차출발이 아니경우 ----> 대기
	
				recSndPnt.setField("YD_PNT_CD"			, "0000");               

// 2025.08.19 YD_MSG_NM(야드메시지) 추가
				szMsg = "야드차량진행상태 [" + szYD_CAR_PROG_STAT + "] 상차출발 / 하차출발 이 아닙니다.---> '0000' 포인트 송신";
				commUtils.printLog(logId, szMsg, "SL");
				
				recSndPnt.setField("YD_MSG_NM"			, szMsg);

				return sndRecord = commUtils.addSndData(sndRecord,recSndPnt);	    		
	    	}
	    	
	    	szMsg = "[" + methodNm + "] 운송장비코드 [" + srTRN_EQP_CD + "]로 차량스케줄 [" + szYD_CAR_SCH_ID + "], 메세지msgId[" + msgId 
	    		  + "] 차량스케줄포인트 [" + szYD_PNT_CD + "] 차량진행상태 [" + szYD_CAR_PROG_STAT + "]";
	    	
			commUtils.printLog(logId, szMsg, "SL");

			/**********************************************************
			* 3. 가용포인트 조회 ( YSYSJ901 / TSYSJ002:0000 (대기장)
			*  - 특수강 이송은 LOT편성된 정보가 없으면 무조건 대기장으로 처리 함
			**********************************************************/		

		
	    	szMsg = "[" + methodNm + "] 개소코드 [" + srWLOC_CD + "] szYS_STK_COL_GP [" + szYS_STK_COL_GP + "]";
			commUtils.printLog(logId, szMsg, "SL");

			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
	    	recInTemp.setField("MODIFIER"     , "TSYSJ002"     ); 	// 수정자
			
			/**********************************************************
			* 4. 가용포인트 조회
			**********************************************************/
	    	boolean isReqCheck = false; // 각강입고/중복 포인트요구 체크를 위함 - WC

	    	/**********************************************************
			* 4-1. 야드구분+동구분+설비구분+% ---> 적치열 검색
			**********************************************************/	
			// 스케줄코드의 야드구분 동구분 설비구분으로 적치열을 LIKE 검색한다.
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp1 = JDTORecordFactory.getInstance().create();
	    	recOutTemp1 = JDTORecordFactory.getInstance().create();
	    
	    	if (szYS_STK_COL_GP == null ) {
	    		szYS_STK_COL_GP = "%";
	    	}
	    	
	    	recInTemp1.setField("WLOC_CD"		, srWLOC_CD);
	    	recInTemp1.setField("YS_STK_COL_GP"	, szYS_STK_COL_GP);

szMsg = "[" + methodNm + "] Check 1";
commUtils.printLog(logId, szMsg, "SL");
	    	
	    	rsResult = commDao.select(recInTemp1, getYdStkcolColGpLike, logId, methodNm, "적치열 조회"); 
	    	
	    	
szMsg = "[" + methodNm + "] Check 2";
commUtils.printLog(logId, szMsg, "SL");
	    	
	    	String szCOL_TRN_EQP_CD = "";						//적치열에 이미 등록된 운송장비코드
	    	String szYD_PREP_SCH_ID = "";						
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		szMsg	= "적치열 조회 getYdStkcol data not found";
				commUtils.printLog(logId, szMsg, "SL");
				recSndPnt.setField("YD_PNT_CD"			, "0000");
//2025.08.19 YD_MSG_NM(야드메시지) 추가	
				recSndPnt.setField("YD_MSG_NM"			, szMsg);
				
				return sndRecord = commUtils.addSndData(sndRecord,recSndPnt);
			} else {
szMsg = "[" + methodNm + "] Check 3";
commUtils.printLog(logId, szMsg, "SL");

				/**********************************************************
				* 4-1-2. 중복 포인트 요구 체크
				*        --->   기존 차량정보로 송신 처리 함
				**********************************************************/	
				for (int i = 1; i <= rsResult.size(); i++) {
					rsResult.absolute(i);
					recOutTemp1	= rsResult.getRecord();
					
					szCOL_TRN_EQP_CD = commUtils.trim(recOutTemp1.getFieldString("TRN_EQP_CD"));
					szCOL_WLOC_CD    = commUtils.trim(recOutTemp1.getFieldString("WLOC_CD"));
				
					if (szCOL_TRN_EQP_CD.equals(srTRN_EQP_CD)){
						isReqCheck = true;		
						szYD_PNT_CD    	  	= commUtils.trim(recOutTemp1.getFieldString("YD_PNT_CD")); 
				    	szYS_STK_COL_GP   	= commUtils.trim(recOutTemp1.getFieldString("YS_STK_COL_GP"));
						break;
					}
				}
szMsg = "[" + methodNm + "] Check 4";
commUtils.printLog(logId, szMsg, "SL");

				/**********************************************************
				* 4-1-3. 적치열 검색 성공 시 / 적치가능여부 체크
				**********************************************************/	
				if(!isReqCheck) { // 중복포인트요구가 아닐 경우
					
szMsg = "[" + methodNm + "] Check 5";
commUtils.printLog(logId, szMsg, "SL");

					
		    		for(int i = 1; i <= rsResult.size(); i++ ) {
		    			rsResult.absolute(i);
		    			recOutTemp1			= rsResult.getRecord();
		    			
				    	szYD_PNT_CD    		  	= commUtils.trim(recOutTemp1.getFieldString("YD_PNT_CD")); 
				    	szYS_STK_COL_GP 	  	= commUtils.trim(recOutTemp1.getFieldString("YS_STK_COL_GP"));
				    	szYD_STK_COL_ACT_STAT 	= commUtils.trim(recOutTemp1.getFieldString("YD_STK_COL_ACT_STAT"));
				    	szCOL_TRN_EQP_CD		= commUtils.trim(recOutTemp1.getFieldString("TRN_EQP_CD")); 
				    	szYD_PREP_SCH_ID		= commUtils.trim(recOutTemp1.getFieldString("YD_PREP_SCH_ID")); 
				    	szCOL_WLOC_CD   		= commUtils.trim(recOutTemp1.getFieldString("WLOC_CD"));
				    	
				    	if(	szYD_STK_COL_ACT_STAT.equals("C")) {
				    		
				    		if( szCOL_TRN_EQP_CD.equals("") ) {
				    			
				    			
////////////////////////////////////////////////////////////////////////////////
// 2025.11.30 사용가능한 경우 요청한 운송장비코드로 전송 하지 않고 우선순위 빠른(요청한 순위가 빠른) 운송장비코드로 전송
//            면저 요청 했는데 후순위에 있는 차량이 먼저 선택 될수 있어서 야드포인트 요청 일시가 빠른 구내운송장비가 우선 들어오게 변경 
//            야드차량스케쥴ID(YD_CAR_SCH_ID), 운송장비코드(TRN_EQP_CD)Get 후 변경
////////////////////////////////////////////////////////////////////////////////
				    			String sApplyYnPI = "";

				    			sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "002", "*");
								
								if("Y".equals(sApplyYnPI)){
									szMsg = "TB_YS_RULE 002_구내운송_포인트_요구개선 ";
						      		commUtils.printLog(logId, szMsg, "");

						    		JDTORecord    	recInTemp2  = null;
							    	recInTemp2 = JDTORecordFactory.getInstance().create();

							    	JDTORecordSet 	rsResult1    = null;
									rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
							    	
							    	
							    	recInTemp2.setField("TRN_WRK_FULLVOID_GP"	, srTRN_WRK_FULLVOID_GP	);
							    	recInTemp2.setField("WLOC_CD"				, srWLOC_CD				);

							    	rsResult1 = commDao.select(recInTemp2, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsCarschByWlocYdPntReq", logId, methodNm, "TB_YS_CARSCH 야드 포인트요구 순위"); 

							    	if (rsResult1.size() > 0) {
							    		
										szMsg 	= "개소코드 [" + srWLOC_CD + "] 야드 포인트요구 요청 결과  = [" + rsResult1.size() + "] 건";
							      		commUtils.printLog(logId, szMsg, "");
								    	
							      		String sTRN_EQP_CD 		= srTRN_EQP_CD;
							      		String sYD_CAR_SCH_ID 	= "";
							      		String sREQ_YD_PNT 		= "";
							      		
										for (int iCnt = 1; iCnt <= rsResult1.size(); iCnt++) {
											rsResult1.absolute(iCnt);
											
									    	recOutTemp1 = JDTORecordFactory.getInstance().create();
											recOutTemp1	= rsResult1.getRecord();
											
											sTRN_EQP_CD 	= commUtils.trim(recOutTemp1.getFieldString("TRN_EQP_CD"));		// 운송장비코드
											sYD_CAR_SCH_ID  = commUtils.trim(recOutTemp1.getFieldString("YD_CAR_SCH_ID"));	// 야드차량스케쥴ID
											sREQ_YD_PNT    	= commUtils.trim(recOutTemp1.getFieldString("REQ_YD_PNT"));		// 요청 야드포인트코드

											szMsg 	= "가장 최근 요청  [" + iCnt + "] 운송장비코드 [" + sTRN_EQP_CD + "] 야드차량스케쥴ID [" + sYD_CAR_SCH_ID + "] 요청 야드포인트코드 [" + sREQ_YD_PNT + "]";
								      		commUtils.printLog(logId, szMsg, "");
											
										}
										
										// 가장 마지막 운송장비코드가 가장 먼저 요청한 운송장비 해당 운송장비로 소재차량Point지시()
										
										// 요청한  운송장비코드가 대상 운송장비코드 와 다르면 교체 
										if( !srTRN_EQP_CD.equals(sTRN_EQP_CD) ) {
											
											szMsg 	= "해당 개소코드 [" + srWLOC_CD + "] 요청한 운송장비코드 [" + srTRN_EQP_CD + "] 가장 먼저 요청한 운송장비코드 [" + sTRN_EQP_CD + "] 가 달라서 대상 교체";
								      		commUtils.printLog(logId, szMsg, "");

								      		srTRN_EQP_CD 	= sTRN_EQP_CD;
											szYD_CAR_SCH_ID = sYD_CAR_SCH_ID;
											
											// 위에서 UPDATE 할 운송장비코드에 대한 야드차량스케쥴ID를 먼저 등록 했기 때문에 변경

									    	recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
											
								    	}  else {
											szMsg 	= "요청한 운송장비코드 [" + srTRN_EQP_CD + "] 와 포인트전송 운송장비코드가 동일";
								      		commUtils.printLog(logId, szMsg, "");
								    	}
										
							    	}
						      		
								}
				    			

szMsg = "[" + methodNm + "] Check 6";
commUtils.printLog(logId, szMsg, "SL");
				    			
				    			recInTemp1 = JDTORecordFactory.getInstance().create();
				    			recInTemp1.setField("YS_STK_COL_GP"	, szYS_STK_COL_GP);
				    	    	recInTemp1.setField("TRN_EQP_CD"	, srTRN_EQP_CD);
				    	    	recInTemp1.setField("YD_CAR_USE_GP"	, "L");
				    	    	recInTemp1.setField("MODIFIER"		, szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
				    	    	recInTemp1.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
				    	    	recInTemp1.setField("YD_STK_COL_ACT_STAT"	, szYD_STK_COL_ACT_STAT);
				    	    	
						    	intRtnVal = commDao.update(recInTemp1, updYdStkcolByColActStat, logId, methodNm, "TB_YS_STKCOL 등록");				    	    	
				    	    	
				    	    	szMsg = "[" + szOperationName + "] 적치열구분 [" + szYS_STK_COL_GP + "] - 운송장비코드 [" + srTRN_EQP_CD + "] 예약 성공 - 포인트 지시 : [" + szYD_PNT_CD + "]";
				    	    	commUtils.printLog(logId, szMsg, "SL");


				    	    	
szMsg = "[" + methodNm + "] Check 7";
commUtils.printLog(logId, szMsg, "SL");
				    	    	
				    	    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				    	    	this.YsCarPointinforeg2("3", "", srTRN_EQP_CD, szYS_STK_COL_GP, "", "", "R", logId, methodNm);

szMsg = "[" + methodNm + "] Check 7-1";
commUtils.printLog(logId, szMsg, "SL");
				    	    					    	    	
				    			
				    			break;
				    		}else{
szMsg	= "[" + methodNm + "] Check 8";
commUtils.printLog(logId, szMsg, "SL");
				    			
				    			szMsg	= "[" + szOperationName + "] 적치열구분[" + szYS_STK_COL_GP + "] - 운송장비코드[" + szCOL_TRN_EQP_CD + "]로 예약되어 있음";
				    			commUtils.printLog(logId, szMsg, "SL");
				    		}
				    	}else{
szMsg	= "[" + methodNm + "] Check 9";
commUtils.printLog(logId, szMsg, "SL");

				    		szMsg	= "[" + szOperationName + "] 적치열구분[" + szYS_STK_COL_GP + "] - 운송장비코드[" + srTRN_EQP_CD + "], 적치열의 활성상태[" + szYD_STK_COL_ACT_STAT + "]가 비활성화[C]가 아니므로 사용불가";
				    		commUtils.printLog(logId, szMsg, "SL");
				    	}
				    	 
				    	
				    	if(i == rsResult.size()) {
szMsg	= "[" + methodNm + "] Check 10";
commUtils.printLog(logId, szMsg, "SL");

// 2025.11.04 공차이면서 이미 다른 차량이 들어와 있는경우 야드포인트코드(YD_PNT_CD)는 대기장 이지만 적치열은 등록으로 변경
//            적치열이 있어야 차량작업관리 화면에 대기 차량으로 보임
// 2025.11.25 영차 일때 적치열 CLEAR 막음
//							if(!srTRN_WRK_FULLVOID_GP.equals("E") ) {
//					    		szYS_STK_COL_GP = "";
//							}
			    			szYD_PNT_CD = "0000";
			    			
			    			szMsg	= "[" + szOperationName + "] 차량도착위치 기본동에 가용한 차량도착Point가 존재하지 않으므로 대기장으로 지시";
			    			commUtils.printLog(logId, szMsg, "SL");
				    	}
		    		}
				}
			}
	    	
	    	
	    	
			szMsg	= "=========검색된 szCOL_WLOC_CD : " + szCOL_WLOC_CD + "===포인트 : " + szYD_PNT_CD + "======";
			commUtils.printLog(logId, szMsg, "SL");
			
			recInTemp.setField("TRN_WRK_FULLVOID_GP"		, srTRN_WRK_FULLVOID_GP); 		//영공구분
			/**********************************************************
			* 4-1-3. 차량스케줄에 포인트코드를 등록
			**********************************************************/	
			 // 차량스케줄에 포인트코드를 등록하고 소재차량Point지시 전문 전송
			if(srTRN_WRK_FULLVOID_GP.equals("E") ) {
szMsg	= "[" + methodNm + "] Check 11";
commUtils.printLog(logId, szMsg, "SL");

				
				//공차인경우
				recInTemp.setField("SPOS_WLOC_CD"		, szCOL_WLOC_CD				); 	//발지개소코드
				recInTemp.setField("YD_CARLD_STOP_LOC"	, szYS_STK_COL_GP			); 	//상차정지위치
				recInTemp.setField("YD_CARLD_PNT_WO_DT"	, commUtils.getDateTime14()	); 	//상차point지시일시
				recInTemp.setField("YD_PNT_CD1"			, szYD_PNT_CD				);
				srWLOC_CD = szCOL_WLOC_CD;                                             	//포인트지시 메세지 전송시 필요 

			} else if(srTRN_WRK_FULLVOID_GP.equals("F") ) {
szMsg	= "[" + methodNm + "] Check 12";
commUtils.printLog(logId, szMsg, "SL");

				
				//영차인경우
				recInTemp.setField("ARR_WLOC_CD"		, srWLOC_CD					);  //착지개소코드
				recInTemp.setField("YD_CARUD_STOP_LOC"	, szYS_STK_COL_GP			); 	//하차정지위치
				recInTemp.setField("YD_CARUD_PNT_WO_DT"	, commUtils.getDateTime14()	); 	//하차point지시일시
				recInTemp.setField("YD_PNT_CD3"			, szYD_PNT_CD				);
			}

szMsg	= "[" + methodNm + "] Check 13";
commUtils.printLog(logId, szMsg, "SL");

			recInTemp.setField("MODIFIER"     , "TSYSJ002"     ); 	// 수정자
			
			intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsCarschByWloc", logId, methodNm, "TB_YS_CARSCH 등록");
			if(intRtnVal <= 0) {
				recSndPnt.setField("YD_PNT_CD"			, "0000");               
// 2025.08.19 YD_MSG_NM(야드메시지) 추가	
				recSndPnt.setField("YD_MSG_NM"			, "TB_YS_CARSCH 등록 오류");

				return sndRecord = commUtils.addSndData(sndRecord,recSndPnt);
			}
			
			szMsg 	= "=========검색된 적치열 : " + srWLOC_CD + "===포인트 : " + szYD_PNT_CD + "======";
			commUtils.printLog(logId, szMsg, "SL");

			/**********************************************************
			* 4-1-4. 소재차량Point지시 전문 전송
			**********************************************************/	
szMsg	= "[" + methodNm + "] Check 14";
commUtils.printLog(logId, szMsg, "SL");
			

			//포인트지시 메세지 전송
			recInTemp = JDTORecordFactory.getInstance().create();

			recInTemp.setResultCode(logId);	//Log ID
			recInTemp.setResultMsg(methodNm);	//Log Method Name
			recInTemp.setField("JMS_TC_CD"			, "YSTSJ011");
			recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
			recInTemp.setField("TRN_EQP_CD"			, srTRN_EQP_CD);
			recInTemp.setField("WLOC_CD"			, srWLOC_CD);
			recInTemp.setField("YD_PNT_CD"			, szYD_PNT_CD);
			recInTemp.setField("PNT_WO_GP"			, "A");
			recInTemp.setField("PNT_WO_DT"			, commUtils.getDateTime14());
// 2025.08.19 YD_MSG_NM(야드메시지) 추가	
			recInTemp.setField("YD_MSG_NM"			, "소재차량도착Point요구 정상 처리 되었습니다.");

szMsg	= "[" + methodNm + "] Check 15";
commUtils.printLog(logId, szMsg, "SL");

			//전송 Data 생성
			sndRecord = commUtils.addSndData(sndRecord,recInTemp);	
		
			
		} catch(Exception e){
			
			szMsg	= "소재차량 도착 Point요구 처리 Error:" + e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			
			m_ctx.setRollbackOnly();
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
		commUtils.printLog(logId, methodNm, "S-");
		return sndRecord;
	} //end of rcvTSYSJ002()
	
	
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량도착(TSYSJ003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYSJ003(JDTORecord rcvMsg)throws DAOException  {

		/*
		 * 전문내용 : TSYSJ003(소재차량도착)
		 * TRN_EQP_CD                    	운송장비코드			CHAR	8		구내운송 차량 제원 등록 NO.
		 * ARR_WLOC_CD						착지개소코드			CHAR	5		차량이 도착한 작업 개소(공차: 상차개소코드, 영차: 하차개소코드)
		 * ARR_YD_PNT_CD					착지야드포인트코드		CHAR	4		차량이 도착한 작업 개소의 정지위치를 포인트코드로 변환
		 * TRN_WRK_FULLVOID_GP				운송작업영공구분		CHAR	1		F(영차) / E(공차)
		 * TRN_EQP_STK_CAPA					운송장비적재능력		NUMBER	22		
		 * CAR_ARR_DT						차량도착일시			CHAR	14
		 * MSG_GP							전문구분			CHAR	1		I(신규), U(수정)		
		 */
		
		String methodNm = "소재차량도착[YsCommCarTSMvSeEJBBean.rcvTSYSJ003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		JDTORecordSet rsResult          = null;
		JDTORecordSet rsWbookMtl        = null;
		JDTORecord	  sndRecord			= JDTORecordFactory.getInstance().create();
		JDTORecord recCarSch        	= null;
		JDTORecord recOutTemp        	= null;
		JDTORecord recInTemp         	= null;		
	    int intRtnVal 		   			= 0 ;
	    
	    String szMsg           			= "";
	    String srMSG_GP					= "";
	    String szOperationName			= "소재차량도착";
	    String srTRN_EQP_CD    			= "";
	    String szYD_WBOOK_ID   			= "";
	    String szYD_SCH_CD				= "";
	    String srARR_WLOC_CD   			= "";
	    String srARR_YD_PNT_CD   		= "";
	    String szYD_CAR_SCH_ID 			= "";
	    String szTRN_EQP_STK_CAPA 		= "";
	    String szYD_CAR_PROG_STAT		= "";
	    String srTRN_WRK_FULLVOID_GP	= "";
	    	    
        String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID        
        if(msgId==null || msgId.equals("")){
        	return sndRecord;
        }
        
	    try{
			commUtils.printLog(logId, methodNm, "S+");
	        szMsg	= "[" + szOperationName + "] 전문수신 : TCCODE=" + msgId ;

	        commUtils.printParam(logId + szMsg, rcvMsg);
			
			srMSG_GP      			= commUtils.trim(rcvMsg.getFieldString("MSG_GP"));
			srARR_WLOC_CD     		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 
			srARR_YD_PNT_CD   		= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 
			srTRN_EQP_CD   			= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 
			srTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); 

			if( srARR_YD_PNT_CD.equals("1Z99") ) {
	    		szMsg	= "[" + methodNm + "] 착지개소코드 ["+srARR_WLOC_CD+"], 착지포인트코드["+srARR_YD_PNT_CD+"]가 대기장이므로 업무처리 종료";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
	    	}
	    	
	    	if( srARR_WLOC_CD.equals("DMY1P"))	{
	    		szMsg	= "[" + methodNm + "] 착지개소코드 [" + srARR_WLOC_CD + "]가 가상개소코드이므로 업무처리 종료";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
	    	}
			
// 2025.10.30 체크 로직 추가	    	
			if(!srTRN_WRK_FULLVOID_GP.equals("E") && !srTRN_WRK_FULLVOID_GP.equals("F")){
	    		szMsg	= "[" + methodNm + "] 착지개소코드 ["+srARR_WLOC_CD+"], 운송작업영공구분["+srTRN_WRK_FULLVOID_GP+"]가 영차/공차가 아님. 업무처리 종료";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}	    	
			
	    	szMsg	= "[" + methodNm + "] 운송장비코드[" + srTRN_EQP_CD + "]로 차량스케줄 조회 시작";
			commUtils.printLog(logId, szMsg, "SL");
	    	
	    	//운송장비코드로 차량스케줄 조회	    	
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();

			recInTemp.setField("TRN_EQP_CD", srTRN_EQP_CD);
	    	
	    	rsResult = commDao.select(recInTemp, getYdCarschDaoTrnEqpCd, logId, methodNm, "차량스케줄을 조회"); 	
	    	
			if (rsResult == null || rsResult.size() < 0) {
				szMsg	= "[" + methodNm+"] 운송장비코드[" + srTRN_EQP_CD + "]로 차량스케줄 조회 시 : parameter error";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			} else if (rsResult.size() > 1) {
				szMsg	= "[" + methodNm + "] 차량스케줄 조회 시 오류발생 - 운송장비코드로 차량스케줄이 여러건[" + rsResult.size() + "]이 존재합니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}
	    	
	    	rsResult.first();
	    	recCarSch = rsResult.getRecord(); 
	    	
			szYD_CAR_SCH_ID     = commUtils.trim(recCarSch.getFieldString("YD_CAR_SCH_ID")); 
			szYD_CAR_PROG_STAT	= commUtils.trim(recCarSch.getFieldString("YD_CAR_PROG_STAT")); 
			
			String isUnMatched = "N";
			if(srTRN_WRK_FULLVOID_GP.equals("F")){
	    		//지시포인트와 도착포인트가 틀리면 ERROR 처리
	    		if(!commUtils.trim(recCarSch.getFieldString("YD_PNT_CD3")).equals(srARR_YD_PNT_CD)) {
					szMsg	= "[" + methodNm + "] 지시포인트와 도착된 포인트가 틀립니다.";
		    		commUtils.printLog(logId, szMsg, "SL");
		    		isUnMatched = "Y";
// 2025.10.30 기존 로직은 로그만 담고 SKIP 했지만 오류 처리로 변경
					throw new Exception(szMsg);
		    	}
// 2025.10.22 영차 도착이면 YD_CAR_PROG_STAT (야드차량진행상태) 가  하차출발(A) 아니면 오류
				if( !szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARUD_LEV)) {
					szMsg	= "[" + methodNm + "] 운송장비코드[" + srTRN_EQP_CD + "]로 차량스케줄[" + szYD_CAR_SCH_ID + "] 하차출발상태(A) 가 아닙니다. - 차량진행상태[" + szYD_CAR_PROG_STAT + "]" + "종료함";
					commUtils.printLog(logId, szMsg, "SL");
					throw new Exception(szMsg);
				}
	    		
			} else {
	    		//지시포인트와 도착포인트가 틀리면 ERROR 처리
	    		if(!commUtils.trim(recCarSch.getFieldString("YD_PNT_CD1")).equals(srARR_YD_PNT_CD)) {
					szMsg	= "[" + methodNm + "] 지시포인트와 도착된 포인트가 틀립니다.";
		    		commUtils.printLog(logId, szMsg, "SL");
					throw new Exception(szMsg);
		    	}
			}	
			szMsg	= "[" + methodNm + "] 운송장비코드[" + srTRN_EQP_CD + "]로 차량스케줄[" + szYD_CAR_SCH_ID + "], 차량진행상태[" + szYD_CAR_PROG_STAT + "] ";
			commUtils.printLog(logId, szMsg, "SL");
			
			if( !srMSG_GP.equals("U") ) {
				//도착전문이 수정이 아니고 상차도착/하차도착인 경우에는 업무종료처리
				if( szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARLD_ARR) || szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARUD_ARR) ) {
					szMsg	= "[" + methodNm + "] 운송장비코드[" + srTRN_EQP_CD + "]로 차량스케줄[" + szYD_CAR_SCH_ID + "]이 이미 도착처리된 상태입니다. - 차량진행상태[" + szYD_CAR_PROG_STAT + "]" + "종료함";
					commUtils.printLog(logId, szMsg, "SL");
					throw new Exception(szMsg);
				}
			} 

			
			String szSPOS_WLOC_CD = commUtils.trim(recCarSch.getFieldString("SPOS_WLOC_CD"));
			
	    	//작업예약정보에서
	    	//운송장비코드 , 야드차량사용구분으로  조회 
	    	//해당된 작업예약 재료 정보를 가지고 온다
			rsWbookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	recInTemp  = JDTORecordFactory.getInstance().create();
	    
//////////////////////////////////////////////////////////////////////////////////////////////////////	    	
// 2025.11.13 공차 이면서 착지 개소 코드가 빌렛정정 보급대 크래들 (밴드쏘) GETR42 S3Y99 2E04이면 크레인 작업을 할수 없기 때문에 작업 예약 없음
	    	if(srTRN_WRK_FULLVOID_GP.equals("E") && srARR_WLOC_CD.equals("S3Y99"))	{
	    		
	    		szMsg	= "[" + methodNm + "] 남42문 착지개소코드 [" + srARR_WLOC_CD+"], 운송작업영공구분[" + srTRN_WRK_FULLVOID_GP + "] 이므로 크레인 작업 예약 없음";
				commUtils.printLog(logId, szMsg, "SL");
	    		
	    	} else if(srTRN_WRK_FULLVOID_GP.equals("F") && srARR_WLOC_CD.equals("S3Y99"))	{
	    		// 영차 남42문 도착 오류  
	    		szMsg	= "[" + methodNm + "] 남42문 착지개소코드 [" + srARR_WLOC_CD + "], 운송작업영공구분[" + srTRN_WRK_FULLVOID_GP + "] 이므로 오류 처리";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
	    		
	    	}
	    	else {
		    	recInTemp.setField("TRN_EQP_CD",    srTRN_EQP_CD);
			    
		    	rsWbookMtl = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookMtlbyTrnEqpCd", logId, methodNm, "운송장비코드로 작업예약을 조회"); 
				
		    	if (rsWbookMtl == null || rsWbookMtl.size() < 0 ) {
					szMsg	=	"[" + methodNm + "] 운송장비코드 [" + srTRN_EQP_CD + "]로 작업예약 조회 시 : parameter error";
					commUtils.printLog(logId, szMsg, "SL");
					throw new Exception(szMsg);
				} else if (rsWbookMtl.size() == 0 ){
// 2025.09.30 운송장비코드로 작업예약 조회시 없으면 개소코드로 조회
			    	recInTemp.setField("WLOC_CD",    srARR_WLOC_CD);
				    
			    	rsWbookMtl = commDao.select(recInTemp, getWorkBookMtlbyWlocCd, logId, methodNm, "개소코드로 작업예약을 조회"); 
			    	if (rsWbookMtl.size() > 0 ){
				    	rsWbookMtl.absolute(1);
						
				    	recOutTemp = JDTORecordFactory.getInstance().create();
				    	recOutTemp.setRecord(rsWbookMtl.getRecord());
						
						szYD_WBOOK_ID	= commUtils.trim(recOutTemp.getFieldString("YD_WBOOK_ID")); 
						szYD_SCH_CD    	= commUtils.trim(recOutTemp.getFieldString("YD_SCH_CD")); 
			    	}
					
				} else {
			    	rsWbookMtl.absolute(1);
					
			    	recOutTemp = JDTORecordFactory.getInstance().create();
			    	recOutTemp.setRecord(rsWbookMtl.getRecord());
					
					szYD_WBOOK_ID	= commUtils.trim(recOutTemp.getFieldString("YD_WBOOK_ID")); 
					szYD_SCH_CD    	= commUtils.trim(recOutTemp.getFieldString("YD_SCH_CD")); 
				}
	    	}
//////////////////////////////////////////////////////////////////////////////////////////////////////

			/**********************************************************
			* 1. 공차도착  및 영차도착 실적
			**********************************************************/			

	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("WLOC_CD",   srARR_WLOC_CD);
	    	recInTemp.setField("YD_PNT_CD", srARR_YD_PNT_CD);

	    	//착지개소코드와 착지야드포인트코드로 적치열을 조회한다.
	    	szMsg	=	"[" + methodNm + "] 수신된 착지개소코드 [" + srARR_WLOC_CD + "]와 수신된 착지야드포인트코드[" + srARR_YD_PNT_CD + "]로 적치열을 조회 시작";
			commUtils.printLog(logId, szMsg, "SL");

// 2026.01.29 Query 변경			
//	    	rsResult = commDao.select(recInTemp, getYdStkcolWLocCdandPntCd, logId, methodNm, "적치열 조회"); 
	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCdG", logId, methodNm, "개소코드로 적치열 조회");  
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		szMsg	= "[" + methodNm + "] 수신된 착지개소코드[" + srARR_WLOC_CD + "]와 수신된 착지야드포인트코드[" + srARR_YD_PNT_CD + "] 적치열 조회 시 적치열이 존재하지 않습니다.";
	    		commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
	    	}
	    	
	    	rsResult.absolute(1);
	    	JDTORecord rcvMsgCol = JDTORecordFactory.getInstance().create();
	    	rcvMsgCol.setRecord(rsResult.getRecord());
	    	
    		//공차도착실적
	    	//재송신 된 경우는 SKIP
	    	if(!commUtils.trim(rcvMsgCol.getFieldString("TRN_EQP_CD")).equals(srTRN_EQP_CD)) {
	    		if(commUtils.trim(rcvMsgCol.getFieldString("YD_STK_COL_ACT_STAT")).equals("L")) {
					szMsg	=	"[" + methodNm + "] 차량정지위치가 이미 활성상태입니다.";
		    		commUtils.printLog(logId, szMsg, "SL");
					throw new Exception(szMsg);
		    	}
	    	}	
    		if(commUtils.trim(rcvMsgCol.getFieldString("YD_STK_COL_ACT_STAT")).equals("N")) {
    			szMsg	=	"[" + methodNm + "] 차량정지위치가 사용 불가상태입니다.";
	    		commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
	    	}
	    	

	    	if(srTRN_WRK_FULLVOID_GP.equals("E") && srARR_WLOC_CD.equals("S3Y99"))	{
//////////////////////////////////////////////////////////////////////////////////////////////////////
//2025.11.13 공차 이면서 착지 개소 코드가 빌렛정정 보급대 크래들 (밴드쏘) GETR42 S3Y99 2E04이면  
				/**********************************************************
				* 1.차량스케줄 갱신 (실적등록된 상차위치로)
				* 2.차량 도착열(COL) 등록
				* 3.차량포인트 등록
				* 4.적치베드 활성화
				* 5.적치단 활성화
				* ?.저장위치 제원 야드L2로 전송
				* 7.차량 상차 재료 생성
				* 8.준비스케줄 삭제
				* 9.상차 완료 전송
				**********************************************************/   
	    		
	    		szMsg	= "[" + methodNm + "] 남42문 착지개소코드 [" + srARR_WLOC_CD + "], 운송작업영공구분[" + srTRN_WRK_FULLVOID_GP + "] 이므로 크레인 작업 없이 상차 완료 등록";
				commUtils.printLog(logId, szMsg, "SL");
				
	    		sndRecord = this.proc42GateCarArr(logId, rcvMsg, recCarSch , rcvMsgCol);
	    		intRtnVal = Integer.parseInt(sndRecord.getTaskCode());
	    		
	    		szMsg	= "[" + methodNm + "] proc42GateCarArr -----> intRtnVal";
   				commUtils.printLog(logId, szMsg, "SL");
   				
   				if(intRtnVal == -100) {							
	    			return sndRecord;
	    		} else if( intRtnVal <= -1 ) {
	    			return sndRecord;
	    		}
				
//////////////////////////////////////////////////////////////////////////////////////////////////////
	    		
	    	} else if(srTRN_WRK_FULLVOID_GP.equals("E")){
	    		rcvMsg.setField("YD_CARLD_WRK_BOOK_ID"	, szYD_WBOOK_ID);
	    		rcvMsg.setField("YD_SCH_CD"				, szYD_SCH_CD);
	    		
	    		/**********************************************************
			     * 1.작업예약 생성
			     * 2.준비스케줄 삭제
			     * 3.차량스케줄 갱신 (실적등록된 상차위치로)
			     * 4.차량 도착열(COL) 등록
			     * 5.차량포인트 등록
				 * 6.적치베드 활성화
				 * 7.적치단 활성화
				 * 8.저장위치 제원 야드L2로 전송
				 * 9.크레인 스케줄 호출
			     **********************************************************/   
	    		szMsg	= "[" + methodNm + "] procLDMatlCarArr 전";
   				commUtils.printLog(logId, szMsg, "SL");

   				sndRecord = this.procLDMatlCarArr(logId, rcvMsg, recCarSch , rcvMsgCol);

	    		szMsg	= "[" + methodNm + "] procLDMatlCarArr 후";
   				commUtils.printLog(logId, szMsg, "SL");
   				
   				intRtnVal = Integer.parseInt(sndRecord.getTaskCode());
	    		
	    		szMsg	= "[" + methodNm + "] procLDMatlCarArr -----> intRtnVal";
   				commUtils.printLog(logId, szMsg, "SL");
   				
   				if(intRtnVal == -100) {							
	    			return sndRecord;
	    		} else if( intRtnVal <= -1 ) {
	    			return sndRecord;
	    		}

	    	}else{
	    		//영차도착실적

	    		rcvMsg.setField("YD_CARUD_WRK_BOOK_ID"	, szYD_WBOOK_ID);
	    		rcvMsg.setField("TRN_EQP_STK_CAPA"		, szTRN_EQP_STK_CAPA);
	    		rcvMsg.setField("IS_UNMATCHED"			, isUnMatched);
	    		
	    		/**********************************************************
			     * 1.하차 작업예약 생성(하차동이 변경된 경우만 )
			     * 2.차량스케줄 갱신
			     * 3.작업예약상세 갱신
			     * 4.차량 도착열(COL) 등록
			     * 5.차량포인트 등록
				 * 6.적치베드 활성화
				 * 7.적치단 활성화
				 * 8.저장위치 제원 야드L2로 전송
				 * 9.저장품 제원 야드L2로 전송
				 * 9.크레인 스케줄 호출
			     **********************************************************/   	    		
	    		sndRecord = this.procUDMatlCarArr(logId, rcvMsg, rsWbookMtl, recCarSch, rcvMsgCol);
	    		
	    		intRtnVal = Integer.parseInt(sndRecord.getTaskCode());

	    		szMsg	= "[" + methodNm + "] procUDMatlCarArr -----> intRtnVal";
   				commUtils.printLog(logId, szMsg, "SL");
	    		
	    		if(intRtnVal == -100) {							//하차정지위치가 이미 활성상태입니다.
	    			return sndRecord;
	    		} else if( intRtnVal <= -1 ) {
	    			return sndRecord;
	    		}
	    	}
	    	
			
		} catch(Exception e){
			
			szMsg	= "소재차량 도착처리 Error:" + e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			m_ctx.setRollbackOnly();
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
		commUtils.printLog(logId, methodNm, "S-");
		return sndRecord;
			
	} //end of rcvTSYSJ003()	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량출발(TSYSJ004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYSJ004(JDTORecord rcvMsg)throws DAOException  {

		/*
		 * 전문내용 : TSYDJ004(소재차량출발)
		 * TRN_EQP_CD                    	운송장비코드			CHAR	8		구내운송 차량 제원 등록 NO.	
		 * SPOS_WLOC_CD                  	발지개소코드			CHAR	5		공차 - 가상개소코드, 영차 - 도착지개소코드	
		 * SPOS_YD_PNT_CD                	발지야드포인트코드		CHAR	4		공차 - 가샹야드포인트코드, 영차 - 도착지야드포인트코드	
		 * ARR_WLOC_CD                   	착지개소코드			CHAR	5		공차 - 도착지개소코드	
		 * ARR_YD_PNT_CD                 	착지야드포인트코드		CHAR	4		공차, 영차 - 야드포인트전송전 이라 값 없음
		 * TRN_WRK_FULLVOID_GP				운송작업영공구분		CHAR	1			
		 * TRN_EQP_STK_CAPA              	운송장비적재능력		NUMBER	22,2			
		 * CARUD_PAP_LEV_TT              	하차지출발시각		DATE
		 * YD_WO_CNCL_YN					야드지시취소여부		CHAR	1
		 * TRN_WRK_MTL_GP					운송작업재료구분		CHAR	1		2025.08.13 추가
		 * WLOC_CD							개소코드			CHAR	5		2025.08.13 추가
		 * YD_PNT_CD						야드포인트코드		CHAR	4		2025.08.13 추가
		 * CARLD_SH							상차매수			NUMBER	22
		 * SSTL_NO1~10						특수강재료번호1~10	CHAR	12		2025.08.13 전문에 없음
		 *                                                                  2025.08.29 영차인 경우 재료번호 수신 (오원재 책임)
		 */
		
		String methodNm = "소재차량출발[YsCommCarTSMvSeEJBBean.rcvTSYSJ004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		JDTORecordSet	rsResult				= null;
		JDTORecord		sndRecord				= JDTORecordFactory.getInstance().create();
		JDTORecord		recOutTemp				= null;
		JDTORecord		recInTemp				= null;	
		JDTORecord		recPara					= null;	
		
	    int intRtnVal 		   					= 0 ;
	    
	    String 			szMsg					= "";
		JDTORecordSet	rsStkCol	   			= null;
		JDTORecord    	recInPara				= null;
	    int				intLevLocGp     	    = 0;
	    String			szMethodName    		= "procMatlCarLev";
	    String 			szYD_CARLD_STOP_LOC		= "";
	    String 			szYD_EQP_ID				= "";
	    String			szYD_CARLD_WRK_BOOK_ID	= "";
	    String			szYD_CARUD_WRK_BOOK_ID	= "";
	    
	    String 			szYD_CAR_SCH_ID			= "";
	    String 			szYD_CAR_SCH_ID_CUR		= "";
	    String 			szYD_CAR_PROG_STAT		= "";
		JDTORecordSet 	rsWrkBookMtl			= null;
		String 			szMSG_ID				= "";
		String 			szCARSCH_SPOS_WLOC_CD	= "";
		String 			szYD_GP					= null;
		
		// WC 추가 변수
		String			srCARLD_SH				= null;
		
		//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
		String msgId = commUtils.getMsgId(rcvMsg); 
        if(msgId==null || msgId.equals("")){
        	return sndRecord;
        }
        
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "소재차량출발 수신 ", rcvMsg);
			
	    	/**********************************************************
	    	 * 1. 파라미터 확인
	    	 **********************************************************/	    	
	    	//운송장비코드, 발지개소코드, 발지야드포인트코드, 착지개소코드, 착지야드포인트코드
			String srTRN_EQP_CD     		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")			); 
			String srSPOS_WLOC_CD     		= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD")			); 
			String srSPOS_YD_PNT_CD        	= commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD")		);
			String srARR_WLOC_CD   			= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")			); 
			String srARR_YD_PNT_CD        	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")			);
			String srTRN_WRK_FULLVOID_GP  	= commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")	); 
			String srTRN_EQP_STK_CAPA      	= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA")		);
// 2025.08.19 TRN_WRK_MTL_GP(운송작업재료구분) 추가	
			String srTRN_WRK_MTL_GP      	= commUtils.trim(rcvMsg.getFieldString("TRN_WRK_MTL_GP")		);
			
			
		   	//화면에서 넘어오는 직상차 구분 - BACKUP 실적처리시 포함
			String srYD_DIRECT_CARLD_GP 	= commUtils.trim(rcvMsg.getFieldString("YD_DIRECT_CARLD_GP")	); 
			String srIS_EJB_CALL 			= commUtils.trim(rcvMsg.getFieldString("IS_EJB_CALL")			);
			String srYD_AUTO_LOT 			= commUtils.trim(rcvMsg.getFieldString("YD_AUTO_LOT")			); // 자동LOT/수동LOT 편성 판단 변수
	    	
			// 출발실적 수신시 발지개소 코드 및 착지개소 코드 체크
			if( srSPOS_WLOC_CD.equals("") || srARR_WLOC_CD.equals("") ) {
				szMsg 	= "[JSP Facade] 소재차량출발실적 수신 시 발지개소코드 [" + srSPOS_WLOC_CD + "] 나 착지개소코드 [" + srARR_WLOC_CD + "] 가 없습니다." ;
				commUtils.printLog(logId, szMsg, "SL");
// 2025.10.30 바로 return 막고 Exception 처리				
//	    		return sndRecord;
				throw new Exception(szMsg);
			}
			else {
		    	szMsg 	= "수신발지개소코드 [" + srSPOS_WLOC_CD + "], 수신발지개소POINT코드 [" + srSPOS_YD_PNT_CD + "] 로 야드에서 관리되는 적치열구분 조회 시작";
		    	commUtils.printLog(logId, szMsg, "SL");
			}
			
// 2025.10.30 체크 로직 추가	    	
			if(!srTRN_WRK_FULLVOID_GP.equals("E") && !srTRN_WRK_FULLVOID_GP.equals("F")){
	    		szMsg	= "[" + methodNm + "] 착지개소코드 [" + srARR_WLOC_CD + "], 운송작업영공구분[" + srTRN_WRK_FULLVOID_GP + "]가 영차/공차가 아님. 업무처리 종료";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}	    	
			
// 2025.12.02 L2 -> L3 구내운송 영차 도착 
			String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "006", "*");

			if("Y".equals(sApplyYnPI) && "F".equals(srTRN_WRK_FULLVOID_GP)){
				szMsg = "TB_YS_RULE 006_L2 -> L3 구내운송 영차 출발  체크";
	      		commUtils.printLog(logId, szMsg, "");

// 2025.12.02 발지가 L2(등록되지않은 개소코드) 이고 착지가  L3(등록된 개소코드) 이면 L2 영차 도착 실행
	      		if(  !"Y".equals(commDao.ApplyYnPI(logId, methodNm, "GE0005", srSPOS_WLOC_CD, "*"))
	      		  &&  "Y".equals(commDao.ApplyYnPI(logId, methodNm, "GE0005", srARR_WLOC_CD, "*"))){ 
	      			
					szMsg = "발지 [" + srSPOS_WLOC_CD + "] 착지 [" + srARR_WLOC_CD + "] L2 -> L3 구내운송 영차실적 처리 시작";
		      		commUtils.printLog(logId, szMsg, "");
	      			
		      		sndRecord	= this.procL2MatlCarArr(logId, rcvMsg);

					szMsg = "발지 [" + srSPOS_WLOC_CD + "] 착지 [" + srARR_WLOC_CD + "] L2 -> L3 구내운송 영차실적 처리 완료";
		      		commUtils.printLog(logId, szMsg, "");

		    		commUtils.printLog(logId, methodNm, "S-");
		    		return sndRecord;
		      		
	      		} else {

					szMsg = "발지 [" + srSPOS_WLOC_CD + "] 착지 [" + srARR_WLOC_CD + "] 기존 영차 출발 실적 처리 시작";
		      		commUtils.printLog(logId, szMsg, "");
	      			
	      		}
				
			}
			
			
	    	szMsg	= "[" + methodNm + "] 운송장비코드[" + srTRN_EQP_CD + "]로 차량스케줄 조회 시작";
			commUtils.printLog(logId, szMsg, "SL");
			
			/**********************************************************
	    	 * 2.운송장비코드로 차량스케줄 조회
	    	 **********************************************************/
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp 	= JDTORecordFactory.getInstance().create();			
			recInTemp.setField("TRN_EQP_CD", srTRN_EQP_CD);
			
			rsResult = commDao.select(recInTemp, getYdCarschDaoTrnEqpCd, logId, methodNm, "차량스케줄을 조회"); 
			if (rsResult == null || rsResult.size() < 0) {
				szMsg	= "[" + methodNm + "] 운송장비코드[" + srTRN_EQP_CD + "]로 차량스케줄 조회 시 : parameter error";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			} else if (rsResult.size() > 1) {
				szMsg	= "[" + methodNm + "] 차량스케줄 조회 시 오류발생 - 운송장비코드로 차량스케줄이 여러건[" + rsResult.size() + "]이 존재합니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}
			
			// 설비 ID 기본 설비ID(구내운송차량)로 설정
			szYD_EQP_ID     = YsConstant.YD_TS_CAR_EQP_ID;
			
			/**********************************************************
	    	 * 3.운송장비코드로 차량스케줄 조회시 차량 SCH 있는 경우
	    	 **********************************************************/
			if( rsResult.size() == 1 ) {			

				rsResult.absolute(1);
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord());
				
				srTRN_EQP_CD     		= commUtils.trim(recOutTemp.getFieldString("TRN_EQP_CD"       		)); 
				szYD_CAR_PROG_STAT 		= commUtils.trim(recOutTemp.getFieldString("YD_CAR_PROG_STAT" 		));  	// 차량진행상태
				szYD_CAR_SCH_ID 		= commUtils.trim(recOutTemp.getFieldString("YD_CAR_SCH_ID" 			)); 	// 차량스케줄ID
				szCARSCH_SPOS_WLOC_CD 	= commUtils.trim(recOutTemp.getFieldString("SPOS_WLOC_CD" 			)); 	// 발지개소코드
				szYD_CARLD_WRK_BOOK_ID	= commUtils.trim(recOutTemp.getFieldString("YD_CARLD_WRK_BOOK_ID" 	)); 	// 상차작업예약
				szYD_CARUD_WRK_BOOK_ID	= commUtils.trim(recOutTemp.getFieldString("YD_CARUD_WRK_BOOK_ID" 	)); 	// 하차작업예약
				szYD_GP         		= commUtils.trim(recOutTemp.getFieldString("YD_AIM_YD_GP" 			));		// 목적지 야드구분
				
				szYD_CAR_SCH_ID_CUR = szYD_CAR_SCH_ID;
				
				szMsg = "[" + methodNm + "] 차량스케줄[" + szYD_CAR_SCH_ID + "] - 차량진행상태[" + szYD_CAR_PROG_STAT + "], "
				      + "발지개소코드[" + szCARSCH_SPOS_WLOC_CD + "], 상차작업예약[" + szYD_CARLD_WRK_BOOK_ID + "], 하차작업예약[" + szYD_CARUD_WRK_BOOK_ID + "]";
				
				commUtils.printLog(logId, szMsg, "SL");

				/**********************************************************
		    	 * 2-1.수신 DATE CHECK
				 *  
		    	 **********************************************************/
				
				 if( srTRN_WRK_FULLVOID_GP.equals("E")  ) {  //
					 
					sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "005", "*");

					if("Y".equals(sApplyYnPI)){
// 2025.12.02 구내운송 차량 하차지 도착후 출발시 하차 완료 아닌 상태라도 하차후 출발 처리 가능 하게 개선						
						 if(  szYD_CAR_PROG_STAT.equals("1")
						   || szYD_CAR_PROG_STAT.equals("B")
						   || szYD_CAR_PROG_STAT.equals("C")
						   || szYD_CAR_PROG_STAT.equals("D")
						   || szYD_CAR_PROG_STAT.equals("E")
						   ) {
							szMsg = "TB_YS_RULE 005_구내운송_하자후_공차출발";
				      		commUtils.printLog(logId, szMsg, "");
							 
							 // 야드차량진행상태 하차완료(E) 설정
							 szYD_CAR_PROG_STAT = "E";
						 }
						 
						 else {
							szMsg	= "[" + methodNm + "] 상차도착 및 작업중인 상태입니다..";
							commUtils.printLog(logId, szMsg, "SL");
							
							throw new Exception(szMsg); 
						 }	 
					}
					else {
						 if(szYD_CAR_PROG_STAT.equals("1")||szYD_CAR_PROG_STAT.equals("E")) {
						 }
						 
						 else {
							szMsg	= "[" + methodNm + "] 상차도착 및 작업중인 상태입니다..";
							commUtils.printLog(logId, szMsg, "SL");
							
							throw new Exception(szMsg); 
						 }	 
					}
				} else if( srTRN_WRK_FULLVOID_GP.equals("F")  ) {  //
// 2025.10.22 영차 출발이면 YD_CAR_PROG_STAT (야드차량진행상태) 가  상차완료(5) 아니면 오류
// 2026.04.06 오원재 책임 요청 으로 영차 출발이면 상태 체크에 YD_CAR_PROG_STAT (야드차량진행상태) 가  하차출발(A) 추가
//					if(!szYD_CAR_PROG_STAT.equals("5") && !szYD_CAR_PROG_STAT.equals("A") ) {
 					if("5".equals(szYD_CAR_PROG_STAT) || "A".equals(szYD_CAR_PROG_STAT) ) {
 						// 상차완료(5), 하차출발(A) 상태
						szMsg	= "[" + methodNm + "] 운송장비코드[" + srTRN_EQP_CD + "]로 차량스케줄[" + szYD_CAR_SCH_ID + "] 차량진행상태[" + szYD_CAR_PROG_STAT + "]";
						commUtils.printLog(logId, szMsg, "SL");
						
 					} else {
//						szMsg	= "[" + methodNm + "] 운송장비코드[" + srTRN_EQP_CD + "]로 차량스케줄[" + szYD_CAR_SCH_ID + "] 상차완료(5) 상태가 아닙니다. - 차량진행상태[" + szYD_CAR_PROG_STAT + "]" + "종료함";
						szMsg	= "[" + methodNm + "] 운송장비코드[" + srTRN_EQP_CD + "]로 차량스케줄[" + szYD_CAR_SCH_ID + "] 상차완료(5), 하차출발(A) 상태가 아닙니다. - 차량진행상태[" + szYD_CAR_PROG_STAT + "]" + "종료함";
						commUtils.printLog(logId, szMsg, "SL");
						
						throw new Exception(szMsg);
						
					}
					
//					if(szYD_CAR_PROG_STAT.equals("5")||szYD_CAR_PROG_STAT.equals("A")) {
//					} else {
//						szMsg	= "[" + methodNm + "] 상차완료 및 하차출발 상태가 아닙니다...";
//						commUtils.printLog(logId, szMsg, "SL");
//						
//						throw new Exception(szMsg);
//					}
				}
				
				/**********************************************************
		    	 * 3-1.야드차량진행상태(YD_CAR_PROG_STAT)이 하차완료인 경우
				 *  차량이송소재 및 차량스케줄 종료)
		    	 **********************************************************/
				if(szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARUD_CMPL)){
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recInTemp.setField("DEL_YN", "Y");
					recInTemp.setField("MODIFIER", szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
					
					//차량이송소재 종료
					
					commDao.update(recInTemp, updDelYnCarFtmvMtl, logId, methodNm, "TB_YS_CARFTMVMTL 종료");
		            
		        	//차량스케줄 종료
					intRtnVal = commDao.update(recInTemp, updDelYnCarSch, logId, methodNm, "TB_YS_CARSCH 종료");
					
					
					szMsg	= "[" + methodNm + "] 하차완료된 차량스케줄[" + szYD_CAR_SCH_ID + "] 종료 성공 ";
					commUtils.printLog(logId, szMsg, "SL");
						
				}
				/**********************************************************
		    	 * 3-2.상차출발이고 공차인 경우
		    	 **********************************************************/
				else if( szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARLD_LEV) && srTRN_WRK_FULLVOID_GP.equals("E") ) {  //1
					/**********************************************************
			    	 * 3-2-1.착지개소코드 및 발지개소코드가 동일 && 발지개소POINT = 1Z99(대기장) -> 중복 상차 출발 -> 업무종료
			    	 **********************************************************/
					if( srARR_WLOC_CD.equals(szCARSCH_SPOS_WLOC_CD) ) {
						if( srSPOS_YD_PNT_CD.equals("1Z99") || srSPOS_YD_PNT_CD.equals("0000") || srSPOS_YD_PNT_CD.equals("")){ // 발지개소POINT = 1Z99(대기장)
							
							szMsg	= "[" + methodNm + "] 대기장[1Z99]에서 공차출발 시 소재차량도착Point요구 모듈 호출 시작";
							commUtils.printLog(logId, szMsg, "SL");

							//소재차량도착Point요구 호출
							recPara = JDTORecordFactory.getInstance().create();
							recPara.setResultCode(logId);	//Log ID
							recPara.setResultMsg(methodNm);	//Log Method Name
							recPara.setField("JMS_TC_CD",               YsConstant.YSYSJ901);
							recPara.setField("JMS_TC_CREATE_DDTT",      commUtils.getDateTime14()); //JMSTC생성일시
							recPara.setField("TRN_EQP_CD",              srTRN_EQP_CD);
							recPara.setField("WLOC_CD",                 szCARSCH_SPOS_WLOC_CD);
							recPara.setField("TRN_WRK_FULLVOID_GP",     srTRN_WRK_FULLVOID_GP);
							recPara.setField("PNT_DMD_DT",              commUtils.getDateTime14());
							
							//소재차량도착Point요구 호출
							sndRecord = commUtils.addSndData(sndRecord, recPara);	
							
							return sndRecord;
						} 
					
					} 
					/**********************************************************
			    	 * 3-2-2.정보 이상 -> Data Clear(예약POINT제거/차량스케줄 삭제/자업예약삭제/준비스케줄복구) : 발생-상차출발 재 수신 
			    	 **********************************************************/
					else {		
						/**********************************************************
				    	 * 3-2-2-1. 예약된 차량 도착 위치 POINT 삭제 처리
				    	 **********************************************************/
						szMsg	= "[" + methodNm + "] 운송장비코드로 예약된 차량정비Point 삭제 시작";
						commUtils.printLog(logId, szMsg, "SL");
						 						
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("TRN_EQP_CD",	srTRN_EQP_CD); // 운송장비코드

						intRtnVal = commDao.update(recInTemp, updYdStkcolTrnEqpCdToNull, logId, methodNm, "TB_YS_STKCOL CLEAR");
						
						szMsg 	= "[" + methodNm + "] 운송장비코드로 예약된 차량정비Point 삭제 완료 - 반환값 : " + intRtnVal;
						commUtils.printLog(logId, szMsg, "SL");
						
						/**********************************************************
				    	 * 3-2-2-2. 차량 스케줄 삭제 처리
				    	 **********************************************************/

						szMsg 	= "[" + methodNm + "] 차량스케줄[" + szYD_CAR_SCH_ID + "]이 존재하므로 삭제 시작";
						commUtils.printLog(logId, szMsg, "SL");
						
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
						recInTemp.setField("DEL_YN", 		"Y");
						recInTemp.setField("MODIFIER",		szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);

						intRtnVal = commDao.update(recInTemp, updDelYnCarSch, logId, methodNm, "TB_YS_CARSCH 종료");
						
						szMsg 	= "[" + methodNm + "] 차량스케줄[" + szYD_CAR_SCH_ID + "]이 존재하므로 삭제 완료 - 메세지 : " + intRtnVal;
						commUtils.printLog(logId, szMsg, "SL");
						
						/**********************************************************
				    	 * 3-2-2-3. 작업예약 및 작업예약 재료 삭제 처리--상차정보
				    	 **********************************************************/
						if( !szYD_CARLD_WRK_BOOK_ID.equals(""))	{	
							
							/**********************************************************
					    	 * 3-2-2-3-1. 작업예약재료 삭제 처리
					    	 **********************************************************/
							szMsg 	= "[" + methodNm + "] 상차작업예약[" + szYD_CARLD_WRK_BOOK_ID + "]이 존재하므로 삭제 시작";
							commUtils.printLog(logId, szMsg, "SL");
							
							//szRtnMsg	= YdCommonUtils.delYdWrkbookNMtl(szYD_CARLD_WRK_BOOK_ID, szMethodName);
							recInTemp = JDTORecordFactory.getInstance().create();
			    	    	recInTemp.setField("YD_WBOOK_ID", 	   	szYD_CARLD_WRK_BOOK_ID);
			    	    	recInTemp.setField("MODIFIER", 	   		szMethodName.substring(0, 10));
			    	    	recInTemp.setField("DEL_YN", 	   		"Y");			    	    	

			    	    	commDao.update(recInTemp, updDelYnWrkBookMtl, logId, methodNm, "TB_YS_WRKBOOKMTL 삭제");

							/**********************************************************
					    	 * 2-2-2-3-2. 작업예약 삭제 처리
					    	 **********************************************************/

			    	    	commDao.update(recInTemp, updDelYnWrkBook, logId, methodNm, "TB_YS_WRKBOOK 삭제");

							/**********************************************************
					    	 * 2-2-2-3-3. 준비스케줄 종료
					    	 **********************************************************/							
							/**********************************************************
							* 2. 준비스케줄 종료
							**********************************************************/
			    	    	// 준비재료 종료 - 

			    	    	commDao.update(recInTemp, updCommPrepMtlRcvr, logId, methodNm, "TB_YS_PREPMTL 복원");	

			    	    	// 준비스케줄 종료 - 

			    	    	commDao.update(recInTemp, updCommPrepSchRcvr, logId, methodNm, "TB_YS_PREPSCH 복원");	

							
							szMsg 	= "[" + methodNm + "] 상차작업예약[" + szYD_CARLD_WRK_BOOK_ID + "]이 존재하므로 삭제 완료";
							commUtils.printLog(logId, szMsg, "SL");
						}
					}		
				}
			}

			/**********************************************************
	    	 * 4.각강 입고 체크/CARLD_SH > 0(구내운송 -> 하차출발/영차) - 저장품 정보 존재하지 않음 - WC
	    	 **********************************************************/			
			// 전문으로부터 CARLD_SH 가져오기
			srCARLD_SH = commUtils.trim(rcvMsg.getFieldString("CARLD_SH"));	
			
			szMsg 	= "[" + methodNm + "] 각강/빌렛 입고 수량 CARLD_SH : [" + srCARLD_SH + "]";
			commUtils.printLog(logId, szMsg, "SL");
			
			
			/**********************************************************
	    	 * 5.출발지 적치열 베드/단 정보 체크
	    	 **********************************************************/			
			rsStkCol 	= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp 	= JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("WLOC_CD",   srSPOS_WLOC_CD);
	    	recInTemp.setField("YD_PNT_CD", srSPOS_YD_PNT_CD);

// 2026.01.29 Query 변경			
//	    	rsStkCol = commDao.select(recInTemp, getYdStkcolWLocCdandPntCd, logId, methodNm, "적치열 조회"); 
	    	rsStkCol = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCdG", logId, methodNm, "개소코드로 적치열 조회");  
	    	intLevLocGp = rsStkCol.size();	    	
	    	if (rsStkCol == null || intLevLocGp == 0) {
	    		szMsg	= "[" + methodNm + "] 발지개소[" + srSPOS_WLOC_CD + "] 및 포인트 코드[" + srSPOS_YD_PNT_CD + "]가 타공정코드가 아니고 대기장입니다.";
	    		commUtils.printLog(logId, szMsg, "SL");
	    	}
	    	
			/**********************************************************
	    	 * 6.출발지 정보 CLEAR / 비활성화 상태(YD_STK_COL_ACT_STAT = C)로 업데이트
	    	 **********************************************************/
	    	if(intLevLocGp > 0) {
	    		
	    		rsStkCol.absolute(1);
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsStkCol.getRecord());		    	
		    	szYD_CARLD_STOP_LOC     	= commUtils.trim(recOutTemp.getFieldString("YS_STK_COL_GP")); 
		    	String szCOL_TRN_EQP_CD   	= commUtils.trim(recOutTemp.getFieldString("TRN_EQP_CD"));
		    	
		    	szMsg = "[" + methodNm + "] 발지개소코드[" + srSPOS_WLOC_CD + "], " 
		    	      + "발지개소POINT코드[" + srSPOS_YD_PNT_CD + "]로 야드에서 관리되는 적치열구분[출발지:" + szYD_CARLD_STOP_LOC + "]이 존재합니다.";
		    	commUtils.printLog(logId, szMsg, "SL");
				
				/**********************************************************
		    	 * 6-1.(적치열의 운송코드 = 전문 운송코드) -> 맵 Clear
		    	 **********************************************************/
				if( szCOL_TRN_EQP_CD.equals(srTRN_EQP_CD))	{					
					szMsg	= "[" + methodNm + "] 출발야드의 적치열[" + szYD_CARLD_STOP_LOC + "]의 운송장비코드[" + szCOL_TRN_EQP_CD 
							+ "]와 전문의 운송장비코드[" + srTRN_EQP_CD + "]가 같으므로 맵 Clear 시작 ";
					commUtils.printLog(logId, szMsg, "SL");
					
					/**********************************************************
			    	 * 6-1-1. 출발야드 적치열 -> 비활성상태(C) 로 업데이트
			    	 **********************************************************/
					szMsg	= "[" + methodNm + "] 출발야드의 적치열[" + szYD_CARLD_STOP_LOC + "]을 비활성상태로 변경처리 시작 ";
					commUtils.printLog(logId, szMsg, "SL");
					
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YS_STK_COL_GP",        szYD_CARLD_STOP_LOC	);
			    	recInTemp.setField("YD_STK_COL_ACT_STAT",  "C"					);
			    	recInTemp.setField("YD_CAR_USE_GP",        ""					);
			    	recInTemp.setField("TRN_EQP_CD",           ""					);
			    	recInTemp.setField("CAR_NO",               ""					);
			    	recInTemp.setField("CARD_NO",              ""					);
			    	recInTemp.setField("MODIFIER", 				szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);

			    	intRtnVal = commDao.update(recInTemp, updYdStkcolByColActStatClear, logId, methodNm, "TB_YS_STKCOL CLEAR");
					if(intRtnVal <= 0) {
						szMsg	="[" + methodNm + "] 적치열[" + szYD_CARLD_STOP_LOC + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, methodNm, "SL");
						m_ctx.setRollbackOnly();
						throw new DAOException(szMsg);
					}
				
					/**********************************************************
			    	 * 6-1-2. 차량포인트통합관리
			    	 **********************************************************/
					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					szMsg	= 	"차량포인트통합관리 [2] 변경처리 시작 ";
					commUtils.printLog(logId, szMsg, "SL");

					this.YsCarPointinforeg2("2","","",szYD_CARLD_STOP_LOC,"","","C",logId,methodNm);
					
					 // 적치베드 비활성상태로 변경
					/**********************************************************
			    	 * 6-1-3. 출발야드 적치베드 -> 야드적치베드활성상태(=C(비활성상태), YD_STK_BED_ACT_STAT) 
			    	 *                         및 BED중량MAX(=기본값, YD_STK_BED_WT_MAX) 으로 업데이트
			    	 **********************************************************/
					szMsg	= 	"[" + methodNm + "] 출발야드의 적치열[" + szYD_CARLD_STOP_LOC + "]의 베드를 비활성상태와 BED중량MAX기본값으로 변경처리 시작 ";
					commUtils.printLog(logId, szMsg, "SL");
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_STK_BED_WT_MAX", 	YsConstant.YD_STK_BED_WT_MAX_DEFAULT);
					recInTemp.setField("YS_STK_COL_GP", 		szYD_CARLD_STOP_LOC);
					recInTemp.setField("YD_STK_BED_ACT_STAT", 	"C");					
			    	recInTemp.setField("MODIFIER", 				szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);

					intRtnVal = commDao.update(recInTemp, updYdStkbedYdStkColGp, logId, methodNm, "TB_YS_STKBED 등록");
					if(intRtnVal <= 0) {
						szMsg	= "[" + methodNm + "] 적치BED[" + szYD_CARLD_STOP_LOC + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, methodNm, "SL");
						
						throw new DAOException(szMsg);
					}
					
					/**********************************************************
			    	 * 6-1-4. 출발야드 적치단 -> 야드적치단활성상태(=C(비활성상태), YD_STK_LYR_ACT_STAT) 
			    	 *                       및 야드적치단재료상태(=E(적치가능), YD_STK_LYR_MTL_STAT) 로 업데이트
			    	 **********************************************************/
					szMsg	= "[" + methodNm + "] 출발야드의 적치열[" + szYD_CARLD_STOP_LOC + "]의 적치단을 비활성상태로 변경처리 시작 ";
					commUtils.printLog(logId, methodNm, "SL");
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);		// Log ID
					recInTemp.setResultMsg(methodNm);	// Log Method Name
					
					recInTemp.setField("YS_STK_COL_GP", 		szYD_CARLD_STOP_LOC	);
					recInTemp.setField("YD_STK_LYR_ACT_STAT", 	"C"					);
					recInTemp.setField("SSTL_NO", 				""					);
					recInTemp.setField("YD_STK_LYR_MTL_STAT", 	"E"					);

//					intRtnVal = commDao.update(recInTemp, updYdStkLyrYdStkColGpClear, logId, methodNm, "TB_YS_STKLYR CLEAR");
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear2", logId, methodNm, "YS_적치단 CLEAR");
					if(intRtnVal <= 0) {
						szMsg	= "[" + methodNm + "] 적치단[" + szYD_CARLD_STOP_LOC + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szMsg, "SL");
						
						throw new DAOException(szMsg);
					}

					
					/**********************************************************
			    	 * 6-1-5. 차량 출발 시 상차지 저장위치 제원 야드 L2 로 전송
			    	 *          야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6
			    	 *          2025.08.20 특수강 정정 야드 : N7, 대형 봉강 옥외 야드 : N8 추가
			    	 *          YSNxLx01 저장위치제원
			    	 *          YSNxLx02 저장품제원
			    	 *          YSNxLx03 크레인작업지시
			    	 *          YSNxLx04 크레인작업실적응답
			    	 **********************************************************/
					recInTemp = JDTORecordFactory.getInstance().create();
			    	String szYdGp = szYD_CARLD_STOP_LOC.substring(0,2);
					String szJMS_TC_CD = "";
					
					if(szYdGp.startsWith("GE")){
// 2025.08.20 특수강 정정 야드 : N7 추가
						szJMS_TC_CD =  "YSN7L201";
			    	}else if(szYdGp.startsWith("GF")){
// 2025.08.20 대형 봉강 옥외 야드 : N8 추가
						szJMS_TC_CD =  "YSN8L001";
			    	}else if(szYdGp.startsWith("GD")){
// 2026.02.05 소형 야드 : N7 추가
						szJMS_TC_CD =  "YSN7L301";
			    	}
					
		    		recInTemp.setField("MSG_ID"			, 	szJMS_TC_CD							);
					recInTemp.setField("YD_INFO_SYNC_CD", 	"3"									);						//1:동,2:SPAN,3:열,4:BED
					recInTemp.setField("YD_GP"			, 	szYD_CARLD_STOP_LOC.substring(0, 1)	);
					recInTemp.setField("YS_STK_COL_GP"	, 	szYD_CARLD_STOP_LOC					);
					
					if(srTRN_WRK_FULLVOID_GP.equals("E")) {									// 공차출발
						recInTemp.setField("YD_CAR_PROG_STAT", "1");
						recInTemp.setField("YD_EQP_WRK_STAT" , "U");
						
						szMsg 	= "[" + methodNm + "] 공차출발시 시 저장위치 제원 야드L2로 전송";
					}else{																	// 영차출발
						recInTemp.setField("YD_CAR_PROG_STAT", "A");
						recInTemp.setField("YD_EQP_WRK_STAT" , "L");
						
						szMsg 	= "[" + methodNm + "] 영차출발시 시 저장위치 제원 야드L2로 전송";
					}
					
					commUtils.printLog(logId, szMsg, "SL");					
					
					//전송 Data 생성
					sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szJMS_TC_CD, recInTemp));

					// 저장위치제원(YSNxLx01)
//					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
//					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { sndRecord });
					
					szMsg	= "[" + methodNm + "] 차량출발시 시 저장위치 제원 야드L2로 전송";
					
					commUtils.printLog(logId, szMsg, "SL");					
					
				}
				/**********************************************************
		    	 * 6-2.(적치열의 운송코드 != 전문 운송코드) -> 맵 Clear 하지 않음
		    	 **********************************************************/
				else{
					szMsg	= "[" + methodNm + "] 출발야드의 적치열[" + szYD_CARLD_STOP_LOC + "]의 운송장비코드[" + szCOL_TRN_EQP_CD 
							+ "]와 전문의 운송장비코드[" + srTRN_EQP_CD + "]가 다르므로 맵 Clear하지 않음 ";
					commUtils.printLog(logId, szMsg, "SL");
				}

	    	} 
	    	/***************************  출발개소코드 비활성처리 / 하차완료 차량스케줄 삭제 처리 / 각강 저장품 등록 완료  ***************************

	    	/***************************  하차개소코드 처리                                                                                                    ***************************/

	    	recInPara = JDTORecordFactory.getInstance().create();
	    	
	    	recInPara.setField("YD_CAR_USE_GP",			"L"							);
	    	recInPara.setField("YD_EQP_ID",             szYD_EQP_ID					);
	    	recInPara.setField("TRN_EQP_CD",            srTRN_EQP_CD				);
	    	recInPara.setField("WLOC_CD",               srARR_WLOC_CD				);
	    	recInPara.setField("PNT_DMD_DT",            commUtils.getDateTime14()	);
	    	recInPara.setField("SPOS_YD_PNT_CD",        srSPOS_YD_PNT_CD			);
	    	recInPara.setField("SPOS_WLOC_CD",          srSPOS_WLOC_CD				);
	    	recInPara.setField("TRN_WRK_FULLVOID_GP",	srTRN_WRK_FULLVOID_GP		);
// 2025.09.01 recInPara 항목 추가	    	
	    	recInPara.setField("ARR_WLOC_CD",           srARR_WLOC_CD				);
	    	
	    	if( !srYD_DIRECT_CARLD_GP.equals("")) {
	    		recInPara.setField("YD_DIRECT_CARLD_GP",srYD_DIRECT_CARLD_GP		);
	    	}else{
	    		recInPara.setField("YD_DIRECT_CARLD_GP",""							);
	    	}
	    	
	    	recInPara.setField("IS_EJB_CALL", 			srIS_EJB_CALL				); 	// 하위 모듈에서 EJB Call or JMS Call 유무 설정
	    	recInPara.setField("YD_AUTO_LOT", 			srYD_AUTO_LOT				);	// 자동LOT/수동LOT 편성 판단 변수
	    	
	    	/**********************************************************
	    	 * 8. 공차 (차량스케줄등록/차량도착POINT요구모듈 호출)
	    	 **********************************************************/	
	    	if(srTRN_WRK_FULLVOID_GP.equals("E")) {
	    		
	    		//야드상차출발위치
// 2025.09.29 공차인 경우 ARR_WLOC_CD(착지개소코드) 가 구내운송 차량이 상차하러 가는 발지개소코드 	    
		    	rsStkCol = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWLocCdToYdStkcol", logId, methodNm, "개소코드 적치열 조회"); 
				if (rsStkCol == null || rsStkCol.size() <= 0) {
		    		szMsg	= "[" + methodNm + "] 공차 출발 작지개소 [" + srARR_WLOC_CD + "] 대기장입니다.";
		    		commUtils.printLog(logId, szMsg, "SL");
		    	}
				else {
		    		rsStkCol.absolute(1);
			    	recOutTemp = JDTORecordFactory.getInstance().create();
			    	recOutTemp.setRecord(rsStkCol.getRecord());
			    	
			    	szYD_CARLD_STOP_LOC	= commUtils.trim(recOutTemp.getFieldString("YS_STK_COL_GP")); 


				}
	    		
	    		
	    		recInPara.setField("YD_CARLD_LEV_LOC", szYD_CARLD_STOP_LOC	);
	    		recInPara.setField("TRN_EQP_STK_CAPA", srTRN_EQP_STK_CAPA	);

	    		/*
	    		위치										차량위치		개소코드		야드포인트코드
	    		빌렛정정 남1문								GETR11			S3Y22			1E01
	    		빌렛정정 남2문								GETR21			S4S13			1E02
	    		대형옥내 남4문								GETR41			S3S20			1E04
	    		빌렛정정 대형옥외야드 이송재(밴드쏘)					GETR42			S3Y99			2E04
	    		대형옥외 옥외야드								GFTR11			S3Y21			1F01
	    		철분말 													BSY04  
	    		*/
	    		
	    		// 공차 착지가 특수강 대형야드 신예화 관련 개소코드만 처리
// 2026.01.28 소형봉강야드(S5Y30) 개소코드 추가
// 2026.05.07 소형 야드 개소 코드 S5Y30 -> SMS12, S5S20 분리
	    		if( "S3Y22".equals(srARR_WLOC_CD)
	    		 || "S4S13".equals(srARR_WLOC_CD)
   	    		 || "S3S20".equals(srARR_WLOC_CD)
   	    		 || "S3Y21".equals(srARR_WLOC_CD)
   	    		 || "BSY04".equals(srARR_WLOC_CD)
   	    		 || "S3Y99".equals(srARR_WLOC_CD)
   	    		 || "SMS12".equals(srARR_WLOC_CD)
   	    		 || "S5S20".equals(srARR_WLOC_CD)
   	    		  ) {
	    			
// JMS 에 있는 발지,야드포인트로 TB_YD_CARPOINT 테이블 SELECT Query - com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd
// 있으면 해당 적치열
	    			
	    			rcvMsg.setField("YD_CARLD_LEV_LOC", szYD_CARLD_STOP_LOC);
	    			
    				/**********************************************************
		    		* 하차지 정보로 
		    		* 1.차량스케줄 생성
		    		* 2.차량도착POINT요구모듈을 호출
		    		**********************************************************/
// 2025.09.12 procInsCarSch call 시 수신 받은 JMS 사용
//	    			sndRecord = this.procInsCarSch(logId, methodNm,recInPara,sndRecord);
	    			sndRecord = this.procInsCarSch(logId, methodNm, rcvMsg, sndRecord);
	    			
		    		commUtils.printLog(logId, sndRecord.getTaskCode(), "SL");			
		    		intRtnVal =   Integer.parseInt(sndRecord.getTaskCode());
		    		if(intRtnVal == -1) {
		    			
						szMsg	=" LOT편성 호출 중 Error";
						commUtils.printLog(logId, szMsg, "SL");					
						m_ctx.setRollbackOnly();
						
						throw new DAOException(szMsg);
		    		}
	    		}	
	    	}
	    	/**********************************************************
	    	 * 9. 영차 
	    	 **********************************************************/
	    	else if(srTRN_WRK_FULLVOID_GP.equals("F")) {
	    		
	    			
    			/**********************************************************
		    	 * 9-1. 차량재료 조회 (YD_CAR_SCH_ID 이용)
		    	 **********************************************************/		
	    		recInPara.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID);	    		
		    	

	    		rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    	rsWrkBookMtl = commDao.select(recInPara, getYdCarftmvmtlID, logId, methodNm, "차량재료 조회"); 		    	
		    	if (rsWrkBookMtl == null || rsWrkBookMtl.size() <= 0) {
					szMsg = "[" + methodNm + "] 차량재료 조회 data not found";
		    		commUtils.printLog(logId, szMsg, "SL");
		    		
		    		m_ctx.setRollbackOnly();
		    		
					throw new DAOException(szMsg);
				}	    	
				
	    		/**********************************************************
		    	 * 9-3. 하차작업예약 생성 작업(중복으로 영차 출발 실적 들어오는 경우 제외
		    	 **********************************************************/
////////////////////////////////////////////////////////////////
// 2025.11.19 작업 예약 막음 START	    	
// 2026.04.20 착지가 특수강신예화개소코드(S3Y22, S4S13, S3S20, S3Y21, S3Y99, BSY04, S5Y30) 인 경우만 작업 예약 생성 
// 2026.05.07 소형 야드 개소 코드 S5Y30 -> SMS12, S5S20 분리
		    	if (  (  "S3Y22".equals(srARR_WLOC_CD) 
		  	  	      || "S4S13".equals(srARR_WLOC_CD)	 
		  		      || "S3S20".equals(srARR_WLOC_CD)	 
		  			  || "S3Y21".equals(srARR_WLOC_CD)	
		  			  || "BSY04".equals(srARR_WLOC_CD)	
		  			  || "S3Y99".equals(srARR_WLOC_CD)	
		  			  || "SMS12".equals(srARR_WLOC_CD)	
		  			  || "S5S20".equals(srARR_WLOC_CD)	
		  			  ))
				{
		    		if(!szYD_CARUD_WRK_BOOK_ID.equals("")) {
			    		JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
				    	recInTemp1.setRecord(rcvMsg);
				    	recInTemp1.setField("YD_WBOOK_ID"	, szYD_CARUD_WRK_BOOK_ID);
				    	
				    	JDTORecordSet rsWrkBookMtl1 = commDao.select(recInTemp1, getYdWrkbookmtlId, logId, methodNm, "작업예약재료 조회"); 		    	
				    	if (rsWrkBookMtl1 == null || rsWrkBookMtl1.size() <= 0) {
				    		
							szMsg = "[" + methodNm + "] 기편성된 작업 예약 재료 가 없습니다. 재편성";
				    		commUtils.printLog(logId, szMsg, "SL");
				    		
					    	this.procInsWrkBookCarUd(logId,recInPara);
						}
			    	} else{
			    		/**********************************************************
			    		* 작업예약 생성
			    		* 차량스케줄 차량 상차출발 상태로 변경
			    		**********************************************************/
			    		this.procInsWrkBookCarUd(logId,recInPara);
			    	}		    	
				}
		    	else {
		    		
			    	szMsg=" 영차 출발시 착지가 특수강신예화개소코드(S3Y22, S4S13, S3S20, S3Y99, BSY04, S3Y99, SMS12, S5S20) 가 아니면 작업 예약 생성 하지 않음";
					commUtils.printLog(logId, szMsg, "SL");
		    	}		    			    	
// 2025.11.19 작업 예약 막음 END	    	
////////////////////////////////////////////////////////////////


		    	/**********************************************************
		    	 * 9-4. 차량 영차 출발 시 저장품 제원 -> 야드 L2 로 전송
		    	 *        야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6
		    	 *        2025.08.20 특수강 정정 야드 : N7, 대형 봉강 옥외 야드 : N8 추가
		    	 **********************************************************/
// 2029.10.31 영차 출발시 도착지에 저장품제원 전송 막음 (영차 도착시 전송 하기 때문)		    	
//				if(szYD_GP.startsWith("GE")){
//// 2025.08.20 특수강 정정 야드 : N7 추가
//		    		szMSG_ID =  "YSN7L202";
//		    	}else if(szYD_GP.startsWith("GF")){
//// 2025.08.20 대형 봉강 옥외 야드 : N8 추가
//		    		szMSG_ID =  "YSN8L002";
//		    	}	
//	    		
//		    	for(int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
//		    		rsWrkBookMtl.absolute(Loop_i);
//		    		recInTemp  = JDTORecordFactory.getInstance().create();
//		    		recOutTemp = JDTORecordFactory.getInstance().create();
//		    		recOutTemp.setRecord(rsWrkBookMtl.getRecord());
//		    		
//					if( !szMSG_ID.equals("") ) {
//						recInTemp  = JDTORecordFactory.getInstance().create();
//						
//						recInTemp.setResultCode(logId);	//Log ID
//						recInTemp.setResultMsg(methodNm);	//Log Method Name
//						
//						recInTemp.setField("MSG_ID"			, szMSG_ID												);
//						recInTemp.setField("YD_INFO_SYNC_CD", "B"													);	//차량입고
//				    	recInTemp.setField("SSTL_NO"		, commUtils.trim(recOutTemp.getFieldString("SSTL_NO"))	); 
//				    	recInTemp.setField("YS_STK_COL_GP"	, szYD_GP												);
//				    	
//				    	sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szMSG_ID, recInTemp));
//				    	
//				    	szMsg=" 영차 출발 시 저장품 제원 야드L2[" + szMSG_ID + "]로 전송";
//						commUtils.printLog(logId, szMsg, "SL");
//					}		    		
//		    	}

		    	
		    	
		    	
/////////////////////////////////////////////////////////////////////////////////////////////////////////		    	
// 2025.11.26 착지가 특수강신예화개소코드(S3Y22, S4S13, S3S20, S3Y21, S3Y99, BSY04, S5Y30) 가 아니면 차량 스케쥴 종료(L2 이송 물량) 
// 2026.01.28 소형봉강야드(S5Y30) 개소코드 추가
// 2026.05.07 소형 야드 개소 코드 S5Y30 -> SMS12, S5S20 분리
		    	if (  (  "S3Y22".equals(srARR_WLOC_CD) 
		  	  	      || "S4S13".equals(srARR_WLOC_CD)	 
		  		      || "S3S20".equals(srARR_WLOC_CD)	 
		  			  || "S3Y21".equals(srARR_WLOC_CD)	
		  			  || "BSY04".equals(srARR_WLOC_CD)	
		  			  || "S3Y99".equals(srARR_WLOC_CD)	
		  			  || "SMS12".equals(srARR_WLOC_CD)	
		  			  || "S5S20".equals(srARR_WLOC_CD)	
		  			  ))
					{

		    		
		    		/**********************************************************
			    	 * 9-5. 차량 도착 POINT 요구 (TSYDJ002)
			    	 **********************************************************/	
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setRecord(rcvMsg);
			    	
			    	recInTemp.setField("JMS_TC_CD"			, "TSYSJ002"	); 
			    	recInTemp.setField("WLOC_CD"			, srARR_WLOC_CD	);
			    	recInTemp.setField("TRN_WRK_FULLVOID_GP", "F"			); 	    	
			    	
//			    	sndRecord = this.rcvTSYSJ002(recInTemp);
			    	sndRecord = commUtils.addSndData(sndRecord,this.rcvTSYSJ002(recInTemp));
			    	

		    	}
		    	else {
		    		
			    	szMsg=" 영차 출발 발지가 특수강신예화개소코드(S3Y22, S4S13, S3S20, S3Y99, BSY04, S3Y99, SMS12, S5S20) 이고 착지가 특수강신예화개소코드(S3Y22, S4S13, S3S20, S3Y99, BSY04, S3Y99, SMS12, S5S20) 가 아니면 종료";
					commUtils.printLog(logId, szMsg, "SL");
		    		
			    	szMsg=" 발지 [" + srSPOS_WLOC_CD + "] 착지 [" + srARR_WLOC_CD + "]";
					commUtils.printLog(logId, szMsg, "SL");
		    		

					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recInTemp.setField("DEL_YN", "Y");
					recInTemp.setField("MODIFIER", szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
					
					
					/**********************************************************
			    	 * 이송지시 종료 처리
			    	 **********************************************************/
					
					szMsg 	= "[" + methodNm + "] 차량스케줄[" + szYD_CAR_SCH_ID + "]이 이송지시 종료 시작";
					commUtils.printLog(logId, szMsg, "SL");
					
					commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.updPbStlFrToMoveDown", logId, methodNm, "TB_PB_STLFRTOMOVE 종료");

					szMsg 	= "[" + methodNm + "] 차량스케줄[" + szYD_CAR_SCH_ID + "]이 이송지시 종료 끝";
					
					/**********************************************************
			    	 * 차량 스케줄(TB_YS_CARSCH, TB_YS_CARFTMVMTL) 종료 처리
			    	 **********************************************************/

					szMsg 	= "[" + methodNm + "] 차량스케줄[" + szYD_CAR_SCH_ID + "]이 종료 시작";
					commUtils.printLog(logId, szMsg, "SL");
					

					commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSchMtl", logId, methodNm, "TB_YS_CARFTMVMTL 종료");
					
					commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch", logId, methodNm, "TB_YS_CARSCH 종료");
					
					szMsg 	= "[" + methodNm + "] 차량스케줄[" + szYD_CAR_SCH_ID + "]이 종료 끝";
					commUtils.printLog(logId, szMsg, "SL");

					
					
		    	}
		    	
/////////////////////////////////////////////////////////////////////////////////////////////////////////		    	
		    		
		    	
// 2025.12.15 영차 출발(상차후 출발) 이면 차량 저장위치 크레인 작업 CLEAR
				sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "018", "*");
				if("Y".equals(sApplyYnPI)){
					szMsg = "TB_YS_RULE 018 차량 상차 완료후 해당 차량 위치 스케쥴 취소 ";
		      		commUtils.printLog(logId, szMsg, "");

		      		szMsg 	= "[" + methodNm + "] 영차 발지개소코드 [" + srSPOS_WLOC_CD + "]";
					commUtils.printLog(logId, szMsg, "SL");

		    		JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
			    	recInTemp1.setField("WLOC_CD"	, srSPOS_WLOC_CD);
					
			    	rsResult = commDao.select(recInTemp1, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolColGpLike", logId, methodNm, "적치열 조회"); 

			    	if (rsResult.size() > 0) {
			    		
						szMsg 	= "영차 발지 개소코드 [" + srSPOS_WLOC_CD + "] 야드 포인트요구 요청 결과  = [" + rsResult.size() + "] 건";
			      		commUtils.printLog(logId, szMsg, "");
			      		
			      		String szYS_STK_COL_GP    = commUtils.trim(rsResult.getRecord(0).getFieldString("YS_STK_COL_GP"   ));
			      		
						szMsg 	= "영차 발지 개소코드 [" + srSPOS_WLOC_CD + "] 차량저장위치 [" + szYS_STK_COL_GP + "]";
			      		commUtils.printLog(logId, szMsg, "");
			      		
						if(szYS_STK_COL_GP.startsWith("GE")){
// 2025.12.15 특수강 대형 압연 옥내 야드, 특수강 정정 야드
//							CbtYsJsp.trtCrnSchCncl_Car(szYS_STK_COL_GP);
					    	sndRecord = commUtils.addSndData(sndRecord, CbtYsJsp.trtCrnSchCncl_Car(szYS_STK_COL_GP));

						}else if(szYS_STK_COL_GP.startsWith("GF")){
// 2025.12.15 특수강 대형 봉강 옥외 야드
//				    		EbtYsJsp.trtCrnSchCncl_Car(szYS_STK_COL_GP);
					    	sndRecord = commUtils.addSndData(sndRecord, EbtYsJsp.trtCrnSchCncl_Car(szYS_STK_COL_GP));

						}else if(szYS_STK_COL_GP.startsWith("GD")){
// 2026.01.29 특수강 소형 야드
					    	sndRecord = commUtils.addSndData(sndRecord, SbrYsJsp.trtCrnSchCncl_Car(szYS_STK_COL_GP));

				    	}
			      		
			      		
			    	}
					
				}
		    	
	    	}
	    	else{	    		
				szMsg	= "운송작업영공구분코드 Error";
				commUtils.printLog(logId, szMsg, "SL");
				
				m_ctx.setRollbackOnly();
				throw new DAOException(szMsg);				
	    	}		    	
	    	
	    	szMsg	= "[" + methodNm + "] 소재차량 출발 실적 처리 메소드 성공적으로 실행 완료 ]";
			commUtils.printLog(logId, szMsg, "SL");	  
			
		} catch(Exception e){
			szMsg	= "소재차량 도착처리 Error:" + e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			
			m_ctx.setRollbackOnly();
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	    
		commUtils.printLog(logId, methodNm, "S-");
		return sndRecord;
			
	} //end of rcvTSYSJ004()	


	
	
	/**
	 * 오퍼레이션명 : 소재차량 공차도착 실적
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procLDMatlCarArr(String logId, JDTORecord rcvMsg, JDTORecord recCarSch, JDTORecord rcvMsgCol)throws JDTOException  {
		String methodNm = "소재차량 공차도착 실적[YsCommCarTSMvSeEJBBean.procLDMatlCarArr] < " + rcvMsg.getResultMsg();
		
		JDTORecordSet rsResult          = null;
		JDTORecord    recInTemp         = null;
//		JDTORecord    recOutTemp        = null;
		JDTORecord    sndRecord         = JDTORecordFactory.getInstance().create();
		
	    int intRtnVal 		   	= 0 ;
	    
	    String szMsg           	= "";
	    String szMethodName    	= "procLDMatlCarArr";
	    String szREG_MOD_USER   = "MtlCarArr";

	    String szTRN_EQP_CD    	= "";
	    String szARR_WLOC_CD   	= "";
	    String szARR_YD_PNT_CD 	= "";
	    String szYS_STK_COL_GP 	= "";
	    String szYD_WBOOK_ID   	= "";
	    String szYD_SCH_CD		= null;
	    String szYD_CAR_SCH_ID 	= "";
	    String szCOL_TRN_EQP_CD	= "";
	    String szCARD_NO        = "";
	    String szMSG_GP			= null;
	    String szYD_PNT_CD		= null;
	    String szYD_CARLD_STOP_LOC = null;
	    String szYD_CAR_PROG_STAT  = null;
	    String szYD_STK_COL_ACT_STAT = "";

	    boolean bIsReplacable	= false;

	    
	    try{

	    	commUtils.printLog(logId, methodNm, "S+");
	    	
	    	szMSG_GP    			= commUtils.trim(rcvMsg.getFieldString("MSG_GP")); 
	    	szARR_WLOC_CD   		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 
	    	szARR_YD_PNT_CD 		= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 
	    	szYD_WBOOK_ID   		= commUtils.trim(rcvMsg.getFieldString("YD_CARLD_WRK_BOOK_ID")); // 작업예약TABLE의 작업예약ID
	    	szYD_SCH_CD    			= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));            // 작업예약TABLE의 스케쥴 코드
	    	szTRN_EQP_CD   			= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 

	    	szMsg	=	"[" + methodNm + "] 수신정보 : MSG_GP[" + szMSG_GP + "],착지개소코드[" + szARR_WLOC_CD + "],포인트[" + szARR_YD_PNT_CD + "],상차작업예약ID[" + szYD_WBOOK_ID + "],스케쥴코드[" + szYD_SCH_CD + "],차량번호[" + szTRN_EQP_CD + "]";
			commUtils.printLog(logId, szMsg, "SL");

			//수신된 차량 정지위치(COL) 정보
	    	szYS_STK_COL_GP   		= commUtils.trim(rcvMsgCol.getFieldString("YS_STK_COL_GP")); 
	    	szYD_STK_COL_ACT_STAT	= commUtils.trim(rcvMsgCol.getFieldString("YD_STK_COL_ACT_STAT")); 
	    	szCOL_TRN_EQP_CD    	= commUtils.trim(rcvMsgCol.getFieldString("TRN_EQP_CD")); 
	    	szCARD_NO    			= commUtils.trim(rcvMsgCol.getFieldString("CARD_NO")); 

	    	szMsg	=	"[" + methodNm + "] COL 정보 : 적치열[" + szYS_STK_COL_GP + "],적치상태[" + szYD_STK_COL_ACT_STAT + "],적치열 차량[" + szCOL_TRN_EQP_CD + "],적치열 카드[" + szCARD_NO + "]";
			commUtils.printLog(logId, szMsg, "SL");
	    	
	    	//차량 SCH 정보
			szYD_CAR_SCH_ID   		= commUtils.trim(recCarSch.getFieldString("YD_CAR_SCH_ID")); 
			szYD_CAR_PROG_STAT  	= commUtils.trim(recCarSch.getFieldString("YD_CAR_PROG_STAT")); 
			szYD_CARLD_STOP_LOC 	= commUtils.trim(recCarSch.getFieldString("YD_CARLD_STOP_LOC")); 
			szYD_PNT_CD   			= commUtils.trim(recCarSch.getFieldString("YD_PNT_CD1" )); 

	    	szMsg	=	"[" + methodNm + "] 차량 SCH 정보 : 차량스케쥴[" + szYD_CAR_SCH_ID + "],야드적치열활성상태[" + szYD_CAR_PROG_STAT + "],도착위치[" + szYD_CARLD_STOP_LOC + "],도착포인드[" + szYD_PNT_CD + "]";
			commUtils.printLog(logId, szMsg, "SL");

	    	
			if( !szYD_WBOOK_ID.equals("") ) { 
		    	szMsg="****** szYD_WBOOK_ID [" + szYD_WBOOK_ID + "]";
				commUtils.printLog(logId, szMsg, "SL");

				//작업예약이 존재하는 경우
				if(szYD_SCH_CD.substring(0,2).equals(szYS_STK_COL_GP.substring(0,2)) ) {			
	    			//신규 작업예약 생성하지 않기
			    	szMsg="****** 신규 작업예약 생성하지 않음";
					commUtils.printLog(logId, szMsg, "SL");
					
	    			bIsReplacable = false;
	    		}else{	
	    			//기존작업예약이 등록된 동과 차량이 도착한 현재동이 다른 경우
	    			//기존작업예약을 삭제처리하고 이미 등록된 차량이송준비스케줄을 조회
	    			
			    	szMsg="****** 기존작업예약이 등록된 동과 차량이 도착한 현재동이 다른 경우";
					commUtils.printLog(logId, szMsg, "SL");
	    			
					recInTemp = JDTORecordFactory.getInstance().create();
	    	    	recInTemp.setField("YD_WBOOK_ID", 	   	szYD_WBOOK_ID);
	    	    	recInTemp.setField("MODIFIER", 	   		szMethodName.substring(0, 10));
	    	    	recInTemp.setField("DEL_YN", 	   		"Y");
	    	    	
	    	    	commDao.update(recInTemp, updDelYnWrkBookMtl, logId, methodNm, "TB_YS_WRKBOOKMTL 삭제");
	    			
					szMsg="["+methodNm+"]  기존에 등록된 작업예약이 존재하는 동과 차량이 도착한 현재동이 다르므로 기존작업예약["+szYD_WBOOK_ID+"]재료을 삭제 완료";
					commUtils.printLog(logId, szMsg, "SL");
					
	    	    	commDao.update(recInTemp, updDelYnWrkBook, logId, methodNm, "TB_YS_WRKBOOK 삭제");
	    			
	    	    	szMsg="["+methodNm+"]  기존에 등록된 작업예약이 존재하는 동과 차량이 도착한 현재동이 다르므로 기존작업예약["+szYD_WBOOK_ID+"]을 삭제 완료";
	    			commUtils.printLog(logId, szMsg, "SL");
					
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					//신규 작업예약 생성
	    			bIsReplacable = true;
	    		}
    		}else{	 
    			//신규 작업예약 생성하기
		    	szMsg="****** 신규 작업예약 생성";
				commUtils.printLog(logId, szMsg, "SL");

				bIsReplacable = true;
    		}
    		
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			//신규 작업예약 생성
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			JDTORecord recOutPara = JDTORecordFactory.getInstance().create();
    		if( bIsReplacable ) {
    			szMsg	=	"****** 적치열 [" + szYS_STK_COL_GP + "]";
				commUtils.printLog(logId, szMsg, "SL");
    			
				// LOT 편성된 정보가 없기 때문에 SCH_CD편성
				if("GETR11".equals(szYS_STK_COL_GP)){    				
    				szYD_SCH_CD = "GETR11UB"; 								// 빌렛정정 이송출고 (남1문)
				} else if("GETR21".equals(szYS_STK_COL_GP)){    				
    				szYD_SCH_CD = "GETR21UB"; 								// 빌렛정정 이송출고 (남2문)
				} else if("GETR41".equals(szYS_STK_COL_GP)){    				
    				szYD_SCH_CD = "GETR41U_"; 								// 대형옥내 이송출고 (남4문)
				} else if(szYS_STK_COL_GP.startsWith("GF")){    				
    				szYD_SCH_CD = "GFTR01UM"; 								// 대형옥외 이송출고
				} else if(szYS_STK_COL_GP.startsWith("GD")){
// 2026.02.05 소형 야드 추가
    				szYD_SCH_CD = szYS_STK_COL_GP.substring(0, 6) + "UM";	// 소형 야드 이송출고
				} else {
    				szYD_SCH_CD = szYS_STK_COL_GP.substring(0, 6) + "UB"; 	// 그외
				}
//				if(szYS_STK_COL_GP.startsWith("GF")){    				
//					szYD_SCH_CD = "GFTR01"; 								// 대형옥외 이송출고
//				} else {
//					szYD_SCH_CD = szYS_STK_COL_GP.substring(0, 6); 	// 그외
//				}

    			szMsg	=	"****** 스케쥴코드 [" + szYD_SCH_CD + "]";
				commUtils.printLog(logId, szMsg, "SL");
 
    			//이미 등록된 차량이송준비스케줄을 조회
    			szMsg	=	"[" + methodNm + "] 차량이송준비스케줄을 조회 후 작업예약 등록 시작";
    			commUtils.printLog(logId, szMsg, "SL");
    			
				recInTemp = JDTORecordFactory.getInstance().create();
				
				//-------------------------------------------------------------------------------------------------
				//	도착포인트에 해당하는 준비스케줄이 존재 하는 경우 조회
				//-------------------------------------------------------------------------------------------------
				szMsg 	= 	"[" + methodNm + "] ★★★도착포인트에 해당하는 준비스케줄이 존재 하는 경우 조회 시작★★★";
				commUtils.printLog(logId, szMsg, "SL");
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord recPara  = JDTORecordFactory.getInstance().create();
				
				recPara.setField("YD_GP"			, szYD_SCH_CD.substring(0, 1));
				
				recPara.setField("YD_SCH_CD"		, szYD_SCH_CD);
				
				recPara.setField("YD_WRK_PLAN_CRN"	, "");
				recPara.setField("YD_PREP_WK_ST"	, "L");
				recPara.setField("YD_PNT_CD"		, szYD_PNT_CD);
	 			recPara.setField("CAR_GP"			, szTRN_EQP_CD.substring(1, 2));
				
				// 가장빠른 상차 LOT
				//열 작은것부터, 베드 높은것부터, 단 높은것부터
				
				rsResult = commDao.select(recPara, getYdStockPrepSchByYdCrn, logId, methodNm, "적치열 조회"); 
		    	
		    	if (rsResult == null || rsResult.size() <= 0) {
		    		szMsg = "["+methodNm+"] 배차순서가 빠르고 준비스케줄ID가 빠른 차량 이송 대상재를 준비스케줄에서 조회 시 대상 없음 ";
					commUtils.printLog(logId, szMsg, "SL");
		    		szMsg = "["+methodNm+"] 직상차를 위한 작업 대기 ";
					commUtils.printLog(logId, szMsg, "SL");
//					throw new DAOException(szMsg);
		    	} else {
		    		
//////////////////////////////////////////////////////////////////////////////////
// 2025.11.10 작업예약/작업예약재료 등록 막음 START		    		
//					//-------------------------------------------------------------------------------------------------
//					//	작업예약/작업예약재료 등록
//					//-------------------------------------------------------------------------------------------------
//			    	String szYD_PREP_SCH_ID = "";
//			    	String szYD_SCH_PRIOR = "";
//			    	
//					for(int i = 1; i <= rsResult.size(); i++ ) {
//						rsResult.absolute(i);
//						recPara			= rsResult.getRecord();
//						String szSSTL_NO		= commUtils.trim(recPara.getFieldString("SSTL_NO" ));				
//						String szYD_GP			= szYD_SCH_CD.substring(0, 1);
//						String szYD_BAY_GP		= szYD_SCH_CD.substring(1, 2);
//						String szYD_AIM_YD_GP	= commUtils.trim(recPara.getFieldString("YD_AIM_YD_GP" ));
//						String szYD_AIM_BAY_GP	= commUtils.trim(recPara.getFieldString("YD_AIM_BAY_GP" ));
//						//String szYS_STK_COL_GP	= commUtils.trim(recPara.getFieldString("YS_STK_COL_GP" ));
//						String szYS_STK_BED_NO	= commUtils.trim(recPara.getFieldString("YS_STK_BED_NO" ));
//						String szYS_STK_LYR_NO	= commUtils.trim(recPara.getFieldString("YS_STK_LYR_NO" ));
//						String szYS_STK_SEQ_NO	= commUtils.trim(recPara.getFieldString("YS_STK_SEQ_NO" ));
//						
//						if( i == 1 ) {
//							szYD_PREP_SCH_ID	= commUtils.trim(recPara.getFieldString("YD_PREP_SCH_ID" ));
//							//-------------------------------------------------------------------------------------------------
//							//	스케줄코드 조회
//							//-------------------------------------------------------------------------------------------------
//					    	//스케줄코드로 스케줄기준Table조회
//							szMsg="스케줄코드로 스케줄기준Table조회";
//							commUtils.printLog(logId, szMsg, "SL");
//							
//							recInTemp = JDTORecordFactory.getInstance().create();
//					    	recInTemp.setField("YD_SCH_CD", szYD_SCH_CD);
//					    	
//					    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
//							szMsg="스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.";
//							commUtils.printLog(logId, szMsg, "SL");
//							
//							// rsResult 지역변수가 For문 반복시마다  초기화 됨 변수명 변경 rsResult -> rsResult2(아래 참조하는 부분 포함) - wc
//							JDTORecordSet rsResult2 = JDTORecordFactory.getInstance().createRecordSet("Temp");
//					    	/*  com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule  */
//					    	
//// 2025.09.05 SELECT 부분에 DECODE(YD_EQP_GP,'CR','CR','S'||SUBSTR(YD_EQP_ID,-1)) AS YD_SCH_GP 로직이 들어 있어 빌렛정정 야드 크레인 2,4,5 는 오류 발생 jSpeed Query 변경
//							szMsg="***********  szYS_STK_COL_GP = [" + szYS_STK_COL_GP + "]";
//							commUtils.printLog(logId, szMsg, "SL");
//							
//							rsResult2 = commDao.select(recInTemp, getYdSchruleCrn, logId, methodNm, "스케줄 기준 조회"); 
//
//					    	
//					    	if (rsResult2 == null || rsResult2.size() <= 0) {
//					    		szMsg = "["+methodNm+"] 스케줄 기준 조회 data not found";
//								commUtils.printLog(logId, methodNm, "SL");
//								throw new DAOException(szMsg);
//							}
//					    	
//							//레코드 추출
//							rsResult2.first();
//							recPara = rsResult2.getRecord();
//							szYD_SCH_PRIOR 	= commUtils.trim(recPara.getFieldString("YD_WRK_CRN_PRIOR"  ));
//		    				szYD_SCH_CD 	= commUtils.trim(recPara.getFieldString("YD_SCH_CD"       	));
//
//					    	//-------------------------------------------------------------------------------------------------
//							
//							//-------------------------------------------------------------------------------------------------
//							//	작업예약 등록
//							//-------------------------------------------------------------------------------------------------
//							
//							//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
//							szYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
//							
//							if ("".equals(szYD_WBOOK_ID)) {
//								commUtils.printLog(logId, "작업예약ID 생성 실패", "SL");
//								throw new DAOException(szMsg);
//							}
//	
//							//작업예약항목SETTING
//							recOutPara = JDTORecordFactory.getInstance().create();
//							
//							recOutPara.setField("YD_WBOOK_ID", 			szYD_WBOOK_ID);
//							recOutPara.setField("REGISTER", 			szREG_MOD_USER);
//							recOutPara.setField("YD_GP", 				szYD_GP);
//							recOutPara.setField("YD_BAY_GP", 			szYD_BAY_GP);
//							recOutPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
//							recOutPara.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);
//							recOutPara.setField("YD_AIM_YD_GP", 		szYD_AIM_YD_GP);
//							recOutPara.setField("YD_AIM_BAY_GP", 		szYD_AIM_BAY_GP);
//							recOutPara.setField("YD_CAR_USE_GP", 		"L");  //구내운송
//							recOutPara.setField("TRN_EQP_CD", 			szTRN_EQP_CD);
//							
//// 2025.10.23 TB_YS_WRKBOOK INSERT 시 야드To위치Guide 추가()							
//							recOutPara.setField("YD_TO_LOC_GUIDE", 		szYS_STK_COL_GP);
//							
//							szMsg = "["+methodNm+"] 작업예약["+szYD_WBOOK_ID+"]등록 시작";
//							commUtils.printLog(logId, szMsg, "SL");
//	
//	
//							//com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook
//							commDao.insert(recOutPara, insWrkBook, logId, methodNm, "TB_YS_WRKBOOK 등록");
//							
//							szMsg ="["+methodNm+"] 작업예약["+szYD_WBOOK_ID+"]등록 완료";
//							commUtils.printLog(logId, szMsg, "SL");
//							
//							//-------------------------------------------------------------------------------------------------
//						}
//						
//						//-------------------------------------------------------------------------------------------------
//						//	작업예약재료 등록
//						//-------------------------------------------------------------------------------------------------
//						
//						recOutPara = JDTORecordFactory.getInstance().create();
//						
//						recOutPara.setField("YD_WBOOK_ID", 				szYD_WBOOK_ID);
//						recOutPara.setField("REGISTER", 				szREG_MOD_USER);
//						recOutPara.setField("SSTL_NO", 					szSSTL_NO);
//						recOutPara.setField("YS_STK_COL_GP", 			szYS_STK_COL_GP);
//						recOutPara.setField("YS_STK_BED_NO", 			szYS_STK_BED_NO);
//						recOutPara.setField("YS_STK_LYR_NO", 			szYS_STK_LYR_NO);
//						recOutPara.setField("YS_STK_SEQ_NO", 			szYS_STK_SEQ_NO);
//						recOutPara.setField("YD_UP_COLL_SEQ", 			String.valueOf(i));
//					
//						intRtnVal = commDao.insert(recOutPara, insWrkBookMtl, logId, methodNm, "TB_YS_WRKBOOKMTL 등록");
//			    		if(intRtnVal <= 0) {
//			    			commUtils.printLog(logId, "작업예약ID 생성 실패", "SL");
//							throw new DAOException(szMsg);
//			    		}
//						
//						
//						szMsg = "["+methodNm+"] ["+i+"] 작업예약["+szYD_WBOOK_ID+"]의 작업재료["+szSSTL_NO+"] 등록 완료";
//						commUtils.printLog(logId, szMsg, "SL");
//						//-------------------------------------------------------------------------------------------------
//					}
//					
//					//-------------------------------------------------------------------------------------------------
//					//	준비스케줄 삭제
//					//-------------------------------------------------------------------------------------------------
//					
//					szMsg = "["+methodNm+"]  준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시작";
//					commUtils.printLog(logId, szMsg, "SL");
//					recOutPara = JDTORecordFactory.getInstance().create();
//					
//					recOutPara.setField("YD_PREP_SCH_ID", 	szYD_PREP_SCH_ID);
//					recOutPara.setField("MODIFIER", 		szREG_MOD_USER);
//					recOutPara.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
//					recOutPara.setField("DEL_YN", 			"Y");
//					
//					commDao.update(recOutPara, delYdPrepmtlByPrepSchIdYN, logId, methodNm, "준비작업 재료 삭제");
//					
//					// WC - 준비스케줄 삭제
//					commDao.update(recOutPara, delYdPrepSch, logId, methodNm, "준비스케줄 삭제");
//	
//					szMsg = "["+methodNm+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 완료 - 메세지 : " ;
//					commUtils.printLog(logId, szMsg, "SL");
//					
//					
// 2025.11.10 작업예약/작업예약재료 등록 막음 END		    		
//////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////
// 2025.11.10 작업예약/작업예약재료 등록 새로운 로직 START		    		
		    		
		    		String sstlNos 			= "";															// 재료번호

					for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++ ) {
			    		rsResult.absolute(Loop_i);
						
			    		recInTemp = JDTORecordFactory.getInstance().create();
			    		recInTemp.setRecord(rsResult.getRecord());

				    	sstlNos += commUtils.trim(recInTemp.getFieldString("SSTL_NO")); 
						if(Loop_i < rsResult.size()) {
							sstlNos += ",";
						}
						
					}

					GridData inGridData = new GridData();
					JDTORecord jrRtn 	= null;
			    	
					inGridData.addParam("SSTL_NOS", 		sstlNos);			// 재료번호들
					inGridData.addParam("YS_STK_COL_GP", 	szYS_STK_COL_GP);	// 야드적치열구분(6자리 이상)
					inGridData.addParam("YD_TO_LOC_GUIDE", 	szYS_STK_COL_GP);	// 야드To위치Guide
					inGridData.addParam("YD_WRK_CRN", 		"");				// 야드작업크레인(작업자지정 크레인)
					inGridData.addParam("userid", 			"TSYSJ003");		// 수정자
					
					szMsg = "\n\t SSTL_NOS   		: " 	+ sstlNos 
		            	  + "\n\t YS_STK_COL_GP     : " 	+ szYS_STK_COL_GP;

		      		commUtils.printLog(logId, szMsg, "");
		// 작업 예약 등록				
					if(szYS_STK_COL_GP.startsWith("GE")){
		// 2025.10.02 특수강 정정 야드
				    	szMsg	= "[" + methodNm + "] 하차작업예약 CbtYsJsp.updbtMvStkWrkBook Method Call";
			      		commUtils.printLog(logId, szMsg, "");

				    	jrRtn = CbtYsJsp.updbtMvStkWrkBook(inGridData);

				    	szMsg	= "[" + methodNm + "] 하차작업예약 CbtYsJsp.updbtMvStkWrkBook Method 종료";
				    	commUtils.printLog(logId, szMsg, "SL");
				    	
			    	}else if(szYS_STK_COL_GP.startsWith("GF")){
		// 2025.10.02 대형 봉강 옥외 야드
				    	szMsg	= "[" + methodNm + "] 하차작업예약 EbtYsJsp.updbtMvStkWrkBook Method Call";
			      		commUtils.printLog(logId, szMsg, "");

				    	jrRtn = EbtYsJsp.updbtMvStkWrkBook(inGridData);

				    	szMsg	= "[" + methodNm + "] 하차작업예약 EbtYsJsp.updbtMvStkWrkBook Method 종료";
				    	commUtils.printLog(logId, szMsg, "SL");
				    	
			    	}else if(szYS_STK_COL_GP.startsWith("GD")){
// 2026.01.29 소형 야드
				    	szMsg	= "[" + methodNm + "] 하차작업예약 SbrYsJsp.updbtMvStkWrkBook Method Call";
			      		commUtils.printLog(logId, szMsg, "");

				    	jrRtn = SbrYsJsp.updbtMvStkWrkBook(inGridData);

				    	szMsg	= "[" + methodNm + "] 하차작업예약 SbrYsJsp.updbtMvStkWrkBook Method 종료";
				    	commUtils.printLog(logId, szMsg, "SL");
				    	
			    	} else {

			    		szMsg	= "야드 적치열 [" + szYS_STK_COL_GP + "] 빌렛정정야드, 대형 옥외 야드, 소형 야드 아님 ";
				    	commUtils.printLog(logId, szMsg, "SL");
				    	
			    	}

					// 2025.11.25 작업 예약 Method 에서 return 되면 크레인 작업 작업  			
					// 전송할 Data가 있으면 전송 처리
					if (jrRtn != null) {
				    	szMsg	= "[" + methodNm + "] 하차작업예약 updbtMvStkWrkBook Method RETURN sndInterface 처리";
			      		commUtils.printLog(logId, szMsg, "");

//			      		jrRtn.setResultCode(logId);
//						jrRtn.setResultMsg(methodNm);
//
//						EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
//						sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			      		sndRecord = commUtils.addSndData(sndRecord,jrRtn);	
			      		
			      		
					}
					
					
//2025.11.10 작업예약/작업예약재료 등록 새로운 로직 END		    		
//////////////////////////////////////////////////////////////////////////////////
		    		
	    		}
    		}
    		
    		
    		
			/**********************************************************
			* 1. 차량스케줄 갱신 (실적등록된 상차위치로)
			* 1.1 상차스케쥴 및 포인트
			**********************************************************/			

			szMsg="****** 차량스케줄 갱신 (실적등록된 상차위치로)";
			commUtils.printLog(logId, methodNm, "SL");
    		
    		recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CAR_SCH_ID", 	   	szYD_CAR_SCH_ID);
	    	recInTemp.setField("YD_CARLD_WRK_BOOK_ID", 	szYD_WBOOK_ID);
	    	recInTemp.setField("YD_STK_COL_ACT_STAT",  	"L");                 //적치가능
	    	recInTemp.setField("YD_PNT_CD1",  			szARR_YD_PNT_CD);
	    	recInTemp.setField("YD_CAR_PROG_STAT",     	YsConstant.YD_CARLD_ARR);
	    	recInTemp.setField("YD_CARLD_STOP_LOC", 	szYS_STK_COL_GP);	 // 실제차량이 들어온 상차정지위치를 재등록한다.    	
	    	recInTemp.setField("YD_CARLD_ARR_DT", 		commUtils.getDateTime14());//상차도착
	    	recInTemp.setField("ARR_WLOC_CD",  			szARR_WLOC_CD);
	    	                           
// 2025.08.26 UPDATE 항목에 TRN_EQP_CD 추가
	    	recInTemp.setField("TRN_EQP_CD",  			szTRN_EQP_CD);					// 운송장비코드
	    	recInTemp.setField("MODIFIER", 				commUtils.getMsgId(rcvMsg));	// 전문 I/F ID 
	    	
	    	intRtnVal = commDao.update(recInTemp, updYdCarSchCarWrkBook2, logId, methodNm, "TB_YS_CARSCH 갱신");
			if(intRtnVal <= 0) {
				szMsg	= "[" + methodNm + "] 차량스케줄 업데이트 시 존재하지 않습니다.";
				commUtils.printLog(logId, methodNm, "SL");
				throw new DAOException(szMsg);
			}	

			/**********************************************************
			* 1. 차량 도착열(COL) 등록
			* 1.1 해당장비로 CLEAR
			* 1.2 해당열 등록
			**********************************************************/			
			szMsg	= "****** 차량 도착열(COL) 등록";
			commUtils.printLog(logId, methodNm, "SL");

			recInTemp = JDTORecordFactory.getInstance().create();			 
			recInTemp.setField("TRN_EQP_CD",     szTRN_EQP_CD);
			
			
			intRtnVal = commDao.update(recInTemp, updYdStkcolTrnEqpCdToNull, logId, methodNm, "TB_YS_STKCOL CLEAR");
			if(intRtnVal < 0) {
				szMsg = "["+methodNm+"] 운송장비코드[" + szTRN_EQP_CD + "] - 예약된 차량도착point 삭제 시 오류발생 - 반환값 : " + intRtnVal;
				commUtils.printLog(logId, szMsg, "SL");
			} else {			
				szMsg = "["+methodNm+"] 운송장비코드[" + szTRN_EQP_CD + "] - 예약된 차량도착point 삭제 성공";
				commUtils.printLog(logId, szMsg, "SL");
			}
			
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
	    	recInTemp.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
	    	recInTemp.setField("YD_STK_COL_ACT_STAT", "L");
	    	recInTemp.setField("YD_CAR_USE_GP"		, "L");			// WC 추가
			recInTemp.setField("MODIFIER"			, szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);

			
	    	intRtnVal = commDao.update(recInTemp, updYdStkcolByColActStat, logId, methodNm, "TB_YS_STKCOL 등록");
			if(intRtnVal <= 0) {
				szMsg	="[" + methodNm + "] 적치열[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
				commUtils.printLog(logId, methodNm, "SL");
	    		sndRecord.setTaskCode("-1");
	    		return sndRecord;

			}
			/**********************************************************
			* 1. 차량포인트 등록
			* 1.1 해당장비로 CLEAR
			* 1.2 해당차량포인트 등록
			**********************************************************/			
			//설비코드로 초기화 
		    this.YsCarPointinforeg2("1","",szTRN_EQP_CD,"","","","C",logId,methodNm);
		    
		    //저장위치로 차량 포인트 예약
		    this.YsCarPointinforeg2("3","",szTRN_EQP_CD,szYS_STK_COL_GP,"","","L",logId,methodNm);
			
			/**********************************************************
			* 1. 적치베드 활성화
			**********************************************************/			
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
			recInTemp.setField("YD_STK_BED_ACT_STAT", "L");
			
			intRtnVal = commDao.update(recInTemp, updYdStkbedYdStkColGp, logId, methodNm, "TB_YS_STKBED 등록");
			if(intRtnVal <= 0) {
				szMsg	= "[" + methodNm + "] 적치BED[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
				commUtils.printLog(logId, methodNm, "SL");
				throw new DAOException(szMsg);
			}
			
			/**********************************************************
			* 1. 적치단 활성화
			**********************************************************/			
	    	recInTemp  = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
	    	recInTemp.setField("SSTL_NO"			, "");
	    	recInTemp.setField("YD_STK_LYR_ACT_STAT", "E");
	    	recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
	    	        
			intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear2", logId, methodNm, "YS_적치단 CLEAR");
			
			if(intRtnVal <= 0) {
				szMsg	= "[" + methodNm + "] 적치단[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new DAOException(szMsg);

			}

			/**********************************************************
			* 1. 공차 도착 시 저장위치 제원 야드L2로 전송
			* 야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6 	
			* 2025.08.21 특수강 정정 야드 : N7, 대형 봉강 옥외 야드 : N8 추가
			**********************************************************/			

			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setResultCode(logId);	//Log ID
	    	recInTemp.setResultMsg(methodNm);	//Log Method Name
	    	String szYdGp = szYS_STK_COL_GP.substring(0,2);
			String szJMS_TC_CD = "";
			
			if(szYdGp.startsWith("GE")){
//2025.08.21 특수강 정정 야드 : N7 추가
	    		szJMS_TC_CD =  "YSN7L201";
	    	}else if(szYdGp.startsWith("GF")){
//2025.08.21 대형 봉강 옥외 야드 : N8 추가
	    		szJMS_TC_CD =  "YSN8L001";
	    	}else if(szYdGp.startsWith("GD")){
//2026.02.05 소형 야드 : YSN7L301 추가
	    		szJMS_TC_CD =  "YSN7L301";
	    	}
			
    		recInTemp.setField("MSG_ID"				, szJMS_TC_CD);
			recInTemp.setField("YD_INFO_SYNC_CD"	, "3");						//1:동,2:SPAN,3:열,4:BED
			recInTemp.setField("YD_GP"				, szYS_STK_COL_GP.substring(0, 1));
			recInTemp.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
			recInTemp.setField("YD_CAR_PROG_STAT"	, "2");
			recInTemp.setField("YD_EQP_WRK_STAT"	, "U");

			szMsg	= "[" + methodNm + "] szJMS_TC_CD [" + szJMS_TC_CD + "] YS_STK_COL_GP [" + szYS_STK_COL_GP + "]";
			commUtils.printLog(logId, szMsg, "SL");
			
			//전송 Data 생성
			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szJMS_TC_CD, recInTemp));

			szMsg	= "[" + methodNm + "] 공차도착 시 저장위치 제원 야드L2로 전송";
			commUtils.printLog(logId, szMsg, "SL");
			
			/**********************************************************
			* 1. 크레인 스케줄 호출 작업
			**********************************************************/			
			
			//작업예약이 존재하는 경우 - 
			szMsg	= "******** szYD_WBOOK_ID [" + szYD_WBOOK_ID + "]";
			commUtils.printLog(logId, szMsg, "SL");
	    	if( !szYD_WBOOK_ID.equals("") ) {
				szMsg	= "szYD_WBOOK_ID [" + szYD_WBOOK_ID + "] procCallCrnSch call";
				commUtils.printLog(logId, szMsg, "SL");

				//작업예약을 조회한다.
				recInTemp = JDTORecordFactory.getInstance().create();
				
				recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
		    	//크레인 스케줄 호출
				sndRecord = this.procCallCrnSch(logId, recInTemp, sndRecord);
				
				szMsg	= "[" + methodNm + "] 크레인 스케줄 호출";
				commUtils.printLog(logId, szMsg, "SL");
	    	}
	    	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
			commUtils.printParam(logId + "확인1 ", sndRecord);
			
		}catch(Exception e){
			
			szMsg	= "[" + methodNm + "] Error:" + e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	
		szMsg	= "[" + methodNm + "] 메소드 완료";
		commUtils.printLog(logId, szMsg, "S-");
		sndRecord.setTaskCode("1");
 		return sndRecord;
	} //end of procLDMatlCarArr()
	
	
	/**
	 * 오퍼레이션명 : 소재차량 영차도착 실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procUDMatlCarArr(String logId, JDTORecord rcvMsg, JDTORecordSet rsWbookMtl, JDTORecord recCarSch, JDTORecord rcvMsgCol)throws JDTOException  {
		String methodNm = "소재차량 영차도착 실적[YsCommCarTSMvSeEJBBean.procUDMatlCarArr] < " + rcvMsg.getResultMsg();
		
		JDTORecordSet rsResult          = null;
		JDTORecord	  sndRecord			= JDTORecordFactory.getInstance().create();
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord	  recInPara			= null;
		
		
	    int intRtnVal 		   = 0 ;
	    
	    String szMsg           = "";
	    String szMethodName    = "procUDMatlCarArr";
	    String szOperationName = "소재차량 영차도착 실적";
	    
	    String szTRN_EQP_CD    = "";
	    String szARR_WLOC_CD   = "";
	    String szARR_YD_PNT_CD = "";
	    String szYS_STK_COL_GP = "";
	    String szYD_WBOOK_ID   = "";
	    String isUnMatched     = "";
	    String szYD_CAR_SCH_ID = "";
	    String szSSTL_NO        = "";
	    String szYD_STK_COL_ACT_STAT = "";
	    String szTRN_WRK_FULLVOID_GP= "";
	    
		String sstlNos 			= "";															// 재료번호
	    
	    try{
	    	//----------------------------------------------------------------------------------------------------------
	    	//	파라미터 값 확인
	    	//----------------------------------------------------------------------------------------------------------
	    	
	    	szARR_WLOC_CD   		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 
	    	szARR_YD_PNT_CD 		= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 
	    	szYD_WBOOK_ID   		= commUtils.trim(rcvMsg.getFieldString("YD_CARUD_WRK_BOOK_ID")); 
	    	isUnMatched    			= commUtils.trim(rcvMsg.getFieldString("IS_UNMATCHED")); 
	    	szTRN_EQP_CD   			= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"));
	    	
	    	szMsg	= "[" + szOperationName + "] 수신정보 : 착지개소코드 [" + szARR_WLOC_CD + "], 포인트 [" + szARR_YD_PNT_CD + "], 하차작업예약ID [" + szYD_WBOOK_ID + "], 차량번호 [" + szTRN_EQP_CD + "]";
			commUtils.printLog(logId, szMsg, "SL");
	    	
	    	//수신된 차량 정지위치(COL) 정보
	    	szYS_STK_COL_GP  		= commUtils.trim(rcvMsgCol.getFieldString("YS_STK_COL_GP")); 
	    	szYD_STK_COL_ACT_STAT	= commUtils.trim(rcvMsgCol.getFieldString("YD_STK_COL_ACT_STAT")); 
	    	
	    	//차량 SCH 정보
	    	szYD_CAR_SCH_ID   		= commUtils.trim(recCarSch.getFieldString("YD_CAR_SCH_ID")); 
	    	
	    	szTRN_WRK_FULLVOID_GP   = "F";
	    	
	    	szMsg	= "[" + szOperationName + "] COL 정보 : 적치열 [" + szYS_STK_COL_GP + "], 적치상태 [" + szYD_STK_COL_ACT_STAT + "]";
			commUtils.printLog(logId, szMsg, "SL");
 
	    	
	    	szMsg	= "[" + szOperationName + "] 운송장비코드 [" + szTRN_EQP_CD + "] 로 차량스케줄을 조회 완료 - 차량스케줄ID [" + szYD_CAR_SCH_ID + "]";
	    	commUtils.printLog(logId, szMsg, "SL");
			
	    	
////////////////////////////////////////////////////////////////
// 2025.11.19 작업 예약 막음 START	    	
//			//----------------------------------------------------------------------------------------------------------
//			//	하차 작업예약 생성(하차동이 변경된 경우 )
//			//----------------------------------------------------------------------------------------------------------
//	    	if("Y".equals(isUnMatched) && szTRN_WRK_FULLVOID_GP.equals("F")){ 
//	    		// 영차이고 하차동이 변경될 경우 작업예약 스케쥴코드만 변경해준다.
//
//	    		szMsg	=	"영차이고 하차동이 변경될 경우 작업예약 스케쥴코드만 변경";
//		    	commUtils.printLog(logId, szMsg, "SL");
//	    		
//	    		recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("MODIFIER"		, "TSYSJ003");
//		    	recInTemp.setField("YD_BAY_GP"		, szYS_STK_COL_GP.substring(1 , 2));//실제 차량이 들어온 하차정지위치를 재등록한다.
////2025.08.27 특수강 정정 야드 : N7 추가
//		    	if(szYS_STK_COL_GP.startsWith("GE")){
//			    	recInTemp.setField("YD_SCH_CD"		, szYS_STK_COL_GP + "LB");
//		    	}else if(szYS_STK_COL_GP.startsWith("GF")){
////2025.08.27 대형 봉강 옥외 야드 : N8 추가 (GFTR01LM	대형옥외 이송입고)
//			    	recInTemp.setField("YD_SCH_CD"		, szYS_STK_COL_GP.substring(0 , 4) + "01LM");
//		    	} else {
//					szMsg	=	"[" + szOperationName + "] 영차 적치열 오류 [" + szYS_STK_COL_GP + "]" ;
//					commUtils.printLog(logId, szMsg, "SL");	
//		    		sndRecord.setTaskCode("0");
//		    		return sndRecord;
//		    	}
//
//		    	recInTemp.setField("YD_WBOOK_ID"	, szYD_WBOOK_ID);
//		    	
//		    	intRtnVal = commDao.update(recInTemp, updYdWrkbook, logId, methodNm, "TB_YS_WBOOK 수정");
//	    		
//	    	}else if("".equals(szYD_WBOOK_ID) && szTRN_WRK_FULLVOID_GP.equals("F")){ 
//	    		szMsg	=	"영차이고 작업예약 스케쥴코드 없는 경우";
//		    	commUtils.printLog(logId, szMsg, "SL");
//		    	
//		    	recInPara = JDTORecordFactory.getInstance().create();
//		    	recInPara.setResultCode(logId);	//Log ID
//		    	recInPara.setResultMsg(methodNm);	//Log Method Name
//		    	recInPara.setField("YD_CAR_SCH_ID",            szYD_CAR_SCH_ID);
//		    	recInPara.setField("TRN_EQP_CD",               szTRN_EQP_CD);
//		    	recInPara.setField("WLOC_CD",                  szARR_WLOC_CD);
//		    	
////				//트렌젝션 분리 작업
//// 2025.10.23 트랜젝션 분리 막음		    	
//		    	szMsg	=	"procInsWrkBookCarUd Method 전";
//		    	commUtils.printLog(logId, szMsg, "SL");
//
////	    		EJBConnector ejbConn = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
////	    		ejbConn.trx("procInsWrkBookCarUd", new Class[] { String.class,JDTORecord.class }, new Object[] { logId, recInPara });
//		    	this.procInsWrkBookCarUd(logId,recInPara);
//
//		    	szMsg	=	"procInsWrkBookCarUd Method 후";
//		    	commUtils.printLog(logId, szMsg, "SL");
//
//		    	
//	    		rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//
//		    	//차량 SCH 정보
//
//		    	recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
//	    		
//	    		/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch */
//	    		rsResult = commDao.select(recInTemp, getYdCarsch, logId, methodNm, "차량스케줄조회"); 
//		    	
//		    	if (rsResult == null || rsResult.size() <= 0) {
//					szMsg	=	"[" + szOperationName + "] 차량스케줄조회 중 오류 [" + intRtnVal + "]" ;
//					commUtils.printLog(logId, szMsg, "SL");	
//		    		sndRecord.setTaskCode("0");
//		    		return sndRecord;
//				}
//
//		    	szMsg	=	"************** CHECK 1";
//		    	commUtils.printLog(logId, szMsg, "SL");
//		    	
//				rsResult.absolute(1);
//
//				szMsg	=	"************** CHECK 2";
//		    	commUtils.printLog(logId, szMsg, "SL");
//
//		    	recOutTemp = JDTORecordFactory.getInstance().create();
//
//				szMsg	=	"************** CHECK 3";
//		    	commUtils.printLog(logId, szMsg, "SL");
//
//		    	recOutTemp.setRecord(rsResult.getRecord());
//
//				szMsg	=	"************** CHECK 4";
//		    	commUtils.printLog(logId, szMsg, "SL");
//
//		    	
//		    	//작업예약
//		    	szYD_WBOOK_ID   	= commUtils.trim(recOutTemp.getFieldString("YD_CARUD_WRK_BOOK_ID")); 
//
//		    	szMsg	=	"************** CHECK 5";
//		    	commUtils.printLog(logId, szMsg, "SL");
// 			}
// 2025.11.19 작업 예약 막음 END	    	
////////////////////////////////////////////////////////////////

			/**********************************************************
			* 1. 차량스케줄 갱신 (실적등록된  하차위치로)
			* 1.1 차량스케줄에 하차작업예약id, 차량진행상태(하차도착) 등록
			**********************************************************/				
	    	szMsg	= "[" + szOperationName + "] 차량스케줄 [" + szYD_CAR_SCH_ID + "]에 하차작업예약 [" + szYD_WBOOK_ID + "], 착지개소코드[" + szARR_WLOC_CD 
	    			+ "], 차량진행상태[B], 하차정지위치[" + szYS_STK_COL_GP + "] 등록 시작";
	    	commUtils.printLog(logId, szMsg, "SL");
	    	
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("MODIFIER", 					"TSYSJ003");
	    	recInTemp.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
	    	recInTemp.setField("YD_CARUD_WRK_BOOK_ID", 		szYD_WBOOK_ID);
	    	recInTemp.setField("ARR_WLOC_CD", 				szARR_WLOC_CD);
	    	recInTemp.setField("YD_STK_COL_ACT_STAT", 		szYD_STK_COL_ACT_STAT);
	    	recInTemp.setField("YD_CAR_PROG_STAT", 			"B");
	    	recInTemp.setField("YD_CARUD_STOP_LOC", 		szYS_STK_COL_GP);//실제 차량이 들어온 하차정지위치를 재등록한다.
	    	recInTemp.setField("YD_CARUD_ARR_DT", 			commUtils.getDateTime14());//하차도착

	    	intRtnVal = commDao.update(recInTemp, updYsCarSchCarWrkUD, logId, methodNm, "TB_YS_CARSCH 하차정보 등록");
			if(intRtnVal <= 0) {
				szMsg = "[" + szOperationName + "] 차량스케줄 업데이트 시 존재하지 않습니다.";
				commUtils.printLog(logId, methodNm, "SL");
	    		sndRecord.setTaskCode("-1");
	    		return sndRecord;

			}	
			/**********************************************************
			* 1.작업예약재료에 적치열정보를 등록한다
			* (하차작업예약생성시에는 정지위치를 알수없기에 도착처리시에 등록.)
			**********************************************************/				

			szMsg = "[" + szOperationName + "] 하차작업예약 [" + szYD_WBOOK_ID + "]의 작업재료에 차량도착위치 [" + szYS_STK_COL_GP + "]를 등록 시작";
			commUtils.printLog(logId, szMsg, "SL");	

//////////////////////////////////////////////////////////////////////////////////////////////////////////////			
// 2025.10.02 작업 예약 정보 만들지 않고 updbtMvStkWrkBook Method Call
//			String stlNos           = commUtils.trim(gdReq.getParam("SSTL_NOS"       ));	//재료번호들
//			String ysStkColGp       = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"  ));	//야드적치열구분(6자리 이상)
	
			
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp.setField("YD_WBOOK_ID", 	   szYD_WBOOK_ID);
////////////////////////////////////////////////////////////////
// 2025.11.19 작업 예약 재료 대신 차량 재료로 변경	    	
//	    	rsResult = commDao.select(recInTemp, getYdWrkbookmtlId, logId, methodNm, "작업예약상세 조회");
	    	
	    	recInTemp.setField("YD_CAR_SCH_ID", 	   szYD_CAR_SCH_ID);
	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlIDLm", logId, methodNm, "차량재료 조회"); 
////////////////////////////////////////////////////////////////
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		szMsg = "작업예약 id로 작업예약 조회 중 Error code : " + intRtnVal;
				commUtils.printLog(logId, szMsg, "SL");	
			}
	    	
	    	for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
	    		rsResult.absolute(Loop_i);
				
	    		recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsResult.getRecord());

		    	sstlNos += commUtils.trim(recOutTemp.getFieldString("SSTL_NO")); 
				if(Loop_i < rsResult.size()) {
					sstlNos += ",";
				}

		    	
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_WBOOK_ID"	, szYD_WBOOK_ID);
//		    	recInTemp.setField("SSTL_NO"		, szSSTL_NO);
//		    	recInTemp.setField("YS_STK_COL_GP"	, szYS_STK_COL_GP);
//		    	recInTemp.setField("YS_STK_BED_NO"	, recOutTemp.getFieldString("YS_STK_BED_NO")); // WC
//		    	recInTemp.setField("YS_STK_LYR_NO"	, recOutTemp.getFieldString("YS_STK_LYR_NO")); // WC
//		    	recInTemp.setField("YS_STK_SEQ_NO"	, recOutTemp.getFieldString("YS_STK_SEQ_NO")); // WC
//		    	
//		    	intRtnVal = commDao.update(recInTemp, updYdWrkbookmtl, logId, methodNm, "TB_YS_WRKBOOKMTL 등록");
//				if(intRtnVal <= 0) {
//					szMsg	=	"[" + szOperationName + "] 작업 예약 등록 시 ERROR 발생.";
//					commUtils.printLog(logId, methodNm, "SL");
//		    		sndRecord.setTaskCode("-1");
//		    		return sndRecord;
//				}	
//				szMsg	=	"[" + szOperationName + "] 하차작업예약 [" + szYD_WBOOK_ID + "]의 작업재료 [" + szSSTL_NO + "]에 차량도착위치 [" + szYS_STK_COL_GP + "]를 등록 성공";
//				commUtils.printLog(logId, szMsg, "SL");
//				
//				
			}
	    	
	    	
	    	
// 2025.10.16 updbtMvStkWrkBook Method 에서 저장위치 체크 하기 때문에 Method 끝으로 이동	    	
//			GridData inGridData = new GridData();
//			JDTORecord jrRtn 	= null;
//	    	
//			inGridData.addParam("SSTL_NOS", 		sstlNos);			// 재료번호들
//			inGridData.addParam("YS_STK_COL_GP", 	szYS_STK_COL_GP);	// 야드적치열구분(6자리 이상)
//			inGridData.addParam("YD_TO_LOC_GUIDE", 	"");				// 야드To위치Guide
//			inGridData.addParam("YD_WRK_CRN", 		"");				// 야드작업크레인(작업자지정 크레인)
//			inGridData.addParam("userid", 			"TSYSJ003");		// 수정자
//			
//			szMsg = "\n\t SSTL_NOS   		: " 	+ sstlNos 
//            	  + "\n\t YS_STK_COL_GP     : " 	+ szYS_STK_COL_GP;
//
//      		commUtils.printLog(logId, szMsg, "");
//// 작업 예약 등록				
//			if(szYS_STK_COL_GP.startsWith("GE")){
//// 2025.10.02 특수강 정정 야드
//		    	szMsg	= "[" + szOperationName + "] 하차작업예약 CbtYsJsp.updbtMvStkWrkBook Method Call";
//		    	commUtils.printLog(logId, szMsg, "SL");
//
//		    	jrRtn = CbtYsJsp.updbtMvStkWrkBook(inGridData);
//	    	}else if(szYS_STK_COL_GP.startsWith("GF")){
//// 2025.10.02 대형 봉강 옥외 야드
//		    	szMsg	= "[" + szOperationName + "] 하차작업예약 EbtYsJsp.updbtMvStkWrkBook Method Call";
//		    	commUtils.printLog(logId, szMsg, "SL");
//
//		    	jrRtn = EbtYsJsp.updbtMvStkWrkBook(inGridData);
//	    	}
//
//	    	commUtils.printLog(logId, szMsg, "SL");
			
			
			/**********************************************************
			* 1. 차량 도착열(COL) 등록
			* 1.1 해당장비로 CLEAR
			* 1.2 해당열 등록
			**********************************************************/	
			recInTemp = JDTORecordFactory.getInstance().create();			 
			recInTemp.setField("TRN_EQP_CD",     szTRN_EQP_CD);
			
			intRtnVal = commDao.update(recInTemp, updYdStkcolTrnEqpCdToNull, logId, methodNm, "TB_YS_STKCOL CLEAR");
			if(intRtnVal <= 0) {
				szMsg = "[" + methodNm + "] 운송장비코드 [" + szTRN_EQP_CD + "] - 예약된 차량도착point 삭제 시 오류발생 - 반환값 : " + intRtnVal;
				commUtils.printLog(logId, methodNm, "SL");
			} else {			
				szMsg = "[" + szOperationName + "] 운송장비코드 [" + szTRN_EQP_CD + "] - 예약된 차량도착point 삭제 성공";
				commUtils.printLog(logId, szMsg, "SL");
			}	    	
			
			//----------------------------------------------------------------------------------------------------------
	    	//하차정지위치 Map Open(적치열, 적치베드, 적치단 활성화)
			//----------------------------------------------------------------------------------------------------------
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YS_STK_COL_GP",        szYS_STK_COL_GP);
	    	recInTemp.setField("TRN_EQP_CD",           szTRN_EQP_CD);
	    	recInTemp.setField("YD_STK_COL_ACT_STAT",  "L");
	    	recInTemp.setField("YD_CAR_USE_GP",        "L");
			recInTemp.setField("MODIFIER", 		szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);

			intRtnVal = commDao.update(recInTemp, updYdStkcolByColActStat, logId, methodNm, "TB_YS_STKCOL 등록");
			if(intRtnVal <= 0) {
				szMsg = "[" + methodNm + "] 적치열[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
				commUtils.printLog(logId, methodNm, "SL");
	    		sndRecord.setTaskCode("-1");
	    		return sndRecord;
			}
			
			//----------------------------------------------------------------------------------------------------------
			/**********************************************************
			* 1. 차량포인트 등록
			**********************************************************/	
			
			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		    this.YsCarPointinforeg2("1","",szTRN_EQP_CD,"","","","C",logId,methodNm);
		    
			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		    this.YsCarPointinforeg2("3","",szTRN_EQP_CD,szYS_STK_COL_GP,"","","L",logId,methodNm);
			
			/**********************************************************
			* 1. 적치베드 활성화
			**********************************************************/	
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YS_STK_COL_GP", 		szYS_STK_COL_GP);
			recInTemp.setField("YD_STK_BED_ACT_STAT", 	"L");
			recInTemp.setField("MODIFIER", 				szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
			
			intRtnVal = commDao.update(recInTemp, updYdStkbedYdStkColGp, logId, methodNm, "TB_YS_STKBED 등록");
			if(intRtnVal <= 0) {
				szMsg = "[" + methodNm + "] 적치BED[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
				commUtils.printLog(logId, methodNm, "SL");
				throw new DAOException(szMsg);
			}
			
			/**********************************************************
			* 1. 적치단 활성화
			**********************************************************/	

////////////////////////////////////////////////////////////////
// 2025.11.19 작업 예약 재료 대신 차량 재료로 변경	    	
//			for(int Loop_i = 1; Loop_i <= rsWbookMtl.size(); Loop_i++) {
//				rsWbookMtl.absolute(Loop_i);
//	    		recOutTemp = JDTORecordFactory.getInstance().create();
//	    		recOutTemp.setRecord(rsWbookMtl.getRecord());
			for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				rsResult.absolute(Loop_i);
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsResult.getRecord());
////////////////////////////////////////////////////////////////
		    	
		    	szSSTL_NO = commUtils.trim(recOutTemp.getFieldString("SSTL_NO"       ));
		    	
		    	recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MODIFIER", 				szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		    	recInTemp.setField("YS_STK_COL_GP", 		szYS_STK_COL_GP);
		    	recInTemp.setField("SSTL_NO",        		szSSTL_NO);
		    	recInTemp.setField("YD_STK_LYR_MTL_STAT", 	"C");		    	
		    	recInTemp.setField("YD_STK_LYR_ACT_STAT", 	"E");		    	

		    	
		    	// WC 추가
// 2025.09.30 영차 차량 도착 YS_STK_BED_NO(특수강야드적치Bed번호), YS_STK_LYR_NO(특수강야드적치단번호) '01'로 설정 		    
// 2025.10.17 YS_STK_LYR_NO(특수강야드적치단번호) TB_YS_CARFTMVMTL 적치단 GET 		    	
//		    	recInTemp.setField("YS_STK_BED_NO", recOutTemp.getFieldString("YS_STK_BED_NO")); // WC
//		    	recInTemp.setField("YS_STK_BED_NO", "01");
//		    	recInTemp.setField("YS_STK_LYR_NO", "01");
		    	recInTemp.setField("YS_STK_LYR_NO", recOutTemp.getFieldString("YS_STK_LYR_NO")); // WC
		    	recInTemp.setField("YS_STK_BED_NO", recOutTemp.getFieldString("YS_STK_BED_NO")); // WC
		    	recInTemp.setField("YS_STK_SEQ_NO", recOutTemp.getFieldString("YS_STK_SEQ_NO")); // WC
				recInTemp.setField("MODIFIER", 		szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		    	
// 2025.12.08 YS_STK_BED_NO 는 차량베드로 재설정 01, 02
				
				intRtnVal = commDao.update(recInTemp, updYdStkLyrYdStkColGp2, logId, methodNm, "TB_YS_STKLYR 등록");
				
				if(intRtnVal <= 0) {
					szMsg = "[" + methodNm + "] 적치단[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
					commUtils.printLog(logId, szMsg, "SL");
		    		sndRecord.setTaskCode("-1");
		    		return sndRecord;
				}
			}
			//----------------------------------------------------------------------------------------------------------
			
			
			/**********************************************************
			* 1. 영차 도착 시 저장위치 제원 야드L2로 전송
			* 야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6 	
			* 2025.08.21 특수강 정정 야드 : N7, 대형 봉강 옥외 야드 : N8 추가
			**********************************************************/	
			
			String szYdGp =  szYS_STK_COL_GP.substring(0, 2);
			String szJMS_TC_CD1  = "";
			String szJMS_TC_CD2  = "";
			
			if(szYdGp.startsWith("GE")){
//2025.08.21 특수강 정정 야드 : N7 추가
	    		szJMS_TC_CD1 =  "YSN7L201";
				szJMS_TC_CD2 =  "YSN7L202";
	    	}else if(szYdGp.startsWith("GF")){
//2025.08.21 대형 봉강 옥외 야드 : N8 추가
	    		szJMS_TC_CD1 =  "YSN8L001";
				szJMS_TC_CD2 =  "YSN8L002";
	    	}else if(szYdGp.startsWith("GD")){
//2026.02.05 소형 야드 : N7 추가
	    		szJMS_TC_CD1 =  "YSN7L301";
				szJMS_TC_CD2 =  "YSN7L302";
	    	}	
				
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setResultCode(logId);	//Log ID
			recInTemp.setResultMsg(methodNm);	//Log Method Name
			recInTemp.setField("MSG_ID"				, szJMS_TC_CD1);
			recInTemp.setField("YD_INFO_SYNC_CD"	, "3");
			recInTemp.setField("YD_GP"				, szYS_STK_COL_GP.substring(0, 1));
			recInTemp.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
			recInTemp.setField("YD_CAR_PROG_STAT"	, "B");
			recInTemp.setField("YD_EQP_WRK_STAT"	, "L");

			//전송 Data 생성
			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szJMS_TC_CD1, recInTemp));

			szMsg = "[" + szOperationName + "] 영차도착 시 저장위치 제원 야드L2로 전송";
			commUtils.printLog(logId, szMsg, "SL");
			
			/**********************************************************
			* 1. 영차 도착 시 저장품 제원 야드L2로 전송
			* 야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6 	
			**********************************************************/	
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setResultCode(logId);	//Log ID
			recInTemp.setResultMsg(methodNm);	//Log Method Name
			recInTemp.setField("MSG_ID"			, szJMS_TC_CD2);
			recInTemp.setField("YD_INFO_SYNC_CD", "1");
			recInTemp.setField("YS_STK_COL_GP"	, szYS_STK_COL_GP);
			recInTemp.setField("YS_STK_BED_NO"	, "");

			//전송 Data 생성
			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szJMS_TC_CD2, recInTemp));

			szMsg = "[" + szOperationName + "] 영차도착 시 저장품제원 제원 야드L2로 전송";
			commUtils.printLog(logId, szMsg, "SL");

			/**********************************************************
			* 1. 크레인 스케줄 호출 작업
			**********************************************************/	
// 2025.10.02 작업 예약 정보 만들지 않고 updbtMvStkWrkBook Method Call
//			String stlNos           = commUtils.trim(gdReq.getParam("SSTL_NOS"       ));	//재료번호들
//			String ysStkColGp       = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"  ));	//야드적치열구분(6자리 이상)

			
//			recInTemp = JDTORecordFactory.getInstance().create();
//			recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
//			
//			sndRecord = this.procCallCrnSch(logId,recInTemp,sndRecord);
//			
//			intRtnVal = Integer.parseInt(sndRecord.getTaskCode());
//    		
//			if(intRtnVal == -1) {
//	    		return sndRecord;
//	    	}
			//----------------------------------------------------------------------------------------------------------

//////////////////////////////////////////////////////////////////////////////////
// 2025.12.19  대형 봉강 옥외 야드  To위치Guide   				
			String ydToLocGuide = ""; // 야드To위치Guide
			String sApplyYnPI 	= commDao.ApplyYnPI(logId, methodNm, "APPNEW", "020", "*");
			if("Y".equals(sApplyYnPI) && szYS_STK_COL_GP.startsWith("GF")){
				szMsg = "TB_YS_RULE APPNEW 020 대형봉강옥외야드-하차예정저장위치 [" + szYS_STK_COL_GP +"] ";
	      		commUtils.printLog(logId, szMsg, "");
				
				ydToLocGuide = this.ApplyRuleItem(logId, methodNm, "APPGI7", "*");

				szMsg = "TB_YS_RULE APPGI7 대형봉강옥외야드-하차예정저장위치 [" + ydToLocGuide +"] ";
	      		commUtils.printLog(logId, szMsg, "");
			}
//////////////////////////////////////////////////////////////////////////////////

			
			
// 2025.10.16 updbtMvStkWrkBook Method 에서 저장위치 체크 하기 때문에 Method 끝으로 이동	    	
			GridData inGridData = new GridData();
			JDTORecord jrRtn 	= null;
	    	
			inGridData.addParam("SSTL_NOS", 		sstlNos);			// 재료번호들
			inGridData.addParam("YS_STK_COL_GP", 	szYS_STK_COL_GP);	// 야드적치열구분(6자리 이상)
			inGridData.addParam("YD_TO_LOC_GUIDE", 	ydToLocGuide);				// 야드To위치Guide
			inGridData.addParam("YD_WRK_CRN", 		"");				// 야드작업크레인(작업자지정 크레인)
			inGridData.addParam("userid", 			"TSYSJ003");		// 수정자
			
			szMsg = "\n\t SSTL_NOS   		: " 	+ sstlNos 
            	  + "\n\t YS_STK_COL_GP     : " 	+ szYS_STK_COL_GP;

      		commUtils.printLog(logId, szMsg, "");
// 작업 예약 등록				
			if(szYS_STK_COL_GP.startsWith("GE")){
// 2025.10.02 특수강 정정 야드
		    	szMsg = "[" + szOperationName + "] 하차작업예약 CbtYsJsp.updbtMvStkWrkBook Method Call";
	      		commUtils.printLog(logId, szMsg, "");

		    	jrRtn = CbtYsJsp.updbtMvStkWrkBook(inGridData);

		    	szMsg = "[" + szOperationName + "] 하차작업예약 CbtYsJsp.updbtMvStkWrkBook Method 종료";
		    	commUtils.printLog(logId, szMsg, "SL");
		    	
	    	}else if(szYS_STK_COL_GP.startsWith("GF")){
// 2025.10.02 대형 봉강 옥외 야드
		    	szMsg = "[" + szOperationName + "] 하차작업예약 EbtYsJsp.updbtMvStkWrkBook Method Call";
	      		commUtils.printLog(logId, szMsg, "");

		    	jrRtn = EbtYsJsp.updbtMvStkWrkBook(inGridData);

		    	szMsg = "[" + szOperationName + "] 하차작업예약 EbtYsJsp.updbtMvStkWrkBook Method 종료";
		    	commUtils.printLog(logId, szMsg, "SL");
		    	
	    	}else if(szYS_STK_COL_GP.startsWith("GD")){
// 2026.01.29 소형봉강 야드
		    	szMsg = "[" + szOperationName + "] 하차작업예약 SbrYsJsp.updbtMvStkWrkBook Method Call";
	      		commUtils.printLog(logId, szMsg, "");

		    	jrRtn = SbrYsJsp.updbtMvStkWrkBook(inGridData);

		    	szMsg = "[" + szOperationName + "] 하차작업예약 SbrYsJsp.updbtMvStkWrkBook Method 종료";
		    	commUtils.printLog(logId, szMsg, "SL");
		    	
	    	} else {

	    		szMsg = "야드 적치열 [" + szYS_STK_COL_GP + "] 빌렛정정야드, 대형 옥외 야드 아님 ";
		    	commUtils.printLog(logId, szMsg, "SL");
		    	
	    	}

// 2025.11.25 작업 예약 Method 에서 return 되면 크레인 작업 작업  			
			// 전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
		    	szMsg	= "[" + methodNm + "] 하차작업예약 updbtMvStkWrkBook Method RETURN sndInterface 처리";
	      		commUtils.printLog(logId, szMsg, "");

//	      		jrRtn.setResultCode(logId);
//				jrRtn.setResultMsg(methodNm);
//
//				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
//				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
	      		
	      		sndRecord = commUtils.addSndData(sndRecord,jrRtn);	
	      		
			}
	    	
////////////////////////////////////////////////////////////////
// 2025.11.19 작업 예약 막음 START	    	
// 2025.10.16 영차 출발시 생성한 작업예약 정보 종료 처리 AND 차량테이블 하차 작업 예약  ID 는 어떻게?	    	
//			szYD_WBOOK_ID, MODIFIER
//			/**********************************************************
//			 * 4. 작업예약/재료 삭제
//			 **********************************************************/
//			// DAO Parameter - Log ID, Method, 수정자 Set
//			JDTORecord jrParam = commUtils.getParam(logId, methodNm, "TSYSJ003");
//
//			jrParam.setField("YD_WBOOK_ID" , szYD_WBOOK_ID);
//			jrParam.setField("MODIFIER"    , "TSYSJ003");
//
//	    	szMsg	= "상차시 등록한 작업 예약 종료 처리 예약ID [" + szYD_WBOOK_ID + "]";
//	    	commUtils.printLog(logId, szMsg, "SL");
//			
//			// 작업예약재료 삭제 (상차 차량 출발시 만든 예약재료 삭제)
//			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL 종료");
//
//			// 작업예약 삭제(상차 차량 출발시 만든 예약 삭제)
//			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook", logId, methodNm, "TB_YS_WRKBOOK 종료");
// 2025.11.19 작업 예약 막음 END	    	
////////////////////////////////////////////////////////////////

			// 차량스케줄 갱신 (YD_CARUD_WRK_BOOK_ID) 새로 등록된 YD_WBOOK_ID UPDATE 
	    	szMsg = "새로 등록된 YD_WBOOK_ID UPDATE YS_STK_COL_GP [" + szYS_STK_COL_GP + "] YD_CAR_SCH_ID [" + szYD_CAR_SCH_ID + "]";
	    	commUtils.printLog(logId, szMsg, "SL");
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, "TSYSJ003");
			jrParam.setField("YS_STK_COL_GP" , szYS_STK_COL_GP);
			jrParam.setField("YD_CAR_SCH_ID" , szYD_CAR_SCH_ID);
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsCarSchCarudWrkBookID", logId, methodNm, "TB_YS_CARSCH 야드하차작업예약ID 등록");

// 2025.12.23 사외 통합(철분말) 영차 도착 이면 하차 개시 전문 전송
			sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "024", "*");
			if("Y".equals(sApplyYnPI) && szYS_STK_COL_GP.startsWith("GH")){
		    	szMsg = "TB_YS_RULE APPNEW 024 사외 통합(철분말) 영차 도착 이면 하차 개시 전문 전송";
		    	commUtils.printLog(logId, szMsg, "SL");

				JDTORecord recPara = JDTORecordFactory.getInstance().create();
				
				recPara.setResultCode(logId);	// Log ID
				recPara.setResultMsg(methodNm);	// Log Method Name
				recPara.setField("WR_DT" 		 , commUtils.getDateTime14()  		); 
				recPara.setField("YS_STK_COL_GP" , szYS_STK_COL_GP.substring(0, 6)  ); 
				// 구내운송 소재차량하차개시
				sndRecord = commUtils.addSndData(sndRecord, commDao.getMsgL3("YSTSJ009", recPara));

				// 하차 차량스케줄 야드차량진행상태, 야드하차개시일시 수정
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID); 
				recPara.setField("WR_DT" 		, commUtils.getDateTime14()     ); 
				recPara.setField("MODIFIER" 	, szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName); 
    	
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCarSchUd", logId, methodNm, "하차 차량스케줄 수정 ");
				
			}
			
	    	
		}catch(Exception e){
			
			szMsg = "소재차량 영차도착 처리 Error:" + e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
    		sndRecord.setTaskCode("-1");
    		return sndRecord;
		}
	
		szMsg = "소재차량 영차도착 처리 (" + szMethodName + ") 완료";
		commUtils.printLog(logId, szMsg, "SL");
		sndRecord.setTaskCode("1");
		return sndRecord;
		
	} //end of procUDMatlCarArr()

	/**
	 * 오퍼레이션명 : 크레인스케줄호출
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public JDTORecord procCallCrnSch(String logId,  JDTORecord rcvMsg, JDTORecord sndRecord)throws JDTOException  {
		String methodNm = "크레인스케줄호출[YsCommCarTSMvSeEJBBean.procCallCrnSch]";
		
		JDTORecordSet rsResult          = null;
		JDTORecord    recInTemp         = null;
		
	    String szMsg              		= "";
//	    String szMethodName       		= "procCallCrnSch";
	    String szWbookId                = "";
	    String szSchCd                  = "";
	    String szEqpId                  = "";
	    
	    try{
	    	commUtils.printLog(logId, methodNm, "S+");
	        
	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
	    	//작업예약ID
	    	szWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"       ));
	    	
	    	//작업예약 id로 작업예약Table를 조회한다.
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");

	    	rsResult = commDao.select(rcvMsg, getYdWrkbook, logId, methodNm, "작업예약 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
				szMsg = "[" + methodNm + "] getYdWrkbook data not found";
	    		commUtils.printLog(logId, szMsg, "SL");
	    		sndRecord.setTaskCode("-1");
	    		return sndRecord;
			}
	    	
	    	rsResult.absolute(1);
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setRecord(rsResult.getRecord());
	    	
	    	//스케줄코드
	    	szSchCd   	= commUtils.trim(recInTemp.getFieldString("YD_SCH_CD"       ));
	   
	    	//스케줄기준Table조회
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	   	
	    	
// 2025.08.27 스케쥴코드가 GE OR GF 이면 추가 (GE 크레인 호기는 2,4,5 다른 특수강 크레인은 1,2)	    	
    		rsResult = commDao.select(recInTemp, getYdSchruleCrn, logId, methodNm, "스케줄기준 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
				szMsg = "[" + methodNm + "] getYdSchrule data not found";
	    		commUtils.printLog(logId, szMsg, "SL");
	    		sndRecord.setTaskCode("-1");
	    		return sndRecord;
			}
	    	
	    	rsResult.absolute(1);
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setRecord(rsResult.getRecord());
	    	recInTemp.setResultCode(logId);	//Log ID
	    	recInTemp.setResultMsg(methodNm);	//Log Method Name
	    	
	    	//크레인설비ID
	    	szEqpId   	= commUtils.trim(recInTemp.getFieldString("YD_WRK_CRN"       ));
	    	
	    	//크레인스케줄MAIN호출 TC :/** 1:블름,2:빌렛,3:봉강,4:선재	 */
	    	String szJMS_TC_CD = "";
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	String szYdGpBay = szSchCd.substring(0, 2);

			if(szEqpId.startsWith("GE")){
		    	// 대형 옥내 스케쥴 기동
				szJMS_TC_CD =  "YSYSJ502";
	    	}else if(szEqpId.startsWith("GF")){
		    	// 대형 옥외 스케쥴 기동
				szJMS_TC_CD =  "YSYSJ602";
	    	}else if(szEqpId.startsWith("GD")){
// 2026.02.05 소형 야드 스케쥴 기동
				szJMS_TC_CD =  "YSYSJ702";
	    	}
	    	
	    	/*
	    	 * 차량도착 시 크레인스케줄메인을 호출할 때 작업예약ID를 명시적을 전달처리한다.
	    	 */
	    	
	    	recInTemp.setField("JMS_TC_CD"	, szJMS_TC_CD);
	    	recInTemp.setField("JMS_TC_CREATE_DDTT"	,  commUtils.getDateTime14());
	    	recInTemp.setField("YD_SCH_CD"	, szSchCd);
	    	recInTemp.setField("YD_EQP_ID"	, szEqpId);
	    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
	    	
	    	
	    	//차량 하차작업 여부 
	    	if(szSchCd.substring(2, 4).equals("PT") && szSchCd.substring(6).equals("LM")) {
	    		recInTemp.setField("CRN_SCH_INS_TYPE", "U");
	    	}
			
	        szMsg = recInTemp.toString();
			commUtils.printLog(logId, szMsg, "");
	    	
	    	
	    	//내부 전송 Data 생성
			sndRecord = commUtils.addSndData(sndRecord, recInTemp);

 		}catch(Exception e){
	
			szMsg = "크레인스케줄 호출 Error:" + e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
    		sndRecord.setTaskCode("-1");
    		return sndRecord;
		}
	
 		commUtils.printLog(logId, methodNm, "S-");

		sndRecord.setTaskCode("1");
		return sndRecord;
	} //end of C3CallCrnSch()
	
	
	
	/**
	 * 오퍼레이션명 : 소재차량 하차작업예약생성( makeCarUdWrkBook )
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public int procInsWrkBookCarUd(String logId, JDTORecord rcvMsg)throws JDTOException  {
		String methodNm = "소재차량 하차작업예약생성[YsCommCarTSMvSeEJBBean.procInsWrkBookCarUd] < " + rcvMsg.getResultMsg();
		
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsCarBookMtl      = null;
		JDTORecord    recPara           = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
	    int     intRtnVal 	        	= 0 ;
	    
	    String szMsg           = "";
	    String szMethodName    = "procInsWrkBookCarUd";
	    String szOperationName = "소재차량하차작업예약생성";
	    String szYD_CAR_SCH_ID = "";
	    String szYD_WBOOK_ID   = "";
	    String szYD_GP         = "";
	    String szREGISTER      = "SYSTEM";
	    String szYD_BAY_GP     = "";
	    String szCUR_SCH_CD    = "";
	    String szSCH_PRIOR     = "";
	    String szTRN_EQP_CD    = "";
	    String szYD_CAR_USE_GP = "";
	    String szARR_WLOC_CD   = "";
	    String szYS_STK_COL_GP   = "";
	    
	    try{
	    	commUtils.printLog(logId, methodNm, "S+");
			szMsg	= "[" + szOperationName + "] 하차 작업예약 생성 START!!";
			commUtils.printLog(logId, szMsg, "SL");
	    	
	    	
	    	szYD_CAR_SCH_ID	= commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID")	);
	    	szTRN_EQP_CD   	= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")	);
	    	szARR_WLOC_CD   = commUtils.trim(rcvMsg.getFieldString("WLOC_CD")		);
	    	
	    	//하차지 개소로  TO 행선 결정
    		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	
	    	recInTemp.setField("YS_STK_COL_GP"	, "" );
	    	recInTemp.setField("ARR_WLOC_CD"	, szARR_WLOC_CD);
	    	recInTemp.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID);
	    	
	    	rsResult = commDao.select(recInTemp, getYdStkcolColGpLike2, logId, methodNm, "적치열 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
				szMsg	= "[" + methodNm + "] 하차지 개소 이상 발생 data not found";
				throw new Exception(szMsg);
			}
	    		    	
	    	rsResult.absolute(1);
    		recOutTemp = JDTORecordFactory.getInstance().create();
    		recOutTemp.setRecord(rsResult.getRecord());
	    	
	    	szYD_GP   			= commUtils.trim(recOutTemp.getFieldString("YD_GP")		);
	    	szYD_BAY_GP			= commUtils.trim(recOutTemp.getFieldString("YD_BAY_GP")	);
	    	szYS_STK_COL_GP		= commUtils.trim(recOutTemp.getFieldString("YS_STK_COL_GP")	);
	    	
	    	//차량이송재료 조회
			szMsg	= "차량이송재료와 저장품 join조회";
			commUtils.printLog(logId, szMsg, "SL");
			
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
	    	
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");

	    	rsCarBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	rsCarBookMtl = commDao.select(recInTemp, getYdCarftmvmtlID, logId, methodNm, "차량재료 조회"); 
	    	
	    	if (rsCarBookMtl == null || rsCarBookMtl.size() <= 0) {
				szMsg = "[" + methodNm + "] 차량재료 조회 data not found";
	    		commUtils.printLog(logId, szMsg, "SL");
	    		
	    		return intRtnVal = -1;
			}
			
		    // 스케줄코드
		    if ("G".equals(szYD_GP) && "F".equals(szYD_BAY_GP) ) {
// 2025.09.09 영차 출발 착지에 대한 스케쥴코드 정의
		    	szCUR_SCH_CD = "GFTR01LM";  						// 대형옥외 이송입고
		    } else if ("G".equals(szYD_GP) && "E".equals(szYD_BAY_GP) ) {
// 2025.10.21 영차 출발 착지에 대한 스케쥴코드 정의
		    	szCUR_SCH_CD = szYS_STK_COL_GP + "LB";  			// 빌렛정정 이송입고
		    }
			
	    	szMsg	= "[" + szOperationName + "] 스케줄코드[" + szCUR_SCH_CD + "]";
	    	commUtils.printLog(logId, szMsg, "SL");    	
	    	//-------------------------------------------------------------------------------------------------------------
	    	
	    	//스케줄코드로 스케줄기준Table조회
			szMsg="스케줄코드로 스케줄기준Table조회";
			commUtils.printLog(logId, szMsg, "SL");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_SCH_CD", szCUR_SCH_CD);
	    	
	    	
	    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
			szMsg="스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.";
			commUtils.printLog(logId, szMsg, "SL");
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");

// 2025.09.09 대형옥외야드는 크레인 1대만 있기 때문에 오류 발생 jSpeed Query 변경
	    	rsResult = commDao.select(recInTemp, getYdSchruleCrn, logId, methodNm, "스케줄 기준 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		szMsg = "["+methodNm+"] 스케줄 기준 조회 data not found";
				commUtils.printLog(logId, szMsg, "SL");
				return intRtnVal = -1;
			}
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			szSCH_PRIOR = commUtils.trim(recPara.getFieldString("YD_WRK_CRN_PRIOR"));	

			//생성한 작업예약ID
			//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
			szYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
			
			if ("".equals(szYD_WBOOK_ID)) {
				throw new Exception("작업예약ID 생성 실패");
			}

			//작업예약항목SETTING
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	
	    	recInTemp.setField("YD_WBOOK_ID",      szYD_WBOOK_ID);
	    	recInTemp.setField("REGISTER",         szREGISTER);
	    	recInTemp.setField("YD_GP",            szYD_GP);
	    	recInTemp.setField("YD_BAY_GP",        szYD_BAY_GP);
	    	recInTemp.setField("YD_AIM_YD_GP",     szYD_GP);
	    	recInTemp.setField("YD_AIM_BAY_GP",    szYD_BAY_GP);
	    	recInTemp.setField("YD_SCH_PRIOR", 	   szSCH_PRIOR);
	    	recInTemp.setField("YD_SCH_CD",        szCUR_SCH_CD);
	    	recInTemp.setField("TRN_EQP_CD",       szTRN_EQP_CD);
	    	recInTemp.setField("YD_CAR_USE_GP",    szYD_CAR_USE_GP);
	    	
			//com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook
////////////////////////////////////////////////////////////////
// 2025.11.19 작업 예약 막음 START	    	
//			commDao.insert(recInTemp, insWrkBook, logId, methodNm, "TB_YS_WRKBOOK");
//			
//	    	//작업예약재료 등록
//	    	for(int Loop_i = 1; Loop_i <= rsCarBookMtl.size(); Loop_i++) {
//	    		rsCarBookMtl.absolute(Loop_i);
//	    		recInTemp  = JDTORecordFactory.getInstance().create();
//	    		recOutTemp = JDTORecordFactory.getInstance().create();
//	    		recOutTemp.setRecord(rsCarBookMtl.getRecord());
//	    		
//	    		recInTemp.setField("YD_WBOOK_ID",    szYD_WBOOK_ID);
//	    		recInTemp.setField("REGISTER",       szREGISTER);
//	    		recInTemp.setField("YS_STK_BED_NO",  commUtils.trim(recOutTemp.getFieldString("YS_STK_BED_NO"       )));
//	    		recInTemp.setField("YS_STK_LYR_NO",  commUtils.trim(recOutTemp.getFieldString("YS_STK_LYR_NO"       )));
//	    		recInTemp.setField("SSTL_NO",        commUtils.trim(recOutTemp.getFieldString("SSTL_NO"       )));
//	    		recInTemp.setField("YD_UP_COLL_SEQ", "" + Loop_i);
//	    		
//	    		recInTemp.setField("YS_STK_SEQ_NO",  commUtils.trim(recOutTemp.getFieldString("YS_STK_SEQ_NO"       ))); // WC 추가
//	    		
//	    		commDao.insert(recInTemp, insWrkBookMtl, logId, methodNm, "TB_YS_WRKBOOKMTL");
//	    		if(intRtnVal == -2) {
//	    			szMsg = "["+methodNm+"] insYdWrkbookmtl parameter error";
//					commUtils.printLog(logId, szMsg, "SL");
//					return intRtnVal = -1;
//	    		}
//	    	}
// 2025.11.19 작업 예약 막음 END	    	
////////////////////////////////////////////////////////////////
	    		    	
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CAR_SCH_ID"			, szYD_CAR_SCH_ID);
	    	recInTemp.setField("YD_CARUD_WRK_BOOK_ID"	, szYD_WBOOK_ID);
	    	recInTemp.setField("YD_CAR_PROG_STAT"		, "A");
			recInTemp.setField("YD_CARUD_LEV_DT"		, commUtils.getDateTime14());
			recInTemp.setField("MODIFIER"				, szREGISTER);
			recInTemp.setField("YD_CARUD_STOP_LOC"		, szYD_GP + szYD_BAY_GP);
			
	    	intRtnVal = commDao.update(recInTemp, updYsCarSchCarWrkUD, logId, methodNm, "TB_YS_CARSCH 등록");
			if(intRtnVal <= 0) {
				szMsg="["+szOperationName+"] 차량스케줄 업데이트 시 존재하지 않습니다.";
				commUtils.printLog(logId, methodNm, "SL");
				throw new DAOException(szMsg);
			}	
			
		}catch(Exception e){
			
			szMsg="차량하차작업예약생성 처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			return intRtnVal = -1;
		}
	
	
		szMsg="차량하차작업예약생성 처리 ("+szMethodName+") 완료";
		commUtils.printLog(logId, szMsg, "SL");
		commUtils.printLog(logId, methodNm, "S-");
		return intRtnVal = 1;
	} //end of procInsWrkBookCarUd()
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 소재차량 개소코드구분 및 상차Lot편성 호출 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procInsCarSch(String logId, String rcvMsg, JDTORecord msgRecord, JDTORecord sndRecord)throws JDTOException  {
		String methodNm = "소재차량 개소코드구분 및 상차Lot편성 호출[YsCommCarTSMvSeEJBBean.procInsCarSch] < " + rcvMsg ;
		JDTORecord recPara 		= null;

		int intRtnVal					= 0 ;
	    String szMsg					= "";
	    String szMethodName				= "procInsCarSch";
	    
	    String szTRN_EQP_CD				= null;
	    String szSPOS_WLOC_CD			= "";
	    String szSPOS_YD_PNT_CD			= "";
	    String szARR_WLOC_CD			= "";
	    String szARR_YD_PNT_CD			= "";
	    String szTRN_WRK_FULLVOID_GP 	= null;
	    String szTRN_EQP_STK_CAPA 		= null;
	    String szCARUD_PAP_LEV_TT 		= null;
	    String szYD_WO_CNCL_YN 			= null;
	    String szTRN_WRK_MTL_GP			= null;
	    String szWLOC_CD				= "";
	    String szYD_PNT_CD				= "";

	    String szYD_CARLD_LEV_LOC		= null;
	    String szYD_CAR_SCH_ID			= null;
	    
		JDTORecordSet 	rsResult    = null;
		JDTORecord    	recInTemp   = null;
	    
// 2025.09.29 공차인 경우 ARR_WLOC_CD(착지개소코드) 가 구내운송 차량이 상차하러 가는 발지개소코드 	    
	    try{
	    	commUtils.printLog(logId, methodNm, "S+");
	    	
	    	szMsg	=	"[" + methodNm + "] 메소드 시작";
	    	commUtils.printLog(logId, szMsg, "SL");
	    	
	    	// 하위모듈에서 EJB Call or JMS Call 유무
	    	szTRN_EQP_CD 			= commUtils.trim(msgRecord.getFieldString("TRN_EQP_CD"			));
	    	szSPOS_WLOC_CD 			= commUtils.trim(msgRecord.getFieldString("SPOS_WLOC_CD"		));
	    	szSPOS_YD_PNT_CD		= commUtils.trim(msgRecord.getFieldString("SPOS_YD_PNT_CD"		));
	    	szARR_WLOC_CD 			= commUtils.trim(msgRecord.getFieldString("ARR_WLOC_CD"			));
	    	szARR_YD_PNT_CD			= commUtils.trim(msgRecord.getFieldString("ARR_YD_PNT_CD"		));
	    	szTRN_WRK_FULLVOID_GP 	= commUtils.trim(msgRecord.getFieldString("TRN_WRK_FULLVOID_GP"	));
	    	szTRN_EQP_STK_CAPA 		= commUtils.trim(msgRecord.getFieldString("TRN_EQP_STK_CAPA"	));
	    	szCARUD_PAP_LEV_TT 		= commUtils.trim(msgRecord.getFieldString("CARUD_PAP_LEV_TT"	));
	    	szYD_WO_CNCL_YN 		= commUtils.trim(msgRecord.getFieldString("YD_WO_CNCL_YN"		));
	    	szTRN_WRK_MTL_GP 		= commUtils.trim(msgRecord.getFieldString("TRN_WRK_MTL_GP"		));
	    	szWLOC_CD 				= commUtils.trim(msgRecord.getFieldString("WLOC_CD"				));
	    	szYD_PNT_CD 			= commUtils.trim(msgRecord.getFieldString("YD_PNT_CD"			));
	    	
	    	// JMS 에 없는 항목 Method Call 전 항목 추가
	    	szYD_CARLD_LEV_LOC 		= commUtils.trim(msgRecord.getFieldString("YD_CARLD_LEV_LOC"	));
	    	
	    	
///////////////////////////////////////////////////////////////////////////////////////////	    	
// 2025.10.20 종료 안된 구내운송 TB_YS_CARSCH(YS_차량스케줄) 있으면 가장빠른 1건 SELECT 후 UPDATE 신규 생성 하지 않음
			/**********************************************************
			* 1. 공차 착지 개소 코드로 차량스케줄을 조회(LOT 편성시 구내운송코드를 알수 없기 때문에 개소 코드만 등록)
			**********************************************************/			
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp 	= JDTORecordFactory.getInstance().create();

// 2025.10.20 공차 출발인 경우 착지개소코드가 도착할 개소코드 	    	
	    	recInTemp.setField("SPOS_WLOC_CD", szARR_WLOC_CD);
	    	
	    	
	    	// 운송장비코드로 차량스케쥴 조회
			rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchToSposWlocCd", logId, methodNm, "차량스케줄을 조회"); 
			if (rsResult == null || rsResult.size() <= 0) {
// 신규 등록			
		    	//----------------------------------------------------------------------------------------
		    	//	차량스케줄을 생성
		    	//----------------------------------------------------------------------------------------
		    	
		    	recPara = JDTORecordFactory.getInstance().create();

				recPara.setField("YD_CAR_SCH_ID",    commDao.getSeqId(logId, methodNm, "CarSch"	));		// 야드차량스케쥴ID
				recPara.setField("YD_EQP_ID",        YsConstant.YD_TS_CAR_EQP_ID				 );		// 야드설비ID
				recPara.setField("YD_CAR_USE_GP",    "L"										 );		// 차량사용구분(L:구내,G:출하)
				recPara.setField("TRN_EQP_CD",       szTRN_EQP_CD								 );		// 운송장비코드
				recPara.setField("YD_EQP_WRK_STAT",  "U"										 );		// 야드설비작업상태(U:공차,L:영차)
				recPara.setField("SPOS_WLOC_CD",     szSPOS_WLOC_CD								 );		// 발지개소코드
				recPara.setField("ARR_WLOC_CD",      szARR_WLOC_CD								 );		// 착지개소코드(공차이면 재료실으러 들어올 위치)
				recPara.setField("YD_CARLD_LEV_LOC", szYD_CARLD_LEV_LOC							 );		// 야드상차출발위치
																							    		// JMS 에 있는 발지,야드포인트로 TB_YD_CARPOINT 테이블 SELECT Query - com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd
																										// 있으면 해당 적치열
				recPara.setField("YD_CARLD_LEV_DT",  commUtils.getDateTime14()					 );		// 상차출발일시
				recPara.setField("YD_CAR_PROG_STAT", YsConstant.YD_CARLD_LEV					 );		// 상차출발상태
				recPara.setField("YD_BAYIN_WO_SEQ",  "9"										 );		// 입동지시순번 - 기본값으로 설정(9)
				recPara.setField("REGISTER",         szMethodName.length() > 10 ? szMethodName.subSequence(0, 10) : szMethodName);	// 등록자

				
				//차량스케줄 등록
	// 2025.09.08 YSYSJ901 전송시 공차 도착 인데 착지 개소 코드가 전송 발지개소코드로 변경
	// 2025.09.10 공차 차량 출발인 경우 ARR_WLOC_CD 항목에 차가 도착할 개소 코드가 있음 				
				
// 2026.04.02 차량 스케쥴 (TB_YS_CARSCH) 테이블 차랑변호 등록 추가
//				intRtnVal = commDao.insert(recPara, insYsCarsch, logId, methodNm, "차량스케줄 등록");
				intRtnVal = commDao.insert(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.insYsCarSch2", logId, methodNm, "차량스케줄 등록");
				
				if( intRtnVal <= 0 ){
					szMsg	= szMethodName + "개소코드[" + szWLOC_CD+"] : 차량스케줄[" + szYD_CAR_SCH_ID + "] 생성 시 오류발생 - 반환값 : " + intRtnVal;
					commUtils.printLog(logId, szMsg, "SL");

					sndRecord.setTaskCode("-1");
					return sndRecord;
				}
				
				szYD_CAR_SCH_ID = commUtils.trim(recPara.getFieldString("YD_CAR_SCH_ID"));
				
	    		szMsg	= methodNm + "개소코드[" + szARR_WLOC_CD + "] : 차량스케줄[" + szYD_CAR_SCH_ID + "] 생성 완료 - 반환값 : " + intRtnVal;
	    		commUtils.printLog(logId, szMsg, "SL");
				
				
			} else {
// 운송장비코드 UPDATE
				szYD_CAR_SCH_ID    = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"   ));
				
		    	recPara = JDTORecordFactory.getInstance().create();

				recPara.setField("MODIFIER",         szMethodName.length() > 10 ? szMethodName.subSequence(0, 10) : szMethodName);	// 등록자
				recPara.setField("TRN_EQP_CD",       szTRN_EQP_CD		);		// 운송장비코드
				recPara.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID	);		// 야드차량스케쥴ID
//				recPara.setField("TRN_EQP_CD",       szTRN_EQP_CD								 );		// 운송장비코드

				/**********************************************************
				* 3. 설비상태 수정
				**********************************************************/
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsCarSchByTrnEqpCd", logId, methodNm, "차량스케쥴 운송장비코드 수정");
				
	    		szMsg	= methodNm + "발지개소코드[" + szSPOS_WLOC_CD + "] : 차량스케줄[" + szYD_CAR_SCH_ID + "] 운송장비코드 [" + szTRN_EQP_CD + "] UPDATE";
    	    	commUtils.printLog(logId, szMsg, "SL");
				
			}
	    	
	    	
///////////////////////////////////////////////////////////////////////////////////////////	    	
	    	
			
			//----------------------------------------------------------------------------------------
	    	//	자동/수동LOT편성 판단 후 자동LOT편성이면 상차LOT편성 모듈 호출하고
	    	//	수동LOT편성이면 차량도착POINT요구모듈을 호출
	    	//----------------------------------------------------------------------------------------
				
			//소재차량도착Point요구 호출
			//record 생성
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setResultCode(logId);	//Log ID
			recPara.setResultMsg(methodNm);	//Log Method Name
			recPara.setField("JMS_TC_CD",               YsConstant.YSYSJ901); 			//"YDYDJ630"); // rcvTSYSJ002
			recPara.setField("JMS_TC_CREATE_DDTT",      commUtils.getDateTime14()); 	//JMSTC생성일시
			recPara.setField("TRN_EQP_CD",              szTRN_EQP_CD);
			recPara.setField("WLOC_CD",                 szARR_WLOC_CD);
			recPara.setField("TRN_WRK_FULLVOID_GP",     szTRN_WRK_FULLVOID_GP);
			recPara.setField("PNT_DMD_DT",              commUtils.getDateTime14());
			
			//소재차량도착Point요구 호출
			sndRecord = commUtils.addSndData(sndRecord, recPara);	
			
			szMsg=methodNm + " 소재차량도착Point요구 모듈 호출 완료";

	    	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    	
		}catch(Exception e){
			
			szMsg=methodNm + "개소코드구분 및 상차Lot편성 호출처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			sndRecord.setTaskCode("-1");
			return sndRecord;
		}
	
	
		szMsg= methodNm + "개소코드구분 및 상차Lot편성 호출처리 완료 - 메소드 끝";
		commUtils.printLog(logId, szMsg, "SL");
		sndRecord.setTaskCode("1");
		commUtils.printLog(logId, methodNm, "S-");
		return sndRecord;
	} //end of procInsCarSch()
	

	
	
	
	/**
	 * 오퍼레이션명 : 차량포인트 통합관리 (기존형태 유지)
 	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */     	
	public boolean YsCarPointinforeg2(String chk 
									, String s_CAR_NO
									, String s_TRN_EQP_CD
									, String s_STACK_COL_GP
									, String szARR_WLOC_CD 
									, String szARR_YD_PNT_CD
									, String s_STAT
									, String logId
									, String mthdNm
									)throws DAOException {
		
		boolean isSuccess 	= 	false;
		int 	iSeq		=	0;
		String 	stkQueryId 	=	"";
		String 	szMsg 		=	"";
		YsPiDAO ysPiDAO 	= 	new YsPiDAO();
		String 	methodNm 	=  	"[YsCommCarTSMvSeEJBBean.YsCarPointinforeg2] < " + mthdNm;

		commUtils.printLog(logId, methodNm, "S+");
		szMsg 	= 	"▣▣▣▣차량포인트 통합관리(START):" + chk + "," + s_CAR_NO + "," + s_TRN_EQP_CD + "," + s_STACK_COL_GP + "," + szARR_WLOC_CD + "," + szARR_YD_PNT_CD + "," + s_STAT + "▣▣▣▣▣";
		commUtils.printLog(logId, szMsg, "ST");
		
		try{

    		szMsg =  "▣▣▣▣차량포인트 통합관리(START):통합작업 시작 ▣▣▣▣▣";
			commUtils.printLog(logId, szMsg, "ST");

// 2025.12.23 YsPiDAO.java requestupdateData Method 사용 하지 않고 직접 UPDATE 
			String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "025", "*");
			
			if("Y".equals(sApplyYnPI)){
				szMsg = "TB_YS_RULE 025 차량포인트 통합관리 ";
	      		commUtils.printLog(logId, szMsg, "");

				JDTORecord recPara = JDTORecordFactory.getInstance().create();
	      		
				if(chk.equals("1")){
					//설비코드로 초기화 하는 경우(구내운송)			 			

					stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdate";
//					iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_TRN_EQP_CD});
					
					recPara.setField("STAT", 		s_STAT			); 
					recPara.setField("TRN_EQP_CD", 	s_TRN_EQP_CD 	); 
					
				}else if(chk.equals("2")){
					//저장위치로 초기화 하는 경우(구내운송)

					stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointstackcolgpupdateCT";
//					iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_STACK_COL_GP});
					
					recPara.setField("STAT", 			s_STAT			); 
					recPara.setField("YS_STK_COL_GP", 	s_STACK_COL_GP 	); 
					
				}else if(chk.equals("3")){
					//저장위치로 차량 포인트 예약 하는 경우(구내운송)

					stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdateC";
//					iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_TRN_EQP_CD,s_STACK_COL_GP});
					
					recPara.setField("STAT", 			s_STAT			); 
					recPara.setField("TRN_EQP_CD", 		s_TRN_EQP_CD 	); 
					recPara.setField("YS_STK_COL_GP", 	s_STACK_COL_GP 	); 
					
				}
    	
				commDao.update(recPara, stkQueryId, logId, methodNm, " 차량포인트 통합관리 ");
				
			}
			else {
				if(chk.equals("1")){
					//설비코드로 초기화 하는 경우(구내운송)			 			

					stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdate";
					iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_TRN_EQP_CD});
				}else if(chk.equals("2")){
					//저장위치로 초기화 하는 경우(구내운송)

					stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointstackcolgpupdateCT";
					iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_STACK_COL_GP});
				}else if(chk.equals("3")){
					//저장위치로 차량 포인트 예약 하는 경우(구내운송)

					stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdateC";
					iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_TRN_EQP_CD,s_STACK_COL_GP});
				} else if(chk.equals("4")){
					//개소코드,포인트로 차량 포인트 예약 하는 경우(구내운송)
					stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointWlocpntupdate";
					iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_TRN_EQP_CD,szARR_WLOC_CD,szARR_YD_PNT_CD});
				}else if(chk.equals("A")){
					//설비코드로 초기화 하는 경우(출하)			 		

					stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdatePT";
					iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_TRN_EQP_CD});
				}else if(chk.equals("B")){
					//저장위치로 초기화 하는 경우(출하)
					
					stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointstackcolgpupdateC";
					iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_STACK_COL_GP});
				}else if(chk.equals("C")){
					//저장위치로 차량 포인트 예약 하는 경우(출하)

					stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdateC2";
					iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_CAR_NO ,s_TRN_EQP_CD,s_STACK_COL_GP});
				} else if(chk.equals("D")){
					
					//개소코드,포인트로 차량 포인트 예약 하는 경우(출하)
					stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointWlocpntupdatePT";
					 iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_CAR_NO, s_TRN_EQP_CD,szARR_WLOC_CD,szARR_YD_PNT_CD});
				}  
				
			}
			
	 
	    	szMsg =  "▣▣▣▣차량포인트 통합관리(END)COUNT:"+iSeq+"▣▣▣▣▣";
			isSuccess = true;
			commUtils.printLog(logId, methodNm, "S-");	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}		

	
	
	

	/**
	 *      [A] 오퍼레이션명 : 공차출발취소(TSYSJ014) 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvTSYSJ014(JDTORecord rcvMsg)throws DAOException  {
		/*
		 * 전문내용 : TSYSJ014(공차출발취소)
		 * TRN_EQP_CD                    	운송장비코드			CHAR	8		구내운송 차량 제원 등록 NO.
		 * TRN_WRK_FULLVOID_GP				운송작업영공구분		CHAR	1		F(영차) / E(공차)
		 */

		String szMsg           			= "";
	    String szOperationName			= "공차출발취소";

		String mthdNm = "공차출발취소[YsCommCarTSMvSeEJBBean.rcvTSYSJ014] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord	sndRecord	= JDTORecordFactory.getInstance().create();
		JDTORecord 	jrRtn 		= JDTORecordFactory.getInstance().create();
		
        String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID        
        if(msgId==null || msgId.equals("")){
        	return sndRecord;
        }
		
	    try {

	    	commUtils.printLog(logId, mthdNm, "S+");
	    	
			commUtils.printParam(logId + "공차출발취소 수신 ", rcvMsg);
			
			
			String sTrnEqpCd      	 = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"         )); 	// 운송장비코드
			String sTrnWrkFullvoidGp = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); 	// 운송작업영공구분[상하차구분 E:공차상태(상차작업),F:영차(하차작업)]
			
	    	String sModifier  		= "TSYSJ014";														// 수정자(Backup Only)
			
			String sCurrDate = commUtils.getDateTime14();
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRN_EQP_CD"	      , sTrnEqpCd        );
			jrParam.setField("TRN_WRK_FULLVOID_GP", sTrnWrkFullvoidGp);
			
			/**********************************************************
			* 1. 차량포인트 해당장비로 CLEAR
			**********************************************************/			
			// 구내운송코드로 초기화 
		    this.YsCarPointinforeg2("1","",sTrnEqpCd,"","","","C",logId,mthdNm);
		    
		    
			/*******************************
			 *  0. 기존 출발정보 유무 확인
			 *******************************/
			/* 기존 출발정보 유무 확인 - com.inisteel.cim.ys.common.dao.YsCommDAO.getListSposYNchk
				SELECT YD_CAR_SCH_ID
				     , SPOS_WLOC_CD
				     , YD_CARLD_WRK_BOOK_ID
				     , YD_CAR_PROG_STAT
				  FROM TB_YS_CARSCH
				 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				   AND DEL_YN     = 'N'
				   AND YD_CAR_PROG_STAT IN ('1','2')
            */
			
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getListSposYNchk", logId, mthdNm, "기존 출발정보 유무 확인");
			
			if (jsCarSch.size() > 0) {
				
				String sSposWlocCd     = commUtils.trim(jsCarSch.getRecord(0).getFieldString("SPOS_WLOC_CD"));
				String sCarldWrkBookId = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CARLD_WRK_BOOK_ID"));
				String ydCarSchId      = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID"   ));
				String ydCarProgStat   = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT"));
				
				commUtils.printLog(logId, "=상차개소           = "	+ sSposWlocCd    , "SL");
				commUtils.printLog(logId, "=취소할 작업예약 ID = "	+ sCarldWrkBookId, "SL");
				commUtils.printLog(logId, "=취소할 차량작업 ID = "	+ ydCarSchId     , "SL");
				commUtils.printLog(logId, "=취소할 차량상태    = "	+ ydCarProgStat  , "SL");
				          
				/***********************************
				 *  1. 신규야드스케쥴 존재여부 체크
				 ***********************************/
				/* 크레인스케쥴 체크 - com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnschByWrkId
				SELECT *
				  FROM TB_YS_CRNSCH
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND DEL_YN='N'
				 ORDER BY YD_CRN_SCH_ID
				*/
				jrParam.setField("YD_WBOOK_ID"	, sCarldWrkBookId);
				
// 2025.11.04  차량작업 ID SET 추가				
				jrParam.setField("YD_CAR_SCH_ID", ydCarSchId     );
				
				JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnschByWrkId", logId, mthdNm, "크레인스케쥴 체크");
				if (rsResult.size() > 0) {
					szMsg = "신규야드스케쥴 존재여부 체크 결과 YD_CRN_SCH_ID : " 
					      + commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID")) 
					      + " 검색되어 차량출발취소 처리가 불가합니다.";
					commUtils.printLog(logId, szMsg, "S-");
							
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
				/* 작업예약 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getWrkbookForTsCncl
				SELECT YD_TO_LOC_GUIDE AS YD_STK_COL_GP 
				     , YD_WBOOK_ID
				  FROM TB_YS_WRKBOOK
				 WHERE TRN_EQP_CD = (SELECT TRN_EQP_CD 
				                       FROM TB_YS_WRKBOOK
				                      WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				                        AND DEL_YN = 'N')
				   AND DEL_YN = 'N'  
				 */
				
				JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWrkbookForTsCncl", logId, mthdNm, "작업예약 조회");

				String ydStkColGp = "";
				
				for (int i = 0; i < jsWrkBook.size(); ++i) {
					
					ydStkColGp = jsWrkBook.getRecord(0).getFieldString("YD_STK_COL_GP");
					
					jrParam.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(i).getFieldString("YD_WBOOK_ID"));
					jrParam.setField("DEL_YN"       , "Y"            );
					jrParam.setField("TRN_EQP_CD"   , sTrnEqpCd      );
					jrParam.setField("YD_CAR_SCH_ID", ydCarSchId     );
					/* 작업예약 재료 삭제 - com.inisteel.cim.ys.common.dao.YsCommDAO.delWrkbookMtl 
					UPDATE TB_YS_WRKBOOKMTL  
					   SET MOD_DDTT = SYSDATE
					     , MODIFIER = :V_MODIFIER
					     , DEL_YN   = :V_DEL_YN
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					 */
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.delWrkbookMtl", logId, mthdNm, "작업예약 재료 삭제");
					
					/* 작업예약 삭제 - com.inisteel.cim.ys.common.dao.YsCommDAO.delYdWrkbook
					UPDATE TB_YS_WRKBOOK
					   SET MOD_DDTT = SYSDATE
					     , MODIFIER = :V_MODIFIER
					     , DEL_YN   = :V_DEL_YN
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					 */
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.delYdWrkbook", logId, mthdNm, "작업예약 삭제");
				}
				
				/* 예약위치정보삭제 - com.inisteel.cim.ys.common.dao.YsCommDAO.delYDStkBookLoc
				UPDATE TB_YS_STKCOL
				   SET MOD_DDTT = SYSDATE 
				     , MODIFIER = :V_MODIFIER
				     , YD_STK_COL_ACT_STAT = 'C'
				     , YD_CAR_USE_GP       = ''
				     , TRN_EQP_CD          = ''
				 WHERE TRN_EQP_CD = :V_TRN_EQP_CD 
				 */
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.delYDStkBookLoc", logId, mthdNm, "TB_YS_STKCOL CLEAR");
				
				/* 차량스케줄정보삭제 - com.inisteel.cim.ys.common.dao.YsCommDAO.delCarschID
				UPDATE TB_YS_CARSCH
				   SET DEL_YN = 'Y'
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.delCarschID", logId, mthdNm, "차량스케줄정보삭제");

// **************************************************				
// 2025.08.27 특수강 에는 TB_YS_CARPOINT 테이블 없음				
//				/* 
//				UPDATE TB_YS_CARPOINT
//				   SET TRN_EQP_CD = NULL
//				     , YD_STK_COL_ACT_STAT = DECODE(CARD_NO,NULL,(DECODE(YD_STK_COL_ACT_STAT,'N','N',:V_STAT)),YD_STK_COL_ACT_STAT)
//				     , MOD_DDTT = SYSDATE
//				     , MODIFIER = :V_MODIFIER
//				 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
//				   AND MOD_DDTT <> SYSDATE 
//				   AND DEL_YN = 'N'
//				*/
//				jrParam.setField("STAT"  		, "C"); 
//		    	
//		    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.YsCommCarTSMvSeEJBBean.carpointtrneqpcdupdate", logId, mthdNm, "TB_YS_CARPOINT 수정");
// **************************************************				
				
				/********************************************************
				 * 3. 위치대기차량 입동지시 요구
				 ********************************************************/
		    	jrParam.setField("YD_STK_COL_GP", ydStkColGp);

		    	JDTORecordSet jsCarSchNext = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getTsNextTrnEqp", logId, mthdNm, "차량스케줄 조회");
		    	if (jsCarSchNext.size() > 0) {
		    		
		     		String sTrnEqpCdNext         = jsCarSchNext.getRecord(0).getFieldString("TRN_EQP_CD");
		    		String sTrnWrkFullvoidGpNext = jsCarSchNext.getRecord(0).getFieldString("TRN_WRK_FULLVOID_GP");
		    		String sWlocCdNext           = jsCarSchNext.getRecord(0).getFieldString("WLOC_CD");
		    		
		    		jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("TRN_EQP_CD"			, sTrnEqpCdNext        );	
					jrParam.setField("WLOC_CD"				, sWlocCdNext          );	
					jrParam.setField("TRN_WRK_FULLVOID_GP"	, sTrnWrkFullvoidGpNext);	
					jrParam.setField("PNT_DMD_DT"			, sCurrDate  );			
					
					JDTORecord jrYdMsg = this.rcvTSYSJ002(jrParam);
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
		    	}
			
			} else {
				
				szMsg="기존 출발정보 없음[운송장비코드 : " + sTrnEqpCd + " ]" ;
				commUtils.printLog(logId, szMsg, "SL");
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
	 * [A] 오퍼레이션명 : 소재차량대기장 도착(TSYSJ006)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvTSYSJ006(JDTORecord rcvMsg) throws DAOException  {
		/*
		 * 전문내용 : TSYSJ006(소재차량대기장 도착)
		 * CR_PLNT_GP				냉연공장구분			CHAR	4	
		 * TRN_EQP_CD				운송장비코드			CHAR	8
		 * ARR_WLOC_CD				착지개소코드			CHAR	5
		 * ARR_YD_PNT_CD			착지야드포인트코드		CHAR	4
		 * TRN_WRK_FULLVOID_GP		운송작업영공구분코드	CHAR	1
		 * TRN_EQP_STK_CAPA			운송장비적재능력		NUMBER	22
		 * CAR_ARR_DT				차량도착일시			CHAR	14
		 * MSG_GP					MSG_GP			CHAR	1
		 */
	    String szMsg  = "";
	    String szOperationName		= "소재차량대기장 도착";
	    
		String mthdNm = "소재차량대기장 도착[YsCommCarTSMvSeEJBBean.rcvTSYSJ006] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		String msgId  = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID

		JDTORecord 	jrRtn 		= JDTORecordFactory.getInstance().create();
		JDTORecord	sndRecord	= JDTORecordFactory.getInstance().create();
		
        
        if(msgId==null || msgId.equals("")){
        	return sndRecord;
        }

        szMsg="["+szOperationName+"] 전문수신 : TCCODE=" +msgId ;
		
	    try {
	    	 
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId + "소재차량대기장 도착 수신 ", rcvMsg);
			
	    	//수신항목 변수 저장
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
			/* 차량스케줄 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschByTrnEqpCd
			SELECT *
			  FROM (
			        SELECT    YD_CAR_SCH_ID
			                , YD_CARUD_CMPL_DT
			          FROM TB_YS_CARSCH A
			         WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			           AND DEL_YN     ='N'
			         ORDER BY YD_CAR_SCH_ID DESC
			                , YD_CARUD_CMPL_DT DESC
			       ) A
			 WHERE ROWNUM <= 1
			 */
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschByTrnEqpCd", logId, mthdNm, "차량스케줄 조회");
			if (jsCarSch.size() > 1) {
				commUtils.printLog(logId, "차량스케줄 조회 오류 ["+jsCarSch.size()+"건]", "S-");
				throw new DAOException("스케줄 조회 시 오류발생 - 운송장비코드로 차량스케줄이 여러건["+jsCarSch.size()+"]이 존재합니다");
			}
			
			String ydCarSchId = jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID");
			jrParam.setField("YD_CAR_SCH_ID"		, ydCarSchId);
			
			/**********************************************************
			* 대기장 조회
			**********************************************************/
			String ydBayInPnt = "GETR11"; // 빌렛정정 남1문 default 값
			
			if ("E".equals(sTrnWrkFullvoidGp)) {
// 공차 대기장 도착 : 착지 개소 코드				
				jrParam.setField("SPOS_WLOC_CD"		, sArrWlocCd);
				
// 차량이 들어갈 개소 코드에 매치 되는 야드적치열구분 검색		
				
				/* 개소 코드로 적치열 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getWLocToYdStkColGp 
				SELECT YS_STK_COL_GP, WLOC_CD, YD_PNT_CD, YD_STK_COL_ACT_STAT
				FROM   TB_YS_STKCOL
				WHERE  1=1
				AND    WLOC_CD = :V_WLOC_CD
				-- AND    YD_STK_COL_ACT_STAT = 'C' -- 비활성화
				 */
				
				JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWLocToYdStkColGp", logId, mthdNm, "개소 코드로 적치열 조회");
				
				if (jsStock.size() > 0) {
					ydBayInPnt  = commUtils.nvl(jsStock.getRecord(0).getFieldString("YS_STK_COL_GP"), "GETR11"); 	// 상차도 ex)GETR11
					jrParam.setField("YD_CARLD_STOP_LOC", ydBayInPnt);	
				}
			}

			
			if ("F".equals(sTrnWrkFullvoidGp)) {
				/* 차량포인트 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getListloadStoppointGDh
				SELECT D.YS_STK_COL_GP 
				     , D.WLOC_CD
				     , D.YD_PNT_CD
				  FROM TB_YS_CARFTMVMTL   A
				     , TB_YS_STKCOL       D
				     , TB_YS_STOCK        ST     
				 WHERE 1=1
				   AND D.YS_STK_COL_GP   LIKE 'G_TR%'
				   AND A.YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
				   AND D.DEL_YN          = 'N'	
				   AND A.SSTL_NO          = ST.SSTL_NO
				 GROUP BY D.YS_STK_COL_GP 
				        , D.WLOC_CD
				        , D.YD_PNT_CD
				 */
				JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getListloadStoppointGDh", logId, mthdNm, "차량포인트 조회");
				if (jsRst.size() > 0) {
					ydBayInPnt = commUtils.nvl(jsRst.getRecord(0).getFieldString("YD_STK_COL_GP"), "GETR11");	 // 하차도 ex)GETR11
					jrParam.setField("YD_CARUD_STOP_LOC", ydBayInPnt);
				} 
			}
			
			/***************************************
	    	 * 차량스케줄 상하차 포인트 수정
	    	 ***************************************/
			jrParam.setField("WAIT_ARR_DDTT"		 , sCarArrDt);	//대기장도착시간
			/* 차량스케줄 수정 - com.inisteel.cim.ys.common.dao.YsCommDAO.updCarSchTSYSJ002
			UPDATE TB_YS_CARSCH
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
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID	    	 */
	    	commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCarSchTSYSJ002", logId, mthdNm, "차량스케줄 수정");
			
			/***************************************
	    	 * 소재차량도착Point요구
	    	 ***************************************/
			commUtils.printLog(logId, "2. 소재차량도착Point요구 모듈 호출", "SL");
    		jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRN_EQP_CD"			, sTrnEqpCd        );	
			jrParam.setField("WLOC_CD"				, sArrWlocCd       );	
			jrParam.setField("TRN_WRK_FULLVOID_GP"	, sTrnWrkFullvoidGp);	
			jrParam.setField("PNT_DMD_DT"			, sCurrDate        );			
			
			JDTORecord jrYdMsg = this.rcvTSYSJ002(jrParam);
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
	 *      [A] 오퍼레이션명 : 이송지시 상차 완료 UPDATE
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void updPbStlFrToMoveUp(JDTORecord jrParam) throws DAOException {
	    String szMsg           		= "";
		String mthdNm = "이송지시 상차완료 UPDATE[YsCommCarTSMvSeEJBBean.updPbStlFrToMoveUp]";
// 2026.02.05 logId 변경		
//        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	
        String logId = jrParam.getResultCode();	

		try {

			commUtils.printLog(logId, mthdNm, "S+");

	        szMsg = jrParam.toString();
			commUtils.printLog(logId, szMsg, "");
			
			// 이송지시 상차 완료 UPDATE
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.updPbStlFrToMoveUp", logId, mthdNm, "이송지시 상차 완료 UPDATE");

			commUtils.printLog(logId, mthdNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
		  
	
	/**
	 *      [A] 오퍼레이션명 : 이송지시 하차 완료 UPDATE
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void updPbStlFrToMoveDown(JDTORecord jrParam) throws DAOException {
	    String szMsg           		= "";
		String mthdNm = "이송지시 하차완료 UPDATE[YsCommCarTSMvSeEJBBean.updPbStlFrToMoveDown]";
// 2026.02.05 logId 변경		
//        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	
        String logId = jrParam.getResultCode();	

		try {

			commUtils.printLog(logId, mthdNm, "S+");

	        szMsg = jrParam.toString();
			commUtils.printLog(logId, szMsg, "");
			
			
			// 이송지시 하차 완료 UPDATE
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.updPbStlFrToMoveDown", logId, mthdNm, "이송지시 하차 완료 UPDATE");
			
			commUtils.printLog(logId, mthdNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
		  

	/**
	 *      [A] 오퍼레이션명 : TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료) 종료
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void delPrepSch(JDTORecord jrParam) throws DAOException {
	    String szMsg           		= "";
		String mthdNm = "준비스케줄 종료[YsCommCarTSMvSeEJBBean.delPrepSch]";
// 2026.02.05 logId 변경		
//        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	
        String logId = jrParam.getResultCode();	

		JDTORecordSet rsResult		= null;
		JDTORecord 	recInTemp		= null;		
		JDTORecord	recOutTemp  	= null;

		try {

			commUtils.printLog(logId, mthdNm, "S+");

	        szMsg = jrParam.toString();
			commUtils.printLog(logId, szMsg, "");

		    String szYD_PREP_SCH_ID 	= "";										// 야드준비스케쥴ID

// 2025.11.07 대상 재료 정보를 작업예약 테이블에서 크레인작업재료 테이블로 변경 
		    String szYD_CRN_SCH_ID 		= jrParam.getFieldString("YD_CRN_SCH_ID");	// 크레인작업ID
		    
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();

			recInTemp.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
			
			rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsPrepMtl", logId, mthdNm, "준비스케줄 조회"); 
			if (rsResult == null || rsResult.size() <= 0) {
				szMsg	=	"[" + mthdNm + "] YD_CRN_SCH_ID [" + szYD_CRN_SCH_ID + "]로 준비스케줄 조회 결과 없음";
				commUtils.printLog(logId, szMsg, "SL");
				
				return;
				
			} else if (rsResult.size() > 1) {
				szMsg	=	"[" + mthdNm + "] YD_CRN_SCH_ID [" + szYD_CRN_SCH_ID + "]로 준비스케줄 조회 결과 1건 이상";
				commUtils.printLog(logId, szMsg, "SL");

				return;
				
			}
		    
			rsResult.absolute(1);			
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord());
			
			szYD_PREP_SCH_ID      	= commUtils.trim(recOutTemp.getFieldString("YD_PREP_SCH_ID")); 
		    
			jrParam.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);

			szMsg	=	"[" + mthdNm + "] YD_PREP_SCH_ID [" + szYD_PREP_SCH_ID + "] 야드준비스케쥴ID";
			commUtils.printLog(logId, szMsg, "SL");
			
			// TB_YS_PREPMTL(YS_준비재료) 종료
// 2025.11.07 준비스케쥴 재료 전체 종료 에서 크레인 작업 재료단위 종료로 변경			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.delYsPrepMtl", logId, mthdNm, "TB_YS_PREPMTL(YS_준비재료) 종료");

			// TB_YS_PREPSCH(YS_준비스케줄) 종료
// 2025.11.07 준비스케쥴 재료가 없으면 종료로 변경			
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsPrepMtlChk", logId, mthdNm, "준비스케줄 재료 조회"); 
			if (rsResult == null || rsResult.size() <= 0) {
				szMsg	=	"[" + mthdNm + "] YD_PREP_SCH_ID [" + szYD_PREP_SCH_ID + "]로 준비스케줄 재료 조회 결과 없음";
				commUtils.printLog(logId, szMsg, "SL");
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.delYsPrepSch", logId, mthdNm, "TB_YS_PREPSCH(YS_준비스케줄) 종료");
				
			} else if (rsResult.size() > 0) {
				szMsg	=	"[" + mthdNm + "] YD_PREP_SCH_ID [" + szYD_PREP_SCH_ID + "]로 준비스케줄 재료 조회 결과 1건 이상";
				commUtils.printLog(logId, szMsg, "SL");
				
			}
			
			
			commUtils.printLog(logId, mthdNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
		  

	
	/**
	 * 오퍼레이션명 : 남42문 소재차량 공차도착 실적
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord proc42GateCarArr(String logId, JDTORecord rcvMsg, JDTORecord recCarSch, JDTORecord rcvMsgCol)throws JDTOException  {

		String methodNm = "소재차량 공차도착 실적[YsCommCarTSMvSeEJBBean.proc42GateCarArr] < " + rcvMsg.getResultMsg();
		
		JDTORecordSet rsResult          = null;
		JDTORecord    recInTemp         = null;
//		JDTORecord    recOutTemp        = null;
		JDTORecord    sndRecord         = JDTORecordFactory.getInstance().create();
		
	    int intRtnVal 		   	= 0 ;
	    
	    String szMsg           	= "";
	    String szMethodName    	= "proc42GateCarArr";
	    String szREG_MOD_USER   = "42GateCarArr";

	    String szTRN_EQP_CD    	= "";
	    String szARR_WLOC_CD   	= "";
	    String szARR_YD_PNT_CD 	= "";
	    String szYS_STK_COL_GP 	= "";
	    String szYD_WBOOK_ID   	= "";
	    String szYD_SCH_CD		= null;
	    String szYD_CAR_SCH_ID 	= "";
	    String szCOL_TRN_EQP_CD	= "";
	    String szCARD_NO        = "";
	    String szMSG_GP			= null;
	    String szYD_PNT_CD		= null;
	    String szYD_CARLD_STOP_LOC = null;
	    String szYD_CAR_PROG_STAT  = null;
	    String szYD_STK_COL_ACT_STAT = "";

	    boolean bIsReplacable	= false;

	    try{

	    	commUtils.printLog(logId, methodNm, "S+");
	    	
	    	//차량 SCH 정보
			szYD_CAR_SCH_ID   	= commUtils.trim(recCarSch.getFieldString("YD_CAR_SCH_ID")	); 
	    	szARR_YD_PNT_CD 	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")		); 
	    	szARR_WLOC_CD   	= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")		); 
	    	szTRN_EQP_CD   		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")		); 

			//수신된 차량 정지위치(COL) 정보
	    	szYS_STK_COL_GP   		= commUtils.trim(rcvMsgCol.getFieldString("YS_STK_COL_GP")); 

			/**********************************************************
			* 1. 차량스케줄 갱신 (실적등록된 상차위치로)
			* 1.1 상차스케쥴 및 포인트
			**********************************************************/			

			szMsg	= "****** 차량스케줄 갱신 (실적등록된 상차위치로)";
			commUtils.printLog(logId, methodNm, "SL");
    		
    		recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CAR_SCH_ID", 	   	szYD_CAR_SCH_ID);
	    	recInTemp.setField("YD_CARLD_WRK_BOOK_ID", 	"");
	    	recInTemp.setField("YD_STK_COL_ACT_STAT",  	"L"							);	//적치가능
	    	recInTemp.setField("YD_PNT_CD1",  			szARR_YD_PNT_CD				);
	    	recInTemp.setField("YD_CAR_PROG_STAT",     	YsConstant.YD_CARLD_ARR		);
	    	recInTemp.setField("YD_CARLD_STOP_LOC", 	szYS_STK_COL_GP				);	 // 실제차량이 들어온 상차정지위치를 재등록한다.    	
	    	recInTemp.setField("YD_CARLD_ARR_DT", 		commUtils.getDateTime14()	);	//상차도착
	    	recInTemp.setField("ARR_WLOC_CD",  			szARR_WLOC_CD				);
	    	                           
// 2025.08.26 UPDATE 항목에 TRN_EQP_CD 추가
	    	recInTemp.setField("TRN_EQP_CD",  			szTRN_EQP_CD);					// 운송장비코드
	    	recInTemp.setField("MODIFIER", 				commUtils.getMsgId(rcvMsg));	// 전문 I/F ID 
	    	
	    	intRtnVal = commDao.update(recInTemp, updYdCarSchCarWrkBook2, logId, methodNm, "TB_YS_CARSCH 갱신");
			if(intRtnVal <= 0) {
				szMsg="["+methodNm+"] 차량스케줄 업데이트 시 존재하지 않습니다.";
				commUtils.printLog(logId, methodNm, "SL");
				throw new DAOException(szMsg);
			}	
	    	
			/**********************************************************
			* 1. 차량 도착열(COL) 등록
			* 1.1 해당장비로 CLEAR
			* 1.2 해당열 등록
			**********************************************************/			
			szMsg	= "****** 차량 도착열(COL) 등록";
			commUtils.printLog(logId, methodNm, "SL");

			recInTemp = JDTORecordFactory.getInstance().create();			 
			recInTemp.setField("TRN_EQP_CD",     szTRN_EQP_CD);
			
			
			intRtnVal = commDao.update(recInTemp, updYdStkcolTrnEqpCdToNull, logId, methodNm, "TB_YS_STKCOL CLEAR");
			if(intRtnVal < 0) {
				szMsg = "["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"] - 예약된 차량도착point 삭제 시 오류발생 - 반환값 : " + intRtnVal;
				commUtils.printLog(logId, szMsg, "SL");
			} else {			
				szMsg = "["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"] - 예약된 차량도착point 삭제 성공";
				commUtils.printLog(logId, szMsg, "SL");
			}
			
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
	    	recInTemp.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
	    	recInTemp.setField("YD_STK_COL_ACT_STAT", "L");
	    	recInTemp.setField("YD_CAR_USE_GP"		, "L");			// WC 추가
			recInTemp.setField("MODIFIER"			, szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);

			
	    	intRtnVal = commDao.update(recInTemp, updYdStkcolByColActStat, logId, methodNm, "TB_YS_STKCOL 등록");
			if(intRtnVal <= 0) {
				szMsg="[" + methodNm + "] 적치열[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
				commUtils.printLog(logId, methodNm, "SL");

	    		return sndRecord;

			}

			/**********************************************************
			* 1. 차량포인트 등록
			* 1.1 해당장비로 CLEAR
			* 1.2 해당차량포인트 등록
			**********************************************************/			
			//설비코드로 초기화 
		    this.YsCarPointinforeg2("1","",szTRN_EQP_CD,"","","","C",logId,methodNm);
		    
		    //저장위치로 차량 포인트 예약
		    this.YsCarPointinforeg2("3","",szTRN_EQP_CD,szYS_STK_COL_GP,"","","L",logId,methodNm);
			
		    
			/**********************************************************
			* 1. 적치베드 활성화
			**********************************************************/			
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
			recInTemp.setField("YD_STK_BED_ACT_STAT", "L");
			
			intRtnVal = commDao.update(recInTemp, updYdStkbedYdStkColGp, logId, methodNm, "TB_YS_STKBED 등록");
			if(intRtnVal <= 0) {
				szMsg="[" + methodNm + "] 적치BED[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
				commUtils.printLog(logId, methodNm, "SL");
				throw new DAOException(szMsg);
			}
			
			
			
			/**********************************************************
			* 차량이송재료(TB_YS_CARFTMVMTL) 상차 등록
			**********************************************************/			
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", 	szYD_CAR_SCH_ID ); 
			recPara.setField("MODIFIER" , 		szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName); 

			intRtnVal = commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.upd42GateCarMtlIns", logId, methodNm, " 남42문 상차 이송재료 등록 ");
			if(intRtnVal <= 0) {
				szMsg="[" + methodNm + "]남42문 상차 이송재료가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new DAOException(szMsg);

			}

			/**********************************************************
			* 상차 차량스케줄 수정
			**********************************************************/			
			recPara.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
			recPara.setField("YD_WBOOK_ID" 		, "" ); 
			recPara.setField("YS_STK_COL_GP" 	, "GETR42" ); 
			recPara.setField("YD_CAR_USE_GP" 	, "L" ); 
			recPara.setField("WR_DT" 			, commUtils.getDateTime14()); //현재시각 
			recPara.setField("YD_CAR_SCH_ID" 	, szYD_CAR_SCH_ID ); 
			recPara.setField("MODIFIER" 		, szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName); 

			commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN7YSL006CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");
			

			/**********************************************************
			* 남42문 상차 재료 적치단 CLEAR
			**********************************************************/			
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("MODIFIER" , 		szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName); 
			recPara.setField("YD_STK_LYR_ACT_STAT", "E");
			recPara.setField("YD_STK_LYR_MTL_STAT", "E");
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID); 
	    	        
			intRtnVal = commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear3", logId, methodNm, "남42문 상차 재료 적치단 CLEAR");
			
			if(intRtnVal <= 0) {
				szMsg="[" + methodNm + "] 적치단[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new DAOException(szMsg);

			}

			/**********************************************************
			* 이송지시 상차 완료 UPDATE
			**********************************************************/
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setResultCode(logId);	//Log ID
			recPara.setResultMsg(methodNm);	//Log Method Name
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID); 
			recPara.setField("MODIFIER"     , szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);

			this.updPbStlFrToMoveUp(recPara);
			
			/**********************************************************
			* TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료) 종료
			**********************************************************/

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID); 
			recPara.setField("MODIFIER"     , szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);

			commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.delYsPrepSch", logId, methodNm, "남42문 준비스케줄 종료");

			commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.delYsPrepMtl", logId, methodNm, "남42문 준비재료 종료");

			/**********************************************************
			* 구내운송 상차 개시(YSTSJ007), 상차 완료(YSTSJ008) 전송
			**********************************************************/
			
			// 구내운송 상차 개시(YSTSJ007)
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID); 
			
			recPara.setField("JMS_TC_CD"         , "YSTSJ007"); 				// JMSTC코드
			recPara.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());	// JMSTC생성일시
			recPara.setField("TRN_EQP_CD"        , szTRN_EQP_CD); 				// 운송장비코드
			recPara.setField("SPOS_WLOC_CD"      , "S3Y99"); 					// 발지개소코드
			recPara.setField("SPOS_YD_PNT_CD"    , "2E04"); 					// 발지야드포인트코드
			recPara.setField("ARR_WLOC_CD"       , "S3Y21"); 					// 착지개소코드
			recPara.setField("TRN_WRK_ST_DT"     , commUtils.getDateTime14());	// 운송작업시작일시

			sndRecord = commUtils.addSndData(sndRecord, recPara);
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("MODIFIER", 		szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
			recPara.setField("ARR_WLOC_CD", 	"S3Y21"); 					// 착지개소코드
			recPara.setField("YD_WBOOK_ID", 	"");
			recPara.setField("WR_DT", 			commUtils.getDateTime14());
			recPara.setField("YD_CAR_SCH_ID", 	szYD_CAR_SCH_ID); 			// 야드차량스케쥴ID
			// 상차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정

			commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");
			
			
			String currDt = commUtils.getDateTime14(); //현재시각
			
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
			
			recPara.setField("YD_WBOOK_ID" 		, "" ); 
			recPara.setField("YS_STK_COL_GP" 	, "GETR42" );  
			recPara.setField("WR_DT" 			, currDt ); 
			recPara.setField("YD_CAR_SCH_ID" 	, szYD_CAR_SCH_ID ); 
			recPara.setField("MODIFIER" 		, szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName); 

			commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN7YSL006CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");
			
			
			// 상차 완료(YSTSJ008)
			szMsg	= "[" + methodNm + "] 남42문 구내운송 상차 완료 전송 전";
			commUtils.printLog(logId, szMsg, "S+");

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID); 

			sndRecord = commUtils.addSndData(sndRecord, commDao.getMsgL3("YSTSJ008", recPara));

			szMsg	= "[" + methodNm + "] 남42문 구내운송 상차 완료 전송 후";
			commUtils.printLog(logId, szMsg, "S-");
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setResultCode(logId);	//Log ID
			recPara.setResultMsg(methodNm);	//Log Method Name
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID); 
			recPara.setField("YD_CRN_SCH_ID", ""); 
			recPara.setField("MODIFIER"     , szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
			
			// 이송지시 상차 완료 UPDATE
			this.updPbStlFrToMoveUp(recPara);
			
//TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료) 종료
			recPara.setField("YD_WBOOK_ID" 		, "" ); 

			this.delPrepSch(recPara);
			
			/**********************************************************
			* 빌렛정정출하상 추출완료(YSM4L215) 전송
			**********************************************************/
			
			szMsg	= "[" + methodNm + "] 빌렛정정출하상 추출완료(YSM4L215) 전송 전";
			commUtils.printLog(logId, szMsg, "S+");

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_EQP_ID"     , "GEPC05");	// 운송작업시작일시
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID); 
			
			sndRecord = commUtils.addSndData(sndRecord, commDao.getMsgL2("YSM4L215", recPara));
			
			szMsg	= "[" + methodNm + "] 빌렛정정출하상 추출완료(YSM4L215) 전송 후";
			commUtils.printLog(logId, szMsg, "S-");
			
			
		}catch(Exception e){
			
			szMsg	="[" + methodNm + "] Error:" + e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	

	    
		szMsg	= "[" + methodNm + "] 메소드 완료";
		commUtils.printLog(logId, szMsg, "S-");
		
		sndRecord.setTaskCode("1");
 		return sndRecord;
	    
	}
	

	/**
	 * 상차 완료 기준 조회
	 * 
	 * @param JDTORecord recCarSch
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public String carLdCmplYn(String logId, JDTORecord recCarSch)throws JDTOException  {
		String mthdNm 		= "상차 완료 기준 조회[YsCommCarTSMvSeEJBBean.carLdCmplYn] ";
		String carLdCmplYn 	= "N";
		String szMsg		= "";
		String sApplyYnPI 	= "N"; 
		try {
			commUtils.printLog(logId, mthdNm, "S+");

// 2025.12.03 차량 상차완료 ON/OFF 기준관련 항목 추가 					
			String ydDnWrLoc  	= commUtils.trim(recCarSch.getFieldString("YS_DN_WR_LOC"));
			String ydCarSchId  	= commUtils.trim(recCarSch.getFieldString("YD_CAR_SCH_ID"));
			
			commUtils.printParam("recCarSch", recCarSch);

			szMsg	= "상차 완료 기준 조회  야드차량스케쥴ID [" + ydCarSchId + "] 차량 위치 [" + ydDnWrLoc + "]";
			commUtils.printLog(logId, szMsg, "SL");

// 차량 포인트에 따른 rule
///////////////////////////////////////////////////////////			
//2025.12.04 설비 보급 이나 추출시 Seq 역순 등록
			sApplyYnPI = commDao.ApplyYnPI(logId, mthdNm, "APPNEW", "015", "*");
			String dnLoc = ydDnWrLoc.substring(0, 6);
			
			if("Y".equals(sApplyYnPI)){
				szMsg = "TB_YS_RULE 015 대형압연/빌렛 정정 야드 차량 자동 상차 완료 ON/OFF ";
				commUtils.printLog(logId, szMsg, "");
				
				szMsg	= dnLoc + " 자동상차완료 추가 로직 적용";
				commUtils.printLog(logId, szMsg, "SL");
			
				if("GFTR11".equals(dnLoc)) {
					// 대형옥외 봉강 야드
					szMsg	= "차량 위치 [" + dnLoc + "] ";
					commUtils.printLog(logId, szMsg, "SL");

					sApplyYnPI = commDao.ApplyYnPI(logId, mthdNm, "GF0001", dnLoc, "*");
					if("N".equals(sApplyYnPI)){
	
						szMsg	= "차량 위치 [" + ydDnWrLoc + "] 자동상차완료 OFF";
						commUtils.printLog(logId, szMsg, "SL");
	
						return carLdCmplYn;
						
					}
				} else if(dnLoc.startsWith("GDTR")) {
// 2026.01.28 소형  야드  우선 SKIP					
					
				}
				else {
					szMsg	= "차량 위치 [" + dnLoc + "] ";
					commUtils.printLog(logId, szMsg, "SL");

					sApplyYnPI = commDao.ApplyYnPI(logId, mthdNm, "GE0002", dnLoc, "*");
					if("N".equals(sApplyYnPI)){
	
						szMsg	= "차량 위치 [" + ydDnWrLoc + "] 자동상차완료 OFF";
						commUtils.printLog(logId, szMsg, "SL");
	
						return carLdCmplYn;
						
					}
				}
			}
			
			szMsg	= "****** 상차 완료 기준 조회  야드차량스케쥴ID [" + ydCarSchId + "]";
			commUtils.printLog(logId, szMsg, "SL");
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			jrParam.setResultCode(logId);								// Log ID
			jrParam.setResultMsg(mthdNm);								// Log Method Name
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId );				// 야드차량스케쥴ID

			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCarLdCmpl", logId, mthdNm, "상차 완료 기준 조회");
			
			if (jsChk == null || jsChk.size() == 0) {
				szMsg	= "****** 야드차량스케쥴ID [" + ydCarSchId + "] 상차 완료 기준 재료 없음";
				commUtils.printLog(logId, szMsg, "SL");
				
				return carLdCmplYn;
			}

			int Cnt = jsChk.size();

			String 	cls		= ""; // 수정자(Backup Only)
			int 	icnt 	= 0;
			int		iMtlL 	= 0;
			float	fMtlT 	= 0.0f;

			szMsg	= "LOOP [" + Cnt + "] 시작";
			commUtils.printLog(logId, szMsg, "SL");
			
			for (int ii = 0; ii < Cnt; ii++) {
				szMsg	= "LOOP [" + ii + "]";
				
				commUtils.printParam("jsChk", jsChk);
				
				commUtils.printLog(logId, szMsg, "SL");

				cls 	= jsChk.getRecord(ii).getFieldString("CLS"); 											// 봉강(RR)/각강,빌렛(QT) 구분
				fMtlT 	= Float.parseFloat(commUtils.nvl(jsChk.getRecord(ii).getFieldString("YD_MTL_T"),"0")); 	// 두께
				iMtlL 	= Integer.parseInt(commUtils.nvl(jsChk.getRecord(ii).getFieldString("YD_MTL_L"),"0")); 	// 길이
				icnt 	+= Integer.parseInt(commUtils.nvl(jsChk.getRecord(ii).getFieldString("CNT"),"0")); 		// 매수
				
				szMsg	= "********** " + dnLoc + " LOOP [" + ii + "] 구분 [" + cls + "] 두께 [" + fMtlT + "] 길이 [" + iMtlL + "] 매수 [" + icnt + "]";
				commUtils.printLog(logId, szMsg, "SL");

			}

			szMsg	= "LOOP [" + Cnt + "] 종료";
			commUtils.printLog(logId, szMsg, "SL");
			

			
			if( "RR".equals(cls)) {
				// 봉강
				if("GFTR11".equals(dnLoc)) {
// 2026.02.24 대형봉강옥외야드 자동 상차 기준 변경 -->
					/*
					대형봉강옥외야드
					-------------------------------------------
					이상    이하    상차완료기준
					R80     R93     12번들
					R95     R180    10번들
					
					- R80 미만  : 수동
					- R84       : 수동
					- R181 이상 : 수동
					*/
					if(80 <= fMtlT && fMtlT <= 93 && icnt >= 12) {
						szMsg	= "대형봉강옥외야드 봉강 R80 ~ R93 " + icnt + "번들 상차 완료";
// 2026.02.24 대형봉강옥외야드 자동 상차 기준 변경 <--
						commUtils.printLog(logId, szMsg, "SL");

						carLdCmplYn = "Y";
					} else if(95 <= fMtlT && fMtlT <= 180 && icnt >= 10) {
						szMsg	= "대형봉강옥외야드 봉강 R95 ~ R180 " + icnt + "번들 상차 완료";
						commUtils.printLog(logId, szMsg, "SL");

						carLdCmplYn = "Y";
					}
				} else if(dnLoc.startsWith("GDTR")) {
// 2026.01.28 소형  야드 12매 상차 완료 					
					if(icnt >= 12) {
						szMsg	= "소형  야드 [" + icnt + "]매 상차 완료 ";
						commUtils.printLog(logId, szMsg, "SL");
						
						carLdCmplYn = "Y";
					}
				} else {
					/*
					대형압연옥내야드, 정정야드
					-------------------------------------------
					이상    이하    작업매수    상차완료기준
					        R86     3번들       8번들
					R87     R260    3번들       8번들
					R270    R270    4번들       16번들
					R271    R330    3번들       12번들
					R331            3번들
					*/
					if(fMtlT <= 86 && icnt >= 8) {
						szMsg	= "봉강 R86 8번들 상차 완료";
						commUtils.printLog(logId, szMsg, "SL");
						
						carLdCmplYn = "Y";
					} else if(87 <= fMtlT && fMtlT <= 269 && icnt >= 8) {
						szMsg	= "봉강 R87 ~ R260 8번들 상차 완료";
						commUtils.printLog(logId, szMsg, "SL");

						carLdCmplYn = "Y";
					} else if(270 == fMtlT && icnt >= 16) {
						szMsg	= "봉강 R270 16번들 상차 완료";
						commUtils.printLog(logId, szMsg, "SL");

						carLdCmplYn = "Y";
					} else if(271 <= fMtlT && fMtlT <= 330 && icnt >= 12) {
						szMsg	= "봉강 R271 ~ R330 12번들 상차 완료";
						commUtils.printLog(logId, szMsg, "SL");

						carLdCmplYn = "Y";
					}
				}
				
				szMsg	= "봉강 종료";
				commUtils.printLog(logId, szMsg, "SL");
				
			} else {
				// 빌렛/각강		
				/*
				대형야드 각강 상차 기준	
				이상		이하		길이			작업매수		상차완료기준	비고
				-----------------------------------------------------------------
				　		S119	-			8매			16매			S120 미만 사이즈 수주시, 1BED에 10매 이상 가능하게 개발 필요
				S120	S120	-			8매			16매	　
				S121	S129	-			8매			16매	　
				S130	S130	8M			8매			16매	　
				S130	S130	11M			6매			12매	　
				S131	S131	10M 미만		8매			16매	　
				S131	S181.5	-			6매			12매	　
				S181.6	S190	　			6매			12매	　
				S191	S200	　			4매			 8매	　
				*/
				if(fMtlT <= 119 && icnt >= 16) {
					szMsg	= "빌렛/각강 S119 16매 상차 완료";
					commUtils.printLog(logId, szMsg, "SL");
					
					carLdCmplYn = "Y";
				} else if(120 == fMtlT && icnt >= 16) {
					szMsg	= "빌렛/각강 S120 16매 상차 완료";
					commUtils.printLog(logId, szMsg, "SL");

					carLdCmplYn = "Y";
				} else if(121 <= fMtlT && fMtlT <= 129 && icnt >= 16) {
					szMsg	= "빌렛/각강 S121 ~ S129 16매 상차 완료";
					commUtils.printLog(logId, szMsg, "SL");

					carLdCmplYn = "Y";
				} else if(130 == fMtlT && iMtlL <= 8000 && icnt >= 16) {
					szMsg	= "빌렛/각강 S130 8M 16매 상차 완료";
					commUtils.printLog(logId, szMsg, "SL");

					carLdCmplYn = "Y";
				} else if(130 == fMtlT && 8000 < iMtlL && icnt >= 12) {
					szMsg	= "빌렛/각강 S130 11M 12매 상차 완료";
					commUtils.printLog(logId, szMsg, "SL");

					carLdCmplYn = "Y";
				} else if(131 == fMtlT && iMtlL < 10000 && icnt >= 16) {
					szMsg	= "빌렛/각강 S131 10M 미만 16매 상차 완료";
					commUtils.printLog(logId, szMsg, "SL");

					carLdCmplYn = "Y";
				} else if(131 <= fMtlT && fMtlT <= 181.5 && icnt >= 12) {
					szMsg	= "빌렛/각강 S131 ~ S181.5 12매 상차 완료";
					commUtils.printLog(logId, szMsg, "SL");

					carLdCmplYn = "Y";
				} else if(181.6 <= fMtlT && fMtlT <= 190 && icnt >= 12) {
					szMsg	= "빌렛/각강 S181.6 ~ S190 12매 상차 완료";
					commUtils.printLog(logId, szMsg, "SL");

					carLdCmplYn = "Y";
				} else if(191 <= fMtlT && icnt >= 8) {
					szMsg	= "빌렛/각강 S191 ~ S200 8매 상차 완료";
					commUtils.printLog(logId, szMsg, "SL");

					carLdCmplYn = "Y";
				}
				
				szMsg	= "빌렛/각강 종료";
				commUtils.printLog(logId, szMsg, "SL");
			}
			
			commUtils.printLog(logId, mthdNm, "S-");

			return carLdCmplYn;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return carLdCmplYn;
		} catch (Exception e) {
			return carLdCmplYn;
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : RULE ITEM RETURN
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String ApplyRuleItem(String logId, String mthdNms, String sReprCdGp, String sCdGp) throws DAOException {

		String mthdNm = "RULE ITEM 체크[YsCommCarTSMvSeEJBBean.ApplyRuleItem] < " + mthdNms;
		String szITEM = "N";

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			//수신 항목 값
			/**********************************************************
			* 2. 열정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("REPR_CD_GP", sReprCdGp  ); //작업구분
			jrParam.setField("CD_GP"     , sCdGp      ); //구분

			//필드명 변환 (필드명 -> V_필드명)
			jrParam = commDao.conversionFieldname(jrParam, 0);			

			//query id setting
			jrParam.setField("JSPEED_QUERY_ID", "com.inisteel.cim.ys.common.dao.YsCommDAO.getRuleItem");

			//query execute
			JDTORecordSet jsChk = commDao.getRecordSet(jrParam);			

			if (jsChk.size() > 0) {
				szITEM    = commUtils.trim(jsChk.getRecord(0).getFieldString("ITEM"));
			}
            
			commUtils.printLog(logId, mthdNm, "S-");

			return szITEM;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return szITEM;
		} catch (Exception e) {
			return szITEM;
		}
	}
	
	/**
	 * L2 -> L3 구내운송 영차 도착
	 * 
	 * @param JDTORecord recCarSch
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procL2MatlCarArr(String logId, JDTORecord rcvMsg)throws JDTOException  {
		String mthdNm 		= "L2 -> L3 구내운송 영차 도착[YsCommCarTSMvSeEJBBean.procL2MatlCarArr] ";
		String szMsg		= "";

	    String methodNm					= "procL2MatlCarArr";
	    
	    String srTRN_EQP_CD				= null;
	    String srSPOS_WLOC_CD			= "";
	    String srSPOS_YD_PNT_CD			= "";
	    String srARR_WLOC_CD			= "";
	    String srARR_YD_PNT_CD			= "";
	    String srTRN_WRK_FULLVOID_GP 	= null;
	    String srTRN_EQP_STK_CAPA 		= null;
	    String srCARUD_PAP_LEV_TT 		= null;
	    String srYD_WO_CNCL_YN 			= null;
	    String srTRN_WRK_MTL_GP			= null;
	    String srWLOC_CD				= "";
	    String srYD_PNT_CD				= "";
	    String srCARLD_SH				= "";
	    
	    
	    String szYD_CAR_SCH_ID			= null;
		String sstlNo 					= "";			// 재료번호
		String modifier        			= "TSYSJ004"; 	// 수정자(Backup Only)
//		String sstlNos 					= "";															// 재료번호

		int intRtnVal					= 0 ;

		JDTORecord	  	sndRecord	= JDTORecordFactory.getInstance().create();
		JDTORecordSet 	rsResult    	= null;
		JDTORecord    	recInTemp   	= null;
		JDTORecord 		recPara 		= null;
	    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
	    	
	    	szMsg	=	"[" + methodNm + "] 메소드 시작";
	    	commUtils.printLog(logId, szMsg, "SL");
	    	
			
	    	srTRN_EQP_CD 			= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"			));		// 운송장비코드
	    	srSPOS_WLOC_CD 			= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"		));		// 발지개소코드
	    	srSPOS_YD_PNT_CD		= commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"		));		// 발지야드포인트코드
	    	srARR_WLOC_CD 			= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD"		));		// 착지개소코드
	    	srARR_YD_PNT_CD			= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD"		));		// 착지야드포인트코드
	    	srTRN_WRK_FULLVOID_GP 	= commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP"));		// 운송작업영공구분
	    	srTRN_EQP_STK_CAPA 		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA"	));		// 운송장비적재능력
	    	srCARUD_PAP_LEV_TT 		= commUtils.trim(rcvMsg.getFieldString("CARUD_PAP_LEV_TT"	));		// 하차지출발시각
	    	srYD_WO_CNCL_YN 		= commUtils.trim(rcvMsg.getFieldString("YD_WO_CNCL_YN"		));		// 야드지시취소여부
	    	srTRN_WRK_MTL_GP 		= commUtils.trim(rcvMsg.getFieldString("TRN_WRK_MTL_GP"		));		// 운송작업재료구분
	    	srWLOC_CD 				= commUtils.trim(rcvMsg.getFieldString("WLOC_CD"			));		// 개소코드
	    	srYD_PNT_CD 			= commUtils.trim(rcvMsg.getFieldString("YD_PNT_CD"			));		// 야드포인트코드
	    	srCARLD_SH 				= commUtils.trim(rcvMsg.getFieldString("CARLD_SH"			));		// 상차매수

	    	
            szMsg = "\n\t TRN_EQP_CD   			: " 	+ srTRN_EQP_CD 
                  + "\n\t SPOS_WLOC_CD       	: " 	+ srSPOS_WLOC_CD 
                  + "\n\t SPOS_YD_PNT_CD  		: " 	+ srSPOS_YD_PNT_CD 
                  + "\n\t ARR_WLOC_CD         	: " 	+ srARR_WLOC_CD 
                  + "\n\t ARR_YD_PNT_CD     	: " 	+ srARR_YD_PNT_CD 
                  + "\n\t TRN_WRK_FULLVOID_GP	: " 	+ srTRN_WRK_FULLVOID_GP
                  + "\n\t TRN_EQP_STK_CAPA		: " 	+ srTRN_EQP_STK_CAPA
                  + "\n\t CARUD_PAP_LEV_TT		: " 	+ srCARUD_PAP_LEV_TT
                  + "\n\t YD_WO_CNCL_YN			: " 	+ srYD_WO_CNCL_YN
                  + "\n\t TRN_WRK_MTL_GP		: " 	+ srTRN_WRK_MTL_GP
                  + "\n\t WLOC_CD     			: " 	+ srWLOC_CD
                  + "\n\t YD_PNT_CD     		: " 	+ srYD_PNT_CD
                  + "\n\t CARLD_SH     			: " 	+ srCARLD_SH
                  ;

      		commUtils.printLog(logId, szMsg, "");
	    	
            if ("".equals(srCARLD_SH)) {
				szMsg	= methodNm + " 상차매수[" + srCARLD_SH +"] 오류";
				commUtils.printLog(logId, szMsg, "SL");

				return sndRecord;
			}
		
			/**********************************************************
	    	 * 운송장비코드로 차량스케줄 조회
	    	 **********************************************************/
// 2026.04.13 체크 추가
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp 	= JDTORecordFactory.getInstance().create();			
			recInTemp.setField("TRN_EQP_CD", srTRN_EQP_CD);
			
			rsResult = commDao.select(recInTemp, getYdCarschDaoTrnEqpCd, logId, methodNm, "차량스케줄을 조회"); 
			if (rsResult == null || rsResult.size() < 0) {
				szMsg	= "[" + methodNm + "] 운송장비코드[" + srTRN_EQP_CD + "]로 차량스케줄 조회 시 : parameter error";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			} else if (rsResult.size() > 0) {
				szMsg	= "[" + methodNm + "] 차량스케줄 조회 시 오류발생 - 운송장비코드로 차량스케줄이 [" + rsResult.size() + "]건 존재합니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}
			
	    	//----------------------------------------------------------------------------------------
	    	//	차량스케줄을 생성
	    	//----------------------------------------------------------------------------------------
			szYD_CAR_SCH_ID = commDao.getSeqId(logId, methodNm, "CarSch" );
			
	    	recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID				);		// 야드차량스케쥴ID
			recPara.setField("YD_EQP_ID",        YsConstant.YD_TS_CAR_EQP_ID	);		// 야드설비ID
			recPara.setField("YD_CAR_USE_GP",    "L"							);		// 차량사용구분(L:구내,G:출하)
			recPara.setField("TRN_EQP_CD",       srTRN_EQP_CD					);		// 운송장비코드
			recPara.setField("YD_EQP_WRK_STAT",  "L"							);		// 야드설비작업상태(U:공차,L:영차)
			recPara.setField("SPOS_WLOC_CD",     srSPOS_WLOC_CD					);		// 발지개소코드
			recPara.setField("ARR_WLOC_CD",      srARR_WLOC_CD					);		// 착지개소코드(공차이면 재료실으러 들어올 위치)
			recPara.setField("YD_CARLD_LEV_LOC", ""							 	);		// 야드상차출발위치
			recPara.setField("YD_CARLD_LEV_DT",  ""					 			);		// 상차출발일시
			recPara.setField("YD_CAR_PROG_STAT", YsConstant.YD_CARUD_LEV		);		// 야드차량진행상태 (A : 하차출발)
			recPara.setField("YD_BAYIN_WO_SEQ",  "9"							);		// 입동지시순번 - 기본값으로 설정(9)
			recPara.setField("REGISTER",         modifier						);		// 등록자

			
			//차량스케줄 등록
			
// 2026.04.02 차량 스케쥴 (TB_YS_CARSCH) 테이블 차랑변호 등록 추가
//			intRtnVal = commDao.insert(recPara, insYsCarsch, logId, methodNm, "차량스케줄 등록");
			intRtnVal = commDao.insert(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.insYsCarSch2", logId, methodNm, "차량스케줄 등록");
			
			if( intRtnVal <= 0 ){
				szMsg	= methodNm + " 개소코드[" + srWLOC_CD +"] : 차량스케줄[" + szYD_CAR_SCH_ID + "] 생성 시 오류발생 - 반환값 : " + intRtnVal;
				commUtils.printLog(logId, szMsg, "SL");

				return sndRecord;
			}
			
			szYD_CAR_SCH_ID = commUtils.trim(recPara.getFieldString("YD_CAR_SCH_ID"));
			
    		szMsg	= methodNm + " 개소코드[" + srARR_WLOC_CD + "] : 차량스케줄[" + szYD_CAR_SCH_ID + "] 생성 완료 - 반환값 : " + intRtnVal;
    		commUtils.printLog(logId, szMsg, "SL");
				
	    	//----------------------------------------------------------------------------------------
	    	//	차량제료 등록
	    	//----------------------------------------------------------------------------------------

    		String szYS_STK_BED_NO 	= "";
    		String szYS_STK_LYR_NO 	= "";
    		String szYS_STK_SEQ_NO 	= "";
    		String szSSTL_LOC		= "";
    		
    		int iSh = Integer.parseInt(srCARLD_SH); // 상차매수
			
			for (int ii = 1; ii <= iSh; ii++) {
				
				// 작업예약 생성시 사용 (도착시 작업예약 생성 하기 때문에 사용 하지 않음 )
//		    	sstlNos += commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii)); 
//				if(ii < iSh) {
//					sstlNos += ",";
//				}
				
				sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호

				if ("".equals(sstlNo)) {
					szMsg	= " 번호 [" + ii + "] 재료번호  없음";
					commUtils.printLog(logId, szMsg, "SL");

					return sndRecord;
				}
				
				szSSTL_LOC = commUtils.trim(rcvMsg.getFieldString("SSTL_LOC" + ii));	// 위치
				
				szMsg	= " 번호 [" + ii + "] 재료번호 [" + sstlNo + "] 위치 [" + szSSTL_LOC + "]";
				commUtils.printLog(logId, szMsg, "SL");
				
				
		    	recPara = JDTORecordFactory.getInstance().create();

// 2025.12.06 L2 -> L3 영차 도착 인경우 TB_YS_STOCK 에서 YS_STK_BED_NO, YS_STK_SEQ_NO 사용하고
//		 		YS_STK_LYR_NO 만 1단으로 재설정(차량은 이송은 모두 1단)		    	
				recPara.setField("YD_CAR_SCH_ID", 	szYD_CAR_SCH_ID		);		// 야드차량스케쥴ID
				recPara.setField("MODIFIER",      	modifier			);		// 수정자
				recPara.setField("SSTL_NO",    		sstlNo				);		// 재료번호

// L2 -> L3 영차 출발 전문에 있는 위치를 등록
				
				szMsg	= "베드 [" + szSSTL_LOC.substring(0, 2) + "] 단 [" + szSSTL_LOC.substring(2, 4) + "] Seq [" + szSSTL_LOC.substring(4, 5) + "] 전문 적치단";
				commUtils.printLog(logId, szMsg, "SL");

				szYS_STK_BED_NO = szSSTL_LOC.substring(0, 2); 
				szYS_STK_LYR_NO = szSSTL_LOC.substring(2, 4); 
				szYS_STK_SEQ_NO = szSSTL_LOC.substring(4, 5); 

				szMsg	= "베드 [" + szYS_STK_BED_NO + "] 단 [" + szYS_STK_LYR_NO + "] Seq [" + szYS_STK_SEQ_NO + "] 설정";
				commUtils.printLog(logId, szMsg, "SL");
				
// YS_STK_LYR_NO 만 1단으로 재설정(차량은 이송은 모두 1단)		    	
//				recPara.setField("YS_STK_LYR_NO",		"01");		// YS_STK_LYR_NO(단)
				
				
//  TB_YS_STOCK 에서 YS_STK_BED_NO, YS_STK_SEQ_NO 사용하지 않을 경우 8 Seq 까지는 01 YS_STK_BED_NO
//	9 Seq 부터는 02 YS_STK_BED_NO			
//				if(ii > 8) {
//					szYS_STK_BED_NO = "02";
//					szYS_STK_LYR_NO = "01";
//					szYS_STK_SEQ_NO = String.valueOf(ii-8);
//				}
//				else {
//					szYS_STK_BED_NO = "01";
//					szYS_STK_LYR_NO = "01";
//					szYS_STK_SEQ_NO = String.valueOf(ii);
//				}

//				V_YS_STK_BED_NO
//				V_YS_STK_LYR_NO
//				V_YS_STK_SEQ_NO

				recPara.setField("YS_STK_BED_NO",		szYS_STK_BED_NO);		// 베드
				recPara.setField("YS_STK_LYR_NO",		szYS_STK_LYR_NO);		// 단
				recPara.setField("YS_STK_SEQ_NO",		szYS_STK_SEQ_NO);		// Seq
				
				
				intRtnVal = commDao.insert(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCarFtMvMtl", logId, methodNm, "이송작업재료등록");
	    		if(intRtnVal <= 0) {
					szMsg	= " 번호 [" + ii + "] 재료번호  이송작업재료등록 오류";
					commUtils.printLog(logId, szMsg, "SL");

					return sndRecord;
	    		}
	    		
	    		
	    		recPara = JDTORecordFactory.getInstance().create();

	    		recPara.setResultCode(logId);							// Log ID
	    		recPara.setResultMsg(methodNm);							// Log Method Name
	    		recPara.setField("SSTL_NO"      		, sstlNo   			); 	// 재료번호
	    		recPara.setField("USER_ID"      		, modifier			); 	// 등록자, 수정자
	    		recPara.setField("SPOS_WLOC_CD"			, srSPOS_WLOC_CD   	); 	// L2 개소 코드	 
	    		recPara.setField("ARR_WLOC_CD"      	, srARR_WLOC_CD		); 	// 착지 개소 코드
	    		recPara.setField("REG_PGM"				, "YardSystem "		); 	// 등록프로그램 

				szMsg = "****** insPbStlFrToMove jrParam : " + recPara.toString();
	      		commUtils.printLog(logId, szMsg, "");
				
				// 이송지시 테이블 INSERT
				commDao.insert(recPara, "com.inisteel.cim.ys.common.dao.insPbStlFrToMove", logId, methodNm, "이송지시 등록");
	    		
			}
				
			recPara.setField("SPOS_WLOC_CD",     srSPOS_WLOC_CD					);		// 발지개소코드
			recPara.setField("ARR_WLOC_CD",      srARR_WLOC_CD					);		// 착지개소코드(공차이면 재료실으러 들어올 위치)
			
			//소재차량도착Point요구 호출
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setResultCode(logId);	//Log ID
			recPara.setResultMsg(methodNm);	//Log Method Name
			
			recPara.setField("JMS_TC_CD",               YsConstant.YSYSJ901);
			recPara.setField("JMS_TC_CREATE_DDTT",      commUtils.getDateTime14()); //JMSTC생성일시
			recPara.setField("TRN_EQP_CD",              srTRN_EQP_CD);
			recPara.setField("WLOC_CD",                 srARR_WLOC_CD);
			recPara.setField("TRN_WRK_FULLVOID_GP",     srTRN_WRK_FULLVOID_GP);
			recPara.setField("PNT_DMD_DT",              commUtils.getDateTime14());
			
			//소재차량도착Point요구 호출
			sndRecord = commUtils.addSndData(sndRecord, recPara);	
			
			commUtils.printLog(logId, mthdNm, "S-");

			return sndRecord;
			

		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return sndRecord;
		} catch (Exception e) {
			return sndRecord;
		}
	}

    
} // end of class YsCommCarTSMvSeEJBBean
