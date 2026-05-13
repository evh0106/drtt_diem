/**
 * @(#)YdSlabL3RcvPISeEJBBean
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
package com.inisteel.cim.ydPI.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdCodeMapping;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.ydStock.StockSpecReg.StockSpecRegSeEJBBean;
import com.inisteel.cim.ydPI.common.util.PIYdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.ydPI.dao.YdPiDAO;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;


/**
 *      [A] 클래스명 : PI관련 SLAB야드 출하수신 처리
 *
 * @ejb.bean name="YdSlabL3RcvPISeEJB" jndi-name="YdSlabL3RcvPISeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class YdSlabL3RcvPISeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	
	private StockSpecRegSeEJBBean stock = new StockSpecRegSeEJBBean();
	private PIYdUtils     commPiUtils = new PIYdUtils();
	private YdPICommDAO   ydPICommDAO = new YdPICommDAO();
	
	YdDelegate      ydDelegate      = new YdDelegate();
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	

	/**	 
	 * [A] 오퍼레이션명 :제품상태(슬라브)-출하지시대기,목전,반품,운송지시대기
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1013(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YdSlabL3RcvPISeEJB.rcvM10LMYDJ1013] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		
		try {
			
			commPiUtils.printLog(logId, methodNm, "S+");

//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", methodNm, "APPPI0", "S", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}				
			
			YdCodeMapping ydCodeMapping  = new YdCodeMapping();
			YdStockDao ydStockDao        = new YdStockDao();
	
			//수신 항목 값
			String msgId  = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String infoGp = commPiUtils.trim(rcvMsg.getFieldString("INFO_GP")); //야드구분
			// 1':목전정보,'2': 보관지시,'3':반품',4':출하지시대기,'5':운송지시대기
			String sMsg   = "";
			
			JDTORecord reqMsg = JDTORecordFactory.getInstance().create();
			JDTORecord recStockColumn 	 = JDTORecordFactory.getInstance().create();
			
			// reqMsg.addField("MODIFIER", "" ); // 수정자
			
			if("1".equals(infoGp)) { // 외판슬라브목전(DMYDR013)procOutplSlabOrdtrn
				//=============================================================
				// Log 테이블 등록 
				//=============================================================
				sMsg = "[출하] 외판슬라브목전 수신";
				commPiUtils.printLog(logId, sMsg , "SL");
				
				//수신한 재료번호
				String sSTL_NO    = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"));
				String sModifier  = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));
				if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
				String sCANCEL_YN = commPiUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));             
				
				if ("Y".equals(sCANCEL_YN)) {
					
					this.receiveCancel(rcvMsg);
					return ;
					
				}
				
//				recStockColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
//				recStockColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
//				recStockColumn.setField("STL_PROG_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
//				recStockColumn.setField("ORD_YEOJAE_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));
//				recStockColumn.setField("ORD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO"));
//				recStockColumn.setField("ORD_DTL", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL"));
//				recStockColumn.setField("ORD_GP", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_GP"));
//				recStockColumn.setField("CUST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD"));
//				recStockColumn.setField("DEST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"DEST_CD"));
//				recStockColumn.setField("YD_DLVRDD_RULE_DD", 	ydDaoUtils.paraRecChkNull(inRecord,"DLVRDD_RULE_DD"));
//				recStockColumn.setField("DEST_TEL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"DEST_TEL_NO"));
//				recStockColumn.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"));
//				recStockColumn.setField("MODIFIER", 			"DMYDR013");
		        
				recStockColumn.setField("STL_APPEAR_GP"      , commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")));
				recStockColumn.setField("STL_NO"             , commPiUtils.trim(rcvMsg.getFieldString("STL_NO")));
				recStockColumn.setField("STL_PROG_CD"        , commPiUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD")));
				recStockColumn.setField("ORD_YEOJAE_GP"      , commPiUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP")));
				recStockColumn.setField("ORD_NO"             , commPiUtils.trim(rcvMsg.getFieldString("ORD_NO")));
				recStockColumn.setField("ORD_DTL"            , commPiUtils.trim(rcvMsg.getFieldString("ORD_DTL")));
				recStockColumn.setField("ORD_GP"             , commPiUtils.trim(rcvMsg.getFieldString("ORD_GP")));
				recStockColumn.setField("CUST_CD"            , commPiUtils.trim(rcvMsg.getFieldString("CUST_CD")));
				recStockColumn.setField("DEST_CD"            , commPiUtils.trim(rcvMsg.getFieldString("DEST_CD")));
				recStockColumn.setField("YD_DLVRDD_RULE_DD"  , commPiUtils.trim(rcvMsg.getFieldString("YD_DLVRDD_RULE_DD")));
				recStockColumn.setField("DEST_TEL_NO"        , commPiUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO")));
				recStockColumn.setField("DIST_SHIPASSIGN_GP" , commPiUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP")));
				recStockColumn.setField("MODIFIER"           , sModifier);
				
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
//				rVal= YdCommonUtils.getYdAimRtGp("S",inRecord );		
//				recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
				
				String[] rVal = new String[2];
				rVal= YdCommonUtils.getYdAimRtGp("S",rcvMsg );		
				
				recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
				//****************************************************************************************************

				//저장품갱신******************************************************************************************** 
				int intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
				if(intRtnVal <= 0){
					sMsg= "YD_STOCK[외판슬라브목전] UPDATE Error :: [" + intRtnVal + "]" ;
					commPiUtils.printLog(logId, sMsg , "SL");
					return ;
				}
				commPiUtils.printLog(logId, "[2] YD_STOCK[외판슬라브목전] UPDATE Success" , "SL");
				//****************************************************************************************************
				
				JDTORecordSet rsResult	= JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord recGetVal    = null;
				String 	sYD_STK_COL_GP   ="";
				intRtnVal = ydStockDao.getYdStock(recStockColumn, rsResult, 26);
				 
				if(intRtnVal > 0) {
					recGetVal = rsResult.getRecord(0);
					sYD_STK_COL_GP = commPiUtils.trim(recGetVal.getFieldString("YD_STK_COL_GP"));
					if("".equals(sYD_STK_COL_GP )){
						sMsg= "재료번호 ["+sSTL_NO+"] 에 대한 적치단 정보 존재 X" ;
						commPiUtils.printLog(logId, sMsg , "SL");
					}
					else {
						sYD_STK_COL_GP = sYD_STK_COL_GP.substring(0, 1);
					}
				}
				
				if(!"".equals(sYD_STK_COL_GP) && !"S".equals(sYD_STK_COL_GP)){
				
					//======================================================
					// 저장품제원 : 연주슬라브L2 로 송신(YDY1L002)
					//======================================================
					JDTORecord recResult = null;
					recResult = JDTORecordFactory.getInstance().create();
					recResult.setField("MSG_ID"          , "YDY1L002");
					recResult.setField("YD_INFO_SYNC_CD" , "5");    // 5:지정저장품
					recResult.setField("STL_NO"          , sSTL_NO);
					recResult.setField("YD_STK_COL_GP"   , "");
					recResult.setField("YD_STK_BED_NO"   , "");
					ydDelegate.sendMsg(recResult);
				
				}
				
			} else if("3".equals(infoGp)) { // 외판슬라브반품(DMYDR032)procOutplSlabRetngds
	
				//=============================================================
				// Log 테이블 등록 
				// 야드는 X로 일단 두었음 수정해야됨
				//=============================================================
				sMsg = "[출하] 외판슬라브반품 수신";
				commPiUtils.printLog(logId, sMsg , "SL");
				
				//수신한 재료번호
//				sSTL_NO  = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");
				//수신한 재료번호
				String sSTL_NO    = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"));
				String sModifier  = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));
				if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
				//수신한 전문값******************************************************************************************
				/*
				STL_APPEAR_GP		재료외형구분
				STL_NO				재료번호
				CURR_PROG_CD		현재진도코드
				ORD_YEOJAE_GP		주문여재구분
				ORD_NO				주문번호
				ORD_DTL				주문행번
				ORD_GP				수주구분
				CUST_CD				고객코드
				DEST_CD				목적지코드
				DEST_TEL_NO			목적지전화번호
				DIST_SHIPASSIGN_GP	출하배선지시구분
				*/
				
