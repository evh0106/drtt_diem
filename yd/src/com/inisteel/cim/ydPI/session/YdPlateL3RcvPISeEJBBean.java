/**
 * @(#)YdPlateL3RcvPISeEJBBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2022/02/28
 *
 * @description      후판제품야드 물류진행 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2022/02/28   정종균  송정현      최초 등록
 * 
 */
package com.inisteel.cim.ydPI.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yd.common.delegate.YdDelegate;

import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;

import com.inisteel.cim.yd.common.dao.ydMarkingHistDao.YdMarkingHistDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ptPlateCommDao.PtPlateCommDao;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.dao.ymEtcDao.YmEtcDao;

import com.inisteel.cim.ydPI.common.M10YdExLm21SenderFaEJBBean;
import com.inisteel.cim.ydPI.common.util.PIYdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.ydPI.dao.YdPiDAO;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO;

/**
 *      [A] 클래스명 : PI관련 후판제품야드 출하수신 처리
 * 
 * @ejb.bean name="YdPlateL3RcvPISeEJB" jndi-name="YdPlateL3RcvPISeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/
public class YdPlateL3RcvPISeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private M10YdExLm21SenderFaEJBBean      M10YdExLm21Sender   = new M10YdExLm21SenderFaEJBBean();
	
	private PIYdUtils     commPiUtils = new PIYdUtils();
	private YdPiDAO       ydPiDAO     = new YdPiDAO();
	private YdPICommDAO   ydPICommDAO   = new YdPICommDAO();
	
	YdDelegate            ydDelegate  = new YdDelegate();
	
	private String szSessionName=getClass().getName();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
		
	/**	 
	 * [A] 오퍼레이션명 : 제품상태(후판)-운송지시대기,출하지시대기,목전,반품,보관지시
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1012(JDTORecord rcvMsg) throws DAOException { //S
		String mthdNm = "[YdPlateL3RcvPISeEJB.rcvM10LMYDJ1012] < " + rcvMsg.getResultMsg();
		String logId     = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		YdStockDao ydStockDao 		      = new YdStockDao();
		PtPlateCommDao ptPlateCommDao     = new PtPlateCommDao();
		YdMarkingHistDao ydMarkingHistDao = new YdMarkingHistDao();
		
		try {
			
			commPiUtils.printLog(logId, mthdNm, "S+");
	
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "T", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}					
			
			//수신 항목 값
			String msgId  = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String infoGp = commPiUtils.trim(rcvMsg.getFieldString("INFO_GP")); //야드구분
	
			JDTORecord reqMsg = JDTORecordFactory.getInstance().create();
			String sMsg = "";

			String ydGp                 = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"             ));   // 야드구분
			String sStlAppearGp         = commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"     ));   // 재료외형구분
            String sStlNo               = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"            ));   // 재료번호
            String sCurrProgCd          = commPiUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"      ));   // 현재진도코드
            String sOrdYeojaeGp         = commPiUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP"     ));   // 주문여재구분
            String sOrdNo               = commPiUtils.trim(rcvMsg.getFieldString("ORD_NO"            ));   // 주문번호
            String sOrdDtl              = commPiUtils.trim(rcvMsg.getFieldString("ORD_DTL"           ));   // 주문행번
            String sOrdGp               = commPiUtils.trim(rcvMsg.getFieldString("ORD_GP"            ));   // 수주구분
            String sCustCd              = commPiUtils.trim(rcvMsg.getFieldString("CUST_CD"           ));   // 고객코드
            String sDestCd              = commPiUtils.trim(rcvMsg.getFieldString("DEST_CD"           ));   // 목적지코드
            String sDlvrddRuleDd        = commPiUtils.trim(rcvMsg.getFieldString("DLVRDD_RULE_DD"    ));   // 납기기준일
            String sDestTelNo           = commPiUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO"       ));   // 목적지전화번호
            String sDistShipassignGp    = commPiUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP"));   // 출하배선지시구분
            String sShipassignWordDate  = commPiUtils.trim(rcvMsg.getFieldString("SHIPASSIGN_WORD_DATE")); // 배선작업지시일자
            String sShipassignWordSeono = commPiUtils.trim(rcvMsg.getFieldString("SHIPASSIGN_WORD_SEQNO"));// 배선작업지시순번
            
            String sShipCd              = commPiUtils.trim(rcvMsg.getFieldString("SHIP_CD"));              // 선박코드
            String sShipName            = commPiUtils.trim(rcvMsg.getFieldString("SHIP_NAME"));            // 선박명
            String sSailNo              = commPiUtils.trim(rcvMsg.getFieldString("SAILNO"));               // 선박항차
            String sDetailArrCd         = commPiUtils.trim(rcvMsg.getFieldString("DETAIL_ARR_CD"));        // 상세착지
            String sDeliverTermCd       = commPiUtils.trim(rcvMsg.getFieldString("DELIVER_TERM_CD"));      // 인도조건
            String sTransMeansGp        = commPiUtils.trim(rcvMsg.getFieldString("TRANS_MEANS_GP"));       // 운송수단구분
            String sIssueGp             = commPiUtils.trim(rcvMsg.getFieldString("ISSUE_GP"));             // 구분
//            String sGoodNo              = commPiUtils.trim(rcvMsg.getFieldString("GOODS_NO"));             // 재료번호
           
            String sCancelYn            = commPiUtils.trim(rcvMsg.getFieldString("CANCEL_YN"         ));   // Y: 취소 , N: 지시
            String sModifier            = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));             //수정자(Backup Only)
            if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }

            if("1".equals(infoGp)) { // 후판제품목전(DMYDR015)procPlageGdsOrdtrn
				
				/***************************************************************
				 * 목전 처리(DMYDR015)
				 ***************************************************************/
				JDTORecord jrStockSet = JDTORecordFactory.getInstance().create();
				jrStockSet.setField("STL_APPEAR_GP", 		sStlAppearGp);
				jrStockSet.setField("STL_NO", 				sStlNo);
				jrStockSet.setField("STL_PROG_CD", 			sCurrProgCd);
				jrStockSet.setField("ORD_YEOJAE_GP", 		sOrdYeojaeGp);
				jrStockSet.setField("ORD_NO", 				sOrdNo);
				jrStockSet.setField("ORD_DTL", 				sOrdDtl);
				jrStockSet.setField("ORD_GP", 				sOrdGp);
				jrStockSet.setField("CUST_CD", 				sCustCd);
				jrStockSet.setField("DEST_CD", 				sDestCd);
				jrStockSet.setField("YD_DLVRDD_RULE_DD", 	sDlvrddRuleDd);
				jrStockSet.setField("DEST_TEL_NO", 			sDestTelNo);
				jrStockSet.setField("DIST_SHIPASSIGN_GP", 	sDistShipassignGp);
				jrStockSet.setField("MODIFIER", 			sModifier);
				jrStockSet.setField("YD_AIM_RT_GP", 		"K3");

				//-------------------------------------------------------------------------
				// 여재다운/충당/목전시 해당 파일링코드 셋팅 시작
				//-------------------------------------------------------------------------
				
				JDTORecordSet jsStockGet = JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord jrParam 		= JDTORecordFactory.getInstance().create();
				
				
				jrParam.setField("PLATE_NO", sStlNo);
				/* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECOMM_PIDEV */
//PIDEV_S :병행가동용:PI_YD
				jrParam.setField("PI_YD",    	"T");						
				int intRtnVal = ydStockDao.getYdStock(jrParam, jsStockGet, 4);
				
				jsStockGet.first();
				JDTORecord jrStockGet = jsStockGet.getRecord();
				
				JDTORecord jrSizeSet		= JDTORecordFactory.getInstance().create();
				jrSizeSet.setField("YD_MTL_L", commPiUtils.trim(jrStockGet.getFieldString("PL_MEA_GDS_L")));
				jrSizeSet.setField("YD_MTL_W", commPiUtils.trim(jrStockGet.getFieldString("PL_MEA_GDS_W"))); 
				
				String sYdPilingCd = "";
				
				if ("K".equals(sCurrProgCd)) {
					
					//-------------------------------------------------------------------------
					//	주문재인 경우 OS공통테이블의 정보를 조회해서  Piling Code
					//-------------------------------------------------------------------------
					JDTORecord jrEdit  = JDTORecordFactory.getInstance().create();
					
					jrEdit.setField("ORD_NO",  commPiUtils.trim(jrStockGet.getFieldString("ORD_NO")));
					jrEdit.setField("ORD_DTL", commPiUtils.trim(jrStockGet.getFieldString("ORD_DTL")));
					
					JDTORecordSet jsOut = JDTORecordFactory.getInstance().createRecordSet("");
					//OS공통조회
					intRtnVal = ydStockDao.getYdStock(jrEdit, jsOut, 88);
					
					jsOut.first();
					
					jrEdit = jsOut.getRecord();
					
					sYdPilingCd = commPiUtils.trim(jrEdit.getFieldString("YD_PILING_CD"));  //oscomm에서 조회된 파일링코드 다시 불러와서 셋팅하고있는데
					                                                                        //실제 영업정보 변경(고객사, 목적지 등)되고 영업공통(oscomm)에는 안바뀌는 경우 다수 있음.
					                                                                        //입력받은 영업정보에 따라 파일링코드 재셋팅하는 절차 필요함.
																							//REQ202309487421
					
					//수신받은 고객사/목적지 정보 기반으로 파일링코드 재셋팅하자.
					//ORD_GP(주문구분:육송/수출/해송 등), DEST_CD, CUST_CD 정보기반으로 파일링 재셋팅.
					//수신항목중에 DETAIL_ARR_CD 값이 없음. 이 값은 파일링코드 셋팅에 필수값임. 물류에서 넣어줄수 있는지 문의필요. 
					
					commPiUtils.printLog(logId, "[후판제품목전] sYdPilingCd="+sYdPilingCd, "SL");	
					commPiUtils.printLog(logId, "[후판제품목전] rsOut.size()="+jsOut.size(), "SL");	
//					commPiUtils.printLog(logId, "[후판제품목전] recEdit="+jrEdit, "SL");	
					commPiUtils.printLog(logId, "[후판제품목전] ORD_PROG_CD="+commPiUtils.trim(jrEdit.getFieldString("ORD_PROG_CD")), "SL");	
					commPiUtils.printLog(logId, "[후판제품목전] ORD_PROG_STAT="+commPiUtils.trim(jrEdit.getFieldString("ORD_PROG_STAT")), "SL");
					
				} else if ("Z".equals(sCurrProgCd)) {
					
					jrSizeSet.setField("STRCHAR_CUST_CD" 		, "");	
					jrSizeSet.setField("STRCHAR_ORD_YEOJAE_GP" 	, "2");
					jrSizeSet.setField("YD_STRCHAR_GRP_CD" 		, "M001");
					
					PlateGdsYdUtil.getWTLGp(jrSizeSet);
						
					sYdPilingCd	  	= "M001" + 
							commPiUtils.trim(jrSizeSet.getFieldString("YD_MTL_W_GP")) + 
							commPiUtils.trim(jrSizeSet.getFieldString("YD_MTL_L_GP"));
				}
				
				jrStockSet.setField("PLATE_NO", 		sStlNo);
				jrStockSet.setField("YD_PILING_CD", 	sYdPilingCd);
				jrStockSet.setField("YD_BOOK_OUT_LOC", 	"88888");
				
				intRtnVal = ptPlateCommDao.updPtPlateComm(jrStockSet, 0);
				
				//-------------------------------------------------------------------------
				//여재다운/충당/목전시 해당 파일링코드 셋팅 완료 
				//-------------------------------------------------------------------------
				
				//저장품갱신******************************************************************************************** 
				intRtnVal = ydStockDao.updYdStock(jrStockSet, 0);

				commPiUtils.printLog(logId, "[후판제품목전] 전문수신처리 성공", "SL");
				//****************************************************************************************************
				JDTORecordSet jsResult = JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord jrResult = JDTORecordFactory.getInstance().create();
				
				String sMaxWorkStepNo = "";
				
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setField("STL_NO", sStlNo);
				int nRet = ydMarkingHistDao.getYdMarkingHist(jrParam, jsResult, 1);
				
				if(nRet < 0){
					sMsg = "TB_YD_MARKINGHIST MAX차수 조회 오류 Ret(" + nRet + ")";
					commPiUtils.printLog(logId, sMsg, "SL");
					return ;
				
				} else if(nRet == 0){
					sMsg = "TB_YD_MARKINGHIST MAX차수 조회 건수가 없음 Ret(" + nRet + ")";
					commPiUtils.printLog(logId, sMsg, "SL");
					sMaxWorkStepNo = "0";
				
				} else if(nRet > 0) {
					jsResult.first();
					jrResult = jsResult.getRecord();
					sMaxWorkStepNo = commPiUtils.trim(jrResult.getFieldString("WORK_STEP_NO"));
					
					sMsg = "TB_YD_MARKINGHIST MAX차수 조회 Ret(" + nRet + ") MAX(" + sMaxWorkStepNo + ")";
					commPiUtils.printLog(logId, sMsg, "SL");
					
				}			

				//====================================================================
				// 취소유무에 따라 "N"이면 Marking이력테이블에 등록 
				// "Y"면 Marking이력테이블에서 삭제처리(해당 재료번호의 MAX차수에 대해DEL_YN처리)
				//====================================================================	
				// 레코드 생성
				JDTORecord jrMarkSet = JDTORecordFactory.getInstance().create();

				if ("N".equals(sCancelYn)){
					// 등록처리
					jrMarkSet.setField("STL_NO"             , sStlNo);
					jrMarkSet.setField("WORK_STEP_NO"       , "" + (Integer.parseInt(sMaxWorkStepNo)+1));    // 해당재료번호의 최대차수에서 +1차수, 없다면 1차
					jrMarkSet.setField("REGISTER"           , sModifier);                                    
					jrMarkSet.setField("OCCUR_DDTT"         , commPiUtils.getCurDate("yyyyMMddHHmmss"));         // 현재일시 YYYYMMDDHH24MISS
					jrMarkSet.setField("MK_MOD_EXN"         , "N");                                           
					jrMarkSet.setField("MK_MOD_DT"          , "");                                           // 화면에서 처리하는 항목이므로 공백
					jrMarkSet.setField("MK_MOD_RSN"         , "3");                                          // 목전충당은 3으로 고정
					jrMarkSet.setField("MK_MOD_RSN_REG_DT"  , commPiUtils.getCurDate("yyyyMMddHHmmss"));         // 현재일시 YYYYMMDDHH24MISS 
					jrMarkSet.setField("ORD_NO"             , sOrdNo);
					jrMarkSet.setField("ORD_DTL"            , sOrdDtl);
					//=================================================
					//목전전문에는 지시차공장공정코드가 없음. 공백처리
					//=================================================
					jrMarkSet.setField("WO_CAR_PLNT_PROC_CD", "");      
					jrMarkSet.setField("CANCEL_YN"          , sCancelYn);
					
					nRet = ydMarkingHistDao.insYdMarkingHist(jrMarkSet);
					if(nRet <= 0){
						sMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] INSERT ERROR :: [" + nRet + "]";
						commPiUtils.printLog(logId, sMsg, "SL");
						return ;
					} else {
						sMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] INSERT SUCCESSFULL :: [" + nRet + "]";
						commPiUtils.printLog(logId, sMsg, "SL");
					}
					
				} else if ("Y".equals(sCancelYn)) {
					
					if (Integer.parseInt(sMaxWorkStepNo) > 0){
						// 삭제처리
						jrMarkSet.setField("MODIFIER"     , sModifier);
						jrMarkSet.setField("DEL_YN"       , "Y");
						jrMarkSet.setField("STL_NO"       , sStlNo);
						jrMarkSet.setField("WORK_STEP_NO" , sMaxWorkStepNo);        // 삭제는 해당 재료번호의 최대차수
						nRet = ydMarkingHistDao.updYdMarkingHist(jrMarkSet, 0);
						
						if(nRet <= 0){
							sMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] UPDATE ERROR :: [" + nRet + "]";
							commPiUtils.printLog(logId, sMsg, "SL");
							return ;					
						} else {
							sMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] UPDATE SUCCESSFULL :: [" + nRet + "]";
							commPiUtils.printLog(logId, sMsg, "SL");
						}		
						
						// Facade에 있던 취소유무 'Y' 수신시 처리하는 EJB호출 
//						EJBConnector ydEjbCon = new EJBConnector("default", this);
//						ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", rcvMsg);
//PIDEV						
						this.receiveCancel(rcvMsg);
						
					}
				}

				//======================================================
				// 저장품제원 : 후판제품 L2 로 송신(YDY8L002)
				//======================================================
				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
				
				if ("T".equals(ydGp)) { //- 2013.01.16 수정 (3기)
					sndL2Msg.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고
					//2024.08.05 YDY4L002 미사용 전문 타는 경우 있어, ELSE 문 제거 및 야드 T 일 경우만 송신처리. by HJW
					sndL2Msg.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
					sndL2Msg.setField("STL_NO"         , sStlNo);
					sndL2Msg.setField("YD_STK_COL_GP"  , "");
					sndL2Msg.setField("YD_STK_BED_NO"  , "");
					sndL2Msg.setField("CURR_PROG_CD"   , sCurrProgCd);
					
					ydDelegate.sendMsg(sndL2Msg);
					
				}
				
				// 전사물류개선 2021. 4. 3
				if(PlateGdsYdUtil.isSendToEaiY9_stlNo(sStlNo)){
					sndL2Msg.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
					ydDelegate.sendMsg(sndL2Msg);
				}
				
			} else if("2".equals(infoGp) ) { // 후판제품보관지시(DMYDR028)procPlGdsKeepOrd
				
//				//=============================================================
//				// Log 테이블 등록 
//				//=============================================================
//				szMsg = "[출하] 후판제품보관지시 수신";
//				ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_K, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
//				
//				//수신한 전문값******************************************************************************************
//				/*
//				TRANSMIT_DATE		지시일자
//				SEND_SEQ			지시순번
//				*/
//				intRtnVal = ydStockDao.updateStlHoldstat_01(ydDaoUtils.paraRecChkNull(inRecord,"TRANSMIT_DATE"),
//														    ydDaoUtils.paraRecChkNull(inRecord,"SEND_SEQ"));
//				
//				ydUtils.putLog(szSessionName, szMethodName,"YD_STOCK[후판제품보관지시] UPDATE Success",3);
//				//****************************************************************************************************
//				
//				// 2021. 5. 17 제원정보 송신
//				YdPlateCommDAO commDao	= new YdPlateCommDAO();
//				JDTORecordSet rsRecord = JDTORecordFactory.getInstance().createRecordSet("plateComm");
//				JDTORecord recResult = null;
//				if( commDao.select(inRecord, rsRecord, "com.inisteel.cim.yd.dao.ydstkbeddao.YdStockDao.getPlGdsKeepOrdList") > 0 ){
//					
//					int nRow = rsRecord.size();
//					for(int i=0; i < nRow; i++){
//						recResult = JDTORecordFactory.getInstance().create();
//						recResult.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고 L2전문
//						recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
//						recResult.setField("STL_NO"         , rsRecord.getRecord(i).getFieldString("STL_NO"));
//						recResult.setField("YD_STK_COL_GP"  , "");
//						recResult.setField("YD_STK_BED_NO"  , "");
//						
//						// 2021. 05. 17 전문생성 시점에 PT_PLATE_COMM에 진도코드가 바뀌지 않은 문제가 있어
//						// 진도코드를 파라메터로 넘겨 처리함
//						recResult.setField("CURR_PROG_CD"  , rsRecord.getRecord(i).getFieldString("CURR_PROG_CD"));
//						ydDelegate.sendMsg(recResult);		
//						
//						// 전사물류개선 2021. 4. 3
//						recResult.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
//						ydDelegate.sendMsg(recResult);
//					}
//				}
				
				
			} else if("3".equals(infoGp)) { // 후판제품반품(DMYDR034)procPlGdsRetngds
				
				/***************************************************************
				 * 후판제품 반품 (DMYDR034)
				 ***************************************************************/						
	            
				JDTORecord jrStockSet = JDTORecordFactory.getInstance().create();
				jrStockSet.setField("STL_APPEAR_GP", 		sStlAppearGp);
				jrStockSet.setField("STL_NO", 				sStlNo);
				jrStockSet.setField("STL_PROG_CD",			sCurrProgCd);
				jrStockSet.setField("ORD_YEOJAE_GP", 		sOrdYeojaeGp);
				jrStockSet.setField("ORD_NO", 				sOrdNo);
				jrStockSet.setField("ORD_DTL", 				sOrdDtl);
				jrStockSet.setField("ORD_GP", 				sOrdGp);
				jrStockSet.setField("CUST_CD", 				sCustCd);
				jrStockSet.setField("DEST_CD", 				sDestCd);
				jrStockSet.setField("DEST_TEL_NO", 			sDestTelNo);
				jrStockSet.setField("DIST_SHIPASSIGN_GP", 	sDistShipassignGp);
				jrStockSet.setField("TRANS_ORD_DATE", 		"");
				jrStockSet.setField("TRANS_ORD_SEQNO", 		"");
				jrStockSet.setField("CAR_NO", 				"");
				jrStockSet.setField("CARD_NO", 				"");
				jrStockSet.setField("CAR_LOTID", 			"");
				jrStockSet.setField("DEL_YN", 			    "N");
				jrStockSet.setField("MODIFIER", 			sModifier);				

				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
//				rVal= YdCommonUtils.getYdAimRtGp("P",inRecord );		
//				recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
				
				String[] rVal = new String[2];
				rVal= YdCommonUtils.getYdAimRtGp("P",rcvMsg );	
				jrStockSet.setField("YD_AIM_RT_GP", rVal[0]);				
				
				//****************************************************************************************************

							
				//저장품갱신******************************************************************************************** 
				int intRtnVal = ydStockDao.updYdStock(jrStockSet, 0);
				if(intRtnVal <= 0){
					throw new Exception("YD_STOCK[후판제품 반품] UPDATE Error :: [" + intRtnVal + "]");
				}
				//****************************************************************************************************
				//======================================================
				// 저장품제원 : 후판제품 L2 로 송신(YDY8L002)
				//======================================================
				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
				
				if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(sStlNo)) { //- 2013.01.17 수정 (3기)
					sndL2Msg.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고
					//2024.08.05 YDY4L002 미사용 전문 타는 경우 있어, ELSE 문 제거 및 야드 T 일 경우만 송신처리. by HJW
					sndL2Msg.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
					sndL2Msg.setField("STL_NO"         , sStlNo);
					sndL2Msg.setField("YD_STK_COL_GP"  , "");
					sndL2Msg.setField("YD_STK_BED_NO"  , "");
					
					// 2021. 05. 17 전문생성 시점에 PT_PLATE_COMM에 진도코드가 바뀌지 않은 문제가 있어
					// 진도코드를 파라메터로 넘겨 처리함
					sndL2Msg.setField("CURR_PROG_CD"  , sCurrProgCd);
					
					ydDelegate.sendMsg(sndL2Msg);		
				} 
				// 전사물류개선 2021. 4. 3
				if(PlateGdsYdUtil.isSendToEaiY9_stlNo( sStlNo) ){
					sndL2Msg.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
					ydDelegate.sendMsg(sndL2Msg);
				}	
				
							
			} else if("4".equals(infoGp)) { // 후판제품출하지시대기(DMYDR006)procPlateGdsDistOrdWait


				/***************************************************************
				 * 출하지시대기 처리(DMYDR006)
				 ***************************************************************/
				JDTORecord jrStockSet = JDTORecordFactory.getInstance().create();
				jrStockSet.setField("STL_APPEAR_GP", 		sStlAppearGp);
				jrStockSet.setField("STL_NO", 				sStlNo);
				jrStockSet.setField("STL_PROG_CD", 			sCurrProgCd);
				jrStockSet.setField("ORD_YEOJAE_GP", 		sOrdYeojaeGp);
				jrStockSet.setField("ORD_NO", 				sOrdNo);
				jrStockSet.setField("ORD_DTL", 				sOrdDtl);
				jrStockSet.setField("ORD_GP", 				sOrdGp);
				jrStockSet.setField("CUST_CD", 				sCustCd);
				jrStockSet.setField("DEST_CD", 				sDestCd);
				jrStockSet.setField("YD_DLVRDD_RULE_DD", 	sDlvrddRuleDd);
				jrStockSet.setField("DEST_TEL_NO", 			sDestTelNo);
				jrStockSet.setField("DIST_SHIPASSIGN_GP", 	sDistShipassignGp);
				jrStockSet.setField("MODIFIER", 			sModifier);
				
				//-------------------------------------------------------------------------
				//	여재다운/충당/목전시 해당 파일링코드 셋팅 시작 
				//-------------------------------------------------------------------------
				
				JDTORecord jrParam 		= JDTORecordFactory.getInstance().create();
				JDTORecordSet jsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				
				jrParam.setField("PLATE_NO", sStlNo);

//PIDEV_S :병행가동용:PI_YD
				jrParam.setField("PI_YD",    	"T");									
				int intRtnVal = ydStockDao.getYdStock(jrParam, jsOutRecSet, 4);
				
				jsOutRecSet.first();
				JDTORecord jrOutRecSet = jsOutRecSet.getRecord();
				
				jrParam	= JDTORecordFactory.getInstance().create();
				jrParam.setField("YD_MTL_L",  commPiUtils.trim(jrOutRecSet.getFieldString("PL_MEA_GDS_L"))); 
				jrParam.setField("YD_MTL_W",  commPiUtils.trim(jrOutRecSet.getFieldString("PL_MEA_GDS_W"))); 
				
				String sYdPilingCd = "";
				
				if("K".equals(sCurrProgCd)|| "3".equals(sCurrProgCd)) {
					
					//-------------------------------------------------------------------------
					//	주문재인 경우 OS공통테이블의 정보를 조회해서  Piling Code
					//-------------------------------------------------------------------------
					JDTORecord jrEdit = JDTORecordFactory.getInstance().create();
					jrEdit.setField("ORD_NO",  commPiUtils.trim(jrOutRecSet.getFieldString("ORD_NO")));  
					jrEdit.setField("ORD_DTL", commPiUtils.trim(jrOutRecSet.getFieldString("ORD_DTL"))); 
					
					JDTORecordSet jsOut = JDTORecordFactory.getInstance().createRecordSet("");
					JDTORecord    jrOut = JDTORecordFactory.getInstance().create();
					//OS공통조회
					intRtnVal = ydStockDao.getYdStock(jrEdit, jsOut, 88);
					
					jsOut.first();
				
					jrOut.setRecord(jsOut.getRecord());
					
					sYdPilingCd = commPiUtils.trim(jrOut.getFieldString("YD_PILING_CD")); 
					
				}else if("Z".equals(sCurrProgCd)){
					
					jrParam.setField("STRCHAR_CUST_CD" 			, "");	
					jrParam.setField("STRCHAR_ORD_YEOJAE_GP" 	, "2");
					jrParam.setField("YD_STRCHAR_GRP_CD" 		, "M001");

					PlateGdsYdUtil.getWTLGp(jrParam);
					
					sYdPilingCd	  	= "M001" + 
							          commPiUtils.trim(jrParam.getFieldString("YD_MTL_W_GP"))+ 
							          commPiUtils.trim(jrParam.getFieldString("YD_MTL_L_GP"));
				}
				
				jrStockSet.setField("PLATE_NO", 		sStlNo);
				jrStockSet.setField("YD_PILING_CD", 	sYdPilingCd);
				jrStockSet.setField("YD_BOOK_OUT_LOC", 	"77777");
				
				intRtnVal = ptPlateCommDao.updPtPlateComm(jrStockSet, 0);
				
				//-------------------------------------------------------------------------
				//	여재다운/충당/목전시 해당 파일링코드 셋팅 완료 
				//-------------------------------------------------------------------------

				String ydAimRtGp = "";
				
				if ("Y".equals(sCurrProgCd)) {
					ydAimRtGp =sCurrProgCd+"C";	//재공충당대기(A후판plate)
				} else if("G".equals(sCurrProgCd)) {
					ydAimRtGp =sCurrProgCd+"3";	//종합판정대기
				} else if("I".equals(sCurrProgCd)) {
					ydAimRtGp =sCurrProgCd+"3";	//반송대기
				} else if("H".equals(sCurrProgCd)) {
					ydAimRtGp =sCurrProgCd+"3";	//입고대기
				} else if("J".equals(sCurrProgCd)) {
					ydAimRtGp =sCurrProgCd+"3";	//반납대기
				} else if("Z".equals(sCurrProgCd)) {
					ydAimRtGp =sCurrProgCd+"3";	//제품충당대기
				} else if("X".equals(sCurrProgCd)) {
					ydAimRtGp =sCurrProgCd+"3";	//경매대상선정
				} else if("K".equals(sCurrProgCd)) {
					ydAimRtGp =sCurrProgCd+"3";	//출하지시대기		
				} else if("N".equals(sCurrProgCd)) {
					ydAimRtGp =sCurrProgCd+"3";	//운송지시대기
				} else if("M".equals(sCurrProgCd)) {
					ydAimRtGp =sCurrProgCd+"3";	//출하완료				
				}
				
				jrStockSet.setField("YD_AIM_RT_GP"   , ydAimRtGp);
				jrStockSet.setField("STL_PROG_CD"    , sCurrProgCd);			
				jrStockSet.setField("PRE_AR_STAT_CD" , "");              //보관매출발생상태코드 초기화 
				
				//저장품갱신********************************************************************************************	
				intRtnVal = ydStockDao.updYdStock(jrStockSet, 0);
				
				commPiUtils.printLog(logId, "[후판제품출하지시대기] 전문수신처리 성공", "SL");
			
				//======================================================
				// 저장품제원 : 후판제품 L2 로 송신(YDY8L002)
				//======================================================
				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
				
				if("T".equals(ydGp)) {
					sndL2Msg.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고
					//2024.08.05 YDY4L002 미사용 전문 타는 경우 있어, ELSE 문 제거 및 야드 T 일 경우만 송신처리. by HJW
					sndL2Msg.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
					sndL2Msg.setField("STL_NO"         , sStlNo);
					sndL2Msg.setField("YD_STK_COL_GP"  , "");
					sndL2Msg.setField("YD_STK_BED_NO"  , "");
					sndL2Msg.setField("CURR_PROG_CD"   , sCurrProgCd);
					
					ydDelegate.sendMsg(sndL2Msg);		
				} 	
				
				// 전사물류개선 2021. 4. 3
				if(PlateGdsYdUtil.isSendToEaiY9_stlNo(sStlNo) ){
					sndL2Msg.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
					ydDelegate.sendMsg(sndL2Msg);
				}
								
				
			} else if("5".equals(infoGp)) { // 후판제품운송지시대기(DMYDR018)procPlateGdsTrnOrdWait

				/***************************************************************
				 * 운송지시대기처리(DMYDR018)
				 ***************************************************************/				
				if("Y".equals(sCancelYn)){ //취소시 운송지시대기 마지막 셋팅시간도 같이 클리어해줘야할지 협의 필요.
					// (취소)
					//ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);
					this.receiveCancel(rcvMsg);
					return ; 
				}	
				
				JDTORecord jrStockSet = JDTORecordFactory.getInstance().create();
				jrStockSet.setField("STL_APPEAR_GP", 		sStlAppearGp);
				jrStockSet.setField("STL_NO", 				sStlNo);
				jrStockSet.setField("STL_PROG_CD",			sCurrProgCd);
				jrStockSet.setField("ORD_YEOJAE_GP", 		sOrdYeojaeGp);
				jrStockSet.setField("ORD_NO", 				sOrdNo);
				jrStockSet.setField("ORD_DTL", 				sOrdDtl);
				jrStockSet.setField("ORD_GP", 				sOrdGp);
				jrStockSet.setField("CUST_CD", 				sCustCd);
				jrStockSet.setField("DEST_CD", 				sDestCd);
				jrStockSet.setField("YD_DLVRDD_RULE_DD", 	sDlvrddRuleDd);
				jrStockSet.setField("DEST_TEL_NO", 			sDestTelNo);
				
				//>>>>>>>>>>>>선박정보<<<<<<<<<<<<<<<<<<
				jrStockSet.setField("DIST_SHIPASSIGN_GP", 	sDistShipassignGp);
				jrStockSet.setField("SHIPASSIGN_WORD_DATE", sShipassignWordDate);
				jrStockSet.setField("SHIPASSIGN_WORD_SEQNO",sShipassignWordSeono);
				jrStockSet.setField("SHIP_CD", 				sShipCd);
				jrStockSet.setField("SHIP_NAME", 			sShipName);
//PIDEV 확인					
//					jrStockSet.setField("RSHP_HOLD_NO", 		ydDaoUtils.paraRecChkNull(inRecord,"RSHP_HOLD_NO"));
				jrStockSet.setField("SAILNO", 				sSailNo);
				jrStockSet.setField("MODIFIER", 			sModifier);

				jrStockSet.setField("YD_AIM_RT_GP", "N3");

				//운송지시대상 출하보류코드(URGENT_DIST_YN)를 초기화한다. - 방법은 쿼리에서 항목 초기화
				//인도조건구분 코드에 운송수단구분 항목으로 대체
				if("".equals(sTransMeansGp)){
					jrStockSet.setField("DELIVER_TERM_CD", 		sDeliverTermCd);
				}else{
					jrStockSet.setField("DELIVER_TERM_CD", 		sTransMeansGp);
				}
				
				//jrStockSet.setField("DETAIL_ARR_CD", 		sDeliverTermCd);  //왜 인도처코드에 인도조건코드 셋팅?  기존 DMYDR018에도 DETAIL_ARR_CD 셋팅중.
				if(!"".equals(sDetailArrCd)){  //인도처코드 값을 수신 받으면 셋팅.
					jrStockSet.setField("DETAIL_ARR_CD", 		sDetailArrCd);
				}
				
				if("M".equals (sCurrProgCd)) {
					jrStockSet.setField("PRE_AR_STAT_CD", 	"1"); //선별대상에 포함된다.
				}
				
				//2013.02.15 진고코드가 'N' 이더라도 ISSUE_GP가 '2'(->보관매출발생제) 이면
				// YD_저장품의 보관매출방생코드를 '2'로 설정하여  선별 대상에서 제외 시킨다.
				if ("N".equals (sCurrProgCd) && "2".equals (sIssueGp)) {
					jrStockSet.setField("PRE_AR_STAT_CD", 	"2");
				}
				//****************************************************************************************************
				//2024.04.22 운송지시대기시간 셋팅시간 UPDATE (TEMP9 필드 활용 12자리-ex:202404221350)  
				/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPlateYdStockDMYDR028*/
				int intRtnVal = ydStockDao.updYdStock_DMYDR028(jrStockSet);
				if(intRtnVal <= 0){
					
					throw new Exception("YD_STOCK[후판제품운송지시대기] UPDATE Error :: [" + intRtnVal + "]");
				}
				//======================================================
				// 저장품제원 : 후판제품 L2 로 송신(YDY8L002)
				//======================================================
				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
				
				// 2021. 05. 17 전문생성 시점에 PT_PLATE_COMM에 진도코드가 바뀌지 않은 문제가 있어
				// 진도코드를 파라메터로 넘겨 처리함
				sndL2Msg.setField("CURR_PROG_CD", sCurrProgCd);
				if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(ydGp)) { 
					sndL2Msg.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고 L2전문
					//2024.08.05 YDY4L002 미사용 전문 타는 경우 있어, ELSE 문 제거 및 야드 T 일 경우만 송신처리. by HJW
					sndL2Msg.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
					sndL2Msg.setField("STL_NO"         , sStlNo);
					sndL2Msg.setField("YD_STK_COL_GP"  , "");
					sndL2Msg.setField("YD_STK_BED_NO"  , "");
					ydDelegate.sendMsg(sndL2Msg);	
				}
				
				// 전사물류개선 2021. 4. 3
				if(PlateGdsYdUtil.isSendToEaiY9_stlNo(sStlNo) ){
					sndL2Msg.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
					ydDelegate.sendMsg(sndL2Msg);
				}				
			}
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}
	}		
	
	
	/**	 
	 * [A] 오퍼레이션명 :후판제품반납대기(DMYDR009)- procPlGdsRetnWait
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1022(JDTORecord rcvMsg) throws DAOException { //S
		String mthdNm = "[YdPlateL3RcvPISeEJB.rcvM10LMYDJ1022] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commPiUtils.printLog(logId, mthdNm, "S+");
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "T", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}					
			
			YdStockDao ydStockDao   = new YdStockDao();
			
			String sMsg 			  = "";
//			String sMK_MOD_RSN        = "";
			int intRtnVal		      = 0;
			
			//전문받아서 msgId에 저장
			String msgId  = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			sMsg = "[출하] 후판제품반납대기 수신";
			commPiUtils.printLog(logId, sMsg, "SL");
			
			String ydGp                    = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"                  ));   // 야드구분
			String sSTL_APPEAR_GP          = commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"          ));   // 재료외형구분
            String sSTL_NO                 = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"                 ));   // 재료번호
            String sCURR_PROG_CD           = commPiUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"           ));   // 현재진도코드
            String sWO_CAR_PLNT_PROC_CD    = commPiUtils.trim(rcvMsg.getFieldString("WO_CAR_PLNT_PROC_CD"    ));
            String sURGENT_FRTOMOVE_WORD_GP= commPiUtils.trim(rcvMsg.getFieldString("URGENT_FRTOMOVE_WORD_GP"));
            String sModifier               = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));             //수정자(Backup Only)
            if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }


			/* 1	정보반송			2	정보반납			3	제품목전충당			4	현물반송			5	현물반납            */
