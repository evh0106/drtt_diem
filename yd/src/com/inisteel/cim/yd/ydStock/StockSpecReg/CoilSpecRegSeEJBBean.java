package com.inisteel.cim.yd.ydStock.StockSpecReg;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.jms.JmsQueueSender;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCodeMapping;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;

/**
 * Coil제원등록 Session EJB
 *
 * @ejb.bean name="CoilSpecRegSeEJB" jndi-name="CoilSpecRegSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CoilSpecRegSeEJBBean extends BaseSessionBean {

	// Session Name
	private String szSessionName  = getClass().getName();

	private YdUtils ydUtils       = new YdUtils();
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	private YdTcConst ydTcConst   = new YdTcConst();
	private YdDelegate ydDelegate = new YdDelegate();
	private SlabYdCommDAO commDao= new SlabYdCommDAO ();
	private StockSpecRegSeEJBBean stock = new StockSpecRegSeEJBBean();
	private YmCommUtils commUtils = new YmCommUtils();
	
	// [DEBUG] message flag
	private boolean bDebugFlag    = true;
	String[] rVal = new String[1];

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}




	/**
	 * C열연 압연생산실적(평량실적) (HRYDJ003)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCHrMillPrdWr(JDTORecord inRecord)throws JDTOException  {
		// 저장품DAO
		YdStockDao ydStockDao                   = new YdStockDao();
		
		JDTORecord recEditInRecord		= JDTORecordFactory.getInstance().create();
		JDTORecord recCoilColumn		= JDTORecordFactory.getInstance().create();
		JDTORecord recEditRec			= JDTORecordFactory.getInstance().create();
		JDTORecord recEditRec1			= JDTORecordFactory.getInstance().create();
		JDTORecord recStockColumn		= JDTORecordFactory.getInstance().create();
		JDTORecord recMtlstatmodhist 	= JDTORecordFactory.getInstance().create();
		
		JDTORecord recInTemp = null;

		JDTORecordSet rsGetCoilComm 	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsGetYdStock  	= JDTORecordFactory.getInstance().createRecordSet("");

		String szMethodName 			= "procCHrMillPrdWr";
		String szMsg 					= "";
		String szOperationName          = "C열연 압연생산실적";
		
		int intRtnVal 					= 0;

		
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode = ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode == null){
			szMsg = "[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		// 수신한 전문내용 
		ydUtils.displayRecord(szOperationName, inRecord);
		
		try{
			//=============================================================
			// 권오창
			// 2009.11.05
			//
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[열연조업] 압연생산실적 수신";
			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			
			
			
			
			recEditInRecord.setField("COIL_NO", ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			intRtnVal = ydStockDao.getYdStock(recEditInRecord, rsGetCoilComm, 8);
			if(intRtnVal <=0 ){
				if(intRtnVal == 0){
					szMsg= "COILCOMM[코일공통] Error :: [" + inRecord.getFieldString("STL_NO") + "]" +"DO NOT EXIST";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}else{
					szMsg= "COILCOMM[코일공통] Error :: PARAMETER ERROR" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
			rsGetCoilComm.first();
			recCoilColumn = rsGetCoilComm.getRecord();
			
			// Debug Msg
			ydUtils.putLog(CoilSpecRegSeEJBBean.class.getName(), szMethodName, "\n========[편집 전 항목  표시]========\n", 4);	
			ydUtils.displayRecord(szOperationName, recCoilColumn);
			ydUtils.putLog(CoilSpecRegSeEJBBean.class.getName(), szMethodName, "\n===============================\n", 4);
			
			intRtnVal = this.edtCoilCommYdstock(recCoilColumn,recEditRec1);
			//항목 편집이 실패일 때
			if(intRtnVal < 0){
				szMsg= "[항목편집] Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
				return;
			}
			
			// Debug Msg
			ydUtils.putLog(CoilSpecRegSeEJBBean.class.getName(), szMethodName, "\n========[편집 후 항목  표시]========\n", 4);	
			ydUtils.displayRecord(szOperationName, recEditRec1);
			ydUtils.putLog(CoilSpecRegSeEJBBean.class.getName(), szMethodName, "\n===============================\n", 4);
			
			
			intRtnVal = ydStockDao.getYdStock(recEditRec1, rsGetYdStock, 0);
			
			if(intRtnVal < 0){
				szMsg= "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			else if(intRtnVal == 0){
				
				szMsg= "YD_STOCK[저장품] INSERT :: " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
				recEditRec1.setField("REGISTER", "HRYDJ003");
				
				intRtnVal = ydStockDao.insYdStock(recEditRec1);
				if(intRtnVal < 0){
					szMsg= "YD_STOCK[저장품] INSERT Error :: [" + intRtnVal + "]" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
				szMsg=recEditRec1.getFieldString("STL_NO") +" :: YD_STOCK[C열연압연생산실적수신] INSERT Success  " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
			}
			
			else{
				
				szMsg= "YD_STOCK[저장품] UPDATE :: [1] YD_MTLSTATMODHIST INSERT " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recMtlstatmodhist = JDTORecordFactory.getInstance().create();
				rsGetYdStock.first();
				recStockColumn = rsGetYdStock.getRecord();
				
				recMtlstatmodhist.setField("STL_NO",  		ydDaoUtils.paraRecChkNull(recStockColumn,"STL_NO"));
				recMtlstatmodhist.setField("STL_APPEAR_GP", ydDaoUtils.paraRecChkNull(recStockColumn,"STL_APPEAR_GP")); 
				recMtlstatmodhist.setField("STL_PROG_CD",  	ydDaoUtils.paraRecChkNull(recStockColumn,"STL_PROG_CD")); 
				recMtlstatmodhist.setField("YD_MTL_ITEM",  	ydDaoUtils.paraRecChkNull(recStockColumn,"YD_MTL_ITEM")); 
				recMtlstatmodhist.setField("ORD_GP",  		ydDaoUtils.paraRecChkNull(recStockColumn,"ORD_GP")); 
				recMtlstatmodhist.setField("CUST_CD", 		ydDaoUtils.paraRecChkNull(recStockColumn,"CUST_CD")); 
				recMtlstatmodhist.setField("DEST_CD",  		ydDaoUtils.paraRecChkNull(recStockColumn,"DEST_CD")); 
				recMtlstatmodhist.setField("ORD_NO", 		ydDaoUtils.paraRecChkNull(recStockColumn,"ORD_NO")); 
				recMtlstatmodhist.setField("ORD_DTL", 		ydDaoUtils.paraRecChkNull(recStockColumn,"ORD_DTL")); 
				recMtlstatmodhist.setField("ORD_YEOJAE_GP", ydDaoUtils.paraRecChkNull(recStockColumn,"ORD_YEOJAE_GP")); 
				
				
				szMsg= "YD_STOCK[저장품] UPDATE :: [2] YD_STOCK UPDATE " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recEditRec1.setField("MODIFIER", "HRYDJ003");
		
				intRtnVal= ydStockDao.updYdStock(recEditRec1, 0);
				if(intRtnVal <= 0){
					szMsg= "YD_STOCK[저장품] UPDATE Error :: [" + intRtnVal + "]" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
				szMsg=recEditRec1.getFieldString("STL_NO") +" :: YD_STOCK[C열연압연생산실적수신]UPDATE Success  " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    		 * 업무기준 : C열연압연생산실적수신 시 저장품 제원 야드L2로 전송
    		 * 수정자 : 임춘수
    		 * 수정일자 : 2009.08.24
    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
			recInTemp  = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID"			,        "YDY5L002");
			recInTemp.setField("YD_INFO_SYNC_CD",        "A");							//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
	    	recInTemp.setField("STL_NO",        ydDaoUtils.paraRecChkNull(recEditRec1, "STL_NO"));
	    	recInTemp.setField("YD_STK_COL_GP", YdConstant.YD_GP_C_HR_COIL_GDS_YARD);
	    	//recInTemp.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_NO"));
	    	
	    	ydDelegate.sendMsg(recInTemp);
		    	
	    	szMsg="<procCHrMillPrdWr> C열연압연생산실적수신 시 저장품 제원 야드L2[YDY5L002]로 전송";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
		}catch(Exception e){
			
			szMsg="[C열연압연생산실적수신] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg="C열연압연생산실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procCHrMillPrdWr
	

	/**
	 * [C열연압연생산실적수신] 항목 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtCoilCommYdstock(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {

		JDTORecord recEditRec1			= JDTORecordFactory.getInstance().create();
		String szMethodName		="edtCoilCommYdstock";
		String szMsg			="";
		String szHCR_GP			="";
		String szSTL_APPEAR_GP 	="";
		String szYD_MTL_ITEM	=""; 
		String szYD_AIM_YD_GP	="";
		String szYD_AIM_RT_GP	="";
		String szSTL_PROG_CD	="";

		double  iW_GP              = 0; 
		double  iOUTDIA            = 0;
		String sW_GP            ="";
		String sOUTDIA          ="";		
		

		
		try{
			szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP");
//			if(szSTL_APPEAR_GP.equals("E")){
//				szYD_MTL_ITEM = "CM";
//				szYD_AIM_YD_GP= "H";
//			}else if(szSTL_APPEAR_GP.equals("Y")){
//				szYD_MTL_ITEM = "CG";
//				szYD_AIM_YD_GP= "J";
//			}
			szHCR_GP = ydDaoUtils.paraRecChkNull(inRecord,"HCR_GP");
			szSTL_PROG_CD = ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD");
			
			recEditRec1		 = JDTORecordFactory.getInstance().create();
			recEditRec1.setField("STL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"COIL_NO"));
			recEditRec1.setField("CURR_PROG_CD",	ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			
			rVal = YdCommonUtils.getYdAimRtGp("C", recEditRec1);

			//실적발생 후 진도코드에 따른 목표야드 변경 작업
			if(rVal[1].equals("H") ||rVal[1].equals("G") ||rVal[1].equals("F")){
				szYD_AIM_YD_GP= "J";
				szYD_MTL_ITEM = "CG";
			}else{
				szYD_AIM_YD_GP= "H";
				szYD_MTL_ITEM = "CM";
			}
			
			recEditRec.setField("STL_NO"				, ydDaoUtils.paraRecChkNull(inRecord,"COIL_NO")); 					
			recEditRec.setField("YD_MTL_T"				, ydDaoUtils.paraRecChkNull(inRecord,"COIL_T")); 		
			recEditRec.setField("YD_MTL_W"				, ydDaoUtils.paraRecChkNull(inRecord,"COIL_W")); 		    
			recEditRec.setField("YD_MTL_L"				, ydDaoUtils.paraRecChkNull(inRecord,"COIL_LEN"));		
			recEditRec.setField("YD_MTL_WT"				, ydDaoUtils.paraRecChkNull(inRecord,"COIL_WT"));	
			recEditRec.setField("COIL_INDIA"			, ydDaoUtils.paraRecChkNull(inRecord,"COIL_INDIA"));	
			recEditRec.setField("COIL_OUTDIA"			, ydDaoUtils.paraRecChkNull(inRecord,"COIL_OUTDIA"));	
			recEditRec.setField("PLNT_PROC_CD"			, ydDaoUtils.paraRecChkNull(inRecord,"PLNT_PROC_CD"));				
			recEditRec.setField("ORD_YEOJAE_GP"			, ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));			
			recEditRec.setField("ORD_NO"				, ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO")); 			
			recEditRec.setField("ORD_DTL"				, ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL")); 				
			recEditRec.setField("HYSCO_TRANS_GP"		, ydDaoUtils.paraRecChkNull(inRecord,"HYSCO_TRANS_GP"));		
			recEditRec.setField("COOL_METHOD"			, ydDaoUtils.paraRecChkNull(inRecord,"COOL_METHOD"));
			recEditRec.setField("COOL_DONE_GP"			, ydDaoUtils.paraRecChkNull(inRecord,"COOL_DONE_GP"));
			recEditRec.setField("YD_CONVEYOR_BRANCH_CD"	, ydDaoUtils.paraRecChkNull(inRecord,"BRANCH_CD"));	
			recEditRec.setField("CUST_CD"				, ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD")); 			
			recEditRec.setField("DEMANDER_CD"			, ydDaoUtils.paraRecChkNull(inRecord,"DEMANDER_CD"));
			recEditRec.setField("HCR_GP"				, ydDaoUtils.paraRecChkNull(inRecord,"HCR_GP"));
			recEditRec.setField("ITEMNAME_CD"			, ydDaoUtils.paraRecChkNull(inRecord,"ITEMNAME_CD"));
			recEditRec.setField("PTOP_PLNT_GP"			, ydDaoUtils.paraRecChkNull(inRecord,"PTOP_PLNT_GP"));
			//recEditRec.setField("NEXT_PROC"			, ydDaoUtils.paraRecChkNull(inRecord,"NEXT_PROC"));
			recEditRec.setField("STL_PROG_CD"			, szSTL_PROG_CD);
			recEditRec.setField("STL_APPEAR_GP"			, szSTL_APPEAR_GP);
			recEditRec.setField("YD_MTL_ITEM"			, szYD_MTL_ITEM);
			recEditRec.setField("YD_AIM_YD_GP"			, szYD_AIM_YD_GP);	
			recEditRec.setField("YD_AIM_RT_GP", 			rVal[0]);	
			//			폭 구분 
			//iW_GP = Integer.parseInt(ydDaoUtils.paraRecChkNull(inRecord,"COIL_W"));
			iW_GP = ydDaoUtils.paraRecChkNullDouble(inRecord,"SHEAR_W");
			
			if (iW_GP < 1601) {
				sW_GP = "M";
			} else {
				sW_GP = "L";
			}
			recEditRec.setField("YD_MTL_W_GP"		 , sW_GP);
			
//외경그룹			
			//iOUTDIA = Integer.parseInt(ydDaoUtils.paraRecChkNull(inRecord,"COIL_OUTDIA"));
			iOUTDIA = ydDaoUtils.paraRecChkNullDouble(inRecord,"COIL_OUTDIA");
			
			if (iOUTDIA <= 1280) {
				sOUTDIA = "A";
			} else if (( iOUTDIA > 1280 )&&( iOUTDIA <= 1930)) { 
				sOUTDIA = "B";
			} else if ( iOUTDIA > 1930 ) {
				sOUTDIA = "C";
			}
			recEditRec.setField("YD_COIL_OUTDIA_GRP_GP"		 , sOUTDIA);

		
			
			
		} catch(Exception e){

			return -1;
		}
		return 1;

	} //end of edtCoilCommYdstock()
	
	
	
	/**
	 * C열연 압연작업실적 (HRYDJ004)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord procCHrMillWrkWr
	 * @throws JDTOException
	 */
	public void procCHrMillWrkWr(JDTORecord inRecord)throws JDTOException  {
		// 저장품DAO
		YdStockDao ydStockDao                   = new YdStockDao();
		
		JDTORecord recEditInRecord	 = JDTORecordFactory.getInstance().create();
		JDTORecord recCoilColumn	 = JDTORecordFactory.getInstance().create();
		JDTORecord recEditRec		 = JDTORecordFactory.getInstance().create();
		JDTORecord recEditRec1		 = JDTORecordFactory.getInstance().create();
		JDTORecord recStockColumn	 = JDTORecordFactory.getInstance().create();
		JDTORecord recMtlstatmodhist = JDTORecordFactory.getInstance().create();

		JDTORecordSet rsGetCoilComm  = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsGetYdStock   = JDTORecordFactory.getInstance().createRecordSet("");

		String szMethodName          = "procCHrMillWrkWr";
		String szMsg                 = "";
		String szOperationName       = "C열연 압연작업실적";
		int intRtnVal = 0;

		
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg = "[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		

		ydUtils.displayRecord(szOperationName, inRecord);
		
		
		try{
			//=============================================================
			// 권오창
			// 2009.11.05
			//
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[열연조업] 압연작업실적 수신";
			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			
			
			
			
			recEditInRecord.setField("COIL_NO", ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			intRtnVal = ydStockDao.getYdStock(recEditInRecord, rsGetCoilComm, 8);
			if(intRtnVal <=0 ){

				if(intRtnVal == 0){
					szMsg= "COILCOMM[코일공통] Error :: [" + inRecord.getFieldString("STL_NO") + "]" +"DO NOT EXIST";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}else{
					szMsg= "COILCOMM[코일공통] Error :: PARAMETER ERROR" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
			rsGetCoilComm.first();
			recCoilColumn = rsGetCoilComm.getRecord();
			
			// Debug Msg
			ydUtils.putLog(CoilSpecRegSeEJBBean.class.getName(), szMethodName, "\n========[편집 전 항목  표시]========\n", 4);	
			ydUtils.displayRecord(szOperationName, recCoilColumn);
			ydUtils.putLog(CoilSpecRegSeEJBBean.class.getName(), szMethodName, "\n===============================\n", 4);
			 
			intRtnVal = this.editCHrMillWrkWr(recCoilColumn,recEditRec);
			//항목 편집이 실패일 때
			if(intRtnVal < 0){
				szMsg = "[항목편집] Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
				return;
			}
			
			// Debug Msg
			ydUtils.putLog(CoilSpecRegSeEJBBean.class.getName(), szMethodName, "\n========[편집 후 항목  표시]========\n", 4);	
			ydUtils.displayRecord(szOperationName, recEditRec);
			ydUtils.putLog(CoilSpecRegSeEJBBean.class.getName(), szMethodName, "\n===============================\n", 4);
			
			
			intRtnVal = ydStockDao.getYdStock(recEditRec, rsGetYdStock, 0);
			
			if(intRtnVal < 0){
				szMsg= "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			} else if(intRtnVal == 0){
				
				szMsg= "YD_STOCK[저장품] INSERT :: " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recEditRec.setField("REGISTER", "HRYDJ004");
							
				intRtnVal = ydStockDao.insYdStock(recEditRec);
				if(intRtnVal < 0){
					szMsg= "YD_STOCK[저장품] INSERT Error :: [" + intRtnVal + "]" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
				szMsg=recEditRec.getFieldString("STL_NO") +" :: YD_STOCK[C열연압연작업실적수신] INSERT Success  " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			else{
				
				szMsg= "YD_STOCK[저장품] UPDATE :: [1] YD_MTLSTATMODHIST INSERT " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recMtlstatmodhist = JDTORecordFactory.getInstance().create();
				rsGetYdStock.first();
				recStockColumn = rsGetYdStock.getRecord();
				
				recMtlstatmodhist.setField("STL_NO",  			ydDaoUtils.paraRecChkNull(recStockColumn,"STL_NO"));
				recMtlstatmodhist.setField("STL_APPEAR_GP", 	ydDaoUtils.paraRecChkNull(recStockColumn,"STL_APPEAR_GP")); 
				recMtlstatmodhist.setField("STL_PROG_CD",  		ydDaoUtils.paraRecChkNull(recStockColumn,"STL_PROG_CD")); 
				recMtlstatmodhist.setField("YD_MTL_ITEM",  		ydDaoUtils.paraRecChkNull(recStockColumn,"YD_MTL_ITEM")); 
				recMtlstatmodhist.setField("ORD_GP",  			ydDaoUtils.paraRecChkNull(recStockColumn,"ORD_GP")); 
				recMtlstatmodhist.setField("HYSCO_TRANS_GP", 	ydDaoUtils.paraRecChkNull(recStockColumn,"HYSCO_TRANS_GP")); 
				recMtlstatmodhist.setField("ORD_YEOJAE_GP", 	ydDaoUtils.paraRecChkNull(recStockColumn,"ORD_YEOJAE_GP")); 
				recMtlstatmodhist.setField("COOL_METHOD",  		ydDaoUtils.paraRecChkNull(recStockColumn,"COOL_METHOD")); 
				recMtlstatmodhist.setField("ORD_NO", 			ydDaoUtils.paraRecChkNull(recStockColumn,"ORD_NO")); 
				recMtlstatmodhist.setField("ORD_DTL", 			ydDaoUtils.paraRecChkNull(recStockColumn,"ORD_DTL")); 
				recMtlstatmodhist.setField("YD_CONVEYOR_BRANCH_CD", ydDaoUtils.paraRecChkNull(recStockColumn,"YD_CONVEYOR_BRANCH_CD")); 
				
				
				szMsg= "YD_STOCK[저장품] UPDATE :: [2] YD_STOCK UPDATE " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recEditRec.setField("MODIFIER", "HRYDJ004");
				
				intRtnVal= ydStockDao.updYdStock(recEditRec, 0);
				if(intRtnVal <= 0){
					szMsg= "YD_STOCK[저장품] UPDATE Error :: [" + intRtnVal + "]" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
				szMsg=recEditRec.getFieldString("STL_NO") +" :: YD_STOCK[C열연압연작업실적수신] UPDATE Success  " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			
			
			
			
			//======================================================
			// 2009.08.31 권오창
			// 저장품제원 : 코일야드L2로 송신(YDY5L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY5L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recEditRec, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			ydDelegate.sendMsg(recResult);
			
		}catch(Exception e){
			szMsg="[C열연압연작업실적수신] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg="C열연압연작업실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procCHrMillWrkWr
	
	
	/**
	 * [C열연압연작업실적수신] 항목 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int editCHrMillWrkWr(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {

		String szMethodName		="editCHrMillWrkWr";
		String szMsg			="";
		String szSTL_APPEAR_GP 	="";
		String szYD_MTL_ITEM	=""; 
		String szSTL_PROG_CD	=""; 
		String szYD_AIM_YD_GP	=""; 

		double  iW_GP              = 0; 
		double  iOUTDIA            = 0;
		String sW_GP            ="";
		String sOUTDIA          ="";		
		JDTORecord recEditRec1		 = JDTORecordFactory.getInstance().create();
		
		try{
			// 야드재료품목
			szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP");
//			if(szSTL_APPEAR_GP.equals("E")){
//				szYD_MTL_ITEM = "CM";
//				szYD_AIM_YD_GP= "H";
//			}else if(szSTL_APPEAR_GP.equals("Y")){
//				szYD_MTL_ITEM = "CG";
//				szYD_AIM_YD_GP= "J";
//			}
			// 야드목표행선구분
			// 야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			recEditRec1		 = JDTORecordFactory.getInstance().create();
			recEditRec1.setField("STL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			recEditRec1.setField("CURR_PROG_CD",	ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			
			rVal = YdCommonUtils.getYdAimRtGp("C", recEditRec1);

			//실적발생 후 진도코드에 따른 목표야드 변경 작업
			if(rVal[1].equals("H") ||rVal[1].equals("G") ||rVal[1].equals("F")){
				szYD_AIM_YD_GP= "J";
				szYD_MTL_ITEM = "CG";
			}else{
				szYD_AIM_YD_GP= "H";
				szYD_MTL_ITEM = "CM";
			}
			
			ydUtils.putLog(szSessionName, szMethodName, rVal[0], YdConstant.DEBUG);
			szSTL_PROG_CD = ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD");	
			ydUtils.putLog(szSessionName, szMethodName, szSTL_PROG_CD, YdConstant.DEBUG);
			
			recEditRec.setField("STL_NO", 					ydDaoUtils.paraRecChkNull(inRecord,"COIL_NO")); 					
			recEditRec.setField("YD_MTL_T", 				ydDaoUtils.paraRecChkNull(inRecord,"MILL_T")); 		
			recEditRec.setField("YD_MTL_W", 				ydDaoUtils.paraRecChkNull(inRecord,"MILL_W")); 		    
			recEditRec.setField("YD_MTL_L", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_LEN"));		
			recEditRec.setField("YD_MTL_WT", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_WT"));	
			recEditRec.setField("COIL_INDIA", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_INDIA"));	
			recEditRec.setField("COIL_OUTDIA", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_OUTDIA"));	
			recEditRec.setField("PLNT_PROC_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"PLNT_PROC_CD"));				
			recEditRec.setField("ORD_YEOJAE_GP", 			ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));			
			recEditRec.setField("ORD_NO", 					ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO")); 		
			recEditRec.setField("ORD_DTL", 					ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL")); 				
			recEditRec.setField("HYSCO_TRANS_GP", 			ydDaoUtils.paraRecChkNull(inRecord,"HYSCO_TRANS_GP"));		
			recEditRec.setField("CUST_CD", 					ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD")); 			
			recEditRec.setField("DEMANDER_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"DEMANDER_CD"));
			recEditRec.setField("HCR_GP", 					ydDaoUtils.paraRecChkNull(inRecord,"HCR_GP"));	
			recEditRec.setField("COOL_METHOD", 				ydDaoUtils.paraRecChkNull(inRecord,"COOL_METHOD"));
			recEditRec.setField("COOL_DONE_GP", 			ydDaoUtils.paraRecChkNull(inRecord,"COOL_DONE_GP"));
			recEditRec.setField("YD_CONVEYOR_BRANCH_CD", 	ydDaoUtils.paraRecChkNull(inRecord,"BRANCH_CD"));	
			//recEditRec.setField("NEXT_PROC", 				ydDaoUtils.paraRecChkNull(inRecord,"NEXT_PROC"));
			recEditRec.setField("PTOP_PLNT_GP", 			ydDaoUtils.paraRecChkNull(inRecord,"PTOP_PLNT_GP"));				
			recEditRec.setField("ITEMNAME_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"ITEMNAME_CD"));
			recEditRec.setField("STL_APPEAR_GP", 			szSTL_APPEAR_GP);		
			recEditRec.setField("STL_PROG_CD", 				szSTL_PROG_CD);	
			recEditRec.setField("YD_MTL_ITEM", 				szYD_MTL_ITEM);	
			recEditRec.setField("YD_AIM_YD_GP", 			szYD_AIM_YD_GP);	
			recEditRec.setField("YD_AIM_RT_GP", 			rVal[0]);	
			//			폭 구분 
//			iW_GP = Integer.parseInt(ydDaoUtils.paraRecChkNull(inRecord,"MILL_W"));
			iW_GP = ydDaoUtils.paraRecChkNullDouble(inRecord,"SHEAR_W");
			ydUtils.putLog(szSessionName, szMethodName, ""+iW_GP, YdConstant.DEBUG);
			
			if (iW_GP < 1601) {
				sW_GP = "M";
			} else {
				sW_GP = "L";
			}
			recEditRec.setField("YD_MTL_W_GP"		 , sW_GP);
			ydUtils.putLog(szSessionName, szMethodName, sW_GP, YdConstant.DEBUG);
//외경그룹			
//			iOUTDIA = Integer.parseInt(ydDaoUtils.paraRecChkNull(inRecord,"COIL_OUTDIA"));
			iOUTDIA = ydDaoUtils.paraRecChkNullDouble(inRecord,"COIL_OUTDIA");
			ydUtils.putLog(szSessionName, szMethodName, ""+iOUTDIA, YdConstant.DEBUG);
			if (iOUTDIA <= 1280) {
				sOUTDIA = "A";
			} else if (( iOUTDIA > 1280 )&&( iOUTDIA <= 1930)) { 
				sOUTDIA = "B";
			} else if ( iOUTDIA > 1930 ) {
				sOUTDIA = "C";
			}
			recEditRec.setField("YD_COIL_OUTDIA_GRP_GP"		 , sOUTDIA);
			ydUtils.putLog(szSessionName, szMethodName, sOUTDIA, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "편집완료", YdConstant.DEBUG);
			
		
		} catch(Exception e){

			return -1;
		}
		return 1;

	} //end of editCHrMillWrkWr()
	
	
	
	
	
	/**
	 * C열연 정정작업실적 (HRYDJ007)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCHrShearWrkWr(JDTORecord inRecord)throws JDTOException  {
		// DAO객체 선언
		YdStockDao ydStockDao     = new YdStockDao();
		YdStkLyrDao ydStkLyrDao   = new YdStkLyrDao();

		// 레코드 선언
		JDTORecord recPara        = null;
		JDTORecord recEditRec     = null;
		JDTORecordSet rsResult    = null;
		JDTORecordSet rsResult1    = null;
		JDTORecord recGetComm     = null;
		JDTORecord recGetStoc     = null;
		JDTORecord recGetStoc1     = null;
		JDTORecord recGetVal      = null;
		JDTORecord inRecord1      = null;
		JDTORecord outRecord1     = null;

		String szMethodName       = "procCHrShearWrkWr";
		String szMsg              = "";
		String szOperationName    = "C열연 정정작업실적";
		String szRcvTcCode        = "";
		String szSTL_NO           = "";
		String szYD_AIM_RT_GP     = "";
		String szMMATL_FEE_NO     = "";                          // 모재료 번호
		String szCOIL_DIVIDERE_GP = "";                          // 코일 분할재 구분
		String sPARA_ERR_CONTENTS = "";
		int nRet2 			 	  = 0;	
		int nRet                  = 0;
		int intRtnVal             = 0;
		
		double  iW_GP              = 0; 
		double  iOUTDIA            = 0;
		String sW_GP              = "";
		String sOUTDIA            = "";		
		String szLOC_CHANGE_YN	  = "";
		String szLOC_CHANGE_COIL_NO	  = "";
		String sYD_BAY_GP = "A";
		// 전문받아서 szRcvTcCode에 저장
		szRcvTcCode = ydUtils.getTcCode(inRecord);
		if(szRcvTcCode == null){
			szMsg = "[ERROR] " + szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			
			Thread.sleep(1000);		//야드 보급취소 요청 TC 호출 후 자체 재조회 로직 시간 대기.(1초 여유)
			
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[열연조업] 정정작업실적 수신";
			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
		
			// Debug Msg
			ydUtils.putLog(CoilSpecRegSeEJBBean.class.getName(), szMethodName, "\n============ 수신전문 출력 ===============\n", 4);
			ydUtils.displayRecord(szOperationName, inRecord);
			ydUtils.putLog(CoilSpecRegSeEJBBean.class.getName(), szMethodName, "\n=======================================\n", 4);

			szSTL_NO 				= ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");
			szYD_AIM_RT_GP 			= ydDaoUtils.paraRecChkNull(inRecord, "YD_AIM_RT_GP");
			szLOC_CHANGE_YN 		= ydDaoUtils.paraRecChkNull(inRecord, "LOC_CHANGE_YN");  //야드위치 변경대상 여부(Y일 경우 야드에서 해당)
			szLOC_CHANGE_COIL_NO 	= ydDaoUtils.paraRecChkNull(inRecord, "LOC_CHANGE_COIL_NO"); //야드위치 변경대상 코일(모코일)
			
 
			///////////////////////////////////////////////////////////////////////////////////////////////////////
			
			if("Y".equals(szLOC_CHANGE_YN)){
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO" 			, szSTL_NO);
				recPara.setField("MODIFIER"      		, "HRYDJ007");
				recPara.setField("LOC_CHANGE_COIL_NO" 		, szLOC_CHANGE_COIL_NO); 				
				
				//수정
				commDao.update(recPara, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrChangeStlNo", "HRYDJ007", szMethodName, "HRYDJ007 모코일 대체 작업");
				
				commDao.update(recPara, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updCoilcommChangeStlNo", "HRYDJ007", szMethodName, "HRYDJ007 모코일 대체 작업2");
			}
			
			
			
			
			JDTORecordSet   outRecSet   = JDTORecordFactory.getInstance().createRecordSet("YD");
			inRecord1 = JDTORecordFactory.getInstance().create();
			inRecord1.setField("STL_NO",   szSTL_NO);

			//=====================================================
			// 혹시 있을 저장위치 삭제
			//=====================================================
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkPosInfo_PIDEV*/

			intRtnVal = ydStkLyrDao.getYdStklyr(inRecord1, outRecSet, 24);
			if (intRtnVal > 0) {
				//적치되어 있는 정보 삭제처리
				outRecSet.first();
				outRecord1 = outRecSet.getRecord();
				//UPDATE 항목 record  생성
				recPara = JDTORecordFactory.getInstance().create();
				
				//적치단 재료상태가 적치 가능이면 재료 등록
				//적치단 테이블 업데이트
				//적치열구분 = 설비ID
				String sYD_GP = ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_COL_GP").substring(0, 1);
				sYD_BAY_GP = ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_COL_GP").substring(1, 2);
				String sYD_EQP_GP = ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_COL_GP").substring(2, 4);
				recPara.setField("YD_STK_COL_GP" 		, ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_COL_GP"));
				recPara.setField("YD_STK_BED_NO" 		, ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_BED_NO"));
				recPara.setField("YD_STK_LYR_NO" 		, ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_LYR_NO"));
				recPara.setField("MODIFIER"      		, "HRYDJ007");
				recPara.setField("YD_STK_LYR_MTL_STAT"	, "E");
				recPara.setField("STL_NO"				,  "");
				
				
				if(sYD_GP.equals("J") ){
					szMsg = "코일야드 제품장 코일에 대한 HRYDJ007실적 처리 skip";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);							
					
					sPARA_ERR_CONTENTS = "코일야드 제품장 코일에 대한 HRYDJ007실적 처리 skip";
//					m_ctx.setRollbackOnly();	//롤백 처리
//					throw new DAOException(getClass().getName() + "USER_ERR_MSG:" + sPARA_ERR_CONTENTS);
				}else{
 
					//코일위치가 설비가 아닌 경우 SKIP
					if(	!"DD".equals(sYD_EQP_GP) && 
						!"DE".equals(sYD_EQP_GP) && 
						!"FD".equals(sYD_EQP_GP) && 
						!"FE".equals(sYD_EQP_GP) && 
						!"KD".equals(sYD_EQP_GP) && 
						!"KE".equals(sYD_EQP_GP)){
						szMsg = "코일야드 설비위치가 아닌 코일에 대한 저장위치 수정  skip";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
					}else{
							
						if(sYD_BAY_GP.equals("F") || sYD_BAY_GP.equals("D") || (sYD_EQP_GP.equals("FE") && sYD_BAY_GP.equals("B"))){
							szMsg = "코일야드 결속장 인 경우   skip";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						} else {
							//업데이트 실행
							//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo
							intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 303);
							if(intRtnVal < 1) {
								sPARA_ERR_CONTENTS = "저장위치 삭제 실패";
								m_ctx.setRollbackOnly();	//롤백 처리
								throw new DAOException(getClass().getName());
								
							}
						}	
					}
				}
			} 
			
			
			
			
			/*
			 * 2019.03.17
			 * 소재통로를 통하여 냉연 소재이송이 되도록 처리 하는 부분 로직
			 * 0. 입고 대기 인경우 
			 * 1. 저장위치를 H ->J 야드로 변경 작업
			 * 2. 기존 H야드 저장위치 삭제 및 맵 비활성화
			 * 
			 */
			
			//#################################################################################################
			//#################################################################################################
			String sCHK_YN    = "N";
			String sCURR_PROG_CD    = "";
			String sPut_Position   ="";
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			YdCarSchDao     ydCarSchDao     = new YdCarSchDao();
			JDTORecordSet getRecSet1 = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			jrParam.setField("YD_BAY_GP"    , sYD_BAY_GP   ); //동정보
			jrParam.setField("STL_NO"    , szSTL_NO   ); //코일번호 
			
			JDTORecordSet jsSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			jsSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYML007ChkYN", "SYSTEM", szMethodName, "소재통로냉연이송여부 조회");
			
			if (jsSch.size() > 0) {
				/**********************************************************
				* 해당동 사용여부
				**********************************************************/
				sCHK_YN    		= commUtils.trim(jsSch.getRecord(0).getFieldString("CHK_YN"   )); 
				sCURR_PROG_CD   = commUtils.trim(jsSch.getRecord(0).getFieldString("CURR_PROG_CD"   )); 
				sPut_Position	= commUtils.trim(jsSch.getRecord(0).getFieldString("YD_STR_LOC"   )); 
			}
			 
			szMsg = "HRYDJ007실적소재통로냉연이송여부:"+sCHK_YN+" , 진도코드:"+sCURR_PROG_CD;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
			
			if("Y".equals(sCHK_YN)){
				
				if("H".equals(sCURR_PROG_CD)|| "2".equals(sCURR_PROG_CD)){
					
					//2-1: 입고 대기재에 대하여 저장위치를 소재야드 -> 제품야드로 변경 #############################################
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO" 			, szSTL_NO);
					recPara.setField("MODIFIER"      		, "HRYDJ007"); 				
					
					//수정
					commDao.update(recPara, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrChange", "HRYDJ007", szMethodName, "소재야드에서 제품야드로 변경");
					//#################################################################################################
					
					
					
					//2-2: 코일공통 저장위치 변경  (소재야드 -> 제품야드)###################################################### 
					recPara.setField("COIL_NO",       			szSTL_NO); 
					recPara.setField("YD_GP",       			sPut_Position.substring(0,1));   
					recPara.setField("YD_GP",       			sPut_Position.substring(0,1));   
					recPara.setField("YD_BAY_GP",       		sPut_Position.substring(1,2));   
					recPara.setField("YD_EQP_GP",       		sPut_Position.substring(2,4)); 
					recPara.setField("YD_STK_COL_NO",       	sPut_Position.substring(4,6));   
					recPara.setField("YD_STK_BED_NO",       	sPut_Position.substring(6,8)); 
					recPara.setField("YD_STK_LYR_NO", 			sPut_Position.substring(8,10)) ;
								 
	    	        //트렌젝션 분리
	    	        EJBConnector ejbConn = new EJBConnector("default", "CoilCraneUdHdSeEJB", this);
	    	        ejbConn.trx("Y5SetYdStrLocCoil", new Class[] { JDTORecord.class }, new Object[] { recPara });	    	        
	    	        
	    	        //#################################################################################################
	    	        
	    	        
	    	        
	    	        //2-3: 진행관리 실적전송 송신#####################################################################
	    	        szMsg = "▼▼▼▼▼[" + szOperationName + "]  진행관리 실적전송 송신 START ▼▼▼▼▼";
		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		            		 
					
					getRecSet1 = JDTORecordFactory.getInstance().createRecordSet(""); 
					/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschYdCarMtlCoilComm*/
					intRtnVal = ydCarSchDao.getYdCarsch(recPara, getRecSet1, 305);	
					if(intRtnVal < 0) {
						szMsg = "COIL공통 테이블 조회오류  [Ret : " + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
					} else if(intRtnVal == 0) {
						szMsg = "COIL공통 테이블 조회건수 없음  [Ret : " + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
					} else{				
					
						getRecSet1.first();
						recGetVal = getRecSet1.getRecord(0);	
						
						recInTemp 	= JDTORecordFactory.getInstance().create();
			
						recInTemp.setField("JMS_TC_CD"				, "YDPTJ002");
						recInTemp.setField("JMS_TC_CREATE_DDTT"		, YdUtils.getCurDate("yyyyMMddHHmmss"));				
						recInTemp.setField("STL_NO"					, szSTL_NO.trim()); // 재료번호
						recInTemp.setField("ORD_NO"					, ydDaoUtils.paraRecChkNull(recGetVal, "ORD_NO")); // 주문번호
						recInTemp.setField("ORD_DTL"				, ydDaoUtils.paraRecChkNull(recGetVal, "ORD_DTL"));  // 주문행번
						recInTemp.setField("PLNT_PROC_CD"			, ydDaoUtils.paraRecChkNull(recGetVal, "PLNT_PROC_CD")); // 공장공정코드
						recInTemp.setField("STL_APPEAR_GP"			, ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));  // 재료외형구분
						recInTemp.setField("CURR_PROG_CD"			, ydDaoUtils.paraRecChkNull(recGetVal, "CURR_PROG_CD"));   // 현재진도코드
						recInTemp.setField("ORD_YEOJAE_GP"			, ydDaoUtils.paraRecChkNull(recGetVal, "ORD_YEOJAE_GP"));  // 주문여재구분
						recInTemp.setField("STL_WT"					, ydDaoUtils.paraRecChkNull(recGetVal, "COIL_WT"));   // 재료중량 (COIL중량) 
						recInTemp.setField("DS_MTL_WT"				, "");		// 설계재료중량
						recInTemp.setField("MTL_STAT_GP"			, ydDaoUtils.paraRecChkNull(recGetVal, "MTL_STAT_GP")); // 재료상태구분
						recInTemp.setField("RECORD_END_GP"			, ydDaoUtils.paraRecChkNull(recGetVal, "RECORD_END_GP")); // Record 종료구분
						recInTemp.setField("RECORD_END_GP1"			, "");
						recInTemp.setField("BEFO_PROG_CD"			, ydDaoUtils.paraRecChkNull(recGetVal, "BEFO_PROG_CD")); // 전진도 코드
						recInTemp.setField("BEF_ORD_NO"				, ydDaoUtils.paraRecChkNull(recGetVal, "BEF_ORD_NO"));	// 전주문 번호
						recInTemp.setField("BEF_ORD_DTL"			, ydDaoUtils.paraRecChkNull(recGetVal, "BEF_ORD_DTL"));	// 전주문 행번
						recInTemp.setField("MMATL_FEE_NO"			, ydDaoUtils.paraRecChkNull(recGetVal, "MMATL_FEE_NO"));	// 모재료번호   
						recInTemp.setField("ORDERTRANS_MATCH_GP"	, ydDaoUtils.paraRecChkNull(recGetVal, "MATCH_ORDERTRANS_GP"));	// 목전충당구분
						
						this.sndJMSInfo2(recInTemp);
					
					}
					
					szMsg = "▲▲▲▲▲[" + szOperationName + "]  진행관리 실적전송 송신 END ▲▲▲▲▲";
		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);  
		            //#################################################################################################
		            
					
					//2-4: 입고 대기재에 대하여 소재야드 맵 비활성화  및 재료 삭제 처리################################################ 
					commDao.update(recPara, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdHStklyrChange", "HRYDJ007", szMethodName, "소재야드 맵 비활성화 변경");
					
					//#################################################################################################
					
					
					//2-5: 출하로 입고 TC 전송 ##############################################################
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    //코일입고작업실적
					JDTORecord tcRecordDM = null;
					tcRecordDM = JDTORecordFactory.getInstance().create(); 
					tcRecordDM.setField("GOODS_NO",szSTL_NO);
					tcRecordDM.setField("YD_GP","J");
					tcRecordDM.setField("STORE_LOC",sPut_Position);
					tcRecordDM.setField("CURR_PROG_CD",sCURR_PROG_CD);
					
					//인터페이스 전문 호출
					EJBConnector ejbConn2 = new EJBConnector("default","JNDIYardWrkResReg",this);
					ejbConn2.trx("getYDDMR001",new Class[]{JDTORecord.class},  new Object[]{tcRecordDM});  
					
                    szMsg = "HRYDJ007실적 출하입고TC 전송:"+sCHK_YN+" , 진도코드:"+sCURR_PROG_CD;
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        			//#################################################################################################
        			
        			
				}else{
					
					szMsg = "HRYDJ007실적 해당 진도가 입고대기재가 아님: "+sCURR_PROG_CD ;
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}								
				
			}else{
				szMsg = "HRYDJ007실적 해당동 냉연 소재이송 불가: "+sCHK_YN ;
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			//#################################################################################################
			//#################################################################################################
			
			
			
			
			
			
				
			//=====================================================
			// 저장품 조회
			//=====================================================
			recPara  = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara.setField("COIL_NO", szSTL_NO); 
			recPara.setField("STL_NO", szSTL_NO); 
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStock_PIDEV*/
			nRet = ydStockDao.getYdStock(recPara, rsResult, 0);
			

			
			if(nRet < 0){
				szMsg = "YD_STOCK[저장품] SELECT Error [" + nRet + "] (" +  szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				sPARA_ERR_CONTENTS = szMsg;
//				m_ctx.setRollbackOnly();	//롤백 처리
//				throw new DAOException(getClass().getName());

			} else if(nRet > 0) {
				//#################################### 자코일 실적 처리 ####################################################
				
				rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");				
				/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCOILCOMM*/
				nRet2 = ydStockDao.getYdStock(recPara, rsResult1, 8);
				
				
				szMsg = "YD_STOCK[저장품] SELECT [" + nRet2 + "] 전문으로 받은 재료번호 (" + szSTL_NO + ")가 저장품 테이블에 존재함  (입측 1개 => 출측 1개)";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				rsResult1.first();
				recGetStoc1 = JDTORecordFactory.getInstance().create();
				recGetStoc1 = rsResult1.getRecord();
				
//				szCOIL_DIVIDERE_GP 	= ydDaoUtils.paraRecChkNull(recGetStoc, "COIL_DIVIDERE_GP");
//				szMMATL_FEE_NO 		= ydDaoUtils.paraRecChkNull(recGetStoc, "MMATL_FEE_NO");
//폭 구분 
//				iW_GP 				= Integer.parseInt(ydDaoUtils.paraRecChkNull(recGetStoc,"YD_MTL_W"));
//				iW_GP = Double.parseDouble(ydDaoUtils.paraRecChkNull(inRecord,"YD_MTL_W"));
				iW_GP = ydDaoUtils.paraRecChkNullDouble(recGetStoc1,"SHEAR_W");
				
				if (iW_GP < 1601) {
					sW_GP = "M";
				} else {
					sW_GP = "L";
				}
				recPara.setField("YD_MTL_W_GP"	, sW_GP);
				
//외경그룹			
				//iOUTDIA 			= Integer.parseInt(ydDaoUtils.paraRecChkNull(recGetStoc,"COIL_OUTDIA"));
				iOUTDIA = ydDaoUtils.paraRecChkNullDouble(recGetStoc1,"COIL_OUTDIA");
				
				if (iOUTDIA <= 1280) {
					sOUTDIA = "A";
				} else if (( iOUTDIA > 1280 )&&( iOUTDIA <= 1930)) { 
					sOUTDIA = "B";
				} else if ( iOUTDIA > 1930 ) {
					sOUTDIA = "C";
				}
				recPara.setField("YD_COIL_OUTDIA_GRP_GP"		 , sOUTDIA);
				
				// ===저장품에 Update 할 항목Set==================//
				// 야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal = YdCommonUtils.getYdAimRtGp("C", recPara);
				
				//판정보류,종합판정대기,입고인 경우 제품장으로 적치
				if (rVal[1].equals("F") || 
					rVal[1].equals("G") || 
					rVal[1].equals("H")	){ 
					recPara.setField("YD_AIM_YD_GP", 	"J");
				} else {
					recPara.setField("YD_AIM_YD_GP", 	"H");						
				}
				
				recPara.setField("YD_MTL_W", 		ydDaoUtils.paraRecChkNull(recGetStoc1,"SHEAR_W"));
				recPara.setField("COIL_OUTDIA", 	ydDaoUtils.paraRecChkNull(recGetStoc1,"COIL_OUTDIA"));
				recPara.setField("YD_AIM_RT_GP", 	rVal[0]);
				recPara.setField("STL_PROG_CD", 	rVal[1]);
				recPara.setField("MODIFIER", 		"HRYDJ007");
				
				nRet = ydStockDao.updYdStock(recPara, 0);
				if(nRet <= 0){
					szMsg = "YD_STOCK[C열연 정정작업실적수신] UPDATE Error :: [" + nRet + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					sPARA_ERR_CONTENTS = szMsg;
					m_ctx.setRollbackOnly();	//롤백 처리
					throw new DAOException(getClass().getName());
				}
				szMsg = "[" + recPara.getFieldString("STL_NO") + "] :: YD_STOCK [C열연 정정작업실적수신]UPDATE Success";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	

				//======================================================
				// 저장품제원 : 코일야드L2로 송신(YDY5L002)---> SJH 추가
				//======================================================
				JDTORecord recResult = null;
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YDY5L002");
				recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
				recResult.setField("STL_NO"         , szSTL_NO);
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				
				ydDelegate.sendMsg(recResult);

				szMsg = "코일야드L2로 응답전문 [YDY5L002] 전송";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);							
				
				return ;
			}
//			#################################### 모코일 실적 처리 ####################################################
			szMsg = "YD_STOCK[저장품] 조회 [" + nRet + "] 하였으나 재료번호 (" + szSTL_NO + ")의 데이터는 존재하지 않음 (입측 1개 => 출측 n개분할)";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			
			//=====================================================
			// COIL 공통 조회
			//=====================================================
			recPara  = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara.setField("COIL_NO", szSTL_NO);
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCOILCOMM*/
			nRet = ydStockDao.getYdStock(recPara, rsResult, 8);
			if(nRet < 0){
				szMsg = "COILCOMM[코일공통] Error :: [" + nRet + "] (" + szSTL_NO + ") PARAMETER ERROR" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				sPARA_ERR_CONTENTS = szMsg;
				m_ctx.setRollbackOnly();	//롤백 처리
				throw new DAOException(getClass().getName());

			} else if(nRet == 0){
				szMsg = "COILCOMM[코일공통] Error :: [" + nRet + "] (" + szSTL_NO + ") DO NOT EXIST";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				sPARA_ERR_CONTENTS = szMsg;
				m_ctx.setRollbackOnly();	//롤백 처리
				throw new DAOException(getClass().getName());
			}

			szMsg = "COILCOMM[코일공통] 조회 SUCCESSFULL [" + nRet + "] 분할 재료번호 (" + szSTL_NO + ")의 정보를 가져옴" ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			rsResult.first();
			recGetComm = JDTORecordFactory.getInstance().create();
			recGetComm = rsResult.getRecord();
			
			szCOIL_DIVIDERE_GP 	= ydDaoUtils.paraRecChkNull(recGetComm, "COIL_DIVIDERE_GP");
			szMMATL_FEE_NO 		= ydDaoUtils.paraRecChkNull(recGetComm, "MMATL_FEE_NO");
							
			//=====================================================
			// 저장품 조회 (모재료번호 조회)
			//=====================================================
			recPara  = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara.setField("STL_NO", szMMATL_FEE_NO);
			nRet = ydStockDao.getYdStock(recPara, rsResult, 0);
			if(nRet < 0){
				szMsg = "YD_STOCK[저장품] SELECT Error [" + nRet + "] 모재료번호 (" + szMMATL_FEE_NO + ")" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				sPARA_ERR_CONTENTS = szMsg;
				m_ctx.setRollbackOnly();	//롤백 처리
				throw new DAOException(getClass().getName());
			} else if(nRet == 0){
				szMsg = "YD_STOCK[저장품] SELECT [" + nRet + "] 모재료번호 (" + szMMATL_FEE_NO + ") 가 저장품에 없음" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				// 모재료번호가 저장품에 존재하든 안하든 자재료번호의 정보는 처리 되어야 함
			} else {
				szMsg = "YD_STOCK[저장품] 조회 [" + nRet + "] SUCCESSFULL 모재료번호(" + szMMATL_FEE_NO + ") 가 존재";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				rsResult.first();
				recGetVal = rsResult.getRecord();		
				
				//=====================================================
				// 저장품 업데이트 (모재료번호 정보 삭제)
				//=====================================================
				recGetVal.setField("DEL_YN", "Y");
				recGetVal.setField("MODIFIER", "HRYDJ007");
				nRet = ydStockDao.updYdStock(recGetVal, 0);
				if(nRet <= 0){
					szMsg = "YD_STOCK[저장품] UPDATE Error [" + nRet + "] 모재료번호(" + szMMATL_FEE_NO +")의 데이터삭제(DEL_YN) 실패 " ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					sPARA_ERR_CONTENTS = szMsg;
					m_ctx.setRollbackOnly();	//롤백 처리
					throw new DAOException(getClass().getName());
				}		
				
				szMsg = "YD_STOCK[저장품] UPDATE SUCCESSFULL 모재료번호(" + szMMATL_FEE_NO + ") 정보(DEL_YN)를 삭제";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}
			
			//=====================================================
			// 항목편집
			//=====================================================
			recEditRec  = JDTORecordFactory.getInstance().create();
			nRet 		= this.editCHrShearWrkWr(recGetComm, recEditRec);
			if(nRet < 0){
				szMsg = "항목편집 Error [" + nRet + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
				sPARA_ERR_CONTENTS = szMsg;
				m_ctx.setRollbackOnly();	//롤백 처리
				throw new DAOException(getClass().getName());
			}

			szMsg = "항목편집 SUCCESSFULL";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				

			//=====================================================
			// 저장품 생성
			//=====================================================
			recEditRec.setField("REGISTER", "HRYDJ007");
			nRet = ydStockDao.insYdStock(recEditRec);
			if(nRet < 0){
				szMsg = "YD_STOCK[저장품] INSERT ERROR :: [" + nRet + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				sPARA_ERR_CONTENTS = szMsg;
				m_ctx.setRollbackOnly();	//롤백 처리
				throw new DAOException(getClass().getName());
			}

			szMsg = "YD_STOCK[저장품] INSERT SUCCESSFULL (" + ydDaoUtils.paraRecChkNull(recEditRec, "STL_NO") + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);							

			//======================================================
			// 저장품제원 : 코일야드L2로 송신(YDY5L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY5L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recEditRec, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			
			ydDelegate.sendMsg(recResult);

			szMsg = "코일야드L2로 응답전문 [YDY5L002] 전송";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);							

		}catch(Exception e){
			szMsg="[C열연정정작업실적수신] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + "USER_ERR_MSG:" + sPARA_ERR_CONTENTS);
		}
		
		szMsg="C열연정정작업실적수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procCHrShearWrkWr

	
	/**
	 * [C열연정정작업실적수신] 항목 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException 
	 */
	public int editCHrShearWrkWr(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {

		String szMethodName	=	"editCHrShearWrkWr";
		String szMsg="";
		String szSTL_APPEAR_GP 	="";
		String szYD_MTL_ITEM	=""; 
		String szSTL_PROG_CD	=""; 
		String szYD_AIM_YD_GP	="";
		String szBRANCH_CD		="";
		String szNEXT_PROC		="";
		String szYD_AIM_RT_GP	="";
		String szYD_AIM_BAY_GP  ="";
		double  iW_GP              = 0; 
		double  iOUTDIA            = 0;
		String sW_GP            ="";
		String sOUTDIA          ="";
		String sYD_AIM_RT_GP2   ="";
		String sSCRAP_CAUSE_CD   ="";
		String sEQP_CD   ="";
		try{
			// 야드재료품목
			szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP");
			if(szSTL_APPEAR_GP.equals("E")){
				szYD_MTL_ITEM = "CM";
				szYD_AIM_YD_GP= "H";
			}else if(szSTL_APPEAR_GP.equals("Y")){
				szYD_MTL_ITEM = "CG";
				szYD_AIM_YD_GP= "J";
			}
			// 야드목표행선구분
			szNEXT_PROC   	= ydDaoUtils.paraRecChkNull(inRecord,"NEXT_PROC");
			szBRANCH_CD   	= ydDaoUtils.paraRecChkNull(inRecord,"BRANCH_CD");
			szSTL_PROG_CD 	= ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD");
			sYD_AIM_RT_GP2	= ydDaoUtils.paraRecChkNull(inRecord, "YD_AIM_RT_GP2");
			sSCRAP_CAUSE_CD	= ydDaoUtils.paraRecChkNull(inRecord, "SCRAP_CAUSE_CD");
			sEQP_CD			= ydDaoUtils.paraRecChkNull(inRecord, "EQP_CD");
			
			if(szSTL_PROG_CD.equals("G")){
				szYD_AIM_RT_GP = "G2";
				szYD_AIM_YD_GP= "J";
			}else if(szSTL_PROG_CD.equals("F")){
				szYD_AIM_RT_GP = "F3";
				szYD_AIM_YD_GP= "J";
			}else if(szSTL_PROG_CD.equals("H")){
				szYD_AIM_RT_GP = "H2";
				szYD_AIM_YD_GP= "J";
			}else if(szSTL_PROG_CD.equals("K")){
				szYD_AIM_RT_GP = "K2";
				szYD_AIM_YD_GP= "J";
			}else if(szSTL_PROG_CD.equals("C")){
				
				if(!szNEXT_PROC.equals("")){
					if(szNEXT_PROC.equals("HH")){
						szYD_AIM_RT_GP = "CF";
						szYD_AIM_YD_GP= "H";
					}
				}else{
					if(szBRANCH_CD.equals("3S")||szBRANCH_CD.equals("HS")){
						szYD_AIM_RT_GP = "CF";						
					}else if(szBRANCH_CD.equals("GH")){
						szYD_AIM_RT_GP = "CE";	
					}else if(szBRANCH_CD.equals("EH")){
						szYD_AIM_RT_GP = "CG";	
					}else if(szBRANCH_CD.equals("FH")){
						szYD_AIM_RT_GP = "CH";	
					}else if(szBRANCH_CD.equals("DH")){
						szYD_AIM_RT_GP = "CI";	
					}
					szYD_AIM_YD_GP= "H";
				}
				if(sYD_AIM_RT_GP2.equals("F4") || sYD_AIM_RT_GP2.equals("F5")) {   		//재작업인 경우 
					szYD_AIM_RT_GP = sYD_AIM_RT_GP2;										   //재작업인(C열연정정)
					szYD_AIM_YD_GP= "H";
				}				
				
			}

			//차공정으로 목표동을 셋팅함.
			if(!szNEXT_PROC.equals("")){
				szYD_AIM_BAY_GP = szNEXT_PROC.substring(0, 1);				
				
			}
			
			//스크랩코일 체크
			if(!sSCRAP_CAUSE_CD.equals("")){
				if(sEQP_CD.equals("SPM1")){
					szYD_AIM_BAY_GP ="H";	
				}else if(sEQP_CD.equals("SPM2")){
					szYD_AIM_BAY_GP ="E";
				}else if(sEQP_CD.equals("SPM3")){
					szYD_AIM_BAY_GP ="C";
				}else if(sEQP_CD.equals("SPM4")){
					szYD_AIM_BAY_GP ="B";
				} 
				
				szYD_AIM_RT_GP = "YC";								
				
			}
			
			recEditRec.setField("STL_NO"			 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_NO")); 					
			recEditRec.setField("YD_MTL_T"			 , ydDaoUtils.paraRecChkNull(inRecord,"SHEAR_T")); 		
			recEditRec.setField("YD_MTL_W"			 , ydDaoUtils.paraRecChkNull(inRecord,"SHEAR_W")); 		    
			recEditRec.setField("YD_MTL_L"			 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_LEN"));		
			recEditRec.setField("YD_MTL_WT"			 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_WT"));	
			recEditRec.setField("COIL_INDIA"		 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_INDIA"));	
			recEditRec.setField("COIL_OUTDIA"		 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_OUTDIA"));	
			recEditRec.setField("PLNT_PROC_CD"		 , ydDaoUtils.paraRecChkNull(inRecord,"PLNT_PROC_CD"));				
			recEditRec.setField("ORD_YEOJAE_GP"		 , ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));			
			recEditRec.setField("ORD_NO"			 , ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO")); 			
			recEditRec.setField("ORD_DTL"			 , ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL")); 
			recEditRec.setField("CUST_CD"			 , ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD")); 			
			recEditRec.setField("DEMANDER_CD"		 , ydDaoUtils.paraRecChkNull(inRecord,"DEMANDER_CD"));
			recEditRec.setField("SPEC_ABBSYM"		 , ydDaoUtils.paraRecChkNull(inRecord,"SPEC_ABBSYM"));
			recEditRec.setField("HYSCO_TRANS_GP"	 , ydDaoUtils.paraRecChkNull(inRecord,"HYSCO_TRANS_GP"));		
		  
			recEditRec.setField("HCR_GP"		 	 , ydDaoUtils.paraRecChkNull(inRecord,"HCR_GP"));	
			recEditRec.setField("COOL_METHOD"		 , ydDaoUtils.paraRecChkNull(inRecord,"COOL_METHOD"));
			recEditRec.setField("COOL_DONE_GP"		 , ydDaoUtils.paraRecChkNull(inRecord,"COOL_DONE_GP"));
			recEditRec.setField("YD_CONVEYOR_BRANCH_CD" , szBRANCH_CD);
			recEditRec.setField("PTOP_PLNT_GP"		 , ydDaoUtils.paraRecChkNull(inRecord,"PTOP_PLNT_GP"));				
			recEditRec.setField("OVERALL_STAMP_GRADE", ydDaoUtils.paraRecChkNull(inRecord,"OVERALL_STAMP_GRADE"));
			recEditRec.setField("STL_PROG_CD"		 , szSTL_PROG_CD);
			recEditRec.setField("STL_APPEAR_GP"		 , szSTL_APPEAR_GP);
			recEditRec.setField("YD_MTL_ITEM"		 , szYD_MTL_ITEM);
			recEditRec.setField("YD_AIM_YD_GP"		 , szYD_AIM_YD_GP);
			recEditRec.setField("YD_AIM_RT_GP"		 , szYD_AIM_RT_GP);
			recEditRec.setField("YD_AIM_BAY_GP"      , szYD_AIM_BAY_GP);
			
			
//			폭 구분 
			//iW_GP = Integer.parseInt(ydDaoUtils.paraRecChkNull(inRecord,"SHEAR_W"));
			
			iW_GP = ydDaoUtils.paraRecChkNullDouble(inRecord,"SHEAR_W");
			
			if (iW_GP < 1601) {
				sW_GP = "M";
			} else {
				sW_GP = "L";
			}
			recEditRec.setField("YD_MTL_W_GP"		 , sW_GP);
			
//외경그룹			
			//iOUTDIA = Integer.parseInt(ydDaoUtils.paraRecChkNull(inRecord,"COIL_OUTDIA"));
			iOUTDIA = ydDaoUtils.paraRecChkNullDouble(inRecord,"COIL_OUTDIA");
			
			if (iOUTDIA <= 1280) {
				sOUTDIA = "A";
			} else if (( iOUTDIA > 1280 )&&( iOUTDIA <= 1930)) { 
				sOUTDIA = "B";
			} else if ( iOUTDIA > 1930 ) {
				sOUTDIA = "C";
			}
			recEditRec.setField("YD_COIL_OUTDIA_GRP_GP"		 , sOUTDIA);
			
			
			
		} catch(Exception e){
			return -1;
		}
		return 1;
	} //end of editCHrShearWrkWr()
	

	

	/**
	 * 코일제품반품(DMYDR033)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilGdsRetngds(JDTORecord inRecord)throws JDTOException  {

		String szMethodName 			= "procCoilGdsRetngds";
		String szMsg 					= "";
		String szOperationName  		= "코일제품반품";
		String szSTL_NO					= "";
		String szDIST_GOODS_GP  		= "";
		String szOLD_TRANS_WORD_DATE  	= "";
		String szOLD_TRANS_WORD_SEQNO  	= "";
		String szNEW_TRANS_WORD_DATE  	= "";
		String szNEW_TRANS_WORD_SEQNO  	= "";
		String szYD_CAR_SCH_ID		  	= "";		
		String 	szRtnMsg				= null;
		
		JDTORecord recPara               = null; 	 
		JDTORecordSet rsResult           = null; 

		
		int intRtnVal 					 = 0;
		int nRet                         = 0;
 
		JDTORecord		recTemp			 = null;
		JDTORecord recStockColumn 		 = JDTORecordFactory.getInstance().create();
		YdStockDao ydStockDao 			 = new YdStockDao();
		YdCarFtmvMtlDao ydCarFtmvMtlDao  = new YdCarFtmvMtlDao();
		
 
		
		// 전문받아서 szRcvTcCode에 저장
		String szRcvTcCode = ydUtils.getTcCode(inRecord);

		// 수신한 전문이 null이라면 error
		if(szRcvTcCode == null){

			szMsg = "[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		String sJmsTcCd = StringHelper.evl(inRecord.getFieldString("JMS_TC_CD"), StringHelper.evl(inRecord.getFieldString("TC_CODE"),""));		
		
		ydUtils.displayRecord(szOperationName, inRecord);
		
		
		try{
 
			//수신한 재료번호
			szSTL_NO  = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");
			
			szDIST_GOODS_GP  = ydDaoUtils.paraRecChkNull(inRecord,"DIST_GOODS_GP");
			szOLD_TRANS_WORD_DATE  = ydDaoUtils.paraRecChkNull(inRecord,"OLD_TRANS_WORD_DATE");
			szOLD_TRANS_WORD_SEQNO  = ydDaoUtils.paraRecChkNull(inRecord,"OLD_TRANS_WORD_SEQNO");
			szNEW_TRANS_WORD_DATE  = ydDaoUtils.paraRecChkNull(inRecord,"NEW_TRANS_WORD_DATE");
			szNEW_TRANS_WORD_SEQNO  = ydDaoUtils.paraRecChkNull(inRecord,"NEW_TRANS_WORD_SEQNO");

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
			DIST_GOODS_GP		출하제품구분	H:코일, T:HRPLATE
			OLD_TRANS_WORD_DATE	구운송지시일자
			OLD_TRANS_WORD_SEQNO구운송지시순번
			NEW_TRANS_WORD_DATE	신운송지시일자
			NEW_TRANS_WORD_SEQNO신운송지시순번

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
			recStockColumn.setField("DEL_YN", 				"N");
			recStockColumn.setField("MODIFIER", 			"DMYDR033");

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("C",inRecord );		
			recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
			//****************************************************************************************************

						
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[코일제품반품] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[코일제품반품] UPDATE Success",3);
			//****************************************************************************************************

			
					
			
			//=====================================================================================================
			// 2013.04.11
			// 정종균
			// 1.저장품 운송지시 변경
			// 2.차량스케줄 운송지시 변경
			// 3.검수운송지시 변경 및 재검수 상태로 변경
			//=====================================================================================================
 
			// 레코드생성-----------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DATE", szOLD_TRANS_WORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO", szOLD_TRANS_WORD_SEQNO);

			// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTRANS_ORD_DAT*/
			nRet = ydStockDao.getYdStock(recPara, rsResult, 31);
			if(nRet > 0){
				szMsg = "[코일제품반품(DMYDR033)] 이전 운송지시번호로 변경 대상이 존재 함 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
				
				
				// 레코드생성
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("OLD_TRANS_WORD_DATE" , szOLD_TRANS_WORD_DATE);
				recPara.setField("OLD_TRANS_WORD_SEQNO", szOLD_TRANS_WORD_SEQNO);
				recPara.setField("NEW_TRANS_WORD_DATE" , szNEW_TRANS_WORD_DATE);
				recPara.setField("NEW_TRANS_WORD_SEQNO", szNEW_TRANS_WORD_SEQNO);
				recPara.setField("CHK_GP", "YD");
				
				// 차량스케줄 삭제 및 차량 포인트 클리어
				szMsg = "[코일제품반품(DMYDR033)] 운송지시 변경 작업 시작 - ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szRtnMsg = YdCommonUtils.transOrdChange(recPara);
				
				szMsg = "[코일제품반품(DMYDR033)] 운송지시 변경 작업 완료 - " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
			//---------------------------------------------------------------------------- 
			
			 
			
			//=====================================================================================================
			// 2013.04.11
			// 정종균
			// 1.차량스케줄 재료 삭제
			// 2.검수재료 삭제
			//=====================================================================================================
			
			//차량스케줄ID 조회--------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DATE", 			szNEW_TRANS_WORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO", 		szNEW_TRANS_WORD_SEQNO);
			szRtnMsg		= DaoManager.getYdCarsch(recPara, rsResult, 34);
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szMsg = "["+szOperationName+"] 운송지시일자 :["+szNEW_TRANS_WORD_DATE+"] , 운송지시순번["+szNEW_TRANS_WORD_SEQNO+"]로 차량스케줄 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			}
			
			rsResult.first();
			recTemp		= rsResult.getRecord();
			
			szYD_CAR_SCH_ID	 = ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_SCH_ID");
			//--------------------------------------------------------------------------------
			
			
			//1.차량스케줄 재료 삭제--------------------------------------------------------------
			recPara =  JDTORecordFactory.getInstance().create();			
			recPara.setField("YD_CAR_SCH_ID", 	szYD_CAR_SCH_ID);
			recPara.setField("STL_NO", 			szSTL_NO);
 			
			/*com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.deleteYdCarftmvmtl*/
			nRet = ydCarFtmvMtlDao.updYdCarftmvmtl(recPara, 10);
			//--------------------------------------------------------------------------------
			
			
			//2.검수재료 삭제--------------------------------------------------------------------
			recPara =  JDTORecordFactory.getInstance().create();	
			recPara.setField("TRANS_ORD_DATE", 			szNEW_TRANS_WORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO", 		szNEW_TRANS_WORD_SEQNO);
			recPara.setField("STL_NO", 					szSTL_NO);
 			
			/*com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.deleteYdExaminationmtl*/
			nRet = ydCarFtmvMtlDao.updYdCarftmvmtl(recPara, 11);
			//--------------------------------------------------------------------------------
			
			
			//3.저장품재료 삭제--------------------------------------------------------------------
			recPara =  JDTORecordFactory.getInstance().create();	
			recPara.setField("TRANS_ORD_DATE", 			"");
			recPara.setField("TRANS_ORD_SEQNO", 		"");
			recPara.setField("CAR_NO", 					"");
			recPara.setField("CARD_NO", 				"");
			recPara.setField("YD_STK_BED_NO", 			"");			
			recPara.setField("YD_CAR_UPP_LOC_CD", 		"");
			recPara.setField("STL_NO", 					szSTL_NO);
 			
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStock*/
			nRet = ydStockDao.updYdStock(recPara, 0);
			//--------------------------------------------------------------------------------
			
			
			
			
			//=====================================================================================================
			// 2009.08.31 권오창
			// 저장품제원 : 코일야드L2 로 송신(YDY5L002)
			//=====================================================================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY5L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			ydDelegate.sendMsg(recResult);			
			
		}catch(Exception e){
			szMsg="[코일제품반품]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		} // end of try-catch
		
		szMsg="코일제품반품수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of procCoilGdsRetngds()
	
	
	/**
	 *코일공통 항목을 야드 저장품 항목으로 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtCoilToStock(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {


		String szMethodName	="edtCoilToStock";
		String szMsg ="";
		String szSTL_APPEAR_GP 	="";
		String szYD_MTL_ITEM	=""; 
	
		double  iW_GP              = 0; 
		double  iOUTDIA            = 0;
		String sW_GP            ="";
		String sOUTDIA          ="";		
		

		
		try{
			szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP");
			if(szSTL_APPEAR_GP.equals("E")){
				szYD_MTL_ITEM = "CM";
			}else if(szSTL_APPEAR_GP.equals("Y")){
				szYD_MTL_ITEM = "CG";
			}
						
			recEditRec.setField("STL_NO"			 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_NO")); 					
			recEditRec.setField("YD_MTL_T"			 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_T")); 		
			recEditRec.setField("YD_MTL_W"			 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_W")); 		    
			recEditRec.setField("YD_MTL_L"			 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_LEN"));		
			recEditRec.setField("YD_MTL_WT"			 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_WT"));	
			recEditRec.setField("COIL_INDIA"		 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_INDIA"));	
			recEditRec.setField("COIL_OUTDIA"		 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_OUTDIA"));	
			recEditRec.setField("PLNT_PROC_CD"		 , ydDaoUtils.paraRecChkNull(inRecord,"PLNT_PROC_CD"));				
			recEditRec.setField("STL_PROG_CD"		 , ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));				
			recEditRec.setField("ORD_YEOJAE_GP"		 , ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));			
			recEditRec.setField("ORD_NO"			 , ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO")); 			
			recEditRec.setField("ORD_DTL"			 , ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL")); 
			recEditRec.setField("CUST_CD"			 , ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD")); 			
			recEditRec.setField("DEMANDER_CD"		 , ydDaoUtils.paraRecChkNull(inRecord,"DEMANDER_CD"));
			recEditRec.setField("SPEC_ABBSYM"		 , ydDaoUtils.paraRecChkNull(inRecord,"SPEC_ABBSYM"));
			recEditRec.setField("HYSCO_TRANS_GP"	 , ydDaoUtils.paraRecChkNull(inRecord,"HYSCO_TRANS_GP"));		
		  	recEditRec.setField("HCR_GP"		 	 , ydDaoUtils.paraRecChkNull(inRecord,"HCR_GP"));	
			recEditRec.setField("COOL_METHOD"		 , ydDaoUtils.paraRecChkNull(inRecord,"COOL_METHOD"));
			recEditRec.setField("COOL_DONE_GP"		 , ydDaoUtils.paraRecChkNull(inRecord,"COOL_DONE_GP"));
			recEditRec.setField("YD_CONVEYOR_BRANCH_CD" , ydDaoUtils.paraRecChkNull(inRecord,"BRANCH_CD"));
			recEditRec.setField("PTOP_PLNT_GP"		 , ydDaoUtils.paraRecChkNull(inRecord,"PTOP_PLNT_GP"));				
			recEditRec.setField("OVERALL_STAMP_GRADE", ydDaoUtils.paraRecChkNull(inRecord,"OVERALL_STAMP_GRADE"));
			recEditRec.setField("STL_APPEAR_GP"		 , szSTL_APPEAR_GP);
			recEditRec.setField("YD_MTL_ITEM"		 , szYD_MTL_ITEM);
	
			
//			폭 구분 
//			iW_GP = Integer.parseInt(ydDaoUtils.paraRecChkNull(inRecord,"COIL_W"));
			iW_GP = ydDaoUtils.paraRecChkNullDouble(inRecord,"SHEAR_W");
			if (iW_GP < 1601) {
				sW_GP = "M";
			} else {
				sW_GP = "L";
			}
			recEditRec.setField("YD_MTL_W_GP"		 , sW_GP);
			
//외경그룹			
			//iOUTDIA = Integer.parseInt(ydDaoUtils.paraRecChkNull(inRecord,"COIL_OUTDIA"));
			iOUTDIA = ydDaoUtils.paraRecChkNullDouble(inRecord,"COIL_OUTDIA");
			
			if (iOUTDIA <= 1280) {
				sOUTDIA = "A";
			} else if (( iOUTDIA > 1280 )&&( iOUTDIA <= 1930)) { 
				sOUTDIA = "B";
			} else if ( iOUTDIA > 1930 ) {
				sOUTDIA = "C";
			}
			recEditRec.setField("YD_COIL_OUTDIA_GRP_GP"		 , sOUTDIA);
			
			
			
		} catch(Exception e){

			return -1;
		}
		return 1;

	} //end of edtCoilToStock()
	
	/**
	 *      [A] 나머지 항목 수집
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public int getYdEtcItem(JDTORecord inRecord,JDTORecord outRecord) throws JDTOException {
		String szMethodName 	= "getYdEtcItem";
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
		
		int nRet                = 0;
		
		try{			
			szSTL_APPEAR_GP 		= ydDaoUtils.paraRecChkNull(inRecord, "STL_APPEAR_GP");
			szORD_YEOJAE_GP			= ydDaoUtils.paraRecChkNull(inRecord, "ORD_YEOJAE_GP");
			szSTL_PROG_CD 			= ydDaoUtils.paraRecChkNull(inRecord, "STL_PROG_CD");
			szSLAB_WO_RT_CD 		= ydDaoUtils.paraRecChkNull(inRecord, "SLAB_WO_RT_CD");
			szHCR_GP 				= ydDaoUtils.paraRecChkNull(inRecord, "HCR_GP");
			szSCARFING_YN 			= ydDaoUtils.paraRecChkNull(inRecord, "SCARFING_YN");
			szPTOP_PLNT_GP 			= ydDaoUtils.paraRecChkNull(inRecord, "PTOP_PLNT_GP");
			
			if(szSTL_APPEAR_GP.equals("B")&& szSLAB_WO_RT_CD.startsWith("H")){
				szYD_MTL_ITEM  	= "BH";
			}			
			else if(szSTL_APPEAR_GP.equals("B")&& szSLAB_WO_RT_CD.startsWith("P")){
				szYD_MTL_ITEM  	= "BP";
			}
			//=================================================================================
			// 재표외형구분이 [C]:: [슬라브]일 경우
			//=================================================================================
			if(szSTL_APPEAR_GP.equals("C") && szORD_YEOJAE_GP.equals("1")){
				
				// 재료진도코드 [B]: 지시대기
				if(szSTL_PROG_CD.equals("B")){
					if(szSLAB_WO_RT_CD.equals("HC")){
						szYD_MTL_ITEM  	= "SH";
						szYD_AIM_YD_GP  = "A";
						if(szHCR_GP.equals("H")|| szHCR_GP.equals("W")){	
							szYD_AIM_RT_GP  = "B3";
							szYD_AIM_BAY_GP = "A";
						}else{
							szYD_AIM_RT_GP  = "B4";
							szYD_AIM_BAY_GP = "B";
						}
					}else if(szSLAB_WO_RT_CD.equals("HB")){
						szYD_MTL_ITEM  	= "SH";
						szYD_AIM_YD_GP  = "2";
						if(szHCR_GP.equals("H")|| szHCR_GP.equals("W"))	{
							//야드목표행선구분 [B열연 HCR 지시대기] :: [B1]
							szYD_AIM_RT_GP  = "B1";
							szYD_AIM_BAY_GP = "A";
						}else if(szHCR_GP.equals("C")){
							//야드목표행선구분 [B열연 CCR 지시대기] :: [B2]
							szYD_AIM_RT_GP  = "B2";
							szYD_AIM_BAY_GP = "B";
						}
					}
					else if(szSLAB_WO_RT_CD.equals("PA")){
						szYD_MTL_ITEM = "SP";
						if(szHCR_GP.equals("H")|| szHCR_GP.equals("W"))	{
							//야드목표행선구분 [A후판 HCR 지시대기] :: [B5]
							szYD_AIM_RT_GP  = "B5";
							szYD_AIM_YD_GP  = "D";
							szYD_AIM_BAY_GP = "C";
						}else{
							//야드목표행선구분 [A후판 CCR 지시대기] :: [B6]
							szYD_AIM_RT_GP  = "B6";
							szYD_AIM_YD_GP  = "D";
							szYD_AIM_BAY_GP = "A";
						}

					}
				}
			}
			
			//=================================================================================
			// 재표외형구분이 [B]:: [주편]일 경우
			//=================================================================================
			else if(szSTL_APPEAR_GP.equals("B")){
				//===========================
				// 주문여재구분 [1]:: 
				//===========================
				if(szORD_YEOJAE_GP.equals("1")){
					if(szSTL_PROG_CD.equals("C")){
						szYD_MTL_ITEM  	= "SH";
						
						if(szPTOP_PLNT_GP.equals("HC")){
							// 야드목표행선구분 [C열연 가열로보급대기] :: [C2]
							szYD_AIM_RT_GP  = "C2";
							szYD_AIM_YD_GP  = "A";
							szYD_AIM_BAY_GP = "A";
						}else if(szPTOP_PLNT_GP.equals("HB")){
							// 야드목표행선구분 [B열연 가열로보급대기] :: [C1]
							szYD_AIM_RT_GP  = "C1";
							szYD_AIM_YD_GP  = "2";
							szYD_AIM_BAY_GP = "C";
						}
					}
					else if(szSTL_PROG_CD.equals("D")){
						if(szSLAB_WO_RT_CD.equals("HB")){
							szYD_MTL_ITEM  	= "BH";
							// 야드목표행선구분 [B열연 이송대기] :: [38]
							szYD_AIM_RT_GP  = "E3";
							szYD_AIM_YD_GP  = "2";
							
							// HCR구분 [H] 
							if(szHCR_GP.equals("H")){
								szYD_AIM_RT_GP  = "E1";
								szYD_AIM_BAY_GP = "A";
							}
							// HCR구분 [W] 
							else if(szHCR_GP.equals("W")){
								szYD_AIM_BAY_GP = "D";
							}
							// HCR구분 [C]
							else if(szHCR_GP.equals("C")){
				
								if(szSCARFING_YN.equals("Y")){
									szYD_AIM_BAY_GP = "E";
								}
								else{
									szYD_AIM_BAY_GP = "D";
								}
							}
						}
					}
					else if(szSTL_PROG_CD.equals("A")){
						if(szSLAB_WO_RT_CD.equals("PA")){
							szYD_MTL_ITEM  	= "BP";
							szYD_AIM_RT_GP  = "A4";
							szYD_AIM_YD_GP  = "D";
							if(szSCARFING_YN.equals("Y")){
								szYD_AIM_RT_GP  = "A3";
								szYD_AIM_YD_GP  = "D";
							}
						}
						else if(szSLAB_WO_RT_CD.equals("HC")){
							szYD_MTL_ITEM  	= "BH";
							szYD_AIM_RT_GP  = "A2";
							szYD_AIM_YD_GP  = "H";
							
						}
						else if(szSLAB_WO_RT_CD.equals("HB")){
							szYD_MTL_ITEM  	= "BH";
							szYD_AIM_RT_GP  = "A1";
							szYD_AIM_YD_GP  = "3";
						}
					}
				}
				//===========================
				// 주문여재구분 [2]:: 
				//===========================
				else if(szORD_YEOJAE_GP.equals("2")){
					if(szSLAB_WO_RT_CD.equals("PA")){
						szYD_MTL_ITEM  	= "BP";
						szYD_AIM_RT_GP  = "Y3";
						szYD_AIM_YD_GP  = "A";
						szYD_AIM_BAY_GP = "C";
					}
					else if(szSLAB_WO_RT_CD.equals("HB")){
						szYD_MTL_ITEM  	= "BH";
						szYD_AIM_RT_GP  = "YB";
						szYD_AIM_YD_GP  = "A";
						szYD_AIM_BAY_GP = "C";
					}
					else if(szSLAB_WO_RT_CD.equals("HC")){
						szYD_MTL_ITEM  	= "BH";
						szYD_AIM_RT_GP  = "YC";
						szYD_AIM_YD_GP  = "A";
						szYD_AIM_BAY_GP = "C";
					}
					
				}
			}
			//=================================================================================
			// 재표외형구분이 [E]:: [열연코일]일 경우
			//=================================================================================
			else if(szSTL_APPEAR_GP.equals("E")){
				szYD_MTL_ITEM  	= "CM";
			}
			
		    // 공통항목 편집 한 것(inRecord) + 야드항목(outRecord) = outRecord
			outRecord.addRecord(inRecord);
			
			outRecord.setField("YD_MTL_ITEM"  , szYD_MTL_ITEM);		//야드재료품목
			outRecord.setField("YD_AIM_RT_GP" , szYD_AIM_RT_GP);	//야드목표행선구분
			outRecord.setField("YD_AIM_YD_GP" , szYD_AIM_YD_GP);	//야드목표야드구분
			outRecord.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);   //야드목표동구분
			szSTL_APPEAR_GP 		= ydDaoUtils.paraRecChkNull(inRecord, "STL_APPEAR_GP");
			szORD_YEOJAE_GP			= ydDaoUtils.paraRecChkNull(inRecord, "ORD_YEOJAE_GP");
			szSTL_PROG_CD 			= ydDaoUtils.paraRecChkNull(inRecord, "STL_PROG_CD");
			szSLAB_WO_RT_CD 		= ydDaoUtils.paraRecChkNull(inRecord, "SLAB_WO_RT_CD");
			szHCR_GP 				= ydDaoUtils.paraRecChkNull(inRecord, "HCR_GP");
			szPTOP_PLNT_GP 			= ydDaoUtils.paraRecChkNull(inRecord, "PTOP_PLNT_GP");
		} catch(Exception e) {

			szMsg="Error:"+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}
		return 0;
	}
	
	
	/**
	 * COIL 저장품 등록 수정 삭제 처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param String szSTLNO, int procFlag
	 * @param outRec
	 * @throws JDTOException
	 */
	public void stockProcCom (String szSTL_NO, int procFlag) throws JDTOException {
		/*
		 * procFlag : 1.등록(Insert), 2.수정(Update), 3.삭제(Delete)
		 */
		
		YdStockDao ydStockDao 					= new YdStockDao();
		YdCodeMapping ydCodeMapping             = new YdCodeMapping();
		
		JDTORecordSet rsGetYdStock 			= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsOutRecSet 			= null;
		JDTORecord rsGetStock  				= null;
		JDTORecord outRec         			= null;
		JDTORecord recIn          			= null;
		JDTORecord recEdit        			= null;
		JDTORecord recPara        			= null;
		JDTORecord recInTemp      			= null;
		JDTORecord recGetVal      			= null;
		JDTORecord outRecTemp               = null;
		JDTORecordSet rsResult 				= null;
		
		String szMethodName 	= "stockProcCom";
		String szMsg 			= "";
		String szOperationName 	= "저장품 등록_수정_삭제 처리";
		//String szSTL_NO 		= "";
		int intRtnVal 			= 0;
		int nRet 				= 0;

		try{

			if(szSTL_NO.equals("")){
				szMsg = "[파라미터 Error] 재료번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}

			rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recEdit = JDTORecordFactory.getInstance().create();
			
			
			if(procFlag == 1){
				
				recPara  = JDTORecordFactory.getInstance().create();
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recPara.setField("STL_NO", szSTL_NO);
				
				nRet = ydStockDao.getYdStock(recPara, rsResult, 0);
				
				if(nRet < 0){
					szMsg = "[저장품등록  처리] 저장품조회] Error :: [" + intRtnVal + "]"+"PARAMETER ERROR(" +  szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				} 
				
				
				//=====================================================
				// 저장품 등록
				//=====================================================	
				recIn = JDTORecordFactory.getInstance().create();
				recIn.setField("COIL_NO", szSTL_NO);
				
				intRtnVal	= ydStockDao.getYdStock(recIn, rsOutRecSet, 8);
				
				if(intRtnVal <= 0){
					if(intRtnVal == 0){
						szMsg= "[COIL 공통] Error :: [" + intRtnVal + "]"+"PARAMETER ERROR" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}else{
						szMsg= "[COIL 공통] Error :: [" + intRtnVal + "]"+"PARAMETER ERROR" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
				}	

				
				rsOutRecSet.first();
				rsGetStock = rsOutRecSet.getRecord();

				intRtnVal = this.edtCoilYdstock(rsGetStock, recEdit);
				
				if( intRtnVal < 0 ){
					szMsg= "[저장품등록 처리] Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				
				if (nRet == 0) {

					ydUtils.putLog(szSessionName, szMethodName, "[저장품등록 처리] INSERT :: "+szSTL_NO , 3);

					recEdit.setField("REGISTER", "stockProc");

					intRtnVal = ydStockDao.insYdStock(recEdit);
				
					if(intRtnVal <0){
						szMsg= "[저장품등록 처리] INSERT Error :: [" + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
						return ;
					}
					ydUtils.putLog(szSessionName, szMethodName, szSTL_NO+") [저장품등록 처리] INSERT :: SUCCESS" , 3);	
					
				} else if (nRet > 0) {
					
					recEdit.setField("STL_PROG_CD", 	recEdit.getFieldString("CURR_PROG_CD") );
					recEdit.setField("MODIFIER", 		"stockProc");
					
					nRet = ydStockDao.updYdStock(recEdit, 0);
					if(nRet <= 0){
						szMsg = "[저장품수정 처리] UPDATE Error :: [" + nRet + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
					szMsg = "[" + recPara.getFieldString("STL_NO") + "] :: [저장품수정 처리] UPDATE Success";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
					
				}
				
				return ;
				
			}else if(procFlag == 2){
				
				//=====================================================
				// 저장품 수정
				//=====================================================
				recPara  = JDTORecordFactory.getInstance().create();
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recPara.setField("STL_NO", szSTL_NO);
				
				nRet = ydStockDao.getYdStock(recPara, rsResult, 0);
				
				if(nRet < 0){
					szMsg = "[저장품수정 처리] SELECT Error [" + nRet + "] (" +  szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				} else if(nRet > 0) {
					szMsg = "[저장품수정 처리] SELECT [" + nRet + "] 넘겨받은 재료번호 (" + szSTL_NO + ")가 저장품 테이블에 존재함 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					// ===저장품에 Update 할 항목Set==================//
					// 야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
					rVal = YdCommonUtils.getYdAimRtGp("C", recPara);		
					recPara.setField("YD_AIM_RT_GP", 	rVal[0]);
					recPara.setField("STL_PROG_CD", 	recPara.getFieldString("CURR_PROG_CD") );
					recPara.setField("MODIFIER", 		"stockProc");
					
					rsOutRecSet.first();
					rsGetStock = rsOutRecSet.getRecord();

					intRtnVal = this.edtCoilYdstock(rsGetStock, recEdit);
					
					if( intRtnVal < 0 ){
						szMsg= "[저장품등록 처리] Error :: [" + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
					
					
					nRet = ydStockDao.updYdStock(recPara, 0);
					if(nRet <= 0){
						szMsg = "[저장품수정 처리] UPDATE Error :: [" + nRet + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
					szMsg = "[" + recPara.getFieldString("STL_NO") + "] :: [저장품수정 처리] UPDATE Success";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
					
					return ;
				}		
				
			}else if(procFlag == 3){	
				
				//=====================================================
				// 저장품 삭제
				//=====================================================
				recPara  = JDTORecordFactory.getInstance().create();
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recPara.setField("STL_NO", szSTL_NO);
				
				nRet = ydStockDao.getYdStock(recPara, rsResult, 0);
				
				if(nRet < 0){
					szMsg = "[저장품삭제 처리] SELECT Error [" + nRet + "] (" +  szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				} else if(nRet > 0) {
					szMsg = "[저장품삭제 처리] SELECT [" + nRet + "] 넘겨받은 재료번호 (" + szSTL_NO + ")가 저장품 테이블에 존재함 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					// ===저장품에 Update 할 항목Set==================//
					// 야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
					rVal = YdCommonUtils.getYdAimRtGp("C", recPara);		
					recPara.setField("DEL_YN", 	"Y");
					recPara.setField("MODIFIER", 		"stockProc");
					
					nRet = ydStockDao.updYdStock(recPara, 0);
					if(nRet <= 0){
						szMsg = "[저장품삭제 처리] UPDATE Error :: [" + nRet + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
					szMsg = "[" + recPara.getFieldString("STL_NO") + "] :: [저장품 삭제 처리]UPDATE Success";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
					
					return ;
				}					
			}
			
		}catch(Exception e){
			szMsg="[저장품 등록 수정 삭제 처리]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		} // end of try-catch
		
		szMsg="저장품 등록 수정 삭제 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} //end of stockProcCommit()

	
	
	/**
	 *코일공통 항목을 야드 저장품 항목으로 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtCoilYdstock(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {


		String szMethodName		= "edtCoilYdstock";
		String szMsg			= "";
		String szSTL_APPEAR_GP 	= "";
		String szYD_MTL_ITEM	= "";
		String szSTL_PROG_CD 	= "";
		String szYD_AIM_RT_GP	= "";
		String szYD_AIM_YD_GP	= "";
		String szPTOP_PLNT_GP	= "";
		String szNEXT_PROC	= "";
		String szYD_AIM_BAY_GP	= "";

		double  iW_GP              = 0; 
		double  iOUTDIA            = 0;
		String sW_GP            ="";
		String sOUTDIA          ="";		
		
		
		try{
			szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP");
			if(szSTL_APPEAR_GP.equals("E")){
				szYD_MTL_ITEM = "CM";
			}else if(szSTL_APPEAR_GP.equals("Y")){
				szYD_MTL_ITEM = "CG";
			}

			szYD_AIM_RT_GP = "EC";
			
			//재료외형구분으로 목표야드를 셋팅함.
			if(szYD_MTL_ITEM.equals("CM")){
				szYD_AIM_YD_GP = "H";
			}else if(szYD_MTL_ITEM.equals("CG")){
				szYD_AIM_YD_GP = "J";
			}
			
			//차공정으로 목표동을 셋팅함.
			szNEXT_PROC = ydDaoUtils.paraRecChkNull(inRecord,"NEXT_PROC");
			ydUtils.putLog(szSessionName, szMethodName, "[저장품등록 처리] NEXT_PROC :: "+szNEXT_PROC,3);
			if(!szNEXT_PROC.equals("")){
				szYD_AIM_BAY_GP = szNEXT_PROC.substring(0, 1);				
				
			}
			
			recEditRec.setField("STL_NO", 					ydDaoUtils.paraRecChkNull(inRecord,"COIL_NO")); 					
			recEditRec.setField("YD_MTL_T", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_T")); 		
			recEditRec.setField("YD_MTL_W", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_W")); 		    
			recEditRec.setField("YD_MTL_L", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_LEN"));		
			recEditRec.setField("YD_MTL_WT", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_WT"));	
			recEditRec.setField("COIL_INDIA", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_INDIA"));	
			recEditRec.setField("COIL_OUTDIA", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_OUTDIA"));	
			recEditRec.setField("PLNT_PROC_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"PLNT_PROC_CD"));				
			recEditRec.setField("STL_PROG_CD", 				szSTL_PROG_CD);				
			recEditRec.setField("ORD_YEOJAE_GP", 			ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));			
			recEditRec.setField("ORD_NO", 					ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO")); 			
			recEditRec.setField("ORD_DTL", 					ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL")); 				
			recEditRec.setField("DEMANDER_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"DEMANDER_CD")); 		    
			recEditRec.setField("HCR_GP", 					ydDaoUtils.paraRecChkNull(inRecord,"HCR_GP"));		
			recEditRec.setField("HYSCO_TRANS_GP", 			ydDaoUtils.paraRecChkNull(inRecord,"HYSCO_TRANS_GP"));		
			recEditRec.setField("CUST_CD", 					ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD"));				
			recEditRec.setField("ITEMNAME_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"ITEMNAME_CD"));
			recEditRec.setField("PTOP_PLNT_GP", 			szPTOP_PLNT_GP);
			recEditRec.setField("STL_APPEAR_GP", 			szSTL_APPEAR_GP);	
			recEditRec.setField("YD_MTL_ITEM", 				szYD_MTL_ITEM);
			recEditRec.setField("YD_AIM_RT_GP", 			szYD_AIM_RT_GP);
			recEditRec.setField("YD_AIM_YD_GP", 			szYD_AIM_YD_GP);
			recEditRec.setField("YD_AIM_BAY_GP", 			szYD_AIM_BAY_GP);
	
//			폭 구분 
			//iW_GP = Integer.parseInt(ydDaoUtils.paraRecChkNull(inRecord,"COIL_W"));
			iW_GP = ydDaoUtils.paraRecChkNullDouble(inRecord,"SHEAR_W");	
			
			if (iW_GP < 1601) {
				sW_GP = "M";
			} else {
				sW_GP = "L";
			}
			recEditRec.setField("YD_MTL_W_GP"		 , sW_GP);
			
//외경그룹			
			//iOUTDIA = Integer.parseInt(ydDaoUtils.paraRecChkNull(inRecord,"COIL_OUTDIA"));
			iOUTDIA = ydDaoUtils.paraRecChkNullDouble(inRecord,"COIL_OUTDIA");
			
			if (iOUTDIA <= 1280) {
				sOUTDIA = "A";
			} else if (( iOUTDIA > 1280 )&&( iOUTDIA <= 1930)) { 
				sOUTDIA = "B";
			} else if ( iOUTDIA > 1930 ) {
				sOUTDIA = "C";
			}
			recEditRec.setField("YD_COIL_OUTDIA_GRP_GP"		 , sOUTDIA);
			
			
			
		} catch(Exception e){

			return -1;
		}
		return 1;

	} //end of edtCoilYdstock()
	
	
//	/**
//	 *      [A] 오퍼레이션명 : 크레인 비상조업실적(Y5YDL019)
//	 *
//	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 *      @param JDTORecord rcvMsg
//	 *      @return JDTORecord
//	 *      @throws DAOException 
//	*/
//	public JDTORecord rcvY5YDL019(JDTORecord rcvMsg) throws DAOException {
//		String methodNm = "크레인 비상조업실적[CoilSpecRegSeEJB.rcvY5YDL019] < " + rcvMsg.getResultMsg();
//		String logId = rcvMsg.getResultCode();
//		
//		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
//		
//		try {
//			
//			commUtils.printLog(logId, methodNm, "S+");
//			commUtils.printParam(logId + "크레인 비상조업실적(rcvY5YDL019) 수신 ", rcvMsg);
//
//			//수신 항목 값
//			String sYD_EQP_ID		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //야드설비ID(6)
//			String sYD_UP_LOC		= commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LOC")); //권상위치(11)
//			String sYD_DN_LOC		= commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LOC")); //권하위치(11) 
//			String sSTL_NO			= commUtils.trim(rcvMsg.getFieldString("STL_NO")); //재료번호 
//			String sYD_UP_CMPL_DT   = commUtils.trim(rcvMsg.getFieldString("YD_UP_CMPL_DT")); //야드권상완료일시
//			String sYD_DN_CMPL_DT   = commUtils.trim(rcvMsg.getFieldString("YD_DN_CMPL_DT")); //야드권하완료일시
//			
//			String msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"rcvY5YDL019"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
//			if ("".equals(modifier)) { modifier = msgId; }
//			
//			//수신항목 Check
//			if(!"H".equals(sYD_EQP_ID.substring(0,1)) || !"CR".equals(sYD_EQP_ID.substring(2,4))) {
//				throw new Exception("야드설비ID(YD_EQP_ID)가 야드구분이 'H'이 아니거나 설비 구분이 'CR'이 아닙니다!! [" + sYD_EQP_ID + "]");
//			}
//			if(sYD_UP_LOC.length() != 11) {
//				throw new Exception("권상위치(YD_UP_LOC)가 11자리가 아닙니다!! [" + sYD_UP_LOC + "]");
//			}
//			if(sYD_DN_LOC.length() != 11) {
//				throw new Exception("권하위치(YD_DN_LOC)가 11자리가 아닙니다!! [" + sYD_DN_LOC + "]");
//			}
//			if(!"H".equals(sYD_UP_LOC.substring(0,1))) {
//				throw new Exception("권상위치(YD_UP_LOC)가 야드구분이 'H'이  아닙니다!! [" + sYD_UP_LOC + "]");
//			}
//			if(!"H".equals(sYD_DN_LOC.substring(0,1))) {
//				throw new Exception("권하위치(YD_DN_LOC)가 야드구분이 'H'이  아닙니다!! [" + sYD_DN_LOC + "]");
//			}
//		 
//			if("".equals(sSTL_NO)) {
//				throw new Exception("STL_NO 에 빈 값이 들어왔습니다!! [" + sSTL_NO + "]");
//			}
//	 
//			if(sYD_UP_CMPL_DT.length() != 14) {
//				throw new Exception("야드권상완료일시(YD_UP_CMPL_DT)가 14자리가 아닙니다!! [" + sYD_UP_CMPL_DT + "]");
//			}
//			if(sYD_DN_CMPL_DT.length() != 14) {
//				throw new Exception("야드권하완료일시(YD_DN_CMPL_DT)가 14자리가 아닙니다!! [" + sYD_DN_CMPL_DT + "]");
//			}
//  
//			JDTORecord jrParam	= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
//			jrParam.setResultCode(logId);	//Log ID
//			jrParam.setResultMsg(methodNm);	//Log Method Name 
//			
//			
//			//Crane스케줄ID생성 
//			String sCrnSchID = commDao.getSeqId(logId, methodNm, "CrnSch"); //비상조업실적용 1개의 Crane스케줄ID 사용
//	 
//				/**********************************************************
//				* 1. STL_NO 로 적치단 Clear 하기
//				**********************************************************/
//				/* com.inisteel.cim.yd.ydStock.StockSpecReg.CoilSpecRegSeEJBBean.clearStackLayer
//				UPDATE TB_YD_STKLYR
//				   SET STL_NO = ''
//				      ,YD_STK_LYR_MTL_STAT = 'E'
//				      ,MODIFIER = :V_MODIFIER
//				      ,MOD_DDTT = SYSDATE
//				WHERE  STL_NO = :V_STL_NO
//				AND    YD_STK_COL_GP LIKE :V_YD_GP || '%' */
//				jrParam.setField("MODIFIER"	, modifier		); //수정자
//				jrParam.setField("STL_NO"	, commUtils.trim(sSTL_NO));
//				jrParam.setField("YD_GP"	, "H");	
//				commDao.update(jrParam, "com.inisteel.cim.yd.ydStock.StockSpecReg.CoilSpecRegSeEJBBean.clearStackLayer", logId, methodNm, "STL_NO 로 적치단 Clear");
//				
//				
//				/**********************************************************
//				* 2. 권하위치에  STL_NO 를 적치중으로 설정한다.
//				**********************************************************/
//				/* com.inisteel.cim.yd.ydStock.StockSpecReg.CoilSpecRegSeEJBBean.setStackLayer
//				UPDATE TB_YD_STKLYR
//				   SET STL_NO = :V_STL_NO
//				      ,YD_STK_LYR_MTL_STAT = 'C'
//				      ,MODIFIER = :V_MODIFIER
//				      ,MOD_DDTT = SYSDATE
//				WHERE  YD_STK_COL_GP = :V_YD_STK_COL_GP
//				  AND  YD_STK_BED_NO = :V_YD_STK_BED_NO
//				  AND  YD_STK_LYR_NO = :V_YD_STK_LYR_NO*/
//				jrParam.setField("MODIFIER"			, modifier		); //수정자
//				jrParam.setField("STL_NO"			, commUtils.trim(sSTL_NO));
//				jrParam.setField("YD_STK_COL_GP"	, sYD_DN_LOC.substring(0,6));	
//				jrParam.setField("YD_STK_BED_NO"	, sYD_DN_LOC.substring(6,8));	
//				jrParam.setField("YD_STK_LYR_NO"	, sYD_DN_LOC.substring(8,11));
//				commDao.update(jrParam, "com.inisteel.cim.yd.ydStock.StockSpecReg.CoilSpecRegSeEJBBean.setStackLayer", logId, methodNm, "권하위치에  STL_NO 를 적치중으로 설정 ");
//
//				
//				/**********************************************************
//				* 3. 코일공통 수정 (별도 Transaction 으로 처리)
//				**********************************************************/  
//				
//				jrParam	= JDTORecordFactory.getInstance().create();
//				jrParam.setResultCode(logId);		//Log ID
//				jrParam.setResultMsg(methodNm);	//Log Method Name
//				jrParam.setField("MODIFIER"        , modifier    ); //권하 코일번호
//				jrParam.setField("STOCK_ID"        , commUtils.trim(sSTL_NO)     ); //권하 코일번호
//				jrParam.setField("YD_DN_WR_LOC"    , sYD_DN_LOC.substring(0,8)   ); //야드권하실적위치
//				jrParam.setField("YD_DN_WR_LAYER"  , sYD_DN_LOC.substring(8,11) ); //야드권하실적단		
//				jrParam.setField("YD_LOC"          , sYD_DN_LOC.substring(0,8)+sYD_DN_LOC.substring(9,11)   ); //야드권하실적위치
//				
//				EJBConnector ejbConn1 = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
//				ejbConn1.trx("UpdCoilComLoc", new Class[] { JDTORecord.class }, new Object[] { jrParam });
//				
//				/**********************************************************
//				* 4. 저장품 비상조업 수정
//				**********************************************************/
//				/* com.inisteel.cim.yd.ydStock.StockSpecReg.CoilSpecRegSeEJBBean.updStock 
//				UPDATE TB_YD_STOCK
//				   SET MODIFIER    = :V_MODIFIER
//				     , MOD_DDTT    = SYSDATE
//				     , URGENT_DIST_YN ='Y' --비상조업구분
//				 WHERE STL_NO    = :V_STL_NO*/ 
//				jrParam.setField("MODIFIER"	, modifier	 ); //수정자 
//				jrParam.setField("STL_NO"	, commUtils.trim(sSTL_NO));
//				commDao.update(jrParam, "com.inisteel.cim.yd.ydStock.StockSpecReg.CoilSpecRegSeEJBBean.updStock", logId, methodNm, "저장품 URGENT_DIST_YN 수정 ");
//				
//				
//				/**********************************************************
//				* 5. 작업이력 등록
//				**********************************************************/
//				/* com.inisteel.cim.yd.ydStock.StockSpecReg.CoilSpecRegSeEJBBean.insWrkHist 
//				INSERT INTO TB_YD_WRKHIST (
//				     YD_WRK_HIST_ID
//				    ,YD_CRN_SCH_ID
//				    ,YD_SCH_CD
//				    ,STL_NO
//				    ,YD_EQP_ID
//				    ,YD_UP_WR_LOC
//				    ,YD_UP_WO_LAYER
//				    ,YD_UP_CMPL_DT
//				    ,YD_DN_WR_LOC
//				    ,YD_DN_WR_LAYER
//				    ,YD_DN_CMPL_DT
//				    ,REGISTER
//				    ,REG_DDTT
//				    ,MODIFIER
//				    ,MOD_DDTT
//				    ,DEL_YN
//				    ,YD_GP
//				) VALUES (
//				     CONCAT(TO_CHAR(SYSDATE-1,'YYYYMMDDHH24MI'),LPAD(SUBSTR((select max(YD_WRK_HIST_ID) from TB_YD_WRKHIST),13,6)+1, 6, '0')) 
//				    ,:V_YD_CRN_SCH_ID
//				    ,SUBSTR(:V_YD_EQP_ID,1,2)||'CV01LM'
//				    ,:V_STL_NO
//				    ,:V_YD_EQP_ID
//				    ,:V_YD_UP_WR_LOC
//				    ,:V_YD_UP_WO_LAYER
//				    ,TO_DATE(:V_YD_UP_CMPL_DT,'YYYYMMDDHH24MISS')
//				    ,:V_YD_DN_WR_LOC
//				    ,:V_YD_DN_WR_LAYER
//				    ,TO_DATE(:V_YD_DN_CMPL_DT,'YYYYMMDDHH24MISS')
//				    ,:V_MODIFIER
//				    ,SYSDATE
//				    ,:V_MODIFIER
//				    ,SYSDATE
//				    ,'N'
//				    ,:V_YD_GP
//				)*/
//				jrParam.setField("YD_CRN_SCH_ID"		, sCrnSchID);	
//				jrParam.setField("STL_NO"				, commUtils.trim(sSTL_NO));
//				jrParam.setField("YD_EQP_ID"			, sYD_EQP_ID);	
//				jrParam.setField("YD_UP_WR_LOC"   		, sYD_UP_LOC.substring(0,8)); //야드권하실적위치
//				jrParam.setField("YD_UP_WO_LAYER"   	, sYD_UP_LOC.substring(8,11) ); //야드권하실적위치
//				jrParam.setField("YD_UP_CMPL_DT"		, sYD_UP_CMPL_DT);
//				jrParam.setField("YD_DN_WR_LOC"   		, sYD_DN_LOC.substring(0,8)); //야드권하실적위치
//				jrParam.setField("YD_DN_WR_LAYER"  		, sYD_DN_LOC.substring(8,11) ); //야드권하실적위치
//				jrParam.setField("YD_DN_CMPL_DT"		, sYD_DN_CMPL_DT);	
//				jrParam.setField("MODIFIER"				, modifier); //수정자
//				jrParam.setField("YD_GP"				, "H");	
//				commDao.insert(jrParam, "com.inisteel.cim.yd.ydStock.StockSpecReg.CoilSpecRegSeEJBBean.insWrkHist", logId, methodNm, "비상조업용 작업이력 등록 ");
// 
//				
//		  
//				//L2저장품재원 정보 송신
//				//======================================================
//				// 저장품제원 : 코일야드L2로 송신(YDY5L002)
//				//======================================================
//				JDTORecord recResult = null;
//				recResult = JDTORecordFactory.getInstance().create();
//				recResult.setField("MSG_ID"         , "YDY5L002");
//				recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
//				recResult.setField("STL_NO"         , commUtils.trim(sSTL_NO));
//				recResult.setField("YD_STK_COL_GP"  , "");
//				recResult.setField("YD_STK_BED_NO"  , "");
//				
//				ydDelegate.sendMsg(recResult);
//	
//				String szMsg = "코일야드L2로 응답전문 [YDY5L002] 전송완료";
//				ydUtils.putLog(szSessionName, methodNm, szMsg, YdConstant.DEBUG);
//				
//			
//			commUtils.printLog(logId, methodNm, "S-");
//
//			return jrRtn;
//			
//		} catch (DAOException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
//		}
//	} // end of 크레인 비상조업실적(rcvY5YDL019)
	
	
	/**
	 * C열연 HFL검사완료실적 (HRYDJ011)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord procCHrMillWrkWr
	 * @throws JDTOException
	 */
	public void proHRYDJ011(JDTORecord inRecord)throws JDTOException  {
		// 저장품DAO
		YdStockDao ydStockDao                   = new YdStockDao(); 
		String szMethodName          = "proHRYDJ011";
		String szMsg                 = "";
		String szOperationName       = "C열연 HFL검사완료실적";
		int intRtnVal = 0;

		
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg = "[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		

		ydUtils.displayRecord(szOperationName, inRecord);
		
		
		try{ 
			szMsg = "C열연 HFL검사완료실적-시작"; 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			JDTORecord jrParam	 = JDTORecordFactory.getInstance().create(); 
			jrParam.setField("STL_NO", ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));			
			jrParam.setField("YD_ABMTL_GRD", "Y");
			jrParam.setField("MODIFIER", "HRYDJ011");
			
			szMsg= "YD_STOCK[저장품] UPDATE :: [2] YD_STOCK UPDATE " ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
			
			intRtnVal= commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updproHRYDJ011", "HRYDJ011", szMethodName, "C열연 HFL검사완료실적 수정"); 
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[저장품] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			szMsg=jrParam.getFieldString("STL_NO") +" :: YD_STOCK[C열연 HFL검사완료실적] UPDATE Success  " ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 
			
		}catch(Exception e){
			szMsg="[C열연 HFL검사완료실적] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg="C열연 HFL검사완료실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of proHRYDJ011

	
	/**
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * [A] 오퍼레이션명 : (JMS :JDTORecord 송신처리)
	 * 
	 */
	public void sndJMSInfo2 (JDTORecord param) throws DAOException {	
		
		JmsQueueSender sender = null;
		String queueName = null;
		JDTORecord insRecord = null; 			
		PropertyService propertyService=null;	
		
		
		try {	
			
			StringBuffer sbf = new StringBuffer();			
			
			// 프로퍼티 서비스 인스턴스를 취득합니다.
			propertyService = PropertyService.getInstance();
			
			// JDTORecord인스턴스 객체 취득
			insRecord = JDTORecordFactory.getInstance().create();			
					
			String JMS_TC_CD	    	 = StringHelper.evl(param.getFieldString("JMS_TC_CD"), "");				//JMS전문 ID		8
			String Message = "";
			String szWkGp  = JMS_TC_CD.substring(2,4);
			
				// 큐 명칭을 프로퍼티로부터 취득합니다.
			queueName = propertyService.getProperty("common.properties","jms.queue."+szWkGp+"_MDB_QUEUE");	
		
			sender = new JmsQueueSender();			
			sender.initQueueService(queueName);		
	
			sender.send(param);

		}catch (Exception e) {
		}finally {
			try {
				sender.closeAll();
			} catch (Exception e) {
			}
		}
	}
	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                     일관제철소정보관리시스템-야드관리
	//              저장품관리-저장품제원종료 Facade Session Bean
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛



//	-----------------------------------------------------------------------------
}// end of class