//				recStockColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
//				recStockColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
//				recStockColumn.setField("STL_PROG_CD",			ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
//				recStockColumn.setField("ORD_YEOJAE_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));
//				recStockColumn.setField("ORD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO"));
//				recStockColumn.setField("ORD_DTL", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL"));
//				recStockColumn.setField("ORD_GP", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_GP"));
//				recStockColumn.setField("CUST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD"));
//				recStockColumn.setField("DEST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"DEST_CD"));
//				recStockColumn.setField("DEST_TEL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"DEST_TEL_NO"));
//				recStockColumn.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"));
//				recStockColumn.setField("MODIFIER", 			"DMYDR032");

				recStockColumn.setField("STL_APPEAR_GP"      , commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")));
				recStockColumn.setField("STL_NO"             , commPiUtils.trim(rcvMsg.getFieldString("STL_NO")));
				recStockColumn.setField("STL_PROG_CD"        , commPiUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD")));
				recStockColumn.setField("ORD_YEOJAE_GP"      , commPiUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP")));
				recStockColumn.setField("ORD_NO"             , commPiUtils.trim(rcvMsg.getFieldString("ORD_NO")));
				recStockColumn.setField("ORD_DTL"            , commPiUtils.trim(rcvMsg.getFieldString("ORD_DTL")));
				recStockColumn.setField("ORD_GP"             , commPiUtils.trim(rcvMsg.getFieldString("ORD_GP")));
				recStockColumn.setField("CUST_CD"            , commPiUtils.trim(rcvMsg.getFieldString("CUST_CD")));
				recStockColumn.setField("DEST_CD"            , commPiUtils.trim(rcvMsg.getFieldString("DEST_CD")));
				recStockColumn.setField("DEST_TEL_NO"        , commPiUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO")));
				recStockColumn.setField("DIST_SHIPASSIGN_GP" , commPiUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP")));
				recStockColumn.setField("MODIFIER"           , sModifier);				
				
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
//				rVal= YdCommonUtils.getYdAimRtGp("S",inRecord );		
//				recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
				String[] rVal = new String[2];
				rVal= YdCommonUtils.getYdAimRtGp("S",rcvMsg );	
				recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
				//****************************************************************************************************

							
				//저장품갱신******************************************************************************************** 
				int intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
				if(intRtnVal <= 0){
					sMsg = "YD_STOCK[외판슬라브반품] UPDATE Error :: [" + intRtnVal + "]" ;
					commPiUtils.printLog(logId, sMsg , "SL");
					return ;
				}
//				ydUtils.putLog(sSessionName, sMethodName,"[2] YD_STOCK[외판슬라브반품] UPDATE Success",3);
				commPiUtils.printLog(logId, "[2] YD_STOCK[외판슬라브반품] UPDATE Success" , "SL");
				//=====================================================================================================
				// 권오창
				// DMYDR032, DMYDR033, DMYDR034 => 차량스케줄 삭제 및 차량POINT Clear
				// 기존에 만들어져 있는 delCarSchNCarPointForDist() 호출
				//=====================================================================================================
				// 레코드생성
				JDTORecordSet rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord recPara      = JDTORecordFactory.getInstance().create();
				JDTORecord recGetVal    = JDTORecordFactory.getInstance().create();
				String sTRANS_ORD_DATE  = "";
				String sTRANS_ORD_SEQNO = "";
				
				recPara.setField("STL_NO", sSTL_NO);

				// 재료번호로 저장품 조회(운송일자, 운송순번)
				int nRet = ydStockDao.getYdStock(recPara, rsResult, 0);
				if(nRet < 0){
					sMsg = "[외판슬라브반품(DMYDR032)] 저장품 조회 오류 nRet[" + nRet + "] STL_NO(" + sSTL_NO + ")";
					commPiUtils.printLog(logId, sMsg , "SL");
					
				} else if(nRet == 0){
					sMsg = "[외판슬라브반품(DMYDR032)] 저장품 조회 건수가 없음 nRet[" + nRet + "] STL_NO(" + sSTL_NO + ")";
					commPiUtils.printLog(logId, sMsg , "SL");							
				} else {
					sMsg = "[외판슬라브반품(DMYDR032)] 저장품 조회 성공  nRet[" + nRet + "] STL_NO(" + sSTL_NO + ")";
					commPiUtils.printLog(logId, sMsg , "SL");							
				
					rsResult.first();
					recGetVal = rsResult.getRecord();
						
					sTRANS_ORD_DATE  = commPiUtils.trim(recGetVal.getFieldString("TRANS_ORD_DATE"));
					sTRANS_ORD_SEQNO = commPiUtils.trim(recGetVal.getFieldString("TRANS_ORD_SEQNO"));
						
					if(!sTRANS_ORD_DATE.equals("") && !sTRANS_ORD_SEQNO.equals("")){
						// 레코드생성
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("TRANS_ORD_DATE" , sTRANS_ORD_DATE);
						recPara.setField("TRANS_ORD_SEQNO", sTRANS_ORD_SEQNO);
						recPara.setField("PI_YD"          , "S");
						
						// 차량스케줄 삭제 및 차량 포인트 클리어
						sMsg = "[외판슬라브반품(DMYDR032)] 차량스케줄삭제 및 차량Point Clear 시작 - TRANS_ORD_DATE(" + sTRANS_ORD_DATE + ") TRANS_ORD_SEQNO(" + sTRANS_ORD_SEQNO + ")";
						commPiUtils.printLog(logId, sMsg , "SL");	
//PIDEV_S :병행가동용:PI_YD
						recPara.setField("PI_YD",    	"S");		
						String sRtnMsg = YdCommonUtils.delCarSchNCarPointForDist(recPara, sModifier);
						
						sMsg = "[외판슬라브반품(DMYDR032)] 차량스케줄삭제 및 차량Point Clear 완료 - " + sRtnMsg;
						commPiUtils.printLog(logId, sMsg , "SL");	
					}else {
						sMsg = "[외판슬라브반품(DMYDR032)] 운송일자 혹은 순번의 값이 공백. TRANS_ORD_DATE(" + sTRANS_ORD_DATE + ") TRANS_ORD_SEQNO(" + sTRANS_ORD_SEQNO + ")";
						commPiUtils.printLog(logId, sMsg , "SL");						
					}
				}
				
				//======================================================
				// 저장품제원 : 연주슬라브L2 로 송신(YDY1L002)
				//======================================================
				JDTORecord recResult = null;
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YDY1L002");
				recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
				recResult.setField("STL_NO"         , sSTL_NO);
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				ydDelegate.sendMsg(recResult);

				
			} else if("4".equals(infoGp)) { // 외판슬라브출하지시대기(DMYDR004)procOutplSlabDistOrdWait

				//=============================================================
				// Log 테이블 등록 
				// 야드는 X로 일단 두었음 수정해야됨
				//=============================================================
				sMsg = "[출하] 외판슬라브출하지시대기 수신";
				commPiUtils.printLog(logId, sMsg , "SL");
				
				//수신한 재료번호
				String sYD_GP     = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"));
				String sSTL_NO    = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"));
				String sModifier  = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER")); 
				if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
				
				JDTORecord recSet = JDTORecordFactory.getInstance().create();
				
				recSet.setField("STL_APPEAR_GP"      , commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")));
				recSet.setField("STL_NO"             , commPiUtils.trim(rcvMsg.getFieldString("STL_NO")));
				recSet.setField("STL_PROG_CD"        , commPiUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD")));
				recSet.setField("ORD_YEOJAE_GP"      , commPiUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP")));
				recSet.setField("ORD_NO"             , commPiUtils.trim(rcvMsg.getFieldString("ORD_NO")));
				recSet.setField("ORD_DTL"            , commPiUtils.trim(rcvMsg.getFieldString("ORD_DTL")));
				recSet.setField("ORD_GP"             , commPiUtils.trim(rcvMsg.getFieldString("ORD_GP")));
				recSet.setField("CUST_CD"            , commPiUtils.trim(rcvMsg.getFieldString("CUST_CD")));
				recSet.setField("DEST_CD"            , commPiUtils.trim(rcvMsg.getFieldString("DEST_CD")));
				recSet.setField("YD_DLVRDD_RULE_DD"  , commPiUtils.trim(rcvMsg.getFieldString("YD_DLVRDD_RULE_DD")));
				recSet.setField("DEST_TEL_NO"        , commPiUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO")));
				recSet.setField("DIST_SHIPASSIGN_GP" , commPiUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP")));
				recSet.setField("MODIFIER"           , sModifier);				
				
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
//				rVal= YdCommonUtils.getYdAimRtGp("S",inRecord );		
//				recSet.setField("YD_AIM_RT_GP", rVal[0]);
				String[] rVal = new String[2];
				rVal= YdCommonUtils.getYdAimRtGp("S",rcvMsg );
				recSet.setField("YD_AIM_RT_GP", rVal[0]);
	            //=============================================================================================
				// 코드 매핑값 호출 
				//
				//     * 앞에서 코드매핑값에 대해 어떤처리를 하든지 다시 엎어침... 
				//       나중에 앞에서 아래코드에 대해 처리하는 부분 삭제해야 됨                         
	            //=============================================================================================
				JDTORecord outRecTemp = JDTORecordFactory.getInstance().create();
				int nRet = ydCodeMapping.MakeCodeMapping(msgId, sSTL_NO, rcvMsg, outRecTemp);
				if(nRet <= 0){
					String sTempSTL_APPEAR_GP = commPiUtils.trim(outRecTemp.getFieldString("STL_APPEAR_GP"));
					if(!sTempSTL_APPEAR_GP.trim().equals("")){
						recSet.setField("STL_APPEAR_GP", sTempSTL_APPEAR_GP);
					}

					String sTempSCARFING_YN = commPiUtils.trim(outRecTemp.getFieldString("SCARFING_YN")); 
					if(!sTempSCARFING_YN.trim().equals("")){
						recSet.setField("SCARFING_YN", sTempSCARFING_YN);
					}

					String sSCARFING_DONE_YN = commPiUtils.trim(outRecTemp.getFieldString("SCARFING_DONE_YN")); 
					if(!sSCARFING_DONE_YN.trim().equals("")){
						recSet.setField("SCARFING_DONE_YN", sSCARFING_DONE_YN);
					}
					
					sMsg = "[nRet " + nRet + "] 매핑되는 코드가 없습니다. 재료외형, 스카핑여부, 스카핑 완료여부는 업데이트를 위함 STL_APPEAR_GP(" + sTempSTL_APPEAR_GP + ") SCARFING_YN(" + sTempSCARFING_YN + ") SCARFING_DONE_YN(" + sSCARFING_DONE_YN + ")";
					commPiUtils.printLog(logId, sMsg , "SL");
				}else {
					String sSTL_APPEAR_GP = commPiUtils.trim(outRecTemp.getFieldString("STL_APPEAR_GP"));
					if(!sSTL_APPEAR_GP.equals("")){
						recSet.setField("STL_APPEAR_GP"   , sSTL_APPEAR_GP);
					}
					
					String sYD_AIM_RT_GP = commPiUtils.trim(outRecTemp.getFieldString("YD_AIM_RT_GP")); 
					if(!sYD_AIM_RT_GP.equals("")){
						recSet.setField("YD_AIM_RT_GP"    , sYD_AIM_RT_GP);
					}

					String sYD_AIM_YD_GP = commPiUtils.trim(outRecTemp.getFieldString("YD_AIM_YD_GP"));
					if(!sYD_AIM_YD_GP.equals("")){
						recSet.setField("YD_AIM_YD_GP"    , sYD_AIM_YD_GP);
					}

					String sYD_AIM_BAY_GP = commPiUtils.trim(outRecTemp.getFieldString("YD_AIM_BAY_GP"));
					if(!sYD_AIM_BAY_GP.equals("")){
						recSet.setField("YD_AIM_BAY_GP"   , sYD_AIM_BAY_GP);
					}
					
					String sSCARFING_YN = commPiUtils.trim(outRecTemp.getFieldString("SCARFING_YN"));
					if(!sSCARFING_YN.equals("")){
						recSet.setField("SCARFING_YN"     , sSCARFING_YN);
					}
					
					String sSCARFING_DONE_YN = commPiUtils.trim(outRecTemp.getFieldString("SCARFING_DONE_YN"));
					if(!sSCARFING_DONE_YN.equals("")){
						recSet.setField("SCARFING_DONE_YN"   , sSCARFING_DONE_YN);
					}				
				}
	            //=============================================================================================
				
				stock.setYdStkLocTpCd(recSet);
				
				//저장품갱신******************************************************************************************** 
				int intRtnVal = ydStockDao.updYdStock(recSet, 0);
				if(intRtnVal <= 0){
					sMsg= "YD_STOCK[외판슬라브출하지시대기] UPDATE Error :: [" + intRtnVal + "]" ;
					commPiUtils.printLog(logId, sMsg , "SL");
					return ;
				}
				commPiUtils.printLog(logId, "[2] YD_STOCK[외판슬라브출하지시대기] UPDATE Success" , "SL");
				//****************************************************************************************************

				if(sYD_GP.equals("A")) { //야드구분이 연주슬라브야드인 경우에만 
				//======================================================
				// 저장품제원 : 연주슬라브L2 로 송신(YDY1L002)
				//======================================================
					JDTORecord recResult = null;
					recResult = JDTORecordFactory.getInstance().create();
					recResult.setField("MSG_ID"         , "YDY1L002");
					recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
					recResult.setField("STL_NO"         , commPiUtils.trim(recSet.getFieldString("STL_NO")));
					recResult.setField("YD_STK_COL_GP"  , "");
					recResult.setField("YD_STK_BED_NO"  , "");
					ydDelegate.sendMsg(recResult);
				}			
				
			} else if("5".equals(infoGp)) { // 외판슬라브운송지시대기(DMYDR016)procOutplSlabTrnOrdWait
	
	
//				//=============================================================
//				// Log 테이블 등록 
//				//=============================================================
				sMsg = "[출하] 외판슬라브운송지시대기 수신";
				commPiUtils.printLog(logId, sMsg , "SL");
				
				//수신한 재료번호
				String sYD_GP     = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"));
				String sSTL_NO    = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"));
				String sModifier  = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER")); 
				if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
				String sCANCEL_YN = commPiUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));             
				
				if ("Y".equals(sCANCEL_YN)) {
					
					this.receiveCancel(rcvMsg);
					return ;
				}
				recStockColumn.setField("STL_APPEAR_GP"      , commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")));
				recStockColumn.setField("STL_NO"             , commPiUtils.trim(rcvMsg.getFieldString("STL_NO")));
				recStockColumn.setField("STL_PROG_CD"        , commPiUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD")));
				recStockColumn.setField("ORD_YEOJAE_GP"      , commPiUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP")));
				recStockColumn.setField("ORD_NO"             , commPiUtils.trim(rcvMsg.getFieldString("ORD_NO")));
				recStockColumn.setField("ORD_DTL"            , commPiUtils.trim(rcvMsg.getFieldString("ORD_DTL")));
				recStockColumn.setField("ORD_GP"             , commPiUtils.trim(rcvMsg.getFieldString("ORD_GP")));
				recStockColumn.setField("CUST_CD"            , commPiUtils.trim(rcvMsg.getFieldString("CUST_CD")));
				recStockColumn.setField("DEST_CD"            , commPiUtils.trim(rcvMsg.getFieldString("DEST_CD")));
				recStockColumn.setField("YD_DLVRDD_RULE_DD"  , commPiUtils.trim(rcvMsg.getFieldString("YD_DLVRDD_RULE_DD")));
				recStockColumn.setField("DEST_TEL_NO"        , commPiUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO")));
				recStockColumn.setField("DIST_SHIPASSIGN_GP" , commPiUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP")));
				recStockColumn.setField("MODIFIER"           , sModifier);
				

				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
//				rVal= YdCommonUtils.getYdAimRtGp("S",inRecord );		
//				recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
				String[] rVal = new String[2];
//				rVal = ydPiDAO.getYdAimRtGpPI("S" , rcvMsg );
				rVal= YdCommonUtils.getYdAimRtGp("S",rcvMsg );		
				recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
				//****************************************************************************************************

	            //=============================================================================================
				// 코드 매핑값 호출 
				//
				//     * 앞에서 코드매핑값에 대해 어떤처리를 하든지 다시 엎어침... 
				//       나중에 앞에서 아래코드에 대해 처리하는 부분 삭제해야 됨                         
	            //=============================================================================================
				JDTORecord outRecTemp = JDTORecordFactory.getInstance().create();
				int nRet = ydCodeMapping.MakeCodeMapping(msgId, sSTL_NO, rcvMsg, outRecTemp);
				if(nRet <= 0){
					String sTempSTL_APPEAR_GP = commPiUtils.trim(outRecTemp.getFieldString("STL_APPEAR_GP"));
					if(!sTempSTL_APPEAR_GP.trim().equals("")){
						recStockColumn.setField("STL_APPEAR_GP", sTempSTL_APPEAR_GP);
					}
					
					String sTempSCARFING_YN =  commPiUtils.trim(outRecTemp.getFieldString("SCARFING_YN")); 
					if(!sTempSCARFING_YN.trim().equals("")){
						recStockColumn.setField("SCARFING_YN", sTempSCARFING_YN);
					}

					String sSCARFING_DONE_YN =  commPiUtils.trim(outRecTemp.getFieldString("SCARFING_DONE_YN"));
					if(!sSCARFING_DONE_YN.trim().equals("")){
						recStockColumn.setField("SCARFING_DONE_YN", sSCARFING_DONE_YN);
					}
					
					sMsg = "[nRet " + nRet + "] 매핑되는 코드가 없습니다. 재료외형, 스카핑여부, 스카핑 완료여부는 업데이트를 위함 STL_APPEAR_GP(" + sTempSTL_APPEAR_GP + ") SCARFING_YN(" + sTempSCARFING_YN + ") SCARFING_DONE_YN(" + sSCARFING_DONE_YN + ")";
					commPiUtils.printLog(logId, sMsg , "SL");
				}else {
					String sSTL_APPEAR_GP = commPiUtils.trim(outRecTemp.getFieldString("STL_APPEAR_GP"));
					if(!sSTL_APPEAR_GP.equals("")){
						recStockColumn.setField("STL_APPEAR_GP"   , sSTL_APPEAR_GP);
					}
					
					String sYD_AIM_RT_GP = commPiUtils.trim(outRecTemp.getFieldString("YD_AIM_RT_GP"));
					if(!sYD_AIM_RT_GP.equals("")){
						recStockColumn.setField("YD_AIM_RT_GP"    , sYD_AIM_RT_GP);
					}

					String sYD_AIM_YD_GP = commPiUtils.trim(outRecTemp.getFieldString("YD_AIM_YD_GP"));
					if(!sYD_AIM_YD_GP.equals("")){
						recStockColumn.setField("YD_AIM_YD_GP"    , sYD_AIM_YD_GP);
					}

					String sYD_AIM_BAY_GP = commPiUtils.trim(outRecTemp.getFieldString("YD_AIM_BAY_GP"));
					if(!sYD_AIM_BAY_GP.equals("")){
						recStockColumn.setField("YD_AIM_BAY_GP"   , sYD_AIM_BAY_GP);
					}
					
					String sSCARFING_YN = commPiUtils.trim(outRecTemp.getFieldString("SCARFING_YN"));
					if(!sSCARFING_YN.equals("")){
						recStockColumn.setField("SCARFING_YN"     , sSCARFING_YN);
					}
					
					String sSCARFING_DONE_YN = commPiUtils.trim(outRecTemp.getFieldString("SCARFING_DONE_YN"));
					if(!sSCARFING_DONE_YN.equals("")){
						recStockColumn.setField("SCARFING_DONE_YN"   , sSCARFING_DONE_YN);
					}		
				}
	            //=============================================================================================
				
				stock.setYdStkLocTpCd(recStockColumn);
				
				//저장품갱신******************************************************************************************** 
				int intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
				if(intRtnVal <= 0){
					sMsg= "YD_STOCK[외판슬라브운송지시대기] UPDATE Error :: [" + intRtnVal + "]" ;
					commPiUtils.printLog(logId, sMsg , "SL");
					return ;
				}
				
				commPiUtils.printLog(logId, "[2] YD_STOCK[외판슬라브운송지시대기] UPDATE Success" , "SL");
				//****************************************************************************************************

				if(sYD_GP.equals("A")){ //야드구분이 연주슬라브야드인 경우에만 
					//======================================================
					// 저장품제원 : 연주슬라브L2 로 송신(YDY1L002)
					//======================================================
					JDTORecord recResult = null;
					recResult = JDTORecordFactory.getInstance().create();
					recResult.setField("MSG_ID"         , "YDY1L002");
					recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
					recResult.setField("STL_NO"         ,  commPiUtils.trim(recStockColumn.getFieldString("STL_NO")));;
					recResult.setField("YD_STK_COL_GP"  , "");
					recResult.setField("YD_STK_BED_NO"  , "");
					ydDelegate.sendMsg(recResult);
				}
			}
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, methodNm, e));
		}
	}		
		
	
	/**	 
	 * [A] 오퍼레이션명 :SLAB운송상차지시(DMYDR060)procCoilGdsTrnOrdNEW
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1033(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YdSlabL3RcvPISeEJB.rcvM10LMYDJ1033] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		
		try {
			
			commPiUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", methodNm, "APPPI0", "S", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}				
			
			YdStockDao ydStockDao = new YdStockDao();
			//수신 항목 값
			String msgId    	    = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번

			
			String sCMBN_CARLD_YN   = commPiUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN"     ));   // 조합상차유무
			String sSTL_APPEAR_GP   = commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"     ));   // 재료외형구분
            String sTRANS_ORD_DT    = commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE"      )); //운송지시일자
			String sTRANS_ORD_SEQNO = commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ"       )); //운송지시순번
            String sCAR_NO          = commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"            ));   // 차량번호
            String sLOT_NO          = commPiUtils.trim(rcvMsg.getFieldString("LOT_NO"            ));   // carLOT번호
            String sCAR_KIND        = commPiUtils.trim(rcvMsg.getFieldString("CAR_KIND"          ));   // 차량종류
            String sCANCEL_YN       = commPiUtils.trim(rcvMsg.getFieldString("CANCEL_YN"         ));   // Y: 취소 , N: 지시

            int sYD_EQP_WRK_SH      = Integer.parseInt(commPiUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수
            
            String sModifier        = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER")); 
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
            
			if ("Y".equals(sCANCEL_YN)) {
				
				this.receiveCancel(rcvMsg);
				return ;
			}
			
			String sMsg           = "";
			String sYD_GP         = "";
			String sSTL_NO        = "";
			String sOperationName = "제품운송상차지시";
			
			//저장품정보 등록 ///////////////////////////////////////////////////////////////////////////////////////
            JDTORecord recEditColumn 	= JDTORecordFactory.getInstance().create();
			recEditColumn.setField("YD_RULE_PL_RS_GP", 		sCMBN_CARLD_YN);
			recEditColumn.setField("STL_APPEAR_GP", 		sSTL_APPEAR_GP);
			recEditColumn.setField("TRANS_ORD_DATE", 		sTRANS_ORD_DT);
			recEditColumn.setField("TRANS_ORD_SEQNO", 		sTRANS_ORD_SEQNO); 
 			recEditColumn.setField("CAR_NO", 				sCAR_NO);
//			recEditColumn.setField("CARD_NO", 				sCARD_NO);
			recEditColumn.setField("CAR_LOTID", 			sLOT_NO);
			recEditColumn.setField("MODIFIER", 				sModifier);
			
			//박판열연 신규모듈 적용여부 조회
 //           YdPlateCommDAO commDao2	= new YdPlateCommDAO();
//            JDTORecord jrResult		= commDao2.getYfNewModuleEffYn();

			//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
			for(int i = 1 ; i<=sYD_EQP_WRK_SH; i++){
				recEditColumn.setField("STL_NO"            , commPiUtils.trim(rcvMsg.getFieldString("STL_NO" + i) ));				
				recEditColumn.setField("YD_CAR_UPP_LOC_CD" , commPiUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC" + i))); //야드 차상위치코드
				
				//?????????
				if("S".equals(sCMBN_CARLD_YN) && "TR".equals(sCAR_KIND) && "".equals(commPiUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"))) ){
					recEditColumn.setField("YD_CAR_UPP_LOC_CD", 	"0"+i );
				}
				sMsg="[" +  "] "+commPiUtils.trim(rcvMsg.getFieldString("STL_NO" + i) )+"차상위치 세팅[YD_CAR_UPP_LOC_CD : " + recEditColumn.getField("YD_CAR_UPP_LOC_CD") + "]";
				commPiUtils.printLog(logId, sMsg , "SL");
				
				sYD_GP = commPiUtils.trim(rcvMsg.getFieldString("YD_GP" + i) );
				
				//STL_NO가 없다면 loop종료
				sSTL_NO = recEditColumn.getFieldString("STL_NO");
				if(sSTL_NO.equals("")){
					break;
				}
				

				//C지구 
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)***************************************************
				JDTORecord recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO",  sSTL_NO);
				recPara.setField("TC_CODE", msgId);
				
				//작업예약만 존재 하는 경우 강제 삭제처리
				ydPICommDAO.update(recPara, "com.inisteel.cim.yd.ydStock.RouteModReg.updWbookCancel", "DMYDR060", methodNm, "작업예약 삭제");
				
				ydPICommDAO.update(recPara, "com.inisteel.cim.yd.ydStock.RouteModReg.updWbookMtlCancel", "DMYDR060", methodNm, "작업예약제료 삭제");
								
//				rVal= YdCommonUtils.getYdAimRtGp("C",recPara );		
//				recEditColumn.setField("YD_AIM_RT_GP", rVal[0]);
//				recEditColumn.setField("STL_PROG_CD", rVal[1]);
				
				String[] rVal = new String[2];
				//rVal = ydPiDAO.getYdAimRtGpPI("C" , rcvMsg );		
				rVal= YdCommonUtils.getYdAimRtGp("C" , rcvMsg );	
				recEditColumn.setField("YD_AIM_RT_GP", rVal[0]);
				recEditColumn.setField("STL_PROG_CD" , rVal[1]);
				
				recEditColumn.setField("DEL_YN", "N");
				recEditColumn.setField("YD_STK_BED_NO",sCAR_KIND);
					
				//저장품갱신*****************************************************************************************
				int intRtnVal = ydStockDao.updYdStock(recEditColumn);
				if(intRtnVal <= 0){
					sMsg= "YD_STOCK["+sOperationName+"] UPDATE Error :: [" + intRtnVal + "]" ;
					commPiUtils.printLog(logId, sMsg , "SL");					 
				}

				commPiUtils.printLog(logId, "YD_STOCK["+sOperationName+"] UPDATE Success" , "SL");

			} //end of for ****************************************************************************************
 
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, methodNm, e));
		}		
	}
	
	
	/**	 
	 * [A] 오퍼레이션명 :SLAB대기장도착실적 송신(DMYDR061)procStandByYdArrive
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1043(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YdSlabL3RcvPISeEJB.rcvM10LMYDJ1043] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		
		try {
			
			commPiUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", methodNm, "APPPI0", "S", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}				
			
			YdCarSchDao ydCarSchDao	= new YdCarSchDao();
			
			String sMsg = "";
			//수신 항목 값
			String msgId    	        = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번

			
			String sYD_GP 				= commPiUtils.trim(rcvMsg.getFieldString("YD_GP"));
			//조합상차(시작:S , 종료: E ,  단일상차: N )
			String sCMBN_CARLD_YN 		= commPiUtils.nvl (rcvMsg.getFieldString("CMBN_CARLD_YN"),"N");
			String sWORK_GP 			= commPiUtils.trim(rcvMsg.getFieldString("WORK_GP"));
			String sTEL_NO 			    = commPiUtils.trim(rcvMsg.getFieldString("TEL_NO"));
			String sTRANS_ORD_DT  		= commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			String sTRANS_ORD_SEQNO 	= commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String sCAR_NO 			    = commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"));
			String sCARD_NO 			= commPiUtils.trim(rcvMsg.getFieldString("CARD_NO"));
			String sCAR_KIND 			= commPiUtils.trim(rcvMsg.getFieldString("CAR_KIND"));
			String sWAIT_ARR_DDTT		= commPiUtils.trim(rcvMsg.getFieldString("WAIT_ARR_DDTT"));
			String sWAIT_ARR_GP		    = commPiUtils.trim(rcvMsg.getFieldString("WAIT_ARR_GP"));
			String sTRANS_FRTOMOVE_GP	= commPiUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP")); //1 운송 2 이송
			String sDRIVER_NAME		    = commPiUtils.trim(rcvMsg.getFieldString("DRIVER_NAME"));	//운전기사명
			String sModifier            = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));             //수정자(Backup Only)			 
	        if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
	        
	        
			//차량정보 존재여부 체크 //////////////////////////////////////////////////////////////////////////////////
			JDTORecordSet rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DT"		, sTRANS_ORD_DT);
			recPara.setField("TRANS_ORD_SEQNO"	, sTRANS_ORD_SEQNO);
//			recPara.setField("CARD_NO"			, sCARD_NO );
			recPara.setField("CAR_NO"			, sCAR_NO );
			recPara.setField("CMBN_CARLD_YN"	, sCMBN_CARLD_YN );

			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			String sYD_CAR_SCH_ID = "";
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	sYD_GP);							
			//중복 check
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdCmbnCarldYn_PIDEV*/
			//int intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 419);
			
			int intRtnVal = ydPICommDAO.selectS(recPara, rsResult1, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdCmbnCarldYn_PIDEV");
			if(intRtnVal > 0){
				sMsg= "["+methodNm+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				commPiUtils.printLog(logId, sMsg , "SL");
				
				rsResult1.first();
				recInTemp = rsResult1.getRecord();
				sYD_CAR_SCH_ID    = StringHelper.evl(recInTemp.getFieldString("YD_CAR_SCH_ID"), "");
			 
				recInTemp.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);
				recInTemp.setField("DEL_YN"       , "Y");
				recInTemp.setField("MODIFIER"     , sModifier);
				
				intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
				if( intRtnVal <= 0 ){
					sMsg="[" + methodNm + "] 차량스케줄 삭제 시 오류발생[반환값 : " + intRtnVal + "]";
					commPiUtils.printLog(logId, sMsg , "SL");
					m_ctx.setRollbackOnly();
					throw new DAOException(sMsg);
	    		}
			}
			///////////////////////////////////////////////////////////////////////////////////////////////////
			
			
			//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////////////////				
			sYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
			
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("YD_GP"				, sYD_GP);
			recPara.setField("TRANS_ORD_DATE"		, sTRANS_ORD_DT);
			recPara.setField("TRANS_ORD_SEQNO"		, sTRANS_ORD_SEQNO);

			///도착가능 포인트 조회
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPointSelect*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 420);
			if(intRtnVal <= 0){
				sMsg= "["+methodNm+"] TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다. 확인요망]";
				commPiUtils.printLog(logId, sMsg , "SL");
				m_ctx.setRollbackOnly();
				throw new DAOException(sMsg);
				
			}
			rsResult1.first();
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp = rsResult1.getRecord();
			String sYD_STK_COL_GP    = StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP"), "");
			String sYD_CARPNT_CD     = StringHelper.evl(recInTemp.getFieldString("YD_CARPNT_CD"), "");
			String sYD_PNT_CD        = StringHelper.evl(recInTemp.getFieldString("YD_PNT_CD"), "");
			String sSPOS_WLOC_CD     = StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
			sCAR_KIND		         = StringHelper.evl(sCAR_KIND, "TR");
			
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID"    , sYD_CAR_SCH_ID);
			recInTemp.setField("REGISTER"         , sModifier);
			recInTemp.setField("YD_EQP_WRK_STAT"  , "U");									//야드설비작업상태
			recInTemp.setField("YD_EQP_ID"        , YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
			recInTemp.setField("YD_CAR_USE_GP"    , YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분 
			recInTemp.setField("CAR_NO"           , sCAR_NO);								//차량번호
			recInTemp.setField("CAR_KIND"         , sCAR_KIND);							//차량종류
			recInTemp.setField("SPOS_WLOC_CD"     , sSPOS_WLOC_CD);							//발지개소코드
//			recInTemp.setField("CARD_NO"          , sCARD_NO);								//카드번호
			recInTemp.setField("YD_CARLD_LEV_DT"  , commPiUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
			recInTemp.setField("YD_PNT_CD1"       , sYD_PNT_CD);							//야드포인트코드1
			recInTemp.setField("YD_CARLD_STOP_LOC", sYD_STK_COL_GP);						//야드상차정지위치 
			recInTemp.setField("TRANS_ORD_DATE"   , sTRANS_ORD_DT);							//운송지시일자
			recInTemp.setField("TRANS_ORD_SEQNO"  , sTRANS_ORD_SEQNO);						//운송지시순번 
			
			if("E".equals(sCMBN_CARLD_YN)){
				recInTemp.setField("YD_BAYIN_WO_SEQ",  "1");								//입동지시순번 - 복수상차 마지막 1순위	
			}else{
				recInTemp.setField("YD_BAYIN_WO_SEQ",  YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 - 기본값으로 설정(9)
			}
			recInTemp.setField("YD_CAR_PROG_STAT" , "1");									//상차출발상태
			recInTemp.setField("YD_CAR_WRK_GP"    , sWORK_GP);
			recInTemp.setField("TEL_NO"           , sTEL_NO);								//기사핸드폰번호
			recInTemp.setField("CMBN_CARLD_YN"    , sCMBN_CARLD_YN);						//첫번째 도착창고 : S 두번째 도착창고 : E
			recInTemp.setField("WAIT_ARR_DDTT"    , sWAIT_ARR_DDTT);						//대기장도착시간
			recInTemp.setField("WAIT_ARR_GP"      , sWAIT_ARR_GP);							//대기장도착구분  - B:BACKUP , S:SMARTPHONE
			recInTemp.setField("DRIVER_NAME"      , sDRIVER_NAME);							//운전기사명
			
    		//차량스케줄 등록
	    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
    		if( intRtnVal <= 0 ){
				sMsg="[" + methodNm + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
				commPiUtils.printLog(logId, sMsg , "SL");
				m_ctx.setRollbackOnly();
				throw new DAOException(sMsg);
    		}			
    		/////////////////////////////////////////////////////////////////////////////////////////////////			
    		
    		
			//입동지시 호출/////////////////////////////////////////////////////////////////////////////////////
    		if(!sYD_CARPNT_CD.equals("")){
    			sMsg="[" + methodNm + "] 차량입동포인트[" + sYD_CARPNT_CD + "], 차량스케줄ID[" + sYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 시작";
    			commPiUtils.printLog(logId, sMsg , "SL"); 
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("JMS_TC_CD"        , "YDYDJ662");         // procCarBayInOrdReqNEW   
				recInTemp.setField("YD_CARPNT_CD"     , sYD_CARPNT_CD);		 //입동포인트
				recInTemp.setField("YD_CAR_SCH_ID"    , sYD_CAR_SCH_ID);	 //차량스케줄ID
				recInTemp.setField("CARD_NO"          , sCARD_NO);
				recInTemp.setField("CAR_NO"           , sCAR_NO);
				recInTemp.setField("CAR_KIND"         , sCAR_KIND); 	 	 //차량종류
				recInTemp.setField("TRANS_FRTOMOVE_GP", sTRANS_FRTOMOVE_GP); //1 운송 2 이송
				
//				ydUtils.displayRecord(methodNm, recInTemp);	 
				commPiUtils.printParam(methodNm, recInTemp);
				ydDelegate.sendMsg(recInTemp);
	  
//				EJBConnector 	ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);			
//			    ejbConn.trx("procCarBayInOrdReqNEW", new Class[] { JDTORecord.class }, new Object[] { recInTemp });					
				
				sMsg="[" + methodNm + "] 차량입동포인트[" + sYD_CARPNT_CD + "], 차량스케줄ID[" + sYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공";
				commPiUtils.printLog(logId, sMsg , "SL");
    		}
			/////////////////////////////////////////////////////////////////////////////////////////////////			
					
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, methodNm, e));
		}		
	}	
	
	
	/**	 
	 * [A] 오퍼레이션명 :SLAB출하완료(DMYDR029)procOutplSlabDistCmpl
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1073(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "[YdSlabL3RcvPISeEJB.rcvM10LMYDJ1073] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		
		try {
			
			commPiUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", methodNm, "APPPI0", "S", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}			
			
			YdStockDao ydStockDao  = new YdStockDao();
			
			String sMsg = "";
			//=============================================================
			sMsg = "[출하] 외판슬라브출하완료 수신";
			commPiUtils.printLog(logId, sMsg , "SL");
			String msgId = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			rcvMsg.setField("TRANS_ORD_DATE" , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번

			//수신한 재료번호
			//sSTL_NO  = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");
			String sYD_GP    = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String sSTL_NO   = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"));
			String sModifier = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));             //수정자(Backup Only)			 
	        if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }

	
			JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();
			recStockColumn.setField("STL_APPEAR_GP" , commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")));
			recStockColumn.setField("STL_NO"        , sSTL_NO);				
			recStockColumn.setField("STL_PROG_CD"   , "M");
			recStockColumn.setField("YD_AIM_RT_GP"  , "M1");
			recStockColumn.setField("MODIFIER"      , sModifier);
			//****************************************************************************************************

						 
			//저장품갱신******************************************************************************************** 
			int intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				sMsg= "YD_STOCK[외판슬라브출하완료] UPDATE Error :: [" + intRtnVal + "]";
				commPiUtils.printLog(logId, sMsg , "SL");
				return ;
			}

			commPiUtils.printLog(logId, "[2] YD_STOCK[외판슬라브출하완료] UPDATE Success" , "SL");


			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		    /* 외판슬라브출하차량출발실적(자동출발)
		    TRANS_ORD_DT	운송지시일자	DATE				
		    TRANS_ORD_SEQNO	운송지시순번	CHAR	4			
		    CAR_NO	차량번호	CHAR	15			
		    CARD_NO	카드번호	CHAR	4		출하차량 ID카드번호	
		    SPOS_WLOC_CD                  	발지개소코드	CHAR	6		차량도착 개소코드	
		    SPOS_YD_PNT_CD                	발지야드포인트코드	CHAR	4		차량도착  포인트코드
		    */		
			
			JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInTemp2  = JDTORecordFactory.getInstance().create();
			JDTORecord recResult   = JDTORecordFactory.getInstance().create();
			
//PIDEV_S :병행가동용:PI_YD
			rcvMsg.setField("PI_YD",    	"S");		
			intRtnVal = ydStockDao.getYdStock(rcvMsg, rsResult, 116);
			if (intRtnVal > 0){	//차량에 대한 모든 재료가 출하완료된 경우 에만 자동출발 처리를 함
				
				rsResult.first();
				recResult = rsResult.getRecord();
				
				recInTemp2.setField("TC_CODE"        , "DMYDR039");									//전문코드
//				recInTemp2.setField("CARD_NO"        , ydDaoUtils.paraRecChkNull(recResult,"CARD_NO"));
				recInTemp2.setField("CAR_NO"         , commPiUtils.trim(recResult.getFieldString("CAR_NO")));				
				recInTemp2.setField("SPOS_WLOC_CD"   , commPiUtils.trim(recResult.getFieldString("WLOC_CD")));		
				recInTemp2.setField("SPOS_YD_PNT_CD" , commPiUtils.trim(recResult.getFieldString("YD_PNT_CD")));	
				recInTemp2.setField("TRANS_ORD_DT"   , commPiUtils.trim(recResult.getFieldString("TRANS_ORD_DATE")));
				recInTemp2.setField("TRANS_ORD_SEQNO", commPiUtils.trim(recResult.getFieldString("TRANS_ORD_SEQNO")));
				
				recInTemp2.setField("YD_GP"          , sYD_GP);
				
				EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
				Boolean isSucf = (Boolean) ejbConn.trx("rcvOutplSlabDistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp2 });
			}
			 //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

			
			
			if("A".equals(sYD_GP)){ //야드구분이 연주슬라브야드인 경우에만 
						
				//======================================================
				// 저장품제원 : 연주슬라브L2 로 송신(YDY1L002)
				//======================================================
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YDY1L002");
				recResult.setField("YD_INFO_SYNC_CD", "A");    // A:생산실적
				recResult.setField("STL_NO"         , commPiUtils.trim(recStockColumn.getFieldString("STL_NO")));
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				recResult.setField("DEL_YN_CHECK"   , "N");			
				ydDelegate.sendMsg(recResult);
			
			}
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, methodNm, e));
		}		
	}		

	/**
	 * 오퍼레이션명 : 일관제철  출하전문 취소처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receiveCancel(JDTORecord rcvMsg) {
		String mthdNm = "출하전문 취소처리[YdSlabL3RcvPISeEJB.receiveCancel] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		int intRtnVal = 0;
		String sMethodName              ="receiveCancel";

	    try {	        
	    	
	    	commPiUtils.printLog(logId, mthdNm, "S+");

	    	YdStockDao ydStockDao = new YdStockDao();
	    	
			commPiUtils.printLog(logId,  "############# 일관제철  출하취소 전문 START ###############" , "SL");
			String msgId                   = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
	    	String sYD_GP                  = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"));   // 야드구분
	    	String sYD_GP1                  = commPiUtils.trim(rcvMsg.getFieldString("YD_GP1"));   // 야드구분
	    	//// 1':목전정보,'2': 보관지시,'3':반품',4':출하지시대기,'5':운송지시대기
	    	String sInfoGp                 = commPiUtils.trim(rcvMsg.getFieldString("INFO_GP"                ));   // 정보구분
			String sSTL_APPEAR_GP          = commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"          ));   // 재료외형구분
            String sSTL_NO                 = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"                 ));   // 재료번호
            String sCURR_PROG_CD           = commPiUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"           ));   // 현재진도코드
            String sORD_YEOJAE_GP          = commPiUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP"          ));   // 주문여재구분
            String sORD_NO                 = commPiUtils.trim(rcvMsg.getFieldString("ORD_NO"                 ));   // 주문번호
            String sORD_DTL                = commPiUtils.trim(rcvMsg.getFieldString("ORD_DTL"                ));   // 주문행번
            String sORD_GP                 = commPiUtils.trim(rcvMsg.getFieldString("ORD_GP"                 ));   // 수주구분
            String sCUST_CD                = commPiUtils.trim(rcvMsg.getFieldString("CUST_CD"                ));   // 고객코드
            String sDEST_CD                = commPiUtils.trim(rcvMsg.getFieldString("DEST_CD"                ));   // 목적지코드
            String sDLVRDD_RULE_DD         = commPiUtils.trim(rcvMsg.getFieldString("YD_DLVRDD_RULE_DD"      ));   // 납기기준일
            String sDEST_TEL_NO            = commPiUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO"            ));   // 목적지전화번호
            String sDIST_SHIPASSIGN_GP     = commPiUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP"     ));   // 출하배선지시구분
            String sModifier               = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"               ));   //수정자(Backup Only)
            if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
	    	

//	    	1':목전정보,'2': 보관지시,'3':반품',4':출하지시대기,'5':운송지시대기
//	    	DMYDR004 M10LMYDJ1013 4  
//	    	DMYDR013 M10LMYDJ1013 1  cancel 
//	    	DMYDR016              5  cancel
//	    	DMYDR032              3
//	    	DMYDR060 M10LMYDJ1033    cancel
//	    	DMYDR061 M10LMYDJ1043
//	    	DMYDR029 M10LMYDJ1073   	
	    	
	    	//=======================================================
	    	// 야드구분
	    	//=======================================================
	    	String sSTL_GP = "";
	    	if("A".equals(sYD_GP) || "S".equals(sYD_GP)){ //S:SLAB
	    		sSTL_GP = "S";	
	    	} else if ("A".equals(sYD_GP1) || "S".equals(sYD_GP1)){
	    		sSTL_GP = "S";
	    	} else {
	    	
	    		commPiUtils.printLog(logId,  "전문에 야드구분(YD_GP)항목의 값이 없음", "SL");
	    		return false;
	    	}

      		JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();
    	
//	    	if(YdConstant.DMYDR003.equals(sJmsTcCd) || YdConstant.DMYDR008.equals(sJmsTcCd) || YdConstant.DMYDR009.equals(sJmsTcCd) ||	YdConstant.DMYDR013.equals(sJmsTcCd) ||	
//	     	   YdConstant.DMYDR014.equals(sJmsTcCd) || YdConstant.DMYDR015.equals(sJmsTcCd) || YdConstant.DMYDR016.equals(sJmsTcCd) ||	YdConstant.DMYDR018.equals(sJmsTcCd) ||	
//	     	   YdConstant.DMYDR026.equals(sJmsTcCd) || YdConstant.DMYDR027.equals(sJmsTcCd) || YdConstant.DMYDR028.equals(sJmsTcCd)){

	    	if (("M10LMYDJ1013".equals(msgId) && "1".equals(sInfoGp)) || //DMYDR013
	    		("M10LMYDJ1013".equals(msgId) && "5".equals(sInfoGp))){  //DMYDR016	    		
	    		//==================================================================
		    	// 전문에 따른 분기 (재료번호 1개 : STL_NO)
				//==================================================================

	    		
	    		// 레코드에 추가
		    	recStockColumn.setField("STL_APPEAR_GP", sSTL_APPEAR_GP);
		    	recStockColumn.setField("STL_NO"       , sSTL_NO);
				recStockColumn.setField("STL_PROG_CD"  , sCURR_PROG_CD);
				
	    		// TC에 따른 변동항목 추출
//	    		if(YdConstant.DMYDR003.equals(sJmsTcCd) || YdConstant.DMYDR008.equals(sJmsTcCd) || YdConstant.DMYDR009.equals(sJmsTcCd)){
//	    			sWO_CAR_PLNT_PROC_CD     = ydDaoUtils.paraRecChkNull(inRecord, "WO_CAR_PLNT_PROC_CD");
//		    		sFRTOMOVE_ORD_DATE       = ydDaoUtils.paraRecChkNull(inRecord, "FRTOMOVE_ORD_DATE");
//		    		sURGENT_FRTOMOVE_WORD_GP = ydDaoUtils.paraRecChkNull(inRecord, "URGENT_FRTOMOVE_WORD_GP");    			
//	    		
//		    		// 레코드에 추가
//			    	recStockColumn.setField("WO_CAR_PLNT_PROC_CD"    , sWO_CAR_PLNT_PROC_CD);
//			    	recStockColumn.setField("FRTOMOVE_ORD_DATE"      , sFRTOMOVE_ORD_DATE);
//			    	recStockColumn.setField("URGENT_FRTOMOVE_WORD_GP", sURGENT_FRTOMOVE_WORD_GP);	 
//			    	
//	    		} else if(YdConstant.DMYDR013.equals(sJmsTcCd) || YdConstant.DMYDR014.equals(sJmsTcCd) || YdConstant.DMYDR015.equals(sJmsTcCd) || 
//	    				  YdConstant.DMYDR016.equals(sJmsTcCd) || YdConstant.DMYDR018.equals(sJmsTcCd) || YdConstant.DMYDR026.equals(sJmsTcCd) || 
//	    				  YdConstant.DMYDR027.equals(sJmsTcCd) || YdConstant.DMYDR028.equals(sJmsTcCd)){	

	    		// 레코드에 추가
		    	recStockColumn.setField("ORD_YEOJAE_GP"     , sORD_YEOJAE_GP);
		    	recStockColumn.setField("ORD_NO"            , sORD_NO);
		    	recStockColumn.setField("ORD_DTL"           , sORD_DTL);
		    	recStockColumn.setField("ORD_GP"            , sORD_GP);
		    	recStockColumn.setField("CUST_CD"           , sCUST_CD);
		    	recStockColumn.setField("DEST_CD"           , sDEST_CD);
		    	recStockColumn.setField("YD_DLVRDD_RULE_DD" , sDLVRDD_RULE_DD);
		    	recStockColumn.setField("DEST_TEL_NO"       , sDEST_TEL_NO);
		    	recStockColumn.setField("DIST_SHIPASSIGN_GP", sDIST_SHIPASSIGN_GP);
	    		
    	    	//if(YdConstant.DMYDR016.equals(msgId)){
		    	if ("M10LMYDJ1013".equals(msgId) && "5".equals(sInfoGp)){
    	    		recStockColumn.setField("CAR_NO" , "");
    	    		recStockColumn.setField("CARD_NO", "");
    	    	}
	    		
	    		
				// 야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
//				rVal = YdCommonUtils.getYdAimRtGp2(sJmsTcCd , sSTL_GP, sSTL_NO, sCURR_PROG_CD);		
//				sYD_AIM_RT_GP = rVal[0];
//				recStockColumn.setField("YD_AIM_RT_GP", sYD_AIM_RT_GP);

	    		String[] rVal = new String[2];
//				rVal = ydPiDAO.getYdAimRtGp2PI(msgId , sSTL_GP, sSTL_NO, sCURR_PROG_CD);	
				rVal = YdCommonUtils.getYdAimRtGp2(msgId , sSTL_GP, sSTL_NO, sCURR_PROG_CD);	
				String ydAimRtGp = rVal[0];
				recStockColumn.setField("YD_AIM_RT_GP", ydAimRtGp);
				
		    	
	    		commPiUtils.printLog(logId,  "출하취소 처리(" + msgId + " : 받은전문 그대로 저장품 업데이트 ", "SL");
	    		
	    		//  저장품 업데이트 처리 (업데이트 쳐야 될 항목은 업데이트 클리어될 항목은 클리어해야 하기 위해 기본쿼리로 처리)
 	    		int nRet = ydStockDao.updYdStock(recStockColumn, 0);	
 				if(nRet > 0){
 					commPiUtils.printLog(logId,  "출하취소 처리(" + msgId + " : 받은전문 그대로 저장품 업데이트 ", "SL");

 				}else if(intRtnVal == 0){
 					commPiUtils.printLog(logId,  "출하 전문 취소작업 실패 TC_CODE(" + msgId + ")", "SL");
 					return false;
 				}	    		
	    	
//	    	} else if(YdConstant.DMYDR011.equals(msgId) || 
//	    			  YdConstant.DMYDR012.equals(msgId) || 
//	    			  YdConstant.DMYDR020.equals(msgId) || 
//	    			  YdConstant.DMYDR060.equals(msgId) ||
//	    			  YdConstant.DMYDR021.equals(msgId) || 
//	    			  YdConstant.DMYDR022.equals(msgId) || 
//	    			  YdConstant.DMYDR070.equals(msgId) || 
//	    			  YdConstant.DMYDR073.equals(msgId) ||
//	    			  YdConstant.DMYDR025.equals(msgId)){

 		    } else if("M10LMYDJ1033".equals(msgId)){  //DMYDR060
 				
	    		//==================================================================
		    	// 전문에 따른 분기 (재료번호 N개 : STL_NO1 ... STL_NO20)
				//==================================================================
	    		
	    		if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(sYD_GP)){
	    			// 일단 재료정보 초기화는 하지 않음.
	    			// 2016.04.06 윤재광
	    		}else{
	    			// 전문에서 공통적인 항목의 값을 추출
//		    		sYD_GP           = ydDaoUtils.paraRecChkNull(rcvMsg, "YD_GP");
//		    		sSTL_APPEAR_GP   = ydDaoUtils.paraRecChkNull(rcvMsg, "STL_APPEAR_GP");
//		    		sTRANS_ORD_DATE  = ydDaoUtils.paraRecChkNull(rcvMsg, "TRANS_ORD_DT");
//		    		sTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(rcvMsg, "TRANS_ORD_SEQNO");
//		    		sCANCEL_YN       = ydDaoUtils.paraRecChkNull(rcvMsg, "CANCEL_YN");
//	
//		    		intYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNullInt(rcvMsg,"YD_EQP_WRK_SH");
		    		
		    		int intYD_EQP_WRK_SH = Integer.parseInt(commPiUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수
		    		
		    		// 전문에서 반복적인 재료번호 추출 
		    		for(int i=0; i<intYD_EQP_WRK_SH; i++){
			    		// 레코드 생성
				    	recStockColumn = JDTORecordFactory.getInstance().create();
	
						// 전문에서 재료번호를 추출
		    			sSTL_NO = commPiUtils.trim(rcvMsg.getFieldString("STL_NO" + (i+1) ));
			    		
		    			if("".equals(sSTL_NO)) break;
		    			
		    			// 레코드에 추가
		    			recStockColumn.setField("STL_NO"			, sSTL_NO);
	    				recStockColumn.setField("TRANS_ORD_DATE" 	, "");
				    	recStockColumn.setField("TRANS_ORD_SEQNO"	, "");	    	    	
		    	    	recStockColumn.setField("CAR_NO"			, "");
		    	    	recStockColumn.setField("CARD_NO"			, "");
		    	    	recStockColumn.setField("YD_AIM_RT_GP"		, "NB");
		    	    	
		    	    	//  저장품 업데이트 처리 
		 	    		int nRet = ydStockDao.updYdStock(recStockColumn);	
		 				if(nRet > 0){
		 					commPiUtils.printLog(logId,  "출하 전문 취소작업 성공 TC_CODE(" + msgId + ")", "SL");
		 				}else if(intRtnVal == 0){
		 					commPiUtils.printLog(logId,  "출하 전문 취소작업 실패 TC_CODE(" + msgId + ") STL_NO(" + sSTL_NO + ")", "SL");
		 					// 업데이트 시 에러가 발생해도 N건처리를 위해 continue
		 					continue;
		 				}	
		    		}	    	
	    		}
				//=========================================================
				//	차량스케줄 삭제 및 차량 POINT Clear
				//=========================================================
				commPiUtils.printLog(logId,  "차량스케줄삭제 및 차량Point Clear 시작", "SL");
//PIDEV_S :병행가동용:PI_YD
				rcvMsg.setField("PI_YD",    	"S");		
				String sRtnMsg = YdCommonUtils.delCarSchNCarPointForDist(rcvMsg, msgId);
				commPiUtils.printLog(logId,  "차량스케줄삭제 및 차량Point Clear 완료 - ", "SL");
	    	}	
	    	commPiUtils.printLog(logId,  "############# 일관제철 출하취소 전문 END ###############", "SL");
	    	
	    }catch(DAOException daoe) {
               throw daoe;
        }catch(Exception e) {
	        throw new EJBServiceException(e);
        }
		return true;
	}
}
