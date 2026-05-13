/**
 * @(#)YfCoilL3RcvPISeEJBBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2022/02/28
 *
 * @description      2열연 COIL 야드 물류진행 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2022/02/28   정종균  송정현      최초 등록
 * 
 */
package com.inisteel.cim.yfPI.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ymEtcDao.YmEtcDao;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yf.common.YfCommUtils;
import com.inisteel.cim.yf.common.YfConstant;
import com.inisteel.cim.yf.common.YfQueryIF;
import com.inisteel.cim.yf.common.YfQueryIF2;
import com.inisteel.cim.yf.common.dao.YfCommDAO;
import com.inisteel.cim.yf.common.session.YfComm;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.util.YmConstant;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

 
/**
 *      [A] 클래스명 : PI관련 2열연COIL야드 출하수신 처리
 *
 * @ejb.bean name="YfCoilL3RcvPISeEJB" jndi-name="YfCoilL3RcvPISeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class YfCoilL3RcvPISeEJBSBean extends BaseSessionBean  implements YfQueryIF, YfQueryIF2 {
	
	private static final long serialVersionUID = 1L;

	private YfCommUtils yfCommUtils = new YfCommUtils();
	private YfCommDAO 	commDao = new YfCommDAO();
	private YfComm 		yfComm = new YfComm();
	
	private String classNm = getClass().getName();
	
	private Logger logger = new Logger("yf");
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	

	/**
	 * 출하전문 취소처리 PI
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord receiveCancelPI(JDTORecord rcvMsg) throws DAOException 
	{
		String methodNm	= "A열연 출하전문 취소처리[YfCommSeEJBSBean.receiveCancelPI] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
 
		String sSTOCK_MOVE_TERM		= "";
		String sSchId				= "";
		String sBookId				= "";
		String sYD_STK_COL_GP		= "";
		String sYD_EQP_WRK_STAT		= "";

    	JDTORecordSet 	jrStlNo		= null;
    	JDTORecordSet	rsResult	= null;
    	JDTORecord 		jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
		JDTORecord 		jrRtn 		= JDTORecordFactory.getInstance().create();
    	
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			String msgId    			= yfCommUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID

			String sCURR_PROG_CD		= yfCommUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"));	    		
			String sTRANS_ORD_DT		= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			String sTRANS_ORD_SEQNO		= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String sFRTOMOVE_WORD_NO	= yfCommUtils.trim(rcvMsg.getFieldString("FRTOMOVE_WORD_NO"));	//이송 작업지시 번호
			String sSTL_NO 				= yfCommUtils.trim(rcvMsg.getFieldString("STL_NO"));
			String modifier 			= yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));			//수정자(Backup Only)
			String scarNo				= yfCommUtils.trim(rcvMsg.getFieldString("CAR_NO"));				//차량번호
			String infoGp				= yfCommUtils.trim(rcvMsg.getFieldString("INFO_GP"));				//정보구분
			
			//변경자 설정 (insert,update 문에서 사용)
			if ("".equals(modifier)) { modifier = msgId.substring(3,12); }
			
			jrParam.setField("MODIFIER", modifier); //수정자
			
	    	/*
			M10LMYDJ1011_1(DMYDR014)  코일제품목전			1.저장품 이동 조건 변경
			M10LMYDJ1011_2(DMYDR027)	코일제품보관지시		KEEPSTOCK_STL_YN= ''
			
			M10LMYDJ1021(DMYDR008)	코일제품반납확정		1.저장품 이동 조건 변경

			M10LMYDJ1071(DMYDR030)	코일제품출하완료              1.저장품 이동 조건 변경
			
			M10LMYDJ1041(DMYDR070)	코일이송상차대기장도착PDA
			M10LMYDJ1091(DMYDR073)	이송하차도착전(PDA)
			M10LMYDJ1031(DMYDR060)	코일제품운송상차지시
	    	 */

	    	if("".equals(sSTL_NO))
	    	{
 				/**********************************************************
 				* 운송지시번호,이송 작업지시 번호로 저장품 조회
 				**********************************************************/  
				jrParam.setField("TRANS_ORD_DATE",		sTRANS_ORD_DT);
				jrParam.setField("TRANS_ORD_SEQNO",		sTRANS_ORD_SEQNO);
				jrParam.setField("FRTOMOVE_WORD_NO",	sFRTOMOVE_WORD_NO);
				jrStlNo = commDao.select(jrParam, getTransStock, logId, methodNm, "운송지시번호,이송 작업지시 번호로 저장품 조회");				
				
				if(jrStlNo.size() <= 0 )
				{
					yfCommUtils.printLog("", "출하/이송/이적 대상을 찾지 못했습니다!!", "[INFO]");
				}
				else
				{
					sSTL_NO = yfCommUtils.trim(jrStlNo.getRecord(0).getFieldString("STL_NO"));  
				}
	    	} 
			
			/**********************************************************
			* 진도코드로 저장품이동조건을 가져온다.
			**********************************************************/  
			jrParam.setField("STL_NO",			sSTL_NO); 
			jrParam.setField("CURR_PROG_CD",	sCURR_PROG_CD);
			
			JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd2(jrParam);
			sSTOCK_MOVE_TERM = yfCommUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"));
	    	
	    	if ( "M10LMYDJ1021".equals(msgId) ||  	//코일제품반납대기
	    	    ("M10LMYDJ1011".equals(msgId) && infoGp.equals("1")) ||  //코일제품목전
	    		 "M10LMYDJ1071".equals(msgId)) {	//코일제품출하완료
 				/**********************************************************
 				* 저장품 이동 조건 변경
 				**********************************************************/  
	    		jrParam.setField("STOCK_MOVE_TERM",	sSTOCK_MOVE_TERM);
	    		jrParam.setField("STL_NO",			sSTL_NO);
	    		commDao.update(jrParam, updYdStock, logId, methodNm, "TB_YF_STOCK 저장품 이동 조건 변경");
	    		
	        } else if ( "M10LMYDJ1041".equals(msgId) ||  //코일이송상차대기장도착PDA
	        			"M10LMYDJ1091".equals(msgId) ||  //코일이송하차대기장도착PDA
	        			"M10LMYDJ1031".equals(msgId)) {  //코일제품운송상차지시
	    		jrParam.setField("STOCK_MOVE_TERM", sSTOCK_MOVE_TERM);
				jrParam.setField("TRANS_ORD_DATE",	sTRANS_ORD_DT);
				jrParam.setField("TRANS_ORD_SEQNO",	sTRANS_ORD_SEQNO);
	    		commDao.update(jrParam, updYdStock6, logId, methodNm, "TB_YF_STOCK 운송지시번호 삭제,저장품 이동 조건 변경 ");
	    		
	 	    } else if ("M10LMYDJ1011".equals(msgId) && infoGp.equals("2")) {	//코일제품보관지시
 				/**********************************************************
 				* 보관지시구분 KEEPSTOCK_STL_YN = ''
 				**********************************************************/
	    		jrParam.setField("KEEP_STL_YN",	"");
	    		jrParam.setField("STL_NO",			sSTL_NO);
	    		commDao.update(jrParam, updYdStock2, logId, methodNm, "TB_YM_STOCK 보관지시구분 KEEP_STL_YN = '' ");
	        }
    	
    		for(int ii = 0; ii < jrStlNo.size(); ii++) 
    		{
    			sSTL_NO = yfCommUtils.trim(jrStlNo.getRecord(ii).getFieldString("STL_NO"));  
		
 				/**********************************************************
 				* STL_NO로 스케줄 ID,작업예약ID 가져오기
 				**********************************************************/  
    			jrParam.setField("STL_NO", sSTL_NO);
    			rsResult  = commDao.select(jrParam, getYdWrkbookDelChk, logId, methodNm, "STL_NO로 스케줄 ID,작업예약ID 가져오기");				
    		
	    		if(rsResult.size() > 0) 
	    		{
		    		sSchId 	= yfCommUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID"));   
		    		sBookId = yfCommUtils.trim(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"));
		    		
		    		//박판코일
		    		if(!"".equals(sSchId))
			    	{
		    			//박판코일 크레인스케줄취소
    			       	JDTORecord jrRst = JDTORecordFactory.getInstance().create();
	        			jrParam.setField("YD_CRN_SCH_ID", sSchId);
	        			jrParam.setField("YD_WBOOK_ID"  , sBookId);
	        			jrParam.setField("WRK_CNCL_YN", "Y"); //작업취소 여부
	        			
	        			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspSeEJB", this);
	        			jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
	        			jrRtn = yfCommUtils.addSndData(jrRtn, jrRst);
			    	}
		    			
		    		if(!"".equals(sBookId))
		    		{
		    			//박판코일 작업예약취소
    			       	JDTORecord jrRst = JDTORecordFactory.getInstance().create();
    			       	jrParam.setField("YD_WBOOK_ID"  , sBookId);
    			       	
    			       	EJBConnector ejbConn = new EJBConnector("default", "ACoilJspSeEJB", this);
	        			jrRst = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
	        			jrRtn = yfCommUtils.addSndData(jrRtn, jrRst);
		    		}
	    		}
    		} //end of for 
    	
        	if(!"".equals(scarNo))
        	{
        		jrParam.setField("CAR_NO",	scarNo);
        		
        		rsResult  = commDao.select(jrParam, getYdStkColGpForCarNo, logId, methodNm, "CAR_NO로 점유중인 적치열 정보 가지고 오기");
        		
        		//CAR_NO,CARD_NO 기준으로 점유중인 적치열이 있으면 L2로 차량 출발 정보 전송
        		if(rsResult.size() > 0)
        		{	        			
        			sYD_STK_COL_GP = yfCommUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
        			
        			if ("M10LMYDJ1041".equals(msgId) ||	//코일이송상차대기장도착PDA
			    		"M10LMYDJ1031".equals(msgId)) {	//코일제품운송상차지시
        				sYD_EQP_WRK_STAT = "U";	//공차
        				
		    		} else if ("M10LMYDJ1091".equals(msgId)) {	
		    			sYD_EQP_WRK_STAT = "L";	//영차
		    		}
        			
        			/**********************************************************
					* 저장위치제원정보 송신 (YFF1L001) -- 차량출발
					**********************************************************/
					JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
					sndL2Msg.setResultCode(logId);		//Log ID
					sndL2Msg.setResultMsg(methodNm);	//Log Method Name
					sndL2Msg.setField("YD_INFO_SYNC_CD",		"3");				//야드정보동기화코드
					sndL2Msg.setField("YD_STK_COL_GP",			sYD_STK_COL_GP);	//CAR_NO로 조회한 점유중인 적치열
					sndL2Msg.setField("YD_STK_BED_NO",			"01");
					sndL2Msg.setField("YD_CAR_ARRSTRT_STAT",	"S");				//A:도착, S:출발
					sndL2Msg.setField("YD_CAR_USE_GP",			"G");				//L:구내운송, G:출하차량
					sndL2Msg.setField("YD_EQP_WRK_STAT",		sYD_EQP_WRK_STAT);	//U:공차, L:영차
					sndL2Msg.setField("CAR_NO",					scarNo);			//차량번호
					sndL2Msg.setField("CARD_NO",				"");			//카드번호
					sndL2Msg.setField("YD_CAR_AIM_YD_GP",		"1");

					//전송 Data 생성
					jrRtn = yfCommUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L001_CarInfo", sndL2Msg));
        		}
        		
				/**********************************************************
				* TB_YF_STKLYR 하차인 경우 차량위치재료 종료처리
				**********************************************************/
        		commDao.update(jrParam, modifyCardNoOflayerEND_PIDEV, logId, methodNm, "TB_YF_STACKLAYER 하차인 경우 차량위치재료 종료처리");

				/**********************************************************
				* TB_YD_CARFTMVMTL 차량이송재로 종료처리
				**********************************************************/
        		commDao.update(jrParam, modifyCardNoOfDetailEND_PIDEV, logId, methodNm, "TB_YD_CARFTMVMTL 차량이송재로 종료처리");

				/**********************************************************
				* TB_YD_CARSCH 차량스케줄 종료 처리
				**********************************************************/
        		commDao.update(jrParam, modifyCardNoOfEND_PIDEV, logId, methodNm, "TB_YD_CARSCH 차량스케줄 종료 처리");
                 
				/**********************************************************
				* TB_YF_STKCOL 적치열 차량예약 포인트 지우기
				**********************************************************/
        		commDao.update(jrParam, updateCardNoOfStackCol1_PIDEV, logId, methodNm, "TB_YF_STACKCOL 적치열 차량예약 포인트 지우기");
    			
        		//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
        		EJBConnector ejbConn2 = new EJBConnector("default","YfCommCarMvSeEJB",this);
        		ejbConn2.trx("YfCarPointinforeg", new Class[]{String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class},
        				new Object[]{"A", "", scarNo, "", "", "", "C", logId, methodNm});
        	}
        	
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		} 
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
		
	/**	 
	 * [A] 오퍼레이션명 :제품상태(열연코일)-출하지시대기,목전,반품,보관지시
	 *
	 * YfRcvFaEJB.java -> rcvDMYDR005 (코일제품출하지시대기)
	 * YfRcvFaEJB.java -> rcvDMYDR014 (코일제품목전)
	 * YfRcvFaEJB.java -> rcvDMYDR033 (코일제품반품)
	 * YfRcvFaEJB.java -> rcvDMYDR027 (코일제품보관지시)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1011(JDTORecord rcvMsg) throws DAOException {
	
		String methodNm = "[YfCoilL3RcvPISeEJBBean.rcvM10LMYDJ1011] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			yfCommUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "1", "*");
//			if ("N".equals(sApplyYnPI)) {
//				yfCommUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
			String infoGp = yfCommUtils.trim(rcvMsg.getFieldString("INFO_GP")); //야드구분
			
			rcvMsg.addField("TC_CREATE_DDTT"		, yfCommUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
			rcvMsg.addField("OLD_TRANS_WORD_DATE"	, yfCommUtils.trim(rcvMsg.getFieldString("OLD_TRN_REQ_DATE")) );
			rcvMsg.addField("OLD_TRANS_WORD_SEQNO"	, yfCommUtils.trim(rcvMsg.getFieldString("OLD_TRN_REQ_SEQ")) );
			rcvMsg.addField("NEW_TRANS_WORD_DATE"	, yfCommUtils.trim(rcvMsg.getFieldString("NEW_TRN_REQ_DATE")) );
			rcvMsg.addField("NEW_TRANS_WORD_SEQNO"	, yfCommUtils.trim(rcvMsg.getFieldString("NEW_TRN_REQ_SEQ")) );
			
			if("1".equals(infoGp)) { // 코일제품목전(DMYDR014)
			
				try 
				{
					methodNm += "코일제품목전(DMYDR014)";
					
					yfCommUtils.printLog(logId, methodNm, "S+");
					
					//수신 항목 값
					String cancelChk = yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));	//Y: 취소 , N: 지시
					
					if("Y".equals(cancelChk))
					{
						//취소('Y')
						jrRtn = this.receiveCancelPI(rcvMsg);
					}
					else
					{
						JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
						JDTORecord	jrRtnProg	= JDTORecordFactory.getInstance().create();
						int intRtnVal = 0;
						String		szMsg		= "";
						 
						//기본 수신 항목 값
						String msgId    = yfCommUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
						String sModifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

						if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }

						jrParam.setResultCode(logId);	//Log ID
						jrParam.setResultMsg(methodNm);	//Log Method Name
						jrParam.setField("MODIFIER", sModifier); //수정자 셋팅

						yfCommUtils.printLog(logId, "=============코일제품목전 시작========", "SL");

						//수신 항목 값
						String stl_No = yfCommUtils.paraRecChkNull(rcvMsg, "STL_NO");

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
						jrParam.setField("TC_CD",	msgId);
						jrParam.setField("STL_NO",	stl_No);
						jrParam.setField("INFO_GP",	infoGp);
						
						jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);

						jrParam.setField("STOCK_MOVE_TERM", yfCommUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));
						intRtnVal = commDao.update(jrParam, updYdStock, logId, methodNm, "TB_YF_STOCK 수정");

						if(intRtnVal > 0)
						{
							szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATE가 존재함";
							yfCommUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
						}
						else if(intRtnVal == 0)
						{
							szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATA가 존재하지 않음";
							yfCommUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

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

						jrRtn = yfCommUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성

						yfCommUtils.printLog(logId, "=============코일제품목전 종료========", "SL");

						yfCommUtils.printLog(logId, methodNm, "S-");
					}
					
					return jrRtn;
				} 
				catch (DAOException e) 
				{
					logger.println("==================rcvDMYDR014 catch 1");
					throw e;
				}
				catch (Exception e) 
				{
					logger.println("==================rcvDMYDR014 catch 2");
					throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
				}

			} else if("2".equals(infoGp)) { // 코일제품보관지시(DMYDR027)

				try 
				{
					methodNm += "코일제품보관지시(DMYDR027)";
					
					yfCommUtils.printLog(logId, methodNm, "S+");
					
					//수신 항목 값
					String cancelChk = yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));	//Y: 취소 , N: 지시
					
					if("Y".equals(cancelChk))
					{
						//취소('Y')
						jrRtn = this.receiveCancelPI(rcvMsg);
					}
					else
					{
						//지시('N')
						JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
					
						int intRtnVal = 0;
						String		szMsg		= "";
						
						//기본 수신 항목 값
						String msgId    = yfCommUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
						String modifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
//						if ("".equals(modifier))
//						{
//							modifier = msgId;
//						}						
                        if ("".equals(modifier)) { modifier = msgId.substring(3,12); }

						jrParam.setResultCode(logId);	//Log ID
						jrParam.setResultMsg(methodNm);	//Log Method Name
						jrParam.setField("MODIFIER", modifier); //수정자 셋팅

						yfCommUtils.printLog(logId, "=============코일제품보관지시 수신 시작========", "SL");

						//수신 항목 값
						String stl_No = yfCommUtils.paraRecChkNull(rcvMsg, "STL_NO");

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
							yfCommUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
						}
						else if(intRtnVal == 0)
						{
							szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATA가 존재하지 않음";
							yfCommUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

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

						jrRtn = yfCommUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성

						yfCommUtils.printLog(logId, "=============코일제품보관지시 종료========", "SL");						
					}
					
					yfCommUtils.printLog(logId, methodNm, "S-");
					 
					return jrRtn;
				} 
				catch (DAOException e) 
				{
					logger.println("==================rcvDMYDR027 catch 1");
					throw e;
				}
				catch (Exception e) 
				{
					logger.println("==================rcvDMYDR027 catch 2");
					throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
				}				
				
				
			} else if("3".equals(infoGp)) { // 코일제품반품(DMYDR033)

				try
				{
					methodNm += "코일제품반품(DMYDR033)";
					yfCommUtils.printLog(logId, methodNm, "S+");

					
					JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
					String		szMsg			= "";
					int			intRtnVal		= 0;						
					
					//기본 수신 항목 값
					String msgId    = yfCommUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
					String modifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

//					if ("".equals(modifier))
//					{
//						modifier = msgId;
//					}
					if ("".equals(modifier)) { modifier = msgId.substring(3,12); }

					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("MODIFIER", modifier); //수정자 셋팅

					yfCommUtils.printLog(logId, "=============코일제품반품 시작========", "SL");

					//수신 항목 값
					String Stl_no 			= yfCommUtils.trim(rcvMsg.getFieldString("STL_NO"));    		//재료번호
//					String distGoodsGp  	= yfCommUtils.trim(rcvMsg.getFieldString("DIST_GOODS_GP"));   //출하제품구분 H:열연코일, L:냉연코일
					String oldTransOrdDt  	= yfCommUtils.trim(rcvMsg.getFieldString("OLD_TRANS_WORD_DATE"));  //구운송지시일자
					String oldTransOrdSeqNo	= yfCommUtils.trim(rcvMsg.getFieldString("OLD_TRANS_WORD_SEQNO"));
					String newTransOrdDt  	= yfCommUtils.trim(rcvMsg.getFieldString("NEW_TRANS_WORD_DATE"));  //신운송지시일자
					String newTransOrdSeqNo	= yfCommUtils.trim(rcvMsg.getFieldString("NEW_TRANS_WORD_SEQNO"));

					/**********************************************************
					* 1. 수신 항목 값 Check
					**********************************************************/