//PIDEV 사용안함			//
//			if(sWO_CAR_PLNT_PROC_CD.equals("")){
//				// 정보반납
//				sMK_MOD_RSN = "2"; 
//			} else {
//				// 현물반납
//				sMK_MOD_RSN = "5"; 			
//			}
			
			//수신한 전문값******************************************************************************************
            JDTORecord recStockColumn 	= JDTORecordFactory.getInstance().create();
			recStockColumn.setField("STL_APPEAR_GP"          , sSTL_APPEAR_GP);
			recStockColumn.setField("STL_NO"                 , sSTL_NO);
			recStockColumn.setField("STL_PROG_CD"            , sCURR_PROG_CD);
			recStockColumn.setField("WO_CAR_PLNT_PROC_CD"    , sWO_CAR_PLNT_PROC_CD);
			recStockColumn.setField("URGENT_FRTOMOVE_WORD_GP", sURGENT_FRTOMOVE_WORD_GP);
			recStockColumn.setField("MODIFIER"               , sModifier);

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			String[] rVal = new String[1];
			rVal= YdCommonUtils.getYdAimRtGp("P",rcvMsg );
			rcvMsg.setField("YD_AIM_RT_GP", rVal[0]);
			rcvMsg.setField("STL_PROG_CD" , sCURR_PROG_CD);
			//****************************************************************************************************

			
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(rcvMsg, 0);
			if(intRtnVal <= 0){
				throw new Exception("YD_STOCK[후판제품반납대기] UPDATE Error :: [" + intRtnVal + "]");
			}
			commPiUtils.printLog(logId, " YD_STOCK[후판제품반납대기] UPDATE Success", "SL");
			
			//======================================================
			// 저장품제원 : 후판제품 L2 로 송신(YDY8L002)
			//======================================================
			JDTORecord recResult = JDTORecordFactory.getInstance().create();
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(ydGp)) { //- 2013.01.16 수정 (3기)
				recResult.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고
			//2024.08.05 YDY4L002 미사용 전문 타는 경우 있어, ELSE 문 제거 및 야드 T 일 경우만 송신처리 else문 제거. by HJW
				recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
				recResult.setField("STL_NO"         , commPiUtils.trim(recStockColumn.getFieldString("STL_NO"))); 
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				
				//  전문생성 시점에 PT_PLATE_COMM에 진도코드가 바뀌지 않은 문제가 있어
				// 진도코드를 파라메터로 넘겨 처리함
				recResult.setField("CURR_PROG_CD"  , sCURR_PROG_CD);
				
				ydDelegate.sendMsg(recResult);	
			}
			
			// 전사물류개선 2021. 4. 3
			if(PlateGdsYdUtil.isSendToEaiY9_stlNo( commPiUtils.trim(recStockColumn.getFieldString("STL_NO")))){
				recResult.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
				ydDelegate.sendMsg(recResult);
			}
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}		
	
	
	/**	 
	 * [A] 오퍼레이션명 :후판운송상차지시(DMYDR060)-procPlateGdsTrnOrd4GUpGrade
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1032(JDTORecord rcvMsg) throws DAOException {   //S 
		String mthdNm = "[YdPlateL3RcvPISeEJB.rcvM10LMYDJ1032] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "T", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}					
			
			String msgId    = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			//기본 변수 정의
			String sMethodName 		    = "procPlateGdsTrnOrd4GUpGrade";
			String sOperationName		= "후판용 제품운송상차지시(DMYDR060)";		
			String sMsg 				= "";
			String sSTL_NO				= null;
			String sYD_GP				= null;
			String sCARLD_PNT_CD		= null;
			String sCARLD_PNT_CD_old	= null;
			String sYD_STK_LOC			= null;
			String sYD_STK_LOC_old		= null;
			String sJOB_GP				= null;
			String sJOB_GP_old			= null;
			String sCAR_LOTID			= null;
			// 2021. 4. 7 추가
//			String sYD_FTMV_MEANS_GP = "";
//			String sCR_FRTOMOVE_GP = "";
			
			int intRtnVal 				= 0;
			
			JDTORecord	recPara			= null;
			JDTORecord	recEditColumn	= null;
			JDTORecord  recInTemp		= null;
			
			YdStockDao ydStockDao 		= new YdStockDao();		//저장품DAO
			
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
            String sCAR_NO            = commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"            ));   // 차량번호
            String sTRANS_ORD_DATE    = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"      ));   // 운송지시일자
			String sTRANS_ORD_SEQNO   = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"   ));   // 운송지시순번
            int intYD_EQP_WRK_SH      = Integer.parseInt(commPiUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수
            String sCarLotId          = commPiUtils.trim(rcvMsg.getFieldString("LOT_NO"            ));   // carLOT번호
			String sCMBN_CARLD_YN     = commPiUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN"     ));   // 조합상차유무
            String sCANCEL_YN         = commPiUtils.trim(rcvMsg.getFieldString("CANCEL_YN"         ));   // Y: 취소 , N: 지시

        	// 2021. 4. 7 추가
            /***************************************
             * PIDEV  전문 확인 해봐야 함 : TRANS_METHOD_GP = 32
             ***************************************/
            String sYD_FTMV_MEANS_GP  = commPiUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP" ));   // 운송이송구분 
            String sCR_FRTOMOVE_GP    = commPiUtils.trim(rcvMsg.getFieldString("TRANS_METHOD_GP"   ));   // 냉연이송구분
            
            
            String sCAR_KIND          = commPiUtils.trim(rcvMsg.getFieldString("CAR_KIND"          ));   // 차량종류
            String sSTL_APPEAR_GP     = commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"     ));   // 재료외형구분
			
            String sModifier          = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));             //수정자(Backup Only)			 
            if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }			
 
			
//			try{
//				sCAR_KIND 	= ydDaoUtils.paraRecChkNull(rcvMsg,"CAR_KIND");
//			}catch(Exception ex){
//				sCAR_KIND = "TR";
//			};
			
//			if("".equals(sUNIQUE_ID)){
//				sUNIQUE_ID = "0";
//			}		
			
			//전문항목 에러체크
			if("".equals(sCAR_NO)){
				sMsg="[" + sOperationName + "] CAR_NO IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");
			}
			if("".equals(sTRANS_ORD_DATE)){
				sMsg="[" + sOperationName + "] TRANS_ORD_DT IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");
				return ;
			}
			if("".equals(sTRANS_ORD_SEQNO)){
				sMsg="[" + sOperationName + "] TRANS_ORD_SEQNO IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");
				return ;
			}
			
			//기존 선별 LOT편성정보 호출 추가  
			if("N".equals(sCANCEL_YN)) { //선별LOT체크해서 있으면 일반베드->완산베드 변경
				//if(!"".equals(sCarLotId)){  //sCarLotId 값이 있으면 아래 베드 상태 변경.  LOTID존재 여부와 상관없이 처리.
				//베드 상태 변경 (rcvM10LMYDJ1132내  ydStkBedDao.updYdStkBedStat_01(recEditColumn); 부분 참조.
					YdStkBedDao     ydStkBedDao  = new YdStkBedDao();
					
					JDTORecord recEditColumn1 	= JDTORecordFactory.getInstance().create();
					recEditColumn1.setField("TRANS_ORD_DATE", 		sTRANS_ORD_DATE);
					recEditColumn1.setField("TRANS_ORD_SEQNO", 		sTRANS_ORD_SEQNO);
					recEditColumn1.setField("CAR_LOTID", 			sCarLotId);
					recEditColumn1.setField("MODIFIER", 			sModifier);
					
					sMsg="[" + sOperationName + "]["+intYD_EQP_WRK_SH+"]매수에 대한  저장품 업데이트 시작";
					commPiUtils.printLog(logId, sMsg , "SL");
					
					/*
					for(int i = 1 ; i<= intYD_EQP_WRK_SH; i++){
						
						sSTL_NO = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"+i));
						
						if(sSTL_NO.equals("")){
							break;
						}else {
							rcvMsg.setField("STL_NO", sSTL_NO);
						}
						
						recEditColumn1.setField("STL_NO",sSTL_NO);
						
						recEditColumn1.setField("CAR_LOTID", sCarLotId);
						
						
				        // 2021. 06. 03
						// Update컬럼을 최소화한다.
						recEditColumn1.setField("QUERY_ID", "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updYdStockByDMYDR060");

						// 2021. 06. 03 트랜잭션 분리를 위한 EJb Bean설정
						EJBConnector 	ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
						ejbConn.trx("updYdStkColTX", new Class[] { JDTORecord.class }, new Object[] { recEditColumn1 });	
						
						sMsg="[" + sOperationName + "] ["+i+"] 재료["+ sSTL_NO +"]에 TB_YD_STOCK 업데이트 완료 : ";
						commPiUtils.printLog(logId, sMsg , "SL");
						
						// 2021. 06. 03
						// 출하에서 야드에->Lot편성, 진행관리->운송대기 두 개 전문을 동시 전송 후 처리하는 과정에서 
						// TB_PT_PLATECOMM 테이블의 진도코드가 야드L2전송 전송 이후에 바뀌는 문제가 있어 하드코딩으로 처리한다.
						// 취소일경우엔 진도코드를
						recInTemp  = JDTORecordFactory.getInstance().create();
						recInTemp.setField("MSG_ID"         , "YDY8L002");
						recInTemp.setField("YD_INFO_SYNC_CD", "A");							
				    	recInTemp.setField("STL_NO"         , sSTL_NO);
				    	recInTemp.setField("YD_STK_COL_GP"  , YdConstant.YD_GP_PLATE2_GDS_YARD);
						recInTemp.setField("CURR_PROG_CD"   , YdConstant.PROG_CD_TRN_WAIT); // 운송대기(L)로 강제로 셋팅한다.
						ydDelegate.sendMsg(recInTemp);
					    	
				    	// 전사물류개선 2021. 4. 3 생산실적 발생시 동일하게 Y9도 전송처리한다.
				    	recInTemp.setField("MSG_ID"         , "YDY9L002");
				    	ydDelegate.sendMsg(recInTemp);
				    	
					} */  //YD_STOCK은 어차피 아래에서 업데이트 하므로 업데이트 안함.
				//}  //CAR_LOTID 존재 여부 
					/*
					 *  야드베드입출고상태 : 일반베드이면  완산베드로 변경,
				 	 *  선별상태 : 출하송신
					 */
					intRtnVal = ydStkBedDao.updYdStkBedStat_01(recEditColumn1);
						
					// 전사물류개선 2021. 4. 3
					// 지시확정이 변경된 BED정보를 전송한다.
					JDTORecord params = JDTORecordFactory.getInstance().create();
					JDTORecordSet rsBedRecord 	= JDTORecordFactory.getInstance().createRecordSet("ydPlate");
					params.setField("TRANS_ORD_DATE"           	,sTRANS_ORD_DATE);
					params.setField("TRANS_ORD_SEQNO"         	,sTRANS_ORD_SEQNO);
					
					if(ydPICommDAO.selectT(params, rsBedRecord, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getDMYDR060_SendToL2List") > 0){
						for(int idx=0; idx<rsBedRecord.size();idx++){
							JDTORecord jtoYDY89L001 = JDTORecordFactory.getInstance().create();
							jtoYDY89L001.setField("YD_INFO_SYNC_CD", "4"); // BED까지(4)
							jtoYDY89L001.setField("YD_GP"          , YdConstant.YD_GP_PLATE2_GDS_YARD);
							jtoYDY89L001.setField("YD_STK_COL_GP"  , rsBedRecord.getRecord(idx).getFieldString("YD_STK_COL_GP"));
							jtoYDY89L001.setField("YD_STK_BED_NO"  , rsBedRecord.getRecord(idx).getFieldString("YD_STK_BED_NO"));
							YdCommonUtils.sndStrPosSpecToL2(jtoYDY89L001);
						}
					}					
			}
			
			//-------------------------------------------------------------------------------------------------------------
			if("Y".equals(sCANCEL_YN)) { //취소일 경우
				
				String sPI0002 = ydPICommDAO.ApplyYn(logId, mthdNm, "PI0002","T","*"); //
				String		szRtnMsg				= null;
				
				if("Y".equals(sPI0002)) {
					commPiUtils.printLog(logId, "M10LMYDJ1032 취소시 신로직 들어감 'PI0002' = 'Y" , "SL");
					YdStkBedDao     ydStkBedDao  = new YdStkBedDao();
					// 작업 예약 CHECK 부분을 -> 크레인 스케쥴 CHECK로
			    	/*
			    	 * 후판출하상차지시 취소시점에 크레인 스케쥴 에 있으면  SKIP 
			    	 */		
					/* com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdCrnWrkYn
					SELECT A.YD_CRN_SCH_ID 
					     , A.YD_SCH_CD
					  FROM TB_YD_CRNSCH    A
					 WHERE A.YD_WBOOK_ID IN (  
					            SELECT B.YD_WBOOK_ID
					              FROM TB_YD_WRKBOOK    A
					                 , TB_YD_WRKBOOKMTL B
					                 , TB_YD_STOCK      C
					             WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
					               AND B.STL_NO = C.STL_NO
					               AND A.DEL_YN = 'N'
					               AND A.DEL_YN = 'N'
					               AND C.TRANS_ORD_DATE   = '20230105'
					               AND C.TRANS_ORD_SEQNO  = '106680'
					               AND SUBSTR(C.YD_SCH_CD,3,2) IN ('PT', 'TR')  --출하 작업
                                   AND SUBSTR(C.YD_SCH_CD,7,1) IN ('L','U')  --출고/차량입고
					               
					      )					
		            */		   
					JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("TRANS_ORD_DATE" , sTRANS_ORD_DATE );
	 				jrParam.setField("TRANS_ORD_SEQNO", sTRANS_ORD_SEQNO);
	 				
	 				JDTORecordSet jsRst = ydPICommDAO.selectT(jrParam, "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdCrnWrkYn", logId, mthdNm, "크레인스케줄 조회");
	 				if (jsRst.size() > 0) {
		    		
	 					sMsg="["+sMethodName+"] 후판출하상차지시 취소시 크레인스케줄이 존재함.["+sTRANS_ORD_DATE+"] - ["+sTRANS_ORD_SEQNO+"]";
						commPiUtils.printLog(logId, sMsg , "SL");
						return;
	 				} 

	 				/* com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWorkBookWrkYn  
	 				SELECT B.YD_WBOOK_ID
	 				  FROM TB_YD_WRKBOOK    A
	 				     , TB_YD_WRKBOOKMTL B
	 				     , TB_YD_STOCK      C
	 				 WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
	 				   AND B.STL_NO = C.STL_NO
	 				   AND A.DEL_YN = 'N'
	 				   AND A.DEL_YN = 'N'
	 				   AND C.TRANS_ORD_DATE   = '20230105'
	 				   AND C.TRANS_ORD_SEQNO  = '106680'   
                       AND SUBSTR(A.YD_SCH_CD,3,2) IN ('PT', 'TR')  --출하 작업
                       AND SUBSTR(A.YD_SCH_CD,7,1) IN ('L','U')  --출고/차량입고	 				   
	 				   AND ROWNUM = 1
	 				*/   
	 				JDTORecordSet jsRst1 = ydPICommDAO.selectT(jrParam, "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWorkBookWrkYn", logId, mthdNm, "크레인작업예약 조회");
	 				if (jsRst1.size() <= 0) {
		    		
	 					sMsg="["+sMethodName+"] 후판출하상차지시 취소시 작업예약 없음.["+sTRANS_ORD_DATE+"] - ["+sTRANS_ORD_SEQNO+"]";
						commPiUtils.printLog(logId, sMsg , "SL");
//						return;  //작업예약 없어도 포인트/차량스케줄,저장품 정보는 클리어되어야함.
	 				} else { 
	 					String szYD_WBOOK_ID   = commPiUtils.trim(jsRst1.getRecord(0).getFieldString("YD_WBOOK_ID"));
	 					
		 				szRtnMsg = YdCommonUtils.delYdWrkbookNMtl(szYD_WBOOK_ID, sModifier);
		 				
						//------------------------------------------------------------------------------------------------
						//	작업예약/재료 삭제
						//------------------------------------------------------------------------------------------------
	
						szRtnMsg = YdCommonUtils.delYdWrkbookNMtl(szYD_WBOOK_ID, sModifier);
	
						if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
							sMsg = "[Jsp Session : "+ sOperationName +"] 작업예약["+szYD_WBOOK_ID+"]삭제 성공";
							commPiUtils.printLog(logId, sMsg , "SL");
						}else{
							sMsg = "[Jsp Session : "+ sOperationName +"] 작업예약["+szYD_WBOOK_ID+"]삭제 실패 - 메세지 : " + szRtnMsg;
							commPiUtils.printLog(logId, sMsg , "SL");
						}		    		
	 				}	
		    		/*
		    		 * 취소처리 : 운송지시,순번,차량번호가 일치하는 것만 취소처리 한다.
		    		 */
					
		    		recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("TRANS_ORD_DATE"	, sTRANS_ORD_DATE);
					recPara.setField("TRANS_ORD_SEQNO"	, sTRANS_ORD_SEQNO);
					recPara.setField("CAR_NO"	, sCAR_NO);
		    		
		    		for(int index = 0; index < intYD_EQP_WRK_SH; index++){
		    			
						//sSTL_NO = ydDaoUtils.paraRecChkNull(rcvMsg,"STL_NO"+(index+1));
		    			sSTL_NO = commPiUtils.trim(rcvMsg.getFieldString("STL_NO" + (index+1) ));
						recPara.setField("STL_NO"	, sSTL_NO);
						
						
						// 2021. 06. 03 트랜잭 이슈로 인하여 분리처리한다.
						// 운송지시 취소 및 재 편성작업시 동일 재료가 동시에 처리되는 문제가 발생하여
						// 트랜잭션 분리조치함
						// 간혹 트랜잭션 이슈가 발생하여 조치함
	//	    			intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0047");
						/* com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0047 
						UPDATE USRYDA.TB_YD_STOCK
						SET YD_AIM_RT_GP     = 'NB'
						  , YD_RULE_PL_RS_GP = ''
						  , TRANS_ORD_DATE   = ''
						  , TRANS_ORD_SEQNO  = ''
						  , YD_CAR_UPP_LOC_CD= ''
						  , MODIFIER         = 'DMYDR060'
						  , MOD_DDTT         = SYSDATE
						  , CAR_NO           = ''
						  , CARD_NO          = ''
						  , CAR_LOTID        = ''
						  , CAR_LOTID_REG_DDTT =  NULL 
						  , YD_STK_BED_NO    = 'TR'  
						WHERE STL_NO    = :V_STL_NO
						AND   TRANS_ORD_DATE = :V_TRANS_ORD_DATE
						AND   TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
						AND   NVL(CAR_NO,'-') LIKE NVL(:V_CAR_NO,'-')||'%'
						*/
						
						//상차취소시 lotid 클리어 기능 제외시킴(단순 배차취소인지, 운송지시 취소인지 야드는 알 수 없으므로)물류진행과 협의. 2023.04.11
						//쿼리 내 lot id 클리어 부분 제외시킴.
						
		    			recPara.setField("QUERY_ID", "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0047");
		    			
						// 2021. 06. 03 트랜잭션 분리를 위한 EJb Bean설정
						EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
						ejbConn.trx("updYdStkColTX", new Class[] { JDTORecord.class }, new Object[] { recPara });	
						
		    			/*
		    		     * 선별LOT편성정보 취소
		    		     *
		    		     * 출하에서  선별Lot편성취소  정보가 수신되면  해당 저장위치 상태변경
		    		     *  야드베드입출고상태 : 
		    		     *  선별상태 : 
		    		     */
						/* com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedStsCd_02 */
		    		    //intRtnVal = ydStkBedDao.updYdStkBedStat_02(recPara);  //출하취소시에도 완산베드 풀리는 현상 제외 2023.03.30 임진후 사원 요청사항
		    		}					
				} else {
			    	/*
			    	 * 후판출하상차지시 취소시점에 작업예약에 있으면  체크 
			    	 */				
		    		JDTORecordSet rsChkPara	= null;
		    		JDTORecord recChkPara	= null;
		    		String sChkStlNo		= "";
		    		
		    		YdWrkbookMtlDao ydWrkbookDao = new YdWrkbookMtlDao();
		    		YdStkBedDao     ydStkBedDao  = new YdStkBedDao();
		    		
		    		for(int index = 0; index < intYD_EQP_WRK_SH; index++){
		    			
		    			rsChkPara  = JDTORecordFactory.getInstance().createRecordSet("");
		    			recChkPara = JDTORecordFactory.getInstance().create();
		    			
		    			sChkStlNo = commPiUtils.trim(rcvMsg.getFieldString("STL_NO" + (index+1) ));
		    			
		    			if("".equals(sChkStlNo)) break;
		    			
		    			recChkPara.setField("STL_NO" , sChkStlNo);
		    			/*com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlSTLNO*/
		//    			intRtnVal = ydWrkbookDao.getYdWrkbookmtl(recChkPara, rsChkPara, 2);
		    			intRtnVal = ydPICommDAO.selectT(recChkPara, rsChkPara, "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlSTLNO2");
		    			if(intRtnVal <= 0) {
						}else{
							sMsg="["+sMethodName+"] 후판출하상차지시 취소시 이미 작업예약에 재료 존재함.["+sChkStlNo+"]";
							commPiUtils.printLog(logId, sMsg , "SL");
							return;
						}    			
		    		}
					
		    		/*
		    		 * 취소처리 : 운송지시,순번,차량번호가 일치하는 것만 취소처리 한다.
		    		 */
		    		recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("TRANS_ORD_DATE"	, sTRANS_ORD_DATE);
					recPara.setField("TRANS_ORD_SEQNO"	, sTRANS_ORD_SEQNO);
					recPara.setField("CAR_NO"	, sCAR_NO);
		    		
		    		for(int index = 0; index < intYD_EQP_WRK_SH; index++){
		    			
						//sSTL_NO = ydDaoUtils.paraRecChkNull(rcvMsg,"STL_NO"+(index+1));
		    			sSTL_NO = commPiUtils.trim(rcvMsg.getFieldString("STL_NO" + (index+1) ));
						recPara.setField("STL_NO"	, sSTL_NO);
						
						
						// 2021. 06. 03 트랜잭 이슈로 인하여 분리처리한다.
						// 운송지시 취소 및 재 편성작업시 동일 재료가 동시에 처리되는 문제가 발생하여
						// 트랜잭션 분리조치함
						// 간혹 트랜잭션 이슈가 발생하여 조치함
	//	    			intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0047");
						/* com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0047 
						UPDATE USRYDA.TB_YD_STOCK
						SET YD_AIM_RT_GP     = 'NB'
						  , YD_RULE_PL_RS_GP = ''
						  , TRANS_ORD_DATE   = ''
						  , TRANS_ORD_SEQNO  = ''
						  , YD_CAR_UPP_LOC_CD= ''
						  , MODIFIER         = 'DMYDR060'
						  , MOD_DDTT         = SYSDATE
						  , CAR_NO           = ''
						  , CARD_NO          = ''
						  , CAR_LOTID        = ''
						  , CAR_LOTID_REG_DDTT =  NULL 
						  , YD_STK_BED_NO    = 'TR'  
						WHERE STL_NO    = :V_STL_NO
						AND   TRANS_ORD_DATE = :V_TRANS_ORD_DATE
						AND   TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
						AND   NVL(CAR_NO,'-') LIKE NVL(:V_CAR_NO,'-')||'%'
						*/
		    			recPara.setField("QUERY_ID", "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0047");
		    			
						// 2021. 06. 03 트랜잭션 분리를 위한 EJb Bean설정
						EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
						ejbConn.trx("updYdStkColTX", new Class[] { JDTORecord.class }, new Object[] { recPara });	
						
		    			/*
		    		     * 선별LOT편성정보 취소
		    		     *
		    		     * 출하에서  선별Lot편성취소  정보가 수신되면  해당 저장위치 상태변경
		    		     *  야드베드입출고상태 : 
		    		     *  선별상태 : 
		    		     */
						/* com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedStsCd_02 */
		    		    intRtnVal = ydStkBedDao.updYdStkBedStat_02(recPara);
		    		}
				}
	    		//=========================================================
				//	차량스케줄 삭제 및 차량 POINT Clear
				//=========================================================
	    		commPiUtils.printLog(logId, mthdNm + "차량스케줄삭제 및 차량Point Clear 시작" , "SL");
//PIDEV_S :병행가동용:PI_YD
	    		rcvMsg.setField("PI_YD",    	"T");		
				String sRtnMsg = YdCommonUtils.delCarSchNCarPointForDist(rcvMsg, sMethodName);
				commPiUtils.printLog(logId, mthdNm + "차량스케줄삭제 및 차량Point Clear 완료 -" , "SL");
				
				
				// 전사물류개선 2021. 4. 3
				JDTORecordSet rsBedRecord = JDTORecordFactory.getInstance().createRecordSet("ydPlate");
				JDTORecord params = JDTORecordFactory.getInstance().create();
				params.setField("TRANS_ORD_DATE"   , sTRANS_ORD_DATE);
				params.setField("TRANS_ORD_SEQNO"  , sTRANS_ORD_SEQNO);
				
				if(ydPICommDAO.selectT(params, rsBedRecord, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getDMYDR060_SendToL2List") > 0){
				
					for(int i=0; i<rsBedRecord.size();i++){
						JDTORecord jtoYDY89L001 = JDTORecordFactory.getInstance().create();
						jtoYDY89L001.setField("YD_INFO_SYNC_CD",  "4"); // BED까지(4)
						jtoYDY89L001.setField("YD_GP"          , YdConstant.YD_GP_PLATE2_GDS_YARD);
						jtoYDY89L001.setField("YD_STK_COL_GP", 	rsBedRecord.getRecord(i).getFieldString("YD_STK_COL_GP"));
						jtoYDY89L001.setField("YD_STK_BED_NO", 	rsBedRecord.getRecord(i).getFieldString("YD_STK_BED_NO"));
						YdCommonUtils.sndStrPosSpecToL2(jtoYDY89L001);
					}
				}
				
				return;
			}
			//-------------------------------------------------------------------------------------------------------------
			
			
			//---------------------------------------------------------------------------------
			//	후판출하운송지시 상차정보가 중복해서 수신될경우 SKIP처리한다. 
			//---------------------------------------------------------------------------------
			JDTORecordSet rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DATE"	, sTRANS_ORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO"	, sTRANS_ORD_SEQNO);
