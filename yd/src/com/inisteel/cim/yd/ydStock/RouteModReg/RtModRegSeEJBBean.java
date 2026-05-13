package com.inisteel.cim.yd.ydStock.RouteModReg;

import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yd.ccoil.dao.CCoilDAO;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.dao.ptPlateCommDao.PtPlateCommDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.DaoManager; 
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.ydStock.StockSpecReg.CoilSpecRegSeEJBBean;
import com.inisteel.cim.yd.ydStock.StockSpecReg.StockSpecRegSeEJBBean;
import com.inisteel.cim.yd.common.util.YdCodeMapping;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.common.dao.ydMarkingHistDao.YdMarkingHistDao;
import com.inisteel.cim.yd.common.dao.ydWrkHistDao.YdWrkHistDao;
import com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 * 
 * 저장품행선변경등록 Session EJB
 *
 * @ejb.bean name="RtModRegSeEJB" jndi-name="RtModRegSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class RtModRegSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName 	= getClass().getName();
	 
	private YdUtils ydUtils      	= new YdUtils();
	
	private YmCommDAO commDao = new YmCommDAO();
	
	private YdDaoUtils ydDaoUtils 	= new YdDaoUtils();
	
	private StockSpecRegSeEJBBean stock = new StockSpecRegSeEJBBean();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	
	YdDelegate      ydDelegate      = new YdDelegate();
	
	// [DEBUG] message flag
	private boolean bDebugFlag		= true;
	String[] rVal = new String[1];
	
	private CCommUtils commUtils = new CCommUtils();
	private CCoilDAO coilDao = new CCoilDAO();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * 주편재설계확정지시 ( 생산예정슬라브 Logic 추후 추가) (CTYDJ012)
	 * 전단지시 > 전단실적 > 주편(진행), 슬라브(예정) 상태에서 다시 주편정보를 변경시 처리(예정 슬라브 정보 변경)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procMslabDsCmmtWo(JDTORecord inRecord)throws JDTOException  {

		String szMethodName 	= "procMslabDsCmmtWo";
		String szMsg 			= "";
		String szOperationName  = "주편재설계확정지시";

		YdStockDao ydStockDao 	= new YdStockDao();
		int intRtnVal = 0;

		JDTORecord recResult 	= null;
		JDTORecord recResult1 	= null;

		JDTORecordSet rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");

		recResult 	= JDTORecordFactory.getInstance().create();
		recResult1 	= JDTORecordFactory.getInstance().create();

		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode	=	ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[생산통제] 주편재설계확정지시 수신";
			ydUtils.putLogMsg("X",YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//수신한 주편번호에 편성되어있던 슬라브를 종료처리하기.
			{
				// 추후 반영 
			}
			/*
			 * 재등록하는 부분
			 * '생산예정슬라브' 로직 추후 추가.
			 * 수신한 주편번호로 슬라브스펙 읽기.	 
			 */
			/*
			 * 2010.08.31 윤재광 - 일단 SIKP 처리 : 사용하는 부분이 정의안됨.
			intRtnVal = ydStockDao.getYdStock(inRecord, rsResult, 86);
			if(intRtnVal>0){
				szMsg="주편번호로 예정슬라브정보 가져오기 성공" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			}else{
				szMsg="주편번호로 예정슬라브정보 가져오기 실패" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			}
		
			rsResult.first();

			for( int i = 0; i < rsResult.size() ; i++){

				recResult = rsResult.getRecord();
				
				//저장품 등록항목 편집 
				this.edtMslabReWo(recResult,recResult1);
				
				//저장품에 등록하기
				intRtnVal = ydStockDao.insYdStock(recResult1);
				if(intRtnVal>0){
					szMsg = "-----저장품에 등록 성공-----";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				}else if(intRtnVal == -2){
					szMsg = "-----저장품에 등록 실패 : Parameter Error-----";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}else{
					szMsg = "-----저장품에 등록 실패 : Exception  Error-----";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				rsResult.next();
			}
			*/			
		}catch(Exception e){

			szMsg="Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch

	} // end of procMslabDsCmmtWo()
	
	/**
	 * [주편재설계확정지시] 항목 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtMslabReWo(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {

		try{
			recEditRec.setField("STL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"PLN_SLAB_NO")); 	// 재료번호-예정슬라브번호
			recEditRec.setField("REGISTER", 		"CTYDJ012");											// 등록자 	
			recEditRec.setField("MODIFIER", 		"CTYDJ012");											// 수정자
			recEditRec.setField("YD_STK_LOT_CD", 	ydDaoUtils.paraRecChkNull(inRecord,"STACK_LOT_NO"));	// 슬라브산적LOT코드	
			recEditRec.setField("YD_MTL_W", 		ydDaoUtils.paraRecChkNull(inRecord,"REAL_MEASURE_SLAB_W"));		// 폭
			recEditRec.setField("YD_MTL_L", 		ydDaoUtils.paraRecChkNull(inRecord,"REAL_MEASURE_SLAB_LEN"));	// 길이
			recEditRec.setField("YD_MTL_T", 		ydDaoUtils.paraRecChkNull(inRecord,"REAL_MEASURE_SLAB_T"));		// 두께
			recEditRec.setField("YD_MTL_WT", 		ydDaoUtils.paraRecChkNull(inRecord,"CAL_SLAB_WT"));				// 중량
			recEditRec.setField("ORD_YEOJAE_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));	// 주문여재구분
			recEditRec.setField("ORD_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO"));			// 주문번호
			recEditRec.setField("ORD_DTL", 			ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL"));			// 주문행번
			recEditRec.setField("STL_PROG_CD", 		ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));	// 진도코드
			
			recEditRec.setField("STL_APPEAR_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));	// 재료외형
			recEditRec.setField("SCARFING_YN", 		ydDaoUtils.paraRecChkNull(inRecord,"SCARFING_YN"));		// 유무
			recEditRec.setField("SCARFING_DONE_YN", ydDaoUtils.paraRecChkNull(inRecord,"SCARFING_DONE_YN"));// SF실적
			recEditRec.setField("SLAB_WO_RT_CD", 	ydDaoUtils.paraRecChkNull(inRecord,"SLAB_WO_RT_CD"));	// 지시행선
			recEditRec.setField("HCR_GP", 			ydDaoUtils.paraRecChkNull(inRecord,"HCR_GP"));			// HCR구분
			
			recEditRec.setField("ORD_HCR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"ORD_HCR_GP"));		// 설계HCR구분
			recEditRec.setField("HCR_GP", 			ydDaoUtils.paraRecChkNull(inRecord,"HCR_GP"));			// HCR구분

		} catch(Exception e){

			return -1;
		}
		return 1;

	} //end of edtMslabReWo()
	
	/**
	 * 외판행선변경확정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procOutplRtChng(JDTORecord inRecord)throws JDTOException  {
		
		// 2011.04.11 YJK - 사용안함.
		
	} // end of procOutplRtChng()
	
	/**
	 * 후판제품반송확정(DMYDR003) [권오창 2009.11.25]
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlGdsHoldCommt(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		YdStockDao ydStockDao 					= new YdStockDao();
		YdMarkingHistDao ydMarkingHistDao       = new YdMarkingHistDao();

		// 레코드 선언
		JDTORecordSet rsPlateComm 	            = null;
		JDTORecordSet rsResult               	= null;
		JDTORecord recStockColumn 	            = null;
		JDTORecord recPara 			            = null;
		JDTORecord recResult                    = null;
		JDTORecord recGetVal                    = null;
		
		// 변수 선언
		String szMethodName 				    = "procPlGdsHoldCommt";
		String szMsg 							= "";
		String szOperationName                  = "후판제품반송확정";
		String szSTL_APPEAR_GP       	        = "";
		String szSTL_NO 						= "";
		String szCURR_PROG_CD               	= "";
		String szWO_CAR_PLNT_PROC_CD        	= "";
		String szFRTOMOVE_ORD_DATE          	= "";   
		String szURGENT_FRTOMOVE_WORD_GP   	    = "";
		String szCANCEL_YN 	       				= "";
		String szORD_NO                     	= "";
		String szORD_DTL                    	= "";
		String szMAXWORK_STEP_NO            	= "0";
		String szMK_MOD_RSN                    	= "";
		int intRtnVal							= 0;
		int nRet                            	= 0;

		// 전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);
		if(szRcvTcCode==null){
			// 수신한 전문이 null이라면 error
			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 후판제품보류확정 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//=============================================================
			// 수신전문 항목 값 추출
			//=============================================================
			szSTL_APPEAR_GP              = ydDaoUtils.paraRecChkNull(inRecord, "STL_APPEAR_GP");
			szSTL_NO 					 = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");                  // 수신전문의 재료번호는 PLATE_NO
			szCURR_PROG_CD               = ydDaoUtils.paraRecChkNull(inRecord, "CURR_PROG_CD");
			szWO_CAR_PLNT_PROC_CD        = ydDaoUtils.paraRecChkNull(inRecord, "WO_CAR_PLNT_PROC_CD");
			szFRTOMOVE_ORD_DATE          = ydDaoUtils.paraRecChkNull(inRecord, "FRTOMOVE_ORD_DATE");  
			szURGENT_FRTOMOVE_WORD_GP    = ydDaoUtils.paraRecChkNull(inRecord, "URGENT_FRTOMOVE_WORD_GP");
			szCANCEL_YN 	       		 = ydDaoUtils.paraRecChkNull(inRecord, "CANCEL_YN");
					
			if(szWO_CAR_PLNT_PROC_CD.equals("")){
				// 정보반송
				szMK_MOD_RSN = "1";
			} else {
				// 현물반송
				szMK_MOD_RSN = "4"; 			
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
			recStockColumn.setField("STL_APPEAR_GP"          , szSTL_APPEAR_GP);	
			recStockColumn.setField("STL_NO"                 , szSTL_NO);
			recStockColumn.setField("STL_PROG_CD"            , szCURR_PROG_CD);	
			recStockColumn.setField("PLNT_PROC_CD"           , szWO_CAR_PLNT_PROC_CD);
			recStockColumn.setField("FRTOMOVE_ORD_DATE"      , szFRTOMOVE_ORD_DATE);			
			recStockColumn.setField("URGENT_FRTOMOVE_WORD_GP", szURGENT_FRTOMOVE_WORD_GP);
			recStockColumn.setField("MODIFIER"               , "DMYDR003");
	
			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("P",inRecord );		
			recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
			//****************************************************************************************************

			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[후판제품보류확정] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[후판제품보류확정] UPDATE Success", YdConstant.DEBUG);
			//****************************************************************************************************
			
			//================================================================================================
			// TB_PT_PLATECOMM 에서 주분번호, 주문행번, 충당일자를 반환 (GP : 4)
			//
			// com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECOMM_PIDEV 
			// 
			// 파라미터 : PLATE_NO ( V_PLATE_NO )
			//================================================================================================			
			// 레코드 생성
			/* 2012.07.18 윤재광
			 * 사용안함-후판마킹정보
			 * recPara = JDTORecordFactory.getInstance().create();
			rsPlateComm = JDTORecordFactory.getInstance().createRecordSet("");

			// PLATECOMM 조회 (주문번호, 주문행번, 충당일자)
			// 확인 : 수신받는 재료번호가 PLATE_NO이고 PLATE_NO로 조회 한다면 위에 재료이력에 INSERT시에 저장품에 없다면
			// return 됨 (PLATE_NO인지 SLAB_NO인지 확인)
			recPara.setField("PLATE_NO", szSTL_NO);
			nRet = ydStockDao.getYdStock(recPara, rsPlateComm, 4);
			if(nRet < 0){
				szMsg = "TB_PT_PLATECOMM 조회 오류 Ret(" + nRet + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(nRet == 0){
				szMsg = "TB_PT_PLATECOMM 조회 건수가 없음 Ret(" + nRet + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
			} else if(nRet > 0) {
				rsPlateComm.first();
				recGetVal = rsPlateComm.getRecord();
				
				szORD_NO     = ydDaoUtils.paraRecChkNull(recGetVal, "ORD_NO");       // 주문번호
				szORD_DTL    = ydDaoUtils.paraRecChkNull(recGetVal, "ORD_DTL");      // 주문행번   
				
				szMsg = "TB_PT_PLATECOMM 조회  ORD_NO(" + szORD_NO + ") ORD_DTL(" + szORD_DTL + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);									
			}
			
			//================================================================================================
			// TB_YD_MARKINGHIST에서 해당 재료번호의 최대차수 구함 (GP : 1)
			//
			// com.inisteel.cim.yd.dao.ydmarkinghist.YdMarkingHistDao.getYdMarkingHistMaxStepNoByStlNo
			//
			// 파라미터 : STL_NO ( V_STL_NO )
			//================================================================================================
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara.setField("STL_NO", szSTL_NO);
			nRet = ydMarkingHistDao.getYdMarkingHist(recPara, rsResult, 1);
			if(nRet < 0){
				szMsg = "TB_YD_MARKINGHIST MAX차수 조회 오류 Ret(" + nRet + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(nRet == 0){
				szMsg = "TB_YD_MARKINGHIST MAX차수 조회 건수가 없음 Ret(" + nRet + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
				szMAXWORK_STEP_NO = "0";
			} else if(nRet > 0) {
				rsResult.first();
				recGetVal = rsResult.getRecord();
				szMAXWORK_STEP_NO = ydDaoUtils.paraRecChkNull(recGetVal, "WORK_STEP_NO");
				szMsg = "TB_YD_MARKINGHIST MAX차수 조회 Ret(" + nRet + ") MAX(" + szMAXWORK_STEP_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
			}			

			//====================================================================
			// 취소유무에 따라 "N"이면 Marking이력테이블에 등록 
			// "Y"면 Marking이력테이블에서 삭제처리(해당 재료번호의 MAX차수에 대해DEL_YN처리)
			//====================================================================	
			// 레코드 생성
			recResult = JDTORecordFactory.getInstance().create();

			if(szCANCEL_YN.equals("N")){
				// 등록처리
				recResult.setField("STL_NO"             , szSTL_NO);
				recResult.setField("WORK_STEP_NO"       , "" + (Integer.parseInt(szMAXWORK_STEP_NO)+1)); // 해당재료번호의 최대차수에서 +1차수, 없다면 1차
				recResult.setField("REGISTER"           , "DMYDR003");                                     
				recResult.setField("OCCUR_DDTT"         , YdUtils.getCurDate("yyyyMMddHHmmss"));         // 현재일시 YYYYMMDDHH24MISS
				recResult.setField("MK_MOD_EXN"         , "N"); 
				recResult.setField("MK_MOD_DT"          , "");                                           // 화면에서 처리할 항목이므로 공백  
				recResult.setField("MK_MOD_RSN"         , szMK_MOD_RSN);                                 // 지시차공정공장코드를 통해 정보/현물 반송 구분
				recResult.setField("MK_MOD_RSN_REG_DT"  , YdUtils.getCurDate("yyyyMMddHHmmss"));         // 현재일시 YYYYMMDDHH24MISS
				recResult.setField("ORD_NO"             , szORD_NO);     
				recResult.setField("ORD_DTL"            , szORD_DTL);
				recResult.setField("WO_CAR_PLNT_PROC_CD", szWO_CAR_PLNT_PROC_CD);
				recResult.setField("CANCEL_YN"          , szCANCEL_YN);
				
				nRet = ydMarkingHistDao.insYdMarkingHist(recResult);
				if(nRet <= 0){
					szMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] INSERT ERROR :: [" + nRet + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				} else {
					szMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] INSERT SUCCESSFULL :: [" + nRet + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
				}
			} else if(szCANCEL_YN.equals("Y")){
				if(Integer.parseInt(szMAXWORK_STEP_NO) > 0){
					// 삭제처리
					recResult.setField("MODIFIER"     , "DMYDR003");
					recResult.setField("DEL_YN"       , "Y");
					recResult.setField("STL_NO"       , szSTL_NO);
					recResult.setField("WORK_STEP_NO" , szMAXWORK_STEP_NO);        // 삭제는 해당 재료번호의 최대차수
					nRet = ydMarkingHistDao.updYdMarkingHist(recResult, 0);
					if(nRet <= 0){
						szMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] UPDATE ERROR :: [" + nRet + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;					
					} else {
						szMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] UPDATE SUCCESSFULL :: [" + nRet + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);										
					}		
					
					// Facade에 있던 취소유무 'Y' 수신시 처리하는 EJB호출 
					EJBConnector ydEjbCon = new EJBConnector("default", this);
					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);
				}
			}*/
			
			//======================================================
			// 저장품제원 : 후판제품 L2 로 송신(YDY4L002, YDY8L002)
			//======================================================
			// 레코드 생성
			recResult = JDTORecordFactory.getInstance().create();
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(ydDaoUtils.paraRecChkNull(inRecord,"YD_GP"))) { //- 2013.01.16 수정 (3기)
				recResult.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고
				szMsg = "후판제품L2로 저장품제원 전문(YDY8L002) 송신";
			} else {
				recResult.setField("MSG_ID"         , "YDY4L002"); //1후판 제품창고
				szMsg = "후판제품L2로 저장품제원 전문(YDY4L002) 송신";
			}			
			recResult.setField("YD_INFO_SYNC_CD", "5");            // 5:지정저장품
			recResult.setField("STL_NO"         , szSTL_NO);
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			
			// 2021. 05. 17 전문생성 시점에 PT_PLATE_COMM에 진도코드가 바뀌지 않은 문제가 있어
			// 진도코드를 파라메터로 넘겨 처리함
			recResult.setField("CURR_PROG_CD"  , szCURR_PROG_CD);
			
			
			ydDelegate.sendMsg(recResult);			
			
			// 전사물류개선 2021. 4. 3
			if(PlateGdsYdUtil.isSendToEaiY9_stlNo( ydDaoUtils.paraRecChkNull(inRecord, "STL_NO")) ){
				recResult.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
				ydDelegate.sendMsg(recResult);
			}
			
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);										
		}catch(Exception e){
			szMsg = "[후판제품보류확정]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		}
		szMsg = "[후판제품보류확정]수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} 
	
	/**
	 *슬라브공통 항목을 야드 저장품 항목으로 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtPmSlabYdstock(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {

		try{

			recEditRec.setField("STL_NO",        			ydDaoUtils.paraRecChkNull(inRecord,"SLAB_NO"));
			//recEditRec.setField("YD_STK_LOT_TP",        	ydDaoUtils.paraRecChkNull(inRecord,"YD_STK_LOT_TP"));
			recEditRec.setField("YD_STK_LOT_CD",        	ydDaoUtils.paraRecChkNull(inRecord,"STACK_LOT_NO"));
			
			recEditRec.setField("YD_MTL_T",        			ydDaoUtils.paraRecChkNull(inRecord,"SLAB_T"));
			recEditRec.setField("YD_MTL_W",        			ydDaoUtils.paraRecChkNull(inRecord,"SLAB_W"));
			recEditRec.setField("YD_MTL_L",        			ydDaoUtils.paraRecChkNull(inRecord,"SLAB_LEN"));
			recEditRec.setField("YD_MTL_WT",        		ydDaoUtils.paraRecChkNull(inRecord,"SLAB_WT"));
			recEditRec.setField("STL_APPEAR_GP",        	ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recEditRec.setField("PLNT_PROC_CD",        		ydDaoUtils.paraRecChkNull(inRecord,"PLNT_PROC_CD"));
			recEditRec.setField("STL_PROG_CD",        		ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			recEditRec.setField("ORD_YEOJAE_GP",        	ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));
			recEditRec.setField("ORD_NO",        			ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO"));
			recEditRec.setField("ORD_DTL",        			ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL"));
			recEditRec.setField("BUY_SLAB_NO",        		ydDaoUtils.paraRecChkNull(inRecord,"BUY_SLAB_NO"));
			recEditRec.setField("SLAB_WO_RT_CD",        	ydDaoUtils.paraRecChkNull(inRecord,"SLAB_WO_RT_CD"));
			recEditRec.setField("ORD_HCR_GP",        		ydDaoUtils.paraRecChkNull(inRecord,"ORD_HCR_GP"));
			recEditRec.setField("HCR_GP",        			ydDaoUtils.paraRecChkNull(inRecord,"HCR_GP"));
			recEditRec.setField("SCARFING_YN",        		ydDaoUtils.paraRecChkNull(inRecord,"SCARFING_YN"));
			recEditRec.setField("SCARFING_DONE_YN",        	ydDaoUtils.paraRecChkNull(inRecord,"SCARFING_DONE_YN"));
			recEditRec.setField("WO_MSLAB_RPR_MTD",        	ydDaoUtils.paraRecChkNull(inRecord,"WO_MSLAB_RPR_MTD"));
			recEditRec.setField("REHEAT_SLAB_GP",        	ydDaoUtils.paraRecChkNull(inRecord,"REHEAT_SLAB_GP"));
			recEditRec.setField("ITEMNAME_CD",        		ydDaoUtils.paraRecChkNull(inRecord,"ITEMNAME_CD"));
			recEditRec.setField("OVERALL_STAMP_GRADE",      ydDaoUtils.paraRecChkNull(inRecord,"OVERALL_STAMP_GRADE"));
			recEditRec.setField("HANDSCARFING_YN",        	ydDaoUtils.paraRecChkNull(inRecord,"HANDSCARFING_YN"));
			recEditRec.setField("DEMANDER_CD",      		ydDaoUtils.paraRecChkNull(inRecord,"DEMANDER_CD"));
			recEditRec.setField("SPEC_ABBSYM",      		ydDaoUtils.paraRecChkNull(inRecord,"SPEC_ABBSYM"));
			

		} catch(Exception e){

			return -1;
		}
		return 1;

	} //end of edtPmSlabYdstock()()
	
	/**
	 * 슬라브충당실적 (PMYDJ001) 타임 아웃 시간 지정: 10분 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @weblogic.transaction-descriptor trans-timeout-seconds="600"
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procSlabMatchWr(JDTORecord inRecord)throws JDTOException  {
		
		// DAO 및 UTIL 객체 생성
		YdStockDao ydStockDao                   = new YdStockDao();
		YdCodeMapping ydCodeMapping             = new YdCodeMapping();
		
		// 변수선언
		String szMethodName                     = "procSlabMatchWr";
		String szOperationName                  = "슬라브충당실적(PMYDJ001)";
		String szMsg                            = "";
		
		// 레코드 선언
		JDTORecordSet rsResult 				    = null;
		JDTORecord recPara  			        = null;
		JDTORecord recResult  			        = null;
		JDTORecord recGetValMSlab		        = null;
		JDTORecord recGetVal		        	= null;
		
		int nRet                                = 0;
		
		String szRcvTcCode 						= "PMYDJ001";
		
		try{	
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[공정계획] 슬라브충당실적 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			String sFrDate  	= ydDaoUtils.paraRecChkNull(inRecord, "WRK_HDS_DD1");
			String sFrSeqno  	= ydDaoUtils.paraRecChkNull(inRecord, "STEP_NO1");
			String sFrStatCd	= ydDaoUtils.paraRecChkNull(inRecord, "MATCH_MTD_GP1");
			
			szMsg = "[" + szOperationName + "] 메소드 시작 - 슬라브충당실적전문내용확인(WRK_HDS_DD1)" + sFrDate +"\n" 
				  + "[" + szOperationName + "] 메소드 시작 - 슬라브충당실적전문내용확인(STEP_NO1)" + sFrSeqno +"\n"
				  + "[" + szOperationName + "] 메소드 시작 - 슬라브충당실적전문내용확인(MATCH_MTD_GP1)" + sFrStatCd +"\n";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(!sFrDate.equals(""))
			{
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("WRK_HDS_DD1", sFrDate);
				recPara.setField("STEP_NO1", sFrSeqno);
				recPara.setField("MATCH_MTD_GP1", sFrStatCd);
				
				nRet = ydStockDao.getYdStock(recPara, rsResult, 221);
				if(nRet <= 0){
					szMsg = "[슬라브충당실적] 충당대상 존재안함  nRet(" + nRet + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return ;
				}
				
				String sStlNo 			= "";
				//=========================================================================================================
				// 주편에서 조회된 N건 수 만큼 반복 (index는 0에서부터 처리 함, 레코드는 1부터가 시작)
				//=========================================================================================================
				for(int nIdx=0; nIdx<rsResult.size(); nIdx++){
					
					rsResult.absolute(nIdx+1);
					recGetValMSlab 	= rsResult.getRecord();
					recPara   		= JDTORecordFactory.getInstance().create();
					this.edtSlabToYdStock(recGetValMSlab, recPara);
					
					recPara.setField("MODIFIER", "PMYDJ001");
					nRet = ydStockDao.updYdStock(recPara, 0);
					
					sStlNo 			= ydDaoUtils.paraRecChkNull(recGetValMSlab, "STL_NO");
					//================================================================
					// 야드저장품 항목을 업데이트 한다.
					//================================================================
					nRet = ydCodeMapping.getMappingCommonField(szRcvTcCode, sStlNo, false);
				}
			}
			/* 2012.07.18 윤재광 
			 * 사용안함 - 후판마킹정보관련.
			 * else{
				YdMarkingHistDao ydMarkingHistDao = new YdMarkingHistDao();
				 -----------------------------------------------------------------------------------------
				 * 2010.01.18 슬라브 충당 실적 전문을 통해서 후판제품 정보 반송 수신
				 * 
				 * 1. 수신 받은 재료가 야드구분 'K' = 후판제품
				 *    com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECOMM_PIDEV 
				 *    
				 * 2. Marking 이력 Table
				 *    STL_NO	    			VARCHAR2(11)	Not Null	재료번호
				 *    WORK_STEP_NO				NUMBER(2)	    Not Null	작업차수
				 * 
				 * 3. Max(작업차수) Insert 
				 *    MK_MOD_RSN				VARCHAR2(1)		Marking 변경사유
				 *    MK_MOD_RSN_REG_DT			DATE			Marking 변경사유등록일시
				 *    
				 *    코드값	코드값명
				 *    1	정보반송
				 *    2	정보반납
				 *    3	제품목전충당
				 *    4	현물반송
				 *    5	현물반납
				  -----------------------------------------------------------------------------------------
				// 레코드 생성
				recPara  = JDTORecordFactory.getInstance().create();
				recResult= JDTORecordFactory.getInstance().create();
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				
				String szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");
				
				recPara.setField("PLATE_NO", szSTL_NO);
				nRet = ydStockDao.getYdStock(recPara, rsResult, 4);
				
				if(nRet < 0){
					szMsg = "[슬라브충당실적 (PMYDJ001)] (레코드상태 3, 아무값도 없는것) 후판제품 정보 반송 수신  파라미터 에러 nRet(" + nRet + ") STL_NO" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				} else if(nRet == 0){
					szMsg = "[슬라브충당실적 (PMYDJ001)] (레코드상태 3, 아무값도 없는것) 후판제품 정보 반송 수신 조회 건수 없음 nRet(" + nRet + ") STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return ;
				} else {
					szMsg = "[슬라브충당실적 (PMYDJ001)] (레코드상태 3, 아무값도 없는것) 후판제품 정보 반송 수신 조회 성공 nRet(" + nRet + ") STL_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
				}	
				
				rsResult.first();
				recGetVal = null;
				recGetVal = rsResult.getRecord();
				String szYD_GP = ydDaoUtils.paraRecChkNull(recGetVal, "YD_GP");
				
				if(szYD_GP.equals("K")){ // 후판제품
					String szMAXWORK_STEP_NO = "";
					//================================================================================================
					// TB_YD_MARKINGHIST에서 해당 재료번호의 최대차수 구함 (GP : 1)
					//
					// com.inisteel.cim.yd.dao.ydmarkinghist.YdMarkingHistDao.getYdMarkingHistMaxStepNoByStlNo
					//
					// 파라미터 : STL_NO ( V_STL_NO )
					//================================================================================================
					// 레코드 생성
					recPara = JDTORecordFactory.getInstance().create();
					rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					recPara.setField("STL_NO", szSTL_NO);
					nRet = ydMarkingHistDao.getYdMarkingHist(recPara, rsResult, 1);
					if(nRet < 0){
						szMsg = "[PMYDJ001]TB_YD_MARKINGHIST MAX차수 조회 오류 Ret(" + nRet + ")";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					} else if(nRet == 0){
						szMsg = "[PMYDJ001]TB_YD_MARKINGHIST MAX차수 조회 건수가 없음 Ret(" + nRet + ")";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
						szMAXWORK_STEP_NO = "0";
					} else if(nRet > 0) {
						rsResult.first();
						recGetVal = rsResult.getRecord();
						szMAXWORK_STEP_NO = ydDaoUtils.paraRecChkNull(recGetVal, "WORK_STEP_NO");
						szMsg = "[PMYDJ001]TB_YD_MARKINGHIST MAX차수 조회 Ret(" + nRet + ") MAX(" + szMAXWORK_STEP_NO + ")";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
					}			
	
					//====================================================================
					// Marking 이력테이블에 등록 
					//====================================================================	
					// 레코드 생성
					recResult = JDTORecordFactory.getInstance().create();
	
					// 등록처리
					recResult.setField("STL_NO"             , szSTL_NO);
					recResult.setField("WORK_STEP_NO"       , "" + (Integer.parseInt(szMAXWORK_STEP_NO)+1)); // 해당재료번호의 최대차수에서 +1차수, 없다면 1차
					recResult.setField("REGISTER"           , "PMYDJ001");                                     
					recResult.setField("OCCUR_DDTT"         , YdUtils.getCurDate("yyyyMMddHHmmss"));         // 현재일시 YYYYMMDDHH24MISS
					recResult.setField("MK_MOD_EXN"         , "N"); 
					recResult.setField("MK_MOD_DT"          , "");                                           // 화면에서 처리할 항목이므로 공백  
					recResult.setField("MK_MOD_RSN"         , "1");                                 		 // 지시차공정공장코드를 통해 정보 반송 구분
					recResult.setField("MK_MOD_RSN_REG_DT"  , YdUtils.getCurDate("yyyyMMddHHmmss"));         // 현재일시 YYYYMMDDHH24MISS
					recResult.setField("ORD_NO"             , ydDaoUtils.paraRecChkNull(inRecord, "ORD_NO"));     
					recResult.setField("ORD_DTL"            , ydDaoUtils.paraRecChkNull(inRecord, "ORD_DTL"));
					recResult.setField("WO_CAR_PLNT_PROC_CD", "");
					recResult.setField("CANCEL_YN"          , "N");
					
					nRet = ydMarkingHistDao.insYdMarkingHist(recResult);
					if(nRet <= 0){
						szMsg = "[PMYDJ001]TB_YD_MARKINGHIST[YD_MARKING 이력] INSERT ERROR :: [" + nRet + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					} else {
						szMsg = "[PMYDJ001]TB_YD_MARKINGHIST[YD_MARKING 이력] INSERT SUCCESSFULL :: [" + nRet + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
					}
					//======================================================
					// 저장품제원 : 후판제품 L2 로 송신(YDY4L002)
					//======================================================
					// 레코드 생성
					recResult = JDTORecordFactory.getInstance().create();
					recResult.setField("MSG_ID"         , "YDY4L002");
					recResult.setField("YD_INFO_SYNC_CD", "5");            // 5:지정저장품
					recResult.setField("STL_NO"         , szSTL_NO);
					recResult.setField("YD_STK_COL_GP"  , "");
					recResult.setField("YD_STK_BED_NO"  , "");
					ydDelegate.sendMsg(recResult);			
					
					szMsg = "[PMYDJ001]후판제품L2로 저장품제원 전문(YDY4L002) 송신";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
				}
			}*/
		}catch(Exception e){
			szMsg="Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try-catch
	} // end of procSlabMatchWr()
	
	/**
	 * 슬라브충당실적(PMYDJ001) - 주편 항목을 야드저장품 항목으로 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtSlabToYdStock(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {

		String szMethodName	= "edtSlabToYdStock";
		String szMsg		= "";
		
		try{
			recEditRec.setField("STL_NO",        			ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			recEditRec.setField("YD_STK_LOT_CD",        	ydDaoUtils.paraRecChkNull(inRecord,"STACK_LOT_NO"));
			recEditRec.setField("YD_MTL_T",        			ydDaoUtils.paraRecChkNull(inRecord,"SLAB_T"));   // 쿼리에서 Alias는 SLAB_T 로 바꾸었지만 편집함수는 주편/슬라브 따로 두었음
			recEditRec.setField("YD_MTL_W",        			ydDaoUtils.paraRecChkNull(inRecord,"SLAB_W"));   // 쿼리에서 Alias는 SLAB_W 로 바꾸었지만 편집함수는 주편/슬라브 따로 두었음 
			recEditRec.setField("YD_MTL_L",        			ydDaoUtils.paraRecChkNull(inRecord,"SLAB_LEN")); // 쿼리에서 Alias는 SLAB_LEN 로 바꾸었지만 편집함수는 주편/슬라브 따로 두었음 
			recEditRec.setField("YD_MTL_WT",        		ydDaoUtils.paraRecChkNull(inRecord,"SLAB_WT"));  // 쿼리에서 Alias는 SLAB_WT 로 바꾸었지만 편집함수는 주편/슬라브 따로 두었음
			recEditRec.setField("STL_APPEAR_GP",        	ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recEditRec.setField("PLNT_PROC_CD",        		ydDaoUtils.paraRecChkNull(inRecord,"PLNT_PROC_CD"));
			recEditRec.setField("STL_PROG_CD",        		ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			recEditRec.setField("ORD_YEOJAE_GP",        	ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));
			recEditRec.setField("ORD_NO",        			ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO"));
			recEditRec.setField("ORD_DTL",        			ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL"));
			recEditRec.setField("SLAB_WO_RT_CD",        	ydDaoUtils.paraRecChkNull(inRecord,"SLAB_WO_RT_CD"));
			recEditRec.setField("ORD_HCR_GP",        		ydDaoUtils.paraRecChkNull(inRecord,"ORD_HCR_GP"));
			recEditRec.setField("HCR_GP",        			ydDaoUtils.paraRecChkNull(inRecord,"HCR_GP"));
			recEditRec.setField("SCARFING_YN",        		ydDaoUtils.paraRecChkNull(inRecord,"SCARFING_YN"));
			recEditRec.setField("SCARFING_DONE_YN",        	ydDaoUtils.paraRecChkNull(inRecord,"SCARFING_DONE_YN"));
			recEditRec.setField("WO_MSLAB_RPR_MTD",        	ydDaoUtils.paraRecChkNull(inRecord,"WO_MSLAB_RPR_MTD"));
			recEditRec.setField("ITEMNAME_CD",        		ydDaoUtils.paraRecChkNull(inRecord,"ITEMNAME_CD"));  
			recEditRec.setField("DEMANDER_CD",      		ydDaoUtils.paraRecChkNull(inRecord,"DEMANDER_CD"));
			recEditRec.setField("SPEC_ABBSYM",      		ydDaoUtils.paraRecChkNull(inRecord,"SPEC_ABBSYM"));
			recEditRec.setField("PTOP_PLNT_GP", 			ydDaoUtils.paraRecChkNull(inRecord,"PTOP_PLNT_GP"));
			recEditRec.setField("YD_GP", 					ydDaoUtils.paraRecChkNull(inRecord,"YD_GP"));
			stock.SetYD_MTL_ITEM_SLAB(recEditRec);
				
		} catch(Exception e){
			szMsg="Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}
		return 1;
	} 
	
	/**
	 * 코일충당실적 (PTYDJ001)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilMatchWr(JDTORecord inRecord)throws JDTOException  {
		//저장품DAO
		YdStockDao ydStockDao 					= new YdStockDao();

		JDTORecordSet rsCoilcomm				= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsYdStock			 		= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recGetCoil					= null;
		JDTORecord recGetStock					= null;
		JDTORecord recEdit	 					= null;
		JDTORecord recResult	 				= null;

		String szMethodName 					= "procCoilMatchWr";
		String szMsg 							= "";
		String szOperationName                  = "코일충당실적";
		String szOCCUR_DDTT 					= "";
		String szSTL_NO							= "";
		int intRtnVal = 0;

		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode = ydUtils.getTcCode(inRecord);
		if(szRcvTcCode == null){
			//수신한 전문이 null이라면 error
			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[진행관리] 코일충당실적 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
							
			// 수신항목 - 충당일시
			szOCCUR_DDTT= ydDaoUtils.paraRecChkNull(inRecord, "OCCUR_DDTT");

			// 목전충당여재처리이력 +코일공통 조회
			intRtnVal	= ydStockDao.getYdStock(inRecord, rsCoilcomm, 80);
			if(intRtnVal <= 0){
				if(intRtnVal == 0){
					szMsg = "ORDERTRANSMATCHLOG[목전충당여재처리이력] Error:: 충당일시["
						+szOCCUR_DDTT+"]를 만족하는 결과 없음 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}else{
					szMsg= "ORDERTRANSMATCHLOG[목전충당여재처리이력] Error :: [" 
						+ intRtnVal + "]"+"PARAMETER ERROR" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}
			
			for(int i=0; i<rsCoilcomm.size();i++){
				recEdit = JDTORecordFactory.getInstance().create();		
				recGetCoil = rsCoilcomm.getRecord(i);
				intRtnVal = this.edtCoilYdstock(recGetCoil, recEdit);
				if( intRtnVal < 0 ){
					szMsg= "[코일충당실적 항목편집] Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}	

				// 재료번호
				szSTL_NO	= ydDaoUtils.paraRecChkNull(recEdit, "STL_NO");

				// 저장품 조회
				intRtnVal 	= ydStockDao.getYdStock(recEdit, rsYdStock, 0);

				// 저장품 조회 Error
				if(intRtnVal < 0){
					szMsg= "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
					return ;
				}
				// 저장품 갱신
				else if(intRtnVal >0){

					rsYdStock.first();
					recGetStock = rsYdStock.getRecord();

					ydUtils.putLog(szSessionName, szMethodName, "YD_STOCK[저장품] UPDATE :: [2] YD_STOCK UPDATE -"+szSTL_NO, 3);

					recEdit.setField("MODIFIER", "PTYDJ001");
					intRtnVal = ydStockDao.updYdStock(recEdit, 0);
					if(intRtnVal <=0){
						szMsg= "YD_STOCK[저장품] UPDATE :: [2] YD_STOCK UPDATE Error:: [" + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
					ydUtils.putLog(szSessionName, szMethodName, szSTL_NO+")YD_STOCK[저장품] UPDATE :: [2] YD_STOCK UPDATE SUCCESS ", 3);	

				}
				// 저장품 등록
				else{

					ydUtils.putLog(szSessionName, szMethodName, "YD_STOCK[저장품] INSERT :: "+szSTL_NO , 3);

					recEdit.setField("REGISTER", "PTYDJ001");

					intRtnVal = ydStockDao.insYdStock(recEdit);
					if(intRtnVal <0){
						szMsg= "YD_STOCK[저장품] INSERT Error :: [" + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
						return ;
					}
					ydUtils.putLog(szSessionName, szMethodName, szSTL_NO+")YD_STOCK[저장품] INSERT :: SUCCESS" , 3);	
				}

				//======================================================
				// 2009.09.08 권오창
				// 저장품제원 : 열연코일L2 로 송신(YDY5L002)
				//======================================================
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YDY5L002");
				recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
				recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recEdit, "STL_NO"));
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				ydDelegate.sendMsg(recResult);
			} 

		}catch(Exception e){

			szMsg="[코일충당실적]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try-catch

		szMsg="코일충당실적  등록("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of procCoilMatchWr()
	
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
		try{
			szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP");
			if(szSTL_APPEAR_GP.equals("E")){
				szYD_MTL_ITEM = "CM";
			}else if(szSTL_APPEAR_GP.equals("Y")){
				szYD_MTL_ITEM = "CG";
			}

			szSTL_PROG_CD = ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD");	
			if(szSTL_PROG_CD.equals("Z")){
				szYD_AIM_RT_GP = "Z2";
			}
			
			szPTOP_PLNT_GP = ydDaoUtils.paraRecChkNull(inRecord,"PTOP_PLNT_GP");	
			if(szPTOP_PLNT_GP.equals("HA")){
				szYD_AIM_YD_GP = "1";
			}else if(szPTOP_PLNT_GP.equals("HB")){
				szYD_AIM_YD_GP = "3";
			}else if(szPTOP_PLNT_GP.equals("HC")){
				if(szYD_MTL_ITEM.equals("CM")){
					szYD_AIM_YD_GP = "H";
				}else if(szYD_MTL_ITEM.equals("CG")){
					szYD_AIM_YD_GP = "J";
				}
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
	
			
		} catch(Exception e){

			return -1;
		}
		return 1;

	} //end of edtCoilYdstock()
	
	
	/**
	 *      [A] 오퍼레이션명 : A후판 제품행선변경실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procAPlGdsRtChngWr(JDTORecord msgRecord)throws JDTOException  {
		
		// 변수 선언
		String szMethodName            = "procAPlGdsRtChngWr";
		String szMsg                   = "";
		String szOperationName         = "A후판 제품행선변경실적";
		
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode == null){
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		if(bDebugFlag){
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[후판조업] 제품행선변경실적 수신";
			ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

		}catch(Exception e){
			szMsg = "[A후판제품행선변경실적수신] Exception Error:" + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
	}// end of procAPlGdsRtChngWr()
		
	/**
	 * 코일제품반납대기(DMYDR008) - J2
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilGdsRetnWait(JDTORecord inRecord)throws JDTOException  {
		// 저장품DAO
		YdStockDao ydStockDao 					= new YdStockDao();
		
		JDTORecord recStockColumn 			= JDTORecordFactory.getInstance().create();

		String szMethodName 				= "procCoilGdsRetnWait";
		String szMsg 						= "";
		String szOperationName              = "코일제품반납대기";
		String szSTL_NO 					= "";
				
		int intRtnVal						= 0;
		
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 코일제품반납대기 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//수신한 재료번호
			szSTL_NO  = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");
			
			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP			재료외형구분
			STL_NO					재료번호
			CURR_PROG_CD			현재진도코드
			WO_CAR_PLNT_PROC_CD		지시차공장공정코드
			FRTOMOVE_ORD_DATE		이송지시일자
			URGENT_FRTOMOVE_WORD_GP	긴급이송작업지시구분
			 */
			
			recStockColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recStockColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STL_PROG_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			recStockColumn.setField("WO_CAR_PLNT_PROC_CD", 	ydDaoUtils.paraRecChkNull(inRecord,"WO_CAR_PLNT_PROC_CD"));
//			recStockColumn.setField("FRTOMOVE_ORD_DATE", 				ydDaoUtils.paraRecChkNull(inRecord,"FRTOMOVE_ORD_DATE"));
			recStockColumn.setField("URGENT_FRTOMOVE_WORD_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"URGENT_FRTOMOVE_WORD_GP"));
			recStockColumn.setField("MODIFIER", 			"DMYDR008");

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("C",inRecord );		
			inRecord.setField("YD_AIM_RT_GP", rVal[0]);
			//****************************************************************************************************

			
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(inRecord, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[코일제품반납대기] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName, "[2] YD_STOCK[코일제품반납대기] UPDATE Success",3);
			//****************************************************************************************************

			//======================================================
			// 저장품제원 : 코일야드L2 로 송신(YDY5L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY5L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			ydDelegate.sendMsg(recResult);
			
		}catch(Exception e){
			
			szMsg = "[코일제품반납대기]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		
		szMsg = "[코일제품반납대기]수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procCoilGdsRetnWait()
	
	
	
	/**
	 * 후판제품반납대기(DMYDR009) - J3  [권오창 2009.11.25]
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlGdsRetnWait(JDTORecord inRecord)throws JDTOException  {
		
		// DAO 및 UTIL 객체 생성
		YdStockDao ydStockDao 					= new YdStockDao();
		
		YdMarkingHistDao ydMarkingHistDao       = new YdMarkingHistDao();
		
		// 레코드 선언
		JDTORecordSet rsPlateComm 	            = null;
		JDTORecordSet rsResult               	= null;
		JDTORecord recStockColumn 	            = null;
		JDTORecord recPara 			            = null;
		JDTORecord recResult                    = null;
		JDTORecord recGetVal                    = null;
		
		// 변수 선언
		String szMethodName 				    = "procPlGdsRetnWait";
		String szMsg 							= "";
		String szOperationName                  = "후판제품반납대기";
		String szSTL_APPEAR_GP       	        = "";
		String szSTL_NO 						= "";
		String szCURR_PROG_CD               	= "";
		String szWO_CAR_PLNT_PROC_CD        	= "";
		String szURGENT_FRTOMOVE_WORD_GP   	    = "";
		String szCANCEL_YN 	       				= "";
		String szORD_NO                     	= "";
		String szORD_DTL                    	= "";
		String szMAXWORK_STEP_NO            	= "0";
		String szMK_MOD_RSN                     = "";
		int intRtnVal							= 0;
		int nRet                            	= 0;
		
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;

		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 후판제품반납대기 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			//=============================================================
			// 수신전문 항목 값 추출
			//=============================================================
			szSTL_APPEAR_GP              = ydDaoUtils.paraRecChkNull(inRecord, "STL_APPEAR_GP");
			szSTL_NO 					 = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");                  // 수신전문의 재료번호는 PLATE_NO
			szCURR_PROG_CD               = ydDaoUtils.paraRecChkNull(inRecord, "CURR_PROG_CD");
			szWO_CAR_PLNT_PROC_CD        = ydDaoUtils.paraRecChkNull(inRecord, "WO_CAR_PLNT_PROC_CD");
			szURGENT_FRTOMOVE_WORD_GP    = ydDaoUtils.paraRecChkNull(inRecord, "URGENT_FRTOMOVE_WORD_GP");
			szCANCEL_YN 	       		 = ydDaoUtils.paraRecChkNull(inRecord, "CANCEL_YN");			

			/*
			1	정보반송
			2	정보반납
			3	제품목전충당
			4	현물반송
			5	현물반납
            */
			
			if(szWO_CAR_PLNT_PROC_CD.equals("")){
				// 정보반납
				szMK_MOD_RSN = "2"; 
			} else {
				// 현물반납
				szMK_MOD_RSN = "5"; 			
			}
			
			//수신한 재료번호
			szSTL_NO  = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");

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
			recStockColumn.setField("STL_APPEAR_GP"          , szSTL_APPEAR_GP);
			recStockColumn.setField("STL_NO"                 , szSTL_NO);
			recStockColumn.setField("STL_PROG_CD"            , szCURR_PROG_CD);
			recStockColumn.setField("WO_CAR_PLNT_PROC_CD"    , szWO_CAR_PLNT_PROC_CD);
//			recStockColumn.setField("FRTOMOVE_ORD_DATE"      , szFRTOMOVE_ORD_DATE);
			recStockColumn.setField("URGENT_FRTOMOVE_WORD_GP", szURGENT_FRTOMOVE_WORD_GP);
			recStockColumn.setField("MODIFIER"               , "DMYDR009");

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("P",inRecord );		
			inRecord.setField("YD_AIM_RT_GP", rVal[0]);
			inRecord.setField("STL_PROG_CD",  ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			//****************************************************************************************************

			
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(inRecord, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[후판제품반납대기] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName, "[2] YD_STOCK[후판제품반납대기] UPDATE Success",3);
			//****************************************************************************************************

			//================================================================================================
			// TB_PT_PLATECOMM 에서 주분번호, 주문행번, 충당일자를 반환 (GP : 4)
			//
			// com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECOMM_PIDEV       
			// 
			// 파라미터 : PLATE_NO ( V_PLATE_NO )
			//================================================================================================			
			// 레코드 생성
			/* 2012.07.18 윤재광
			 * 사용안함-후판마킹정보
			 * recPara = JDTORecordFactory.getInstance().create();
			rsPlateComm = JDTORecordFactory.getInstance().createRecordSet("");

			// PLATECOMM 조회 (주문번호, 주문행번, 충당일자)
			// 확인 : 수신받는 재료번호가 PLATE_NO이고 PLATE_NO로 조회 한다면 위에 재료이력에 INSERT시에 저장품에 없다면
			// return 됨 (PLATE_NO인지 SLAB_NO인지 확인)
			recPara.setField("PLATE_NO", szSTL_NO);
			nRet = ydStockDao.getYdStock(recPara, rsPlateComm, 4);
			if(nRet < 0){
				szMsg = "TB_PT_PLATECOMM 조회 오류 Ret(" + nRet + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(nRet == 0){
				szMsg = "TB_PT_PLATECOMM 조회 건수가 없음 Ret(" + nRet + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
			} else if(nRet > 0) {
				rsPlateComm.first();
				recGetVal = rsPlateComm.getRecord();
				
				szORD_NO     = ydDaoUtils.paraRecChkNull(recGetVal, "ORD_NO");       // 주문번호
				szORD_DTL    = ydDaoUtils.paraRecChkNull(recGetVal, "ORD_DTL");      // 주문행번   
				
				szMsg = "TB_PT_PLATECOMM 조회  ORD_NO(" + szORD_NO + ") ORD_DTL(" + szORD_DTL + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);									
			}
			
			//================================================================================================
			// TB_YD_MARKINGHIST에서 해당 재료번호의 최대차수 구함 (GP : 1)
			//
			// com.inisteel.cim.yd.dao.ydmarkinghist.YdMarkingHistDao.getYdMarkingHistMaxStepNoByStlNo
			//
			// 파라미터 : STL_NO ( V_STL_NO )
			//================================================================================================
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara.setField("STL_NO", szSTL_NO);
			nRet = ydMarkingHistDao.getYdMarkingHist(recPara, rsResult, 1);
			if(nRet < 0){
				szMsg = "TB_YD_MARKINGHIST MAX차수 조회 오류 Ret(" + nRet + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(nRet == 0){
				szMsg = "TB_YD_MARKINGHIST MAX차수 조회 건수가 없음 Ret(" + nRet + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
				szMAXWORK_STEP_NO = "0";
			} else if(nRet > 0) {
				rsResult.first();
				recGetVal = rsResult.getRecord();
				szMAXWORK_STEP_NO = ydDaoUtils.paraRecChkNull(recGetVal, "WORK_STEP_NO");
				szMsg = "TB_YD_MARKINGHIST MAX차수 조회 Ret(" + nRet + ") MAX(" + szMAXWORK_STEP_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
			}			

			//====================================================================
			// 취소유무에 따라 "N"이면 Marking이력테이블에 등록 
			// "Y"면 Marking이력테이블에서 삭제처리(해당 재료번호의 MAX차수에 대해DEL_YN처리)
			//====================================================================	
			// 레코드 생성
			recResult = JDTORecordFactory.getInstance().create();

			if(szCANCEL_YN.equals("N")){
				// 등록처리
				recResult.setField("STL_NO"             , szSTL_NO);
				recResult.setField("WORK_STEP_NO"       , "" + (Integer.parseInt(szMAXWORK_STEP_NO)+1)); // 해당재료번호의 최대차수에서 +1차수, 없다면 1차
				recResult.setField("REGISTER"           , "DMYDR009");                                    
				recResult.setField("OCCUR_DDTT"         , YdUtils.getCurDate("yyyyMMddHHmmss"));         // 현재일시 YYYYMMDDHH24MISS
				
				if(szMK_MOD_RSN.equals("2")){
					recResult.setField("MK_MOD_EXN"         , "Y");                                           
					recResult.setField("MK_MOD_DT"          , YdUtils.getCurDate("yyyyMMddHHmmss"));     // 정보 반납					
				}else{
					recResult.setField("MK_MOD_EXN"         , "N");                                           
					recResult.setField("MK_MOD_DT"          , "");                                       // 화면에서 처리하는 항목이므로 공백					
				}

				recResult.setField("MK_MOD_RSN"         , szMK_MOD_RSN);                                 // 지시차공장공정코드를 통해 정보/현물 반납구분
				recResult.setField("MK_MOD_RSN_REG_DT"  , YdUtils.getCurDate("yyyyMMddHHmmss"));         // 현재일시 YYYYMMDDHH24MISS 
				recResult.setField("ORD_NO"             , szORD_NO);
				recResult.setField("ORD_DTL"            , szORD_DTL);
				recResult.setField("WO_CAR_PLNT_PROC_CD", szWO_CAR_PLNT_PROC_CD);
				recResult.setField("CANCEL_YN"          , szCANCEL_YN);
				
				nRet = ydMarkingHistDao.insYdMarkingHist(recResult);
				if(nRet <= 0){
					szMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] INSERT ERROR :: [" + nRet + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				} else {
					szMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] INSERT SUCCESSFULL :: [" + nRet + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
				}
			} else if(szCANCEL_YN.equals("Y")){
				if(Integer.parseInt(szMAXWORK_STEP_NO) > 0){
					// 삭제처리
					recResult.setField("MODIFIER"     , "DMYDR009");
					recResult.setField("DEL_YN"       , "Y");
					recResult.setField("STL_NO"       , szSTL_NO);
					recResult.setField("WORK_STEP_NO" , szMAXWORK_STEP_NO);        // 삭제는 해당 재료번호의 최대차수
					nRet = ydMarkingHistDao.updYdMarkingHist(recResult, 0);
					if(nRet <= 0){
						szMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] UPDATE ERROR :: [" + nRet + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;					
					} else {
						szMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] UPDATE SUCCESSFULL :: [" + nRet + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);										
					}		
					
					// Facade에 있던 취소유무 'Y' 수신시 처리하는 EJB호출 
					EJBConnector ydEjbCon = new EJBConnector("default", this);
					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);
				}
			}*/
			
			//======================================================
			// 저장품제원 : 후판제품 L2 로 송신(YDY4L002,YDY8L002)
			//======================================================
			recResult = JDTORecordFactory.getInstance().create();
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(ydDaoUtils.paraRecChkNull(inRecord,"YD_GP"))) { //- 2013.01.16 수정 (3기)
				recResult.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고
			} else {
				recResult.setField("MSG_ID"         , "YDY4L002"); //1후판 제품창고
			}			
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			
			// 2021. 05. 17 전문생성 시점에 PT_PLATE_COMM에 진도코드가 바뀌지 않은 문제가 있어
			// 진도코드를 파라메터로 넘겨 처리함
			recResult.setField("CURR_PROG_CD"  , szCURR_PROG_CD);
			
			
			ydDelegate.sendMsg(recResult);		
			
			// 전사물류개선 2021. 4. 3
			if(PlateGdsYdUtil.isSendToEaiY9_stlNo( ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO")) ){
				recResult.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
				ydDelegate.sendMsg(recResult);
			}
			
		}catch(Exception e){
			
			szMsg = "[후판제품반납대기]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		
		szMsg = "[후판제품반납대기]수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procPlGdsRetnWait()
	
	/**
	 * 외판슬라브목전(DMYDR013)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procOutplSlabOrdtrn(JDTORecord inRecord)throws JDTOException  {
		// 저장품DAO
		YdStockDao ydStockDao 					= new YdStockDao();
		
		JDTORecord recStockColumn 		= JDTORecordFactory.getInstance().create();

		String szMethodName 			= "procOutplSlabOrdtrn";
		String szMsg				 	= "";
		String szOperationName          = "외판슬라브목전";
		String szSTL_NO 				= "";
		
		int intRtnVal 					= 0;
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ; 
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 외판슬라브목전 수신";
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
			DLVRDD_RULE_DD		납기기준일
			DEST_TEL_NO			목적지전화번호
			DIST_SHIPASSIGN_GP	출하배선지시구분
			*/
			
			recStockColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recStockColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STL_PROG_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			recStockColumn.setField("ORD_YEOJAE_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));
			recStockColumn.setField("ORD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO"));
			recStockColumn.setField("ORD_DTL", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL"));
			recStockColumn.setField("ORD_GP", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_GP"));
			recStockColumn.setField("CUST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD"));
			recStockColumn.setField("DEST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"DEST_CD"));
			recStockColumn.setField("YD_DLVRDD_RULE_DD", 	ydDaoUtils.paraRecChkNull(inRecord,"DLVRDD_RULE_DD"));
			recStockColumn.setField("DEST_TEL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"DEST_TEL_NO"));
			recStockColumn.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"));
			recStockColumn.setField("MODIFIER", 			"DMYDR013");

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("S",inRecord );		
			recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
			//****************************************************************************************************

			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[외판슬라브목전] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[외판슬라브목전] UPDATE Success",3);
			//****************************************************************************************************
			
			JDTORecordSet rsResult	= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recGetVal    = null;
			String 	sYD_STK_COL_GP   ="";
			intRtnVal = ydStockDao.getYdStock(recStockColumn, rsResult, 26);
			 
			if(intRtnVal > 0) {
				recGetVal = rsResult.getRecord(0);
				sYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_COL_GP");
				if("".equals(sYD_STK_COL_GP )){
					szMsg= "재료번호 ["+ydDaoUtils.paraRecChkNull(inRecord,"STL_NO")+"] 에 대한 적치단 정보 존재 X" ;
					ydUtils.putLog(szSessionName, szMethodName,szMsg,YdConstant.DEBUG);
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
			recResult.setField("MSG_ID"         , "YDY1L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			ydDelegate.sendMsg(recResult);
			
			}
			
		}catch(Exception e){
			szMsg = "[외판슬라브목전]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		}
		szMsg = "[외판슬라브목전]수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of procOutplSlabOrdtrn()
	
	
	/**
	 * 코일제품목전(DMYDR014)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilGdsOrdtrn(JDTORecord inRecord)throws JDTOException  {
		// 저장품DAO
		YdStockDao ydStockDao 					= new YdStockDao();
		
		JDTORecord recStockColumn 	= JDTORecordFactory.getInstance().create();

		String szMethodName 		= "procCoilGdsOrdtrn";
		String szMsg				= "";
		String szOperationName      = "코일제품목전";
		String szSTL_NO 			= "";

		int intRtnVal 				= 0;
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 코일제품목전 수신";
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
			DLVRDD_RULE_DD		납기기준일
			DEST_TEL_NO			목적지전화번호
			DIST_SHIPASSIGN_GP	출하배선지시구분
			*/
			
			recStockColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recStockColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STL_PROG_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			recStockColumn.setField("ORD_YEOJAE_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));
			recStockColumn.setField("ORD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO"));
			recStockColumn.setField("ORD_DTL", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL"));
			recStockColumn.setField("ORD_GP", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_GP"));
			recStockColumn.setField("CUST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD"));
			recStockColumn.setField("DEST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"DEST_CD"));
			recStockColumn.setField("YD_DLVRDD_RULE_DD", 	ydDaoUtils.paraRecChkNull(inRecord,"DLVRDD_RULE_DD"));
			recStockColumn.setField("DEST_TEL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"DEST_TEL_NO"));
			recStockColumn.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"));
			recStockColumn.setField("MODIFIER", 			"DMYDR014");

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("C",inRecord );		
			recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
			//****************************************************************************************************

			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[코일제품목전] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[코일제품목전] UPDATE Success",3);
			//****************************************************************************************************
			
			//======================================================
			// 2009.08.31 권오창
			// 저장품제원 : 코일야드L2 로 송신(YDY5L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY5L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			ydDelegate.sendMsg(recResult);
			
		}catch(Exception e){
			
			szMsg = "[코일제품목전]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		
		szMsg = "[코일제품목전]수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of procCoilGdsOrdtrn()
	
	
	
	/**
	 * 후판제품목전(DMYDR015) [권오창 2009.11.25]
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlageGdsOrdtrn(JDTORecord inRecord)throws JDTOException  {

		// DAO 및 UTIL 객체 생성
		YdStockDao ydStockDao 					= new YdStockDao();
		
		YdMarkingHistDao ydMarkingHistDao       = new YdMarkingHistDao();
		PtPlateCommDao ptPlateCommDao   		= new PtPlateCommDao();
		
		// 레코드 선언
		JDTORecordSet rsResult               	= null;
		JDTORecord recStockColumn 	            = null;
		JDTORecord recPara 			            = null;
		JDTORecord recResult                    = null;
		JDTORecord recGetVal                    = null;
		
		String szMethodName 			= "procPlageGdsOrdtrn";
		String szMsg 					= "";
		String szOperationName 			= "후판제품목전";
		String szSTL_NO 				= "";
		String szORD_NO 				= "";
		String szORD_DTL 				= "";
		String szMAXWORK_STEP_NO 		= "";
		String szWO_CAR_PLNT_PROC_CD 	= "";
		String szMK_MOD_RSN 			= "";
		String szCANCEL_YN 				= "";
		int nRet 						= 0;	
		int intRtnVal 					= 0;
			
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 후판제품목전 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

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
			DLVRDD_RULE_DD		납기기준일
			DEST_TEL_NO			목적지전화번호
			DIST_SHIPASSIGN_GP	출하배선지시구분
			*/
			// 수신 전문 항목 추출
			szSTL_NO             = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");
			szORD_NO             = ydDaoUtils.paraRecChkNull(inRecord, "ORD_NO");
			szORD_DTL            = ydDaoUtils.paraRecChkNull(inRecord, "ORD_DTL");
			szCANCEL_YN          = ydDaoUtils.paraRecChkNull(inRecord, "CANCEL_YN");
			szMK_MOD_RSN         = "3";// 제품목전충당은 Marking변경사유 3
			
			recStockColumn 	= JDTORecordFactory.getInstance().create();
			recStockColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recStockColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STL_PROG_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			recStockColumn.setField("ORD_YEOJAE_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));
			recStockColumn.setField("ORD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO"));
			recStockColumn.setField("ORD_DTL", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL"));
			recStockColumn.setField("ORD_GP", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_GP"));
			recStockColumn.setField("CUST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD"));
			recStockColumn.setField("DEST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"DEST_CD"));
			recStockColumn.setField("YD_DLVRDD_RULE_DD", 	ydDaoUtils.paraRecChkNull(inRecord,"DLVRDD_RULE_DD"));
			recStockColumn.setField("DEST_TEL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"DEST_TEL_NO"));
			recStockColumn.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"));
			recStockColumn.setField("MODIFIER", 			"DMYDR015");
			recStockColumn.setField("YD_AIM_RT_GP", 		"K3");
			
			//-------------------------------------------------------------------------
			//	2010.12.22 윤재광 - 여재다운/충당/목전시 해당 파일링코드 셋팅 시작
			//-------------------------------------------------------------------------
			String sPlateNo	= ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");
			
			JDTORecord recIn 			= null;
			JDTORecord recTemp 			= null;
			JDTORecord recEdit 			= null;
			
			JDTORecordSet rsOutRecSet 	= null;
			JDTORecordSet rsOut 		= null;
			
			recIn 		= JDTORecordFactory.getInstance().create();
			rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recIn.setField("PLATE_NO", sPlateNo);
			intRtnVal = ydStockDao.getYdStock(recIn, rsOutRecSet, 4);
			
			rsOutRecSet.first();
			recIn = rsOutRecSet.getRecord();
			
			recTemp		= JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_MTL_L", ydDaoUtils.paraRecChkNull(recIn,"PL_MEA_GDS_L"));
			recTemp.setField("YD_MTL_W", ydDaoUtils.paraRecChkNull(recIn,"PL_MEA_GDS_W"));
			
			String sYdPilingCd = "";
			
			if("K".equals(ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"))){
				
				//-------------------------------------------------------------------------
				//	주문재인 경우 OS공통테이블의 정보를 조회해서  Piling Code
				//-------------------------------------------------------------------------
				recEdit = JDTORecordFactory.getInstance().create();
				recEdit.setField("ORD_NO",  ydDaoUtils.paraRecChkNull(recIn,"ORD_NO"));
				recEdit.setField("ORD_DTL", ydDaoUtils.paraRecChkNull(recIn,"ORD_DTL"));
				
				rsOut = JDTORecordFactory.getInstance().createRecordSet("");
				//OS공통조회
				intRtnVal = ydStockDao.getYdStock(recEdit, rsOut, 88);
				
				rsOut.first();
				
				recEdit = rsOut.getRecord();
				
				//recEdit.setRecord(rsOut.getRecord());
				
				sYdPilingCd = ydDaoUtils.paraRecChkNull(recEdit,"YD_PILING_CD");
				
				ydUtils.putLog(szSessionName, szMethodName, "[후판제품목전] sYdPilingCd="+sYdPilingCd,3);
				ydUtils.putLog(szSessionName, szMethodName, "[후판제품목전] rsOut.size()="+rsOut.size(),3);
				ydUtils.putLog(szSessionName, szMethodName, "[후판제품목전] recEdit="+recEdit,3);
				ydUtils.putLog(szSessionName, szMethodName, "[후판제품목전] ORD_PROG_CD="+ydDaoUtils.paraRecChkNull(recEdit,"ORD_PROG_CD"),3);
				ydUtils.putLog(szSessionName, szMethodName, "[후판제품목전] ORD_PROG_STAT="+ydDaoUtils.paraRecChkNull(recEdit,"ORD_PROG_STAT"),3);
				
			}else if("Z".equals(ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"))){
				
				recTemp.setField("STRCHAR_CUST_CD" 			, "");	
				recTemp.setField("STRCHAR_ORD_YEOJAE_GP" 	, "2");
				recTemp.setField("YD_STRCHAR_GRP_CD" 		, "M001");
				
				PlateGdsYdUtil.getWTLGp(recTemp);
					
				sYdPilingCd	  	= "M001" + 
								  ydDaoUtils.paraRecChkNull(recTemp,"YD_MTL_W_GP")+ 
								  ydDaoUtils.paraRecChkNull(recTemp,"YD_MTL_L_GP");
			}
			
			recStockColumn.setField("PLATE_NO", 		sPlateNo);
			recStockColumn.setField("YD_PILING_CD", 	sYdPilingCd);
			recStockColumn.setField("YD_BOOK_OUT_LOC", 	"88888");
			
			intRtnVal = ptPlateCommDao.updPtPlateComm(recStockColumn, 0);
			
			//-------------------------------------------------------------------------
			//	2010.12.22 윤재광 - 여재다운/충당/목전시 해당 파일링코드 셋팅 완료 
			//-------------------------------------------------------------------------
			/*
			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("P",inRecord );		
			recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
			*/
			//****************************************************************************************************
	
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			
			ydUtils.putLog(szSessionName, szMethodName, "[후판제품목전] 전문수신처리 성공",3);
			//****************************************************************************************************

			//================================================================================================
			// TB_YD_MARKINGHIST에서 해당 재료번호의 최대차수 구함 (GP : 1)
			//
			// com.inisteel.cim.yd.dao.ydmarkinghist.YdMarkingHistDao.getYdMarkingHistMaxStepNoByStlNo
			//
			// 파라미터 : STL_NO ( V_STL_NO )
			//================================================================================================
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara.setField("STL_NO", szSTL_NO);
			nRet = ydMarkingHistDao.getYdMarkingHist(recPara, rsResult, 1);
			if(nRet < 0){
				szMsg = "TB_YD_MARKINGHIST MAX차수 조회 오류 Ret(" + nRet + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(nRet == 0){
				szMsg = "TB_YD_MARKINGHIST MAX차수 조회 건수가 없음 Ret(" + nRet + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
				szMAXWORK_STEP_NO = "0";
			} else if(nRet > 0) {
				rsResult.first();
				recGetVal = rsResult.getRecord();
				szMAXWORK_STEP_NO = ydDaoUtils.paraRecChkNull(recGetVal, "WORK_STEP_NO");
				szMsg = "TB_YD_MARKINGHIST MAX차수 조회 Ret(" + nRet + ") MAX(" + szMAXWORK_STEP_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
			}			

			//====================================================================
			// 취소유무에 따라 "N"이면 Marking이력테이블에 등록 
			// "Y"면 Marking이력테이블에서 삭제처리(해당 재료번호의 MAX차수에 대해DEL_YN처리)
			//====================================================================	
			// 레코드 생성
			recResult = JDTORecordFactory.getInstance().create();

			if(szCANCEL_YN.equals("N")){
				// 등록처리
				recResult.setField("STL_NO"             , szSTL_NO);
				recResult.setField("WORK_STEP_NO"       , "" + (Integer.parseInt(szMAXWORK_STEP_NO)+1)); // 해당재료번호의 최대차수에서 +1차수, 없다면 1차
				recResult.setField("REGISTER"           , "DMYDR015");                                    
				recResult.setField("OCCUR_DDTT"         , YdUtils.getCurDate("yyyyMMddHHmmss"));         // 현재일시 YYYYMMDDHH24MISS
				recResult.setField("MK_MOD_EXN"         , "N");                                           
				recResult.setField("MK_MOD_DT"          , "");                                           // 화면에서 처리하는 항목이므로 공백
				recResult.setField("MK_MOD_RSN"         , szMK_MOD_RSN);                                 // 목전충당은 3으로 고정
				recResult.setField("MK_MOD_RSN_REG_DT"  , YdUtils.getCurDate("yyyyMMddHHmmss"));         // 현재일시 YYYYMMDDHH24MISS 
				recResult.setField("ORD_NO"             , szORD_NO);
				recResult.setField("ORD_DTL"            , szORD_DTL);
				//=================================================
				// 2010.01.05 
				// 권오창
				//DMYDR015전문에는 지시차공장공정코드가 없음. 공백처리
				//=================================================
				recResult.setField("WO_CAR_PLNT_PROC_CD", szWO_CAR_PLNT_PROC_CD);      
				recResult.setField("CANCEL_YN"          , szCANCEL_YN);
				
				nRet = ydMarkingHistDao.insYdMarkingHist(recResult);
				if(nRet <= 0){
					szMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] INSERT ERROR :: [" + nRet + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				} else {
					szMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] INSERT SUCCESSFULL :: [" + nRet + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
				}
			} else if(szCANCEL_YN.equals("Y")){
				if(Integer.parseInt(szMAXWORK_STEP_NO) > 0){
					// 삭제처리
					recResult.setField("MODIFIER"     , "DMYDR015");
					recResult.setField("DEL_YN"       , "Y");
					recResult.setField("STL_NO"       , szSTL_NO);
					recResult.setField("WORK_STEP_NO" , szMAXWORK_STEP_NO);        // 삭제는 해당 재료번호의 최대차수
					nRet = ydMarkingHistDao.updYdMarkingHist(recResult, 0);
					if(nRet <= 0){
						szMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] UPDATE ERROR :: [" + nRet + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;					
					} else {
						szMsg = "TB_YD_MARKINGHIST[YD_MARKING 이력] UPDATE SUCCESSFULL :: [" + nRet + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);										
					}		
					
					// Facade에 있던 취소유무 'Y' 수신시 처리하는 EJB호출 
					EJBConnector ydEjbCon = new EJBConnector("default", this);
					ydEjbCon.trx("RtModRegSeEJB", "receiveCancel", inRecord);
				}
			}

			//======================================================
			// 2009.08.31 권오창
			// 저장품제원 : 후판제품 L2 로 송신(YDY4L002)
			//======================================================
			recResult = JDTORecordFactory.getInstance().create();
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(ydDaoUtils.paraRecChkNull(inRecord,"YD_GP"))) { //- 2013.01.16 수정 (3기)
				recResult.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고
			} else {
				recResult.setField("MSG_ID"         , "YDY4L002"); //1후판 제품창고
			}				
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			
			// 2021. 05. 17 전문생성 시점에 PT_PLATE_COMM에 진도코드가 바뀌지 않은 문제가 있어
			// 진도코드를 파라메터로 넘겨 처리함
			recResult.setField("CURR_PROG_CD"  , ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			
			ydDelegate.sendMsg(recResult);
			
			// 전사물류개선 2021. 4. 3
			if(PlateGdsYdUtil.isSendToEaiY9_stlNo(ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"))){
				recResult.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
				ydDelegate.sendMsg(recResult);
			}
			
		}catch(Exception e){
			
			szMsg = "[후판제품목전]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		
		szMsg = "[후판제품목전]수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of procPlageGdsOrdtrn()
	
	
	/**
	 *Plate공통 항목을 야드 저장품 항목으로 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtPlateYdstock(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {

		try{

			recEditRec.setField("STL_NO",        				ydDaoUtils.paraRecChkNull(inRecord,"PLATE_NO"));
			recEditRec.setField("YD_MTL_T",        				ydDaoUtils.paraRecChkNull(inRecord,"PL_MEA_GDS_T"));
			recEditRec.setField("YD_MTL_W",        				ydDaoUtils.paraRecChkNull(inRecord,"PL_MEA_GDS_W"));
			recEditRec.setField("YD_MTL_L",        				ydDaoUtils.paraRecChkNull(inRecord,"PL_MEA_GDS_L"));
			recEditRec.setField("YD_MTL_WT",        			ydDaoUtils.paraRecChkNull(inRecord,"PL_MEA_GDS_WT"));
			recEditRec.setField("STL_APPEAR_GP",    			ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recEditRec.setField("PLNT_PROC_CD",     			ydDaoUtils.paraRecChkNull(inRecord,"PLNT_PROC_CD"));
			recEditRec.setField("STL_PROG_CD",      			ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			recEditRec.setField("ORD_YEOJAE_GP",    			ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));
			recEditRec.setField("ORD_NO",        				ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO"));
			recEditRec.setField("ORD_DTL",        				ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL"));
			recEditRec.setField("ITEMNAME_CD",      			ydDaoUtils.paraRecChkNull(inRecord,"ITEMNAME_CD"));
			recEditRec.setField("OVERALL_STAMP_GRADE",        	ydDaoUtils.paraRecChkNull(inRecord,"OVERALL_STAMP_GRADE"));

		
		} catch(Exception e){

			return -1;
		}
		return 1;

	} //end of edtPlateYdstock()
	
	/**
	 * 외판슬라브출하지시대기(DMYDR004) - K1
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procOutplSlabDistOrdWait(JDTORecord inRecord)throws JDTOException  {
		YdStockDao ydStockDao 					= new YdStockDao();
		
		YdCodeMapping ydCodeMapping = new YdCodeMapping();
		
		JDTORecord recSet 			= JDTORecordFactory.getInstance().create();
		JDTORecord outRecTemp      	= null;

		String szMethodName 		= "procOutplSlabDistOrdWait";
		String szMsg 				= "";
		String szOperationName      = "외판슬라브출하지시대기";
		String szSTL_NO 			= "";
		int intRtnVal				= 0;
		int nRet                    = 0;

		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			// 야드는 X로 일단 두었음 수정해야됨
			//=============================================================
			szMsg = "[출하] 외판슬라브출하지시대기 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

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
			YD_DLVRDD_RULE_DD	납기기준일
			DEST_TEL_NO			목적지전화번호
			DIST_SHIPASSIGN_GP	출하배선지시구분
			*/
			
			recSet.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recSet.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			recSet.setField("STL_PROG_CD",			ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			recSet.setField("ORD_YEOJAE_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));
			recSet.setField("ORD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO"));
			recSet.setField("ORD_DTL", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL"));
			recSet.setField("ORD_GP", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_GP"));
			recSet.setField("CUST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD"));
			recSet.setField("DEST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"DEST_CD"));
			recSet.setField("YD_DLVRDD_RULE_DD", 	ydDaoUtils.paraRecChkNull(inRecord,"YD_DLVRDD_RULE_DD"));
			recSet.setField("DEST_TEL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"DEST_TEL_NO"));
			recSet.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"));
			recSet.setField("MODIFIER", 			"DMYDR004");

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("S",inRecord );		
			recSet.setField("YD_AIM_RT_GP", rVal[0]);
			//****************************************************************************************************
				
            //=============================================================================================
			// 코드 매핑값 호출 
			//
			//     * 앞에서 코드매핑값에 대해 어떤처리를 하든지 다시 엎어침... 
			//       나중에 앞에서 아래코드에 대해 처리하는 부분 삭제해야 됨                         
            //=============================================================================================
			outRecTemp = JDTORecordFactory.getInstance().create();
			nRet = ydCodeMapping.MakeCodeMapping(szRcvTcCode, szSTL_NO, inRecord, outRecTemp);
			if(nRet <= 0){
				String szTempSTL_APPEAR_GP =  ydDaoUtils.paraRecChkNull(outRecTemp, "STL_APPEAR_GP");
				if(!szTempSTL_APPEAR_GP.trim().equals("")){
					recSet.setField("STL_APPEAR_GP", szTempSTL_APPEAR_GP);
				}

				String szTempSCARFING_YN =  ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_YN");
				if(!szTempSCARFING_YN.trim().equals("")){
					recSet.setField("SCARFING_YN", szTempSCARFING_YN);
				}

				String szSCARFING_DONE_YN =  ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_DONE_YN");
				if(!szSCARFING_DONE_YN.trim().equals("")){
					recSet.setField("SCARFING_DONE_YN", szSCARFING_DONE_YN);
				}
				
				szMsg = "[nRet " + nRet + "] 매핑되는 코드가 없습니다. 재료외형, 스카핑여부, 스카핑 완료여부는 업데이트를 위함 STL_APPEAR_GP(" + szTempSTL_APPEAR_GP + ") SCARFING_YN(" + szTempSCARFING_YN + ") SCARFING_DONE_YN(" + szSCARFING_DONE_YN + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else {
				String szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "STL_APPEAR_GP");
				if(!szSTL_APPEAR_GP.equals("")){
					recSet.setField("STL_APPEAR_GP"   , szSTL_APPEAR_GP);
				}
				
				String szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_RT_GP");
				if(!szYD_AIM_RT_GP.equals("")){
					recSet.setField("YD_AIM_RT_GP"    , szYD_AIM_RT_GP);
				}

				String szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_YD_GP");
				if(!szYD_AIM_YD_GP.equals("")){
					recSet.setField("YD_AIM_YD_GP"    , szYD_AIM_YD_GP);
				}

				String szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_BAY_GP");
				if(!szYD_AIM_BAY_GP.equals("")){
					recSet.setField("YD_AIM_BAY_GP"   , szYD_AIM_BAY_GP);
				}
				
				String szSCARFING_YN = ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_YN");
				if(!szSCARFING_YN.equals("")){
					recSet.setField("SCARFING_YN"   , szSCARFING_YN);
				}
				
				String szSCARFING_DONE_YN = ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_DONE_YN");
				if(!szSCARFING_DONE_YN.equals("")){
					recSet.setField("SCARFING_DONE_YN"   , szSCARFING_DONE_YN);
				}				
			}
            //=============================================================================================
			
			stock.setYdStkLocTpCd(recSet);
			
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recSet, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[외판슬라브출하지시대기] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName, "[2] YD_STOCK[외판슬라브출하지시대기] UPDATE Success",3);
			//****************************************************************************************************

			String s_YD_GP = ydDaoUtils.paraRecChkNull(inRecord,"YD_GP");
		
			if(s_YD_GP.equals("A")) //야드구분이 연주슬라브야드인 경우에만 
			{
			//======================================================
			// 2009.08.31 권오창
			// 저장품제원 : 연주슬라브L2 로 송신(YDY1L002)
			//======================================================
				JDTORecord recResult = null;
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YDY1L002");
				recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
				recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recSet, "STL_NO"));
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				ydDelegate.sendMsg(recResult);
			
			}
			
		}catch(Exception e){

			szMsg="[외판슬라브출하지시대기]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		
		} // end of try-catch

		szMsg="외판슬라브출하지시대기  등록("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procOutplSlabDistOrdWait()

	/**
	 * 외판슬라브운송지시대기(DMYDR016) - N1
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procOutplSlabTrnOrdWait(JDTORecord inRecord)throws JDTOException  {
		YdStockDao ydStockDao = new YdStockDao();
		
		YdCodeMapping ydCodeMapping = new YdCodeMapping();
		
		JDTORecord recStockColumn 	= JDTORecordFactory.getInstance().create();
		JDTORecord outRecTemp       = null;
		
		String szMethodName 			= "procOutplSlabTrnOrdWait";
		String szMsg 					= "";	
		String szOperationName          = "외판슬라브운송지시대기";
		String szSTL_NO					= "";
		int intRtnVal = 0;
		int nRet = 0;

		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 외판슬라브운송지시대기 수신";
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
			DLVRDD_RULE_DD		납기기준일
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
			recStockColumn.setField("YD_DLVRDD_RULE_DD", 	ydDaoUtils.paraRecChkNull(inRecord,"DLVRDD_RULE_DD"));
			recStockColumn.setField("DEST_TEL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"DEST_TEL_NO"));
			recStockColumn.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"));
			recStockColumn.setField("MODIFIER", 			"DMYDR016");

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("S",inRecord );		
			recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
			//****************************************************************************************************

            //=============================================================================================
			// 코드 매핑값 호출 
			//
			//     * 앞에서 코드매핑값에 대해 어떤처리를 하든지 다시 엎어침... 
			//       나중에 앞에서 아래코드에 대해 처리하는 부분 삭제해야 됨                         
            //=============================================================================================
			outRecTemp = JDTORecordFactory.getInstance().create();
			nRet = ydCodeMapping.MakeCodeMapping(szRcvTcCode, szSTL_NO, inRecord, outRecTemp);
			if(nRet <= 0){
				String szTempSTL_APPEAR_GP =  ydDaoUtils.paraRecChkNull(outRecTemp, "STL_APPEAR_GP");
				if(!szTempSTL_APPEAR_GP.trim().equals("")){
					recStockColumn.setField("STL_APPEAR_GP", szTempSTL_APPEAR_GP);
				}
				
				String szTempSCARFING_YN =  ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_YN");
				if(!szTempSCARFING_YN.trim().equals("")){
					recStockColumn.setField("SCARFING_YN", szTempSCARFING_YN);
				}

				String szSCARFING_DONE_YN =  ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_DONE_YN");
				if(!szSCARFING_DONE_YN.trim().equals("")){
					recStockColumn.setField("SCARFING_DONE_YN", szSCARFING_DONE_YN);
				}
				
				szMsg = "[nRet " + nRet + "] 매핑되는 코드가 없습니다. 재료외형, 스카핑여부, 스카핑 완료여부는 업데이트를 위함 STL_APPEAR_GP(" + szTempSTL_APPEAR_GP + ") SCARFING_YN(" + szTempSCARFING_YN + ") SCARFING_DONE_YN(" + szSCARFING_DONE_YN + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else {
				String szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "STL_APPEAR_GP");
				if(!szSTL_APPEAR_GP.equals("")){
					recStockColumn.setField("STL_APPEAR_GP"   , szSTL_APPEAR_GP);
				}
				
				String szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_RT_GP");
				if(!szYD_AIM_RT_GP.equals("")){
					recStockColumn.setField("YD_AIM_RT_GP"    , szYD_AIM_RT_GP);
				}

				String szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_YD_GP");
				if(!szYD_AIM_YD_GP.equals("")){
					recStockColumn.setField("YD_AIM_YD_GP"    , szYD_AIM_YD_GP);
				}

				String szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_BAY_GP");
				if(!szYD_AIM_BAY_GP.equals("")){
					recStockColumn.setField("YD_AIM_BAY_GP"   , szYD_AIM_BAY_GP);
				}
				
				String szSCARFING_YN = ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_YN");
				if(!szSCARFING_YN.equals("")){
					recStockColumn.setField("SCARFING_YN"   , szSCARFING_YN);
				}
				
				String szSCARFING_DONE_YN = ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_DONE_YN");
				if(!szSCARFING_DONE_YN.equals("")){
					recStockColumn.setField("SCARFING_DONE_YN"   , szSCARFING_DONE_YN);
				}		
			}
            //=============================================================================================
			
			stock.setYdStkLocTpCd(recStockColumn);
			
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[외판슬라브운송지시대기] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[외판슬라브운송지시대기] UPDATE Success",3);
			//****************************************************************************************************

			String s_YD_GP = ydDaoUtils.paraRecChkNull(inRecord,"YD_GP");
			
			if(s_YD_GP.equals("A")) //야드구분이 연주슬라브야드인 경우에만 
			{
			
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
			}
			
		}catch(Exception e){

			szMsg="[외판슬라브운송지시대기]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try-catch

		szMsg="외판슬라브운송지시대기  등록("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of procOutplSlabTrnOrdWait()
	
	/**
	 * 코일제품출하지시대기(DMYDR005) - K2
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilGdsDistOrdWait(JDTORecord inRecord)throws JDTOException  {
		// 저장품DAO
		YdStockDao ydStockDao 					= new YdStockDao();
		
		JDTORecord recSet 			= JDTORecordFactory.getInstance().create();

		String szMethodName 		= "procCoilGdsDistOrdWait";
		String szMsg 				= "";
		String szOperationName      = "코일제품출하지시대기";
		String szSTL_NO 			= "";
		int intRtnVal				= 0;

		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 코일제품출하지시대기 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

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
			DLVRDD_RULE_DD		납기기준일
			DEST_TEL_NO			목적지전화번호
			DIST_SHIPASSIGN_GP	출하배선지시구분 
			 */

			recSet.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recSet.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			recSet.setField("STL_PROG_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			recSet.setField("ORD_YEOJAE_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));
			recSet.setField("ORD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO"));
			recSet.setField("ORD_DTL", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL"));
			recSet.setField("ORD_GP", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_GP"));
			recSet.setField("CUST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD"));
			recSet.setField("DEST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"DEST_CD"));
			recSet.setField("YD_DLVRDD_RULE_DD", 	ydDaoUtils.paraRecChkNull(inRecord,"DLVRDD_RULE_DD"));
			recSet.setField("DEST_TEL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"DEST_TEL_NO"));
			recSet.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"));
			recSet.setField("MODIFIER", 			"DMYDR005");
			
			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("C",inRecord );		
			recSet.setField("YD_AIM_RT_GP", rVal[0]);
			recSet.setField("STL_PROG_CD", rVal[1]);
			//****************************************************************************************************

			
			//저장품갱신********************************************************************************************	
			intRtnVal = ydStockDao.updYdStock(recSet, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[코일제품출하지시대기] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName, "[2] YD_STOCK[코일제품출하지시대기] UPDATE Success",3);
			//****************************************************************************************************

			//======================================================
			// 2009.08.31 권오창
			// 저장품제원 : 코일야드L2 로 송신(YDY5L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY5L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recSet, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			ydDelegate.sendMsg(recResult);
			
		}catch(Exception e){

			szMsg="[코일제품출하지시대기]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);	
		} // end of try-catch

		szMsg="코일제품출하지시대기  등록("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of procCoilGdsDistOrdWait()

	/**
	 * 코일제품운송지시(DMYDR020) - L2
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilGdsTrnOrd(JDTORecord inRecord)throws JDTOException  {
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		YdCarSchDao ydCarSchDao	= new YdCarSchDao();
		JDTORecord recInTemp		= JDTORecordFactory.getInstance().create();
		JDTORecord recEditColumn	= JDTORecordFactory.getInstance().create();
		JDTORecord	recPara			= JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult1 	= null;
		String szMethodName 		= "procCoilGdsTrnOrd";
		String szMsg 				= "";
		String szOperationName      = "코일제품운송지시";
		String szSTL_NO 			= "";
		String szYD_CAR_SCH_ID 		= "";
		String szWLOC_CD			= "";
		String szYD_STK_COL_GP		= "";	
		
		String szYD_GP			 	= "";
		String szCARLD_PNT_CD 		= "";
		String szHANDLING_CNT 		= "";
		String szYD_PNT_CD 			= "";
		
		int intRtnVal 				= 0;
		int i =0;
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 코일제품운송지시 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP	재료외형구분
			TRANS_ORD_DT	운송지시일자
			TRANS_ORD_SEQNO	운송지시순번
			YD_EQP_WRK_SH	야드설비작업매수
			STL_NO1	재료번호1
			GDS_CARLD_LOC1	제품상차위치1
			STL_NO2	재료번호2
			GDS_CARLD_LOC2	제품상차위치2 
			STL_NO3	재료번호3
			GDS_CARLD_LOC3	제품상차위치3
			STL_NO4	재료번호4
			GDS_CARLD_LOC4	제품상차위치4
			STL_NO5	재료번호5
			GDS_CARLD_LOC5	제품상차위치5
			STL_NO6	재료번호6
			GDS_CARLD_LOC6	제품상차위치6
			STL_NO7	재료번호7
			GDS_CARLD_LOC7	제품상차위치7
			STL_NO8	재료번호8
			GDS_CARLD_LOC8	제품상차위치8
			STL_NO9	재료번호9
			GDS_CARLD_LOC9	제품상차위치9
			STL_NO10	재료번호10
			GDS_CARLD_LOC10	제품상차위치10
			STL_NO11	재료번호11
			GDS_CARLD_LOC11	제품상차위치11
			STL_NO12	재료번호12
			GDS_CARLD_LOC12	제품상차위치12
			STL_NO13	재료번호13
			GDS_CARLD_LOC13	제품상차위치13
			STL_NO14	재료번호14
			GDS_CARLD_LOC14	제품상차위치14
			STL_NO15	재료번호15
			GDS_CARLD_LOC15	제품상차위치15
			STL_NO16	재료번호16
			GDS_CARLD_LOC16	제품상차위치16
			STL_NO17	재료번호17
			GDS_CARLD_LOC17	제품상차위치17
			STL_NO18	재료번호18
			GDS_CARLD_LOC18	제품상차위치18
			STL_NO19	재료번호19
			GDS_CARLD_LOC19	제품상차위치19
			STL_NO20	재료번호20
			GDS_CARLD_LOC20	제품상차위치20
			DIST_SHIPASSIGN_GP1	출하배선지시구분1
			SHIPASSIGN_WORD_DATE1	배선작업지시일자1
			SHIPASSIGN_WORD_SEQNO1	배선작업지시순번1
			DIST_SHIPASSIGN_GP2	출하배선지시구분2
			SHIPASSIGN_WORD_DATE2	배선작업지시일자2
			SHIPASSIGN_WORD_SEQNO2	배선작업지시순번2
			DIST_SHIPASSIGN_GP3	출하배선지시구분3
			SHIPASSIGN_WORD_DATE3	배선작업지시일자3
			SHIPASSIGN_WORD_SEQNO3	배선작업지시순번3
			DIST_SHIPASSIGN_GP4	출하배선지시구분4
			SHIPASSIGN_WORD_DATE4	배선작업지시일자4
			SHIPASSIGN_WORD_SEQNO4	배선작업지시순번4
			DIST_SHIPASSIGN_GP5	출하배선지시구분5
			SHIPASSIGN_WORD_DATE5	배선작업지시일자5
			SHIPASSIGN_WORD_SEQNO5	배선작업지시순번5
			DIST_SHIPASSIGN_GP6	출하배선지시구분6
			SHIPASSIGN_WORD_DATE6	배선작업지시일자6
			SHIPASSIGN_WORD_SEQNO6	배선작업지시순번6
			DIST_SHIPASSIGN_GP7	출하배선지시구분7
			SHIPASSIGN_WORD_DATE7	배선작업지시일자7
			SHIPASSIGN_WORD_SEQNO7	배선작업지시순번7
			DIST_SHIPASSIGN_GP8	출하배선지시구분8
			SHIPASSIGN_WORD_DATE8	배선작업지시일자8
			SHIPASSIGN_WORD_SEQNO8	배선작업지시순번8
			DIST_SHIPASSIGN_GP9	출하배선지시구분9
			SHIPASSIGN_WORD_DATE9	배선작업지시일자9
			SHIPASSIGN_WORD_SEQNO9	배선작업지시순번9
			DIST_SHIPASSIGN_GP10	출하배선지시구분10
			SHIPASSIGN_WORD_DATE10	배선작업지시일자10
			SHIPASSIGN_WORD_SEQNO10	배선작업지시순번10
			DIST_SHIPASSIGN_GP11	출하배선지시구분11
			SHIPASSIGN_WORD_DATE11	배선작업지시일자11
			SHIPASSIGN_WORD_SEQNO11	배선작업지시순번11
			DIST_SHIPASSIGN_GP12	출하배선지시구분12
			SHIPASSIGN_WORD_DATE12	배선작업지시일자12
			SHIPASSIGN_WORD_SEQNO12	배선작업지시순번12
			DIST_SHIPASSIGN_GP13	출하배선지시구분13
			SHIPASSIGN_WORD_DATE13	배선작업지시일자13
			SHIPASSIGN_WORD_SEQNO13	배선작업지시순번13
			DIST_SHIPASSIGN_GP14	출하배선지시구분14
			SHIPASSIGN_WORD_DATE14	배선작업지시일자14
			SHIPASSIGN_WORD_SEQNO14	배선작업지시순번14
			DIST_SHIPASSIGN_GP15	출하배선지시구분15
			SHIPASSIGN_WORD_DATE15	배선작업지시일자15
			SHIPASSIGN_WORD_SEQNO15	배선작업지시순번15
			DIST_SHIPASSIGN_GP16	출하배선지시구분16
			SHIPASSIGN_WORD_DATE16	배선작업지시일자16
			SHIPASSIGN_WORD_SEQNO16	배선작업지시순번16
			DIST_SHIPASSIGN_GP17	출하배선지시구분17
			SHIPASSIGN_WORD_DATE17	배선작업지시일자17
			SHIPASSIGN_WORD_SEQNO17	배선작업지시순번17
			DIST_SHIPASSIGN_GP18	출하배선지시구분18
			SHIPASSIGN_WORD_DATE18	배선작업지시일자18
			SHIPASSIGN_WORD_SEQNO18	배선작업지시순번18
			DIST_SHIPASSIGN_GP19	출하배선지시구분19
			SHIPASSIGN_WORD_DATE19	배선작업지시일자19
			SHIPASSIGN_WORD_SEQNO19	배선작업지시순번19
			DIST_SHIPASSIGN_GP20	출하배선지시구분20
			SHIPASSIGN_WORD_DATE20	배선작업지시일자20
			SHIPASSIGN_WORD_SEQNO20	배선작업지시순번20
			SHIP_CD	선박코드
			SHIP_NAME	선박명
			RSHP_HOLD_NO	선박Hold번호
			SAILNO	선박항차

			*/
			String sCAR_KIND =ydDaoUtils.paraRecChkNull(inRecord,"CAR_KIND"); //장비구분: Trailer-T , TT Trailer -TT
			
			recEditColumn 	= JDTORecordFactory.getInstance().create();
			recEditColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recEditColumn.setField("TRANS_ORD_DATE", 		ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
			recEditColumn.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
			//>>>>>>>>>>>>선박정보<<<<<<<<<<<<<<<<<<<
			recEditColumn.setField("SHIP_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"SHIP_CD"));
			recEditColumn.setField("SHIP_NAME", 			ydDaoUtils.paraRecChkNull(inRecord,"SHIP_NAME"));
			recEditColumn.setField("RSHP_HOLD_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"RSHP_HOLD_NO"));
			recEditColumn.setField("SAILNO", 				ydDaoUtils.paraRecChkNull(inRecord,"SAILNO"));
			//>>>>>>>>>>>>차량정보(하이스코 2냉연<<<<<<<<<<<<<<<<<<<
			recEditColumn.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO"));
			recEditColumn.setField("CAR_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO"));
			recEditColumn.setField("REHEAT_SLAB_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"WORK_GP"));
			recEditColumn.setField("COIL_CAR_NO",			ydDaoUtils.paraRecChkNull(inRecord,"CARLD_PNT_CD"));
			
			if(sCAR_KIND.length()>2){ //장비구분: Trailer-T , TT Trailer -TT
				recEditColumn.setField("YD_STK_BED_NO", 		sCAR_KIND.substring(0, 2));
			}else {
				recEditColumn.setField("YD_STK_BED_NO", 		sCAR_KIND);
			}
			recEditColumn.setField("MODIFIER", 				"DMYDR020");
 
			
			
			//****************************************************************************************************
			
			//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
			for(i = 1 ; i<=20; i++){
				recEditColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i));
				inRecord.setField("STL_NO", 					ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i));
				recEditColumn.setField("YD_CAR_UPP_LOC_CD", 	ydDaoUtils.paraRecChkNull(inRecord,"GDS_CARLD_LOC"+i)); //야드 차상위치코드
				//>>>>>>>>>>>>선박정보<<<<<<<<<<<<<<<<<<
				recEditColumn.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"+i));
				recEditColumn.setField("SHIPASSIGN_WORD_DATE", 	ydDaoUtils.paraRecChkNull(inRecord,"SHIPASSIGN_WORD_DATE"+i));
				recEditColumn.setField("SHIPASSIGN_WORD_SEQNO", ydDaoUtils.paraRecChkNull(inRecord,"SHIPASSIGN_WORD_SEQNO"+i));
				
				//STL_NO가 없다면 loop종료
				szSTL_NO = recEditColumn.getFieldString("STL_NO");
				if(szSTL_NO.equals("")){
					break;
				}				
				
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal= YdCommonUtils.getYdAimRtGp("C",inRecord );		
				recEditColumn.setField("YD_AIM_RT_GP", rVal[0]);
				recEditColumn.setField("STL_PROG_CD", rVal[1]);
				recEditColumn.setField("DEL_YN", "N");
					
					
				//저장품갱신******************************************************************************************** 
				intRtnVal = ydStockDao.updYdStock(recEditColumn, 0);
				if(intRtnVal <= 0){
					szMsg= "YD_STOCK[코일제품운송지시] UPDATE Error :: [" + intRtnVal + "]" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[코일제품운송지시] UPDATE Success",3);
				//****************************************************************************************************


			} //end of for *******************************************************************************************
			
			
			
			String sWORK_GP =ydDaoUtils.paraRecChkNull(inRecord,"WORK_GP");
			
			
			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if(sWORK_GP.equals("9")){
			//차량정보 존재여부 체크 /////////////////////////////////////////////////////////////////
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DT"		, ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
			recPara.setField("TRANS_ORD_SEQNO"	, ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
			recPara.setField("CARD_NO"			, ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO") );

			//중복 check
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdDEL_YN*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 304);
			if(intRtnVal > 0){
				szMsg= "["+szMethodName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				
				rsResult1.first();
				recInTemp = rsResult1.getRecord();
				szYD_CAR_SCH_ID    = StringHelper.evl(recInTemp.getFieldString("YD_CAR_SCH_ID"), "");
			 
				recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				recInTemp.setField("DEL_YN", "Y");
				recInTemp.setField("MODIFIER", 	"DMYDR020");
				
				intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
				if( intRtnVal <= 0 ){
					szMsg="[" + szMethodName + "] 차량스케줄 삭제 시 오류발생[반환값 : " + intRtnVal + "]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);					 
	    		}
			}
			////////////////////////////////////////////////////////////////////////////////////////
			
			
			//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////
			
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CARPNT_CD"		, ydDaoUtils.paraRecChkNull(inRecord,"CARLD_PNT_CD"));
			
			//포인트코드 -> 개소코드와 저장위치 가져오기
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 412);
			if(intRtnVal > 0){
				szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 합니다..]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				rsResult1.first();
				recInTemp = rsResult1.getRecord();
				szWLOC_CD    		= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
				szYD_STK_COL_GP    	= StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP"), "");		
				szYD_PNT_CD	    	= StringHelper.evl(recInTemp.getFieldString("YD_PNT_CD"), "");		
				
				/*
				 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
				 */
				szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
				recInTemp.setField("REGISTER",         szRcvTcCode);
				recInTemp.setField("YD_EQP_WRK_STAT",  "U");									//야드설비작업상태
				recInTemp.setField("YD_EQP_ID",        YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
				recInTemp.setField("SPOS_WLOC_CD",     szWLOC_CD);								//발지개소코드
				recInTemp.setField("CAR_NO",           ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO"));								//차량번호
				recInTemp.setField("CARD_NO",          ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO"));								//카드번호
				recInTemp.setField("YD_CARLD_LEV_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));						//운송지시순번
		    	recInTemp.setField("YD_CARLD_STOP_LOC",szYD_STK_COL_GP);						//차량상차정지위치
		    	recInTemp.setField("YD_BAYIN_WO_SEQ",  YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);		//입동지시순번 - 기본값으로 설정(9)
				recInTemp.setField("YD_CAR_PROG_STAT", "1");									//상차출발상태
				recInTemp.setField("YD_CAR_WRK_GP", 	sWORK_GP);
				recInTemp.setField("YD_PNT_CD1", 	szYD_PNT_CD);
				
				if(sCAR_KIND.equals("TT")){
					recInTemp.setField("CAR_KIND",          "TT");									//차량종류
				}else{
					recInTemp.setField("CAR_KIND",          "TR");									//차량종류
				}
	    		
	    		//차량스케줄 등록
		    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
	    		if( intRtnVal <= 0 ){
					szMsg="[" + szMethodName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	 
	    		}
	    		
	    		if(sCAR_KIND.equals("TT")){
		    		//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			        EJBConnector ejbConn3 = new EJBConnector("default","JNDITsInfoReg",this);
					ejbConn3.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
				  	             new Object[]{"C","","",szYD_STK_COL_GP,"","","R"});
	    		}
			}else {
				szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 안합니다..]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			}
    		//////////////////////////////////////////////////////////////////////////////////////////
			
			
			//입동지시요구모듈 호출(trailer인 경우)///////////////////////////////////////////////////////
			if(sCAR_KIND.equals("T") || sCAR_KIND.equals("TR")){
				/*
				 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
				 */
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				recInTemp = JDTORecordFactory.getInstance().create();			 
				recInTemp.setField("YD_CARPNT_CD"			,ydDaoUtils.paraRecChkNull(inRecord,"CARLD_PNT_CD"));
				recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
				
				EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
				ejbConn.trx("procCarBayInOrdReqTr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				

				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
			}
			//////////////////////////////////////////////////////////////////////////////////////////
			
			
			
			//야드핸들링정보 송신////////////////////////////////////////////////////////////////////////////////
			//육송출하고도화
			ymCommonDAO dao = ymCommonDAO.getInstance();
			 List chkList = null;
			String QueryId 	= "com.inisteel.cim.yd.dao.chklist";
			chkList = dao.getCommonList(QueryId, new Object[]{});

		    JDTORecord unloadPointrec = (JDTORecord)chkList.get(0);
	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");	    	
	    	ydUtils.putLog(szSessionName, szMethodName, "◑◑◑◑◑ TC_CODE:DMYDR020 , CHK:"+CHK, YdConstant.INFO);
	    	
	    	if(CHK.equals("Y")){    		 
	    		if(sCAR_KIND.equals("T") || sCAR_KIND.equals("TR")){
		    		rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
					recPara 	= JDTORecordFactory.getInstance().create();
					recPara.setField("TRANS_ORD_DATE"		,ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
					recPara.setField("TRANS_ORD_SEQNO"		, ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
		 
					//출하제품핸들링횟수
					/*com.inisteel.cim.yd.ydStock.RouteModReg.procCoilGdsTrnOrdNEW.getHandlingCnt*/
					intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 418);	
					if( intRtnVal > 0 ){
						szMsg="[" + szMethodName + "] 출하제품핸들링횟수[반환값 : " + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	 
		    		 
						for( i = 1; i <= rsResult1.size(); i++ ) {
							recInTemp = JDTORecordFactory.getInstance().create();
							rsResult1.absolute(i);					
							recInTemp 		= rsResult1.getRecord();
							
							szYD_GP			 	= StringHelper.evl(recInTemp.getFieldString("YD_GP"), "");
							szCARLD_PNT_CD 		= StringHelper.evl(recInTemp.getFieldString("CARLD_PNT_CD"), "");
							szHANDLING_CNT 		= StringHelper.evl(recInTemp.getFieldString("HANDLING_CNT"), "");
							
							
							szMsg="상차완료 송신 YDDMR050 (야드핸들링정보) 송신 시작";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							recInTemp = JDTORecordFactory.getInstance().create();
							recInTemp.setField("MSG_ID",        			"YDDMR050");
							recInTemp.setField("YD_GP"           	 		,szYD_GP );
							recInTemp.setField("TRANS_ORD_DT"           	,ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
							recInTemp.setField("TRANS_ORD_SEQNO"         	,ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
							recInTemp.setField("CMBN_CARLD_YN"         		,"" );
							recInTemp.setField("CARLD_PNT_CD"         		,szCARLD_PNT_CD );
							recInTemp.setField("CAR_NO"           			,ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO") );
							recInTemp.setField("HANDLING_CNT"          		,szHANDLING_CNT ); 
							recInTemp.setField("YD_STK_BED_WHIO_STAT"       ,"" );
		
							ydDelegate.sendMsg(recInTemp);
							
							szMsg="상차완료 송신 YDDMR050 (야드핸들링정보) 송신 완료";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
					}
				}
	    	}
			/////////////////////////////////////////////////////////////////////////////////////////////////
    		
			
			}
			
		}catch(Exception e){

			szMsg="[코일제품운송지시] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch

		szMsg="코일제품운송지시수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


	} // end of procCoilGdsTrnOrd()
	
	/**
	 * 후판제품출하지시대기 - K3 (DMYDR006)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlateGdsDistOrdWait(JDTORecord inRecord)throws JDTOException  {
		// 저장품DAO
		YdStockDao ydStockDao 			= new YdStockDao();
		PtPlateCommDao ptPlateCommDao   = new PtPlateCommDao();
		
		JDTORecord recSet 			=  JDTORecordFactory.getInstance().create();

		String szMethodName 		= "procPlateGdsDistOrdWait";
		String szMsg 				= "";
		String szOperationName      = "후판제품출하지시대기";
		String szSTL_NO 			= "";
		String ydAimRtGp 			= "";
		String currProgCd 			= "";
		String szYD_BAY_GP 			= "";
		int intRtnVal				= 0;

		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 후판제품출하지시대기 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP	재료외형구분
			STL_NO	재료번호
			CURR_PROG_CD	현재진도코드
			ORD_YEOJAE_GP	주문여재구분
			ORD_NO	주문번호
			ORD_DTL	주문행번
			ORD_GP	수주구분
			CUST_CD	고객코드
			DEST_CD	목적지코드
			DLVRDD_RULE_DD	납기기준일
			DEST_TEL_NO	목적지전화번호
			DIST_SHIPASSIGN_GP	출하배선지시구분
			*/

			recSet.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recSet.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			recSet.setField("STL_PROG_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			recSet.setField("ORD_YEOJAE_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));
			recSet.setField("ORD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO"));
			recSet.setField("ORD_DTL", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL"));
			recSet.setField("ORD_GP", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_GP"));
			recSet.setField("CUST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD"));
			recSet.setField("DEST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"DEST_CD"));
			recSet.setField("YD_DLVRDD_RULE_DD", 	ydDaoUtils.paraRecChkNull(inRecord,"DLVRDD_RULE_DD"));
			recSet.setField("DEST_TEL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"DEST_TEL_NO"));
			recSet.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"));
			recSet.setField("MODIFIER", 			"DMYDR006");
			
			//-------------------------------------------------------------------------
			//	2010.12.22 윤재광 - 여재다운/충당/목전시 해당 파일링코드 셋팅 시작 
			//-------------------------------------------------------------------------
			
			String sPlateNo	= ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");
			
			JDTORecord recIn 			= null;
			JDTORecord recTemp 			= null;
			JDTORecord recEdit 			= null;
			
			JDTORecordSet rsOutRecSet 	= null;
			JDTORecordSet rsOut 		= null;
			
			recIn 		= JDTORecordFactory.getInstance().create();
			rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recIn.setField("PLATE_NO", sPlateNo);
			intRtnVal = ydStockDao.getYdStock(recIn, rsOutRecSet, 4);
			
			rsOutRecSet.first();
			recIn = rsOutRecSet.getRecord();
			
			recTemp		= JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_MTL_L", ydDaoUtils.paraRecChkNull(recIn,"PL_MEA_GDS_L"));
			recTemp.setField("YD_MTL_W", ydDaoUtils.paraRecChkNull(recIn,"PL_MEA_GDS_W"));
			szYD_BAY_GP  =  ydDaoUtils.paraRecChkNull(recIn,"YD_BAY_GP");
			
			String sYdPilingCd = "";
			
			if("K".equals(ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"))||
			   "3".equals(ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"))){
				
				//-------------------------------------------------------------------------
				//	주문재인 경우 OS공통테이블의 정보를 조회해서  Piling Code
				//-------------------------------------------------------------------------
				recEdit = JDTORecordFactory.getInstance().create();
				recEdit.setField("ORD_NO",  ydDaoUtils.paraRecChkNull(recIn,"ORD_NO"));
				recEdit.setField("ORD_DTL", ydDaoUtils.paraRecChkNull(recIn,"ORD_DTL"));
				
				rsOut = JDTORecordFactory.getInstance().createRecordSet("");
				//OS공통조회
				intRtnVal = ydStockDao.getYdStock(recEdit, rsOut, 88);
				
				recEdit = JDTORecordFactory.getInstance().create();
				rsOut.first();
				recEdit.setRecord(rsOut.getRecord());
				
				sYdPilingCd = ydDaoUtils.paraRecChkNull(recEdit,"YD_PILING_CD");
				
			}else if("Z".equals(ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"))){
				
				recTemp.setField("STRCHAR_CUST_CD" 			, "");	
				recTemp.setField("STRCHAR_ORD_YEOJAE_GP" 	, "2");
				recTemp.setField("YD_STRCHAR_GRP_CD" 		, "M001");

				PlateGdsYdUtil.getWTLGp(recTemp);
				
				sYdPilingCd	  	= "M001" + 
								  ydDaoUtils.paraRecChkNull(recTemp,"YD_MTL_W_GP")+ 
								  ydDaoUtils.paraRecChkNull(recTemp,"YD_MTL_L_GP");
			}
			
			recSet.setField("PLATE_NO", 		sPlateNo);
			recSet.setField("YD_PILING_CD", 	sYdPilingCd);
			recSet.setField("YD_BOOK_OUT_LOC", 	"77777");
			
			intRtnVal = ptPlateCommDao.updPtPlateComm(recSet, 0);
			
			//-------------------------------------------------------------------------
			//	2010.12.22 윤재광 - 여재다운/충당/목전시 해당 파일링코드 셋팅 완료 
			//-------------------------------------------------------------------------
			/*
			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("P",inRecord );
			
			recSet.setField("YD_AIM_RT_GP", rVal[0]);
			recSet.setField("STL_PROG_CD", rVal[1]);
			*/
			currProgCd = ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD");
			
			if(currProgCd.equals("Y")){
				ydAimRtGp =currProgCd+"C";	//재공충당대기(A후판plate)
			}else if(currProgCd.equals("G")){
				ydAimRtGp =currProgCd+"3";	//종합판정대기
			}else if(currProgCd.equals("I")){
				ydAimRtGp =currProgCd+"3";	//반송대기
			}else if(currProgCd.equals("H")){
				ydAimRtGp =currProgCd+"3";	//입고대기
			}else if(currProgCd.equals("J")){
				ydAimRtGp =currProgCd+"3";	//반납대기
			}else if(currProgCd.equals("Z")){
				ydAimRtGp =currProgCd+"3";	//제품충당대기
			}else if(currProgCd.equals("X")){
				ydAimRtGp =currProgCd+"3";	//경매대상선정
			}else if(currProgCd.equals("K")){		
				ydAimRtGp =currProgCd+"3";	//출하지시대기		
			}else if(currProgCd.equals("N")){
				ydAimRtGp =currProgCd+"3";	//운송지시대기
			}else if(currProgCd.equals("M")){
				ydAimRtGp =currProgCd+"3";	//출하완료				
			}
			
			recSet.setField("YD_AIM_RT_GP", ydAimRtGp);
			recSet.setField("STL_PROG_CD",  currProgCd);			
			
			recSet.setField("PRE_AR_STAT_CD", ""); //보관매출발생상태코드 초기화 (2013.02.15)
			
			//저장품갱신********************************************************************************************	
			intRtnVal = ydStockDao.updYdStock(recSet, 0);
			
			ydUtils.putLog(szSessionName, szMethodName, "[후판제품출하지시대기] 전문수신처리 성공",3);
			//****************************************************************************************************
			
			//======================================================================
			// 입고대기에서 출하지시대기로 넘어온 것 중 정보반납이력이 있는 것들에 한해서
			// 반납, 입고  작업실적 이력을 생성해주는 루틴
			// 2010.01.05 석창화, 이현성
			//======================================================================
			/*
			JDTORecord recPara 		= null;
			JDTORecordSet rsOutSet 	= null;
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("PLATE_NO" , ydDaoUtils.paraRecChkNull(recSet, "STL_NO"));
			
			intRtnVal = ptPlateCommDao.getPtPlateCommDao(recPara, rsOutSet, 0);
			
			if (intRtnVal >= 1) {
				// 적업이력 생성루틴 호출
				this.procPlateGdsReRcpt(ydDaoUtils.paraRecChkNull(recSet, "STL_NO"));
			}
			*/
			//======================================================
			// 2009.08.31 권오창
			// 저장품제원 : 후판제품 L2 로 송신(YDY4L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(ydDaoUtils.paraRecChkNull(inRecord,"YD_GP"))) { //- 2013.01.07 수정 (3기)
				recResult.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고
			} else {
				recResult.setField("MSG_ID"         , "YDY4L002"); //1후판 제품창고
			}
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recSet, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			
			// 2021. 05. 17 전문생성 시점에 PT_PLATE_COMM에 진도코드가 바뀌지 않은 문제가 있어
			// 진도코드를 파라메터로 넘겨 처리함
			recResult.setField("CURR_PROG_CD"  , ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			
			ydDelegate.sendMsg(recResult);		
			
			// 전사물류개선 2021. 4. 3
			if(PlateGdsYdUtil.isSendToEaiY9_stlNo(ydDaoUtils.paraRecChkNull(recSet, "STL_NO")) ){
				recResult.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
				ydDelegate.sendMsg(recResult);
			}
			
		}catch(Exception e){

			szMsg="[후판제품출하지시대기]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try-catch

		szMsg="후판제품출하지시대기  등록("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	} // end of procPlateGdsDistOrdWait()
	
	/**
	 * 후판제품창고 재입고 처리시 작업 이력 생성 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlateGdsReRcpt(String  pStlNo)throws JDTOException  {
		
		/*
		 * 1. 후판제품 재입고 처리시 출고, 입고 이력을 생성한다. 
		 * 2. 저장품 데이터를 기준으로 생성한다.
		 * 
		 * **********스케줄 및 권상 권하 위치 관련 , 설비 정보는 추가 작성해주어야한다. (미완성)
		 */
		
		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		
		// 작업이력 DAO
		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();
		
		String szMethodName = "procPlateGdsReRcpt";
		String szOperationName	= "후판제품창고 재입고 처리시 작업 이력 생성";
		String szMsg = null;
		
		// JDTORecord
		JDTORecord recStockInfo = JDTORecordFactory.getInstance().create();
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		// JDTORecordSet
		JDTORecordSet rsStockInfo = JDTORecordFactory.getInstance().createRecordSet("rsStockInfo");
		
		int intRtnVal = 0;
		
		szMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작  입력받은 재료번호 :   " +  pStlNo;
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		if(pStlNo.equals("")  || pStlNo.trim().equals("")){
			
			szMsg = "[Jsp Session : "+szOperationName+"] 재료번호가 올바르지 않습니다. - 이력생성이 불가능합니다. ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);			
			return ;
		}
		
		
		//1. 해당 재료의 저장품 데이터를 가지고 온다.
		recPara.setField("STL_NO", pStlNo);
		
		
		intRtnVal = ydStockDao.getYdStock(recPara, rsStockInfo, 0);
		
		
		if (intRtnVal < 0){
			szMsg = "[Jsp Session : "+szOperationName+"] 저장품 조회시 ERROR ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
			return;
			
		} else if( intRtnVal == 0 ){
			szMsg = "[Jsp Session : "+szOperationName+"] 조회된 데이터 정보가 없어 이력을 생성할 수 없습니다. ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
			return;
		}
		
		rsStockInfo.first();
		recStockInfo = rsStockInfo.getRecord();
		
		
		// 2. 저장품 정보에 출고 필요정보를 추가한다.
		
		recStockInfo.setField("YD_GP",YdConstant.YD_GP_PLATE_GDS_YARD); // 야드 구분 - 후판제품창고
		recStockInfo.setField("YD_AID_WRK_YN" , "N");  // 보조작업 여부 'N'		
		recStockInfo.setField("YD_SCH_ST_GP" , "B");  // 야드스케줄 기동 구분 "B"
		
		recStockInfo.setField("YD_GNT_GP" , YdConstant.YD_GNT_GP_ISSUE);  // 이력정보구분  - 출고 
		
		
		recStockInfo.setField("YD_UP_CMPL_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
		recStockInfo.setField("YD_DN_CMPL_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
		
		
		recStockInfo.setField("YD_SCH_CD" , "");  // 스케줄 코드
		recStockInfo.setField("YD_EQP_ID" , "");  // 설비(크레인)		
		recStockInfo.setField("YD_UP_WR_LOC" , "");
		recStockInfo.setField("YD_UP_WR_LAYER" , "");
		recStockInfo.setField("YD_DN_WR_LOC" , "");
		recStockInfo.setField("YD_DN_WR_LAYER" , "");
			
		
		// 3. 출고 정보를 이력에 남긴다.
	
		intRtnVal = ydWrkHistDao.insYdWrkHistPosFix(recStockInfo);
		
		if(intRtnVal > 0){
			szMsg = "[Jsp Session : " + szOperationName+"]  출고 이력 정보를 로깅하였습니다." ; 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
		}else{
			szMsg = "[Jsp Session : " + szOperationName+"] 출고 이력 정보를 로깅 실패 하였습니다" ; 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
			
		}
		
		
		// 4. 저장품 정보에 입고 정보를 추가한다.
		
		recStockInfo.setField("YD_GNT_GP" , YdConstant.YD_GNT_GP_RCPT);  // 이력정보구분 - 입고 
		
		recStockInfo.setField("YD_UP_CMPL_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
		recStockInfo.setField("YD_DN_CMPL_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
		
		recStockInfo.setField("YD_SCH_CD" , "");  // 스케줄 코드
		recStockInfo.setField("YD_EQP_ID" , "");  // 설비(크레인)
		recStockInfo.setField("YD_UP_WR_LOC" , "");
		recStockInfo.setField("YD_UP_WR_LAYER" , "");
		recStockInfo.setField("YD_DN_WR_LOC" , "");
		recStockInfo.setField("YD_DN_WR_LAYER" , "");
	
		// 5. 입고 정보를 이력에 남긴다.
		intRtnVal = ydWrkHistDao.insYdWrkHistPosFix(recStockInfo);
		
		if(intRtnVal > 0){
			szMsg = "[Jsp Session : " + szOperationName+"]  입고 이력 정보를 로깅하였습니다." ; 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
		}else{
			szMsg = "[Jsp Session : " + szOperationName+"] 입고 이력 정보를 로깅 실패 하였습니다" ; 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
			
		}
		
	}
	
	/**
	 * 후판제품운송지시대기(DMYDR018) - N3/ NA/ NB
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlateGdsTrnOrdWait(JDTORecord inRecord)throws JDTOException  {
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		
		JDTORecord recStockColumn 	= JDTORecordFactory.getInstance().create();
		
		String szMethodName 			= "procPlateGdsTrnOrdWait";
		String szMsg 					= "";
		String szOperationName          = "후판제품운송지시대기";
		String szSTL_NO					= "";
		int intRtnVal = 0;
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 후판제품운송지시대기 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
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
			DLVRDD_RULE_DD		납기기준일
			DEST_TEL_NO			목적지전화번호
			DIST_SHIPASSIGN_GP	출하배선지시구분
			SHIPASSIGN_WORD_DATE 	배선작업지시일자 
			SHIPASSIGN_WORD_SEQNO 	배선작업지시순번 
			SHIP_CD	선박코드
			SHIP_NAME	선박명
			RSHP_HOLD_NO	선박Hold번호
			SAILNO	선박항차
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
			recStockColumn.setField("YD_DLVRDD_RULE_DD", 	ydDaoUtils.paraRecChkNull(inRecord,"DLVRDD_RULE_DD"));
			recStockColumn.setField("DEST_TEL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"DEST_TEL_NO"));
			
			//>>>>>>>>>>>>선박정보<<<<<<<<<<<<<<<<<<
			recStockColumn.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"));
			recStockColumn.setField("SHIPASSIGN_WORD_DATE", ydDaoUtils.paraRecChkNull(inRecord,"SHIPASSIGN_WORD_DATE"));
			recStockColumn.setField("SHIPASSIGN_WORD_SEQNO",ydDaoUtils.paraRecChkNull(inRecord,"SHIPASSIGN_WORD_SEQNO"));
			recStockColumn.setField("SHIP_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"SHIP_CD"));
			recStockColumn.setField("SHIP_NAME", 			ydDaoUtils.paraRecChkNull(inRecord,"SHIP_NAME"));
			recStockColumn.setField("RSHP_HOLD_NO", 		ydDaoUtils.paraRecChkNull(inRecord,"RSHP_HOLD_NO"));
			recStockColumn.setField("SAILNO", 				ydDaoUtils.paraRecChkNull(inRecord,"SAILNO"));
			recStockColumn.setField("MODIFIER", 			"DMYDR018");

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			//rVal= YdCommonUtils.getYdAimRtGp("P",inRecord );		
			recStockColumn.setField("YD_AIM_RT_GP", "N3");
			
//SJH10.E
			//운송지시대상 출하보류코드(URGENT_DIST_YN)를 초기화한다. - 방법은 쿼리에서 항목 초기화
			//인도조건구분 코드에 운송수단구분 항목으로 대체
			if("".equals(ydDaoUtils.paraRecChkNull(inRecord,"TRANS_MEANS_GP"))){
				recStockColumn.setField("DELIVER_TERM_CD", 		ydDaoUtils.paraRecChkNull(inRecord,"DELIVER_TERM_CD"));
			}else{
				recStockColumn.setField("DELIVER_TERM_CD", 		ydDaoUtils.paraRecChkNull(inRecord,"TRANS_MEANS_GP"));
			}
			
			recStockColumn.setField("DETAIL_ARR_CD", 		ydDaoUtils.paraRecChkNull(inRecord,"DETAIL_ARR_CD"));
			
			if(ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD").equals("M")) {
				recStockColumn.setField("PRE_AR_STAT_CD", 	"1"); //선별대상에 포함된다.
			}
			
			//2013.02.15 진고코드가 'N' 이더라도 ISSUE_GP가 '2'(->보관매출발생제) 이면
			// YD_저장품의 보관매출방생코드를 '2'로 설정하여  선별 대상에서 제외 시킨다.
			if(ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD").equals("N") 
					&& ydDaoUtils.paraRecChkNull(inRecord, "ISSUE_GP").equals("2") ) {
				
				recStockColumn.setField("PRE_AR_STAT_CD", 	"2");
			}
			//****************************************************************************************************

			//저장품갱신******************************************************************************************** 
//			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPlateYdStockDMYDR028*/
			
			intRtnVal = ydStockDao.updYdStock_DMYDR028(recStockColumn);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[후판제품운송지시대기] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[후판제품운송지시대기] UPDATE Success",3);
			//****************************************************************************************************

			//======================================================
			// 2009.08.31 권오창
			// 저장품제원 : 후판제품 L2 로 송신(YDY4L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			
			// 2021. 05. 17 전문생성 시점에 PT_PLATE_COMM에 진도코드가 바뀌지 않은 문제가 있어
			// 진도코드를 파라메터로 넘겨 처리함
			recResult.setField("CURR_PROG_CD", ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(ydDaoUtils.paraRecChkNull(inRecord,"YD_GP"))) { //-2013.01.07 수정 (3기)
				recResult.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고 L2전문
			} else {
				recResult.setField("MSG_ID"         , "YDY4L002"); //1후판 제품창고 L2전문
			}
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			ydDelegate.sendMsg(recResult);	
			
			// 전사물류개선 2021. 4. 3
			if(PlateGdsYdUtil.isSendToEaiY9_stlNo( ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO")) ){
				recResult.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
				ydDelegate.sendMsg(recResult);
			}
		}catch(Exception e){

			szMsg="[후판제품운송지시대기]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try-catch

		szMsg="후판제품운송지시대기  등록("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procPlateGdsTrnOrdWait()
	
	/**
	 * 후판제품운송상차지시(DMYDR021) - L3 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlateGdsTrnOrd(JDTORecord inRecord)throws JDTOException  {
		/*
		 * 업무기준 : 1. 수신된 전문의 재료들을 재료상태변경이력 테이블로 등록
		 *			 2. 저장품테이블의 재료들에 재료진도코드와 야드목표행선을 수정처리
		 *			 3. 저장품이 적치된 저장위치 정보를 조회
		 *				- 차량진입순서[A-->D]에 따라 적치열Asc, 적치베드Asc, 적치단 Desc순으로 조회
		 *				- 저장위치가 04, 05, 06스판이면 A통로, 07스판이면 B통로로 처리한다.
		 *			 4. 위의 저장위치정보를 가지고 차량이 입동가능한 차량정지위치를 조회한다
		 *				- 입동가능한 차량정지위치가 존재하지 않을 시 첫번째 차량정지위치[기본값]를 사용한다.
		 *			 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
		 *			 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.11
		 */
		//DAO 정의
		YdStockDao ydStockDao = new YdStockDao();									//저장품DAO
		YdStkColDao ydStkColDao = new YdStkColDao();								//적치열 DAO
		YdStkBedDao ydStkBedDao = new YdStkBedDao();								//적치베드DAO
		YdCarSchDao ydCarSchDao = new YdCarSchDao();								//차량스케줄DAO
		YdEqpDao   ydEqpDao   = new YdEqpDao();
		
		
		//JDTO 정의
		JDTORecord recStlNo 		= JDTORecordFactory.getInstance().create();
		JDTORecord	recEditColumn	= JDTORecordFactory.getInstance().create();
		JDTORecord	recPara			= JDTORecordFactory.getInstance().create();
		JDTORecord	recInTemp		= null;
		JDTORecordSet rsGetStock 	= null;
		JDTORecordSet rsResult		= null;
		//기본 변수 정의
		String szMethodName 		= "procPlateGdsTrnOrd";
		String szOperationName		= "후판제품운송상차지시(DMYDR021)";
		String szMsg 				= "";
		//로컬에서 사용되는 변수 정의
		String szSTL_NO 			= "";
		String szYD_GP				= null;
		String szYD_BAY_GP			= null;
		String szYD_STK_COL_GP		= null;
		String szYD_EQP_GP			= null;
		String szYD_STK_COL_ACT_STAT = null;
		String szWLOC_CD			= null;
		String szYD_PNT_CD			= null;
		String szCAR_NO 			= null;
		String szCARD_NO 			= null;
		String szCarLotId			= null;
		String szTRANS_ORD_DATE		= null;
		String szTRANS_ORD_SEQNO	= null;
		String szYD_CAR_SCH_ID		= null;
		String szRtnMsg				= "";
		String szAPPLY_YN  			= "N";
		String szYD_SAPN_COL_GP     = "";
		String szUNIQUE_ID          = "";	 
 
		int intA_DONG  				= 0;
		int intB_DONG  				= 0;
		int intC_DONG  				= 0;
		int intD_DONG  				= 0;
		int intE_DONG  				= 0;
		int intF_DONG  				= 0;
		int intYD_SAPN_COL_GP     	= 0;
		
		int intYD_EQP_WRK_SH		= 0;
		boolean isCarArrPoint		= false;
		int intRtnVal 				= 0;
		int	i						= 0;
		JDTORecordSet rsResult1 				= null;
		
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){
			szMsg="[" + szOperationName + "] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 후판제품운송상차지시 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//수신한 전문값***********************************************************
			/*
			STL_APPEAR_GP	재료외형구분
			TRANS_ORD_DT	운송지시일자
			TRANS_ORD_SEQNO	운송지시순번
			CAR_NO			차량번호			
			CARD_NO			카드번호			
			ARR_WLOC_CD     착지개소코드		
			ARR_YD_PNT_CD 	착지야드포인트코드
			YD_EQP_WRK_SH	야드설비작업매수
			STL_NO1			재료번호1
			STL_NO2			재료번호2
			STL_NO3			재료번호3
			STL_NO4			재료번호4
			STL_NO5			재료번호5
			STL_NO6			재료번호6
			STL_NO7			재료번호7
			STL_NO8			재료번호8
			STL_NO9			재료번호9
			STL_NO10		재료번호10
			STL_NO11		재료번호11
			STL_NO12		재료번호12
			STL_NO13		재료번호13
			STL_NO14		재료번호14
			STL_NO15		재료번호15
			STL_NO16		재료번호16
			STL_NO17		재료번호17
			STL_NO18		재료번호18
			STL_NO19		재료번호19
			STL_NO20		재료번호20
			CURR_PROG_CD    현재진도코드
			CANCEL_YN 		취소유무
			LOT_NO			선별LOTID
			*/
			
			//******************************************************************
			szCAR_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO");
			szCARD_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO");
			szTRANS_ORD_DATE 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT");
			szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO");
			intYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNullInt(inRecord,"YD_EQP_WRK_SH");
			szWLOC_CD 			= ydDaoUtils.paraRecChkNull(inRecord,"ARR_WLOC_CD");
			szYD_PNT_CD 		= ydDaoUtils.paraRecChkNull(inRecord,"ARR_YD_PNT_CD");
			szCarLotId			= ydDaoUtils.paraRecChkNull(inRecord,"LOT_NO");
			szUNIQUE_ID 		= ydDaoUtils.paraRecChkNull(inRecord,"UNIQUE_ID");
 
			
			/*
			 *  복수동 출하시 나머지 출하작업에 대한 상태 = 'Y'
			 *  -- 권하처리시 선 복수동 작업일때 해당항목을 셋팅
			 */
			String szDOUBLEDONG_CHECK  			= ydDaoUtils.paraRecChkNull(inRecord,"DOUBLEDONG_CHECK");
			String szDOUBLEDONG_YD_CAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(inRecord,"YD_CAR_SCH_ID"); 
			
			szMsg="["+szOperationName+"] szUNIQUE_ID:" + szUNIQUE_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if("".equals(szUNIQUE_ID)){
				szUNIQUE_ID = "0";
			}
			
			//전문항목 에러체크
			if("".equals(szCAR_NO)){
				szMsg="[" + szOperationName + "] CAR_NO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			if("".equals(szCARD_NO)){
				szMsg="[" + szOperationName + "] CARD_NO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			if("".equals(szTRANS_ORD_DATE)){
				szMsg="[" + szOperationName + "] TRANS_ORD_DT IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			if("".equals(szTRANS_ORD_SEQNO)){
				szMsg="[" + szOperationName + "] TRANS_ORD_SEQNO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			if(szCARD_NO.startsWith("P")&&"".equals(szWLOC_CD)){
				szMsg="[" + szOperationName + "] Pallet 출하 ARR_WLOC_CD IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			if(szCARD_NO.startsWith("P")&&"".equals(szYD_PNT_CD)){
				szMsg="[" + szOperationName + "] Pallet 출하 ARR_YD_PNT_CD IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			//---------------------------------------------------------------------------------
			//	후판출하운송지시 상차정보가 중복해서 수신될경우 SKIP처리한다. 
			//---------------------------------------------------------------------------------
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DT"		, szTRANS_ORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			recPara.setField("CARD_NO"			, szCARD_NO );

			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdDEL_YN*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 304);
			if(intRtnVal > 0){
				szMsg= "["+szOperationName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
						
			/////////////////////////////PALLET 상차지시 처리////////////////////////////////////////
			if(szCARD_NO.startsWith("P"))
			{
				JDTORecord	recInYdPnt		= null;
				JDTORecordSet rsYdPnt 		= null;
				rsYdPnt = JDTORecordFactory.getInstance().createRecordSet("");
				recInYdPnt = JDTORecordFactory.getInstance().create();
				recInYdPnt.setField("WLOC_CD"	, szWLOC_CD);
				recInYdPnt.setField("YD_PNT_CD"	, szYD_PNT_CD);
				recInYdPnt.setField("YD_EQP_GP"	, "PT");
				intRtnVal = ydStkColDao.getYdStkcol(recInYdPnt, rsYdPnt, 14);
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						szMsg="["+szOperationName+"] PALLET 출하상차LOT 대상재가 존재하는 개소/포인트 조회 시 data not found";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if(intRtnVal == -2 ) {
						szMsg="["+szOperationName+"] PALLET 출하상차LOT 대상재가 존재하는 개소/포인트 조회 시 parameter error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg="["+szOperationName+"] PALLET 출하상차LOT 대상재가 존재하는 개소/포인트 조회 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return;
				}
				
				szMsg="["+szOperationName+"] PALLET 출하상차LOT 대상재가 존재하는 개소/포인트  사용가능 체크 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				for( i = 1; i <= rsYdPnt.size(); i++ ) {
					rsYdPnt.first();
					recInYdPnt = rsYdPnt.getRecord();
					
					szYD_STK_COL_GP 		= StringHelper.evl(recInYdPnt.getFieldString("YD_STK_COL_GP"), "");
					szYD_STK_COL_ACT_STAT 	= StringHelper.evl(recInYdPnt.getFieldString("YD_STK_COL_ACT_STAT"), "");
										
					if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_INACTIVE) ) {
						szMsg="["+szOperationName+"] 조회된 PALLET 개소/포인트["+szYD_STK_COL_GP+"]["+szYD_STK_COL_ACT_STAT+"]가 사용가능 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else{
						szMsg="["+szOperationName+"] 조회된 PALLET 개소/포인트["+szYD_STK_COL_GP+"]["+szYD_STK_COL_ACT_STAT+"]가 사용불가 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						return;
					}
				}
			}
			///////////////////////////////////////////////////////////////////////////////////
			
			if(!"Y".equals(szDOUBLEDONG_CHECK)){
				
				//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
				recEditColumn 	= JDTORecordFactory.getInstance().create();
				recEditColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
				recEditColumn.setField("TRANS_ORD_DATE", 		szTRANS_ORD_DATE);
				recEditColumn.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
				recEditColumn.setField("CAR_NO", 				szCAR_NO);
				recEditColumn.setField("CARD_NO", 				szCARD_NO);
				recEditColumn.setField("ARR_WLOC_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"ARR_WLOC_CD"));
				recEditColumn.setField("YD_PNT_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"ARR_YD_PNT_CD"));
				recEditColumn.setField("YD_STK_LOT_CD", 		ydDaoUtils.paraRecChkNull(inRecord,"TEL_NO"));
				recEditColumn.setField("MODIFIER", 				"DMYDR021");
				
				szMsg="[" + szOperationName + "]["+intYD_EQP_WRK_SH+"]매수에 대한  저장품 업데이트 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				for(i = 1 ; i<= intYD_EQP_WRK_SH; i++){
					
					szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i);
					 
					if(szSTL_NO.equals("")){
						break;
					}else {
						inRecord.setField("STL_NO", 	szSTL_NO);
					}
					recEditColumn.setField("STL_NO",	szSTL_NO);
					
					recEditColumn.setField("YD_AIM_RT_GP", "L6");
					recEditColumn.setField("STL_PROG_CD",  ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
						
					/* 
					 * 1. 저장품테이블의 재료들에 재료진도코드와 야드목표행선을 수정처리
					 */
					//저장품갱신******************************************************************************************** 
					szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 야드목표행선[]과 재료진도코드[] 업데이트 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					intRtnVal = ydStockDao.updYdStock(recEditColumn, 0);
					if(intRtnVal <= 0){
						szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 야드목표행선[]과 재료진도코드[] 업데이트 시 오류발생 - 반환값 : " +intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
					szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 야드목표행선[]과 재료진도코드[] 업데이트 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					//****************************************************************************************************
	
				} //end of for *******************************************************************************************
			}
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			if(szCARD_NO.startsWith("P")){ // PALLET 출하 
				
			}else{// 일반차량 출하
				/*
				 * 2. 저장품이 적치된 저장위치 정보를 조회 - 저장위치가 04, 05, 06스판이면 A통로, 07스판이면 B통로로 처리한다.
				 */
				szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//저장품 동 구하기 
				rsGetStock 	= JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP",   "");
				recInTemp.setField("CARD_NO", 		  szCARD_NO);
				recInTemp.setField("TRANS_ORD_DATE" , szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
				
				intRtnVal = ydStockDao.getYdStock_DoubleDong(recInTemp, rsGetStock, "1");
				if(intRtnVal <= 0){
					return ;
				}
    		
				szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 성공 - 건수["+intRtnVal+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsGetStock.first();
				recStlNo = rsGetStock.getRecord();
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recStlNo,"YD_STK_COL_GP");
				szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 대상재가 존재하는 동["+szYD_STK_COL_GP+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				szYD_GP 	= szYD_STK_COL_GP.substring(0, 1);
				szYD_BAY_GP = szYD_STK_COL_GP.substring(1, 2);
				szYD_EQP_GP = szYD_STK_COL_GP.substring(2, 4);
				/*
				 * 4. 위의 저장위치정보를 가지고 차량이 입동가능한 차량정지위치를 조회한다
				 *	- 입동가능한 차량정지위치가 존재하지 않을 시 첫번째 차량정지위치[기본값]를 사용한다.
				 */
				szMsg="[" + szOperationName + "] 출하상차LOT 대상재가 존재하는 차량이 입동가능한 차량정지위치를 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//차량정지위치 구하기
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP.substring(0,2) + "PT");
		    	intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 8);
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량정지위치를 조회 시 data not found";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if(intRtnVal == -2 ) {
						szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량정지위치를 조회 시 parameter error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량정지위치를 조회 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return ;
				}
				
				szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량이 입동가능한 차량정지위치를 조회 성공 - 건수["+intRtnVal+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szMsg="["+szOperationName+"] 조회된 차량정지위치에 대한 사용가능 체크 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------------------------
				//	신 상차처리 적용여부
	            //  권상시 입동 지시로 
				//------------------------------------------------------------------------------------------------------------
				JDTORecordSet 	outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecordSet 	outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord 		inRecord1 	= JDTORecordFactory.getInstance().create();
				JDTORecord 		inRecord2 	= JDTORecordFactory.getInstance().create();
				JDTORecord 		outRecord  	= JDTORecordFactory.getInstance().create();
				inRecord1.setField("REPR_CD_GP", "K00030");
				
				/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
				intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 999);
				if(intRtnVal > 0) {
					outResult.first();
					outRecord  = outResult.getRecord();
					szAPPLY_YN = outRecord.getFieldString("ITEM1");				
				}
				szMsg="신 상차도 처리 적용여부 " + szAPPLY_YN ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if(szAPPLY_YN.equals("Y")) {	

					inRecord1 	= JDTORecordFactory.getInstance().create();
					inRecord1.setField("REPR_CD_GP" ,"K00031");			
					inRecord1.setField("CD_GP" ,"D");			
			
					/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgt*/
					intRtnVal = ydCarSchDao.getYdCarsch(inRecord1, outResult1, 400);
					
					if (intRtnVal > 0) {
						outResult1.first();
						inRecord2 = outResult1.getRecord();
						intA_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"A_DONG");
						intB_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"B_DONG");
						intC_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"C_DONG");
						intD_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"D_DONG");
						intE_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"E_DONG");
						intF_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"F_DONG");
						
						szYD_SAPN_COL_GP = szYD_STK_COL_GP.substring(2, 6);

						/// 일반야드에 있는 경우 : 혹시 차량위는 제외 
						if(szYD_SAPN_COL_GP.matches("\\d\\d\\d\\d")){
							intYD_SAPN_COL_GP = Integer.parseInt(szYD_STK_COL_GP.substring(2, 6));  // SPAN열정보
	
							szMsg="["+szOperationName+"] 조회된 차량정지위치에 SPAN열정보" + intYD_SAPN_COL_GP;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
							
							szMsg="["+szOperationName+"] 기준정보 A_DONG:" + intA_DONG + " B_DONG:" + intB_DONG +" C_DONG:" 
							       + intC_DONG + " D_DONG:" + intD_DONG +"  E_DONG:" + intE_DONG +"  F_DONG:" + intF_DONG;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

							for( i = 1; i <= rsResult.size(); i++ ) {
								rsResult.absolute(i);
								recInTemp 		= rsResult.getRecord();
								szYD_STK_COL_GP = StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP"), "");
								szWLOC_CD 		= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
								szYD_STK_COL_ACT_STAT = StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_ACT_STAT"), "");
								
								szMsg="["+szOperationName+"] i:" + i ;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
								if(( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_INACTIVE) ) && (szYD_STK_COL_GP.substring(5,6).equals("1"))) {
									szMsg="["+szOperationName+"] szYD_BAY_GP:" + szYD_BAY_GP + " szYD_STK_COL_GP.substring(1,2):" + szYD_STK_COL_GP.substring(1,2) 
									   +" szYD_STK_COL_GP.substring(4,5):"  + szYD_STK_COL_GP.substring(4,5) + " szYD_STK_COL_GP: " + szYD_STK_COL_GP;
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
									
									// 대상동 하구 상차도 동하구 같고
									if(("A".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("A"))) {
									
										if(( intA_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("A"))) {
											szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - A통로";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											isCarArrPoint = true;
											break;
										} else if(( intA_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("B"))) {
											szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - B통로";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											isCarArrPoint = true;
											break;
										}
									} else if(("B".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("B"))) {
										      
										if(( intB_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("A"))) {
											szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - A통로";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											isCarArrPoint = true;
											break;
										} else if(( intB_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("B"))) {
											szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - B통로";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											isCarArrPoint = true;
											break;
										}
									} else if(("C".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("C"))) {
										if(( intC_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("A"))) {
											szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - A통로";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											isCarArrPoint = true;
											break;
										} else if(( intC_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("B"))) {
											szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - B통로";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											isCarArrPoint = true;
											break;
										}
									} else if(("D".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("D"))) {
										if(( intD_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("A"))) {
											szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - A통로";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											isCarArrPoint = true;
											break;
										} else if(( intD_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("B"))) {
											szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - B통로";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											isCarArrPoint = true;
											break;
										}
									} else if(("E".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("E"))) {
										if(( intE_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("A"))) {
											szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - A통로";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											isCarArrPoint = true;
											break;
										} else if(( intE_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("B"))) {
											szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - B통로";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											isCarArrPoint = true;
											break;
										}
									} else if(("F".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("F"))) {
										if(( intF_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("A"))) {
											szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - A통로";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											isCarArrPoint = true;
											break;
										} else if(( intF_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("B"))) {
											szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - B통로";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											isCarArrPoint = true;
											break;
										}
									}
								}
							}	//szWLOC_CD 이게 필요함
							
						} else {
							szAPPLY_YN = "N";
						}
					} else { // 기준 READ
						szAPPLY_YN = "N";
					}
				} 
				
				if(szAPPLY_YN.equals("Y")) {	
				} else {	
						
					//후판제품창고는 04, 05, 06스판이면 A통로, 07스판이면 B통로로 처리한다.
					for( i = 1; i <= rsResult.size(); i++ ) {
						rsResult.first();
						recInTemp 		= rsResult.getRecord();
						szYD_STK_COL_GP = StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP"), "");
						szWLOC_CD 		= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
						szYD_STK_COL_ACT_STAT = StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_ACT_STAT"), "");
						
						szMsg="["+szOperationName+"] 조회된 출하상차 대상재의 저장위치 스판정보["+szYD_EQP_GP+"]가 04, 05, 06스판이면 A통로로, 07스판이면 B통로로 처리한다. ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]에 대한 야드적치열활성상태["+szYD_STK_COL_ACT_STAT+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_INACTIVE) ) {
							if("A".equals(szYD_BAY_GP)){
								if( (szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_04) || szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_05)) && szYD_STK_COL_GP.substring(4,5).equals("A")) {
									//대상재가 01스판인 경우에는 A통로 진입
									szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - A통로";
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									isCarArrPoint = true;
									break;
								}else if( (szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_06) || szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_07) || szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_TP)) && szYD_STK_COL_GP.substring(4,5).equals("B")) {
									szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - B통로";
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									isCarArrPoint = true;
									break;
								}
							}else{
								if( (szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_04) || szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_05)||szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_06)) && szYD_STK_COL_GP.substring(4,5).equals("A")) {
									//대상재가 04, 05, 06스판인 경우에는 A통로 진입
									szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - A통로";
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									isCarArrPoint = true;
									break;
								}else if( (szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_07) || szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_TP)) && szYD_STK_COL_GP.substring(4,5).equals("B")) {
									szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - B통로";
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									isCarArrPoint = true;
									break;
								}
							}
						}
					}
				}
				
				
				if( !isCarArrPoint ) {
					
					if(szAPPLY_YN.equals("Y")) {	
						
						// 디폴트 처리 하는거 // 입동대기
						if("A".equals(szYD_BAY_GP)) {
						
							if(  intYD_SAPN_COL_GP <= intA_DONG) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTA1"; 
								szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - A통로";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							} else {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTB1"; 
								szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - B통로";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
						} else if("B".equals(szYD_BAY_GP)) {
							
							if(  intYD_SAPN_COL_GP <= intB_DONG) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTA1"; 
								szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - A통로";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							} else {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTB1"; 
								szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - B통로";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
						} else if("C".equals(szYD_BAY_GP)) {
							
							if(  intYD_SAPN_COL_GP <= intC_DONG) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTA1"; 
								szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - A통로";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							} else {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTB1"; 
								szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - B통로";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
						} else if("D".equals(szYD_BAY_GP)) {
							
							if(  intYD_SAPN_COL_GP <= intD_DONG) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTA1"; 
								szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - A통로";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							} else {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTB1"; 
								szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - B통로";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
						} else if("E".equals(szYD_BAY_GP)) {
							
							if(  intYD_SAPN_COL_GP <= intE_DONG) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTA1"; 
								szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - A통로";
								isCarArrPoint = true;
							} else {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTB1"; 
								szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - B통로";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
						} else if("F".equals(szYD_BAY_GP)) {
							
							if(  intYD_SAPN_COL_GP <= intF_DONG) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTA1"; 
								szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - A통로";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							} else {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTB1"; 
								szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능 - B통로";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
						}	
						
						szMsg="["+szOperationName+"] 사용가능한 차량정지위치가 없으므로 기본값["+szYD_STK_COL_GP+"]을 사용";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
					} else {
		
						if("A".equals(szYD_BAY_GP)){
							if( szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_04) || 
								szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_05)) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTA1";
							}else if( szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_06) || 
									  szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_07) || 
									  szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTB1";
							}
						}else{
							if( szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_04) || 
								szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_05) || 
								szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_06) ) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTA1";
							}else if( szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_07) || 
									  szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PTB1";
							}
						}
						
						szMsg="["+szOperationName+"] 사용가능한 차량정지위치가 없으므로 기본값["+szYD_STK_COL_GP+"]을 사용";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}	
				}
				
				szMsg="["+szOperationName+"] 조회된 차량정지위치에 대한 사용가능 체크 완료 - 사용가능한 차량정지위치["+szYD_STK_COL_GP+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//-----------------------------------------------------------------------------------------------------------------
				//	사용가능한 차량정지위치로 적치열 조회 후 개소POINT를 구함
				//-----------------------------------------------------------------------------------------------------------------
				
				szMsg="["+szOperationName+"] 사용가능한 차량정지위치["+szYD_STK_COL_GP+"]로 적치열 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP",			szYD_STK_COL_GP);
				
				szRtnMsg = DaoManager.getYdStkcol(recInTemp, rsResult, 0);
				
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					szMsg="["+szOperationName+"] 사용가능한 차량정지위치["+szYD_STK_COL_GP+"]로 적치열 조회 시 오류발생 - 메세지 : " + szRtnMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
				
				rsResult.first();
				recInTemp		= rsResult.getRecord();
				
				szYD_PNT_CD		= ydDaoUtils.paraRecChkNull(recInTemp,"YD_PNT_CD");
				
				szMsg="["+szOperationName+"] 사용가능한 차량정지위치["+szYD_STK_COL_GP+"]로 적치열 조회 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			//-----------------------------------------------------------------------------------------------------------------
			
			if("Y".equals(szDOUBLEDONG_CHECK)){
				/*
				 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 초기화 수정한다.
				 */
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    		szDOUBLEDONG_YD_CAR_SCH_ID);
				recInTemp.setField("REGISTER",         		szRcvTcCode);
				recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
				recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
				recInTemp.setField("SPOS_WLOC_CD",     		szWLOC_CD);								//발지개소코드
				recInTemp.setField("YD_PNT_CD1",     		szYD_PNT_CD);							//야드포인트코드1
				recInTemp.setField("CAR_NO",           		szCAR_NO);								//차량번호
				recInTemp.setField("CARD_NO",          		szCARD_NO);								//카드번호
				recInTemp.setField("YD_CARLD_LEV_DT",  		YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
		    	recInTemp.setField("YD_CARLD_STOP_LOC",		szYD_STK_COL_GP);						//차량상차정지위치
		    	recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
		    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		"2");									//입동지시순번 (2013.06.20 cho 1순위 --> 2순위 변경)
				recInTemp.setField("DEL_YN", 				"N");					
				
				//차량스케줄수정
		    	intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
		    	
	    		if( intRtnVal <= 0 ){
					szMsg="[" + szOperationName + "] 차량스케줄 수정 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
	    		}
	    		
	    		//차량 스케쥴재료수정
	    		YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao(); 
	        	intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(recInTemp, 1) ;
	            
	    		szMsg="[" + szOperationName + "] 차량스케줄 수정 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				
	 
				/*
				 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
				 */
				szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    		szYD_CAR_SCH_ID);
				recInTemp.setField("REGISTER",         		szRcvTcCode);
				recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
				recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
				recInTemp.setField("CAR_KIND", 				"TR");
				recInTemp.setField("SPOS_WLOC_CD",     		szWLOC_CD);								//발지개소코드
				recInTemp.setField("YD_PNT_CD1",     		szYD_PNT_CD);							//야드포인트코드1
				recInTemp.setField("CAR_NO",           		szCAR_NO);								//차량번호
				recInTemp.setField("CARD_NO",          		szCARD_NO);								//카드번호
				recInTemp.setField("YD_CARLD_LEV_DT",  		YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
		    	recInTemp.setField("YD_CARLD_STOP_LOC",		szYD_STK_COL_GP);						//차량상차정지위치
		    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 --기본값으로 설정(9)
				recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
				recInTemp.setField("IF_SEQ_NO", 			szUNIQUE_ID);	// 운송지시 SEQ
	    		//차량스케줄 등록
		    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
	    		if( intRtnVal <= 0 ){
					szMsg="[" + szOperationName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
	    		}
	    		
	    		szMsg="[" + szOperationName + "] 차량스케줄 생성 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
	 
			
	 
				/*
				 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
				 */
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("TC_CODE"				,"YDYDJ633");
				recInTemp.setField("TC_CREATE_DDTT"			,YdUtils.getCurDate("yyyyMMddHHmmss"));
				recInTemp.setField("YD_CAR_STOP_LOC"		,szYD_STK_COL_GP);
				recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
				recInTemp.setField("CALL_PGM"			    ,"SANGCHA");
				
				//----------------------------------------------------------------------------------
				//	동기화 문제로 인하여 JMS --> EJB Call로 변경
				//	수정자 : 임춘수
				//	수정일 : 1) 2009.12.30 - 최초등록
				//----------------------------------------------------------------------------------
				//ydDelegate.sendMsg(recInTemp);
	
				EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
				szRtnMsg = (String) ejbConn.trx("procCarBayInOrdReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				
				//----------------------------------------------------------------------------------
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 성공 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 
			
		}catch(Exception e){

			szMsg="[후판제품운송상차지시] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch
		
		szMsg="[후판제품운송상차지시]수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procPlateGdsTrnOrd()
	
	
	/**
	 * 후판제품운송상차지시(DMYDR021) - L3 (3기)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlateGdsTrnOrd3G(JDTORecord inRecord)throws JDTOException  {
		/*
		 * 업무기준 : 1. 수신된 전문의 재료들을 재료상태변경이력 테이블로 등록
		 *			 2. 저장품테이블의 재료들에 재료진도코드와 야드목표행선을 수정처리
		 *			 3. 저장품이 적치된 저장위치 정보를 조회
		 *				- 차량진입순서[A-->D]에 따라 적치열Asc, 적치베드Asc, 적치단 Desc순으로 조회
		 *				- 저장위치가 04, 05, 06스판이면 A통로, 07스판이면 B통로로 처리한다.
		 *			 4. 위의 저장위치정보를 가지고 차량이 입동가능한 차량정지위치를 조회한다
		 *				- 입동가능한 차량정지위치가 존재하지 않을 시 첫번째 차량정지위치[기본값]를 사용한다.
		 *			 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
		 *			 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.11
		 */
		//DAO 정의
		YdStockDao ydStockDao = new YdStockDao();									//저장품DAO
		YdStkColDao ydStkColDao = new YdStkColDao();								//적치열 DAO
		YdStkBedDao ydStkBedDao = new YdStkBedDao();								//적치베드DAO
		YdCarSchDao ydCarSchDao = new YdCarSchDao();								//차량스케줄DAO
		YdEqpDao   ydEqpDao   = new YdEqpDao();
		
		YdPlateCommDAO commDao = new YdPlateCommDAO(); //3기 후판제품공용dao
		
		//JDTO 정의
		JDTORecord recStlNo 		= JDTORecordFactory.getInstance().create();
		JDTORecord	recEditColumn	= JDTORecordFactory.getInstance().create();
		JDTORecord	recPara			= JDTORecordFactory.getInstance().create();
		JDTORecord	recInTemp		= null;
		JDTORecordSet rsGetStock 	= null;
		JDTORecordSet rsResult		= null;
		//기본 변수 정의
		String szMethodName 		= "procPlateGdsTrnOrd3G";
		String szOperationName		= "후판제품운송상차지시(DMYDR021)";
		String szMsg 				= "";
		//로컬에서 사용되는 변수 정의
		String szSTL_NO 			= "";
		String szYD_GP				= null;
		String szYD_BAY_GP			= null;
		String szYD_STK_COL_GP		= null;
		String szYD_EQP_GP			= null;
		String szYD_STK_COL_ACT_STAT = null;
		String szWLOC_CD			= null;
		String szYD_PNT_CD			= null;
		String szCAR_NO 			= null;
		String szCARD_NO 			= null;
		String szCarLotId			= null;
		String szTRANS_ORD_DATE		= null;
		String szTRANS_ORD_SEQNO	= null;
		String szYD_CAR_SCH_ID		= null;
		String szRtnMsg				= "";
		String szAPPLY_YN  			= "N";
		String szYD_SAPN_COL_GP     = "";
		String szUNIQUE_ID          = "";	 
		String szCARLD_PNT_CD		= null;
		String szCAR_KIND			= null;

		int intA_DONG  				= 0;
		int intB_DONG  				= 0;
		int intC_DONG  				= 0;
		int intD_DONG  				= 0;
		int intE_DONG  				= 0;
		int intF_DONG  				= 0;
		int intG_DONG  				= 0;
		int intYD_SAPN_COL_GP     	= 0;
		
		int intYD_EQP_WRK_SH		= 0;
		boolean isCarArrPoint		= false;
		int intRtnVal 				= 0;
		int	i						= 0;
		JDTORecordSet rsResult1 				= null;
		
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){
			szMsg="[" + szOperationName + "] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 후판제품운송상차지시 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//수신한 전문값***********************************************************
			/*
			STL_APPEAR_GP	재료외형구분
			TRANS_ORD_DT	운송지시일자
			TRANS_ORD_SEQNO	운송지시순번
			CAR_NO			차량번호			
			CARD_NO			카드번호			
			ARR_WLOC_CD     착지개소코드		
			ARR_YD_PNT_CD 	착지야드포인트코드
			YD_EQP_WRK_SH	야드설비작업매수
			STL_NO1			재료번호1
			STL_NO2			재료번호2
			STL_NO3			재료번호3
			STL_NO4			재료번호4
			STL_NO5			재료번호5
			STL_NO6			재료번호6
			STL_NO7			재료번호7
			STL_NO8			재료번호8
			STL_NO9			재료번호9
			STL_NO10		재료번호10
			STL_NO11		재료번호11
			STL_NO12		재료번호12
			STL_NO13		재료번호13
			STL_NO14		재료번호14
			STL_NO15		재료번호15
			STL_NO16		재료번호16
			STL_NO17		재료번호17
			STL_NO18		재료번호18
			STL_NO19		재료번호19
			STL_NO20		재료번호20
			CURR_PROG_CD    현재진도코드
			CANCEL_YN 		취소유무
			LOT_NO			선별LOTID
			CARLD_PNT_CD	상차포인트
			PLBAY_PNT_YN	복수포인트유무
			CAR_KIND		차량종류
			*/
			
			//******************************************************************
			szCAR_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO");
			szCARD_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO");
			szTRANS_ORD_DATE 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT");
			szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO");
			intYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNullInt(inRecord,"YD_EQP_WRK_SH");
			szWLOC_CD 			= ydDaoUtils.paraRecChkNull(inRecord,"ARR_WLOC_CD");
			szYD_PNT_CD 		= ydDaoUtils.paraRecChkNull(inRecord,"ARR_YD_PNT_CD");
			szCarLotId			= ydDaoUtils.paraRecChkNull(inRecord,"LOT_NO");
			szUNIQUE_ID 		= ydDaoUtils.paraRecChkNull(inRecord,"UNIQUE_ID");
			szCARLD_PNT_CD		= ydDaoUtils.paraRecChkNull(inRecord,"CARLD_PNT_CD"); //상차포인트 (차량포인트)
			szCAR_KIND			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_KIND"); //차량종류
			
			/*
			 *  복수동 출하시 나머지 출하작업에 대한 상태 = 'Y'
			 *  -- 권하처리시 선 복수동 작업일때 해당항목을 셋팅
			 */
			String szDOUBLEDONG_CHECK  			= ydDaoUtils.paraRecChkNull(inRecord,"DOUBLEDONG_CHECK");
			String szDOUBLEDONG_YD_CAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(inRecord,"YD_CAR_SCH_ID"); 
			
			szMsg="["+szOperationName+"] szUNIQUE_ID:" + szUNIQUE_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if("".equals(szUNIQUE_ID)){
				szUNIQUE_ID = "0";
			}
			
			//전문항목 에러체크
			if("".equals(szCAR_NO)){
				szMsg="[" + szOperationName + "] CAR_NO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			if("".equals(szCARD_NO)){
				szMsg="[" + szOperationName + "] CARD_NO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			if("".equals(szTRANS_ORD_DATE)){
				szMsg="[" + szOperationName + "] TRANS_ORD_DT IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			if("".equals(szTRANS_ORD_SEQNO)){
				szMsg="[" + szOperationName + "] TRANS_ORD_SEQNO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			//if(szCARD_NO.startsWith("P")&&"".equals(szWLOC_CD)){
			//	szMsg="[" + szOperationName + "] Pallet 출하 ARR_WLOC_CD IS NULL ("+szRcvTcCode+")";
			//	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//	return;
			//}
			
			//if(szCARD_NO.startsWith("P")&&"".equals(szYD_PNT_CD)){
			//	szMsg="[" + szOperationName + "] Pallet 출하 ARR_YD_PNT_CD IS NULL ("+szRcvTcCode+")";
			//	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//	return;
			//}

			//---------------------------------------------------------------------------------
			//	후판출하운송지시 상차정보가 중복해서 수신될경우 SKIP처리한다. 
			//---------------------------------------------------------------------------------
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DT"		, szTRANS_ORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			recPara.setField("CARD_NO"			, szCARD_NO );

			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdDEL_YN*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 304);
			if(intRtnVal > 0){
				szMsg= "["+szOperationName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
						
			/////////////////////////////PALLET 상차지시 처리////////////////////////////////////////
			if(szCARD_NO.startsWith("P"))
			{
				//차량포인트 테이블에서 WLOC_CD 와 YD_PNT_CD 를 읽어 온다. 
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CARPNT_CD", 		  szCARLD_PNT_CD);
				
				intRtnVal = commDao.select(recInTemp, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0102");
				
				if(intRtnVal <= 0){
					szMsg="["+szOperationName+"] PALLET 출하 차량포인트 조회 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
					return ;
				}
				
				rsResult.first();
				recInTemp = rsResult.getRecord();
				
				szWLOC_CD   	= ydDaoUtils.paraRecChkNull(recInTemp,"WLOC_CD");
				szYD_PNT_CD 	= ydDaoUtils.paraRecChkNull(recInTemp,"YD_PNT_CD");
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recInTemp,"YD_STK_COL_GP");
				
				/* 
				JDTORecord	recInYdPnt		= null;
				JDTORecordSet rsYdPnt 		= null;
				rsYdPnt = JDTORecordFactory.getInstance().createRecordSet("");
				recInYdPnt = JDTORecordFactory.getInstance().create();
				recInYdPnt.setField("WLOC_CD"	, szWLOC_CD);
				recInYdPnt.setField("YD_PNT_CD"	, szYD_PNT_CD);
				recInYdPnt.setField("YD_EQP_GP"	, "PT");
				intRtnVal = ydStkColDao.getYdStkcol(recInYdPnt, rsYdPnt, 14);
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						szMsg="["+szOperationName+"] PALLET 출하상차LOT 대상재가 존재하는 개소/포인트 조회 시 data not found";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if(intRtnVal == -2 ) {
						szMsg="["+szOperationName+"] PALLET 출하상차LOT 대상재가 존재하는 개소/포인트 조회 시 parameter error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg="["+szOperationName+"] PALLET 출하상차LOT 대상재가 존재하는 개소/포인트 조회 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return;
				}
				
				szMsg="["+szOperationName+"] PALLET 출하상차LOT 대상재가 존재하는 개소/포인트  사용가능 체크 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				for( i = 1; i <= rsYdPnt.size(); i++ ) {
					rsYdPnt.first();
					recInYdPnt = rsYdPnt.getRecord();
					
					szYD_STK_COL_GP 		= StringHelper.evl(recInYdPnt.getFieldString("YD_STK_COL_GP"), "");
					szYD_STK_COL_ACT_STAT 	= StringHelper.evl(recInYdPnt.getFieldString("YD_STK_COL_ACT_STAT"), "");
										
					if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_INACTIVE) ) {
						szMsg="["+szOperationName+"] 조회된 PALLET 개소/포인트["+szYD_STK_COL_GP+"]["+szYD_STK_COL_ACT_STAT+"]가 사용가능 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else{
						szMsg="["+szOperationName+"] 조회된 PALLET 개소/포인트["+szYD_STK_COL_GP+"]["+szYD_STK_COL_ACT_STAT+"]가 사용불가 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						return;
					}
				}
				*/
			}
			///////////////////////////////////////////////////////////////////////////////////
			
			if(!"Y".equals(szDOUBLEDONG_CHECK)){ //복수동이 아닐 경우..
				
				//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
				recEditColumn 	= JDTORecordFactory.getInstance().create();
				recEditColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
				recEditColumn.setField("TRANS_ORD_DATE", 		szTRANS_ORD_DATE);
				recEditColumn.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
				recEditColumn.setField("CAR_NO", 				szCAR_NO);
				recEditColumn.setField("CARD_NO", 				szCARD_NO);
				recEditColumn.setField("ARR_WLOC_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"ARR_WLOC_CD"));
				recEditColumn.setField("YD_PNT_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"ARR_YD_PNT_CD"));
				recEditColumn.setField("YD_STK_LOT_CD", 		ydDaoUtils.paraRecChkNull(inRecord,"TEL_NO"));
				recEditColumn.setField("MODIFIER", 				"DMYDR021");
				
				szMsg="[" + szOperationName + "]["+intYD_EQP_WRK_SH+"]매수에 대한  저장품 업데이트 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				for(i = 1 ; i<= intYD_EQP_WRK_SH; i++){
					
					szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i);
					 
					if(szSTL_NO.equals("")){
						break;
					}else {
						inRecord.setField("STL_NO", 	szSTL_NO);
					}
					recEditColumn.setField("STL_NO",	szSTL_NO);
					
					recEditColumn.setField("YD_AIM_RT_GP", "L6");
					recEditColumn.setField("STL_PROG_CD",  ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
						
					/* 
					 * 1. 저장품테이블의 재료들에 재료진도코드와 야드목표행선을 수정처리
					 */
					//저장품갱신******************************************************************************************** 
					szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 야드목표행선[]과 재료진도코드[] 업데이트 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					intRtnVal = ydStockDao.updYdStock(recEditColumn, 0);
					if(intRtnVal <= 0){
						szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 야드목표행선[]과 재료진도코드[] 업데이트 시 오류발생 - 반환값 : " +intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
					szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 야드목표행선[]과 재료진도코드[] 업데이트 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					//****************************************************************************************************
	
				} //end of for *******************************************************************************************
			}
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			if(szCARD_NO.startsWith("P")){// PALLET 출하 
				
				/*
				 * 2. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
				 */
				szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    		szYD_CAR_SCH_ID);
				recInTemp.setField("REGISTER",         		szRcvTcCode);
				recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
				recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
				recInTemp.setField("CAR_KIND", 				szCAR_KIND);							//차량종류
				recInTemp.setField("SPOS_WLOC_CD",     		szWLOC_CD);								//발지개소코드
				recInTemp.setField("YD_PNT_CD1",     		szYD_PNT_CD);							//야드포인트코드1
				recInTemp.setField("CAR_NO",           		szCAR_NO);								//차량번호
				recInTemp.setField("CARD_NO",          		szCARD_NO);								//카드번호
				recInTemp.setField("YD_CARLD_LEV_DT",  		YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
		    	recInTemp.setField("YD_CARLD_STOP_LOC",		szYD_STK_COL_GP);						//차량상차정지위치
		    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 --기본값으로 설정(9) **
				recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
				recInTemp.setField("IF_SEQ_NO", 			szUNIQUE_ID);	// 운송지시 SEQ
	    		//차량스케줄 등록
		    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
	    		if( intRtnVal <= 0 ){
					szMsg="[" + szOperationName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
	    		}
	    		
	    		szMsg="[" + szOperationName + "] 차량스케줄 생성 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				/*
				 * 3. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
				 */
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("TC_CODE"				,"YDYDJ633");
				recInTemp.setField("TC_CREATE_DDTT"			,YdUtils.getCurDate("yyyyMMddHHmmss"));
				recInTemp.setField("YD_CAR_STOP_LOC"		,szYD_STK_COL_GP);
				recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
				recInTemp.setField("CALL_PGM"			    ,"SANGCHA");
				
				//----------------------------------------------------------------------------------
				//	동기화 문제로 인하여 JMS --> EJB Call로 변경
				//	수정자 : 임춘수
				//	수정일 : 1) 2009.12.30 - 최초등록
				//----------------------------------------------------------------------------------
				//ydDelegate.sendMsg(recInTemp);
	
				EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
				szRtnMsg = (String) ejbConn.trx("procCarBayInOrdReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				
				//----------------------------------------------------------------------------------
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 성공 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

				return;
				
			}else{// 일반차량 출하
				/*
				 * 2. 저장품이 적치된 저장위치 정보를 조회 
				 */
				szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//저장품 동 구하기 
				rsGetStock 	= JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP",   "");
				recInTemp.setField("CARD_NO", 		  szCARD_NO);
				recInTemp.setField("TRANS_ORD_DATE" , szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
				
    			intRtnVal = commDao.select(recInTemp, rsGetStock, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0037");			
    			
				if(intRtnVal <= 0){
					return ;
				}
				
				szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 성공 - 건수["+intRtnVal+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsGetStock.first();
				recStlNo = rsGetStock.getRecord();
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recStlNo,"YD_STK_COL_GP");
				szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 대상재가 존재하는 동["+szYD_STK_COL_GP+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				szYD_GP 	= szYD_STK_COL_GP.substring(0, 1); //야드
				szYD_BAY_GP = szYD_STK_COL_GP.substring(1, 2); //동
				szYD_EQP_GP = szYD_STK_COL_GP.substring(2, 4); //Span (01,02,03,  04,05,06,07)
				/*
				 * 4. 위의 저장위치정보를 가지고 차량이 입동가능한 차량정지위치를 조회한다
				 *	- 입동가능한 차량정지위치가 존재하지 않을 시 첫번째 차량정지위치[기본값]를 사용한다.
				 */
				szMsg="[" + szOperationName + "] 출하상차LOT 대상재가 존재하는 차량이 입동가능한 차량정지위치를 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//차량정지위치 구하기
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP.substring(0,2) + "PT");
		    	
		    	/* com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolColGpLike */
		    	intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 8);
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량정지위치를 조회 시 data not found";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if(intRtnVal == -2 ) {
						szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량정지위치를 조회 시 parameter error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량정지위치를 조회 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return ;
				}
				
				szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량이 입동가능한 차량정지위치를 조회 성공 - 건수["+intRtnVal+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szMsg="["+szOperationName+"] 조회된 차량정지위치에 대한 사용가능 체크 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------------------------
				//	신 상차처리 적용여부
	            //  권상시 입동 지시로 
				//------------------------------------------------------------------------------------------------------------
				JDTORecordSet 	outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecordSet 	outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord 		inRecord1 	= JDTORecordFactory.getInstance().create();
				JDTORecord 		inRecord2 	= JDTORecordFactory.getInstance().create();
				JDTORecord 		outRecord  	= JDTORecordFactory.getInstance().create();
				inRecord1.setField("REPR_CD_GP", szYD_GP + "00030"); //1후판 : K00030, 2후판 : T00030
				
				/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
				intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 999);
				if(intRtnVal > 0) {
					outResult.first();
					outRecord  = outResult.getRecord();
					szAPPLY_YN = outRecord.getFieldString("ITEM1");				
				}
				szMsg="신 상차도 처리 적용여부 " + szAPPLY_YN ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//TB_YD_RULE 의 기준값을 사용하여 통로를 결정할 경우
				if(szAPPLY_YN.equals("Y")) {	

					inRecord1 	= JDTORecordFactory.getInstance().create();
					inRecord1.setField("REPR_CD_GP" ,szYD_GP + "00031");	//1후판 : K00031, 2후판 : T00031		
					inRecord1.setField("CD_GP" ,"D");			
			
					/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgt*/
					intRtnVal = ydCarSchDao.getYdCarsch(inRecord1, outResult1, 400);
					
					if (intRtnVal > 0) {
						outResult1.first();
						inRecord2 = outResult1.getRecord();
						
						if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYD_GP)) {
							//1후판 'K' 이면
							intA_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"A_DONG");
							intB_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"B_DONG");
							intC_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"C_DONG");
							intD_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"D_DONG");
							intE_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"E_DONG");
							intF_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"F_DONG");
							intG_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"G_DONG");
						} else {
							//2후판 또는 통합 이후 'T'
							if("01".equals(szYD_EQP_GP) || "02".equals(szYD_EQP_GP) || "03".equals(szYD_EQP_GP)) {
								//01, 02, 03 스판일경우
								intA_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"A_DONG");
								intB_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"B_DONG");
								intC_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"C_DONG");
								intD_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"D_DONG");
								intE_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"E_DONG");
								intF_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"F_DONG");
								intG_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"G_DONG");
							} else {
								//04, 05, 06, 07 스판일경우
								intA_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"A_DONG2");
								intB_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"B_DONG2");
								intC_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"C_DONG2");
								intD_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"D_DONG2");
								intE_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"E_DONG2");
								intF_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"F_DONG2");
								intG_DONG = ydDaoUtils.paraRecChkNullInt(inRecord2,"G_DONG2");
							}
						}
						
						szYD_SAPN_COL_GP = szYD_STK_COL_GP.substring(2, 6); //위 상단 2. 에서 구한 저장품이 있는 적치열에서 스판,열을 구해 아래에서 비교할 때 사용한다. (ex: TE0324 --> 0326 즉 3스판 26열)

						/// 일반야드에 있는 경우 : 혹시 차량위는 제외 
						if(szYD_SAPN_COL_GP.matches("\\d\\d\\d\\d")){
							intYD_SAPN_COL_GP = Integer.parseInt(szYD_STK_COL_GP.substring(2, 6));  // SPAN열정보 
	
							szMsg="["+szOperationName+"] 조회된 차량정지위치에 SPAN열정보" + intYD_SAPN_COL_GP;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
							
							szMsg="["+szOperationName+"] 기준정보 A_DONG:" + intA_DONG + " B_DONG:" + intB_DONG +" C_DONG:" 
							       + intC_DONG + " D_DONG:" + intD_DONG +"  E_DONG:" + intE_DONG +"  F_DONG:" + intF_DONG +"  G_DONG:" + intG_DONG;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

							for( i = 1; i <= rsResult.size(); i++ ) { //rsResult 는 출하상차LOT 대상재가 존재하는 차량이 입동가능한 차량정지위치를 조회한 결과 이다.
								rsResult.absolute(i);
								recInTemp 		= rsResult.getRecord();
								szYD_STK_COL_GP = StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP"), "");   //ex) TEPT11
								szWLOC_CD 		= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");		 //ex) 1E01
								szYD_STK_COL_ACT_STAT = StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_ACT_STAT"), "");  //ex) C
								
								szMsg="["+szOperationName+"] i:" + i ;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
								//차량정지위치의 상태가 'C' 이고 1번지 일때만 가능하다..
								if(( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_INACTIVE) ) && (szYD_STK_COL_GP.substring(5,6).equals("1"))) {
									
									szMsg="["+szOperationName+"] szYD_BAY_GP:" + szYD_BAY_GP + " szYD_STK_COL_GP.substring(1,2):" + szYD_STK_COL_GP.substring(1,2) 
									   +" szYD_STK_COL_GP.substring(4,5):"  + szYD_STK_COL_GP.substring(4,5) + " szYD_STK_COL_GP: " + szYD_STK_COL_GP;
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
									
									if("01".equals(szYD_EQP_GP) || "02".equals(szYD_EQP_GP) || "03".equals(szYD_EQP_GP)) { //01, 02, 03 스판일경우
										
										// 대상동 하구 상차도 동하구 같고
										if(("A".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("A"))) { //A동
										
											if(( intA_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("1"))) {
												isCarArrPoint = true;
												break;
											} else if(( intA_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("2"))) {
												isCarArrPoint = true;
												break;
											}
										} else if(("B".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("B"))) { //B동
											      
											if(( intB_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("1"))) {
												isCarArrPoint = true;
												break;
											} else if(( intB_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("2"))) {
												isCarArrPoint = true;
												break;
											}
										} else if(("C".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("C"))) { //C동
											if(( intC_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("1"))) {
												isCarArrPoint = true;
												break;
											} else if(( intC_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("2"))) {
												isCarArrPoint = true;
												break;
											}
										} else if(("D".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("D"))) { //D동
											if(( intD_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("1"))) {
												isCarArrPoint = true;
												break;
											} else if(( intD_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("2"))) {
												isCarArrPoint = true;
												break;
											}
										} else if(("E".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("E"))) { //E동
											if(( intE_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("1"))) {
												isCarArrPoint = true;
												break;
											} else if(( intE_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("2"))) {
												isCarArrPoint = true;
												break;
											}
										} else if(("F".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("F"))) { //F동
											if(( intF_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("1"))) {
												isCarArrPoint = true;
												break;
											} else if(( intF_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("2"))) {
												isCarArrPoint = true;
												break;
											}
										} else if(("G".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("G"))) { //G동
											if(( intG_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("1"))) {
												isCarArrPoint = true;
												break;
											} else if(( intG_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("2"))) {
												isCarArrPoint = true;
												break;
											}
										}
									} else { //04, 05, 06, 07 스판일경우
										// 대상동 하구 상차도 동하구 같고
										if(("A".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("A"))) { //A동
										
											if(( intA_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("3"))) {
												isCarArrPoint = true;
												break;
											} else if(( intA_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("4"))) {
												isCarArrPoint = true;
												break;
											}
										} else if(("B".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("B"))) { //B동
											      
											if(( intB_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("3"))) {
												isCarArrPoint = true;
												break;
											} else if(( intB_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("4"))) {
												isCarArrPoint = true;
												break;
											}
										} else if(("C".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("C"))) { //C동
											if(( intC_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("3"))) {
												isCarArrPoint = true;
												break;
											} else if(( intC_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("4"))) {
												isCarArrPoint = true;
												break;
											}
										} else if(("D".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("D"))) { //D동
											if(( intD_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("3"))) {
												isCarArrPoint = true;
												break;
											} else if(( intD_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("4"))) {
												isCarArrPoint = true;
												break;
											}
										} else if(("E".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("E"))) { //E동
											if(( intE_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("3"))) {
												isCarArrPoint = true;
												break;
											} else if(( intE_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("4"))) {
												isCarArrPoint = true;
												break;
											}
										} else if(("F".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("F"))) { //F동
											if(( intF_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("3"))) {
												isCarArrPoint = true;
												break;
											} else if(( intF_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("4"))) {
												isCarArrPoint = true;
												break;
											}
										} else if(("G".equals(szYD_BAY_GP)) && (szYD_STK_COL_GP.substring(1,2).equals("G"))) { //G동
											if(( intG_DONG >= intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("3"))) {
												isCarArrPoint = true;
												break;
											} else if(( intG_DONG < intYD_SAPN_COL_GP ) && (szYD_STK_COL_GP.substring(4,5).equals("4"))) {
												isCarArrPoint = true;
												break;
											}
										}
									}
										
										
								}
							}	//szWLOC_CD 이게 필요함
							
						} else {
							szAPPLY_YN = "N";
						}
					} else { // 기준 READ
						szAPPLY_YN = "N";
					}
				} 
				
				//TB_YD_RULE 의 기준값을 사용하여 통로를 결정하지 않을 경우나 위에서  TB_YD_RULE 의 기준값으로 통로를 못 찾았을 경우..
				if(szAPPLY_YN.equals("N")) {	
						
					//1후판제품창고는 04, 05, 06스판이면 3통로, 07스판이면 4통로로 처리한다. 단 A동은 04,05스판이 3, 06,07 스판이 4통로이다.
					//2후판제품창고는 01 스판이면 1통로, 02,03 스판이면 2통로로 처리한다.
					for( i = 1; i <= rsResult.size(); i++ ) {
						//rsResult.first();
						rsResult.absolute(i);
						recInTemp 		= rsResult.getRecord();
						szYD_STK_COL_GP = StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP"), "");
						szWLOC_CD 		= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
						szYD_STK_COL_ACT_STAT = StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_ACT_STAT"), "");
						
						szMsg="["+szOperationName+"] 조회된 출하상차 대상재의 저장위치 스판정보["+szYD_EQP_GP+"]가 04, 05, 06스판이면 3통로로, 07스판이면 4통로로 처리한다. ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]에 대한 야드적치열활성상태["+szYD_STK_COL_ACT_STAT+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_INACTIVE) ) {
							if("A".equals(szYD_BAY_GP)){
								//1후판 A동 일경우 
								if( (szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_04) || szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_05)) && szYD_STK_COL_GP.substring(4,5).equals("3")) {
									isCarArrPoint = true;
									break;
								}else if( (szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_06) || szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_07) || szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_TP)) && szYD_STK_COL_GP.substring(4,5).equals("4")) {
									isCarArrPoint = true;
									break;
								}
							}else{
								//1후판 A동이 아닐 경우
								if( (szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_04) || szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_05)||szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_06)) && szYD_STK_COL_GP.substring(4,5).equals("3")) {
									isCarArrPoint = true;
									break;
								}else if( (szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_07) || szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_TP)) && szYD_STK_COL_GP.substring(4,5).equals("4")) {
									isCarArrPoint = true;
									break;
								}else if ( szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_01) && szYD_STK_COL_GP.substring(4,5).equals("1")) {
									isCarArrPoint = true;
									break;
								}else if ( (szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_02) || szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_03)) && szYD_STK_COL_GP.substring(4,5).equals("2")) {
									isCarArrPoint = true;
									break;
								}							}
						}
					}
				}
				
				
				//위에서 열상태구분(YD_STK_COL_ACT_STAT)이 'C:비활성상태'인  사용가능한  통로를 못 찾았을 경우(ex:모두 사용중일 때) 앞이 차량이 빠지면 작업 할 수 있도록 
				//열상태구분 값을 체크 안하고 통로를 설정한다.
				if( !isCarArrPoint ) {
					
					String szATongRoGp = null;
					String szBTongRoGp = null;
					
					if("T".equals(szYD_GP)) {
						if("01".equals(szYD_EQP_GP) || "02".equals(szYD_EQP_GP) || "03".equals(szYD_EQP_GP)) {
							szATongRoGp = "1";
							szBTongRoGp = "2";
						} else {
							szATongRoGp = "3";
							szBTongRoGp = "4";
						}
					} else {
						szATongRoGp = "3";
						szBTongRoGp = "4";
					}
					
					//이 경우에도 TB_YD_RULE 의 기준값을 사용하여 통로를 결정할 경우와 그렇지 않을 경우로 나눠서 처리 한다.
					if(szAPPLY_YN.equals("Y")) {	
						

						
						// 디폴트 처리 하는거 // 입동대기
						if("A".equals(szYD_BAY_GP)) {
						
							if(  intYD_SAPN_COL_GP <= intA_DONG) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szATongRoGp+"1"; 
							} else {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szBTongRoGp+"1"; 
							}
						} else if("B".equals(szYD_BAY_GP)) {
							
							if(  intYD_SAPN_COL_GP <= intB_DONG) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szATongRoGp+"1"; 
							} else {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szBTongRoGp+"1"; 
							}
						} else if("C".equals(szYD_BAY_GP)) {
							
							if(  intYD_SAPN_COL_GP <= intC_DONG) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szATongRoGp+"1"; 
							} else {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szBTongRoGp+"1"; 
							}
						} else if("D".equals(szYD_BAY_GP)) {
							
							if(  intYD_SAPN_COL_GP <= intD_DONG) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szATongRoGp+"1"; 
							} else {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szBTongRoGp+"1"; 
							}
						} else if("E".equals(szYD_BAY_GP)) {
							
							if(  intYD_SAPN_COL_GP <= intE_DONG) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szATongRoGp+"1"; 
							} else {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szBTongRoGp+"1"; 
							}
						} else if("F".equals(szYD_BAY_GP)) {
							
							if(  intYD_SAPN_COL_GP <= intF_DONG) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szATongRoGp+"1"; 
							} else {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szBTongRoGp+"1"; 
							}
						} else if("G".equals(szYD_BAY_GP)) {
							
							if(  intYD_SAPN_COL_GP <= intG_DONG) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szATongRoGp+"1"; 
							} else {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szBTongRoGp+"1"; 
							}
						}	
						
						szMsg="["+szOperationName+"] 사용가능한 차량정지위치가 없으므로 기본값["+szYD_STK_COL_GP+"]을 사용";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
					} else {
						//1후판제품창고는 04, 05, 06스판이면 A통로, 07스판이면 B통로로 처리한다. 단 A동은 04,05스판이 A, 06,07 스판이 B통로이다.
						//2후판제품창고는 01 스판이면 A통로, 02,03 스판이면 B통로로 처리한다.
						if("A".equals(szYD_BAY_GP)){
							if( szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_04) || 
								szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_05)) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szATongRoGp+"1";
							}else if( szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_06) || 
									  szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_07) || 
									  szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szBTongRoGp+"1";
							}
						}else{
							if( 
								szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_01) ||
								szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_04) || 
								szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_05) || 
								szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_06) ) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szATongRoGp+"1";
							}else if( 
									  szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_02) || 
									  szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_03) || 
									  szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_07) || 
									  szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
								szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT"+szBTongRoGp+"1";
							}
						}
						
						szMsg="["+szOperationName+"] 사용가능한 차량정지위치가 없으므로 기본값["+szYD_STK_COL_GP+"]을 사용";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}	
				}
				
				//위 복잡한 if 문 구분 들은 결과적으로 아래 szYD_STK_COL_GP 를 구하기 위한 것이다. (ex: TEPT11, TCPT21..)
				szMsg="["+szOperationName+"] 조회된 차량정지위치에 대한 사용가능 체크 완료 - 사용가능한 차량정지위치["+szYD_STK_COL_GP+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//-----------------------------------------------------------------------------------------------------------------
				//	사용가능한 차량정지위치로 적치열 조회 후 개소POINT를 구함
				//-----------------------------------------------------------------------------------------------------------------
				
				szMsg="["+szOperationName+"] 사용가능한 차량정지위치["+szYD_STK_COL_GP+"]로 적치열 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP",			szYD_STK_COL_GP);
				
				szRtnMsg = DaoManager.getYdStkcol(recInTemp, rsResult, 0);
				
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					szMsg="["+szOperationName+"] 사용가능한 차량정지위치["+szYD_STK_COL_GP+"]로 적치열 조회 시 오류발생 - 메세지 : " + szRtnMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
				
				rsResult.first();
				recInTemp		= rsResult.getRecord();
				
				szWLOC_CD		= ydDaoUtils.paraRecChkNull(recInTemp,"WLOC_CD"); //2013.11.14 복수 창고 출하시 개소코드 변경하기 위하여 추가
				szYD_PNT_CD		= ydDaoUtils.paraRecChkNull(recInTemp,"YD_PNT_CD");
				
				szMsg="["+szOperationName+"] 사용가능한 차량정지위치["+szYD_STK_COL_GP+"]로 적치열 조회 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			//-----------------------------------------------------------------------------------------------------------------
			
			if("Y".equals(szDOUBLEDONG_CHECK)){ //복수동일 경우
				/*
				 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 초기화 수정한다.
				 */
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    		szDOUBLEDONG_YD_CAR_SCH_ID);
				recInTemp.setField("REGISTER",         		szRcvTcCode);
				recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
				recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
				recInTemp.setField("SPOS_WLOC_CD",     		szWLOC_CD);								//발지개소코드
				recInTemp.setField("YD_PNT_CD1",     		szYD_PNT_CD);							//야드포인트코드1
				recInTemp.setField("CAR_NO",           		szCAR_NO);								//차량번호
				recInTemp.setField("CARD_NO",          		szCARD_NO);								//카드번호
				recInTemp.setField("YD_CARLD_LEV_DT",  		YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
		    	recInTemp.setField("YD_CARLD_STOP_LOC",		szYD_STK_COL_GP);						//차량상차정지위치
		    	recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
		    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		"2");									//입동지시순번  (2013.06.20 cho 1순위 --> 2순위 변경)
				recInTemp.setField("DEL_YN", 				"N");					
				
				//차량스케줄수정
		    	intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
		    	
	    		if( intRtnVal <= 0 ){
					szMsg="[" + szOperationName + "] 차량스케줄 수정 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
	    		}
	    		
	    		//차량 스케쥴재료수정
	    		YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao(); 
	        	intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(recInTemp, 1) ;
	            
	    		szMsg="[" + szOperationName + "] 차량스케줄 수정 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			} else { //복수동이 아닐 경우 

					/*
					 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
					 */
					szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID",    		szYD_CAR_SCH_ID);
					recInTemp.setField("REGISTER",         		szRcvTcCode);
					recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
					recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
					recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
					recInTemp.setField("CAR_KIND", 				"TR");
					recInTemp.setField("SPOS_WLOC_CD",     		szWLOC_CD);								//발지개소코드
					recInTemp.setField("YD_PNT_CD1",     		szYD_PNT_CD);							//야드포인트코드1
					recInTemp.setField("CAR_NO",           		szCAR_NO);								//차량번호
					recInTemp.setField("CARD_NO",          		szCARD_NO);								//카드번호
					recInTemp.setField("YD_CARLD_LEV_DT",  		YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
					recInTemp.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
					recInTemp.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
			    	recInTemp.setField("YD_CARLD_STOP_LOC",		szYD_STK_COL_GP);						//차량상차정지위치
			    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 --기본값으로 설정(9) **
					recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
					recInTemp.setField("IF_SEQ_NO", 			szUNIQUE_ID);	// 운송지시 SEQ
		    		//차량스케줄 등록
			    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
		    		if( intRtnVal <= 0 ){
						szMsg="[" + szOperationName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
		    		}
		    		
		    		szMsg="[" + szOperationName + "] 차량스케줄 생성 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}

				/*
				 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
				 */
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("TC_CODE"				,"YDYDJ633");
				recInTemp.setField("TC_CREATE_DDTT"			,YdUtils.getCurDate("yyyyMMddHHmmss"));
				recInTemp.setField("YD_CAR_STOP_LOC"		,szYD_STK_COL_GP);
				recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
				recInTemp.setField("CALL_PGM"			    ,"SANGCHA");
				
				//----------------------------------------------------------------------------------
				//	동기화 문제로 인하여 JMS --> EJB Call로 변경
				//	수정자 : 임춘수
				//	수정일 : 1) 2009.12.30 - 최초등록
				//----------------------------------------------------------------------------------
				//ydDelegate.sendMsg(recInTemp);
	
				EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
				szRtnMsg = (String) ejbConn.trx("procCarBayInOrdReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				
				//----------------------------------------------------------------------------------
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 성공 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		}catch(Exception e){

			szMsg="[후판제품운송상차지시] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch
		
		szMsg="[후판제품운송상차지시]수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procPlateGdsTrnOrd3G()
	
	/**
	 * 후판제품선별LOT편성정보(DMYDR046)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlateGdsTrnOrdLot(JDTORecord inRecord)throws JDTOException  {
		/*
		 * 업무기준 	: 
		 * 수정자 	: 
		 * 수정일 	: 
		 */
		//DAO 정의
		YdStockDao ydStockDao = new YdStockDao();									//저장품DAO
		YdStkBedDao ydStkBedDao = new YdStkBedDao();								//적치베드DAO
		YdPlateCommDAO commDao 		= new YdPlateCommDAO(); //3기 후판제품공용dao
		//JDTO 정의
		JDTORecord	recEditColumn	= JDTORecordFactory.getInstance().create();
		JDTORecord  recInTemp		= null;
		//기본 변수 정의
		String szMethodName 		= "procPlateGdsTrnOrdLot";
		String szOperationName		= "재광제품선별LOT편성정보(DMYDR046)";
		String szMsg 				= "";
		//로컬에서 사용되는 변수 정의
		String szSTL_NO 			= "";
		String szCarLotId			= null;
		String szCarLotGbn			= null;
		String szTRANS_ORD_DATE		= null;
		String szTRANS_ORD_SEQNO	= null;
		
		int intYD_EQP_WRK_SH		= 0;
		int intRtnVal 				= 0;
		int	i						= 0;
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){
			szMsg="[" + szOperationName + "] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 후판제품선별LOT편성정보 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP	재료외형구분
			TRANS_ORD_DT	운송지시일자
			TRANS_ORD_SEQNO	운송지시순번
			YD_EQP_WRK_SH	야드설비작업매수
			STL_NO1			재료번호1
			STL_NO2			재료번호2
			STL_NO3			재료번호3
			STL_NO4			재료번호4
			STL_NO5			재료번호5
			STL_NO6			재료번호6
			STL_NO7			재료번호7
			STL_NO8			재료번호8
			STL_NO9			재료번호9
			STL_NO10		재료번호10
			STL_NO11		재료번호11
			STL_NO12		재료번호12
			STL_NO13		재료번호13
			STL_NO14		재료번호14
			STL_NO15		재료번호15
			STL_NO16		재료번호16
			STL_NO17		재료번호17
			STL_NO18		재료번호18
			STL_NO19		재료번호19
			STL_NO20		재료번호20
			LOT_NO			선별LOTID
			LOT_GBN			1:수동선별LOT편성,2,선별LOT편성 취소
			*/
			
			//****************************************************************************************************
			szTRANS_ORD_DATE 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT");
			szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO");
			intYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNullInt(inRecord,"YD_EQP_WRK_SH");
			szCarLotId 			= ydDaoUtils.paraRecChkNull(inRecord,"LOT_NO");
			szCarLotGbn			= ydDaoUtils.paraRecChkNull(inRecord,"LOT_GBN");

			if("".equals(szCarLotId)){
				szMsg="[" + szOperationName + "] LOT_NO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			if("".equals(szCarLotGbn)){
				szMsg="[" + szOperationName + "] LOT_GBN IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} 
			
			recEditColumn 	= JDTORecordFactory.getInstance().create();
			recEditColumn.setField("TRANS_ORD_DATE", 		szTRANS_ORD_DATE);
			recEditColumn.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
			recEditColumn.setField("CAR_LOTID", 			szCarLotId);
			recEditColumn.setField("MODIFIER", 				"DMYDR046");  
			
			szMsg="[" + szOperationName + "]["+intYD_EQP_WRK_SH+"]매수에 대한  저장품 업데이트 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			for(i = 1 ; i<= intYD_EQP_WRK_SH; i++){
				
				szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i);
				
				if(szSTL_NO.equals("")){
					break;
				}else {
					inRecord.setField("STL_NO", 				szSTL_NO);
				}
				
				recEditColumn.setField("STL_NO", 				szSTL_NO);
				
				if("1".equals(szCarLotGbn)){
					recEditColumn.setField("CAR_LOTID", szCarLotId);
				}else if("2".equals(szCarLotGbn)){
					recEditColumn.setField("CAR_LOTID", "");
				}
				
				
//				2021. 06. 03 주석처리 Update컬럼을 최소화한다.
//				intRtnVal = ydStockDao.updYdStock(recEditColumn, 0);
//				if(intRtnVal <= 0){
//					szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 업데이트 시 오류발생 - 반환값 : " +intRtnVal;
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return ;
//				}
				
		        // 2021. 06. 03
				// Update컬럼을 최소화한다.
				recEditColumn.setField("QUERY_ID", "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updYdStockByDMYDR060");

				// 2021. 06. 03 트랜잭션 분리를 위한 EJb Bean설정
				EJBConnector 	ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
				ejbConn.trx("updYdStkColTX", new Class[] { JDTORecord.class }, new Object[] { recEditColumn });	
				szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 TB_YD_STOCK 업데이트 완료 : ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				// 2021. 06. 03
				// 출하에서 야드에->Lot편성, 진행관리->운송대기 두 개 전문을 동시 전송 후 처리하는 과정에서 
				// TB_PT_PLATECOMM 테이블의 진도코드가 야드L2전송 전송 이후에 바뀌는 문제가 있어 하드코딩으로 처리한다.
				// 취소일경우엔 진도코드를
				recInTemp  = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID"         , "YDY8L002");
				recInTemp.setField("YD_INFO_SYNC_CD", "A");							
		    	recInTemp.setField("STL_NO"         , szSTL_NO);
		    	recInTemp.setField("YD_STK_COL_GP"  , YdConstant.YD_GP_PLATE2_GDS_YARD);
				recInTemp.setField("CURR_PROG_CD"         , YdConstant.PROG_CD_TRN_WAIT); // 운송대기(L)로 강제로 셋팅한다.
				if("2".equals(szCarLotGbn)){
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
			
			if("1".equals(szCarLotGbn)){
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
			params.setField("TRANS_ORD_DATE"           	,szTRANS_ORD_DATE);
			params.setField("TRANS_ORD_SEQNO"         	,szTRANS_ORD_SEQNO);
			if(commDao.select(params, rsBedRecord, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getDMYDR060_SendToL2List") > 0){
				for(int idx=0; idx<rsBedRecord.size();idx++){
					JDTORecord jtoYDY89L001 = JDTORecordFactory.getInstance().create();
					jtoYDY89L001.setField("YD_INFO_SYNC_CD",  "4"); // BED까지(4)
					jtoYDY89L001.setField("YD_GP"          , YdConstant.YD_GP_PLATE2_GDS_YARD);
					jtoYDY89L001.setField("YD_STK_COL_GP", 	rsBedRecord.getRecord(idx).getFieldString("YD_STK_COL_GP"));
					jtoYDY89L001.setField("YD_STK_BED_NO", 	rsBedRecord.getRecord(idx).getFieldString("YD_STK_BED_NO"));
					YdCommonUtils.sndStrPosSpecToL2(jtoYDY89L001);
				}
			}
		}catch(Exception e){

			szMsg="[후판제품선별LOT편성정보] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch
		
		szMsg="[후판제품선별LOT편성정보]수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procPlateGdsTrnOrdLot()
	
	/**
	 * 후판제품해송선별LOT편성정보(DMYDR048)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlateGdsShptrTrnOrdLot(JDTORecord inRecord)throws JDTOException  {
		/*
		 * 업무기준 	: 
		 * 수정자 	: 
		 * 수정일 	: 
		 */
		//DAO 정의
		//YdStockDao ydStockDao = new YdStockDao();									//저장품DAO
		YdStkBedDao ydStkBedDao = new YdStkBedDao();								//적치베드DAO
		
		YdPlateCommDAO	commDao 		= new YdPlateCommDAO();

		
		//JDTO 정의
		//JDTORecord	recEditColumn	= null;
		//기본 변수 정의
		String szMethodName 		= "procPlateGdsShptrTrnOrdLot";
		String szOperationName		= "후판제품해송선별LOT편성정보(DMYDR048)";
		String szMsg 				= "";
		//로컬에서 사용되는 변수 정의
		String szSTL_NO 			= "";
		String szCarLotId			= null; //선별LOT번호
		String szCarLotGbn			= null; //작업구분 1:수동선별LOT편성, 2:수동선별LOT편성취소
		String szTRANS_ORD_DATE		= null; //운송지시일자
		String szTRANS_ORD_SEQNO	= null; //운송지시순번
		String szCARLD_PNT_CD		= null; //상차포인트 (실제 차량포인트 - TB_YD_CARPONT 의 YD_CARPNT_CD 값)
		String szCAR_KIND			= null; //차량종류 
		String szYD_STK_COL_GP		= null; //야드적치열구분
		String szCAR_NO				= null; //차량번호
		String szCARD_NO			= null; //CARD_NO
		
		int intYD_EQP_WRK_SH		= 0;
		int intRtnVal 				= 0;
		int	i						= 0;
		
		JDTORecordSet	rsResult		= null;
		JDTORecord		recInTemp		= null;
		JDTORecord      sendMsg_L002 = null;
		JDTORecord      recPara2  = JDTORecordFactory.getInstance().create();
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){
			szMsg="[" + szOperationName + "] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 후판제품해송선별LOT편성정보 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//수신한 전문값******************************************************************************************
			/*
			YD_GP			야드구분
			STL_APPEAR_GP	재료외형구분
			TRANS_ORD_DT	운송지시일자
			TRANS_ORD_SEQNO	운송지시순번
			YD_EQP_WRK_SH	야드설비작업매수
			CAR_NO			차량번호
			CAR_KIND		차량종류
			CARD_NO			카드번호
			WORK_GP			작업구분
			CARLD_PNT_CD	상차포인트(차량포인트)
			PLBAY_PNT_YN	복수포인트유무
			STL_NO1			재료번호1
			STL_NO2			재료번호2
			STL_NO3			재료번호3
			STL_NO4			재료번호4
			STL_NO5			재료번호5
			STL_NO6			재료번호6
			STL_NO7			재료번호7
			STL_NO8			재료번호8
			STL_NO9			재료번호9
			STL_NO10		재료번호10
			STL_NO11		재료번호11
			STL_NO12		재료번호12
			STL_NO13		재료번호13
			STL_NO14		재료번호14
			STL_NO15		재료번호15
			STL_NO16		재료번호16
			STL_NO17		재료번호17
			STL_NO18		재료번호18
			STL_NO19		재료번호19
			STL_NO20		재료번호20
			LOT_NO			선별LOTID
			LOT_GBN			1:수동선별LOT편성,2,선별LOT편성 취소
			*/
			
			//****************************************************************************************************
			szTRANS_ORD_DATE 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT");
			szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO");
			intYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNullInt(inRecord,"YD_EQP_WRK_SH");
			szCarLotId 			= ydDaoUtils.paraRecChkNull(inRecord,"LOT_NO");
			szCarLotGbn			= ydDaoUtils.paraRecChkNull(inRecord,"LOT_GBN");
			szCARLD_PNT_CD		= ydDaoUtils.paraRecChkNull(inRecord,"CARLD_PNT_CD");
			szCAR_KIND			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_KIND");
			szCAR_NO			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO");
			szCARD_NO			= ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO");

			if("".equals(szCarLotId)){
				szMsg="[" + szOperationName + "] LOT_NO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}

			if("".equals(szCarLotGbn)){
				szMsg="[" + szOperationName + "] LOT_GBN IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} 
			
			if("".equals(szCARLD_PNT_CD)){
				szMsg="[" + szOperationName + "] CARLD_PNT_CD IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} 			
			
			if("".equals(szCAR_KIND)){
				szMsg="[" + szOperationName + "] CAR_KIND IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} 					
			
			
			                  
			recPara2.setField("TRANS_ORD_DATE", 		szTRANS_ORD_DATE);
			recPara2.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
			recPara2.setField("CAR_LOTID", 			szCarLotId);
			recPara2.setField("CAR_NO", 				szCAR_NO);
			recPara2.setField("CARD_NO", 				szCARD_NO);
			recPara2.setField("MODIFIER", 				"DMYDR048");  
			
			
			szMsg="[" + szOperationName + "]["+intYD_EQP_WRK_SH+"]매수에 대한  저장품 업데이트 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			for(i = 1 ; i<= intYD_EQP_WRK_SH; i++){
				
				szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i);
				
				if(szSTL_NO.equals("")){
					break;
				}
				
				recPara2.setField("STL_NO", 				szSTL_NO);
				
				if("1".equals(szCarLotGbn)){
					recPara2.setField("CAR_LOTID", szCarLotId);
				}else if("2".equals(szCarLotGbn)){
					recPara2.setField("CAR_LOTID", "");
				}
				
				
				
		        // 2021. 07. 06
				recPara2.setField("QUERY_ID", "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0049");

				// 2021. 07. 06 트랜잭션 분리를 위한 EJb Bean설정
				EJBConnector 	ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
				ejbConn.trx("updYdStkColTX", new Class[] { JDTORecord.class }, new Object[] { recPara2 });	
				szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 TB_YD_STOCK 업데이트 완료 : ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
//				intRtnVal = commDao.update(recPara2, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0049");	
//				if(intRtnVal <= 0){
//					szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 업데이트 시 오류발생 - 반환값 : " +intRtnVal;
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return ;
//				}
				
				// 2021. 07. 06
				// 출하에서 야드에->Lot편성, 진행관리->운송대기 두 개 전문을 동시 전송 후 처리하는 과정에서 
				// TB_PT_PLATECOMM 테이블의 진도코드가 야드L2전송 전송 이후에 바뀌는 문제가 있어 하드코딩으로 처리한다.
				// 취소일경우엔 진도코드를
				sendMsg_L002  = JDTORecordFactory.getInstance().create();
				sendMsg_L002.setField("MSG_ID"         , "YDY8L002");
				sendMsg_L002.setField("YD_INFO_SYNC_CD", "A");							
				sendMsg_L002.setField("STL_NO"         , szSTL_NO);
				sendMsg_L002.setField("YD_STK_COL_GP"  , YdConstant.YD_GP_PLATE2_GDS_YARD);
				sendMsg_L002.setField("CURR_PROG_CD"   , YdConstant.PROG_CD_TRN_WAIT); // 운송대기(L)로 강제로 셋팅한다.

				
				if("2".equals(szCarLotGbn)){
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
			

			if("1".equals(szCarLotGbn)){
				intRtnVal = ydStkBedDao.updYdStkBedStat_01(recPara2);				
			}

 			//-------------------------------------------------------------------------------------------------------------------
			szYD_STK_COL_GP = szCARLD_PNT_CD.substring(0,1) + szCARLD_PNT_CD.substring(2,3) + "PT" + szCARLD_PNT_CD.substring(1,2) + szCARLD_PNT_CD.substring(3,4); 
			
			if("1".equals(szCarLotGbn)){
				
				//차량포인트 "예정중"으로 변경하기
				if(!"PT".equals(szCAR_KIND)&&szCARLD_PNT_CD.endsWith("2") ) {
					//차량종류가 PT가 아니고 차량포인트가 2번포인트인경우는 3번 포인트까지 예약을 건다.
					
					recInTemp = JDTORecordFactory.getInstance().create();
					//그외는 해당 포인트만 예약을 건다.
			    	recInTemp.setField("YD_CARPNT_CD", szCARLD_PNT_CD);
			    	recInTemp.setField("YD_STK_COL_ACT_STAT", "R");					
					
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
					
			    	recInTemp.setField("YD_CARPNT_CD", szCARLD_PNT_CD.substring(0,3)+"3");
			    	recInTemp.setField("YD_STK_COL_ACT_STAT", "N");					
					
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
					
					
				} else {
					
					recInTemp = JDTORecordFactory.getInstance().create();
					//그외는 해당 포인트만 예약을 건다.
			    	recInTemp.setField("YD_CARPNT_CD", szCARLD_PNT_CD);
			    	recInTemp.setField("YD_STK_COL_ACT_STAT", "R");					
					
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
					
				}
			} else if("2".equals(szCarLotGbn)) {
				//취소일경우
				if(!"PT".equals(szCAR_KIND)&&szCARLD_PNT_CD.endsWith("2") ) {
					//차량종류가 PT가 아니고 차량포인트가 2번포인트인경우는 3번 포인트까지 예약을 건다.
					
					recInTemp = JDTORecordFactory.getInstance().create();
					//그외는 해당 포인트만 예약을 건다.
			    	recInTemp.setField("YD_CARPNT_CD", szCARLD_PNT_CD);
			    	recInTemp.setField("YD_STK_COL_ACT_STAT", "C");					
					
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
					
			    	recInTemp.setField("YD_CARPNT_CD", szCARLD_PNT_CD.substring(0,3)+"3");
			    	recInTemp.setField("YD_STK_COL_ACT_STAT", "C");					
					
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
					
					
				} else {
					
					recInTemp = JDTORecordFactory.getInstance().create();
					//그외는 해당 포인트만 예약을 건다.
			    	recInTemp.setField("YD_CARPNT_CD", szCARLD_PNT_CD);
			    	recInTemp.setField("YD_STK_COL_ACT_STAT", "C");					
					
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
					
				}				
			}
			//-------------------------------------------------------------------------------------------------------------------			
			// 전사물류개선 2021. 4. 3
			// 지시확정이 변경된 BED정보를 전송한다.
			JDTORecord params = JDTORecordFactory.getInstance().create();
			JDTORecordSet rsBedRecord 	= JDTORecordFactory.getInstance().createRecordSet("ydPlate");
			params.setField("TRANS_ORD_DATE"           	,szTRANS_ORD_DATE);
			params.setField("TRANS_ORD_SEQNO"         	,szTRANS_ORD_SEQNO);
			if(commDao.select(params, rsBedRecord, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getDMYDR060_SendToL2List") > 0){
				for(int idx=0; idx<rsBedRecord.size();idx++){
					JDTORecord jtoYDY89L001 = JDTORecordFactory.getInstance().create();
					jtoYDY89L001.setField("YD_INFO_SYNC_CD",  "4"); // BED까지(4)
					jtoYDY89L001.setField("YD_GP"          , YdConstant.YD_GP_PLATE2_GDS_YARD);
					jtoYDY89L001.setField("YD_STK_COL_GP", 	rsBedRecord.getRecord(idx).getFieldString("YD_STK_COL_GP"));
					jtoYDY89L001.setField("YD_STK_BED_NO", 	rsBedRecord.getRecord(idx).getFieldString("YD_STK_BED_NO"));
					YdCommonUtils.sndStrPosSpecToL2(jtoYDY89L001);
				}
			}
			
			
		}catch(Exception e){

			szMsg="[후판제품해송선별LOT편성정보] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch
		
		szMsg="[후판제품해송선별LOT편성정보]수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procPlateGdsShptrTrnOrdLot()	
	
	/**
	 * 후판제품해송적하그룹편성정보(DMYDR049)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlateGdsShpudGrpGpInfo(JDTORecord inRecord)throws JDTOException  {
		/*
		 * 업무기준 	: 
		 * 수정자 	: 
		 * 수정일 	: 
		 */
		//DAO 정의
		YdStockDao ydStockDao = new YdStockDao();									//저장품DAO
		YdStkBedDao ydStkBedDao = new YdStkBedDao();								//적치베드DAO
		
		YdPlateCommDAO commDao = new YdPlateCommDAO(); //3기 후판제품공용dao

		
		//JDTO 정의
		JDTORecord	recEditColumn	= JDTORecordFactory.getInstance().create();
		//기본 변수 정의
		String szMethodName 		= "procPlateGdsShpudGrpGpInfo";
		String szOperationName		= "후판제품해송적하그룹편성정보(DMYDR049)";
		String szMsg 				= "";
		//로컬에서 사용되는 변수 정의
		String szTRANSMIT_DATE		= null;
		String szSEND_SEQ			= null;
		String szWORK_GP			= null;
		
		int intRtnVal 				= 0;
		int	i						= 0;
		
		JDTORecordSet	rsResult		= null;
		JDTORecord		recInTemp		= null;
		JDTORecord		recOutTemp		= null;
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){
			szMsg="[" + szOperationName + "] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 후후판제품해송적하그룹편성정보 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//수신한 전문값******************************************************************************************
			/*
			TRANSMIT_DATE	전송일자
			SEND_SEQ		전송전문
			WORK_GP         작업구분 (A:등록, U:수정, C:취소)
			*/
			
			//****************************************************************************************************
			szTRANSMIT_DATE 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANSMIT_DATE");
			szSEND_SEQ 			= ydDaoUtils.paraRecChkNull(inRecord,"SEND_SEQ");
			szWORK_GP			= ydDaoUtils.paraRecChkNull(inRecord,"WORK_GP");
			
			
			if("".equals(szTRANSMIT_DATE)){
				szMsg="[" + szOperationName + "] TRANSMIT_DATE IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}

			if("".equals(szSEND_SEQ)){
				szMsg="[" + szOperationName + "] SEND_SEQ IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} 
			
			if("".equals(szWORK_GP)){
				szMsg="[" + szOperationName + "] WORK_GP IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} 
			
			// New 버젼.. Merge 를 이용하여 한번에 Update 를 수행한다.
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("TRANSMIT_DATE", 	szTRANSMIT_DATE);	
			recInTemp.setField("SEND_SEQ",			szSEND_SEQ);	
			recInTemp.setField("MODIFIER", 			"DMYDR049");
			
			//PIDEV			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "후판제품해송적하그룹편성정보(DMYDR049)", "APPPI0", "T", "*");
//			if("Y".equals(sApplyYnPI)) {
				
				if("C".equals(szWORK_GP)) {
					//취소일 경우 TB_YD_STKBED_PLATEINFO 의 해당 정보를 DELETE 한다.
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0046_PIDEV");
					
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0054_PIDEV");
					
				} else {
				
					//TB_YD_STOCK UPDATE
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0043_PIDEV");
				}
				
				//TB_DM_SHPUDGRPINFOIFTEMP@DL_SMDB 의 PROCESS_FLAG = 'Y' 로 UPDATE
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0045_PIDEV");
				
//			} else {
//				if("C".equals(szWORK_GP)) {
//					//취소일 경우 TB_YD_STKBED_PLATEINFO 의 해당 정보를 DELETE 한다.
//					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0046");
//					
//					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0054");
//					
//				} else {
//				
//					//TB_YD_STOCK UPDATE
//					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0043");
//				}
//				
//				//TB_DM_SHPUDGRPINFOIFTEMP@DL_SMDB 의 PROCESS_FLAG = 'Y' 로 UPDATE
//				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0045");
//	
//			}
		
		}catch(Exception e){

			szMsg="[후판제품해송적하그룹편성정보] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch
		
		szMsg="[후판제품해송적하그룹편성정보]수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procPlateGdsShpudGrpGpInfo()	
	
	/**
	 * 외판슬라브제품운송상차지시(DMYDR022) - L3
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procSlabGdsTrnOrd(JDTORecord inRecord)throws JDTOException  {
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		
		JDTORecord	recEditColumn	= JDTORecordFactory.getInstance().create();
		String szMethodName 		= "procSlabGdsTrnOrd";
		String szMsg 				= "";
		String szOperationName      = "외판슬라브제품운송상차지시";
		String szSTL_NO 			= "";
		int intRtnVal 				= 0;
		int	i						= 0;

		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 외판슬라브제품운송상차지시 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP	재료외형구분
			TRANS_ORD_DT	운송지시일자
			TRANS_ORD_SEQNO	운송지시순번
			CAR_NO			차량번호			추가
			CARD_NO			카드번호			추가
			YD_EQP_WRK_SH	야드설비작업매수
			STL_NO1			재료번호1
			STL_NO2			재료번호2
			STL_NO3			재료번호3
			STL_NO4			재료번호4
			STL_NO5			재료번호5
			STL_NO6			재료번호6
			STL_NO7			재료번호7
			STL_NO8			재료번호8
			STL_NO9			재료번호9
			STL_NO10		재료번호10
			STL_NO11		재료번호11
			STL_NO12		재료번호12
			STL_NO13		재료번호13
			STL_NO14		재료번호14
			STL_NO15		재료번호15
			STL_NO16		재료번호16
			STL_NO17		재료번호17
			STL_NO18		재료번호18
			STL_NO19		재료번호19
			STL_NO20		재료번호20
			*/
			
			//****************************************************************************************************
			
			//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
				recEditColumn 	= JDTORecordFactory.getInstance().create();
				recEditColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
				recEditColumn.setField("TRANS_ORD_DATE", 		ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
				recEditColumn.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
				recEditColumn.setField("CAR_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO"));
				recEditColumn.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO"));
				recEditColumn.setField("MODIFIER", 				"DMYDR022");
				
			for(i = 1 ; i<=20; i++){
				recEditColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i));
				inRecord.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i));
				
				//STL_NO가 없다면 loop종료
				szSTL_NO = recEditColumn.getFieldString("STL_NO");
				if(szSTL_NO.equals("")){
					break;
				}				

				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal= YdCommonUtils.getYdAimRtGp("S",inRecord );		
				recEditColumn.setField("YD_AIM_RT_GP", rVal[0]);
				recEditColumn.setField("STL_PROG_CD", rVal[1]);
					
					
				//저장품갱신******************************************************************************************** 
				intRtnVal = ydStockDao.updYdStock(recEditColumn, 0);
				if(intRtnVal <= 0){
					szMsg= "YD_STOCK[외판슬라브운송상차지시] UPDATE Error :: [" + intRtnVal + "]" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[외판슬라브운송상차지시] UPDATE Success",3);
				//****************************************************************************************************


			} //end of for *******************************************************************************************
			
		}catch(Exception e){

			szMsg="[외판슬라브운송상차지시] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch
		
		szMsg="[외판슬라브운송상차지시]수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procSlabGdsTrnOrd()
	
	/**
	 * 슬라브이송지시 (PMYDJ002) 타임 아웃 시간 지정: 10분 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @weblogic.transaction-descriptor trans-timeout-seconds="600"
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procSlavFtmvOrd(JDTORecord inRecord)throws JDTOException  {
		
		// DAO 및 UTIL 객체 생성
		YdStockDao ydStockDao                   = new YdStockDao();
		YdCodeMapping ydCodeMapping             = new YdCodeMapping();
		
		// 변수선언
		String szMethodName                     = "procSlavFtmvOrd";
		String szOperationName                  = "슬라브이송지시(PMYDJ002)";
		String szMsg                            = "";
		
		// 레코드 선언
		JDTORecordSet rsResult 				    = null;
		JDTORecord recPara  			        = null;
		JDTORecord recGetValMSlab		        = null;
		
		int nRet                                = 0;
		
		String szRcvTcCode 						= "PMYDJ002";
		
		try{	
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[공정계획] 슬라브이송지시 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			String sFrDate  	= ydDaoUtils.paraRecChkNull(inRecord, "FRTOMOVE_WORD_DATE1");
			String sFrSeqno  	= ydDaoUtils.paraRecChkNull(inRecord, "TRANSWORD_SEQNO1");
			String sFrStatCd	= ydDaoUtils.paraRecChkNull(inRecord, "FRTOMOVE_STAT_CD1");
			
			szMsg = "[" + szOperationName + "] 메소드 시작 - 이송지시전문내용확인(FRTOMOVE_WORD_DATE1)" + sFrDate +"\n" 
				  + "[" + szOperationName + "] 메소드 시작 - 이송지시전문내용확인(TRANSWORD_SEQNO1)" + sFrSeqno +"\n"
				  + "[" + szOperationName + "] 메소드 시작 - 이송지시전문내용확인(FRTOMOVE_STAT_CD1)" + sFrStatCd +"\n";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("FRTOMOVE_WORD_DATE1", sFrDate);
			recPara.setField("TRANSWORD_SEQNO1", sFrSeqno);
			recPara.setField("FRTOMOVE_STAT_CD1", sFrStatCd);
			
			nRet = ydStockDao.getYdStock(recPara, rsResult, 220);
			if(nRet <= 0){
				szMsg = "[슬라브이송지시] 이송대상 존재안함  nRet(" + nRet + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			}
			
			String sStlNo 			= "";
			String sTranswordSeqno 	= "";
			String sFrtomoveStatCd 	= "";
			String sFrtomoveStatCdYd= "";
			
			//=========================================================================================================
			// 주편에서 조회된 N건 수 만큼 반복 (index는 0에서부터 처리 함, 레코드는 1부터가 시작)
			//=========================================================================================================
			for(int nIdx=0; nIdx<rsResult.size(); nIdx++){
				
				rsResult.absolute(nIdx+1);
				recGetValMSlab = rsResult.getRecord();
				
				sStlNo 			= ydDaoUtils.paraRecChkNull(recGetValMSlab, "STL_NO");
				sTranswordSeqno = ydDaoUtils.paraRecChkNull(recGetValMSlab, "TRANSWORD_SEQNO");
				sFrtomoveStatCd = ydDaoUtils.paraRecChkNull(recGetValMSlab, "FRTOMOVE_STAT_CD");
				
				if("1".equals(sFrStatCd)){
					sFrtomoveStatCdYd = "3";
				}else if("C".equals(sFrStatCd)){
					sFrtomoveStatCdYd = "D";
				}
				
				//================================================================
				// TB_PT_STLFRTOMOVE 테이블에 이송상태코드를  업데이트 해준다.
				//================================================================
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("FRTOMOVE_STAT_CD", sFrtomoveStatCdYd);
				recPara.setField("STL_NO"          , sStlNo);
				recPara.setField("TRANSWORD_SEQNO" , sTranswordSeqno);
				nRet = ydStockDao.updYdStock8(recPara, 0);
				
				szMsg = "[슬라브이송지시] 이송대상 처리결과  nRet(" + nRet + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//================================================================
				// 야드저장품 항목을 업데이트 한다.
				//================================================================
				nRet = ydCodeMapping.getMappingCommonField(szRcvTcCode, sStlNo, false);
				
			}
			
		}catch(Exception e){
			szMsg="Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} // end of try-catch
	} // end of procSlavFtmvOrd()
	
	/**
	 * 코일소재이송지시 (PTYDJ002)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilMatlFtmvOrd(JDTORecord inRecord)throws JDTOException  {

		String szMethodName = "procCoilMatlFtmvOrd";
		String szMsg = "";
		String szOperationName = "코일소재이송지시";
		int intRtnVal = 0;
		String szFRTOMOVE_STAT_CD ="";
		String szYD_AIM_RT_GP ="";
		String szSTL_APPEAR_GP ="";
		String szYD_MTL_ITEM  = "";

		JDTORecord recResult 			= null;
		JDTORecord recStockColumn 		= null;
		
		JDTORecordSet rsResult 					= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsOutStock 				= JDTORecordFactory.getInstance().createRecordSet("");
		
		recResult 							= JDTORecordFactory.getInstance().create();
		recStockColumn  					= JDTORecordFactory.getInstance().create();
		
		YdStockDao ydStockDao = new YdStockDao();
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[진행관리] 코일소재이송지시 수신";
			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATE_COIL*/
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydStockDao.getYdStock(inRecord, rsResult, 84);
			//이송지시가 존재 하지 않을경우 
			if(intRtnVal <= 0){
				if(intRtnVal == 0){
					szMsg= "getYdStockFRTOMOVE_WORD_DATE [이송지시] Error :: DO NOT EXIST ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}else{
					szMsg= "getYdStockFRTOMOVE_WORD_DATE [이송지시] Error :: PARAMETER ERROR" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}	
			}

			szMsg="[1]getYdStockFRTOMOVE_WORD_DATE[이송지시] SELECT Success !";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			rsResult.first();

			/*
			 * TB_PT_STLFRTOMOVE TABLE MAX 재료번호 
			 * 상차완료시점 : FRTOMOVE_CARLOAD_DATE(이송상차일자)
			 * 하차완료시점 : FRTOMOVE_DONE_DATE(이송완료일시) FTMV_HDS_DD(이송계상일자) FRTOMOVE_STAT_CD(이송상태코드 = [ * 이송작업완료])
			 */
			for( int i = 0; i < rsResult.size() ; i++){

				recResult = rsResult.getRecord();

				szFRTOMOVE_STAT_CD = ydDaoUtils.paraRecChkNull(recResult, "FRTOMOVE_STAT_CD");
				szYD_AIM_RT_GP =  ydDaoUtils.paraRecChkNull(recResult,"YD_AIM_RT_GP");
				szSTL_APPEAR_GP 	= ydDaoUtils.paraRecChkNull(recResult,"STL_APPEAR_GP");
				
				//압연완료 ~ 제품창고 입고 이전 (종합판정)
				if(szSTL_APPEAR_GP.equals("E")){
					szYD_MTL_ITEM  	= "CM";
				}else {
					szYD_MTL_ITEM  	= "CG";
				}
				
				//이미 이송지시 상태인 경우 통과
				if(!szFRTOMOVE_STAT_CD.equals("C")&& // 공정 이송지시 확정 
						szYD_AIM_RT_GP.startsWith("E")){	// 목표행선이 이송상태로 이미 등록
					szMsg ="[코일이송지시]등록불가 Error : [" +recResult.getFieldString("STL_NO")+"]가 이미 이송지시 상태";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					//재료상태변경이력 등록 --------------------------------------------------------------------------------
					intRtnVal = ydStockDao.getYdStock(recResult, rsOutStock, 0);
					if(intRtnVal <=0 ){
						if(intRtnVal == 0){
							szMsg= "YDSTOCK[저장품] SELECT Error :: [" + recResult.getFieldString("STL_NO") + "]" +"DO NOT EXIST";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							return ;
						}else{
							szMsg= "YDSTOCK[저장품] SELECT Error :: [" + intRtnVal + "]"+"PARAMETER ERROR" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							return ;
						}
					}

					rsOutStock.first();
					recStockColumn = rsOutStock.getRecord();
					
				}
				
				//이송지시등록 -------------------------------------------------------------------------------------------
				if(!szFRTOMOVE_STAT_CD.equals("C")&& 
				   !szYD_AIM_RT_GP.startsWith("E")){
					
					recStockColumn  = JDTORecordFactory.getInstance().create();				
					recStockColumn.setField("YD_MTL_ITEM", szYD_MTL_ITEM);
					recStockColumn.setField("YD_AIM_RT_GP"	, getCoilCurYdAimRtGpInfo_002(ydDaoUtils.paraRecChkNull(recResult,"STL_NO")));	
					recStockColumn.setField("STL_NO", 		ydDaoUtils.paraRecChkNull(recResult,"STL_NO"));
					recStockColumn.setField("MODIFIER"		, "PTYDJ002");
					intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
					if(intRtnVal <= 0){
						szMsg= "YD_STOCK[코일이송지시] 등록실패 :: [" + intRtnVal + "]" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg= recResult.getFieldString("STL_NO") +") : YD_STOCK[코일이송지시] 등록완료"+i;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				}
				//이송지시취소-------------------------------------------------------------------------------------------
				else if(szFRTOMOVE_STAT_CD.equals("C")&&	// 이송지시 취소 
						szYD_AIM_RT_GP.startsWith("E")){	// 목표행선이 이송상태로 이미 등록

					szMsg = "YD_STOCK[코일이송지시] :: 이송취시취소 상태임 " ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					recStockColumn  = JDTORecordFactory.getInstance().create();					

					//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
					rVal= YdCommonUtils.getYdAimRtGp("C",inRecord );		
					recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
					recStockColumn.setField("STL_NO", 	ydDaoUtils.paraRecChkNull(recResult,"STL_NO"));
					recStockColumn.setField("MODIFIER", "PTYDJ002");
					intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
					if(intRtnVal <= 0){
						szMsg= "YD_STOCK[코일이송지시취소] 등록실패 :: [" + intRtnVal + "]" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg= recResult.getFieldString("STL_NO") +") : YD_STOCK[코일이송지시취소] 등록완료"+i;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}

				}//-------------------------------------------------------------------------------------------
				else if(szFRTOMOVE_STAT_CD.equals("C")&&
						!szYD_AIM_RT_GP.startsWith("E")){	// 목표행선이 이송상태가 아닌경우 
					szMsg = recResult.getFieldString("STL_NO")+"[코일이송지시취소]등록 Error : 진도코드가 이송지시 상태가 아님";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				rsResult.next();
			}
		}catch(Exception e){

			szMsg="[코일이송지시]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch

	} // end of procCoilMatlFtmvOrd()
	
	/**
	 * 코일소재임가공이송지시 (PTYDJ003)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilMatlRentprocFtmvOrd(JDTORecord inRecord)throws JDTOException  {

		String szMethodName = "procCoilMatlRentprocFtmvOrd";
		String szMsg = "";
		String szOperationName = "코일소재임가공이송지시";
		int intRtnVal = 0;

		String szFRTOMOVE_STAT_CD ="";
		String szYD_AIM_RT_GP ="";

		JDTORecord recResult 			= null;
		JDTORecord recStockColumn 		= null;
		
		JDTORecordSet rsResult 					= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsOutStock 				= JDTORecordFactory.getInstance().createRecordSet("");
		
		recResult 							= JDTORecordFactory.getInstance().create();
		recStockColumn  					= JDTORecordFactory.getInstance().create();
		
		YdStockDao ydStockDao = new YdStockDao();
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[진행관리] 코일소재임가공이송지시 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			intRtnVal = ydStockDao.getYdStock(inRecord, rsResult, 85);
			if(intRtnVal>0){

				szMsg=" 이송지시 검색 완료!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
			else{
				szMsg="Error : 지시를 받았으나 이송지시이거나 이송지시취소인  재료번호가 없음 ---------- !";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			rsResult.first();
			
			/*
			 * TB_PT_STLFRTOMOVE TABLE MAX 재료번호 
			 * 상차완료시점 : FRTOMOVE_CARLOAD_DATE(이송상차일자)
			 * 하차완료시점 : FRTOMOVE_DONE_DATE(이송완료일시) FTMV_HDS_DD(이송계상일자) FRTOMOVE_STAT_CD(이송상태코드 = [ * 이송작업완료])
			 */
			for( int i = 0; i < rsResult.size() ; i++){

				recResult = rsResult.getRecord();

				szFRTOMOVE_STAT_CD = recResult.getFieldString("FRTOMOVE_STAT_CD");
				ydDaoUtils.paraRecChkNull(recResult, "FRTOMOVE_STAT_CD");
				szYD_AIM_RT_GP =  ydDaoUtils.paraRecChkNull(recResult,"YD_AIM_RT_GP");
				
				//이송지시등록 
				if(szFRTOMOVE_STAT_CD.equals("1")&& // 공정 이송지시 확정 
				   szYD_AIM_RT_GP.startsWith("E")){	// 목표행선이 이송상태로 이미 등록
				
					szMsg = recResult.getFieldString("STL_NO")+"-----등록불가 :이미 이송지시 상태임. ---------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				else if(szFRTOMOVE_STAT_CD.equals("1")&& 
						!szYD_AIM_RT_GP.startsWith("E"))
				{

					intRtnVal = ydStockDao.getYdStock(recResult, rsOutStock, 0);
					if(intRtnVal<0){
						szMsg = "-----저장품 조회시 실패---------------";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}

					szMsg = "-----저장품에 해당 재료번호가 있음 . 이송지시등록 전,재료상태변경이력에 현재의 관련항목과 값을 등록함 ---------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					rsOutStock.first();
					recStockColumn = rsOutStock.getRecord();
					
					recStockColumn.setField("STL_NO", 					ydDaoUtils.paraRecChkNull(recResult,"STL_NO"));
					recStockColumn.setField("FRTOMOVE_ORD_DATE", 		ydDaoUtils.paraRecChkNull(recResult,"FRTOMOVE_WORD_DATE"));
					recStockColumn.setField("PLNT_PROC_CD", 			ydDaoUtils.paraRecChkNull(recResult,"WO_CAR_PLNT_PROC_CD"));
					recStockColumn.setField("URGENT_FRTOMOVE_WORD_GP", 	ydDaoUtils.paraRecChkNull(recResult,"URGENT_FRTOMOVE_WORD_GP"));
//					recStockColumn.setField("YD_AIM_RT_GP", 			getCoilCurYdAimRtGpInfo_002(ydDaoUtils.paraRecChkNull(recResult,"STL_NO")));	

					//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
					rVal= YdCommonUtils.getYdAimRtGp("C",inRecord );		
					recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
					
					recStockColumn.setField("MODIFIER", "PTYDJ003");
					intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
					if(intRtnVal >0){
						szMsg=recStockColumn.getFieldString("STL_NO")+"의 이송지시등록 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else{
						szMsg="저장품 갱신 실패! ["+intRtnVal+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
				}
				//이송지시취소
				else if(szFRTOMOVE_STAT_CD.equals("C")&&	// 이송지시 취소 
						szYD_AIM_RT_GP.startsWith("E")){	// 목표행선이 이송상태로 이미 등록
					
					szMsg = "----이송지시를 취소함!----";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recStockColumn.setField("STL_NO", 		ydDaoUtils.paraRecChkNull(recResult,"STL_NO"));
//					recStockColumn.setField("YD_AIM_RT_GP", "TT"); // 이송취소시 이전 야드행선구분으로 셋팅_추후 BRE 등록 
					
					//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
					rVal= YdCommonUtils.getYdAimRtGp("C",inRecord );		
					recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
					
					recStockColumn.setField("MODIFIER", "PTYDJ003");
					intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
					if(intRtnVal >0){
						szMsg=recStockColumn.getFieldString("STL_NO")+"의 이송지시취소 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else{
						szMsg="저장품 갱신 실패! ["+intRtnVal+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}

				}
				else if(szFRTOMOVE_STAT_CD.equals("C")&&
						!szYD_AIM_RT_GP.startsWith("E")){	// 목표행선이 이송상태가 아닌경우 
					szMsg = recResult.getFieldString("STL_NO")+"----이송지시취소 불가 : 야드목표행선이 이송지시 상태가 아님!----";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				rsResult.next();
			}
		}catch(Exception e){

			szMsg="Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch

	} // end of procCoilMatlRentprocFtmvOrd()
	
	/**
	 * OS주문투입실적 (PTYDJ004)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procOrdInputHis(JDTORecord inRecord)throws JDTOException  {

		String szMethodName = "procOrdInputHis";
		String szMsg = "";
		String szOperationName = "OS주문투입실적";
		String szRtnMsg = "";
		
		String szRcvTcCode=ydUtils.getTcCode(inRecord);


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.10 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= inRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(inRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "OS주문투입실적 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.10 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			return ;
		}
		
		try{
			
			String szORD_NO  	= ydDaoUtils.paraRecChkNull(inRecord, "ORD_NO");
			String szORD_DTL 	= ydDaoUtils.paraRecChkNull(inRecord, "ORD_DTL");
			String szFINAL_YN 	= ydDaoUtils.paraRecChkNull(inRecord, "FINAL_YN");
			
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			
			// 석창화 2009.12.21 - OS투입주문 Piling_cd, Book_out, 입고예정위치 Update
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.10 로그 개선  START
// procPtOsCommUpdateByYdStrCharGrp call 시  logId 항목 추가 개선 
//			szRtnMsg = PlateGdsYdUtil.procPtOsCommUpdateByYdStrCharGrp(szORD_NO, szORD_DTL, szFINAL_YN);
			szRtnMsg = PlateGdsYdUtil.procPtOsCommUpdateByYdStrCharGrp(szORD_NO, szORD_DTL, szFINAL_YN, logId);

// 2024.09.10 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
			
			if(szRtnMsg != YdConstant.RETN_CD_SUCCESS) {
				szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() PlateGdsYdUtil.procPtOsCommUpdateByYdStrCharGrp Error :" + szRtnMsg;
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
				return ;
			}
			
		}catch(Exception e){

			szMsg="Exception Error:" +e.getMessage();
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			throw new JDTOException(szMsg);

		} // end of try-catch

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.10 로그 개선  START

szMsg = "OS주문투입실적 처리(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.10 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
	} // end of procOrdInputHis()
	
	/**
	 * OS주문변경정보 (PTYDJ005)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procOrdInputChg(JDTORecord inRecord)throws JDTOException  {

		String szMethodName 	= "procOrdInputChg";
		String szOperationName 	= "OS주문변경정보";
		String szMsg 			= "";
		String szRtnMsg = "";
		
		String logId                    = ydUtils.getJDTOLogId(inRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		szMsg = "OS주문변경정보(" + szMethodName + ") 시작";
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);
		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR,logId);
			return ;
		}
		
		try{
			String sOrdNo		=	ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO");				
			String sOrdDtl		=	ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL");				
			String sDemanderCd	=	ydDaoUtils.paraRecChkNull(inRecord,"DEMANDER_CD");		
			String sDestCd		=	ydDaoUtils.paraRecChkNull(inRecord,"DEST_CD");
			
			ydUtils.putLog(szSessionName, szMethodName, "ORD_NO 	= "+ sOrdNo, 3);		
			ydUtils.putLog(szSessionName, szMethodName, "ORD_DTL 	= "+ sOrdDtl, 3);			
			ydUtils.putLog(szSessionName, szMethodName, "DEMANDER_CD= "+ sDemanderCd, 3);	
			ydUtils.putLog(szSessionName, szMethodName, "DEST_CD 	= "+ sDestCd, 3);		
			
			//변경 주문번호 다시 읽어서, 해당 운송지역/고객사/인도처/수요가에 맞는 파일링코드로 재셋팅.  
			szRtnMsg = PlateGdsYdUtil.procPtOsCommModifyByYdStrCharGrp(sOrdNo, sOrdDtl, logId);

			
			if(szRtnMsg != YdConstant.RETN_CD_SUCCESS) {
				szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() PlateGdsYdUtil.procPtOsCommUpdateByYdStrCharGrp Error :" + szRtnMsg;
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
				return ;
			}			
			
		}catch(Exception e){

			szMsg="Exception Error:" +e.getMessage();
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			throw new JDTOException(szMsg);

		} // end of try-catch


		szMsg = "OS주문변경정보 처리(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
	} // end of procOrdInputChg()
	
	/*
	 * 코일 이송지시 시점에 해당 재료의 목표행선 정보를 가져온다.
	 */
	private String getCoilCurYdAimRtGpInfo_002(String sStockId){
		
		String szMethodName 	= "getCoilCurYdAimRtGpInfo_002";
		String szMsg 			= "";
		String sYdAimRtGp 		= "XX";
		String sRecodeProgStat 	= "";
		
		int intRtnVal	= 0;
		
		JDTORecordSet jDtoRecSetM = JDTORecordFactory.getInstance().createRecordSet("");
		
		JDTORecord jDtoRecM	= JDTORecordFactory.getInstance().create();
		JDTORecord jDtoParM	= JDTORecordFactory.getInstance().create();
		
		YdStockDao ydStockDao = new YdStockDao();
		
		String sPlntProcCd 		= "";	// 공장공정코드
		String sCurrProgCd		= "";	// 현재진도코드
		String sNextProc		= "";	// 다음공정 
		String sPlanProc1		= "";	// 열연계획작업코드1
		
		try{
			//수신한 재료번호로 코일공통 읽고 저장품 항목으로 편집
			jDtoParM.setField("COIL_NO", sStockId);
			intRtnVal = ydStockDao.getYdStock(jDtoParM, jDtoRecSetM, 8);
			
			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+jDtoParM.getFieldString("COIL_NO")+"]에 대한 데이타가 코일공통에 존재함";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+jDtoParM.getFieldString("COIL_NO")+"]에 대한 데이타가 코일공통에 없음.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return "";
			}else if(intRtnVal == -2){
				szMsg = "수신한 "+jDtoParM.getFieldString("COIL_NO")+"로 코일공통 조회  중 Parameter Error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return "";
			}else{
				szMsg = "Error :수신한 "+jDtoParM.getFieldString("COIL_NO")+"로 코일공통 조회 중 예외상황발생 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return "";
			}
			
			jDtoRecSetM.first();
			jDtoRecM = jDtoRecSetM.getRecord();

			sRecodeProgStat = ydDaoUtils.paraRecChkNull(jDtoRecM, "RECORD_PROG_STAT");
			
			// 레코드진행상태가 '2'일 때 관련항목을 저장품 항목으로 편집
			
			if(sRecodeProgStat.equals("2")){ 
				
				sPlntProcCd		=	ydDaoUtils.paraRecChkNull(jDtoRecM,"PLNT_PROC_CD");				
				sCurrProgCd		=	ydDaoUtils.paraRecChkNull(jDtoRecM,"CURR_PROG_CD");				
				sNextProc		=	ydDaoUtils.paraRecChkNull(jDtoRecM,"NEXT_PROC");		
				sPlanProc1		=	ydDaoUtils.paraRecChkNull(jDtoRecM,"PLAN_PROC1");
				
				ydUtils.putLog(szSessionName, szMethodName, "PLNT_PROC_CD 	= "+	  sPlntProcCd, 3);		
				ydUtils.putLog(szSessionName, szMethodName, "CURR_PROG_CD 	= "+	  sCurrProgCd, 3);			
				ydUtils.putLog(szSessionName, szMethodName, "NEXT_PROC 		= "+	  sNextProc, 3);	
				ydUtils.putLog(szSessionName, szMethodName, "PLAN_PROC1 	= "+      sPlanProc1, 3);		
			}
			{
				String sWorkProc	= "";
				
				if(!"".equals(sNextProc)){
					sWorkProc = sNextProc;
				}else{
					sWorkProc = sPlanProc1;
				}
				// 계획공정정보를 가지고 야드행선을 셋팅
				if(sWorkProc.startsWith("1")){
					sYdAimRtGp	= "EA";
				}else if(sWorkProc.startsWith("5")||
						 sWorkProc.startsWith("6")){
					sYdAimRtGp	= "EB";
				}else if(sWorkProc.startsWith("9S")){
					sYdAimRtGp	= "ED";
					//sYdAimRtGp	= "EE";
					//sYdAimRtGp	= "EF";
				}else{
					sYdAimRtGp	= "EC";
				}
			}
			szMsg="코일 이송지시 최종목표행선 : " + sYdAimRtGp;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
		}catch(Exception e){
	
			szMsg="Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	
		} // end of try-catch
		return sYdAimRtGp;
	}
	
	/**
	 * 코일제품고간이송지시(DMYDR011)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilGdsWhFtmvOrd(JDTORecord inRecord)throws JDTOException  {
		YdStockDao ydStockDao 					= new YdStockDao();
				
		JDTORecord recEditColumn 				= JDTORecordFactory.getInstance().create();
		
		String szMethodName 					= "procCoilGdsWhFtmvOrd";
		String szMsg 							= "";
		String szOperationName                  = "코일제품고간이송지시";
		String szSTL_NO 						= "";
		int intRtnVal 							= 0;
		int i 									= 0;

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;

		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 코일제품고간이송지시 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP	재료외형구분
			TRANS_ORD_DT	운송지시일자
			TRANS_ORD_SEQNO	운송지시순번
			STL_NO1			재료번호1
			STL_NO2			재료번호2
			STL_NO3			재료번호3
			STL_NO4			재료번호4
			STL_NO5			재료번호5
			STL_NO6			재료번호6
			STL_NO7			재료번호7
			STL_NO8			재료번호8
			STL_NO9			재료번호9
			STL_NO10		재료번호10
			STL_NO11		재료번호11
			STL_NO12		재료번호12
			STL_NO13		재료번호13
			STL_NO14		재료번호14
			STL_NO15		재료번호15
			STL_NO16		재료번호16
			STL_NO17		재료번호17
			STL_NO18		재료번호18
			STL_NO19		재료번호19
			STL_NO20		재료번호20
			*/
			


			//****************************************************************************************************
			
			//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
			for(i = 1 ; i<=20; i++){
				recEditColumn 	= JDTORecordFactory.getInstance().create();
				recEditColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
				recEditColumn.setField("TRANS_ORD_DATE", 		ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
				recEditColumn.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
				recEditColumn.setField("MODIFIER", 				"DMYDR011");
				recEditColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i));
				
				//STL_NO가 없다면 loop종료
				szSTL_NO = recEditColumn.getFieldString("STL_NO");
				if(szSTL_NO.equals("")){
					break;
				}				

				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal= YdCommonUtils.getYdAimRtGp("C",recEditColumn );		
				recEditColumn.setField("YD_AIM_RT_GP", rVal[0]);
					
					
				//저장품갱신******************************************************************************************** 
				intRtnVal = ydStockDao.updYdStock(recEditColumn, 0);
				if(intRtnVal <= 0){
					szMsg= "YD_STOCK[코일제품고간이송지시] UPDATE Error :: [" + intRtnVal + "]" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[코일제품고간이송지시] UPDATE Success",3);
				//****************************************************************************************************


			} //end of for *******************************************************************************************
			

		}catch(Exception e){
			szMsg = "[코일제품고간이송지시]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
	} // end of procCoilGdsWhFtmvOrd
	
	

	/**
	 * 후판제품고간이송지시(DMYDR012)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlGdsWhFtmvOrd(JDTORecord inRecord)throws JDTOException  {

		String szMethodName = "procPlGdsWhFtmvOrd";
		String szMsg = "";	
		String szOperationName = "후판제품고간이송지시";
		String szSTL_NO = null;

		int intRtnVal = 0;
		int i = 0;

		JDTORecord recEditColumn 	= JDTORecordFactory.getInstance().create();
		YdStockDao ydStockDao = new YdStockDao();
		
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;

		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 후판제품고간이송지시 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_K, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP	재료외형구분
			TRANS_ORD_DT	운송지시일자
			TRANS_ORD_SEQNO	운송지시순번
			STL_NO1			재료번호1
			STL_NO2			재료번호2
			STL_NO3			재료번호3
			STL_NO4			재료번호4
			STL_NO5			재료번호5
			STL_NO6			재료번호6
			STL_NO7			재료번호7
			STL_NO8			재료번호8
			STL_NO9			재료번호9
			STL_NO10		재료번호10
			STL_NO11		재료번호11
			STL_NO12		재료번호12
			STL_NO13		재료번호13
			STL_NO14		재료번호14
			STL_NO15		재료번호15
			STL_NO16		재료번호16
			STL_NO17		재료번호17
			STL_NO18		재료번호18
			STL_NO19		재료번호19
			STL_NO20		재료번호20
			*/
			
			recEditColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recEditColumn.setField("TRANS_ORD_DATE", 		ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
			recEditColumn.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
			recEditColumn.setField("MODIFIER", 				"DMYDR012");

			//****************************************************************************************************
			
			//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
			for(i = 1 ; i<=20; i++){
				recEditColumn 	= JDTORecordFactory.getInstance().create();
				recEditColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i));
				
				//STL_NO가 없다면 loop종료
				szSTL_NO = recEditColumn.getFieldString("STL_NO");
				if(szSTL_NO.equals("")){
					break;
				}				

				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal= YdCommonUtils.getYdAimRtGp("P",inRecord );		
				recEditColumn.setField("YD_AIM_RT_GP", rVal[0]);
					
					
				//저장품갱신******************************************************************************************** 
				intRtnVal = ydStockDao.updYdStock(recEditColumn, 0);
				if(intRtnVal <= 0){
					szMsg= "YD_STOCK[후판제품고간이송지시] UPDATE Error :: [" + intRtnVal + "]" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[후판제품고간이송지시] UPDATE Success",3);
				//****************************************************************************************************


			} //end of for *******************************************************************************************
			
		}catch(Exception e){
			szMsg = "[후판제품고간이송지시]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
	} // end of procPlGdsWhFtmvOrd
	
	/**
	 * 외판슬라브보관지시(DMYDR026)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */	
	public void procOutplSlabKeepOrd(JDTORecord inRecord)throws JDTOException  {
		YdStockDao ydStockDao = new YdStockDao();
		
		YdCodeMapping ydCodeMapping = new YdCodeMapping();

		JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();
		JDTORecord outRecTemp = null;
		
		String szMethodName = "procOutplSlabKeepOrd";
		String szMsg = "";
		String szOperationName = "외판슬라브보관지시";
		String szSTL_NO ="";
		int intRtnVal = 0;
		int nRet = 0;

		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode = ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 외판슬라브보관지시 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

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
			DLVRDD_RULE_DD		납기기준일
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
			recStockColumn.setField("YD_DLVRDD_RULE_DD", 	ydDaoUtils.paraRecChkNull(inRecord,"DLVRDD_RULE_DD"));
			recStockColumn.setField("DEST_TEL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"DEST_TEL_NO"));
			recStockColumn.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"));
			recStockColumn.setField("MODIFIER", 			"DMYDR026");

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("S",inRecord );		
			recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
			//****************************************************************************************************

            //=============================================================================================
			// 2009.09.15
			// 권오창
			// 코드 매핑값 호출 
			//
			//     * 앞에서 코드매핑값에 대해 어떤처리를 하든지 다시 엎어침... 
			//       나중에 앞에서 아래코드에 대해 처리하는 부분 삭제해야 됨                         
            //=============================================================================================
			outRecTemp = JDTORecordFactory.getInstance().create();
			nRet = ydCodeMapping.MakeCodeMapping(szRcvTcCode, szSTL_NO, inRecord, outRecTemp);
			if(nRet <= 0){
				String szTempSTL_APPEAR_GP =  ydDaoUtils.paraRecChkNull(outRecTemp, "STL_APPEAR_GP");
				if(!szTempSTL_APPEAR_GP.trim().equals("")){
					recStockColumn.setField("STL_APPEAR_GP", szTempSTL_APPEAR_GP);
				}
				
				String szTempSCARFING_YN =  ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_YN");
				if(!szTempSCARFING_YN.trim().equals("")){
					recStockColumn.setField("SCARFING_YN", szTempSCARFING_YN);
				}

				String szSCARFING_DONE_YN =  ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_DONE_YN");
				if(!szSCARFING_DONE_YN.trim().equals("")){
					recStockColumn.setField("SCARFING_DONE_YN", szSCARFING_DONE_YN);
				}
				
				szMsg = "[nRet " + nRet + "] 매핑되는 코드가 없습니다. 재료외형, 스카핑여부, 스카핑 완료여부는 업데이트를 위함 STL_APPEAR_GP(" + szTempSTL_APPEAR_GP + ") SCARFING_YN(" + szTempSCARFING_YN + ") SCARFING_DONE_YN(" + szSCARFING_DONE_YN + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else {
				String szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "STL_APPEAR_GP");
				if(!szSTL_APPEAR_GP.equals("")){
					recStockColumn.setField("STL_APPEAR_GP"   , szSTL_APPEAR_GP);
				}
				
				String szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_RT_GP");
				if(!szYD_AIM_RT_GP.equals("")){
					recStockColumn.setField("YD_AIM_RT_GP"    , szYD_AIM_RT_GP);
				}

				String szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_YD_GP");
				if(!szYD_AIM_YD_GP.equals("")){
					recStockColumn.setField("YD_AIM_YD_GP"    , szYD_AIM_YD_GP);
				}

				String szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_BAY_GP");
				if(!szYD_AIM_BAY_GP.equals("")){
					recStockColumn.setField("YD_AIM_BAY_GP"   , szYD_AIM_BAY_GP);
				}
				
				String szSCARFING_YN = ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_YN");
				if(!szSCARFING_YN.equals("")){
					recStockColumn.setField("SCARFING_YN"   , szSCARFING_YN);
				}
				
				String szSCARFING_DONE_YN = ydDaoUtils.paraRecChkNull(outRecTemp, "SCARFING_DONE_YN");
				if(!szSCARFING_DONE_YN.equals("")){
					recStockColumn.setField("SCARFING_DONE_YN"   , szSCARFING_DONE_YN);
				}		
			}
            //=============================================================================================
			
			stock.setYdStkLocTpCd(recStockColumn);			

			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[외판슬라브보관지시] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[외판슬라브보관지시] UPDATE Success",3);
			//****************************************************************************************************

			//======================================================
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
			szMsg="[외판슬라브보관지시]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch

	} // end of procOutplSlabKeepOrd()
	
	/**
	 *코일공통 항목을 야드 저장품 항목으로 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtCoilCommYdstock(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {

		try{

			recEditRec.setField("REGISTER", 				ydDaoUtils.paraRecChkNull(inRecord,"REGISTER")); 			
			recEditRec.setField("STL_NO", 					ydDaoUtils.paraRecChkNull(inRecord,"COIL_NO")); 					
			recEditRec.setField("YD_MTL_T", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_T")); 		
			recEditRec.setField("YD_MTL_W", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_W")); 		    
			recEditRec.setField("YD_MTL_L", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_LEN"));		
			recEditRec.setField("YD_MTL_WT", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_WT"));	
			recEditRec.setField("COIL_INDIA", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_INDIA"));	
			recEditRec.setField("COIL_OUTDIA", 				ydDaoUtils.paraRecChkNull(inRecord,"COIL_OUTDIA"));	
			recEditRec.setField("PLNT_PROC_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"PLNT_PROC_CD"));				
			recEditRec.setField("STL_PROG_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));				
			recEditRec.setField("STL_APPEAR_GP", 			ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));		
			recEditRec.setField("ORD_YEOJAE_GP", 			ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));			
			recEditRec.setField("ORD_NO", 					ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO")); 			
			recEditRec.setField("ORD_DTL", 					ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL")); 				
			//recEditRec.setField("ORD_HCR_GP", 			ydDaoUtils.paraRecChkNull(inRecord,"ORD_HCR_GP")); 		    
			//recEditRec.setField("HCR_GP", 				ydDaoUtils.paraRecChkNull(inRecord,"HCR_GP"));
			//recEditRec.setField("NEXT_PROC", 				ydDaoUtils.paraRecChkNull(inRecord,"NEXT_PROC"));
			//recEditRec.setField("NEXT_PROC", 				ydDaoUtils.paraRecChkNull(inRecord,"NEXT_PROC"));
			recEditRec.setField("HYSCO_TRANS_GP", 			ydDaoUtils.paraRecChkNull(inRecord,"HYSCO_TRANS_GP"));		
			//recEditRec.setField("CUST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD"));				
			//recEditRec.setField("ITEMNAME_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"ITEMNAME_CD"));
			recEditRec.setField("COOL_METHOD", 				ydDaoUtils.paraRecChkNull(inRecord,"COOL_METHOD"));		
			//recEditRec.setField("YD_CONVEYOR_BRANCH_CD", 	ydDaoUtils.paraRecChkNull(inRecord,"HYSCO_TRANS_GP"));		
	
		
		} catch(Exception e){

			return -1;
		}
		return 1;

	} //end of edtCoilCommYdstock()
	
	/**
	 * 코일제품보관지시(DMYDR027)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */	
	public void procCoilGdsKeepOrd(JDTORecord inRecord)throws JDTOException  {

		String szMethodName = "procCoilGdsKeepOrd";
		String szMsg = "";
		String szOperationName = "코일제품보관지시";
		String szSTL_NO ="";
		int intRtnVal = 0;
		
		JDTORecord recStockColumn 			= JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
	
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 코일제품보관지시 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

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
			DLVRDD_RULE_DD		납기기준일
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
			recStockColumn.setField("YD_DLVRDD_RULE_DD", 	ydDaoUtils.paraRecChkNull(inRecord,"DLVRDD_RULE_DD"));
			recStockColumn.setField("DEST_TEL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"DEST_TEL_NO"));
			recStockColumn.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"));
			recStockColumn.setField("MODIFIER", 			"DMYDR027");

//SJH추가
			recStockColumn.setField("SCARFING_YN", 			"Y");
			
			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("C",inRecord );		
			recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
			//****************************************************************************************************

						
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[코일제품보관지시] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[코일제품보관지시] UPDATE Success",3);
			//****************************************************************************************************

			//======================================================
			// 저장품제원 : 코일야드L2 로 송신(YDY5L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY5L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			ydDelegate.sendMsg(recResult);
			
		}catch(Exception e){
			szMsg="[코일제품보관지시]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch

	} // end of procCoilGdsKeepOrd()
	
	/**
	 * 후판제품보관지시(DMYDR028)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */	
	public void procPlGdsKeepOrd(JDTORecord inRecord)throws JDTOException  {

		String szMethodName = "procPlGdsKeepOrd";
		String szMsg = "";
		String szOperationName = "후판제품보관지시";
		String szSTL_NO ="";
		int intRtnVal = 0;
		
		JDTORecord recStockColumn 			= JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 후판제품보관지시 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_K, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//수신한 전문값******************************************************************************************
			/*
			TRANSMIT_DATE		지시일자
			SEND_SEQ			지시순번
			*/
			intRtnVal = ydStockDao.updateStlHoldstat_01(ydDaoUtils.paraRecChkNull(inRecord,"TRANSMIT_DATE"),
													    ydDaoUtils.paraRecChkNull(inRecord,"SEND_SEQ"));
			
			ydUtils.putLog(szSessionName, szMethodName,"YD_STOCK[후판제품보관지시] UPDATE Success",3);
			//****************************************************************************************************
			
			// 2021. 5. 17 제원정보 송신
			YdPlateCommDAO commDao	= new YdPlateCommDAO();
			JDTORecordSet rsRecord = JDTORecordFactory.getInstance().createRecordSet("plateComm");
			JDTORecord recResult = null;
			if( commDao.select(inRecord, rsRecord, "com.inisteel.cim.yd.dao.ydstkbeddao.YdStockDao.getPlGdsKeepOrdList") > 0 ){
				
				int nRow = rsRecord.size();
				for(int i=0; i < nRow; i++){
					recResult = JDTORecordFactory.getInstance().create();
					recResult.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고 L2전문
					recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
					recResult.setField("STL_NO"         , rsRecord.getRecord(i).getFieldString("STL_NO"));
					recResult.setField("YD_STK_COL_GP"  , "");
					recResult.setField("YD_STK_BED_NO"  , "");
					
					// 2021. 05. 17 전문생성 시점에 PT_PLATE_COMM에 진도코드가 바뀌지 않은 문제가 있어
					// 진도코드를 파라메터로 넘겨 처리함
					recResult.setField("CURR_PROG_CD"  , rsRecord.getRecord(i).getFieldString("CURR_PROG_CD"));
					ydDelegate.sendMsg(recResult);		
					
					// 전사물류개선 2021. 4. 3
					recResult.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
					ydDelegate.sendMsg(recResult);
				}
			}
			
			
			
		}catch(Exception e){
			szMsg="[후판제품보관지시]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch

	} // end of procPlGdsKeepOrd()
	
	/**
	 * 후판제품보관지시(DMYDR028)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */	
	public void procPlGdsKeepOrd_backup(JDTORecord inRecord)throws JDTOException  {

		String szMethodName = "procPlGdsKeepOrd_backup";
		String szMsg = "";
		String szOperationName = "후판제품보관지시";
		String szSTL_NO ="";
		int intRtnVal = 0;
		
		JDTORecord recStockColumn 			= JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 후판제품보관지시 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_K, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
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
			DLVRDD_RULE_DD		납기기준일
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
			recStockColumn.setField("YD_DLVRDD_RULE_DD", 	ydDaoUtils.paraRecChkNull(inRecord,"DLVRDD_RULE_DD"));
			recStockColumn.setField("DEST_TEL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"DEST_TEL_NO"));
			recStockColumn.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"));
			recStockColumn.setField("MODIFIER", 			"DMYDR028");

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("P",inRecord );		
			recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
			//****************************************************************************************************
						
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[후판제품보관지시] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[후판제품보관지시] UPDATE1 Success",3);
			
			intRtnVal = ydStockDao.updateStlHoldstat(szSTL_NO,"DMYDR028","PA");
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[후판제품보관지시] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[후판제품보관지시] UPDATE2 Success",3);
			//****************************************************************************************************
			//======================================================
			// 2009.08.31 권오창
			// 저장품제원 : 후판제품 L2 로 송신(YDY4L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY4L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			ydDelegate.sendMsg(recResult);		
			
		}catch(Exception e){
			szMsg="[후판제품보관지시]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch

	} // end of procPlGdsKeepOrd_backup()
	
	/**
	 * 오퍼레이션명 : 일관제철  출하전문 취소처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receiveCancel(JDTORecord inRecord) {
		YdStockDao ydStockDao            = new YdStockDao();
		YdWrkbookMtlDao ydWrkbookDao     = new YdWrkbookMtlDao();

		
		JDTORecord recStockColumn        = null;
		JDTORecord recPara               = JDTORecordFactory.getInstance().create();
		JDTORecord recGetVal             = null;
		JDTORecordSet rsResult           = JDTORecordFactory.getInstance().createRecordSet("");
		

		// 변수선언
		String szMethodName              ="receiveCancel";
		String[] rVal                    = new String[2];		
		String sSTL_GP                   ="";
		String szYD_AIM_RT_GP            = "";		
		String szYD_GP                   = "";
		String szSTL_APPEAR_GP           = "";
		String szSTL_NO                  = "";
		String szCURR_PROG_CD            = ""; 
		String szCANCEL_YN               = "";
		String szWO_CAR_PLNT_PROC_CD     = "";
		String szFRTOMOVE_ORD_DATE       = "";
		String szURGENT_FRTOMOVE_WORD_GP = "";
		String szORD_YEOJAE_GP           = "";     
		String szORD_NO                  = "";            
		String szORD_DTL                 = "";       
		String szORD_GP                  = "";  
		String szCUST_CD                 = "";
		String szDEST_CD                 = "";
		String szDLVRDD_RULE_DD          = "";
		String szDEST_TEL_NO             = "";
		String szDIST_SHIPASSIGN_GP      = "";
		String szSHIPASSIGN_WORD_DATE    = ""; 
		String szSHIPASSIGN_WORD_SEQNO   = ""; 
		String szSHIP_CD                 = ""; 
		String szSHIP_NAME               = ""; 
		String szSAILNO                  = ""; 
		String szTRANS_ORD_DATE          = "";
		String szTRANS_ORD_SEQNO         = "";
		String szRSHP_HOLD_NO            = "";
		String szCAR_NO                  = "";
		String szCARD_NO                 = "";     
		String szARR_WLOC_CD             = ""; 
		int nRet                         = 0;

		int intRtnVal                    = 0;
		int intYD_EQP_WRK_SH			 = 0;

		ydUtils.putLog(szSessionName, szMethodName, "############# 일관제철  출하취소 전문 START ###############", YdConstant.DEBUG);

		String sJmsTcCd = StringHelper.evl(inRecord.getFieldString("JMS_TC_CD"), StringHelper.evl(inRecord.getFieldString("TC_CODE"),""));		
		ydUtils.putLog(szSessionName, szMethodName, "==>>일관제철 내부수신 전문[" + sJmsTcCd + "]", YdConstant.DEBUG);
		
	    try {	        
	    	/*
				DMYDR003	후판제품반송확정			STL_NO
				DMYDR008	코일제품반납확정			STL_NO
				DMYDR009	후판제품반납확정			STL_NO
				DMYDR011	코일제품고간이송지시		STL_NO1 ~ STL_NO20
				DMYDR012	후판제품고간이송지시		STL_NO1 ~ STL_NO20
				DMYDR013	외판슬라브목전				STL_NO
				DMYDR014	코일제품목전				STL_NO
				DMYDR015	후판제품목전				STL_NO
				DMYDR016	외판슬라브운송지시대기		STL_NO
				DMYDR018	후판제품운송지시대기		STL_NO
				DMYDR020	코일제품운송지시 			STL_NO1 ~ STL_NO20
				DMYDR021	후판제품운송상차지시		STL_NO1 ~ STL_NO20
				DMYDR022	외판슬라브운송상차지시		STL_NO1 ~ STL_NO20
				DMYDR023	코일제품상차지시			CAR_NO, CARD_NO
				DMYDR025	임가공이송상차지시			STL_NO1 ~ STL_NO20
				DMYDR026	외판슬라브보관지시			STL_NO
				DMYDR027	코일제품보관지시			STL_NO
				DMYDR028	후판제품보관지시			STL_NO

				
				DMYDR008	코일제품반납대기		1.저장품 이동 조건 변경
				DMYDR009	후판제품반납대기		1.저장품 이동 조건 변경
				DMYDR013	외판슬라브목전			1.저장품 이동 조건 변경
				DMYDR014	코일제품목전			1.저장품 이동 조건 변경
				DMYDR015	후판제품목전			1.저장품 이동 조건 변경
				DMYDR016	외판슬라브운송지시대기	1.저장품 이동 조건 변경
				DMYDR018	후판제품운송지시대기	1.저장품 이동 조건 변경
				DMYDR029	외판슬라브출하완료 		1.저장품 이동 조건 변경
				DMYDR030	코일제품출하완료              1.저장품 이동 조건 변경
				DMYDR031	후판제품출하완료		1.저장품 이동 조건 변경
				DMYDR011	코일제품고간이송지시	1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함),저장품 이동 조건 변경
				DMYDR012	후판제품고간이송지시	1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함),저장품 이동 조건 변경
				DMYDR020	코일제품운송지시              1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함) ,저장품 이동 조건 변경
				DMYDR021	후판제품운송지시		1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함),저장품 이동 조건 변경
				DMYDR022	외판슬라브운송상차지시	1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함),저장품 이동 조건 변경
				DMYDR023	코일제품상차지시		1.크레인 스케줄취소 ,2 작업예약취소 , 3. 카드번호삭제,저장품 이동 조건 변경
				DMYDR025	임가공이송상차지시		1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함),저장품 이동 조건 변경
				DMYDR026	외판슬라브보관지시		KEEPSTOCK_STL_YN= ''
				DMYDR027	코일제품보관지시		KEEPSTOCK_STL_YN= ''
				DMYDR028	후판제품보관지시
				DMYDR003	후판제품보류확정
	    	 */

	    	
	    	
	    	//=======================================================
	    	// 야드구분
	    	//=======================================================
	    	szYD_GP = ydDaoUtils.paraRecChkNull(inRecord, "YD_GP");
	    	if("A".equals(szYD_GP) || "S".equals(szYD_GP)){ //S:SLAB
	    		sSTL_GP = "S";	
	    	}else if(szYD_GP.equals("D")|| 
	    			 YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)){  //후판 - 2013.01.07 수정 (3기)
	    		sSTL_GP = "P";
	    	}else if(szYD_GP.equals("H") || szYD_GP.equals("J")){  //COIL
	    		this.coilReceiveCancel(inRecord);
	    		return true;
	    	} else {
	    		ydUtils.putLog(szSessionName, szMethodName, "전문에 야드구분(YD_GP)항목의 값이 없음", YdConstant.ERROR);	    		
	    		return false;
	    	}

	    	/*
	    	 * 후판출하상차지시 취소시점에 예외사항 체크 
	    	 */
	    	if(YdConstant.DMYDR060.equals(sJmsTcCd) && YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)){
	    		
	    		JDTORecordSet rsChkPara	= null;
	    		JDTORecord recChkPara	= null;
	    		String sChkStlNo		= "";
	    		String szMsg			= "";
	    		
	    		intYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNullInt(inRecord,"YD_EQP_WRK_SH");
	    		
	    		for(int index = 0; index < intYD_EQP_WRK_SH; index++){
	    			
	    			rsChkPara = JDTORecordFactory.getInstance().createRecordSet("");
	    			recChkPara  = JDTORecordFactory.getInstance().create();
	    			
	    			sChkStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO" + (index+1));
	    			
	    			if("".equals(sChkStlNo)) break;
	    			
	    			recChkPara.setField("STL_NO" , sChkStlNo);
	    			
	    			intRtnVal = ydWrkbookDao.getYdWrkbookmtl(recChkPara, rsChkPara, 2);
	    			
	    			if(intRtnVal <= 0) {
					}else{
						szMsg="["+szMethodName+"] 후판출하상차지시 취소시 이미 작업예약에 재료 존재함.["+sChkStlNo+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return false;
					}
	    		}
	    	}
			
	    	if(YdConstant.DMYDR003.equals(sJmsTcCd) || YdConstant.DMYDR008.equals(sJmsTcCd) || YdConstant.DMYDR009.equals(sJmsTcCd) ||	YdConstant.DMYDR013.equals(sJmsTcCd) ||	
    		   YdConstant.DMYDR014.equals(sJmsTcCd) || YdConstant.DMYDR015.equals(sJmsTcCd) || YdConstant.DMYDR016.equals(sJmsTcCd) ||	YdConstant.DMYDR018.equals(sJmsTcCd) ||	
    		   YdConstant.DMYDR026.equals(sJmsTcCd) || YdConstant.DMYDR027.equals(sJmsTcCd) || YdConstant.DMYDR028.equals(sJmsTcCd)){

	    		//==================================================================
		    	// 전문에 따른 분기 (재료번호 1개 : STL_NO)
				//==================================================================

	    		// 레코드 생성
		    	recStockColumn = JDTORecordFactory.getInstance().create();

	    		// 전문에서 공통적인 항목의 값을 추출
	    		szYD_GP         = ydDaoUtils.paraRecChkNull(inRecord, "YD_GP");
	    		szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(inRecord, "STL_APPEAR_GP");
	    		szSTL_NO        = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");
	    		szCURR_PROG_CD  = ydDaoUtils.paraRecChkNull(inRecord, "CURR_PROG_CD");
	    		szCANCEL_YN     = ydDaoUtils.paraRecChkNull(inRecord, "CANCEL_YN");
	    		
	    		// 레코드에 추가
//		    	recStockColumn.setField("YD_GP"        , szYD_GP);
		    	recStockColumn.setField("STL_APPEAR_GP", szSTL_APPEAR_GP);
		    	recStockColumn.setField("STL_NO"       , szSTL_NO);
				recStockColumn.setField("STL_PROG_CD"  , szCURR_PROG_CD);
				
	    		// TC에 따른 변동항목 추출
	    		if(YdConstant.DMYDR003.equals(sJmsTcCd) || YdConstant.DMYDR008.equals(sJmsTcCd) || YdConstant.DMYDR009.equals(sJmsTcCd)){
	    			szWO_CAR_PLNT_PROC_CD     = ydDaoUtils.paraRecChkNull(inRecord, "WO_CAR_PLNT_PROC_CD");
		    		szFRTOMOVE_ORD_DATE       = ydDaoUtils.paraRecChkNull(inRecord, "FRTOMOVE_ORD_DATE");
		    		szURGENT_FRTOMOVE_WORD_GP = ydDaoUtils.paraRecChkNull(inRecord, "URGENT_FRTOMOVE_WORD_GP");    			
	    		
		    		// 레코드에 추가
			    	recStockColumn.setField("WO_CAR_PLNT_PROC_CD"    , szWO_CAR_PLNT_PROC_CD);
			    	recStockColumn.setField("FRTOMOVE_ORD_DATE"      , szFRTOMOVE_ORD_DATE);
			    	recStockColumn.setField("URGENT_FRTOMOVE_WORD_GP", szURGENT_FRTOMOVE_WORD_GP);	 
			    	
	    		} else if(YdConstant.DMYDR013.equals(sJmsTcCd) || YdConstant.DMYDR014.equals(sJmsTcCd) || YdConstant.DMYDR015.equals(sJmsTcCd) || 
	    				  YdConstant.DMYDR016.equals(sJmsTcCd) || YdConstant.DMYDR018.equals(sJmsTcCd) || YdConstant.DMYDR026.equals(sJmsTcCd) || 
	    				  YdConstant.DMYDR027.equals(sJmsTcCd) || YdConstant.DMYDR028.equals(sJmsTcCd)){	
	    			
		    		szORD_YEOJAE_GP      = ydDaoUtils.paraRecChkNull(inRecord, "ORD_YEOJAE_GP");
		    		szORD_NO             = ydDaoUtils.paraRecChkNull(inRecord, "ORD_NO");
		    		szORD_DTL            = ydDaoUtils.paraRecChkNull(inRecord, "ORD_DTL");
		    		szORD_GP             = ydDaoUtils.paraRecChkNull(inRecord, "ORD_GP");
		    		szCUST_CD            = ydDaoUtils.paraRecChkNull(inRecord, "CUST_CD");
		    		szDEST_CD            = ydDaoUtils.paraRecChkNull(inRecord, "DEST_CD");
		    		szDLVRDD_RULE_DD     = ydDaoUtils.paraRecChkNull(inRecord, "DLVRDD_RULE_DD");
		    		szDEST_TEL_NO        = ydDaoUtils.paraRecChkNull(inRecord, "DEST_TEL_NO");
		    		szDIST_SHIPASSIGN_GP = ydDaoUtils.paraRecChkNull(inRecord, "DIST_SHIPASSIGN_GP");
	    			
		    		// 레코드에 추가
			    	recStockColumn.setField("ORD_YEOJAE_GP"     , szORD_YEOJAE_GP);
			    	recStockColumn.setField("ORD_NO"            , szORD_NO);
			    	recStockColumn.setField("ORD_DTL"           , szORD_DTL);
			    	recStockColumn.setField("ORD_GP"            , szORD_GP);
			    	recStockColumn.setField("CUST_CD"           , szCUST_CD);
			    	recStockColumn.setField("DEST_CD"           , szDEST_CD);
			    	recStockColumn.setField("YD_DLVRDD_RULE_DD" , szDLVRDD_RULE_DD);
			    	recStockColumn.setField("DEST_TEL_NO"       , szDEST_TEL_NO);
			    	recStockColumn.setField("DIST_SHIPASSIGN_GP", szDIST_SHIPASSIGN_GP);
		    		
		    		// 후판제품운송지시대기(YDDMR018)에는 항목이 더 있음
	    			if(YdConstant.DMYDR018.equals(sJmsTcCd)){
			    		szSHIPASSIGN_WORD_DATE  = ydDaoUtils.paraRecChkNull(inRecord, "SHIPASSIGN_WORD_DATE");
			    		szSHIPASSIGN_WORD_SEQNO = ydDaoUtils.paraRecChkNull(inRecord, "SHIPASSIGN_WORD_SEQNO");
			    		szSHIP_CD               = ydDaoUtils.paraRecChkNull(inRecord, "SHIP_CD");
			    		szSHIP_NAME             = ydDaoUtils.paraRecChkNull(inRecord, "SHIP_NAME");
			    		szSAILNO                = ydDaoUtils.paraRecChkNull(inRecord, "SAILNO");
			    		
			    		// 레코드에 추가
				    	recStockColumn.setField("SHIPASSIGN_WORD_DATE" , szSHIPASSIGN_WORD_DATE);
				    	recStockColumn.setField("SHIPASSIGN_WORD_SEQNO", szSHIPASSIGN_WORD_SEQNO);
				    	recStockColumn.setField("SHIP_CD"              , szSHIP_CD);
				    	recStockColumn.setField("SHIP_NAME"            , szSHIP_NAME);
				    	recStockColumn.setField("SAILNO"               , szSAILNO);
				    	recStockColumn.setField("PRE_AR_STAT_CD"       , ""); // 보관매출 운송지시 항목 취소로 무조건 셋팅.
				    	recStockColumn.setField("CAR_LOTID"       	   , ""); // 선별LOT편성정보 삭제
				    	
				    	
				    	//======================================================
						// 저장품제원 : 후판제품 L2 로 송신(YDY4L002)
						//======================================================
						JDTORecord recResult = null;
						recResult = JDTORecordFactory.getInstance().create();
						recResult.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고 L2전문
						recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
						recResult.setField("STL_NO"         , szSTL_NO);
						recResult.setField("YD_STK_COL_GP"  , "");
						recResult.setField("YD_STK_BED_NO"  , "");
						
						// 2021. 05. 17 전문생성 시점에 PT_PLATE_COMM에 진도코드가 바뀌지 않은 문제가 있어
						// 진도코드를 파라메터로 넘겨 처리함
						recResult.setField("CURR_PROG_CD"  , szCURR_PROG_CD);
						ydDelegate.sendMsg(recResult);		
						
						// 전사물류개선 2021. 4. 3
		    			if(PlateGdsYdUtil.isSendToEaiY9_stlNo(szSTL_NO) ){
		    				recResult.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
		    				ydDelegate.sendMsg(recResult);
		    			}
	    			}
	    			    			
	    			
	    	    	if(YdConstant.DMYDR016.equals(sJmsTcCd)){ 
	    	    		recStockColumn.setField("CAR_NO", "");
	    	    		recStockColumn.setField("CARD_NO", "");
	    	    	}
	    		}
	    		
				// 야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal = YdCommonUtils.getYdAimRtGp2(sJmsTcCd , sSTL_GP, szSTL_NO, szCURR_PROG_CD);		
				szYD_AIM_RT_GP = rVal[0];
				recStockColumn.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);

		    	if(YdConstant.DMYDR026.equals(sJmsTcCd) || YdConstant.DMYDR027.equals(sJmsTcCd) || YdConstant.DMYDR028.equals(sJmsTcCd)){
	    	    	ydUtils.displayRecord("출하취소 처리(" + sJmsTcCd + " : 보관지시 처리", recStockColumn);
		    		
	    	    	// 레코드 생성
			    	recStockColumn = JDTORecordFactory.getInstance().create();
			    	recStockColumn.setField("STL_NO", szSTL_NO);
		        	recStockColumn.setField("DEL_YN", "N");
		    		intRtnVal = ydStockDao.updYdStock(recStockColumn,0);	
					if(intRtnVal > 0){
	 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 성공 TC_CODE(" + sJmsTcCd + ")", YdConstant.DEBUG);
					}else if(intRtnVal == 0){
	 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 실패 TC_CODE(" + sJmsTcCd + ")", YdConstant.ERROR);
						return false;
					}	    	    	
		    	} else {
	    	    	ydUtils.displayRecord("출하취소 처리(" + sJmsTcCd + " : 받은전문 그대로 저장품 업데이트 ", recStockColumn);
	    	    	
		    		//  저장품 업데이트 처리 (업데이트 쳐야 될 항목은 업데이트 클리어될 항목은 클리어해야 하기 위해 기본쿼리로 처리)
	 	    		nRet = ydStockDao.updYdStock(recStockColumn, 0);	
	 				if(nRet > 0){
	 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 성공 TC_CODE(" + sJmsTcCd + ")", YdConstant.DEBUG);
	
	 				}else if(intRtnVal == 0){
	 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 실패 TC_CODE(" + sJmsTcCd + ")", YdConstant.ERROR);
	 					return false;
	 				}	    		
		    	}
	    	} else if(YdConstant.DMYDR011.equals(sJmsTcCd) || YdConstant.DMYDR012.equals(sJmsTcCd) || YdConstant.DMYDR020.equals(sJmsTcCd) || YdConstant.DMYDR060.equals(sJmsTcCd) ||
	    			  YdConstant.DMYDR021.equals(sJmsTcCd) || YdConstant.DMYDR022.equals(sJmsTcCd) || YdConstant.DMYDR070.equals(sJmsTcCd) || YdConstant.DMYDR073.equals(sJmsTcCd) ||
	    			  YdConstant.DMYDR025.equals(sJmsTcCd)){
	    		
	    		//==================================================================
		    	// 전문에 따른 분기 (재료번호 N개 : STL_NO1 ... STL_NO20)
				//==================================================================
	    		
	    		if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)){
	    			// 일단 재료정보 초기화는 하지 않음.
	    			// 2016.04.06 윤재광
	    		}else{
	    			// 전문에서 공통적인 항목의 값을 추출
		    		szYD_GP           = ydDaoUtils.paraRecChkNull(inRecord, "YD_GP");
		    		szSTL_APPEAR_GP   = ydDaoUtils.paraRecChkNull(inRecord, "STL_APPEAR_GP");
		    		szTRANS_ORD_DATE  = ydDaoUtils.paraRecChkNull(inRecord, "TRANS_ORD_DT");
		    		szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(inRecord, "TRANS_ORD_SEQNO");
		    		szCANCEL_YN       = ydDaoUtils.paraRecChkNull(inRecord, "CANCEL_YN");
	
		    		intYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNullInt(inRecord,"YD_EQP_WRK_SH");
		    		
		    		// 전문에서 반복적인 재료번호 추출 
		    		for(int i=0; i<intYD_EQP_WRK_SH; i++){
			    		// 레코드 생성
				    	recStockColumn = JDTORecordFactory.getInstance().create();
	
						// 전문에서 재료번호를 추출
		    			szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO" + (i+1));
			    		
		    			if("".equals(szSTL_NO)) break;
		    			
		    			// 레코드에 추가
		    			recStockColumn.setField("STL_NO"			, szSTL_NO);
	    				recStockColumn.setField("TRANS_ORD_DATE" 	, "");
				    	recStockColumn.setField("TRANS_ORD_SEQNO"	, "");	    	    	
		    	    	recStockColumn.setField("CAR_NO"			, "");
		    	    	recStockColumn.setField("CARD_NO"			, "");
		    	    	recStockColumn.setField("YD_AIM_RT_GP"		, "NB");
		    	    	
		    	    	//  저장품 업데이트 처리 
		 	    		nRet = ydStockDao.updYdStock(recStockColumn);	
		 				if(nRet > 0){
		 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 성공 TC_CODE(" + sJmsTcCd + ")", YdConstant.DEBUG);
		 				}else if(intRtnVal == 0){
		 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 실패 TC_CODE(" + sJmsTcCd + ") STL_NO(" + szSTL_NO + ")", YdConstant.ERROR);
		 					// 업데이트 시 에러가 발생해도 N건처리를 위해 continue
		 					continue;
		 				}	
		    		}	    	
	    		}
				//=========================================================
				//	차량스케줄 삭제 및 차량 POINT Clear
				//=========================================================
				ydUtils.putLog(szSessionName, szMethodName, "차량스케줄삭제 및 차량Point Clear 시작", YdConstant.DEBUG);
//PIDEV_S :병행가동용:PI_YD
				inRecord.setField("PI_YD",    	szYD_GP);				
				String szRtnMsg = YdCommonUtils.delCarSchNCarPointForDist(inRecord, szMethodName);
				ydUtils.putLog(szSessionName, szMethodName, "차량스케줄삭제 및 차량Point Clear 완료 - " + szRtnMsg, YdConstant.DEBUG);
				
	    	} else if(YdConstant.DMYDR023.equals(sJmsTcCd)){
	    		// 레코드 생성
		    	recStockColumn = JDTORecordFactory.getInstance().create();

		    	// 전문에서 공통적인 항목의 값을 추출
	    		szYD_GP           = ydDaoUtils.paraRecChkNull(inRecord, "YD_GP");
	    		szTRANS_ORD_DATE  = ydDaoUtils.paraRecChkNull(inRecord, "TRANS_ORD_DT");
	    		szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(inRecord, "TRANS_ORD_SEQNO");
				szCAR_NO          = ydDaoUtils.paraRecChkNull(inRecord, "CAR_NO");
				szCARD_NO         = ydDaoUtils.paraRecChkNull(inRecord, "CARD_NO");	    		
				szARR_WLOC_CD     = ydDaoUtils.paraRecChkNull(inRecord, "ARR_WLOC_CD");
				szCURR_PROG_CD    = ydDaoUtils.paraRecChkNull(inRecord, "CURR_PROG_CD");
				szCANCEL_YN       = ydDaoUtils.paraRecChkNull(inRecord, "CANCEL_YN");    	
				
		    	// 레코드에 추가
		    	recStockColumn.setField("TRANS_ORD_DATE" , szTRANS_ORD_DATE);
				recStockColumn.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);				
    			recStockColumn.setField("ARR_WLOC_CD"    , szARR_WLOC_CD);
    			recStockColumn.setField("STL_PROG_CD"    , szCURR_PROG_CD);	    	
    			
    			// 운송지시날짜와 운송지시순번으로  재료번호 조회
    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
    			recPara  = JDTORecordFactory.getInstance().create();
    			recPara.setField("TRANS_ORD_DT" , szTRANS_ORD_DATE);
    			recPara.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
    			nRet = ydStockDao.getYdStock(recPara, rsResult, 113);
    			if(nRet <= 0){
    				ydUtils.putLog(szSessionName, szMethodName, "운송지시일자와 운송지시순번으로 재료번호 조회 실패", YdConstant.ERROR);
    			} else {
    				ydUtils.putLog(szSessionName, szMethodName, "운송지시일자와 운송지시순번으로 재료번호 조회 성공", YdConstant.DEBUG);
    				
    				rsResult.first();
    				recGetVal = rsResult.getRecord();

    				// 차량번호와 카드번호 클리어
    				recStockColumn.setField("STL_NO", ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO"));
        			recStockColumn.setField("CAR_NO" , "");
        			recStockColumn.setField("CARD_NO", "");	
	    	    	
	    	    	//  저장품 업데이트 처리 
	 	    		nRet = ydStockDao.updYdStock(recStockColumn, 0);	
	 				if(nRet > 0){
	 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 성공 TC_CODE(" + sJmsTcCd + ")", YdConstant.DEBUG);

	 				}else if(intRtnVal == 0){
	 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 실패 TC_CODE(" + sJmsTcCd + ") STL_NO(" + szSTL_NO + ")", YdConstant.ERROR);
	 				}	 				
    			}
	    	}
	    	
	    	ydUtils.putLog(szSessionName, szMethodName, "############# 일관제철 출하취소 전문 END ###############", YdConstant.DEBUG);
	    }catch(DAOException daoe) {
               throw daoe;
        }catch(Exception e) {
	        throw new EJBServiceException(e);
        }
		return true;
	}
	
	/**
	 * 오퍼레이션명 : 일관제철 C열연코일야드  출하전문 취소처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean coilReceiveCancel(JDTORecord inRecord) {
		YdStockDao ydStockDao            = new YdStockDao();
		YdCrnSchDao ydCrnSchDao          = new YdCrnSchDao();
		
		JDTORecord recStockColumn        = null;
		JDTORecord recSch                = JDTORecordFactory.getInstance().create();
		JDTORecord recPara               = JDTORecordFactory.getInstance().create();
		JDTORecord recGetVal             = null;
		JDTORecordSet rsResult           = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet	outRecSet		 = JDTORecordFactory.getInstance().createRecordSet("");
 
		// 변수선언
		String szMethodName              ="receiveCancel";
		String[] rVal                    = new String[2];		
		String sSTL_GP                   ="";
		String szYD_AIM_RT_GP            = "";		
		String szSTL_APPEAR_GP           = "";
		String szSTL_NO                  = "";
		String szCURR_PROG_CD            = ""; 
		String szWO_CAR_PLNT_PROC_CD     = "";
		String szFRTOMOVE_ORD_DATE       = "";
		String szURGENT_FRTOMOVE_WORD_GP = "";
		String szORD_YEOJAE_GP           = "";     
		String szORD_NO                  = "";            
		String szORD_DTL                 = "";       
		String szORD_GP                  = "";  
		String szCUST_CD                 = "";
		String szDEST_CD                 = "";
		String szDLVRDD_RULE_DD          = "";
		String szDEST_TEL_NO             = "";
		String szDIST_SHIPASSIGN_GP      = "";
		String szSHIPASSIGN_WORD_DATE    = ""; 
		String szSHIPASSIGN_WORD_SEQNO   = ""; 
		String szSHIP_CD                 = ""; 
		String szSHIP_NAME               = ""; 
		String szSAILNO                  = ""; 
		String szTRANS_ORD_DATE          = "";
		String szTRANS_ORD_SEQNO         = "";
		String szRSHP_HOLD_NO            = "";
		String szCAR_NO                  = "";
		String szCARD_NO                 = "";     
		String szARR_WLOC_CD             = ""; 
		int nRet                         = 0;
		YdWrkbookDao ydWrkbookDao    = new YdWrkbookDao();
		int intRtnVal                    = 0;
		EJBConnector ejbConn = null;
		JDTORecord[] 	inRecordarr   		= null;	
		ydUtils.putLog(szSessionName, szMethodName, "############# 일관제철 C열연야드  출하취소 전문 START ###############", YdConstant.DEBUG);

		String sJmsTcCd = StringHelper.evl(inRecord.getFieldString("JMS_TC_CD"), StringHelper.evl(inRecord.getFieldString("TC_CODE"),""));		
		ydUtils.putLog(szSessionName, szMethodName, "==>>일관제철 내부수신 전문[" + sJmsTcCd + "]", YdConstant.DEBUG);
		
		ydUtils.displayRecord("출하 전문취소 수신전문 항목 출력(" + sJmsTcCd + ")", inRecord);
		
		
	    try {	        
	    	/*
				DMYDR008	코일제품반납확정			STL_NO
				DMYDR011	코일제품고간이송지시		STL_NO1 ~ STL_NO20
				DMYDR014	코일제품목전				STL_NO
				DMYDR020	코일제품운송지시 			STL_NO1 ~ STL_NO20
				DMYDR023	코일제품상차지시			CAR_NO, CARD_NO
				DMYDR025	임가공이송상차지시			STL_NO1 ~ STL_NO20
				DMYDR027	코일제품보관지시			STL_NO

				
				DMYDR008	코일제품반납대기		1.저장품 이동 조건 변경
				DMYDR014	코일제품목전			1.저장품 이동 조건 변경
				DMYDR030	코일제품출하완료              1.저장품 이동 조건 변경
				DMYDR011	코일제품고간이송지시	1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함),저장품 이동 조건 변경
				DMYDR020	코일제품운송지시              1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함) ,저장품 이동 조건 변경
				DMYDR023	코일제품상차지시		1.크레인 스케줄취소 ,2 작업예약취소 , 3. 카드번호삭제,저장품 이동 조건 변경
				DMYDR025	임가공이송상차지시		1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함),저장품 이동 조건 변경
				DMYDR027	코일제품보관지시		KEEPSTOCK_STL_YN= ''
	    	 */

	    	sSTL_GP = "C";

	    	
	    	//==================================================================
    		//1.크레인 스케줄취소 ,2 작업예약취소
    		//==================================================================	    	
	    	if(YdConstant.DMYDR020.equals(sJmsTcCd) ||YdConstant.DMYDR070.equals(sJmsTcCd) || YdConstant.DMYDR073.equals(sJmsTcCd) ||
    		   YdConstant.DMYDR023.equals(sJmsTcCd) ){
	    		
	    		
	    		//차량스케줄 삭제 및 차량 POINT Clear
				ydUtils.putLog(szSessionName, szMethodName, "차량스케줄삭제 및 차량Point Clear 시작", YdConstant.DEBUG);
//PIDEV_S :병행가동용:PI_YD
				String szYD_GP = ydDaoUtils.paraRecChkNull(inRecord, "YD_GP");
				inRecord.setField("PI_YD",    	szYD_GP);				
				String szRtnMsg = YdCommonUtils.delCarSchNCarPointForDist(inRecord, szMethodName);
				ydUtils.putLog(szSessionName, szMethodName, "차량스케줄삭제 및 차량Point Clear 완료 - " + szRtnMsg, YdConstant.DEBUG);
				
				//1.크레인 스케줄취소 ,2 작업예약취소
	    		recStockColumn = JDTORecordFactory.getInstance().create();
	    		recStockColumn.setField("TRANS_ORD_DT"		, ydDaoUtils.paraRecChkNull(inRecord, "TRANS_ORD_DT"));
	    		recStockColumn.setField("TRANS_ORD_SEQNO"	, ydDaoUtils.paraRecChkNull(inRecord, "TRANS_ORD_SEQNO"));
	    		intRtnVal =ydCrnSchDao.getYdCrnsch(recStockColumn, outRecSet, 306);
	    		if(intRtnVal > 0){
 					ydUtils.putLog(szSessionName, szMethodName, "스케줄  취소대상이 존재 함 TC_CODE(" + sJmsTcCd + ")", YdConstant.DEBUG);
 					
 					
 		    		for(int Loop_i = 1; Loop_i <= outRecSet.size(); Loop_i++) {
 		    			outRecSet.absolute(Loop_i);
 		    			recSch = JDTORecordFactory.getInstance().create();
 		    			recSch = outRecSet.getRecord();
 			    		
 						//TC CODE
 						//크레인 스케줄 삭제
 						recPara.setField("JMS_TC_CD"	, "YDYD9003");					
 						recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recSch, "YD_CRN_SCH_ID")); //스케줄 ID					
 						recPara.setField("YD_SCH_CD"	, ydDaoUtils.paraRecChkNull(recSch, "YD_SCH_CD")); //스케줄 CODE					
 						recPara.setField("DEL_YN"		, "Y"); //삭제유무 					
 						recPara.setField("MODIFIER"		, "JSPUSER");	//수정자 		
 						
 						ejbConn = new EJBConnector("default", this);
 						ejbConn.trx("YdSimSeEJB", "wrkCncl", recPara);
 						
 		    		}
 		    		
				}else if(intRtnVal == 0){
 					ydUtils.putLog(szSessionName, szMethodName, "스케줄  취소대상이 존재 안함 작업예약 삭제  TC_CODE(" + sJmsTcCd + ")", YdConstant.DEBUG);
 					
 					//크레인 작업예약 대상 검색
 					recStockColumn = JDTORecordFactory.getInstance().create();
 		    		recStockColumn.setField("TRANS_ORD_DT"		, ydDaoUtils.paraRecChkNull(inRecord, "TRANS_ORD_DT"));
 		    		recStockColumn.setField("TRANS_ORD_SEQNO"	, ydDaoUtils.paraRecChkNull(inRecord, "TRANS_ORD_SEQNO"));
 		    		
 		    		/*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookTransSeq*/
 					intRtnVal = ydWrkbookDao.getYdWrkbook(recStockColumn, rsResult, 507);			
 					if(intRtnVal <= 0){ 		 
 						ydUtils.putLog(szSessionName, szMethodName,  "[오류발생]: 작업예약조회 중 오류 ["+intRtnVal+"]", YdConstant.DEBUG);
 	 
 					}else{
 					
						for (int i = 0; i < intRtnVal; i++) {
							recPara = rsResult.getRecord(i);
	
							inRecordarr = new JDTORecord[1];
	
							inRecordarr[0] = JDTORecordFactory.getInstance().create();
							inRecordarr[0].setField("YD_WBOOK_ID" , ydDaoUtils.paraRecChkNull(recPara , "YD_WBOOK_ID"));
							inRecordarr[0].setField("YD_USER_ID" , sJmsTcCd);
							
							//크레인 작업예약 삭제
							ejbConn = new EJBConnector("default" , "CoilJspSeEJB" , this);
							String rtnMsg = (String) ejbConn.trx("delYdWrkbook" , new Class[]{JDTORecord[].class} , new Object[]{inRecordarr});
						}
 					}
	
				}	 
	    	}
	    	
	    	//================================================================================================
	    	// 출하 취소전문들 처리 수정
	    	// 저장품에 없는 항목은 주석으로 걸어 두었음
	    	//================================================================================================
	    	
	    	if(YdConstant.DMYDR008.equals(sJmsTcCd) ||
    			YdConstant.DMYDR014.equals(sJmsTcCd) ||
    			YdConstant.DMYDR027.equals(sJmsTcCd) ||
    			YdConstant.DMYDR030.equals(sJmsTcCd)){

	    		//==================================================================
		    	// 전문에 따른 분기 (재료번호 1개 : STL_NO)
				//==================================================================

	    		
	    		// 레코드 생성
		    	recStockColumn = JDTORecordFactory.getInstance().create();

		    	
	    		// 전문에서 공통적인 항목의 값을 추출
	    		szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(inRecord, "STL_APPEAR_GP");
	    		szSTL_NO        = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");
	    		szCURR_PROG_CD  = ydDaoUtils.paraRecChkNull(inRecord, "CURR_PROG_CD");
	    		
	    		
	    		// 레코드에 추가
		    	recStockColumn.setField("STL_APPEAR_GP", szSTL_APPEAR_GP);
		    	recStockColumn.setField("STL_NO"       , szSTL_NO);
				recStockColumn.setField("STL_PROG_CD"  , szCURR_PROG_CD);
				
				
	    		// TC에 따른 변동항목 추출
	    		if(YdConstant.DMYDR008.equals(sJmsTcCd)){
	    			szWO_CAR_PLNT_PROC_CD     = ydDaoUtils.paraRecChkNull(inRecord, "WO_CAR_PLNT_PROC_CD");
		    		szFRTOMOVE_ORD_DATE       = ydDaoUtils.paraRecChkNull(inRecord, "FRTOMOVE_ORD_DATE");
		    		szURGENT_FRTOMOVE_WORD_GP = ydDaoUtils.paraRecChkNull(inRecord, "URGENT_FRTOMOVE_WORD_GP");    			

	    		
		    		// 레코드에 추가
			    	recStockColumn.setField("WO_CAR_PLNT_PROC_CD"    , szWO_CAR_PLNT_PROC_CD);
			    	recStockColumn.setField("FRTOMOVE_ORD_DATE"      , szFRTOMOVE_ORD_DATE);
			    	recStockColumn.setField("URGENT_FRTOMOVE_WORD_GP", szURGENT_FRTOMOVE_WORD_GP);	 
			    	
	    		} else if(YdConstant.DMYDR014.equals(sJmsTcCd) || 
	    				  YdConstant.DMYDR027.equals(sJmsTcCd) ){	
	    			
		    		szORD_YEOJAE_GP      = ydDaoUtils.paraRecChkNull(inRecord, "ORD_YEOJAE_GP");
		    		szORD_NO             = ydDaoUtils.paraRecChkNull(inRecord, "ORD_NO");
		    		szORD_DTL            = ydDaoUtils.paraRecChkNull(inRecord, "ORD_DTL");
		    		szORD_GP             = ydDaoUtils.paraRecChkNull(inRecord, "ORD_GP");
		    		szCUST_CD            = ydDaoUtils.paraRecChkNull(inRecord, "CUST_CD");
		    		szDEST_CD            = ydDaoUtils.paraRecChkNull(inRecord, "DEST_CD");
		    		szDLVRDD_RULE_DD     = ydDaoUtils.paraRecChkNull(inRecord, "DLVRDD_RULE_DD");
		    		szDEST_TEL_NO        = ydDaoUtils.paraRecChkNull(inRecord, "DEST_TEL_NO");
		    		szDIST_SHIPASSIGN_GP = ydDaoUtils.paraRecChkNull(inRecord, "DIST_SHIPASSIGN_GP");
	    			
		    		
		    		// 레코드에 추가
			    	recStockColumn.setField("ORD_YEOJAE_GP"     , szORD_YEOJAE_GP);
			    	recStockColumn.setField("ORD_NO"            , szORD_NO);
			    	recStockColumn.setField("ORD_DTL"           , szORD_DTL);
			    	recStockColumn.setField("ORD_GP"            , szORD_GP);
			    	recStockColumn.setField("CUST_CD"           , szCUST_CD);
			    	recStockColumn.setField("DEST_CD"           , szDEST_CD);
			    	recStockColumn.setField("YD_DLVRDD_RULE_DD" , szDLVRDD_RULE_DD);
			    	recStockColumn.setField("DEST_TEL_NO"       , szDEST_TEL_NO);
			    	recStockColumn.setField("DIST_SHIPASSIGN_GP", szDIST_SHIPASSIGN_GP);
		    		
	    		}
	    		
	    		
	    		// 전문에서 공통적인 항목의 값을 추출
	    		szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(inRecord, "STL_APPEAR_GP");
	    		szSTL_NO        = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");
	    		szCURR_PROG_CD  = ydDaoUtils.paraRecChkNull(inRecord, "CURR_PROG_CD");
			    	
				// 야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal = YdCommonUtils.getYdAimRtGp2(sJmsTcCd , sSTL_GP, szSTL_NO, szCURR_PROG_CD);		
				szYD_AIM_RT_GP = rVal[0];
				recStockColumn.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);
	
		    	if(YdConstant.DMYDR027.equals(sJmsTcCd)){
	    	    	ydUtils.displayRecord("출하취소 처리(" + sJmsTcCd + " : 보관지시 처리", recStockColumn);
		    		
	    	    	// . . . . . 보관지시처리
	    	    	// 정종균대리가 보관지시구분 KEEPSTOCK_STL_YN 항목에대해 확인 중...
	
	    	    	// 레코드 생성
			    	recStockColumn = JDTORecordFactory.getInstance().create();
			    	recStockColumn.setField("STL_NO", szSTL_NO);
		        	recStockColumn.setField("DEL_YN", "N");
//SJH추가
		        	recStockColumn.setField("SCARFING_YN", "N");
		        	
		        	
		    		intRtnVal = ydStockDao.updYdStock(recStockColumn,0);	
					if(intRtnVal > 0){
	 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 성공 TC_CODE(" + sJmsTcCd + ")", YdConstant.DEBUG);
					}else if(intRtnVal == 0){
	 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 실패 TC_CODE(" + sJmsTcCd + ")", YdConstant.ERROR);
						return false;
					}	    	    	
		    	} else {
	    	    	ydUtils.displayRecord("출하취소 처리(" + sJmsTcCd + " : 받은전문 그대로 저장품 업데이트 ", recStockColumn);
	    	    	
		    		//  저장품 업데이트 처리 (업데이트 쳐야 될 항목은 업데이트 클리어될 항목은 클리어해야 하기 위해 기본쿼리로 처리)
	 	    		nRet = ydStockDao.updYdStock(recStockColumn, 0);	
	 				if(nRet > 0){
	 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 성공 TC_CODE(" + sJmsTcCd + ")", YdConstant.DEBUG);
	
	 				}else if(intRtnVal == 0){
	 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 실패 TC_CODE(" + sJmsTcCd + ")", YdConstant.ERROR);
	 					return false;
	 				}	    		
		    	}
		    	
	    	} else if(YdConstant.DMYDR011.equals(sJmsTcCd)  ||YdConstant.DMYDR020.equals(sJmsTcCd)
	    			||YdConstant.DMYDR070.equals(sJmsTcCd) || YdConstant.DMYDR073.equals(sJmsTcCd) 
	    			||YdConstant.DMYDR060.equals(sJmsTcCd) || YdConstant.DMYDR025.equals(sJmsTcCd)){
	    		//==================================================================
		    	// 전문에 따른 분기 (재료번호 N개 : STL_NO1 ... STL_NO20)
				//==================================================================

	    		
    			// 전문에서 공통적인 항목의 값을 추출
	    		szSTL_APPEAR_GP   = ydDaoUtils.paraRecChkNull(inRecord, "STL_APPEAR_GP");
	    		szTRANS_ORD_DATE  = ydDaoUtils.paraRecChkNull(inRecord, "TRANS_ORD_DT");
	    		szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(inRecord, "TRANS_ORD_SEQNO");

	    		
	    		// 전문에서 반복적인 재료번호 추출 
	    		for(int i=0; i<20; i++){
		    		// 레코드 생성
			    	recStockColumn = JDTORecordFactory.getInstance().create();

			    	
			    	// 이미 읽어온 공통적인 값은 레코드에 추가
			    	recStockColumn.setField("STL_APPEAR_GP"  , szSTL_APPEAR_GP);
			    	recStockColumn.setField("TRANS_ORD_DATE" , szTRANS_ORD_DATE);
					recStockColumn.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);	    		

					
					// 전문에서 재료번호를 추출
	    			szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO" + (i+1));
		    		
	    			if("".equals(szSTL_NO)) break;
	    			
	    			// 레코드에 추가
	    			recStockColumn.setField("STL_NO", szSTL_NO);
    				
	    			
	    			if(YdConstant.DMYDR020.equals(sJmsTcCd)||YdConstant.DMYDR070.equals(sJmsTcCd) || YdConstant.DMYDR073.equals(sJmsTcCd) ||YdConstant.DMYDR060.equals(sJmsTcCd)){
						// 전문에서 항목값들 추출	    				
	    				szDIST_SHIPASSIGN_GP    = ydDaoUtils.paraRecChkNull(inRecord, "DIST_SHIPASSIGN_GP" + (i+1));
	    				szSHIPASSIGN_WORD_DATE  = ydDaoUtils.paraRecChkNull(inRecord, "SHIPASSIGN_WORD_DATE" + (i+1));
	    				szSHIPASSIGN_WORD_SEQNO = ydDaoUtils.paraRecChkNull(inRecord, "SHIPASSIGN_WORD_SEQNO" + (i+1));
	    				szSHIP_CD               = ydDaoUtils.paraRecChkNull(inRecord, "SHIP_CD");
	    				szSHIP_NAME             = ydDaoUtils.paraRecChkNull(inRecord, "SHIP_NAME");
	    				szRSHP_HOLD_NO          = ydDaoUtils.paraRecChkNull(inRecord, "RSHP_HOLD_NO");
	    				szSAILNO                = ydDaoUtils.paraRecChkNull(inRecord, "SAILNO");
	    				szCURR_PROG_CD          = ydDaoUtils.paraRecChkNull(inRecord, "CURR_PROG_CD");
	    				
	    				// 레코드에 추가
		    			recStockColumn.setField("DIST_SHIPASSIGN_GP"   , szDIST_SHIPASSIGN_GP);
		    			recStockColumn.setField("SHIPASSIGN_WORD_DATE" , szSHIPASSIGN_WORD_DATE);
		    			recStockColumn.setField("SHIPASSIGN_WORD_SEQNO", szSHIPASSIGN_WORD_SEQNO);
		    			recStockColumn.setField("SHIP_CD"              , szSHIP_CD);
		    			recStockColumn.setField("SHIP_NAME"            , szSHIP_NAME);
		    			recStockColumn.setField("RSHP_HOLD_NO"         , szRSHP_HOLD_NO);
		    			recStockColumn.setField("SAILNO"               , szSAILNO);
		    			recStockColumn.setField("STL_PROG_CD"          , szCURR_PROG_CD);

	    			} else if(YdConstant.DMYDR025.equals(sJmsTcCd)){
						// 전문에서 항목값들 추출	    				
	    				szCAR_NO        = ydDaoUtils.paraRecChkNull(inRecord, "CAR_NO");
	    				szCARD_NO       = ydDaoUtils.paraRecChkNull(inRecord, "CARD_NO");
	    				
	    				// 레코드에 추가
		    			recStockColumn.setField("CAR_NO"       , szCAR_NO);
		    			recStockColumn.setField("CARD_NO"      , szCARD_NO);
	    			
	    			}	    			
		    		
					// 야드목표행선지구분 (제품구분-S:SLAB, C:COIL ,P:후판)
	    	    	if(YdConstant.DMYDR011.equals(sJmsTcCd)|| YdConstant.DMYDR025.equals(sJmsTcCd)){  
	    	    		// 처리 없음
	    	    	}else {
						rVal = YdCommonUtils.getYdAimRtGp2(sJmsTcCd , sSTL_GP, szSTL_NO, szCURR_PROG_CD);		
						szYD_AIM_RT_GP = rVal[0];
						recStockColumn.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);
	    	 	    }
	    	    	
	    	    	
		    		// 운송지시일자(TRANS_ORD_DATE)와 운송지시순번(TRANS_ORD_SEQNO)은 클리어가 되어야 함
			    	recStockColumn.setField("TRANS_ORD_DATE" , "");
			    	recStockColumn.setField("TRANS_ORD_SEQNO", "");	    	    	
	    	    	
			    	ydUtils.displayRecord("[Index : " + i + " ] 출하취소 처리(" + sJmsTcCd + " : 운송지시일자와 운송지시순번은 클리어 나머지는 받은 전문 그대로 저장품 업데이트 ", recStockColumn);

		    	    	
	    	    	//C열연취소 인경우 
	    	    	if(YdConstant.DMYDR020.equals(sJmsTcCd)||YdConstant.DMYDR070.equals(sJmsTcCd) || YdConstant.DMYDR073.equals(sJmsTcCd) ||YdConstant.DMYDR060.equals(sJmsTcCd)){
	    	    		recStockColumn.setField("CAR_NO", "");
	    	    		recStockColumn.setField("CARD_NO", "");
	    	    		recStockColumn.setField("YD_STK_BED_NO", "");
	    	    		recStockColumn.setField("YD_CAR_UPP_LOC_CD", "");
	    	    		recStockColumn.setField("YD_RULE_PL_RS_GP", "");
	    	    	}
	    	    	
	    	    	
	    	    	//  저장품 업데이트 처리 
	 	    		nRet = ydStockDao.updYdStock(recStockColumn);	
	 				if(nRet > 0){
	 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 성공 TC_CODE(" + sJmsTcCd + ")", YdConstant.DEBUG);

	 				}else if(intRtnVal == 0){
	 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 실패 TC_CODE(" + sJmsTcCd + ") STL_NO(" + szSTL_NO + ")", YdConstant.ERROR);

	 					// 업데이트 시 에러가 발생해도 N건처리를 위해 continue
	 					continue;
	 				}	
	    		}	    	
	    	} 

	    	
	    	if(YdConstant.DMYDR023.equals(sJmsTcCd)){
	    		// 레코드 생성
		    	recStockColumn = JDTORecordFactory.getInstance().create();

		    	// 전문에서 공통적인 항목의 값을 추출
	    		szTRANS_ORD_DATE  = ydDaoUtils.paraRecChkNull(inRecord, "TRANS_ORD_DT");
	    		szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(inRecord, "TRANS_ORD_SEQNO");
				szCAR_NO          = ydDaoUtils.paraRecChkNull(inRecord, "CAR_NO");
				szCARD_NO         = ydDaoUtils.paraRecChkNull(inRecord, "CARD_NO");	    		
				szARR_WLOC_CD     = ydDaoUtils.paraRecChkNull(inRecord, "ARR_WLOC_CD");
				szCURR_PROG_CD    = ydDaoUtils.paraRecChkNull(inRecord, "CURR_PROG_CD");
				
		    	// 레코드에 추가
		    	recStockColumn.setField("TRANS_ORD_DATE" , szTRANS_ORD_DATE);
				recStockColumn.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);				
    			recStockColumn.setField("ARR_WLOC_CD"    , szARR_WLOC_CD);
    			recStockColumn.setField("STL_PROG_CD"    , szCURR_PROG_CD);	    	
    			
    			// 운송지시날짜와 운송지시순번으로  재료번호 조회
    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
    			recPara  = JDTORecordFactory.getInstance().create();
    			recPara.setField("TRANS_ORD_DT" 	, szTRANS_ORD_DATE);
    			recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
    			nRet = ydStockDao.getYdStock(recPara, rsResult, 113);
    			if(nRet <= 0){
    				ydUtils.putLog(szSessionName, szMethodName, "운송지시일자와 운송지시순번으로 재료번호 조회 실패", YdConstant.ERROR);
    			} else {
    				ydUtils.putLog(szSessionName, szMethodName, "운송지시일자와 운송지시순번으로 재료번호 조회 성공", YdConstant.DEBUG);

    				
    				for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
    					rsResult.absolute(Loop_i);
    					recGetVal = JDTORecordFactory.getInstance().create();
    	    			recGetVal = rsResult.getRecord();
    	    			
	    				// 차량번호와 카드번호 클리어
	    				recStockColumn.setField("STL_NO"	, ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO"));
	        			recStockColumn.setField("CAR_NO" 	, "");
	        			recStockColumn.setField("CARD_NO"	, "");	
		    	    	
		    	    	//  저장품 업데이트 처리 
		 	    		nRet = ydStockDao.updYdStock(recStockColumn, 0);	
		 				if(nRet > 0){
		 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 성공 TC_CODE(" + sJmsTcCd + ")", YdConstant.DEBUG);
	
		 				}else if(intRtnVal == 0){
		 					ydUtils.putLog(szSessionName, szMethodName, "출하 전문 취소작업 실패 TC_CODE(" + sJmsTcCd + ") STL_NO(" + szSTL_NO + ")", YdConstant.ERROR);
		 				}	 	
    				}
    			}
	    	}
	    	
	    	
	    	ydUtils.putLog(szSessionName, szMethodName, "############# 일관제철 출하취소 전문 END ###############", YdConstant.DEBUG);
	    }catch(DAOException daoe) {
               throw daoe;
        }catch(Exception e) {
	        throw new EJBServiceException(e);
        }
		return true;
	}
	
	/**
	 * 슬라브진행변경 (PMYDJ003) - 2010.01.25 권오창
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procSlabProgSync(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		YdCodeMapping ydCodeMapping             = new YdCodeMapping();
		
		// 변수 선언
		String szMethodName 					= "procSlabProgSync";
		String szMsg 							= "";
		String szOperationName                  = "슬라브진행변경 (PMYDJ003)";
		String szRcvTcCode                      = null;
		String szSTL_NO                         = "";
		
		// 전문받아서 szRcvTcCode에 대입
		szRcvTcCode = ydUtils.getTcCode(inRecord);
		if(szRcvTcCode == null){
			szMsg = "[ERROR] " + szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[공정계획] 슬라브진행변경(PMYDJ003) 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			// 수신항목 - 재료번호
			szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");

			// 재료번호로 주편/슬라브 조회하여 저장품에 업데이트
			ydCodeMapping.getMappingCommonField(szRcvTcCode, szSTL_NO);
			
			szMsg = "[공정계획] 슬라브진행변경(PMYDJ003) 수신 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
		}catch(Exception e){
			szMsg = "[슬라브진행변경 (PMYDJ003)] Exception Error:" + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} 

		szMsg = "슬라브진행변경  등록(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	}		
	
	/**
	 * 후판제품출하 변경정보수신 (DMYDR047)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procStlFrtMoveCancel(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		YdStockDao ydStockDao = new YdStockDao();
		
		JDTORecord	recInTemp					= null;
		// 변수 선언
		String szMethodName 					= "procStlFrtMoveCancel";
		String szMsg 							= "";
		String szOperationName                  = "후판제품출하 변경정보수신 (DMYDR047)";
		String szRcvTcCode                      = null;
		
		int intYD_EQP_WRK_SH					= 0;
		String szWRK_GBN						= "";
		String szSTL_NO                         = "";
		String szSTL_NM                         = "";
		
		int intRtnVal							= 0;
		
		// 전문받아서 szRcvTcCode에 대입
		szRcvTcCode = ydUtils.getTcCode(inRecord);
		if(szRcvTcCode == null){
			szMsg = "[ERROR] " + szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 후판제품선별LOT편성정보 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//수신한 전문값******************************************************************************************
			/*
			WRK_GBN 		1 - 전화번호 변경, 2 - 이후 발생할 수 있는 변경항목(추후사용)
			YD_EQP_WRK_SH	야드설비작업매수
			STL_NO1			재료번호1
			~
			STL_NO20		재료번호2
			*/
			
			//****************************************************************************************************
			intYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNullInt(inRecord,"YD_EQP_WRK_SH");
			szWRK_GBN 			= ydDaoUtils.paraRecChkNull(inRecord,"WRK_GBN");
			
			if("".equals(szWRK_GBN)){
				szMsg="[" + szOperationName + "] szWRK_GBN IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			if(intYD_EQP_WRK_SH<=0){
				szMsg="[" + szOperationName + "] intYD_EQP_WRK_SH IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} 
			
			recInTemp 	= JDTORecordFactory.getInstance().create();
			
			szMsg="[" + szOperationName + "]["+intYD_EQP_WRK_SH+"]매수에 대한  저장품 업데이트 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			for(int i = 1 ; i<= intYD_EQP_WRK_SH; i++){
				
				szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i);
				szSTL_NM = ydDaoUtils.paraRecChkNull(inRecord,"STL_NM"+i);
				
				recInTemp.setField("STL_NO", 				szSTL_NO);
				
				if("1".equals(szWRK_GBN)){//목적지 전화번호 변경
					
					recInTemp.setField("DEST_TEL_NO", 		szSTL_NM);
					intRtnVal = ydStockDao.updYdStockTelInfo(recInTemp);
					if(intRtnVal <= 0){
						szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 업데이트 시 오류발생 - 반환값 : " +intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
				}
			}  
			
		}catch(Exception e){
			szMsg = "[후판제품출하 변경정보수신 (DMYDR047)] Exception Error:" + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		} 

		szMsg = "후판제품출하 변경정보수신 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	}
	
	
	/**
	 * 코일제원정보 전송(타업무호출)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilInfoSend(String szSTL_NO)throws JDTOException  {
		 
		JDTORecord recStockColumn 			= JDTORecordFactory.getInstance().create();
		JDTORecord jrRtn					= JDTORecordFactory.getInstance().create();
		String szMethodName 				= "procCoilInfoSend";
		String szMsg 						= "";
		String szOperationName              = "코일제원정보 전송(타업무호출NEW)";
		String logId = commUtils.getLogId();
 
		try{
			
			szMsg = "[코일제원정보 전송(타업무호출)]수신처리 ("+szMethodName+") 시작NEW";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
			//======================================================
			// 저장품제원 : 코일야드L2 로 송신(YDY5L002)
			//======================================================
//			JDTORecord recResult = null;
//			recResult = JDTORecordFactory.getInstance().create();
//			recResult.setField("MSG_ID"         , "YDY5L002");
//			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
//			recResult.setField("STL_NO"         , szSTL_NO);
//			recResult.setField("YD_STK_COL_GP"  , "");
//			recResult.setField("YD_STK_BED_NO"  , "");
//			ydDelegate.sendMsg(recResult);
			 
			/**********************************************************
			* 5. 저장품제원 : 코일야드L2로 송신(YDY5L002)
			**********************************************************/
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setField("JMS_TC_CD"      , "YDY5L002");
			sndL2Msg.setField("YD_INFO_SYNC_CD", "5"       );    // 5:지정저장품
			sndL2Msg.setField("STL_NO"         , szSTL_NO    );
			sndL2Msg.setField("YD_STK_COL_GP"  , ""        );
			sndL2Msg.setField("YD_STK_BED_NO"  , ""        );
			
			jrRtn = commUtils.addSndData(jrRtn, coilDao.getMsgL2("YDY5L002", sndL2Msg));
			jrRtn.setResultCode(logId);
			jrRtn.setResultMsg(szMethodName);
			
			EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
			sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			
			 
		}catch(Exception e){
			
			szMsg = "[코일제원정보 전송(타업무호출)]Exception Error : "+ e.getMessage() ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		
		szMsg = "[코일제원정보 전송(타업무호출)]수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of procCoilInfoSend()
	
	
	
	/**
	 * 제품운송상차지시등록(DMYDR060)  
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilGdsTrnOrdNEW(JDTORecord inRecord)throws JDTOException  {
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		YdCarSchDao ydCarSchDao	= new YdCarSchDao();
		JDTORecord recInTemp		= JDTORecordFactory.getInstance().create();
		JDTORecord recEditColumn	= JDTORecordFactory.getInstance().create();
		JDTORecord	recPara			= JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult1 	= null;
		String szMethodName 		= "procCoilGdsTrnOrdNEW";
		String szMsg 				= "";
		String szOperationName      = "제품운송상차지시";
		String szSTL_NO 			= "";
		String szYD_CAR_SCH_ID 		= "";
		String szWLOC_CD			= "";
		String szYD_STK_COL_GP		= "";
		String szCARLD_PNT_CD		= "";
		String szHANDLING_CNT		= "";
		
		String szCMBN_CARLD_YN 		= ydDaoUtils.paraRecChkNull(inRecord,"CMBN_CARLD_YN");
		String szSTL_APPEAR_GP 		= ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP");
		String szTRANS_ORD_DT  		= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT");
		String szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO");
		String szCAR_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO");
		String szCARD_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO");
		String szLOT_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"LOT_NO");
		String szCAR_KIND			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_KIND");
		String szYD_GP	 			= "";
		int szYD_EQP_WRK_SH			= ydDaoUtils.paraRecChkNullInt(inRecord,"YD_EQP_WRK_SH");
		
    	// PIDEV
//    	String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szMethodName, "APPPI0", "*", "*");		
		
		int intRtnVal 				= 0;
		int i =0;
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{ 
			//저장품정보 등록 ///////////////////////////////////////////////////////////////////////////////////////
			recEditColumn 	= JDTORecordFactory.getInstance().create();
			recEditColumn.setField("YD_RULE_PL_RS_GP", 		szCMBN_CARLD_YN);
			recEditColumn.setField("STL_APPEAR_GP", 		szSTL_APPEAR_GP);
			recEditColumn.setField("TRANS_ORD_DATE", 		szTRANS_ORD_DT);
			recEditColumn.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO); 
 			recEditColumn.setField("CAR_NO", 				szCAR_NO);
			recEditColumn.setField("CARD_NO", 				szCARD_NO);
			recEditColumn.setField("CAR_LOTID", 			szLOT_NO);
			recEditColumn.setField("MODIFIER", 				szRcvTcCode);
			
			//박판열연 신규모듈 적용여부 조회
            YdPlateCommDAO commDao2	= new YdPlateCommDAO();
            JDTORecord jrResult		= commDao2.getYfNewModuleEffYn();

            String sACOIL_EFF_YN	= StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),	"N");

            szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
            ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
			
			//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
			for(i = 1 ; i<=szYD_EQP_WRK_SH; i++){
				recEditColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i));				
				recEditColumn.setField("YD_CAR_UPP_LOC_CD", 	ydDaoUtils.paraRecChkNull(inRecord,"GDS_CARLD_LOC"+i)); //야드 차상위치코드
				
//**************150916 hun 차상위치 아래 조건 으로 세팅
//				CMBN_CARLD_YN = S,CAR_KIND=TR,GDS_CARLD_LOC1=NULL
				if("S".equals(szCMBN_CARLD_YN) && "TR".equals(szCAR_KIND) && "".equals(ydDaoUtils.paraRecChkNull(inRecord,"GDS_CARLD_LOC")) ){
					recEditColumn.setField("YD_CAR_UPP_LOC_CD", 	"0"+i );
				}
				szMsg="[" + szMethodName + "] "+ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i)+"차상위치 세팅[YD_CAR_UPP_LOC_CD : " + recEditColumn.getField("YD_CAR_UPP_LOC_CD") + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//**************150916 hun 차상위치 세팅 end
				
				szYD_GP =ydDaoUtils.paraRecChkNull(inRecord,"YD_GP"+i);
				
				//STL_NO가 없다면 loop종료
				szSTL_NO = recEditColumn.getFieldString("STL_NO");
				if(szSTL_NO.equals("")){
					break;
				}
				
				if(szYD_GP.equals("0")||szYD_GP.equals("1")||szYD_GP.equals("2")||szYD_GP.equals("3") )
				{
					if("1".equals(szYD_GP) && "Y".equals(sACOIL_EFF_YN))
					{
						//1:A열연 COIL야드 신규모듈

						//======================================================
						// 저장품 이동 조건(STOCK_MOVE_TERM) 생성
						//======================================================
						rVal = YmCommonUtil.getCoilCurrProgCd(szSTL_NO,szRcvTcCode);

						//======================================================
						// TB_YF_STOCK 수정
						//======================================================
						recEditColumn.setField("STOCK_MOVE_TERM",	rVal[1]);
						recEditColumn.setField("YD_RULE_PL_RS_GP",	szCMBN_CARLD_YN);
						recEditColumn.setField("TRANS_ORD_DATE",	szTRANS_ORD_DT);
						recEditColumn.setField("TRANS_ORD_SEQNO",	szTRANS_ORD_SEQNO);
						recEditColumn.setField("CAR_NO",			szCAR_NO);
						recEditColumn.setField("CAR_CARD_NO",		szCARD_NO);
						recEditColumn.setField("STL_NO",			szSTL_NO);
						
				    	//PIDEV				
//						if("Y".equals(sApplyYnPI)) {
							commDao.update(recEditColumn, "com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYfStock_PIDEV", "DMYDR060", szMethodName, "TB_YF_STOCK 수정");
//						} else {
//							commDao.update(recEditColumn, "com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYfStock", "DMYDR060", szMethodName, "TB_YF_STOCK 수정");	
//						}
						
					}
					else
					{
						//AB지구
						//야드목표행선지구분**********************************************************************************
						rVal = YmCommonUtil.getCoilCurrProgCd(szSTL_NO,szRcvTcCode);					
						recEditColumn.setField("STOCK_MOVE_TERM", rVal[1]);
						//*************************************************************************************************
						
						
						recEditColumn.setField("SHEAR_SUPPLY_GP",szCAR_KIND);
						
						//저장품갱신*****************************************************************************************
						intRtnVal = ydStockDao.updYmStock(recEditColumn);
						if(intRtnVal <= 0){
							szMsg= "YM_STOCK["+szOperationName+"] UPDATE Error :: [" + intRtnVal + "]" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					 
						}
						ydUtils.putLog(szSessionName, szMethodName,"YM_STOCK["+szOperationName+"] UPDATE Success",3);
						//*************************************************************************************************
					}
				}else{
					//C지구 
					//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)***************************************************
					recPara 	= JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO",  szSTL_NO);
					recPara.setField("TC_CODE", szRcvTcCode);
					
					//작업예약만 존재 하는 경우 강제 삭제처리
					commDao.update(recPara, "com.inisteel.cim.yd.ydStock.RouteModReg.updWbookCancel", "DMYDR060", szMethodName, "작업예약 삭제");
					
					commDao.update(recPara, "com.inisteel.cim.yd.ydStock.RouteModReg.updWbookMtlCancel", "DMYDR060", szMethodName, "작업예약제료 삭제");
									
					rVal= YdCommonUtils.getYdAimRtGp("C",recPara );		
					recEditColumn.setField("YD_AIM_RT_GP", rVal[0]);
					recEditColumn.setField("STL_PROG_CD", rVal[1]);
					recEditColumn.setField("DEL_YN", "N");
					//*************************************************************************************************
					
					recEditColumn.setField("YD_STK_BED_NO",szCAR_KIND);
						
					//저장품갱신*****************************************************************************************
					intRtnVal = ydStockDao.updYdStock(recEditColumn);
					if(intRtnVal <= 0){
						szMsg= "YD_STOCK["+szOperationName+"] UPDATE Error :: [" + intRtnVal + "]" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					 
					}
					ydUtils.putLog(szSessionName, szMethodName,"YD_STOCK["+szOperationName+"] UPDATE Success",3);
					//*************************************************************************************************
				}

			} //end of for ****************************************************************************************
			
			///////////////////////////////////////////////////////////////////////////////////////////////////////
			
 
			
			//야드핸들링정보 송신////////////////////////////////////////////////////////////////////////////////
    		//코일야드 인 경우 우선 적용
    		if(szYD_GP.equals("1")||szYD_GP.equals("3")||szYD_GP.equals("H")||szYD_GP.equals("J") ) {    			
    		 
    		rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DATE"		,szTRANS_ORD_DT);
			recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
 
			//출하제품핸들링횟수
			/*com.inisteel.cim.yd.ydStock.RouteModReg.procCoilGdsTrnOrdNEW.getHandlingCnt*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 418);	
			if( intRtnVal > 0 ){
				szMsg="[" + szMethodName + "] 출하제품핸들링횟수[반환값 : " + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	 
    		 
				for( i = 1; i <= rsResult1.size(); i++ ) {
					recInTemp = JDTORecordFactory.getInstance().create();
					rsResult1.absolute(i);					
					recInTemp 		= rsResult1.getRecord();
					
					szYD_GP			 	= StringHelper.evl(recInTemp.getFieldString("YD_GP"), "");
					szCARLD_PNT_CD 		= StringHelper.evl(recInTemp.getFieldString("CARLD_PNT_CD"), "");
					szHANDLING_CNT 		= StringHelper.evl(recInTemp.getFieldString("HANDLING_CNT"), "");
					
					
					szMsg="상차완료 송신 YDDMR050 (야드핸들링정보) 송신 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",        			"YDDMR050");
					recInTemp.setField("YD_GP"           	 		,szYD_GP );
					recInTemp.setField("TRANS_ORD_DT"           	,szTRANS_ORD_DT);
					recInTemp.setField("TRANS_ORD_SEQNO"         	,szTRANS_ORD_SEQNO);
					recInTemp.setField("CMBN_CARLD_YN"         		,szCMBN_CARLD_YN );
					recInTemp.setField("CARLD_PNT_CD"         		,szCARLD_PNT_CD );
					recInTemp.setField("CAR_NO"           			,szCAR_NO );
					recInTemp.setField("HANDLING_CNT"          		,szHANDLING_CNT ); 
					recInTemp.setField("YD_STK_BED_WHIO_STAT"       ,"" );

					ydDelegate.sendMsg(recInTemp);
					
					szMsg="상차완료 송신 YDDMR050 (야드핸들링정보) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}
    		}
			/////////////////////////////////////////////////////////////////////////////////////////////////
			
		 
			
		}catch(Exception e){

			szMsg="[코일제품운송지시] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch

		szMsg="코일제품운송지시수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


	} // end of procCoilGdsTrnOrdNEW()
	
	
	
	/**
	 * 대기장도착실적(DMYDR061)  
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procStandByYdArrive(JDTORecord inRecord)throws JDTOException  {

		YdCarSchDao ydCarSchDao	= new YdCarSchDao();
		JDTORecord recInTemp		= JDTORecordFactory.getInstance().create();
		JDTORecord recEditColumn	= JDTORecordFactory.getInstance().create();
		JDTORecord	recPara			= JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult1 	= null;
		String szMethodName 		= "procStandByYdArrive";
		String szMsg 				= "";
		String szOperationName      = "대기장도착실적";
		String szYD_CAR_SCH_ID 		= "";
		String szSPOS_WLOC_CD		= "";
		String szYD_STK_COL_GP		= "";
		String szYD_CARPNT_CD		= "";
		String szYD_PNT_CD			= "";
		
		String szYD_GP 				= ydDaoUtils.paraRecChkNull(inRecord,"YD_GP");
		String szCMBN_CARLD_YN 		= ydDaoUtils.paraRecChkNull(inRecord,"CMBN_CARLD_YN");
		String szWORK_GP 			= ydDaoUtils.paraRecChkNull(inRecord,"WORK_GP");
		String szTEL_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"TEL_NO");
		String szTRANS_ORD_DT  		= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT");
		String szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO");
		String szCAR_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO");
		String szCARD_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO");
		String szCAR_KIND 			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_KIND");
		String szWAIT_ARR_DDTT		= ydDaoUtils.paraRecChkNull(inRecord,"WAIT_ARR_DDTT");
		String szWAIT_ARR_GP		= ydDaoUtils.paraRecChkNull(inRecord,"WAIT_ARR_GP");
		String szTRANS_FRTOMOVE_GP	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_FRTOMOVE_GP"); //1 운송 2 이송
		String szDRIVER_NAME		= ydDaoUtils.paraRecChkNull(inRecord,"DRIVER_NAME");	//운전기사명
	 
		szMsg= "["+szMethodName+"] start !!";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);

		int intRtnVal 				= 0;
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		}
		
		try{ 
			//조합상차(시작:S , 종료: E ,  단일상차: N )
			szCMBN_CARLD_YN = StringHelper.evl(szCMBN_CARLD_YN, "N");
 
 
			//차량정보 존재여부 체크 //////////////////////////////////////////////////////////////////////////////////
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DT"		, szTRANS_ORD_DT);
			recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			recPara.setField("CARD_NO"			, szCARD_NO );
			recPara.setField("CMBN_CARLD_YN"	, szCMBN_CARLD_YN );
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	szYD_GP);				
			//중복 check
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdCmbnCarldYn_PIDEV*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 419);
			if(intRtnVal > 0){
				szMsg= "["+szMethodName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				
				rsResult1.first();
				recInTemp = rsResult1.getRecord();
				szYD_CAR_SCH_ID    = StringHelper.evl(recInTemp.getFieldString("YD_CAR_SCH_ID"), "");
			 
				recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				recInTemp.setField("DEL_YN", "Y");
				recInTemp.setField("MODIFIER", 	szRcvTcCode);
				
				intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
				if( intRtnVal <= 0 ){
					szMsg="[" + szMethodName + "] 차량스케줄 삭제 시 오류발생[반환값 : " + intRtnVal + "]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);	
					m_ctx.setRollbackOnly();
					throw new DAOException(szMsg);
	    		}
			}
			///////////////////////////////////////////////////////////////////////////////////////////////////
			
			
			//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////////////////				
			szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
			
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("YD_GP"				, szYD_GP);
			recPara.setField("TRANS_ORD_DATE"		, szTRANS_ORD_DT);
			recPara.setField("TRANS_ORD_SEQNO"		, szTRANS_ORD_SEQNO);

			///도착가능 포인트 조회
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPointSelect*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 420);
			if(intRtnVal <= 0){
				szMsg= "["+szMethodName+"] TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다. 확인요망]";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				m_ctx.setRollbackOnly();
				throw new DAOException(szMsg);
				
			}
			rsResult1.first();
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp = rsResult1.getRecord();
			szYD_STK_COL_GP    = StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP"), "");
			szYD_CARPNT_CD     = StringHelper.evl(recInTemp.getFieldString("YD_CARPNT_CD"), "");
			szYD_PNT_CD        = StringHelper.evl(recInTemp.getFieldString("YD_PNT_CD"), "");
			szSPOS_WLOC_CD     = StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
			szCAR_KIND		   = StringHelper.evl(szCAR_KIND, "TR");
			
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
			recInTemp.setField("REGISTER",         szRcvTcCode);
			recInTemp.setField("YD_EQP_WRK_STAT",  "U");									//야드설비작업상태
			recInTemp.setField("YD_EQP_ID",        YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
			recInTemp.setField("YD_CAR_USE_GP",    YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분 
			recInTemp.setField("CAR_NO",           szCAR_NO);								//차량번호
			recInTemp.setField("CAR_KIND",          szCAR_KIND);							//차량종류
			recInTemp.setField("SPOS_WLOC_CD",     szSPOS_WLOC_CD);							//발지개소코드
			recInTemp.setField("CARD_NO",          szCARD_NO);								//카드번호
			recInTemp.setField("YD_CARLD_LEV_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
			recInTemp.setField("YD_PNT_CD1",       szYD_PNT_CD);							//야드포인트코드1
			recInTemp.setField("YD_CARLD_STOP_LOC",  szYD_STK_COL_GP);						//야드상차정지위치 
			recInTemp.setField("TRANS_ORD_DATE",   szTRANS_ORD_DT);							//운송지시일자
			recInTemp.setField("TRANS_ORD_SEQNO",  szTRANS_ORD_SEQNO);						//운송지시순번 
			
			if("E".equals(szCMBN_CARLD_YN)){
				recInTemp.setField("YD_BAYIN_WO_SEQ",  "1");								//입동지시순번 - 복수상차 마지막 1순위	
			}else{
				recInTemp.setField("YD_BAYIN_WO_SEQ",  YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 - 기본값으로 설정(9)
			}
			recInTemp.setField("YD_CAR_PROG_STAT", "1");									//상차출발상태
			recInTemp.setField("YD_CAR_WRK_GP",    szWORK_GP);
			recInTemp.setField("TEL_NO", 		   szTEL_NO);								//기사핸드폰번호
			recInTemp.setField("CMBN_CARLD_YN",    szCMBN_CARLD_YN);						//첫번째 도착창고 : S 두번째 도착창고 : E
			recInTemp.setField("WAIT_ARR_DDTT",    szWAIT_ARR_DDTT);						//대기장도착시간
			recInTemp.setField("WAIT_ARR_GP",      szWAIT_ARR_GP);							//대기장도착구분  - B:BACKUP , S:SMARTPHONE
			recInTemp.setField("DRIVER_NAME",      szDRIVER_NAME);							//운전기사명
			
    		//차량스케줄 등록
	    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
    		if( intRtnVal <= 0 ){
				szMsg="[" + szMethodName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				m_ctx.setRollbackOnly();
				throw new DAOException(szMsg);
    		}			
    		/////////////////////////////////////////////////////////////////////////////////////////////////			
    		
    		
			//입동지시 호출/////////////////////////////////////////////////////////////////////////////////////
    		if(!szYD_CARPNT_CD.equals("")){
    			szMsg="[" + szOperationName + "] 차량입동포인트[" + szYD_CARPNT_CD + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	 
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("JMS_TC_CD",  "YDYDJ662");
				recInTemp.setField("YD_CARPNT_CD",    szYD_CARPNT_CD);		//입동포인트
				recInTemp.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);	//차량스케줄ID
				recInTemp.setField("CARD_NO",          szCARD_NO);
				recInTemp.setField("CAR_NO",           szCAR_NO);
				recInTemp.setField("CAR_KIND",          szCAR_KIND);	 	//차량종류
				recInTemp.setField("TRANS_FRTOMOVE_GP",          szTRANS_FRTOMOVE_GP);	 	//1 운송 2 이송
				
				ydUtils.displayRecord(szOperationName, recInTemp);	 
				ydDelegate.sendMsg(recInTemp);
	  
//				EJBConnector 	ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);			
//			    ejbConn.trx("procCarBayInOrdReqNEW", new Class[] { JDTORecord.class }, new Object[] { recInTemp });					
				
				szMsg="[" + szOperationName + "] 차량입동포인트[" + szYD_CARPNT_CD + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		}
			/////////////////////////////////////////////////////////////////////////////////////////////////			
		 
			
		}catch(Exception e){

			szMsg="["+szOperationName+"] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);

		} // end of try-catch

		szMsg="["+szOperationName+"] 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		 

	} // end of procStandByYdArrive()
	
	
	/**
	 * 복수상차 처리 로직  
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public String procCmbnCarldYn(JDTORecord inRecord)throws JDTOException  {

		YdCarSchDao ydCarSchDao	= new YdCarSchDao();
		JDTORecord recInTemp		= JDTORecordFactory.getInstance().create();
		JDTORecord recEditColumn	= JDTORecordFactory.getInstance().create();
		JDTORecord	recPara			= JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult1 	= null;
		String szMethodName 		= "procCmbnCarldYn";
		String szMsg 				= "";
		String szOperationName      = "복수상차 처리 로직";
		String szYD_CAR_SCH_ID 		= "";
		String szSPOS_WLOC_CD 		= "";
		String szYD_PNT_CD1 		= "";		
		String szWLOC_CD			= "";
		String szYD_STK_COL_GP		= "";
		String szYD_CARPNT_CD		= "";
		String szYD_PNT_CD			= "";
		String szNEXT_YD_GP			= "";
		String szYD_GP 				= ydDaoUtils.paraRecChkNull(inRecord,"YD_GP");
		String szSTL_NO				= ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");
		String szCMBN_CARLD_YN 		= "";
		String szWORK_GP 			= "";
		String szTEL_NO 			= "";
		String szTRANS_ORD_DT  		= "";
		String szTRANS_ORD_SEQNO 	= "";
		String szCAR_NO 			= "";
		String szCARD_NO 			= "";
		String szWAIT_ARR_DDTT		= "";
		String szWAIT_ARR_GP		= "";
		String szCHK				= "";
		String szDriverName			= "";
		YdStockDao ydStockDao 		= new YdStockDao();
		
		int intRtnVal 				= 0;
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return  YdConstant.RETN_CD_FAILURE ;
		}
		
		try{		
 
			//차량정보 존재여부 체크 //////////////////////////////////////////////////////////////////////////////////
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO"		, szSTL_NO);
 

			//차량정보 조회
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarwbookid*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 425);
			if(intRtnVal <= 0){
				szMsg= "["+szMethodName+"] TB_YD_CARSCH[차량스케줄이 존재 안 합니다.]";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return  YdConstant.RETN_CD_FAILURE ;
			}
			
			rsResult1.first();
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp = rsResult1.getRecord();
			szCMBN_CARLD_YN     = StringHelper.evl(recInTemp.getFieldString("CMBN_CARLD_YN"), "N");	
			szWORK_GP			= StringHelper.evl(recInTemp.getFieldString("YD_CAR_WRK_GP"), "");
			szTEL_NO	        = StringHelper.evl(recInTemp.getFieldString("TEL_NO"), "");	
			szCAR_NO	        = StringHelper.evl(recInTemp.getFieldString("CAR_NO"), "");	
			szCARD_NO	        = StringHelper.evl(recInTemp.getFieldString("CARD_NO"), "");			
			szWAIT_ARR_DDTT   	= StringHelper.evl(recInTemp.getFieldString("WAIT_ARR_DDTT"), "");
			szWAIT_ARR_GP     	= StringHelper.evl(recInTemp.getFieldString("WAIT_ARR_GP"), "");
			szSPOS_WLOC_CD		= StringHelper.evl(recInTemp.getFieldString("SPOS_WLOC_CD"), "");
			szYD_PNT_CD1		= StringHelper.evl(recInTemp.getFieldString("YD_PNT_CD1"), "");
			szYD_STK_COL_GP		= StringHelper.evl(recInTemp.getFieldString("YD_CARLD_STOP_LOC"), "");
			szTRANS_ORD_DT		= StringHelper.evl(recInTemp.getFieldString("TRANS_ORD_DATE"), "");
			szTRANS_ORD_SEQNO	= StringHelper.evl(recInTemp.getFieldString("TRANS_ORD_SEQNO"), "");
			szDriverName  		= StringHelper.evl(recInTemp.getFieldString("DRIVER_NAME"), "");
			///////////////////////////////////////////////////////////////////////////////////////////////////
			
			//조합상차(시작:S , 종료: E ,  단일상차: N )
			if("S".equals(szCMBN_CARLD_YN)){ 
				szMsg= "["+szMethodName+"] ★★★★★ 복수 상차 인 경우 ★★★★★";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				
				
				//자동차량출발 처리      //////////////////////////////////////////////////////////////////////////////				 
				if("1".equals(szYD_GP)||"3".equals(szYD_GP)){
					// -----------------------------------------AB열연---------------------------------------------
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("TRANS_ORD_DT", 			szTRANS_ORD_DT);
					recInTemp.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
					recInTemp.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);					
					recInTemp.setField("DEL_YN", 				"Y");
					recInTemp.setField("STOCK_MOVE_TERM" , 		"MG");
					recInTemp.setField("MODIFIER", 				"복수상차");	 

								
					//저장품종료처리  
					//intRtnVal = ydStockDao.updYmStockTrnsOrd(recInTemp);
					EJBConnector ejbConn = new EJBConnector("default", "RtModRegSeEJB", this);
					Integer iRtn = (Integer)ejbConn.trx("updYmStockTrnsOrdTX", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					
//					if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {
// 						szMsg="["+ szOperationName +"] YD_STOCK[코일제품출하완료] UPDATE Error :: [" + intRtnVal + "]";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//						return  YdConstant.RETN_CD_FAILURE ;
//					}
					
					
					szMsg= "["+ szOperationName +"]AB야드 차량번호[" + szCAR_NO + "]는 자동차량출발";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					EJBConnector ejbConn1 = new EJBConnector("default","JNDICTSStatusReg",this);
					Boolean isTemp = (Boolean)ejbConn1.trx("carStartOrder",
												new  Class[]{String.class,
															 String.class,
															 String.class},
												new Object[]{" ",						//한자리공백
															szCARD_NO,					//카드번호
															szYD_STK_COL_GP});			//차량정지위치
					
				}else{
					// -----------------------------------------C열연---------------------------------------------
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("TRANS_ORD_DT", 			szTRANS_ORD_DT);
					recInTemp.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
					recInTemp.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
					recInTemp.setField("DEL_YN", 				"Y");
					recInTemp.setField("STL_PROG_CD" , 			"M");
					recInTemp.setField("YD_AIM_RT_GP" , 		"M2");
					recInTemp.setField("MODIFIER", 				"복수상차");	 

								
					//저장품종료처리  
					//intRtnVal = ydStockDao.updYdStockTrnsOrd(recInTemp);
					EJBConnector ejbConn = new EJBConnector("default", "RtModRegSeEJB", this);
					Integer iRtn = (Integer)ejbConn.trx("updYdStockTrnsOrdTX", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					
//					if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {
//						szMsg="["+ szOperationName +"] YD_STOCK[코일제품출하완료] UPDATE Error :: [" + intRtnVal + "]";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//						return  YdConstant.RETN_CD_FAILURE ;
//					}				
					
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("TC_CODE",        		"DMYDR040");									//전문코드
					recInTemp.setField("CARD_NO", 				szCARD_NO);
					recInTemp.setField("CAR_NO", 				szCAR_NO);			
					recInTemp.setField("SPOS_WLOC_CD", 			szSPOS_WLOC_CD);
					recInTemp.setField("SPOS_YD_PNT_CD", 		szYD_PNT_CD1);
					recInTemp.setField("TRANS_ORD_DT", 			szTRANS_ORD_DT);
					recInTemp.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
					
					szMsg= "["+ szOperationName +"]C야드 차량번호[" + szCAR_NO + "]는 자동차량출발";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					EJBConnector ejbConn2 = new EJBConnector("default", "CarMvHdFaEJB", this);
					ejbConn2.trx("rcvCoilGdsdistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					
					
					//======================================================
					// 2009.08.31 권오창
					// 저장품제원 : 코일야드L2 로 송신(YDY5L002)
					//======================================================
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID"         , "YDY5L002");
					recInTemp.setField("YD_INFO_SYNC_CD", "3");    
					recInTemp.setField("YD_STK_COL_GP"  , szYD_STK_COL_GP);
					recInTemp.setField("YD_STK_BED_NO"  , "");
					recInTemp.setField("DEL_YN_CHECK"   , "N");
					ydDelegate.sendMsg(recInTemp);
				}
				/////////////////////////////////////////////////////////////////////////////////////////////////
				
				
				
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DT);
				recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
				recPara.setField("YD_GP"			, szYD_GP );
 				
				//복수창고 구분
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdCmbnCarldGP*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 423);
				if(intRtnVal <= 0){
					szMsg= "["+szMethodName+"] ☆☆☆☆☆ 복수 창고가 아닌 경우 ☆☆☆☆☆";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					
					rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
					recPara 	= JDTORecordFactory.getInstance().create();
					recPara.setField("YD_GP"			, szYD_GP );
					recPara.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DT);
					recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
					recPara.setField("STL_NO"			, szSTL_NO);
					
	 				
					//복수창고 구분
					/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarNoCardNoTransNoCHK*/
					intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 432);
					
					if(intRtnVal > 0){
						rsResult1.first();
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp = rsResult1.getRecord();
						szCHK     = StringHelper.evl(recInTemp.getFieldString("CHK"), "");
					} 
					
					
					
					//입동지시 호출/////////////////////////////////////////////////////////////////////////////////////
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("TC_CODE"				,"YDYDJ394");
					recInTemp.setField("YD_GP"		            ,szYD_GP);					
					recInTemp.setField("WORK_GP"		        ,szWORK_GP);
					recInTemp.setField("TEL_NO"		            ,szTEL_NO);
					recInTemp.setField("TRANS_ORD_DT"		    ,szTRANS_ORD_DT);
					recInTemp.setField("TRANS_ORD_SEQNO" 		,szTRANS_ORD_SEQNO);
					recInTemp.setField("CAR_NO"					,szCAR_NO);
					recInTemp.setField("CARD_NO"				,szCARD_NO);
					recInTemp.setField("WAIT_ARR_DDTT"			,YdUtils.getCurDate("yyyyMMddHHmmss"));
					recInTemp.setField("WAIT_ARR_GP"			,"B");
					recInTemp.setField("DRIVER_NAME"			,szDriverName);
					
					
					
					szMsg= "["+szMethodName+"] 복수동 존재 수량 체크"+szCHK;
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					if(szCHK.equals("0")||szCHK.equals("1")){
						recInTemp.setField("CMBN_CARLD_YN"		    ,"E");
					}else {
						recInTemp.setField("CMBN_CARLD_YN"		    ,"S");
					}
					
					ydUtils.displayRecord(szOperationName, recInTemp);
					ydDelegate.sendMsg(recInTemp);
					/////////////////////////////////////////////////////////////////////////////////////////////////
				}else{
					szMsg= "["+szMethodName+"] ☆☆☆☆☆ 복수 창고 인 경우 ☆☆☆☆☆";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				
					rsResult1.first();
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp = rsResult1.getRecord();
					szNEXT_YD_GP     = StringHelper.evl(recInTemp.getFieldString("NEXT_YD_GP"), "");	
					
					
					rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
					recPara 	= JDTORecordFactory.getInstance().create();
					recPara.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DT);
					recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
					recPara.setField("YD_GP"			, szNEXT_YD_GP );
	 				
					//다음 창고 도착 포인트
					/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdCmbnCarSch*/
					intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 424);
					
					if(intRtnVal <= 0){
						szMsg= "["+szMethodName+"] 다음 창고 도착가능 포인트가 존재 안 합니다.";
						YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
						return  YdConstant.RETN_CD_FAILURE ;					
					}
					
					rsResult1.first();
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp = rsResult1.getRecord();
					szWLOC_CD     	= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");	
					szYD_PNT_CD     = StringHelper.evl(recInTemp.getFieldString("YD_PNT_CD"), "");
					szYD_CARPNT_CD  = StringHelper.evl(recInTemp.getFieldString("YD_CARPNT_CD"), "");
					
					//다음 창고 입동TC 전송//////////////////////////////////////////////////////////////////////////////
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("TC_CODE"				,"YDDMR028");
					recInTemp.setField("TRANS_WORD_DATE"		,szTRANS_ORD_DT);
					recInTemp.setField("TRANS_WORD_SEQNO" 		,szTRANS_ORD_SEQNO);
					recInTemp.setField("CARD_NO"				,szCARD_NO);
					recInTemp.setField("CAR_NO"					,szCAR_NO);
					recInTemp.setField("WLOC_CD"				,szWLOC_CD);
					recInTemp.setField("YD_PNT_CD"				,szYD_PNT_CD);
//					recInTemp.setField("YD_CARPNT_CD"			,szYD_CARPNT_CD);	
//					recInTemp.setField("LOAN_PULLOUT_ABLE_YN","N");
					
					//복수창고인 경우 다음 창고로 대기 하기 위해서 다음과 같이 전송 한다.
					recInTemp.setField("YD_CARPNT_CD"			,"");	
					recInTemp.setField("LOAN_PULLOUT_ABLE_YN","Y");
					
					ydUtils.displayRecord(szOperationName, recInTemp);
					ydDelegate.sendMsg(recInTemp);		
					
					szMsg="["+ szOperationName +"] 입동대기 TC 전송 처리("+szMethodName+") 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					 
					/////////////////////////////////////////////////////////////////////////////////////////////////
					
				}

	
			}else{
				szMsg= "["+szMethodName+"] ★★★★★ 복수 상차가 아닌 경우 ★★★★★";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return YdConstant.RETN_CD_EXIST;
			}
			
		}catch(Exception e){

			szMsg="["+szOperationName+"] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);

		} // end of try-catch

		szMsg="["+szOperationName+"] 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return YdConstant.RETN_CD_SUCCESS;

	} // end of procCmbnCarldYn()
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updYdStockTrnsOrdTX(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updYdStockTrnsOrd(inRec);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockTrnsOrdTX
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public int updYmStockTrnsOrdTX(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal               = 0;
		YdStockDao ydStockDao = new YdStockDao();
		try {
			
//			기존 방식 적용 
    		intRtnVal = ydStockDao.updYmStockTrnsOrd(inRec);
    		if(intRtnVal ==0){
    			return intRtnVal = -1;
    		}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYmStockTrnsOrdTX
	
	
	/**
	 * 후판용 제품운송상차지시(DMYDR060) - (출하고도화)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlateGdsTrnOrd3GUpGrade(JDTORecord inRecord)throws JDTOException  {
		
		//기본 변수 정의
		String szMethodName 		= "procPlateGdsTrnOrd3GUpGrade";
		String szOperationName		= "후판용 제품운송상차지시(DMYDR060)";		
		String szMsg 				= "";
		
		String szCAR_NO 			= null;
		String szCARD_NO 			= null;
		String szTRANS_ORD_DATE		= null;
		String szTRANS_ORD_SEQNO	= null;
		String szCarLotId			= null;
		String szUNIQUE_ID          = null;
		String szSTL_NO				= null;
		String szCMBN_CARLD_YN 		= null;
		String szYD_GP				= null;
		String szCARLD_PNT_CD		= null;
		String szCARLD_PNT_CD_old	= null;
		String szHANDLING_CNT		= null;
		String szYD_STK_LOC			= null;
		String szYD_STK_LOC_old		= null;
		String szJOB_GP				= null;
		String szJOB_GP_old			= null;
		String szCAR_LOTID			= null;
		String szCANCEL_YN			= null;
		
		int intYD_EQP_WRK_SH		= 0;
		int intRtnVal 				= 0;
		
		JDTORecordSet rsResult1 	= null;
		JDTORecord	recPara			= null;
		JDTORecord	recEditColumn	= null;
		JDTORecord  recInTemp		= null;
		
		YdCarSchDao ydCarSchDao 	= new YdCarSchDao();	//차량스케줄DAO
		YdStockDao ydStockDao 		= new YdStockDao();		//저장품DAO
		YdPlateCommDAO commDao 		= new YdPlateCommDAO(); //3기 후판제품공용dao
		
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){
			szMsg="[" + szOperationName + "] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{	
			
			szCAR_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO");
			szCARD_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO");
			szTRANS_ORD_DATE 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT");
			szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO");
			intYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNullInt(inRecord,"YD_EQP_WRK_SH");
			szCarLotId			= ydDaoUtils.paraRecChkNull(inRecord,"LOT_NO");
			szUNIQUE_ID 		= ydDaoUtils.paraRecChkNull(inRecord,"UNIQUE_ID");	
			szCMBN_CARLD_YN 	= ydDaoUtils.paraRecChkNull(inRecord,"CMBN_CARLD_YN");
			szCANCEL_YN			= ydDaoUtils.paraRecChkNull(inRecord,"CANCEL_YN");
			
			szMsg="["+szOperationName+"] szUNIQUE_ID:" + szUNIQUE_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if("".equals(szUNIQUE_ID)){
				szUNIQUE_ID = "0";
			}		
			
			//전문항목 에러체크
			if("".equals(szCAR_NO)){
				szMsg="[" + szOperationName + "] CAR_NO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//return ;
			}
			//if("".equals(szCARD_NO)){
			//	szMsg="[" + szOperationName + "] CARD_NO IS NULL ("+szRcvTcCode+")";
			//	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//	return ;
			//}
			if("".equals(szTRANS_ORD_DATE)){
				szMsg="[" + szOperationName + "] TRANS_ORD_DT IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			if("".equals(szTRANS_ORD_SEQNO)){
				szMsg="[" + szOperationName + "] TRANS_ORD_SEQNO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			//-------------------------------------------------------------------------------------------------------------
			if("Y".equals(szCANCEL_YN)) { //취소일 경우
				
		    	/*
		    	 * 후판출하상차지시 취소시점에 예외사항 체크 
		    	 */				
	    		JDTORecordSet rsChkPara	= null;
	    		JDTORecord recChkPara	= null;
	    		String sChkStlNo		= "";
	    		
	    		YdWrkbookMtlDao ydWrkbookDao     = new YdWrkbookMtlDao();
	    		
	    		intYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNullInt(inRecord,"YD_EQP_WRK_SH");
	    		
	    		for(int index = 0; index < intYD_EQP_WRK_SH; index++){
	    			
	    			rsChkPara = JDTORecordFactory.getInstance().createRecordSet("");
	    			recChkPara  = JDTORecordFactory.getInstance().create();
	    			
	    			sChkStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO" + (index+1));
	    			
	    			if("".equals(sChkStlNo)) break;
	    			
	    			recChkPara.setField("STL_NO" , sChkStlNo);
	    			
	    			intRtnVal = ydWrkbookDao.getYdWrkbookmtl(recChkPara, rsChkPara, 2);
	    			
	    			if(intRtnVal <= 0) {
					}else{
						szMsg="["+szMethodName+"] 후판출하상차지시 취소시 이미 작업예약에 재료 존재함.["+sChkStlNo+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return;
					}
	    		}
				
	    		YdStkBedDao ydStkBedDao = new YdStkBedDao();
	    		
	    		/*
	    		 * 취소처리 : 운송지시,순번,차량번호가 일치하는 것만 취소처리 한다.
	    		 */
	    		recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DATE);
				recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
				recPara.setField("CAR_NO"	, szCAR_NO);
	    		
	    		for(int index = 0; index < intYD_EQP_WRK_SH; index++){
	    			
					szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+(index+1));
					recPara.setField("STL_NO"	, szSTL_NO);
					
	    			intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0047");
	    			
	    			/*
	    		     * 선별LOT편성정보 취소
	    		     *
	    		     * 출하에서  선별Lot편성취소  정보가 수신되면  해당 저장위치 상태변경
	    		     *  야드베드입출고상태 : 
	    		     *  선별상태 : 
	    		     */
	    		    intRtnVal = ydStkBedDao.updYdStkBedStat_02(recPara);
	    		}
	    		
	    		//=========================================================
				//	차량스케줄 삭제 및 차량 POINT Clear
				//=========================================================
				ydUtils.putLog(szSessionName, szMethodName, "차량스케줄삭제 및 차량Point Clear 시작", YdConstant.DEBUG);
//PIDEV_S :병행가동용:PI_YD
				inRecord.setField("PI_YD",    	"T");						
				String szRtnMsg = YdCommonUtils.delCarSchNCarPointForDist(inRecord, szMethodName);
				ydUtils.putLog(szSessionName, szMethodName, "차량스케줄삭제 및 차량Point Clear 완료 - " + szRtnMsg, YdConstant.DEBUG);
				
				
				// 전사물류개선 2021. 4. 3
				JDTORecord params = JDTORecordFactory.getInstance().create();
				JDTORecordSet rsBedRecord 	= JDTORecordFactory.getInstance().createRecordSet("ydPlate");
				params.setField("TRANS_ORD_DATE"           	,szTRANS_ORD_DATE);
				params.setField("TRANS_ORD_SEQNO"         	,szTRANS_ORD_SEQNO);
				if(commDao.select(params, rsBedRecord, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getDMYDR060_SendToL2List") > 0){
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
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			recPara.setField("CARD_NO"			, szCARD_NO );
			recPara.setField("DEL_YN"			, "N" );
			
			intRtnVal = commDao.select(recPara, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0087_PIDEV");
			if(intRtnVal > 0){
				szMsg= "["+szOperationName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
				
			//---------------------------------------------------------------------------------
			//	YD저장품에 운송상차지시 정보를 등록(UPDATE) 한다.
			//---------------------------------------------------------------------------------			
			recEditColumn 	= JDTORecordFactory.getInstance().create();
			recEditColumn.setField("YD_RULE_PL_RS_GP", 		szCMBN_CARLD_YN);
			recEditColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recEditColumn.setField("TRANS_ORD_DATE", 		szTRANS_ORD_DATE);
			recEditColumn.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
			recEditColumn.setField("CAR_NO", 				szCAR_NO);
			recEditColumn.setField("CARD_NO", 				szCARD_NO);
			//recEditColumn.setField("ARR_WLOC_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"ARR_WLOC_CD"));
			//recEditColumn.setField("YD_PNT_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"ARR_YD_PNT_CD"));
			//recEditColumn.setField("YD_STK_LOT_CD", 		ydDaoUtils.paraRecChkNull(inRecord,"TEL_NO"));
			if(!"".equals(szCarLotId)) {
				recEditColumn.setField("CAR_LOTID", 			szCarLotId);
			}
			recEditColumn.setField("MODIFIER", 				szRcvTcCode);
			
			szMsg="[" + szOperationName + "]["+intYD_EQP_WRK_SH+"]매수에 대한  저장품 업데이트 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			for(int i = 1 ; i<= intYD_EQP_WRK_SH; i++){
				
				szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i);
				
				if(szSTL_NO.equals("")){
					break;
				}else {
					inRecord.setField("STL_NO", 	szSTL_NO);
				}
				recEditColumn.setField("STL_NO",	szSTL_NO);
				
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)***************************************************
				//recPara.setField("STL_NO",  szSTL_NO);
				//recPara.setField("TC_CODE", szRcvTcCode);
				//rVal= YdCommonUtils.getYdAimRtGp("P",recPara );		
				//recEditColumn.setField("YD_AIM_RT_GP", rVal[0]);
				//recEditColumn.setField("STL_PROG_CD", rVal[1]);				
				
				recEditColumn.setField("YD_AIM_RT_GP", "L6"); 
				recEditColumn.setField("STL_PROG_CD",  "L");  //운송대기	
				
				/* 
				 * 1. 저장품테이블의 재료들에 재료진도코드와 야드목표행선을 수정처리
				 */
				//저장품갱신******************************************************************************************** 
				szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 야드목표행선[L6]과 재료진도코드[L] 업데이트 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				intRtnVal = ydStockDao.updYdStock(recEditColumn, 0);
				if(intRtnVal <= 0){
					szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 야드목표행선[L6]과 재료진도코드[L] 업데이트 시 오류발생 - 반환값 : " +intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 야드목표행선[L6]과 재료진도코드[L] 업데이트 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//****************************************************************************************************
				
			}
			
			
			//야드핸들링정보 송신////////////////////////////////////////////////////////////////////////////////
			//최대 폭			
			double dblMaxWidth  = 0;
			//현재 폭
			double dblCurrWidth = 0;
			//최대 두께			
			double dblMaxThick  = 0;
			//현재 두께
			double dblCurrThick = 0;
			//중량의 합
			//long lngSumWt     = 0;		
			//현재 중량
			//long lngCurrWt    = 0;			
			//재료매수
			int intMtlSh      = 0;	
			//크레인작업가능매수
			int intCrnWrkableSh	= 0;
			//핸들링 수 
			int intHndlingCnt = 1;
			
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara.setField("TRANS_ORD_DT"		, szTRANS_ORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			
			intRtnVal = commDao.select(recPara, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0086");	
			
			if( intRtnVal > 0 ){
				
				for( int i = 1; i <= rsResult1.size(); i++ ) {
					
					rsResult1.absolute(i);
					recPara = rsResult1.getRecord();
					
					szCARLD_PNT_CD 		= ydDaoUtils.paraRecChkNull(recPara,"YD_CARPNT_CD"); 
					szYD_GP				= szCARLD_PNT_CD.substring(0,1);
					szYD_STK_LOC		= ydDaoUtils.paraRecChkNull(recPara,"YD_STK_LOC");
					szJOB_GP			= ydDaoUtils.paraRecChkNull(recPara,"JOB_GP");
					szCAR_LOTID			= ydDaoUtils.paraRecChkNull(recPara,"CAR_LOTID");
					
					if( i == 1 ) {
						szCARLD_PNT_CD_old	= szCARLD_PNT_CD;
						szYD_STK_LOC_old	= szYD_STK_LOC;
						szJOB_GP_old		= szJOB_GP;
					}		
					
					if("A".equals(szJOB_GP) && !"".equals(szCAR_LOTID)) {
						//주작업대상이 아닌 보조작업(이적)대상에 차량lotId가 셋팅되어 있다면 핸들링 Count 에서 배제한다.
						continue;
					}
					
					if(!szYD_STK_LOC_old.equals(szYD_STK_LOC) || !szJOB_GP_old.equals(szJOB_GP)) {
						//위치가 변경되었거나, 주작업/보조작업이 변경되었다면 핸들링수 1 증가
						intHndlingCnt++;
						
		    			//폭,중량,매수 초기화
		    			dblCurrWidth = 0;
		    			dblCurrThick = 0;
		    			dblMaxWidth = 0;
		    			dblMaxThick = 0;
		    			intMtlSh = 0;							
					}
					
					if(!szCARLD_PNT_CD_old.equals(szCARLD_PNT_CD)) {
						//차량 POINT 가 변경 되었을 경우
						szMsg="상차완료 송신 YDDMR050 (야드핸들링정보) 송신 시작";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("MSG_ID",        			"YDDMR050");
						recInTemp.setField("YD_GP"           	 		,szYD_GP );
						recInTemp.setField("TRANS_ORD_DT"           	,szTRANS_ORD_DATE);
						recInTemp.setField("TRANS_ORD_SEQNO"         	,szTRANS_ORD_SEQNO);
						recInTemp.setField("CMBN_CARLD_YN"         		,szCMBN_CARLD_YN );
						recInTemp.setField("CARLD_PNT_CD"         		,szCARLD_PNT_CD_old );
						recInTemp.setField("CAR_NO"           			,szCAR_NO );
						recInTemp.setField("HANDLING_CNT"          		,Integer.toString(intHndlingCnt)); 
						recInTemp.setField("YD_STK_BED_WHIO_STAT"       ,"" );

						ydDelegate.sendMsg(recInTemp);
						
						szMsg="상차완료 송신 YDDMR050 (야드핸들링정보) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
						
						
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
    				dblCurrWidth = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_MTL_W");
    				//재료의 현재 두께
    				dblCurrThick = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_MTL_T");
    				
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
    				
					szCARLD_PNT_CD_old	= szCARLD_PNT_CD;
					szYD_STK_LOC_old	= szYD_STK_LOC;
					szJOB_GP_old		= szJOB_GP;    				
				}
				
				//for 문 밖에서 마지막 차량POINT의 핸들링 수를 전송한다.
				szMsg="상차완료 송신 YDDMR050 (야드핸들링정보) 송신 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID",        			"YDDMR050");
				recInTemp.setField("YD_GP"           	 		,szYD_GP );
				recInTemp.setField("TRANS_ORD_DT"           	,szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO"         	,szTRANS_ORD_SEQNO);
				recInTemp.setField("CMBN_CARLD_YN"         		,szCMBN_CARLD_YN );
				recInTemp.setField("CARLD_PNT_CD"         		,szCARLD_PNT_CD );
				recInTemp.setField("CAR_NO"           			,szCAR_NO );
				recInTemp.setField("HANDLING_CNT"          		,Integer.toString(intHndlingCnt)); 
				recInTemp.setField("YD_STK_BED_WHIO_STAT"       ,"" );

				ydDelegate.sendMsg(recInTemp);
				
				szMsg="상차완료 송신 YDDMR050 (야드핸들링정보) 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
			}
			/////////////////////////////////////////////////////////////////////////////////////////////////
			
			
		}catch(Exception e){

			szMsg="["+szOperationName+"] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch
		
		szMsg="["+szOperationName+"] 수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
		
	} // end of procPlateGdsTrnOrd3GUpGrade()
	
	/**
	 * 후판용 대기장도착실적(DMYDR061) - (출하고도화)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procStandByYdArrivePlate(JDTORecord inRecord)throws JDTOException  {
		
		//기본 변수 정의
		String szMethodName 		= "procStandByYdArrivePlate";
		String szOperationName		= "후판용 대기장도착실적(DMYDR061)";		
		String szMsg 				= "";
		
		String szYD_GP				= null;
		String szCMBN_CARLD_YN		= null;
		String szWORK_GP			= null;
		String szTEL_NO				= null;
		String szTRANS_ORD_DATE		= null;
		String szTRANS_ORD_SEQNO	= null;
		String szCAR_NO 			= null;
		String szCARD_NO			= null;
		String szCAR_KIND			= null;
		String szCARLD_PNT_CD		= null;
		String szWAIT_ARR_DDTT		= null;
		String szWAIT_ARR_GP		= null;
		String szUNIQUE_ID          = null;
		
		String szYD_STK_COL_GP		= null;
		String szYD_BAY_GP			= null;
		String szYD_EQP_GP			= null;
		
		String szAPPLY_YN  			= "N";
		String szYD_SAPN_COL_GP     = null;
		String szWLOC_CD			= null;
		String szYD_STK_COL_ACT_STAT = null;
		String szRtnMsg				= null;
		
		String szYD_PNT_CD			= null;
		String szYD_CAR_SCH_ID		= null;
		String szSHIP_NAME   		= null;
		String szRENTPROC_CD   		= null;
		
		String szDOUBLEDONG_CHECK	= null;
		String szDOUBLEDONG_YD_CAR_SCH_ID = null;
		
		JDTORecordSet rsResult		= null;
		JDTORecordSet rsResult1 	= null;
		JDTORecordSet rsGetStock 	= null;
		
		JDTORecord	recPara			= null;		
		JDTORecord	recInTemp		= null;
		JDTORecord  recStlNo 		= null;
		
		int intRtnVal 				= 0;
		
		int intA_DONG  				= 0;
		int intB_DONG  				= 0;
		int intC_DONG  				= 0;
		int intD_DONG  				= 0;
		int intE_DONG  				= 0;
		int intF_DONG  				= 0;
		int intG_DONG  				= 0;
		int intYD_SAPN_COL_GP     	= 0;		
		
		YdCarSchDao ydCarSchDao 	= new YdCarSchDao();	//차량스케줄DAO
		YdPlateCommDAO commDao 		= new YdPlateCommDAO(); //3기 후판제품공용dao
		YdEqpDao   ydEqpDao   		= new YdEqpDao();		//야드설비 DAO
		YdStkColDao ydStkColDao 	= new YdStkColDao();	//적치열 DAO
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){
			szMsg="[" + szOperationName + "] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{ 
			
			szYD_GP 				= ydDaoUtils.paraRecChkNull(inRecord,"YD_GP");
			szCMBN_CARLD_YN 		= ydDaoUtils.paraRecChkNull(inRecord,"CMBN_CARLD_YN");
			//조합상차(시작:S , 종료: E ,  단일상차: N )
			szCMBN_CARLD_YN 		= StringHelper.evl(szCMBN_CARLD_YN, "N");
			szWORK_GP 				= ydDaoUtils.paraRecChkNull(inRecord,"WORK_GP");
			szTEL_NO 				= ydDaoUtils.paraRecChkNull(inRecord,"TEL_NO");
			szTRANS_ORD_DATE  		= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT");
			szTRANS_ORD_SEQNO 		= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO");
			szCAR_NO 				= ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO");
			szCARD_NO 				= ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO");
			szCAR_KIND 				= ydDaoUtils.paraRecChkNull(inRecord,"CAR_KIND");
			szCARLD_PNT_CD			= ydDaoUtils.paraRecChkNull(inRecord,"CARLD_PNT_CD");
			szWAIT_ARR_DDTT			= ydDaoUtils.paraRecChkNull(inRecord,"WAIT_ARR_DDTT");
			szWAIT_ARR_GP			= ydDaoUtils.paraRecChkNull(inRecord,"WAIT_ARR_GP");	
			szUNIQUE_ID 			= ydDaoUtils.paraRecChkNull(inRecord,"UNIQUE_ID");
			szSHIP_NAME				= ydDaoUtils.paraRecChkNull(inRecord,"SHIP_NAME");
			szRENTPROC_CD			= ydDaoUtils.paraRecChkNull(inRecord,"CRN_WRK_METHOD_GP");
			
			szMsg="["+szOperationName+"] szUNIQUE_ID:" + szUNIQUE_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if("".equals(szUNIQUE_ID)){
				szUNIQUE_ID = "0";
			}		
			
			//전문항목 에러체크
			if("".equals(szCAR_NO)){
				szMsg="[" + szOperationName + "] CAR_NO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			//if("".equals(szCARD_NO)){
			//	szMsg="[" + szOperationName + "] CARD_NO IS NULL ("+szRcvTcCode+")";
			//	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//	return ;
			//}
			if("".equals(szTRANS_ORD_DATE)){
				szMsg="[" + szOperationName + "] TRANS_ORD_DT IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			if("".equals(szTRANS_ORD_SEQNO)){
				szMsg="[" + szOperationName + "] TRANS_ORD_SEQNO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			//---------------------------------------------------------------------------------
			//	DEL_YN = 'N' 인 차량스케줄이 존재할경우 SKIP처리한다. 
			//---------------------------------------------------------------------------------
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			recPara.setField("CARD_NO"			, szCARD_NO );
			recPara.setField("DEL_YN"			, "N" );
			
			intRtnVal = commDao.select(recPara, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0087_PIDEV");
			if(intRtnVal > 0){
				szMsg= "["+szOperationName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}	
			
			if(!"".equals(szTEL_NO)) {
				//---------------------------------------------------------------------------------
				//	TB_YD_STOCK 에 TEL_NO 을 셋팅한다.
				//---------------------------------------------------------------------------------			
				recPara.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DATE);
				recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
				recPara.setField("YD_STK_LOT_CD"	, szTEL_NO);
				
				intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0030");
			}
			
			
			/*
			 *  복수동 출하시 나머지 출하작업에 대한 상태 = 'Y'
			 *  -- 권하처리시 선 복수동 작업일때 해당항목을 셋팅
			 */
			szDOUBLEDONG_CHECK  		= ydDaoUtils.paraRecChkNull(inRecord,"DOUBLEDONG_CHECK");
			szDOUBLEDONG_YD_CAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(inRecord,"YD_CAR_SCH_ID"); 			
			

			//--------------------------------------------------------------------------------------
			// 이하 AS-IS 루틴 그대로 사용
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			if(szCARD_NO.startsWith("P")){ // PALLET 출하 
				
				//차량포인트 테이블에서 WLOC_CD 와 YD_PNT_CD 를 읽어 온다. 
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				
				if("".equals(szCARLD_PNT_CD)){
				//출하포인트코드
				szCARLD_PNT_CD = ydDaoUtils.paraRecChkNull(inRecord, "YD_CARPNT_CD");
				}
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CARPNT_CD", 		  szCARLD_PNT_CD);
				
				intRtnVal = commDao.select(recInTemp, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0102");
				
				if(intRtnVal <= 0){
					szMsg="["+szOperationName+"] PALLET 출하 차량포인트 조회 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
					return ;
				}
				
				rsResult.first();
				recInTemp = rsResult.getRecord();
				
				szWLOC_CD   	= ydDaoUtils.paraRecChkNull(recInTemp,"WLOC_CD");
				szYD_PNT_CD 	= ydDaoUtils.paraRecChkNull(recInTemp,"YD_PNT_CD");
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recInTemp,"YD_STK_COL_GP");
				
				/*
				 * 2. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
				 */
				szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    		szYD_CAR_SCH_ID);
				recInTemp.setField("REGISTER",         		szRcvTcCode);
				recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
				recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
				recInTemp.setField("CAR_KIND", 				szCAR_KIND);							//차량종류
				recInTemp.setField("SPOS_WLOC_CD",     		szWLOC_CD);								//발지개소코드
				recInTemp.setField("YD_PNT_CD1",     		szYD_PNT_CD);							//야드포인트코드1
				recInTemp.setField("CAR_NO",           		szCAR_NO);								//차량번호
				recInTemp.setField("CARD_NO",          		szCARD_NO);								//카드번호
				recInTemp.setField("YD_CARLD_LEV_DT",  		YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
		    	recInTemp.setField("YD_CARLD_STOP_LOC",		szYD_STK_COL_GP);						//차량상차정지위치
		    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 --기본값으로 설정(9) **
				recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
				recInTemp.setField("SHIP_NAME", 			szSHIP_NAME);							//선박명
				recInTemp.setField("RENTPROC_CD", 			szRENTPROC_CD);							//해송 줄거리작업 방법 M:마그네틱, T:줄거리
				recInTemp.setField("IF_SEQ_NO", 			szUNIQUE_ID);	// 운송지시 SEQ
	    		//차량스케줄 등록
		    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
	    		if( intRtnVal <= 0 ){
					szMsg="[" + szOperationName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
	    		}
	    		
	    		szMsg="[" + szOperationName + "] 차량스케줄 생성 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				/*
				 * 2016.06.28 윤 재광
				 * 상차 후 복수동으로 가기위해 장착할 경우 이 이벤트 발생
				 * 이 시점에 해당 포인트 예약처리 
				 */
				recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
		    	recInTemp.setField("YD_STK_COL_ACT_STAT", "R");					
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0018");	
				
				recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_CARPNT_CD", szCARLD_PNT_CD);
		    	recInTemp.setField("YD_STK_COL_ACT_STAT", "R");					
				
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
				
				/*
				 * 3. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
				 */
				//szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 시작";
				//ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//recInTemp = JDTORecordFactory.getInstance().create();
				//recInTemp.setField("TC_CODE"				,"YDYDJ633");
				//recInTemp.setField("TC_CREATE_DDTT"		,YdUtils.getCurDate("yyyyMMddHHmmss"));
				//recInTemp.setField("YD_CAR_STOP_LOC"		,szYD_STK_COL_GP);
				//recInTemp.setField("YD_CAR_SCH_ID"		,szYD_CAR_SCH_ID);
				//recInTemp.setField("CALL_PGM"			    ,"SANGCHA");
				
				//----------------------------------------------------------------------------------
				//	동기화 문제로 인하여 JMS --> EJB Call로 변경
				//	수정자 : 임춘수
				//	수정일 : 1) 2009.12.30 - 최초등록
				//----------------------------------------------------------------------------------
				//ydDelegate.sendMsg(recInTemp);
	
				//EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
				//szRtnMsg = (String) ejbConn.trx("procCarBayInOrdReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				
				//----------------------------------------------------------------------------------
				//szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 성공 - 메세지 : " + szRtnMsg;
				//ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

				return;
				
			}else{// 일반차량 출하
				/*
				 * 2. 저장품이 적치된 저장위치 정보를 조회 
				 */
				szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//저장품 동 구하기 
				rsGetStock 	= JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP",   "");
				recInTemp.setField("CARD_NO", 		  szCARD_NO);
				recInTemp.setField("TRANS_ORD_DATE" , szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
				recInTemp.setField("YD_GP", 		  szYD_GP);
    				
				intRtnVal = commDao.select(recInTemp, rsGetStock, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0089_PIDEV");			
    			
				if(intRtnVal <= 0){
					return ;
				}
				
				szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 성공 - 건수["+intRtnVal+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsGetStock.first();
				recStlNo = rsGetStock.getRecord();
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recStlNo,"YD_STK_COL_GP");
				
				szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 대상재가 존재하는 동["+szYD_STK_COL_GP+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//-----------------------------------------------------------------------------------------------------------------
				//	사용가능한 차량정지위치로 적치열 조회 후 개소POINT를 구함
				//-----------------------------------------------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP",szYD_STK_COL_GP);
				
				intRtnVal = commDao.select(recInTemp, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0133");		
				
				if( intRtnVal <= 0) {
					szMsg="["+szOperationName+"] 사용가능한 개소POINT 조회 시 오류발생 - 메세지 : ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
				
				rsResult.first();
				recInTemp		= rsResult.getRecord();
				
				szWLOC_CD		= ydDaoUtils.paraRecChkNull(recInTemp,"WLOC_CD"); //2013.11.14 복수 창고 출하시 개소코드 변경하기 위하여 추가				
				szYD_PNT_CD		= ydDaoUtils.paraRecChkNull(recInTemp,"YD_PNT_CD");
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recInTemp,"YD_CARLD_STOP_LOC");
				
				szMsg="["+szOperationName+"] 사용가능한 차량정지위치["+szYD_STK_COL_GP+"]로 개소POINT 조회 완료 - 메세지 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			}
			//-----------------------------------------------------------------------------------------------------------------
			
			if("Y".equals(szDOUBLEDONG_CHECK)) { //복수동일 경우 
				/*
				 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 초기화 수정한다.
				 */
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    		szDOUBLEDONG_YD_CAR_SCH_ID);
				recInTemp.setField("REGISTER",         		szRcvTcCode);
				recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
				recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
				recInTemp.setField("SPOS_WLOC_CD",     		szWLOC_CD);								//발지개소코드
				recInTemp.setField("YD_PNT_CD1",     		szYD_PNT_CD);							//야드포인트코드1
				recInTemp.setField("CAR_NO",           		szCAR_NO);								//차량번호
				recInTemp.setField("CARD_NO",          		szCARD_NO);								//카드번호
				recInTemp.setField("YD_CARLD_LEV_DT",  		YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
		    	recInTemp.setField("YD_CARLD_STOP_LOC",		szYD_STK_COL_GP);						//차량상차정지위치
		    	recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
		    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		"2");									//입동지시순번  (2013.06.20 cho 1순위 --> 2순위 변경)
				recInTemp.setField("DEL_YN", 				"N");					
				
				//차량스케줄수정
		    	intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
		    	
	    		if( intRtnVal <= 0 ){
					szMsg="[" + szOperationName + "] 차량스케줄 수정 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
	    		}
	    		
	    		//차량 스케쥴재료수정
	    		YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao(); 
	        	intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(recInTemp, 1) ;
	            
	    		szMsg="[" + szOperationName + "] 차량스케줄 수정 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			} else { //복수동이 아닐 경우  

					/*
					 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
					 */
					szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID",    		szYD_CAR_SCH_ID);
					recInTemp.setField("REGISTER",         		szRcvTcCode);
					recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
					recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
					recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
					recInTemp.setField("CAR_KIND", 				StringHelper.evl(szCAR_KIND, "TR"));
					recInTemp.setField("SPOS_WLOC_CD",     		szWLOC_CD);								//발지개소코드
					recInTemp.setField("YD_PNT_CD1",     		szYD_PNT_CD);							//야드포인트코드1
					recInTemp.setField("CAR_NO",           		szCAR_NO);								//차량번호
					recInTemp.setField("CARD_NO",          		szCARD_NO);								//카드번호
					recInTemp.setField("YD_CARLD_LEV_DT",  		YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
					recInTemp.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
					recInTemp.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
			    	recInTemp.setField("YD_CARLD_STOP_LOC",		szYD_STK_COL_GP);						//차량상차정지위치
		    		recInTemp.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 --기본값으로 설정(9) **
					recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
					recInTemp.setField("IF_SEQ_NO", 			szUNIQUE_ID);							//운송지시 SEQ
					
					recInTemp.setField("YD_CAR_WRK_GP",    		szWORK_GP);
					recInTemp.setField("TEL_NO", 		   		szTEL_NO);								//기사핸드폰번호
					recInTemp.setField("CMBN_CARLD_YN",    		szCMBN_CARLD_YN);						//첫번째 도착창고 : S 두번째 도착창고 : E
					recInTemp.setField("WAIT_ARR_DDTT",    		szWAIT_ARR_DDTT);						//대기장도착시간
					recInTemp.setField("WAIT_ARR_GP",      		szWAIT_ARR_GP);							//대기장도착구분  - B:BACKUP , S:SMARTPHONE		
					
					
		    		//차량스케줄 등록
			    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
		    		if( intRtnVal <= 0 ){
						szMsg="[" + szOperationName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
		    		}
		    		
		    		szMsg="[" + szOperationName + "] 차량스케줄 생성 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}

			/*
			 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
			 */
		
			szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("TC_CODE"				,"YDYDJ633");
			recInTemp.setField("TC_CREATE_DDTT"			,YdUtils.getCurDate("yyyyMMddHHmmss"));
			recInTemp.setField("YD_CAR_STOP_LOC"		,szYD_STK_COL_GP);
			recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
			recInTemp.setField("CALL_PGM"			    ,"SANGCHA");
			
			//----------------------------------------------------------------------------------
			//	동기화 문제로 인하여 JMS --> EJB Call로 변경
			//	수정자 : 임춘수
			//	수정일 : 1) 2009.12.30 - 최초등록
			//----------------------------------------------------------------------------------
			//ydDelegate.sendMsg(recInTemp);
			
			EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
			szRtnMsg = (String) ejbConn.trx("procCarBayInOrdReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
			
			//----------------------------------------------------------------------------------
			szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 성공 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		}catch(Exception e){
		
			szMsg="["+szOperationName+"] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
			
		} // end of try-catch
		
		szMsg="["+szOperationName+"] 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
		
	} // end of procStandByYdArrivePlate()
	
	
	
	/**
	 * 코일이송상차대기장도착PDA(DMYDR070)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilGdsTrnOrdLdPDA(JDTORecord inRecord)throws JDTOException  {
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		YdCarSchDao ydCarSchDao	= new YdCarSchDao();
		YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();
		
		JDTORecord recInTemp		= JDTORecordFactory.getInstance().create();
		JDTORecord recEditColumn	= JDTORecordFactory.getInstance().create();
		JDTORecord	recPara			= JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult1 	= null;
		String szMethodName 		= "procCoilGdsTrnOrdLdPDA";
		String szMsg 				= "";
		String szOperationName      = "코일이송상차대기장도착PDA";
		String szSTL_NO 			= "";
		String szYD_CAR_SCH_ID 		= "";
		String szWLOC_CD			= "";
		String szYD_STK_COL_GP		= "";	
		
		String szYD_GP			 	= "";
		String szCARLD_PNT_CD 		= "";
		String szHANDLING_CNT 		= "";
		String szYD_PNT_CD 			= "";
		String szGDS_CARLD_LOC		= "";
		String szYD_STK_BED_NO		= "";
		String szMOV_YN				= "N";
		
		int intRtnVal 				= 0;
		int i =0;
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			 
			String sCAR_KIND =ydDaoUtils.paraRecChkNull(inRecord,"CAR_KIND"); //장비구분: Trailer-T , TT Trailer -TT
			
			recEditColumn 	= JDTORecordFactory.getInstance().create();
			recEditColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recEditColumn.setField("TRANS_ORD_DATE", 		ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
			recEditColumn.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
 
			//>>>>>>>>>>>>차량정보(하이스코 2냉연<<<<<<<<<<<<<<<<<<<
			recEditColumn.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO"));
			recEditColumn.setField("CAR_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO"));
			recEditColumn.setField("REHEAT_SLAB_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"WORK_GP"));
			recEditColumn.setField("COIL_CAR_NO",			ydDaoUtils.paraRecChkNull(inRecord,"CARLD_PNT_CD"));
			recEditColumn.setField("CR_FRTOMOVE_GP",		ydDaoUtils.paraRecChkNull(inRecord,"CR_FRTOMOVE_GP")); 
			 
			
			szMOV_YN = ydDaoUtils.paraRecChkNull(inRecord,"UGNT_BAYIN_YN");//복수상차 마지막 차량에 대한 구분 Y: 1순위
			szMsg= "["+szMethodName+"] 작업구분:::::"+ydDaoUtils.paraRecChkNull(inRecord,"WORK_GP")+" UGNT_BAYIN_YN:"+ szMOV_YN;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(sCAR_KIND.length()>2){ //장비구분: Trailer-T , TT Trailer -TT
				recEditColumn.setField("YD_STK_BED_NO", 		sCAR_KIND.substring(0, 2));
			}else {
				recEditColumn.setField("YD_STK_BED_NO", 		sCAR_KIND);
			}
			recEditColumn.setField("MODIFIER", 				szRcvTcCode);
 
			
			
			//****************************************************************************************************
			
			//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
			for(i = 1 ; i<=20; i++){
				inRecord.setField("STL_NO", 					ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i));
				
				recEditColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i));				
				recEditColumn.setField("YD_CAR_UPP_LOC_CD", 	ydDaoUtils.paraRecChkNull(inRecord,"GDS_CARLD_LOC"+i)); //야드 차상위치코드
 
				
				//STL_NO가 없다면 loop종료
				szSTL_NO = recEditColumn.getFieldString("STL_NO");
				if(szSTL_NO.equals("")){
					break;
				}				
				
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal= YdCommonUtils.getYdAimRtGp("C",inRecord );		
				recEditColumn.setField("YD_AIM_RT_GP", rVal[0]);
				recEditColumn.setField("STL_PROG_CD", rVal[1]);
				recEditColumn.setField("DEL_YN", "N");
					
				//작업예약만 존재 하는 경우 강제 삭제처리
				commDao.update(recEditColumn, "com.inisteel.cim.yd.ydStock.RouteModReg.updWbookCancel", "DMYDR070", szMethodName, "작업예약 삭제");
				
				commDao.update(recEditColumn, "com.inisteel.cim.yd.ydStock.RouteModReg.updWbookMtlCancel", "DMYDR070", szMethodName, "작업예약제료 삭제");
					
				//저장품갱신******************************************************************************************** 
				/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockReg*/
				intRtnVal = ydStockDao.updYdStockReg(recEditColumn);
				if(intRtnVal <= 0){
					szMsg= "YD_STOCK[코일이송상차대기장도착PDA] UPDATE Error :: [" + intRtnVal + "]" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[코일제품운송지시] UPDATE Success",3);
				//****************************************************************************************************


			} //end of for *******************************************************************************************
			
			
			
			String sWORK_GP =ydDaoUtils.paraRecChkNull(inRecord,"WORK_GP");
			
			
			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if(sWORK_GP.equals("9")){
			//차량정보 존재여부 체크 /////////////////////////////////////////////////////////////////
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DT"		, ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
			recPara.setField("TRANS_ORD_SEQNO"	, ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
			recPara.setField("CARD_NO"			, ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO") );

			//중복 check
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdDEL_YN*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 304);
			if(intRtnVal > 0){
				szMsg= "["+szMethodName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				
				rsResult1.first();
				recInTemp = rsResult1.getRecord();
				szYD_CAR_SCH_ID    = StringHelper.evl(recInTemp.getFieldString("YD_CAR_SCH_ID"), "");
			 
				recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				recInTemp.setField("DEL_YN", "Y");
				recInTemp.setField("MODIFIER", 	szRcvTcCode	);
				
				intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
				if( intRtnVal <= 0 ){
					szMsg="[" + szMethodName + "] 차량스케줄 삭제 시 오류발생[반환값 : " + intRtnVal + "]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);					 
	    		}
			}
			////////////////////////////////////////////////////////////////////////////////////////
			
			
			//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////
			
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CARPNT_CD"		, ydDaoUtils.paraRecChkNull(inRecord,"CARLD_PNT_CD"));
			
			//포인트코드 -> 개소코드와 저장위치 가져오기
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 412);
			if(intRtnVal > 0){
				szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 합니다..]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				rsResult1.first();
				recInTemp = rsResult1.getRecord();
				szWLOC_CD    		= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
				szYD_STK_COL_GP    	= StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP"), "");		
				szYD_PNT_CD	    	= StringHelper.evl(recInTemp.getFieldString("YD_PNT_CD"), "");		
				
				if(!"Y".equals(szMOV_YN)){
					szMOV_YN		    	= StringHelper.evl(recInTemp.getFieldString("MOV_YN"), "");	
				}
				
				/*
				 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
				 */
				szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
				recInTemp.setField("REGISTER",         szRcvTcCode);
				recInTemp.setField("YD_EQP_WRK_STAT",  "U");									//야드설비작업상태
				recInTemp.setField("YD_EQP_ID",        YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
				recInTemp.setField("SPOS_WLOC_CD",     szWLOC_CD);								//발지개소코드
				recInTemp.setField("CAR_NO",           ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO"));								//차량번호
				recInTemp.setField("CARD_NO",          ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO"));								//카드번호
				recInTemp.setField("YD_CARLD_LEV_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));						//운송지시순번
		    	recInTemp.setField("YD_CARLD_STOP_LOC",szYD_STK_COL_GP);						//차량상차정지위치
		    	
		    	if("Y".equals(szMOV_YN)){
		    		recInTemp.setField("YD_BAYIN_WO_SEQ",  "1");		//복수상차 마지막 차량에 대한 구분 Y: 1순위
		    	}else{
		    		recInTemp.setField("YD_BAYIN_WO_SEQ",  YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);		//입동지시순번 - 기본값으로 설정(9)
		    	}
		    	
				recInTemp.setField("YD_CAR_PROG_STAT", "1");									//상차출발상태
				recInTemp.setField("YD_CAR_WRK_GP", 	sWORK_GP);
				recInTemp.setField("YD_PNT_CD1", 	szYD_PNT_CD);
				recInTemp.setField("TRANS_EQUIPMENT_TYPE", 	"P");			//운송장비타입 P : PDA				
				
				if(sCAR_KIND.equals("TT")){
					recInTemp.setField("CAR_KIND",          "TT");									//차량종류
				}else{
					recInTemp.setField("CAR_KIND",          "TR");									//차량종류
				}
				
				recInTemp.setField("TEL_NO",   		ydDaoUtils.paraRecChkNull(inRecord,"TEL_NO"));						//연락처
				recInTemp.setField("DRIVER_NAME",  	ydDaoUtils.paraRecChkNull(inRecord,"DRIVER_NAME"));						//운전기사명
	    		
	    		//차량스케줄 등록
		    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
	    		if( intRtnVal <= 0 ){
					szMsg="[" + szMethodName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	 
	    		}
	    		
	    		
	    		
	    		int intYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNullInt(inRecord, "YD_EQP_WRK_SH");
	    		
	    		szMsg="[" + szMethodName + "] 차량스케줄 상차 수량: [ " + ydDaoUtils.paraRecChkNull(inRecord, "YD_EQP_WRK_SH") + " ]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
	    		//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
				for(i = 1 ; i<=intYD_EQP_WRK_SH; i++){
					szGDS_CARLD_LOC = ydDaoUtils.paraRecChkNull(inRecord,"GDS_CARLD_LOC"+i);
					
					if(szGDS_CARLD_LOC.substring(0 , 1).equals("A")){
						szYD_STK_BED_NO = "0"+szGDS_CARLD_LOC.substring(1 , 2);
					}else if(szGDS_CARLD_LOC.substring(0 , 1).equals("B")){
						if(szGDS_CARLD_LOC.substring(1 , 2).equals("1")){
							szYD_STK_BED_NO="06";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("2")){
							szYD_STK_BED_NO="07";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("3")){
							szYD_STK_BED_NO="08";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("4")){
							szYD_STK_BED_NO="09";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("5")){
							szYD_STK_BED_NO="10";
						}
					}else if(szGDS_CARLD_LOC.substring(0 , 1).equals("C")){
						if(szGDS_CARLD_LOC.substring(1 , 2).equals("1")){
							szYD_STK_BED_NO="11";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("2")){
							szYD_STK_BED_NO="12";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("3")){
							szYD_STK_BED_NO="13";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("4")){
							szYD_STK_BED_NO="14";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("5")){
							szYD_STK_BED_NO="15";
						}
					} 
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID",    		szYD_CAR_SCH_ID);
					recInTemp.setField("REGISTER",         		szRcvTcCode);
					recInTemp.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i));	 
					//recInTemp.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO); //야드 차상위치코드
					recInTemp.setField("YD_STK_LYR_NO", 		"001");
					recInTemp.setField("DEL_YN"					, "N");
			 
					intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(recInTemp);
		    		if(intRtnVal != 1) {
		    			szMsg="[" + szMethodName + "] 차량스케줄제료 생성 시 오류발생[반환값 : " + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		    		}


				} //end of for *******************************************************************************************
	    		
	    		if(sCAR_KIND.equals("TT")){
		    		//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			        EJBConnector ejbConn3 = new EJBConnector("default","JNDITsInfoReg",this);
					ejbConn3.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
				  	             new Object[]{"C","","",szYD_STK_COL_GP,"","","R"});
	    		}
			}else {
				szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 안합니다..]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			}
    		//////////////////////////////////////////////////////////////////////////////////////////
			
			
			//입동지시요구모듈 호출(trailer인 경우)///////////////////////////////////////////////////////
			if(sCAR_KIND.equals("T") || sCAR_KIND.equals("TR")){
				/*
				 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
				 */
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				recInTemp = JDTORecordFactory.getInstance().create();			 
				recInTemp.setField("YD_CARPNT_CD"			,ydDaoUtils.paraRecChkNull(inRecord,"CARLD_PNT_CD"));
				recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
				
				EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
				ejbConn.trx("procCarBayInOrdReqTr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				

				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
			}
			//////////////////////////////////////////////////////////////////////////////////////////

			
			}
			
		}catch(Exception e){

			szMsg="[코일이송상차대기장도착PDA] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch

		szMsg="코일이송상차대기장도착PDA 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


	} // end of procCoilGdsTrnOrdLdPDA()
	
	
	
	/**
	 * 코일이송하차대기장도착PDA(DMYDR073)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilGdsTrnOrdUdPDA(JDTORecord inRecord)throws JDTOException  {
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		YdCarSchDao ydCarSchDao	= new YdCarSchDao();
		YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();
		CoilSpecRegSeEJBBean CoilSpecRegSeEJBBean=new CoilSpecRegSeEJBBean();
		YdPlateCommDAO commDao 		= new YdPlateCommDAO(); 
		
		JDTORecord recInTemp		= JDTORecordFactory.getInstance().create();
		JDTORecord recEditColumn	= JDTORecordFactory.getInstance().create();
		JDTORecord	recPara			= JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult	 	= null;
		JDTORecordSet rsResult1 	= null;
		String szMethodName 		= "procCoilGdsTrnOrdUdPDA";
		String szMsg 				= "";
		String szOperationName      = "코일이송하차대기장도착PDA";
		String szSTL_NO 			= "";
		String szYD_CAR_SCH_ID 		= "";
		String szWLOC_CD			= "";
		String szYD_STK_COL_GP		= "";	
		
		String szYD_GP			 	= "";
		String szCARLD_PNT_CD 		= "";
		String szHANDLING_CNT 		= "";
		String szYD_PNT_CD 			= "";
		String szGDS_CARLD_LOC		= "";
		String szYD_STK_BED_NO		= "";
		String szMOV_YN				= "N";
		
		int intRtnVal 				= 0;
		int i =0;
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			 
			String sCAR_KIND =ydDaoUtils.paraRecChkNull(inRecord,"CAR_KIND"); //장비구분: Trailer-T , TT Trailer -TT
 
			recEditColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recEditColumn.setField("TRANS_ORD_DATE", 		ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
			recEditColumn.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO")); 
			recEditColumn.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO"));
			recEditColumn.setField("CAR_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO"));
			recEditColumn.setField("REHEAT_SLAB_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"WORK_GP"));
			recEditColumn.setField("COIL_CAR_NO",			ydDaoUtils.paraRecChkNull(inRecord,"CARUD_PNT_CD"));
			recEditColumn.setField("CR_FRTOMOVE_GP",		ydDaoUtils.paraRecChkNull(inRecord,"CR_FRTOMOVE_GP"));
			recEditColumn.setField("MODIFIER", 				szRcvTcCode);
			
			szMOV_YN = ydDaoUtils.paraRecChkNull(inRecord,"UGNT_BAYIN_YN");
			szMsg= "["+szMethodName+"] 작업구분:::::"+ydDaoUtils.paraRecChkNull(inRecord,"WORK_GP")+" UGNT_BAYIN_YN:"+ szMOV_YN;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(sCAR_KIND.length()>2){ //장비구분: Trailer-T , TT Trailer -TT
				recEditColumn.setField("YD_STK_BED_NO", 		sCAR_KIND.substring(0, 2));
			}else {
				recEditColumn.setField("YD_STK_BED_NO", 		sCAR_KIND);
			}
			
 
			
			
			//****************************************************************************************************
			
			//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
			for(i = 1 ; i<=20; i++){
				
				//STL_NO가 없다면 loop종료
				szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i);
				if(szSTL_NO.equals("")){
					break;
				}
				
				inRecord.setField("STL_NO", 					ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i));
				
				recEditColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i));				
				recEditColumn.setField("YD_CAR_UPP_LOC_CD", 	ydDaoUtils.paraRecChkNull(inRecord,"GDS_CARLD_LOC"+i)); //야드 차상위치코드
 
				
						
				
				//C열연 코일 저장품 등록 
	    		CoilSpecRegSeEJBBean.stockProcCom(szSTL_NO,1);
				
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal= YdCommonUtils.getYdAimRtGp("C",inRecord );		
				recEditColumn.setField("YD_AIM_RT_GP", "A1");
				recEditColumn.setField("STL_PROG_CD", rVal[1]);
				recEditColumn.setField("DEL_YN", "N");
					
				
				//저장품갱신******************************************************************************************** 
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("STL_NO", 		  szSTL_NO);
				
				intRtnVal = commDao.select(recInTemp, rsResult, "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.YdStockList");
				
				if(intRtnVal > 0){
					/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockReg*/
					intRtnVal = ydStockDao.updYdStockReg(recEditColumn);
					if(intRtnVal <= 0){
						szMsg= "YD_STOCK[코일이송상차대기장도착PDA] UPDATE Error :: [" + intRtnVal + "]" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
					ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[코일제품운송지시] UPDATE Success",3); 
				}else if(intRtnVal == 0){
					intRtnVal = this.insYdStock(recEditColumn);
					if(intRtnVal <= 0){
						szMsg= "YD_STOCK[코일이송상차대기장도착PDA] INSERT Error :: [" + intRtnVal + "]" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					}
					ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[코일제품운송지시] INSERT Success",3); 
				}

				//****************************************************************************************************


			} //end of for *******************************************************************************************
			
			
			
			String sWORK_GP =ydDaoUtils.paraRecChkNull(inRecord,"WORK_GP");
			
			szMsg= "["+szMethodName+"] 작업구분:::::"+sWORK_GP;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, 1);
			
			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if(sWORK_GP.equals("9")){
			//차량정보 존재여부 체크 /////////////////////////////////////////////////////////////////
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DT"		, ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
			recPara.setField("TRANS_ORD_SEQNO"	, ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
			recPara.setField("CARD_NO"			, ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO") );

			//중복 check
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdDEL_YN*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 304);
			if(intRtnVal > 0){
				szMsg= "["+szMethodName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				
				rsResult1.first();
				recInTemp = rsResult1.getRecord();
				szYD_CAR_SCH_ID    = StringHelper.evl(recInTemp.getFieldString("YD_CAR_SCH_ID"), "");
			 
				recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				recInTemp.setField("DEL_YN", "Y");
				recInTemp.setField("MODIFIER", 	szRcvTcCode);
				
				intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
				if( intRtnVal <= 0 ){
					szMsg="[" + szMethodName + "] 차량스케줄 삭제 시 오류발생[반환값 : " + intRtnVal + "]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);					 
	    		}
			}
			////////////////////////////////////////////////////////////////////////////////////////
			
			
			//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////
			
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CARPNT_CD"		, ydDaoUtils.paraRecChkNull(inRecord,"CARUD_PNT_CD"));
			
			//포인트코드 -> 개소코드와 저장위치 가져오기
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 412);
			if(intRtnVal > 0){
				szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 합니다..]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				rsResult1.first();
				recInTemp = rsResult1.getRecord();
				szWLOC_CD    		= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
				szYD_STK_COL_GP    	= StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP"), "");		
				szYD_PNT_CD	    	= StringHelper.evl(recInTemp.getFieldString("YD_PNT_CD"), "");		
				
				if(!"Y".equals(szMOV_YN)){
					szMOV_YN		    	= StringHelper.evl(recInTemp.getFieldString("MOV_YN"), "");	
				}
				
				/*
				 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
				 */
				szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    	szYD_CAR_SCH_ID);
				recInTemp.setField("REGISTER",         	szRcvTcCode);
				recInTemp.setField("YD_EQP_WRK_STAT",  	"L");													//야드설비작업상태(영차)
				recInTemp.setField("YD_EQP_ID",        	YdConstant.YD_DM_CAR_EQP_ID);							//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    	YdConstant.YD_CAR_USE_GP_DM);							//차량사용구분
				recInTemp.setField("SPOS_WLOC_CD",     	szWLOC_CD);
				recInTemp.setField("ARR_WLOC_CD",     	szWLOC_CD);												//발지개소코드
				recInTemp.setField("CAR_NO",           	ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO"));			//차량번호
				recInTemp.setField("CARD_NO",          	ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO"));			//카드번호
				recInTemp.setField("YD_CARUD_LEV_DT",  	YdUtils.getCurDate("yyyyMMddHHmmss"));					//하차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   	ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));		//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  	ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));	//운송지시순번
		    	recInTemp.setField("YD_CARUD_STOP_LOC",	szYD_STK_COL_GP);										//차량하차정지위치
//		    	recInTemp.setField("YD_BAYIN_WO_SEQ",  	YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);						//입동지시순번 - 기본값으로 설정(9)
		    	
		    	if("Y".equals(szMOV_YN)){
		    		recInTemp.setField("YD_BAYIN_WO_SEQ"      , "1");	//입동지시순번 - 제품이송 하차인 경우 1순위로 작업
		    	}else{
		    		recInTemp.setField("YD_BAYIN_WO_SEQ"      , "9");	//입동지시순번 - 제품이송 하차인 경우 1순위로 작업
		    	}
		    	
				recInTemp.setField("YD_CAR_PROG_STAT", 	"A");													//하차출발상태
				recInTemp.setField("YD_CAR_WRK_GP", 	sWORK_GP);
				recInTemp.setField("YD_PNT_CD3", 		szYD_PNT_CD);
				recInTemp.setField("TRANS_EQUIPMENT_TYPE", 		"P");
				 
				
				if(sCAR_KIND.equals("TT")){
					recInTemp.setField("CAR_KIND",          "TT");									//차량종류
				}else{
					recInTemp.setField("CAR_KIND",          "TR");									//차량종류
				}
				
				recInTemp.setField("TEL_NO",   		ydDaoUtils.paraRecChkNull(inRecord,"TEL_NO"));						//연락처
				recInTemp.setField("DRIVER_NAME",  	ydDaoUtils.paraRecChkNull(inRecord,"DRIVER_NAME"));						//운전기사명
	    		
	    		//차량스케줄 등록
		    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
	    		if( intRtnVal <= 0 ){
					szMsg="[" + szMethodName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	 
	    		}
	    		  
	    		int intYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNullInt(inRecord, "YD_EQP_WRK_SH");
	    		
	    		szMsg="[" + szMethodName + "] 차량스케줄 상차 수량: [ " + ydDaoUtils.paraRecChkNull(inRecord, "YD_EQP_WRK_SH") + " ]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
	    		//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
				for(i = 1 ; i<=intYD_EQP_WRK_SH; i++){
					szGDS_CARLD_LOC = ydDaoUtils.paraRecChkNull(inRecord,"GDS_CARLD_LOC"+i);
					
					if(szGDS_CARLD_LOC.substring(0 , 1).equals("A")){
						szYD_STK_BED_NO = "0"+szGDS_CARLD_LOC.substring(1 , 2);
					}else if(szGDS_CARLD_LOC.substring(0 , 1).equals("B")){
						if(szGDS_CARLD_LOC.substring(1 , 2).equals("1")){
							szYD_STK_BED_NO="06";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("2")){
							szYD_STK_BED_NO="07";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("3")){
							szYD_STK_BED_NO="08";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("4")){
							szYD_STK_BED_NO="09";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("5")){
							szYD_STK_BED_NO="10";
						}
					}else if(szGDS_CARLD_LOC.substring(0 , 1).equals("C")){
						if(szGDS_CARLD_LOC.substring(1 , 2).equals("1")){
							szYD_STK_BED_NO="11";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("2")){
							szYD_STK_BED_NO="12";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("3")){
							szYD_STK_BED_NO="13";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("4")){
							szYD_STK_BED_NO="14";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("5")){
							szYD_STK_BED_NO="15";
						}
					} 
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID",    		szYD_CAR_SCH_ID);
					recInTemp.setField("REGISTER",         		szRcvTcCode);
					recInTemp.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i));	 
					recInTemp.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO); //야드 차상위치코드
					recInTemp.setField("YD_STK_LYR_NO", 		"001");
					recInTemp.setField("DEL_YN"					, "N");
			 
					intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(recInTemp);
		    		if(intRtnVal != 1) {
		    			szMsg="[" + szMethodName + "] 차량스케줄제료 생성 시 오류발생[반환값 : " + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		    		}


				} //end of for *******************************************************************************************
				
	    		
	    		if(sCAR_KIND.equals("TT")){
		    		//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			        EJBConnector ejbConn3 = new EJBConnector("default","JNDITsInfoReg",this);
					ejbConn3.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
				  	             new Object[]{"C","","",szYD_STK_COL_GP,"","","R"});
	    		}
			}else {
				szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 안합니다..1]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			}
    		//////////////////////////////////////////////////////////////////////////////////////////
			
			
			//입동지시요구모듈 호출(trailer인 경우)///////////////////////////////////////////////////////
			if(sCAR_KIND.equals("T") || sCAR_KIND.equals("TR")){
				/*
				 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
				 */
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 시작1";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				recInTemp = JDTORecordFactory.getInstance().create();			 
				recInTemp.setField("YD_CARPNT_CD"			,ydDaoUtils.paraRecChkNull(inRecord,"CARUD_PNT_CD"));
				recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
				
				EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
				ejbConn.trx("procCarBayInOrdReqTr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				

				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공1";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
			}
			//////////////////////////////////////////////////////////////////////////////////////////

			
			}
			
		}catch(Exception e){

			szMsg="[코일이송하차대기장도착PDA] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch

		szMsg="코일이송하차대기장도착PDA 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


	} // end of procCoilGdsTrnOrdUdPDA()
	
	 /**
     * 오퍼레이션명 : C열연 저장품 insert
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
     * @param msgRecord, intGp
     * @return intRtnVal
     * @throws 
     */
    public int insYdStock (JDTORecord msgRecord){
    	CoilGdsJspDao dao = new CoilGdsJspDao();
    	
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "insYdStock";
        String szOperationName = "저장품 C 등록";
        try{
        	
        	/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.insYdStock*/
        	intRtnVal = dao.insYdStock(msgRecord);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	        }
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
        }//end of try~catch
        
        
        return  intRtnVal;
    	
    }//end of insYdStock()
	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                     일관제철소정보관리시스템-야드관리
	//              저장품관리-작업예정등록 Facade Session Bean
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

	
  //---------------------------------------------------------------------------

	///////////////////////////////////////////////////////////////////////////////
	///                          전사물류개선 프로젝트 2021.1.6                  ///
	///////////////////////////////////////////////////////////////////////////////
    
	/**
	 * 후판용 제품운송상차지시(DMYDR060) - (출하고도화)
	 *  - 전사물류개선 2021.1.6
	 *  - 복수동 상차우선순위(거래처별)
	 *  - 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlateGdsTrnOrd4GUpGrade(JDTORecord inRecord)throws JDTOException  {
		
		//기본 변수 정의
		String szMethodName 		= "procPlateGdsTrnOrd4GUpGrade";
		String szOperationName		= "후판용 제품운송상차지시(DMYDR060)";		
		String szMsg 				= "";
		
		String szCAR_NO 			= null;
		String szCARD_NO 			= null;
		String szTRANS_ORD_DATE		= null;
		String szTRANS_ORD_SEQNO	= null;
		String szCarLotId			= null;
		String szUNIQUE_ID          = null;
		String szSTL_NO				= null;
		String szCMBN_CARLD_YN 		= null;
		String szYD_GP				= null;
		String szCARLD_PNT_CD		= null;
		String szCARLD_PNT_CD_old	= null;
		String szHANDLING_CNT		= null;
		String szYD_STK_LOC			= null;
		String szYD_STK_LOC_old		= null;
		String szJOB_GP				= null;
		String szJOB_GP_old			= null;
		String szCAR_LOTID			= null;
		String szCANCEL_YN			= null;
		String szCAR_KIND = "";
		
		int intYD_EQP_WRK_SH		= 0;
		int intRtnVal 				= 0;
		
		JDTORecordSet rsResult1 	= null;
		JDTORecord	recPara			= null;
		JDTORecord	recEditColumn	= null;
		JDTORecord  recInTemp		= null;
		
		YdCarSchDao ydCarSchDao 	= new YdCarSchDao();	//차량스케줄DAO
		YdStockDao ydStockDao 		= new YdStockDao();		//저장품DAO
		YdPlateCommDAO commDao 		= new YdPlateCommDAO(); //3기 후판제품공용dao
		
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		// 2021. 4. 7 추가
		String szYD_FTMV_MEANS_GP = "";
		String szCR_FRTOMOVE_GP = "";
		
		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){
			szMsg="[" + szOperationName + "] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{	
			
			szCAR_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO");
			szCARD_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO");
			szTRANS_ORD_DATE 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT");
			szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO");
			intYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNullInt(inRecord,"YD_EQP_WRK_SH");
			szCarLotId			= ydDaoUtils.paraRecChkNull(inRecord,"LOT_NO");
			szUNIQUE_ID 		= ydDaoUtils.paraRecChkNull(inRecord,"UNIQUE_ID");	
			szCMBN_CARLD_YN 	= ydDaoUtils.paraRecChkNull(inRecord,"CMBN_CARLD_YN");
			szCANCEL_YN			= ydDaoUtils.paraRecChkNull(inRecord,"CANCEL_YN");
			// 2021. 4. 7 추가
			szYD_FTMV_MEANS_GP	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_FRTOMOVE_GP");
			szCR_FRTOMOVE_GP	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_METHOD_GP");
			 
			try{
				szCAR_KIND 	= ydDaoUtils.paraRecChkNull(inRecord,"CAR_KIND");
			}catch(Exception ex){
				szCAR_KIND = "TR";
			};
			
			szMsg="["+szOperationName+"] szUNIQUE_ID:" + szUNIQUE_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if("".equals(szUNIQUE_ID)){
				szUNIQUE_ID = "0";
			}		
			
			//전문항목 에러체크
			if("".equals(szCAR_NO)){
				szMsg="[" + szOperationName + "] CAR_NO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//return ;
			}
			//if("".equals(szCARD_NO)){
			//	szMsg="[" + szOperationName + "] CARD_NO IS NULL ("+szRcvTcCode+")";
			//	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//	return ;
			//}
			if("".equals(szTRANS_ORD_DATE)){
				szMsg="[" + szOperationName + "] TRANS_ORD_DT IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			if("".equals(szTRANS_ORD_SEQNO)){
				szMsg="[" + szOperationName + "] TRANS_ORD_SEQNO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			//-------------------------------------------------------------------------------------------------------------
			if("Y".equals(szCANCEL_YN)) { //취소일 경우
				
		    	/*
		    	 * 후판출하상차지시 취소시점에 예외사항 체크 
		    	 */				
	    		JDTORecordSet rsChkPara	= null;
	    		JDTORecord recChkPara	= null;
	    		String sChkStlNo		= "";
	    		
	    		YdWrkbookMtlDao ydWrkbookDao     = new YdWrkbookMtlDao();
	    		
	    		intYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNullInt(inRecord,"YD_EQP_WRK_SH");
	    		
	    		for(int index = 0; index < intYD_EQP_WRK_SH; index++){
	    			
	    			rsChkPara = JDTORecordFactory.getInstance().createRecordSet("");
	    			recChkPara  = JDTORecordFactory.getInstance().create();
	    			
	    			sChkStlNo = ydDaoUtils.paraRecChkNull(inRecord, "STL_NO" + (index+1));
	    			
	    			if("".equals(sChkStlNo)) break;
	    			
	    			recChkPara.setField("STL_NO" , sChkStlNo);
	    			
	    			intRtnVal = ydWrkbookDao.getYdWrkbookmtl(recChkPara, rsChkPara, 2);
	    			
	    			if(intRtnVal <= 0) {
					}else{
						szMsg="["+szMethodName+"] 후판출하상차지시 취소시 이미 작업예약에 재료 존재함.["+sChkStlNo+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return;
					}
	    		}
				
	    		YdStkBedDao ydStkBedDao = new YdStkBedDao();
	    		
	    		/*
	    		 * 취소처리 : 운송지시,순번,차량번호가 일치하는 것만 취소처리 한다.
	    		 */
	    		recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DATE);
				recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
				recPara.setField("CAR_NO"	, szCAR_NO);
	    		
	    		for(int index = 0; index < intYD_EQP_WRK_SH; index++){
	    			
					szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+(index+1));
					recPara.setField("STL_NO"	, szSTL_NO);
					
					
					// 2021. 06. 03 트랜잭 이슈로 인하여 분리처리한다.
					// 운송지시 취소 및 재 편성작업시 동일 재료가 동시에 처리되는 문제가 발생하여
					// 트랜잭션 분리조치함
					// 간혹 트랜잭션 이슈가 발생하여 조치함
//	    			intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0047");
	    			recPara.setField("QUERY_ID", "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0047");
					// 2021. 06. 03 트랜잭션 분리를 위한 EJb Bean설정
					EJBConnector 	ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
					ejbConn.trx("updYdStkColTX", new Class[] { JDTORecord.class }, new Object[] { recPara });	
					
	    			/*
	    		     * 선별LOT편성정보 취소
	    		     *
	    		     * 출하에서  선별Lot편성취소  정보가 수신되면  해당 저장위치 상태변경
	    		     *  야드베드입출고상태 : 
	    		     *  선별상태 : 
	    		     */
	    		    intRtnVal = ydStkBedDao.updYdStkBedStat_02(recPara);
	    		}
	    		
	    		//=========================================================
				//	차량스케줄 삭제 및 차량 POINT Clear
				//=========================================================
				ydUtils.putLog(szSessionName, szMethodName, "차량스케줄삭제 및 차량Point Clear 시작", YdConstant.DEBUG);
//PIDEV_S :병행가동용:PI_YD
				inRecord.setField("PI_YD",    	"T");							
				String szRtnMsg = YdCommonUtils.delCarSchNCarPointForDist(inRecord, szMethodName);
				ydUtils.putLog(szSessionName, szMethodName, "차량스케줄삭제 및 차량Point Clear 완료 - " + szRtnMsg, YdConstant.DEBUG);
				
				
				// 전사물류개선 2021. 4. 3
				JDTORecord params = JDTORecordFactory.getInstance().create();
				JDTORecordSet rsBedRecord 	= JDTORecordFactory.getInstance().createRecordSet("ydPlate");
				params.setField("TRANS_ORD_DATE"           	,szTRANS_ORD_DATE);
				params.setField("TRANS_ORD_SEQNO"         	,szTRANS_ORD_SEQNO);
				if(commDao.select(params, rsBedRecord, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getDMYDR060_SendToL2List") > 0){
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
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			recPara.setField("CARD_NO"			, szCARD_NO );
			recPara.setField("DEL_YN"			, "N" );
			
			intRtnVal = commDao.select(recPara, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0087_PIDEV");
			if(intRtnVal > 0){
				szMsg= "["+szOperationName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
				
			//---------------------------------------------------------------------------------
			//	YD저장품에 운송상차지시 정보를 등록(UPDATE) 한다.
			//---------------------------------------------------------------------------------			
			recEditColumn 	= JDTORecordFactory.getInstance().create();
			recEditColumn.setField("YD_RULE_PL_RS_GP", 		szCMBN_CARLD_YN);
			recEditColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recEditColumn.setField("TRANS_ORD_DATE", 		szTRANS_ORD_DATE);
			recEditColumn.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
			recEditColumn.setField("CAR_NO", 				szCAR_NO);
			recEditColumn.setField("CARD_NO", 				szCARD_NO);
			//recEditColumn.setField("ARR_WLOC_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"ARR_WLOC_CD"));
			//recEditColumn.setField("YD_PNT_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"ARR_YD_PNT_CD"));
			//recEditColumn.setField("YD_STK_LOT_CD", 		ydDaoUtils.paraRecChkNull(inRecord,"TEL_NO"));
			if(!"".equals(szCarLotId)) {
				recEditColumn.setField("CAR_LOTID", 			szCarLotId);
			}
			recEditColumn.setField("MODIFIER", 				szRcvTcCode);
			
			szMsg="[" + szOperationName + "]["+intYD_EQP_WRK_SH+"]매수에 대한  저장품 업데이트 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			// 전사물류개선 차량종류
			String szCarKind = ydDaoUtils.paraRecChkNull(inRecord,"CAR_KIND");
			String szYD_CAR_UPP_LOC_CD = ""; // 거래처별 우선순위
			
			for(int i = 1 ; i<= intYD_EQP_WRK_SH; i++){
				
				szSTL_NO = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i);
				szYD_CAR_UPP_LOC_CD = ydDaoUtils.paraRecChkNull(inRecord,"GDS_CARLD_LOC"+i);
				if(szSTL_NO.equals("")){
					break;
				}else {
					inRecord.setField("STL_NO", 	szSTL_NO);
				}
				recEditColumn.setField("STL_NO",	szSTL_NO);
				
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)***************************************************
				//recPara.setField("STL_NO",  szSTL_NO);
				//recPara.setField("TC_CODE", szRcvTcCode);
				//rVal= YdCommonUtils.getYdAimRtGp("P",recPara );		
				//recEditColumn.setField("YD_AIM_RT_GP", rVal[0]);
				//recEditColumn.setField("STL_PROG_CD", rVal[1]);				
				
				recEditColumn.setField("YD_AIM_RT_GP", "L6"); 
				recEditColumn.setField("STL_PROG_CD",  "L");  //운송대기
				
				
				// 전사물류개선 2021.1.21
				// 차상위치필드를 거래처별 우선순위로 설정한다.
				// 차량종류는 TB_YD_STOCK의 YD_CONVEYOR_BRANCH_CD에 재료별로 담는다.
				recEditColumn.setField("YD_CAR_UPP_LOC_CD",  szYD_CAR_UPP_LOC_CD); 
				
				if("".equals(szYD_CAR_UPP_LOC_CD)){
					szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 거래처우선순위가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					recEditColumn.setField("YD_CAR_UPP_LOC_CD",  "1"); 
//					throw new Exception(szMsg);
				}
				
				// 2021. 4. 7 항목추가
				recEditColumn.setField("YD_FTMV_MEANS_GP", szYD_FTMV_MEANS_GP);
				recEditColumn.setField("CR_FRTOMOVE_GP", szCR_FRTOMOVE_GP);
				
				/* 
				 * 1. 저장품테이블의 재료들에 재료진도코드와 야드목표행선을 수정처리
				 */
				//저장품갱신******************************************************************************************** 
				szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 야드목표행선[L6]과 재료진도코드[L] 업데이트 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				intRtnVal = ydStockDao.updYdStock(recEditColumn, 0);
				if(intRtnVal <= 0){
					szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 야드목표행선[L6]과 재료진도코드[L] 업데이트 시 오류발생 - 반환값 : " +intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				szMsg="[" + szOperationName + "] ["+i+"] 재료["+ szSTL_NO +"]에 대한 야드목표행선[L6]과 재료진도코드[L] 업데이트 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//****************************************************************************************************
				
			}
			
			
			//야드핸들링정보 송신////////////////////////////////////////////////////////////////////////////////
			//최대 폭			
			double dblMaxWidth  = 0;
			//현재 폭
			double dblCurrWidth = 0;
			//최대 두께			
			double dblMaxThick  = 0;
			//현재 두께
			double dblCurrThick = 0;
			//중량의 합
			//long lngSumWt     = 0;		
			//현재 중량
			//long lngCurrWt    = 0;			
			//재료매수
			int intMtlSh      = 0;	
			//크레인작업가능매수
			int intCrnWrkableSh	= 0;
			//핸들링 수 
			int intHndlingCnt = 1;
			
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara.setField("TRANS_ORD_DT"		, szTRANS_ORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			
			intRtnVal = commDao.select(recPara, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0086");	
			
			if( intRtnVal > 0 ){
				
				for( int i = 1; i <= rsResult1.size(); i++ ) {
					
					rsResult1.absolute(i);
					recPara = rsResult1.getRecord();
					
					szCARLD_PNT_CD 		= ydDaoUtils.paraRecChkNull(recPara,"YD_CARPNT_CD"); 
					szYD_GP				= szCARLD_PNT_CD.substring(0,1);
					szYD_STK_LOC		= ydDaoUtils.paraRecChkNull(recPara,"YD_STK_LOC");
					szJOB_GP			= ydDaoUtils.paraRecChkNull(recPara,"JOB_GP");
					szCAR_LOTID			= ydDaoUtils.paraRecChkNull(recPara,"CAR_LOTID");
					
					if( i == 1 ) {
						szCARLD_PNT_CD_old	= szCARLD_PNT_CD;
						szYD_STK_LOC_old	= szYD_STK_LOC;
						szJOB_GP_old		= szJOB_GP;
					}		
					
					if("A".equals(szJOB_GP) && !"".equals(szCAR_LOTID)) {
						//주작업대상이 아닌 보조작업(이적)대상에 차량lotId가 셋팅되어 있다면 핸들링 Count 에서 배제한다.
						continue;
					}
					
					if(!szYD_STK_LOC_old.equals(szYD_STK_LOC) || !szJOB_GP_old.equals(szJOB_GP)) {
						//위치가 변경되었거나, 주작업/보조작업이 변경되었다면 핸들링수 1 증가
						intHndlingCnt++;
						
		    			//폭,중량,매수 초기화
		    			dblCurrWidth = 0;
		    			dblCurrThick = 0;
		    			dblMaxWidth = 0;
		    			dblMaxThick = 0;
		    			intMtlSh = 0;							
					}
					
					if(!szCARLD_PNT_CD_old.equals(szCARLD_PNT_CD)) {
						//차량 POINT 가 변경 되었을 경우
						szMsg="상차완료 송신 YDDMR050 (야드핸들링정보) 송신 시작";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("MSG_ID",        			"YDDMR050");
						recInTemp.setField("YD_GP"           	 		,szYD_GP );
						recInTemp.setField("TRANS_ORD_DT"           	,szTRANS_ORD_DATE);
						recInTemp.setField("TRANS_ORD_SEQNO"         	,szTRANS_ORD_SEQNO);
						recInTemp.setField("CMBN_CARLD_YN"         		,szCMBN_CARLD_YN );
						recInTemp.setField("CARLD_PNT_CD"         		,szCARLD_PNT_CD_old );
						recInTemp.setField("CAR_NO"           			,szCAR_NO );
						recInTemp.setField("HANDLING_CNT"          		,Integer.toString(intHndlingCnt)); 
						recInTemp.setField("YD_STK_BED_WHIO_STAT"       ,"" );

						ydDelegate.sendMsg(recInTemp);
						
						szMsg="상차완료 송신 YDDMR050 (야드핸들링정보) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
						
						
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
    				dblCurrWidth = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_MTL_W");
    				//재료의 현재 두께
    				dblCurrThick = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_MTL_T");
    				
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
    				
					szCARLD_PNT_CD_old	= szCARLD_PNT_CD;
					szYD_STK_LOC_old	= szYD_STK_LOC;
					szJOB_GP_old		= szJOB_GP;    				
				}
				
				
				// 전사물류개선 2021. 1. 6
				// 길이 폭으로 차종을 구한다.
				// --가변슬라이드 : 폭이 3400 초과 && 길이 14,000 초과
				// --가변차량 : 폭이 3400 초과 && 길이 14,000 이하
				// --일반 : 폭이 3400 이하 && 길이 14,000 이하
				// --일반슬라이드 : 폭이 3400 이하 && 길이 14,000 초과
				JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
				JDTORecordSet rsResult2 	= JDTORecordFactory.getInstance().createRecordSet("");
				String sCarKind = "";
				if(szCARD_NO.startsWith("P")){
					sCarKind = "PT";
				}
				else if("P12".equals(szCAR_KIND) 
							|| "P18".equals(szCAR_KIND) 
							|| "PX".equals(szCAR_KIND) 
							|| "PY".equals(szCAR_KIND) 
							|| "PU".equals(szCAR_KIND) ){
					sCarKind = "PT";
				}
				else{
					recInTemp1.setField("TRANS_ORD_DATE"           	,szTRANS_ORD_DATE);
					recInTemp1.setField("TRANS_ORD_SEQNO"         	,szTRANS_ORD_SEQNO);
					recInTemp1.setField("CAR_NO"           			,szCAR_NO );
					if(commDao.select(recInTemp1, rsResult2, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCarKindByWdithLength")>0){
						sCarKind =  rsResult2.getRecord(0).getFieldString("CAR_KIND");
					}
					else{
						sCarKind = "TR";
					}
				}
				
				// YD_STOCK 차량TYPE정보 Set				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CONVEYOR_BRANCH_CD", sCarKind); 
				recInTemp.setField("MODIFIER"    , "DMYDR060"  );	//수정자
				recInTemp.setField("TRANS_ORD_DATE"           	,szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO"         	,szTRANS_ORD_SEQNO);
				recInTemp.setField("CAR_NO"           			,szCAR_NO );
				commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updYdCarTransType2");
				
				
				//for 문 밖에서 마지막 차량POINT의 핸들링 수를 전송한다.
				szMsg="상차완료 송신 YDDMR050 (야드핸들링정보) 송신 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID",        			"YDDMR050");
				recInTemp.setField("YD_GP"           	 		,szYD_GP );
				recInTemp.setField("TRANS_ORD_DT"           	,szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO"         	,szTRANS_ORD_SEQNO);
				recInTemp.setField("CMBN_CARLD_YN"         		,szCMBN_CARLD_YN );
				recInTemp.setField("CARLD_PNT_CD"         		,szCARLD_PNT_CD );
				recInTemp.setField("CAR_NO"           			,szCAR_NO );
				recInTemp.setField("HANDLING_CNT"          		,Integer.toString(intHndlingCnt)); 
				recInTemp.setField("YD_STK_BED_WHIO_STAT"       ,"" );

				ydDelegate.sendMsg(recInTemp);
				
				szMsg="상차완료 송신 YDDMR050 (야드핸들링정보) 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
			}
			/////////////////////////////////////////////////////////////////////////////////////////////////
			
			
		}catch(Exception e){

			szMsg="["+szOperationName+"] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch
		
		szMsg="["+szOperationName+"] 수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
		
	} // end of procPlateGdsTrnOrd4GUpGrade()
	
	/**
	 * 후판용 대기장도착실적(DMYDR061) - (출하고도화)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procStandByYdArrivePlate4G(JDTORecord inRecord)throws JDTOException  {
		
		//기본 변수 정의
		String szMethodName 		= "procStandByYdArrivePlate4G";
		String szOperationName		= "후판용 대기장도착실적(DMYDR061)";		
		String szMsg 				= "";
		
		String szYD_GP				= null;
		String szCMBN_CARLD_YN		= null;
		String szWORK_GP			= null;
		String szTEL_NO				= null;
		String szTRANS_ORD_DATE		= null;
		String szTRANS_ORD_SEQNO	= null;
		String szCAR_NO 			= null;
		String szCARD_NO			= null;
		String szCAR_KIND			= null;
		String szCARLD_PNT_CD		= null;
		String szWAIT_ARR_DDTT		= null;
		String szWAIT_ARR_GP		= null;
		String szUNIQUE_ID          = null;
		
		String szYD_STK_COL_GP		= null;
		String szYD_BAY_GP			= null;
		String szYD_EQP_GP			= null;
		
		String szAPPLY_YN  			= "N";
		String szYD_SAPN_COL_GP     = null;
		String szWLOC_CD			= null;
		String szYD_STK_COL_ACT_STAT = null;
		String szRtnMsg				= null;
		
		String szYD_PNT_CD			= null;
		String szYD_CAR_SCH_ID		= null;
		String szSHIP_NAME   		= null;
		String szRENTPROC_CD   		= null;
		
		String szDOUBLEDONG_CHECK	= null;
		String szDOUBLEDONG_YD_CAR_SCH_ID = null;
		
		JDTORecordSet rsResult		= null;
		JDTORecordSet rsResult1 	= null;
		JDTORecordSet rsGetStock 	= null;
		
		JDTORecord	recPara			= null;		
		JDTORecord	recInTemp		= null;
		JDTORecord  recStlNo 		= null;
		
		int intRtnVal 				= 0;
		
		int intA_DONG  				= 0;
		int intB_DONG  				= 0;
		int intC_DONG  				= 0;
		int intD_DONG  				= 0;
		int intE_DONG  				= 0;
		int intF_DONG  				= 0;
		int intG_DONG  				= 0;
		int intYD_SAPN_COL_GP     	= 0;		
		int nTRANS_ORD_SEQNO = 0; // 전사물류개선프로젝트 2021.1.6 추가
		
		YdCarSchDao ydCarSchDao 	= new YdCarSchDao();	//차량스케줄DAO
		YdPlateCommDAO commDao 		= new YdPlateCommDAO(); //3기 후판제품공용dao
		YdEqpDao   ydEqpDao   		= new YdEqpDao();		//야드설비 DAO
		YdStkColDao ydStkColDao 	= new YdStkColDao();	//적치열 DAO

		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=ydUtils.getTcCode(inRecord);
 
		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){
			szMsg="[" + szOperationName + "] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		// 전사물류개선 2021. 1 6 전차량정차위치
		String szBefore_YD_CARLD_STOP_LOC = "";
		
		// 2021. 4. 7 추가
		String szYD_FTMV_MEANS_GP = "";
		String szCR_FRTOMOVE_GP = "";
		String szYD_CARPNT_CD   = "";
		try{ 
			
			szYD_GP 				= ydDaoUtils.paraRecChkNull(inRecord,"YD_GP");
			szCMBN_CARLD_YN 		= ydDaoUtils.paraRecChkNull(inRecord,"CMBN_CARLD_YN");
			//조합상차(시작:S , 종료: E ,  단일상차: N )
			szCMBN_CARLD_YN 		= StringHelper.evl(szCMBN_CARLD_YN, "N");
			szWORK_GP 				= ydDaoUtils.paraRecChkNull(inRecord,"WORK_GP");
			szTEL_NO 				= ydDaoUtils.paraRecChkNull(inRecord,"TEL_NO");
			szTRANS_ORD_DATE  		= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT");
			szTRANS_ORD_SEQNO 		= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO");
			szCAR_NO 				= ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO");
			szCARD_NO 				= ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO");
			szCAR_KIND 				= ydDaoUtils.paraRecChkNull(inRecord,"CAR_KIND");
			szCARLD_PNT_CD			= ydDaoUtils.paraRecChkNull(inRecord,"CARLD_PNT_CD");
			szWAIT_ARR_DDTT			= ydDaoUtils.paraRecChkNull(inRecord,"WAIT_ARR_DDTT");
			szWAIT_ARR_GP			= ydDaoUtils.paraRecChkNull(inRecord,"WAIT_ARR_GP");	
			szUNIQUE_ID 			= ydDaoUtils.paraRecChkNull(inRecord,"UNIQUE_ID");
			szSHIP_NAME				= ydDaoUtils.paraRecChkNull(inRecord,"SHIP_NAME");
			szRENTPROC_CD			= ydDaoUtils.paraRecChkNull(inRecord,"CRN_WRK_METHOD_GP");
			szYD_CARPNT_CD			= ydDaoUtils.paraRecChkNull(inRecord,"YD_CARPNT_CD");
			
			// 2021. 4. 7 항목추가
			szYD_FTMV_MEANS_GP	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_FRTOMOVE_GP");
			szCR_FRTOMOVE_GP	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_METHOD_GP");

			// 전사물류개선프로젝트 2021.1.6 추가
			try{ nTRANS_ORD_SEQNO = Integer.parseInt(szTRANS_ORD_SEQNO); }catch(Exception e){ nTRANS_ORD_SEQNO=0; }
			
//PIDEV_S :병행가동용:PI_YD		
//			String sApplyYnPI   = ydPICommDAO.ApplyYnPI("", szMethodName, "APPPI0", "T", "*");
			if("PIDEV".equals("PIDEV")) {
				JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
				recPara1.setField("MQ_TC_CD"			, "M10LMYDJ1042");
				recPara1.setField("MQ_TC_CREATE_DDTT"	, YdUtils.getCurDate("yyyyMMddHHmmss"));
				recPara1.setField("YD_GP"				, szYD_GP);
				recPara1.setField("CMBN_CARLD_YN"		, szCMBN_CARLD_YN);
				recPara1.setField("WORK_GP"				, szWORK_GP);
				recPara1.setField("TEL_NO"				, szTEL_NO);
				recPara1.setField("TRN_REQ_DATE"		, szTRANS_ORD_DATE);
				recPara1.setField("TRN_REQ_SEQ"			, szTRANS_ORD_SEQNO);
				recPara1.setField("CAR_KIND"			, szCARD_NO);
				recPara1.setField("CAR_NO"				, szCAR_NO);
				recPara1.setField("SHIP_NAME"			, szSHIP_NAME);
//				recPara1.setField("CRN_WRK_METHOD_GP"	, szTRANS_ORD_DATE);
				recPara1.setField("TRANS_FRTOMOVE_GP"	, szYD_FTMV_MEANS_GP);
				recPara1.setField("TRANS_METHOD_GP"		, szCR_FRTOMOVE_GP);
				recPara1.setField("YD_CARPNT_CD"		, szYD_CARPNT_CD);
				szDOUBLEDONG_CHECK  		= ydDaoUtils.paraRecChkNull(inRecord,"DOUBLEDONG_CHECK");
				szDOUBLEDONG_YD_CAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(inRecord,"YD_CAR_SCH_ID"); 	
				recPara1.setField("DOUBLEDONG_CHECK"	, szDOUBLEDONG_CHECK);
				recPara1.setField("YD_CAR_SCH_ID"		, szDOUBLEDONG_YD_CAR_SCH_ID);
				
				EJBConnector ejbConn1 = new EJBConnector("default", "YdPlateL3RcvPISeEJB", this);  
				ejbConn1.trx("procM10LMYDJ1042",  new Class[] { JDTORecord.class }, new Object[] { recPara1 });
				return;
				
			}

			// 차량종류가 세분화되어 
//			TV	가변기
//			TS	슬라이더
//			VS	슬라이더+가변기
//			TR	일반
//			PT	Pallet
			if("TV".equals(szCAR_KIND) || "TS".equals(szCAR_KIND) || "VS".equals(szCAR_KIND)){
				szCAR_KIND = "TR";
			}
			
			
			szMsg="["+szOperationName+"] szUNIQUE_ID:" + szUNIQUE_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if("".equals(szUNIQUE_ID)){
				szUNIQUE_ID = "0";
			}		
			
			//전문항목 에러체크
			if("".equals(szCAR_NO)){
				szMsg="[" + szOperationName + "] CAR_NO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			//if("".equals(szCARD_NO)){
			//	szMsg="[" + szOperationName + "] CARD_NO IS NULL ("+szRcvTcCode+")";
			//	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//	return ;
			//}
			if("".equals(szTRANS_ORD_DATE)){
				szMsg="[" + szOperationName + "] TRANS_ORD_DT IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			if("".equals(szTRANS_ORD_SEQNO)){
				szMsg="[" + szOperationName + "] TRANS_ORD_SEQNO IS NULL ("+szRcvTcCode+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			//---------------------------------------------------------------------------------
			//	DEL_YN = 'N' 인 차량스케줄이 존재할경우 SKIP처리한다. 
			//---------------------------------------------------------------------------------
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			recPara.setField("CARD_NO"			, szCARD_NO );
			recPara.setField("DEL_YN"			, "N" );
			
			intRtnVal = commDao.select(recPara, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0087_PIDEV");
			if(intRtnVal > 0){
				szMsg= "["+szOperationName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}	
			
			if(!"".equals(szTEL_NO)) {
				//---------------------------------------------------------------------------------
				//	TB_YD_STOCK 에 TEL_NO 을 셋팅한다.
				//---------------------------------------------------------------------------------			
				recPara.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DATE);
				recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
				recPara.setField("YD_STK_LOT_CD"	, szTEL_NO);
				
				intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0030");
			}
			
			
			/*
			 *  복수동 출하시 나머지 출하작업에 대한 상태 = 'Y'
			 *  -- 권하처리시 선 복수동 작업일때 해당항목을 셋팅
			 */
			szDOUBLEDONG_CHECK  		= ydDaoUtils.paraRecChkNull(inRecord,"DOUBLEDONG_CHECK");
			szDOUBLEDONG_YD_CAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(inRecord,"YD_CAR_SCH_ID"); 			
			

			//--------------------------------------------------------------------------------------
			// 이하 AS-IS 루틴 그대로 사용
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			if(szCARD_NO.startsWith("P")){ // PALLET 출하 
				
				//차량포인트 테이블에서 WLOC_CD 와 YD_PNT_CD 를 읽어 온다. 
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				
				if("".equals(szCARLD_PNT_CD)){
				//출하포인트코드
				szCARLD_PNT_CD = ydDaoUtils.paraRecChkNull(inRecord, "YD_CARPNT_CD");
				}
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CARPNT_CD", 		  szCARLD_PNT_CD);
				
				intRtnVal = commDao.select(recInTemp, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0102");
				
				if(intRtnVal <= 0){
					szMsg="["+szOperationName+"] PALLET 출하 차량포인트 조회 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
					return ;
				}
				
				rsResult.first();
				recInTemp = rsResult.getRecord();
				
				szWLOC_CD   	= ydDaoUtils.paraRecChkNull(recInTemp,"WLOC_CD");
				szYD_PNT_CD 	= ydDaoUtils.paraRecChkNull(recInTemp,"YD_PNT_CD");
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recInTemp,"YD_STK_COL_GP");
				
				/*
				 * 2. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
				 */
				szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    		szYD_CAR_SCH_ID);
				recInTemp.setField("REGISTER",         		szRcvTcCode);
				recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
				recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
				recInTemp.setField("CAR_KIND", 				szCAR_KIND);							//차량종류
				recInTemp.setField("SPOS_WLOC_CD",     		szWLOC_CD);								//발지개소코드
				recInTemp.setField("YD_PNT_CD1",     		szYD_PNT_CD);							//야드포인트코드1
				recInTemp.setField("CAR_NO",           		szCAR_NO);								//차량번호
				recInTemp.setField("CARD_NO",          		szCARD_NO);								//카드번호
				recInTemp.setField("YD_CARLD_LEV_DT",  		YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
		    	recInTemp.setField("YD_CARLD_STOP_LOC",		szYD_STK_COL_GP);						//차량상차정지위치
		    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 --기본값으로 설정(9) **
				recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
				recInTemp.setField("SHIP_NAME", 			szSHIP_NAME);							//선박명
				recInTemp.setField("RENTPROC_CD", 			szRENTPROC_CD);							//해송 줄거리작업 방법 M:마그네틱, T:줄거리
				recInTemp.setField("IF_SEQ_NO", 			szUNIQUE_ID);	// 운송지시 SEQ
				recInTemp.setField("TRANS_EQUIPMENT_TYPE", 	"PT");	// 세부차종
				
	    		//차량스케줄 등록
		    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
	    		if( intRtnVal <= 0 ){
					szMsg="[" + szOperationName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
	    		}
	    		
	    		szMsg="[" + szOperationName + "] 차량스케줄 생성 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				/*
				 * 2016.06.28 윤 재광
				 * 상차 후 복수동으로 가기위해 장착할 경우 이 이벤트 발생
				 * 이 시점에 해당 포인트 예약처리 
				 */
				recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
		    	recInTemp.setField("YD_STK_COL_ACT_STAT", "R");					
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0018");	
				
				recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_CARPNT_CD", szCARLD_PNT_CD);
		    	recInTemp.setField("YD_STK_COL_ACT_STAT", "R");					
				
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
				
				/*
				 * 3. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
				 */
				//szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 시작";
				//ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//recInTemp = JDTORecordFactory.getInstance().create();
				//recInTemp.setField("TC_CODE"				,"YDYDJ633");
				//recInTemp.setField("TC_CREATE_DDTT"		,YdUtils.getCurDate("yyyyMMddHHmmss"));
				//recInTemp.setField("YD_CAR_STOP_LOC"		,szYD_STK_COL_GP);
				//recInTemp.setField("YD_CAR_SCH_ID"		,szYD_CAR_SCH_ID);
				//recInTemp.setField("CALL_PGM"			    ,"SANGCHA");
				
				//----------------------------------------------------------------------------------
				//	동기화 문제로 인하여 JMS --> EJB Call로 변경
				//	수정자 : 임춘수
				//	수정일 : 1) 2009.12.30 - 최초등록
				//----------------------------------------------------------------------------------
				//ydDelegate.sendMsg(recInTemp);
	
				//EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
				//szRtnMsg = (String) ejbConn.trx("procCarBayInOrdReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				
				//----------------------------------------------------------------------------------
				//szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 성공 - 메세지 : " + szRtnMsg;
				//ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

				
				// 2021. 4. 7 야드이송야드구분
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
				recInTemp.setField("CAR_NO",           		szCAR_NO);								//차량번호
				recInTemp.setField("YD_FTMV_MEANS_GP", szYD_FTMV_MEANS_GP);
				recInTemp.setField("CR_FRTOMOVE_GP", szCR_FRTOMOVE_GP);
				recInTemp.setField("MODIFIER"    , "DMYDR061"  );	//수정자
				commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateYdStockByFTMV");
				
				return ;
				
			}
			//-----------------------------------------------------------------------------------------------------------------
			// [전사물류시스템개선] 2021.1.6
			// - 차량입고(반품,회송,출고취소)의 경우엔 차량에 적재된 순서대로 처리
			// - 저장위치 및 차량포인트를 지정처리함 야드저장위치 및 포인트를 지정
			//-----------------------------------------------------------------------------------------------------------------
//221115 오류 수정			
			else if(nTRANS_ORD_SEQNO>999000){
						szMsg="["+szOperationName+"] 차량입고(반품)은 차량정지위치를 지정되어 있기때문에(반품등록화면) 사용가능한 차량정지위치를 로직은 Pass한다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				// 일반차량 출하
				/*
				 * 2. 저장품이 적치된 저장위치 정보를 조회 
				 */
				szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//저장품 동 구하기 
				rsGetStock 	= JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP",   "");
				recInTemp.setField("CARD_NO", 		  szCARD_NO);
				recInTemp.setField("TRANS_ORD_DATE" , szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
				recInTemp.setField("YD_GP", 		  szYD_GP);
				
				//-----------------------------------------------------------------------------------------------------------------
				// [전사물류시스템개선]
				//  - 복수동일경우 상차우선순위에 의하여 쿼리를 분리처리한다.
				//
				//-----------------------------------------------------------------------------------------------------------------
				String sDongSelectQuery = "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0089_PIDEV";
				if(PlateGdsYdUtil.isApplyYn("복수상차우선순위적용여부")){					
					JDTORecordSet rsMulti = JDTORecordFactory.getInstance().createRecordSet("");
					JDTORecord recMulti = JDTORecordFactory.getInstance().create();
					recMulti.setField("CARD_NO", 		  szCARD_NO);
					recMulti.setField("TRANS_ORD_DATE" , szTRANS_ORD_DATE);
					recMulti.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
					
					// 조합상차이거나, 운송지시가 C로시작하는것이라면, 거래처가 2곳 이상이라면
					if( commDao.select(recMulti, rsMulti, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.isCMBN_CARLD_YN_PIDEV") > 0){
						// 동 결정순위 설정
						recInTemp.setField("DOUBLEDONG_YD_CAR_SCH_ID",    		szDOUBLEDONG_YD_CAR_SCH_ID);
						sDongSelectQuery = "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getSelectDongByCMBN_CARLD_PIDEV";
					}
				}
				intRtnVal = commDao.select(recInTemp, rsGetStock, sDongSelectQuery);			
    			
				if(intRtnVal <= 0){
					return ;
				}
				
				szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 성공 - 건수["+intRtnVal+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsGetStock.first();
				recStlNo = rsGetStock.getRecord();
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recStlNo,"YD_STK_COL_GP");
				
				// 동우선순위쿼리가 Select되었다면 이전 정차위치를 구한다.
				// 동일한 정차위치일경우 바로 도착처리모듈을 호출하기 위함
				// 만약 차량형상 사용유무가 Y일 경우엔 형상유무와 상관없이 크레인스케쥴을 편성한다.
				if("com.inisteel.cim.yd.common.dao.YdPlateCommDao.getSelectDongByCMBN_CARLD_PIDEV".equals(sDongSelectQuery)){
					szBefore_YD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(recStlNo,"BF_YD_CARLD_STOP_LOC");
				}
				
				szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 대상재가 존재하는 동["+szYD_STK_COL_GP+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//-----------------------------------------------------------------------------------------------------------------
				//	사용가능한 차량정지위치로 적치열 조회 후 개소POINT를 구함
				//-----------------------------------------------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP",szYD_STK_COL_GP);
				intRtnVal = commDao.select(recInTemp, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0133");		
				
				if( intRtnVal <= 0) {
					szMsg="["+szOperationName+"] 사용가능한 개소POINT 조회 시 오류발생 - 메세지 : ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				
				rsResult.first();
				recInTemp		= rsResult.getRecord();
				
				szWLOC_CD		= ydDaoUtils.paraRecChkNull(recInTemp,"WLOC_CD"); //2013.11.14 복수 창고 출하시 개소코드 변경하기 위하여 추가				
				szYD_PNT_CD		= ydDaoUtils.paraRecChkNull(recInTemp,"YD_PNT_CD");
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recInTemp,"YD_CARLD_STOP_LOC");
				
				szMsg="["+szOperationName+"] 사용가능한 차량정지위치["+szYD_STK_COL_GP+"]로 개소POINT 조회 완료 - 메세지 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			}
			//-----------------------------------------------------------------------------------------------------------------
			
			if("Y".equals(szDOUBLEDONG_CHECK)) { //복수동일 경우 
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
					recInTemp.setField("YD_CAR_SCH_ID",    		szDOUBLEDONG_YD_CAR_SCH_ID);
					recInTemp.setField("REGISTER",         		szRcvTcCode);
					recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
					recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
					recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
					recInTemp.setField("SPOS_WLOC_CD",     		szWLOC_CD);								//발지개소코드
					recInTemp.setField("YD_PNT_CD1",     		szYD_PNT_CD);							//야드포인트코드1
					recInTemp.setField("CAR_NO",           		szCAR_NO);								//차량번호
					recInTemp.setField("CARD_NO",          		szCARD_NO);								//카드번호
					recInTemp.setField("YD_CARLD_LEV_DT",  		YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
					recInTemp.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
					recInTemp.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
			    	recInTemp.setField("YD_CARLD_STOP_LOC",		szYD_STK_COL_GP);						//차량상차정지위치
			    	recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
			    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		"2");									//입동지시순번  (2013.06.20 cho 1순위 --> 2순위 변경)
					recInTemp.setField("DEL_YN", 				"N");		
					
					//차량스케줄수정
			    	intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
			    	
		    		if( intRtnVal <= 0 ){
						szMsg="[" + szOperationName + "] 차량스케줄 수정 시 오류발생[반환값 : " + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
		    		}
		    		
		    		//차량 스케쥴재료수정
		    		YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao(); 
		        	intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(recInTemp, 1) ;
		            
		    		szMsg="[" + szOperationName + "] 차량스케줄 수정 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					// 복수동상차우선순위에 의하여 동일한 정지위치에서 작업예약이 2번 발생할경우
					// 차량형상유무 상관없이 바로 도착처리하기 위함
					 
					if(PlateGdsYdUtil.isApplyYn("복수상차우선순위적용여부")){
						
						if(szBefore_YD_CARLD_STOP_LOC.equals(szYD_STK_COL_GP)){
							// YD_CARLD_SCH_REQ_GP 스케쥴요청구분컬럼에 형상PASS 여부를 업데이트한다. 
							// YD_CARLD_SCH_REQ_GP 컬럼은 과거 공대차구분(대차에서 씌임)
							// 운영계확인결과 TB_YD_CARSCH에 사용되고 있지 않음(NULL값임)
							szMsg="[" + szOperationName + "] 전과 동일한 차량정지위치임 이전["+szBefore_YD_CARLD_STOP_LOC+"]현재["+szYD_STK_COL_GP+"]";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							JDTORecord jtoUpdate = JDTORecordFactory.getInstance().create();
							jtoUpdate.setField("YD_CAR_SCH_ID", szDOUBLEDONG_YD_CAR_SCH_ID);
							jtoUpdate.setField("YD_CARLD_SCH_REQ_GP",  "Y");
							jtoUpdate.setField("MODIFIER",        "DMYDR061"); 
							commDao.update(jtoUpdate, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCarschByFrmYnPassYn");
//							recInTemp.setField("QUERY_ID", "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCarschByFrmYnPassYn");
//				    		EJBConnector 	ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);			
//						    ejbConn.trx("updYdStkColTX", new Class[] { JDTORecord.class }, new Object[] { recInTemp }); 
						}
					}
		    		
				}
				// 차량입고(반품) 복수동 하차
				else{
        			JDTORecord params = JDTORecordFactory.getInstance().create();
        			JDTORecordSet rsCarStopLoc = JDTORecordFactory.getInstance().createRecordSet("");
        			params.setField("YD_CAR_SCH_ID",    		szDOUBLEDONG_YD_CAR_SCH_ID);
					if( commDao.select(params, rsCarStopLoc, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCarUnloadStopLoc") > 0){
						
						recInTemp = JDTORecordFactory.getInstance().create();
						szYD_STK_COL_GP = rsCarStopLoc.getRecord(0).getFieldString("YD_CARUD_STOP_LOC");
						recInTemp.setField("YD_PNT_CD3",     				rsCarStopLoc.getRecord(0).getFieldString("YD_PNT_CD"));
						recInTemp.setField("YD_CARUD_STOP_LOC",     		szYD_STK_COL_GP);
						
						recInTemp.setField("YD_CAR_SCH_ID",    		szDOUBLEDONG_YD_CAR_SCH_ID);
						recInTemp.setField("REGISTER",         		szRcvTcCode);
						recInTemp.setField("YD_EQP_WRK_STAT",  		"L");									//야드설비작업상태
						recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
						recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
						recInTemp.setField("YD_CARUD_LEV_DT",  		YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
						recInTemp.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
						recInTemp.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
				    	recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARUD_LEV);				//상차출발상태
				    	recInTemp.setField("YD_BAYIN_WO_SEQ",  		"2");									//입동지시순번  (2013.06.20 cho 1순위 --> 2순위 변경)
				    	recInTemp.setField("DEL_YN", 				"N");
						
						//차량스케줄수정
				    	intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);

				    	// DEL_YN = 'Y' 처리한다.
	        			params = JDTORecordFactory.getInstance().create();
	        			params.setField("YD_CAR_SCH_ID", szDOUBLEDONG_YD_CAR_SCH_ID);
	        			commDao.update(params,  "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updYdCarftmvmtlByCaudStopLoc");
 					}
				}

				
			} else { //복수동이 아닐 경우  

					/*
					 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
					 */
					szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID",    		szYD_CAR_SCH_ID);
					recInTemp.setField("REGISTER",         		szRcvTcCode);
					recInTemp.setField("YD_EQP_WRK_STAT",  		"U");									//야드설비작업상태
					recInTemp.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
					recInTemp.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
					recInTemp.setField("CAR_KIND", 				StringHelper.evl(szCAR_KIND, "TR"));
					recInTemp.setField("SPOS_WLOC_CD",     		szWLOC_CD);								//발지개소코드
					recInTemp.setField("YD_PNT_CD1",     		szYD_PNT_CD);							//야드포인트코드1
					recInTemp.setField("CAR_NO",           		szCAR_NO);								//차량번호
					recInTemp.setField("CARD_NO",          		szCARD_NO);								//카드번호
					recInTemp.setField("YD_CARLD_LEV_DT",  		YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
					recInTemp.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
					recInTemp.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
			    	recInTemp.setField("YD_CARLD_STOP_LOC",		szYD_STK_COL_GP);						//차량상차정지위치
		    		recInTemp.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 --기본값으로 설정(9) **
					recInTemp.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARLD_LEV);				//상차출발상태
					recInTemp.setField("IF_SEQ_NO", 			szUNIQUE_ID);							//운송지시 SEQ
					
					recInTemp.setField("YD_CAR_WRK_GP",    		szWORK_GP);
					recInTemp.setField("TEL_NO", 		   		szTEL_NO);								//기사핸드폰번호
					recInTemp.setField("CMBN_CARLD_YN",    		szCMBN_CARLD_YN);						//첫번째 도착창고 : S 두번째 도착창고 : E
					recInTemp.setField("WAIT_ARR_DDTT",    		szWAIT_ARR_DDTT);						//대기장도착시간
					recInTemp.setField("WAIT_ARR_GP",      		szWAIT_ARR_GP);							//대기장도착구분  - B:BACKUP , S:SMARTPHONE		
					
					
					// 전사물류개선 2021. 1. 6
					// 길이 폭으로 차종을 구한다.
					// --가변슬라이드 : 폭이 3400 초과 && 길이 14,000 초과
					// --가변차량 : 폭이 3400 초과 && 길이 14,000 이하
					// --일반 : 폭이 3400 이하 && 길이 14,000 이하
					// --일반슬라이드 : 폭이 3400 이하 && 길이 14,000 초과
					JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
					JDTORecordSet rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
					String sCarKind = "";
					if(szCARD_NO.startsWith("P")){
						sCarKind = "PT";
					}
					else if("P12".equals(szCAR_KIND) 
							|| "P18".equals(szCAR_KIND) 
							|| "PX".equals(szCAR_KIND) 
							|| "PY".equals(szCAR_KIND) 
							|| "PU".equals(szCAR_KIND) ){
						sCarKind = "PT";
					}
					else{
						recInTemp1.setField("TRANS_ORD_DATE"           	,szTRANS_ORD_DATE);
						recInTemp1.setField("TRANS_ORD_SEQNO"         	,szTRANS_ORD_SEQNO);
						recInTemp1.setField("CAR_NO"           			,szCAR_NO );
						if(commDao.select(recInTemp1, rsResult2, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCarKindByWdithLength")>0){
							sCarKind =  rsResult2.getRecord(0).getFieldString("CAR_KIND");
						}
						else{
							sCarKind = "TR";
						}
					}
					recInTemp.setField("TRANS_EQUIPMENT_TYPE", 	sCarKind);	// 세부차종
					
		    		//차량스케줄 등록
			    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
		    		if( intRtnVal <= 0 ){
						szMsg="[" + szOperationName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
		    		}
		    		
					// YD_STOCK 차량TYPE정보 Set				
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CONVEYOR_BRANCH_CD", sCarKind); 
					recInTemp.setField("MODIFIER"    , "DMYDR061"  );	//수정자
					recInTemp.setField("YD_CAR_SCH_ID",    		szYD_CAR_SCH_ID); 
					commDao.update(recInTemp, "com.inisteel.cim.yd.dao.ydstockdao.updYdCarTransType");
					
					// 2021. 4. 7
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
					recInTemp.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
					recInTemp.setField("CAR_NO",           		szCAR_NO);								//차량번호
					recInTemp.setField("YD_FTMV_MEANS_GP", szYD_FTMV_MEANS_GP);
					recInTemp.setField("CR_FRTOMOVE_GP", szCR_FRTOMOVE_GP);
					recInTemp.setField("MODIFIER"    , "DMYDR061"  );	//수정자
					commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateYdStockByFTMV");
					
		    		szMsg="[" + szOperationName + "] 차량스케줄 생성 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}

			/*
			 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
			 */
		
			szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("TC_CODE"				,"YDYDJ633");
			recInTemp.setField("TC_CREATE_DDTT"			,YdUtils.getCurDate("yyyyMMddHHmmss"));
			recInTemp.setField("YD_CAR_STOP_LOC"		,szYD_STK_COL_GP);
			recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
			recInTemp.setField("CALL_PGM"			    ,"SANGCHA");
			
			//----------------------------------------------------------------------------------
			//	동기화 문제로 인하여 JMS --> EJB Call로 변경
			//	수정자 : 임춘수
			//	수정일 : 1) 2009.12.30 - 최초등록
			//----------------------------------------------------------------------------------
			//ydDelegate.sendMsg(recInTemp);
			
			EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
			szRtnMsg = (String) ejbConn.trx("procCarBayInOrdReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
			
			//----------------------------------------------------------------------------------
			szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 EJB 호출 성공 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
		}catch(Exception e){
		
			szMsg="["+szOperationName+"] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
			
		} // end of try-catch
		
		szMsg="["+szOperationName+"] 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
		
	} // end of procStandByYdArrivePlate4G()
	///////////////////////////////////////////////////////////////////////////////
	///                          전사물류개선 프로젝트 2021.1.6                  ///
	///////////////////////////////////////////////////////////////////////////////
}