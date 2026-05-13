/**
 * 
 */
package com.inisteel.cim.yd.common.util.plate;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import xlib.cmc.GridData;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.sms.SmsSender;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ptOsCommDao.PtOsCommDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSpecDao.YdCrnSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydStrCharDao.YdStrCharDao;
import com.inisteel.cim.yd.common.dao.ydStrCharGrpDao.YdStrCharGrpDao;
import com.inisteel.cim.yd.common.dao.ydPilingGrpDao.YdPilingGrpDao;
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.yd.common.dao.ptPlateCommDao.PtPlateCommDao;
import com.inisteel.cim.cm.message.MessageSenderAuto;

import com.inisteel.cim.yd.message.MessageSenderTalk;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 * @author 임춘수
 *
 */
public class PlateGdsYdUtil {
	
	private static YdDaoUtils	ydDaoUtils			= new YdDaoUtils();
	private static YdUtils        ydUtils        	= new YdUtils();
	private static String	szClassName				= PlateGdsYdUtil.class.getName();
	private static YDDataUtil  yddatautil          = new YDDataUtil();
    
    /**
     * 오퍼레이션명 : 후판제품 크레인 작업지시에 내려질 Y1, Y2 좌표 계산
     *  
     * @param  ● szSTL_No            : 제품번호				- 크레인스케줄작업재료들중에서 제일 긴 재료번호
     *           szYD_STK_COL_GP     : 야드적치열구분		- 베드정보 조회 시 사용
     * 			 szYD_STK_BED_NO     : 야드적치Bed번호		- 베드정보 조회 시 사용
     * 			 szYD_EQP_ID         : 야드설비ID			- 빔길이 조회 시 사용
     * 			 szYD_CRN_GRAB_GP    : 크레인 Grab 구분		- D : 1Grab Crane(2호기), E : 2Grab Crane(1호기)
     *           
     *           결과값 
     *           JDTORecord resResult.Crane_Grab_Use_Gp : Grab 사용 구분 
     *           JDTORecord resResult.Grab_Y1_Addr      : Grab Y1 Address 코드
     *           JDTORecord resResult.Grab_Y1_Value     : Grab Y1 좌표값
     *           JDTORecord resResult.Grab_Y2_Addr      : Grab Y2 Address 코드
     *           JDTORecord resResult.Grab_Y2_Value     : Grab Y2 좌표값
     * @return ● False, True
     * @throws ● JDTOException
     */
//	public static boolean PlateCraneXYCal1(String szSTL_No, 
//            String szYD_STK_COL_GP, 
//            String szYD_STK_BED_NO, 
//            //String szYD_CRN_SCH_ID,
//            String szYD_EQP_ID,
//            JDTORecord recResult){
//		// DAO 객체 생성
//    	YdCrnSpecDao   ydCrnSpecDao  	= new YdCrnSpecDao();
//    	YdStkBedDao    ydStkBedDao    	= new YdStkBedDao();
//    	//YdCrnSchDao    ydCrnSchDao     	= new YdCrnSchDao();
//    	YdStockDao     ydStockDao     	= new YdStockDao();
//    	
//    	YdDaoUtils     ydDaoUtils     	= new YdDaoUtils();
//
//    	// Method 선언
//    	String szMethodName          	= "PlateCraneXYCal"; 
//    	String szOperationName			= "좌표계산(후판제품)";
//
//    	// 레코드 선언
//    	//JDTORecordSet getYdCrnspec   	= JDTORecordFactory.getInstance().createRecordSet("");
//    	JDTORecord    recPara        	= null;
//    	JDTORecordSet outRecSet      	= null;
//    	JDTORecord    recCrnSpec     	= null;
//    	JDTORecord    recStrBed       	= null;
//    	//JDTORecord    recCrnSch        	= null;
//    	
//    	String szYD_CRN_GRAB_GP			= "";
//    	String szYD_STK_BED_L_GP		= null;
//    	double dblYD_STK_BED_W_MAX		= 0;
//    	// 변수 선언
//    	int intRtnVal                 	= 0;
//		double Tmp_X 						= 0;
//		int Tmp_Y 						= 0;
//		//int Tmp_Y1 						= 0;
//		//int Tmp_Y2 						= 0;
//		int Tmp_YD_MTL_L         		= 0;
//		//int Tmp_YD_EQP_WRK_MAX_L 		= 0;
//		int intYD_STK_BED_YAXIS 		= 0;
//		int intYD_CRN_TONG_L    		= 0;
//		
//		//---------------------------------------------------------------------------------------------------------
//		//	1Grab Type 변수 정의
//		//---------------------------------------------------------------------------------------------------------
//		
//		int Tmp_D1 						= 0;
//		int Tmp_D2 						= 0;
//		int Tmp_D3 						= 0;
//		int Tmp_D4 						= 0;
//		int Tmp_D5 						= 0;
//		int Tmp_D6 						= 0;
//		int Tmp_D7 						= 0;
//		int Tmp_D8 						= 0;
//		int Tmp_D9 						= 0;
//		int Tmp_DA 						= 0;
//		int Tmp_DX 						= 0;
//		
//		//---------------------------------------------------------------------------------------------------------
//		
//		//---------------------------------------------------------------------------------------------------------
//		//	2Grab Type 변수 정의
//		//---------------------------------------------------------------------------------------------------------
//		
//		int Tmp_E1 						= 0;
//		int Tmp_E2 						= 0;
//		int Tmp_E3 						= 0;
//		int Tmp_E4 						= 0;
//		int Tmp_E5 						= 0;
//		int Tmp_E6 						= 0;
//		int Tmp_E7 						= 0;
//		int Tmp_E8 						= 0;
//		int Tmp_E9 						= 0;
//		int Tmp_EA 						= 0;
//		int Tmp_EX 						= 0;
//		
//		//---------------------------------------------------------------------------------------------------------
//		
//    	String szMsg                 	= "";
//    	double dblYD_MTL_W				= 0;
//    	String szYD_MTL_L               = ""; 
//    	String szYD_CRN_TONG_L     	  	= "";
//    	//String szYD_EQP_WRK_MAX_W  	    = "";
//    	//String szYD_EQP_WRK_MAX_L  		= "";
//    	int intYD_STK_BED_XAXIS 		= 0;
//    	String szYD_STK_BED_YAXIS 		= "";
//		String szCrane_Grab_Use_Gp 		= "";
//		String szGrab_X_Value     		= "";
//		String szGrab_Y_Value     		= "";
//		String szGrab_Y1_Addr      		= "";
//		String szGrab_Y1_Value     		= "";
//		String szGrab_Y2_Addr      		= "";
//		String szGrab_Y2_Value     		= "";    	
//		
//		
//    	try{ 
//    		szMsg ="["+szOperationName+"] ----------------------------- 메소드 시작 - 파라미터 확인 -----------------------------" ;
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			
//			szMsg ="["+szOperationName+"] 크레인작업재료의 길이가 제일 긴 재료번호 - " + szSTL_No;
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			
//			szMsg ="["+szOperationName+"] 권상/권하지시위치 : 적치열구분 - " + szYD_STK_COL_GP + ", 베드번호 - " + szYD_STK_BED_NO;			//권상/권하 지시 위치
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			
//			szMsg ="["+szOperationName+"] 크레인설비ID - " + szYD_EQP_ID;
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			
//			
//			if( szYD_EQP_ID.substring(5).equals("1") ) {					//1호기
//				szYD_CRN_GRAB_GP = "E";											//2 Grab
//			}else{															//2호기
//				szYD_CRN_GRAB_GP = "D";											//1 Grab
//			}
//			
//			szMsg ="["+szOperationName+"] 크레인 Grab 구분[D : 1Grab Crane(2호기), E : 2Grab Crane(1호기)] - " + szYD_CRN_GRAB_GP;
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//    		//---------------------------------------------------------------------------------------------------------
//    		// 저장품 조회
//    		// 제품길이 (YD_MTL_L) 추출
//    		//---------------------------------------------------------------------------------------------------------
//			szMsg ="["+szOperationName+"] 재료번호[" + szSTL_No + "]로 저장품 조회 시작";
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			
//    		recPara 		= JDTORecordFactory.getInstance().create();
//    		outRecSet 		= JDTORecordFactory.getInstance().createRecordSet("");
//    		
//    		recPara.setField("STL_NO", 			szSTL_No);
//    		intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 0);
//    		
//			if(intRtnVal < 0){
//				szMsg ="["+szOperationName+" - 오류발생]: 저장품조회  중 오류 [" + intRtnVal + "]" ;
//				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
//				return false;
//			} else if(intRtnVal == 0){
//				szMsg ="["+szOperationName+" - 오류발생]: 저장품조회  중 건수가 없음 [" + intRtnVal + "]" ;
//				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
//				return false;
//			}
//			
//			outRecSet.first();
//    		recCrnSpec = outRecSet.getRecord();
//    		
//    		dblYD_MTL_W 		= ydDaoUtils.paraRecChkNullDouble(recCrnSpec, "YD_MTL_W"); 			// 제품폭
//    		szYD_MTL_L 			= ydDaoUtils.paraRecChkNull(recCrnSpec, "YD_MTL_L"); 				// 제품길이
//    		szYD_STK_BED_L_GP 	= ydDaoUtils.paraRecChkNull(recCrnSpec, "YD_MTL_L_GP"); 			// 베드의 길이구분은 제품의 길이구분으로 대체
//    		
//    		szMsg ="["+szOperationName+"] 재료번호[" + szSTL_No + "] - 제품폭["+dblYD_MTL_W+"], 제품길이["+szYD_MTL_L+"], 제품의길이구분["+szYD_STK_BED_L_GP+"]";
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			
//    		if( !szYD_STK_BED_L_GP.equals("") ) {
//    			szYD_STK_BED_L_GP = szYD_STK_BED_L_GP.substring(0, 1);
//    			szMsg ="["+szOperationName+"] 재료번호[" + szSTL_No + "]의 제품의길이구분["+szYD_STK_BED_L_GP+"]존재하므로 베드의 길이구분자로 대체 사용 가능";
//    			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//    		}
//    		//---------------------------------------------------------------------------------------------------------
//    		
//    		//---------------------------------------------------------------------------------------------------------
//    		// 크레인사양 조회
//    		// 설비ID로  크레인 Beam 길이(YD_CRN_TONG_L) 추출
//    		//---------------------------------------------------------------------------------------------------------
//    		szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]로 크레인사양 조회 시작";
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			
//    		recPara = JDTORecordFactory.getInstance().create();
//    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
//    		
//    		recPara.setField("YD_EQP_ID", szYD_EQP_ID);
//    		intRtnVal = ydCrnSpecDao.getYdCrnspec(recPara, outRecSet, 0);
//			if(intRtnVal < 0){
//				szMsg ="["+szOperationName+" - 오류발생]: 크레인사양조회  중 오류 [" + intRtnVal + "]" ;
//				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
//				return false;
//			} else if(intRtnVal == 0){
//				szMsg ="["+szOperationName+" - 오류발생]: 크레인사양조회  중 건수가 없음 [" + intRtnVal + "]" ;
//				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
//				return false;
//			}
//			
//			outRecSet.first();
//    		recCrnSpec = outRecSet.getRecord();
//    		
//    		szYD_CRN_TONG_L = ydDaoUtils.paraRecChkNull(recCrnSpec, "YD_CRN_TONG_L"); // 크레인 Beam 길이
//    		
//    		szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]의 크레인Beam길이[YD_CRN_TONG_L:"+szYD_CRN_TONG_L+"]";
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//    		//---------------------------------------------------------------------------------------------------------
//
//    		//---------------------------------------------------------------------------------------------------------
//    		// 적치Bed 조회
//    		// 야드적치열구분, 야드적치Bed번호로  야드적치BedY축(YD_STK_BED_YAXIS) 추출
//    		//---------------------------------------------------------------------------------------------------------
//			szMsg ="["+szOperationName+"] 적치열구분[" + szYD_STK_COL_GP + "], 베드번호[" + szYD_STK_BED_NO + "]로 베드 조회 시작";
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			
//    		recPara = JDTORecordFactory.getInstance().create();
//    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
//    		
//    		recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//    		recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
//    		intRtnVal = ydStkBedDao.getYdStkbed(recPara, outRecSet, 0);
//			if(intRtnVal < 0){
//				szMsg ="["+szOperationName+" - 오류발생]: 적치Bed 조회  중 오류 [" + intRtnVal + "]" ;
//				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
//				return false;
//			} else if(intRtnVal == 0){
//				szMsg ="["+szOperationName+" - 오류발생]: 적치Bed 조회  중 건수가 없음 [" + intRtnVal + "]" ;
//				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
//				return false;
//			}
//			
//			outRecSet.first();
//			recStrBed = outRecSet.getRecord();
//    		
//			intYD_STK_BED_XAXIS 		= ydDaoUtils.paraRecChkNullInt(recStrBed, "YD_STK_BED_XAXIS"); 		// 야드적치BedY축
//    		szYD_STK_BED_YAXIS 			= ydDaoUtils.paraRecChkNull(recStrBed, "YD_STK_BED_YAXIS"); 		// 야드적치BedY축
//    		//if( szYD_STK_BED_L_GP.equals("") ) {
//    			szYD_STK_BED_L_GP 		= ydDaoUtils.paraRecChkNull(recStrBed, "YD_STK_BED_L_GP"); 			//야드적치Bed길이구분
//    		//}
//    		dblYD_STK_BED_W_MAX			= ydDaoUtils.paraRecChkNullInt(recStrBed, "YD_STK_BED_W_MAX"); 		// 야드적치Bed폭Max
//    		
//    		szMsg ="["+szOperationName+"] 적치열구분[" + szYD_STK_COL_GP + "], 베드번호[" + szYD_STK_BED_NO + "]의  야드적치BED X축["+intYD_STK_BED_XAXIS+"], 야드적치BED Y축["+szYD_STK_BED_YAXIS+"], 야드적치Bed길이구분["+szYD_STK_BED_L_GP+"] 조회 완료";
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//    		//---------------------------------------------------------------------------------------------------------
//    		
//    		
//    		//---------------------------------------------------------------------------------------------------------
//    		// 계산
//    		//---------------------------------------------------------------------------------------------------------
//    		// 크레인 Grab 구분      szYD_CRN_GRAB_GP
//    		// 야드적치Bed번호        szYD_STK_BED_NO
//    		// 야드적치Bed길이구분 szYD_STK_BED_L_GP
//    		// 제품길이                     szYD_MTL_L
//    		// 야드설비작업최대길이 szYD_EQP_WRK_MAX_L
//    		// Bed Y-Address    szYD_STK_BED_YAXIS
//    		// 크레인 Beam 길이       szYD_CRN_TONG_L
//    		//---------------------------------------------------------------------------------------------------------
//    		if( szYD_MTL_L.trim().equals("") ){
//    			szYD_MTL_L = "0";
//    		}
//   		    Tmp_YD_MTL_L         		= Integer.parseInt(szYD_MTL_L);             // 크레인스케줄작업재료들중에서 제일 긴 재료의 제품길이
//
//    		//if(szYD_EQP_WRK_MAX_L == null || szYD_EQP_WRK_MAX_L.trim().equals("")){
//    		//	szYD_EQP_WRK_MAX_L = "0";
//    		//}   		    
//   		    //Tmp_YD_EQP_WRK_MAX_L 		= Integer.parseInt(szYD_EQP_WRK_MAX_L);     // 야드설비작업최대길이
//   		    
//    		if( szYD_STK_BED_YAXIS.equals("") ){
//    			szYD_STK_BED_YAXIS = "0";
//    		}   		       		    
//   		    intYD_STK_BED_YAXIS 		= Integer.parseInt(szYD_STK_BED_YAXIS);     // Bed Y-Address
//   		    
//    		if( szYD_CRN_TONG_L.equals("") ){
//    			szYD_CRN_TONG_L = "0";
//    		}
//    		intYD_CRN_TONG_L    		= Integer.parseInt(szYD_CRN_TONG_L);        // 크레인 Beam 길이
//    		//---------------------------------------------------------------------------------------------------------
// 
//    		
//    		//----------------------------------------------------------------------------------------------
//			//	1 Grab Type Crane
//			//----------------------------------------------------------------------------------------------
//    		
//    		if ( szYD_CRN_GRAB_GP.equals("D") ) {
//    			
//    			int intYD_CRN_BEAM_L				= 14000;
//    			int intYD_CRN_BEAM_L_MAX			= 16100;
//    			int intYD_STND_OFFSET				= (Tmp_YD_MTL_L - intYD_CRN_BEAM_L_MAX) / 2;
//    			
//    			if( szYD_STK_COL_GP.equals("PT") ) {						//차량위치이면
//    				szGrab_Y1_Value			= String.valueOf(intYD_STK_BED_YAXIS);
//    			}else if( (szYD_STK_COL_GP.equals("KATF0206") 
//    					|| szYD_STK_COL_GP.equals("KBTF0206")
//    					|| szYD_STK_COL_GP.equals("KARTRA10")
//    					|| szYD_STK_COL_GP.equals("KARTRB10")
//    					|| szYD_STK_COL_GP.equals("KBRTRB30")
//    					|| szYD_STK_COL_GP.equals("KCRTRA50")
//    					|| szYD_STK_COL_GP.equals("KCRTRB50")
//    					|| szYD_STK_COL_GP.equals("KDRTRA70")
//    					|| szYD_STK_COL_GP.equals("KDRTRB70")
//    					|| szYD_STK_COL_GP.equals("KAGJ0106") )
//    				&& Tmp_YD_MTL_L < 14001
//    			) {
//    				
//    			}else if( Tmp_YD_MTL_L < 14001 ) {
//    				szGrab_Y1_Value			= String.valueOf( intYD_STK_BED_YAXIS + (intYD_CRN_BEAM_L / 2) );
//    			}else if( Tmp_YD_MTL_L < 16101 ) {
//    				szGrab_Y1_Value			= String.valueOf( intYD_STK_BED_YAXIS + (intYD_CRN_BEAM_L_MAX / 2) );
//    			}else if( Tmp_YD_MTL_L < 18001 ) {
//    				szGrab_Y1_Value			= String.valueOf( intYD_STK_BED_YAXIS + (intYD_CRN_BEAM_L_MAX / 2) + intYD_STND_OFFSET ) ;
//    			}else{
//    				szGrab_Y1_Value			= String.valueOf( intYD_STK_BED_YAXIS + (Tmp_YD_MTL_L / 2) );
//    			}
//    				
//    			
//    			//---------------------------------------------------------------------------------------------------------
//	    		//	Y좌표값 계산식
//	    		//	수정자 : 임춘수
//	    		//	수정일 : 2009.12.18
//	    		//---------------------------------------------------------------------------------------------------------
//    			
//    			/*
//	    		 * D1 계산 로직
//	    		 * Bed Y_기준점+ 6,490
//	    		 */
//    			Tmp_D1	= Tmp_YD_STK_BED_YAXIS + 6490;
//    			
//    			/*
//	    		 * D2 계산 로직
//	    		 * Bed Y_기준점+ 8,790
//	    		 */
//    			Tmp_D2	= Tmp_YD_STK_BED_YAXIS + 8790;
//    			
//    			/*
//	    		 * D3 계산 로직
//	    		 * 10,600
//	    		 */
//    			Tmp_D3	= 10600;
//    			
//    			/*
//	    		 * D4 계산 로직
//	    		 * Bed Y_기준점 + (제품길이 / 2)
//	    		 */
//    			Tmp_D4	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_MTL_L / 2);
//    			
//    			/*
//	    		 * D5 계산 로직
//	    		 * 24,300
//	    		 */
//    			Tmp_D5	= 24300;
//    			
//    			/*
//	    		 * D6 계산 로직
//	    		 * Bed Y_기준점 + (제품길이 / 2)
//	    		 */
//    			Tmp_D6	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_MTL_L / 2);
//    			
//    			/*
//	    		 * D7 계산 로직
//	    		 * Bed Y_기준점 + (제품길이 / 2)
//	    		 */
//    			Tmp_D7	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_MTL_L / 2);
//    			
//    			/*
//	    		 * D8 계산 로직
//	    		 * Bed Y_기준점 - (제품길이 - 14,000) + (제품길이 / 2)
//	    		 */
//    			Tmp_D8	= Tmp_YD_STK_BED_YAXIS - (Tmp_YD_MTL_L - 14000) + (Tmp_YD_MTL_L / 2);
//    			
//    			//---------------------------------------------------------------------------------------------------------
//    			
//    			szCrane_Grab_Use_Gp = "1";
//    			
//    			if( szYD_STK_COL_GP.substring(2, 4).equals("RT") ) {
//    				
//    				//----------------------------------------------------------------------------------------------
//    				//	Roller Table 상
//    				//----------------------------------------------------------------------------------------------
//    				
//    				if( szYD_STK_BED_NO.equals("10") 
//    						|| szYD_STK_BED_NO.equals("30") 
//    						|| szYD_STK_BED_NO.equals("50") 
//    						|| szYD_STK_BED_NO.equals("70") )	{
//    					
//    					szGrab_Y1_Addr      = "D1";
//		    			szGrab_Y1_Value     = "" + Tmp_D1;
//		    			szGrab_Y2_Addr      = "DX";
//		    			szGrab_Y2_Value     = "" + Tmp_DX;
//    					
//    				}else if( szYD_STK_BED_NO.equals("20") 
//    						|| szYD_STK_BED_NO.equals("40") 
//    						|| szYD_STK_BED_NO.equals("60") 
//    						|| szYD_STK_BED_NO.equals("80") )	{
//    					
//    					szGrab_Y1_Addr      = "D2";
//		    			szGrab_Y1_Value     = "" + Tmp_D2;
//		    			szGrab_Y2_Addr      = "DX";
//		    			szGrab_Y2_Value     = "" + Tmp_DX;
//		    			
//    				}
//    				
//    				//----------------------------------------------------------------------------------------------
//    				
//    			}else if( szYD_STK_COL_GP.substring(2, 4).equals("TF") ) {
//    				
//    				//----------------------------------------------------------------------------------------------
//    				//	Transfer 상
//    				//----------------------------------------------------------------------------------------------
//    				
//    				if( szYD_STK_BED_NO.equals("06"))	{
//    					
//		    			szGrab_Y1_Addr      = "D1";
//		    			szGrab_Y1_Value     = "" + Tmp_D1;
//		    			szGrab_Y2_Addr      = "DX";
//		    			szGrab_Y2_Value     = "" + Tmp_DX;
//		    			
//    				}else if( szYD_STK_BED_NO.equals("16"))	{
//    					
//    					szGrab_Y1_Addr      = "D2";
//		    			szGrab_Y1_Value     = "" + Tmp_D2;
//		    			szGrab_Y2_Addr      = "DX";
//		    			szGrab_Y2_Value     = "" + Tmp_DX;
//		    			
//    				}
//    				
//    				//----------------------------------------------------------------------------------------------
//    			}else{
//    				
//    				//----------------------------------------------------------------------------------------------
//    				//	일반 Bed 상
//    				//----------------------------------------------------------------------------------------------
//    				
//    				if( szYD_STK_BED_L_GP.equals("S") ) {
//	    				
//	    				if( szYD_STK_BED_NO.equals("01") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 1Grab Type이고, 1번지, 단척이면 Grab 1 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szGrab_Y1_Addr      = "D3";
//	    	    			szGrab_Y1_Value     = "" + Tmp_D3;
//	    	    			szGrab_Y2_Addr      = "DX";
//	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					
//	    				}else if( szYD_STK_BED_NO.equals("02") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 1Grab Type이고, 2번지, 단척이면 Grab 1 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szGrab_Y1_Addr      = "D4";
//	    	    			szGrab_Y1_Value     = "" + Tmp_D4;
//	    	    			szGrab_Y2_Addr      = "DX";
//	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    	    			
//	    				}else if( szYD_STK_BED_NO.equals("03") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 1Grab Type이고, 3번지, 단척이면 Grab 1 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szGrab_Y1_Addr      = "D5";
//	    	    			szGrab_Y1_Value     = "" + Tmp_D5;
//	    	    			szGrab_Y2_Addr      = "DX";
//	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    	    			
//	    				}
//	    				
//	    				
//	    			}else if( szYD_STK_BED_L_GP.equals("M") ) {
//	    				
//	    				if( szYD_STK_BED_NO.equals("01") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 1Grab Type이고, 1번지, 중척이면 Grab 1 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szGrab_Y1_Addr      = "D6";
//	    	    			szGrab_Y1_Value     = "" + Tmp_D6;
//	    	    			szGrab_Y2_Addr      = "DX";
//	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					
//	    				}else if( szYD_STK_BED_NO.equals("02") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 1Grab Type이고, 2번지, 중척이면 Grab 1 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szGrab_Y1_Addr      = "D7";
//	    	    			szGrab_Y1_Value     = "" + Tmp_D7;
//	    	    			szGrab_Y2_Addr      = "DX";
//	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    	    			
//	    				}else if( szYD_STK_BED_NO.equals("03") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 1Grab Type이고, 3번지, 중척이면 Grab 1 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szGrab_Y1_Addr      = "D8";
//	    	    			szGrab_Y1_Value     = "" + Tmp_D8;
//	    	    			szGrab_Y2_Addr      = "DX";
//	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    	    			
//	    				}
//	    			}else if( szYD_STK_BED_L_GP.equals("L") ) {
//	    				
//	    				if( szYD_STK_BED_NO.equals("01") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 1Grab Type이고, 1번지, 장척이면 Grab 1 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szGrab_Y1_Addr      = "D6";
//	    	    			szGrab_Y1_Value     = "" + Tmp_D6;
//	    	    			szGrab_Y2_Addr      = "DX";
//	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					
//	    				}else if( szYD_STK_BED_NO.equals("02") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 1Grab Type이고, 2번지, 장척이면 Grab 1 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szGrab_Y1_Addr      = "D7";
//	    	    			szGrab_Y1_Value     = "" + Tmp_D7;
//	    	    			szGrab_Y2_Addr      = "DX";
//	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    	    			
//	    				}else if( szYD_STK_BED_NO.equals("03") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 1Grab Type이고, 3번지, 장척이면 Grab 1 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szGrab_Y1_Addr      = "D8";
//	    	    			szGrab_Y1_Value     = "" + Tmp_D8;
//	    	    			szGrab_Y2_Addr      = "DX";
//	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    	    			
//	    				}
//	    			}else if( szYD_STK_BED_L_GP.equals("X") ) {
//	    				
//	    				if( szYD_STK_BED_NO.equals("01") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 1Grab Type이고, 1번지, 초장척이면 Grab 1 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szGrab_Y1_Addr      = "D6";
//	    	    			szGrab_Y1_Value     = "" + Tmp_D6;
//	    	    			szGrab_Y2_Addr      = "DX";
//	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					
//	    				}else if( szYD_STK_BED_NO.equals("02") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 1Grab Type이고, 2번지, 초장척이면 Grab 1 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szGrab_Y1_Addr      = "D7";
//	    	    			szGrab_Y1_Value     = "" + Tmp_D7;
//	    	    			szGrab_Y2_Addr      = "DX";
//	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    	    			
//	    				}else if( szYD_STK_BED_NO.equals("03") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 1Grab Type이고, 3번지, 초장척이면 Grab 1 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szGrab_Y1_Addr      = "D8";
//	    	    			szGrab_Y1_Value     = "" + Tmp_D8;
//	    	    			szGrab_Y2_Addr      = "DX";
//	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    	    			
//	    				}
//	    			}
//    				
//    				//----------------------------------------------------------------------------------------------
//    				
//    			}
//    			
//    			//---------------------------------------------------------------------------------------------------------
//    		}
//    		//----------------------------------------------------------------------------------------------
//			//	2 Grab Type Crane
//			//----------------------------------------------------------------------------------------------
//    		else if ( szYD_CRN_GRAB_GP.equals("E") ) {
//    			
//	    		
//	    		//---------------------------------------------------------------------------------------------------------
//	    		//	Y좌표값 계산식 수정 반영
//	    		//	수정자 : 임춘수
//	    		//	수정일 : 2009.12.17
//	    		//---------------------------------------------------------------------------------------------------------
//	    		/*
//	    		 * E1  계산 로직
//	    		 *	if   제품길이 < 14,001 
//				 *	      Bed Y_기준점+ 7,000
//				 *	else
//				 *	      Bed Y_기준점+ (빔길이 / 2)
//	    		 */
//	
//	    		if( Tmp_YD_MTL_L < 14001 ) {
//	    			Tmp_E1	= Tmp_YD_STK_BED_YAXIS + 7000;
//	    		}else{
//	    			Tmp_E1	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_CRN_TONG_L / 2);
//	    		}
//	    		
//	    		/*
//	    		 * E2 계산 로직
//	    		 * Bed Y_기준점+ 2000 + (Beam길이 / 2)
//	    		 */
//	    		Tmp_E2	= Tmp_YD_STK_BED_YAXIS + 2000 + (Tmp_YD_CRN_TONG_L / 2);
//	    		
//	    		/*
//	    		 * E3 계산 로직
//	    		 * Bed Y_기준점+ 4500 + (Beam길이 / 2)
//	    		 */
//	    		Tmp_E3	= Tmp_YD_STK_BED_YAXIS + 4500 + (Tmp_YD_CRN_TONG_L / 2);
//	    		
//	    		/*
//	    		 * E4 계산 로직
//	    		 * Bed Y_기준점+ (Beam길이 / 2)
//	    		 */
//	    		Tmp_E4	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_CRN_TONG_L / 2);
//	    		
//	    		/*
//	    		 * E5 계산 로직
//	    		 * Bed Y_기준점+ (제품길이 / 2)
//	    		 */
//	    		Tmp_E5	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_MTL_L / 2);
//	    		
//	    		/*
//	    		 * E6 계산 로직
//	    		 * Bed Y_기준점+ (빔길이+2000) + (빔길이/2)
//	    		 */
//	    		Tmp_E6	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_CRN_TONG_L + 2000) + (Tmp_YD_CRN_TONG_L / 2);
//	    		
//	    		/*
//	    		 * E7 계산 로직
//	    		 * if   제품길이 〉23501
//	    		 * 		(Bed Y_기준점+ 900) + (빔길이 / 2)
//	    		 * else
//	    		 * 		Bed Y_기준점+ (빔길이 / 2)
//	    		 */
//	    		if( Tmp_YD_MTL_L > 23501 ) {
//	    			Tmp_E7	= Tmp_YD_STK_BED_YAXIS + 900 + (Tmp_YD_CRN_TONG_L / 2);
//	    		}else{
//	    			Tmp_E7	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_CRN_TONG_L / 2);
//	    		}
//	    		
//	    		/*
//	    		 * E8 계산 로직
//	    		 * If 제품길이 〉23501
//	    		 * 		(Bed Y_기준점+ 900) +  빔길이 + (제품길이-22000-900) + (빔길이/2)
//	    		 * else
//	    		 * 		Bed Y_기준점+ (빔길이+2000) + (빔길이/2)
//	    		 */
//	    		if( Tmp_YD_MTL_L > 23501 ) {
//	    			Tmp_E8	= Tmp_YD_STK_BED_YAXIS + 900 + Tmp_YD_CRN_TONG_L + Tmp_YD_MTL_L - 22000 - 900 + (Tmp_YD_CRN_TONG_L / 2);;
//	    		}else{
//	    			Tmp_E8	= Tmp_YD_STK_BED_YAXIS + Tmp_YD_CRN_TONG_L + 2000 + (Tmp_YD_CRN_TONG_L / 2);
//	    		}
//	    		
//	    		//---------------------------------------------------------------------------------------------------------
//	    		
//	    		
//	    		//E9	= 권상 또는 권하 1Grab Type 적용
//	    		Tmp_E9	= 0;
//	    		//EA	= 권상 또는 권하 2Grab Type 적용
//	    		Tmp_EA	= 0;
//	    		//EX	= Skip
//	    		Tmp_EX	= 0;
//	    		
//	    		
//	    		//---------------------------------------------------------------------------------------------------------
//	    		// 조건문에 따라 결과값을 Return
//	    		//---------------------------------------------------------------------------------------------------------
//	    		// 조건 검색 항목
//	    		// 크레인 Grab 구분      szYD_CRN_GRAB_GP
//	    		// 야드적치Bed번호        szYD_STK_BED_NO
//	    		// 야드적치Bed길이구분 szYD_STK_BED_L_GP
//	    		//
//	    		// 결과값
//	    		// JDTORecord resResult.Crane_Grab_Use_Gp : Grab 사용 구분
//	    	    // JDTORecord resResult.Grab_Y1_Addr      : Grab Y1 Address 코드
//	    	    // JDTORecord resResult.Grab_Y1_Value     : Grab Y1 좌표값
//	    	    // JDTORecord resResult.Grab_Y2_Addr      : Grab Y2 Address 코드
//	    	    // JDTORecord resResult.Grab_Y2_Value     : Grab Y2 좌표값
//	    		//---------------------------------------------------------------------------------------------------------
//	    		
//	    		//Roller Table이거나 Transfer인 경우에는 베드번호가 다르므로 베드번호 변환이 필요
//	    		if( szYD_STK_COL_GP.substring(2, 4).equals("RT") ) {
//	    			//Roller Table인 경우
//	    			szMsg ="["+szOperationName+"] Roller Table인 경우 적치열["+szYD_STK_COL_GP+"], 변경 전 적치베드["+szYD_STK_BED_NO+"]" ;
//	    			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//	    			
//	    			szYD_STK_BED_NO = getYdBedNoFromRtBedNo(szYD_STK_BED_NO);
//	    			
//	    			szMsg ="["+szOperationName+"] Roller Table인 경우 적치열["+szYD_STK_COL_GP+"], 변경 후 베드정보["+szYD_STK_BED_NO+"] " ;
//	    			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//	    		}else if( szYD_STK_COL_GP.substring(2, 4).equals("TF") ) {
//	    			//Transfer인 경우
//	    			szMsg ="["+szOperationName+"] Transfer인 경우 적치열["+szYD_STK_COL_GP+"], 변경 전 적치베드["+szYD_STK_BED_NO+"]" ;
//	    			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//	    			
//	    			szYD_STK_BED_NO = getYdBedNoFromTfBedNo(szYD_STK_BED_NO);
//	    			
//	    			szMsg ="["+szOperationName+"] Transfer인 경우 적치열["+szYD_STK_COL_GP+"], 변경 후 적치베드["+szYD_STK_BED_NO+"]" ;
//	    			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//	    		}
//	
//	    		
//	    		//---------------------------------------------------------------------------------------------------------
//	    		//	Y1, Y2좌표 구하기 - 로직 변경
//	    		//	수정자 : 임춘수
//	    		//	수정일 : 2009.12.17
//	    		//---------------------------------------------------------------------------------------------------------
//	    		
//	    		if ( szYD_CRN_GRAB_GP.equals("D") ){
//	    			//---------------------------------------------------------------------------------
//	    			//	일반Bed이고, 1Grab Type이면 Grab 1 적용
//	    			//---------------------------------------------------------------------------------
//	    			
//	    			szCrane_Grab_Use_Gp = "1";
//	    			szGrab_Y1_Addr      = "E1";
//	    			szGrab_Y1_Value     = "" + Tmp_E1;
//	    			szGrab_Y2_Addr      = "EX";
//	    			szGrab_Y2_Value     = "" + Tmp_EX;
//	    			
//	    			//---------------------------------------------------------------------------------
//	    			
//	    		}else if ( szYD_CRN_GRAB_GP.equals("E") ){
//	    			
//	    			if( szYD_STK_BED_L_GP.equals("S") ) {
//	    				
//	    				if( szYD_STK_BED_NO.equals("01") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 2Grab Type이고, 1번지, 단척이면 Grab 1 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szCrane_Grab_Use_Gp = "1";
//	    	    			szGrab_Y1_Addr      = "E2";
//	    	    			szGrab_Y1_Value     = "" + Tmp_E2;
//	    	    			szGrab_Y2_Addr      = "EX";
//	    	    			szGrab_Y2_Value     = "" + Tmp_EX;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					
//	    				}else if( szYD_STK_BED_NO.equals("02") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 2Grab Type이고, 2번지, 단척이면 Grab 2 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szCrane_Grab_Use_Gp = "2";
//	    	    			szGrab_Y1_Addr      = "EX";
//	    	    			szGrab_Y1_Value     = "" + Tmp_EX;
//	    	    			szGrab_Y2_Addr      = "E3";
//	    	    			szGrab_Y2_Value     = "" + Tmp_E3;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    	    			
//	    				}else if( szYD_STK_BED_NO.equals("03") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 2Grab Type이고, 3번지, 단척이면 Grab 2 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szCrane_Grab_Use_Gp = "2";
//	    	    			szGrab_Y1_Addr      = "EX";
//	    	    			szGrab_Y1_Value     = "" + Tmp_EX;
//	    	    			szGrab_Y2_Addr      = "E4";
//	    	    			szGrab_Y2_Value     = "" + Tmp_E4;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    	    			
//	    				}
//	    				
//	    				
//	    			}else if( szYD_STK_BED_L_GP.equals("M") ) {
//	    				
//	    				if( szYD_STK_BED_NO.equals("01") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 2Grab Type이고, 1번지, 중척이면 Grab 1
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szCrane_Grab_Use_Gp = "1";
//	    	    			szGrab_Y1_Addr      = "E5";
//	    	    			szGrab_Y1_Value     = "" + Tmp_E5;
//	    	    			szGrab_Y2_Addr      = "EX";
//	    	    			szGrab_Y2_Value     = "" + Tmp_EX;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					
//	    				}else if( szYD_STK_BED_NO.equals("03") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 2Grab Type이고, 3번지, 중척이면 Grab 2 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szCrane_Grab_Use_Gp = "2";
//	    	    			szGrab_Y1_Addr      = "EX";
//	    	    			szGrab_Y1_Value     = "" + Tmp_EX;
//	    	    			szGrab_Y2_Addr      = "E5";
//	    	    			szGrab_Y2_Value     = "" + Tmp_E5;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    	    			
//	    				}
//	
//	    			}else if( szYD_STK_BED_L_GP.equals("L") ) {
//	    				if( szYD_STK_BED_NO.equals("01") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 2Grab Type이고, 1번지, 장척이면 Grab 1, 2 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szCrane_Grab_Use_Gp = "3";
//	    	    			szGrab_Y1_Addr      = "E4";
//	    	    			szGrab_Y1_Value     = "" + Tmp_E4;
//	    	    			szGrab_Y2_Addr      = "E6";
//	    	    			szGrab_Y2_Value     = "" + Tmp_E6;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    	    			
//	    				}
//	    			}else if( szYD_STK_BED_L_GP.equals("X") ) {
//	    				if( szYD_STK_BED_NO.equals("01") ) {
//	    					
//	    					//---------------------------------------------------------------------------------
//	    					//	일반Bed이고, 2Grab Type이고, 1번지, 초장척이면 Grab 1, 2 적용
//	    					//---------------------------------------------------------------------------------
//	    					
//	    					szCrane_Grab_Use_Gp = "3";
//	    	    			szGrab_Y1_Addr      = "E7";
//	    	    			szGrab_Y1_Value     = "" + Tmp_E7;
//	    	    			szGrab_Y2_Addr      = "E8";
//	    	    			szGrab_Y2_Value     = "" + Tmp_E8;
//	    					
//	    					//---------------------------------------------------------------------------------
//	    	    			
//	    				}
//	    			}
//	    			
//	    		}
//	    		
//	    		//---------------------------------------------------------------------------------
//    		}
//	    		
//    		//---------------------------------------------------------------------------------------------------------
//    		
//    		
//    		//---------------------------------------------------------------------------------------------------------
//    		// X좌표 구하기
//    		//---------------------------------------------------------------------------------------------------------
//    		
//    		if( szYD_STK_COL_GP.substring(2, 4).equals("TF") ) {
//    			//TRANSFER이면 DB기준값 - (제품 폭/2)
//    			Tmp_X = intYD_STK_BED_XAXIS - (dblYD_MTL_W / 2);
//    		}else if( szYD_STK_COL_GP.substring(2, 4).equals("GJ") ) {
//    			//가적장이면 DB기준값
//    			Tmp_X = intYD_STK_BED_XAXIS;
//    		}else{
//    			//TRANSFER이면 DB기준값 + (베드 폭/2)
//    			Tmp_X = intYD_STK_BED_XAXIS + (dblYD_STK_BED_W_MAX / 2);
//    		}
//    		
//    		//---------------------------------------------------------------------------------------------------------
//    		// Y좌표 구하기
//    		//---------------------------------------------------------------------------------------------------------
//    		
//    		Tmp_Y = Tmp_YD_STK_BED_YAXIS + (Tmp_YD_MTL_L / 2);
//    		//Tmp_Y = procYCalForPlateYd(Tmp_YD_STK_BED_YAXIS, Tmp_YD_MTL_L, szYD_EQP_ID, szYD_STK_BED_L_GP, szYD_STK_BED_NO);
//    		
//    		//---------------------------------------------------------------------------------------------------------
//    		
//    		szGrab_X_Value = String.valueOf(Tmp_X);
//    		szGrab_Y_Value = String.valueOf(Tmp_Y);
//    		
//    		szMsg ="["+szOperationName+"] 소수점 포함 X축값["+szGrab_X_Value+"], Y축값["+szGrab_Y_Value+"] " ;
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			
//			if( !szGrab_X_Value.equals("") ) {
//				int pointIdx = szGrab_X_Value.lastIndexOf(".");
//				if( pointIdx >= 0 ) {
//					szGrab_X_Value = szGrab_X_Value.substring(0, pointIdx);
//				}
//			}
//			
//			szGrab_X_Value = String.valueOf(Integer.parseInt(szGrab_X_Value));
//			
//			szMsg ="["+szOperationName+"] 정수변환 후 X축값["+szGrab_X_Value+"], Y축값["+szGrab_Y_Value+"] " ;
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//    		//--------------------------------------------------------------------------------------------------------- 
//    		
//    		//---------------------------------------------------------------------------------------------------------
//    		recResult.setField("Crane_Grab_Use_Gp", szCrane_Grab_Use_Gp); 	// Grab 사용 구분[1: 1Grab, 2: 2Grab, 3:양쪽Grab 사용]
//    		recResult.setField("Grab_X_Value",      szGrab_X_Value);      	// Grab X 좌표값
//    		recResult.setField("Grab_Y_Value",     	szGrab_Y_Value);     	// Grab Y 좌표값
//    		recResult.setField("Grab_Y1_Addr",      szGrab_Y1_Addr);      	// Grab Y1 Address 코드
//    		recResult.setField("Grab_Y1_Value",     szGrab_Y1_Value);     	// Grab Y1 좌표값
//    		recResult.setField("Grab_Y2_Addr",      szGrab_Y2_Addr);      	// Grab Y2 Address 코드
//    		recResult.setField("Grab_Y2_Value",     szGrab_Y2_Value);     	// Grab Y2 좌표값
//		    //--------------------------------------------------------------------------------------------------------- 
//    		
//    		szMsg ="["+szOperationName+"] -------------------------------- Out -------------------------------- " ;
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//    		
//			ydUtils.displayRecord(szOperationName, recResult);
//			
//			szMsg ="["+szOperationName+"] --------------------------------------------------------------------- " ;
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//    		szMsg ="["+szOperationName+"] 메소드 끝 " ;
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//    	}catch(Exception e){
//			szMsg="<PlateCraneXYCal> Error : "+ e.getLocalizedMessage();
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
//			return false;
//    	}
//    	return true;
//	}
	
	
    public static boolean PlateCraneXYCal(String szSTL_No, 
    		                              String szYD_STK_COL_GP, 
    		                              String szYD_STK_BED_NO, 
    		                              //String szYD_CRN_SCH_ID,
    		                              String szYD_EQP_ID,
    		                              
    		                              JDTORecord recResult){
 
    	// DAO 객체 생성
    	YdCrnSpecDao   ydCrnSpecDao  	= new YdCrnSpecDao();
    	YdStkBedDao    ydStkBedDao    	= new YdStkBedDao();
    	//YdCrnSchDao    ydCrnSchDao     	= new YdCrnSchDao();
    	YdStockDao     ydStockDao     	= new YdStockDao();
    	
    	YdDaoUtils     ydDaoUtils     	= new YdDaoUtils();

    	// Method 선언
    	String szMethodName          	= "PlateCraneXYCal"; 
    	String szOperationName			= "좌표계산(후판제품): 사용안함";

    	// 레코드 선언
    	//JDTORecordSet getYdCrnspec   	= JDTORecordFactory.getInstance().createRecordSet("");
    	JDTORecord    recPara        	= null;
    	JDTORecordSet outRecSet      	= null;
    	JDTORecord    recCrnSpec     	= null;
    	JDTORecord    recStrBed       	= null;
    	//JDTORecord    recCrnSch        	= null;
    	
    	String szYD_CRN_GRAB_GP			= "";
    	String szYD_STK_BED_L_GP		= null;
    	// 변수 선언
    	int intRtnVal                 	= 0;
		double Tmp_X 						= 0;
		int Tmp_Y 						= 0;
		//int Tmp_Y1 						= 0;
		//int Tmp_Y2 						= 0;
		int Tmp_YD_MTL_L         		= 0;
		//int Tmp_YD_EQP_WRK_MAX_L 		= 0;
		int Tmp_YD_STK_BED_YAXIS 		= 0;
		int Tmp_YD_CRN_TONG_L    		= 0;
		
		//---------------------------------------------------------------------------------------------------------
		//	1Grab Type 변수 정의
		//---------------------------------------------------------------------------------------------------------
		
		int Tmp_D1 						= 0;
		int Tmp_D2 						= 0;
		int Tmp_D3 						= 0;
		int Tmp_D4 						= 0;
		int Tmp_D5 						= 0;
		int Tmp_D6 						= 0;
		int Tmp_D7 						= 0;
		int Tmp_D8 						= 0;
		int Tmp_D9 						= 0;
		int Tmp_DA 						= 0;
		int Tmp_DX 						= 0;
		
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		//	2Grab Type 변수 정의
		//---------------------------------------------------------------------------------------------------------
		
		int Tmp_E1 						= 0;
		int Tmp_E2 						= 0;
		int Tmp_E3 						= 0;
		int Tmp_E4 						= 0;
		int Tmp_E5 						= 0;
		int Tmp_E6 						= 0;
		int Tmp_E7 						= 0;
		int Tmp_E8 						= 0;
		int Tmp_E9 						= 0;
		int Tmp_EA 						= 0;
		int Tmp_EX 						= 0;
		
		//---------------------------------------------------------------------------------------------------------
		
    	String szMsg                 	= "";
    	double dblYD_MTL_W				= 0;
    	String szYD_MTL_L               = ""; 
    	String szYD_CRN_TONG_L     	  	= "";
    	//String szYD_EQP_WRK_MAX_W  	    = "";
    	//String szYD_EQP_WRK_MAX_L  		= "";
    	int intYD_STK_BED_XAXIS 		= 0;
    	String szYD_STK_BED_YAXIS 		= "";
		String szCrane_Grab_Use_Gp 		= "";
		String szGrab_X_Value     		= "";
		String szGrab_Y_Value     		= "";
		String szGrab_Y1_Addr      		= "";
		String szGrab_Y1_Value     		= "";
		String szGrab_Y2_Addr      		= "";
		String szGrab_Y2_Value     		= "";    	
		
		
    	try{ 
    		szMsg ="["+szOperationName+"] 메소드 시작 - 파라미터 확인 " ;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg ="["+szOperationName+"] 재료번호 - " + szSTL_No;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg ="["+szOperationName+"] 적치열구분 - " + szYD_STK_COL_GP + ", 베드번호 - " + szYD_STK_BED_NO;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg ="["+szOperationName+"] 크레인설비ID - " + szYD_EQP_ID;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
			
			if( szYD_EQP_ID.substring(5).equals("1") ) {
				szYD_CRN_GRAB_GP = "E";
			}else{
				szYD_CRN_GRAB_GP = "D";
			}
			
			szMsg ="["+szOperationName+"] 크레인 Grab 구분[D : 1Grab Crane(2호기), E : 2Grab Crane(1호기)] - " + szYD_CRN_GRAB_GP;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
    		//---------------------------------------------------------------------------------------------------------
    		// 저장품 조회
    		// 제품길이 (YD_MTL_L) 추출
    		//---------------------------------------------------------------------------------------------------------
			szMsg ="["+szOperationName+"] 재료번호[" + szSTL_No + "]로 저장품 조회 시작";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
    		recPara = JDTORecordFactory.getInstance().create();
    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
    		
    		recPara.setField("STL_NO", szSTL_No);
    		intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 0);
			if(intRtnVal < 0){
				szMsg ="["+szOperationName+" - 오류발생]: 저장품조회  중 오류 [" + intRtnVal + "]" ;
				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
				return false;
			} else if(intRtnVal == 0){
				szMsg ="["+szOperationName+" - 오류발생]: 저장품조회  중 건수가 없음 [" + intRtnVal + "]" ;
				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
				return false;
			}
			
			outRecSet.first();
    		recCrnSpec = outRecSet.getRecord();
    		
    		dblYD_MTL_W = ydDaoUtils.paraRecChkNullDouble(recCrnSpec, "YD_MTL_W"); 			// 제품폭
    		szYD_MTL_L = ydDaoUtils.paraRecChkNull(recCrnSpec, "YD_MTL_L"); 			// 제품길이
    		szYD_STK_BED_L_GP = ydDaoUtils.paraRecChkNull(recCrnSpec, "YD_MTL_L_GP"); 	// 베드의 길이구분은 제품의 길이구분으로 대체
    		
    		szMsg ="["+szOperationName+"] 재료번호[" + szSTL_No + "] - 제품폭["+dblYD_MTL_W+"], 제품길이["+szYD_MTL_L+"], 제품의길이구분["+szYD_STK_BED_L_GP+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
    		if( !szYD_STK_BED_L_GP.equals("") ) {
    			szYD_STK_BED_L_GP = szYD_STK_BED_L_GP.substring(0, 1);
    			szMsg ="["+szOperationName+"] 재료번호[" + szSTL_No + "]의 제품의길이구분["+szYD_STK_BED_L_GP+"]존재하므로 베드의 길이구분자로 대체 사용 가능";
    			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
    		}
    		//---------------------------------------------------------------------------------------------------------
    		
    		//---------------------------------------------------------------------------------------------------------
    		// 크레인사양 조회
    		// 설비ID로  크레인 Beam 길이(YD_CRN_TONG_L) 추출
    		//---------------------------------------------------------------------------------------------------------
    		szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]로 크레인사양 조회 시작";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
    		recPara = JDTORecordFactory.getInstance().create();
    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
    		
    		recPara.setField("YD_EQP_ID", szYD_EQP_ID);
    		intRtnVal = ydCrnSpecDao.getYdCrnspec(recPara, outRecSet, 0);
			if(intRtnVal < 0){
				szMsg ="["+szOperationName+" - 오류발생]: 크레인사양조회  중 오류 [" + intRtnVal + "]" ;
				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
				return false;
			} else if(intRtnVal == 0){
				szMsg ="["+szOperationName+" - 오류발생]: 크레인사양조회  중 건수가 없음 [" + intRtnVal + "]" ;
				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
				return false;
			}
			
			outRecSet.first();
    		recCrnSpec = outRecSet.getRecord();
    		
    		szYD_CRN_TONG_L = ydDaoUtils.paraRecChkNull(recCrnSpec, "YD_CRN_TONG_L"); // 크레인 Beam 길이
    		
    		szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]의 크레인Beam길이[YD_CRN_TONG_L:"+szYD_CRN_TONG_L+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
    		//---------------------------------------------------------------------------------------------------------

    		//---------------------------------------------------------------------------------------------------------
    		// 적치Bed 조회
    		// 야드적치열구분, 야드적치Bed번호로  야드적치BedY축(YD_STK_BED_YAXIS) 추출
    		//---------------------------------------------------------------------------------------------------------
			szMsg ="["+szOperationName+"] 적치열구분[" + szYD_STK_COL_GP + "], 베드번호[" + szYD_STK_BED_NO + "]로 베드 조회 시작";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
    		recPara = JDTORecordFactory.getInstance().create();
    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
    		
    		recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
    		recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
    		intRtnVal = ydStkBedDao.getYdStkbed(recPara, outRecSet, 0);
			if(intRtnVal < 0){
				szMsg ="["+szOperationName+" - 오류발생]: 적치Bed 조회  중 오류 [" + intRtnVal + "]" ;
				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
				return false;
			} else if(intRtnVal == 0){
				szMsg ="["+szOperationName+" - 오류발생]: 적치Bed 조회  중 건수가 없음 [" + intRtnVal + "]" ;
				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
				return false;
			}
			
			outRecSet.first();
			recStrBed = outRecSet.getRecord();
    		
			intYD_STK_BED_XAXIS = ydDaoUtils.paraRecChkNullInt(recStrBed, "YD_STK_BED_XAXIS"); 		// 야드적치BedY축
    		szYD_STK_BED_YAXIS = ydDaoUtils.paraRecChkNull(recStrBed, "YD_STK_BED_YAXIS"); 		// 야드적치BedY축
    		//if( szYD_STK_BED_L_GP.equals("") ) {
    			szYD_STK_BED_L_GP = ydDaoUtils.paraRecChkNull(recStrBed, "YD_STK_BED_L_GP"); 	//야드적치Bed길이구분
    		//}
    		
    		szMsg ="["+szOperationName+"] 적치열구분[" + szYD_STK_COL_GP + "], 베드번호[" + szYD_STK_BED_NO + "]의  야드적치BED X축["+intYD_STK_BED_XAXIS+"], 야드적치BED Y축["+szYD_STK_BED_YAXIS+"], 야드적치Bed길이구분["+szYD_STK_BED_L_GP+"] 조회 완료";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
    		//---------------------------------------------------------------------------------------------------------
    		
    		
    		//---------------------------------------------------------------------------------------------------------
    		// 계산
    		//---------------------------------------------------------------------------------------------------------
    		// 크레인 Grab 구분      szYD_CRN_GRAB_GP
    		// 야드적치Bed번호        szYD_STK_BED_NO
    		// 야드적치Bed길이구분 szYD_STK_BED_L_GP
    		// 제품길이                     szYD_MTL_L
    		// 야드설비작업최대길이 szYD_EQP_WRK_MAX_L
    		// Bed Y-Address    szYD_STK_BED_YAXIS
    		// 크레인 Beam 길이       szYD_CRN_TONG_L
    		//---------------------------------------------------------------------------------------------------------
    		if( szYD_MTL_L.trim().equals("") ){
    			szYD_MTL_L = "0";
    		}
   		    Tmp_YD_MTL_L         		= Integer.parseInt(szYD_MTL_L);             // 크레인스케줄작업재료들중에서 제일 긴 재료의 제품길이

    		//if(szYD_EQP_WRK_MAX_L == null || szYD_EQP_WRK_MAX_L.trim().equals("")){
    		//	szYD_EQP_WRK_MAX_L = "0";
    		//}   		    
   		    //Tmp_YD_EQP_WRK_MAX_L 		= Integer.parseInt(szYD_EQP_WRK_MAX_L);     // 야드설비작업최대길이
   		    
    		if( szYD_STK_BED_YAXIS.equals("") ){
    			szYD_STK_BED_YAXIS = "0";
    		}   		       		    
   		    Tmp_YD_STK_BED_YAXIS 		= Integer.parseInt(szYD_STK_BED_YAXIS);     // Bed Y-Address
   		    
    		if( szYD_CRN_TONG_L.equals("") ){
    			szYD_CRN_TONG_L = "0";
    		}   		       		       		    
    		Tmp_YD_CRN_TONG_L    		= Integer.parseInt(szYD_CRN_TONG_L);        // 크레인 Beam 길이
    		//---------------------------------------------------------------------------------------------------------
 
    		
    		//----------------------------------------------------------------------------------------------
			//	1 Grab Type Crane
			//----------------------------------------------------------------------------------------------
    		
    		if ( szYD_CRN_GRAB_GP.equals("D") ) {
    			
    			
    			//---------------------------------------------------------------------------------------------------------
	    		//	Y좌표값 계산식
	    		//	수정자 : 임춘수
	    		//	수정일 : 2009.12.18
	    		//---------------------------------------------------------------------------------------------------------
    			
    			/*
	    		 * D1 계산 로직
	    		 * Bed Y_기준점+ 6,490
	    		 */
    			Tmp_D1	= Tmp_YD_STK_BED_YAXIS + 6490;
    			
    			/*
	    		 * D2 계산 로직
	    		 * Bed Y_기준점+ 8,790
	    		 */
    			Tmp_D2	= Tmp_YD_STK_BED_YAXIS + 8790;
    			
    			/*
	    		 * D3 계산 로직
	    		 * 10,600
	    		 */
    			Tmp_D3	= 10600;
    			
    			/*
	    		 * D4 계산 로직
	    		 * Bed Y_기준점 + (제품길이 / 2)
	    		 */
    			Tmp_D4	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_MTL_L / 2);
    			
    			/*
	    		 * D5 계산 로직
	    		 * 24,300
	    		 */
    			Tmp_D5	= 24300;
    			
    			/*
	    		 * D6 계산 로직
	    		 * Bed Y_기준점 + (제품길이 / 2)
	    		 */
    			Tmp_D6	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_MTL_L / 2);
    			
    			/*
	    		 * D7 계산 로직
	    		 * Bed Y_기준점 + (제품길이 / 2)
	    		 */
    			Tmp_D7	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_MTL_L / 2);
    			
    			/*
	    		 * D8 계산 로직
	    		 * Bed Y_기준점 - (제품길이 - 14,000) + (제품길이 / 2)
	    		 */
    			Tmp_D8	= Tmp_YD_STK_BED_YAXIS - (Tmp_YD_MTL_L - 14000) + (Tmp_YD_MTL_L / 2);
    			
    			//---------------------------------------------------------------------------------------------------------
    			
    			szCrane_Grab_Use_Gp = "1";
 
//DONG_INSERT    			
    			if( szYD_STK_COL_GP.substring(2, 4).equals("RT") ) {
    				
    				//----------------------------------------------------------------------------------------------
    				//	Roller Table 상
    				//----------------------------------------------------------------------------------------------
    				
    				if( szYD_STK_BED_NO.equals("10") 
    						|| szYD_STK_BED_NO.equals("30") 
    						|| szYD_STK_BED_NO.equals("50") 
    						|| szYD_STK_BED_NO.equals("70") )	{
    					
    					szGrab_Y1_Addr      = "D1";
		    			szGrab_Y1_Value     = "" + Tmp_D1;
		    			szGrab_Y2_Addr      = "DX";
		    			szGrab_Y2_Value     = "" + Tmp_DX;
    					
    				}else if( szYD_STK_BED_NO.equals("20") 
    						|| szYD_STK_BED_NO.equals("40") 
    						|| szYD_STK_BED_NO.equals("60") 
    						|| szYD_STK_BED_NO.equals("80") )	{
    					
    					szGrab_Y1_Addr      = "D2";
		    			szGrab_Y1_Value     = "" + Tmp_D2;
		    			szGrab_Y2_Addr      = "DX";
		    			szGrab_Y2_Value     = "" + Tmp_DX;
		    			
    				}
    				
    				//----------------------------------------------------------------------------------------------
    				
    			}else if( szYD_STK_COL_GP.substring(2, 4).equals("TF") ) {
    				
    				//----------------------------------------------------------------------------------------------
    				//	Transfer 상
    				//----------------------------------------------------------------------------------------------
    				
    				if( szYD_STK_BED_NO.equals("06"))	{
    					
		    			szGrab_Y1_Addr      = "D1";
		    			szGrab_Y1_Value     = "" + Tmp_D1;
		    			szGrab_Y2_Addr      = "DX";
		    			szGrab_Y2_Value     = "" + Tmp_DX;
		    			
    				}else if( szYD_STK_BED_NO.equals("16"))	{
    					
    					szGrab_Y1_Addr      = "D2";
		    			szGrab_Y1_Value     = "" + Tmp_D2;
		    			szGrab_Y2_Addr      = "DX";
		    			szGrab_Y2_Value     = "" + Tmp_DX;
		    			
    				}
    				
    				//----------------------------------------------------------------------------------------------
    			}else{
    				
    				//----------------------------------------------------------------------------------------------
    				//	일반 Bed 상
    				//----------------------------------------------------------------------------------------------
    				
    				if( szYD_STK_BED_L_GP.equals("S") ) {
	    				
	    				if( szYD_STK_BED_NO.equals("01") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 1번지, 단척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szGrab_Y1_Addr      = "D3";
	    	    			szGrab_Y1_Value     = "" + Tmp_D3;
	    	    			szGrab_Y2_Addr      = "DX";
	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
	    					
	    					//---------------------------------------------------------------------------------
	    					
	    				}else if( szYD_STK_BED_NO.equals("02") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 2번지, 단척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szGrab_Y1_Addr      = "D4";
	    	    			szGrab_Y1_Value     = "" + Tmp_D4;
	    	    			szGrab_Y2_Addr      = "DX";
	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
	    					
	    					//---------------------------------------------------------------------------------
	    	    			
	    				}else if( szYD_STK_BED_NO.equals("03") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 3번지, 단척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szGrab_Y1_Addr      = "D5";
	    	    			szGrab_Y1_Value     = "" + Tmp_D5;
	    	    			szGrab_Y2_Addr      = "DX";
	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
	    					
	    					//---------------------------------------------------------------------------------
	    	    			
	    				}
	    				
	    				
	    			}else if( szYD_STK_BED_L_GP.equals("M") ) {
	    				
	    				if( szYD_STK_BED_NO.equals("01") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 1번지, 중척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szGrab_Y1_Addr      = "D6";
	    	    			szGrab_Y1_Value     = "" + Tmp_D6;
	    	    			szGrab_Y2_Addr      = "DX";
	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
	    					
	    					//---------------------------------------------------------------------------------
	    					
	    				}else if( szYD_STK_BED_NO.equals("02") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 2번지, 중척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szGrab_Y1_Addr      = "D7";
	    	    			szGrab_Y1_Value     = "" + Tmp_D7;
	    	    			szGrab_Y2_Addr      = "DX";
	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
	    					
	    					//---------------------------------------------------------------------------------
	    	    			
	    				}else if( szYD_STK_BED_NO.equals("03") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 3번지, 중척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szGrab_Y1_Addr      = "D8";
	    	    			szGrab_Y1_Value     = "" + Tmp_D8;
	    	    			szGrab_Y2_Addr      = "DX";
	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
	    					
	    					//---------------------------------------------------------------------------------
	    	    			
	    				}
	    			}else if( szYD_STK_BED_L_GP.equals("L") ) {
	    				
	    				if( szYD_STK_BED_NO.equals("01") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 1번지, 장척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szGrab_Y1_Addr      = "D6";
	    	    			szGrab_Y1_Value     = "" + Tmp_D6;
	    	    			szGrab_Y2_Addr      = "DX";
	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
	    					
	    					//---------------------------------------------------------------------------------
	    					
	    				}else if( szYD_STK_BED_NO.equals("02") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 2번지, 장척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szGrab_Y1_Addr      = "D7";
	    	    			szGrab_Y1_Value     = "" + Tmp_D7;
	    	    			szGrab_Y2_Addr      = "DX";
	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
	    					
	    					//---------------------------------------------------------------------------------
	    	    			
	    				}else if( szYD_STK_BED_NO.equals("03") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 3번지, 장척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szGrab_Y1_Addr      = "D8";
	    	    			szGrab_Y1_Value     = "" + Tmp_D8;
	    	    			szGrab_Y2_Addr      = "DX";
	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
	    					
	    					//---------------------------------------------------------------------------------
	    	    			
	    				}
	    			}else if( szYD_STK_BED_L_GP.equals("X") ) {
	    				
	    				if( szYD_STK_BED_NO.equals("01") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 1번지, 초장척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szGrab_Y1_Addr      = "D6";
	    	    			szGrab_Y1_Value     = "" + Tmp_D6;
	    	    			szGrab_Y2_Addr      = "DX";
	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
	    					
	    					//---------------------------------------------------------------------------------
	    					
	    				}else if( szYD_STK_BED_NO.equals("02") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 2번지, 초장척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szGrab_Y1_Addr      = "D7";
	    	    			szGrab_Y1_Value     = "" + Tmp_D7;
	    	    			szGrab_Y2_Addr      = "DX";
	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
	    					
	    					//---------------------------------------------------------------------------------
	    	    			
	    				}else if( szYD_STK_BED_NO.equals("03") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 3번지, 초장척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szGrab_Y1_Addr      = "D8";
	    	    			szGrab_Y1_Value     = "" + Tmp_D8;
	    	    			szGrab_Y2_Addr      = "DX";
	    	    			szGrab_Y2_Value     = "" + Tmp_DX;
	    					
	    					//---------------------------------------------------------------------------------
	    	    			
	    				}
	    			}
    				
    				//----------------------------------------------------------------------------------------------
    				
    			}
    			
    			//---------------------------------------------------------------------------------------------------------
    		}
    		//----------------------------------------------------------------------------------------------
			//	2 Grab Type Crane
			//----------------------------------------------------------------------------------------------
    		else if ( szYD_CRN_GRAB_GP.equals("E") ) {
    			
	    		
	    		//---------------------------------------------------------------------------------------------------------
	    		//	Y좌표값 계산식 수정 반영
	    		//	수정자 : 임춘수
	    		//	수정일 : 2009.12.17
	    		//---------------------------------------------------------------------------------------------------------
	    		/*
	    		 * E1  계산 로직
	    		 *	if   제품길이 < 14,001 
				 *	      Bed Y_기준점+ 7,000
				 *	else
				 *	      Bed Y_기준점+ (빔길이 / 2)
	    		 */
	
	    		if( Tmp_YD_MTL_L < 14001 ) {
	    			Tmp_E1	= Tmp_YD_STK_BED_YAXIS + 7000;
	    		}else{
	    			Tmp_E1	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_CRN_TONG_L / 2);
	    		}
	    		
	    		/*
	    		 * E2 계산 로직
	    		 * Bed Y_기준점+ 2000 + (Beam길이 / 2)
	    		 */
	    		Tmp_E2	= Tmp_YD_STK_BED_YAXIS + 2000 + (Tmp_YD_CRN_TONG_L / 2);
	    		
	    		/*
	    		 * E3 계산 로직
	    		 * Bed Y_기준점+ 4500 + (Beam길이 / 2)
	    		 */
	    		Tmp_E3	= Tmp_YD_STK_BED_YAXIS + 4500 + (Tmp_YD_CRN_TONG_L / 2);
	    		
	    		/*
	    		 * E4 계산 로직
	    		 * Bed Y_기준점+ (Beam길이 / 2)
	    		 */
	    		Tmp_E4	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_CRN_TONG_L / 2);
	    		
	    		/*
	    		 * E5 계산 로직
	    		 * Bed Y_기준점+ (제품길이 / 2)
	    		 */
	    		Tmp_E5	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_MTL_L / 2);
	    		
	    		/*
	    		 * E6 계산 로직
	    		 * Bed Y_기준점+ (빔길이+2000) + (빔길이/2)
	    		 */
	    		Tmp_E6	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_CRN_TONG_L + 2000) + (Tmp_YD_CRN_TONG_L / 2);
	    		
	    		/*
	    		 * E7 계산 로직
	    		 * if   제품길이 〉23501
	    		 * 		(Bed Y_기준점+ 900) + (빔길이 / 2)
	    		 * else
	    		 * 		Bed Y_기준점+ (빔길이 / 2)
	    		 */
	    		if( Tmp_YD_MTL_L > 23501 ) {
	    			Tmp_E7	= Tmp_YD_STK_BED_YAXIS + 900 + (Tmp_YD_CRN_TONG_L / 2);
	    		}else{
	    			Tmp_E7	= Tmp_YD_STK_BED_YAXIS + (Tmp_YD_CRN_TONG_L / 2);
	    		}
	    		
	    		/*
	    		 * E8 계산 로직
	    		 * If 제품길이 〉23501
	    		 * 		(Bed Y_기준점+ 900) +  빔길이 + (제품길이-22000-900) + (빔길이/2)
	    		 * else
	    		 * 		Bed Y_기준점+ (빔길이+2000) + (빔길이/2)
	    		 */
	    		if( Tmp_YD_MTL_L > 23501 ) {
	    			Tmp_E8	= Tmp_YD_STK_BED_YAXIS + 900 + Tmp_YD_CRN_TONG_L + Tmp_YD_MTL_L - 22000 - 900 + (Tmp_YD_CRN_TONG_L / 2);;
	    		}else{
	    			Tmp_E8	= Tmp_YD_STK_BED_YAXIS + Tmp_YD_CRN_TONG_L + 2000 + (Tmp_YD_CRN_TONG_L / 2);
	    		}
	    		
	    		//---------------------------------------------------------------------------------------------------------
	    		
	    		
	    		//E9	= 권상 또는 권하 1Grab Type 적용
	    		Tmp_E9	= 0;
	    		//EA	= 권상 또는 권하 2Grab Type 적용
	    		Tmp_EA	= 0;
	    		//EX	= Skip
	    		Tmp_EX	= 0;
	    		
	    		
	    		//---------------------------------------------------------------------------------------------------------
	    		// 조건문에 따라 결과값을 Return
	    		//---------------------------------------------------------------------------------------------------------
	    		// 조건 검색 항목
	    		// 크레인 Grab 구분      szYD_CRN_GRAB_GP
	    		// 야드적치Bed번호        szYD_STK_BED_NO
	    		// 야드적치Bed길이구분 szYD_STK_BED_L_GP
	    		//
	    		// 결과값
	    		// JDTORecord resResult.Crane_Grab_Use_Gp : Grab 사용 구분
	    	    // JDTORecord resResult.Grab_Y1_Addr      : Grab Y1 Address 코드
	    	    // JDTORecord resResult.Grab_Y1_Value     : Grab Y1 좌표값
	    	    // JDTORecord resResult.Grab_Y2_Addr      : Grab Y2 Address 코드
	    	    // JDTORecord resResult.Grab_Y2_Value     : Grab Y2 좌표값
	    		//---------------------------------------------------------------------------------------------------------
	    		
	    		//Roller Table이거나 Transfer인 경우에는 베드번호가 다르므로 베드번호 변환이 필요
	    		if( szYD_STK_COL_GP.substring(2, 4).equals("RT") ) {
	    			//Roller Table인 경우
	    			szMsg ="["+szOperationName+"] Roller Table인 경우 적치열["+szYD_STK_COL_GP+"], 변경 전 적치베드["+szYD_STK_BED_NO+"]" ;
	    			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			szYD_STK_BED_NO = getYdBedNoFromRtBedNo(szYD_STK_BED_NO);
	    			
	    			szMsg ="["+szOperationName+"] Roller Table인 경우 적치열["+szYD_STK_COL_GP+"], 변경 후 베드정보["+szYD_STK_BED_NO+"] " ;
	    			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
	    		}else if( szYD_STK_COL_GP.substring(2, 4).equals("TF") ) {
	    			//Transfer인 경우
	    			szMsg ="["+szOperationName+"] Transfer인 경우 적치열["+szYD_STK_COL_GP+"], 변경 전 적치베드["+szYD_STK_BED_NO+"]" ;
	    			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			szYD_STK_BED_NO = getYdBedNoFromTfBedNo(szYD_STK_BED_NO);
	    			
	    			szMsg ="["+szOperationName+"] Transfer인 경우 적치열["+szYD_STK_COL_GP+"], 변경 후 적치베드["+szYD_STK_BED_NO+"]" ;
	    			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
	    		}
	
	    		
	    		//---------------------------------------------------------------------------------------------------------
	    		//	Y1, Y2좌표 구하기 - 로직 변경
	    		//	수정자 : 임춘수
	    		//	수정일 : 2009.12.17
	    		//---------------------------------------------------------------------------------------------------------
	    		
	    		if ( szYD_CRN_GRAB_GP.equals("D") ){
	    			//---------------------------------------------------------------------------------
	    			//	일반Bed이고, 1Grab Type이면 Grab 1 적용
	    			//---------------------------------------------------------------------------------
	    			
	    			szCrane_Grab_Use_Gp = "1";
	    			szGrab_Y1_Addr      = "E1";
	    			szGrab_Y1_Value     = "" + Tmp_E1;
	    			szGrab_Y2_Addr      = "EX";
	    			szGrab_Y2_Value     = "" + Tmp_EX;
	    			
	    			//---------------------------------------------------------------------------------
	    			
	    		}else if ( szYD_CRN_GRAB_GP.equals("E") ){
	    			
	    			if( szYD_STK_BED_L_GP.equals("S") ) {
	    				
	    				if( szYD_STK_BED_NO.equals("01") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 2Grab Type이고, 1번지, 단척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szCrane_Grab_Use_Gp = "1";
	    	    			szGrab_Y1_Addr      = "E2";
	    	    			szGrab_Y1_Value     = "" + Tmp_E2;
	    	    			szGrab_Y2_Addr      = "EX";
	    	    			szGrab_Y2_Value     = "" + Tmp_EX;
	    					
	    					//---------------------------------------------------------------------------------
	    					
	    				}else if( szYD_STK_BED_NO.equals("02") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 2Grab Type이고, 2번지, 단척이면 Grab 2 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szCrane_Grab_Use_Gp = "2";
	    	    			szGrab_Y1_Addr      = "EX";
	    	    			szGrab_Y1_Value     = "" + Tmp_EX;
	    	    			szGrab_Y2_Addr      = "E3";
	    	    			szGrab_Y2_Value     = "" + Tmp_E3;
	    					
	    					//---------------------------------------------------------------------------------
	    	    			
	    				}else if( szYD_STK_BED_NO.equals("03") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 2Grab Type이고, 3번지, 단척이면 Grab 2 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szCrane_Grab_Use_Gp = "2";
	    	    			szGrab_Y1_Addr      = "EX";
	    	    			szGrab_Y1_Value     = "" + Tmp_EX;
	    	    			szGrab_Y2_Addr      = "E4";
	    	    			szGrab_Y2_Value     = "" + Tmp_E4;
	    					
	    					//---------------------------------------------------------------------------------
	    	    			
	    				}
	    				
	    				
	    			}else if( szYD_STK_BED_L_GP.equals("M") ) {
	    				
	    				if( szYD_STK_BED_NO.equals("01") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 2Grab Type이고, 1번지, 중척이면 Grab 1
	    					//---------------------------------------------------------------------------------
	    					
	    					szCrane_Grab_Use_Gp = "1";
	    	    			szGrab_Y1_Addr      = "E5";
	    	    			szGrab_Y1_Value     = "" + Tmp_E5;
	    	    			szGrab_Y2_Addr      = "EX";
	    	    			szGrab_Y2_Value     = "" + Tmp_EX;
	    					
	    					//---------------------------------------------------------------------------------
	    					
	    				}else if( szYD_STK_BED_NO.equals("03") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 2Grab Type이고, 3번지, 중척이면 Grab 2 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szCrane_Grab_Use_Gp = "2";
	    	    			szGrab_Y1_Addr      = "EX";
	    	    			szGrab_Y1_Value     = "" + Tmp_EX;
	    	    			szGrab_Y2_Addr      = "E5";
	    	    			szGrab_Y2_Value     = "" + Tmp_E5;
	    					
	    					//---------------------------------------------------------------------------------
	    	    			
	    				}
	
	    			}else if( szYD_STK_BED_L_GP.equals("L") ) {
	    				if( szYD_STK_BED_NO.equals("01") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 2Grab Type이고, 1번지, 장척이면 Grab 1, 2 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szCrane_Grab_Use_Gp = "3";
	    	    			szGrab_Y1_Addr      = "E4";
	    	    			szGrab_Y1_Value     = "" + Tmp_E4;
	    	    			szGrab_Y2_Addr      = "E6";
	    	    			szGrab_Y2_Value     = "" + Tmp_E6;
	    					
	    					//---------------------------------------------------------------------------------
	    	    			
	    				}
	    			}else if( szYD_STK_BED_L_GP.equals("X") ) {
	    				if( szYD_STK_BED_NO.equals("01") ) {
	    					
	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 2Grab Type이고, 1번지, 초장척이면 Grab 1, 2 적용
	    					//---------------------------------------------------------------------------------
	    					
	    					szCrane_Grab_Use_Gp = "3";
	    	    			szGrab_Y1_Addr      = "E7";
	    	    			szGrab_Y1_Value     = "" + Tmp_E7;
	    	    			szGrab_Y2_Addr      = "E8";
	    	    			szGrab_Y2_Value     = "" + Tmp_E8;
	    					
	    					//---------------------------------------------------------------------------------
	    	    			
	    				}
	    			}
	    			
	    		}
	    		
	    		//---------------------------------------------------------------------------------
    		}
	    		
    		//---------------------------------------------------------------------------------------------------------
    		
    		
    		//---------------------------------------------------------------------------------------------------------
    		// X, Y좌표 구하기
    		//---------------------------------------------------------------------------------------------------------
    		if( szYD_STK_COL_GP.substring(2, 4).equals("TF") ) {
    			Tmp_X = intYD_STK_BED_XAXIS - (dblYD_MTL_W / 2);
    		}else{
    			Tmp_X = intYD_STK_BED_XAXIS + (dblYD_MTL_W / 2);
    		}
    		
    		Tmp_Y = Tmp_YD_STK_BED_YAXIS + (Tmp_YD_MTL_L / 2);
    		//Tmp_Y = procYCalForPlateYd(Tmp_YD_STK_BED_YAXIS, Tmp_YD_MTL_L, szYD_EQP_ID, szYD_STK_BED_L_GP, szYD_STK_BED_NO);
    		
    		szGrab_X_Value = String.valueOf(Tmp_X);
    		szGrab_Y_Value = String.valueOf(Tmp_Y);
    		
    		szMsg ="["+szOperationName+"] 소수점 포함 X축값["+szGrab_X_Value+"], Y축값["+szGrab_Y_Value+"] " ;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
			if( !szGrab_X_Value.equals("") ) {
				int pointIdx = szGrab_X_Value.lastIndexOf(".");
				if( pointIdx >= 0 ) {
					szGrab_X_Value = szGrab_X_Value.substring(0, pointIdx);
				}
			}
			
			szGrab_X_Value = String.valueOf(Integer.parseInt(szGrab_X_Value));
			
			szMsg ="["+szOperationName+"] 정수변환 후 X축값["+szGrab_X_Value+"], Y축값["+szGrab_Y_Value+"] " ;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
    		//--------------------------------------------------------------------------------------------------------- 
    		
    		//---------------------------------------------------------------------------------------------------------
    		recResult.setField("Crane_Grab_Use_Gp", szCrane_Grab_Use_Gp); 	// Grab 사용 구분[1: 1Grab, 2: 2Grab, 3:양쪽Grab 사용]
    		recResult.setField("Grab_X_Value",      szGrab_X_Value);      	// Grab X 좌표값
    		recResult.setField("Grab_Y_Value",     	szGrab_Y_Value);     	// Grab Y 좌표값
    		recResult.setField("Grab_Y1_Addr",      szGrab_Y1_Addr);      	// Grab Y1 Address 코드
    		recResult.setField("Grab_Y1_Value",     szGrab_Y1_Value);     	// Grab Y1 좌표값
    		recResult.setField("Grab_Y2_Addr",      szGrab_Y2_Addr);      	// Grab Y2 Address 코드
    		recResult.setField("Grab_Y2_Value",     szGrab_Y2_Value);     	// Grab Y2 좌표값
		    //--------------------------------------------------------------------------------------------------------- 
    		
    		szMsg ="["+szOperationName+"] -------------------------------- Out -------------------------------- " ;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
    		
			ydUtils.displayRecord(szOperationName, recResult);
			
			szMsg ="["+szOperationName+"] --------------------------------------------------------------------- " ;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
    		szMsg ="["+szOperationName+"] 메소드 끝 " ;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
    	}catch(Exception e){
			szMsg="<PlateCraneXYCal> Error : "+ e.getLocalizedMessage();
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return false;
    	}
    	return true;
    }
    
    /**
     * 후판제품XY좌표계산(3기)
     * @param recPara
     * @param recResult
     * @return
     * @throws JDTOException
     */
    public static String procXYCalForPlateCrane3G(JDTORecord recPara, JDTORecord recResult) throws JDTOException {
    	
    	String szRtnMsg				= YdConstant.RETN_CD_SUCCESS;
    	String szLogMsg 			= ""; 
    	String szMethodName			= "procXYCalForPlateCrane3G";
		String szOperationName		= "후판제품XY좌표계산(3기)";
		int intRtnVal				= -100;
		
		JDTORecordSet   outStlInfoRecSet   = null;
		JDTORecordSet   outStlInfoRecSet1  = null;
		JDTORecordSet   outBedInfoRecSet   = null;		
		JDTORecord		recInPara	= null;
		JDTORecord		recOutPara	= null;
		JDTORecord 		breRecord 	= null;
		
		String szYD_CRN_SCH_ID		= null; //크레인스케줄ID
		String szYD_UP_STK_COL_GP	= null; //크레인설비ID
		String szYD_UP_STK_BED_NO	= null; //권상위치 적치열
		String szYD_DN_STK_COL_GP	= null; //권상위치 적치베드
		String szYD_DN_STK_BED_NO	= null; //권하위치 적치열
		String szYD_EQP_ID			= null; //권하위치 적치베드
		
		String 	szCRANE_GAP_UP_X	= null; //권상X축허용오차
		String 	szCRANE_GAP_UP_Y	= null; //권상Y축허용오차
		//String 	szCRANE_GAP_UP_Z	= null; //권상Z축허용오차
		String 	szCRANE_GAP_DN_X	= null; //권하X축허용오차
		String 	szCRANE_GAP_DN_Y	= null; //권하Y축허용오차
		//String 	szCRANE_GAP_DN_Z	= null;	//권하Z축허용오차
		
		// 전사물류개선 2021. 1.6 Z축 추가
		int		iYD_STK_BED_ZAXIS_UP = 0;	//야드적치Bed길이Z축 (권상)
		int		iYD_STK_BED_ZAXIS_DN = 0;	//야드적치Bed길이Z축 (권하)
		int		iYD_UP_YAXIS = 0;	//야드적치Bed길이Y축(Y1축+Y2축/2) (권상)
		int		iYD_DN_YAXIS = 0;	//야드적치Bed길이Y축(Y1축+Y2축/2) (권하)
		
		int     iGrabType			= 0;	//Grab Type
		int		iGrab1BeamMinSize	= 0;	//Grab#1 Beam Min Size
		int		iGrab1BeamMaxSize	= 0;	//Grab#1 Beam Max Size
		int		iGrab2BeamMinSize	= 0;	//Grab#2 Beam Min Size
		int		iGrab2BeamMaxSize	= 0;	//Grab#2 Beam Max Size		
		int		iBeamGap			= 0;	//Beam 간격
		int     iYdStkBedXaxisTol	= 0;	//X축 허용오차
		int     iYdStkBedYaxisTol	= 0;	//Y축 허용오차
		int     iYdCarWrkXaxisTol	= 0;	//야드차량작업시 X축 허용오차
		int     iYdCarWrkYaxisTol	= 0;	//야드차량작업시 Y축 허용오차
		int     iYdEqpWrkXaxisTol	= 0;	//야드설비작업X축허용오차
		int     iYdEqpWrkYaxisTol	= 0;	//야드설비작업Y축허용오차
		
		double 	dYD_MTL_MAX_W		= 0;	//작업재료 중 가장 넓은 재료의 폭
		int     iYD_MTL_MAX_L		= 0;	//작업재료 중 가장 긴 재료의 길이
		int     iYD_MAX_L		    = 0;	//작업지시 중 가장 긴 재료의 길이
		
		int		iYD_STK_BED_XAXIS_UP = 0;	//야드적치BedX축 (권상)
		int		iYD_STK_BED_YAXIS_UP = 0;	//야드적치BedY축 (권상)
		int		iYD_STK_BED_L_MAX_UP = 0;	//야드적치Bed길이Max (권상)
		
		int		iYD_STK_BED_XAXIS_DN = 0;	//야드적치BedX축 (권하)
		int		iYD_STK_BED_YAXIS_DN = 0;	//야드적치BedY축 (권하)		
		int		iYD_STK_BED_L_MAX_DN = 0;	//야드적치Bed길이Max (권하)
		
		String szYD_STK_BED_W_GP_UP 			= ""; 		// 야드적치Bed폭구분
		String szYD_STK_BED_WHIO_STAT_UP		= ""; 		// 야드적치Bed상태구분
		String szYD_STK_BED_W_GP_DN 			= ""; 		// 야드적치Bed폭구분
		String szYD_STK_BED_WHIO_STAT_DN		= ""; 		// 야드적치Bed상태구분
		
		String szUp_Grab_X_Value = null;	// 권상 X 좌표값
		String szDn_Grab_X_Value = null;	// 권하 X 좌표값
		String szUp_Grab_Y_Value = null;    // 권상 Y 좌표값
		String szUp_Grab_Y1_Value = null;	// 권상 Y1 좌표값
		String szUp_Grab_Y2_Value = null;	// 권상 Y2 좌표값
		String szDn_Grab_Y_Value = null;	// 권하 Y 좌표값
		String szDn_Grab_Y1_Value = null;	// 권하 Y1 좌표값
		String szDn_Grab_Y2_Value = null;	// 권하 Y1 좌표값
		String sCAR_KIND = null;
		
		String szCrane_Grab_Use_Gp = null;  // Grab 사용구분 (1:Grab#1 만 사용 , 2:Grab#2 만 사용 , 3: Grab#1,#2 모두 사용)
		
		YdPlateCommDAO commDao = new YdPlateCommDAO();
		
		
		//--------------------------------------- 수신 파라미터 ------------------------------------------------------------
		szYD_CRN_SCH_ID 	= ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_SCH_ID");			//크레인스케줄ID
		szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID");				//크레인설비ID
		szYD_UP_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_STK_COL_GP");		//권상위치 적치열
		szYD_UP_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_STK_BED_NO");		//권상위치 적치베드
		szYD_DN_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_STK_COL_GP");		//권하위치 적치열
		szYD_DN_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_STK_BED_NO");		//권하위치 적치베드
		//---------------------------------------------------------------------------------------------------------------		
		
		//=============================================================================
		// 1. X,Y 좌표를 구하기 위해 필요한 값을 BRE 와 DB 에서 읽어 온다.
		//-----------------------------------------------------------------------------
		
		// 1.1 크레인설비ID로 BRE 에서 정보를 읽어 온다.(허용오차 포함)
		//업무기준 : YDB675 (후판제품창고 좌표계산 기준)
		
		breRecord 	= JDTORecordFactory.getInstance().create();
		breRecord.setField("YD_EQP_ID", szYD_EQP_ID);
		
		if( GetBreRule6.getYDB675(breRecord) ) {
			
			iGrabType			= ydDaoUtils.paraRecChkNullInt(breRecord, "YDB675_RV01_YD_CRN_GRAB_EA"); 			//Grab Type
			iGrab1BeamMinSize	= ydDaoUtils.paraRecChkNullInt(breRecord, "YDB675_RV02_YD_CRN_GRAB1_BM_MIN_L");		//Grab#1 Beam Min Size
			iGrab1BeamMaxSize	= ydDaoUtils.paraRecChkNullInt(breRecord, "YDB675_RV03_YD_CRN_GRAB1_BM_EXPN_L");	//Grab#1 Beam Max Size
			iGrab2BeamMinSize	= ydDaoUtils.paraRecChkNullInt(breRecord, "YDB675_RV04_YD_CRN_GRAB2_BM_MIN_L");		//Grab#2 Beam Min Size
			iGrab2BeamMaxSize	= ydDaoUtils.paraRecChkNullInt(breRecord, "YDB675_RV05_YD_CRN_GRAB2_BM_EXPN_L");	//Grab#2 Beam Max Size		
			iBeamGap			= ydDaoUtils.paraRecChkNullInt(breRecord, "YDB675_RV06_YD_CRN_BM_MSTT_MGNT_GAP");	//Beam 간격
			iYdStkBedXaxisTol	= ydDaoUtils.paraRecChkNullInt(breRecord, "YDB675_RV07_YD_STK_BED_XAXIS_TOL");		//X축 허용오차
			iYdStkBedYaxisTol	= ydDaoUtils.paraRecChkNullInt(breRecord, "YDB675_RV08_YD_STK_BED_YAXIS_TOL");		//Y축 허용오차
			iYdCarWrkXaxisTol	= ydDaoUtils.paraRecChkNullInt(breRecord, "YDB675_RV09_YD_CAR_WRK_XAXIS_TOL");		//야드차량작업시 X축 허용오차
			iYdCarWrkYaxisTol	= ydDaoUtils.paraRecChkNullInt(breRecord, "YDB675_RV010_YD_CAR_WRK_YAXIS_TOL");		//야드차량작업시 Y축 허용오차		
			iYdEqpWrkXaxisTol	= ydDaoUtils.paraRecChkNullInt(breRecord, "YDB675_RV011_YD_CRN_RULE_X_XYAXIS");		//야드설비작업X축허용오차
			iYdEqpWrkYaxisTol	= ydDaoUtils.paraRecChkNullInt(breRecord, "YDB675_RV012_YD_CRN_RULE_Y_XYAXIS");		//야드설비작업Y축허용오차
			
		} else {
			szLogMsg="["+szOperationName+":"+szMethodName+"] 업무기준 YDB675가 존재하지 않습니다!!!" ;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_SUCCESS;	
		}			
		
		// 1.2 크레인스케줄ID로 작업제품중 가장 긴 제품길이와 가장 긴 폭을 읽어온다.
		recInPara = JDTORecordFactory.getInstance().create();
		outStlInfoRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		recInPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
		
		intRtnVal = commDao.select(recInPara, outStlInfoRecSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0075");
		
		if(intRtnVal < 1) {
			szLogMsg="["+szOperationName+":"+szMethodName+"] 크레인작업재료의 폭과 넓이 조회 시 execution failed - 반환값 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_SUCCESS;			
		}
		
		outStlInfoRecSet.first();
		recInPara = outStlInfoRecSet.getRecord();
		
		dYD_MTL_MAX_W = ydDaoUtils.paraRecChkNullDouble(recInPara, "YD_MTL_W");
		iYD_MTL_MAX_L = ydDaoUtils.paraRecChkNullInt(recInPara, "YD_MTL_L");
		
		
		// 1.3 권상,권하 위치로 해당 BED의 X,Y 좌표와 BED 길이를 읽어온다.
		outBedInfoRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		recInPara.setField("YD_UP_STK_COL_GP", szYD_UP_STK_COL_GP);
		recInPara.setField("YD_UP_STK_BED_NO", szYD_UP_STK_BED_NO);
		recInPara.setField("YD_DN_STK_COL_GP", szYD_DN_STK_COL_GP);
		recInPara.setField("YD_DN_STK_BED_NO", szYD_DN_STK_BED_NO);
		
		intRtnVal = commDao.select(recInPara, outBedInfoRecSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0076");
		
		if(intRtnVal < 1) {
			szLogMsg="["+szOperationName+":"+szMethodName+"] 권상,권하 위치로 해당 BED의 X,Y 좌표와 BED 길이 조회 시 execution failed - 반환값 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_SUCCESS;			
		}	
		
		outBedInfoRecSet.first();
		recInPara = outBedInfoRecSet.getRecord();
		
		iYD_STK_BED_XAXIS_UP = ydDaoUtils.paraRecChkNullInt(recInPara, "YD_STK_BED_XAXIS_UP");	//야드적치BedX축 (권상)
		iYD_STK_BED_YAXIS_UP = ydDaoUtils.paraRecChkNullInt(recInPara, "YD_STK_BED_YAXIS_UP");	//야드적치BedY축 (권상)
		iYD_STK_BED_L_MAX_UP = ydDaoUtils.paraRecChkNullInt(recInPara, "YD_STK_BED_L_MAX_UP");	//야드적치Bed길이Max (권상)
		
		iYD_STK_BED_XAXIS_DN = ydDaoUtils.paraRecChkNullInt(recInPara, "YD_STK_BED_XAXIS_DN");	//야드적치BedX축 (권하)
		iYD_STK_BED_YAXIS_DN = ydDaoUtils.paraRecChkNullInt(recInPara, "YD_STK_BED_YAXIS_DN");	//야드적치BedY축 (권하)		
		iYD_STK_BED_L_MAX_DN = ydDaoUtils.paraRecChkNullInt(recInPara, "YD_STK_BED_L_MAX_DN");	//야드적치Bed길이Max (권하)		
		
		szYD_STK_BED_W_GP_UP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_W_GP_UP"); 			// 야드적치Bed폭구분
		szYD_STK_BED_WHIO_STAT_UP	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_WHIO_STAT_UP"); 		// 야드적치Bed상태구분
		szYD_STK_BED_W_GP_DN 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_W_GP_DN"); 			// 야드적치Bed폭구분
		szYD_STK_BED_WHIO_STAT_DN	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_WHIO_STAT_DN"); 		// 야드적치Bed상태구분
		
	    // 전사물류개선 2021. 1.6 형상측정정보를 셋팅한다.
		// 무인 + 형상측정이 완료되었다라면 
		boolean isAutoCrn = false;
		boolean isAutoCrnCarPoint = false;  // 형상이면서 무인일 경우 구분자 추가  220517 박성열
		if(PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szYD_EQP_ID)){
			
			isAutoCrn = true;
			
			JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	  		JDTORecord params = JDTORecordFactory.getInstance().create();
	  		params.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
	  		params.setField("YD_UP_STK_COL_GP", szYD_UP_STK_COL_GP);
	  		params.setField("YD_UP_STK_BED_NO", szYD_UP_STK_BED_NO);
	  		params.setField("YD_DN_STK_COL_GP", szYD_DN_STK_COL_GP);
	  		params.setField("YD_DN_STK_BED_NO", szYD_DN_STK_BED_NO);
			
	  		
  			szLogMsg="["+szOperationName+":"+szMethodName+"] ==> 유무인체크<==" ;
  			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			// 자동화크레인의 경우 Z축값 혹은 차량형상측정이면 차량종류별 
	  		// 작업예약테이블의 컬럼중 YD_CTS_RELAY_YN :: 값을 형상측정완료 값으로 취급한다. Y-> 측정완료
	  		if(commDao.select(params, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getCarPointFrmRslt") > 0){
	  			//1221  권하위치 X좌표 재계산여부 결정시, 위 쿼리 타는데, 주작업 대상으로 기존 좌표(베드좌표)=기존좌표+재료폭/2 해주는데, 
	  			//재계산할지 여부 체크 기준이 DN_CARKIND_VS 값이 Y일 경우인데, 해당 값이 Y가 되는 조건중하나가 작업이 주작업일때임(YD_AID_WRK_YN='N').
	  			//근데 PT로 내리는 출하작업을 권하위치 변경으로 일반베드로 변경할 경우, 해당 값이 여전히 N으로 셋팅되어있음(보조작업으로 변경됨에도 불구하고)
	  			//48935853
	  			
				//------------------------------------------------------------------------------------------------------------
				//	전사물류개선
				//------------------------------------------------------------------------------------------------------------
	  			isAutoCrnCarPoint = true;  // 여기를 타야 형상으로 판단   220517 박성열
	  			szLogMsg="["+szOperationName+":"+szMethodName+"] ==> 자동화크레인 좌표 시작 <==" ;
	  			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	  			
				iYD_STK_BED_XAXIS_UP = rsResult.getRecord(0).getFieldInt("YD_STK_LYR_UP_XAXIS");	//야드적치BedX축(권하)
				iYD_STK_BED_YAXIS_UP = rsResult.getRecord(0).getFieldInt("YD_STK_LYR_UP_YAXIS");	//야드적치BedY축 (권하)
				iYD_STK_BED_XAXIS_DN = rsResult.getRecord(0).getFieldInt("YD_STK_LYR_DN_XAXIS");	//야드적치BedX축(권하)		
				iYD_STK_BED_YAXIS_DN = rsResult.getRecord(0).getFieldInt("YD_STK_LYR_DN_YAXIS");	//야드적치BedY축 (권하)
				
				iYD_STK_BED_ZAXIS_UP = rsResult.getRecord(0).getFieldInt("YD_STK_LYR_UP_ZAXIS");	//야드적치BedZ축 (권상)
				iYD_STK_BED_ZAXIS_DN = rsResult.getRecord(0).getFieldInt("YD_STK_LYR_DN_ZAXIS");	//야드적치BedZ축 (권하)
				
				sCAR_KIND = rsResult.getRecord(0).getFieldString("CAR_KIND");	//차량종류
				// X축 좌표를 CAR_FRONT_EDGE_XAXIS로 사용하게되면 다음과 같은 수식이 필요함
				if("Y".equals(rsResult.getRecord(0).getFieldString("UP_CARKIND_VS"))){
					double a = (dYD_MTL_MAX_W)/2;
					iYD_STK_BED_XAXIS_UP = iYD_STK_BED_XAXIS_UP + (int)Math.round(a);	
				}
				if("Y".equals(rsResult.getRecord(0).getFieldString("DN_CARKIND_VS"))){
					double a = (dYD_MTL_MAX_W)/2;
					iYD_STK_BED_XAXIS_DN = iYD_STK_BED_XAXIS_DN + (int)Math.round(a);
				}
				
				szLogMsg ="권상 X:: [YD_STK_BED_UP_XAXIS :: "+iYD_STK_BED_XAXIS_UP+"]" ;
				szLogMsg+="권상 Y:: [YD_STK_BED_UP_YAXIS :: "+iYD_STK_BED_YAXIS_UP+"]" ;
				szLogMsg+="권상 Z:: [YD_STK_BED_UP_ZAXIS :: "+iYD_STK_BED_ZAXIS_UP+"]" ;
				szLogMsg+="권하 X:: [YD_STK_BED_DN_XAXIS :: "+iYD_STK_BED_XAXIS_DN+"]" ;
				szLogMsg+="권하 Y:: [YD_STK_BED_DN_YAXIS :: "+iYD_STK_BED_YAXIS_DN+"]" ;
				szLogMsg+="권하 Z:: [YD_STK_BED_DN_ZAXIS :: "+iYD_STK_BED_ZAXIS_DN+"]" ; 
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
	  	  	    szLogMsg="["+szOperationName+":"+szMethodName+"] ==> 자동화크레인 좌표 종료 <==" ;
	  			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	  		}
	  		// 자동인데 형상정보가 없다면 + 유인크레인의 경우 Z축만 셋팅하자.
	  		else{
	  			
	  	  	    szLogMsg="["+szOperationName+":"+szMethodName+"] ==> 유인 <==" ;
	  			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	  			
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				params = JDTORecordFactory.getInstance().create();
		  		params.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
		  		params.setField("YD_UP_STK_COL_GP", szYD_UP_STK_COL_GP);
		  		params.setField("YD_UP_STK_BED_NO", szYD_UP_STK_BED_NO);
		  		params.setField("YD_DN_STK_COL_GP", szYD_DN_STK_COL_GP);
		  		params.setField("YD_DN_STK_BED_NO", szYD_DN_STK_BED_NO);
				if(commDao.select(params, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getCarPointFrmRsltByZ") > 0){
					iYD_STK_BED_ZAXIS_UP = rsResult.getRecord(0).getFieldInt("YD_STK_LYR_UP_ZAXIS");	//야드적치BedZ축 (권상)
					iYD_STK_BED_ZAXIS_DN = rsResult.getRecord(0).getFieldInt("YD_STK_LYR_DN_ZAXIS");	//야드적치BedZ축 (권하)
		  		}

				szLogMsg="권상 Z:: [YD_STK_BED_UP_ZAXIS :: "+iYD_STK_BED_ZAXIS_UP+"]" ;
				szLogMsg+="권하 Z:: [YD_STK_BED_DN_ZAXIS :: "+iYD_STK_BED_ZAXIS_DN+"]" ; 
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG); 
	  		}
		}
		

		// 2021. 10. 22 전사물류개선 자동화크레인대비 좌표계산 신규로직 수행
		// 자동화크레인 대상이면 별도 계산로직을 수행한다.
		boolean isAutoCrnGrabY1Y2 = false; // Grab1, 2 좌표계산 신규로직 여부(Y1, Y2)
		if(isAutoCrn){
			if(PlateGdsYdUtil.isApplyYn(szYD_EQP_ID+"좌표계산자동화신규여부")){
				isAutoCrnGrabY1Y2 = true;
			}
		}
		
		szLogMsg="isAutoCrn : "+isAutoCrn ; 
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG); 
		szLogMsg="isAutoCrnGrabY1Y2 : "+isAutoCrnGrabY1Y2 ; 
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG); 

		// AT000 2023.01.26 권상위치가 R/T인 경우 제품폭에 따라 폭구분 제 정의(이유:기존은 해당 bed의 폭구분으로 적용)                         
        if(isAutoCrn && "TBRTR".equals(szYD_UP_STK_COL_GP.substring(0, 5))){ 
            if (dYD_MTL_MAX_W < 2250){
                szYD_STK_BED_W_GP_UP = "S";
            }
            else if ((dYD_MTL_MAX_W >= 2250) || (dYD_MTL_MAX_W <= 3400)){
                szYD_STK_BED_W_GP_UP = "M";
            }
            else {
                szYD_STK_BED_W_GP_UP = "L";
            }
            szLogMsg="["+szOperationName+":"+szMethodName+"] ==> R/T 제품폭에 따라 폭구분 제 정의 <==" ;
     	    ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
            szLogMsg="dYD_MTL_MAX_W : " +dYD_MTL_MAX_W ; 
     	    ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
     	    szLogMsg="szYD_STK_BED_W_GP_UP : " +szYD_STK_BED_W_GP_UP ; 
        }
		//=============================================================================
		// 2. X 좌표 계산
		//-----------------------------------------------------------------------------
		// 2.1 권상위치 X좌표와  허용오차를 설정한다.
		recOutPara = JDTORecordFactory.getInstance().create();
		procXCalForPlateYd3G( szYD_UP_STK_COL_GP
							, iYD_STK_BED_XAXIS_UP
							, dYD_MTL_MAX_W
							, iYdStkBedXaxisTol
							, iYdCarWrkXaxisTol
							, iYdEqpWrkXaxisTol
							, szYD_STK_BED_W_GP_UP
							, szYD_STK_BED_WHIO_STAT_UP
							, recOutPara
							);
		
		szUp_Grab_X_Value	= recOutPara.getFieldString("CRANE_X_XAXIS");
		szCRANE_GAP_UP_X 	= recOutPara.getFieldString("CRANE_GAP_X");
		
		// 2.2 권하위치 X좌표와 허용오차를 설정한다.

		procXCalForPlateYd3G( szYD_DN_STK_COL_GP
							, iYD_STK_BED_XAXIS_DN
							, dYD_MTL_MAX_W
							, iYdStkBedXaxisTol
							, iYdCarWrkXaxisTol
							, iYdEqpWrkXaxisTol
							, szYD_STK_BED_W_GP_DN
							, szYD_STK_BED_WHIO_STAT_DN
							, recOutPara
							);
		
		szDn_Grab_X_Value	= recOutPara.getFieldString("CRANE_X_XAXIS");
		szCRANE_GAP_DN_X 	= recOutPara.getFieldString("CRANE_GAP_X");	
		
		// AT000 2022.11.22 권하위치가 RT인 경우 중앙 정렬로 계산(RT반납)	 2500 --> RT 폭(5M)의 절반							
		if(isAutoCrn && "RT".equals(szYD_DN_STK_COL_GP.substring(2, 4))){ 
		   szDn_Grab_X_Value = Integer.toString(iYD_STK_BED_XAXIS_DN + 2500);
		}
		
		// AT000 2023.01.30 R/T 소폭제 권하위치 X좌표값 보정(소폭제인 경우 중심좌표에서 (Grab길이(2250) - 제품폭)/2 한 값 더해 준다))                        
        if(isAutoCrn && "TBRTR".equals(szYD_UP_STK_COL_GP.substring(0, 5))){ 
            if (dYD_MTL_MAX_W < 2250){
            	szDn_Grab_X_Value = Integer.toString(Integer.parseInt(szDn_Grab_X_Value) + (int)Math.round(((2250 - dYD_MTL_MAX_W)/2)));
            }
           
            szLogMsg="["+szOperationName+":"+szMethodName+"] ==> RT 권하위치 보정--> 중심좌표에서 (Grab길이(2250) - 제품폭)/2 한 값 더해 준다) <==" ;
     	    ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
            szLogMsg="dYD_MTL_MAX_W : " + dYD_MTL_MAX_W ; 
     	    ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
     	    szLogMsg="szDn_Grab_X_Value : " + szDn_Grab_X_Value ; 
        }
		//=============================================================================
		// 3. Y 좌표 계산
		//-----------------------------------------------------------------------------
		if(iGrabType == 1) { 
			// 3.1 One Grab Crane
			
			szCrane_Grab_Use_Gp = "1"; //Grab#1 만 사용
			
			if("TBRTRC".equals(szYD_UP_STK_COL_GP) && "TF".equals(szYD_DN_STK_COL_GP.substring(2,4))) {
				// 3.1.1 Trans Bed Piling 작업일경우
				
				//	3.1.1.1 권상,권하위치 Y좌표와 허용오차를 설정한다.
				proc1GrabTbPilingYCalForPlateYd3G(szYD_UP_STK_COL_GP
												, szYD_UP_STK_BED_NO
												, szYD_DN_STK_COL_GP
												, szYD_DN_STK_BED_NO
												, iYD_STK_BED_YAXIS_UP
												, iYD_MTL_MAX_L
												, iYdStkBedYaxisTol
												, iYdCarWrkYaxisTol
												, iYdEqpWrkYaxisTol
												, szYD_EQP_ID
												, iGrab1BeamMinSize
												, iGrab1BeamMaxSize
												, recOutPara
												);		
				
				szUp_Grab_Y_Value 	= Integer.toString(iYD_STK_BED_YAXIS_UP);
				szUp_Grab_Y1_Value 	= recOutPara.getFieldString("CRANE_UP_Y1_YAXIS");
				szCRANE_GAP_UP_Y	= recOutPara.getFieldString("CRANE_UP_GAP_Y");
				
				szDn_Grab_Y_Value	= Integer.toString(iYD_STK_BED_YAXIS_DN);
				szDn_Grab_Y1_Value	= recOutPara.getFieldString("CRANE_DN_Y1_YAXIS");
				szCRANE_GAP_DN_Y	= recOutPara.getFieldString("CRANE_DN_GAP_Y");
				
			} else {
				// 3.1.2 그 외 작업일 경우 
				//	3.1.2.1 권상위치 Y좌표와 허용오차를 설정한다.
				proc1GrabYCalForPlateYd3G(szYD_UP_STK_COL_GP
										, szYD_UP_STK_BED_NO
										, iYD_STK_BED_YAXIS_UP
										, iYD_STK_BED_L_MAX_UP
										, iYD_MTL_MAX_L
										, iYdStkBedYaxisTol
										, iYdCarWrkYaxisTol
										, iYdEqpWrkYaxisTol
										, iGrab1BeamMaxSize
										, recOutPara
										);
				
				szUp_Grab_Y_Value 	= Integer.toString(iYD_STK_BED_YAXIS_UP);
				szUp_Grab_Y1_Value 	= recOutPara.getFieldString("CRANE_Y1_YAXIS");
				szCRANE_GAP_UP_Y	= recOutPara.getFieldString("CRANE_GAP_Y");
				
				//	3.1.2.2 권하위치 Y좌표와 허용오차를 설정한다.
				proc1GrabYCalForPlateYd3G(szYD_DN_STK_COL_GP
										, szYD_DN_STK_BED_NO
										, iYD_STK_BED_YAXIS_DN
										, iYD_STK_BED_L_MAX_DN
										, iYD_MTL_MAX_L
										, iYdStkBedYaxisTol
										, iYdCarWrkYaxisTol
										, iYdEqpWrkYaxisTol
										, iGrab1BeamMaxSize
										, recOutPara
										);		
				
				szDn_Grab_Y_Value	= Integer.toString(iYD_STK_BED_YAXIS_DN);
				szDn_Grab_Y1_Value	= recOutPara.getFieldString("CRANE_Y1_YAXIS");
				szCRANE_GAP_DN_Y	= recOutPara.getFieldString("CRANE_GAP_Y");				
			}
			
		} else {
			// 3.2 Two Grab Crane
			// 2021. 10. 22 전사물류개선 자동화크레인대비 좌표계산 신규로직 수행
			szLogMsg="isAutoCrnCarPoint : "+isAutoCrnCarPoint ; 
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG); 
			
			

			if(isAutoCrnGrabY1Y2){
				szLogMsg="["+szOperationName+":"+szMethodName+"] ==> 자동화크레인 Garb Y1, Y2 좌표생성 시작 <==" ;
	  			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord params = JDTORecordFactory.getInstance().create();
				// 좌표계산 공통 파라메터셋팅
				params.setField("YD_EQP_ID", szYD_EQP_ID);
				params.setField("YD_MTL_MAX_L", ""+iYD_MTL_MAX_L); // 크레인작업 재료중 제품최대길이
				params.setField("iYdStkBedYaxisTol", ""+iYdStkBedYaxisTol);
				params.setField("iYdCarWrkYaxisTol", ""+iYdCarWrkYaxisTol);
				params.setField("iYdEqpWrkYaxisTol", ""+iYdEqpWrkYaxisTol);
				if (isAutoCrnCarPoint == true)
				{// 형상 자동화 크레인일 경우  220517 박성열
					params.setField("ISAUTOCAR", sCAR_KIND);
				}
				else
				{// 형상 자동화 크레인이 아닐경우  220517 박성열
					params.setField("ISAUTOCAR", "");  //자동화 차량이 아니기 때문에, 값이 없다.
				}
				
				
				// 권상관련 파라메터정보 셋팅
		  		params.setField("YD_STK_COL_GP", szYD_UP_STK_COL_GP);
				params.setField("YD_STK_BED_NO", szYD_UP_STK_BED_NO);
				params.setField("YD_STK_BED_L_MAX",  ""+iYD_STK_BED_L_MAX_UP);
				params.setField("YD_OTHER_STK_COL_GP", szYD_DN_STK_COL_GP);
				params.setField("YD_OTHER_STK_BED_NO", szYD_DN_STK_BED_NO);
				params.setField("YD_STK_BED_YAXIS", ""+iYD_STK_BED_YAXIS_UP);
				
				
				szLogMsg="["+szOperationName+":"+szMethodName+"]"+params ;
	  			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
		  		if(commDao.select(params, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getAutoCrnGrabXYCal") > 0){
					szLogMsg="["+szOperationName+":"+szMethodName+"] ==> 자동화크레인 [권상] Garb Y1, Y2 좌표생성 계산결과 Set <==" ;
		  			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		  			try{
		  				//szUp_Grab_Y_Value 	= Integer.toString(iYD_STK_BED_YAXIS_UP);
		  				szUp_Grab_Y1_Value 	= rsResult.getRecord(0).getFieldString("CRANE_Y1_YAXIS");
		  				szUp_Grab_Y2_Value 	= rsResult.getRecord(0).getFieldString("CRANE_Y2_YAXIS");
		  				
		  			// 2022.0207 L2 조민주 부장 요청  Y축 좌표 값에 Y1,Y2축 좌표의 평균값 set 요청
		  			//	iYD_UP_YAXIS = (int)Math.round((Integer.parseInt(szUp_Grab_Y1_Value) + Integer.parseInt(szUp_Grab_Y2_Value)) / 2 );
		  				
		  				// 2022.0413 L2 조민주 부장 요청 Y축 좌표 값 평균값 안쓴다고 Y1값을 달라고 하였음
		  				iYD_UP_YAXIS = Integer.parseInt(szUp_Grab_Y1_Value);
		  				
		  				szUp_Grab_Y_Value 	= Integer.toString(iYD_UP_YAXIS);
		  				szCRANE_GAP_UP_Y	= rsResult.getRecord(0).getFieldString("CRANE_GAP_Y");
		  				szCrane_Grab_Use_Gp = rsResult.getRecord(0).getFieldString("CRANE_GRAB_USE_GP");
		  			}catch(Exception e){
		  				szLogMsg="["+szOperationName+":"+szMethodName+"] ==> 중요!! 자동화크레인 [권상] Garb Y1, Y2 좌표생성 중 오류 <=="+ e.getMessage() ;
		  				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		  			}
		  		}

		  		
		  		
		  	// 권하관련 파라메터정보 셋팅
		  		if (!"PT".equals(sCAR_KIND) && ("PT".equals(szYD_DN_STK_COL_GP.substring(2, 4)))) {
		  			if( PlateGdsYdUtil.isApplyYn("출하차량 Y 좌표 신규로직 적용 여부") ){
		  				outStlInfoRecSet1 = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		  				iYD_MAX_L = 0;
	  					recInPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
	  					if ( commDao.select(recInPara, outStlInfoRecSet1, "com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.getCarMaxLen")	> 0){
	  						iYD_MAX_L = outStlInfoRecSet1.getRecord(0).getFieldInt("YD_MTL_L");
	  						params.setField("YD_MAX_L", ""+iYD_MAX_L); // 크레인작업 재료중 제품최대길이
	  						szLogMsg="["+szOperationName+":"+szMethodName+"] ==> 자동화크레인 [권하] 크레인작업 재료중 제품최대길이 <=="+ iYD_MAX_L ;
	  			  			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	  					}
					}
			  	}
		  		params.setField("YD_STK_COL_GP", szYD_DN_STK_COL_GP);
				params.setField("YD_STK_BED_NO", szYD_DN_STK_BED_NO);
				params.setField("YD_STK_BED_L_MAX",  ""+iYD_STK_BED_L_MAX_DN);
				params.setField("YD_OTHER_STK_COL_GP", szYD_UP_STK_COL_GP);
				params.setField("YD_OTHER_STK_BED_NO", szYD_UP_STK_BED_NO);
				params.setField("YD_STK_BED_YAXIS", ""+iYD_STK_BED_YAXIS_DN);
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		  		if(commDao.select(params, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getAutoCrnGrabXYCal") > 0){
					szLogMsg="["+szOperationName+":"+szMethodName+"] ==> 자동화크레인 [권하] Garb Y1, Y2 좌표생성 계산결과 Set <==" ;
		  			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		  			try{
		  				//szDn_Grab_Y_Value	= Integer.toString(iYD_STK_BED_YAXIS_DN);
		  				szDn_Grab_Y1_Value	= rsResult.getRecord(0).getFieldString("CRANE_Y1_YAXIS");
		  				szDn_Grab_Y2_Value	= rsResult.getRecord(0).getFieldString("CRANE_Y2_YAXIS");
		  				// 2022.0207 L2 조민주 부장 요청  Y축 좌표 값에 Y1,Y2축 좌표의 평균값 set 요청
		  				//iYD_DN_YAXIS = (int)Math.round((Integer.parseInt(szDn_Grab_Y1_Value) +  Integer.parseInt(szDn_Grab_Y2_Value)) /2);
		  				
		  				// 2022.0413 L2 조민주 부장 요청 Y축 좌표 값 평균값 안쓴다고 Y1값을 달라고 하였음
		  				iYD_DN_YAXIS = Integer.parseInt(szDn_Grab_Y1_Value);
		  				
		  				szDn_Grab_Y_Value	= Integer.toString(iYD_DN_YAXIS);
		  				szCRANE_GAP_DN_Y	= rsResult.getRecord(0).getFieldString("CRANE_GAP_Y");
		  			}catch(Exception e){
		  				szLogMsg="["+szOperationName+":"+szMethodName+"] ==> 중요!! 자동화크레인 [권하] Garb Y1, Y2 좌표생성 중 오류 <=="+ e.getMessage() ;
		  				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		  			}
		  		} 
			}
			else
			{
				if("TBRTRC".equals(szYD_UP_STK_COL_GP) && "TF".equals(szYD_DN_STK_COL_GP.substring(2,4))) {
				
					// 3.2.1 Trans Bed Piling 작업일경우
					
					//	3.2.1.1 권상,권하위치 Y좌표와 허용오차를 설정한다.
					proc2GrabTbPilingYCalForPlateYd3G(szYD_UP_STK_COL_GP
													, szYD_UP_STK_BED_NO
													, szYD_DN_STK_COL_GP
													, szYD_DN_STK_BED_NO
													, iYD_STK_BED_YAXIS_UP
													, iYD_MTL_MAX_L
													, iYdStkBedYaxisTol
													, iYdCarWrkYaxisTol
													, iYdEqpWrkYaxisTol
													, szYD_EQP_ID
													, iGrab1BeamMinSize
													, iGrab1BeamMaxSize
													, iGrab2BeamMinSize
													, iGrab2BeamMaxSize
													, iBeamGap
													, recOutPara
													);		
					
					szUp_Grab_Y_Value 	= Integer.toString(iYD_STK_BED_YAXIS_UP);
					szUp_Grab_Y1_Value 	= recOutPara.getFieldString("CRANE_UP_Y1_YAXIS");
					szUp_Grab_Y2_Value 	= recOutPara.getFieldString("CRANE_UP_Y2_YAXIS");
					szCRANE_GAP_UP_Y	= recOutPara.getFieldString("CRANE_UP_GAP_Y");
					
					szDn_Grab_Y_Value	= Integer.toString(iYD_STK_BED_YAXIS_DN);
					szDn_Grab_Y1_Value	= recOutPara.getFieldString("CRANE_DN_Y1_YAXIS");
					szDn_Grab_Y2_Value	= recOutPara.getFieldString("CRANE_DN_Y2_YAXIS");
					szCRANE_GAP_DN_Y	= recOutPara.getFieldString("CRANE_DN_GAP_Y");	
					
					szCrane_Grab_Use_Gp = recOutPara.getFieldString("Crane_Grab_Use_Gp");	

				} else {
					// 3.2.2 그 외 작업일 경우 
					
					//	3.2.2.1 권상위치 Y1좌표,Y2좌표와 허용오차를 설정한다.
					proc2GrabYCalForPlateYd3G(szYD_UP_STK_COL_GP
											, szYD_UP_STK_BED_NO
											, szYD_DN_STK_COL_GP
											, szYD_DN_STK_BED_NO
											, iYD_STK_BED_YAXIS_UP
											, iYD_STK_BED_L_MAX_UP
											, iYD_MTL_MAX_L
											, iYdStkBedYaxisTol
											, iYdCarWrkYaxisTol
											, iYdEqpWrkYaxisTol
											, szYD_EQP_ID
											, iGrab1BeamMinSize
											, iGrab1BeamMaxSize
											, iGrab2BeamMinSize
											, iGrab2BeamMaxSize
											, iBeamGap
											, recOutPara
											);				
	
					szUp_Grab_Y_Value 	= Integer.toString(iYD_STK_BED_YAXIS_UP);
					szUp_Grab_Y1_Value 	= recOutPara.getFieldString("CRANE_Y1_YAXIS");
					szUp_Grab_Y2_Value 	= recOutPara.getFieldString("CRANE_Y2_YAXIS");
					szCRANE_GAP_UP_Y	= recOutPara.getFieldString("CRANE_GAP_Y");
					
					
					//	3.2.2.2 권하위치 Y1좌표,Y2좌표와 허용오차를 설정한다.				
					proc2GrabYCalForPlateYd3G(szYD_DN_STK_COL_GP
											, szYD_DN_STK_BED_NO
											, szYD_UP_STK_COL_GP
											, szYD_UP_STK_BED_NO
											, iYD_STK_BED_YAXIS_DN
											, iYD_STK_BED_L_MAX_DN
											, iYD_MTL_MAX_L
											, iYdStkBedYaxisTol
											, iYdCarWrkYaxisTol
											, iYdEqpWrkYaxisTol
											, szYD_EQP_ID
											, iGrab1BeamMinSize
											, iGrab1BeamMaxSize
											, iGrab2BeamMinSize
											, iGrab2BeamMaxSize
											, iBeamGap
											, recOutPara
											);		
					
					szDn_Grab_Y_Value	= Integer.toString(iYD_STK_BED_YAXIS_DN);
					szDn_Grab_Y1_Value	= recOutPara.getFieldString("CRANE_Y1_YAXIS");
					szDn_Grab_Y2_Value	= recOutPara.getFieldString("CRANE_Y2_YAXIS");
					szCRANE_GAP_DN_Y	= recOutPara.getFieldString("CRANE_GAP_Y");				
					
					szCrane_Grab_Use_Gp = recOutPara.getFieldString("Crane_Grab_Use_Gp");	
					
				}
			} // end of Grab계산
		}

		
		//=============================================================================
		// 4. 계산된 X,Y 좌표와 허용오차를 recResult 에 담아 호출자에게 반환 한다.
		//-----------------------------------------------------------------------------		
		
		
		recResult.setField("Crane_Grab_Use_Gp"	,szCrane_Grab_Use_Gp);

		recResult.setField("Up_Grab_X_Value"	,szUp_Grab_X_Value);
		recResult.setField("Up_Grab_Y_Value"	,szUp_Grab_Y_Value);
		recResult.setField("Up_Grab_Y1_Value"	,szUp_Grab_Y1_Value);
		recResult.setField("Up_Grab_Y2_Value"	,szUp_Grab_Y2_Value);

		recResult.setField("Dn_Grab_X_Value"	,szDn_Grab_X_Value);
		recResult.setField("Dn_Grab_Y_Value"	,szDn_Grab_Y_Value);
		recResult.setField("Dn_Grab_Y1_Value"	,szDn_Grab_Y1_Value);
		recResult.setField("Dn_Grab_Y2_Value"	,szDn_Grab_Y2_Value);		

		recResult.setField("CRANE_GAP_UP_X"		,szCRANE_GAP_UP_X);
		recResult.setField("CRANE_GAP_UP_Y"		,szCRANE_GAP_UP_Y);
		recResult.setField("CRANE_GAP_UP_Z"		,szCRANE_GAP_UP_X);
		
		recResult.setField("CRANE_GAP_DN_X"		,szCRANE_GAP_DN_X);
		recResult.setField("CRANE_GAP_DN_Y"		,szCRANE_GAP_DN_Y);
		recResult.setField("CRANE_GAP_DN_Z"		,szCRANE_GAP_UP_X);
		
		

		// 전사물류개선 계산된 자표를 별로로 담는다.
		// Y9YDL004에서 재계산 여부에 따라서 씌일 수 있음
		recResult.setField("YD_STK_BED_UP_XAXIS", szUp_Grab_X_Value);
		recResult.setField("YD_STK_BED_UP_YAXIS", szUp_Grab_Y_Value);
		recResult.setField("YD_STK_BED_UP_ZAXIS", Integer.toString(iYD_STK_BED_ZAXIS_UP));
		
		recResult.setField("YD_STK_BED_DN_XAXIS", szDn_Grab_X_Value);
		recResult.setField("YD_STK_BED_DN_YAXIS", szDn_Grab_Y_Value);
		recResult.setField("YD_STK_BED_DN_ZAXIS", Integer.toString(iYD_STK_BED_ZAXIS_DN));
		szLogMsg ="["+szOperationName+"] ::좌표계산결과:: " ;
		szLogMsg+="권상 X:: [YD_STK_BED_UP_XAXIS :: "+szUp_Grab_X_Value+"]" ;
		szLogMsg+="권상 Y:: [YD_STK_BED_UP_YAXIS :: "+szUp_Grab_Y_Value+"]" ;
		szLogMsg+="권상 Z:: [YD_STK_BED_UP_ZAXIS :: "+Integer.toString(iYD_STK_BED_ZAXIS_UP)+"]" ;
		szLogMsg+="권하 X:: [YD_STK_BED_DN_XAXIS :: "+szDn_Grab_X_Value+"]" ;
		szLogMsg+="권하 Y:: [YD_STK_BED_DN_YAXIS :: "+szDn_Grab_Y_Value+"]" ;
		szLogMsg+="권하 Z:: [YD_STK_BED_DN_ZAXIS :: "+Integer.toString(iYD_STK_BED_ZAXIS_DN)+"]" ; 
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		szLogMsg	= "["+szOperationName+"] -------------------------- OUT -------------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.displayRecord(szOperationName, recResult);
		szLogMsg	= "["+szOperationName+"] --------------------------------------------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return szRtnMsg;
    }    	
    
    /**
     * 후판제품 X축 값 구하기(3기)
     * @param recPara
     * @param recResult
     * @return
     * @throws JDTOException
     */    
    public static String procXCalForPlateYd3G( String szYD_STK_COL_GP 	/* 권상,권하 적치열 구분*/
    										 , int iYD_STK_BED_XAXIS	/* 권상,권하 적치BED X축 기준 값*/
    										 , double dYD_MTL_MAX_W		/* 제품 폭 */
    										 , int iYdStkBedXaxisTol	/* X좌표 허용차*/
    										 , int iYdCarWrkXaxisTol	/* 차량작업시 X좌표 허용차*/
    										 , int iYdEqpWrkXaxisTol	/* 설비작업시 X좌표 허용차*/
    										 , String szYD_STK_BED_W_GP /* 야드적치Bed폭구분 */
    										 , String szYD_STK_BED_WHIO_STAT /* 야드적치Bed상태구분 */
    										 , JDTORecord recResult
    						                 ) throws JDTOException {
    	
    	YdPlateCommDAO commDao = new YdPlateCommDAO();
    	
    	String szRtnMsg				= YdConstant.RETN_CD_SUCCESS;
    	String szLogMsg 			= ""; 
    	String szMethodName			= "procXCalForPlateYd3G";
		String szOperationName		= "후판제품 X축 값 구하기(3기)";
		int intRtnVal				= -100;
		
		String	szCRANE_X_XAXIS	= null;
		String  szCRANE_GAP_X		= null;
		
		if("G".equals(szYD_STK_BED_WHIO_STAT)){			// 가적장
			
			if("S".equals(szYD_STK_BED_W_GP)){			// 협폭
    			szCRANE_X_XAXIS = Integer.toString(iYD_STK_BED_XAXIS + 1050);
    		}else if("L".equals(szYD_STK_BED_W_GP)){	// 광폭
    			szCRANE_X_XAXIS = Integer.toString(iYD_STK_BED_XAXIS + 2400);
    		}else{										// 중폭
    			szCRANE_X_XAXIS = Integer.toString(iYD_STK_BED_XAXIS + 1700);
    		}
    		
		}else if("PT".equals(szYD_STK_COL_GP.substring(2,4))) { 
			//차량 상,하차시
			
			// X축 좌표값 : TB_YD_STKBED 에서 읽은 야드적치BedX축 값을 그대로 사용한다.
			szCRANE_X_XAXIS = Integer.toString(iYD_STK_BED_XAXIS);
			 
		} else if(
				  ("A".equals(szYD_STK_COL_GP.substring(1, 2))&&"TF".equals(szYD_STK_COL_GP.substring(2, 4))&&
				   ("01".equals(szYD_STK_COL_GP.substring(4, 6))||"02".equals(szYD_STK_COL_GP.substring(4, 6))||"03".equals(szYD_STK_COL_GP.substring(4, 6))||"04".equals(szYD_STK_COL_GP.substring(4, 6)))
				  )
				  ||
				  ("B".equals(szYD_STK_COL_GP.substring(1, 2))&&"TF".equals(szYD_STK_COL_GP.substring(2, 4))&&
				   ("08".equals(szYD_STK_COL_GP.substring(4, 6))||"09".equals(szYD_STK_COL_GP.substring(4, 6))||"10".equals(szYD_STK_COL_GP.substring(4, 6))||"11".equals(szYD_STK_COL_GP.substring(4, 6)))
				  )
				) {
			// X축 좌표값 : BED X 기준 좌표 값  - (제품 폭/2)
			szCRANE_X_XAXIS = Integer.toString(iYD_STK_BED_XAXIS - (int)Math.round((dYD_MTL_MAX_W / 2)));
		
		} else if("01".equals(szYD_STK_COL_GP.substring(2, 4))||"02".equals(szYD_STK_COL_GP.substring(2, 4))||"03".equals(szYD_STK_COL_GP.substring(2, 4))){
			
			// 2후판 일반야드일경우  X축 좌표값 : BED X 기준 좌표 값  + BED폭/2
			if("S".equals(szYD_STK_BED_W_GP)){			// 협폭
    			szCRANE_X_XAXIS = Integer.toString(iYD_STK_BED_XAXIS + 1050);
    		}else if("L".equals(szYD_STK_BED_W_GP)){	// 광폭
    			szCRANE_X_XAXIS = Integer.toString(iYD_STK_BED_XAXIS + 2400);
    		}else{										// 중폭
    			szCRANE_X_XAXIS = Integer.toString(iYD_STK_BED_XAXIS + 1700);
    		}
 
		} else {
			
			String sIsOk = "N";
			
			JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	  		JDTORecord params = JDTORecordFactory.getInstance().create();
	  		params.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
	  		
			if(commDao.select(params, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0139") > 0){
	  			
	  			sIsOk = rsResult.getRecord(0).getFieldString("IS_OK");
	  			
	  			szLogMsg="["+szOperationName+":"+szMethodName+"] ==> 1후판 좌표계산식 중앙정렬방식으로 변경처리 가능여부 판단 <=="+szYD_STK_COL_GP+"=>"+sIsOk ;
	  			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	  			
	  		}
			
			if("Y".equals(sIsOk)){
				// 중앙 정렬
				if("S".equals(szYD_STK_BED_W_GP)){			// 협폭
	    			szCRANE_X_XAXIS = Integer.toString(iYD_STK_BED_XAXIS + 1050);
	    		}else if("L".equals(szYD_STK_BED_W_GP)){	// 광폭
	    			szCRANE_X_XAXIS = Integer.toString(iYD_STK_BED_XAXIS + 2400);
	    		}else{										// 중폭
	    			szCRANE_X_XAXIS = Integer.toString(iYD_STK_BED_XAXIS + 1700);
	    		}
			}else{
				// 측면 정렬 
				if("S".equals(szYD_STK_BED_W_GP)){			// 협폭
	    			// X축 좌표값 : BED X 기준 좌표 값 + (제품 폭/2)
	    			szCRANE_X_XAXIS = Integer.toString(iYD_STK_BED_XAXIS + 1250);
	    		}else{										// 그외
	    			// X축 좌표값 : BED X 기준 좌표 값 + (제품 폭/2)
	    			szCRANE_X_XAXIS = Integer.toString(iYD_STK_BED_XAXIS + (int)Math.round((dYD_MTL_MAX_W / 2)));
	    		}
			}
		}
		
		if("RT".equals(szYD_STK_COL_GP.substring(2,4)) || "TF".equals(szYD_STK_COL_GP.substring(2,4))) {
			// 허용 오차 :  BRE 에서 읽어온 설비작업 X축 허용오차를 사용
			szCRANE_GAP_X = Integer.toString(iYdEqpWrkXaxisTol);
		}else if("PT".equals(szYD_STK_COL_GP.substring(2,4))) { 
			// 허용 오차 :  BRE 에서 읽어온 차량작업 X축 허용오차를 사용
			szCRANE_GAP_X = Integer.toString(iYdCarWrkXaxisTol);
		} else {
			// 허용 오차 :  BRE 에서 읽어온  X축 허용오차를 사용
			szCRANE_GAP_X = Integer.toString(iYdStkBedXaxisTol);
		}
		
		//결과 값 셋팅
		recResult.setField("CRANE_X_XAXIS", szCRANE_X_XAXIS);
		recResult.setField("CRANE_GAP_X", szCRANE_GAP_X);
		
		return szRtnMsg;
    }
    
    /**
     * 후판제품 OneGrab Crane Trans Bed Piling Y축 값 구하기(3기)
     * @param recPara
     * @param recResult
     * @return
     * @throws JDTOException
     */    
    public static String proc1GrabTbPilingYCalForPlateYd3G(   String szYD_UP_STK_COL_GP /* 권상 적치열 구분 */
    														, String szYD_UP_STK_BED_NO	/* 권상 적치Bed번호 */
    														, String szYD_DN_STK_COL_GP	/* 권하 적치Bed번호 */
    														, String szYD_DN_STK_BED_NO	/* 권하 적치Bed번호 */
    														, int iYD_STK_BED_YAXIS_UP	/* 권상 적치BED Y축 기준 값 */
    														, int iYD_MTL_MAX_L			/* 제품 길이 */
    														, int iYdStkBedYaxisTol		/* Y좌표 허용차 */
    														, int iYdCarWrkYaxisTol		/* 차량작업시 Y좌표 허용차 */
    														, int iYdEqpWrkYaxisTol		/* 설비작업시 Y좌표 허용차 */
    														, String szYD_EQP_ID		/* 크래인 설비 ID*/
    														, int iGrab1BeamMinSize		/* Grab#1 Beam Min Size */
    														, int iGrab1BeamMaxSize		/* Grab#1 Beam Max Size */
    														, JDTORecord recResult
    											  		  ) throws JDTOException {
	
    	String szRtnMsg				= YdConstant.RETN_CD_SUCCESS;
    	String szLogMsg 			= ""; 
    	String szMethodName			= "proc1GrabTbPilingYCalForPlateYd3G";
		String szOperationName		= "후판제품 OneGrab Crane Trans Bed Piling Y축 값 구하기(3기))";
		int intRtnVal				= -100;
		
		//String	szCRANE_UP_Y_YAXIS	= null;
		String	szCRANE_UP_Y1_YAXIS	= null;
		String  szCRANE_UP_GAP_Y	= null;
		
		//String	szCRANE_DN_Y_YAXIS	= null;
		String	szCRANE_DN_Y1_YAXIS	= null;		
		String  szCRANE_DN_GAP_Y	= null;		
		
		
		if((iYD_MTL_MAX_L < iGrab1BeamMinSize) && "20".equals(szYD_UP_STK_BED_NO)) {
			// 제품 최대길이 < Grab #1 Beam Min Size 이고 From Bed 가 20 이면..
			szCRANE_UP_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS_UP + (int)Math.round((iGrab1BeamMinSize / 2)));
			szCRANE_DN_Y1_YAXIS = szCRANE_UP_Y1_YAXIS;
			
		} else if ((iYD_MTL_MAX_L < iGrab1BeamMinSize) && "10".equals(szYD_UP_STK_BED_NO)) {
			// 제품 최대길이 < Grab #1 Beam Min Size 이고 From Bed 가 10 이면..
			szCRANE_UP_Y1_YAXIS = Integer.toString((iYD_STK_BED_YAXIS_UP + iYD_MTL_MAX_L) - (int)Math.round((iGrab1BeamMinSize / 2)));
			szCRANE_DN_Y1_YAXIS = szCRANE_UP_Y1_YAXIS;
			
		} else if ((iYD_MTL_MAX_L < iGrab1BeamMaxSize) && "20".equals(szYD_UP_STK_BED_NO)) {
			// 제품 최대길이 < Grab #1 Beam Max Size 이고 From Bed 가 20 이면..
			szCRANE_UP_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS_UP + (int)Math.round((iGrab1BeamMaxSize / 2)));
			szCRANE_DN_Y1_YAXIS = szCRANE_UP_Y1_YAXIS;
			
		} else if ((iYD_MTL_MAX_L < iGrab1BeamMaxSize) && "10".equals(szYD_UP_STK_BED_NO)) {
			// 제품 최대길이 < Grab #1 Beam Max Size 이고 From Bed 가 10 이면..
			szCRANE_UP_Y1_YAXIS = Integer.toString((iYD_STK_BED_YAXIS_UP + iYD_MTL_MAX_L) - (int)Math.round((iGrab1BeamMaxSize / 2)));
			szCRANE_DN_Y1_YAXIS = szCRANE_UP_Y1_YAXIS;
			
		} else {
			// 제품 최대길이 >= Grab #1 Beam Max Size 이면 ..
			szCRANE_UP_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS_UP + (int)Math.round((iYD_MTL_MAX_L / 2)));
			szCRANE_DN_Y1_YAXIS = szCRANE_UP_Y1_YAXIS;
			
		}
		//szCRANE_UP_Y_YAXIS = szCRANE_UP_Y1_YAXIS;
		//szCRANE_DN_Y_YAXIS = szCRANE_DN_Y1_YAXIS;
		
		// 허용 오차 
		szCRANE_UP_GAP_Y = Integer.toString( iYdStkBedYaxisTol );
		szCRANE_DN_GAP_Y = Integer.toString( iYdStkBedYaxisTol );
		
		
		//결과 값 셋팅
		//recResult.setField("CRANE_UP_Y_YAXIS"	, szCRANE_UP_Y_YAXIS);
		recResult.setField("CRANE_UP_Y1_YAXIS"	, szCRANE_UP_Y1_YAXIS);
		recResult.setField("CRANE_UP_GAP_Y"		, szCRANE_UP_GAP_Y);

		//recResult.setField("CRANE_DN_Y_YAXIS"	, szCRANE_DN_Y_YAXIS);
		recResult.setField("CRANE_DN_Y1_YAXIS"	, szCRANE_DN_Y1_YAXIS);
		recResult.setField("CRANE_DN_GAP_Y"		, szCRANE_DN_GAP_Y);

		
		return szRtnMsg;
    }    
    
    /**
     * 후판제품 One Grab Crane Y축 값 구하기(3기)
     * @param recPara
     * @param recResult
     * @return
     * @throws JDTOException
     */    
    public static String proc1GrabYCalForPlateYd3G(   String szYD_STK_COL_GP 	/* 권상,권하 적치열 구분 */
    												, String szYD_STK_BED_NO	/* 권상,권하 적치Bed번호 */
    												, int iYD_STK_BED_YAXIS		/* 권상,권하 적치BED Y축 기준 값 */
    												, int iYD_STK_BED_L_MAX     /* 권상,권하 적치BED 길이 */
    												, int iYD_MTL_MAX_L			/* 제품 길이 */
    												, int iYdStkBedYaxisTol		/* Y좌표 허용차 */
    												, int iYdCarWrkYaxisTol		/* 차량작업시 Y좌표 허용차 */
    												, int iYdEqpWrkYaxisTol		/* 설비작업시 Y좌표 허용차 */
    												, int iGrab1BeamMaxSize		/* Grab#1 Beam Max Size */	
    												, JDTORecord recResult
    											  ) throws JDTOException {
    	
    	String szRtnMsg				= YdConstant.RETN_CD_SUCCESS;
    	String szLogMsg 			= ""; 
    	String szMethodName			= "proc1GrabYCalForPlateYd3G";
		String szOperationName		= "후판제품 One Grab Crane Y축 값 구하기(3기)";
		int intRtnVal				= -100;
		
		//String	szCRANE_Y_YAXIS		= null;
		String	szCRANE_Y1_YAXIS	= null;
		String  szCRANE_GAP_Y		= null;
		
		if("RT".equals(szYD_STK_COL_GP.substring(2,4))) {
			//Book-In/Out 작업 (입고, 동간이적, RT반납)시 RT 상의 Y 좌표
			if("RD".equals(szYD_STK_COL_GP.substring(4,6))||
			   "RE".equals(szYD_STK_COL_GP.substring(4,6))|| 
			   "RF".equals(szYD_STK_COL_GP.substring(4,6))){
				
				if(("C".equals(szYD_STK_COL_GP.substring(1,2)))&&(iYD_MTL_MAX_L > 14000 && iYD_MTL_MAX_L <= 18600)){
					szCRANE_Y1_YAXIS = Integer.toString( iYD_STK_BED_YAXIS - 7000 + (int)Math.round((iYD_MTL_MAX_L / 2)));
				 
				}else if(("D".equals(szYD_STK_COL_GP.substring(1,2)))&&(iYD_MTL_MAX_L > 9200 && iYD_MTL_MAX_L <= 14000)){
					if("01".equals(szYD_STK_BED_NO)||"80".equals(szYD_STK_BED_NO)){
						szCRANE_Y1_YAXIS = Integer.toString( iYD_STK_BED_YAXIS + 2400);
					}else{
						szCRANE_Y1_YAXIS = Integer.toString( iYD_STK_BED_YAXIS - 2400);
					}
				}else if(("E".equals(szYD_STK_COL_GP.substring(1,2)))&&(iYD_MTL_MAX_L > 6800 && iYD_MTL_MAX_L <= 9200)){
					szCRANE_Y1_YAXIS = Integer.toString( iYD_STK_BED_YAXIS - 3400 + (int)Math.round((iYD_MTL_MAX_L / 2)));	
				
				}else{
					szCRANE_Y1_YAXIS = Integer.toString( iYD_STK_BED_YAXIS );
				}
			}else{
				szCRANE_Y1_YAXIS = Integer.toString( iYD_STK_BED_YAXIS + (int)Math.round((iYD_MTL_MAX_L /2)) );
			}
		} else if("PT".equals(szYD_STK_COL_GP.substring(2,4))) { 
			//차량 상,하차시
			szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS);
			
		} else {
			// 그 외  
			szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round((iYD_STK_BED_L_MAX / 2)));
		}
		//szCRANE_Y_YAXIS = szCRANE_Y1_YAXIS;
		
		if("RT".equals(szYD_STK_COL_GP.substring(2,4)) || "TF".equals(szYD_STK_COL_GP.substring(2,4))) {
			// 허용 오차 :  BRE 에서 읽어온 설비작업 X축 허용오차를 사용
			szCRANE_GAP_Y = Integer.toString(iYdEqpWrkYaxisTol);
		}else if("PT".equals(szYD_STK_COL_GP.substring(2,4))) { 
			// 허용 오차 :  BRE 에서 읽어온 차량작업 Y축 허용오차를 사용
			szCRANE_GAP_Y = Integer.toString(iYdCarWrkYaxisTol);
		} else {
			// 허용 오차 :  BRE 에서 읽어온  X축 허용오차를 사용
			szCRANE_GAP_Y = Integer.toString(iYdStkBedYaxisTol);
		}
		
		//결과 값 셋팅
		//recResult.setField("CRANE_Y_YAXIS"	, szCRANE_Y_YAXIS);
		recResult.setField("CRANE_Y1_YAXIS"	, szCRANE_Y1_YAXIS);
		recResult.setField("CRANE_GAP_Y"	, szCRANE_GAP_Y);
		
		return szRtnMsg;
    }    
    
    /**
     * 후판제품 Two Grab Crane Trans Bed Piling Y축 값 구하기(3기)
     * @param recPara
     * @param recResult
     * @return
     * @throws JDTOException
     */    
    public static String proc2GrabTbPilingYCalForPlateYd3G(   String szYD_UP_STK_COL_GP /* 권상 적치열 구분 */
    														, String szYD_UP_STK_BED_NO	/* 권상 적치Bed번호 */
    														, String szYD_DN_STK_COL_GP	/* 권하 적치Bed번호 */
    														, String szYD_DN_STK_BED_NO	/* 권하 적치Bed번호 */
    														, int iYD_STK_BED_YAXIS_UP	/* 권상 적치BED Y축 기준 값 */
    														, int iYD_MTL_MAX_L			/* 제품 길이 */
    														, int iYdStkBedYaxisTol		/* Y좌표 허용차 */
    														, int iYdCarWrkYaxisTol		/* 차량작업시 Y좌표 허용차 */
    														, int iYdEqpWrkYaxisTol		/* 설비작업시 Y좌표 허용차 */
    														, String szYD_EQP_ID		/* 크래인 설비 ID*/
    														, int iGrab1BeamMinSize		/* Grab#1 Beam Min Size */
    														, int iGrab1BeamMaxSize		/* Grab#1 Beam Max Size */
    														, int iGrab2BeamMinSize		/* Grab#2 Beam Min Size */
    														, int iGrab2BeamMaxSize		/* Grab#2 Beam Max Size */
    														, int iBeamGap				/* Beam 간격 */
    														, JDTORecord recResult
    											  		  ) throws JDTOException {
	
    	String szRtnMsg				= YdConstant.RETN_CD_SUCCESS;
    	String szLogMsg 			= ""; 
    	String szMethodName			= "proc2GrabTbPilingYCalForPlateYd3G";
		String szOperationName		= "후판제품 Two Grab Crane Trans Bed Piling Y축 값 구하기(3기))";
		int intRtnVal				= -100;
		
		//String	szCRANE_UP_Y_YAXIS	= null;
		String	szCRANE_UP_Y1_YAXIS	= null;
		String	szCRANE_UP_Y2_YAXIS	= null;
		String  szCRANE_UP_GAP_Y	= null;
		
		//String	szCRANE_DN_Y_YAXIS	= null;
		String	szCRANE_DN_Y1_YAXIS	= null;		
		String	szCRANE_DN_Y2_YAXIS	= null;		
		String  szCRANE_DN_GAP_Y	= null;		
		
		String szCrane_Grab_Use_Gp = null;  // Grab 사용구분 (1:Grab#1 만 사용 , 2:Grab#2 만 사용 , 3: Grab#1,#2 모두 사용)
		
		
		if((iYD_MTL_MAX_L < 10400) ) {
			// 제품 최대길이 < 10,400
			if("10".equals(szYD_UP_STK_BED_NO)) {
				//  From Bed 가 10 이면..
				szCrane_Grab_Use_Gp = "2"; //Grab#2 만 사용
				szCRANE_UP_Y1_YAXIS = "0";
				szCRANE_UP_Y2_YAXIS = Integer.toString(iYD_STK_BED_YAXIS_UP + (int)Math.round((iGrab2BeamMinSize / 2)));
				szCRANE_DN_Y1_YAXIS = szCRANE_UP_Y1_YAXIS;
				szCRANE_DN_Y2_YAXIS = szCRANE_UP_Y2_YAXIS;
			} else {
				//  From Bed 가 20 이면..
				szCrane_Grab_Use_Gp = "1"; //Grab#1 만 사용
				szCRANE_UP_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS_UP + (int)Math.round((iGrab1BeamMinSize / 2)));
				szCRANE_UP_Y2_YAXIS = "0";
				szCRANE_DN_Y1_YAXIS = szCRANE_UP_Y1_YAXIS;
				szCRANE_DN_Y2_YAXIS = szCRANE_UP_Y2_YAXIS;
			}
			
		} else if ((iYD_MTL_MAX_L < 13001)) {
			// 제품 최대길이 < 13,001
			if( "10".equals(szYD_UP_STK_BED_NO)){
				//  From Bed 가 10 이면..
				szCrane_Grab_Use_Gp = "2"; //Grab#2 만 사용
				szCRANE_UP_Y1_YAXIS = "0";
				szCRANE_UP_Y2_YAXIS = Integer.toString(iYD_STK_BED_YAXIS_UP + (int)Math.round((iYD_MTL_MAX_L / 2)));
				szCRANE_DN_Y1_YAXIS = szCRANE_UP_Y1_YAXIS;
				szCRANE_DN_Y2_YAXIS = szCRANE_UP_Y2_YAXIS;
			} else {
				//  From Bed 가 20 이면..
				szCrane_Grab_Use_Gp = "1"; //Grab#1 만 사용
				szCRANE_UP_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS_UP + (int)Math.round((iYD_MTL_MAX_L / 2)));
				szCRANE_UP_Y2_YAXIS = "0";
				szCRANE_DN_Y1_YAXIS = szCRANE_UP_Y1_YAXIS;
				szCRANE_DN_Y2_YAXIS = szCRANE_UP_Y2_YAXIS;
			}
			
		} else if ((iYD_MTL_MAX_L <= 22200)) {
			// 제품 최대길이 <= 22,200			
			szCrane_Grab_Use_Gp = "3"; //Grab#1, Grab#2  모두 사용
			szCRANE_UP_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS_UP + (int)Math.round((iGrab1BeamMinSize / 2)));
			szCRANE_UP_Y2_YAXIS = Integer.toString(iYD_STK_BED_YAXIS_UP + iGrab1BeamMinSize + iBeamGap + (int)Math.round((iGrab2BeamMinSize / 2)));
			szCRANE_DN_Y1_YAXIS = szCRANE_UP_Y1_YAXIS;
			szCRANE_DN_Y2_YAXIS = szCRANE_UP_Y2_YAXIS;			
			
		} else {
			// 제품 최대길이 > 22,000 이면 ..
			double dConstant = 0.0;
			int iBeamAlpha = 0;
			
			if(iGrab2BeamMaxSize <= 8800) {
				dConstant = 2.75;
			} else {
				dConstant = 1.75;
			}
			
			iBeamAlpha = (int)Math.round((iYD_MTL_MAX_L - (iGrab1BeamMinSize + iBeamGap + iGrab2BeamMaxSize))/dConstant) + iBeamGap;
			
			szCrane_Grab_Use_Gp = "3"; //Grab#1, Grab#2  모두 사용
			szCRANE_UP_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS_UP + (int)Math.round((iYD_MTL_MAX_L / 2)) - (int)Math.round((iBeamAlpha / 2)) - (int)Math.round((iGrab1BeamMinSize / 2)) );
			szCRANE_UP_Y2_YAXIS = Integer.toString(iYD_STK_BED_YAXIS_UP + (int)Math.round((iYD_MTL_MAX_L / 2)) + (int)Math.round((iBeamAlpha / 2)) + (int)Math.round((iGrab2BeamMaxSize / 2)) );
			szCRANE_DN_Y1_YAXIS = szCRANE_UP_Y1_YAXIS;
			
		}
		//szCRANE_UP_Y_YAXIS = szCRANE_UP_Y1_YAXIS;
		//szCRANE_DN_Y_YAXIS = szCRANE_DN_Y1_YAXIS;
		
		// 허용 오차 
		szCRANE_UP_GAP_Y = Integer.toString( iYdStkBedYaxisTol );
		szCRANE_DN_GAP_Y = Integer.toString( iYdStkBedYaxisTol );
		
		
		//결과 값 셋팅
		//recResult.setField("CRANE_UP_Y_YAXIS"	, szCRANE_UP_Y_YAXIS);
		recResult.setField("CRANE_UP_Y1_YAXIS"	, szCRANE_UP_Y1_YAXIS);
		recResult.setField("CRANE_UP_Y2_YAXIS"	, szCRANE_UP_Y2_YAXIS);
		recResult.setField("CRANE_UP_GAP_Y"		, szCRANE_UP_GAP_Y);

		//recResult.setField("CRANE_DN_Y_YAXIS"	, szCRANE_DN_Y_YAXIS);
		recResult.setField("CRANE_DN_Y1_YAXIS"	, szCRANE_DN_Y1_YAXIS);
		recResult.setField("CRANE_DN_Y2_YAXIS"	, szCRANE_DN_Y2_YAXIS);
		recResult.setField("CRANE_DN_GAP_Y"		, szCRANE_DN_GAP_Y);
		
		recResult.setField("Crane_Grab_Use_Gp"	,szCrane_Grab_Use_Gp);
		
		return szRtnMsg;
    }        
    
    
    /**
     * 후판제품 Two Grab Crane Y축 값 구하기(3기)
     * @param recPara
     * @param recResult
     * @return
     * @throws JDTOException
     */    
    public static String proc2GrabYCalForPlateYd3G(   String szYD_STK_COL_GP 	/* 권상,권하 적치열 구분 */
    												, String szYD_STK_BED_NO	/* 권상,권하 적치Bed번호 */
    												, String szYD_THE_OTHER_COL_GP /* 권상일경운 권하 적치열 구분, 권하일경우 권상 적치열 구분 */
    												, String szYD_THE_OTHER_BED_NO /* 권상일경운 권하 Bed번호, 권하일경우 권상 Bed번호 */
    												, int iYD_STK_BED_YAXIS		/* 권상,권하 적치BED Y축 기준 값 */
    												, int iYD_STK_BED_L_MAX     /* 권상,권하 적치BED 길이 */
    												, int iYD_MTL_MAX_L			/* 제품 길이 */
    												, int iYdStkBedYaxisTol		/* Y좌표 허용차 */
    												, int iYdCarWrkYaxisTol		/* 차량작업시 Y좌표 허용차 */
    												, int iYdEqpWrkYaxisTol		/* 설비작업시 Y좌표 허용차 */
													, String szYD_EQP_ID		/* 크래인 설비 ID*/
													, int iGrab1BeamMinSize		/* Grab#1 Beam Min Size */
													, int iGrab1BeamMaxSize		/* Grab#1 Beam Max Size */
													, int iGrab2BeamMinSize		/* Grab#2 Beam Min Size */
													, int iGrab2BeamMaxSize		/* Grab#2 Beam Max Size */
													, int iBeamGap				/* Beam 간격 */

    												, JDTORecord recResult
    											  ) throws JDTOException {
    	
    	String szRtnMsg				= YdConstant.RETN_CD_SUCCESS;
    	String szLogMsg 			= ""; 
    	String szMethodName			= "proc2GrabYCalForPlateYd3G";
		String szOperationName		= "후판제품 Two Grab Crane Y축 값 구하기(3기)";
		int intRtnVal				= -100;
		
		//String	szCRANE_Y_YAXIS		= null;
		String	szCRANE_Y1_YAXIS	= null;
		String	szCRANE_Y2_YAXIS	= null;
		String  szCRANE_GAP_Y		= null;
		
		String szCrane_Grab_Use_Gp = null;  // Grab 사용구분 (1:Grab#1 만 사용 , 2:Grab#2 만 사용 , 3: Grab#1,#2 모두 사용)
		
		
		if("RT".equals(szYD_STK_COL_GP.substring(2,4)) || "TF".equals(szYD_STK_COL_GP.substring(2,4))) {
			//Book-In/Out 작업 (입고, 동간이적, RT반납)시 RT, TF 상의 Y 좌표
			
			if(("TBCRB3".equals(szYD_EQP_ID)||"TBCRB4".equals(szYD_EQP_ID))&&(iYD_MTL_MAX_L < 12000)) {
				// 제품최대길이 < 12,000 
				if("01".equals(szYD_THE_OTHER_BED_NO) || "02".equals(szYD_THE_OTHER_BED_NO)) {
					//01,02 Bed 관련 작업
					szCrane_Grab_Use_Gp = "1"; //Grab#1 만 사용
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS);
					szCRANE_Y2_YAXIS = "0";					
				} else {
					// 그외 Bed 작업
					szCrane_Grab_Use_Gp = "2"; //Grab#2 만 사용
					szCRANE_Y1_YAXIS = "0";
					szCRANE_Y2_YAXIS = Integer.toString(iYD_STK_BED_YAXIS);
				}
			}else if(("TBCRB1".equals(szYD_EQP_ID)||"TCCRC1".equals(szYD_EQP_ID)||"TCCRC2".equals(szYD_EQP_ID))&&(iYD_MTL_MAX_L < 12000)) {
				// 제품최대길이 < 12,000 
				if("01".equals(szYD_THE_OTHER_BED_NO) || "02".equals(szYD_THE_OTHER_BED_NO)) {
					//01,02 Bed 관련 작업
					szCrane_Grab_Use_Gp = "1"; //Grab#1 만 사용
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round((iYD_MTL_MAX_L / 2)));
					szCRANE_Y2_YAXIS = "0";					
				} else {
					// 그외 Bed 작업
					szCrane_Grab_Use_Gp = "2"; //Grab#2 만 사용
					szCRANE_Y1_YAXIS = "0";
					szCRANE_Y2_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round((iYD_MTL_MAX_L / 2)));
				}
			}else if( "TBCRB1".equals(szYD_EQP_ID) ) {  
				// 2후판 B1 크레인 일 경우
				szCrane_Grab_Use_Gp = "3"; //Grab#1, Grab#2  모두 사용
				
				if (iYD_MTL_MAX_L <= 22200) {
					// 제품 최대길이 <= 22,200	 
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round((iYD_MTL_MAX_L / 2)) - (int)Math.round((iBeamGap + iGrab1BeamMinSize)/2));
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamGap) + (int)Math.round((iGrab1BeamMinSize + iGrab2BeamMinSize)/2));
				} else {
					// 제품 최대길이 > 22,200	 
					double dConstant = 0.0;
					int iBeamAlpha = 0;
					
					if(iGrab2BeamMaxSize <= 8800) {
						dConstant = 2.75;
					} else {
						dConstant = 1.75;
					}
					
					iBeamAlpha = (int)Math.round((iYD_MTL_MAX_L - (iGrab1BeamMaxSize + iBeamGap + iGrab2BeamMaxSize))/dConstant) + iBeamGap;
					
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round((iYD_MTL_MAX_L / 2)) - (int)Math.round((iBeamAlpha + iGrab1BeamMaxSize )/ 2)  );
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamAlpha) + (int)Math.round((iGrab1BeamMaxSize + iGrab2BeamMaxSize)/2));
				}
			} else if("TBCRB3".equals(szYD_EQP_ID)||"TBCRB4".equals(szYD_EQP_ID)){

				szCrane_Grab_Use_Gp = "3"; //Grab#1, Grab#2  모두 사용
				
				if (iYD_MTL_MAX_L <= 18600) {
					// 제품 최대길이 <= 18,600	 
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS - (int)Math.round((iYD_MTL_MAX_L / 2)) + (int)Math.round((iGrab1BeamMinSize)/2));
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamGap) + (int)Math.round((iGrab1BeamMinSize + iGrab2BeamMinSize)/2));

				} else if (iYD_MTL_MAX_L <= 20600) {
					// 제품 최대길이 <= 20,600	 
					//#2 Beam 확장 Size
					int i2BeamExpSize = iYD_MTL_MAX_L - (iBeamGap + iGrab1BeamMaxSize);
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + 3200 - (int)Math.round(iYD_MTL_MAX_L/2) + (int)Math.round((iGrab1BeamMaxSize)/2)); 
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamGap) + (int)Math.round((iGrab1BeamMaxSize + i2BeamExpSize)/2));
				} else {
					// 제품 최대길이 > 20,600	 
					double dConstant = 0.0;
					int iBeamAlpha = 0;
					
					if(iGrab2BeamMaxSize <= 8800) {      
						dConstant = 2.75;
					} else {
						dConstant = 1.75;
					}
					
					iBeamAlpha = (int)Math.round((iYD_MTL_MAX_L - (iGrab1BeamMinSize + iBeamGap + iGrab2BeamMaxSize))/dConstant) + iBeamGap;
					
					//Grab기준점이동값
					int iGrabBasMv = (int)Math.round((iYD_MTL_MAX_L - (iGrab1BeamMaxSize + iBeamAlpha + iGrab2BeamMaxSize))/2);
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + 3200 - (int)Math.round(iYD_MTL_MAX_L/2) + iGrabBasMv + (int)Math.round((iGrab1BeamMaxSize / 2)));
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamAlpha) + (int)Math.round((iGrab1BeamMaxSize + iGrab2BeamMaxSize)/2));
				}
			} else {
				// TCCRC1,TCCRC2
				szCrane_Grab_Use_Gp = "3"; //Grab#1, Grab#2  모두 사용
				
				if (iYD_MTL_MAX_L <= 18600) {
					// 제품 최대길이 <= 18,600	 
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round((iGrab1BeamMinSize / 2)) - (int)Math.round((iGrab1BeamMinSize + iBeamGap + iGrab2BeamMinSize - iYD_MTL_MAX_L)/2));
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamGap) + (int)Math.round((iGrab1BeamMinSize + iGrab2BeamMinSize)/2));

				} else if (iYD_MTL_MAX_L <= 21220) {
					// 제품 최대길이 <= 21,220	 
					//#2 Beam 확장 Size
					int i2BeamExpSize = iYD_MTL_MAX_L - (iBeamGap + iGrab1BeamMinSize);
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round(iGrab1BeamMinSize/2)); 
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamGap) + (int)Math.round((iGrab1BeamMinSize + i2BeamExpSize)/2));
				} else {
					// 제품 최대길이 > 21,220	 

					double dConstant = 0.0;
					int iBeamAlpha = 0;
					
					if(iGrab2BeamMaxSize <= 8800) {
						dConstant = 2.75;
					} else {
						dConstant = 1.75;
					}
					
					iBeamAlpha = (int)Math.round((iYD_MTL_MAX_L - (iGrab1BeamMinSize + iBeamGap + iGrab2BeamMaxSize))/dConstant) + iBeamGap;
					
					//Grab기준점이동값
					int iGrabBasMv = (int)Math.round((iYD_MTL_MAX_L - (iGrab1BeamMaxSize + iBeamAlpha + iGrab2BeamMaxSize))/2);
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + iGrabBasMv + (int)Math.round((iGrab1BeamMaxSize / 2)));
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamAlpha) + (int)Math.round((iGrab1BeamMaxSize + iGrab2BeamMaxSize)/2));
				}
			}
		
		} else if("PT".equals(szYD_STK_COL_GP.substring(2,4))) { 
			//차량 상,하차시
			
			if(iYD_MTL_MAX_L < 12000) {
				// 제품최대길이 < 12,000 
				if("01".equals(szYD_THE_OTHER_BED_NO) || "02".equals(szYD_THE_OTHER_BED_NO)) {
					//01,02 Bed 관련 작업
					szCrane_Grab_Use_Gp = "1"; //Grab#1 만 사용
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS);
					szCRANE_Y2_YAXIS = "0";					
				} else {
					// 그외 Bed 작업
					szCrane_Grab_Use_Gp = "2"; //Grab#2 만 사용
					szCRANE_Y1_YAXIS = "0";
					szCRANE_Y2_YAXIS = Integer.toString(iYD_STK_BED_YAXIS);
				}
			/*
			} else if( "TBCRB1".equals(szYD_EQP_ID) ) { 
				// 2후판 B1 크레인 일 경우
				szCrane_Grab_Use_Gp = "3"; //Grab#1, Grab#2  모두 사용
				
				if (iYD_MTL_MAX_L <= 22200) {
					// 제품 최대길이 <= 22,200	 
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS - (int)Math.round((iYD_MTL_MAX_L / 2)) + (int)Math.round(iGrab1BeamMinSize/2));
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamGap) + (int)Math.round((iGrab1BeamMinSize + iGrab2BeamMinSize)/2));
				} else {
					// 제품 최대길이 > 22,200	 
					double dConstant = 0.0;
					int iBeamAlpha = 0;
					
					if(iGrab2BeamMaxSize <= 8800) {
						dConstant = 2.75;
					} else {
						dConstant = 1.75;
					}
					
					iBeamAlpha = (int)Math.round((iYD_MTL_MAX_L - (iGrab1BeamMinSize + iBeamGap + iGrab2BeamMaxSize))/dConstant) + iBeamGap;
					
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS  - (int)Math.round((iBeamAlpha + iGrab1BeamMaxSize )/ 2)  );
					szCRANE_Y2_YAXIS = Integer.toString(  Integer.parseInt(szCRANE_Y1_YAXIS) + (int)Math.round((iGrab1BeamMaxSize + iBeamGap + iGrab2BeamMaxSize)/2));
				}
			*/	
			} else {
				// 그 외 2 후판 C1, C2 크레인 일 경우
				szCrane_Grab_Use_Gp = "3"; //Grab#1, Grab#2  모두 사용
				
				if (iYD_MTL_MAX_L <= 18600) {
					// 제품 최대길이 <= 18,600	 
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS - (int)Math.round((iYD_MTL_MAX_L / 2))  + (int)Math.round(iGrab1BeamMinSize/ 2));
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamGap) + (int)Math.round((iGrab1BeamMinSize + iGrab2BeamMinSize)/2));
				} else if (iYD_MTL_MAX_L <= 21220) {
					// 제품 최대길이 <= 21,220	 
					//#2 Beam 확장 Size
					int i2BeamExpSize = iYD_MTL_MAX_L - (iBeamGap + iGrab1BeamMinSize);
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS - (int)Math.round((iYD_MTL_MAX_L / 2)) + (int)Math.round(iGrab1BeamMinSize/ 2));
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamGap) + (int)Math.round((iGrab1BeamMaxSize + i2BeamExpSize)/2));
				} else {
					// 제품 최대길이 > 21,220	 

					double dConstant = 0.0;
					int iBeamAlpha = 0;
					
					if(iGrab2BeamMaxSize <= 8800) {
						dConstant = 2.75;
					} else {
						dConstant = 1.75;
					}
					
					iBeamAlpha = (int)Math.round((iYD_MTL_MAX_L - (iGrab1BeamMinSize + iBeamGap + iGrab2BeamMaxSize))/dConstant) + iBeamGap;
					
					//Grab기준점이동값
					int iGrabBasMv = (int)Math.round((iYD_MTL_MAX_L - (iGrab1BeamMaxSize + iBeamAlpha + iGrab2BeamMaxSize))/2);
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS - (int)Math.round((iYD_MTL_MAX_L / 2)) + iGrabBasMv + (int)Math.round((iGrab1BeamMaxSize / 2)));
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamAlpha) + (int)Math.round((iGrab1BeamMaxSize + iGrab2BeamMaxSize)/2));
				}
			}
			
		} else {
			// 그 외  

			if(iYD_MTL_MAX_L < 12000) {
				// 제품최대길이 < 12,000 
				if("RT".equals(szYD_THE_OTHER_COL_GP.substring(2,4)) || "TF".equals(szYD_THE_OTHER_COL_GP.substring(2,4)) || "PT".equals(szYD_THE_OTHER_COL_GP.substring(2,4)) ) {
					//야드에서 야드로의 작업이 아닐경우...
					if("01".equals(szYD_STK_BED_NO) || "02".equals(szYD_STK_BED_NO)) {
						//01,02 Bed 관련 작업
						szCrane_Grab_Use_Gp = "1"; //Grab#1 만 사용
						szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round((iYD_STK_BED_L_MAX / 2)) );
						szCRANE_Y2_YAXIS = "0";					
					} else {
						// 그외 Bed 작업
						szCrane_Grab_Use_Gp = "2"; //Grab#2 만 사용
						szCRANE_Y1_YAXIS = "0";
						szCRANE_Y2_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round((iYD_STK_BED_L_MAX / 2)) );
					}					
				} else {
					if("01".equals(szYD_THE_OTHER_BED_NO) || "02".equals(szYD_THE_OTHER_BED_NO)) {
						//01,02 Bed 관련 작업
						szCrane_Grab_Use_Gp = "1"; //Grab#1 만 사용
						szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round((iYD_STK_BED_L_MAX / 2)) );
						szCRANE_Y2_YAXIS = "0";					
					} else {
						// 그외 Bed 작업
						szCrane_Grab_Use_Gp = "2"; //Grab#2 만 사용
						szCRANE_Y1_YAXIS = "0";
						szCRANE_Y2_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round((iYD_STK_BED_L_MAX / 2)) );
					}
				}
			/*	
			} else if( "TBCRB1".equals(szYD_EQP_ID) ) { 
				// 2후판 B1 크레인 일 경우
				szCrane_Grab_Use_Gp = "3"; //Grab#1, Grab#2  모두 사용
				
				if (iYD_MTL_MAX_L <= 22200) {
					// 제품 최대길이 <= 22,200	 
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round((iYD_STK_BED_L_MAX / 2)) - (int)Math.round((iBeamGap + iGrab1BeamMinSize)/2));
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamGap) + (int)Math.round((iGrab1BeamMinSize + iGrab2BeamMinSize)/2));
				} else {
					// 제품 최대길이 > 22,200	 
					double dConstant = 0.0;
					int iBeamAlpha = 0;
					
					if(iGrab2BeamMaxSize <= 8800) {
						dConstant = 2.75;
					} else {
						dConstant = 1.75;
					}
					
					iBeamAlpha = (int)Math.round((iYD_MTL_MAX_L - (iGrab1BeamMinSize + iBeamGap + iGrab2BeamMaxSize))/dConstant) + iBeamGap;
					
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round((iYD_STK_BED_L_MAX / 2)) - (int)Math.round((iBeamAlpha + iGrab1BeamMaxSize )/ 2)  );
					szCRANE_Y2_YAXIS = Integer.toString(  Integer.parseInt(szCRANE_Y1_YAXIS) + iBeamAlpha + (int)Math.round((iGrab1BeamMaxSize + iGrab2BeamMaxSize)/2));
				}
			*/	
			} else {
				// 그 외 2 후판 C1, C2 크레인 일 경우
				szCrane_Grab_Use_Gp = "3"; //Grab#1, Grab#2  모두 사용
				
				if (iYD_MTL_MAX_L <= 18600) {
					// 제품 최대길이 <= 18,600	 
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round((iYD_STK_BED_L_MAX / 2)) - (int)Math.round((iGrab1BeamMinSize + iBeamGap + iGrab2BeamMinSize)/ 2) + (int)Math.round((iGrab1BeamMinSize / 2)) );
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamGap) + (int)Math.round((iGrab1BeamMinSize + iGrab2BeamMinSize)/2));
				} else if (iYD_MTL_MAX_L <= 21220) {
					// 제품 최대길이 <= 21,220	 
					//#2 Beam 확장 Size
					int i2BeamExpSize = iYD_MTL_MAX_L - (iBeamGap + iGrab1BeamMinSize);
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round((iYD_STK_BED_L_MAX / 2)) - (int)Math.round((iYD_MTL_MAX_L - iGrab1BeamMinSize)/ 2));
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamGap) + (int)Math.round((iGrab1BeamMinSize + i2BeamExpSize)/2));
				} else {
					// 제품 최대길이 > 21,220	 

					double dConstant = 0.0;
					int iBeamAlpha = 0;
					
					if(iGrab2BeamMaxSize <= 8800) {
						dConstant = 2.75;
					} else {
						dConstant = 1.75;
					}
					
					iBeamAlpha = (int)Math.round((iYD_MTL_MAX_L - (iGrab1BeamMinSize + iBeamGap + iGrab2BeamMaxSize))/dConstant) + iBeamGap;
					
					//Grab기준점이동값
					int iGrabBasMv = (int)Math.round((iYD_MTL_MAX_L - (iGrab1BeamMaxSize + iBeamAlpha + iGrab2BeamMaxSize))/2);
					if(iGrabBasMv < 1) {
						iGrabBasMv = 0;
					}
					szCRANE_Y1_YAXIS = Integer.toString(iYD_STK_BED_YAXIS + (int)Math.round((iYD_STK_BED_L_MAX - iYD_MTL_MAX_L) / 2) + iGrabBasMv + (int)Math.round((iGrab1BeamMaxSize / 2)));
					szCRANE_Y2_YAXIS = Integer.toString(  (Integer.parseInt(szCRANE_Y1_YAXIS)+iBeamAlpha) + (int)Math.round((iGrab1BeamMaxSize + iGrab2BeamMaxSize)/2));
				}
			}
			
		}

		if("RT".equals(szYD_STK_COL_GP.substring(2,4)) || "TF".equals(szYD_STK_COL_GP.substring(2,4))) {
			// 허용 오차 :  BRE 에서 읽어온 설비작업 X축 허용오차를 사용
			szCRANE_GAP_Y = Integer.toString(iYdEqpWrkYaxisTol);
		}else if("PT".equals(szYD_STK_COL_GP.substring(2,4))) { 
			// 허용 오차 :  BRE 에서 읽어온 차량작업 Y축 허용오차를 사용
			szCRANE_GAP_Y = Integer.toString(iYdCarWrkYaxisTol);
		} else {
			// 허용 오차 :  BRE 에서 읽어온  X축 허용오차를 사용
			szCRANE_GAP_Y = Integer.toString(iYdStkBedYaxisTol);
		}
		
		//결과 값 셋팅
		//recResult.setField("CRANE_Y_YAXIS"	, szCRANE_Y_YAXIS);
		recResult.setField("CRANE_Y1_YAXIS"		, szCRANE_Y1_YAXIS);
		recResult.setField("CRANE_Y2_YAXIS"		, szCRANE_Y2_YAXIS);
		recResult.setField("CRANE_GAP_Y"		, szCRANE_GAP_Y);
		
		recResult.setField("Crane_Grab_Use_Gp"	,szCrane_Grab_Use_Gp);
		
		return szRtnMsg;
    }    
    
    
    /**
     * 후판제품XY좌표계산
     * @param recPara
     * @param recResult
     * @return
     * @throws JDTOException
     */
    public static String procXYCalForPlateCrane(JDTORecord recPara, JDTORecord recResult) throws JDTOException {
    	
    	String szRtnMsg				= YdConstant.RETN_CD_SUCCESS;
    	String szLogMsg 			= ""; 
    	String szMethodName			= "procXYCalForPlateCrane";
		String szOperationName		= "후판제품XY좌표계산";
		int intRtnVal				= -100;
		
		JDTORecordSet	rsResult	= null;
		JDTORecord		recInPara	= null;
		JDTORecord		recOutPara	= null;
		String szYD_CRN_SCH_ID		= null;
		String szYD_UP_STK_COL_GP	= null;
		String szYD_UP_STK_BED_NO	= null;
		String szYD_DN_STK_COL_GP	= null;
		String szYD_DN_STK_BED_NO	= null;
		String szYD_EQP_ID			= null;
		String szYD_MTL_W			= null;
		
		YdCrnWrkMtlDao ydCrnWrkMtlDao	= new YdCrnWrkMtlDao();
		
		//--------------------------------------- 파라미터 ------------------------------------------------------------
		szYD_CRN_SCH_ID 	= ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_SCH_ID");			//크레인스케줄ID
		szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID");				//크레인설비ID
		szYD_UP_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_STK_COL_GP");		//권상위치 적치열
		szYD_UP_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_STK_BED_NO");		//권상위치 적치베드
		szYD_DN_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_STK_COL_GP");		//권하위치 적치열
		szYD_DN_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_STK_BED_NO");		//권하위치 적치베드
		//------------------------------------------------------------------------------------------------------------
		
		//------------------------------------------------------------------------------------------------------------
		//1. 크레인작업재료중에서 길이가 제일 긴 재료 구하기
		//------------------------------------------------------------------------------------------------------------
		String szSTL_NO = "";
		rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
		recInPara 	= JDTORecordFactory.getInstance().create();
		recInPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
		intRtnVal 	= ydCrnWrkMtlDao.getYdCrnwrkmtl(recInPara, rsResult, 16);
		
		if( intRtnVal <= 0 ) {
			szLogMsg="["+szOperationName+":"+szMethodName+"] 크레인작업재료의 길이가 제일 긴 재료 조회 시 execution failed - 반환값 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_SUCCESS;
		}
		
		rsResult.first();
		recInPara = rsResult.getRecord();
		
		szSTL_NO = ydDaoUtils.paraRecChkNull(recInPara, "STL_NO");
		
		szLogMsg="["+szOperationName+":"+szMethodName+"] 크레인작업재료의 길이가 제일 긴 재료["+szSTL_NO+"] 조회 성공";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		//------------------------------------------------------------------------------------------------------------
		
		
		//------------------------------------------------------------------------------------------------------------
		//2. 크레인작업재료중에서 폭이 제일 넓은 재료조회 
		//------------------------------------------------------------------------------------------------------------
		rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
		recInPara 	= JDTORecordFactory.getInstance().create();
		recInPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
		intRtnVal 	= ydCrnWrkMtlDao.getYdCrnwrkmtl(recInPara, rsResult, 20);
		
		if( intRtnVal <= 0 ) {
			szLogMsg="["+szOperationName+":"+szMethodName+"] 크레인작업재료의 폭이 제일 넓은 재료 조회 시 execution failed - 반환값 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_SUCCESS;
		}
		
		rsResult.first();
		recInPara = rsResult.getRecord();
		
		szYD_MTL_W = ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W");
		
		szLogMsg="["+szOperationName+":"+szMethodName+"] 크레인작업재료의 폭이 제일 넓은 재료["+ydDaoUtils.paraRecChkNull(recInPara, "STL_NO")+"] 조회 성공";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		//------------------------------------------------------------------------------------------------------------
		
		//------------------------------------------------------------------------------------------------------------
		// X, Y좌표 계산.
		//------------------------------------------------------------------------------------------------------------
		recInPara			= JDTORecordFactory.getInstance().create();
		recOutPara			= JDTORecordFactory.getInstance().create();
		
		/*
		String szSTL_NO				= null;						//크레인작업재료들중에서 길이가 제일 긴 재료번호
    	String szYD_UP_STK_COL_GP	= null;						//권상지시위치 - 적치열
    	String szYD_UP_STK_BED_NO	= null;						//권상지시위치 - 적치베드
    	String szYD_DN_STK_COL_GP	= null;						//권하지시위치 - 적치열
    	String szYD_DN_STK_BED_NO	= null;						//권하지시위치 - 적치베드
    	String szYD_EQP_ID			= null;						//크레인설비ID
		 */
		
		recInPara.setField("STL_NO", 				szSTL_NO);
		recInPara.setField("YD_UP_STK_COL_GP", 		szYD_UP_STK_COL_GP);
		recInPara.setField("YD_UP_STK_BED_NO", 		szYD_UP_STK_BED_NO);
		recInPara.setField("YD_DN_STK_COL_GP", 		szYD_DN_STK_COL_GP);
		recInPara.setField("YD_DN_STK_BED_NO", 		szYD_DN_STK_BED_NO);
		recInPara.setField("YD_EQP_ID", 			szYD_EQP_ID);
		recInPara.setField("YD_MTL_W", 				szYD_MTL_W);
		
		szRtnMsg	= PlateCraneXYCal(recInPara, recOutPara);
		
		String szCrane_Grab_Use_Gp 			= ydDaoUtils.paraRecChkNull(recOutPara, "Crane_Grab_Use_Gp");
		String szUp_Grab_X_Value     		= ydDaoUtils.paraRecChkNull(recOutPara, "Up_Grab_X_Value");
		String szUp_Grab_Y_Value     		= ydDaoUtils.paraRecChkNull(recOutPara, "Up_Grab_Y_Value");
		String szUp_Grab_Y1_Addr      		= ydDaoUtils.paraRecChkNull(recOutPara, "Up_Grab_Y1_Addr");
		String szUp_Grab_Y1_Value     		= ydDaoUtils.paraRecChkNull(recOutPara, "Up_Grab_Y1_Value");
		String szUp_Grab_Y2_Addr      		= ydDaoUtils.paraRecChkNull(recOutPara, "Up_Grab_Y2_Addr");
		String szUp_Grab_Y2_Value     		= ydDaoUtils.paraRecChkNull(recOutPara, "Up_Grab_Y2_Value");
		
		String szDn_Grab_X_Value     		= ydDaoUtils.paraRecChkNull(recOutPara, "Dn_Grab_X_Value");
		String szDn_Grab_Y_Value     		= ydDaoUtils.paraRecChkNull(recOutPara, "Dn_Grab_Y_Value");
		String szDn_Grab_Y1_Addr      		= ydDaoUtils.paraRecChkNull(recOutPara, "Dn_Grab_Y1_Addr");
		String szDn_Grab_Y1_Value     		= ydDaoUtils.paraRecChkNull(recOutPara, "Dn_Grab_Y1_Value");
		String szDn_Grab_Y2_Addr      		= ydDaoUtils.paraRecChkNull(recOutPara, "Dn_Grab_Y2_Addr");
		String szDn_Grab_Y2_Value     		= ydDaoUtils.paraRecChkNull(recOutPara, "Dn_Grab_Y2_Value");
		
		//------------------------------------------------------------------------------------------------------------
		// 크레인 Grab 구분[D : 1Grab Crane(2호기), E : 2Grab Crane(1호기)]
		// 1Grab Crane : Y좌표만 설정 Y1, Y2는 0으로 설정
		// 2Grab Crane : Y1, Y2좌표만 설정 Y는 0으로 설정
		//------------------------------------------------------------------------------------------------------------
		recResult.setField("Crane_Grab_Use_Gp", 	szCrane_Grab_Use_Gp);
		recResult.setField("Up_Grab_X_Value", 		szUp_Grab_X_Value);
		recResult.setField("Up_Grab_Y_Value", 		szUp_Grab_Y_Value);
		recResult.setField("Up_Grab_Y1_Addr", 		szUp_Grab_Y1_Addr);
		recResult.setField("Up_Grab_Y1_Value", 		szUp_Grab_Y1_Value);
		recResult.setField("Up_Grab_Y2_Addr", 		szUp_Grab_Y2_Addr);
		recResult.setField("Up_Grab_Y2_Value", 		szUp_Grab_Y2_Value);
		recResult.setField("Dn_Grab_X_Value", 		szDn_Grab_X_Value);
		recResult.setField("Dn_Grab_Y_Value", 		szDn_Grab_Y_Value);
		recResult.setField("Dn_Grab_Y1_Addr", 		szDn_Grab_Y1_Addr);
		recResult.setField("Dn_Grab_Y1_Value", 		szDn_Grab_Y1_Value);
		recResult.setField("Dn_Grab_Y2_Addr", 		szDn_Grab_Y2_Addr);
		recResult.setField("Dn_Grab_Y2_Value", 		szDn_Grab_Y2_Value);
		
		szLogMsg	= "["+szOperationName+"] -------------------------- OUT -------------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.displayRecord(szOperationName, recResult);
		szLogMsg	= "["+szOperationName+"] --------------------------------------------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return szRtnMsg;
    }
    
    /**
     * 좌표계산(후판제품)
     * @param recInPara
     * @param recOutPara
     * @return
     * @throws JDTOException
     */
    public static String PlateCraneXYCal(JDTORecord recInPara, JDTORecord recOutPara) throws JDTOException {
    	String szOperationName		= "좌표계산(후판제품)";
    	String szMethodName			= "PlateCraneXYCal";
    	String szMsg				= null;
    	String szRtnMsg				= null;
    	
    	JDTORecord		recPara		= null;
    	JDTORecord		recTemp		= null;
    	JDTORecordSet	outRecSet	= null;
    	//-------------------------------------------------------------------------------------------------
    	//	파라미터로 전달되는 항목 정의
    	//-------------------------------------------------------------------------------------------------
    	String szSTL_NO				= null;						//크레인작업재료들중에서 길이가 제일 긴 재료번호
    	String szYD_UP_STK_COL_GP	= null;						//권상지시위치 - 적치열
    	String szYD_UP_STK_BED_NO	= null;						//권상지시위치 - 적치베드
    	String szYD_DN_STK_COL_GP	= null;						//권하지시위치 - 적치열
    	String szYD_DN_STK_BED_NO	= null;						//권하지시위치 - 적치베드
    	String szYD_EQP_ID			= null;						//크레인설비ID
    	//-------------------------------------------------------------------------------------------------
    	
    	String szYD_CRN_GRAB_GP		= null;						//Grab Type
    	String szYD_CRN_TONG_L		= null;						//Crane Beam 길이
    	String szYD_CRN_GRAB_TP		= null;						//1,2 Grab 구분
    	
    	String szYD_MTL_W			= null;						//재료 폭
    	int intYD_MTL_W				= 0;						//재료 폭
    	String szYD_MTL_L			= null;						//재료 길이
    	int intYD_MTL_L				= 0;						//재료 길이
    	
    	//-------------------------------------------------------------------------------------------------
    	//	권상위치 정보
    	//-------------------------------------------------------------------------------------------------
    	String szYD_UP_STK_COL_BED_L_TP = null;					//권상지시위치 - 야드적치열Bed길이Type
    	String szYD_UP_CAR_USE_GP		= null;					//차량사용구분
    	String szUP_TRN_EQP_CD			= null;					//운송장비코드
    	String szUP_CAR_NO				= null;					//차량번호
    	String szUP_CARD_NO				= null;					//카드번호
    	
    	int intYD_UP_STK_BED_XAXIS		= 0;					//권상지시위치베드 - 야드적치BedX축
    	int intYD_UP_STK_BED_YAXIS		= 0;					//권상지시위치베드 - 야드적치BedY축
    	String szYD_UP_STK_BED_L_GP		= null;					//권상지시위치베드 - 야드적치Bed길이구분
    	String szYD_UP_STK_BED_W_GP 	= null;					//권상지시위치베드 - 야드적치Bed폭구분
        String szYD_UP_STK_BED_WHIO_STAT= null;					//권상지시위치베드 - 야드적치Bed상태구분
        String szYD_UP_STK_BED_W_MAX	= null;
    	int intYD_UP_STK_BED_W_MAX		= 0;
    	int intYD_UP_STK_BED_L_MAX		= 0;
    	//-------------------------------------------------------------------------------------------------
    	
    	//-------------------------------------------------------------------------------------------------
    	//	권하위치 정보
    	//-------------------------------------------------------------------------------------------------
    	String szYD_DN_STK_COL_BED_L_TP = null;					//권하지시위치 - 야드적치열Bed길이Type
    	String szYD_DN_CAR_USE_GP		= null;					//차량사용구분
    	String szDN_TRN_EQP_CD			= null;					//운송장비코드
    	String szDN_CAR_NO				= null;					//차량번호
    	String szDN_CARD_NO				= null;					//카드번호
    	
    	int intYD_DN_STK_BED_XAXIS		= 0;					//권하지시위치베드 - 야드적치BedX축
    	int intYD_DN_STK_BED_YAXIS		= 0;					//권하지시위치베드 - 야드적치BedY축
    	String szYD_DN_STK_BED_L_GP		= null;					//권하지시위치베드 - 야드적치Bed길이구분
    	String szYD_DN_STK_BED_W_GP 	= null;					//권하지시위치베드 - 야드적치Bed폭구분
    	String szYD_DN_STK_BED_WHIO_STAT= null;					//권하지시위치베드 - 야드적치Bed상태구분
    	
    	String szYD_DN_STK_BED_W_MAX	= null;
    	int intYD_DN_STK_BED_W_MAX		= 0;
    	int intYD_DN_STK_BED_L_MAX		= 0;
    	//-------------------------------------------------------------------------------------------------
    	
    	String szCrane_Grab_Use_Gp 		= null;
		String szUp_Grab_X_Value     	= null;
		String szUp_Grab_Y_Value     	= null;
		int intUp_Grab_Y1_Value     	= 0;
		int intUp_Grab_Y2_Value     	= 0;
		
		String szDn_Grab_X_Value     	= null;
		String szDn_Grab_Y_Value     	= null;
		int intDn_Grab_Y1_Value     	= 0;
		int intDn_Grab_Y2_Value     	= 0;
    	
    	szMsg ="["+szOperationName+"] -------------------------- 메소드 시작 - 파라미터 확인 --------------------------" ;
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
 
 		YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
 		
 		// PIDEV
//		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APPPI0", "T", "*");
		
//		if("PIDEV".equals("PIDEV")) {			 
			szRtnMsg = PlateCraneXYCal_PIDEV(recInPara, recOutPara);			
			return szRtnMsg;
//		}
//		
//		szSTL_NO					= ydDaoUtils.paraRecChkNull(recInPara, "STL_NO");					//크레인작업재료들중에서 길이가 제일 긴 재료번호
//		szYD_UP_STK_COL_GP			= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_COL_GP");			//권상지시위치 - 적치열
//		szYD_UP_STK_BED_NO			= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_BED_NO");			//권상지시위치 - 적치베드
//		szYD_DN_STK_COL_GP			= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_COL_GP");			//권하지시위치 - 적치열
//		szYD_DN_STK_BED_NO			= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_BED_NO");			//권하지시위치 - 적치베드
//		szYD_EQP_ID					= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID");				//크레인설비ID
//		szYD_MTL_W 					= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W"); 				//크레인작업재료들중에서 폭이 제일 넓은 재료의 폭 값
//		
//		szMsg ="["+szOperationName+"] 크레인작업재료들중에서 길이가 제일 긴 재료번호 - " + szSTL_NO;
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분 - " + szYD_UP_STK_COL_GP + ", 베드번호 - " + szYD_UP_STK_BED_NO + "]";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분 - " + szYD_DN_STK_COL_GP + ", 베드번호 - " + szYD_DN_STK_BED_NO + "]";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		szMsg ="["+szOperationName+"] 크레인설비ID - " + szYD_EQP_ID;
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//
//		
//		if (szYD_UP_STK_COL_GP.startsWith("KE")||szYD_UP_STK_COL_GP.startsWith("KF")) {
//			szRtnMsg	= PlateCraneXYCal_EF(recInPara, recOutPara);
//			return szRtnMsg;
//		}
//		
//		
//		//---------------------------------------------------------------------------------------------------------
//		// 저장품 조회
//		// 제품길이 (YD_MTL_L) 추출
//		//---------------------------------------------------------------------------------------------------------
//		szMsg ="["+szOperationName+"] 재료번호[" + szSTL_NO + "]로 저장품 조회 시작";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
//		recPara 	= JDTORecordFactory.getInstance().create();
//		recPara.setField("STL_NO", szSTL_NO);
//		
//		szRtnMsg	= DaoManager.getYdStock(recPara, outRecSet, 0);
//		
//		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//			szMsg ="["+szOperationName+"] 재료번호[" + szSTL_NO + "]로 저장품 조회 시 오류발생 - 메세지 : " + szRtnMsg;
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
//			return szRtnMsg;
//		}
//		
//		outRecSet.first();
//		recTemp = outRecSet.getRecord();
//		
//		szYD_MTL_L 				= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L"); 			// 제품길이
//		
//		int pointIdx			= szYD_MTL_W.lastIndexOf(".");
//		
//		if( pointIdx >= 0 ) {
//			szYD_MTL_W			= szYD_MTL_W.substring(0, pointIdx);
//		}
//		
//		intYD_MTL_W				= Integer.parseInt(szYD_MTL_W);
//		intYD_MTL_L				= Integer.parseInt(szYD_MTL_L);
//		
//		szMsg ="["+szOperationName+"] 크레인작업재료들중에서 길이가 제일 긴 재료번호[" + szSTL_NO + "] - 제품길이["+szYD_MTL_L+"]";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		szMsg ="["+szOperationName+"] 크레인작업재료들중에서 폭이 제일 넓은 재료의 제품폭["+szYD_MTL_W+"]";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		//---------------------------------------------------------------------------------------------------------
//		
//		//---------------------------------------------------------------------------------------------------------
//		// 크레인사양 조회
//		// 설비ID로  크레인 Beam 길이(YD_CRN_TONG_L) 추출
//		//---------------------------------------------------------------------------------------------------------
//		szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]로 크레인사양 조회 시작";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		recPara 	= JDTORecordFactory.getInstance().create();
//		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
//		recPara.setField("YD_EQP_ID", szYD_EQP_ID);
//		
//		szRtnMsg	= DaoManager.getYdCrnspec(recPara, outRecSet, 0);
//		
//		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//			szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]로 크레인사양 조회 시 오류발생 - 메세지 : " + szRtnMsg;
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
//			return szRtnMsg;
//		}
//		
//		outRecSet.first();
//		recTemp = outRecSet.getRecord();
//		
//		szYD_CRN_TONG_L = ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_TONG_L"); // 크레인 Beam 길이
//		
//		szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]의 크레인Beam길이[YD_CRN_TONG_L:"+szYD_CRN_TONG_L+"]";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		szYD_CRN_GRAB_TP = ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_GRAB_TP"); // 크레인 Grab 구분
//		
//		if("X".equals(szYD_CRN_GRAB_TP)){
//			szYD_CRN_GRAB_GP = "E";
//		}else{
//			szYD_CRN_GRAB_GP = "D";
//		}
//		
//		szMsg ="["+szOperationName+"] 크레인 Grab 구분[D : 1Grab Crane, E : 2Grab Crane] - " + szYD_CRN_GRAB_GP;
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		//---------------------------------------------------------------------------------------------------------
//		
//		//---------------------------------------------------------------------------------------------------------
//		// 권상지시위치 - 적치열조회 : 야드적치열Bed길이Type, 차량정보가 존재하면 차량정보 조회
//		//---------------------------------------------------------------------------------------------------------
//		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "]로 적치열 조회 시작";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
//		recPara 	= JDTORecordFactory.getInstance().create();
//		recPara.setField("YD_STK_COL_GP", szYD_UP_STK_COL_GP);
//		
//		szRtnMsg	= DaoManager.getYdStkcol(recPara, outRecSet, 0);
//		
//		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//			szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "]로 적치열 조회 시 오류발생 - 메세지 : " + szRtnMsg;
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
//			return szRtnMsg;
//		}
//		
//		outRecSet.first();
//		recTemp = outRecSet.getRecord();
//		
//		szYD_UP_STK_COL_BED_L_TP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_BED_L_TP"); 		//야드적치열Bed길이Type
//		szYD_UP_CAR_USE_GP					= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_USE_GP"); 				//차량사용구분
//		szUP_TRN_EQP_CD						= ydDaoUtils.paraRecChkNull(recTemp, "TRN_EQP_CD"); 				//운송장비코드
//		szUP_CAR_NO							= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO"); 					//차량번호
//		szUP_CARD_NO						= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO"); 					//카드번호
//		
//		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "]로 적치열 조회 완료 - 야드적치열Bed길이Type["+szYD_UP_STK_COL_BED_L_TP+"]";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "] - 차량사용구분["+szYD_UP_CAR_USE_GP+"], 운송장비코드["+szUP_TRN_EQP_CD+"], 차량번호["+szUP_CAR_NO+"], 카드번호["+szUP_CARD_NO+"]";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		//---------------------------------------------------------------------------------------------------------
//		
//		//---------------------------------------------------------------------------------------------------------
//		// 권하지시위치 - 적치열조회 : 야드적치열Bed길이Type, 차량정보가 존재하면 차량정보 조회
//		//---------------------------------------------------------------------------------------------------------
//		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "]로 적치열 조회 시작";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
//		recPara 	= JDTORecordFactory.getInstance().create();
//		recPara.setField("YD_STK_COL_GP", szYD_DN_STK_COL_GP);
//		
//		szRtnMsg	= DaoManager.getYdStkcol(recPara, outRecSet, 0);
//		
//		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//			szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "]로 적치열 조회 시 오류발생 - 메세지 : " + szRtnMsg;
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
//			return szRtnMsg;
//		}
//		
//		outRecSet.first();
//		recTemp = outRecSet.getRecord();
//		
//		szYD_DN_STK_COL_BED_L_TP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_BED_L_TP"); 		//야드적치열Bed길이Type
//		szYD_DN_CAR_USE_GP					= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_USE_GP"); 				//차량사용구분
//		szDN_TRN_EQP_CD						= ydDaoUtils.paraRecChkNull(recTemp, "TRN_EQP_CD"); 				//운송장비코드
//		szDN_CAR_NO							= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO"); 					//차량번호
//		szDN_CARD_NO						= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO"); 					//카드번호
//		
//		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "]로 적치열 조회 완료 - 야드적치열Bed길이Type["+szYD_DN_STK_COL_BED_L_TP+"]";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "] - 차량사용구분["+szYD_DN_CAR_USE_GP+"], 운송장비코드["+szDN_TRN_EQP_CD+"], 차량번호["+szDN_CAR_NO+"], 카드번호["+szDN_CARD_NO+"]";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		//---------------------------------------------------------------------------------------------------------
//
//		//---------------------------------------------------------------------------------------------------------
//		// 권상지시위치 - 적치Bed 조회
//		// 야드적치열구분, 야드적치Bed번호로  야드적치BedY축(YD_STK_BED_YAXIS) 추출
//		//---------------------------------------------------------------------------------------------------------
//		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + ", 베드번호:" + szYD_UP_STK_BED_NO + "]로 베드 조회 시작";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
//		recPara 	= JDTORecordFactory.getInstance().create();
//		recPara.setField("YD_STK_COL_GP", szYD_UP_STK_COL_GP);
//		recPara.setField("YD_STK_BED_NO", szYD_UP_STK_BED_NO);
//		
//		szRtnMsg	= DaoManager.getYdStkbed(recPara, outRecSet, 0);
//		
//		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//			szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + ", 베드번호:" + szYD_UP_STK_BED_NO + "]로 베드 조회 시 오류발생 - 메세지 : " + szRtnMsg;
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
//			return szRtnMsg;
//		}
//		
//		outRecSet.first();
//		recTemp = outRecSet.getRecord();
//		
//		intYD_UP_STK_BED_XAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS"); 		// 야드적치BedX축
//		intYD_UP_STK_BED_YAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS"); 		// 야드적치BedY축
//		szYD_UP_STK_BED_L_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP"); 			// 야드적치Bed길이구분
//		szYD_UP_STK_BED_W_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP"); 			// 야드적치Bed폭구분
//		szYD_UP_STK_BED_WHIO_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT"); 		// 야드적치Bed상태구분
//		szYD_UP_STK_BED_W_MAX			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_MAX"); 			// 야드적치Bed폭Max
//		intYD_UP_STK_BED_L_MAX			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_L_MAX"); 		// 야드적치Bed길이Max
//		
//		pointIdx	= szYD_UP_STK_BED_W_MAX.lastIndexOf(".");
//		
//		if( pointIdx >= 0 ) {
//			szYD_UP_STK_BED_W_MAX		= szYD_UP_STK_BED_W_MAX.substring(0, pointIdx);
//		}
//		
//		intYD_UP_STK_BED_W_MAX			= Integer.parseInt(szYD_UP_STK_BED_W_MAX);
//		
//		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + ", 베드번호:" + szYD_UP_STK_BED_NO + "]의  야드적치BED X축["+intYD_UP_STK_BED_XAXIS+"], 야드적치BED Y축["+intYD_UP_STK_BED_YAXIS+"], 야드적치Bed길이구분["+szYD_UP_STK_BED_L_GP+"] 조회 완료";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		//---------------------------------------------------------------------------------------------------------
//		
//    	
//		//---------------------------------------------------------------------------------------------------------
//		// 권하지시위치 - 적치Bed 조회
//		// 야드적치열구분, 야드적치Bed번호로  야드적치BedY축(YD_STK_BED_YAXIS) 추출
//		//---------------------------------------------------------------------------------------------------------
//		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + ", 베드번호:" + szYD_DN_STK_BED_NO + "]로 베드 조회 시작";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
//		recPara 	= JDTORecordFactory.getInstance().create();
//		recPara.setField("YD_STK_COL_GP", szYD_DN_STK_COL_GP);
//		recPara.setField("YD_STK_BED_NO", szYD_DN_STK_BED_NO);
//		
//		szRtnMsg	= DaoManager.getYdStkbed(recPara, outRecSet, 0);
//		
//		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//			szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + ", 베드번호:" + szYD_DN_STK_BED_NO + "]로 베드 조회 시 오류발생 - 메세지 : " + szRtnMsg;
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
//			return szRtnMsg;
//		}
//		
//		outRecSet.first();
//		recTemp = outRecSet.getRecord();
//		
//		intYD_DN_STK_BED_XAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS"); 		// 야드적치BedX축
//		intYD_DN_STK_BED_YAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS"); 		// 야드적치BedY축
//		szYD_DN_STK_BED_L_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP"); 			// 야드적치Bed길이구분
//		szYD_DN_STK_BED_W_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP"); 			// 야드적치Bed폭구분
//		szYD_DN_STK_BED_WHIO_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT"); 		// 야드적치Bed상태구분
//		szYD_DN_STK_BED_W_MAX			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_MAX"); 			// 야드적치Bed폭Max
//		intYD_DN_STK_BED_L_MAX			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_L_MAX"); 		// 야드적치Bed길이Max
//		
//		pointIdx	= szYD_DN_STK_BED_W_MAX.lastIndexOf(".");
//		
//		if( pointIdx >= 0 ) {
//			szYD_DN_STK_BED_W_MAX		= szYD_DN_STK_BED_W_MAX.substring(0, pointIdx);
//		}
//		
//		intYD_DN_STK_BED_W_MAX			= Integer.parseInt(szYD_DN_STK_BED_W_MAX);
//		
//		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + ", 베드번호:" + szYD_DN_STK_BED_NO + "]의  야드적치BED X축["+intYD_DN_STK_BED_XAXIS+"], 야드적치BED Y축["+intYD_DN_STK_BED_YAXIS+"], 야드적치Bed길이구분["+szYD_DN_STK_BED_L_GP+"] 조회 완료";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		//---------------------------------------------------------------------------------------------------------
//		
//		//---------------------------------------------------------------------------------------------------------
//		//	X좌표 구하기
//		//---------------------------------------------------------------------------------------------------------
//		szUp_Grab_X_Value			= String.valueOf(procXCalForPlateYd(intYD_UP_STK_BED_XAXIS, intYD_MTL_W, intYD_UP_STK_BED_W_MAX, szYD_UP_STK_COL_GP,szYD_UP_STK_BED_W_GP,szYD_UP_STK_BED_WHIO_STAT));
//		szDn_Grab_X_Value			= String.valueOf(procXCalForPlateYd(intYD_DN_STK_BED_XAXIS, intYD_MTL_W, intYD_DN_STK_BED_W_MAX, szYD_DN_STK_COL_GP,szYD_DN_STK_BED_W_GP,szYD_DN_STK_BED_WHIO_STAT));
//		//---------------------------------------------------------------------------------------------------------
//		
//		//---------------------------------------------------------------------------------------------------------
//		//	Y좌표 구하기 
//		//---------------------------------------------------------------------------------------------------------
//		//(Tmp_YD_MTL_L / 2)
//		/*
//		int iUpLsize = 0;  
//		if(szYD_UP_STK_COL_GP.startsWith("KB01")){
//			if("U".equals(szYD_UP_STK_BED_L_GP)){		iUpLsize = 6800;
//			}else if("S".equals(szYD_UP_STK_BED_L_GP)){ iUpLsize = 9200;
//			}else if("M".equals(szYD_UP_STK_BED_L_GP)){ iUpLsize = 14000;
//			}else if("L".equals(szYD_UP_STK_BED_L_GP)){	iUpLsize = 18600;
//			}else if("X".equals(szYD_UP_STK_BED_L_GP)){	iUpLsize = 25000;
//			}
//			szUp_Grab_Y_Value			= String.valueOf(intYD_UP_STK_BED_YAXIS + (iUpLsize/2));
//		}else{
//			szUp_Grab_Y_Value			= String.valueOf(intYD_UP_STK_BED_YAXIS + (intYD_MTL_L/2));
//		}
//		*/
//		szUp_Grab_Y_Value			= String.valueOf(intYD_UP_STK_BED_YAXIS + (intYD_MTL_L/2));
//		/*
//		int iDnLsize = 0; 
//		if(szYD_DN_STK_COL_GP.startsWith("KB01")){
//			if("U".equals(szYD_DN_STK_BED_L_GP)){		iDnLsize = 6800;	
//			}else if("S".equals(szYD_DN_STK_BED_L_GP)){ iDnLsize = 9200;	
//			}else if("M".equals(szYD_DN_STK_BED_L_GP)){ iDnLsize = 14000;	
//			}else if("L".equals(szYD_DN_STK_BED_L_GP)){	iDnLsize = 18600;	
//			}else if("X".equals(szYD_DN_STK_BED_L_GP)){	iDnLsize = 25000;	
//			}
//			szDn_Grab_Y_Value			= String.valueOf(intYD_DN_STK_BED_YAXIS + (iDnLsize/2));
//		}else{
//			szDn_Grab_Y_Value			= String.valueOf(intYD_DN_STK_BED_YAXIS + (intYD_MTL_L/2));
//		}
//		*/
//		szDn_Grab_Y_Value			= String.valueOf(intYD_DN_STK_BED_YAXIS + (intYD_MTL_L/2));
//		//---------------------------------------------------------------------------------------------------------
//		
//		//---------------------------------------------------------------------------------------------------------
//		//	Y1, Y2좌표 구하기
//		//---------------------------------------------------------------------------------------------------------
//		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "] - 야드적치열Bed길이Type["+szYD_UP_STK_COL_BED_L_TP+"]";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "] - 차량사용구분["+szYD_UP_CAR_USE_GP+"], 운송장비코드["+szUP_TRN_EQP_CD+"], 차량번호["+szUP_CAR_NO+"], 카드번호["+szUP_CARD_NO+"]";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "] - 야드적치열Bed길이Type["+szYD_DN_STK_COL_BED_L_TP+"]";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "] - 차량사용구분["+szYD_DN_CAR_USE_GP+"], 운송장비코드["+szDN_TRN_EQP_CD+"], 차량번호["+szDN_CAR_NO+"], 카드번호["+szDN_CARD_NO+"]";
//		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//		
//		//----------------------------------------------------------------------------
//		
//		//----------------------------------------------------------------------------
//		//	2호기 - 1Grab
//		//----------------------------------------------------------------------------
//		if( szYD_CRN_GRAB_GP.equals("D") )	{			//2호기 - 1Grab
//			
//			int intYD_CRN_BEAM_L			= 	  0;	//Beam길이
//			//----------------------------------------------------------------------------
//		
//			if(szYD_UP_STK_COL_GP.startsWith("KA")){
//				intYD_CRN_BEAM_L			=  14000;	//Beam길이 
//			}else if(szYD_UP_STK_COL_GP.startsWith("KC")||
//					 szYD_UP_STK_COL_GP.startsWith("KD")){
//				intYD_CRN_BEAM_L			=  9200;	//Beam길이 
//			}else if(szYD_UP_STK_COL_GP.startsWith("KE")){
//				intYD_CRN_BEAM_L			=  6800;	//Beam길이 
//			}else if(szYD_UP_STK_COL_GP.startsWith("KF")){
//				intYD_CRN_BEAM_L			= 12600;	//Beam길이
//			}
//			
//			szMsg ="["+szOperationName+"] 크레인작업재료들중에서 길이가 제일 긴 재료번호[" + szSTL_NO + "]의 제품길이["+intYD_MTL_L+"]";
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			
//			//----------------------------------------------------------------------------
//			//	1Grab Crn - From위치 Y1좌표
//			//----------------------------------------------------------------------------
//			
//			if( szYD_UP_STK_COL_GP.substring(2).matches("\\d\\d\\d\\d") 			/* 일반베드 */
//				|| szYD_UP_STK_COL_GP.substring(2, 4).equals("TF")					/* TRANSFER */
//				|| szYD_UP_STK_COL_GP.substring(2, 4).equals("RT")					/* ROLLER TABLE */
//				|| szYD_UP_STK_COL_GP.substring(2, 4).equals("GJ")					/* 가적장 */
//			) {
//				if( szYD_UP_STK_COL_GP.substring(2, 4).equals("TF") ) {				/* TRANSFER */
//					if( szYD_UP_STK_BED_NO.equals("06") 							/* 06, 05베드를 03번지로 그 외의 베드는 01번지로 */
//						|| szYD_UP_STK_BED_NO.equals("05")
//					) {
//						szYD_UP_STK_BED_NO			= "03";
//					}else{
//						szYD_UP_STK_BED_NO			= "01";
//					}
//				}else if( szYD_UP_STK_COL_GP.substring(2, 4).equals("RT") ) {		/* ROLLER TABLE */
//					if( szYD_UP_STK_BED_NO.equals("10") 							/* 10, 30, 50, 70베드를 03번지로 그 외의 베드는 01번지로 */
//							|| szYD_UP_STK_BED_NO.equals("30")
//							|| szYD_UP_STK_BED_NO.equals("50")
//							|| szYD_UP_STK_BED_NO.equals("70")
//						) {
//							szYD_UP_STK_BED_NO			= "03";
//						}else{
//							szYD_UP_STK_BED_NO			= "01";
//						}
//				}else if( szYD_UP_STK_COL_GP.substring(2, 4).equals("GJ") ) {		/* 가적장 */
//					if( szYD_UP_STK_BED_NO.equals("06") ) {							/* 06베드를 03번지로 그 외의 베드는 01번지로 */
//						szYD_UP_STK_BED_NO			= "03";
//					}else{
//						szYD_UP_STK_BED_NO			= "01";
//					}
//				}
//				
//				intUp_Grab_Y1_Value				= procY1CalFor1GrabCrn( intYD_UP_STK_BED_YAXIS, intYD_MTL_L, intYD_CRN_BEAM_L);
//			}
//			
//			szMsg ="["+szOperationName+"] 1Grab Crn[2호기:"+szYD_EQP_ID+"]의 차량이 아닌 경우 From위치 Y1좌표["+intUp_Grab_Y1_Value+"]";
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			//----------------------------------------------------------------------------
//			
//			//----------------------------------------------------------------------------
//			//	1Grab Crn - To위치 Y1좌표
//			//----------------------------------------------------------------------------
//			
//			if( szYD_DN_STK_COL_GP.substring(2).matches("\\d\\d\\d\\d") 			/* 일반베드 */
//					|| szYD_DN_STK_COL_GP.substring(2, 4).equals("TF")				/* TRANSFER */
//					|| szYD_DN_STK_COL_GP.substring(2, 4).equals("RT")				/* ROLLER TABLE */
//					|| szYD_DN_STK_COL_GP.substring(2, 4).equals("GJ")				/* 가적장 */
//			) {
//				if( szYD_DN_STK_COL_GP.substring(2, 4).equals("TF") ) {				/* TRANSFER */
//					if( szYD_DN_STK_BED_NO.equals("06") 							/* 06, 05베드를 03번지로 그 외의 베드는 01번지로 */
//						|| szYD_DN_STK_BED_NO.equals("05")
//					) {
//						szYD_DN_STK_BED_NO			= "03";
//					}else{
//						szYD_DN_STK_BED_NO			= "01";
//					}
//				}else if( szYD_DN_STK_COL_GP.substring(2, 4).equals("RT") ) {		/* ROLLER TABLE */
//					if( szYD_DN_STK_BED_NO.equals("10") 							/* 10, 30, 50, 70베드를 03번지로 그 외의 베드는 01번지로 */
//							|| szYD_DN_STK_BED_NO.equals("30")
//							|| szYD_DN_STK_BED_NO.equals("50")
//							|| szYD_DN_STK_BED_NO.equals("70")
//						) {
//						szYD_DN_STK_BED_NO			= "03";
//						}else{
//							szYD_DN_STK_BED_NO			= "01";
//						}
//				}else if( szYD_DN_STK_COL_GP.substring(2, 4).equals("GJ") ) {		/* 가적장 */
//					if( szYD_DN_STK_BED_NO.equals("06") ) {							/* 06베드를 03번지로 그 외의 베드는 01번지로 */
//						szYD_DN_STK_BED_NO			= "03";
//					}else{
//						szYD_DN_STK_BED_NO			= "01";
//					}
//				}
//				
//				intDn_Grab_Y1_Value				= procY1CalFor1GrabCrn(intYD_DN_STK_BED_YAXIS, intYD_MTL_L, intYD_CRN_BEAM_L);
//			}
//			
//			szMsg ="["+szOperationName+"] 1Grab Crn[2호기:"+szYD_EQP_ID+"]의 차량이 아닌 경우 To위치 Y1좌표["+intDn_Grab_Y1_Value+"]";
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			//----------------------------------------------------------------------------
//			
//			//----------------------------------------------------------------------------
//			//	차량하차인 경우 From위치 Y1보정
//			//----------------------------------------------------------------------------
//			if( szYD_UP_STK_COL_GP.substring(2, 4).equals("PT") ) {
//				
//				if( ( szYD_UP_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) && szUP_CARD_NO.startsWith("P")	)						/* 출하Pallet */
//					|| ( szYD_UP_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_TS) && szUP_TRN_EQP_CD.substring(1, 3).equals("PT")	)		/* 구내운송Pallet */
//				) {
//					intUp_Grab_Y1_Value			= intYD_UP_STK_BED_YAXIS + (intDn_Grab_Y1_Value - intYD_DN_STK_BED_YAXIS);
//				}else{															/* Trailer */
//					intUp_Grab_Y1_Value			= intDn_Grab_Y1_Value;
//				}
//			}
//			//----------------------------------------------------------------------------
//			
//			//----------------------------------------------------------------------------
//			//	차량상차인 경우 To위치 Y1보정
//			//----------------------------------------------------------------------------
//			if( szYD_DN_STK_COL_GP.substring(2, 4).equals("PT") ) {
//				
//				if( ( szYD_DN_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) && szDN_CARD_NO.startsWith("P") )							/* 출하Pallet */
//						|| ( szYD_DN_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_TS) && szDN_TRN_EQP_CD.substring(1, 3).equals("PT") )	/* 구내운송Pallet */
//				) {
//					intDn_Grab_Y1_Value			= intYD_DN_STK_BED_YAXIS + (intUp_Grab_Y1_Value - intYD_UP_STK_BED_YAXIS);
//				}else{																/* Trailer */
//					intDn_Grab_Y1_Value			= intUp_Grab_Y1_Value;
//				}
//			}
//			//----------------------------------------------------------------------------
//
//			szCrane_Grab_Use_Gp				= "1";
//			
//			szMsg ="["+szOperationName+"] 1Grab Crn[2호기:"+szYD_EQP_ID+"]의 From위치 Y1좌표["+intUp_Grab_Y1_Value+"], To위치 Y1좌표["+intDn_Grab_Y1_Value+"]";
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			
//		//----------------------------------------------------------------------------
//		//	1호기 - 2Grab
//		//----------------------------------------------------------------------------
//		}else{														//1호기 - 2Grab
//			
//			int intYD_CRN_GRAB1_BEAM_L			= 10400;
//			int intYD_CRN_GRAB2_BEAM_L			=  6800;
//			int intYD_CRN_GRAB_GAP				=  2500;
//			int intYD_CRN_GRAB_GAP_ALPHA		= ((intYD_MTL_L - 21700) / 3) + intYD_CRN_GRAB_GAP;
//			int intYD_CRN_GRAB_STND				= (intYD_MTL_L - (intYD_CRN_GRAB1_BEAM_L + intYD_CRN_GRAB_GAP_ALPHA + intYD_CRN_GRAB2_BEAM_L + 2000)) / 2;
//			
//			if( intYD_CRN_GRAB_STND < 1 ) intYD_CRN_GRAB_STND = 0;
//			
//			//----------------------------------------------------------------------------
//			//	2Grab Crn - From위치 Y1좌표
//			//----------------------------------------------------------------------------
//			if( szYD_UP_STK_COL_GP.substring(2).matches("\\d\\d\\d\\d") 			/* 일반베드 */
//				|| szYD_UP_STK_COL_GP.substring(2, 4).equals("TF")					/* TRANSFER */
//				|| szYD_UP_STK_COL_GP.substring(2, 4).equals("RT")					/* ROLLER TABLE */
//				|| szYD_UP_STK_COL_GP.substring(2, 4).equals("GJ")					/* 가적장 */
//			) {
//				if( szYD_UP_STK_COL_GP.substring(2, 4).equals("TF") ) {				/* TRANSFER */
//					if( szYD_UP_STK_BED_NO.equals("06") 							/* 06, 05베드를 03번지로 그 외의 베드는 01번지로 */
//						|| szYD_UP_STK_BED_NO.equals("05")
//					) {
//						szYD_UP_STK_BED_NO			= "03";
//					}else{
//						szYD_UP_STK_BED_NO			= "01";
//					}
//				}else if( szYD_UP_STK_COL_GP.substring(2, 4).equals("RT") ) {		/* ROLLER TABLE */
//					if( szYD_UP_STK_BED_NO.equals("10") 							/* 10, 30, 50, 70베드를 03번지로 그 외의 베드는 01번지로 */
//							|| szYD_UP_STK_BED_NO.equals("30")
//							|| szYD_UP_STK_BED_NO.equals("50")
//							|| szYD_UP_STK_BED_NO.equals("70")
//						) {
//							szYD_UP_STK_BED_NO			= "03";
//						}else{
//							szYD_UP_STK_BED_NO			= "01";
//						}
//				}else if( szYD_UP_STK_COL_GP.substring(2, 4).equals("GJ") ) {		/* 가적장 */
//					if( szYD_UP_STK_BED_NO.equals("06") ) {							/* 06베드를 03번지로 그 외의 베드는 01번지로 */
//						szYD_UP_STK_BED_NO			= "03";
//					}else{
//						szYD_UP_STK_BED_NO			= "01";
//					}
//				}
//				
//				recPara			= JDTORecordFactory.getInstance().create();
//				
//				procY1Y2CalFor2GrabCrn(szYD_UP_STK_BED_NO,								/* 베드번지(01, 02, 03) */
//						intYD_UP_STK_BED_YAXIS,											/* DB기준 Y값 */
//						intYD_MTL_L,													/* 크레인작업재료들중 제일 긴 재료의 제품길이 */
//						intYD_CRN_GRAB1_BEAM_L,											/* #1Grab BEAM 길이 */
//						intYD_CRN_GRAB2_BEAM_L, 										/* #2Grab BEAM 길이 */
//			    		intYD_CRN_GRAB_GAP, 											/* BEAM 간격  */
//			    		intYD_CRN_GRAB_GAP_ALPHA, 										/* BEAM 간격 알파 */
//			    		intYD_CRN_GRAB_STND,											/* GRAB기준점 이동값*/
//			    		recPara
//			    );
//				
//				szCrane_Grab_Use_Gp			= ydDaoUtils.paraRecChkNull(recPara, "GRAB_USE_GP");
//				intUp_Grab_Y1_Value			= ydDaoUtils.paraRecChkNullInt(recPara, "Y1_VALUE");
//				intUp_Grab_Y2_Value			= ydDaoUtils.paraRecChkNullInt(recPara, "Y2_VALUE");
//				
//				szMsg ="["+szOperationName+"] 2Grab Crn[1호기:"+szYD_EQP_ID+"]의 차량이 아닌 경우 From위치 Y1좌표["+intUp_Grab_Y1_Value+"], Y2좌표["+intUp_Grab_Y2_Value+"], Grab Type["+szCrane_Grab_Use_Gp+"]";
//				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			}
//			//----------------------------------------------------------------------------
//			
//			//----------------------------------------------------------------------------
//			//	2Grab Crn - To위치 Y1좌표
//			//----------------------------------------------------------------------------
//			if( szYD_DN_STK_COL_GP.substring(2).matches("\\d\\d\\d\\d") 			/* 일반베드 */
//					|| szYD_DN_STK_COL_GP.substring(2, 4).equals("TF")				/* TRANSFER */
//					|| szYD_DN_STK_COL_GP.substring(2, 4).equals("RT")				/* ROLLER TABLE */
//					|| szYD_DN_STK_COL_GP.substring(2, 4).equals("GJ")				/* 가적장 */
//			) {
//				if( szYD_DN_STK_COL_GP.substring(2, 4).equals("TF") ) {				/* TRANSFER */
//					if( szYD_DN_STK_BED_NO.equals("06") 							/* 06, 05베드를 03번지로 그 외의 베드는 01번지로 */
//						|| szYD_DN_STK_BED_NO.equals("05")
//					) {
//						szYD_DN_STK_BED_NO			= "03";
//					}else{
//						szYD_DN_STK_BED_NO			= "01";
//					}
//				}else if( szYD_DN_STK_COL_GP.substring(2, 4).equals("RT") ) {		/* ROLLER TABLE */
//					if( szYD_DN_STK_BED_NO.equals("10") 							/* 10, 30, 50, 70베드를 03번지로 그 외의 베드는 01번지로 */
//							|| szYD_DN_STK_BED_NO.equals("30")
//							|| szYD_UP_STK_BED_NO.equals("50")
//							|| szYD_UP_STK_BED_NO.equals("70")
//						) {
//						szYD_DN_STK_BED_NO			= "03";
//						}else{
//							szYD_DN_STK_BED_NO			= "01";
//						}
//				}else if( szYD_DN_STK_COL_GP.substring(2, 4).equals("GJ") ) {		/* 가적장 */
//					if( szYD_DN_STK_BED_NO.equals("06") ) {							/* 06베드를 03번지로 그 외의 베드는 01번지로 */
//						szYD_DN_STK_BED_NO			= "03";
//					}else{
//						szYD_DN_STK_BED_NO			= "01";
//					}
//				}
//				
//				recPara			= JDTORecordFactory.getInstance().create();
//				
//				procY1Y2CalFor2GrabCrn(szYD_DN_STK_BED_NO,								/* 베드번지(01, 02, 03) */
//						intYD_DN_STK_BED_YAXIS,											/* DB기준 Y값 */
//						intYD_MTL_L,													/* 크레인작업재료들중 제일 긴 재료의 제품길이 */
//						intYD_CRN_GRAB1_BEAM_L,											/* #1Grab BEAM 길이 */
//						intYD_CRN_GRAB2_BEAM_L, 										/* #2Grab BEAM 길이 */
//			    		intYD_CRN_GRAB_GAP, 											/* BEAM 간격  */
//			    		intYD_CRN_GRAB_GAP_ALPHA, 										/* BEAM 간격 알파 */
//			    		intYD_CRN_GRAB_STND,											/* GRAB기준점 이동값*/
//			    		recPara
//			    );
//				
//				szCrane_Grab_Use_Gp			= ydDaoUtils.paraRecChkNull(recPara, "GRAB_USE_GP");
//				intDn_Grab_Y1_Value			= ydDaoUtils.paraRecChkNullInt(recPara, "Y1_VALUE");
//				intDn_Grab_Y2_Value			= ydDaoUtils.paraRecChkNullInt(recPara, "Y2_VALUE");
//				
//				szMsg ="["+szOperationName+"] 2Grab Crn[1호기:"+szYD_EQP_ID+"]의 차량이 아닌 경우 To위치 Y1좌표["+intDn_Grab_Y1_Value+"], Y2좌표["+intDn_Grab_Y2_Value+"], Grab Type["+szCrane_Grab_Use_Gp+"]";
//				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			}
//			//----------------------------------------------------------------------------
//			
//			//----------------------------------------------------------------------------
//			//	차량하차인 경우 From위치 Y1보정
//			//----------------------------------------------------------------------------
//			if( szYD_UP_STK_COL_GP.substring(2, 4).equals("PT") ) {
//				
//				if( ( szYD_UP_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) && szUP_CARD_NO.startsWith("P")	)					/* 출하Pallet */
//					|| ( szYD_UP_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_TS) && szUP_TRN_EQP_CD.substring(1, 3).equals("PT") )	/* 구내운송Pallet */
//				) {
//					if( szCrane_Grab_Use_Gp.equals("1") ) {
//						intUp_Grab_Y1_Value			= intYD_UP_STK_BED_YAXIS + (intDn_Grab_Y1_Value - intYD_DN_STK_BED_YAXIS);
//					}else if( szCrane_Grab_Use_Gp.equals("2") ) {
//						intUp_Grab_Y2_Value			= intYD_UP_STK_BED_YAXIS + (intDn_Grab_Y2_Value - intYD_DN_STK_BED_YAXIS);
//					}else if( szCrane_Grab_Use_Gp.equals("3") ) {
//						intUp_Grab_Y1_Value			= intYD_UP_STK_BED_YAXIS + (intDn_Grab_Y1_Value - intYD_DN_STK_BED_YAXIS);
//						intUp_Grab_Y2_Value			= intYD_UP_STK_BED_YAXIS + (intDn_Grab_Y2_Value - intYD_DN_STK_BED_YAXIS);
//						
//					}
//					
//				}else{															/* Trailer */
//					if( szCrane_Grab_Use_Gp.equals("1") ) {
//						intUp_Grab_Y1_Value			= intDn_Grab_Y1_Value;
//					}else if( szCrane_Grab_Use_Gp.equals("2") ) {
//						intUp_Grab_Y2_Value			= intDn_Grab_Y2_Value;
//					}else if( szCrane_Grab_Use_Gp.equals("3") ) {
//						intUp_Grab_Y1_Value			= intDn_Grab_Y1_Value;
//						intUp_Grab_Y2_Value			= intDn_Grab_Y2_Value;
//					}
//				}
//			}
//			//----------------------------------------------------------------------------
//			
//			
//			//----------------------------------------------------------------------------
//			//	차량상차인 경우 To위치 Y1보정
//			//----------------------------------------------------------------------------
//			if( szYD_DN_STK_COL_GP.substring(2, 4).equals("PT") ) {
//				
//				if( ( szYD_DN_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) && szDN_CARD_NO.startsWith("P") )							/* 출하Pallet */
//						|| ( szYD_DN_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_TS) && szDN_TRN_EQP_CD.substring(1, 3).equals("PT") )	/* 구내운송Pallet */
//				) {
//					if( szCrane_Grab_Use_Gp.equals("1") ) {
//						intDn_Grab_Y1_Value			= intYD_DN_STK_BED_YAXIS + (intUp_Grab_Y1_Value - intYD_UP_STK_BED_YAXIS);
//					}else if( szCrane_Grab_Use_Gp.equals("2") ) {
//						intDn_Grab_Y2_Value			= intYD_DN_STK_BED_YAXIS + (intUp_Grab_Y2_Value - intYD_UP_STK_BED_YAXIS);
//					}else if( szCrane_Grab_Use_Gp.equals("3") ) {
//						intDn_Grab_Y1_Value			= intYD_DN_STK_BED_YAXIS + (intUp_Grab_Y1_Value - intYD_UP_STK_BED_YAXIS);
//						intDn_Grab_Y2_Value			= intYD_DN_STK_BED_YAXIS + (intUp_Grab_Y2_Value - intYD_UP_STK_BED_YAXIS);
//						
//					}
//				}else{																/* Trailer */
//					if( szCrane_Grab_Use_Gp.equals("1") ) {
//						intDn_Grab_Y1_Value			= intUp_Grab_Y1_Value;
//					}else if( szCrane_Grab_Use_Gp.equals("2") ) {
//						intDn_Grab_Y2_Value			= intUp_Grab_Y2_Value;
//					}else if( szCrane_Grab_Use_Gp.equals("3") ) {
//						intDn_Grab_Y1_Value			= intUp_Grab_Y1_Value;
//						intDn_Grab_Y2_Value			= intUp_Grab_Y2_Value;
//					}
//				}
//			}
//			
//			szMsg ="["+szOperationName+"] 2Grab Crn[1호기:"+szYD_EQP_ID+"]의 From위치 Y1좌표["+intUp_Grab_Y1_Value+"], Y2좌표["+intUp_Grab_Y2_Value+"], Grab Type["+szCrane_Grab_Use_Gp+"]";
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			
//			szMsg ="["+szOperationName+"] 2Grab Crn[1호기:"+szYD_EQP_ID+"]의 To위치 Y1좌표["+intDn_Grab_Y1_Value+"], Y2좌표["+intDn_Grab_Y2_Value+"], Grab Type["+szCrane_Grab_Use_Gp+"]";
//			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
//			
//			//----------------------------------------------------------------------------
//		}
//		
//		//----------------------------------------------------------------------------
//		
//		recOutPara.setField("Crane_Grab_Use_Gp", 		szCrane_Grab_Use_Gp);
//		recOutPara.setField("Up_Grab_X_Value", 			szUp_Grab_X_Value);
//		recOutPara.setField("Up_Grab_Y_Value", 			szUp_Grab_Y_Value);
//		recOutPara.setField("Up_Grab_Y1_Value", 		String.valueOf(intUp_Grab_Y1_Value));
//		recOutPara.setField("Up_Grab_Y2_Value", 		String.valueOf(intUp_Grab_Y2_Value));
//		recOutPara.setField("Dn_Grab_X_Value", 			szDn_Grab_X_Value);
//		recOutPara.setField("Dn_Grab_Y_Value", 			szDn_Grab_Y_Value);
//		recOutPara.setField("Dn_Grab_Y1_Value", 		String.valueOf(intDn_Grab_Y1_Value));
//		recOutPara.setField("Dn_Grab_Y2_Value", 		String.valueOf(intDn_Grab_Y2_Value));
//		//---------------------------------------------------------------------------------------------------------
//		
//    	return YdConstant.RETN_CD_SUCCESS;
    }
    
    /**
     * 1 Grab Y1좌표 구하는 메소드
     * @param szYD_STK_BED_NO
     * @param intYD_STK_BED_YAXIS
     * @param intYD_MTL_L
     * @param intYD_CRN_BEAM_L
     * @param intYD_CRN_BEAM_L_MAX
     * @param szYD_STK_COL_BED_L_TP
     * @return
     */
    public static int procY1CalFor1GrabCrn(
    		int intYD_STK_BED_YAXIS, 				/* DB기준 Y값 */
    		int intYD_MTL_L, 						/* 크레인작업재료들중 제일 긴 재료의 제품길이 */
    		int intYD_CRN_BEAM_L 					/* BEAM 길이 */
    ) {
    	int intY1_VALUE				= 0;
    	
    	if( intYD_CRN_BEAM_L <= intYD_MTL_L ) {
    		intY1_VALUE			= intYD_STK_BED_YAXIS + (intYD_MTL_L / 2);
    	} else {
    		intY1_VALUE			= intYD_STK_BED_YAXIS + (intYD_CRN_BEAM_L / 2);
    	}
    	
    	return intY1_VALUE;
    }
    
     /**
     * 2 Grab Y1, Y2좌표 구하는 메소드
     * @param szYD_STK_BED_NO
     * @param intYD_STK_BED_YAXIS
     * @param intYD_MTL_L
     * @param intYD_CRN_GRAB1_BEAM_L
     * @param intYD_CRN_GRAB2_BEAM_L
     * @param intYD_CRN_GRAB_GAP
     * @param intYD_CRN_GRAB_GAP_ALPHA
     * @param szYD_STK_COL_BED_L_TP
     * @param recOutPara
     * @throws JDTOException
     */
    public static void procY1Y2CalFor2GrabCrn(String szYD_STK_BED_NO, 			/* 베드번지(01, 02, 03) */
    		int intYD_STK_BED_YAXIS, 											/* DB기준 Y값 */
    		int intYD_MTL_L, 													/* 크레인작업재료들중 제일 긴 재료의 제품길이 */
    		int intYD_CRN_GRAB1_BEAM_L, 										/* #1Grab BEAM 길이 */
    		int intYD_CRN_GRAB2_BEAM_L, 										/* #2Grab BEAM 길이 */
    		int intYD_CRN_GRAB_GAP, 											/* BEAM 간격  */
    		int intYD_CRN_GRAB_GAP_ALPHA, 										/* BEAM 간격 알파 */
    		int intYD_CRN_GRAB_STND,											/* GRAB기준점 이동값*/
    		JDTORecord	recOutPara 
    ) throws JDTOException {
    	
    	String szGrab_Use_Gp			= null;
    	int	intY1_VALUE					= 0;
    	int intY2_VALUE					= 0;
   
    	/*
    	 * 오주원 주임 수정사항 반영구간.
    	 */
    	if( szYD_STK_BED_NO.equals("01") || 
    		szYD_STK_BED_NO.equals("02") ){
	    	if( intYD_MTL_L < 12401  ) {
	    		szGrab_Use_Gp			= "1";
	    		if( intYD_CRN_GRAB1_BEAM_L < intYD_MTL_L ) {
	    			intY1_VALUE			= intYD_STK_BED_YAXIS + (intYD_MTL_L / 2);
    	    	} else {
    	    		intY1_VALUE			= intYD_STK_BED_YAXIS + (intYD_CRN_GRAB1_BEAM_L / 2);
    	    	}
	    	}else if( intYD_MTL_L < 19701 ) {
	    		szGrab_Use_Gp			= "3";
	    		intY1_VALUE				= intYD_STK_BED_YAXIS + (intYD_CRN_GRAB1_BEAM_L / 2);
	    		intY2_VALUE				= intYD_STK_BED_YAXIS + intYD_CRN_GRAB1_BEAM_L + intYD_CRN_GRAB_GAP + (intYD_CRN_GRAB2_BEAM_L / 2);
	    	}else if( intYD_MTL_L < 21701 ) {
	    		szGrab_Use_Gp			= "3";
	    		intY1_VALUE				= intYD_STK_BED_YAXIS + (intYD_CRN_GRAB1_BEAM_L / 2);
	    		intY2_VALUE				= intY1_VALUE + (intYD_CRN_GRAB1_BEAM_L / 2) + intYD_CRN_GRAB_GAP + ((intYD_MTL_L - intYD_CRN_GRAB1_BEAM_L - intYD_CRN_GRAB_GAP) / 2);	
	    	}else{
	    		szGrab_Use_Gp			= "3";
	    		intY1_VALUE				= intYD_STK_BED_YAXIS + intYD_CRN_GRAB_STND + (intYD_CRN_GRAB1_BEAM_L / 2);
	    		intY2_VALUE				= intYD_STK_BED_YAXIS + intYD_CRN_GRAB_STND + intYD_CRN_GRAB1_BEAM_L + intYD_CRN_GRAB_GAP_ALPHA + ((intYD_CRN_GRAB2_BEAM_L + 2000) / 2);
	    	}
    	}else{
    			szGrab_Use_Gp			= "2";
    			if( intYD_CRN_GRAB2_BEAM_L < intYD_MTL_L ) {
    				intY2_VALUE			= intYD_STK_BED_YAXIS + (intYD_MTL_L / 2);
    	    	} else {
    	    		intY2_VALUE			= intYD_STK_BED_YAXIS + (intYD_CRN_GRAB2_BEAM_L / 2);
    	    	}
    	}
    	
    	recOutPara.setField("Y1_VALUE", 			String.valueOf(intY1_VALUE));
    	recOutPara.setField("Y2_VALUE", 			String.valueOf(intY2_VALUE));
    	recOutPara.setField("GRAB_USE_GP", 			szGrab_Use_Gp);
    }
    
    /**
     * X축값구하기(후판제품)
     * @param intYD_STK_BED_XAXIS
     * @param intYD_MTL_W
     * @param intYD_STK_BED_W_MAX
     * @param szYD_STK_COL_GP
     * @return
     */
    public static int procXCalForPlateYd(int intYD_STK_BED_XAXIS, 	/* DB기준 X축값 */
    		int intYD_MTL_W, 										/* 가장 긴 재료의 폭 값 */
    		int intYD_STK_BED_W_MAX, 								/* 베드폭 */
    		String szYD_STK_COL_GP,									/* 적치열구분 */
    		String szYD_DN_STK_BED_W_GP,                        	/* 야드적치Bed폭구분 */
    		String szYD_DN_STK_BED_WHIO_STAT 						/* 야드적치Bed상태구분 */
    		) {	
    	
    	String szOperationName		= "X축값구하기(후판제품)";
    	String szMethodName			= "procXCalForPlateYd";
    	String szLogMsg				= null;
    	
    	int intXValue = 0;
    	
    	try{
	    	
	    	if("G".equals(szYD_DN_STK_BED_WHIO_STAT)){		// 가적장
	    		if("S".equals(szYD_DN_STK_BED_W_GP)){		// 협폭
	    			intXValue			= intYD_STK_BED_XAXIS + 1050;
	    		}else if("L".equals(szYD_DN_STK_BED_W_GP)){	// 광폭
	    			intXValue			= intYD_STK_BED_XAXIS + 2400;
	    		}else{										// 중폭
	    			intXValue			= intYD_STK_BED_XAXIS +  1700;
	    		}
	    		
	    		szLogMsg="["+szOperationName+":"+szMethodName+"] 가적베드 좌표값 계산="+szYD_DN_STK_BED_W_GP +"="+intYD_STK_BED_XAXIS+"="+intXValue;
	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    	}else{
		    	if( szYD_STK_COL_GP.substring(2, 4).equals("TF") ) {
		    		intXValue				= intYD_STK_BED_XAXIS - (intYD_MTL_W / 2);
		    	}else if( szYD_STK_COL_GP.substring(2, 4).equals("PT") ) {
		    		intXValue				= intYD_STK_BED_XAXIS;
		    	}else if( szYD_STK_COL_GP.substring(2, 4).equals("GJ") ) {
		    		//intXValue				= intYD_STK_BED_XAXIS;
		    		//2010.05.04 윤재광 수정 
		    		intXValue				= intYD_STK_BED_XAXIS + (intYD_MTL_W / 2);
		    	}else{
		    		intXValue				= intYD_STK_BED_XAXIS + (intYD_MTL_W / 2);
		    	}
	    	}
	    	
    	}catch(Exception ex) {
		}
    	return intXValue;
    }
    
    /**
     * Y축값구하기(후판제품)
     * @param YD_STK_BED_YAXIS
     * @param YD_MTL_L
     * @param szYD_EQP_ID
     * @param szYD_STK_BED_L_GP
     * @param szYD_STK_BED_NO
     * @return
     */
    public static int procYCalForPlateYd(int YD_STK_BED_YAXIS, 	/* DB기준 Y축값 */
    		int YD_MTL_L, 										/* 가장 긴 재료의 길이 값 */
    		String szYD_EQP_ID, 								/* 크레인 설비 ID */
    		String szYD_STK_BED_L_GP, 							/* 길이구분 (S:단척, M:중척, L:장척, X:초장척 */
    		String szYD_STK_BED_NO								/* 베드번호(01번지, 02번지, 03번지) */
    		) {							
    	String szOperationName		= "Y축값구하기(후판제품)";
    	String szMethodName			= "procYCalForPlateYd";
    	String szLogMsg				= null;
    	int intYValue = 0;
    	
    	szLogMsg="["+szOperationName+":"+szMethodName+"] 메소드 시작 - DB기준Y값["+YD_STK_BED_YAXIS+"], 재료길이["+YD_MTL_L+"], 크레인설비ID["+szYD_EQP_ID+"], 길이구분["+szYD_STK_BED_L_GP+"], 베드번호["+szYD_STK_BED_NO+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    	
    	String szCRN_NO		= szYD_EQP_ID.substring(5);

//DONG_INSERT    	
    	if( szCRN_NO.equals("1")) {				//1호기 크레인(2Grab)
//    		if( YD_MTL_L <= 9200 ) {
//    			
//    		}else if( YD_MTL_L <= 12000 ) {
//    			
//    		}else if( YD_MTL_L <= 22000 ) {
//    			
//    		}else if( YD_MTL_L <= 23500 ) {
//    			
//    		}else{
//    			
//    		}
    		
    		szLogMsg="["+szOperationName+":"+szMethodName+"] 1호기 크레인은 Y축좌표값은 0으로 설정";
    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    	}else if( szYD_EQP_ID.equals("2")) {	//2호기 크레인(1Grab)
    		if( szYD_STK_BED_NO.equals("01") ) {					// 1번지
    			if( szYD_STK_BED_L_GP.equals("S") ) {					//단척제품
    				intYValue = YD_STK_BED_YAXIS + 2000 + (YD_MTL_L / 2);	//DB기준값 + 2000 + (제품길이/2)
    			}else{													//그외
    				intYValue = YD_STK_BED_YAXIS + (YD_MTL_L / 2);			//DB기준값 + (제품길이/2)
    			}
    		}else if( szYD_STK_BED_NO.equals("02") ) {				//2번지
    			if( szYD_STK_BED_L_GP.equals("S") ) {					//단척제품
    				intYValue = YD_STK_BED_YAXIS + 4500 + (YD_MTL_L / 2);	//DB기준값 + 4500 + (제품길이/2)
    			}else{													//그외
    				intYValue = YD_STK_BED_YAXIS + (YD_MTL_L / 2);			//DB기준값 + (제품길이/2)
    			}
    		}else if( szYD_STK_BED_NO.equals("03") ) {				//3번지
    			if( szYD_STK_BED_L_GP.equals("M") ) {					//중척제품
    				intYValue = YD_STK_BED_YAXIS - 4800 + (YD_MTL_L / 2);	//DB기준값 + 4500 + (제품길이/2)
    			}else{													//그외
    				intYValue = YD_STK_BED_YAXIS + (YD_MTL_L / 2);			//DB기준값 + (제품길이/2)
    			}
    		}
    		
    		szLogMsg="["+szOperationName+":"+szMethodName+"] 2호기 크레인은 Y축좌표값["+intYValue+"]으로 설정";
    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    	}
    	
    	szLogMsg="["+szOperationName+":"+szMethodName+"] 메소드 끝 - 반환 Y값["+intYValue+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    	
    	return intYValue;
    }
    
    /**
     * RT상의 베드에 대응하는 야드베드번지 반환하는 메소드
     * @param szYD_STK_BED_NO
     * @return
     */
    public static String getYdBedNoFromRtBedNo(String szYD_STK_BED_NO) {
//DONG_INSERT :OK   	
    	if( szYD_STK_BED_NO.equals("10") 
			|| szYD_STK_BED_NO.equals("30") 
			|| szYD_STK_BED_NO.equals("50") 
			|| szYD_STK_BED_NO.equals("70") ) {
			return "03";
		} else if( szYD_STK_BED_NO.equals("90") 
				|| szYD_STK_BED_NO.equals("B0")) {
			return "03";
		} else if( szYD_STK_BED_NO.equals("85")
				|| szYD_STK_BED_NO.equals("A5")) {
			return "04";
		} else if( szYD_STK_BED_NO.equals("95")
				|| szYD_STK_BED_NO.equals("B5")) {
			return "02";
		} else {	
			return "01";
		}
    }
    
    /**
     * TF상의 베드에 대응하는 야드베드번지 반환하는 메소드
     * @param szYD_STK_BED_NO
     * @return
     */
    public static String getYdBedNoFromTfBedNo(String szYD_STK_BED_NO) {
    	if( szYD_STK_BED_NO.equals("06") || 
    		szYD_STK_BED_NO.equals("05") ){
			return "03";
		}else{
			return "01";
		}
    }
    
    /**
     * 스판별 차량통로 반환하는 메소드
     * @param szYD_STK_COL_GP
     * @return
     */
    public static String getCarPathForPlateYd(String szYD_STK_COL_GP) {
    	String szPATH = "";
    	String szYD_SPAN_NO			= szYD_STK_COL_GP.substring(2, 4);
    	
    	if( szYD_SPAN_NO.equals(YdConstant.SPAN_ORDER_NEW_04) || szYD_SPAN_NO.equals(YdConstant.SPAN_ORDER_NEW_05) || szYD_SPAN_NO.equals(YdConstant.SPAN_ORDER_NEW_06) ) {			//04, 05, 06스판
    		szPATH = "A";
    	}else if( szYD_SPAN_NO.equals(YdConstant.SPAN_ORDER_NEW_07) || szYD_SPAN_NO.equals(YdConstant.SPAN_ORDER_NEW_TP) ) {	//07스판
    		szPATH = "B";
    	}
    	return szPATH;
    }
    
    /**
     * 통로별 크레인스케줄코드 반환하는 메소드
     * @param szYD_STK_COL_GP
     * @return
     */
    public static String getCarLdSchCd(String szYD_STK_COL_GP) {
    	String szYD_SCH_CD = "";
    	
    	String szPATH = getCarPathForPlateYd(szYD_STK_COL_GP);
    	
    	if( szPATH.equals("A") ) {				//A통로
    		szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01LM";
    	}else if( szPATH.equals("B") ) {		//B통로
    		szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT02LM";
    	}
    	return szYD_SCH_CD;
    }
    
    /**
     * 폭/두께에 따른 크레인작업가능매수
     * @param dblYD_MTL_T
     * @param dblYD_MTL_W
     * @return
     */
    public static int getCrnWrkableShBasedOnWT(double dblYD_MTL_T, double dblYD_MTL_W ) {
    	int intCrnWrkableSh	= 0;
    	if( dblYD_MTL_W <= 2100 ) {
    		if( dblYD_MTL_T <= 8  ) {
    			intCrnWrkableSh = 5;
    		}else if( dblYD_MTL_T <= 10  ) {
    			intCrnWrkableSh = 4;
    		}else if( dblYD_MTL_T <= 20  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 30  ) {
    			intCrnWrkableSh = 2;
    		}else{
    			intCrnWrkableSh = 1;
    		}
    	}else if( dblYD_MTL_W <= 3450 ) {
    		if( dblYD_MTL_T <= 8  ) {
    			intCrnWrkableSh = 5;
    		}else if( dblYD_MTL_T <= 10  ) {
    			intCrnWrkableSh = 4;
    		}else if( dblYD_MTL_T <= 20  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 30  ) {
    			intCrnWrkableSh = 2;
    		}else{
    			intCrnWrkableSh = 1;
    		}
    	}else if( dblYD_MTL_W <= 4800 ) {
    		if( dblYD_MTL_T <= 8  ) {
    			intCrnWrkableSh = 5;
    		}else if( dblYD_MTL_T <= 10  ) {
    			intCrnWrkableSh = 4;
    		}else if( dblYD_MTL_T <= 20  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 30  ) {
    			intCrnWrkableSh = 2;
    		}else{
    			intCrnWrkableSh = 1;
    		}
    	}
    	
    	return intCrnWrkableSh;
    }
    
    /**
     * 폭/두께에 따른 크레인작업가능매수(A/B 동)
     * @param dblYD_MTL_T
     * @param dblYD_MTL_W
     * @return
     */
    public static int getCrnWrkableShBasedOnWT_AB(double dblYD_MTL_T, double dblYD_MTL_W ) {
    	int intCrnWrkableSh	= 0;
    	if( dblYD_MTL_W <= 2100 ) {
    		if( dblYD_MTL_T <= 7  ) {
    			intCrnWrkableSh = 6;
    		}else if( dblYD_MTL_T <= 8  ) {
    			intCrnWrkableSh = 5;
    		}else if( dblYD_MTL_T <= 10  ) {
    			intCrnWrkableSh = 5;
    		}else if( dblYD_MTL_T <= 12  ) {
    			intCrnWrkableSh = 4;
    		}else if( dblYD_MTL_T <= 16  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 20  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 25  ) {
    			intCrnWrkableSh = 2;	
    		}else{
    			intCrnWrkableSh = 1;
    		}
    	}else if( dblYD_MTL_W <= 2800 ) {
    		if( dblYD_MTL_T <= 7  ) {
    			intCrnWrkableSh = 5;
    		}else if( dblYD_MTL_T <= 8  ) {
    			intCrnWrkableSh = 4;
    		}else if( dblYD_MTL_T <= 10  ) {
    			intCrnWrkableSh = 4;
    		}else if( dblYD_MTL_T <= 12  ) {
    			intCrnWrkableSh = 4;
    		}else if( dblYD_MTL_T <= 16  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 20  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 25  ) {
    			intCrnWrkableSh = 2;	
    		}else{
    			intCrnWrkableSh = 1;
    		}
    	}else if( dblYD_MTL_W <= 3400 ) {
    		if( dblYD_MTL_T <= 7  ) {
    			intCrnWrkableSh = 4;
    		}else if( dblYD_MTL_T <= 8  ) {
    			intCrnWrkableSh = 4;
    		}else if( dblYD_MTL_T <= 10  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 12  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 16  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 20  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 25  ) {
    			intCrnWrkableSh = 1;	
    		}else{
    			intCrnWrkableSh = 1;
    		}
    	}else if( dblYD_MTL_W <= 4000 ) {
    		if( dblYD_MTL_T <= 7  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 8  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 10  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 12  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 16  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 20  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 25  ) {
    			intCrnWrkableSh = 1;	
    		}else{
    			intCrnWrkableSh = 1;
    		}
    	}else{
    		if( dblYD_MTL_T <= 7  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 8  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 10  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 12  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 16  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 20  ) {
    			intCrnWrkableSh = 1;
    		}else if( dblYD_MTL_T <= 25  ) {
    			intCrnWrkableSh = 1;	
    		}else{
    			intCrnWrkableSh = 1;
    		}
    	}
    	
    	return intCrnWrkableSh;
    }
    
    /**
     * 폭/두께에 따른 크레인작업가능매수(C/D 동)
     * @param dblYD_MTL_T
     * @param dblYD_MTL_W
     * @return
     */
    public static int getCrnWrkableShBasedOnWT_CD(double dblYD_MTL_T, double dblYD_MTL_W ) {
    	int intCrnWrkableSh	= 0;
    	if( dblYD_MTL_W <= 2100 ) {
    		if( dblYD_MTL_T <= 7  ) {
    			intCrnWrkableSh = 6;
    		}else if( dblYD_MTL_T <= 8  ) {
    			intCrnWrkableSh = 5;
    		}else if( dblYD_MTL_T <= 10  ) {
    			intCrnWrkableSh = 4;
    		}else if( dblYD_MTL_T <= 12  ) {
    			intCrnWrkableSh = 4;
    		}else if( dblYD_MTL_T <= 16  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 20  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 25  ) {
    			intCrnWrkableSh = 2;	
    		}else{
    			intCrnWrkableSh = 1;
    		}
    	}else if( dblYD_MTL_W <= 2800 ) {
    		if( dblYD_MTL_T <= 7  ) {
    			intCrnWrkableSh = 6;
    		}else if( dblYD_MTL_T <= 8  ) {
    			intCrnWrkableSh = 5;
    		}else if( dblYD_MTL_T <= 10  ) {
    			intCrnWrkableSh = 4;
    		}else if( dblYD_MTL_T <= 12  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 16  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 20  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 25  ) {
    			intCrnWrkableSh = 2;	
    		}else{
    			intCrnWrkableSh = 1;
    		}
    	}else if( dblYD_MTL_W <= 3400 ) {
    		if( dblYD_MTL_T <= 7  ) {
    			intCrnWrkableSh = 5;
    		}else if( dblYD_MTL_T <= 8  ) {
    			intCrnWrkableSh = 4;
    		}else if( dblYD_MTL_T <= 10  ) {
    			intCrnWrkableSh = 4;
    		}else if( dblYD_MTL_T <= 12  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 16  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 20  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 25  ) {
    			intCrnWrkableSh = 1;	
    		}else{
    			intCrnWrkableSh = 1;
    		}
    	}else if( dblYD_MTL_W <= 4000 ) {
    		if( dblYD_MTL_T <= 7  ) {
    			intCrnWrkableSh = 4;
    		}else if( dblYD_MTL_T <= 8  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 10  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 12  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 16  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 20  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 25  ) {
    			intCrnWrkableSh = 1;	
    		}else{
    			intCrnWrkableSh = 1;
    		}
    	}else{
    		if( dblYD_MTL_T <= 7  ) {
    			intCrnWrkableSh = 3;
    		}else if( dblYD_MTL_T <= 8  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 10  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 12  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 16  ) {
    			intCrnWrkableSh = 2;
    		}else if( dblYD_MTL_T <= 20  ) {
    			intCrnWrkableSh = 1;
    		}else if( dblYD_MTL_T <= 25  ) {
    			intCrnWrkableSh = 1;	
    		}else{
    			intCrnWrkableSh = 1;
    		}
    	}
    	
    	return intCrnWrkableSh;
    }
    /** 
	 * 후판제품길이/폭구분결정
	 * @param inRecord
	 * @param YD_MTL_L
     * @param YD_MTL_W
     * @param STRCHAR_CUST_CD
     * @param STRCHAR_ORD_YEOJAE_GP
	 * @throws JDTOException
	 */
	
	public static String getWTLGp(JDTORecord inRecord) {
		
		String szMethodName		= "getWTLGp";
		String szOperationName 	= "후판제품길이/폭구분결정";
		String szMsg        	= null;
		String szYD_MTL_W_GP	= null;
		String szYD_MTL_L_GP	= null;
		
		int intRtnVal			= 0;
		int    intMtlL			= 0;
		double  dblMtlW			= 0;   
	
		JDTORecordSet rsResult	= null;
		JDTORecord recInTemp   	= null;
		JDTORecord recOutTemp  	= null;
		
		String sCUST_CD     	= "";
		String sCUST_GP     	= "";
		String sORD_YEOJAE_GP   = "";
		String sCUST_CD_SINGLE  = "";
		String sYD_STRCHAR_GRP_CD = "";
		
		YdEqpDao	ydEqpDao 	= new YdEqpDao();
		
		try {
			intMtlL 			= ydDaoUtils.paraRecChkNullInt(inRecord,"YD_MTL_L");
			dblMtlW 			= ydDaoUtils.paraRecChkNullDouble(inRecord,"YD_MTL_W");
			sCUST_CD 			= ydDaoUtils.paraRecChkNull(inRecord,"STRCHAR_CUST_CD");
			sORD_YEOJAE_GP		= ydDaoUtils.paraRecChkNull(inRecord,"STRCHAR_ORD_YEOJAE_GP");
			sCUST_CD_SINGLE		= ydDaoUtils.paraRecChkNull(inRecord,"STRCHAR_CUST_CD_SINGLE");
			sYD_STRCHAR_GRP_CD	= StringHelper.evl(inRecord.getFieldString("YD_STRCHAR_GRP_CD"),"KKKK");
			
			szMsg = "["+szOperationName+"] 전달된 길이["+intMtlL+"], 폭["+dblMtlW+"], 고객사["+sCUST_CD+"] 속성 [" + sYD_STRCHAR_GRP_CD +"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
 			szMsg = "["+szOperationName+"] 전달된 길이["+ydDaoUtils.paraRecChkNull(inRecord,"YD_MTL_L")+"]" +
					", 폭["+ydDaoUtils.paraRecChkNull(inRecord,"YD_MTL_W")+"], 고객사["+sCUST_CD+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(sCUST_CD.equals("")) {           
				sCUST_GP = "A";
			} else {
				sCUST_GP = "Y";                 //대형고객사로 폭그룹 고정
			}
			
			/*
			 * 2016.09.22 윤재광
			 * 기준값 변경 : 소폭에 대해 S1,S2구분은 수출재에 대해서만 처리요
			 */
			String sCUST_GP_TMP = "";
			
			if(sYD_STRCHAR_GRP_CD.startsWith("E")) {           
				sCUST_GP_TMP = "Y";
			} else {
				sCUST_GP_TMP = "A";                 
			}
			
	       	//후판제품창고저장속성그룹폭그룹부여기준 	
        	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
        	recInTemp 	= JDTORecordFactory.getInstance().create();
        	recInTemp.setField("ORD_YEOJAE_GP" 	, sORD_YEOJAE_GP);            //신규주문은 무조건1	
        	recInTemp.setField("CUST_GP" 		, sCUST_GP_TMP);	
        	recInTemp.setField("W_GP" 			, ydDaoUtils.paraRecChkNull(inRecord,"YD_MTL_W"));	
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB651*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 651);
			if(intRtnVal <= 0) {
				szYD_MTL_W_GP	= "";		
			} else {
				rsResult.absolute(1);
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord());
				szYD_MTL_W_GP	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GDS_W_GP");
			}	

			if((sCUST_GP.equals("Y"))&&(sCUST_CD_SINGLE.equals("Y"))) {
				sCUST_GP = "Y"; 				//대형고객사,주문수량 * 두께 < 2000 로 폭그룹 고정	                                   
			} else {
				sCUST_GP = "A";
			}			
			
	       	//후판제품창고저장속성그룹길이그룹부여기준 	
        	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
        	recInTemp 	= JDTORecordFactory.getInstance().create();
        	recInTemp.setField("ORD_YEOJAE_GP" 	, sORD_YEOJAE_GP);            //신규주문은 무조건1
        	recInTemp.setField("CUST_GP" 		, sCUST_GP);	
        	recInTemp.setField("L_GP" 			, ydDaoUtils.paraRecChkNull(inRecord,"YD_MTL_L"));	
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB652*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 652);
			if(intRtnVal <= 0) {
				szYD_MTL_L_GP	= "";		
			} else {
				rsResult.absolute(1);
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord());
				szYD_MTL_L_GP	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GDS_L_GP");
			}
			
			/*
			 * 2016.04.04 윤재광
			 * - 이명운 과장 요청사항 반여
			 * - 수출재 파일링코드 길이구분 요청
			 * - U0 : 3000 ~ 5000
			 * - U1 : 5001 ~ 6800
			 */
			if(sYD_STRCHAR_GRP_CD.startsWith("E")){
				if(intMtlL >= 3000 && intMtlL <= 5000){
					szYD_MTL_L_GP = "U0";
				}else if(intMtlL >= 5001 && intMtlL <= 6800){
					szYD_MTL_L_GP = "U1";
				}
			}
			
			/*
			 * 2017.05.02 윤재광
			 * - 이명운 과장 요청사항 반여
			 * - F동 광폭/중척제품 12미터 이하만 M1으로 셋팅
			 * 2021.02.02 윤재광
			 * - 서윤요청사항에 의해 막음
			 */
			/*
			if(szYD_MTL_W_GP.startsWith("L")&&szYD_MTL_L_GP.startsWith("M")){
				if(intMtlL < 12001){
					szYD_MTL_L_GP = "M1";
				}
			}
			*/
			inRecord.setField("YD_MTL_W_GP"  , szYD_MTL_W_GP);	//야드재료폭구분
			inRecord.setField("YD_MTL_L_GP"  , szYD_MTL_L_GP);	//야드재료길이구분
			
			szMsg = "["+szOperationName+"] 결정된 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
		}catch(JDTOException ex) {
			szMsg = "["+szOperationName+"] 길이구분, 폭구분 결정 시 오류발생 - 메세지 : " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
		}
		return YdConstant.RETN_CD_SUCCESS;
	} //end of getWTLGp
	
	/** 
	 * 투입주문의 야드저장속성 결정
	 * @param String
	 * @throws JDTOException
	 * 작성자 : 석창화
	 * 작성일 : 2009.12.21
	 * 내 용  : OS주문투입실적 (PTYDJ004) 전문 수신 후 OS공통의 미투입 주문을 검색하여
	 *         PILING_CD, BOOK_OUT_LOC, 입고예정저장위치 등을 설정해준다. 
	 */
	
	public static String procPtOsCommUpdateByYdStrCharGrp(String sOrdNo, String sOrdDtl, String sFinalYn, String logId) {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.10 로그 개선  START
// procPtOsCommUpdateByYdStrCharGrp argument 에 logId 항목 추가 개선
// public static String procPtOsCommUpdateByYdStrCharGrp(String sOrdNo, String sOrdDtl, String sFinalYn) {
// 2024.09.10 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		PtOsCommDao ptOsCommDao 		= new PtOsCommDao();
		
		String szMethodName				= "procPtOsCommUpdateByYdStrCharGrp";
		String szOperationName 			= "투입주문의 야드저장속성 결정";
		String szMsg        			= null;
		String szMOD_CNT = "";
		String szRtnMsg			 = "";
		String sRECV_TELNO       = "";
		String sTO_CONTENT       = "";
		JDTORecordSet  rsOsComm 	= null;
		
		JDTORecord     recPara 		= null;
		JDTORecord     recPara1		= null;
		JDTORecord     recOsComm 	= null;
		
		int intRtnVal;
		int intRtnVal1;
		int intRtnVal2;
		
		JDTORecordSet 	outResult 	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
		JDTORecord 		inRecord 	= JDTORecordFactory.getInstance().create();
		JDTORecordSet 	outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
		
		JDTORecordSet   rsResultYN 	= null;	//FlagYn
		JDTORecord 		recInParaYN	= null;	//FlagYn
		JDTORecord 		recParaYN   = null;	//FlagYn
		int 			intRtnValYN	= 0;	//FlagYn
		String			FlagYn 		= "N";	//FlagYn
		YdPlateCommDAO 	commDao 		= new YdPlateCommDAO(); //FlagYn

        
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.10 로그 개선  START
// logId Empty 이면 logId 신규 생성 개선
if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "투입주문의 야드저장속성 결정(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.INFO, logId);

// 2024.09.10 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		try {
			
			szMsg = "["+szOperationName+"] 처리시작  ";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG, logId);
			
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.10 로그 개선  START
// procPtOsCommUpdateByYdStrCharGrpNextNew call 시  logId 항목 추가 개선
//			szRtnMsg = procPtOsCommUpdateByYdStrCharGrpNextNew(sOrdNo, sOrdDtl);		
			szRtnMsg = procPtOsCommUpdateByYdStrCharGrpNextNew(sOrdNo, sOrdDtl, logId);	

// 2024.09.10 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
			
			if(szRtnMsg != YdConstant.RETN_CD_SUCCESS) { 
// 2024.09.10 메세지 내용  procPtOsCommUpdateByYdStrCharGrp -> procPtOsCommUpdateByYdStrCharGrpNextNew 개선
//				szMsg="[ERROR] "+szClassName+"::"+szMethodName+"() PlateGdsYdUtil.procPtOsCommUpdateByYdStrCharGrp Error :" + szRtnMsg;
				szMsg="[ERROR] "+szClassName+"::"+szMethodName+"() PlateGdsYdUtil.procPtOsCommUpdateByYdStrCharGrpNextNew Error :" + szRtnMsg;
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);
				return YdConstant.RETN_CD_FAILURE; 
			}
			
			if("Y".equals(sFinalYn)){
				//------------------------------------------------------------------------------------------------------------
				//	SMS 전송 적용
				//------------------------------------------------------------------------------------------------------------
				YdEqpDao   ydEqpDao   = new YdEqpDao();
				outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
				inRecord 	= JDTORecordFactory.getInstance().create();
				outRecord  	= JDTORecordFactory.getInstance().create();
				String szAPPLY_YN 			= "N";
				
				inRecord.setField("REPR_CD_GP", "T00120");    //SMS 전송 적용
				
				/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
				intRtnVal = ydEqpDao.getYdEqp(inRecord, outResult, 999);
				if(intRtnVal > 0) {
					outResult.first();
					outRecord  = outResult.getRecord();
					szAPPLY_YN = outRecord.getFieldString("ITEM1");				
				}
				szMsg="SMS 전송 적용 " + szAPPLY_YN ;
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);			
				ydUtils.putLogNew(szOperationName, szMethodName, szMsg, YdConstant.DEBUG, logId);			
				
				if(szAPPLY_YN.equals("Y")) {	
					
					rsOsComm 	= JDTORecordFactory.getInstance().createRecordSet("OS");
					recOsComm 	= JDTORecordFactory.getInstance().create();
					/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPilingNullCnt*/
					intRtnVal1 = ptOsCommDao.getPtOsComm(inRecord, rsOsComm, 205);  //남은 OS 건수
					
					if (intRtnVal1 > 0) {

						rsOsComm.first();
						recOsComm = rsOsComm.getRecord();
						szMOD_CNT = recOsComm.getFieldString("MOD_CNT");
						
						/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB659*/
						intRtnVal2 = ydEqpDao.getYdEqp(inRecord, outResult1, 659);
						if(intRtnVal2 > 0) {
							for(int i = 1; i <= outResult1.size(); i++ ) {
								recPara = JDTORecordFactory.getInstance().create();
						
								outResult1.absolute(i);
								recPara = outResult1.getRecord();
							
								sRECV_TELNO	= recPara.getFieldString("RECV_TELNO");
								sTO_CONTENT = "신규주문"	+ szMOD_CNT + "이 등록되었습니다.";	// SMS 전송 내용
								
								if( sRECV_TELNO.length() > 8 ) {
									
									// 알림톡 전환 FLAG
									rsResultYN = JDTORecordFactory.getInstance().createRecordSet("");
									recInParaYN = JDTORecordFactory.getInstance().create();
									intRtnValYN = commDao.select(recInParaYN, rsResultYN, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.TalkFlagYN");	
									if(intRtnValYN <= 0) {
										szMsg = "["+szMethodName+"] 알림톡 전환 FLAG 조회 실패";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//										ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);
										ydUtils.putLogNew(szOperationName, szMethodName, szMsg, YdConstant.DEBUG, logId);			
									} else{
										szMsg = "["+szMethodName+"] 알림톡 전환 FLAG 조회 성공";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//										ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);
										ydUtils.putLogNew(szOperationName, szMethodName, szMsg, YdConstant.DEBUG, logId);
									}
									
									//레코드 추출
									rsResultYN.first();
									recParaYN = rsResultYN.getRecord();
									FlagYn 	  = ydDaoUtils.paraRecChkNull(recParaYN, "FLAG_YN");
									
									if("Y".equals(FlagYn)){
										MessageSenderTalk    sender = new MessageSenderTalk();
										
										recPara1 = JDTORecordFactory.getInstance().create();
										recPara1.setField("PHONE_NUM", new String(sRECV_TELNO));
										recPara1.setField("TMPL_CD", new String("CM1"));
										recPara1.setField("SND_MSG", new String("[현대제철 공지사항]\n" + sTO_CONTENT));
										recPara1.setField("SUBJECT", new String("파일링코드 미셋팅 알림"));
										recPara1.setField("SMS_SND_NUM", new String("0416801616"));
										recPara1.setField("RECV_ID","1522110");
										recPara1.setField("GROUP_ID","KaKao");
										recPara1.setField("PROGRAM_ID","udttalk");
										sender.sendTalk(recPara1);

									} else{
										recPara1 = JDTORecordFactory.getInstance().create();
										recPara1.setField("FROM_PHONE_NO", "0416801616");	
										recPara1.setField("TO_PHONE_NO", sRECV_TELNO);	
										recPara1.setField("TO_CONTENT", sTO_CONTENT);	
										
										szRtnMsg = updSmsMsgSend(recPara1);		     // SMS 송신
									}

									if(szRtnMsg != YdConstant.RETN_CD_SUCCESS) {
										szMsg="[ERROR] "+szClassName+"::"+szMethodName+"송신 Error :" + szRtnMsg;
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//										ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
										ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);			
									}
								} else{
									szMsg = "["+szMethodName+"] 전화번호 자릿수 8자리 미만["+ sRECV_TELNO.length() + "] 파일링코드 미셋팅건 신규 알림톡 송신 안함";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//									ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);
									ydUtils.putLogNew(szOperationName, szMethodName, szMsg, YdConstant.DEBUG, logId);
								}
							}	
						}
					}
				}	
			}
			
			szMsg = "["+szOperationName+"] 처리종료 ";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG, logId);
			
		}catch(Exception ex) {
			szMsg = "O/S신규주문"+szOperationName+"] 투입주문의 야드저장속성 결정 시 오류발생 - 메세지 : " + ex.getMessage();
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_CD_FAILURE;
		}
		

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.10 로그 개선  START
szMsg = "투입주문의 야드저장속성 결정(" + szMethodName + ") 완료";
ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.10 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
	} //end of procPtOsCommUpdateByYdStrCharGrp
	
	/** 
	 * 투입주문의 야드저장속성 결정(수정 bY 영업)
	 * @param String
	 * @throws JDTOException
	 * 작성자 : 박종호 
	 * 작성일 : 2023.11.07
	 * 내 용  : OS주문투입수정 (PTYDJ005) 전문 수신 후 OS공통의 기셋팅된 파일링코드를 변경해준다.
	 *         
	 */
	public static String procPtOsCommModifyByYdStrCharGrp(String sOrdNo, String sOrdDtl,String logId) {
		PtOsCommDao ptOsCommDao 		= new PtOsCommDao();
		
		String szMethodName				= "procPtOsCommModifyByYdStrCharGrp";
		String szOperationName 			= "수정주문의 야드저장속성 수정";
		String szMsg        			= null;
		String szMOD_CNT = "";
		String szRtnMsg			 = "";
		String sRECV_TELNO       = "";
		String sTO_CONTENT       = "";
		JDTORecordSet  rsOsComm 	= null;
		
		JDTORecord     recPara 		= null;
		JDTORecord     recPara1		= null;
		JDTORecord     recOsComm 	= null;
		
		int intRtnVal;
		int intRtnVal1;
		int intRtnVal2;
		
		JDTORecordSet 	outResult 	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
		JDTORecord 		inRecord 	= JDTORecordFactory.getInstance().create();
		JDTORecordSet 	outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
		
		JDTORecordSet   rsResultYN 	= null;	//FlagYn
		JDTORecord 		recInParaYN	= null;	//FlagYn
		JDTORecord 		recParaYN   = null;	//FlagYn
		int 			intRtnValYN	= 0;	//FlagYn
		String			FlagYn 		= "N";	//FlagYn
		YdPlateCommDAO 	commDao 		= new YdPlateCommDAO(); //FlagYn
		
		try {
			if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

			
			szMsg = "["+szOperationName+"] 처리시작  ";
			ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG, logId);
			
			szRtnMsg = procPtOsCommModifyByYdStrCharGrpNextNew(sOrdNo, sOrdDtl,logId);  //수정전문에 맞는 메소드로 수정		
			if(szRtnMsg != YdConstant.RETN_CD_SUCCESS) { 
				szMsg="[ERROR] "+szClassName+"::"+szMethodName+"() PlateGdsYdUtil.procPtOsCommUpdateByYdStrCharGrp Error :" + szRtnMsg;
				ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);
				return YdConstant.RETN_CD_FAILURE; 
			}
			
			szMsg = "["+szOperationName+"] 처리종료 ";
			ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG, logId);
			
		}catch(Exception ex) {
			szMsg = "O/S신규주문"+szOperationName+"] 변경주문의 야드저장속성 결정 시 오류발생 - 메세지 : " + ex.getMessage();
			ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_CD_FAILURE;
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	} //end of procPtOsCommModifyByYdStrCharGrp	
	

	/** 
	 * 투입주문의 야드저장속성 결정 신적용-화면에서 사용하기 위해
	 * @param String
	 * @throws JDTOException
	 * 작성자 : 석창화
	 * 작성일 : 2009.12.21
	 * 내 용  : OS주문투입실적 (PTYDJ004) 전문 수신 후 OS공통의 미투입 주문을 검색하여
	 *         PILING_CD, BOOK_OUT_LOC, 입고예정저장위치 등을 설정해준다. 
	 */
	public static String procPtOsCommUpdateByYdStrCharGrpNext() {
		
		PtOsCommDao ptOsCommDao 		= new PtOsCommDao();
		
		YdStrCharDao ydStrCharDao 		= new YdStrCharDao();
		YdStrCharGrpDao ydStrCharGrpDao = new YdStrCharGrpDao();
		
		String szMethodName				= "procPtOsCommUpdateByYdStrCharGrpNext";
		String szOperationName 			= "투입주문의 야드저장속성 결정next";
		String szMsg        			= null;
		
		String szDEST_CD 				= null;
		String szCUST_CD 				= null;
		String szORD_NO 				= null;
		String szORD_DTL 				= null;
		String szORD_GP 				= null;
		String szORD_CONV_W 			= null;
		String szORD_CONV_LEN 			= null;
		String szORD_CONV_T				= null;
		String szORD_EA					= null;
		String szYD_STRCHAR_GRP_CD 		= null;
		String szDETAIL_ARR_CD 			= "";
		String szCUST_CHK       		= "";
		
		JDTORecordSet  rsOsComm 	= null;
		JDTORecord     recPara 		= null;
		JDTORecord     recOsComm 	= null;
		
		String szDELIVER_TERM_CD    =null; //인도조건코드 추가
		String szEXP_LAND_SHIP_GBN  ="";  //수출(E) 육송(D) 해송(Y) 구분.
		String szSTRGRP_FORTMP="";  //PI오픈후 다수 파일링코드 미셋팅될 부분 대비해서, 임시 수출(E307)/육송(D777)/해송(Y040)에 따른 임시 저장속성그룹 생성. 		
				
		JDTORecordSet  outRecordSet = JDTORecordFactory.getInstance().createRecordSet("OS1");
		JDTORecordSet  outRecordSet1= JDTORecordFactory.getInstance().createRecordSet("OS1");
		JDTORecord inRecord9 		= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord2 		= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord9 		= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord10 		= JDTORecordFactory.getInstance().create();
		
		String szRtnMsg          = "";
		
		int intRtnVal;
		
		boolean bIsUpDateYN			= false;         
		
		try {
			
			szMsg = "["+szOperationName+"] 처리시작  ";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
			// 1. OS 미생산 주문중 속성그룹이 셋팅안된 정보를 읽어온다.
			rsOsComm = JDTORecordFactory.getInstance().createRecordSet("OS");
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("TEMP", "");
			
			//아래 SQL에 DELIVER_TERM_CD 항목 추가(인도조건코드로 육송/해송 구분해서 임시 파일링코드 셋팅하기 위해) 
			//E307(수출) , D777(육송) , Y040(해송) ,  //임진후 사원 요청사항.2022.12.31 파일링 대량 미셋팅시 대응위해 사용하는 임시 코드 						
			
			/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPilingNull_PIDEV*/
			intRtnVal = ptOsCommDao.getPtOsComm(recPara, rsOsComm, 200);
			
			if (intRtnVal <= 0) {
				if (intRtnVal == 0) {
					szMsg = "["+szOperationName+"] 신규투입주문이 없습니다.  ";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					return YdConstant.RETN_CD_SUCCESS;
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
			}
						
			for(int i = 1; i <= rsOsComm.size(); i++ ) {
				
				bIsUpDateYN	= false;  
				
				recOsComm = JDTORecordFactory.getInstance().create();
			
				rsOsComm.absolute(i);
				recOsComm = rsOsComm.getRecord();
				
				szORD_NO 			= recOsComm.getFieldString("ORD_NO");
				szORD_DTL 			= recOsComm.getFieldString("ORD_DTL");
				szORD_GP 			= recOsComm.getFieldString("ORD_GP");
				szDEST_CD 			= recOsComm.getFieldString("DEST_CD");
				szCUST_CD 			= recOsComm.getFieldString("CUST_CD");
				szDETAIL_ARR_CD 	= recOsComm.getFieldString("DETAIL_ARR_CD");
				szORD_CONV_W 		= recOsComm.getFieldString("ORD_CONV_W");
				szORD_CONV_LEN 		= recOsComm.getFieldString("ORD_CONV_LEN");
				szORD_EA 			= recOsComm.getFieldString("ORD_EA"); 
				szORD_CONV_T	  	= recOsComm.getFieldString("ORD_CONV_T"); 
				
				szDELIVER_TERM_CD = recOsComm.getFieldString("DELIVER_TERM_CD");  //인도조건코드 추가

				szMsg = "["+szOperationName+"] 주문:"+ szORD_NO+" 행번:" + szORD_DTL + " 인도조건코드:"+szDELIVER_TERM_CD+" 처리시작  ";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);

				/*
				 *  수출 처리
				 */
				if ((szORD_GP.equals("E")) || (szORD_GP.equals("F"))){
									
					outRecordSet = JDTORecordFactory.getInstance().createRecordSet("");
					inRecord9 = JDTORecordFactory.getInstance().create();
					
					inRecord9.setField("ORD_NO"		, szORD_NO);
					inRecord9.setField("ORD_DTL"	, szORD_DTL);
					inRecord9.setField("ORD_GP" 	, "E");   //수출 로직
					
					szEXP_LAND_SHIP_GBN="E";  //수출구분
					szSTRGRP_FORTMP="E307"; //수출 임시 저장속성그룹					
//PIDEV_S :병행가동용:PI_YD
					inRecord9.setField("PI_YD",    	"T");						
					/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForSameDestCustPigCustDtlArr_PIDEV*/
					// 목적지/고객사 /대형고객사/상세착지 동일
					intRtnVal = ptOsCommDao.getPtOsComm(inRecord9, outRecordSet, 201);
					if (intRtnVal > 0) {
						outRecordSet.absolute(1);
						outRecord9 = outRecordSet.getRecord();
						
						szYD_STRCHAR_GRP_CD = outRecord9.getFieldString("YD_STRCHAR_GRP_CD");
						szCUST_CHK			= outRecord9.getFieldString("CUST_CHK");        // 저장속성에 있는 고객사코드(대형고객사)
						
						bIsUpDateYN	= true;  
					} else {
						szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 목적지/고객사 /대형고객사/상세착지 동일 동일한 수출저장속성그룹이 없습니다.";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
						
						outRecordSet = JDTORecordFactory.getInstance().createRecordSet("");
						/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForYardStkNO*/
						/* 야드에 재고가 없고 주문이 종결되고 속성그룹이 가장 작은 속성 그룹 찾는 SQL*/
						intRtnVal = ptOsCommDao.getPtOsComm(inRecord9, outRecordSet, 202);
						if (intRtnVal > 0) {
							outRecordSet.absolute(1);
							outRecord10 = outRecordSet.getRecord();
							
							szYD_STRCHAR_GRP_CD = outRecord10.getFieldString("YD_STRCHAR_GRP_CD");
							szCUST_CHK			= szCUST_CD;
							
							YdPICommDAO ydPICommDAO = new YdPICommDAO();
							String sApplyYnPI_IN = ydPICommDAO.ApplyYnPI("", "후판 임시속성그룹 할당여부", "APPPI2", "*", "*");
							//FLAG=TRUE이면 임시 저장속성그룹 셋팅. 추후 FLAG 풀면 이전처럼 미셋팅.
							if(sApplyYnPI_IN.equals("Y")){
								szYD_STRCHAR_GRP_CD="E307";
							}
							
							/*com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.updYdStrchargrp*/
							recPara = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_STRCHAR_ID"		, "");
							recPara.setField("YD_STRCHAR_GRP_CD"	, szYD_STRCHAR_GRP_CD);
							recPara.setField("REGISTER"				, "PTYDJ004");
							recPara.setField("MODIFIER"				, "PTYDJ004");
							recPara.setField("YD_GP"				, YdConstant.YD_GP_PLATE2_GDS_YARD);
							recPara.setField("ORD_GP"				, "E");
							recPara.setField("DEST_CD"				, szDEST_CD);
							recPara.setField("CUST_CD"				, szCUST_CD);
							recPara.setField("DEMANDER_CD"			, szCUST_CD);
							recPara.setField("DETAIL_ARR_CD"		, szDETAIL_ARR_CD);
							recPara.setField("ORD_NO"				, szORD_NO);

							recPara.setField("YD_STR_CHAR_GRP_ACT_STAT",	"O");   // 속성그룹 활성
							
							intRtnVal = ydStrCharGrpDao.updYdStrchargrp(recPara, 0);								
							if( intRtnVal <= 0 ) {
								szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 저장속성그룹 수정 실패 입습니다.";
								ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
							}
							
							outRecordSet1 = JDTORecordFactory.getInstance().createRecordSet("");
							
							/*com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrcharBySameOrdNo*/
							/* 기존 저장속성 SEARCH */
							intRtnVal = ydStrCharDao.getYdStrchar(recPara, outRecordSet1, 303);
							if (intRtnVal > 0) {
								for(int j = 1; j <= outRecordSet1.size(); j++ ) {

									outRecord2 = JDTORecordFactory.getInstance().create();
									outRecordSet1.absolute(j);
									outRecord2 = outRecordSet1.getRecord();

									// 완전히 삭제처리
									intRtnVal = ydStrCharDao.delYdStrchar(outRecord2);
								}
							} 	
							
							/*com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.insYdStrchar
							 INSERT INTO TB_YD_STRCHAR*/
							intRtnVal = ydStrCharDao.insYdStrchar(recPara);
							if( intRtnVal <= 0 ) {
								szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 저장속성 등록 실패 입습니다.";
								ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
							}

							bIsUpDateYN	= true;  
							
						} else {
							szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 사용가능한 저장속성그룹이 없습니다.";
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
							szYD_STRCHAR_GRP_CD = "K---";								
						}
					}	
				/*
				 *  수출외 처리	
				 */
				} else {
					
					if(szDELIVER_TERM_CD.substring(0, 1).equals("1")){  //육송
						szEXP_LAND_SHIP_GBN="D";  //육송구분
						szSTRGRP_FORTMP="D777"; //육송 임시 저장속성그룹
					}
					else{  //DELIVER_TERM_CD:2  //해송
						szEXP_LAND_SHIP_GBN="Y";  //해송구분
						szSTRGRP_FORTMP="Y040"; //해송 임시 저장속성그룹
					}
					
					outRecordSet = JDTORecordFactory.getInstance().createRecordSet("");
					inRecord9 = JDTORecordFactory.getInstance().create();
					
					inRecord9.setField("ORD_NO"	, szORD_NO);
					inRecord9.setField("ORD_DTL", szORD_DTL);
					inRecord9.setField("ORD_GP" , "D");   //내수 로직
//PIDEV_S :병행가동용:PI_YD
					inRecord9.setField("PI_YD",    	"T");						
					/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForSameDestCustPigCustDtlArr_PIDEV*/
					// 목적지/고객사 /대형고객사/상세착지 동일
					intRtnVal = ptOsCommDao.getPtOsComm(inRecord9, outRecordSet, 201);
					if (intRtnVal > 0) {
						outRecordSet.absolute(1);
						outRecord9 = outRecordSet.getRecord();
						szYD_STRCHAR_GRP_CD = outRecord9.getFieldString("YD_STRCHAR_GRP_CD");
						szCUST_CHK			= outRecord9.getFieldString("CUST_CHK");        // 저장속성에 있는 고객사코드(대형고객사)

						bIsUpDateYN	= true;  
					} else {
						szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 목적지/고객사/대형고객사/상세착지 동일한저장속성그룹이 없습니다.";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);

						outRecordSet = JDTORecordFactory.getInstance().createRecordSet("");

//PIDEV_S :병행가동용:PI_YD
						inRecord9.setField("PI_YD",    	"T");						
						/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForSameDestCustDtlArr_PIDEV*/
						// 목적지/고객사/상세착지 동일
						intRtnVal = ptOsCommDao.getPtOsComm(inRecord9, outRecordSet, 203);
						if (intRtnVal > 0) {
							outRecordSet.absolute(1);
							outRecord9 = outRecordSet.getRecord();

							szYD_STRCHAR_GRP_CD = outRecord9.getFieldString("YD_STRCHAR_GRP_CD");
							szCUST_CHK			= outRecord9.getFieldString("CUST_CHK");        // 저장속성에 있는 고객사코드(대형고객사)

							bIsUpDateYN	= true;
						} else {
							szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 목적지/고객사/상세착지 동일 동일한저장속성그룹이 없습니다.";
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
							
							outRecordSet = JDTORecordFactory.getInstance().createRecordSet("");
							/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForSameDestDtlArr_PIDEV*/
							// 목적지/상세착지 동일
//PIDEV_S :병행가동용:PI_YD
							inRecord9.setField("PI_YD",    	"T");						
							intRtnVal = ptOsCommDao.getPtOsComm(inRecord9, outRecordSet, 204);
							if (intRtnVal > 0) {
								outRecordSet.absolute(1);
								outRecord9 = outRecordSet.getRecord();
								
								szYD_STRCHAR_GRP_CD = outRecord9.getFieldString("YD_STRCHAR_GRP_CD");
								szCUST_CHK			= outRecord9.getFieldString("CUST_CHK");        // 저장속성에 있는 고객사코드(대형고객사)

								
								bIsUpDateYN	= true;
							} else {
								szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 목적지/상세착지 동일 동일한저장속성그룹이 없습니다.";
								ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
								
								
								YdPICommDAO ydPICommDAO = new YdPICommDAO();
								String sApplyYnPI_IN = ydPICommDAO.ApplyYnPI("", "후판 임시속성그룹 할당여부", "APPPI2", "*", "*");
								//FLAG=TRUE이면 임시 저장속성그룹 셋팅. 추후 FLAG 풀면 이전처럼 미셋팅.
								if(sApplyYnPI_IN.equals("Y")){
									if(szEXP_LAND_SHIP_GBN.equals("Y") || szEXP_LAND_SHIP_GBN.equals("D")){ 
										szYD_STRCHAR_GRP_CD=szSTRGRP_FORTMP; //임시 저장속성그룹 셋팅.D777(육송) , Y040(해송)
										bIsUpDateYN	= true;
									}
								}
								else{
									continue ;
								}
							}	
						}
					}	
				}
				
				if (bIsUpDateYN) {
					/*
					 * 위에서 주문정보에 대한 속성그룹이 셋팅되면 
					 * 아래에서 파일링코드 및 예정위치를 셋팅하는 메소드
					 */
					szRtnMsg = PlateGdsYdUtil.procPtOsCommUpdateByOrder(    szORD_EA,
																			szORD_CONV_T,
																			szORD_CONV_W,
																			szORD_CONV_LEN,
																			szCUST_CHK,
																			szYD_STRCHAR_GRP_CD,
																			szORD_NO,
																			szORD_DTL); 
					
					if(szRtnMsg != YdConstant.RETN_CD_SUCCESS) {
						szMsg="[ERROR] "+szClassName+"::"+szMethodName+"() procPtOsCommUpdateByOrder Error :" + szRtnMsg;
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_FAILURE; 
					}
				}	
					
			} //end of for(i)		

			szMsg = "["+szOperationName+"] 처리종료 ";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
		}catch(Exception ex) {
			szMsg = "O/S신규주문"+szOperationName+"] 투입주문의 야드저장속성 결정 시 오류발생 - 메세지 : " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	} //end of procPtOsCommUpdateByYdStrCharGrp
	
	/** 
	 * 투입주문의 야드저장속성 결정 신적용-화면에서 사용하기 위해
	 * @param String
	 * @throws JDTOException
	 * 작성자 : 윤재광
	 * 작성일 : 2014.12.10
	 * 내 용  : OS주문투입실적 (PTYDJ004) 전문 수신 후 OS공통의 미투입 주문을 검색하여
	 *         PILING_CD, BOOK_OUT_LOC, 입고예정저장위치 등을 설정해준다. 
	 */
	public static String procPtOsCommUpdateByYdStrCharGrpNextNew(String sOrdNo, String sOrdDtl, String logId) {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.10 로그 개선  START
// procPtOsCommUpdateByYdStrCharGrpNextNew argument 에 logId 항목 추가 개선
// public static String procPtOsCommUpdateByYdStrCharGrpNextNew(String sOrdNo, String sOrdDtl) {
// 2024.09.10 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		PtOsCommDao ptOsCommDao 		= new PtOsCommDao();
		
		YdStrCharDao ydStrCharDao 		= new YdStrCharDao();
		YdStrCharGrpDao ydStrCharGrpDao = new YdStrCharGrpDao();
		
		String szMethodName				= "procPtOsCommUpdateByYdStrCharGrpNextNew";
		String szOperationName 			= "투입주문의 야드저장속성 결정next";
		String szMsg        			= null;
		
		String szDEST_CD 				= null;
		String szCUST_CD 				= null;
		String szORD_NO 				= null;
		String szORD_DTL 				= null;
		String szORD_GP 				= null;
		String szORD_CONV_W 			= null;
		String szORD_CONV_LEN 			= null;
		String szORD_CONV_T				= null;
		String szORD_EA					= null;
		String szYD_STRCHAR_GRP_CD 		= null;
		String szDETAIL_ARR_CD 				= "";
		String szCUST_CHK       			= "";
		String szCUST_DEMAND_ARRIVAL_DATE	= "";
		
		String szDELIVER_TERM_CD    =null; //인도조건코드 추가
		String szEXP_LAND_SHIP_GBN  ="";  //수출(E) 육송(D) 해송(Y) 구분.
		String szSTRGRP_FORTMP="";  //PI오픈후 다수 파일링코드 미셋팅될 부분 대비해서, 임시 수출(E307)/육송(D777)/해송(Y040)에 따른 임시 저장속성그룹 생성. 
		
		JDTORecordSet  rsOsComm 	= null;
		JDTORecord     recPara 		= null;
		JDTORecord     recOsComm 	= null;
		
		JDTORecordSet  outRecordSet = JDTORecordFactory.getInstance().createRecordSet("OS1");
		JDTORecordSet  outRecordSet1= JDTORecordFactory.getInstance().createRecordSet("OS1");
		JDTORecord inRecord9 		= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord2 		= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord9 		= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord10 		= JDTORecordFactory.getInstance().create();
		
		String szRtnMsg          = "";
		
		int intRtnVal;
		
		boolean bIsUpDateYN			= false;  
		boolean bIsNewCustYN        = false;
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.10 로그 개선  START
// logId Empty 이면 logId 신규 생성 개선
if(ydUtils.isEmpty(logId)) logId 		= ydUtils.getLogIdNew("T"); 			// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "OS주문투입실적 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.10 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		try {
			
			szMsg = "["+szOperationName+"] 처리시작  ";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG, logId);
				
			// 1. OS 미생산 주문중 속성그룹이 셋팅안된 정보를 읽어온다.
			rsOsComm = JDTORecordFactory.getInstance().createRecordSet("OS");
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("TEMP"		, "");
			recPara.setField("ORD_NO"	, sOrdNo);
			recPara.setField("ORD_DTL"	, sOrdDtl);
			
			//아래 SQL에 DELIVER_TERM_CD 항목 추가(인도조건코드로 육송/해송 구분해서 임시 파일링코드 셋팅하기 위해) 
			//E307(수출) , D777(육송) , Y040(해송) ,  //임진후 사원 요청사항.2022.12.31 파일링 대량 미셋팅시 대응위해 사용하는 임시 코드 			
			/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPilingNull_PIDEV*/
			intRtnVal = ptOsCommDao.getPtOsComm(recPara, rsOsComm, 200);
			
			if (intRtnVal <= 0) {
				if (intRtnVal == 0) {
					szMsg = "["+szOperationName+"] 신규투입주문이 없습니다.  ";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG, logId);
					return YdConstant.RETN_CD_SUCCESS;
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);
					return YdConstant.RETN_CD_FAILURE;
				}
			}
						
			for(int i = 1; i <= rsOsComm.size(); i++ ) {
				
				bIsUpDateYN	= false;  
				
				recOsComm = JDTORecordFactory.getInstance().create();
			
				rsOsComm.absolute(i);
				recOsComm = rsOsComm.getRecord();
				
				szORD_NO 			= recOsComm.getFieldString("ORD_NO");
				szORD_DTL 			= recOsComm.getFieldString("ORD_DTL");
				szORD_GP 			= recOsComm.getFieldString("ORD_GP");
				szDEST_CD 			= recOsComm.getFieldString("DEST_CD");
				szCUST_CD 			= recOsComm.getFieldString("CUST_CD");
				szDETAIL_ARR_CD 	= recOsComm.getFieldString("DETAIL_ARR_CD");
				szORD_CONV_W 		= recOsComm.getFieldString("ORD_CONV_W");
				szORD_CONV_LEN 		= recOsComm.getFieldString("ORD_CONV_LEN");
				szORD_EA 			= recOsComm.getFieldString("ORD_EA"); 
				szORD_CONV_T	  	= recOsComm.getFieldString("ORD_CONV_T");
				szCUST_DEMAND_ARRIVAL_DATE  	= recOsComm.getFieldString("CUST_DEMAND_ARRIVAL_DATE");
				
				szDELIVER_TERM_CD = recOsComm.getFieldString("DELIVER_TERM_CD");  //인도조건코드 추가				

				szMsg = "["+szOperationName+"] 주문:"+ szORD_NO+" 행번:" + szORD_DTL +" 인도조건코드:"+szDELIVER_TERM_CD+ " 처리시작  ";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG, logId);
				
				outRecordSet = JDTORecordFactory.getInstance().createRecordSet("");
				inRecord9 	 = JDTORecordFactory.getInstance().create();
				
				/*
				 *  수출 처리
				 */
				if ((szORD_GP.equals("E")) || (szORD_GP.equals("F"))){
					
					inRecord9.setField("ORD_NO"		, szORD_NO);
					inRecord9.setField("ORD_DTL"	, szORD_DTL);
					inRecord9.setField("ORD_GP" 	, "E");   //수출 로직
					
					szEXP_LAND_SHIP_GBN="E";  //수출구분
					szSTRGRP_FORTMP="E307"; //수출 임시 저장속성그룹					
				/*
				 *  수출외 처리	
				 */
				} else {
					
					inRecord9.setField("ORD_NO"	, szORD_NO);
					inRecord9.setField("ORD_DTL", szORD_DTL);
					inRecord9.setField("ORD_GP" , "D");   //내수 로직
					
					
					if(szDELIVER_TERM_CD.substring(0, 1).equals("1")){  //육송
						szEXP_LAND_SHIP_GBN="D";  //육송구분
						szSTRGRP_FORTMP="D777"; //육송 임시 저장속성그룹
					}
					else{  //DELIVER_TERM_CD:2  //해송
						szEXP_LAND_SHIP_GBN="Y";  //해송구분
						szSTRGRP_FORTMP="Y040"; //해송 임시 저장속성그룹
					}					
				}	
				/*
				 *  이명운 요청사항 - 2016.08.26 윤재광
				 * 
					미국향 착지코드 : 	USLGB (LA / LONG BEACH PORT)
	                				USHOU (HOUSTON)
	                				USNEW (NEW ORLEANS)
	                				USVAN (VANCOUVER, WA)
	                                              에 대해서 주문번호 단위로 저장속성그룹을 셋팅한다.
	                                              
	                                              서윤 요청사항 - 2021.06.03 위 조건에 대해 처리막음
                */
				/*				
				if("USLGB".equals(szDETAIL_ARR_CD)||
				   "USHOU".equals(szDETAIL_ARR_CD)||
				   "USNEW".equals(szDETAIL_ARR_CD)||
				   "USVAN".equals(szDETAIL_ARR_CD))
				{
					//====================================================================================
					//===========================시작1=====================================================
					//====================================================================================
					com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForYardStkNO
					 야드에 재고가 없고 주문이 종결되고 속성그룹이 가장 작은 속성 그룹 찾는 SQL
					intRtnVal = ptOsCommDao.getPtOsComm(inRecord9, outRecordSet, 202);
					if (intRtnVal > 0) {
						outRecordSet.absolute(1);
						outRecord10 = outRecordSet.getRecord();
						
						szYD_STRCHAR_GRP_CD = outRecord10.getFieldString("YD_STRCHAR_GRP_CD");
						szCUST_CHK			= szCUST_CD;
						
						com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.updYdStrchargrp
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_STRCHAR_ID"		, "");
						recPara.setField("YD_STRCHAR_GRP_CD"	, szYD_STRCHAR_GRP_CD);
						recPara.setField("REGISTER"				, "PTYDJ004");
						recPara.setField("MODIFIER"				, "PTYDJ004");
						recPara.setField("YD_GP"				, YdConstant.YD_GP_PLATE2_GDS_YARD);
						recPara.setField("ORD_GP"				, "E");
						recPara.setField("DEST_CD"				, szDEST_CD);
						recPara.setField("CUST_CD"				, szCUST_CD);
						recPara.setField("DEMANDER_CD"			, szCUST_CD);
						recPara.setField("DETAIL_ARR_CD"		, szDETAIL_ARR_CD);
						recPara.setField("ORD_NO"				, szORD_NO);
						recPara.setField("YD_STR_CHAR_GRP_ACT_STAT",	"O");   // 속성그룹 활성
						
						intRtnVal = ydStrCharGrpDao.updYdStrchargrp(recPara, 0);								
						if( intRtnVal <= 0 ) {
							szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 저장속성그룹 수정 실패 입습니다.";
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
						}
						
						outRecordSet1 = JDTORecordFactory.getInstance().createRecordSet("");
						
						com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrcharBySameOrdNo
						 기존 저장속성 SEARCH 
						intRtnVal = ydStrCharDao.getYdStrchar(recPara, outRecordSet1, 303);
						if (intRtnVal > 0) {
							for(int j = 1; j <= outRecordSet1.size(); j++ ) {

								outRecord2 = JDTORecordFactory.getInstance().create();
								outRecordSet1.absolute(j);
								outRecord2 = outRecordSet1.getRecord();

								// 완전히 삭제처리
								intRtnVal = ydStrCharDao.delYdStrchar(outRecord2);
							}
						} 	
						
						com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.insYdStrchar
						 INSERT INTO TB_YD_STRCHAR
						intRtnVal = ydStrCharDao.insYdStrchar(recPara);
						if( intRtnVal <= 0 ) {
							szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 저장속성 등록 실패 입습니다.";
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
						}

						bIsUpDateYN	= true;  
						
					} else {
						szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 사용가능한 저장속성그룹이 없습니다.";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
						szYD_STRCHAR_GRP_CD = "K---";			
					
					}
					//====================================================================================
					//===========================끝1======================================================
					//====================================================================================
				}else{
				 	*/
					//====================================================================================
					//===========================시작2=====================================================
					//====================================================================================
					
					/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForSameDestCustPigCustDtlArr_PIDEV*/
					// 목적지/고객사 /대형고객사/상세착지 동일
//PIDEV_S :병행가동용:PI_YD
					inRecord9.setField("PI_YD",    	"T");				
					intRtnVal = ptOsCommDao.getPtOsComm(inRecord9, outRecordSet, 201);
					if (intRtnVal > 0) {
						outRecordSet.absolute(1);
						outRecord9 = outRecordSet.getRecord();
						szYD_STRCHAR_GRP_CD = outRecord9.getFieldString("YD_STRCHAR_GRP_CD");
						szCUST_CHK			= outRecord9.getFieldString("CUST_CHK");        // 저장속성에 있는 고객사코드(대형고객사)

						bIsUpDateYN	= true;  
					} else {
						szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 목적지/고객사/대형고객사/상세착지 동일한저장속성그룹이 없습니다.";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG, logId);

						outRecordSet = JDTORecordFactory.getInstance().createRecordSet("");
//PIDEV_S :병행가동용:PI_YD
						inRecord9.setField("PI_YD",    	"T");
						/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForSameDestCustDtlArr_PIDEV*/
						// 목적지/고객사/상세착지 동일
						intRtnVal = ptOsCommDao.getPtOsComm(inRecord9, outRecordSet, 203);
						if (intRtnVal > 0) {
							outRecordSet.absolute(1);
							outRecord9 = outRecordSet.getRecord();

							szYD_STRCHAR_GRP_CD = outRecord9.getFieldString("YD_STRCHAR_GRP_CD");
							szCUST_CHK			= outRecord9.getFieldString("CUST_CHK");        // 저장속성에 있는 고객사코드(대형고객사)

							bIsUpDateYN	= true;
						} else {
							szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 목적지/고객사/상세착지 동일 동일한저장속성그룹이 없습니다.";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
							ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG, logId);
//PIDEV_S :병행가동용:PI_YD
							inRecord9.setField("PI_YD",    	"T");						
							outRecordSet = JDTORecordFactory.getInstance().createRecordSet("");
							/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForSameDestDtlArr_PIDEV*/
							// 목적지/상세착지 동일
							intRtnVal = ptOsCommDao.getPtOsComm(inRecord9, outRecordSet, 204);
							if (intRtnVal > 0) {
								outRecordSet.absolute(1);
								outRecord9 = outRecordSet.getRecord();
								
								szYD_STRCHAR_GRP_CD = outRecord9.getFieldString("YD_STRCHAR_GRP_CD");
								szCUST_CHK			= outRecord9.getFieldString("CUST_CHK");        // 저장속성에 있는 고객사코드(대형고객사)
								
								bIsUpDateYN	= true;
							} else {
								szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 목적지/상세착지 동일 동일한저장속성그룹이 없습니다.";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//								ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
								ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG, logId);
								
								//수출재일경우에만 처리
								if ((szORD_GP.equals("E")) || (szORD_GP.equals("F")))
								{
									outRecordSet = JDTORecordFactory.getInstance().createRecordSet("");
									/*com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.getYdAbleStrchargrp*/
									//사용가능 수출재 속성그룹 검색							
									intRtnVal = ydStrCharGrpDao.getYdStrchargrp(inRecord9, outRecordSet, 8);
									if (intRtnVal > 0) {
										outRecordSet.absolute(1);
										outRecord10 = outRecordSet.getRecord();
										
										szYD_STRCHAR_GRP_CD = outRecord10.getFieldString("YD_STRCHAR_GRP_CD");
										szCUST_CHK			= szCUST_CD;
										
										/*2024.09.10 후판동별저장계획 화면 개선요청 임진후 기사 요청 --REQ202408611796
										 * 수출재 신규고객사 추가시, 동별저장계획 자동생성을 위한 수정 
										 * 
										 * */
										
										/*com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.updYdStrchargrp*/
										recPara = JDTORecordFactory.getInstance().create();
										recPara.setField("YD_STRCHAR_ID"		, "");
										recPara.setField("YD_STRCHAR_GRP_CD"	, szYD_STRCHAR_GRP_CD);
										recPara.setField("REGISTER"				, "PTYDJ004");
										recPara.setField("MODIFIER"				, "PTYDJ004");
										recPara.setField("YD_GP"				, YdConstant.YD_GP_PLATE2_GDS_YARD);
										recPara.setField("ORD_GP"				, "E");
										recPara.setField("DEST_CD"				, szDEST_CD);
										recPara.setField("CUST_CD"				, szCUST_CD);
										recPara.setField("DEMANDER_CD"			, "");
										recPara.setField("DETAIL_ARR_CD"		, szDETAIL_ARR_CD);
										recPara.setField("ORD_NO"				, "");
										recPara.setField("YD_STR_CHAR_GRP_ACT_STAT",	"O");   // 속성그룹 활성
										
										intRtnVal = ydStrCharGrpDao.updYdStrchargrp(recPara, 0);								
										if( intRtnVal <= 0 ) {
											szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 저장속성그룹 수정 실패 입습니다.";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//											ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
											ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);
										}
										
										outRecordSet1 = JDTORecordFactory.getInstance().createRecordSet("");
										
										/*com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrcharBySameOrdNo*/
										/* 기존 저장속성 SEARCH */
										intRtnVal = ydStrCharDao.getYdStrchar(recPara, outRecordSet1, 303);
										if (intRtnVal > 0) {
											for(int j = 1; j <= outRecordSet1.size(); j++ ) {

												outRecord2 = JDTORecordFactory.getInstance().create();
												outRecordSet1.absolute(j);
												outRecord2 = outRecordSet1.getRecord();

												// 완전히 삭제처리
												intRtnVal = ydStrCharDao.delYdStrchar(outRecord2);
											}
										} 	
										
										/*com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.insYdStrchar
										 INSERT INTO TB_YD_STRCHAR*/
										intRtnVal = ydStrCharDao.insYdStrchar(recPara);
										if( intRtnVal <= 0 ) {
											szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 저장속성 등록 실패 입습니다.";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//											ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
											ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);
										}

										bIsUpDateYN	= true;  
										bIsNewCustYN = true;
										
									} else {
										szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 사용가능한 저장속성그룹이 없습니다.";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//										ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
										ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);
										szYD_STRCHAR_GRP_CD = "K---";		
										
										continue ;
									}
								}else{  //수출 제외(육송/해송)
									szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 사용가능한 저장속성그룹이 없습니다.";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//									ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
									ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);
									szYD_STRCHAR_GRP_CD = "K---";	
									
									YdPICommDAO ydPICommDAO = new YdPICommDAO();
									String sApplyYnPI_IN = ydPICommDAO.ApplyYnPI("", "후판 임시속성그룹 할당여부", "APPPI2", "*", "*");
									//FLAG=TRUE이면 임시 저장속성그룹 셋팅. 추후 FLAG 풀면 이전처럼 미셋팅.
									if(sApplyYnPI_IN.equals("Y")){
										if(szEXP_LAND_SHIP_GBN.equals("Y") || szEXP_LAND_SHIP_GBN.equals("D")){ 
											szYD_STRCHAR_GRP_CD=szSTRGRP_FORTMP; //임시 저장속성그룹 셋팅.D777(육송) , Y040(해송)
											bIsUpDateYN	= true;
										}
									}
									else{
										continue ;
									}
								}
							}	
						}
					}
					//====================================================================================
					//===========================끝2======================================================
					//====================================================================================
				//}
				
				/*
				 * 2016.04.18 윤재광
				 * 현대삼호 중공업 연안 대상재 인도기한일별 속성그룹 분리적용
				 * Y093/A11119/I3402/ARRIVAL_DUE_DATE
				 * => Y001 ~ Y012 월별로 분리
				 * 
				 * 2016.08.31 윤재광
				 * 위 해당내용 처리 막아달라고 요청
				 */
				/*
				if("A11119".equals(szCUST_CD)&&
				   "I3402".equals(szDETAIL_ARR_CD)&&
				   "Y093".equals(szYD_STRCHAR_GRP_CD)){
					
					if(!"".equals(szCUST_DEMAND_ARRIVAL_DATE)){
						
						szYD_STRCHAR_GRP_CD = "Y0"+szCUST_DEMAND_ARRIVAL_DATE;
						
						szMsg = "["+szOperationName+"] 현대삼호 인도기한일 월별분리 ORD_NO [" + szORD_NO + "] ORD_DTL [" + szORD_DTL + "]의 속성그룹은 [" + szYD_STRCHAR_GRP_CD + "].";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				}
				*/
				if (bIsUpDateYN) {
					/*
					 * 2024.09.10 후판동별저장계획 화면 개선요청 임진후 기사 요청 --REQ202408611796
					 * 수출재 신규고객사 추가시, 동별저장계획 자동생성을 위한 수정 
					 * 
					 * 2024.10.07 신규 고객사 추가 됐을경우에만 실행되도록 변경
					 * */
					YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
					String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "002");
					
					szMsg = "["+szOperationName+"] 신규고객사 추가여부 [" + Boolean.toString(bIsNewCustYN) + "] 신규시스템적용여부 [" + sApplyYnPI + "] 주문구분 [" + szORD_GP + "]";
					ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR,logId);
					
					if (bIsNewCustYN && "Y".equals(sApplyYnPI) && ( (szORD_GP.equals("E")) || (szORD_GP.equals("F")) ) ){
						recPara.setResultCode(logId);
						PlateGdsYdUtil.insPlateYdBayLocPlnMgt(recPara);
					}
					
					
					/*
					 * 위에서 주문정보에 대한 속성그룹이 셋팅되면 
					 * 아래에서 파일링코드 및 예정위치를 셋팅하는 메소드
					 */
					szRtnMsg = PlateGdsYdUtil.procPtOsCommUpdateByOrder(    szORD_EA,
																			szORD_CONV_T,
																			szORD_CONV_W,
																			szORD_CONV_LEN,
																			szCUST_CHK,
																			szYD_STRCHAR_GRP_CD,
																			szORD_NO,
																			szORD_DTL); 
					
					if(szRtnMsg != YdConstant.RETN_CD_SUCCESS) {
						szMsg="[ERROR] "+szClassName+"::"+szMethodName+"() procPtOsCommUpdateByOrder Error :" + szRtnMsg;
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
						ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);
						return YdConstant.RETN_CD_FAILURE; 
					}
				}	
					
			} //end of for(i)		

			szMsg = "["+szOperationName+"] 처리종료 ";
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG, logId);
			
		}catch(Exception ex) {
			szMsg = "O/S신규주문"+szOperationName+"] 투입주문의 야드저장속성 결정 시 오류발생 - 메세지 : " + ex.getMessage();
// 2024.09.10 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_CD_FAILURE;
		}


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.10 로그 개선  START
szMsg = "OS주문투입실적 처리(" + szMethodName + ") 완료";
ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.10 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
	} //end of procPtOsCommUpdateByYdStrCharGrpNextNew
	
	/**
	 * 신규 동별저장계획 insert (동별저장계획 자동생성을 위한 수정 ) --REQ202408611796
	 * @param szYD_BOOK_OUT_LOC
	 * @param startBedNo
	 * @return
	 * @throws JDTOException
	 */
	public static String insPlateYdBayLocPlnMgt(JDTORecord msgRecord) throws JDTOException {
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		String szMethodName				= "insPlateYdBayLocPlnMgt";
		String szOperationName			= "동별저장계획 자동생성";
		String szMsg					= "";
		
		String logId                    = ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 				// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		JDTORecord	recPara				= null;
		JDTORecordSet outRecSet			= null;
		
		YdPlateCommDAO commDao = new YdPlateCommDAO();
		
		
		try {
			String ydStrCharGrpCd = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STRCHAR_GRP_CD");
			
			szMsg="["+szOperationName+"] 저장그룹 ["+ydStrCharGrpCd+"] 신규 동별저장계획 등록 시작";
			ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);
			
			
			msgRecord.setField("CD_GP"	 ,	ydStrCharGrpCd);
			msgRecord.setField("REGISTER",	"PTYDJ004");
			
			commDao.insert(msgRecord,"com.inisteel.cim.yd.common.dao.YdPlateCommDAO.insPlateYdBayLocPlnMgtNEW",logId, szMethodName, "신규 동별저장계획등록");
			
			szMsg="["+szOperationName+"] 저장그룹 ["+ydStrCharGrpCd+"] 신규 동별저장계획 등록 끝";
			ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);
			
			return szRtnMsg;
			
		}
		catch(JDTOException ex) {
			szMsg = "[" + szOperationName + "] 예외발생 - 오류메세지 : " + ex.getMessage();
			ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);
			
			szRtnMsg =YdConstant.RETN_CD_FAILURE;
			return szRtnMsg;	
   		}
	}
	
	/** 
	 * 변경주문의 야드저장속성 수정 
	 * @param String
	 * @throws JDTOException
	 * 작성자 : 박종호
	 * 작성일 : 2023.11.07
	 * 내 용  : OS주문투입수정 (PTYDJ005) 전문 수신 후 OS공통의 기셋팅된 파일링코드 변경 
	 */
	public static String procPtOsCommModifyByYdStrCharGrpNextNew(String sOrdNo, String sOrdDtl,String logId) {
		
		PtOsCommDao ptOsCommDao 		= new PtOsCommDao();
		
		YdStrCharDao ydStrCharDao 		= new YdStrCharDao();
		YdStrCharGrpDao ydStrCharGrpDao = new YdStrCharGrpDao();
		
		String szMethodName				= "procPtOsCommModifyByYdStrCharGrpNextNew";
		String szOperationName 			= "변경주문의 야드저장속성 결정next";
		String szMsg        			= null;
		
		String szDEST_CD 				= null;
		String szCUST_CD 				= null;
		String szORD_NO 				= null;
		String szORD_DTL 				= null;
		String szORD_GP 				= null;
		String szORD_CONV_W 			= null;
		String szORD_CONV_LEN 			= null;
		String szORD_CONV_T				= null;
		String szORD_EA					= null;
		String szYD_STRCHAR_GRP_CD 		= null;
		String szDETAIL_ARR_CD 				= "";
		String szCUST_CHK       			= "";
		String szCUST_DEMAND_ARRIVAL_DATE	= "";
		
		String szDELIVER_TERM_CD    =null; //인도조건코드 추가
		String szEXP_LAND_SHIP_GBN  ="";  //수출(E) 육송(D) 해송(Y) 구분.
		String szSTRGRP_FORTMP="";  //PI오픈후 다수 파일링코드 미셋팅될 부분 대비해서, 임시 수출(E307)/육송(D777)/해송(Y040)에 따른 임시 저장속성그룹 생성. 
		
		JDTORecordSet  rsOsComm 	= null;
		JDTORecord     recPara 		= null;
		JDTORecord     recOsComm 	= null;
		
		JDTORecordSet  outRecordSet = JDTORecordFactory.getInstance().createRecordSet("OS1");
		JDTORecordSet  outRecordSet1= JDTORecordFactory.getInstance().createRecordSet("OS1");
		JDTORecord inRecord9 		= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord2 		= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord9 		= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord10 		= JDTORecordFactory.getInstance().create();
		
		String szRtnMsg          = "";
		
		int intRtnVal;
		
		boolean bIsUpDateYN			= false;    
		boolean bIsNewCustYN        = false;
		
		try {
			// 2024.09.10 로그 개선  START
			// logId Empty 이면 logId 신규 생성 개선
			if(ydUtils.isEmpty(logId)) logId 		= ydUtils.getLogIdNew("T"); 			// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

			szMsg = "OS주문투입실적 처리(" + szMethodName + ") 시작";
			ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.INFO, logId);
			
				
			// 1. OS 미생산 주문중 속성그룹이 셋팅안된 정보를 읽어온다.
			//->  주문정보 모두 읽어온다.(속성그룹 셋팅여부 상관없이)
			rsOsComm = JDTORecordFactory.getInstance().createRecordSet("OS");
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("TEMP"		, "");
			recPara.setField("ORD_NO"	, sOrdNo);
			recPara.setField("ORD_DTL"	, sOrdDtl);
			
			//아래 SQL에 DELIVER_TERM_CD 항목 추가(인도조건코드로 육송/해송 구분해서 임시 파일링코드 셋팅하기 위해) 
			//E307(수출) , D777(육송) , Y040(해송) ,  //임진후 사원 요청사항.2022.12.31 파일링 대량 미셋팅시 대응위해 사용하는 임시 코드 			
			/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForPilingNull_PIDEV*/
			/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsComm_PIDEV*/  //파일링코드 셋팅여부와 상관없이 조회되도록 쿼리 신규 개발
			intRtnVal = ptOsCommDao.getPtOsComm(recPara, rsOsComm, 206);  //206번으로 셋팅.셋팅완료
			
			if (intRtnVal <= 0) {
				if (intRtnVal == 0) {
					szMsg = "["+szOperationName+"] 대상주문이 없습니다.  ";
					ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG, logId);
					return YdConstant.RETN_CD_SUCCESS;
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR, logId);
					return YdConstant.RETN_CD_FAILURE;
				}
			}
						
			for(int i = 1; i <= rsOsComm.size(); i++ ) {
				
				bIsUpDateYN	= false;  
				
				recOsComm = JDTORecordFactory.getInstance().create();
			
				rsOsComm.absolute(i);
				recOsComm = rsOsComm.getRecord();
				
				szORD_NO 			= recOsComm.getFieldString("ORD_NO");
				szORD_DTL 			= recOsComm.getFieldString("ORD_DTL");
				szORD_GP 			= recOsComm.getFieldString("ORD_GP");
				szDEST_CD 			= recOsComm.getFieldString("DEST_CD");
				szCUST_CD 			= recOsComm.getFieldString("CUST_CD");
				szDETAIL_ARR_CD 	= recOsComm.getFieldString("DETAIL_ARR_CD");
				szORD_CONV_W 		= recOsComm.getFieldString("ORD_CONV_W");
				szORD_CONV_LEN 		= recOsComm.getFieldString("ORD_CONV_LEN");
				szORD_EA 			= recOsComm.getFieldString("ORD_EA"); 
				szORD_CONV_T	  	= recOsComm.getFieldString("ORD_CONV_T");
				szCUST_DEMAND_ARRIVAL_DATE  	= recOsComm.getFieldString("CUST_DEMAND_ARRIVAL_DATE");
				
				szDELIVER_TERM_CD = recOsComm.getFieldString("DELIVER_TERM_CD");  //인도조건코드 추가				

				szMsg = "["+szOperationName+"] 주문:"+ szORD_NO+" 행번:" + szORD_DTL +" 인도조건코드:"+szDELIVER_TERM_CD+ " 처리시작  ";
				ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG,logId);
				
				outRecordSet = JDTORecordFactory.getInstance().createRecordSet("");
				inRecord9 	 = JDTORecordFactory.getInstance().create();
				
				/*
				 *  수출 처리
				 */
				if ((szORD_GP.equals("E")) || (szORD_GP.equals("F"))){
					
					inRecord9.setField("ORD_NO"		, szORD_NO);
					inRecord9.setField("ORD_DTL"	, szORD_DTL);
					inRecord9.setField("ORD_GP" 	, "E");   //수출 로직
					
					szEXP_LAND_SHIP_GBN="E";  //수출구분
					szSTRGRP_FORTMP="E307"; //수출 임시 저장속성그룹					
				/*
				 *  수출외 처리	
				 */
				} else {
					
					inRecord9.setField("ORD_NO"	, szORD_NO);
					inRecord9.setField("ORD_DTL", szORD_DTL);
					inRecord9.setField("ORD_GP" , "D");   //내수 로직
					
					
					if(szDELIVER_TERM_CD.substring(0, 1).equals("1")){  //육송
						szEXP_LAND_SHIP_GBN="D";  //육송구분
						szSTRGRP_FORTMP="D777"; //육송 임시 저장속성그룹
					}
					else{  //DELIVER_TERM_CD:2  //해송
						szEXP_LAND_SHIP_GBN="Y";  //해송구분
						szSTRGRP_FORTMP="Y040"; //해송 임시 저장속성그룹
					}					
				}	
				
					/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForSameDestCustPigCustDtlArr_PIDEV*/
					// 목적지/고객사 /대형고객사/상세착지 동일
					//PIDEV_S :병행가동용:PI_YD
					inRecord9.setField("PI_YD",    	"T");				
					intRtnVal = ptOsCommDao.getPtOsComm(inRecord9, outRecordSet, 201);
					if (intRtnVal > 0) {
						outRecordSet.absolute(1);
						outRecord9 = outRecordSet.getRecord();
						szYD_STRCHAR_GRP_CD = outRecord9.getFieldString("YD_STRCHAR_GRP_CD");
						szCUST_CHK			= outRecord9.getFieldString("CUST_CHK");        // 저장속성에 있는 고객사코드(대형고객사)

						bIsUpDateYN	= true;  
					} else {
						szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 목적지/고객사/대형고객사/상세착지 동일한저장속성그룹이 없습니다.";
						ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG,logId);

						outRecordSet = JDTORecordFactory.getInstance().createRecordSet("");
						//PIDEV_S :병행가동용:PI_YD
						inRecord9.setField("PI_YD",    	"T");
						/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForSameDestCustDtlArr_PIDEV*/
						// 목적지/고객사/상세착지 동일
						intRtnVal = ptOsCommDao.getPtOsComm(inRecord9, outRecordSet, 203);
						if (intRtnVal > 0) {
							outRecordSet.absolute(1);
							outRecord9 = outRecordSet.getRecord();

							szYD_STRCHAR_GRP_CD = outRecord9.getFieldString("YD_STRCHAR_GRP_CD");
							szCUST_CHK			= outRecord9.getFieldString("CUST_CHK");        // 저장속성에 있는 고객사코드(대형고객사)

							bIsUpDateYN	= true;
						} else {
							szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 목적지/고객사/상세착지 동일 동일한저장속성그룹이 없습니다.";
							ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG,logId);
//PIDEV_S :병행가동용:PI_YD
							inRecord9.setField("PI_YD",    	"T");						
							outRecordSet = JDTORecordFactory.getInstance().createRecordSet("");
							/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommForSameDestDtlArr_PIDEV*/
							// 목적지/상세착지 동일
							intRtnVal = ptOsCommDao.getPtOsComm(inRecord9, outRecordSet, 204);
							if (intRtnVal > 0) {
								outRecordSet.absolute(1);
								outRecord9 = outRecordSet.getRecord();
								
								szYD_STRCHAR_GRP_CD = outRecord9.getFieldString("YD_STRCHAR_GRP_CD");
								szCUST_CHK			= outRecord9.getFieldString("CUST_CHK");        // 저장속성에 있는 고객사코드(대형고객사)
								
								bIsUpDateYN	= true;
							} else {
								szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 목적지/상세착지 동일 동일한저장속성그룹이 없습니다.";
								ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG,logId);
								
								//수출재일경우에만 처리
								if ((szORD_GP.equals("E")) || (szORD_GP.equals("F")))
								{
									outRecordSet = JDTORecordFactory.getInstance().createRecordSet("");
									/*com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.getYdAbleStrchargrp*/
									//사용가능 수출재 속성그룹 검색
									//PI오픈이후 안정화기간동안 대상 수출재 속성그룹을 E307로 할당해서사용(쿼리내 FLAG처리)									
									intRtnVal = ydStrCharGrpDao.getYdStrchargrp(inRecord9, outRecordSet, 8);
									if (intRtnVal > 0) {
										outRecordSet.absolute(1);
										outRecord10 = outRecordSet.getRecord();
										
										szYD_STRCHAR_GRP_CD = outRecord10.getFieldString("YD_STRCHAR_GRP_CD");
										szCUST_CHK			= szCUST_CD;
										
										/*com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.updYdStrchargrp*/
										recPara = JDTORecordFactory.getInstance().create();
										recPara.setField("YD_STRCHAR_ID"		, "");
										recPara.setField("YD_STRCHAR_GRP_CD"	, szYD_STRCHAR_GRP_CD);
										recPara.setField("REGISTER"				, "PTYDJ005");
										recPara.setField("MODIFIER"				, "PTYDJ005");
										recPara.setField("YD_GP"				, YdConstant.YD_GP_PLATE2_GDS_YARD);
										recPara.setField("ORD_GP"				, "E");
										recPara.setField("DEST_CD"				, szDEST_CD);
										recPara.setField("CUST_CD"				, szCUST_CD);
										recPara.setField("DEMANDER_CD"			, "");
										recPara.setField("DETAIL_ARR_CD"		, szDETAIL_ARR_CD);
										recPara.setField("ORD_NO"				, "");
										recPara.setField("YD_STR_CHAR_GRP_ACT_STAT",	"O");   // 속성그룹 활성
										
										intRtnVal = ydStrCharGrpDao.updYdStrchargrp(recPara, 0);								
										if( intRtnVal <= 0 ) {
											szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 저장속성그룹 수정 실패 입습니다.";
											ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR,logId);
										}
										
										outRecordSet1 = JDTORecordFactory.getInstance().createRecordSet("");
										
										/*com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrcharBySameOrdNo*/
										/* 기존 저장속성 SEARCH */
										intRtnVal = ydStrCharDao.getYdStrchar(recPara, outRecordSet1, 303);
										if (intRtnVal > 0) {
											for(int j = 1; j <= outRecordSet1.size(); j++ ) {

												outRecord2 = JDTORecordFactory.getInstance().create();
												outRecordSet1.absolute(j);
												outRecord2 = outRecordSet1.getRecord();

												// 완전히 삭제처리
												intRtnVal = ydStrCharDao.delYdStrchar(outRecord2);
											}
										} 	
										
										/*com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.insYdStrchar
										 INSERT INTO TB_YD_STRCHAR*/
										intRtnVal = ydStrCharDao.insYdStrchar(recPara);
										if( intRtnVal <= 0 ) {
											szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 저장속성 등록 실패 입습니다.";
											ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR,logId);
										}

										bIsUpDateYN	= true;  
										bIsNewCustYN = true;
										
									} else {
										szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 사용가능한 저장속성그룹이 없습니다.";
										ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR,logId);
										szYD_STRCHAR_GRP_CD = "K---";		
										
										continue ;
									}
								}else{  //수출 제외(육송/해송)
									szMsg = "["+szOperationName+"] DEST_CD [" + szDEST_CD + "]의 사용가능한 저장속성그룹이 없습니다.";
									ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR,logId);
									szYD_STRCHAR_GRP_CD = "K---";	
									
									YdPICommDAO ydPICommDAO = new YdPICommDAO();
									String sApplyYnPI_IN = ydPICommDAO.ApplyYnPI("", "후판 임시속성그룹 할당여부", "APPPI2", "*", "*");
									//FLAG=TRUE이면 임시 저장속성그룹 셋팅. 추후 FLAG 풀면 이전처럼 미셋팅.
									if(sApplyYnPI_IN.equals("Y")){
										if(szEXP_LAND_SHIP_GBN.equals("Y") || szEXP_LAND_SHIP_GBN.equals("D")){ 
											szYD_STRCHAR_GRP_CD=szSTRGRP_FORTMP; //임시 저장속성그룹 셋팅.D777(육송) , Y040(해송)
											bIsUpDateYN	= true;
										}
									}
									else{
										continue ;
									}
								}
							}	
						}
					}
			
				if (bIsUpDateYN) {  //여기서 파일링코드만 셋팅되도록(예정위치는 셋팅x)로직 변경 필요.
					/*
					 * 2024.09.10 후판동별저장계획 화면 개선요청 임진후 기사 요청 --REQ202408611796
					 * 수출재 신규고객사 추가시, 동별저장계획 자동생성을 위한 수정 
					 * 
					 * */
					YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
					String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "002");
					
					szMsg = "["+szOperationName+"] 신규고객사 추가여부 [" + Boolean.toString(bIsNewCustYN) + "] 신규시스템적용여부 [" + sApplyYnPI + "] 주문구분 [" + szORD_GP + "]";
					ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR,logId);
					
					if (bIsNewCustYN && "Y".equals(sApplyYnPI) && ( (szORD_GP.equals("E")) || (szORD_GP.equals("F")) ) ){
						recPara.setResultCode(logId);
						PlateGdsYdUtil.insPlateYdBayLocPlnMgt(recPara);
					}
					
					/*
					 * 위에서 주문정보에 대한 속성그룹이 셋팅되면 
					 * 아래에서 파일링코드 및 예정위치를 셋팅하는 메소드
					 */
					szRtnMsg = PlateGdsYdUtil.procPtOsCommModifyByOrder(    szORD_EA,
																			szORD_CONV_T,
																			szORD_CONV_W,
																			szORD_CONV_LEN,
																			szCUST_CHK,
																			szYD_STRCHAR_GRP_CD,
																			szORD_NO,
																			szORD_DTL); 
					
					if(szRtnMsg != YdConstant.RETN_CD_SUCCESS) {
						szMsg="[ERROR] "+szClassName+"::"+szMethodName+"() procPtOsCommUpdateByOrder Error :" + szRtnMsg;
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR,logId);
						return YdConstant.RETN_CD_FAILURE; 
					}
				}	
					
			} //end of for(i)		

			szMsg = "["+szOperationName+"] 처리종료 ";
			ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.DEBUG,logId);
			
		}catch(Exception ex) {
			szMsg = "O/S변경주문"+szOperationName+"] 투입주문의 야드저장속성 결정 시 오류발생 - 메세지 : " + ex.getMessage();
			ydUtils.putLogNew(szClassName, szMethodName, szMsg, YdConstant.ERROR,logId);
			return YdConstant.RETN_CD_FAILURE;
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	} //end of procPtOsCommUpdateByYdStrCharGrpNextNew	
	
	/** 
	 * 투입주문의 야드파일링코드 및 예정위치 셋팅
	 * @param String
	 * @throws JDTOException
	 * 작성자 : 윤재광
	 * 작성일 : 2014.09.01
	 * 내 용  : OS신규주문속성화면에서 미속성 주문정보 셋팅을 위해 메소드 분리 
	 */
	public static String procPtOsCommUpdateByOrder( String szORD_EA,
													String szORD_CONV_T,
													String szORD_CONV_W,
													String szORD_CONV_LEN,
													String szCUST_CHK, //대형고객사 여부(속성테이블 DEMANDER_CD 항목)         
													String szYD_STRCHAR_GRP_CD,
													String szORD_NO,
													String szORD_DTL) {
		
		String szMethodName				= "procPtOsCommUpdateByOrder";
		String szOperationName 			= "투입주문의 야드파일링코드 및 예정위치 셋팅";
		String szMsg        			= "";
		String szYD_PILING_SIZE 		= "";
		String szYD_PILING_CODE 		= "";
		
		double dblheight 				= 0;
		int intRtnVal					= 0;
		
		String szRtnMsg          		= "";
		
		PtOsCommDao ptOsCommDao 		= new PtOsCommDao();
		
		try{
			
			JDTORecord recPara = JDTORecordFactory.getInstance().create();

			ydUtils.putLog(szClassName, szMethodName, "1. szORD_EA : " + szORD_EA, YdConstant.DEBUG);
			ydUtils.putLog(szClassName, szMethodName, "2. szORD_CONV_T : " + szORD_CONV_T, YdConstant.DEBUG);
			// 베드 높이 계산	
			dblheight = Double.parseDouble(szORD_CONV_T) * Integer.parseInt(szORD_EA);
			
			if(dblheight < 2000){
				recPara.setField("STRCHAR_CUST_CD_SINGLE"		, "Y");
			} else {
				recPara.setField("STRCHAR_CUST_CD_SINGLE"		, "N");
			}
			ydUtils.putLog(szClassName, szMethodName, "11. szORD_EA : " + szORD_EA, YdConstant.DEBUG);
			ydUtils.putLog(szClassName, szMethodName, "12. szORD_CONV_T : " + szORD_CONV_T, YdConstant.DEBUG);
			ydUtils.putLog(szClassName, szMethodName, "13. dblheight : " + dblheight, YdConstant.DEBUG);
			
			recPara.setField("YD_MTL_L"					, szORD_CONV_LEN    );
			recPara.setField("YD_MTL_W"					, szORD_CONV_W);
			recPara.setField("STRCHAR_CUST_CD" 			, szCUST_CHK);	
			recPara.setField("STRCHAR_ORD_YEOJAE_GP" 	, "1");
			recPara.setField("YD_STRCHAR_GRP_CD" 		, szYD_STRCHAR_GRP_CD);
			
			PlateGdsYdUtil.getWTLGp(recPara);
			
			szYD_PILING_SIZE = yddatautil.setDataDefault(recPara.getField("YD_MTL_W_GP"), "") + yddatautil.setDataDefault(recPara.getField("YD_MTL_L_GP"), "");
			szYD_PILING_CODE = szYD_STRCHAR_GRP_CD + szYD_PILING_SIZE;
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_PILING_CD",	szYD_PILING_CODE);
			recPara.setField("YD_BOOK_OUT_LOC",	"00000");
			recPara.setField("YD_RCPT_STR_LOC",	"TX010101");
			recPara.setField("ORD_NO",   		szORD_NO);
			recPara.setField("ORD_DTL",  		szORD_DTL);
			
			/*com.inisteel.cim.yd.common.dao.ptOsCommDao.updPtOsCommDaoForPTYDJ004*/
			intRtnVal = ptOsCommDao.updPtOsComm(recPara, 5);
			
			szMsg = "[JSP Session : "+szOperationName+"] [주문"+szORD_NO+"] 행번:" + szORD_DTL + " 파일링 코드:" + szYD_PILING_CODE + " OS COMM 업데이트 : " + intRtnVal;
			ydUtils.putLog(szMethodName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//충당처리 된 제품 UPDATE 처리					
			szRtnMsg = PlateGdsYdUtil.procPlateStockPlatecomUpt(recPara);
			  
			if(szRtnMsg != YdConstant.RETN_CD_SUCCESS) {
				szMsg="[ERROR] "+szClassName+"::"+szMethodName+"() procPlateStockPlatecomUpt Error :" + szRtnMsg;
				szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE; 
			}
			
		}catch(Exception ex) {
			szMsg = "O/S신규주문"+szOperationName+"] 투입주문의 야드파일링코드 및 예정위치 셋팅시 오류발생 - 메세지 : " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/** 
	 * 변경주문의 야드파일링코드 재셋팅(위치정보등은 재셋팅x)
	 * @param String
	 * @throws JDTOException
	 * 작성자 : 박종호
	 * 작성일 : 2023.11.07
	 * 내 용  : OS변경주문속성통해 파일링코드 재셋팅(PTYDJ005) 
	 */
	public static String procPtOsCommModifyByOrder( String szORD_EA,
													String szORD_CONV_T,
													String szORD_CONV_W,
													String szORD_CONV_LEN,
													String szCUST_CHK, //대형고객사 여부(속성테이블 DEMANDER_CD 항목)         
													String szYD_STRCHAR_GRP_CD,
													String szORD_NO,
													String szORD_DTL) {
		
		String szMethodName				= "procPtOsCommUpdateByOrder";
		String szOperationName 			= "투입주문의 야드파일링코드 및 예정위치 셋팅";
		String szMsg        			= "";
		String szYD_PILING_SIZE 		= "";
		String szYD_PILING_CODE 		= "";
		
		double dblheight 				= 0;
		int intRtnVal					= 0;
		
		String szRtnMsg          		= "";
		
		PtOsCommDao ptOsCommDao 		= new PtOsCommDao();
		
		try{
			
			JDTORecord recPara = JDTORecordFactory.getInstance().create();

			ydUtils.putLog(szClassName, szMethodName, "1. szORD_EA : " + szORD_EA, YdConstant.DEBUG);
			ydUtils.putLog(szClassName, szMethodName, "2. szORD_CONV_T : " + szORD_CONV_T, YdConstant.DEBUG);
			// 베드 높이 계산	
			dblheight = Double.parseDouble(szORD_CONV_T) * Integer.parseInt(szORD_EA);
			
			if(dblheight < 2000){
				recPara.setField("STRCHAR_CUST_CD_SINGLE"		, "Y");
			} else {
				recPara.setField("STRCHAR_CUST_CD_SINGLE"		, "N");
			}
			ydUtils.putLog(szClassName, szMethodName, "11. szORD_EA : " + szORD_EA, YdConstant.DEBUG);
			ydUtils.putLog(szClassName, szMethodName, "12. szORD_CONV_T : " + szORD_CONV_T, YdConstant.DEBUG);
			ydUtils.putLog(szClassName, szMethodName, "13. dblheight : " + dblheight, YdConstant.DEBUG);
			
			recPara.setField("YD_MTL_L"					, szORD_CONV_LEN    );
			recPara.setField("YD_MTL_W"					, szORD_CONV_W);
			recPara.setField("STRCHAR_CUST_CD" 			, szCUST_CHK);	
			recPara.setField("STRCHAR_ORD_YEOJAE_GP" 	, "1");
			recPara.setField("YD_STRCHAR_GRP_CD" 		, szYD_STRCHAR_GRP_CD);
			
			PlateGdsYdUtil.getWTLGp(recPara);
			
			szYD_PILING_SIZE = yddatautil.setDataDefault(recPara.getField("YD_MTL_W_GP"), "") + yddatautil.setDataDefault(recPara.getField("YD_MTL_L_GP"), "");
			szYD_PILING_CODE = szYD_STRCHAR_GRP_CD + szYD_PILING_SIZE;
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_PILING_CD",	szYD_PILING_CODE);
			//recPara.setField("YD_BOOK_OUT_LOC",	"00000");  //BOOKOUT위치 업데이트 안함. 입고대기존까지와서 주문변경되면 북아웃로케이션 초기화되므로 
			//recPara.setField("YD_RCPT_STR_LOC",	"TX010101");  //TO위치 업데이트 안함. 위와 같은 이유.
			recPara.setField("ORD_NO",   		szORD_NO);
			recPara.setField("ORD_DTL",  		szORD_DTL);
			
			/*com.inisteel.cim.yd.common.dao.ptOsCommDao.updPtOsCommDaoForPTYDJ004*/
			/*com.inisteel.cim.yd.common.dao.ptOsCommDao.updPtOsCommDaoForPTYDJ005*///파일링코드만 업데이트하는 버전으로 쿼리 추가
			intRtnVal = ptOsCommDao.updPtOsComm(recPara, 8); //8번으로 수정
			
			szMsg = "[JSP Session : "+szOperationName+"] [주문"+szORD_NO+"] 행번:" + szORD_DTL + " 파일링 코드:" + szYD_PILING_CODE + " OS COMM 업데이트 : " + intRtnVal;
			ydUtils.putLog(szMethodName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//충당처리 된 제품 UPDATE 처리					
			//szRtnMsg = PlateGdsYdUtil.procPlateStockPlatecomUpt(recPara);  //이거 대신 STOCK UPDATE만 처리 우선 스킵하자.23.11.13
			
			//여기부터
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("ORD_NO"		, szORD_NO);
			recPara.setField("ORD_DTL"		, szORD_DTL);
			recPara.setField("YD_USER_ID"	, "PTYDJ005");
			recPara.setField("YD_PILING_CD"	, szYD_PILING_CODE);
							
			//stock update 
			/*com.inisteel.cim.yd.common.dao.ptPlateCommDao.updYdStockPilingCdOrdNo*/
			PtPlateCommDao ptPlateCommDao     	= new PtPlateCommDao();
			int intRtnVal1 = ptPlateCommDao.updPtPlateComm(recPara, 7);//7로 쿼리 새로 만들기
			//여기까지
			  
			if(szRtnMsg != YdConstant.RETN_CD_SUCCESS) {
				szMsg="[ERROR] "+szClassName+"::"+szMethodName+"() procPlateStockPlatecomUpt Error :" + szRtnMsg;
				szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE; 
			}
			
		}catch(Exception ex) {
			szMsg = "O/S변경주문"+szOperationName+"] 의 야드파일링코드 및 예정위치 셋팅시 오류발생 - 메세지 : " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return YdConstant.RETN_CD_SUCCESS;
	}	
	/** 
	 * 후판제품목전된 제품 파일링코드 UPDATE처리
	 * @param inRecord
	 * @throws JDTOException
	 */
	public static String procPlateStockPlatecomUpt(JDTORecord inRecord) {
	
		YdStockDao     ydStockDao     	= new YdStockDao();
		PtPlateCommDao ptPlateCommDao     	= new PtPlateCommDao();
		
		JDTORecordSet 	outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord 		recPara 	= JDTORecordFactory.getInstance().create();
		JDTORecord 		outRecord  	= JDTORecordFactory.getInstance().create();
		
		String szMethodName		= "procPlateStockPlatecomUpt";
		String szOperationName 	= "후판제품목전된 제품 파일링코드 UPDATE처리";
		String szMsg        	= "";
		String szORD_NO			= "";
		String szORD_DTL		= "";
		String szYD_PILING_CD	= "";
		String  szSTL_NO     	= "";
		int    intRtnVal 		= 0;
		int    intRtnVal1 		= 0;
		int    intRtnVal2 		= 0;
				
		try {
	
			szMsg = "["+szOperationName+"] 주문:"+ szORD_NO+" 행번:" + szORD_DTL + " 목전대상재 유무 처리시작  ";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szORD_NO 		= inRecord.getFieldString("ORD_NO");
			szORD_DTL 		= inRecord.getFieldString("ORD_DTL");
			szYD_PILING_CD	= inRecord.getFieldString("YD_PILING_CD");
			
			outResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdPilingNotCnt*/
			intRtnVal = ydStockDao.getYdStock(inRecord, outResult, 610);
			if (intRtnVal > 0) {

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("ORD_NO"		, szORD_NO);
				recPara.setField("ORD_DTL"		, szORD_DTL);
				recPara.setField("YD_USER_ID"	, "PTYDJ004");
				recPara.setField("YD_PILING_CD"	, szYD_PILING_CD);
								
				//stock update 
				/*com.inisteel.cim.yd.common.dao.ptPlateCommDao.updYdStockPilingCdOrdNo*/
				intRtnVal1 = ptPlateCommDao.updPtPlateComm(recPara, 4);
				if (intRtnVal1 > 0) {
					
					szMsg = "["+szOperationName+"] TB_YD_STOCK UPDATE 완료.";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);

		    		for(int Loop_i = 1; Loop_i <= outResult.size(); Loop_i++) {
		    			outResult.absolute(Loop_i);
		    			outRecord = JDTORecordFactory.getInstance().create();
		    			outRecord = outResult.getRecord();
		    		
		    			szSTL_NO = ydDaoUtils.paraRecChkNull(outRecord, "STL_NO");
						
		    			recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("PLATE_NO"		, szSTL_NO);
						recPara.setField("YD_PILING_CD"	, szYD_PILING_CD);
						recPara.setField("ORD_NO"		, szORD_NO);
						recPara.setField("ORD_DTL"		, szORD_DTL);
								
		    			// Platecom update 
						/*com.inisteel.cim.yd.common.dao.ptPlateCommDao.updPtPlateCommPilingCdStlNo*/
						intRtnVal2 = ptPlateCommDao.updPtPlateComm(recPara, 5);
						if (intRtnVal2 != 1) {
							szMsg = "["+szOperationName+"] TB_PT_PLATECOMM UPDATE ERROR ";
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
						}
		    		}	
					szMsg = "["+szOperationName+"] TB_PT_PLATECOMM UPDATE 완료.";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);

				} else {
					szMsg = "["+szOperationName+"] TB_YD_STOCK UPDATE ERROR ";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				}	

			} else {
				szMsg = "["+szOperationName+"] TB_YD_STOCK 대상재가 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}	

		}catch(Exception ex) {
			szMsg = "["+szOperationName+"] 후판제품목전된 제품 파일링코드 UPDATE처리 오류발생 - 메세지 : " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
		}
		return YdConstant.RETN_CD_SUCCESS;
	} //end of procPlateStockPlatecomUpt
	
	
    /**
     * 좌표계산(후판제품:EF추가)
     * @param recInPara
     * @param recOutPara
     * @return
     * @throws JDTOException
     */
    public static String PlateCraneXYCal_EF(JDTORecord recInPara, JDTORecord recOutPara) throws JDTOException {
    	String szOperationName		= "좌표계산(후판제품:EF)";
    	String szMethodName			= "PlateCraneXYCal_EF";
    	String szMsg				= null;
    	String szRtnMsg				= null;
    	
    	JDTORecord		recPara		= null;
    	JDTORecord		recTemp		= null;
    	JDTORecordSet	outRecSet	= null;
    	//-------------------------------------------------------------------------------------------------
    	//	파라미터로 전달되는 항목 정의
    	//-------------------------------------------------------------------------------------------------
    	String szSTL_NO				= null;						//크레인작업재료들중에서 길이가 제일 긴 재료번호
    	String szYD_UP_STK_COL_GP	= null;						//권상지시위치 - 적치열
    	String szYD_UP_STK_BED_NO	= null;						//권상지시위치 - 적치베드
    	String szYD_DN_STK_COL_GP	= null;						//권하지시위치 - 적치열
    	String szYD_DN_STK_BED_NO	= null;						//권하지시위치 - 적치베드
    	String szYD_EQP_ID			= null;						//크레인설비ID
    	//-------------------------------------------------------------------------------------------------
    	
    	String szYD_CRN_GRAB_GP		= null;						//Grab Type
    	String szYD_CRN_TONG_L		= null;						//Crane Beam 길이
    	String szYD_CRN_GRAB_TP		= null;						//1,2 Grab 구분
    	
    	String szYD_MTL_W			= null;						//재료 폭
    	int intYD_MTL_W				= 0;						//재료 폭
    	String szYD_MTL_L			= null;						//재료 길이
    	int intYD_MTL_L				= 0;						//재료 길이
    	
    	//-------------------------------------------------------------------------------------------------
    	//	권상위치 정보
    	//-------------------------------------------------------------------------------------------------
    	String szYD_UP_STK_COL_BED_L_TP = null;					//권상지시위치 - 야드적치열Bed길이Type
    	String szYD_UP_CAR_USE_GP		= null;					//차량사용구분
    	String szUP_TRN_EQP_CD			= null;					//운송장비코드
    	String szUP_CAR_NO				= null;					//차량번호
    	String szUP_CARD_NO				= null;					//카드번호
    	
    	int intYD_UP_STK_BED_XAXIS		= 0;					//권상지시위치베드 - 야드적치BedX축
    	int intYD_UP_STK_BED_YAXIS		= 0;					//권상지시위치베드 - 야드적치BedY축
    	String szYD_UP_STK_BED_L_GP		= null;					//권상지시위치베드 - 야드적치Bed길이구분
    	String szYD_UP_STK_BED_W_GP 	= null;					//권상지시위치베드 - 야드적치Bed폭구분
        String szYD_UP_STK_BED_WHIO_STAT= null;					//권상지시위치베드 - 야드적치Bed상태구분
    	String szYD_UP_STK_BED_W_MAX	= null;
    	int intYD_UP_STK_BED_W_MAX		= 0;
    	int intYD_UP_STK_BED_L_MAX		= 0;
    	//-------------------------------------------------------------------------------------------------
    	
    	//-------------------------------------------------------------------------------------------------
    	//	권하위치 정보
    	//-------------------------------------------------------------------------------------------------
    	String szYD_DN_STK_COL_BED_L_TP = null;					//권하지시위치 - 야드적치열Bed길이Type
    	String szYD_DN_CAR_USE_GP		= null;					//차량사용구분
    	String szDN_TRN_EQP_CD			= null;					//운송장비코드
    	String szDN_CAR_NO				= null;					//차량번호
    	String szDN_CARD_NO				= null;					//카드번호
    	
    	int intYD_DN_STK_BED_XAXIS		= 0;					//권하지시위치베드 - 야드적치BedX축
    	int intYD_DN_STK_BED_YAXIS		= 0;					//권하지시위치베드 - 야드적치BedY축
    	String szYD_DN_STK_BED_L_GP		= null;					//권하지시위치베드 - 야드적치Bed길이구분
    	String szYD_DN_STK_BED_W_GP 	= null;					//권하지시위치베드 - 야드적치Bed폭구분
    	String szYD_DN_STK_BED_WHIO_STAT= null;					//권하지시위치베드 - 야드적치Bed상태구분
    	String szYD_DN_STK_BED_W_MAX	= null;
    	int intYD_DN_STK_BED_W_MAX		= 0;
    	int intYD_DN_STK_BED_L_MAX		= 0;
    	//-------------------------------------------------------------------------------------------------
    	
    	String szCrane_Grab_Use_Gp 		= null;
		String szUp_Grab_X_Value     	= null;
		String szUp_Grab_Y_Value     	= null;
		int intUp_Grab_Y1_Value     	= 0;
		int intUp_Grab_Y2_Value     	= 0;
		
		String szDn_Grab_X_Value     	= null;
		String szDn_Grab_Y_Value     	= null;
		int intDn_Grab_Y1_Value     	= 0;
		int intDn_Grab_Y2_Value     	= 0;
    	
    	szMsg ="["+szOperationName+"] -------------------------- 메소드 시작 - 파라미터 확인 --------------------------" ;
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szSTL_NO					= ydDaoUtils.paraRecChkNull(recInPara, "STL_NO");					//크레인작업재료들중에서 길이가 제일 긴 재료번호
		szYD_UP_STK_COL_GP			= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_COL_GP");			//권상지시위치 - 적치열
		szYD_UP_STK_BED_NO			= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_BED_NO");			//권상지시위치 - 적치베드
		szYD_DN_STK_COL_GP			= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_COL_GP");			//권하지시위치 - 적치열
		szYD_DN_STK_BED_NO			= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_BED_NO");			//권하지시위치 - 적치베드
		szYD_EQP_ID					= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID");				//크레인설비ID
		szYD_MTL_W 					= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W"); 				//크레인작업재료들중에서 폭이 제일 넓은 재료의 폭 값
		
		szMsg ="["+szOperationName+"] 크레인작업재료들중에서 길이가 제일 긴 재료번호 - " + szSTL_NO;
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분 - " + szYD_UP_STK_COL_GP + ", 베드번호 - " + szYD_UP_STK_BED_NO + "]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분 - " + szYD_DN_STK_COL_GP + ", 베드번호 - " + szYD_DN_STK_BED_NO + "]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 크레인설비ID - " + szYD_EQP_ID;
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		//---------------------------------------------------------------------------------------------------------
		// 저장품 조회
		// 제품길이 (YD_MTL_L) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 재료번호[" + szSTL_NO + "]로 저장품 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("STL_NO", szSTL_NO);
		
		szRtnMsg	= DaoManager.getYdStock(recPara, outRecSet, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 재료번호[" + szSTL_NO + "]로 저장품 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		szYD_MTL_L 				= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L"); 			// 제품길이
		
		int pointIdx			= szYD_MTL_W.lastIndexOf(".");
		
		if( pointIdx >= 0 ) {
			szYD_MTL_W			= szYD_MTL_W.substring(0, pointIdx);
		}
		
		intYD_MTL_W				= Integer.parseInt(szYD_MTL_W);
		intYD_MTL_L				= Integer.parseInt(szYD_MTL_L);
		
		szMsg ="["+szOperationName+"] 크레인작업재료들중에서 길이가 제일 긴 재료번호[" + szSTL_NO + "] - 제품길이["+szYD_MTL_L+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 크레인작업재료들중에서 폭이 제일 넓은 재료의 제품폭["+szYD_MTL_W+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		// 크레인사양 조회
		// 설비ID로  크레인 Beam 길이(YD_CRN_TONG_L) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]로 크레인사양 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		recPara 	= JDTORecordFactory.getInstance().create();
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara.setField("YD_EQP_ID", szYD_EQP_ID);
		
		szRtnMsg	= DaoManager.getYdCrnspec(recPara, outRecSet, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]로 크레인사양 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		szYD_CRN_TONG_L = ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_TONG_L"); // 크레인 Beam 길이
		
		szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]의 크레인Beam길이[YD_CRN_TONG_L:"+szYD_CRN_TONG_L+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szYD_CRN_GRAB_TP = ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_GRAB_TP"); // 크레인 Grab 구분
		
		if("X".equals(szYD_CRN_GRAB_TP)){
			szYD_CRN_GRAB_GP = "E";
		}else{
			szYD_CRN_GRAB_GP = "D";
		}
		
		szMsg ="["+szOperationName+"] 크레인 Grab 구분[D : 1Grab Crane, E : 2Grab Crane] - " + szYD_CRN_GRAB_GP;
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		// 권상지시위치 - 적치열조회 : 야드적치열Bed길이Type, 차량정보가 존재하면 차량정보 조회
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "]로 적치열 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYD_UP_STK_COL_GP);
		
		szRtnMsg	= DaoManager.getYdStkcol(recPara, outRecSet, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "]로 적치열 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		szYD_UP_STK_COL_BED_L_TP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_BED_L_TP"); 		//야드적치열Bed길이Type
		szYD_UP_CAR_USE_GP					= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_USE_GP"); 				//차량사용구분
		szUP_TRN_EQP_CD						= ydDaoUtils.paraRecChkNull(recTemp, "TRN_EQP_CD"); 				//운송장비코드
		szUP_CAR_NO							= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO"); 					//차량번호
		szUP_CARD_NO						= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO"); 					//카드번호
		
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "]로 적치열 조회 완료 - 야드적치열Bed길이Type["+szYD_UP_STK_COL_BED_L_TP+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "] - 차량사용구분["+szYD_UP_CAR_USE_GP+"], 운송장비코드["+szUP_TRN_EQP_CD+"], 차량번호["+szUP_CAR_NO+"], 카드번호["+szUP_CARD_NO+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		// 권하지시위치 - 적치열조회 : 야드적치열Bed길이Type, 차량정보가 존재하면 차량정보 조회
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "]로 적치열 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYD_DN_STK_COL_GP);
		
		szRtnMsg	= DaoManager.getYdStkcol(recPara, outRecSet, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "]로 적치열 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		szYD_DN_STK_COL_BED_L_TP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_BED_L_TP"); 		//야드적치열Bed길이Type
		szYD_DN_CAR_USE_GP					= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_USE_GP"); 				//차량사용구분
		szDN_TRN_EQP_CD						= ydDaoUtils.paraRecChkNull(recTemp, "TRN_EQP_CD"); 				//운송장비코드
		szDN_CAR_NO							= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO"); 					//차량번호
		szDN_CARD_NO						= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO"); 					//카드번호
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "]로 적치열 조회 완료 - 야드적치열Bed길이Type["+szYD_DN_STK_COL_BED_L_TP+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "] - 차량사용구분["+szYD_DN_CAR_USE_GP+"], 운송장비코드["+szDN_TRN_EQP_CD+"], 차량번호["+szDN_CAR_NO+"], 카드번호["+szDN_CARD_NO+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------

		//---------------------------------------------------------------------------------------------------------
		// 권상지시위치 - 적치Bed 조회
		// 야드적치열구분, 야드적치Bed번호로  야드적치BedY축(YD_STK_BED_YAXIS) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + ", 베드번호:" + szYD_UP_STK_BED_NO + "]로 베드 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYD_UP_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", szYD_UP_STK_BED_NO);
		
		szRtnMsg	= DaoManager.getYdStkbed(recPara, outRecSet, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + ", 베드번호:" + szYD_UP_STK_BED_NO + "]로 베드 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		intYD_UP_STK_BED_XAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS"); 		// 야드적치BedX축
		intYD_UP_STK_BED_YAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS"); 		// 야드적치BedY축
		szYD_UP_STK_BED_L_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP"); 			// 야드적치Bed길이구분
		szYD_UP_STK_BED_W_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP"); 			// 야드적치Bed폭구분
		szYD_UP_STK_BED_WHIO_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT"); 		// 야드적치Bed상태구분
		szYD_UP_STK_BED_W_MAX			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_MAX"); 			// 야드적치Bed폭Max
		intYD_UP_STK_BED_L_MAX			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_L_MAX"); 		// 야드적치Bed길이Max
		
		pointIdx	= szYD_UP_STK_BED_W_MAX.lastIndexOf(".");
		
		if( pointIdx >= 0 ) {
			szYD_UP_STK_BED_W_MAX		= szYD_UP_STK_BED_W_MAX.substring(0, pointIdx);
		}
		
		intYD_UP_STK_BED_W_MAX			= Integer.parseInt(szYD_UP_STK_BED_W_MAX);
		
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + ", 베드번호:" + szYD_UP_STK_BED_NO + "]의  야드적치BED X축["+intYD_UP_STK_BED_XAXIS+"], 야드적치BED Y축["+intYD_UP_STK_BED_YAXIS+"], 야드적치Bed길이구분["+szYD_UP_STK_BED_L_GP+"] 조회 완료";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		
    	
		//---------------------------------------------------------------------------------------------------------
		// 권하지시위치 - 적치Bed 조회
		// 야드적치열구분, 야드적치Bed번호로  야드적치BedY축(YD_STK_BED_YAXIS) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + ", 베드번호:" + szYD_DN_STK_BED_NO + "]로 베드 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYD_DN_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", szYD_DN_STK_BED_NO);
		
		szRtnMsg	= DaoManager.getYdStkbed(recPara, outRecSet, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + ", 베드번호:" + szYD_DN_STK_BED_NO + "]로 베드 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		intYD_DN_STK_BED_XAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS"); 		// 야드적치BedX축
		intYD_DN_STK_BED_YAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS"); 		// 야드적치BedY축
		szYD_DN_STK_BED_L_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP"); 			// 야드적치Bed길이구분
		szYD_DN_STK_BED_W_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP"); 			// 야드적치Bed폭구분
		szYD_DN_STK_BED_WHIO_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT"); 		// 야드적치Bed상태구분
		szYD_DN_STK_BED_W_MAX			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_MAX"); 			// 야드적치Bed폭Max
		intYD_DN_STK_BED_L_MAX			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_L_MAX"); 		// 야드적치Bed길이Max
		
		pointIdx	= szYD_DN_STK_BED_W_MAX.lastIndexOf(".");
		
		if( pointIdx >= 0 ) {
			szYD_DN_STK_BED_W_MAX		= szYD_DN_STK_BED_W_MAX.substring(0, pointIdx);
		}
		
		intYD_DN_STK_BED_W_MAX			= Integer.parseInt(szYD_DN_STK_BED_W_MAX);
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + ", 베드번호:" + szYD_DN_STK_BED_NO + "]의  야드적치BED X축["+intYD_DN_STK_BED_XAXIS+"], 야드적치BED Y축["+intYD_DN_STK_BED_YAXIS+"], 야드적치Bed길이구분["+szYD_DN_STK_BED_L_GP+"] 조회 완료";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		//	X좌표 구하기
		//---------------------------------------------------------------------------------------------------------
		szUp_Grab_X_Value			= String.valueOf(procXCalForPlateYd(intYD_UP_STK_BED_XAXIS, intYD_MTL_W, intYD_UP_STK_BED_W_MAX, szYD_UP_STK_COL_GP,szYD_UP_STK_BED_W_GP,szYD_UP_STK_BED_WHIO_STAT));
		szDn_Grab_X_Value			= String.valueOf(procXCalForPlateYd(intYD_DN_STK_BED_XAXIS, intYD_MTL_W, intYD_DN_STK_BED_W_MAX, szYD_DN_STK_COL_GP,szYD_DN_STK_BED_W_GP,szYD_DN_STK_BED_WHIO_STAT));
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		//	Y좌표 구하기 
		//---------------------------------------------------------------------------------------------------------
		//(Tmp_YD_MTL_L / 2)
		szUp_Grab_Y_Value			= String.valueOf(intYD_UP_STK_BED_YAXIS + (intYD_MTL_L/2));
		szDn_Grab_Y_Value			= String.valueOf(intYD_DN_STK_BED_YAXIS + (intYD_MTL_L/2));
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		//	Y1, Y2좌표 구하기
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "] - 야드적치열Bed길이Type["+szYD_UP_STK_COL_BED_L_TP+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "] - 차량사용구분["+szYD_UP_CAR_USE_GP+"], 운송장비코드["+szUP_TRN_EQP_CD+"], 차량번호["+szUP_CAR_NO+"], 카드번호["+szUP_CARD_NO+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "] - 야드적치열Bed길이Type["+szYD_DN_STK_COL_BED_L_TP+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "] - 차량사용구분["+szYD_DN_CAR_USE_GP+"], 운송장비코드["+szDN_TRN_EQP_CD+"], 차량번호["+szDN_CAR_NO+"], 카드번호["+szDN_CARD_NO+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		//----------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------
		//	1Grab
		//----------------------------------------------------------------------------
		if( szYD_CRN_GRAB_GP.equals("D") )	{			//2호기 - 1Grab
			
			int intYD_CRN_BEAM_L			= 	  0;	//Beam길이
			//----------------------------------------------------------------------------
		
			if(szYD_UP_STK_COL_GP.startsWith("KA")){
				intYD_CRN_BEAM_L			=  14000;	//Beam길이 
			}else if(szYD_UP_STK_COL_GP.startsWith("KC")||
					 szYD_UP_STK_COL_GP.startsWith("KD")){
				intYD_CRN_BEAM_L			=  9200;	//Beam길이 
			}else if(szYD_UP_STK_COL_GP.startsWith("KE")){
				intYD_CRN_BEAM_L			=  6800;	//Beam길이 
			}else if(szYD_UP_STK_COL_GP.startsWith("KF")){
				intYD_CRN_BEAM_L			= 12600;	//Beam길이
			}
			
			szMsg ="["+szOperationName+"] 크레인작업재료들중에서 길이가 제일 긴 재료번호[" + szSTL_NO + "]의 제품길이["+intYD_MTL_L+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
			//----------------------------------------------------------------------------
			//	1Grab Crn - From위치 Y1좌표
			//----------------------------------------------------------------------------
			
			if( szYD_UP_STK_COL_GP.substring(2).matches("\\d\\d\\d\\d") 			/* 일반베드 */
				|| szYD_UP_STK_COL_GP.substring(2, 4).equals("RT")					/* ROLLER TABLE */
			) {
				if( szYD_UP_STK_COL_GP.substring(2, 4).equals("RT") ) {				/* ROLLER TABLE */
					if(szYD_UP_STK_COL_GP.startsWith("KE")){						// E 동
						if( szYD_UP_STK_BED_NO.equals("85")){
							szYD_UP_STK_BED_NO	= "04";
						} else if(szYD_UP_STK_BED_NO.equals("90")){
							szYD_UP_STK_BED_NO	= "03";
						} else if(szYD_UP_STK_BED_NO.equals("95")){
							szYD_UP_STK_BED_NO	= "02";
						} else if(szYD_UP_STK_BED_NO.equals("A0")){
							szYD_UP_STK_BED_NO	= "01";
						}
					} else { 														// F 동
					   if(szYD_UP_STK_BED_NO.equals("B0")){
							szYD_UP_STK_BED_NO	= "03";
						}else{
							szYD_UP_STK_BED_NO	= "01";
						}
					}	
				}
				
				intUp_Grab_Y1_Value			= procY1CalFor1GrabCrn( intYD_UP_STK_BED_YAXIS, intYD_MTL_L, intYD_CRN_BEAM_L);
			}
			
			szMsg ="["+szOperationName+"] 1Grab Crn[2호기:"+szYD_EQP_ID+"]의 차량이 아닌 경우 From위치 Y1좌표["+intUp_Grab_Y1_Value+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------
			
			//----------------------------------------------------------------------------
			//	1Grab Crn - To위치 Y1좌표
			//----------------------------------------------------------------------------
			
			if( szYD_DN_STK_COL_GP.substring(2).matches("\\d\\d\\d\\d") 			/* 일반베드 */
			) {
				
				intDn_Grab_Y1_Value			= procY1CalFor1GrabCrn(intYD_DN_STK_BED_YAXIS, intYD_MTL_L, intYD_CRN_BEAM_L);
			}
			
			szMsg ="["+szOperationName+"] 1Grab Crn[2호기:"+szYD_EQP_ID+"]의 차량이 아닌 경우 To위치 Y1좌표["+intDn_Grab_Y1_Value+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------
			
			//----------------------------------------------------------------------------
			//	차량하차인 경우 From위치 Y1보정
			//----------------------------------------------------------------------------
			if( szYD_UP_STK_COL_GP.substring(2, 4).equals("PT") ) {
				
				if( ( szYD_UP_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) && szUP_CARD_NO.startsWith("P")	)						/* 출하Pallet */
					|| ( szYD_UP_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_TS) && szUP_TRN_EQP_CD.substring(1, 3).equals("PT")	)		/* 구내운송Pallet */
				) {
					intUp_Grab_Y1_Value			= intYD_UP_STK_BED_YAXIS + (intDn_Grab_Y1_Value - intYD_DN_STK_BED_YAXIS);
				}else{															/* Trailer */
					intUp_Grab_Y1_Value			= intDn_Grab_Y1_Value;
				}
			}
			//----------------------------------------------------------------------------
			
			//----------------------------------------------------------------------------
			//	차량상차인 경우 To위치 Y1보정
			//----------------------------------------------------------------------------
			if( szYD_DN_STK_COL_GP.substring(2, 4).equals("PT") ) {
				
				if( ( szYD_DN_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) && szDN_CARD_NO.startsWith("P") )							/* 출하Pallet */
						|| ( szYD_DN_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_TS) && szDN_TRN_EQP_CD.substring(1, 3).equals("PT") )	/* 구내운송Pallet */
				) {
					intDn_Grab_Y1_Value			= intYD_DN_STK_BED_YAXIS + (intUp_Grab_Y1_Value - intYD_UP_STK_BED_YAXIS);
				}else{																/* Trailer */
					intDn_Grab_Y1_Value			= intUp_Grab_Y1_Value;
				}
			}
			//----------------------------------------------------------------------------

			szCrane_Grab_Use_Gp				= "1";
			
			szMsg ="["+szOperationName+"] 1Grab Crn[2호기:"+szYD_EQP_ID+"]의 From위치 Y1좌표["+intUp_Grab_Y1_Value+"], To위치 Y1좌표["+intDn_Grab_Y1_Value+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
		//----------------------------------------------------------------------------
		//	 2Grab
		//----------------------------------------------------------------------------
		}else{														//1호기 - 2Grab
			
			szMsg ="["+szOperationName+"] EF동은 2Grab 없슴  기준확인";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
			//----------------------------------------------------------------------------
		}
		
		//----------------------------------------------------------------------------
		
		recOutPara.setField("Crane_Grab_Use_Gp", 		szCrane_Grab_Use_Gp);
		recOutPara.setField("Up_Grab_X_Value", 			szUp_Grab_X_Value);
		recOutPara.setField("Up_Grab_Y_Value", 			szUp_Grab_Y_Value);
		recOutPara.setField("Up_Grab_Y1_Value", 		String.valueOf(intUp_Grab_Y1_Value));
		recOutPara.setField("Up_Grab_Y2_Value", 		String.valueOf(intUp_Grab_Y2_Value));
		recOutPara.setField("Dn_Grab_X_Value", 			szDn_Grab_X_Value);
		recOutPara.setField("Dn_Grab_Y_Value", 			szDn_Grab_Y_Value);
		recOutPara.setField("Dn_Grab_Y1_Value", 		String.valueOf(intDn_Grab_Y1_Value));
		recOutPara.setField("Dn_Grab_Y2_Value", 		String.valueOf(intDn_Grab_Y2_Value));
		//---------------------------------------------------------------------------------------------------------
		
    	return YdConstant.RETN_CD_SUCCESS;
    }
    
	/**
	 * SMS SENDER
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
    public static String updSmsMsgSend(JDTORecord recInPara) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;
		
		szMsg        				= "";
		szMethodName 				= "updSmsMsgSend";
		String szOperationName 		= "SMS SENDER";
		 
		JDTORecord	inRecord 		= null;
		try {
			
			szMsg = "SMS SENDER 시작";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.INFO);
			
			// JDTORecord 생성
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("FROM_SENDER_NAME", new String("야드관리"));	// SMS 보내는 사람 성명
			
			inRecord.setField("FROM_PHONE_NO", recInPara.getFieldString("FROM_PHONE_NO"));	// SMS 보내는 사람 핸드펀번호
			inRecord.setField("TO_PHONE_NO"  , recInPara.getFieldString("TO_PHONE_NO"));	// SMS 받는 사람 핸드펀번호
			inRecord.setField("TO_CONTENT"   , recInPara.getFieldString("TO_CONTENT"));		// SMS 전송 내용
			inRecord.setField("TO_SEND_TIME" , new String(""));								// SMS 전송시간
			
			//---------------------------------------------------------------------
//			// SMS전송 객체
//			SmsSender	sender			= null;	
//			// 객체생성
//		    sender = new SmsSender();
//		    // 객체초기화
//		    sender.initService();
//		
//		    sender.send(inRecord);
			//---------------------------------------------------------------------
		    
			//---------------------------------------------------------------------
		    MessageSenderAuto    sender = new MessageSenderAuto();
		    inRecord.setField("RECV_ID", "YD00001");
    		inRecord.setField("GROUP_ID", "MMS1");
		    inRecord.setField("PROGRAM_ID", "updSmsMsgSendYD");

		    sender.sendAutoSMS(inRecord);
		    //---------------------------------------------------------------------

			
			szMsg = "SMS SENDER 끝";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.INFO);
			
		}catch(Exception ex) {
			szMsg = "["+szOperationName+"] SMS 송신 ERROR - 메세지 : " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
		}
		return YdConstant.RETN_CD_SUCCESS;
		
		
	}	// end of updSmsMsgSend
    
    
    
    
    /**
	 * MMS SENDER
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
    public static String updMmsMsgSend(JDTORecord recInPara) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;
		
		szMsg        				= "";
		szMethodName 				= "updMmsMsgSend";
		String szOperationName 		= "MMS SENDER";
		 
		JDTORecord	inRecord 		= null;
		try {
			
			szMsg = "MMS SENDER 시작";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.INFO);
			
			// JDTORecord 생성
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("FROM_SENDER_NAME", new String("야드관리"));	// SMS 보내는 사람 성명
			
			inRecord.setField("FROM_PHONE_NO", recInPara.getFieldString("FROM_PHONE_NO"));	// SMS 보내는 사람 핸드펀번호
			inRecord.setField("TO_PHONE_NO"  , recInPara.getFieldString("TO_PHONE_NO"));	// SMS 받는 사람 핸드펀번호
			inRecord.setField("TO_CONTENT"   , recInPara.getFieldString("TO_CONTENT"));		// SMS 전송 내용
			inRecord.setField("TO_SUBJECT"   , recInPara.getFieldString("TO_SUBJECT"));       // SMS 제목
			inRecord.setField("TO_SEND_TIME" , new String(""));								// SMS 전송시간
			
			//---------------------------------------------------------------------
//			// SMS전송 객체
//			SmsSender	sender			= null;	
//			// 객체생성
//		    sender = new SmsSender();
//		    // 객체초기화
//		    sender.initService();
//		
//		    sender.send(inRecord);
			//---------------------------------------------------------------------
		    
			//---------------------------------------------------------------------
		    MessageSenderAuto    sender = new MessageSenderAuto();
		    inRecord.setField("RECV_ID", "YD00002");
    		inRecord.setField("GROUP_ID", "MMS2");
		    inRecord.setField("PROGRAM_ID", "updMmsMsgSendYD");

		    sender.sendAutoMMS(inRecord);
		    //---------------------------------------------------------------------

			
			szMsg = "MMS SENDER 끝";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.INFO);
			
		}catch(Exception ex) {
			szMsg = "["+szOperationName+"] MMS 송신 ERROR - 메세지 : " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
		}
		return YdConstant.RETN_CD_SUCCESS;
		
		
	}	// end of updMmsMsgSend
	
    /**
     * 전사물류개선 프로젝트 후판제품신규모듈 적용여부를 판단한다.
     * 
     * 
     * @param sDiv(CRN:크레인, DM:출하 )
     * @return true or false
     * @throws JDTOException
     */
    public static boolean isPlateNewMoudleApply(String sDiv) throws JDTOException {
    	
    	boolean isNew = false;
    	String szMethodName = "isPlateNewMoudleApply";
    	String szMsg = "통합후판제품 신규모듈 적용여부 판단";
    	
    	YdPlateCommDAO 	commDao = null;
    	JDTORecord recInPara = null;
    	JDTORecordSet outStlInfoRecSet = null;
    	ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	
    	try{
    		recInPara = JDTORecordFactory.getInstance().create();
    		outStlInfoRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
    		
        	commDao = new YdPlateCommDAO(); //FlagYn
        	int recordCnt = commDao.select(recInPara, outStlInfoRecSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getPlateNewModuleEffYn");
        	if(recordCnt>0){
        		
        		String sColumn = "";
        		if("CRN".equals(sDiv)){
        			sColumn = "CRN_EFF_YN";
        		}
        		else if("DM".equals(sDiv)){
        			sColumn = "DM_EFF_YN";
        		}
        		else{
        			sColumn = "MODULE_YN ";
        		}
        		
    			if("Y".equals( StringHelper.evl(outStlInfoRecSet.getRecord(0).getFieldString(sColumn),"N")) ){
    				isNew = true;
    			}
        	} 
        	szMsg = "통합후판제품 신규모듈 적용여부 판단 " + isNew;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
    	}catch(Exception e){
    		szMsg = "통합후판제품 신규모듈 적용여부 판단중 오류발생하여 false를 Return한다.";
    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
    		isNew = false;
    	}
    	
    	return isNew;
    }
    
    /**
     * 전사물류개선 프로젝트 Y9(후판제품자동화L2시스템) 전송여부를 판단한다.
     * 
     * 
     * @param JDTORecord recInPara(STL_NO, YD_STK_COL_GP, YD_EQP_ID, TRANS_ORD_DATE && TRANS_ORD_SEQNO)
     * @return true or false
     * @throws JDTOException
     */
    public static boolean isSendToEaiY9(JDTORecord recInPara) throws JDTOException {
    	
    	boolean isNew = false;
    	String szMethodName = "isSendToEaiY9";
    	String szMsg = "통합후판제품 Y9(후판제품자동화L2시스템) 전송여부 판단";
    	
    	YdPlateCommDAO 	commDao = null;
    	JDTORecordSet outStlInfoRecSet = null;
    	ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//    	ydUtils.displayRecord(szMethodName, recInPara);
    	
    	try{ 
    		
    		outStlInfoRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
    		
        	commDao = new YdPlateCommDAO(); //FlagYn
        	int recordCnt = commDao.select(recInPara, outStlInfoRecSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getYDY9SendYN");
        	if(recordCnt>0){
        		
    			if("Y".equals( StringHelper.evl(outStlInfoRecSet.getRecord(0).getFieldString("SEND_TO_EAI_Y9_YN"),"N")) ){
    				isNew = true;
    			}
        	}
        	szMsg = "통합후판제품 Y9(후판제품자동화L2시스템) 전송여부 판단 " + isNew;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
    	}catch(Exception e){
    		szMsg = "통합후판제품 Y9(후판제품자동화L2시스템) 전송여부 판단중 오류발생하여 false Return";
    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
    		isNew = false;
    	}
    	
    	return isNew;
    }
     
    public static boolean isSendToEaiY9_ydStkColGp(String sYdStkColGp) throws JDTOException {
    	if("".equals(sYdStkColGp))
    		return false;
    	
    	JDTORecord parmas = JDTORecordFactory.getInstance().create();
    	parmas.setField("YD_STK_COL_GP", sYdStkColGp);
    	
    	return isSendToEaiY9(parmas);
    }
    
    
    public static boolean isSendToEaiY9_ydEqpId(String sYdEqpId) throws JDTOException {
    	if("".equals(sYdEqpId))
    		return false;
    	
    	JDTORecord parmas = JDTORecordFactory.getInstance().create();
    	parmas.setField("YD_EQP_ID", sYdEqpId);
    	
    	return isSendToEaiY9(parmas);
    }
    
    public static boolean isSendToEaiY9_stlNo(String sStlNo) throws JDTOException {
    	if("".equals(sStlNo))
    		return false;
    	
    	JDTORecord parmas = JDTORecordFactory.getInstance().create();
    	parmas.setField("STL_NO", sStlNo);
    	
    	return isSendToEaiY9(parmas);
    }
     
    /**
     * 전사물류개선프로젝트 신규모듈 적용여부
     *  - 관련 DIV 코드 : TB_YD_RULE.DTL_ITEM1에 정의하여 사용함
     *  - 관련 DATA : TB_YD_RULE 테이블내 REPR_CD_GP LIKE 'T3%' AND CD_GP = 'T' AND ITEM LIKE 'APP%' 
     *  
     * @param String sDiv : 
     * @return true or false
     * @throws JDTOException
     */
    public static boolean isApplyYn(String sDiv) throws JDTOException {
    	
    	boolean isAsIsTobe = false;
    	String szMethodName = "isSendToEaiY9";
    	String szMsg = "통합후판제품 Y9(후판제품자동화L2시스템) 적용여부 판단";
    	
    	YdPlateCommDAO 	commDao = null;
    	JDTORecordSet outStlInfoRecSet = null;
    	ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//    	ydUtils.displayRecord(szMethodName, recInPara);
    	
    	try{ 
    		
    		outStlInfoRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
    		
        	commDao = new YdPlateCommDAO(); //FlagYn
        	JDTORecord recInPara = JDTORecordFactory.getInstance().create();
        	recInPara.setField("APPLY_DIV",sDiv);
        	
        	int recordCnt = commDao.select(recInPara, outStlInfoRecSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getPlateGdsApplyYn");
        	if(recordCnt>0){
    			if("Y".equals( StringHelper.evl(outStlInfoRecSet.getRecord(0).getFieldString("ITEM1"),"N")) ){
    				isAsIsTobe = true;
    			}
        	}
        	szMsg = "전사물류개선 적용여부(" + isAsIsTobe + ")";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
    	}catch(Exception e){
    		szMsg = "전사물류개선 적용여부 판단중 오류발생하여 false Return";
    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
    		isAsIsTobe = false;
    	}
    	
    	return isAsIsTobe;
    }
        
	/**
	 * 문자열이 null 일때 임의의 문자열을 반환한다.
	 * @param value
	 * @param defaultValue
	 * @return String
	 */
	public static String nvl(String value, String defaultValue) {
		return (value == null || "".equals(value) || "NULL".equals(value.trim().toUpperCase())) ? defaultValue : value;
	}

	public static String nvl(Object o, String defaultValue) {
		return (o == null) ? defaultValue : o.toString();
	}

	/**
	 * 문자열이 null 일때 ""을 반환한다.
	 * @param value
	 * @return String
	 */
	public static String trim(String value) {
		return (value == null || "NULL".equals(value.trim().toUpperCase())) ? "" : value.trim();
	}

	/**
	 * Object가 null 일때 true를 반환한다.
	 * @param obj
	 * @return boolean
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof String) {
			if ("".equals(obj)) {
				return true;
			}
		} else if (obj instanceof JDTORecord) {
			if (((JDTORecord)obj).size() <= 0) {
				return true;
			}
		} else if (obj instanceof JDTORecord[]) {
			if (((JDTORecord[])obj).length <= 0) {
				return true;
			}
		} else if (obj instanceof JDTORecordSet) {
			if (((JDTORecordSet)obj).size() <= 0) {
				return true;
			}
		} else if (obj instanceof Object[]) {
			if (((Object[])obj).length <= 0) {
				return true;
			}
		} else if (obj instanceof Object[][]) {
			if (((Object[][])obj).length <= 0) {
				return true;
			}
		}

		return false;
	} 
	
	/**
	 * 숫자형을 입력받은 형태로 Return한다.
	 *  - 만약 정수부의 자릿수가 초과되면 포멧이 제대로 작동하지 않음
	 *  - 소수부의 자릿수 초과시 반올림 처리
	 * @return
	 */	
	public static String genDecmailFomatter(String sFormat, String sNum){
		
		String rtnStr = "";
		float fval = 0;
		BigDecimal nBigDec = null;
		
		try{
			if(!isEmpty(sFormat)){
				
				if(isEmpty(sNum)){
					sNum= "0";
				};
				
				DecimalFormat decf = new DecimalFormat(sFormat);
				nBigDec = new BigDecimal(trim(sNum)); 
				fval = nBigDec.floatValue();
				rtnStr = decf.format(nBigDec);
				
				// 음수가 포함되어 있다면
				// 자릿수 확인하여 공백부터 채우자.
				if(fval<0){
					decf = null;
					String sNewFormat = sFormat;
					for(int i=0; i<rtnStr.length(); i++){
						if( rtnStr.charAt(i) == '.'){
							break;
						}
						else if( rtnStr.charAt(i) == '0'){
							sNewFormat = sFormat.substring(i,sFormat.length());
							break;
						}
					}
					decf = new DecimalFormat(sNewFormat);
					rtnStr = decf.format(nBigDec); 
				}
			}
			
			rtnStr = StringHelper.replaceStr(rtnStr, ".", "");
		}catch(Exception e){
			return sNum;
		}
		
		return rtnStr;
	}
	
	   /**
     * 좌표계산(후판제품)
     * @param recInPara
     * @param recOutPara
     * @return
     * @throws JDTOException
     */
    public static String PlateCraneXYCal_PIDEV(JDTORecord recInPara, JDTORecord recOutPara) throws JDTOException {
    	String szOperationName		= "좌표계산(후판제품PI)";
    	String szMethodName			= "PlateCraneXYCal_PIDEV";
    	String szMsg				= null;
    	String szRtnMsg				= null;
    	
    	JDTORecord		recPara		= null;
    	JDTORecord		recTemp		= null;
    	JDTORecordSet	outRecSet	= null;
    	//-------------------------------------------------------------------------------------------------
    	//	파라미터로 전달되는 항목 정의
    	//-------------------------------------------------------------------------------------------------
    	String szSTL_NO				= null;						//크레인작업재료들중에서 길이가 제일 긴 재료번호
    	String szYD_UP_STK_COL_GP	= null;						//권상지시위치 - 적치열
    	String szYD_UP_STK_BED_NO	= null;						//권상지시위치 - 적치베드
    	String szYD_DN_STK_COL_GP	= null;						//권하지시위치 - 적치열
    	String szYD_DN_STK_BED_NO	= null;						//권하지시위치 - 적치베드
    	String szYD_EQP_ID			= null;						//크레인설비ID
    	//-------------------------------------------------------------------------------------------------
    	
    	String szYD_CRN_GRAB_GP		= null;						//Grab Type
    	String szYD_CRN_TONG_L		= null;						//Crane Beam 길이
    	String szYD_CRN_GRAB_TP		= null;						//1,2 Grab 구분
    	
    	String szYD_MTL_W			= null;						//재료 폭
    	int intYD_MTL_W				= 0;						//재료 폭
    	String szYD_MTL_L			= null;						//재료 길이
    	int intYD_MTL_L				= 0;						//재료 길이
    	
    	//-------------------------------------------------------------------------------------------------
    	//	권상위치 정보
    	//-------------------------------------------------------------------------------------------------
    	String szYD_UP_STK_COL_BED_L_TP = null;					//권상지시위치 - 야드적치열Bed길이Type
    	String szYD_UP_CAR_USE_GP		= null;					//차량사용구분
    	String szUP_TRN_EQP_CD			= null;					//운송장비코드
    	String szUP_CAR_NO				= null;					//차량번호
    	String szUP_CARD_NO				= null;					//카드번호
    	String szUP_CAR_KIND			= null;					//차량종류
    	
    	int intYD_UP_STK_BED_XAXIS		= 0;					//권상지시위치베드 - 야드적치BedX축
    	int intYD_UP_STK_BED_YAXIS		= 0;					//권상지시위치베드 - 야드적치BedY축
    	String szYD_UP_STK_BED_L_GP		= null;					//권상지시위치베드 - 야드적치Bed길이구분
    	String szYD_UP_STK_BED_W_GP 	= null;					//권상지시위치베드 - 야드적치Bed폭구분
        String szYD_UP_STK_BED_WHIO_STAT= null;					//권상지시위치베드 - 야드적치Bed상태구분
        String szYD_UP_STK_BED_W_MAX	= null;
    	int intYD_UP_STK_BED_W_MAX		= 0;
    	int intYD_UP_STK_BED_L_MAX		= 0;
    	//-------------------------------------------------------------------------------------------------
    	//	권하위치 정보
    	//-------------------------------------------------------------------------------------------------
    	String szYD_DN_STK_COL_BED_L_TP = null;					//권하지시위치 - 야드적치열Bed길이Type
    	String szYD_DN_CAR_USE_GP		= null;					//차량사용구분
    	String szDN_TRN_EQP_CD			= null;					//운송장비코드
    	String szDN_CAR_NO				= null;					//차량번호
    	String szDN_CARD_NO				= null;					//카드번호
    	String szDN_CAR_KIND			= null;					//차량종류
    	
    	int intYD_DN_STK_BED_XAXIS		= 0;					//권하지시위치베드 - 야드적치BedX축
    	int intYD_DN_STK_BED_YAXIS		= 0;					//권하지시위치베드 - 야드적치BedY축
    	String szYD_DN_STK_BED_L_GP		= null;					//권하지시위치베드 - 야드적치Bed길이구분
    	String szYD_DN_STK_BED_W_GP 	= null;					//권하지시위치베드 - 야드적치Bed폭구분
    	String szYD_DN_STK_BED_WHIO_STAT= null;					//권하지시위치베드 - 야드적치Bed상태구분
    	
    	String szYD_DN_STK_BED_W_MAX	= null;
    	int intYD_DN_STK_BED_W_MAX		= 0;
    	int intYD_DN_STK_BED_L_MAX		= 0;
    	//-------------------------------------------------------------------------------------------------
    	
    	String szCrane_Grab_Use_Gp 		= null;
		String szUp_Grab_X_Value     	= null;
		String szUp_Grab_Y_Value     	= null;
		int intUp_Grab_Y1_Value     	= 0;
		int intUp_Grab_Y2_Value     	= 0;
		
		String szDn_Grab_X_Value     	= null;
		String szDn_Grab_Y_Value     	= null;
		int intDn_Grab_Y1_Value     	= 0;
		int intDn_Grab_Y2_Value     	= 0;
    	
    	szMsg ="["+szOperationName+"] -------------------------- 메소드 시작 - 파라미터 확인 --------------------------" ;
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szSTL_NO					= ydDaoUtils.paraRecChkNull(recInPara, "STL_NO");					//크레인작업재료들중에서 길이가 제일 긴 재료번호
		szYD_UP_STK_COL_GP			= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_COL_GP");			//권상지시위치 - 적치열
		szYD_UP_STK_BED_NO			= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_BED_NO");			//권상지시위치 - 적치베드
		szYD_DN_STK_COL_GP			= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_COL_GP");			//권하지시위치 - 적치열
		szYD_DN_STK_BED_NO			= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_BED_NO");			//권하지시위치 - 적치베드
		szYD_EQP_ID					= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID");				//크레인설비ID
		szYD_MTL_W 					= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W"); 				//크레인작업재료들중에서 폭이 제일 넓은 재료의 폭 값
		
		szMsg ="["+szOperationName+"] 크레인작업재료들중에서 길이가 제일 긴 재료번호 - " + szSTL_NO;
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분 - " + szYD_UP_STK_COL_GP + ", 베드번호 - " + szYD_UP_STK_BED_NO + "]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분 - " + szYD_DN_STK_COL_GP + ", 베드번호 - " + szYD_DN_STK_BED_NO + "]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 크레인설비ID - " + szYD_EQP_ID;
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);

		
		if (szYD_UP_STK_COL_GP.startsWith("KE")||szYD_UP_STK_COL_GP.startsWith("KF")) {
			szRtnMsg	= PlateCraneXYCal_EF_PIDEV(recInPara, recOutPara);
			return szRtnMsg;
		}
		
		
		//---------------------------------------------------------------------------------------------------------
		// 저장품 조회
		// 제품길이 (YD_MTL_L) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 재료번호[" + szSTL_NO + "]로 저장품 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("STL_NO", szSTL_NO);
		
		szRtnMsg	= DaoManager.getYdStock(recPara, outRecSet, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 재료번호[" + szSTL_NO + "]로 저장품 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		szYD_MTL_L 				= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L"); 			// 제품길이
		
		int pointIdx			= szYD_MTL_W.lastIndexOf(".");
		
		if( pointIdx >= 0 ) {
			szYD_MTL_W			= szYD_MTL_W.substring(0, pointIdx);
		}
		
		intYD_MTL_W				= Integer.parseInt(szYD_MTL_W);
		intYD_MTL_L				= Integer.parseInt(szYD_MTL_L);
		
		szMsg ="["+szOperationName+"] 크레인작업재료들중에서 길이가 제일 긴 재료번호[" + szSTL_NO + "] - 제품길이["+szYD_MTL_L+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 크레인작업재료들중에서 폭이 제일 넓은 재료의 제품폭["+szYD_MTL_W+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		// 크레인사양 조회
		// 설비ID로  크레인 Beam 길이(YD_CRN_TONG_L) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]로 크레인사양 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		recPara 	= JDTORecordFactory.getInstance().create();
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara.setField("YD_EQP_ID", szYD_EQP_ID);
		
		szRtnMsg	= DaoManager.getYdCrnspec(recPara, outRecSet, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]로 크레인사양 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		szYD_CRN_TONG_L = ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_TONG_L"); // 크레인 Beam 길이
		
		szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]의 크레인Beam길이[YD_CRN_TONG_L:"+szYD_CRN_TONG_L+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szYD_CRN_GRAB_TP = ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_GRAB_TP"); // 크레인 Grab 구분
		
		if("X".equals(szYD_CRN_GRAB_TP)){
			szYD_CRN_GRAB_GP = "E";
		}else{
			szYD_CRN_GRAB_GP = "D";
		}
		
		szMsg ="["+szOperationName+"] 크레인 Grab 구분[D : 1Grab Crane, E : 2Grab Crane] - " + szYD_CRN_GRAB_GP;
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		// 권상지시위치 - 적치열조회 : 야드적치열Bed길이Type, 차량정보가 존재하면 차량정보 조회
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "]로 적치열 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYD_UP_STK_COL_GP);
		
//PIDEV			
		szRtnMsg	= DaoManager.getYdStkcol(recPara, outRecSet, 900);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "]로 적치열 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		szYD_UP_STK_COL_BED_L_TP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_BED_L_TP"); 		//야드적치열Bed길이Type
		szYD_UP_CAR_USE_GP					= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_USE_GP"); 				//차량사용구분
		szUP_TRN_EQP_CD						= ydDaoUtils.paraRecChkNull(recTemp, "TRN_EQP_CD"); 				//운송장비코드
		szUP_CAR_NO							= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO"); 					//차량번호
		szUP_CARD_NO						= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO"); 					//카드번호
		szUP_CAR_KIND						= ydDaoUtils.paraRecChkNull(recTemp, "CAR_KIND"); 					//차량종류
		
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "]로 적치열 조회 완료 - 야드적치열Bed길이Type["+szYD_UP_STK_COL_BED_L_TP+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "] - 차량사용구분["+szYD_UP_CAR_USE_GP+"], 운송장비코드["+szUP_TRN_EQP_CD+"], 차량번호["+szUP_CAR_NO+"], 카드번호["+szUP_CARD_NO+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		// 권하지시위치 - 적치열조회 : 야드적치열Bed길이Type, 차량정보가 존재하면 차량정보 조회
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "]로 적치열 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYD_DN_STK_COL_GP);
		
//PIDEV			
		szRtnMsg	= DaoManager.getYdStkcol(recPara, outRecSet, 900);
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "]로 적치열 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		szYD_DN_STK_COL_BED_L_TP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_BED_L_TP"); 		//야드적치열Bed길이Type
		szYD_DN_CAR_USE_GP					= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_USE_GP"); 				//차량사용구분
		szDN_TRN_EQP_CD						= ydDaoUtils.paraRecChkNull(recTemp, "TRN_EQP_CD"); 				//운송장비코드
		szDN_CAR_NO							= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO"); 					//차량번호
		szDN_CARD_NO						= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO"); 					//카드번호
		szDN_CAR_KIND						= ydDaoUtils.paraRecChkNull(recTemp, "CAR_KIND"); 					//차량종류
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "]로 적치열 조회 완료 - 야드적치열Bed길이Type["+szYD_DN_STK_COL_BED_L_TP+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "] - 차량사용구분["+szYD_DN_CAR_USE_GP+"], 운송장비코드["+szDN_TRN_EQP_CD+"], 차량번호["+szDN_CAR_NO+"], 카드번호["+szDN_CARD_NO+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------

		//---------------------------------------------------------------------------------------------------------
		// 권상지시위치 - 적치Bed 조회
		// 야드적치열구분, 야드적치Bed번호로  야드적치BedY축(YD_STK_BED_YAXIS) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + ", 베드번호:" + szYD_UP_STK_BED_NO + "]로 베드 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYD_UP_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", szYD_UP_STK_BED_NO);
		
		szRtnMsg	= DaoManager.getYdStkbed(recPara, outRecSet, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + ", 베드번호:" + szYD_UP_STK_BED_NO + "]로 베드 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		intYD_UP_STK_BED_XAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS"); 		// 야드적치BedX축
		intYD_UP_STK_BED_YAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS"); 		// 야드적치BedY축
		szYD_UP_STK_BED_L_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP"); 			// 야드적치Bed길이구분
		szYD_UP_STK_BED_W_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP"); 			// 야드적치Bed폭구분
		szYD_UP_STK_BED_WHIO_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT"); 		// 야드적치Bed상태구분
		szYD_UP_STK_BED_W_MAX			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_MAX"); 			// 야드적치Bed폭Max
		intYD_UP_STK_BED_L_MAX			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_L_MAX"); 		// 야드적치Bed길이Max
		
		pointIdx	= szYD_UP_STK_BED_W_MAX.lastIndexOf(".");
		
		if( pointIdx >= 0 ) {
			szYD_UP_STK_BED_W_MAX		= szYD_UP_STK_BED_W_MAX.substring(0, pointIdx);
		}
		
		intYD_UP_STK_BED_W_MAX			= Integer.parseInt(szYD_UP_STK_BED_W_MAX);
		
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + ", 베드번호:" + szYD_UP_STK_BED_NO + "]의  야드적치BED X축["+intYD_UP_STK_BED_XAXIS+"], 야드적치BED Y축["+intYD_UP_STK_BED_YAXIS+"], 야드적치Bed길이구분["+szYD_UP_STK_BED_L_GP+"] 조회 완료";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		
    	
		//---------------------------------------------------------------------------------------------------------
		// 권하지시위치 - 적치Bed 조회
		// 야드적치열구분, 야드적치Bed번호로  야드적치BedY축(YD_STK_BED_YAXIS) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + ", 베드번호:" + szYD_DN_STK_BED_NO + "]로 베드 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYD_DN_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", szYD_DN_STK_BED_NO);
		
		szRtnMsg	= DaoManager.getYdStkbed(recPara, outRecSet, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + ", 베드번호:" + szYD_DN_STK_BED_NO + "]로 베드 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		intYD_DN_STK_BED_XAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS"); 		// 야드적치BedX축
		intYD_DN_STK_BED_YAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS"); 		// 야드적치BedY축
		szYD_DN_STK_BED_L_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP"); 			// 야드적치Bed길이구분
		szYD_DN_STK_BED_W_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP"); 			// 야드적치Bed폭구분
		szYD_DN_STK_BED_WHIO_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT"); 		// 야드적치Bed상태구분
		szYD_DN_STK_BED_W_MAX			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_MAX"); 			// 야드적치Bed폭Max
		intYD_DN_STK_BED_L_MAX			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_L_MAX"); 		// 야드적치Bed길이Max
		
		pointIdx	= szYD_DN_STK_BED_W_MAX.lastIndexOf(".");
		
		if( pointIdx >= 0 ) {
			szYD_DN_STK_BED_W_MAX		= szYD_DN_STK_BED_W_MAX.substring(0, pointIdx);
		}
		
		intYD_DN_STK_BED_W_MAX			= Integer.parseInt(szYD_DN_STK_BED_W_MAX);
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + ", 베드번호:" + szYD_DN_STK_BED_NO + "]의  야드적치BED X축["+intYD_DN_STK_BED_XAXIS+"], 야드적치BED Y축["+intYD_DN_STK_BED_YAXIS+"], 야드적치Bed길이구분["+szYD_DN_STK_BED_L_GP+"] 조회 완료";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		//	X좌표 구하기
		//---------------------------------------------------------------------------------------------------------
		szUp_Grab_X_Value			= String.valueOf(procXCalForPlateYd(intYD_UP_STK_BED_XAXIS, intYD_MTL_W, intYD_UP_STK_BED_W_MAX, szYD_UP_STK_COL_GP,szYD_UP_STK_BED_W_GP,szYD_UP_STK_BED_WHIO_STAT));
		szDn_Grab_X_Value			= String.valueOf(procXCalForPlateYd(intYD_DN_STK_BED_XAXIS, intYD_MTL_W, intYD_DN_STK_BED_W_MAX, szYD_DN_STK_COL_GP,szYD_DN_STK_BED_W_GP,szYD_DN_STK_BED_WHIO_STAT));
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		//	Y좌표 구하기 
		//---------------------------------------------------------------------------------------------------------
		//(Tmp_YD_MTL_L / 2)
		/*
		int iUpLsize = 0;  
		if(szYD_UP_STK_COL_GP.startsWith("KB01")){
			if("U".equals(szYD_UP_STK_BED_L_GP)){		iUpLsize = 6800;
			}else if("S".equals(szYD_UP_STK_BED_L_GP)){ iUpLsize = 9200;
			}else if("M".equals(szYD_UP_STK_BED_L_GP)){ iUpLsize = 14000;
			}else if("L".equals(szYD_UP_STK_BED_L_GP)){	iUpLsize = 18600;
			}else if("X".equals(szYD_UP_STK_BED_L_GP)){	iUpLsize = 25000;
			}
			szUp_Grab_Y_Value			= String.valueOf(intYD_UP_STK_BED_YAXIS + (iUpLsize/2));
		}else{
			szUp_Grab_Y_Value			= String.valueOf(intYD_UP_STK_BED_YAXIS + (intYD_MTL_L/2));
		}
		*/
		szUp_Grab_Y_Value			= String.valueOf(intYD_UP_STK_BED_YAXIS + (intYD_MTL_L/2));
		/*
		int iDnLsize = 0; 
		if(szYD_DN_STK_COL_GP.startsWith("KB01")){
			if("U".equals(szYD_DN_STK_BED_L_GP)){		iDnLsize = 6800;	
			}else if("S".equals(szYD_DN_STK_BED_L_GP)){ iDnLsize = 9200;	
			}else if("M".equals(szYD_DN_STK_BED_L_GP)){ iDnLsize = 14000;	
			}else if("L".equals(szYD_DN_STK_BED_L_GP)){	iDnLsize = 18600;	
			}else if("X".equals(szYD_DN_STK_BED_L_GP)){	iDnLsize = 25000;	
			}
			szDn_Grab_Y_Value			= String.valueOf(intYD_DN_STK_BED_YAXIS + (iDnLsize/2));
		}else{
			szDn_Grab_Y_Value			= String.valueOf(intYD_DN_STK_BED_YAXIS + (intYD_MTL_L/2));
		}
		*/
		szDn_Grab_Y_Value			= String.valueOf(intYD_DN_STK_BED_YAXIS + (intYD_MTL_L/2));
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		//	Y1, Y2좌표 구하기
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "] - 야드적치열Bed길이Type["+szYD_UP_STK_COL_BED_L_TP+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "] - 차량사용구분["+szYD_UP_CAR_USE_GP+"], 운송장비코드["+szUP_TRN_EQP_CD+"], 차량번호["+szUP_CAR_NO+"], 카드번호["+szUP_CARD_NO+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "] - 야드적치열Bed길이Type["+szYD_DN_STK_COL_BED_L_TP+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "] - 차량사용구분["+szYD_DN_CAR_USE_GP+"], 운송장비코드["+szDN_TRN_EQP_CD+"], 차량번호["+szDN_CAR_NO+"], 카드번호["+szDN_CARD_NO+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		//----------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------
		//	2호기 - 1Grab
		//----------------------------------------------------------------------------
		if( szYD_CRN_GRAB_GP.equals("D") )	{			//2호기 - 1Grab
			
			int intYD_CRN_BEAM_L			= 	  0;	//Beam길이
			//----------------------------------------------------------------------------
		
			if(szYD_UP_STK_COL_GP.startsWith("KA")){
				intYD_CRN_BEAM_L			=  14000;	//Beam길이 
			}else if(szYD_UP_STK_COL_GP.startsWith("KC")||
					 szYD_UP_STK_COL_GP.startsWith("KD")){
				intYD_CRN_BEAM_L			=  9200;	//Beam길이 
			}else if(szYD_UP_STK_COL_GP.startsWith("KE")){
				intYD_CRN_BEAM_L			=  6800;	//Beam길이 
			}else if(szYD_UP_STK_COL_GP.startsWith("KF")){
				intYD_CRN_BEAM_L			= 12600;	//Beam길이
			}
			
			szMsg ="["+szOperationName+"] 크레인작업재료들중에서 길이가 제일 긴 재료번호[" + szSTL_NO + "]의 제품길이["+intYD_MTL_L+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
			//----------------------------------------------------------------------------
			//	1Grab Crn - From위치 Y1좌표
			//----------------------------------------------------------------------------
			
			if( szYD_UP_STK_COL_GP.substring(2).matches("\\d\\d\\d\\d") 			/* 일반베드 */
				|| szYD_UP_STK_COL_GP.substring(2, 4).equals("TF")					/* TRANSFER */
				|| szYD_UP_STK_COL_GP.substring(2, 4).equals("RT")					/* ROLLER TABLE */
				|| szYD_UP_STK_COL_GP.substring(2, 4).equals("GJ")					/* 가적장 */
			) {
				if( szYD_UP_STK_COL_GP.substring(2, 4).equals("TF") ) {				/* TRANSFER */
					if( szYD_UP_STK_BED_NO.equals("06") 							/* 06, 05베드를 03번지로 그 외의 베드는 01번지로 */
						|| szYD_UP_STK_BED_NO.equals("05")
					) {
						szYD_UP_STK_BED_NO			= "03";
					}else{
						szYD_UP_STK_BED_NO			= "01";
					}
				}else if( szYD_UP_STK_COL_GP.substring(2, 4).equals("RT") ) {		/* ROLLER TABLE */
					if( szYD_UP_STK_BED_NO.equals("10") 							/* 10, 30, 50, 70베드를 03번지로 그 외의 베드는 01번지로 */
							|| szYD_UP_STK_BED_NO.equals("30")
							|| szYD_UP_STK_BED_NO.equals("50")
							|| szYD_UP_STK_BED_NO.equals("70")
						) {
							szYD_UP_STK_BED_NO			= "03";
						}else{
							szYD_UP_STK_BED_NO			= "01";
						}
				}else if( szYD_UP_STK_COL_GP.substring(2, 4).equals("GJ") ) {		/* 가적장 */
					if( szYD_UP_STK_BED_NO.equals("06") ) {							/* 06베드를 03번지로 그 외의 베드는 01번지로 */
						szYD_UP_STK_BED_NO			= "03";
					}else{
						szYD_UP_STK_BED_NO			= "01";
					}
				}
				
				intUp_Grab_Y1_Value				= procY1CalFor1GrabCrn( intYD_UP_STK_BED_YAXIS, intYD_MTL_L, intYD_CRN_BEAM_L);
			}
			
			szMsg ="["+szOperationName+"] 1Grab Crn[2호기:"+szYD_EQP_ID+"]의 차량이 아닌 경우 From위치 Y1좌표["+intUp_Grab_Y1_Value+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------
			
			//----------------------------------------------------------------------------
			//	1Grab Crn - To위치 Y1좌표
			//----------------------------------------------------------------------------
			
			if( szYD_DN_STK_COL_GP.substring(2).matches("\\d\\d\\d\\d") 			/* 일반베드 */
					|| szYD_DN_STK_COL_GP.substring(2, 4).equals("TF")				/* TRANSFER */
					|| szYD_DN_STK_COL_GP.substring(2, 4).equals("RT")				/* ROLLER TABLE */
					|| szYD_DN_STK_COL_GP.substring(2, 4).equals("GJ")				/* 가적장 */
			) {
				if( szYD_DN_STK_COL_GP.substring(2, 4).equals("TF") ) {				/* TRANSFER */
					if( szYD_DN_STK_BED_NO.equals("06") 							/* 06, 05베드를 03번지로 그 외의 베드는 01번지로 */
						|| szYD_DN_STK_BED_NO.equals("05")
					) {
						szYD_DN_STK_BED_NO			= "03";
					}else{
						szYD_DN_STK_BED_NO			= "01";
					}
				}else if( szYD_DN_STK_COL_GP.substring(2, 4).equals("RT") ) {		/* ROLLER TABLE */
					if( szYD_DN_STK_BED_NO.equals("10") 							/* 10, 30, 50, 70베드를 03번지로 그 외의 베드는 01번지로 */
							|| szYD_DN_STK_BED_NO.equals("30")
							|| szYD_DN_STK_BED_NO.equals("50")
							|| szYD_DN_STK_BED_NO.equals("70")
						) {
						szYD_DN_STK_BED_NO			= "03";
						}else{
							szYD_DN_STK_BED_NO			= "01";
						}
				}else if( szYD_DN_STK_COL_GP.substring(2, 4).equals("GJ") ) {		/* 가적장 */
					if( szYD_DN_STK_BED_NO.equals("06") ) {							/* 06베드를 03번지로 그 외의 베드는 01번지로 */
						szYD_DN_STK_BED_NO			= "03";
					}else{
						szYD_DN_STK_BED_NO			= "01";
					}
				}
				
				intDn_Grab_Y1_Value				= procY1CalFor1GrabCrn(intYD_DN_STK_BED_YAXIS, intYD_MTL_L, intYD_CRN_BEAM_L);
			}
			
			szMsg ="["+szOperationName+"] 1Grab Crn[2호기:"+szYD_EQP_ID+"]의 차량이 아닌 경우 To위치 Y1좌표["+intDn_Grab_Y1_Value+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------
			
			//----------------------------------------------------------------------------
			//	차량하차인 경우 From위치 Y1보정
			//----------------------------------------------------------------------------
			if( szYD_UP_STK_COL_GP.substring(2, 4).equals("PT") ) {
//PIDEV				
				if( ( szYD_UP_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) && szUP_CAR_KIND.startsWith("P")	)						/* 출하Pallet */
						|| ( szYD_UP_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_TS) && szUP_TRN_EQP_CD.substring(1, 3).equals("PT")	)		/* 구내운송Pallet */
					) {
						intUp_Grab_Y1_Value			= intYD_UP_STK_BED_YAXIS + (intDn_Grab_Y1_Value - intYD_DN_STK_BED_YAXIS);
					}else{															/* Trailer */
						intUp_Grab_Y1_Value			= intDn_Grab_Y1_Value;
					}

			}
			//----------------------------------------------------------------------------
			
			//----------------------------------------------------------------------------
			//	차량상차인 경우 To위치 Y1보정
			//----------------------------------------------------------------------------
			if( szYD_DN_STK_COL_GP.substring(2, 4).equals("PT") ) {
				if( ( szYD_DN_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) && szDN_CAR_KIND.startsWith("P") )							/* 출하Pallet */
						|| ( szYD_DN_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_TS) && szDN_TRN_EQP_CD.substring(1, 3).equals("PT") )	/* 구내운송Pallet */
				) {
					intDn_Grab_Y1_Value			= intYD_DN_STK_BED_YAXIS + (intUp_Grab_Y1_Value - intYD_UP_STK_BED_YAXIS);
				}else{																/* Trailer */
					intDn_Grab_Y1_Value			= intUp_Grab_Y1_Value;
				}
			}
			//----------------------------------------------------------------------------

			szCrane_Grab_Use_Gp				= "1";
			
			szMsg ="["+szOperationName+"] 1Grab Crn[2호기:"+szYD_EQP_ID+"]의 From위치 Y1좌표["+intUp_Grab_Y1_Value+"], To위치 Y1좌표["+intDn_Grab_Y1_Value+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
		//----------------------------------------------------------------------------
		//	1호기 - 2Grab
		//----------------------------------------------------------------------------
		}else{														//1호기 - 2Grab
			
			int intYD_CRN_GRAB1_BEAM_L			= 10400;
			int intYD_CRN_GRAB2_BEAM_L			=  6800;
			int intYD_CRN_GRAB_GAP				=  2500;
			int intYD_CRN_GRAB_GAP_ALPHA		= ((intYD_MTL_L - 21700) / 3) + intYD_CRN_GRAB_GAP;
			int intYD_CRN_GRAB_STND				= (intYD_MTL_L - (intYD_CRN_GRAB1_BEAM_L + intYD_CRN_GRAB_GAP_ALPHA + intYD_CRN_GRAB2_BEAM_L + 2000)) / 2;
			
			if( intYD_CRN_GRAB_STND < 1 ) intYD_CRN_GRAB_STND = 0;
			
			//----------------------------------------------------------------------------
			//	2Grab Crn - From위치 Y1좌표
			//----------------------------------------------------------------------------
			if( szYD_UP_STK_COL_GP.substring(2).matches("\\d\\d\\d\\d") 			/* 일반베드 */
				|| szYD_UP_STK_COL_GP.substring(2, 4).equals("TF")					/* TRANSFER */
				|| szYD_UP_STK_COL_GP.substring(2, 4).equals("RT")					/* ROLLER TABLE */
				|| szYD_UP_STK_COL_GP.substring(2, 4).equals("GJ")					/* 가적장 */
			) {
				if( szYD_UP_STK_COL_GP.substring(2, 4).equals("TF") ) {				/* TRANSFER */
					if( szYD_UP_STK_BED_NO.equals("06") 							/* 06, 05베드를 03번지로 그 외의 베드는 01번지로 */
						|| szYD_UP_STK_BED_NO.equals("05")
					) {
						szYD_UP_STK_BED_NO			= "03";
					}else{
						szYD_UP_STK_BED_NO			= "01";
					}
				}else if( szYD_UP_STK_COL_GP.substring(2, 4).equals("RT") ) {		/* ROLLER TABLE */
					if( szYD_UP_STK_BED_NO.equals("10") 							/* 10, 30, 50, 70베드를 03번지로 그 외의 베드는 01번지로 */
							|| szYD_UP_STK_BED_NO.equals("30")
							|| szYD_UP_STK_BED_NO.equals("50")
							|| szYD_UP_STK_BED_NO.equals("70")
						) {
							szYD_UP_STK_BED_NO			= "03";
						}else{
							szYD_UP_STK_BED_NO			= "01";
						}
				}else if( szYD_UP_STK_COL_GP.substring(2, 4).equals("GJ") ) {		/* 가적장 */
					if( szYD_UP_STK_BED_NO.equals("06") ) {							/* 06베드를 03번지로 그 외의 베드는 01번지로 */
						szYD_UP_STK_BED_NO			= "03";
					}else{
						szYD_UP_STK_BED_NO			= "01";
					}
				}
				
				recPara			= JDTORecordFactory.getInstance().create();
				
				procY1Y2CalFor2GrabCrn(szYD_UP_STK_BED_NO,								/* 베드번지(01, 02, 03) */
						intYD_UP_STK_BED_YAXIS,											/* DB기준 Y값 */
						intYD_MTL_L,													/* 크레인작업재료들중 제일 긴 재료의 제품길이 */
						intYD_CRN_GRAB1_BEAM_L,											/* #1Grab BEAM 길이 */
						intYD_CRN_GRAB2_BEAM_L, 										/* #2Grab BEAM 길이 */
			    		intYD_CRN_GRAB_GAP, 											/* BEAM 간격  */
			    		intYD_CRN_GRAB_GAP_ALPHA, 										/* BEAM 간격 알파 */
			    		intYD_CRN_GRAB_STND,											/* GRAB기준점 이동값*/
			    		recPara
			    );
				
				szCrane_Grab_Use_Gp			= ydDaoUtils.paraRecChkNull(recPara, "GRAB_USE_GP");
				intUp_Grab_Y1_Value			= ydDaoUtils.paraRecChkNullInt(recPara, "Y1_VALUE");
				intUp_Grab_Y2_Value			= ydDaoUtils.paraRecChkNullInt(recPara, "Y2_VALUE");
				
				szMsg ="["+szOperationName+"] 2Grab Crn[1호기:"+szYD_EQP_ID+"]의 차량이 아닌 경우 From위치 Y1좌표["+intUp_Grab_Y1_Value+"], Y2좌표["+intUp_Grab_Y2_Value+"], Grab Type["+szCrane_Grab_Use_Gp+"]";
				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			}
			//----------------------------------------------------------------------------
			
			//----------------------------------------------------------------------------
			//	2Grab Crn - To위치 Y1좌표
			//----------------------------------------------------------------------------
			if( szYD_DN_STK_COL_GP.substring(2).matches("\\d\\d\\d\\d") 			/* 일반베드 */
					|| szYD_DN_STK_COL_GP.substring(2, 4).equals("TF")				/* TRANSFER */
					|| szYD_DN_STK_COL_GP.substring(2, 4).equals("RT")				/* ROLLER TABLE */
					|| szYD_DN_STK_COL_GP.substring(2, 4).equals("GJ")				/* 가적장 */
			) {
				if( szYD_DN_STK_COL_GP.substring(2, 4).equals("TF") ) {				/* TRANSFER */
					if( szYD_DN_STK_BED_NO.equals("06") 							/* 06, 05베드를 03번지로 그 외의 베드는 01번지로 */
						|| szYD_DN_STK_BED_NO.equals("05")
					) {
						szYD_DN_STK_BED_NO			= "03";
					}else{
						szYD_DN_STK_BED_NO			= "01";
					}
				}else if( szYD_DN_STK_COL_GP.substring(2, 4).equals("RT") ) {		/* ROLLER TABLE */
					if( szYD_DN_STK_BED_NO.equals("10") 							/* 10, 30, 50, 70베드를 03번지로 그 외의 베드는 01번지로 */
							|| szYD_DN_STK_BED_NO.equals("30")
							|| szYD_UP_STK_BED_NO.equals("50")
							|| szYD_UP_STK_BED_NO.equals("70")
						) {
						szYD_DN_STK_BED_NO			= "03";
						}else{
							szYD_DN_STK_BED_NO			= "01";
						}
				}else if( szYD_DN_STK_COL_GP.substring(2, 4).equals("GJ") ) {		/* 가적장 */
					if( szYD_DN_STK_BED_NO.equals("06") ) {							/* 06베드를 03번지로 그 외의 베드는 01번지로 */
						szYD_DN_STK_BED_NO			= "03";
					}else{
						szYD_DN_STK_BED_NO			= "01";
					}
				}
				
				recPara			= JDTORecordFactory.getInstance().create();
				
				procY1Y2CalFor2GrabCrn(szYD_DN_STK_BED_NO,								/* 베드번지(01, 02, 03) */
						intYD_DN_STK_BED_YAXIS,											/* DB기준 Y값 */
						intYD_MTL_L,													/* 크레인작업재료들중 제일 긴 재료의 제품길이 */
						intYD_CRN_GRAB1_BEAM_L,											/* #1Grab BEAM 길이 */
						intYD_CRN_GRAB2_BEAM_L, 										/* #2Grab BEAM 길이 */
			    		intYD_CRN_GRAB_GAP, 											/* BEAM 간격  */
			    		intYD_CRN_GRAB_GAP_ALPHA, 										/* BEAM 간격 알파 */
			    		intYD_CRN_GRAB_STND,											/* GRAB기준점 이동값*/
			    		recPara
			    );
				
				szCrane_Grab_Use_Gp			= ydDaoUtils.paraRecChkNull(recPara, "GRAB_USE_GP");
				intDn_Grab_Y1_Value			= ydDaoUtils.paraRecChkNullInt(recPara, "Y1_VALUE");
				intDn_Grab_Y2_Value			= ydDaoUtils.paraRecChkNullInt(recPara, "Y2_VALUE");
				
				szMsg ="["+szOperationName+"] 2Grab Crn[1호기:"+szYD_EQP_ID+"]의 차량이 아닌 경우 To위치 Y1좌표["+intDn_Grab_Y1_Value+"], Y2좌표["+intDn_Grab_Y2_Value+"], Grab Type["+szCrane_Grab_Use_Gp+"]";
				ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			}
			//----------------------------------------------------------------------------
			
			//----------------------------------------------------------------------------
			//	차량하차인 경우 From위치 Y1보정
			//----------------------------------------------------------------------------
			if( szYD_UP_STK_COL_GP.substring(2, 4).equals("PT") ) {
				
				if( ( szYD_UP_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) && szUP_CAR_KIND.startsWith("P")	)					/* 출하Pallet */
					|| ( szYD_UP_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_TS) && szUP_TRN_EQP_CD.substring(1, 3).equals("PT") )	/* 구내운송Pallet */
				) {
					if( szCrane_Grab_Use_Gp.equals("1") ) {
						intUp_Grab_Y1_Value			= intYD_UP_STK_BED_YAXIS + (intDn_Grab_Y1_Value - intYD_DN_STK_BED_YAXIS);
					}else if( szCrane_Grab_Use_Gp.equals("2") ) {
						intUp_Grab_Y2_Value			= intYD_UP_STK_BED_YAXIS + (intDn_Grab_Y2_Value - intYD_DN_STK_BED_YAXIS);
					}else if( szCrane_Grab_Use_Gp.equals("3") ) {
						intUp_Grab_Y1_Value			= intYD_UP_STK_BED_YAXIS + (intDn_Grab_Y1_Value - intYD_DN_STK_BED_YAXIS);
						intUp_Grab_Y2_Value			= intYD_UP_STK_BED_YAXIS + (intDn_Grab_Y2_Value - intYD_DN_STK_BED_YAXIS);
						
					}
					
				}else{															/* Trailer */
					if( szCrane_Grab_Use_Gp.equals("1") ) {
						intUp_Grab_Y1_Value			= intDn_Grab_Y1_Value;
					}else if( szCrane_Grab_Use_Gp.equals("2") ) {
						intUp_Grab_Y2_Value			= intDn_Grab_Y2_Value;
					}else if( szCrane_Grab_Use_Gp.equals("3") ) {
						intUp_Grab_Y1_Value			= intDn_Grab_Y1_Value;
						intUp_Grab_Y2_Value			= intDn_Grab_Y2_Value;
					}
				}
			}
			//----------------------------------------------------------------------------
			
			
			//----------------------------------------------------------------------------
			//	차량상차인 경우 To위치 Y1보정
			//----------------------------------------------------------------------------
			if( szYD_DN_STK_COL_GP.substring(2, 4).equals("PT") ) {
				
				if( ( szYD_DN_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) && szDN_CAR_KIND.startsWith("P") )							/* 출하Pallet */
						|| ( szYD_DN_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_TS) && szDN_TRN_EQP_CD.substring(1, 3).equals("PT") )	/* 구내운송Pallet */
				) {
					if( szCrane_Grab_Use_Gp.equals("1") ) {
						intDn_Grab_Y1_Value			= intYD_DN_STK_BED_YAXIS + (intUp_Grab_Y1_Value - intYD_UP_STK_BED_YAXIS);
					}else if( szCrane_Grab_Use_Gp.equals("2") ) {
						intDn_Grab_Y2_Value			= intYD_DN_STK_BED_YAXIS + (intUp_Grab_Y2_Value - intYD_UP_STK_BED_YAXIS);
					}else if( szCrane_Grab_Use_Gp.equals("3") ) {
						intDn_Grab_Y1_Value			= intYD_DN_STK_BED_YAXIS + (intUp_Grab_Y1_Value - intYD_UP_STK_BED_YAXIS);
						intDn_Grab_Y2_Value			= intYD_DN_STK_BED_YAXIS + (intUp_Grab_Y2_Value - intYD_UP_STK_BED_YAXIS);
						
					}
				}else{																/* Trailer */
					if( szCrane_Grab_Use_Gp.equals("1") ) {
						intDn_Grab_Y1_Value			= intUp_Grab_Y1_Value;
					}else if( szCrane_Grab_Use_Gp.equals("2") ) {
						intDn_Grab_Y2_Value			= intUp_Grab_Y2_Value;
					}else if( szCrane_Grab_Use_Gp.equals("3") ) {
						intDn_Grab_Y1_Value			= intUp_Grab_Y1_Value;
						intDn_Grab_Y2_Value			= intUp_Grab_Y2_Value;
					}
				}
			}
			
			szMsg ="["+szOperationName+"] 2Grab Crn[1호기:"+szYD_EQP_ID+"]의 From위치 Y1좌표["+intUp_Grab_Y1_Value+"], Y2좌표["+intUp_Grab_Y2_Value+"], Grab Type["+szCrane_Grab_Use_Gp+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg ="["+szOperationName+"] 2Grab Crn[1호기:"+szYD_EQP_ID+"]의 To위치 Y1좌표["+intDn_Grab_Y1_Value+"], Y2좌표["+intDn_Grab_Y2_Value+"], Grab Type["+szCrane_Grab_Use_Gp+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
			//----------------------------------------------------------------------------
		}
		
		//----------------------------------------------------------------------------
		
		recOutPara.setField("Crane_Grab_Use_Gp", 		szCrane_Grab_Use_Gp);
		recOutPara.setField("Up_Grab_X_Value", 			szUp_Grab_X_Value);
		recOutPara.setField("Up_Grab_Y_Value", 			szUp_Grab_Y_Value);
		recOutPara.setField("Up_Grab_Y1_Value", 		String.valueOf(intUp_Grab_Y1_Value));
		recOutPara.setField("Up_Grab_Y2_Value", 		String.valueOf(intUp_Grab_Y2_Value));
		recOutPara.setField("Dn_Grab_X_Value", 			szDn_Grab_X_Value);
		recOutPara.setField("Dn_Grab_Y_Value", 			szDn_Grab_Y_Value);
		recOutPara.setField("Dn_Grab_Y1_Value", 		String.valueOf(intDn_Grab_Y1_Value));
		recOutPara.setField("Dn_Grab_Y2_Value", 		String.valueOf(intDn_Grab_Y2_Value));
		//---------------------------------------------------------------------------------------------------------
		
    	return YdConstant.RETN_CD_SUCCESS;
    }	
    
    /**
     * 좌표계산(후판제품:EF추가)
     * @param recInPara
     * @param recOutPara
     * @return
     * @throws JDTOException
     */
    public static String PlateCraneXYCal_EF_PIDEV(JDTORecord recInPara, JDTORecord recOutPara) throws JDTOException {
    	String szOperationName		= "좌표계산(후판제품:EF)";
    	String szMethodName			= "PlateCraneXYCal_EF_PIDEV";
    	String szMsg				= null;
    	String szRtnMsg				= null;
    	
    	JDTORecord		recPara		= null;
    	JDTORecord		recTemp		= null;
    	JDTORecordSet	outRecSet	= null;
    	//-------------------------------------------------------------------------------------------------
    	//	파라미터로 전달되는 항목 정의
    	//-------------------------------------------------------------------------------------------------
    	String szSTL_NO				= null;						//크레인작업재료들중에서 길이가 제일 긴 재료번호
    	String szYD_UP_STK_COL_GP	= null;						//권상지시위치 - 적치열
    	String szYD_UP_STK_BED_NO	= null;						//권상지시위치 - 적치베드
    	String szYD_DN_STK_COL_GP	= null;						//권하지시위치 - 적치열
    	String szYD_DN_STK_BED_NO	= null;						//권하지시위치 - 적치베드
    	String szYD_EQP_ID			= null;						//크레인설비ID
    	//-------------------------------------------------------------------------------------------------
    	
    	String szYD_CRN_GRAB_GP		= null;						//Grab Type
    	String szYD_CRN_TONG_L		= null;						//Crane Beam 길이
    	String szYD_CRN_GRAB_TP		= null;						//1,2 Grab 구분
    	
    	String szYD_MTL_W			= null;						//재료 폭
    	int intYD_MTL_W				= 0;						//재료 폭
    	String szYD_MTL_L			= null;						//재료 길이
    	int intYD_MTL_L				= 0;						//재료 길이
    	
    	//-------------------------------------------------------------------------------------------------
    	//	권상위치 정보
    	//-------------------------------------------------------------------------------------------------
    	String szYD_UP_STK_COL_BED_L_TP = null;					//권상지시위치 - 야드적치열Bed길이Type
    	String szYD_UP_CAR_USE_GP		= null;					//차량사용구분
    	String szUP_TRN_EQP_CD			= null;					//운송장비코드
    	String szUP_CAR_NO				= null;					//차량번호
    	String szUP_CARD_NO				= null;					//카드번호
    	String szUP_CAR_KIND			= null;					//카드번호
    	
    	int intYD_UP_STK_BED_XAXIS		= 0;					//권상지시위치베드 - 야드적치BedX축
    	int intYD_UP_STK_BED_YAXIS		= 0;					//권상지시위치베드 - 야드적치BedY축
    	String szYD_UP_STK_BED_L_GP		= null;					//권상지시위치베드 - 야드적치Bed길이구분
    	String szYD_UP_STK_BED_W_GP 	= null;					//권상지시위치베드 - 야드적치Bed폭구분
        String szYD_UP_STK_BED_WHIO_STAT= null;					//권상지시위치베드 - 야드적치Bed상태구분
    	String szYD_UP_STK_BED_W_MAX	= null;
    	int intYD_UP_STK_BED_W_MAX		= 0;
    	int intYD_UP_STK_BED_L_MAX		= 0;
    	//-------------------------------------------------------------------------------------------------
    	
    	//-------------------------------------------------------------------------------------------------
    	//	권하위치 정보
    	//-------------------------------------------------------------------------------------------------
    	String szYD_DN_STK_COL_BED_L_TP = null;					//권하지시위치 - 야드적치열Bed길이Type
    	String szYD_DN_CAR_USE_GP		= null;					//차량사용구분
    	String szDN_TRN_EQP_CD			= null;					//운송장비코드
    	String szDN_CAR_NO				= null;					//차량번호
    	String szDN_CARD_NO				= null;					//카드번호
    	String szDN_CAR_KIND			= null;					//카드번호
    	
    	int intYD_DN_STK_BED_XAXIS		= 0;					//권하지시위치베드 - 야드적치BedX축
    	int intYD_DN_STK_BED_YAXIS		= 0;					//권하지시위치베드 - 야드적치BedY축
    	String szYD_DN_STK_BED_L_GP		= null;					//권하지시위치베드 - 야드적치Bed길이구분
    	String szYD_DN_STK_BED_W_GP 	= null;					//권하지시위치베드 - 야드적치Bed폭구분
    	String szYD_DN_STK_BED_WHIO_STAT= null;					//권하지시위치베드 - 야드적치Bed상태구분
    	String szYD_DN_STK_BED_W_MAX	= null;
    	int intYD_DN_STK_BED_W_MAX		= 0;
    	int intYD_DN_STK_BED_L_MAX		= 0;
    	//-------------------------------------------------------------------------------------------------
    	
    	String szCrane_Grab_Use_Gp 		= null;
		String szUp_Grab_X_Value     	= null;
		String szUp_Grab_Y_Value     	= null;
		int intUp_Grab_Y1_Value     	= 0;
		int intUp_Grab_Y2_Value     	= 0;
		
		String szDn_Grab_X_Value     	= null;
		String szDn_Grab_Y_Value     	= null;
		int intDn_Grab_Y1_Value     	= 0;
		int intDn_Grab_Y2_Value     	= 0;
    	
    	szMsg ="["+szOperationName+"] -------------------------- 메소드 시작 - 파라미터 확인 --------------------------" ;
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szSTL_NO					= ydDaoUtils.paraRecChkNull(recInPara, "STL_NO");					//크레인작업재료들중에서 길이가 제일 긴 재료번호
		szYD_UP_STK_COL_GP			= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_COL_GP");			//권상지시위치 - 적치열
		szYD_UP_STK_BED_NO			= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_BED_NO");			//권상지시위치 - 적치베드
		szYD_DN_STK_COL_GP			= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_COL_GP");			//권하지시위치 - 적치열
		szYD_DN_STK_BED_NO			= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_BED_NO");			//권하지시위치 - 적치베드
		szYD_EQP_ID					= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID");				//크레인설비ID
		szYD_MTL_W 					= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W"); 				//크레인작업재료들중에서 폭이 제일 넓은 재료의 폭 값
		
		szMsg ="["+szOperationName+"] 크레인작업재료들중에서 길이가 제일 긴 재료번호 - " + szSTL_NO;
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분 - " + szYD_UP_STK_COL_GP + ", 베드번호 - " + szYD_UP_STK_BED_NO + "]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분 - " + szYD_DN_STK_COL_GP + ", 베드번호 - " + szYD_DN_STK_BED_NO + "]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 크레인설비ID - " + szYD_EQP_ID;
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		//---------------------------------------------------------------------------------------------------------
		// 저장품 조회
		// 제품길이 (YD_MTL_L) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 재료번호[" + szSTL_NO + "]로 저장품 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("STL_NO", szSTL_NO);
		
		szRtnMsg	= DaoManager.getYdStock(recPara, outRecSet, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 재료번호[" + szSTL_NO + "]로 저장품 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		szYD_MTL_L 				= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L"); 			// 제품길이
		
		int pointIdx			= szYD_MTL_W.lastIndexOf(".");
		
		if( pointIdx >= 0 ) {
			szYD_MTL_W			= szYD_MTL_W.substring(0, pointIdx);
		}
		
		intYD_MTL_W				= Integer.parseInt(szYD_MTL_W);
		intYD_MTL_L				= Integer.parseInt(szYD_MTL_L);
		
		szMsg ="["+szOperationName+"] 크레인작업재료들중에서 길이가 제일 긴 재료번호[" + szSTL_NO + "] - 제품길이["+szYD_MTL_L+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 크레인작업재료들중에서 폭이 제일 넓은 재료의 제품폭["+szYD_MTL_W+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		// 크레인사양 조회
		// 설비ID로  크레인 Beam 길이(YD_CRN_TONG_L) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]로 크레인사양 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		recPara 	= JDTORecordFactory.getInstance().create();
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara.setField("YD_EQP_ID", szYD_EQP_ID);
		
		szRtnMsg	= DaoManager.getYdCrnspec(recPara, outRecSet, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]로 크레인사양 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		szYD_CRN_TONG_L = ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_TONG_L"); // 크레인 Beam 길이
		
		szMsg ="["+szOperationName+"] 크레인설비ID[" + szYD_EQP_ID + "]의 크레인Beam길이[YD_CRN_TONG_L:"+szYD_CRN_TONG_L+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szYD_CRN_GRAB_TP = ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_GRAB_TP"); // 크레인 Grab 구분
		
		if("X".equals(szYD_CRN_GRAB_TP)){
			szYD_CRN_GRAB_GP = "E";
		}else{
			szYD_CRN_GRAB_GP = "D";
		}
		
		szMsg ="["+szOperationName+"] 크레인 Grab 구분[D : 1Grab Crane, E : 2Grab Crane] - " + szYD_CRN_GRAB_GP;
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		// 권상지시위치 - 적치열조회 : 야드적치열Bed길이Type, 차량정보가 존재하면 차량정보 조회
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "]로 적치열 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYD_UP_STK_COL_GP);
		
		szRtnMsg	= DaoManager.getYdStkcol(recPara, outRecSet, 900);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "]로 적치열 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		szYD_UP_STK_COL_BED_L_TP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_BED_L_TP"); 		//야드적치열Bed길이Type
		szYD_UP_CAR_USE_GP					= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_USE_GP"); 				//차량사용구분
		szUP_TRN_EQP_CD						= ydDaoUtils.paraRecChkNull(recTemp, "TRN_EQP_CD"); 				//운송장비코드
		szUP_CAR_NO							= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO"); 					//차량번호
		szUP_CARD_NO						= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO"); 					//카드번호
		szUP_CAR_KIND						= ydDaoUtils.paraRecChkNull(recTemp, "CAR_KIND"); 					//카드번호
		
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "]로 적치열 조회 완료 - 야드적치열Bed길이Type["+szYD_UP_STK_COL_BED_L_TP+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "] - 차량사용구분["+szYD_UP_CAR_USE_GP+"], 운송장비코드["+szUP_TRN_EQP_CD+"], 차량번호["+szUP_CAR_NO+"], 카드번호["+szUP_CARD_NO+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		// 권하지시위치 - 적치열조회 : 야드적치열Bed길이Type, 차량정보가 존재하면 차량정보 조회
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "]로 적치열 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYD_DN_STK_COL_GP);
		
		szRtnMsg	= DaoManager.getYdStkcol(recPara, outRecSet, 900);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "]로 적치열 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		szYD_DN_STK_COL_BED_L_TP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_BED_L_TP"); 		//야드적치열Bed길이Type
		szYD_DN_CAR_USE_GP					= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_USE_GP"); 				//차량사용구분
		szDN_TRN_EQP_CD						= ydDaoUtils.paraRecChkNull(recTemp, "TRN_EQP_CD"); 				//운송장비코드
		szDN_CAR_NO							= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO"); 					//차량번호
		szDN_CARD_NO						= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO"); 					//카드번호
		szDN_CAR_KIND						= ydDaoUtils.paraRecChkNull(recTemp, "CAR_KIND"); 					//카드번호
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "]로 적치열 조회 완료 - 야드적치열Bed길이Type["+szYD_DN_STK_COL_BED_L_TP+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "] - 차량사용구분["+szYD_DN_CAR_USE_GP+"], 운송장비코드["+szDN_TRN_EQP_CD+"], 차량번호["+szDN_CAR_NO+"], 카드번호["+szDN_CARD_NO+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------

		//---------------------------------------------------------------------------------------------------------
		// 권상지시위치 - 적치Bed 조회
		// 야드적치열구분, 야드적치Bed번호로  야드적치BedY축(YD_STK_BED_YAXIS) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + ", 베드번호:" + szYD_UP_STK_BED_NO + "]로 베드 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYD_UP_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", szYD_UP_STK_BED_NO);
		
		szRtnMsg	= DaoManager.getYdStkbed(recPara, outRecSet, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + ", 베드번호:" + szYD_UP_STK_BED_NO + "]로 베드 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		intYD_UP_STK_BED_XAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS"); 		// 야드적치BedX축
		intYD_UP_STK_BED_YAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS"); 		// 야드적치BedY축
		szYD_UP_STK_BED_L_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP"); 			// 야드적치Bed길이구분
		szYD_UP_STK_BED_W_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP"); 			// 야드적치Bed폭구분
		szYD_UP_STK_BED_WHIO_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT"); 		// 야드적치Bed상태구분
		szYD_UP_STK_BED_W_MAX			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_MAX"); 			// 야드적치Bed폭Max
		intYD_UP_STK_BED_L_MAX			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_L_MAX"); 		// 야드적치Bed길이Max
		
		pointIdx	= szYD_UP_STK_BED_W_MAX.lastIndexOf(".");
		
		if( pointIdx >= 0 ) {
			szYD_UP_STK_BED_W_MAX		= szYD_UP_STK_BED_W_MAX.substring(0, pointIdx);
		}
		
		intYD_UP_STK_BED_W_MAX			= Integer.parseInt(szYD_UP_STK_BED_W_MAX);
		
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + ", 베드번호:" + szYD_UP_STK_BED_NO + "]의  야드적치BED X축["+intYD_UP_STK_BED_XAXIS+"], 야드적치BED Y축["+intYD_UP_STK_BED_YAXIS+"], 야드적치Bed길이구분["+szYD_UP_STK_BED_L_GP+"] 조회 완료";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		
    	
		//---------------------------------------------------------------------------------------------------------
		// 권하지시위치 - 적치Bed 조회
		// 야드적치열구분, 야드적치Bed번호로  야드적치BedY축(YD_STK_BED_YAXIS) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + ", 베드번호:" + szYD_DN_STK_BED_NO + "]로 베드 조회 시작";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYD_DN_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", szYD_DN_STK_BED_NO);
		
		szRtnMsg	= DaoManager.getYdStkbed(recPara, outRecSet, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + ", 베드번호:" + szYD_DN_STK_BED_NO + "]로 베드 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		outRecSet.first();
		recTemp = outRecSet.getRecord();
		
		intYD_DN_STK_BED_XAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS"); 		// 야드적치BedX축
		intYD_DN_STK_BED_YAXIS 			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS"); 		// 야드적치BedY축
		szYD_DN_STK_BED_L_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP"); 			// 야드적치Bed길이구분
		szYD_DN_STK_BED_W_GP 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP"); 			// 야드적치Bed폭구분
		szYD_DN_STK_BED_WHIO_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT"); 		// 야드적치Bed상태구분
		szYD_DN_STK_BED_W_MAX			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_MAX"); 			// 야드적치Bed폭Max
		intYD_DN_STK_BED_L_MAX			= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_L_MAX"); 		// 야드적치Bed길이Max
		
		pointIdx	= szYD_DN_STK_BED_W_MAX.lastIndexOf(".");
		
		if( pointIdx >= 0 ) {
			szYD_DN_STK_BED_W_MAX		= szYD_DN_STK_BED_W_MAX.substring(0, pointIdx);
		}
		
		intYD_DN_STK_BED_W_MAX			= Integer.parseInt(szYD_DN_STK_BED_W_MAX);
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + ", 베드번호:" + szYD_DN_STK_BED_NO + "]의  야드적치BED X축["+intYD_DN_STK_BED_XAXIS+"], 야드적치BED Y축["+intYD_DN_STK_BED_YAXIS+"], 야드적치Bed길이구분["+szYD_DN_STK_BED_L_GP+"] 조회 완료";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		//	X좌표 구하기
		//---------------------------------------------------------------------------------------------------------
		szUp_Grab_X_Value			= String.valueOf(procXCalForPlateYd(intYD_UP_STK_BED_XAXIS, intYD_MTL_W, intYD_UP_STK_BED_W_MAX, szYD_UP_STK_COL_GP,szYD_UP_STK_BED_W_GP,szYD_UP_STK_BED_WHIO_STAT));
		szDn_Grab_X_Value			= String.valueOf(procXCalForPlateYd(intYD_DN_STK_BED_XAXIS, intYD_MTL_W, intYD_DN_STK_BED_W_MAX, szYD_DN_STK_COL_GP,szYD_DN_STK_BED_W_GP,szYD_DN_STK_BED_WHIO_STAT));
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		//	Y좌표 구하기 
		//---------------------------------------------------------------------------------------------------------
		//(Tmp_YD_MTL_L / 2)
		szUp_Grab_Y_Value			= String.valueOf(intYD_UP_STK_BED_YAXIS + (intYD_MTL_L/2));
		szDn_Grab_Y_Value			= String.valueOf(intYD_DN_STK_BED_YAXIS + (intYD_MTL_L/2));
		//---------------------------------------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------------------------------------
		//	Y1, Y2좌표 구하기
		//---------------------------------------------------------------------------------------------------------
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "] - 야드적치열Bed길이Type["+szYD_UP_STK_COL_BED_L_TP+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권상지시위치[적치열구분:" + szYD_UP_STK_COL_GP + "] - 차량사용구분["+szYD_UP_CAR_USE_GP+"], 운송장비코드["+szUP_TRN_EQP_CD+"], 차량번호["+szUP_CAR_NO+"], 카드번호["+szUP_CARD_NO+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "] - 야드적치열Bed길이Type["+szYD_DN_STK_COL_BED_L_TP+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg ="["+szOperationName+"] 권하지시위치[적치열구분:" + szYD_DN_STK_COL_GP + "] - 차량사용구분["+szYD_DN_CAR_USE_GP+"], 운송장비코드["+szDN_TRN_EQP_CD+"], 차량번호["+szDN_CAR_NO+"], 카드번호["+szDN_CARD_NO+"]";
		ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
		//----------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------
		//	1Grab
		//----------------------------------------------------------------------------
		if( szYD_CRN_GRAB_GP.equals("D") )	{			//2호기 - 1Grab
			
			int intYD_CRN_BEAM_L			= 	  0;	//Beam길이
			//----------------------------------------------------------------------------
		
			if(szYD_UP_STK_COL_GP.startsWith("KA")){
				intYD_CRN_BEAM_L			=  14000;	//Beam길이 
			}else if(szYD_UP_STK_COL_GP.startsWith("KC")||
					 szYD_UP_STK_COL_GP.startsWith("KD")){
				intYD_CRN_BEAM_L			=  9200;	//Beam길이 
			}else if(szYD_UP_STK_COL_GP.startsWith("KE")){
				intYD_CRN_BEAM_L			=  6800;	//Beam길이 
			}else if(szYD_UP_STK_COL_GP.startsWith("KF")){
				intYD_CRN_BEAM_L			= 12600;	//Beam길이
			}
			
			szMsg ="["+szOperationName+"] 크레인작업재료들중에서 길이가 제일 긴 재료번호[" + szSTL_NO + "]의 제품길이["+intYD_MTL_L+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
			//----------------------------------------------------------------------------
			//	1Grab Crn - From위치 Y1좌표
			//----------------------------------------------------------------------------
			
			if( szYD_UP_STK_COL_GP.substring(2).matches("\\d\\d\\d\\d") 			/* 일반베드 */
				|| szYD_UP_STK_COL_GP.substring(2, 4).equals("RT")					/* ROLLER TABLE */
			) {
				if( szYD_UP_STK_COL_GP.substring(2, 4).equals("RT") ) {				/* ROLLER TABLE */
					if(szYD_UP_STK_COL_GP.startsWith("KE")){						// E 동
						if( szYD_UP_STK_BED_NO.equals("85")){
							szYD_UP_STK_BED_NO	= "04";
						} else if(szYD_UP_STK_BED_NO.equals("90")){
							szYD_UP_STK_BED_NO	= "03";
						} else if(szYD_UP_STK_BED_NO.equals("95")){
							szYD_UP_STK_BED_NO	= "02";
						} else if(szYD_UP_STK_BED_NO.equals("A0")){
							szYD_UP_STK_BED_NO	= "01";
						}
					} else { 														// F 동
					   if(szYD_UP_STK_BED_NO.equals("B0")){
							szYD_UP_STK_BED_NO	= "03";
						}else{
							szYD_UP_STK_BED_NO	= "01";
						}
					}	
				}
				
				intUp_Grab_Y1_Value			= procY1CalFor1GrabCrn( intYD_UP_STK_BED_YAXIS, intYD_MTL_L, intYD_CRN_BEAM_L);
			}
			
			szMsg ="["+szOperationName+"] 1Grab Crn[2호기:"+szYD_EQP_ID+"]의 차량이 아닌 경우 From위치 Y1좌표["+intUp_Grab_Y1_Value+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------
			
			//----------------------------------------------------------------------------
			//	1Grab Crn - To위치 Y1좌표
			//----------------------------------------------------------------------------
			
			if( szYD_DN_STK_COL_GP.substring(2).matches("\\d\\d\\d\\d") 			/* 일반베드 */
			) {
				
				intDn_Grab_Y1_Value			= procY1CalFor1GrabCrn(intYD_DN_STK_BED_YAXIS, intYD_MTL_L, intYD_CRN_BEAM_L);
			}
			
			szMsg ="["+szOperationName+"] 1Grab Crn[2호기:"+szYD_EQP_ID+"]의 차량이 아닌 경우 To위치 Y1좌표["+intDn_Grab_Y1_Value+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------
			
			//----------------------------------------------------------------------------
			//	차량하차인 경우 From위치 Y1보정
			//----------------------------------------------------------------------------
			if( szYD_UP_STK_COL_GP.substring(2, 4).equals("PT") ) {
				
				if( ( szYD_UP_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) && szUP_CAR_KIND.startsWith("P")	)						/* 출하Pallet */
					|| ( szYD_UP_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_TS) && szUP_TRN_EQP_CD.substring(1, 3).equals("PT")	)		/* 구내운송Pallet */
				) {
					intUp_Grab_Y1_Value			= intYD_UP_STK_BED_YAXIS + (intDn_Grab_Y1_Value - intYD_DN_STK_BED_YAXIS);
				}else{															/* Trailer */
					intUp_Grab_Y1_Value			= intDn_Grab_Y1_Value;
				}
			}
			//----------------------------------------------------------------------------
			
			//----------------------------------------------------------------------------
			//	차량상차인 경우 To위치 Y1보정
			//----------------------------------------------------------------------------
			if( szYD_DN_STK_COL_GP.substring(2, 4).equals("PT") ) {
				
				if( ( szYD_DN_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) && szDN_CAR_KIND.startsWith("P") )							/* 출하Pallet */
						|| ( szYD_DN_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_TS) && szDN_TRN_EQP_CD.substring(1, 3).equals("PT") )	/* 구내운송Pallet */
				) {
					intDn_Grab_Y1_Value			= intYD_DN_STK_BED_YAXIS + (intUp_Grab_Y1_Value - intYD_UP_STK_BED_YAXIS);
				}else{																/* Trailer */
					intDn_Grab_Y1_Value			= intUp_Grab_Y1_Value;
				}
			}
			//----------------------------------------------------------------------------

			szCrane_Grab_Use_Gp				= "1";
			
			szMsg ="["+szOperationName+"] 1Grab Crn[2호기:"+szYD_EQP_ID+"]의 From위치 Y1좌표["+intUp_Grab_Y1_Value+"], To위치 Y1좌표["+intDn_Grab_Y1_Value+"]";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
		//----------------------------------------------------------------------------
		//	 2Grab
		//----------------------------------------------------------------------------
		}else{														//1호기 - 2Grab
			
			szMsg ="["+szOperationName+"] EF동은 2Grab 없슴  기준확인";
			ydUtils.putLog(YdUtils.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
			//----------------------------------------------------------------------------
		}
		
		//----------------------------------------------------------------------------
		
		recOutPara.setField("Crane_Grab_Use_Gp", 		szCrane_Grab_Use_Gp);
		recOutPara.setField("Up_Grab_X_Value", 			szUp_Grab_X_Value);
		recOutPara.setField("Up_Grab_Y_Value", 			szUp_Grab_Y_Value);
		recOutPara.setField("Up_Grab_Y1_Value", 		String.valueOf(intUp_Grab_Y1_Value));
		recOutPara.setField("Up_Grab_Y2_Value", 		String.valueOf(intUp_Grab_Y2_Value));
		recOutPara.setField("Dn_Grab_X_Value", 			szDn_Grab_X_Value);
		recOutPara.setField("Dn_Grab_Y_Value", 			szDn_Grab_Y_Value);
		recOutPara.setField("Dn_Grab_Y1_Value", 		String.valueOf(intDn_Grab_Y1_Value));
		recOutPara.setField("Dn_Grab_Y2_Value", 		String.valueOf(intDn_Grab_Y2_Value));
		//---------------------------------------------------------------------------------------------------------
		
    	return YdConstant.RETN_CD_SUCCESS;
    } 
    
}