//			recPara.setField("CARD_NO"			, sCARD_NO );
			recPara.setField("CAR_NO"			, sCAR_NO );
			recPara.setField("DEL_YN"			, "N" );
			
			/* com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0087_PIDEV 
			SELECT YD_CAR_SCH_ID 
			  FROM TB_YD_CARSCH
			 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND CAR_NO    = :V_CAR_NO
			   AND DEL_YN    = :V_DEL_YN
			ORDER BY YD_CAR_SCH_ID DESC   
			*/
			intRtnVal = ydPICommDAO.selectT(recPara, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0087_PIDEV");
			if(intRtnVal > 0){
				sMsg= "["+sOperationName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				commPiUtils.printLog(logId, sMsg , "SL");
				return;
			}
				
			//---------------------------------------------------------------------------------
			//	YD저장품에 운송상차지시 정보를 등록(UPDATE) 한다.
			//---------------------------------------------------------------------------------			
			recEditColumn 	= JDTORecordFactory.getInstance().create();
			recEditColumn.setField("YD_RULE_PL_RS_GP", 		sCMBN_CARLD_YN);
			recEditColumn.setField("STL_APPEAR_GP", 		sSTL_APPEAR_GP);
			recEditColumn.setField("TRANS_ORD_DATE", 		sTRANS_ORD_DATE);
			recEditColumn.setField("TRANS_ORD_SEQNO", 		sTRANS_ORD_SEQNO);
			recEditColumn.setField("CAR_NO", 				sCAR_NO);
//			recEditColumn.setField("CARD_NO", 				sCARD_NO);
			
			String sApplyYnPI = "Y";
			if(sApplyYnPI.equals("Y")){
			   //CAR_LOTID 셋팅안함.	
			}
			else{
				if(!"".equals(sCarLotId)) {
					recEditColumn.setField("CAR_LOTID", sCarLotId);
				} //LOT ID 셋팅 부분 제거 (LOTID 셋팅은 1022에서 셋팅하기로 했으므로) - 반품등록시에, 여기서 셋팅되면서 LOTID 셋팅되는 현상 발생. 20230517 박종호
			}
			recEditColumn.setField("MODIFIER", 				sModifier);
			
			sMsg="[" + sOperationName + "]["+intYD_EQP_WRK_SH+"]매수에 대한  저장품 업데이트 시작";
			commPiUtils.printLog(logId, sMsg , "SL");
			
			// 전사물류개선 차량종류
//			String sCarKind = ydDaoUtils.paraRecChkNull(rcvMsg,"CAR_KIND");
			String sYD_CAR_UPP_LOC_CD = ""; // 거래처별 우선순위
			
			for(int i = 1 ; i<= intYD_EQP_WRK_SH; i++){
				
//				sSTL_NO            = ydDaoUtils.paraRecChkNull(rcvMsg,"STL_NO"+i);
//				sYD_CAR_UPP_LOC_CD = ydDaoUtils.paraRecChkNull(rcvMsg,"GDS_CARLD_LOC"+i);
				sSTL_NO            = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"+i));
				sYD_CAR_UPP_LOC_CD = commPiUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i));
				
				if(sSTL_NO.equals("")){
					break;
				}else {
					rcvMsg.setField("STL_NO", 	sSTL_NO);
				}
				recEditColumn.setField("STL_NO",	sSTL_NO);
				
				recEditColumn.setField("YD_AIM_RT_GP", "L6"); 
				recEditColumn.setField("STL_PROG_CD",  "L");  //운송대기
				
				
				// 전사물류개선 2021.1.21
				// 차상위치필드를 거래처별 우선순위로 설정한다.
				// 차량종류는 TB_YD_STOCK의 YD_CONVEYOR_BRANCH_CD에 재료별로 담는다.
				recEditColumn.setField("YD_CAR_UPP_LOC_CD",  sYD_CAR_UPP_LOC_CD); 
				
				if("".equals(sYD_CAR_UPP_LOC_CD)){
					sMsg="[" + sOperationName + "] ["+i+"] 재료["+ sSTL_NO +"]에 대한 거래처우선순위가 존재하지 않습니다.";
					commPiUtils.printLog(logId, sMsg , "SL");
					recEditColumn.setField("YD_CAR_UPP_LOC_CD",  "1"); 
//					throw new Exception(sMsg);
				}
				
				// 2021. 4. 7 항목추가
				recEditColumn.setField("YD_FTMV_MEANS_GP", sYD_FTMV_MEANS_GP);
				recEditColumn.setField("CR_FRTOMOVE_GP", sCR_FRTOMOVE_GP);
				
				/* 
				 * 1. 저장품테이블의 재료들에 재료진도코드와 야드목표행선을 수정처리
				 */
				//저장품갱신******************************************************************************************** 
				sMsg="[" + sOperationName + "] ["+i+"] 재료["+ sSTL_NO +"]에 대한 야드목표행선[L6]과 재료진도코드[L] 업데이트 시작";
				commPiUtils.printLog(logId, sMsg , "SL");
				
				intRtnVal = ydStockDao.updYdStock(recEditColumn, 0);
				
				if(intRtnVal <= 0){
					sMsg="[" + sOperationName + "] ["+i+"] 재료["+ sSTL_NO +"]에 대한 야드목표행선[L6]과 재료진도코드[L] 업데이트 시 오류발생 - 반환값 : " +intRtnVal;
					commPiUtils.printLog(logId, sMsg , "SL");
					return ;
				}
				sMsg="[" + sOperationName + "] ["+i+"] 재료["+ sSTL_NO +"]에 대한 야드목표행선[L6]과 재료진도코드[L] 업데이트 성공";
				commPiUtils.printLog(logId, sMsg , "SL");
				//****************************************************************************************************
				
			}
			
			
			//야드핸들링정보 송신////////////////////////////////////////////////////////////////////////////////
					
			double dblMaxWidth  = 0; //최대 폭	
			double dblCurrWidth = 0; //현재 폭
			double dblMaxThick  = 0; //최대 두께	
			double dblCurrThick = 0; //현재 두께
			int intMtlSh        = 0; //재료매수
			int intCrnWrkableSh	= 0; //크레인작업가능매수
			int intHndlingCnt   = 1; //핸들링 수 
			
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara.setField("TRANS_ORD_DT"		, sTRANS_ORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO"	, sTRANS_ORD_SEQNO);
			
			intRtnVal = ydPICommDAO.selectT(recPara, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0086");	
			
			if( intRtnVal > 0 ){
				
				for( int i = 1; i <= rsResult1.size(); i++ ) {
					
					rsResult1.absolute(i);
					recPara = rsResult1.getRecord();
					
//					sCARLD_PNT_CD 	= ydDaoUtils.paraRecChkNull(recPara,"YD_CARPNT_CD"); 
//					sYD_GP			= sCARLD_PNT_CD.substring(0,1);
//					sYD_STK_LOC		= ydDaoUtils.paraRecChkNull(recPara,"YD_STK_LOC");
//					sJOB_GP			= ydDaoUtils.paraRecChkNull(recPara,"JOB_GP");
//					sCAR_LOTID		= ydDaoUtils.paraRecChkNull(recPara,"CAR_LOTID");

					sCARLD_PNT_CD 	= commPiUtils.trim(recPara.getFieldString("YD_CARPNT_CD" )); 
					sYD_GP			= sCARLD_PNT_CD.substring(0,1);
					sYD_STK_LOC		= commPiUtils.trim(recPara.getFieldString("YD_STK_LOC" ));
					sJOB_GP			= commPiUtils.trim(recPara.getFieldString("JOB_GP" ));
					sCAR_LOTID		= commPiUtils.trim(recPara.getFieldString("CAR_LOTID" ));
					
					if( i == 1 ) {
						sCARLD_PNT_CD_old	= sCARLD_PNT_CD;
						sYD_STK_LOC_old	= sYD_STK_LOC;
						sJOB_GP_old		= sJOB_GP;
					}		
					
					if("A".equals(sJOB_GP) && !"".equals(sCAR_LOTID)) {
						//주작업대상이 아닌 보조작업(이적)대상에 차량lotId가 셋팅되어 있다면 핸들링 Count 에서 배제한다.
						continue;
					}
					
					if(!sYD_STK_LOC_old.equals(sYD_STK_LOC) || !sJOB_GP_old.equals(sJOB_GP)) {
						//위치가 변경되었거나, 주작업/보조작업이 변경되었다면 핸들링수 1 증가
						intHndlingCnt++;
						
		    			//폭,중량,매수 초기화
		    			dblCurrWidth = 0;
		    			dblCurrThick = 0;
		    			dblMaxWidth = 0;
		    			dblMaxThick = 0;
		    			intMtlSh = 0;							
					}
					
					if(!sCARLD_PNT_CD_old.equals(sCARLD_PNT_CD)) {
						//차량 POINT 가 변경 되었을 경우
						sMsg="상차완료 송신 YDDMR050->M10YDLMJ1052 (야드핸들링정보) 송신 시작";
						commPiUtils.printLog(logId, sMsg , "SL");
						
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("MQ_TC_CD"                   , "M10YDLMJ1052");
						recInTemp.setField("MQ_TC_CREATE_DDTT"          , commPiUtils.getCurDate("yyyyMMddHHmmss"));
						recInTemp.setField("YD_GP"           	 		, sYD_GP );
						recInTemp.setField("DIST_GOODS_GP"         		, "P" );
						recInTemp.setField("CAR_NO"           			, sCAR_NO );
						recInTemp.setField("TRANS_ORD_DATE"           	, sTRANS_ORD_DATE);
						recInTemp.setField("TRANS_ORD_SEQNO"         	, sTRANS_ORD_SEQNO);
						recInTemp.setField("CMBN_CARLD_YN"         		, sCMBN_CARLD_YN );
						recInTemp.setField("CARLD_PNT_CD"         		, sCARLD_PNT_CD_old );
						recInTemp.setField("HANDLING_CNT"          		, Integer.toString(intHndlingCnt)); 
						recInTemp.setField("YD_STK_BED_WHIO_STAT"       , "" );
						ydDelegate.sendMsg(recInTemp);
						
						sMsg="상차완료 송신 YDDMR050->M10YDLMJ1052 (야드핸들링정보) 송신 완료";
						commPiUtils.printLog(logId, sMsg , "SL");	
						
						
		    			//폭,중량,매수 초기화
		    			dblCurrWidth = 0;
		    			dblCurrThick = 0;
		    			dblMaxWidth = 0;
		    			dblMaxThick = 0;
		    			intMtlSh = 0;		
		    			
		    			//핸들링수 초기화
		    			intHndlingCnt = 1;
					}					
					
    				//재료의 현재 폭
    				dblCurrWidth = Double.parseDouble(commPiUtils.trim(recPara.getFieldString("YD_MTL_W" ))); 
    				//재료의 현재 두께
    				dblCurrThick = Double.parseDouble(commPiUtils.trim(recPara.getFieldString("YD_MTL_T" ))); 
    				
    				//최대 폭
    				if(dblCurrWidth > dblMaxWidth) dblMaxWidth = dblCurrWidth;
    				//최대 두께
    				if(dblCurrThick > dblMaxThick) dblMaxThick = dblCurrThick;		
    				
    				//현재 재료 매수
    				intMtlSh++;    				
					
    				intCrnWrkableSh = PlateGdsYdUtil.getCrnWrkableShBasedOnWT(dblMaxThick, dblMaxWidth);
    				
    				
    				if (intMtlSh <= intCrnWrkableSh) {
    					//핸들링수 변화없음 
    					
    				} else {
    					//핸들링수 1 증가
    					intHndlingCnt++;
    					
    					//최대폭,두께 에 = 현재 폭,두께 대입
    					dblMaxWidth = dblCurrWidth;
    					dblMaxThick = dblCurrThick;
    					
    					intMtlSh = 1;    					
    				}
    				
					sCARLD_PNT_CD_old = sCARLD_PNT_CD;
					sYD_STK_LOC_old	  = sYD_STK_LOC;
					sJOB_GP_old		  = sJOB_GP;    				
				}
				
				
				// 전사물류개선 2021. 1. 6
				// 길이 폭으로 차종을 구한다.
				// --가변슬라이드 : 폭이 3400 초과 && 길이 14,000 초과
				// --가변차량 : 폭이 3400 초과 && 길이 14,000 이하
				// --일반 : 폭이 3400 이하 && 길이 14,000 이하
				// --일반슬라이드 : 폭이 3400 이하 && 길이 14,000 초과
				JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
				JDTORecordSet rsResult2 	= JDTORecordFactory.getInstance().createRecordSet("");
				String sCarKind_DB = "";
//PIDEV
//				if(sCARD_NO.startsWith("P")){
//					sCarKind_DB = "PT";
//				}
//				else if("P12".equals(sCAR_KIND) 
//							|| "P18".equals(sCAR_KIND) 
//							|| "PX".equals(sCAR_KIND) 
//							|| "PY".equals(sCAR_KIND) 
//							|| "PU".equals(sCAR_KIND) ){
//					sCarKind_DB = "PT";
//				}
//				else{
//					recInTemp1.setField("TRANS_ORD_DATE"           	,sTRANS_ORD_DATE);
//					recInTemp1.setField("TRANS_ORD_SEQNO"         	,sTRANS_ORD_SEQNO);
//					recInTemp1.setField("CAR_NO"           			,sCAR_NO );
//					if(commDao.select(recInTemp1, rsResult2, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCarKindByWdithLength")>0){
//						sCarKind_DB =  rsResult2.getRecord(0).getFieldString("CAR_KIND");
//					}
//					else{
//						sCarKind_DB = "TR";
//					}
//				}
				if(sCAR_KIND.startsWith("P")){
					sCarKind_DB = "PT";
				} else if (sCAR_KIND.startsWith("T")){
					sCarKind_DB = "TR";
				} else if (sCAR_KIND.startsWith("C")){
					sCarKind_DB = "TR";
				} else{
					recInTemp1.setField("TRANS_ORD_DATE"    , sTRANS_ORD_DATE);
					recInTemp1.setField("TRANS_ORD_SEQNO"   , sTRANS_ORD_SEQNO);
					recInTemp1.setField("CAR_NO"           	, sCAR_NO );
					if(ydPICommDAO.selectT(recInTemp1, rsResult2, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCarKindByWdithLength")>0){
						sCarKind_DB =  rsResult2.getRecord(0).getFieldString("CAR_KIND");
					}
					else{
						sCarKind_DB = "TR";
					}
				}
				// YD_STOCK 차량TYPE정보 Set				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CONVEYOR_BRANCH_CD" , sCarKind_DB); 
				recInTemp.setField("MODIFIER"              , sModifier );	//수정자
				recInTemp.setField("TRANS_ORD_DATE"        , sTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO"       , sTRANS_ORD_SEQNO);
				recInTemp.setField("CAR_NO"           	   , sCAR_NO );
				ydPICommDAO.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updYdCarTransType2");
				
				
				//for 문 밖에서 마지막 차량POINT의 핸들링 수를 전송한다.
				sMsg="상차완료 송신 YDDMR050-> (M10YDLMJ1052) (야드핸들링정보) 송신 시작";
				commPiUtils.printLog(logId, sMsg , "SL");	
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MQ_TC_CD"                   , "M10YDLMJ1052");
				recInTemp.setField("MQ_TC_CREATE_DDTT"          , commPiUtils.getCurDate("yyyyMMddHHmmss"));
				recInTemp.setField("YD_GP"           	 		, sYD_GP );
				recInTemp.setField("DIST_GOODS_GP"         		, "P" );
				recInTemp.setField("CAR_NO"           			, sCAR_NO );
				recInTemp.setField("TRANS_ORD_DATE"           	, sTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO"         	, sTRANS_ORD_SEQNO);
				recInTemp.setField("CMBN_CARLD_YN"         		, sCMBN_CARLD_YN );
				recInTemp.setField("CARLD_PNT_CD"         		, sCARLD_PNT_CD_old );
				recInTemp.setField("HANDLING_CNT"          		, Integer.toString(intHndlingCnt)); 
				recInTemp.setField("YD_STK_BED_WHIO_STAT"       , "" );
				ydDelegate.sendMsg(recInTemp);
				
				
				sMsg="상차완료 송신 YDDMR050-> (야드핸들링정보) 송신 완료";
				commPiUtils.printLog(logId, sMsg , "SL");						
				
			}
			/////////////////////////////////////////////////////////////////////////////////////////////////
			
//			return jrRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}

	
	/**	 
	 * [A] 오퍼레이션명 :후판대기장도착실적 송신(DMYDR061)- procStandByYdArrivePlate4G
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1042(JDTORecord rcvMsg) throws DAOException { //S 
		String mthdNm = "[YdPlateL3RcvPISeEJB.rcvM10LMYDJ1042] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "T", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}					
			
			String sOperationName		= "후판용 대기장도착실적(M10LMYDJ1042)";
			
			//this.procM10LMYDJ1042(rcvMsg);
			String msgId     = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sWorkGp	 = commPiUtils.trim(rcvMsg.getFieldString("WORK_GP"));   //야드구분
			String sModifier = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));  
			String sCarKind  = commPiUtils.trim(rcvMsg.getFieldString("CAR_KIND"));  
			
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			
			rcvMsg.setField("MODIFIER", sModifier);
			
			if((!"9".equals(sWorkGp))||(sCarKind.startsWith("P"))) {
				// 후판대기장도착
				this.procM10LMYDJ1042(rcvMsg);	
			} else {
				// 후판TR이송상차대기장도착PDA
				this.procM10LMYDJ1042_DMYDR070(rcvMsg);
			}			
			
			commPiUtils.printLog(logId, mthdNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}

	
	/**	 
	 * [A] 오퍼레이션명 :후판제품 포인트 도착
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1052(JDTORecord rcvMsg) throws DAOException { //S 
		String mthdNm = "[YdPlateL3RcvPISeEJB.rcvM10LMYDJ1052] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "T", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}					
			
			String sMethodName 		    = "rcvM10LMYDJ1052";
			String sOperationName		= "후판용 포인트도착실적(M10LMYDJ1052)";
			String msgId    = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			//기본 변수 정의
			String sMsg 				= "";
			int intRtnVal 				= 0;
			
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
            String ydGp            = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"         ));   // 차량번호
            String sTrnRegDate     = commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE"  ));   // 운송지시일자
			String sTrnRegSeq      = commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ"   ));   // 운송지시순번
			String sCarNo          = commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"        ));   // CAR_NO
			String sCarldPntCd     = commPiUtils.trim(rcvMsg.getFieldString("CARLD_PNT_CD"  ));   // 상차 포인트
			
            String sModifier       = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"      ));   //수정자(Backup Only)
            
            if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }			
 
			
			//전문항목 에러체크
			if("".equals(ydGp)){
				sMsg="[" + sOperationName + "] YD_GP IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");
			}
			if("".equals(sTrnRegDate)){
				sMsg="[" + sOperationName + "] TRANS_ORD_DT IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");
				return ;
			}
			if("".equals(sTrnRegSeq)){
				sMsg="[" + sOperationName + "] TRANS_ORD_SEQNO IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");
				return ;
			}
			if("".equals(sCarNo)){
				sMsg="[" + sOperationName + "] CAR_NO IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");
				return ;
			}
			if("".equals(sCarldPntCd)){
				sMsg="[" + sOperationName + "] CARLD_PNT_CD IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");
				return ;
			}
			//-------------------------------------------------------------------------------------------------------------

			JDTORecordSet jsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord jrParam1 		= JDTORecordFactory.getInstance().create();
			jrParam1.setField("YD_CARPNT_CD" , sCarldPntCd);
			/* com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0102 
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
			FROM   TB_YD_CARPOINT
			WHERE  YD_CARPNT_CD = :V_YD_CARPNT_CD
			*/
			intRtnVal = ydPICommDAO.selectT(jrParam1, jsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0102");
			
			if(intRtnVal <= 0){
				sMsg="["+sOperationName+"] 해당포인트 관리 안함 - 반환값 : " + intRtnVal;
				commPiUtils.printLog(logId, sMsg , "SL");					
				return ;
			}
			
			jsResult1.first();
			JDTORecord jrResult1 = jsResult1.getRecord();
			
			String sCarNo_DB   = commPiUtils.trim(jrResult1.getFieldString("CAR_NO"));
			String ydStkColGp  = commPiUtils.trim(jrResult1.getFieldString("YD_STK_COL_GP"));
			
			String ydBayGp    =  commPiUtils.trim(jrResult1.getFieldString("YD_BAY_GP"));
			if(!sCarNo.equals(sCarNo_DB)) {
				sMsg="["+sOperationName+"] 수신포인트 차량번호"+ sCarNo_DB + "와 수신차량포인트가 틀림"+ sCarNo + " - 반환값 : " + intRtnVal;
				commPiUtils.printLog(logId, sMsg , "SL");					
				return ;
			}
			
			commPiUtils.printLog(logId, "해당운송지시로 작업예약 검색" , "SL");

			JDTORecordSet jsResult = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord jrParam  = JDTORecordFactory.getInstance().create();
			jrParam.setField("TRANS_ORD_DATE" , sTrnRegDate   );
			jrParam.setField("TRANS_ORD_SEQNO", sTrnRegSeq    );
			jrParam.setField("CAR_NO"         , sCarNo        );
			jrParam.setField("YD_CARPNT_CD"   , sCarldPntCd);
			/* com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryIdWrkBook_PIDEV 
			SELECT A.YD_SCH_CD
			     , A.YD_WBOOK_ID 
			     , NVL((SELECT YD_FRM_YN FROM TB_YD_CARPOINT WHERE YD_CARPNT_CD = :V_YD_CARPNT_CD),'N') AS FM_FLAG
			  FROM TB_YD_WRKBOOK A
			     , TB_YD_WRKBOOKMTL B
			 WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID 
			   AND A.DEL_YN = 'N'
			   AND B.DEL_YN = 'N'
			   AND B.STL_NO IN (
			                   SELECT STL_NO 
			                     FROM TB_YD_CARSCH A
			                        , TB_YD_STOCK  B
			                    WHERE 1=1
			                      AND A.DEL_YN = 'N'
			                      AND B.DEL_YN = 'N'
			                      AND A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
			                      AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
			                      AND A.CAR_NO          = B.CAR_NO 
			                      AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
			                      AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			                      AND A.CAR_NO          = :V_CAR_NO 
			                   )  
			   AND ROWNUM = 1  
			*/   
			intRtnVal = ydPICommDAO.selectT(jrParam, jsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryIdWrkBook_PIDEV");
			
			if(intRtnVal > 0) {
				jsResult.first();
				JDTORecord jrResult = jsResult.getRecord();
				
				String ydSchCd   	   = commPiUtils.trim(jrResult.getFieldString("YD_SCH_CD"       ));
				String ydWbookId 	   = commPiUtils.trim(jrResult.getFieldString("YD_WBOOK_ID"     ));
				String sFM_FLAG 	   = commPiUtils.trim(jrResult.getFieldString("FM_FLAG"         )); // 형상여부
				
				
				if ("N".equals(sFM_FLAG)) {
					commPiUtils.printLog(logId, "크레인스케줄Main[YDYDJ506] 호출 시작" , "SL");
					JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
					//전문코드
					recInTemp.setField("JMS_TC_CD"  , "YDYDJ506"); //스케줄코드
		    		recInTemp.setField("YD_SCH_CD"  , ydSchCd   ); //설비ID
		    		recInTemp.setField("YD_EQP_ID"  , ""        ); //작업예약ID
		    		recInTemp.setField("YD_WBOOK_ID", ydWbookId );
		    				    		
		    		//전문호출 조건 판단 
		    		// 해당 스케줄이 이미 존재한다면 작업예약만 만들고 스케줄Main 호출은 하지 않는다.
		    		if("TR".equals(ydSchCd.substring(2,4))||"PT".equals(ydSchCd.substring(2,4))) {
		    		
			    		//포인트도착 처리시, 해당 시간 차량 스케줄 내 라벨링 2024.04.22 TB_YD_CARSCH 내 YD_CARLD_PNT_WO_DT 항목에 UPDATE
			    		//작업예약 내 업데이트 구조로 변경. 복수동 처리시 앞선 포인트 도착만 처리되도, 뒤이어 작업할 포인트까지 도착처리로 라벨링되므로.
		    			//작업예약 내 DEST_TEL_NO 항목에 도착시간 추가. 현재 미사용 항목
			    		//신규쿼리 개발 필요.
		    			
		    			//String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI9", "T", "*");
		    			//if(sApplyYnPI.equals("Y")){
							sMsg = "["+sOperationName+"] 해당 출하 작업예약:"+ydWbookId+" 에 대한 포인트 도착 라벨링 시작";
							commPiUtils.printLog(logId, sMsg , "SL");
							
				    		ydPICommDAO.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updYdWrkBookArrivedTime");

							sMsg = "["+sOperationName+"] 해당 출하 작업예약:"+ydWbookId+" 에 대한 포인트 도착 라벨링 종료";
							commPiUtils.printLog(logId, sMsg , "SL");		    				
		    			//}
		    			
		    			JDTORecordSet rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				
						intRtnVal = ydPICommDAO.selectT(recInTemp, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0110");	
						
						if(intRtnVal > 0) {
							//스케줄이 존재한다면 스케줄 Main을 호출하지 않는다.
							
							sMsg = "["+sOperationName+"] 해당야드["+ydGp+"]의 크레인스케줄 ["+ydSchCd+"] 이 존재하여 크레인스케줄Main 호출 안함";
							commPiUtils.printLog(logId, sMsg , "SL");
							
						} else {
				    		//전문 송신
							ydDelegate.sendMsg(recInTemp);
							
							sMsg = "["+sOperationName+"] 해당야드["+ydGp+"]의 크레인스케줄Main[YDYDJ506] 호출 완료";
							commPiUtils.printLog(logId, sMsg , "SL");				
						}
		    			
		    		} else {
		    		
			    		//전문 송신
						ydDelegate.sendMsg(recInTemp);
						
						sMsg = "["+sOperationName+"] 해당야드["+ydGp+"]의 크레인스케줄Main[YDYDJ506] 호출 완료";
						commPiUtils.printLog(logId, sMsg , "SL");
		    		}
				}	
			}
			
			//25.01.15 길선배 주임 요청 출하차량 스케줄 올라갈시 색상 변경이 아닌, 도착 기준으로 변경 START(도착처리여부 확인용)
			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APP060", "T", "009");//후판 개발 적용여부
			
			//25.04.17 임진후기사 요청. 출하차량 도착기준으로 변경은 유인크레인에 한해서만 변경. (자동화는 형상스캔 이후 변경)
			if("Y".equals(sApplyYnPI) && !"B".equals(ydBayGp)){
				
				JDTORecord      recStkBedPara       = null;
				recStkBedPara 	= JDTORecordFactory.getInstance().create();
				recStkBedPara.setField("MSG_ID", "YDY8L001");
				recStkBedPara.setField("YD_INFO_SYNC_CD",  "3");
				
				
				recStkBedPara.setField("YD_GP", 			ydStkColGp.substring(0,1)); //- 2012.12.21 수정 (3기)
				recStkBedPara.setField("YD_STK_COL_GP", 	ydStkColGp.substring(0,6));
				recStkBedPara.setField("YD_STK_BED_NO", 	"01");
				
				recStkBedPara.setField("YD_CAR_PROG_STAT", 	"2");  //야드차량착발상태 "A": 도착, "S": 출발
				recStkBedPara.setField("YD_EQP_WRK_STAT", 	"U");  
				
				
				recStkBedPara.setField("CAR_POINT_ARRIVED_FLAG", 	"Y");  //포인트 도착 후 스케줄 기동 대상인지 표기 구분 추가 
				
				sMsg = "[Jsp-Session "+szSessionName+" ] L2 로 적치열 수정된 정보 송신 시작" ;
				commPiUtils.printLog(logId, sMsg , "SL");
	
	
				recStkBedPara.setField("LOG_ID", logId);
				
				ydDelegate.sendMsg(recStkBedPara);
				
				sMsg = "[Jsp-Session "+szSessionName+" ] L2 로 적치열 수정된 정보 송신 완료" ;
				commPiUtils.printLog(logId, sMsg , "SL");
			}
			
			
			//25.01.15 길선배 주임 요청 출하차량 스케줄 올라갈시 색상 변경이 아닌, 도착 기준으로 변경 END (도착처리여부 확인용)
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}
	
	/**	 
	 * [A] 오퍼레이션명 :후판tr이송대기장도착실적 송신(DMYDR061)- procStandByYdArrivePlate4G
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */	
	public void procM10LMYDJ1042_DMYDR070(JDTORecord rcvMsg) throws DAOException { //S 
		String mthdNm = "[YdPlateL3RcvPISeEJB.procM10LMYDJ1042_DMYDR070] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "T", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}					
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			String sOperationName		= "후판용 대기장도착실적(M10LMYDJ1042_DMYDR070)";
			
			YdCarSchDao ydCarSchDao 	= new YdCarSchDao();	//차량스케줄DAO
			
			String sMsg 				= "";
		
			int intRtnVal 				= 0;
			
			int nTRANS_ORD_SEQNO = 0; // 전사물류개선프로젝트 2021.1.6 추가

			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
			String msgId             = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sYD_GP		     = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"             ));   // 야드구분
			String sCMBN_CARLD_YN    = commPiUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN"     ));   // 조합상차유무조합상차(시작:S , 종료: E ,  단일상차: N )
			String sWORK_GP          = commPiUtils.trim(rcvMsg.getFieldString("WORK_GP"           ));   // 작업구분
            String sTEL_NO           = commPiUtils.trim(rcvMsg.getFieldString("TEL_NO"      	  ));   // 전화번호
			String sDRIVER_NAME      = commPiUtils.trim(rcvMsg.getFieldString("DRIVER_NAME"       ));   // 운전기사명
            String sTRANS_ORD_DATE   = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"      ));   // 운송지시일자 
            String sTRANS_ORD_SEQNO  = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"   ));   // 운송지시순번 
            String sCAR_KIND         = commPiUtils.trim(rcvMsg.getFieldString("CAR_KIND"          ));   // 차량종류
            String sCAR_NO           = commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"            ));   // 차량번호
            String sSHIP_NAME        = commPiUtils.trim(rcvMsg.getFieldString("SHIP_NAME"         ));   // 선박명
            String sRENTPROC_CD      = commPiUtils.trim(rcvMsg.getFieldString("CRN_WRK_METHOD_GP" ));   // 크레인작업방법구분해송 줄거리작업 방법 M:마그네틱, T:줄거리
            String sWAIT_ARR_DDTT    = commPiUtils.trim(rcvMsg.getFieldString("WAIT_ARR_DDTT"     ));   // 대기장도착시간
            String sWAIT_ARR_GP      = commPiUtils.trim(rcvMsg.getFieldString("WAIT_ARR_GP"       ));   // 대기장도착구분
            String sYD_CARPNT_CD	 = commPiUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD"      ));
            int intYD_EQP_WRK_SH     = Integer.parseInt(commPiUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수
            String sYD_FTMV_MEANS_GP = commPiUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP" ));   // 이송구분
            String sCR_FRTOMOVE_GP   = commPiUtils.trim(rcvMsg.getFieldString("TRANS_METHOD_GP"   ));   // 냉연이송구분 
            
            String sModifier         = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));             //수정자(Backup Only)			 
            if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			mthdNm = msgId.substring(0, 2) + mthdNm;	
			
			String sBefore_YD_CARLD_STOP_LOC = "";
            

			// 차량종류가 세분화되어 
//			TV	가변기
//			TS	슬라이더
//			VS	슬라이더+가변기
//			TR	일반
//			PT	Pallet
//PIDEV	if("TV".equals(sCAR_KIND) || "TS".equals(sCAR_KIND) || "VS".equals(sCAR_KIND)){
//				sCAR_KIND = "TR";
//			}
			
			sCAR_KIND = "TR";
			
			//전문항목 에러체크
			if("".equals(sCAR_NO)){
				sMsg="[" + sOperationName + "] CAR_NO IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");	
				return ;
			}

			if("".equals(sTRANS_ORD_DATE)){
				sMsg="[" + sOperationName + "] TRANS_ORD_DT IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");	
				return ;
			}
			if("".equals(sTRANS_ORD_SEQNO)){
				sMsg="[" + sOperationName + "] TRANS_ORD_SEQNO IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");	
				return ;
			}
			
			//---------------------------------------------------------------------------------
			//	DEL_YN = 'N' 인 차량스케줄이 존재할경우 SKIP처리한다. 
			//---------------------------------------------------------------------------------
			JDTORecordSet rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DATE"	, sTRANS_ORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO"	, sTRANS_ORD_SEQNO);
			//recPara.setField("CARD_NO"			, sCARD_NO );
			recPara.setField("CAR_NO"			, sCAR_NO );
			recPara.setField("DEL_YN"			, "N" );
			
			intRtnVal = ydPICommDAO.selectT(recPara, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0087_PIDEV");
			if(intRtnVal > 0){
				sMsg= "["+sOperationName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				commPiUtils.printLog(logId, sMsg , "SL");	
				return ;
			}	
			
			JDTORecord jrEditColumn 	= JDTORecordFactory.getInstance().create();
			jrEditColumn.setField("YD_RULE_PL_RS_GP" , sCMBN_CARLD_YN);
			jrEditColumn.setField("TRANS_ORD_DATE"   , sTRANS_ORD_DATE);
			jrEditColumn.setField("TRANS_ORD_SEQNO"  , sTRANS_ORD_SEQNO);
			jrEditColumn.setField("CAR_NO"           , sCAR_NO);
			jrEditColumn.setField("MODIFIER"         , sModifier);
			jrEditColumn.setField("YD_STK_LOT_CD"    , sTEL_NO);
//			if(!"".equals(sCarLotId)) {
//				recEditColumn.setField("CAR_LOTID", 			sCarLotId);
//			}
			
			sMsg="[" + mthdNm + "]["+intYD_EQP_WRK_SH+"]매수에 대한  저장품 업데이트 시작";
			commPiUtils.printLog(logId, sMsg , "SL");
			
			String sSTL_NO = ""; // 거래처별 우선순위
			String sYD_CAR_UPP_LOC_CD = ""; // 거래처별 우선순위
			
			for(int i = 1 ; i<= intYD_EQP_WRK_SH; i++){
				
				sSTL_NO = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"+i));
				
				if(sSTL_NO.equals("")){
					break;
				}
				
				jrEditColumn.setField("STL_NO",	sSTL_NO);
				jrEditColumn.setField("YD_AIM_RT_GP", "L6"); 
				jrEditColumn.setField("STL_PROG_CD",  "L");  //운송대기
				
				
				// 전사물류개선 2021.1.21
				// 차상위치필드를 거래처별 우선순위로 설정한다.
				// 차량종류는 TB_YD_STOCK의 YD_CONVEYOR_BRANCH_CD에 재료별로 담는다.
				if (i < 10) {
					sYD_CAR_UPP_LOC_CD = "0" + i;	
				} else {
					sYD_CAR_UPP_LOC_CD = ""  + i;
				}
				jrEditColumn.setField("YD_CAR_UPP_LOC_CD",  sYD_CAR_UPP_LOC_CD); 
				
				if("".equals(sYD_CAR_UPP_LOC_CD)){
					sMsg="[" + mthdNm + "] ["+i+"] 재료["+ sSTL_NO +"]에 대한 거래처우선순위가 존재하지 않습니다.";
					commPiUtils.printLog(logId, sMsg , "SL");
					jrEditColumn.setField("YD_CAR_UPP_LOC_CD",  "1"); 
//					throw new Exception(sMsg);
				}
				
				jrEditColumn.setField("YD_FTMV_MEANS_GP" , sYD_FTMV_MEANS_GP);
				jrEditColumn.setField("CR_FRTOMOVE_GP"   , sCR_FRTOMOVE_GP);
				jrEditColumn.setField("SHIP_NAME"        , sSHIP_NAME);
				
				/* 
				 * 1. 저장품테이블의 재료들에 재료진도코드와 야드목표행선을 수정처리
				 */
				//저장품갱신******************************************************************************************** 
				sMsg="[" + mthdNm + "] ["+i+"] 재료["+ sSTL_NO +"]에 대한 야드목표행선[L6]과 재료진도코드[L] 업데이트 시작";
				commPiUtils.printLog(logId, sMsg , "SL");
				
//				intRtnVal = ydStockDao.updYdStock(jrEditColumn, 0);
				/* com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updStockM70_PIDEV
				UPDATE TB_YD_STOCK
				   SET MODIFIER          = SYSDATE
				     , YD_RULE_PL_RS_GP  = :V_YD_RULE_PL_RS_GP
				     , TRANS_ORD_DATE    = :V_TRANS_ORD_DATE
				     , TRANS_ORD_SEQNO   = :V_TRANS_ORD_SEQNO
				     , CAR_NO            = :V_CAR_NO
				     , YD_STK_LOT_CD     = :V_YD_STK_LOT_CD  --TEL_NO
				     , YD_AIM_RT_GP      = :V_YD_AIM_RT_GP
				     , STL_PROG_CD       = :V_STL_PROG_CD
				     , YD_CAR_UPP_LOC_CD = :V_YD_CAR_UPP_LOC_CD
				     , YD_FTMV_MEANS_GP  = :V_YD_FTMV_MEANS_GP
				     , CR_FRTOMOVE_GP    = :V_CR_FRTOMOVE_GP
				     , SHIP_NAME         = :V_SHIP_NAME
				 WHERE STL_NO = :V_STL_NO
				*/ 
				intRtnVal = ydPICommDAO.update(jrEditColumn, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updStockM70_PIDEV");
				
				if(intRtnVal <= 0){
					sMsg="[" + mthdNm + "] ["+i+"] 재료["+ sSTL_NO +"]에 대한 야드목표행선[L6]과 재료진도코드[L] 업데이트 시 오류발생 - 반환값 : " +intRtnVal;
					commPiUtils.printLog(logId, sMsg , "SL");
					return ;
				}
				sMsg="[" + mthdNm + "] ["+i+"] 재료["+ sSTL_NO +"]에 대한 야드목표행선[L6]과 재료진도코드[L] 업데이트 성공";
				commPiUtils.printLog(logId, sMsg , "SL");
				//****************************************************************************************************
			}				
			
			/*
			 *  복수동 출하시 나머지 출하작업에 대한 상태 = 'Y'
			 *  -- 권하처리시 선 복수동 작업일때 해당항목을 셋팅
			 */
//			sDOUBLEDONG_CHECK  		    = ydDaoUtils.paraRecChkNull(inRecord,"DOUBLEDONG_CHECK");
//			sDOUBLEDONG_YD_CAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(inRecord,"YD_CAR_SCH_ID"); 		
			String sDOUBLEDONG_CHECK  		 = commPiUtils.trim(rcvMsg.getFieldString("DOUBLEDONG_CHECK"   ));
			String sDOUBLEDONG_YD_CAR_SCH_ID = commPiUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"   ));    					
			

			//--------------------------------------------------------------------------------------
			// 이하 AS-IS 루틴 그대로 사용
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			String sYD_STK_COL_GP = "";
			String sWLOC_CD		  = "";	
			String sYD_PNT_CD	  = "";
			String sYD_CAR_SCH_ID = "";
			
//PIDEV		
			JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord   recInTemp = JDTORecordFactory.getInstance().create();

			if(nTRANS_ORD_SEQNO>999000){
//PIDEV 반드시 확인 해야 함				
				sMsg="["+sOperationName+"] 차량입고(반품)은 차량정지위치를 지정되어 있기때문에(반품등록화면) 사용가능한 차량정지위치를 로직은 Pass한다.";
				commPiUtils.printLog(logId, sMsg , "SL");	
			}else{
				// 일반차량 출하
				/*
				 * 2. 저장품이 적치된 저장위치 정보를 조회 
				 */
				sMsg="[" + sOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 시작";
				commPiUtils.printLog(logId, sMsg , "SL");	
				//저장품 동 구하기 
				JDTORecordSet rsGetStock 	= JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP"   , "");
				//recInTemp.setField("CARD_NO", 		  sCARD_NO);
				recInTemp.setField("CAR_NO"          , sCAR_NO);
				recInTemp.setField("TRANS_ORD_DATE"  , sTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO" , sTRANS_ORD_SEQNO);
				recInTemp.setField("YD_GP"           , sYD_GP);
				
				//-----------------------------------------------------------------------------------------------------------------
				// [전사물류시스템개선]
				//  - 복수동일경우 상차우선순위에 의하여 쿼리를 분리처리한다.
				//
				//-----------------------------------------------------------------------------------------------------------------
				/* com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0089_PIDEV 
				SELECT 
				     SUBSTR(A.YD_STK_COL_GP,1,1)||ITEM||SPAN AS YD_STK_COL_GP,
				     NVL(CNT,0) AS CNT, 
				     ITEM , GBN
				FROM
				    (
				        SELECT 
				          A.ITEM,A.GBN,MIN(ITEM2) AS SPAN, MAX(YD_STK_COL_GP) AS YD_STK_COL_GP
				        FROM
				        (
				            SELECT 
				                REPR_CD_GP,CD_GP,ITEM,ITEM1,SUBSTR(YD_STK_COL_GP,3) AS ITEM2, YD_STK_COL_GP,
				                CASE WHEN SUBSTR(YD_STK_COL_GP,3,2) IN ('01','02','03') THEN 
				                -- 2후판인경우
				                    CASE WHEN ITEM1 >= SUBSTR(YD_STK_COL_GP,3) THEN '1' ELSE '2' END
				                ELSE
				                -- 1후판인경우
				                    CASE WHEN SUBSTR(B.YD_STK_COL_GP,2,1) IN ('E','F') THEN
				                         -- E,F동인경우
				                             CASE WHEN SUBSTR(YD_STK_COL_GP,3) <= ITEM_VALUE1 THEN '3' 
				                                  WHEN SUBSTR(YD_STK_COL_GP,3) <= ITEM2       THEN '5' 
				                                  ELSE '4' END
				                         ELSE
				                         -- E,F동이 아닌경우
				                            CASE WHEN ITEM2 >= SUBSTR(YD_STK_COL_GP,3) THEN '3' ELSE '4' END
				                    END
				                END AS GBN
				            FROM TB_YD_RULE A,
				                (
				                    SELECT DISTINCT
				                        B.YD_STK_COL_GP
				                    FROM TB_YD_STOCK A,
				                         TB_YD_STKLYR B
				                    WHERE A.STL_NO  = B.STL_NO
				                      AND A.CAR_NO         = :V_CAR_NO
				                      AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
				                      AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				                      AND B.YD_STK_LYR_MTL_STAT IN ('C','U')
				                      AND B.YD_STK_COL_GP LIKE NVL(:V_YD_GP,'_') ||'_0%'  -- 야드에 있는 대상재
				                )B
				            WHERE A.REPR_CD_GP  = SUBSTR(B.YD_STK_COL_GP,1,1) || '00031'
				              AND A.ITEM        = SUBSTR(B.YD_STK_COL_GP,2,1)
				        )A
				        GROUP BY  A.ITEM,A.GBN
				    )A,
				    (
				        SELECT YD_CARLD_STOP_LOC,SUBSTR(YD_CARLD_STOP_LOC,2,1) AS DONG ,SUBSTR(YD_CARLD_STOP_LOC,5,1) AS TONGRO ,COUNT(*) AS CNT
				        FROM
				        (
				            SELECT YD_CARLD_STOP_LOC FROM USRYDA.TB_YD_CARSCH  
				            WHERE DEL_YN = 'N'
				              AND SPOS_WLOC_CD = 'DWY26'
				        )A
				        GROUP BY A.YD_CARLD_STOP_LOC
				    )B
				WHERE A.ITEM = B.DONG(+)
				  AND A.GBN  = B.TONGRO(+)
				ORDER BY CNT  , 
				         YD_STK_COL_GP
				*/         
				String sDongSelectQuery = "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0089_PIDEV";
				if(PlateGdsYdUtil.isApplyYn("복수상차우선순위적용여부")){	
					
					JDTORecordSet rsMulti = JDTORecordFactory.getInstance().createRecordSet("");
					JDTORecord recMulti = JDTORecordFactory.getInstance().create();
					recMulti.setField("CAR_NO"          , sCAR_NO);
					recMulti.setField("TRANS_ORD_DATE"  , sTRANS_ORD_DATE);
					recMulti.setField("TRANS_ORD_SEQNO" , sTRANS_ORD_SEQNO);
					
					// 조합상차이거나, 운송지시가 C로시작하는것이라면, 거래처가 2곳 이상이라면
					/* com.inisteel.cim.yd.common.dao.YdPlateCommDAO.isCMBN_CARLD_YN_PIDEV 
					WITH V_DATA AS (
					    SELECT
					        :V_TRANS_ORD_DATE AS V_TRANS_ORD_DATE
					        , :V_TRANS_ORD_SEQNO AS V_TRANS_ORD_SEQNO
					        , :V_CAR_NO AS V_CAR_NO
					    FROM DUAL 
					)  
					/* 거래처 우선순위가 등록된 것 중 2개 이상인것 
					SELECT
					    TRANS_ORD_DATE
					    ,TRANS_ORD_SEQNO
					    ,CAR_NO
					FROM
					(
					    SELECT
					          A.TRANS_ORD_DATE
					        , A.TRANS_ORD_SEQNO
					        , A.CAR_NO
					        , COUNT(DISTINCT A.YD_CAR_UPP_LOC_CD) AS UPP_LOC
					    FROM TB_YD_STOCK A, V_DATA B
					    WHERE  A.TRANS_ORD_DATE = B.V_TRANS_ORD_DATE
					    AND A.TRANS_ORD_SEQNO = B.V_TRANS_ORD_SEQNO
					    AND A.CAR_NO = B.V_CAR_NO 
					    GROUP BY 
					          A.TRANS_ORD_DATE
					        , A.TRANS_ORD_SEQNO
					        , A.CAR_NO
					) 
					WHERE UPP_LOC > 1
					UNION ALL
					/* 상차지가 2개 이상인 것
					SELECT
					      NULL
					    , NULL
					    , NULL 
					FROM
					(
					        SELECT COUNT(DISTINCT Y.GATE) AS PT_LOAD_LOC_CNT 
					        FROM   (      
					                SELECT 
					                       CASE WHEN T.SPAN||T.RYUL <= (CASE WHEN T.SPAN IN ('01','02','03')THEN ITEM1 ELSE ITEM2 END) 
					                            THEN T.YARD||T.DONG||'PT'||( CASE WHEN T.SPAN IN ('01','02','03') THEN '1' ELSE '3' END )||'1' 
					                            ELSE T.YARD||T.DONG||'PT'||( CASE WHEN T.SPAN IN ('01','02','03') THEN '2' ELSE '4' END )||'1' 
					                       END AS GATE1
					                       
					                      ,CASE WHEN T.SPAN IN ('01','02','03') THEN
					                            -- 2후판일 경우
					                                CASE WHEN T.SPAN||T.RYUL <= ITEM1
					                                     THEN T.YARD||T.DONG||'PT'||'11'  
					                                     ELSE T.YARD||T.DONG||'PT'||'21'  
					                                END
					                            ELSE
					                            -- 1후판일 경우
					                                CASE WHEN T.DONG IN ('E','F') THEN
					                                     -- E,F동일 경우
					                                        CASE WHEN T.SPAN||T.RYUL <= ITEM_VALUE1
					                                             THEN T.YARD||T.DONG||'PT'||'31'  
					                                             WHEN T.SPAN||T.RYUL <= ITEM2
					                                             THEN T.YARD||T.DONG||'PT'||'51'  
					                                             ELSE T.YARD||T.DONG||'PT'||'41'  
					                                        END
					                                     ELSE
					                                     -- E,F동이 아닐 경우
					                                        CASE WHEN T.SPAN||T.RYUL <= ITEM2
					                                             THEN T.YARD||T.DONG||'PT'||'31'  
					                                             ELSE T.YARD||T.DONG||'PT'||'41'  
					                                        END
					                                END
					                       END AS GATE     
					                FROM    (  
					                           SELECT 
					                                SUBSTR(A.YD_STK_COL_GP,1,1) AS YARD,
					                                SUBSTR(A.YD_STK_COL_GP,2,1) AS DONG,
					                                SUBSTR(A.YD_STK_COL_GP,3,2) AS SPAN,
					                                SUBSTR(A.YD_STK_COL_GP,5,2) AS RYUL,
					                                A.YD_STK_COL_GP             AS YD_STK_COL_GP
					                           FROM (
					                                SELECT  /*+ ORDERED 
					                                    C.YD_STK_COL_GP 
					                                FROM TB_YD_STOCK A, V_DATA B, TB_YD_STKLYR C
					                                WHERE  A.TRANS_ORD_DATE = B.V_TRANS_ORD_DATE
					                                AND A.TRANS_ORD_SEQNO = B.V_TRANS_ORD_SEQNO
					                                AND A.CAR_NO = B.V_CAR_NO 
					                                AND A.STL_NO = C.STL_NO
					                                AND C.YD_STK_LYR_MTL_STAT IN ('C','U')
					                                AND (C.YD_STK_COL_GP LIKE '__0___%'  OR C.YD_STK_COL_GP LIKE '__CR__%' ) 
					                           )A
					                       ) T,
					                       TB_YD_RULE A
					               WHERE   A.REPR_CD_GP = 'T00031'
					                 AND   A.ITEM       = T.DONG
					               ) Y
					              ,TB_YD_STKCOL X
					              ,TB_YD_CARPOINT Z
					        WHERE  Y.GATE = X.YD_STK_COL_GP
					          AND  Y.GATE = Z.YD_STK_COL_GP  
					)
					WHERE PT_LOAD_LOC_CNT > 1
					UNION ALL
					/* 조합상차여부가 S, E 인것들 
					SELECT
					       A.TRANS_ORD_DATE
					     , A.TRANS_ORD_SEQNO
					     , A.CAR_NO
					FROM TB_YD_CARSCH A, V_DATA B
					WHERE A.TRANS_ORD_DATE = B.V_TRANS_ORD_DATE
					AND A.TRANS_ORD_SEQNO = B.V_TRANS_ORD_SEQNO
					AND A.CAR_NO = B.V_CAR_NO 
					AND A.DEL_YN = 'N'
					AND A.CMBN_CARLD_YN IN ('S','E')
				*/	
					if( ydPICommDAO.selectT(recMulti, rsMulti, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.isCMBN_CARLD_YN_PIDEV") > 0){
						// 동 결정순위 설정
						recInTemp.setField("DOUBLEDONG_YD_CAR_SCH_ID" ,	sDOUBLEDONG_YD_CAR_SCH_ID);
						sDongSelectQuery = "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getSelectDongByCMBN_CARLD_PIDEV";
					}
				}
				intRtnVal = ydPICommDAO.selectT(recInTemp, rsGetStock, sDongSelectQuery);			
    			
				if(intRtnVal <= 0){
					return ;
				}
				
				sMsg="[" + sOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 성공 - 건수["+intRtnVal+"]";
				commPiUtils.printLog(logId, sMsg , "SL");	
				
				rsGetStock.first();
				JDTORecord recStlNo = rsGetStock.getRecord();
				sYD_STK_COL_GP = commPiUtils.trim(recStlNo.getFieldString("YD_STK_COL_GP")); 
				
				// 동우선순위쿼리가 Select되었다면 이전 정차위치를 구한다.
				// 동일한 정차위치일경우 바로 도착처리모듈을 호출하기 위함
				// 만약 차량형상 사용유무가 Y일 경우엔 형상유무와 상관없이 크레인스케쥴을 편성한다.
				
				if("com.inisteel.cim.yd.common.dao.YdPlateCommDao.getSelectDongByCMBN_CARLD_PIDEV".equals(sDongSelectQuery)){
					sBefore_YD_CARLD_STOP_LOC = commPiUtils.trim(recStlNo.getFieldString("BF_YD_CARLD_STOP_LOC"));
				}
				
				sMsg="[" + sOperationName + "] 차량정지위치를 구하기 위한 대상재가 존재하는 동["+sYD_STK_COL_GP+"]";
				commPiUtils.printLog(logId, sMsg , "SL");	
				
				//-----------------------------------------------------------------------------------------------------------------
				//	사용가능한 차량정지위치로 적치열 조회 후 개소POINT를 구함
				//-----------------------------------------------------------------------------------------------------------------
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP",sYD_STK_COL_GP);
				
				/* com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0133 (입동포인트검색) 
				SELECT Y.GATE           AS YD_CARLD_STOP_LOC
				      ,X.WLOC_CD        AS WLOC_CD
				      ,X.YD_PNT_CD      AS YD_PNT_CD
				      ,Z.YD_CARPNT_CD   AS YD_CARPNT_CD
				FROM   (      
				        SELECT 
				               CASE WHEN T.SPAN||T.RYUL <= (CASE WHEN T.SPAN IN ('01','02','03')THEN ITEM1 ELSE ITEM2 END) 
				                    THEN T.YARD||T.DONG||'PT'||( CASE WHEN T.SPAN IN ('01','02','03') THEN '1' ELSE '3' END )||'1' 
				                    ELSE T.YARD||T.DONG||'PT'||( CASE WHEN T.SPAN IN ('01','02','03') THEN '2' ELSE '4' END )||'1' 
				               END AS GATE1
				               
				              ,CASE WHEN T.SPAN IN ('01','02','03') THEN
				                    -- 2후판일 경우
				                        CASE WHEN T.SPAN||T.RYUL <= ITEM1
				                             THEN T.YARD||T.DONG||'PT'||'11'  
				                             ELSE T.YARD||T.DONG||'PT'||'21'  
				                        END
				                    ELSE
				                    -- 1후판일 경우
				                        CASE WHEN T.DONG IN ('E','F') THEN
				                             -- E,F동일 경우
				                                CASE WHEN T.SPAN||T.RYUL <= ITEM_VALUE1
				                                     THEN T.YARD||T.DONG||'PT'||'31'  
				                                     WHEN T.SPAN||T.RYUL <= ITEM2
				                                     THEN T.YARD||T.DONG||'PT'||'51'  
				                                     ELSE T.YARD||T.DONG||'PT'||'41'  
				                                END
				                             ELSE
				                             -- E,F동이 아닐 경우
				                                CASE WHEN T.SPAN||T.RYUL <= ITEM2
				                                     THEN T.YARD||T.DONG||'PT'||'31'  
				                                     ELSE T.YARD||T.DONG||'PT'||'41'  
				                                END
				                        END
				               END AS GATE     
				        FROM   TB_YD_RULE, 
				               (  
				                   SELECT 
				                        SUBSTR(A.YD_STK_COL_GP,1,1) AS YARD,
				                        SUBSTR(A.YD_STK_COL_GP,2,1) AS DONG,
				                        SUBSTR(A.YD_STK_COL_GP,3,2) AS SPAN,
				                        SUBSTR(A.YD_STK_COL_GP,5,2) AS RYUL,
				                        A.YD_STK_COL_GP             AS YD_STK_COL_GP
				                   FROM (SELECT :V_YD_STK_COL_GP AS YD_STK_COL_GP FROM DUAL)A
				               )T
				       WHERE   REPR_CD_GP = 'T00031'
				         AND   ITEM       = T.DONG
				       ) Y
				      ,TB_YD_STKCOL X
				      ,TB_YD_CARPOINT Z
				WHERE  Y.GATE = X.YD_STK_COL_GP    
				  AND  Y.GATE = Z.YD_STK_COL_GP    
				  */
				intRtnVal = ydPICommDAO.selectT(recInTemp, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0133");		
				
				if( intRtnVal <= 0) {
					sMsg="["+sOperationName+"] 사용가능한 개소POINT 조회 시 오류발생 - 메세지 : ";
					commPiUtils.printLog(logId, sMsg , "SL");	
					return ;
				}
				
				rsResult.first();
				recInTemp		= rsResult.getRecord();
				
				sWLOC_CD	   = commPiUtils.trim(recInTemp.getFieldString("WLOC_CD")); //2013.11.14 복수 창고 출하시 개소코드 변경하기 위하여 추가				
				sYD_PNT_CD	   = commPiUtils.trim(recInTemp.getFieldString("YD_PNT_CD"));
				sYD_STK_COL_GP = commPiUtils.trim(recInTemp.getFieldString("YD_CARLD_STOP_LOC"));
				
				sMsg="["+sOperationName+"] 사용가능한 차량정지위치["+sYD_STK_COL_GP+"]로 개소POINT 조회 완료 - 메세지 : " + intRtnVal;
				commPiUtils.printLog(logId, sMsg , "SL");	
			
			}
			//-----------------------------------------------------------------------------------------------------------------
			
			if("Y".equals(sDOUBLEDONG_CHECK)) { //복수동일 경우 
				// [전사물류시스템개선]
				// 2021.1.6
				//  - 복수동상차와 복수동 하차를 사용하도록 수정
				//  - nTRANS_ORD_SEQNO번호가 999000 보다 큰 건은 차량입고(반품,회송,출고취소)
				//-----------------------------------------------------------------------------------------------------------------
				// 복수동 상차
				if( nTRANS_ORD_SEQNO < 999000){
					/*
					 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 초기화 수정한다.
					 */
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID",    		sDOUBLEDONG_YD_CAR_SCH_ID);
					recInTemp.setField("REGISTER",         		sModifier);
					recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
					recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
					recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
					recInTemp.setField("SPOS_WLOC_CD",     		sWLOC_CD);								//발지개소코드
					recInTemp.setField("YD_PNT_CD1",     		sYD_PNT_CD);							//야드포인트코드1
					recInTemp.setField("CAR_NO",           		sCAR_NO);								//차량번호
//					recInTemp.setField("CARD_NO",          		sCARD_NO);								//카드번호
					recInTemp.setField("CARD_NO",          		"");								    //카드번호
					recInTemp.setField("YD_CARLD_LEV_DT",  		commPiUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
					recInTemp.setField("TRANS_ORD_DATE",   		sTRANS_ORD_DATE);						//운송지시일자
					recInTemp.setField("TRANS_ORD_SEQNO",  		sTRANS_ORD_SEQNO);						//운송지시순번
			    	recInTemp.setField("YD_CARLD_STOP_LOC",		sYD_STK_COL_GP);						//차량상차정지위치
			    	recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
			    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		"2");									//입동지시순번  (2013.06.20 cho 1순위 --> 2순위 변경)
					recInTemp.setField("DEL_YN", 				"N");		
					
					 //2025.07.31 임진후기사 요청 RITM1277126
                    //복수동 5순위로 순위 조정
                    String sApplyYnPI = ydPICommDAO.ApplyYnPI("", sOperationName, "APP060", "T", "020");//후판 개발 적용여부
                    
					if("Y".equals(sApplyYnPI)){
						sMsg="[" + sOperationName + "] 복수동차량 2순위 -> 5순위 조정";
			    		commPiUtils.printLog(logId, sMsg , "SL");	
						recInTemp.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DOUBLE);	
                        
                    }
					
					//차량스케줄수정
			    	intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
			    	
		    		if( intRtnVal <= 0 ){
						sMsg="[" + sOperationName + "] 차량스케줄 수정 시 오류발생[반환값 : " + intRtnVal + "]";
						commPiUtils.printLog(logId, sMsg , "SL");	
						return ;
		    		}
		    		
		    		//차량 스케쥴재료수정
		    		YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao(); 
		        	intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(recInTemp, 1) ;
		            
		    		sMsg="[" + sOperationName + "] 차량스케줄 수정 완료";
		    		commPiUtils.printLog(logId, sMsg , "SL");	
					
					// 복수동상차우선순위에 의하여 동일한 정지위치에서 작업예약이 2번 발생할경우
					// 차량형상유무 상관없이 바로 도착처리하기 위함
					 
					if(PlateGdsYdUtil.isApplyYn("복수상차우선순위적용여부")){
						
						if(sBefore_YD_CARLD_STOP_LOC.equals(sYD_STK_COL_GP)){
							// YD_CARLD_SCH_REQ_GP 스케쥴요청구분컬럼에 형상PASS 여부를 업데이트한다. 
							// YD_CARLD_SCH_REQ_GP 컬럼은 과거 공대차구분(대차에서 씌임)
							// 운영계확인결과 TB_YD_CARSCH에 사용되고 있지 않음(NULL값임)
							sMsg="[" + sOperationName + "] 전과 동일한 차량정지위치임 이전["+sBefore_YD_CARLD_STOP_LOC+"]현재["+sYD_STK_COL_GP+"]";
							commPiUtils.printLog(logId, sMsg , "SL");	
						
							JDTORecord jtoUpdate = JDTORecordFactory.getInstance().create();
							jtoUpdate.setField("YD_CAR_SCH_ID"       , sDOUBLEDONG_YD_CAR_SCH_ID);
							jtoUpdate.setField("YD_CARLD_SCH_REQ_GP" , "Y");
							jtoUpdate.setField("MODIFIER"            , sModifier); 
							ydPICommDAO.update(jtoUpdate, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCarschByFrmYnPassYn");

						}
					}
		    		
				}
				// 차량입고(반품) 복수동 하차
				else{
        			JDTORecord params = JDTORecordFactory.getInstance().create();
        			JDTORecordSet rsCarStopLoc = JDTORecordFactory.getInstance().createRecordSet("");
        			params.setField("YD_CAR_SCH_ID",    		sDOUBLEDONG_YD_CAR_SCH_ID);
					if( ydPICommDAO.selectT(params, rsCarStopLoc, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCarUnloadStopLoc") > 0){
						
						recInTemp = JDTORecordFactory.getInstance().create();
						sYD_STK_COL_GP = rsCarStopLoc.getRecord(0).getFieldString("YD_CARUD_STOP_LOC");
						recInTemp.setField("YD_PNT_CD3",     				rsCarStopLoc.getRecord(0).getFieldString("YD_PNT_CD"));
						recInTemp.setField("YD_CARUD_STOP_LOC",     		sYD_STK_COL_GP);
						
						recInTemp.setField("YD_CAR_SCH_ID",    		sDOUBLEDONG_YD_CAR_SCH_ID);
						recInTemp.setField("REGISTER",         		sModifier);
						recInTemp.setField("YD_EQP_WRK_STAT",  		"L");									//야드설비작업상태
						recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
						recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
						recInTemp.setField("YD_CARUD_LEV_DT",  		commPiUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
						recInTemp.setField("TRANS_ORD_DATE",   		sTRANS_ORD_DATE);						//운송지시일자
						recInTemp.setField("TRANS_ORD_SEQNO",  		sTRANS_ORD_SEQNO);						//운송지시순번
				    	recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARUD_LEV);				//상차출발상태
				    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		"2");									//입동지시순번  (2013.06.20 cho 1순위 --> 2순위 변경)
				    	recInTemp.setField("DEL_YN", 				"N");
						
				    	 //2025.07.31 임진후기사 요청 RITM1277126
	                    //복수동 5순위로 순위 조정
	                    String sApplyYnPI = ydPICommDAO.ApplyYnPI("", sOperationName, "APP060", "T", "020");//후판 개발 적용여부
	                    
						if("Y".equals(sApplyYnPI)){
							sMsg="[" + sOperationName + "] 복수동차량 2순위 -> 5순위 조정";
				    		commPiUtils.printLog(logId, sMsg , "SL");	
							recInTemp.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DOUBLE);	
	                        
	                    }
				    	
						//차량스케줄수정
				    	intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);

				    	// DEL_YN = 'Y' 처리한다.
	        			params = JDTORecordFactory.getInstance().create();
	        			params.setField("YD_CAR_SCH_ID", sDOUBLEDONG_YD_CAR_SCH_ID);
	        			ydPICommDAO.update(params,  "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updYdCarftmvmtlByCaudStopLoc");
 					}
				}

				
			} else { //복수동이 아닐 경우  

					/*
					 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
					 */
					sYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID",    		sYD_CAR_SCH_ID);
					recInTemp.setField("REGISTER",         		sModifier);
					recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
					recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
					recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
					recInTemp.setField("CAR_KIND", 				StringHelper.evl(sCAR_KIND, "TR"));
					recInTemp.setField("SPOS_WLOC_CD",     		sWLOC_CD);								//발지개소코드
					recInTemp.setField("YD_PNT_CD1",     		sYD_PNT_CD);							//야드포인트코드1
					recInTemp.setField("CAR_NO",           		sCAR_NO);								//차량번호
//PIDEV					recInTemp.setField("CARD_NO",          		sCARD_NO);								//카드번호
					recInTemp.setField("CARD_NO",          		"");								//카드번호
					recInTemp.setField("YD_CARLD_LEV_DT",  		commPiUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
					recInTemp.setField("TRANS_ORD_DATE",   		sTRANS_ORD_DATE);						//운송지시일자
					recInTemp.setField("TRANS_ORD_SEQNO",  		sTRANS_ORD_SEQNO);						//운송지시순번
			    	recInTemp.setField("YD_CARLD_STOP_LOC",		sYD_STK_COL_GP);						//차량상차정지위치
		    		recInTemp.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 --기본값으로 설정(9) **
					recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
					recInTemp.setField("IF_SEQ_NO", 			"0");							        //운송지시 SEQ
					
					recInTemp.setField("YD_CAR_WRK_GP",    		sWORK_GP);
					recInTemp.setField("TEL_NO", 		   		sTEL_NO);								//기사핸드폰번호
					recInTemp.setField("CMBN_CARLD_YN",    		sCMBN_CARLD_YN);						//첫번째 도착창고 : S 두번째 도착창고 : E
					recInTemp.setField("WAIT_ARR_DDTT",    		sWAIT_ARR_DDTT);						//대기장도착시간
					recInTemp.setField("WAIT_ARR_GP",      		sWAIT_ARR_GP);							//대기장도착구분  - B:BACKUP , S:SMARTPHONE		
					
					// 전사물류개선 2021. 1. 6
					// 길이 폭으로 차종을 구한다.
					// --가변슬라이드 : 폭이 3400 초과 && 길이 14,000 초과
					// --가변차량 : 폭이 3400 초과 && 길이 14,000 이하
					// --일반 : 폭이 3400 이하 && 길이 14,000 이하
					// --일반슬라이드 : 폭이 3400 이하 && 길이 14,000 초과
					JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
					JDTORecordSet rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
					String sCarKind = "";

					if(sCAR_KIND.startsWith("P")){
						sCarKind = "PT";
					} else if (sCAR_KIND.startsWith("T")){
						sCarKind = "TR";
					} else if (sCAR_KIND.startsWith("C")){
						sCarKind = "TR";
					} else{
						recInTemp1.setField("TRANS_ORD_DATE"  , sTRANS_ORD_DATE);
						recInTemp1.setField("TRANS_ORD_SEQNO" , sTRANS_ORD_SEQNO);
						recInTemp1.setField("CAR_NO"          , sCAR_NO );
						if(ydPICommDAO.selectT(recInTemp1, rsResult2, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCarKindByWdithLength")>0){
							sCarKind =  rsResult2.getRecord(0).getFieldString("CAR_KIND");
						}
						else{
							sCarKind = "TR";
						}
					}	
					
					recInTemp.setField("TRANS_EQUIPMENT_TYPE", sCarKind);	// 세부차종
//PIDEV 추가
//tr이송건					
					recInTemp.setField("YD_WRK_PROG_STAT"    , "9"); // 1:일반  9:tr이송		
					
//김기태 부장님 	01/13		
					recInTemp.setField("SHIP_NAME"           , sSHIP_NAME);							//선박명
		    		//차량스케줄 등록
			    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
		    		if( intRtnVal <= 0 ){
						sMsg="[" + sOperationName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
						commPiUtils.printLog(logId, sMsg , "SL");
						return ;
		    		}
		    		
					// YD_STOCK 차량TYPE정보 Set				
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CONVEYOR_BRANCH_CD" , sCarKind); 
					recInTemp.setField("YD_CAR_SCH_ID"         , sYD_CAR_SCH_ID); 
					recInTemp.setField("MODIFIER"              , sModifier  );	//수정자
					ydPICommDAO.update(recInTemp, "com.inisteel.cim.yd.dao.ydstockdao.updYdCarTransType");
					
					// 2021. 4. 7
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("TRANS_ORD_DATE"     , sTRANS_ORD_DATE);						//운송지시일자
					recInTemp.setField("TRANS_ORD_SEQNO"    , sTRANS_ORD_SEQNO);						//운송지시순번
					recInTemp.setField("CAR_NO"             , sCAR_NO);								//차량번호
					recInTemp.setField("YD_FTMV_MEANS_GP"   , sYD_FTMV_MEANS_GP);
					recInTemp.setField("CR_FRTOMOVE_GP"     , sCR_FRTOMOVE_GP);
					recInTemp.setField("MODIFIER"           , sModifier  );	//수정자
					ydPICommDAO.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateYdStockByFTMV");
					
		    		sMsg="[" + sOperationName + "] 차량스케줄 생성 완료";
		    		commPiUtils.printLog(logId, sMsg , "SL");
			}

			/*
			 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
			 */
		
			sMsg="[" + sOperationName + "] 차량정지위치[" + sYD_STK_COL_GP + "], 차량스케줄ID[" + sYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 시작";
			commPiUtils.printLog(logId, sMsg , "SL");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("TC_CODE"			, "YDYDJ633");
			recInTemp.setField("TC_CREATE_DDTT"		, commPiUtils.getCurDate("yyyyMMddHHmmss"));
			recInTemp.setField("YD_CAR_STOP_LOC"	, sYD_STK_COL_GP);
			recInTemp.setField("YD_CAR_SCH_ID"		, sYD_CAR_SCH_ID);
			recInTemp.setField("CALL_PGM"			, "SANGCHA");
			
			//----------------------------------------------------------------------------------
			//	동기화 문제로 인하여 JMS --> EJB Call로 변경
			//----------------------------------------------------------------------------------
//PIDEV 반드시 확인			
			EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
			String sRtnMsg = (String) ejbConn.trx("procCarBayInOrdReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
			
			//----------------------------------------------------------------------------------
			sMsg="[" + sOperationName + "] 차량정지위치[" + sYD_STK_COL_GP + "], 차량스케줄ID[" + sYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 성공 - 메세지 : " + sRtnMsg;
			commPiUtils.printLog(logId, sMsg , "S-");
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}
		
	
	/**	 
	 * [A] 오퍼레이션명 :후판제품Pallet상차도착(DMYDR038)- procPlGdsDistCarArrWr
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1062(JDTORecord rcvMsg) throws DAOException {  //S
		String mthdNm = "[YdPlateL3RcvPISeEJB.rcvM10LMYDJ1062] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		String sRtnVal	 = null;
		
		try {
			commPiUtils.printLog(logId, mthdNm, "S+");

//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "T", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}					
			
			String sOperationName	   = "후판제품출하차량도착실적처리";
//		    String sMethodName         = "procPlGdsDistCarArrWr";
		    
		    String sMsg  = "";
					
			commPiUtils.printLog(logId, "[출하] 후판제품출하차량도착실적처리 수신" , "SL");
			
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
			String msgId             = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
            String sTRANS_ORD_DT     = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"      ));   // 운송지시일자 
            String sTRANS_ORD_SEQNO  = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"   ));   // 운송지시순번 
            String sCAR_NO           = commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"            ));   // 차량번호
            String sSPOS_WLOC_CD     = commPiUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"      ));   //
            String sSPOS_YD_PNT_CD   = commPiUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"    ));   //
            String sYD_CARPNT_CD     = commPiUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD"      ));   // 야드차량 포인트
            String sModifier         = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"           ));   //수정자(Backup Only)
            String ydBayGp           = "";
            if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }

            JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            
            JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
            if(!sYD_CARPNT_CD.equals("")) {
	    		
	    		//차량포인트 테이블에서 WLOC_CD 와 YD_PNT_CD 를 읽어 온다. 

	    		recInTemp.setField("YD_CARPNT_CD" , sYD_CARPNT_CD);

	    		int intRtnVal = ydPICommDAO.selectT(recInTemp, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0102");

	    		if(intRtnVal <= 0){
	    		    sMsg="["+sOperationName+"] PALLET 출하 차량포인트 조회 시 오류발생 - 반환값 : " + intRtnVal;
	    		    commPiUtils.printLog(logId, sMsg , "SL");				
	    		    throw new DAOException(sMsg);
	    		}

	    		rsResult.first();
	    		recInTemp = rsResult.getRecord();
	    		
	    		rcvMsg.setField("SPOS_WLOC_CD"   , commPiUtils.trim(recInTemp.getFieldString("WLOC_CD" )));
	    		rcvMsg.setField("SPOS_YD_PNT_CD" , commPiUtils.trim(recInTemp.getFieldString("YD_PNT_CD" )));
	    		ydBayGp = commPiUtils.trim(recInTemp.getFieldString("YD_BAY_GP" ));
	    		
	    		commPiUtils.printLog(logId, "포인트 도착 동 : ["+ydBayGp+"]동" , "SL");
            }            
            
			if(sTRANS_ORD_DT.equals("")) {
				sMsg = "[전문 이상] 운송지시일자가 없습니다.";
				commPiUtils.printLog(logId, sMsg , "SL");	
				throw new DAOException(sMsg);
			}	    	
	    	
			if(sTRANS_ORD_SEQNO.equals("")) {
				sMsg = "[전문 이상] 운송지시순번이 없습니다.";
				commPiUtils.printLog(logId, sMsg , "SL");	
				throw new DAOException(sMsg);
			}	    	

			if(sCAR_NO.equals("")) {
				sMsg = "[전문 이상] 차량번호가 없습니다.";
				commPiUtils.printLog(logId, sMsg , "SL");	
				throw new DAOException(sMsg);
			}	    	
			
			JDTORecordSet rsStkCol = JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecord recStkCol = JDTORecordFactory.getInstance().create();
			
			if( this.procOutCarArrWr_PIDEV(rcvMsg, rsStkCol) > 0 ) {
			
				rsStkCol.absolute(1);
				recStkCol = rsStkCol.getRecord();
				
				String sYD_CARLD_STOP_LOC = rcvMsg.getFieldString("YD_CARLD_STOP_LOC");
				
				//전문발송처리//후판제품출하상차LOT편성전문
				rcvMsg.setField("IS_EJB_CALL", "Y");

				sRtnVal = this.sndOutCarLoadLotTC_PIDEV(rcvMsg, recStkCol, "YDYDJ284", "rcvM10LMYDJ1062", "procPlGdsDistCarLdLotComp");
				
				//--------------------------------------------------------------------------------------------------
				//	
				//--------------------------------------------------------------------------------------------------
				/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	    		 * 업무기준 : 후판제품출하 공차 도착 시 저장위치 제원 야드L2로 전송

	    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
				sMsg="["+sOperationName+"] 저장위치 제원 야드L2로 전송 시작";
				commPiUtils.printLog(logId, sMsg , "SL");
				
				JDTORecord recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_INFO_SYNC_CD"   , "3");							//1:동,2:SPAN,3:열,4:BED
				recPara.setField("YD_GP"             , sYD_CARLD_STOP_LOC.substring(0, 1));
				recPara.setField("YD_STK_COL_GP"     , sYD_CARLD_STOP_LOC);
				recPara.setField("YD_CAR_PROG_STAT"  , YdConstant.YD_CARLD_ARR);
				recPara.setField("YD_EQP_WRK_STAT"   , "U");
				
				//25.01.15 길선배 주임 요청 출하차량 스케줄 올라갈시 색상 변경이 아닌, 도착 기준으로 변경 START(도착처리여부 확인용)
				String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APP060", "T", "009");//후판 개발 적용여부
				
				//25.04.17 임진후기사 요청. 출하차량 도착기준으로 변경은 유인크레인에 한해서만 변경. (자동화는 형상스캔 이후 변경)
				if("Y".equals(sApplyYnPI) && !"B".equals(ydBayGp)){
					recPara.setField("CAR_POINT_ARRIVED_FLAG", 	"Y");  //포인트 도착 후 스케줄 기동 대상인지 표기 구분 추가 
				}
				
				YdCommonUtils.sndStrPosSpecToL2(recPara);

				commPiUtils.printLog(logId, "["+sOperationName+"] 저장위치 제원 야드L2로 전송 성공" , "SL");	
				
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	    	}else{
	    		sMsg = "출하차량도착실적처리 - 맵활성화 오류";
				commPiUtils.printLog(logId, sMsg , "SL");	
	    		throw new DAOException(sMsg);
	    	}//end of if			
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}

	
	/**	 
	 * [A] 오퍼레이션명 :후판제품출하완료(DMYDR031)-procPlGdsDistCmpl 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1072(JDTORecord rcvMsg) throws DAOException { //S
		String mthdNm = "[YdPlateL3RcvPISeEJB.rcvM10LMYDJ1072] < " + rcvMsg.getResultMsg();
		//String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";		
		
		try {
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "T", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}					
			
			YdStockDao ydStockDao 				= new YdStockDao();
			YdCarSchDao ydCarSchDao 	        = new YdCarSchDao();
			JDTORecord recInTemp 				= null;
			JDTORecord recResult 				= null;
			JDTORecordSet rsResult 				= null;

			String sMethodName 				    = "procPlGdsDistCmpl";
			String sOperationName 				= "후판제품출하완료(DMYDR031)";
			String sMsg 						= "";
			String sIS9NI                       = null;
			
			int intRtnVal = 0;
			boolean is9Ni=false;  //9%니켈 강종 여부 체크
		
			sMsg = "[출하] 후판제품출하완료 수신";
			commPiUtils.printLog(logId, sMsg , "SL");	
			
			/********************************
			 * 수신 전문 
			 ********************************/
			
			String msgId            = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID

			rcvMsg.setField("TRANS_ORD_DATE" , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
			String sYD_GP			= commPiUtils.trim(rcvMsg.getFieldString("YD_GP"         ));
			String sSTL_NO  		= commPiUtils.trim(rcvMsg.getFieldString("STL_NO"        ));
			String sSTL_APPEAR_GP	= commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP" ));   // 재료외형구분
			String sCURR_PROG_CD	= commPiUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"  ));   //재료진도코드 
			
			String sTRANS_ORD_DATE	= commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DATE"));   // 
			String sTRANS_ORD_SEQNO	= commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"   ));   // 
			String sCAR_NO	        = commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"        ));   // 
			
			String sModifier        = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));             //수정자(Backup Only)
			String backUpYn			= commPiUtils.trim(rcvMsg.getFieldString("BACKUP_YN"));
			
            if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
            
			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP	재료외형구분
			STL_NO			재료번호
			*/
			JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();
			recStockColumn.setField("STL_APPEAR_GP"  , sSTL_APPEAR_GP);
			recStockColumn.setField("STL_NO"         , sSTL_NO);
			recStockColumn.setField("DEL_YN"         , "N");
			recStockColumn.setField("CAR_LOTID"      , "");
			recStockColumn.setField("STL_PROG_CD"    , "L".equals(sCURR_PROG_CD)? "L":"M" );
			recStockColumn.setField("YD_AIM_RT_GP"   , "L".equals(sCURR_PROG_CD)? "M5":"M3");
			recStockColumn.setField("MODIFIER"       , sModifier);

			//박판야드 후판제품의 경우, 상차완료 수신시 야드맵 클리어 작업 수행.  2024.03.22
			if(sYD_GP.equals("4") || sYD_GP.equals("")){ //박판창고 내 출하는 창고구분 null로 들어옴.  우선 배포안함. 명우 팀장님과 협의 후 배포 예정.
			
				sMsg= "["+ sOperationName +"] 박판야드 후판제품의 경우, 상차완료 수신시 야드맵 클리어 작업 수행.";
				commPiUtils.printLog(logId, sMsg , "SL");
				
				JDTORecord recPara3		= JDTORecordFactory.getInstance().create();
				recPara3.setField("STL_NO", sSTL_NO);  
				
				intRtnVal = ydPICommDAO.update(recPara3, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStklyrInitC", logId, sMethodName, "해당 재료 적치단 clear");
				
				if(intRtnVal==0){
					sMsg= "["+ sOperationName +"] 박판창고(후판제품)[" + sSTL_NO + "]에 대한 적치단 clear 과정 중 Error 발생";
					commPiUtils.printLog(logId, sMsg , "SL");	
				}
			}
			
			
			
			//----------------------------------------------------------------------------------------------------
			//	9% 니켈강종이며, A동에 속해있는 재료일 경우, 야드 맵 클리어 작업 수행. 이명운 책임매니저 요청사항
			//	등록자 : 박종호
			//	등록일 : 2021.08.09
			//----------------------------------------------------------------------------------------------------
			JDTORecord recPara2		= JDTORecordFactory.getInstance().create();
			recPara2.setField("PLATE_NO", sSTL_NO);    
			//JDTORecordSet getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getPLATECOMM", logId, sMethodName, "제품정보 조회");
			JDTORecordSet getRecSet	= ydPICommDAO.selectT(recPara2, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getChk9NiByStlNo", logId, sMethodName, "제품 9%니켈강여부 조회");
			
			if (getRecSet.size() > 0) {
				for (int ii=0; ii<getRecSet.size(); ii++) {
					sIS9NI  	= getRecSet.getRecord(ii).getFieldString("PL_NI_RMN_MAG_MEA_MTL_ASGN_GP");
					//if("LR-9NI".equals(sSPEC_ABBSYM) || "9NI-QT".equals(sSPEC_ABBSYM) || "KR-RL9N490QT".equals(sSPEC_ABBSYM))  //해당재료가 9% 니켈강 강종일경우, 재료위치가 제품창고 A동인지 조회
					//if(1==1) //니켈 규정 강종이 자주 바뀌어서, A동에서 제품출하지시받으면 무조건 니켈이라고 가정하고 처리함. 2021.12.02 허동수매니저 요청사항.
					if(sIS9NI.equals("Y")) //9%니켈 판단 여부를 기존 특정 강종코드에서 품질에서 관리되는 FLAG값으로 변경 2021.12.17
					{
						recPara2 = JDTORecordFactory.getInstance().create();
						recPara2.setField("STL_NO"	, sSTL_NO);
						
						getRecSet = ydPICommDAO.selectT(recPara2, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getStrLocInfo2", logId, sMethodName, "재료 위치 조회");
						if(getRecSet.size()>0){
							String sYD_GP_BAY_GP  	= getRecSet.getRecord(ii).getFieldString("YD_STK_COL_GP").substring(0,2);
							for (int j=0; j<getRecSet.size(); j++) {
								if("TA".equals(sYD_GP_BAY_GP)){  //제품창고 A동일 경우 해당 재료 적치단 Clear
									is9Ni=true;
									//야드맵 클리어
									recPara2		= JDTORecordFactory.getInstance().create();
									recPara2.setField("MODIFIER"	, sModifier); 
									recPara2.setField("STL_NO"	    , sSTL_NO); 
									intRtnVal = ydPICommDAO.update(recPara2, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStklyrInitC", logId, sMethodName, "해당 재료 적치단 clear");
									
									if(intRtnVal==0){
										sMsg= "["+ sOperationName +"] 후판제품[" + sSTL_NO + "]에 대한 적치단 clear 과정 중 Error 발생";
										commPiUtils.printLog(logId, sMsg , "SL");	
									}
									
									recPara2.setField("YD_GP"	      , getRecSet.getRecord(j).getFieldString("YD_STK_COL_GP").substring(0,1));  //T
									recPara2.setField("YD_BAY_GP"	  , getRecSet.getRecord(j).getFieldString("YD_STK_COL_GP").substring(1,2));  //A
									recPara2.setField("YD_EQP_GP"	  , getRecSet.getRecord(j).getFieldString("YD_STK_COL_GP").substring(2,4));  //07
									recPara2.setField("YD_STK_COL_NO" , getRecSet.getRecord(j).getFieldString("YD_STK_COL_GP").substring(4,6));  //61
									recPara2.setField("YD_STK_BED_NO" , getRecSet.getRecord(j).getFieldString("YD_STK_BED_NO"));  //01
									recPara2.setField("YD_STK_LYR_NO" , getRecSet.getRecord(j).getFieldString("YD_STK_LYR_NO"));  //030
									recPara2.setField("YD_STR_LOC"    , getRecSet.getRecord(j).getFieldString("YD_STK_COL_GP").substring(0,2)+"PT011001");  //TAPT011001 (Default:차상위치) 
									recPara2.setField("MODIFIER"      , sModifier);  
									recPara2.setField("FNL_REG_PGM"   , "procPlGdsDistCmpl");
									recPara2.setField("PLATE_NO"      , sSTL_NO);
									
									if(intRtnVal!=0){
										//저장위치 수정 모듈 호출(적치단 클리어 및 PLATE_COMM UPDATE)
										sMsg = "해당재료[" + sSTL_NO  +"] 에대한 후판 공통 UPDATE 작업시작" ;
										commPiUtils.printLog(logId, sMsg , "SL");	
										
										intRtnVal = ydStockDao.updPtComm_LOC(recPara2, 1);
										
										if(intRtnVal< 0) {
											sMsg = "공통 UPDATE ERROR";
											commPiUtils.printLog(logId, sMsg , "SL");	
											//return ;
										}else if (intRtnVal == 0){
											sMsg = "후판 공통 UPDATE 해야할 데이터가 없습니다.";
											commPiUtils.printLog(logId, sMsg , "SL");	
										}else{
											sMsg = "후판 공통 UPDATE 성공 재료번호[" + sSTL_NO + "]";
											commPiUtils.printLog(logId, sMsg , "SL");	
										}
									}
								}
							}
						}
					}
				}
			}
			
						
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				sMsg= "["+sOperationName+"] YD_STOCK[후판제품출하완료] UPDATE Error :: [" + intRtnVal + "]";
				commPiUtils.printLog(logId, sMsg , "SL");	
				return ;
			}
			commPiUtils.printLog(logId, "["+sOperationName+"] [2] YD_STOCK[후판제품출하완료] UPDATE Success" , "SL");	
			//****************************************************************************************************

			//----------------------------------------------------------------------------------------------------
			//	재료외형구분에 *로 설정되어 오는 경우에는 마지막재료의 출하완료 처리이므로 차량출발 처리 모듈 호출
			//----------------------------------------------------------------------------------------------------
			
			String sCarKind = "";
			String ydCarSchId = "";
			
			if( sSTL_APPEAR_GP.equals("*")) {
				
				sMsg= "["+ sOperationName +"] 출하완료된 후판제품[" + sSTL_NO + "]의 재료외형구분["+sSTL_APPEAR_GP+"]이 *이므로 차량출발 모듈 호출 처리";
				commPiUtils.printLog(logId, sMsg , "SL");
				
				sMsg= "["+ sOperationName +"] 차량번호[" + sCAR_NO + "]로 차량스케줄 조회 시작";
				commPiUtils.printLog(logId, sMsg , "SL");

				
				recInTemp  = JDTORecordFactory.getInstance().create();
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp.setField("CAR_NO"          , sCAR_NO);
//				recInTemp.setField("CARD_NO"         , sCARD_NO);
				recInTemp.setField("TRANS_ORD_DT"    , sTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO" , sTRANS_ORD_SEQNO );

//PIDEV_S :병행가동용:PI_YD
				recInTemp.setField("PI_YD",    	sYD_GP);						
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschTransDTSeq2_PIDEV*/
				intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 413);
				
//				if(intRtnVal <= 0){
//					sMsg = "["+sOperationName+"] 차량번호[" + sCAR_NO + "]와 카드번호[" + "]로 차량스케줄 조회 시 에러발생 - 반환값 : " + intRtnVal;
//				}
				if (intRtnVal > 0) {
					rsResult.first();
					recResult = rsResult.getRecord();
					sCAR_NO   = commPiUtils.trim(recResult.getFieldString("CAR_NO"   )); 
//PIDEV				
					sCarKind  = commPiUtils.trim(recResult.getFieldString("CAR_KIND" ));
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);	//Logging 을 위한 ID
					recInTemp.setResultMsg(sMethodName);	//상위 Method 명
					recInTemp.setField("TC_CODE",        		"DMYDR042");	//전문코드
//					recInTemp.setField("MQ_TC_CD",        		"M10LMYDJ1082");	//전문코드
					recInTemp.setField("YD_GP",					sYD_GP);		//야드구분
//					recInTemp.setField("CARD_NO", 				commPiUtils.trim(recResult.getFieldString("CARD_NO")));
					recInTemp.setField("CAR_NO", 				sCAR_NO);			
					recInTemp.setField("SPOS_WLOC_CD", 			commPiUtils.trim(recResult.getFieldString("SPOS_WLOC_CD")));
					recInTemp.setField("SPOS_YD_PNT_CD", 		commPiUtils.trim(recResult.getFieldString("YD_PNT_CD1")));
					recInTemp.setField("TRN_REQ_DATE", 			commPiUtils.trim(recResult.getFieldString("TRANS_ORD_DATE")));
					recInTemp.setField("TRN_REQ_SEQ", 		    commPiUtils.trim(recResult.getFieldString("TRANS_ORD_SEQNO")));
					recInTemp.setField("TRANS_ORD_DT",          sTRANS_ORD_DATE);
					recInTemp.setField("TRANS_ORD_SEQNO",       sTRANS_ORD_SEQNO );
					
					sCarKind = commPiUtils.trim(recResult.getFieldString("CARD_NO"));
					ydCarSchId = commPiUtils.trim(recResult.getFieldString("YD_CAR_SCH_ID"));
					
					//E/T Car 나 해송차량인 경우에는 차량출발처리를 자동으로 하지 않는다.  
					//if(sCAR_NO.matches("\\d\\d\\d\\d")) {				//E/T Car
					
					//E/T Car 나 해송차량인 경우 또는 9%니켈강종의 경우 차량출발처리를 자동으로 하지 않는다.
					if(sCAR_NO.matches("\\d\\d\\d\\d") || is9Ni) {				//E/T Car 또는 9%니켈 강종
						sMsg= "["+ sOperationName +"] E/T Car[" + sCAR_NO + "]는 차량출발처리를 하지 않습니다.";
						commPiUtils.printLog(logId, sMsg , "SL");
					} else {
						sMsg= "["+ sOperationName +"] 차량번호[" + sCAR_NO + "]는 자동으로 차량출발 모듈 EJB 호출 시작";
						commPiUtils.printLog(logId, sMsg , "SL");
						
						// 2021. 06. 22 차량선별입동지시시점[T00171] "D"일경우 입동지시 전문이 날아가지 않는 문제 수정
						recInTemp.setField("CALL_PGM", "SANGCHA"); // 입동지시 전문을 보내기 위함
						EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
						ejbConn.trx("rcvPlGdsDistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });

//PIDEV					
//						EJBConnector ejbConn = new EJBConnector("default", "YdPlateL3RcvPISeEJB", this);
//						ejbConn.trx("rcvM10LMYDJ1082", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
						
						sMsg= "["+ sOperationName +"] 차량번호[" + sCAR_NO + "]는 자동으로 차량출발 모듈 EJB 호출 완료";
						commPiUtils.printLog(logId, sMsg , "SL");
					}
				}
			}
			
			
			//======================================================
			// 저장품제원 : 후판제품 L2 로 송신(YDY8002)
			//======================================================
			//if(!is9Ni){  //9%니켈강종의 경우 L2 정보 송신 안함 2021.08.09
			//if(true){  //9%니켈강종도 L2 야드 정보 송신으로 변경 2021.08.12 (L2 야드맵 클리어 위해)
			if(sYD_GP.equals("T")){  //9%니켈강종도 L2 야드 정보 송신으로 변경 2021.08.12 (L2 야드맵 클리어 위해)
				//후판창고 내 출하일 경우만 L2 전송(박판창고출하는 YD_GP값 null로 수신됨)
				String sMSG_ID = null;
				
				if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(sYD_GP)) { //- 2013.01.17 수정 (3기)
					sMSG_ID = "YDY8L002"; //2후판 제품창고
					//2024.08.05 YDY4L002 미사용 전문 타는 경우 있어, ELSE 문 제거 및 야드 T 일 경우만 송신처리. by HJW
					sMsg= "["+ sOperationName +"] 후판제품[" + sSTL_NO + "]에 대한 저장품제원을 후판제품L2["+sMSG_ID+"]로 송신 시작";
					commPiUtils.printLog(logId, sMsg , "SL");
					
					recResult = JDTORecordFactory.getInstance().create();
					recResult.setField("MSG_ID"         , sMSG_ID);
					recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
					recResult.setField("STL_NO"         , sSTL_NO);
					recResult.setField("YD_STK_COL_GP"  , "");
					recResult.setField("YD_STK_BED_NO"  , "");
					recResult.setField("DEL_YN_CHECK"   , "N");			
					ydDelegate.sendMsg(recResult);
				} 				
				
				// 2021. 4. 3 출하완료시 Y9도 강제로 전송한다.
				if("YDY8L002".equals(sMSG_ID)){
					recResult.setField("MSG_ID"         , "YDY9L002");
					ydDelegate.sendMsg(recResult);
				}
				
				sMsg= "["+ sOperationName +"] 후판제품[" + sSTL_NO + "]에 대한 저장품제원을 후판제품L2["+sMSG_ID+"]로 송신 완료";
				commPiUtils.printLog(logId, sMsg , "SL");
			}
// 기존 fa에 있는 내용			
			String sCARLD_PNT_CD  = StringHelper.evl(rcvMsg.getFieldString("CARLD_PNT_CD"), "");
//			String sSTL_APPEAR_GP = StringHelper.evl(rcvMsg.getFieldString("STL_APPEAR_GP"), "");
//			String sCARD_NO       = StringHelper.evl(rcvMsg.getFieldString("CARD_NO"), "");
//PIDEV			
			if("*".equals(sSTL_APPEAR_GP) && "T".equals(sYD_GP) && sCARLD_PNT_CD.endsWith("2") && sCarKind.startsWith("P")) {
				YdPlateCommDAO	commDao = new YdPlateCommDAO();			
				
				//TB_YD_CARPOINT 상태 변경
				commDao.update(rcvMsg, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0051");
				
				//TB_YD_STKCOL 상태변경
				commDao.update(rcvMsg, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0050");
			}		

			commPiUtils.printLog(logId, "백업 여부 [" + sSTL_NO + "] - " + backUpYn , "SL");
			
			//======================================================
			// 백업으로 취소 처리 시
			//======================================================
			if ("Y".equals(backUpYn)) {
				// 정상 처리 여부 확인
				JDTORecord jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setField("PI_YD",    	"T");
				jrParam.setField("PLATE_NO", sSTL_NO);
				
				JDTORecordSet getCoilComm	= ydPICommDAO.selectT(jrParam, "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECOMM_PIDEV", logId, sMethodName, "후판 공통 조회");
				
				if (getCoilComm.size() <= 0) {
					throw new Exception("TB_PT_COILCOMM 존재하지 않는 코일번호:" + sSTL_NO);
				}

				// 적지단 (STKLAYR) 초기화 -->
				String ydStkColGp = "TX0101";
				String ydGpBay    = "";
				String ydStkBedNo = "01";
				String ydStkLyrNo = "001";
				
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setField("PI_YD",    	"T");
				jrParam.setField("STL_NO", 	sSTL_NO);

				getRecSet = ydPICommDAO.selectT(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getStrLocInfo2", logId, sMethodName, "재료 위치 조회");
				if(getRecSet.size() > 0) {				
					
					ydStkColGp = commPiUtils.trim(getRecSet.getRecord(0).getFieldString("YD_STK_COL_GP"));
					ydStkBedNo = commPiUtils.trim(getRecSet.getRecord(0).getFieldString("YD_STK_BED_NO"));
					ydStkLyrNo = commPiUtils.trim(getRecSet.getRecord(0).getFieldString("YD_STK_LYR_NO"));

					String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI3", "T", "*");
					if(sApplyYnPI.equals("Y")){//복수동 상차 백업시, 실제 상차된 대상만 야드 클리어하도록 변경.(실제 상차 안된동은 야드맵에 남겨두도록) 2023.05.22 문제윤 주임 요청.
						ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrT", logId, mthdNm, "적치단 수정");
					}
					else{
						ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStklyrInitC", logId, mthdNm, "적치단 수정");
					}
					commPiUtils.printLog(logId, "[코일제품출하완료][" + sSTL_NO + "]에 저장위치맵을 비웁니다.+" + ydGpBay, "SL");
				}
				// 적지단 (STKLAYR) 초기화 <--
				
				
				/***********************************
				 * Coil 공통 Table 저장위치 Update 
				 ***********************************/
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setField("STL_NO" , sSTL_NO);
				
				getRecSet = ydPICommDAO.selectT(jrParam, "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStock_PIDEV", logId,sMethodName, "저장품 조회");
				
				if (getRecSet.size() <= 0) {
					sMsg= "["+sOperationName+"] YD_STOCK[후판제품출하완료] 조회 Error :: [" + sSTL_NO + "," + getRecSet.size() + "]";
					commPiUtils.printLog(logId, sMsg , "SL");	
				}
				
				String ydCarUppLocCd = commPiUtils.nvl(getRecSet.getRecord(0).getFieldString("YD_CAR_UPP_LOC_CD"), "01");
				
				ydGpBay = ydStkColGp.substring(0, 2);
				String ydStkColGpLoc = ydGpBay + "PT01" + ydCarUppLocCd + "001";
				
				commPiUtils.printLog(logId, "ydStkColGp:" + ydStkColGp + ",ydStkBedNo:" + ydStkBedNo + ",ydStkLyrNo:" + ydStkLyrNo + ",ydStkColGpLoc:" + ydStkColGpLoc + ",ydCarUppLocCd:" + ydCarUppLocCd, "SL");

				// 코일 공통 상차백업 위치로 위치 변경 작업
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setField("YD_GP"	     , ydStkColGpLoc.substring(0,1));
				jrParam.setField("YD_BAY_GP"	 , ydStkColGpLoc.substring(1,2));
				jrParam.setField("YD_EQP_GP"	 , ydStkColGpLoc.substring(2,4));
				jrParam.setField("YD_STK_COL_NO" , ydStkColGpLoc.substring(4,6));
				jrParam.setField("YD_STK_BED_NO" , ydStkBedNo);
				jrParam.setField("YD_STK_LYR_NO" , ydStkLyrNo);
				jrParam.setField("YD_STR_LOC"    , ydStkColGp.substring(0,2) + "PT011001"); 
				jrParam.setField("MODIFIER"      , sModifier);  
				jrParam.setField("FNL_REG_PGM"   , "rcvM10LMYDJ1072");
				jrParam.setField("PLATE_NO"      , sSTL_NO);
				
				//여기 PLATECOMM도 실제 하차된것만 대상으로 PT로 업데이트해야함.
				String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI3", "T", "*");
				if(sApplyYnPI.equals("Y")){  //실제 PT로 옮겨진대상만 PLATECOMM에 위치 업데이트	
					intRtnVal = ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtPlatecommLOC2", logId, sMethodName, "해당 재료 적치단 clear");
				}
				else{
					intRtnVal = ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtPlatecommLOC", logId, sMethodName, "해당 재료 적치단 clear");	
				}
				
				
				if(intRtnVal==0){
					sMsg= "["+ sOperationName +"] 후판제품[" + sSTL_NO + "]에 대한 적치단 clear 과정 중 Error 발생";
					commPiUtils.printLog(logId, sMsg , "SL");
				}
				
				/*************************
				 * 작업실적에 저장
				 *************************/
				JPlateYdWrkHistDAO ydWrkHistDao	= new JPlateYdWrkHistDAO();				
				JDTORecord jparam = commPiUtils.getParam(logId, mthdNm, sModifier);
				
				jparam.setField("YD_GP"            , "T");
				jparam.setField("STL_NO"           , sSTL_NO);
				jparam.setField("YD_EQP_ID"        , ydGpBay + "CR"+ "00");
				jparam.setField("YD_CRN_SCH_ID"    , "000000000000000000");
				jparam.setField("YD_SCH_CD"        , "TX9999");
				jparam.setField("YD_WRK_DUTY"      , commPiUtils.getWorkDuty());  
				jparam.setField("YD_WRK_PARTY"     , commPiUtils.getWorkParty()); 
				jparam.setField("YD_UP_WR_LOC"     , ydStkColGp + ydStkBedNo);
				jparam.setField("YD_UP_WR_LAYER"   , ydStkLyrNo        );
				jparam.setField("YD_DN_WR_LOC"     , ydGpBay + "PT0101");
				jparam.setField("YD_DN_WR_LAYER"   , "001"             );
				
				// 이력테이블에 INSERT
				intRtnVal = ydWrkHistDao.insYdWrkHist(jparam);

				if (intRtnVal <= 0) {
					sMsg = "재료번호(" + sSTL_NO + ")에 대한 insYdWrkHist 가 실패하였습니다.";
					commPiUtils.printLog(logId, sMsg , "SL");
				}
				
//				// 코일쪽(YdCoilL3RcvPISeEJBBean.java)에 주석 처리 되어 동일하게 주석 처리 함
//				/******************************************************
//				 * 이송재료 정보 등록 - 백업처리대상이 반품될 경우 대비
//				 ******************************************************/
//				JPlateYdTcarFtmvMtlDAO ydTcarFtmvMtlDao = new JPlateYdTcarFtmvMtlDAO();
//				
//				jrParam = JDTORecordFactory.getInstance().create();
//				jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
//				jrParam.setField("YD_TCAR_SCH_ID", ydCarSchId   );
//				jrParam.setField("STL_NO"       , sSTL_NO       );
//				jrParam.setField("YD_STK_BED_NO", ydCarUppLocCd); //야드 차상위치코드
//				jrParam.setField("YD_STK_LYR_NO", "001"        );
//				jrParam.setField("DEL_YN"       , "Y"          );
//				jrParam.setField("REGISTER"     , sModifier    );
//
//				ydPICommDAO.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.insYdTcarftmvmtlBackup", logId, mthdNm, "이송재료 등록");
			}
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}
		
	
	/**	 
	 * [A] 오퍼레이션명 :후판제품Pallet출발실적(DMYDR042) - procOutCarLevWr
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1082(JDTORecord rcvMsg) throws DAOException { //S
		String mthdNm = "[YdPlateL3RcvPISeEJB.rcvM10LMYDJ1082] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commPiUtils.printLog(logId, mthdNm, "S+");
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "T", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}					
			
		    String sMsg                    = "";
		    String sMethodName      	   = "procOutCarLevWr";
		    String sOperationName		   = "후판제품Pallet출발실적";
		    
			YdCarSchDao ydCarSchDao        = new YdCarSchDao(); 
			YdStkBedDao ydStkBedDao        = new YdStkBedDao();
			YdStkColDao ydStkColDao        = new YdStkColDao();
			YdStkLyrDao ydStkLyrDao        = new YdStkLyrDao();
			YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
			
			JDTORecordSet rsResult         = null;
			JDTORecordSet rsStkCol         = null;
			JDTORecord    recInTemp        = null;
			JDTORecord    recOutTemp       = null;
			JDTORecord    recInPara        = null;
			JDTORecord    recGetVal        = null;

		    // 발지야드포인트코드
		    String sYD_CARLD_LEV_LOC  = "";
		    String sCarSchId          = "";
		    String sSTLNo             = "";
		    String sCMBN_CARLD_YN	  = "";
		    String sCAR_NO_CHK		  = "";
		    String sCHK_YN		  	  = "N";
		    String sTAT 			  = "";
		    
		    int intRtnVal 			  = 0;
		    int nRet                  = 0;
		    
			//수신 항목 값
			String msgId  = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번

			/********************************
			 * 수신 전문 
			 ********************************/
            String sYD_GP            = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"      ));   // 운송지시일자 
            String sTRANS_ORD_DT     = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"      ));   // 운송지시일자 
            String sTRANS_ORD_SEQNO  = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"   ));   // 운송지시순번 
            String sCAR_NO           = commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"            ));   // 차량번호
            String sSPOS_WLOC_CD     = commPiUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"      ));   // 발지개소코드
            String sSPOS_YD_PNT_CD   = commPiUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"    ));   // 발지포인트코드
            
            String sYD_CARPNT_CD     = commPiUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD"      ));   // 출하포인트코드
            String sCallPgm          = commPiUtils.trim(rcvMsg.getFieldString("CALL_PGM"      ));
            String sModifier         = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));             //수정자(Backup Only)
            if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
//            String sYD_CARPNT_CD	= ""; 

	    		    	
	    	if(sTRANS_ORD_DT.equals("")) {
				sMsg="운송지시일자가 없습니다.";
				commPiUtils.printLog(logId, sMsg , "SL");
				throw new DAOException("TRANS_ORD_DT Error");
	    	}
	    	if(sTRANS_ORD_SEQNO.equals("")) {
				sMsg="운송지시순번이 없습니다.";
				commPiUtils.printLog(logId, sMsg , "SL");
				throw new DAOException("TRANS_ORD_SEQNO Error");
	    	}
	    	if(sCAR_NO.equals("")) {
				sMsg="차량번호가 없습니다.";
				commPiUtils.printLog(logId, sMsg , "SL");
				throw new DAOException("CAR_NO Error");
	    	}
	    	if(sYD_CARPNT_CD.equals("")) {
				sMsg="포인트가 없습니다.";
				commPiUtils.printLog(logId, sMsg , "SL");
				throw new DAOException("sYD_CARPNT_CD Error");
	    	}	    	
//	    	//카드번호
//	    	sCARD_NO         = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");
//	    	if(sCARD_NO.equals("")) {
//				sMsg="카드번호가 없습니다.";
//				ydUtils.putLog(sSessionName, sMethodName, sMsg, YdConstant.ERROR);
//				throw new DAOException("CARD_NO Error");
//	    	}
//	    	if(sSPOS_WLOC_CD.equals("")) {
//				sMsg="발지개소코드가 없습니다.";
//				commPiUtils.printLog(logId, sMsg , "SL");
//	    	}
//	    	//발지포인트코드
//	    	if(sSPOS_YD_PNT_CD.equals("")) {
//				sMsg="발지포인트코드가 없습니다.";
//				commPiUtils.printLog(logId, sMsg , "SL");
//			}
	    	/*
    		 * 2015.08.21 윤재광 
    		 * 후판출하 해송 팔레트 Flow기능개선
    		 * - I/F 항목중에 구내운송 개소/포인트 코드는 사용안함. 출하포인트코드로 통일
    		 */
	    	//출하포인트코드
//	    	sYD_CARPNT_CD    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARPNT_CD");
	    	
	    	
	    	
	    	if(!sYD_CARPNT_CD.equals("")) {
	    		//차량포인트 테이블에서 WLOC_CD 와 YD_PNT_CD 를 읽어 온다. 
//	    		YdPlateCommDAO commDao = new YdPlateCommDAO(); //3기 후판제품공용dao

	    		rsResult = JDTORecordFactory.getInstance().createRecordSet("");

	    		recInTemp = JDTORecordFactory.getInstance().create();
	    		recInTemp.setField("YD_CARPNT_CD", 		  sYD_CARPNT_CD);

	    		intRtnVal = ydPICommDAO.selectT(recInTemp, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0102");

	    		if(intRtnVal <= 0){
	    		    sMsg="["+sOperationName+"] PALLET 출하 차량포인트 조회 시 오류발생 - 반환값 : " + intRtnVal;
	    		    commPiUtils.printLog(logId, sMsg , "SL");				
					throw new DAOException("CAR_NO Error");
	    		}

	    		rsResult.first();
	    		recInTemp = rsResult.getRecord();

	    		sSPOS_WLOC_CD   	= commPiUtils.trim(recInTemp.getFieldString("WLOC_CD"  ));
	    		sSPOS_YD_PNT_CD 	= commPiUtils.trim(recInTemp.getFieldString("YD_PNT_CD"));
	    	}
	    	
	    	
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("TRANS_ORD_DT",    sTRANS_ORD_DT);
	    	recInTemp.setField("TRANS_ORD_SEQNO", sTRANS_ORD_SEQNO);
	    	recInTemp.setField("CAR_NO",          sCAR_NO);
//	    	recInTemp.setField("CARD_NO",         sCARD_NO);
	    	recInTemp.setField("SPOS_WLOC_CD",    sSPOS_WLOC_CD);
	    	recInTemp.setField("SPOS_YD_PNT_CD",  sSPOS_YD_PNT_CD);
			
	    	//발지위치정보로 출발위치 Clear
	    	//열정보 Clear 업데이트 후 리턴값이 1이상이면 베드 단정보도 Clear
	    	//업데이트값이 없다면 그냥 종료
	    	//발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)
	    	rsStkCol = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("WLOC_CD",   sSPOS_WLOC_CD);
	    	recInTemp.setField("YD_PNT_CD", sSPOS_YD_PNT_CD);
	    	/* com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolWLocCdandPntCd */
	    	intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsStkCol, 4);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					sMsg="<procOutCarLevWr> getYdStkcol data not found";
					commPiUtils.printLog(logId, sMsg , "SL");
				}else if(intRtnVal == -2) {
					sMsg="<procOutCarLevWr> getYdStkcol parameter error";
					commPiUtils.printLog(logId, sMsg , "SL");
				}
//				return intRtnVal = -1;
				new DAOException(sMsg);
			}
			
	    	//조회된 값이 있을 경우
	    	if(intRtnVal > 0) {
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsStkCol.getRecord(0));
	    	
		    	//열구분을 조회(도착지)
		    	sYD_CARLD_LEV_LOC = commPiUtils.trim(recOutTemp.getFieldString("YD_STK_COL_GP"));
		    	sYD_CARPNT_CD	  = commPiUtils.trim(recOutTemp.getFieldString("YD_CARPNT_CD")); 
		    	sCAR_NO_CHK  	  = commPiUtils.trim(recOutTemp.getFieldString("CAR_NO"));  
		    	
		    	//다른 차량이 존재 하는 경우 
		    	if(!sCAR_NO_CHK.equals(sCAR_NO)){

		    		sCHK_YN ="Y";
		    		
		    		sMsg="<procOutCarLevWr> 해당 위치에 다른 차량이 존재 합니다. 포인트 차량:"+sCAR_NO_CHK + "  취소대상 차량:"+ sCAR_NO;
		    		commPiUtils.printLog(logId, sMsg , "SL");
					//return intRtnVal = -1;
		    	}
		    	
		    	//동일 차량이 존재 하는 경우 차량위치정보를 초기화 한다.
		    	if(sCHK_YN.equals("N")){ 
	
			    	if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(sYD_GP)) {
			    		///////////////////////////////////////////////////////////////////////////
			    		// 후판 인 경우 *******************************************************
			    		///////////////////////////////////////////////////////////////////////////
			    		recInTemp = JDTORecordFactory.getInstance().create();
				    	recInTemp.setField("YD_STK_COL_GP", sYD_CARLD_LEV_LOC);
				    	recInTemp.setField("YD_STK_COL_ACT_STAT",  "C");
				    	recInTemp.setField("YD_CAR_USE_GP",        "");
				    	recInTemp.setField("TRN_EQP_CD",           "");
				    	recInTemp.setField("CAR_NO",               "");
				    	recInTemp.setField("CARD_NO",              "");
						//-----------------------------------------------------------------------------------
						//-----------------------------------------------------------------------------------
						// transaction 을 분리하여 적치열의 할성상태를 'C'로 업데이트 한다. - 2013.06.24 
						/*
						 * 2014.08.11 윤재광
						 * 사용불가로 되어있으면 상태값을 바꾸지 않는다
						 */
						if(!"N".equals(commPiUtils.trim(recOutTemp.getFieldString("YD_STK_COL_ACT_STAT")))){
							
							recInTemp.setField("QUERY_ID", "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0019");
				    		
				    		EJBConnector 	ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);			
						    ejbConn.trx("updYdStkColTX", new Class[] { JDTORecord.class }, new Object[] { recInTemp });	
						    
						    //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
//						    CarPointinforeg2("B","","",sYD_CARLD_LEV_LOC,"","","C");
						    
					        EJBConnector ejbConn2 = new EJBConnector("default","CarMvHdSeEJB",this);
							ejbConn2.trx("CarPointinforeg2", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
						  	             new Object[]{"B","","",sYD_CARLD_LEV_LOC,"","","C"});

						    
						    
						    /*
						     * 2014.07.29 윤재광
						     * 2번 포인트 출발처리시 트레일러일 경우 3번 포인트도 같이 닫는다.
						     */
						    if("2".equals(sYD_CARLD_LEV_LOC.substring(5))){
						    	
						    	JDTORecordSet rsJK 	= JDTORecordFactory.getInstance().createRecordSet("");
								JDTORecord rdJK 	= JDTORecordFactory.getInstance().create();
								
								rdJK.setField("YD_STK_COL_GP", sYD_CARLD_LEV_LOC.substring(0, 5)+"3");
								
								intRtnVal = ydStkColDao.getYdStkcol(rdJK, rsJK, 0);
								
								if( intRtnVal > 0 ) {
									
									rsJK.first();
									rdJK = rsJK.getRecord();
									
									if("L".equals(StringHelper.evl(rdJK.getFieldString("YD_STK_COL_ACT_STAT"), ""))){
										
										recInTemp = JDTORecordFactory.getInstance().create();
								    	recInTemp.setField("QUERY_ID", "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0018");
								    	recInTemp.setField("YD_STK_COL_GP", sYD_CARLD_LEV_LOC.substring(0, 5)+"3");
								    	recInTemp.setField("YD_STK_COL_ACT_STAT", "C");
								    		
							    		ejbConn.trx("updYdStkColTX", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
							    		
							    		//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
									    //CarPointinforeg2("B","","",sYD_CARLD_LEV_LOC.substring(0, 5)+"3","","","C");
							    		EJBConnector ejbConn3 = new EJBConnector("default","CarMvHdSeEJB",this);
										ejbConn3.trx("CarPointinforeg2", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
										  	             new Object[]{"B","","",sYD_CARLD_LEV_LOC.substring(0, 5)+"3","","","C"});
									    
									}
								}	
						    }	
						}
			    	}
					//-----------------------------------------------------------------------------------
					//-----------------------------------------------------------------------------------		    	
					
					//적치베드 상태 비활성화등록
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_STK_BED_WT_MAX", YdConstant.YD_STK_BED_WT_MAX_DEFAULT);
					recInTemp.setField("YD_STK_COL_GP", sYD_CARLD_LEV_LOC);
					recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
					
					intRtnVal = ydStkBedDao.updYdStkbedYdStkColGp(recInTemp, 0);
					if(intRtnVal <= 0) {
						sMsg = "<procOutCarLevWr> 차량 적치베드 정보 비활성화중 Error!! ";
						commPiUtils.printLog(logId, sMsg , "SL");
						throw new DAOException("<procOutCarLevWr> updYdStkbedYdStkColGp" + sMsg);
					}
					
					//적치단 비활성화
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_STK_COL_GP", sYD_CARLD_LEV_LOC);
					recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
					recInTemp.setField("STL_NO", "");
					recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
			    	
					intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGp(recInTemp);
					if(intRtnVal <= 0) {
						sMsg = "<procOutCarLevWr> 차량 적치단 정보 비활성화중 Error!! ";
						commPiUtils.printLog(logId, sMsg , "SL");
						throw new DAOException("<procOutCarLevWr> updYdStklyrYdStkColGp " + sMsg);
					}
					
					/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		    		 * 업무기준 : 출하 영차 출발 시 저장위치 제원 야드L2로 전송
		    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_INFO_SYNC_CD", "3");							//1:동,2:SPAN,3:열,4:BED
					recInTemp.setField("YD_GP", sYD_CARLD_LEV_LOC.substring(0, 1));
					recInTemp.setField("YD_STK_COL_GP", sYD_CARLD_LEV_LOC);
					recInTemp.setField("YD_CAR_PROG_STAT", "A");						//영차출발
					recInTemp.setField("YD_EQP_WRK_STAT", "L");
//					150925 hun 출발차량 번호 전송
					recInTemp.setField("CAR_NO", sCAR_NO);
					
					YdCommonUtils.sndStrPosSpecToL2(recInTemp);
					sMsg="<procOutCarLevWr>  출하 영차 출발 시 저장위치 제원 야드L2로 전송";
					commPiUtils.printLog(logId, sMsg , "SL");
					/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
		    	}
		    	
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// 권오창 20090820
				// 차량스케쥴 클리어  - DEL_YN
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				String sTmpCarNo	= "";
				if(sCAR_NO.startsWith("ET")){
					sTmpCarNo = "ET";
				}else{
					sTmpCarNo = sCAR_NO;
				}
				
				recInTemp = JDTORecordFactory.getInstance().create();
				
				recInTemp.setField("CAR_NO" , sTmpCarNo);
//				recInTemp.setField("CARD_NO", sCARD_NO);
				recInTemp.setField("TRANS_ORD_DT",    sTRANS_ORD_DT);
		    	recInTemp.setField("TRANS_ORD_SEQNO", sTRANS_ORD_SEQNO);

//PIDEV    	    /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschTransDTSeq2_PIDEV*/
//				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//				intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 413);	
//				if(intRtnVal <= 0) {
//					sMsg = "차량스케쥴 조회 오류 + (" + sCAR_NO + ", " + sCARD_NO + ", 'G')";
//					commPiUtils.printLog(logId, sMsg , "SL");
//					return intRtnVal = 1;
//				}		
		    	
		    	rsResult	= ydPICommDAO.selectT(recInTemp, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschTransDTSeq2_PIDEV", logId, sMethodName, "제품 9%니켈강여부 조회");
				if (rsResult.size() <= 0){
					sMsg = "["+sOperationName+"] 차량번호[" + sCAR_NO + "]로 차량스케줄 조회 시 에러발생 - 반환값 : " + intRtnVal;
					commPiUtils.printLog(logId, sMsg , "SL");
//					return intRtnVal = 1;
					throw new DAOException(sMsg);
				}
				sMsg= "["+ sOperationName +"] 차량번호[" + sCAR_NO + "]로 차량스케줄 조회 완료 - 반환값 : " + intRtnVal;
				commPiUtils.printLog(logId, sMsg , "SL");				
				
				rsResult.first();
				recGetVal = rsResult.getRecord();
				sCarSchId      = commPiUtils.trim(recGetVal.getFieldString("YD_CAR_SCH_ID"));
				sCMBN_CARLD_YN = commPiUtils.trim(recGetVal.getFieldString("CMBN_CARLD_YN"));
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID" , sCarSchId);
				recInTemp.setField("DEL_YN" , "Y");
				nRet = ydCarSchDao.updYdCarsch(recInTemp, 0);
				if(nRet <= 0 ){
					sMsg = "차량 스케쥴 업데이트 갱신 오류 + (" + sCarSchId + ")";
					commPiUtils.printLog(logId, sMsg , "SL");
					throw new DAOException("<procOutCarLevWr> updYdCarsch " + sMsg);
				}
				
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// 저장품 클리어             - 운송지시일자, 운송지시순번, 차량번호, 카드번호, 야드적치Bed번호,적치단  클리어
				// 차량 이송재료 클리어  - DEL_YN
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID" , sCarSchId);
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				//intRtnVal = ydStockDao.getYdStock(recInTemp, rsResult, 118);
				intRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recInTemp, rsResult, 4);
				if(intRtnVal <= 0) {
					sMsg = "차량재료정보가 존재 안합니다. + (" + sTRANS_ORD_DT + ", " + sTRANS_ORD_SEQNO + ", " + sCAR_NO + ", " +  ")";
					commPiUtils.printLog(logId, sMsg , "SL");
					//14053 이슈
					//throw new DAOException("<procOutCarLevWr> getYdStock " + sMsg);
				}				
 
				for(int nIdx=0; nIdx<intRtnVal; nIdx++){
					recGetVal = rsResult.getRecord(nIdx);

					sSTLNo = commPiUtils.trim(recGetVal.getFieldString("STL_NO"));
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO"        , sSTLNo);
					recInTemp.setField("YD_CAR_SCH_ID" , sCarSchId);
					recInTemp.setField("DEL_YN"        , "Y");
					nRet = ydCarFtmvMtlDao.updYdCarftmvmtl(recInTemp, 0);
					if(nRet <= 0 ){
						sMsg = "차량 이송재료 업데이트 갱신 오류 + (" + sSTLNo + ", " + sCarSchId + ")";
						commPiUtils.printLog(logId, sMsg , "SL");
						throw new DAOException("<procOutCarLevWr> updYdCarftmvmtl" + sMsg);
					}
					//YD_STOCK도 클리어.(CAR_LOTID)  2023.06.16 임진후 기사요청. 파렛트차량도 상차완료시 LOTID 클리어. REQ202306467829
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("STL_NO"        , sSTLNo);
						recInTemp.setField("CAR_LOTID"        , "");
						recInTemp.setField("DEL_YN"        , "N");
						recInTemp.setField("MODIFIER"        , sModifier);
						nRet = ydCarFtmvMtlDao.updYdCarftmvmtl(recInTemp, 13);  //14번 신규 쿼리 생성 필요.
					//}
				}
				
				if(sCHK_YN.equals("N")){
					/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			         * 			구내운송 소재차량Point개폐 전송  - YDTSJ012
			         * 업무기준 Desc : 1. 외판슬라브 출하차량 도착 실적처리 후 구내운송에소재차량Point개폐 전송 전송
			         * 				  2. 출하관리와 구내운송간의 차량point 정보공유를 위해서, 구내운송에서 출하차량이 도착한 point를 사용하지
			         * 					않도록 하기 위해서.
			         * 				  3. +++++++++++ 출발 시는 포인트가 열렸다고 전송 +++++++++++++
			         * 기능 추가 : 임춘수
			         * 일자 : 2009.06.25
			         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("MSG_ID"         , "YDTSJ012");
					recInPara.setField("YD_STK_COL_GP"  , sYD_CARLD_LEV_LOC);			//적치열구분
					recInPara.setField("PNT_UNIT_CL_GP" , YdConstant.PNT_UNIT_CL_GP_OPEN);									//포인트개폐구분
					ydDelegate.sendMsg(recInPara);
					sMsg = "[후판 출하차량출발]구내운송 소재차량Point개폐 전송 완료 - PNT_UNIT_CL_GP : " + YdConstant.PNT_UNIT_CL_GP_OPEN;
					commPiUtils.printLog(logId, sMsg , "SL");
					/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
					
					/*
					 * 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
					 */
					sMsg="[" + sOperationName + "] 차량정지위치[" + sYD_CARLD_LEV_LOC + "] - 차량입동지시요구 모듈을 호출 시작";
					commPiUtils.printLog(logId, sMsg , "SL");
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("TC_CODE"				,"YDYDJ633");
					recInTemp.setField("TC_CREATE_DDTT"			,commPiUtils.getCurDate("yyyyMMddHHmmss"));
					recInTemp.setField("YD_CAR_STOP_LOC"		,sYD_CARLD_LEV_LOC);
					
		    		//입동요구 방식
//		    		ydUtils.putLog(sSessionName, sMethodName, " sYD_CARPNT_CD:"+sYD_CARPNT_CD+", 복수창고구분:"+sCMBN_CARLD_YN, YdConstant.INFO);
		    		
		    		commPiUtils.printLog(logId,  "sYD_CARPNT_CD:"+sYD_CARPNT_CD+" 복수창고구분:"+sCMBN_CARLD_YN , "SL");
		    		if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(sYD_GP)) {
		    			
						// 2021. 06. 24 차량선별입동지시시점[T00171] "D"일경우 입동지시 전문이 날아가지 않는 문제 수정
		    			// 이 메서드 호출시 "CALL_PGM"의 값이 있는 것에 한해 "SANGCHA" 파라메터를 첨부한다.
		    			//String sCallPgm = ydDaoUtils.paraRecChkNull(msgRecord, "CALL_PGM");
		    			
		    			//250711 허정욱 수정 - 팔렛트 상차완료 후 입동지시요구 모듈 호출 시 "SANGCHA" 값이 없어, 다음차에 대한 입동지시 송신이 안됨.
		    			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APP060", "T", "018");
		    			if("Y".equals(sApplyYnPI)){
		    				sCallPgm = "SANGCHA";
		    			}
		    			
		    			if( !"".equals(sCallPgm) ){
		    				recInTemp.setField("CALL_PGM", sCallPgm); // 입동지시 전문을 보내기 위함
		    			}
		    			
		    			// 1,2 후판 창고일 경우 기존 입동요구 방식
//PIDEV    				procCarBayInOrdReq(recInTemp);
//PIDEV 반드시 확인			    			
		    			EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
						ejbConn.trx("procCarBayInOrdReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
		    		} 
		    	
					sMsg="[" + sOperationName + "] 차량정지위치[" + sYD_CARLD_LEV_LOC + "] - 차량입동지시요구 모듈을 호출 성공";
					commPiUtils.printLog(logId, sMsg , "SL");
		    	}//end of if
	    	}		
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}	
	
	
	
	/**	 
	 * [A] 오퍼레이션명 :후판제품긴급/보류재 처리실적(DMYDR044)-procPlGdsDestChgInfo 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1122(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "[YdPlateL3RcvPISeEJB.rcvM10LMYDJ1122] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			//수신 항목 값
			String msgId  = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID

//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "T", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}					
			
			YdStockDao ydStockDao	= new YdStockDao();
			
			/********************************
			 * 수신 전문 
			 ********************************/
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", 	       commPiUtils.trim(rcvMsg.getFieldString("STL_NO"))); 		
			recPara.setField("URGENT_DIST_YN", commPiUtils.trim(rcvMsg.getFieldString("URGENT_DIST_YN")));
			recPara.setField("URGENT_DIST_GP", commPiUtils.trim(rcvMsg.getFieldString("URGENT_DIST_GP")));

			/*
			 * 1. 야드 저장품 긴급재/보류재 변경.
			 */			
			ydStockDao.update_Dm_DestCd(recPara, 1);
			
			//if(sApplyYnPI.equals("Y")){  //배포 flag 적용시
				//Y8,Y9L002 전송. 긴급재/보류재 변경시 동기화위해.
				//======================================================
				// 저장품제원 : 후판제품 L2 로 송신(YDY8L002)
				//======================================================
				JDTORecord recResult = null;
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고 제원정보
				recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
				recResult.setField("STL_NO"         , rcvMsg.getFieldString("STL_NO"));
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				
				if( PlateGdsYdUtil.isSendToEaiY9_stlNo(rcvMsg.getFieldString("STL_NO")) ){  //자동화(1후판 B동) 대상인지 확인
					recResult.setField("MSG_ID"         , "YDY9L002");
				}
				ydDelegate.sendMsg(recResult);
			//}

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}	

	/**	 
	 * [A] 오퍼레이션명 :후판제품선별LOT편성정보(DMYDR046)- procPlateGdsTrnOrdLot
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1132(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "[YdPlateL3RcvPISeEJB.rcvM10LMYDJ1132] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "T", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}					
			
			YdStkBedDao ydStkBedDao     = new YdStkBedDao();
			//JDTO 정의
			JDTORecord	recEditColumn	= JDTORecordFactory.getInstance().create();
			JDTORecord  recInTemp		= null;
			//기본 변수 정의
			String sMethodName 		    = "procPlateGdsTrnOrdLot";
			String sOperationName		= "재광제품선별LOT편성정보(DMYDR046)";
			String sMsg 				= "";
			String sSTL_NO 			    = "";
			int intRtnVal 				= 0;
			int	i						= 0;
		    
			//수신 항목 값
			String msgId  = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			/********************************
			 * 수신 전문 
			 ********************************/
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ" )) );       // 운송의뢰순번

            String sYD_GP            = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"               )); 
            String sTRANS_ORD_DATE   = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"        ));   // 운송지시일자 
            String sTRANS_ORD_SEQNO  = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"     ));   // 운송지시순번
            String sCarLotId         = commPiUtils.trim(rcvMsg.getFieldString("LOT_NO"              ));
            String sCarLotGbn        = commPiUtils.trim(rcvMsg.getFieldString("LOT_GBN"             ));
            int intYD_EQP_WRK_SH     = Integer.parseInt(commPiUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수
            String sModifier         = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));             //수정자(Backup Only)
            if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }

			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			sMsg = "[출하] 후판제품선별LOT편성정보 수신";
			commPiUtils.printLog(logId, sMsg , "SL");
			
			//****************************************************************************************************
			if("".equals(sCarLotId)){
				sMsg="[" + sOperationName + "] LOT_NO IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");
				return ;
			}
			if("".equals(sCarLotGbn)){
				sMsg="[" + sOperationName + "] LOT_GBN IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");
				return ;
			} 
			
			recEditColumn 	= JDTORecordFactory.getInstance().create();
			recEditColumn.setField("TRANS_ORD_DATE", 		sTRANS_ORD_DATE);
			recEditColumn.setField("TRANS_ORD_SEQNO", 		sTRANS_ORD_SEQNO);
			recEditColumn.setField("CAR_LOTID", 			sCarLotId);
			recEditColumn.setField("MODIFIER", 				sModifier);  
			
			sMsg="[" + sOperationName + "]["+intYD_EQP_WRK_SH+"]매수에 대한  저장품 업데이트 시작";
			commPiUtils.printLog(logId, sMsg , "SL");
			
			for(i = 1 ; i<= intYD_EQP_WRK_SH; i++){
				
				sSTL_NO = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"+i));
				
				if(sSTL_NO.equals("")){
					break;
				}else {
					rcvMsg.setField("STL_NO", sSTL_NO);
				}
				
				recEditColumn.setField("STL_NO",sSTL_NO);
				
				if("1".equals(sCarLotGbn)){
					recEditColumn.setField("CAR_LOTID", sCarLotId);
				}else if("2".equals(sCarLotGbn)){
					recEditColumn.setField("CAR_LOTID", "");
				}
				
				
		        // 2021. 06. 03
				// Update컬럼을 최소화한다.
				recEditColumn.setField("QUERY_ID", "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updYdStockByDMYDR060");

				// 2021. 06. 03 트랜잭션 분리를 위한 EJb Bean설정
				EJBConnector 	ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
				ejbConn.trx("updYdStkColTX", new Class[] { JDTORecord.class }, new Object[] { recEditColumn });	
				
				sMsg="[" + sOperationName + "] ["+i+"] 재료["+ sSTL_NO +"]에 TB_YD_STOCK 업데이트 완료 : ";
				commPiUtils.printLog(logId, sMsg , "SL");
				
				// 2021. 06. 03
				// 출하에서 야드에->Lot편성, 진행관리->운송대기 두 개 전문을 동시 전송 후 처리하는 과정에서 
				// TB_PT_PLATECOMM 테이블의 진도코드가 야드L2전송 전송 이후에 바뀌는 문제가 있어 하드코딩으로 처리한다.
				// 취소일경우엔 진도코드를
				recInTemp  = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID"         , "YDY8L002");
				recInTemp.setField("YD_INFO_SYNC_CD", "A");							
		    	recInTemp.setField("STL_NO"         , sSTL_NO);
		    	recInTemp.setField("YD_STK_COL_GP"  , YdConstant.YD_GP_PLATE2_GDS_YARD);
				recInTemp.setField("CURR_PROG_CD"   , YdConstant.PROG_CD_TRN_WAIT); // 운송대기(L)로 강제로 셋팅한다.
				if("2".equals(sCarLotGbn)){
					/*
					 * 선별LOT편성정보 취소
					 *
					 * 출하에서  선별Lot편성취소  정보가 수신되면  해당 저장위치 상태변경
					 *  야드베드입출고상태 : 
				 	 *  선별상태 : 
					 */
					intRtnVal = ydStkBedDao.updYdStkBedStat_02(recEditColumn);
					recInTemp.setField("CURR_PROG_CD"   , YdConstant.PROG_CD_TRN_WO_WAIT); // 운송지시대기(N)로 강제로 셋팅한다.
				}
		    	ydDelegate.sendMsg(recInTemp);
			    	
		    	// 전사물류개선 2021. 4. 3 생산실적 발생시 동일하게 Y9도 전송처리한다.
		    	recInTemp.setField("MSG_ID"         , "YDY9L002");
		    	ydDelegate.sendMsg(recInTemp);
		    	
			} 
			
			if("1".equals(sCarLotGbn)){
				/*
				 * 수동선별LOT편성
				 * 출하에서 수동 선별Lot편성 정보가 있으면 해당 저장위치 상태변경
				 *  야드베드입출고상태 : 일반베드이면  완산베드로 변경,
			 	 *  선별상태 : 출하송신
				 */
				intRtnVal = ydStkBedDao.updYdStkBedStat_01(recEditColumn);
				
			}
			
			// 전사물류개선 2021. 4. 3
			// 지시확정이 변경된 BED정보를 전송한다.
			JDTORecord params = JDTORecordFactory.getInstance().create();
			JDTORecordSet rsBedRecord 	= JDTORecordFactory.getInstance().createRecordSet("ydPlate");
			params.setField("TRANS_ORD_DATE"           	,sTRANS_ORD_DATE);
			params.setField("TRANS_ORD_SEQNO"         	,sTRANS_ORD_SEQNO);
			
			if(ydPICommDAO.selectT(params, rsBedRecord, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getDMYDR060_SendToL2List") > 0){
				for(int idx=0; idx<rsBedRecord.size();idx++){
					JDTORecord jtoYDY89L001 = JDTORecordFactory.getInstance().create();
					jtoYDY89L001.setField("YD_INFO_SYNC_CD", "4"); // BED까지(4)
					jtoYDY89L001.setField("YD_GP"          , YdConstant.YD_GP_PLATE2_GDS_YARD);
					jtoYDY89L001.setField("YD_STK_COL_GP"  , rsBedRecord.getRecord(idx).getFieldString("YD_STK_COL_GP"));
					jtoYDY89L001.setField("YD_STK_BED_NO"  , rsBedRecord.getRecord(idx).getFieldString("YD_STK_BED_NO"));
					YdCommonUtils.sndStrPosSpecToL2(jtoYDY89L001);
				}
			}
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}		
	
	
	/**	 
	 * [A] 오퍼레이션명 :해송Pallet선별LOT편성(DMYDR048)-procPlateGdsShptrTrnOrdLot
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1142(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "[YdPlateL3RcvPISeEJB.rcvM10LMYDJ1142] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "T", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}					
			
			//수신 항목 값
			String msgId  = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			YdStkBedDao ydStkBedDao = new YdStkBedDao();								//적치베드DAO
			
			//JDTO 정의
			//JDTORecord	recEditColumn	= null;
			//기본 변수 정의
			String sMethodName 		= "procPlateGdsShptrTrnOrdLot";
			String sOperationName		= "후판제품해송선별LOT편성정보(DMYDR048)";
			String sMsg 				= "";
			//로컬에서 사용되는 변수 정의
			String sSTL_NO 			= "";
//			String sCarLotId			= null; //선별LOT번호
//			String sCarLotGbn			= null; //작업구분 1:수동선별LOT편성, 2:수동선별LOT편성취소
//			String sTRANS_ORD_DATE		= null; //운송지시일자
//			String sTRANS_ORD_SEQNO	= null; //운송지시순번
//			String sCARLD_PNT_CD		= null; //상차포인트 (실제 차량포인트 - TB_YD_CARPONT 의 YD_CARPNT_CD 값)
//			String sCAR_KIND			= null; //차량종류 
			String sYD_STK_COL_GP		= null; //야드적치열구분
//			String sCAR_NO				= null; //차량번호
//			String sCARD_NO			= null; //CARD_NO
			
//			int intYD_EQP_WRK_SH		= 0;
			int intRtnVal 				= 0;
			int	i						= 0;
			
			JDTORecordSet	rsResult		= null;
			JDTORecord		recInTemp		= null;
			JDTORecord      sendMsg_L002 = null;
			JDTORecord      recPara2  = JDTORecordFactory.getInstance().create();
			
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			sMsg = "[출하] 후판제품해송선별LOT편성정보 수신";
			commPiUtils.printLog(logId, sMsg , "SL");
			
			//****************************************************************************************************
//			sTRANS_ORD_DATE 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT");
//			sTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO");
//			intYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNullInt(inRecord,"YD_EQP_WRK_SH");
//			sCarLotId 			= ydDaoUtils.paraRecChkNull(inRecord,"LOT_NO");
//			sCarLotGbn			= ydDaoUtils.paraRecChkNull(inRecord,"LOT_GBN");
//			sCARLD_PNT_CD		= ydDaoUtils.paraRecChkNull(inRecord,"CARLD_PNT_CD");
//			sCAR_KIND			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_KIND");
//			sCAR_NO			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO");
//			sCARD_NO			= ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO");
			
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
			String sTRANS_ORD_DATE   = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"      ));   // 운송지시일자 
			String sTRANS_ORD_SEQNO  = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"   ));   // 운송지시순번 
			String sCarLotId         = commPiUtils.trim(rcvMsg.getFieldString("LOT_NO"            ));   //선별LOT번호
	        String sCarLotGbn        = commPiUtils.trim(rcvMsg.getFieldString("LOT_GBN"           ));   //작업구분 1:수동선별LOT편성, 2:수동선별LOT편성취소
	        String sCARLD_PNT_CD	 = commPiUtils.trim(rcvMsg.getFieldString("CARLD_PNT_CD"      ));	//상차포인트 (실제 차량포인트 - TB_YD_CARPONT 의 YD_CARPNT_CD 값)
	        String sCAR_KIND         = commPiUtils.trim(rcvMsg.getFieldString("CAR_KIND"          ));   // 차량종류
	        String sCAR_NO           = commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"            ));   // 차량번호
	        int intYD_EQP_WRK_SH     = Integer.parseInt(commPiUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수   
	        

			if("".equals(sCarLotId)){
				sMsg="[" + sOperationName + "] LOT_NO IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");
				return ;
			}

			if("".equals(sCarLotGbn)){
				sMsg="[" + sOperationName + "] LOT_GBN IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");
				return ;
			} 
			
			if("".equals(sCARLD_PNT_CD)){
				sMsg="[" + sOperationName + "] CARLD_PNT_CD IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");
				return ;
			} 			
			
			if("".equals(sCAR_KIND)){
				sMsg="[" + sOperationName + "] CAR_KIND IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");
				return ;
			} 					
			
			recPara2.setField("TRANS_ORD_DATE", 		sTRANS_ORD_DATE);
			recPara2.setField("TRANS_ORD_SEQNO", 		sTRANS_ORD_SEQNO);
			recPara2.setField("CAR_LOTID", 			sCarLotId);
			recPara2.setField("CAR_NO", 				sCAR_NO);
//			recPara2.setField("CARD_NO", 				sCARD_NO);
			recPara2.setField("MODIFIER", 				"DMYDR048");  
			
			
			sMsg="[" + sOperationName + "]["+intYD_EQP_WRK_SH+"]매수에 대한  저장품 업데이트 시작";
			commPiUtils.printLog(logId, sMsg , "SL");
			
			for(i = 1 ; i<= intYD_EQP_WRK_SH; i++){
				
				sSTL_NO =  commPiUtils.trim(rcvMsg.getFieldString("STL_NO"+i)); 
				
				if(sSTL_NO.equals("")){
					break;
				}
				
				recPara2.setField("STL_NO", 				sSTL_NO);
				
				if("1".equals(sCarLotGbn)){
					recPara2.setField("CAR_LOTID", sCarLotId);
				}else if("2".equals(sCarLotGbn)){
					recPara2.setField("CAR_LOTID", "");
				}
				
		        // 2021. 07. 06
				recPara2.setField("QUERY_ID", "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0049");

				// 2021. 07. 06 트랜잭션 분리를 위한 EJb Bean설정
				EJBConnector 	ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
				ejbConn.trx("updYdStkColTX", new Class[] { JDTORecord.class }, new Object[] { recPara2 });	
				sMsg="[" + sOperationName + "] ["+i+"] 재료["+ sSTL_NO +"]에 TB_YD_STOCK 업데이트 완료 : ";
				commPiUtils.printLog(logId, sMsg , "SL");
				
				
				// 2021. 07. 06
				// 출하에서 야드에->Lot편성, 진행관리->운송대기 두 개 전문을 동시 전송 후 처리하는 과정에서 
				// TB_PT_PLATECOMM 테이블의 진도코드가 야드L2전송 전송 이후에 바뀌는 문제가 있어 하드코딩으로 처리한다.
				// 취소일경우엔 진도코드를
				sendMsg_L002  = JDTORecordFactory.getInstance().create();
				sendMsg_L002.setField("MSG_ID"         , "YDY8L002");
				sendMsg_L002.setField("YD_INFO_SYNC_CD", "A");							
				sendMsg_L002.setField("STL_NO"         , sSTL_NO);
				sendMsg_L002.setField("YD_STK_COL_GP"  , YdConstant.YD_GP_PLATE2_GDS_YARD);
				sendMsg_L002.setField("CURR_PROG_CD"   , YdConstant.PROG_CD_TRN_WAIT); // 운송대기(L)로 강제로 셋팅한다.

				
				if("2".equals(sCarLotGbn)){
					/*
					 * 선별LOT편성정보 취소
					 *
					 * 출하에서  선별Lot편성취소  정보가 수신되면  해당 저장위치 상태변경
					 *  야드베드입출고상태 : 
				 	 *  선별상태 : 
					 */
					intRtnVal = ydStkBedDao.updYdStkBedStat_02(recPara2);
					sendMsg_L002.setField("CURR_PROG_CD"   , YdConstant.PROG_CD_TRN_WO_WAIT); // 운송지시대기(N)로 강제로 셋팅한다.
				}
				
		    	ydDelegate.sendMsg(sendMsg_L002);
