/**
 * @(#)StockSpecEndSeEJBBean.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2011/07/13
 * 
 * @description		이클래스는 저장품제원종료 Session EJB 클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2011/05/24                    최초 등록
 * V1.01  2013/01/17   조병기       조병기      procPlGdsDistCmpl 메소드 수정 
 *                                     :저장품제원정보(YDY8L002) 후판제품 L2 로 송신 추가
 * V1.02  2013/03/26   조병기       조병기      개발표준점검에 의한 보완요청사항 수정 (개발표준검증결과서 참조)                                    
 */

package com.inisteel.cim.yd.ydStock.StockSpecEnd;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;

import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdCodeMapping;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.ydStock.StockSpecReg.StockSpecRegSeEJBBean;

//import com.inisteel.cim.yd.common.util.*;

/**
 * 저장품제원종료 Session EJB
 *
 * @ejb.bean name="StockSpecEndSeEJB" jndi-name="StockSpecEndSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class StockSpecEndSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	
	private YdDelegate ydDelegate = new YdDelegate();
	private YdUtils ydUtils =new YdUtils();
	private YdDaoUtils ydDaoUtils =new YdDaoUtils();
	private YdTcConst ydTcConst =new YdTcConst();
	
	// [DEBUG] message flag

	
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	
	/**
	 * A후판 가열로추출실적 (PRYDJ002)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procAPlRefurExtWr(JDTORecord inRecord)throws JDTOException  {
		
		// 변수 선언
		String szMethodName                     = "procAPlRefurExtWr";
		String szMsg                            = "";
		String szOperationName                  = "A후판 가열로추출실적";
		String szREHEAT_SLAB_GP                 = "";
		
		try{
			
			szREHEAT_SLAB_GP = ydDaoUtils.paraRecChkNull(inRecord,"REHEAT_SLAB_GP");
			
			/*
			 * 재열재 실적 발생시점에 처리.
			 */
			if(szREHEAT_SLAB_GP.equals("1")){
				
				//======================================================
				// 저장품정보 항목 셋팅.
				//======================================================
				this.procStockLocCd(inRecord);
				
				//======================================================
				// 2009.08.31 권오창
				// 저장품제원 : 후판슬라브L2 로 송신(YDY3L002)
				//======================================================
				JDTORecord recResult = null;
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YDY3L002");
				recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
				recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				ydDelegate.sendMsg(recResult);
			}
			
		}catch(Exception e){
			szMsg = "[A후판가열로추출실적수신]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		szMsg="A후판가열로추출실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procAPlRefurExtWr
	
	
	
	/**
	 * C열연 가열로추출실적 (HRYDJ002)
	 * C열연 재열재오작실적 (HRYDJ010) 같이 사용
	 * 
	 * 향후 필요에 따라서 기능 추가할것.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCHrRefurExtWr(JDTORecord inRecord)throws JDTOException  {
		
		// 변수선언
		String szMethodName   		  = "procCHrRefurExtWr";
		String szMsg          		  = "";
		String szOperationName        = "C열연 가열로추출실적";
		String szREHEAT_SLAB_GP 	  = "";
	
		try{

			szREHEAT_SLAB_GP = ydDaoUtils.paraRecChkNull(inRecord, "AB_OCCUR_GP_CD");
			
			/*
			 * 재열재 실적 발생시점에 처리.
			 */
			if(szREHEAT_SLAB_GP.equals("1")||szREHEAT_SLAB_GP.equals("2")){
				
				//======================================================
				// 저장품정보 항목 셋팅.
				//======================================================
				//this.procStockLocCd(inRecord);
				
				//======================================================
				// TAKE OUT 요구 : AAPS01
				//======================================================
				/*
					MSG_ID				전문 ID	CHAR	8
					DATE				생성일	CHAR	10
					TIME				생성시간	CHAR	8
					MSG_GP				전문구분	CHAR	1
					MSG_LEN				전문길이	NUMBER	4
					TEMP				임시	CHAR	29
					YD_EQP_ID			야드설비ID 	CHAR	6
					CARRY_OUT_REQ_GP	Carry-Out요구구분	CHAR	1
					YD_STK_BED_NO		야드적치Bed번호	CHAR	2
					STL_NO				재료번호	CHAR	11
					YD_STK_BED_STL_SH	야드적치Bed재료매수	NUMBER	3
					YD_CARRY_OUT_SH		Carry-Out매수	NUMBER	3
					STL_NO1				재료번호1	CHAR	11
					STL_NO2				재료번호2	CHAR	11
					STL_NO3				재료번호3	CHAR	11
					STL_NO4				재료번호4	CHAR	11
					STL_NO5				재료번호5	CHAR	11
				 */
				JDTORecord recResult = null;
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         	,"C3YDL004");
				recResult.setField("YD_EQP_ID"			,"AAPS01");
				recResult.setField("CARRY_OUT_REQ_GP"	,"Y");
				recResult.setField("YD_STK_BED_NO"		,"01");
				recResult.setField("STL_NO"				,ydDaoUtils.paraRecChkNull(inRecord,"SLAB_NO"));
				recResult.setField("YD_STK_BED_STL_SH"	,"1");
				recResult.setField("YD_CARRY_OUT_SH"	,"1");
				recResult.setField("STL_NO1"			,ydDaoUtils.paraRecChkNull(inRecord,"SLAB_NO"));
				
				EJBConnector ydEjbCon = new EJBConnector("default", this);
				ydEjbCon.trx("RcptWrkDmdSeEJB", "procC3TakeOutCmpl", recResult);
				
				//======================================================
				// 재열재재료정보 : 연주정정L2로 송신(YDC3L008)
				//======================================================
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID",        "YDC3L009");
				recResult.setField("STL_NO",        ydDaoUtils.paraRecChkNull(inRecord,"SLAB_NO"));
				ydDelegate.sendMsg(recResult);
				
				//======================================================
				// 저장품제원 : 연주야드야드L2로 송신(YDY1L002)
				//======================================================
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YDY1L002");
				recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
				recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(inRecord,"SLAB_NO"));
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				ydDelegate.sendMsg(recResult);
			}
			
		}catch(Exception e){
			szMsg = "Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
	} // end of procCHrRefurExtWr

	private void procStockLocCd(JDTORecord inRecord){
		
		// 변수선언
		String szMethodName   		  = "procStockLocCd";
		String szMsg          		  = "";
		
		// DAO 및 UTIL 객체 생성
		YdDaoUtils ydDaoUtils         = new YdDaoUtils();
		YdStockDao ydStockDao     	  = new YdStockDao();
		YdCodeMapping ydCodeMapping   = new YdCodeMapping();
		StockSpecRegSeEJBBean stock   = new StockSpecRegSeEJBBean();
		// 레코드 선언
		JDTORecordSet rsGetSlabComm   = null;
		JDTORecordSet rsOut          = null;
		JDTORecord recPara            = null;
		JDTORecord recGetVal          = null;
		JDTORecord recEditRec         = null;
		JDTORecord outRecTemp         = null;
		// 변수선언
		String szSTL_NO               = "";
		int intRtnVal				  = 0;
		int nRet                      = 0;
		String szSLAB_WO_RT_CD       = "";
		String szYD_STK_LOT_CD       = "";
		
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		
		try{
			szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord,"SLAB_NO");
			
			//=========================================================================================================
			// 슬라브공통 조회 (GP : 2)
			//=========================================================================================================
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("SLAB_NO", szSTL_NO);
			rsGetSlabComm = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydStockDao.getYdStock(recPara, rsGetSlabComm, 2);
			if(intRtnVal < 0){
				szMsg = "[가열로추출실적 (HRYDJ002)] 슬라브공통 테이블 조회 파라미터 에러 nRet(" + intRtnVal + ") SLAB_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(intRtnVal == 0){
				szMsg = "[가열로추출실적 (HRYDJ002)] 슬라브공통 테이블 조회 데이터가 없음 nRet(" + intRtnVal + ") SLAB_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			} else {
				szMsg = "[가열로추출실적 (HRYDJ002)] 슬라브공통 테이블 조회 SUCCESS";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			// 슬라브 레코드 가져옴
			rsGetSlabComm.first();
			recGetVal = rsGetSlabComm.getRecord();
			
			szSLAB_WO_RT_CD    = ydDaoUtils.paraRecChkNull(recGetVal, "SLAB_WO_RT_CD"); 
			szYD_STK_LOT_CD    = ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_LOT_CD"); 
			
			//=============================================================================================
			// 2009.09.15
			// 권오창
			// 코드 매핑값 호출 
			//			
			//     * 주편/슬라브 조회 시(1건) 코드매핑의 재료정보는 편집된 재료번호(STL_NO)로 조회
			//       저장품 업데이트할 레코드 : recEditRec
			//
			//=============================================================================================

			// 재료번호를 미리 가져오고 밑에서 다시 인스턴스 생성
			szSTL_NO = ydDaoUtils.paraRecChkNull(recGetVal, "SLAB_NO");

			// 위에서 INSERT/UPDATE 쳤기때문에 인스턴스를 새로 만들어서 코드매핑후 가져올 값만 다시 업데이트  
			outRecTemp = JDTORecordFactory.getInstance().create();
			recEditRec = JDTORecordFactory.getInstance().create();
			rsOut      = JDTORecordFactory.getInstance().createRecordSet("");
			
			// 새로 할당한 레코드에 업데이트를 위해 재료번호를 설정
			recEditRec.setField("STL_NO", szSTL_NO);

			// 코드매핑 처리
			nRet = ydCodeMapping.MakeCodeMapping(szRcvTcCode, szSTL_NO, inRecord, outRecTemp);
			if(nRet <= 0){
				String szTempSTL_APPEAR_GP =  ydDaoUtils.paraRecChkNull(outRecTemp, "STL_APPEAR_GP");
				if(!szTempSTL_APPEAR_GP.trim().equals("")){
					recEditRec.setField("STL_APPEAR_GP", szTempSTL_APPEAR_GP);
				}
				
				String szTempSCARFING_YN =  ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_YN");
				if(!szTempSCARFING_YN.trim().equals("")){
					recEditRec.setField("SCARFING_YN", szTempSCARFING_YN);
				}

				String szSCARFING_DONE_YN =  ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_DONE_YN");
				if(!szSCARFING_DONE_YN.trim().equals("")){
					recEditRec.setField("SCARFING_DONE_YN", szSCARFING_DONE_YN);
				}
				
				szMsg = "[nRet " + nRet + "] 매핑되는 코드가 없습니다. 재료외형, 스카핑여부, 스카핑 완료여부는 업데이트를 위함 STL_APPEAR_GP(" + szTempSTL_APPEAR_GP + ") SCARFING_YN(" + szTempSCARFING_YN + ") SCARFING_DONE_YN(" + szSCARFING_DONE_YN + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else {
				String szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "STL_APPEAR_GP");
				if(!szSTL_APPEAR_GP.equals("")){
					recEditRec.setField("STL_APPEAR_GP"   , szSTL_APPEAR_GP);
				}
				
				String szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_RT_GP");
				if(!szYD_AIM_RT_GP.equals("")){
					recEditRec.setField("YD_AIM_RT_GP"    , szYD_AIM_RT_GP);
				}

				String szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_YD_GP");
				if(!szYD_AIM_YD_GP.equals("")){
					recEditRec.setField("YD_AIM_YD_GP"    , szYD_AIM_YD_GP);
				}

				String szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_BAY_GP");
				if(!szYD_AIM_BAY_GP.equals("")){
					recEditRec.setField("YD_AIM_BAY_GP"   , szYD_AIM_BAY_GP);
				}
				
				String szSCARFING_YN = ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_YN");
				if(!szSCARFING_YN.equals("")){
					recEditRec.setField("SCARFING_YN"   , szSCARFING_YN);
				}
				
				String szSCARFING_DONE_YN = ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_DONE_YN");
				if(!szSCARFING_DONE_YN.equals("")){
					recEditRec.setField("SCARFING_DONE_YN"   , szSCARFING_DONE_YN);
				}		
			}
			//=============================================================================================

			//==============================================================
			// 2010.03.24
			// 권오창
			//
			// 산적 LOT타입, 산적 LOT코드
			//==============================================================
			recEditRec.setField("SLAB_WO_RT_CD", szSLAB_WO_RT_CD);
			recEditRec.setField("YD_STK_LOT_CD", szYD_STK_LOT_CD);
			stock.setYdStkLocTpCd(recEditRec);
			
			//=============================================================================================
			// 코드 매핑 후 저장품에 업데이트
			//=============================================================================================
			intRtnVal = ydStockDao.getYdStock(recEditRec, rsOut, 0);
			if(intRtnVal < 0){
				szMsg = "코드매핑 후 YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}else if(intRtnVal > 0){
				intRtnVal = ydStockDao.updYdStock(recEditRec, 0);
				if(intRtnVal < 0){
					szMsg = "코드매핑 후 YD_STOCK[저장품] UPDATE Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}else {
					szMsg = "코드매핑 후 YD_STOCK[저장품] 성공 [" + intRtnVal + "] STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}else{	
				szMsg = "코드매핑 후 YD_STOCK[저장품] SELECT 조회건수가 없음 :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}
			
		}catch(Exception e){
			szMsg = "Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
	}
	/**
	 * 외판슬라브출하완료(DMYDR029)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procOutplSlabDistCmpl(JDTORecord inRecord)throws JDTOException  {
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		JDTORecord recInTemp2 = JDTORecordFactory.getInstance().create();
		JDTORecord recResult = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();

		JDTORecordSet rsGetStock = JDTORecordFactory.getInstance().createRecordSet("");
 
		
		String szMethodName = "procOutplSlabDistCmpl";
		String szMsg = "";
		String szOperationName = "외판슬라브출하완료";
		String szSTL_NO ="";
		
		int intRtnVal = 0;

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;

		}
		
		
		ydUtils.displayRecord(szOperationName, inRecord);
		
		
		try{
			//=============================================================
			// 권오창
			// 2009.11.05
			//
			// Log 테이블 등록 
			// 야드는 X로 일단 두었음 수정해야됨
			//=============================================================
			szMsg = "[출하] 외판슬라브출하완료 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			
			
			
			
			//수신한 재료번호
			szSTL_NO  = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");

			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP	재료외형구분
			STL_NO			재료번호
			*/
			
			recStockColumn.setField("STL_APPEAR_GP", 					ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recStockColumn.setField("STL_NO", 							ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STL_PROG_CD" , 					"M");
			recStockColumn.setField("YD_AIM_RT_GP" , 					"M1");
			recStockColumn.setField("MODIFIER", 			"DMYDR029");
			//****************************************************************************************************

						 
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[외판슬라브출하완료] UPDATE Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[외판슬라브출하완료] UPDATE Success", YdConstant.DEBUG);
			//****************************************************************************************************
			
			
	        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		    /* 외판슬라브출하차량출발실적(자동출발)
		    TRANS_ORD_DT	운송지시일자	DATE				
		    TRANS_ORD_SEQNO	운송지시순번	CHAR	4			
		    CAR_NO	차량번호	CHAR	15			
		    CARD_NO	카드번호	CHAR	4		출하차량 ID카드번호	
		    SPOS_WLOC_CD                  	발지개소코드	CHAR	6		차량도착 개소코드	
		    SPOS_YD_PNT_CD                	발지야드포인트코드	CHAR	4		차량도착  포인트코드
		    */		
//PIDEV_S :병행가동용:PI_YD
			inRecord.setField("PI_YD",    	"S");			
			intRtnVal = ydStockDao.getYdStock(inRecord, rsResult, 116);
			if (intRtnVal > 0){	//차량에 대한 모든 재료가 출하완료된 경우 에만 자동출발 처리를 함
				
				rsResult.first();
				recResult = rsResult.getRecord();
				
				recInTemp2.setField("TC_CODE",        		"DMYDR039");									//전문코드
				recInTemp2.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(recResult,"CARD_NO"));
				recInTemp2.setField("CAR_NO", 				ydDaoUtils.paraRecChkNull(recResult,"CAR_NO"));			
				recInTemp2.setField("SPOS_WLOC_CD", 		ydDaoUtils.paraRecChkNull(recResult,"WLOC_CD"));
				recInTemp2.setField("SPOS_YD_PNT_CD", 		ydDaoUtils.paraRecChkNull(recResult,"YD_PNT_CD"));
				recInTemp2.setField("TRANS_ORD_DT", 		ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_DATE"));
				recInTemp2.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_SEQNO"));
				
				
				EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
				Boolean isSucf = (Boolean) ejbConn.trx("rcvOutplSlabDistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp2 });
			}
			 //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

			
			String s_YD_GP = ydDaoUtils.paraRecChkNull(inRecord,"YD_GP");
			
			if(s_YD_GP.equals("A")) //야드구분이 연주슬라브야드인 경우에만 
			{
						
				//======================================================
				// 2009.08.31 권오창
				// 저장품제원 : 연주슬라브L2 로 송신(YDY1L002)
				//======================================================
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YDY1L002");
				recResult.setField("YD_INFO_SYNC_CD", "A");    // A:생산실적
				recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"));
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				recResult.setField("DEL_YN_CHECK"   , "N");			
				ydDelegate.sendMsg(recResult);
			
			}

		}catch(Exception e){
			szMsg = "[외판슬라브출하완료수신]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		szMsg="외판슬라브출하완료수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of procOutplSlabDistCmpl
	
	
	/**
	 * 코일제품출하완료(DMYDR030)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilGdsDistCmpl(JDTORecord inRecord)throws JDTOException  {
		//저장품DAO
		YdStockDao ydStockDao 		= new YdStockDao();
		YdCarSchDao ydCarSchDao 	= new YdCarSchDao();
		JDTORecord recInTemp 		= null;
		JDTORecord recResult 		= null;
		JDTORecordSet rsResult 		= null;
		JDTORecord recStockColumn 	= null;

		String szMethodName 		= "procCoilGdsDistCmpl";
		String szOperationName 		= "코일제품출하완료(DMYDR030)";
		String szMsg 				= "";
		String szSTL_NO 			= "";
		String szCAR_NO 			= null;
		String szCARD_NO 			= null;
		String szYD_STK_COL_GP 		= null;
		String szTRANS_ORD_DATE 	= null;
		String szTRANS_ORD_SEQNO 	= null;
		String sSTL_APPEAR_GP		= "";
		String szYD_GP				= "";
		
		int intRtnVal = 0;

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){

			szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		
		try{
			//=============================================================
			// 권오창
			// 2009.11.05
			//
			// Log 테이블 등록 
			// 야드는 X로 일단 두었음 수정해야됨
			//=============================================================
			szMsg = "[출하] 코일제품출하완료 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);


			
			
			
			//수신한 재료번호
			szSTL_NO 		= ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");
			sSTL_APPEAR_GP	= ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP");
			szYD_GP 		= ydDaoUtils.paraRecChkNull(inRecord,"YD_GP");

			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp 	= JDTORecordFactory.getInstance().create();
			recInTemp.setField("STL_NO", szSTL_NO);
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockJoinStkLyr2*/
			intRtnVal = ydStockDao.getYdStock(recInTemp, rsResult, 503);
			if(intRtnVal <= 0){
				szMsg= "["+szOperationName+"] YD_STOCK[코일제품출하완료] 조회 Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			rsResult.first();
			recInTemp 			= rsResult.getRecord();
			szCAR_NO 			= ydDaoUtils.paraRecChkNull(recInTemp,"CAR_NO");
			szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInTemp,"YD_STK_COL_GP");
			szCARD_NO 			= ydDaoUtils.paraRecChkNull(recInTemp,"CARD_NO");
			szTRANS_ORD_DATE 	= ydDaoUtils.paraRecChkNull(recInTemp,"TRANS_ORD_DATE");
			szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(recInTemp,"TRANS_ORD_SEQNO");
			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP	재료외형구분
			STL_NO			재료번호
			*/
			recStockColumn = JDTORecordFactory.getInstance().create();
			recStockColumn.setField("STL_APPEAR_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recStockColumn.setField("STL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("DEL_YN", 			"Y");
			recStockColumn.setField("STL_PROG_CD" , 	"M");
			recStockColumn.setField("YD_AIM_RT_GP" , 	"M2");
			recStockColumn.setField("MODIFIER", 		"DMYDR030");
			//****************************************************************************************************

						
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[코일제품출하완료] UPDATE Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[코일제품출하완료] UPDATE Success", YdConstant.DEBUG);
			//****************************************************************************************************
			
			
			
			

			
			
			
			/*
			 * 저장품이 적치된 저장위치 정보를 조회
			 */
			szMsg="[" + szOperationName + "]카드번호["+szCARD_NO+"], 차량번호["+szCAR_NO+"], 운송지시일자["+szTRANS_ORD_DATE+"], 운송지시순번["+szTRANS_ORD_SEQNO+"] : 출하완료된 동["+szYD_STK_COL_GP+"]의 저장품들 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//저장품 동 구하기 
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			
//---------------------------------------------------------------------------------------------				
//			recInTemp.setField("STL_NO", 			szSTL_NO);
//			recInTemp.setField("CARD_NO", 			szCARD_NO);
//			recInTemp.setField("TRANS_ORD_DATE", 	szTRANS_ORD_DATE);
//			recInTemp.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
//			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMtlCTransDTSeqCardNo2*/
//			intRtnVal = ydStockDao.getYdStock(recInTemp, rsResult, 504);
////---------------------------------------------------------------------------------------------	
//			
//			if(intRtnVal < 0){
//				szMsg="[" + szOperationName + "] YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "] PARAMETER ERROR";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
//			}else 
				if(sSTL_APPEAR_GP.equals("*") ) {
			
				szMsg="[" + szOperationName + "] 마지막 상차완료 전문";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp  = JDTORecordFactory.getInstance().create();
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp.setField("CAR_NO" , szCAR_NO);
				recInTemp.setField("CARD_NO", szCARD_NO);
				recInTemp.setField("TRANS_ORD_DT", 	szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
				
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschTransDTSeq2_PIDEV*/
				intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 413);
				if(intRtnVal <= 0){
					szMsg = "["+szOperationName+"] 차량스케쥴 조회 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;	
				}
	        
			
				rsResult.first();
				recResult = rsResult.getRecord();
				szCAR_NO = ydDaoUtils.paraRecChkNull(recResult,"CAR_NO");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("TC_CODE",        		"DMYDR040");									//전문코드
				recInTemp.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(recResult,"CARD_NO"));
				recInTemp.setField("CAR_NO", 				szCAR_NO);			
				recInTemp.setField("SPOS_WLOC_CD", 			ydDaoUtils.paraRecChkNull(recResult,"SPOS_WLOC_CD"));
				recInTemp.setField("SPOS_YD_PNT_CD", 		ydDaoUtils.paraRecChkNull(recResult,"YD_PNT_CD1"));
				recInTemp.setField("TRANS_ORD_DT", 			ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_DATE"));
				recInTemp.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_SEQNO"));
				recInTemp.setField("YD_GP", 		szYD_GP);
				
				//E/T Car 나 해송차량인 경우에는 차량출발처리를 자동으로 하지 않는다.
//				if(szCAR_NO.matches("\\d\\d\\d\\d")) {				//E/T Car
				if(szCARD_NO.equals("")){
					szCARD_NO = "XXXXX";
				}				
//				if(szCARD_NO.substring(0, 1).equals("T")||
//						szCARD_NO.substring(0, 1).equals("P")||
//						szCARD_NO.substring(0, 1).equals("E")
//		 	 			){
//					szMsg= "["+ szOperationName +"] E/T Car[" + szCAR_NO + "]는 차량출발처리를 하지 않습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				}else{
				szMsg= "["+ szOperationName +"] 차량번호[" + szCAR_NO + "]는 자동차량출발";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
				ejbConn.trx("rcvCoilGdsdistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				
				
				// 151014 hun 차량 출발후 저장품 정보 갱신
				// L2저장품재원 정보 송신
				// ======================================================
				// 저장품제원 : 코일야드L2로 송신(YDY5L002)
				// ======================================================
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID" , "YDY5L002");
				recResult.setField("YD_INFO_SYNC_CD" , "5"); // 5:지정저장품
				recResult.setField("STL_NO" , ydDaoUtils.paraRecChkNull(inRecord , "STL_NO"));
				recResult.setField("YD_STK_COL_GP" , "");
				recResult.setField("YD_STK_BED_NO" , "");

				ydDelegate.sendMsg(recResult);

				szMsg = "마지막 상차완료 전문 코일야드L2로 응답전문 [YDY5L002] 전송완료";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
				
				
				
					
			}else{
				szMsg="[" + szOperationName + "] 마지막 상차완료 전문이 아님";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			}
			

			 //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//======================================================
			// 2009.08.31 권오창
			// 저장품제원 : 코일야드L2 로 송신(YDY5L002)
			//======================================================
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY5L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			recResult.setField("DEL_YN_CHECK"   , "N");
			ydDelegate.sendMsg(recResult);
			
		}catch(Exception e){
			szMsg = "[코일제품출하완료 수신]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		szMsg="코일제품출하완료 수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of procCoilGdsDistCmpl
	
	
	/**
	 * 후판제품출하완료(DMYDR031)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlGdsDistCmpl(JDTORecord inRecord)throws JDTOException  {
		//저장품DAO
		YdStockDao ydStockDao 				= new YdStockDao();
		YdCarSchDao ydCarSchDao 			= new YdCarSchDao();
		JDTORecord recInTemp 				= null;
		JDTORecord recResult 				= null;
		JDTORecordSet rsResult 				= null;
		JDTORecord recStockColumn 			= null;

		String szMethodName 				= "procPlGdsDistCmpl";
		String szOperationName 				= "후판제품출하완료(DMYDR031)";
		String szMsg 						= "";
		String szSTL_NO 					= "";
		String szCAR_NO 					= null;
		String szCARD_NO 					= null;
		String szTRANS_ORD_DATE 			= null;
		String szTRANS_ORD_SEQNO 			= null;
		String szYD_STK_COL_GP 				= null;
		String szSTL_APPEAR_GP				= null;
		String szCURR_PROG_CD				= null;
		String szYD_GP						= null;
		String szIS9NI      =null;
		
		int intRtnVal = 0;
		boolean is9Ni=false;  //9%니켈 강종 여부 체크
		
		JPlateYdCommDAO 	commDao 		= new JPlateYdCommDAO();
    	String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";		

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){

			szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		
		try{
			//=============================================================
			// 권오창
			// 2009.11.05
			//
			// Log 테이블 등록 
			// 야드는 X로 일단 두었음 수정해야됨
			//=============================================================
			szMsg = "[출하] 후판제품출하완료 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_K, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//수신한 야드구분
			szYD_GP						= ydDaoUtils.paraRecChkNull(inRecord,"YD_GP");

			//수신한 재료번호
			szSTL_NO  					= ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");
			//재료외형구분
			szSTL_APPEAR_GP				= ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP");
			//재료진도코드 
			szCURR_PROG_CD				= ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD");
			
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("STL_NO", 							szSTL_NO);
			
			intRtnVal = ydStockDao.getYdStock(recInTemp, rsResult, 0);
			if(intRtnVal <= 0){
				szMsg= "["+szOperationName+"] YD_STOCK[후판제품출하완료] 조회 Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			rsResult.first();
			recInTemp = rsResult.getRecord();
			
			szCAR_NO 				= ydDaoUtils.paraRecChkNull(recInTemp,"CAR_NO");
//			szYD_STK_COL_GP 		= ydDaoUtils.paraRecChkNull(recInTemp,"YD_STK_COL_GP");
			szCARD_NO 				= ydDaoUtils.paraRecChkNull(recInTemp,"CARD_NO");
			szTRANS_ORD_DATE 		= ydDaoUtils.paraRecChkNull(recInTemp,"TRANS_ORD_DATE");
			szTRANS_ORD_SEQNO 		= ydDaoUtils.paraRecChkNull(recInTemp,"TRANS_ORD_SEQNO");
			
			
			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP	재료외형구분
			STL_NO			재료번호
			*/
			recStockColumn = JDTORecordFactory.getInstance().create();
			recStockColumn.setField("STL_APPEAR_GP", 					szSTL_APPEAR_GP);
			recStockColumn.setField("STL_NO", 							szSTL_NO);
			recStockColumn.setField("DEL_YN", 							"N");
			recStockColumn.setField("CAR_LOTID", 						"");
			recStockColumn.setField("STL_PROG_CD" , 					"L".equals(szCURR_PROG_CD)? "L":"M" );
			recStockColumn.setField("YD_AIM_RT_GP" , 					"L".equals(szCURR_PROG_CD)? "M5":"M3");
			recStockColumn.setField("MODIFIER", 						"DMYDR031");
			//****************************************************************************************************

			//----------------------------------------------------------------------------------------------------
			//	9% 니켈강종이며, A동에 속해있는 재료일 경우, 야드 맵 클리어 작업 수행. 이명운 책임매니저 요청사항
			//	등록자 : 박종호
			//	등록일 : 2021.08.09
			//----------------------------------------------------------------------------------------------------
			JDTORecord recPara2		= JDTORecordFactory.getInstance().create();
			recPara2.setField("PLATE_NO"	, szSTL_NO);    
			//JDTORecordSet getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getPLATECOMM", logId, szMethodName, "제품정보 조회");
			JDTORecordSet getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getChk9NiByStlNo", logId, szMethodName, "제품 9%니켈강여부 조회");
			if (getRecSet.size() > 0) {
				for (int ii=0; ii<getRecSet.size(); ii++) {
					szIS9NI  	= getRecSet.getRecord(ii).getFieldString("PL_NI_RMN_MAG_MEA_MTL_ASGN_GP");
					//if("LR-9NI".equals(szSPEC_ABBSYM) || "9NI-QT".equals(szSPEC_ABBSYM) || "KR-RL9N490QT".equals(szSPEC_ABBSYM))  //해당재료가 9% 니켈강 강종일경우, 재료위치가 제품창고 A동인지 조회
					//if(1==1) //니켈 규정 강종이 자주 바뀌어서, A동에서 제품출하지시받으면 무조건 니켈이라고 가정하고 처리함. 2021.12.02 허동수매니저 요청사항.
					if(szIS9NI.equals("Y")) //9%니켈 판단 여부를 기존 특정 강종코드에서 품질에서 관리되는 FLAG값으로 변경 2021.12.17
					{
						recPara2		= JDTORecordFactory.getInstance().create();
						recPara2.setField("STL_NO"	, szSTL_NO);
						getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getStrLocInfo2", logId, szMethodName, "재료 위치 조회");
						if(getRecSet.size()>0){
							String szYD_GP_BAY_GP  	= getRecSet.getRecord(ii).getFieldString("YD_STK_COL_GP").substring(0,2);
							for (int j=0; j<getRecSet.size(); j++) {
								if("TA".equals(szYD_GP_BAY_GP)){  //제품창고 A동일 경우 해당 재료 적치단 Clear
									is9Ni=true;
									//야드맵 클리어
									recPara2		= JDTORecordFactory.getInstance().create();
									recPara2.setField("MODIFIER"	, "DMYDR031"); 
									recPara2.setField("STL_NO"	, szSTL_NO); 
									intRtnVal = commDao.update(recPara2, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStklyrInitC", logId, szMethodName, "해당 재료 적치단 clear");
									
									if(intRtnVal==0){
										szMsg= "["+ szOperationName +"] 후판제품[" + szSTL_NO + "]에 대한 적치단 clear 과정 중 Error 발생";
										ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									}
									
									recPara2.setField("YD_GP"	, getRecSet.getRecord(j).getFieldString("YD_STK_COL_GP").substring(0,1));  //T
									recPara2.setField("YD_BAY_GP"	, getRecSet.getRecord(j).getFieldString("YD_STK_COL_GP").substring(1,2));  //A
									recPara2.setField("YD_EQP_GP"	, getRecSet.getRecord(j).getFieldString("YD_STK_COL_GP").substring(2,4));  //07
									recPara2.setField("YD_STK_COL_NO",  getRecSet.getRecord(j).getFieldString("YD_STK_COL_GP").substring(4,6));  //61
									recPara2.setField("YD_STK_BED_NO",  getRecSet.getRecord(j).getFieldString("YD_STK_BED_NO"));  //01
									recPara2.setField("YD_STK_LYR_NO",  getRecSet.getRecord(j).getFieldString("YD_STK_LYR_NO"));  //030
									recPara2.setField("YD_STR_LOC",  getRecSet.getRecord(j).getFieldString("YD_STK_COL_GP").substring(0,2)+"PT011001");  //TAPT011001 (Default:차상위치) 
									recPara2.setField("MODIFIER",  "DMYDR031");  
									recPara2.setField("FNL_REG_PGM",  "procPlGdsDistCmpl");
									recPara2.setField("PLATE_NO",  szSTL_NO);
									
									if(intRtnVal!=0){
										//저장위치 수정 모듈 호출(적치단 클리어 및 PLATE_COMM UPDATE)
										szMsg = "해당재료[" + szSTL_NO  +"] 에대한 후판 공통 UPDATE 작업시작" ;
										ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
										
										intRtnVal = ydStockDao.updPtComm_LOC(recPara2, 1);
										
										if(intRtnVal< 0) {
											szMsg = "공통 UPDATE ERROR";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
											//return ;
										}else if (intRtnVal == 0){
											szMsg = "후판 공통 UPDATE 해야할 데이터가 없습니다.";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
										}else{
											szMsg = "후판 공통 UPDATE 성공 재료번호[" + szSTL_NO + "]";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
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
				szMsg= "["+szOperationName+"] YD_STOCK[후판제품출하완료] UPDATE Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"["+szOperationName+"] [2] YD_STOCK[후판제품출하완료] UPDATE Success", YdConstant.DEBUG);
			//****************************************************************************************************

			//----------------------------------------------------------------------------------------------------
			//	재료외형구분에 *로 설정되어 오는 경우에는 마지막재료의 출하완료 처리이므로 차량출발 처리 모듈 호출
			//	등록자 : 임춘수
			//	등록일 : 2010.01.14
			//----------------------------------------------------------------------------------------------------
			
			if( szSTL_APPEAR_GP.equals("*")) {
				
				szMsg= "["+ szOperationName +"] 출하완료된 후판제품[" + szSTL_NO + "]의 재료외형구분["+szSTL_APPEAR_GP+"]이 *이므로 차량출발 모듈 호출 처리";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szMsg= "["+ szOperationName +"] 차량번호[" + szCAR_NO + "]와 카드번호[" + szCARD_NO + "]로 차량스케줄 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
//				recInTemp  = JDTORecordFactory.getInstance().create();
//				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//				recInTemp.setField("CAR_NO" , szCAR_NO);
//				recInTemp.setField("CARD_NO", szCARD_NO);
//				intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 11);
				
				recInTemp  = JDTORecordFactory.getInstance().create();
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp.setField("CAR_NO" , szCAR_NO);
				recInTemp.setField("CARD_NO", szCARD_NO);
				recInTemp.setField("TRANS_ORD_DT", 	szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
				
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschTransDTSeq2_PIDEV*/
				intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 413);				
				if(intRtnVal <= 0){
					szMsg = "["+szOperationName+"] 차량번호[" + szCAR_NO + "]와 카드번호[" + szCARD_NO + "]로 차량스케줄 조회 시 에러발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ; 	
				}
	        
				szMsg= "["+ szOperationName +"] 차량번호[" + szCAR_NO + "]와 카드번호[" + szCARD_NO + "]로 차량스케줄 조회 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
				rsResult.first();
				recResult = rsResult.getRecord();
				szCAR_NO = ydDaoUtils.paraRecChkNull(recResult,"CAR_NO");
			
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("TC_CODE",        		"DMYDR042");	//전문코드
				recInTemp.setField("YD_GP",					szYD_GP);		//야드구분
				recInTemp.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(recResult,"CARD_NO"));
				recInTemp.setField("CAR_NO", 				szCAR_NO);			
				recInTemp.setField("SPOS_WLOC_CD", 			ydDaoUtils.paraRecChkNull(recResult,"SPOS_WLOC_CD"));
				recInTemp.setField("SPOS_YD_PNT_CD", 		ydDaoUtils.paraRecChkNull(recResult,"YD_PNT_CD1"));
				recInTemp.setField("TRANS_ORD_DT", 			ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_DATE"));
				recInTemp.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_SEQNO"));
				
				
				//E/T Car 나 해송차량인 경우에는 차량출발처리를 자동으로 하지 않는다.  
				//if(szCAR_NO.matches("\\d\\d\\d\\d")) {				//E/T Car
				
				//E/T Car 나 해송차량인 경우 또는 9%니켈강종의 경우 차량출발처리를 자동으로 하지 않는다.
				if(szCAR_NO.matches("\\d\\d\\d\\d") || is9Ni) {				//E/T Car 또는 9%니켈 강종
					szMsg= "["+ szOperationName +"] E/T Car[" + szCAR_NO + "]는 차량출발처리를 하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					szMsg= "["+ szOperationName +"] 차량번호[" + szCAR_NO + "]는 자동으로 차량출발 모듈 EJB 호출 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					// 2021. 06. 22 차량선별입동지시시점[T00171] "D"일경우 입동지시 전문이 날아가지 않는 문제 수정
					recInTemp.setField("CALL_PGM", "SANGCHA"); // 입동지시 전문을 보내기 위함
					EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
					ejbConn.trx("rcvPlGdsDistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					 
					szMsg= "["+ szOperationName +"] 차량번호[" + szCAR_NO + "]는 자동으로 차량출발 모듈 EJB 호출 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
			}
			
			
			//======================================================
			// 2009.08.31 권오창
			// 저장품제원 : 후판제품 L2 로 송신(YDY4L002,YDY8002)
			//======================================================
			//if(!is9Ni){  //9%니켈강종의 경우 L2 정보 송신 안함 2021.08.09
			if(true){  //9%니켈강종도 L2 야드 정보 송신으로 변경 2021.08.12 (L2 야드맵 클리어 위해)
				String szMSG_ID = null;
				
				if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(ydDaoUtils.paraRecChkNull(inRecord,"YD_GP"))) { //- 2013.01.17 수정 (3기)
					szMSG_ID = "YDY8L002"; //2후판 제품창고
				} else {
					szMSG_ID = "YDY4L002"; //1후판 제품창고
				}	
				
				szMsg= "["+ szOperationName +"] 후판제품[" + szSTL_NO + "]에 대한 저장품제원을 후판제품L2["+szMSG_ID+"]로 송신 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , szMSG_ID);
				recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
				recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"));
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				recResult.setField("DEL_YN_CHECK"   , "N");			
				ydDelegate.sendMsg(recResult);		
				
				// 2021. 4. 3 출하완료시 Y9도 강제로 전송한다.
				if("YDY8L002".equals(szMSG_ID)){
					recResult.setField("MSG_ID"         , "YDY9L002");
					ydDelegate.sendMsg(recResult);	
				}
				
				szMsg= "["+ szOperationName +"] 후판제품[" + szSTL_NO + "]에 대한 저장품제원을 후판제품L2["+szMSG_ID+"]로 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		}catch(Exception e){
			szMsg = "[후판제품출하완료 수신]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		
		szMsg="후판제품출하완료 수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of procPlGdsDistCmpl
	
	
	/**
	 * C연주주편생산예정종료 (YDYDJ101)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCCsMslabPrdPlnEnd(JDTORecord inRecord)throws JDTOException  {

		String szMethodName = "procCCsMslabPrdPlnEnd";
		String szMsg = "";
		String szOperationName = "C연주주편생산예정종료";

		int intRtnVal = 0;

		YdStockDao ydStockDao = new YdStockDao();
	
		JDTORecordSet rsGetStock = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();
		

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;

		}
		try{
			//생산예정종료를 수신
			//수신한 재료번호로 저장품 읽기
			szMsg = "YD_STOCK[저장품] SELECT :: WHERE[STL_NO]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//수신한 재료번호로 저장품 읽기
			intRtnVal = ydStockDao.getYdStock(inRecord, rsGetStock, 0);
			if(intRtnVal <= 0){
				
				if(intRtnVal == 0){
					szMsg= "YD_STOCK[저장품] SELECT Error :: [" + inRecord.getFieldString("STL_NO") + "]" +"DO NOT EXIST";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}else{
					szMsg= "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]"+"PARAMETER ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}
	
			rsGetStock.first();
			recStockColumn = JDTORecordFactory.getInstance().create();
			recStockColumn = rsGetStock.getRecord();
			
			
			szMsg= "YD_STOCK[저장품] UPDATE :: [2] YD_STOCK UPDATE ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recStockColumn.setField("DEL_YN", "Y");

			intRtnVal =ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[C연주주편생산예정종료 수신]UPDATE Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			szMsg= "["+ recStockColumn.getFieldString("STL_NO") +"] :: YD_STOCK[C연주주편생산예정종료 수신]UPDATE Success  ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
		}catch(Exception e){
			szMsg = "[C연주주편생산예정종료 수신]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		szMsg="C연주주편생산예정종료 수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
	} // end of procCCsMslabPrdPlnEnd
	

	
	
	/**
	 * A후판모슬라브제원종료 (YDYDJ105)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procAPlSlabSpecEnd(JDTORecord inRecord)throws JDTOException  {

		String szMethodName = "procAPlSlabSpecEnd";
		String szMsg = "";
		String szOperationName = "A후판모슬라브제원종료";

		int intRtnVal = 0;

		YdStockDao ydStockDao = new YdStockDao();
		
		JDTORecordSet rsGetStock = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();
		

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;

		}
		try{
			//생산예정종료를 수신
			//수신한 재료번호로 저장품 읽기
			szMsg = "YD_STOCK[저장품] SELECT :: WHERE[STL_NO]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//수신한 재료번호로 저장품 읽기
			intRtnVal = ydStockDao.getYdStock(inRecord, rsGetStock, 0);
			if(intRtnVal <= 0){
				
				if(intRtnVal == 0){
					szMsg= "YD_STOCK[저장품] SELECT Error :: [" + inRecord.getFieldString("STL_NO") + "]" +"DO NOT EXIST";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}else{
					szMsg= "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]"+"PARAMETER ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}
	
			rsGetStock.first();
			recStockColumn = JDTORecordFactory.getInstance().create();
			recStockColumn = rsGetStock.getRecord();
			
			
			szMsg= "YD_STOCK[저장품] UPDATE :: [2] YD_STOCK UPDATE ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recStockColumn.setField("DEL_YN", "Y");

			intRtnVal =ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[A후판모슬라브제원종료 수신]UPDATE Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			szMsg= "["+ recStockColumn.getFieldString("STL_NO") +"] :: YD_STOCK[A후판모슬라브제원종료 수신]UPDATE Success  ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
		}catch(Exception e){
			szMsg = "[A후판모슬라브제원종료 수신]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		szMsg="A후판모슬라브제원종료 수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
	} // end of procAPlSlabSpecEnd
	
	
	//------------

	/**
	 * A후판제품생산예정종료
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procAPlGdsPrdPlnEnd(JDTORecord inRecord)throws JDTOException  {

		String szMethodName = "procAPlGdsPrdPlnEnd";
		String szMsg = "";
		String szOperationName = "A후판제품생산예정종료";

		int intRtnVal = 0;

		YdStockDao ydStockDao = new YdStockDao();
		
		JDTORecordSet rsGetStock = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();
		

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;

		}
		try{
			//생산예정종료를 수신
			//수신한 재료번호로 저장품 읽기
			szMsg = "YD_STOCK[저장품] SELECT :: WHERE[STL_NO]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//수신한 재료번호로 저장품 읽기
			intRtnVal = ydStockDao.getYdStock(inRecord, rsGetStock, 0);
			if(intRtnVal <= 0){
				
				if(intRtnVal == 0){
					szMsg= "YD_STOCK[저장품] SELECT Error :: [" + inRecord.getFieldString("STL_NO") + "]" +"DO NOT EXIST";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}else{
					szMsg= "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]"+"PARAMETER ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}
	
			rsGetStock.first();
			recStockColumn = JDTORecordFactory.getInstance().create();
			recStockColumn = rsGetStock.getRecord();
			
			
			szMsg= "YD_STOCK[저장품] UPDATE :: [2] YD_STOCK UPDATE ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recStockColumn.setField("DEL_YN", "Y");

			intRtnVal =ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[A후판제품생산예정종료 수신]UPDATE Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			szMsg= "["+ recStockColumn.getFieldString("STL_NO") +"] :: YD_STOCK[A후판제품생산예정종료 수신]UPDATE Success  ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
		}catch(Exception e){
			szMsg = "[A후판제품생산예정종료 수신]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		szMsg="A후판제품생산예정종료 수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procAPlGdsPrdPlnEnd
	
	
	
	
	/**
	 * C연주정정모주편제원종료
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCCsShearMslabSpecEnd(JDTORecord inRecord)throws JDTOException  {

		String szMethodName = "procCCsShearMslabSpecEnd";
		String szMsg = "";
		String szOperationName = "C연주정정모주편제원종료";

		int intRtnVal = 0;
		YdStockDao ydStockDao = new YdStockDao();
		
		JDTORecordSet rsGetStock = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();
		

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;

		}
		try{
			//생산예정종료를 수신
			//수신한 재료번호로 저장품 읽기
			szMsg = "YD_STOCK[저장품] SELECT :: WHERE[STL_NO]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//수신한 재료번호로 저장품 읽기
			intRtnVal = ydStockDao.getYdStock(inRecord, rsGetStock, 0);
			if(intRtnVal <= 0){
				
				if(intRtnVal == 0){
					szMsg= "YD_STOCK[저장품] SELECT Error :: [" + inRecord.getFieldString("STL_NO") + "]" +"DO NOT EXIST";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}else{
					szMsg= "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]"+"PARAMETER ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}
	
			rsGetStock.first();
			recStockColumn = JDTORecordFactory.getInstance().create();
			recStockColumn = rsGetStock.getRecord();
			
			
			szMsg= "YD_STOCK[저장품] UPDATE :: [2] YD_STOCK UPDATE ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recStockColumn.setField("DEL_YN", "Y");

			intRtnVal =ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[C연주정정모주편제원종료 수신]UPDATE Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			szMsg= "["+ recStockColumn.getFieldString("STL_NO") +"] :: YD_STOCK[C연주정정모주편제원종료 수신]UPDATE Success  ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
		}catch(Exception e){
			szMsg = "[C연주정정모주편제원종료 수신]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		szMsg="C연주정정모주편제원종료 수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procCCsShearMslabSpecEnd
	
	
	/**
	 * C열연모슬라브제원종료
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCHrSlabSpecEnd(JDTORecord inRecord)throws JDTOException  {

		String szMethodName = "procCHrSlabSpecEnd";
		String szMsg = "";
		String szOperationName = "C열연모슬라브제원종료";

		int intRtnVal = 0;
		YdStockDao ydStockDao = new YdStockDao();
		
		JDTORecordSet rsGetStock = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();
		

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;

		}
		try{
			//생산예정종료를 수신
			//수신한 재료번호로 저장품 읽기
			szMsg = "YD_STOCK[저장품] SELECT :: WHERE[STL_NO]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//수신한 재료번호로 저장품 읽기
			intRtnVal = ydStockDao.getYdStock(inRecord, rsGetStock, 0);
			if(intRtnVal <= 0){
				
				if(intRtnVal == 0){
					szMsg= "YD_STOCK[저장품] SELECT Error :: [" + inRecord.getFieldString("STL_NO") + "]" +"DO NOT EXIST";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}else{
					szMsg= "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]"+"PARAMETER ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}
	
			rsGetStock.first();
			recStockColumn = JDTORecordFactory.getInstance().create();
			recStockColumn = rsGetStock.getRecord();
			
			
			szMsg= "YD_STOCK[저장품] UPDATE :: [2] YD_STOCK UPDATE ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recStockColumn.setField("DEL_YN", "Y");

			intRtnVal =ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[C열연모슬라브제원종료 수신]UPDATE Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			szMsg= "["+ recStockColumn.getFieldString("STL_NO") +"] :: YD_STOCK[C열연모슬라브제원종료 수신]UPDATE Success  ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
		}catch(Exception e){
			szMsg = "[C열연모슬라브제원종료 수신]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		szMsg="C열연모슬라브제원종료 수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procCHrSlabSpecEnd
	
	
	/**
	 * C열연정정모코일제원종료
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCHrShearCoilSpecEnd(JDTORecord inRecord)throws JDTOException  {

		String szMethodName = "procCHrShearCoilSpecEnd";
		String szMsg = "";
		String szOperationName = "C열연정정모코일제원종료";

		int intRtnVal = 0;

		YdStockDao ydStockDao = new YdStockDao();
		
		JDTORecordSet rsGetStock = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();
		

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;

		}
		try{
			//생산예정종료를 수신
			//수신한 재료번호로 저장품 읽기
			szMsg = "YD_STOCK[저장품] SELECT :: WHERE[STL_NO]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//수신한 재료번호로 저장품 읽기
			intRtnVal = ydStockDao.getYdStock(inRecord, rsGetStock, 0);
			if(intRtnVal <= 0){
				
				if(intRtnVal == 0){
					szMsg= "YD_STOCK[저장품] SELECT Error :: [" + inRecord.getFieldString("STL_NO") + "]" +"DO NOT EXIST";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}else{
					szMsg= "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]"+"PARAMETER ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}
	
			rsGetStock.first();
			recStockColumn = JDTORecordFactory.getInstance().create();
			recStockColumn = rsGetStock.getRecord();
			
			szMsg= "YD_STOCK[저장품] UPDATE :: [2] YD_STOCK UPDATE ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recStockColumn.setField("DEL_YN", "Y");

			intRtnVal =ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[C열연정정모코일제원종료 수신]UPDATE Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			szMsg= "["+ recStockColumn.getFieldString("STL_NO") +"] :: YD_STOCK[C열연정정모코일제원종료 수신]UPDATE Success";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
		}catch(Exception e){
			szMsg = "[C열연정정모코일제원종료 수신]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		szMsg="C열연정정모코일제원종료 수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procCHrShearCoilSpecEnd
	
	
	
	/**
	 * 코일이송상차완료PDA(DMYDR072)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilGdsDistCmplLdPDA(JDTORecord inRecord)throws JDTOException  {
		//저장품DAO
		YdStockDao ydStockDao 		= new YdStockDao();
		YdCarSchDao ydCarSchDao 	= new YdCarSchDao();
		JDTORecord recInTemp 		= null;
		JDTORecord recResult 		= null;
		JDTORecordSet rsResult 		= null;
		JDTORecord recStockColumn 	= null;

		String szMethodName 		= "procCoilGdsDistCmplLdPDA";
		String szOperationName 		= "코일이송상차완료PDA(DMYDR072)";
		String szMsg 				= "";
		String szSTL_NO 			= "";
		String szCAR_NO 			= null;
		String szCARD_NO 			= null;
		String szYD_STK_COL_GP 		= null;
		String szTRANS_ORD_DATE 	= null;
		String szTRANS_ORD_SEQNO 	= null;
		String sSTL_APPEAR_GP		= "";
		int intRtnVal = 0;

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){

			szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		
		try{

			//수신한 재료번호
			szSTL_NO 		= ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");
			sSTL_APPEAR_GP	= ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP");

			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp 	= JDTORecordFactory.getInstance().create();
			recInTemp.setField("STL_NO", szSTL_NO);
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockJoinStkLyr2*/
			intRtnVal = ydStockDao.getYdStock(recInTemp, rsResult, 503);
			if(intRtnVal <= 0){
				szMsg= "["+szOperationName+"] YD_STOCK[코일제품출하완료] 조회 Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			rsResult.first();
			recInTemp 			= rsResult.getRecord();
			szCAR_NO 			= ydDaoUtils.paraRecChkNull(recInTemp,"CAR_NO");
			szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInTemp,"YD_STK_COL_GP");
			szCARD_NO 			= ydDaoUtils.paraRecChkNull(recInTemp,"CARD_NO");
			szTRANS_ORD_DATE 	= ydDaoUtils.paraRecChkNull(recInTemp,"TRANS_ORD_DATE");
			szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(recInTemp,"TRANS_ORD_SEQNO");
			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP	재료외형구분
			STL_NO			재료번호
			*/
			recStockColumn = JDTORecordFactory.getInstance().create();
			recStockColumn.setField("STL_APPEAR_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));			
			recStockColumn.setField("YD_AIM_RT_GP" , 	"M2");
			recStockColumn.setField("DEL_YN", 			"N");	
			recStockColumn.setField("MODIFIER", 		szRcvTcCode);
			recStockColumn.setField("STL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			//****************************************************************************************************

						
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStockEndReg(recStockColumn);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[코일제품출하완료] UPDATE Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[코일제품출하완료] UPDATE Success", YdConstant.DEBUG);
			//****************************************************************************************************
			
			
			
			

			
			
			
			/*
			 * 저장품이 적치된 저장위치 정보를 조회
			 */
			szMsg="[" + szOperationName + "]카드번호["+szCARD_NO+"], 운송지시일자["+szTRANS_ORD_DATE+"], 운송지시순번["+szTRANS_ORD_SEQNO+"] : 출하완료된 동["+szYD_STK_COL_GP+"]의 저장품들 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//저장품 동 구하기 
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
 
			if(sSTL_APPEAR_GP.equals("*") ) {
		
				szMsg="[" + szOperationName + "] 마지막 상차완료 전문";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp  = JDTORecordFactory.getInstance().create();
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp.setField("CAR_NO" , szCAR_NO);
				recInTemp.setField("CARD_NO", szCARD_NO);
				recInTemp.setField("TRANS_ORD_DT", 	szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
				
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschTransDTSeq2_PIDEV*/
				intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 413);
				if(intRtnVal <= 0){
					szMsg = "["+szOperationName+"] 차량스케쥴 조회 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;	
				}
	        
			
				rsResult.first();
				recResult = rsResult.getRecord();
				szCAR_NO = ydDaoUtils.paraRecChkNull(recResult,"CAR_NO");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("TC_CODE",        		"DMYDR040");									//전문코드
				recInTemp.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(recResult,"CARD_NO"));
				recInTemp.setField("CAR_NO", 				szCAR_NO);			
				recInTemp.setField("SPOS_WLOC_CD", 			ydDaoUtils.paraRecChkNull(recResult,"SPOS_WLOC_CD"));
				recInTemp.setField("SPOS_YD_PNT_CD", 		ydDaoUtils.paraRecChkNull(recResult,"YD_PNT_CD1"));
				recInTemp.setField("TRANS_ORD_DT", 			ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_DATE"));
				recInTemp.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_SEQNO"));

				if(szCARD_NO.equals("")){
					szCARD_NO = "XXXX";
				}				

				szMsg= "["+ szOperationName +"] 차량번호[" + szCAR_NO + "]는 자동차량출발";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
				ejbConn.trx("rcvCoilGdsdistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
			}else{
				szMsg="[" + szOperationName + "] 마지막 상차완료 전문이 아님";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			}
 
			 //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			
			
			
			
			//======================================================
			// 2009.08.31 권오창
			// 저장품제원 : 코일야드L2 로 송신(YDY5L002)
			//======================================================
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY5L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			recResult.setField("DEL_YN_CHECK"   , "N");
			ydDelegate.sendMsg(recResult);
			
		}catch(Exception e){
			szMsg = "[코일제품출하완료 수신]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		szMsg="코일제품출하완료 수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of procCoilGdsDistCmplLdPDA
	
	
	
	/**
	 * 코일이송상차완료PDA(DMYDR075)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilGdsDistCmplUdPDA(JDTORecord inRecord)throws JDTOException  {
		//저장품DAO
		YdStockDao ydStockDao 		= new YdStockDao();
		YdCarSchDao ydCarSchDao 	= new YdCarSchDao();
		JDTORecord recInTemp 		= null;
		JDTORecord recResult 		= null;
		JDTORecordSet rsResult 		= null;
		JDTORecord recStockColumn 	= null;

		String szMethodName 		= "procCoilGdsDistCmplUdPDA";
		String szOperationName 		= "코일이송상차완료PDA(DMYDR075)";
		String szMsg 				= "";
		String szSTL_NO 			= "";
		String szCAR_NO 			= null;
		String szCARD_NO 			= null;
		String szYD_STK_COL_GP 		= null;
		String szTRANS_ORD_DATE 	= null;
		String szTRANS_ORD_SEQNO 	= null;
		String sSTL_APPEAR_GP		= "";
		int intRtnVal = 0;

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){

			szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		
		try{

			//수신한 재료번호
			szSTL_NO 		= ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");
			sSTL_APPEAR_GP	= ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP");

			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp 	= JDTORecordFactory.getInstance().create();
			recInTemp.setField("STL_NO", szSTL_NO);
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockJoinStkLyr2*/
			intRtnVal = ydStockDao.getYdStock(recInTemp, rsResult, 503);
			if(intRtnVal <= 0){
				szMsg= "["+szOperationName+"] YD_STOCK[코일제품출하완료] 조회 Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			rsResult.first();
			recInTemp 			= rsResult.getRecord();
			szCAR_NO 			= ydDaoUtils.paraRecChkNull(recInTemp,"CAR_NO");
			szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInTemp,"YD_STK_COL_GP");
			szCARD_NO 			= ydDaoUtils.paraRecChkNull(recInTemp,"CARD_NO");
			szTRANS_ORD_DATE 	= ydDaoUtils.paraRecChkNull(recInTemp,"TRANS_ORD_DATE");
			szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(recInTemp,"TRANS_ORD_SEQNO");
			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP	재료외형구분
			STL_NO			재료번호
			*/
			recStockColumn = JDTORecordFactory.getInstance().create();
			recStockColumn.setField("STL_APPEAR_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));			
			recStockColumn.setField("YD_AIM_RT_GP" , 	"M2");
			recStockColumn.setField("DEL_YN", 			"N");	
			recStockColumn.setField("MODIFIER", 		szRcvTcCode);
			recStockColumn.setField("STL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			//****************************************************************************************************

						
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStockEndReg(recStockColumn);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[코일제품출하완료] UPDATE Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[코일제품출하완료] UPDATE Success", YdConstant.DEBUG);
			//****************************************************************************************************
			
			
			
			

			
			
			
			/*
			 * 저장품이 적치된 저장위치 정보를 조회
			 */
			szMsg="[" + szOperationName + "]카드번호["+szCARD_NO+"], 운송지시일자["+szTRANS_ORD_DATE+"], 운송지시순번["+szTRANS_ORD_SEQNO+"] : 출하완료된 동["+szYD_STK_COL_GP+"]의 저장품들 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//저장품 동 구하기 
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
 
			if(sSTL_APPEAR_GP.equals("*") ) {
		
				szMsg="[" + szOperationName + "] 마지막 상차완료 전문";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp  = JDTORecordFactory.getInstance().create();
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp.setField("CAR_NO" , szCAR_NO);
				recInTemp.setField("CARD_NO", szCARD_NO);
				recInTemp.setField("TRANS_ORD_DT", 	szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
				
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschTransDTSeq2_PIDEV*/
				intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 413);
				if(intRtnVal <= 0){
					szMsg = "["+szOperationName+"] 차량스케쥴 조회 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;	
				}
	        
			
				rsResult.first();
				recResult = rsResult.getRecord();
				szCAR_NO = ydDaoUtils.paraRecChkNull(recResult,"CAR_NO");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("TC_CODE",        		"DMYDR040");									//전문코드
				recInTemp.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(recResult,"CARD_NO"));
				recInTemp.setField("CAR_NO", 				szCAR_NO);			
				recInTemp.setField("SPOS_WLOC_CD", 			ydDaoUtils.paraRecChkNull(recResult,"SPOS_WLOC_CD"));
				recInTemp.setField("SPOS_YD_PNT_CD", 		ydDaoUtils.paraRecChkNull(recResult,"YD_PNT_CD1"));
				recInTemp.setField("TRANS_ORD_DT", 			ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_DATE"));
				recInTemp.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_SEQNO"));

				if(szCARD_NO.equals("")){
					szCARD_NO = "XXXX";
				}				

				szMsg= "["+ szOperationName +"] 차량번호[" + szCAR_NO + "]는 자동차량출발";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
				ejbConn.trx("rcvCoilGdsdistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
			}else{
				szMsg="[" + szOperationName + "] 마지막 상차완료 전문이 아님";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			}
 
			 //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			
			
			
			
			//======================================================
			// 2009.08.31 권오창
			// 저장품제원 : 코일야드L2 로 송신(YDY5L002)
			//======================================================
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY5L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			recResult.setField("DEL_YN_CHECK"   , "N");
			ydDelegate.sendMsg(recResult);
			
		}catch(Exception e){
			szMsg = "[코일제품출하완료 수신]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		szMsg="코일제품출하완료 수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of procCoilGdsDistCmplUdPDA
	

  //---------------------------------------------------------------------------	
} // end of class StockSpecEndSeEJBBean
