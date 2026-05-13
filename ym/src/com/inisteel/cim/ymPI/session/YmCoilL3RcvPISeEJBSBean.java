/**
 * @(#)YmCoilL3RcvPISeEJBBean
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
package com.inisteel.cim.ymPI.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ymEtcDao.YmEtcDao;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.util.YmConstant;
 
/**
 *      [A] 클래스명 : PI관련 2열연COIL야드 출하수신 처리
 *
 * @ejb.bean name="YmCoilL3RcvPISeEJB" jndi-name="YmCoilL3RcvPISeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class YmCoilL3RcvPISeEJBSBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();
	private YmComm ymComm = new YmComm();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	

	/**	 
	 * [A] 오퍼레이션명 :제품상태(열연코일)-출하지시대기,목전,반품,보관지시
	 * BCoilL3RcvSeEJB.java -> rcvDMYDR005 (코일제품출하지시대기)
	 * BCoilL3RcvSeEJB.java -> rcvDMYDR014 (코일제품목전)
	 * BCoilL3RcvSeEJB.java -> rcvDMYDR033 (코일제품반품)
	 * BCoilL3RcvSeEJB.java -> rcvDMYDR027 (코일제품보관지시)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	
	public JDTORecord rcvM10LMYDJ1011(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YmCoilL3RcvPISeEJBBean.rcvM10LMYDJ1011] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "3", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
			//수신 항목 값
			String msgId  = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID					
			String infoGp = commUtils.trim(rcvMsg.getFieldString("INFO_GP")); //야드구분
			
			rcvMsg.setField("TC_CREATE_DDTT", commUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
			
			if("1".equals(infoGp)) { // 
				methodNm += "[ 코일제품목전(DMYDR014)]";		
				int intRtnVal = 0;

				try {
					commUtils.printLog(logId, methodNm, "S+");

					//수신 항목 값
					String ydStockId	= commUtils.trim(rcvMsg.getFieldString("STL_NO"));	//Stock_id
					String sModifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
					String sCancelChk 	= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN")); //Y: 취소 , N: 지시
					if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }

					/**********************************************************
					* 1. 수신 항목 값 Check
					**********************************************************/
					if ("".equals(ydStockId)) {
						throw new Exception("저장품Id가 없습니다..");
					}
					if (("Y".equals(sCancelChk))) {
						
						return jrRtn;
						
					}else{
						
					   /**********************************************************
						* 2. 저장품 이동 조건 수정
						**********************************************************/
						JDTORecord jrParam = JDTORecordFactory.getInstance().create();
						jrParam.setResultCode(logId);	//Log ID
						jrParam.setResultMsg(methodNm);	//Log Method Name
						jrParam.setField("TC_CD"			, msgId);  //TC_CODE
						jrParam.setField("INFO_GP"			, infoGp);  //정보구분
						jrParam.setField("STOCK_ID"			, ydStockId); //저장품 ID

						JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(rcvMsg);
						jrParam.setField("STOCK_MOVE_TERM" 	, commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"))); 
						jrParam.setField("MODIFIER"  		, sModifier); 
						
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
							
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L002", sndL2Msg));	 //전송 Data 생성	
							
						commUtils.printLog(logId, methodNm, "S-");
					}
				} catch (DAOException e) {
					throw e;
				} catch (Exception e) {
					throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
				}
				
			} else if("2".equals(infoGp)) { // 코일제품보관지시(DMYDR027)
				methodNm += "[ 코일제품보관지시(DMYDR027) ]";	
				 
				try {
					int intRtnVal = 0;
					
					commUtils.printLog(logId, methodNm, "S+");

//					String szRcvTcCode = commUtils.trim(rcvMsg.getFieldString("JMS_TC_CD"));
					String modifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
					String StockId     = commUtils.trim(rcvMsg.getFieldString("STL_NO"));
					if ("".equals(modifier)) { modifier = msgId.substring(3,12); }
					
					/**********************************************************
					* 1. 수신 항목 값 Check
					**********************************************************/  
					JDTORecord jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("STL_NO"  	          ,StockId);
					jrParam.setField("KEEPSTOCK_STL_YN"   ,"Y");
					jrParam.setField("MODIFIER"  	      ,modifier);

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
					 
				} catch (DAOException e) {
					throw e;
				} catch (Exception e) {
					throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
				}
								
			} else if("3".equals(infoGp)) { // 코일제품반품(DMYDR033)
				methodNm += "[ 코일제품반품(DMYDR033) ]";			
				
				try {					
					commUtils.printLog(logId, methodNm, "S+");
					int intRtnVal = 0;
					String szMsg = "";
					
					//수신 항목 값
					String stlAppearGp 		= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));   //재료외형
					String StockId 			= commUtils.trim(rcvMsg.getFieldString("STL_NO"));    		//재료번호
					String distGoodsGp  	= commUtils.trim(rcvMsg.getFieldString("DIST_GOODS_GP"));   //출하제품구분 H:코일, T:HRPLATE
					String oldTransOrdDt  	= commUtils.trim(rcvMsg.getFieldString("OLD_TRANS_WORD_DATE"));  //구운송지시일자
					String oldTransOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("OLD_TRANS_WORD_SEQNO"));
					String newTransOrdDt  	= commUtils.trim(rcvMsg.getFieldString("NEW_TRANS_WORD_DATE"));  //신운송지시일자
					String newTransOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("NEW_TRANS_WORD_SEQNO"));
					String modifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
					if ("".equals(modifier)) { modifier = msgId.substring(3,12); }

					
					/**********************************************************
					* 1. 수신 항목 값 Check
					**********************************************************/

					JDTORecord jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("MODIFIER"  		, modifier  ); //수정자
					jrParam.setField("DEL_YN"  		    , "N" );	   //
					jrParam.setField("STOCK_ID"			, StockId);
					
					String[] rVal = new String[1];
					rVal= commUtils.getYdAimRtGp("C",rcvMsg );		
					jrParam.setField("YD_AIM_RT_GP", rVal[0]);

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
					JDTORecordSet jsStock = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdStockTRANS_ORD_DAT", logId, methodNm, "(운송일자, 운송순번)로 저장품 조회");

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
					JDTORecordSet jsCarsch = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCarschByTransDTSeq", logId, methodNm, "(운송일자, 운송순번)로 TB_YD_CARSCH 조회");			
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
					jrParam.setField("WBOOK_ID"         , "");
					jrParam.setField("STOCK_ID"         , StockId);
					
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
					     , SHEAR_SUPPLY_GP  = NVL(:V_SHEAR_SUPPLY_GP , SHEAR_SUPPLY_GP)   --차량종류
					     , WBOOK_ID         = NVL(:V_WBOOK_ID        , WBOOK_ID)          --작업예약 사용안함
					 WHERE STOCK_ID = :V_STOCK_ID
					 */
					intRtnVal=	commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStock_PIDEV", logId, methodNm, "TB_YM_STOCK 수정");

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
					
				} catch (DAOException e) {
					throw e;
				} catch (Exception e) {
					throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
				}
				
			} else if("4".equals(infoGp)) { // 코일제품출하지시대기(DMYDR005)
				methodNm += "[ 코일제품출하지시대기(DMYDR005) ]";
				try {
					int intRtnVal = 0;
					//수신 항목 값
					String ydStockId = commUtils.trim(rcvMsg.getFieldString("STL_NO"));	//Stock_id
					String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
					if ("".equals(modifier)) { modifier = msgId.substring(3,12); }
	
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
					jrParam.setField("TC_CD"			, msgId);  //TC_CODE
					jrParam.setField("INFO_GP"			, infoGp);  //정보구분
					jrParam.setField("STOCK_ID"	        , ydStockId); //저장품 ID
	
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
				} catch (DAOException e) {
					throw e;
				} catch (Exception e) {
					throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
				}
			}
			
			return jrRtn;		
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}			
	}	

	/**	 
	 * [A] 오퍼레이션명 :1열연 코일제품반납확정(DMYDR008)
	 * BCoilL3RcvSeEJBSBean.java -> rcvDMYDR008
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */		
	public JDTORecord rcvM10LMYDJ1021(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YmCoilL3RcvPISeEJBBean.rcvM10LMYDJ1021] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "3", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
			rcvMsg.setField("TC_CREATE_DDTT", 	  	commUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
			
			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
 			String ydStockId  = commUtils.trim(rcvMsg.getFieldString("STL_NO"));	//Stock_id
			String sModifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String sCancelChk = commUtils.trim(rcvMsg.getFieldString("CANCEL_YN")); //Y: 취소 , N: 지시
			String dmTcCode   = commUtils.trim(rcvMsg.getFieldString("TC_CODE"));//TC_CODE
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }

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
				jrParam.setField("TC_CD"	, msgId);  //TC_CODE
				jrParam.setField("STOCK_ID"	, ydStockId); //저장품 ID
	
				JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
				jrParam.setField("STOCK_MOVE_TERM" , commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"))); 
				jrParam.setField("MODIFIER" , sModifier); 
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
//					throw new Exception("수신한 재료번호 ["+ ydStockId+"]에 대한 저장품 DATA가 존재하지 않음");
					commUtils.printLog(logId, "수신한 재료번호 ["+ ydStockId+"]에 대한 저장품 DATA가 존재하지 않음", "SL");
					return jrRtn;
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
		
	/**	 
	 * [A] 오퍼레이션명 :코일제품운송상차지시(DMYDR060)
	 * YmCommL3RcvSeEJBSBean.java -> rcvDMYDR060
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1031(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YmCoilL3RcvPISeEJBBean.rcvM10LMYDJ1031] < " + rcvMsg.getResultMsg();
		String logId    = rcvMsg.getResultCode();
		
		JDTORecord jrRtn   = JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam = JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecordSet rsResult 	= null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "3", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
			rcvMsg.setField("TC_CREATE_DDTT", 	  	commUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
		
			//수신 항목 값
			String msgId     = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			jrParam.setField("MODIFIER", sModifier); //수정자 셋팅
			
			rcvMsg.setField("TRANS_ORD_DT"   , commUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ" )) );
			
			String szCMBN_CARLD_YN	 = commUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN")); //조합상차유무
			String szTRANS_ORD_DT	 = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); //운송지시일자
			String szTRANS_ORD_SEQNO = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송지시순번
			String szCAR_NO 		 = commUtils.trim(rcvMsg.getFieldString("CAR_NO")); //차량번호
			String szCARD_NO 		 = commUtils.trim(rcvMsg.getFieldString("CARD_NO")); //카드번호
			String szLOT_NO 		 = commUtils.trim(rcvMsg.getFieldString("LOT_NO")); //LOT번호
			String szCAR_KIND 		 = commUtils.trim(rcvMsg.getFieldString("CAR_KIND")); //차량종류
			String szCANCEL_YN 		 = commUtils.trim(rcvMsg.getFieldString("CANCEL_YN")); //취소여부
			
			/***********************************
			 * 취소
			 ***********************************/
			if ("Y".equals(szCANCEL_YN)) {
				String sPI0003 = ymComm.BCoilApplyYn("PI0003", "*", "*");
				if ("Y".equals(sPI0003)) {
					commUtils.printLog(logId, "M10LMYDJ1031 취소시 신로직 들어감 'PI0003' = 'Y", "SL");
					jrRtn = this.procDmTcCnclPI(rcvMsg);
				} else {
			    	jrRtn = this.procDmTcCnclYmPI(rcvMsg);
				}

			    return jrRtn;
			}
			
			int szYD_EQP_WRK_SH		 = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수
			
			String szYD_GP			 = "";
			String szSTL_NO          = "";
			String szGDS_CARLD_LOC   = "";
			String szSTOCK_MOVE_TERM   = "";
			String szYD_AIM_RT_GP   = "";
			String szMsg = "";
			String[] rVal = new String[1];
			
			/////////////////////////////////////////////////////////////////////////////////////
			//박판열연 신규모듈 적용여부 체크
//			JDTORecord jrResult =  commDao.getYfNewModuleEffYn();
//			String sACOIL_EFF_YN = commUtils.nvl(jrResult.getFieldString("ACOIL_EFF_YN"),"N");

//			String szMsg = "YmCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
			commUtils.printLog(logId, methodNm + szMsg , "SL");
			/////////////////////////////////////////////////////////////////////////////////////
			
			//수신된 전문의 STL_NO의 수 만큼 Loop
			for(int ii = 1 ; ii<=szYD_EQP_WRK_SH; ii++) 
			{
				szYD_GP 		= commUtils.trim(rcvMsg.getFieldString("YD_GP"+ii)); //야드구분
				szSTL_NO 		= commUtils.trim(rcvMsg.getFieldString("STL_NO"+ii)); //재료번호
				szGDS_CARLD_LOC = commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+ii)); //상차위치
				
				if ("S".equals(szCMBN_CARLD_YN)&&"TR".equals(szCAR_KIND)&&"".equals(szGDS_CARLD_LOC)) 
				{
					szGDS_CARLD_LOC = "0" + ii;
				}
				
				if ("".equals(szSTL_NO)) {
					break;
				}
				
				
				//저장품 이동 조건 
				jrParam.setField("TC_CD"	, msgId);  //TC_CODE
				jrParam.setField("STOCK_ID" , szSTL_NO); //저장품 ID
				szSTOCK_MOVE_TERM = commUtils.trim(ymComm.getCoilCurrProgCd(jrParam).getFieldString("STOCK_MOVE_TERM"));
				
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
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStock_PIDEV
				UPDATE USRYMA.TB_YM_STOCK
				   SET MODIFIER         = :V_MODIFIER
				     , MOD_DDTT         = SYSDATE
				     , STOCK_MOVE_TERM  = NVL(:V_STOCK_MOVE_TERM , STOCK_MOVE_TERM)   --저장품이동조건
				     , YD_RULE_PL_RS_GP = NVL(:V_YD_RULE_PL_RS_GP, YD_RULE_PL_RS_GP)  --조합구분
				     , TRANS_WORD_NO    = :V_TRANS_ORD_DATE2 || :V_TRANS_ORD_SEQNO2
				     , TRANS_ORD_DATE2  = NVL(:V_TRANS_ORD_DATE2 , TRANS_ORD_DATE2)   --운송지시
				     , TRANS_ORD_SEQNO2 = NVL(:V_TRANS_ORD_SEQNO2, TRANS_ORD_SEQNO2)  --운송지시행번
				     , SHEAR_SUPPLY_SEQ = NVL(:V_SHEAR_SUPPLY_SEQ, SHEAR_SUPPLY_SEQ)  --차상위치
				     , CAR_NO2          = NVL(:V_CAR_NO2         , CAR_NO2)           --차량번호 
				     , SHEAR_SUPPLY_GP  = NVL(:V_SHEAR_SUPPLY_GP , SHEAR_SUPPLY_GP)   --차량종류
				     , WBOOK_ID         = NVL(:V_WBOOK_ID        , WBOOK_ID)          --작업예약 사용안함
				     , CR_FRTOMOVE_GP   = NVL(:V_CR_FRTOMOVE_GP  , CR_FRTOMOVE_GP)    -- 냉연이송구분       
				 WHERE STOCK_ID = :V_STOCK_ID */  
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStock_PIDEV", logId, methodNm, "TB_YM_STOCK 수정");
					
				
			} // end of for loop
			
			//야드핸들링정보 송신////////////////////////////////////////////////////////////////////////////////
			//코일야드 인 경우 우선 적용
			if ("3".equals(szYD_GP)) {
				
				//1:A열연 COIL야드,3:B열연 COIL야드,H:C열연 COIL소재야드,J:C열연 COIL제품야드
				
				//출하제품핸들링횟수 구하기
				jrParam.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DT);
				jrParam.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getHandlingCnt 
				--//출하제품핸들링횟수
				WITH TEMP_TABLE AS (
				    SELECT STOCK_ID         AS STL_NO
				         , TRANS_ORD_SEQNO2 AS TRANS_ORD_SEQNO
				      FROM USRYMA.TB_YM_STOCK A
				     WHERE A.TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE 
				       AND A.TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
				)
				, TEMP_TABLE2 AS (
				    SELECT SUBSTR(YD_CARPNT_CD,1,1) AS YD_GP 
				         , YD_CARPNT_CD  AS CARLD_PNT_CD
				         , COUNT(STL_NO) AS HANDLING_CNT  
				      FROM ( 
				             SELECT SUBSTR(C.YD_CARPNT_CD,1,3) AS YD_CARPNT_CD
				                  , A.STL_NO 
				                  , TRANS_ORD_SEQNO
				               FROM (
				                      SELECT 'A' AS CHK 
				                           , STL_NO 
				                           , TRANS_ORD_SEQNO
				                        FROM TEMP_TABLE
				                       UNION 
				                      SELECT 'B' 
				                           , (SELECT STL_NO_LEFT FROM VW_YM_STKLYERSEARCH B WHERE A.STL_NO=B.STL_NO) AS STL_NO_LEFT
				                           , TRANS_ORD_SEQNO
				                        FROM TEMP_TABLE A
				                       UNION 
				                      SELECT 'B'
				                           , (SELECT STL_NO_RIGHT FROM VW_YM_STKLYERSEARCH B WHERE A.STL_NO=B.STL_NO) AS STL_NO_RIGHT
				                           , TRANS_ORD_SEQNO
				                        FROM TEMP_TABLE A 
				                     ) A
				                   , TB_PT_COILCOMM B
				                   , USRYDA.TB_YD_CARPOINT C
				               WHERE A.STL_NO    = B.COIL_NO
				                 AND B.YD_GP     = C.YD_GP
				                 AND B.YD_BAY_GP = C.YD_BAY_GP
				                 AND B.YD_EQP_GP BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
				               GROUP BY SUBSTR(C.YD_CARPNT_CD,1,3) , A.STL_NO ,TRANS_ORD_SEQNO
				            )
				            GROUP BY YD_CARPNT_CD
				)
				SELECT * 
				  FROM TEMP_TABLE2
				 WHERE EXISTS(SELECT 1 FROM TEMP_TABLE2)  
				UNION ALL
				SELECT B.YD_GP , '' ,  COUNT(A.STL_NO) AS HANDLING_CNT 
				  FROM TEMP_TABLE A
				     , TB_PT_COILCOMM B
				 WHERE A.STL_NO = B.COIL_NO
				   AND NOT EXISTS (SELECT 1 FROM TEMP_TABLE2)
				 GROUP BY B.YD_GP */
				rsResult = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getHandlingCnt", logId, methodNm, "출하Handling 갯수 구하기");

				for(int ii = 0; ii < rsResult.size() ; ii++)
				{	
					jrParam.setField("YD_GP"				, commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_GP")) );
					jrParam.setField("TRANS_ORD_DT"			, szTRANS_ORD_DT);
					jrParam.setField("TRANS_ORD_SEQNO"		, szTRANS_ORD_SEQNO);
					jrParam.setField("CMBN_CARLD_YN"		, szCMBN_CARLD_YN );
					jrParam.setField("CARLD_PNT_CD"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("CARLD_PNT_CD")) );
					jrParam.setField("CAR_NO"				, szCAR_NO );
					jrParam.setField("HANDLING_CNT"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("HANDLING_CNT")) ); 
					jrParam.setField("YD_STK_BED_WHIO_STAT"	, "" );
					
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL3("M10YDLMJ1051", jrParam));					
				}
			}
			
			//-------------------------------------------------------------------------------------------------------------
			// A동 2통로 출하대상 자동이적 YMYMJ312{
				
			jrParam.setField("TRANS_ORD_DATE" 	, szTRANS_ORD_DT);
			jrParam.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			
			rsResult = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getListA3toA4Dm", logId, methodNm, "A동 2통로 출하 자동이적 대상 리스트 조회");
			
			if (rsResult.size() > 0) {
				//A4 크레인이 갈수 없는 영역에 출하 대상이 존재하면 자동이적  YMYMJ312 호출 
			
				JDTORecord jrYMYMJ312 = JDTORecordFactory.getInstance().create();
				jrYMYMJ312.setResultCode(logId);	//Log ID
				jrYMYMJ312.setResultMsg(methodNm);	//Log Method Name
				jrYMYMJ312.setField("JMS_TC_CD" 		, "YMYMJ312");
				jrYMYMJ312.setField("TRANS_ORD_DATE" 	, szTRANS_ORD_DT);
				jrYMYMJ312.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
				jrYMYMJ312.setField("MODIFIER"	        , sModifier);
				
				jrRtn = commUtils.addSndData(jrRtn, jrYMYMJ312);
			
			}
			
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
	 * [A] 오퍼레이션명 :임가공 운송상차지시(DMYDR060)
	 * YmCommL3RcvSeEJBSBean.java -> rcvDMYDR060
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1035(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YmCoilL3RcvPISeEJBBean.rcvM10LMYDJ1035] < " + rcvMsg.getResultMsg();
		String logId    = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam = JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecordSet rsResult 	= null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "3", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sMqTcCd    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
			if ("".equals(modifier)) { modifier = sMqTcCd.substring(3,12); }			
			rcvMsg.setField("MODIFIER", modifier);		
			
			rcvMsg.setField("TRANS_ORD_DT"   , commUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ" )) );			
			
			String szCMBN_CARLD_YN	 = commUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN")); //조합상차유무
			String szTRANS_ORD_DT	 = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); //운송지시일자
			String szTRANS_ORD_SEQNO = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송지시순번
			String szCAR_NO 		 = commUtils.trim(rcvMsg.getFieldString("CAR_NO")); //차량번호
			String szCARD_NO 		 = commUtils.trim(rcvMsg.getFieldString("CARD_NO")); //카드번호
			String szLOT_NO 		 = commUtils.trim(rcvMsg.getFieldString("LOT_NO")); //LOT번호
			String szCAR_KIND 		 = commUtils.trim(rcvMsg.getFieldString("CAR_KIND")); //차량종류
			
			int szYD_EQP_WRK_SH		 = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수
			
			String szYD_GP			 = "";
			String szSTL_NO;
			String szGDS_CARLD_LOC;
			String szSTOCK_MOVE_TERM;
			
			String szYD_AIM_RT_GP;
			
			YdStockDao ydStockDao = new YdStockDao();
			
			String[] rVal = new String[1];			
			
			/////////////////////////////////////////////////////////////////////////////////////
			//박판열연 신규모듈 적용여부 체크