//			    	
//		    	// 전사물류개선 2021. 4. 3 생산실적 발생시 동일하게 Y9도 전송처리한다.
		    	sendMsg_L002.setField("MSG_ID"         , "YDY9L002");
		    	ydDelegate.sendMsg(sendMsg_L002);
			} 
			

			if("1".equals(sCarLotGbn)){
				intRtnVal = ydStkBedDao.updYdStkBedStat_01(recPara2);				
			}

 			//-------------------------------------------------------------------------------------------------------------------
			sYD_STK_COL_GP = sCARLD_PNT_CD.substring(0,1) + sCARLD_PNT_CD.substring(2,3) + "PT" + sCARLD_PNT_CD.substring(1,2) + sCARLD_PNT_CD.substring(3,4); 
			
			if("1".equals(sCarLotGbn)){
				//포인트 예약 기능 막음. 1042 시점(지시요구)에 포인트 예약. 1142 시점이 운송지시 편성 시점으로 옮겨지면서 이때 예약잡으면 너무 일직 포인트 예약잡힘.예약은 1042때 잡힘.
				String sApplyYnPI = "Y";
				if(sApplyYnPI.equals("Y")){//배포 FLAG 확인
					//포인트 예약 안함.
				}
				else{
					//차량포인트 "예정중"으로 변경하기
					if(!"PT".equals(sCAR_KIND)&&sCARLD_PNT_CD.endsWith("2") ) {
						//차량종류가 PT가 아니고 차량포인트가 2번포인트인경우는 3번 포인트까지 예약을 건다.
						
						recInTemp = JDTORecordFactory.getInstance().create();
						//그외는 해당 포인트만 예약을 건다.
				    	recInTemp.setField("YD_CARPNT_CD", sCARLD_PNT_CD);
				    	recInTemp.setField("YD_STK_COL_ACT_STAT", "R");					
						
						intRtnVal = ydPICommDAO.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
						
				    	recInTemp.setField("YD_CARPNT_CD", sCARLD_PNT_CD.substring(0,3)+"3");
				    	recInTemp.setField("YD_STK_COL_ACT_STAT", "N");					
						
						intRtnVal = ydPICommDAO.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
						
						
					} else {
						
						recInTemp = JDTORecordFactory.getInstance().create();
						//그외는 해당 포인트만 예약을 건다.
				    	recInTemp.setField("YD_CARPNT_CD", sCARLD_PNT_CD);
				    	recInTemp.setField("YD_STK_COL_ACT_STAT", "R");					
						
						intRtnVal = ydPICommDAO.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
						
					}
				}
				
			} else if("2".equals(sCarLotGbn)) {
				//취소일경우
				
				String sApplyYnPI = "Y";
				if(sApplyYnPI.equals("Y")){//배포 FLAG 확인  //파렛트 선별 취소시 포인트 클리어 안함.(애초에 선별 등록시 예약등록을 안하므로. 상차취소시 포인트 클리어함.)
					
				}
				else{
					if(!"PT".equals(sCAR_KIND)&&sCARLD_PNT_CD.endsWith("2") ) {
						//차량종류가 PT가 아니고 차량포인트가 2번포인트인경우는 3번 포인트까지 예약을 건다.
						
						recInTemp = JDTORecordFactory.getInstance().create();
						//그외는 해당 포인트만 예약을 건다.
				    	recInTemp.setField("YD_CARPNT_CD", sCARLD_PNT_CD);
				    	recInTemp.setField("YD_STK_COL_ACT_STAT", "C");					
						
						intRtnVal = ydPICommDAO.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
						
				    	recInTemp.setField("YD_CARPNT_CD", sCARLD_PNT_CD.substring(0,3)+"3");
				    	recInTemp.setField("YD_STK_COL_ACT_STAT", "C");					
						
						intRtnVal = ydPICommDAO.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
						
						
					} else {
						
						recInTemp = JDTORecordFactory.getInstance().create();
						//그외는 해당 포인트만 예약을 건다.
				    	recInTemp.setField("YD_CARPNT_CD", sCARLD_PNT_CD);
				    	recInTemp.setField("YD_STK_COL_ACT_STAT", "C");					
						
						intRtnVal = ydPICommDAO.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
						
					}	
				}
			}
			//-------------------------------------------------------------------------------------------------------------------			
			// 전사물류개선 2021. 4. 3
			// 지시확정이 변경된 BED정보를 전송한다.
			JDTORecord params = JDTORecordFactory.getInstance().create();
			JDTORecordSet rsBedRecord 	= JDTORecordFactory.getInstance().createRecordSet("ydPlate");
			params.setField("TRANS_ORD_DATE"           	,sTRANS_ORD_DATE);
			params.setField("TRANS_ORD_SEQNO"         	,sTRANS_ORD_SEQNO);
			if(ydPICommDAO.selectT(params, rsBedRecord, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getDMYDR060_SendToL2List") > 0){
				for(int idx=0; idx<rsBedRecord.size();idx++){
					JDTORecord jtoYDY89L001 = JDTORecordFactory.getInstance().create();
					jtoYDY89L001.setField("YD_INFO_SYNC_CD",  "4"); // BED까지(4)
					jtoYDY89L001.setField("YD_GP"          , YdConstant.YD_GP_PLATE2_GDS_YARD);
					jtoYDY89L001.setField("YD_STK_COL_GP", 	rsBedRecord.getRecord(idx).getFieldString("YD_STK_COL_GP"));
					jtoYDY89L001.setField("YD_STK_BED_NO", 	rsBedRecord.getRecord(idx).getFieldString("YD_STK_BED_NO"));
					YdCommonUtils.sndStrPosSpecToL2(jtoYDY89L001);
				}
			}
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}	
	
	/**	 
	 * [A] 오퍼레이션명 :후판제품반송확정(DMYDR003)-procPlGdsHoldCommt
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1152(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "[YdPlateL3RcvPISeEJB.rcvM10LMYDJ1152] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			
			
			//수신 항목 값
			String msgId  = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			YdStockDao ydStockDao 		= new YdStockDao();

			// 레코드 선언
			JDTORecord recStockColumn 	= null;
			JDTORecord recResult        = null;
			
			JDTORecordSet rsResult         = null;
			JDTORecord    recInTemp        = null;
			JDTORecord    recGetVal        = null;
			
			// 변수 선언
			String sMethodName 			= "procPlGdsHoldCommt";
			String sMsg 				= "";
			String sOperationName       = "후판제품반송확정";
			String sORD_NO              = "";
			String sORD_DTL             = "";
			String sMK_MOD_RSN          = "";
			int intRtnVal				= 0;
			int nRet                    = 0;
			
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			sMsg = "[출하] 후판제품보류확정 수신";
			commPiUtils.printLog(logId, sMsg , "SL");
			
			//=============================================================
			// 수신전문 항목 값 추출
			//=============================================================
//			sSTL_APPEAR_GP              = ydDaoUtils.paraRecChkNull(inRecord, "STL_APPEAR_GP");
//			sSTL_NO 					 = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");                  // 수신전문의 재료번호는 PLATE_NO
//			sCURR_PROG_CD               = ydDaoUtils.paraRecChkNull(inRecord, "CURR_PROG_CD");
//			sWO_CAR_PLNT_PROC_CD        = ydDaoUtils.paraRecChkNull(inRecord, "WO_CAR_PLNT_PROC_CD");
//			sFRTOMOVE_ORD_DATE          = ydDaoUtils.paraRecChkNull(inRecord, "FRTOMOVE_ORD_DATE");  
//			sURGENT_FRTOMOVE_WORD_GP    = ydDaoUtils.paraRecChkNull(inRecord, "URGENT_FRTOMOVE_WORD_GP");
//			sCANCEL_YN 	       		 = ydDaoUtils.paraRecChkNull(inRecord, "CANCEL_YN");
			String sYD_GP                   = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"      ));
			String sSTL_APPEAR_GP           = commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));  
			String sSTL_NO                  = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"));  
			String sCURR_PROG_CD            = commPiUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"));  
			String sWO_CAR_PLNT_PROC_CD     = commPiUtils.trim(rcvMsg.getFieldString("WO_CAR_PLNT_PROC_CD"));  
			String sFRTOMOVE_ORD_DATE       = commPiUtils.trim(rcvMsg.getFieldString("FRTOMOVE_ORD_DATE"));  
			String sURGENT_FRTOMOVE_WORD_GP = commPiUtils.trim(rcvMsg.getFieldString("URGENT_FRTOMOVE_WORD_GP"));  
			String sCANCEL_YN               = commPiUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));  
			String sModifier                = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));  
			
			//CANCEL_YN=Y 일시, 입고 가능여부 확인후 입고처리
			if(sCANCEL_YN.equals("Y")){  //임시 YY. 실제 배포할땐 Y로 변경
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				
				recInTemp.setField("STL_NO" , sSTL_NO);
				
				//현재위치, 입고 TC 송신 가능여부 체크
				rsResult	= ydPICommDAO.selectT(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.chkPlateYDLMJ1012", logId, sMethodName, "입고TC 가능여부조회");
				
				if (rsResult.size() <= 0){
					sMsg = "["+sOperationName+"] 제품번호[" + sSTL_NO + "]로 제품정보 조회 시 에러발생";
					commPiUtils.printLog(logId, sMsg , "SL");					
					throw new DAOException(sMsg);
				}
				sMsg = "["+sOperationName+"] 제품번호[" + sSTL_NO + "]로 제품정보 조회 완료";
				commPiUtils.printLog(logId, sMsg , "SL");				
				
				rsResult.first();
				recGetVal = rsResult.getRecord();
				String sFLAG      = commPiUtils.trim(recGetVal.getFieldString("FLAG"));
				String sYD_STR_LOC = commPiUtils.trim(recGetVal.getFieldString("YD_STR_LOC"));
				
				JDTORecord outRec  	= JDTORecordFactory.getInstance().create();
				String curDate 		= YdUtils.getCurDate("yyyyMMddHHmmss");
				
				if(sFLAG.equals("Y")){
				
				outRec.setField("MQ_TC_CD"        , "M10YDLMJ1012");
				outRec.setField("MQ_TC_CREATE_DDTT" , new String(curDate));
				outRec.setField("YD_GP"          , "T");
				outRec.setField("DIST_GOODS_GP"  , "P");
				outRec.setField("YARD_GP" 		 , "");
				outRec.setField("GOODS_NO"       , sSTL_NO);
				outRec.setField("STORE_LOC_CD"      , sYD_STR_LOC);  
				outRec.setField("RECEIPT_DATE"   , curDate.substring(0, 8));
				outRec.setField("RECEIPT_TIME"   , curDate.substring(8, 14));
				
				ydDelegate.sendMsg_NoMakeTc(outRec);
				sMsg= "후판입고 TC 전송 (STL_NO:: [" + sSTL_NO + "])" ;
				commPiUtils.printLog(logId, sMsg , "SL");
				}
				else{
				sMsg= "후판입고 TC 미전송 (STL_NO:: [" + sSTL_NO + "])" ;
				commPiUtils.printLog(logId, sMsg , "SL");					
				}
				return;
			}
			
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
				mthdNm = msgId.substring(0, 2) + mthdNm;	
				
			if(sWO_CAR_PLNT_PROC_CD.equals("")){
				// 정보반송
				sMK_MOD_RSN = "1";
			} else {
				// 현물반송
				sMK_MOD_RSN = "4"; 			
			}
			
			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP			재료외형구분
			STL_NO					재료번호
			CURR_PROG_CD			현재진도코드
			WO_CAR_PLNT_PROC_CD		지시차공장공정코드
			FRTOMOVE_ORD_DATE		이송지시일자
			URGENT_FRTOMOVE_WORD_GP	긴급이송작업지시구분
			*/
			recStockColumn 	= JDTORecordFactory.getInstance().create();
			recStockColumn.setField("STL_APPEAR_GP"          , sSTL_APPEAR_GP);	
			recStockColumn.setField("STL_NO"                 , sSTL_NO);
			recStockColumn.setField("STL_PROG_CD"            , sCURR_PROG_CD);	
			recStockColumn.setField("PLNT_PROC_CD"           , sWO_CAR_PLNT_PROC_CD);
			recStockColumn.setField("FRTOMOVE_ORD_DATE"      , sFRTOMOVE_ORD_DATE);			
			recStockColumn.setField("URGENT_FRTOMOVE_WORD_GP", sURGENT_FRTOMOVE_WORD_GP);
			recStockColumn.setField("MODIFIER"               , sModifier);
	
			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
