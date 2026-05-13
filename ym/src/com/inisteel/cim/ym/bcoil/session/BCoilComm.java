/**
 * @(#)BCoilComm
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      B열연 COIL 야드 공통 처리 EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bcoil.session;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.bcommon.util.YmConstant;
import com.inisteel.cim.common.exception.EJBServiceException;
/**
 *      [A] 클래스명 : B열연 COIL 야드 공통 처리
 *
*/

public class BCoilComm {
 
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();	
	private YmComm commComm = new YmComm();

	/**
	 *      [A] 오퍼레이션명 : B열연크레인작업실적응답(YMA7L005) 전문 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYMA7L005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업실적응답 조회[BCoilComm.getYMA7L005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//수신 항목 값
			String msgId      = ""; //전문ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID
			String ydL2WrGp   = commUtils.trim(rcvMsg.getFieldString("YD_L2_WR_GP"   )); //야드L2실적구분
			String ydL3HdRsCd = commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD")); //야드L3처리결과코드
			String ydL3Msg    = commUtils.trim(rcvMsg.getFieldString("YD_L3_MSG"     )); //야드L3MESSAGE

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				return null;
			}

			if (ydEqpId.startsWith("3")) {
				msgId = "YMA7L005";
			} else {
				return null;
			}

			/**********************************************************
			* 2. 크레인작업실적응답 전문 생성
			**********************************************************/
			//야드L3Message가 없으면 생성
			if ("".equals(ydL3Msg)) {
				if ("U".equals(ydL2WrGp)) {
					ydL3Msg = "권상실적";
				} else if ("D".equals(ydL2WrGp)) {
					ydL3Msg = "권하실적";
				} else if ("E".equals(ydL2WrGp)) {
					ydL3Msg = "비상조업실적";
				} else if ("R".equals(ydL2WrGp)) {
					ydL3Msg = "고장복구실적";
				} else if ("M".equals(ydL2WrGp)) {
					ydL3Msg = "운전모드전환";
				} else if ("J".equals(ydL2WrGp)) {
					ydL3Msg = "지시요구";
				} else if ("F".equals(ydL2WrGp)) {
					ydL3Msg = "강제권하";
				} else if ("G".equals(ydL2WrGp)) {
					ydL3Msg = "강제권상요구";
				} else {
					ydL3Msg = ydL2WrGp;
				}

				if ("0000".equals(ydL3HdRsCd)) {
					ydL3Msg = ydL3Msg + " 정상 처리";
				} else if ("9999".equals(ydL3HdRsCd)) {
					ydL3Msg = ydL3Msg + " 정보 없음";
				} else {
					ydL3Msg = ydL3Msg + " 오류 <" + logId + ">";
				}
			}

			StringBuffer sbMsg = new StringBuffer();