//			JDTORecord jrResult =  commDao.getYfNewModuleEffYn();
//			String sACOIL_EFF_YN = commUtils.nvl(jrResult.getFieldString("ACOIL_EFF_YN"),"N");

//			String szMsg = "YmCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
//			commUtils.printLog(logId, methodNm + szMsg , "SL");
			/////////////////////////////////////////////////////////////////////////////////////			
			
			//수신된 전문의 STL_NO의 수 만큼 Loop
			for(int ii = 1 ; ii<=szYD_EQP_WRK_SH; ii++) 
			{

				szYD_GP 		= commUtils.trim(rcvMsg.getFieldString("YD_GP"+ii)); //야드구분
				szSTL_NO 		= commUtils.trim(rcvMsg.getFieldString("STL_NO"+ii)); //재료번호
				szGDS_CARLD_LOC = commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+ii)); //상차위치
				
				if ("S".equals(szCMBN_CARLD_YN)&&"TR".equals(szCAR_KIND)&&"".equals(szGDS_CARLD_LOC)) 
				{
					szGDS_CARLD_LOC = "0" + ii;
				}
				
				if ("".equals(szSTL_NO)) {
					break;
				}				
				
				//일관제철 야드 - RtModRegSeEJB.procCoilGdsTrnOrdNEW 와 동일함
				
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				jrParam.setField("TC_CODE", msgId);  //TC_CODE
				jrParam.setField("STL_NO" , szSTL_NO); //저장품 ID
				rVal= commUtils.getYdAimRtGp("C",jrParam );	
				
				szYD_AIM_RT_GP	= rVal[0]; //야드목표행선
				
				//TB_YD_STOCK 수정
				jrParam.setField("STL_NO"			, szSTL_NO);
				jrParam.setField("YD_CAR_UPP_LOC_CD", szGDS_CARLD_LOC);
				jrParam.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DT);
				jrParam.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
				jrParam.setField("YD_AIM_RT_GP"		, szYD_AIM_RT_GP);
				jrParam.setField("CAR_NO"			, szCAR_NO);
				jrParam.setField("CARD_NO"			, szCARD_NO);
				jrParam.setField("YD_RULE_PL_RS_GP"	, szCMBN_CARLD_YN);
				jrParam.setField("CAR_LOTID"		, szLOT_NO);
				jrParam.setField("YD_STK_BED_NO"	, szCAR_KIND);
				/*com.inisteel.cim.ym.dao.ydstockdao.updYdStock
				UPDATE USRYDA.TB_YD_STOCK
				SET YD_AIM_RT_GP     =NVL(:V_YD_AIM_RT_GP,YD_AIM_RT_GP)
				  , YD_RULE_PL_RS_GP =:V_YD_RULE_PL_RS_GP
				  , TRANS_ORD_DATE   =:V_TRANS_ORD_DATE
				  , TRANS_ORD_SEQNO  =:V_TRANS_ORD_SEQNO
				  , YD_CAR_UPP_LOC_CD=:V_YD_CAR_UPP_LOC_CD
				  , MODIFIER         =NVL(:V_MODIFIER,MODIFIER)
				  , MOD_DDTT         =SYSDATE
				  , CAR_NO           =:V_CAR_NO
				  , CARD_NO          =:V_CARD_NO
				  , CAR_LOTID        =:V_CAR_LOTID
				  , CAR_LOTID_REG_DDTT =NVL(CAR_LOTID_REG_DDTT,(CASE WHEN :V_CAR_LOTID<>'' THEN SYSDATE ELSE NULL END))
				  , YD_STK_BED_NO    =NVL( :V_YD_STK_BED_NO,'TR')  
				  , CR_FRTOMOVE_GP= NULL
				WHERE STL_NO    =:V_STL_NO */
				ydStockDao.updYdStock(jrParam);
				
				//현재 com.inisteel.cim.ym.dao.ydstockdao.updYdStock 의 파라메터 지정이 안되 어 있어서
				//ydStockDao.updYdStock 메소드를 사용한 것임. 
				//jspeed에서 파라메터 지정 후 아래 와 같이 사용해도 됨!
				//commDao.update(jrParam, "com.inisteel.cim.ym.dao.ydstockdao.updYdStock, logId, methodNm, "TB_YD_STOCK 수정");
				
			}
			
			//-------------------------------------------------------------------------------------------------------------
			// A동 2통로 출하대상 자동이적 YMYMJ312
				
			jrParam.setField("TRANS_ORD_DATE" 	, szTRANS_ORD_DT);
			jrParam.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			
			rsResult = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getListA3toA4Dm", logId, methodNm, "A동 2통로 출하 자동이적 대상 리스트 조회");
			
			if (rsResult.size() > 0) {
				//A4 크레인이 갈수 없는 영역에 출하 대상이 존재하면 자동이적  YMYMJ312 호출 
			
				JDTORecord jrYMYMJ312 = JDTORecordFactory.getInstance().create();
				jrYMYMJ312.setResultCode(logId);	//Log ID
				jrYMYMJ312.setResultMsg(methodNm);	//Log Method Name
				jrYMYMJ312.setField("JMS_TC_CD" 		, "YMYMJ312");
				jrYMYMJ312.setField("TRANS_ORD_DATE" 	, szTRANS_ORD_DT);
				jrYMYMJ312.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
				jrYMYMJ312.setField("MODIFIER"	        , modifier);
				
				jrRtn = commUtils.addSndData(jrRtn, jrYMYMJ312);
			
			}		
			
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
	 * [A] 오퍼레이션명 :출하대기장도착(DMYDR061), 코일이송상차대기장도착PDA(DMYDR070) 
	 * YmCommL3RcvSeEJBSBean.java	-> rcvDMYDR061
	 * BCoilL3RcvSeEJBSBean.java	-> rcvDMYDR070
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1041(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "[YmCoilL3RcvPISeEJBBean.rcvM10LMYDJ1041[출하대기장도착 (61), 코일이송상차대기장도착PDA(70)]] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try	{
			commUtils.printLog(logId, mthdNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", mthdNm, "APPPI0", "3", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
//	
//			String sWorkGp		= commUtils.trim(rcvMsg.getFieldString("WORK_GP")); //야드구분
//			String sMqTcCd		= commUtils.trim(rcvMsg.getFieldString("MQ_TC_CD")); //야드구분
//		
//			String sModifier = "";
			
//			if ("".equals(sModifier)) { sModifier = sMqTcCd.substring(3,12); }
			
			rcvMsg.setField("TRANS_ORD_DT"   , commUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
			String msgId     = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sWorkGp	 = commUtils.trim(rcvMsg.getFieldString("WORK_GP"));   //야드구분
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));  
			
			String ydSndYn   = commUtils.nvl (rcvMsg.getFieldString("YD_SND_YN"),"N"); //복수동시 필요
			if("Y".equals(ydSndYn)) {
				Thread.sleep(1000);
			}			
			
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }			
			
			rcvMsg.setField("MODIFIER", sModifier);
			
			if(! "9".equals(sWorkGp) ) {
				// 
				jrRtn = this.procM10LMYDJ_DMYDR061(rcvMsg);	
			} else {
				// 코일이송상차대기장도착PDA
				jrRtn = this.procM10LMYDJ_DMYDR070(rcvMsg);
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
	 * [A] 오퍼레이션명 :상차대기장도착 (임가공)(DMYDR061)
	 * YmCommL3RcvSeEJBSBean.java	-> rcvDMYDR061
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1045(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "[YmCoilL3RcvPISeEJBBean.rcvM10LMYDJ1045 [상차대기장도착 (임가공)(DMYDR061)]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", mthdNm, "APPPI0", "3", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
			commUtils.printParam(logId, rcvMsg);			
			
			rcvMsg.setField("TC_CREATE_DDTT", commUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
			//수신 항목 값
			String msgId     = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID			
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			
			rcvMsg.setField("MODIFIER", sModifier);
			
			// 코일제품반납확정
			jrRtn = this.procM10LMYDJ_DMYDR061(rcvMsg);	
						
			return jrRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}		
		
	/**	 
	 * [A] 오퍼레이션명 :코일이송상차도착PDA(DMYDR071) 
	 * BCoilL3RcvSeEJBSBean.java	-> rcvDMYDR071
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1051(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YmCoilL3RcvPISeEJBBean.rcvM10LMYDJ1051 [코일이송상차도착PDA(DMYDR071)]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "3", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			rcvMsg.setField("TRANS_ORD_DT"   , commUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ" )) );
			
			String transOrdDt  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); //운송실적일자
			String transOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));  //운송실적순번
			String StockId          = ""; 
			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
//			String ydCardNo 		= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
			String ydCardNo 		= "";
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
			if ("".equals(modifier)) { modifier = msgId.substring(3,12); }

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
	 
			JDTORecordSet jsStlno = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getTransStockInfo", logId, methodNm, "운송실적번호에 맞는 제품번호가 존재 조회");			
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
				
				JDTORecordSet jsWbookMtl = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockWbookcheck", logId, methodNm, "작업예약 조회");
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
				JDTORecordSet jsChk2 = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookStockYN", logId, methodNm, "작업예약 등록여부");

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
					JDTORecordSet jsStockCnt = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStockLayerCntChk", logId, methodNm, "작업대상갯수 조회");
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
			 JDTORecordSet jsCarSch = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCarsch", logId, methodNm, "TB_YD_CARSCH 조회");
			 이미 입동된 카드번호를 가진 차량은 다시 입동되지 않는다. 20180419
			 */
			JDTORecordSet jsCarSch = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCarschNot2", logId, methodNm, "TB_YD_CARSCH 조회");
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
				JDTORecordSet jsCarPnt = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarPoint", logId, methodNm, "차량포인트 조회");
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
//									if( "21".equals(sTrnFrtomoveGp) ) {   //1통로 제품이송상차
//										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT22UM";
//									} else { //1통로 냉연이송상차
//										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT12UM";
//									}
//								}
								if ( "21".equals(sTrnFrtomoveGp) && "K".equals(hIssueGp) ) {	// 제품이송
									if ("2".equals(CarldPntCd.substring(1,2))) {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT26UM";
									} else {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT22UM";
									}
								} else if ( "22".equals(sTrnFrtomoveGp) && "9".equals(hIssueGp) ) {	// 제품이송
									if ("2".equals(CarldPntCd.substring(1,2))) {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT26UM";
									} else {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT22UM";
									}
								} else if ( ("21".equals(sTrnFrtomoveGp) && "C".equals(hIssueGp))
											||
											("21".equals(sTrnFrtomoveGp) && "D".equals(hIssueGp)) ) {	// 이송출고
									if ("2".equals(CarldPntCd.substring(1,2))) {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT16UM";
									} else {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT12UM";
									}
								} else {
									if ("2".equals(CarldPntCd.substring(1,2))) {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT16UM";
									} else {
										ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT12UM";
									}
								}
								
//							} else {
//							
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
//								
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
						JDTORecordSet jsCarMtl = commDao.select3(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockOfCarLoad", logId, methodNm, "재료정보 조회");					
						
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
						JDTORecordSet rsResult = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPntFrmYn", logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 ");
						
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
	 * [A] 오퍼레이션명 :코일제품출하완료(DMYDR030)
	 * BCoilL3RcvSeEJBSBean.java	-> rcvDMYDR030
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1071(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YmCoilL3RcvPISeEJBBean.rcvM10LMYDJ1071 [코일제품출하완료(DMYDR030)]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
	    String szMsg	= "";
	    
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "3", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
			String msgId       	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String StockId    	= commUtils.trim(rcvMsg.getFieldString("STL_NO")); 
			String stlAppearGp	= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")); 
			String ydGp        	= commUtils.trim(rcvMsg.getFieldString("YD_GP")); 
			String BackUpYn     = commUtils.trim(rcvMsg.getFieldString("BACKUP_YN")); 
			String modifier    	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String sCancelChk   = commUtils.trim(rcvMsg.getFieldString("CANCEL_YN"    )); // Y: 취소 , N: 지시
			if ("".equals(modifier)) { modifier = msgId.substring(3,12); }
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/  			
			// 수신한 전문이 null이라면 error
			if (msgId.equals("")) {
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
			JDTORecordSet loadYdStkcol = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdStockJoinStkLyr2", logId, methodNm, "차량정정보검색");
			
			if (loadYdStkcol.size() <= 0) {
				szMsg="["+methodNm+"] YMSTOCK[코일제품출하완료]조건조회시 SELECT Error ::  DO NOT EXIST"  ;
				commUtils.printLog(logId, szMsg, "SL");	
				return jrRtn ;
			}
			//수신 항목 값
			String ydCarNo    	= commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("CAR_NO"));
			String ydStkColGp 	= commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("YD_STK_COL_GP"));
//			String ydCardNo     = commUtils.trim(loadYdStkcol.getRecord(0).getFieldString("CARD_NO"));
			String ydCardNo     = "";
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
			
			szMsg="[" + methodNm + "]카드번호["+ydCarNo+"], 차량번호["+ydCarNo+"], 운송지시일자["+transOrdDate+"], 운송지시순번["+transOrdSeqNo+"] : 출하완료된 동["+ydStkColGp+"]의 저장품들 조회 시작";
			commUtils.printLog(logId, szMsg, "SL");
			
			if (stlAppearGp.equals("*")  && "N".equals(sCancelChk)) {

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
				JDTORecordSet loadCarsch = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschTransDTSeq2", logId, methodNm, "차량정정보검색");
				
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
					if ("".equals(ydCarNo)) {
						ydCarNo = "XXXXX";
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
			commUtils.printLog(logId,"[코일제품출하완료][" + StockId + "] BACKUP 유무=>:+" + BackUpYn +" CANCEL_YN=>:"+ sCancelChk, "SL");

			
			if (BackUpYn.equals("Y") && "N".equals(sCancelChk)) {
				/*
				SELECT *
				  FROM TB_YM_STACKLAYER
				 WHERE STOCK_ID = :V_STOCK_ID
				   AND SUBSTR(STACK_COL_GP,3,2) NOT IN ('TR','PT','TT') --//차량위치가 아닌경우 	
				 */
				JDTORecordSet loadStacklayer = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStacklayerList", logId, methodNm, "단정보검색");
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
			
			
			
			/*******************************************
			 * 출하실적취소 시 상차이전 위치로 원복 한다. 2023.11.09 문제윤 주임 출하관제
			 * 2023.12.14 1열연 출하만 예외적으로 제외 처리 요청 김기태 계장님 출하관제
			 *******************************************/
//			if ("Y".equals(sCancelChk)) {
//				jrParam.setField("COIL_NO", StockId);
//				JDTORecordSet jsCoilcomm = commDao.select3(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getPtCoilComm", logId, methodNm, "코일공통 조회");
//				
//				if (jsCoilcomm.size() <= 0) {
//					throw new Exception("TB_PT_COILCOMM 존재하지 않는 코일번호:" + StockId);
//				}
//				
//				String ydEqpGp  	 = commUtils.trim(jsCoilcomm.getRecord(0).getFieldString("YD_EQP_GP")); //현위치가 차량
//				String ydStrLocHis1  = commUtils.trim(jsCoilcomm.getRecord(0).getFieldString("YD_STR_LOC_HIS1")); //상차전 저장위치
//				
//				if (!"PT".equals(ydEqpGp)) {
//					commUtils.printLog(logId, "상차완료백업이 안된 코일이여서 상차전 위치로 원복 할 수 없습니다.", "SL");
//					throw new Exception("상차완료백업이 안된 코일이여서 상차전 위치로 원복 할 수 없습니다." + StockId);
//				}
//				 
//						
//					   ydStkColGp = commUtils.trim(ydStrLocHis1.substring(0, 6));
//				String ydStkBedNo = commUtils.trim(ydStrLocHis1.substring(6, 8));
//				String ydStkLyrNo = commUtils.trim(ydStrLocHis1.substring(8, 10));
//				String	ydGpBay	  = commUtils.trim(ydStrLocHis1.substring(1, 2));
//				
//				
//				 				
//				jrParam.setField("STOCK_ID"       , StockId );
//				jrParam.setField("STACK_LAYER_STAT", "C" );
//				jrParam.setField("STACK_COL_GP", ydStkColGp);
//				jrParam.setField("STACK_BED_GP", ydStkBedNo);  
//				jrParam.setField("STACK_LAYER_GP", ydStkLyrNo);	
//				
//				
//				jsCoilcomm = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStackLayerInfo", logId, methodNm, "저장위치 조회");
//				
//				if (jsCoilcomm.size() > 0) {
//					String stlNoChk  	 = commUtils.trim(jsCoilcomm.getRecord(0).getFieldString("STOCK_ID"));
//					
//					if(!"".equals(stlNoChk)){
//						commUtils.printLog(logId, "상차전 위치로 원복 할 위치에 다른 코일번호 존재하여 원복 불가.", "SL");
//						throw new Exception("상차전 위치로 원복 할 위치에 다른 코일번호 존재하여 원복 불가."+ydStrLocHis1+" 코일번호:" + stlNoChk);
//					}
//				}
//				
//				/***********************************
//				 * TB_YD_STKLYR 저장위치 Update 
//				 ***********************************/
//				
//				/*com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStackLayerStat 
//					UPDATE TB_YM_STACKLAYER            
//					   SET MOD_DDTT             = SYSDATE             
//					     , MODIFIER             = :V_MODIFIER             
//					     , STOCK_ID				= :V_STOCK_ID
//						 , STACK_LAYER_STAT	    = :V_STACK_LAYER_STAT
//					 WHERE STACK_COL_GP     = :V_STACK_COL_GP
//					   AND STACK_BED_GP     = :V_STACK_BED_GP
//					   AND STACK_LAYER_GP   = :V_STACK_LAYER_GP
//				 */
//				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStackLayerStat", logId, methodNm, "적치단 수정");				
//				commUtils.printLog(logId, "[코일제품출하완료]["+StockId+"]에 저장위치맵을 복원합니다.", "SL");
//				
//		 
//				/***********************************
//				 * TB_YM_STOCK 저장위치 Update 
//				 ***********************************/
//				jrParam.setField("STL_APPEAR_GP", 	stlAppearGp);
//				jrParam.setField("DEL_YN"         , "N");
//				jrParam.setField("YD_AIM_RT_GP"   , "M2");
//				jrParam.setField("STL_PROG_CD"    , "M"); 
//				jrParam.setField("STOCK_ID"		  , StockId);
//				jrParam.setField("MODIFIER"       , modifier);
//				
//				/*
//	            UPDATE USRYMA.TB_YM_STOCK
//					SET DEL_YN=:V_DEL_YN
//					  , MODIFIER=:V_MODIFIER  
//					  , MOD_DDTT =SYSDATE
//					  , YD_AIM_RT_GP = :V_YD_AIM_RT_GP
//					WHERE STOCK_ID=:V_STOCK_ID
//				*/
//				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStock", logId, methodNm, "TB_YM_STOCK 수정");
//				
//				
//				
//				/***********************************
//				 * Coil 공통 Table 저장위치 Update 
//				 ***********************************/					
//				//코일 공통 상차백업 위치로 위치 변경 작업 
//				JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
//				recInTemp1.setResultCode(logId);	//Log ID
//				recInTemp1.setResultMsg(methodNm);	//Log Method Name
//				recInTemp1.setField("STOCK_ID"	, StockId       ); //재료번호
//				recInTemp1.setField("YD_LOC"	, ydStkColGp+ydStkBedNo+ydStkLyrNo); //현재위치
//				
//				EJBConnector ejbConn1 = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
//				ejbConn1.trx("UpdCoilComLoc", new Class[] { JDTORecord.class }, new Object[] { recInTemp1 });
//				
//				
//				/*************************
//				 * 작업실적에 저장
//				 *************************/
//				JDTORecord jparam = JDTORecordFactory.getInstance().create();
//				
//				jparam.setField("SCH_ID"           , "000000000000000000");
//				jparam.setField("STOCK_ID"         , StockId);
//				jparam.setField("EQUIP_GP"         , "3"+ydGpBay+YmConstant.EQUIP_KIND_CR + "00");
//				jparam.setField("YD_SCH_CD"        , "3X9999");
//				jparam.setField("SCRANE_WORK_DUTY" , YmCommUtils.getWorkDuty());  
//				jparam.setField("SCRANE_WORK_PARTY", YmCommUtils.getWorkParty()); 
//				jparam.setField("SCH_WDEMAND_DUTY" , YmCommUtils.getWorkDuty());  
//				jparam.setField("SCH_WDEMAND_PARTY", YmCommUtils.getWorkParty()); 
//				jparam.setField("UP_LOC"           , "3"+ydGpBay + "PT010101");
//				jparam.setField("PUT_LOC"          , ydStkColGp + ydStkBedNo +ydStkLyrNo);
//				jparam.setField("UP_FUNC"          , YmConstant.CRANE_FUNC_S);
//				jparam.setField("PUT_FUNC"         , YmConstant.CRANE_FUNC_S);
//				jparam.setField("YD_GP"            , "3");
//				jparam.setField("REGISTER"         , modifier);
//				jparam.setField("MODIFIER"         , modifier);
//				
//				commDao.insert(jparam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insertCrnWrslt", logId, methodNm, "작업실적 저장");
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
	 * [A] 오퍼레이션명 :코일이송하차대기장도착PDA(DMYDR073)
	 * BCoilL3RcvSeEJBSBean.java	-> rcvDDMYDR073
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1091(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YmCoilL3RcvPISeEJBSBean.rcvM10LMYDJ1091 [코일이송하차대기장도착PDA 송신(DMYDR073)]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();		
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "3", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
			commUtils.printParam(logId, rcvMsg);	
			
			rcvMsg.setField("TC_CREATE_DDTT" , commUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
			rcvMsg.setField("TRANS_ORD_DT"   , commUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
			//수신 항목 값
			String msgId    		= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlAppearGp 		= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));    //재료외형
			String transOrdDt  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			String transOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String cancelYn     	= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));
			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
//			String ydCardNo 		= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
			String WorkGp 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP"));      //작업구분
			String ydCarKind		= commUtils.trim(rcvMsg.getFieldString("CAR_KIND"));
			String CarudPntCd 		= commUtils.trim(rcvMsg.getFieldString("CARUD_PNT_CD")); //하차포인트
			String transFrtoMoveGp 	= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP")); //냉연이송구분 
			int ydEqpWrkSh 			= Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0"));
			String modifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String dmTcCode 		= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));//TC_CODE
			
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			
			rcvMsg.setField("MODIFIER", sModifier);
			
			/***********************************
			 * 취소
			 ***********************************/
			if ("Y".equals(cancelYn)) {
				String sPI0003 = ymComm.BCoilApplyYn("PI0003", "*", "*");
				if ("Y".equals(sPI0003)) {
					commUtils.printLog(logId, "M10LMYDJ1091 취소시 신로직 들어감 'PI0003' = 'Y", "SL");
					jrRtn = this.procDmTcCnclPI(rcvMsg);
				}

				return jrRtn;
			}
			
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
			jrParam.setField("CAR_NO2" 			, ydCarNo);
			jrParam.setField("CAR_NO" 			, ydCarNo);
			jrParam.setField("CR_FRTOMOVE_GP" 	, transFrtoMoveGp);
			jrParam.setField("YD_CARPNT_CD"		, CarudPntCd);
			jrParam.setField("TRANS_WORD_NO" 	, transOrdDt+transOrdSeqNo);
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
				         , CAR_NO2           , CR_FRTOMOVE_GP  
				         )
				    VALUES (
				           :V_STOCK_ID          , DD.STOCK_ITEM     , DD.STOCK_MOVE_TERM 
				         , DD.MODIFIER          , DD.MOD_DDTT       , DD.MODIFIER  
				         , DD.MOD_DDTT          , DD.DEL_YN         , DD.TRANS_WORD_NO 
				         , DD.SHEAR_SUPPLY_SEQ  , DD.TRANS_ORD_DATE2, DD.TRANS_ORD_SEQNO2
				         , DD.CAR_NO2        , DD.CR_FRTOMOVE_GP  
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
				         , CAR_NO2          = DD.CAR_NO2       
				         , CR_FRTOMOVE_GP   = DD.CR_FRTOMOVE_GP 
				*/         
		    	commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStockTransInfo_PIDEV", logId, methodNm, "TB_YM_STOCK 등록");
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
				JDTORecordSet jsCarSch = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarYdCmbnCarldYn_PIDEV", logId, methodNm, "차량스케쥴 조회");
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
				
				JDTORecordSet jsCarPnt = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarPoint", logId, methodNm, "차량포인트 조회");
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
				recInTemp.setField("YD_CAR_PROG_STAT"	, "A");								//하차출발상태
				recInTemp.setField("TRANS_ORD_DATE"		, transOrdDt);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);					//운송지시순번 
				//recInTemp.setField("YD_BAYIN_WO_SEQ"	, YmConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 - 기본값으로 설정(9)
				
				if("Y".equals(szMovYn)){
					recInTemp.setField("YD_BAYIN_WO_SEQ"	, "1");	//입동지시순번 - 제품이송하차는 1순위로 변경 함
		    	}else{
		    		recInTemp.setField("YD_BAYIN_WO_SEQ"    , "9");						//입동지시순번 - 기본값으로 설정(9)
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
				commDao.insert(recInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insYdCarsch_PIDEV", logId, methodNm, "TB_YD_CARSCH 등록");
				
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
			
			return jrRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}		
	}
	
	/**
	 * [A] 오퍼레이션명 :코일이송하차도착PDA(DMYDR074)
	 * BCoilL3RcvSeEJBSBean.java	-> rcvDMYDR074
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1101(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YmCoilL3RcvPISeEJBSBean.rcvM10LMYDJ1101 [코일이송하차도착PDA 송신(DMYDR074)]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();		
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "3", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
			commUtils.printParam(logId, rcvMsg);
			
			rcvMsg.setField("TC_CREATE_DDTT" , commUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
			rcvMsg.setField("TRANS_ORD_DT"   , commUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번			
			
			//수신 항목 값
			String msgId    		= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String transOrdDt  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			String transOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
//			String ydCardNo 		= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
			String ydCardNo 		= "";
			String ydCarKind		= commUtils.trim(rcvMsg.getFieldString("CAR_KIND"));     //차량종류
			String WorkGp 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP"));      //작업구분
			String CarldPntCd 		= commUtils.trim(rcvMsg.getFieldString("CARUD_PNT_CD")); //하차포인트 CARUD_PNT_CD ?
			String transFrtoMoveGp 	= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP")); //냉연이송구분 
			
			String modifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String dmTcCode 		= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));//TC_CODE
			String ydCarProgStat    = "";
			String ydCarWrkGp 		= ""; 	//야드차량작업구분
			String ydEqpWrkStat		= ""; 	//야드설비작업상태			
		
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			
			rcvMsg.setField("MODIFIER", sModifier);
			
			
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
			JDTORecordSet jsStock = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTransStockInfo", logId, methodNm, "운송실적번호에 맞는 제품번호가 존재 조회");
			
			String ydCarSchId = commUtils.trim(jsStock.getRecord(0).getFieldString("YD_CAR_SCH_ID")); //차량 작업지시

			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
			JDTORecordSet jsCarSch = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCarsch", logId, methodNm, "TB_YD_CARSCH 조회");
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
				JDTORecordSet jsCarPnt = commDao.select3(jrInTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarPoint", logId, methodNm, "차량포인트 조회");				
				
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

//						if("Y".equals(sApplyYnPI)) {
							String[] rVal = commDao.getTrnFrtomoveGpPI("", methodNm, transOrdDt, transOrdSeqNo);
							String sTrnFrtomoveGp = rVal[0];
							String hIssueGp = rVal[1];
							
							commUtils.printLog(logId, "sTrnFrtomoveGp:" + sTrnFrtomoveGp + "," + "hIssueGp:" + hIssueGp, "SL");
							
//							if (CarldPntCd.substring(1,2).equals("2")) {
//								if( "21".equals(sTrnFrtomoveGp) ) {   //제품이송
//									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT26LM";  
//								} else {
//									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT16LM";
//								}
//							} else {
//								if( "21".equals(sTrnFrtomoveGp) ) {   //제품이송
//									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT22LM";
//								} else {
//									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT12LM";
//								}
//							}
							if ( "21".equals(sTrnFrtomoveGp) && "K".equals(hIssueGp) ) {	// 제품이송
								if ("2".equals(CarldPntCd.substring(1,2))) {
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT26LM";
								} else {
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT22LM";
								}
							} else if ( "22".equals(sTrnFrtomoveGp) && "9".equals(hIssueGp) ) {	// 제품이송
								if ("2".equals(CarldPntCd.substring(1,2))) {
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT26LM";
								} else {
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT22LM";
								}
							} else if ( ("21".equals(sTrnFrtomoveGp) && "C".equals(hIssueGp))
										||
										("21".equals(sTrnFrtomoveGp) && "D".equals(hIssueGp)) ) {	// 이송출고
								if ("2".equals(CarldPntCd.substring(1,2))) {
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT16LM";
								} else {
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT12LM";
								}
							} else {
								if ("2".equals(CarldPntCd.substring(1,2))) {	// 냉연이송상차
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT16LM";
								} else {
									ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT12LM";
								}
							}
							
//						} else {						
//						
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
//							
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
						JDTORecordSet jsCarMtl = commDao.select3(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockOfCarLoad", logId, methodNm, "재료정보 조회");					
						
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
						JDTORecordSet rsResult = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPntFrmYn", logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 ");
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
//			/*************************************** 
//			 * 	1.작업예약이 생성 
//			 **************************************/	
//			//작업예약,작업재료 등록	
//			String ydSchCd  = "";
//			if ("3".equals(ydStkColGp.substring(5 , 6)) || "4".equals(ydStkColGp.substring(5 , 6))) {
//				ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT06LM";
//			} else {
//				ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT02LM";
//			}
//			jrInTemp.setField("YD_SCH_CD"       	, ydSchCd   );// 자동이적 
//			jrInTemp.setField("CAR_NO"				, ydCarNo);
//			jrInTemp.setField("CARD_NO"				, ydCardNo);
//			
//			String ydWbookId = commComm.procCarWkBookInsert(jrInTemp);
//			
//			if(ydWbookId.equals(YmConstant.RETN_CD_FAILURE)){
//				throw new Exception("작업예약ID 생성 실패"); 				
//			}
//			
////			/**********************************************************
////			* 2.2 크레인스케줄 전문 호출
////			**********************************************************/
////			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
////			jrYdMsg.setResultCode(logId);	//Log ID
////			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
////
////			jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId	); //야드작업예약ID
////			jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  	); //야드스케쥴코드
////			jrYdMsg.setField("YD_SCH_ST_GP" , "O"		); //야드스케쥴기동구분
////			jrYdMsg.setField("YD_SCH_REQ_GP", "L"      	); //야드스케쥴요청구분(인출)
////			jrYdMsg.setField("MODIFIER"     , modifier 	); //수정자
////
////			jrRtn = commUtils.addSndData(commComm.getCrnSchMsg(jrYdMsg));				
//
//			//도착위치가 없을 시 도착처리 skip
//			if(ydCarKind.equals("TT")||ydCarKind.equals("T")){
//				ydCarKind="T";
//			}else{
//				if(ydCarKind.equals("TR")){
//					ydCarKind="R";
//				}else{
//					ydCarKind="N";
//				}
//			}
//			
//			if(!ydStkColGp.equals("")){
//				//차량도착처리 
//				jrParam.setField("MOVE_GP"		, ydCarKind); //T: TT,TR:R,그외:N
//				jrParam.setField("CARD_NO" 		, ydCardNo); 
//				jrParam.setField("STACK_COL_GP"	, ydStkColGp);
//				EJBConnector ejbConn = new EJBConnector("default","YmCommCarMvSeEJB",this);
//				jrRst = (JDTORecord)ejbConn.trx("procCarArr", new Class[] { JDTORecord.class }, new Object[] { jrParam });
//			}
//			 
//			//다음 작업예약 호출을 막기 위한 처리 
//			return jrRtn;
//		}
		
		//################################################################################################################
//	} 
	
			commUtils.printLog(logId, methodNm, "S-");			
			
			
			return jrRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}					
	}
	
	
	/**
	 * [A] 오퍼레이션명 :코일이송하차완료PDA(DMYDR075)
	 * BCoilL3RcvSeEJBSBean.java	-> rcvDMYDR075
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1111(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YmCoilL3RcvPISeEJBSBean.rcvM10LMYDJ1111 [코일이송하차완료PDA(DMYDR075)]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();		
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "3", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn;
//			}
			
			commUtils.printParam(logId, rcvMsg);
			
			rcvMsg.setField("TC_CREATE_DDTT" , commUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
			rcvMsg.setField("TRANS_ORD_DT"   , commUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp		= commUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String stlAppearGp 	= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));   //재료외형
			String StockId 		= commUtils.trim(rcvMsg.getFieldString("STL_NO"));    		//재료번호
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
//			String dmTcCode 	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));//TC_CODE
			
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			
			rcvMsg.setField("MODIFIER", sModifier);			
				
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("TC_CD"		, msgId);   //
			jrParam.setField("STOCK_ID" 	, StockId); 						
			jrParam.setField("MODIFIER"     , modifier); //수정자

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
			
			String ydCarNo  	= "";  	
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
				JDTORecordSet jsCarSch = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarStartOrderInfo_PIDEV", logId, methodNm, "차량스케쥴 조회");
				if(jsCarSch.size() > 0) {
					commUtils.printLog(logId, methodNm +" StockId" + StockId +"에 대한 포인트정보가 존재함", "SL");
					ydCarNo  	= commUtils.trim(jsCarSch.getRecord(0).getFieldString("CAR_NO"));  	
					ydStackColGp= commUtils.trim(jsCarSch.getRecord(0).getFieldString("STACK_COL_GP")); 
		 			
					jrParam.setField("CAR_NO"		, ydCarNo);   
					jrParam.setField("STACK_COL_GP" , ydStackColGp); 

		 			if(!ydCarNo.equals("")){
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
			if("M10LMYDJ1111".equals(msgId)){
				CarDnWrkYn = "Y";  //하차작업완료 인경우 생략 ,
			}
 			
			if(CarDnWrkYn.equals("N")){
				/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStacklayerList
				SELECT STOCK_ID , STACK_COL_GP 
				 FROM TB_YM_STACKLAYER
				WHERE STOCK_ID = :V_STOCK_ID
				 AND SUBSTR(STACK_COL_GP,3,2) NOT IN ('TR','PT','TT') --//차량위치가 아닌경우 	
				*/			
				JDTORecordSet jsStacklayer = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStacklayerList", logId, methodNm, "단정보검색");
				
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
	 * 대기장도착실적(DMYDR061)  
	 * YmCommL3RcvSeEJBSBean.java	-> rcvDMYDR061
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procM10LMYDJ_DMYDR061(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대기장도착실적 수신[YmCoilL3RcvPISeEJBBean.procM10LMYDJ_DMYDR061] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();	
		JDTORecordSet rsResult 	= null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			
			//수신 항목 값
			rcvMsg.setField("TRANS_ORD_DT"   , commUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ" )) );
			
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId.substring(3,12); }
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅
			
			String szYD_GP 				= commUtils.trim(rcvMsg.getFieldString("YD_GP")); //야드구분	
			String szCMBN_CARLD_YN 		= commUtils.nvl (rcvMsg.getFieldString("CMBN_CARLD_YN"),"N"); //조합상차유무(시작:S , 종료: E ,  단일상차: N )
			String szWORK_GP 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP")); //작업구분
			String szTEL_NO 			= commUtils.trim(rcvMsg.getFieldString("TEL_NO")); //전화번호
			String szTRANS_ORD_DT  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); //운송지시일자
			String szTRANS_ORD_SEQNO	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송지시순번
			String szCAR_NO 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO")); //차량번호

			String szCAR_KIND 			= commUtils.nvl (rcvMsg.getFieldString("CAR_KIND"),"TR"); //차량종류
			String szWAIT_ARR_DDTT		= commUtils.trim(rcvMsg.getFieldString("WAIT_ARR_DDTT")); //대기장도착시간
			String szWAIT_ARR_GP		= commUtils.trim(rcvMsg.getFieldString("WAIT_ARR_GP")); //대기장도착구분
			String szTRANS_FRTOMOVE_GP	= commUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP")); //1 운송 2 이송
			String szDRIVER_NAME		= commUtils.trim(rcvMsg.getFieldString("DRIVER_NAME")); //운전기사명
			
			//복수창고
			int iYdEqpWrkSh             = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수
			String sDoubleYardYn        = "N"; //복수창고 여부			
			
			//차량정보 존재여부 체크 //////////////////////////////////////////////////////////////////////////////
			jrParam.setField("TRANS_ORD_DT"		, szTRANS_ORD_DT);
			jrParam.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			jrParam.setField("CAR_NO"			, szCAR_NO );
			jrParam.setField("CMBN_CARLD_YN"	, szCMBN_CARLD_YN );
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarYdCmbnCarldYn_PIDEV 
			SELECT *
			  FROM TB_YD_CARSCH
			 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DT
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND CARD_NO         = :V_CARD_NO
			   AND CMBN_CARLD_YN   = :V_CMBN_CARLD_YN
			   AND DEL_YN   = 'N'	
			*/	   
			rsResult = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarYdCmbnCarldYn_PIDEV", logId, methodNm, "차량정보 존재여부 체크");
			
			if (rsResult.size() > 0) {
				
				commUtils.printLog(logId, methodNm + " TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.:기존지시 삭제 " , "SL");	
				
				//기존 차량정보 삭제처리///////////////////////////////////////////////////////////////////////////
				jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID")));
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
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch", logId, methodNm, "TB_YD_CARSCH 기존 차량스케줄 정보 삭제처리");
			}
			
			//도착가능 포인트 조회
			jrParam.setField("YD_GP"			, szYD_GP);
			jrParam.setField("TRANS_ORD_DT"		, szTRANS_ORD_DT);
			jrParam.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarPointSelect 
			--AB열연
			SELECT *
			  FROM (
			         SELECT CC.YD_STK_COL_GP,CC.YD_CARPNT_CD,CC.YD_PNT_CD
			              , DECODE(CC.YD_STK_COL_ACT_STAT,'C',1,2) AS STAT_RANK
			              , (SELECT COUNT(*)
			                   FROM USRYDA.TB_YD_CARSCH A
			                 WHERE A.DEL_YN='N'
			                   AND A.YD_CARLD_STOP_LOC=CC.YD_STK_COL_GP) CARLD_RANK
			              , CC.WLOC_CD 
			           FROM ( 
			                SELECT A.STOCK_ID  
			                     , :V_YD_GP   AS YD_GP 
			                  FROM USRYMA.TB_YM_STOCK A
			                 WHERE A.TRANS_ORD_DATE2  = :V_TRANS_ORD_DT
			                   AND A.TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
			                ) AA
			              , TB_PT_COILCOMM BB
			              , USRYDA.TB_YD_CARPOINT CC
			          WHERE AA.STOCK_ID = BB.COIL_NO
			            AND CC.YD_CARPNT_CD LIKE AA.YD_GP||'%'
			            AND BB.YD_GP = CC.YD_GP
			            AND BB.YD_BAY_GP = CC.YD_BAY_GP
			            AND BB.YD_EQP_GP BETWEEN CC.YD_SPAN_FROM AND CC.YD_SPAN_TO
			            AND CC.YD_CAR_USETYPE_GP IN('TR','RT','RA','TO') --트레일러
			            AND CC.DEL_YN = 'N'
			            AND CC.YD_STK_COL_ACT_STAT <>'N'
			          ORDER BY CARLD_RANK, SUBSTR(YD_STK_COL_GP,2,1)
			       )
			 WHERE ROWNUM<=1 */
			rsResult = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarPointSelect", logId, methodNm, "도착가능 차량포인트 조회");
			if (rsResult.size() <= 0 ) {
				//commUtils.printLog(logId, methodNm + " TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다. " , "SL");
				//m_ctx.setRollbackOnly();
				throw new Exception("TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다....");
			} 
			
			//도착가능 포인트 조회 결과 값
			String szYD_STK_COL_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP")); //야드적치열
			String szYD_CARPNT_CD 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CARPNT_CD")); //차량보인트
			String szYD_PNT_CD 		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD")); //야드포인트코드
			String szSPOS_WLOC_CD	= commUtils.trim(rsResult.getRecord(0).getFieldString("WLOC_CD")); //개소코드
			
			//차량스케줄ID 생성 				
			String szYD_CAR_SCH_ID = commDao.getSeqId(logId, methodNm, "CarSch");
			
			//차량스케줄 등록
			jrParam.setField("YD_CAR_SCH_ID"		, szYD_CAR_SCH_ID);
			jrParam.setField("REGISTER"				, modifier);
			jrParam.setField("YD_EQP_WRK_STAT"		, "U");	//야드설비작업상태
			jrParam.setField("YD_EQP_ID"			, YmConstant.YD_DM_CAR_EQP_ID);	//야드설비ID
			jrParam.setField("YD_CAR_USE_GP"		, YmConstant.YD_CAR_USE_GP_DM);	//차량사용구분 
			jrParam.setField("CAR_NO"				, szCAR_NO); //차량번호
			jrParam.setField("CAR_KIND"				, szCAR_KIND); //차량종류
			jrParam.setField("SPOS_WLOC_CD"			, szSPOS_WLOC_CD); //발지개소코드
			jrParam.setField("YD_CARLD_LEV_DT"		, commUtils.getDateTime14());		//상차출발일시
			jrParam.setField("YD_PNT_CD1"			, szYD_PNT_CD);	//야드포인트코드1
			jrParam.setField("YD_CARLD_STOP_LOC"	, szYD_STK_COL_GP);	//야드상차정지위치 
			jrParam.setField("TRANS_ORD_DATE"		, szTRANS_ORD_DT); //운송지시일자
			jrParam.setField("TRANS_ORD_SEQNO"		, szTRANS_ORD_SEQNO); //운송지시순번 
			if ("E".equals(szCMBN_CARLD_YN)) {
				jrParam.setField("YD_BAYIN_WO_SEQ"	, "1"); //입동지시순번 - 복수상차 마지막 1순위	
			} else {
				jrParam.setField("YD_BAYIN_WO_SEQ"	, YmConstant.YD_BAYIN_WO_SEQ_DEFAULT); //입동지시순번 - 기본값으로 설정(9)
			}
			jrParam.setField("YD_CAR_PROG_STAT"		, "1");	//상차출발상태
			jrParam.setField("YD_CAR_WRK_GP"		, szWORK_GP); //야드차량작업구분
			jrParam.setField("TEL_NO"				, szTEL_NO); //기사핸드폰번호
			jrParam.setField("CMBN_CARLD_YN"		, szCMBN_CARLD_YN);	//첫번째 도착창고 : S 두번째 도착창고 : E
			jrParam.setField("WAIT_ARR_DDTT"		, szWAIT_ARR_DDTT);	//대기장도착시간
			jrParam.setField("WAIT_ARR_GP"			, szWAIT_ARR_GP); //대기장도착구분  - B:BACKUP , S:SMARTPHONE
			jrParam.setField("DRIVER_NAME"			, szDRIVER_NAME); //운전기사명
			
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
			     , YD_CAR_PROG_STAT
			     , YD_CAR_WRK_GP
			     , TRANS_ORD_DATE
			     , TRANS_ORD_SEQNO
			     , YD_BAYIN_WO_SEQ
			     , TEL_NO
			     , CMBN_CARLD_YN
			     , WAIT_ARR_DDTT
			     , WAIT_ARR_GP
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
			     , :V_YD_CAR_PROG_STAT
			     , :V_YD_CAR_WRK_GP
			     , :V_TRANS_ORD_DATE
			     , :V_TRANS_ORD_SEQNO
			     , :V_YD_BAYIN_WO_SEQ
			     , :V_TEL_NO
			     , :V_CMBN_CARLD_YN
			     , :V_WAIT_ARR_DDTT
			     , :V_WAIT_ARR_GP     
			       )
			 */      
			commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insYdCarsch_PIDEV", logId, methodNm, "TB_YD_CARSCH 등록");
						
			//복수 창고 인 경우 :마지막에 E로 오기로 함
			if ("E".equals(szCMBN_CARLD_YN)) {
				String sStlNo       = "";
				String sWorkState   = "";
				String ydGp         = "";
				String sGdsCarldLoc = "";
				String ydStkBedNo   = "";
				
				commUtils.printLog(logId, "복수 창고 여부 check " , "SL");
				for (int ii = 1 ; ii <= iYdEqpWrkSh; ii++) {
					sStlNo 		 = commUtils.trim(rcvMsg.getFieldString("STL_NO"+ii)); //재료번호
					sWorkState   = commUtils.trim(rcvMsg.getFieldString("WORK_STATE"+ii));    //0: 미상차 , 1: 상차
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
				commUtils.printLog(logId, "복수 창고: "+ sDoubleYardYn , "SL");
				// 복수 창고 임
				if ("E".equals(szCMBN_CARLD_YN) && "Y".equals(sDoubleYardYn)) {

/**********************************************
 *   복수창고  로직
 **********************************************/					
					commUtils.printLog(logId, "복수 창고인 경우 " , "SL");
					for (int ii = 1 ; ii <= iYdEqpWrkSh; ii++) {
						ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"+ii)); 
						sStlNo 		 = commUtils.trim(rcvMsg.getFieldString("STL_NO"+ii)); //재료번호
						sGdsCarldLoc = commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+ii)); //상차위치
						sWorkState   = commUtils.trim(rcvMsg.getFieldString("WORK_STATE"+ii));    //0: 미상차 , 1: 상차
						
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
							jrParam1 = commUtils.getParam(logId, methodNm, modifier);
							jrParam1.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID   );
							jrParam1.setField("STL_NO"       , sStlNo       );
							jrParam1.setField("YD_STK_BED_NO", ydStkBedNo ); //야드 차상위치코드
							jrParam1.setField("YD_STK_LYR_NO", "001"        );
							jrParam1.setField("DEL_YN"       , "Y"          );
							/* com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insCarSchmtl
							INSERT INTO TB_YD_CARFTMVMTL(
							       YD_CAR_SCH_ID
							     , STL_NO
							     , REGISTER
							     , REG_DDTT
							     , MODIFIER
							     , MOD_DDTT
							     , DEL_YN
							     , YD_CAR_UPP_LOC_CD
							     , YD_STK_BED_NO
							     , YD_STK_LYR_NO
							     , HCR_GP
							     , STL_PROG_CD
							     , YD_MTL_ITEM
							     , YD_ROUTE_GP
							) VALUES ( 
							       :V_YD_CAR_SCH_ID
							     , :V_STL_NO
							     , :V_MODIFIER
							     , SYSDATE
							     , :V_MODIFIER
							     , SYSDATE
							     , 'N'
							     , :V_YD_CAR_UPP_LOC_CD
							     , :V_YD_STK_BED_NO
							     , :V_YD_STK_LYR_NO
							     , :V_HCR_GP
							     , :V_STL_PROG_CD
							     , :V_YD_MTL_ITEM
							     , :V_YD_ROUTE_GP     
							)
							*/
							commDao.insert(jrParam1, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insCarSchmtl", logId, methodNm, "차량재료 스케쥴 INSERT");
							
						} else {
							// 현재 야드  저장품 table 상차 위치 수정 : 차량예정 정보를 위해서
							jrParam1 = commUtils.getParam(logId, methodNm, modifier);
							jrParam1.setField("YD_CAR_UPP_LOC_CD", ydStkBedNo   );
							jrParam1.setField("STL_NO"           , sStlNo       );

							/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYmStockYdCarUppLocCd_PIDEV  
							UPDATE TB_YM_STOCK
							   SET SHEAR_SUPPLY_SEQ  = :V_YD_CAR_UPP_LOC_CD
							     , MODIFIER = :V_MODIFIER
							     , MOD_DDTT = SYSDATE
							 WHERE STOCK_ID   = :V_STL_NO
							*/
							commDao.update(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYmStockYdCarUppLocCd_PIDEV", logId, methodNm, "TB_YD_STOCK 삭제");
						}	
					}
				}
				
			}			
			if ("E".equals(szCMBN_CARLD_YN) && "N".equals(sDoubleYardYn)) {
/**********************************************
 *   복수동  로직
 **********************************************/				
				// 복수동 마지막 도착시 상차된 정보 INSERT 
				//이송작업재료등록
				jrParam.setField("MODIFIER"	        , modifier); 
				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCarFtMvMtlCmbnCarldYn_PIDEV 
				MERGE INTO TB_YD_CARFTMVMTL TM USING (
				    SELECT CC.COIL_NO          AS STOCK_ID
				         , CC.HCR_GP           AS HCR_GP
				         , CC.RECORD_PROG_STAT AS STL_PROG_CD
				    --         , COIL.YD_STK_BED_NO AS STACK_BED_GP
				         , (SELECT MAX(A.YD_STK_BED_NO)
				              FROM TB_YD_CARFTMVMTL A
				             WHERE A.STL_NO        = CC.COIL_NO
				               AND A.YD_CAR_SCH_ID = (SELECT MAX(YD_CAR_SCH_ID) 
				                                        FROM TB_YD_CARSCH  
				                                       WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DT
				                                         AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				                                         AND CAR_NO         = :V_CAR_NO
				                                         AND CMBN_CARLD_YN = 'S'
				                                       )
				            )                   AS STACK_BED_GP   
				         , '001'                 AS STACK_LAYER_GP
				         , :V_YD_CAR_SCH_ID     AS YD_CAR_SCH_ID
				         , :V_MODIFIER          AS MODIFIER
				         , SYSDATE              AS MOD_DDTT
				         , 'N'                  AS DEL_YN
				      FROM USRPTA.TB_PT_COILCOMM  CC
				     WHERE COIL_NO  IN ( 
				        SELECT B.STL_NO
				          FROM TB_YD_CARSCH A 
				             , TB_YD_CARFTMVMTL B
				         WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				           AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DT
				           AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				           AND A.CAR_NO         = :V_CAR_NO
				           AND A.DEL_YN = 'Y'    
				           AND B.DEL_YN = 'Y' 
				           AND A.CMBN_CARLD_YN = 'S'
				       )    
				) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.STL_NO = DD.STOCK_ID )    
				WHEN NOT MATCHED THEN
				INSERT (TM.YD_CAR_SCH_ID, TM.STL_NO, TM.REGISTER, TM.REG_DDTT,
				        TM.MODIFIER, TM.MOD_DDTT, TM.DEL_YN, TM.YD_STK_BED_NO,
				        TM.YD_STK_LYR_NO, TM.HCR_GP, TM.STL_PROG_CD)
				VALUES (DD.YD_CAR_SCH_ID, DD.STOCK_ID, DD.MODIFIER, DD.MOD_DDTT,
				        DD.MODIFIER, DD.MOD_DDTT, DD.DEL_YN, DD.STACK_BED_GP,
				        DD.STACK_LAYER_GP, DD.HCR_GP, DD.STL_PROG_CD)
				*/        
				
				commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCarFtMvMtlCmbnCarldYn_PIDEV", logId, methodNm, "복수동이송작업재료등록");			
				
				
			}
			
			//입동지시 호출/////////////////////////////////////////////////////////////////////////////////////