//			rVal= YdCommonUtils.getYdAimRtGp("P",rcvMsg );		
//			recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);

			String[] rVal = new String[2];
			rVal= YdCommonUtils.getYdAimRtGp("P",rcvMsg );		
			recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
			//****************************************************************************************************

			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				sMsg= "YD_STOCK[후판제품보류확정] UPDATE Error :: [" + intRtnVal + "]" ;
				commPiUtils.printLog(logId, sMsg , "SL");
				return ;
			}
			commPiUtils.printLog(logId, "[2] YD_STOCK[후판제품보류확정] UPDATE Success" , "SL");
			//****************************************************************************************************

			//======================================================
			// 저장품제원 : 후판제품 L2 로 송신(YDY8L002)
			//======================================================
			// 레코드 생성
			recResult = JDTORecordFactory.getInstance().create();
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(sYD_GP)) { //- 2013.01.16 수정 (3기)
				recResult.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고
				//2024.08.05 YDY4L002 미사용 전문 타는 경우 있어, ELSE 문 제거 및 야드 T 일 경우만 송신처리. by HJW
				sMsg = "후판제품L2로 저장품제원 전문(YDY8L002) 송신";
				recResult.setField("YD_INFO_SYNC_CD", "5");            // 5:지정저장품
				recResult.setField("STL_NO"         , sSTL_NO);
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				
				// 2021. 05. 17 전문생성 시점에 PT_PLATE_COMM에 진도코드가 바뀌지 않은 문제가 있어
				// 진도코드를 파라메터로 넘겨 처리함
				recResult.setField("CURR_PROG_CD"  , sCURR_PROG_CD);
				
				ydDelegate.sendMsg(recResult);		
			}		
			
			// 전사물류개선 2021. 4. 3
			if(PlateGdsYdUtil.isSendToEaiY9_stlNo(sSTL_NO) ){
				recResult.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
				ydDelegate.sendMsg(recResult);
			}			
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}
	
	
	/**	 
	 * [A] 오퍼레이션명 :후판제품사외야드도착실적(DMYDR043) - procPlGdsDistShipArrWr
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1162(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "[YdPlateL3RcvPISeEJB.rcvM10LMYDJ1162] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commPiUtils.printLog(logId, mthdNm, "S+");
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "T", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}			
			
			//수신 항목 값
			String msgId  = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			// DAO 및 UTIL 객체 생성
			YmEtcDao YmEtcDao   = new YmEtcDao();
			// 레코드 선언
			JDTORecord recIn    = null;
			
		    String sMsg         = "";
		    String sMethodName  = "procPlGdsDistShipArrWr";	    	        	
			int intRtnVal = 0;
			
	    	sMsg = "[출하] 후판/코일 제품사외창고도착실적 수신";
	    	commPiUtils.printLog(logId, sMsg , "SL");
	    	
	    	/*
	    	YD_GP			야드구분	CHAR	1	Y		
	    	TRANSMIT_DATE	전송일자	CHAR	8			
	    	SEND_SEQ		전송순번	NUMBER	5			
	    	CANCEL_YN		취소유무	CHAR	1	Y	Y: 취소 , N: 지시	
	    	FR_GBN			사외창고구분 CHAR  2   4A-삼우, 4B-지전 , VC-(주)삼일 , VD-한원스틸 , VE-(주)중앙운수
	    	TABLE : TB_DM_SETTLEDOWNWRSLTIFTEMP
			*/
	    	