//					if ( !("H".equals(distGoodsGp) || "L".equals(distGoodsGp)) )
//					{
//						throw new Exception("출하제품구분이상 (H 또는 L)가 아닙니다...");
//					}

					jrParam.setField("DEL_YN",	"N");
					jrParam.setField("STL_NO",	Stl_no);

					String[] rVal = new String[1];
					jrParam.setField("MQ_TC_CD",	msgId);		//TC_CODE
					jrParam.setField("INFO_GP" ,	infoGp);		//TC_CODE
					
					rVal= yfCommUtils.getYdAimRtGp("C", jrParam);


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
				    	yfCommUtils.printLog(logId, methodNm+ "[코일제품반품(DMYDR033)] 이전 운송지시번호로 변경 대상이 존재 함", "SL");

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
						yfCommUtils.printLog(logId, szMsg, "SL");
						return jrRtn;
					}
					else
					{
						ydCarSchId = yfCommUtils.trim(jsCarsch.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
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
					intRtnVal =	commDao.update(jrParam, updYfStock_PIDEV, logId, methodNm, "TB_YF_STOCK 수정");
					
					//======================================================
					// 저장품제원 : 코일야드L2 로 송신(YFF1L002)
					//======================================================
					JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
					sndL2Msg.setResultCode(logId);	//Log ID
					sndL2Msg.setResultMsg(methodNm);	//Log Method Name
					sndL2Msg.setField("YD_INFO_SYNC_CD",	"5");		//야드정보동기화코드
					sndL2Msg.setField("MSG_GP",				"I");		//전문구분
					sndL2Msg.setField("STL_NO",				Stl_no);	//재료번호

					jrRtn = yfCommUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성	//차후 수정해야함

					yfCommUtils.printLog(logId, "=============코일제품반품 종료========", "SL");

					yfCommUtils.printLog(logId, methodNm, "S-");

					return jrRtn;
				}
				catch (DAOException e)
				{
					throw e;
				}
				catch (Exception e)
				{
					throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
				}
				
			} else if("4".equals(infoGp)) { // 코일제품출하지시대기(DMYDR005)
				
				try 
				{
					JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
					JDTORecord	jrRtnProg	= JDTORecordFactory.getInstance().create();
					int intRtnVal = 0;
					String szMsg = "";
					
					methodNm += "코일제품출하지시대기(DMYDR005)";
					
					yfCommUtils.printLog(logId, methodNm, "S+");

					//기본 수신 항목 값
					String msgId    = yfCommUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
					String modifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

//					if ("".equals(modifier))
//					{
//						modifier = msgId;
//					}
					if ("".equals(modifier)) { modifier = msgId.substring(3,12); }

					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("MODIFIER", modifier); //수정자 셋팅

					yfCommUtils.printLog(logId, "=============코일제품출하차량도착실적 시작========", "SL");

					//수신 항목 값
					String stl_No	= yfCommUtils.paraRecChkNull(rcvMsg, "STL_NO");
					String yd_gp	= yfCommUtils.paraRecChkNull(rcvMsg, "YD_GP");

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
					jrParam.setField("TC_CD",	msgId);
					jrParam.setField("STL_NO",	stl_No);
					jrParam.setField("YD_GP",	yd_gp);
					jrParam.setField("INFO_GP",	infoGp);
					jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);

					jrParam.setField("STOCK_MOVE_TERM", yfCommUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));
					intRtnVal = commDao.update(jrParam, updYdStock, logId, methodNm, "TB_YF_STOCK 수정");

					if(intRtnVal > 0)
					{
						szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATE가 존재함";
						yfCommUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
					}
					else if(intRtnVal == 0)
					{
						szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATA가 존재하지 않음";
						yfCommUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

						throw new Exception(szMsg);
					}
					
					JDTORecordSet rsResult = commDao.select(jrParam, stlLocChk, logId, methodNm, "코일이 야드에 위치하는지 확인");

					//2020.11.02 정종균책임 요청으로 해당 코일이 야드에 있는지 확인 후 YFF1L002(저장품제원) 생성함
					if( "Y".equals(yfCommUtils.trim(rsResult.getRecord(0).getFieldString("LOC_YN"))) )
					{
						//======================================================
						// 저장품제원 : 코일야드L2 로 송신(YFF1L002)	//L2전문 확인후 수정
						//======================================================
						JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
						sndL2Msg.setResultCode(logId);						//Log ID
						sndL2Msg.setResultMsg(methodNm);					//Log Method Name
						sndL2Msg.setField("YD_INFO_SYNC_CD",	"5");		//야드정보동기화코드
						sndL2Msg.setField("MSG_GP",				"I");		//전문구분
						sndL2Msg.setField("STL_NO",				stl_No);	//재료번호

						jrRtn = yfCommUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성
					}

					yfCommUtils.printLog(logId, "=============코일제품출하차량도착실적 종료========", "SL");

					yfCommUtils.printLog(logId, methodNm, "S-");

					return jrRtn;
				} 
				catch (DAOException e) 
				{
					logger.println("==================rcvDMYDR005 catch 1");
					throw e;
				}
				catch (Exception e) 
				{
					logger.println("==================rcvDMYDR005 catch 2");
					throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
				}
			}
			
			return jrRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
		
	/**
	 * [A] 오퍼레이션명 :코일제품반납대기 DMYDR008
	 * YfRcvFaEJBSBean.java -> rcvDMYDR008
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvM10LMYDJ1021(JDTORecord rcvMsg) throws DAOException {

		String methodNm = "[YfCoilL3RcvPISeEJBBean.rcvM10LMYDJ1021] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			yfCommUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "1", "*");
//			if ("N".equals(sApplyYnPI)) {
//				yfCommUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}

			rcvMsg.setField("TC_CREATE_DDTT", 	  	 	yfCommUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
			
			//수신 항목 값
			String cancelChk = yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));	//Y: 취소 , N: 지시
			
			if("Y".equals(cancelChk))
			{
				//취소('Y')
				jrRtn = this.receiveCancelPI(rcvMsg);
			}
			else
			{
				//지시('N')
			    String		szMsg		= "";
			    JDTORecord	jrRtnProg	= JDTORecordFactory.getInstance().create();
			    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
			    int			intRtnVal 	= 0;
			    
				//기본 수신 항목 값
				String msgId    = yfCommUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
				String sModifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

				if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
				
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("MODIFIER", sModifier); //수정자 셋팅

				yfCommUtils.printLog(logId, "=============코일제품반납대기 시작========", "SL");

				//수신 항목 값
				String stl_No = yfCommUtils.paraRecChkNull(rcvMsg, "STL_NO");

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
				jrParam.setField("TC_CD",	msgId);
				jrParam.setField("STL_NO",	stl_No);
				
				jrRtnProg = yfComm.getCoilCurrProgCd(jrParam);

				jrParam.setField("STOCK_MOVE_TERM", yfCommUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));
				intRtnVal = commDao.update(jrParam, updYdStock, logId, methodNm, "TB_YF_STOCK 수정");

				if(intRtnVal > 0)
				{
					szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATE가 존재함";
					yfCommUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
				}
				else if(intRtnVal == 0)
				{
					szMsg = "수신한 재료번호 [" + stl_No + "]에 대한 저장품 DATA가 존재하지 않음";
					yfCommUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

					//throw new Exception(szMsg);
					return jrRtn;
					
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

				jrRtn = yfCommUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성				
			}			
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}			
			
	/**	 
	 * [A] 오퍼레이션명 :코일제품운송상차지시(DMYDR060)
	 * YfRcvFaEJBSBean.java -> rcvDMYDR060
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1031(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YfCoilL3RcvPISeEJBBean.rcvM10LMYDJ1031] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			yfCommUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "1", "*");
//			if ("N".equals(sApplyYnPI)) {
//				yfCommUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}

            rcvMsg.setField("TC_CREATE_DDTT" , yfCommUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
            rcvMsg.addField("TRANS_ORD_DT"   , yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
            rcvMsg.addField("TRANS_ORD_SEQNO", yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번

			//수신 항목 값
			String cancelChk = yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));	//Y: 취소 , N: 지시
			
			if("Y".equals(cancelChk))
			{
				String sPI0003 = yfComm.ACoilApplyYn("PI0003","*","*");
				
				if ("Y".equals(sPI0003)) {
					yfCommUtils.printLog(logId, "M10LMYDJ1031 취소시 신로직 들어감 'PI0003' = 'Y" , "SL");
					jrRtn = this.procDmTcCnclPI(rcvMsg);
				} else {
					//취소('Y')
					jrRtn = this.receiveCancelPI(rcvMsg);
				}
			}
			else
			{
				//지시('N')
				JDTORecord		jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

				JDTORecordSet	rsResult	= null;
				
				//기본 수신 항목 값
				String msgId    = yfCommUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
				String sModifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

				if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
				
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("MODIFIER", sModifier); //수정자 셋팅

				yfCommUtils.printLog(logId, "=============코일제품운송상차지시 수신 시작========", "SL");

				//수신 항목 값
				String szCMBN_CARLD_YN	 	= yfCommUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN"));		//조합상차유무(시작:S,종료:E,단일상차:N)
				String szTRANS_ORD_DT	 	= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));		//운송지시일자
				String szTRANS_ORD_SEQNO	= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));	//운송지시순번
				String szCAR_NO 		 	= yfCommUtils.trim(rcvMsg.getFieldString("CAR_NO"));			//차량번호
//				String szCARD_NO 		 	= yfCommUtils.trim(rcvMsg.getFieldString("CARD_NO"));				//카드번호
//				String szLOT_NO 		 	= yfCommUtils.trim(rcvMsg.getFieldString("LOT_NO"));				//LOT번호
				String szCAR_KIND 		 	= yfCommUtils.trim(rcvMsg.getFieldString("CAR_KIND"));			//차량종류
				String szTRANS_FRTOMOVE_GP	= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP"));	//운송이송구분

				int szYD_EQP_WRK_SH		 	= Integer.parseInt(yfCommUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수
				
				yfCommUtils.printLog(logId, "CMBN_CARLD_YN : "      + szCMBN_CARLD_YN     + ", " +
											"TRANS_ORD_DT : "       + szTRANS_ORD_DT      + ", " +
											"TRANS_ORD_SEQNO : "    + szTRANS_ORD_SEQNO   + ", " +
											"CAR_NO : "             + szCAR_NO            + ", " +
											"CAR_KIND : "           + szCAR_KIND          + ", " +
											"TRANS_FRTOMOVE_GP : "  + szTRANS_FRTOMOVE_GP + ", " +
											"YD_EQP_WRK_SH : "      + Integer.toString(szYD_EQP_WRK_SH), "SL");

				String szYD_GP			 	= "";
				String szSTL_NO;
				String szGDS_CARLD_LOC;
				String szSTOCK_MOVE_TERM;

//				YdStockDao	ydStockDao		= new YdStockDao();
//				String[]	rVal			= new String[1];
//				String		szYD_AIM_RT_GP	= "";

				//======================================================
				// 수신된 전문의 STL_NO의 수 만큼 Loop
				//======================================================
				for(int i = 1 ; i <= szYD_EQP_WRK_SH; i++)
				{
					szYD_GP			= yfCommUtils.trim(rcvMsg.getFieldString("YD_GP" + i));			//야드구분
					szSTL_NO		= yfCommUtils.trim(rcvMsg.getFieldString("STL_NO" + i)); 			//재료번호
					szGDS_CARLD_LOC	= yfCommUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC" + i));	//차상위치

					if ("S".equals(szCMBN_CARLD_YN) && "TR".equals(szCAR_KIND) && "".equals(szGDS_CARLD_LOC))
					{
						szGDS_CARLD_LOC = "0" + i;
					}

					if ("".equals(szSTL_NO))
					{
						break;
					}

					//1:A열연 COIL야드

					//======================================================
					// 저장품 이동 조건(STOCK_MOVE_TERM) 생성
					//======================================================
					jrParam.setField("TC_CD",	msgId);	//TC_CODE
					jrParam.setField("STL_NO",	szSTL_NO);	//저장품 ID
					szSTOCK_MOVE_TERM = yfCommUtils.trim(yfComm.getCoilCurrProgCd_PIDEV(jrParam).getFieldString("STOCK_MOVE_TERM"));

					//======================================================
					// TB_YF_STOCK 수정
					//======================================================
					jrParam.setField("STOCK_MOVE_TERM",		szSTOCK_MOVE_TERM);
					jrParam.setField("YD_RULE_PL_RS_GP",	szCMBN_CARLD_YN);
					jrParam.setField("TRANS_ORD_DATE",		szTRANS_ORD_DT);
					jrParam.setField("TRANS_ORD_SEQNO",		szTRANS_ORD_SEQNO);
					jrParam.setField("YD_CAR_UPP_LOC_CD",	szGDS_CARLD_LOC);
					jrParam.setField("CAR_NO",				szCAR_NO);
//                      jrParam.setField("CAR_CARD_NO",         szCARD_NO);
                    jrParam.setField("CAR_CARD_NO",         "");
					jrParam.setField("TRANS_FRTOMOVE_GP",	szTRANS_FRTOMOVE_GP);
					jrParam.setField("STL_NO",				szSTL_NO);

					commDao.update(jrParam, updYfStock_PIDEV, logId, methodNm, "TB_YF_STOCK 수정");

				} // end of for loop

				//야드핸들링정보 송신////////////////////////////////////////////////////////////////////////////////
				//코일야드 인 경우 우선 적용

				//======================================================
				// 출하제품핸들링횟수 구하기
				//======================================================
				jrParam.setField("TRANS_ORD_DATE",	szTRANS_ORD_DT);
				jrParam.setField("TRANS_ORD_SEQNO",	szTRANS_ORD_SEQNO);

				rsResult = commDao.select(jrParam, getHandlingCnt, logId, methodNm, "출하Handling 갯수 구하기");

				for(int i = 0; i < rsResult.size() ; i++)
				{
					String CoilGp = yfCommUtils.trim(rsResult.getRecord(i).getFieldString("COIL_GP"));
					
					jrParam.setField("YD_GP",					yfCommUtils.trim(rsResult.getRecord(i).getFieldString("YD_GP")));
					jrParam.setField("DIST_GOODS_GP",			"HR".equals(CoilGp) ? "H" : "CR".equals(CoilGp) ? "L" : "" );
					jrParam.setField("TRANS_ORD_DT",			szTRANS_ORD_DT);
					jrParam.setField("TRANS_ORD_SEQNO",			szTRANS_ORD_SEQNO);
					jrParam.setField("CMBN_CARLD_YN",			szCMBN_CARLD_YN);
					jrParam.setField("CARLD_PNT_CD",			yfCommUtils.trim(rsResult.getRecord(i).getFieldString("CARLD_PNT_CD")));
					jrParam.setField("CAR_NO",					szCAR_NO);
					jrParam.setField("HANDLING_CNT",			yfCommUtils.trim(rsResult.getRecord(i).getFieldString("HANDLING_CNT")));
					jrParam.setField("YD_STK_BED_WHIO_STAT",	"");

					//전송 Data 생성
					jrRtn = yfCommUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1051", jrParam));						
				}
			}

			yfCommUtils.printLog(logId, "=============코일제품운송상차지시 수신 종료========", "SL");
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}		
	}		

	
	/**	 
	 * [A] 오퍼레이션명 :코일제품반납확정(DMYDR061), 코일이송상차대기장도착PDA(DMYDR070) 
	 * YfRcvFaEJBSBean.java	-> rcvDMYDR061
	 * YfRcvFaEJBSBean.java	-> rcvDMYDR070
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1041(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YfCoilL3RcvPISeEJB.rcvM10LMYDJ1041 [코일제품반납확정(DMYDR061), 코일이송상차대기장도착PDA(DMYDR070)]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try {
			yfCommUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "1", "*");
//			if ("N".equals(sApplyYnPI)) {
//				yfCommUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
            rcvMsg.setField("TC_CREATE_DDTT" , yfCommUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
            rcvMsg.setField("TRANS_ORD_DT"   , yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
            rcvMsg.setField("TRANS_ORD_SEQNO", yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
			String sWorkGp		= yfCommUtils.trim(rcvMsg.getFieldString("WORK_GP")); //야드구분
			String sMqTcCd		= yfCommUtils.trim(rcvMsg.getFieldString("MQ_TC_CD")); //야드구분
			
			String sModifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));
			
			if ("".equals(sModifier)) { sModifier = sMqTcCd.substring(3,12); }
			
			rcvMsg.setField("MODIFIER", sModifier);
			
			if(! "9".equals(sWorkGp) ) {
				// 코일제품반납확정
				jrRtn = this.procM10LMYDJ_DMYDR061(rcvMsg);	
			} else {
				// 코일이송상차대기장도착PDA
				jrRtn = this.procM10LMYDJ_DMYDR070(rcvMsg);
			}
			
			yfCommUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}		
	
	/**	 
	 * [A] 오퍼레이션명 :코일이송상차도착PDA(DMYDR071)
	 * YfRcvFaEJBSBean.java	-> rcvDMYDR071
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1051(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YfCoilL3RcvPISeEJB.rcvM10LMYDJ1051 [코일이송상차도착PDA(DMYDR071)]] < " + rcvMsg.getResultMsg();
		
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		int			intRtnVal	= 0;

		try	{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "1", "*");
//			if ("N".equals(sApplyYnPI)) {
//				yfCommUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
            rcvMsg.addField("TC_CREATE_DDTT" , yfCommUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
            rcvMsg.addField("TRANS_ORD_DT"   , yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
            rcvMsg.addField("TRANS_ORD_SEQNO", yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번

			//기본 수신 항목 값
			String msgId    = yfCommUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID

			String modifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier)) { modifier = msgId.substring(3,12); }
			
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			yfCommUtils.printLog(logId, "=============코일이송상차도착PDA 수신 시작========", "SL");

			//수신 항목 값
			String transOrdDt  		= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); 	//운송실적일자
			String transOrdSeqNo	= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));	//운송실적순번
			String ydCarNo 			= yfCommUtils.trim(rcvMsg.getFieldString("CAR_NO"));

			String WorkGp 			= yfCommUtils.trim(rcvMsg.getFieldString("WORK_GP"));   		//작업구분
			String ydCarKind		= yfCommUtils.trim(rcvMsg.getFieldString("CAR_KIND"));
			String CarldPntCd		= yfCommUtils.trim(rcvMsg.getFieldString("CARLD_PNT_CD"));   	//상차포인트
			String crFrtomoveGp		= yfCommUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP")); 	//냉연이송구분

			String Stl_no			= "";
			String szMsg            = "";
			String ydCarSchId       = "";
			String ydCarProgStat    = "";
			String ydCarWrkGp 		= ""; 	//야드차량작업구분
			String ydEqpWrkStat		= ""; 	//야드설비작업상태

			String ydFrmYn			= "";	//차량형상사용유무
			String dummyYn			= "";	//더미작업여부

			String sAPPLY = yfComm.ACoilApplyYn("APP003", "1", "1");
			yfCommUtils.printLog(logId,  "차량ERROR LOG 처리:" + sAPPLY, "SL");

		   /**********************************************************
			* 2. 운송실적번호로 제품번호 가져오기
			**********************************************************/
			jrParam.setField("TRANS_ORD_DATE",	transOrdDt);
			jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
			JDTORecordSet jsStlno = commDao.select(jrParam, getTransStockInfo, logId, methodNm, "운송실적번호에 맞는 제품번호가 존재 조회");

			if(jsStlno.size() <= 0 )
			{
				szMsg = "[" + methodNm + "] [코일제품상차지시등록] 운송실적번호에 맞는 제품번호가 존재 안함: TRANS_WORD_NO:[" + transOrdDt+transOrdSeqNo + "]";
				yfCommUtils.printLog(logId, szMsg, "SL");

				throw new Exception("[코일제품상차지시등록] [차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다....");
			}
			else
			{
				Stl_no 		= yfCommUtils.trim(jsStlno.getRecord(0).getFieldString("STL_NO"));
				ydCarSchId	= yfCommUtils.trim(jsStlno.getRecord(0).getFieldString("YD_CAR_SCH_ID"));	//차량 작업지시

				jrParam.setField("STL_NO", Stl_no);

				//------------------------------------------------------------------------------------------------------------
		    	//	이적 및 대차출하 작업예약 존재 여부 CHECK
		    	//------------------------------------------------------------------------------------------------------------
				jrParam.setField("TRANS_ORD_DT",	transOrdDt);
				jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
				JDTORecordSet jsWbookMtl = commDao.select(jrParam, getYfStockWbookcheck, logId, methodNm, "작업예약 조회");

				if(jsWbookMtl.size() > 0)
			 	{
					yfCommUtils.printLog(logId, "이적 및 대차출하  작업예약이 삭제처리 합니다", "SL");

					for (int Loop_i = 1; Loop_i <= jsWbookMtl.size() ; Loop_i++)
					{
						jsWbookMtl.absolute(Loop_i);
						JDTORecord jrInPara = JDTORecordFactory.getInstance().create();

						jrInPara.setRecord(jsWbookMtl.getRecord());
						jrInPara.setResultCode(logId);		//Log ID
						jrInPara.setResultMsg(methodNm);	//Log Method Name
						jrInPara.setField("MODIFIER",	modifier); //수정자

						//크레인 작업예약 삭제
						EJBConnector ejbConn = new EJBConnector("default", "ACoilJspSeEJB", this);
						ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrInPara });
					}
				}

			   /**********************************************************
				* 3. 작업예약 편성여부 :
				**********************************************************/
				JDTORecordSet jsChk2 = commDao.select(jrParam, getYfwBookStockYN, logId, methodNm, "작업예약 편성여부");

				yfCommUtils.printLog(logId, "작업 예약 편성여부(WB_STL_YN) : " + jsChk2.getRecord(0).getFieldString("WB_STL_YN") , "SL");

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

							EJBConnector ejbConnLog = new EJBConnector("default", "YfCommCarMvSeEJB", this);
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

						EJBConnector ejbConnLog = new EJBConnector("default", "YfCommCarMvSeEJB", this);
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
				ydCarProgStat 	= yfCommUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT")); //차량진행상태
				ydCarWrkGp 		= yfCommUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_WRK_GP")); 	//야드차량작업구분
				ydEqpWrkStat	= yfCommUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_EQP_WRK_STAT")); 	//야드설비작업상태
			}

			jrParam.setField("TC_CD",		msgId);
			jrParam.setField("STL_NO",		Stl_no);
			jrParam.setField("MODIFIER",	modifier);	//수정자

			JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd_PIDEV(jrParam);
			jrParam.setField("STOCK_MOVE_TERM",	yfCommUtils.nvl(jrRtnProg.getFieldString("STOCK_MOVE_TERM"), "LG"));
			jrParam.setField("MODIFIER",		modifier);
			
		    intRtnVal = commDao.update(jrParam, updYdStock4_PIDEV, logId, methodNm, "TB_YF_STOCK 수정");

            if(intRtnVal <= 0)
            {
            	szMsg = "["+methodNm+"]에 대한 저장품 DATA가 존재하지않음:"+" STL_NO:["+Stl_no+"]"+" TRANS_WORD_NO:["+transOrdDt+transOrdSeqNo+"]"+intRtnVal+"건" ;
            	yfCommUtils.printLog(logId, szMsg, "SL");
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
				jrParam.setField("YD_CARPNT_CD",	CarldPntCd);
				
				JDTORecordSet jsCarPnt = commDao.select(jrParam, getYdCarPoint, logId, methodNm, "차량포인트 조회");

				if(jsCarPnt.size() > 0)
				{
					String ydStkColGp		= yfCommUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_GP2"));
					String ydStkColActStat	= yfCommUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));

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
						jrInTemp.setField("CAR_NO",					ydCarNo);
						jrInTemp.setField("TRN_EQP_CD",				"");
						jrInTemp.setField("YD_CAR_PROG_STAT",		ydCarProgStat);
						jrInTemp.setField("TRANS_EQUIPMENT_TYPE",	"P");  //냉연 이송

						EJBConnector ejbConn1 = new EJBConnector("default", "YfCommCarMvSeEJB", this);
						ejbConn1.trx("procYfLayerOpen", new Class[] { JDTORecord.class }, new Object[] { jrInTemp });

						/***************************************
						 * 	5.작업예약이 생성
						 **************************************/
						//작업예약,작업재료 등록
						String ydSchCd  = "";

						// PIDEV
						String[] rVal = commDao.getTrnFrtomoveGpPI("", methodNm, transOrdDt, transOrdSeqNo);
						String sTrnFrtomoveGp = rVal[0];
						String hIssueGp = rVal[1];
						
						yfCommUtils.printLog(logId, "sTrnFrtomoveGp:" + sTrnFrtomoveGp + "," + "hIssueGp:" + hIssueGp, "SL");
						
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
//							if ( "21".equals(sTrnFrtomoveGp) )
//						 	{
//								//제품이송상차
//								if ("2".equals(CarldPntCd.substring(1,2)))
//								{
//									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT22UM";
//								}
//								else
//								{
//									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT21UM";
//								}
//							}
							if ( "21".equals(sTrnFrtomoveGp) && "K".equals(hIssueGp) ) {
								// 제품이송상차
								if ("2".equals(CarldPntCd.substring(1,2))) {
									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT22UM";
								} else {
									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT21UM";
								}
							} else if ( "22".equals(sTrnFrtomoveGp) && "9".equals(hIssueGp)) {
								//제품이송상차
								if ("2".equals(CarldPntCd.substring(1,2))) {
									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT22UM";
								} else {
									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT21UM";
								}
//						 	else if ( "23".equals(sTrnFrtomoveGp) )
//						 	{
//								//(임가공이송 OR 순천 이송) 스케줄 구분 : 냉연이송
//								//소재이송상차
//								if ("2".equals(CarldPntCd.substring(1,2)))
//								{
//									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT22UM";
//								}
//								else
//								{
//									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT21UM";
//								}
//						 	}
							} else if ( ("21".equals(sTrnFrtomoveGp) && "C".equals(hIssueGp))
										||
										("21".equals(sTrnFrtomoveGp) && "D".equals(hIssueGp)) ) {
								// (임가공이송 OR 순천 이송) 스케줄 구분 : 냉연이송
								// 소재이송상차
								if ("2".equals(CarldPntCd.substring(1,2))) {
									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT22UM";
								} else {
									ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT21UM";
								}

							} else {
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
						jrInTemp.setField("CAR_NO",			ydCarNo);
						jrInTemp.setField("MODIFIER",		modifier);
						
						yfCommUtils.printLog(logId, "sTrnFrtomoveGp:" + sTrnFrtomoveGp + ", " +
													"ydSchCd:" + ydSchCd + ", " +
													"ydCarSchId:" + ydCarSchId + ", " +
													"ydStkColGp:" + ydStkColGp + ", " +
													"ydCarNo:" + ydCarNo + ", " +
													"modifier:" + modifier, "SL");
						
		    			String ydWbookId = yfComm.procCarWkBookInsert(jrInTemp);	//작업예약 생성

		    			if(ydWbookId.equals(YfConstant.RETN_CD_FAILURE))
		    			{
		    				throw new Exception("작업예약ID 생성 실패");
		    			}

		    			//----------------------------------------------------------------------
						// 야드저장위치제원(YFF1L001) 전문전송
						//----------------------------------------------------------------------
		    			jrInTemp.setField("YD_INFO_SYNC_CD",	"3");			//야드정보동기화코드(3:열)
		    			jrInTemp.setField("YD_STK_COL_GP",		ydStkColGp);	//야드적치열구분
						jrRtn = yfCommUtils.addSndData(commDao.getMsgL2("YFF1L001", jrInTemp));

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
							jrInTemp.setField("YD_CARUD_ARR_DT",	yfCommUtils.getDateTime14());
							jrInTemp.setField("YD_CAR_PROG_STAT",	"B");		//하차도착상태
						}
						else
						{
							jrInTemp.setField("YD_CARLD_ARR_DT",	yfCommUtils.getDateTime14());
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

						yfCommUtils.printLog(logId, "차량형상 시스템 사용 여부: " + rsResult3.getRecord(0).getFieldString("YD_FRM_YN"), "SL");
						yfCommUtils.printLog(logId, "더미작업여부 : " + rsResult4.getRecord(0).getFieldString("DUMMY_YN"), "SL");

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
							jrRtn = yfCommUtils.addSndData(jrRtn, yfComm.procCarPlanInfo(jrParam));	//YFF1L008 생성
						//}

						//차량형상사용 'N' 또는 더미작업유무 'Y'인 경우 크레인 스케줄 기동
						if("N".equals(ydFrmYn) || "Y".equals(dummyYn))
						{
							//크레인 스케줄 기동 YFYFJ303 호출
							JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
							jrCrnSchMsg.setField("JMS_TC_CD",			"YFYFJ303");
							jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT",	yfCommUtils.getDateTime14()); //JMSTC생성일시
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

							jrRtn = yfCommUtils.addSndData(jrRtn, jrCrnSchMsg);
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

					EJBConnector ejbConnLog = new EJBConnector("default", "YfCommCarMvSeEJB", this);
					ejbConnLog.trx("updCarErrorLogClear", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });
		 		}
			}

			yfCommUtils.printLog(logId, "=============코일이송상차도착PDA 수신 종료========", "SL");

			yfCommUtils.printLog(logId, methodNm, "S-");
						
			return jrRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}		
	}		
		
	/**
	 * [A] 오퍼레이션명 :코일제품출하완료(DMYDR030)
	 * YfRcvFaEJBSBean.java	-> rcvDMYDR030
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1071(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YfCoilL3RcvPISeEJBBean.rcvM10LMYDJ1071 [코일제품출하완료(DMYDR030)]] < " + rcvMsg.getResultMsg();
		String			logId		= rcvMsg.getResultCode();
	    JDTORecord		jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord		jrRtn		= JDTORecordFactory.getInstance().create();
	    JDTORecordSet	rsResult	= null;

	    String			szMsg		= "";

		try	{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "1", "*");
//			if ("N".equals(sApplyYnPI)) {
//				yfCommUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
            rcvMsg.addField("TC_CREATE_DDTT" , yfCommUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
            rcvMsg.addField("TRANS_ORD_DT"   , yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
            rcvMsg.addField("TRANS_ORD_SEQNO", yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번

			//기본 수신 항목 값
			String msgId    = yfCommUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier)) { modifier = msgId.substring(3,12); }

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			yfCommUtils.printLog(logId, "=============코일제품출하완료 시작========", "SL");

			//수신 항목 값
//			String szRcvTcCode 	= yfCommUtils.trim(rcvMsg.getFieldString("JMS_TC_CD"));
			String Stl_no    	= yfCommUtils.trim(rcvMsg.getFieldString("STL_NO"));
			String stlAppearGp	= yfCommUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));
			String ydGp        	= yfCommUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String BackUpYn     = yfCommUtils.trim(rcvMsg.getFieldString("BACKUP_YN"));
			String sModifier	= yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));
			String sModDdtt		= yfCommUtils.trim(rcvMsg.getFieldString("TC_CREATE_DDTT"));
			String sCancelChk   = yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"    )); // Y: 취소 , N: 지시

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
//			// 수신한 전문이 null이라면 error
//			if("".equals(szRcvTcCode))
//			{
//				szMsg="[ERROR] "+methodNm+"::"+methodNm+"() TC Code Error (NULL)";
//				throw new Exception("TC Code Error.");
//			}

			jrParam.setField("STL_NO",	Stl_no);
			JDTORecordSet loadYdStkcol = commDao.select(jrParam, getYdStockJoinStkLyr2, logId, methodNm, "차량정정보검색");

			if (loadYdStkcol.size() <= 0)
			{
				szMsg="["+methodNm+"] [코일제품출하완료]조건조회시 SELECT Error ::  DO NOT EXIST"  ;
				yfCommUtils.printLog(logId, szMsg, "SL");
				return jrRtn ;
			}

			//수신 항목 값
			String ydCarNo    	= yfCommUtils.trim(loadYdStkcol.getRecord(0).getFieldString("CAR_NO"));
			String ydStkColGp 	= yfCommUtils.trim(loadYdStkcol.getRecord(0).getFieldString("YD_STK_COL_GP"));
			String ydCardNo     = "";
			String transOrdDate	= yfCommUtils.trim(loadYdStkcol.getRecord(0).getFieldString("TRANS_ORD_DATE"));
			String transOrdSeqNo= yfCommUtils.trim(loadYdStkcol.getRecord(0).getFieldString("TRANS_ORD_SEQNO"));

			jrParam.setField("DEL_YN",			"Y");
			jrParam.setField("MODIFIER",		modifier);
			jrParam.setField("STOCK_MOVE_TERM",	"M2");
			jrParam.setField("STL_NO",			Stl_no);
			commDao.update(jrParam, updateStock2, logId, methodNm, "TB_YF_STOCK 수정");

			//***************************************************************************
			//  저장품이 적치된 저장위치 정보를 조회
			//***************************************************************************
			szMsg="[" + methodNm + "]카드번호["+ydCarNo+"], 차량번호["+ydCarNo+"], 운송지시일자["+transOrdDate+"], 운송지시순번["+transOrdSeqNo+"] : 출하완료된 동["+ydStkColGp+"]의 저장품들 조회 시작";
			yfCommUtils.printLog(logId, szMsg, "SL");

			if("*".equals(stlAppearGp) && "N".equals(sCancelChk))
			{
				yfCommUtils.printLog(logId, "[" + methodNm + "] 마지막 상차완료 전문", "SL");

				jrParam.setField("CAR_NO",			ydCarNo);
				jrParam.setField("CARD_NO",			ydCardNo);
				jrParam.setField("TRANS_ORD_DATE",	transOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
				JDTORecordSet loadCarsch = commDao.select(jrParam, getYdCarschTransDTSeq2, logId, methodNm, "차량정정보검색");

				if(loadCarsch.size() <= 0 )
				{
					szMsg="["+methodNm+"] 차량스케쥴 조회 SELECT Error ::  DO NOT EXIST"  ;
					yfCommUtils.printLog(logId, szMsg, "SL");

					return jrRtn ;
				}

				jrParam.setField("TC_CODE",			"DMYDR040");	//전문코드
				jrParam.setField("SPOS_WLOC_CD",	yfCommUtils.trim(loadCarsch.getRecord(0).getFieldString("SPOS_WLOC_CD")));
				jrParam.setField("SPOS_YD_PNT_CD",	yfCommUtils.trim(loadCarsch.getRecord(0).getFieldString("YD_PNT_CD1")));
				jrParam.setField("YD_GP",			ydGp);

				if ("".equals(ydCarNo))
				{
					ydCarNo = "XXXXX";
				}
				
				if("Y".equals(BackUpYn))
				{
					jrParam.setField("YD_CAR_SCH_ID",		yfCommUtils.trim(loadCarsch.getRecord(0).getFieldString("YD_CAR_SCH_ID")));
					jrParam.setField("YD_CAR_PROG_STAT",	"5");	//상차완료
					commDao.updateTx(jrParam, updateCarSchStats, logId, methodNm, "백업처리시 차량스케줄 상차완료로 업데이트 처리함");
				}

				yfCommUtils.printParam(logId, jrParam);
				//전송 Data 생성
				szMsg= "["+ methodNm +"] 차량번호[" + ydCarNo + "]는 코일제품출하차량출발실적호출";
				yfCommUtils.printLog(logId, szMsg, "SL");

				EJBConnector ejbConn1 = new EJBConnector("default", "YfCommCarMvSeEJB", this);
				JDTORecord jrRtn1 = (JDTORecord)ejbConn1.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { jrParam });

				jrRtn = yfCommUtils.addSndData(jrRtn, jrRtn1);
			}
			else
			{
				yfCommUtils.printLog(logId, "[" + methodNm + "] 마지막 상차완료 전문이 아님", "SL");
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

			jrRtn = yfCommUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성
			yfCommUtils.printLog(logId,"[코일제품출하완료][" + Stl_no + "] BACKUP 유무=>:+" + BackUpYn +" CANCEL_YN=>:"+ sCancelChk, "SL");

			
			/*******************************************
			 * layer정보가 존재 하는 경우 (백업으로 취소 처리 시)
			 *******************************************/
			String sYD_STK_LOC = "1X01010101";

			if("Y".equals(BackUpYn) && "N".equals(sCancelChk))
			{
				//20.12.03 이호연책임 요청으로 백업처리시 남아있는 크레인스케줄 및 작업예약도 삭제해야함
				//종료안된 크레인스케줄 삭제
				jrParam.setField("STL_NO", Stl_no);
    			rsResult  = commDao.select(jrParam, getCrnSchInCarNo, logId, methodNm, "재료번호로 종료안된 크레인스케줄 가지고 오기");
				
    			if(rsResult.size() > 0) 
	    		{
    				JDTORecord jrRst = JDTORecordFactory.getInstance().create();
    				jrParam.setField("YD_CRN_SCH_ID",	rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
    				jrParam.setField("YD_WBOOK_ID",		rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"));
    				jrParam.setField("WRK_CNCL_YN",		"Y"); //작업취소 여부
	    			
    				EJBConnector ejbConn = new EJBConnector("default", "ACoilJspSeEJB", this);
    				jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
    				jrRtn = yfCommUtils.addSndData(jrRtn, jrRst);
	    		}
    			
    			//종료안된 작업예약 삭제
    			jrParam.setField("STL_NO", Stl_no);
    			rsResult  = commDao.select(jrParam, getWrkBookInCarNo, logId, methodNm, "재료번호로 종료안된 크레인스케줄 가지고 오기");
    			
    			if(rsResult.size() > 0) 
	    		{
    				JDTORecord jrRst = JDTORecordFactory.getInstance().create();
    				jrParam.setField("YD_WBOOK_ID",		rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"));
    		       	
    				EJBConnector ejbConn = new EJBConnector("default", "ACoilJspSeEJB", this);
    				jrRst = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
    				jrRtn = yfCommUtils.addSndData(jrRtn, jrRst);
	    		}
        		
				jrParam.setField("STL_NO",	Stl_no);
				JDTORecordSet loadStacklayer = commDao.select(jrParam, getStacklayerList2, logId, methodNm, "단정보검색");
				String sPointNo = "";

				if(loadStacklayer.size() > 0)
				{

					sPointNo    = yfCommUtils.trim(loadStacklayer.getRecord(0).getFieldString("YD_STK_COL_GP"));
					sYD_STK_LOC = yfCommUtils.trim(loadStacklayer.getRecord(0).getFieldString("YD_STK_COL_GP"))
					            + yfCommUtils.trim(loadStacklayer.getRecord(0).getFieldString("YD_STK_BED_NO"))
					            + yfCommUtils.trim(loadStacklayer.getRecord(0).getFieldString("YD_STK_LYR_NO"));

					commDao.update(jrParam, updateStacklayer, logId, methodNm, "TB_YF_STKLYR 수정");

					yfCommUtils.printLog(logId, "[코일제품출하완료][" + Stl_no + "]에 저장위치맵을 비웁니다.+" + sPointNo, "SL");
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

				EJBConnector ejbConn1 = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
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
				jparam2.setField("YD_WRK_DUTY",		yfCommUtils.getWorkDuty());
				jparam2.setField("YD_WRK_PARTY",	yfCommUtils.getWorkParty());
				jparam2.setField("YD_UP_WO_LOC",	sYD_STK_LOC);
				jparam2.setField("YD_DN_WO_LOC",	sPointNo + "PT010101");
				jparam2.setField("UP_FUNC",			YfConstant.CRANE_FUNC_S);
				jparam2.setField("PUT_FUNC",		YfConstant.CRANE_FUNC_S);
				commDao.insert(jparam2, insertCrnWrslt, logId, methodNm, "작업실적 저장");
			}
			
			
			/*******************************************
			 * 출하실적취소 시 상차이전 위치로 원복 한다. 2023.11.09 문제윤 주임 출하관제
			 *******************************************/
			if ("Y".equals(sCancelChk)) {
				jrParam.setField("COIL_NO", Stl_no);
				JDTORecordSet jsCoilcomm = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getPtCoilComm", logId, methodNm, "코일공통 조회");
				
				if (jsCoilcomm.size() <= 0) {
					throw new Exception("TB_PT_COILCOMM 존재하지 않는 코일번호:" + Stl_no);
				}
				
				String ydEqpGp  	 = yfCommUtils.trim(jsCoilcomm.getRecord(0).getFieldString("YD_EQP_GP")); //현위치가 차량
				String ydStrLocHis1  = yfCommUtils.trim(jsCoilcomm.getRecord(0).getFieldString("YD_STR_LOC_HIS1")); //상차전 저장위치
				
				if (!"PT".equals(ydEqpGp)) {
					yfCommUtils.printLog(logId, "상차완료백업이 안된 코일이여서 상차전 위치로 원복 할 수 없습니다.", "SL");
					throw new Exception("상차완료백업이 안된 코일이여서 상차전 위치로 원복 할 수 없습니다." + Stl_no);
				}
				 
						
					   ydStkColGp = yfCommUtils.trim(ydStrLocHis1.substring(0, 6));
				String ydStkBedNo = yfCommUtils.trim(ydStrLocHis1.substring(6, 8));
				String ydStkLyrNo = yfCommUtils.trim(ydStrLocHis1.substring(8, 10));
				String	ydGpBay	  = yfCommUtils.trim(ydStrLocHis1.substring(1, 2));
				
				
				 				
				jrParam.setField("STL_NO"       , Stl_no );
				jrParam.setField("YD_STK_LYR_STAT", "C" );
				jrParam.setField("YD_STK_COL_GP", ydStkColGp);
				jrParam.setField("YD_STK_BED_NO", ydStkBedNo);  
				jrParam.setField("YD_STK_LYR_NO", ydStkLyrNo);	
				
				
				jsCoilcomm = commDao.select(jrParam, "com.inisteel.cim.yf.acoil.dao.ACoilDAO.getStackLayerInfo", logId, methodNm, "저장위치 조회");
				
				if (jsCoilcomm.size() > 0) {
					String stlNoChk  	 = yfCommUtils.trim(jsCoilcomm.getRecord(0).getFieldString("STL_NO"));
					
					if(!"".equals(stlNoChk)){
						yfCommUtils.printLog(logId, "상차전 위치로 원복 할 위치에 다른 코일번호 존재하여 원복 불가.", "SL");
						throw new Exception("상차전 위치로 원복 할 위치에 다른 코일번호 존재하여 원복 불가."+ydStrLocHis1+" 코일번호:" + stlNoChk);
					}
				}
				
				/***********************************
				 * TB_YD_STKLYR 저장위치 Update 
				 ***********************************/
				
				/*--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStackLayerStat
				UPDATE TB_YF_STKLYR            
				   SET MOD_DDTT             = SYSDATE             
				     , MODIFIER             = :V_MODIFIER             
				     , STL_NO				= :V_STL_NO
					 , YD_STK_LYR_STAT	    = :V_YD_STK_LYR_STAT
				 WHERE YD_STK_COL_GP     = :V_YD_STK_COL_GP
				   AND YD_STK_BED_NO     = :V_YD_STK_BED_NO
				   AND YD_STK_LYR_NO   = :V_YD_STK_LYR_NO
				 */
				commDao.update(jrParam, "com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStackLayerStat", logId, methodNm, "적치단 수정");				
				yfCommUtils.printLog(logId, "[코일제품출하완료]["+Stl_no+"]에 저장위치맵을 복원합니다.", "SL");
				
		 
				/***********************************
				 * TB_YF_STOCK 저장품 Update 
				 ***********************************/
				jrParam.setField("DEL_YN",			"N");
				jrParam.setField("MODIFIER",		modifier);
				jrParam.setField("STOCK_MOVE_TERM",	"M2");
				jrParam.setField("STL_NO",			Stl_no);
				commDao.update(jrParam, updateStock2, logId, methodNm, "TB_YF_STOCK 수정");
				
				
				/***********************************
				 * Coil 공통 Table 저장위치 Update 
				 ***********************************/	
				//코일 공통 상차백업 위치로 위치 변경 작업
				JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
				recInTemp1.setResultCode(logId);	//Log ID
				recInTemp1.setResultMsg(methodNm);	//Log Method Name
				recInTemp1.setField("STL_NO",	Stl_no);				//재료번호
				recInTemp1.setField("YD_LOC",	ydStkColGp+ydStkBedNo+ydStkLyrNo);	//현재위치

				EJBConnector ejbConn1 = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
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
				jparam2.setField("YD_EQP_ID",		"1"+ydGpBay+YfConstant.EQUIP_KIND_CR + "00");
				jparam2.setField("YD_WRK_DUTY",		yfCommUtils.getWorkDuty());
				jparam2.setField("YD_WRK_PARTY",	yfCommUtils.getWorkParty());
				jparam2.setField("YD_UP_WO_LOC",	"1"+ydGpBay + "PT010101");
				jparam2.setField("YD_DN_WO_LOC",	ydStkColGp + ydStkBedNo +ydStkLyrNo);
				jparam2.setField("UP_FUNC",			YfConstant.CRANE_FUNC_S);
				jparam2.setField("PUT_FUNC",		YfConstant.CRANE_FUNC_S);
				commDao.insert(jparam2, insertCrnWrslt, logId, methodNm, "작업실적 저장");
		 
			}

			yfCommUtils.printLog(logId, "=============코일제품출하완료 종료========", "SL");

			yfCommUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * [A] 오퍼레이션명 :코일이송하차대기장도착PDA(DMYDR073)
	 * YfRcvFaEJBSBean.java	-> rcvDDMYDR073
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1091(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YfCoilL3RcvPISeEJBSBean.rcvM10LMYDJ1091 [코일이송하차대기장도착PDA 송신(DMYDR073)]] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try {
			yfCommUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "1", "*");
//			if ("N".equals(sApplyYnPI)) {
//				yfCommUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
			yfCommUtils.printParam(logId, rcvMsg);
			
            rcvMsg.setField("TC_CREATE_DDTT" , yfCommUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
            rcvMsg.setField("TRANS_ORD_DT"   , yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
            rcvMsg.setField("TRANS_ORD_SEQNO", yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번    
	
			//수신 항목 값
			String cancelChk = yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));	//Y: 취소 , N: 지시
			
			if("Y".equals(cancelChk))
			{
				String sPI0003 = yfComm.ACoilApplyYn("PI0003","*","*");
				
				if ("Y".equals(sPI0003)) {
					yfCommUtils.printLog(logId, "M10LMYDJ1091 취소시 신로직 들어감 'PI0003' = 'Y" , "SL");
					jrRtn = this.procDmTcCnclPI(rcvMsg);
				} else {
					//취소('Y')
					this.receiveCancelPI(rcvMsg);
				}
			}
			else
			{
				//지시('N')
				//기본 수신 항목 값
				String msgId    = yfCommUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
				String modifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

				if ("".equals(modifier)) { modifier = msgId.substring(3,12); }

				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("MODIFIER", modifier); //수정자 셋팅
				
				yfCommUtils.printLog(logId, "=============코일이송하차지시PDA 수신 시작========", "SL");			
				
				//수신 항목 값
				String stlAppearGp 		= yfCommUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));    //재료외형
				String transOrdDt  		= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
				String transOrdSeqNo	= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
				String cancelYn     	= yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));
				String ydCarNo 			= yfCommUtils.trim(rcvMsg.getFieldString("CAR_NO"));
				String WorkGp 			= yfCommUtils.trim(rcvMsg.getFieldString("WORK_GP"));      //작업구분
				String ydCarKind		= yfCommUtils.trim(rcvMsg.getFieldString("CAR_KIND"));
				String CarudPntCd 		= yfCommUtils.trim(rcvMsg.getFieldString("CARUD_PNT_CD")); //하차포인트
				String transFrtoMoveGp 	= yfCommUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP")); //냉연이송구분
				int ydEqpWrkSh 			= Integer.parseInt(yfCommUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0"));
				
				yfCommUtils.printLog(logId, "stlAppearGp:" + stlAppearGp + "," +
											"transOrdDt:" + transOrdDt + "," +
											"transOrdSeqNo:" + transOrdSeqNo + "," +
											"cancelYn:" + cancelYn + "," +
											"ydCarNo:" + ydCarNo + "," +
											"WorkGp:" + WorkGp + "," +
											"ydCarKind:" + ydCarKind + "," +
											"CarudPntCd:" + CarudPntCd + "," +
											"transFrtoMoveGp:" + transFrtoMoveGp + "," +
											"ydEqpWrkSh:" + Integer.toString(ydEqpWrkSh), "SL");
				
				/**********************************************************
				* 1. 수신 항목 값 Check
				**********************************************************/
				jrParam.setField("MODIFIER",		modifier);
				jrParam.setField("STOCK_ITEM",		"CG");
				jrParam.setField("STOCK_MOVE_TERM",	"CS");
				jrParam.setField("TRANS_ORD_DATE",	transOrdDt);
				jrParam.setField("TRANS_ORD_DT",	transOrdDt);
				jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
				jrParam.setField("CAR_NO",			ydCarNo);
				jrParam.setField("CR_FRTOMOVE_GP",	transFrtoMoveGp);
				jrParam.setField("YD_CARPNT_CD",	CarudPntCd);
				jrParam.setField("TRANS_WORD_NO",	transOrdDt + transOrdSeqNo);
				
				if("".equals(CarudPntCd))
				{
					yfCommUtils.printLog(logId, methodNm + "DMYDR073전문...차량 : " + ydCarNo + "에 CARUD_PNT_CD(하차포인트) 값이 없습니다." , "SL");
					//throw new Exception("DMYDR073전문...차량 : " + ydCarNo + "에 CARUD_PNT_CD(하차포인트) 값이 없습니다.");
					
					//21.11.05 이호연사원 요청으로 포인트가 빈값으로 올경우 자동으로 포인트 셋팅
					//1순위 : 1통로 2통로 모두 비어있으면 01~08스판과 09~15스판에 적치가능한위치 갯수파악하여 적은쪽 으로 셋팅
					//2순위 : 1통로 2통로중 비어있는 차량포인트가 많은곳으로
					//3순위 : 1통로 2통로중 들어온차량 + 대기중인차량이 적은곳으로
					jrParam.setField("YD_EQP_WRK_SH",	ydEqpWrkSh + "");	//제품이송 하차매수
					JDTORecordSet jsYdCarPntCd = commDao.select(jrParam, getYdCarpntCd2, logId, methodNm, "하차포인트(YD_CARPNT_CD) 조회");
					CarudPntCd = yfCommUtils.trim(jsYdCarPntCd.getRecord(0).getFieldString("YD_CARPNT_CD"));	//조회된 YD_CARPNT_CD
				}				
				
				String Stl_no	= "";
				String Stl_noS	= ",";

				for(int i = 1 ; i <= ydEqpWrkSh ; i++)
				{
					Stl_no	= yfCommUtils.trim(rcvMsg.getFieldString("STL_NO"+i));
					
					if("".equals(Stl_no))
					{
						break;
					}
					
					Stl_noS	= Stl_noS + Stl_no + ",";

					jrParam.setField("STL_NO",				Stl_no);
					jrParam.setField("YD_CAR_UPP_LOC_CD",	yfCommUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i)));  // 차상위치
			    	commDao.update(jrParam, insStockTransInfo_PIDEV, logId, methodNm, "TB_YF_STOCK 등록");
				}
				
				//작업구분(1:내수/2:수출/3:연안해송/9:냉연이송)
				if("9".equals(WorkGp))
				{
					JDTORecordSet jsCarSch = commDao.select(jrParam, getYdCarYdCmbnCarldYn, logId, methodNm, "차량스케쥴 조회");

					if(jsCarSch.size() > 0)
					{
						yfCommUtils.printLog(logId, methodNm + " TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.:기존지시 삭제 " , "SL");

						String oldCarSchId = yfCommUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID")); //차량 작업지시

						jrParam.setField("YD_CAR_SCH_ID",	oldCarSchId);
						jrParam.setField("DEL_YN",			"Y");
						commDao.update(jrParam, updYdCarsch, logId, methodNm, "TB_YD_CARSCH 차량 스케줄정보");
					}

					//차량스케줄 생성
					String ydCarSchId = commDao.getSeqId(logId, methodNm, "CarSch");

					///도착가능 포인트 조회
					JDTORecord jrCarPnt = JDTORecordFactory.getInstance().create();
					jrParam.setField("STL_NOS",			Stl_noS);		//하차할 코일들모음
					jrParam.setField("YD_CARPNT_CD",	CarudPntCd);	//전문에서 받은 하차포인트
					
					JDTORecordSet bzInfo = commDao.select(jrParam, getCarPntBz, logId, methodNm, "해당차량포인트가 복포존운영중인지 체크");
					String bzGp = yfCommUtils.trim(bzInfo.getRecord(0).getFieldString("BZ_GP")); //복포존운영체크
					
					JDTORecordSet jsCarPnt = null;
					
					if("BZ".equals(bzGp))
					{
						jsCarPnt = commDao.select(jrParam, getYdCarPoint3, logId, methodNm, "차량포인트 조회");
					}
					else
					{
						jsCarPnt = commDao.select(jrParam, getYdCarPoint2, logId, methodNm, "차량포인트 조회");
					}

					if(jsCarPnt.size() <= 0 )
					{
						yfCommUtils.printLog(logId, methodNm + " TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다. " , "SL");
						m_ctx.setRollbackOnly();
						throw new Exception("TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다....");
					}

					jsCarPnt.first();

					jrCarPnt = jsCarPnt.getRecord();
					String ydStkColGp	= yfCommUtils.trim(jrCarPnt.getFieldString("YD_STK_COL_GP"));
					String ydPntCd      = yfCommUtils.trim(jrCarPnt.getFieldString("YD_PNT_CD"));
					String arrWlocCd    = yfCommUtils.trim(jrCarPnt.getFieldString("WLOC_CD"));
					String szMovYn      = yfCommUtils.trim(jrCarPnt.getFieldString("MOV_YN"));
					String ydCarpntCd	= yfCommUtils.trim(jrCarPnt.getFieldString("YD_CARPNT_CD"));
					
					yfCommUtils.printLog(logId, "ydStkColGp:" + ydStkColGp + ",ydPntCd:" + ydPntCd + ",arrWlocCd:" + arrWlocCd + ",szMovYn:" + szMovYn + ",ydCarpntCd:" + ydCarpntCd, "SL");
					
					CarudPntCd = ydCarpntCd;

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
					recInTemp.setField("YD_CARUD_LEV_DT",		yfCommUtils.getDateTime14());		//하차출발일시
					recInTemp.setField("YD_PNT_CD3",			ydPntCd);						//야드포인트코드3
					recInTemp.setField("YD_CARUD_STOP_LOC",		ydStkColGp);					//야드하차차정지위치
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
					commDao.insert(recInTemp, insYdCarsch_PIDEV, logId, methodNm, "TB_YD_CARSCH 등록");

					String gdsCarldLoc = "";
					String ydStkBedNo  = "";
					JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();

					for(int i = 1 ; i <= ydEqpWrkSh ; i++)
					{
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_CAR_SCH_ID",		ydCarSchId);
						recInTemp.setField("MODIFIER",			modifier);
						recInTemp.setField("STL_NO",			yfCommUtils.trim(rcvMsg.getFieldString("STL_NO"+i)));

						gdsCarldLoc = yfCommUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i));

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
						sndL2Msg.setField("STL_NO",				yfCommUtils.trim(rcvMsg.getFieldString("STL_NO"+i)));

						jrRtn = yfCommUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성
					}

		    		if("TT".equals(ydCarKind))
		    		{
			    		//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		    			EJBConnector ejbConn3 = new EJBConnector("default","YfCommCarMvSeEJB",this);
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
						recInTemp.setField("JMS_TC_CREATE_DDTT",	yfCommUtils.getDateTime14()); //JMSTC생성일시
						recInTemp.setField("YD_CARPNT_CD",			CarudPntCd);
						recInTemp.setField("YD_CAR_SCH_ID",			ydCarSchId);
						recInTemp.setField("CHK_YN",				"N");

						jrRtn = yfCommUtils.addSndData(jrRtn, recInTemp);

						yfCommUtils.printLog(logId, methodNm + "차량정지위치[" + ydStkColGp + "], 차량스케줄ID[" + ydCarSchId + "] -AB 차량입동지시요구 모듈을 호출", "SL");
					}
				}

				yfCommUtils.printLog(logId, "=============코일이송하차지시PDA 수신 종료========", "SL");
				
			}			
			
			yfCommUtils.printLog(logId, methodNm, "S-");			
			
			return jrRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}			
	}
	
	/**
	 * [A] 오퍼레이션명 :코일이송하차도착PDA(DMYDR074)
	 * YfRcvFaEJBSBean.java	-> rcvDMYDR074
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1101(JDTORecord rcvMsg) throws DAOException {

		String methodNm = "[YfCoilL3RcvPISeEJBSBean.rcvM10LMYDJ1101 [코일이송하차도착PDA 송신(DMYDR074)]] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try {
			yfCommUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "1", "*");
//			if ("N".equals(sApplyYnPI)) {
//				yfCommUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
			yfCommUtils.printParam(logId, rcvMsg);
			
            rcvMsg.setField("TC_CREATE_DDTT" , yfCommUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
            rcvMsg.setField("TRANS_ORD_DT"   , yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
            rcvMsg.setField("TRANS_ORD_SEQNO", yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
			//기본 수신 항목 값
			String msgId    = yfCommUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String tcCode  	= yfCommUtils.trim(rcvMsg.getFieldString("MQ_TC_CD"));		//TC_CODE
			String modifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier)) { modifier = msgId.substring(3,12); }

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			yfCommUtils.printLog(logId, "=============코일이송하차대기장도착PDA 수신 시작========", "SL");			

			//수신 항목 값
			String transOrdDt  		= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			String transOrdSeqNo	= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String ydCarNo 			= yfCommUtils.trim(rcvMsg.getFieldString("CAR_NO"));
//			String ydCardNo 		= yfCommUtils.trim(rcvMsg.getFieldString("CARD_NO"));
			String ydCardNo 		= "";
			String ydCarKind		= yfCommUtils.trim(rcvMsg.getFieldString("CAR_KIND"));     //차량종류
			String WorkGp 			= yfCommUtils.trim(rcvMsg.getFieldString("WORK_GP"));      //작업구분
			String CarldPntCd 		= yfCommUtils.trim(rcvMsg.getFieldString("CARUD_PNT_CD")); //하차포인트 CARUD_PNT_CD ?
			String transFrtoMoveGp 	= yfCommUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP")); //냉연이송구분

			String ydCarProgStat    = "";
			String ydCarWrkGp 		= ""; 	//야드차량작업구분
			String ydEqpWrkStat		= ""; 	//야드설비작업상태

			String ydFrmYn			= "";	//차량형상사용유무
			
			String ydStkColGp		= "";
			
			// PIDEV
			yfCommUtils.printLog(logId, "transOrdDt : " + transOrdDt + ", " +
										"transOrdSeqNo : " + transOrdSeqNo + ", " +
										"ydCarNo : " + ydCarNo + ", " +
										"ydCarKind : " + ydCarKind + ", " +
										"WorkGp : " + WorkGp + ", " +
										"CarldPntCd : " + CarldPntCd + ", " +
										"transFrtoMoveGp : " + transFrtoMoveGp, "SL");
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrInTemp = JDTORecordFactory.getInstance().create();

			jrParam.setField("TRANS_ORD_DATE",	transOrdDt);
			jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
			JDTORecordSet jsStock = commDao.select(jrParam, getTransStockInfo, logId, methodNm, "운송실적번호에 맞는 제품번호가 존재 조회");

			String ydCarSchId = yfCommUtils.trim(jsStock.getRecord(0).getFieldString("YD_CAR_SCH_ID")); //차량 작업지시

			jrParam.setField("YD_CAR_SCH_ID",	ydCarSchId);
			JDTORecordSet jsCarSch = commDao.select(jrParam, getYdCarsch, logId, methodNm, "TB_YD_CARSCH 조회");

			if(jsCarSch.size() <= 0 )
			{
				yfCommUtils.printLog(logId, "["+methodNm+"] 차량 스케쥴 없음" , "SL");
				throw new Exception("차량 스케쥴 없음");
			}
			else
			{
				ydCarProgStat 	= yfCommUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT")); //차량진행상태
				ydCarWrkGp 		= yfCommUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_WRK_GP")); 	//야드차량작업구분
				ydEqpWrkStat	= yfCommUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_EQP_WRK_STAT")); 	//야드설비작업상태
			}
			
			// PIDEV
			yfCommUtils.printLog(logId, "ydCarSchId : " + ydCarSchId + ", ydCarProgStat : " + ydCarProgStat + ", ydCarWrkGp : " + ydCarWrkGp + ", ydEqpWrkStat : " + ydEqpWrkStat, "SL");
	
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
					ydStkColGp				= yfCommUtils.trim(jrCarPnt.getFieldString("YD_STK_COL_GP2"));
					String ydStkColActStat	= yfCommUtils.trim(jrCarPnt.getFieldString("YD_STK_COL_ACT_STAT"));
					
					// PIDEV
					yfCommUtils.printLog(logId, "ydStkColGp : " + ydStkColGp + ", ydStkColActStat : " + ydStkColActStat, "SL");

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

					    EJBConnector ejbConn = new EJBConnector("default", "YfCommCarMvSeEJB", this);
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

						EJBConnector ejbConn1 = new EJBConnector("default", "YfCommCarMvSeEJB", this);
						ejbConn1.trx("procYfLayerOpen", new Class[] { JDTORecord.class }, new Object[] { jrInTemp });

						/***************************************
						 * 	5.작업예약이 생성
						 **************************************/
						//작업예약,작업재료 등록
						String ydSchCd  = "";

						int  intTransOrdSeqNo   = Integer.parseInt(transOrdSeqNo);

						String[] rVal = commDao.getTrnFrtomoveGpPI("", methodNm, transOrdDt, transOrdSeqNo);
						String sTrnFrtomoveGp = rVal[0];
						String hIssueGp = rVal[1];
						
						yfCommUtils.printLog(logId, "sTrnFrtomoveGp:" + sTrnFrtomoveGp + "," + "hIssueGp:" + hIssueGp, "SL");
						
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
//					 	else if ( "21".equals(sTrnFrtomoveGp) )
//					 	{
//							//제품이송하차
//							if ("2".equals(CarldPntCd.substring(1, 2)))
//							{
//								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT22LM";
//							}
//							else
//							{
//								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT21LM";
//							}
//						}
					 	else if ( "21".equals(sTrnFrtomoveGp) && "K".equals(hIssueGp) )
					 	{
							//제품이송하차
							if ("2".equals(CarldPntCd.substring(1, 2))) {
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT22LM";
							} else {
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT21LM";
							}
						} else if ( "22".equals(sTrnFrtomoveGp) && "9".equals(hIssueGp) ) {
							//제품이송하차
							if ("2".equals(CarldPntCd.substring(1,2))) {
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT22LM";
							} else {
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1,2) + "PT21LM";
							}
						}
