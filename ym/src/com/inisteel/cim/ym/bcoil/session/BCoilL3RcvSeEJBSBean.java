/**
 * @(#)BCoilL3RcvSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      B열연 COIL 야드 L3 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bcoil.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.message.MessageSenderTalk;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.util.YmConstant;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;


/**
 *      [A] 클래스명 : B열연 COIL 야드 L3수신 처리
 *
 * @ejb.bean name="BCoilL3RcvSeEJB" jndi-name="BCoilL3RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class BCoilL3RcvSeEJBSBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();

	private YmCommDAO commDao = new YmCommDAO();
	private YmComm ymComm = new YmComm();
	private BCoilComm bcoilComm = new BCoilComm();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	
	/**	
	 * [A] 오퍼레이션명 : 코일정보 수신(POYMJ001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPOYMJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일정보 수신[BCoilL3RcvSeEJB.rcvPOYMJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
 
			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String procId 		= commUtils.trim(rcvMsg.getFieldString("ProcessID"));		// 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
			String CoilNo 		= commUtils.trim(rcvMsg.getFieldString("CoilNo"));			// 'S' Scrap  'H' A열연  'K' B열연 
			String ydGp 		= commUtils.trim(rcvMsg.getFieldString("YardID"));			// 야드 구분
			String procCd		= commUtils.trim(rcvMsg.getFieldString("ProcessCode"));		//
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			
			if(!ydGp.equals("3")) {
				
				JDTORecord jrResult =  commDao.getYfNewModuleEffYn();
                String sACOIL_EFF_YN = commUtils.nvl(jrResult.getFieldString("ACOIL_EFF_YN"),"N");
                
                String szMsg = "YmCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                commUtils.printLog(logId, methodNm + szMsg , "SL");
                
                if( "1".equals(ydGp) && "Y".equals(sACOIL_EFF_YN) )
                {
                	commUtils.printLog(logId, methodNm + "YF 신규모듈 호출" , "SL");
                    EJBConnector ejbCon = new EJBConnector("default", "YfRcvFaEJB", this);
                    ejbCon.trx("rcvInterface", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });

                    return jrRtn;
                }
                else
                {
				
                	commUtils.printLog(logId, methodNm + "이전 시스템 기동" , "SL");
                	EJBConnector ejbCon = new EJBConnector("default", "JNDICoilInfoReg", this);
                	((Boolean)ejbCon.trx("receivePOYM001", 
                			new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg })).booleanValue();
                	return jrRtn;
				 
                }
			}
			commUtils.printLog(logId, methodNm + "신 시스템 기동" , "SL");

			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("PROCESS_ID"	, procId);
			jrParam.setField("COIL_NO"   	, CoilNo);
			jrParam.setField("YARD_ID"   	, ydGp);
			jrParam.setField("PROCESS_CODE" , procCd);
			jrParam.setField("MODIFIER"		, modifier);
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			
			
			boolean isSuccess = false;
			/*
			 * 1. 압연실적 (Back-up포함)처리시점
			 */
			if(procId.equals("01")){
				// isVal = setInnerIFCoilInfo_01( sCoilNo, sYardID , "01");
				jrRtn = commUtils.addSndData(jrRtn, bcoilComm.setInnerIFCoilInfo_01(jrParam));
			/* 
			 * 2. 정정실적 (Back-up포함)처리시점
			 */
			} else if(procId.equals("02")){
				
				//isVal = setInnerIFCoilInfo_02( sCoilNo,"SPM");
				jrParam.setField("WORK_CHK"  , "SPM");
				isSuccess = bcoilComm.setInnerIFCoilInfo_02(jrParam);
			/*
			 * 5. 보류재 처리 시점
			 */
			} else if(procId.equals("05")){
				
				//WORK_CHK
				//isVal = setInnerIFCoilInfo_03( sCoilNo,  sYardID );
				isSuccess = bcoilComm.setInnerIFCoilInfo_03(jrParam);
					
			/*
			 * 7. HFL처리시점
			 */
			} else if(procId.equals("07")){
				
				//isVal = setInnerIFCoilInfo_02( sCoilNo,"HFL");
				jrParam.setField("WORK_CHK"   , "HFL");
				isSuccess = bcoilComm.setInnerIFCoilInfo_02(jrParam);
				
			/*
			 * 8. 모 Coil 종료
			 */
			} else if(procId.equals("08")){
				
				jrRtn = commUtils.addSndData(jrRtn, bcoilComm.setInnerIFCoilInfo_05(jrParam));
			/*
			 * 9. 자 Coil
			 */
			} else if(procId.equals("09")){
				
				//isVal = setInnerIFCoilInfo_06( sCoilNo );
				//isSuccess = bcoilComm.setInnerIFCoilInfo_06(jrParam);//EJBServiceException 확인
				jrRtn = commUtils.addSndData(jrRtn, bcoilComm.setInnerIFCoilInfo_06(jrParam));
			/*
			 * 10. 반납 시점
			 */
			} else if(procId.equals("10")){
				
				//isVal = setInnerIFCoilInfo_04( sCoilNo );
				isSuccess = bcoilComm.setInnerIFCoilInfo_04(jrParam);
			
			/*
			 * J2. SPM 재작업(J2재작업, J3정보상의 재작업)
			 */
			} else if (procId.equals("J2") || procId.equals("J3")) {
				
				//isVal = setInnerIFCoilInfo_07( sCoilNo, sYardID);
				jrParam.setField("PROCESS_ID", procId);
				jrRtn = commUtils.addSndData(jrRtn, bcoilComm.setInnerIFCoilInfo_07(jrParam));
			
			} else if(procId.equals("91")){		
				
				//isVal = setInnerIFCoilInfo_01( sCoilNo, sYardID , "91");	
				jrRtn = commUtils.addSndData(jrRtn, bcoilComm.setInnerIFCoilInfo_01(jrParam));
			/*
			 * DC. DC OFF.
			 *		Coil 공통 테이블에 있는 정보만 처리한다.
			 */
			} else if(procId.equals("DC")){		
				
				//isVal = setInnerIFCoilInfo_01( sCoilNo, sYardID , "DC");		
				jrRtn = commUtils.addSndData(jrRtn, bcoilComm.setInnerIFCoilInfo_01(jrParam));
			/*
			 * 11. 요구차 공정 변경.
			 *		Coil 다음 공정 정보 변경시 처리.
			 */
			} else if(procId.equals("11")){		
				
				//isVal = setInnerIFCoilInfo_11( sCoilNo, sYardID , sProcessCode);			
				isSuccess = bcoilComm.setInnerIFCoilInfo_11(jrParam);//
				
				/**********************************************************
				* 6. 저장품제원정보 (YMA7L002) 송신
				**********************************************************/
				JDTORecord recInTemp  = JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);	    //Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("TC_CD"          , "YMA7L002");
				recInTemp.setField("MSG_GP"         , "I");
				recInTemp.setField("YD_INFO_SYNC_CD", "5");
				recInTemp.setField("STOCK_ID"       , CoilNo);
				
				//전송 Data 생성
				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L002", recInTemp));
			}
			/*
			 * HP. HR-Plate 생성
			 */
			else if(procId.equals("HP")){		
				
				//isVal = setInnerIFCoilInfo_HP( sCoilNo, sYardID , sProcessCode);
				jrRtn = bcoilComm.setInnerIFCoilInfo_HP(jrParam);
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일 결번 실적 수신(POYMJ002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPOYMJ002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일 결번 실적 수신[BCoilL3RcvSeEJB.rcvPOYMJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			
			String sYD_GP    = commUtils.trim(rcvMsg.getFieldString("yardID"));
			String sCOIL_NO  = commUtils.trim(rcvMsg.getFieldString("CoilNo"));

			if(!sYD_GP.equals("3")) {
				 
				JDTORecord jrResult =  commDao.getYfNewModuleEffYn();
                String sACOIL_EFF_YN = commUtils.nvl(jrResult.getFieldString("ACOIL_EFF_YN"),"N");
                
                String szMsg = "YmCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                commUtils.printLog(logId, methodNm + szMsg , "SL");
                
                if("1".equals(sYD_GP) && "Y".equals(sACOIL_EFF_YN) )
                {
                	commUtils.printLog(logId, methodNm + "YF 신규모듈 호출" , "SL");
                    EJBConnector ejbCon = new EJBConnector("default", "YfRcvFaEJB", this);
                    ejbCon.trx("rcvInterface", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });

                    return jrRtn;
                }
                else
                {
				
					commUtils.printLog(logId, methodNm + "이전 시스템 기동" , "SL");
					 
					EJBConnector ejbCon = new EJBConnector("default", "JNDICoilInfoReg", this);
					((Boolean)ejbCon.trx("receivePOYM002", 
							new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg })).booleanValue();
					return jrRtn;
                }
				
			}
			commUtils.printLog(logId, methodNm + "신 시스템 기동" , "SL");

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sCOIL_NO)) {
				throw new Exception("코일이 없습니다..");
			}

			/**********************************************************
			* 2. 조업에서 코일결번 실적 발생후 종합판정한뒤 야드에게 실적 처리완료 정보를 송신 
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("STOCK_ID", sCOIL_NO);
			jrParam.setField("MODIFIER", modifier);
			jrParam.setField("TC_CD"   , YmConstant.POYMJ002);
			
			JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
			
			String sCURR_PROG_CD    = jrRtnProg.getFieldString("CURR_PROG_CD");
			String sSTOCK_MOVE_TERM = jrRtnProg.getFieldString("STOCK_MOVE_TERM");
				
			if (!sSTOCK_MOVE_TERM.equals("")) {
	    		/*
				 UPDATE TB_YM_STOCK
					SET	
						STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM,
						MODIFIER   = :V_MODIFIER
					 	MOD_DDTT   = SYSDATE           
				WHERE STOCK_ID = :V_STOCK_ID
	    		 */
				jrParam.setField("STOCK_ID"       , sCOIL_NO);
	    		jrParam.setField("STOCK_MOVE_TERM", sSTOCK_MOVE_TERM);
	    		commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockTransInfo");
	    	}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**	
	 * [A] 오퍼레이션명 : 코일 SPM/HFL 작업 요구 정보를 수신(POYMJ004)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPOYMJ004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일 SPM/HFL 작업 요구 정보를 수신[BCoilL3RcvSeEJB.rcvPOYMJ004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrRst = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp 		= commUtils.trim(rcvMsg.getFieldString("YardId"));			// 야드 구분
			String WorkId 		= commUtils.trim(rcvMsg.getFieldString("WorkId"));			// 공정코드 S:SPM,H:HFL,D:결속대   // 2SPM  처리
			String procId 		= commUtils.trim(rcvMsg.getFieldString("ProcessId"));		// 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
			String CoilNo 		= commUtils.trim(rcvMsg.getFieldString("CoilNo"));			// 'S' Scrap  'H' A열연  'K' B열연 
			String position 	= commUtils.trim(rcvMsg.getFieldString("Position"));		// 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
			String takeOutProc 	= commUtils.trim(rcvMsg.getFieldString("TakeOutProcess"));	// Take-Out시  1:결번,2:임시보류처리(잠시 내려놨다가 Take-In할 Coil)
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			commUtils.printParam("", rcvMsg);

			if(!ydGp.equals("3")) {
				
				JDTORecord jrResult =  commDao.getYfNewModuleEffYn();
                String sACOIL_EFF_YN = commUtils.nvl(jrResult.getFieldString("ACOIL_EFF_YN"),"N");
                
                String szMsg = "YmCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                commUtils.printLog(logId, methodNm + szMsg , "SL");
                
                if( "1".equals(ydGp) && "Y".equals(sACOIL_EFF_YN) )
                {
                	commUtils.printLog(logId, methodNm + "YF 신규모듈 호출" , "SL");
                    EJBConnector ejbCon = new EJBConnector("default", "YfRcvFaEJB", this);
                    ejbCon.trx("rcvInterface", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });

                    return jrRtn;
                }
                else
                {
				
					commUtils.printLog(logId, methodNm + "이전 시스템 기동" , "SL");
					EJBConnector ejbCon = new EJBConnector("default", "JNDISPMConStatReg", this);
					((Boolean)ejbCon.trx("receivePOYM004", 
			        	  new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg })).booleanValue();
					return jrRtn;
				 
                }
			}
			commUtils.printLog(logId, methodNm + "신 시스템 기동" , "SL");
			
			JDTORecord rcvMsg1 = JDTORecordFactory.getInstance().create();
			rcvMsg1.setResultCode(logId);	//Log ID
			rcvMsg1.setResultMsg(methodNm);	//Log Method Name
			rcvMsg1.setField("MODIFIER"     , modifier  ); //수정자
			rcvMsg1.setField("YARDID"   	, ydGp);
			rcvMsg1.setField("WORKID" 		, WorkId);
			rcvMsg1.setField("PROCESSID"	, procId);
			rcvMsg1.setField("COILNO"   	, CoilNo);
			rcvMsg1.setField("POSITION"   	, position);
			rcvMsg1.setField("MODIFIER"		, modifier);
			
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"  , modifier  ); //수정자
			jrParam.setField("STOCK_ID"  , CoilNo);

			boolean isSuccess = false;
			
			if(procId.equals("2")){	
				/***********************************
				 * 보급 취소
				 **********************************/
				
				commUtils.printLog(logId, methodNm + "보급취소: " + CoilNo , "SL");	
				
				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdWrkbookDelChk 
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
				
				JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdWrkbookDelChk2", logId, methodNm, "크레인스케줄재료 조회");

				String ydCrnSchId = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID")); //크레인 작업지시
				String ydWbookId  = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID"));  //작업예약ID

				commUtils.printLog(logId, "작업취소 [ " + ydCrnSchId + " - " + ydWbookId + " ]", "SL");
				
				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
				if(!ydCrnSchId.equals("")) {
					/**********************************************************
					* 1.1 크레인스케줄 취소
					**********************************************************/
					jrParam.setField("WRK_CNCL_YN", "Y"); //작업취소 여부
					jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					jrRtn = commUtils.addSndData(jrRtn, jrRst);
				}
				
				if(!ydWbookId.equals("")) {
					/**********************************************************
					* 2.1 작업예약 취소
					**********************************************************/
					jrRst = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					jrRtn = commUtils.addSndData(jrRtn, jrRst);
				}	
				
			} else if (procId.equals(YmConstant.PROCESS_ID_1)){                           // 보급
				
				/***********************************
				 * 보급처리
				 **********************************/			
				commUtils.printLog(logId, methodNm + "SPM 보급: " + CoilNo , "SL");	
                  
				if(WorkId.equals("S")||WorkId.equals("H")){ //SPM, HFL
					
					/****  SPM, HFL : layer 위치 확인  *****/
					isSuccess = SpmHflProcessCheck(rcvMsg1);
					
					if (isSuccess == true) {
						/****  스케줄/작업예약 생성/ 스케쥴 호출  *****/
						jrRtn = this.callLineInOut(logId, methodNm, ydGp, WorkId, procId, CoilNo,modifier);
					}	
					
				} else if(WorkId.equals("D")) {       ///HFL 결속대

					/****  스케줄/작업예약 생성/ 스케쥴 호출  *****/
					jrRtn = this.callLineInOut(logId, methodNm, ydGp, WorkId, procId, CoilNo,modifier);
					
					return jrRtn;
				}
				
				
			} else if (procId.equals(YmConstant.PROCESS_ID_3)){                           // 추출
				/***********************************
				 * 추출처리
				**********************************/		
				//stock 에 없는 경우 생성처리
				this.procCoilStock(logId, methodNm, ydGp, WorkId, procId, CoilNo,modifier);
				
				
				if(WorkId.equals("S")||WorkId.equals("H")){ 
					//SPM, HFL
					/***********************************
					 * 보관매출 체크
					 * 보관매출인경우 추출요구 시 자동으로 실적 발생 후 추출 처리
					 **********************************/			
					/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilKeepstockInfo
					SELECT A.STOCK_ID
					  FROM USRYMA.TB_YM_STOCK A   
					     , USRYMA.TB_YM_STACKLAYER b
					 WHERE A.STOCK_ID = B.STOCK_ID
					   AND STACK_COL_GP LIKE '___E%'
					   AND A.KEEPSTOCK_STL_YN = 'Y'
					   AND A.STOCK_ID         = :V_STOCK_ID
					   AND B.STACK_LAYER_STAT = 'C'
					*/	   
					JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilKeepstockInfo", logId, methodNm, "코일작업지시read");

					if(jsCrnSch.size()> 0){
						//정정실적 처리 
						String Workchk = "";
						if(WorkId.equals("H")){
							Workchk="HFL";
						} else {
							Workchk="SPM";
						}
						
						commUtils.printLog(logId, methodNm + ">>★★★★★★★★★보관매출 자동정정실적처리>>>" + CoilNo , "SL");	
						JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
						jrParam1.setResultCode(logId);	//Log ID
						jrParam1.setResultMsg(methodNm);	//Log Method Name
						jrParam1.setField("MODIFIER"  , modifier  ); //수정자
						jrParam1.setField("COIL_NO"   , CoilNo);
						jrParam1.setField("WORK_CHK"  , Workchk);
						
						bcoilComm.setInnerIFCoilInfo_02(jrParam1);
						
					}
					//***************************************************************************************************************
					
					/****  SPM, HFL : layer 에 없는 경우 생성 처리함  *****/
					isSuccess = SpmHflProcessCheck(rcvMsg1);
					
					if (isSuccess == true){
						/****  스케줄/작업예약 생성/ 스케쥴 호출  *****/
						jrRtn = this.callLineInOut(logId, methodNm, ydGp, WorkId, procId, CoilNo,modifier);
					}
				} else {
					/****  스케줄/작업예약 생성/ 스케쥴 호출  *****/
					jrRtn = this.callLineInOut(logId, methodNm, ydGp, WorkId, procId, CoilNo,modifier);
					return jrRtn;
				}
						
			} else if (procId.equals(YmConstant.PROCESS_ID_4)){                     // Take-Out
				/***********************************
				 * Take-Out
				 **********************************/			
				//stock 에 없는 경우 생성처리
				this.procCoilStock(logId, methodNm, ydGp, WorkId, procId, CoilNo,modifier);
				
				if(WorkId.equals("S")||WorkId.equals("H")){ //SPM, HFL
					jrRtn = this.callTakeOut(rcvMsg1);
				}	
				
			} else if (procId.equals(YmConstant.PROCESS_ID_5)){                     // Take-in
				/***********************************
				 * Take-in
				 **********************************/			
				if(WorkId.equals("S")||WorkId.equals("H")){ //SPM, HFL
					
					/****  SPM, HFL : layer 위치 확인  *****/
					isSuccess = SpmHflProcessCheck(rcvMsg1);
						
					if (isSuccess == true){
						/****  스케줄/작업예약 생성/ 스케쥴 호출  *****/
						jrRtn = this.callLineInOut(logId, methodNm, ydGp, WorkId, procId, CoilNo,modifier);
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
	
	
	/**	
	 * [A] 오퍼레이션명 : 코일공냉재 실적 수신(POYMJ008)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPOYMJ008(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일공냉재 실적 수신[BCoilL3RcvSeEJB.rcvPOYMJ008] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			
			String sYD_GP    = commUtils.trim(rcvMsg.getFieldString("yardID"));
			String sSTOCK_ID = commUtils.trim(rcvMsg.getFieldString("stockid"));

			if(!sYD_GP.equals("3")) {
				
				JDTORecord jrResult =  commDao.getYfNewModuleEffYn();
                String sACOIL_EFF_YN = commUtils.nvl(jrResult.getFieldString("ACOIL_EFF_YN"),"N");
                
                String szMsg = "YmCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                commUtils.printLog(logId, methodNm + szMsg , "SL");
                
                if( "1".equals(sYD_GP) && "Y".equals(sACOIL_EFF_YN) )
                {
                	commUtils.printLog(logId, methodNm + "YF 신규모듈 호출" , "SL");
                    EJBConnector ejbCon = new EJBConnector("default", "YfRcvFaEJB", this);
                    ejbCon.trx("rcvInterface", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });

                    return jrRtn;
                }
                else
                {
				
					commUtils.printLog(logId, methodNm + "이전 시스템 기동" , "SL");
					EJBConnector ejbCon = new EJBConnector("default", "JNDIACCoolOrdReg", this);
					((Boolean)ejbCon.trx("receivePOYM008", 
			        	  new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg })).booleanValue();
					return jrRtn;
                }
			}
			commUtils.printLog(logId, methodNm + "신 시스템 기동" , "SL");
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sSTOCK_ID)) {
				throw new Exception("저장품이 없습니다..");
			} else if ("".equals(sSTOCK_ID)) {
				throw new Exception("저장품이 없습니다..");
			}

			/**********************************************************
			* 2. 조업에서 공냉재 실적 발생후 종합판정한뒤 야드에게 실적 처리완료 정보를 송신 
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("TC_CD"   , YmConstant.POYMJ008);
			jrParam.setField("STOCK_ID", sSTOCK_ID);
			jrParam.setField("MODIFIER", modifier);
			
			JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
			
			String sCURR_PROG_CD    = jrRtnProg.getFieldString("CURR_PROG_CD");
			String sSTOCK_MOVE_TERM = jrRtnProg.getFieldString("STOCK_MOVE_TERM");
				
			if (YmConstant.NEW_STOCK_MOVE_TERM_HG.equals(sSTOCK_MOVE_TERM)) {
	    		/*
				UPDATE TB_YM_STOCK
				   SET STOCK_ITEM	   = (CASE WHEN KEEPSTOCK_STL_YN='Y' THEN STOCK_ITEM ELSE :V_STOCK_ITEM END)
				     , STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
				     , MODIFIER   = :V_MODIFIER
				     , MOD_DDTT   = SYSDATE           
				 WHERE STOCK_ID = :V_STOCK_ID
	    		 */
				jrParam.setField("STOCK_ID"       , sSTOCK_ID);
	    		jrParam.setField("STOCK_ITEM"     , YmConstant.ITEM_CG);
	    		jrParam.setField("STOCK_MOVE_TERM", sSTOCK_MOVE_TERM);
	    		commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockTransInfo_06");
	    	} else {
	    		/*
				UPDATE TB_YM_STOCK
				   SET STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
				     , MODIFIER   = :V_MODIFIER
				     , MOD_DDTT   = SYSDATE           
				 WHERE STOCK_ID = :V_STOCK_ID
	    		 */
	    		jrParam.setField("STOCK_ID"       , sSTOCK_ID);
	    		jrParam.setField("STOCK_MOVE_TERM", sSTOCK_MOVE_TERM);
	    		commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockTransInfo");
	    	}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일 SPM2 작업 요구 정보를 수신(POYMJ010)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPOYMJ010(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일 SPM2 작업 요구 정보를 수신[BCoilL3RcvSeEJB.rcvPOYMJ010] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrRst = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp 		= commUtils.trim(rcvMsg.getFieldString("YardId"));			// 야드 구분
			String WorkId 		= commUtils.trim(rcvMsg.getFieldString("WorkId"));			// 공정코드 2SPM  처리
			String procId 		= commUtils.trim(rcvMsg.getFieldString("ProcessId"));		// 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In, 7 포장모드 보급(출측1번지 3EKD02-01로 보급한다)
			String CoilNo 		= commUtils.trim(rcvMsg.getFieldString("CoilNo"));			// 'S' Scrap  'H' A열연  'K' B열연 
			String position 	= commUtils.trim(rcvMsg.getFieldString("Position"));		// 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
			String takeOutProc 	= commUtils.trim(rcvMsg.getFieldString("TakeOutProcess"));	// Take-Out시  1:결번,2:임시보류처리(잠시 내려놨다가 Take-In할 Coil)
			String sTAKE_IN_GP	= commUtils.trim(rcvMsg.getFieldString("TAKE_IN_GP"));      // TAKE_IN 입측 출측 구분(화면 HFL/SPM 입출측조회 에만 존재하는 입력 값 TakeIn에서만 사용한다)
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			
			if(!ydGp.equals("3")) {
				commUtils.printLog(logId, methodNm + "이전 시스템 기동" , "SL");
				 EJBConnector ejbCon = new EJBConnector("default", "JNDISPMConStatMReg", this);
				 ((Boolean)ejbCon.trx("receivePOYM010", 
		        	  new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg })).booleanValue();
				 return jrRtn;
				
			}
			commUtils.printLog(logId, methodNm + "신 시스템 기동" , "SL");
			JDTORecord rcvMsg1 = JDTORecordFactory.getInstance().create();
			rcvMsg1.setResultCode(logId);	//Log ID
			rcvMsg1.setResultMsg(methodNm);	//Log Method Name
			rcvMsg1.setField("MODIFIER"     , modifier  ); //수정자
			rcvMsg1.setField("YARDID"   	, ydGp);
			rcvMsg1.setField("WORKID" 		, WorkId);
			rcvMsg1.setField("PROCESSID"	, procId);
			rcvMsg1.setField("COILNO"   	, CoilNo);
			rcvMsg1.setField("POSITION"   	, position);
			rcvMsg1.setField("TAKE_IN_GP"   , sTAKE_IN_GP);
			rcvMsg1.setField("MODIFIER"		, modifier);
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"  , modifier  ); //수정자
			jrParam.setField("STOCK_ID"  , CoilNo);

			boolean isSuccess = false;
			
			if (procId.equals("2")) {	
				/***********************************
				 * 보급 취소
				 **********************************/
				
				commUtils.printLog(logId, methodNm + "보급취소: " + CoilNo , "SL");	
				
				/* 
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
				JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdWrkbookDelChk2", logId, methodNm, "크레인스케줄재료 조회");

				String ydCrnSchId = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID")); //크레인 작업지시
				String ydWbookId  = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID"));  //작업예약ID

				commUtils.printLog(logId, "작업취소 [ " + ydCrnSchId + " - " + ydWbookId + " ]", "SL");
				
				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
				if(!ydCrnSchId.equals("")) {
					/**********************************************************
					* 1. 크레인스케줄 취소
					**********************************************************/
					jrParam.setField("WRK_CNCL_YN", "Y"); //작업취소 여부
					jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					jrRtn = commUtils.addSndData(jrRtn, jrRst);
				}
				
				if(!ydWbookId.equals("")) {
					/**********************************************************
					* 2. 작업예약 취소
					**********************************************************/
					jrRst = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					jrRtn = commUtils.addSndData(jrRtn, jrRst);
				}	
				
			} else if (procId.equals(YmConstant.PROCESS_ID_1) || procId.equals(YmConstant.PROCESS_ID_7)) { // 보급(1), 포장모드보급(7)
				
				/***********************************
				 * SPM2 보급 처리
				 **********************************/			
				commUtils.printLog(logId, methodNm + "SPM2 보급: " + CoilNo , "SL");	
				
				/****  SPM2 layer 위치 확인  *****/              
				isSuccess = Spm2ProcessCheck(rcvMsg1);
				
				if (isSuccess == true) {
					// 요구된 코일에 대한 작업예약 및 스케줄 생성한다.
					
					/****  스케줄/작업예약 생성/ 스케쥴 호출  *****/
					//jrRtn = this.callSPM2LineInOut(logId, methodNm, ydGp, WorkId, procId, CoilNo ,modifier);
					jrRtn = this.callSPM2LineInOut(rcvMsg1);

				}	
				
			} else if (procId.equals(YmConstant.PROCESS_ID_3)) { // 추출
				//stock 에 없는 경우 생성처리
				this.procCoilStock(logId, methodNm, ydGp, WorkId, procId, CoilNo,modifier);
				
				/***********************************
				 * 보관매출 체크
				 * 보관매출인경우 추출요구 시 자동으로 실적 발생 후 추출 처리
				 **********************************/			
				/*
				SELECT A.STOCK_ID
				  FROM USRYMA.TB_YM_STOCK A   
				     , USRYMA.TB_YM_STACKLAYER b
				 WHERE A.STOCK_ID = B.STOCK_ID
				   AND STACK_COL_GP LIKE '___E%'
				   AND A.KEEPSTOCK_STL_YN = 'Y'
				   AND A.STOCK_ID         = :V_COIL_NO
				   AND B.STACK_LAYER_STAT = 'C'
				*/	   
				JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilKeepstockInfo", logId, methodNm, "코일작업지시read");

				if (jsCrnSch.size()> 0) {
					//정정실적 처리 
					String Workchk = "";
					if (WorkId.equals("H")) {
						Workchk = "HFL";
					} else if(WorkId.equals("S")) {
						Workchk = "SPM";
					} else if(WorkId.equals("N")) {
						Workchk = "SPM2";
					}	
					
					commUtils.printLog(logId, methodNm + ">>★★★★★★★★★보관매출 자동정정실적처리>>>" + CoilNo , "SL");	
					JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
					jrParam1.setResultCode(logId);	//Log ID
					jrParam1.setResultMsg(methodNm);	//Log Method Name
					jrParam1.setField("MODIFIER"  , modifier  ); //수정자
					jrParam1.setField("COIL_NO"   , CoilNo);
					jrParam1.setField("WORK_CHK"  , Workchk);
					
					bcoilComm.setInnerIFCoilInfo_02(jrParam1);
					
				}
				//***************************************************************************************************************
				
				// SPM/HFL 여부 검사
				/****  SPM2 : layer 에 없는 경우 생성 처리함  *****/
				isSuccess = Spm2ProcessCheck(rcvMsg1);
				
				if (isSuccess == true){
					/****  스케줄/작업예약 생성/ 스케쥴 호출  *****/
					//jrRtn = this.callSPM2LineInOut(logId, methodNm, ydGp, WorkId, procId, CoilNo ,modifier);
					jrRtn = this.callSPM2LineInOut(rcvMsg1);
				}
						
			} else if (procId.equals(YmConstant.PROCESS_ID_4)) { // Take-Out
				//stock 에 없는 경우 생성처리
				this.procCoilStock(logId, methodNm, ydGp, WorkId, procId, CoilNo,modifier);
				
				jrRtn = callSPM2TakeOut(rcvMsg1);
				
			} else if (procId.equals(YmConstant.PROCESS_ID_5)) { // Take-in
				
				/****  SPM2 layer 위치 확인  *****/       
				isSuccess = Spm2ProcessCheck(rcvMsg1);

				String sAPP043 = ymComm.BCoilApplyYn("APP043","3","1");//출측 TAKE-IN
				/****  스케줄/작업예약 생성/ 스케쥴 호출  *****/
				if (isSuccess == true){
					if ("Y".equals(sAPP043) && "D".equals(sTAKE_IN_GP)){
						jrRtn = this.callSPM2LineInOutD(logId, methodNm, ydGp, WorkId, procId, CoilNo, modifier, sTAKE_IN_GP);						
					} else {
						//jrRtn = this.callSPM2LineInOut(logId, methodNm, ydGp, WorkId, procId, CoilNo, modifier);
						jrRtn = this.callSPM2LineInOut(rcvMsg1);
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
	
	 
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *  SPM / HFL Take-Out
	 * param YardID : 야드구분
	 * param WorkID : S SPM, H HFL
	 * param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * param CoilNo
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public JDTORecord callTakeOut(JDTORecord rcvMsg)throws DAOException {
		String methodNm = "SPM/HFL Take-Out[BCoilL3RcvSeEJB.callTakeOut] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		boolean isSuccess = false;

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String WorkId 		= commUtils.trim(rcvMsg.getFieldString("WORKID"));			// 공정코드 S:SPM,H:HFL,D:결속대   // 2SPM  처리
			String procId 		= commUtils.trim(rcvMsg.getFieldString("PROCESSID"));		//  4 Take-Out
			String CoilNo 		= commUtils.trim(rcvMsg.getFieldString("COILNO"));			// 'S' Scrap  'H' A열연  'K' B열연 
			String position 	= commUtils.trim(rcvMsg.getFieldString("POSITION"));		// 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));		//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
		
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STOCK_ID"	, CoilNo        ); //
			jrParam.setField("MODIFIER"         , modifier    ); //수정자
			/*************************************** 
			 * 	작업예약이 존재하면 ERROR
			 **************************************/	

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookYN 
			SELECT DECODE(COUNT(*),0,'N','Y') AS WB_STL_YN --작업예약재료여부
			  FROM TB_YM_WRKBOOKMTL WM
			     , TB_YM_WRKBOOK    WB
			WHERE WM.YD_WBOOK_ID    = WB.YD_WBOOK_ID
			  AND WM.STOCK_ID       = :V_STOCK_ID
			  AND WM.DEL_YN         = 'N'
			  AND WB.DEL_YN         = 'N'
			*/  	  
			JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookYN", logId, methodNm, "작업예약 등록여부");

			if (jsChk2 != null && jsChk2.size() > 0) {
				if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN"))) {
					commUtils.printLog(logId, "작업요구한 CoilNo이 이미 작업예약되어 있슴  Error : "+ CoilNo, "SL");
					return jrRtn;	
				}
			}	
			
			
			// 기존 위치 삭제
	   		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStackLayer
	   		UPDATE TB_YM_STACKLAYER
	   		   SET STOCK_ID         = NULL
	   		     , STACK_LAYER_STAT = 'E'
	   		     , MODIFIER = :V_MODIFIER
	   		     , MOD_DDTT = SYSDATE
	   		 WHERE STOCK_ID = :V_STOCK_ID
	   		   AND SUBSTR(STACK_COL_GP,1,1) = '3'
	   		 */  
	   		commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStackLayer", logId, methodNm, "기존 위치 삭제");
	   		 
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTrkStackColGplayerTakeOut 
			MERGE INTO TB_YM_STACKLAYER SL USING (
			    SELECT CASE WHEN PROC_GP = 'K' THEN --SPM
			                CASE WHEN BAY_GP = 'C' AND STACK_BED_GP IN ('01','02')  THEN '3CKE01'   --SPM
			                     ELSE '3BKE01' END
			           WHEN PROC_GP = 'H' THEN --HFL
			                CASE WHEN BAY_GP = 'A' AND STACK_BED_GP IN ('01','02')  THEN '3AFE01'   --HFL
			                     ELSE '3BFE01' END
			           WHEN PROC_GP = 'P' THEN --SPM2
			                CASE WHEN BAY_GP = 'D' AND STACK_BED_GP IN ('01','02')  THEN '3DKE01'   --HFL
			                     ELSE '3EKE01' END
			           END  AS   STACK_COL_GP   
			         , '3'           AS   STACK_COL_GP1       
			         , CASE WHEN PROC_GP = 'K' THEN --SPM
			                CASE WHEN BAY_GP = 'C' AND STACK_BED_GP IN ('01','02')  THEN 'C'   --SPM
			                     ELSE 'B' END
			           WHEN PROC_GP = 'H' THEN --HFL
			                CASE WHEN BAY_GP = 'A' AND STACK_BED_GP IN ('01','02')  THEN 'A'   --HFL
			                     ELSE 'B' END
			           WHEN PROC_GP = 'P' THEN --SPM2
			                CASE WHEN BAY_GP = 'D' AND STACK_BED_GP IN ('01','02')  THEN 'D'   --HFL
			                     ELSE 'E' END
			           END  AS   STACK_COL_GP2
			         , '00'          AS   STACK_BED_GP
			         , '01'          AS   STACK_LAYER_GP
			         , 'E'           AS   STACK_LAYER_ACTIVE_STAT 
			         , 'C'           AS   STACK_LAYER_STAT
			         , STL_NO        AS   STOCK_ID
			      FROM TB_YM_EQPTRACKING  A 
			     WHERE STL_NO = :V_STOCK_ID 
			 ) DD ON (SL.STACK_COL_GP = DD.STACK_COL_GP AND SL.STACK_BED_GP   = DD.STACK_BED_GP
			                                           AND SL.STACK_LAYER_GP = DD.STACK_LAYER_GP)
			WHEN MATCHED THEN UPDATE SET
			     SL.MODIFIER                = :V_MODIFIER
			    ,SL.MOD_DDTT                = SYSDATE
			    ,SL.STACK_LAYER_ACTIVE_STAT = DD.STACK_LAYER_ACTIVE_STAT
			    ,SL.STACK_LAYER_STAT        = DD.STACK_LAYER_STAT
			    ,SL.STOCK_ID                = DD.STOCK_ID 
			*/    
			
			int updCnt = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTrkStackColGplayerTakeOut", logId, methodNm, "TRACKING LAYER 등록");
			
			if(updCnt == 0 ) {
				commUtils.printLog(logId, "적치단(TB_YM_STACKLAYER) Table Read Error : "+ CoilNo, "SL");
				throw new Exception("적치단(TB_YM_STACKLAYER) Table Read Error : "+ CoilNo);
				//return jrRtn;	
			}
			
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
			JDTORecordSet jsStackLayer = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1", logId, methodNm, "TB_YM_STACKLAYER 정보");
			if (jsStackLayer.size() < 1) {
				commUtils.printLog(logId, "StackColGp 이상: "+ CoilNo, "SL");
				return jrRtn;				
	    	} 			

			jsStackLayer.first();
			JDTORecord jrStackLayer = jsStackLayer.getRecord();
			String ydStackColGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP"),     "");
			String ydGp			= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP1"),    "");
			String ydBayGp		= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP2"),    "");
			String ydStackBedGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_BED_GP"),     "");
			String ydStackLayer	= commUtils.nvl(jrStackLayer.getFieldString("STACK_LAYER_GP"),   "");
			String stackStat	= commUtils.nvl(jrStackLayer.getFieldString("STACK_LAYER_STAT"), "");
			String TmpEquip 	= ydStackColGp.substring(2,4); 
	    	/*
	    	 * Take-Out 요구가 왔을때 수신한 Coil이 Take-Out이 왔는데 Coil이 야드 Skid에 있다면 Error 
			 */
			
			//SPM보급
	    	if (WorkId.equals(YmConstant.WORK_SPM_S)){
    			if (procId.equals(YmConstant.PROCESS_ID_4)){      //SPM Take-Out STACK_COL_GP_3CKE01/STACK_COL_GP_3BKE01/STACK_COL_GP_3BKD01
    				if (ydStackColGp.equals(YmConstant.STACK_COL_GP_3CKE01) ||
						ydStackColGp.equals(YmConstant.STACK_COL_GP_3BKE01) ||
						ydStackColGp.equals(YmConstant.STACK_COL_GP_3BKD01)){ 
    			    }else{ 
    					return jrRtn;
    			    }
    			}	
    		}else if (WorkId.equals(YmConstant.WORK_HFL_H)){
    			if (procId.equals(YmConstant.PROCESS_ID_4)){      //HFL Take-Out STACK_COL_GP_3AFE01/STACK_COL_GP_3BFE01/STACK_COL_GP_3BFD01
    				if (ydStackColGp.equals(YmConstant.STACK_COL_GP_3AFE01) ||
    						ydStackColGp.equals(YmConstant.STACK_COL_GP_3BFE01) ||	
    						ydStackColGp.equals(YmConstant.STACK_COL_GP_3BFD01)){
    			    }else{ 
    					return jrRtn;
    			    }
    			}	
    		}
			
			String TmpstackCol = "";
			String ydSchCd     = "";
			if (ydStackColGp != null && !ydStackColGp.equals("")){
				if (WorkId.equals(YmConstant.WORK_SPM_S)){
					/************************** 
					 * SPM CHECK 
					 *************************/						
					ydSchCd 	= "3"+ydBayGp+"KE03LM" ;
				}else if (WorkId.equals(YmConstant.WORK_HFL_H)){

					ydSchCd 	= "3"+ydBayGp+"FE03LM" ;
				}
				
				/*************************************** 
				 * 	1.작업예약이 생성 
                 *  2.STOCK UPDATE(작업행선)  
                 *  3.스케쥴 호출
				 **************************************/					
	  			//작업예약,작업재료 등록		
    			JDTORecord jrOutTemp = JDTORecordFactory.getInstance().create();
    			jrOutTemp.setField("STL_NO"           , commUtils.trim(jrOutTemp.getFieldString("STOCK_ID"))); //재료번호
    			jrOutTemp.setField("STACK_COL_GP"     , ydStackColGp  ); 
    			jrOutTemp.setField("STACK_BED_GP"     , ydStackBedGp  ); 
    			jrOutTemp.setField("STACK_LAYER_GP"   , ydStackLayer ); 
    			jrOutTemp.setField("YD_SCH_CD"        , ydSchCd   );// 자동이적 
    			jrOutTemp.setField("MODIFIER"         , modifier    ); //수정자
//    			jrOutTemp.setField("YD_TO_LOC_GUIDE"  , toLocGuide    ); //TO위치가이드
    			
    			String ydWbookId = ymComm.procWkBookInsert(jrOutTemp);
    			
    			if(ydWbookId.equals(YmConstant.RETN_CD_FAILURE)){
    				throw new Exception("작업예약ID 생성 실패"); 				
    			}
				
				
				/*************************************** 
				 * 	1.저장품 수정
                 *  2.STOCK UPDATE(작업행선)  
                 *  3.스케쥴 호출
				 **************************************/
				// 저장품 Table(TB_YM_STOCK)에 작업예약ID,저장품이동조건을 Update 한다.	
				
				String sStackMoveTerm = "";
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name

				jrParam.setField("STOCK_ID"	, CoilNo);   //
				jrParam.setField("MODIFIER" , modifier); //수정자

				JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
				
				if(jrRtnProg.size() > 0 ) {
					sStackMoveTerm = commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"));
				}
								
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("MODIFIER" 		, modifier);    	//수정자
				jrParam.setField("WBOOK_ID" 		, ydWbookId); 		//작업예약
				jrParam.setField("PROCESS_ID" 		, procId);     	    //처리구분
				jrParam.setField("WORK_ID" 		    , WorkId);     	    //처리구분
				jrParam.setField("STOCK_MOVE_TERM" 	, sStackMoveTerm);  //저장품이동조건
				jrParam.setField("STOCK_ID"			, CoilNo);   		//
				
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo1 
				UPDATE TB_YM_STOCK
				   SET MODIFIER   = :V_MODIFIER
				     , MOD_DDTT   = SYSDATE 
				     , SHEAR_SUPPLY_GP          = CASE WHEN :V_PROCESS_ID = 1 THEN ''
				                                  ELSE SHEAR_SUPPLY_GP          END   -- 정정보급구분 
				     , SHEAR_SUPPLY_DEMAND_DDTT = CASE WHEN :V_PROCESS_ID = 1 THEN ''
				                                  ELSE SHEAR_SUPPLY_DEMAND_DDTT          END   --정정보급요구일시
				     , WBOOK_ID = :V_WBOOK_ID                              
				     , STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM  --저장품이동조건
				 WHERE STOCK_ID = :V_STOCK_ID   
				*/ 
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo1", logId, methodNm, "TB_YM_STOCK 수정");
				
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

				jrRtn = commUtils.addSndData(ymComm.getCrnSchMsg(jrYdMsg));				
					
				commUtils.printLog(logId, methodNm, "S-");
			}	
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
		    return jrRtn;
	}		

	/**
	 * STACKLAYER 에 위치등록
	 * @param YardID : 야드구분
	 * @param WorkID : S SPM, H HFL
	 * @param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * @param CoilNo
	 * @return
	 */
	private boolean SpmHflProcessCheck(JDTORecord rcvMsg)throws DAOException {
		String methodNm 	= "코일 위치 정보 확인[BCoilL3RcvSeEJB.SpmHflProcessCheck] < " + rcvMsg.getResultMsg();
		String logId	 	= rcvMsg.getResultCode();
		boolean isSuccess 	= false;
		JDTORecordSet jsStackLayer = JDTORecordFactory.getInstance().createRecordSet("");
		try {
			
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String WorkId 	= commUtils.trim(rcvMsg.getFieldString("WORKID"));		// 공정코드 S:SPM,H:HFL,D:결속대   // 2SPM 별도처리
			String procId 	= commUtils.trim(rcvMsg.getFieldString("PROCESSID"));	// 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
			String CoilNo 	= commUtils.trim(rcvMsg.getFieldString("COILNO"));		// 'S' Scrap  'H' A열연  'K' B열연 
			String modifier	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

	    	if (CoilNo.equals("")){

				commUtils.printLog(logId, methodNm + "Coil No = Space Error" , "SL");	
				return false;
	    	}
	    	
	    	JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"  	, modifier  ); //수정자
			jrParam.setField("STOCK_ID" 	, CoilNo);
			jrParam.setField("PROCESSID" 	, procId);
			jrParam.setField("WORKID" 	    , WorkId);
			
			if (procId.equals("3")) {
				/*********************
				 * 추출이거나  TAKE OUT 인 경우
				 *  - 입측 클리어 작업 
				 *  - TB_YM_STACKLAYER 에 없는 경우 생성처리 한다.
				 ******************************************/
				// 기존 위치 삭제
		   		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStackLayer
		   		UPDATE TB_YM_STACKLAYER
		   		   SET STOCK_ID         = NULL
		   		     , STACK_LAYER_STAT = 'E'
		   		     , MODIFIER = :V_MODIFIER
		   		     , MOD_DDTT = SYSDATE
		   		 WHERE STOCK_ID = :V_STOCK_ID
		   		   AND SUBSTR(STACK_COL_GP,1,1) = '3'
		   		 */  
		   		commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStackLayer", logId, methodNm, "기존 위치 삭제");
		   		

				/*********************
				 * 추출인 경우 
				 * TB_YM_STACKLAYER 에 없는 경우 생성처리 한다.
				 ******************************************/
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTrkStackColGplayer 
				MERGE INTO TB_YM_STACKLAYER SL USING (
				    SELECT CASE WHEN :V_WORKID = 'S'  THEN '3AKD01'   --SPM
				                WHEN :V_WORKID = 'H'  THEN '3CFD01'   --HFL
				                WHEN :V_WORKID = 'N'  THEN '3EKD01'   --SPM2
				                END      AS   STACK_COL_GP
				         , '3'           AS   STACK_COL_GP1
				         , CASE WHEN :V_WORKID = 'S'  THEN 'A'   --SPM
				                WHEN :V_WORKID = 'H'  THEN 'C'   --HFL
				                WHEN :V_WORKID = 'N'  THEN 'E'   --SPM2
				                END      AS   STACK_COL_GP2
				         , '00'          AS   STACK_BED_GP
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
				int updCnt = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTrkStackColGplayer", logId, methodNm, "TRACKING LAYER 등록");
				
				if(updCnt == 0 ) {
					commUtils.printLog(logId, "적치단(TB_YM_STACKLAYER) Table Read Error : "+ CoilNo, "SL");
					return false;
				}

			} 			
			
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
	    	jsStackLayer = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1", logId, methodNm, "TB_YM_STACKLAYER 정보");
	    	
			if (jsStackLayer.size() == 0) {
				commUtils.printLog(logId, "적치단(TB_YM_STACKLAYER)에 해당 재료가 없습니다. : "+ CoilNo, "SL");
				return false;
			}

			jsStackLayer.first();
			JDTORecord jrStackLayer = jsStackLayer.getRecord();
			String ydStackColGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP"),     "");
			String ydBayGp		= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP2"),    "");
			String ydEquip 		= ydStackColGp.substring(2,4);   
			
    		if (WorkId.equals(YmConstant.WORK_SPM_S)){	          // SPM 
    			if (procId.equals(YmConstant.PROCESS_ID_1)){      // 보급
    				if (ydBayGp.equals(YmConstant.BAY_GP_C) || ydBayGp.equals(YmConstant.BAY_GP_B)){  
	    				if (ydEquip.equals(YmConstant.WORK_SPM_IN_KE) || ydEquip.equals(YmConstant.WORK_SPM_OUT_KD)){ 
	    					isSuccess = false; 
	    			    }else{ 
	    			    	isSuccess = true;   
	    			    }		    					
    				}
    			}else if (procId.equals(YmConstant.PROCESS_ID_3)){//SPM 추출    
    				
    				if (ydBayGp.equals(YmConstant.BAY_GP_A) || ydBayGp.equals(YmConstant.BAY_GP_B)){	// 동 구분 A동 || B동

	    				if (ydEquip.equals(YmConstant.WORK_SPM_OUT_KD) ){ //SPM 출측
	    					isSuccess = true;  
	    			    }else{ 
	    			    	isSuccess = false;   
	    			    }
    				}else{
    					isSuccess = false;
    				}	    				
    			}else if (procId.equals(YmConstant.PROCESS_ID_5)){//SPM Take-In  : 적치열(3BKE01)
    				if (ydBayGp.equals(YmConstant.BAY_GP_B)){
	    				if (ydEquip.equals(YmConstant.WORK_SPM_IN_KE) || ydEquip.equals(YmConstant.WORK_SPM_OUT_KD)){ //SPM 출측
	    					isSuccess = false; 
	    			    }else{ 
	    			    	isSuccess = true;   
	    			    }		    					
    				}
    			}
    		// HFL
    		}else if (WorkId.equals(YmConstant.WORK_HFL_H)){
    			if (procId.equals(YmConstant.PROCESS_ID_1)){      //HFL 보급            : 적치열(3AFE01)
    				if (ydBayGp.equals(YmConstant.BAY_GP_A) || ydBayGp.equals(YmConstant.BAY_GP_B)){
	    				if (ydEquip.equals(YmConstant.WORK_HFL_IN_FE) || ydEquip.equals(YmConstant.WORK_HFL_OUT_FD)){
	    					isSuccess = false; 
	    			    }else{ 
	    			    	isSuccess = true;   
	    			    }		    					
    				}
    			}else if (procId.equals(YmConstant.PROCESS_ID_3)){//HFL 추출  
    				if (ydBayGp.equals(YmConstant.BAY_GP_B) || ydBayGp.equals(YmConstant.BAY_GP_C)){
	    				if (ydEquip.equals(YmConstant.WORK_HFL_OUT_FD)){
	    					isSuccess = true; 
	    			    }else{ 
	    			    	isSuccess = false;   
	    			    }		    					
    				}else{ 
    			    	isSuccess = false; 	    					
    				}	    				
    			}else if (procId.equals(YmConstant.PROCESS_ID_5)){//HFL Take-In  : 적치열(3BFE01)
    				if (ydBayGp.equals(YmConstant.BAY_GP_B)){
	    				if (ydBayGp.equals(YmConstant.WORK_HFL_IN_FE) || ydBayGp.equals(YmConstant.WORK_HFL_OUT_FD)){
	    					isSuccess = false;
	    			    }else{ 
	    			    	isSuccess = true;   
	    			    }		    					
    				}
    			}   			
    		}
	    	
    		commUtils.printLog(logId, methodNm, "S-");

	    	
	    	return isSuccess;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
						
 	/**
	 * 오퍼레이션명 : 코일 생성처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean procCoilStock(String logId, String methodNms, String YardID, String WorkID, String ProcessID, String CoilNo, String modifier){	
		String methodNm = "SPM / HFL 보급, 추출, Take-In[BCoilL3RcvSeEJB.procCoilStock] < " + methodNms;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			/************************** 
			 * 저장품 CHECK	 
			 *************************/			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STOCK_ID"	, CoilNo        ); //
			jrParam.setField("MODIFIER"	, modifier        ); //
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectStockIdNoDel 
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
			JDTORecordSet jsChk1 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectStockIdNoDel", logId, methodNm, " 저장품 등록여부");
			if (jsChk1.size() < 1) {
				
				if (ProcessID.equals(YmConstant.PROCESS_ID_3)||ProcessID.equals(YmConstant.PROCESS_ID_4)){  // 추출,TAKE OUT 인 경우 STOCK 이 없는 경우 생성
					// 코일번호가 S  SCRAP
			    	if (CoilNo.substring(0, 1).equals("S")) {
			    		/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insStockScrapInfo  
			    		MERGE INTO TB_YM_STOCK ST USING (
			    		    SELECT :V_STOCK_ID      AS STOCK_ID    --재료번호
			    		         , :V_MODIFIER      AS MODIFIER    --수정자
			    		         , SYSDATE          AS MOD_DDTT    --수정일시
			    		         , 'N'              AS DEL_YN      --삭제유무
			    		      FROM DUAL
			    		) DD ON ( ST.STOCK_ID = DD.STOCK_ID)
			    		WHEN NOT MATCHED THEN
			    		    INSERT (
			    		           STOCK_ID     , STOCK_ITEM    , STOCK_MOVE_TERM  --저장품이동조건
			    		         , REGISTER     , REG_DDTT      , MODIFIER         -- 'SYSTEM'
			    		         , MOD_DDTT     , DEL_YN )
			    		    VALUES (:V_STOCK_ID , 'CM'          , 'A2'
			    		         , 'SYSTEM'     , SYSDATE       , 'SYSTEM'
			    		         , SYSDATE      , 'N') 
			    		WHEN MATCHED THEN UPDATE SET
			    		     ST.STOCK_ITEM      = 'CM'
			    		   , ST.STOCK_MOVE_TERM = 'A2'
			    		   , ST.MODIFIER        = 'SYSTEM'
			    		   , ST.MOD_DDTT        = SYSDATE
						*/ 
			    		commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insStockScrapInfo", logId, methodNm, "SCRAP STOCK 등록");
			    		
			    	} else {
			    		//일반재
			    		/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insertStockTransInfo
			    		MERGE INTO TB_YM_STOCK ST USING (
			    		    SELECT :V_STOCK_ID      AS STOCK_ID    --재료번호
			    		         , :V_MODIFIER      AS MODIFIER    --수정자
			    		         , SYSDATE          AS MOD_DDTT    --수정일시
			    		         , 'N'              AS DEL_YN      --삭제유무
			    		      FROM DUAL
			    		) DD ON ( ST.STOCK_ID = DD.STOCK_ID)
			    		WHEN NOT MATCHED THEN
			    		    INSERT (
			    		           STOCK_ID     , STOCK_ITEM    , STOCK_MOVE_TERM  --저장품이동조건
			    		         , REGISTER     , REG_DDTT      , MODIFIER         -- 'SYSTEM'
			    		         , MOD_DDTT     , DEL_YN )
			    		    VALUES (:V_STOCK_ID , ''            , ''
			    		         , DD.MOD_DDTT  , DD.MOD_DDTT   , DD.MOD_DDTT
			    		         , DD.MOD_DDTT  , DD.DEL_YN 	) 
			    		WHEN MATCHED THEN UPDATE SET
			    		     ST.STOCK_ITEM      = ''
			    		   , ST.STOCK_MOVE_TERM = ''
			    		   , ST.MODIFIER        = DD.MODIFIER
			    		   , ST.MOD_DDTT        = DD.MOD_DDTT
			    		   , ST.DEL_YN          = DD.DEL_YN 						 
						*/
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insertStockTransInfo", logId, methodNm, "TB_YM_STOCK 생성여부");
			    	}
				} else {
					commUtils.printLog(logId, "COIL_NO :이상 "+ CoilNo, "SL");
					return false;

				}
				
	    	}	

			commUtils.printLog(logId, methodNm, "S-");

		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return true;
	} 
 	/**
	 * 오퍼레이션명 : SPM / HFL 보급, 추출, Take-In
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public JDTORecord callLineInOut(String logId, String methodNms, String YardID, String WorkID, String ProcessID, String CoilNo, String modifier){	
		String methodNm = "SPM / HFL 보급, 추출, Take-In[BCoilL3RcvSeEJB.callLineInOut] < " + methodNms;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STOCK_ID"	, CoilNo        ); //
			jrParam.setField("MODIFIER"	, modifier        ); //
			
			/*************************************** 
			 * 	작업예약이 존재하면 ERROR
			 **************************************/	

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookYN 
			SELECT DECODE(COUNT(*),0,'N','Y') AS WB_STL_YN --작업예약재료여부
			  FROM TB_YM_WRKBOOKMTL WM
			     , TB_YM_WRKBOOK    WB
			WHERE WM.YD_WBOOK_ID    = WB.YD_WBOOK_ID
			  AND WM.STOCK_ID       = :V_STOCK_ID
			  AND WM.DEL_YN         = 'N'
			  AND WB.DEL_YN         = 'N'
			*/  	  
			JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookYN", logId, methodNm, "작업예약 등록여부");

			if (jsChk2 != null && jsChk2.size() > 0) {
				if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN"))) {
					commUtils.printLog(logId, "작업요구한 CoilNo이 이미 작업예약되어 있슴  Error : "+ CoilNo, "SL");
					return jrRtn;	
				}
			}	
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1 
			-- STACK_COL_GP(적치열:첫번째자리 야드구분, 두번째 자리 동구분), STACK_LAYER_STAT(적치단 상태 L:적치중)
			SELECT STACK_COL_GP
			     , SUBSTR(STACK_COL_GP,1,1) STACK_COL_GP1
			     , SUBSTR(STACK_COL_GP,2,1) STACK_COL_GP2
			     , STACK_BED_GP
			     , STACK_LAYER_STAT
			     , STACK_LAYER_GP
			     -- 스크랩 적용여부
			     , NVL((SELECT DTL_ITM1 FROM USRYMA.TB_YM_RULE WHERE REPR_CD_GP = 'APP022' AND CD_GP = '3' AND ITEM = '1'),'N') AS APP022_YN --코일야드 스크랩적용 여부(E동)
			     , NVL((SELECT DTL_ITM1 FROM USRYMA.TB_YM_RULE WHERE REPR_CD_GP = 'APP022' AND CD_GP = '3' AND ITEM = '2'),'N') AS APP022_A_YN --코일야드 A동 스크랩추출 적용여부
			 FROM TB_YM_STACKLAYER  A
			WHERE STOCK_ID = :V_STOCK_ID
			 */	
			JDTORecordSet jsStackLayer = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1", logId, methodNm, "TB_YM_STACKLAYER 정보");

			if (jsStackLayer.size() < 1) {
				commUtils.printLog(logId, "StackColGp 이상: "+ CoilNo, "SL");
				return jrRtn;				
	    	} 	
			jsStackLayer.first();
			JDTORecord jrStackLayer = jsStackLayer.getRecord();
			String ydStackColGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP"),     "");
			String ydGp			= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP1"),    "");
			String ydBayGp		= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP2"),    "");
			String ydStackBedGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_BED_GP"),     "");
			String ydStackLayer	= commUtils.nvl(jrStackLayer.getFieldString("STACK_LAYER_GP"),   "");
			//String sAPP022_YN	= commUtils.nvl(jrStackLayer.getFieldString("APP022_YN"),        "N");
			String sAPP022_A_YN	= commUtils.nvl(jrStackLayer.getFieldString("APP022_A_YN"),      "N"); //코일야드 A동 스크랩추출 적용여부
			
			String TmpstackCol 	= "";
			String ydSchCd 		= "";  //SCH_CD
			String toLocGuide 	= "";  //TO위치 가이드

			if (ydStackColGp != null && !ydStackColGp.equals("")){

				if (WorkID.equals(YmConstant.WORK_SPM_S)){  
					/*************************************** 
					 * 	 SPM 작업
					 **************************************/				
							
					if (ProcessID.equals(YmConstant.PROCESS_ID_1)){		
						/*****  보급처리   ************/
						// 보급위치 : YD에서는 TO위치 1개만 관리, 보급요구가 오면 무조건 작업예약 생성
						ydSchCd 	= "3"+ydBayGp+"KE01UM" ;
						
//						//SPM 보급 요청시 입측 01BED 가 빈 상태일 경우만 스케줄 생성여부 기준을 확인하여 처리한다.
//						String sAPP053_SPM_YN = ymComm.BCoilApplyYn("APP053","3","SPM");   //SPM 보급요청시 01BED 비였을 때만 스케줄 생성 여부
//						commUtils.printLog(logId,  "==========[[[ APP053 SPM 보급요청시 01BED 비였을 때만 스케줄 생성 여부 : " + sAPP053_SPM_YN + " ]]]============", "SL");
//						
//						if ("Y".equals(sAPP053_SPM_YN)) {
//							jrParam.setField("PROC_GP"	, "K"); 
//							JDTORecordSet rsEqpTracking = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.bcoilHFLSPMInOutSdInqjl.getListCOILGPTracking_PIDEV", logId, methodNm, "SPM 트래킹 테이블 조회");
//							if(rsEqpTracking.size()>0) {
//								if(!"".equals(rsEqpTracking.getRecord(0).getFieldString("STOCK_ID"))) {
//									commUtils.printLog(logId, "<<<<< SPM 입측 01 BED 에 " + rsEqpTracking.getRecord(0).getFieldString("STOCK_ID") + " 코일이 존재하여 크레인 스케줄을 생성하지 않습니다! >>>>>" , "SL");
//									return jrRtn;
//								}
//							}
//						}

					} else if (ProcessID.equals(YmConstant.PROCESS_ID_3)){
						/*****  추출처리   ************/

						ydSchCd 	= "3"+ydBayGp+"KD01LM" ;
						
						// Scrap일 경우를 비교한다. 
						if (CoilNo.substring(0, 1).equals("S") ){	
							// Scrap SPM 추출.
							if ("N".equals(sAPP022_A_YN) ){
								ydSchCd 	= "3"+ydBayGp+"SP01LM" ;  // ERROR 처리 - SCARP 추출 요구가 와도 APP022 가 N 이면 스케줄코드을 3ASP01LM 으로 주고 뒤에서(작업예약생성시) 스케줄코드가 없어서 에러로 빠진다.
							} else {	
								ydSchCd 	= "3"+ydBayGp+"KD01LM" ;
							}
							
						}else{
//							toLocGuide 	= "";
						}
						ydStackLayer = "01";
						
									
					} else if (ProcessID.equals(YmConstant.PROCESS_ID_5)){
						/*****  Coil SPM Take In처리   ************/		
						ydSchCd 	= "3"+ydBayGp+"KE03UM" ;
					}	
					
				} else if (WorkID.equals(YmConstant.WORK_HFL_H)){  //HFL
					/*************************************** 
					 * 	 HFL 
					 **************************************/
					
					if (ProcessID.equals(YmConstant.PROCESS_ID_1)){   
						/*****  보급처리   ************/	
						ydSchCd 	= "3"+ydBayGp+"FE01UM" ;
						
//						//HFL 보급 요청시 입측 01BED 가 빈 상태일 경우만 스케줄 생성여부 기준을 확인하여 처리한다.
//						String sAPP053_HFL_YN = ymComm.BCoilApplyYn("APP053","3","HFL");   //HFL 보급요청시 01BED 비였을 때만 스케줄 생성 여부
//						commUtils.printLog(logId,  "==========[[[ APP053 HFL 보급요청시 01BED 비였을 때만 스케줄 생성 여부 : " + sAPP053_HFL_YN + " ]]]============", "SL");
//						
//						if ("Y".equals(sAPP053_HFL_YN)) {
//							jrParam.setField("PROC_GP"	, "H"); 
//							JDTORecordSet rsEqpTracking = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.bcoilHFLSPMInOutSdInqjl.getListCOILGPTracking_PIDEV", logId, methodNm, "HFL 트래킹 테이블 조회");
//							if(rsEqpTracking.size()>0) {
//								if(!"".equals(rsEqpTracking.getRecord(0).getFieldString("STOCK_ID"))) {
//									commUtils.printLog(logId, "<<<<< HFL 입측 01 BED 에 " + rsEqpTracking.getRecord(0).getFieldString("STOCK_ID") + " 코일이 존재하여 크레인 스케줄을 생성하지 않습니다! >>>>>" , "SL");
//									return jrRtn;
//								}
//							}
//						}
						
					}else if (ProcessID.equals(YmConstant.PROCESS_ID_3)){
						/*****  추출처리   ************/
						ydSchCd 	= "3"+ydBayGp+"FE01LM" ;
						ydStackLayer = "01";
					}else if (ProcessID.equals(YmConstant.PROCESS_ID_5)){
						/*****  HFL Take In처리   ************/
						ydSchCd 	= "3"+ydBayGp+"FE03UM" ;
					}
					
				} else if (WorkID.equals(YmConstant.WORK_HFL_S)){
					/*************************************** 
					 * 	 HFL 결속대
					 **************************************/						
					if (ProcessID.equals(YmConstant.PROCESS_ID_1)){                          
						/*****  보급처리   ************/
						ydSchCd 	= "3"+ydBayGp+"HS01UM" ;
						toLocGuide  = "3"+ydBayGp+"HS01" ;
					}else if (ProcessID.equals(YmConstant.PROCESS_ID_3)){
						
//						TmpstackCol	= "3"+ydBayGp+"HS01";  //3DHS01
//						ydStackColGp= TmpstackCol;
//						ydGp 		= TmpstackCol.substring(0,1);	// 이동할 야드 구분
//						ydBayGp 	= TmpstackCol.substring(1,2);	// 이동할 동 구분

						/*****  추출처리   ************/						
						ydSchCd 	= "3"+ydBayGp+"HS01LM" ;
						ydStackLayer = "01";
					}
					
				}  
				if(ydSchCd.equals("")){
    				throw new Exception("스케쥴 코드 생성 실패"); 				
    			}
				
				/*************************************** 
				 * 	1.작업예약이 생성 
                 *  2.STOCK UPDATE(작업행선)  
                 *  3.스케쥴 호출
				 **************************************/	
				//작업예약,작업재료 등록		
    			JDTORecord jrOutTemp = JDTORecordFactory.getInstance().create();
    			jrOutTemp.setResultCode(logId);		//Log ID
    			jrOutTemp.setResultMsg(methodNm);	//Log Method Name
    			
    			//jrOutTemp.setField("STL_NO"           , commUtils.trim(jrOutTemp.getFieldString("STOCK_ID"))); //재료번호
    			jrOutTemp.setField("STACK_COL_GP"     , ydStackColGp  	); 
    			jrOutTemp.setField("STACK_BED_GP"     , ydStackBedGp  	); 
    			jrOutTemp.setField("STACK_LAYER_GP"   , ydStackLayer 	); 
    			jrOutTemp.setField("YD_SCH_CD"        , ydSchCd   		);
    			jrOutTemp.setField("MODIFIER"         , modifier    	); 	//수정자
    			jrOutTemp.setField("YD_TO_LOC_GUIDE"  , toLocGuide   	); 	//TO위치가이드
    			
    			String ydWbookId = ymComm.procWkBookInsert(jrOutTemp);
    			
    			if(ydWbookId.equals(YmConstant.RETN_CD_FAILURE)){
    				
    				commUtils.printLog(logId, "작업예약ID 생성 실패", "SL");
    				return jrRtn;				
    				//throw new Exception("작업예약ID 생성 실패"); 				
    			}
				
				/*************************************** 
				 * 	1.저장품 수정
                 *  2.STOCK UPDATE(작업행선)  
                 *  3.스케쥴 호출
				 **************************************/
				// 저장품 Table(TB_YM_STOCK)에 작업예약ID,저장품이동조건을 Update 한다.	
				
				String sStackMoveTerm = "";
				
				if (WorkID.equals(YmConstant.WORK_HFL_S)){
					// HFL결속대
					if(ProcessID.equals(YmConstant.PROCESS_ID_3)){
						// HFL결속대  대한 코드는 고정으로 적용한다.
						sStackMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_A7; //HFL 결속대 추출 							
					}else{
						sStackMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_CC; //HFL 결속대 보급
					}

				} else if ( CoilNo.substring(0, 1).equals("S") ) {
					// Scrap에 대한 코드는 고정으로 적용한다.
					sStackMoveTerm =  YmConstant.NEW_STOCK_MOVE_TERM_A2;
					
				} else {
					
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name

					jrParam.setField("STOCK_ID"	, CoilNo);   //
					jrParam.setField("MODIFIER" , modifier); //수정자

					JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
					
					if(jrRtnProg.size() > 0 ) {
						sStackMoveTerm = commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"));
					}
					
				}
								
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("MODIFIER" 		, modifier);    	//수정자
				jrParam.setField("WBOOK_ID" 		, ydWbookId); 		//작업예약
				jrParam.setField("PROCESS_ID" 		, ProcessID);     	//처리구분
				jrParam.setField("STOCK_MOVE_TERM" 	, sStackMoveTerm);  //저장품이동조건
				jrParam.setField("STOCK_ID"			, CoilNo);   		//
				
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo1 
				UPDATE TB_YM_STOCK
				   SET MODIFIER   = :V_MODIFIER
				     , MOD_DDTT   = SYSDATE 
				     , SHEAR_SUPPLY_GP = CASE WHEN :V_PROCESS_ID = 1 THEN ''
				                              ELSE SHEAR_SUPPLY_GP          END   -- 정정보급구분 
				     , SHEAR_SUPPLY_DEMAND_DDTT = CASE WHEN :V_PROCESS_ID = 1 THEN ''
				                              ELSE SHEAR_SUPPLY_DEMAND_DDTT END --정정보급요구일시
				     , WBOOK_ID = :V_WBOOK_ID                              
				     , STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM  --저장품이동조건
				 WHERE STOCK_ID = :V_STOCK_ID
				*/ 
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo1", logId, methodNm, "TB_YM_STOCK 수정");
				
				
				String ydCrnSchChk	= "Y"; //스케줄기동 여부
				
				// HFL2 결속장 보급 추출인 경우 크레인 스케쥴이 존재 안하는 경우에만 스케쥴을 생성한다.
				if("A7".equals(sStackMoveTerm)||"CC".equals(sStackMoveTerm)){
				
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("YD_SCH_CD"        , ydSchCd   );// 자동이적 
					JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectCrnSchChk", logId, methodNm, "TB_YM_CRNSCH 정보");

					if (jsCrnSch.size() > 0) {
						jsCrnSch.first();
						JDTORecord jrCrnSch = jsCrnSch.getRecord();
						ydCrnSchChk	= commUtils.nvl(jrCrnSch.getFieldString("CHK"), "Y");			
			    	} 	 
				}
				
				/***********************************************************************
				 * HFL/SPM 보급시 해당 스케줄 자동기동 여부가 N이면 스케줄 기동하지 않음 
				 ***********************************************************************/
				/*
				SELECT *
				  FROM TB_YM_SCHEDULERULE
				 WHERE YD_SCH_CD = :V_YD_SCH_CD
				 */
				jrParam.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드
				JDTORecordSet jrSchInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchCdInfo", logId, methodNm, "TB_YM_SCHEDULERULE 조회");
				String sYD_SCH_AUTO_ST_YN = jrSchInfo.getRecord(0).getFieldString("YD_SCH_AUTO_ST_YN");
				
				if ("N".equals(sYD_SCH_AUTO_ST_YN)) {
					ydCrnSchChk = "N";
				}
				
				if("Y".equals(ydCrnSchChk)){
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
	
					jrRtn = commUtils.addSndData(ymComm.getCrnSchMsg(jrYdMsg));	
				
				}
					
				commUtils.printLog(logId, methodNm, "S-");
				
			}	
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
	 *  SPM2 Take-Out
	 * param YardID : 야드구분
	 * param WorkID : S SPM, H HFL
	 * param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * param CoilNo
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public JDTORecord callSPM2TakeOut(JDTORecord rcvMsg)throws DAOException {
		String methodNm = "SPM2 Take-Out[BCoilL3RcvSeEJB.callSPM2TakeOut] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		boolean isSuccess = false;

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String WorkId 		= commUtils.trim(rcvMsg.getFieldString("WORKID"));			// 공정코드 S:SPM,H:HFL,D:결속대   // 2SPM  처리
			String procId 		= commUtils.trim(rcvMsg.getFieldString("PROCESSID"));		// 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
			String CoilNo 		= commUtils.trim(rcvMsg.getFieldString("COILNO"));			// 'S' Scrap  'H' A열연  'K' B열연 
			String position 	= commUtils.trim(rcvMsg.getFieldString("POSITION"));		// 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STOCK_ID"	, CoilNo        ); //
			
			/*************************************** 
			 * 	작업예약이 존재하면 ERROR
			 **************************************/	

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookYN 
			SELECT DECODE(COUNT(*),0,'N','Y') AS WB_STL_YN --작업예약재료여부
			  FROM TB_YM_WRKBOOKMTL WM
			     , TB_YM_WRKBOOK    WB
			WHERE WM.YD_WBOOK_ID    = WB.YD_WBOOK_ID
			  AND WM.STOCK_ID       = :V_STOCK_ID
			  AND WM.DEL_YN         = 'N'
			  AND WB.DEL_YN         = 'N'
			*/ 	  
			JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookYN", logId, methodNm, "작업예약 등록여부");

			if (jsChk2 != null && jsChk2.size() > 0) {
				if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN"))) {
					commUtils.printLog(logId, "작업요구한 CoilNo이 이미 작업예약되어 있슴  Error : "+ CoilNo, "SL");
					return jrRtn;	
				}
			}	
			
			// 기존 위치 삭제
	   		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStackLayer
	   		UPDATE TB_YM_STACKLAYER
	   		   SET STOCK_ID         = NULL
	   		     , STACK_LAYER_STAT = 'E'
	   		     , MODIFIER = :V_MODIFIER
	   		     , MOD_DDTT = SYSDATE
	   		 WHERE STOCK_ID = :V_STOCK_ID
	   		   AND SUBSTR(STACK_COL_GP,1,1) = '3'
	   		 */  
	   		commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStackLayer", logId, methodNm, "기존 위치 삭제");
			
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTrkStackColGplayerTakeOut 
			MERGE INTO TB_YM_STACKLAYER SL USING (
			    SELECT CASE WHEN PROC_GP = 'K' THEN --SPM
			                CASE WHEN BAY_GP = 'C' AND STACK_BED_GP IN ('01','02')  THEN '3CKE01'   --SPM
			                     ELSE '3BKE01' END
			           WHEN PROC_GP = 'H' THEN --HFL
			                CASE WHEN BAY_GP = 'A' AND STACK_BED_GP IN ('01','02')  THEN '3AFE01'   --HFL
			                     ELSE '3BFE01' END
			           WHEN PROC_GP = 'P' THEN --SPM2
			                CASE WHEN BAY_GP = 'D' AND STACK_BED_GP IN ('01','02')  THEN '3DKE01'   --HFL
			                     ELSE '3EKE01' END
			           END  AS   STACK_COL_GP   
			         , '3'           AS   STACK_COL_GP1       
			         , CASE WHEN PROC_GP = 'K' THEN --SPM
			                CASE WHEN BAY_GP = 'C' AND STACK_BED_GP IN ('01','02')  THEN 'C'   --SPM
			                     ELSE 'B' END
			           WHEN PROC_GP = 'H' THEN --HFL
			                CASE WHEN BAY_GP = 'A' AND STACK_BED_GP IN ('01','02')  THEN 'A'   --HFL
			                     ELSE 'B' END
			           WHEN PROC_GP = 'P' THEN --SPM2
			                CASE WHEN BAY_GP = 'D' AND STACK_BED_GP IN ('01','02')  THEN 'D'   --HFL
			                     ELSE 'E' END
			           END  AS   STACK_COL_GP2
			         , '00'          AS   STACK_BED_GP
			         , '01'          AS   STACK_LAYER_GP
			         , 'E'           AS   STACK_LAYER_ACTIVE_STAT 
			         , 'C'           AS   STACK_LAYER_STAT
			         , STL_NO        AS   STOCK_ID
			      FROM TB_YM_EQPTRACKING  A 
			     WHERE STL_NO = :V_STOCK_ID 
			 ) DD ON (SL.STACK_COL_GP = DD.STACK_COL_GP AND SL.STACK_BED_GP   = DD.STACK_BED_GP
			                                           AND SL.STACK_LAYER_GP = DD.STACK_LAYER_GP)
			WHEN MATCHED THEN UPDATE SET
			     SL.MODIFIER                = :V_MODIFIER
			    ,SL.MOD_DDTT                = SYSDATE
			    ,SL.STACK_LAYER_ACTIVE_STAT = DD.STACK_LAYER_ACTIVE_STAT
			    ,SL.STACK_LAYER_STAT        = DD.STACK_LAYER_STAT
			    ,SL.STOCK_ID                = DD.STOCK_ID 
			*/    
			
			int updCnt = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTrkStackColGplayerTakeOut", logId, methodNm, "TRACKING LAYER 등록");
			
			if(updCnt == 0 ) {
				commUtils.printLog(logId, "적치단(TB_YM_STACKLAYER) Table Read Error : "+ CoilNo, "SL");
				throw new Exception("적치단(TB_YM_STACKLAYER) Table Read Error : "+ CoilNo);
				//return jrRtn;
			}			
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
			JDTORecordSet jsStackLayer = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1", logId, methodNm, "TB_YM_STACKLAYER 정보");
			if (jsStackLayer.size() < 1) {
				commUtils.printLog(logId, "StackColGp 이상: "+ CoilNo, "SL");
				return jrRtn;				
	    	} 			

			jsStackLayer.first();
			JDTORecord jrStackLayer = jsStackLayer.getRecord();
			String ydStackColGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP"),     "");
			String ydGp			= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP1"),    "");
			String ydBayGp		= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP2"),    "");
			String ydStackBedGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_BED_GP"),     "");
			String ydStackLayer	= commUtils.nvl(jrStackLayer.getFieldString("STACK_LAYER_GP"),   "");
			String stackStat	= commUtils.nvl(jrStackLayer.getFieldString("STACK_LAYER_STAT"), "");
			String TmpEquip 	= ydStackColGp.substring(2,4);
			
			String ydSchCd 	= "3"+ydBayGp+"KE04LM" ;
			
	    	/*
	    	 * Take-Out 요구가 왔을때 수신한 Coil이 Take-Out이 왔는데 Coil이 야드 Skid에 있다면 Error 
			 */
			
			
	    	if (WorkId.equals(YmConstant.WORK_SPM_N)){
				if (ydStackColGp.equals(YmConstant.STACK_COL_GP_3DKE01) ||
					ydStackColGp.equals(YmConstant.STACK_COL_GP_3EKE01) ||
					ydStackColGp.equals(YmConstant.STACK_COL_GP_3EKD01)){ 
			    }else{ 
					return jrRtn;
			    }
    		}
	 
			/*************************************** 
			 * 	1.작업예약이 생성 
             *  2.STOCK UPDATE(작업행선)  
             *  3.스케쥴 호출
			 **************************************/					
  			//작업예약,작업재료 등록		
			JDTORecord jrOutTemp = JDTORecordFactory.getInstance().create();
			jrOutTemp.setField("STL_NO"           , commUtils.trim(jrOutTemp.getFieldString("STOCK_ID"))); //재료번호
			jrOutTemp.setField("STACK_COL_GP"     , ydStackColGp  ); 
			jrOutTemp.setField("STACK_BED_GP"     , ydStackBedGp  ); 
			jrOutTemp.setField("STACK_LAYER_GP"   , ydStackLayer ); 
			jrOutTemp.setField("YD_SCH_CD"        , ydSchCd   );// 자동이적 
			jrOutTemp.setField("MODIFIER"         , modifier    ); //수정자
//			jrOutTemp.setField("YD_TO_LOC_GUIDE"  , toLocGuide    ); //TO위치가이드
			
			String ydWbookId = ymComm.procWkBookInsert(jrOutTemp);
			
			if(ydWbookId.equals(YmConstant.RETN_CD_FAILURE)){
				throw new Exception("작업예약ID 생성 실패"); 				
			}
			
			
			/*************************************** 
			 * 	1.저장품 수정
             *  2.STOCK UPDATE(작업행선)  
             *  3.스케쥴 호출
			 **************************************/
			// 저장품 Table(TB_YM_STOCK)에 작업예약ID,저장품이동조건을 Update 한다.	
			
			String sStackMoveTerm = "";
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("STOCK_ID"	, CoilNo);   //
			jrParam.setField("MODIFIER" , modifier); //수정자

			JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
			
			if(jrRtnProg.size() > 0 ) {
				sStackMoveTerm = commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"));
			}
							
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER" 		, modifier);    	//수정자
			jrParam.setField("WBOOK_ID" 		, ydWbookId); 		//작업예약
			jrParam.setField("PROCESS_ID" 		, procId);     	    //처리구분
			jrParam.setField("WORK_ID" 			, WorkId);     	    //공정구분
			jrParam.setField("STOCK_MOVE_TERM" 	, sStackMoveTerm);  //저장품이동조건
			jrParam.setField("STOCK_ID"			, CoilNo);   		//
			
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo1 
			UPDATE TB_YM_STOCK
			   SET MODIFIER   = :V_MODIFIER
			     , MOD_DDTT   = SYSDATE 
			     , SHEAR_SUPPLY_GP          = CASE WHEN :V_PROCESS_ID = 1 THEN ''
			                                  ELSE SHEAR_SUPPLY_GP          END   -- 정정보급구분 
			     , SHEAR_SUPPLY_DEMAND_DDTT = CASE WHEN :V_PROCESS_ID = 1 THEN ''
			                                  ELSE SHEAR_SUPPLY_DEMAND_DDTT          END   --정정보급요구일시
			     , WBOOK_ID = :V_WBOOK_ID                              
			     , STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM  --저장품이동조건
			 WHERE STOCK_ID = :V_STOCK_ID   
			*/  
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo1", logId, methodNm, "TB_YM_STOCK 수정");
			
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

			jrRtn = commUtils.addSndData(ymComm.getCrnSchMsg(jrYdMsg));				
				
			commUtils.printLog(logId, methodNm, "S-");	    	
			

		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
		    return jrRtn;
	}		
	
	
//
//	
//	/**
//	 * 오퍼레이션명 : 
//	 *
//	 *  SPM2 Take-Out
//	 * param YardID : 야드구분
//	 * param WorkID : S SPM, H HFL
//	 * param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
//	 * param CoilNo
//	 * 
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param 
//	 * @return
//	 * @throws 
//	 */ 
//	public JDTORecord callSPM2TakeOut(JDTORecord rcvMsg)throws DAOException {
//		String methodNm = "SPM2 Take-Out[BCoilL3RcvSeEJB.callSPM2TakeOut] < " + rcvMsg.getResultMsg();
//		String logId = rcvMsg.getResultCode();
//		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
//		boolean isSuccess = false;
//
//		try {
//			commUtils.printLog(logId, methodNm, "S+");
//
//			//수신 항목 값
//			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String WorkId 		= commUtils.trim(rcvMsg.getFieldString("WORKID"));			// 공정코드 S:SPM,H:HFL,D:결속대   // 2SPM  처리
//			String procId 		= commUtils.trim(rcvMsg.getFieldString("PROCESSID"));		// 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
//			String CoilNo 		= commUtils.trim(rcvMsg.getFieldString("COILNO"));			// 'S' Scrap  'H' A열연  'K' B열연 
//			String position 	= commUtils.trim(rcvMsg.getFieldString("POSITION"));		// 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
//			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
//			if ("".equals(modifier)) { modifier = msgId; }
//
//			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
//			jrParam.setResultCode(logId);	//Log ID
//			jrParam.setResultMsg(methodNm);	//Log Method Name
//			jrParam.setField("STOCK_ID"	, CoilNo        ); //
//			
//			/*************************************** 
//			 * 	작업예약이 존재하면 ERROR
//			 **************************************/	
//
//			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookYN 
//			SELECT DECODE(COUNT(*),0,'N','Y') AS WB_STL_YN --작업예약재료여부
//			  FROM TB_YM_WRKBOOKMTL WM
//			     , TB_YM_WRKBOOK    WB
//			WHERE WM.YD_WBOOK_ID    = WB.YD_WBOOK_ID
//			  AND WM.STOCK_ID       = :V_STOCK_ID
//			  AND WM.DEL_YN         = 'N'
//			  AND WB.DEL_YN         = 'N'
//			*/ 	  
//			JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookYN", logId, methodNm, "작업예약 등록여부");
//
//			if (jsChk2 != null && jsChk2.size() > 0) {
//				if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN"))) {
//					commUtils.printLog(logId, "작업요구한 CoilNo이 이미 작업예약되어 있슴  Error : "+ CoilNo, "SL");
//					return jrRtn;	
//				}
//			}	
//			
//			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTrkStackColGplayerTakeOut 
//			MERGE INTO TB_YM_STACKLAYER SL USING (
//			    SELECT CASE WHEN PROC_GP = 'K' THEN --SPM
//			                CASE WHEN BAY_GP = 'C' AND STACK_BED_GP IN ('01','02')  THEN '3CKE01'   --SPM
//			                     ELSE '3BKE01' END
//			           WHEN PROC_GP = 'H' THEN --HFL
//			                CASE WHEN BAY_GP = 'A' AND STACK_BED_GP IN ('01','02')  THEN '3AFE01'   --HFL
//			                     ELSE '3BFE01' END
//			           WHEN PROC_GP = 'P' THEN --SPM2
//			                CASE WHEN BAY_GP = 'D' AND STACK_BED_GP IN ('01','02')  THEN '3DKE01'   --HFL
//			                     ELSE '3EKE01' END
//			           END  AS   STACK_COL_GP   
//			         , '3'           AS   STACK_COL_GP1       
//			         , CASE WHEN PROC_GP = 'K' THEN --SPM
//			                CASE WHEN BAY_GP = 'C' AND STACK_BED_GP IN ('01','02')  THEN 'C'   --SPM
//			                     ELSE 'B' END
//			           WHEN PROC_GP = 'H' THEN --HFL
//			                CASE WHEN BAY_GP = 'A' AND STACK_BED_GP IN ('01','02')  THEN 'A'   --HFL
//			                     ELSE 'B' END
//			           WHEN PROC_GP = 'P' THEN --SPM2
//			                CASE WHEN BAY_GP = 'D' AND STACK_BED_GP IN ('01','02')  THEN 'D'   --HFL
//			                     ELSE 'E' END
//			           END  AS   STACK_COL_GP2
//			         , '00'          AS   STACK_BED_GP
//			         , '01'          AS   STACK_LAYER_GP
//			         , 'E'           AS   STACK_LAYER_ACTIVE_STAT 
//			         , 'C'           AS   STACK_LAYER_STAT
//			         , STL_NO        AS   STOCK_ID
//			      FROM TB_YM_EQPTRACKING  A 
//			     WHERE STL_NO = :V_STOCK_ID 
//			 ) DD ON (SL.STACK_COL_GP = DD.STACK_COL_GP AND SL.STACK_BED_GP   = DD.STACK_BED_GP
//			                                           AND SL.STACK_LAYER_GP = DD.STACK_LAYER_GP)
//			WHEN MATCHED THEN UPDATE SET
//			     SL.MODIFIER                = :V_MODIFIER
//			    ,SL.MOD_DDTT                = SYSDATE
//			    ,SL.STACK_LAYER_ACTIVE_STAT = DD.STACK_LAYER_ACTIVE_STAT
//			    ,SL.STACK_LAYER_STAT        = DD.STACK_LAYER_STAT
//			    ,SL.STOCK_ID                = DD.STOCK_ID 
//			*/    
//			
//			int updCnt = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTrkStackColGplayerTakeOut", logId, methodNm, "TRACKING LAYER 등록");
//			
//			if(updCnt == 0 ) {
//				commUtils.printLog(logId, "적치단(TB_YM_STACKLAYER) Table Read Error : "+ CoilNo, "SL");
//				return jrRtn;	
//			}			
//	    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1 
//			-- STACK_COL_GP(적치열:첫번째자리 야드구분, 두번째 자리 동구분), STACK_LAYER_STAT(적치단 상태 L:적치중)
//			SELECT STACK_COL_GP
//			     , SUBSTR(STACK_COL_GP,1,1) STACK_COL_GP1
//			     , SUBSTR(STACK_COL_GP,2,1) STACK_COL_GP2
//			     , STACK_BED_GP
//			     , STACK_LAYER_STAT
//			     , STACK_LAYER_GP
//			  FROM TB_YM_STACKLAYER  A
//			WHERE STOCK_ID = :V_STOCK_ID
//	    	*/	
//			JDTORecordSet jsStackLayer = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1", logId, methodNm, "TB_YM_STACKLAYER 정보");
//			if (jsStackLayer.size() < 1) {
//				commUtils.printLog(logId, "StackColGp 이상: "+ CoilNo, "SL");
//				return jrRtn;				
//	    	} 			
//
//			jsStackLayer.first();
//			JDTORecord jrStackLayer = jsStackLayer.getRecord();
//			String ydStackColGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP"),     "");
//			String ydGp			= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP1"),    "");
//			String ydBayGp		= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP2"),    "");
//			String ydStackBedGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_BED_GP"),     "");
//			String ydStackLayer	= commUtils.nvl(jrStackLayer.getFieldString("STACK_LAYER_GP"),   "");
//			String stackStat	= commUtils.nvl(jrStackLayer.getFieldString("STACK_LAYER_STAT"), "");
//			String TmpEquip 	= ydStackColGp.substring(2,4);
//			
//			String ydSchCd 	= "3"+ydBayGp+"KE03LM" ;
//			
//	    	/*
//	    	 * Take-Out 요구가 왔을때 수신한 Coil이 Take-Out이 왔는데 Coil이 야드 Skid에 있다면 Error 
//			 */
//			
//			
//	    	if (WorkId.equals(YmConstant.WORK_SPM_N)){
//				if (ydStackColGp.equals(YmConstant.STACK_COL_GP_3DKE01) ||
//					ydStackColGp.equals(YmConstant.STACK_COL_GP_3EKE01) ||
//					ydStackColGp.equals(YmConstant.STACK_COL_GP_3EKD01)){ 
//			    }else{ 
//					return jrRtn;
//			    }
//    		}
//	 
//			/*************************************** 
//			 * 	1.작업예약이 생성 
//             *  2.STOCK UPDATE(작업행선)  
//             *  3.스케쥴 호출
//			 **************************************/					
//  			//작업예약,작업재료 등록		
//			JDTORecord jrOutTemp = JDTORecordFactory.getInstance().create();
//			jrOutTemp.setField("STL_NO"           , commUtils.trim(jrOutTemp.getFieldString("STOCK_ID"))); //재료번호
//			jrOutTemp.setField("STACK_COL_GP"     , ydStackColGp  ); 
//			jrOutTemp.setField("STACK_BED_GP"     , ydStackBedGp  ); 
//			jrOutTemp.setField("STACK_LAYER_GP"   , ydStackLayer ); 
//			jrOutTemp.setField("YD_SCH_CD"        , ydSchCd   );// 자동이적 
//			jrOutTemp.setField("MODIFIER"         , modifier    ); //수정자
////			jrOutTemp.setField("YD_TO_LOC_GUIDE"  , toLocGuide    ); //TO위치가이드
//			
//			String ydWbookId = ymComm.procWkBookInsert(jrOutTemp);
//			
//			if(ydWbookId.equals(YmConstant.RETN_CD_FAILURE)){
//				throw new Exception("작업예약ID 생성 실패"); 				
//			}
//			
//			
//			/*************************************** 
//			 * 	1.저장품 수정
//             *  2.STOCK UPDATE(작업행선)  
//             *  3.스케쥴 호출
//			 **************************************/
//			// 저장품 Table(TB_YM_STOCK)에 작업예약ID,저장품이동조건을 Update 한다.	
//			
//			String sStackMoveTerm = "";
//			jrParam = JDTORecordFactory.getInstance().create();
//			jrParam.setResultCode(logId);	//Log ID
//			jrParam.setResultMsg(methodNm);	//Log Method Name
//
//			jrParam.setField("STOCK_ID"	, CoilNo);   //
//			jrParam.setField("MODIFIER" , modifier); //수정자
//
//			JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
//			
//			if(jrRtnProg.size() > 0 ) {
//				sStackMoveTerm = commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"));
//			}
//							
//			jrParam = JDTORecordFactory.getInstance().create();
//			jrParam.setResultCode(logId);	//Log ID
//			jrParam.setResultMsg(methodNm);	//Log Method Name
//			jrParam.setField("MODIFIER" 		, modifier);    	//수정자
//			jrParam.setField("WBOOK_ID" 		, ydWbookId); 		//작업예약
//			jrParam.setField("PROCESS_ID" 		, procId);     	    //처리구분
//			jrParam.setField("WORK_ID" 			, WorkId);     	    //공정구분
//			jrParam.setField("STOCK_MOVE_TERM" 	, sStackMoveTerm);  //저장품이동조건
//			jrParam.setField("STOCK_ID"			, CoilNo);   		//
//			
//			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo1 
//			UPDATE TB_YM_STOCK
//			   SET MODIFIER   = :V_MODIFIER
//			     , MOD_DDTT   = SYSDATE 
//			     , SHEAR_SUPPLY_GP          = CASE WHEN :V_PROCESS_ID = 1 THEN ''
//			                                  ELSE SHEAR_SUPPLY_GP          END   -- 정정보급구분 
//			     , SHEAR_SUPPLY_DEMAND_DDTT = CASE WHEN :V_PROCESS_ID = 1 THEN ''
//			                                  ELSE SHEAR_SUPPLY_DEMAND_DDTT          END   --정정보급요구일시
//			     , WBOOK_ID = :V_WBOOK_ID                              
//			     , STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM  --저장품이동조건
//			 WHERE STOCK_ID = :V_STOCK_ID   
//			*/  
//			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo1", logId, methodNm, "TB_YM_STOCK 수정");
//			
//			/**********************************************************
//			* 2.2 크레인스케줄 전문 호출
//			**********************************************************/
//			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//			jrYdMsg.setResultCode(logId);	//Log ID
//			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
//
//			jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
//			jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
//			jrYdMsg.setField("YD_SCH_ST_GP" , "O"); //야드스케쥴기동구분
//			jrYdMsg.setField("YD_SCH_REQ_GP", "L"      ); //야드스케쥴요청구분(인출)
//			jrYdMsg.setField("MODIFIER"     , modifier ); //수정자
//
//			jrRtn = commUtils.addSndData(ymComm.getCrnSchMsg(jrYdMsg));				
//				
//			commUtils.printLog(logId, methodNm, "S-");	    	
//			
////			String TmpstackCol = "";
////			String ydSchCd     = "";
////			if (ydStackColGp != null && !ydStackColGp.equals("")){
////				if (WorkId.equals(YmConstant.WORK_SPM_N)){
////					if (ydStackColGp.equals(YmConstant.STACK_COL_GP_3DKE01)){   //B열연 SPM2 입측
////						/*
////						 * 2006.12.06 SPM 5,6,7 Positin Take-Out시  E동 C/R 에 작업 지시 
////						 */
////						if ( position.equals(YmConstant.WORK_SPM_5) || 
////							 position.equals(YmConstant.WORK_SPM_6) || 
////							 position.equals(YmConstant.WORK_SPM_7)){
////							
////							TmpstackCol	= YmConstant.STACK_COL_GP_3EKE01;
////							ydBayGp 	= YmConstant.BAY_GP_E;
////							
////							JDTORecord jrInRecord = JDTORecordFactory.getInstance().create();
////							jrInRecord.setField("STACK_COL_GP"			, TmpstackCol); 
////							jrInRecord.setField("STACK_BED_GP"			, "01");	 
////							jrInRecord.setField("STACK_LAYER_GP"		, "01");	 
////							jrInRecord.setField("STOCK_ID"				, CoilNo);	 
////							jrInRecord.setField("STACK_LAYER_STAT"		, "C");  //기존	 
////							
////							// "Scrap 적치 단의 상태를 'E'로 변경"
////							/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStackLayerStat 
////							UPDATE TB_YM_STACKLAYER            
////							   SET MOD_DDTT             = SYSDATE             
////							     , MODIFIER             = :V_MODIFIER             
////							     , STOCK_ID				= :V_STOCK_ID
////								 , STACK_LAYER_STAT	    = :V_STACK_LAYER_STAT
////							 WHERE STACK_COL_GP     = :V_STACK_COL_GP
////							   AND STACK_BED_GP     = :V_STACK_BED_GP
////							   AND STACK_LAYER_GP   = :V_STACK_LAYER_GP;
////							*/
////							commDao.update(jrInRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStackLayerStat", logId, methodNm, "TB_YM_STACKLAYER 등록 갱신");	
////
////							jrParam.setField("STACK_COL_GP"   	, ydStackColGp);
////							/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStacklayerConvClear 
////							UPDATE TB_YM_STACKLAYER
////							   SET MODIFIER   = NVL(:V_MODIFIER,'SYSTEM')
////							     , MOD_DDTT   = SYSDATE           
////							     , STOCK_ID                = NULL
////							     , STACK_LAYER_ACTIVE_STAT = 'E'
////							     , STACK_LAYER_STAT        = 'E'
////							 WHERE STOCK_ID     = :V_STOCK_ID
////							   AND STACK_COL_GP NOT IN (:V_STACK_COL_GP)
////							*/      
////							commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStacklayerConvClear", logId, methodNm, "해당열을 제외한 모든 위치 CLEAR");								
////							
////						}
////					} 
////					ydSchCd 	= "3"+ydBayGp+"KE03LM" ;
////					
////				}else if (WorkId.equals(YmConstant.WORK_HFL_H)){
////						if (ydStackColGp.equals(YmConstant.STACK_COL_GP_3AFE01)){   //B열연 HFL 입측
////							
////							if (position.equals(YmConstant.WORK_SPM_5)){
////								
////								JDTORecord jrInRecord = JDTORecordFactory.getInstance().create();
////								jrInRecord.setField("STACK_COL_GP"			, TmpstackCol); 
////								jrInRecord.setField("STACK_BED_GP"			, "01");	 
////								jrInRecord.setField("STACK_LAYER_GP"		, "01");	 
////								jrInRecord.setField("STOCK_ID"				, CoilNo);	 
////								jrInRecord.setField("STACK_LAYER_STAT"		, "C");  //기존	 
////								
////								// "Scrap 적치 단의 상태를 'E'로 변경"
////								/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStackLayerStat 
////								UPDATE TB_YM_STACKLAYER            
////								   SET MOD_DDTT             = SYSDATE             
////								     , MODIFIER             = :V_MODIFIER             
////								     , STOCK_ID				= :V_STOCK_ID
////									 , STACK_LAYER_STAT	    = :V_STACK_LAYER_STAT
////								 WHERE STACK_COL_GP     = :V_STACK_COL_GP
////								   AND STACK_BED_GP     = :V_STACK_BED_GP
////								   AND STACK_LAYER_GP   = :V_STACK_LAYER_GP;
////								*/
////								commDao.update(jrInRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStackLayerStat", logId, methodNm, "크레인LAYOUT 등록 갱신");	
////
////								jrParam.setField("STACK_COL_GP"   	, ydStackColGp);
////								/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStacklayerConvClear 
////								UPDATE TB_YM_STACKLAYER
////								   SET MODIFIER   = NVL(:V_MODIFIER,'SYSTEM')
////								     , MOD_DDTT   = SYSDATE           
////								     , STOCK_ID                = NULL
////								     , STACK_LAYER_ACTIVE_STAT = 'E'
////								     , STACK_LAYER_STAT        = 'E'
////								 WHERE STOCK_ID     = :V_STOCK_ID
////								   AND STACK_COL_GP NOT IN (:V_STACK_COL_GP)
////								*/      
////								commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStacklayerConvClear", logId, methodNm, "해당열 저장품삭제");								
////							}
////					}	
////					ydSchCd 	= "3"+ydBayGp+"FE03LM" ;
//
//		}catch(DAOException daoe){
//	        throw daoe;
//	    }catch(Exception e){
//	        throw new EJBServiceException(e);
//	    }
//		    return jrRtn;
//	}		


	/**
	 * STACKLAYER 에 위치등록
	 * @param YardID : 야드구분
	 * @param WorkID : S SPM, H HFL
	 * @param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * @param CoilNo
	 * @return
	 */
	private boolean Spm2ProcessCheck(JDTORecord rcvMsg)throws DAOException {
		String methodNm = "코일 위치 정보 확인[BCoilL3RcvSeEJB.Spm2ProcessCheck] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		boolean isSuccess = false;
		JDTORecordSet jsStackLayer = JDTORecordFactory.getInstance().createRecordSet("");

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String WorkId 		= commUtils.trim(rcvMsg.getFieldString("WORKID"));			// 공정코드 S:SPM,H:HFL,D:결속대   // 2SPM 별도 처리
			String procId 		= commUtils.trim(rcvMsg.getFieldString("PROCESSID"));		// 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
			String CoilNo 		= commUtils.trim(rcvMsg.getFieldString("COILNO"));			// 'S' Scrap  'H' A열연  'K' B열연 
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

	    	if (CoilNo.equals("")){

				commUtils.printLog(logId, methodNm + "Coil No = Space Error" , "SL");	
				return false;
	    	}
	    	
	    	JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"  	, modifier  ); //수정자
			jrParam.setField("STOCK_ID" 	, CoilNo);
			jrParam.setField("PROCESSID" 	, procId);
			jrParam.setField("WORKID" 	    , WorkId);
			
			if (procId.equals("3")) {
				/*********************
				 * 추출이거나  TAKE OUT 인 경우
				 *  - 입측 클리어 작업 
				 *  - TB_YM_STACKLAYER 에 없는 경우 생성처리 한다.
				 ******************************************/
				// 기존 위치 삭제
		   		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStackLayer
		   		UPDATE TB_YM_STACKLAYER
		   		   SET STOCK_ID         = NULL
		   		     , STACK_LAYER_STAT = 'E'
		   		     , MODIFIER = :V_MODIFIER
		   		     , MOD_DDTT = SYSDATE
		   		 WHERE STOCK_ID = :V_STOCK_ID
		   		   AND SUBSTR(STACK_COL_GP,1,1) = '3'
		   		 */  
		   		commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStackLayer", logId, methodNm, "기존 위치 삭제");


				/*********************
				 * 추출인 경우 
				 * TB_YM_STACKLAYER 에 없는 경우 생성처리 한다.
				 ******************************************/
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTrkStackColGplayer 
				MERGE INTO TB_YM_STACKLAYER SL USING (
				    SELECT CASE WHEN :V_WORKID = 'S'  THEN '3AKD01'   --SPM
				                WHEN :V_WORKID = 'H'  THEN '3CFD01'   --HFL
				                WHEN :V_WORKID = 'N'  THEN '3EKD01'   --SPM2
				                END      AS   STACK_COL_GP
				         , '3'           AS   STACK_COL_GP1
				         , CASE WHEN :V_WORKID = 'S'  THEN 'A'   --SPM
				                WHEN :V_WORKID = 'H'  THEN 'C'   --HFL
				                WHEN :V_WORKID = 'N'  THEN 'E'   --SPM2
				                END      AS   STACK_COL_GP2
				         , '00'          AS   STACK_BED_GP
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
				int updCnt = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTrkStackColGplayer", logId, methodNm, "TRACKING LAYER 등록");
				
				if(updCnt == 0 ) {
					commUtils.printLog(logId, "적치단(TB_YM_STACKLAYER) Table Read Error : "+ CoilNo, "SL");
					return false;
				}
			} 			
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1 
			-- STACK_COL_GP(적치열:첫번째자리 야드구분, 두번째 자리 동구분), STACK_LAYER_STAT(적치단 상태 L:적치중)
			SELECT STACK_COL_GP
			     , SUBSTR(STACK_COL_GP,1,1) STACK_COL_GP1
			     , SUBSTR(STACK_COL_GP,2,1) STACK_COL_GP2
			     , STACK_BED_GP
			     , STACK_LAYER_STAT
			     , STACK_LAYER_GP
			     -- 스크랩 적용여부
			     , NVL((SELECT DTL_ITM1 FROM USRYMA.TB_YM_RULE WHERE REPR_CD_GP = 'APP022' AND ROWNUM = 1),'N') AS APP022_YN
			 FROM TB_YM_STACKLAYER  A
			WHERE STOCK_ID = :V_STOCK_ID
	    	 */	
	    	jsStackLayer = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1", logId, methodNm, "TB_YM_STACKLAYER 정보");
	    	
			if (jsStackLayer.size() == 0) {
				commUtils.printLog(logId, "적치단(TB_YM_STACKLAYER)에 해당 재료가 없습니다. : "+ CoilNo, "SL");
				return false;
			}
			
			jsStackLayer.first();
			JDTORecord jrStackLayer = jsStackLayer.getRecord();
			String ydStackColGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP"),     "");
			String ydBayGp		= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP2"),    "");
			String ydEquip 		= ydStackColGp.substring(2,4);   
			
    		if (WorkId.equals(YmConstant.WORK_SPM_N)){	// SPM2 
    			if (procId.equals(YmConstant.PROCESS_ID_1)){   
    				if (ydBayGp.equals(YmConstant.BAY_GP_D) || ydBayGp.equals(YmConstant.BAY_GP_E)){  
	    				if (ydEquip.equals(YmConstant.WORK_SPM_IN_KE) || ydEquip.equals(YmConstant.WORK_SPM_OUT_KD)){ 
	    					isSuccess = false; 
	    			    }else{ 
	    			    	isSuccess = true;   
	    			    }		    					
    				}
    			} else if (procId.equals(YmConstant.PROCESS_ID_3)){//SPM 추출    
    				
    				if (ydBayGp.equals(YmConstant.BAY_GP_E)){	// 동 구분 A동 || B동
    					/*
    					 * SPM 추출 요구시 
    					 * */
	    				if (ydEquip.equals(YmConstant.WORK_SPM_OUT_KD) ){ //SPM 출측
	    					isSuccess = true;  
	    			    }else{ 
	    			    	isSuccess = false;   
	    			    }
    				}else{
    					isSuccess = false;
    				}	    				
    			} else if (procId.equals(YmConstant.PROCESS_ID_5)){//SPM Take-In  : 적치열(3BKE01)
    				if (ydBayGp.equals(YmConstant.BAY_GP_E)){
	    				if (ydEquip.equals(YmConstant.WORK_SPM_IN_KE) || ydEquip.equals(YmConstant.WORK_SPM_OUT_KD)){ //SPM 출측
	    					isSuccess = false; 
	    			    }else{ 
	    			    	isSuccess = true;   
	    			    }		    					
    				}
    			} else if (procId.equals(YmConstant.PROCESS_ID_7)) { // SPM2 포장모드(7H) 보급
    				if (ydBayGp.equals(YmConstant.BAY_GP_E)){
	    				if (ydEquip.equals(YmConstant.WORK_SPM_OUT_KD)){ 
	    					isSuccess = false; 
	    			    }else{ 
	    			    	isSuccess = true;   
	    			    }		    					
    				}
    			}

    		} 
//    		// HFL
//    		else if (WorkId.equals(YmConstant.WORK_HFL_F)){
//    			if (procId.equals(YmConstant.PROCESS_ID_1)){      //HFL 보급            : 적치열(3AFE01)
//    				if (ydBayGp.equals(YmConstant.BAY_GP_D) || ydBayGp.equals(YmConstant.BAY_GP_E)){
//	    				if (ydEquip.equals(YmConstant.WORK_HFL_IN_FE) || ydEquip.equals(YmConstant.WORK_HFL_OUT_FD)){
//	    					isSuccess = false; 
//	    			    }else{ 
//	    			    	isSuccess = true;   
//	    			    }		    					
//    				}
//    			} else if (procId.equals(YmConstant.PROCESS_ID_3)){//HFL 추출  
//    				if (ydBayGp.equals(YmConstant.BAY_GP_E)){
//	    				if (ydEquip.equals(YmConstant.WORK_HFL_OUT_FD)){
//	    					isSuccess = true; 
//	    			    }else{ 
//	    			    	isSuccess = false;   
//	    			    }		    					
//    				}else{ 
//    			    	isSuccess = false; 	    					
//    				}
//    			}	
//    		}
	    	
    		commUtils.printLog(logId, methodNm, "S-");

	    	
	    	return isSuccess;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}

 	/**
	 * 오퍼레이션명 : SPM / HFL 보급, 추출, Take-In
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public JDTORecord callSPM2LineInOut(JDTORecord rcvMsg){	
		String methodNm = "SPM2  보급, 추출, Take-In[BCoilL3RcvSeEJB.callSPM2LineInOut] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String WorkID 		= commUtils.trim(rcvMsg.getFieldString("WORKID"));		// 공정코드 S:SPM,H:HFL,D:결속대   // 2SPM  처리
			String ProcessID 	= commUtils.trim(rcvMsg.getFieldString("PROCESSID"));	// 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In, 7 포장모드보급(출측1번지 3EKD02-01로 보급한다)
			String CoilNo 		= commUtils.trim(rcvMsg.getFieldString("COILNO"));		// 'S' Scrap  'H' A열연  'K' B열연 
			String position 	= commUtils.trim(rcvMsg.getFieldString("POSITION"));	// 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	// 수정자(Backup Only)
			
			String sTAKE_IN_GP  = commUtils.trim(rcvMsg.getFieldString("TAKE_IN_GP"));	// TAKE_IN 입측 출측 구분(화면 HFL/SPM 입출측조회 에만 존재하는 입력 값 TakeIn에서만 사용한다)
			//String WrapMdYn  	= commUtils.trim(rcvMsg.getFieldString("WRAPMDYN"));	// 포장모드구분(Y:포장모드(출측1번지 3EKD02-01로 보급한다))
			
			if ("".equals(modifier)) { modifier = msgId; }
			
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STOCK_ID"	, CoilNo        ); //
			
			/*************************************** 
			 * 	작업예약이 존재하면 ERROR
			 **************************************/	

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookYN 
			SELECT DECODE(COUNT(*),0,'N','Y') AS WB_STL_YN --작업예약재료여부
			  FROM TB_YM_WRKBOOKMTL WM
			     , TB_YM_WRKBOOK    WB
			WHERE WM.YD_WBOOK_ID    = WB.YD_WBOOK_ID
			  AND WM.STOCK_ID       = :V_STOCK_ID
			  AND WM.DEL_YN         = 'N'
			  AND WB.DEL_YN         = 'N'
			*/  	  
			JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookYN", logId, methodNm, "작업예약 등록여부");

			if (jsChk2 != null && jsChk2.size() > 0) {
				if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN"))) {
					commUtils.printLog(logId, "작업요구한 CoilNo이 이미 작업예약되어 있슴  Error : "+ CoilNo, "SL");
					return jrRtn;	
				}
			}	
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1 
			-- STACK_COL_GP(적치열:첫번째자리 야드구분, 두번째 자리 동구분), STACK_LAYER_STAT(적치단 상태 L:적치중)
			SELECT STACK_COL_GP
			     , SUBSTR(STACK_COL_GP,1,1) STACK_COL_GP1
			     , SUBSTR(STACK_COL_GP,2,1) STACK_COL_GP2
			     , STACK_BED_GP
			     , STACK_LAYER_STAT
			     , STACK_LAYER_GP
			     -- 스크랩 적용여부
			     , NVL((SELECT DTL_ITM1 FROM USRYMA.TB_YM_RULE WHERE REPR_CD_GP = 'APP022' AND ROWNUM = 1),'N') AS APP022_YN
			 FROM TB_YM_STACKLAYER  A
			WHERE STOCK_ID = :V_STOCK_ID
			*/	
			JDTORecordSet jsStackLayer = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1", logId, methodNm, "TB_YM_STACKLAYER 정보");

			if (jsStackLayer.size() < 1) {
				commUtils.printLog(logId, "StackColGp 이상: "+ CoilNo, "SL");
				return jrRtn;				
	    	} 
			
			jsStackLayer.first();
			JDTORecord jrStackLayer = jsStackLayer.getRecord();
			String ydStackColGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP"),     "");
			String ydGp			= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP1"),    "");
			String ydBayGp		= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP2"),    "");
			String ydStackBedGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_BED_GP"),     "");
			String ydStackLayer	= commUtils.nvl(jrStackLayer.getFieldString("STACK_LAYER_GP"),   "");
			String sAPP022_YN	= commUtils.nvl(jrStackLayer.getFieldString("APP022_YN"),        "N");
			String TmpstackCol 	= "";
			String ydSchCd 		= "";  //SCH_CD
			String toLocGuide 	= "";  //TO위치 가이드
			
			if (ydStackColGp != null && !ydStackColGp.equals("")){

				if (WorkID.equals(YmConstant.WORK_SPM_N)){  
					/*************************************** 
					 * 	 SPM2 작업
					 **************************************/				
							
					if (ProcessID.equals(YmConstant.PROCESS_ID_1)){ 		
						// 보급위치 : YD에서는 TO위치 1개만 관리, 보급요구가 오면 무조건 작업예약 생성
						ydSchCd 	= "3"+ydBayGp+"KE02UM" ;
						
//						//SPM2 보급 요청시 입측 01BED 가 빈 상태일 경우만 스케줄 생성여부 기준을 확인하여 처리한다.
//						String sAPP053_SPM2_YN = ymComm.BCoilApplyYn("APP053","3","SPM2");   //SPM 보급요청시 01BED 비였을 때만 스케줄 생성 여부
//						commUtils.printLog(logId,  "==========[[[ APP053 SPM2 보급요청시 01BED 비였을 때만 스케줄 생성 여부 : " + sAPP053_SPM2_YN + " ]]]============", "SL");
//						
//						if ("Y".equals(sAPP053_SPM2_YN)) {
//							jrParam.setField("PROC_GP"	, "P"); 
//							JDTORecordSet rsEqpTracking = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.bcoilHFLSPMInOutSdInqjl.getListCOILGPTracking_PIDEV", logId, methodNm, "SPM2 트래킹 테이블 조회");
//							if(rsEqpTracking.size()>0) {
//								if(!"".equals(rsEqpTracking.getRecord(0).getFieldString("STOCK_ID"))) {
//									commUtils.printLog(logId, "<<<<< SPM2 입측 01 BED 에 " + rsEqpTracking.getRecord(0).getFieldString("STOCK_ID") + " 코일이 존재하여 크레인 스케줄을 생성하지 않습니다! >>>>>" , "SL");
//									return jrRtn;
//								}
//							}
//						}
						

					}else if (ProcessID.equals(YmConstant.PROCESS_ID_3)){
						/*****  추출처리   ************/

						if (ydBayGp.equals(YmConstant.BAY_GP_B)){
							
							TmpstackCol   = YmConstant.STACK_COL_GP_3EKD02;  //3EKD01 -> 3EKD02
							
							ydStackColGp= TmpstackCol;
							ydGp      	= TmpstackCol.substring(0,1);	// 이동할 야드 구분
							ydBayGp   	= TmpstackCol.substring(1,2);	// 이동할 동 구분	
						}
						ydSchCd 	= "3"+ydBayGp+"KD02LM" ;
						// Scrap일 경우를 비교한다. 
						if (CoilNo.substring(0, 1).equals("S") ){								 // Scrap SPM 추출.
							if ("N".equals(sAPP022_YN) ){
								ydSchCd 	= "3ESP01LM" ;  // ERROR 처리
							} else {	
								ydSchCd 	= "3EKD02LM" ;
							}	
						}else{
						//	toLocGuide 	= "";
						}
						ydStackLayer = "01";
					/*****  Coil SPM Take In처리   ************/						
					} else if (ProcessID.equals(YmConstant.PROCESS_ID_5)){
						ydSchCd 	= "3"+ydBayGp+"KE04UM" ;
					} else if (ProcessID.equals(YmConstant.PROCESS_ID_7)){
						// E동 SPM2 포장모드 보급 
						if (ydBayGp.equals(YmConstant.BAY_GP_E)){
							ydSchCd 	= "3"+ydBayGp+"KE07UM" ;
						}
					}
				}  
				if(ydSchCd.equals("")){
    				throw new Exception("스케쥴 코드 생성 실패"); 				
    			}
				
				/*************************************** 
				 * 	1.작업예약이 생성 
                 *  2.STOCK UPDATE(작업행선)  
                 *  3.스케쥴 호출
				 **************************************/	
				//작업예약,작업재료 등록		
    			JDTORecord jrOutTemp = JDTORecordFactory.getInstance().create();
    			jrOutTemp.setField("STL_NO"           , commUtils.trim(jrOutTemp.getFieldString("STOCK_ID"))); //재료번호
    			jrOutTemp.setField("STACK_COL_GP"     , ydStackColGp  ); 
    			jrOutTemp.setField("STACK_BED_GP"     , ydStackBedGp  ); 
    			jrOutTemp.setField("STACK_LAYER_GP"   , ydStackLayer ); 
    			jrOutTemp.setField("YD_SCH_CD"        , ydSchCd   );// 자동이적 
    			jrOutTemp.setField("MODIFIER"         , modifier    ); //수정자
    			if ("E".equals(ydBayGp) && "7".equals(ProcessID)) {
    				jrOutTemp.setField("YD_TO_LOC_GUIDE"  , "3"+ydBayGp+"KD020101"); //E동 포장모드보급(7)이면 3EKD02-01 로 보급한다.
    			} else {
    				jrOutTemp.setField("YD_TO_LOC_GUIDE"  , toLocGuide    ); //TO위치가이드
    			}
    			
    			String ydWbookId = ymComm.procWkBookInsert(jrOutTemp);
    			
    			if(ydWbookId.equals(YmConstant.RETN_CD_FAILURE)){
    				throw new Exception("작업예약ID 생성 실패"); 				
    			}
    			
				/*************************************** 
				 * 	1.저장품 수정
                 *  2.STOCK UPDATE(작업행선)  
                 *  3.스케쥴 호출
				 **************************************/
				// 저장품 Table(TB_YM_STOCK)에 작업예약ID,저장품이동조건을 Update 한다.	
				
				String sStackMoveTerm = "";
				
				if (WorkID.equals(YmConstant.WORK_HFL_S)){
					if(ProcessID.equals(YmConstant.PROCESS_ID_3)){
						// HFL결속대  대한 코드는 고정으로 적용한다.
						sStackMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_A7; //HFL 결속대 추출 							
					}else{
						sStackMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_CC; //HFL 결속대 보급
					}

				} else if ( !CoilNo.substring(0, 1).equals("S") ) {
					// Scrap에 대한 코드는 고정으로 적용한다.
					sStackMoveTerm =  YmConstant.NEW_STOCK_MOVE_TERM_A2;
					
				} else {	
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name

					jrParam.setField("STOCK_ID"	, CoilNo);   //
					jrParam.setField("MODIFIER" , modifier); //수정자

					JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
					
					if(jrRtnProg.size() > 0 ) {
						sStackMoveTerm = commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"));
					}
				}
								
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("MODIFIER" 		, modifier);    	//수정자
				jrParam.setField("WBOOK_ID" 		, ydWbookId); 		//작업예약
				jrParam.setField("PROCESS_ID" 		, ProcessID);     	//처리구분
				jrParam.setField("STOCK_MOVE_TERM" 	, sStackMoveTerm);  //저장품이동조건
				jrParam.setField("STOCK_ID"			, CoilNo);   		//
				
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo1 
				UPDATE TB_YM_STOCK
				   SET MODIFIER   = :V_MODIFIER
				     , MOD_DDTT   = SYSDATE 
				     , SHEAR_SUPPLY_GP = CASE WHEN :V_PROCESS_ID = 1 THEN ''
				                              ELSE SHEAR_SUPPLY_GP          END   -- 정정보급구분 
				     , SHEAR_SUPPLY_DEMAND_DDTT = CASE WHEN :V_PROCESS_ID = 1 THEN ''
				                              ELSE SHEAR_SUPPLY_DEMAND_DDTT END --정정보급요구일시
				     , WBOOK_ID = :V_WBOOK_ID                              
				     , STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM  --저장품이동조건
				 WHERE STOCK_ID = :V_STOCK_ID
				*/ 
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo1", logId, methodNm, "TB_YM_STOCK 수정");
				
				/***********************************************************************
				 * 보급시 해당 스케줄 자동기동 여부가 N이면 스케줄 기동하지 않음 
				 ***********************************************************************/
				/*
				SELECT *
				  FROM TB_YM_SCHEDULERULE
				 WHERE YD_SCH_CD = :V_YD_SCH_CD
				 */
				jrParam.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드
				JDTORecordSet jrSchInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchCdInfo", logId, methodNm, "TB_YM_SCHEDULERULE 조회");
				String sYD_SCH_AUTO_ST_YN = jrSchInfo.getRecord(0).getFieldString("YD_SCH_AUTO_ST_YN");
				
				if ("N".equals(sYD_SCH_AUTO_ST_YN)) {
					return jrRtn;
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

				jrRtn = commUtils.addSndData(ymComm.getCrnSchMsg(jrYdMsg));				
					
				commUtils.printLog(logId, methodNm, "S-");
				
			}	
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return jrRtn;
	} 
	
 	/**
	 * 오퍼레이션명 : SPM / HFL 보급, 추출, Take-In
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public JDTORecord callSPM2LineInOut(String logId, String methodNms, String YardID, String WorkID, String ProcessID, String CoilNo, String modifier){	
		String methodNm = "SPM2 / HFL 보급, 추출, Take-In[BCoilL3RcvSeEJB.callSPM2LineInOut] < " + methodNms;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STOCK_ID"	, CoilNo        ); //
			
			/*************************************** 
			 * 	작업예약이 존재하면 ERROR
			 **************************************/	

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookYN 
			SELECT DECODE(COUNT(*),0,'N','Y') AS WB_STL_YN --작업예약재료여부
			  FROM TB_YM_WRKBOOKMTL WM
			     , TB_YM_WRKBOOK    WB
			WHERE WM.YD_WBOOK_ID    = WB.YD_WBOOK_ID
			  AND WM.STOCK_ID       = :V_STOCK_ID
			  AND WM.DEL_YN         = 'N'
			  AND WB.DEL_YN         = 'N'
			*/  	  
			JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookYN", logId, methodNm, "작업예약 등록여부");

			if (jsChk2 != null && jsChk2.size() > 0) {
				if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN"))) {
					commUtils.printLog(logId, "작업요구한 CoilNo이 이미 작업예약되어 있슴  Error : "+ CoilNo, "SL");
					return jrRtn;	
				}
			}	
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1 
			-- STACK_COL_GP(적치열:첫번째자리 야드구분, 두번째 자리 동구분), STACK_LAYER_STAT(적치단 상태 L:적치중)
			SELECT STACK_COL_GP
			     , SUBSTR(STACK_COL_GP,1,1) STACK_COL_GP1
			     , SUBSTR(STACK_COL_GP,2,1) STACK_COL_GP2
			     , STACK_BED_GP
			     , STACK_LAYER_STAT
			     , STACK_LAYER_GP
			     -- 스크랩 적용여부
			     , NVL((SELECT DTL_ITM1 FROM USRYMA.TB_YM_RULE WHERE REPR_CD_GP = 'APP022' AND ROWNUM = 1),'N') AS APP022_YN
			 FROM TB_YM_STACKLAYER  A
			WHERE STOCK_ID = :V_STOCK_ID
			*/	
			JDTORecordSet jsStackLayer = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1", logId, methodNm, "TB_YM_STACKLAYER 정보");

			if (jsStackLayer.size() < 1) {
				commUtils.printLog(logId, "StackColGp 이상: "+ CoilNo, "SL");
				return jrRtn;				
	    	} 
			
			jsStackLayer.first();
			JDTORecord jrStackLayer = jsStackLayer.getRecord();
			String ydStackColGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP"),     "");
			String ydGp			= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP1"),    "");
			String ydBayGp		= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP2"),    "");
			String ydStackBedGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_BED_GP"),     "");
			String ydStackLayer	= commUtils.nvl(jrStackLayer.getFieldString("STACK_LAYER_GP"),   "");
			String sAPP022_YN	= commUtils.nvl(jrStackLayer.getFieldString("APP022_YN"),        "N");
			String TmpstackCol 	= "";
			String ydSchCd 		= "";  //SCH_CD
			String toLocGuide 	= "";  //TO위치 가이드
			
			if (ydStackColGp != null && !ydStackColGp.equals("")){

				if (WorkID.equals(YmConstant.WORK_SPM_N)){  
					/*************************************** 
					 * 	 SPM2 작업
					 **************************************/				
							
					if (ProcessID.equals(YmConstant.PROCESS_ID_1)){		
						// 보급위치 : YD에서는 TO위치 1개만 관리, 보급요구가 오면 무조건 작업예약 생성
						ydSchCd 	= "3"+ydBayGp+"KE02UM" ;
						
//						//SPM2 보급 요청시 입측 01BED 가 빈 상태일 경우만 스케줄 생성여부 기준을 확인하여 처리한다.
//						String sAPP053_SPM2_YN = ymComm.BCoilApplyYn("APP053","3","SPM2");   //SPM 보급요청시 01BED 비였을 때만 스케줄 생성 여부
//						commUtils.printLog(logId,  "==========[[[ APP053 SPM2 보급요청시 01BED 비였을 때만 스케줄 생성 여부 : " + sAPP053_SPM2_YN + " ]]]============", "SL");
//						
//						if ("Y".equals(sAPP053_SPM2_YN)) {
//							jrParam.setField("PROC_GP"	, "P"); 
//							JDTORecordSet rsEqpTracking = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.bcoilHFLSPMInOutSdInqjl.getListCOILGPTracking_PIDEV", logId, methodNm, "SPM2 트래킹 테이블 조회");
//							if(rsEqpTracking.size()>0) {
//								if(!"".equals(rsEqpTracking.getRecord(0).getFieldString("STOCK_ID"))) {
//									commUtils.printLog(logId, "<<<<< SPM2 입측 01 BED 에 " + rsEqpTracking.getRecord(0).getFieldString("STOCK_ID") + " 코일이 존재하여 크레인 스케줄을 생성하지 않습니다! >>>>>" , "SL");
//									return jrRtn;
//								}
//							}
//						}
						

					}else if (ProcessID.equals(YmConstant.PROCESS_ID_3)){
						/*****  추출처리   ************/

						if (ydBayGp.equals(YmConstant.BAY_GP_B)){
							
							TmpstackCol   = YmConstant.STACK_COL_GP_3EKD02;  //3EKD01 -> 3EKD02
							
							ydStackColGp= TmpstackCol;
							ydGp      	= TmpstackCol.substring(0,1);	// 이동할 야드 구분
							ydBayGp   	= TmpstackCol.substring(1,2);	// 이동할 동 구분	
						}
						ydSchCd 	= "3"+ydBayGp+"KD02LM" ;
						// Scrap일 경우를 비교한다. 
						if (CoilNo.substring(0, 1).equals("S") ){								 // Scrap SPM 추출.
							if ("N".equals(sAPP022_YN) ){
								ydSchCd 	= "3ESP01LM" ;  // ERROR 처리
							} else {	
								ydSchCd 	= "3EKD02LM" ;
							}	
						}else{
						//	toLocGuide 	= "";
						}
						ydStackLayer = "01";
					/*****  Coil SPM Take In처리   ************/						
					} else if (ProcessID.equals(YmConstant.PROCESS_ID_5)){
						ydSchCd 	= "3"+ydBayGp+"KE04UM" ;
					}				
				}  
				if(ydSchCd.equals("")){
    				throw new Exception("스케쥴 코드 생성 실패"); 				
    			}
				
				/*************************************** 
				 * 	1.작업예약이 생성 
                 *  2.STOCK UPDATE(작업행선)  
                 *  3.스케쥴 호출
				 **************************************/	
  			//작업예약,작업재료 등록		
    			JDTORecord jrOutTemp = JDTORecordFactory.getInstance().create();
    			jrOutTemp.setField("STL_NO"           , commUtils.trim(jrOutTemp.getFieldString("STOCK_ID"))); //재료번호
    			jrOutTemp.setField("STACK_COL_GP"     , ydStackColGp  ); 
    			jrOutTemp.setField("STACK_BED_GP"     , ydStackBedGp  ); 
    			jrOutTemp.setField("STACK_LAYER_GP"   , ydStackLayer ); 
    			jrOutTemp.setField("YD_SCH_CD"        , ydSchCd   );// 자동이적 
    			jrOutTemp.setField("MODIFIER"         , modifier    ); //수정자
    			jrOutTemp.setField("YD_TO_LOC_GUIDE"  , toLocGuide    ); //TO위치가이드
    			
    			String ydWbookId = ymComm.procWkBookInsert(jrOutTemp);
    			
    			if(ydWbookId.equals(YmConstant.RETN_CD_FAILURE)){
    				throw new Exception("작업예약ID 생성 실패"); 				
    			}
    			
				/*************************************** 
				 * 	1.저장품 수정
                 *  2.STOCK UPDATE(작업행선)  
                 *  3.스케쥴 호출
				 **************************************/
				// 저장품 Table(TB_YM_STOCK)에 작업예약ID,저장품이동조건을 Update 한다.	
				
				String sStackMoveTerm = "";
				
				if (WorkID.equals(YmConstant.WORK_HFL_S)){
					if(ProcessID.equals(YmConstant.PROCESS_ID_3)){
						// HFL결속대  대한 코드는 고정으로 적용한다.
						sStackMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_A7; //HFL 결속대 추출 							
					}else{
						sStackMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_CC; //HFL 결속대 보급
					}

				} else if ( !CoilNo.substring(0, 1).equals("S") ) {
					// Scrap에 대한 코드는 고정으로 적용한다.
					sStackMoveTerm =  YmConstant.NEW_STOCK_MOVE_TERM_A2;
					
				} else {	
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name

					jrParam.setField("STOCK_ID"	, CoilNo);   //
					jrParam.setField("MODIFIER" , modifier); //수정자

					JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
					
					if(jrRtnProg.size() > 0 ) {
						sStackMoveTerm = commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"));
					}
				}
								
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("MODIFIER" 		, modifier);    	//수정자
				jrParam.setField("WBOOK_ID" 		, ydWbookId); 		//작업예약
				jrParam.setField("PROCESS_ID" 		, ProcessID);     	//처리구분
				jrParam.setField("STOCK_MOVE_TERM" 	, sStackMoveTerm);  //저장품이동조건
				jrParam.setField("STOCK_ID"			, CoilNo);   		//
				
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo1 
				UPDATE TB_YM_STOCK
				   SET MODIFIER   = :V_MODIFIER
				     , MOD_DDTT   = SYSDATE 
				     , SHEAR_SUPPLY_GP = CASE WHEN :V_PROCESS_ID = 1 THEN ''
				                              ELSE SHEAR_SUPPLY_GP          END   -- 정정보급구분 
				     , SHEAR_SUPPLY_DEMAND_DDTT = CASE WHEN :V_PROCESS_ID = 1 THEN ''
				                              ELSE SHEAR_SUPPLY_DEMAND_DDTT END --정정보급요구일시
				     , WBOOK_ID = :V_WBOOK_ID                              
				     , STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM  --저장품이동조건
				 WHERE STOCK_ID = :V_STOCK_ID
				*/ 
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo1", logId, methodNm, "TB_YM_STOCK 수정");
				
				/***********************************************************************
				 * 보급시 해당 스케줄 자동기동 여부가 N이면 스케줄 기동하지 않음 
				 ***********************************************************************/
				/*
				SELECT *
				  FROM TB_YM_SCHEDULERULE
				 WHERE YD_SCH_CD = :V_YD_SCH_CD
				 */
				jrParam.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드
				JDTORecordSet jrSchInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchCdInfo", logId, methodNm, "TB_YM_SCHEDULERULE 조회");
				String sYD_SCH_AUTO_ST_YN = jrSchInfo.getRecord(0).getFieldString("YD_SCH_AUTO_ST_YN");
				
				if ("N".equals(sYD_SCH_AUTO_ST_YN)) {
					return jrRtn;
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

				jrRtn = commUtils.addSndData(ymComm.getCrnSchMsg(jrYdMsg));				
					
				commUtils.printLog(logId, methodNm, "S-");
				
			}	
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return jrRtn;
	} 
		
	
	/**
	 * 오퍼레이션명 : SPM / HFL 보급, 추출, Take-In
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public JDTORecord callSPM2LineInOutD(String logId, String methodNms, String YardID, String WorkID, String ProcessID, String CoilNo, String modifier, String sTAKE_IN_GP){	
		String methodNm = "SPM2 / HFL 보급, 추출, Take-In출측[BCoilL3RcvSeEJB.callSPM2LineInOutD] < " + methodNms;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STOCK_ID"	, CoilNo        ); //
			
			/*************************************** 
			 * 	작업예약이 존재하면 ERROR
			 **************************************/	
			/* 
			SELECT DECODE(COUNT(*),0,'N','Y') AS WB_STL_YN --작업예약재료여부
			  FROM TB_YM_WRKBOOKMTL WM
			     , TB_YM_WRKBOOK    WB
			WHERE WM.YD_WBOOK_ID    = WB.YD_WBOOK_ID
			  AND WM.STOCK_ID       = :V_STOCK_ID
			  AND WM.DEL_YN         = 'N'
			  AND WB.DEL_YN         = 'N'
			*/  	  
			JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookYN", logId, methodNm, "작업예약 등록여부");

			if (jsChk2 != null && jsChk2.size() > 0) {
				if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN"))) {
					commUtils.printLog(logId, "작업요구한 CoilNo이 이미 작업예약되어 있슴  Error : "+ CoilNo, "SL");
					return jrRtn;	
				}
			}	
			/* 
			-- STACK_COL_GP(적치열:첫번째자리 야드구분, 두번째 자리 동구분), STACK_LAYER_STAT(적치단 상태 L:적치중)
			SELECT STACK_COL_GP
			     , SUBSTR(STACK_COL_GP,1,1) STACK_COL_GP1
			     , SUBSTR(STACK_COL_GP,2,1) STACK_COL_GP2
			     , STACK_BED_GP
			     , STACK_LAYER_STAT
			     , STACK_LAYER_GP
			     -- 스크랩 적용여부
			     , NVL((SELECT DTL_ITM1 FROM USRYMA.TB_YM_RULE WHERE REPR_CD_GP = 'APP022' AND ROWNUM = 1),'N') AS APP022_YN
			 FROM TB_YM_STACKLAYER  A
			WHERE STOCK_ID = :V_STOCK_ID
			*/	
			JDTORecordSet jsStackLayer = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1", logId, methodNm, "TB_YM_STACKLAYER 정보");

			if (jsStackLayer.size() < 1) {
				commUtils.printLog(logId, "StackColGp 이상: "+ CoilNo, "SL");
				return jrRtn;				
	    	} 
			
			jsStackLayer.first();
			JDTORecord jrStackLayer = jsStackLayer.getRecord();
			String ydStackColGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP"),     "");
			String ydGp			= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP1"),    "");
			String ydBayGp		= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP2"),    "");
			String ydStackBedGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_BED_GP"),     "");
			String ydStackLayer	= commUtils.nvl(jrStackLayer.getFieldString("STACK_LAYER_GP"),   "");
			String TmpstackCol 	= "";
			String ydSchCd 		= "";  //SCH_CD
			String toLocGuide 	= "";  //TO위치 가이드
			
			if (ydStackColGp != null && !ydStackColGp.equals("")){

				if (WorkID.equals(YmConstant.WORK_SPM_N)){  
					/*************************************** 
					 * 	 SPM2 작업
					 **************************************/				
							
					if (ProcessID.equals(YmConstant.PROCESS_ID_1)){		
						// 보급위치 : YD에서는 TO위치 1개만 관리, 보급요구가 오면 무조건 작업예약 생성
						ydSchCd 	= "3"+ydBayGp+"KE02UM" ;

					} else if (ProcessID.equals(YmConstant.PROCESS_ID_3)){
						/*****  추출처리   ************/

						if (ydBayGp.equals(YmConstant.BAY_GP_B)){
							
							TmpstackCol   = YmConstant.STACK_COL_GP_3EKD02;  //3EKD01 -> 3EKD02
							
							ydStackColGp= TmpstackCol;
							ydGp      	= TmpstackCol.substring(0,1);	// 이동할 야드 구분
							ydBayGp   	= TmpstackCol.substring(1,2);	// 이동할 동 구분	
						}
						
						ydSchCd 	= "3"+ydBayGp+"KD02LM" ;
						// Scrap일 경우를 비교한다. 
						if (CoilNo.substring(0, 1).equals("S")) {// Scrap SPM2 추출.
							ydSchCd 	= "3EKD02LM" ;
						}
						ydStackLayer = "01";
					} else if (ProcessID.equals(YmConstant.PROCESS_ID_5)) { //Coil SPM Take In처리
						ydSchCd 	= "3"+ydBayGp+"KE04UM" ;
					}				
				}  
				if (ydSchCd.equals("")) {
    				throw new Exception("스케쥴 코드 생성 실패"); 				
    			}
				
				/*************************************** 
				 * 	1.작업예약이 생성 
                 *  2.STOCK UPDATE(작업행선)  
                 *  3.스케쥴 호출
				 **************************************/	
  			//작업예약,작업재료 등록		
    			JDTORecord jrOutTemp = JDTORecordFactory.getInstance().create();
    			jrOutTemp.setField("STL_NO"           , commUtils.trim(jrOutTemp.getFieldString("STOCK_ID"))); //재료번호
    			jrOutTemp.setField("STACK_COL_GP"     , ydStackColGp  ); 
    			jrOutTemp.setField("STACK_BED_GP"     , ydStackBedGp  ); 
    			jrOutTemp.setField("STACK_LAYER_GP"   , ydStackLayer ); 
    			jrOutTemp.setField("YD_SCH_CD"        , ydSchCd   );// 자동이적 
    			jrOutTemp.setField("MODIFIER"         , modifier    ); //수정자
    			if ("D".equals(sTAKE_IN_GP)) {
    				jrOutTemp.setField("YD_TO_LOC_GUIDE"  , "3"+ydBayGp+"KD02"); //출측 TakeIn작업은 TO위치가이드로 구분한다 
    			} else {
    				jrOutTemp.setField("YD_TO_LOC_GUIDE"  , toLocGuide    ); //TO위치가이드
    			}
    			
    			
    			String ydWbookId = ymComm.procWkBookInsert(jrOutTemp);
    			
    			if(ydWbookId.equals(YmConstant.RETN_CD_FAILURE)){
    				throw new Exception("작업예약ID 생성 실패"); 				
    			}
    			
				/*************************************** 
				 * 	1.저장품 수정
                 *  2.STOCK UPDATE(작업행선)  
                 *  3.스케쥴 호출
				 **************************************/
				// 저장품 Table(TB_YM_STOCK)에 작업예약ID,저장품이동조건을 Update 한다.	
				
				String sStackMoveTerm = "";
				
				if (WorkID.equals(YmConstant.WORK_HFL_S)){
					if(ProcessID.equals(YmConstant.PROCESS_ID_3)){
						// HFL결속대  대한 코드는 고정으로 적용한다.
						sStackMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_A7; //HFL 결속대 추출 							
					}else{
						sStackMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_CC; //HFL 결속대 보급
					}

				} else if ( !CoilNo.substring(0, 1).equals("S") ) {
					// Scrap에 대한 코드는 고정으로 적용한다.
					sStackMoveTerm =  YmConstant.NEW_STOCK_MOVE_TERM_A2;
					
				} else {	
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name

					jrParam.setField("STOCK_ID"	, CoilNo);   //
					jrParam.setField("MODIFIER" , modifier); //수정자

					JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
					
					if(jrRtnProg.size() > 0 ) {
						sStackMoveTerm = commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"));
					}
				}
								
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("MODIFIER" 		, modifier);    	//수정자
				jrParam.setField("WBOOK_ID" 		, ydWbookId); 		//작업예약
				jrParam.setField("PROCESS_ID" 		, ProcessID);     	//처리구분
				jrParam.setField("STOCK_MOVE_TERM" 	, sStackMoveTerm);  //저장품이동조건
				jrParam.setField("STOCK_ID"			, CoilNo);   		//
				
				/* 
				UPDATE TB_YM_STOCK
				   SET MODIFIER   = :V_MODIFIER
				     , MOD_DDTT   = SYSDATE 
				     , SHEAR_SUPPLY_GP = CASE WHEN :V_PROCESS_ID = 1 THEN ''
				                              ELSE SHEAR_SUPPLY_GP          END   -- 정정보급구분 
				     , SHEAR_SUPPLY_DEMAND_DDTT = CASE WHEN :V_PROCESS_ID = 1 THEN ''
				                              ELSE SHEAR_SUPPLY_DEMAND_DDTT END --정정보급요구일시
				     , WBOOK_ID = :V_WBOOK_ID                              
				     , STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM  --저장품이동조건
				 WHERE STOCK_ID = :V_STOCK_ID
				*/ 
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo1", logId, methodNm, "TB_YM_STOCK 수정");
				
				/***********************************************************************
				 * 보급시 해당 스케줄 자동기동 여부가 N이면 스케줄 기동하지 않음 
				 ***********************************************************************/
				/*
				SELECT *
				  FROM TB_YM_SCHEDULERULE
				 WHERE YD_SCH_CD = :V_YD_SCH_CD
				 */
				jrParam.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드
				JDTORecordSet jrSchInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchCdInfo", logId, methodNm, "TB_YM_SCHEDULERULE 조회");
				String sYD_SCH_AUTO_ST_YN = jrSchInfo.getRecord(0).getFieldString("YD_SCH_AUTO_ST_YN");
				
				if ("N".equals(sYD_SCH_AUTO_ST_YN)) {
					return jrRtn;
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

				jrRtn = commUtils.addSndData(ymComm.getCrnSchMsg(jrYdMsg));				
					
				commUtils.printLog(logId, methodNm, "S-");
				
			}	
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return jrRtn;
	} 
	
	
	/**	
	 * [A] 오퍼레이션명 : 충당정보 수신(PTYDJ001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPTYDJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일충당정보수신[BCoilL3RcvSeEJB.rcvPTYDJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String StlNo 	= commUtils.trim(rcvMsg.getFieldString("STL_NO"));	//충당재료
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(StlNo)) {
				throw new Exception("충당재료 정보가 없습니다..");
			}

			/**********************************************************
			* 2. 저장품 이동 조건 수정
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("STOCK_ID"	, StlNo); //충당재료
			jrParam.setField("MODIFIER" , modifier); //수정자

			JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
			
			if(jrRtnProg.size() > 0 ) {
			
				jrParam.setField("STOCK_MOVE_TERM" , commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"))); 

				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo
				UPDATE TB_YM_STOCK
				   SET MODIFIER   = :V_MODIFIER
				     , MOD_DDTT   = SYSDATE 
				     , STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
				WHERE STOCK_ID = :V_STOCK_ID
				*/ 
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo", logId, methodNm, "TB_YM_STOCK 수정");
				
//				//======================================================
//				// 저장품제원 : 코일야드L2 로 송신(YMA7L002)
//				//======================================================
//				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
//				sndL2Msg.setResultCode(logId);	//Log ID
//				sndL2Msg.setResultMsg(methodNm);	//Log Method Name
//				sndL2Msg.setField("YD_INFO_SYNC_CD"	, "5"       ); //야드정보동기화코드
//				sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
//				sndL2Msg.setField("STOCK_ID"       	, StlNo ); //재료번호
//					
//				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L002", sndL2Msg));	 //전송 Data 생성					
			}
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일소재 이송지시 수신(PTYDJ002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPTYDJ002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일소재 이송지시 수신[BCoilL3RcvSeEJB.rcvPTYDJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String ModGp 	= commUtils.trim(rcvMsg.getFieldString("MOD_GP"));				//작업구분
			String FmWordDt = commUtils.trim(rcvMsg.getFieldString("FRTOMOVE_WORD_DATE"));	//이송작업지시일자
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String szMsg    ="";
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(FmWordDt)) {
				//throw new Exception("이송작업지시일자가 없습니다..");
				szMsg = "["+methodNm+"] 이송작업지시일자가 없습니다..";
		      	commUtils.printLog(logId, szMsg, "SL");
		      	 
			    return jrRtn;	
			}

			/**********************************************************
			* 2. 저장품 이동 조건 수정
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("FRTOMOVE_WORD_DATE"	, FmWordDt); //이송작업지시일자
			
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdStockFRTOMOVE_WORD_DATE_COIL 
			SELECT C.STL_NO             AS STL_NO
			     , D.FRTOMOVE_STAT_CD   AS FRTOMOVE_STAT_CD
			     , D.ARR_WLOC_CD        AS ARR_WLOC_CD
			     , E.CURR_PROG_CD       AS STL_PROG_CD
			     , E.PLNT_PROC_CD       AS PLNT_PROC_CD
			   FROM TB_YD_STOCK C
			       ,(SELECT A.*
			           FROM TB_PT_STLFRTOMOVE A
			               ,(SELECT STL_NO
			                       ,MAX(TRANSWORD_SEQNO) AS TRANSWORD_SEQNO
			                   FROM TB_PT_STLFRTOMOVE
			                  WHERE FRTOMOVE_WORD_DATE = :FRTOMOVE_WORD_DATE
			                    AND FRTOMOVE_STAT_CD IN ('1','C','0')
			                  GROUP BY STL_NO ) B
			          WHERE B.STL_NO = A.STL_NO
			            AND A.TRANSWORD_SEQNO = B.TRANSWORD_SEQNO 
			            AND A.STL_APPEAR_GP = 'E'
			            AND A.RENTPROC_COMCD IS NULL ) D
			        ,TB_PT_COILCOMM E   
			  WHERE C.STL_NO = D.STL_NO
			  AND D.STL_NO = E.COIL_NO
			 */ 
			JDTORecordSet jsSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdStockFRTOMOVE_WORD_DATE_COIL", logId, methodNm, "크레인스케줄 조회");
			
			JDTORecord recInCrn = JDTORecordFactory.getInstance().create();
			for(int Loop_i = 1; Loop_i <= jsSch.size(); Loop_i++) {
				jsSch.absolute(Loop_i);
				recInCrn	= jsSch.getRecord();

				jrParam.setField("STOCK_ID"	, recInCrn.getFieldString("STL_NO")); //충당재료

				JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
				
				jrParam.setField("STOCK_MOVE_TERM" , commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"))); 

				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo_05
				UPDATE TB_YM_STOCK
				   SET MODIFIER   = :V_MODIFIER
				     , MOD_DDTT   = SYSDATE 
				     , STOCK_MOVE_TERM  = :V_STOCK_MOVE_TERM
				     , FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO
				 WHERE STOCK_ID = :V_STOCK_ID
				*/ 
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo_05", logId, methodNm, "TB_YM_STOCK 수정");
					
			}
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
		
	/**	
	 * [A] 오퍼레이션명 : 코일소재 임가공 이송지시 수신(PTYDJ003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPTYDJ003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일소재 임가공 이송지시 수신[BCoilL3RcvSeEJB.rcvPTYDJ003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String ModGp 	= commUtils.trim(rcvMsg.getFieldString("MOD_GP"));				//작업구분
			String FmWordDt = commUtils.trim(rcvMsg.getFieldString("FRTOMOVE_WORD_DATE"));	//이송작업지시일자
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String szMsg    ="";
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(FmWordDt)) {
				//throw new Exception("이송작업지시일자가 없습니다..");
				szMsg = "["+methodNm+"] 이송작업지시일자가 없습니다..";
		      	commUtils.printLog(logId, szMsg, "SL");
		        return jrRtn; 
			}

			/**********************************************************
			* 2. 저장품 이동 조건 수정
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("FRTOMOVE_WORD_DATE"	, FmWordDt); //이송작업지시일자
			
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdStockFRTOMOVE_WORD_DATE_RENTCOIL 
			 SELECT C.STL_NO                    AS STL_NO
			       ,C.YD_AIM_RT_GP              AS YD_AIM_RT_GP
			       ,D.FRTOMOVE_WORD_DATE        AS FRTOMOVE_WORD_DATE
			       ,D.URGENT_FRTOMOVE_WORD_GP   AS URGENT_FRTOMOVE_WORD_GP
			       ,D.WO_CAR_PLNT_PROC_CD       AS PLNT_PROC_CD
			       ,D.FRTOMOVE_STAT_CD          AS FRTOMOVE_STAT_CD
			       ,D.FRTOMOVE_ORD_CANCEL_DATE  AS FRTOMOVE_ORD_CANCEL_DATE
			       ,D.RENTPROC_COMCD
			   FROM TB_YD_STOCK C
			       ,(SELECT A.STL_NO                    AS STL_NO
			               ,A.TRANSWORD_SEQNO           AS TRANSWORD_SEQNO
			               ,A.FRTOMOVE_WORD_DATE        AS FRTOMOVE_WORD_DATE
			               ,A.WO_CAR_PLNT_PROC_CD       AS WO_CAR_PLNT_PROC_CD
			               ,A.URGENT_FRTOMOVE_WORD_GP   AS URGENT_FRTOMOVE_WORD_GP
			               ,A.FRTOMOVE_STAT_CD          AS FRTOMOVE_STAT_CD
			               ,A.SLAB_WO_RT_CD             AS SLAB_WO_RT_CD
			               ,A.FRTOMOVE_ORD_CANCEL_DATE  AS FRTOMOVE_ORD_CANCEL_DATE
			               ,A.RENTPROC_COMCD
			           FROM TB_PT_STLFRTOMOVE A
			               ,(SELECT STL_NO
			                       ,MAX(TRANSWORD_SEQNO) AS TRANSWORD_SEQNO
			                   FROM TB_PT_STLFRTOMOVE
			                  WHERE FRTOMOVE_WORD_DATE = :V_FRTOMOVE_WORD_DATE
			                    AND FRTOMOVE_STAT_CD IN ('1','C')
			                  GROUP BY STL_NO ) B
			          WHERE B.STL_NO = A.STL_NO
			            AND A.TRANSWORD_SEQNO = B.TRANSWORD_SEQNO 
			            AND A.STL_APPEAR_GP = 'E'
			            AND A.RENTPROC_COMCD IS NOT NULL ) D
			  WHERE C.STL_NO = D.STL_NO
			 */ 
			JDTORecordSet jsSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdStockFRTOMOVE_WORD_DATE_RENTCOIL", logId, methodNm, "크레인스케줄 조회");
			
			JDTORecord recInCrn = JDTORecordFactory.getInstance().create();
			for(int Loop_i = 1; Loop_i <= jsSch.size(); Loop_i++) {
				jsSch.absolute(Loop_i);
				recInCrn	= jsSch.getRecord();

				jrParam.setField("STOCK_ID"	, recInCrn.getFieldString("STL_NO")); //충당재료

				JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
				
				jrParam.setField("STOCK_MOVE_TERM" , commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"))); 

				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo_05
				UPDATE TB_YM_STOCK
				   SET MODIFIER   = :V_MODIFIER
				     , MOD_DDTT   = SYSDATE 
				     , STOCK_MOVE_TERM  = :V_STOCK_MOVE_TERM
				     , FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO
				 WHERE STOCK_ID = :V_STOCK_ID
				*/ 
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo_05", logId, methodNm, "TB_YM_STOCK 수정");
					
			}
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
			
	
	/**
	 * 코일제품보류확정 (YD 없음)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일제품보류확정 수신[BCoilL3RcvSeEJB.rcvDMYDR002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "코일제품보류확정(DMYDR002) 수신 ", rcvMsg);

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
 
			String ydStockId = commUtils.trim(rcvMsg.getFieldString("STOCK_ID"));	//Stock_id
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydStockId)) {
				throw new Exception("저장품Id가 없습니다..");
			}

			/**********************************************************
			* 2. 저장품 이동 조건 수정
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STOCK_ID"	, ydStockId); //저장품 ID
			jrParam.setField("MODIFIER" , modifier); 

			JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
			
			jrParam.setField("STOCK_MOVE_TERM" , commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"))); 
			//jrParam.setField("STOCK_MOVE_TERM" , StMvTerm); 

			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock
			UPDATE TB_YM_STOCK
			   SET STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
			     , MOD_DDTT        = SYSDATE
			     , MODIFIER        = :V_MODIFIER
			     , DEL_YN          = 'N'
			 WHERE STOCK_ID = :V_STOCK_ID
			*/ 
			intRtnVal=	commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock", logId, methodNm, "TB_YM_STOCK 수정");
				
            if(intRtnVal == 0){
				throw new Exception("수신한 재료번호 ["+ ydStockId+"]에 대한 저장품 DATA가 존재하지 않음");
			}
					
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} 

	
	/**
	 * 코일제품출하지시대기
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일제품출하지시대기 수신[BCoilL3RcvSeEJB.rcvDMYDR005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "코일제품출하지시대기(DMYDR005) 수신 ", rcvMsg);

			//수신 항목 값
			String msgId     = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String dmTcCode  = commUtils.trim(rcvMsg.getFieldString("TC_CODE"));//TC_CODE
			String ydStockId = commUtils.trim(rcvMsg.getFieldString("STL_NO"));	//Stock_id
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydStockId)) {
				throw new Exception("저장품Id가 없습니다..");
			}

			/**********************************************************
			* 2. 저장품 이동 조건 수정
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("TC_CD"	, dmTcCode);  //TC_CODE
			jrParam.setField("STOCK_ID"	, ydStockId); //저장품 ID

			JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
			jrParam.setField("STOCK_MOVE_TERM" , commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"))); 
			jrParam.setField("MODIFIER" , modifier); 

			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock
			UPDATE TB_YM_STOCK
			   SET STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
			     , MOD_DDTT        = SYSDATE
			     , MODIFIER        = :V_MODIFIER
			     , DEL_YN          = 'N'
			 WHERE STOCK_ID = :V_STOCK_ID
			*/ 
			intRtnVal=	commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock", logId, methodNm, "TB_YM_STOCK 수정");
				
			if(intRtnVal == 0){
				throw new Exception("수신한 재료번호 ["+ ydStockId+"]에 대한 저장품 DATA가 존재하지 않음");
			}
			
			//======================================================
			// 저장품제원 : 코일야드L2 로 송신(YMA7L002)
			//======================================================
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);	//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
			sndL2Msg.setField("YD_INFO_SYNC_CD"	, "5"       ); //야드정보동기화코드
			sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
			sndL2Msg.setField("STOCK_ID"       	, ydStockId ); //재료번호
				
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L002", sndL2Msg));	 //전송 Data 생성	
			
			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of rcvDMYDR005()
	
	
	/**
	 * 코일제품반납대기
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR008(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일제품반납대기 수신[BCoilL3RcvSeEJB.rcvDMYDR008] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
 			String ydStockId  = commUtils.trim(rcvMsg.getFieldString("STL_NO"));	//Stock_id
			String modifier   = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String sCancelChk = commUtils.trim(rcvMsg.getFieldString("CANCEL_YN")); //Y: 취소 , N: 지시
			String dmTcCode   = commUtils.trim(rcvMsg.getFieldString("TC_CODE"));//TC_CODE
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydStockId)) {
				throw new Exception("저장품Id가 없습니다..");
			}
			if (("Y".equals(sCancelChk))) {
				//JDTORecord recInPara = JDTORecordFactory.getInstance().create();
				//sndRecord = this.receiveCancel(logId, methodNm,recInPara,sndRecord);
				// 또는 this.receiveCancel(logId, methodNm,rcvMsg);
				return jrRtn;
			}else{
				
			   /**********************************************************
				* 2. 저장품 이동 조건 수정
				**********************************************************/
				JDTORecord jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("TC_CD"	, dmTcCode);  //TC_CODE
				jrParam.setField("STOCK_ID"	, ydStockId); //저장품 ID
	
				JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
				jrParam.setField("STOCK_MOVE_TERM" , commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"))); 
				jrParam.setField("MODIFIER" , modifier); 
				//jrParam.setField("STOCK_MOVE_TERM" , StMvTerm); 

				/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStockDMYDR008
				UPDATE TB_YM_STOCK
				   SET STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
				     , MOD_DDTT        = SYSDATE
				     , MODIFIER        = :V_MODIFIER
				     , DEL_YN          = 'N'
				     , TRANS_WORD_NO   = NULL
				     , CAR_CARD_NO     = NULL
				     , CAR_NO2         = NULL
				     , TRANS_ORD_DATE2 = NULL
				     , TRANS_ORD_SEQNO2= NULL
				 WHERE STOCK_ID = :V_STOCK_ID
				 */
				intRtnVal=	commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStockDMYDR008", logId, methodNm, "TB_YM_STOCK 수정");
					
				if(intRtnVal == 0){
					throw new Exception("수신한 재료번호 ["+ ydStockId+"]에 대한 저장품 DATA가 존재하지 않음");
				}
				
				//======================================================
				// 저장품제원 : 코일야드L2 로 송신(YMA7L002)
				//======================================================
				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
				sndL2Msg.setResultCode(logId);	//Log ID
				sndL2Msg.setResultMsg(methodNm);	//Log Method Name
				sndL2Msg.setField("YD_INFO_SYNC_CD"	, "5"                         ); //야드정보동기화코드
				sndL2Msg.setField("MSG_GP"			, "I"                         ); //전문구분
				sndL2Msg.setField("STOCK_ID"       	, ydStockId                    ); //재료번호
					
				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L002", sndL2Msg));	 //전송 Data 생성	
				
				commUtils.printLog(logId, methodNm, "S-");
				return jrRtn;
			}
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} 
	
//	/**
//	 * 코일제품고간이송지시
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param JDTORecord rcvMsg
//	 * @return JDTORecord
//	 * @throws DAOException
//	 * @ejb.transaction type="RequiresNew"
//	 */
//	public JDTORecord rcvDMYDR011(JDTORecord rcvMsg) throws DAOException {
//		String methodNm = "코일제품고간이송지시 수신[BCoilL3RcvSeEJB.rcvDMYDR011] < " + rcvMsg.getResultMsg();
//		String logId = rcvMsg.getResultCode();
//		int intRtnVal = 0;
//		int i = 0;
//		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
//
//		try {
//			commUtils.printLog(logId, methodNm, "S+");
//
//			//수신 항목 값
//			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String dmTcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));//TC_CODE
//			String modifier		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
//			String sCancelChk 	= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN")); //Y: 취소 , N: 지시
//			if ("".equals(modifier)) { modifier = msgId; }
//
//			/**********************************************************
//			* 1. 수신 항목 값 Check
//			**********************************************************/
//
//			if (("Y".equals(sCancelChk))) {
//				//JDTORecord recInPara = JDTORecordFactory.getInstance().create();
//				//sndRecord = this.receiveCancel(logId, methodNm,recInPara,sndRecord);
//				// 또는 this.receiveCancel(logId, methodNm,rcvMsg);
//				return jrRtn;
//			}else{
//				
//			   /**********************************************************
//				* 2. 저장품 이동 조건 수정
//				**********************************************************/
//				for(i = 1 ; i<=20; i++){				
//					JDTORecord jrParam = JDTORecordFactory.getInstance().create();
//					jrParam.setResultCode(logId);	//Log ID
//					jrParam.setResultMsg(methodNm);	//Log Method Name
//		
//					//jrParam.setField("STOCK_MOVE_TERM"	, StMvTerm); //저장품이송조건
//					jrParam.setField("STOCK_ID" 		, commUtils.trim(rcvMsg.getFieldString("STL_NO"+i))); //저장품 ID
//					jrParam.setField("TC_CD"			, dmTcCode);  //TC_CODE
//					JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
//
//					jrParam.setField("SHEAR_SUPPLY_SEQ" , commUtils.trim(rcvMsg.getFieldString("SHEAR_SUPPLY_SEQ"))); 
//					jrParam.setField("STOCK_MOVE_TERM" 	, commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"))); 
//					jrParam.setField("TRANS_ORD_DT2" 	, commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"))); 						
//					jrParam.setField("TRANS_ORD_SEQNO2"	, commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"))); 						
//					jrParam.setField("MODIFIER" 		, modifier); 						
//					jrParam.setField("CAR_KIND" 		, commUtils.trim(rcvMsg.getFieldString("CAR_KIND"))); 
//					jrParam.setField("CAR_CARD_NO" 		, commUtils.trim(rcvMsg.getFieldString("CAR_CARD_NO"))); 
//					jrParam.setField("CAR_NO2" 			, commUtils.trim(rcvMsg.getFieldString("CAR_NO2"))); 
//					jrParam.setField("WBOOK_ID" 		, ""); 
//					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStock
//					UPDATE USRYMA.TB_YM_STOCK
//					   SET MODIFIER         = :V_MODIFIER
//					     , MOD_DDTT         = SYSDATE
//					     , STOCK_MOVE_TERM  = NVL(:V_STOCK_MOVE_TERM , STOCK_MOVE_TERM)   --저장품이동조건
//					     , YD_RULE_PL_RS_GP = NVL(:V_YD_RULE_PL_RS_GP, YD_RULE_PL_RS_GP)  --조합구분
//					     , TRANS_WORD_NO    = :V_TRANS_ORD_DATE2||:V_TRANS_ORD_SEQNO2
//					     , TRANS_ORD_DATE2  = NVL(:V_TRANS_ORD_DATE2 , TRANS_ORD_DATE2)   --운송지시
//					     , TRANS_ORD_SEQNO2 = NVL(:V_TRANS_ORD_SEQNO2, TRANS_ORD_SEQNO2)  --운송지시행번
//					     , SHEAR_SUPPLY_SEQ = NVL(:V_SHEAR_SUPPLY_SEQ, SHEAR_SUPPLY_SEQ)  --차상위치
//					     , CAR_NO2          = NVL(:V_CAR_NO2         , CAR_NO2)           --차량번호 
//					     , CAR_CARD_NO      = NVL(:V_CAR_CARD_NO     , CAR_CARD_NO)       --카드번호
//					     , SHEAR_SUPPLY_GP  = NVL(:V_SHEAR_SUPPLY_GP , SHEAR_SUPPLY_GP)   --차량종류
//					     , WBOOK_ID         = NVL(:V_WBOOK_ID        , WBOOK_ID)          --작업예약 사용안함
//					 WHERE STOCK_ID = :V_STOCK_ID
//					*/ 
//					
//					
//					intRtnVal=	commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStock", logId, methodNm, "TB_YM_STOCK 수정");
//						
//		            if(intRtnVal == 0){
//		           	   continue;
//							//throw new Exception("수신한 재료번호 ["+ StStlNo+"]에 대한 저장품 DATA가 존재하지 않음");
//					}
//				}	
//				
//				commUtils.printLog(logId, methodNm, "S-");
//				return jrRtn;
//			}
//
//		} catch (DAOException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
//		}
//	} // end of rcvDMYDR011()		
	
	
	/**
	 * 코일제품목전
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR014(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일제품목전 수신[BCoilL3RcvSeEJB.rcvDMYDR014] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String dmTcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));//TC_CODE
			String ydStockId	= commUtils.trim(rcvMsg.getFieldString("STL_NO"));	//Stock_id
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String sCancelChk 	= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN")); //Y: 취소 , N: 지시
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydStockId)) {
				throw new Exception("저장품Id가 없습니다..");
			}
			if (("Y".equals(sCancelChk))) {
				//JDTORecord recInPara = JDTORecordFactory.getInstance().create();
				//sndRecord = this.receiveCancel(logId, methodNm,recInPara,sndRecord);
				// 또는 this.receiveCancel(logId, methodNm,rcvMsg);
				return jrRtn;
			}else{
				
			   /**********************************************************
				* 2. 저장품 이동 조건 수정
				**********************************************************/
				JDTORecord jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("TC_CD"			, dmTcCode);  //TC_CODE
				jrParam.setField("STOCK_ID"			, ydStockId); //저장품 ID

				JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
				jrParam.setField("STOCK_MOVE_TERM" 	, commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"))); 
				jrParam.setField("MODIFIER"  		, modifier); 

				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock 
				UPDATE TB_YM_STOCK
				   SET STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
				     , MOD_DDTT        = SYSDATE
				     , MODIFIER        = :V_MODIFIER
				     , DEL_YN          = 'N'
				 WHERE STOCK_ID = :V_STOCK_ID
				*/ 
				intRtnVal=	commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock", logId, methodNm, "TB_YM_STOCK 수정");
					
				if(intRtnVal == 0){
					throw new Exception("수신한 재료번호 ["+ ydStockId+"]에 대한 저장품 DATA가 존재하지 않음");
				}
				//======================================================
				// 저장품제원 : 코일야드L2 로 송신(YMA7L002)
				//======================================================
				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
				sndL2Msg.setResultCode(logId);	//Log ID
				sndL2Msg.setResultMsg(methodNm);	//Log Method Name
				sndL2Msg.setField("YD_INFO_SYNC_CD"	, "5"                         ); //야드정보동기화코드
				sndL2Msg.setField("MSG_GP"			, "I"                         ); //전문구분
				sndL2Msg.setField("STOCK_ID"       	, ydStockId                    ); //재료번호
					
				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L002", sndL2Msg));	 //전송 Data 생성	
					
				commUtils.printLog(logId, methodNm, "S-");
				return jrRtn;
			}
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of rcvDMYDR014()
	
	/**
	 * 코일제품보관지시
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR027(JDTORecord rcvMsg) throws DAOException {
		//from >com.inisteel.cim.ym.ilkwan.session.CoilRegSBean.procCoilGdsKeepOrd
		String methodNm = "코일제품보관지시 수신[BCoilL3RcvSeEJB.rcvDMYDR027] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
 
		try {
			commUtils.printLog(logId, methodNm, "S+");

			String msgId       = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String szRcvTcCode = commUtils.trim(rcvMsg.getFieldString("JMS_TC_CD"));
			String modifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String StockId     = commUtils.trim(rcvMsg.getFieldString("STL_NO"));
			if ("".equals(modifier)) { modifier = msgId; }
			

			// 수신한 전문이 null이라면 error
			if(szRcvTcCode.equals("")){
				throw new Exception("TC Code Error.");
			} else {
				// 수신한 전문내용 
				commUtils.printParam(logId, rcvMsg);
			}
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/  
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("STL_NO"  	          ,StockId);
			jrParam.setField("KEEPSTOCK_STL_YN"   ,"Y");
			jrParam.setField("MODIFIER"  	      ,modifier);
 			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updYmStock27*/
           /*UPDATE TB_YM_STOCK 
				SET  
					 MODIFIER = :V_MODIFIER
				  	,MOD_DDTT = SYSDATE
				    ,KEEPSTOCK_STL_YN= :V_KEEPSTOCK_STL_YN
				WHERE STOCK_ID  = :V_STL_NO
			*/
			intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYmStock27", logId, methodNm, "TB_YM_STOCK 수정");
			if(intRtnVal <= 0){
				commUtils.printLog(logId, "["+methodNm+"] YD_STOCK[코일제품보관지시] UPDATE Error : STOCK_ID: "+ StockId, "SL");
				return jrRtn;
			}				
		
			// ======================================================
			// 저장품제원 : 코일야드L2로 송신(YMA7L002)
			// ======================================================				
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);	//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
			sndL2Msg.setField("YD_INFO_SYNC_CD"	, "5"       ); //야드정보동기화코드
			sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
			sndL2Msg.setField("STOCK_ID"       	, StockId  ); //재료번호
			sndL2Msg.setField("YD_STK_COL_GP"     , "");
			sndL2Msg.setField("YD_STK_BED_NO"     , "");				
			commUtils.printParam(logId, sndL2Msg);
 
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L002", sndL2Msg));	 //전송 Data 생성	

			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;
			 
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of rcvDMYDR027()
	
	
	/**
	 * 코일제품출하완료
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR030(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일제품출하완료 수신[BCoilL3RcvSeEJB.rcvDMYDR030] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
	    String szMsg	= "";
	    
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			String msgId       	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String szRcvTcCode 	= commUtils.trim(rcvMsg.getFieldString("JMS_TC_CD"));
			String StockId    	= commUtils.trim(rcvMsg.getFieldString("STL_NO")); 
			String stlAppearGp	= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")); 
			String ydGp        	= commUtils.trim(rcvMsg.getFieldString("YD_GP")); 
			String BackUpYn     = commUtils.trim(rcvMsg.getFieldString("BACKUP_YN")); 
			String modifier    	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/  			
			// 수신한 전문이 null이라면 error
			if (szRcvTcCode.equals("")) {
				szMsg="[ERROR] "+methodNm+"::"+methodNm+"() TC Code Error (NULL)";	
				throw new Exception("TC Code Error.");
			}
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STL_NO"	, StockId);

			/*SELECT                                                                                
			     A.STOCK_ID                  AS STL_NO                                         
			    ,A.TRANS_ORD_DATE2           AS TRANS_ORD_DATE                                  
			    ,A.TRANS_ORD_SEQNO2          AS TRANS_ORD_SEQNO                                 
			    ,A.CAR_NO2                   AS CAR_NO                                          
			    ,A.CAR_CARD_NO               AS CARD_NO                                       
			    ,NVL(B.STACK_COL_GP, C.STACK_COL_GP)   AS YD_STK_COL_GP 
			FROM TB_YM_STOCK A                                                                    
			    ,TB_YM_STACKLAYER B                                                               
			    ,TB_YM_STACKCOL C                                                                 
			WHERE A.STOCK_ID = :V_STL_NO                                                                   
			  AND A.STOCK_ID = B.STOCK_ID(+)                                                          
			  AND A.CAR_CARD_NO = C.CAR_CARD_NO(+)    */                                                     				
			JDTORecordSet loadYdStkcol = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdStockJoinStkLyr2", logId, methodNm, "차량정정보검색");
			
			if (loadYdStkcol.size() <= 0) {
				szMsg="["+methodNm+"] YMSTOCK[코일제품출하완료]조건조회시 SELECT Error ::  DO NOT EXIST"  ;
				commUtils.printLog(logId, szMsg, "SL");	
				return jrRtn ;
			}
			//수신 항목 값
			String ydCarNo    	= commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("CAR_NO"));
			String ydStkColGp 	= commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("YD_STK_COL_GP"));
			String ydCardNo     = commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("CARD_NO"));
			String transOrdDate	= commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("TRANS_ORD_DATE"));
			String transOrdSeqNo= commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("TRANS_ORD_SEQNO"));

			jrParam.setField("STL_APPEAR_GP", 	stlAppearGp);
			jrParam.setField("DEL_YN"         , "Y");
			jrParam.setField("YD_AIM_RT_GP"   , "M2");
			jrParam.setField("STL_PROG_CD"    , "M"); 
			jrParam.setField("STOCK_ID"		  , StockId);
			jrParam.setField("MODIFIER"       , modifier);
			
			/*
            UPDATE USRYMA.TB_YM_STOCK
				SET DEL_YN=:V_DEL_YN
				  , MODIFIER=:V_MODIFIER  
				  , MOD_DDTT =SYSDATE
				  , YD_AIM_RT_GP = :V_YD_AIM_RT_GP
				WHERE STOCK_ID=:V_STOCK_ID
			*/
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStock", logId, methodNm, "TB_YM_STOCK 수정");
							
			//***************************************************************************
			//  저장품이 적치된 저장위치 정보를 조회
			//***************************************************************************
			
			szMsg="[" + methodNm + "]카드번호["+ydCardNo+"], 차량번호["+ydCardNo+"], 운송지시일자["+transOrdDate+"], 운송지시순번["+transOrdSeqNo+"] : 출하완료된 동["+ydStkColGp+"]의 저장품들 조회 시작";
			commUtils.printLog(logId, szMsg, "SL");
			
			if (stlAppearGp.equals("*") ) {

				commUtils.printLog(logId, "[" + methodNm + "] 마지막 상차완료 전문", "SL");
				jrParam.setField("CAR_NO" 			, ydCarNo);
				jrParam.setField("CARD_NO"			, ydCardNo);
				jrParam.setField("TRANS_ORD_DATE"	, transOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);
				
				/*
		    	SELECT *
		    	  FROM (
		    	       SELECT YD_CAR_SCH_ID
		    	         FROM TB_YD_CARSCH
		    	        WHERE CAR_NO        LIKE :V_CAR_NO||'%'
		    	          AND TRANS_ORD_DATE = :V_TRANS_ORD_DATE
		    	          AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
		    	        --AND DEL_YN='N'
		    	        ORDER BY YD_CAR_SCH_ID DESC
		    	        ) A
		    	WHERE ROWNUM<=1
		    	*/                                                     				
				JDTORecordSet loadCarsch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschTransDTSeq2", logId, methodNm, "차량정정보검색");
				
				//if(loadCarsch.size() <= 0 ){
				//	szMsg="["+methodNm+"] 차량스케쥴 조회 SELECT Error ::  DO NOT EXIST"  ;
				//	commUtils.printLog(logId, szMsg, "SL");	
				//	return jrRtn ;
				//}
				
				if(loadCarsch.size() > 0 ) {

					jrParam.setField("TC_CODE"			, "DMYDR040");	//전문코드
					jrParam.setField("SPOS_WLOC_CD"		, commUtils.trim(loadCarsch.getRecord(0).getFieldString("SPOS_WLOC_CD")));
					jrParam.setField("SPOS_YD_PNT_CD"	, commUtils.trim(loadCarsch.getRecord(0).getFieldString("YD_PNT_CD1")));
					jrParam.setField("YD_GP"			, ydGp);
					if ("".equals(ydCardNo)) {
						ydCardNo = "XXXXX";
					}					
					
					commUtils.printParam(logId, jrParam);
					//전송 Data 생성
					szMsg= "["+ methodNm +"] 차량번호[" + ydCarNo + "]는 코일제품출하차량출발실적호출";
					commUtils.printLog(logId, szMsg, "SL");
					
					EJBConnector ejbConn1 = new EJBConnector("default", "YmCommCarMvSeEJB", this);
					JDTORecord jrRtn1 = (JDTORecord)ejbConn1.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					
					jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
				}
				
			} else {
				commUtils.printLog(logId, "[" + methodNm + "] 마지막 상차완료 전문이 아님", "SL");
			}
			
			/********************************************************
			 * 차량 출발후 저장품 정보 갱신
			 * 저장품제원 : 코일야드L2로 송신(YMA7L002)
			 ********************************************************/
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);	//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
			sndL2Msg.setField("YD_INFO_SYNC_CD"	, "5"       ); //야드정보동기화코드
			sndL2Msg.setField("MSG_GP"			, "D"       ); //전문구분 
			sndL2Msg.setField("STOCK_ID"       	, StockId  ); //재료번호
 
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L002", sndL2Msg));	 //전송 Data 생성	
			
			commUtils.printLog(logId,"[코일제품출하완료]["+StockId+"] BACKUP 유무=>:+"+BackUpYn, "SL");

			
			/*******************************************
			 * layer정보가 존재 하는 경우 (백업으로 취소 처리 시)
			 *******************************************/
			String sYD_STK_LOC = "3X01010101";
			if (BackUpYn.equals("Y")) {
				/*
				SELECT *
				  FROM TB_YM_STACKLAYER
				 WHERE STOCK_ID = :V_STOCK_ID
				   AND SUBSTR(STACK_COL_GP,3,2) NOT IN ('TR','PT','TT') --//차량위치가 아닌경우 	
				 */
				JDTORecordSet loadStacklayer = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStacklayerList", logId, methodNm, "단정보검색");
				String sPointNo = "";
				
				if (loadStacklayer.size() > 0) {
					
					sPointNo    = commUtils.trim(loadStacklayer.getRecord(0).getFieldString("STACK_COL_GP"));
					sYD_STK_LOC = commUtils.trim(loadStacklayer.getRecord(0).getFieldString("STACK_COL_GP"))
					            + commUtils.trim(loadStacklayer.getRecord(0).getFieldString("STACK_BED_GP"))
					            + commUtils.trim(loadStacklayer.getRecord(0).getFieldString("STACK_LAYER_GP"));
					/*
					UPDATE TB_YM_STACKLAYER
					   SET STOCK_ID = NULL
					     , STACK_LAYER_ACTIVE_STAT = 'C'
					     , STACK_LAYER_STAT = 'E'
					 WHERE STOCK_ID = :V_STOCK_ID
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updateStacklayer", logId, methodNm, "TB_YM_STACKLAYER 수정");
					
					commUtils.printLog(logId, "[코일제품출하완료]["+StockId+"]에 저장위치맵을 비웁니다.+"+sPointNo, "SL");
				}
				
				/***********************************
				 * Coil 공통 Table 저장위치 Update 
				 ***********************************/	 
				if (!"".equals(sPointNo)) {
					sPointNo = sPointNo.substring(0 , 2);
				} else {
					sPointNo = ydGp+"X";
				}
				
				//코일 공통 상차백업 위치로 위치 변경 작업 
				JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
				recInTemp1.setResultCode(logId);	//Log ID
				recInTemp1.setResultMsg(methodNm);	//Log Method Name
				recInTemp1.setField("STOCK_ID"	, StockId       ); //재료번호
				recInTemp1.setField("YD_LOC"	, sPointNo + "PT010101"); //현재위치
				
				EJBConnector ejbConn1 = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				ejbConn1.trx("UpdCoilComLoc", new Class[] { JDTORecord.class }, new Object[] { recInTemp1 });
				
				
				/*************************
				 * 작업실적에 저장
				 *************************/
				JDTORecord jparam = JDTORecordFactory.getInstance().create();
				
				jparam.setField("SCH_ID"           , "000000000000000000");
				jparam.setField("STOCK_ID"         , StockId);
				jparam.setField("EQUIP_GP"         , sPointNo.substring(1, 2)+YmConstant.EQUIP_KIND_CR + "00");
				jparam.setField("YD_SCH_CD"        , "3X9999");
				jparam.setField("SCRANE_WORK_DUTY" , YmCommUtils.getWorkDuty());  
				jparam.setField("SCRANE_WORK_PARTY", YmCommUtils.getWorkParty()); 
				jparam.setField("SCH_WDEMAND_DUTY" , YmCommUtils.getWorkDuty());  
				jparam.setField("SCH_WDEMAND_PARTY", YmCommUtils.getWorkParty()); 
				jparam.setField("UP_LOC"           , sYD_STK_LOC);
				jparam.setField("PUT_LOC"          , sPointNo + "PT010101");
				jparam.setField("UP_FUNC"          , YmConstant.CRANE_FUNC_S);
				jparam.setField("PUT_FUNC"         , YmConstant.CRANE_FUNC_S);
				jparam.setField("YD_GP"            , "3");
				jparam.setField("REGISTER"         , modifier);
				jparam.setField("MODIFIER"         , modifier);
				
				commDao.insert(jparam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insertCrnWrslt", logId, methodNm, "작업실적 저장");
			}
		
			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;
			 
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of rcvDMYDR030()	
	

	/**
	 * 코일제품반품
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR033(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일제품반품 수신[BCoilL3RcvSeEJB.rcvDMYDR033] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		String szMsg = "";
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId    		= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlAppearGp 		= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));   //재료외형
			String StockId 			= commUtils.trim(rcvMsg.getFieldString("STL_NO"));    		//재료번호
			String distGoodsGp  	= commUtils.trim(rcvMsg.getFieldString("DIST_GOODS_GP"));   //출하제품구분 H:코일, T:HRPLATE
			String oldTransOrdDt  	= commUtils.trim(rcvMsg.getFieldString("OLD_TRANS_WORD_DATE"));  //구운송지시일자
			String oldTransOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("OLD_TRANS_WORD_SEQNO"));
			String newTransOrdDt  	= commUtils.trim(rcvMsg.getFieldString("NEW_TRANS_WORD_DATE"));  //신운송지시일자
			String newTransOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("NEW_TRANS_WORD_SEQNO"));
			String modifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"H".equals(distGoodsGp)) {
				throw new Exception("출하제품구분이상 (H)가 아닙니다...");
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"  		, modifier  ); //수정자
			jrParam.setField("DEL_YN"  		    , "N" );	   //
			jrParam.setField("STOCK_ID"			, StockId);
			
			String[] rVal = new String[1];
			rVal= commUtils.getYdAimRtGp("C",jrParam );		
			jrParam.setField("YD_AIM_RT_GP", rVal[0]);

			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStock
             UPDATE USRYMA.TB_YM_STOCK
				SET DEL_YN=:V_DEL_YN
				  , MODIFIER=:V_MODIFIER  
				  , MOD_DDTT =SYSDATE
				  , YD_AIM_RT_GP = :V_YD_AIM_RT_GP
				WHERE STOCK_ID=:V_STOCK_ID
			*/
			intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStock", logId, methodNm, "TB_YM_STOCK 수정");
			if(intRtnVal <= 0){
				throw new Exception("YM_STOCK[코일제품반품] UPDATE Error :: [" + intRtnVal + "]");
			}
			
			jrParam.setField("TRANS_ORD_DATE"  , oldTransOrdDt);
			jrParam.setField("TRANS_ORD_SEQNO" , oldTransOrdSeqNo);
			
			//=====================================================================================================
			// 2013.04.11
			// 정종균
			// 1.저장품 운송지시 변경
			// 2.차량스케줄 운송지시 변경
			// 3.검수운송지시 변경 및 재검수 상태로 변경
			//=====================================================================================================			
			/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdStockTRANS_ORD_DAT
			SELECT STOCK_ID 
			  FROM TB_YM_STOCK A
			 WHERE TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE
			   AND TRANS_ORD_SEQNO2  = :V_TRANS_ORD_SEQNO
			   AND DEL_YN='N'
				 */                                                     				
			JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdStockTRANS_ORD_DAT", logId, methodNm, "(운송일자, 운송순번)로 저장품 조회");

		    if(jsStock.size() > 0){
		    	commUtils.printLog(logId, methodNm+ "[코일제품반품(DMYDR033)] 이전 운송지시번호로 변경 대상이 존재 함", "SL");
		    	
				// 레코드생성
				jrParam.setField("OLD_TRANS_WORD_DATE"			, oldTransOrdDt);
				jrParam.setField("OLD_TRANS_WORD_SEQNO"			, oldTransOrdSeqNo);
				jrParam.setField("NEW_TRANS_WORD_DATE"			, newTransOrdDt);
				jrParam.setField("NEW_TRANS_WORD_SEQNO"			, newTransOrdSeqNo);
				//--------------------------------------------------------------------------------
				//	차량스케줄 운송지시 변경
				//--------------------------------------------------------------------------------
				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarschTransOrd 
				UPDATE USRYDA.TB_YD_CARSCH
				   SET MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				     , TRANS_ORD_DATE  = :V_NEW_TRANS_WORD_DATE
				     , TRANS_ORD_SEQNO = :V_NEW_TRANS_WORD_SEQNO
				 WHERE TRANS_ORD_DATE  = :V_OLD_TRANS_WORD_DATE
				   AND TRANS_ORD_SEQNO = :V_OLD_TRANS_WORD_SEQNO
				*/   
				
				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarschTransOrd", logId, methodNm, "TB_YD_CARSCH 차량스케줄 운송지시 변경");
				
				//--------------------------------------------------------------------------------
				//	검수재료 운송지시 변경
				//--------------------------------------------------------------------------------
				/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdExamTransOrd
				UPDATE USRYDA.TB_YD_EXAMINATIONCHKLIST
				   SET TRANS_ORD_DATE = :V_NEW_TRANS_WORD_DATE
				     , TRANS_ORD_SEQNO = :V_NEW_TRANS_WORD_SEQNO
				     , MODIFIER  = :V_MODIFIER
				     , MOD_DDTT = sysdate
				     , DEL_YN = 'N'
				     , CHECKING_YN = 'N'
				     , LABEL_YN = NULL
				     , YD_AB_CD = NULL
				 WHERE TRANS_ORD_DATE  = :V_OLD_TRANS_WORD_DATE
				   AND TRANS_ORD_SEQNO = :V_OLD_TRANS_WORD_SEQNO
				*/   
				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdExamTransOrd", logId, methodNm, "TB_YD_EXAMINATIONCHKLIST 검수재료 운송지시 변경");

				//--------------------------------------------------------------------------------
				//	재료정보 운송지시 변경
				//--------------------------------------------------------------------------------
				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYmStockTransOrd
				UPDATE USRYMA.TB_YM_STOCK
				   SET TRANS_WORD_NO=:V_NEW_TRANS_WORD_DATE||:V_NEW_TRANS_WORD_SEQNO
				     , MODIFIER =:V_MODIFIER
				     , MOD_DDTT=sysdate
				     , TRANS_ORD_DATE2  = :V_NEW_TRANS_WORD_DATE
				     , TRANS_ORD_SEQNO2 = :V_NEW_TRANS_WORD_SEQNO
				 WHERE TRANS_ORD_DATE2  = :V_OLD_TRANS_WORD_DATE
				   AND TRANS_ORD_SEQNO2 = :V_OLD_TRANS_WORD_SEQNO
				*/
				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYmStockTransOrd", logId, methodNm, "TB_YM_STOCK 재료정보 운송지시 변경");
			}
			//---------------------------------------------------------------------------- 
		
			//차량스케줄ID 조회--------------------------------------------------------------
		    /* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCarschByTransDTSeq 
		    SELECT YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
		      FROM TB_YD_CARSCH
		     WHERE TRANS_ORD_DATE  = :V_NEW_TRANS_WORD_DATE
		       AND TRANS_ORD_SEQNO = :V_NEW_TRANS_WORD_SEQNO
		       AND DEL_YN='N'
		    */
			String ydCarSchId = "";
			JDTORecordSet jsCarsch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCarschByTransDTSeq", logId, methodNm, "(운송일자, 운송순번)로 TB_YD_CARSCH 조회");			
			if(jsCarsch.size() <= 0 ){
				
				szMsg = "["+methodNm+"] 운송지시일자 :["+newTransOrdDt+"] , 운송지시순번["+newTransOrdSeqNo+"]로 차량스케줄 조회 시 오류발생 - 메세지 : " + jsCarsch.size();
				commUtils.printLog(logId, szMsg, "SL");	
				return jrRtn ;
			}else{
				
				ydCarSchId = commUtils.trim(jsCarsch.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
			}			
			//--------------------------------------------------------------------------------
			
			
			//1.차량스케줄 재료 삭제--------------------------------------------------------------
			JDTORecord recPara =  JDTORecordFactory.getInstance().create();			
			recPara.setField("YD_CAR_SCH_ID", 	ydCarSchId);
			recPara.setField("STL_NO", 			StockId);
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.deleteYdCarftmvmtl 
			DELETE FROM USRYDA.TB_YD_CARFTMVMTL
			 WHERE YD_CAR_SCH_ID=:V_YD_CAR_SCH_ID
			   AND STL_NO=:V_STL_NO
			*/   
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.deleteYdCarftmvmtl", logId, methodNm, "TB_YD_CARFTMVMTL 삭제");
			
			//2.검수재료 삭제--------------------------------------------------------------------
			recPara.setField("TRANS_ORD_DATE", 			newTransOrdDt);
			recPara.setField("TRANS_ORD_SEQNO", 		newTransOrdSeqNo);
 			
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.deleteYdExaminationmtl 
			DELETE FROM USRYDA.TB_YD_EXAMINATIONCHKLIST
			 WHERE TRANS_ORD_DATE=:V_TRANS_ORD_DATE
			   AND TRANS_ORD_SEQNO=:V_TRANS_ORD_SEQNO
			   AND STL_NO =:V_STL_NO
			*/
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.deleteYdExaminationmtl", logId, methodNm, "TB_YD_EXAMINATIONCHKLIST 삭제");
				
			//3.저장품재료 --------------------------------------------------------------------
			
			jrParam.setField("TRANS_ORD_DATE2"	, "");
			jrParam.setField("TRANS_ORD_SEQNO2"	, "");
			jrParam.setField("CAR_NO2"          , "");
			jrParam.setField("CAR_CARD_NO"      , "");
			jrParam.setField("WBOOK_ID"         , "");
			jrParam.setField("STOCK_ID"         , StockId);
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStock
			UPDATE USRYMA.TB_YM_STOCK
			   SET MODIFIER         = :V_MODIFIER
			     , MOD_DDTT         = SYSDATE
			     , STOCK_MOVE_TERM  = NVL(:V_STOCK_MOVE_TERM , STOCK_MOVE_TERM)   --저장품이동조건
			     , YD_RULE_PL_RS_GP = NVL(:V_YD_RULE_PL_RS_GP, YD_RULE_PL_RS_GP)  --조합구분
			     , TRANS_WORD_NO    = :V_TRANS_ORD_DATE2||:V_TRANS_ORD_SEQNO2
			     , TRANS_ORD_DATE2  = NVL(:V_TRANS_ORD_DATE2 , TRANS_ORD_DATE2)   --운송지시
			     , TRANS_ORD_SEQNO2 = NVL(:V_TRANS_ORD_SEQNO2, TRANS_ORD_SEQNO2)  --운송지시행번
			     , SHEAR_SUPPLY_SEQ = NVL(:V_SHEAR_SUPPLY_SEQ, SHEAR_SUPPLY_SEQ)  --차상위치
			     , CAR_NO2          = NVL(:V_CAR_NO2         , CAR_NO2)           --차량번호 
			     , CAR_CARD_NO      = NVL(:V_CAR_CARD_NO     , CAR_CARD_NO)       --카드번호
			     , SHEAR_SUPPLY_GP  = NVL(:V_SHEAR_SUPPLY_GP , SHEAR_SUPPLY_GP)   --차량종류
			     , WBOOK_ID         = NVL(:V_WBOOK_ID        , WBOOK_ID)          --작업예약 사용안함
			 WHERE STOCK_ID = :V_STOCK_ID
			 */
			intRtnVal=	commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStock", logId, methodNm, "TB_YM_STOCK 수정");

			//======================================================
			// 저장품제원 : 코일야드L2 로 송신(YMA7L002)
			//======================================================
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);	//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
			sndL2Msg.setField("YD_INFO_SYNC_CD"	, "5"       ); //야드정보동기화코드
			sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
			sndL2Msg.setField("STOCK_ID"       	, StockId ); //재료번호
				
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L002", sndL2Msg));	 //전송 Data 생성	
			
			
			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;
			 
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of rcvDMYDR033()
	

	/**
	 * 코일이송상차대기장도착PDA(DMYDR070) 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR070(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일이송상차대기장도착PDA 수신[BCoilL3RcvSeEJB.rcvDMYDR070] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlAppearGp 		= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));    //재료외형
			String transOrdDt  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			String transOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String cancelYn     	= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));
			String ydCarKind		= commUtils.trim(rcvMsg.getFieldString("CAR_KIND"));
			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
			String ydCardNo 		= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
			String transFrtoMoveGp	= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP")); 	//1 운송 2 이송
			String WorkGp 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP"));   			// 작업구분
			String CarldPntCd		= commUtils.trim(rcvMsg.getFieldString("CARLD_PNT_CD"));   		// 상차포인트
			
			int ydEqpWrkSh 			= Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0"));
			String modifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String dmTcCode 		= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));//TC_CODE
			
			String ugntBayInYn		= commUtils.trim(rcvMsg.getFieldString("UGNT_BAYIN_YN"));  	// 긴급입동유무 (Y:입동순서 1)
			
	    	// PIDEV
//	    	String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "*", "*");			
			
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/

			if (("Y".equals(cancelYn))) {
				//JDTORecord recInPara = JDTORecordFactory.getInstance().create();
				//sndRecord = this.receiveCancel(logId, methodNm,recInPara,sndRecord);
				// 또는 this.receiveCancel(logId, methodNm,rcvMsg);
				return jrRtn;
			}else{
				
			   /**********************************************************
				* 2. 저장품 이동 조건 수정
				**********************************************************/
				JDTORecord jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
	
				//jrParam.setField("STOCK_MOVE_TERM"	, StMvTerm); //저장품이송조건

				jrParam.setField("TRANS_ORD_DATE2" 	, transOrdDt); 						
				jrParam.setField("TRANS_ORD_SEQNO2" , transOrdSeqNo); 						
				jrParam.setField("CAR_NO2" 			, ydCarNo); 
				jrParam.setField("CAR_CARD_NO" 		, ydCardNo); 
				jrParam.setField("CR_FRTOMOVE_GP" 	, transFrtoMoveGp); 
				jrParam.setField("MODIFIER" 		, modifier); 						
				
				for(int i = 1 ; i<=ydEqpWrkSh; i++){				
		
					//jrParam.setField("STOCK_MOVE_TERM"	, StMvTerm); //저장품이송조건
					jrParam.setField("STOCK_ID" 		, commUtils.trim(rcvMsg.getFieldString("STL_NO"+i))); //저장품 ID
					jrParam.setField("TC_CD"			, dmTcCode);  //TC_CODE
					
					JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);

					jrParam.setField("STOCK_MOVE_TERM" 	, commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"))); 
					jrParam.setField("SHEAR_SUPPLY_SEQ" , commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i))); //차상위치
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStock
					UPDATE USRYMA.TB_YM_STOCK
					   SET MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					     , STOCK_MOVE_TERM  = NVL(:V_STOCK_MOVE_TERM , STOCK_MOVE_TERM)   --저장품이동조건
					     , YD_RULE_PL_RS_GP = NVL(:V_YD_RULE_PL_RS_GP, YD_RULE_PL_RS_GP)  --조합구분
					     , TRANS_WORD_NO    = :V_TRANS_ORD_DATE2||:V_TRANS_ORD_SEQNO2
					     , TRANS_ORD_DATE2  = NVL(:V_TRANS_ORD_DATE2 , TRANS_ORD_DATE2)   --운송지시
					     , TRANS_ORD_SEQNO2 = NVL(:V_TRANS_ORD_SEQNO2, TRANS_ORD_SEQNO2)  --운송지시행번
					     , SHEAR_SUPPLY_SEQ = NVL(:V_SHEAR_SUPPLY_SEQ, SHEAR_SUPPLY_SEQ)  --차상위치
					     , CAR_NO2          = NVL(:V_CAR_NO2         , CAR_NO2)           --차량번호 
					     , CAR_CARD_NO      = NVL(:V_CAR_CARD_NO     , CAR_CARD_NO)       --카드번호
					     , SHEAR_SUPPLY_GP  = NVL(:V_SHEAR_SUPPLY_GP , SHEAR_SUPPLY_GP)   --차량종류
					     , WBOOK_ID         = NVL(:V_WBOOK_ID        , WBOOK_ID)          --작업예약 사용안함
					     , CR_FRTOMOVE_GP   = NVL(:V_CR_FRTOMOVE_GP  , CR_FRTOMOVE_GP)    -- 냉연이송구분       
					 WHERE STOCK_ID = :V_STOCK_ID
					*/
					intRtnVal=	commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStock", logId, methodNm, "TB_YM_STOCK 수정");
						
		            if(intRtnVal == 0){
		           	   continue;
							//throw new Exception("수신한 재료번호 ["+ StStlNo+"]에 대한 저장품 DATA가 존재하지 않음");
					}
				}	
				
				//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
				if(WorkGp.equals("9")){
					//차량정보 존재여부 체크 /////////////////////////////////////////////////////////////////
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("MODIFIER" 		, modifier); 	
					jrParam.setField("TRANS_ORD_DT"		, transOrdDt);
					jrParam.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);
					
			    	// PIDEV				
//					if("Y".equals(sApplyYnPI)) {
						jrParam.setField("CAR_NO"			, ydCarNo);	
//					} else {					
//						jrParam.setField("CARD_NO"			, ydCardNo);
//					}
					
					jrParam.setField("YD_CARPNT_CD"		, CarldPntCd);
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarYdCmbnCarldYn_PIDEV 
					SELECT *
					  FROM TB_YD_CARSCH
					 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DT
					   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
					   AND CARD_NO         = :V_CARD_NO
--					   AND CMBN_CARLD_YN   = :V_CMBN_CARLD_YN
					   AND DEL_YN   = 'N'	
					*/	   
					JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarYdCmbnCarldYn_PIDEV", logId, methodNm, "차량스케쥴 조회");
					if(jsCarSch.size() > 0) {
						commUtils.printLog(logId, methodNm + " TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.:기존지시 삭제 " , "SL");	
						
						String ydOldCarSchId = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID")); //차량 작업지시
					 
						jrParam.setField("YD_CAR_SCH_ID", ydOldCarSchId);
						jrParam.setField("DEL_YN"       , "Y");
						
						/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch
						UPDATE TB_YD_CARSCH
						   SET MODIFIER = :V_MODIFIER
						     , MOD_DDTT = SYSDATE
						     , DEL_YN = :V_DEL_YN
						 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID 
						*/
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch", logId, methodNm, "TB_YD_CARSCH 차량 스케줄정보");
					}
					
					//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////////////////				
					String ydCarSchId = commDao.getSeqId(logId, methodNm, "CarSch");
					
					
					//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////
					//차량정보 존재여부 체크 /////////////////////////////////////////////////////////////////

					/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarPoint
					SELECT SUBSTR(YD_STK_COL_GP,1,4)||'0'||SUBSTR(YD_STK_COL_GP,6,1)  AS YD_STK_COL_GP2
					     , DECODE(A.YD_STK_COL_ACT_STAT,'R','C',YD_STK_COL_ACT_STAT)  AS YD_STK_COL_ACT_STAT
					     , YD_CARPNT_CD
					     , REG_DDTT
					     , REGISTER
					     , MOD_DDTT
					     , MODIFIER
					     , DEL_YN
					     , YD_CAR_USETYPE_GP
					     , YD_GP
					     , YD_BAY_GP
					     , YD_STK_COL_GP
					     , TRN_EQP_CD
					     , CAR_NO
					     , CARD_NO
					     , WLOC_CD
					     , YD_PNT_CD
					     , YD_CARPNT_DESC
					     , YD_SPAN_FROM
					     , YD_SPAN_TO
					  FROM TB_YD_CARPOINT A
					 WHERE YD_CARPNT_CD=:V_YD_CARPNT_CD	
					*/	   
					JDTORecordSet jsCarPnt = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarPoint", logId, methodNm, "차량스케쥴 조회");
					String ydWlocCd 	= "";
					String ydStkColGp   = "";
					String ydPntCd      = "";
					String szMovYn      = "";

					if(jsCarPnt.size() > 0) {

						ydWlocCd 	= commUtils.trim(jsCarPnt.getRecord(0).getFieldString("WLOC_CD"));
						ydStkColGp	= commUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_GP"));
						ydPntCd     = commUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_PNT_CD"));
						szMovYn     = commUtils.trim(jsCarPnt.getRecord(0).getFieldString("MOV_YN")); 
	
						JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_CAR_SCH_ID"		, ydCarSchId);
						recInTemp.setField("REGISTER"			, modifier);
						recInTemp.setField("YD_EQP_ID"			, YmConstant.YD_DM_CAR_EQP_ID);		//야드설비ID
						recInTemp.setField("YD_CAR_USE_GP"		, YmConstant.YD_CAR_USE_GP_DM);		//차량사용구분 
						recInTemp.setField("CAR_NO"				, ydCarNo);							//차량번호
						recInTemp.setField("CAR_KIND"			, ydCarKind);						//차량종류
						recInTemp.setField("YD_EQP_WRK_STAT"	, "U");								//야드설비작업상태
						recInTemp.setField("SPOS_WLOC_CD"		, ydWlocCd);						//발지개소코드
						recInTemp.setField("YD_CARLD_LEV_DT"	, commUtils.getDateTime14());		//상차출발일시
						recInTemp.setField("YD_PNT_CD1"			, ydPntCd);							//야드포인트코드1
						recInTemp.setField("YD_CARLD_STOP_LOC"	, ydStkColGp);						//야드상차정지위치
				    	// PIDEV
//						if ("N".equals(sApplyYnPI)) {
//							recInTemp.setField("CARD_NO"		, ydCardNo);						//카드번호
//						}
						recInTemp.setField("YD_CAR_PROG_STAT"	, "1");								//상차출발상태
						recInTemp.setField("YD_CAR_WRK_GP"		, WorkGp);
						recInTemp.setField("TRANS_ORD_DATE"		, transOrdDt);						//운송지시일자
						recInTemp.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);					//운송지시순번 
						
						if ("Y".equals(ugntBayInYn)) {
							//긴급입동유무 (Y:입동순서 1)
							recInTemp.setField("YD_BAYIN_WO_SEQ", "1"); //입동지시순번 - 1순위	
						} else {
							if("Y".equals(szMovYn)){
								//배차차량작업관리화면 -> 제품이송우선순위 사용함 일경우
								recInTemp.setField("YD_BAYIN_WO_SEQ", "1"); //입동지시순번 - 1순위
							} else {
								recInTemp.setField("YD_BAYIN_WO_SEQ", YmConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 - 기본값으로 설정(9)
							}
						}
						if(ydCarKind.equals("TT")){
							recInTemp.setField("CAR_KIND",          "TT");									//차량종류
						}else{
							recInTemp.setField("CAR_KIND",          "TR");									//차량종류
						}
						recInTemp.setField("TRANS_EQUIPMENT_TYPE", 	"P");			//운송장비타입 P : PDA

						recInTemp.setField("TEL_NO",   		rcvMsg.getFieldString("TEL_NO"));						//연락처
						recInTemp.setField("DRIVER_NAME",  	rcvMsg.getFieldString("DRIVER_NAME"));						//운전기사명
						
			    		//차량스케줄 등록
						
						/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insYdCarsch_PIDEV
						INSERT INTO USRYDA.TB_YD_CARSCH
						(	   YD_CAR_SCH_ID
						     , REGISTER
						     , REG_DDTT	
						     , DEL_YN
						     , YD_EQP_ID
						     , YD_CAR_USE_GP
						     , CAR_NO
						     , TRN_EQP_CD
						     , CAR_KIND
						     , YD_EQP_WRK_STAT
						     , SPOS_WLOC_CD
						     , YD_CARLD_LEV_LOC
						     , YD_CARLD_LEV_DT
						     , YD_PNT_CD1
						     , YD_CARLD_STOP_LOC
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
						     , DRIVER_NAME
						       )
						VALUES (
						       :V_YD_CAR_SCH_ID
						     , :V_REGISTER
						     , SYSDATE
						     , 'N'
						     , :V_YD_EQP_ID
						     , :V_YD_CAR_USE_GP
						     , :V_CAR_NO
						     , :V_TRN_EQP_CD
						     , :V_CAR_KIND
						     , :V_YD_EQP_WRK_STAT
						     , :V_SPOS_WLOC_CD
						     , :V_YD_CARLD_LEV_LOC
						     , :V_YD_CARLD_LEV_DT
						     , NVL(:V_YD_PNT_CD1,'0000')
						     , :V_YD_CARLD_STOP_LOC
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
						     , :V_DRIVER_NAME
						       )
						 */    
				    	// PIDEV
//						if ("Y".equals(sApplyYnPI)) {
							commDao.insert(recInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insYdCarsch_PIDEV", logId, methodNm, "TB_YD_CARSCH 등록");
//						} else {
//							commDao.insert(recInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insYdCarsch", logId, methodNm, "TB_YD_CARSCH 등록");
//						}

//						for(int i = 1 ; i<=ydEqpWrkSh; i++){	
//							recInTemp = JDTORecordFactory.getInstance().create();
//							recInTemp.setField("YD_CAR_SCH_ID"	, ydCarSchId);
//							recInTemp.setField("MODIFIER"		, modifier);
//							recInTemp.setField("STL_NO"			, commUtils.trim(rcvMsg.getFieldString("STL_NO"+i)));	 
//							
//							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarSchmtl 
//							INSERT INTO TB_YD_CARFTMVMTL(
//			
//							       YD_CAR_SCH_ID
//							     , STL_NO
//							     , REGISTER
//							     , REG_DDTT
//							     , MODIFIER
//							     , MOD_DDTT
//							     , DEL_YN
//							--     , YD_CAR_UPP_LOC_CD
//							--     , YD_STK_BED_NO
//							--     , YD_STK_LYR_NO
//							--     , HCR_GP
//							--     , STL_PROG_CD
//							--     , YD_MTL_ITEM
//							--     , YD_ROUTE_GP
//							) SELECT :V_YD_CAR_SCH_ID
//							       , :V_STL_NO
//							       , :V_MODIFIER
//							       , SYSDATE
//							       , :V_MODIFIER
//							       , SYSDATE
//							       , 'N'
//							    FROM DUAL
//						   */	    
//							
//							commDao.insert(recInTemp, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarSchmtl", logId, methodNm, "차량재료 스케쥴 INSERT ");					 
//				    		
//						}	
			    		
			    		if(ydCarKind.equals("TT")){
			    			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					        EJBConnector ejbConn3 = new EJBConnector("default","YmCommCarMvSeEJB",this);
							ejbConn3.trx("YmCarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
						  	             new Object[]{"C",ydCarNo,ydCardNo,ydStkColGp,"","","R"});
			    		}
					}else {
						commUtils.printLog(logId, methodNm + "TB_YD_CARPOINT[차량포인트가 존재 안합니다..]" , "SL");
					}
					
					//입동지시요구모듈 호출(trailer인 경우)///////////////////////////////////////////////////////
					if(ydCarKind.equals("T") || ydCarKind.equals("TR")){
						
						//-------------------------------------------------------------------------------------------------------------
						// A동 2통로 출하대상 자동이적 YMYMJ312
						String sAPPLY060 = ymComm.BCoilApplyYn("APP060","3","A3TOA4_DM");
						commUtils.printLog(logId,  ">>> A동 2통로 출하작업 고도화 적용 (Y:적용, N:비적용) :" + sAPPLY060, "SL");
						if (sAPPLY060.equals("Y")) {
							
							jrParam.setField("TRANS_ORD_DATE" 	, transOrdDt);
							jrParam.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);
							
							JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getListA3toA4Dm", logId, methodNm, "A동 2통로 출하 자동이적 대상 리스트 조회");
							
							if (rsResult.size() > 0) {
								//A4 크레인이 갈수 없는 영역에 출하 대상이 존재하면 자동이적  YMYMJ312 호출 
							
								JDTORecord jrYMYMJ312 = JDTORecordFactory.getInstance().create();
								jrYMYMJ312.setResultCode(logId);	//Log ID
								jrYMYMJ312.setResultMsg(methodNm);	//Log Method Name
								jrYMYMJ312.setField("JMS_TC_CD" 		, "YMYMJ312");
								jrYMYMJ312.setField("TRANS_ORD_DATE" 	, transOrdDt);
								jrYMYMJ312.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);
								jrYMYMJ312.setField("MODIFIER"	        , modifier);
								
								//jrRtn = commUtils.addSndData(jrRtn, jrYMYMJ312);
								jrRtn = this.rcvYMYMJ312(jrYMYMJ312);
							} 
						} 
						//-------------------------------------------------------------------------------------------------------------
						
						
						/*
						 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
						 */
						commUtils.printLog(logId, "차량정지위치[" + ydStkColGp + "], 차량스케줄ID[" + ydCarSchId + "] -PDA AB차량입동지시요구 모듈을 호출 시작" , "SL");
						
						JDTORecord recInTemp = JDTORecordFactory.getInstance().create();	
						recInTemp.setResultCode(logId);	//Log ID
						recInTemp.setResultMsg(methodNm);	//Log Method Name
						recInTemp.setField("JMS_TC_CD"			, "YMYMJ662");          //차량입동지시 요구 기존:YDYDJ662
						recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
						recInTemp.setField("YD_CARPNT_CD"		, CarldPntCd);
						recInTemp.setField("YD_CAR_SCH_ID"		, ydCarSchId);
						//recInTemp.setField("CHK_YN"				, "N");
						recInTemp.setField("CR_FRTOMOVE_GP"		, transFrtoMoveGp); //냉연이송구분
						recInTemp.setField("TRANS_ORD_DATE"		, transOrdDt); 		//운송지시일자
						recInTemp.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo); 	//운송지시순번
						
						jrRtn = commUtils.addSndData(jrRtn, recInTemp);
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
	/**
	 * 코일이송상차도착PDA(DMYDR071) 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR071(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일이송상차도착PDA 수신[BCoilL3RcvSeEJB.rcvDMYDR071] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrRst = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String transOrdDt  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); //운송실적일자
			String transOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));  //운송실적순번
			String StockId          = ""; 
			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
			String ydCardNo 		= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
			String WorkGp 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP"));   			// 작업구분
			String ydCarKind		= commUtils.trim(rcvMsg.getFieldString("CAR_KIND"));
			String CarldPntCd		= commUtils.trim(rcvMsg.getFieldString("CARLD_PNT_CD"));   		// 상차포인트
			String modifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String dmTcCode 		= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));//TC_CODE
			String transFrtoMoveGp	= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP")); 	//1 운송 2 이송
			String szMsg            = "";
			String ydCarSchId       = "";
			String ydCarProgStat    = "";
			String ydCarWrkGp 		= ""; 	//야드차량작업구분
			String ydEqpWrkStat		= ""; 	//야드설비작업상태
			if ("".equals(modifier)) { modifier = msgId; }

			String sAPPLY = ymComm.BCoilApplyYn("APP003","3","1");
			commUtils.printLog(logId,  "차량ERROR LOG 처리:" + sAPPLY, "SL");	
			
		   /**********************************************************
			* 2. 운송실적번호로 제품번호 가져오기
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("TRANS_ORD_DATE"	, transOrdDt);		
			jrParam.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);		

			//제품번호조회--------------------------------------------------------------
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getTransStockInfo 
			 SELECT A.STOCK_ID 
			      , (SELECT WLOC_CD 
			           FROM USRYDA.TB_YD_CARPOINT C
			              , USRYMA.TB_YM_STACKLAYER B
			          WHERE A.STOCK_ID=B.STOCK_ID  
			            AND SUBSTR(B.STACK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
			            AND C.YD_STK_COL_GP LIKE SUBSTR(B.STACK_COL_GP,1,2)||'%'
			            AND ROWNUM<=1 ) AS WLOC_CD
			      , (SELECT SUBSTR(YD_STK_COL_GP,1,4)||'0'||SUBSTR(YD_STK_COL_GP,6,1) 
			           FROM USRYDA.TB_YD_CARPOINT C
			              , USRYMA.TB_YM_STACKLAYER B
			          WHERE A.STOCK_ID=B.STOCK_ID  
			            AND SUBSTR(B.STACK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
			            AND C.YD_STK_COL_GP LIKE SUBSTR(B.STACK_COL_GP,1,2)||'%'
			            AND YD_STK_COL_ACT_STAT IN('C','R')
			            AND ROWNUM<=1 ) AS YD_STK_COL_GP   
			      , A.CAR_CARD_NO
			      , (SELECT YD_CAR_SCH_ID FROM USRYDA.TB_YD_CARSCH
			          WHERE DEL_YN='N'
			            AND TRANS_ORD_DATE=A.TRANS_ORD_DATE2
			            AND TRANS_ORD_SEQNO=A.TRANS_ORD_SEQNO2
			            AND ROWNUM<=1
			        ) AS YD_CAR_SCH_ID
			      , (SELECT YD_CAR_PROG_STAT FROM USRYDA.TB_YD_CARSCH
			          WHERE DEL_YN='N'
			            AND TRANS_ORD_DATE=A.TRANS_ORD_DATE2
			            AND TRANS_ORD_SEQNO=A.TRANS_ORD_SEQNO2
			            AND ROWNUM<=1
			        ) AS YD_CAR_PROG_STAT
			   FROM TB_YM_STOCK A      
			  WHERE A.TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE
			    AND A.TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
			    AND A.DEL_YN = 'N'
		    */
	 
			JDTORecordSet jsStlno = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getTransStockInfo", logId, methodNm, "운송실적번호에 맞는 제품번호가 존재 조회");			
			if(jsStlno.size() <= 0 ){
				szMsg = "["+methodNm+"] [코일제품상차지시등록] 운송실적번호에 맞는 제품번호가 존재 안함: TRANS_WORD_NO:["+transOrdDt+transOrdSeqNo+"]" ;
				commUtils.printLog(logId, szMsg, "SL");	
				throw new Exception("[코일제품상차지시등록] [차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다....");
			} else {
				
				StockId 		= commUtils.trim(jsStlno.getRecord(0).getFieldString("STOCK_ID"));
				ydCarSchId 		= commUtils.trim(jsStlno.getRecord(0).getFieldString("YD_CAR_SCH_ID")); 	//차량 작업지시

				jrParam.setField("STOCK_ID", StockId);
				
				//------------------------------------------------------------------------------------------------------------
		    	//	이적 및 대차출하 작업예약 존재 여부 CHECK
		    	//------------------------------------------------------------------------------------------------------------
				jrParam.setField("TRANS_ORD_DT", 			transOrdDt);
				jrParam.setField("TRANS_ORD_SEQNO", 		transOrdSeqNo);
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockWbookcheck 
				SELECT B.YD_WBOOK_ID
				  FROM USRYMA.TB_YM_STOCK A
				     , USRYMA.TB_YM_WRKBOOK B
				     , USRYMA.TB_YM_WRKBOOKMTL C
				 WHERE B.YD_WBOOK_ID=C.YD_WBOOK_ID
				   AND A.TRANS_ORD_DATE2=:V_TRANS_ORD_DT
				   AND A.TRANS_ORD_SEQNO2=:V_TRANS_ORD_SEQNO
				   AND A.STOCK_ID = C.STOCK_ID
				   AND A.DEL_YN = 'N'
				   AND B.DEL_YN = 'N'
				   AND C.DEL_YN = 'N'
				   AND (SUBSTR(B.YD_SCH_CD,3,2) IN ('YD') OR  SUBSTR(B.YD_SCH_CD,3,4) = 'TC12')                      
				   AND B.YD_WBOOK_ID NOT IN ( SELECT YD_WBOOK_ID FROM TB_YM_CRNSCH WHERE DEL_YN = 'N' )
				GROUP BY B.YD_WBOOK_ID     
				 */  
				
				JDTORecordSet jsWbookMtl = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockWbookcheck", logId, methodNm, "작업예약 조회");
			 	if(jsWbookMtl.size() > 0) {
					commUtils.printLog(logId, "이적 및 대차출하  작업예약이 삭제처리 합니다", "SL");
					
					for (int Loop_i = 1; Loop_i <= jsWbookMtl.size() ; Loop_i++) {
						
						jsWbookMtl.absolute(Loop_i);
						JDTORecord jrInPara = JDTORecordFactory.getInstance().create();
						
						jrInPara.setRecord(jsWbookMtl.getRecord());
						jrInPara.setResultCode(logId);			//Log ID
						jrInPara.setResultMsg(methodNm);		//Log Method Name
						jrInPara.setField("MODIFIER" 			, modifier); //수정자
						//크레인 작업예약 삭제
						EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
						ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrInPara });
					}
				}
			 	
			   /**********************************************************
				* 3. 작업예약 등록여부 :
				**********************************************************/		
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookStockYN    
				SELECT DECODE(COUNT(*),0,'N','Y') AS WB_STL_YN --작업예약재료여부
				  FROM TB_YM_WRKBOOKMTL WM
				     , TB_YM_WRKBOOK    WB
				WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
				  AND WM.DEL_YN      = 'N'
				  AND WB.DEL_YN      = 'N'
				  AND WM.STOCK_ID    IN (SELECT STOCK_ID 
				                           FROM TB_YM_STOCK 
				                          WHERE TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE 
				                            AND TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
				                            AND DEL_YN = 'N')
				*/  	  
				JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookStockYN", logId, methodNm, "작업예약 등록여부");

				commUtils.printLog(logId, "작업 예약 편성여부:" + jsChk2.getRecord(0).getFieldString("WB_STL_YN") , "SL");
				if (jsChk2 != null && jsChk2.size() > 0) {
					if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN"))) {
						
						if(sAPPLY.equals("Y")) {						
							/***** 차량log ****/
							JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
							jrLogMsg.setResultCode(logId);	//Log ID
							jrLogMsg.setResultMsg(methodNm);	//Log Method Name
							jrLogMsg.setField("MODIFIER"		, modifier); //수정자 셋팅
							jrLogMsg.setField("YD_MSG_NM"		, "대상 코일 작업중(스케줄확인)"); //메세지
							jrLogMsg.setField("YD_CAR_SCH_ID"	, ydCarSchId); //차량스케쥴
							
							EJBConnector ejbConnLog = new EJBConnector("default", "YmCommSeEJB", this);
							ejbConnLog.trx("updCarErrorLogNew", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });
						}	
						throw new Exception("이미 작업예약되어 있슴  Error : "+ StockId);
					}
				}
				
//		 		if(sAPPLY.equals("Y")) {						
				 	/**********************************************************
					* 1. 운송지시갯수와 LAYER 갯수 확인  CHECK
					**********************************************************/
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStockLayerCntChk 
					SELECT CASE WHEN STOCK_CNT = LAYER_CNT THEN 'Y' --정상
					            ELSE 'N' END AS STOCK_LAYER_CHK     --비정상
					            
					  FROM
					       (
					        SELECT COUNT(*) AS STOCK_CNT
					          FROM TB_YM_STOCK
					         WHERE TRANS_ORD_DATE2  = :V_TRANS_ORD_DT
					           AND TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
					       ) A
					     , (
					        SELECT COUNT(*) AS LAYER_CNT
					          FROM TB_YM_STACKLAYER
					         WHERE STOCK_ID IN (
					                             SELECT STOCK_ID
					                               FROM TB_YM_STOCK
					                              WHERE TRANS_ORD_DATE2  = :V_TRANS_ORD_DT
					                                AND TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
					                           )    
					           AND STACK_LAYER_STAT IN ('C','U')                
					                               
					       ) B
					  WHERE STOCK_CNT <> LAYER_CNT 
					*/
					jrParam.setField("TRANS_ORD_DT"		,transOrdDt);
					jrParam.setField("TRANS_ORD_SEQNO"	,transOrdSeqNo);
					JDTORecordSet jsStockCnt = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStockLayerCntChk", logId, methodNm, "작업대상갯수 조회");
				 	if(jsStockCnt.size() <= 0) {
				 		if(sAPPLY.equals("Y")) {	
							/***** 차량log ****/
							JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
							jrLogMsg.setResultCode(logId);	//Log ID
							jrLogMsg.setResultMsg(methodNm);	//Log Method Name
							jrLogMsg.setField("MODIFIER"		, modifier); //수정자 셋팅
							jrLogMsg.setField("YD_MSG_NM"		, "운송지시갯수와  저장위치 저장품갯수가 틀림:코일정보 확인 "); //메세지
							jrLogMsg.setField("YD_CAR_SCH_ID"	, ydCarSchId); //차량스케쥴
							
							EJBConnector ejbConnLog = new EJBConnector("default", "YmCommSeEJB", this);
							ejbConnLog.trx("updCarErrorLogNew", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });
				 		}	
						throw new Exception("운송지시갯수와  저장위치 저장품갯수가 틀림 : ");
			 		}
			 		
//				}
			}
			
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
			/*			
			 JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCarsch", logId, methodNm, "TB_YD_CARSCH 조회");
			 이미 입동된 카드번호를 가진 차량은 다시 입동되지 않는다. 20180419
			 */
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCarschNot2", logId, methodNm, "TB_YD_CARSCH 조회");
			if(jsCarSch.size() <= 0 ){
				szMsg = "["+methodNm+"] 차량 스케쥴 없음" ;
				throw new Exception("차량 스케쥴 없음 : "+ StockId);
			} else {
				ydCarProgStat 	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT")); //차량진행상태
				ydCarWrkGp 		= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_WRK_GP")); 	//야드차량작업구분
				ydEqpWrkStat	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_EQP_WRK_STAT")); 	//야드설비작업상태
				
			}
		
			
			jrParam.setField("TC_CD"			, msgId);   //
			jrParam.setField("STOCK_ID"			, StockId);   //
			jrParam.setField("MODIFIER" 		, modifier); //수정자
			
			JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
			jrParam.setField("STOCK_MOVE_TERM" 	, commUtils.nvl(jrRtnProg.getFieldString("STOCK_MOVE_TERM"), "LG"));
			jrParam.setField("CAR_CARD_NO"		, ydCardNo);	
			jrParam.setField("MODIFIER"			, modifier);			
			
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock4 
			UPDATE TB_YM_STOCK
			   SET STOCK_MOVE_TERM = (CASE WHEN STOCK_MOVE_TERM ='VL' THEN  'VL'  ELSE :V_STOCK_MOVE_TERM END)
			     , CAR_CARD_NO     = :V_CAR_CARD_NO
			     , MODIFIER        = :V_MODIFIER
			     , MOD_DDTT        = SYSDATE
			 WHERE TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE
			   AND TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
			*/
			intRtnVal =	commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock4", logId, methodNm, "TB_YM_STOCK 수정");
				
            if(intRtnVal <= 0){
            	szMsg = "["+methodNm+"]에 대한 저장품 DATA가 존재하지않음:"+" STOCK_ID:["+StockId+"]"+" TRANS_WORD_NO:["+transOrdDt+transOrdSeqNo+"]"+intRtnVal+"건" ;
            	commUtils.printLog(logId, szMsg, "SL");	
            	throw new Exception(szMsg + StockId);
            	
			}
			
			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if(WorkGp.equals("9")){

				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("MODIFIER" 		, modifier); 	
				jrParam.setField("TRANS_ORD_DT"		, transOrdDt);
				jrParam.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);
				jrParam.setField("CARD_NO"			, ydCardNo);
				jrParam.setField("YD_CARPNT_CD"		, CarldPntCd);

				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarPoint
				SELECT SUBSTR(YD_STK_COL_GP,1,4)||'0'||SUBSTR(YD_STK_COL_GP,6,1)  AS YD_STK_COL_GP2
				     , DECODE(A.YD_STK_COL_ACT_STAT,'R','C',YD_STK_COL_ACT_STAT)  AS YD_STK_COL_ACT_STAT
				     , YD_CARPNT_CD
				     , REG_DDTT
				     , REGISTER
				     , MOD_DDTT
				     , MODIFIER
				     , DEL_YN
				     , YD_CAR_USETYPE_GP
				     , YD_GP
				     , YD_BAY_GP
				     , YD_STK_COL_GP
				     , TRN_EQP_CD
				     , CAR_NO
				     , CARD_NO
				     , WLOC_CD
				     , YD_PNT_CD
				     , YD_CARPNT_DESC
				     , YD_SPAN_FROM
				     , YD_SPAN_TO
				  FROM TB_YD_CARPOINT A
				 WHERE YD_CARPNT_CD=:V_YD_CARPNT_CD	
				*/	   
				JDTORecordSet jsCarPnt = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarPoint", logId, methodNm, "차량포인트 조회");
				if(jsCarPnt.size() > 0) {
					String ydStkColGp		= commUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_GP2"));
					String ydStkColActStat	= commUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));
					String carNo			= commUtils.trim(jsCarPnt.getRecord(0).getFieldString("CAR_NO"));

					// TT-Car가 예약인 경우 차량도착처리 호출이 가능 하도록 조치
					if ("TT".equals(ydCarKind) && "R".equals(ydStkColActStat)) {
						ydStkColActStat = "C";
					}					
					//TR가 도착해 있는 경우 (사용중)
					if ("TR".equals(ydCarKind) && "L".equals(ydStkColActStat)) {
						ydStkColActStat = "C";
					}
					//TR가 도착해 있는 경우  (사용불가)
					if ("TR".equals(ydCarKind) && "N".equals(ydStkColActStat) && ydCarNo.equals(carNo)) {
						ydStkColActStat = "C";
					}
					if("C".equals(ydStkColActStat)){	//사용가능일 때만 수행				
					
						
						JDTORecord jrInTemp = JDTORecordFactory.getInstance().create();	 
						jrInTemp.setResultCode(logId);	//Log ID
					    jrInTemp.setResultMsg(methodNm);	//Log Method Name
						jrInTemp.setField("MODIFIER"     		, modifier  ); //수정자
						jrInTemp.setField("STACK_COL_GP"		, ydStkColGp);
						jrInTemp.setField("CARD_NO"				, ydCardNo);
						jrInTemp.setField("CAR_NO"				, ydCarNo);
						jrInTemp.setField("TRN_EQP_CD"			, "");
						jrInTemp.setField("YD_CAR_PROG_STAT"	, ydCarProgStat);
						jrInTemp.setField("TRANS_EQUIPMENT_TYPE", "P");  //냉연 이송
						
						EJBConnector ejbConn1 = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
						ejbConn1.trx("procYmLayerOpen", new Class[] { JDTORecord.class }, new Object[] { jrInTemp });					
						
						/*************************************** 
						 * 	5.작업예약이 생성 
						 **************************************/	
						//작업예약,작업재료 등록	
						String ydSchCd  = "";
						int  intTransOrdSeqNo   = Integer.parseInt(transOrdSeqNo);
						//2020.07.03 CR_FRTOMOVE_GP(냉연이송구분)의 값이
						//임가공(CR_FRTOMOVE_GP=63), 수출사내(CR_FRTOMOVE_GP=11) 인경우 출하로 판단하여 스케줄코드 생성
						if("11".equals(transFrtoMoveGp) || "63".equals(transFrtoMoveGp))
						{
							//CR_FRTOMOVE_GP(냉연이송구분) 값이 63, 11인경우
							//차량출하
							if ("2".equals(CarldPntCd.substring(1,2)))
							{
								ydSchCd = "3" + ydStkColGp.substring(1,2) + "PT05UM";
							}
							else
							{
								ydSchCd = "3" + ydStkColGp.substring(1,2) + "PT01UM";
							}
						}
						else
						{
							// PIDEV
//							String sApplyYnPI = commDao.ApplyYnPI("", "", "APPPI0", "*", "*");
//							
//							if("Y".equals(sApplyYnPI)) {								
								String[] rVal = commDao.getTrnFrtomoveGpPI("", methodNm, transOrdDt, transOrdSeqNo);
								String sTrnFrtomoveGp = rVal[0];
								String hIssueGp = rVal[1];
								
								commUtils.printLog(logId, "sTrnFrtomoveGp:" + sTrnFrtomoveGp + "," + "hIssueGp:" + hIssueGp, "SL");
								
//								if ("2".equals(CarldPntCd.substring(1,2))) {
//									if( "21".equals(sTrnFrtomoveGp) ) {   //2통로 제품이송상차
//										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT26UM";  
//									} else { //2톨로 냉연이송상차
//										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT16UM";
//									}
//								} else {
//									if("21".equals(sTrnFrtomoveGp)) {   //1통로 제품이송상차
//										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT22UM";
//									} else { //1통로 냉연이송상차
//										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT12UM";
//									}
//								}
								if( "21".equals(sTrnFrtomoveGp) && "K".equals(hIssueGp) ) {   
									if ("2".equals(CarldPntCd.substring(1,2))) {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT26UM";	// 2통로 제품이송상차
									} else {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT22UM";	// 1통로 제품이송상차
									}
								} else if ( "22".equals(sTrnFrtomoveGp) && "9".equals(hIssueGp) ) {
									if ("2".equals(CarldPntCd.substring(1,2))) {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT26UM";	// 2통로 제품이송상차
									} else {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT22UM";	// 1통로 제품이송상차
									}
								} else if ( ("21".equals(sTrnFrtomoveGp) && "C".equals(hIssueGp))
											||
											("21".equals(sTrnFrtomoveGp) && "D".equals(hIssueGp)) ) {
									if ("2".equals(CarldPntCd.substring(1,2))) {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT16UM";	// 2통로 이송출고상차
									} else {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT12UM";	// 1통로 이송출고상차
									}
								} else {
									if ("2".equals(CarldPntCd.substring(1,2))) {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT16UM";
									} else {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT12UM";
									}
								}
							// 이하 sApplyYnPI 적용 전 로직
//							} else {
//								if ("2".equals(CarldPntCd.substring(1,2))) {
//									if(intTransOrdSeqNo > 800000) {   //2통로 제품이송상차
//										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT26UM";  
//									} else { //2톨로 냉연이송상차
//										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT16UM";
//									}
//								} else {
//									if(intTransOrdSeqNo > 800000) {   //1통로 제품이송상차
//										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT22UM";
//									} else { //1통로 냉연이송상차
//										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT12UM";
//									}
//								}
//							}
						}
						
						jrInTemp = JDTORecordFactory.getInstance().create();	 
						jrInTemp.setResultCode(logId);	//Log ID
					    jrInTemp.setResultMsg(methodNm);	//Log Method Name
						jrInTemp.setField("YD_SCH_CD"       , ydSchCd   );// 자동이적 
						jrInTemp.setField("YD_CAR_SCH_ID"	, ydCarSchId);
						jrInTemp.setField("STACK_COL_GP"    , ydStkColGp);
						jrInTemp.setField("CARD_NO"       	, ydCardNo);
						jrInTemp.setField("CAR_NO"       	, ydCarNo);
						jrInTemp.setField("MODIFIER"       	, modifier);
						
		    			String ydWbookId = ymComm.procCarWkBookInsert(jrInTemp);
		    			
		    			if(ydWbookId.equals(YmConstant.RETN_CD_FAILURE)){
		    				throw new Exception("작업예약ID 생성 실패"); 				
		    			}		    	
		    			//----------------------------------------------------------------------
						// 야드저장위치제원(YMA7L001) 전문전송
						//----------------------------------------------------------------------
		    			jrInTemp.setField("YD_INFO_SYNC_CD"	, "3" ); 		//야드정보동기화코드(3:열)
		    			jrInTemp.setField("STACK_COL_GP"   	, ydStkColGp);  //야드적치열구분
						jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA7L001", jrInTemp));
		    			
						//------------------------------------------------------------------------------------------------------------
				    	//	차량스케줄 도착상태 변경 처리
				    	//------------------------------------------------------------------------------------------------------------
			
					    jrInTemp = JDTORecordFactory.getInstance().create();	 
					    jrInTemp.setResultCode(logId);	//Log ID
					    jrInTemp.setResultMsg(methodNm);	//Log Method Name
						jrInTemp.setField("MODIFIER"     		, modifier  ); //수정자
						jrInTemp.setField("YD_CAR_SCH_ID"		, ydCarSchId); 
						
						if(ydEqpWrkStat.equals("L")){
							jrInTemp.setField("YD_CARUD_ARR_DT"	, commUtils.getDateTime14());
							jrInTemp.setField("YD_CAR_PROG_STAT", "B" );		//하차도착상태
						}else{
							jrInTemp.setField("YD_CARLD_ARR_DT"	, commUtils.getDateTime14());
							jrInTemp.setField("YD_CAR_PROG_STAT", "2" );		//상차도착상태
						}
						
						/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch
						UPDATE TB_YD_CARSCH
						   SET MODIFIER         = :V_MODIFIER
						     , MOD_DDTT         = SYSDATE
						     , DEL_YN           = NVL(:V_DEL_YN          , DEL_YN)
						     --하차
						     , YD_CAR_PROG_STAT     = NVL(:V_YD_CAR_PROG_STAT    , YD_CAR_PROG_STAT)
						     , YD_CARUD_WRK_BOOK_ID = NVL(:V_YD_CARUD_WRK_BOOK_ID, YD_CARUD_WRK_BOOK_ID)
						     , YD_CARUD_STOP_LOC    = NVL(:V_YD_CARUD_STOP_LOC   , YD_CARUD_STOP_LOC)
						     , YD_CARUD_ARR_DT      = NVL(:V_YD_CARUD_ARR_DT     , YD_CARUD_ARR_DT)
						     , YD_PNT_CD3           = NVL(:V_YD_PNT_CD3          , YD_PNT_CD3)
						     --상차
						     , YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_CARLD_WRK_BOOK_ID, YD_CARLD_WRK_BOOK_ID)
						     , YD_CARLD_STOP_LOC    = NVL(:V_YD_CARLD_STOP_LOC   , YD_CARLD_STOP_LOC)
						     , YD_CARLD_ARR_DT      = NVL(:V_YD_CARLD_ARR_DT     , YD_CARLD_ARR_DT)
						     , YD_PNT_CD1           = NVL(:V_YD_PNT_CD1          , YD_PNT_CD1)
						 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  

						*/ 
						commDao.update(jrInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch", logId, methodNm, "TB_YD_CARSCH 차량 상태변경");
						
						//----------------------------------------------------------------------------------------
						
						//------------------------------------------------------------------------------------------------------------
				    	//	재료정보 조회 (2단,1단 순)
				    	//------------------------------------------------------------------------------------------------------------				   
						JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
						jrParam1.setResultCode(logId);	//Log ID
			 			jrParam1.setResultMsg(methodNm);	//Log Method Name
						jrParam1.setField("TRANS_ORD_DATE2"	, transOrdDt);
						jrParam1.setField("TRANS_ORD_SEQNO2" 	, transOrdSeqNo);
			 			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockOfCarLoad 
			 			SELECT 
			 			       C.STACK_COL_GP||C.STACK_BED_GP|| C.STACK_LAYER_GP  AS UP_LOC
			 			     , C.STACK_LAYER_GP
			 			     , A.STOCK_ID
			 			     , B.YD_SCH_CD
			 			     , DECODE(LENGTH(A.TRANS_WORD_NO), 10, A.TRANS_WORD_NO
			 			     , SUBSTR(A.TRANS_WORD_NO, 1, 8) || '0' || SUBSTR(A.TRANS_WORD_NO, 9,1)) AS TRANS_WORD_DATE_NO
			 			     , B.YD_WBOOK_ID
			 			     , B.DEL_YN
			 			  FROM USRYMA.TB_YM_STOCK  A
			 			     , USRYMA.TB_YM_WRKBOOK  B
			 			     , USRYMA.TB_YM_WRKBOOKMTL  D
			 			     , USRYMA.TB_YM_STACKLAYER C
			 			 WHERE B.YD_WBOOK_ID=D.YD_WBOOK_ID
			 			   AND A.STOCK_ID = D.STOCK_ID
			 			   AND D.STOCK_ID = C.STOCK_ID
			 			   AND A.TRANS_ORD_DATE2=:V_TRANS_ORD_DATE2
			 			   AND A.TRANS_ORD_SEQNO2 =:V_TRANS_ORD_SEQNO2
			 			   AND B.DEL_YN = 'N'
			 			   AND D.DEL_YN = 'N'
			 			 ORDER BY C.STACK_LAYER_GP DESC, B.YD_WBOOK_ID */
						JDTORecordSet jsCarMtl = commDao.select(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockOfCarLoad", logId, methodNm, "재료정보 조회");					
						
						// 차량예정정보 송신
						jrParam = JDTORecordFactory.getInstance().create();
						jrParam.setResultCode(logId);	//Log ID
						jrParam.setResultMsg(methodNm);	//Log Method Name
			 			jrParam.setField("MODIFIER"		, modifier  ); //수정자
						jrParam.setField("YD_CAR_SCH_ID", ydCarSchId); //야드차량스케쥴ID
						jrParam.setField("SEARCH_FLAG"  , "2");  //1:상차도, 2:차량스케쥴 ID	
						jrRtn = commUtils.addSndData(jrRtn, ymComm.procCarPlanInfo(jrParam));

					
						/**********************************************************
						* Crane스케줄 호출
						*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
						*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
						**********************************************************/
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPntFrmYn 
						SELECT CP.YD_CARPNT_CD
						     , CP.YD_STK_COL_ACT_STAT
						     , CP.YD_CAR_USETYPE_GP
						     , CP.WLOC_CD
						     , CP.YD_PNT_CD
						     , nvl(DUMMY_YN,'N') AS DUMMY_YN ,YD_FRM_YN AS YD_FRM_YN2
						     , CASE WHEN YD_FRM_YN = 'Y' AND DUMMY_YN = 'Y' THEN 'N'
						            WHEN YD_FRM_YN IS NULL                  THEN 'N'
						            ELSE YD_FRM_YN
						        END AS YD_FRM_YN
						  FROM TB_YD_CARPOINT  CP
						     , (SELECT CP.YD_STK_COL_GP
						             , WB.YD_SCH_CD
						             , WB.YD_WBOOK_ID
						             , SL.STOCK_ID
						             , SL.STACK_COL_GP
						             , SL.STACK_BED_GP
						             , SL.STACK_LAYER_GP
						             , CASE WHEN SL.STACK_LAYER_GP = '01' AND (SELECT COUNT(*) FROM TB_YM_STACKLAYER
						                                                        WHERE STACK_COL_GP   = SL.STACK_COL_GP
						                                                          AND STACK_BED_GP   IN(SL.STACK_BED_GP, LPAD(TO_NUMBER(SL.STACK_BED_GP)-1, 2, '0'))
						                                                          AND STACK_LAYER_GP = '02'
						                                                          AND STOCK_ID IS NOT NULL) > 0
						                    THEN 'Y' ELSE 'N' END AS DUMMY_YN
						          FROM TB_YM_WRKBOOK     WB
						             , TB_YM_WRKBOOKMTL  WM
						             , (SELECT TRN_EQP_CD, CAR_NO, CARD_NO, YD_STK_COL_GP
						                  FROM USRYDA.TB_YD_CARPOINT
						                 WHERE YD_CARPNT_CD LIKE '3%'
						                   AND YD_STK_COL_GP = :V_YD_STK_COL_GP
						               ) CP
						             , TB_YM_STACKLAYER  SL
						         WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
						           AND WB.DEL_YN = 'N'
						           AND WM.DEL_YN = 'N'
						           AND WM.STOCK_ID = SL.STOCK_ID
						           AND SL.STACK_COL_GP   = WM.STACK_COL_GP
						           AND SL.STACK_BED_GP   = WM.STACK_BED_GP
						           AND SL.STACK_LAYER_GP = WM.STACK_LAYER_GP
						           AND SL.STACK_LAYER_STAT IN ('C','U')
						           AND ((WB.YD_CAR_USE_GP = 'L' AND WB.TRN_EQP_CD = CP.TRN_EQP_CD)
						             OR (WB.YD_CAR_USE_GP = 'G' AND WB.CAR_NO = CP.CAR_NO AND WB.CARD_NO = CP.CARD_NO))
						         ORDER BY WM.STACK_LAYER_GP DESC, WB.YD_WBOOK_ID    
						      ) WB_INFO
						 WHERE CP.YD_STK_COL_GP = :V_YD_STK_COL_GP
						   AND CP.YD_STK_COL_GP = WB_INFO.YD_STK_COL_GP(+)
						   AND ROWNUM = 1 */

						jrParam.setField("YD_STK_COL_GP" , ydStkColGp); 	
						JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPntFrmYn", logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 ");
						
						commUtils.printLog(logId, "더미 대상 여부: " + rsResult.getRecord(0).getFieldString("DUMMY_YN") , "SL");
						commUtils.printLog(logId, "차량형상 시스템 사용 여부: " + rsResult.getRecord(0).getFieldString("YD_FRM_YN2") , "SL");
						
						//com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPntFrmYn 쿼리에서 DUMMY_YN 이 'Y' 이면 YD_FRM_YN 은 'N'으로 셋팅된다.
						//즉 더미 작업이 있을 경우는 형상유무에 관계없이 YMYMJ303 이 호출 된다.
						if("N".equals(rsResult.getRecord(0).getFieldString("YD_FRM_YN"))) {
							
							//EJBConnector ejbConn = new EJBConnector("default", "BCoilSchSeEJB", this);
							JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
							
							//크레인 스케줄 기동 YMYMJ303 호출
							jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ303"); 
							jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
							jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
							jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
							
							int pcnt = 0;
							
							//2단 적치된 대상 스케줄 호출
							for(int i = 0; i < jsCarMtl.size(); i++) {
								
								if("02".equals(jsCarMtl.getRecord(i).getFieldString("STACK_LAYER_GP"))) {
									
									jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
								}
							}
							
							//1단 적치된 대상 스케줄 호출
							for(int i = 0; i < jsCarMtl.size(); i++) {
								
								if("01".equals(jsCarMtl.getRecord(i).getFieldString("STACK_LAYER_GP"))) {
									
									jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
									
								}
							}
							
							//작업예약 순서 대로 스케줄 호출
							//for(int i = 0; i < jsCarMtl.size(); i++) {
							//	
							//	jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
							//}
							
							
							jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(pcnt));
							
							jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
						}
						//----------------------------------------------------------------------------------------
						
						if("32".equals(CarldPntCd.substring(0,2)) && !"E".equals(CarldPntCd.substring(2,3))) {
							//E동이 아닌 A,B,C,D 동 2번 포인트 일 경우 
							//해당 포인트 입동지시 요구 호출
							
							// 입동지시 미리 주기 적용여부 확인
							String sAPPLY060 = ymComm.BCoilApplyYn("APP060","3","PREINWO_YN");
							commUtils.printLog(logId,  ">>> 입동지시 미리 주기 적용여부 (Y:적용, N:비적용) :" + sAPPLY060, "SL");
							if (sAPPLY060.equals("Y")) {						
								JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
								jrTemp.setResultCode(logId);	//Log ID
								jrTemp.setResultMsg(methodNm);	//Log Method Name
								jrTemp.setField("JMS_TC_CD"			, "YMYMJ662");          //차량입동지시 요구 
								jrTemp.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
								jrTemp.setField("YD_CARPNT_CD"		, CarldPntCd);		//입동포인트
								
								jrRtn = commUtils.addSndData(jrRtn, jrTemp);
							}
						}						
					} else {
						throw new Exception("사용불가한 차량포인트 또는 입동지시 받은 포인트가 아닙니다!"); 
					}
				}	
		 		if(sAPPLY.equals("Y")) {
					/***** 차량log Claer ****/
					JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
					jrLogMsg.setResultCode(logId);	//Log ID
					jrLogMsg.setResultMsg(methodNm);	//Log Method Name
					jrLogMsg.setField("MODIFIER"		, modifier); //수정자 셋팅
					jrLogMsg.setField("YD_CAR_SCH_ID"	, ydCarSchId); //차량스케쥴
					
					EJBConnector ejbConnLog = new EJBConnector("default", "YmCommSeEJB", this);
					ejbConnLog.trx("updCarErrorLogClear", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });

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
	/**
	 * 코일이송상차완료PDA(DMYDR072) 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR072(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일이송상차완료PDA 수신[BCoilL3RcvSeEJB.rcvDMYDR072] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = 0;
		 String szMsg	= "";
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrRst = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp		= commUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String szRcvTcCode 	= commUtils.trim(rcvMsg.getFieldString("JMS_TC_CD"));
			String stlAppearGp 	= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));   //재료외형
			String StockId 		= commUtils.trim(rcvMsg.getFieldString("STL_NO"));    		//재료번호
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String dmTcCode 	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));//TC_CODE
			
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/  			
			// 수신한 전문이 null이라면 error
			if(szRcvTcCode.equals("")){
				szMsg="[ERROR] "+methodNm+"::"+methodNm+"() TC Code Error (NULL)";	
				throw new Exception("TC Code Error.");
			}
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STL_NO"	, StockId);

			/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdStockJoinStkLyr2*/               
			/*SELECT                                                                                
			     A.STOCK_ID                  AS STL_NO                                         
			    ,A.TRANS_ORD_DATE2           AS TRANS_ORD_DATE                                  
			    ,A.TRANS_ORD_SEQNO2          AS TRANS_ORD_SEQNO                                 
			    ,A.CAR_NO2                   AS CAR_NO                                          
			    ,A.CAR_CARD_NO               AS CARD_NO                                       
			    ,NVL(B.STACK_COL_GP, C.STACK_COL_GP)   AS YD_STK_COL_GP 
			FROM TB_YM_STOCK A                                                                    
			    ,TB_YM_STACKLAYER B                                                               
			    ,TB_YM_STACKCOL C                                                                 
			WHERE A.STOCK_ID = :V_STL_NO                                                                   
			  AND A.STOCK_ID = B.STOCK_ID(+)                                                          
			  AND A.CAR_CARD_NO = C.CAR_CARD_NO(+)    */                                                     				
			JDTORecordSet loadYdStkcol = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdStockJoinStkLyr2", logId, methodNm, "차량정정보검색");
			
			if(loadYdStkcol.size() <= 0 ){
				szMsg="["+methodNm+"] YMSTOCK[코일이송상차완료]조건조회시 SELECT Error ::  DO NOT EXIST"  ;
				commUtils.printLog(logId, szMsg, "SL");	
				throw new Exception(szMsg);
			}
			//수신 항목 값
			String ydCarNo    	= commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("CAR_NO"));
			String ydStkColGp 	= commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("YD_STK_COL_GP"));
			String ydCardNo     = commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("CARD_NO"));
			String transOrdDate	= commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("TRANS_ORD_DATE"));
			String transOrdSeqNo= commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("TRANS_ORD_SEQNO"));

			jrParam.setField("STL_APPEAR_GP"	, stlAppearGp);
			jrParam.setField("DEL_YN"         	, "Y");
			jrParam.setField("YD_AIM_RT_GP"   	, "M2");
			jrParam.setField("STL_PROG_CD"    	, "M"); 
			jrParam.setField("STOCK_ID"		  	, StockId);
			jrParam.setField("MODIFIER"			, szRcvTcCode);
			
			//저장품갱신**************************************************************		
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStock
            UPDATE USRYMA.TB_YM_STOCK
				SET DEL_YN=:V_DEL_YN
				  , MODIFIER=:V_MODIFIER  
				  , MOD_DDTT =SYSDATE
				  , YD_AIM_RT_GP = :V_YD_AIM_RT_GP
				WHERE STOCK_ID=:V_STOCK_ID
			*/
			intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStock", logId, methodNm, "TB_YM_STOCK 수정");
			if(intRtnVal <= 0){
				szMsg="["+methodNm+"]" + " YM_STOCK[코일이송상차완료] UPDATE Error " + " STOCK_ID: "+StockId         ;           
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
	 		}				
			//***************************************************************************
			//  저장품이 적치된 저장위치 정보를 조회
			//***************************************************************************
			
			szMsg="[" + methodNm + "]카드번호["+ydCardNo+"], 차량번호["+ydCardNo+"], 운송지시일자["+transOrdDate+"], 운송지시순번["+transOrdSeqNo+"] : 출하완료된 동["+ydStkColGp+"]의 저장품들 조회 시작";
			commUtils.printLog(logId, szMsg, "SL");
			
			if(stlAppearGp.equals("*") ) {

				commUtils.printLog(logId, "[" + methodNm + "] 마지막 상차완료 전문", "SL");
				jrParam.setField("CAR_NO" 			, ydCarNo);
				jrParam.setField("CARD_NO"			, ydCardNo);
				jrParam.setField("TRANS_ORD_DATE"	, transOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschTransDTSeq2
		    	SELECT *
		    	  FROM (
		    	       SELECT YD_CAR_SCH_ID
		    	         FROM TB_YD_CARSCH
		    	        WHERE CAR_NO        LIKE :V_CAR_NO||'%'
		    	          AND TRANS_ORD_DATE = :V_TRANS_ORD_DATE
		    	          AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
		    	        --AND DEL_YN='N'
		    	        ORDER BY YD_CAR_SCH_ID DESC
		    	        ) A
		    	WHERE ROWNUM<=1
		    	*/                                                     				
				JDTORecordSet loadCarsch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschTransDTSeq2", logId, methodNm, "차량정정보검색");
				
				if(loadCarsch.size() <= 0 ){
					szMsg="["+methodNm+"] 차량스케쥴 조회 SELECT Error ::  DO NOT EXIST"  ;
					commUtils.printLog(logId, szMsg, "SL");	
					throw new Exception(szMsg);
				}	

				jrParam.setField("TC_CODE"			, "DMYDR040");	//전문코드
				jrParam.setField("SPOS_WLOC_CD"		, commUtils.trim(loadCarsch.getRecord(0).getFieldString("SPOS_WLOC_CD")));
				jrParam.setField("SPOS_YD_PNT_CD"	, commUtils.trim(loadCarsch.getRecord(0).getFieldString("YD_PNT_CD1")));
				jrParam.setField("YD_GP"			, ydGp);
				if(ydCardNo.equals("")){
					ydCardNo = "XXXXX";
				}					
				
				commUtils.printParam(logId, jrParam);
				//전송 Data 생성
				szMsg= "["+ methodNm +"] 차량번호[" + ydCarNo + "]는 코일제품출하차량출발실적호출";
				commUtils.printLog(logId, szMsg, "SL");
				
				EJBConnector ejbConn1 = new EJBConnector("default", "YmCommCarMvSeEJB", this);
				JDTORecord jrRtn1 = (JDTORecord)ejbConn1.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				
				jrRtn = commUtils.addSndData(jrRtn, jrRtn1);		
				
			}else{
				szMsg="[" + methodNm + "] 마지막 상차완료 전문이 아님";
				commUtils.printLog(logId, szMsg, "SL");
				
			}
			// 차량 출발후 저장품 정보 갱신
			// L2저장품재원 정보 송신
			// ======================================================
			// 저장품제원 : 코일야드L2로 송신(YMA7L002)
			// ======================================================				
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);	//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
			sndL2Msg.setField("YD_INFO_SYNC_CD"	, "5"       ); //야드정보동기화코드
			sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
			sndL2Msg.setField("STOCK_ID"       	, StockId  ); //재료번호
			commUtils.printParam(logId, sndL2Msg);
 
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L002", sndL2Msg));	 //전송 Data 생성	
			
			szMsg="[" + methodNm + "] 저장품제원 : 코일야드L2 로 송신 저장품ID[" + StockId + "] - 저장품제원 : 코일야드L2 로 송신 호출 성공"+jrRtn.size();

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	
	/**
	 * 코일이송하차지시PDA(DMYDR073) 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR073(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일이송하차지시PDA 수신[BCoilL3RcvSeEJB.rcvDMYDR073] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrRst = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    		= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlAppearGp 		= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));    //재료외형
			String transOrdDt  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			String transOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String cancelYn     	= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));
			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
			String ydCardNo 		= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
			String WorkGp 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP"));      //작업구분
			String ydCarKind		= commUtils.trim(rcvMsg.getFieldString("CAR_KIND"));
			String CarudPntCd 		= commUtils.trim(rcvMsg.getFieldString("CARUD_PNT_CD")); //하차포인트
			String transFrtoMoveGp 	= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP")); //냉연이송구분 
			int ydEqpWrkSh 			= Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0"));
			String modifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String dmTcCode 		= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));//TC_CODE
			if ("".equals(modifier)) { modifier = msgId; }
	    	// PIDEV
//	    	String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "*", "*");			
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER" 		, modifier);
			jrParam.setField("STOCK_ITEM" 		, "CG");
			jrParam.setField("STOCK_MOVE_TERM" 	, "CS");
			jrParam.setField("TRANS_ORD_DT" 	, transOrdDt);
			jrParam.setField("TRANS_ORD_SEQNO" 	, transOrdSeqNo);
			jrParam.setField("CAR_CARD_NO" 		, ydCardNo);
			jrParam.setField("CAR_NO2" 			, ydCarNo);
			jrParam.setField("CR_FRTOMOVE_GP" 	, transFrtoMoveGp);
			jrParam.setField("YD_CARPNT_CD"		, CarudPntCd);
			jrParam.setField("TRANS_WORD_NO" 	, transOrdDt+transOrdSeqNo);			
	    	// PIDEV
//			if ("Y".equals(sApplyYnPI)) {
				jrParam.setField("CAR_NO" 		, ydCarNo);
//			}
			
			String StockId  = "";
			
			for(int i = 1 ; i<=20; i++){
				   
				StockId = commUtils.trim(rcvMsg.getFieldString("STL_NO"+i));
				
				if(StockId.equals("")){
					break;
				}
				jrParam.setField("STOCK_ID" 		, StockId);
				jrParam.setField("SHEAR_SUPPLY_SEQ" , commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i)));  // 차상위치
				
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
		    	//PIDEV				
//				if("Y".equals(sApplyYnPI)) {
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStockTransInfo_PIDEV", logId, methodNm, "TB_YM_STOCK 등록");
//				} else {
//					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStockTransInfo", logId, methodNm, "TB_YM_STOCK 등록");	
//				}		    	
			}			
			//작업구분(1:내수/2:수출/3:연안해송/9:냉연이송)
			
			if(WorkGp.equals("9")){
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarYdCmbnCarldYn_PIDEV
				SELECT *
				  FROM TB_YD_CARSCH
				 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DT
				   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				   AND CARD_NO         = :V_CARD_NO
	--			   AND CMBN_CARLD_YN   = :V_CMBN_CARLD_YN
				   AND DEL_YN   = 'N'	
				*/	   
				JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarYdCmbnCarldYn_PIDEV", logId, methodNm, "차량스케쥴 조회");
				if(jsCarSch.size() > 0) {
					commUtils.printLog(logId, methodNm + " TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.:기존지시 삭제 " , "SL");	
					
					String oldCarSchId = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID")); //차량 작업지시
				 
					jrParam.setField("YD_CAR_SCH_ID", oldCarSchId);
					jrParam.setField("DEL_YN"       , "Y");
					
					/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch
					UPDATE TB_YD_CARSCH
					   SET MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					     , DEL_YN           = NVL(:V_DEL_YN          , DEL_YN)
					     --하차
					     , YD_CAR_PROG_STAT     = NVL(:V_YD_CAR_PROG_STAT    , YD_CAR_PROG_STAT)
					     , YD_CARUD_WRK_BOOK_ID = NVL(:V_YD_CARUD_WRK_BOOK_ID, YD_CARUD_WRK_BOOK_ID)
					     , YD_CARUD_STOP_LOC    = NVL(:V_YD_CARUD_STOP_LOC   , YD_CARUD_STOP_LOC)
					     , YD_CARUD_ARR_DT      = DECODE(:V_YD_CARUD_ARR_DT,NULL,YD_CARUD_ARR_DT, TO_DATE(:V_YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS')) 
					     , YD_PNT_CD3           = NVL(:V_YD_PNT_CD3          , YD_PNT_CD3)
					     --상차
					     , YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_CARLD_WRK_BOOK_ID, YD_CARLD_WRK_BOOK_ID)
					     , YD_CARLD_STOP_LOC    = NVL(:V_YD_CARLD_STOP_LOC   , YD_CARLD_STOP_LOC)
					     , YD_CARLD_ARR_DT      = DECODE(:V_YD_CARLD_ARR_DT,NULL,YD_CARLD_ARR_DT, TO_DATE(:V_YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS'))
					     , YD_PNT_CD1           = NVL(:V_YD_PNT_CD1          , YD_PNT_CD1)
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
					*/
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch", logId, methodNm, "TB_YD_CARSCH 차량 스케줄정보");
				}
				////////////////////////////////////////////////////////////////////////////////////////
				//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////////////////				
				String ydCarSchId = commDao.getSeqId(logId, methodNm, "CarSch");
				
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarPoint
				SELECT SUBSTR(YD_STK_COL_GP,1,4)||'0'||SUBSTR(YD_STK_COL_GP,6,1)  AS YD_STK_COL_GP2
				     , DECODE(A.YD_STK_COL_ACT_STAT,'R','C',YD_STK_COL_ACT_STAT)  AS YD_STK_COL_ACT_STAT
				     , YD_CARPNT_CD
				     , REG_DDTT
				     , REGISTER
				     , MOD_DDTT
				     , MODIFIER
				     , DEL_YN
				     , YD_CAR_USETYPE_GP
				     , YD_GP
				     , YD_BAY_GP
				     , YD_STK_COL_GP
				     , TRN_EQP_CD
				     , CAR_NO
				     , CARD_NO
				     , WLOC_CD
				     , YD_PNT_CD
				     , YD_CARPNT_DESC
				     , YD_SPAN_FROM
				     , YD_SPAN_TO
				  FROM TB_YD_CARPOINT A
				 WHERE YD_CARPNT_CD=:V_YD_CARPNT_CD
				 */
				///도착가능 포인트 조회
				JDTORecord jrCarPnt = JDTORecordFactory.getInstance().create();
				
				JDTORecordSet jsCarPnt = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarPoint", logId, methodNm, "차량포인트 조회");
				if(jsCarPnt.size() <= 0 ) {
					commUtils.printLog(logId, methodNm + " TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다. " , "SL");
					m_ctx.setRollbackOnly();
					throw new Exception("TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다....");
				}

				jsCarPnt.first();
				
				jrCarPnt = jsCarPnt.getRecord();
				String ydStkColGp	= commUtils.trim(jrCarPnt.getFieldString("YD_STK_COL_GP"));
				String ydPntCd      = commUtils.trim(jrCarPnt.getFieldString("YD_PNT_CD"));
				String arrWlocCd    = commUtils.trim(jrCarPnt.getFieldString("WLOC_CD")); 
				String szMovYn      = commUtils.trim(jrCarPnt.getFieldString("MOV_YN")); 
				
				JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID"		, ydCarSchId);
				recInTemp.setField("REGISTER"			, modifier);
				recInTemp.setField("YD_EQP_ID"			, YmConstant.YD_DM_CAR_EQP_ID);		//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP"		, YmConstant.YD_CAR_USE_GP_DM);		//차량사용구분 
				recInTemp.setField("CAR_NO"				, ydCarNo);							//차량번호
				recInTemp.setField("CAR_KIND"			, ydCarKind);						//차량종류
				recInTemp.setField("YD_EQP_WRK_STAT"	, "L");								//야드설비작업상태
				recInTemp.setField("SPOS_WLOC_CD"		, arrWlocCd);						//발지개소코드
				recInTemp.setField("ARR_WLOC_CD"		, arrWlocCd);						//착지개소코드
				recInTemp.setField("YD_CARUD_LEV_DT"	, commUtils.getDateTime14());		//하차출발일시
				recInTemp.setField("YD_PNT_CD3"			, ydPntCd);							//야드포인트코드3
				recInTemp.setField("YD_CARUD_STOP_LOC"	, ydStkColGp);						//야드하차차정지위치 
		    	// PIDEV				
//				if ("N".equals(sApplyYnPI)) {				
//					recInTemp.setField("CARD_NO"		, ydCardNo);						//카드번호
//				}				
				recInTemp.setField("YD_CAR_PROG_STAT"	, "A");								//하차출발상태
				recInTemp.setField("TRANS_ORD_DATE"		, transOrdDt);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);					//운송지시순번 
				//recInTemp.setField("YD_BAYIN_WO_SEQ"	, YmConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 - 기본값으로 설정(9)
				
				if("Y".equals(szMovYn)){
					recInTemp.setField("YD_BAYIN_WO_SEQ"	, "1");	//입동지시순번 - 제품이송하차는 1순위로 변경 함
		    	}else{
		    		recInTemp.setField("YD_BAYIN_WO_SEQ",  	YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);						//입동지시순번 - 기본값으로 설정(9)
		    	}
				
				
				recInTemp.setField("YD_CAR_WRK_GP"		, WorkGp);
				recInTemp.setField("TRANS_EQUIPMENT_TYPE",	"P");
				
				if(ydCarKind.equals("TT")){
					recInTemp.setField("CAR_KIND",          "TT");									//차량종류
				}else{
					recInTemp.setField("CAR_KIND",          "TR");									//차량종류
				}			
				
				recInTemp.setField("TEL_NO",   		rcvMsg.getFieldString("TEL_NO"));						//연락처
				recInTemp.setField("DRIVER_NAME",  	rcvMsg.getFieldString("DRIVER_NAME"));						//운전기사명
				
	    		//차량스케줄 등록
				
				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insYdCarsch_PIDEV
				INSERT INTO USRYDA.TB_YD_CARSCH
				(	   YD_CAR_SCH_ID
				     , REGISTER
				     , REG_DDTT	
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
				       )
				VALUES (
				       :V_YD_CAR_SCH_ID
				     , :V_REGISTER
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
				     , :V_YD_CARLD_LEV_DT
				     , :V_YD_CARUD_LEV_DT
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
				       )
				 */
				 
		    	// PIDEV				
//				if ("Y".equals(sApplyYnPI)) {
					commDao.insert(recInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insYdCarsch_PIDEV", logId, methodNm, "TB_YD_CARSCH 등록");
//				} else {
//					commDao.insert(recInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insYdCarsch", logId, methodNm, "TB_YD_CARSCH 등록");	
//				}
				
				String gdsCarldLoc = "";
				String ydStkBedNo  = "";
				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
				for(int i = 1 ; i<=ydEqpWrkSh; i++){	
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID"	, ydCarSchId);
					recInTemp.setField("MODIFIER"		, modifier);
					recInTemp.setField("STL_NO"			, commUtils.trim(rcvMsg.getFieldString("STL_NO"+i)));
					
					gdsCarldLoc = commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i));	 
					if(gdsCarldLoc.substring(0 , 1).equals("A")){
						ydStkBedNo = "0"+gdsCarldLoc.substring(1 , 2);
					}else if(gdsCarldLoc.substring(0 , 1).equals("B")){
						if(gdsCarldLoc.substring(1 , 2).equals("1")){
							ydStkBedNo="06";
						}else if(gdsCarldLoc.substring(1 , 2).equals("2")){
							ydStkBedNo="07";
						}else if(gdsCarldLoc.substring(1 , 2).equals("3")){
							ydStkBedNo="08";
						}else if(gdsCarldLoc.substring(1 , 2).equals("4")){
							ydStkBedNo="09";
						}else if(gdsCarldLoc.substring(1 , 2).equals("5")){
							ydStkBedNo="10";
						}
					}else if(gdsCarldLoc.substring(0 , 1).equals("C")){
						if(gdsCarldLoc.substring(1 , 2).equals("1")){
							ydStkBedNo="11";
						}else if(gdsCarldLoc.substring(1 , 2).equals("2")){
							ydStkBedNo="12";
						}else if(gdsCarldLoc.substring(1 , 2).equals("3")){
							ydStkBedNo="13";
						}else if(gdsCarldLoc.substring(1 , 2).equals("4")){
							ydStkBedNo="14";
						}else if(gdsCarldLoc.substring(1 , 2).equals("5")){
							ydStkBedNo="15";
						}
					} 
					recInTemp.setField("YD_STK_BED_NO"		, ydStkBedNo);
					recInTemp.setField("YD_STK_LYR_NO"		, "001");
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
					
					commDao.insert(recInTemp, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarSchmtl", logId, methodNm, "차량재료 스케쥴 INSERT ");	
					
					sndL2Msg = JDTORecordFactory.getInstance().create();
					sndL2Msg.setField("MSG_GP"         , "I");
					sndL2Msg.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
					sndL2Msg.setField("STOCK_ID"       ,  commUtils.trim(rcvMsg.getFieldString("STL_NO"+i)));
					
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L002", sndL2Msg));	 //전송 Data 생성	
					
				}
	    		
				
	    		if(ydCarKind.equals("TT")){
		    		//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
	    			EJBConnector ejbConn3 = new EJBConnector("default","YmCommCarMvSeEJB",this);
					ejbConn3.trx("YmCarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
					 			new Object[]{"C","","",ydStkColGp,"","","R"});
	    		}			
				
				//입동지시요구모듈 호출(trailer인 경우)///////////////////////////////////////////////////////
				if(ydCarKind.equals("T") || ydCarKind.equals("TR")){
					/*
					 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
					 */
					recInTemp = JDTORecordFactory.getInstance().create();			 
					recInTemp.setResultCode(logId);	//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
					recInTemp.setField("JMS_TC_CD"			, "YMYMJ662");          //차량입동지시 요구 기존:YDYDJ662
					recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
					recInTemp.setField("YD_CARPNT_CD"		, CarudPntCd);
					recInTemp.setField("YD_CAR_SCH_ID"		, ydCarSchId);
					//recInTemp.setField("CHK_YN"				, "N");
					
					jrRtn = commUtils.addSndData(jrRtn, recInTemp);	
					
//					EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
//					ejbConn.trx("procCarBayInOrdReqTr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					
					commUtils.printLog(logId, methodNm + "차량정지위치[" + ydStkColGp + "], 차량스케줄ID[" + ydCarSchId + "] -AB 차량입동지시요구 모듈을 호출", "SL");
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
	
	/**
	 * 코일이송하차대기장도착PDA(DMYDR074) 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR074(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일이송하차대기장도착PDA 수신[BCoilL3RcvSeEJB.rcvDMYDR074] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrRst = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    		= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String transOrdDt  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			String transOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
			String ydCardNo 		= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
			String ydCarKind		= commUtils.trim(rcvMsg.getFieldString("CAR_KIND"));     //차량종류
			String WorkGp 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP"));      //작업구분
			String CarldPntCd 		= commUtils.trim(rcvMsg.getFieldString("CARUD_PNT_CD")); //하차포인트 CARUD_PNT_CD ?
			String transFrtoMoveGp 	= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP")); //냉연이송구분 
			
			String modifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String dmTcCode 		= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));//TC_CODE
			String ydCarProgStat    = "";
			String ydCarWrkGp 		= ""; 	//야드차량작업구분
			String ydEqpWrkStat		= ""; 	//야드설비작업상태
			
			if ("".equals(modifier)) { modifier = msgId; }
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrInTemp = JDTORecordFactory.getInstance().create();
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("TRANS_ORD_DATE", transOrdDt); 						
			jrParam.setField("TRANS_ORD_SEQNO", transOrdSeqNo); 						
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTransStockInfo
			SELECT A.STOCK_ID AS STL_NO
			     , (SELECT WLOC_CD 
			          FROM USRYDA.TB_YD_CARPOINT C
			             , USRYMA.TB_YM_STACKLAYER B
			         WHERE A.STOCK_ID=B.STOCK_ID  
			           AND SUBSTR(B.STACK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
			           AND C.YD_STK_COL_GP LIKE SUBSTR(B.STACK_COL_GP,1,2)||'%'
			           AND ROWNUM<=1 ) 
			       AS WLOC_CD
			     , (SELECT SUBSTR(YD_STK_COL_GP,1,4)||'0'||SUBSTR(YD_STK_COL_GP,6,1) 
			          FROM USRYDA.TB_YD_CARPOINT C
			             , USRYMA.TB_YM_STACKLAYER B
			         WHERE A.STOCK_ID=B.STOCK_ID  
			           AND SUBSTR(B.STACK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
			           AND C.YD_STK_COL_GP LIKE SUBSTR(B.STACK_COL_GP,1,2)||'%'
			           AND C.YD_STK_COL_ACT_STAT IN('C','R')
			           AND ROWNUM<=1 ) 
			       AS YD_STK_COL_GP   
			     , A.CAR_CARD_NO
			     , (SELECT YD_CAR_SCH_ID FROM USRYDA.TB_YD_CARSCH
			         WHERE DEL_YN='N'
			           AND TRANS_ORD_DATE  = A.TRANS_ORD_DATE2
			           AND TRANS_ORD_SEQNO = A.TRANS_ORD_SEQNO2
			           AND ROWNUM<=1 ) 
			       AS YD_CAR_SCH_ID
			  FROM TB_YM_STOCK A      
			 WHERE A.TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE
			   AND A.TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
			   AND A.DEL_YN = 'N'
			*/	   
			JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTransStockInfo", logId, methodNm, "운송실적번호에 맞는 제품번호가 존재 조회");
			
			String ydCarSchId = commUtils.trim(jsStock.getRecord(0).getFieldString("YD_CAR_SCH_ID")); //차량 작업지시

			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCarsch", logId, methodNm, "TB_YD_CARSCH 조회");
			if(jsCarSch.size() <= 0 ){
				commUtils.printLog(logId, "["+methodNm+"] 차량 스케쥴 없음" , "SL");	
				throw new Exception("차량 스케쥴 없음");
			} else {
				ydCarProgStat 	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT")); //차량진행상태
				ydCarWrkGp 		= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_WRK_GP")); 	//야드차량작업구분
				ydEqpWrkStat	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_EQP_WRK_STAT")); 	//야드설비작업상태
			}
			
			//작업구분(1:내수/2:수출/3:연안해송/9:냉연이송)
			if(WorkGp.equals("9")){
				//########################################## 9:HYSCO스케줄 ####################################################

				jrInTemp = JDTORecordFactory.getInstance().create();
				jrInTemp.setResultCode(logId);	//Log ID
				jrInTemp.setResultMsg(methodNm);	//Log Method Name
				jrInTemp.setField("MODIFIER" 			, modifier); //수정자
				jrInTemp.setField("YD_CARPNT_CD"		, CarldPntCd);
				
				//포인트코드 -> 개소코드와 저장위치 가져오기
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint*/
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarPoint
				SELECT SUBSTR(YD_STK_COL_GP,1,4)||'0'||SUBSTR(YD_STK_COL_GP,6,1)  AS YD_STK_COL_GP2
				     , DECODE(A.YD_STK_COL_ACT_STAT,'R','C',YD_STK_COL_ACT_STAT)  AS YD_STK_COL_ACT_STAT
				     , YD_CARPNT_CD
				     , REG_DDTT
				     , REGISTER
				     , MOD_DDTT
				     , MODIFIER
				     , DEL_YN
				     , YD_CAR_USETYPE_GP
				     , YD_GP
				     , YD_BAY_GP
				     , YD_STK_COL_GP
				     , TRN_EQP_CD
				     , CAR_NO
				     , CARD_NO
				     , WLOC_CD
				     , YD_PNT_CD
				     , YD_CARPNT_DESC
				     , YD_SPAN_FROM
				     , YD_SPAN_TO
				  FROM TB_YD_CARPOINT A
				 WHERE YD_CARPNT_CD=:V_YD_CARPNT_CD	
				*/	   
				JDTORecordSet jsCarPnt = commDao.select(jrInTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarPoint", logId, methodNm, "차량포인트 조회");				
				
				if(jsCarPnt.size() > 0){
					// TB_YM_STACKLAYER 에 해당 재료 등록 처리
					jsCarPnt.first();
					JDTORecord jrCarPnt 	= jsCarPnt.getRecord();
					String ydStkColGp		= commUtils.trim(jrCarPnt.getFieldString("YD_STK_COL_GP2"));
					String ydStkColActStat	= commUtils.trim(jrCarPnt.getFieldString("YD_STK_COL_ACT_STAT"));
					String carNo			= commUtils.trim(jsCarPnt.getRecord(0).getFieldString("CAR_NO"));
					
					jrInTemp.setField("YD_CAR_SCH_ID"		, ydCarSchId);
					jrInTemp.setField("STACK_COL_GP"		, ydStkColGp);
					
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStacklayer 
					MERGE INTO TB_YM_STACKLAYER SL USING (
					SELECT A.STACK_COL_GP
					     , A.STACK_BED_GP
					     , A.STACK_LAYER_GP
					     , B.STL_NO
					  FROM USRYMA.TB_YM_STACKLAYER A
					     , USRYDA.TB_YD_CARFTMVMTL B
					 WHERE A.STACK_COL_GP  = :V_STACK_COL_GP
					   AND B.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					   AND B.YD_STK_BED_NO = A.STACK_BED_GP
					   AND '01' = A.STACK_LAYER_GP
					 ) DD ON (SL.STACK_COL_GP = DD.STACK_COL_GP AND SL.STACK_BED_GP = DD.STACK_BED_GP AND SL.STACK_LAYER_GP = DD.STACK_LAYER_GP)
					WHEN MATCHED THEN UPDATE SET
					     SL.MOD_DDTT = SYSDATE
					   , SL.MODIFIER = :V_MODIFIER
					   , SL.STOCK_ID = DD.STL_NO
					   , SL.STACK_LAYER_ACTIVE_STAT = 'E'
					   , SL.STACK_LAYER_STAT = 'C'  
					*/	   
					commDao.update(jrInTemp, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStacklayer", logId, methodNm, "TB_YM_STACKLAYER 수정");
					
					// TT-Car가 예약인 경우 차량도착처리 호출이 가능 하도록 조치
					if ("TT".equals(ydCarKind) && "R".equals(ydStkColActStat)) {
						ydStkColActStat = "C";
					}					
					//TR가 도착해 있는 경우 
					if ("TR".equals(ydCarKind) && "L".equals(ydStkColActStat)) {
						ydStkColActStat = "C";
					}
					//TR가 도착해 있는 경우  (사용불가)
					if ("TR".equals(ydCarKind) && "N".equals(ydStkColActStat) && ydCarNo.equals(carNo)) {
						ydStkColActStat = "C";
					}
					if("C".equals(ydStkColActStat)){					
					
						//------------------------------------------------------------------------------------------------------------
				       //	차량 POINT TABLE 점유
				       //------------------------------------------------------------------------------------------------------------
						jrInTemp = JDTORecordFactory.getInstance().create();	 
					    jrInTemp.setResultCode(logId);	//Log ID
					    jrInTemp.setResultMsg(methodNm);	//Log Method Name
						jrInTemp.setField("MODIFIER"     	, modifier  ); //수정자
						jrInTemp.setField("CAR_NO"			, ydCarNo);
						jrInTemp.setField("CARD_NO"			, ydCardNo );	
						jrInTemp.setField("YD_MAKECARPNT_CD", CarldPntCd );	
						
					    EJBConnector ejbConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
					    ejbConn.trx("procUpdYdTransOrdChange", new Class[] { JDTORecord.class }, new Object[] { jrInTemp });	
						
						jrInTemp = JDTORecordFactory.getInstance().create();	 
						jrInTemp.setResultCode(logId);	//Log ID
					    jrInTemp.setResultMsg(methodNm);	//Log Method Name
						jrInTemp.setField("MODIFIER"     		, modifier  ); //수정자
						jrInTemp.setField("STACK_COL_GP"		, ydStkColGp);
						jrInTemp.setField("CARD_NO"				, ydCardNo);
						jrInTemp.setField("CAR_NO"				, ydCarNo);
						jrInTemp.setField("TRN_EQP_CD"			, "");
						jrInTemp.setField("YD_CAR_PROG_STAT"	, ydCarProgStat);
						jrInTemp.setField("TRANS_EQUIPMENT_TYPE", "P");  //냉연 이송
						
						EJBConnector ejbConn1 = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
						ejbConn1.trx("procYmLayerOpen", new Class[] { JDTORecord.class }, new Object[] { jrInTemp });					
						
						/*************************************** 
						 * 	5.작업예약이 생성 
						 **************************************/	
						//작업예약,작업재료 등록	
						String ydSchCd  = "";

						int  intTransOrdSeqNo   = Integer.parseInt(transOrdSeqNo);
						
						// PIDEV
//						String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "*", "*");

//						if ("Y".equals(sApplyYnPI)) {
							String[] rVal = commDao.getTrnFrtomoveGpPI("", methodNm, transOrdDt, transOrdSeqNo);
							String sTrnFrtomoveGp = rVal[0];
							String hIssueGp = rVal[1];
							
							commUtils.printLog(logId, "sTrnFrtomoveGp:" + sTrnFrtomoveGp + "," + "hIssueGp:" + hIssueGp, "SL");

//							if (CarldPntCd.substring(1,2).equals("2")) {
//								if ( "21".equals(sTrnFrtomoveGp) ) {   //제품이송
//									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT26LM";  
//								} else {
//									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT16LM";
//								}
//							} else {
//								if ( "21".equals(sTrnFrtomoveGp) ) {   //제품이송
//									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT22LM";
//								} else {
//									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT12LM";
//								}
//							}
							if( "21".equals(sTrnFrtomoveGp) && "K".equals(hIssueGp) ) {
								if ("2".equals(CarldPntCd.substring(1,2))) {
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT26LM";	// 제품이송상차
								} else {
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT22LM";	// 제품이송상차
								}
							} else if ( "22".equals(sTrnFrtomoveGp) && "9".equals(hIssueGp) ) {
								if ("2".equals(CarldPntCd.substring(1,2))) {
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT26LM";	// 제품이송상차
								} else {
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT22LM";	// 제품이송상차
								}
							} else if ( ("21".equals(sTrnFrtomoveGp) && "C".equals(hIssueGp))
										||
										("21".equals(sTrnFrtomoveGp) && "D".equals(hIssueGp)) ) {
								if ("2".equals(CarldPntCd.substring(1,2))) {
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT16LM";	// 이송출고상차
								} else {
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT12LM";	// 이송출고상차
								}
							} else {
								if ("2".equals(CarldPntCd.substring(1,2))) {
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT16LM";
								} else {
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT12LM";
								}
							}
							
//						} else {
//							if (CarldPntCd.substring(1,2).equals("2")) {
//								if(intTransOrdSeqNo > 800000) {   //제품이송
//									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT26LM";  
//								} else {
//									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT16LM";
//								}
//							} else {
//								if(intTransOrdSeqNo > 800000) {   //제품이송
//									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT22LM";
//								} else {
//									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT12LM";
//								}
//							}
//						}
						
						jrInTemp = JDTORecordFactory.getInstance().create();	 
						jrInTemp.setResultCode(logId);	//Log ID
					    jrInTemp.setResultMsg(methodNm);	//Log Method Name
						jrInTemp.setField("YD_SCH_CD"       , ydSchCd   );// 자동이적 
						jrInTemp.setField("YD_CAR_SCH_ID"	, ydCarSchId);
						jrInTemp.setField("STACK_COL_GP"    , ydStkColGp);
						jrInTemp.setField("CARD_NO"       	, ydCardNo);
						jrInTemp.setField("CAR_NO"       	, ydCarNo);
						jrInTemp.setField("MODIFIER"     	, modifier  ); //수정자
						
		    			String ydWbookId = ymComm.procCarWkBookInsert(jrInTemp);
		    			
		    			if(ydWbookId.equals(YmConstant.RETN_CD_FAILURE)){
		    				throw new Exception("작업예약ID 생성 실패"); 				
		    			}		    	
		    			//----------------------------------------------------------------------
						// 야드저장위치제원(YMA7L001) 전문전송
						//----------------------------------------------------------------------
		    			jrInTemp.setField("YD_INFO_SYNC_CD"	, "3" ); 		//야드정보동기화코드(3:열)
		    			jrInTemp.setField("STACK_COL_GP"   	, ydStkColGp);  //야드적치열구분
						jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA7L001", jrInTemp));
		    			
						//------------------------------------------------------------------------------------------------------------
				    	//	차량스케줄 도착상태 변경 처리
				    	//------------------------------------------------------------------------------------------------------------
			
					    jrInTemp = JDTORecordFactory.getInstance().create();	 
					    jrInTemp.setResultCode(logId);	//Log ID
					    jrInTemp.setResultMsg(methodNm);	//Log Method Name
						jrInTemp.setField("MODIFIER"     	, modifier  ); //수정자
						jrInTemp.setField("YD_CAR_SCH_ID"	, ydCarSchId); 
						jrInTemp.setField("YD_CARUD_ARR_DT"	, commUtils.getDateTime14());
						jrInTemp.setField("YD_CAR_PROG_STAT", "B" );		//하차도착상태
						
						/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch
						UPDATE TB_YD_CARSCH
						   SET MODIFIER         = :V_MODIFIER
						     , MOD_DDTT         = SYSDATE
						     , DEL_YN           = NVL(:V_DEL_YN          , DEL_YN)
						     --하차
						     , YD_CAR_PROG_STAT     = NVL(:V_YD_CAR_PROG_STAT    , YD_CAR_PROG_STAT)
						     , YD_CARUD_WRK_BOOK_ID = NVL(:V_YD_CARUD_WRK_BOOK_ID, YD_CARUD_WRK_BOOK_ID)
						     , YD_CARUD_STOP_LOC    = NVL(:V_YD_CARUD_STOP_LOC   , YD_CARUD_STOP_LOC)
						     , YD_CARUD_ARR_DT      = NVL(:V_YD_CARUD_ARR_DT     , YD_CARUD_ARR_DT)
						     , YD_PNT_CD3           = NVL(:V_YD_PNT_CD3          , YD_PNT_CD3)
						     --상차
						     , YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_CARLD_WRK_BOOK_ID, YD_CARLD_WRK_BOOK_ID)
						     , YD_CARLD_STOP_LOC    = NVL(:V_YD_CARLD_STOP_LOC   , YD_CARLD_STOP_LOC)
						     , YD_CARLD_ARR_DT      = NVL(:V_YD_CARLD_ARR_DT     , YD_CARLD_ARR_DT)
						     , YD_PNT_CD1           = NVL(:V_YD_PNT_CD1          , YD_PNT_CD1)
						 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  

						*/ 
						commDao.update(jrInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch", logId, methodNm, "TB_YD_CARSCH 차량 상태변경");


						//----------------------------------------------------------------------------------------
						
						//------------------------------------------------------------------------------------------------------------
				    	//	재료정보 조회 (2단,1단 순)
				    	//------------------------------------------------------------------------------------------------------------				   
						JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
						jrParam1.setResultCode(logId);	//Log ID
			 			jrParam1.setResultMsg(methodNm);	//Log Method Name
						jrParam1.setField("TRANS_ORD_DATE2"	, transOrdDt);
						jrParam1.setField("TRANS_ORD_SEQNO2" 	, transOrdSeqNo);
			 			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockOfCarLoad 
			 			SELECT 
			 			       C.STACK_COL_GP||C.STACK_BED_GP|| C.STACK_LAYER_GP  AS UP_LOC
			 			     , C.STACK_LAYER_GP
			 			     , A.STOCK_ID
			 			     , B.YD_SCH_CD
			 			     , DECODE(LENGTH(A.TRANS_WORD_NO), 10, A.TRANS_WORD_NO
			 			     , SUBSTR(A.TRANS_WORD_NO, 1, 8) || '0' || SUBSTR(A.TRANS_WORD_NO, 9,1)) AS TRANS_WORD_DATE_NO
			 			     , B.YD_WBOOK_ID
			 			     , B.DEL_YN
			 			  FROM USRYMA.TB_YM_STOCK  A
			 			     , USRYMA.TB_YM_WRKBOOK  B
			 			     , USRYMA.TB_YM_WRKBOOKMTL  D
			 			     , USRYMA.TB_YM_STACKLAYER C
			 			 WHERE B.YD_WBOOK_ID=D.YD_WBOOK_ID
			 			   AND A.STOCK_ID = D.STOCK_ID
			 			   AND D.STOCK_ID = C.STOCK_ID
			 			   AND A.TRANS_ORD_DATE2=:V_TRANS_ORD_DATE2
			 			   AND A.TRANS_ORD_SEQNO2 =:V_TRANS_ORD_SEQNO2
			 			   AND B.DEL_YN = 'N'
			 			   AND D.DEL_YN = 'N'
			 			 ORDER BY C.STACK_LAYER_GP DESC, B.YD_WBOOK_ID */
						JDTORecordSet jsCarMtl = commDao.select(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockOfCarLoad", logId, methodNm, "재료정보 조회");					
						
						// 차량예정정보 송신
						jrParam = JDTORecordFactory.getInstance().create();
						jrParam.setResultCode(logId);	//Log ID
						jrParam.setResultMsg(methodNm);	//Log Method Name
			 			jrParam.setField("MODIFIER"		, modifier  ); //수정자
						jrParam.setField("YD_CAR_SCH_ID", ydCarSchId); //야드차량스케쥴ID
						jrParam.setField("SEARCH_FLAG"  , "2");  //1:상차도, 2:차량스케쥴 ID	
						jrRtn = commUtils.addSndData(jrRtn, ymComm.procCarPlanInfo(jrParam));

					
						/**********************************************************
						* Crane스케줄 호출
						*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
						*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
						**********************************************************/
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPntFrmYn 
						SELECT YD_CARPNT_CD
						      ,YD_STK_COL_ACT_STAT
						      ,YD_CAR_USETYPE_GP
						      ,WLOC_CD
						      ,YD_PNT_CD
						      ,NVL(YD_FRM_YN,'N') AS YD_FRM_YN
						FROM   TB_YD_CARPOINT
						WHERE  YD_STK_COL_GP = :V_YD_STK_COL_GP */

						jrParam.setField("YD_STK_COL_GP" , ydStkColGp); 	
						JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPntFrmYn", logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 ");
						if("N".equals(rsResult.getRecord(0).getFieldString("YD_FRM_YN"))) {
							
							//ejbConn = new EJBConnector("default", "BCoilSchSeEJB", this);
							JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
							
							//크레인 스케줄 기동 YMYMJ303 호출
							jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ303"); 
							jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
							jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
							jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
							
							int pcnt = 0;
							
							//2단 적치된 대상 스케줄 호출
							for(int i = 0; i < jsCarMtl.size(); i++) {
								
								if("02".equals(jsCarMtl.getRecord(i).getFieldString("STACK_LAYER_GP"))) {
									
									jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
								}
							}
							
							//1단 적치된 대상 스케줄 호출
							for(int i = 0; i < jsCarMtl.size(); i++) {
								
								if("01".equals(jsCarMtl.getRecord(i).getFieldString("STACK_LAYER_GP"))) {
									
									jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
									
								}
							}

							//작업예약 순서 대로 스케줄 호출
							//for(int i = 0; i < jsCarMtl.size(); i++) {
							//	
							//	jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
							//}
							
							jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(pcnt));
							
							jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
							
						}
						
						//저장품제원정보 송신
						for(int i = 0; i < jsCarMtl.size(); i++) {
							
							//======================================================
							// 저장품제원 : 코일야드L2 로 송신(YMA7L002)
							//======================================================
							JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
							sndL2Msg.setResultCode(logId);	//Log ID
							sndL2Msg.setResultMsg(methodNm);	//Log Method Name
							sndL2Msg.setField("YD_INFO_SYNC_CD"	, "R"       ); //야드정보동기화코드
							sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
							sndL2Msg.setField("STOCK_ID"       	, jsCarMtl.getRecord(i).getFieldString("STOCK_ID") ); //재료번호
								
							jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L002", sndL2Msg));	 //전송 Data 생성	
						}
						//----------------------------------------------------------------------------------------
						
						if("32".equals(CarldPntCd.substring(0,2)) && !"E".equals(CarldPntCd.substring(2,3))) {
							//E동이 아닌 A,B,C,D 동 2번 포인트 일 경우 
							//해당 포인트 입동지시 요구 호출
							
							// 입동지시 미리 주기 적용여부 확인
							String sAPPLY060 = ymComm.BCoilApplyYn("APP060","3","PREINWO_YN");
							commUtils.printLog(logId,  ">>> 입동지시 미리 주기 적용여부 (Y:적용, N:비적용) :" + sAPPLY060, "SL");
							if (sAPPLY060.equals("Y")) {						
								JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
								jrTemp.setResultCode(logId);	//Log ID
								jrTemp.setResultMsg(methodNm);	//Log Method Name
								jrTemp.setField("JMS_TC_CD"			, "YMYMJ662");          //차량입동지시 요구 
								jrTemp.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
								jrTemp.setField("YD_CARPNT_CD"		, CarldPntCd);		//입동포인트
								
								jrRtn = commUtils.addSndData(jrRtn, jrTemp);
							}
						}						
						
					}
				}	
			}							
					
//					
//					/*************************************** 
//					 * 	1.작업예약이 생성 
//					 **************************************/	
//					//작업예약,작업재료 등록	
//					String ydSchCd  = "";
//					if ("3".equals(ydStkColGp.substring(5 , 6)) || "4".equals(ydStkColGp.substring(5 , 6))) {
//						ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT06LM";
//					} else {
//						ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT02LM";
//					}
//					jrInTemp.setField("YD_SCH_CD"       	, ydSchCd   );// 자동이적 
//					jrInTemp.setField("CAR_NO"				, ydCarNo);
//					jrInTemp.setField("CARD_NO"				, ydCardNo);
//					
//	    			String ydWbookId = commComm.procCarWkBookInsert(jrInTemp);
//	    			
//	    			if(ydWbookId.equals(YmConstant.RETN_CD_FAILURE)){
//	    				throw new Exception("작업예약ID 생성 실패"); 				
//	    			}
//	    			
////					/**********************************************************
////					* 2.2 크레인스케줄 전문 호출
////					**********************************************************/
////					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
////					jrYdMsg.setResultCode(logId);	//Log ID
////					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
////
////					jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId	); //야드작업예약ID
////					jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  	); //야드스케쥴코드
////					jrYdMsg.setField("YD_SCH_ST_GP" , "O"		); //야드스케쥴기동구분
////					jrYdMsg.setField("YD_SCH_REQ_GP", "L"      	); //야드스케쥴요청구분(인출)
////					jrYdMsg.setField("MODIFIER"     , modifier 	); //수정자
////
////					jrRtn = commUtils.addSndData(commComm.getCrnSchMsg(jrYdMsg));				
//
//					//도착위치가 없을 시 도착처리 skip
//					if(ydCarKind.equals("TT")||ydCarKind.equals("T")){
//						ydCarKind="T";
//					}else{
//						if(ydCarKind.equals("TR")){
//							ydCarKind="R";
//						}else{
//							ydCarKind="N";
//						}
//					}
//					
//					if(!ydStkColGp.equals("")){
//						//차량도착처리 
//						jrParam.setField("MOVE_GP"		, ydCarKind); //T: TT,TR:R,그외:N
//						jrParam.setField("CARD_NO" 		, ydCardNo); 
//						jrParam.setField("STACK_COL_GP"	, ydStkColGp);
//						EJBConnector ejbConn = new EJBConnector("default","YmCommCarMvSeEJB",this);
//						jrRst = (JDTORecord)ejbConn.trx("procCarArr", new Class[] { JDTORecord.class }, new Object[] { jrParam });
//					}
//					 
//					//다음 작업예약 호출을 막기 위한 처리 
//					return jrRtn;
//				}
				
				//################################################################################################################
//			} 
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 코일이송하차완료PDA(DMYDR075) 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR075(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일이송하차완료PDA 수신[BCoilL3RcvSeEJB.rcvDMYDR075] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrRst = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp		= commUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String stlAppearGp 	= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));   //재료외형
			String StockId 		= commUtils.trim(rcvMsg.getFieldString("STL_NO"));    		//재료번호
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String dmTcCode 	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));//TC_CODE
			
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STOCK_ID" 	, StockId); 						
			jrParam.setField("MODIFIER" , modifier); //수정자

			JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);

			jrParam.setField("STOCK_MOVE_TERM" 	, commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"))); 
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStock_PIDEV
			UPDATE USRYMA.TB_YM_STOCK
			   SET MODIFIER         = :V_MODIFIER
			     , MOD_DDTT         = SYSDATE
			     , STOCK_MOVE_TERM  = NVL(:V_STOCK_MOVE_TERM , STOCK_MOVE_TERM)   --저장품이동조건
			     , YD_RULE_PL_RS_GP = NVL(:V_YD_RULE_PL_RS_GP, YD_RULE_PL_RS_GP)  --조합구분
			     , TRANS_WORD_NO    = :V_TRANS_ORD_DATE2||:V_TRANS_ORD_SEQNO2
			     , TRANS_ORD_DATE2  = NVL(:V_TRANS_ORD_DATE2 , TRANS_ORD_DATE2)   --운송지시
			     , TRANS_ORD_SEQNO2 = NVL(:V_TRANS_ORD_SEQNO2, TRANS_ORD_SEQNO2)  --운송지시행번
			     , SHEAR_SUPPLY_SEQ = NVL(:V_SHEAR_SUPPLY_SEQ, SHEAR_SUPPLY_SEQ)  --차상위치
			     , CAR_NO2          = NVL(:V_CAR_NO2         , CAR_NO2)           --차량번호 
			     , CAR_CARD_NO      = NVL(:V_CAR_CARD_NO     , CAR_CARD_NO)       --카드번호
			     , SHEAR_SUPPLY_GP  = NVL(:V_SHEAR_SUPPLY_GP , SHEAR_SUPPLY_GP)   --차량종류
			     , WBOOK_ID         = NVL(:V_WBOOK_ID        , WBOOK_ID)          --작업예약 사용안함
			     , CR_FRTOMOVE_GP   = NVL(:V_CR_FRTOMOVE_GP  , CR_FRTOMOVE_GP)    -- 냉연이송구분       
			 WHERE STOCK_ID = :V_STOCK_ID
			*/
			intRtnVal=	commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStock_PIDEV", logId, methodNm, "TB_YM_STOCK 수정");

			if(intRtnVal == 0){
				commUtils.printLog(logId, methodNm +" StockId" + StockId +"에 대한 저장품 DATA가 존재하지 않음", "SL");
				return jrRtn;
			}
			
			/*
		 	 *차량  자동출발 모듈 CALL
		 	 */
			
			String ydCardNo  	= "";  	
			String ydStackColGp = ""; 
			if(stlAppearGp.equals("*")){	

				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarStartOrderInfo_PIDEV 
				WITH TEMP_CARSCH AS 
				(
				 SELECT B.CARD_NO
				      , (CASE WHEN YD_CAR_PROG_STAT IN('1','2','3','4','5') THEN  B.YD_CARLD_STOP_LOC ELSE B.YD_CARUD_STOP_LOC END) AS  STACK_COL_GP
				      , B.YD_CAR_SCH_ID
				      , (SELECT YD_CARPNT_CD 
				           FROM USRYDA.TB_YD_CARPOINT
				          WHERE YD_STK_COL_GP=REPLACE(B.YD_CARLD_STOP_LOC,'TR0','TR1')
				        ) AS YD_CARPNT_CD
				   FROM TB_YM_STOCK A
				      , USRYDA.TB_YD_CARSCH B
				  WHERE A.TRANS_ORD_DATE2  = B.TRANS_ORD_DATE
				    AND A.TRANS_ORD_SEQNO2 = B.TRANS_ORD_SEQNO
				    AND A.CAR_NO2 = B.CAR_NO
				    AND B.DEL_YN='N'
				    AND A.STOCK_ID = :V_STOCK_ID
				 UNION 
				 SELECT B.CARD_NO
				      , (CASE WHEN YD_CAR_PROG_STAT IN('1','2','3','4','5') THEN  B.YD_CARLD_STOP_LOC ELSE B.YD_CARUD_STOP_LOC END) AS  STACK_COL_GP
				      , B.YD_CAR_SCH_ID
				      , (SELECT YD_CARPNT_CD 
				           FROM USRYDA.TB_YD_CARPOINT
				          WHERE YD_STK_COL_GP=REPLACE(B.YD_CARLD_STOP_LOC,'TR0','TR1')
				        ) AS YD_CARPNT_CD
				   FROM  TB_YM_STOCK A
				      , USRYDA.TB_YD_CARSCH B
				  WHERE A.TRANS_WORD_NO = B.TRANS_ORD_DATE||B.TRANS_ORD_SEQNO
				    AND A.CAR_CARD_NO   = B.CARD_NO
				    AND B.DEL_YN='N'
				    AND A.STOCK_ID= :V_STOCK_ID
				)
				SELECT *
				  FROM (
				        SELECT *
				          FROM (
				                --차량스케줄 존재
				                SELECT 1 AS CHK 
				                     , CARD_NO 
				                     , NVL((SELECT YD_STK_COL_GP FROM USRYDA.TB_YD_CARPOINT B
				                             WHERE B.CARD_NO=A.CARD_NO
				                               AND B.YD_STK_COL_GP LIKE SUBSTR(A.STACK_COL_GP,1,5) ||'%'
				                               AND ROWNUM<=1)
				                            , A.STACK_COL_GP ) AS STACK_COL_GP
				                      , YD_CAR_SCH_ID 
				                      , YD_CARPNT_CD
				                 FROM TEMP_CARSCH A
				                WHERE EXISTS(SELECT 1 FROM TEMP_CARSCH)
				                UNION ALL
				                --백업처리 한 경우
				                SELECT 2 AS CHK 
				                     , C.CARD_NO
				                     , REPLACE( (CASE WHEN C.YD_CAR_PROG_STAT IN('1','2','3','4','5') THEN  C.YD_CARLD_STOP_LOC ELSE C.YD_CARUD_STOP_LOC END),'TR1','TR0') AS YD_STK_COL_GP
				                     , C.YD_CAR_SCH_ID , B.YD_CARPNT_CD
				                  FROM TB_YM_STOCK A
				                     , USRYDA.TB_YD_CARSCH C
				                     , USRYDA.TB_YD_CARPOINT B
				                 WHERE A.TRANS_ORD_DATE2=C.TRANS_ORD_DATE
				                   AND A.TRANS_ORD_SEQNO2=C.TRANS_ORD_SEQNO
				                   AND (CASE WHEN C.YD_CAR_PROG_STAT IN('1','2','3','4','5') THEN  C.YD_CARLD_STOP_LOC ELSE C.YD_CARUD_STOP_LOC END)=B.YD_STK_COL_GP    
				                   AND A.DEL_YN='N'
				                   AND NOT EXISTS(SELECT 1 FROM TEMP_CARSCH)
				                   AND A.STOCK_ID = :V_STOCK_ID
				                UNION ALL
				                 --하이스코 초기화
				                SELECT 3 AS CHK 
				                     , C.CARD_NO
				                     , REPLACE((CASE WHEN C.YD_CAR_PROG_STAT IN('1','2','3','4','5') THEN  C.YD_CARLD_STOP_LOC ELSE C.YD_CARUD_STOP_LOC END),'TR1','TR0') AS YD_STK_COL_GP
				                     , C.YD_CAR_SCH_ID 
				                     , B.YD_CARPNT_CD
				                  FROM TB_YD_CARFTMVMTL A
				                     , USRYDA.TB_YD_CARSCH C
				                     , USRYDA.TB_YD_CARPOINT B
				                 WHERE A.YD_CAR_SCH_ID=C.YD_CAR_SCH_ID
				                   AND C.DEL_YN='N'
				                   AND (CASE WHEN C.YD_CAR_PROG_STAT IN('1','2','3','4','5') THEN  C.YD_CARLD_STOP_LOC ELSE C.YD_CARUD_STOP_LOC END)=B.YD_STK_COL_GP(+)
				                   AND NOT EXISTS(SELECT 1 FROM TEMP_CARSCH)
				                   AND A.STL_NO = :V_STOCK_ID
				                ) A
				        ORDER BY YD_CAR_SCH_ID DESC
				    ) B
				WHERE ROWNUM<=1
				*/
				JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarStartOrderInfo_PIDEV", logId, methodNm, "차량스케쥴 조회");
				if(jsCarSch.size() > 0) {
					commUtils.printLog(logId, methodNm +" StockId" + StockId +"에 대한 포인트정보가 존재함", "SL");
					ydCardNo  	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("CARD_NO"));  	
					ydStackColGp= commUtils.trim(jsCarSch.getRecord(0).getFieldString("STACK_COL_GP")); 
		 			
					jrParam.setField("CARD_NO"		, ydCardNo);   
					jrParam.setField("STACK_COL_GP" , ydStackColGp); 

		 			if(!ydCardNo.equals("")){
						//이송차량 출발 처리
						EJBConnector ejbConn1 = new EJBConnector("default", "YmCommCarMvSeEJB", this);
						JDTORecord jrRtn1 = (JDTORecord)ejbConn1.trx("procFrtoCarLevWr", new Class[] { JDTORecord.class }, new Object[] { jrParam });
						
						jrRtn = commUtils.addSndData(jrRtn, jrRtn1);		
		 			}
				}	
			}
		 	/*
		 	 *****************************************************************************
		 	 */
			String CarDnWrkYn 	= "N"; // 하차작업 여부
			if("DMYDR075".equals(msgId)){
				CarDnWrkYn = "Y";  //하차작업완료 인경우 생략 ,
			}
 			
			if(CarDnWrkYn.equals("N")){
				/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStacklayerList
				SELECT STOCK_ID , STACK_COL_GP 
				 FROM TB_YM_STACKLAYER
				WHERE STOCK_ID = :V_STOCK_ID
				 AND SUBSTR(STACK_COL_GP,3,2) NOT IN ('TR','PT','TT') --//차량위치가 아닌경우 	
				*/			
				JDTORecordSet jsStacklayer = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStacklayerList", logId, methodNm, "단정보검색");
				
				if(jsStacklayer.size() > 0 ){
					/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updateStacklayer
					update TB_YM_STACKLAYER
					set STOCK_ID=NULL
					, STACK_LAYER_ACTIVE_STAT='C'
					, STACK_LAYER_STAT='E'
					WHERE STOCK_ID = :V_STOCK_ID
					*/
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updateStacklayer", logId, methodNm, "TB_YM_STACKLAYER 수정");
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
	
	
	/**
	 * 출하전문 취소처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord receiveCancel(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "B열연 출하전문 취소처리[BCoilL3RcvSeEJB.receiveCancel] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
 
		//JDTORecord recStockColumn =JDTORecordFactory.getInstance().create();
		String 		sSTOCK_MOVE_TERM="";
		String 		sSchId	="";
		String 		sBookId	="";
 
		String      szMsg    = "";

    	JDTORecordSet jrStlNo	= null;
    	JDTORecordSet rsResult  = null;
    	JDTORecord jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
		JDTORecord jrRtn 		= JDTORecordFactory.getInstance().create();
    	
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			String msgId    			= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sYD_GP 				= commUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String sCURR_PROG_CD		= commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"));	    		
			String sTRANS_ORD_DT		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			String sTRANS_ORD_SEQNO		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String sFRTOMOVE_WORD_NO	= commUtils.trim(rcvMsg.getFieldString("FRTOMOVE_WORD_NO")); //이송 작업지시 번호
			String scardNo				= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
			String sSTL_NO 				= commUtils.trim(rcvMsg.getFieldString("STL_NO"));
			String modifier 			= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String scarNo				= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));//차량번호
			String sCarSchId			= commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"));//차량스케줄번호
			
			
			if ("".equals(modifier)) { modifier = msgId; }
			//변경자 설정 (insert,update 문에서 사용)
			jrParam.setField("MODIFIER", modifier); //수정자
			
	    	/*
			DMYDR008	코일제품반납대기		1.저장품 이동 조건 변경
			DMYDR013	외판슬라브목전			1.저장품 이동 조건 변경
			DMYDR014	코일제품목전			1.저장품 이동 조건 변경
			DMYDR016	외판슬라브운송지시대기	1.저장품 이동 조건 변경
			DMYDR029	외판슬라브출하완료 		1.저장품 이동 조건 변경
			DMYDR030	코일제품출하완료              1.저장품 이동 조건 변경
			DMYDR011	코일제품고간이송지시	1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함),저장품 이동 조건 변경
			DMYDR020	코일제품운송지시              1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함) ,저장품 이동 조건 변경
			DMYDR022	외판슬라브운송상차지시 	1.크레인 스케줄취소 ,2 작업예약취소 , 3. 카드번호삭제,저장품 이동 조건 변경
			DMYDR023	코일제품상차지시		1.크레인 스케줄취소 ,2 작업예약취소 , 3. 카드번호삭제,저장품 이동 조건 변경
			DMYDR026	외판슬라브보관지시		KEEPSTOCK_STL_YN= ''
			DMYDR027	코일제품보관지시		KEEPSTOCK_STL_YN= ''
	    	 */
			
	    	if(!YmConstant.DMYDR011.equals(msgId)){
		    	
		    	if("".equals(sSTL_NO)) {
					
	 				/**********************************************************
	 				* 운송지시번호,이송 작업지시 번호로 저장품 조회
	 				**********************************************************/  
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTransStock   
					SELECT STOCK_ID --출하,차량이적
					  FROM TB_YM_STOCK
					 WHERE TRANS_ORD_DATE2 = :V_TRANS_ORD_DATE2
					   AND TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO2
					   AND DEL_YN = 'N'
					 UNION 
					SELECT STOCK_ID --구내운송
					  FROM TB_YM_STOCK
					 WHERE FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO
					   AND DEL_YN = 'N' */
					jrParam.setField("TRANS_ORD_DATE2"	, sTRANS_ORD_DT);
					jrParam.setField("TRANS_ORD_SEQNO2"	, sTRANS_ORD_SEQNO);
					jrParam.setField("FRTOMOVE_WORD_NO"	, sFRTOMOVE_WORD_NO);
					jrStlNo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTransStock", logId, methodNm, "운송지시번호,이송 작업지시 번호로 저장품 조회");				
					if(jrStlNo.size() <= 0 ){
						//throw new Exception("출하/이송/이적 대상을 찾지 못했습니다!!");
						commUtils.printLog("", "출하/이송/이적 대상을 찾지 못했습니다!!", "[INFO]");
					}else{			
						sSTL_NO = commUtils.trim(jrStlNo.getRecord(0).getFieldString("STOCK_ID"));  
					}	
		    	} 
				
 				/**********************************************************
 				* 진도코드로 저장품이동조건을 가져온다.
 				**********************************************************/  
				jrParam.setField("STOCK_ID"		, sSTL_NO); 
				jrParam.setField("CURR_PROG_CD"	, sCURR_PROG_CD);
				JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd2(jrParam);
				sSTOCK_MOVE_TERM = commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")); 	    	
	      	}
	    	
	    	if(YmConstant.DMYDR008.equals(msgId)||  //코일제품반납대기
		       YmConstant.DMYDR013.equals(msgId)||  //외판슬라브목전
	    	   YmConstant.DMYDR014.equals(msgId)||  //코일제품목전
	    	   YmConstant.DMYDR016.equals(msgId)||  //외판슬라브운송지시대기
	    	   YmConstant.DMYDR029.equals(msgId)||  //외판슬라브출하완료
	    	   YmConstant.DMYDR030.equals(msgId)) { //코일제품출하완료
	    		
 				/**********************************************************
 				* 저장품 이동 조건 변경
 				**********************************************************/  
	    		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock  
	    		UPDATE TB_YM_STOCK
	    		   SET STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
	    		     , MOD_DDTT        = SYSDATE
	    		     , MODIFIER        = :V_MODIFIER
	    		     , DEL_YN          = 'N'
	    		 WHERE STOCK_ID = :V_STOCK_ID */
	    		jrParam.setField("STOCK_MOVE_TERM"	, sSTOCK_MOVE_TERM);
	    		jrParam.setField("STOCK_ID"			, sSTL_NO);
	    		commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock", logId, methodNm, "TB_YM_STOCK 저장품 이동 조건 변경");
	    		
	        } else 
	        if(YmConstant.DMYDR011.equals(msgId)||  //코일제품고간이송지시
	           YmConstant.DMYDR070.equals(msgId)||  //코일이송상차대기장도착PDA
	           YmConstant.DMYDR073.equals(msgId)||  //코일이송하차대기장도착PDA
	           YmConstant.DMYDR060.equals(msgId)||  //코일제품운송상차지시
	           YmConstant.DMYDR022.equals(msgId)) { //외판슬라브운송상차지시 
	        	
 				/**********************************************************
 				* 적치열 차량포인트 점유 정보 비우기
 				**********************************************************/  
	        	/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStackcolCarPint
	        	UPDATE TB_YM_STACKCOL
	        	SET  CAR_CARD_NO = NULL
	        	   , CARD_NO = NULL
	        	   , MODIFIER = :V_MODIFIER 
	        	   , MOD_DDTT= SYSDATE
	        	WHERE CAR_CARD_NO =(SELECT A.CAR_CARD_NO 
	        	                     FROM USRYMA.TB_YM_STOCK A
	        	                    WHERE  A.TRANS_ORD_DATE2 = :V_TRANS_ORD_DATE2
	        	                      AND  A.TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO2
	        	                      AND ROWNUM <=1 ) */
				jrParam.setField("TRANS_ORD_DATE2"	, sTRANS_ORD_DT);
				jrParam.setField("TRANS_ORD_SEQNO2"	, sTRANS_ORD_SEQNO);
			    commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStackcolCarPint", logId, methodNm, "TB_YM_STACKCOL 적치열 차량포인트 점유 정보 비우기");
			    
		        if(YmConstant.DMYDR011.equals(msgId)||  //코일제품고간이송지시
	 	    	   YmConstant.DMYDR070.equals(msgId)||  //코일이송상차대기장도착PDA
		           YmConstant.DMYDR073.equals(msgId)||  //코일이송하차대기장도착PDA
	 	    	   YmConstant.DMYDR060.equals(msgId)) { //코일제품운송상차지시
		        	
	 				/**********************************************************
	 				* 운송지시번호 삭제,저장품 이동 조건 변경
	 				**********************************************************/  
		    		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock6 
		    		UPDATE TB_YM_STOCK
		    		   SET STOCK_MOVE_TERM = decode(:V_STOCK_MOVE_TERM,'',STOCK_MOVE_TERM,:V_STOCK_MOVE_TERM) 
		    		      ,TRANS_WORD_NO = ''
		    		      ,CAR_CARD_NO = ''
		    		      ,CAR_NO2= ''
		    		      ,YD_RULE_PL_RS_GP= ''
		    		      ,TRANS_ORD_DATE2= ''
		    		      ,TRANS_ORD_SEQNO2= ''
		    		      ,CR_FRTOMOVE_GP= NULL
		    		      ,MOD_DDTT = SYSDATE
		    		      ,MODIFIER = :V_MODIFIER
		    		 WHERE TRANS_ORD_DATE2 = :V_TRANS_ORD_DATE2
		    		   AND TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO2 */
		    		if(YmConstant.DMYDR011.equals(msgId)) {
		    			jrParam.setField("STOCK_MOVE_TERM"	, "");
		    		} else {
		    			jrParam.setField("STOCK_MOVE_TERM"	, sSTOCK_MOVE_TERM);
		    		}
					jrParam.setField("TRANS_ORD_DATE2"	, sTRANS_ORD_DT);
					jrParam.setField("TRANS_ORD_SEQNO2"	, sTRANS_ORD_SEQNO);
		    		commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock6", logId, methodNm, "TB_YM_STOCK 운송지시번호 삭제,저장품 이동 조건 변경 ");	    		
		 				
		 	    } else if(YmConstant.DMYDR022.equals(msgId)) { //DMYDR022 외판슬라브운송상차지시 	  
		 	    	
	 				/**********************************************************
	 				* 카드번호삭제,저장품 이동 조건 변경
	 				**********************************************************/  
		 	    	/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock7 
		 	    	UPDATE TB_YM_STOCK
		 	    	   SET STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
		 	    	      ,CAR_CARD_NO = (CASE WHEN (SYSDATE-MOD_DDTT) > 0.001 THEN '' ELSE CAR_CARD_NO END)
		 	    	      ,MOD_DDTT = SYSDATE
		 	    	      ,MODIFIER = :V_MODIFIER
		 	    	 WHERE TRANS_ORD_DATE2 = :V_TRANS_ORD_DATE2
		 	    	   AND TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO2 */
		 	    	jrParam.setField("STOCK_MOVE_TERM"	, sSTOCK_MOVE_TERM);
					jrParam.setField("TRANS_ORD_DATE2"	, sTRANS_ORD_DT);
					jrParam.setField("TRANS_ORD_SEQNO2"	, sTRANS_ORD_SEQNO);
		    		commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock7", logId, methodNm, "TB_YM_STOCK 카드번호삭제,저장품 이동 조건 변경");	
		 	    }
		        
	 	    } else if(YmConstant.DMYDR027.equals(msgId)) { //코일제품보관지시
	 	    	
 				/**********************************************************
 				* 보관지시구분 KEEPSTOCK_STL_YN = ''
 				**********************************************************/  
	        	/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock2 
	        	UPDATE TB_YM_STOCK
	        	SET    KEEPSTOCK_STL_YN = :V_KEEPSTOCK_STL_YN
	        	      ,MODIFIER = :V_MODIFIER 
	        	      ,MOD_DDTT = SYSDATE
	        	WHERE  STOCK_ID = :V_STOCK_ID    */
	    		jrParam.setField("KEEPSTOCK_STL_YN"	, "");
	    		jrParam.setField("STOCK_ID"			, sSTL_NO);
	    		commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock2", logId, methodNm, "TB_YM_STOCK 보관지시구분 KEEPSTOCK_STL_YN= ''");
	        }
    	
			///////////////////////////////////////////////////////////////////////////////////////////////////
	    	if(!YmConstant.DMYDR011.equals(msgId)) {
	    		
	    		for(int ii = 0; ii < jrStlNo.size(); ii++) {
	    			
	    			sSTL_NO = commUtils.trim(jrStlNo.getRecord(ii).getFieldString("STOCK_ID"));  
			
	 				/**********************************************************
	 				* STOCK_ID로 스케줄 ID,작업예약ID 가져오기
	 				**********************************************************/  
					/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdWrkbookDelChk 
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
					   FROM DUAL */
	    			jrParam.setField("STOCK_ID", sSTL_NO);
	    			rsResult  = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdWrkbookDelChk", logId, methodNm, "STOCK_ID로 스케줄 ID,작업예약ID 가져오기");				
	    		
		    		if ( rsResult.size() > 0) {
		    			
			    		sSchId 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID"));   
			    		sBookId = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"));   
			        	
	        			if((sYD_GP.compareTo("1") == 0) || (sYD_GP.compareTo("3") == 0)) {
	        				
	        				if(!sSchId.equals("")) {
	        					
	        					/**********************************************************
	        					* 크레인스케줄 취소
	        					**********************************************************/	        					
	    			        	EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this); 
	    			        	JDTORecord jrRst = JDTORecordFactory.getInstance().create();
		        				jrParam.setField("YD_CRN_SCH_ID", sSchId);
		        				jrParam.setField("WRK_CNCL_YN", "Y"); //작업취소 여부
		        				jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
		        				jrRtn = commUtils.addSndData(jrRtn, jrRst);
	        				 
	        				}
		        			
		    				if(!sBookId.equals("")) {
		    					/**********************************************************
		    					* 작업예약 취소
		    					**********************************************************/
	    			        	EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this); 
	    			        	JDTORecord jrRst = JDTORecordFactory.getInstance().create();
		        			    jrParam.setField("YD_WBOOK_ID"  , sBookId);
		    					jrRst = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
		    					jrRtn = commUtils.addSndData(jrRtn, jrRst);
		    				}	
		    				 
	        			} else if( (sYD_GP.compareTo("2") == 0) || (sYD_GP.compareTo("4") == 0 || sYD_GP.compareTo("0") == 0) ) {
	        		      
	        				//ejbConn.trx("cancelSlabSchInfo",new Class[]{ String.class}, new Object[]{ sSchId });  //확인필요
	        			}
		    		}
		    		
	    		} //end of for 
	    	
	    		
	        	if(!"".equals(sCarSchId)){
	        		
	    			String ydCarProgStat    = "";
	    			String ydCarWrkGp 		= ""; 	//야드차량작업구분
	    			String ydEqpWrkStat		= ""; 	//야드설비작업상태
	    			String carLdStopLoc		= ""; 	//야드상차정지위치
	    			String carUdStopLoc		= ""; 	//야드하차정지위치
	    			String sCarStopLoc		= ""; 	//야드정지위치
	    			
	    			jrParam.setField("YD_CAR_SCH_ID", sCarSchId);
	    			
	    			
	    			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCarsch", logId, methodNm, "TB_YD_CARSCH 조회");
	    			if(jsCarSch.size() > 0 ){
	    				
	    				ydCarProgStat 	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT")); //차량진행상태
	    				ydCarWrkGp 		= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_WRK_GP")); 	//야드차량작업구분
	    				ydEqpWrkStat	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_EQP_WRK_STAT")); 	//야드설비작업상태
	    				
	    				carLdStopLoc	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CARLD_STOP_LOC")); 	//야드상차정지위치
	    				carUdStopLoc	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CARUD_STOP_LOC")); 	//야드하차정지위치
	    				 
	    				
	    				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSchMtl 
	    				UPDATE TB_YD_CARFTMVMTL
	    				   SET MODIFIER = :V_MODIFIER
	    				     , MOD_DDTT = SYSDATE
	    				     , DEL_YN = 'Y'
	    				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	    				 */  
	    				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSchMtl", logId, methodNm, "TB_YD_CARFTMVMTL 차량이송재로 종료처리");
	    				
	    				
	    				/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSch   
						UPDATE TB_YD_CARSCH
						   SET MODIFIER = :V_MODIFIER
						     , MOD_DDTT = SYSDATE
						     , DEL_YN = 'Y'
						 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
						     AND DEL_YN = 'N'
						*/
	    				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSch", logId, methodNm, "TB_YD_CARSCH 차량스케줄 종료 처리");

	    				
	    				// 상차, 하차 위치 찾기
	    				if ( "2".equals(ydCarProgStat) ||"3".equals(ydCarProgStat) ||"4".equals(ydCarProgStat) ||"5".equals(ydCarProgStat) ) {
	    					if ( !"".equals(carLdStopLoc) ) {
	    						sCarStopLoc = carLdStopLoc;//상차위치
	    					}
	    				}else if("B".equals(ydCarProgStat) ||"C".equals(ydCarProgStat) ||"D".equals(ydCarProgStat) ||"E".equals(ydCarProgStat)){
	    					if ( !"".equals(carUdStopLoc) ) {
	    						sCarStopLoc = carUdStopLoc;//하차위치
	    					}
	    				}
	    				
	    				if(!"".equals(sCarStopLoc)){
	    					jrParam.setField("STACK_COL_GP"			, sCarStopLoc);
	        				jrParam.setField("STACK_LAYER_ACTIVE_STAT", "E");
	    		    		jrParam.setField("STACK_LAYER_STAT", "E");
	    		    		
	    		    		/*UPDATE USRYMA.TB_YM_STACKLAYER           
	    					   SET MOD_DDTT     = SYSDATE             
	    						 , MODIFIER     = :V_MODIFIER             
	    						 , STACK_LAYER_ACTIVE_STAT  = :V_STACK_LAYER_ACTIVE_STAT 
	    					     , STOCK_ID  = null
	    					     , STACK_LAYER_STAT  = :V_STACK_LAYER_STAT
	    					 WHERE STACK_COL_GP = :V_STACK_COL_GP*/	
	    							    		
	        				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YM_STACKLAYER 차량 적치단 정보 초기화");
	        				
	    			    	jrParam.setField("STACK_COL_ACTIVE_STAT", "L");
	    			    	jrParam.setField("YD_CAR_USE_GP"		, "");
	    			    	jrParam.setField("TRN_EQP_CD"			, "");
	    			    	jrParam.setField("CAR_NO"				, "");
	    			    	jrParam.setField("CARD_NO"				, "");
	    			    	
	        				/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkcol
	        				UPDATE TB_YM_STACKCOL
	        				   SET MOD_DDTT      = SYSDATE             
	        					 , MODIFIER      = :V_MODIFIER   
	        					 , YD_CAR_USE_GP = :V_YD_CAR_USE_GP      
	        					 , TRN_EQP_CD    = :V_TRN_EQP_CD           
	        					 , CAR_NO        = :V_CAR_NO               
	        					 , CARD_NO       = :V_CARD_NO  
	        				     , STACK_COL_ACTIVE_STAT = :V_STACK_COL_ACTIVE_STAT
	        				 WHERE STACK_COL_GP  = :V_STACK_COL_GP
	        				*/
	        				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkcol", logId, methodNm, "TB_YM_STACKCOL 차량 적치열 정보 초기화");


					        //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					        EJBConnector ejbConn2 = new EJBConnector("default","YmCommCarMvSeEJB",this);
							ejbConn2.trx("YmCarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class},
						  	             new Object[]{"B","","",sCarStopLoc,"","","C",logId,methodNm});
							
	    				}
	    			}//jsCarSch.size() > 0
	        	}else{
	        		
					/**********************************************************
					* TB_YM_STACKLAYER 하차인 경우 차량위치재료 종료처리
					**********************************************************/ 
	        		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.modifyCardNoOflayerEND_PIDEV
	        		UPDATE TB_YM_STACKLAYER
	        		   SET MODIFIER = :V_MODIFIER
	        		     , MOD_DDTT = SYSDATE 
	        		     , STOCK_ID = NULL
	        		     , STACK_LAYER_STAT='E'
	        		 WHERE STACK_COL_GP IN ( 
	        		        SELECT YD_CARUD_STOP_LOC
	        		          FROM USRYDA.TB_YD_CARSCH A
	        		         WHERE A.DEL_YN = 'N'
	        		           AND A.YD_CAR_PROG_STAT IN('A','B','C','D','E') --//하차
	        		           AND A.CARD_NO = :V_CARD_NO 
	        		           AND A.CAR_NO = :V_CAR_NO 
	        		       )
	        		  AND SUBSTR(STACK_COL_GP ,3,2) ='PT'*/
	    			jrParam.setField("CARD_NO", scardNo);
	    			jrParam.setField("CAR_NO", scarNo);
	    			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.modifyCardNoOflayerEND_PIDEV", logId, methodNm, "TB_YM_STACKLAYER 하차인 경우 차량위치재료 종료처리");
	    			
	        		
					/**********************************************************
					* TB_YD_CARFTMVMTL 차량이송재로 종료처리
					**********************************************************/ 
	    			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.modifyCardNoOfDetailEND_PIDEV
	    			UPDATE USRYDA.TB_YD_CARFTMVMTL
	    			   SET MODIFIER = :V_MODIFIER
	    			     , MOD_DDTT = SYSDATE 
	    			     , DEL_YN = 'Y'
	    			 WHERE YD_CAR_SCH_ID IN (
	    			 
	    			        SELECT YD_CAR_SCH_ID
	    			          FROM USRYDA.TB_YD_CARSCH
	    			         WHERE DEL_YN = 'N'
	    			           AND CARD_NO = :V_CARD_NO 
	    			           AND CAR_NO = :V_CAR_NO
	    			       ) */
	    			jrParam.setField("CARD_NO", scardNo);
	    			jrParam.setField("CAR_NO", scarNo);
	    			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.modifyCardNoOfDetailEND_PIDEV", logId, methodNm, "TB_YD_CARFTMVMTL 차량이송재로 종료처리");
	    			
					/**********************************************************
					* TB_YD_CARSCH 차량스케줄 종료 처리
					**********************************************************/ 
	    			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.modifyCardNoOfEND_PIDEV
	    			UPDATE USRYDA.TB_YD_CARSCH
	    			   SET MODIFIER = :V_MODIFIER
	    			     , MOD_DDTT = SYSDATE 
	    			     , DEL_YN = 'Y'
	    			 WHERE DEL_YN = 'N'
	    			   AND CARD_NO = :V_CARD_NO
	    			   AND CAR_NO = :V_CAR_NO		*/
	    			jrParam.setField("CARD_NO", scardNo);
	    			jrParam.setField("CAR_NO", scarNo);
	    			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.modifyCardNoOfEND_PIDEV", logId, methodNm, "TB_YD_CARSCH 차량스케줄 종료 처리");
			        
					/**********************************************************
					* TB_YM_STACKCOL 적치열 차량예약 포인트 지우기
					**********************************************************/ 
	    			/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateCardNoOfStackCol1 
	    			UPDATE  TB_YM_STACKCOL 
	    			   SET  CARD_NO = NULL
	    			      , CAR_CARD_NO = NULL
	    			      , CAR_NO = NULL
	    			      , YD_CAR_USE_GP = NULL
	    			WHERE   CARD_NO = :V_CARD_NO
	    			  AND   CAR_NO = :V_CAR_NO  */
	    			jrParam.setField("CARD_NO", scardNo);
	    			jrParam.setField("CAR_NO", scarNo);
				    commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateCardNoOfStackCol1_PIDEV", logId, methodNm, "TB_YM_STACKCOL 적치열 차량예약 포인트 지우기");
			        
			        //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			        EJBConnector ejbConn2 = new EJBConnector("default","YmCommCarMvSeEJB",this);
					ejbConn2.trx("YmCarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class},
				  	             new Object[]{"A",scarNo,scardNo,"","","","C",logId,methodNm});
	        	}
	        	
	    	}
	    	///////////////////////////////////////////////////////////////////////////////////////////////////
         
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/**	
	 * [A] 오퍼레이션명 : 코일 SPM/HFL 작업 요구 정보를 수신(rcvPOYMJ004_2HFL)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPOYMJ004_2HFL(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일 2HFL 작업 요구 정보를 수신[BCoilL3RcvSeEJB.rcvrcvPOYMJ004_2HFL] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrRst = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp 		= commUtils.trim(rcvMsg.getFieldString("YardId"));			// 야드 구분
			String WorkId 		= commUtils.trim(rcvMsg.getFieldString("WorkId"));			// 공정코드 S:SPM,H:HFL,D:결속대   // 2SPM  처리
			String procId 		= commUtils.trim(rcvMsg.getFieldString("ProcessId"));		// 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
			String CoilNo 		= commUtils.trim(rcvMsg.getFieldString("CoilNo"));			// 'S' Scrap  'H' A열연  'K' B열연 
			String position 	= commUtils.trim(rcvMsg.getFieldString("Position"));		// 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
			String takeOutProc 	= commUtils.trim(rcvMsg.getFieldString("TakeOutProcess"));	// Take-Out시  1:결번,2:임시보류처리(잠시 내려놨다가 Take-In할 Coil)
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			commUtils.printParam("", rcvMsg);

			JDTORecord rcvMsg1 = JDTORecordFactory.getInstance().create();
			rcvMsg1.setResultCode(logId);	//Log ID
			rcvMsg1.setResultMsg(methodNm);	//Log Method Name
			rcvMsg1.setField("MODIFIER"     , modifier  ); //수정자
			rcvMsg1.setField("YARDID"   	, ydGp);
			rcvMsg1.setField("WORKID" 		, WorkId);
			rcvMsg1.setField("PROCESSID"	, procId);
			rcvMsg1.setField("COILNO"   	, CoilNo);
			rcvMsg1.setField("POSITION"   	, position);
			rcvMsg1.setField("MODIFIER"		, modifier);
			
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"  , modifier  ); //수정자
			jrParam.setField("STOCK_ID"  , CoilNo);

			boolean isSuccess = false;
			
			if(procId.equals("2")){	
				/***********************************
				 * 보급 취소
				 **********************************/
				
				commUtils.printLog(logId, methodNm + "보급취소: " + CoilNo , "SL");	
				
				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdWrkbookDelChk 
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
				
				JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdWrkbookDelChk2", logId, methodNm, "크레인스케줄재료 조회");

				String ydCrnSchId = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID")); //크레인 작업지시
				String ydWbookId  = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID"));  //작업예약ID

				commUtils.printLog(logId, "작업취소 [ " + ydCrnSchId + " - " + ydWbookId + " ]", "SL");
				
				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
				if(!ydCrnSchId.equals("")) {
					/**********************************************************
					* 1.1 크레인스케줄 취소
					**********************************************************/
					jrParam.setField("WRK_CNCL_YN", "Y"); //작업취소 여부
					jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					jrRtn = commUtils.addSndData(jrRtn, jrRst);
				}
				
				if(!ydWbookId.equals("")) {
					/**********************************************************
					* 2.1 작업예약 취소
					**********************************************************/
					jrRst = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					jrRtn = commUtils.addSndData(jrRtn, jrRst);
				}	
				
			} else if (procId.equals(YmConstant.PROCESS_ID_1)){                           // 보급
				
				/***********************************
				 * 보급처리
				 **********************************/			
				commUtils.printLog(logId, methodNm + "SPM 보급: " + CoilNo , "SL");	
                  
				if(WorkId.equals("D")) {       ///HFL 결속대

					/****  스케줄/작업예약 생성/ 스케쥴 호출  *****/
					jrRtn = this.callLineInOut_2HFL(logId, methodNm, ydGp, WorkId, procId, CoilNo,modifier);
					
					return jrRtn;
				}
				
				
			} else if (procId.equals(YmConstant.PROCESS_ID_3)){                           // 추출
				/***********************************
				 * 추출처리
				**********************************/		
				//stock 에 없는 경우 생성처리
				this.procCoilStock(logId, methodNm, ydGp, WorkId, procId, CoilNo,modifier);
				
				/****  스케줄/작업예약 생성/ 스케쥴 호출  *****/
				jrRtn = this.callLineInOut_2HFL(logId, methodNm, ydGp, WorkId, procId, CoilNo,modifier);
				return jrRtn;
						
			}	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
 	/**
	 * 오퍼레이션명 : SPM / HFL 보급, 추출, Take-In
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public JDTORecord callLineInOut_2HFL(String logId, String methodNms, String YardID, String WorkID, String ProcessID, String CoilNo, String modifier){	
		String methodNm = "SPM / HFL 보급, 추출, Take-In[BCoilL3RcvSeEJB.callLineInOut_2HFL] < " + methodNms;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STOCK_ID"	, CoilNo        ); //
			jrParam.setField("MODIFIER"	, modifier        ); //
			
			/*************************************** 
			 * 	작업예약이 존재하면 ERROR
			 **************************************/	

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookYN 
			SELECT DECODE(COUNT(*),0,'N','Y') AS WB_STL_YN --작업예약재료여부
			  FROM TB_YM_WRKBOOKMTL WM
			     , TB_YM_WRKBOOK    WB
			WHERE WM.YD_WBOOK_ID    = WB.YD_WBOOK_ID
			  AND WM.STOCK_ID       = :V_STOCK_ID
			  AND WM.DEL_YN         = 'N'
			  AND WB.DEL_YN         = 'N'
			*/  	  
			JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookYN", logId, methodNm, "작업예약 등록여부");

			if (jsChk2 != null && jsChk2.size() > 0) {
				if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN"))) {
					commUtils.printLog(logId, "작업요구한 CoilNo이 이미 작업예약되어 있슴  Error : "+ CoilNo, "SL");
					return jrRtn;	
				}
			}	
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
			JDTORecordSet jsStackLayer = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp1", logId, methodNm, "TB_YM_STACKLAYER 정보");

			if (jsStackLayer.size() < 1) {
				commUtils.printLog(logId, "StackColGp 이상: "+ CoilNo, "SL");
				return jrRtn;				
	    	} 	
			jsStackLayer.first();
			JDTORecord jrStackLayer = jsStackLayer.getRecord();
			String ydStackColGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP"),     "");
			String ydGp			= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP1"),    "");
			String ydBayGp		= commUtils.nvl(jrStackLayer.getFieldString("STACK_COL_GP2"),    "");
			String ydStackBedGp	= commUtils.nvl(jrStackLayer.getFieldString("STACK_BED_GP"),     "");
			String ydStackLayer	= commUtils.nvl(jrStackLayer.getFieldString("STACK_LAYER_GP"),   "");
			String TmpstackCol 	= "";
			String ydSchCd 		= "";  //SCH_CD
			String toLocGuide 	= "";  //TO위치 가이드

			if (ydStackColGp != null && !ydStackColGp.equals("")){

				/*************************************** 
				 * 	 HFL 결속대
				 **************************************/						
				if (ProcessID.equals(YmConstant.PROCESS_ID_1)){                          
					/*****  보급처리   ************/
					ydSchCd 	= "3"+ydBayGp+"HS01UM" ;
					toLocGuide  = "3"+ydBayGp+"HS01" ;
				}else if (ProcessID.equals(YmConstant.PROCESS_ID_3)){
					
					/*****  추출처리   ************/						
					ydSchCd 	= "3"+ydBayGp+"HS01LM" ;
					ydStackLayer = "01";
				}

				if(ydSchCd.equals("")){
    				throw new Exception("스케쥴 코드 생성 실패"); 				
    			}
				
				/*************************************** 
				 * 	1.작업예약이 생성 
                 *  2.STOCK UPDATE(작업행선)  
                 *  3.스케쥴 호출
				 **************************************/	
  			//작업예약,작업재료 등록		
    			JDTORecord jrOutTemp = JDTORecordFactory.getInstance().create();
    			jrOutTemp.setField("STL_NO"           , commUtils.trim(jrOutTemp.getFieldString("STOCK_ID"))); //재료번호
    			jrOutTemp.setField("STACK_COL_GP"     , ydStackColGp  ); 
    			jrOutTemp.setField("STACK_BED_GP"     , ydStackBedGp  ); 
    			jrOutTemp.setField("STACK_LAYER_GP"   , ydStackLayer ); 
    			jrOutTemp.setField("YD_SCH_CD"        , ydSchCd   );// 자동이적 
    			jrOutTemp.setField("MODIFIER"         , modifier    ); //수정자
    			jrOutTemp.setField("YD_TO_LOC_GUIDE"  , toLocGuide    ); //TO위치가이드
    			
    			String ydWbookId = ymComm.procWkBookInsert(jrOutTemp);
    			
    			if(ydWbookId.equals(YmConstant.RETN_CD_FAILURE)){
    				throw new Exception("작업예약ID 생성 실패"); 				
    			}
				
				/*************************************** 
				 * 	1.저장품 수정
                 *  2.STOCK UPDATE(작업행선)  
                 *  3.스케쥴 호출
				 **************************************/
				// 저장품 Table(TB_YM_STOCK)에 작업예약ID,저장품이동조건을 Update 한다.	
				
				String sStackMoveTerm = "";
				
				// HFL결속대
				if(ProcessID.equals(YmConstant.PROCESS_ID_3)){
					// HFL결속대  대한 코드는 고정으로 적용한다.
					sStackMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_A7; //HFL 결속대 추출 							
				}else{
					sStackMoveTerm = YmConstant.NEW_STOCK_MOVE_TERM_CC; //HFL 결속대 보급
				}

				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("MODIFIER" 		, modifier);    	//수정자
				jrParam.setField("WBOOK_ID" 		, ydWbookId); 		//작업예약
				jrParam.setField("PROCESS_ID" 		, ProcessID);     	//처리구분
				jrParam.setField("STOCK_MOVE_TERM" 	, sStackMoveTerm);  //저장품이동조건
				jrParam.setField("STOCK_ID"			, CoilNo);   		//
				
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo1 
				UPDATE TB_YM_STOCK
				   SET MODIFIER   = :V_MODIFIER
				     , MOD_DDTT   = SYSDATE 
				     , SHEAR_SUPPLY_GP = CASE WHEN :V_PROCESS_ID = 1 THEN ''
				                              ELSE SHEAR_SUPPLY_GP          END   -- 정정보급구분 
				     , SHEAR_SUPPLY_DEMAND_DDTT = CASE WHEN :V_PROCESS_ID = 1 THEN ''
				                              ELSE SHEAR_SUPPLY_DEMAND_DDTT END --정정보급요구일시
				     , WBOOK_ID = :V_WBOOK_ID                              
				     , STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM  --저장품이동조건
				 WHERE STOCK_ID = :V_STOCK_ID
				*/ 
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo1", logId, methodNm, "TB_YM_STOCK 수정");
				
				
				/**********************************************************
				* 크레인 스케쥴이 있으면 기동처리 안함
				**********************************************************/
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchCdCnt  
				SELECT DECODE(COUNT(*),0,'Y','N') AS SCH_CD_CNT  
				  FROM TB_YM_CRNSCH
				WHERE YD_SCH_CD = :V_YD_SCH_CD 
				     AND DEL_YN = 'N'
				*/
				jrParam.setField("YD_SCH_CD" , ydSchCd);   		//
		    	JDTORecordSet jsStackLayer1 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchCdCnt", logId, methodNm, "TB_YM_STACKLAYER 정보");

				if (jsStackLayer1.size() > 0) {
					if ("Y".equals(jsStackLayer1.getRecord(0).getFieldString("SCH_CD_CNT"))) {
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

						jrRtn = commUtils.addSndData(ymComm.getCrnSchMsg(jrYdMsg));		
					}
		    	} 	    	 
						
					
				commUtils.printLog(logId, methodNm, "S-");
				
			}	
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return jrRtn;
	} 	
	
	
	/**	
	 *      [A] 오퍼레이션명 : 냉각코일 자동 이적 기능 (A동)(YMYMJ307)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ307(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "신고도화 냉각코일 자동 이적 기능 (A동) [BCoilL3RcvSeEJB.rcvYMYMJ307] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			String sUP_STOCK_ID     = "";//권상코일
			String sUP_STACK_COL_GP = "";//권상위치

			String sYD_CRN_SCH_ID   = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); //크레인 스케줄
			String sUP_YD_EQP_ID    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     //작업크레인
			
			String sWRK_EQUIP       = ""; //작업크레인
			if (!"".equals(sUP_YD_EQP_ID)) {
				if ("1".equals(sUP_YD_EQP_ID.substring(5, 6))) {
					sWRK_EQUIP = sUP_YD_EQP_ID.substring(0, 5) + "2";
				} else if ("2".equals(sUP_YD_EQP_ID.substring(5, 6))) {
					sWRK_EQUIP = sUP_YD_EQP_ID.substring(0, 5) + "1";
				} 
			}
			
			String sSTOCK_ID = ""; //작업예약 대상 저장품
			String sSTACK_COL_GP   = "";
			String sSTACK_BED_GP   = "";
			String sSTACK_LAYER_GP = "";
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sYD_CRN_SCH_ID)) {
				throw new Exception("크레인스케줄 이상");
			}
			
			jrParam.setField("YD_CRN_SCH_ID"    , sYD_CRN_SCH_ID);
			JDTORecordSet jrSchInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getSchInfoWithSchId", logId, methodNm, "크레인스케줄 조회");
			
			if (jrSchInfo.size() <= 0) {
				commUtils.printLog(logId, "해당 크레인스케줄 존재하지 않음", "SL");
				return jrRtn;
			}
			
			sUP_STOCK_ID     = jrSchInfo.getRecord(0).getFieldString("STOCK_ID");
			sUP_STACK_COL_GP = jrSchInfo.getRecord(0).getFieldString("YD_UP_WO_LOC").substring(0, 6); //권상 위치
			
			jrParam.setField("STOCK_ID"    , sUP_STOCK_ID);
			jrParam.setField("STACK_COL_GP", sUP_STACK_COL_GP);
						
			String sYD_GP      = sUP_STACK_COL_GP.substring(0, 1);
			String sYD_BAY_GP  = sUP_STACK_COL_GP.substring(1, 2);
			String sYD_SECT_GP = sUP_STACK_COL_GP.substring(2, 4); //
			
			jrParam.setField("YD_GP"    , sYD_GP    );
			jrParam.setField("YD_BAY_GP", sYD_BAY_GP);
			
			/********************************************
			 * 상대크레인이 수행할 작업이 존재하면 종료
			 ********************************************/
			/*
			SELECT *
			  FROM (
			        SELECT CASE WHEN BB.WRK_WPROG_STAT <> 'B' AND BB.WRK_WORK_MODE = '1' THEN AA.YD_WRK_CRN
			                    WHEN CC.ALT_WORK_MODE  <> 'B' AND CC.ALT_WORK_MODE = '1' THEN AA.YD_ALT_CRN
			                    ELSE 'XXXXXX' END AS WRK_CRANE
			             , AA.*   
			          FROM (
			                SELECT A1.*
			                     , NVL(A1.YD_WRK_PLAN_CRN, B1.YD_WRK_CRN) AS YD_WRK_CRN
			                     , B1.YD_ALT_CRN
			                  FROM TB_YM_WRKBOOK A1
			                     , TB_YM_WRKBOOKMTL A2
			                     , TB_YM_SCHEDULERULE B1
			                WHERE A1.YD_WBOOK_ID = A2.YD_WBOOK_ID
			                   AND A1.DEL_YN = 'N'  
			                   AND A2.DEL_YN = 'N'  
			                   AND A1.YD_GP  = '3'
			                   AND A1.YD_SCH_CD = B1.YD_SCH_CD
			               ) AA
			             , (SELECT EQUIP_GP, WPROG_STAT AS WRK_WPROG_STAT,WORK_MODE AS WRK_WORK_MODE FROM TB_YM_EQUIP) BB
			             , (SELECT EQUIP_GP, WPROG_STAT AS ALT_WPROG_STAT,WORK_MODE AS ALT_WORK_MODE FROM TB_YM_EQUIP) CC
			         WHERE AA.YD_WRK_CRN = BB.EQUIP_GP
			           AND AA.YD_ALT_CRN = CC.EQUIP_GP  
			      )
			WHERE WRK_CRANE = :V_YD_EQP_ID
			 */
			jrParam.setField("YD_EQP_ID", sWRK_EQUIP);
			JDTORecordSet jsWrkList = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWrkListByEqpId", logId, methodNm, "작업 존재 조회");
			if (jsWrkList.size() > 0) {
				commUtils.printLog(logId, "상대크레인의 작업이 있으므로 자동이적 생성안함", "[INFO]");
				return jrRtn;
			} 
			
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getAutoMvstkYNByEqpId 
			WITH PARAM AS (
			SELECT :V_YD_EQP_ID  AS P_YD_EQP_ID 
			  FROM DUAL
			)
			SELECT CASE WHEN P_YD_EQP_ID = '3ACRA2' AND (SELECT COUNT(*)
			                                               FROM TB_YM_EQPTRACKING ET
			                                                  , TB_PT_COILCOMM    CC
			                                              WHERE ET.YD_GP = '3'
			                                                AND ET.PROC_GP = 'D'
			                                                AND ET.EQUIP_GP IN ('DT15','DT16','DT17')
			                                                AND ET.STL_NO = CC.COIL_NO
			                                                AND CC.NEXT_PROC IN ('5H')) > 0 THEN 'N'
			         --권상예정위치 내 영역인가(CR0003)
			            WHEN P_YD_EQP_ID = '3ACRA2' AND (SELECT COUNT(*) 
			                                               FROM TB_YM_RULE 
							                              WHERE CD_GP = P_YD_EQP_ID
							                                AND DEL_YN = 'N' 
							                                AND REPR_CD_GP = 'CR0003'  
							                                AND ITEM = :V_STACK_COL_GP) = 0  THEN 'N'                                                
			            ELSE 'Y' END AS MVSTK_YN                                                   
			 FROM PARAM 
			 */
			JDTORecordSet jsMvstkYn = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getAutoMvstkYNByEqpId", logId, methodNm, "작업 수행여부 조회");
			if (jsMvstkYn.size() > 0) {
				if ("N".equals(jsMvstkYn.getRecord(0).getFieldString("MVSTK_YN"))){
					commUtils.printLog(logId, "자동이적 생성 조건 만족하지 않음", "[INFO]");
					return jrRtn;	
				}
			}
			
			/*********************************************
			 * 2. 이적대상 추출
			 *  - 압연시간
			 *  - 2단 우선
			 *  - 유사규격
			 *********************************************/
			/*
			WITH BEFORE_COIL_INFO AS (
			SELECT :V_YD_GP        AS P_YD_GP
			     , :V_YD_BAY_GP    AS P_YD_BAY_GP 
			  FROM DUAL
			)
			SELECT ABS(TO_NUMBER(SUBSTR(A.STACK_COL_GP,3,2)) -TO_NUMBER(SUBSTR(A.HOT_STACK_COL_GP,3,2))) AS RANK_CHK
			
			       , A.*
			 FROM (
			    SELECT A.*
			          , (SELECT STACK_COL_GP FROM USRYMA.TB_YM_STACKLAYER B
			             WHERE B.STOCK_ID=A.HOT_STOCK_ID AND ROWNUM<=1) AS HOT_STACK_COL_GP
			          ,TO_NUMBER(( SELECT  --1~4스판 적치율 
			                   ROUND(SUM(CASE WHEN STACK_LAYER_STAT IN ('C','U') THEN 1 ELSE 0 END)
			                   /COUNT(*) *100) AS STR_RATE
			                FROM USRYMA.TB_YM_STACKLAYER SL
			             WHERE SL.STACK_COL_GP LIKE '3A%'
			               AND SUBSTR(SL.STACK_COL_GP,3,2) BETWEEN '01' AND '04'
			               AND STACK_LAYER_ACTIVE_STAT='E') ) AS STR_RATE   
			      FROM (
			            SELECT SL.STACK_COL_GP
			                 , SL.STACK_BED_GP
			                 , SL.STACK_LAYER_GP
			                 , SL.STOCK_ID 
			                 , CC.COIL_CREATE_DDTT AS COIL_CREATE_DT --코일생성일시
			                    ,(CASE WHEN TO_NUMBER(TRUNC((SYSDATE - CC.COIL_CREATE_DDTT) * 24)) < 48
			                             THEN 'TRUE'
			                             ELSE 'FALSE'
			                        END)  AS IS_HOTCOIL  -- 냉각경과일자에 따른 분기별 핫코일 정의 
			
			                   ,(CASE WHEN SL.STACK_LAYER_GP ='01' AND (SELECT COUNT(1) FROM TB_YM_STACKLAYER A
			                                                            WHERE A.STACK_COL_GP=SL.STACK_COL_GP
			                                                              AND A.STACK_BED_GP IN(SL.STACK_BED_GP,LPAD(TO_NUMBER(SL.STACK_BED_GP)-1,2,'0'))
			                                                              AND A.STACK_LAYER_GP='02'
			                                                              AND A.STACK_LAYER_STAT <>'E')>0 --2단에 코일이 있는경우
			                                                THEN 'N' ELSE 'Y' END ) AS DAN_CHK  
			                ,MAX(SL.STOCK_ID) OVER (PARTITION BY   SUBSTR(SL.STACK_COL_GP,1,2) ORDER BY CC.COIL_CREATE_DDTT DESC ) AS HOT_STOCK_ID   
			                ,NEXT_PROC 
			                ,(SELECT COUNT(*) FROM USRYMA.TB_YM_CRNSCH WHERE YD_SCH_CD='3ADC01LM' AND DEL_YN='N' AND ROWNUM<=1) AS CRN_CHK
			                ,( SELECT COUNT(*) FROM TB_YM_EQPTRACKING WHERE YD_GP ='3'  AND STACK_COL_GP = '3AST01'  AND STL_NO IS NOT NULL) AS TRACK_CHK
			                ,CC.CURR_PROG_CD
			              FROM TB_YM_STACKLAYER  SL
			                 , TB_PT_COILCOMM    CC 
			                 , BEFORE_COIL_INFO  INFO
			             WHERE 1 = 1
			              AND SL.STOCK_ID=CC.COIL_NO
			              AND SL.STACK_COL_GP LIKE P_YD_GP||P_YD_BAY_GP||'%'
			              AND CC.CURR_PROG_CD in('E','B')       --이송대기,정정작업대기재
			              AND SUBSTR(SL.STACK_COL_GP,3,2) BETWEEN '05' AND '08' --hot coil 대상영역    
			              AND SL.STACK_LAYER_STAT='C' 
			              AND NOT EXISTS (SELECT STOCK_ID FROM USRYMA.TB_YM_WRKBOOKMTL WB WHERE WB.STOCK_ID=SL.STOCK_ID AND DEL_YN='N')
			           ) A
			      WHERE IS_HOTCOIL='FALSE' --냉각코일
			        AND DAN_CHK='Y'  --더미가 없는 경우
			     ) A
			 WHERE 1=1
			   AND CRN_CHK=0  --수입 없고
			   AND TRACK_CHK=0 --트레킹 존재 안함
			   AND STR_RATE<85 --1~4스판 적치율 80% 미만 인경우
			 ORDER BY RANK_CHK , STACK_COL_GP , STACK_BED_GP DESC , STACK_LAYER_GP
			 */
			JDTORecordSet jrMvstStock = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getSmlrStockIdForAutoMvstk", logId, methodNm, "이적대상 추출");
			if (jrMvstStock.size() <= 0) {
				commUtils.printLog(logId, "자동 이적 대상 없음", "SL");
				return jrRtn;
			}
			
			jrMvstStock.absolute(1);
			sSTOCK_ID       = jrMvstStock.getRecord().getFieldString("STOCK_ID");
			sSTACK_COL_GP   = jrMvstStock.getRecord().getFieldString("STACK_COL_GP");  
			sSTACK_BED_GP   = jrMvstStock.getRecord().getFieldString("STACK_BED_GP");  
			sSTACK_LAYER_GP = jrMvstStock.getRecord().getFieldString("STACK_LAYER_GP");
			
			/****************************************************
			 * 3. 작업예약 등록
			 ****************************************************/
			String ydSchCd = sYD_GP + sYD_BAY_GP;
			String sLtRtGp = "";
			
			jrParam.setField("REPR_CD_GP", "SCH001");
			jrParam.setField("CD_GP"     , "LTRT_RULE");
			jrParam.setField("ITEM"      , sYD_BAY_GP);
			
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "스케줄코드 좌우기준 조회");
			
			if (rsResult.size() <= 0 ) {
				commUtils.printLog(logId, "해당 동의 스케줄코드 좌우 구분이 없습니다", "SL");
				return jrRtn;
			}
			int nLtRtRule = Integer.parseInt(rsResult.getRecord(0).getFieldString("DTL_ITM1"));
			int nSECT_GP  = Integer.parseInt(sYD_SECT_GP); 
			
			if (nSECT_GP < nLtRtRule) {
				sLtRtGp = "L";
			} else if (nSECT_GP >= nLtRtRule) {
				sLtRtGp = "R";
			}
			
			if ("L".equals(sLtRtGp)) {
				ydSchCd = ydSchCd + "YD01MM";
			} else if ("R".equals(sLtRtGp)) { 
				ydSchCd = ydSchCd + "YD05MM";
			} else {
				ydSchCd = ydSchCd + "YD01MM";//default
			}
	
			jrParam.setField("STL_NO"         , sSTOCK_ID); //재료번호
			jrParam.setField("YD_SCH_CD"      , ydSchCd   ); 
			jrParam.setField("YD_TO_LOC_GUIDE", sUP_STACK_COL_GP);
			jrParam.setField("STACK_COL_GP"   , sSTACK_COL_GP);
			jrParam.setField("STACK_BED_GP"   , sSTACK_BED_GP);
			jrParam.setField("STACK_LAYER_GP" , sSTACK_LAYER_GP);
			jrParam.setField("MODIFIER"       , "AUTO_MV_A");
			jrParam.setField("YD_EQP_ID"      , sWRK_EQUIP); // 작업대상 크레인
			
			String ydWbookId = ymComm.procWkBookInsert(jrParam);
			
			if (ydWbookId.equals(YmConstant.RETN_CD_FAILURE)) {
				//throw new Exception("작업예약ID 생성 실패");
				commUtils.printLog(logId, "작업예약ID 생성 실패" + sSTOCK_ID, "SL");

				return jrRtn;
			} else {
				jrRtn.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			}	
			/***********************************************************************
			 * 4. 스케줄 자동기동 여부가 N이면 스케줄 기동하지 않음 
			 ***********************************************************************/
			/*
			SELECT *
			  FROM TB_YM_SCHEDULERULE
			 WHERE YD_SCH_CD = :V_YD_SCH_CD
			 */
			jrParam.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드
			JDTORecordSet jsSchInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchCdInfo", logId, methodNm, "TB_YM_SCHEDULERULE 조회");
			
			if(jsSchInfo.size() > 0 ) {
				
				String sYD_SCH_AUTO_ST_YN = jsSchInfo.getRecord(0).getFieldString("YD_SCH_AUTO_ST_YN");
			
				if ("Y".equals(sYD_SCH_AUTO_ST_YN)) {
					
					/**********************************************************
					* 5. 크레인스케줄 전문 호출
					**********************************************************/
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
		
					jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
					jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
					jrYdMsg.setField("YD_SCH_ST_GP" , "O"         ); //야드스케쥴기동구분
					jrYdMsg.setField("YD_SCH_REQ_GP", "L"         ); //야드스케쥴요청구분(인출)
					jrYdMsg.setField("MODIFIER"     , "YMYMJ307"  ); //수정자
		
					jrRtn = commUtils.addSndData(ymComm.getCrnSchMsg(jrYdMsg));		
				} else {
					commUtils.printLog(logId, "자동기동 안됨" + sSTOCK_ID + "    자동기동 여부 : " +sYD_SCH_AUTO_ST_YN, "SL");
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

	/**	
	 *      [A] 오퍼레이션명 : 신고도화 설비 작업 더미 자동이적 기능 (YMYMJ308)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ308(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "신고도화 설비 작업 더미 자동이적 기능 [BCoilL3RcvSeEJB.rcvYMYMJ308] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			String sDummyCoil   	= commUtils.trim(rcvMsg.getFieldString("DUMMY_COIL_NO"));	 //dummy 코일
			String sDummyCoilLoc   	= commUtils.trim(rcvMsg.getFieldString("DUMMY_COIL_LOC"));	 //dummy 코일 위치
			String sOtherCrane  	= commUtils.trim(rcvMsg.getFieldString("OTHER_CRANE"));    	 //작업크레인

			/********************************************
			 * 1. PARAM CHECK
			 ********************************************/
			
			if ("".equals(sOtherCrane)) {
				commUtils.printLog(logId, "크레인 정보 없음 .sOtherCrane:" + sOtherCrane, "SL");
				return jrRtn;
			}
			if ("".equals(sDummyCoil)) {
				commUtils.printLog(logId, "더미코일 정보 없음 .sDummyCoil:" + sDummyCoil, "SL");
				return jrRtn;
			}
			if ("".equals(sDummyCoilLoc)) {
				commUtils.printLog(logId, "더미코일 위치정보 없음 .sDummyCoilLoc:" + sDummyCoilLoc, "SL");
				return jrRtn;
			}
			
			String ydGp     	= sOtherCrane.substring(0,1); //야드 구분
			String ydBayGp  	= sOtherCrane.substring(1,2); //동
			String stackColGp  	= sDummyCoilLoc.substring(0,6); 
			String stackBedGp  	= sDummyCoilLoc.substring(6,8); 
			String stackLayerGp = sDummyCoilLoc.substring(8,10);
			
			String ydSpan 	= sDummyCoilLoc.substring(0, 4); 
			
			jrParam.setField("YD_GP"    , ydGp    );
			jrParam.setField("YD_BAY_GP", ydBayGp );
			
			/****************************************************
			 * 3. 작업예약 등록
			 ****************************************************/
			String ydSchCd = ydGp + ydBayGp;
			String sLtRtGp = "";
			
			jrParam.setField("REPR_CD_GP", "SCH001");
			jrParam.setField("CD_GP"     , "LTRT_RULE");
			jrParam.setField("ITEM"      , ydBayGp);
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule
			SELECT REPR_CD_GP
			     , CD_GP
			     , ITEM
			     , REPR_CD_CONTENTS
			     , REGISTER
			     , REG_DDTT
			     , MODIFIER
			     , TO_CHAR(MOD_DDTT, 'YYYY/MM/DD HH24:MI:SS') AS MOD_DDTT
			     , DEL_YN
			     , DTL_ITM1
			     , DTL_ITM2
			     , DTL_ITM3
			     , DTL_ITM4
			     , DTL_ITM5
			     , DTL_ITM6
			     , DTL_ITM7
			     , DTL_ITM8
			     , DTL_ITM9
			     , DTL_ITM10
			  FROM USRYMA.TB_YM_RULE
			 WHERE REPR_CD_GP = :V_REPR_CD_GP                  
			   AND CD_GP      = NVL(:V_CD_GP, CD_GP)
			   AND ITEM       = NVL(:V_ITEM , ITEM )
			   AND DEL_YN     = 'N'
			 ORDER BY CD_GP
			        , ITEM
			*/        
			
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "스케줄코드 좌우기준 조회");
			
			if (rsResult.size() <= 0 ) {
//				throw new Exception("해당 동의 스케줄코드 좌우 구분이 없습니다.");
				return jrRtn;
			}
			int nLtRtRule = Integer.parseInt(rsResult.getRecord(0).getFieldString("DTL_ITM1"));
			int nSECT_GP  = Integer.parseInt(sDummyCoilLoc.substring(2, 4)); 
			
			if (nSECT_GP < nLtRtRule) {
				sLtRtGp = "L";
			} else if (nSECT_GP >= nLtRtRule) {
				sLtRtGp = "R";
			}
			if("A".equals(ydBayGp)) {
				ydSchCd = ydSchCd + "YD21MM";//A동
			} else {
				if ("L".equals(sLtRtGp)) {
					ydSchCd = ydSchCd + "YD21MM";
				} else if ("R".equals(sLtRtGp)) { 
					ydSchCd = ydSchCd + "YD25MM";
				} else {
					ydSchCd = ydSchCd + "YD21MM";//default
				}
			}	
			
			jrParam.setField("STL_NO"         , sDummyCoil   ); //재료번호
			jrParam.setField("YD_SCH_CD"      , ydSchCd      ); 
			jrParam.setField("YD_TO_LOC_GUIDE", ydSpan       );
			jrParam.setField("STACK_COL_GP"   , stackColGp   );
			jrParam.setField("STACK_BED_GP"   , stackBedGp   );
			jrParam.setField("STACK_LAYER_GP" , stackLayerGp );
			jrParam.setField("MODIFIER"       , "AUTO_MV_B");
			jrParam.setField("YD_EQP_ID"      , sOtherCrane  ); // 작업대상 크레인
			
			String ydWbookId = ymComm.procWkBookInsert(jrParam);
			
			if (ydWbookId.equals(YmConstant.RETN_CD_FAILURE)) {
				//throw new Exception("작업예약ID 생성 실패");
				commUtils.printLog(logId, "작업예약ID 생성 실패" + sDummyCoil, "SL");

				return jrRtn;
			} else {
				jrRtn.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			}	
			/***********************************************************************
			 * 4. 스케줄 자동기동 여부가 N이면 스케줄 기동하지 않음 
			 ***********************************************************************/
			/*
			SELECT *
			  FROM TB_YM_SCHEDULERULE
			 WHERE YD_SCH_CD = :V_YD_SCH_CD
			 */
			jrParam.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드
			JDTORecordSet jsSchInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchCdInfo", logId, methodNm, "TB_YM_SCHEDULERULE 조회");
			
			if(jsSchInfo.size() > 0 ) {
				
				String sYD_SCH_AUTO_ST_YN = jsSchInfo.getRecord(0).getFieldString("YD_SCH_AUTO_ST_YN");
			
				if ("Y".equals(sYD_SCH_AUTO_ST_YN)) {
					
					/**********************************************************
					* 5. 크레인스케줄 전문 호출
					**********************************************************/
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
		
					jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
					jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
					jrYdMsg.setField("YD_SCH_ST_GP" , "O"         ); //야드스케쥴기동구분
					jrYdMsg.setField("YD_SCH_REQ_GP", "L"         ); //야드스케쥴요청구분(인출)
					jrYdMsg.setField("MODIFIER"     , "YMYMJ308"  ); //수정자
		
					jrRtn = commUtils.addSndData(ymComm.getCrnSchMsg(jrYdMsg));		
				} else {
					commUtils.printLog(logId, "자동기동 안됨" + sDummyCoil + "    자동기동 여부 : " +sYD_SCH_AUTO_ST_YN, "SL");
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
	

	/**	
	 *      [A] 오퍼레이션명 : E동 자동 동내이적(YMYMJ310)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ310(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "E동 자동 동내이적[BCoilL3RcvSeEJB.rcvYMYMJ310] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			String sYD_EQP_ID  = "3ECRE3";//commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     //작업크레인
			
			String sYD_GP      = "3";
			String sYD_BAY_GP  = "E";
			
			String sSTOCK_ID       = ""; //작업예약 대상 저장품
			String sSTACK_COL_GP   = "";
			String sSTACK_BED_GP   = "";
			String sSTACK_LAYER_GP = "";			
			
			String sYD_SCH_CD      = "3EYD31MM"; //E동 자동 동내이적
			
			jrParam.setField("YD_GP"    , sYD_GP);
			jrParam.setField("YD_BAY_GP", sYD_BAY_GP);
			
			/*********************************************
			 * 0. 크레인스케줄 생성안된 작업예약 체크
			 *********************************************/
			/*
			SELECT *
			  FROM TB_YM_WRKBOOK
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = '3EYD31MM'  
			 */
			JDTORecordSet jsWrkList = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getAutoStkMvWrkListEBay", logId, methodNm, "자동이적 작업 조회");
			if (jsWrkList.size() > 0) {
				commUtils.printLog(logId, "자동이적 작업 이미 존재함", "[INFO]");
				return jrRtn;
			}
			
			/*********************************************
			 * 1. 이적대상 추출
			 * - 11~13 Span -> 16, 17 Span
			 *********************************************/
			/*
			SELECT SL.*
			  FROM TB_YM_STACKLAYER  SL
			     , TB_PT_COILCOMM    CC
			 WHERE SL.STOCK_ID = CC.COIL_NO
			   AND STOCK_ID IS NOT NULL
			   AND STACK_LAYER_STAT = 'C'
			   AND STACK_COL_GP BETWEEN '3E1101' AND '3E1399'
			   AND 'Y' = (SELECT CASE WHEN WPROG_STAT = 'W' AND WORK_MODE = '1' THEN 'Y' ELSE 'N' END AS WRK_YN
			                FROM TB_YM_EQUIP
			               WHERE EQUIP_GP = '3ECRE3'
			                 AND DEL_YN   = 'N' 
			             )
			   AND 'Y' = (SELECT CASE WHEN DTL_ITM1 = 'Y' AND (TO_CHAR(SYSDATE, 'HH24') > DTL_ITM2 
			                                                OR TO_CHAR(SYSDATE, 'HH24') < DTL_ITM3)
			                          THEN  'Y'
			                     ELSE 'N' END AS APP_YN
			                FROM USRYMA.TB_YM_RULE 
			               WHERE REPR_CD_GP = 'APP047'
			                 AND CD_GP = '3'
			                 AND ITEM  = '1'             
			              )
			   AND CC.CURR_PROG_CD = 'K' --출하지시 대기재
			   AND NOT EXISTS (SELECT *
			                     FROM TB_YM_WRKBOOK    WB
			                        , TB_YM_WRKBOOKMTL WM
			                    WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
			                      AND WB.DEL_YN = 'N'
			                      AND WM.DEL_YN = 'N'
			                      AND WM.STOCK_ID = SL.STOCK_ID
			                  )
			 ORDER BY STACK_LAYER_GP DESC
			        , STACK_COL_GP
			        , STACK_BED_GP
			 */
			JDTORecordSet jrMvstStock = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getAutoStkMvListEBay", logId, methodNm, "이적대상 추출");
			if (jrMvstStock.size() <= 0) {
				commUtils.printLog(logId, "자동 이적 대상 없음", "[INFO]");
				return jrRtn;
			}
			
			jrMvstStock.absolute(1);
			sSTOCK_ID       = jrMvstStock.getRecord().getFieldString("STOCK_ID");
			sSTACK_COL_GP   = jrMvstStock.getRecord().getFieldString("STACK_COL_GP");  
			sSTACK_BED_GP   = jrMvstStock.getRecord().getFieldString("STACK_BED_GP");  
			sSTACK_LAYER_GP = jrMvstStock.getRecord().getFieldString("STACK_LAYER_GP");		
			
			/****************************************************
			 * 2. 작업예약 등록
			 ****************************************************/
			jrParam.setField("STOCK_ID"       , sSTOCK_ID); //재료번호
			jrParam.setField("YD_SCH_CD"      , sYD_SCH_CD); 
			jrParam.setField("STACK_COL_GP"   , sSTACK_COL_GP);
			jrParam.setField("STACK_BED_GP"   , sSTACK_BED_GP);
			jrParam.setField("STACK_LAYER_GP" , sSTACK_LAYER_GP);
			jrParam.setField("MODIFIER"       , "AUTO_MV_E");
			jrParam.setField("YD_EQP_ID"      , sYD_EQP_ID); // 작업대상 크레인
			
			String ydWbookId = ymComm.procWkBookInsert(jrParam);
			
			if (ydWbookId.equals(YmConstant.RETN_CD_FAILURE)) {
				commUtils.printLog(logId, "작업예약ID 생성 실패" + sSTOCK_ID, "SL");
				return jrRtn;
			} 	
			
			/***********************************************************************
			 * 3. TO위치 지정 가능한지 체크
			 ***********************************************************************/
			jrParam.setField("YD_SCH_CD"      , sYD_SCH_CD); //크레인스케줄코드
			jrParam.setField("YD_WBOOK_ID"    , ydWbookId);  //작업예약ID
			jrParam.setField("STOCK_ID"       , sSTOCK_ID);  //재료번호
			jrParam.setField("YD_EQP_ID"      , sYD_EQP_ID); //작업대상 크레인
			jrParam.setField("YD_UP_WO_LOC"   , sSTACK_COL_GP + sSTACK_BED_GP); 
			jrParam.setField("YD_UP_WO_LAYER" , sSTACK_LAYER_GP); 
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilSchSeEJB", this);
			String sLocAbleRtn = (String)ejbConn.trx("procToLocTest", new Class[] { String.class, String.class, JDTORecord.class }, new Object[] { logId, methodNm, jrParam});
			
			if(YmConstant.RETN_CD_FAILURE.equals(sLocAbleRtn)) {
				//TO위치를 찾지 못하면 앞에서 만든 작업예약 ID를 삭제한다.
				
				jrParam.setField("YD_WBOOK_ID" , ydWbookId);    //삭제할 작업예약ID
				jrParam.setField("MODIFIER"    , "CANCEL310");  //수정자에 CANCEL310 등록 --> com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getAutoStkMvListEBay 쿼리에서 CANCEL310 으로 취소된 작업예약이 있으면  대상에서 제외한다.
				
				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBookMtl 
				--작업예약재료 삭제 
				UPDATE TB_YM_WRKBOOKMTL
				   SET MODIFIER    = :V_MODIFIER
				      ,MOD_DDTT    = SYSDATE
				      ,DEL_YN      = 'Y'
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND DEL_YN      = 'N' */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBookMtl", logId, methodNm, "작업예약재료 삭제");				

				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBook
				--작업예약 삭제
				UPDATE TB_YM_WRKBOOK
				   SET MODIFIER    = :V_MODIFIER
				      ,MOD_DDTT    = SYSDATE
				      ,DEL_YN      = 'Y'
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND DEL_YN      = 'N' */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBook", logId, methodNm, "작업예약 삭제");				
				
				return jrRtn;
			}
			
			
			/***********************************************************************
			 * 4. 스케줄 자동기동 여부가 N이면 스케줄 기동하지 않음 
			 ***********************************************************************/
			/*
			SELECT *
			  FROM TB_YM_SCHEDULERULE
			 WHERE YD_SCH_CD = :V_YD_SCH_CD
			 */
			jrParam.setField("YD_SCH_CD", sYD_SCH_CD); //야드스케쥴코드
			JDTORecordSet jsSchInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchCdInfo", logId, methodNm, "TB_YM_SCHEDULERULE 조회");
			
			if(jsSchInfo.size() > 0 ) {
				
				String sYD_SCH_AUTO_ST_YN = jsSchInfo.getRecord(0).getFieldString("YD_SCH_AUTO_ST_YN");
			
				if ("Y".equals(sYD_SCH_AUTO_ST_YN)) {
					
					/**********************************************************
					* 4. 크레인스케줄 전문 호출
					**********************************************************/
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
		
					jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
					jrYdMsg.setField("YD_SCH_CD"    , sYD_SCH_CD  ); //야드스케쥴코드
					jrYdMsg.setField("YD_SCH_ST_GP" , "O"         ); //야드스케쥴기동구분
					jrYdMsg.setField("YD_SCH_REQ_GP", "L"         ); //야드스케쥴요청구분(인출)
					jrYdMsg.setField("MODIFIER"     , "YMYMJ310"  ); //수정자
		
					jrRtn = commUtils.addSndData(ymComm.getCrnSchMsg(jrYdMsg));		
				} else {
					commUtils.printLog(logId, "자동기동 안됨" + sSTOCK_ID + "    자동기동 여부 : " +sYD_SCH_AUTO_ST_YN, "SL");
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
		
	
	/**	
	 *      [A] 오퍼레이션명 : C동 자동 동내이적(YMYMJ311)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ311(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C동 자동 동내이적[BCoilL3RcvSeEJB.rcvYMYMJ311] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			String sYD_EQP_ID  = "3CCRC3";//commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     //작업크레인
			
			String sYD_GP      = "3";
			String sYD_BAY_GP  = "C";
			
			String sSTOCK_ID       = ""; //작업예약 대상 저장품
			String sSTACK_COL_GP   = "";
			String sSTACK_BED_GP   = "";
			String sSTACK_LAYER_GP = "";			
			
			String sYD_SCH_CD      = "3CYD31MM"; //E동 자동 동내이적
			
			jrParam.setField("YD_GP"    , sYD_GP);
			jrParam.setField("YD_BAY_GP", sYD_BAY_GP);
			
			/*********************************************
			 * 0. 크레인스케줄 생성안된 작업예약 체크
			 *********************************************/
			/*
			SELECT *
			  FROM TB_YM_WRKBOOK
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = '3CYD31MM'  
			 */
			JDTORecordSet jsWrkList = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getAutoStkMvWrkListCBay", logId, methodNm, "자동이적 작업 조회");
			if (jsWrkList.size() > 0) {
				commUtils.printLog(logId, "자동이적 작업 이미 존재함", "[INFO]");
				return jrRtn;
			}
			
			/*********************************************
			 * 1. 이적대상 추출
			 * - 13~14 Span -> 17~ Span
			 *********************************************/
			/*
			SELECT SL.*
			  FROM TB_YM_STACKLAYER  SL
			     , TB_PT_COILCOMM    CC
			     ,(
			         SELECT *
			          FROM (
			        SELECT S2.COIL_NO        AS STOCK_ID
			             , (SELECT STACK_COL_GP||STACK_BED_GP||STACK_LAYER_GP 
			                  FROM TB_YM_STACKLAYER 
			                 WHERE STOCK_ID = S2.COIL_NO
			                   AND STACK_LAYER_STAT IN ('C','U')
			                   AND ROWNUM = 1) AS  NEXT_COIL_UP_LOC
			          FROM TB_HR_C_SHEARWOUNIT     S1,    -- 정정작업단위순위
			               TB_HR_C_SHEARWOPRIOR    S2,    -- 정정지시순위
			               TB_HR_C_SHEARWOWR       S3,    -- 정정지시실적
			               TB_PT_COILCOMM          S4,    -- COIL공통
			                (SELECT A.*
			                      , SUBSTR(A.PROC_GP,1,1)||DECODE(SUBSTR(A.PROC_GP,2,1),'K','R') as PROC_GP2               
			                   FROM 
			                      (
			                        SELECT 'B'        AS HR_PLNT_GP
			                             , '5K' AS PROC_GP   -- 5H(HFL),5K(SPM1),6K(SPM2)
			                          FROM dual
			                          ) A
			                ) DD       
			        WHERE  S1.HR_PLNT_GP            = DD.HR_PLNT_GP
			           AND  (S3.WORD_PROC = DD.PROC_GP OR S3.WORD_PROC = DD.PROC_GP2)  
			           AND  S1.HR_PLNT_GP            = S2.HR_PLNT_GP
			           AND  S1.PROC_GP               = S2.PROC_GP
			           AND  S1.PUT_PRIOR            >= 1
			           AND  S1.PUT_PRIOR            <= 100
			           AND  S1.WORD_UNIT_NAME        = S2.WORD_UNIT_NAME
			           AND  S1.WORK_STAT             IN ('3','5')            -- 2:투입전, 3:작업중,  *:작업완료, 5: 보관매출(BY강태경)
			           AND  S2.WORK_STAT             in ('2','X','R')        -- 2:진행중, X:보류취소, R:임시보류
			           AND  S3.HR_PLNT_GP            = S2.HR_PLNT_GP
			           AND  S3.PROC_GP               = S2.PROC_GP
			           AND  S3.WORD_UNIT_NAME        = S2.WORD_UNIT_NAME
			           AND  S3.COIL_NO               = S2.COIL_NO
			           AND  S3.STEP_NO               = S2.STEP_NO
			           AND  EXISTS(SELECT 1 FROM TB_HR_C_SHEARWOWR SS WHERE SS.WORK_STAT IN( 'B') AND SS.WORD_UNIT_NAME=S1.WORD_UNIT_NAME) --추가(보급요구 중인 단위)
			           AND  S2.STEP_NO               = (SELECT MAX(Y.STEP_NO)  
			                                             FROM TB_HR_C_SHEARWOPRIOR Y   
			                                            WHERE Y.COIL_NO        = S2.COIL_NO 
			                                              AND Y.HR_PLNT_GP     = S2.HR_PLNT_GP
			                                              AND Y.PROC_GP        = S2.PROC_GP
			                                              AND Y.WORD_UNIT_NAME = S2.WORD_UNIT_NAME)
			           AND  S4.COIL_NO               = S2.COIL_NO
			           AND  DECODE(S1.WORK_STAT,'5','C',S4.CURR_PROG_CD)   = 'C'  
			        ORDER BY S1.PUT_PRIOR, S2.WORD_UNIT_SEQNO
			              )
			         WHERE ROWNUM < 6
			           AND NEXT_COIL_UP_LOC LIKE '3C%'
			      ) SUP_TRGT
			 WHERE SL.STOCK_ID = CC.COIL_NO
			   AND SL.STOCK_ID IS NOT NULL
			   AND SL.STACK_LAYER_STAT = 'C'
			   AND SL.STACK_COL_GP BETWEEN '3C1301' AND '3C1499'
			   AND 'Y' = (SELECT CASE WHEN WPROG_STAT = 'W' AND WORK_MODE = '1' THEN 'Y' ELSE 'N' END AS WRK_YN
			                FROM TB_YM_EQUIP
			               WHERE EQUIP_GP = '3CCRC3'
			                 AND DEL_YN   = 'N' 
			             )
			   AND 'Y' = (SELECT CASE WHEN DTL_ITM1 = 'Y' AND (TO_CHAR(SYSDATE, 'HH24') > DTL_ITM2 
			                                                OR TO_CHAR(SYSDATE, 'HH24') < DTL_ITM3)
			                          THEN  'Y'
			                     ELSE 'N' END AS APP_YN
			                FROM USRYMA.TB_YM_RULE 
			               WHERE REPR_CD_GP = 'APP047'
			                 AND CD_GP = '3'
			                 AND ITEM  = 'C'             
			              )
			   AND NOT EXISTS (SELECT *
			                     FROM TB_YM_WRKBOOK    WB
			                        , TB_YM_WRKBOOKMTL WM
			                    WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
			                      AND WB.DEL_YN = 'N'
			                      AND WM.DEL_YN = 'N'
			                      AND WM.STOCK_ID = SL.STOCK_ID
			                  )
			   AND SL.STOCK_ID != SUP_TRGT.STOCK_ID
			 ORDER BY STACK_LAYER_GP DESC
			        , STACK_COL_GP
			        , STACK_BED_GP
			 */
			JDTORecordSet jrMvstStock = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getAutoStkMvListCBay", logId, methodNm, "이적대상 추출");
			if (jrMvstStock.size() <= 0) {
				commUtils.printLog(logId, "자동 이적 대상 없음", "[INFO]");
				return jrRtn;
			}
			
			jrMvstStock.absolute(1);
			sSTOCK_ID       = jrMvstStock.getRecord().getFieldString("STOCK_ID");
			sSTACK_COL_GP   = jrMvstStock.getRecord().getFieldString("STACK_COL_GP");  
			sSTACK_BED_GP   = jrMvstStock.getRecord().getFieldString("STACK_BED_GP");  
			sSTACK_LAYER_GP = jrMvstStock.getRecord().getFieldString("STACK_LAYER_GP");		
			
			/****************************************************
			 * 2. 작업예약 등록
			 ****************************************************/
			jrParam.setField("STOCK_ID"       , sSTOCK_ID); //재료번호
			jrParam.setField("YD_SCH_CD"      , sYD_SCH_CD); 
			jrParam.setField("STACK_COL_GP"   , sSTACK_COL_GP);
			jrParam.setField("STACK_BED_GP"   , sSTACK_BED_GP);
			jrParam.setField("STACK_LAYER_GP" , sSTACK_LAYER_GP);
			jrParam.setField("MODIFIER"       , "AUTO_MV_C");
			jrParam.setField("YD_EQP_ID"      , sYD_EQP_ID); // 작업대상 크레인
			
			String ydWbookId = ymComm.procWkBookInsert(jrParam);
			
			if (ydWbookId.equals(YmConstant.RETN_CD_FAILURE)) {
				commUtils.printLog(logId, "작업예약ID 생성 실패" + sSTOCK_ID, "SL");
				return jrRtn;
			} 	
			
			/***********************************************************************
			 * 3. TO위치 지정 가능한지 체크
			 ***********************************************************************/
			jrParam.setField("YD_SCH_CD"      , sYD_SCH_CD); //크레인스케줄코드
			jrParam.setField("YD_WBOOK_ID"    , ydWbookId);  //작업예약ID
			jrParam.setField("STOCK_ID"       , sSTOCK_ID);  //재료번호
			jrParam.setField("YD_EQP_ID"      , sYD_EQP_ID); //작업대상 크레인
			jrParam.setField("YD_UP_WO_LOC"   , sSTACK_COL_GP + sSTACK_BED_GP); 
			jrParam.setField("YD_UP_WO_LAYER" , sSTACK_LAYER_GP); 
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilSchSeEJB", this);
			String sLocAbleRtn = (String)ejbConn.trx("procToLocTest", new Class[] { String.class, String.class, JDTORecord.class }, new Object[] { logId, methodNm, jrParam});
			
			if(YmConstant.RETN_CD_FAILURE.equals(sLocAbleRtn)) {
				//TO위치를 찾지 못하면 앞에서 만든 작업예약 ID를 삭제한다.
				
				jrParam.setField("YD_WBOOK_ID" , ydWbookId);    //삭제할 작업예약ID
				jrParam.setField("MODIFIER"    , "CANCEL311");  //수정자에 CANCEL311 등록 --> com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getAutoStkMvListCBay 쿼리에서 CANCEL311 으로 취소된 작업예약이 있으면  대상에서 제외한다.
				
				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBookMtl 
				--작업예약재료 삭제 
				UPDATE TB_YM_WRKBOOKMTL
				   SET MODIFIER    = :V_MODIFIER
				      ,MOD_DDTT    = SYSDATE
				      ,DEL_YN      = 'Y'
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND DEL_YN      = 'N' */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBookMtl", logId, methodNm, "작업예약재료 삭제");				

				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBook
				--작업예약 삭제
				UPDATE TB_YM_WRKBOOK
				   SET MODIFIER    = :V_MODIFIER
				      ,MOD_DDTT    = SYSDATE
				      ,DEL_YN      = 'Y'
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND DEL_YN      = 'N' */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBook", logId, methodNm, "작업예약 삭제");				
				
				return jrRtn;
			}
			
			/***********************************************************************
			 * 4. 스케줄 자동기동 여부가 N이면 스케줄 기동하지 않음 
			 ***********************************************************************/
			/*
			SELECT *
			  FROM TB_YM_SCHEDULERULE
			 WHERE YD_SCH_CD = :V_YD_SCH_CD
			 */
			jrParam.setField("YD_SCH_CD", sYD_SCH_CD); //야드스케쥴코드
			JDTORecordSet jsSchInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchCdInfo", logId, methodNm, "TB_YM_SCHEDULERULE 조회");
			
			if(jsSchInfo.size() > 0 ) {
				
				String sYD_SCH_AUTO_ST_YN = jsSchInfo.getRecord(0).getFieldString("YD_SCH_AUTO_ST_YN");
			
				if ("Y".equals(sYD_SCH_AUTO_ST_YN)) {
					
					/**********************************************************
					* 4. 크레인스케줄 전문 호출
					**********************************************************/
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
		
					jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
					jrYdMsg.setField("YD_SCH_CD"    , sYD_SCH_CD  ); //야드스케쥴코드
					jrYdMsg.setField("YD_SCH_ST_GP" , "O"         ); //야드스케쥴기동구분
					jrYdMsg.setField("YD_SCH_REQ_GP", "L"         ); //야드스케쥴요청구분(인출)
					jrYdMsg.setField("MODIFIER"     , "YMYMJ311"  ); //수정자
		
					jrRtn = commUtils.addSndData(ymComm.getCrnSchMsg(jrYdMsg));		
				} else {
					commUtils.printLog(logId, "자동기동 안됨" + sSTOCK_ID + "    자동기동 여부 : " +sYD_SCH_AUTO_ST_YN, "SL");
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
	
	/**	
	 *      [A] 오퍼레이션명 : A동 2통로 상차대기재 자동이적(YMYMJ312)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ312(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "A동 2통로 상차대기재 자동이적[BCoilL3RcvSeEJB.rcvYMYMJ312] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
		JDTORecordSet rsResult 	= null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅
			
			String szTRANS_ORD_DATE	 = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DATE")); //운송지시일자
			String szTRANS_ORD_SEQNO = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송지시순번
			
			String sSTOCK_ID;
			String sYD_SCH_CD = "3AYD31MM";
			String sSTACK_COL_GP;
			String sSTACK_BED_GP;
			String sSTACK_LAYER_GP;
			String sSTACK_LAYER_STAT;

			jrParam.setField("TRANS_ORD_DATE" 	, szTRANS_ORD_DATE);
			jrParam.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getListA3toA4Dm", logId, methodNm, "A동 2통로 출하 자동이적 대상 리스트 조회");

			if (rsResult.size() > 0) {
				
				//if("DMYDR060".equals(modifier) || "DMYDR070".equals(modifier)) {

				JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
				jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ303"); 
				jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
				jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
				jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
				
				int pcnt = 0;
				
				for(int ii = 0 ; ii<rsResult.size(); ii++) {
					
					sSTOCK_ID 			= rsResult.getRecord(ii).getFieldString("STOCK_ID");
					sSTACK_COL_GP		= rsResult.getRecord(ii).getFieldString("STACK_COL_GP");
					sSTACK_BED_GP		= rsResult.getRecord(ii).getFieldString("STACK_BED_GP");
					sSTACK_LAYER_GP 	= rsResult.getRecord(ii).getFieldString("STACK_LAYER_GP");
					sSTACK_LAYER_STAT	= rsResult.getRecord(ii).getFieldString("STACK_LAYER_STAT");
					
					if("C".equals(sSTACK_LAYER_STAT)) {
					
						/****************************************************
						 * 2. 작업예약 등록
						 ****************************************************/
						jrParam.setField("STOCK_ID"       , sSTOCK_ID); //재료번호
						jrParam.setField("YD_SCH_CD"      , sYD_SCH_CD); 
						jrParam.setField("STACK_COL_GP"   , sSTACK_COL_GP);
						jrParam.setField("STACK_BED_GP"   , sSTACK_BED_GP);
						jrParam.setField("STACK_LAYER_GP" , sSTACK_LAYER_GP);
						jrParam.setField("MODIFIER"       , modifier);
						jrParam.setField("YD_EQP_ID"      , "3ACRA3"); // 작업대상 크레인
						
						String ydWbookId = ymComm.procWkBookInsert(jrParam);
						
						if (ydWbookId.equals(YmConstant.RETN_CD_FAILURE)) {
							commUtils.printLog(logId, "작업예약ID 생성 실패" + sSTOCK_ID, "SL");
							return jrRtn;
						} 	
						
						/***********************************************************************
						 * 스케줄 자동기동 여부가 N이면 스케줄 기동하지 않음 
						 ***********************************************************************/
						/*
						SELECT *
						  FROM TB_YM_SCHEDULERULE
						 WHERE YD_SCH_CD = :V_YD_SCH_CD
						 */
						jrParam.setField("YD_SCH_CD", sYD_SCH_CD); //야드스케쥴코드
						JDTORecordSet jsSchInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchCdInfo", logId, methodNm, "TB_YM_SCHEDULERULE 조회");
						
						if(jsSchInfo.size() > 0 ) {
							
							String sYD_SCH_AUTO_ST_YN = jsSchInfo.getRecord(0).getFieldString("YD_SCH_AUTO_ST_YN");
						
							if ("Y".equals(sYD_SCH_AUTO_ST_YN)) {
								
								/**********************************************************
								* 4. 크레인스케줄 전문 호출
								**********************************************************/
								//JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
								//jrYdMsg.setResultCode(logId);	//Log ID
								//jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					
								//jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
								//jrYdMsg.setField("YD_SCH_CD"    , sYD_SCH_CD  ); //야드스케쥴코드
								//jrYdMsg.setField("YD_SCH_ST_GP" , "O"         ); //야드스케쥴기동구분
								//jrYdMsg.setField("YD_SCH_REQ_GP", "L"         ); //야드스케쥴요청구분(인출)
								//jrYdMsg.setField("MODIFIER"     , modifier  ); //수정자
					
								//jrRtn = commUtils.addSndData(jrRtn, ymComm.getCrnSchMsg(jrYdMsg));
								
								jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, ydWbookId); //야드작업예약ID
								jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(pcnt));
								
							} else {
								commUtils.printLog(logId, "자동기동 안됨" + sSTOCK_ID + "    자동기동 여부 : " +sYD_SCH_AUTO_ST_YN, "SL");
							}
						}			
					}
					
					if(pcnt > 0) {
						jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
					}
					
				} //for
				
			}
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/**	
	 *      [A] 오퍼레이션명 : 1열연 제품창 재고관리 알림 카톡 기능(YMYMJ313)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvYMYMJ313(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "1열연 제품창 재고관리 알림 카톡 기능[BCoilL3RcvSeEJB.rcvYMYMJ313] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
		JDTORecordSet rsResult 	= null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			// 1열연 제품창 재고관리 알림 카톡 기능 적용여부 확인
			String sAPPLY060 = ymComm.BCoilApplyYn("APP060","3","KAKAO_YN");
			commUtils.printLog(logId,  ">>> 1열연 제품창 재고관리 알림 카톡 기능 적용여부 (Y:적용, N:비적용) :" + sAPPLY060, "SL");
			if (sAPPLY060.equals("N")) {						
				commUtils.printLog(logId, methodNm, "S-");
				return jrRtn;
			}
			
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅
			
			
			String szGUBUN	 = commUtils.trim(rcvMsg.getFieldString("GUBUN")); //구분
			
			//구분 값이 "CHK_TEST" 기능이 정상 작동하는지 Check 하기 위해서 기준관리>카톡 알람 수신 전화번호[APP062] 에서  "Test전송" 누르면 선택된 전화번호에 전송된다.
			//모든 제품장이 적정재고 이하여됴 메세지 전송 된다.
			
			if("TEST".equals(szGUBUN)) {
			
				MessageSenderTalk    sender = new MessageSenderTalk();
				
				JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
				
				recPara1.setField("PHONE_NUM", commUtils.trim(rcvMsg.getFieldString("PHONE_NUM")));
				recPara1.setField("TMPL_CD", "CM1");
				recPara1.setField("SND_MSG", "[현대제철 공지사항]\n" + commUtils.trim(rcvMsg.getFieldString("SND_MSG")) );
				recPara1.setField("SUBJECT", "제품창 재고관리 알림");
				recPara1.setField("SMS_SND_NUM", "0416801616");
				recPara1.setField("RECV_ID","1521612");
				recPara1.setField("GROUP_ID","KaKao");
				recPara1.setField("PROGRAM_ID","udttalk");
				sender.sendTalk(recPara1);
				
			} else if("RUN".equals(szGUBUN) || "CHK_TEST".equals(szGUBUN)) {
				
				String SND_MSG = "[1열연 제품장 재고관리 알림]\n\n";
				int    iLMT_RULE = 0;
				int    iCURR_INV_RATE = 0;
				int    iNoMsgCnt = 0;
				
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGdsYdInvMgtSPM1_A", logId, methodNm, "A동 1SPM 제품장 재고관리 조회 -- kakao 알람용");
				
				if(rsResult.size()>0) {
					iLMT_RULE = rsResult.getRecord(0).getFieldInt("LMT_RULE"); //카톡 알람 메세지 보내는 기준 (현재고율이 65% 이상 되면 알람 전송) 
					iCURR_INV_RATE = rsResult.getRecord(0).getFieldInt("CURR_INV_RATE"); //현재고율 (적정재고율 대비 00%)
					
					if(iCURR_INV_RATE>=100) {
						SND_MSG += "■ A동 1SPM 적정 재고 초과\n";
					} else if(iCURR_INV_RATE>=iLMT_RULE) {
						SND_MSG += "■ A동 1SPM 적정 재고 초과\n";
						//SND_MSG += "■ A동 1SPM 적정 재고 초과전(최대재고의 "+iCURR_INV_RATE+"%)\n";
					} else {
						iNoMsgCnt++;
					}
					
				} else {
					SND_MSG += "■ A동 1SPM 제품장 조회 실패!!\n";
				}
				
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGdsYdInvMgtSPM1_B", logId, methodNm, "B동 1SPM 제품장 재고관리 조회 -- kakao 알람용");
				
				if(rsResult.size()>0) {
					iLMT_RULE = rsResult.getRecord(0).getFieldInt("LMT_RULE"); //카톡 알람 메세지 보내는 기준 (현재고율이 65% 이상 되면 알람 전송) 
					iCURR_INV_RATE = rsResult.getRecord(0).getFieldInt("CURR_INV_RATE"); //현재고율 (적정재고율 대비 00%)
					
					if(iCURR_INV_RATE>=100) {
						SND_MSG += "■ B동 1SPM 적정 재고 초과\n";
					} else if(iCURR_INV_RATE>=iLMT_RULE) {
						SND_MSG += "■ B동 1SPM 적정 재고 초과\n";
						//SND_MSG += "■ B동 1SPM 적정 재고 초과전(최대재고의 "+iCURR_INV_RATE+"%)\n";
					} else {
						iNoMsgCnt++;
					}
					
				} else {
					SND_MSG += "■ B동 1SPM 제품장 조회 실패!!\n";
				}
				
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGdsYdInvMgtAIRCL_B", logId, methodNm, "B동 공냉장 재고관리 조회 -- kakao 알람용");
				
				if(rsResult.size()>0) {
					iLMT_RULE = rsResult.getRecord(0).getFieldInt("LMT_RULE"); //카톡 알람 메세지 보내는 기준 (현재고율이 65% 이상 되면 알람 전송) 
					iCURR_INV_RATE = rsResult.getRecord(0).getFieldInt("CURR_INV_RATE"); //현재고율 (적정재고율 대비 00%)
					
					if(iCURR_INV_RATE>=100) {
						SND_MSG += "■ B동 공냉장 적정 재고 초과\n";
					} else if(iCURR_INV_RATE>=iLMT_RULE) {
						SND_MSG += "■ B동 공냉장 적정 재고 초과\n";
						//SND_MSG += "■ B동 공냉장 적정 재고 초과전(최대재고의 "+iCURR_INV_RATE+"%)\n";
					} else {
						iNoMsgCnt++;
					}
					
				} else {
					SND_MSG += "■ B동 공냉장 제품장 조회 실패!!\n";
				}
				
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGdsYdInvMgtHFL_B", logId, methodNm, "B동 HFL 제품장 재고관리 조회 -- kakao 알람용");
				
				if(rsResult.size()>0) {
					iLMT_RULE = rsResult.getRecord(0).getFieldInt("LMT_RULE"); //카톡 알람 메세지 보내는 기준 (현재고율이 65% 이상 되면 알람 전송) 
					iCURR_INV_RATE = rsResult.getRecord(0).getFieldInt("CURR_INV_RATE"); //현재고율 (적정재고율 대비 00%)
					
					if(iCURR_INV_RATE>=100) {
						SND_MSG += "■ B동 HFL 적정 재고 초과\n";
					} else if(iCURR_INV_RATE>=iLMT_RULE) {
						SND_MSG += "■ B동 HFL 적정 재고 초과\n";
						//SND_MSG += "■ B동 HFL 적정 재고 초과전(최대재고의 "+iCURR_INV_RATE+"%)\n";
					} else {
						iNoMsgCnt++;
					}
					
				} else {
					SND_MSG += "■ B동 HFL 제품장 조회 실패!!\n";
				}
				
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGdsYdInvMgtHFL_C", logId, methodNm, "C동 HFL 제품장 재고관리 조회 -- kakao 알람용");
				
				if(rsResult.size()>0) {
					iLMT_RULE = rsResult.getRecord(0).getFieldInt("LMT_RULE"); //카톡 알람 메세지 보내는 기준 (현재고율이 65% 이상 되면 알람 전송) 
					iCURR_INV_RATE = rsResult.getRecord(0).getFieldInt("CURR_INV_RATE"); //현재고율 (적정재고율 대비 00%)
					
					if(iCURR_INV_RATE>=100) {
						SND_MSG += "■ C동 HFL 적정 재고 초과\n";
					} else if(iCURR_INV_RATE>=iLMT_RULE) {
						SND_MSG += "■ C동 HFL 적정 재고 초과\n";
						//SND_MSG += "■ C동 HFL 적정 재고 초과전(최대재고의 "+iCURR_INV_RATE+"%)\n";
					} else {
						iNoMsgCnt++;
					}
					
				} else {
					SND_MSG += "■ C동 HFL 제품장 조회 실패!!\n";
				}
				
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGdsYdInvMgtHFL_D", logId, methodNm, "D동 HFL 제품장 재고관리 조회 -- kakao 알람용");
				
				if(rsResult.size()>0) {
					iLMT_RULE = rsResult.getRecord(0).getFieldInt("LMT_RULE"); //카톡 알람 메세지 보내는 기준 (현재고율이 65% 이상 되면 알람 전송) 
					iCURR_INV_RATE = rsResult.getRecord(0).getFieldInt("CURR_INV_RATE"); //현재고율 (적정재고율 대비 00%)
					
					if(iCURR_INV_RATE>=100) {
						SND_MSG += "■ D동 HFL 적정 재고 초과\n";
					} else if(iCURR_INV_RATE>=iLMT_RULE) {
						SND_MSG += "■ D동 HFL 적정 재고 초과\n";
						//SND_MSG += "■ D동 HFL 적정 재고 초과전(최대재고의 "+iCURR_INV_RATE+"%)\n";
					} else {
						iNoMsgCnt++;
					}
					
				} else {
					SND_MSG += "■ D동 HFL 제품장 조회 실패!!\n";
				}
				
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGdsYdInvMgtHFL_E", logId, methodNm, "E동 HFL 제품장 재고관리 조회 -- kakao 알람용");
				
				if(rsResult.size()>0) {
					iLMT_RULE = rsResult.getRecord(0).getFieldInt("LMT_RULE"); //카톡 알람 메세지 보내는 기준 (현재고율이 65% 이상 되면 알람 전송) 
					iCURR_INV_RATE = rsResult.getRecord(0).getFieldInt("CURR_INV_RATE"); //현재고율 (적정재고율 대비 00%)
					
					if(iCURR_INV_RATE>=100) {
						SND_MSG += "■ E동 HFL 적정 재고 초과\n";
					} else if(iCURR_INV_RATE>=iLMT_RULE) {
						SND_MSG += "■ E동 HFL 적정 재고 초과\n";
						//SND_MSG += "■ E동 HFL 적정 재고 초과전(최대재고의 "+iCURR_INV_RATE+"%)\n";
					} else {
						iNoMsgCnt++;
					}
					
				} else {
					SND_MSG += "■ E동 HFL 제품장 조회 실패!!\n";
				}
				
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGdsYdInvMgtSPM2_E", logId, methodNm, "E동 2SPM 제품장 재고관리 조회 -- kakao 알람용");
				
				if(rsResult.size()>0) {
					iLMT_RULE = rsResult.getRecord(0).getFieldInt("LMT_RULE"); //카톡 알람 메세지 보내는 기준 (현재고율이 65% 이상 되면 알람 전송) 
					iCURR_INV_RATE = rsResult.getRecord(0).getFieldInt("CURR_INV_RATE"); //현재고율 (적정재고율 대비 00%)

					if(iCURR_INV_RATE>=100) {
						SND_MSG += "■ E동 2SPM 적정 재고 초과\n";
					} else if(iCURR_INV_RATE>=iLMT_RULE) {
						SND_MSG += "■ E동 2SPM 적정 재고 초과\n";
						//SND_MSG += "■ E동 2SPM 적정 재고 초과전(최대재고의 "+iCURR_INV_RATE+"%)\n";
					} else {
						iNoMsgCnt++;
					}
					
				} else {
					SND_MSG += "■ E동 1SPM 제품장 조회 실패!!\n";
				}
				
				String sSEND_FLAG = "Y"; //카톡 알람 전송 여부 Y:전송함, N:전송안함
				
				if(iNoMsgCnt == 8) {
					//SND_MSG += "○ 모든 제품장 재고 기준치 이하 입니다.\n";
					SND_MSG += "○ 모든 제품장 적정 재고 이하 입니다.\n";
					
					commUtils.printLog(logId, "++++++  ○ 모든 제품장 적정 재고 이하 입니다. 카톡 알람 전송 안합니다. ++++++ " , "S+");
					sSEND_FLAG = "N";
					
				} else if(iNoMsgCnt > 0) {
					//SND_MSG += "○ 그 외 제품장 재고 기준치 이하 입니다.\n";
					SND_MSG += "○ 그 외 제품장은 적정 재고 이하 입니다.\n";
				} 
				
				//-------------------------------------------------------------------------------------------------------------
				if("CHK_TEST".equals(szGUBUN)) {
					
					//구분 값이 "CHK_TEST" 기능이 정상 작동하는지 Check 하기 위해서 기준관리>카톡 알람 수신 전화번호[APP062] 에서  "Test전송" 누르면 선택된 전화번호에 전송된다.
					//모든 제품장이 적정재고 이하여됴 메세지 전송 된다.

					MessageSenderTalk    sender = new MessageSenderTalk();
					
					JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
					
					String sPHONE_NUM = commUtils.trim(rcvMsg.getFieldString("PHONE_NUM"));
					
					recPara1.setField("PHONE_NUM", sPHONE_NUM);
					
					recPara1.setField("TMPL_CD", "CM1");
					recPara1.setField("SND_MSG", "[현대제철 공지사항]\n" + SND_MSG + " [Test]");
					recPara1.setField("SUBJECT", "제품창 재고관리 알림");
					recPara1.setField("SMS_SND_NUM", "0416801616");
					recPara1.setField("RECV_ID","1521612");
					recPara1.setField("GROUP_ID","KaKao");
					recPara1.setField("PROGRAM_ID","udttalk");
					sender.sendTalk(recPara1);
					
					
				} else if("Y".equals(sSEND_FLAG)) {
					
					MessageSenderTalk    sender = new MessageSenderTalk();
					
					JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
					
					String sPHONE_NUM = "";
					
					jrParam.setField("REPR_CD_GP"			, "APP062" );
					jrParam.setField("CD_GP"				, "PHONE_NUM" );
					
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "전송할 전화번호 조회");
					
					for(int ii = 0; ii < rsResult.size(); ii++) {
					
						sPHONE_NUM = commUtils.trim(rsResult.getRecord(ii).getFieldString("DTL_ITM1"));
						
						recPara1.setField("PHONE_NUM", sPHONE_NUM);
						
						recPara1.setField("TMPL_CD", "CM1");
						recPara1.setField("SND_MSG", "[현대제철 공지사항]\n" + SND_MSG);
						recPara1.setField("SUBJECT", "제품창 재고관리 알림");
						recPara1.setField("SMS_SND_NUM", "0416801616");
						recPara1.setField("RECV_ID","1521612");
						recPara1.setField("GROUP_ID","KaKao");
						recPara1.setField("PROGRAM_ID","udttalk");
						sender.sendTalk(recPara1);
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
	