//	    	String sYdGp			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
//	    	String sTransmitDate 	= ydDaoUtils.paraRecChkNull(msgRecord, "TRANSMIT_DATE");
//	    	String sSendSeq     	= ydDaoUtils.paraRecChkNull(msgRecord, "SEND_SEQ");
//	    	String sCancelYn     	= ydDaoUtils.paraRecChkNull(msgRecord, "CANCEL_YN");
//	    	String sFrGbn	     	= ydDaoUtils.paraRecChkNull(msgRecord, "FR_GBN");
	    	
	    	String sYdGp         = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"));    
	    	String sTransmitDate = commPiUtils.trim(rcvMsg.getFieldString("TRANSMIT_DATE"));    
	    	String sSendSeq      = commPiUtils.trim(rcvMsg.getFieldString("SEND_SEQ"));    
	    	String sCancelYn     = commPiUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));    
	    	String sFrGbn        = commPiUtils.trim(rcvMsg.getFieldString("FR_GBN"));  
	    	String sTrnReqDate   = commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE"));    
	    	String sTrnReqSeq    = commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ"));    
	    	String sCarNo        = commPiUtils.trim(rcvMsg.getFieldString("CAR_NO")); 

	    	if("N".equals(sCancelYn)){
		    	
	    		// 레코드 생성
				recIn = JDTORecordFactory.getInstance().create();
//				recIn.setField("TRANSMIT_DATE", sTransmitDate);
//				recIn.setField("SEND_SEQ", sSendSeq);
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

				if(sYdGp.equals("4")){
					//intRtnVal = YmEtcDao.uptYmEtcDao(recIn, 4);  //후판 사외창고 인경우
					//PIDEV
					intRtnVal = YmEtcDao.uptYmEtcDao(recIn, 84);  //후판 사외창고 인경우
					//com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updTB_DM_SETTLEDOWNWRSLTIFTEMP_PIDEV
					if(intRtnVal < 0){
						sMsg = "PLATECOMM[PLATE작업지시] Error :: TRN_REQ_DATE(" + sTrnReqDate + ") SEND_SEQ(" + sTrnReqSeq + ") [" + intRtnVal + "]" + "PARAMETER ERROR";
						commPiUtils.printLog(logId, sMsg , "SL");
						return ;
					} else if(intRtnVal == 0){
						sMsg = "PLATECOMM[PLATE작업지시] Error :: TRN_REQ_DATE(" + sTrnReqDate + ") SEND_SEQ(" + sTrnReqSeq + ")[" + intRtnVal + "]" + "DO NOT EXIST";
						commPiUtils.printLog(logId, sMsg , "SL");
						return ;
					}
					
				}else if(sYdGp.equals("V")){
//					intRtnVal = YmEtcDao.uptYmEtcDao(recIn, 12); //열연 사외창고 인경우
					intRtnVal = YmEtcDao.uptYmEtcDao(recIn, 82); //열연 사외창고 인경우
									
					if(intRtnVal < 0){
						sMsg = "COILCOMM[COIL작업지시] Error :: TRN_REQ_DATE(" + sTrnReqDate + ") SEND_SEQ(" + sTrnReqSeq + ") [" + intRtnVal + "]" + "PARAMETER ERROR";
						commPiUtils.printLog(logId, sMsg , "SL");
						return ;
					} else if(intRtnVal == 0){
						sMsg = "COILCOMM[COIL작업지시] Error :: TRN_REQ_DATE(" + sTrnReqDate + ") SEND_SEQ(" + sTrnReqSeq + ")[" + intRtnVal + "]" + "DO NOT EXIST";
						commPiUtils.printLog(logId, sMsg , "SL");
						return ;
					}
				}
	    	}
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}		
	
	
	/**
	 * 오퍼레이션명 : 일관제철  후판출하전문 취소처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receiveCancel(JDTORecord rcvMsg) {
		String mthdNm = "후판출하전문 취소처리[PlateL3RcvPISeEJB.receiveCancel] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		int intRtnVal = 0;
		
		YdStockDao ydStockDao = new YdStockDao();
		
//      1':목전정보,'2': 보관지시,'3':반품',4':출하지시대기,'5':운송지시대기		
//		DMYDR006 M10LMYDJ1012 4
//		DMYDR015 M10LMYDJ1012 1  cancel 
//		DMYDR018              5  cancel
//		DMYDR028              2  cancel 
//		DMYDR034              3
//		DMYDR009 M10LMYDJ1022    //cancel
//		DMYDR060 M10LMYDJ1032    cancel
//		DMYDR061 M10LMYDJ1042
//		DMYDR038 M10LMYDJ1062
//		DMYDR031 M10LMYDJ1072
//		DMYDR042 M10LMYDJ1082
//		DMYDR044 M10LMYDJ1122
//		DMYDR046 M10LMYDJ1132
//		DMYDR048 M10LMYDJ1142
//		DMYDR003 M10LMYDJ1152    //cancel
//		DMYDR043 M10LMYDJ1162

		
	    try {
	    	
	    	commPiUtils.printLog(logId, mthdNm, "S+");
	    	String msgId                = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
	    	String ydGp                 = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"             ));   // 야드구분
			String sInfoGp              = commPiUtils.trim(rcvMsg.getFieldString("INFO_GP"           ));   // 정보구분
			//'1':목전정보,'2': 보관지시,'3':반품',4':출하지시대기,'5':운송지시대기
            String sStlAppearGp         = commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"     ));   // 재료외형구분
            String sStlNo               = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"            ));   // 재료번호
            String sCurrProgCd          = commPiUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"      ));   // 현재진도코드
            String sOrdYeojaeGp         = commPiUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP"     ));   // 주문여재구분
            String sOrdNo               = commPiUtils.trim(rcvMsg.getFieldString("ORD_NO"            ));   // 주문번호
            String sOrdDtl              = commPiUtils.trim(rcvMsg.getFieldString("ORD_DTL"           ));   // 주문행번
            String sOrdGp               = commPiUtils.trim(rcvMsg.getFieldString("ORD_GP"            ));   // 수주구분
            String sCustCd              = commPiUtils.trim(rcvMsg.getFieldString("CUST_CD"           ));   // 고객코드
            String sDestCd              = commPiUtils.trim(rcvMsg.getFieldString("DEST_CD"           ));   // 목적지코드
            String sDlvrddRuleDd        = commPiUtils.trim(rcvMsg.getFieldString("YD_DLVRDD_RULE_DD" ));   // 납기기준일
            String sDestTelNo           = commPiUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO"       ));   // 목적지전화번호
            String sDistShipassignGp    = commPiUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP"));   // 출하배선지시구분
            String sShipassignWordDate  = commPiUtils.trim(rcvMsg.getFieldString("SHIPASSIGN_WORD_DATE")); // 배선작업지시일자
            String sShipassignWordSeono = commPiUtils.trim(rcvMsg.getFieldString("SHIPASSIGN_WORD_SEQNO"));// 배선작업지시순번
            
            String sShipCd              = commPiUtils.trim(rcvMsg.getFieldString("SHIP_CD"));              // 선박코드
            String sShipName            = commPiUtils.trim(rcvMsg.getFieldString("SHIP_NAME"));            // 선박명
            String sSailNo              = commPiUtils.trim(rcvMsg.getFieldString("SAILNO"));               // 선박항차
//            String sDetailArrCd         = commPiUtils.trim(rcvMsg.getFieldString("DETAIL_ARR_CD"));        // 상세착지
//            String sDeliverTermCd       = commPiUtils.trim(rcvMsg.getFieldString("DELIVER_TERM_CD"));      // 인도조건
//            String sTransMeansGp        = commPiUtils.trim(rcvMsg.getFieldString("TRANS_MEANS_GP"));       // 운송수단구분
//            String sIssueGp             = commPiUtils.trim(rcvMsg.getFieldString("ISSUE_GP"));             // 구분
//            String sGoodNo              = commPiUtils.trim(rcvMsg.getFieldString("GOODS_NO"));             // 재료번호
//            String sCancelYn            = commPiUtils.trim(rcvMsg.getFieldString("CANCEL_YN"         ));   // Y: 취소 , N: 지시
            String sModifier            = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));             //수정자(Backup Only)
            
	    	//=======================================================
	    	// 야드구분
	    	//=======================================================
	    	String sStlGp = "";

	    	if ("A".equals(ydGp) || "S".equals(ydGp)){ //S:SLAB
	    		sStlGp = "S";	
	    	} else if("D".equals(ydGp)|| "T".equals(ydGp)){  
	    		sStlGp = "P";
	    	} else {
		    	throw new Exception("전문에 야드구분(YD_GP)항목의 값이 이상 :: [" + ydGp + "]");
	    	}
	    	
	    	/*
	    	 * 후판출하상차지시 취소시점에 예외사항 체크
	    	 * M10LMYDJ1032에서  후판출하상차지시 취소시 이미 작업예약에 재료 존재함여부 CHECK함
	    	 */ 
//	    	if("M10LMYDJ1032".equals(msgId) && YdConstant.YD_GP_PLATE2_GDS_YARD.equals(ydGp)){
//	    		
//	    		JDTORecordSet rsChkPara	= null;
//	    		JDTORecord recChkPara	= null;
//	    		String sChkStlNo		= "";
//	    		String szMsg			= "";
//	    		
//	    		intYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNullInt(rcvMsg,"YD_EQP_WRK_SH");
//	    		
//	    		for(int index = 0; index < intYD_EQP_WRK_SH; index++){
//	    			
//	    			rsChkPara = JDTORecordFactory.getInstance().createRecordSet("");
//	    			recChkPara  = JDTORecordFactory.getInstance().create();
//	    			
//	    			sChkStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO" + (index+1));
//	    			
//	    			if("".equals(sChkStlNo)) break;
//	    			
//	    			recChkPara.setField("STL_NO" , sChkStlNo);
//	    			
//	    			intRtnVal = ydWrkbookDao.getYdWrkbookmtl(recChkPara, rsChkPara, 2);
//	    			
//	    			if(intRtnVal <= 0) {
//					}else{
//						szMsg="["+szMethodName+"] 후판출하상차지시 취소시 이미 작업예약에 재료 존재함.["+sChkStlNo+"]";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//						return false;
//					}
//	    		}
//	    	}
	    	