//					 	else if ( "23".equals(sTrnFrtomoveGp) )
//					 	{
//							//(임가공이송 OR 순천 이송) 스케줄 구분 : 냉연이송
//							//소재이송하차
//							if ("2".equals(CarldPntCd.substring(1, 2)))
//							{
//								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT22LM";
//							}
//							else
//							{
//								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT21LM";
//							}
//					 	}
					 	else if ( ("21".equals(sTrnFrtomoveGp) && "C".equals(hIssueGp))
								   ||
							      ("21".equals(sTrnFrtomoveGp) && "D".equals(hIssueGp)) ) {
							// (임가공이송 OR 순천 이송) 스케줄 구분 : 냉연이송
							// 소재이송하차
							if ("2".equals(CarldPntCd.substring(1,2))) {
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT22LM";
							} else {
								ydSchCd = YfConstant.YD_GP_1 + ydStkColGp.substring(1, 2) + "PT21LM";
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

		    			String ydWbookId = yfComm.procCarWkBookInsert(jrInTemp);	//작업예약 생성

		    			if(ydWbookId.equals(YfConstant.RETN_CD_FAILURE))
		    			{
		    				throw new Exception("작업예약ID 생성 실패");
		    			}

		    			//----------------------------------------------------------------------
						// 야드저장위치제원(YFF1L001) 전문전송
						//----------------------------------------------------------------------
		    			jrInTemp.setField("YD_INFO_SYNC_CD",	"3");			//야드정보동기화코드(3:열)
		    			jrInTemp.setField("YD_STK_COL_GP",		ydStkColGp);	//야드적치열구분
						jrRtn = yfCommUtils.addSndData(commDao.getMsgL2("YFF1L001", jrInTemp));

						//------------------------------------------------------------------------------------------------------------
				    	//	차량스케줄 도착상태 변경 처리
				    	//------------------------------------------------------------------------------------------------------------
					    jrInTemp = JDTORecordFactory.getInstance().create();
					    jrInTemp.setResultCode(logId);		//Log ID
					    jrInTemp.setResultMsg(methodNm);	//Log Method Name
						jrInTemp.setField("MODIFIER",			modifier);	//수정자
						jrInTemp.setField("YD_CAR_SCH_ID",		ydCarSchId);
						jrInTemp.setField("YD_CARUD_ARR_DT",	yfCommUtils.getDateTime14());
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
							jrRtn = yfCommUtils.addSndData(jrRtn, yfComm.procCarPlanInfo(jrParam));	//YFF1L008 생성
						//}

						//차량형상 사용안하면 크레인 스케줄 기동
						if("N".equals(ydFrmYn))
						{
							JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();

							//크레인 스케줄 기동 YFYFJ303 호출
							jrCrnSchMsg.setField("JMS_TC_CD",			"YFYFJ303");
							jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT",	yfCommUtils.getDateTime14()); //JMSTC생성일시
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

							jrRtn = yfCommUtils.addSndData(jrRtn, jrCrnSchMsg);
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

							jrRtn = yfCommUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));		//전송 Data 생성
						}
						//저장품제원정보 송신 끝

					}
				}
			}

			yfCommUtils.printLog(logId, "=============코일이송하차도착PDA 수신 종료========", "SL");			
			
			return jrRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}				
	}
	
	/**
	 * [A] 오퍼레이션명 :코일이송하차완료PDA(DMYDR075)
	 * YfRcvFaEJBSBean.java	-> rcvDMYDR075
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1111(JDTORecord rcvMsg) throws DAOException {

		String methodNm = "[YfCoilL3RcvPISeEJBSBean.rcvM10LMYDJ1111 [코일이송하차완료PDA 송신(DMYDR075)]] < " + rcvMsg.getResultMsg();
		
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
	    int			intRtnVal	= 0;
		
		try {
			yfCommUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "1", "*");
//			if ("N".equals(sApplyYnPI)) {
//				yfCommUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
			yfCommUtils.printParam(logId, rcvMsg);
			
            rcvMsg.setField("TC_CREATE_DDTT" , yfCommUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
            rcvMsg.setField("TRANS_ORD_DT"   , yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
            rcvMsg.setField("TRANS_ORD_SEQNO", yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
			//기본 수신 항목 값
			String msgId    = yfCommUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String tcCode  	= yfCommUtils.trim(rcvMsg.getFieldString("MQ_TC_CD"));		//TC_CODE
			String modifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier)) { modifier = msgId.substring(3,12); }

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			yfCommUtils.printLog(logId, "=============코일이송하차완료PDA 수신 시작========", "SL");

			//수신 항목 값
			String ydGp			= yfCommUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String stlAppearGp	= yfCommUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));   //재료외형
			String Stl_no 		= yfCommUtils.trim(rcvMsg.getFieldString("STL_NO"));    		//재료번호

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			jrParam.setField("TC_CD",		msgId);
			jrParam.setField("STL_NO",		Stl_no);
			jrParam.setField("MODIFIER",	modifier); //수정자
			JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd_PIDEV(jrParam);
			jrParam.setField("STOCK_MOVE_TERM",	yfCommUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));
			intRtnVal=	commDao.update(jrParam, updYfStock_PIDEV, logId, methodNm, "TB_YF_STOCK 수정");

			if(intRtnVal == 0)
			{
				yfCommUtils.printLog(logId, methodNm +" STL_NO : " + Stl_no +"에 대한 저장품 DATA가 존재하지 않음", "SL");
				return jrRtn;
			}
			

			/*
		 	 *차량  자동출발 모듈 CALL
		 	 */
			String ydCarNo  	= "";
			String ydStackColGp = "";

			if("*".equals(stlAppearGp))
			{
				JDTORecordSet jsCarSch = commDao.select(jrParam, getCarStartOrderInfo, logId, methodNm, "차량스케쥴 조회");

				if(jsCarSch.size() > 0)
				{
					yfCommUtils.printLog(logId, methodNm +" STL_NO : " + Stl_no +"에 대한 포인트정보가 존재함", "SL");
					ydCarNo  		= yfCommUtils.trim(jsCarSch.getRecord(0).getFieldString("CAR_NO"));
					ydStackColGp	= yfCommUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_STK_COL_GP"));

					jrParam.setField("CAR_NO",			ydCarNo);
					jrParam.setField("YD_STK_COL_GP",	ydStackColGp);

		 			if(!"".equals(ydCarNo))
		 			{
						//이송차량 출발 처리
						EJBConnector ejbConn1 = new EJBConnector("default", "YfCommCarMvSeEJB", this);
						JDTORecord jrRtn1 = (JDTORecord)ejbConn1.trx("procFrtoCarLevWrPI", new Class[] { JDTORecord.class }, new Object[] { jrParam });

						jrRtn = yfCommUtils.addSndData(jrRtn, jrRtn1);
		 			}
				}
			}			

			String CarDnWrkYn 	= "N"; // 하차작업 여부

			if("M10LMYDJ1111".equals(msgId))
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

			yfCommUtils.printLog(logId, "=============코일이송하차완료PDA 수신 종료========", "SL");

			yfCommUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * [A] 오퍼레이션명 :사외창고배차정보(냉연)(DMYFJ004)
	 * YfRcvFaEJBSBean.java	-> rcvDMYFJ004
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */			
	public JDTORecord rcvM10LMYDJ1181(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YfCoilL3RcvPISeEJBBean.rcvM10LMYDJ1181 [사외창고배차정보(냉연)(DMYFJ004)]] < " + rcvMsg.getResultMsg();
		String			logId		= rcvMsg.getResultCode();
	    JDTORecord		jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
	
	    JDTORecord		jrRtn		= JDTORecordFactory.getInstance().create();
	    JDTORecordSet	rsResult	= null;
	
	    int				intRtnVal	= 0;
	    String			szMsg		= "";
	
		try	{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "1", "*");
