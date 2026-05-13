/**
 * @(#)ACoilRcvL3SeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      박판열연 COIL 야드 L3 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 *
 */
package com.inisteel.cim.yf.acoilBak.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yf.acoilBak.YfCommUtils;
import com.inisteel.cim.yf.acoilBak.YfConstant;
import com.inisteel.cim.yf.acoilBak.YfQueryIFOld;
import com.inisteel.cim.yf.acoilBak.YfQueryIFOld2;
import com.inisteel.cim.yf.acoilBak.dao.YfCommDAO;
import com.inisteel.cim.yf.acoilBak.session.YfComm;

/**
 *      [A] 클래스명 : 박판열연 COIL 야드 L3수신 처리
 *
 * @ejb.bean name="ACoilRcvL3BakSeEJB" jndi-name="ACoilRcvL3BakSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class ACoilRcvL3SeEJBSBean extends BaseSessionBean implements YfQueryIFOld, YfQueryIFOld2
{
	private static final long serialVersionUID = 1L;
	private String classNm = getClass().getName();
	private Logger logger = new Logger("yf");
	private YfCommUtils commUtils = new YfCommUtils();
	private YfCommDAO commDao = new YfCommDAO();
	private YfComm yfComm = new YfComm();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException
	{

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
	public JDTORecord rcvPOYMJ001(JDTORecord rcvMsg)
	{
		boolean		isVal		= false;
		String		methodNm	= "코일정보 수신[ACoilRcvL3SeEJB.rcvPOYMJ001] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    String		szMsg		= "";
	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일정보 수신 시작========", "SL");

			//수신 항목 값
			String sProcessId	= StringHelper.evl(rcvMsg.getFieldString("ProcessID"),"").trim();	// 01 압연실적, 02 정정실적, 05 보류재, 07 HFL처리, 08 모COIL종료, 09 자COIL, 10 반납시점, 91 조업시스템에러, DC DC OFF, J2 SPM재작업, Q2 EQL재작업
			String sCoilNo		= StringHelper.evl(rcvMsg.getFieldString("CoilNo"),"").trim();		// 'S' Scrap  'H' A열연  'K' B열연
			String sYardID		= StringHelper.evl(rcvMsg.getFieldString("YardID"),"").trim();		// 야드 구분
			String sProcessCode	= StringHelper.evl(rcvMsg.getFieldString("ProcessCode"),"").trim();
			String sTcDate		= StringHelper.evl(rcvMsg.getFieldString("TcDate"),"").trim();
			String sTcTime		= StringHelper.evl(rcvMsg.getFieldString("TcTime"),"").trim();

			logger.println(LogLevel.DEBUG,this," TcCode 		=" + tcCode);
			logger.println(LogLevel.DEBUG,this," ProcessID 		=" + sProcessId);
			logger.println(LogLevel.DEBUG,this," CoilNo 		=" + sCoilNo);
			logger.println(LogLevel.DEBUG,this," YardID 		=" + sYardID);
			logger.println(LogLevel.DEBUG,this," ProcessCode 	=" + sProcessCode);
			logger.println(LogLevel.DEBUG,this," TcDate 		=" + sTcDate);
			logger.println(LogLevel.DEBUG,this," TcTime 		=" + sTcTime);

			jrParam.setField("TC_CODE",			msgId);
			jrParam.setField("PROCESS_ID",		sProcessId);
			jrParam.setField("COIL_NO",			sCoilNo);
			jrParam.setField("YARD_ID",			sYardID);
			jrParam.setField("PROCESS_CODE",	sProcessCode);
			jrParam.setField("TC_DATE",			sTcDate);
			jrParam.setField("TC_TIME",			sTcTime);

			if("01".equals(sProcessId))
			{
				//01. 평량실적
				jrRtn = commUtils.addSndData(jrRtn, yfComm.setInnerIFCoilInfo_01( jrParam ));	//YFF1L009(압연실적정보) 생성
			}
			else if("02".equals(sProcessId))
			{
				//02. 정정실적 (Back-up포함)처리시점
				jrParam.setField("WORK_CHK"  , "SPM");
				isVal = yfComm.setInnerIFCoilInfo_02( jrParam );
			}
			else if("05".equals(sProcessId))
			{
				//05. 보류재 처리 시점
				jrRtn = commUtils.addSndData(jrRtn, yfComm.setInnerIFCoilInfo_03( jrParam ));	//YDDMR001(일관제철 코일입고작업실적) 생성
			}
			else if("07".equals(sProcessId))
			{
				//07. HFL처리시점
				jrParam.setField("WORK_CHK"  , "HFL");
				isVal = yfComm.setInnerIFCoilInfo_02( jrParam );
			}
			else if("08".equals(sProcessId))
			{
				//08. 모 Coil 종료
				jrRtn = commUtils.addSndData(jrRtn, yfComm.setInnerIFCoilInfo_05( jrParam ));	//산적위치수정호출뒤 전문 + YDDMR003(임가공입고작업실적)
			}
			else if("09".equals(sProcessId))
			{
				//09. 자 Coil
				jrRtn = commUtils.addSndData(jrRtn, yfComm.setInnerIFCoilInfo_06( jrParam ));	//YFF1L002(저장품제원) 생성
			}
			else if("10".equals(sProcessId))
			{
				//10. 반납 시점
				isVal = yfComm.setInnerIFCoilInfo_04( jrParam );
			}
			//박판은 요구차 공정변경 없음 주석...테스트 후 이상 없으면 삭제 예정
			/*
			else if("11".equals(sProcessId))
			{
				//11. 요구차 공정 변경.
				//Coil 다음 공정 정보 변경시 처리.
				isVal = yfComm.setInnerIFCoilInfo_11( jrParam );
			}
			*/
			else if("91".equals(sProcessId))
			{
				//91. 조업시스템 에러.
				//Coil 공통 테이블에 있는 정보만 처리한다.
				jrRtn = commUtils.addSndData(jrRtn, yfComm.setInnerIFCoilInfo_01( jrParam ));	//YFF1L009(압연실적정보) 생성
			}
			else if("DC".equals(sProcessId))
			{
				//DC. 권취실적
				//Coil 공통 테이블에 있는 정보만 처리한다.
				jrRtn = commUtils.addSndData(jrRtn, yfComm.setInnerIFCoilInfo_01( jrParam ));	//YFF1L009(압연실적정보) 생성
			}
			//박판에 HR_PLATE가 없음 주석...테스트 후 이상 없으면 삭제 예정
			/*
			else if("HP".equals(sProcessId))
			{
				//HP. HR-Plate 생성
				jrRtn = commUtils.addSndData(jrRtn, yfComm.setInnerIFCoilInfo_HP( jrParam ));
			}
			*/
			else if("J2".equals(sProcessId))
			{
				//J2. SPM 재작업
				jrRtn = commUtils.addSndData(jrRtn, yfComm.setInnerIFCoilInfo_07( jrParam ));	//YMPOJ161(코일보급 및 보급취소), YDQMJ002(열연정정입측보급실적)
			}
			else if("Q2".equals(sProcessId))
			{
				//Q2. EQL 재작업
				jrRtn = commUtils.addSndData(jrRtn, yfComm.setInnerIFCoilInfoEQL_07( jrParam ));	////YMPOJ161(코일보급 및 보급취소), YDQMJ002(열연정정입측보급실적)
			}

			commUtils.printLog(logId, "=============코일정보 수신 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
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
	public JDTORecord rcvPOYMJ002(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "코일 결번 실적 수신[ACoilRcvL3SeEJB.rcvPOYMJ002] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일 결번 실적 시작========", "SL");

			//수신 항목 값
			String sYD_GP    = commUtils.trim(rcvMsg.getFieldString("yardID"));
			String sCOIL_NO  = commUtils.trim(rcvMsg.getFieldString("CoilNo"));

			/**
			 * 1. 수신 항목 값 Check
			 */
			if ("".equals(sCOIL_NO))
			{
				throw new Exception("코일이 없습니다..");
			}

			/**
			 * 2. 조업에서 코일결번 실적 발생후 종합판정한뒤 야드에게 실적 처리완료 정보를 송신
			 */
			jrParam.setField("STL_NO",		sCOIL_NO);
			jrParam.setField("MODIFIER",	modifier);
			jrParam.setField("TC_CD",		YfConstant.POYMJ002);

			JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);

			String sCURR_PROG_CD    = jrRtnProg.getFieldString("CURR_PROG_CD");
			String sSTOCK_MOVE_TERM = jrRtnProg.getFieldString("STOCK_MOVE_TERM");

			if (!"".equals(sSTOCK_MOVE_TERM))
			{
				jrParam.setField("STL_NO",			sCOIL_NO);
	    		jrParam.setField("STOCK_MOVE_TERM",	sSTOCK_MOVE_TERM);
	    		commDao.update(jrParam, updateStockTransInfo, logId, methodNm, "TB_YF_STOCK 수정");
	    	}

			commUtils.printLog(logId, "=============코일 결번 실적 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * [A] 오퍼레이션명 : 코일 SPM/HFL/EQL 작업 요구 정보를 수신(POYMJ004)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvPOYMJ004(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "코일 SPM/HFL/EQL 작업 요구 정보 정보를 수신[ACoilRcvL3SeEJB.rcvPOYMJ004] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
	    boolean		blRtn		= false;

	    JDTORecord	jrRst		= JDTORecordFactory.getInstance().create();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일 SPM/HFL/EQL 작업 요구 정보를 수신 시작========", "SL");

			//수신 항목 값
			String yardId 		= commUtils.trim(rcvMsg.getFieldString("YardId"));			// 야드 구분
			String WorkId 		= commUtils.trim(rcvMsg.getFieldString("WorkId"));			// 공정코드 S:SPM,H:HFL,D:결속대   // 2SPM  처리
			String procId 		= commUtils.trim(rcvMsg.getFieldString("ProcessId"));		// 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
			String CoilNo 		= commUtils.trim(rcvMsg.getFieldString("CoilNo"));			// 'S' Scrap  'H' A열연  'K' B열연
			String position 	= commUtils.trim(rcvMsg.getFieldString("Position"));		// 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
			String takeOutProc 	= commUtils.trim(rcvMsg.getFieldString("TakeOutProcess"));	// Take-Out시  1:결번,2:임시보류처리(잠시 내려놨다가 Take-In할 Coil)

			commUtils.printParam("", rcvMsg);

			jrParam.setField("YARDID",			yardId);
			jrParam.setField("WORKID",			WorkId);
			jrParam.setField("PROCESSID",		procId);
			jrParam.setField("COILNO",			CoilNo);
			jrParam.setField("POSITION",		position);
			jrParam.setField("STL_NO",			CoilNo);
			jrParam.setField("TAKEOUTPROCESS",	takeOutProc);

			//HFL 결속대 프로세스##################################################################
			//박판은 HFL 결속대가 없음 주석처리
//			if("D".equals(WorkId))
//			{
//				jrRtn = this.receiveHFLConStat(rcvMsg);
//
//				return jrRtn;
//			}
			//###################################################################################

			if (YfConstant.PROCESS_ID_1.equals(procId))
			{
				/**
				 * 보급처리
				 */
				commUtils.printLog(logId, methodNm + "SPM/HFL/EQL 보급: " + CoilNo , "SL");

				if("S".equals(WorkId) || "H".equals(WorkId) || "E".equals(WorkId))
				{
					//SPM, HFL, EQL
					/****  SPM, HFL, EQL : layer 위치 확인  *****/
					blRtn = SpmHflProcessCheck(jrParam);

					if(blRtn == true)
					{
						/****  스케줄/작업예약 생성/ 스케쥴 호출  *****/
						jrRtn = this.callLineInOut(logId, methodNm, yardId, WorkId, procId, CoilNo, "", modifier);
					}
				}
			}
			else if(YfConstant.PROCESS_ID_2.equals(procId))
			{
				/**
				 * 보급 취소
				 */
				commUtils.printLog(logId, methodNm + "보급취소: " + CoilNo , "SL");
				JDTORecordSet jsCrnSch = commDao.select(jrParam, getYdWrkbookDelChk2, logId, methodNm, "크레인스케줄재료 조회");

				if(jsCrnSch != null && jsCrnSch.size() > 0)
				{
					String ydWbookId  = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID"));			//작업예약ID
					String ydCrnSchId = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"));			//크레인 작업지시
					String ydWrkProgStat = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));	//크레인 작업지시

					commUtils.printLog(logId, "작업취소 [ YD_CRN_SCH_ID : " + ydCrnSchId + " / YD_WBOOK_ID : " + ydWbookId + " / YD_WRK_PROG_STAT : " + ydWrkProgStat + " ]", "SL");

					jrParam.setField("YD_WBOOK_ID",		ydWbookId);
					jrParam.setField("YD_CRN_SCH_ID",	ydCrnSchId);

					EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);

					// 2019. 12. 12 로직수정
					// 크레인의 작업진행상태가  "W"인 경우에만 작업예약을 삭제 할 수 있도록 조치함
					if(YfConstant.YD_WRK_PROG_STAT_W.equals(ydWrkProgStat))		//크레인스케쥴 상태 명령선택대기(W)
					{
						if(!"".equals(ydCrnSchId))
						{
							jrParam.setField("WRK_CNCL_YN", "Y"); //작업취소 여부
							jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
							jrRtn = commUtils.addSndData(jrRtn, jrRst);
						}

						if(!"".equals(ydWbookId))
						{
							// 크레인 스케쥴 상태를 확인하여 실제 취소할지 결정하자
							// W 상태만 취소가 가능함
							jrRst = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
							jrRtn = commUtils.addSndData(jrRtn, jrRst);
						}
					}
					else
					{
						throw new Exception( "야드작업진행상태(YD_WRK_PROG_STAT): W(명령선택대기), S(명령선택지시) 만 보급취소가 가능합니다. ::현재(YD_WRK_PROG_STAT::)"+ydWrkProgStat  );
					}
				}
			}
			else if (YfConstant.PROCESS_ID_3.equals(procId))	//추출
			{
				/**
				 * 추출
				 */
				//stock 에 없는 경우 생성처리
				this.procCoilStock(logId, methodNm, yardId, WorkId, procId, CoilNo, modifier);

				if(!WorkId.equals("E"))
				{
					//SPM, HFL
					/***********************************
					 * 보관매출 체크
					 * 보관매출인경우 추출요구 시 자동으로 실적 발생 후 추출 처리
					 **********************************/
					JDTORecordSet jsCrnSch = commDao.select(jrParam, getCoilKeepstockInfo, logId, methodNm, "코일작업지시read");

					if(jsCrnSch.size()> 0)
					{
						//정정실적 처리
						String Workchk = "";

						if("S".equals(WorkId))
						{
							Workchk	= "SPM";
						}
						else if("H".equals(WorkId))
						{
							Workchk	= "HFL";
						}

						commUtils.printLog(logId, methodNm + ">>★★★★★★★★★보관매출 자동정정실적처리>>>" + CoilNo , "SL");

						jrParam.setResultCode(logId);	//Log ID
						jrParam.setResultMsg(methodNm);	//Log Method Name
						jrParam.setField("MODIFIER",	modifier);
						jrParam.setField("COIL_NO",		CoilNo);
						jrParam.setField("WORK_CHK",	Workchk);

						yfComm.setInnerIFCoilInfo_02(jrParam);
					}
				}

				/****  SPM, HFL, EQL : layer 에 없는 경우 생성 처리함  *****/
				blRtn = SpmHflProcessCheck(jrParam);

				if (blRtn == true)
				{
					/****  스케줄/작업예약 생성/ 스케쥴 호출  *****/
					jrRtn = this.callLineInOut(logId, methodNm, yardId, WorkId, procId, CoilNo, "", modifier);
				}
				//추출종료
			}
			else if (YfConstant.PROCESS_ID_4.equals(procId))
			{
				/**
				 * Take-Out
				 */
				//stock 에 없는 경우 생성처리
				this.procCoilStock(logId, methodNm, yardId, WorkId, procId, CoilNo, modifier);

				if("S".equals(WorkId) || "H".equals(WorkId) || "E".equals(WorkId))
				{
					//SPM, HFL, EQL
					jrRtn = this.callTakeOut(jrParam);
				}
			}
			else if (YfConstant.PROCESS_ID_5.equals(procId))
			{
				/**
				 * Take-in
				 */
				if("S".equals(WorkId) || "H".equals(WorkId) || "E".equals(WorkId))
				{
					//SPM, HFL, EQL
					/****  SPM, HFL : layer 위치 확인  *****/
					blRtn = SpmHflProcessCheck(jrParam);

					if (blRtn == true)
					{
						/****  스케줄/작업예약 생성/ 스케쥴 호출  *****/
						jrRtn = this.callLineInOut(logId, methodNm, yardId, WorkId, procId, CoilNo, position, modifier);
					}
				}
			}

			commUtils.printLog(logId, "=============코일 SPM/HFL/EQL 작업 요구 정보를 수신 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 오퍼레이션명 : HFL 결속대 작업...박판COIL은 결속대가 없어서 사용안함...추후 삭제 예정
	 *
	 *  조업 LEVEL3로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
   	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  TREAT_GP :  1 보급, 2 보급취소, 3 추출
	 * @return
	 * @throws
	 */
	public JDTORecord receiveHFLConStat(JDTORecord rcvMsg) throws JDTOException
	{
		String		methodNm	= "HFL 결속대 작업[ACoilRcvL3SeEJB.receiveHFLConStat] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try
		{
			logger.println(LogLevel.DEBUG,this, "Start-receiveHFLConStat()");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			//수신 항목 값
			String yardId 		= commUtils.trim(rcvMsg.getFieldString("YardId"));			// 야드 구분
			String WorkId 		= commUtils.trim(rcvMsg.getFieldString("WorkId"));			// 공정코드 S:SPM, H:HFL, D:결속대   // 2SPM  처리
			String procId 		= commUtils.trim(rcvMsg.getFieldString("ProcessId"));		// 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
			String CoilNo 		= commUtils.trim(rcvMsg.getFieldString("CoilNo"));			// 'S' Scrap, 'H' A열연, 'K' B열연

			// 공정에 따라 분기. ProcessID기준.
			if (YfConstant.PROCESS_ID_1.equals(procId))
			{
				// 보급
				logger.println(LogLevel.DEBUG,this,"receiveHFLConStat 보급 ["+CoilNo+"]" );

				// 요구된 코일에 대한 작업예약 및 스케줄 생성한다.
				jrRtn = this.callLineInOut(logId, methodNm, yardId, WorkId, procId, CoilNo, "", modifier);
			}
			else if (YfConstant.PROCESS_ID_3.equals(procId))
			{
				// 추출
				logger.println(LogLevel.DEBUG,this,"receiveHFLConStat 추출 ["+CoilNo+"]" );

				jrRtn = this.callLineInOut(logId, methodNm, yardId, WorkId, procId, CoilNo, "", modifier);
			}

			logger.println(LogLevel.DEBUG,this, "End-receiveHFLConStat");

			return jrRtn;
	    }
		catch(DAOException daoe)
		{
	        throw daoe;
	    }
		catch(Exception e)
		{
	        throw new EJBServiceException(e);
	    }
	}

	/**
	 * TB_YF_STKLYR 에 위치등록
	 * @param YardID : 야드구분
	 * @param WorkID : S SPM, H HFL, E EQL
	 * @param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * @param CoilNo
	 * @return
	 */
	private boolean SpmHflProcessCheck(JDTORecord rcvMsg)throws DAOException
	{
		String		methodNm	=  "코일 위치 정보 확인[ACoilRevL3SeEJB.SpmHflProcessCheck] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

		boolean		blRtn		= false;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일 위치 정보 확인 시작========", "SL");

			//수신 항목 값
			String YardId 	= commUtils.trim(rcvMsg.getFieldString("YARDID"));
			String WorkId 	= commUtils.trim(rcvMsg.getFieldString("WORKID"));		// 공정코드 S:SPM,H:HFL,D:결속대
			String procId 	= commUtils.trim(rcvMsg.getFieldString("PROCESSID"));	// 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
			String CoilNo 	= commUtils.trim(rcvMsg.getFieldString("COILNO"));		// 'S' Scrap  'H' A열연  'K' B열연

	    	if ("".equals(CoilNo))
	    	{
				commUtils.printLog(logId, methodNm + "Coil No = Space Error" , "SL");
				return false;
	    	}

	    	jrParam.setField("YARDID",		YardId);
			jrParam.setField("STL_NO",		CoilNo);
			jrParam.setField("PROCESSID",	procId);
			jrParam.setField("WORKID",		WorkId);

			if (YfConstant.PROCESS_ID_3.equals(procId))	//추출
			{
				/*********************
				 * 추출이거나  TAKE OUT 인 경우
				 *  - 입측 클리어 작업
				 *  - TB_YF_STKLYR 에 없는 경우 생성처리 한다.
				 ******************************************/
		   		commDao.update(jrParam, updStackLayer, logId, methodNm, "TB_YF_STKLYR에서 기존 위치 삭제");

				/*********************
				 * 추출인 경우
				 * TB_YF_STKLYR 에 없는 경우 생성처리 한다.
				 ******************************************/
				int updCnt = commDao.update(jrParam, updTrkStackColGplayer, logId, methodNm, "TB_YF_EQPTRACKING 조회해서 TB_YF_STKLYR 수정");

				if(updCnt == 0)
				{
					commUtils.printLog(logId, "적치단(TB_YF_STKLYR) Table Read Error : "+ CoilNo, "SL");
					return false;
				}
			}

			JDTORecordSet jsStackLayer = JDTORecordFactory.getInstance().createRecordSet("");
	    	jsStackLayer = commDao.select(jrParam, getselectStackColGp1, logId, methodNm, "TB_YF_STKLYR 정보");

			if (jsStackLayer.size() == 0)
			{
				commUtils.printLog(logId, "적치단(TB_YF_STKLYR)에 해당 재료가 없습니다. : "+ CoilNo, "SL");
				return false;
			}

			jsStackLayer.first();
			JDTORecord jrStackLayer = jsStackLayer.getRecord();
			String ydStackColGp	= commUtils.nvl(jrStackLayer.getFieldString("YD_STK_COL_GP"),	"");
			String ydBayGp		= commUtils.nvl(jrStackLayer.getFieldString("YD_STK_COL_GP2"),	"");
			String ydEquip 		= ydStackColGp.substring(2, 4);

			if (YfConstant.WORK_SPM_E.equals(WorkId))	//EQL 작업
    		{
    			if (YfConstant.PROCESS_ID_1.equals(procId))	// EQL 보급
    			{
    				if (YfConstant.BAY_GP_E.equals(ydBayGp) || YfConstant.BAY_GP_F.equals(ydBayGp))		//EQL 보급가능동 : E동, F동
    				{
	    				if (YfConstant.WORK_EQL_IN_QE.equals(ydEquip) || YfConstant.WORK_EQL_OUT_QD.equals(ydEquip))	//EQL설비 보급 또는 출측 위치
	    				{
	    					blRtn = false;	//보급이나 추출 위치에 있다는 것을 의미
	    			    }
	    				else
	    				{
	    			    	blRtn = true;	//보급 예정인 제품으로 적치단에 있음을 의미
	    			    }
    				}
    				else
    				{
    					blRtn = false;
    				}
    			}
    			else if (YfConstant.PROCESS_ID_3.equals(procId))	//EQL 추출(재작업포함) : 추출적치열(1GQD01) + 재작업동(1FQE01)
    			{
    				if (YfConstant.BAY_GP_F.equals(ydBayGp) || YfConstant.BAY_GP_G.equals(ydBayGp))		//EQL 추출가능동 : F동(재작업), G동(추출, 재작업)
    				{
    					if (YfConstant.WORK_EQL_IN_QE.equals(ydEquip) || YfConstant.WORK_EQL_OUT_QD.equals(ydEquip))	//EQL설비 재작업, 추출 위치
	    				{
	    					blRtn = true;	//보급이나 추출 위치에 있다는 것을 의미
	    			    }
	    				else
	    				{
	    			    	blRtn = false;	//보급 예정인 제품으로 적치단에 있음을 의미
	    				}
    				}
    				else
    				{
    					blRtn = false;
    				}
    			}
    			else if (YfConstant.PROCESS_ID_5.equals(procId))	//EQL Take-In
    			{
    				if (YfConstant.BAY_GP_E.equals(ydBayGp) || YfConstant.BAY_GP_F.equals(ydBayGp))		//Take-In 가능동 : E동, F동
    				{
	    				if (YfConstant.WORK_EQL_IN_QE.equals(ydEquip) || YfConstant.WORK_EQL_OUT_QD.equals(ydEquip))	//EQL설비 보급 또는 출측 위치
	    				{
	    					blRtn = false;	//보급이나 추출 위치에 있다는 것을 의미
	    			    }
	    				else
	    				{
	    			    	blRtn = true;	//보급 예정인 제품으로 적치단에 있음을 의미
	    				}
    				}
    				else
    				{
    					blRtn = false;
    				}
    			}
    		}
    		else if(YfConstant.WORK_SPM_S.equals(WorkId))	// SPM
    		{
    			if(YfConstant.PROCESS_ID_1.equals(procId))			// SPM 보급
    			{
    				if (YfConstant.BAY_GP_D.equals(ydBayGp) || YfConstant.BAY_GP_E.equals(ydBayGp))		//SPM 보급가능동 : D동, E동
    				{
	    				if (YfConstant.WORK_SPM_IN_KE.equals(ydEquip) || YfConstant.WORK_SPM_OUT_KD.equals(ydEquip))	//SPM설비 보급 또는 출측 위치
	    				{
	    					blRtn = false;	//보급이나 추출 위치에 있다는 것을 의미
	    			    }
	    				else
	    				{
	    			    	blRtn = true;	//보급 예정인 제품으로 적치단에 있음을 의미
	    			    }
    				}
    				else
    				{
    					blRtn = false;
    				}
    			}
    			else if (YfConstant.PROCESS_ID_3.equals(procId))	//SPM 추출
    			{
    				if (YfConstant.BAY_GP_E.equals(ydBayGp) || YfConstant.BAY_GP_F.equals(ydBayGp))		//SPM 추출가능동 : E동, F동 추출 및 재작업
    				{
	    				if (YfConstant.WORK_SPM_OUT_KD.equals(ydEquip))		//SPM설비 추출위치
	    				{
	    					blRtn = true;	//보급이나 추출 위치에 있다는 것을 의미
	    			    }
	    				else
	    				{
	    			    	blRtn = false;	//보급 예정인 제품으로 적치단에 있음을 의미
	    				}
    				}
    				else
    				{
    					blRtn = false;
    				}
    			}
    			else if (YfConstant.PROCESS_ID_5.equals(procId))	//SPM Take-In
    			{
    				if (YfConstant.BAY_GP_D.equals(ydBayGp) || YfConstant.BAY_GP_E.equals(ydBayGp))		//Take-In 가능동 : D동, E동
    				{
	    				if (YfConstant.WORK_SPM_IN_KE.equals(ydEquip) || YfConstant.WORK_SPM_OUT_KD.equals(ydEquip))	//SPM설비 보급 또는 출측 위치
	    				{
	    					blRtn = false;	//보급이나 추출 위치에 있다는 것을 의미
	    			    }
	    				else
	    				{
	    			    	blRtn = true;	//보급 예정인 제품으로 적치단에 있음을 의미
	    				}
    				}
    				else
    				{
    					blRtn = false;
    				}
    			}
    		}
    		else if (YfConstant.WORK_HFL_H.equals(WorkId))	//HFL
    		{
    			if (YfConstant.PROCESS_ID_1.equals(procId))		//HFL 보급
    			{
    				if (YfConstant.BAY_GP_B.equals(ydBayGp))	//HFL 보급가능동 : B동
    				{
	    				if (YfConstant.WORK_HFL_IN_FE.equals(ydEquip) || YfConstant.WORK_HFL_OUT_FD.equals(ydEquip))	//HFL설비 보급 또는 출측 위치
	    				{
	    					blRtn = false;	//보급이나 추출 위치에 있다는 것을 의미
	    			    }
	    				else
	    				{
	    			    	blRtn = true;	//보급 예정인 제품으로 적치단에 있음을 의미
	    			    }
    				}
    				else
    				{
    					blRtn = false;
    				}
    			}
    			else if (YfConstant.PROCESS_ID_3.equals(procId))	//HFL 추출
    			{
    				if (YfConstant.BAY_GP_C.equals(ydBayGp))		//HFL 추출가능동 : C동
    				{
	    				if (YfConstant.WORK_HFL_OUT_FD.equals(ydEquip))		//HFL설치 출측 위치
	    				{
	    					blRtn = true;	//보급이나 추출 위치에 있다는 것을 의미
	    			    }
	    				else
	    				{
	    			    	blRtn = false;	//보급 예정인 제품으로 적치단에 있음을 의미
	    			    }
    				}
    				else
    				{
    					blRtn = false;
    				}
    			}
    			else if (YfConstant.PROCESS_ID_5.equals(procId))	//HFL Take-In
    			{
    				if (YfConstant.BAY_GP_B.equals(ydBayGp) || YfConstant.BAY_GP_C.equals(ydBayGp))		//Take-In 가능동 : B동 C동
    				{
	    				if (YfConstant.WORK_HFL_IN_FE.equals(ydEquip) || YfConstant.WORK_HFL_OUT_FD.equals(ydEquip))	//HFL설비 보급 또는 출측 위치
	    				{
	    					blRtn = false;	//보급이나 추출 위치에 있다는 것을 의미
	    			    }
	    				else
	    				{
	    			    	blRtn = true;	//보급 예정인 제품으로 적치단에 있음을 의미
	    			    }
    				}
    				else
    				{
    					blRtn = false;
    				}
    			}
    		}

    		commUtils.printLog(logId, "=============코일 위치 정보 확인 종료========", "SL");

    		commUtils.printLog(logId, methodNm, "S-");

	    	return blRtn;
	    }
		catch(DAOException daoe)
		{
	        throw daoe;
	    }
		catch(Exception e)
		{
	        throw new EJBServiceException(e);
	    }
	}

	/**
	 * 오퍼레이션명 : SPM / HFL / EQL 보급, 추출, Take-In
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 */
	public JDTORecord callLineInOut(String logId, String methodNms, String YardID, String WorkID, String ProcessID, String CoilNo, String position, String modifier)
	{
		String		methodNm	= "SPM / HFL / EQL 보급, 추출, Take-In[ACoilRevL3SeEJB.callLineInOut] < " + methodNms;
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			commUtils.printLog(logId, "=============SPM/HFL/EQL 보급,추출,Take-In 시작========", "SL");

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STL_NO",		CoilNo);
			jrParam.setField("MODIFIER",	modifier);

			/**
			 * 	작업예약이 존재하면 ERROR
			 */
			JDTORecordSet jsChk2 = commDao.select(jrParam, getYmwBookYN, logId, methodNm, "작업예약 등록여부");

			if (jsChk2 != null && jsChk2.size() > 0)
			{
				if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN")))
				{
					commUtils.printLog(logId, "작업요구한 CoilNo이 이미 작업예약되어 있슴  Error : "+ CoilNo, "SL");
					return jrRtn;
				}
			}

			JDTORecordSet jsStackLayer = commDao.select(jrParam, getselectStackColGp1, logId, methodNm, "TB_YF_STKLYR 정보");

			if (jsStackLayer.size() < 1)
			{
				commUtils.printLog(logId, "TB_YF_STKLYR 이상: "+ CoilNo, "SL");
				return jrRtn;
	    	}

			jsStackLayer.first();
			JDTORecord jrStackLayer = jsStackLayer.getRecord();
			String ydStackColGp	= commUtils.nvl(jrStackLayer.getFieldString("YD_STK_COL_GP"),	"");
			String ydGp			= commUtils.nvl(jrStackLayer.getFieldString("YD_STK_COL_GP1"),	"");
			String ydBayGp		= commUtils.nvl(jrStackLayer.getFieldString("YD_STK_COL_GP2"),	"");
			String ydStackBedNo	= commUtils.nvl(jrStackLayer.getFieldString("YD_STK_BED_NO"),	"");
			String ydStackLyrNo	= commUtils.nvl(jrStackLayer.getFieldString("YD_STK_LYR_NO"),	"");
			String chk			= commUtils.nvl(jrStackLayer.getFieldString("CHK"),				"");
			String ydSchCd 		= "";	//SCH_CD
			String toLocGuide 	= "";	//TO위치 가이드

			if (ydStackColGp != null && !"".equals(ydStackColGp))
			{
				if (YfConstant.WORK_SPM_S.equals(WorkID))	//SPM
				{
					if (YfConstant.PROCESS_ID_1.equals(ProcessID))		//SPM 보급
					{
						// 보급위치 : YD에서는 TO위치 1개만 관리, 보급요구가 오면 무조건 작업예약 생성
						ydSchCd	= ydGp + ydBayGp + "KE01UM";
					}
					else if (YfConstant.PROCESS_ID_3.equals(ProcessID))	//SPM 추출
					{
						//2020.03.13 구용모주임 요청  추출시 진도코드 판정보류(F) + 보류원인코드='43' 인경우도 재작업으로 처리
						if("J".equals(chk) || "Q".equals(chk) || "43".equals(chk))
						{
							// Scrap SPM 재작업
							ydSchCd	= ydGp + ydBayGp + "KE10UM";
						}
						else
						{
							// Scrap SPM 추출.
							ydSchCd	= ydGp + ydBayGp + "KD01LM";
						}

						ydStackLyrNo = "01";
					}
					else if (YfConstant.PROCESS_ID_5.equals(ProcessID))	//SPM TAKE IN처리
					{
						ydSchCd	= ydGp + ydBayGp + "KE02UM" ;
						toLocGuide = position;

						//2020.04.21 SPM E동  Take-In인 경우는 위치 고정
						if("E".equals(ydBayGp))
						{
							toLocGuide = "1EKE0101";
						}
					}
				}
				else if (YfConstant.WORK_HFL_H.equals(WorkID))	//HFL
				{
					if (YfConstant.PROCESS_ID_1.equals(ProcessID))		//HFL 보급
					{
						ydSchCd	= ydGp + ydBayGp + "FE01UM" ;
					}
					else if (YfConstant.PROCESS_ID_3.equals(ProcessID))	//HFL 추출
					{
						ydSchCd	= ydGp + ydBayGp + "FD01LM" ;
						ydStackLyrNo = "01";
					}
					else if (YfConstant.PROCESS_ID_5.equals(ProcessID))	//HFL TAKE IN처리
					{
						ydSchCd	= ydGp + ydBayGp + "FE02UM" ;
						toLocGuide = position;
				 	}
				}
				else if (YfConstant.WORK_SPM_E.equals(WorkID))	//EQL
				{
					if (YfConstant.PROCESS_ID_1.equals(ProcessID))		//EQL 보급
					{
						// 보급위치 : YD에서는 TO위치 1개만 관리, 보급요구가 오면 무조건 작업예약 생성
						ydSchCd	= ydGp + ydBayGp + "QE01UM";
					}
					else if (YfConstant.PROCESS_ID_3.equals(ProcessID))	//EQL 추출
					{
						if("J".equals(chk) || "Q".equals(chk))
						{
							// Scrap EQL 재작업
							ydSchCd	= ydGp + ydBayGp + "QE10UM";
						}
						else
						{
							// Scrap EQL 추출.
							ydSchCd	= ydGp + ydBayGp + "QD01LM";
						}

						ydStackLyrNo = "01";
					}
					else if (YfConstant.PROCESS_ID_5.equals(ProcessID))	//EQL TAKE IN처리
					{
						ydSchCd	= ydGp + ydBayGp + "QE02UM" ;
						toLocGuide = position;
					}
				}

				if("".equals(ydSchCd))
				{
    				throw new Exception("스케쥴 코드 생성 실패");
    			}

				/***************************************
				 * 	1.작업예약이 생성
                 *  2.STOCK UPDATE(작업행선)
                 *  3.스케쥴 호출
				 **************************************/
				//작업예약,작업재료 등록
    			JDTORecord jrOutTemp = JDTORecordFactory.getInstance().create();
    			jrOutTemp.setField("STL_NO",			CoilNo); //재료번호
    			jrOutTemp.setField("YD_STK_COL_GP",		ydStackColGp);
    			jrOutTemp.setField("YD_STK_BED_NO",		ydStackBedNo);
    			jrOutTemp.setField("YD_STK_LYR_NO",		ydStackLyrNo);
    			jrOutTemp.setField("YD_SCH_CD",			ydSchCd);
    			jrOutTemp.setField("MODIFIER",			modifier);
    			jrOutTemp.setField("YD_TO_LOC_GUIDE",	toLocGuide);	//TO위치가이드

    			String ydWbookId = yfComm.procWkBookInsert(jrOutTemp);

    			if(YfConstant.RETN_CD_FAILURE.equals(ydWbookId))
    			{
    				throw new Exception("작업예약ID 생성 실패");
    			}

				/**
				 * 	1.저장품 수정(STOCK_MOVE_TERM)
                 *  2.스케쥴 호출(TB_YF_SCHRULE 조회 후 YD_SCH_AUTO_ST_YN(야드스케줄자동기동여부)가  'Y' 일경우 스케쥴 호출)
				 */
				// 저장품 Table(TB_YF_STOCK)에 작업예약ID,저장품이동조건을 Update 한다.
				String sStackMoveTerm = "";

				if ("S".equals(CoilNo.substring(0, 1)))
				{
					// Scrap에 대한 코드는 고정으로 적용한다.
					sStackMoveTerm =  YfConstant.NEW_STOCK_MOVE_TERM_A2;
				}
				else
				{
					JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);

					if(jrRtnProg.size() > 0 )
					{
						sStackMoveTerm = commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"));
					}
				}

				jrParam.setField("MODIFIER",		modifier);    	//수정자
				jrParam.setField("STOCK_MOVE_TERM",	sStackMoveTerm);  //저장품이동조건
				jrParam.setField("STL_NO",			CoilNo);
				commDao.update(jrParam, updStockTransInfo1, logId, methodNm, "TB_YF_STOCK 수정");


				/***********************************************************************
				 * HFL/SPM/EQL 보급시 해당 스케줄 자동기동 여부가 N이면 스케줄 기동하지 않음
				 ***********************************************************************/
				jrParam.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드
				JDTORecordSet jrSchInfo = commDao.select(jrParam, getSchCdInfo, logId, methodNm, "TB_YF_SCHRULE 조회");
				String ydCrnSchChk = jrSchInfo.getRecord(0).getFieldString("YD_SCH_AUTO_ST_YN");

				if("Y".equals(ydCrnSchChk))
				{
					/**
					* 3. 크레인스케줄 전문 호출
					**********************************************************/
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

					jrYdMsg.setField("YD_WBOOK_ID",		ydWbookId);	//야드작업예약ID
					jrYdMsg.setField("YD_SCH_CD",		ydSchCd);	//야드스케쥴코드
					jrYdMsg.setField("YD_SCH_ST_GP",	"O");		//야드스케쥴기동구분
					jrYdMsg.setField("YD_SCH_REQ_GP",	"L");		//야드스케쥴요청구분(인출)
					jrYdMsg.setField("MODIFIER",		modifier);	//수정자

					jrRtn = commUtils.addSndData(yfComm.getCrnSchMsg(jrYdMsg));
				}
			}

			commUtils.printLog(logId, "=============SPM/HFL/EQL 보급,추출,Take-In 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch(DAOException daoe)
		{
	        throw daoe;
	    }
		catch(Exception e)
		{
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
	public boolean procCoilStock(String logId, String methodNms, String YardID, String WorkID, String ProcessID, String CoilNo, String modifier)
	{
		String		methodNm	= "코일 생성처리[ACoilRevL3SeEJB.procCoilStock] < " + methodNms;
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			commUtils.printLog(logId, "=============코일생성처리 시작========", "SL");

			/**
			 * 저장품 CHECK
			 */
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STL_NO",		CoilNo);
			jrParam.setField("MODIFIER",	modifier);

			JDTORecordSet jsChk1 = commDao.select(jrParam, selectStockIdNoDel, logId, methodNm, " 저장품 등록여부");

			if (jsChk1.size() < 1)
			{
				if (YfConstant.PROCESS_ID_3.equals(ProcessID) || YfConstant.PROCESS_ID_4.equals(ProcessID))	// 추출,TAKE OUT인 경우 STOCK 이 없는 경우 생성
				{
			    	if ("S".equals(CoilNo.substring(0, 1)))	// 코일번호가 S  SCRAP
			    	{
			    		commDao.update(jrParam, insStockScrapInfo, logId, methodNm, "TB_YF_STOCK 등록 - SCRAP COIL");
			    	}
			    	else
			    	{
						commDao.update(jrParam, insertStockTransInfo, logId, methodNm, "TB_YF_STOCK 등록 - COIL");
			    	}
				}
				else
				{
					commUtils.printLog(logId, "COIL_NO :이상 "+ CoilNo, "SL");
					return false;
				}
	    	}

			commUtils.printLog(logId, "=============코일생성처리 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");
		}
		catch(DAOException daoe)
		{
	        throw daoe;
	    }
		catch(Exception e)
		{
	        throw new EJBServiceException(e);
	    }

	    return true;
	}

	/**
	 * 오퍼레이션명 :
	 *
	 *  SPM / HFL Take-Out
	 * param YardID : 야드구분
	 * param WorkID : S SPM, H HFL, E EQL
	 * param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * param CoilNo
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 */
	public JDTORecord callTakeOut(JDTORecord rcvMsg)throws DAOException
	{
		String		methodNm	= "SPM/HFL Take-Out[ACoilRevL3SeEJB.callTakeOut] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		boolean		isSuccess	= false;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER",	modifier);	//수정자 셋팅

			commUtils.printLog(logId, "=============SPM/HFL/EQL Take-Out 시작========", "SL");

			//수신 항목 값
			String WorkId 	= commUtils.trim(rcvMsg.getFieldString("WORKID"));		// 공정코드 S:SPM,H:HFL,E:EQL
			String procId 	= commUtils.trim(rcvMsg.getFieldString("PROCESSID"));	// 4 Take-Out
			String CoilNo 	= commUtils.trim(rcvMsg.getFieldString("COILNO"));		// 'S' Scrap 'H' A열연  'K' B열연
			String position = commUtils.trim(rcvMsg.getFieldString("POSITION"));	// 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨

			jrParam.setField("STL_NO",		CoilNo);

			/**
			 * 	작업예약이 존재하면 ERROR
			 */
			JDTORecordSet jsChk2 = commDao.select(jrParam, getYmwBookYN, logId, methodNm, "작업예약 등록여부");

			if (jsChk2 != null && jsChk2.size() > 0)
			{
				if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN")))
				{
					commUtils.printLog(logId, "작업요구한 CoilNo이 이미 작업예약되어 있슴  Error : "+ CoilNo, "SL");
					return jrRtn;
				}
			}

			// 기존 위치 삭제
	   		commDao.update(jrParam, updStackLayer, logId, methodNm, "기존 위치 삭제");

			int updCnt = commDao.update(jrParam, updTrkStackColGplayerTakeOut, logId, methodNm, "TB_YF_EQPTRACKING 조회해서 TB_YF_STKLYR 수정");

			if(updCnt == 0 )
			{
				commUtils.printLog(logId, "적치단(TB_YF_STKLYR) + 트래킹(TB_YF_EQPTRACKING) Table Read Error : "+ CoilNo, "SL");
				throw new Exception("적치단(TB_YF_STKLYR) + 트래킹(TB_YF_EQPTRACKING) Table Read Error : "+ CoilNo);
				//return jrRtn;
			}

			JDTORecordSet jsStackLayer = commDao.select(jrParam, getselectStackColGp1, logId, methodNm, "TB_YF_STKLYR 정보");

			if (jsStackLayer.size() < 1)
			{
				commUtils.printLog(logId, "YD_STK_COL_GP 이상: "+ CoilNo, "SL");
				return jrRtn;
	    	}

			jsStackLayer.first();
			JDTORecord jrStackLayer = jsStackLayer.getRecord();
			String ydStackColGp	= commUtils.nvl(jrStackLayer.getFieldString("YD_STK_COL_GP"),	"");
			String ydGp			= commUtils.nvl(jrStackLayer.getFieldString("YD_STK_COL_GP1"),	"");
			String ydBayGp		= commUtils.nvl(jrStackLayer.getFieldString("YD_STK_COL_GP2"),	"");
			String ydStackBedGp	= commUtils.nvl(jrStackLayer.getFieldString("YD_STK_BED_NO"),	"");
			String ydStackLayer	= commUtils.nvl(jrStackLayer.getFieldString("YD_STK_LYR_NO"),   "");
			String stackStat	= commUtils.nvl(jrStackLayer.getFieldString("YD_STK_LYR_STAT"),	"");
			String TmpEquip 	= ydStackColGp.substring(2,4);

			/*
	    	 * Take-Out 요구가 왔을때 수신한 Coil이 Take-Out이 왔는데 Coil이 야드 Skid에 있다면 Error
			 */
			if (YfConstant.WORK_SPM_S.equals(WorkId))		//SPM
	    	{
    			if (YfConstant.PROCESS_ID_4.equals(procId))	//Take-Out
    			{
    				//SPM Take-Out STACK_COL_GP_1DKE01/STACK_COL_GP_1EKE01/STACK_COL_GP_1FKD01
    				if
    				(
    					YfConstant.STACK_COL_GP_1DKE01.equals(ydStackColGp) ||
    					YfConstant.STACK_COL_GP_1EKD01.equals(ydStackColGp) ||
    					YfConstant.STACK_COL_GP_1FKD01.equals(ydStackColGp)
					)
    				{
    					//??
    			    }
    				else
    				{
    					commUtils.printLog(logId, "YD_STK_COL_GP 이상: "+ CoilNo, "SL");
    					return jrRtn;
    			    }
    			}
    		}
	    	else if (YfConstant.WORK_HFL_H.equals(WorkId))	//HFL
    		{
    			if (YfConstant.PROCESS_ID_4.equals(procId))	//Take-Out
    			{
    				//HFL Take-Out
    				if
    				(
    					YfConstant.STACK_COL_GP_1BFE01.equals(ydStackColGp)	||
    					YfConstant.STACK_COL_GP_1CFD01.equals(ydStackColGp)
    				)
    				{
    					//??
    			    }
    				else
    				{
    					commUtils.printLog(logId, "YD_STK_COL_GP 이상: "+ CoilNo, "SL");
    					return jrRtn;
    			    }
    			}
    		}
    		else if (YfConstant.WORK_SPM_E.equals(WorkId))	//EQL
    		{
    			if (YfConstant.PROCESS_ID_4.equals(procId))	//Take-Out
    			{
    				//EQL Take-Out
    				if
    				(
    					YfConstant.STACK_COL_GP_1EQE01.equals(ydStackColGp)	||
    					YfConstant.STACK_COL_GP_1FQE01.equals(ydStackColGp)	||
    					YfConstant.STACK_COL_GP_1GQD01.equals(ydStackColGp)
    				)
    				{
    					//??
    			    }
    				else
    				{
    					commUtils.printLog(logId, "YD_STK_COL_GP 이상: "+ CoilNo, "SL");
    					return jrRtn;
    			    }
    			}
    		}
			//TB_YF_EQPTRACKING 테이블에서 정보 읽어서 셋팅하면서 체크하는부분 주석처리함

			String TmpstackCol = "";
			String ydSchCd     = "";

			if (ydStackColGp != null && !"".equals(ydStackColGp))
			{
				if (YfConstant.WORK_SPM_S.equals(WorkId))
				{
					ydSchCd 	= YfConstant.YD_GP_1 + ydBayGp + "KD02LM" ;	//SPM Take Out 스케쥴코드
				}
				else if (YfConstant.WORK_HFL_H.equals(WorkId))
				{
					ydSchCd 	= YfConstant.YD_GP_1 + ydBayGp + "FD02LM" ;	//HFL Take Out 스케쥴코드
				}
				else if (YfConstant.WORK_SPM_E.equals(WorkId))
				{
					ydSchCd 	= YfConstant.YD_GP_1 + ydBayGp + "QD02LM" ;	//EQL Take Out 스케쥴코드
				}

				/**
				 * 	1.작업예약이 생성
                 *  2.STOCK UPDATE(작업행선)
                 *  3.스케쥴 호출
				 */
	  			//작업예약,작업재료 등록
    			JDTORecord jrOutTemp = JDTORecordFactory.getInstance().create();
    			jrOutTemp.setField("STL_NO",		CoilNo); //재료번호
    			jrOutTemp.setField("YD_STK_COL_GP",	ydStackColGp);
    			jrOutTemp.setField("YD_STK_BED_NO",	ydStackBedGp);
    			jrOutTemp.setField("YD_STK_LYR_NO",	ydStackLayer);
    			jrOutTemp.setField("YD_SCH_CD",		ydSchCd);
    			jrOutTemp.setField("MODIFIER",		modifier);

    			String ydWbookId = yfComm.procWkBookInsert(jrOutTemp);

    			if(YfConstant.RETN_CD_FAILURE.equals(ydWbookId))
    			{
    				throw new Exception("작업예약ID 생성 실패");
    			}

				/**
				 * 	1.저장품 수정
                 *  2.STOCK UPDATE(작업행선)
                 *  3.스케쥴 호출
				 */
				// 저장품 Table(TB_YF_STOCK)에 작업예약ID,저장품이동조건을 Update 한다.

				String sStackMoveTerm = "";

				jrParam.setField("STL_NO",		CoilNo);   //
				jrParam.setField("MODIFIER",	modifier); //수정자

				JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);

				if(jrRtnProg.size() > 0 )
				{
					sStackMoveTerm = commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"));
				}

				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("MODIFIER",		modifier);			//수정자
				jrParam.setField("WBOOK_ID",		ydWbookId);			//작업예약
				jrParam.setField("PROCESS_ID",		procId);			//처리구분
				jrParam.setField("WORK_ID",			WorkId);			//처리구분
				jrParam.setField("STOCK_MOVE_TERM",	sStackMoveTerm);	//저장품이동조건
				jrParam.setField("STL_NO",			CoilNo);
				commDao.update(jrParam, updStockTransInfo1, logId, methodNm, "TB_YF_STOCK 수정");

				/**********************************************************
				* 2.2 크레인스케줄 전문 호출
				**********************************************************/
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name

				jrYdMsg.setField("YD_WBOOK_ID",		ydWbookId);	//야드작업예약ID
				jrYdMsg.setField("YD_SCH_CD",		ydSchCd);	//야드스케쥴코드
				jrYdMsg.setField("YD_SCH_ST_GP",	"O");		//야드스케쥴기동구분
				jrYdMsg.setField("YD_SCH_REQ_GP",	"L");		//야드스케쥴요청구분(인출)
				jrYdMsg.setField("MODIFIER",		modifier);	//수정자

				jrRtn = commUtils.addSndData(yfComm.getCrnSchMsg(jrYdMsg));
			}

			commUtils.printLog(logId, "=============SPM/HFL/EQL Take-Out 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch(DAOException daoe)
		{
	        throw daoe;
	    }
		catch(Exception e)
		{
	        throw new EJBServiceException(e);
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
	public JDTORecord rcvPOYMJ008(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "코일공냉재 실적 수신[ACoilRcvL3SeEJB.rcvPOYMJ008] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일공냉재 실적 수신 시작========", "SL");

			//수신 항목 값
			String sYD_GP	= commUtils.trim(rcvMsg.getFieldString("yardID"));
			String sSTL_NO	= commUtils.trim(rcvMsg.getFieldString("stockid"));

			/**
			 * 1. 수신 항목 값 Check
			 */
			if ("".equals(sSTL_NO))
			{
				throw new Exception("저장품이 없습니다..");
			}

			/**
			 * 2. 조업에서 공냉재 실적 발생후 종합판정한뒤 야드에게 실적 처리완료 정보를 송신
			 */
			jrParam.setField("TC_CD",		YfConstant.POYMJ008);
			jrParam.setField("STL_NO",		sSTL_NO);
			jrParam.setField("MODIFIER",	modifier);

			JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);

			String sCURR_PROG_CD    = jrRtnProg.getFieldString("CURR_PROG_CD");
			String sSTOCK_MOVE_TERM = jrRtnProg.getFieldString("STOCK_MOVE_TERM");

			if (YfConstant.NEW_STOCK_MOVE_TERM_HG.equals(sSTOCK_MOVE_TERM))
			{
				jrParam.setField("STL_NO",			sSTL_NO);
	    		jrParam.setField("STOCK_ITEM",		YfConstant.ITEM_CG);
	    		jrParam.setField("STOCK_MOVE_TERM",	sSTOCK_MOVE_TERM);
	    		commDao.update(jrParam, updateStockTransInfo_06, logId, methodNm, "TB_YF_STOCK 수정");
	    	}
			else
			{
				jrParam.setField("STL_NO",			sSTL_NO);
	    		jrParam.setField("STOCK_MOVE_TERM",	sSTOCK_MOVE_TERM);
	    		commDao.update(jrParam, updateStockTransInfo, logId, methodNm, "TB_YF_STOCK 수정");
	    	}

			commUtils.printLog(logId, "=============코일공냉재 실적 수신 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * [A] 오퍼레이션명 : 코일충당실적(PTYDJ001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPTYDJ001(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "코일충당실적[ACoilRcvL3SeEJB.rcvPTYDJ001] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일충당실적 시작========", "SL");

			//수신 항목 값
			String sSTL_NO	= commUtils.trim(rcvMsg.getFieldString("STL_NO"));

			/**
			 * 1. 수신 항목 값 Check
			 */
			if ("".equals(sSTL_NO))
			{
				throw new Exception("충당재료 정보가 없습니다..");
			}

			/**
			 * 2. 저장품 이동 조건 수정
			 */
			jrParam.setField("STL_NO",	sSTL_NO);
			jrParam.setField("TC_CD",	YfConstant.PTYDJ001);

			JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);

			String sCURR_PROG_CD    = jrRtnProg.getFieldString("CURR_PROG_CD");
			String sSTOCK_MOVE_TERM = jrRtnProg.getFieldString("STOCK_MOVE_TERM");

			if (!"".equals(sSTOCK_MOVE_TERM))
			{
				jrParam.setField("STL_NO",			sSTL_NO);
	    		jrParam.setField("STOCK_MOVE_TERM",	sSTOCK_MOVE_TERM);
	    		commDao.update(jrParam, updateStockTransInfo, logId, methodNm, "TB_YF_STOCK 수정");
	    	}

			//======================================================
			// 저장품제원 : 코일야드L2 로 송신(YFF1L002)
			//======================================================
//			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
//			sndL2Msg.setResultCode(logId);		//Log ID
//			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
//			sndL2Msg.setField("YD_INFO_SYNC_CD",	"5");		//야드정보동기화코드
//			sndL2Msg.setField("MSG_GP",				"I");		//전문구분
//			sndL2Msg.setField("STL_NO",				sSTL_NO);	//재료번호
//
//			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	//전송 Data 생성

			commUtils.printLog(logId, "=============코일충당실적 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * [A] 오퍼레이션명 : 코일소재이송지시(PTYDJ002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPTYDJ002(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "코일소재이송지시[ACoilRcvL3SeEJB.rcvPTYDJ002] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    String		szMsg		= "";

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일소재이송지시 시작========", "SL");

			//수신 항목 값
			String FmWordDt = commUtils.trim(rcvMsg.getFieldString("FRTOMOVE_WORD_DATE"));	//이송작업지시일자

			/**
			 * 1. 수신 항목 값 Check
			 */
			if ("".equals(FmWordDt))
			{
				//throw new Exception("코일이 없습니다..");
				szMsg = "["+methodNm+"] 이송작업지시일자가 없습니다..";
		      	commUtils.printLog(logId, szMsg, "SL");

		      	return jrRtn;
			}

			/**
			 * 2. 저장품 이동 조건 수정
			 */
			jrParam.setField("FRTOMOVE_WORD_DATE",	FmWordDt); //이송작업지시일자
			JDTORecordSet jsSch = commDao.select(jrParam, getYdStockFRTOMOVE_WORD_DATE_COIL, logId, methodNm, "크레인스케줄 조회");

			JDTORecord recInCrn = JDTORecordFactory.getInstance().create();

			for(int Loop_i = 1; Loop_i <= jsSch.size(); Loop_i++)
			{
				jsSch.absolute(Loop_i);
				recInCrn	= jsSch.getRecord();

				jrParam.setField("STL_NO",	recInCrn.getFieldString("STL_NO")); //충당재료
				jrParam.setField("TC_CD",	YfConstant.PTYDJ002);

				JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);

				jrParam.setField("STOCK_MOVE_TERM" , commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));
				commDao.update(jrParam, updStockTransInfo_05, logId, methodNm, "TB_YF_STOCK 수정");
			}

			commUtils.printLog(logId, "=============코일소재이송지시 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * [A] 오퍼레이션명 : 코일소재임가공이송지시(PTYDJ003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPTYDJ003(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "코일소재임가공이송지시[ACoilRcvL3SeEJB.rcvPTYDJ003] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    String		szMsg		= "";

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일소재임가공이송지시 시작========", "SL");

			//수신 항목 값
			String FmWordDt = commUtils.trim(rcvMsg.getFieldString("FRTOMOVE_WORD_DATE"));	//이송작업지시일자

			/**
			 * 1. 수신 항목 값 Check
			 */
			if("".equals(FmWordDt))
			{
				//throw new Exception("이송작업지시일자가 없습니다..");
				szMsg = "["+methodNm+"] 이송작업지시일자가 없습니다..";
		      	commUtils.printLog(logId, szMsg, "SL");

		        return jrRtn;
			}

			/**
			 * 2. 저장품 이동 조건 수정
			 */
			jrParam.setField("FRTOMOVE_WORD_DATE",	FmWordDt); //이송작업지시일자
			JDTORecordSet jsSch = commDao.select(jrParam, getYdStockFRTOMOVE_WORD_DATE_RENTCOIL, logId, methodNm, "크레인스케줄 조회");

			JDTORecord recInCrn = JDTORecordFactory.getInstance().create();

			for(int Loop_i = 1; Loop_i <= jsSch.size(); Loop_i++)
			{
				jsSch.absolute(Loop_i);
				recInCrn	= jsSch.getRecord();

				jrParam.setField("STL_NO",	recInCrn.getFieldString("STL_NO")); //충당재료

				JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);

				jrParam.setField("STOCK_MOVE_TERM" , commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));
				commDao.update(jrParam, updStockTransInfo_05, logId, methodNm, "TB_YF_STOCK 수정");
			}

			commUtils.printLog(logId, "=============코일소재임가공이송지시 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 오퍼레이션명 : 코일제품보류확정(DMYDR002 JMS수신)
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 */
//	public boolean rcvDMYDR002(JDTORecord rcvMsg)
//	{
//		String		methodNm	= "코일제품출하차량도착실적[ACoilRcvL3SeEJB.rcvDMYDR002] < " + rcvMsg.getResultMsg();
//		String		logId		= rcvMsg.getResultCode();
//	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
//
//	    String		szMsg		= "";
//	    String[]	rVal 		= new String[2];
//	    String		sStocMv		= "";
//	    int			intRtnVal 	= 0;
//	    boolean		isVal		= false;
//
//		try
//		{
//			commUtils.printLog(logId, methodNm, "S+");
//
//			//기본 수신 항목 값
//			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
//			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
//
//			if ("".equals(modifier))
//			{
//				modifier = msgId;
//			}
//
//			jrParam.setResultCode(logId);	//Log ID
//			jrParam.setResultMsg(methodNm);	//Log Method Name
//			jrParam.setField("MODIFIER", modifier); //수정자 셋팅
//
//			commUtils.printLog(logId, "=============코일제품출하차량도착실적 시작========", "SL");
//
//			//수신 항목 값
//			String stl_No = commUtils.paraRecChkNull(rcvMsg, "STL_NO");
//
//			//======================================================
//			// 수신항목중 STL_NO 체크 없으면 Exception 처리
//			//======================================================
//			if ("".equals(stl_No))
//			{
//				throw new Exception("저장품Id(STL_NO)가 없습니다..");
//			}
//
//			//======================================================
//			// 저장품 이동 조건(STOCK_MOVE_TERM) 생성 및 TB_YF_STOCK에 업데이트
//			//======================================================
//			rVal = commUtils.getCoilCurrProgCd(stl_No, tcCode);
//			sStocMv = rVal[1];
//
//			jrParam.setField("STL_NO",			stl_No);
//			jrParam.setField("STOCK_MOVE_TERM",	sStocMv);
//			jrParam.setField("MODIFIER",		modifier);
//			intRtnVal = commDao.update(jrParam, updYdStock, logId, methodNm, "TB_YF_STOCK 수정");
//
//			if(intRtnVal > 0)
//			{
//				szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATE가 존재함";
//				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
//			}
//			else if(intRtnVal == 0)
//			{
//				szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATA가 존재하지 않음";
//				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
//
//				throw new Exception(szMsg);
//			}
//
//			commUtils.printLog(logId, "=============코일제품출하차량도착실적 종료========", "SL");
//
//			commUtils.printLog(logId, methodNm, "S-");
//
//			isVal = true;
//		}
//		catch (DAOException e)
//		{
//			throw e;
//		}
//		catch (Exception e)
//		{
//			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
//		}
//
//		return isVal;
//	}

	/**
	 * 오퍼레이션명 : 코일제품출하지시대기(DMYDR005 JMS수신)
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR005(JDTORecord rcvMsg)
	{
		String		methodNm	= "코일제품출하차량도착실적[ACoilRcvL3SeEJB.rcvDMYDR005] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    String		szMsg		= "";
	    JDTORecord	jrRtnProg	= JDTORecordFactory.getInstance().create();
	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
	    int			intRtnVal 	= 0;
	    boolean		isVal		= false;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일제품출하차량도착실적 시작========", "SL");

			//수신 항목 값
			String stl_No = commUtils.paraRecChkNull(rcvMsg, "STL_NO");

			//======================================================
			// 수신항목중 STL_NO 체크 없으면 Exception 처리
			//======================================================
			if ("".equals(stl_No))
			{
				throw new Exception("저장품Id(STL_NO)가 없습니다..");
			}

			//======================================================
			// 저장품 이동 조건(STOCK_MOVE_TERM) 생성 및 TB_YF_STOCK에 업데이트
			//======================================================
			jrParam.setField("TC_CD",	tcCode);
			jrParam.setField("STL_NO",	stl_No);
			jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);

			jrParam.setField("STOCK_MOVE_TERM", commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));
			intRtnVal = commDao.update(jrParam, updYdStock, logId, methodNm, "TB_YF_STOCK 수정");

			if(intRtnVal > 0)
			{
				szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATE가 존재함";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
			}
			else if(intRtnVal == 0)
			{
				szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATA가 존재하지 않음";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

				throw new Exception(szMsg);
			}

			//======================================================
			// 저장품제원 : 코일야드L2 로 송신(YFF1L002)	//L2전문 확인후 수정
			//======================================================
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);						//Log ID
			sndL2Msg.setResultMsg(methodNm);					//Log Method Name
			sndL2Msg.setField("YD_INFO_SYNC_CD",	"5");		//야드정보동기화코드
			sndL2Msg.setField("MSG_GP",				"I");		//전문구분
			sndL2Msg.setField("STL_NO",				stl_No);	//재료번호

			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성

			commUtils.printLog(logId, "=============코일제품출하차량도착실적 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 오퍼레이션명 : 코일제품반납대기(DMYDR008 JMS수신)
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR008(JDTORecord rcvMsg)
	{
		String		methodNm	= "코일제품반납대기[ACoilRcvL3SeEJB.rcvDMYDR008] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    String		szMsg		= "";
	    JDTORecord	jrRtnProg	= JDTORecordFactory.getInstance().create();
	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
	    int			intRtnVal 	= 0;
	    boolean		isVal		= false;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일제품반납대기 시작========", "SL");

			//수신 항목 값
			String stl_No = commUtils.paraRecChkNull(rcvMsg, "STL_NO");

			//======================================================
			// 수신항목중 STL_NO 체크 없으면 Exception 처리
			//======================================================
			if ("".equals(stl_No))
			{
				throw new Exception("저장품Id(STL_NO)가 없습니다..");
			}

			//======================================================
			// 저장품 이동 조건(STOCK_MOVE_TERM) 생성 및 TB_YF_STOCK에 업데이트
			//======================================================
			jrParam.setField("TC_CD",	tcCode);
			jrParam.setField("STL_NO",	stl_No);
			jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);

			jrParam.setField("STOCK_MOVE_TERM", commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));
			intRtnVal = commDao.update(jrParam, updYdStock, logId, methodNm, "TB_YF_STOCK 수정");

			if(intRtnVal > 0)
			{
				szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATE가 존재함";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
			}
			else if(intRtnVal == 0)
			{
				szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATA가 존재하지 않음";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

				throw new Exception(szMsg);
			}

			//======================================================
			// 저장품제원 : 코일야드L2 로 송신(YFF1L002)	//L2전문 확인후 수정
			//======================================================
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);						//Log ID
			sndL2Msg.setResultMsg(methodNm);					//Log Method Name
			sndL2Msg.setField("YD_INFO_SYNC_CD",	"5");		//야드정보동기화코드
			sndL2Msg.setField("MSG_GP",				"I");		//전문구분
			sndL2Msg.setField("STL_NO",				stl_No);	//재료번호

			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성

			commUtils.printLog(logId, "=============코일제품반납대기 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 오퍼레이션명 : 코일제품고간이송지시(DMYDR011 JMS수신)
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 * @ejb.transaction type="RequiresNew"
	 */
	public boolean rcvDMYDR011(JDTORecord rcvMsg)
	{
		String		methodNm	= "코일제품고간이송지시 [ACoilRcvL3SeEJB.rcvDMYDR011] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    int			i			= 0;
	    int 		intRtnVal	= 0;
	    boolean		isVal		= false;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일제품고간이송지시 시작========", "SL");

			//수신 항목 값
			String cancelYN	= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));	//Y: 취소 , N: 지시

			//======================================================
			// 수신항목중 CANCEL_YN 의 값이 'Y'면 Exception 처리
			//======================================================
			if ("Y".equals(cancelYN))
			{
				throw new Exception("CANCEL_YN 의 값이 'Y'입니다...");
			}

			//======================================================
			// 수신항목중 STL_NO1 ~ STL_NO20까지 FOR문을 돌면서 진도코드/저장품이동조건을 구하고 TB_YF_STOCK 에 업데이트 함
			//======================================================
			for(i = 1 ; i<=20; i++)
			{
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name

				jrParam.setField("STL_NO",				commUtils.trim(rcvMsg.getFieldString("STL_NO" + i))); //저장품 ID
				jrParam.setField("TC_CD",				tcCode);  			//TC_CODE
				JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);	//진도코드, 저장품이동조건

				jrParam.setField("YD_CAR_UPP_LOC_CD",	commUtils.trim(rcvMsg.getFieldString("SHEAR_SUPPLY_SEQ")));		//정정 보급 순서...TB_YM_STOCK당시 SHEAR_SUPPLY_SEQ컬럼이 야드차상위치 정보를 담고 있다해서 TB_YF_STOCK에서 삭제되고 YD_CAR_UPP_LOC_CD(야드차상위치) 추가됨
				jrParam.setField("STOCK_MOVE_TERM",		commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));	//저장품이동조건
				jrParam.setField("TRANS_ORD_DATE",		commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")));			//운송지시일
				jrParam.setField("TRANS_ORD_SEQNO",		commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")));		//운송지시순번
				jrParam.setField("MODIFIER",			modifier);
				jrParam.setField("CAR_KIND",			commUtils.trim(rcvMsg.getFieldString("CAR_KIND")));				//차량종류
				jrParam.setField("CAR_CARD_NO",			commUtils.trim(rcvMsg.getFieldString("CAR_CARD_NO")));			//차량CARD번호
				jrParam.setField("CAR_NO",				commUtils.trim(rcvMsg.getFieldString("CAR_NO2")));				//차량번호2

				intRtnVal =	commDao.update(jrParam, updYfStock, logId, methodNm, "TB_YF_STOCK 수정");
			}

			commUtils.printLog(logId, "=============코일제품고간이송지시 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			isVal = true;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

		return isVal;
	}

	/**
	 * 오퍼레이션명 : 코일제품목전(DMYDR014 JMS수신)
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR014(JDTORecord rcvMsg)
	{
		boolean		isVal		= false;
		String		methodNm	= "코일제품목전[ACoilRcvL3SeEJB.rcvDMYDR014] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    String		szMsg		= "";
	    JDTORecord	jrRtnProg	= JDTORecordFactory.getInstance().create();
	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
	    int			intRtnVal 	= 0;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일제품목전 시작========", "SL");

			//수신 항목 값
			String stl_No = commUtils.paraRecChkNull(rcvMsg, "STL_NO");

			//======================================================
			// 수신항목중 STL_NO 체크 없으면 Exception 처리
			//======================================================
			if ("".equals(stl_No))
			{
				throw new Exception("저장품Id(STL_NO)가 없습니다..");
			}

			//======================================================
			// 저장품 이동 조건(STOCK_MOVE_TERM) 생성 및 TB_YF_STOCK에 업데이트
			//======================================================
			jrParam.setField("TC_CD",	tcCode);
			jrParam.setField("STL_NO",	stl_No);
			jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);

			jrParam.setField("STOCK_MOVE_TERM", commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));
			intRtnVal = commDao.update(jrParam, updYdStock, logId, methodNm, "TB_YF_STOCK 수정");

			if(intRtnVal > 0)
			{
				szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATE가 존재함";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
			}
			else if(intRtnVal == 0)
			{
				szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATA가 존재하지 않음";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

				throw new Exception(szMsg);
			}

			//======================================================
			// 저장품제원 : 코일야드L2 로 송신(YFF1L002)	//L2전문 확인후 수정
			//======================================================
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);						//Log ID
			sndL2Msg.setResultMsg(methodNm);					//Log Method Name
			sndL2Msg.setField("YD_INFO_SYNC_CD",	"5");		//야드정보동기화코드
			sndL2Msg.setField("MSG_GP",				"I");		//전문구분
			sndL2Msg.setField("STL_NO",				stl_No);	//재료번호

			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성

			commUtils.printLog(logId, "=============코일제품목전 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 오퍼레이션명 : 코일제품보관지시(DMYDR027 JMS수신)
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR027(JDTORecord rcvMsg)
	{
		String		methodNm	= "코일제품보관지시[ACoilRcvL3SeEJB.rcvDMYDR027] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    String		szMsg		= "";
	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
	    int			intRtnVal 	= 0;
	    boolean		isVal		= false;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일제품보관지시 수신 시작========", "SL");

			//수신 항목 값
			String stl_No = commUtils.paraRecChkNull(rcvMsg, "STL_NO");

			//======================================================
			// 수신항목중 STL_NO 체크 없으면 Exception 처리
			//======================================================
			if ("".equals(stl_No))
			{
				throw new Exception("저장품Id(STL_NO)가 없습니다..");
			}

			jrParam.setField("KEEP_STL_YN",	"Y");
			jrParam.setField("STL_NO",		stl_No);

			intRtnVal = commDao.update(jrParam, updYmStock27, logId, methodNm, "TB_YF_STOCK 수정");

			if(intRtnVal > 0)
			{
				szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATE가 존재함";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
			}
			else if(intRtnVal == 0)
			{
				szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATA가 존재하지 않음";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

				throw new Exception(szMsg);
			}

			//======================================================
			// 저장품제원 : 코일야드L2 로 송신(YFF1L002)
			//======================================================
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);						//Log ID
			sndL2Msg.setResultMsg(methodNm);					//Log Method Name
			sndL2Msg.setField("YD_INFO_SYNC_CD",	"5");		//야드정보동기화코드
			sndL2Msg.setField("MSG_GP",				"I");		//전문구분
			sndL2Msg.setField("STL_NO",				stl_No);	//재료번호

			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성

			commUtils.printLog(logId, "=============코일제품보관지시 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 코일제품출하완료
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR030(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm		= "코일제품출하완료[ACoilRcvL3SeEJB.rcvDMYDR030] < " + rcvMsg.getResultMsg();
		String		logId			= rcvMsg.getResultCode();
	    JDTORecord	jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn			= JDTORecordFactory.getInstance().create();

	    int			intRtnVal		= 0;
	    String		szMsg			= "";

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일제품출하완료 시작========", "SL");

			//수신 항목 값
			String szRcvTcCode 	= commUtils.trim(rcvMsg.getFieldString("JMS_TC_CD"));
			String Stl_no    	= commUtils.trim(rcvMsg.getFieldString("STL_NO"));
			String stlAppearGp	= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));
			String ydGp        	= commUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String BackUpYn     = commUtils.trim(rcvMsg.getFieldString("BACKUP_YN"));
			String sModifier	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
			String sModDdtt		= commUtils.trim(rcvMsg.getFieldString("MOD_DDTT"));

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			// 수신한 전문이 null이라면 error
			if("".equals(szRcvTcCode))
			{
				szMsg="[ERROR] "+methodNm+"::"+methodNm+"() TC Code Error (NULL)";
				throw new Exception("TC Code Error.");
			}

			jrParam.setField("STL_NO",	Stl_no);
			JDTORecordSet loadYdStkcol = commDao.select(jrParam, getYdStockJoinStkLyr2, logId, methodNm, "차량정정보검색");

			if (loadYdStkcol.size() <= 0)
			{
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

			jrParam.setField("DEL_YN",			"Y");
			jrParam.setField("MODIFIER",		modifier);
			jrParam.setField("STOCK_MOVE_TERM",	"M2");
			jrParam.setField("STL_NO",			Stl_no);
			commDao.update(jrParam, updateStock2, logId, methodNm, "TB_YF_STOCK 수정");

			//***************************************************************************
			//  저장품이 적치된 저장위치 정보를 조회
			//***************************************************************************
			szMsg="[" + methodNm + "]카드번호["+ydCardNo+"], 차량번호["+ydCardNo+"], 운송지시일자["+transOrdDate+"], 운송지시순번["+transOrdSeqNo+"] : 출하완료된 동["+ydStkColGp+"]의 저장품들 조회 시작";
			commUtils.printLog(logId, szMsg, "SL");

			if("*".equals(stlAppearGp))
			{
				commUtils.printLog(logId, "[" + methodNm + "] 마지막 상차완료 전문", "SL");

				jrParam.setField("CAR_NO",			ydCarNo);
				jrParam.setField("CARD_NO",			ydCardNo);
				jrParam.setField("TRANS_ORD_DATE",	transOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
				JDTORecordSet loadCarsch = commDao.select(jrParam, getYdCarschTransDTSeq2, logId, methodNm, "차량정정보검색");

				if(loadCarsch.size() <= 0 )
				{
					szMsg="["+methodNm+"] 차량스케쥴 조회 SELECT Error ::  DO NOT EXIST"  ;
					commUtils.printLog(logId, szMsg, "SL");

					return jrRtn ;
				}

				jrParam.setField("TC_CODE",			"DMYDR040");	//전문코드
				jrParam.setField("SPOS_WLOC_CD",	commUtils.trim(loadCarsch.getRecord(0).getFieldString("SPOS_WLOC_CD")));
				jrParam.setField("SPOS_YD_PNT_CD",	commUtils.trim(loadCarsch.getRecord(0).getFieldString("YD_PNT_CD1")));
				jrParam.setField("YD_GP",			ydGp);

				if ("".equals(ydCardNo))
				{
					ydCardNo = "XXXXX";
				}

				commUtils.printParam(logId, jrParam);
				//전송 Data 생성
				szMsg= "["+ methodNm +"] 차량번호[" + ydCarNo + "]는 코일제품출하차량출발실적호출";
				commUtils.printLog(logId, szMsg, "SL");

				EJBConnector ejbConn1 = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
				JDTORecord jrRtn1 = (JDTORecord)ejbConn1.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { jrParam });

				jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
			}
			else
			{
				commUtils.printLog(logId, "[" + methodNm + "] 마지막 상차완료 전문이 아님", "SL");
			}

			/********************************************************
			 * 차량 출발후 저장품 정보 갱신
			 * 저장품제원 : 코일야드L2로 송신(YFF1L002)
			 ********************************************************/
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);		//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
			sndL2Msg.setField("YD_INFO_SYNC_CD",	"5");		//야드정보동기화코드
			sndL2Msg.setField("MSG_GP",				"D");		//전문구분
			sndL2Msg.setField("STL_NO",				Stl_no);	//재료번호

			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성

			commUtils.printLog(logId,"[코일제품출하완료][" + Stl_no + "] BACKUP 유무=>:+" + BackUpYn, "SL");


			/*******************************************
			 * layer정보가 존재 하는 경우 (백업으로 취소 처리 시)
			 *******************************************/
			String sYD_STK_LOC = "1X01010101";

			if("Y".equals(BackUpYn))
			{
				jrParam.setField("STL_NO",	Stl_no);
				JDTORecordSet loadStacklayer = commDao.select(jrParam, getStacklayerList2, logId, methodNm, "단정보검색");
				String sPointNo = "";

				if(loadStacklayer.size() > 0)
				{

					sPointNo    = commUtils.trim(loadStacklayer.getRecord(0).getFieldString("YD_STK_COL_GP"));
					sYD_STK_LOC = commUtils.trim(loadStacklayer.getRecord(0).getFieldString("YD_STK_COL_GP"))
					            + commUtils.trim(loadStacklayer.getRecord(0).getFieldString("YD_STK_BED_NO"))
					            + commUtils.trim(loadStacklayer.getRecord(0).getFieldString("YD_STK_LYR_NO"));

					commDao.update(jrParam, updateStacklayer, logId, methodNm, "TB_YF_STKLYR 수정");

					commUtils.printLog(logId, "[코일제품출하완료][" + Stl_no + "]에 저장위치맵을 비웁니다.+" + sPointNo, "SL");
				}

				/***********************************
				 * Coil 공통 Table 저장위치 Update
				 ***********************************/
				if(!"".equals(sPointNo))
				{
					sPointNo = sPointNo.substring(0 , 2);
				}
				else
				{
					sPointNo = ydGp + "X";
				}

				//코일 공통 상차백업 위치로 위치 변경 작업
				JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
				recInTemp1.setResultCode(logId);	//Log ID
				recInTemp1.setResultMsg(methodNm);	//Log Method Name
				recInTemp1.setField("STL_NO",	Stl_no);				//재료번호
				recInTemp1.setField("YD_LOC",	sPointNo + "PT010101");	//현재위치

				EJBConnector ejbConn1 = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				ejbConn1.trx("UpdCoilComLoc", new Class[] { JDTORecord.class }, new Object[] { recInTemp1 });

				/*************************
				 * 작업실적에 저장
				 *************************/
				JDTORecord jparam2 = JDTORecordFactory.getInstance().create();
				jparam2.setField("REGISTER",		sModifier);
				jparam2.setField("MODIFIER",		sModifier);
				jparam2.setField("MOD_DDTT",		sModDdtt);
				jparam2.setField("YD_GP",			YfConstant.YD_GP_1);
				jparam2.setField("STL_NO",			Stl_no);
				jparam2.setField("YD_CRN_SCH_ID",	"000000000000000000");
				jparam2.setField("YD_SCH_CD",		"1X9999");
				jparam2.setField("YD_EQP_ID",		sPointNo.substring(1, 2)+YfConstant.EQUIP_KIND_CR + "00");
				jparam2.setField("YD_WRK_DUTY",		YfCommUtils.getWorkDuty());
				jparam2.setField("YD_WRK_PARTY",	YfCommUtils.getWorkParty());
				jparam2.setField("YD_UP_WO_LOC",	sYD_STK_LOC);
				jparam2.setField("YD_DN_WO_LOC",	sPointNo + "PT010101");
				jparam2.setField("UP_FUNC",			YfConstant.CRANE_FUNC_S);
				jparam2.setField("PUT_FUNC",		YfConstant.CRANE_FUNC_S);
				commDao.insert(jparam2, insertCrnWrslt, logId, methodNm, "작업실적 저장");
			}

			commUtils.printLog(logId, "=============코일제품출하완료 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 오퍼레이션명 : 코일제품반품(DMYDR033 JMS수신)
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR033(JDTORecord rcvMsg)
	{
		String		methodNm	= "코일제품반품[ACoilRcvL3SeEJB.rcvDMYDR033] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

		String		szMsg			= "";
		int			intRtnVal		= 0;
		JDTORecord	jrRtn			= JDTORecordFactory.getInstance().create();
		boolean		isVal			= false;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일제품반품 시작========", "SL");

			//수신 항목 값
			String stlAppearGp 		= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));   //재료외형
			String Stl_no 			= commUtils.trim(rcvMsg.getFieldString("STL_NO"));    		//재료번호
			String distGoodsGp  	= commUtils.trim(rcvMsg.getFieldString("DIST_GOODS_GP"));   //출하제품구분 H:코일, T:HRPLATE
			String oldTransOrdDt  	= commUtils.trim(rcvMsg.getFieldString("OLD_TRANS_WORD_DATE"));  //구운송지시일자
			String oldTransOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("OLD_TRANS_WORD_SEQNO"));
			String newTransOrdDt  	= commUtils.trim(rcvMsg.getFieldString("NEW_TRANS_WORD_DATE"));  //신운송지시일자
			String newTransOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("NEW_TRANS_WORD_SEQNO"));

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"H".equals(distGoodsGp))
			{
				throw new Exception("출하제품구분이상 (H)가 아닙니다...");
			}

			jrParam.setField("DEL_YN",	"N");
			jrParam.setField("STL_NO",	Stl_no);

			String[] rVal = new String[1];
			rVal= commUtils.getYdAimRtGp("C", jrParam);

			jrParam.setField("STOCK_MOVE_TERM", rVal[0]);
			intRtnVal = commDao.update(jrParam, updateStock2, logId, methodNm, "TB_YF_STOCK 수정");

			if(intRtnVal <= 0)
			{
				throw new Exception("TB_YF_STOCK[코일제품반품] UPDATE Error :: [" + intRtnVal + "]");
			}

			jrParam.setField("TRANS_ORD_DATE",	oldTransOrdDt);
			jrParam.setField("TRANS_ORD_SEQNO",	oldTransOrdSeqNo);

			//=====================================================================================================
			// 1.차량스케줄 운송지시 변경
			// 2.검수운송지시 변경 및 재검수 상태로 변경
			// 3.저장품 운송지시 변경
			//=====================================================================================================
			JDTORecordSet jsStock = commDao.select(jrParam, getYdStockTRANS_ORD_DAT, logId, methodNm, "(운송일자, 운송순번)로 저장품 조회");

		    if(jsStock.size() > 0)
		    {
		    	commUtils.printLog(logId, methodNm+ "[코일제품반품(DMYDR033)] 이전 운송지시번호로 변경 대상이 존재 함", "SL");

		    	//--------------------------------------------------------------------------------
				//	차량스케줄 운송지시 변경
				//--------------------------------------------------------------------------------
				jrParam.setField("OLD_TRANS_WORD_DATE",		oldTransOrdDt);
				jrParam.setField("OLD_TRANS_WORD_SEQNO",	oldTransOrdSeqNo);
				jrParam.setField("NEW_TRANS_WORD_DATE",		newTransOrdDt);
				jrParam.setField("NEW_TRANS_WORD_SEQNO",	newTransOrdSeqNo);
				intRtnVal = commDao.update(jrParam, updYdCarschTransOrd, logId, methodNm, "TB_YD_CARSCH 차량스케줄 운송지시 변경");

				//--------------------------------------------------------------------------------
				//	검수재료 운송지시 변경
				//--------------------------------------------------------------------------------
				intRtnVal = commDao.update(jrParam, updYdExamTransOrd, logId, methodNm, "TB_YD_EXAMINATIONCHKLIST 검수재료 운송지시 변경");

				//--------------------------------------------------------------------------------
				//	재료정보 운송지시 변경
				//--------------------------------------------------------------------------------
				intRtnVal = commDao.update(jrParam, updYfStockTransOrd, logId, methodNm, "TB_YF_STOCK 재료정보 운송지시 변경");
			}
			//----------------------------------------------------------------------------

			//차량스케줄ID 조회--------------------------------------------------------------
			String ydCarSchId = "";
			JDTORecordSet jsCarsch = commDao.select(jrParam, getYdCarschByTransDTSeq, logId, methodNm, "(운송일자, 운송순번)로 TB_YD_CARSCH 조회");

			if(jsCarsch.size() <= 0 )
			{
				szMsg = "["+methodNm+"] 운송지시일자 :["+newTransOrdDt+"] , 운송지시순번["+newTransOrdSeqNo+"]로 차량스케줄 조회 시 오류발생 - 메세지 : " + jsCarsch.size();
				commUtils.printLog(logId, szMsg, "SL");
				return jrRtn;
			}
			else
			{
				ydCarSchId = commUtils.trim(jsCarsch.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
			}
			//--------------------------------------------------------------------------------

			//1.차량스케줄 재료 삭제--------------------------------------------------------------
			JDTORecord recPara =  JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", 	ydCarSchId);
			recPara.setField("STL_NO", 			Stl_no);
			commDao.update(jrParam, deleteYdCarftmvmtl, logId, methodNm, "TB_YD_CARFTMVMTL 삭제");

			//2.검수재료 삭제--------------------------------------------------------------------
			recPara.setField("TRANS_ORD_DATE",	newTransOrdDt);
			recPara.setField("TRANS_ORD_SEQNO",	newTransOrdSeqNo);
			commDao.update(jrParam, deleteYdExaminationmtl, logId, methodNm, "TB_YD_EXAMINATIONCHKLIST 삭제");

			//3.저장품재료 --------------------------------------------------------------------
			jrParam.setField("TRANS_ORD_DATE",	"");
			jrParam.setField("TRANS_ORD_SEQNO",	"");
			jrParam.setField("CAR_NO",			"");
			jrParam.setField("CAR_CARD_NO",		"");
			jrParam.setField("STL_NO",			Stl_no);
			intRtnVal =	commDao.update(jrParam, updYfStock, logId, methodNm, "TB_YF_STOCK 수정");

			//======================================================
			// 저장품제원 : 코일야드L2 로 송신(YFF1L002)
			//======================================================
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);	//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
			sndL2Msg.setField("YD_INFO_SYNC_CD",	"5");		//야드정보동기화코드
			sndL2Msg.setField("MSG_GP",				"I");		//전문구분
			sndL2Msg.setField("STL_NO",				Stl_no);	//재료번호

			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성	//차후 수정해야함

			commUtils.printLog(logId, "=============코일제품반품 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 오퍼레이션명 : 코일제품출하차량도착실적(DMYDR036 JMS수신)...물류시스템팀 박주환 엔지니어에게 문의 결과 사용안함
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 */
//	public boolean rcvDMYDR036(JDTORecord rcvMsg)
//	{
//		boolean			isVal		= false;
//		String			methodNm	= "코일제품출하차량도착실적[ACoilRcvL3SeEJB.rcvDMYDR036] < " + rcvMsg.getResultMsg();
//		String			logId		= rcvMsg.getResultCode();
//	    JDTORecord		jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
//
//		try
//		{
//			commUtils.printLog(logId, methodNm, "S+");
//
//			//기본 수신 항목 값
//			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
//			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
//
//			if ("".equals(modifier))
//			{
//				modifier = msgId;
//			}
//
//			jrParam.setResultCode(logId);	//Log ID
//			jrParam.setResultMsg(methodNm);	//Log Method Name
//			jrParam.setField("MODIFIER", modifier); //수정자 셋팅
//
//			commUtils.printLog(logId, "=============코일제품출하차량도착실적 시작========", "SL");
//
//			//수신 항목 값
//			String cardNo	 		= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
//			String SPOS_WLOC_CD		= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"));	//발지개소코드
//			String SPOS_YD_PNT_CD 	= commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"));	//발지야드포인트코드
//
//			//======================================================
//	    	// 적치열 정보 조회
//	    	//======================================================
//	    	JDTORecord jReocd = commDao.readStackCol(SPOS_WLOC_CD, SPOS_YD_PNT_CD);
//
//	    	String pos = jReocd.getFieldString("YD_STK_COL_GP");	//적치 열 구분
//
//	    	//======================================================
//	    	// 차량도착 정보를 처리한다.
//	    	//======================================================
//			EJBConnector ejbCon = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
//			boolean chk = ((Boolean)ejbCon.trx("carArrival", new Class[]{ String.class, String.class, String.class}, new Object[]{ "Y", cardNo, pos })).booleanValue();
//
//		    if(!chk)
//		    {
//		    	return false;
//		    }
//
//		    commUtils.printLog(logId, "=============코일제품출하차량도착실적 종료========", "SL");
//
//			commUtils.printLog(logId, methodNm, "S-");
//
//			isVal = true;
//		}
//		catch (DAOException e)
//		{
//			throw e;
//		}
//		catch (Exception e)
//		{
//			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
//		}
//
//		return isVal;
//	}

	/**
	 * 오퍼레이션명 : 코일제품출하차량도착실적(DMYDR037 JMS수신)...물류시스템팀 박주환 엔지니어에게 문의 결과 사용안함
     * 오리지널소스 : CarMvHdSeEJBBean.procCoilRentprocCarArrWr()
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 */
//	public boolean rcvDMYDR037(JDTORecord rcvMsg)
//	{
//		String			methodNm	= "코일임가공차량도착실적처리[ACoilRcvL3SeEJB.rcvDMYDR037] < " + rcvMsg.getResultMsg();
//		String			logId		= rcvMsg.getResultCode();
//	    JDTORecord		jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
//
//	    JDTORecordSet	rsResult	= null;
//	    boolean			isVal		= false;
//
//		try
//		{
//			commUtils.printLog(logId, methodNm, "S+");
//
//			//기본 수신 항목 값
//			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
//			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
//
//			if ("".equals(modifier))
//			{
//				modifier = msgId;
//			}
//
//			jrParam.setResultCode(logId);	//Log ID
//			jrParam.setResultMsg(methodNm);	//Log Method Name
//			jrParam.setField("MODIFIER", modifier); //수정자 셋팅
//
//			commUtils.printLog(logId, "=============코일임가공차량도착실적처리 시작========", "SL");
//
//			//수신 항목 값
//		    String szTRANS_ORD_DT			= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));			//운송지시일자
//		    String szTRANS_ORD_SEQNO		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));			//운송지시순번
//		    String szCAR_NO					= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));					//차량번호
//		    String szCARD_NO				= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));					//카드번호
//		    String szSPOS_WLOC_CD			= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"));			//발지개소코드
//		    String szSPOS_YD_PNT_CD			= commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"));			//발지야드포인트코드
//		    String szRENTPROC_CD			= commUtils.trim(rcvMsg.getFieldString("RENTPROC_CD"));				//임가공사코드
//		    String szYD_EQP_GP				= commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"));				//야드설비구분
//		    String szYD_WRK_ALW_L			= commUtils.trim(rcvMsg.getFieldString("YD_WRK_ALW_L"));			//야드작업허용길이
//		    String szYD_WRK_ALW_W			= commUtils.trim(rcvMsg.getFieldString("YD_WRK_ALW_W"));			//야드작업허용폭
//		    String szYD_WRK_ALW_SKID_PITCH	= commUtils.trim(rcvMsg.getFieldString("YD_WRK_ALW_SKID_PITCH"));	//야드작업허용Skid간격
//		    String szYD_WRK_ALW_SH			= commUtils.trim(rcvMsg.getFieldString("YD_WRK_ALW_SH"));			//야드작업허용매수
//		    String szYD_WRK_ALW_WT			= commUtils.trim(rcvMsg.getFieldString("YD_WRK_ALW_WT"));			//야드작업허용중량
//		    String szIS_EJB_CALL			= commUtils.trim(rcvMsg.getFieldString("IS_EJB_CALL"));				//연계모듈 호출 유무 변수(EJB:'Y', JMS:'N' or '')
//
//		    String szYD_CARLD_STOP_LOC		= "";	//적치열구분
//
//			if("".equals(szTRANS_ORD_DT))
//			{
//				commUtils.printLog(logId, methodNm + "[전문 이상] 운송지시일자가 없습니다.", "SL");
//				return false;
//			}
//
//			if("".equals(szTRANS_ORD_SEQNO))
//			{
//				commUtils.printLog(logId, methodNm + "[전문 이상] 운송지시순번이 없습니다.", "SL");
//				return false;
//			}
//
//			if("".equals(szCAR_NO))
//			{
//				commUtils.printLog(logId, methodNm + "[전문 이상] 차량번호가 없습니다.", "SL");
//				return false;
//			}
//
//			if("".equals(szCARD_NO))
//			{
//				commUtils.printLog(logId, methodNm + "[전문 이상] 카드번호가 없습니다.", "SL");
//				return false;
//			}
//
//			if("".equals(szSPOS_WLOC_CD))
//			{
//				commUtils.printLog(logId, methodNm + "[전문 이상] 발지개소코드가 없습니다.", "SL");
//				return false;
//			}
//
//			if("".equals(szSPOS_YD_PNT_CD))
//			{
//				commUtils.printLog(logId, methodNm + "[전문 이상] 발지야드포인트 코드가 없습니다.", "SL");
//				return false;
//			}
//
//			if("".equals(szRENTPROC_CD))
//			{
//				commUtils.printLog(logId, methodNm + "[전문 이상] 임가공사코드가 없습니다.", "SL");
//				return false;
//			}
//
//			if("".equals(szYD_EQP_GP))
//			{
//				commUtils.printLog(logId, methodNm + "[전문 이상] 야드설비구분이 없습니다.", "SL");
//				return false;
//			}
//
//			if("".equals(szYD_WRK_ALW_L))
//			{
//				commUtils.printLog(logId, methodNm + "[전문 이상] 야드작업허용길이가 없습니다.", "SL");
//				return false;
//			}
//
//			if("".equals(szYD_WRK_ALW_W))
//			{
//				commUtils.printLog(logId, methodNm + "[전문 이상] 야드작업허용폭이 없습니다.", "SL");
//				return false;
//			}
//
//			if("".equals(szYD_WRK_ALW_SKID_PITCH))
//			{
//				commUtils.printLog(logId, methodNm + "[전문 이상] 야드작업허용Skid간격이 없습니다.", "SL");
//				return false;
//			}
//
//			if("".equals(szYD_WRK_ALW_SH))
//			{
//				commUtils.printLog(logId, methodNm + "[전문 이상] 야드작업허용매수가 없습니다.", "SL");
//				return false;
//			}
//
//			if("".equals(szYD_WRK_ALW_WT))
//			{
//				commUtils.printLog(logId, methodNm + "[전문 이상] 야드작업허용중량이 없습니다.", "SL");
//				return false;
//			}
//
//			jrParam.setField("WLOC_CD",		szSPOS_WLOC_CD);
//			jrParam.setField("YD_PNT_CD",	szSPOS_YD_PNT_CD);
//			jrParam.setField("SECT_GP",		"PT");	//PT로 통합함
//			rsResult = commDao.select(jrParam, getYfStkcolWlocCdPntCdEqpGp, logId, methodNm, "해당 개소코드와 포인트코드로 적치열 조회");
//
//			if (rsResult.size() <= 0)
//			{
//				commUtils.printLog(logId, methodNm + "해당 개소코드와 포인트코드로 적치열 조회 실패! 개소코드(WLOC_CD) :" + szSPOS_WLOC_CD + " / 포인트코드(YD_PNT_CD) : " + szSPOS_YD_PNT_CD, "SL");
//				throw new DAOException("해당 개소코드와 포인트코드로 적치열 조회 실패!");
//			}
//			else
//			{
//				//적치열구분을 조회 (도착지)
//				szYD_CARLD_STOP_LOC = YfCommUtils.fillSpZr(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"), 6, 1).trim();
//			}
//
//
//		    commUtils.printLog(logId, "=============코일임가공차량도착실적처리 종료========", "SL");
//
//			commUtils.printLog(logId, methodNm, "S-");
//
//			isVal = true;
//		}
//		catch (DAOException e)
//		{
//			throw e;
//		}
//		catch (Exception e)
//		{
//			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
//		}
//
//		return isVal;
//	}

	/**
	 * 오퍼레이션명 : 코일제품출하차량출발실적(DMYDR040 JMS수신)...물류시스템팀 박주환 엔지니어에게 문의 결과 사용안함
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 */
//	public boolean rcvDMYDR040(JDTORecord rcvMsg)
//	{
//		boolean			isVal		= false;
//		String			methodNm	= "코일제품출하차량출발실적[ACoilRcvL3SeEJB.rcvDMYDR040] < " + rcvMsg.getResultMsg();
//		String			logId		= rcvMsg.getResultCode();
//	    JDTORecord		jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
//
//		try
//		{
//			commUtils.printLog(logId, methodNm, "S+");
//
//			//기본 수신 항목 값
//			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
//			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
//
//			if ("".equals(modifier))
//			{
//				modifier = msgId;
//			}
//
//			jrParam.setResultCode(logId);	//Log ID
//			jrParam.setResultMsg(methodNm);	//Log Method Name
//			jrParam.setField("MODIFIER", modifier); //수정자 셋팅
//
//			commUtils.printLog(logId, "=============코일제품출하차량출발실적 시작========", "SL");
//
//			//수신 항목 값
//			String cardNo	 		= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
//			String SPOS_WLOC_CD 	= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"));	//발지개소코드
//			String SPOS_YD_PNT_CD 	= commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"));	//발지야드포인트코드
//
//	    	//======================================================
//	    	// 적치열 정보 조회
//	    	//======================================================
//	    	JDTORecord jReocd = commDao.readStackCol(SPOS_WLOC_CD, SPOS_YD_PNT_CD);
//
//	    	String pos = jReocd.getFieldString("YD_STK_COL_GP");	//적치 열 구분
//
//	    	/**
//	    	 * 차량 출발 지시를 처리한다.
//	    	 */
//	    	//======================================================
//	    	// 차량 출발 지시를 처리한다.
//	    	//======================================================
//			EJBConnector ejbCon = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
//			boolean chk = ((Boolean)ejbCon.trx("carStartOrder", new Class[]{ String.class, String.class, String.class}, new Object[]{ "", cardNo, pos })).booleanValue();
//
//		    if(!chk)
//		    {
//		    	return false;
//		    }
//
//		    commUtils.printLog(logId, "=============코일제품출하차량출발실적 종료========", "SL");
//
//			commUtils.printLog(logId, methodNm, "S-");
//
//			isVal = true;
//		}
//		catch (DAOException e)
//		{
//			throw e;
//		}
//		catch (Exception e)
//		{
//			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
//		}
//
//		return isVal;
//	}

	/**
	 * 오퍼레이션명 : 코일임가공차량출발실적(DMYDR041 JMS수신)...물류시스템팀 박주환 엔지니어에게 문의 결과 사용안함
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 */
//	public boolean rcvDMYDR041(JDTORecord rcvMsg)
//	{
//		boolean			isVal		= false;
//		String			methodNm	= "코일임가공차량출발실적[ACoilRcvL3SeEJB.rcvDMYDR041] < " + rcvMsg.getResultMsg();
//		String			logId		= rcvMsg.getResultCode();
//	    JDTORecord		jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
//
//		try
//		{
//			commUtils.printLog(logId, methodNm, "S+");
//
//			//기본 수신 항목 값
//			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
//			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
//
//			if ("".equals(modifier))
//			{
//				modifier = msgId;
//			}
//
//			jrParam.setResultCode(logId);	//Log ID
//			jrParam.setResultMsg(methodNm);	//Log Method Name
//			jrParam.setField("MODIFIER", modifier); //수정자 셋팅
//
//			commUtils.printLog(logId, "=============코일임가공차량출발실적 시작========", "SL");
//
//			EJBConnector ejbCon = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
//			isVal = ((Boolean)ejbCon.trx("procOutCarLevWrAB", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg })).booleanValue();
//
//			commUtils.printLog(logId, "=============코일임가공차량출발실적 종료========", "SL");
//
//			commUtils.printLog(logId, methodNm, "S-");
//		}
//		catch (DAOException e)
//		{
//			throw e;
//		}
//		catch (Exception e)
//		{
//			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
//		}
//
//		return isVal;
//	}

	/**
	 * 오퍼레이션명 : 코일제품운송상차지시(DMYDR060 JMS수신)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR060(JDTORecord rcvMsg)
	{
		String			methodNm	= "코일제품운송상차지시 수신[ACoilRcvL3SeEJB.rcvDMYDR060] < " + rcvMsg.getResultMsg();
		String			logId		= rcvMsg.getResultCode();
		JDTORecord		jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

		String 			tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
		JDTORecord 		jrRtn		= JDTORecordFactory.getInstance().create();
		JDTORecordSet	rsResult	= null;
		boolean			isVal		= false;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일제품운송상차지시 수신 시작========", "SL");

			//수신 항목 값
			String szCMBN_CARLD_YN	 	= commUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN"));	//조합상차유무(시작:S,종료:E,단일상차:N)
			String szTRANS_ORD_DT	 	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));	//운송지시일자
			String szTRANS_ORD_SEQNO	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));	//운송지시순번
			String szCAR_NO 		 	= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));			//차량번호
			String szCARD_NO 		 	= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));			//카드번호
			String szLOT_NO 		 	= commUtils.trim(rcvMsg.getFieldString("LOT_NO"));			//LOT번호
			String szCAR_KIND 		 	= commUtils.trim(rcvMsg.getFieldString("CAR_KIND"));		//차량종류

			int szYD_EQP_WRK_SH		 	= Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수

			String szYD_GP			 	= "";
			String szSTL_NO;
			String szGDS_CARLD_LOC;
			String szSTOCK_MOVE_TERM;

			YdStockDao	ydStockDao		= new YdStockDao();

			String[]	rVal			= new String[1];

			String		szYD_AIM_RT_GP	= "";

			//======================================================
			// 수신된 전문의 STL_NO의 수 만큼 Loop
			//======================================================
			for(int i = 1 ; i <= szYD_EQP_WRK_SH; i++)
			{
				szYD_GP			= commUtils.trim(rcvMsg.getFieldString("YD_GP" + i));			//야드구분
				szSTL_NO		= commUtils.trim(rcvMsg.getFieldString("STL_NO" + i)); 			//재료번호
				szGDS_CARLD_LOC	= commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC" + i));	//차상위치

				if ("S".equals(szCMBN_CARLD_YN) && "TR".equals(szCAR_KIND) && "".equals(szGDS_CARLD_LOC))
				{
					szGDS_CARLD_LOC = "0" + i;
				}

				if ("".equals(szSTL_NO))
				{
					break;
				}

				if (YfConstant.YD_GP_1.equals(szYD_GP))
				{
					//1:A열연 COIL야드

					//======================================================
					// 저장품 이동 조건(STOCK_MOVE_TERM) 생성
					//======================================================
					jrParam.setField("TC_CD",	tcCode);	//TC_CODE
					jrParam.setField("STL_NO",	szSTL_NO);	//저장품 ID
					szSTOCK_MOVE_TERM = commUtils.trim(yfComm.getCoilCurrProgCd(jrParam).getFieldString("STOCK_MOVE_TERM"));

					//======================================================
					// TB_YF_STOCK 수정
					//======================================================
					jrParam.setField("STOCK_MOVE_TERM",		szSTOCK_MOVE_TERM);
					jrParam.setField("YD_RULE_PL_RS_GP",	szCMBN_CARLD_YN);
					jrParam.setField("TRANS_ORD_DATE",		szTRANS_ORD_DT);
					jrParam.setField("TRANS_ORD_SEQNO",		szTRANS_ORD_SEQNO);
					jrParam.setField("YD_CAR_UPP_LOC_CD",	szGDS_CARLD_LOC);
					jrParam.setField("CAR_NO",				szCAR_NO);
					jrParam.setField("CAR_CARD_NO",			szCARD_NO);
					jrParam.setField("STL_NO",				szSTL_NO);

					commDao.update(jrParam, updYfStock, logId, methodNm, "TB_YF_STOCK 수정");
				}
				else if(YfConstant.YD_GP_2.equals(szYD_GP) || YfConstant.YD_GP_3.equals(szYD_GP))
				{
					//2:B열연 SLAB야드,3:B열연 COIL야드

					//======================================================
					// 저장품 이동 조건(STOCK_MOVE_TERM) 생성
					//======================================================
					jrParam.setField("TC_CD",	tcCode);	//TC_CODE
					jrParam.setField("STL_NO",	szSTL_NO);	//저장품 ID
					szSTOCK_MOVE_TERM = commUtils.trim(yfComm.getCoilCurrProgCd(jrParam).getFieldString("STOCK_MOVE_TERM"));

					//TB_YM_STOCK 수정
					jrParam.setField("STOCK_MOVE_TERM"	, szSTOCK_MOVE_TERM);
					jrParam.setField("YD_RULE_PL_RS_GP"	, szCMBN_CARLD_YN);
					jrParam.setField("TRANS_ORD_DATE2"	, szTRANS_ORD_DT);
					jrParam.setField("TRANS_ORD_SEQNO2"	, szTRANS_ORD_SEQNO);
					jrParam.setField("SHEAR_SUPPLY_SEQ"	, szGDS_CARLD_LOC);
					jrParam.setField("CAR_NO2"			, szCAR_NO);
					jrParam.setField("CAR_CARD_NO"		, szCARD_NO);
					jrParam.setField("SHEAR_SUPPLY_GP"	, szCAR_KIND);
					jrParam.setField("STOCK_ID"			, szSTL_NO);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStock", logId, methodNm, "TB_YM_STOCK 수정");
				}
				else
				{
					//일관제철 야드 - RtModRegSeEJB.procCoilGdsTrnOrdNEW 와 동일함

					//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
					jrParam.setField("TC_CODE",	msgId);		//TC_CODE
					jrParam.setField("STL_NO",	szSTL_NO);	//저장품 ID
					rVal	= YdCommonUtils.getYdAimRtGp("C", jrParam);

					szYD_AIM_RT_GP	= rVal[0]; //야드목표행선

					//TB_YD_STOCK 수정
					jrParam.setField("STL_NO",				szSTL_NO);
					jrParam.setField("YD_CAR_UPP_LOC_CD",	szGDS_CARLD_LOC);
					jrParam.setField("TRANS_ORD_DATE",		szTRANS_ORD_DT);
					jrParam.setField("TRANS_ORD_SEQNO",		szTRANS_ORD_SEQNO);
					jrParam.setField("YD_AIM_RT_GP",		szYD_AIM_RT_GP);
					jrParam.setField("CAR_NO",				szCAR_NO);
					jrParam.setField("CARD_NO",				szCARD_NO);
					jrParam.setField("YD_RULE_PL_RS_GP",	szCMBN_CARLD_YN);
					jrParam.setField("CAR_LOTID",			szLOT_NO);
					jrParam.setField("YD_STK_BED_NO",		szCAR_KIND);
					ydStockDao.updYdStock(jrParam);
				}

			} // end of for loop

			//야드핸들링정보 송신////////////////////////////////////////////////////////////////////////////////
			//코일야드 인 경우 우선 적용
			if (YfConstant.YD_GP_1.equals(szYD_GP) || YfConstant.YD_GP_3.equals(szYD_GP) || YfConstant.YD_GP_H.equals(szYD_GP) || YfConstant.YD_GP_J.equals(szYD_GP))
			{
				//1:A열연 COIL야드,3:B열연 COIL야드,H:C열연 COIL소재야드,J:C열연 COIL제품야드

				//======================================================
				// 출하제품핸들링횟수 구하기
				//======================================================
				jrParam.setField("TRANS_ORD_DATE",	szTRANS_ORD_DT);
				jrParam.setField("TRANS_ORD_SEQNO",	szTRANS_ORD_SEQNO);

				rsResult = commDao.select(jrParam, getHandlingCnt, logId, methodNm, "출하Handling 갯수 구하기");

				for(int i = 0; i < rsResult.size() ; i++)
				{
					jrParam.setField("MSG_ID",					"YDDMR050");
					jrParam.setField("YD_GP",					commUtils.trim(rsResult.getRecord(i).getFieldString("YD_GP")));
					jrParam.setField("TRANS_ORD_DT",			szTRANS_ORD_DT);
					jrParam.setField("TRANS_ORD_SEQNO",			szTRANS_ORD_SEQNO);
					jrParam.setField("CMBN_CARLD_YN",			szCMBN_CARLD_YN);
					jrParam.setField("CARLD_PNT_CD",			commUtils.trim(rsResult.getRecord(i).getFieldString("CARLD_PNT_CD")));
					jrParam.setField("CAR_NO",					szCAR_NO);
					jrParam.setField("HANDLING_CNT",			commUtils.trim(rsResult.getRecord(i).getFieldString("HANDLING_CNT")));
					jrParam.setField("YD_STK_BED_WHIO_STAT",	"");

					//전송 Data 생성
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR050", jrParam)); //야드핸들링정보
				}
			}

			commUtils.printLog(logId, "=============코일제품운송상차지시 수신 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 대기장도착실적(DMYDR061)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR061(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "대기장도착실적 수신[ACoilRevL3SeEJB.rcvDMYDR061] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			jrRtn = commUtils.addSndData(jrRtn, this.procDMYDR061(rcvMsg));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 대기장도착실적(DMYDR061)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procDMYDR061(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "대기장도착실적 수신[ACoilRevL3SeEJB.procDMYDR061] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		JDTORecordSet rsResult 	= null;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============대기장도착실적 수신 시작========", "SL");

			//수신 항목 값
			String szYD_GP 				= commUtils.trim(rcvMsg.getFieldString("YD_GP"));				//야드구분
			String szCMBN_CARLD_YN 		= commUtils.nvl(rcvMsg.getFieldString("CMBN_CARLD_YN"),"N");	//조합상차유무(시작:S, 종료:E, 단일상차:N)
			String szWORK_GP 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP"));				//작업구분
			String szTEL_NO 			= commUtils.trim(rcvMsg.getFieldString("TEL_NO"));				//전화번호
			String szTRANS_ORD_DT  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));		//운송지시일자
			String szTRANS_ORD_SEQNO	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));		//운송지시순번
			String szCAR_NO 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));				//차량번호
			String szCARD_NO 			= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));				//카드번호
			String szCAR_KIND 			= commUtils.nvl(rcvMsg.getFieldString("CAR_KIND"),"TR");		//차량종류
			String szWAIT_ARR_DDTT		= commUtils.trim(rcvMsg.getFieldString("WAIT_ARR_DDTT"));		//대기장도착시간
			String szWAIT_ARR_GP		= commUtils.trim(rcvMsg.getFieldString("WAIT_ARR_GP"));			//대기장도착구분
			String szTRANS_FRTOMOVE_GP	= commUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP"));	//1 운송 2 이송
			String szDRIVER_NAME		= commUtils.trim(rcvMsg.getFieldString("DRIVER_NAME"));			//운전기사명

			//차량정보 존재여부 체크
			jrParam.setField("TRANS_ORD_DT",	szTRANS_ORD_DT);	//운송지시일자
			jrParam.setField("TRANS_ORD_SEQNO",	szTRANS_ORD_SEQNO);	//운송지시순번
			jrParam.setField("CARD_NO",			szCARD_NO);			//카드번호
			jrParam.setField("CMBN_CARLD_YN",	szCMBN_CARLD_YN);	//조합상차유무
			rsResult = commDao.select(jrParam, getYdCarYdCmbnCarldYn, logId, methodNm, "차량정보 존재여부 체크");

			if (rsResult.size() > 0)
			{
				commUtils.printLog(logId, methodNm + " TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.:기존지시 삭제 " , "SL");

				//기존 차량정보 삭제처리
				jrParam.setField("YD_CAR_SCH_ID",	commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID")));
				jrParam.setField("DEL_YN",			"Y");
				commDao.update(jrParam, updYdCarsch, logId, methodNm, "TB_YD_CARSCH 기존 차량스케줄 정보 삭제처리");
			}

			//도착가능 포인트 조회
			jrParam.setField("YD_GP",			szYD_GP);
			jrParam.setField("TRANS_ORD_DT",	szTRANS_ORD_DT);
			jrParam.setField("TRANS_ORD_SEQNO",	szTRANS_ORD_SEQNO);
			rsResult = commDao.select(jrParam, getYdCarPointSelect, logId, methodNm, "도착가능 차량포인트 조회");

			if (rsResult.size() <= 0 )
			{
				//commUtils.printLog(logId, methodNm + " TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다. " , "SL");
				//m_ctx.setRollbackOnly();
				throw new Exception("TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다....");
			}

			//도착가능 포인트 조회 결과 값
			String szYD_STK_COL_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));	//야드적치열
			String szYD_CARPNT_CD 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CARPNT_CD"));		//차량포인트
			String szYD_PNT_CD 		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));		//야드포인트코드
			String szSPOS_WLOC_CD	= commUtils.trim(rsResult.getRecord(0).getFieldString("WLOC_CD"));			//개소코드

			commUtils.printLog(logId, methodNm + " 도착가능 포인트 결과 적치열 : " + szYD_STK_COL_GP + " / 차량포인트 : " + szYD_CARPNT_CD + " / 야드포인트코드 : " + szYD_PNT_CD + " / 개소코드 : " + szSPOS_WLOC_CD , "SL");

			//차량스케줄ID 생성
			String szYD_CAR_SCH_ID = commDao.getSeqId(logId, methodNm, "CarSch");

			//차량스케줄 등록
			jrParam.setField("YD_CAR_SCH_ID",		szYD_CAR_SCH_ID);
			jrParam.setField("REGISTER",			modifier);
			jrParam.setField("YD_EQP_WRK_STAT",		"U");							//야드설비작업상태
			jrParam.setField("YD_EQP_ID",			YfConstant.YD_DM_CAR_EQP_ID);	//야드설비ID
			jrParam.setField("YD_CAR_USE_GP",		YfConstant.YD_CAR_USE_GP_DM);	//차량사용구분
			jrParam.setField("CAR_NO",				szCAR_NO);						//차량번호
			jrParam.setField("CAR_KIND",			szCAR_KIND);					//차량종류
			jrParam.setField("SPOS_WLOC_CD",		szSPOS_WLOC_CD);				//발지개소코드
			jrParam.setField("CARD_NO",				szCARD_NO);						//카드번호
			jrParam.setField("YD_CARLD_LEV_DT",		commUtils.getDateTime14());		//상차출발일시
			jrParam.setField("YD_PNT_CD1",			szYD_PNT_CD);					//야드포인트코드1
			jrParam.setField("YD_CARLD_STOP_LOC",	szYD_STK_COL_GP);				//야드상차정지위치
			jrParam.setField("TRANS_ORD_DATE",		szTRANS_ORD_DT);				//운송지시일자
			jrParam.setField("TRANS_ORD_SEQNO",		szTRANS_ORD_SEQNO);				//운송지시순번

			if ("E".equals(szCMBN_CARLD_YN))
			{
				jrParam.setField("YD_BAYIN_WO_SEQ",	"1");							//입동지시순번 - 복수상차 마지막 1순위
			}
			else
			{
				jrParam.setField("YD_BAYIN_WO_SEQ",	YfConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 - 기본값으로 설정(9)
			}

			jrParam.setField("YD_CAR_PROG_STAT",	"1");							//상차출발상태
			jrParam.setField("YD_CAR_WRK_GP",		szWORK_GP);						//야드차량작업구분
			jrParam.setField("TEL_NO",				szTEL_NO);						//기사핸드폰번호
			jrParam.setField("CMBN_CARLD_YN",		szCMBN_CARLD_YN);				//첫번째 도착창고 : S 두번째 도착창고 : E
			jrParam.setField("WAIT_ARR_DDTT",		szWAIT_ARR_DDTT);				//대기장도착시간
			jrParam.setField("WAIT_ARR_GP",			szWAIT_ARR_GP);					//대기장도착구분  - B:BACKUP , S:SMARTPHONE
			jrParam.setField("DRIVER_NAME",			szDRIVER_NAME);					//운전기사명
			commDao.insert(jrParam, insYdCarsch, logId, methodNm, "TB_YD_CARSCH 등록");

			//2020.07.09 배차차량관리 화면 복수동인경우 마지막이 아닌 세번째 이후 입동되는곳에서 대상/실적 중에 실적이 0으로 나오는경우가 있어서 주석처리함
			//if("E".equals(szCMBN_CARLD_YN))
			//{
				//복수동 마지막 도착시 상차된 정보 INSERT
				//이송작업재료등록
				jrParam.setField("MODIFIER",	modifier);
				commDao.insert(jrParam, updCarFtMvMtlCmbnCarldYn, logId, methodNm, "TB_YD_CARFTMVMTL 복수동이송작업재료등록");
			//}

			//입동지시 호출
			if (!"".equals(szYD_CARPNT_CD))
			{
				//도착가능 포인트가 있으면 입동지시 호출
				commUtils.printLog(logId, methodNm + " 차량입동포인트["+szYD_CARPNT_CD+"], 차량스케줄ID["+szYD_CAR_SCH_ID+"] - 차량입동지시요구 모듈을 호출 " , "SL");

				JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);		//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("JMS_TC_CD",				"YFYFJ662");				//차량입동지시
				recInTemp.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14());	//JMSTC생성일시
				recInTemp.setField("YD_CARPNT_CD",			szYD_CARPNT_CD);			//입동포인트
				recInTemp.setField("YD_CAR_SCH_ID",			szYD_CAR_SCH_ID);			//차량스케줄ID
				recInTemp.setField("CARD_NO",				szCARD_NO);
				recInTemp.setField("CAR_NO",				szCAR_NO);
				recInTemp.setField("CAR_KIND",				szCAR_KIND);				//차량종류
				recInTemp.setField("TRANS_FRTOMOVE_GP",		szTRANS_FRTOMOVE_GP);		//1 운송 2 이송

				//JMS 전송
				jrRtn = commUtils.addSndData(jrRtn, recInTemp);
			}

			commUtils.printLog(logId, "=============대기장도착실적 수신 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 코일이송상차대기장도착PDA(DMYDR070)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR070(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "코일이송상차대기장도착PDA 수신[ACoilRevL3SeEJB.rcvDMYDR070] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		int			intRtnVal	= 0;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일이송상차대기장도착PDA 수신 시작========", "SL");

			//수신 항목 값
			String stlAppearGp 		= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));    //재료외형
			String transOrdDt  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			String transOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String cancelYn     	= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));
			String ydCarKind		= commUtils.trim(rcvMsg.getFieldString("CAR_KIND"));
			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
			String ydCardNo 		= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
			String crFrtomoveGp		= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP")); 	//냉연이송구분
			String WorkGp 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP"));   		//작업구분
			String CarldPntCd		= commUtils.trim(rcvMsg.getFieldString("CARLD_PNT_CD"));   	//상차포인트
			String szMOV_YN			= commUtils.trim(rcvMsg.getFieldString("UGNT_BAYIN_YN"));	//복수상차 마지막 차량에 대한 구분 Y: 1순위

			int ydEqpWrkSh 			= Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0"));

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/

			if (("Y".equals(cancelYn)))
			{
				return jrRtn;
			}
			else
			{
			   /**********************************************************
				* 2. 저장품 이동 조건 수정
				**********************************************************/
				jrParam.setField("TRANS_ORD_DATE",	transOrdDt);
				jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
				jrParam.setField("CAR_NO",			ydCarNo);
				jrParam.setField("CAR_CARD_NO",		ydCardNo);
				jrParam.setField("CR_FRTOMOVE_GP",	crFrtomoveGp);
				jrParam.setField("MODIFIER",		modifier);

				for(int i = 1 ; i<= ydEqpWrkSh; i++)
				{
					jrParam.setField("STL_NO",	commUtils.trim(rcvMsg.getFieldString("STL_NO"+i))); //저장품 ID
					jrParam.setField("TC_CD",	tcCode);  //TC_CODE

					JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);

					jrParam.setField("STOCK_MOVE_TERM",		commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));
					jrParam.setField("YD_CAR_UPP_LOC_CD",	commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i)));	//차상위치
					intRtnVal=	commDao.update(jrParam, updYfStock, logId, methodNm, "TB_YF_STOCK 수정");

		            if(intRtnVal == 0)
		            {
		           	   continue;
		           	   //throw new Exception("수신한 재료번호 ["+ StStlNo+"]에 대한 저장품 DATA가 존재하지 않음");
					}
				}

				//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
				if("9".equals(WorkGp))
				{
					//차량정보 존재여부 체크 /////////////////////////////////////////////////////////////////
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("MODIFIER",		modifier);
					jrParam.setField("TRANS_ORD_DT",	transOrdDt);
					jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
					jrParam.setField("CARD_NO",			ydCardNo);
					jrParam.setField("YD_CARPNT_CD",	CarldPntCd);
					JDTORecordSet jsCarSch = commDao.select(jrParam, getYdCarYdCmbnCarldYn, logId, methodNm, "차량스케쥴 조회");

					if(jsCarSch.size() > 0)
					{
						commUtils.printLog(logId, methodNm + " TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.:기존지시 삭제 " , "SL");

						String ydOldCarSchId = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID")); //차량 작업지시

						jrParam.setField("YD_CAR_SCH_ID",	ydOldCarSchId);
						jrParam.setField("DEL_YN",			"Y");
						commDao.update(jrParam, updYdCarsch, logId, methodNm, "TB_YD_CARSCH 차량 스케줄정보");
					}

					//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////////////////
					String ydCarSchId = commDao.getSeqId(logId, methodNm, "CarSch");


					//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////
					//차량정보 존재여부 체크 /////////////////////////////////////////////////////////////////
					JDTORecordSet jsCarPnt = commDao.select(jrParam, getYdCarPoint, logId, methodNm, "TB_YD_CARPOINT 조회");

					String ydWlocCd 	= "";
					String ydStkColGp   = "";
					String ydPntCd      = "";

					if(jsCarPnt.size() > 0)
					{
						ydWlocCd 	= commUtils.trim(jsCarPnt.getRecord(0).getFieldString("WLOC_CD"));
						ydStkColGp	= commUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_GP"));
						ydPntCd     = commUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_PNT_CD"));

						if(!"Y".equals(szMOV_YN))
						{
							szMOV_YN	= commUtils.trim(jsCarPnt.getRecord(0).getFieldString("MOV_YN"));
						}

						JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_CAR_SCH_ID",		ydCarSchId);
						recInTemp.setField("REGISTER",			modifier);
						recInTemp.setField("YD_EQP_ID",			YfConstant.YD_DM_CAR_EQP_ID);	//야드설비ID
						recInTemp.setField("YD_CAR_USE_GP",		YfConstant.YD_CAR_USE_GP_DM);	//차량사용구분
						recInTemp.setField("CAR_NO",			ydCarNo);						//차량번호
						recInTemp.setField("CAR_KIND",			ydCarKind);						//차량종류
						recInTemp.setField("YD_EQP_WRK_STAT",	"U");							//야드설비작업상태
						recInTemp.setField("SPOS_WLOC_CD",		ydWlocCd);						//발지개소코드
						recInTemp.setField("YD_CARLD_LEV_DT",	commUtils.getDateTime14());		//상차출발일시
						recInTemp.setField("YD_PNT_CD1",		ydPntCd);						//야드포인트코드1
						recInTemp.setField("YD_CARLD_STOP_LOC",	ydStkColGp);					//야드상차정지위치
						recInTemp.setField("CARD_NO",			ydCardNo);						//카드번호
						recInTemp.setField("YD_CAR_PROG_STAT",	"1");							//상차출발상태
						recInTemp.setField("YD_CAR_WRK_GP",		WorkGp);
						recInTemp.setField("TRANS_ORD_DATE",	transOrdDt);					//운송지시일자
						recInTemp.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);					//운송지시순번

						if("Y".equals(szMOV_YN))
						{
							recInTemp.setField("YD_BAYIN_WO_SEQ",	"1");									//입동지시순번 - 제품이송하차 또는 복수상차는 1순위로 변경 함
						}
						else
						{
							recInTemp.setField("YD_BAYIN_WO_SEQ",	YfConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 - 기본값으로 설정(9)
						}

						if("TT".equals(ydCarKind))
						{
							recInTemp.setField("CAR_KIND",	"TT");								//차량종류
						}
						else
						{
							recInTemp.setField("CAR_KIND",	"TR");								//차량종류
						}

						recInTemp.setField("TRANS_EQUIPMENT_TYPE",	"P");						//운송장비타입 P : PDA
						recInTemp.setField("TEL_NO",			rcvMsg.getFieldString("TEL_NO"));		//연락처
						recInTemp.setField("DRIVER_NAME",		rcvMsg.getFieldString("DRIVER_NAME"));	//운전기사명

						//차량스케줄 등록
						commDao.insert(recInTemp, insYdCarsch, logId, methodNm, "TB_YD_CARSCH 등록");

						//차량스케쥴재료 등록...주석처리(권하시 등록함)
//						for(int i = 1 ; i<= ydEqpWrkSh; i++)
//						{
//							recInTemp = JDTORecordFactory.getInstance().create();
//							recInTemp.setField("YD_CAR_SCH_ID", ydCarSchId);
//							recInTemp.setField("MODIFIER",		modifier);
//							recInTemp.setField("STL_NO",		commUtils.trim(rcvMsg.getFieldString("STL_NO"+i)));
//							commDao.insert(recInTemp, insCarSchmtl, logId, methodNm, "차량재료 스케쥴 INSERT ");
//						}

			    		if("TT".equals(ydCarKind))
			    		{
			    			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					        EJBConnector ejbConn3 = new EJBConnector("default","YfCommCarMvBakSeEJB",this);
							ejbConn3.trx("YfCarPointinforeg", new Class[]{ String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class },
						  	             new Object[]{ "C", ydCarNo, ydCardNo, ydStkColGp, "", "", "R", logId, methodNm });
			    		}
					}
					else
					{
						commUtils.printLog(logId, methodNm + "TB_YD_CARPOINT[차량포인트가 존재 안합니다..]" , "SL");
					}

					//입동지시요구모듈 호출(trailer인 경우)///////////////////////////////////////////////////////
					if("T".equals(ydCarKind) || "TR".equals(ydCarKind))
					{
						/*
						 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
						 */
						commUtils.printLog(logId, "차량정지위치[" + ydStkColGp + "], 차량스케줄ID[" + ydCarSchId + "] -PDA AB차량입동지시요구 모듈을 호출 시작" , "SL");

						JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setResultCode(logId);		//Log ID
						recInTemp.setResultMsg(methodNm);	//Log Method Name
						recInTemp.setField("JMS_TC_CD",				"YFYFJ662");		//차량입동지시 요구 기존:YDYDJ662
						recInTemp.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14()); //JMSTC생성일시
						recInTemp.setField("YD_CARPNT_CD",			CarldPntCd);
						recInTemp.setField("YD_CAR_SCH_ID",			ydCarSchId);
						recInTemp.setField("CHK_YN",				"N");
						recInTemp.setField("CR_FRTOMOVE_GP",		crFrtomoveGp);	//냉연이송구분

						jrRtn = commUtils.addSndData(jrRtn, recInTemp);
					}
				}
			}

			commUtils.printLog(logId, "=============코일이송상차대기장도착PDA 수신 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
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
	public JDTORecord rcvDMYDR071(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "코일이송상차도착PDA 수신[ACoilRevL3SeEJB.rcvDMYDR071] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
	    JDTORecord	jrRst		= JDTORecordFactory.getInstance().create();
		int			intRtnVal	= 0;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일이송상차도착PDA 수신 시작========", "SL");

			//수신 항목 값
			String transOrdDt  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); 	//운송실적일자
			String transOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));	//운송실적순번
			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
			String ydCardNo 		= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
			String WorkGp 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP"));   		//작업구분
			String ydCarKind		= commUtils.trim(rcvMsg.getFieldString("CAR_KIND"));
			String CarldPntCd		= commUtils.trim(rcvMsg.getFieldString("CARLD_PNT_CD"));   	//상차포인트
			String crFrtomoveGp		= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP")); 	//냉연이송구분

			String Stl_no			= "";
			String szMsg            = "";
			String ydCarSchId       = "";
			String ydCarProgStat    = "";
			String ydCarWrkGp 		= ""; 	//야드차량작업구분
			String ydEqpWrkStat		= ""; 	//야드설비작업상태

			String ydFrmYn			= "";	//차량형상사용유무
			String dummyYn			= "";	//더미작업여부

			String sAPPLY = yfComm.ACoilApplyYn("APP003", "1", "1");
			commUtils.printLog(logId,  "차량ERROR LOG 처리:" + sAPPLY, "SL");

		   /**********************************************************
			* 2. 운송실적번호로 제품번호 가져오기
			**********************************************************/
			jrParam.setField("TRANS_ORD_DATE",	transOrdDt);
			jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
			JDTORecordSet jsStlno = commDao.select(jrParam, getTransStockInfo, logId, methodNm, "운송실적번호에 맞는 제품번호가 존재 조회");

			if(jsStlno.size() <= 0 )
			{
				szMsg = "[" + methodNm + "] [코일제품상차지시등록] 운송실적번호에 맞는 제품번호가 존재 안함: TRANS_WORD_NO:[" + transOrdDt+transOrdSeqNo + "]";
				commUtils.printLog(logId, szMsg, "SL");

				throw new Exception("[코일제품상차지시등록] [차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다....");
			}
			else
			{
				Stl_no 		= commUtils.trim(jsStlno.getRecord(0).getFieldString("STL_NO"));
				ydCarSchId	= commUtils.trim(jsStlno.getRecord(0).getFieldString("YD_CAR_SCH_ID"));	//차량 작업지시

				jrParam.setField("STL_NO", Stl_no);

				//------------------------------------------------------------------------------------------------------------
		    	//	이적 및 대차출하 작업예약 존재 여부 CHECK
		    	//------------------------------------------------------------------------------------------------------------
				jrParam.setField("TRANS_ORD_DT",	transOrdDt);
				jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
				JDTORecordSet jsWbookMtl = commDao.select(jrParam, getYfStockWbookcheck, logId, methodNm, "작업예약 조회");

				if(jsWbookMtl.size() > 0)
			 	{
					commUtils.printLog(logId, "이적 및 대차출하  작업예약이 삭제처리 합니다", "SL");

					for (int Loop_i = 1; Loop_i <= jsWbookMtl.size() ; Loop_i++)
					{
						jsWbookMtl.absolute(Loop_i);
						JDTORecord jrInPara = JDTORecordFactory.getInstance().create();

						jrInPara.setRecord(jsWbookMtl.getRecord());
						jrInPara.setResultCode(logId);		//Log ID
						jrInPara.setResultMsg(methodNm);	//Log Method Name
						jrInPara.setField("MODIFIER",	modifier); //수정자

						//크레인 작업예약 삭제
						EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
						ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrInPara });
					}
				}

			   /**********************************************************
				* 3. 작업예약 편성여부 :
				**********************************************************/
				JDTORecordSet jsChk2 = commDao.select(jrParam, getYfwBookStockYN, logId, methodNm, "작업예약 편성여부");

				commUtils.printLog(logId, "작업 예약 편성여부(WB_STL_YN) : " + jsChk2.getRecord(0).getFieldString("WB_STL_YN") , "SL");

				if (jsChk2 != null && jsChk2.size() > 0)
				{
					if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN")))
					{
						if("Y".equals(sAPPLY))
						{
							/***** 차량log ****/
							JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
							jrLogMsg.setResultCode(logId);		//Log ID
							jrLogMsg.setResultMsg(methodNm);	//Log Method Name
							jrLogMsg.setField("MODIFIER",		modifier); //수정자 셋팅
							jrLogMsg.setField("YD_MSG_NM",		"대상 코일 작업중(스케줄확인)"); //메세지
							jrLogMsg.setField("YD_CAR_SCH_ID",	ydCarSchId); //차량스케쥴

							EJBConnector ejbConnLog = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
							ejbConnLog.trx("updCarErrorLogNew", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });
						}

						throw new Exception("이미 작업예약되어 있슴  Error : "+ Stl_no);
					}
				}

				/**********************************************************
				* 1. 운송지시갯수와 LAYER 갯수 확인  CHECK
				**********************************************************/
				jrParam.setField("TRANS_ORD_DT"		,transOrdDt);
				jrParam.setField("TRANS_ORD_SEQNO"	,transOrdSeqNo);
				JDTORecordSet jsStockCnt = commDao.select(jrParam, getYdStockLayerCntChk, logId, methodNm, "작업대상갯수 조회");

				if(jsStockCnt.size() <= 0)
				{
					if("Y".equals(sAPPLY))
					{
						/***** 차량log ****/
						JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
						jrLogMsg.setResultCode(logId);		//Log ID
						jrLogMsg.setResultMsg(methodNm);	//Log Method Name
						jrLogMsg.setField("MODIFIER",		modifier); //수정자 셋팅
						jrLogMsg.setField("YD_MSG_NM",		"운송지시갯수와  저장위치 저장품갯수가 틀림:코일정보 확인 "); //메세지
						jrLogMsg.setField("YD_CAR_SCH_ID",	ydCarSchId); //차량스케쥴

						EJBConnector ejbConnLog = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
						ejbConnLog.trx("updCarErrorLogNew", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });
					}

					throw new Exception("운송지시갯수와  저장위치 저장품갯수가 틀림 : ");
			 	}
			}

			jrParam.setField("YD_CAR_SCH_ID",	ydCarSchId);
			JDTORecordSet jsCarSch = commDao.select(jrParam, getYdCarschNot2, logId, methodNm, "TB_YD_CARSCH 조회");

			if(jsCarSch.size() <= 0 )
			{
				szMsg = "["+methodNm+"] 차량 스케쥴 없음" ;
				throw new Exception(szMsg + Stl_no);
			}
			else
			{
				ydCarProgStat 	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT")); //차량진행상태
				ydCarWrkGp 		= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_WRK_GP")); 	//야드차량작업구분
				ydEqpWrkStat	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_EQP_WRK_STAT")); 	//야드설비작업상태
			}

			jrParam.setField("TC_CD",		msgId);
			jrParam.setField("STL_NO",		Stl_no);
			jrParam.setField("MODIFIER",	modifier);	//수정자

			JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);
			jrParam.setField("STOCK_MOVE_TERM",	commUtils.nvl(jrRtnProg.getFieldString("STOCK_MOVE_TERM"), "LG"));
			jrParam.setField("CAR_CARD_NO",		ydCardNo);
			jrParam.setField("MODIFIER",		modifier);
			intRtnVal =	commDao.update(jrParam, updYdStock4, logId, methodNm, "TB_YF_STOCK 수정");

            if(intRtnVal <= 0)
            {
            	szMsg = "["+methodNm+"]에 대한 저장품 DATA가 존재하지않음:"+" STL_NO:["+Stl_no+"]"+" TRANS_WORD_NO:["+transOrdDt+transOrdSeqNo+"]"+intRtnVal+"건" ;
            	commUtils.printLog(logId, szMsg, "SL");
            	throw new Exception(szMsg + Stl_no);
			}

			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if("9".equals(WorkGp))
			{
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("MODIFIER",		modifier);
				jrParam.setField("TRANS_ORD_DT",	transOrdDt);
				jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
				jrParam.setField("CARD_NO",			ydCardNo);
				jrParam.setField("YD_CARPNT_CD",	CarldPntCd);
				JDTORecordSet jsCarPnt = commDao.select(jrParam, getYdCarPoint, logId, methodNm, "차량포인트 조회");

				if(jsCarPnt.size() > 0)
				{
					String ydStkColGp		= commUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_GP2"));
					String ydStkColActStat	= commUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));

					// TT-Car가 예약인 경우 차량도착처리 호출이 가능 하도록 조치
					if ("TT".equals(ydCarKind) && "R".equals(ydStkColActStat))
					{
						ydStkColActStat = "C";
					}

					//TR가 도착해 있는 경우
					if ("TR".equals(ydCarKind) && "L".equals(ydStkColActStat))
					{
						ydStkColActStat = "C";
					}

					if("C".equals(ydStkColActStat))
					{
						JDTORecord jrInTemp = JDTORecordFactory.getInstance().create();
						jrInTemp.setResultCode(logId);		//Log ID
					    jrInTemp.setResultMsg(methodNm);	//Log Method Name
						jrInTemp.setField("MODIFIER",				modifier); //수정자
						jrInTemp.setField("YD_STK_COL_GP",			ydStkColGp);
						jrInTemp.setField("CARD_NO",				ydCardNo);
						jrInTemp.setField("CAR_NO",					ydCarNo);
						jrInTemp.setField("TRN_EQP_CD",				"");
						jrInTemp.setField("YD_CAR_PROG_STAT",		ydCarProgStat);
						jrInTemp.setField("TRANS_EQUIPMENT_TYPE",	"P");  //냉연 이송

						EJBConnector ejbConn1 = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
						ejbConn1.trx("procYfLayerOpen", new Class[] { JDTORecord.class }, new Object[] { jrInTemp });

						/***************************************
						 * 	5.작업예약이 생성
						 **************************************/
						//작업예약,작업재료 등록
						String ydSchCd  = "";

						int  intTransOrdSeqNo   = Integer.parseInt(transOrdSeqNo);

						//2020.06.25 CR_FRTOMOVE_GP(냉연이송구분)의 값이 63, 11 인경우 출하로 판단하여 스케줄코드 생성
						if("11".equals(crFrtomoveGp) || "63".equals(crFrtomoveGp))
						{
							//CR_FRTOMOVE_GP(냉연이송구분) 값이 63, 11인경우
							//차량출하
							if ("2".equals(CarldPntCd.substring(1,2)))
							{
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT02UM";
							}
							else
							{
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT01UM";
							}
						}
						else
						{
							if ((intTransOrdSeqNo > 800000))
						 	{
								//제품이송상차
								if ("2".equals(CarldPntCd.substring(1,2)))
								{
									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT22UM";
								}
								else
								{
									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT21UM";
								}
							}
						 	else if ((intTransOrdSeqNo > 700000 && intTransOrdSeqNo <= 800000))
						 	{
								//(임가공이송 OR 순천 이송) 스케줄 구분 : 냉연이송
								//소재이송상차
								if ("2".equals(CarldPntCd.substring(1,2)))
								{
									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT12UM";
								}
								else
								{
									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT11UM";
								}
						 	}
						 	else
						 	{
								//제품이송상차
								if ("2".equals(CarldPntCd.substring(1,2)))
								{
									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT22UM";
								}
								else
								{
									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT21UM";
								}
							}
						}

						jrInTemp = JDTORecordFactory.getInstance().create();
						jrInTemp.setField("YD_SCH_CD",		ydSchCd);// 자동이적
						jrInTemp.setField("YD_CAR_SCH_ID",	ydCarSchId);
						jrInTemp.setField("YD_STK_COL_GP",	ydStkColGp);
						jrInTemp.setField("CARD_NO",		ydCardNo);
						jrInTemp.setField("CAR_NO",			ydCarNo);
						jrInTemp.setField("MODIFIER",		modifier);

		    			String ydWbookId = yfComm.procCarWkBookInsert(jrInTemp);

		    			if(ydWbookId.equals(YfConstant.RETN_CD_FAILURE))
		    			{
		    				throw new Exception("작업예약ID 생성 실패");
		    			}

		    			//----------------------------------------------------------------------
						// 야드저장위치제원(YFF1L001) 전문전송
						//----------------------------------------------------------------------
		    			jrInTemp.setField("YD_INFO_SYNC_CD",	"3");			//야드정보동기화코드(3:열)
		    			jrInTemp.setField("YD_STK_COL_GP",		ydStkColGp);	//야드적치열구분
						jrRtn = commUtils.addSndData(commDao.getMsgL2("YFF1L001", jrInTemp));

						//------------------------------------------------------------------------------------------------------------
				    	//	차량스케줄 도착상태 변경 처리
				    	//------------------------------------------------------------------------------------------------------------
					    jrInTemp = JDTORecordFactory.getInstance().create();
					    jrInTemp.setResultCode(logId);		//Log ID
					    jrInTemp.setResultMsg(methodNm);	//Log Method Name
						jrInTemp.setField("MODIFIER",		modifier);	//수정자
						jrInTemp.setField("YD_CAR_SCH_ID",	ydCarSchId);

						if("L".equals(ydEqpWrkStat))
						{
							jrInTemp.setField("YD_CARUD_ARR_DT",	commUtils.getDateTime14());
							jrInTemp.setField("YD_CAR_PROG_STAT",	"B");		//하차도착상태
						}
						else
						{
							jrInTemp.setField("YD_CARLD_ARR_DT",	commUtils.getDateTime14());
							jrInTemp.setField("YD_CAR_PROG_STAT",	"2");		//상차도착상태
						}

						commDao.update(jrInTemp, updYdCarsch, logId, methodNm, "TB_YD_CARSCH 차량 상태변경");

						//------------------------------------------------------------------------------------------------------------
				    	//	재료정보 조회 (2단,1단 순)
				    	//------------------------------------------------------------------------------------------------------------
						JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
						jrParam1.setResultCode(logId);		//Log ID
			 			jrParam1.setResultMsg(methodNm);	//Log Method Name
						jrParam1.setField("TRANS_ORD_DATE",		transOrdDt);
						jrParam1.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
			 			JDTORecordSet jsCarMtl = commDao.select(jrParam1, getYfStockOfCarLoad, logId, methodNm, "재료정보 조회");

						/**********************************************************
						* Crane스케줄 호출
						*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
						*  - 차량형상 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
						*  - 더미작업여부가 Y일 경우 차량형상 시스템이 있더라도 스케줄 기동
						**********************************************************/
						jrParam.setField("YD_STK_COL_GP",	ydStkColGp);
						JDTORecordSet rsResult3 = commDao.select(jrParam, getCarPntFrmYn,  logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 ");
						JDTORecordSet rsResult4	= commDao.select(jrParam, getCarPntFrmYn2, logId, methodNm, "상차 주작업대상재의 더미작업여부 확인 ");

						commUtils.printLog(logId, "차량형상 시스템 사용 여부: " + rsResult3.getRecord(0).getFieldString("YD_FRM_YN"), "SL");
						commUtils.printLog(logId, "더미작업여부 : " + rsResult4.getRecord(0).getFieldString("DUMMY_YN"), "SL");

						ydFrmYn = rsResult3.getRecord(0).getFieldString("YD_FRM_YN");
						dummyYn	= rsResult4.getRecord(0).getFieldString("DUMMY_YN");

						//2020.02.25 정종균과장요청 형상유무 사용인 경우에만 차량예정정보 송신
						//if("Y".equals(ydFrmYn))
						//{
							// 차량예정정보 송신
							jrParam = JDTORecordFactory.getInstance().create();
							jrParam.setResultCode(logId);	//Log ID
							jrParam.setResultMsg(methodNm);	//Log Method Name
				 			jrParam.setField("MODIFIER",		modifier);		//수정자
							jrParam.setField("YD_CAR_SCH_ID",	ydCarSchId);	//야드차량스케쥴ID
							jrParam.setField("SEARCH_FLAG",		"2");			//1:상차도, 2:차량스케쥴 ID
							jrRtn = commUtils.addSndData(jrRtn, yfComm.procCarPlanInfo(jrParam));	//YFF1L008 생성
						//}

						//차량형상사용 'N' 또는 더미작업유무 'Y'인 경우 크레인 스케줄 기동
						if("N".equals(ydFrmYn) || "Y".equals(dummyYn))
						{
							//크레인 스케줄 기동 YFYFJ303 호출
							JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
							jrCrnSchMsg.setField("JMS_TC_CD",			"YFYFJ303");
							jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14()); //JMSTC생성일시
							jrCrnSchMsg.setField("YD_SCH_CD",			""); //야드스케쥴코드
							jrCrnSchMsg.setField("YD_EQP_ID",			""); //야드설비ID

							int pcnt = 0;

							//2단 적치된 대상 스케줄 호출
							for(int i = 0; i < jsCarMtl.size(); i++)
							{
								if("02".equals(jsCarMtl.getRecord(i).getFieldString("YD_STK_LYR_NO")))
								{
									jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt),	jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
								}
							}

							//1단 적치된 대상 스케줄 호출
							for(int i = 0; i < jsCarMtl.size(); i++)
							{
								if("01".equals(jsCarMtl.getRecord(i).getFieldString("YD_STK_LYR_NO")))
								{
									jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt),	jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
								}
							}

							jrCrnSchMsg.setField("SCH_CNT",	Integer.toString(pcnt));

							jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
						}
					}
				}

				if("Y".equals(sAPPLY))
		 		{
					/***** 차량log Claer ****/
					JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
					jrLogMsg.setResultCode(logId);		//Log ID
					jrLogMsg.setResultMsg(methodNm);	//Log Method Name
					jrLogMsg.setField("MODIFIER",		modifier);		//수정자 셋팅
					jrLogMsg.setField("YD_CAR_SCH_ID",	ydCarSchId);	//차량스케쥴

					EJBConnector ejbConnLog = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
					ejbConnLog.trx("updCarErrorLogClear", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });
		 		}
			}

			commUtils.printLog(logId, "=============코일이송상차도착PDA 수신 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
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
	public JDTORecord rcvDMYDR072(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "코일이송상차완료PDA 수신[ACoilRevL3SeEJB.rcvDMYDR072] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
	    JDTORecord	jrRst		= JDTORecordFactory.getInstance().create();
		int			intRtnVal	= 0;

		String		szMsg		= "";

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일이송상차완료PDA 수신 시작========", "SL");

			//수신 항목 값
			String ydGp			= commUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String szRcvTcCode 	= commUtils.trim(rcvMsg.getFieldString("JMS_TC_CD"));
			String stlAppearGp 	= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));   //재료외형
			String Stl_no 		= commUtils.trim(rcvMsg.getFieldString("STL_NO"));    		//재료번호

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			// 수신한 전문이 null이라면 error
			if("".equals(szRcvTcCode))
			{
				szMsg = "[ERROR] " + methodNm + "::" + methodNm + "() TC Code Error (NULL)";
				commUtils.printLog(logId, szMsg, "SL");

				throw new Exception("TC Code Error.");
			}

			jrParam.setField("STL_NO",	Stl_no);
			JDTORecordSet loadYdStkcol = commDao.select(jrParam, getYdStockJoinStkLyr2, logId, methodNm, "차량정정보검색");

			if(loadYdStkcol.size() <= 0 )
			{
				szMsg = "[" + methodNm + "] TB_YF_STOCK[코일이송상차완료]조건조회시 SELECT Error ::  DO NOT EXIST";
				commUtils.printLog(logId, szMsg, "SL");

				throw new Exception(szMsg);
			}

			String ydCarNo    	= commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("CAR_NO"));
			String ydStkColGp 	= commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("YD_STK_COL_GP"));
			String ydCardNo     = commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("CARD_NO"));
			String transOrdDate	= commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("TRANS_ORD_DATE"));
			String transOrdSeqNo= commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("TRANS_ORD_SEQNO"));

			//저장품갱신**************************************************************
			jrParam.setField("STL_APPEAR_GP",	stlAppearGp);
			jrParam.setField("DEL_YN",			"Y");
			jrParam.setField("YD_AIM_RT_GP",	"M2");	//야드목표행선구분(M2:출하완료(Coil))
			jrParam.setField("STOCK_MOVE_TERM",	"M2");	//야드목표행선구분(M2:출하완료(Coil))
			jrParam.setField("STL_PROG_CD",		"M");
			jrParam.setField("STL_NO",			Stl_no);
			jrParam.setField("MODIFIER",		szRcvTcCode);
			intRtnVal = commDao.update(jrParam, updateStock2, logId, methodNm, "TB_YF_STOCK 수정");

			if(intRtnVal <= 0)
			{
				szMsg = "[" + methodNm + "]" + " TB_YF_STOCK[코일이송상차완료] UPDATE Error " + " STL_NO: " + Stl_no;
				commUtils.printLog(logId, szMsg, "SL");

				throw new Exception(szMsg);
	 		}

			//***************************************************************************
			//  저장품이 적치된 저장위치 정보를 조회
			//***************************************************************************
			szMsg="[" + methodNm + "]카드번호["+ydCardNo+"], 차량번호["+ydCardNo+"], 운송지시일자["+transOrdDate+"], 운송지시순번["+transOrdSeqNo+"] : 출하완료된 동["+ydStkColGp+"]의 저장품들 조회 시작";
			commUtils.printLog(logId, szMsg, "SL");

			if("*".equals(stlAppearGp))
			{
				commUtils.printLog(logId, "[" + methodNm + "] 마지막 상차완료 전문", "SL");

				jrParam.setField("CAR_NO", 			ydCarNo);
				jrParam.setField("CARD_NO",			ydCardNo);
				jrParam.setField("TRANS_ORD_DATE",	transOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
				JDTORecordSet loadCarsch = commDao.select(jrParam, getYdCarschTransDTSeq2, logId, methodNm, "차량정정보검색");

				if(loadCarsch.size() <= 0 )
				{
					szMsg = "[" + methodNm + "] 차량스케쥴 조회 SELECT Error ::  DO NOT EXIST";
					commUtils.printLog(logId, szMsg, "SL");

					throw new Exception(szMsg);
				}

				jrParam.setField("TC_CODE",			"DMYDR040");	//전문코드
				jrParam.setField("SPOS_WLOC_CD",	commUtils.trim(loadCarsch.getRecord(0).getFieldString("SPOS_WLOC_CD")));
				jrParam.setField("SPOS_YD_PNT_CD",	commUtils.trim(loadCarsch.getRecord(0).getFieldString("YD_PNT_CD1")));
				jrParam.setField("YD_GP",			ydGp);

				if("".equals(ydCardNo))
				{
					ydCardNo = "XXXXX";
				}

				commUtils.printParam(logId, jrParam);

				//전송 Data 생성
				szMsg= "[" + methodNm +"] 차량번호[" + ydCarNo + "]는 코일제품출하차량출발실적호출";
				commUtils.printLog(logId, szMsg, "SL");

				EJBConnector ejbConn1 = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
				JDTORecord jrRtn1 = (JDTORecord)ejbConn1.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { jrParam });

				jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
			}
			else
			{
				szMsg = "[" + methodNm + "] 마지막 상차완료 전문이 아님";
				commUtils.printLog(logId, szMsg, "SL");
			}

			// ======================================================
			// 저장품제원 : 코일야드L2로 송신(YFF1L002)
			// ======================================================
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);		//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
			sndL2Msg.setField("YD_INFO_SYNC_CD",	"5"); //야드정보동기화코드
			sndL2Msg.setField("MSG_GP",				"I"); //전문구분
			sndL2Msg.setField("STL_NO",				Stl_no); //재료번호
			commUtils.printParam(logId, sndL2Msg);

			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성

			szMsg="[" + methodNm + "] 저장품제원 : 코일야드L2 로 송신 저장품ID[" + Stl_no + "] - 저장품제원 : 코일야드L2 로 송신 호출 성공"+jrRtn.size();

			commUtils.printLog(logId, "=============코일이송상차완료PDA 수신 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
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
	public JDTORecord rcvDMYDR073(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "코일이송하차지시PDA 수신[ACoilRevL3SeEJB.rcvDMYDR073] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일이송하차지시PDA 수신 시작========", "SL");

			//수신 항목 값
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

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			jrParam.setField("MODIFIER",		modifier);
			jrParam.setField("STOCK_ITEM",		"CG");
			jrParam.setField("STOCK_MOVE_TERM",	"CS");
			jrParam.setField("TRANS_ORD_DATE",	transOrdDt);
			jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
			jrParam.setField("CAR_CARD_NO",		ydCardNo);
			jrParam.setField("CAR_NO",			ydCarNo);
			jrParam.setField("CR_FRTOMOVE_GP",	transFrtoMoveGp);
			jrParam.setField("YD_CARPNT_CD",	CarudPntCd);
			jrParam.setField("TRANS_WORD_NO",	transOrdDt + transOrdSeqNo);

			String Stl_no  = "";

			for(int i = 1 ; i<=20; i++)
			{
				Stl_no = commUtils.trim(rcvMsg.getFieldString("STL_NO"+i));

				if("".equals(Stl_no))
				{
					break;
				}

				jrParam.setField("STL_NO",				Stl_no);
				jrParam.setField("YD_CAR_UPP_LOC_CD",	commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i)));  // 차상위치
		    	commDao.update(jrParam, insStockTransInfo, logId, methodNm, "TB_YF_STOCK 등록");
			}

			//작업구분(1:내수/2:수출/3:연안해송/9:냉연이송)
			if("9".equals(WorkGp))
			{
				JDTORecordSet jsCarSch = commDao.select(jrParam, getYdCarYdCmbnCarldYn, logId, methodNm, "차량스케쥴 조회");

				if(jsCarSch.size() > 0)
				{
					commUtils.printLog(logId, methodNm + " TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.:기존지시 삭제 " , "SL");

					String oldCarSchId = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID")); //차량 작업지시

					jrParam.setField("YD_CAR_SCH_ID",	oldCarSchId);
					jrParam.setField("DEL_YN",			"Y");
					commDao.update(jrParam, updYdCarsch, logId, methodNm, "TB_YD_CARSCH 차량 스케줄정보");
				}

				//차량스케줄 생성
				String ydCarSchId = commDao.getSeqId(logId, methodNm, "CarSch");

				///도착가능 포인트 조회
				JDTORecord jrCarPnt = JDTORecordFactory.getInstance().create();
				JDTORecordSet jsCarPnt = commDao.select(jrParam, getYdCarPoint, logId, methodNm, "차량포인트 조회");

				if(jsCarPnt.size() <= 0 )
				{
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
				recInTemp.setField("YD_CAR_SCH_ID",			ydCarSchId);
				recInTemp.setField("REGISTER",				modifier);
				recInTemp.setField("YD_EQP_ID",				YfConstant.YD_DM_CAR_EQP_ID);	//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",			YfConstant.YD_CAR_USE_GP_DM);	//차량사용구분
				recInTemp.setField("CAR_NO",				ydCarNo);						//차량번호
				recInTemp.setField("CAR_KIND",				ydCarKind);						//차량종류
				recInTemp.setField("YD_EQP_WRK_STAT",		"L");							//야드설비작업상태
				recInTemp.setField("SPOS_WLOC_CD",			arrWlocCd);						//발지개소코드
				recInTemp.setField("ARR_WLOC_CD",			arrWlocCd);						//착지개소코드
				recInTemp.setField("YD_CARUD_LEV_DT",		commUtils.getDateTime14());		//하차출발일시
				recInTemp.setField("YD_PNT_CD3",			ydPntCd);						//야드포인트코드3
				recInTemp.setField("YD_CARUD_STOP_LOC",		ydStkColGp);					//야드하차차정지위치
				recInTemp.setField("CARD_NO",				ydCardNo);						//카드번호
				recInTemp.setField("YD_CAR_PROG_STAT",		"A");							//하차출발상태
				recInTemp.setField("TRANS_ORD_DATE",		transOrdDt);					//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",		transOrdSeqNo);					//운송지시순번

				if("Y".equals(szMovYn))
				{
					recInTemp.setField("YD_BAYIN_WO_SEQ",	"1");									//입동지시순번 - 제품이송하차는 1순위로 변경 함
		    	}
				else
				{
		    		recInTemp.setField("YD_BAYIN_WO_SEQ",  	YfConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 - 기본값으로 설정(9)
		    	}

				recInTemp.setField("YD_CAR_WRK_GP",			WorkGp);
				recInTemp.setField("TRANS_EQUIPMENT_TYPE",	"P");

				if("TT".equals(ydCarKind))
				{
					recInTemp.setField("CAR_KIND",          "TT");							//차량종류
				}
				else
				{
					recInTemp.setField("CAR_KIND",          "TR");							//차량종류
				}

				recInTemp.setField("TEL_NO",   		rcvMsg.getFieldString("TEL_NO"));		//연락처
				recInTemp.setField("DRIVER_NAME",  	rcvMsg.getFieldString("DRIVER_NAME"));	//운전기사명

	    		//차량스케줄 등록
				commDao.insert(recInTemp, insYdCarsch, logId, methodNm, "TB_YD_CARSCH 등록");

				String gdsCarldLoc = "";
				String ydStkBedNo  = "";
				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();

				for(int i = 1 ; i<=ydEqpWrkSh; i++)
				{
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID",		ydCarSchId);
					recInTemp.setField("MODIFIER",			modifier);
					recInTemp.setField("STL_NO",			commUtils.trim(rcvMsg.getFieldString("STL_NO"+i)));

					gdsCarldLoc = commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i));

					if("A".equals(gdsCarldLoc.substring(0, 1)))
					{
						ydStkBedNo = "0" + gdsCarldLoc.substring(1, 2);
					}
					else if("B".equals(gdsCarldLoc.substring(0, 1)))
					{
						if("1".equals(gdsCarldLoc.substring(1, 2)))
						{
							ydStkBedNo = "06";
						}
						else if("2".equals(gdsCarldLoc.substring(1, 2)))
						{
							ydStkBedNo = "07";
						}
						else if("3".equals(gdsCarldLoc.substring(1, 2)))
						{
							ydStkBedNo = "08";
						}
						else if("4".equals(gdsCarldLoc.substring(1, 2)))
						{
							ydStkBedNo = "09";
						}
						else if("5".equals(gdsCarldLoc.substring(1, 2)))
						{
							ydStkBedNo = "10";
						}
					}
					else if("C".equals(gdsCarldLoc.substring(0, 1)))
					{
						if("1".equals(gdsCarldLoc.substring(1, 2)))
						{
							ydStkBedNo = "11";
						}
						else if("2".equals(gdsCarldLoc.substring(1, 2)))
						{
							ydStkBedNo = "12";
						}
						else if("3".equals(gdsCarldLoc.substring(1, 2)))
						{
							ydStkBedNo = "13";
						}
						else if("4".equals(gdsCarldLoc.substring(1, 2)))
						{
							ydStkBedNo = "14";
						}
						else if("5".equals(gdsCarldLoc.substring(1, 2)))
						{
							ydStkBedNo = "15";
						}
					}

					recInTemp.setField("YD_STK_BED_NO",		ydStkBedNo);
					recInTemp.setField("YD_STK_LYR_NO",		"001");
					commDao.insert(recInTemp, insCarSchmtl, logId, methodNm, "TB_YD_CARFTMVMTL 차량재료 스케쥴 INSERT ");

					//======================================================
					// 저장품제원 : 코일야드L2 로 송신(YFF1L002)
					//======================================================
					sndL2Msg = JDTORecordFactory.getInstance().create();
					sndL2Msg.setField("MSG_GP",				"I");
					sndL2Msg.setField("YD_INFO_SYNC_CD",	"5");    // 5:지정저장품
					sndL2Msg.setField("STL_NO",				commUtils.trim(rcvMsg.getFieldString("STL_NO"+i)));

					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성
				}

	    		if("TT".equals(ydCarKind))
	    		{
		    		//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
	    			EJBConnector ejbConn3 = new EJBConnector("default","YfCommCarMvBakSeEJB",this);
					ejbConn3.trx("YfCarPointinforeg", new Class[]{ String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class },
					 			new Object[]{ "C", "", "", ydStkColGp, "", "", "R", logId, methodNm });
	    		}

				//입동지시요구모듈 호출(trailer인 경우)///////////////////////////////////////////////////////
				if("T".equals(ydCarKind) || "TR".equals(ydCarKind))
				{
					/*
					 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
					 */
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);		//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
					recInTemp.setField("JMS_TC_CD",				"YFYFJ662");          //차량입동지시 요구 기존:YDYDJ662
					recInTemp.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14()); //JMSTC생성일시
					recInTemp.setField("YD_CARPNT_CD",			CarudPntCd);
					recInTemp.setField("YD_CAR_SCH_ID",			ydCarSchId);
					recInTemp.setField("CHK_YN",				"N");

					jrRtn = commUtils.addSndData(jrRtn, recInTemp);

					commUtils.printLog(logId, methodNm + "차량정지위치[" + ydStkColGp + "], 차량스케줄ID[" + ydCarSchId + "] -AB 차량입동지시요구 모듈을 호출", "SL");
				}
			}

			commUtils.printLog(logId, "=============코일이송하차지시PDA 수신 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
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
	public JDTORecord rcvDMYDR074(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "코일이송하차대기장도착PDA 수신[ACoilRevL3SeEJB.rcvDMYDR074] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

	    String		ydStkColGp	= "";

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일이송하차대기장도착PDA 수신 시작========", "SL");

			//수신 항목 값
			String transOrdDt  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			String transOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
			String ydCardNo 		= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
			String ydCarKind		= commUtils.trim(rcvMsg.getFieldString("CAR_KIND"));     //차량종류
			String WorkGp 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP"));      //작업구분
			String CarldPntCd 		= commUtils.trim(rcvMsg.getFieldString("CARUD_PNT_CD")); //하차포인트 CARUD_PNT_CD ?
			String transFrtoMoveGp 	= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP")); //냉연이송구분

			String ydCarProgStat    = "";
			String ydCarWrkGp 		= ""; 	//야드차량작업구분
			String ydEqpWrkStat		= ""; 	//야드설비작업상태

			String ydFrmYn			= "";	//차량형상사용유무

			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrInTemp = JDTORecordFactory.getInstance().create();

			jrParam.setField("TRANS_ORD_DATE",	transOrdDt);
			jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
			JDTORecordSet jsStock = commDao.select(jrParam, getTransStockInfo, logId, methodNm, "운송실적번호에 맞는 제품번호가 존재 조회");

			String ydCarSchId = commUtils.trim(jsStock.getRecord(0).getFieldString("YD_CAR_SCH_ID")); //차량 작업지시

			jrParam.setField("YD_CAR_SCH_ID",	ydCarSchId);
			JDTORecordSet jsCarSch = commDao.select(jrParam, getYdCarsch, logId, methodNm, "TB_YD_CARSCH 조회");

			if(jsCarSch.size() <= 0 )
			{
				commUtils.printLog(logId, "["+methodNm+"] 차량 스케쥴 없음" , "SL");
				throw new Exception("차량 스케쥴 없음");
			}
			else
			{
				ydCarProgStat 	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT")); //차량진행상태
				ydCarWrkGp 		= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_WRK_GP")); 	//야드차량작업구분
				ydEqpWrkStat	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_EQP_WRK_STAT")); 	//야드설비작업상태
			}

			//작업구분(1:내수/2:수출/3:연안해송/9:냉연이송)
			if("9".equals(WorkGp))
			{
				//########################################## 9:HYSCO스케줄 ####################################################
				jrInTemp = JDTORecordFactory.getInstance().create();
				jrInTemp.setResultCode(logId);		//Log ID
				jrInTemp.setResultMsg(methodNm);	//Log Method Name
				jrInTemp.setField("MODIFIER",		modifier);	//수정자
				jrInTemp.setField("YD_CARPNT_CD",	CarldPntCd);

				//포인트코드 -> 개소코드와 저장위치 가져오기
				JDTORecordSet jsCarPnt = commDao.select(jrInTemp, getYdCarPoint, logId, methodNm, "차량포인트 조회");

				if(jsCarPnt.size() > 0)
				{
					//TB_YF_STKLYR 에 해당 재료 등록 처리
					jsCarPnt.first();
					JDTORecord jrCarPnt 	= jsCarPnt.getRecord();
					ydStkColGp				= commUtils.trim(jrCarPnt.getFieldString("YD_STK_COL_GP2"));
					String ydStkColActStat	= commUtils.trim(jrCarPnt.getFieldString("YD_STK_COL_ACT_STAT"));

					jrInTemp.setField("YD_CAR_SCH_ID",	ydCarSchId);
					jrInTemp.setField("YD_STK_COL_GP",	ydStkColGp);
					commDao.update(jrInTemp, updYfStacklayer, logId, methodNm, "TB_YF_STKLYR 수정");

					// TT-Car가 예약인 경우 차량도착처리 호출이 가능 하도록 조치
					if ("TT".equals(ydCarKind) && "R".equals(ydStkColActStat))
					{
						ydStkColActStat = "C";
					}

					//TR가 도착해 있는 경우
					if ("TR".equals(ydCarKind) && "L".equals(ydStkColActStat))
					{
						ydStkColActStat = "C";
					}

					if("C".equals(ydStkColActStat))
					{
						//------------------------------------------------------------------------------------------------------------
						//	차량 POINT TABLE 점유
						//------------------------------------------------------------------------------------------------------------
						jrInTemp = JDTORecordFactory.getInstance().create();
					    jrInTemp.setResultCode(logId);		//Log ID
					    jrInTemp.setResultMsg(methodNm);	//Log Method Name
						jrInTemp.setField("MODIFIER",				modifier);	//수정자
						jrInTemp.setField("CAR_NO",					ydCarNo);
						jrInTemp.setField("CARD_NO",				ydCardNo);
						jrInTemp.setField("YD_MAKECARPNT_CD",		CarldPntCd);

					    EJBConnector ejbConn = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
					    ejbConn.trx("procUpdYdTransOrdChange", new Class[] { JDTORecord.class }, new Object[] { jrInTemp });

						jrInTemp = JDTORecordFactory.getInstance().create();
						jrInTemp.setResultCode(logId);		//Log ID
					    jrInTemp.setResultMsg(methodNm);	//Log Method Name
						jrInTemp.setField("MODIFIER",				modifier);	//수정자
						jrInTemp.setField("YD_STK_COL_GP",			ydStkColGp);
						jrInTemp.setField("CARD_NO",				ydCardNo);
						jrInTemp.setField("CAR_NO",					ydCarNo);
						jrInTemp.setField("TRN_EQP_CD",				"");
						jrInTemp.setField("YD_CAR_PROG_STAT",		ydCarProgStat);
						jrInTemp.setField("TRANS_EQUIPMENT_TYPE",	"P");		//냉연 이송

						EJBConnector ejbConn1 = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
						ejbConn1.trx("procYfLayerOpen", new Class[] { JDTORecord.class }, new Object[] { jrInTemp });

						/***************************************
						 * 	5.작업예약이 생성
						 **************************************/
						//작업예약,작업재료 등록
						String ydSchCd  = "";

						int  intTransOrdSeqNo   = Integer.parseInt(transOrdSeqNo);

						if (intTransOrdSeqNo > 999000)
					 	{
					 		//차량반입
							if ("2".equals(CarldPntCd.substring(1, 2)))
							{
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT02LM";
							}
							else
							{
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT01LM";
							}
					 	}
					 	else if ((intTransOrdSeqNo > 800000))
					 	{
							//제품이송하차
							if ("2".equals(CarldPntCd.substring(1, 2)))
							{
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT22LM";
							}
							else
							{
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT21LM";
							}
						}
					 	else if ((intTransOrdSeqNo > 700000 && intTransOrdSeqNo <= 800000))
					 	{
							//(임가공이송 OR 순천 이송) 스케줄 구분 : 냉연이송
							//소재이송하차
							if ("2".equals(CarldPntCd.substring(1, 2)))
							{
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT12LM";
							}
							else
							{
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT11LM";
							}
					 	}
					 	else
					 	{
					 		//제품이송하차
							if ("2".equals(CarldPntCd.substring(1, 2)))
							{
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT22LM";
							}
							else
							{
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT21LM";
							}
					 	}

						jrInTemp = JDTORecordFactory.getInstance().create();
						jrInTemp.setField("YD_SCH_CD",		ydSchCd);
						jrInTemp.setField("YD_CAR_SCH_ID",	ydCarSchId);
						jrInTemp.setField("YD_STK_COL_GP",	ydStkColGp);
						jrInTemp.setField("CARD_NO",		ydCardNo);
						jrInTemp.setField("CAR_NO",			ydCarNo);

		    			String ydWbookId = yfComm.procCarWkBookInsert(jrInTemp);

		    			if(ydWbookId.equals(YfConstant.RETN_CD_FAILURE))
		    			{
		    				throw new Exception("작업예약ID 생성 실패");
		    			}

		    			//----------------------------------------------------------------------
						// 야드저장위치제원(YFF1L001) 전문전송
						//----------------------------------------------------------------------
		    			jrInTemp.setField("YD_INFO_SYNC_CD",	"3");			//야드정보동기화코드(3:열)
		    			jrInTemp.setField("YD_STK_COL_GP",		ydStkColGp);	//야드적치열구분
						jrRtn = commUtils.addSndData(commDao.getMsgL2("YFF1L001", jrInTemp));

						//------------------------------------------------------------------------------------------------------------
				    	//	차량스케줄 도착상태 변경 처리
				    	//------------------------------------------------------------------------------------------------------------
					    jrInTemp = JDTORecordFactory.getInstance().create();
					    jrInTemp.setResultCode(logId);		//Log ID
					    jrInTemp.setResultMsg(methodNm);	//Log Method Name
						jrInTemp.setField("MODIFIER",			modifier);	//수정자
						jrInTemp.setField("YD_CAR_SCH_ID",		ydCarSchId);
						jrInTemp.setField("YD_CARUD_ARR_DT",	commUtils.getDateTime14());
						jrInTemp.setField("YD_CAR_PROG_STAT",	"B");		//하차도착상태
						commDao.update(jrInTemp, updYdCarsch, logId, methodNm, "TB_YD_CARSCH 차량 상태변경");

						//------------------------------------------------------------------------------------------------------------
				    	//	재료정보 조회 (2단,1단 순)
				    	//------------------------------------------------------------------------------------------------------------
						JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
						jrParam1.setResultCode(logId);		//Log ID
			 			jrParam1.setResultMsg(methodNm);	//Log Method Name
						jrParam1.setField("TRANS_ORD_DATE",		transOrdDt);
						jrParam1.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
			 			JDTORecordSet jsCarMtl = commDao.select(jrParam1, getYfStockOfCarLoad, logId, methodNm, "재료정보 조회");

						/**********************************************************
						* Crane스케줄 호출
						*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
						*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
						**********************************************************/
						jrParam.setField("YD_STK_COL_GP",		ydStkColGp);
						JDTORecordSet rsResult = commDao.select(jrParam, getCarPntFrmYn, logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 ");

						ydFrmYn = rsResult.getRecord(0).getFieldString("YD_FRM_YN");

						//2020.02.25 정종균과장요청 형상유무 사용인 경우에만 차량예정정보 송신
						//if("Y".equals(ydFrmYn))
						//{
							// 차량예정정보 송신
							jrParam = JDTORecordFactory.getInstance().create();
							jrParam.setResultCode(logId);	//Log ID
							jrParam.setResultMsg(methodNm);	//Log Method Name
				 			jrParam.setField("MODIFIER",		modifier);		//수정자
							jrParam.setField("YD_CAR_SCH_ID",	ydCarSchId);	//야드차량스케쥴ID
							jrParam.setField("SEARCH_FLAG",		"2");			//1:상차도, 2:차량스케쥴 ID
							jrRtn = commUtils.addSndData(jrRtn, yfComm.procCarPlanInfo(jrParam));	//YFF1L008 생성
						//}

						//차량형상 사용안하면 크레인 스케줄 기동
						if("N".equals(ydFrmYn))
						{
							JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();

							//크레인 스케줄 기동 YFYFJ303 호출
							jrCrnSchMsg.setField("JMS_TC_CD",			"YFYFJ303");
							jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14()); //JMSTC생성일시
							jrCrnSchMsg.setField("YD_SCH_CD",			""); //야드스케쥴코드
							jrCrnSchMsg.setField("YD_EQP_ID",			""); //야드설비ID

							int pcnt = 0;

							//2단 적치된 대상 스케줄 호출
							for(int i = 0; i < jsCarMtl.size(); i++)
							{
								if("02".equals(jsCarMtl.getRecord(i).getFieldString("YD_STK_LYR_NO")))
								{
									jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
								}
							}

							//1단 적치된 대상 스케줄 호출
							for(int i = 0; i < jsCarMtl.size(); i++)
							{
								if("01".equals(jsCarMtl.getRecord(i).getFieldString("YD_STK_LYR_NO")))
								{
									jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
								}
							}

							jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(pcnt));

							jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
						}

						//저장품제원정보 송신 시작
						for(int i = 0; i < jsCarMtl.size(); i++)
						{
							//======================================================
							// 저장품제원 : 코일야드L2 로 송신(YFF1L002)
							//======================================================
							JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
							sndL2Msg.setResultCode(logId);		//Log ID
							sndL2Msg.setResultMsg(methodNm);	//Log Method Name
							sndL2Msg.setField("MSG_GP",				"I");	//전문구분
							sndL2Msg.setField("YD_INFO_SYNC_CD",	"R");	//야드정보동기화코드
							sndL2Msg.setField("STL_NO",				jsCarMtl.getRecord(i).getFieldString("STL_NO"));	//재료번호

							jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));		//전송 Data 생성
						}
						//저장품제원정보 송신 끝

					}
				}
			}


