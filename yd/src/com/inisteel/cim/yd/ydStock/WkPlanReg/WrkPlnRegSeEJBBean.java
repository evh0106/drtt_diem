/**
 * @(#)YdToLocDcsnUtil.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2011/04/12
 * 
 * @description		이클래스는 YD에서 사용되는 TO위치결정하는 클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2011/04/12                    최초 등록
 * V1.01  2013/05/06   조병기       조병기      procPlMillOrdCmmt 메소드에 finally 부분 추가
 *                 
 */

package com.inisteel.cim.yd.ydStock.WkPlanReg;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydPrepSchDao.YdPrepSchDao;
import com.inisteel.cim.yd.common.dao.ydPrepMtlDao.YdPrepMtlDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.rule.GetBreRule2;
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.ydStock.StockSpecReg.StockSpecRegSeEJBBean;
import com.inisteel.cim.yd.ydStock.StockSpecReg.SlabSpecRegSeEJBBean;
import com.inisteel.cim.yd.common.util.YdCodeMapping;
import com.inisteel.cim.yd.common.util.YdConstant;


/**
 * 작업예정등록공통 Session EJB
 *
 * @ejb.bean name="WrkPlnRegSeEJB" jndi-name="WrkPlnRegSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class WrkPlnRegSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName          = getClass().getName();
	
	private YdDelegate ydDelegate         = new YdDelegate();
	private YdUtils ydUtils               = new YdUtils();
	private YdDaoUtils ydDaoUtils         = new YdDaoUtils();
	private StockSpecRegSeEJBBean stock   = new StockSpecRegSeEJBBean();
	private SlabSpecRegSeEJBBean slabSpec = new SlabSpecRegSeEJBBean();

	// [DEBUG] message flag
	private boolean bDebugFlag            = true;
	
	private String[] rVal                 = new String[1];
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	 /**
	 * 연주전단지시확정 (CTYDJ011)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCcFsOrdCmmt(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		YdStockDao ydStockDao 					= new YdStockDao();
		YdCodeMapping ydCodeMapping             = new YdCodeMapping();

		
		// 레코드 선언
		JDTORecordSet rsGetStockTpsWoHeat    	= null;
		JDTORecordSet rsGetFromToPoint 		    = null;
		JDTORecordSet rsGetYdStock		    	= null;
		JDTORecord recFromToPoint		    	= null;
		JDTORecord recMslabWoItm  		    	= null;
		JDTORecord recEditRec			    	= null;
		JDTORecord recResult			    	= null;
		JDTORecord outRecTemp                   = null;

		
		// 변수 선언
		String szMethodName                     = "procCcFsOrdCmmt";
		String szMsg                            = "";
		String szOperationName                  = "연주전단지시확정";
		String szSTL_NO                         = "";
		int nRet                                = 0;
		int intRtnVal                           = 0;

		
		// 전문받아서 szRcvTcCode에 저장
		String szRcvTcCode = ydUtils.getTcCode(inRecord);
		if(szRcvTcCode == null){
			szMsg = "[ERROR] " + szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
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
			szMsg = "[생산통제] 연주전단지시확정 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			
			
			
			
			ydUtils.displayRecord(szOperationName, inRecord);

			
			// 레코드 생성
			rsGetStockTpsWoHeat = JDTORecordFactory.getInstance().createRecordSet("");
			rsGetFromToPoint 	= JDTORecordFactory.getInstance().createRecordSet("");
			rsGetYdStock		= JDTORecordFactory.getInstance().createRecordSet("");		
			recEditRec          = JDTORecordFactory.getInstance().create();
			
            //=============================================================================================			
			// 출강지시INDEX 조회  Dao 호출 - [getCTFTPMSWOIDX_FROMTO intGp :10]
            //=============================================================================================
			recEditRec.setField("CT_PLN_WO_MC_NO", ydDaoUtils.paraRecChkNull(inRecord,"CC_MC_CD"));
			intRtnVal = ydStockDao.getYdStock(recEditRec, rsGetFromToPoint, 10);
			if(intRtnVal == 0){
				//From-ToPoint가 존재 하지 않을경우 
				szMsg = "getCTFTPMSWOIDX_FROMTO [출강지시INDEX] Error :: DO NOT EXIST [FromPoint,ToPoint]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			}else if(intRtnVal < 0){
				szMsg = "getCTFTPMSWOIDX_FROMTO [출강지시INDEX] Error :: PARAMETER ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}	
			
			
			// 구해진 FromPoint와 ToPoint를 recFromToPoint에  담기 
			rsGetFromToPoint.first();
			recFromToPoint = JDTORecordFactory.getInstance().create(); 
			recFromToPoint = rsGetFromToPoint.getRecord();

			
			// Debug Msg
			ydUtils.putLog(szSessionName, szMethodName, "\n========[FromPoint,ToPoint 표시]=======\n", YdConstant.DEBUG);
			szMsg = "[장입지시 FromPoint] :" + recFromToPoint.getFieldString("CHG_WO_FR_PNT");
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			szMsg = "[장입지시 ToPoint  ] :" + recFromToPoint.getFieldString("CHG_WO_TO_PNT");
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "\n=======================================\n", YdConstant.DEBUG);

            //=============================================================================================
			// 주편작업지시DAO- [getCTFMSLABWO_HEATNO intGp :12]
            //=============================================================================================
			intRtnVal = ydStockDao.getYdStock(recFromToPoint, rsGetStockTpsWoHeat, 12);
			if(intRtnVal == 0){
				szMsg= "getCTFMSLABWO_HEATNO [주편작업지시DAO] Error :: DO NOT EXIST ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			}else if(intRtnVal < 0){
				szMsg = "getCTFMSLABWO_HEATNO [주편작업지시DAO] Error :: PARAMETER ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}	

			
			// 레코드의 커서를 처음으로
			rsGetStockTpsWoHeat.first();
			recMslabWoItm = JDTORecordFactory.getInstance().create();

			
			for(int i=1; i<=rsGetStockTpsWoHeat.size(); i++){

				// 메모리 할당
				recEditRec 	  = JDTORecordFactory.getInstance().create();
				recMslabWoItm = rsGetStockTpsWoHeat.getRecord();

				
                //=============================================================================================
				// 항목편집
                //=============================================================================================
				intRtnVal = this.edtMslabWo(recMslabWoItm, recEditRec);
				/*
				intRtnVal = stock.SetYD_STK_LOT_TP(recEditRec);
				if( intRtnVal < 0){
					szMsg = "[연주전단지시 항목편집 산적 LOT TYPE] Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}	
				*/