//			if (!"E".equals(szCMBN_CARLD_YN)) { // 복수동 권하시 (크레인권하실적[rcvA7YML009]) 입동지시 호출 됨
			if ("E".equals(szCMBN_CARLD_YN) && "N".equals(sDoubleYardYn)) { 
				// 복수동 권하시 (크레인권하실적[rcvA7YML009]) 입동지시 호출 됨
			} else {	
				if (!"".equals(szYD_CARPNT_CD)) {
					//도착가능 포인트가 있으면 입동지시 호출
					commUtils.printLog(logId, methodNm + " 차량입동포인트["+szYD_CARPNT_CD+"], 차량스케줄ID["+szYD_CAR_SCH_ID+"] - 차량입동지시요구 모듈을 호출 " , "SL");
				
					JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);	//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
					recInTemp.setField("JMS_TC_CD"			, "YMYMJ662"); //차량입동지시 요구 기존:YDYDJ662
					recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
					recInTemp.setField("YD_CARPNT_CD"		, szYD_CARPNT_CD); //입동포인트
					recInTemp.setField("YD_CAR_SCH_ID"		, szYD_CAR_SCH_ID);	//차량스케줄ID
	//				recInTemp.setField("CARD_NO"			, szCARD_NO);
					recInTemp.setField("CAR_NO"				, szCAR_NO);
					recInTemp.setField("CAR_KIND"			, szCAR_KIND); //차량종류
					recInTemp.setField("TRANS_FRTOMOVE_GP"	, szTRANS_FRTOMOVE_GP); //1 운송 2 이송
					//recInTemp.setField("TRANS_ORD_DATE"		, szTRANS_ORD_DT); 		//운송지시일자
					//recInTemp.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO); 	//운송지시순번
					
					//JMS 전송
					jrRtn = commUtils.addSndData(jrRtn, recInTemp);
					
				}
			}
			jrRtn = commUtils.addSndData(jrRtn, jrRtn); 
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	

	/**
	 * 코일이송상차대기장도착PDA(DMYDR070) 
	 * BCoilL3RcvSeEJBSBean.java -> rcvDMYDR070
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procM10LMYDJ_DMYDR070(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일이송상차대기장도착PDA 수신[YmCoilL3RcvPISeEJBBean.procM10LMYDJ_DMYDR070] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			rcvMsg.setField("TRANS_ORD_DT"   , commUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ" )) );
			
			String msgId            = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlAppearGp 		= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));    //재료외형
			String transOrdDt  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			String transOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String cancelYn     	= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));
			String ydCarKind		= commUtils.trim(rcvMsg.getFieldString("CAR_KIND"));
			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
			
			String transFrtoMoveGp	= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP")); 	//1 운송 2 이송
			String WorkGp 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP"));   			// 작업구분
			String CarldPntCd		= commUtils.trim(rcvMsg.getFieldString("CARLD_PNT_CD"));   		// 상차포인트
			
			int ydEqpWrkSh 			= Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0"));
			String modifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId.substring(3,12); }
			
			String ugntBayInYn		= commUtils.trim(rcvMsg.getFieldString("UGNT_BAYIN_YN"));  	// 긴급입동유무 (Y:입동순서 1)
			
			String ydSndYn          = commUtils.nvl (rcvMsg.getFieldString("YD_SND_YN"  ),"N"); //복수동시 필요
			String sCmbnCarldYn     = commUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN"  )); //조합상차유무
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
				jrParam.setField("CR_FRTOMOVE_GP" 	, transFrtoMoveGp); 
				jrParam.setField("MODIFIER" 		, modifier); 						
				jrParam.setField("TC_CD"			, msgId);   //
				
//복수동적욥 재 송신시 필요 없음 
				if("N".equals(ydSndYn)) {
					
					for(int i = 1 ; i<=ydEqpWrkSh; i++){				
			
						//jrParam.setField("STOCK_MOVE_TERM"	, StMvTerm); //저장품이송조건
						jrParam.setField("STOCK_ID" 		, commUtils.trim(rcvMsg.getFieldString("STL_NO"+i))); //저장품 ID
						
						JDTORecord jrRtnProg = ymComm.getCoilCurrProgCd(jrParam);
						
						jrParam.setField("STOCK_MOVE_TERM" 	, commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"))); 
						jrParam.setField("SHEAR_SUPPLY_SEQ" , commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i))); //차상위치
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
						     , SHEAR_SUPPLY_GP  = NVL(:V_SHEAR_SUPPLY_GP , SHEAR_SUPPLY_GP)   --차량종류
						     , WBOOK_ID         = NVL(:V_WBOOK_ID        , WBOOK_ID)          --작업예약 사용안함
						     , CR_FRTOMOVE_GP   = NVL(:V_CR_FRTOMOVE_GP  , CR_FRTOMOVE_GP)    -- 냉연이송구분       
						 WHERE STOCK_ID = :V_STOCK_ID
						*/
						intRtnVal=	commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStock_PIDEV", logId, methodNm, "TB_YM_STOCK 수정");
							
			            if(intRtnVal == 0){
			           	   continue;
								//throw new Exception("수신한 재료번호 ["+ StStlNo+"]에 대한 저장품 DATA가 존재하지 않음");
						}
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
					jrParam.setField("CAR_NO"			, ydCarNo);
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
					JDTORecordSet jsCarSch = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarYdCmbnCarldYn_PIDEV", logId, methodNm, "차량스케쥴 조회");
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
					JDTORecordSet jsCarPnt = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarPoint", logId, methodNm, "차량스케쥴 조회");
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
						recInTemp.setField("YD_CAR_PROG_STAT"	, "1");								//상차출발상태
						recInTemp.setField("YD_CAR_WRK_GP"		, WorkGp);
						recInTemp.setField("TRANS_ORD_DATE"		, transOrdDt);						//운송지시일자
						recInTemp.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);					//운송지시순번 
						
						if ("Y".equals(ugntBayInYn)||"E".equals(sCmbnCarldYn)) {
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
//복수동적용						
						recInTemp.setField("CMBN_CARLD_YN", sCmbnCarldYn);	
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
						commDao.insert(recInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insYdCarsch_PIDEV", logId, methodNm, "TB_YD_CARSCH 등록");

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
					        /*
							ejbConn3.trx("YmCarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
						  	             new Object[]{"C",ydCarNo,ydCardNo,ydStkColGp,"","","R"});
						  	 */
							ejbConn3.trx("YmCarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
					  	             new Object[]{"C",ydCarNo,ydCarNo,ydStkColGp,"","","R"});					        
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
// 현재 'N'임							
							jrParam.setField("TRANS_ORD_DATE" 	, transOrdDt);
							jrParam.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);
							
							JDTORecordSet rsResult = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getListA3toA4Dm", logId, methodNm, "A동 2통로 출하 자동이적 대상 리스트 조회");
							
							if (rsResult.size() > 0) {
								//A4 크레인이 갈수 없는 영역에 출하 대상이 존재하면 자동이적  YMYMJ312 호출 
							
								JDTORecord jrYMYMJ312 = JDTORecordFactory.getInstance().create();
								jrYMYMJ312.setResultCode(logId);	//Log ID
								jrYMYMJ312.setResultMsg(methodNm);	//Log Method Name
								jrYMYMJ312.setField("JMS_TC_CD" 		, "YMYMJ312");
								jrYMYMJ312.setField("TRANS_ORD_DATE" 	, transOrdDt);
								jrYMYMJ312.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);
								jrYMYMJ312.setField("MODIFIER"	        , modifier);
								
//								jrRtn = this.rcvYMYMJ312(jrYMYMJ312);
								
								EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
								jrRtn = (JDTORecord)ejbConn.trx("rcvYMYMJ312", new Class[] { JDTORecord.class }, new Object[] { jrYMYMJ312 });
								
								
							} 
						} 
						//-------------------------------------------------------------------------------------------------------------
						
						/***********************************
			        	 * 4. 복수동 마지막 도착시 상차 정보 insert
			        	 ***********************************/