			sbMsg = sbMsg.append(msgId                                 ); //전문ID
			sbMsg = sbMsg.append(commUtils.getDateTime18()             ); //생성일,생성시간(yyyy-MM-ddHH:mm:ss)
			sbMsg = sbMsg.append("I"                                   ); //전문구분
			sbMsg = sbMsg.append("0078"                                ); //전문길이
			sbMsg = sbMsg.append(commUtils.getRPad(" "       , 29, " ")); //임시
			sbMsg = sbMsg.append(commUtils.getRPad(ydEqpId   ,  6, " ")); //야드설비ID
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")),  1, " ")); //야드작업진행상태
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )),  8, " ")); //야드스케쥴코드
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )), 18, " ")); //야드크레인스케쥴ID
			sbMsg = sbMsg.append(commUtils.getRPad(ydL2WrGp  ,  1, " ")); //야드L2실적구분
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3HdRsCd,  4, " ")); //야드L3처리결과코드
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3Msg   , 40, " ")); //야드L3Message

			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();

			sndL2Msg.setResultCode(logId);	//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
			sndL2Msg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndL2Msg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndL2Msg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

			//전송 Data Return
			return commUtils.addSndData(sndL2Msg);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}
	
	
	/**
	 * 오퍼레이션명 : 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 */  
    public JDTORecord setInnerIFCoilInfo_01(JDTORecord rcvMsg) throws DAOException {
    	String methodNm = "압연실적 처리[BCoilComm.setInnerIFCoilInfo_01] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode(); 
    	JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    	try{
    		commUtils.printLog(logId, methodNm, "S+");
    		
    		String sStockMoveTerm = "";
    		String coilNo    	= commUtils.trim(rcvMsg.getFieldString("COIL_NO")); //코일번호 
			String yardId   	= commUtils.trim(rcvMsg.getFieldString("YARD_ID")); //
			String processId   	= commUtils.trim(rcvMsg.getFieldString("PROCESS_ID")); //
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			//ProcessID
			
			
			JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
			jrParam1.setResultCode(logId);	//Log ID
			jrParam1.setResultMsg(methodNm);	//Log Method Name
			jrParam1.setField("STOCK_ID"   , coilNo);
			jrParam1.setField("COIL_NO"    , coilNo);
			jrParam1.setField("MODIFIER"   , modifier);
			
			commUtils.printLog(logId, "=============압연실적 처리 시작========", "SL");

		    /*************************************
		     *	1.	공통 Coil정보를 가져온다.
		     *************************************/
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo
			SELECT (CASE WHEN YD_GP ='1' THEN 'A' ELSE HR_PLNT_GP END) 		AS PLNT_GP
			     , HR_PLNT_GP                                       -- 공장구분
				 , REPLACE(PASS_PROC1,'6R','6K')        AS PASS_PROC1   
				 , REPLACE(PASS_PROC2,'6R','6K')        AS PASS_PROC2   
				 , REPLACE(PASS_PROC3,'6R','6K')        AS PASS_PROC3   
				 , REPLACE(PASS_PROC4,'6R','6K')        AS PASS_PROC4   
				 , REPLACE(PASS_PROC5,'6R','6K')        AS PASS_PROC5   
			     , ORD_NO 		                                    -- 제작번호
			     , ORD_DTL 		                                    -- 제작행번
			     , COIL_T 			                                -- 코일두께
			     , COIL_W 			                                -- 코일폭
			     , CURR_COIL_LEN	                                -- 코일길이
			     , COIL_INDIA 		                                -- 코일내경
			     , COIL_OUTDIA 	                                    -- 코일외경
			     , DECODE(COIL_WT,0,NET_CAL_WT,COIL_WT) AS COIL_WT  -- 코일중량
			     , NEXT_PROC 		                                -- 차공정
			     , PLAN_PROC1                                       -- 계획공정
			     , BRANCH_CD 		                                -- 분기위치코드
			     , EXTEND_CONVEYOR_BRANCH_CD                        -- 확장분기위치코드
			     , HYSCO_TRANS_GP 	                                -- HYSCO이송수단
			     , COOL_METHOD 	                                    -- 냉각방법
			     , DECODE(CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',CURR_PROG_CD) AS CURR_PROG_CD
			     , RETURN_GP
			FROM  TB_PT_COILCOMM 
			WHERE COIL_NO = :V_COIL_NO
			*/
		    JDTORecordSet jsCoilCom = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo", logId, methodNm, "공통 Coil정보 조회");
		    
		    if(jsCoilCom == null || jsCoilCom.size() == 0){ 
		    	if("91".equals(processId)){	
		    		throw new EJBServiceException("=압연실적=>조업 91 NO HAVE COMMON COIL DATA");
		    	}
			} else {
		    
			    String sProgCd 		= jsCoilCom.getRecord(0).getFieldString("CURR_PROG_CD");
			    String sCoilProc    = "";
		    	String sNextProc 	= jsCoilCom.getRecord(0).getFieldString("NEXT_PROC");
				String sPlanProc 	= jsCoilCom.getRecord(0).getFieldString("PLAN_PROC1");
				
				if("".equals(sNextProc)){
					sCoilProc = sPlanProc;
				} else {
					sCoilProc = sNextProc;
				}
				
				if (YmConstant.CURR_PROG_CD_COIL_1.equals(sProgCd)) {
		    		
					sStockMoveTerm   = YmConstant.NEW_STOCK_MOVE_TERM_1C;
		    	
		    	} else if(YmConstant.CURR_PROG_CD_COIL_3.equals(sProgCd)) {
				
					commUtils.printLog(logId, methodNm+ "▶▶▶["+coilNo+"]===압연실적 처리 종료(생산종료된 정보입니다)========", "SL");
					return jrRtn;			
					
				} else {
		    		if("5K".equals(sCoilProc)){		 //B열연 SPM
						sStockMoveTerm = "A2";		//SPM 추출
						
					}else if("5H".equals(sCoilProc)){//B열연 HFL
						sStockMoveTerm = "A1";		//HFL 추출
						
					}else if("5T".equals(sCoilProc)){//B열연 수냉재
						sStockMoveTerm = "A3";		//수냉재 추출
						
					}else if("5A".equals(sCoilProc)){//B열연 공냉재
						sStockMoveTerm = "A4";		//공냉재 추출
						
					}else if("6K".equals(sCoilProc)){//B열연 SPM2
						sStockMoveTerm = "A6";		// SPM2 추출
						
					}else if("6H".equals(sCoilProc)){//B열연 HFL결속장
						sStockMoveTerm = "A7";		// B열연 HFL결속장 추출
					}
				}
			}
		    
			if ("".equals(sStockMoveTerm)) { 
				sStockMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_A2;
			}
			
			jrParam1.setField("STOCK_ITEM"		, YmConstant.ITEM_CM);
			jrParam1.setField("STOCK_MOVE_TERM" , sStockMoveTerm);
			
	    	/****************************************************
		     *	2.	저장품Table에 정보를 등록,수정한다.
		     *		최초 발생시 등록, 재 실적발생시 수정
		     *****************************************************/				
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStock 
			MERGE INTO TB_YM_STOCK ST USING (
			    SELECT :V_STOCK_ID          AS STOCK_ID                                 --재료번호
			         , :V_MODIFIER          AS MODIFIER         --수정자
			         , SYSDATE              AS MOD_DDTT         --수정일시
			         , 'N'                  AS DEL_YN           --삭제유무
			         , :V_STOCK_ITEM        AS STOCK_ITEM       --저장품 품목
			         , :V_STOCK_MOVE_TERM   AS STOCK_MOVE_TERM  --저장품 이동 조건
			      FROM DUAL
			) DD ON ( ST.STOCK_ID = DD.STOCK_ID)

			WHEN NOT MATCHED THEN
			    INSERT (
			           STOCK_ID             , STOCK_ITEM        , STOCK_MOVE_TERM 
			         , REGISTER             , REG_DDTT          , MODIFIER  
			         , MOD_DDTT             , DEL_YN    
			         )
			    VALUES (
			           :V_STOCK_ID          , DD.STOCK_ITEM     , DD.STOCK_MOVE_TERM 
			         , DD.MODIFIER          , DD.MOD_DDTT       , DD.MODIFIER  
			         , DD.MOD_DDTT          , DD.DEL_YN 
			         )
			WHEN MATCHED THEN 
			    UPDATE SET
			           STOCK_ITEM       = DD.STOCK_ITEM
			         , STOCK_MOVE_TERM  = DD.STOCK_MOVE_TERM 
			         , MODIFIER         = DD.MODIFIER 
			         , MOD_DDTT         = DD.MOD_DDTT          
			*/         
			commDao.update(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStock");
			
		    /****************************************
		     *	5.	압연실적전문을 송신한다.
		     ****************************************/	
		    
		    if(jsCoilCom.size() > 0){

		    	jrParam1.setField("COIL_NO", coilNo);

				jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA7L009", jrParam1));
		    }

		    commUtils.printLog(logId, methodNm, "S-");
		    
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }  
    	return jrRtn;
    }
    
    
    /**
	 * 오퍼레이션명 : 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 */  
    public boolean setInnerIFCoilInfo_02(JDTORecord rcvMsg) throws DAOException {
    	String methodNm = "정정실적 처리[BCoilComm.setInnerIFCoilInfo_02] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode(); //String sGoodsNo, String sGbnWork){
		boolean blRtn = false;
		try{
			commUtils.printLog(logId, methodNm, "S+");
			
			String coilNo    = commUtils.trim(rcvMsg.getFieldString("COIL_NO"     )); //코일번호 
			String workChk   = commUtils.trim(rcvMsg.getFieldString("WORK_CHK"    )); //작업구분 HFL,SPM
			
			JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
			jrParam1.setResultCode(logId);	//Log ID
			jrParam1.setResultMsg(methodNm);	//Log Method Name
			jrParam1.setField("COIL_NO"   , coilNo);
			jrParam1.setField("STOCK_ID"  , coilNo);
			
			commUtils.printLog(logId, "=============정정실적 처리 시작========", "SL");

			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo
			SELECT (CASE WHEN YD_GP ='1' THEN 'A' ELSE HR_PLNT_GP END) 		AS PLNT_GP
			     , HR_PLNT_GP                                       -- 공장구분
				 , REPLACE(PASS_PROC1,'6R','6K')        AS PASS_PROC1   
				 , REPLACE(PASS_PROC2,'6R','6K')        AS PASS_PROC2   
				 , REPLACE(PASS_PROC3,'6R','6K')        AS PASS_PROC3   
				 , REPLACE(PASS_PROC4,'6R','6K')        AS PASS_PROC4   
				 , REPLACE(PASS_PROC5,'6R','6K')        AS PASS_PROC5   
			     , ORD_NO 		                                    -- 제작번호
			     , ORD_DTL 		                                    -- 제작행번
			     , COIL_T 			                                -- 코일두께
			     , COIL_W 			                                -- 코일폭
			     , CURR_COIL_LEN	                                -- 코일길이
			     , COIL_INDIA 		                                -- 코일내경
			     , COIL_OUTDIA 	                                    -- 코일외경
			     , DECODE(COIL_WT,0,NET_CAL_WT,COIL_WT) AS COIL_WT  -- 코일중량
			     , NEXT_PROC 		                                -- 차공정
			     , PLAN_PROC1                                       -- 계획공정
			     , BRANCH_CD 		                                -- 분기위치코드
			     , EXTEND_CONVEYOR_BRANCH_CD                        -- 확장분기위치코드
			     , HYSCO_TRANS_GP 	                                -- HYSCO이송수단
			     , COOL_METHOD 	                                    -- 냉각방법
			     , DECODE(CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',CURR_PROG_CD) AS CURR_PROG_CD
			     , RETURN_GP
			FROM  TB_PT_COILCOMM 
			WHERE COIL_NO = :V_COIL_NO
			*/
		    JDTORecordSet jsCoilCom = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo", logId, methodNm, "공통 Coil정보 조회");
		    
		    if(jsCoilCom == null || jsCoilCom.size() == 0){ 
		    	return 	blRtn;
			}
		    String ydPlntGp   = commUtils.trim(jsCoilCom.getRecord(0).getFieldString("PLNT_GP")); //공장구분
			
			String sTotalPassProc = "";
			String sFinalPassProc = "";
			
    		/**
    		2. 통과공정의 정보를 비교한다.
    		*/
    		// COILCOMM TBL의 통과공정 1~5 중 가장 마지막에 입력된 통과공정을 비교하여야 한다. 최규성. 
    		for(int i=0; i < 5; i++)
    		{	 
    			sTotalPassProc = commUtils.nvl(jsCoilCom.getRecord(0).getFieldString("PLAN_PROC"+String.valueOf(i+1)),"-");
    			if (sTotalPassProc.equals("-") || sTotalPassProc.equals("")) {
    				break;
    			}else{
    				sFinalPassProc = sTotalPassProc;
    			}
    		}

      		if(sFinalPassProc == "6K") {
    			workChk = "SPM2"; 	
    		}

      		/**
		     *	2.	코일공통 진도코드 Table 참조.
		     */
    		
    		JDTORecord jrRtnProg = commComm.getCoilCurrProgCd(jrParam1);
    		String sStockItem = "";
    		if(YmConstant.NEW_STOCK_MOVE_TERM_HG.equals(commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")))){
	    		sStockItem	   = YmConstant.ITEM_CG;
	    	}else{
	    		sStockItem	   = YmConstant.ITEM_CM;
	    	}
			
    		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STOCK_ID"   		, coilNo);
			jrParam.setField("STOCK_ITEM"   	, sStockItem);
			jrParam.setField("STOCK_MOVE_TERM"  , commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));
    		/* 
    		UPDATE TB_YM_STOCK
    		   SET MODIFIER   = 'SYSTEM'
    		     , MOD_DDTT   = SYSDATE           
    		     , STOCK_ITEM = (CASE WHEN KEEPSTOCK_STL_YN = 'Y' THEN STOCK_ITEM ELSE :V_STOCK_ITEM END)
    		     , STOCK_MOVE_TERM  = :V_STOCK_MOVE_TERM
    		WHERE STOCK_ID = :V_STOCK_ID
    		*/
    		commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStockTransInfo_06", logId, methodNm, "TB_YM_STOCK 수정");
    		
    		/*********************** 
    		 *  LAYOUT 생성 안해도 됨
    		 *  추출시 없을 경우 생성 처리 됨
    		 ************************************/

			commUtils.printLog(logId, "=============정정실적 처리 종료========", "SL");
	
			commUtils.printLog(logId, methodNm, "S+");		            
			blRtn = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return blRtn;
	}
    
    
    /**
	 * 오퍼레이션명 : 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 */  
    public boolean setInnerIFCoilInfo_03(JDTORecord rcvMsg) throws DAOException {
    	boolean blRtn = false;
    	String methodNm = "보류재 처리[BCoilComm.setInnerIFCoilInfo_03] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
    	
    	try{
    		String coilNo   = commUtils.trim(rcvMsg.getFieldString("COIL_NO")); //코일번호 
			String yardId   = commUtils.trim(rcvMsg.getFieldString("YARD_ID")); //야드구분
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
			
			JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
			jrParam1.setResultCode(logId);	//Log ID
			jrParam1.setResultMsg(methodNm);	//Log Method Name
			jrParam1.setField("COIL_NO"    , coilNo);
			jrParam1.setField("STOCK_ID"   , coilNo);
			jrParam1.setField("MODIFIER"   , modifier);
			
			commUtils.printLog(logId, "=============보류재실적 처리 시작========", "SL");

			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo
			SELECT (CASE WHEN YD_GP ='1' THEN 'A' ELSE HR_PLNT_GP END) 		AS PLNT_GP
			     , HR_PLNT_GP                                       -- 공장구분
				 , REPLACE(PASS_PROC1,'6R','6K')        AS PASS_PROC1   
				 , REPLACE(PASS_PROC2,'6R','6K')        AS PASS_PROC2   
				 , REPLACE(PASS_PROC3,'6R','6K')        AS PASS_PROC3   
				 , REPLACE(PASS_PROC4,'6R','6K')        AS PASS_PROC4   
				 , REPLACE(PASS_PROC5,'6R','6K')        AS PASS_PROC5   
			     , ORD_NO 		                                    -- 제작번호
			     , ORD_DTL 		                                    -- 제작행번
			     , COIL_T 			                                -- 코일두께
			     , COIL_W 			                                -- 코일폭
			     , CURR_COIL_LEN	                                -- 코일길이
			     , COIL_INDIA 		                                -- 코일내경
			     , COIL_OUTDIA 	                                    -- 코일외경
			     , DECODE(COIL_WT,0,NET_CAL_WT,COIL_WT) AS COIL_WT  -- 코일중량
			     , NEXT_PROC 		                                -- 차공정
			     , PLAN_PROC1                                       -- 계획공정
			     , BRANCH_CD 		                                -- 분기위치코드
			     , EXTEND_CONVEYOR_BRANCH_CD                        -- 확장분기위치코드
			     , HYSCO_TRANS_GP 	                                -- HYSCO이송수단
			     , COOL_METHOD 	                                    -- 냉각방법
			     , DECODE(CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',CURR_PROG_CD) AS CURR_PROG_CD
			     , RETURN_GP
			FROM  TB_PT_COILCOMM 
			WHERE COIL_NO = :V_COIL_NO
			*/
		    JDTORecordSet jsCoilCom = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo", logId, methodNm, "공통 Coil정보 조회");
		    
		    if(jsCoilCom == null || jsCoilCom.size() == 0){ 
		    	return 	blRtn;
			}
		    
		    String sStockMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_HG;
		    
		    jrParam1.setField("STOCK_ITEM", YmConstant.ITEM_CG);
		    jrParam1.setField("STOCK_MOVE_TERM", sStockMoveTerm);
    		
		    /**
		     *	2.	저장품Table에 정보를 등록,수정한다.
		     */
		    /*
		     * UPDATE TB_YM_STOCK
				   SET STOCK_ITEM	   = (CASE WHEN KEEPSTOCK_STL_YN='Y' THEN STOCK_ITEM ELSE :V_STOCK_ITEM END)
				     , STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
				     , MODIFIER   = :V_MODIFIER
				     , MOD_DDTT   = SYSDATE           
				 WHERE STOCK_ID = :V_STOCK_ID
		     */
		    commDao.update(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockTransInfo_06", logId, methodNm, "저장품Table 수정");	
    		
    		blRtn = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }  
    	return blRtn;
    }
    
    
    /**
	 * 오퍼레이션명 : 
	 *  
        * 반납 실적 처리
        *
        * param String	: 저장품ID
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */    
    public boolean setInnerIFCoilInfo_04(JDTORecord rcvMsg){
		
		String methodNm = "반납 실적 처리[BCoilComm.setInnerIFCoilInfo_04] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode(); 
		boolean blRtn = false;

		
		try{
			commUtils.printLog(logId, methodNm, "S+");
			
			String coilNo   = commUtils.trim(rcvMsg.getFieldString("COIL_NO")); //코일번호 
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
			JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
			jrParam1.setResultCode(logId);	//Log ID
			jrParam1.setResultMsg(methodNm);	//Log Method Name
			jrParam1.setField("COIL_NO"    , coilNo);
			jrParam1.setField("STOCK_ID"   , coilNo);
			jrParam1.setField("MODIFIER"   , modifier);
			
			commUtils.printLog(logId, "============반납실적 처리 시작========", "SL");
		    /**
		     *	1.	공통 Coil정보를 가져온다.
		     */
			
			JDTORecordSet jsCoilCom = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo", logId, methodNm, "공통 Coil정보 조회");
	    
		    if(jsCoilCom == null || jsCoilCom.size() == 0){ 
		    	return 	blRtn;
			}
		    
	    	/**  stock_move_term
		     *	2.	저장품Table에 정보를 등록,수정한다.
		     *		최초 발생시 등록, 재 실적발생시 수정
		     */
		    jrParam1.setField("STOCK_MOVE_TERM", YmConstant.NEW_STOCK_MOVE_TERM_JR);
		    
		    /*
		    UPDATE TB_YM_STOCK
		       SET STOCK_MOVE_TERM = DECODE(STOCK_MOVE_TERM,'BD',STOCK_MOVE_TERM, :V_STOCK_MOVE_TERM)
		         , MODIFIER   = :V_MODIFIER
		         , MOD_DDTT   = SYSDATE     
		     WHERE STOCK_ID = :V_STOCK_ID
		     */
		    commDao.update(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockTransInfo");

		    commUtils.printLog(logId, "============반납실적 처리 종료========", "SL");
		    blRtn = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return blRtn;
	} 
    
    
    /**
	 * 오퍼레이션명 : 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 */  
    public JDTORecord setInnerIFCoilInfo_05(JDTORecord rcvMsg) throws DAOException {
    	String methodNm = "모코일종료 처리[BCoilComm.setInnerIFCoilInfo_05] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode(); 
		boolean blRtn = false;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		try{
			commUtils.printLog(logId, methodNm, "S+");
			
			String workChk = "";
			
			String coilNo   = commUtils.trim(rcvMsg.getFieldString("COIL_NO")); //코일번호 
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)

			//TODO 열연코일? HR-PLATE?? 임가공사코드??
// PIDEV			
//			String sApplyYnPI = commDao.ApplyYnPI("", "", "APPPI0", "*", "*");
			
			JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
			jrParam1.setResultCode(logId);	//Log ID
			jrParam1.setResultMsg(methodNm);	//Log Method Name
			jrParam1.setField("COIL_NO"    , coilNo);
			jrParam1.setField("STOCK_ID"   , coilNo);
			jrParam1.setField("MODIFIER"   , modifier);
			
			
			commUtils.printLog(logId, "=============정정실적 처리 시작========", "SL");

			/*
			SELECT (CASE WHEN YD_GP ='1' THEN 'A' ELSE HR_PLNT_GP END) 		AS PLNT_GP
			     , HR_PLNT_GP                                       -- 공장구분
				 , REPLACE(PASS_PROC1,'6R','6K')        AS PASS_PROC1   
				 , REPLACE(PASS_PROC2,'6R','6K')        AS PASS_PROC2   
				 , REPLACE(PASS_PROC3,'6R','6K')        AS PASS_PROC3   
				 , REPLACE(PASS_PROC4,'6R','6K')        AS PASS_PROC4   
				 , REPLACE(PASS_PROC5,'6R','6K')        AS PASS_PROC5   
			     , ORD_NO 		                                    -- 제작번호
			     , ORD_DTL 		                                    -- 제작행번
			     , COIL_T 			                                -- 코일두께
			     , COIL_W 			                                -- 코일폭
			     , CURR_COIL_LEN	                                -- 코일길이
			     , COIL_INDIA 		                                -- 코일내경
			     , COIL_OUTDIA 	                                    -- 코일외경
			     , DECODE(COIL_WT,0,NET_CAL_WT,COIL_WT) AS COIL_WT  -- 코일중량
			     , NEXT_PROC 		                                -- 차공정
			     , PLAN_PROC1                                       -- 계획공정
			     , BRANCH_CD 		                                -- 분기위치코드
			     , EXTEND_CONVEYOR_BRANCH_CD                        -- 확장분기위치코드
			     , HYSCO_TRANS_GP 	                                -- HYSCO이송수단
			     , COOL_METHOD 	                                    -- 냉각방법
			     , DECODE(CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',CURR_PROG_CD) AS CURR_PROG_CD
			     , RETURN_GP
			FROM  TB_PT_COILCOMM 
			WHERE COIL_NO = :V_COIL_NO
			*/
		    JDTORecordSet jsCoilCom = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo", logId, methodNm, "공통 Coil정보 조회");
		    
		    if(jsCoilCom == null || jsCoilCom.size() == 0){ 
		    	return 	jrRtn;
			}
			String sTotalPassProc = "";
			String sFinalPassProc = "";
			
    		/**
    		2. 통과공정의 정보를 비교한다.
    		*/
    		// COILCOMM TBL의 통과공정 1~5 중 가장 마지막에 입력된 통과공정을 비교하여야 한다. checkCoilCommonInfo
			//sFinalPassProc = checkCoilCommonInfo(sGoodsNo);
    		for (int i = 0; i < 5; i++) {	 
    			sTotalPassProc = commUtils.nvl(jsCoilCom.getRecord(0).getFieldString("PASS_PROC"+String.valueOf(i+1)),"-");
    			if (sTotalPassProc.equals("-") || sTotalPassProc.equals("")) {
    				break;
    			}else{
    				sFinalPassProc = sTotalPassProc;
    			}
    		}

      		if(sFinalPassProc == "6K") {
    			workChk = "SPM2"; 	
    		}else{
    			workChk = "SPM";
    		}
      		
      		/**
			 * 2. Coil 입고실적 (출하로 제품입고실적 송신 YMDM001) 공통 진도 Code가 입고대기 H 이면 출하로
			 * 입고실적송신
			 */
      		/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.HRPlatecommlist
      		SELECT A.COIL_NO
      		     , A.RECEIPT_DATE                                   AS RECEIPT_DATE
      		     , TO_CHAR(A.COIL_CREATE_DDTT,'hh24MISS')           AS RECEIPT_TIME  
      		     , SUBSTR(B.STACK_COL_GP,1,1)                       AS YD_GP  
      		     , B.STACK_COL_GP||B.STACK_BED_GP||B.STACK_LAYER_GP AS STORE_LOC  
      		  FROM USRPTA.TB_PT_HRPLATECOMM A
      		     , USRYMA.TB_YM_STACKLAYER B
      		 WHERE A.COIL_NO = B.STOCK_ID(+)
      		   AND A.PARENT_COIL_NO = :V_COIL_NO
      		 ORDER BY A.COIL_NO
			*/		   		
      		JDTORecordSet StockList = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.HRPlatecommlist", logId, methodNm, "공통 HRPlate정보 조회");
//PIDEV
//      		if ("Y".equals(sApplyYnPI)) {
      			
      			if (StockList.size() > 0 ) {
      				/**********************************************
      				   * 임가공 실적등록
      				   **********************************************/
      				//진도코드가 H(입고대기) 상태만 출하로 전송 함 
      				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.HRPlatecommlistNew_PIDEV 
      				SELECT A.COIL_NO
      				     , TO_CHAR(SYSDATE ,'YYYYMMDD')           AS RECEIPT_DATE  
      				     , TO_CHAR(A.COIL_CREATE_DDTT,'hh24MISS') AS RECEIPT_TIME  
      				     , A.YD_GP                                AS YD_GP
      				     , A.YD_STR_LOC AS STORE_LOC  
      				     , CASE WHEN A.YD_GP  = '5' THEN 'DX3B'
      				            WHEN A.YD_GP  = '7' THEN 'DH1G'
      				            WHEN A.YD_GP  = 'N' THEN 'DH1J'
      				            WHEN A.YD_GP  = 'Q' THEN 'DX3A'
      				       END                                    AS YARD_GP
      				     , 'T'                                    AS DIST_GOODS_GP
      				  FROM USRPTA.TB_PT_HRPLATECOMM A
      				 WHERE A.PARENT_COIL_NO = :V_COIL_NO
      				   AND A.CURR_PROG_CD ='H'
      				 ORDER BY A.COIL_NO
      				*/
      				JDTORecordSet jsStockList2 = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.HRPlatecommlistNew_PIDEV", logId, methodNm, "공통 HRPlate정보 조회2");
      				 
      				JDTORecord 	jrStockList2 = JDTORecordFactory.getInstance().create();
      				JDTORecord 	tcRecord1 = JDTORecordFactory.getInstance().create();
      				
      				for(int Loop_i = 1; Loop_i <= jsStockList2.size(); Loop_i++) {
      					jsStockList2.absolute(Loop_i);
      					
      					jrStockList2.setRecord( jsStockList2.getRecord() );
      							
      					tcRecord1 = JDTORecordFactory.getInstance().create();
  						tcRecord1.setField("MQ_TC_CD"    		, "M10YDLMJ1015");
  						tcRecord1.setField("MQ_TC_CREATE_DDTT"  , commUtils.getDateTime14());
  						tcRecord1.setField("DIST_GOODS_GP"      , commUtils.trim(jrStockList2.getFieldString("DIST_GOODS_GP")));
  						tcRecord1.setField("YARD_GP"            , commUtils.trim(jrStockList2.getFieldString("YARD_GP")));
  						tcRecord1.setField("GOODS_NO"			, commUtils.trim(jrStockList2.getFieldString("COIL_NO")));
  						tcRecord1.setField("RECEIPT_DATE"		, commUtils.trim(jrStockList2.getFieldString("RECEIPT_DATE")));
  						tcRecord1.setField("RECEIPT_TIME"		, commUtils.trim(jrStockList2.getFieldString("RECEIPT_TIME")));
  						tcRecord1.setField("YD_GP"				, commUtils.trim(jrStockList2.getFieldString("YD_GP")));
  						tcRecord1.setField("STORE_LOC"			, commUtils.trim(jrStockList2.getFieldString("STORE_LOC")));
      					
      					//내부인터페이스 송신모듈 호출 
      					jrRtn = commUtils.addSndData(jrRtn, tcRecord1);	
      		   		} //for end 
      			blRtn = true;
  				return jrRtn;
      				
      			} 
//      		}
      		
			//압연실적처리
			for (int i = 0; i < StockList.size(); i++) {
				rcvMsg.setField("COIL_NO" , StockList.getRecord(i).getFieldString("COIL_NO"));
				rcvMsg.setField("YARD_ID" , StockList.getRecord(i).getFieldString("YD_GP"));
				
				jrRtn = commUtils.addSndData(jrRtn, setInnerIFCoilInfo_HP(rcvMsg));	
				
		   	} //for end 
			
			//진도코드가 H(입고대기) 상태만 출하로 전송 함 
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.HRPlatecommlist2 
			SELECT A.COIL_NO
			     , TO_CHAR(SYSDATE ,'YYYYMMDD')           AS RECEIPT_DATE  
			     , TO_CHAR(A.COIL_CREATE_DDTT,'hh24MISS') AS RECEIPT_TIME  
			     , A.YD_GP                                AS YD_GP
			     , B.STACK_COL_GP||B.STACK_BED_GP||B.STACK_LAYER_GP AS STORE_LOC  
			  FROM USRPTA.TB_PT_HRPLATECOMM A
			     , USRYMA.TB_YM_STACKLAYER B
			 WHERE A.COIL_NO =B.STOCK_ID(+)
			   AND A.PARENT_COIL_NO = :V_COIL_NO
			   AND A.CURR_PROG_CD ='H'
			 ORDER BY A.COIL_NO
			*/
			JDTORecordSet jsStockList2 = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.HRPlatecommlist2_PIDEV", logId, methodNm, "공통 HRPlate정보 조회2");
			 
			JDTORecord 	jrStockList2 = JDTORecordFactory.getInstance().create();
			JDTORecord 	tcRecord1 = JDTORecordFactory.getInstance().create();
			
			for(int Loop_i = 1; Loop_i <= jsStockList2.size(); Loop_i++) {
				jsStockList2.absolute(Loop_i);
				
				jrStockList2.setRecord( jsStockList2.getRecord() );
						
				tcRecord1 = JDTORecordFactory.getInstance().create();
				
//				if("Y".equals(sApplyYnPI)) {
					tcRecord1.setField("MQ_TC_CD"    		, "M10YDLMJ1015");
					tcRecord1.setField("MQ_TC_CREATE_DDTT"  , commUtils.getDateTime14());
					tcRecord1.setField("DIST_GOODS_GP"      , "H");
					tcRecord1.setField("YARD_GP"            , ""); //임가공사 코드
					tcRecord1.setField("GOODS_NO"			, commUtils.trim(jrStockList2.getFieldString("COIL_NO")));
					tcRecord1.setField("RECEIPT_DATE"		, commUtils.trim(jrStockList2.getFieldString("RECEIPT_DATE")));
					tcRecord1.setField("RECEIPT_TIME"		, commUtils.trim(jrStockList2.getFieldString("RECEIPT_TIME")));
					tcRecord1.setField("YD_GP"				, commUtils.trim(jrStockList2.getFieldString("YD_GP")));
					tcRecord1.setField("STORE_LOC"			, commUtils.trim(jrStockList2.getFieldString("STORE_LOC")));
//				} else {
//					tcRecord1.setField("TC_CODE"			, "YDDMR003");				
//					tcRecord1.setField("TC_CREATE_DDTT"		, commUtils.getDateTime14());
//					tcRecord1.setField("JMS_TC_CD"			, "YDDMR003");				
//					tcRecord1.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
//					tcRecord1.setField("GOODS_NO"			, commUtils.trim(jrStockList2.getFieldString("COIL_NO")));
//					tcRecord1.setField("RECEIPT_DATE"		, commUtils.trim(jrStockList2.getFieldString("RECEIPT_DATE")));
//					tcRecord1.setField("RECEIPT_TIME"		, commUtils.trim(jrStockList2.getFieldString("RECEIPT_TIME")));
//					tcRecord1.setField("YD_GP"				, commUtils.trim(jrStockList2.getFieldString("YD_GP")));
//					tcRecord1.setField("STORE_LOC"			, commUtils.trim(jrStockList2.getFieldString("STORE_LOC")));
//					tcRecord1.setField("PROD_ITEM_CODE"		, "");
//				}
				
				//내부인터페이스 송신모듈 호출 
				jrRtn = commUtils.addSndData(jrRtn, tcRecord1);	
	   		} //for end 
	   		
			/**
		     *	3.	STOCK DATA DELETE
		     */    	
	   		/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockDelYnInfo
	   		UPDATE TB_YM_STOCK
	   		SET	
	   		    DEL_YN     = 'Y',
	   		    MODIFIER   = 'SYSTEM',
	   		    MOD_DDTT   = SYSDATE           
	   		WHERE STOCK_ID = :V_STOCK_ID
	   		*/
	   		commDao.update(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockDelYnInfo");
			/**
		     *	3.	SPM 야드 MAP clear 정보 셋팅
		     */
	   		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStackLayer
	   		UPDATE TB_YM_STACKLAYER
	   		   SET STOCK_ID         = NULL
	   		     , STACK_LAYER_STAT = 'E'
	   		     , MODIFIER = :V_MODIFIER
	   		     , MOD_DDTT = SYSDATE
	   		 WHERE STOCK_ID = :V_STOCK_ID
	   		   AND SUBSTR(STACK_COL_GP,1,1) = '3'
	   		 */  
	   		 commDao.update(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStackLayer");

			/**
		   	 * 모 Coil 종료 처리 종료 발생시에 야드에 존재하는 
		   	 * 코일정보가 있으면 삭제한다.
		   	 */
				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdWrkbookDelChk2 
			 SELECT (SELECT A.YD_CRN_SCH_ID
			           FROM TB_YM_CRNSCH A
			              , TB_YM_CRNWRKMTL B
			          WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
			            AND B.STOCK_ID    = :V_STOCK_ID
			            AND A.DEL_YN      = 'N'
			            AND B.DEL_YN      = 'N'
			            AND A.YD_WRK_PROG_STAT = 'W'
			            AND ROWNUM = 1 ) AS YD_CRN_SCH_ID
			      , (SELECT A1.YD_WBOOK_ID AS 
			           FROM TB_YM_WRKBOOK A1
			              , TB_YM_WRKBOOKMTL B1
			          WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			            AND B1.STOCK_ID = :V_STOCK_ID
			            AND A1.DEL_YN = 'N'
			            AND B1.DEL_YN = 'N'
			            AND ROWNUM = 1 ) AS YD_WBOOK_ID     
			   FROM DUAL
			*/	   
			
			JDTORecordSet jsCrnSch = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdWrkbookDelChk2", logId, methodNm, "크레인스케줄재료 조회");

			String ydCrnSchId = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID")); //크레인 작업지시
			String ydWbookId  = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID"));  //작업예약ID

			commUtils.printLog(logId, "작업취소 [ " + ydCrnSchId + " - " + ydWbookId + " ]", "SL");
			
			jrParam1.setField("YD_WBOOK_ID"  , ydWbookId );
			jrParam1.setField("YD_CRN_SCH_ID", ydCrnSchId);
			
			
			JDTORecord 	jrRst = JDTORecordFactory.getInstance().create();
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			if(!ydCrnSchId.equals("")) {
				/**********************************************************
				* 1.1 크레인스케줄 취소
				**********************************************************/
				jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam1 });
				jrRtn = commUtils.addSndData(jrRtn, jrRst);
			}
			
			if(!ydWbookId.equals("")) {
				/**********************************************************
				* 2.1 작업예약 취소
				**********************************************************/
				jrRst = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam1 });
				jrRtn = commUtils.addSndData(jrRtn, jrRst);
			}	

			/**********************************************************
			* 6. 저장품제원정보 (YMA7L002) 송신
			**********************************************************/
			JDTORecord recInTemp  = JDTORecordFactory.getInstance().create();
			recInTemp.setResultCode(logId);	    //Log ID
			recInTemp.setResultMsg(methodNm);	//Log Method Name
			recInTemp.setField("TC_CD"          , "YMA7L002");
			recInTemp.setField("MSG_GP"         , "D");
			recInTemp.setField("YD_INFO_SYNC_CD", "5");
			recInTemp.setField("STOCK_ID"       , coilNo);
			
			//전송 Data 생성
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L002", recInTemp));
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1 
			-- STACK_COL_GP(적치열:첫번째자리 야드구분, 두번째 자리 동구분), STACK_LAYER_STAT(적치단 상태 L:적치중)
			SELECT STACK_COL_GP
			     , SUBSTR(STACK_COL_GP,1,1) STACK_COL_GP1
			     , SUBSTR(STACK_COL_GP,2,1) STACK_COL_GP2
			     , STACK_BED_GP
			     , STACK_LAYER_STAT
			     , STACK_LAYER_GP
			 FROM TB_YM_STACKLAYER  A
			WHERE STOCK_ID = :V_STOCK_ID
			*/
			
			JDTORecordSet jsStackLayer = commDao.select(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1", logId, methodNm, "TB_YM_STACKLAYER 정보");
			if (jsStackLayer.size() < 1) {
				commUtils.printLog(logId, "StackColGp 이상: "+ coilNo, "SL");
				return jrRtn;				
	    	} 			

			jsStackLayer.first();
			JDTORecord jrStackLayer = jsStackLayer.getRecord();
			String ydBayGp		= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP2"),    "");
			/*
			INSERT INTO TB_YM_WRSLT (
					CRANE_WRSLT_ID, 
					SCH_ID,   
					STOCK_ID,  
					EQUIP_GP,  
					SCH_WORK_KIND,     
	                CRANE_WORK_DDTT,      
	                CRANE_WORK_DUTY,
	                CRANE_WORK_PARTY,
	                CRANE_WORD_DDTT,   
	                CRANE_WRSLT_CD,  
	                SCH_WPREFER,
	                SCH_WDEMAND_DDTT,
				    SCH_WDEMAND_DUTY,
				    SCH_WDEMAND_PARTY,
	                CRANE_WORD_UP_LOC,   
	                CRANE_WORD_PUT_LOC,
	                CRANE_WRSLT_UP_LOC,   
	                CRANE_WRSLT_UP_FUNC,   
	                CRANE_WRSLT_UP_DDTT, 
	                CRANE_WRSLT_PUT_LOC, 
	                CRANE_WRSLT_PUT_FUNC,	
	                CRANE_WRSLT_PUT_DDTT, 
	                REGISTER,    
	                REG_DDTT,    
	                MODIFIER,     
	                MOD_DDTT,    
	                DEL_YN,
	                YD_GP)              
			VALUES (TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||YM_WRSLT_SEQ.NEXTVAL, 
	                :V_SCH_ID,    
	                :V_STOCK_ID,     
	                :V_EQUIP_GP,    
	                :V_SCH_WORK_KIND,   
	                TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),   
	                :V_SCRANE_WORK_DUTY,
	                :V_SCRANE_WORK_PARTY,
	                TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 
	                'N', 
	                '1',
	                TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 
				    :V_SCH_WDEMAND_DUTY,
				    :V_SCH_WDEMAND_PARTY,
	                :V_UP_LOC,  
	                :V_PUT_LOC,
	                :V_UP_LOC, 
	                :V_UP_FUNC, 
	                TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 
	                :V_PUT_LOC, 
	                :V_PUT_FUNC, 
	                TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 
	                :V_REGISTER, 
	                SYSDATE,  
	                :V_MODIFIER, 
	                SYSDATE, 
	                'N',
	                :V_YD_GP)
			 */
			jrParam1.setField("SCH_ID"				, "000000000000000000");
			jrParam1.setField("STOCK_ID"			, coilNo);
			jrParam1.setField("EQUIP_GP"			, "3XCR00");
			jrParam1.setField("SCH_WORK_KIND"		, "");
			jrParam1.setField("SCRANE_WORK_DUTY"	, YmCommUtils.getWorkDuty());
			jrParam1.setField("SCRANE_WORK_PARTY"	, YmCommUtils.getWorkParty());
			jrParam1.setField("SCH_WDEMAND_DUTY"	, YmCommUtils.getWorkDuty());
			jrParam1.setField("SCH_WDEMAND_PARTY"	, YmCommUtils.getWorkParty());
			jrParam1.setField("UP_LOC"				, "모 종료");
			jrParam1.setField("PUT_LOC"				, "모 종료");
			jrParam1.setField("UP_FUNC"				, YmConstant.CRANE_FUNC_N);
			jrParam1.setField("PUT_FUNC"			, YmConstant.CRANE_FUNC_N);
			jrParam1.setField("REGISTER"			, "SYSTEM");
			jrParam1.setField("MODIFIER"			, "SYSTEM");
			jrParam1.setField("YD_GP"				, "3");
			
			if( workChk.equals("SPM2")){
				if(ydBayGp.equals("E") || ydBayGp.equals("D")){
					jrParam1.setField("YD_SCH_CD", "3"+ydBayGp+"KE02UM");
				}
				//3EKE02UM 3DKE02UM - SPM2 보급
			}else{
				if(ydBayGp.equals("B") || ydBayGp.equals("C")){
					jrParam1.setField("YD_SCH_CD", "3"+ydBayGp+"KE01UM");
				}
				//3BKE01UM 3CKE01UM - SPM 보급
			}	
			
			commDao.insert(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insertCraneWrsltSanJuk");
      	
			commUtils.printLog(logId, methodNm, "S+");		            
			blRtn = true;
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return jrRtn;
	}
    
    
    /**
	 * 오퍼레이션명 : 
	 *  
     * 자 Coil 실적을 처리
     *
     * param String	: 저장품ID
     * param String	: 야드구분
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
    public JDTORecord setInnerIFCoilInfo_06(JDTORecord rcvMsg){
    	String methodNm = "자 Coil 실적을 처리[BCoilComm.setInnerIFCoilInfo_06] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode(); //String sGoodsNo, String sGbnWork){
		boolean blRtn = false;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try{
			commUtils.printLog(logId, methodNm, "S+");

			String coilNo   = commUtils.trim(rcvMsg.getFieldString("COIL_NO")); //코일번호 
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
	    	// PIDEV
//	    	String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "*", "*");			
			
			JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
			jrParam1.setResultCode(logId);	//Log ID
			jrParam1.setResultMsg(methodNm);	//Log Method Name
			jrParam1.setField("COIL_NO"   , coilNo);
			jrParam1.setField("STOCK_ID"   , coilNo);
			jrParam1.setField("MODIFIER"   , modifier);
			
			commUtils.printLog(logId, "============Coil 실적 처리 시작========", "SL");
		    /**
		     *	1.	공통 Coil정보를 가져온다.
		     */
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo
			SELECT (CASE WHEN YD_GP ='1' THEN 'A' ELSE HR_PLNT_GP END) 		AS PLNT_GP
			     , HR_PLNT_GP                                       -- 공장구분
				 , REPLACE(PASS_PROC1,'6R','6K')        AS PASS_PROC1   
				 , REPLACE(PASS_PROC2,'6R','6K')        AS PASS_PROC2   
				 , REPLACE(PASS_PROC3,'6R','6K')        AS PASS_PROC3   
				 , REPLACE(PASS_PROC4,'6R','6K')        AS PASS_PROC4   
				 , REPLACE(PASS_PROC5,'6R','6K')        AS PASS_PROC5   
			     , ORD_NO 		                                    -- 제작번호
			     , ORD_DTL 		                                    -- 제작행번
			     , COIL_T 			                                -- 코일두께
			     , COIL_W 			                                -- 코일폭
			     , CURR_COIL_LEN	                                -- 코일길이
			     , COIL_INDIA 		                                -- 코일내경
			     , COIL_OUTDIA 	                                    -- 코일외경
			     , DECODE(COIL_WT,0,NET_CAL_WT,COIL_WT) AS COIL_WT  -- 코일중량
			     , NEXT_PROC 		                                -- 차공정
			     , PLAN_PROC1                                       -- 계획공정
			     , BRANCH_CD 		                                -- 분기위치코드
			     , EXTEND_CONVEYOR_BRANCH_CD                        -- 확장분기위치코드
			     , HYSCO_TRANS_GP 	                                -- HYSCO이송수단
			     , COOL_METHOD 	                                    -- 냉각방법
			     , DECODE(CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',CURR_PROG_CD) AS CURR_PROG_CD
			     , RETURN_GP
			FROM  TB_PT_COILCOMM 
			WHERE COIL_NO = :V_COIL_NO
			*/
			JDTORecordSet jsCoilCom = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo", logId, methodNm, "공통 Coil정보 조회");
	    
		    if(jsCoilCom == null || jsCoilCom.size() == 0){ 
		    	return 	jrRtn;
			}
    	/**
	     *	2.	저장품Table에 정보를 등록,수정한다.
	     *		최초 발생시 등록, 재 실적발생시 수정
	     */
		    JDTORecordSet jsExist = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getByPrimaryKey", logId, methodNm, "저장품Table 조회");
			
	    	if(jsExist == null || jsExist.size() == 0){
				JDTORecord jrRtnProg = commComm.getCoilCurrProgCd(jrParam1);
				
				
	    		String sStockItem = "";
	    		if(YmConstant.NEW_STOCK_MOVE_TERM_HG.equals(commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")))){
		    		sStockItem	   = YmConstant.ITEM_CG;
		    	}else{
		    		sStockItem	   = YmConstant.ITEM_CM;
		    	}
	    		
		    	if("".equals(jrRtnProg.getFieldString("STOCK_MOVE_TERM"))){
		    		return 	jrRtn;
		    	}
				
	    		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("STOCK_ID"   		, coilNo);
				jrParam.setField("STOCK_ITEM"   	, sStockItem);
				jrParam.setField("STOCK_MOVE_TERM"  , commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));
				jrParam.setField("MODIFIER"  		, modifier);
				
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStockTransInfo_PIDEV
				MERGE INTO TB_YM_STOCK ST USING (
				    SELECT :V_STOCK_ID          AS STOCK_ID                                 --재료번호
				         , :V_MODIFIER          AS MODIFIER         --수정자
				         , SYSDATE              AS MOD_DDTT         --수정일시
				         , 'N'                  AS DEL_YN           --삭제유무
				         , :V_STOCK_ITEM        AS STOCK_ITEM       --저장품 품목
				         , :V_STOCK_MOVE_TERM   AS STOCK_MOVE_TERM  --저장품 이동 조건
				         , :V_SHEAR_SUPPLY_SEQ  AS SHEAR_SUPPLY_SEQ --차상위치
				         , :V_TRANS_ORD_DT      AS TRANS_ORD_DATE2   --운송지시
				         , :V_TRANS_ORD_SEQNO   AS TRANS_ORD_SEQNO2 --운송지시순번
				         , :V_CAR_CARD_NO       AS CAR_CARD_NO      --카드번호
				         , :V_CAR_NO2           AS CAR_NO2          --차량번호
				         , :V_CR_FRTOMOVE_GP    AS CR_FRTOMOVE_GP   --냉연이송구분
				         , :V_TRANS_WORD_NO     AS TRANS_WORD_NO
				      FROM DUAL
				) DD ON ( ST.STOCK_ID = DD.STOCK_ID)

				WHEN NOT MATCHED THEN
				    INSERT (
				           STOCK_ID             , STOCK_ITEM        , STOCK_MOVE_TERM 
				         , REGISTER             , REG_DDTT          , MODIFIER  
				         , MOD_DDTT             , DEL_YN            , TRANS_WORD_NO
				         , SHEAR_SUPPLY_SEQ     , TRANS_ORD_DATE2   , TRANS_ORD_SEQNO2
				         , CAR_CARD_NO          , CAR_NO2           , CR_FRTOMOVE_GP  
				         )
				    VALUES (
				           :V_STOCK_ID          , DD.STOCK_ITEM     , DD.STOCK_MOVE_TERM 
				         , DD.MODIFIER          , DD.MOD_DDTT       , DD.MODIFIER  
				         , DD.MOD_DDTT          , DD.DEL_YN         , DD.TRANS_WORD_NO 
				         , DD.SHEAR_SUPPLY_SEQ  , DD.TRANS_ORD_DATE2, DD.TRANS_ORD_SEQNO2
				         , DD.CAR_CARD_NO       , DD.CAR_NO2        , DD.CR_FRTOMOVE_GP  
				         )
				WHEN MATCHED THEN 
				    UPDATE SET
				           STOCK_ITEM       = DD.STOCK_ITEM
				         , STOCK_MOVE_TERM  = DD.STOCK_MOVE_TERM 
				         , MODIFIER         = DD.MODIFIER 
				         , MOD_DDTT         = DD.MOD_DDTT          
				         , SHEAR_SUPPLY_SEQ = DD.SHEAR_SUPPLY_SEQ     
				         , TRANS_WORD_NO    = DD.TRANS_WORD_NO
				         , TRANS_ORD_DATE2  = DD.TRANS_ORD_DATE2     
				         , TRANS_ORD_SEQNO2 = DD.TRANS_ORD_SEQNO2
				         , CAR_CARD_NO      = DD.CAR_CARD_NO         
				         , CAR_NO2          = DD.CAR_NO2       
				         , CR_FRTOMOVE_GP   = DD.CR_FRTOMOVE_GP 
				 */

		    	// PIDEV
//				if("Y".equals(sApplyYnPI)) {
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStockTransInfo_PIDEV", logId, methodNm, "TB_YM_STOCK 생성");
//				} else {
//					commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStockTransInfo", logId, methodNm, "TB_YM_STOCK 생성");
//				}
				/**********************************************************
				* 6. 저장품제원정보 (YMA7L002) 송신
				**********************************************************/
				JDTORecord recInTemp  = JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);	    //Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("TC_CD"          , "YMA7L002");
				recInTemp.setField("MSG_GP"         , "I");
				recInTemp.setField("YD_INFO_SYNC_CD", "R");
				recInTemp.setField("STOCK_ID"       , coilNo);
				
				//전송 Data 생성
				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L002", recInTemp));
			}
	    	else{
				//throw new EJBServiceException("=자 Coil 실적=>EXIST STOCK TABLE COIL DATA");
	    		return 	jrRtn;
			}

		    blRtn = true; 
		    commUtils.printLog(logId, methodNm, "S-");

		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return jrRtn;
	}
    
    
    /**
	 * 오퍼레이션명 : 
	 *  
     * SPM 재작업
     *
     * param String	: 저장품ID
     * param String	: 야드구분
     *
     *	수신이 되면 대상재가 출측에 있다면 
	 *	입측 D5로 보내고 조업으로 보급완료실적을 송신
	 *     대상재가 입측에 있다면 그대로 두고 보급완료실적을 송신
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
    public JDTORecord setInnerIFCoilInfo_07(JDTORecord rcvMsg) {
    	String methodNm = "SPM 재작업[BCoilComm.setInnerIFCoilInfo_07] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode(); //String sGoodsNo, String sGbnWork){
		boolean blRtn = false;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			String coilNo      = commUtils.trim(rcvMsg.getFieldString("COIL_NO")); //코일번호 
			String modifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String sPROCESS_ID = commUtils.trim(rcvMsg.getFieldString("PROCESS_ID")); //J3 : 정보상의 재작업
			
			JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
			jrParam1.setResultCode(logId);	//Log ID
			jrParam1.setResultMsg(methodNm);	//Log Method Name
			jrParam1.setField("COIL_NO"    , coilNo);
			jrParam1.setField("STOCK_ID"   , coilNo);
			jrParam1.setField("MODIFIER"   , modifier);
			
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo
			SELECT (CASE WHEN YD_GP ='1' THEN 'A' ELSE HR_PLNT_GP END) 		AS PLNT_GP
			     , HR_PLNT_GP                                       -- 공장구분
				 , REPLACE(PASS_PROC1,'6R','6K')        AS PASS_PROC1   
				 , REPLACE(PASS_PROC2,'6R','6K')        AS PASS_PROC2   
				 , REPLACE(PASS_PROC3,'6R','6K')        AS PASS_PROC3   
				 , REPLACE(PASS_PROC4,'6R','6K')        AS PASS_PROC4   
				 , REPLACE(PASS_PROC5,'6R','6K')        AS PASS_PROC5   
			     , ORD_NO 		                                    -- 제작번호
			     , ORD_DTL 		                                    -- 제작행번
			     , COIL_T 			                                -- 코일두께
			     , COIL_W 			                                -- 코일폭
			     , CURR_COIL_LEN	                                -- 코일길이
			     , COIL_INDIA 		                                -- 코일내경
			     , COIL_OUTDIA 	                                    -- 코일외경
			     , DECODE(COIL_WT,0,NET_CAL_WT,COIL_WT) AS COIL_WT  -- 코일중량
			     , NEXT_PROC 		                                -- 차공정
			     , PLAN_PROC1                                       -- 계획공정
			     , BRANCH_CD 		                                -- 분기위치코드
			     , EXTEND_CONVEYOR_BRANCH_CD                        -- 확장분기위치코드
			     , HYSCO_TRANS_GP 	                                -- HYSCO이송수단
			     , COOL_METHOD 	                                    -- 냉각방법
			     , DECODE(CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',CURR_PROG_CD) AS CURR_PROG_CD
			     , RETURN_GP
			FROM  TB_PT_COILCOMM 
			WHERE COIL_NO = :V_COIL_NO
			*/
			JDTORecordSet jsCoilCom = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo", logId, methodNm, "공통 Coil정보 조회");
		    
		    if(jsCoilCom == null || jsCoilCom.size() == 0){ 
		    	return 	jrRtn;
		    	//throw new EJBServiceException("공통 Coil정보 NULL");
			}

		    commUtils.printLog(logId, "============SPM 재작업 처리 시작========", "SL");
		    
		    /**
		     *	1.	B열연 SPM 입,출측 셋팅
		     */
			String sIStackColGp   = "";
			String sOStackColGp1  = "";
			String sOStackColGp2  = "";
			String sFinalPassProc = "";
			String sTotalPassProc = "";
			String sPlntGp = "";
			int inq;
			
		    sPlntGp = jsCoilCom.getRecord(0).getFieldString("HR_PLNT_GP");
		    
		    //===============================================================================
		    // SPM2 관련 검사 코드 추가. .
//			sFinalPassProc = checkCoilCommonInfo(sGoodsNo);
		   //===============================================================================
		    // COILCOMM TBL의 통과공정 1~5 중 가장 마지막에 입력된 통과공정을 비교하여야 한다. . 
    		for (int i = 0; i < 5; i++) {	 
    			sTotalPassProc = commUtils.nvl(jsCoilCom.getRecord(0).getFieldString("PASS_PROC"+String.valueOf(i+1)),"-");
    			if (sTotalPassProc.equals("-") || sTotalPassProc.equals("")) {
    				break;
    			} else {
    				sFinalPassProc = sTotalPassProc;
    			}
    		}
    		
    		if (sFinalPassProc.equals("5K")) {  //SPM1 수작업처리
				sIStackColGp   = YmConstant.SPM_COL_3BKE+YmConstant.STACK_BED_GP_01;
				sOStackColGp1  = YmConstant.SPM_COL_3BKD+YmConstant.STACK_BED_GP_01;
				sOStackColGp2  = YmConstant.SPM_COL_3AKD+YmConstant.STACK_BED_GP_01;
				
    		} else if( sFinalPassProc.equals("6K")) {// SPM2 스케쥴 생성
    			
				commUtils.printLog(logId, "============SPM2 위치 선정========", "SL");
				sIStackColGp   = YmConstant.SPM_COL_3DKE+YmConstant.STACK_BED_GP_01;
				sOStackColGp1  = YmConstant.SPM_COL_3EKD+YmConstant.STACK_BED_GP_01;
				sOStackColGp2  = YmConstant.SPM_COL_3EKD+YmConstant.STACK_BED_GP_01;
			
			} else {
				commUtils.printLog(logId, "SPM재작업. SPM 통과공정 이상=> 잘못된 통과공정", "[INFO]");
				return 	jrRtn;
			}
		
    		if (sFinalPassProc.equals("5K")) { 
				/**
				 *	2.	출측에 저장품이 존재하는지 체크
				 */
				jrParam1.setField("STACK_COL_GP", sOStackColGp1);
				/*com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getStackLayerInfoWithStockId
				SELECT *
				FROM TB_YM_STACKLAYER
				WHERE STACK_COL_GP  = :V_STACK_COL_GP
				AND   STOCK_ID	     = :V_STOCK_ID 
				*/
				JDTORecordSet jsOutLayer = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getStackLayerInfoWithStockId");
				if(jsOutLayer.size() == 0){
					
					jrParam1.setField("STACK_COL_GP", sOStackColGp2);
					jsOutLayer = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getStackLayerInfoWithStockId");
					
					//출측에 존재를 안하는 경우
					if(jsOutLayer.size() == 0 ){						
						//정정실적 처리
			    		rcvMsg.setField("WORK_CHK"   , "SPM");
			    		blRtn = setInnerIFCoilInfo_02(rcvMsg); 
			    		return 	jrRtn;
					}
				} 
				
				/**
				 *	3.	출측에 저장품이 있으면
				 *		출측정보 삭제 후 보급완료 실적 송신
				 */ 
				if (jsOutLayer.size() > 0) {
				
					/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStackLayer
					UPDATE TB_YM_STACKLAYER
					   SET STOCK_ID         = NULL
					     , STACK_LAYER_ACTIVE_STAT = 'E'
					     , STACK_LAYER_STAT = 'E'
					     , MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					 WHERE STOCK_ID = :V_STOCK_ID
					   AND SUBSTR(STACK_COL_GP,1,1) = '3'
					*/
					commDao.update(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStackLayer", logId, methodNm, "TB_YM_STACKLAYER 삭제");
							  				 				  			 			  			 
					/**
					 *	4.	보급완료 실적을 송신
					 */  
						
					//-----------------------
					//코일보급 및 보급취소(YMPOJ161)
					JDTORecord tcRecord2 = JDTORecordFactory.getInstance().create();
	
					tcRecord2.setField("JMS_TC_CD"         	, "YMPOJ161");
					tcRecord2.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
					
					tcRecord2.setField("tcCode"          	, "YMPOJ161");// TC Code
					tcRecord2.setField("tcDate"          	, commUtils.getDate10());// 발생일자
					tcRecord2.setField("tcTime"          	, commUtils.getTime8());// 발생시각
					tcRecord2.setField("plantGbn"           , "B");// 공장구분
					tcRecord2.setField("procGbn"          	, "S");// 공정구분
					tcRecord2.setField("coilNo"          	, coilNo);// COIL_NO
					tcRecord2.setField("processId"          , "5");// 처리구분
					tcRecord2.setField("downDate"          	, commUtils.getDate8());// 권하일자 
					tcRecord2.setField("downTime"          	, commUtils.getTime6());// 권하시각
					tcRecord2.setField("positionNo"         , YmConstant.PO_POSITION_D5);// 위치
				
				    //내부인터페이스 송신모듈 호출 
					jrRtn = commUtils.addSndData(jrRtn, tcRecord2);	
			
				    commUtils.printLog(logId, "내부IF호출=YMPOJ161 코일보급 및 보급취소BACKUP처리", "[INFO]");
					
				  //품질L3열연정정입측보급실적
				    JDTORecord tcParam = JDTORecordFactory.getInstance().create();
					tcParam.setField("JMS_TC_CD"         , "YDQMJ002");
					tcParam.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
					tcParam.setField("STL_NO"            , coilNo.trim());
					jrRtn = commUtils.addSndData(jrRtn, tcParam);		    
				}
    		} else if ( sFinalPassProc.equals("6K")) { //spm2 재처리
    			
    			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getStackLayerInfoWithStockIdRe 
    			SELECT '3EKD02'     AS STACK_COL_GP 
    			     , '01'         AS STACK_BED_GP 
    			     , '01'         AS STACK_LAYER_GP 
    			     , '3EKE010701' AS YD_TO_LOC_GUIDE
    			  FROM DUAL
    			*/  
				JDTORecordSet jsParaLayer = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getStackLayerInfoWithStockIdRe", logId, methodNm, "");
    			
				///**************************************
				// * 입측에 저장품 존재하면 야드맵 정리 20180314
				// **************************************/
				///*
				//SELECT *
				//  FROM TB_YM_STACKLAYER
				// WHERE STACK_COL_GP  = :V_STACK_COL_GP
				//   AND STOCK_ID      = :V_STOCK_ID 
				//*/
				//jrParam1.setField("STACK_COL_GP", commUtils.trim(jsParaLayer.getRecord(0).getFieldString("YD_TO_LOC_GUIDE")).substring(0, 6)); //"3EKD02");  // 재작업 존
				//JDTORecordSet jsInLayer = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getStackLayerInfoWithStockId", logId, methodNm, "입측 저장품 존재 체크");
				//if (jsInLayer.size() > 0) {
				//	/*
				//	UPDATE TB_YM_STACKLAYER
				//	   SET MODIFIER         = :V_MODIFIER
				//	     , MOD_DDTT         = SYSDATE
				//	     , STOCK_ID			= ''
				//	     , STACK_LAYER_STAT	= 'E'
				//	WHERE STOCK_ID   = :V_STOCK_ID
				//	 */
				//	commDao.update(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackLayerByStockId", logId, methodNm, "입측 야드맵 수정");
				//}
				
				/**************************************
				 * 입측에 저장품 존재하면 야드맵 정리 20190909
				 **************************************/
				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStackLayerBySectGp
				UPDATE TB_YM_STACKLAYER
				   SET STOCK_ID         = NULL
				     , STACK_LAYER_ACTIVE_STAT = 'E'
				     , STACK_LAYER_STAT = 'E'
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				 WHERE STOCK_ID = :V_STOCK_ID
				   AND SUBSTR(STACK_COL_GP,1,1) = '3'
				   AND SUBSTR(STACK_COL_GP,3,2) LIKE :V_SECT_GP
		   		 */  
				jrParam1.setField("SECT_GP", "KE"); //SPM2 설비 입측
		   		commDao.update(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStackLayerBySectGp", logId, methodNm, "기존 위치 삭제-SPM2 설비 입측");
				
				/***************************************
				 *	2.	출측에 저장품이 존재하는지 체크
				 ***************************************/
    			jrParam1.setField("STACK_COL_GP", commUtils.trim(jsParaLayer.getRecord(0).getFieldString("STACK_COL_GP"))); //"3EKD02");  // 재작업 존 
				/*
				SELECT *
				  FROM TB_YM_STACKLAYER
				 WHERE STACK_COL_GP  = :V_STACK_COL_GP
				   AND STOCK_ID      = :V_STOCK_ID 
				*/
				JDTORecordSet jsOutLayer = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getStackLayerInfoWithStockId", logId, methodNm, "출측 저장품 존재 체크");
				
				if (jsOutLayer.size() == 0) {
					/*  
					MERGE INTO TB_YM_STACKLAYER SL USING (
					SELECT :V_STACK_COL_GP                  AS   STACK_COL_GP
					     , '3'                              AS   STACK_COL_GP1
					     , SUBSTR(:V_STACK_COL_GP,2,1)      AS   STACK_COL_GP2
					     , '01'          AS   STACK_BED_GP
					     , '01'          AS   STACK_LAYER_GP
					     , 'E'           AS   STACK_LAYER_ACTIVE_STAT 
					     , 'C'           AS   STACK_LAYER_STAT
					     , :V_STOCK_ID   AS   STOCK_ID
					  FROM DUAL 
					) DD ON (SL.STACK_COL_GP = DD.STACK_COL_GP AND SL.STACK_BED_GP   = DD.STACK_BED_GP
					                                           AND SL.STACK_LAYER_GP = DD.STACK_LAYER_GP)
					WHEN MATCHED THEN UPDATE SET
					     SL.MODIFIER                = :V_MODIFIER
					    ,SL.MOD_DDTT                = SYSDATE
					    ,SL.STACK_LAYER_ACTIVE_STAT = DD.STACK_LAYER_ACTIVE_STAT
					    ,SL.STACK_LAYER_STAT        = DD.STACK_LAYER_STAT
					    ,SL.STOCK_ID                = DD.STOCK_ID
					 */	        
					int updCnt = commDao.update(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTrkStackColGplayerRe", logId, methodNm, "TRACKING LAYER 등록");
					
					if (updCnt == 0) {
						commUtils.printLog(logId, "적치단(TB_YM_STACKLAYER) Table Read Error : "+ coilNo, "SL");
						//정정실적 처리
			    		rcvMsg.setField("WORK_CHK"   , "SPM");
			    		blRtn = setInnerIFCoilInfo_02(rcvMsg); 
			    		return 	jrRtn;
					}					
				} 

				/**********************************************************
				 * J3인 경우 스케줄 없이 정보상으로만 재작업처리되므로 종료
				 **********************************************************/
				if ("J3".equals(sPROCESS_ID)) {
					return jrRtn;
				}
				
				/*************************************** 
				 * 	1.작업예약이 생성 
                 *  2.STOCK UPDATE(작업행선)  
                 *  3.스케쥴 호출
				 **************************************/	
				//작업예약,작업재료 등록		
				String ydSchCd = "3EKE05UM"; //SPM2 재작업
    			JDTORecord jrOutTemp = JDTORecordFactory.getInstance().create();
    			jrOutTemp.setField("STL_NO"           , coilNo); //재료번호
    			jrOutTemp.setField("STACK_COL_GP"     , commUtils.trim(jrParam1.getFieldString("STACK_COL_GP"))); 
    			jrOutTemp.setField("STACK_BED_GP"     , commUtils.trim(jsParaLayer.getRecord(0).getFieldString("STACK_BED_GP"))  ); 
    			jrOutTemp.setField("STACK_LAYER_GP"   , "01" ); 
    			jrOutTemp.setField("YD_SCH_CD"        , ydSchCd ); //SPM2 재처리 
    			jrOutTemp.setField("MODIFIER"         , modifier   ); //수정자
    			jrOutTemp.setField("YD_TO_LOC_GUIDE"  , commUtils.trim(jsParaLayer.getRecord(0).getFieldString("YD_TO_LOC_GUIDE")));//3EKE010701" ); //TO위치가이드
    			
    			String ydWbookId = commComm.procWkBookInsert(jrOutTemp);
    			
    			if (ydWbookId.equals(YmConstant.RETN_CD_FAILURE)) {
    				throw new Exception("작업예약ID 생성 실패"); 				
    			}
				
				/**********************************************************
				* 2.2 크레인스케줄 전문 호출
				**********************************************************/
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name

				jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
				jrYdMsg.setField("YD_SCH_ST_GP" , "O"); //야드스케쥴기동구분
				jrYdMsg.setField("YD_SCH_REQ_GP", "L"      ); //야드스케쥴요청구분(인출)
				jrYdMsg.setField("MODIFIER"     , modifier ); //수정자

				jrRtn = commUtils.addSndData(commComm.getCrnSchMsg(jrYdMsg));				
				
    		} // else if ( sFinalPassProc.equals("6K"))
    		
    		commUtils.printLog(logId, methodNm, "S-");
				            
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return jrRtn;
	} 
    
    
    /**
	 * 오퍼레이션명 : 
	 *  
        * 요구차 공정 변경
        *
        * param String	: 저장품ID
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        
    public boolean setInnerIFCoilInfo_11(JDTORecord rcvMsg){
    	String methodNm = "요구차 공정 변경[BCoilComm.setInnerIFCoilInfo_11] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode(); //String sGoodsNo, String sGbnWork){
		boolean blRtn = false;
		boolean isVal = false;
		try{
			commUtils.printLog(logId, methodNm, "S+");
			
			String coilNo    	= commUtils.trim(rcvMsg.getFieldString("COIL_NO")); //코일번호 
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String processCode 	= commUtils.trim(rcvMsg.getFieldString("PROCESS_CODE"));
			
			JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
			jrParam1.setResultCode(logId);	//Log ID
			jrParam1.setResultMsg(methodNm);	//Log Method Name
			jrParam1.setField("COIL_NO"   , coilNo);
			jrParam1.setField("STOCK_ID"   , coilNo);
			jrParam1.setField("MODIFIER"   , modifier);
			
		    /**
		     *	1.	저장품의 MAP정보를 가져온다.
		     */
			/*  
			SELECT STL_NO   AS STOCK_ID
			  FROM USRYMA.TB_YM_EQPTRACKING
			 WHERE PROC_GP IN ('D','X')
			   AND STL_NO = :V_STOCK_ID
			UNION ALL
			SELECT STOCK_ID AS STOCK_ID
			  FROM USRYMA.TB_YM_STACKLAYER
			 WHERE SUBSTR(STACK_COL_GP,3,2) IN ('WB','ST') -- DC, EC
			   AND STACK_LAYER_STAT IN ('C','U')
			   AND STOCK_ID = :V_STOCK_ID
			 */
		    JDTORecordSet jsMap = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getEqpTracking", logId, methodNm, "MAP/TRACKING 정보 조회");
		    
			if(jsMap.size() == 0 || jsMap == null){ 
				return 	blRtn;
		    } else {
		    	
		    	String sStockMoveTerm 	= "";  		
	    		/**
			     *	2.	저장품Table에 정보를 등록,수정한다.
			     *		최초 발생시 등록, 재 실적발생시 수정
			     */
			    if(YmConstant.SHEAR_SUPPLY_GP_5K.equals(processCode)){		 //B열연 SPM
								
			    	sStockMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_A2;		//SPM 추출
				} else if(YmConstant.SHEAR_SUPPLY_GP_5H.equals(processCode)){//B열연 HFL
					
					sStockMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_A1;		//HFL 추출
				} else if(YmConstant.SHEAR_SUPPLY_GP_5T.equals(processCode)){//B열연 수냉재
					
					sStockMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_A3;		//수냉재 추출
				} else if(YmConstant.SHEAR_SUPPLY_GP_5A.equals(processCode)){//B열연 공냉재
					
					sStockMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_A4;		//공냉재 추출
				} else if(YmConstant.SHEAR_SUPPLY_GP_6K.equals(processCode)){// B열연 SPM2  
					sStockMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_A6;		// SPM2 추출
					
				} else if(YmConstant.SHEAR_SUPPLY_GP_6H.equals(processCode)){// B열연 HFL결속장 
					sStockMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_A7;		// HFL결속장  추출
				}
			    jrParam1.setField("STOCK_MOVE_TERM", sStockMoveTerm);
		     	/*
		     	UPDATE TB_YM_STOCK
		     	   SET STOCK_MOVE_TERM = DECODE(STOCK_MOVE_TERM,'BD',STOCK_MOVE_TERM, :V_STOCK_MOVE_TERM)
		     	     , MODIFIER   = :V_MODIFIER
		     	     , MOD_DDTT   = SYSDATE     
		     	 WHERE STOCK_ID = :V_STOCK_ID
		     	*/ 
		     	
				commDao.update(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockTransInfo");
				 
		    }
		    

//			logger.println(LogLevel.DEBUG,this,"=============요구공정 변경 처리 종료========");				            
			isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return isVal;
	}
    
    /**
	 * 오퍼레이션명 : 
	 *  
        * 압연실적을 처리
        *
        * param String	: 저장품ID
        * param String	: 야드구분
        * param String	: 처리구분
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public JDTORecord setInnerIFCoilInfo_HP(JDTORecord rcvMsg){
		
		String methodNm = "압연실적 처리[BCoilComm.setInnerIFCoilInfo_HP] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode(); 
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		boolean blRtn = false;

		try{
			commUtils.printLog(logId, methodNm, "S+");
			
			String coilNo    = commUtils.trim(rcvMsg.getFieldString("COIL_NO")); //코일번호 
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String processId = commUtils.trim(rcvMsg.getFieldString("PROCESS_ID"));
			String yardId    = commUtils.trim(rcvMsg.getFieldString("YARD_ID"));
			
			JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
			jrParam1.setResultCode(logId);	//Log ID
			jrParam1.setResultMsg(methodNm);	//Log Method Name
			jrParam1.setField("COIL_NO"   , coilNo);
			jrParam1.setField("STOCK_ID"  , coilNo);
			jrParam1.setField("MODIFIER"  , modifier);
			
			String sStockMoveTerm = "";

			/**
			*	1.	공통 Coil정보를 가져온다.
			*/
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo
			SELECT (CASE WHEN YD_GP ='1' THEN 'A' ELSE HR_PLNT_GP END) 		AS PLNT_GP
			     , HR_PLNT_GP                                       -- 공장구분
				 , REPLACE(PASS_PROC1,'6R','6K')        AS PASS_PROC1   
				 , REPLACE(PASS_PROC2,'6R','6K')        AS PASS_PROC2   
				 , REPLACE(PASS_PROC3,'6R','6K')        AS PASS_PROC3   
				 , REPLACE(PASS_PROC4,'6R','6K')        AS PASS_PROC4   
				 , REPLACE(PASS_PROC5,'6R','6K')        AS PASS_PROC5   
			     , ORD_NO 		                                    -- 제작번호
			     , ORD_DTL 		                                    -- 제작행번
			     , COIL_T 			                                -- 코일두께
			     , COIL_W 			                                -- 코일폭
			     , CURR_COIL_LEN	                                -- 코일길이
			     , COIL_INDIA 		                                -- 코일내경
			     , COIL_OUTDIA 	                                    -- 코일외경
			     , DECODE(COIL_WT,0,NET_CAL_WT,COIL_WT) AS COIL_WT  -- 코일중량
			     , NEXT_PROC 		                                -- 차공정
			     , PLAN_PROC1                                       -- 계획공정
			     , BRANCH_CD 		                                -- 분기위치코드
			     , EXTEND_CONVEYOR_BRANCH_CD                        -- 확장분기위치코드
			     , HYSCO_TRANS_GP 	                                -- HYSCO이송수단
			     , COOL_METHOD 	                                    -- 냉각방법
			     , DECODE(CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',CURR_PROG_CD) AS CURR_PROG_CD
			     , RETURN_GP
			FROM  TB_PT_COILCOMM 
			WHERE COIL_NO = :V_COIL_NO
		*/	 
			JDTORecordSet jsCoilCom = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo", logId, methodNm, "공통 Coil정보 조회");
		    
		    if(jsCoilCom == null || jsCoilCom.size() == 0){ 
		    	if("91".equals(processId)){	
					throw new EJBServiceException("=Plate실적=>조업 91 NO HAVE COMMON COIL DATA");
					//return 	blRtn;
				}
			}
			
			if("".equals(sStockMoveTerm)){ 
				sStockMoveTerm =  YmConstant.NEW_STOCK_MOVE_TERM_A2;
			}
			/**
			*	2.	저장품Table에 정보를 등록,수정한다.
			*		최초 발생시 등록, 재 실적발생시 수정
			*/
				
		    /* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStock 
		    MERGE INTO TB_YM_STOCK ST USING (
		        SELECT :V_STOCK_ID          AS STOCK_ID                                 --재료번호
		             , :V_MODIFIER          AS MODIFIER         --수정자
		             , SYSDATE              AS MOD_DDTT         --수정일시
		             , 'N'                  AS DEL_YN           --삭제유무
		             , :V_STOCK_ITEM        AS STOCK_ITEM       --저장품 품목
		             , :V_STOCK_MOVE_TERM   AS STOCK_MOVE_TERM  --저장품 이동 조건
		          FROM DUAL
		    ) DD ON ( ST.STOCK_ID = DD.STOCK_ID)

		    WHEN NOT MATCHED THEN
		        INSERT (
		               STOCK_ID             , STOCK_ITEM        , STOCK_MOVE_TERM 
		             , REGISTER             , REG_DDTT          , MODIFIER  
		             , MOD_DDTT             , DEL_YN    
		             )
		        VALUES (
		               :V_STOCK_ID          , DD.STOCK_ITEM     , DD.STOCK_MOVE_TERM 
		             , DD.MODIFIER          , DD.MOD_DDTT       , DD.MODIFIER  
		             , DD.MOD_DDTT          , DD.DEL_YN 
		             )
		    WHEN MATCHED THEN 
		        UPDATE SET
		               STOCK_ITEM       = (CASE WHEN KEEPSTOCK_STL_YN='Y' THEN STOCK_ITEM ELSE DD.STOCK_ITEM END) 
		             , STOCK_MOVE_TERM  = DD.STOCK_MOVE_TERM 
		             , MODIFIER         = DD.MODIFIER 
		             , MOD_DDTT         = DD.MOD_DDTT                  
		   */          
		    jrParam1.setField("STOCK_ITEM END" , YmConstant.ITEM_HP);
		    jrParam1.setField("STOCK_MOVE_TERM", sStockMoveTerm);
		    commDao.update(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStock", logId, methodNm, "저장품Table 수정");
		    
			/*
			 * 야드 맵 확인 및 수정
			 */
		    /* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getEmptyLoc 
		    SELECT STACK_COL_GP||STACK_BED_GP||STACK_LAYER_GP AS LOCATION
		      FROM TB_YM_STACKLAYER
		     WHERE STACK_COL_GP LIKE :V_YD_GP||'%' 
		       AND STOCK_ID IS NULL
		       AND STACK_LAYER_STAT = 'E'
		       AND ROWNUM = 1
		    */   
		    String putPosition = "";
		    jrParam1.setField("YD_GP"	, yardId);
		    JDTORecordSet jsEmptyloc = commDao.select(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getEmptyLoc", logId, methodNm, "저장품Table 조회");
		    if (jsEmptyloc.size() > 0 ) {
		    	putPosition = jsEmptyloc.getRecord(0).getFieldString("LOCATION");
		    } else {
		    	putPosition = yardId + "A01010101";
		    }

		    jrParam1.setField("FROM_ADDR"	, "");
		    jrParam1.setField("YD_STR_LOC"	, putPosition);
		
		    /*********************************
		     * 산적위치 수정 로직 호출
		     *********************************/
		    
		    EJBConnector ejbConn1 = new EJBConnector("default", "BCoilJspSeEJB", this);
		    jrRtn = (JDTORecord)ejbConn1.trx("changeCoilLocationInfo", new Class[] { JDTORecord.class }, new Object[] { jrParam1 });

		
		    commUtils.printLog(logId, methodNm, "S-");
		
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}    
	return jrRtn;
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : B열연크레인주행금지구간작업실적응답(getYMA7L005_recv) 전문 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYMA7L005_recv(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인주행금지구간작업실적응답 조회[BCoilComm.getYMA7L005_recv] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		 
		try {
			//수신 항목 값 
			String msgId      = "YMA7L005"; //전문ID
			
			String ydL3HdRsCd = commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD"));//야드L3처리결과코드
			String ydL3Msg    = "";														//야드L3처리결과메세지
			
			String ydBayGP = commUtils.trim(rcvMsg.getFieldString("BAY_GP"));
			String ydRepA     = commUtils.trim(rcvMsg.getFieldString("A"        ));//A동 대표크레인
			String ydRepB     = commUtils.trim(rcvMsg.getFieldString("B"        ));//B동 대표크레인
			String ydRepC     = commUtils.trim(rcvMsg.getFieldString("C"        ));//C동 대표크레인
			String ydRepD     = commUtils.trim(rcvMsg.getFieldString("D"        ));//D동 대표크레인
			String ydRepE     = commUtils.trim(rcvMsg.getFieldString("E"        ));//E동 대표크레인
			
			
			if ("0000".equals(ydL3HdRsCd)) {
				ydL3Msg = ydL3Msg + "주행금지구역 설정 처리완료";
			} else {
				ydL3Msg = ydL3Msg + " 오류 <" + logId + ">";
			}
			
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			// 없음
			
			
			/**********************************************************
			* 2. 크레인작업실적응답 전문 생성
			**********************************************************/
			//야드L3Message가 없으면 생성
			

			StringBuffer sbMsg = new StringBuffer();

			sbMsg = sbMsg.append(msgId                                      ); //전문ID
			sbMsg = sbMsg.append(commUtils.getDateTime18()                  ); //생성일,생성시간(yyyy-MM-ddHH:mm:ss)
			sbMsg = sbMsg.append("I"                                        ); //전문구분
			sbMsg = sbMsg.append("0078"                                     ); //전문길이
			sbMsg = sbMsg.append(commUtils.getRPad(" "	     , 29, " ")     ); //임시
			if("A".equals(ydBayGP)){
				sbMsg = sbMsg.append(commUtils.getRPad(ydRepA	     ,  6, " ")     ); //A동 대표크레인
			}else if("B".equals(ydBayGP)){
				sbMsg = sbMsg.append(commUtils.getRPad(ydRepB	     ,  6, " ")     ); //B동 대표크레인
			}else if("C".equals(ydBayGP)){
				sbMsg = sbMsg.append(commUtils.getRPad(ydRepC	     ,  6, " ")     ); //C동 대표크레인
			}else if("D".equals(ydBayGP)){
				sbMsg = sbMsg.append(commUtils.getRPad(ydRepD	     ,  6, " ")     ); //D동 대표크레인
			}else if("E".equals(ydBayGP)){
				sbMsg = sbMsg.append(commUtils.getRPad(ydRepE	     ,  6, " ")     ); //E동 대표크레인
			}
			
			sbMsg = sbMsg.append(commUtils.getRPad(" "       ,  1, " ")     ); //야드작업진행상태
			sbMsg = sbMsg.append(commUtils.getRPad(" "       ,  8, " ")     ); //야드스케쥴코드
			sbMsg = sbMsg.append(commUtils.getRPad(" "       , 18, " ")     ); //야드크레인스케쥴ID
			sbMsg = sbMsg.append(commUtils.getRPad(" "       ,  1, " ")     ); //야드L2실적구분
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3HdRsCd,  4, " ")     ); //야드L3처리결과코드
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3Msg   , 40, " ")     ); //야드L3Message

			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();

			sndL2Msg.setResultCode(logId);		//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
			sndL2Msg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndL2Msg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndL2Msg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

			//전송 Data Return
			return commUtils.addSndData(sndL2Msg);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}//End getYMA7L005New
}