//	    	if(YdConstant.DMYDR003.equals(sJmsTcCd) || YdConstant.DMYDR008.equals(sJmsTcCd) || YdConstant.DMYDR009.equals(sJmsTcCd) ||	YdConstant.DMYDR013.equals(sJmsTcCd) ||	
//    		   YdConstant.DMYDR014.equals(sJmsTcCd) || YdConstant.DMYDR015.equals(sJmsTcCd) || YdConstant.DMYDR016.equals(sJmsTcCd) ||	YdConstant.DMYDR018.equals(sJmsTcCd) ||	
//    		   YdConstant.DMYDR026.equals(sJmsTcCd) || YdConstant.DMYDR027.equals(sJmsTcCd) || YdConstant.DMYDR028.equals(sJmsTcCd)){
	    	if (("M10LMYDJ1012".equals(msgId) &&  "2".equals(sInfoGp)) ||   //DMYDR028
	    		("M10LMYDJ1012".equals(msgId) &&  "5".equals(sInfoGp)) ||   //DMYDR018 
	    		("M10LMYDJ1012".equals(msgId) &&  "3".equals(sInfoGp)) ) {  //DMYDR034

	    		//==================================================================
		    	// 전문에 따른 분기 (재료번호 1개 : STL_NO)
				//==================================================================

	    		JDTORecord jrStockSet = JDTORecordFactory.getInstance().create();
				jrStockSet.setField("STL_APPEAR_GP", 		sStlAppearGp);
				jrStockSet.setField("STL_NO", 				sStlNo);
				jrStockSet.setField("STL_PROG_CD",			sCurrProgCd);
				jrStockSet.setField("ORD_YEOJAE_GP", 		sOrdYeojaeGp);
				jrStockSet.setField("ORD_NO", 				sOrdNo);
				jrStockSet.setField("ORD_DTL", 				sOrdDtl);
				jrStockSet.setField("ORD_GP", 				sOrdGp);
				jrStockSet.setField("CUST_CD", 				sCustCd);
				jrStockSet.setField("DEST_CD", 				sDestCd);
				jrStockSet.setField("YD_DLVRDD_RULE_DD", 	sDlvrddRuleDd);
				jrStockSet.setField("DEST_TEL_NO", 			sDestTelNo);
				jrStockSet.setField("DIST_SHIPASSIGN_GP", 	sDistShipassignGp);				
				
				//>>>>>>>>>>>>선박정보<<<<<<<<<<<<<<<<<<
				jrStockSet.setField("DIST_SHIPASSIGN_GP", 	sDistShipassignGp);
				jrStockSet.setField("SHIPASSIGN_WORD_DATE", sShipassignWordDate);
				jrStockSet.setField("SHIPASSIGN_WORD_SEQNO",sShipassignWordSeono);
				//24.10.30 임진후 기술기사 요청. 주문 취소시 선박명 보이는 현상 수정
				
				String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APP060", "T", "003");
				
				if("Y".equals(sApplyYnPI)){
					jrStockSet.setField("SHIP_CD", 				"");//취소시 선박명 CLEAR 
					jrStockSet.setField("SHIP_NAME", 			"");//취소시 선박명 CLEAR 
				}
				else {
					jrStockSet.setField("SHIP_CD", 				sShipCd);
					jrStockSet.setField("SHIP_NAME", 			sShipName);
				}
//PI 확인					
//				jrStockSet.setField("RSHP_HOLD_NO", 		ydDaoUtils.paraRecChkNull(inRecord,"RSHP_HOLD_NO"));
				jrStockSet.setField("SAILNO", 				sSailNo);
				jrStockSet.setField("MODIFIER", 			sModifier);				
	    		// TC에 따른 변동항목 추출
	    			
		    		// 후판제품운송지시대기(YDDMR018)에는 항목이 더 있음
//	    		if(YdConstant.DMYDR018.equals(msgId)){
	    		if("M10LMYDJ1012".equals(msgId) &&  "5".equals(sInfoGp)){
	    			
					jrStockSet.setField("DIST_SHIPASSIGN_GP", 	sDistShipassignGp);
					jrStockSet.setField("SHIPASSIGN_WORD_DATE", sShipassignWordDate);
					jrStockSet.setField("SHIPASSIGN_WORD_SEQNO",sShipassignWordSeono);
					
					if("Y".equals(sApplyYnPI)){
						jrStockSet.setField("SHIP_CD", 				"");//취소시 선박명 CLEAR 
						jrStockSet.setField("SHIP_NAME", 			"");//취소시 선박명 CLEAR 
					}
					else {
						jrStockSet.setField("SHIP_CD", 				sShipCd);
						jrStockSet.setField("SHIP_NAME", 			sShipName);
					}
//PI 확인					
//					jrStockSet.setField("RSHP_HOLD_NO", 		ydDaoUtils.paraRecChkNull(inRecord,"RSHP_HOLD_NO"));
					jrStockSet.setField("SAILNO", 				sSailNo);
					jrStockSet.setField("PRE_AR_STAT_CD"       , ""); // 보관매출 운송지시 항목 취소로 무조건 셋팅.
					jrStockSet.setField("CAR_LOTID"       	   , ""); // 선별LOT편성정보 삭제
					jrStockSet.setField("MODIFIER", 			sModifier);	
					
		    	
			    	
			    	//======================================================
					// 저장품제원 : 후판제품 L2 로 송신(YDY8L002)
					//======================================================
					JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
					sndL2Msg.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고 L2전문
					sndL2Msg.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
					sndL2Msg.setField("STL_NO"         , sStlNo);
					sndL2Msg.setField("YD_STK_COL_GP"  , "");
					sndL2Msg.setField("YD_STK_BED_NO"  , "");
					
					// 2021. 05. 17 전문생성 시점에 PT_PLATE_COMM에 진도코드가 바뀌지 않은 문제가 있어
					// 진도코드를 파라메터로 넘겨 처리함
					sndL2Msg.setField("CURR_PROG_CD"  , sCurrProgCd);
					ydDelegate.sendMsg(sndL2Msg);		
					
					// 전사물류개선 2021. 4. 3
	    			if(PlateGdsYdUtil.isSendToEaiY9_stlNo(sStlNo) ){
	    				sndL2Msg.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
	    				ydDelegate.sendMsg(sndL2Msg);
	    			}
	    			    			
	    		}
	    		
				// 야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
	    		// 야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
//				rVal = YdCommonUtils.getYdAimRtGp2(sJmsTcCd , sSTL_GP, szSTL_NO, szCURR_PROG_CD);		
//				szYD_AIM_RT_GP = rVal[0];	    		
	    		
	    		String[] rVal = new String[2];
				rVal = YdCommonUtils.getYdAimRtGp2(msgId , "P", sStlNo, sCurrProgCd);
				
				String ydAimRtGp = rVal[0];
				jrStockSet.setField("YD_AIM_RT_GP", ydAimRtGp);

//		    	if(YdConstant.DMYDR028.equals(msgId)){
				if("M10LMYDJ1012".equals(msgId) &&  "2".equals(sInfoGp)){
		    		    	    	// 레코드 생성
		    		jrStockSet = JDTORecordFactory.getInstance().create();
		    		jrStockSet.setField("STL_NO", sStlNo);
		    		jrStockSet.setField("DEL_YN", "N");
		    		intRtnVal = ydStockDao.updYdStock(jrStockSet,0);	
    	
		    	} else {
		    		//  저장품 업데이트 처리 (업데이트 쳐야 될 항목은 업데이트 클리어될 항목은 클리어해야 하기 위해 기본쿼리로 처리)
		    		intRtnVal = ydStockDao.updYdStock(jrStockSet, 0);	
		    	}
	    	}
	    	
	    	commPiUtils.printLog(logId, mthdNm, "S-");
	    }catch(DAOException daoe) {
               throw daoe;
        }catch(Exception e) {
	        throw new EJBServiceException(e);
        }
		return true;
	}	

	
	/********************************
	 * PIDEV
	 *************************************/
	
	
	/**
	 * 오퍼레이션명 : 출하차량상차LOT편성 전문발송
	 * @param msgRecord
	 * @param recCarSpec
	 * @param sSndTcCode
	 * @param sMethodName
	 * @return
	 */
	public String sndOutCarLoadLotTC_PIDEV(JDTORecord msgRecord, JDTORecord recStkCol, String sSndTcCode, String sMethodName, String sEjbMethod) {
		//boolean bResult = true;
		String sMsg = "";
		String sOperationName = "전문 전송";
		//연계모듈을 EJB Call or JMS Call 호출 유무 변수
		String sIS_EJB_CALL = null;
		//리턴값
		String sRtnMsg			= null;
		String sEjbJndiName	= null;
		EJBConnector ejbConn 	= null;
		JDTORecordSet rsResult = null;
		JDTORecord recTemp = null;
		JDTORecord recPara = null;
		YdStockDao ydStockDao = new YdStockDao();
		int nRet = 0;
		String sCR_FRTOMOVE_GP	= "";
		try {
			//연계모듈을 EJB Call[Y값으로 설정] or JMS Call[값이 없거나 N값으로 설정] 호출 유무 변수  - 지금은 화면으로부터 전송
			sIS_EJB_CALL              = commPiUtils.trim(msgRecord.getFieldString("IS_EJB_CALL"));   
			//sIS_EJB_CALL ="Y"
			String sYD_CARLD_STOP_LOC = commPiUtils.trim(msgRecord.getFieldString("YD_CARLD_STOP_LOC"));
			
			// 레코드생성-----------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DATE",  msgRecord.getFieldString("TRANS_ORD_DT"));
			recPara.setField("TRANS_ORD_SEQNO", msgRecord.getFieldString("TRANS_ORD_SEQNO"));

			// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDate*/
			nRet = ydStockDao.getYdStock(recPara, rsResult, 730);
			if(nRet > 0){
				rsResult.first();
				recTemp			= rsResult.getRecord();			
				sCR_FRTOMOVE_GP	= commPiUtils.trim( recTemp.getFieldString("CR_FRTOMOVE_GP"));
			}
			
			// 전문 생성 및 전송
			JDTORecord recSndQue = JDTORecordFactory.getInstance().create();
		
	    	recSndQue.setField("MSG_ID"           	  , sSndTcCode);
	    	recSndQue.setField("YD_CARLD_STOP_LOC"	  , sYD_CARLD_STOP_LOC);							//차량상차정지위치
			recSndQue.setField("YD_CAR_USE_GP"    	  , YdConstant.YD_CAR_USE_GP_DM);	//차량사용구분(G:출하차량, L:구내운송) - 출하차량만 적용
			recSndQue.setField("YD_GP"           	  , sYD_CARLD_STOP_LOC.substring(0, 1));			//야드
			recSndQue.setField("YD_BAY_GP"        	  , sYD_CARLD_STOP_LOC.substring(1, 2)); 			//동
			recSndQue.setField("TRANS_ORD_DATE"   	  , msgRecord.getFieldString("TRANS_ORD_DT"));		//운송지시일자
			recSndQue.setField("TRANS_ORD_SEQNO" 	  , msgRecord.getFieldString("TRANS_ORD_SEQNO"));	//운송지시순번
			recSndQue.setField("CAR_NO"           	  , msgRecord.getFieldString("CAR_NO"));			//차량번호
			recSndQue.setField("CARD_NO"          	  , msgRecord.getFieldString("CARD_NO"));			//카드번호
			recSndQue.setField("SPOS_WLOC_CD"         , msgRecord.getFieldString("SPOS_WLOC_CD"));		//발지개소코드
			recSndQue.setField("SPOS_YD_PNT_CD"       , msgRecord.getFieldString("SPOS_YD_PNT_CD"));	//발지야드포인트코드
			recSndQue.setField("CAR_KIND"			  , commPiUtils.trim(msgRecord.getFieldString("CAR_KIND")));
			recSndQue.setField("WORK_GP"			  , commPiUtils.trim(msgRecord.getFieldString("WORK_GP")));
			recSndQue.setField("CR_FRTOMOVE_GP"       , sCR_FRTOMOVE_GP);

			recSndQue.setField("IS_EJB_CALL", sIS_EJB_CALL);
			
//			ydUtils.displayRecord(sOperationName, recSndQue);
			commPiUtils.printParam(sOperationName, recSndQue);
			
			
			sMsg="[출하차량상차LOT편성 전문발송]sIS_EJB_CALL = " + sIS_EJB_CALL;
			commPiUtils.printLog(sOperationName, sMsg , "SL");
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * EJB Call or JMS Call 유무를 판단하여 호출하는 기능
			 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			if( sIS_EJB_CALL.equals("Y")) {
				//EJB Call ==> 메소드 콜
				sMsg = "출하차량상차LOT편성 전문발송 - 메소드["+sEjbMethod+"] 콜 시작";
				commPiUtils.printLog(sOperationName, sMsg , "SL");
				
			
				sEjbJndiName = "IssueWrkDmdSeEJB";
				
				ejbConn = new EJBConnector("default", sEjbJndiName, this);
				sRtnMsg = (String)ejbConn.trx(sEjbMethod, new Class[] { JDTORecord.class }, new Object[] { recSndQue });
					
				sMsg = "출하차량상차LOT편성 전문발송 - 메소드["+sEjbMethod+"] 콜 완료";
				commPiUtils.printLog(sOperationName, sMsg , "SL");
			}else{
				//전문 송신
				ydDelegate.sendMsg(recSndQue);
				sMsg = "출하차량상차LOT편성 전문발송["+sEjbMethod+"] - 전문 송신 완료";
				commPiUtils.printLog(sOperationName, sMsg , "SL");
			}
			
	    	//ydDelegate.sendMsg(recSndQue);	
		}catch(Exception e) {
			sMsg= sMethodName + "(" + sOperationName + ") Error:" + e.getMessage();
			commPiUtils.printLog(sOperationName, sMsg , "SL");
			return YdConstant.RETN_CD_FAILURE;
		}
		return sRtnMsg;
	}

	
	/**
	 * 오퍼레이션명 : 출하차량도착실적처리 - 맵활성화
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szSPOS_WLOC_CD
	 * @param szSPOS_YD_PNT_CD
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public int procOutCarArrWr_PIDEV(JDTORecord msgRecord, JDTORecordSet rsStkCol) throws DAOException {
		String mthdNm = "[YdPlateL3RcvPISeEJB.procOutCarArrWr_PIDEV] < " + msgRecord.getResultMsg();
		String logId = msgRecord.getResultCode();
		String szMsg                   = "";
		YdStkColDao ydStkColDao        = new YdStkColDao();
		YdStkBedDao ydStkBedDao        = new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao        = new YdStkLyrDao();
		YdStockDao ydStockDao = new YdStockDao();
		
		//JDTORecordSet rsStkCol = null;
		JDTORecord recInTemp = null;
		
		JDTORecordSet rsTemp = null;
		
		String szMethodName			= "procOutCarArrWr_PIDEV";
		
		int intRtnVal = 0;
		int intLevLocGp = 0;
		// 쿼리
		String szYD_CARLD_STOP_LOC = "";
		String szTRANS_EQUIPMENT_TYPE = "";
		String szYD_CAR_PROG_STAT = "";
	    String szTRANS_ORD_DATE = null;
	    String szTRANS_ORD_SEQNO = null;
	    
	    String szYD_WRK_ALW_WT = null;
	    String szYD_MTL_SH = null;
	    
	    try {
	    	String szSPOS_WLOC_CD    = commPiUtils.trim(msgRecord.getFieldString("SPOS_WLOC_CD"  ));  
	    	String szSPOS_YD_PNT_CD  = commPiUtils.trim(msgRecord.getFieldString("SPOS_YD_PNT_CD"));  
	    	String szCAR_NO          = commPiUtils.trim(msgRecord.getFieldString("CAR_NO"        ));
			String szCARD_NO         = commPiUtils.trim(msgRecord.getFieldString("CARD_NO"       )); 
			
			szMsg="[CarMvHdSeEJBBean - procOutCarArrWr] 차량도착 개소코드[" + szSPOS_WLOC_CD + "], 차량도착포인트코드[" + szSPOS_YD_PNT_CD + "], 차량번호[" + szCAR_NO + "], 카드번호[" + szCARD_NO + "]";
			commPiUtils.printLog(szMethodName, szMsg , "SL");
			
	    	//착지개소코드와 착지야드포인트코드로 적치열을 조회한다.
	    	//rsStkCol = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("WLOC_CD"    , szSPOS_WLOC_CD);
	    	recInTemp.setField("YD_PNT_CD"  , szSPOS_YD_PNT_CD);
	    	intLevLocGp = ydStkColDao.getYdStkcol(recInTemp, rsStkCol, 4);
			if(intLevLocGp <= 0) {
				if(intRtnVal == 0) {
					szMsg="[CarMvHdSeEJBBean - procOutCarArrWr] getYdStkcol  data not found";
					commPiUtils.printLog(szMethodName, szMsg , "SL");
				}else if(intRtnVal == -2) {
					szMsg="[CarMvHdSeEJBBean - procOutCarArrWr] getYdStkcol  parameter error";
					commPiUtils.printLog(szMethodName, szMsg , "SL");
				}
				return intRtnVal = -1;
			}
	    	
	    	if(intLevLocGp > 0) {
	    		szMsg= "[CarMvHdSeEJBBean - procOutCarArrWr] 개소코드와 야드포인트코드로 적치열 조회 성공";
	    		commPiUtils.printLog(szMethodName, szMsg , "SL");
	    		JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsStkCol.getRecord(0));
	    	
		    	// 적치열구분을 조회 (도착지)
		    	szYD_CARLD_STOP_LOC = YdUtils.fillSpZr(commPiUtils.trim(recOutTemp.getFieldString("YD_STK_COL_GP"  )), 6, 1);
		    	if( szYD_CARLD_STOP_LOC.trim().equals("") ) {
		    		szMsg = "[CarMvHdSeEJBBean - procOutCarArrWr] 적치열구분(상차정지위치)이 존재하지 않음";
		    		throw new DAOException(szMethodName + " : " + szMethodName + " Error : " + szMsg);
		    	}
		    	// 적치열 테이블에 활성상태 처리 
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP"      , szYD_CARLD_STOP_LOC);
		    	recInTemp.setField("YD_STK_COL_ACT_STAT", "L");
		    	recInTemp.setField("YD_CAR_USE_GP"      , "G");
		    	recInTemp.setField("TRN_EQP_CD"         , "");
		    	recInTemp.setField("CAR_NO"             , szCAR_NO);
		    	recInTemp.setField("CARD_NO"            , szCARD_NO);
		    	
		    	intRtnVal = ydStkColDao.updYdStkcol(recInTemp, 0);
		    	
		    	szMsg= "[CarMvHdSeEJBBean - procOutCarArrWr] 적치열 활성화 처리";
		    	commPiUtils.printLog(szMethodName, szMsg , "SL");
				
				if(intRtnVal <= 0) {
	    			if(intRtnVal == 0) {
	    				szMsg="[CarMvHdSeEJBBean - procOutCarArrWr] updYdStkcol Data Not Found";
	    				commPiUtils.printLog(szMethodName, szMsg , "SL");
	    			}else if(intRtnVal == -1) {
	    				szMsg="[CarMvHdSeEJBBean - procOutCarArrWr] updYdStkcol Duplicate Data,";
	    				commPiUtils.printLog(szMethodName, szMsg , "SL");
	    			}else if(intRtnVal == -2) {
	    				szMsg="[CarMvHdSeEJBBean - procOutCarArrWr] updYdStkcol Parameter Error";
	    				commPiUtils.printLog(szMethodName, szMsg , "SL");
	    			}else if(intRtnVal == -3){
	    				szMsg="[CarMvHdSeEJBBean - procOutCarArrWr] updYdStkcol Execution Failed";
	    				commPiUtils.printLog(szMethodName, szMsg , "SL");
	    			}
	    			throw new DAOException(szMethodName + " : " + szMethodName + " Error : " + szMsg);
	    		}
				

				//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
//				CarPointinforeg2("C",szCAR_NO,szCARD_NO,szYD_CARLD_STOP_LOC,"","","L");
		        EJBConnector ejbConn5 = new EJBConnector("default","CarMvHdSeEJB",this);
				ejbConn5.trx("CarPointinforeg2", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
			  	             new Object[]{"C",szCAR_NO,szCARD_NO,szYD_CARLD_STOP_LOC,"","","L"});
				
				/////////////////////////////////////////////////////////////////////////////////
				// 수신항목 - 운송지시일자
				szTRANS_ORD_DATE  = commPiUtils.trim(msgRecord.getFieldString("TRANS_ORD_DT"  ));
				// 수신항목 - 운송지시순번
				szTRANS_ORD_SEQNO = commPiUtils.trim(msgRecord.getFieldString("TRANS_ORD_SEQNO"));
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP"  , szYD_CARLD_STOP_LOC.substring(0, 1));
//				recInTemp.setField("CARD_NO"        , szCARD_NO);
				recInTemp.setField("CAR_NO"         , szCAR_NO);
				recInTemp.setField("TRANS_ORD_DATE" , szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
				
//				ydUtils.putLog(szSessionName, szMethodName, "[CarMvHdSeEJBBean - procOutCarArrWr] TRANS_ORD_DATE ::"+szTRANS_ORD_DATE, 3);
//				ydUtils.putLog(szSessionName, szMethodName, "[CarMvHdSeEJBBean - procOutCarArrWr] TRANS_ORD_SEQNO ::"+szTRANS_ORD_SEQNO, 3);
				commPiUtils.printLog(szMethodName, "[CarMvHdSeEJBBean - procOutCarArrWr] TRANS_ORD_DATE  ::"+szTRANS_ORD_DATE , "SL");
				commPiUtils.printLog(szMethodName, "[CarMvHdSeEJBBean - procOutCarArrWr] TRANS_ORD_SEQNO ::"+szTRANS_ORD_SEQNO , "SL");
				rsTemp  = JDTORecordFactory.getInstance().createRecordSet("");
				//저장품조회 - 
				//if( szYD_CARLD_STOP_LOC.substring(0, 1).equals(YdConstant.YD_GP_PLATE_GDS_YARD)) {
				if( YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYD_CARLD_STOP_LOC.substring(0, 1)) 
					|| YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_CARLD_STOP_LOC.substring(0, 1)) ) { // - 2013.01.17 수정 (3기)
					
					/* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMtlCTransDTSeqCardNoAsc_PIDEV */
//PIDEV_S :병행가동용:PI_YD
					recInTemp.setField("PI_YD",    	szYD_CARLD_STOP_LOC.substring(0, 1));							
					intRtnVal = ydStockDao.getYdStock(recInTemp, rsTemp, 128);
					if(intRtnVal <=0 ){
						if(intRtnVal == 0){
							szMsg = "[CarMvHdSeEJBBean - procOutCarArrWr] getYdStock Error :: [" + szTRANS_ORD_DATE + "/"
							       + szTRANS_ORD_SEQNO +"] DO NOT EXIST";
							commPiUtils.printLog(szMethodName, szMsg , "SL");
							throw new DAOException("<CarMvHdSeEJBBean> procOutCarArrWr" + szMsg);
						}else{
							szMsg = "[CarMvHdSeEJBBean - procOutCarArrWr] getYdStock Error :: [" + intRtnVal + "]"+"PARAMETER ERROR" ;
							commPiUtils.printLog(szMethodName, szMsg , "SL");
							throw new DAOException("<CarMvHdSeEJBBean> procOutCarArrWr" + szMsg);
						}
					}
				}else{
					/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMtlCTransDTSeqCardNoDesc_PIDEV*/ 
					recInTemp.setField("YD_STK_COL_GP", 		"");
//PIDEV_S :병행가동용:PI_YD
					recInTemp.setField("PI_YD",    	szYD_CARLD_STOP_LOC.substring(0, 1));						
					intRtnVal = ydStockDao.getYdStock(recInTemp, rsTemp, 126);
				}
				
				if(intRtnVal > 0 ){
					rsTemp.absolute(1);
					recInTemp = rsTemp.getRecord();
					
					szYD_WRK_ALW_WT        = commPiUtils.trim(recInTemp.getFieldString("YD_MTL_WT_SUM"));
					szYD_MTL_SH            = commPiUtils.trim(recInTemp.getFieldString("YD_MTL_SH"));
					szTRANS_EQUIPMENT_TYPE = commPiUtils.trim(recInTemp.getFieldString("TRANS_EQUIPMENT_TYPE"));
					szYD_CAR_PROG_STAT 	   = commPiUtils.trim(recInTemp.getFieldString("YD_CAR_PROG_STAT"));
					
				}
				
				szMsg = "[CarMvHdSeEJBBean - procOutCarArrWr] 베드에 작업가능 중량["+ szYD_WRK_ALW_WT +"], 작업가능매수["+ szYD_MTL_SH +"-등록 미정] 설정 " ;
				commPiUtils.printLog(szMethodName, szMsg , "SL");
				/////////////////////////////////////////////////////////////////////////////////
				
				//적치베드 상태 활성화등록
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_BED_WT_MAX", "1"+szYD_WRK_ALW_WT);
				recInTemp.setField("YD_STK_COL_GP", szYD_CARLD_STOP_LOC);
				recInTemp.setField("YD_STK_BED_ACT_STAT", "L");
				
				intRtnVal = ydStkBedDao.updYdStkbedYdStkColGp(recInTemp, 0);
				if(intRtnVal <= 0) {
					szMsg = "[CarMvHdSeEJBBean - procOutCarArrWr] updYdStkbedYdStkColGp 적치베드 정보 활성화중 Error!! ";
					commPiUtils.printLog(szMethodName, szMsg , "SL");
					throw new DAOException("<CarMvHdSeEJBBean> updYdStkbedYdStkColGp" + szMsg);
				}
				
				//PDA하차 출하인 경우 생략
				if(("A".equals(szYD_CAR_PROG_STAT) || "B".equals(szYD_CAR_PROG_STAT)) && "P".equals(szTRANS_EQUIPMENT_TYPE)){
					szMsg= "[CarMvHdSeEJBBean - procOutCarArrWr] PDA하차 출하 인 경우 적치단 활성화 생략";
					commPiUtils.printLog(szMethodName, szMsg , "SL");
				}else{
					//적치단 활성화 2009.10.08 김진욱 수정 적치단활성상태는 'L' -> 'E'로 변경
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_STK_COL_GP", szYD_CARLD_STOP_LOC);
					recInTemp.setField("YD_STK_LYR_ACT_STAT", "E");
					recInTemp.setField("STL_NO", "");
					recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
			    	
					intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGp(recInTemp);
					if(intRtnVal <= 0) {
						szMsg = "[CarMvHdSeEJBBean - procOutCarArrWr] updYdStklyrYdStkColGp 적치단 정보 활성화중 Error!! ";
						commPiUtils.printLog(szMethodName, szMsg , "SL");
						throw new DAOException("<CarMvHdSeEJBBean> updYdStklyrYdStkColGp " + szMsg);
					}
				}
				
				
				szMsg= "[CarMvHdSeEJBBean - procOutCarArrWr] updYdStklyrYdStkColGp 적치단 활성화 처리";
				commPiUtils.printLog(szMethodName, szMsg , "SL");
				
				if(intRtnVal <= 0) {
					szMsg = "[CarMvHdSeEJBBean - procOutCarArrWr] updYdStklyrYdStkColGp 적치단  활성화상태 등록 중 Error";
					commPiUtils.printLog(szMethodName, szMsg , "SL");
					throw new DAOException(szMethodName + " : " + szMsg);
				}
				
				msgRecord.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
	    	}else{
	    		szMsg= "[CarMvHdSeEJBBean - procOutCarArrWr] getYdStkcol 개소코드와 야드포인트코드로 적치열 조회 실패";
	    		commPiUtils.printLog(szMethodName, szMsg , "SL");
				throw new DAOException(szMethodName + " : " + szMsg);
	    	}
	    } catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	    return intRtnVal;
	}

	/**	 
	 * [A] 오퍼레이션명 :후판대기장도착실적 송신(DMYDR061)- procStandByYdArrivePlate4G
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */	
	public void procM10LMYDJ1042(JDTORecord rcvMsg) throws DAOException { //S 
		String mthdNm = "[YdPlateL3RcvPISeEJB.procM10LMYDJ1042] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "T", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}					
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			String sOperationName		= "후판용 대기장도착실적(M10LMYDJ1042)";
			
			YdCarSchDao ydCarSchDao 	= new YdCarSchDao();	//차량스케줄DAO
			
			String sMsg 				= "";
			String sUNIQUE_ID           = null;
			String sRtnMsg				= null;
			
			JDTORecordSet rsResult		= null;
			JDTORecordSet rsGetStock 	= null;
			JDTORecord	  recInTemp		= null;
			JDTORecord    recStlNo 		= null;
			
			int intRtnVal 				= 0;
			
			int nTRANS_ORD_SEQNO = 0; // 전사물류개선프로젝트 2021.1.6 추가

			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
			String msgId             = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sYD_GP		     = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"             ));   // 야드구분
			String sCMBN_CARLD_YN    = commPiUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN"     ));   // 조합상차유무조합상차(시작:S , 종료: E ,  단일상차: N )
			String sWORK_GP          = commPiUtils.trim(rcvMsg.getFieldString("WORK_GP"           ));   // 작업구분
            String sTEL_NO           = commPiUtils.trim(rcvMsg.getFieldString("TEL_NO"      	  ));   // 전화번호
			String sDRIVER_NAME      = commPiUtils.trim(rcvMsg.getFieldString("DRIVER_NAME"       ));   // 운전기사명
            String sTRANS_ORD_DATE   = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"      ));   // 운송지시일자 
            String sTRANS_ORD_SEQNO  = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"   ));   // 운송지시순번 
            String sCAR_KIND         = commPiUtils.trim(rcvMsg.getFieldString("CAR_KIND"          ));   // 차량종류
            String sCAR_NO           = commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"            ));   // 차량번호
            String sSHIP_NAME        = commPiUtils.trim(rcvMsg.getFieldString("SHIP_NAME"         ));   // 선박명
            String sRENTPROC_CD      = commPiUtils.trim(rcvMsg.getFieldString("CRN_WRK_METHOD_GP" ));   // 크레인작업방법구분해송 줄거리작업 방법 M:마그네틱, T:줄거리
            String sWAIT_ARR_DDTT    = commPiUtils.trim(rcvMsg.getFieldString("WAIT_ARR_DDTT"     ));   // 대기장도착시간
            String sWAIT_ARR_GP      = commPiUtils.trim(rcvMsg.getFieldString("WAIT_ARR_GP"       ));   // 대기장도착구분
            String sCARLD_PNT_CD	 = commPiUtils.trim(rcvMsg.getFieldString("CARLD_PNT_CD"      ));
            
            String sModifier         = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));             //수정자(Backup Only)			 
            if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			mthdNm = msgId.substring(0, 2) + mthdNm;	
			
			// 2021. 4. 7 항목추가
            String sYD_FTMV_MEANS_GP = commPiUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP" ));   // 이송구분
            String sCR_FRTOMOVE_GP   = commPiUtils.trim(rcvMsg.getFieldString("TRANS_METHOD_GP"   ));   // 냉연이송구분 
			// 전사물류개선 2021. 1 6 전차량정차위치
			String sBefore_YD_CARLD_STOP_LOC = "";
            

			// 차량종류가 세분화되어 