//			if ("N".equals(sApplyYnPI)) {
//				yfCommUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
            rcvMsg.setField("TC_CREATE_DDTT" , yfCommUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
	
			//기본 수신 항목 값
			String msgId    = yfCommUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
	
			if ("".equals(modifier)) { modifier = msgId.substring(3,12); }
	
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅
	
			yfCommUtils.printLog(logId, "=============사외창고배차정보(냉연) 시작========", "SL");
	
			//수신 항목 값
			String szCAR_NO					= yfCommUtils.trim(rcvMsg.getFieldString("CAR_NO"));		       //차량번호
			String szCR_CAR_NO				= yfCommUtils.trim(rcvMsg.getFieldString("CR_CAR_NO"));	           //냉연차량번호
			String szTRANS_ORD_DT			= yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE"));		   //운송지시일자
			String szTRANS_ORD_SEQNO		= yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ"));		   //운송지시순번
			String szCARLD_SH				= yfCommUtils.trim(rcvMsg.getFieldString("CARLD_SH"));			   //상차매수
			String szCOIL_MTL_OUT_SHP_TP	= yfCommUtils.trim(rcvMsg.getFieldString("COIL_MTL_OUT_SHP_TP"));  //코일재료외형구분
			String szCANCLE_YN				= yfCommUtils.trim(rcvMsg.getFieldString("CANCLE_YN"));			   //취소여부
	
			jrParam.setField("CAR_NO",		szCAR_NO);		//차량번호 셋팅
			jrParam.setField("CR_CAR_NO",	szCR_CAR_NO);	//냉연차량번호 셋팅
			
			if("Y".equals(szCANCLE_YN))
			{
				//사외창고배차정보(냉연) 취소
				/*
				 * 사외창고배차정보(냉연) 취소는 차량이 도착 전에만 취소 할 수 있다.(차량이 도착 후에는 취소 불가)
				 * 1. 차량상태 확인하여 하차도착이후 스케줄이 있으면 취소 불가
				 * 2. 차량스케줄에 발지를 사외야드로 만들어서 반품화면에서 조회할수 없게 만듬
				 * 3. TB_YF_STOCK 삭제
				 */
				String			svYD_CAR_PROG_STAT	= "0";
				
				rsResult = commDao.select(jrParam, getYdCarschDaoByYdCarNoOrTrnEqpCd, logId, methodNm, "차량번호로 진행중인 차량스케줄조회");

				if(rsResult != null && rsResult.size() > 0)
				{
					svYD_CAR_PROG_STAT	= rsResult.getRecord(0).getFieldString("YD_CAR_PROG_STAT");	//야드차량진행상태
					
					throw new Exception("야드차량 진행상태 값이 " + svYD_CAR_PROG_STAT + " 입니다! 사외창고배차정보(냉연) 취소 불가!");
				}
				
				//발지개소코드를 바꿔서 화면에서 조회되지 않도록 수정함
				jrParam.setField("SPOS_WLOC_CD",	"DZY30");	//사외야드 : DZY30
				jrParam.setField("CAR_NO",			szCAR_NO);
				commDao.update(jrParam, updYdCarschS, logId, methodNm, "TB_YD_CARSCH 수정");
				
				for(int i = 1; i <= Integer.parseInt(szCARLD_SH) ; i++)
				{
					String szSTL_NO	= yfCommUtils.trim(rcvMsg.getFieldString("STL_NO" + i));
					
					if(!"".equals(szSTL_NO))
					{
						//TB_YF_STOCK 코일정보 삭제
			    		jrParam.setField("DEL_YN",			"Y");
						jrParam.setField("MODIFIER",		modifier);
						jrParam.setField("STOCK_MOVE_TERM",	"M2");
						jrParam.setField("STL_NO",			szSTL_NO);
						commDao.update(jrParam, updateStock2, logId, methodNm, "TB_YF_STOCK 수정");
					}
				}
				
				//차량스케줄 및 차량스케줄재료는 생성부터 DEL_YN = 'Y'로 만들어짐으로 따로 삭제처리 할 필요가 없다.
			}
			else
			{
				//사외창고배차정보(냉연)
				/*
				 * 1. 사외창고배차정보 에 상차매수가 0이면 오류 발생
				 * 2. TB_YD_CARSCH 생성(A : 하차출발 + DEL_YN = "Y" )
				 * 3. 하상차매수 만큼 FOR문을 돌며 TB_YD_CARFTMVMTL 재료등록 및  YFCRJ001(저장품제원정보요구)를 냉연MES에 요청한다.
				 */
				if("0".equals(szCARLD_SH))
				{
					throw new Exception("사외창고배차정보(냉연) 코일 매수가 '0' 입니다!");
				}
				
				szMsg="차량스케쥴 하차출발(A)로 INSERT < " + methodNm;
				yfCommUtils.printLog(logId, szMsg, "SL");
				
				String s_CAR_SCH_ID = commDao.getSeqId(logId, methodNm, "CarSch");				//신규 야드차량스케쥴ID
				
				jrParam.setField("YD_CAR_SCH_ID",		s_CAR_SCH_ID);					//야드차량스케쥴ID
				jrParam.setField("YD_CAR_PROG_STAT",	"A");							//차량진행상태 (A:하차출발)
				jrParam.setField("YD_EQP_ID",			YfConstant.YD_DM_CAR_EQP_ID);	//야드설비ID
				jrParam.setField("YD_CAR_USE_GP",		"G");							//야드차량사용구분 (L:구내운송, G:출하차량 )
				jrParam.setField("CAR_NO",				szCAR_NO);						//차량번호
				jrParam.setField("TRANS_ORD_DATE",		szTRANS_ORD_DT);				//운송지시일자
				jrParam.setField("TRANS_ORD_SEQNO",		szTRANS_ORD_SEQNO);				//운송지시순번
				jrParam.setField("YD_EQP_WRK_STAT",		"L");							//야드설비작업상태 (L:영차, U:공차)
				jrParam.setField("YD_EQP_WRK_SH",		szCARLD_SH);					//야드설비작업매수
				jrParam.setField("YD_EQP_WRK_WT",		"0");							//야드설비작업중량...차후 저장품제원정보 받아서 업데이트
				jrParam.setField("SPOS_WLOC_CD",		"DZY30");						//발지개소코드(상차지)-DZY30(사외야드)
				jrParam.setField("YD_PNT_CD1",			"");							//발지야드포인트코드
				jrParam.setField("ARR_WLOC_CD",			"D2Y45");						//착지개소코드(하차지)-박판창고
				jrParam.setField("YD_PNT_CD3",			"");							//착지야드포인트코드
				jrParam.setField("FRTOMOVE_PLANT_GP",	"CR");							//이송공장구분

				commDao.insert(jrParam, insCarSchUdS_PIDEV, logId, methodNm, "차량스케쥴 하차출발(A) INSERT ");
				
				for(int i = 1 ; i <= Integer.parseInt(szCARLD_SH) ; i++)
				{
					String szSTL_NO	= yfCommUtils.trim(rcvMsg.getFieldString("STL_NO" + i));
					
					if(!"".equals(szSTL_NO))
					{
						String s_YD_STK_BED_NO = "";
						
						//for문1번처리 : TB_YD_CARFTMVMTL 에 상차되어있는 재료 등록  
						if(i >= 10)
						{
							s_YD_STK_BED_NO	= ("" + i);
						}
						else
						{
							s_YD_STK_BED_NO	= ("0" + i);
						}
						
						jrParam.setField("YD_CAR_SCH_ID",		s_CAR_SCH_ID);		//야드차량스케쥴ID
						jrParam.setField("STL_NO",				szSTL_NO);
						jrParam.setField("YD_STK_BED_NO",		s_YD_STK_BED_NO);
						jrParam.setField("YD_STK_LYR_NO",		"001");
						
						commDao.insert(jrParam, insCarSchmtlS, logId, methodNm, "차량재료 스케쥴 INSERT TB_YD_CARFTMVMTL");
						
						//for문2번처리 : 냉연으로 해당 코일의 YFCRJ001(저장품제원정보) 전문생성
						jrRtn = yfCommUtils.addSndData(jrRtn, commDao.getMsgL3("YFCRJ001", jrParam));
					}
				}
			}
			
			yfCommUtils.printLog(logId, "=============사외창고배차정보(냉연) 종료========", "SL");
	
			yfCommUtils.printLog(logId, methodNm, "S-");
	
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**	 
	 * [A] 오퍼레이션명 :열연옥외야드도착실적  
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1161(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "[YfCoilL3RcvPISeEJBBean.rcvM10LMYDJ1161] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();  
		try {
			yfCommUtils.printLog(logId, mthdNm, "S+");
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}			
			
			//수신 항목 값
			String msgId  = yfCommUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			// DAO 및 UTIL 객체 생성
			YmEtcDao YmEtcDao   = new YmEtcDao();
			// 레코드 선언
			JDTORecord recIn    = null;
			
		    String sMsg         = "";
		    String sMethodName  = "rcvM10LMYDJ1161";	    	        	
			int intRtnVal = 0;
			
	    	sMsg = "[출하]박판냉연 열연옥외창고도착실적 수신";
	    	yfCommUtils.printLog(logId, sMsg , "SL");
	    	
	    	/*
	    	YD_GP			야드구분	CHAR	1	V		
	    	TRANSMIT_DATE	전송일자	CHAR	8			
	    	SEND_SEQ		전송순번	NUMBER	5			
	    	CANCEL_YN		취소유무	CHAR	1	Y	Y: 취소 , N: 지시	
	    	FR_GBN			사외창고구분 CHAR  2   VA : 열연옥외야드장
	    	TABLE : TB_DM_SETTLEDOWNWRSLTIFTEMP
			*/ 
	    	
	    	String sYdGp         = yfCommUtils.trim(rcvMsg.getFieldString("YD_GP"));    
	    	String sTransmitDate = yfCommUtils.trim(rcvMsg.getFieldString("TRANSMIT_DATE"));    
	    	String sSendSeq      = yfCommUtils.trim(rcvMsg.getFieldString("SEND_SEQ"));    
	    	String sCancelYn     = yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));    
	    	String sFrGbn        = yfCommUtils.trim(rcvMsg.getFieldString("FR_GBN"));  
	    	String sTrnReqDate   = yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE"));    
	    	String sTrnReqSeq    = yfCommUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ"));    
	    	String sCarNo        = yfCommUtils.trim(rcvMsg.getFieldString("CAR_NO")); 

	    	if("N".equals(sCancelYn)){
		    	
	    		// 레코드 생성
				recIn = JDTORecordFactory.getInstance().create(); 
				recIn.setField("TRN_REQ_DATE", sTrnReqDate);
				recIn.setField("TRN_REQ_SEQ" , sTrnReqSeq);
				recIn.setField("CAR_NO"      , sCarNo);
				
				String sFrYdGp	= "";
				String sYdLoc	= "";
				
				if("".equals(sFrGbn)){
					sFrYdGp = sYdGp;
					sYdLoc  = sYdGp+"A11111111";
				}else{
					sFrYdGp = sYdGp;
					sYdLoc  = sFrGbn+"11111111";
				}

				recIn.setField("YD_GP", sFrYdGp);
				recIn.setField("YD_STR_LOC", sYdLoc);

			 if(sYdGp.equals("V")){ 
					//intRtnVal = YmEtcDao.uptYmEtcDao(recIn, 82); //열연 사외창고 인경우
	 
					intRtnVal = commDao.update(recIn, "com.inisteel.cim.ym.bcoil.dao.updTB_DM_SETTLEDOWNWRSLTIFTEMPCOIL_PIDEV", logId, sMethodName, "코일공통 수정");
									
					if(intRtnVal < 0){
						sMsg = "COILCOMM[COIL작업지시] Error :: TRN_REQ_DATE(" + sTrnReqDate + ") SEND_SEQ(" + sTrnReqSeq + ") [" + intRtnVal + "]" + "PARAMETER ERROR";
						yfCommUtils.printLog(logId, sMsg , "SL");
						return ;
					} else if(intRtnVal == 0){
						sMsg = "COILCOMM[COIL작업지시] Error :: TRN_REQ_DATE(" + sTrnReqDate + ") SEND_SEQ(" + sTrnReqSeq + ")[" + intRtnVal + "]" + "DO NOT EXIST";
						yfCommUtils.printLog(logId, sMsg , "SL");
						return ;
					}
				}
	    	}
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}
	
	/**
	 * 대기장도착실적(DMYDR061)
	 * YfRcvFaEJBSBean.java	-> rcvDMYDR061
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procM10LMYDJ_DMYDR061(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "대기장도착실적 수신[YfCoilL3RcvPISeEJBBean.procM10LMYDJ_DMYDR061] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		JDTORecordSet rsResult 	= null;

		try
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String modifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
			String msgId    = yfCommUtils.getMsgId(rcvMsg);                         //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			if ("".equals(modifier)) { modifier = msgId.substring(3,12); }

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			yfCommUtils.printLog(logId, "=============대기장도착실적 수신 시작========", "SL");

			//수신 항목 값
			String szYD_GP 				= yfCommUtils.trim(rcvMsg.getFieldString("YD_GP"));				//야드구분
			String szCMBN_CARLD_YN 		= yfCommUtils.nvl(rcvMsg.getFieldString("CMBN_CARLD_YN"),"N");	//조합상차유무(시작:S, 종료:E, 단일상차:N)
			String szWORK_GP 			= yfCommUtils.trim(rcvMsg.getFieldString("WORK_GP"));				//작업구분
			String szTEL_NO 			= yfCommUtils.trim(rcvMsg.getFieldString("TEL_NO"));				//전화번호
			String szTRANS_ORD_DT  		= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));		//운송지시일자
			String szTRANS_ORD_SEQNO	= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));		//운송지시순번
			String szCAR_NO 			= yfCommUtils.trim(rcvMsg.getFieldString("CAR_NO"));				//차량번호
			String szCAR_KIND 			= yfCommUtils.nvl(rcvMsg.getFieldString("CAR_KIND"),"TR");		//차량종류
			String szWAIT_ARR_DDTT		= yfCommUtils.trim(rcvMsg.getFieldString("WAIT_ARR_DDTT"));		//대기장도착시간
			String szWAIT_ARR_GP		= yfCommUtils.trim(rcvMsg.getFieldString("WAIT_ARR_GP"));			//대기장도착구분
			String szTRANS_FRTOMOVE_GP	= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP"));	//1 운송 2 이송
			String szDRIVER_NAME		= yfCommUtils.trim(rcvMsg.getFieldString("DRIVER_NAME"));			//운전기사명

			//차량정보 존재여부 체크
			jrParam.setField("TRANS_ORD_DT",	szTRANS_ORD_DT);	//운송지시일자
			jrParam.setField("TRANS_ORD_SEQNO",	szTRANS_ORD_SEQNO);	//운송지시순번
			jrParam.setField("CAR_NO",			szCAR_NO);			// 차량번호
			jrParam.setField("CMBN_CARLD_YN",	szCMBN_CARLD_YN);	//조합상차유무
			rsResult = commDao.select(jrParam, getYdCarYdCmbnCarldYn, logId, methodNm, "차량정보 존재여부 체크");

			if (rsResult.size() > 0)
			{
				yfCommUtils.printLog(logId, methodNm + " TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.:기존지시 삭제 " , "SL");

				//기존 차량정보 삭제처리
				jrParam.setField("YD_CAR_SCH_ID",	yfCommUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID")));
				jrParam.setField("DEL_YN",			"Y");
				commDao.update(jrParam, updYdCarsch, logId, methodNm, "TB_YD_CARSCH 기존 차량스케줄 정보 삭제처리");
			}

			//도착가능 포인트 조회
			jrParam.setField("YD_GP",			szYD_GP);			//야드구분
			jrParam.setField("TRANS_ORD_DT",	szTRANS_ORD_DT);	//운송지시일자
			jrParam.setField("TRANS_ORD_SEQNO",	szTRANS_ORD_SEQNO);	//운송지시순번
			jrParam.setField("CMBN_CARLD_YN",	szCMBN_CARLD_YN);	//조합상차유무
			rsResult = commDao.select(jrParam, getYdCarPointSelect, logId, methodNm, "도착가능 차량포인트 조회");	//김일권주임 요청으로 순위조정 2통로 EFG 1통로 GFE 순으로

			if (rsResult.size() <= 0 )
			{
				//commUtils.printLog(logId, methodNm + " TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다. " , "SL");
				//m_ctx.setRollbackOnly();
				throw new Exception("TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다....");
			}

			//도착가능 포인트 조회 결과 값
			String szYD_STK_COL_GP 	= yfCommUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));	//야드적치열
			String szYD_CARPNT_CD 	= yfCommUtils.trim(rsResult.getRecord(0).getFieldString("YD_CARPNT_CD"));		//차량포인트
			String szYD_PNT_CD 		= yfCommUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));		//야드포인트코드
			String szSPOS_WLOC_CD	= yfCommUtils.trim(rsResult.getRecord(0).getFieldString("WLOC_CD"));			//개소코드
			String szCOIL_GP	= yfCommUtils.trim(rsResult.getRecord(0).getFieldString("COIL_GP"));			//코일구분

			yfCommUtils.printLog(logId, methodNm + " 도착가능 포인트 결과 적치열 : " + szYD_STK_COL_GP + " / 차량포인트 : " + szYD_CARPNT_CD + " / 야드포인트코드 : " + szYD_PNT_CD + " / 개소코드 : " + szSPOS_WLOC_CD , "SL");

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
			jrParam.setField("YD_CARLD_LEV_DT",		yfCommUtils.getDateTime14());		//상차출발일시
			jrParam.setField("YD_PNT_CD1",			szYD_PNT_CD);					//야드포인트코드1
			jrParam.setField("YD_CARLD_STOP_LOC",	szYD_STK_COL_GP);				//야드상차정지위치
			jrParam.setField("TRANS_ORD_DATE",		szTRANS_ORD_DT);				//운송지시일자
			jrParam.setField("TRANS_ORD_SEQNO",		szTRANS_ORD_SEQNO);				//운송지시순번

			if ("E".equals(szCMBN_CARLD_YN))
			{
				//2021.05.28 출하반 김일권 주임요청으로 복수동종료(E) + 냉연출하 는 우선순위 1->9 로 설정, 열연출하는 기존대로 1유지
				//냉연출하차량은 카드넘버가 5자리, 열연출하는 4자리
				//if(szCARD_NO.length() > 4)
				if(szCOIL_GP.equals("HR"))
				{
					//냉연출하차량
					jrParam.setField("YD_BAYIN_WO_SEQ",	"9");						//입동지시순번 - 기본값으로 설정(9)
				}
				else
				{
					//열연출하차량
					jrParam.setField("YD_BAYIN_WO_SEQ",	"1");						//입동지시순번 - 복수상차 마지막 1순위
				}
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
			commDao.insert(jrParam, insYdCarsch_PIDEV, logId, methodNm, "TB_YD_CARSCH 등록");

			//2020.07.09 배차차량관리 화면 복수동인경우 마지막이 아닌 세번째 이후 입동되는곳에서 대상/실적 중에 실적이 0으로 나오는경우가 있어서 주석처리함--다시 주석해제
			if("E".equals(szCMBN_CARLD_YN))
			{

				String sDoubleYardYn = "N";	//복수창고 여부
				int iYdEqpWrkSh = Integer.parseInt(yfCommUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수
				JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
				
				String sStlNo       = "";
				String sWorkState   = "";
				String ydGp         = "";
				String sGdsCarldLoc = "";
				String ydStkBedNo   = "";
				
				yfCommUtils.printLog(logId, "복수 창고 여부 check " , "SL");
				for (int ii = 1 ; ii <= iYdEqpWrkSh; ii++) {
					sStlNo 		 = yfCommUtils.trim(rcvMsg.getFieldString("STL_NO"+ii)); //재료번호
					sWorkState   = yfCommUtils.trim(rcvMsg.getFieldString("WORK_STATE"+ii));    //0: 미상차 , 1: 상차
					//재료가 없으면 종료
					if ("".equals(sStlNo)) {
						break;
					}
					
					// 복수창고 인 경우 상차된 상태
					if ("1".equals(sWorkState)) {    // 상차된 코일이 있는 경우
						//복수 창고 여부
						sDoubleYardYn = "Y";
					}
				}
				yfCommUtils.printLog(logId, "복수 창고: "+ sDoubleYardYn , "SL");
				
				// 복수 창고 임
				if ("E".equals(szCMBN_CARLD_YN) && "Y".equals(sDoubleYardYn)) {
					/**********************************************
					 *   복수창고  로직
					 **********************************************/
					yfCommUtils.printLog(logId, "복수 창고인 경우 " , "SL");
					for (int ii = 1 ; ii <= iYdEqpWrkSh; ii++) {
						ydGp         = yfCommUtils.trim(rcvMsg.getFieldString("YD_GP"+ii)); 
						sStlNo 		 = yfCommUtils.trim(rcvMsg.getFieldString("STL_NO"+ii)); //재료번호
						sGdsCarldLoc = yfCommUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+ii)); //상차위치
						sWorkState   = yfCommUtils.trim(rcvMsg.getFieldString("WORK_STATE"+ii));    //0: 미상차 , 1: 상차
						
						//재료가 없으면 종료
						if ("".equals(sStlNo)) {
							break;
						}
						
						if(sGdsCarldLoc.length() == 2 ) {  //일반출하시 안들어옴 . 이송일 경우 B1,B2...
							if ("A".equals(sGdsCarldLoc.substring(0, 1))) {
								
								ydStkBedNo = "0" + sGdsCarldLoc.substring(1, 2);
								
							} else if ("B".equals(sGdsCarldLoc.substring(0, 1))) {
								
								if ("1".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "06";
								} else if ("2".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "07";
								} else if ("3".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "08";
								} else if ("4".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "09";
								} else if ("5".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "10";
								}
								
							} else if ("C".equals(sGdsCarldLoc.substring(0, 1))) {
								
								if ("1".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "11";
								} else if ("2".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "12";
								} else if ("3".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "13";
								} else if ("4".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "14";
								} else if ("5".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "15";
								}
							}
						} else{
							ydStkBedNo = "0" + ii;
						}
						
						// 복수창고 인 경우 상차된 상태
						if ("1".equals(sWorkState)) {    // 상차된 상태
							
							/*************************
							 * 이송재료 정보 등록 
							 *************************/
							jrParam1 = yfCommUtils.getParam(logId, methodNm, modifier);
							jrParam1.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID   );
							jrParam1.setField("STL_NO"       , sStlNo       );
							jrParam1.setField("YD_STK_BED_NO", ydStkBedNo ); //야드 차상위치코드
							jrParam1.setField("YD_STK_LYR_NO", "001"        );
							jrParam1.setField("DEL_YN"       , "Y"          );
							commDao.insert(jrParam1, insCarFtMvMtl, logId, methodNm, "TB_YD_CARFTMVMTL 복수창고 이송작업재료등록");								
							
						} else {
							// 현재 야드  저장품 table 상차 위치 수정 : 차량예정 정보를 위해서
							jrParam1 = yfCommUtils.getParam(logId, methodNm, modifier);
							jrParam1.setField("YD_CAR_UPP_LOC_CD", ydStkBedNo   );
							jrParam1.setField("STL_NO"           , sStlNo       );

							commDao.update(jrParam1, updYfStock_PIDEV, logId, methodNm, "TB_YD_STOCK 삭제");
						}
					}
				}
				
			} else {
				//복수동 마지막 도착시 상차된 정보 INSERT
				//이송작업재료등록
				jrParam.setField("MODIFIER",	modifier);
				commDao.insert(jrParam, updCarFtMvMtlCmbnCarldYn_PIDEV, logId, methodNm, "TB_YD_CARFTMVMTL 복수동이송작업재료등록");
			}

			//입동지시 호출
			if (!"".equals(szYD_CARPNT_CD))
			{
				//도착가능 포인트가 있으면 입동지시 호출
				yfCommUtils.printLog(logId, methodNm + " 차량입동포인트["+szYD_CARPNT_CD+"], 차량스케줄ID["+szYD_CAR_SCH_ID+"] - 차량입동지시요구 모듈을 호출 " , "SL");

				JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);		//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("JMS_TC_CD",				"YFYFJ662");				//차량입동지시
				recInTemp.setField("JMS_TC_CREATE_DDTT",	yfCommUtils.getDateTime14());	//JMSTC생성일시
				recInTemp.setField("YD_CARPNT_CD",			szYD_CARPNT_CD);			//입동포인트
				recInTemp.setField("YD_CAR_SCH_ID",			szYD_CAR_SCH_ID);			//차량스케줄ID
				recInTemp.setField("CAR_NO",				szCAR_NO);
				recInTemp.setField("CAR_KIND",				szCAR_KIND);				//차량종류
				recInTemp.setField("TRANS_FRTOMOVE_GP",		szTRANS_FRTOMOVE_GP);		//1 운송 2 이송

				//JMS 전송
				jrRtn = yfCommUtils.addSndData(jrRtn, recInTemp);
			}

			yfCommUtils.printLog(logId, "=============대기장도착실적 수신 종료========", "SL");

			yfCommUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 코일이송상차대기장도착PDA(DMYDR070)
	 * YfRcvFaEJBSBean.java	-> rcvDMYDR070
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord procM10LMYDJ_DMYDR070(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "코일이송상차대기장도착PDA 수신[YfCoilL3RcvPISeEJBBean.procM10LMYDJ_DMYDR070] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	   
	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		int			intRtnVal	= 0;

		try
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String cancelChk = yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));	//Y: 취소 , N: 지시
			
			if("Y".equals(cancelChk))
			{
				//취소('Y')
				this.receiveCancelPI(rcvMsg);
			}
			else
			{
				//지시('N')
				JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();
				
				String modifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
	            String msgId    = yfCommUtils.getMsgId(rcvMsg);                         //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
	            if ("".equals(modifier)) { modifier = msgId.substring(3,12); }

				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("MODIFIER", modifier); //수정자 셋팅

				yfCommUtils.printLog(logId, "=============코일이송상차대기장도착PDA 수신 시작========", "SL");

				//수신 항목 값
				String stlAppearGp 		= yfCommUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));    //재료외형
				String transOrdDt  		= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
				String transOrdSeqNo	= yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
				String cancelYn     	= yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));
				String ydCarKind		= yfCommUtils.trim(rcvMsg.getFieldString("CAR_KIND"));
				String ydCarNo 			= yfCommUtils.trim(rcvMsg.getFieldString("CAR_NO"));
				String crFrtomoveGp		= yfCommUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP")); 	//냉연이송구분
				String WorkGp 			= yfCommUtils.trim(rcvMsg.getFieldString("WORK_GP"));   		//작업구분
				String CarldPntCd		= yfCommUtils.trim(rcvMsg.getFieldString("CARLD_PNT_CD"));   	//상차포인트
				String szMOV_YN			= yfCommUtils.trim(rcvMsg.getFieldString("UGNT_BAYIN_YN"));	//복수상차 마지막 차량에 대한 구분 Y: 1순위

				int ydEqpWrkSh 			= Integer.parseInt(yfCommUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0"));
				//복수동적용			
				String sCmbnCarldYn     = yfCommUtils.nvl(yfCommUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN")), "N"); //조합상차유무				
				String ydSndYn          = yfCommUtils.nvl(rcvMsg.getFieldString("YD_SND_YN"  ),"N"); //복수동시 필요
				
				yfCommUtils.printLog(logId, "sCmbnCarldYn:" + sCmbnCarldYn + ",ydSndYn:" + ydSndYn + ",ydCarKind:" + ydCarKind + ",WorkGp:" + WorkGp + ",CarldPntCd:" + CarldPntCd, "SL");
				
				/**********************************************************
				* 1. 수신 항목 값 Check
				**********************************************************/

				if (("Y".equals(cancelYn)))
				{
					return jrRtn;
				}
				else
				{
//복수동적용 재 송신시 필요 없음 
					if("N".equals(ydSndYn)) {	
					   /**********************************************************
						* 2. 저장품 이동 조건 수정
						**********************************************************/
						jrParam.setField("TRANS_ORD_DATE",	transOrdDt);
						jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
						jrParam.setField("CAR_NO",			ydCarNo);
						jrParam.setField("CR_FRTOMOVE_GP",	crFrtomoveGp);
						jrParam.setField("MODIFIER",		modifier);
						jrParam.setField("TC_CD",	        rcvMsg.getFieldString("MQ_TC_CD"));  //TC_CODE
						
						for(int i = 1 ; i<= ydEqpWrkSh; i++)
						{
							jrParam.setField("STL_NO",	yfCommUtils.trim(rcvMsg.getFieldString("STL_NO"+i))); //저장품 ID
						
							JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd_PIDEV(jrParam);
	
							jrParam.setField("STOCK_MOVE_TERM",		yfCommUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));
							jrParam.setField("YD_CAR_UPP_LOC_CD",	yfCommUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i)));	//차상위치
							intRtnVal=	commDao.update(jrParam, updYfStock_PIDEV, logId, methodNm, "TB_YF_STOCK 수정");
	
				            if(intRtnVal == 0)
				            {
				           	   continue;
				           	   //throw new Exception("수신한 재료번호 ["+ StStlNo+"]에 대한 저장품 DATA가 존재하지 않음");
							}
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
						jrParam.setField("CAR_NO",			ydCarNo);
						jrParam.setField("YD_CARPNT_CD",	CarldPntCd);
						jrParam.setField("CMBN_CARLD_YN",	sCmbnCarldYn);	//조합상차유무
						JDTORecordSet jsCarSch = commDao.select(jrParam, getYdCarYdCmbnCarldYn, logId, methodNm, "차량스케쥴 조회");

						if(jsCarSch.size() > 0)
						{
							yfCommUtils.printLog(logId, " TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.:기존지시 삭제 " , "SL");

							String ydOldCarSchId = yfCommUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID")); //차량 작업지시

							jrParam.setField("YD_CAR_SCH_ID",	ydOldCarSchId);
							jrParam.setField("DEL_YN",			"Y");
							commDao.update(jrParam, updYdCarsch, logId, methodNm, "TB_YD_CARSCH 차량 스케줄정보");
						}

						//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////////////////
						String ydCarSchId = commDao.getSeqId(logId, methodNm, "CarSch");
						
						// 박판 복수동 이송 기능_PI0201 -->
						String YnPI0201 = commDao.ApplyYn("", "YfCoilL3RcvPISeEJBBean.procM10LMYDJ_DMYDR070", "PI0201", "*", "*");
						
						JDTORecordSet jsCarPnt = null;
						
						if ("Y".equals(YnPI0201)) {
							jrParam.setField("YD_GP",			"1");			//야드구분
							jsCarPnt = commDao.select(jrParam, getYdCarPointSelect, logId, methodNm, "도착가능 차량포인트 조회");	//김일권주임 요청으로 순위조정 2통로 EFG 1통로 GFE 순으로
						} else {
							//출하에서 편성될때 포인트와 현업이 중간에 포인트의 FROM TO 스판을 변경할경우가 있어서 1번재료의 위치로 포인트를 찾아서 변경하기로함
							//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////
							//차량정보 존재여부 체크 /////////////////////////////////////////////////////////////////
							jrParam.setField("STL_NO",			yfCommUtils.trim(rcvMsg.getFieldString("STL_NO1"))); 	//1번재료
							jrParam.setField("YD_CARPNT_CD",	CarldPntCd);
							jsCarPnt = commDao.select(jrParam, getYdCarPoint1, logId, methodNm, "TB_YD_CARPOINT 조회");
						}
						// 박판 복수동 이송 기능_PI0201 <--

						String ydWlocCd 	= "";
						String ydStkColGp   = "";
						String ydPntCd      = "";

						if(jsCarPnt.size() > 0)
						{
							ydWlocCd 	= yfCommUtils.trim(jsCarPnt.getRecord(0).getFieldString("WLOC_CD"));
							ydStkColGp	= yfCommUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_GP"));
							ydPntCd     = yfCommUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_PNT_CD"));
							CarldPntCd	= yfCommUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_CARPNT_CD"));
							
							jrParam.setField("YD_CARPNT_CD",	CarldPntCd);

							JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
							recInTemp.setField("YD_CAR_SCH_ID",		ydCarSchId);
							recInTemp.setField("REGISTER",			modifier);
							recInTemp.setField("YD_EQP_ID",			YfConstant.YD_DM_CAR_EQP_ID);	//야드설비ID
							recInTemp.setField("YD_CAR_USE_GP",		YfConstant.YD_CAR_USE_GP_DM);	//차량사용구분
							recInTemp.setField("CAR_NO",			ydCarNo);						//차량번호
							recInTemp.setField("CAR_KIND",			ydCarKind);						//차량종류
							recInTemp.setField("YD_EQP_WRK_STAT",	"U");							//야드설비작업상태
							recInTemp.setField("SPOS_WLOC_CD",		ydWlocCd);						//발지개소코드
							recInTemp.setField("YD_CARLD_LEV_DT",	yfCommUtils.getDateTime14());		//상차출발일시
							recInTemp.setField("YD_PNT_CD1",		ydPntCd);						//야드포인트코드1
							recInTemp.setField("YD_CARLD_STOP_LOC",	ydStkColGp);					//야드상차정지위치
							recInTemp.setField("YD_CAR_PROG_STAT",	"1");							//상차출발상태
							recInTemp.setField("YD_CAR_WRK_GP",		WorkGp);
							recInTemp.setField("TRANS_ORD_DATE",	transOrdDt);					//운송지시일자
							recInTemp.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);					//운송지시순번

							if("Y".equals(szMOV_YN)||"E".equals(sCmbnCarldYn))
							{
								recInTemp.setField("YD_BAYIN_WO_SEQ",	"1");						//입동지시순번 - 제품이송하차 또는 복수상차는 1순위로 변경 함
							}
							else
							{
								szMOV_YN	= yfCommUtils.trim(jsCarPnt.getRecord(0).getFieldString("MOV_YN"));
								
								if("Y".equals(szMOV_YN))
								{
									//배차차량작업관리화면 -> 제품이송우선순위 사용함 일경우
									recInTemp.setField("YD_BAYIN_WO_SEQ",	"1");									//입동지시순번 - 1순위
								}
								else
								{
									recInTemp.setField("YD_BAYIN_WO_SEQ",	YfConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 - 기본값으로 설정(9)
								}
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
							//복수동적용					
							recInTemp.setField("CMBN_CARLD_YN"        , sCmbnCarldYn        ); //복수동 적용
							//차량스케줄 등록
							commDao.insert(recInTemp, insYdCarsch_PIDEV, logId, methodNm, "TB_YD_CARSCH 등록");

				    		if("TT".equals(ydCarKind))
				    		{
				    			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				    			// YfCommCarMvSeEJBSBean.java -> YfCarPointinforeg
				    			
				    			
						        EJBConnector ejbConn3 = new EJBConnector("default","YfCommCarMvSeEJB",this);
						        /*
								ejbConn3.trx("YfCarPointinforeg", new Class[]{ String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class },
							  	             new Object[]{ "C", ydCarNo, ydCardNo, ydStkColGp, "", "", "R", logId, methodNm });
								*/
								ejbConn3.trx("YfCarPointinforeg", new Class[]{ String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class },
						  	             new Object[]{ "C", ydCarNo, ydCarNo, ydStkColGp, "", "", "R", logId, methodNm });						        
				    		}
						}
						else
						{
							yfCommUtils.printLog(logId, "TB_YD_CARPOINT[차량포인트가 존재 안합니다..]" , "SL");
						}

						//입동지시요구모듈 호출(trailer인 경우)///////////////////////////////////////////////////////
						if("T".equals(ydCarKind) || "TR".equals(ydCarKind))
						{
							if("E".equals(sCmbnCarldYn))
							{
								//복수동 마지막 도착시 상차된 정보 INSERT
								//이송작업재료등록
								jrParam = JDTORecordFactory.getInstance().create();
								jrParam.setField("TRANS_ORD_DT",	transOrdDt);
								jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
								jrParam.setField("CAR_NO",			ydCarNo);
								jrParam.setField("YD_CAR_SCH_ID",	ydCarSchId);
								jrParam.setField("MODIFIER",		modifier);
								commDao.insert(jrParam, updCarFtMvMtlCmbnCarldYn_PIDEV, logId, methodNm, "TB_YD_CARFTMVMTL 복수동이송작업재료등록");
							}
							
							if(!"".equals(CarldPntCd)) {
								/*
								 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
								 */
								yfCommUtils.printLog(logId, "차량정지위치[" + ydStkColGp + "], 차량스케줄ID[" + ydCarSchId + "], " + "YD_CARPNT_CD[" + CarldPntCd + "-PDA AB차량입동지시요구 모듈을 호출 시작" , "SL");

								JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setResultCode(logId);		//Log ID
								recInTemp.setResultMsg(methodNm);	//Log Method Name
								recInTemp.setField("JMS_TC_CD",				"YFYFJ662");		//차량입동지시 요구 기존:YDYDJ662
								recInTemp.setField("JMS_TC_CREATE_DDTT",	yfCommUtils.getDateTime14()); //JMSTC생성일시
								recInTemp.setField("YD_CARPNT_CD",			CarldPntCd);
								recInTemp.setField("YD_CAR_SCH_ID",			ydCarSchId);
								recInTemp.setField("CHK_YN",				"N");
								recInTemp.setField("CR_FRTOMOVE_GP",		crFrtomoveGp);	//냉연이송구분

								jrRtn = yfCommUtils.addSndData(jrRtn, recInTemp);
							}
						}
					}
				}
			}
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 차량정지위치활성/비활성처리 PI_
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procCarPosActiveOrInActive_PIDEV(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "차량정지위치활성/비활성처리[YfCoilL3RcvPISeEJBSBean.procCarPosActiveOrInActive_PIDEV] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			yfCommUtils.printLog(logId, mthdNm, "S+");

			// 수신 항목 값
			String ydStkColGp = yfCommUtils.nvl(rcvMsg.getFieldString("YD_STK_COL_GP"), "");
			String ydCarUseGp = yfCommUtils.nvl(rcvMsg.getFieldString("YD_CAR_USE_GP"), "");
			String sTrnEqpCd = yfCommUtils.nvl(rcvMsg.getFieldString("TRN_EQP_CD"), "");
			String sCarNo = yfCommUtils.nvl(rcvMsg.getFieldString("CAR_NO"), "");
			String ydStkColActStat = yfCommUtils.nvl(rcvMsg.getFieldString("YD_STK_COL_ACT_STAT"), "");

			String sModifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER")); // 수정자(Backup Only)

			yfCommUtils.printLog(logId, "ydStkColGp:" + ydStkColGp + ",ydCarUseGp:" + ydCarUseGp + ",sTrnEqpCd:" + sTrnEqpCd + ",sCarNo:" + sCarNo + ",ydStkColActStat:" + ydStkColActStat, "SL");

			/**********************************************************
			 * 0. 항목 값 Check
			 **********************************************************/
			if ("".equals(ydStkColGp)) {
				yfCommUtils.printLog(logId, "적치열이 존재하지 않습니다.....", "S-");
				return jrRtn;
			}

			if ("L".equals(ydCarUseGp)) { // 구내운송
				if ("L".equals(ydStkColActStat) && "".equals(sTrnEqpCd)) {
					yfCommUtils.printLog(logId, "구내운송은 운송장비코드가 존재해야합니다.", "S-");
					return jrRtn;
				}
			}

			if ("G".equals(ydCarUseGp)) { // 출하
				if ("L".equals(ydStkColActStat)) {
					if ("".equals(sCarNo)) {
						yfCommUtils.printLog(logId, "출하차량은 차량번호가 존재해야합니다.", "S-");
						return jrRtn;
					}
				}
			}

			String ydStkBedActStat = "";
			String ydStkLyrActStat = "";

			if ("L".equals(ydStkColActStat)) { // 적치가능
				ydStkBedActStat = "L";
				ydStkLyrActStat = "E";
			} else if ("C".equals(ydStkColActStat)) { // 비활성화
				ydStkBedActStat = "C"; // 비활성화
				ydStkLyrActStat = "C"; // 비활성화
				ydCarUseGp = "";
			} else {
				yfCommUtils.printLog(logId, "[" + ydStkColActStat + "]사용할 수 없는 상태값입니다.", "S-");
				return jrRtn;
			}

			/**********************************************************
			 * 1.적치열 활성/비활성 처리
			 **********************************************************/
			JDTORecord jrParam = yfCommUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CAR_USE_GP", ydCarUseGp);
			jrParam.setField("TRN_EQP_CD", sTrnEqpCd);
			jrParam.setField("CAR_NO", sCarNo);
			jrParam.setField("YD_STK_COL_ACTIVE_STAT", ydStkColActStat);
			jrParam.setField("YD_STK_COL_GP", ydStkColGp);
			commDao.update(jrParam, updYdStkcol_PIDEV, logId, mthdNm, "적치열 수정");

			/*
			UPDATE TB_YD_CARPOINT
			   SET MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			     , CAR_NO              = :V_CAR_NO
			     , CARD_NO             = :V_TRN_EQP_CD
			     , YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT     
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdCarpointActYn", logId, mthdNm, "차량포인트 수정");

			/**********************************************************
			 * 2.적치베드 활성/비활성 처리
			 **********************************************************/
			jrParam.setField("YD_STK_BED_ACTIVE_STAT", ydStkBedActStat);

			commDao.update(jrParam, updYdStkbedYdStkColGp, logId, mthdNm, "적치베드 수정");

			/**********************************************************
			 * 3.적치단 활성/비활성 처리
			 **********************************************************/
			jrParam.setField("YD_STK_LYR_ACTIVE_STAT", ydStkLyrActStat);
			jrParam.setField("YD_STK_LYR_STAT", "E");

			commDao.update(jrParam, updYdStkLyrYdStkColGpClear, logId, mthdNm, "적치단 수정");

			yfCommUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 출하차량스케줄/차량Point삭제 기능 - 상차지시 취소 시 호출
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord delCarSchNCarPointForDist_PIDEV(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "출하차량스케줄/차량Point삭제[YfCoilL3RcvPISeEJBSBean.delCarSchNCarPointForDist_PIDEV] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			yfCommUtils.printLog(logId, mthdNm, "S+");

			// 수신 항목 값
			String msgId = yfCommUtils.getMsgId(rcvMsg); // EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sTransOrdDate = yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			String sTransOrdSeqno = yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));

			String sModifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER")); // 수정자(Backup Only)
			if ("".equals(sModifier)) {
				sModifier = msgId.substring(3, 12);
			}

			/**********************************************************
			 * 1. 차량스케줄 조회
			 **********************************************************/
			JDTORecord jrParam = yfCommUtils.getParam(logId, mthdNm, sModifier);

			jrParam.setField("TRANS_ORD_DATE", sTransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);

			/*
			SELECT *
			  FROM TB_YD_CARSCH
			 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND DEL_YN = 'N'
			*/
			JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarschByTransDTSeq", logId, mthdNm, "차량스케줄 조회");
			if (jsRst.size() <= 0) {
				yfCommUtils.printLog(logId, "운송지시일자 :[" + sTransOrdDate + "] , 운송지시순번[" + sTransOrdSeqno + "]로 차량스케줄 조회 결과 없음", "S-");
				return jrRtn;
			}

			String ydCarSchId = yfCommUtils.trim(jsRst.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
			String ydCarldStopLoc = yfCommUtils.trim(jsRst.getRecord(0).getFieldString("YD_CARLD_STOP_LOC"));
			String sCarNo = yfCommUtils.trim(jsRst.getRecord(0).getFieldString("CAR_NO"));

			/**********************************************************
			 * 2. 조회된 차량스케줄로 차량이송재료/차량스케줄 삭제
			 **********************************************************/
			jrParam = yfCommUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
			jrParam.setField("DEL_YN", "Y");
			
			/*
			UPDATE TB_YD_CARFTMVMTL
			   SET MODIFIER  = :V_MODIFIER
				 , MOD_DDTT  = SYSDATE
				 , DEL_YN    = :V_DEL_YN
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID    
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.delYdCarftmvmtl", logId, mthdNm, "이송재료 삭제");

			/*
			UPDATE TB_YD_CARSCH
			   SET MODIFIER  = :V_MODIFIER
			     , MOD_DDTT  = SYSDATE
			     , DEL_YN    = :V_DEL_YN
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.delYdCarsch", logId, mthdNm, "차량스케줄 삭제");

			yfCommUtils.printLog(logId, "ydCarSchId:" + ydCarSchId + ",ydCarldStopLoc:" + ydCarldStopLoc + ",sCarNo:" + sCarNo, "SL");

			/**********************************************************
			 * 3. 차량진행상태에 따른 차량정지위치 Clear 실행 - 상차도착 시에만 Clear
			 **********************************************************/
			if (!"".equals(ydCarldStopLoc)) {
				jrParam = yfCommUtils.getParam(logId, mthdNm, sModifier);

				jrParam.setField("YD_STK_COL_GP", ydCarldStopLoc);

				jsRst = commDao.select(jrParam, getYfStkcol, logId, mthdNm, "차량정지위치 조회");
				if (jsRst.size() <= 0) {
					yfCommUtils.printLog(logId, "차량정지위치[" + ydCarldStopLoc + "]조회 결과 없음", "S-");
					return jrRtn;
				}

				String ydStkColCarNo = yfCommUtils.trim(jsRst.getRecord(0).getFieldString("CAR_NO"));

				if (ydStkColCarNo.equals(sCarNo)) {
					yfCommUtils.printLog(logId, "차량정지위치 비활성화", "SL");
					jrParam.setField("YD_STK_COL_GP", ydCarldStopLoc);
					jrParam.setField("YD_CAR_USE_GP", "G"); // 출하차량
					jrParam.setField("YD_STK_COL_ACT_STAT", "C"); // 비활성화

					this.procCarPosActiveOrInActive_PIDEV(jrParam);

					jrParam.setField("STAT", "C");
					jrParam.setField("YD_STK_COL_GP", ydCarldStopLoc);

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
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointstackcolgpupdateC", logId, mthdNm, "TB_YD_CARPOINT 수정");
				} else {
					yfCommUtils.printLog(logId, "차량스케줄의 차량번호[" + sCarNo + "]와 적치열의 차량번호[" + ydStkColCarNo + "]와", "SL");
				}
			}

			yfCommUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 * 박판 출하전문 취소(PI)
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procDmTcCnclPI(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "박판 출하전문 취소[YfCoilL3RcvPISeEJBSBean.procDmTcCnclPI] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			yfCommUtils.printLog(logId, methodNm, "S+");
			yfCommUtils.printParam(logId, rcvMsg);
			String msgId = yfCommUtils.getMsgId(rcvMsg); // EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = yfCommUtils.trim(rcvMsg.getFieldString("MODIFIER")); // 수정자(Backup Only)

			if ("".equals(modifier)) { // 변경자 설정 (insert,update 문에서 사용)
				modifier = msgId.substring(3, 12);
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create(); // Query 실행시 파라메터 전달용 JDTORecord
			jrParam.setField("MODIFIER", modifier); // 수정자

			/**********************************************************
			 * 1. 크레인스케줄취소, 작업예약취소
			 **********************************************************/
			if ("M10LMYDJ1091".equals(msgId) // 하차대기장도착 (코일)
					|| "M10LMYDJ1031".equals(msgId)) // 운송상차지시 (코일)
			{
				// 출하차량스케줄/차량Point삭제
				this.delCarSchNCarPointForDist_PIDEV(rcvMsg);

				// 1.크레인 스케줄취소 ,2 작업예약취소
				String sTransOrdDate = yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
				String sTransOrdSeqno = yfCommUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));

				jrParam.setField("TRANS_ORD_DATE", sTransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);

				/*
				  SELECT
				      A.YD_CRN_SCH_ID
				    , A.YD_SCH_CD
				    , A.YD_WBOOK_ID
				  FROM
				      TB_YF_CRNSCH A
				    , TB_YF_CRNWRKMTL B
				    , TB_YF_STOCK C
				  WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
				  AND B.STL_NO = C.STL_NO
				  AND A.DEL_YN = 'N'
				  AND C.TRANS_ORD_DATE = :V_TRANS_ORD_DATE
				  AND C.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				  AND B.YD_AID_WRK_YN = 'N' --20210826 LHJ 보조작업이 취소대상인 경우 스케줄취소 대상에서 제외
				 */
				JDTORecordSet jsRst = commDao.select(jrParam, "yf.common.dao.getYdCrnschTransNo_PIDEV", logId, methodNm, "크레인스케줄 조회");

				if (jsRst.size() > 0) {
					yfCommUtils.printLog(logId, "스케줄 취소대상이 존재함", "SL");

					for (int i = 1; i <= jsRst.size(); ++i) {
						jsRst.absolute(i);
						jrParam.setField("YD_CRN_SCH_ID", jsRst.getRecord().getFieldString("YD_CRN_SCH_ID"));
						jrParam.setField("YD_SCH_CD", jsRst.getRecord().getFieldString("YD_SCH_CD"));
						jrParam.setField("YD_WBOOK_ID", jsRst.getRecord().getFieldString("YD_WBOOK_ID"));
						jrParam.setField("WRK_CNCL_YN", "Y"); // 작업취소 여부

						// 크레인 스케줄 삭제
						EJBConnector ejbConn = new EJBConnector("default", "ACoilJspSeEJB", this);
						JDTORecord jrRst = (JDTORecord) ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });

						jrRtn = yfCommUtils.addSndData(jrRtn, jrRst);
					}
//				} else if (jsRst.size() == 0) {
				}
				
				yfCommUtils.printLog(logId, "스케줄 취소대상이 존재안함", "SL");
				JDTORecordSet jsWrkBk = commDao.select(jrParam, getYfStockOfCarLoad, logId, methodNm, "작업예약조회");

				if (jsWrkBk.size() > 0) {
					for (int i = 1; i <= jsRst.size(); ++i) {
						jsWrkBk.absolute(i);
						jrParam.setField("YD_WBOOK_ID", jsWrkBk.getRecord().getFieldString("YD_WBOOK_ID"));

						// 크레인 작업예약 삭제
						EJBConnector ejbConn = new EJBConnector("default", "ACoilJspSeEJB", this);
						JDTORecord jrRst = (JDTORecord) ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });

						jrRtn = yfCommUtils.addSndData(jrRtn, jrRst);
					}
				}
				 
			}

			/**********************************************************
			 * 3. 출하전문 처리(재료번호 N개 : STL_NO1 ... STL_NO20)
			 **********************************************************/
			if ("M10LMYDJ1091".equals(msgId) // 하차대기장도착 (코일)
					|| "M10LMYDJ1031".equals(msgId)) // 운송상차지시 (코일)
			{
				String sStlNo = "";

				for (int i = 0; i < 20; ++i) {
					jrParam = JDTORecordFactory.getInstance().create(); // Query 실행시 파라메터 전달용 JDTORecord
					jrParam.setField("MODIFIER", modifier); // 수정자
					sStlNo = yfCommUtils.trim(rcvMsg.getFieldString("STL_NO" + (i + 1)));

					if ("".equals(sStlNo)) {
						break;
					}

					jrParam.setField("STL_NO", sStlNo);

					if ("M10LMYDJ1091".equals(msgId) // 하차대기장도착 (코일)
							|| "M10LMYDJ1031".equals(msgId)) // 운송상차지시 (코일)
					{
						jrParam.setField("TRANS_FRTOMOVE_GP", "");
						jrParam.setField("CR_FRTOMOVE_GP", "");
						jrParam.setField("CAR_NO", "");
						jrParam.setField("YD_CAR_UPP_LOC_CD", "");
						jrParam.setField("YD_RULE_PL_RS_GP", "");
						jrParam.setField("STOCK_MOVE_TERM", "");
					}

					// 운송지시일자(TRANS_ORD_DATE)와 운송지시순번(TRANS_ORD_SEQNO)은 클리어가 되어야 함
					jrParam.setField("TRANS_ORD_DATE", "");
					jrParam.setField("TRANS_ORD_SEQNO", "");

					/*
					  UPDATE USRYFA.TB_YF_STOCK SET
					      MODIFIER = :V_MODIFIER
					    , MOD_DDTT = SYSDATE
					    , STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM      --저장품이동조건
					    , YD_RULE_PL_RS_GP = :V_YD_RULE_PL_RS_GP    --조합구분
					    , TRANS_ORD_DATE = :V_TRANS_ORD_DATE        --운송지시
					    , TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO      --운송지시행번
					    , YD_CAR_UPP_LOC_CD = :V_YD_CAR_UPP_LOC_CD  --차상위치
					    , CAR_NO = :V_CAR_NO                        --차량번호
					    , CR_FRTOMOVE_GP = :V_CR_FRTOMOVE_GP        --냉연이송구분
					    , TRANS_FRTOMOVE_GP = :V_TRANS_FRTOMOVE_GP  --운송이송구분
					  WHERE STL_NO = :V_STL_NO
					 */
					commDao.update(jrParam, "com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYfStock2_PIDEV", logId, methodNm, "저장품 수정");
				}
			}

			yfCommUtils.printLog(logId, methodNm, "S-");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
}