//			/***************************************
//			 * 	1.작업예약이 생성
//			 **************************************/
//			//작업예약,작업재료 등록
//			String ydSchCd  = "";
//			if ("3".equals(ydStkColGp.substring(5, 6)) || "4".equals(ydStkColGp.substring(5, 6)))
//			{
//				ydSchCd  = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT06LM";
//			}
//			else
//			{
//				ydSchCd  = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT02LM";
//			}
//
//			jrInTemp.setField("YD_SCH_CD",	ydSchCd);// 자동이적
//			jrInTemp.setField("CAR_NO",		ydCarNo);
//			jrInTemp.setField("CARD_NO",	ydCardNo);
//
//			String ydWbookId = yfComm.procCarWkBookInsert(jrInTemp);
//
//			if(ydWbookId.equals(YfConstant.RETN_CD_FAILURE))
//			{
//				throw new Exception("작업예약ID 생성 실패");
//			}
//
//			/**********************************************************
//			 * 2.2 크레인스케줄 전문 호출
//			 **********************************************************/
//			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//			jrYdMsg.setResultCode(logId);	//Log ID
//			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
//
//			jrYdMsg.setField("YD_WBOOK_ID",		ydWbookId); //야드작업예약ID
//			jrYdMsg.setField("YD_SCH_CD",		ydSchCd); //야드스케쥴코드
//			jrYdMsg.setField("YD_SCH_ST_GP",	"O"); //야드스케쥴기동구분
//			jrYdMsg.setField("YD_SCH_REQ_GP",	"L"); //야드스케쥴요청구분(인출)
//			jrYdMsg.setField("MODIFIER",		modifier); //수정자
//
//			jrRtn = commUtils.addSndData(yfComm.getCrnSchMsg(jrYdMsg));
//
//			//도착위치가 없을 시 도착처리 skip
//			if("TT".equals(ydCarKind)||"T".equals(ydCarKind))
//			{
//				ydCarKind="T";
//			}
//			else
//			{
//				if("TR".equals(ydCarKind))
//				{
//					ydCarKind="R";
//				}
//				else
//				{
//					ydCarKind="N";
//				}
//			}
//
//			if(!"".equals(ydStkColGp))
//			{
//				//차량도착처리
//				JDTORecord	jrRst = JDTORecordFactory.getInstance().create();
//				jrParam.setField("MOVE_GP",			ydCarKind); //T: TT,TR:R,그외:N
//				jrParam.setField("CARD_NO",			ydCardNo);
//				jrParam.setField("YD_STK_COL_GP",	ydStkColGp);
//				EJBConnector ejbConn = new EJBConnector("default","YfCommCarMvBakSeEJB",this);
//				jrRst = (JDTORecord)ejbConn.trx("procCarArr", new Class[] { JDTORecord.class }, new Object[] { jrParam });
//			}

			commUtils.printLog(logId, "=============코일이송하차대기장도착PDA 수신 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
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
	public JDTORecord rcvDMYDR075(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "코일이송하차완료PDA 수신[ACoilRevL3SeEJB.rcvDMYDR075] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
	    int			intRtnVal	= 0;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일이송하차완료PDA 수신 시작========", "SL");

			//수신 항목 값
			String ydGp			= commUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String stlAppearGp	= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));   //재료외형
			String Stl_no 		= commUtils.trim(rcvMsg.getFieldString("STL_NO"));    		//재료번호

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			jrParam.setField("STL_NO",		Stl_no);
			jrParam.setField("MODIFIER",	modifier); //수정자
			JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);
			jrParam.setField("STOCK_MOVE_TERM",	commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));
			intRtnVal=	commDao.update(jrParam, updYfStock, logId, methodNm, "TB_YF_STOCK 수정");

			if(intRtnVal == 0)
			{
				commUtils.printLog(logId, methodNm +" STL_NO : " + Stl_no +"에 대한 저장품 DATA가 존재하지 않음", "SL");
				return jrRtn;
			}

			/*
		 	 *차량  자동출발 모듈 CALL
		 	 */
			String ydCardNo  	= "";
			String ydStackColGp = "";

			if("*".equals(stlAppearGp))
			{
				JDTORecordSet jsCarSch = commDao.select(jrParam, getCarStartOrderInfo, logId, methodNm, "차량스케쥴 조회");

				if(jsCarSch.size() > 0)
				{
					commUtils.printLog(logId, methodNm +" STL_NO : " + Stl_no +"에 대한 포인트정보가 존재함", "SL");
					ydCardNo  		= commUtils.trim(jsCarSch.getRecord(0).getFieldString("CARD_NO"));
					ydStackColGp	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_STK_COL_GP"));

					jrParam.setField("CARD_NO",			ydCardNo);
					jrParam.setField("YD_STK_COL_GP",	ydStackColGp);

		 			if(!"".equals(ydCardNo))
		 			{
						//이송차량 출발 처리
						EJBConnector ejbConn1 = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
						JDTORecord jrRtn1 = (JDTORecord)ejbConn1.trx("procFrtoCarLevWr", new Class[] { JDTORecord.class }, new Object[] { jrParam });

						jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
		 			}
				}
			}

			String CarDnWrkYn 	= "N"; // 하차작업 여부

			if("DMYDR075".equals(msgId))
			{
				CarDnWrkYn = "Y";  //하차작업완료 인경우 생략 ,
			}

			if("N".equals(CarDnWrkYn))
			{
				JDTORecordSet jsStacklayer = commDao.select(jrParam, getStacklayerList, logId, methodNm, "단정보검색");

				if(jsStacklayer.size() > 0 )
				{
					commDao.update(jrParam, updateStacklayer, logId, methodNm, "TB_YF_STKLYR 수정");
				}
			}

			commUtils.printLog(logId, "=============코일이송하차완료PDA 수신 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 냉연코일정보수신(CRYFJ001)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvCRYFJ001(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "냉연코일정보수신[ACoilRevL3SeEJB.rcvCRYFRJ001] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();	//method 종료시 리턴하는 JDTORecord
	    int			intRtnVal	= 0;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============냉연코일정보수신 시작========", "SL");

			//수신 항목 값
			String Coil_no                  = commUtils.trim(rcvMsg.getFieldString("COIL_NO"));                     //코일번호
			String Rcd_sts_tp               = commUtils.trim(rcvMsg.getFieldString("RCD_STS_TP"));                  //Record상태구분
			String Coil_inf_crt_tp          = commUtils.trim(rcvMsg.getFieldString("COIL_INF_CRT_TP"));             //코일정보생성구분
			String Coil_inf_end_tp          = commUtils.trim(rcvMsg.getFieldString("COIL_INF_END_TP"));             //코일정보종료구분
			String Wk_prg_sts               = commUtils.trim(rcvMsg.getFieldString("WK_PRG_STS"));                  //작업진행Status
			String Prg_cd                   = commUtils.trim(rcvMsg.getFieldString("PRG_CD"));                      //진도코드
			String Ln_tp                    = commUtils.trim(rcvMsg.getFieldString("LN_TP"));                       //호기구분
			String Bf_prg_cd                = commUtils.trim(rcvMsg.getFieldString("BF_PRG_CD"));                   //전진도코드
			String Bf_ln_tp                 = commUtils.trim(rcvMsg.getFieldString("BF_LN_TP"));                    //전호기구분
			String Coil_act_thk             = commUtils.trim(rcvMsg.getFieldString("COIL_ACT_THK"));                //코일실적두께
			String Coil_act_thk_mnv         = commUtils.trim(rcvMsg.getFieldString("COIL_ACT_THK_MNV"));            //코일실적두께최소값
			String Coil_act_thk_mxv         = commUtils.trim(rcvMsg.getFieldString("COIL_ACT_THK_MXV"));            //코일실적두께최대값
			String Coil_act_wth             = commUtils.trim(rcvMsg.getFieldString("COIL_ACT_WTH"));                //코일실적폭
			String Coil_act_wth_mnv         = commUtils.trim(rcvMsg.getFieldString("COIL_ACT_WTH_MNV"));            //코일실적폭최소값
			String Coil_act_wth_mxv         = commUtils.trim(rcvMsg.getFieldString("COIL_ACT_WTH_MXV"));            //코일실적폭최대값
			String Coil_act_lth             = commUtils.trim(rcvMsg.getFieldString("COIL_ACT_LTH"));                //코일실적길이
			String Coil_rmw_wgt             = commUtils.trim(rcvMsg.getFieldString("COIL_RMW_WGT"));                //코일실평중량
			String Coil_thy_wgt             = commUtils.trim(rcvMsg.getFieldString("COIL_THY_WGT"));                //코일이론중량
			String Coil_grs_rmw_wgt         = commUtils.trim(rcvMsg.getFieldString("COIL_GRS_RMW_WGT"));            //코일Gross실평중량
			String Coil_grs_thy_wgt         = commUtils.trim(rcvMsg.getFieldString("COIL_GRS_THY_WGT"));            //코일Gross이론중량
			String Coil_idia                = commUtils.trim(rcvMsg.getFieldString("COIL_IDIA"));                   //코일내경
			String Coil_odia                = commUtils.trim(rcvMsg.getFieldString("COIL_ODIA"));                   //코일외경
			String Hc_aw                    = commUtils.trim(rcvMsg.getFieldString("HC_AW"));                       //HC안배중량
			String Cc_aw                    = commUtils.trim(rcvMsg.getFieldString("CC_AW"));                       //CC안배중량
			String Rem_proc                 = commUtils.trim(rcvMsg.getFieldString("REM_PROC"));                    //잔공정
			String Nxt_proc_cd              = commUtils.trim(rcvMsg.getFieldString("NXT_PROC_CD"));                 //차공정코드
			String Bf_proc_cd               = commUtils.trim(rcvMsg.getFieldString("BF_PROC_CD"));                  //전공정코드
			String Req_nxt_proc_cd          = commUtils.trim(rcvMsg.getFieldString("REQ_NXT_PROC_CD"));             //요구차공정코드
			String Req_nxt_proc_cau_cd      = commUtils.trim(rcvMsg.getFieldString("REQ_NXT_PROC_CAU_CD"));         //요구차공정원인코드
			String Act_pas_proc             = commUtils.trim(rcvMsg.getFieldString("ACT_PAS_PROC"));                //실적통과공정
			String Prdn_cd                  = commUtils.trim(rcvMsg.getFieldString("PRDN_CD"));                     //품명코드
			String Ord_no                   = commUtils.trim(rcvMsg.getFieldString("ORD_NO"));                      //주문번호
			String Ord_ln                   = commUtils.trim(rcvMsg.getFieldString("ORD_LN"));                      //주문행번
			String Ord_no_2                 = commUtils.trim(rcvMsg.getFieldString("ORD_NO_2"));                    //주문번호_2
			String Ord_ln_2                 = commUtils.trim(rcvMsg.getFieldString("ORD_LN_2"));                    //주문행번_2
			String Pl_ord_yn                = commUtils.trim(rcvMsg.getFieldString("PL_ORD_YN"));                   //복수주문여부
			String Bf_ord_no                = commUtils.trim(rcvMsg.getFieldString("BF_ORD_NO"));                   //전주문번호
			String Bf_ord_ln                = commUtils.trim(rcvMsg.getFieldString("BF_ORD_LN"));                   //전주문행번
			String Obj_chg_apt_dh           = commUtils.trim(rcvMsg.getFieldString("OBJ_CHG_APT_DH"));              //목전충당일시
			String Wgt_dcs_mth_tp           = commUtils.trim(rcvMsg.getFieldString("WGT_DCS_MTH_TP"));              //중량결정법구분
			String Crm_mnf_std_cd           = commUtils.trim(rcvMsg.getFieldString("CRM_MNF_STD_CD"));              //냉연제조표준코드
			String Mtl_cd                   = commUtils.trim(rcvMsg.getFieldString("MTL_CD"));                      //MaterialCode
			String Rmtl_sym                 = commUtils.trim(rcvMsg.getFieldString("RMTL_SYM"));                    //원자재기호
			String Coil_mrg_tp              = commUtils.trim(rcvMsg.getFieldString("COIL_MRG_TP"));                 //코일병합구분
			String Fst_rmtl_no              = commUtils.trim(rcvMsg.getFieldString("FST_RMTL_NO"));                 //최초원자재번호
			String Rpv_rmtl_no              = commUtils.trim(rcvMsg.getFieldString("RPV_RMTL_NO"));                 //대표원자재번호
			String Rmtl_no_1                = commUtils.trim(rcvMsg.getFieldString("RMTL_NO_1"));                   //원자재번호_1
			String Rmtl_lth_1               = commUtils.trim(rcvMsg.getFieldString("RMTL_LTH_1"));                  //원자재길이_1
			String Rmtl_wgt_1               = commUtils.trim(rcvMsg.getFieldString("RMTL_WGT_1"));                  //원자재중량_1
			String Rmtl_no_2                = commUtils.trim(rcvMsg.getFieldString("RMTL_NO_2"));                   //원자재번호_2
			String Rmtl_lth_2               = commUtils.trim(rcvMsg.getFieldString("RMTL_LTH_2"));                  //원자재길이_2
			String Rmtl_wgt_2               = commUtils.trim(rcvMsg.getFieldString("RMTL_WGT_2"));                  //원자재중량_2
			String Rmtl_no_3                = commUtils.trim(rcvMsg.getFieldString("RMTL_NO_3"));                   //원자재번호_3
			String Rmtl_lth_3               = commUtils.trim(rcvMsg.getFieldString("RMTL_LTH_3"));                  //원자재길이_3
			String Rmtl_wgt_3               = commUtils.trim(rcvMsg.getFieldString("RMTL_WGT_3"));                  //원자재중량_3
			String Rmtl_slp_cd              = commUtils.trim(rcvMsg.getFieldString("RMTL_SLP_CD"));                 //원자재공급사코드
			String Rmtl_heat_no             = commUtils.trim(rcvMsg.getFieldString("RMTL_HEAT_NO"));                //원자재Heat번호
			String Slp_rmtl_no              = commUtils.trim(rcvMsg.getFieldString("SLP_RMTL_NO"));                 //공급사원자재번호
			String Pl_wld_prt               = commUtils.trim(rcvMsg.getFieldString("PL_WLD_PRT"));                  //산세용접부
			String Pl_wld_cnt               = commUtils.trim(rcvMsg.getFieldString("PL_WLD_CNT"));                  //산세용접수
			String Pl_wld_pnt_loc_1         = commUtils.trim(rcvMsg.getFieldString("PL_WLD_PNT_LOC_1"));            //산세용접점위치_1
			String Pl_wld_pnt_loc_2         = commUtils.trim(rcvMsg.getFieldString("PL_WLD_PNT_LOC_2"));            //산세용접점위치_2
			String Pl_wld_pnt_loc_3         = commUtils.trim(rcvMsg.getFieldString("PL_WLD_PNT_LOC_3"));            //산세용접점위치_3
			String Pl_wld_pnt_loc_4         = commUtils.trim(rcvMsg.getFieldString("PL_WLD_PNT_LOC_4"));            //산세용접점위치_4
			String Pl_wld_pnt_loc_5         = commUtils.trim(rcvMsg.getFieldString("PL_WLD_PNT_LOC_5"));            //산세용접점위치_5
			String Prd_wld_cnt              = commUtils.trim(rcvMsg.getFieldString("PRD_WLD_CNT"));                 //제품용접수
			String Mid_wld_pnt_loc_1        = commUtils.trim(rcvMsg.getFieldString("MID_WLD_PNT_LOC_1"));           //중간용접점위치_1
			String Mid_wld_pnt_loc_2        = commUtils.trim(rcvMsg.getFieldString("MID_WLD_PNT_LOC_2"));           //중간용접점위치_2
			String Mid_wld_pnt_loc_3        = commUtils.trim(rcvMsg.getFieldString("MID_WLD_PNT_LOC_3"));           //중간용접점위치_3
			String Ord_pdn_tp               = commUtils.trim(rcvMsg.getFieldString("ORD_PDN_TP"));                  //주문생산구분
			String Urg_mtl_tp               = commUtils.trim(rcvMsg.getFieldString("URG_MTL_TP"));                  //긴급재구분
			String Coil_top_bot_tp          = commUtils.trim(rcvMsg.getFieldString("COIL_TOP_BOT_TP"));             //코일TopBottom구분
			String Dmy_ptt_plt_tp           = commUtils.trim(rcvMsg.getFieldString("DMY_PTT_PLT_TP"));              //Dummy보호판구분
			String Sem_prd_tp               = commUtils.trim(rcvMsg.getFieldString("SEM_PRD_TP"));                  //중간재구분
			String Pcm_ml_cln_yn            = commUtils.trim(rcvMsg.getFieldString("PCM_ML_CLN_YN"));               //PCMMillClean여부
			String Pin_hole_tp              = commUtils.trim(rcvMsg.getFieldString("PIN_HOLE_TP"));                 //PinHole구분
			String Dmy_coil_use_cnt         = commUtils.trim(rcvMsg.getFieldString("DMY_COIL_USE_CNT"));            //Dummy코일사용횟수
			String Prd_auc_prg_sts_tp       = commUtils.trim(rcvMsg.getFieldString("PRD_AUC_PRG_STS_TP"));          //제품경매진행상태구분
			String Prd_auc_rgs_dh           = commUtils.trim(rcvMsg.getFieldString("PRD_AUC_RGS_DH"));              //제품경매등록일시
			String Coil_mtl_out_shp_tp      = commUtils.trim(rcvMsg.getFieldString("COIL_MTL_OUT_SHP_TP"));         //코일재료외형구분
			String Hld_yn                   = commUtils.trim(rcvMsg.getFieldString("HLD_YN"));                      //보류여부
			String Hld_rgs_dh               = commUtils.trim(rcvMsg.getFieldString("HLD_RGS_DH"));                  //보류등록일시
			String Erp_reg_yn               = commUtils.trim(rcvMsg.getFieldString("ERP_REG_YN"));                  //ERP등록여부
			String Lod_loc                  = commUtils.trim(rcvMsg.getFieldString("LOD_LOC"));                     //적재위치
			String Bf_lod_loc               = commUtils.trim(rcvMsg.getFieldString("BF_LOD_LOC"));                  //전적재위치
			String Stk_no                   = commUtils.trim(rcvMsg.getFieldString("STK_NO"));                      //Stack번호
			String Base_no                  = commUtils.trim(rcvMsg.getFieldString("BASE_NO"));                     //Base번호
			String Sht_cnt                  = commUtils.trim(rcvMsg.getFieldString("SHT_CNT"));                     //Sheet매수
			String Coil_slv_use_yn          = commUtils.trim(rcvMsg.getFieldString("COIL_SLV_USE_YN"));             //코일Sleeve사용여부
			String Spm_use_yn               = commUtils.trim(rcvMsg.getFieldString("SPM_USE_YN"));                  //SPM사용여부
			String Pl_st_yn                 = commUtils.trim(rcvMsg.getFieldString("PL_ST_YN"));                    //산세ST여부
			String Ann_st_yn                = commUtils.trim(rcvMsg.getFieldString("ANN_ST_YN"));                   //소둔ST여부
			String Cor_st_yn                = commUtils.trim(rcvMsg.getFieldString("COR_ST_YN"));                   //정정ST여부
			String Ord_spl_tp               = commUtils.trim(rcvMsg.getFieldString("ORD_SPL_TP"));                  //주여구분
			String Spl_dh                   = commUtils.trim(rcvMsg.getFieldString("SPL_DH"));                      //여재일시
			String Spl_cau_cd               = commUtils.trim(rcvMsg.getFieldString("SPL_CAU_CD"));                  //여재원인코드
			String Coil_sur_grd             = commUtils.trim(rcvMsg.getFieldString("COIL_SUR_GRD"));                //코일표면등급
			String Coil_shp_grd             = commUtils.trim(rcvMsg.getFieldString("COIL_SHP_GRD"));                //코일형상등급
			String Coil_sz_grd              = commUtils.trim(rcvMsg.getFieldString("COIL_SZ_GRD"));                 //코일칫수등급
			String Coil_unt_wgt_grd         = commUtils.trim(rcvMsg.getFieldString("COIL_UNT_WGT_GRD"));            //코일단중등급
			String Coil_apr_ins_grd         = commUtils.trim(rcvMsg.getFieldString("COIL_APR_INS_GRD"));            //코일외관검사등급
			String Coil_mql_grd             = commUtils.trim(rcvMsg.getFieldString("COIL_MQL_GRD"));                //코일재질등급
			String Coil_mql_grd_cau_cd      = commUtils.trim(rcvMsg.getFieldString("COIL_MQL_GRD_CAU_CD"));         //코일재질등급원인코드
			String Coil_mql_syn_grd         = commUtils.trim(rcvMsg.getFieldString("COIL_MQL_SYN_GRD"));            //코일재질종합등급
			String Coil_ing_grd             = commUtils.trim(rcvMsg.getFieldString("COIL_ING_GRD"));                //코일성분등급
			String Coil_qlt_trk_grd         = commUtils.trim(rcvMsg.getFieldString("COIL_QLT_TRK_GRD"));            //코일품질Tracking등급
			String Op_grd                   = commUtils.trim(rcvMsg.getFieldString("OP_GRD"));                      //OP등급
			String Coil_apr_ins_grd_cau_cd  = commUtils.trim(rcvMsg.getFieldString("COIL_APR_INS_GRD_CAU_CD"));     //코일외관검사등급원인코드
			String Coil_arp_ins_dh          = commUtils.trim(rcvMsg.getFieldString("COIL_ARP_INS_DH"));             //코일외관검사일시
			String Prd_syn_grd              = commUtils.trim(rcvMsg.getFieldString("PRD_SYN_GRD"));                 //제품종합등급
			String Prd_syn_grd_cau_cd       = commUtils.trim(rcvMsg.getFieldString("PRD_SYN_GRD_CAU_CD"));          //제품종합등급원인코드
			String Coil_clr_mpr_grd         = commUtils.trim(rcvMsg.getFieldString("COIL_CLR_MPR_GRD"));            //코일칼라물성등급
			String Prd_syn_jdg_apt_dd       = commUtils.trim(rcvMsg.getFieldString("PRD_SYN_JDG_APT_DD"));          //제품종합판정계상일
			String Prd_syn_jdg_dh           = commUtils.trim(rcvMsg.getFieldString("PRD_SYN_JDG_DH"));              //제품종합판정일시
			String Mql_tstp_no              = commUtils.trim(rcvMsg.getFieldString("MQL_TSTP_NO"));                 //재질시편번호
			String Mpr_tstp_no              = commUtils.trim(rcvMsg.getFieldString("MPR_TSTP_NO"));                 //물성시편번호
			String Smp_gth_cnt              = commUtils.trim(rcvMsg.getFieldString("SMP_GTH_CNT"));                 //Sample채취매수
			String Spc_avr                  = commUtils.trim(rcvMsg.getFieldString("SPC_AVR"));                     //규격약호
			String Mql_sym                  = commUtils.trim(rcvMsg.getFieldString("MQL_SYM"));                     //재질기호
			String Coil_mqc                 = commUtils.trim(rcvMsg.getFieldString("COIL_MQC"));                    //코일재질특성
			String Gw_asg_cd                = commUtils.trim(rcvMsg.getFieldString("GW_ASG_CD"));                   //도금량지정코드
			String Wk_gw_frn                = commUtils.trim(rcvMsg.getFieldString("WK_GW_FRN"));                   //작업도금량전면
			String Wk_gw_bak                = commUtils.trim(rcvMsg.getFieldString("WK_GW_BAK"));                   //작업도금량후면
			String Ord_gw_asg_cd            = commUtils.trim(rcvMsg.getFieldString("ORD_GW_ASG_CD"));               //주문도금량지정코드
			String Ord_gw_frn               = commUtils.trim(rcvMsg.getFieldString("ORD_GW_FRN"));                  //주문도금량전면
			String Ord_gw_bak               = commUtils.trim(rcvMsg.getFieldString("ORD_GW_BAK"));                  //주문도금량후면
			String Coil_gal_ptr_cd          = commUtils.trim(rcvMsg.getFieldString("COIL_GAL_PTR_CD"));             //코일도금후처리코드
			String Coil_clr_ptr_cd          = commUtils.trim(rcvMsg.getFieldString("COIL_CLR_PTR_CD"));             //코일칼라후처리코드
			String Coil_sur_phm_cd          = commUtils.trim(rcvMsg.getFieldString("COIL_SUR_PHM_CD"));             //코일표면사상코드
			String Coil_rou_cd              = commUtils.trim(rcvMsg.getFieldString("COIL_ROU_CD"));                 //코일조도코드
			String Ord_usg_cd               = commUtils.trim(rcvMsg.getFieldString("ORD_USG_CD"));                  //주문용도코드
			String Coil_oil_pnt_cd          = commUtils.trim(rcvMsg.getFieldString("COIL_OIL_PNT_CD"));             //코일도유코드
			String Coil_ptt_flm_cd          = commUtils.trim(rcvMsg.getFieldString("COIL_PTT_FLM_CD"));             //코일보호필름코드
			String Crt_no                   = commUtils.trim(rcvMsg.getFieldString("CRT_NO"));                      //CRT번호
			String Pas_proc_cd_1            = commUtils.trim(rcvMsg.getFieldString("PAS_PROC_CD_1"));               //통과공정코드_1
			String Coil_rproc_cnt_1         = commUtils.trim(rcvMsg.getFieldString("COIL_RPROC_CNT_1"));            //코일재처리횟수_1
			String Coil_no_1                = commUtils.trim(rcvMsg.getFieldString("COIL_NO_1"));                   //코일번호_1
			String Pas_proc_cd_2            = commUtils.trim(rcvMsg.getFieldString("PAS_PROC_CD_2"));               //통과공정코드_2
			String Coil_rproc_cnt_2         = commUtils.trim(rcvMsg.getFieldString("COIL_RPROC_CNT_2"));            //코일재처리횟수_2
			String Coil_no_2                = commUtils.trim(rcvMsg.getFieldString("COIL_NO_2"));                   //코일번호_2
			String Pas_proc_cd_3            = commUtils.trim(rcvMsg.getFieldString("PAS_PROC_CD_3"));               //통과공정코드_3
			String Coil_rproc_cnt_3         = commUtils.trim(rcvMsg.getFieldString("COIL_RPROC_CNT_3"));            //코일재처리횟수_3
			String Coil_no_3                = commUtils.trim(rcvMsg.getFieldString("COIL_NO_3"));                   //코일번호_3
			String Pas_proc_cd_4            = commUtils.trim(rcvMsg.getFieldString("PAS_PROC_CD_4"));               //통과공정코드_4
			String Coil_rproc_cnt_4         = commUtils.trim(rcvMsg.getFieldString("COIL_RPROC_CNT_4"));            //코일재처리횟수_4
			String Coil_no_4                = commUtils.trim(rcvMsg.getFieldString("COIL_NO_4"));                   //코일번호_4
			String Pas_proc_cd_5            = commUtils.trim(rcvMsg.getFieldString("PAS_PROC_CD_5"));               //통과공정코드_5
			String Coil_rproc_cnt_5         = commUtils.trim(rcvMsg.getFieldString("COIL_RPROC_CNT_5"));            //코일재처리횟수_5
			String Coil_no_5                = commUtils.trim(rcvMsg.getFieldString("COIL_NO_5"));                   //코일번호_5
			String Pas_proc_cd_6            = commUtils.trim(rcvMsg.getFieldString("PAS_PROC_CD_6"));               //통과공정코드_6
			String Coil_rproc_cnt_6         = commUtils.trim(rcvMsg.getFieldString("COIL_RPROC_CNT_6"));            //코일재처리횟수_6
			String Coil_no_6                = commUtils.trim(rcvMsg.getFieldString("COIL_NO_6"));                   //코일번호_6
			String Pas_proc_cd_7            = commUtils.trim(rcvMsg.getFieldString("PAS_PROC_CD_7"));               //통과공정코드_7
			String Coil_rproc_cnt_7         = commUtils.trim(rcvMsg.getFieldString("COIL_RPROC_CNT_7"));            //코일재처리횟수_7
			String Coil_no_7                = commUtils.trim(rcvMsg.getFieldString("COIL_NO_7"));                   //코일번호_7
			String Pas_proc_cd_8            = commUtils.trim(rcvMsg.getFieldString("PAS_PROC_CD_8"));               //통과공정코드_8
			String Coil_rproc_cnt_8         = commUtils.trim(rcvMsg.getFieldString("COIL_RPROC_CNT_8"));            //코일재처리횟수_8
			String Coil_no_8                = commUtils.trim(rcvMsg.getFieldString("COIL_NO_8"));                   //코일번호_8
			String Pas_proc_cd_9            = commUtils.trim(rcvMsg.getFieldString("PAS_PROC_CD_9"));               //통과공정코드_9
			String Coil_rproc_cnt_9         = commUtils.trim(rcvMsg.getFieldString("COIL_RPROC_CNT_9"));            //코일재처리횟수_9
			String Coil_no_9                = commUtils.trim(rcvMsg.getFieldString("COIL_NO_9"));                   //코일번호_9
			String Pas_proc_cd_10           = commUtils.trim(rcvMsg.getFieldString("PAS_PROC_CD_10"));              //통과공정코드_10
			String Coil_rproc_cnt_10        = commUtils.trim(rcvMsg.getFieldString("COIL_RPROC_CNT_10"));           //코일재처리횟수_10
			String Coil_no_10               = commUtils.trim(rcvMsg.getFieldString("COIL_NO_10"));                  //코일번호_10
			String Poc_tp                   = commUtils.trim(rcvMsg.getFieldString("POC_TP"));                      //위탁가공구분
			String Poc_cmp_cd               = commUtils.trim(rcvMsg.getFieldString("POC_CMP_CD"));                  //위탁가공업체코드
			String Pak_equ_cd               = commUtils.trim(rcvMsg.getFieldString("PAK_EQU_CD"));                  //포장설비코드
			String Coil_pak_ptn             = commUtils.trim(rcvMsg.getFieldString("COIL_PAK_PTN"));                //코일포장양식
			String Coil_pak_apt_dd          = commUtils.trim(rcvMsg.getFieldString("COIL_PAK_APT_DD"));             //코일포장계상일
			String Coil_pak_cplt_dh         = commUtils.trim(rcvMsg.getFieldString("COIL_PAK_CPLT_DH"));            //코일포장완료일시
			String Coil_pak_shf_grp         = commUtils.trim(rcvMsg.getFieldString("COIL_PAK_SHF_GRP"));            //코일포장작업근조
			String Pak_wk_tp                = commUtils.trim(rcvMsg.getFieldString("PAK_WK_TP"));                   //포장작업구분
			String Lbl_pub_cnt              = commUtils.trim(rcvMsg.getFieldString("LBL_PUB_CNT"));                 //Label발행횟수
			String Lbl_pub_dh               = commUtils.trim(rcvMsg.getFieldString("LBL_PUB_DH"));                  //Label발행일시
			String Prd_whs_dh               = commUtils.trim(rcvMsg.getFieldString("PRD_WHS_DH"));                  //제품입고일시
			String Prd_whs_apt_dd           = commUtils.trim(rcvMsg.getFieldString("PRD_WHS_APT_DD"));              //제품입고계상일
			String Prd_dlv_req_knd_tp       = commUtils.trim(rcvMsg.getFieldString("PRD_DLV_REQ_KND_TP"));          //제품출고의뢰유형구분
			String Ctd_sal_tp               = commUtils.trim(rcvMsg.getFieldString("CTD_SAL_TP"));                  //제 품재고구분
			String Prd_dlv_knd_tp           = commUtils.trim(rcvMsg.getFieldString("PRD_DLV_KND_TP"));              //제품납품유형구분
			String Dlv_dh                   = commUtils.trim(rcvMsg.getFieldString("DLV_DH"));                      //출고일시
			String Prd_dlv_apt_dd           = commUtils.trim(rcvMsg.getFieldString("PRD_DLV_APT_DD"));              //제품출고계상일
			String Shpg_dh                  = commUtils.trim(rcvMsg.getFieldString("SHPG_DH"));                     //출하일시
			String Shpg_apt_dd              = commUtils.trim(rcvMsg.getFieldString("SHPG_APT_DD"));                 //출하계상일
			String Rea_inf_whs_tp           = commUtils.trim(rcvMsg.getFieldString("REA_INF_WHS_TP"));              //실물정보입고구분
			String Rea_inf_ret_tp           = commUtils.trim(rcvMsg.getFieldString("REA_INF_RET_TP"));              //실물정보반납구분
			String Prd_ret_dh               = commUtils.trim(rcvMsg.getFieldString("PRD_RET_DH"));                  //제품반납일시
			String Prd_ret_apt_dd           = commUtils.trim(rcvMsg.getFieldString("PRD_RET_APT_DD"));              //제품반납계상일
			String Prd_ret_prg_sts_tp       = commUtils.trim(rcvMsg.getFieldString("PRD_RET_PRG_STS_TP"));          //제품반납진행상태구분
			String Prd_ret_req_proc_cd      = commUtils.trim(rcvMsg.getFieldString("PRD_RET_REQ_PROC_CD"));         //제품반납요구공정코드
			String Prd_ret_req_prg_cd       = commUtils.trim(rcvMsg.getFieldString("PRD_RET_REQ_PRG_CD"));          //제품반납요구진도코드
			String Prd_ret_req_cau_cd       = commUtils.trim(rcvMsg.getFieldString("PRD_RET_REQ_CAU_CD"));          //제품반납요구원인코드
			String Shpg_hld_tp              = commUtils.trim(rcvMsg.getFieldString("SHPG_HLD_TP"));                 //출하보류구분
			String Shpg_hld_dd              = commUtils.trim(rcvMsg.getFieldString("SHPG_HLD_DD"));                 //출하보류일
			String Shpg_hld_cau_txt         = commUtils.trim(rcvMsg.getFieldString("SHPG_HLD_CAU_TXT"));            //출하보류원인내역
			String Shpg_hld_cnl_dd          = commUtils.trim(rcvMsg.getFieldString("SHPG_HLD_CNL_DD"));             //출하보류해제일
			String Prd_long_stk_cau_cd      = commUtils.trim(rcvMsg.getFieldString("PRD_LONG_STK_CAU_CD"));         //제품장기재고원인코드
			String Prd_long_stk_rgs_dh      = commUtils.trim(rcvMsg.getFieldString("PRD_LONG_STK_RGS_DH"));         //제품장기재고등록일시
			String Rea_inf_cryn_tp          = commUtils.trim(rcvMsg.getFieldString("REA_INF_CRYN_TP"));             //실물정보반입구분
			String Prd_cryn_cau_tp          = commUtils.trim(rcvMsg.getFieldString("PRD_CRYN_CAU_TP"));             //제품반입원인코드
			String Prd_cryn_dh              = commUtils.trim(rcvMsg.getFieldString("PRD_CRYN_DH"));                 //제품반입일시
			String Prd_cryn_apt_dd          = commUtils.trim(rcvMsg.getFieldString("PRD_CRYN_APT_DD"));             //제품반입계상일
			String Dlv_prog_sts_tp          = commUtils.trim(rcvMsg.getFieldString("DLV_PROG_STS_TP"));             //출고진행상태
			String Ms_pub_yn                = commUtils.trim(rcvMsg.getFieldString("MS_PUB_YN"));                   //MS발행여부
			String Ms_pub_dh                = commUtils.trim(rcvMsg.getFieldString("MS_PUB_DH"));                   //MS발행일시
			String Pcoil_no                 = commUtils.trim(rcvMsg.getFieldString("PCOIL_NO"));                    //모코일번호
			String Trf_req_no               = commUtils.trim(rcvMsg.getFieldString("TRF_REQ_NO"));                  //이송의뢰번호
			String Trf_prg_sts_tp           = commUtils.trim(rcvMsg.getFieldString("TRF_PRG_STS_TP"));              //이송진행상태구분
			String Trf_whs_dh               = commUtils.trim(rcvMsg.getFieldString("TRF_WHS_DH"));                  //이송입고일시
			String Plnt_tp                  = commUtils.trim(rcvMsg.getFieldString("PLNT_TP"));                     //플랜트구분
			String Created_object_type      = commUtils.trim(rcvMsg.getFieldString("CREATED_OBJECT_TYPE"));         //생성Object유형
			String Created_object_id        = commUtils.trim(rcvMsg.getFieldString("CREATED_OBJECT_ID"));           //생성ObjectID
			String Created_program_id       = commUtils.trim(rcvMsg.getFieldString("CREATED_PROGRAM_ID"));          //생성프로그램ID
			String Creation_timestamp       = commUtils.trim(rcvMsg.getFieldString("CREATION_TIMESTAMP"));          //생성일시
			String Last_updated_object_type = commUtils.trim(rcvMsg.getFieldString("LAST_UPDATED_OBJECT_TYPE"));    //최종변경Object유형
			String Last_updated_object_id   = commUtils.trim(rcvMsg.getFieldString("LAST_UPDATED_OBJECT_ID"));      //최종변경ObjectID
			String Last_update_program_id   = commUtils.trim(rcvMsg.getFieldString("LAST_UPDATE_PROGRAM_ID"));      //최종변경프로그램ID
			String Last_update_timestamp    = commUtils.trim(rcvMsg.getFieldString("LAST_UPDATE_TIMESTAMP"));       //최종변경일시
			String Data_end_status          = commUtils.trim(rcvMsg.getFieldString("DATA_END_STATUS"));             //데이터종료여부
			String Data_end_object_type     = commUtils.trim(rcvMsg.getFieldString("DATA_END_OBJECT_TYPE"));        //데이타종료Object유형
			String Data_end_object_id       = commUtils.trim(rcvMsg.getFieldString("DATA_END_OBJECT_ID"));          //데이타종료ObjectID
			String Data_end_program_id      = commUtils.trim(rcvMsg.getFieldString("DATA_END_PROGRAM_ID"));         //데이타종료프로그램ID
			String Data_end_timestamp       = commUtils.trim(rcvMsg.getFieldString("DATA_END_TIMESTAMP"));          //데이터종료일시
			String Archive_completed_flag   = commUtils.trim(rcvMsg.getFieldString("ARCHIVE_COMPLETED_FLAG"));      //Archive완료여부
			String Archived_employee_num    = commUtils.trim(rcvMsg.getFieldString("ARCHIVED_EMPLOYEE_NUM"));       //Archive작업자사번
			String Archived_timestamp       = commUtils.trim(rcvMsg.getFieldString("ARCHIVED_TIMESTAMP"));          //Archive작업일시
			String Archive_program_id       = commUtils.trim(rcvMsg.getFieldString("ARCHIVE_PROGRAM_ID"));          //Archive프로그램ID
			String Ccl_cr_trt_tp            = commUtils.trim(rcvMsg.getFieldString("CCL_CR_TRT_TP"));               //CCL크롬재처리구분
			String In_plt_cvt_cau_cd        = commUtils.trim(rcvMsg.getFieldString("IN_PLT_CVT_CAU_CD"));           //내판전환원인코드
			String Ccl_2pass_yn             = commUtils.trim(rcvMsg.getFieldString("CCL_2PASS_YN"));                //2PASS여부
			String Coil_rmw_wgt_1           = commUtils.trim(rcvMsg.getFieldString("COIL_RMW_WGT_1"));              //코일실평중량1
			String Coil_rmw_wgt_2           = commUtils.trim(rcvMsg.getFieldString("COIL_RMW_WGT_2"));              //코일실평중량2
			String Sap_mtl_cd               = commUtils.trim(rcvMsg.getFieldString("SAP_MTL_CD"));                  //SAP Material Code
			String Itemname_cd              = commUtils.trim(rcvMsg.getFieldString("ITEMNAME_CD"));                 //통합품명코드
			String Pdn_plnt_tp              = commUtils.trim(rcvMsg.getFieldString("PDN_PLNT_TP"));                 //생산예정공장
			String Pln_pas_proc_cmn         = commUtils.trim(rcvMsg.getFieldString("PLN_PAS_PROC_CMN"));            //계획통과공정
			String Pas_plnt_act             = commUtils.trim(rcvMsg.getFieldString("PAS_PLNT_ACT"));                //통과공장실적
			String Inv_no                   = commUtils.trim(rcvMsg.getFieldString("INV_NO"));                      //송장번호
			String Lgs_bas_cd               = commUtils.trim(rcvMsg.getFieldString("LGS_BAS_CD"));                  //물류기지코드
			String Wip_mtl_cd               = commUtils.trim(rcvMsg.getFieldString("WIP_MTL_CD"));                  //재공MaterialCode
			String Ms_spe_yn                = commUtils.trim(rcvMsg.getFieldString("MS_SPE_YN"));                   //특별채용여부
			String Under_coil_yn            = commUtils.trim(rcvMsg.getFieldString("UNDER_COIL_YN"));               //Under작업 유무
			String Under_coil_pln_pas       = commUtils.trim(rcvMsg.getFieldString("UNDER_COIL_PLN_PAS"));          //Under작업 등록공정
			String Under_coil_object_id     = commUtils.trim(rcvMsg.getFieldString("UNDER_COIL_OBJECT_ID"));        //Under작업 등록자
			String Cor_cau_txt              = commUtils.trim(rcvMsg.getFieldString("COR_CAU_TXT"));                 //정정원인내역
			String Poc_tar_mtl              = commUtils.trim(rcvMsg.getFieldString("POC_TAR_MTL"));                 //위탁가공대상재
			String Bldp_tp                  = commUtils.trim(rcvMsg.getFieldString("BLDP_TP"));                     //Buildup구분
			String Trn_eqp_cd				= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"));					//운송장비코드
			//String Car_no					= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));						//차량번호...출하는 출하전문에서 받을듯?
			//String Cr_car_no				= commUtils.trim(rcvMsg.getFieldString("CR_CAR_NO"));					//냉연차량번호...출하는 출하전문에서 받을듯?
			String Prd_lod_rnk				= commUtils.trim(rcvMsg.getFieldString("PRD_LOD_RNK"));					//제품상차순위
			//String Car_ldud_gp				= commUtils.trim(rcvMsg.getFieldString("CAR_LDUD_GP"));					//차량_상차하차_구분(L:상차/U:하차)...필요할지???

			//1. 쿼리용 변수 등록
			jrParam.setField("REGISTER",                	modifier);
			jrParam.setField("STL_NO",                  	Coil_no);                   //냉연코일번호

			jrParam.setField("COIL_NO",                 	Coil_no);                   //냉연코일번호
			jrParam.setField("RCD_STS_TP",              	Rcd_sts_tp);                //Record상태구분
			jrParam.setField("COIL_INF_CRT_TP",         	Coil_inf_crt_tp);           //코일정보생성구분
			jrParam.setField("COIL_INF_END_TP",         	Coil_inf_end_tp);           //코일정보종료구분
			jrParam.setField("WK_PRG_STS",              	Wk_prg_sts);                //작업진행Status
			jrParam.setField("PRG_CD",                  	Prg_cd);                    //진도코드
			jrParam.setField("LN_TP",                   	Ln_tp);                     //호기구분
			jrParam.setField("BF_PRG_CD",               	Bf_prg_cd);                 //전진도코드
			jrParam.setField("BF_LN_TP",                	Bf_ln_tp);                  //전호기구분
			jrParam.setField("COIL_ACT_THK",            	Coil_act_thk);              //코일실적두께
			jrParam.setField("COIL_ACT_THK_MNV",        	Coil_act_thk_mnv);          //코일실적두께최소값
			jrParam.setField("COIL_ACT_THK_MXV",        	Coil_act_thk_mxv);          //코일실적두께최대값
			jrParam.setField("COIL_ACT_WTH",            	Coil_act_wth);              //코일실적폭
			jrParam.setField("COIL_ACT_WTH_MNV",        	Coil_act_wth_mnv);          //코일실적폭최소값
			jrParam.setField("COIL_ACT_WTH_MXV",        	Coil_act_wth_mxv);          //코일실적폭최대값
			jrParam.setField("COIL_ACT_LTH",            	Coil_act_lth);              //코일실적길이
			jrParam.setField("COIL_RMW_WGT",            	Coil_rmw_wgt);              //코일실평중량
			jrParam.setField("COIL_THY_WGT",            	Coil_thy_wgt);              //코일이론중량
			jrParam.setField("COIL_GRS_RMW_WGT",        	Coil_grs_rmw_wgt);          //코일Gross실평중량
			jrParam.setField("COIL_GRS_THY_WGT",        	Coil_grs_thy_wgt);          //코일Gross이론중량
			jrParam.setField("COIL_IDIA",               	Coil_idia);                 //코일내경
			jrParam.setField("COIL_ODIA",               	Coil_odia);                 //코일외경
			jrParam.setField("HC_AW",                   	Hc_aw);                     //HC안배중량
			jrParam.setField("CC_AW",                   	Cc_aw);                     //CC안배중량
			jrParam.setField("REM_PROC",                	Rem_proc);                  //잔공정
			jrParam.setField("NXT_PROC_CD",             	Nxt_proc_cd);               //차공정코드
			jrParam.setField("BF_PROC_CD",              	Bf_proc_cd);                //전공정코드
			jrParam.setField("REQ_NXT_PROC_CD",         	Req_nxt_proc_cd);           //요구차공정코드
			jrParam.setField("REQ_NXT_PROC_CAU_CD",     	Req_nxt_proc_cau_cd);       //요구차공정원인코드
			jrParam.setField("ACT_PAS_PROC",            	Act_pas_proc);              //실적통과공정
			jrParam.setField("PRDN_CD",                 	Prdn_cd);                   //품명코드
			jrParam.setField("ORD_NO",                  	Ord_no);                    //주문번호
			jrParam.setField("ORD_LN",                  	Ord_ln);                    //주문행번
			jrParam.setField("ORD_NO_2",                	Ord_no_2);                  //주문번호_2
			jrParam.setField("ORD_LN_2",                	Ord_ln_2);                  //주문행번_2
			jrParam.setField("PL_ORD_YN",               	Pl_ord_yn);                 //복수주문여부
			jrParam.setField("BF_ORD_NO",               	Bf_ord_no);                 //전주문번호
			jrParam.setField("BF_ORD_LN",               	Bf_ord_ln);                 //전주문행번
			jrParam.setField("OBJ_CHG_APT_DH",          	Obj_chg_apt_dh);            //목전충당일시
			jrParam.setField("WGT_DCS_MTH_TP",          	Wgt_dcs_mth_tp);            //중량결정법구분
			jrParam.setField("CRM_MNF_STD_CD",          	Crm_mnf_std_cd);            //냉연제조표준코드
			jrParam.setField("MTL_CD",                  	Mtl_cd);                    //MaterialCode
			jrParam.setField("RMTL_SYM",                	Rmtl_sym);                  //원자재기호
			jrParam.setField("COIL_MRG_TP",             	Coil_mrg_tp);               //코일병합구분
			jrParam.setField("FST_RMTL_NO",              	Fst_rmtl_no);               //최초원자재번호
			jrParam.setField("RPV_RMTL_NO",              	Rpv_rmtl_no);               //대표원자재번호
			jrParam.setField("RMTL_NO_1",                	Rmtl_no_1);                 //원자재번호_1
			jrParam.setField("RMTL_LTH_1",               	Rmtl_lth_1);                //원자재길이_1
			jrParam.setField("RMTL_WGT_1",              	Rmtl_wgt_1);                //원자재중량_1
			jrParam.setField("RMTL_NO_2",                	Rmtl_no_2);                 //원자재번호_2
			jrParam.setField("RMTL_LTH_2",               	Rmtl_lth_2);                //원자재길이_2
			jrParam.setField("RMTL_WGT_2",               	Rmtl_wgt_2);                //원자재중량_2
			jrParam.setField("RMTL_NO_3",                	Rmtl_no_3);                 //원자재번호_3
			jrParam.setField("RMTL_LTH_3",               	Rmtl_lth_3);                //원자재길이_3
			jrParam.setField("RMTL_WGT_3",               	Rmtl_wgt_3);                //원자재중량_3
			jrParam.setField("RMTL_SLP_CD",              	Rmtl_slp_cd);               //원자재공급사코드
			jrParam.setField("RMTL_HEAT_NO",             	Rmtl_heat_no);              //원자재Heat번호
			jrParam.setField("SLP_RMTL_NO",              	Slp_rmtl_no);               //공급사원자재번호
			jrParam.setField("PL_WLD_PRT",               	Pl_wld_prt);                //산세용접부
			jrParam.setField("PL_WLD_CNT",              	Pl_wld_cnt);                //산세용접수
			jrParam.setField("PL_WLD_PNT_LOC_1",        	Pl_wld_pnt_loc_1);          //산세용접점위치_1
			jrParam.setField("PL_WLD_PNT_LOC_2",        	Pl_wld_pnt_loc_2);          //산세용접점위치_2
			jrParam.setField("PL_WLD_PNT_LOC_3",        	Pl_wld_pnt_loc_3);          //산세용접점위치_3
			jrParam.setField("PL_WLD_PNT_LOC_4",        	Pl_wld_pnt_loc_4);          //산세용접점위치_4
			jrParam.setField("PL_WLD_PNT_LOC_5",        	Pl_wld_pnt_loc_5);          //산세용접점위치_5
			jrParam.setField("PRD_WLD_CNT",             	Prd_wld_cnt);               //제품용접수
			jrParam.setField("MID_WLD_PNT_LOC_1",       	Mid_wld_pnt_loc_1);         //중간용접점위치_1
			jrParam.setField("MID_WLD_PNT_LOC_2",       	Mid_wld_pnt_loc_2);         //중간용접점위치_2
			jrParam.setField("MID_WLD_PNT_LOC_3",       	Mid_wld_pnt_loc_3);         //중간용접점위치_3
			jrParam.setField("ORD_PDN_TP",              	Ord_pdn_tp);                //주문생산구분
			jrParam.setField("URG_MTL_TP",              	Urg_mtl_tp);                //긴급재구분
			jrParam.setField("COIL_TOP_BOT_TP",         	Coil_top_bot_tp);           //코일TopBottom구분
			jrParam.setField("DMY_PTT_PLT_TP",          	Dmy_ptt_plt_tp);            //Dummy보호판구분
			jrParam.setField("SEM_PRD_TP",              	Sem_prd_tp);                //중간재구분
			jrParam.setField("PCM_ML_CLN_YN",           	Pcm_ml_cln_yn);             //PCMMillClean여부
			jrParam.setField("PIN_HOLE_TP",             	Pin_hole_tp);               //PinHole구분
			jrParam.setField("DMY_COIL_USE_CNT",        	Dmy_coil_use_cnt);          //Dummy코일사용횟수
			jrParam.setField("PRD_AUC_PRG_STS_TP",      	Prd_auc_prg_sts_tp);        //제품경매진행상태구분
			jrParam.setField("PRD_AUC_RGS_DH",          	Prd_auc_rgs_dh);            //제품경매등록일시
			jrParam.setField("COIL_MTL_OUT_SHP_TP",     	Coil_mtl_out_shp_tp);       //코일재료외형구분
			jrParam.setField("HLD_YN",                  	Hld_yn);                    //보류여부
			jrParam.setField("HLD_RGS_DH",              	Hld_rgs_dh);                //보류등록일시
			jrParam.setField("ERP_REG_YN",              	Erp_reg_yn);                //ERP등록여부
			jrParam.setField("LOD_LOC",                 	Lod_loc);                   //적재위치
			jrParam.setField("BF_LOD_LOC",              	Bf_lod_loc);                //전적재위치
			jrParam.setField("STK_NO",                  	Stk_no);                    //Stack번호
			jrParam.setField("BASE_NO",                 	Base_no);                   //Base번호
			jrParam.setField("SHT_CNT",                 	Sht_cnt);                   //Sheet매수
			jrParam.setField("COIL_SLV_USE_YN",         	Coil_slv_use_yn);           //코일Sleeve사용여부
			jrParam.setField("SPM_USE_YN",              	Spm_use_yn);                //SPM사용여부
			jrParam.setField("PL_ST_YN",                	Pl_st_yn);                  //산세ST여부
			jrParam.setField("ANN_ST_YN",               	Ann_st_yn);                 //소둔ST여부
			jrParam.setField("COR_ST_YN",               	Cor_st_yn);                 //정정ST여부
			jrParam.setField("ORD_SPL_TP",              	Ord_spl_tp);                //주여구분
			jrParam.setField("SPL_DH",                  	Spl_dh);                    //여재일시
			jrParam.setField("SPL_CAU_CD",              	Spl_cau_cd);                //여재원인코드
			jrParam.setField("COIL_SUR_GRD",            	Coil_sur_grd);              //코일표면등급
			jrParam.setField("COIL_SHP_GRD",            	Coil_shp_grd);              //코일형상등급
			jrParam.setField("COIL_SZ_GRD",             	Coil_sz_grd);               //코일칫수등급
			jrParam.setField("COIL_UNT_WGT_GRD",        	Coil_unt_wgt_grd);          //코일단중등급
			jrParam.setField("COIL_APR_INS_GRD",        	Coil_apr_ins_grd);          //코일외관검사등급
			jrParam.setField("COIL_MQL_GRD",            	Coil_mql_grd);              //코일재질등급
			jrParam.setField("COIL_MQL_GRD_CAU_CD",     	Coil_mql_grd_cau_cd);       //코일재질등급원인코드
			jrParam.setField("COIL_MQL_SYN_GRD",        	Coil_mql_syn_grd);          //코일재질종합등급
			jrParam.setField("COIL_ING_GRD",            	Coil_ing_grd);              //코일성분등급
			jrParam.setField("COIL_QLT_TRK_GRD",        	Coil_qlt_trk_grd);          //코일품질Tracking등급
			jrParam.setField("OP_GRD",                  	Op_grd);                    //OP등급
			jrParam.setField("COIL_APR_INS_GRD_CAU_CD", 	Coil_apr_ins_grd_cau_cd);   //코일외관검사등급원인코드
			jrParam.setField("COIL_ARP_INS_DH",         	Coil_arp_ins_dh);           //코일외관검사일시
			jrParam.setField("PRD_SYN_GRD",             	Prd_syn_grd);               //제품종합등급
			jrParam.setField("PRD_SYN_GRD_CAU_CD",      	Prd_syn_grd_cau_cd);        //제품종합등급원인코드
			jrParam.setField("COIL_CLR_MPR_GRD",        	Coil_clr_mpr_grd);          //코일칼라물성등급
			jrParam.setField("PRD_SYN_JDG_APT_DD",      	Prd_syn_jdg_apt_dd);        //제품종합판정계상일
			jrParam.setField("PRD_SYN_JDG_DH",          	Prd_syn_jdg_dh);            //제품종합판정일시
			jrParam.setField("MQL_TSTP_NO",            		Mql_tstp_no);               //재질시편번호
			jrParam.setField("MPR_TSTP_NO",              	Mpr_tstp_no);               //물성시편번호
			jrParam.setField("SMP_GTH_CNT",              	Smp_gth_cnt);               //Sample채취매수
			jrParam.setField("SPC_AVR",                  	Spc_avr);                   //규격약호
			jrParam.setField("MQL_SYM",                  	Mql_sym);                   //재질기호
			jrParam.setField("COIL_MQC",                 	Coil_mqc);                  //코일재질특성
			jrParam.setField("GW_ASG_CD",                	Gw_asg_cd);                 //도금량지정코드
			jrParam.setField("WK_GW_FRN",                	Wk_gw_frn);                 //작업도금량전면
			jrParam.setField("WK_GW_BAK",                	Wk_gw_bak);                 //작업도금량후면
			jrParam.setField("ORD_GW_ASG_CD",            	Ord_gw_asg_cd);             //주문도금량지정코드
			jrParam.setField("ORD_GW_FRN",               	Ord_gw_frn);                //주문도금량전면
			jrParam.setField("ORD_GW_BAK",               	Ord_gw_bak);                //주문도금량후면
			jrParam.setField("COIL_GAL_PTR_CD",          	Coil_gal_ptr_cd);           //코일도금후처리코드
			jrParam.setField("COIL_CLR_PTR_CD",          	Coil_clr_ptr_cd);           //코일칼라후처리코드
			jrParam.setField("COIL_SUR_PHM_CD",          	Coil_sur_phm_cd);           //코일표면사상코드
			jrParam.setField("COIL_ROU_CD",              	Coil_rou_cd);               //코일조도코드
			jrParam.setField("ORD_USG_CD",               	Ord_usg_cd);                //주문용도코드
			jrParam.setField("COIL_OIL_PNT_CD",          	Coil_oil_pnt_cd);           //코일도유코드
			jrParam.setField("COIL_PTT_FLM_CD",          	Coil_ptt_flm_cd);           //코일보호필름코드
			jrParam.setField("CRT_NO",                   	Crt_no);                    //CRT번호
			jrParam.setField("PAS_PROC_CD_1",            	Pas_proc_cd_1);             //통과공정코드_1
			jrParam.setField("COIL_RPROC_CNT_1",         	Coil_rproc_cnt_1);          //코일재처리횟수_1
			jrParam.setField("COIL_NO_1",                	Coil_no_1);                 //코일번호_1
			jrParam.setField("PAS_PROC_CD_2",            	Pas_proc_cd_2);             //통과공정코드_2
			jrParam.setField("COIL_RPROC_CNT_2",         	Coil_rproc_cnt_2);          //코일재처리횟수_2
			jrParam.setField("COIL_NO_2",                	Coil_no_2);                 //코일번호_2
			jrParam.setField("PAS_PROC_CD_3",            	Pas_proc_cd_3);             //통과공정코드_3
			jrParam.setField("COIL_RPROC_CNT_3",         	Coil_rproc_cnt_3);          //코일재처리횟수_3
			jrParam.setField("COIL_NO_3",                	Coil_no_3);                 //코일번호_3
			jrParam.setField("PAS_PROC_CD_4",            	Pas_proc_cd_4);             //통과공정코드_4
			jrParam.setField("COIL_RPROC_CNT_4",         	Coil_rproc_cnt_4);          //코일재처리횟수_4
			jrParam.setField("COIL_NO_4",                	Coil_no_4);                 //코일번호_4
			jrParam.setField("PAS_PROC_CD_5",            	Pas_proc_cd_5);             //통과공정코드_5
			jrParam.setField("COIL_RPROC_CNT_5",         	Coil_rproc_cnt_5);          //코일재처리횟수_5
			jrParam.setField("COIL_NO_5",                	Coil_no_5);                 //코일번호_5
			jrParam.setField("PAS_PROC_CD_6",            	Pas_proc_cd_6);             //통과공정코드_6
			jrParam.setField("COIL_RPROC_CNT_6",         	Coil_rproc_cnt_6);          //코일재처리횟수_6
			jrParam.setField("COIL_NO_6",                	Coil_no_6);                 //코일번호_6
			jrParam.setField("PAS_PROC_CD_7",            	Pas_proc_cd_7);             //통과공정코드_7
			jrParam.setField("COIL_RPROC_CNT_7",         	Coil_rproc_cnt_7);          //코일재처리횟수_7
			jrParam.setField("COIL_NO_7",                	Coil_no_7);                 //코일번호_7
			jrParam.setField("PAS_PROC_CD_8",            	Pas_proc_cd_8);             //통과공정코드_8
			jrParam.setField("COIL_RPROC_CNT_8",         	Coil_rproc_cnt_8);          //코일재처리횟수_8
			jrParam.setField("COIL_NO_8",                	Coil_no_8);                 //코일번호_8
			jrParam.setField("PAS_PROC_CD_9",            	Pas_proc_cd_9);             //통과공정코드_9
			jrParam.setField("COIL_RPROC_CNT_9",         	Coil_rproc_cnt_9);          //코일재처리횟수_9
			jrParam.setField("COIL_NO_9",                	Coil_no_9);                 //코일번호_9
			jrParam.setField("PAS_PROC_CD_10",           	Pas_proc_cd_10);            //통과공정코드_10
			jrParam.setField("COIL_RPROC_CNT_10",        	Coil_rproc_cnt_10);         //코일재처리횟수_10
			jrParam.setField("COIL_NO_10",               	Coil_no_10);                //코일번호_10
			jrParam.setField("POC_TP",                   	Poc_tp);                    //위탁가공구분
			jrParam.setField("POC_CMP_CD",               	Poc_cmp_cd);                //위탁가공업체코드
			jrParam.setField("PAK_EQU_CD",               	Pak_equ_cd);                //포장설비코드
			jrParam.setField("COIL_PAK_PTN",             	Coil_pak_ptn);              //코일포장양식
			jrParam.setField("COIL_PAK_APT_DD",          	Coil_pak_apt_dd);           //코일포장계상일
			jrParam.setField("COIL_PAK_CPLT_DH",         	Coil_pak_cplt_dh);          //코일포장완료일시
			jrParam.setField("COIL_PAK_SHF_GRP",         	Coil_pak_shf_grp);          //코일포장작업근조
			jrParam.setField("PAK_WK_TP",                	Pak_wk_tp);                 //포장작업구분
			jrParam.setField("LBL_PUB_CNT",              	Lbl_pub_cnt);               //Label발행횟수
			jrParam.setField("LBL_PUB_DH",               	Lbl_pub_dh);                //Label발행일시
			jrParam.setField("PRD_WHS_DH",               	Prd_whs_dh);                //제품입고일시
			jrParam.setField("PRD_WHS_APT_DD",           	Prd_whs_apt_dd);            //제품입고계상일
			jrParam.setField("PRD_DLV_REQ_KND_TP",       	Prd_dlv_req_knd_tp);        //제품출고의뢰유형구분
			jrParam.setField("CTD_SAL_TP",               	Ctd_sal_tp);                //제 품재고구분
			jrParam.setField("PRD_DLV_KND_TP",           	Prd_dlv_knd_tp);            //제품납품유형구분
			jrParam.setField("DLV_DH",                   	Dlv_dh);                    //출고일시
			jrParam.setField("PRD_DLV_APT_DD",           	Prd_dlv_apt_dd);            //제품출고계상일
			jrParam.setField("SHPG_DH",                  	Shpg_dh);                   //출하일시
			jrParam.setField("SHPG_APT_DD",              	Shpg_apt_dd);               //출하계상일
			jrParam.setField("REA_INF_WHS_TP",           	Rea_inf_whs_tp);            //실물정보입고구분
			jrParam.setField("REA_INF_RET_TP",           	Rea_inf_ret_tp);            //실물정보반납구분
			jrParam.setField("PRD_RET_DH",               	Prd_ret_dh);                //제품반납일시
			jrParam.setField("PRD_RET_APT_DD",           	Prd_ret_apt_dd);            //제품반납계상일
			jrParam.setField("PRD_RET_PRG_STS_TP",       	Prd_ret_prg_sts_tp);        //제품반납진행상태구분
			jrParam.setField("PRD_RET_REQ_PROC_CD",      	Prd_ret_req_proc_cd);       //제품반납요구공정코드
			jrParam.setField("PRD_RET_REQ_PRG_CD",       	Prd_ret_req_prg_cd);        //제품반납요구진도코드
			jrParam.setField("PRD_RET_REQ_CAU_CD",       	Prd_ret_req_cau_cd);        //제품반납요구원인코드
			jrParam.setField("SHPG_HLD_TP",              	Shpg_hld_tp);               //출하보류구분
			jrParam.setField("SHPG_HLD_DD",              	Shpg_hld_dd);               //출하보류일
			jrParam.setField("SHPG_HLD_CAU_TXT",         	Shpg_hld_cau_txt);          //출하보류원인내역
			jrParam.setField("SHPG_HLD_CNL_DD",          	Shpg_hld_cnl_dd);           //출하보류해제일
			jrParam.setField("PRD_LONG_STK_CAU_CD",      	Prd_long_stk_cau_cd);       //제품장기재고원인코드
			jrParam.setField("PRD_LONG_STK_RGS_DH",      	Prd_long_stk_rgs_dh);       //제품장기재고등록일시
			jrParam.setField("REA_INF_CRYN_TP",          	Rea_inf_cryn_tp);           //실물정보반입구분
			jrParam.setField("PRD_CRYN_CAU_TP",          	Prd_cryn_cau_tp);           //제품반입원인코드
			jrParam.setField("PRD_CRYN_DH",              	Prd_cryn_dh);               //제품반입일시
			jrParam.setField("PRD_CRYN_APT_DD",          	Prd_cryn_apt_dd);           //제품반입계상일
			jrParam.setField("DLV_PROG_STS_TP",          	Dlv_prog_sts_tp);           //출고진행상태
			jrParam.setField("MS_PUB_YN",                	Ms_pub_yn);                 //MS발행여부
			jrParam.setField("MS_PUB_DH",                	Ms_pub_dh);                 //MS발행일시
			jrParam.setField("PCOIL_NO",                 	Pcoil_no);                  //모코일번호
			jrParam.setField("TRF_REQ_NO",               	Trf_req_no);                //이송의뢰번호
			jrParam.setField("TRF_PRG_STS_TP",           	Trf_prg_sts_tp);            //이송진행상태구분
			jrParam.setField("TRF_WHS_DH",               	Trf_whs_dh);                //이송입고일시
			jrParam.setField("PLNT_TP",                  	Plnt_tp);                   //플랜트구분
			jrParam.setField("CREATED_OBJECT_TYPE",      	Created_object_type);       //생성Object유형
			jrParam.setField("CREATED_OBJECT_ID",       	Created_object_id);         //생성ObjectID
			jrParam.setField("CREATED_PROGRAM_ID",      	Created_program_id);        //생성프로그램ID
			jrParam.setField("CREATION_TIMESTAMP",      	Creation_timestamp);        //생성일시
			jrParam.setField("LAST_UPDATED_OBJECT_TYPE",	Last_updated_object_type);  //최종변경Object유형
			jrParam.setField("LAST_UPDATED_OBJECT_ID",  	Last_updated_object_id);    //최종변경ObjectID
			jrParam.setField("LAST_UPDATE_PROGRAM_ID",  	Last_update_program_id);    //최종변경프로그램ID
			jrParam.setField("LAST_UPDATE_TIMESTAMP",   	Last_update_timestamp);     //최종변경일시
			jrParam.setField("DATA_END_STATUS",         	Data_end_status);           //데이터종료여부
			jrParam.setField("DATA_END_OBJECT_TYPE",    	Data_end_object_type);      //데이타종료Object유형
			jrParam.setField("DATA_END_OBJECT_ID",      	Data_end_object_id);        //데이타종료ObjectID
			jrParam.setField("DATA_END_PROGRAM_ID",     	Data_end_program_id);       //데이타종료프로그램ID
			jrParam.setField("DATA_END_TIMESTAMP",      	Data_end_timestamp);        //데이터종료일시
			jrParam.setField("ARCHIVE_COMPLETED_FLAG",  	Archive_completed_flag);    //Archive완료여부
			jrParam.setField("ARCHIVED_EMPLOYEE_NUM",   	Archived_employee_num);     //Archive작업자사번
			jrParam.setField("ARCHIVED_TIMESTAMP",      	Archived_timestamp);        //Archive작업일시
			jrParam.setField("ARCHIVE_PROGRAM_ID",      	Archive_program_id);        //Archive프로그램ID
			jrParam.setField("CCL_CR_TRT_TP",           	Ccl_cr_trt_tp);             //CCL크롬재처리구분
			jrParam.setField("IN_PLT_CVT_CAU_CD",       	In_plt_cvt_cau_cd);         //내판전환원인코드
			jrParam.setField("CCL_2PASS_YN",            	Ccl_2pass_yn);              //2PASS여부
			jrParam.setField("COIL_RMW_WGT_1",          	Coil_rmw_wgt_1);            //코일실평중량1
			jrParam.setField("COIL_RMW_WGT_2",          	Coil_rmw_wgt_2);            //코일실평중량2
			jrParam.setField("SAP_MTL_CD",              	Sap_mtl_cd);                //SAP Material Code
			jrParam.setField("ITEMNAME_CD",             	Itemname_cd);               //통합품명코드
			jrParam.setField("PDN_PLNT_TP",             	Pdn_plnt_tp);               //생산예정공장
			jrParam.setField("PLN_PAS_PROC_CMN",        	Pln_pas_proc_cmn);          //계획통과공정
			jrParam.setField("PAS_PLNT_ACT",            	Pas_plnt_act);              //통과공장실적
			jrParam.setField("INV_NO",                  	Inv_no);                    //송장번호
			jrParam.setField("LGS_BAS_CD",              	Lgs_bas_cd);                //물류기지코드
			jrParam.setField("WIP_MTL_CD",              	Wip_mtl_cd);                //재공MaterialCode
			jrParam.setField("MS_SPE_YN",               	Ms_spe_yn);                 //특별채용여부
			jrParam.setField("UNDER_COIL_YN",           	Under_coil_yn);             //Under작업 유무
			jrParam.setField("UNDER_COIL_PLN_PAS",      	Under_coil_pln_pas);        //Under작업 등록공정
			jrParam.setField("UNDER_COIL_OBJECT_ID",    	Under_coil_object_id);      //Under작업 등록자
			jrParam.setField("COR_CAU_TXT",             	Cor_cau_txt);               //정정원인내역
			jrParam.setField("POC_TAR_MTL",             	Poc_tar_mtl);               //위탁가공대상재
			jrParam.setField("BLDP_TP",                 	Bldp_tp);                   //Buildup구분
			jrParam.setField("TRN_EQP_CD",              	Trn_eqp_cd);                //운송장비코드
			jrParam.setField("CAR_NO",                  	"");	                    //차량번호
			jrParam.setField("CAR_CARD_NO",             	"");						//냉연차량번호
			jrParam.setField("YD_CAR_UPP_LOC_CD",       	Prd_lod_rnk);               //제품상차순위(차상위치?)

			//2. 냉연코일공통처리
			//TB_YF_CR_COILCOMM(YF_냉연_코일공통)에 해당 COIL 카운트...0이면 신규등록 0이 아니면 업데이트
			JDTORecordSet rsCoilCommCT = commDao.select(jrParam, getCrCoilCount, logId, methodNm, "TB_YF_CR_COILCOMM 에서 해당 코일 카운트");

			if("0".equals(rsCoilCommCT.getRecord(0).getFieldString("CT")))
			{
				commUtils.printLog(logId, "신규 코일공통 냉연코일(" + Coil_no + ") 등록 시작!" , "SL");

				intRtnVal	= commDao.insert(jrParam, insYfCrCoilComm, logId, methodNm, "TB_YF_CR_COILCOMM 등록");
			}
			else
			{
				commUtils.printLog(logId, "기존 코일공통 냉연코일(" + Coil_no + ") 업데이트 시작!" , "SL");

				intRtnVal	= commDao.update(jrParam, updYfCrCoilComm, logId, methodNm, "TB_YF_CR_COILCOMM 수정");
			}

			//3. 저장품처리
			//TB_YF_STOCK(YF_저장품)에 해당 COIL 카운트...0이면 신규등록 0이 아니면 업데이트
			JDTORecordSet rsStockCT = commDao.select(jrParam, getStockCount, logId, methodNm, "TB_YF_STOCK 에서 코일 카운트");

			if("0".equals(rsStockCT.getRecord(0).getFieldString("CT")))
			{
				commUtils.printLog(logId, "신규 저장품 냉연코일(" + Coil_no + ") 등록 시작!" , "SL");

				intRtnVal	= commDao.insert(jrParam, insStockCrInfo, logId, methodNm, "TB_YF_STOCK 등록");
			}
			else
			{
				commUtils.printLog(logId, "기존 저장품 냉연코일(" + Coil_no + ") 업데이트 시작!" , "SL");

				intRtnVal	= commDao.update(jrParam, updStockCrInfo, logId, methodNm, "TB_YF_STOCK 수정");
			}

			commUtils.printLog(logId, "=============냉연코일정보수신 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 냉연코일위치요구(CRYFJ002)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvCRYFJ002(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "냉연코일위치요구[ACoilRevL3SeEJB.rcvCRYFRJ002] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();	//method 종료시 리턴하는 JDTORecord

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============냉연코일위치요구 시작========", "SL");

			//수신 항목 값
			String Coil_no	= commUtils.trim(rcvMsg.getFieldString("COIL_NO"));	//코일번호

			//1. 쿼리용 변수 등록
			jrParam.setField("REGISTER",	modifier);
			jrParam.setField("STL_NO",      Coil_no);	//냉연코일번호
			jrParam.setField("COIL_NO",     Coil_no);	//냉연코일번호
			
			//2. 저장품체크
			JDTORecordSet rsResult = commDao.select(jrParam, getYfStkLyrInfoByStlNo, logId, methodNm, "TB_YF_STKLYR 에서 해당 코일체크");
			
			if(rsResult.size() <= 0)
			{
				commUtils.printLog(logId, methodNm + "코일창고에 해당코일이 존재하지 않습니다. STL_NO : " + Coil_no, "SL");
				throw new Exception("코일창고에 해당코일이 존재하지 않습니다. STL_NO : " + Coil_no);
			}
			
			//3. YFCRJ002(저장품위치정보)생성...
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YFCRJ002" , jrParam));

			commUtils.printLog(logId, "=============냉연코일위치요구 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

}