//			TV	가변기
//			TS	슬라이더
//			VS	슬라이더+가변기
//			TR	일반
//			PT	Pallet
//PIDEV	if("TV".equals(sCAR_KIND) || "TS".equals(sCAR_KIND) || "VS".equals(sCAR_KIND)){
//				sCAR_KIND = "TR";
//			}
			
			if(sCAR_KIND.startsWith("P")){
				sCAR_KIND = "PT";
			} else if (sCAR_KIND.startsWith("T")){
				sCAR_KIND = "TR";
			} else if (sCAR_KIND.startsWith("C")){
				sCAR_KIND = "TR";
			}
			
			sMsg="["+sOperationName+"] sUNIQUE_ID:" + sUNIQUE_ID;
			commPiUtils.printLog(logId, sMsg , "SL");	
			
			if("".equals(sUNIQUE_ID)){
				sUNIQUE_ID = "0";
			}		
			
			//전문항목 에러체크
			if("".equals(sCAR_NO)){
				sMsg="[" + sOperationName + "] CAR_NO IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");	
				return ;
			}

			if("".equals(sTRANS_ORD_DATE)){
				sMsg="[" + sOperationName + "] TRANS_ORD_DT IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");	
				return ;
			}
			if("".equals(sTRANS_ORD_SEQNO)){
				sMsg="[" + sOperationName + "] TRANS_ORD_SEQNO IS NULL ("+msgId+")";
				commPiUtils.printLog(logId, sMsg , "SL");	
				return ;
			}
			
			//---------------------------------------------------------------------------------
			//	DEL_YN = 'N' 인 차량스케줄이 존재할경우 SKIP처리한다. 
			//---------------------------------------------------------------------------------
			JDTORecordSet rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DATE"	, sTRANS_ORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO"	, sTRANS_ORD_SEQNO);
			//recPara.setField("CARD_NO"			, sCARD_NO );
			recPara.setField("CAR_NO"			, sCAR_NO );
			recPara.setField("DEL_YN"			, "N" );
			
			intRtnVal = ydPICommDAO.selectT(recPara, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0087_PIDEV");
			if(intRtnVal > 0){
				sMsg= "["+sOperationName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				commPiUtils.printLog(logId, sMsg , "SL");	
				return ;
			}	
			
			if(!"".equals(sTEL_NO)) {
				//---------------------------------------------------------------------------------
				//	TB_YD_STOCK 에 TEL_NO 을 셋팅한다.
				//---------------------------------------------------------------------------------			
				recPara.setField("TRANS_ORD_DATE"	, sTRANS_ORD_DATE);
				recPara.setField("TRANS_ORD_SEQNO"	, sTRANS_ORD_SEQNO);
				recPara.setField("YD_STK_LOT_CD"	, sTEL_NO);
				/* com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0030 
				UPDATE TB_YD_STOCK
				SET    YD_STK_LOT_CD = :V_YD_STK_LOT_CD
				WHERE  TRANS_ORD_DATE = :V_TRANS_ORD_DATE
				AND    TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				*/
				intRtnVal = ydPICommDAO.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0030");
			}
			
			
			/*
			 *  복수동 출하시 나머지 출하작업에 대한 상태 = 'Y'
			 *  -- 권하처리시 선 복수동 작업일때 해당항목을 셋팅
			 */
//			sDOUBLEDONG_CHECK  		    = ydDaoUtils.paraRecChkNull(inRecord,"DOUBLEDONG_CHECK");
//			sDOUBLEDONG_YD_CAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(inRecord,"YD_CAR_SCH_ID"); 		
			String sDOUBLEDONG_CHECK  		 = commPiUtils.trim(rcvMsg.getFieldString("DOUBLEDONG_CHECK"   ));
			String sDOUBLEDONG_YD_CAR_SCH_ID = commPiUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"   ));    					
			

			//--------------------------------------------------------------------------------------
			// 이하 AS-IS 루틴 그대로 사용
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			String sYD_STK_COL_GP = "";
			String sWLOC_CD		  = "";	
			String sYD_PNT_CD	  = "";
			String sYD_CAR_SCH_ID = "";
			
//PIDEV			
//			if(sCARD_NO.startsWith("P")){ // PALLET 출하 
			if(sCAR_KIND.startsWith("P")){ // PALLET 출하				
				//차량포인트 테이블에서 WLOC_CD 와 YD_PNT_CD 를 읽어 온다. 
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				
				if("".equals(sCARLD_PNT_CD)){
					//출하포인트코드
					sCARLD_PNT_CD = commPiUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD"   ));
				}
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CARPNT_CD" , sCARLD_PNT_CD);
				/* com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0102 
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
				FROM   TB_YD_CARPOINT
				WHERE  YD_CARPNT_CD = :V_YD_CARPNT_CD
				*/
				intRtnVal = ydPICommDAO.selectT(recInTemp, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0102");
				
				if(intRtnVal <= 0){
					sMsg="["+sOperationName+"] PALLET 출하 차량포인트 조회 시 오류발생 - 반환값 : " + intRtnVal;
					commPiUtils.printLog(logId, sMsg , "SL");					
					return ;
				}
				
				rsResult.first();
				recInTemp = rsResult.getRecord();
				
				sWLOC_CD   	   = commPiUtils.trim(recInTemp.getFieldString("WLOC_CD"       ));
				sYD_PNT_CD 	   = commPiUtils.trim(recInTemp.getFieldString("YD_PNT_CD"     ));
				sYD_STK_COL_GP = commPiUtils.trim(recInTemp.getFieldString("YD_STK_COL_GP" ));
				
				/*
				 * 2. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
				 */
				sYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    		sYD_CAR_SCH_ID);
				recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
				recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
				recInTemp.setField("CAR_KIND", 				sCAR_KIND);							    //차량종류
				recInTemp.setField("SPOS_WLOC_CD",     		sWLOC_CD);								//발지개소코드
				recInTemp.setField("YD_PNT_CD1",     		sYD_PNT_CD);							//야드포인트코드1
				recInTemp.setField("CAR_NO",           		sCAR_NO);								//차량번호
//				recInTemp.setField("CARD_NO",          		sCARD_NO);								//카드번호
				recInTemp.setField("YD_CARLD_LEV_DT",  		commPiUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   		sTRANS_ORD_DATE);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  		sTRANS_ORD_SEQNO);						//운송지시순번
		    	recInTemp.setField("YD_CARLD_STOP_LOC",		sYD_STK_COL_GP);						//차량상차정지위치
		    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		"9");	                                //입동지시순번 --기본값으로 설정(9) **
				recInTemp.setField("YD_CAR_PROG_STAT", 		"1");				                    //상차출발상태
				recInTemp.setField("SHIP_NAME", 			sSHIP_NAME);							//선박명
				recInTemp.setField("RENTPROC_CD", 			sRENTPROC_CD);							//해송 줄거리작업 방법 M:마그네틱, T:줄거리
				recInTemp.setField("IF_SEQ_NO", 			sUNIQUE_ID);	                        // 운송지시 SEQ
				recInTemp.setField("TRANS_EQUIPMENT_TYPE", 	"PT");	                                // 세부차종
				recInTemp.setField("REGISTER",         		sModifier);
				
	    		//차량스케줄 등록
		    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
	    		if( intRtnVal <= 0 ){
					sMsg="[" + sOperationName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					commPiUtils.printLog(logId, sMsg , "SL");	
					return ;
	    		}
	    		
	    		sMsg="[" + sOperationName + "] 차량스케줄 생성 완료";
	    		commPiUtils.printLog(logId, sMsg , "SL");	
				/*
				 * 2016.06.28 윤 재광
				 * 상차 후 복수동으로 가기위해 장착할 경우 이 이벤트 발생
				 * 이 시점에 해당 포인트 예약처리 
				 */
				recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", sYD_STK_COL_GP);
		    	recInTemp.setField("YD_STK_COL_ACT_STAT", "R");					
				intRtnVal = ydPICommDAO.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0018");	
				
				recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_CARPNT_CD", sCARLD_PNT_CD);
		    	recInTemp.setField("YD_STK_COL_ACT_STAT", "R");					
				
				intRtnVal = ydPICommDAO.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
				
			
				// 2021. 4. 7 야드이송야드구분
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("TRANS_ORD_DATE"   , sTRANS_ORD_DATE);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO"  , sTRANS_ORD_SEQNO);						//운송지시순번
				recInTemp.setField("CAR_NO"           ,	sCAR_NO);								//차량번호
				recInTemp.setField("YD_FTMV_MEANS_GP" , sYD_FTMV_MEANS_GP);
				recInTemp.setField("CR_FRTOMOVE_GP"   , sCR_FRTOMOVE_GP);
				recInTemp.setField("MODIFIER"         , sModifier  );	//수정자
				ydPICommDAO.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateYdStockByFTMV");
				
				return ;
				
			}
			//-----------------------------------------------------------------------------------------------------------------
			// [전사물류시스템개선] 2021.1.6
			// - 차량입고(반품,회송,출고취소)의 경우엔 차량에 적재된 순서대로 처리
			// - 저장위치 및 차량포인트를 지정처리함 야드저장위치 및 포인트를 지정
			//-----------------------------------------------------------------------------------------------------------------
			else if(nTRANS_ORD_SEQNO>999000){
//PIDEV 반드시 확인 해야 함				
				sMsg="["+sOperationName+"] 차량입고(반품)은 차량정지위치를 지정되어 있기때문에(반품등록화면) 사용가능한 차량정지위치를 로직은 Pass한다.";
				commPiUtils.printLog(logId, sMsg , "SL");	
			}else{
				// 일반차량 출하
				/*
				 * 2. 저장품이 적치된 저장위치 정보를 조회 
				 */
				sMsg="[" + sOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 시작";
				commPiUtils.printLog(logId, sMsg , "SL");	
				//저장품 동 구하기 
				rsGetStock 	= JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP"   , "");
				//recInTemp.setField("CARD_NO", 		  sCARD_NO);
				recInTemp.setField("CAR_NO"          , sCAR_NO);
				recInTemp.setField("TRANS_ORD_DATE"  , sTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO" , sTRANS_ORD_SEQNO);
				recInTemp.setField("YD_GP"           , sYD_GP);
				
				//-----------------------------------------------------------------------------------------------------------------
				// [전사물류시스템개선]
				//  - 복수동일경우 상차우선순위에 의하여 쿼리를 분리처리한다.
				//
				//-----------------------------------------------------------------------------------------------------------------
				/* com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0089_PIDEV 
				SELECT 
				     SUBSTR(A.YD_STK_COL_GP,1,1)||ITEM||SPAN AS YD_STK_COL_GP,
				     NVL(CNT,0) AS CNT, 
				     ITEM , GBN
				FROM
				    (
				        SELECT 
				          A.ITEM,A.GBN,MIN(ITEM2) AS SPAN, MAX(YD_STK_COL_GP) AS YD_STK_COL_GP
				        FROM
				        (
				            SELECT 
				                REPR_CD_GP,CD_GP,ITEM,ITEM1,SUBSTR(YD_STK_COL_GP,3) AS ITEM2, YD_STK_COL_GP,
				                CASE WHEN SUBSTR(YD_STK_COL_GP,3,2) IN ('01','02','03') THEN 
				                -- 2후판인경우
				                    CASE WHEN ITEM1 >= SUBSTR(YD_STK_COL_GP,3) THEN '1' ELSE '2' END
				                ELSE
				                -- 1후판인경우
				                    CASE WHEN SUBSTR(B.YD_STK_COL_GP,2,1) IN ('E','F') THEN
				                         -- E,F동인경우
				                             CASE WHEN SUBSTR(YD_STK_COL_GP,3) <= ITEM_VALUE1 THEN '3' 
				                                  WHEN SUBSTR(YD_STK_COL_GP,3) <= ITEM2       THEN '5' 
				                                  ELSE '4' END
				                         ELSE
				                         -- E,F동이 아닌경우
				                            CASE WHEN ITEM2 >= SUBSTR(YD_STK_COL_GP,3) THEN '3' ELSE '4' END
				                    END
				                END AS GBN
				            FROM TB_YD_RULE A,
				                (
				                    SELECT DISTINCT
				                        B.YD_STK_COL_GP
				                    FROM TB_YD_STOCK A,
				                         TB_YD_STKLYR B
				                    WHERE A.STL_NO  = B.STL_NO
				                      AND A.CAR_NO         = :V_CAR_NO
				                      AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
				                      AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				                      AND B.YD_STK_LYR_MTL_STAT IN ('C','U')
				                      AND B.YD_STK_COL_GP LIKE NVL(:V_YD_GP,'_') ||'_0%'  -- 야드에 있는 대상재
				                )B
				            WHERE A.REPR_CD_GP  = SUBSTR(B.YD_STK_COL_GP,1,1) || '00031'
				              AND A.ITEM        = SUBSTR(B.YD_STK_COL_GP,2,1)
				        )A
				        GROUP BY  A.ITEM,A.GBN
				    )A,
				    (
				        SELECT YD_CARLD_STOP_LOC,SUBSTR(YD_CARLD_STOP_LOC,2,1) AS DONG ,SUBSTR(YD_CARLD_STOP_LOC,5,1) AS TONGRO ,COUNT(*) AS CNT
				        FROM
				        (
				            SELECT YD_CARLD_STOP_LOC FROM USRYDA.TB_YD_CARSCH  
				            WHERE DEL_YN = 'N'
				              AND SPOS_WLOC_CD = 'DWY26'
				        )A
				        GROUP BY A.YD_CARLD_STOP_LOC
				    )B
				WHERE A.ITEM = B.DONG(+)
				  AND A.GBN  = B.TONGRO(+)
				ORDER BY CNT  , 
				         YD_STK_COL_GP
				*/         
				String sDongSelectQuery = "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0089_PIDEV";
				if(PlateGdsYdUtil.isApplyYn("복수상차우선순위적용여부")){	
					
					JDTORecordSet rsMulti = JDTORecordFactory.getInstance().createRecordSet("");
					JDTORecord recMulti = JDTORecordFactory.getInstance().create();
					recMulti.setField("CAR_NO"          , sCAR_NO);
					recMulti.setField("TRANS_ORD_DATE"  , sTRANS_ORD_DATE);
					recMulti.setField("TRANS_ORD_SEQNO" , sTRANS_ORD_SEQNO);
					
					// 조합상차이거나, 운송지시가 C로시작하는것이라면, 거래처가 2곳 이상이라면
					/* com.inisteel.cim.yd.common.dao.YdPlateCommDAO.isCMBN_CARLD_YN_PIDEV 
					WITH V_DATA AS (
					    SELECT
					        :V_TRANS_ORD_DATE AS V_TRANS_ORD_DATE
					        , :V_TRANS_ORD_SEQNO AS V_TRANS_ORD_SEQNO
					        , :V_CAR_NO AS V_CAR_NO
					    FROM DUAL 
					)  
					/* 거래처 우선순위가 등록된 것 중 2개 이상인것 
					SELECT
					    TRANS_ORD_DATE
					    ,TRANS_ORD_SEQNO
					    ,CAR_NO
					FROM
					(
					    SELECT
					          A.TRANS_ORD_DATE
					        , A.TRANS_ORD_SEQNO
					        , A.CAR_NO
					        , COUNT(DISTINCT A.YD_CAR_UPP_LOC_CD) AS UPP_LOC
					    FROM TB_YD_STOCK A, V_DATA B
					    WHERE  A.TRANS_ORD_DATE = B.V_TRANS_ORD_DATE
					    AND A.TRANS_ORD_SEQNO = B.V_TRANS_ORD_SEQNO
					    AND A.CAR_NO = B.V_CAR_NO 
					    GROUP BY 
					          A.TRANS_ORD_DATE
					        , A.TRANS_ORD_SEQNO
					        , A.CAR_NO
					) 
					WHERE UPP_LOC > 1
					UNION ALL
					/* 상차지가 2개 이상인 것
					SELECT
					      NULL
					    , NULL
					    , NULL 
					FROM
					(
					        SELECT COUNT(DISTINCT Y.GATE) AS PT_LOAD_LOC_CNT 
					        FROM   (      
					                SELECT 
					                       CASE WHEN T.SPAN||T.RYUL <= (CASE WHEN T.SPAN IN ('01','02','03')THEN ITEM1 ELSE ITEM2 END) 
					                            THEN T.YARD||T.DONG||'PT'||( CASE WHEN T.SPAN IN ('01','02','03') THEN '1' ELSE '3' END )||'1' 
					                            ELSE T.YARD||T.DONG||'PT'||( CASE WHEN T.SPAN IN ('01','02','03') THEN '2' ELSE '4' END )||'1' 
					                       END AS GATE1
					                       
					                      ,CASE WHEN T.SPAN IN ('01','02','03') THEN
					                            -- 2후판일 경우
					                                CASE WHEN T.SPAN||T.RYUL <= ITEM1
					                                     THEN T.YARD||T.DONG||'PT'||'11'  
					                                     ELSE T.YARD||T.DONG||'PT'||'21'  
					                                END
					                            ELSE
					                            -- 1후판일 경우
					                                CASE WHEN T.DONG IN ('E','F') THEN
					                                     -- E,F동일 경우
					                                        CASE WHEN T.SPAN||T.RYUL <= ITEM_VALUE1
					                                             THEN T.YARD||T.DONG||'PT'||'31'  
					                                             WHEN T.SPAN||T.RYUL <= ITEM2
					                                             THEN T.YARD||T.DONG||'PT'||'51'  
					                                             ELSE T.YARD||T.DONG||'PT'||'41'  
					                                        END
					                                     ELSE
					                                     -- E,F동이 아닐 경우
					                                        CASE WHEN T.SPAN||T.RYUL <= ITEM2
					                                             THEN T.YARD||T.DONG||'PT'||'31'  
					                                             ELSE T.YARD||T.DONG||'PT'||'41'  
					                                        END
					                                END
					                       END AS GATE     
					                FROM    (  
					                           SELECT 
					                                SUBSTR(A.YD_STK_COL_GP,1,1) AS YARD,
					                                SUBSTR(A.YD_STK_COL_GP,2,1) AS DONG,
					                                SUBSTR(A.YD_STK_COL_GP,3,2) AS SPAN,
					                                SUBSTR(A.YD_STK_COL_GP,5,2) AS RYUL,
					                                A.YD_STK_COL_GP             AS YD_STK_COL_GP
					                           FROM (
					                                SELECT  /*+ ORDERED 
					                                    C.YD_STK_COL_GP 
					                                FROM TB_YD_STOCK A, V_DATA B, TB_YD_STKLYR C
					                                WHERE  A.TRANS_ORD_DATE = B.V_TRANS_ORD_DATE
					                                AND A.TRANS_ORD_SEQNO = B.V_TRANS_ORD_SEQNO
					                                AND A.CAR_NO = B.V_CAR_NO 
					                                AND A.STL_NO = C.STL_NO
					                                AND C.YD_STK_LYR_MTL_STAT IN ('C','U')
					                                AND (C.YD_STK_COL_GP LIKE '__0___%'  OR C.YD_STK_COL_GP LIKE '__CR__%' ) 
					                           )A
					                       ) T,
					                       TB_YD_RULE A
					               WHERE   A.REPR_CD_GP = 'T00031'
					                 AND   A.ITEM       = T.DONG
					               ) Y
					              ,TB_YD_STKCOL X
					              ,TB_YD_CARPOINT Z
					        WHERE  Y.GATE = X.YD_STK_COL_GP
					          AND  Y.GATE = Z.YD_STK_COL_GP  
					)
					WHERE PT_LOAD_LOC_CNT > 1
					UNION ALL
					/* 조합상차여부가 S, E 인것들 
					SELECT
					       A.TRANS_ORD_DATE
					     , A.TRANS_ORD_SEQNO
					     , A.CAR_NO
					FROM TB_YD_CARSCH A, V_DATA B
					WHERE A.TRANS_ORD_DATE = B.V_TRANS_ORD_DATE
					AND A.TRANS_ORD_SEQNO = B.V_TRANS_ORD_SEQNO
					AND A.CAR_NO = B.V_CAR_NO 
					AND A.DEL_YN = 'N'
					AND A.CMBN_CARLD_YN IN ('S','E')
				*/	
					if( ydPICommDAO.selectT(recMulti, rsMulti, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.isCMBN_CARLD_YN_PIDEV") > 0){
						// 동 결정순위 설정
						recInTemp.setField("DOUBLEDONG_YD_CAR_SCH_ID" ,	sDOUBLEDONG_YD_CAR_SCH_ID);
						sDongSelectQuery = "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getSelectDongByCMBN_CARLD_PIDEV";
					}
				}
				intRtnVal = ydPICommDAO.selectT(recInTemp, rsGetStock, sDongSelectQuery);			
    			
				if(intRtnVal <= 0){
					return ;
				}
				
				sMsg="[" + sOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 성공 - 건수["+intRtnVal+"]";
				commPiUtils.printLog(logId, sMsg , "SL");	
				
				rsGetStock.first();
				recStlNo = rsGetStock.getRecord();
				sYD_STK_COL_GP = commPiUtils.trim(recStlNo.getFieldString("YD_STK_COL_GP")); 
				
				// 동우선순위쿼리가 Select되었다면 이전 정차위치를 구한다.
				// 동일한 정차위치일경우 바로 도착처리모듈을 호출하기 위함
				// 만약 차량형상 사용유무가 Y일 경우엔 형상유무와 상관없이 크레인스케쥴을 편성한다.
				
				if("com.inisteel.cim.yd.common.dao.YdPlateCommDao.getSelectDongByCMBN_CARLD_PIDEV".equals(sDongSelectQuery)){
					sBefore_YD_CARLD_STOP_LOC = commPiUtils.trim(recStlNo.getFieldString("BF_YD_CARLD_STOP_LOC"));
				}
				
				sMsg="[" + sOperationName + "] 차량정지위치를 구하기 위한 대상재가 존재하는 동["+sYD_STK_COL_GP+"]";
				commPiUtils.printLog(logId, sMsg , "SL");	
				
				//-----------------------------------------------------------------------------------------------------------------
				//	사용가능한 차량정지위치로 적치열 조회 후 개소POINT를 구함
				//-----------------------------------------------------------------------------------------------------------------
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP",sYD_STK_COL_GP);
				
				/* com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0133 (입동포인트검색) 
				SELECT Y.GATE           AS YD_CARLD_STOP_LOC
				      ,X.WLOC_CD        AS WLOC_CD
				      ,X.YD_PNT_CD      AS YD_PNT_CD
				      ,Z.YD_CARPNT_CD   AS YD_CARPNT_CD
				FROM   (      
				        SELECT 
				               CASE WHEN T.SPAN||T.RYUL <= (CASE WHEN T.SPAN IN ('01','02','03')THEN ITEM1 ELSE ITEM2 END) 
				                    THEN T.YARD||T.DONG||'PT'||( CASE WHEN T.SPAN IN ('01','02','03') THEN '1' ELSE '3' END )||'1' 
				                    ELSE T.YARD||T.DONG||'PT'||( CASE WHEN T.SPAN IN ('01','02','03') THEN '2' ELSE '4' END )||'1' 
				               END AS GATE1
				               
				              ,CASE WHEN T.SPAN IN ('01','02','03') THEN
				                    -- 2후판일 경우
				                        CASE WHEN T.SPAN||T.RYUL <= ITEM1
				                             THEN T.YARD||T.DONG||'PT'||'11'  
				                             ELSE T.YARD||T.DONG||'PT'||'21'  
				                        END
				                    ELSE
				                    -- 1후판일 경우
				                        CASE WHEN T.DONG IN ('E','F') THEN
				                             -- E,F동일 경우
				                                CASE WHEN T.SPAN||T.RYUL <= ITEM_VALUE1
				                                     THEN T.YARD||T.DONG||'PT'||'31'  
				                                     WHEN T.SPAN||T.RYUL <= ITEM2
				                                     THEN T.YARD||T.DONG||'PT'||'51'  
				                                     ELSE T.YARD||T.DONG||'PT'||'41'  
				                                END
				                             ELSE
				                             -- E,F동이 아닐 경우
				                                CASE WHEN T.SPAN||T.RYUL <= ITEM2
				                                     THEN T.YARD||T.DONG||'PT'||'31'  
				                                     ELSE T.YARD||T.DONG||'PT'||'41'  
				                                END
				                        END
				               END AS GATE     
				        FROM   TB_YD_RULE, 
				               (  
				                   SELECT 
				                        SUBSTR(A.YD_STK_COL_GP,1,1) AS YARD,
				                        SUBSTR(A.YD_STK_COL_GP,2,1) AS DONG,
				                        SUBSTR(A.YD_STK_COL_GP,3,2) AS SPAN,
				                        SUBSTR(A.YD_STK_COL_GP,5,2) AS RYUL,
				                        A.YD_STK_COL_GP             AS YD_STK_COL_GP
				                   FROM (SELECT :V_YD_STK_COL_GP AS YD_STK_COL_GP FROM DUAL)A
				               )T
				       WHERE   REPR_CD_GP = 'T00031'
				         AND   ITEM       = T.DONG
				       ) Y
				      ,TB_YD_STKCOL X
				      ,TB_YD_CARPOINT Z
				WHERE  Y.GATE = X.YD_STK_COL_GP    
				  AND  Y.GATE = Z.YD_STK_COL_GP    
				  */
				intRtnVal = ydPICommDAO.selectT(recInTemp, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0133");		
				
				if( intRtnVal <= 0) {
					sMsg="["+sOperationName+"] 사용가능한 개소POINT 조회 시 오류발생 - 메세지 : ";
					commPiUtils.printLog(logId, sMsg , "SL");	
					return ;
				}
				
				rsResult.first();
				recInTemp		= rsResult.getRecord();
				
				sWLOC_CD	   = commPiUtils.trim(recInTemp.getFieldString("WLOC_CD")); //2013.11.14 복수 창고 출하시 개소코드 변경하기 위하여 추가				
				sYD_PNT_CD	   = commPiUtils.trim(recInTemp.getFieldString("YD_PNT_CD"));
				sYD_STK_COL_GP = commPiUtils.trim(recInTemp.getFieldString("YD_CARLD_STOP_LOC"));
				
				sMsg="["+sOperationName+"] 사용가능한 차량정지위치["+sYD_STK_COL_GP+"]로 개소POINT 조회 완료 - 메세지 : " + intRtnVal;
				commPiUtils.printLog(logId, sMsg , "SL");	
			
			}
			//-----------------------------------------------------------------------------------------------------------------
			
			if("Y".equals(sDOUBLEDONG_CHECK)) { //복수동일 경우 
				// [전사물류시스템개선]
				// 2021.1.6
				//  - 복수동상차와 복수동 하차를 사용하도록 수정
				//  - nTRANS_ORD_SEQNO번호가 999000 보다 큰 건은 차량입고(반품,회송,출고취소)
				//-----------------------------------------------------------------------------------------------------------------
				// 복수동 상차
				if( nTRANS_ORD_SEQNO < 999000){
					/*
					 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 초기화 수정한다.
					 */
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID",    		sDOUBLEDONG_YD_CAR_SCH_ID);
					recInTemp.setField("REGISTER",         		sModifier);
					recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
					recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
					recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
					recInTemp.setField("SPOS_WLOC_CD",     		sWLOC_CD);								//발지개소코드
					recInTemp.setField("YD_PNT_CD1",     		sYD_PNT_CD);							//야드포인트코드1
					recInTemp.setField("CAR_NO",           		sCAR_NO);								//차량번호
//					recInTemp.setField("CARD_NO",          		sCARD_NO);								//카드번호
					recInTemp.setField("CARD_NO",          		"");								    //카드번호
					recInTemp.setField("YD_CARLD_LEV_DT",  		commPiUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
					recInTemp.setField("TRANS_ORD_DATE",   		sTRANS_ORD_DATE);						//운송지시일자
					recInTemp.setField("TRANS_ORD_SEQNO",  		sTRANS_ORD_SEQNO);						//운송지시순번
			    	recInTemp.setField("YD_CARLD_STOP_LOC",		sYD_STK_COL_GP);						//차량상차정지위치
			    	recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
			    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		"2");									//입동지시순번  (2013.06.20 cho 1순위 --> 2순위 변경)
					recInTemp.setField("DEL_YN", 				"N");		
					
					 //2025.07.31 임진후기사 요청 RITM1277126
                    //복수동 5순위로 순위 조정
                    String sApplyYnPI = ydPICommDAO.ApplyYnPI("", sOperationName, "APP060", "T", "020");//후판 개발 적용여부
                    
					if("Y".equals(sApplyYnPI)){
						sMsg="[" + sOperationName + "] 복수동차량 2순위 -> 5순위 조정";
			    		commPiUtils.printLog(logId, sMsg , "SL");	
						recInTemp.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DOUBLE);	
                        
                    }
					
					//차량스케줄수정
			    	intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
			    	
		    		if( intRtnVal <= 0 ){
						sMsg="[" + sOperationName + "] 차량스케줄 수정 시 오류발생[반환값 : " + intRtnVal + "]";
						commPiUtils.printLog(logId, sMsg , "SL");	
						return ;
		    		}
		    		
		    		//차량 스케쥴재료수정
		    		YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao(); 
		        	intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(recInTemp, 1) ;
		            
		    		sMsg="[" + sOperationName + "] 차량스케줄 수정 완료";
		    		commPiUtils.printLog(logId, sMsg , "SL");	
					
					// 복수동상차우선순위에 의하여 동일한 정지위치에서 작업예약이 2번 발생할경우
					// 차량형상유무 상관없이 바로 도착처리하기 위함
					 
					if(PlateGdsYdUtil.isApplyYn("복수상차우선순위적용여부")){
						
						if(sBefore_YD_CARLD_STOP_LOC.equals(sYD_STK_COL_GP)){
							// YD_CARLD_SCH_REQ_GP 스케쥴요청구분컬럼에 형상PASS 여부를 업데이트한다. 
							// YD_CARLD_SCH_REQ_GP 컬럼은 과거 공대차구분(대차에서 씌임)
							// 운영계확인결과 TB_YD_CARSCH에 사용되고 있지 않음(NULL값임)
							sMsg="[" + sOperationName + "] 전과 동일한 차량정지위치임 이전["+sBefore_YD_CARLD_STOP_LOC+"]현재["+sYD_STK_COL_GP+"]";
							commPiUtils.printLog(logId, sMsg , "SL");	
						
							JDTORecord jtoUpdate = JDTORecordFactory.getInstance().create();
							jtoUpdate.setField("YD_CAR_SCH_ID"       , sDOUBLEDONG_YD_CAR_SCH_ID);
							jtoUpdate.setField("YD_CARLD_SCH_REQ_GP" , "Y");
							jtoUpdate.setField("MODIFIER"            , sModifier); 
							ydPICommDAO.update(jtoUpdate, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCarschByFrmYnPassYn");

						}
					}
		    		
				}
				// 차량입고(반품) 복수동 하차
				else{
        			JDTORecord params = JDTORecordFactory.getInstance().create();
        			JDTORecordSet rsCarStopLoc = JDTORecordFactory.getInstance().createRecordSet("");
        			params.setField("YD_CAR_SCH_ID",    		sDOUBLEDONG_YD_CAR_SCH_ID);
					if( ydPICommDAO.selectT(params, rsCarStopLoc, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCarUnloadStopLoc") > 0){
						
						recInTemp = JDTORecordFactory.getInstance().create();
						sYD_STK_COL_GP = rsCarStopLoc.getRecord(0).getFieldString("YD_CARUD_STOP_LOC");
						recInTemp.setField("YD_PNT_CD3",     				rsCarStopLoc.getRecord(0).getFieldString("YD_PNT_CD"));
						recInTemp.setField("YD_CARUD_STOP_LOC",     		sYD_STK_COL_GP);
						
						recInTemp.setField("YD_CAR_SCH_ID",    		sDOUBLEDONG_YD_CAR_SCH_ID);
						recInTemp.setField("REGISTER",         		sModifier);
						recInTemp.setField("YD_EQP_WRK_STAT",  		"L");									//야드설비작업상태
						recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
						recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
						recInTemp.setField("YD_CARUD_LEV_DT",  		commPiUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
						recInTemp.setField("TRANS_ORD_DATE",   		sTRANS_ORD_DATE);						//운송지시일자
						recInTemp.setField("TRANS_ORD_SEQNO",  		sTRANS_ORD_SEQNO);						//운송지시순번
				    	recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARUD_LEV);				//상차출발상태
				    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		"2");									//입동지시순번  (2013.06.20 cho 1순위 --> 2순위 변경)
				    	recInTemp.setField("DEL_YN", 				"N");
						
				    	 //2025.07.31 임진후기사 요청 RITM1277126
	                    //복수동 5순위로 순위 조정
	                    String sApplyYnPI = ydPICommDAO.ApplyYnPI("", sOperationName, "APP060", "T", "020");//후판 개발 적용여부
	                    
						if("Y".equals(sApplyYnPI)){
							sMsg="[" + sOperationName + "] 복수동차량 2순위 -> 5순위 조정";
				    		commPiUtils.printLog(logId, sMsg , "SL");	
							recInTemp.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DOUBLE);	
	                        
	                    }
				    	
						//차량스케줄수정
				    	intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);

				    	// DEL_YN = 'Y' 처리한다.
	        			params = JDTORecordFactory.getInstance().create();
	        			params.setField("YD_CAR_SCH_ID", sDOUBLEDONG_YD_CAR_SCH_ID);
	        			ydPICommDAO.update(params,  "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updYdCarftmvmtlByCaudStopLoc");
 					}
				}

				
			} else { //복수동이 아닐 경우  

				/*
				 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
				 */
				sYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    		sYD_CAR_SCH_ID);
				recInTemp.setField("REGISTER",         		sModifier);
				recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
				recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
				recInTemp.setField("CAR_KIND", 				StringHelper.evl(sCAR_KIND, "TR"));
				recInTemp.setField("SPOS_WLOC_CD",     		sWLOC_CD);								//발지개소코드
				recInTemp.setField("YD_PNT_CD1",     		sYD_PNT_CD);							//야드포인트코드1
				recInTemp.setField("CAR_NO",           		sCAR_NO);								//차량번호
//PIDEV					recInTemp.setField("CARD_NO",          		sCARD_NO);								//카드번호
				recInTemp.setField("CARD_NO",          		"");								//카드번호
				recInTemp.setField("YD_CARLD_LEV_DT",  		commPiUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   		sTRANS_ORD_DATE);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  		sTRANS_ORD_SEQNO);						//운송지시순번
		    	recInTemp.setField("YD_CARLD_STOP_LOC",		sYD_STK_COL_GP);						//차량상차정지위치
	    		recInTemp.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 --기본값으로 설정(9) **
				recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
				recInTemp.setField("IF_SEQ_NO", 			sUNIQUE_ID);							//운송지시 SEQ
				
				recInTemp.setField("YD_CAR_WRK_GP",    		sWORK_GP);
				recInTemp.setField("TEL_NO", 		   		sTEL_NO);								//기사핸드폰번호
				recInTemp.setField("CMBN_CARLD_YN",    		sCMBN_CARLD_YN);						//첫번째 도착창고 : S 두번째 도착창고 : E
				recInTemp.setField("WAIT_ARR_DDTT",    		sWAIT_ARR_DDTT);						//대기장도착시간
				recInTemp.setField("WAIT_ARR_GP",      		sWAIT_ARR_GP);							//대기장도착구분  - B:BACKUP , S:SMARTPHONE		
				
				
				// 전사물류개선 2021. 1. 6
				// 길이 폭으로 차종을 구한다.
				// --가변슬라이드 : 폭이 3400 초과 && 길이 14,000 초과
				// --가변차량 : 폭이 3400 초과 && 길이 14,000 이하
				// --일반 : 폭이 3400 이하 && 길이 14,000 이하
				// --일반슬라이드 : 폭이 3400 이하 && 길이 14,000 초과
				JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
				JDTORecordSet rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
				String sCarKind = "";

				if(sCAR_KIND.startsWith("P")){
					sCarKind = "PT";
				} else if (sCAR_KIND.startsWith("T")){
					sCarKind = "TR";
				} else if (sCAR_KIND.startsWith("C")){
					sCarKind = "TR";
				} else{
					recInTemp1.setField("TRANS_ORD_DATE"  , sTRANS_ORD_DATE);
					recInTemp1.setField("TRANS_ORD_SEQNO" , sTRANS_ORD_SEQNO);
					recInTemp1.setField("CAR_NO"          , sCAR_NO );
					if(ydPICommDAO.selectT(recInTemp1, rsResult2, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCarKindByWdithLength")>0){
						sCarKind =  rsResult2.getRecord(0).getFieldString("CAR_KIND");
					}
					else{
						sCarKind = "TR";
					}
				}	
				
				recInTemp.setField("TRANS_EQUIPMENT_TYPE", 	sCarKind);	// 세부차종
//PIDEV 추가
//tr이송건					
				recInTemp.setField("YD_WRK_PROG_STAT"    , "1");//1:일반  9:tr이송							
//김기태 부장님	01/13		
				recInTemp.setField("SHIP_NAME"           , sSHIP_NAME);							//선박명
	    		//차량 스케줄 등록
		    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
	    		if( intRtnVal <= 0 ){
					sMsg="[" + sOperationName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					commPiUtils.printLog(logId, sMsg , "SL");
					return ;
	    		}
	    		
				// YD_STOCK 차량TYPE정보 Set				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CONVEYOR_BRANCH_CD" , sCarKind); 
				recInTemp.setField("YD_CAR_SCH_ID"         , sYD_CAR_SCH_ID); 
				recInTemp.setField("MODIFIER"              , sModifier  );	//수정자
				ydPICommDAO.update(recInTemp, "com.inisteel.cim.yd.dao.ydstockdao.updYdCarTransType");
				
				// 2021. 4. 7
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("TRANS_ORD_DATE"     , sTRANS_ORD_DATE);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO"    , sTRANS_ORD_SEQNO);						//운송지시순번
				recInTemp.setField("CAR_NO"             , sCAR_NO);								//차량번호
				recInTemp.setField("YD_FTMV_MEANS_GP"   , sYD_FTMV_MEANS_GP);
				recInTemp.setField("CR_FRTOMOVE_GP"     , sCR_FRTOMOVE_GP);
				recInTemp.setField("MODIFIER"           , sModifier  );	//수정자
				ydPICommDAO.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateYdStockByFTMV");
				
	    		sMsg="[" + sOperationName + "] 차량스케줄 생성 완료";
	    		commPiUtils.printLog(logId, sMsg , "SL");
			}

			/*
			 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
			 */
		
			sMsg="[" + sOperationName + "] 차량정지위치[" + sYD_STK_COL_GP + "], 차량스케줄ID[" + sYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 시작";
			commPiUtils.printLog(logId, sMsg , "SL");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("TC_CODE"			, "YDYDJ633");
			recInTemp.setField("TC_CREATE_DDTT"		, commPiUtils.getCurDate("yyyyMMddHHmmss"));
			recInTemp.setField("YD_CAR_STOP_LOC"	, sYD_STK_COL_GP);
			recInTemp.setField("YD_CAR_SCH_ID"		, sYD_CAR_SCH_ID);
			recInTemp.setField("CALL_PGM"			, "SANGCHA");
			
			//----------------------------------------------------------------------------------
			//	동기화 문제로 인하여 JMS --> EJB Call로 변경
			//----------------------------------------------------------------------------------
//PIDEV 반드시 확인			
			EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
			sRtnMsg = (String) ejbConn.trx("procCarBayInOrdReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
			
			//----------------------------------------------------------------------------------
			sMsg="[" + sOperationName + "] 차량정지위치[" + sYD_STK_COL_GP + "], 차량스케줄ID[" + sYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 성공 - 메세지 : " + sRtnMsg;
			commPiUtils.printLog(logId, sMsg , "S-");
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}

	/**	 
	 * [A] 오퍼레이션명 : 복수동 파렛트 장착 출발
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */	
	public void rcvM10LMYDJ1192(JDTORecord rcvMsg) throws DAOException {  //S 
		String mthdNm = "[YdPlateL3RcvPISeEJB.rcvM10LMYDJ1192] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			String sOperationName		= "후판용 복수동 파렛트 장착출발(M10LMYDJ1192)";
			String sMsg = "";
			
			YdCarSchDao ydCarSchDao 	= new YdCarSchDao();	//차량스케줄DAO
			
			//수신 항목 값
			String msgId  = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번

			/********************************
			 * 수신 전문 
			 ********************************/
            String sYD_GP            = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"      ));   // 운송지시일자 
            String sTRANS_ORD_DT     = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"      ));   // 운송지시일자 
            String sTRANS_ORD_SEQNO  = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"   ));   // 운송지시순번 
            String sCAR_NO           = commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"            ));   // 차량번호
            String sYD_CARPNT_CD     = commPiUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD"      ));   // 출하포인트코드
            String sYD_FTMV_MEANS_GP = commPiUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP" ));   // 이송구분
            String sCR_FRTOMOVE_GP   = commPiUtils.trim(rcvMsg.getFieldString("TRANS_METHOD_GP"   ));   // 냉연이송구분 

            
            String sModifier         = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));             //수정자(Backup Only)
            if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }

	    		    	
	    	if(sTRANS_ORD_DT.equals("")) {
				sMsg="운송지시일자가 없습니다.";
				commPiUtils.printLog(logId, sMsg , "SL");
				throw new DAOException("TRANS_ORD_DT Error");
	    	}
	    	if(sTRANS_ORD_SEQNO.equals("")) {
				sMsg="운송지시순번이 없습니다.";
				commPiUtils.printLog(logId, sMsg , "SL");
				throw new DAOException("TRANS_ORD_SEQNO Error");
	    	}
	    	if(sCAR_NO.equals("")) {
				sMsg="차량번호가 없습니다.";
				commPiUtils.printLog(logId, sMsg , "SL");
				throw new DAOException("CAR_NO Error");
	    	}
	    	if(sYD_CARPNT_CD.equals("")) {
				sMsg="포인트가 없습니다.";
				commPiUtils.printLog(logId, sMsg , "SL");
				throw new DAOException("sYD_CARPNT_CD Error");
	    	}	    	
	    	
	    	String sCarSchId = "";
	    	String sCarKind  = "";
			//---------------------------------------------------------------------------------
			//	DEL_YN = 'N' 인 차량스케줄이 존재할경우 SKIP처리한다. 
			//---------------------------------------------------------------------------------
			JDTORecordSet jsCarResult = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord jrParam 	= JDTORecordFactory.getInstance().create();
			jrParam.setField("TRANS_ORD_DATE"	, sTRANS_ORD_DT);
			jrParam.setField("TRANS_ORD_SEQNO"	, sTRANS_ORD_SEQNO);
			jrParam.setField("CAR_NO"			, sCAR_NO );
			jrParam.setField("DEL_YN"			, "N" );
			
			int intRtnVal = ydPICommDAO.selectT(jrParam, jsCarResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0087_PIDEV");
			if(intRtnVal <= 0){
				sMsg= "["+sOperationName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				throw new DAOException("해당 차량 스케쥴이 없습니다. sTRANS_ORD_DT:"+ sTRANS_ORD_DT + " TRANS_ORD_SEQNO:"+sTRANS_ORD_SEQNO );
			} else {
				sCarSchId = commPiUtils.trim(jsCarResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
				sCarKind  = commPiUtils.trim(jsCarResult.getRecord(0).getFieldString("CAR_KIND"));
			}
			
	    	/******************************************
	    	 *  복수 P 출발위치 CLEAR 
	    	 ******************************************/	
			JDTORecord recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TC_CODE",        		"M10LMYDJ1082");									//전문코드
			recPara.setField("YD_GP", 				"T");
			recPara.setField("CAR_NO", 				sCAR_NO);			
			recPara.setField("YD_CARPNT_CD", 		sYD_CARPNT_CD);
			recPara.setField("TRANS_ORD_DT", 		sTRANS_ORD_DT);
			recPara.setField("TRANS_ORD_SEQNO", 	sTRANS_ORD_SEQNO);
			
			commPiUtils.printLog(logId, "차량번호[" + sCAR_NO + "]는 자동으로 후판차량출발 모듈 EJB 호출 시작", "SL");
			
			EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
			ejbConn.trx("procPlGdsDistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recPara });
			
			commPiUtils.printLog(logId, "차량번호[" + sCAR_NO + "]는 자동으로 후판차량출발 모듈 EJB 호출 완료", "SL");
	    	
	    	
	    	/******************************************
	    	 *  도착예정동 입동 지시처리
	    	 *  1.작업대상선정
	    	 *  2.해당포인트 작업가능여부 check 하여 입동지시 송신
	    	 *    2.1 작업  
	    	 ******************************************/
			/*
			 *  복수동 출하시 나머지 출하작업에 대한 상태 = 'Y'
			 *  -- 권하처리시 선 복수동 작업일때 해당항목을 셋팅
			 */
			String sDOUBLEDONG_CHECK  		 = "Y";
		//	String sDOUBLEDONG_YD_CAR_SCH_ID = sCarSchId ;    					
			
			
			String sYD_STK_COL_GP = "";
			String sWLOC_CD		  = "";	
			String sYD_PNT_CD	  = "";
			//String sYD_CAR_SCH_ID = "";
			

			// 일반차량 출하
			/*
			 * 2. 저장품이 적치된 저장위치 정보를 조회 
			 */
			sMsg="[" + sOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 시작";
			commPiUtils.printLog(logId, sMsg , "SL");	
			//저장품 동 구하기 
			JDTORecordSet jsGetStock 	= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("DOUBLEDONG_YD_CAR_SCH_ID" , sCarSchId);
			recInTemp.setField("CAR_NO"                   , sCAR_NO);
			recInTemp.setField("TRANS_ORD_DATE"           , sTRANS_ORD_DT);
			recInTemp.setField("TRANS_ORD_SEQNO"          , sTRANS_ORD_SEQNO);
			
			intRtnVal = ydPICommDAO.selectT(recInTemp, jsGetStock, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getSelectDongByCMBN_CARLD_PIDEV");			
			
			if(intRtnVal <= 0){
				throw new DAOException("파렛트 복수동 error sTRANS_ORD_DT:"+ sTRANS_ORD_DT + " TRANS_ORD_SEQNO:"+sTRANS_ORD_SEQNO );
			}
			
			sMsg="[" + sOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 성공 - 건수["+intRtnVal+"]";
			commPiUtils.printLog(logId, sMsg , "SL");	
			
			sYD_STK_COL_GP            = commPiUtils.trim(jsGetStock.getRecord(0).getFieldString("YD_STK_COL_GP"));
			sMsg="[" + sOperationName + "] 차량정지위치를 구하기 위한 대상재가 존재하는 동["+sYD_STK_COL_GP+"]";
			commPiUtils.printLog(logId, sMsg , "SL");	

			
			//-----------------------------------------------------------------------------------------------------------------
			//	사용가능한 차량정지위치로 적치열 조회 후 개소POINT를 구함
			//-----------------------------------------------------------------------------------------------------------------
			JDTORecordSet rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_STK_COL_GP" , sYD_STK_COL_GP);
			
			/* com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0133 (입동포인트검색) 
			SELECT Y.GATE           AS YD_CARLD_STOP_LOC
			      ,X.WLOC_CD        AS WLOC_CD
			      ,X.YD_PNT_CD      AS YD_PNT_CD
			      ,Z.YD_CARPNT_CD   AS YD_CARPNT_CD
			FROM   (      
			        SELECT 
			               CASE WHEN T.SPAN||T.RYUL <= (CASE WHEN T.SPAN IN ('01','02','03')THEN ITEM1 ELSE ITEM2 END) 
			                    THEN T.YARD||T.DONG||'PT'||( CASE WHEN T.SPAN IN ('01','02','03') THEN '1' ELSE '3' END )||'1' 
			                    ELSE T.YARD||T.DONG||'PT'||( CASE WHEN T.SPAN IN ('01','02','03') THEN '2' ELSE '4' END )||'1' 
			               END AS GATE1
			               
			              ,CASE WHEN T.SPAN IN ('01','02','03') THEN
			                    -- 2후판일 경우
			                        CASE WHEN T.SPAN||T.RYUL <= ITEM1
			                             THEN T.YARD||T.DONG||'PT'||'11'  
			                             ELSE T.YARD||T.DONG||'PT'||'21'  
			                        END
			                    ELSE
			                    -- 1후판일 경우
			                        CASE WHEN T.DONG IN ('E','F') THEN
			                             -- E,F동일 경우
			                                CASE WHEN T.SPAN||T.RYUL <= ITEM_VALUE1
			                                     THEN T.YARD||T.DONG||'PT'||'31'  
			                                     WHEN T.SPAN||T.RYUL <= ITEM2
			                                     THEN T.YARD||T.DONG||'PT'||'51'  
			                                     ELSE T.YARD||T.DONG||'PT'||'41'  
			                                END
			                             ELSE
			                             -- E,F동이 아닐 경우
			                                CASE WHEN T.SPAN||T.RYUL <= ITEM2
			                                     THEN T.YARD||T.DONG||'PT'||'31'  
			                                     ELSE T.YARD||T.DONG||'PT'||'41'  
			                                END
			                        END
			               END AS GATE     
			        FROM   TB_YD_RULE, 
			               (  
			                   SELECT 
			                        SUBSTR(A.YD_STK_COL_GP,1,1) AS YARD,
			                        SUBSTR(A.YD_STK_COL_GP,2,1) AS DONG,
			                        SUBSTR(A.YD_STK_COL_GP,3,2) AS SPAN,
			                        SUBSTR(A.YD_STK_COL_GP,5,2) AS RYUL,
			                        A.YD_STK_COL_GP             AS YD_STK_COL_GP
			                   FROM (SELECT :V_YD_STK_COL_GP AS YD_STK_COL_GP FROM DUAL)A
			               )T
			       WHERE   REPR_CD_GP = 'T00031'
			         AND   ITEM       = T.DONG
			       ) Y
			      ,TB_YD_STKCOL X
			      ,TB_YD_CARPOINT Z
			WHERE  Y.GATE = X.YD_STK_COL_GP    
			  AND  Y.GATE = Z.YD_STK_COL_GP    
			  */
			intRtnVal = ydPICommDAO.selectT(recInTemp, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0133");		
			
			if( intRtnVal <= 0) {
				sMsg="["+sOperationName+"] 사용가능한 개소POINT 조회 시 오류발생 - 메세지 : ";
				commPiUtils.printLog(logId, sMsg , "SL");	
				return ;
			}
			
			rsResult.first();
			recInTemp		= rsResult.getRecord();
			
			sWLOC_CD	   = commPiUtils.trim(recInTemp.getFieldString("WLOC_CD")); //2013.11.14 복수 창고 출하시 개소코드 변경하기 위하여 추가				
			sYD_PNT_CD	   = commPiUtils.trim(recInTemp.getFieldString("YD_PNT_CD"));
			sYD_STK_COL_GP = commPiUtils.trim(recInTemp.getFieldString("YD_CARLD_STOP_LOC"));
			
			sMsg="["+sOperationName+"] 사용가능한 차량정지위치["+sYD_STK_COL_GP+"]로 개소POINT 조회 완료 - 메세지 : " + intRtnVal;
			commPiUtils.printLog(logId, sMsg , "SL");	
			
		
			//-----------------------------------------------------------------------------------------------------------------
			
			if("Y".equals(sDOUBLEDONG_CHECK)) { //복수동일 경우 
				// [전사물류시스템개선]
				// 2021.1.6
				//  - 복수동상차와 복수동 하차를 사용하도록 수정
				//-----------------------------------------------------------------------------------------------------------------
				// 복수동 상차
				/*
				 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 초기화 수정한다.
				 */
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    		sCarSchId);
				recInTemp.setField("REGISTER",         		sModifier);
				recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
				recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
				recInTemp.setField("SPOS_WLOC_CD",     		sWLOC_CD);								//발지개소코드
				recInTemp.setField("YD_PNT_CD1",     		sYD_PNT_CD);							//야드포인트코드1
				recInTemp.setField("CAR_NO",           		sCAR_NO);								//차량번호
				recInTemp.setField("CARD_NO",          		"");								    //카드번호
				recInTemp.setField("YD_CARLD_LEV_DT",  		commPiUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   		sTRANS_ORD_DT);						    //운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  		sTRANS_ORD_SEQNO);						//운송지시순번
		    	recInTemp.setField("YD_CARLD_STOP_LOC",		sYD_STK_COL_GP);						//차량상차정지위치
		    	recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
		    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		"2");									//입동지시순번  (2013.06.20 cho 1순위 --> 2순위 변경)
				recInTemp.setField("DEL_YN", 				"N");		
				
				 //2025.07.31 임진후기사 요청 RITM1277126
                //복수동 5순위로 순위 조정
                String sApplyYnPI = ydPICommDAO.ApplyYnPI("", sOperationName, "APP060", "T", "020");//후판 개발 적용여부
                
				if("Y".equals(sApplyYnPI)){
					sMsg="[" + sOperationName + "] 복수동차량 2순위 -> 5순위 조정";
		    		commPiUtils.printLog(logId, sMsg , "SL");	
					recInTemp.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DOUBLE);	
                    
                }
				
				//차량스케줄수정
		    	intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
		    	
	    		if( intRtnVal <= 0 ){
					sMsg="[" + sOperationName + "] 차량스케줄 수정 시 오류발생[반환값 : " + intRtnVal + "]";
					commPiUtils.printLog(logId, sMsg , "SL");	
					return ;
	    		}
	    		
	    		//차량 스케쥴재료수정
	    		YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao(); 
	        	intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(recInTemp, 1) ;
	            
	    		sMsg="[" + sOperationName + "] 차량스케줄 수정 완료";
	    		commPiUtils.printLog(logId, sMsg , "SL");	
	    		
			}
			
			/*
			 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
			 */
		
			sMsg="[" + sOperationName + "] 차량정지위치[" + sYD_STK_COL_GP + "], 차량스케줄ID[" + sCarSchId + "] - 차량입동지시요구 모듈을 EJB 호출 시작";
			commPiUtils.printLog(logId, sMsg , "SL");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("TC_CODE"			, "YDYDJ633");
			recInTemp.setField("TC_CREATE_DDTT"		, commPiUtils.getCurDate("yyyyMMddHHmmss"));
			recInTemp.setField("YD_CAR_STOP_LOC"	, sYD_STK_COL_GP);
			recInTemp.setField("YD_CAR_SCH_ID"		, sCarSchId);
			recInTemp.setField("CALL_PGM"			, "SANGCHA");
			
			//----------------------------------------------------------------------------------
			//	동기화 문제로 인하여 JMS --> EJB Call로 변경
			//----------------------------------------------------------------------------------
//PIDEV 반드시 확인			
			EJBConnector ejbConn1 = new EJBConnector("default", "CarMvHdSeEJB", this);
			String sRtnMsg1 = (String) ejbConn1.trx("procCarBayInOrdReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
			
			//----------------------------------------------------------------------------------
			sMsg="[" + sOperationName + "] 차량정지위치[" + sYD_STK_COL_GP + "], 차량스케줄ID[" + sCarSchId + "] - 차량입동지시요구 모듈을 EJB 호출 성공 - 메세지 : " + sRtnMsg1;
			commPiUtils.printLog(logId, sMsg , "S-");
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}			
		
	}
}
