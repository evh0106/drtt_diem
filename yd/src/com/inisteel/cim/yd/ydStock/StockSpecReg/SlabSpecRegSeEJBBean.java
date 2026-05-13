package com.inisteel.cim.yd.ydStock.StockSpecReg;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdCodeMapping;


/**
 * 
 * 슬라브제원등록 Session EJB
 *
 * @ejb.bean name="SlabSpecRegSeEJB" jndi-name="SlabSpecRegSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class SlabSpecRegSeEJBBean extends BaseSessionBean  {
	
	// Session Name
	private String szSessionName   = getClass().getName();
	
	private YdUtils ydUtils 	   = new YdUtils();
	private YdDaoUtils ydDaoUtils  = new YdDaoUtils();
	private StockSpecRegSeEJBBean stock  = new StockSpecRegSeEJBBean();
	private YdConstant ydConstant  = new YdConstant();
	private YdDelegate ydDelegate = new YdDelegate();
	
	// [DEBUG] message flag
	private boolean bDebugFlag	   = true;
	String[] rVal = new String[1];
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * 연주전단실적 (CSYDJ001) --구소스 사용 안함
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCcFsWr(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		YdStockDao ydStockDao 	     = new YdStockDao();
		YdCodeMapping ydCodeMapping  = new YdCodeMapping();

		 
		// 레코드 선언
		JDTORecord recResult 	     = null;
		JDTORecord recPara           = null;
		JDTORecord recGetVal         = null;
		JDTORecord recEditRec        = null;
		JDTORecord outRecTemp        = null;
		JDTORecordSet rsGetMslabComm = null;
		JDTORecordSet rsGetSlabComm  = null;
		JDTORecordSet rsOut          = null;
		JDTORecord recGetHistVal   = null;
		JDTORecord recSetMltstat   = null;
		JDTORecordSet rsGetYdStock = null;	

		
		// 변수 선언
		String szMethodName 	     = "procCcFsWr";
		String szMsg 			     = "";
		String szOperationName       = "연주전단실적";		
		String szRECORD_PROG_STAT    = "";
		String szSTL_NO              = "";
		String szSLAB_WO_RT_CD       = "";
		String szYD_STK_LOT_CD       = "";
		boolean bReadSlab            = false;
		int intRtnVal 			     = 0;
		int nRet                     = 0;
		
		// 전문받아서 szRcvTcCode에 대입
		String szRcvTcCode = ydUtils.getTcCode(inRecord);
		if(szRcvTcCode == null){
			szMsg = " TC Code Error : [" + szRcvTcCode + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		if(bDebugFlag){
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
				
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[연주조업] 연주전단실적 수신";
			ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			recResult  = JDTORecordFactory.getInstance().create();
			recEditRec = JDTORecordFactory.getInstance().create();
			rsOut      = JDTORecordFactory.getInstance().createRecordSet("");
			
			//=============================================================================================
			// 주편공통 조회 (GP : 6 )
			//=============================================================================================
			rsGetMslabComm = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("MSLAB_NO", ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"));
			intRtnVal = ydStockDao.getYdStock(recPara, rsGetMslabComm, 6);
			if(intRtnVal < 0){
				szMsg = "[연주전단실적 (CSYDJ001)] 주편공통 테이블 조회 파라미터 에러 nRet(" + intRtnVal + ") MSLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(intRtnVal == 0){
				szMsg = "[연주전단실적 (CSYDJ001)] 주편공통테이블 조회 데이터가 없음 nRet(" + intRtnVal + ") MSLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				//=========================================================================================================
				// 슬라브공통 조회 (GP : 2)
				//=========================================================================================================
				rsGetSlabComm = JDTORecordFactory.getInstance().createRecordSet("");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("SLAB_NO", ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"));
				intRtnVal = ydStockDao.getYdStock(recPara, rsGetSlabComm, 2);
				if(intRtnVal < 0){
					szMsg = "[연주전단실적 (CSYDJ001)] 슬라브공통 테이블 조회 파라미터 에러 nRet(" + intRtnVal + ") SLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				} else if(intRtnVal == 0){
					szMsg = "[연주전단실적 (CSYDJ001)] 슬라브공통 테이블 조회 데이터가 없음 nRet(" + intRtnVal + ") SLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return ;
				} else {
					szMsg = "[연주전단실적 (CSYDJ001)] 슬라브공통 테이블 조회 성공 nRet(" + intRtnVal + ") SLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					// 슬라브 레코드 가져옴
					rsGetSlabComm.first();
					recGetVal = rsGetSlabComm.getRecord();
					
					bReadSlab = true;
				}
			} else {
				szMsg = "[연주전단실적 (CSYDJ001)] 주편공통테이블 조회 성공 nRet(" + intRtnVal + ") MSLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				// 주편 레코드 가져옴
				rsGetMslabComm.first();
				recGetVal = rsGetMslabComm.getRecord();				
			}

			
			if(bReadSlab == false){
				szRECORD_PROG_STAT = ydDaoUtils.paraRecChkNull(recGetVal, "RECORD_PROG_STAT");
				if(szRECORD_PROG_STAT.equals("3")){
					//=========================================================================================================
					// 슬라브공통 조회 (GP : 2)
					//=========================================================================================================
					rsGetSlabComm = JDTORecordFactory.getInstance().createRecordSet("");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("SLAB_NO", ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"));
					intRtnVal = ydStockDao.getYdStock(recPara, rsGetSlabComm, 2);
					if(intRtnVal < 0){
						szMsg = "[연주전단실적 (CSYDJ001)] 슬라브공통 테이블 조회 파라미터 에러 nRet(" + intRtnVal + ") SLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					} else if(intRtnVal == 0){
						szMsg = "[연주전단실적 (CSYDJ001)] 슬라브공통 테이블 조회 데이터가 없음 nRet(" + intRtnVal + ") SLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						return ;
					}else {
						szMsg = "[연주전단실적 (CSYDJ001)] 슬라브공통 테이블 조회 성공 nRet(" + intRtnVal + ") SLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}
					
					// 슬라브 레코드 가져옴
					rsGetSlabComm.first();
					recGetVal = rsGetSlabComm.getRecord();
					
					//=============================================================================================
					// 슬라브공통에서 읽어온 레코드편집
					//=============================================================================================
					// CUST_CD 이값은 슬라브공통에 조회시에 없음...
					intRtnVal = this.edtSlabYdstock(recGetVal, recEditRec);
				} else {
					//=============================================================================================
					// 주편공통에서 읽어온 레코드편집
					//=============================================================================================
					intRtnVal = this.edtMslabToYdstock(recGetVal, recEditRec);
				}
			} else {
				//=============================================================================================
				// 슬라브공통에서 읽어온 레코드편집
				// CUST_CD 이값은 슬라브공통에 조회시에 없음...
				//=============================================================================================
				intRtnVal = this.edtSlabYdstock(recGetVal, recEditRec);
			}
			// 항목 편집이 실패일 때			
			if(intRtnVal < 0){
				szMsg = "[연주전단실적 항목편집1] Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else {
				szMsg = "[연주전단실적 항목편집1] 성공:: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}
			//============================================================
			// 재료품목 항목 추출
			//============================================================
			intRtnVal = stock.SetYD_MTL_ITEM_SLAB(recEditRec);	
			if(intRtnVal < 0){
				szMsg = "[연주전단실적 항목편집2] Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}else {
				szMsg = "[연주전단실적 항목편집2] 성공:: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);								
			}
			/* 2010.12.20 윤재광 - 사용안함
			intRtnVal = this.getYdEtcItem(recGetVal, recEditRec);
			if(intRtnVal < 0){
				szMsg = "[연주전단실적 항목편집3] Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}else {
				szMsg = "[연주전단실적 항목편집3] 성공:: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);								
			}			
			*/		
			szSLAB_WO_RT_CD    = ydDaoUtils.paraRecChkNull(recEditRec, "SLAB_WO_RT_CD"); 
			szYD_STK_LOT_CD    = ydDaoUtils.paraRecChkNull(recEditRec, "YD_STK_LOT_CD"); 
			
			//=============================================================================================
			// 저장품 업데이트
			//=============================================================================================
			intRtnVal = ydStockDao.getYdStock(recEditRec, rsOut, 0);
			if(intRtnVal <0){
				szMsg = "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}else if(intRtnVal > 0){
				recEditRec.setField("MODIFIER", "CSYDJ001");
				intRtnVal = ydStockDao.updYdStock(recEditRec, 0);
				if(intRtnVal < 0){
					szMsg = "YD_STOCK[저장품] UPDATE Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				
				szMsg = recEditRec.getFieldString("STL_NO") + " :: YD_STOCK[저장품] UPDATE Success";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{	
				recEditRec.setField("REGISTER", "CSYDJ001"); 
				intRtnVal = ydStockDao.insYdStock(recEditRec);
				if(intRtnVal < 0){
					szMsg = "YD_STOCK[저장품] INSERT Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				
				szMsg = recEditRec.getFieldString("STL_NO") + " :: YD_STOCK[저장품] INSERT Success";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			//=============================================================================================
			// 코드 매핑값 호출 
			//			
			//     * 주편/슬라브 조회 시(1건) 코드매핑의 재료정보는 편집된 재료번호(STL_NO)로 조회
			//       저장품 업데이트할 레코드 : recEditRec
			//
			//=============================================================================================

			// 재료번호를 미리 가져오고 밑에서 다시 인스턴스 생성
			szSTL_NO = ydDaoUtils.paraRecChkNull(recEditRec, "STL_NO");

			// 위에서 INSERT/UPDATE 쳤기때문에 인스턴스를 새로 만들어서 코드매핑후 가져올 값만 다시 업데이트  
			outRecTemp = JDTORecordFactory.getInstance().create();
			recEditRec = JDTORecordFactory.getInstance().create();

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

			if(ydDaoUtils.paraRecChkNull(inRecord, "STL_NO").startsWith("M"))
			{
				/*
				 * 전단실적 시 L2로 저장품제원 전문 전송
				 * 슬라브지시행선이 HB 인 재료는 B-CAST에서도 발생할 수 있으므로 
				 * C연주에서도 발생할 수 있다
				 * 일단은 모두 C연주슬라브야드L2로 전송 처리 
				 */
				/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	    		 * 업무기준 : 연주전단실적수신 시 저장품 제원 야드L2로 전송
	    		 * 수정자 : 임춘수
	    		 * 수정일자 : 2009.08.24
	    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
				recResult  = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YDY1L002");
				recResult.setField("YD_INFO_SYNC_CD", "A");								// 1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
				recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recEditRec, "STL_NO"));
				recResult.setField("YD_STK_COL_GP"  , YdConstant.YD_GP_C_SLAB_YARD);    // C연주슬라브야드
		    	//recInTemp.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_NO"));
		    	ydDelegate.sendMsg(recResult);
			    	
		    	szMsg ="<procCHrMillPrdWr> 연주전단실적수신 시 저장품 제원 야드L2[YDY1L002]로 전송";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */			
			}
			
		}catch(Exception e){
			szMsg = "[연주전단실적]Exception Error:"+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[procCcFsWr_연주전단실적]" + szMsg);
		}

		szMsg = "수신한 연주전단실적 등록 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} //end of procCcFsWr()




	
	/**
	 * 연주전단실적으로 부터 받은 항목 편집 - 주편공통 
	 * return -1: 항목 편집 Exception error
	 * 		   1: 항목 편집 성공	
	 * 
	 * @param inRecord
	 * @param outRecord
	 * @throws JDTOException

	 */
	public int ccFsTcEdt(JDTORecord inRecord,JDTORecord outRecord) throws JDTOException {

		String szMethodName 	= "ccFsTcEdt";
		String szMsg 			= "";
	
		try{
			
			outRecord.setField("STL_NO"			, ydDaoUtils.paraRecChkNull(inRecord, "MSLAB_NO")); 		//재료번호	
			outRecord.setField("YD_MTL_W"		, ydDaoUtils.paraRecChkNull(inRecord, "YD_MTL_W"));			//재료폭
			outRecord.setField("YD_MTL_L"		, ydDaoUtils.paraRecChkNull(inRecord, "YD_MTL_L"));			//재료길이
			outRecord.setField("YD_MTL_T"		, ydDaoUtils.paraRecChkNull(inRecord, "YD_MTL_T"));			//재료두께
			outRecord.setField("YD_MTL_WT"		, ydDaoUtils.paraRecChkNull(inRecord, "YD_MTL_WT"));		//재료중량
			outRecord.setField("ORD_NO"			, ydDaoUtils.paraRecChkNull(inRecord, "ORD_NO"));			//주문번호
			outRecord.setField("ORD_GP"			, ydDaoUtils.paraRecChkNull(inRecord, "ORD_GP"));	        //수주구분
			outRecord.setField("ORD_DTL"		, ydDaoUtils.paraRecChkNull(inRecord, "ORD_DTL"));			//주문행번
			outRecord.setField("ORD_HCR_GP"		, ydDaoUtils.paraRecChkNull(inRecord, "ORD_HCR_GP"));		//지시HCR구분
			outRecord.setField("HCR_GP"			, ydDaoUtils.paraRecChkNull(inRecord, "WR_HCR_GP"));	    //실적HCR구분
			outRecord.setField("CC_CCM_NO"		, ydDaoUtils.paraRecChkNull(inRecord, "CC_CCM_NO"));		//연주머신코드
			outRecord.setField("ORD_YEOJAE_GP"	, ydDaoUtils.paraRecChkNull(inRecord, "ORD_YEOJAE_GP"));	//주문여재구분
			outRecord.setField("STL_APPEAR_GP"	, ydDaoUtils.paraRecChkNull(inRecord, "STL_APPEAR_GP"));	//재료외형구분
			outRecord.setField("STL_PROG_CD"	, ydDaoUtils.paraRecChkNull(inRecord, "CURR_PROG_CD"));		//재료진도코드
			outRecord.setField("SLAB_WO_RT_CD"	, ydDaoUtils.paraRecChkNull(inRecord, "SLAB_WO_RT_CD"));	//슬라브지시행선코드
			outRecord.setField("SCARFING_YN"	, ydDaoUtils.paraRecChkNull(inRecord, "SCARFING_YN"));  	//SCARFING여부
			outRecord.setField("PTOP_PLNT_GP"	, ydDaoUtils.paraRecChkNull(inRecord, "PTOP_PLNT_GP"));		//조업공장구분
			outRecord.setField("PLNT_PROC_CD"	, ydDaoUtils.paraRecChkNull(inRecord, "PLNT_PROC_CD"));		//공장공정코드
			outRecord.setField("YD_STK_LOT_CD"	, ydDaoUtils.paraRecChkNull(inRecord, "STACK_LOT_NO"));		
			outRecord.setField("YD_MTL_STAT"	, "2");	
			
		} catch(Exception e) {

			szMsg ="Exception Error:"+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[ccFsTcEdt_연주전단실적항목편집]" + szMsg);
		}

		return 0;
	} //end of ccFsTcEdt()
	
	
	

	

	/**
	 * SCARFING실적 (CSYDJ002, QMYDJ004)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procScarfWr(JDTORecord inRecord)throws JDTOException  {
		YdStockDao ydStockDao 		 = new YdStockDao();
		YdCodeMapping ydCodeMapping  = new YdCodeMapping();
		
		JDTORecordSet rsGetMslabComm = null;
		JDTORecordSet rsGetSlabComm  = null;
		JDTORecordSet rsOutRecSet    = null;
		JDTORecord recEditRec        = null;
		JDTORecord recOut 			 = null;
		JDTORecord outRecTemp        = null;
		JDTORecord recPara           = null;
		JDTORecord recGetVal         = null;
		
		String szMethodName          = "procScarfWr";
		String szMsg                 = "";
		String szOperationName       = "SCARFING실적";
		String szRcvTcCode           = ydUtils.getTcCode(inRecord);
		String szSTL_NO              = "";
		String szRECORD_PROG_STAT    = "";
		String szSLAB_WO_RT_CD       = "";
		String szYD_STK_LOT_CD       = "";		
		boolean bReadSlab            = false;
		int intRtnVal                = 0;
		int nRet                     = 0;
		
		if(szRcvTcCode == null){
			szMsg = szSessionName+"::"+ szMethodName+"() TC Code Error ("+ szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		if(bDebugFlag){
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[연주조업] SCARFING실적 수신";
			ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recEditRec = JDTORecordFactory.getInstance().create();
			recOut = JDTORecordFactory.getInstance().create();
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("MSLAB_NO", ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"));
			rsGetMslabComm = JDTORecordFactory.getInstance().createRecordSet("");

			//=========================================================================================================
			// 주편테이블 조회 (GP:35)
			//=========================================================================================================
			intRtnVal = ydStockDao.getYdStock(recPara, rsGetMslabComm, 35); 
			if(intRtnVal < 0){
				szMsg = "[스카핑 실적 (" + szRcvTcCode + ")] 주편공통 테이블 조회 파라미터 에러 nRet(" + intRtnVal + ") MSLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(intRtnVal == 0){
				szMsg = "[스카핑 실적 (" + szRcvTcCode + ")] 주편공통테이블 조회 데이터가 없음 nRet(" + intRtnVal + ") MSLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				//=========================================================================================================
				// 슬라브공통 조회 (GP:36)
				//=========================================================================================================
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("SLAB_NO", ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"));
				rsGetSlabComm = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal = ydStockDao.getYdStock(recPara, rsGetSlabComm, 36);
				if(intRtnVal < 0){
					szMsg = "[스카핑 실적 (" + szRcvTcCode + ")] 슬라브공통 테이블 조회 파라미터 에러 nRet(" + intRtnVal + ") SLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				} else if(intRtnVal == 0){
					szMsg = "[스카핑 실적 (" + szRcvTcCode + ")] 슬라브공통 테이블 조회 데이터가 없음 nRet(" + intRtnVal + ") SLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return ;
				} else {
					// 슬라브 레코드 가져옴
					rsGetSlabComm.first();
					recGetVal = rsGetSlabComm.getRecord();
					
					bReadSlab = true;
				}
			} else {
				// 주편 레코드 가져옴
				rsGetMslabComm.first();
				recGetVal = rsGetMslabComm.getRecord();				
			}
			
			if(bReadSlab == false){
				szRECORD_PROG_STAT = ydDaoUtils.paraRecChkNull(recGetVal, "RECORD_PROG_STAT");
				if(szRECORD_PROG_STAT.equals("3")){
					//=========================================================================================================
					// 슬라브공통 조회 (GP:36)
					//=========================================================================================================
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("SLAB_NO", ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"));
					rsGetSlabComm = JDTORecordFactory.getInstance().createRecordSet("");
					intRtnVal = ydStockDao.getYdStock(recPara, rsGetSlabComm, 36);
					if(intRtnVal < 0){
						szMsg = "[스카핑 실적 (" + szRcvTcCode + ")] 슬라브공통 테이블 조회 파라미터 에러 nRet(" + intRtnVal + ") SLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					} else if(intRtnVal == 0){
						szMsg = "[스카핑 실적 (" + szRcvTcCode + ")] 슬라브공통 테이블 조회 데이터가 없음 nRet(" + intRtnVal + ") SLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}	
					
					// 슬라브 레코드 가져옴
					rsGetSlabComm.first();
					recGetVal = rsGetSlabComm.getRecord();
					
					// 슬라브공통에서 읽어온 레코드편집
					intRtnVal = this.edtSlabYdstock(recGetVal, recEditRec);
				} else {
					// 주편공통에서 읽어온 레코드편집
					intRtnVal = this.edtMslabToYdstock(recGetVal, recEditRec);
				}
			} else {
				// 슬라브공통에서 읽어온 레코드편집
				intRtnVal = this.edtSlabYdstock(recGetVal, recEditRec);				
			}

			intRtnVal = this.getYdEtcItem2(recEditRec, recOut);
			// 항목 편집이 실패일 때
			if(intRtnVal < 0){
				szMsg = "[SCARFING실적 항목편집] Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}

			szSLAB_WO_RT_CD    = ydDaoUtils.paraRecChkNull(recOut, "SLAB_WO_RT_CD"); 
			szYD_STK_LOT_CD    = ydDaoUtils.paraRecChkNull(recOut, "YD_STK_LOT_CD"); 			
			
			// 저장품 업데이트 처리
			intRtnVal = ydStockDao.getYdStock(recOut, rsOutRecSet, 0);
			if(intRtnVal < 0){
				ydUtils.putLog(szSessionName, szMethodName, "STOCK[저장품] SELECT Error :: PARAMETER ERROR", 1);	
				return ;
			}else if(intRtnVal == 0){
				ydUtils.putLog(szSessionName, szMethodName, "\n======== STOCK[저장품] INSERT ========\n", 4);
				recOut.setField("REGISTER", szRcvTcCode);
				intRtnVal = ydStockDao.insYdStock(recOut);
				if(intRtnVal < 0){
					ydUtils.putLog(szSessionName, szMethodName, "STOCK[저장품] INSERT Error :: PARAMETER ERROR", 1);	
					return ;
				}
			}else{
				ydUtils.putLog(szSessionName, szMethodName, "\n======== STOCK[저장품] UPDATE ========\n", 4);
				recOut.setField("MODIFIER", szRcvTcCode);
				intRtnVal = ydStockDao.updYdStock(recOut, 0);
				if(intRtnVal <=0){
					ydUtils.putLog(szSessionName, szMethodName, "STOCK[저장품] UPDATE Error :: ["+intRtnVal+"]", 1);	
					return ;
				}
			}
			
            //=============================================================================================
			// 코드 매핑값 호출 
			//
			//       업데이트 할 레코드 => recOut
            //=============================================================================================
			szSTL_NO = ydDaoUtils.paraRecChkNull(recEditRec, "STL_NO");
			recOut = JDTORecordFactory.getInstance().create();
			outRecTemp = JDTORecordFactory.getInstance().create();
			
			// 새로 업데이트 할 재료번호 설정
			recOut.setField("STL_NO", szSTL_NO);
			
			// 코드매핑 처리 호출
			nRet = ydCodeMapping.MakeCodeMapping(szRcvTcCode, szSTL_NO, inRecord, outRecTemp);
			if(nRet <= 0){
				String szTempSTL_APPEAR_GP =  ydDaoUtils.paraRecChkNull(outRecTemp, "STL_APPEAR_GP");
				if(!szTempSTL_APPEAR_GP.trim().equals("")){
					recOut.setField("STL_APPEAR_GP", szTempSTL_APPEAR_GP);
				}
									
				String szTempSCARFING_YN =  ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_YN");
				if(!szTempSCARFING_YN.trim().equals("")){
					recOut.setField("SCARFING_YN", szTempSCARFING_YN);
				}

				String szSCARFING_DONE_YN =  ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_DONE_YN");
				if(!szSCARFING_DONE_YN.trim().equals("")){
					recOut.setField("SCARFING_DONE_YN", szSCARFING_DONE_YN);
				}
				
				szMsg = "[nRet " + nRet + "] 매핑되는 코드가 없습니다. 재료외형, 스카핑여부, 스카핑 완료여부는 업데이트를 위함 STL_APPEAR_GP(" + szTempSTL_APPEAR_GP + ") SCARFING_YN(" + szTempSCARFING_YN + ") SCARFING_DONE_YN(" + szSCARFING_DONE_YN + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else {
				String szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "STL_APPEAR_GP");
				if(!szSTL_APPEAR_GP.equals("")){
					recOut.setField("STL_APPEAR_GP"   , szSTL_APPEAR_GP);
				}
				
				String szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_RT_GP");
				if(!szYD_AIM_RT_GP.equals("")){
					recOut.setField("YD_AIM_RT_GP"    , szYD_AIM_RT_GP);
				}

				String szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_YD_GP");
				if(!szYD_AIM_YD_GP.equals("")){
					recOut.setField("YD_AIM_YD_GP"    , szYD_AIM_YD_GP);
				}

				String szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_BAY_GP");
				if(!szYD_AIM_BAY_GP.equals("")){
					recOut.setField("YD_AIM_BAY_GP"   , szYD_AIM_BAY_GP);
				}
				
				String szSCARFING_YN = ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_YN");
				if(!szSCARFING_YN.equals("")){
					recOut.setField("SCARFING_YN"   , szSCARFING_YN);
				}
				
				String szSCARFING_DONE_YN = ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_DONE_YN");
				if(!szSCARFING_DONE_YN.equals("")){
					recOut.setField("SCARFING_DONE_YN"   , szSCARFING_DONE_YN);
				}		
			}
            //=============================================================================================
			//==============================================================
			// 산적 LOT타입, 산적 LOT코드
			//==============================================================
			recOut.setField("SLAB_WO_RT_CD", szSLAB_WO_RT_CD);
			recOut.setField("YD_STK_LOT_CD", szYD_STK_LOT_CD);
			stock.setYdStkLocTpCd(recOut);			
			
			// 저장품 업데이트
			intRtnVal = ydStockDao.updYdStock(recOut, 0);
			if(intRtnVal <=0){
				ydUtils.putLog(szSessionName, szMethodName, "STOCK[저장품] UPDATE Error :: ["+intRtnVal+"]", 1);	
				return ;
			}
			//======================================================
			// 저장품제원 : 연주슬라브L2 로 송신(YDY1L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY1L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			ydDelegate.sendMsg(recResult);
			
		}catch(Exception e){
			szMsg ="[SCARFING실적] Exception Error:" + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[procScarfWr_SCARFING실적]" + szMsg);
		} // end of try-catch
		
		szMsg = "SCARFING실적 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procScarfWr() 

	
	
	
	
	

	/**
	 * 후판제품 상세변경 (QMYDJ005) - 2010.02.17 이영근
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procQMPlateProgSync(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		YdStockDao ydStockDao 					= new YdStockDao();
		YdCodeMapping ydCodeMapping             = new YdCodeMapping();
		
		// 레코드 선언
		JDTORecord recPara					    = null;
		JDTORecord recGetValMSlab				= null;
		JDTORecord recGetValProc				= null;
		JDTORecord recEdit						= null;
		JDTORecord recGetStock					= null;
		JDTORecord recResult					= null;
		JDTORecord outRecTemp					= null;
		JDTORecord recLogTemp					= null;
		JDTORecord recGetVal					= null;
		JDTORecord recGetHistVal                = null;
		JDTORecord recSetMltstat                = null;
		JDTORecordSet rsResult			 		= null;
		JDTORecordSet rsResultSlab   	 		= null;
		JDTORecordSet rsYdStock			 		= null;
		JDTORecordSet rsGetYdStock	            = null;
		
		
		// 변수 선언
		String szMethodName 					= "procQMPlateProgSync";
		String szMsg 							= "";
		String szOperationName                  = "후판제품 상세변경 (QMYDJ005)";
		String szRcvTcCode                      = null;
		String szOCCUR_DDTT 					= "";
		String szRECORD_PROG_STAT               = "";
		String szMSLAB_STL_NO                   = "";
		String szYD_GP                          = "";
		String szSTL_NO                         = "";
		String szMAXWORK_STEP_NO                = "";
		
		boolean bReadSlab                       = false;
		int nRet                                = 0; 
		

		// 전문받아서 szRcvTcCode에 대입
		szRcvTcCode = ydUtils.getTcCode(inRecord);
		if(szRcvTcCode == null){
			szMsg = "[ERROR] " + szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		ydUtils.displayRecord(szOperationName, inRecord);

		
		try{
			//=============================================================
			// Log 테이블 등록 
			// 야드는 X로 일단 두었음 수정해야됨
			//=============================================================
			szMsg = "[품질관리] 후판제품 상세변경(QMYDJ005) 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_K , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			
			

			
			// 수신항목 - 재료번호
			szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");

			
			
			
			
			// 재료번호로 후판제품 조회하여 저장품에 업데이트
			ydCodeMapping.getMappingCommonFieldPlateComm(szRcvTcCode, szSTL_NO);
				 
			
			szMsg = "[품질관리] 후판제품 상세변경(QMYDJ005) 수신 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
		}catch(Exception e){
			szMsg = "[후판제품 상세변경(QMYDJ005)] Exception Error:" + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		} 

		szMsg = "후판제품 상세변경 등록(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	}	// End of procQMPlateProgSync	
	
	/**
	 * 연주정정실적 (CSYDJ003)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCsShearWr(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		YdStockDao ydStockDao 		 = new YdStockDao();
		YdStkLyrDao ydStkLyrDao      = new YdStkLyrDao();
		YdCodeMapping ydCodeMapping  = new YdCodeMapping();

		// 레코드 선언
		JDTORecordSet rsGetSlabComm  = null;
		JDTORecordSet rsOutRecSet 	 = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsResult   	 = null;
		JDTORecord recPara 		     = null;
		JDTORecord recSlab 		     = null;
		JDTORecord recEditRec	     = null;
		JDTORecord outRecTemp        = null;
		JDTORecord recGetVal         = null;
		
		// 변수선언
		String szMethodName 		 = "procCsShearWr";
		String szMsg 				 = "";
		String szOperationName       = "연주정정실적";
		String szRcvTcCode			 = ydUtils.getTcCode(inRecord);
		String szPARENT_SLAB_NO      = "";
		String szSTL_NO              = "";
		String szYdGp                = "";
		String szYD_STK_COL_GP       = "";
		String szSLAB_WO_RT_CD       = "";
		String szYD_STK_LOT_CD       = "";		
		String szTemp                = "";
		int intRtnVal 				 = 0;
		int nRet                     = 0;
		
		
		if(szRcvTcCode==null){
			szMsg ="[ERROR] " + szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[연주조업] 연주정정실적 수신";
			ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			//==================================================================
			// 연주정정실적 수신 시 모재료(PARENT_SLAB_NO)는 저장품에서 삭제처리 (DEL_YN)
			//==================================================================
			szPARENT_SLAB_NO = ydDaoUtils.paraRecChkNull(inRecord, "PARENT_SLAB_NO");
			recEditRec = JDTORecordFactory.getInstance().create();
			recEditRec.setField("STL_NO", szPARENT_SLAB_NO);
			recEditRec.setField("DEL_YN", "Y");
			intRtnVal = ydStockDao.updYdStock(recEditRec, 0);
			if(intRtnVal <= 0){
				szMsg = "YD_STOCK[저장품] 모재료 DEL_YN = 'Y' UPDATE 에러 :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else {
				szMsg = "YD_STOCK[저장품] 모재료 DEL_YN = 'Y' UPDATE 성공 :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
							
			//=========================================================================================================
			// 저장품 업데이트 (DEL_YN = 'N')
			//=========================================================================================================
			szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");
			recEditRec = JDTORecordFactory.getInstance().create();
			recEditRec.setField("STL_NO", szSTL_NO);
			recEditRec.setField("DEL_YN", "N");
			intRtnVal = ydStockDao.updYdStock(recEditRec, 0);
			if(intRtnVal <= 0){
				szMsg = "YD_STOCK[저장품] STL_NO(" + szSTL_NO + ") DEL_YN = 'N' UPDATE 에러 :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else {
				szMsg = "YD_STOCK[저장품] STL_NO(" + szSTL_NO + ") DEL_YN = 'N' UPDATE 성공 :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			recEditRec 		= JDTORecordFactory.getInstance().create();
			
			//=========================================================================================================
			// 슬라브공통 조회 (GP : 2)
			//=========================================================================================================
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("SLAB_NO", szSTL_NO);
			rsGetSlabComm = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydStockDao.getYdStock(recPara, rsGetSlabComm, 2);
			if(intRtnVal < 0){
				szMsg = "[연주정정실적 (CSYDJ003)] 슬라브공통 테이블 조회 파라미터 에러 nRet(" + intRtnVal + ") SLAB_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(intRtnVal == 0){
				szMsg = "[연주정정실적 (CSYDJ003)] 슬라브공통 테이블 조회 데이터가 없음 nRet(" + intRtnVal + ") SLAB_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			} else {
				szMsg = "[연주정정실적 (CSYDJ003)] 슬라브공통 테이블 조회 SUCCESS";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				// 슬라브 레코드 가져옴
				rsGetSlabComm.first();
				recGetVal = rsGetSlabComm.getRecord();	
			}
			
			// 슬라브공통에서 읽어온 레코드편집
			intRtnVal = this.edtSlabYdstock(recGetVal, recEditRec);
			// 재료품목 편집
			intRtnVal  = stock.SetYD_MTL_ITEM_SLAB(recEditRec);						
			
			szSLAB_WO_RT_CD    = ydDaoUtils.paraRecChkNull(recEditRec, "SLAB_WO_RT_CD"); 
			szYD_STK_LOT_CD    = ydDaoUtils.paraRecChkNull(recEditRec, "YD_STK_LOT_CD"); 
			
			//=========================================================================================================
			// 예정압연지시 정보 셋팅.
			//=========================================================================================================
			rsResult	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO" , szSTL_NO);
			
			if(szSLAB_WO_RT_CD.startsWith("H")){
				// 슬라브 열연장입Lot번호    [야드장입순번 :가열로장입Lot번호(REFUR_CHG_LOT_NO)]
				nRet = ydStockDao.getYdStock(recPara, rsResult, 203);
			}else if(szSLAB_WO_RT_CD.startsWith("P")){
			    // 슬라브 후판장입일련번호    [야드장입순번 :가열로장입장입일련번호(REFUR_CHG_PLN_SERNO)]
				nRet = ydStockDao.getYdStock(recPara, rsResult, 205);
			}
			
			if(nRet < 0) {
				szMsg = "재료번호로 가열로장입LOT번호와 가열로장입LOT순번 조회 오류 (" + nRet + ") STL_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);			
			} else if(nRet == 0){
				szMsg = "재료번호로 가열로장입LOT번호와 가열로장입LOT순번 조회건수 없음 (" + nRet + ") STL_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
			} else {
				szMsg = "재료번호로 가열로장입LOT번호와 가열로장입LOT순번 조회성공 (" + nRet + ") STL_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);							

				rsResult.first();
				recGetVal = rsResult.getRecord();
				
				if(szSLAB_WO_RT_CD.startsWith("H")){
					recEditRec.setField("REFUR_CHG_LOT_NO", 	ydDaoUtils.paraRecChkNull(recGetVal, "REFUR_CHG_LOT_NO"));
					recEditRec.setField("REFUR_CHG_PLN_SERNO", 	ydDaoUtils.paraRecChkNull(recGetVal, "REFUR_CHG_PLN_SERNO"));
				}else{
					recEditRec.setField("REFUR_CHG_LOT_NO", 	ydDaoUtils.paraRecChkNull(recGetVal, "REFUR_CHG_LOT_NO"));
					recEditRec.setField("REFUR_CHG_PLN_SERNO", 	ydDaoUtils.paraRecChkNull(recGetVal, "YD_CHG_NO"));
				}
			}
			
			// 저장품 조회
			intRtnVal = ydStockDao.getYdStock(recEditRec, rsOutRecSet, 0);
			if(intRtnVal < 0){
				szMsg = "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
				return ;
			}
			
			// 저장품 등록
			else if(intRtnVal == 0){
				ydUtils.putLog(szSessionName, szMethodName, "YD_STOCK[저장품] INSERT ", 3);
				
				recEditRec.setField("REGISTER", "CSYDJ003");
				intRtnVal = ydStockDao.insYdStock(recEditRec);
				if(intRtnVal < 0){
					szMsg = "YD_STOCK[저장품] INSERT Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
					return ;
				}
				szMsg = recEditRec.getFieldString("STL_NO") +" :: YD_STOCK[저장품] INSERT Success  " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			// 저장품 갱신
			else{
				ydUtils.putLog(szSessionName, szMethodName, "YD_STOCK[저장품] UPDATE ", 3);	

				recEditRec.setField("MODIFIER", "CSYDJ003");
				intRtnVal = ydStockDao.updYdStock(recEditRec, 0);
				if(intRtnVal <= 0){
					szMsg = "YD_STOCK[저장품] UPDATE Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				szMsg = recEditRec.getFieldString("STL_NO") +" :: YD_STOCK[저장품] UPDATE Success  " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}		
			
          //=============================================================================================
		  // 코드 매핑값 호출 
		  //
		  //     * 앞에서 코드매핑값에 대해 어떤처리를 하든지 다시 엎어침... 
		  //       나중에 앞에서 아래코드에 대해 처리하는 부분 삭제해야 됨      
		  //     
		  //     * 주석처리 (2009.09.18)
		  //     * 주석해제 (2009.10.06)
		  //
		  //     * 매핑할 재료번호는 수신 전문의 재료번호로 처리
		  //       업데이트 할 레코드명 : recEditRec
          //=============================================================================================
			outRecTemp = JDTORecordFactory.getInstance().create();
			recEditRec = JDTORecordFactory.getInstance().create();
			
			// 새로 메모리 할당받은  레코드에 업데이트에 필요한 재료번호를 설정
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
			// 산적 LOT타입, 산적 LOT코드
			//==============================================================
			recEditRec.setField("SLAB_WO_RT_CD", szSLAB_WO_RT_CD);
			recEditRec.setField("YD_STK_LOT_CD", szYD_STK_LOT_CD);
			stock.setYdStkLocTpCd(recEditRec);	
			
			// 저장품 업데이트
			intRtnVal = ydStockDao.updYdStock(recEditRec, 0);
			if(intRtnVal <= 0){
				szMsg = "YD_STOCK[저장품] UPDATE Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			//========================================================================
			// 적치단 테이블 조회
			//========================================================================	
			rsResult	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO"             , szSTL_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT", "C");
			nRet = ydStkLyrDao.getYdStklyr(recPara, rsResult, 3);
			if(nRet < 0){
				szMsg = "적치단테이블 조회 오류 nRet[" + nRet + "] STL_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);										
			} else if(nRet == 0){
				szMsg = "적치단테이블 조회 건수 없음 nRet[" + nRet + "] STL_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);														
			} else {
				szMsg = "적치단테이블 조회 건수 성공 nRet[" + nRet + "] STL_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);																		
				
				rsResult.first();
				recGetVal = rsResult.getRecord();
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_COL_GP");
				if(!szYD_STK_COL_GP.trim().equals("")){
					szTemp = szYD_STK_COL_GP.substring(0, 1);
				}
			}
			szMsg = "====================== 적치단 테이블 조회 OUT =======================";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
			
			boolean isSend = true;
			
			if("A".equals(szTemp)){
				recSlab  = JDTORecordFactory.getInstance().create();
				recSlab.setField("MSG_ID"         , "YDY1L002");
				recSlab.setField("YD_INFO_SYNC_CD", "A");							//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
				recSlab.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recEditRec, "STL_NO"));
				recSlab.setField("YD_STK_COL_GP"  , YdConstant.YD_GP_C_SLAB_YARD);			//C연주 슬라브야드
		    	
		    	ydDelegate.sendMsg(recSlab);
			    	
		    	szMsg ="<procCHrMillPrdWr> 연주정정실적수신 시 슬라브지시행선이 [" + szSLAB_WO_RT_CD + "]인 저장품 제원 야드L2[YDY1L002]로 전송";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				isSend = false;
				
			}else if("D".equals(szTemp)){	
				
				recSlab  = JDTORecordFactory.getInstance().create();
				recSlab.setField("MSG_ID"         , "YDY3L002");
				recSlab.setField("YD_INFO_SYNC_CD", "A");							//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
				recSlab.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recEditRec, "STL_NO"));
				recSlab.setField("YD_STK_COL_GP"  , YdConstant.YD_GP_A_PLATE_SLAB_YARD);			//A후판슬라브야드
		    	
		    	ydDelegate.sendMsg(recSlab);
			    	
		    	szMsg ="<procCHrMillPrdWr> 연주정정실적수신 시 슬라브지시행선이 [" + szSLAB_WO_RT_CD + "]인 저장품 제원 야드L2[YDY3L002]로 전송";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				isSend = false;
			}
			
			if(isSend){
				/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	    		 * 업무기준 : 연주정정실적수신 시 저장품 제원 야드L2로 전송
	    		 * 			슬라브지시행선이 PA, PB인 경우에만 전송
	    		 * 수정자 : 임춘수
	    		 * 수정일자 : 2009.08.24
	    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
				if( szSLAB_WO_RT_CD.equals("PA") || 
					szSLAB_WO_RT_CD.equals("PB") ) {
					recSlab  = JDTORecordFactory.getInstance().create();
					recSlab.setField("MSG_ID"         , "YDY3L002");
					recSlab.setField("YD_INFO_SYNC_CD", "A");							//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
					recSlab.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recEditRec, "STL_NO"));
					recSlab.setField("YD_STK_COL_GP"  , YdConstant.YD_GP_A_PLATE_SLAB_YARD);			//A후판슬라브야드
			    	
			    	ydDelegate.sendMsg(recSlab);
				    	
			    	szMsg ="<procCHrMillPrdWr> 연주정정실적수신 시 슬라브지시행선이 [" + szSLAB_WO_RT_CD + "]인 저장품 제원 야드L2[YDY3L002]로 전송";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}
			/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
		}catch(Exception e){
			szMsg = "[연주정정실적수신] Exception Error :: " +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[procCsShearWrr_연주정정실적]" + szMsg);
		} // end of try-catch
		ydUtils.putLog(szSessionName, szMethodName, "[연주정정실적수신] 처리("+szMethodName+") 완료",4);
	} // end of procCsShearWr()
	
	/**
	 *주편공통항목을 야드 저장품 항목으로 편집
	 * return  0: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtMslabToYdstock(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {


		String szMethodName	= "edtMslabToYdstock";
		String szMsg		= "";

		try{
			
			recEditRec.setField("STL_NO"			, ydDaoUtils.paraRecChkNull(inRecord,"MSLAB_NO")); 			//재료번호	
			recEditRec.setField("STL_APPEAR_GP"		, ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP")); 	//재료외형구분
			recEditRec.setField("YD_MTL_W"			, ydDaoUtils.paraRecChkNull(inRecord,"MSLAB_W"));			//재료폭    (쿼리에서는 REAL_MEASURE_SLAB_W 를 ALIAS)
			recEditRec.setField("YD_MTL_L"			, ydDaoUtils.paraRecChkNull(inRecord,"MSLAB_L"));			//재료길이 (쿼리에서 REAL_MEASURE_SLAB_LEN 를 ALIAS)
			recEditRec.setField("YD_MTL_T"			, ydDaoUtils.paraRecChkNull(inRecord,"MSLAB_T"));			//재료두께 (쿼리에서 REAL_MEASURE_SLAB_T 를 ALIAS)
			recEditRec.setField("YD_MTL_WT"			, ydDaoUtils.paraRecChkNull(inRecord,"MSLAB_WT"));			//재료중량 (쿼리에서 CAL_SLAB_WT 를 ALIAS)
			recEditRec.setField("STL_PROG_CD"		, ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));		//현재진도코드
			recEditRec.setField("ORD_YEOJAE_GP"		, ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));		//주문여재구분
			recEditRec.setField("ORD_NO"			, ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO"));			//주문번호
			recEditRec.setField("ORD_DTL"			, ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL"));			//주문행번
			recEditRec.setField("SLAB_WO_RT_CD"		, ydDaoUtils.paraRecChkNull(inRecord,"SLAB_WO_RT_CD"));		//슬라브 지시행선코드
			recEditRec.setField("ORD_HCR_GP"		, ydDaoUtils.paraRecChkNull(inRecord,"ORD_HCR_GP"));		//설계HCR구분
			recEditRec.setField("HCR_GP"			, ydDaoUtils.paraRecChkNull(inRecord,"WR_HCR_GP"));			//HCR구분
			recEditRec.setField("SCARFING_YN"		, ydDaoUtils.paraRecChkNull(inRecord,"SCARFING_YN"));		//Scarfing 여부
			recEditRec.setField("SCARFING_DONE_YN"	, ydDaoUtils.paraRecChkNull(inRecord,"SCARFING_DONE_YN")); 	//Scarfing 완료유무
			recEditRec.setField("PTOP_PLNT_GP"		, ydDaoUtils.paraRecChkNull(inRecord,"PTOP_PLNT_GP")); 		//조업공장구분
			recEditRec.setField("CC_CCM_NO"			, ydDaoUtils.paraRecChkNull(inRecord,"CC_CCM_NO"));			//연주머신코드
			recEditRec.setField("PLNT_PROC_CD"		, ydDaoUtils.paraRecChkNull(inRecord,"PLNT_PROC_CD"));		//공장공정코드
			recEditRec.setField("ITEMNAME_CD"		, ydDaoUtils.paraRecChkNull(inRecord,"ITEMNAME_CD"));
			recEditRec.setField("DEMANDER_CD"		, ydDaoUtils.paraRecChkNull(inRecord,"DEMANDER_CD"));
			recEditRec.setField("SPEC_ABBSYM"		, ydDaoUtils.paraRecChkNull(inRecord,"SPEC_ABBSYM"));	
			recEditRec.setField("WO_MSLAB_RPR_MTD"	, ydDaoUtils.paraRecChkNull(inRecord,"WO_MSLAB_RPR_MTD"));	
			recEditRec.setField("YD_STK_LOT_CD"		, ydDaoUtils.paraRecChkNull(inRecord,"STACK_LOT_NO"));
			recEditRec.setField("YD_GP"	 , ydDaoUtils.paraRecChkNull(inRecord,"YD_GP"));
			 
		} catch(Exception e){

			return -1;
		}
		return 0;

	} //end of edtMslabToYdstock()
	
	
	/**
	 *슬라브공통 항목을 야드 저장품 항목으로 편집
	 * return  0: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtSlabYdstock(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {

		try{

			recEditRec.setField("STL_NO"			 , ydDaoUtils.paraRecChkNull(inRecord,"SLAB_NO")); 					
			recEditRec.setField("YD_MTL_T"			 , ydDaoUtils.paraRecChkNull(inRecord,"SLAB_T"));    // (쿼리에서는 REAL_MEASURE_SLAB_T 를 ALIAS) 		
			recEditRec.setField("YD_MTL_W"			 , ydDaoUtils.paraRecChkNull(inRecord,"SLAB_W")); 	 // (쿼리에서는 REAL_MEASURE_SLAB_W 를 ALIAS)	    
			recEditRec.setField("YD_MTL_L"			 , ydDaoUtils.paraRecChkNull(inRecord,"SLAB_LEN"));	 // (쿼리에서는 REAL_MEASURE_SLAB_LEN 를 ALIAS)	
			recEditRec.setField("YD_MTL_WT"			 , ydDaoUtils.paraRecChkNull(inRecord,"SLAB_WT"));	 // (쿼리에서는 CAL_SLAB_WT 를 ALIAS)	
			recEditRec.setField("STL_APPEAR_GP"		 , ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));				
			recEditRec.setField("STL_PROG_CD"		 , ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));				
			recEditRec.setField("ORD_YEOJAE_GP"		 , ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));			
			recEditRec.setField("ORD_NO"			 , ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO")); 			
			recEditRec.setField("ORD_DTL"			 , ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL")); 
			recEditRec.setField("ORD_GP"			 , ydDaoUtils.paraRecChkNull(inRecord,"ORD_GP")); 
			recEditRec.setField("SLAB_WO_RT_CD"		 , ydDaoUtils.paraRecChkNull(inRecord,"SLAB_WO_RT_CD"));			
			recEditRec.setField("ORD_HCR_GP"		 , ydDaoUtils.paraRecChkNull(inRecord,"ORD_HCR_GP")); 			
			recEditRec.setField("HCR_GP"			 , ydDaoUtils.paraRecChkNull(inRecord,"HCR_GP"));
			recEditRec.setField("SCARFING_YN"		 , ydDaoUtils.paraRecChkNull(inRecord,"SCARFING_YN")); 			
			recEditRec.setField("SCARFING_DONE_YN"	 , ydDaoUtils.paraRecChkNull(inRecord,"SCARFING_DONE_YN")); 
			recEditRec.setField("PTOP_PLNT_GP"		 , ydDaoUtils.paraRecChkNull(inRecord,"PTOP_PLNT_GP")); 	 
			recEditRec.setField("PLNT_PROC_CD"		 , ydDaoUtils.paraRecChkNull(inRecord,"PLNT_PROC_CD")); 
			recEditRec.setField("ITEMNAME_CD"		 , ydDaoUtils.paraRecChkNull(inRecord,"ITEMNAME_CD"));
			recEditRec.setField("DEMANDER_CD"		 , ydDaoUtils.paraRecChkNull(inRecord,"DEMANDER_CD"));
			recEditRec.setField("SPEC_ABBSYM"		 , ydDaoUtils.paraRecChkNull(inRecord,"SPEC_ABBSYM"));
			recEditRec.setField("YD_STK_LOT_CD"		 , ydDaoUtils.paraRecChkNull(inRecord,"STACK_LOT_NO"));
			recEditRec.setField("MILL_WO_EXN"		 , ydDaoUtils.paraRecChkNull(inRecord,"MILL_WO_EXN"));
			recEditRec.setField("REHEAT_SLAB_GP"	 , ydDaoUtils.paraRecChkNull(inRecord,"REHEAT_SLAB_GP"));
			recEditRec.setField("WO_MSLAB_RPR_MTD"	 , ydDaoUtils.paraRecChkNull(inRecord,"WO_MSLAB_RPR_MTD"));
			recEditRec.setField("CC_CCM_NO"			 , ydDaoUtils.paraRecChkNull(inRecord,"CCM_NO"));
			recEditRec.setField("OVERALL_STAMP_GRADE", ydDaoUtils.paraRecChkNull(inRecord,"OVERALL_STAMP_GRADE"));
			recEditRec.setField("HANDSCARFING_YN"	 , ydDaoUtils.paraRecChkNull(inRecord,"HANDSCARFING_YN"));
			recEditRec.setField("YD_GP"	 			 , ydDaoUtils.paraRecChkNull(inRecord,"YD_GP"));
			recEditRec.setField("YD_MTL_STAT"		 , "2"); 
						
		} catch(Exception e){
			throw new JDTOException(e.getMessage());
		}
		return 1;

	} //end of edtSlabYdstock()
	

	
	
	/**
	 * 외판슬라브반품(DMYDR032)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procOutplSlabRetngds(JDTORecord inRecord)throws JDTOException  {
		//저장품DAO
		YdStockDao ydStockDao 					= new YdStockDao();
		
		JDTORecord recStockColumn 				= JDTORecordFactory.getInstance().create();

		
		JDTORecord recPara                      = null; 
		JDTORecord recGetVal                    = null; 
		JDTORecordSet rsResult                  = null; 
		int nRet                                = 0;
		String szTRANS_ORD_DATE                 = "";
		String szTRANS_ORD_SEQNO                = "";
		String szMethodName 					= "procOutplSlabRetngds";
		String szMsg 							= "";
		String szOperationName                  = "외판슬라브반품";
		String szSTL_NO							= "";
		int intRtnVal 							= 0;

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg ="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		
		// 수신한 전문내용 
		ydUtils.displayRecord(szOperationName, inRecord);
		
		
		try{
			//=============================================================
			// 권오창
			// 2009.11.05
			//
			// Log 테이블 등록 
			// 야드는 X로 일단 두었음 수정해야됨
			//=============================================================
			szMsg = "[출하] 외판슬라브반품 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//수신한 재료번호
			szSTL_NO  = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");

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
			
			recStockColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recStockColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STL_PROG_CD",			ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			recStockColumn.setField("ORD_YEOJAE_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));
			recStockColumn.setField("ORD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO"));
			recStockColumn.setField("ORD_DTL", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL"));
			recStockColumn.setField("ORD_GP", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_GP"));
			recStockColumn.setField("CUST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD"));
			recStockColumn.setField("DEST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"DEST_CD"));
			recStockColumn.setField("DEST_TEL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"DEST_TEL_NO"));
			recStockColumn.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"));
			recStockColumn.setField("MODIFIER", 			"DMYDR032");

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("S",inRecord );		
			recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
			//****************************************************************************************************

						
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg = "YD_STOCK[외판슬라브반품] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[외판슬라브반품] UPDATE Success",3);
			//****************************************************************************************************

			
			
			
			
			//=====================================================================================================
			// 2010.01.04
			// 권오창
			//
			// DMYDR032, DMYDR033, DMYDR034 => 차량스케줄 삭제 및 차량POINT Clear
			// 기존에 만들어져 있는 delCarSchNCarPointForDist() 호출
			//=====================================================================================================
			// 레코드생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szSTL_NO);

			// 재료번호로 저장품 조회(운송일자, 운송순번)
			nRet = ydStockDao.getYdStock(recPara, rsResult, 0);
			if(nRet < 0){
				szMsg = "[외판슬라브반품(DMYDR032)] 저장품 조회 오류 nRet[" + nRet + "] STL_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
			} else if(nRet == 0){
				szMsg = "[외판슬라브반품(DMYDR032)] 저장품 조회 건수가 없음 nRet[" + nRet + "] STL_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);							
			} else {
				szMsg = "[외판슬라브반품(DMYDR032)] 저장품 조회 성공  nRet[" + nRet + "] STL_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);							
			
				rsResult.first();
				recGetVal = rsResult.getRecord();
					
				szTRANS_ORD_DATE  = ydDaoUtils.paraRecChkNull(recGetVal, "TRANS_ORD_DATE");
				szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(recGetVal, "TRANS_ORD_SEQNO");
					
				if(!szTRANS_ORD_DATE.equals("") && !szTRANS_ORD_SEQNO.equals("")){
					// 레코드생성
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("TRANS_ORD_DATE" , szTRANS_ORD_DATE);
					recPara.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
					
					// 차량스케줄 삭제 및 차량 포인트 클리어
					szMsg = "[외판슬라브반품(DMYDR032)] 차량스케줄삭제 및 차량Point Clear 시작 - TRANS_ORD_DATE(" + szTRANS_ORD_DATE + ") TRANS_ORD_SEQNO(" + szTRANS_ORD_SEQNO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//PIDEV_S :병행가동용:PI_YD
					recPara.setField("PI_YD",    	"S");							
					String szRtnMsg = YdCommonUtils.delCarSchNCarPointForDist(recPara, szMethodName);
					
					szMsg = "[외판슬라브반품(DMYDR032)] 차량스케줄삭제 및 차량Point Clear 완료 - " + szRtnMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else {
					szMsg = "[외판슬라브반품(DMYDR032)] 운송일자 혹은 순번의 값이 공백. TRANS_ORD_DATE(" + szTRANS_ORD_DATE + ") TRANS_ORD_SEQNO(" + szTRANS_ORD_SEQNO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
				}
			}
			
			
			
			
			//======================================================
			// 2009.08.31 권오창
			// 저장품제원 : 연주슬라브L2 로 송신(YDY1L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY1L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			ydDelegate.sendMsg(recResult);

		}catch(Exception e){
			szMsg = "[외판슬라브반품]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[procOutplSlabRetngds_외판슬라브반품]" + szMsg);
		} // end of try-catch
		
		szMsg ="외판슬라브반품 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procOutplSlabRetngds()
	

	/**
	 *      [A] 오퍼레이션명 : 구입슬라브등록실적 (QMYDJ001)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procBuySlabRegWr(JDTORecord msgRecord)throws JDTOException  {
		
		
		String szMsg ="";
		String szMethodName="procBuySlabRegWr";
		
		

		String szRcvTcCode=ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode==null){
			szMsg =szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return ;
		}
		if(bDebugFlag){
			szMsg ="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		
		//
		//
		//
		//
		//	toDo Something...
		//
		//
		//
		//
		//

		
		szMsg ="구입슬라브등록실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	}// end of procBuySlabRegWr()
	
	
	
	/**
	 *      [A] 나머지 항목 수집 - 연주전단실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public int getYdEtcItem(JDTORecord inRecord,JDTORecord outRecord) throws JDTOException {
		String szMethodName 	= "getYdEtcItem";
		String szMsg 			= "";
		String szOperationName  = "연주전단실적(나머지 항목 수집)";

		// 재료외형구분
		String szSTL_APPEAR_GP 	= "";
		// 주문여재구분
		String szORD_YEOJAE_GP 	= "";
		// 재료진도코드
		String szSTL_PROG_CD 	= "";
		// 슬라브지시행선코드
		String szSLAB_WO_RT_CD 	= "";
		// HCR구분
		String szHCR_GP 		= "";
		// SCARFING여부
		String szSCARFING_YN 	= "";
		// 조업공장구분
		String szPTOP_PLNT_GP 	= "";
		// 야드재료품목
		String szYD_MTL_ITEM 	= "";
		// 야드목표행선구분
		String szYD_AIM_RT_GP 	= "";
		// 야드목표야드구분
		String szYD_AIM_YD_GP 	= "";
		// 야드목표동구분
		String szYD_AIM_BAY_GP 	= "";
		
		
		String szSlabWoRtCd_sub = "";

		int nRet                = 0;

		try{			
			szSTL_APPEAR_GP 		= ydDaoUtils.paraRecChkNull(inRecord, "STL_APPEAR_GP");
			szORD_YEOJAE_GP			= ydDaoUtils.paraRecChkNull(inRecord, "ORD_YEOJAE_GP");
			szSTL_PROG_CD 			= ydDaoUtils.paraRecChkNull(inRecord, "STL_PROG_CD");
			szSLAB_WO_RT_CD 		= ydDaoUtils.paraRecChkNull(inRecord, "SLAB_WO_RT_CD");
			szSlabWoRtCd_sub 		= szSLAB_WO_RT_CD.substring(0, 1);
			szHCR_GP 				= ydDaoUtils.paraRecChkNull(inRecord, "HCR_GP");
			szSCARFING_YN 			= ydDaoUtils.paraRecChkNull(inRecord, "SCARFING_YN");
			szPTOP_PLNT_GP 			= ydDaoUtils.paraRecChkNull(inRecord, "PTOP_PLNT_GP");
			
			
			if(szSTL_APPEAR_GP.equals("B")&& szSlabWoRtCd_sub.equals("H")){
				szYD_MTL_ITEM  	= "BH";
			}			
			else if(szSTL_APPEAR_GP.equals("B")&& szSlabWoRtCd_sub.equals("P")){
				szYD_MTL_ITEM  	= "BP";
			}else if(szSLAB_WO_RT_CD.equals("MS")){
				szYD_MTL_ITEM  	= stock.YD_ITEM_SLAB_GDS;
			}

			//=================================================================================
			// 재표외형구분이 [B]:: [주편]일 경우
			//=================================================================================
			if(szSTL_APPEAR_GP.equals("B")){

				//===========================
				// 주문여재구분 [1]:: 
				//===========================
				if(szORD_YEOJAE_GP.equals("1")){

					//===========================
					// 진도코드 [C]:: 작업대기
					//===========================
					if(szSTL_PROG_CD.equals("C")){

						//======================
						// 슬라브지시행선코드 [HC]:: 
						//======================
						if(szSLAB_WO_RT_CD.equals("HC") && !szHCR_GP.equals("C")){

							szYD_AIM_RT_GP  = "C2"; // [작업대기-C열연압연] :: [C2]
							szYD_AIM_YD_GP  = "A";
							szYD_AIM_BAY_GP = "D";
						}
						//======================
						// 슬라브지시행선코드 [HB]:: 
						//======================
						if(szSLAB_WO_RT_CD.equals("HB") && !szHCR_GP.equals("C")){

							szYD_AIM_RT_GP  = "C1"; 
							szYD_AIM_YD_GP  = "2";
						}
					}
					//===========================
					// 진도코드 [B]:: 지시대기
					//===========================
					else if(szSTL_PROG_CD.equals("B")){

						//======================
						// 슬라브지시행선코드 [HC]:: 
						//======================
						if(szSLAB_WO_RT_CD.equals("HC")){
							szYD_AIM_YD_GP  = "A";

							// HCR구분 [H] 
							if(szHCR_GP.equals("H")){
								szYD_AIM_RT_GP  = "B3"; // [지시대기-C열연HCR] :: [B3]
								szYD_AIM_BAY_GP = "A";
							}
							else{
								szYD_AIM_RT_GP  = "B4"; // [지시대기-C열연CCR] :: [B4]
								szYD_AIM_BAY_GP = "B";
							}
						}
						//======================
						// 슬라브지시행선코드 [HB]:: 
						//======================
						if(szSLAB_WO_RT_CD.equals("HB")){
							szYD_AIM_YD_GP  = "2";

							// HCR구분 [H] 
							if(!szHCR_GP.equals("C")){
								szYD_AIM_RT_GP  = "B1"; // [지시대기-HCR지시대기] :: [B1]
								szYD_AIM_BAY_GP = "A";
							}
							else{
								if(szSCARFING_YN.equals("Y")){
									szYD_AIM_RT_GP  = "A1"; // 
								}
								else{
									szYD_AIM_RT_GP  = "B2"; //
								}
							}
						}
					}

					//===========================
					// 진도코드 [A]:: 정정작업대기
					//===========================
					else if(szSTL_PROG_CD.equals("A")){
						szYD_AIM_YD_GP 	= "A";
						
						//======================
						// 슬라브지시행선코드 [PA]:: 
						//======================
						if(szSLAB_WO_RT_CD.equals("PA")){
							if(szHCR_GP.equals("C")){
								szYD_AIM_RT_GP  = "A4"; // [정정작업대기-A후판정정CCR] :: [A4]
								if(szSCARFING_YN.equals("Y")){
									szYD_AIM_RT_GP  = "A3"; // [정정작업대기-A후판스카핑CCR] :: [A3]
								}
							}else{
								szYD_AIM_RT_GP  = "A9"; // [정정작업대기-A후판정정HCR] :: [A9]
							}
							
						}

						//======================
						// 슬라브지시행선코드 [HC]:: 
						//======================
						else if(szSLAB_WO_RT_CD.equals("HC")){
							if(szHCR_GP.equals("C")){
								if(szSCARFING_YN.equals("Y")){
									szYD_AIM_RT_GP  = "A2"; // [정정작업대기-C열연CCR스카핑]   :: [A2]
								}else{
									szYD_AIM_RT_GP  = "A7"; // [정정작업대기-C열연CCRNon스카핑]:: [A7]
								}
							}
							else if(szHCR_GP.equals("H")){
								szYD_AIM_RT_GP  = "A8"; // [정정작업대기-C열연HCR스카핑] :: [A8]
							}
						}

						
						//======================
						// 슬라브지시행선코드 [HB]:: 
						//======================
						else if(szSLAB_WO_RT_CD.equals("HB")){
// 09.06.09 추가							
							if(szHCR_GP.equals("C")){
								szYD_AIM_RT_GP  = "A5"; 	// [정정작업대기-B열연CCR Non스카핑] :: [A5]
								if(szSCARFING_YN.equals("Y")){
									szYD_AIM_RT_GP  = "A3"; // [정정작업대기-B열연CCR스카핑] :: [A1]
								}
							}
							else if(szHCR_GP.equals("H")){
								szYD_AIM_RT_GP  = "A6"; 	// [정정작업대기-B열연HCR] :: [A6]
							}
						}
					}

					//===========================
					// 진도코드 [E]:: 재공이송작업대기
					//===========================
					else if(szSTL_PROG_CD.equals("E")){
						//======================
						// 슬라브지시행선코드 [HB]:: 
						//======================
						if(szSLAB_WO_RT_CD.equals("HB")){

							szYD_AIM_RT_GP  = "E3";		 // [B열연NON스카핑] :: [E3]
							szYD_AIM_YD_GP 	= "A";
							szYD_AIM_BAY_GP = "D";

							if(szSCARFING_YN.equals("Y")){
								szYD_AIM_RT_GP  = "E2"; // [B열연스카핑] :: [E2]
								szYD_AIM_YD_GP 	= "2";
								szYD_AIM_BAY_GP = "E";
							}
							if(szHCR_GP.equals("H")){
								szYD_AIM_RT_GP  = "E1"; // [B열연HCR] :: [E1]
								szYD_AIM_YD_GP 	= "2";
								szYD_AIM_BAY_GP = "A";
							}
						}
					}
					if(szSLAB_WO_RT_CD.equals("MS")){
						if(szSCARFING_YN.equals("Y")){
							szYD_AIM_RT_GP  = "AA"; // 
							szYD_AIM_YD_GP 	= "A";
							
						}else{
							szYD_AIM_RT_GP  = "H1"; // 
							szYD_AIM_YD_GP 	= "A";
							
						}
					}
					
				}
				//===========================
				// 주문여재구분 [2]:: 09.06.09 추가
				//                 진도코드에 상관없이 행선코드에 의한 SET
				//===========================
				else if(szORD_YEOJAE_GP.equals("2")){
					szYD_AIM_YD_GP 	= "A";
					szYD_AIM_BAY_GP = "C";
					// 슬라브지시행선코드 [HB]
					if(szSLAB_WO_RT_CD.equals("HB")){
						szYD_MTL_ITEM	= "BH";
						szYD_AIM_RT_GP  = "Y1";		 // [재공충당대기-B열연압연] :: [Y1]
						
					}
					// 슬라브지시행선코드 [HC]
					else if(szSLAB_WO_RT_CD.equals("HC")){
						szYD_MTL_ITEM	= "BH";
						szYD_AIM_RT_GP  = "Y2";		 // [재공충당대기-C열연압연] :: [Y2]
						
					}
					// 슬라브지시행선코드 [PA]
					else if(szSLAB_WO_RT_CD.equals("PA")){
						szYD_MTL_ITEM	= "BP";
						szYD_AIM_RT_GP  = "Y3";		 // [재공충당대기-A후판주편정정] :: [Y3]
						if(szSCARFING_YN.equals("Y")){
							szYD_AIM_RT_GP  = "Y7"; // [B열연스카핑] :: [E2]
							szYD_AIM_YD_GP 	= "A";
						}
					}
					else if (szSLAB_WO_RT_CD.equals("MS")){
						szYD_MTL_ITEM	= stock.YD_ITEM_SLAB_GDS;
						szYD_AIM_RT_GP  = "H1"; // 
						szYD_AIM_YD_GP 	= "A";
						if(szSCARFING_YN.equals("Y")){
							szYD_AIM_RT_GP  = "AC"; // 
							szYD_AIM_YD_GP 	= "A";
						}
					}
				}
			}

			// 공통항목 편집 한 것(inRecord) + 야드항목(outRecord) = outRecord
//			outRecord.addRecord(inRecord);
	
			ydUtils.putLog(szSessionName, szMethodName, "Before setRecord", YdConstant.DEBUG);
			outRecord.setRecord(inRecord);
			outRecord.setField("YD_MTL_ITEM"  , szYD_MTL_ITEM);		//야드재료품목
			outRecord.setField("YD_AIM_RT_GP" , szYD_AIM_RT_GP);	//야드목표행선구분
			outRecord.setField("YD_AIM_YD_GP" , szYD_AIM_YD_GP);	//야드목표야드구분
			outRecord.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);   //야드목표동구분
			outRecord.setField("YD_MTL_STAT"  , "2");	
			ydUtils.displayRecord(szOperationName, outRecord);
		} catch(Exception e) {

			szMsg ="Error:"+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}
		return 0;
	}
	/**
	 *      [A] 나머지 항목 수집2 - SCARFING실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public int getYdEtcItem2(JDTORecord inRecord,JDTORecord outRec) throws JDTOException {
		String szMethodName 	= "getYdEtcItem2";
		String szMsg 			= "";
		
		// 재료외형구분
		String szSTL_APPEAR_GP 	= "";
		// 주문여재구분
		String szORD_YEOJAE_GP 	= "";
		// 재료진도코드
		String szSTL_PROG_CD 	= "";
		// 슬라브지시행선코드
		String szSLAB_WO_RT_CD 	= "";
		// HCR구분
		String szHCR_GP 		= "";
		// SCARFING여부
		String szSCARFING_YN 	= "";
		// 조업공장구분
		String szPTOP_PLNT_GP 	= "";
		// 야드재료품목
		String szYD_MTL_ITEM 	= "";
		// 야드목표행선구분
		String szYD_AIM_RT_GP 	= "";
		// 야드목표야드구분
		String szYD_AIM_YD_GP 	= "";
		// 야드목표동구분
		String szYD_AIM_BAY_GP 	= "";
		// 스카핑 완료유무
		String szSCARFING_DONE_YN= "";
		// 압연지시유무
		String szMILL_WO_EXN	= "";
		String szSlabWoRtCd_sub = "";

		int nRet                = 0;
		
		try{
			
			szSTL_APPEAR_GP 		= ydDaoUtils.paraRecChkNull(inRecord, "STL_APPEAR_GP");
			szORD_YEOJAE_GP			= ydDaoUtils.paraRecChkNull(inRecord, "ORD_YEOJAE_GP");
			szSTL_PROG_CD 			= ydDaoUtils.paraRecChkNull(inRecord, "STL_PROG_CD");
			szSLAB_WO_RT_CD 		= ydDaoUtils.paraRecChkNull(inRecord, "SLAB_WO_RT_CD");
			szSlabWoRtCd_sub 		= szSLAB_WO_RT_CD.substring(0, 1);
			szHCR_GP 				= ydDaoUtils.paraRecChkNull(inRecord, "HCR_GP");
			szSCARFING_YN 			= ydDaoUtils.paraRecChkNull(inRecord, "SCARFING_YN");
			szPTOP_PLNT_GP 			= ydDaoUtils.paraRecChkNull(inRecord, "PTOP_PLNT_GP");
			szMILL_WO_EXN			= ydDaoUtils.paraRecChkNull(inRecord, "MILL_WO_EXN");
			szSCARFING_DONE_YN		= ydDaoUtils.paraRecChkNull(inRecord, "SCARFING_DONE_YN");
			szSCARFING_YN			= ydDaoUtils.paraRecChkNull(inRecord, "SCARFING_YN");

			if(szSTL_APPEAR_GP.equals("B")&& szSlabWoRtCd_sub.equals("H")){
				szYD_MTL_ITEM  	= "BH";
			}else if(szSTL_APPEAR_GP.equals("B")&& szSlabWoRtCd_sub.equals("P")){
				szYD_MTL_ITEM  	= "BP";
			}else if(szSTL_APPEAR_GP.equals("C")&& szSlabWoRtCd_sub.equals("H")){
				szYD_MTL_ITEM  	= "SH";
			}else if(szSTL_APPEAR_GP.equals("C")&& szSlabWoRtCd_sub.equals("P")){
				szYD_MTL_ITEM  	= "SP";
			}else if(szSLAB_WO_RT_CD.equals("MS")){
				szYD_MTL_ITEM  	= stock.YD_ITEM_SLAB_GDS;
			}
			
			//=================================================================================
			// 재표외형구분이 [B]:: [주편] 또는  [C]:: [슬라브]
			//=================================================================================
			if(szSTL_APPEAR_GP.equals("B") || szSTL_APPEAR_GP.equals("C")){
				if(szORD_YEOJAE_GP.equals("1")){
					//========================
					// 재료진도코드 [A]::
					//========================
					if(szSTL_PROG_CD.equals("A")){
						//======================
						// 슬라브지시행선코드 [PA]:: 
						//======================
						if(szSLAB_WO_RT_CD.equals("PA")){

							if(szSCARFING_YN.equals("Y")){
								szYD_AIM_RT_GP  = "A3"; // [정정작업대기-A후판스카핑] :: [A3]
								szYD_AIM_YD_GP 	= "A";
								szYD_AIM_BAY_GP = "C";
							}
							if(szHCR_GP.equals("H")){
								szYD_AIM_RT_GP  = "A4"; // [정정작업대기-A후판정정] :: [A4]
								szYD_AIM_YD_GP 	= "A";
								szYD_AIM_BAY_GP = "A";
							}
							if(szSCARFING_DONE_YN.equals("Y")){
								szYD_AIM_RT_GP  = "A4"; // [정정작업대기-A후판정정] :: [A4]
								szYD_AIM_YD_GP 	= "A";
								szYD_AIM_BAY_GP = "A";
							}
						}

						//======================
						// 슬라브지시행선코드 [HC]:: 
						//======================
						else if(szSLAB_WO_RT_CD.equals("HC")){
							if(szHCR_GP.equals("C")){

								if(szSCARFING_YN.equals("Y")){
									szYD_AIM_RT_GP  = "A2"; // [정정작업대기-C열연CCR스카핑]   :: [A2]
									szYD_AIM_YD_GP 	= "A";
									szYD_AIM_BAY_GP = "C";
								}else{
									szYD_AIM_RT_GP  = "B4"; // [작업지시대기-C열연CCRNon스카핑]:: [B4]
									szYD_AIM_YD_GP 	= "A";
									szYD_AIM_BAY_GP = "D";
								}
							}
							else if(szHCR_GP.equals("H")){
								szYD_AIM_RT_GP  = "A8"; // [정정작업대기-C열연HCR스카핑] :: [A8]
								szYD_AIM_YD_GP 	= "A";
								szYD_AIM_BAY_GP = "D";
							}
						}


						//======================
						// 슬라브지시행선코드 [HB]:: 
						//======================
						else if(szSLAB_WO_RT_CD.equals("HB")){

							if(szHCR_GP.equals("C")){
								szYD_AIM_RT_GP  = "A5"; 	// [정정작업대기-B열연CCR Non스카핑] :: [A5]
								szYD_AIM_YD_GP 	= "2";
								szYD_AIM_BAY_GP = "D";

								if(szSCARFING_YN.equals("Y")){
									szYD_AIM_RT_GP  = "A1"; // [정정작업대기-B열연CCR스카핑] :: [A1]
									szYD_AIM_YD_GP 	= "2";
									szYD_AIM_BAY_GP = "D";
								}
							}
							else if(szHCR_GP.equals("H")){
								szYD_AIM_RT_GP  = "A6"; 	// [정정작업대기-B열연HCR] :: [A6]
								szYD_AIM_YD_GP 	= "A";
								szYD_AIM_BAY_GP = "D";
							}
						}
					}
					
				}else{
					szYD_AIM_YD_GP 	= "A";
					szYD_AIM_BAY_GP = "C";
					// 슬라브지시행선코드 [HB]
					if(szSLAB_WO_RT_CD.equals("HB")){
						szYD_MTL_ITEM	= "BH";
						szYD_AIM_RT_GP  = "Y1";		 // [재공충당대기-B열연압연] :: [Y1]
						
					}
					// 슬라브지시행선코드 [HC]
					else if(szSLAB_WO_RT_CD.equals("HC")){
						szYD_MTL_ITEM	= "BH";
						szYD_AIM_RT_GP  = "Y2";		 // [재공충당대기-C열연압연] :: [Y2]
						
					}
					// 슬라브지시행선코드 [PA]
					else if(szSLAB_WO_RT_CD.equals("PA")){
						szYD_MTL_ITEM	= "BP";
						szYD_AIM_RT_GP  = "Y3";		 // [재공충당대기-A후판주편정정] :: [Y3]
						
					}
				}
			}
			if(szSLAB_WO_RT_CD.equals("MS")){
				if(szORD_YEOJAE_GP.equals("1")){
					
					szYD_AIM_RT_GP  = "AA"; // [정정작업대기-A후판정정] :: [A4]
					szYD_AIM_YD_GP 	= ydConstant.YD_GP_C_SLAB_YARD;
					szYD_AIM_BAY_GP = "C";
					if(szSCARFING_DONE_YN.equals("Y")){
						szYD_AIM_RT_GP  = "H1"; // [정정작업대기-A후판정정] :: [A4]
						szYD_AIM_YD_GP 	= ydConstant.YD_GP_C_SLAB_YARD;
						szYD_AIM_BAY_GP = "C";
					}
				}
			}

			
		    // 공통항목 편집 한 것(inRecord) + 야드항목(outRecord) = outRecord
			outRec.addRecord(inRecord);
			outRec.setField("YD_MTL_ITEM"  , szYD_MTL_ITEM);	 // 야드재료품목
			outRec.setField("YD_AIM_RT_GP" , szYD_AIM_RT_GP);	 // 야드목표행선구분
			outRec.setField("YD_AIM_YD_GP" , szYD_AIM_YD_GP);	 // 야드목표야드구분
			outRec.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);   // 야드목표동구분

		} catch(Exception e) {

			szMsg ="Error:"+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}
		return 0;
	}
	

	
	
	
	/**
	 * 슬라브보류실적 (QMYDJ002) [권오창 2009.10.19]
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procSlabHoldWr(JDTORecord inRecord)throws JDTOException  {
		YdStockDao ydStockDao 	     = new YdStockDao();
		YdCodeMapping ydCodeMapping  = new YdCodeMapping();
		
		JDTORecord recPara      = null;
		JDTORecord outRecTemp   = null;
		JDTORecord recResult 	= null;
		
		JDTORecord recEditRec		    = null;
		JDTORecord recGetVal		    = null;
		JDTORecordSet rsGetMslabComm 	= null;
		JDTORecordSet rsGetSlabComm 	= null;
		JDTORecordSet rsOut 	        = null;
		
		String szMethodName 	  = "procSlabHoldWr";
		String szMsg 			  = "";
		String szSTL_NO           = "";
		String szRECORD_PROG_STAT = "";
		int nRet                  = 0;
		

		// 전문받아서 szRcvTcCode에 대입
		String szRcvTcCode = ydUtils.getTcCode(inRecord);
		if(szRcvTcCode == null){
			szMsg = " TC Code Error : ["+szRcvTcCode+"] ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		if(bDebugFlag){
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		// 수신한 전문내용 
		ydUtils.displayRecord(szMethodName, inRecord);

		try{
			//=============================================================
			// 권오창
			// 2009.11.05
			//
			// Log 테이블 등록 
			// 야드는 X로 일단 두었음 수정해야됨
			//=============================================================
			szMsg = "[품질관리] 슬라브보류실적 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			
			
			
			
			recPara    = JDTORecordFactory.getInstance().create();
			recEditRec = JDTORecordFactory.getInstance().create();
			outRecTemp = JDTORecordFactory.getInstance().create();
			rsOut 	   = JDTORecordFactory.getInstance().createRecordSet("");
			
			// 수신받은 전문에서 재료번호를 추출
			szSTL_NO   = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");

			//=============================================================================================
			// 주편공통 조회 (GP : 6 )
			//=============================================================================================
			rsGetMslabComm = JDTORecordFactory.getInstance().createRecordSet("");
			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("MSLAB_NO", szSTL_NO);
			nRet = ydStockDao.getYdStock(recPara, rsGetMslabComm, 6);
			if(nRet < 0){
				szMsg = "[슬라브보류실적 (QMYDJ002)] 주편공통 테이블 조회 파라미터 에러 nRet(" + nRet + ") MSLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(nRet == 0){
				// 주편에 없을 때 슬라브 테이블은 읽을 필요가 없다...? 일단 주석
				
				szMsg = "[슬라브보류실적 (QMYDJ002)] 주편공통테이블 조회 데이터가 없음 nRet(" + nRet + ") MSLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}

			// 주편 레코드 가져옴
			rsGetMslabComm.first();
			recGetVal = rsGetMslabComm.getRecord();
			
			szRECORD_PROG_STAT = ydDaoUtils.paraRecChkNull(recGetVal, "RECORD_PROG_STAT");
			if(szRECORD_PROG_STAT.equals("3")){
				//=========================================================================================================
				// 슬라브공통 조회 (GP : 2)
				//=========================================================================================================
				rsGetSlabComm = JDTORecordFactory.getInstance().createRecordSet("");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("SLAB_NO", szSTL_NO);
				nRet = ydStockDao.getYdStock(recPara, rsGetSlabComm, 2);
				if(nRet < 0){
					szMsg = "[슬라브보류실적 (QMYDJ002)] 슬라브공통 테이블 조회 파라미터 에러 nRet(" + nRet + ") SLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				} else if(nRet == 0){
					szMsg = "[슬라브보류실적 (QMYDJ002)] 슬라브공통 테이블 조회 데이터가 없음 nRet(" + nRet + ") SLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}	
				
				// 슬라브 레코드 가져옴
				rsGetSlabComm.first();
				recGetVal = rsGetSlabComm.getRecord();
				
				// 슬라브공통에서 읽어온 레코드편집
				// CUST_CD 이값은 슬라브공통에 조회시에 없음...
				nRet = this.edtSlabYdstock(recGetVal, recEditRec);
			} else {
				// 주편공통에서 읽어온 레코드편집
				nRet = this.edtMslabToYdstock(recGetVal, recEditRec);
			}
			
			// 항목 편집이 실패일 때			
			if(nRet < 0){
				szMsg = "[슬라브보류실적 (QMYDJ002) 항목편집1] Error :: [" + nRet + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}	

			
			nRet = stock.SetYD_MTL_ITEM_SLAB(recEditRec);							
			
			if(nRet < 0){
				szMsg = "[슬라브보류실적 (QMYDJ002) 항목편집2] Error :: [" + nRet + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			nRet = this.getYdEtcItem(recResult, recEditRec);
			
			//=============================================================================================
			// 권오창
			// 코드 매핑값 호출 
			//
			//     * 앞에서 코드매핑값에 대해 어떤처리를 하든지 다시 엎어침... 
			//       나중에 앞에서 처리하는 부분 삭제해야 됨            
			//
			//     * 코드매핑은 수신받은 재료번호로 처리
			//       저장품 업데이트 레코드 : recEditRec
			//=============================================================================================
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
			
			
			
			
			
			stock.setYdStkLocTpCd(recEditRec);
			
			
			
			
			
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n========[편집된 항목  표시]========\n", 4);	
			ydUtils.displayRecord(szMethodName, recEditRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===============================\n", 4);
	
			nRet = ydStockDao.getYdStock(recEditRec, rsOut, 0);
			if(nRet <0){
				// 조회 ERROR
				szMsg = "YD_STOCK[저장품] SELECT Error :: [" + nRet + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}else if(nRet > 0){
				// 저장품 UPDATE
				recEditRec.setField("MODIFIER", "QMYDJ002");
				nRet = ydStockDao.updYdStock(recEditRec, 0);
				if(nRet < 0){
					szMsg = "YD_STOCK[저장품] UPDATE Error :: [" + nRet + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}

				szMsg = recEditRec.getFieldString("STL_NO") +" :: YD_STOCK[저장품] UPDATE Success  " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				// 저장품 INSERT
				recEditRec.setField("REGISTER", "QMYDJ002"); 
				nRet = ydStockDao.insYdStock(recEditRec);
				if(nRet < 0){
					szMsg = "YD_STOCK[저장품] INSERT Error :: [" + nRet + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				
				szMsg = recEditRec.getFieldString("STL_NO") +" :: YD_STOCK[저장품] INSERT Success  " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			//========================================================
			// 슬라브보류실적 시 L2로 저장품제원 전문 전송
			//========================================================
			recResult  = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY1L002");
			recResult.setField("YD_INFO_SYNC_CD", "A");							                //1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recEditRec, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , YdConstant.YD_GP_C_SLAB_YARD);		      	    //C연주슬라브야드
	    	ydDelegate.sendMsg(recResult);
		    	
	    	szMsg ="<procSlabHoldWr> 슬라브보류실적 처리 시 저장품 제원 야드L2 [YDY1L002]로 전송";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "[슬라브보류실적] Exception Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[procSlabHoldWr_슬라브보류실적]" + szMsg);
		}
		szMsg = "슬라브보류실적 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} //end of procSlabHoldWr()
	

	
	
	
	
	
	/**
	 * 슬라브 진행 변경 (QMYDJ003) [이영근 2010.01.06]
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procSlabScarSTLNo(JDTORecord inRecord)throws JDTOException  {
		YdStockDao ydStockDao 	     = new YdStockDao();
		YdCodeMapping ydCodeMapping  = new YdCodeMapping();
		
		JDTORecord recPara      = null;
		JDTORecord outRecTemp   = null;
		JDTORecord recResult 	= null;
		
		JDTORecord recEditRec		    = null;
		JDTORecord recGetVal		    = null;
		JDTORecordSet rsGetMslabComm 	= null;
		JDTORecordSet rsGetSlabComm 	= null;
		JDTORecordSet rsOut 	        = null;
		
		String szMethodName 	  = "procSlabScarSTLNo";
		String szMsg 			  = "";
		String szSTL_NO           = "";
		String szRECORD_PROG_STAT = "";
		int nRet                  = 0;
		

		// 전문받아서 szRcvTcCode에 대입
		String szRcvTcCode = ydUtils.getTcCode(inRecord);
		if(szRcvTcCode == null){
			szMsg = " TC Code Error : ["+szRcvTcCode+"] ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		if(bDebugFlag){
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		// 수신한 전문내용 
		ydUtils.displayRecord(szMethodName, inRecord);

		try{
			//=============================================================
			// 이영근
			// 2010.01.06
			//
			// Log 테이블 등록 
			// 야드는 X로 일단 두었음 수정해야됨
			//=============================================================
			szMsg = "[품질관리] Scarfing 대상재변경 수신";
			ydUtils.putLogMsg("X", "yd_monitorA", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			
			
			
			
			recPara    = JDTORecordFactory.getInstance().create();
			recEditRec = JDTORecordFactory.getInstance().create();
			outRecTemp = JDTORecordFactory.getInstance().create();
			rsOut 	   = JDTORecordFactory.getInstance().createRecordSet("");
			
			// 수신받은 전문에서 재료번호를 추출
			szSTL_NO   = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");

			//=============================================================================================
			// 주편공통 조회 (GP : 6 )
			//=============================================================================================
			rsGetMslabComm = JDTORecordFactory.getInstance().createRecordSet("");
			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("MSLAB_NO", szSTL_NO);
			nRet = ydStockDao.getYdStock(recPara, rsGetMslabComm, 6);
			if(nRet < 0){
				szMsg = "[Scarfing 대상재변경 (QMYDJ003)] 주편공통 테이블 조회 파라미터 에러 nRet(" + nRet + ") MSLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(nRet == 0){
				// 주편에 없을 때 슬라브 테이블은 읽을 필요가 없다...? 일단 주석
				
				szMsg = "[Scarfing 대상재변경 (QMYDJ003)] 주편공통테이블 조회 데이터가 없음 nRet(" + nRet + ") MSLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}

			// 주편 레코드 가져옴
			rsGetMslabComm.first();
			recGetVal = rsGetMslabComm.getRecord();
			
			String szTempYD_GP = "";
			
			szRECORD_PROG_STAT = ydDaoUtils.paraRecChkNull(recGetVal, "RECORD_PROG_STAT");
			if(szRECORD_PROG_STAT.equals("3")){
				//=========================================================================================================
				// 슬라브공통 조회 (GP : 2)
				//=========================================================================================================
				rsGetSlabComm = JDTORecordFactory.getInstance().createRecordSet("");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("SLAB_NO", szSTL_NO);
				nRet = ydStockDao.getYdStock(recPara, rsGetSlabComm, 2);
				if(nRet < 0){
					szMsg = "[Scarfing 대상재변경 (QMYDJ003)] 슬라브공통 테이블 조회 파라미터 에러 nRet(" + nRet + ") SLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				} else if(nRet == 0){
					szMsg = "[Scarfing 대상재변경 (QMYDJ003)] 슬라브공통 테이블 조회 데이터가 없음 nRet(" + nRet + ") SLAB_NO(" + ydDaoUtils.paraRecChkNull(inRecord, "STL_NO") + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}	
				
				// 슬라브 레코드 가져옴
				rsGetSlabComm.first();
				recGetVal = rsGetSlabComm.getRecord();
				
				// 슬라브공통에서 읽어온 레코드편집
				// CUST_CD 이값은 슬라브공통에 조회시에 없음...
				nRet = this.edtSlabYdstock(recGetVal, recEditRec);
								
				szTempYD_GP =  ydDaoUtils.paraRecChkNull(recEditRec, "YD_GP");
				
			} else {
				// 주편공통에서 읽어온 레코드편집
				nRet = this.edtMslabToYdstock(recGetVal, recEditRec);
				szTempYD_GP =  ydDaoUtils.paraRecChkNull(recEditRec, "YD_GP");
			}
			
			// 항목 편집이 실패일 때			
			if(nRet < 0){
				szMsg = "[Scarfing 대상재변경 (QMYDJ003) 항목편집1] Error :: [" + nRet + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}	

			
			nRet = stock.SetYD_MTL_ITEM_SLAB(recEditRec);							
			
			if(nRet < 0){
				szMsg = "[Scarfing 대상재변경 (QMYDJ003) 항목편집2] Error :: [" + nRet + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			nRet = this.getYdEtcItem(recResult, recEditRec);
			
			//=============================================================================================
			// 권오창
			// 코드 매핑값 호출 
			//
			//     * 앞에서 코드매핑값에 대해 어떤처리를 하든지 다시 엎어침... 
			//       나중에 앞에서 처리하는 부분 삭제해야 됨            
			//
			//     * 코드매핑은 수신받은 재료번호로 처리
			//       저장품 업데이트 레코드 : recEditRec
			//=============================================================================================
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
			
			
			
			
			
			stock.setYdStkLocTpCd(recEditRec);
			
			
			
			
			
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n========[편집된 항목  표시]========\n", 4);	
			ydUtils.displayRecord(szMethodName, recEditRec);	
			ydUtils.putLog(szSessionName, szMethodName, "\n===============================\n", 4);
	
			nRet = ydStockDao.getYdStock(recEditRec, rsOut, 0);
			if(nRet <0){
				// 조회 ERROR
				szMsg = "YD_STOCK[저장품] SELECT Error :: [" + nRet + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}else if(nRet > 0){
				// 저장품 UPDATE
				recEditRec.setField("MODIFIER", "QMYDJ003");
				nRet = ydStockDao.updYdStock(recEditRec, 0);
				if(nRet < 0){
					szMsg = "YD_STOCK[저장품] UPDATE Error :: [" + nRet + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}

				szMsg = recEditRec.getFieldString("STL_NO") +" :: YD_STOCK[저장품] UPDATE Success  " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				// 저장품 INSERT
				recEditRec.setField("REGISTER", "QMYDJ003"); 
				nRet = ydStockDao.insYdStock(recEditRec);
				if(nRet < 0){
					szMsg = "YD_STOCK[저장품] INSERT Error :: [" + nRet + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				
				szMsg = recEditRec.getFieldString("STL_NO") +" :: YD_STOCK[저장품] INSERT Success  " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			if(!szTempYD_GP.equals("S")||!szTempYD_GP.equals("D"))
			{
			
				//========================================================
				// Scarfing 대상재변경 시 L2로 저장품제원 전문 전송
				//========================================================
				recResult  = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YDY1L002");
				recResult.setField("YD_INFO_SYNC_CD", "A");							                //1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
				recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recEditRec, "STL_NO"));
				recResult.setField("YD_STK_COL_GP"  , YdConstant.YD_GP_C_SLAB_YARD);		      	    //C연주슬라브야드
		    	ydDelegate.sendMsg(recResult);
			    	
		    	szMsg ="<procSlabScarSTLNo> Scarfing 대상재변경 처리 시 저장품 제원 야드L2 [YDY1L002]로 전송";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			}
		}catch(Exception e){
			szMsg = "[Scarfing 대상재변경] Exception Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[procSlabScarSTLNo_Scarfing 대상재변경]" + szMsg);
		}
		szMsg = "Scarfing 대상재변경 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} //end of procSlabScarSTLNo()
		
	
	
	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                     일관제철소정보관리시스템-야드관리
	//             					 저장품관리
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
	



	
	
	

//-----------------------------------------------------------------------------
}// end of class