//				intRtnVal = stock.setYD_STK_LOT_CD(recEditRec);
//				// 항목 편집이 실패일 때
//				if(intRtnVal < 0){
//					szMsg = "[연주전단지시 항목편집 산적 LOT CODE] Error :: [" + intRtnVal + "]";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return ;
//				}				
				
				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n========[편집된 항목 표시]========\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recEditRec);	
				ydUtils.putLog(szSessionName, szMethodName, "\n===============================\n", YdConstant.DEBUG);

				
                //=============================================================================================
				// 저장품 DAO- [SELECT, INSERT, UPDATE]
                //=============================================================================================
				intRtnVal = ydStockDao.getYdStock(recEditRec, rsGetYdStock, 0);
				if(intRtnVal < 0){
					szMsg = "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				} else if(intRtnVal == 0){
					szMsg = "YD_STOCK[저장품] INSERT :: ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recEditRec.setField("REGISTER", "CTYDJ011");
					intRtnVal = ydStockDao.insYdStock(recEditRec);
					if(intRtnVal < 0){
						szMsg = "YD_STOCK[저장품] INSERT Error :: [" + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
					szMsg = i + "[" + recEditRec.getFieldString("STL_NO") + "] :: YD_STOCK[연주전단지시확정 수신]INSERT Success";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				else{

					szMsg = "YD_STOCK[저장품] UPDATE :: [2] YD_STOCK UPDATE ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					recEditRec.setField("MODIFIER", "CTYDJ011");
					intRtnVal= ydStockDao.updYdStock(recEditRec, 0);
					if(intRtnVal <= 0){
						szMsg = "YD_STOCK[저장품] UPDATE Error :: [" + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
					
					szMsg = i + "[" + recEditRec.getFieldString("STL_NO") + "] :: YD_STOCK[연주전단지시확정 수신]UPDATE Success";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}

				
				//=============================================================================================
				// 2010.01.29
				// 이영근
				// 공통으로 사용하는 Code Mapping Function 호출 (T/C CODE, 재료번호)
				// 주편 또는 슬라브를 Read -> 필요한 항목 추출 -> 목표행선 정보 추출 -> 저장품 Update -> 야드 Level-2 제원정보 송신
				//=============================================================================================
				nRet = ydCodeMapping.getMappingCommonField(szRcvTcCode, ydDaoUtils.paraRecChkNull(recEditRec, "STL_NO"));
				if(nRet <= 0){
					szMsg = "[nRet " + nRet + "] TC_CODE(" + szRcvTcCode + ") 실패 STL_NO( " + ydDaoUtils.paraRecChkNull(recEditRec, "STL_NO") + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
				}else{
					szMsg = "[nRet " + nRet + "] TC_CODE(" + szRcvTcCode + ") 성공 STL_NO(" + ydDaoUtils.paraRecChkNull(recEditRec, "STL_NO") + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
				}
				
				

				
				
				
				rsGetStockTpsWoHeat.next();
			} 

		}catch(Exception e){

			szMsg = "[연주전단지시확정] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[procCcFsOrdCmmt_연주전단지시확정]" + szMsg);
		}

		szMsg = "연주전단지시확정  등록("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} //end of procCcFsOrdCmmt();
	
	/**
	 * [연주전단지시] 항목 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtMslabWo(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {
		
		String szYD_MTL_ITEM  ="";
		String szYD_AIM_YD_GP ="";
		String szYD_AIM_RT_GP ="";
		String szYD_AIM_BAY_GP="";
		
		try{
			String szSLAB_WO_RT_CD = ydDaoUtils.paraRecChkNull(inRecord,"REPR_MATL_RT_GP"); // 대표소재행선구분
			String szHCR_GP = ydDaoUtils.paraRecChkNull(inRecord,"CT_HCR_GP"); 
			String szORD_YEOJAE_GP= ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP");
			String szSCARFING_SIGN = ydDaoUtils.paraRecChkNull(inRecord,"SCARFING_SIGN");
			if(szSLAB_WO_RT_CD.equals("PA")){
				szYD_MTL_ITEM ="BP";
				if(szORD_YEOJAE_GP.equals("1")){
					if(szHCR_GP.equals("C")){
						if(!szSCARFING_SIGN.equals("")){
							szYD_AIM_RT_GP = "A3";
							szYD_AIM_YD_GP = "A";
							
								
						}else{
							szYD_AIM_RT_GP = "A4";
							szYD_AIM_YD_GP = "A";
							
						}
					}else{
						szYD_AIM_RT_GP = "A9";
						szYD_AIM_YD_GP = "A";
						szYD_AIM_BAY_GP= "C"; 
					}
				}else{
					if(!szSCARFING_SIGN.equals("")){
						szYD_AIM_RT_GP = "Y7";
						szYD_AIM_YD_GP = "A";
						szYD_AIM_BAY_GP= ""; 
							
					}else{
						szYD_AIM_RT_GP = "Y6";
						szYD_AIM_YD_GP = "A";
						szYD_AIM_BAY_GP= "";
					}
				}
			}else if(szSLAB_WO_RT_CD.equals("HB")){
				szYD_MTL_ITEM ="BH";
				if(szORD_YEOJAE_GP.equals("1")){
					if(szHCR_GP.equals("C")){
						if(!szSCARFING_SIGN.equals("")){
							szYD_AIM_RT_GP = "A1";
							szYD_AIM_YD_GP = "2";
							szYD_AIM_BAY_GP= "C"; 
								
						}else{
							szYD_AIM_RT_GP = "B2";
							szYD_AIM_YD_GP = "2";
							szYD_AIM_BAY_GP= "C"; 
						}
					}else{
						szYD_AIM_RT_GP = "B1";
						szYD_AIM_YD_GP = "2";
						szYD_AIM_BAY_GP= "D"; 
					}
				}else{
					if(!szSCARFING_SIGN.equals("")){
						szYD_AIM_RT_GP = "Y1";
						szYD_AIM_YD_GP = "2";
						szYD_AIM_BAY_GP= ""; 
							
					}else{
						szYD_AIM_RT_GP = "Y2";
						szYD_AIM_YD_GP = "2";
						szYD_AIM_BAY_GP= "";
					}
				}
			}else if(szSLAB_WO_RT_CD.equals("HC")){
				szYD_MTL_ITEM ="BH";
				if(szORD_YEOJAE_GP.equals("1")){
					if(szHCR_GP.equals("C")){
						if(!szSCARFING_SIGN.equals("")){
							szYD_AIM_RT_GP = "A2";
							szYD_AIM_YD_GP = "A";
							szYD_AIM_BAY_GP= "C"; 
								
						}else{
							szYD_AIM_RT_GP = "B4";
							szYD_AIM_YD_GP = "A";
							szYD_AIM_BAY_GP= "B"; 
						}
					}else{
						szYD_AIM_RT_GP = "B3";
						szYD_AIM_YD_GP = "A";
						szYD_AIM_BAY_GP= "A"; 
					}
				}else{
					if(!szSCARFING_SIGN.equals("")){
						szYD_AIM_RT_GP = "Y3";
						szYD_AIM_YD_GP = "A";
						szYD_AIM_BAY_GP= ""; 
							
					}else{
						szYD_AIM_RT_GP = "Y4";
						szYD_AIM_YD_GP = "A";
						szYD_AIM_BAY_GP= "";
					}
				}
			}
			

			recEditRec.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"PLN_MSLAB_NO")); 			// 재료번호	- 예정주편번호

			recEditRec.setField("YD_STK_LOT_CD", 		ydDaoUtils.paraRecChkNull(inRecord,"STACK_LOT_NO"));			// 야드 산적LOT코드 - 슬라브산적LOT코드	
			recEditRec.setField("PTOP_PLNT_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"PTOP_PLNT_GP")); 
			recEditRec.setField("YD_MTL_W", 			ydDaoUtils.paraRecChkNull(inRecord,"CT_WO_MSLAB_WO_W"));		// 폭
			recEditRec.setField("YD_MTL_L", 			ydDaoUtils.paraRecChkNull(inRecord,"CT_WO_MSLAB_WO_L"));		// 길이
			recEditRec.setField("YD_MTL_T", 			ydDaoUtils.paraRecChkNull(inRecord,"CT_WO_MSLAB_WO_T"));		// 두께
			recEditRec.setField("YD_MTL_WT", 			ydDaoUtils.paraRecChkNull(inRecord,"CT_WO_MSLAB_WO_WT"));		// 중량
			recEditRec.setField("ORD_YEOJAE_GP", 		szORD_YEOJAE_GP);			// 주문여재구분
			recEditRec.setField("ORD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"REPRESENT_ORD_NO"));		// 주문번호
			recEditRec.setField("ORD_DTL", 				ydDaoUtils.paraRecChkNull(inRecord,"REPRESENT_ORD_DTL"));		// 주문행번
			recEditRec.setField("SLAB_WO_RT_CD", 	    szSLAB_WO_RT_CD);			// 대표소재행선구분	
			recEditRec.setField("DEMANDER_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"DEMANDER_CD"));				
			recEditRec.setField("HCR_GP", 				szHCR_GP);				// HCR구분
			recEditRec.setField("CC_CCM_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"CT_PLN_WO_MC_NO"));
			recEditRec.setField("YD_MTL_STAT", 			"1");	// 야드재료상태-생산예정
			recEditRec.setField("YD_MTL_ITEM", 			szYD_MTL_ITEM);	// 
			recEditRec.setField("YD_AIM_RT_GP", 		szYD_AIM_RT_GP);	//
			recEditRec.setField("YD_AIM_YD_GP", 		szYD_AIM_YD_GP);	//
			recEditRec.setField("YD_AIM_BAY_GP", 		szYD_AIM_BAY_GP);	//
		
			
		} catch(Exception e){

			return -1;
		}
		return 1;

	} //end of edtMslabWo()
	
	/**
	 * 후판압연지시확정 (CTYDJ031)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlMillOrdCmmt(JDTORecord inRecord)throws JDTOException  {
		
		YdStockDao ydStockDao 					= new YdStockDao();
		YdCodeMapping ydCodeMapping 			= new YdCodeMapping();
		
		JDTORecordSet rsGetStockSlabComm 	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsDelStockSlabComm 	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsGetYdStock 			= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recEditRec 				= null;
		JDTORecord recSlabComm 				= JDTORecordFactory.getInstance().create();
		JDTORecord recInTemp 				= null;
		JDTORecord recResult 				= null;
		JDTORecord outRecTemp 				= null;
		
		String szMethodName 		= "procPlMillOrdCmmt";
		String szMsg 				= "";
		String szOperationName 		= "후판압연지시확정";
		String szSTL_NO 			= "";
		int intRtnVal 				= 0;
		int nRet 					= 0;
	
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg = "[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try{
			//=============================================================
			// Log 테이블 등록 
			// 야드는 X로 일단 두었음 수정해야됨
			//=============================================================
			szMsg = "[생산통제] 후판압연지시확정 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			String sModGp	   = ydDaoUtils.paraRecChkNull(inRecord, "MOD_GP");
			
        	/*
        	 * I : 생산통제 압연지시
        	 * D : 생산통제 압연지시 취소
        	 */
        	if("D".equals(sModGp)){
        		
        		String sSlabNo 		= ydDaoUtils.paraRecChkNull(inRecord, "SLAB_NO");
        		String sPlnSlabNo 	= ydDaoUtils.paraRecChkNull(inRecord, "PLAN_SLAB_NO");
        		
        		// 후판결번실적 호출.
        		if(!"".equals(sSlabNo)) sPlnSlabNo = sSlabNo;
        		inRecord.setField("STL_NO", sPlnSlabNo);
        		this.procAPlMillOrdMissnoWr(inRecord);	
        		
        		return;
        	}
        	
        	/***************************************************************/
			/***************기존 작업지시정보 초기화****************************/
			/***************************************************************/
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("MODIFIER"			,"CTYDJ031");
			recPara.setField("SLAB_WO_RT_CD"	,"PA");
			recPara.setField("YD_AIM_RT_GP"		,"C3");
			recPara.setField("PTOP_PLNT_GP"		,"PA");
			
			szMsg= "[후판압연지시 트랜잭션 분리 시작]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//트렌젝션 분리 작업 
    		EJBConnector ejbConn = new EJBConnector("default", "WrkPlnRegSeEJB", this);
    		ejbConn.trx("makePaRollInfoSend", new Class[] { JDTORecord.class }, new Object[] { recPara});
    		
    		szMsg= "[후판압연지시 트랜잭션 분리 끝]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			A후판슬라브야드L2 저장품 제원 전송  - YDY1L002
	         * 업무기준 Desc : 1. A후판슬라브야드L2 처리 후저장품 제원 일괄 전송
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("JMS_TC_CD"      , "YDY3LXX2");                                        // TC-CODE
			recInTemp.setField("YD_GP"          , "D");                                               // 야드구분
			recInTemp.setField("YD_INFO_SYNC_CD", "P");  											  // 야드동기화 코드 - P : A후판가열로보급
			ydDelegate.sendMsg(recInTemp);
	        szMsg = "[후판압연지시확정]A후판슬라브야드L2 크레인저장품제원  전송 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			A후판슬라브야드L2 크레인작업계획 전송  - YDY3L003
	         * 업무기준 Desc : 1. A후판슬라브야드L2 처리 후 크레인 작업계획 전송
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("JMS_TC_CD"      , "YDY3L003");                                        // TC-CODE
			recInTemp.setField("YD_GP"          , "D");                                               // 야드구분
			recInTemp.setField("YD_INFO_SYNC_CD", "P");  											  // 야드동기화 코드 - P : A후판가열로보급
	        ydDelegate.sendMsg(recInTemp);
	        szMsg = "[후판압연지시확정]A후판슬라브야드L2 크레인작업계획 전송 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

		}catch(Exception e){

			szMsg = "[후판압연지시확정]Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException("[procPlMillOrdCmmt_후판압연지시확정]" + szMsg);
		}finally{
			//-------------------------------------------------------------------------------------------------start
			// 레코드 생성
			JDTORecord recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ031");
			// 전문 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss"));
			
			if("D".equals(ydDaoUtils.paraRecChkNull(inRecord, "MOD_GP"))){
				//D : 후판압연작업지시 취소
				recOutPara.setField("MOD_GP",ydDaoUtils.paraRecChkNull(inRecord, "MOD_GP"));
				//SLAB_NO
				recOutPara.setField("SLAB_NO",ydDaoUtils.paraRecChkNull(inRecord, "SLAB_NO"));
				//PLAN_SLAB_NO
				recOutPara.setField("PLAN_SLAB_NO",ydDaoUtils.paraRecChkNull(inRecord, "PLAN_SLAB_NO"));
				//PL_MPL_NO
				recOutPara.setField("PL_MPL_NO",ydDaoUtils.paraRecChkNull(inRecord, "PL_MPL_NO"));
				
			} else {
				//I : 후판압연작업지시
				recOutPara.setField("MOD_GP",ydDaoUtils.paraRecChkNull(inRecord, "MOD_GP"));
				//PTOP_PLNT_GP
				recOutPara.setField("PTOP_PLNT_GP",ydDaoUtils.paraRecChkNull(inRecord, "PTOP_PLNT_GP"));
				//CHG_WO_FR_PNT
				recOutPara.setField("CHG_WO_FR_PNT",ydDaoUtils.paraRecChkNull(inRecord, "CHG_WO_FR_PNT"));
				//CHG_WO_TO_PNT
				recOutPara.setField("CHG_WO_TO_PNT",ydDaoUtils.paraRecChkNull(inRecord, "CHG_WO_TO_PNT"));
			}
			//CT_JMS_MSG 
			recOutPara.setField("CT_JMS_MSG",ydDaoUtils.paraRecChkNull(inRecord, "CT_JMS_MSG"));
			
			// JMS 전문 송신
			ydDelegate.sendMsg(recOutPara);
			//-------------------------------------------------------------------------------------------------end
		}
		
		szMsg = "후판압연지시등록("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of procPlMillOrdCmmt()
	
	/**
	 * 오퍼레이션명 : 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public int makePaRollInfoSend(JDTORecord recPara)throws JDTOException{
		String szMsg = "";
		String szMethodName = "";
		
		int intRtnVal = 0;
		
		YdStockDao ydStockDao = new YdStockDao();
		try{
			/* 2010.12.25   윤재광                                                                       */
			/* 압연지시확정시 일괄 업데이트 - 속도부하때문.. */
			intRtnVal = ydStockDao.updYdStock_CTYDJ031_DEL(recPara);
			intRtnVal = ydStockDao.updYdStock_CTYDJ031(recPara);
			
		}catch(Exception e){

			szMsg = "[1후판압연지시확정]Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[makePaRollInfoSend]" + szMsg);
		}
		return 1;
	}
	/**
	 * A후판 압연지시결번실적 (PRYDJ001)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procAPlMillOrdMissnoWr (JDTORecord inRecord)throws JDTOException  {
		procAPlMillOrdMissnoWr(inRecord,"Y");
	}
	public void procAPlMillOrdMissnoWr (JDTORecord inRecord,String sL2SendYn)throws JDTOException  {
		
		// DAO 및 UTIL객체 생성
		YdStockDao ydStockDao                   = new YdStockDao();
		YdCodeMapping ydCodeMapping             = new YdCodeMapping();
		
		// 레코드 선언
		JDTORecordSet rsGetYdStock              = null;
		JDTORecord recPara                      = null;
		JDTORecord outRecTemp                   = null;
		JDTORecordSet rsGetMslabComm            = null;
		JDTORecordSet rsGetSlabComm             = null;
		JDTORecord recGetVal                    = null;
		JDTORecord recEditRec                   = null;
		
		// 변수 선언
		String szMethodName                     = "procAPlMillOrdMissnoWr";
		String szMsg                            = "";
		String szOperationName                  = "A후판 압연지시결번실적";
		String szSTL_NO                         = "";
		String szRECORD_PROG_STAT               = "";
		boolean bReadSlab                       = false;
		int intRtnVal                           = 0;
		int nRet                                = 0;
		
		
		// 전문받아서 szRcvTcCode에 저장
		String szRcvTcCode = ydUtils.getTcCode(inRecord);
		if(szRcvTcCode == null){
			// 수신한 전문이 null이라면 error
			szMsg = "[ERROR] " + szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[후판조업] 압연지시결번실적 수신";
			ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			// 레코드 생성
			rsGetYdStock    = JDTORecordFactory.getInstance().createRecordSet("");
			recEditRec      = JDTORecordFactory.getInstance().create();
			
			// 수신받은 전문에서 재료번호 추출
			szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("SLAB_NO", szSTL_NO);
			rsGetSlabComm = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydStockDao.getYdStock(recPara, rsGetSlabComm, 2);
			if(intRtnVal < 0){
				szMsg = "[A후판압연지시결번실적수신 (PRYDJ001)] 슬라브공통 테이블 조회 파라미터 에러 nRet(" + intRtnVal + ") SLAB_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(intRtnVal == 0){
				szMsg = "[A후판압연지시결번실적수신 (PRYDJ001)] 슬라브공통 테이블 조회 데이터가 없음 nRet(" + intRtnVal + ") SLAB_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			} else {
				szMsg = "[A후판압연지시결번실적수신 (PRYDJ001)] 슬라브공통 테이블 조회 성공 SLAB_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				// 슬라브 레코드 가져옴
				rsGetSlabComm.first();
				recGetVal = rsGetSlabComm.getRecord();	
			}
			
			// 슬라브공통에서 읽어온 레코드편집
			intRtnVal = slabSpec.edtSlabYdstock(recGetVal, recEditRec);				
			
			//CLEAR 할 항목들 ""로 셋필드를 해 준 후  ydStockUpdate를 호출해서 갱신한다.
			recEditRec.setField("REFUR_CHG_PLN_SERNO"	,"");
			recEditRec.setField("REFUR_CHG_LOT_NO"		,"");
			recEditRec.setField("ROLL_UNIT_NAME"		,"");
			recEditRec.setField("ROLL_UNIT_GP"			,"");
			recEditRec.setField("MODIFIER"				,"PRYDJ001");
			
			//후판압연지시 취소 시 메시지 초기화
			recEditRec.setField("SNDBK_GP_ETC"			,"");
			recEditRec.setField("SNDBK_REGISTER"		,"");
			recEditRec.setField("SNDBK_GP"				,"");
			
            //=============================================================================================
			// 2009.09.15
			// 권오창
			// 코드 매핑값 호출 
			//
			//     * 앞에서 코드매핑값에 대해 어떤처리를 하든지 다시 엎어침... 
			//       나중에 앞에서 아래코드에 대해 처리하는 부분 삭제해야 됨      
			//
			//     * 수신받은 재료번호를 가지고 코드매핑 처리
			//
			//     * 업데이트할 레코드 : recEditRec
            //=============================================================================================
			outRecTemp = JDTORecordFactory.getInstance().create();
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
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
			
			stock.setYdStkLocTpCd(recEditRec);
			
			//=========================================================================================================
			// 저장품 업데이트
			//=========================================================================================================
			intRtnVal = ydStockDao.updYdStock(recEditRec, 0);
		
			szMsg = "YD_STOCK[A후판압연지시결번실적수신] UPDATE 결과 :: [" + intRtnVal + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			if("Y".equals(sL2SendYn))
			{
				//======================================================
				// 2009.08.31 권오창
				// 저장품제원 : 후판슬라브L2로 송신(YDY3L002)
				//            5:지정저장품
				//======================================================
				JDTORecord recResult = null;
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YDY3L002");
				recResult.setField("YD_INFO_SYNC_CD", "5");        
				recResult.setField("STL_NO"         , szSTL_NO);
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				ydDelegate.sendMsg(recResult);
				
				//+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
				// YDY3L003	크레인작업계획 
				// 내려간 작업계획을  결번으로 인해서 새로 내려 보내야 됨
				// (장입BED 권하실적 수신 전 압연 대상제)
				/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 			A후판슬라브야드L2 크레인작업계획 전송  - YDY3L003
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				JDTORecord recInTemp = null;
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("JMS_TC_CD"      , "YDY3L003");                                        // TC-CODE
				recInTemp.setField("YD_GP"          , "D");                                               // 야드구분
				recInTemp.setField("YD_INFO_SYNC_CD", "P");  											  // 야드동기화 코드 - P : A후판가열로보급
		        ydDelegate.sendMsg(recInTemp);
		        szMsg = "[후판압연지시확정]A후판슬라브야드L2 크레인작업계획 전송 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				//+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
				
				szMsg = "[A후판압연지시결번실적수신 (PRYDJ001)] 후판슬라브L2로 송신(YDY3L002)";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		}catch(Exception e){ 

			szMsg = "[A후판압연지시결번실적수신]Exception Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException("[procAPlMillOrdMissnoWr_A후판압연지시결번실적수신]" + szMsg);
		} // end of try-catch

		szMsg = "A후판압연지시결번등록수신 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procAPlMillOrdMissnoWr()
	
	
	
	/**
	 * C열연압연지시확정 (CTYDJ033)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCHrMillOrdCmmt(JDTORecord inRecord)throws JDTOException  {
		
		YdStockDao ydStockDao 					= new YdStockDao();
		YdCodeMapping ydCodeMapping             = new YdCodeMapping();
		
		JDTORecordSet rsGetStockSlabComm 		= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsDelStockSlabComm 		= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsGetYdStock 				= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recEditRec 					= null;
		JDTORecord recSlabComm 					= JDTORecordFactory.getInstance().create();
		JDTORecord recInTemp 					= null;
		JDTORecord recResult 					= null;
		JDTORecord outRecTemp                   = null;
		
		// 변수 선언
		String szMethodName 			= "procCHrMillOrdCmmt";
		String szMsg 					= "";
		String szOperationName 			= "C열연압연지시확정";
		String szSTL_NO                 = "";
		String szRECORD_PROG_STAT       = "";
		
		int intRtnVal 					= 0;
		int nRet						= 0;
		
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg = "[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try{
			//=============================================================
			// Log 테이블 등록 
			// 야드는 X로 일단 두었음 수정해야됨
			//=============================================================
			szMsg = "[생산통제] C열연압연지시확정 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			String sModGp	   = ydDaoUtils.paraRecChkNull(inRecord, "MOD_GP");
			/*
        	 * I : 생산통제 압연지시
        	 * D : 생산통제 압연지시 취소
        	 */
        	if("D".equals(sModGp)){
        		
        		String sSlabNo 		= ydDaoUtils.paraRecChkNull(inRecord, "SLAB_NO");
        		String sPlnSlabNo 	= ydDaoUtils.paraRecChkNull(inRecord, "PLAN_SLAB_NO");
        		
        		//열연결번실적 호출 
        		if(!"".equals(sSlabNo)) sPlnSlabNo = sSlabNo;
        		inRecord.setField("STL_NO", sSlabNo);
        		this.procCHrMillOrdMissnoWr(inRecord);	
        		
        		return;
        	}
						
			/***************************************************************/
			/***************기존 작업지시정보 초기화****************************/
			/***************************************************************/
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("MODIFIER"			,"CTYDJ033");
			recPara.setField("SLAB_WO_RT_CD"	,"HC");
			recPara.setField("YD_AIM_RT_GP"		,"C2");
			recPara.setField("PTOP_PLNT_GP"		,"HC");
			
			szMsg= "[C열연 일관지시 트랜잭션 분리  시작]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//트렌젝션 분리 작업 
    		EJBConnector ejbConn = new EJBConnector("default", "WrkPlnRegSeEJB", this);
    		ejbConn.trx("makeHcRollInfoSend", new Class[] { JDTORecord.class }, new Object[] { recPara});
    		
    		szMsg= "[C열연 일관지시 트랜잭션 분리  끝]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			C연주슬라브야드L2 저장품 제원 전송  - YDY1L002
	         * 업무기준 Desc : 1. C열연압연지시확정 처리 후저장품 제원 일괄 전송
	         * 2011.02.16 : 김홍수 과장과 협의 후 송신안함.
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			/*
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("JMS_TC_CD"      , "YDY1LXX2");                                        // TC-CODE
			recInTemp.setField("YD_GP"          , "A");                                               // 야드구분
			recInTemp.setField("YD_INFO_SYNC_CD", "H");  											  // 야드동기화 코드 - H : C열연가열로보급
			ydDelegate.sendMsg(recInTemp);
	        szMsg = "[C열연압연지시확정]C연주슬라브야드L2 크레인저장품제원  전송 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			*/
	        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			C연주슬라브야드L2 크레인작업계획 전송  - YDY1L003
	         * 업무기준 Desc : 1. C열연압연지시확정 처리 후 크레인 작업계획 전송
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("JMS_TC_CD"      , "YDY1L003");                                        // TC-CODE
			recInTemp.setField("YD_GP"          , "A");                                               // 야드구분
			recInTemp.setField("YD_INFO_SYNC_CD", "H");  											  // 야드동기화 코드 - H : C열연가열로보급
	        ydDelegate.sendMsg(recInTemp);
	        szMsg = "[C열연압연지시확정]C연주슬라브야드L2 크레인작업계획 전송 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			
			
		}catch(Exception e){

			szMsg = "[C열연압연지시확정]Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[procCHrMillOrdCmmt_C열연압연지시확정]" + szMsg);
		}
		szMsg = "C열연압연지시확정("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of procCHrMillOrdCmmt()
	
	/**
	 * 오퍼레이션명 : 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public int makeHcRollInfoSend(JDTORecord recPara)throws JDTOException{
		String szMsg = "";
		String szMethodName = "";
		
		int intRtnVal = 0;
		
		YdStockDao ydStockDao = new YdStockDao();
		try{
			/* 2010.12.25   윤재광                                                                       */
			/* 압연지시확정시 일괄 업데이트 - 속도부하때문.. */
			intRtnVal = ydStockDao.updYdStock_CTYDJ033_DEL(recPara);
			intRtnVal = ydStockDao.updYdStock_CTYDJ033(recPara);
			
		}catch(Exception e){

			szMsg = "[C열연압연지시확정]Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[makeHcRollInfoSend]" + szMsg);
		}
		return 1;
	}
	/**
	 * C열연 압연지시결번실적 (HRYDJ001)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCHrMillOrdMissnoWr (JDTORecord inRecord)throws JDTOException  {
		procCHrMillOrdMissnoWr(inRecord,"Y");
	}
	public void procCHrMillOrdMissnoWr (JDTORecord inRecord,String sL2SendYn)throws JDTOException  {
		
		// DAO 및 UTIL객체 생성
		YdStockDao ydStockDao                   = new YdStockDao();
		YdCodeMapping ydCodeMapping             = new YdCodeMapping();
		
		// 레코드 선언
		JDTORecordSet rsGetYdStock              = null;
		JDTORecord recPara                      = null;
		JDTORecord outRecTemp                   = null;
		JDTORecordSet rsGetMslabComm            = null;
		JDTORecordSet rsGetSlabComm             = null;
		JDTORecord recGetVal                    = null;
		JDTORecord recEditRec                   = null;
		
		// 변수 선언
		String szMethodName                     = "procCHrMillOrdMissnoWr";
		String szMsg                            = "";
		String szOperationName                  = "C열연 압연지시결번실적";
		String szSTL_NO                         = "";
		String szRECORD_PROG_STAT               = "";
		boolean bReadSlab                       = false;
		int intRtnVal                           = 0;
		int nRet                                = 0;
		
		
		// 전문받아서 szRcvTcCode에 저장
		String szRcvTcCode = ydUtils.getTcCode(inRecord);
		if(szRcvTcCode == null){
			// 수신한 전문이 null이라면 error
			szMsg = "[ERROR] " + szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[열연조업] 압연지시결번실적 수신";
			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			// 레코드 생성
			rsGetYdStock    = JDTORecordFactory.getInstance().createRecordSet("");
			recEditRec      = JDTORecordFactory.getInstance().create();
			
			// 수신받은 전문에서 재료번호 추출
			szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("SLAB_NO", szSTL_NO);
			rsGetSlabComm = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydStockDao.getYdStock(recPara, rsGetSlabComm, 2);
			if(intRtnVal < 0){
				szMsg = "[C열연압연지시결번실적수신 (HRYDJ001)] 슬라브공통 테이블 조회 파라미터 에러 nRet(" + intRtnVal + ") SLAB_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(intRtnVal == 0){
				szMsg = "[C열연압연지시결번실적수신 (HRYDJ001)] 슬라브공통 테이블 조회 데이터가 없음 nRet(" + intRtnVal + ") SLAB_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			} else {
				szMsg = "[C열연압연지시결번실적수신 (HRYDJ001)] 슬라브공통 테이블 조회 성공 SLAB_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				// 슬라브 레코드 가져옴
				rsGetSlabComm.first();
				recGetVal = rsGetSlabComm.getRecord();	
			}
			
			// 슬라브공통에서 읽어온 레코드편집
			intRtnVal = slabSpec.edtSlabYdstock(recGetVal, recEditRec);				
			
			//CLEAR 할 항목들 ""로 셋필드를 해 준 후  ydStockUpdate를 호출해서 갱신한다.
			recEditRec.setField("REFUR_CHG_PLN_SERNO"	,"");
			recEditRec.setField("REFUR_CHG_LOT_NO"		,"");
			recEditRec.setField("ROLL_UNIT_NAME"		,"");
			recEditRec.setField("ROLL_UNIT_GP"			,"");
			recEditRec.setField("MODIFIER"				,"HRYDJ001");
			
            //=============================================================================================
			// 2009.09.15
			// 권오창
			// 코드 매핑값 호출 
			//
			//     * 앞에서 코드매핑값에 대해 어떤처리를 하든지 다시 엎어침... 
			//       나중에 앞에서 아래코드에 대해 처리하는 부분 삭제해야 됨      
			//
			//     * 수신받은 재료번호를 가지고 코드매핑 처리
			//
			//     * 업데이트할 레코드 : recEditRec
            //=============================================================================================
			outRecTemp = JDTORecordFactory.getInstance().create();
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
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
			
			stock.setYdStkLocTpCd(recEditRec);
			
			//=========================================================================================================
			// 저장품 업데이트
			//=========================================================================================================
			intRtnVal = ydStockDao.updYdStock(recEditRec, 0);
		
			szMsg = "YD_STOCK[C열연압연지시결번실적수신] UPDATE 결과 :: [" + intRtnVal + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			if("Y".equals(sL2SendYn))
			{
				//======================================================
				// 2009.08.31 권오창
				// 저장품제원 : C연주슬라브L2로 송신(YDY1L002)
				//            5:지정저장품
				//======================================================
				JDTORecord recResult = null;
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YDY1L002");
				recResult.setField("YD_INFO_SYNC_CD", "5");        
				recResult.setField("STL_NO"         , szSTL_NO);
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				ydDelegate.sendMsg(recResult);
				
				/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 			C연주슬라브야드L2 크레인작업계획 전송  - YDY1L003
		         * 업무기준 Desc : 1. C열연압연지시확정 처리 후 크레인 작업계획 전송
		         * 기능 추가 : 임춘수
		         * 일자 : 2009.06.25
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				JDTORecord recInTemp = null;
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("JMS_TC_CD"      , "YDY1L003");                                        // TC-CODE
				recInTemp.setField("YD_GP"          , "A");                                               // 야드구분
				recInTemp.setField("YD_INFO_SYNC_CD", "H");  											  // 야드동기화 코드 - H : C열연가열로보급
		        ydDelegate.sendMsg(recInTemp);
		        szMsg = "[C열연압연지시확정]C연주슬라브야드L2 크레인작업계획 전송 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				
				szMsg = "[C열연압연지시결번실적수신 (PRYDJ001)] 후판슬라브L2로 송신(YDY3L002)";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}	
		}catch(Exception e){ 

			szMsg = "[C열연압연지시결번실적수신]Exception Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException("[procAPlMillOrdMissnoWr_C열연압연지시결번실적수신]" + szMsg);
		} // end of try-catch

		szMsg = "C열연압연지시결번등록수신 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procCHrMillOrdMissnoWr
	
	/**생산통제 시스템에서 송신한 관련 항목을 야드 저장품 항목으로 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtCtSlabCommToYdstock(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {
		
		String szMethodName	         = "edtCtSlabCommToYdstock";
		String szMsg                 = "";
		String szSLAB_WO_RT_CD 		 = "";
		String szYD_MTL_ITEM		 = "";
		
		int intRtnVal = 0;
		
		try{
			szSLAB_WO_RT_CD 		= ydDaoUtils.paraRecChkNull(inRecord, "SLAB_WO_RT_CD");
			
			if(szSLAB_WO_RT_CD.equals("PA")){
				
				szYD_MTL_ITEM 	= "SP";
				recEditRec.setField("YD_MTL_ITEM"  , szYD_MTL_ITEM);    // 재료품목
				
			} else if(szSLAB_WO_RT_CD.equals("HB")){
				
				szYD_MTL_ITEM 	= "SH";
				recEditRec.setField("YD_MTL_ITEM"  , szYD_MTL_ITEM);    // 재료품목
				
			} else if(szSLAB_WO_RT_CD.equals("HC")){
				
				szYD_MTL_ITEM 	= "SH";
				recEditRec.setField("YD_MTL_ITEM"  , szYD_MTL_ITEM);    // 재료품목
			}
			
			//===================================================================================================
			// 2010.01.20 
			// 권오창 
			// 
			// 원인 : 이 편집함수에서 편집하는 항목중 CTYDJ031(저장품 13번쿼리)에 조회항목이 없어서 공백으로 업데이트되는 문제찾음
			// 처리 : 
			//     [1] 저장품 13번 쿼리에 추가한 항목      ㅡ.ㅡ 
			//
			//     CURR_PROG_CD      (TB_PT_SLABCOMM)
			//     ITEMNAME_CD       (TB_PT_SLABCOMM)
			//     DEMANDER_CD       (TB_PT_SLABCOMM)
			//     ORD_HCR_GP        (TB_PT_SLABCOMM)
			//     STL_APPEAR_GP     (TB_PT_SLABCOMM)
			//     REHEAT_SLAB_GP    (TB_PT_SLABCOMM)
			//     PTOP_PLNT_GP      (TB_PT_SLABCOMM)
			//     ORD_GP            (TB_CT_M_PLMPLSPEC)
			//     ROLL_UNIT_NAME    (TB_CT_M_PLMPLSPEC)
			//
			//     [2] 저장품87번 쿼리에 추가한 항목
			//  
			//     SLAB_NO   (TB_PT_SLABCOMM)
			//     SLAB_T    (TB_PT_SLABCOMM)
			//     SLAB_W    (TB_PT_SLABCOMM)
			//     SLAB_LEN  (TB_PT_SLABCOMM) 
			//     SLAB_WT   (TB_PT_SLABCOMM) 
			//===================================================================================================
			
			recEditRec.setField("STL_NO"             , ydDaoUtils.paraRecChkNull(inRecord,"SLAB_NO")); 
			recEditRec.setField("STL_PROG_CD"        , ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));		 
			recEditRec.setField("HCR_GP"             , ydDaoUtils.paraRecChkNull(inRecord,"HCR_GP"));		
			recEditRec.setField("SPEC_ABBSYM"        , ydDaoUtils.paraRecChkNull(inRecord,"SPEC_ABBSYM"));
			recEditRec.setField("ROLL_UNIT_NAME"     , ydDaoUtils.paraRecChkNull(inRecord,"ROLL_UNIT_NAME"));				
			recEditRec.setField("STL_APPEAR_GP"      , ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recEditRec.setField("PTOP_PLNT_GP"       , ydDaoUtils.paraRecChkNull(inRecord,"PTOP_PLNT_GP")); 
			recEditRec.setField("ORD_GP"             , ydDaoUtils.paraRecChkNull(inRecord,"ORD_GP")); 
			recEditRec.setField("REHEAT_SLAB_GP"     , ydDaoUtils.paraRecChkNull(inRecord,"REHEAT_SLAB_GP"));
			recEditRec.setField("YD_MTL_T"           , ydDaoUtils.paraRecChkNull(inRecord,"SLAB_T"));            // 쿼리에서 REAL_MEASURE_SLAB_T 값을 ALIAS
			recEditRec.setField("YD_MTL_W"           , ydDaoUtils.paraRecChkNull(inRecord,"SLAB_W"));            // 쿼리에서 REAL_MEASURE_SLAB_W 값을 ALIAS
			recEditRec.setField("YD_MTL_L"           , ydDaoUtils.paraRecChkNull(inRecord,"SLAB_LEN"));          // 쿼리에서 REAL_MEASURE_SLAB_LEN 값을 ALIAS
			recEditRec.setField("YD_MTL_WT"          , ydDaoUtils.paraRecChkNull(inRecord,"SLAB_WT"));           // 쿼리에서 CAL_SLAB_WT 값을 ALIAS
			recEditRec.setField("SLAB_WO_RT_CD"      , ydDaoUtils.paraRecChkNull(inRecord, "SLAB_WO_RT_CD")); 
			recEditRec.setField("REFUR_CHG_LOT_NO"   , ydDaoUtils.paraRecChkNull(inRecord, "REFUR_CHG_LOT_NO"));				
			recEditRec.setField("REFUR_CHG_PLN_SERNO", ydDaoUtils.paraRecChkNull(inRecord, "REFUR_CHG_PLN_SERNO"));			
			recEditRec.setField("ITEMNAME_CD"        , ydDaoUtils.paraRecChkNull(inRecord,"ITEMNAME_CD"));		
			recEditRec.setField("DEMANDER_CD"        , ydDaoUtils.paraRecChkNull(inRecord,"DEMANDER_CD"));		
			recEditRec.setField("ORD_HCR_GP"         , ydDaoUtils.paraRecChkNull(inRecord,"ORD_HCR_GP")); 	
			
		} catch(Exception e){

			szMsg = "[압연지시확정]항목 편집 Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException("[edtCtSlabCommToYdstock_연압연지시항목편집]" + szMsg);
		}

		return 1;
	} //end of edtCtSlabCommToYdstock()
	
	
	/**
	 *      [A] 오퍼레이션명 : C열연 정정작업지시 (HRYDJ005)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCHrShearOrd(JDTORecord msgRecord)throws JDTOException  {
		// 레코드 선언
		JDTORecord recOutRec       = null;
		JDTORecordSet rsResult     = null;    
		JDTORecordSet rsResult_Mtl = null;

		// DAO객체 및 UTIL객체 생성
		YdStockDao ydStockDao                   = new YdStockDao();
		YdUtils ydUtils                         = new YdUtils();

		// 변수 선언
		String szMethodName = "procCHrShearOrd";			
		String szMsg = "";
		String szOperationName = "C열연 정정작업지시";
		String szRcvTcCode = null;
		int intRtnVal = 0;

		szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode == null){
			szMsg = szSessionName+"::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		if(bDebugFlag){
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} 
		
		try{
			//=============================================================
			// 권오창
			// 2009.11.05
			//
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[열연조업] 정정작업지시 수신";
			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			rsResult_Mtl = JDTORecordFactory.getInstance().createRecordSet("");

			ydUtils.displayRecord(szOperationName, msgRecord);
			
			///*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockHrCShear*/
			intRtnVal = ydStockDao.getYdStock(msgRecord, rsResult, 34);
			if(intRtnVal <= 0){
				szMsg = "YD_STOCK[저장품] 정정작업지시를 이미 수신 하였습니다. :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				return ;
			}
			
			szMsg = "YD_STOCK[C열연정정작업지시수신] SELECT Success";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			for(int i=0; i<rsResult.size(); i++){
				// 조회된 항목과 값을 담을 Record 생성
				recOutRec = JDTORecordFactory.getInstance().create();
				recOutRec = rsResult.getRecord(i);
				
				// ===저장품에 Update 할 항목Set==================//
				// 야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal = YdCommonUtils.getYdAimRtGp("C", recOutRec);		
				recOutRec.setField("YD_AIM_RT_GP", rVal[0]);
				recOutRec.setField("STL_PROG_CD", recOutRec.getFieldString("CURR_PROG_CD") );
				recOutRec.setField("MODIFIER", "HRYDJ005");

				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n========[UPDATE될 항목 표시]========\n", 4);	
				ydUtils.displayRecord(szOperationName, recOutRec);	
				ydUtils.putLog(szSessionName, szMethodName, "\n=================================\n", 4);

				intRtnVal = ydStockDao.updYdStock(recOutRec, 0);
				if(intRtnVal< 0){
					szMsg = "YD_STOCK[저장품] UPDATE Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				
				szMsg = "YD_STOCK[C열연정정작업지시수신] UPDATE Success";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			} // end of for
		}catch(Exception e){ 
			szMsg = "[C열연정정작업지시수신] Exception Error::" + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[procCHrShearOrd_C열연정정작업지시수신]" + szMsg);
		} // end of try-catch

		szMsg = "C열연정정작업지시수신 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	}// end of procCHrShearOrd()
	
	
	
	
	/**
	 * C열연 정정지시결번실적 (HRYDJ006)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCHrShearOrdMissnoWr (JDTORecord inRecord)throws JDTOException  {
		// 레코드 선언
		JDTORecordSet rsResult 	   = null;
		JDTORecord recYdStockStlNo = null;
		JDTORecord recPara 		   = null;
		JDTORecord recGetVal 	   = null;

		// DAO객체 및 UTIL객체 생성
		YdWrkbookMtlDao ydWrkbookMtlDao         = new YdWrkbookMtlDao();
		YdStockDao ydStockDao                   = new YdStockDao();
		
		// 변수 선언
		String szMethodName       = "procCHrShearOrdMissnoWr";
		String szMsg              = "";
		String szOperationName    = "C열연 정정지시결번실적";
		String szRcvTcCode        = null;
		String szSTL_NO           = "";
		String szYD_SCH_CD        = "";
		String szYD_CRN_SCH_ID    = "";
		String szYD_STK_COL_GP    = "";
		String szYD_STK_BED_NO    = "";
		String szYD_STK_LYR_NO    = "";
		String szYD_WRK_PROG_STAT = "";
		String szYD_WBOOK_ID      = "";
		String szTEMP_STL_NO      = "";
		int nRet                  = 0;
	
		// 전문받아서 szRcvTcCode에 저장
		szRcvTcCode = ydUtils.getTcCode(inRecord);
		if(szRcvTcCode == null){
			szMsg = "[ERROR] "+szSessionName+"::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try{
			//=============================================================
			// 권오창
			// 2009.11.05
			//
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[열연조업] 정정지시결번실적 수신";
			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			
			
			
			
			ydUtils.displayRecord(szOperationName, inRecord);

			szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");

			// 수신한 재료번호로 저장품 우선 조회
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();		
			recPara.setField("STL_NO", szSTL_NO);
			nRet = ydStockDao.getYdStock(recPara, rsResult, 0);
			if(nRet <= 0){
				if(nRet == 0){
					szMsg = "YD_STOCK[저장품] SELECT Error :: [" + szSTL_NO + "]" + "DO NOT EXIST";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}else{
					szMsg = "YD_STOCK[저장품] SELECT Error :: [" + nRet + "]" + "PARAMETER ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}

			// 조회한 항목들을 재료상태변경이력에 등록 또는 갱신
			szMsg = "YD_STOCK[C열연정정지시결번실적수신] :: [1] YD_MTLSTATMODHIST INSERT ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			rsResult.first();
			recYdStockStlNo = rsResult.getRecord();
			
			szMsg = "YD_STOCK[C열연정정지시결번실적수신] :: [2] YD_STOCK UPDATE ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			//==================================================
			// C열연정정지시결번실적수신
			//--------------------------------------------------
			// 2009.09.07 
			// 권오창
			//==================================================

			//=========================================================================================
			// 결번처리 할 재료번호로 작업예약, 작업예약재료 테이블 조회 (1건)
			//-----------------------------------------------------------------------------------------
			// 재료번호로 작업예약 + 작업예약재료 테이블을 재료번호로 조회하여 결번처리할 재료정보의 다른항목들을 조회해 온다.
			//=========================================================================================
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();		
			recPara.setField("STL_NO", szSTL_NO);
			nRet = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, rsResult, 2);
			if(nRet <= 0){
				szMsg = "작업예약(TB_YD_WRKBOOK) + 작업예약재료(TB_YD_WRKBOOKMTL) 테이블  조회실패 [126][" + nRet + "] STL_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);			
				return ;
			} else {
				szMsg = "작업예약(TB_YD_WRKBOOK) + 작업예약재료(TB_YD_WRKBOOKMTL) 테이블  조회성공 [" + nRet + "] : STL_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);				
			}
			
			rsResult.first();
			recGetVal = rsResult.getRecord();
			
			
			//=========================================================================================
			// 스케쥴코드가 정정보급의 결번처리일 경우 처리
			//-----------------------------------------------------------------------------------------
			// 가져온 스케쥴코드가 정정보급의 스케쥴코드일 경우 크레인스케줄 (DEL_YN), 크레인작업재료 (DEL_YN)
			// 작업예약재료 (DEL_YN), 작업예약 테이블은 삭제판단 (작업예약ID로 작업예약+작업예약재료 조회건수 1건이면 삭제)
			//=========================================================================================
			szYD_SCH_CD     = ydDaoUtils.paraRecChkNull(recGetVal, "YD_SCH_CD");
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_COL_GP");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_NO");
			szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_LYR_NO");
			szTEMP_STL_NO   = ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO");
			szYD_WBOOK_ID   = ydDaoUtils.paraRecChkNull(recGetVal, "YD_WBOOK_ID");
//C증설
			if(
			   szYD_SCH_CD.equals("HBKE01UM") || szYD_SCH_CD.equals("HBKD01UM") || szYD_SCH_CD.equals("HBFE01UM") || 
			   szYD_SCH_CD.equals("HAKE01UM") || szYD_SCH_CD.equals("HAKD01UM") ||  szYD_SCH_CD.equals("HBKD01UM") || 
			   szYD_SCH_CD.equals("HBTC01MM") || szYD_SCH_CD.equals("HBTC02MM") ||

			   szYD_SCH_CD.equals("HCKE01UM") || szYD_SCH_CD.equals("HCKD01UM") || szYD_SCH_CD.equals("HCFE01UM") ||
			   szYD_SCH_CD.equals("HCTC01MM") || szYD_SCH_CD.equals("HCTC02MM") ||  szYD_SCH_CD.equals("HCKD01UM") || 
			    
			   szYD_SCH_CD.equals("HDFE01UM") || szYD_SCH_CD.equals("HDTC01MM") || szYD_SCH_CD.equals("HDTC02MM") ||
			   
			   szYD_SCH_CD.equals("HEDE01UM") || szYD_SCH_CD.equals("HEDD01UM") || 
			   szYD_SCH_CD.equals("HETC01MM") || szYD_SCH_CD.equals("HETC02MM") || 
			   
			   szYD_SCH_CD.equals("HFFE01UM") || szYD_SCH_CD.equals("HFTC01MM") || szYD_SCH_CD.equals("HFTC02MM") ||
			   
			   szYD_SCH_CD.equals("HGFE01UM") || szYD_SCH_CD.equals("HGTC01MM") || szYD_SCH_CD.equals("HGTC02MM") ||
			   
			   szYD_SCH_CD.equals("HHKE01UM") || szYD_SCH_CD.equals("HHKD01UM") ||
			   szYD_SCH_CD.equals("HHTC01MM") || szYD_SCH_CD.equals("HHTC02MM")  
			){
				//===================================================================================
				// 크레인스케쥴, 크레인작업재료 테이블 조회
				//-----------------------------------------------------------------------------------
				// 재료번호와 작업예약ID로 크레인스케줄 + 크레인 작업재료 테이블을 조회하여 작업진행상태와 크레인스케쥴ID
				// 를 가져온다.
				//===================================================================================
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recPara  = JDTORecordFactory.getInstance().create();		
				recPara.setField("STL_NO"     , szTEMP_STL_NO);
				recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
				nRet = SearchCrnSchCrnWrkMtl_WrkbookIdStlNo(recPara, rsResult);
				if(nRet <= 0){
					return ;					
				} 
				
				rsResult.first();
				recGetVal = rsResult.getRecord();
				szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recGetVal, "YD_CRN_SCH_ID");
				szYD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recGetVal, "YD_WRK_PROG_STAT");

				
				//==================================================================================
				// 지시대기 및 권상 전 일 경우 삭제처리
				//==================================================================================
				if(szYD_WRK_PROG_STAT.equals("W") || szYD_WRK_PROG_STAT.equals("1")){
					//==================================================================================
					// 크레인스케쥴(TB_YD_CRNSCH), 크레인 작업재료 (TB_YD_CRNWRKMTL)테이블  삭제처리
					//==================================================================================
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
					recPara.setField("STL_NO"       , szTEMP_STL_NO);
					if((nRet = this.DeleteCrnSchCrnWrkMtl_CrnSchIdStlNo(recPara)) <= 0){
						return ;
					}

					
					//=======================================================================================
					// 작업예약재료 (TB_YD_WRKBOOKMTL), 작업예약 (TB_YD_WRKBOOK) 테이블 삭제처리
					//=======================================================================================					
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
					recPara.setField("STL_NO"     , szTEMP_STL_NO);
					if((nRet = DeleteWrkbookWrkbookMtl_WrkbookIdStlNo(recPara)) <= 0){
						return ;
					}
					
					
					//=======================================================================================
					// 결번처리 재료가 001단 일경우 002단의 좌우측 체크하여 있다면 두개 삭제처리
					//---------------------------------------------------------------------------------------
					// 결번처리할 해당 재료의 단이 001단일 경우 002단의 좌우측에 놓인 재료에 대해서도 크레인스케쥴과  1단일 경우
					// 적치단을 체크해서 002단의 좌우측을 확인하여 크레인스케쥴, 크레인작업재료 삭제처리(DEL_YN)
					// 제일 앞의 BED와 제일뒤의 BED에서는 앞뒤쪽 조회시 실패로 떨어지기 때문에 무시
					//=======================================================================================
					if(szYD_STK_LYR_NO.equals("001")){
						//=======================================================================================
						// 해당 BED의 좌측 보조작업재료 조회 및 삭제
						//=======================================================================================
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_WBOOK_ID"  , szYD_WBOOK_ID);
						recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
						recPara.setField("YD_STK_BED_NO", YdUtils.fillSpZr("" + (Integer.parseInt(szYD_STK_BED_NO)-1), 2, 0));
						recPara.setField("YD_STK_LYR_NO", YdUtils.fillSpZr("" + (Integer.parseInt(szYD_STK_LYR_NO)+1), 3, 0));
						nRet = this.setClearAidWork(recPara);
						if(nRet <= 0){
							szMsg = "해당 결번처리 재료번호의 좌측 002단 재료번호 삭제실패 [" + nRet + "] YD_WBOOK_ID(" + szYD_WBOOK_ID + "] YD_STK_COL_GP(" + szYD_STK_COL_GP + ") YD_STK_BED_NO(" + YdUtils.fillSpZr("" + (Integer.parseInt(szYD_STK_BED_NO)-1), 2, 0) + ") YD_STK_LYR_NO(" + YdUtils.fillSpZr("" + (Integer.parseInt(szYD_STK_LYR_NO)+1), 3, 0) + ")";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);																	
						}

						
						//=======================================================================================
						// 해당 BED의 우측 보조작업재료 조회 및 삭제
						//=======================================================================================
						recPara  = JDTORecordFactory.getInstance().create();		
						recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
						recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
						recPara.setField("YD_STK_LYR_NO", YdUtils.fillSpZr("" + (Integer.parseInt(szYD_STK_LYR_NO)+1), 3, 0));
						nRet = this.setClearAidWork(recPara);
						if(nRet <= 0){
							szMsg = "해당 결번처리 재료번호의 우측 002단 재료번호 삭제실패[" + nRet + "] YD_WBOOK_ID(" + szYD_WBOOK_ID + "] YD_STK_COL_GP(" + szYD_STK_COL_GP + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ") YD_STK_LYR_NO(" + YdUtils.fillSpZr("" + (Integer.parseInt(szYD_STK_LYR_NO)+1), 3, 0) + ")";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);																	
						}						
					}
				} else {
					szMsg = "*** 해당 재료번호(" + szTEMP_STL_NO + ")와 작업예약ID(" + szYD_WBOOK_ID + ")로 조회한 작업진행상태(" + szYD_WRK_PROG_STAT + ")가 지시대기(W) 혹은 권상전(1)인 상태가 아님 ***";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
				}
			} else {
				szMsg = "*** 해당 재료번호(" + szTEMP_STL_NO + ")의 스케쥴코드(" + szYD_SCH_CD + "가 정정보급의 결번처리가 아님 ***";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				// 결번처리가 아닐경우 기존의 저장품 클리어하는 업무 처리
				
				
				//CLEAR 할 항목들 ""로 셋필드를 해 준 후  ydStockUpdate를 호출해서 갱신을 한다.	
				recYdStockStlNo = JDTORecordFactory.getInstance().create();
				
				// ===저장품에 Update 할 항목Set==================//
				// 야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal = YdCommonUtils.getYdAimRtGp("C", recYdStockStlNo);		
				recYdStockStlNo.setField("YD_AIM_RT_GP", rVal[0]);
				recYdStockStlNo.setField("STL_PROG_CD", recYdStockStlNo.getFieldString("CURR_PROG_CD") );
				
				recYdStockStlNo.setField("REFUR_CHG_PLN_SERNO", "0");
				recYdStockStlNo.setField("REFUR_CHG_LOT_NO"   , "");
				recYdStockStlNo.setField("STL_PROG_CD"        , "");
				recYdStockStlNo.setField("ROLL_UNIT_NAME"     , "");
				recYdStockStlNo.setField("MODIFIER"           , "HRYDJ006");
				nRet = ydStockDao.updYdStock(recYdStockStlNo, 0);
				if(nRet <= 0){
					szMsg = "YD_STOCK[C열연정정지시결번실적수신] UPDATE Error :: [" + nRet + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				szMsg = "[" + recYdStockStlNo.getFieldString("STL_NO") + "] :: YD_STOCK [C열연정정지시결번실적수신]UPDATE Success";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}

			
			//======================================================
			// 저장품제원 : 코일야드L2로 송신(YDY5L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY5L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recYdStockStlNo, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			ydDelegate.sendMsg(recResult);
			
		}catch(Exception e){ 
			szMsg = "[C열연정정지시결번실적수신]Exception Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[procCHrShearOrdMissnoWr_C열연정정지시결번실적수신]" + szMsg);
		} // end of try-catch

		szMsg = "C열연정정지시결번실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procCHrShearOrdMissnoWr
	
	
	
	
	
	/**
	 * 크레인스케쥴 테이블, 크레인작업재료 테이블 삭제  [권오창 2009.09.08]
	 * 
	 *     1. 크레인 스케쥴ID(YD_CRN_SCH_ID)값으로 크레인스케쥴(TB_YD_CRNSCH) 테이블 삭제
	 *     2. 크레인스케쥴ID(YD_CRN_SCH_ID)와 재료번호(STL_NO)로 크레인 작업재료(TB_YD_CRNWRKMTL) 테이블 삭제  
	 *     
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord  (YD_CRN_SCH_ID, STL_NO)
	 * @throws JDTOException
	 */
	public int DeleteCrnSchCrnWrkMtl_CrnSchIdStlNo(JDTORecord inRecord)throws JDTOException  {
		// 레코드 선언
		JDTORecord recPara 	   = null;

		// DAO객체 및 UTIL객체 생성
		YdCrnSchDao ydCrnSchDao       = new YdCrnSchDao();
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		// 변수선언
		String szMethodName    = "DeleteCrnSchCrnWrkMtl";
		String szMsg           = "";
		String szYD_CRN_SCH_ID = "";
		String szSTL_NO        = "";
		int nRet               = 0;
		
		try{
			szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(inRecord, "YD_CRN_SCH_ID");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");
			
			//==================================================================================
			// 크레인스케쥴(TB_YD_CRNSCH)테이블 삭제처리
			//==================================================================================
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
			recPara.setField("DEL_YN", "Y");
			nRet = ydCrnSchDao.updYdCrnsch(recPara, 0);
			if(nRet <= 0) {
				if(nRet == 0) {
					szMsg = "DeleteCrnSchCrnWrkMtl_CrnSchIdStlNo updYdCrnsch : data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(nRet == -2) {
					szMsg = "DeleteCrnSchCrnWrkMtl_CrnSchIdStlNo updYdCrnsch : parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg = "DeleteCrnSchCrnWrkMtl_CrnSchIdStlNo updYdCrnsch : execution failed";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				
				return nRet;
			} else {
				szMsg = "크레인스케쥴(TB_YD_CRNSCH)테이블 삭제처리 성공 [" + nRet + "] : YD_CRN_SCH_ID(" + szYD_CRN_SCH_ID + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);							
			}
			
			//==================================================================================
			// 크레인 작업재료 (TB_YD_CRNWRKMTL)테이블 삭제처리
			//==================================================================================
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
			recPara.setField("STL_NO", szSTL_NO);
			recPara.setField("DEL_YN", "Y");
			nRet = ydCrnWrkMtlDao.updYdCrnwrkmtl(recPara, 0);
			if(nRet <= 0) {
				if(nRet == 0) {
					szMsg = "DeleteCrnSchCrnWrkMtl_CrnSchIdStlNo updYdCrnwrkmtl : data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(nRet == -2) {
					szMsg = "DeleteCrnSchCrnWrkMtl_CrnSchIdStlNo updYdCrnwrkmtl : parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg = "DeleteCrnSchCrnWrkMtl_CrnSchIdStlNo updYdCrnwrkmtl : execution failed";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				
				return nRet;
			} else { 
				szMsg = "크레인 작업재료 (TB_YD_CRNWRKMTL)테이블  삭제처리 성공 [" + nRet + "] : YD_CRN_SCH_ID(" + szYD_CRN_SCH_ID + ") STL_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);							
			}
		}catch(Exception e){ 
			szMsg = "[크레인스케쥴 테이블, 크레인작업재료 테이블 삭제] Exception Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[DeleteCrnSchCrnWrkMtl_크레인스케쥴 테이블, 크레인작업재료 테이블 삭제]" + szMsg);
		}
		
		return nRet;
	}

	
	
	
	
	/**
	 * 작업예약 테이블, 작업예약재료 테이블 삭제 [권오창 2009.09.08]
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord  (YD_CRN_SCH_ID, STL_NO)
	 * @throws JDTOException
	 */
	public int DeleteWrkbookWrkbookMtl_WrkbookIdStlNo(JDTORecord inRec) throws JDTOException  {
		// 레코드 선언
		JDTORecord recPara 	            = null;

		// DAO객체 및 UTIL객체 생성
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
	
		// 변수선언
		String szMethodName  = "DeleteWrkbookWrkbookMtl_WrkbookIdStlNo";
		String szMsg         = "";		
		String szYD_WBOOK_ID = "";
		String szSTL_NO      = "";
		int nRet             = 0;
		int nRemainCount     = 0;
		
		
		try {
			szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(inRec, "YD_WBOOK_ID");
			szSTL_NO      = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");

			// 결번처리하려는 재료의 작업예약ID로 작업예약 + 작업예약재료를 조회하여 건수를 반환 (나중에 작업예약 테이블 삭제시에 판단하기 위해 필요) 
			nRemainCount = this.getCntWrkbookWrkMtl_WrkbookId(szYD_WBOOK_ID);

			//=======================================================================================
			// 작업예약재료 테이블 삭제
			//=======================================================================================
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("STL_NO"     , szSTL_NO);
			recPara.setField("DEL_YN"     , "Y");
			nRet = ydWrkbookMtlDao.updYdWrkbookmtl(recPara, 0);
			if(nRet <= 0) {
				if(nRet == 0) {
					szMsg = "DeleteWrkbookWrkbookMtl_WrkbookIdStlNo updYdWrkbookmtl : data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(nRet == -2) {
					szMsg = "DeleteWrkbookWrkbookMtl_WrkbookIdStlNo updYdWrkbookmtl : parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg = "DeleteWrkbookWrkbookMtl_WrkbookIdStlNo updYdWrkbookmtl : execution failed";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				
				return nRet;
			} else {
				szMsg = "작업예약재료 (TB_YD_WRKBOOKMTL)테이블 삭제처리 성공 [" + nRet + "] : YD_WBOOK_ID(" + szYD_WBOOK_ID + ") STL_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);									
			}
			
			
			//=======================================================================================
			// 작업예약 테이블 삭제
			//---------------------------------------------------------------------------------------
			// 작업예약(TB_YD_WRKBOOK) 테이블 삭제는 판단하여 처리 (작업예약재료 조회건수 1건이면 삭제)
			// 즉, 조회했던 카운트가 1보다 크다면 아직 이 작업예약에 물려 있는 다른 재료정보가 남아 있다는 것임
			// 그러므로 모든 작업예약재료가 처리 되고 마지막 작업예약재료인 한건만이  조회된다면 작업예약재료 테이블과 동시에
			// 작업예약테이블도 클리어 시켜야 됨
			//=======================================================================================
			if(nRemainCount == 1){
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
				recPara.setField("DEL_YN", "Y");
				nRet = ydWrkbookDao.updYdWrkbook(recPara, 0);
				if(nRet <= 0) {
	    			if(nRet == 0) {
	    				szMsg = "DeleteWrkbookWrkbookMtl_WrkbookIdStlNo updYdWrkbook : data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	    			}else if(nRet == -2) {
	    				szMsg = "DeleteWrkbookWrkbookMtl_WrkbookIdStlNo updYdWrkbook : parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else{
	    				szMsg = "DeleteWrkbookWrkbookMtl_WrkbookIdStlNo updYdWrkbook : execution failed";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
	    			
	    			return nRet;
				} else {
					szMsg = "작업예약 (TB_YD_WRKBOOK)테이블 삭제처리 성공 [" + nRet + "] YD_WBOOK_ID(" + szYD_WBOOK_ID + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);										
				}
			}
		}catch(Exception e){ 
			szMsg = "[작업예약재료, 작업예약  테이블 삭제] Exception Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[DeleteWrkbookWrkbookMtl_WrkbookIdStlNo_작업예약재료, 작업예약 테이블 삭제]" + szMsg);
		}
		
		return nRet;		
	}	
	
     
	
	
    
	/**
	 * 작업예약ID(YD_WBOOK_ID)와 재료번호(STL_NO)로 크레인스케쥴, 크레인작업재료 테이블 조회 [권오창 2009.09.08]
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord  (STL_NO, YD_WBOOK_ID)
	 * @throws JDTOException
	 */
	public int SearchCrnSchCrnWrkMtl_WrkbookIdStlNo(JDTORecord inRec, JDTORecordSet outRecSet) throws JDTOException  {
		// 레코드 선언
		JDTORecordSet rsResult = null;
		JDTORecord recPara     = null;

		// DAO객체 및  UTIL객체선언
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();

		// 변수선언
		String szMethodName    = "SearchCrnSchCrnWrkMtlByWrkbookIdSTLNo";
		String szMsg           = "";
		String szSTL_NO        = "";
		String szYD_WBOOK_ID   = "";
		int nRet               = 0;

		try{	
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();		
			
			szSTL_NO = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");
			szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(inRec, "YD_WBOOK_ID"); 	
			
			recPara.setField("STL_NO"     , szSTL_NO);
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			nRet = ydCrnSchDao.getYdCrnsch(recPara, rsResult, 38);
			if(nRet <= 0){
				szMsg = "크레인스케쥴 + 크레인작업재료 테이블 조회실패 [" + nRet + "] : STL_NO(" + szSTL_NO + ") YD_WBOOK_ID(" + szYD_WBOOK_ID + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);			
				return nRet;					
			} else {
				szMsg = "크레인스케쥴, 크레인작업재료 테이블 조회성공 [" + nRet + "] : STL_NO(" + szSTL_NO + ") YD_WBOOK_ID(" + szYD_WBOOK_ID + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);								
			}
			
			outRecSet.addAll(rsResult);
		}catch(Exception e){ 
			szMsg = "[크레인스케쥴 테이블, 크레인작업재료 테이블 조회] Exception Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[SearchCrnSchCrnWrkMtlByWrkbookIdSTLNo_크레인스케쥴 테이블+크레인작업재료 테이블 조회]" + szMsg);
		}			

		return nRet;
	}
	
	
	
	
	
	/**
	 * 결번처리하려는 재료의 작업예약ID(YD_WBOOK_ID)로 작업예약 + 작업예약재료를 조회하여 건수를 반환 [권오창 2009.09.08]
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param String  (YD_WBOOK_ID)
	 * @throws JDTOException
	 */
	public int getCntWrkbookWrkMtl_WrkbookId(String szYD_WBOOK_ID) throws JDTOException  {
		// 레코드 선언
		JDTORecordSet rsResult = null;
		JDTORecord recPara     = null;

		// DAO객체 및 UTIL객체 생성
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();

		// 변수 선언
		String szMethodName    = "getCntWrkbookWrkMtl_WrkbookId";
		String szMsg           = "";
		int nRet               = 0;
		
		try {
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();	
			
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			nRet = ydWrkbookDao.getYdWrkbook(recPara, rsResult, 17);
			if(nRet <= 0){
				szMsg = "작업예약(TB_YD_WRKBOOK) + 작업예약재료(TB_YD_WRKBOOKMTL) 테이블  조회실패 [" + nRet + "] : YD_WBOOK_ID(" + szYD_WBOOK_ID + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);			
				return nRet;
			} else {
				szMsg = "결번처리 위해 건수 조회성공 [" + nRet + "] : YD_WBOOK_ID(" + szYD_WBOOK_ID + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);								
			}		
		}catch(Exception e){ 
			szMsg = "[크레인스케쥴 테이블, 크레인작업재료 테이블 조회] Exception Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[SearchCrnSchCrnWrkMtlByWrkbookIdSTLNo_크레인스케쥴 테이블+크레인작업재료 테이블 조회]" + szMsg);
		}	
		return nRet;
	}	
	
	
	
	
	
	/**
	 * 결번처리 시 보조작업에 대해 작업진행상태가 지시대기(W) 혹은 권상 전(1) 일경우 크레인스케쥴에서 제거  [권오창 2009.09.08]
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param String  (YD_WBOOK_ID)
	 * @throws JDTOException
	 */
	public int setClearAidWork(JDTORecord inRec) throws JDTOException  {
		// 레코드 선언
		JDTORecordSet rsResult = null;
		JDTORecord recPara     = null;
		JDTORecord recGetVal   = null;

		// DAO객체 및 UTIL객체 생성
		YdStkLyrDao ydStkLyrDao   = new YdStkLyrDao();	
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();

		// 변수 선언
		String szMethodName    = "setClearAidWork";
		String szMsg           = "";
		String szYD_WBOOK_ID   = "";
		String szYD_STK_COL_GP = "";
		String szYD_STK_BED_NO = "";
		String szYD_STK_LYR_NO = "";
		String szSTL_NO        = "";
		String szYD_CRN_SCH_ID = "";
		String szYD_WRK_PROG_STAT = "";
		int nRet               = 0;
		
		try {
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_LYR_NO");
			szYD_WBOOK_ID   = ydDaoUtils.paraRecChkNull(inRec, "YD_WBOOK_ID");
				
			//=======================================================================================
			// 해당 BED의 좌측 OR 우측 조회
			//=======================================================================================
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();		
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
			nRet = ydStkLyrDao.getYdStklyr(recPara, rsResult, 0);
			if(nRet <= 0){
				szMsg = "해당 결번처리 재료번호의 보조작업 재료번호 조회실패 [" + nRet + "] YD_STK_COL_GP(" + szYD_STK_COL_GP + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ") YD_STK_LYR_NO(" + szYD_STK_LYR_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);										
			} else {					
				rsResult.first();
				recGetVal = rsResult.getRecord();
				szSTL_NO = ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO");
	
				szMsg = "해당 결번처리 재료번호의 보조작업 재료번호 조회성공 [" + nRet + "]  STL_NO(" + szSTL_NO + ") : YD_STK_COL_GP(" + szYD_STK_COL_GP + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ") YD_STK_LYR_NO(" + szYD_STK_LYR_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
				
				if(szSTL_NO.trim().equals("")){
					szMsg = "조회한 보조적업 재료번호(STL_NO)의 값이 없습니다 STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);									
				} else {
					//=======================================================================================
					// 작업예약ID와  보조작업 재료번호로 크레인스케쥴ID를 뽑아온다. 
					//=======================================================================================
					rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					recPara  = JDTORecordFactory.getInstance().create();		
					recPara.setField("STL_NO"     , szSTL_NO);
					recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
					nRet = this.SearchCrnSchCrnWrkMtl_WrkbookIdStlNo(recPara, rsResult);
					if(nRet <= 0){
						return nRet;					
					} 

					rsResult.first();
					recGetVal = rsResult.getRecord();		
					szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recGetVal, "YD_CRN_SCH_ID");
					szYD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recGetVal, "YD_WRK_PROG_STAT");
					
					//=================================================================================================
					// 작업진행상태가 지시대기(W)이거나  권상 전(1) 일 경우이며 보조작업재료가 작업예약 재료테이블에 없을 경우에
					// 크레인스케쥴(TB_YD_CRNSCH), 크레인 작업재료 (TB_YD_CRNWRKMTL)테이블  삭제처리
					//=================================================================================================
					if(szYD_WRK_PROG_STAT.equals("W") || szYD_WRK_PROG_STAT.equals("1")){
						rsResult = JDTORecordFactory.getInstance().createRecordSet("");
						recPara  = JDTORecordFactory.getInstance().create();		
						recPara.setField("STL_NO"     , szSTL_NO);
						recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
						nRet = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, rsResult, 0);
						if(nRet < 0){
							szMsg = "작업예약재료(TB_YD_WRKBOOKMTL)테이블 조회오류 [" + nRet + "]";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);																
						} else if(nRet == 0){
							szMsg = "작업예약재료(TB_YD_WRKBOOKMTL)테이블에 STL_NO(" + szSTL_NO + ")가  존재하지 않음. 즉, 결번처리 재로 파생된 보조작업재이므로 삭제가능";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);																

							recPara = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
							recPara.setField("STL_NO"       , szSTL_NO);
							if((nRet = this.DeleteCrnSchCrnWrkMtl_CrnSchIdStlNo(recPara)) <= 0){
								return nRet;
							}						
						} else {
							szMsg = "작업예약재료(TB_YD_WRKBOOKMTL)테이블에 STL_NO(" + szSTL_NO + ")가  존재함. 삭제하면 안됨";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);															
							return 0;
						}
					}
				}
			}
	}catch(Exception e){ 
		szMsg = "[보조작업에 대해 크레인스케쥴 테이블, 크레인작업재료 테이블 삭제처리] Exception Error : " + e.getMessage();
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		throw new DAOException("[SearchCrnSchCrnWrkMtlByWrkbookIdSTLNo_크레인스케쥴 테이블+크레인작업재료 테이블 삭제]" + szMsg);
	}	
	
	return nRet;
}
	
	
	/**
	 * B열연압연지시확정 (CTYDJ032)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procBHrMillOrdCmmt(JDTORecord inRecord)throws JDTOException  {
		//저장품DAO
		YdStockDao ydStockDao 					= new YdStockDao();
		
		JDTORecordSet rsGetStockSlabComm = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsGetYdStock = JDTORecordFactory.getInstance().createRecordSet("");
		
		JDTORecord recEditRec = JDTORecordFactory.getInstance().create();
		JDTORecord recSlabComm = JDTORecordFactory.getInstance().create();
		JDTORecord recStockColumn = null;
		JDTORecord recMtlstatmodhist = null;
		
		
		String szMethodName = "procBHrMillOrdCmmt";
		String szMsg = "";
		String szOperationName = "B열연압연지시확정";


		int intRtnVal = 0;
	
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg = "[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
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
			szMsg = "[생산통제] B열연압연지시확정 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			
			
			
			
			ydUtils.displayRecord(szOperationName, inRecord);
			
			//저장품 조회  Dao 호출 - [87]
			intRtnVal = ydStockDao.getYdStock(inRecord, rsGetStockSlabComm, 87);

			if(intRtnVal <= 0){

				if(intRtnVal == 0){
					szMsg = "getSLABCOMM_MillPlntGp [조건:조업공장구분] Error :: DO NOT EXIST ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}else{
					szMsg = "getSLABCOMM_MillPlntGp [조건:조업공장구분] Error :: PARAMETER ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}	
			}

			
			rsGetStockSlabComm.first();
			recSlabComm  = JDTORecordFactory.getInstance().create();
	
			for(int i=1; i<=rsGetStockSlabComm.size(); i++){

				recSlabComm = rsGetStockSlabComm.getRecord();
				
				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n========[조회된 항목 표시]========\n", 4);	
				ydUtils.displayRecord(szOperationName, recSlabComm);	
				ydUtils.putLog(szSessionName, szMethodName, "\n===============================\n", 4);
				
				//항목편집
				intRtnVal = this.edtCtSlabCommToYdstock(recSlabComm, recEditRec);
				if( intRtnVal < 0){
					szMsg = "[B열연압연지시확정 항목편집] Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}	
		
				// Debug Msg
				ydUtils.putLog(szSessionName, szMethodName, "\n========[편집된 항목 표시]========\n", 4);	
				ydUtils.displayRecord(szOperationName, recEditRec);	
				ydUtils.putLog(szSessionName, szMethodName, "\n===============================\n", 4);

				//저장품 DAO- [SELECT, INSERT, UPDATE]
				intRtnVal = ydStockDao.getYdStock(recEditRec, rsGetYdStock, 0);
				if(intRtnVal < 0){
					szMsg = "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				else if(intRtnVal == 0){

					szMsg = "YD_STOCK[저장품] INSERT";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					recEditRec.setField("REGISTER", "CTYDJ032");
					
					intRtnVal = ydStockDao.insYdStock(recEditRec);
					if(intRtnVal < 0){
						szMsg = "YD_STOCK[저장품] INSERT Error :: [" + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
					szMsg = i +"["+ recEditRec.getFieldString("STL_NO") +"] :: YD_STOCK[B열연압연지시확정 수신]INSERT Success  ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{

					szMsg = "YD_STOCK[저장품] UPDATE :: [1] YD_MTLSTATMODHIST INSERT ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					recStockColumn 		= JDTORecordFactory.getInstance().create();
					recMtlstatmodhist 	= JDTORecordFactory.getInstance().create();
					rsGetYdStock.first();
					recStockColumn = rsGetYdStock.getRecord();

					recMtlstatmodhist.setField("STL_NO",  				ydDaoUtils.paraRecChkNull(recStockColumn,"STL_NO"));
					recMtlstatmodhist.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(recStockColumn,"STL_APPEAR_GP")); 
					recMtlstatmodhist.setField("STL_PROG_CD",  			ydDaoUtils.paraRecChkNull(recStockColumn,"STL_PROG_CD")); 
					recMtlstatmodhist.setField("YD_MTL_ITEM",  			ydDaoUtils.paraRecChkNull(recStockColumn,"YD_MTL_ITEM")); 
					recMtlstatmodhist.setField("ORD_GP",  				ydDaoUtils.paraRecChkNull(recStockColumn,"ORD_GP")); 
					recMtlstatmodhist.setField("REFUR_CHG_LOT_NO", 		ydDaoUtils.paraRecChkNull(recStockColumn,"REFUR_CHG_LOT_NO")); 
					recMtlstatmodhist.setField("REFUR_CHG_PLN_SERNO",  	ydDaoUtils.paraRecChkNull(recStockColumn,"REFUR_CHG_PLN_SERNO")); 
					recMtlstatmodhist.setField("ORD_NO", 				ydDaoUtils.paraRecChkNull(recStockColumn,"ORD_NO")); 
					recMtlstatmodhist.setField("ORD_DTL", 				ydDaoUtils.paraRecChkNull(recStockColumn,"ORD_DTL")); 
					recMtlstatmodhist.setField("ORD_YEOJAE_GP", 		ydDaoUtils.paraRecChkNull(recStockColumn,"ORD_YEOJAE_GP")); 
					recMtlstatmodhist.setField("REHEAT_SLAB_GP", 		ydDaoUtils.paraRecChkNull(recStockColumn,"REHEAT_SLAB_GP")); 

					
					szMsg = "YD_STOCK[저장품] UPDATE :: [2] YD_STOCK UPDATE ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					recEditRec.setField("MODIFIER", "CTYDJ032");
					
					intRtnVal= ydStockDao.updYdStock(recEditRec, 0);
					if(intRtnVal <= 0){
						szMsg = "YD_STOCK[저장품] UPDATE Error :: [" + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
					szMsg = i +"["+ recEditRec.getFieldString("STL_NO") +"] :: YD_STOCK[B열연압연지시확정 수신]UPDATE Success  ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}

				rsGetStockSlabComm.next();
				
			} // end of for

		}catch(Exception e){

			szMsg = "[B열연압연지시확정]Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[procBHrMillOrdCmmt_B열연압연지시확정]" + szMsg);
		}
		szMsg = "B열연압연지시확정("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} //end of procBHrMillOrdCmmt()
	
	/**
	 * A후판 압연지시확정 준비스케줄등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecordSet
	 * @throws JDTOException
	 */
	public int insPrepSchDepilerSup (JDTORecordSet inRecordSet)throws JDTOException  {
		
		YdStkBedDao ydStkBedDao 					= new YdStkBedDao();
		YdPrepSchDao ydPrepSchDao					= new YdPrepSchDao();
		YdPrepMtlDao ydPrepMtlDao					= new YdPrepMtlDao();
		
		int intRtnVal 			= 0;
		String szMethodName     = "insPrepSchDepilerSup";
		String szMsg            = "";
		
		JDTORecord    recSlab 	= null;
		JDTORecord    recPara 	= null;
		JDTORecord    recBed 	= null;
		JDTORecord    recTemp 	= null;
		JDTORecordSet rsBed     = null;
		
		int intLyrMax           = 0;
		int intLyrWtMax         = 0;
		int intLyrTMax          = 0;
		int intLyrWMax          = 0;
		int intSlab_W           = 0;
		String szLyrMax         = "";
		String szLyrWtMax         = "";
		String szLyrTMax         = "";
		
		int intLyrSum           = 0;
		int intLyrWtSum         = 0;
		int intLyrTSum          = 0;
		
		int intLOTDepSum        = 0;
		int intLOTCurrCnt       = 0;
		int intLOTDepCnt        = 3;  //Bre Rule로 등록해야하는 것 현재는 상수로 처리 (석창화)
		String szLOTDepCnt       = "";
		
		boolean bRtnVal         = false;
				
		
		
		String szUserId             = "";
		String szYD_TO_LOC_GUIDE    = "";
		String szYD_TO_LOC_DCSN_MTD = "";
		String szYD_AIM_YD_GP		= "";
		String szYD_GP				= "";
		String szYD_SCH_CD			= "";
		String szYD_PREP_SCH_ID		= "";
		String szYD_AIM_BAY_GP		= "";
		String szSTL_NO				= "";
		String szYD_STK_COL_GP		= "";
		String szYD_STK_BED_NO		= "";
		String szYD_STK_LYR_NO		= "";
		String szYD_CARASGN_SEQ     = "";
		String szYD_WRK_PLAN_CRN    = "";
		String szYD_SPAN_GP         = "";
				
		
		try{
			szMsg = "A후판 압연지시확정 준비스케줄등록("+szMethodName+") 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*********************************************************************************/
			// 2010.01.27  석창화
			// 준비스케줄 처리하기 전에 슬라브 선별 준비스케줄을 삭제처리한다.
			szMsg = "["+szMethodName+"] 선별준비스케줄 삭제 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("MODIFIER", "CTYDJ031");
			
			intRtnVal = ydPrepMtlDao.uptDelYdPrepmtl_CTYDJ031(recPara);
				
			if( intRtnVal < 0 ) {
				szMsg = "["+szMethodName+"] 선별준비스케줄재료 삭제 시 오류발생[1] - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			
			intRtnVal = ydPrepSchDao.updDelYdPrepsch_CTYDJ031(recPara);
			
			if( intRtnVal < 0 ) {
				szMsg = "["+szMethodName+"] 선별준비스케줄 삭제 시 오류발생[1] - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			
			szMsg = "["+szMethodName+"] 선별준비스케줄 삭제 완료 건수 [" + intRtnVal + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			/*********************************************************************************/
			
			
			JDTORecord jdtoRcd = JDTORecordFactory.getInstance().create();
	    	bRtnVal = GetBreRule2.getYDB298(jdtoRcd);
	    	if( bRtnVal ) {
	    		szLOTDepCnt = ydDaoUtils.paraRecChkNull(jdtoRcd, "LOT_DEP_CNT");
	    		intLOTDepCnt = Integer.valueOf(szLOTDepCnt).intValue();
	    		
	    		szLyrTMax = ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_STK_BED_H_MAX");
	    		intLyrTMax = Integer.valueOf(szLyrTMax).intValue();
	    		
	    		szLyrWtMax = ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_STK_BED_WT_MAX");
	    		intLyrWtMax = Integer.valueOf(szLyrWtMax).intValue();
	    		
	    		szLyrMax = ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_STK_BED_LYR_MAX");
	    		intLyrMax = Integer.valueOf(szLyrMax).intValue();
	    		
	    		
	    		szMsg = "BRE RULE -- A후판슬라브야드 장입LOT묶음수[" + intLOTDepCnt + "]";

	    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		szMsg = "BRE RULE -- A후판 장입BED 최대매수[" + intLyrMax + "],최대높이[" + intLyrTMax + "],최대중량[" + intLyrWtMax + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	    	}
			
			// 장입BED의 최대매수, 최대높이, 최대중량을 읽어서 설정한다.
	    	// BRE Rule로 대체 - 2010.01.14 석창화
//			recPara  = JDTORecordFactory.getInstance().create();
//			recPara.setField("YD_STK_COL_GP", YdConstant.EQP_D_PU1);
//			recPara.setField("YD_STK_BED_NO", "01");
//			
//			rsBed = JDTORecordFactory.getInstance().createRecordSet("");
//			
//			intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsBed, 0);
//			
//			if (intRtnVal <= 0 ) {
//				szMsg = "장입BED 정보조회시 오류발생 에러코드 : " + intRtnVal;
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return -1;
//			}
//			rsBed.first();
//			recBed = rsBed.getRecord();
//			
//			szMsg = "장입BED 정보조회후 최대매수,최대높이, 최대중량 추출";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			
//			intLyrMax = recBed.getFieldInt("YD_STK_BED_LYR_MAX");
//			intLyrWtMax = recBed.getFieldInt("YD_STK_BED_WT_MAX");
//			intLyrTMax = recBed.getFieldInt("YD_STK_BED_H_MAX");
//			
//			szMsg = "A후판 장입BED 최대매수[" + intLyrMax + "],최대높이[" + intLyrTMax + "],최대중량[" + intLyrWtMax + "]";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			inRecordSet.first();
			
			recSlab  = JDTORecordFactory.getInstance().create();
			
			for (int row = 1; row <= inRecordSet.size(); row++) {
				inRecordSet.absolute(row);
				recSlab = inRecordSet.getRecord();
					
				szUserId 		= "SYSTEM";
				szYD_TO_LOC_GUIDE = "";
				szYD_TO_LOC_DCSN_MTD = "S";
				
				szYD_AIM_YD_GP = YdConstant.YD_GP_A_PLATE_SLAB_YARD;
				
				szYD_GP = YdConstant.YD_GP_A_PLATE_SLAB_YARD;
				
				//szYD_SCH_CD = YdConstant.SCH_CD_D_REFUR_SUP1;
				
				szYD_SCH_CD = "DAYD01MM";
				szYD_AIM_BAY_GP = "A";
				
				szYD_WRK_PLAN_CRN = "DACRA1";
				
				//준비스케줄 등록
				szYD_PREP_SCH_ID = ydPrepSchDao.getYdPrepschId();
				recTemp = JDTORecordFactory.getInstance().create();
				recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
				recTemp.setField("YD_SCH_CD", szYD_SCH_CD);
				recTemp.setField("YD_WRK_PLAN_CRN", szYD_WRK_PLAN_CRN);
				recTemp.setField("REGISTER", szUserId);
				recTemp.setField("YD_GP", szYD_GP);
				recTemp.setField("YD_PREP_WK_ST", "S");  //선별:S,  상차Lot:L
				
				recTemp.setField("YD_TO_LOC_GUIDE", szYD_TO_LOC_GUIDE);
				recTemp.setField("YD_TO_LOC_DCSN_MTD", szYD_TO_LOC_DCSN_MTD);
				recTemp.setField("YD_AIM_YD_GP",    szYD_AIM_YD_GP);
				recTemp.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
				
				
				if(intLOTDepCnt <= intLOTCurrCnt){
					intLOTCurrCnt = 0;
					intLOTDepSum++;
				}
				szYD_CARASGN_SEQ = Integer.toString((intLOTDepCnt * intLOTDepSum) + (intLOTDepCnt - intLOTCurrCnt));
								
				intLOTCurrCnt++;
				
				
				recTemp.setField("YD_CARASGN_SEQ", szYD_CARASGN_SEQ);
				
				//recTemp.setField("YD_INV_SUM_WT","" + lngGRP_WT);
				
				intRtnVal = ydPrepSchDao.insYdPrepsch(recTemp);
				
				if( intRtnVal < 0 ) {
					szMsg = "["+szMethodName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if( intRtnVal == 0 ) {
					szMsg = "["+szMethodName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg = "["+szMethodName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 등록 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
				for(int i = row; i <= inRecordSet.size(); i++ ) {
					inRecordSet.absolute(i);
					recSlab = inRecordSet.getRecord();
					
					intLyrSum = intLyrSum + 1;
					intLyrWtSum = intLyrWtSum + recSlab.getFieldInt("SLAB_WT");
					intLyrTSum = intLyrTSum + recSlab.getFieldInt("SLAB_T");
					
					intSlab_W =  recSlab.getFieldInt("SLAB_W");
					
					if (intLyrWMax == 0) {
						intLyrWMax = intSlab_W;
					}
					
					if (intLyrSum <= intLyrMax && intLyrWtSum <= intLyrWtMax && intLyrTSum <= intLyrTMax && Math.abs(intLyrWMax - intSlab_W) < 100){
					
						szSTL_NO  		= ydDaoUtils.paraRecChkNull(recSlab, "STL_NO");
						
						if( !"".equals(ydDaoUtils.paraRecChkNull(recSlab, "YD_STK_POS"))) {					
							szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recSlab, "YD_STK_POS").substring(0, 6);
							szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recSlab, "YD_STK_POS").substring(6, 8);
							szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recSlab, "YD_STK_LYR_NO");
						}
											
						//준비재료 등록
						recTemp = JDTORecordFactory.getInstance().create();
						recTemp.setField("STL_NO", szSTL_NO);
						recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
						recTemp.setField("REGISTER", szUserId);
						recTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
						recTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
						recTemp.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
						
						intRtnVal = ydPrepMtlDao.insYdPrepmtl(recTemp);
						
						if( intRtnVal < 0 ) {
							szMsg = "["+szMethodName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 재료["+szSTL_NO+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else if( intRtnVal == 0 ) {
							szMsg = "["+szMethodName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 재료["+szSTL_NO+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else{
							szMsg = "["+szMethodName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 재료["+szSTL_NO+"] 등록 성공 ";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
						
						
						if (intSlab_W > intLyrWMax) {
							intLyrWMax = intSlab_W;
						}
													
						
					} else {
						
						
						intLyrWtSum = intLyrWtSum - recSlab.getFieldInt("SLAB_WT");
						intLyrSum = intLyrSum - 1;
						
						recTemp = JDTORecordFactory.getInstance().create();
						
						//준비스케줄수정
						if (!"".equals(szYD_STK_COL_GP)) {
							szYD_SPAN_GP = szYD_STK_COL_GP.substring(2,4);
							szYD_SCH_CD = "DAYD" + szYD_SPAN_GP + "MM";
							if ("01".equals(szYD_SPAN_GP) || "02".equals(szYD_SPAN_GP)) {
								szYD_WRK_PLAN_CRN = "DACRA1";
							} else {
								szYD_WRK_PLAN_CRN = "DACRA2";
							}
							
							recTemp.setField("YD_SCH_CD", szYD_SCH_CD);
							recTemp.setField("YD_WRK_PLAN_CRN", szYD_WRK_PLAN_CRN);
							
						}
						
						recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
						recTemp.setField("YD_INV_SUM_WT", "" + intLyrWtSum);
						recTemp.setField("YD_EQP_WRK_SH", String.valueOf(intLyrSum));
						intRtnVal = ydPrepSchDao.updYdPrepsch(recTemp, 0);
						
						// 합계값 초기화
						intLyrSum   = 0;
						intLyrWtSum = 0;
						intLyrTSum  = 0;
						intLyrWMax  = 0;
						
						row = i-1;
						break;
					}
					
					if (i == inRecordSet.size()) {
						row = i;
						break;
					}
					
				} //end of for(i)
				
			} //end of for(row)
			
			if (intLyrSum > 0) {
				recTemp = JDTORecordFactory.getInstance().create();
				//준비스케줄수정
				if (!"".equals(szYD_STK_COL_GP)) {
					szYD_SPAN_GP = szYD_STK_COL_GP.substring(2,4);
					szYD_SCH_CD = "DAYD" + szYD_SPAN_GP + "MM";
					if ("01".equals(szYD_SPAN_GP) || "02".equals(szYD_SPAN_GP)) {
						szYD_WRK_PLAN_CRN = "DACRA1";
					} else {
						szYD_WRK_PLAN_CRN = "DACRA2";
					}
					
					recTemp.setField("YD_SCH_CD", szYD_SCH_CD);
					recTemp.setField("YD_WRK_PLAN_CRN", szYD_WRK_PLAN_CRN);
					
				}
				recTemp.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
				recTemp.setField("YD_INV_SUM_WT", "" + intLyrWtSum);
				recTemp.setField("YD_EQP_WRK_SH", String.valueOf(intLyrSum));
				intRtnVal = ydPrepSchDao.updYdPrepsch(recTemp, 0);
			}
		
		}catch(Exception e){ 

			szMsg = "[A후판 압연지시확정 준비스케줄등록]Exception Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[insPrepSchDepilerSup_A후판 압연지시확정 준비스케줄등록]" + szMsg);
		} // end of try-catch
		
		szMsg = "A후판 압연지시확정 준비스케줄등록("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return intRtnVal;
	}
	



	
	

  //---------------------------------------------------------------------------	
} // end of class WrkPlnRegCommSeEJBBean