//복수동적용						
						commUtils.printLog(logId, "4. 복수동 마지막 도착시 재료 insert" , "SL");
						if ("E".equals(sCmbnCarldYn)) {
							/* com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updCarFtMvMtlCmbnCarldYn_PIDEV 
							MERGE INTO TB_YD_CARFTMVMTL TM USING (
							    SELECT CC.COIL_NO          AS STOCK_ID
							         , CC.HCR_GP           AS HCR_GP
							         , CC.RECORD_PROG_STAT AS STL_PROG_CD
							    --         , COIL.YD_STK_BED_NO AS STACK_BED_GP
							         , (SELECT MAX(A.YD_STK_BED_NO)
							              FROM TB_YD_CARFTMVMTL A
							             WHERE A.STL_NO        = CC.COIL_NO
							               AND A.YD_CAR_SCH_ID = (SELECT MAX(YD_CAR_SCH_ID) 
							                                        FROM TB_YD_CARSCH  
							                                       WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
							                                         AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
							                                         AND CAR_NO         = :V_CAR_NO
							                                         AND CMBN_CARLD_YN = 'S'
							                                       )
							            )                   AS STACK_BED_GP   
							         , '001'                 AS STACK_LAYER_GP
							         , :V_YD_CAR_SCH_ID     AS YD_CAR_SCH_ID
							         , :V_MODIFIER          AS MODIFIER
							         , SYSDATE              AS MOD_DDTT
							         , 'N'                  AS DEL_YN
							      FROM USRPTA.TB_PT_COILCOMM  CC
							     WHERE COIL_NO  IN ( 
							        SELECT B.STL_NO
							          FROM TB_YD_CARSCH A 
							             , TB_YD_CARFTMVMTL B
							         WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
							           AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
							           AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
							           AND A.CAR_NO          = :V_CAR_NO
							           AND A.DEL_YN = 'Y'    
							           AND B.DEL_YN = 'Y' 
							           AND A.CMBN_CARLD_YN = 'S'
							       )    
							) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.STL_NO = DD.STOCK_ID )    
							WHEN NOT MATCHED THEN
							INSERT (TM.YD_CAR_SCH_ID, TM.STL_NO, TM.REGISTER, TM.REG_DDTT,
							        TM.MODIFIER, TM.MOD_DDTT, TM.DEL_YN, TM.YD_STK_BED_NO,
							        TM.YD_STK_LYR_NO, TM.HCR_GP, TM.STL_PROG_CD)
							VALUES (DD.YD_CAR_SCH_ID, DD.STOCK_ID, DD.MODIFIER, DD.MOD_DDTT,
							        DD.MODIFIER, DD.MOD_DDTT, DD.DEL_YN, DD.STACK_BED_GP,
							        DD.STACK_LAYER_GP, DD.HCR_GP, DD.STL_PROG_CD)
							 */
							commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updCarFtMvMtlCmbnCarldYn_PIDEV", logId, methodNm, "이송재료 등록");
						}						
						
						if (!"E".equals(sCmbnCarldYn)) { // 복수동 권하시 (크레인권하실적[rcvA7YML009]) 입동지시 호출 됨
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
	public JDTORecord receiveCancelPI(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "B열연 출하전문 취소처리[YmCoilL3RcvPISeEJB.receiveCancelPI] < " + rcvMsg.getResultMsg();
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
			commUtils.printParam("rcvMsg", rcvMsg);
			
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
			String infoGp				= commUtils.trim(rcvMsg.getFieldString("INFO_GP"));				//정보구분
			
			if ("".equals(modifier)) { modifier = msgId; }
			//변경자 설정 (insert,update 문에서 사용)
			jrParam.setField("MODIFIER", modifier); //수정자
			
			String sPI0004 = ymComm.BCoilApplyYn("PI0004", "*", "*");
			boolean newUnloadProc = false;
			
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
				jrStlNo = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTransStock", logId, methodNm, "운송지시번호,이송 작업지시 번호로 저장품 조회");				
				if(jrStlNo.size() <= 0 ){
					//throw new Exception("출하/이송/이적 대상을 찾지 못했습니다!!");
					commUtils.printLog("", "출하/이송/이적 대상을 찾지 못했습니다!!", "[INFO]");
					
					if ("Y".equals(sPI0004)) {
						newUnloadProc = true;
						commUtils.printLog(logId, "'PI0004' = 'Y' 운송지시번호로 찾지 못한 경우 차량 번호로 처리 " + String.valueOf(newUnloadProc), "SL");
					}
					
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
	      
			if ("M10LMYDJ1021".equals(msgId) ||  	//코일제품반납대기
		        ("M10LMYDJ1011".equals(msgId) && infoGp.equals("1")) ||  //코일제품목전
		        "M10LMYDJ1071".equals(msgId)) {	//코일제품출하완료
//	    	if("M10LMYDJ1021".equals(msgId) ||  	//코일제품반납대기
//		       YmConstant.DMYDR013.equals(msgId)||  //외판슬라브목전
//	    	   YmConstant.DMYDR014.equals(msgId)||  //코일제품목전
//	    	   YmConstant.DMYDR016.equals(msgId)||  //외판슬라브운송지시대기
//	    	   YmConstant.DMYDR029.equals(msgId)||  //외판슬라브출하완료
//	    	   YmConstant.DMYDR030.equals(msgId)) { //코일제품출하완료
	    		
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
	    		
//	        } else 
//	        if(YmConstant.DMYDR011.equals(msgId)||  //코일제품고간이송지시
//	           YmConstant.DMYDR070.equals(msgId)||  //코일이송상차대기장도착PDA
//	           YmConstant.DMYDR073.equals(msgId)||  //코일이송하차대기장도착PDA
//	           YmConstant.DMYDR060.equals(msgId)||  //코일제품운송상차지시
//	           YmConstant.DMYDR022.equals(msgId)) { //외판슬라브운송상차지시 
           } else if ( "M10LMYDJ1041".equals(msgId) ||  //코일이송상차대기장도착PDA
    			"M10LMYDJ1091".equals(msgId) ||  //코일이송하차대기장도착PDA
    			"M10LMYDJ1031".equals(msgId)) {  //코일제품운송상차지시	        	
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
			if (newUnloadProc == false) {
				// 기존 취소 proccess
				for (int ii = 0; ii < jrStlNo.size(); ii++) {

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
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdWrkbookDelChk", logId, methodNm, "STOCK_ID로 스케줄 ID,작업예약ID 가져오기");

					if (rsResult.size() > 0) {

						sSchId = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
						sBookId = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"));

						if ((sYD_GP.compareTo("1") == 0) || (sYD_GP.compareTo("3") == 0)) {

							if (!sSchId.equals("")) {

								/**********************************************************
								 * 크레인스케줄 취소
								 **********************************************************/
								EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
								JDTORecord jrRst = JDTORecordFactory.getInstance().create();
								jrParam.setField("YD_CRN_SCH_ID", sSchId);
								jrParam.setField("YD_WBOOK_ID", sBookId);
								jrParam.setField("WRK_CNCL_YN", "Y"); //작업취소 여부
								jrRst = (JDTORecord) ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
								jrRtn = commUtils.addSndData(jrRtn, jrRst);

							}

							if (!sBookId.equals("")) {
								/**********************************************************
								 * 작업예약 취소
								 **********************************************************/
								EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
								JDTORecord jrRst = JDTORecordFactory.getInstance().create();
								jrParam.setField("YD_WBOOK_ID", sBookId);
								jrRst = (JDTORecord) ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
								jrRtn = commUtils.addSndData(jrRtn, jrRst);
							}

						} else if ((sYD_GP.compareTo("2") == 0) || (sYD_GP.compareTo("4") == 0 || sYD_GP.compareTo("0") == 0)) {

							//ejbConn.trx("cancelSlabSchInfo",new Class[]{ String.class}, new Object[]{ sSchId });  //확인필요
						}
					}

					if (!"".equals(sCarSchId)) {

						String ydCarProgStat = "";
						String ydCarWrkGp = ""; //야드차량작업구분
						String ydEqpWrkStat = ""; //야드설비작업상태
						String carLdStopLoc = ""; //야드상차정지위치
						String carUdStopLoc = ""; //야드하차정지위치
						String sCarStopLoc = ""; //야드정지위치

						jrParam.setField("YD_CAR_SCH_ID", sCarSchId);

						JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCarsch", logId, methodNm, "TB_YD_CARSCH 조회");
						if (jsCarSch.size() > 0) {

							ydCarProgStat = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT")); //차량진행상태
							ydCarWrkGp = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_WRK_GP")); //야드차량작업구분
							ydEqpWrkStat = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_EQP_WRK_STAT")); //야드설비작업상태

							carLdStopLoc = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CARLD_STOP_LOC")); //야드상차정지위치
							carUdStopLoc = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CARUD_STOP_LOC")); //야드하차정지위치

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
							if ("2".equals(ydCarProgStat) || "3".equals(ydCarProgStat) || "4".equals(ydCarProgStat) || "5".equals(ydCarProgStat)) {
								if (!"".equals(carLdStopLoc)) {
									sCarStopLoc = carLdStopLoc;//상차위치
								}
							} else if ("B".equals(ydCarProgStat) || "C".equals(ydCarProgStat) || "D".equals(ydCarProgStat) || "E".equals(ydCarProgStat)) {
								if (!"".equals(carUdStopLoc)) {
									sCarStopLoc = carUdStopLoc;//하차위치
								}
							}

							if (!"".equals(sCarStopLoc)) {
								jrParam.setField("STACK_COL_GP", sCarStopLoc);
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
								jrParam.setField("YD_CAR_USE_GP", "");
								jrParam.setField("TRN_EQP_CD", "");
								jrParam.setField("CAR_NO", "");
								jrParam.setField("CARD_NO", "");

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
								EJBConnector ejbConn2 = new EJBConnector("default", "YmCommCarMvSeEJB", this);
								ejbConn2.trx("YmCarPointinforeg", new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class },
										new Object[] { "B", "", "", sCarStopLoc, "", "", "C", logId, methodNm });

							}
						} //jsCarSch.size() > 0
					} else {

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
						           AND A.CAR_NO = nvl(:V_CAR_NO ,A.CAR_NO )
						       )
						  AND SUBSTR(STACK_COL_GP ,3,2) ='PT'
						*/
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
						EJBConnector ejbConn2 = new EJBConnector("default", "YmCommCarMvSeEJB", this);
						ejbConn2.trx("YmCarPointinforeg", new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class },
								new Object[] { "A", scarNo, scardNo, "", "", "", "C", logId, methodNm });
					}
				}
				
			} else {
				// newUnloadProc == true 차량 번호로 취소 process

				/**********************************************************
				 * TB_YD_CARFTMVMTL 에서 Stock ID 가져오기
				 **********************************************************/
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarftmvmtl
				--하차대상제 조회  
				SELECT A.YD_CARUD_STOP_LOC   AS STACK_COL_GP
				     , B.YD_STK_BED_NO       AS STACK_BED_GP
				     , '01'                              AS STACK_LAYER_GP
				     , B.STL_NO              AS STOCK_ID 
				  FROM TB_YD_CARSCH A
				     , TB_YD_CARFTMVMTL B
				 WHERE A.CAR_NO = :V_CAR_NO
				   AND A.TRANS_ORD_DATE = :V_TRANS_ORD_DT
				   AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				   AND A.DEL_YN = 'N'
				   AND B.DEL_YN = 'N'
				   AND A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				 ORDER BY B.YD_STK_BED_NO
				*/
				jrParam.setField("TRANS_ORD_DT", sTRANS_ORD_DT);
				jrParam.setField("TRANS_ORD_SEQNO", sTRANS_ORD_SEQNO);
				jrParam.setField("CAR_NO", scarNo);
				jrStlNo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarftmvmtl", logId, methodNm, "하차대상재 조회");

				for (int ii = 0; ii < jrStlNo.size(); ii++) {

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
					            AND A.YD_SCH_CD LIKE '%PT%'
					            AND B.YD_AID_WRK_YN   = 'N' --주작업일경우만 스케줄 삭제
					            AND ROWNUM = 1 ) AS YD_CRN_SCH_ID
					      , (SELECT +INDEX(B1 PK_YM_WRKBOOKMTL)
					                A1.YD_WBOOK_ID AS 
					           FROM TB_YM_WRKBOOK A1
					              , TB_YM_WRKBOOKMTL B1
					          WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
					            AND B1.STOCK_ID = :V_STOCK_ID
					            AND A1.DEL_YN = 'N'
					            AND B1.DEL_YN = 'N'
					            AND A1.YD_SCH_CD LIKE '%PT%'
					            AND ROWNUM = 1 ) AS YD_WBOOK_ID
					   FROM DUAL
					*/
					jrParam.setField("STOCK_ID", sSTL_NO);
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdWrkbookDelChk", logId, methodNm, "STOCK_ID로 스케줄 ID,작업예약ID 가져오기");

					if (rsResult.size() > 0) {

						sSchId = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
						sBookId = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"));

						if ((sYD_GP.compareTo("1") == 0) || (sYD_GP.compareTo("3") == 0)) {

							if (!sSchId.equals("")) {
								/**********************************************************
								 * 크레인스케줄 취소
								 **********************************************************/
								EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
								JDTORecord jrRst = JDTORecordFactory.getInstance().create();
								jrParam.setField("YD_CRN_SCH_ID", sSchId);
								jrParam.setField("YD_WBOOK_ID", sBookId);
								jrParam.setField("WRK_CNCL_YN", "Y"); //작업취소 여부
								jrRst = (JDTORecord) ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
								jrRtn = commUtils.addSndData(jrRtn, jrRst);
							}

							if (!sBookId.equals("")) {
								/**********************************************************
								 * 작업예약 취소
								 **********************************************************/
								EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
								JDTORecord jrRst = JDTORecordFactory.getInstance().create();
								jrParam.setField("YD_WBOOK_ID", sBookId);
								jrRst = (JDTORecord) ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
								jrRtn = commUtils.addSndData(jrRtn, jrRst);
							}
						} else if ((sYD_GP.compareTo("2") == 0) || (sYD_GP.compareTo("4") == 0 || sYD_GP.compareTo("0") == 0)) {
							//ejbConn.trx("cancelSlabSchInfo",new Class[]{ String.class}, new Object[]{ sSchId });  //확인필요
						}
					}
				}
				
				// 차량스케줄 있을 경우 삭제 처리
				if (!"".equals(sCarSchId)) {
					String ydCarProgStat = "";
					String ydCarWrkGp = ""; //야드차량작업구분
					String ydEqpWrkStat = ""; //야드설비작업상태
					String carLdStopLoc = ""; //야드상차정지위치
					String carUdStopLoc = ""; //야드하차정지위치
					String sCarStopLoc = ""; //야드정지위치

					jrParam.setField("YD_CAR_SCH_ID", sCarSchId);

					JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCarsch", logId, methodNm, "TB_YD_CARSCH 조회");
					if (jsCarSch.size() > 0) {

						ydCarProgStat = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_PROG_STAT")); //차량진행상태
						ydCarWrkGp = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_WRK_GP")); //야드차량작업구분
						ydEqpWrkStat = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_EQP_WRK_STAT")); //야드설비작업상태

						carLdStopLoc = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CARLD_STOP_LOC")); //야드상차정지위치
						carUdStopLoc = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CARUD_STOP_LOC")); //야드하차정지위치

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
						if ("2".equals(ydCarProgStat) || "3".equals(ydCarProgStat) || "4".equals(ydCarProgStat) || "5".equals(ydCarProgStat)) {
							if (!"".equals(carLdStopLoc)) {
								sCarStopLoc = carLdStopLoc;//상차위치
							}
						} else if ("B".equals(ydCarProgStat) || "C".equals(ydCarProgStat) || "D".equals(ydCarProgStat) || "E".equals(ydCarProgStat)) {
							if (!"".equals(carUdStopLoc)) {
								sCarStopLoc = carUdStopLoc;//하차위치
							}
						}

						if (!"".equals(sCarStopLoc)) {
							jrParam.setField("STACK_COL_GP", sCarStopLoc);
							jrParam.setField("STACK_LAYER_ACTIVE_STAT", "E");
							jrParam.setField("STACK_LAYER_STAT", "E");

							/*UPDATE USRYMA.TB_YM_STACKLAYER
							   SET MOD_DDTT     = SYSDATE
								 , MODIFIER     = :V_MODIFIER
								 , STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT 
							     , STOCK_ID  = null
							     , STACK_LAYER_STAT = :V_STACK_LAYER_STAT
							 WHERE STACK_COL_GP = :V_STACK_COL_GP*/

							commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YM_STACKLAYER 차량 적치단 정보 초기화");

							jrParam.setField("STACK_COL_ACTIVE_STAT", "L");
							jrParam.setField("YD_CAR_USE_GP", "");
							jrParam.setField("TRN_EQP_CD", "");
							jrParam.setField("CAR_NO", "");
							jrParam.setField("CARD_NO", "");

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
							EJBConnector ejbConn2 = new EJBConnector("default", "YmCommCarMvSeEJB", this);
							ejbConn2.trx("YmCarPointinforeg", new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class }, new Object[] { "B", "", "", sCarStopLoc, "", "", "C", logId, methodNm });
						}
					}
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
	 * 2열연코일야드 출하전문 취소(PI) - receiveCancel - coilReceiveCancel_
	 * @param rcvMsg
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procDmTcCnclYmPI(JDTORecord rcvMsg) throws DAOException { //S
		String mthdNm = "1열연코일야드 운송지시취소[YmCoilL3RcvPISeEJB.procDmTcCnclYmPI] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
	    	
			commUtils.printLog(logId, mthdNm, "S+");
			
			/**********************************************************
			* 0. 
			**********************************************************/
			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String sModifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			mthdNm = msgId.substring(0, 2) + mthdNm;
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);

			commUtils.printLog(logId, "운송지시 취소처리 시작", "SL");
			
			//1.크레인 스케줄취소 ,2 작업예약취소
			String sTransOrdDate  = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"   ));
	    	String sTransOrdSeqno = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			
	    	jrParam.setField("TRANS_ORD_DATE" , sTransOrdDate );
			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);

			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCrnschTransNo_PIDEV 
			SELECT A.YD_CRN_SCH_ID
			     , A.YD_SCH_CD
			  FROM TB_YM_CRNSCH    A
			     , TB_YM_CRNWRKMTL B
			     , TB_YM_STOCK     C
			 WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
			   AND B.STOCK_ID      = C.STOCK_ID
			   AND A.DEL_YN        = 'N'
			   AND C.TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE
			   AND C.TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
			*/   
			JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCrnschTransNo_PIDEV", logId, mthdNm, "크레인스케줄 조회");
			if (jsRst.size() > 0) {
				throw new Exception("크레인 작업지시가 있어서 취소처리 불가 합니다");
			}
				
			/**********************************************************
			* 3. 출하전문 처리(재료번호 N개 : STL_NO1 ... STL_NO20)
			**********************************************************/
			if ("M10LMYDJ1031".equals(msgId)) {
									
		    	String sStlNo         = "";
				for (int i = 0; i < 20; ++i) {
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					
					
					sStlNo = commUtils.trim(rcvMsg.getFieldString("STL_NO"+(i+1)));
					
					if ("".equals(sStlNo)) {
						break;
					}
					
					jrParam.setField("STOCK_ID", sStlNo);
					/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updcDmTcCnclYm_PIDEV
					UPDATE USRYMA.TB_YM_STOCK
					   SET MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					     , STOCK_MOVE_TERM  = 'KG'  --저장품이동조건출하작업지시대기
					     , YD_RULE_PL_RS_GP = NULL  --조합구분
					     , TRANS_WORD_NO    = NULL
					     , TRANS_ORD_DATE2  = NULL   --운송지시
					     , TRANS_ORD_SEQNO2 = NULL  --운송지시행번
					     , SHEAR_SUPPLY_SEQ = NULL  --차상위치
					     , CAR_NO2          = NULL           --차량번호 
					     , SHEAR_SUPPLY_GP  = NULL   --차량종류
					     , WBOOK_ID         = NULL          --작업예약 사용안함
					     , CR_FRTOMOVE_GP   = NULL    -- 냉연이송구분       
					 WHERE STOCK_ID = :V_STOCK_ID
					*/ 
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updcDmTcCnclYm_PIDEV", logId, mthdNm, "저장품 수정");
					
				} //end for
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
	 * 차량정지위치활성/비활성처리 PI_
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procCarPosActiveOrInActive_PIDEV(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "차량정지위치활성/비활성처리[YmCoilL3RcvPISeEJBSBean.procCarPosActiveOrInActive_PIDEV] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			// 수신 항목 값
			String ydStkColGp = commUtils.nvl(rcvMsg.getFieldString("YD_STK_COL_GP"), "");
			String ydCarUseGp = commUtils.nvl(rcvMsg.getFieldString("YD_CAR_USE_GP"), "");
			String sTrnEqpCd = commUtils.nvl(rcvMsg.getFieldString("TRN_EQP_CD"), "");
			String sCarNo = commUtils.nvl(rcvMsg.getFieldString("CAR_NO"), "");
			String ydStkColActStat = commUtils.nvl(rcvMsg.getFieldString("YD_STK_COL_ACT_STAT"), "");

			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); // 수정자(Backup Only)

			commUtils.printLog(logId, "ydStkColGp:" + ydStkColGp + ",ydCarUseGp:" + ydCarUseGp + ",sTrnEqpCd:" + sTrnEqpCd + ",sCarNo:" + sCarNo + ",ydStkColActStat:" + ydStkColActStat, "SL");

			/**********************************************************
			 * 0. 항목 값 Check
			 **********************************************************/
			if ("".equals(ydStkColGp)) {
				commUtils.printLog(logId, "적치열이 존재하지 않습니다.....", "S-");
				return jrRtn;
			}

			if ("L".equals(ydCarUseGp)) { // 구내운송
				if ("L".equals(ydStkColActStat) && "".equals(sTrnEqpCd)) {
					commUtils.printLog(logId, "구내운송은 운송장비코드가 존재해야합니다.", "S-");
					return jrRtn;
				}
			}

			if ("G".equals(ydCarUseGp)) { // 출하
				if ("L".equals(ydStkColActStat)) {
					if ("".equals(sCarNo)) {
						commUtils.printLog(logId, "출하차량은 차량번호가 존재해야합니다.", "S-");
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
				commUtils.printLog(logId, "[" + ydStkColActStat + "]사용할 수 없는 상태값입니다.", "S-");
				return jrRtn;
			}

			/**********************************************************
			 * 1.적치열 활성/비활성 처리
			 **********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_STK_COL_ACT_STAT", ydStkColActStat);
			jrParam.setField("YD_CAR_USE_GP", ydCarUseGp);
			jrParam.setField("TRN_EQP_CD", sTrnEqpCd);
			jrParam.setField("CAR_NO", sCarNo);
			jrParam.setField("YD_STK_COL_GP", ydStkColGp);

			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYmStackcol_PIDEV
			UPDATE TB_YM_STACKCOL SET
			    MOD_DDTT = SYSDATE
			  , MODIFIER = :V_MODIFIER
			  , STACK_COL_ACTIVE_STAT = :V_YD_STK_COL_ACT_STAT
			  , YD_CAR_USE_GP = :V_YD_CAR_USE_GP
			  , TRN_EQP_CD = :V_TRN_EQP_CD
			  , CAR_NO = :V_CAR_NO
			WHERE STACK_COL_GP = :V_YD_STK_COL_GP
			*/
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYmStackcolS", logId, mthdNm, "적치열 수정");

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
			jrParam.setField("STACK_BED_ACTIVE_STAT", ydStkBedActStat);
			jrParam.setField("STACK_COL_GP", ydStkColGp);
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActByCol
			UPDATE TB_YM_STACKER
			   SET MODIFIER              = :V_MODIFIER
			      ,MOD_DDTT              = SYSDATE
			      ,STACK_BED_ACTIVE_STAT = :V_STACK_BED_ACTIVE_STAT
			 WHERE STACK_COL_GP          = :V_STACK_COL_GP
			   AND DEL_YN                = 'N'*/
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActByCol", logId, mthdNm, "적치베드 수정");

			/**********************************************************
			 * 3.적치단 활성/비활성 처리
			 **********************************************************/
			jrParam.setField("STOCK_ID", "");
			jrParam.setField("STACK_LAYER_ACTIVE_STAT", ydStkLyrActStat);
			jrParam.setField("STACK_LAYER_STAT", "E");

			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByLoc2_PIDEV
			UPDATE TB_YM_STACKLAYER SET
			    MODIFIER = :V_MODIFIER
			  , MOD_DDTT = SYSDATE
			  , STOCK_ID = :V_STOCK_ID
			  , STACK_LAYER_STAT = :V_STACK_LAYER_STAT
			  , STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT
			WHERE STACK_COL_GP = :V_STACK_COL_GP
			*/
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByLoc2_PIDEV", logId, mthdNm, "적치단 수정");

			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 출하차량스케줄/차량Point삭제 기능 - 상차지시 취소 시 호출
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord delCarSchNCarPointForDist_PIDEV(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "출하차량스케줄/차량Point삭제[YmCoilL3RcvPISeEJBSBean.delCarSchNCarPointForDist_PIDEV] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			// 수신 항목 값
			String msgId = commUtils.getMsgId(rcvMsg); // EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sTransOrdDate = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			String sTransOrdSeqno = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));

			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); // 수정자(Backup Only)
			if ("".equals(sModifier)) {
				sModifier = msgId.substring(3, 12);
			}

			/**********************************************************
			 * 1. 차량스케줄 조회
			 **********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRANS_ORD_DATE", sTransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);

			/*
			SELECT *
			  FROM TB_YD_CARSCH
			 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND DEL_YN = 'N'
			*/
			JDTORecordSet jsRst = commDao.select3(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarschByTransDTSeq", logId, mthdNm, "차량스케줄 조회");
			if (jsRst.size() <= 0) {
				commUtils.printLog(logId, "운송지시일자 :[" + sTransOrdDate + "] , 운송지시순번[" + sTransOrdSeqno + "]로 차량스케줄 조회 결과 없음", "S-");
				return jrRtn;
			}

			String ydCarSchId = commUtils.trim(jsRst.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
			String ydCarldStopLoc = commUtils.trim(jsRst.getRecord(0).getFieldString("YD_CARLD_STOP_LOC"));
			String sCarNo = commUtils.trim(jsRst.getRecord(0).getFieldString("CAR_NO"));

			/**********************************************************
			 * 2. 조회된 차량스케줄로 차량이송재료/차량스케줄 삭제
			 **********************************************************/
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
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

			commUtils.printLog(logId, "ydCarSchId:" + ydCarSchId + ",ydCarldStopLoc:" + ydCarldStopLoc + ",sCarNo:" + sCarNo, "SL");

			/**********************************************************
			 * 3. 차량진행상태에 따른 차량정지위치 Clear 실행 - 상차도착 시에만 Clear
			 **********************************************************/
			if (!"".equals(ydCarldStopLoc)) {
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_STK_COL_GP", ydCarldStopLoc);

				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmStkcol
				SELECT 
					STACK_COL_GP AS YD_STK_COL_GP
					,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
					,REGISTER AS REGISTER
					,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
					,MODIFIER AS MODIFIER
					,DEL_YN AS DEL_YN
					,YD_GP AS YD_GP
					,(SELECT YD_STK_COL_ACT_STAT 
				        FROM USRYDA.TB_YD_CARPOINT B
				       WHERE B.YD_STK_COL_GP=A.STACK_COL_GP   
				       AND ROWNUM<=1
				    ) AS YD_STK_COL_ACT_STAT
					,YD_CAR_USE_GP AS YD_CAR_USE_GP
					,TRN_EQP_CD AS TRN_EQP_CD
					,CAR_NO AS CAR_NO
					,CARD_NO AS CARD_NO
					,WLOC_CD AS WLOC_CD
					,YD_PNT_CD AS YD_PNT_CD
				FROM TB_YM_STACKCOL A
				WHERE STACK_COL_GP = :V_YD_STK_COL_GP
				  AND DEL_YN ='N'
				*/
				jsRst = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmStkcol", logId, mthdNm, "차량정지위치 조회");
				if (jsRst.size() <= 0) {
					commUtils.printLog(logId, "차량정지위치[" + ydCarldStopLoc + "]조회 결과 없음", "S-");
					return jrRtn;
				}

				String ydStkColCarNo = commUtils.trim(jsRst.getRecord(0).getFieldString("CAR_NO"));

				if (ydStkColCarNo.equals(sCarNo)) {
					commUtils.printLog(logId, "차량정지위치 비활성화", "SL");
					
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
					commUtils.printLog(logId, "차량스케줄의 차량번호[" + sCarNo + "]와 적치열의 차량번호[" + ydStkColCarNo + "]와", "SL");
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
	 * 1열연 출하전문 취소(PI)
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procDmTcCnclPI(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "1열연 출하전문 취소[YmCoilL3RcvPISeEJBSBean.procDmTcCnclPI] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			String msgId = commUtils.getMsgId(rcvMsg); // EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); // 수정자(Backup Only)

			if ("".equals(modifier)) { // 변경자 설정 (insert,update 문에서 사용)
				modifier = msgId.substring(3, 12);
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create(); // Query 실행시 파라메터 전달용 JDTORecord
			jrParam.setResultCode("logId");	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
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
				String sTransOrdDate = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
				String sTransOrdSeqno = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));

				jrParam.setField("TRANS_ORD_DATE", sTransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
				jrParam.setField("TRANS_ORD_DATE2", sTransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO2", sTransOrdSeqno);
				
				/*
				SELECT A.YD_CRN_SCH_ID
				     , A.YD_SCH_CD
				  FROM TB_YM_CRNSCH    A
				     , TB_YM_CRNWRKMTL B
				     , TB_YM_STOCK     C
				 WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
				   AND B.STOCK_ID      = C.STOCK_ID
				   AND A.DEL_YN        = 'N'
				   AND C.TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE
				   AND C.TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
				 */
				JDTORecordSet jsRst = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCrnschTransNo_PIDEV", logId, methodNm, "크레인스케줄 조회");

				if (jsRst.size() > 0) {
					commUtils.printLog(logId, "스케줄 취소대상이 존재함", "SL");

					for (int i = 1; i <= jsRst.size(); ++i) {
						jsRst.absolute(i);
						jrParam.setField("YD_CRN_SCH_ID", jsRst.getRecord().getFieldString("YD_CRN_SCH_ID"));
						jrParam.setField("YD_SCH_CD", jsRst.getRecord().getFieldString("YD_SCH_CD"));
						jrParam.setField("YD_WBOOK_ID", jsRst.getRecord().getFieldString("YD_WBOOK_ID"));
						jrParam.setField("WRK_CNCL_YN", "Y"); // 작업취소 여부

						// 크레인 스케줄 삭제
						EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
						JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });

						jrRtn = commUtils.addSndData(jrRtn, jrRst);
					}
				} else if (jsRst.size() == 0) {
					commUtils.printLog(logId, "스케줄 취소대상이 존재안함", "SL");

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
					     , A.CR_FRTOMOVE_GP --
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
					 --ORDER BY C.STACK_LAYER_GP DESC, B.YD_WBOOK_ID
					 ORDER BY B.YD_WBOOK_ID, C.STACK_LAYER_GP DESC
					 */
					JDTORecordSet jsWrkBk = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockOfCarLoad", logId, methodNm, "작업예약조회");

					if (jsWrkBk.size() > 0) {
						for (int i = 1; i <= jsRst.size(); ++i) {
							jsWrkBk.absolute(i);
							jrParam.setField("YD_WBOOK_ID", jsWrkBk.getRecord().getFieldString("YD_WBOOK_ID"));

							// 크레인 작업예약 삭제
							EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
							JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });

							jrRtn = commUtils.addSndData(jrRtn, jrRst);
						}
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
					sStlNo = commUtils.trim(rcvMsg.getFieldString("STL_NO" + (i + 1)));

					if ("".equals(sStlNo)) {
						break;
					}

					jrParam.setField("STOCK_ID", sStlNo);
					// 운송지시일자(TRANS_ORD_DATE)와 운송지시순번(TRANS_ORD_SEQNO)은 클리어가 되어야 함
					jrParam.setField("TRANS_ORD_DATE", "");
					jrParam.setField("TRANS_ORD_SEQNO", "");

					if ("M10LMYDJ1091".equals(msgId) // 하차대기장도착 (코일)
							|| "M10LMYDJ1031".equals(msgId)) // 운송상차지시 (코일)
					{
						jrParam.setField("CAR_NO2", "");
						jrParam.setField("CAR_CARD_NO", "");
						jrParam.setField("CR_FRTOMOVE_GP", "");
						jrParam.setField("YD_RULE_PL_RS_GP", "");
						jrParam.setField("STOCK_MOVE_TERM", "");

						//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
						String[] rVal = new String[1];
						jrParam.setField("TC_CODE", msgId); //TC_CODE
						jrParam.setField("STL_NO", sStlNo); //저장품 ID
						rVal = commUtils.getYdAimRtGp("C", jrParam); // rVal[0]
						String sYD_AIM_RT_GP = rVal[0];

						jrParam.setField("YD_AIM_RT_GP", sYD_AIM_RT_GP);
					}

					/*	--com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updYdStockReg
					UPDATE TB_YM_STOCK
					SET TRANS_ORD_DATE2 = :V_TRANS_ORD_DATE2
					      ,TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO2
					      ,CAR_CARD_NO = :V_CAR_CARD_NO
					      ,CAR_NO2 = :V_CAR_NO2
					      ,CR_FRTOMOVE_GP = :V_CR_FRTOMOVE_GP
					      ,MODIFIER = :V_MODIFIER
					      ,YD_AIM_RT_GP = :V_YD_AIM_RT_GP
					      ,DEL_YN = :V_DEL_YN
					      ,MOD_DDTT = SYSDATE
					 WHERE STOCK_ID = :V_STOCK_ID
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updYdStockReg", logId, methodNm, "저장품 수정");
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
	 * [A] 오퍼레이션명 :열연옥외야드도착실적  
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1161(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "[YmCoilL3RcvPISeEJB.rcvM10LMYDJ1161] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();  
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}			
			
			//수신 항목 값
			String msgId  = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			// DAO 및 UTIL 객체 생성
			YmEtcDao YmEtcDao   = new YmEtcDao();
			// 레코드 선언
			JDTORecord recIn    = null;
			
		    String sMsg         = "";
		    String sMethodName  = "rcvM10LMYDJ1161";	    	        	
			int intRtnVal = 0;
			
	    	sMsg = "[출하] 열연옥외창고도착실적 수신";
	    	commUtils.printLog(logId, sMsg , "SL");
	    	
	    	/*
	    	YD_GP			야드구분	CHAR	1	V		
	    	TRANSMIT_DATE	전송일자	CHAR	8			
	    	SEND_SEQ		전송순번	NUMBER	5			
	    	CANCEL_YN		취소유무	CHAR	1	Y	Y: 취소 , N: 지시	
	    	FR_GBN			사외창고구분 CHAR  2   VA : 열연옥외야드장
	    	TABLE : TB_DM_SETTLEDOWNWRSLTIFTEMP
			*/ 
	    	
	    	String sYdGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"));    
	    	String sTransmitDate = commUtils.trim(rcvMsg.getFieldString("TRANSMIT_DATE"));    
	    	String sSendSeq      = commUtils.trim(rcvMsg.getFieldString("SEND_SEQ"));    
	    	String sCancelYn     = commUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));    
	    	String sFrGbn        = commUtils.trim(rcvMsg.getFieldString("FR_GBN"));  
	    	String sTrnReqDate   = commUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE"));    
	    	String sTrnReqSeq    = commUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ"));    
	    	String sCarNo        = commUtils.trim(rcvMsg.getFieldString("CAR_NO")); 

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
						commUtils.printLog(logId, sMsg , "SL");
						return ;
					} else if(intRtnVal == 0){
						sMsg = "COILCOMM[COIL작업지시] Error :: TRN_REQ_DATE(" + sTrnReqDate + ") SEND_SEQ(" + sTrnReqSeq + ")[" + intRtnVal + "]" + "DO NOT EXIST";
						commUtils.printLog(logId, sMsg , "SL");
						return ;
					}
				}
	    	}
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}
}
