package com.inisteel.cim.yd.ydStock.StockSpecReg;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ptPlateCommDao.PtPlateCommDao;
import com.inisteel.cim.yd.common.dao.ptStlFrtoMoveDao.PtStlFrtoMoveDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.dao.ptSlabCommDao.PtSlabCommDao;
import com.inisteel.cim.yd.common.dao.ptMSlabCommDao.PtMSlabCommDao;
import com.inisteel.cim.yd.common.dao.ymEtcDao.YmEtcDao;
import com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO;


/**
 * 
 * 슬라브제원등록 Session EJB
 *
 * @ejb.bean name="StockSpecRegSeEJB" jndi-name="StockSpecRegSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class StockSpecRegSeEJBBean extends BaseSessionBean  {

	// Session Name
	private String szSessionName=getClass().getName();

	private YdUtils ydUtils =new YdUtils();
	private YdDaoUtils ydDaoUtils =new YdDaoUtils();
	private YdTcConst ydTcConst =new YdTcConst();
	//private YdConstant constant =new YdConstant();
	// [DEBUG] message flag
	private boolean bDebugFlag=true;
	
	//LOT타입
	public static final String LOT_TYPE_SCARF			= "SA";		//스카핑재
	public static final String LOT_TYPE_SHEAR			= "SA";		//후판정정재
	public static final String LOT_TYPE_WO				= "SB";		//지시대기
	public static final String LOT_TYPE_LOT_NO			= "SL";		//장입Lot
	public static final String LOT_TYPE_PLN_SERNO		= "SP";		//장입순번
	public static final String LOT_TYPE_MS				= "SG";		//외판재
	public static final String LOT_TYPE_YEOJAE			= "SY";		//여재
		
	public static final String LOT_TYPE_SLAB_SHEAR		= "SA";		//슬라브 정정대기
	public static final String LOT_TYPE_SLAB_WO			= "SB";		//슬라브 지시대기
	public static final String LOT_TYPE_SLAB_TRAN		= "SE";		//슬라브 이송대기
	public static final String LOT_TYPE_SLAB_SHUNG  	= "SY";		//슬라브 충당대기
	public static final String LOT_TYPE_SLAB_PLN_SER	= "SP";		//슬라브 후판장입일련번호
	
	//야드재료품목
	public static final String YD_ITEM_HR_MSLAB			= "BH";		//열연주편
	public static final String YD_ITEM_PL_MSLAB			= "BP";		//후판주편
	public static final String YD_ITEM_PL_STR_MSLAB		= "BK";		//후판비축주편
	public static final String YD_ITEM_HR_SLAB			= "SH";		//열연슬라브
	public static final String YD_ITEM_PL_SLAB			= "SP";		//후판슬라브
	public static final String YD_ITEM_PL_SIZING_SLAB	= "SZ";		//후판SizingSlab
	public static final String YD_ITEM_SLAB_GDS			= "SG";		//슬라브제품(외판)
	public static final String YD_ITEM_COIL_MATL		= "CM";		//COIL소재
	public static final String YD_ITEM_COIL_GDS			= "CG";		//COIL제품
	public static final String YD_ITEM_PL_NOT_RCPT_GDS	= "PT";		//후판미입고제품
	public static final String YD_ITEM_PL_GDS			= "PG";		//후판제품
	
	

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	
	/**
	 * 산적LOT TYPE, CODE
	 * 
	 * return  0: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param outRec
	 * @throws JDTOException
	 */
	public int setYdStkLocTpCd(JDTORecord recOut) throws JDTOException {
		//=============================================================================
		// 산적 LOT타입, 산적LOT코드 항목을 구함
		// 
		// STL_NO                     재료번호
		// SLAB_WO_RT_CD              슬라브지시행선코드
		// SCARFING_YN           	    스카핑여부
		// SCARFING_DONE_YN           스카핑완료여부
		// YD_AIM_RT_GP               야드목표행선구분
		// STACK_LOT_NO               산적LOT번호
		//=============================================================================
		YdStockDao ydStockDao        		= new YdStockDao();
		//진행관리 - 이송지시
		PtStlFrtoMoveDao ptStlFrtoMoveDao 	= new PtStlFrtoMoveDao();
		
		JDTORecordSet rsResult       = null;
		JDTORecord recPara           = null;		
		JDTORecord recGetVal         = null;
		String szSTL_NO              = "";
		String szSLAB_WO_RT_CD       = "";
		String szSCARFING_YN    	 = ""; 
		String szSCARFING_DONE_YN    = "";
		String szYD_AIM_RT_GP	     = "";
		String szSTACK_LOT_NO        = "";
		String szYD_STK_LOT_TP	     = ""; 
		String szYD_STK_LOT_CD       = "";
		String szYD_CHG_NO	 		 = "";
		String szARR_WLOC_CD 		 = "";
		String szORD_YEOJAE_GP 		 = ""; 
		String szPROD_DUE_DATE 		 = ""; 
		String szMethodName		     = "setYdStkLocTpCd";
		String szOperationName       = "산적LOT타입코드";
		String szMsg                 = "";
		int nRet                     = 0;
		
		//=================================================
		// 파라미터 레코드로부터 항목추출
		//=================================================
		szSTL_NO           = ydDaoUtils.paraRecChkNull(recOut, "STL_NO");              // 재료번호
		szYD_AIM_RT_GP	   = ydDaoUtils.paraRecChkNull(recOut, "YD_AIM_RT_GP");        // 야드목표행선구분
		szSTACK_LOT_NO     = ydDaoUtils.paraRecChkNull(recOut, "YD_STK_LOT_CD");       // 산적LOT번호 (STACK_LOT_NO)
	    
		szSLAB_WO_RT_CD    = StringHelper.evl(recOut.getFieldString("SLAB_WO_RT_CD"), "").trim();		// 슬라브지시행선  
		szSCARFING_YN 	   = StringHelper.evl(recOut.getFieldString("SCARFING_YN"), "N").trim();		// 스카핑여부
		szSCARFING_DONE_YN = StringHelper.evl(recOut.getFieldString("SCARFING_DONE_YN"), "N").trim();	// 스카핑완료여부  
		
		/*
		 *	2010.06.01 윤재광  
		 *  스카핑대상이 아닌것은 완료로 본다.
		 */
		if("N".equals(szSCARFING_DONE_YN)){
			if("N".equals(szSCARFING_YN)){
				szSCARFING_DONE_YN	= "Y";
			}
		}
		
		try{
			szMsg = "================================== setYdStkLocTpCd() IN ==================================";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			//=======================================================================================================================
			// 재료번호로 가열로장입LOT번호와 가열로장입LOT순번 조회
			//=======================================================================================================================
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();						
			recPara.setField("STL_NO", szSTL_NO);
			
			if(szYD_AIM_RT_GP.startsWith("C"))
			{
				if(szYD_AIM_RT_GP.equals("C1") || szYD_AIM_RT_GP.equals("C2")){
					// 슬라브 열연장입Lot번호    [야드목표행선구분(YD_AIM_RT_GP) + 야드장입순번 :가열로장입Lot번호(REFUR_CHG_LOT_NO)]
					nRet = ydStockDao.getYdStock(recPara, rsResult, 203);
				}else if(szYD_AIM_RT_GP.equals("C3")){
				    // 슬라브 후판장입일련번호    [야드목표행선구분(YD_AIM_RT_GP) + 야드장입순번 :가열로장입장입일련번호(REFUR_CHG_PLN_SERNO)]
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
	
					szYD_CHG_NO	  = ydDaoUtils.paraRecChkNull(recGetVal, "YD_CHG_NO");    
				}
			}else if(szYD_AIM_RT_GP.startsWith("E"))
			{
				nRet = ptStlFrtoMoveDao.getPtStlFrtoMove(recPara, rsResult, 0); 
				
				if(nRet < 0) {
					szMsg = "재료번호로 이송지시 불출개소코드 조회 오류 (" + nRet + ") STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);			
				} else if(nRet == 0){
					szMsg = "재료번호로이송지시 불출개소코드 조회건수 없음 (" + nRet + ") STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
				} else {
					szMsg = "재료번호로 이송지시 불출개소코드 조회성공 (" + nRet + ") STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);							
	
					rsResult.first();
					recGetVal = rsResult.getRecord();
	
					szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(recGetVal, "ARR_WLOC_CD");    // 불출개소 
				}
			}else if(szYD_AIM_RT_GP.equals("A4")|| // 정정대기(PA-CCR)
					 szYD_AIM_RT_GP.equals("A9"))  // 정정대기(PA-HCR)
			{
				recPara.setField("MSLAB_NO", szSTL_NO);	
				nRet = ydStockDao.getYdStock(recPara, rsResult, 35);
				
				if(nRet < 0) {
					szMsg = "재료번호로 주여구분/생산기한일 조회 오류 (" + nRet + ") STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);			
				} else if(nRet == 0){
					szMsg = "재료번호로 주여구분/생산기한일 조회 건수 없음 (" + nRet + ") STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
				} else {
					szMsg = "재료번호로 주여구분/생산기한일 조회 성공 (" + nRet + ") STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);							
	
					rsResult.first();
					recGetVal = rsResult.getRecord();
					szORD_YEOJAE_GP = StringHelper.evl(recGetVal.getFieldString("ORD_YEOJAE_GP"), "2").trim(); 		// 주여구분 
					szPROD_DUE_DATE = StringHelper.evl(recGetVal.getFieldString("PROD_DUE_DATE"), "00000000").trim(); 	// 생산기한일 
				}
			}
			
			szYD_AIM_RT_GP = szYD_AIM_RT_GP.trim();
			szMsg = "TPCODE 조회조건값   : " + szYD_AIM_RT_GP;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
			
			//=====================================================================
			// 산적 LOT TYPE과 CODE설정
			//=====================================================================
			if(szYD_AIM_RT_GP.equals("A1") || szYD_AIM_RT_GP.equals("A2") || szYD_AIM_RT_GP.equals("A3") || szYD_AIM_RT_GP.equals("A5") || 
			   szYD_AIM_RT_GP.equals("A6") || szYD_AIM_RT_GP.equals("A7") || szYD_AIM_RT_GP.equals("A8") || szYD_AIM_RT_GP.equals("AA")){
				
				// 슬라브 정정대기    [스카핑완료여부(SCARFING_DONE_YN) + 슬라브지시행선(SLAB_WO_RT_CD) + 저장품 : 야드목표행선구분(YD_AIM_RT_GP)] 
				szYD_STK_LOT_TP = LOT_TYPE_SLAB_SHEAR;
				szYD_STK_LOT_CD = szSCARFING_DONE_YN + szSLAB_WO_RT_CD + szYD_AIM_RT_GP; 
			
			}else if(szYD_AIM_RT_GP.equals("A4")|| 
					 szYD_AIM_RT_GP.equals("A9")){
						
				// 슬라브 정정대기    [스카핑완료여부(SCARFING_DONE_YN) + 슬라브지시행선(SLAB_WO_RT_CD) + 저장품 : 야드목표행선구분(YD_AIM_RT_GP)+주여구분(ORD_YEOJAE_GP)+생산기한일(PROD_DUE_DATE)] 
				szYD_STK_LOT_TP = LOT_TYPE_SLAB_SHEAR;
				if("1".equals(szORD_YEOJAE_GP)){
					szYD_STK_LOT_CD = szSCARFING_DONE_YN + szSLAB_WO_RT_CD + szYD_AIM_RT_GP + szORD_YEOJAE_GP + szPROD_DUE_DATE;
				}else{
					szYD_STK_LOT_CD = szSCARFING_DONE_YN + szSLAB_WO_RT_CD + szYD_AIM_RT_GP + szORD_YEOJAE_GP + "00000000";
				}
						
			}else if(szYD_AIM_RT_GP.equals("B1") || szYD_AIM_RT_GP.equals("B2") || szYD_AIM_RT_GP.equals("B3") || szYD_AIM_RT_GP.equals("B4") || szYD_AIM_RT_GP.equals("B5") || 
					 szYD_AIM_RT_GP.equals("B6") || szYD_AIM_RT_GP.equals("B7")){
				
				// 슬라브 지시대기    [스카핑완료여부(SCARFING_DONE_YN) + 슬라브지시행선(SLAB_WO_RT_CD) + 산적LOT번호(STACK_LOT_NO)]     
				szYD_STK_LOT_TP = LOT_TYPE_SLAB_WO;
				if(szSTACK_LOT_NO.length()> 13) szSTACK_LOT_NO = szSTACK_LOT_NO.substring(3);
				szYD_STK_LOT_CD = szSCARFING_DONE_YN + szSLAB_WO_RT_CD + szSTACK_LOT_NO;
				
			}else if(szYD_AIM_RT_GP.equals("C1") || szYD_AIM_RT_GP.equals("C2")){
				
				// 슬라브 열연장입Lot번호    [야드목표행선구분(YD_AIM_RT_GP) + 가열로장입Lot번호(REFUR_CHG_LOT_NO)]
			    szYD_STK_LOT_TP = LOT_TYPE_LOT_NO;
			    szYD_STK_LOT_CD = szYD_AIM_RT_GP + szYD_CHG_NO;
				    
			}else if(szYD_AIM_RT_GP.equals("C3")){
				
			     // 슬라브 후판장입일련번호    [야드목표행선구분(YD_AIM_RT_GP) + 가열로장입장입일련번호(REFUR_CHG_PLN_SERNO)]
			   	 szYD_STK_LOT_TP = LOT_TYPE_SLAB_PLN_SER;
			     szYD_STK_LOT_CD = szYD_AIM_RT_GP + szYD_CHG_NO;
			     	 
			}else if(szYD_AIM_RT_GP.equals("Y1") || szYD_AIM_RT_GP.equals("Y2") || szYD_AIM_RT_GP.equals("Y3") || szYD_AIM_RT_GP.equals("Y4") || szYD_AIM_RT_GP.equals("Y5") || 
					 szYD_AIM_RT_GP.equals("Y6") || szYD_AIM_RT_GP.equals("Y7") || szYD_AIM_RT_GP.equals("Y8") || szYD_AIM_RT_GP.equals("YA")){
				
				// 슬라브 충당대기    [스카핑완료여부(SCARFING_DONE_YN) + 슬라브지시행선(SLAB_WO_RT_CD) + 산적LOT번호(STACK_LOT_NO)]  
				szYD_STK_LOT_TP = LOT_TYPE_SLAB_SHUNG;
				if(szSTACK_LOT_NO.length()> 9) szSTACK_LOT_NO = szSTACK_LOT_NO.substring(3);
				szYD_STK_LOT_CD = szSCARFING_DONE_YN + szSLAB_WO_RT_CD + szSTACK_LOT_NO; 
				     
			}else if(szYD_AIM_RT_GP.equals("E1") || szYD_AIM_RT_GP.equals("E2") || szYD_AIM_RT_GP.equals("E3") || szYD_AIM_RT_GP.equals("E4") || szYD_AIM_RT_GP.equals("E5") || 
					 szYD_AIM_RT_GP.equals("E6") || szYD_AIM_RT_GP.equals("E7") || szYD_AIM_RT_GP.equals("E8") || szYD_AIM_RT_GP.equals("E9") || szYD_AIM_RT_GP.equals("EA")){
				
				// 슬라브 이송대기    [스카핑완료여부(SCARFING_DONE_YN) + 슬라브지시행선(SLAB_WO_RT_CD) + 저장품 : 야드목표행선구분(YD_AIM_RT_GP)] + 불출개소코드. 
				szYD_STK_LOT_TP = LOT_TYPE_SLAB_TRAN;
				szYD_STK_LOT_CD = szSCARFING_DONE_YN + szSLAB_WO_RT_CD + szYD_AIM_RT_GP + szARR_WLOC_CD; 
				     
			}else if(szYD_AIM_RT_GP.equals("GA") || szYD_AIM_RT_GP.equals("HA") || szYD_AIM_RT_GP.equals("KA") || szYD_AIM_RT_GP.equals("LA") || szYD_AIM_RT_GP.equals("MA") || 
					 szYD_AIM_RT_GP.equals("NA") || szYD_AIM_RT_GP.equals("OA") || szYD_AIM_RT_GP.equals("ZA")){

				// 슬라브 외판대기    [스카핑완료여부(SCARFING_DONE_YN) +  저장품 : 야드목표행선구분(YD_AIM_RT_GP)]
				szYD_STK_LOT_TP = LOT_TYPE_MS;
				szYD_STK_LOT_CD = szSCARFING_DONE_YN + szSLAB_WO_RT_CD + szYD_AIM_RT_GP; 
			
			}else{
				// 해당 업무가 없을시에
				ydUtils.putLog(szSessionName, szMethodName, "정의된 LOT타입과 코드가  없음 ", YdConstant.DEBUG);
				szYD_STK_LOT_TP = LOT_TYPE_WO;				     
				szYD_STK_LOT_CD = szSCARFING_DONE_YN + szSLAB_WO_RT_CD + szYD_AIM_RT_GP; 				 
			}
			
			if(szYD_STK_LOT_TP.equals("")){
				ydUtils.putLog(szSessionName, szMethodName, "LOT CODE :: LOT TYPE이 공란이므로 공란으로 설정됨", YdConstant.DEBUG);
				szYD_STK_LOT_CD = "";
			}
			
			//===========================================================================	
			// 산적LOT타입, 산적LOT코드 값을 레코드에 설정
			//===========================================================================	
			recOut.setField("YD_STK_LOT_TP", szYD_STK_LOT_TP); // 야드산적Lot타입
			recOut.setField("YD_STK_LOT_CD", szYD_STK_LOT_CD); // 야드산적Lot코드

			szMsg = "================================== setYdStkLocTpCd() OUT ==================================";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
		} catch(Exception e){
			szMsg = "산적LOT타입과 코드 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		
		return 1;
	}
	
	/**
	 * 재료품목[슬라브]
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	
	public int SetYD_MTL_ITEM_SLAB(JDTORecord recOut) throws JDTOException {		
		String szMethodName		= "SetYD_MTL_ITEM_SLAB";
		String szMsg          	= "";
		String szOperationName  = "재료품목[슬라브]";
		
		String szSTL_APPEAR_GP	= ydDaoUtils.paraRecChkNull(recOut,"STL_APPEAR_GP");
		String szSLAB_WO_RT_CD  = ydDaoUtils.paraRecChkNull(recOut,"SLAB_WO_RT_CD");
		String szYD_MTL_ITEM	= "";
		
		
		try{
			if(szSTL_APPEAR_GP.equals("B")&& szSLAB_WO_RT_CD.startsWith("H")){
				szYD_MTL_ITEM  	= YD_ITEM_HR_MSLAB;		//열연주편
			}else if(szSTL_APPEAR_GP.equals("B")&& szSLAB_WO_RT_CD.startsWith("P")){
				szYD_MTL_ITEM  	= YD_ITEM_PL_MSLAB;		//후판주편
			}else if(szSTL_APPEAR_GP.equals("C")&& szSLAB_WO_RT_CD.startsWith("H")){
				szYD_MTL_ITEM  	= YD_ITEM_HR_SLAB;		//열연슬라브
			}else if(szSTL_APPEAR_GP.equals("C")&& szSLAB_WO_RT_CD.startsWith("P")){
				szYD_MTL_ITEM  	= YD_ITEM_PL_SLAB;		//후판슬라브
			}else if(szSLAB_WO_RT_CD.equals("MS")){
				szYD_MTL_ITEM  	= YD_ITEM_SLAB_GDS;  	//외판슬라브
			}else if(szSTL_APPEAR_GP.equals("D")){
				szYD_MTL_ITEM	= YD_ITEM_PL_SIZING_SLAB;//후판Sizing슬라브
			}else if(szSTL_APPEAR_GP.equals("Y")){
				szYD_MTL_ITEM  	= YD_ITEM_SLAB_GDS;		//슬라브제품
			}
		
			recOut.setField("YD_MTL_ITEM"  , szYD_MTL_ITEM);
			
		} catch(Exception e){
			szMsg= "[SLAB재료품목] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(e.getMessage());
		}
		return 1;

	} //end of SetYD_MTL_ITEM_SLAB()
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updYdStockReTX(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updYdStockTX(inRec,0);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockReTX
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updPtPlateCommReTX(JDTORecord inRec , String StringGp) throws DAOException {

		int intRtnVal               = 0;
		PtPlateCommDao		ptPlateCommDao		= new PtPlateCommDao();
		try {
			
			int intGp= Integer.parseInt(StringGp) ;
			
//			기존 방식 적용 
    		intRtnVal = ptPlateCommDao.updPtPlateCommTX(inRec,intGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updPtPlateCommReTX
	
	
	/**
	 *      [A] 오퍼레이션명 : 후판주문외제품 이송지시 등록/취소 업데이트 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updateDmFrReTX(JDTORecord inRec , String StringGp) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
			int intGp= Integer.parseInt(StringGp) ;
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updateDmFrTX(inRec,intGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updateDmFrReTX
	
	
	/**
	 *      [A] 오퍼레이션명 : 후판주문외제품 이송지시 등록/취소 업데이트 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int update_Dm_TimeReTX(JDTORecord inRec , String StringGp) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
			int intGp= Integer.parseInt(StringGp) ;
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.update_Dm_TimeTX(inRec,intGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updateDmFrReTX
	
	
	/**
	 *      [A] 오퍼레이션명 : 저장품의 YD_WBOOK_ID, YD_SCH_CD 삭제 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updYdStockDelYdWBookIdReTX(JDTORecord inRec ) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updYdStockDelYdWBookIdTX(inRec);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockDelYdWBookIdReTX
	
	/**
	 *      [A] 오퍼레이션명 : 작업보류/해제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updateStlHoldstatReTX(String sStlNo,String sUserId,String sWorkHoldGp) throws DAOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updateStlHoldstatTX(sStlNo,sUserId,sWorkHoldGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockDelYdWBookIdReTX
	
	/**
	 *      [A] 오퍼레이션명 : 이상재 등록/해제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */ 
	public int updateStlAbMtlRsnCdReTX(  String sStlNo,
									     String sUserId,
									     String sYdAbmtlRsnCd,
									     String sYdAbmtlHdMtdCd,
									     String sYdAbmtlGrd,
									     String sYdAbmtlRem,
									     String sYDAbmtAsgnDd) throws DAOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
			//기존 방식 적용 
    		intRtnVal = ydStockDao.updateStlAbMtlRsnCdTX( sStlNo,  sUserId,	  sYdAbmtlRsnCd,  sYdAbmtlHdMtdCd,sYdAbmtlGrd,sYdAbmtlRem,sYDAbmtAsgnDd);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockDelYdWBookIdReTX
	
	/**
	 *      [A] 오퍼레이션명 : 작업보류/해제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updYdStockBookOutLocReTX(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updYdStockBookOutLocTX(inRec);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockBookOutLocReTX
	
	/**
	 *      [A] 오퍼레이션명 : 작업보류/해제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updYdStockBookOutLoc_YeojaeReTX(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updYdStockBookOutLoc_YeojaeTX(inRec);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockBookOutLoc_YeojaeReTX
	
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updPtComm_LOCReTX(JDTORecord inRec , String StringGp) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
			int intGp= Integer.parseInt(StringGp) ;
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updPtComm_LOCTX(inRec,intGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updPtComm_LOCTX
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updPtComm_FIXReTX(JDTORecord inRec , String StringGp) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
			int intGp= Integer.parseInt(StringGp) ;
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updPtComm_FIXTX(inRec,intGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updPtComm_FIXReTX
	
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updPtComm_PROG_CDReTX(JDTORecord inRec , String StringGp) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
			int intGp= Integer.parseInt(StringGp) ;
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updPtComm_PROG_CDTX(inRec,intGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updPtComm_FIXReTX
	
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updYdMSlabCommYdGpReTX(JDTORecord inRec , String StringGp) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
			int intGp= Integer.parseInt(StringGp) ;
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updYdMSlabCommYdGpTX(inRec,intGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updPtComm_FIXReTX
	
	/**
	 *  [A] 오퍼레이션명 : 후판제품 목적지코드 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int update_PlateYeajaeGpReTX(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.update_PlateYeajaeGpTX(inRec);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockBookOutLoc_YeojaeReTX
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updYdPlateCommBookOutLocReTX(JDTORecord inRec , String StringGp) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
			int intGp= Integer.parseInt(StringGp) ;
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updYdPlateCommBookOutLocTX(inRec,intGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdPlateCommBookOutLocReTX
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updYdSlabCommYdGpReTX(JDTORecord inRec , String StringGp) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
			int intGp= Integer.parseInt(StringGp) ;
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updYdSlabCommYdGpTX(inRec,intGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdPlateCommBookOutLocReTX
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updPtSlabCommReTX(JDTORecord inRec , String StringGp) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		PtSlabCommDao PtSlabCommDao = new PtSlabCommDao();
		try {
			
			int intGp= Integer.parseInt(StringGp) ;
			
//			기존 방식 적용 
    		intRtnVal = PtSlabCommDao.updPtSlabCommTX(inRec,intGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdPlateCommBookOutLocReTX
	
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updPtMSlabCommReTX(JDTORecord inRec , String StringGp) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		PtMSlabCommDao PtMSlabCommDao = new PtMSlabCommDao();
		try {
			
			int intGp= Integer.parseInt(StringGp) ;
			
//			기존 방식 적용 
    		intRtnVal = PtMSlabCommDao.updPtMSlabCommTX(inRec,intGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdPlateCommBookOutLocReTX
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updYdStockBookOutLocReTX(JDTORecord inRec , String StringGp) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
			int intGp= Integer.parseInt(StringGp) ;
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updYdStockBookOutLocTX(inRec,intGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdPlateCommBookOutLocReTX
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updYdStock2ReTX(JDTORecord inRec , String StringGp) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
			int intGp= Integer.parseInt(StringGp) ;
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updYdStock2TX(inRec,intGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdPlateCommBookOutLocReTX
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int uptYmEtcDaoReTX(JDTORecord inRec , String StringGp) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YmEtcDao YmEtcDao = new YmEtcDao();
		try {
			
			int intGp= Integer.parseInt(StringGp) ;
			
//			기존 방식 적용 
    		intRtnVal = YmEtcDao.uptYmEtcDaoTX(inRec,intGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of uptYmEtcDaoReTX
	
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updPtPlateCommForBookOutYeojaeReTX(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal               = -1;
		JspCommonDAO JspCommonDAO = new JspCommonDAO();
		try {
			
//			기존 방식 적용 
			JspCommonDAO.updPtPlateCommForBookOutYeojaeTX(inRec);
   
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of uptYmEtcDaoReTX
	
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updPtPlateCommForBookOutBYOrdPilingReTX(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal               = -1;
		JspCommonDAO JspCommonDAO = new JspCommonDAO();
		try {
			
//			기존 방식 적용 
			JspCommonDAO.updPtPlateCommForBookOutBYOrdPilingTX(inRec);
   
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of uptYmEtcDaoReTX
	
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int insYdStockReTX(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.insYdStockTX(inRec);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdPlateCommBookOutLocReTX

	/**
	 *      [A] 오퍼레이션명 : 후판제품운송지시 대기 update
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updYdStock_DMYDR028TX(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updYdStock_DMYDR028TX(inRec);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStock_DMYDR028TX
	
	
	/**
	 *      [A] 오퍼레이션명 : 후판제품차량LOTID update
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updYdStock_LOTID(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updYdStock_LOTIDTX(inRec);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStock_LOTID
	
	
	/**
	 *      [A] 오퍼레이션명 : C열연 차량LOTID update
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updYdStock_COIL_LOTID(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updYdStock_COIL_LOTIDTX(inRec);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStock_LOTID
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 저장위치 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updYdStklyrReTX(JDTORecord inRec, String StringGp) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStkLyrDao ydStkLyrDao 			= new YdStkLyrDao();
		try {
			
			int intGp= Integer.parseInt(StringGp) ;
			
//			기존 방식 적용 
    		intRtnVal = ydStkLyrDao.updYdStklyrNEWTX(inRec,intGp);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyrReTX
	
	
}


