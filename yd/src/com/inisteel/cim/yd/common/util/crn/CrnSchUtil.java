/**
 * 크레인 스케줄
 */
package com.inisteel.cim.yd.common.util.crn;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 * @author Administrator
 *
 */
public class CrnSchUtil {
	
	private static String szClassName = CrnSchUtil.class.getName();
	private static YdUtils ydUtils = new YdUtils();
	private static YdDaoUtils ydDaoUtils = new YdDaoUtils();
	private static YdPICommDAO   ydPICommDAO   = new YdPICommDAO();
	
	
	/*--------------------------------------------------------------------------------------------------------
	 * 1. 대상재 데이터 정합성 체크
	 * 2. 크레인스케줄 수행조건 판단
	 * 3. 크레인 작업대상 베드별 조회
	 * 4. 작업대상에 그룹핑 파라미터 설정
	 * 	W:보조작업  
	 * 	A:주작업
	 * 	M:보조작업/주작업
	 * 	R:리버스주작업(모음작업)
	 * 	B:베드의 최하단주작업(모음작업)
	 * 	T:최종위치 주작업
	 * 	S:베드의 최하단주작업(최종위치)
	 * 5. 핸들링LOT별로 정렬
	 *  . 보조작업, 보조작업/주작업에 대한 크레인사양 적용
	 * 6. 보조작업, 보조작업/주작업에 대한 권상위치와 권하위치 업데이트
	 * 7. 모음작업/최종위치에 대한 권상위치 업데이트 와 크레인작업대상재 추가
	 * 8. 모음작업/최종위치에 대한 크레인사양 적용
	 * 9. 모음작업/최종위치에 대한 권하위치 업데이트
	 --------------------------------------------------------------------------------------------------------*/
	
	public static String procCrnSchMain(JDTORecord msgRecord) throws JDTOException  {
		
		String		szMethodName				= "procCrnSchMain";
		String		szOperationName				= "크레인스케줄메인";
		String		szLogMsg					= null;
		String		szRtnMsg					= null;
		
		String		szYD_EQP_ID					= null;
		String		szYD_SCH_CD					= null;
		String		szYD_WBOOK_ID				= null;
		
		//-------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//-------------------------------------------------------------------------------------------------------------
		
		szLogMsg	= "["+szOperationName+"]-------------------------------------------------------------------------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		szLogMsg	= "["+szOperationName+"]--------------------------------- 메소드 시작 - 파라미터확인 ---------------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, msgRecord);
		
		szYD_EQP_ID		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
		szYD_SCH_CD		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
		szYD_WBOOK_ID	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
		
		szLogMsg	= "["+szOperationName+"]-------------------------------------------------------------------------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		//-------------------------------------------------------------------------------------------------------------
		
		//-------------------------------------------------------------------------------------------------------------
		//	크레인설비 상태 체크
		//-------------------------------------------------------------------------------------------------------------
		szLogMsg	= "["+szOperationName+"] 크레인설비["+szYD_EQP_ID+"]상태체크 시작";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		szRtnMsg	= YdCommonUtils.checkCrnStat(szYD_EQP_ID);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			return szRtnMsg;
		}
		
		szLogMsg	= "["+szOperationName+"] 크레인설비["+szYD_EQP_ID+"]상태체크 완료";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		//-------------------------------------------------------------------------------------------------------------
		
		
		//-------------------------------------------------------------------------------------------------------------
		//	작업예약 조회
		//-------------------------------------------------------------------------------------------------------------
		if( szYD_WBOOK_ID.equals("") ) {
			
			
		}
		
		
		//-------------------------------------------------------------------------------------------------------------
		
		
		//-------------------------------------------------------------------------------------------------------------
		//	권상모음순으로 정렬된 작업예약재료가 크레인사양 체크 시 분리 유무 체크
		//-------------------------------------------------------------------------------------------------------------
		
		
		//-------------------------------------------------------------------------------------------------------------
		
		
		//-------------------------------------------------------------------------------------------------------------
		//	모듈 분기 
		//	1. 모음작업 포함한 크레인스케줄 생성
		//	2. 모음작업 없이 크레인 스케줄 생성
		//-------------------------------------------------------------------------------------------------------------
		
		
		//-------------------------------------------------------------------------------------------------------------
		
		szLogMsg	= "["+szOperationName+"]---------------------------------------------------------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		szLogMsg	= "["+szOperationName+"]--------------------------------- 메소드 끝  ---------------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		szLogMsg	= "["+szOperationName+"]----------------------------------------------------------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	
	public static String getYdWbookForCrnSch(JDTORecord msgRecord) throws JDTOException  {
		String		szMethodName				= "getYdWbookForCrnSch";
		String		szOperationName				= "크레인스케줄작업예약조회";
		String		szLogMsg					= null;
		String		szRtnMsg					= null;
		
		String		szYD_EQP_ID					= null;
		String		szYD_SCH_CD					= null;
		String		szYD_WBOOK_ID				= null;
		
		szLogMsg	= "["+szOperationName+"]--------------------------------- 메소드 시작 ---------------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		szYD_EQP_ID		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
		szYD_SCH_CD		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
		
		//---------------------------------------------------------------------------------------------------------------
		//	전달된 스케줄코드의 유무에 따른 로직 분기
		//---------------------------------------------------------------------------------------------------------------
		
		if( szYD_SCH_CD.equals("") )	{
			
			//-------------------------------------------------------------------------------------------------------------
			//	파라미터로 넘겨진 크레인설비ID로 스케줄금지되지 않고 크레인우선순위가 빠른 작업예약중에서 먼저 생성된 작업예약을 조회
			//-------------------------------------------------------------------------------------------------------------
			
			
			
			//-------------------------------------------------------------------------------------------------------------
			
		}else{
			
			//-------------------------------------------------------------------------------------------------------------
			//	파라미터로 넘겨진 크레인설비ID와 크레인스케줄코드로 스케줄금지되지 않고 크레인우선순위가 빠른 작업예약중에서 먼저 생성된 작업예약을 조회
			//-------------------------------------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------------------
			//	파라미터로 넘겨진 크레인설비ID와 크레인스케줄코드로 스케줄금지되지 않고 크레인우선순위가 빠른 작업예약중에서 먼저 생성된 작업예약을 조회
			//-------------------------------------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------------------
			
		}
		
		//---------------------------------------------------------------------------------------------------------------
		
		szLogMsg	= "["+szOperationName+"]--------------------------------- 메소드 끝 ---------------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	
	/**
	 * 크레인XY좌표수정
	 * @param recInPara
	 * @return
	 * @throws JDTOException
	 */
	public static String uptCrnSchXYCord(JDTORecord recInPara) throws JDTOException {
		
		String szLogMsg						= null;
		String szOperationName				= "크레인XY좌표수정";
		String szMethodName					= "uptCrnSchXYCord";
		
		String szRtnMsg						= null;
		
		JDTORecord recUpdCrnSchData			= null;
		JDTORecord 			recPara 		= null;
		JDTORecordSet		rsResult		= null;
		
		String szYD_CRN_SCH_ID				= null;
		String szYD_EQP_ID					= null;
		String szYD_UP_STK_COL_GP			= null;
		String szYD_UP_STK_BED_NO			= null;
		String szYD_DN_STK_COL_GP			= null;
		String szYD_DN_STK_BED_NO			= null;
		
		String szYD_UP_WO_LOC				= null;
		String szYD_UP_WO_LAYER				= null;
		String szYD_DN_WO_LOC				= null;
		String szYD_DN_WO_LAYER				= null;
		
		String szYD_EQP_WRK_SH				= null;
		String szYD_EQP_WRK_WT				= null;
		String szYD_EQP_WRK_T				= null;
		String szYD_EQP_WRK_MAX_W			= null;
		String szYD_EQP_WRK_MAX_L			= null;
		String szYD_TO_LOC_GUIDE            = null;  // AT000 물류시스템 개선 2022.10.27 
		
		int 	intCRANE_GAP_UP_X			= 20;
		int 	intCRANE_GAP_UP_Y			= 20;
		int 	intCRANE_GAP_UP_Z			= 20;
		int 	intCRANE_GAP_DN_X			= 20;
		int 	intCRANE_GAP_DN_Y			= 20;
		int 	intCRANE_GAP_DN_Z			= 20;
		
		String 	szYD_GP						= null;
		
		String szUp_Crane_Grab_Use_Gp 		= "";
		String szUp_Grab_X_Value     		= "";
		String szUp_Grab_Y_Value     		= "";
		String szUp_Grab_Y1_Value     		= "";
		String szUp_Grab_Y2_Value     		= "";
		
		String szDn_Crane_Grab_Use_Gp 		= "";
		String szDn_Grab_X_Value     		= "";
		String szDn_Grab_Y_Value     		= "";
		String szDn_Grab_Y1_Value     		= "";
		String szDn_Grab_Y2_Value     		= "";
		
		String szYD_UP_WO_LOC_ZAXIS			= "";
		String szYD_DN_WO_LOC_ZAXIS			= "";
		
		boolean isUP_DN_XY_CAL				= false;
		
		//-------------------------------------------------------------------------------------------------------------
		//	파라미터확인
		//-------------------------------------------------------------------------------------------------------------
		
		szLogMsg	= "["+szOperationName+"] -------------------------- 메소드 시작 - 파라미터확인 -------------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, recInPara);
		
		szYD_CRN_SCH_ID 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_CRN_SCH_ID");
		szYD_EQP_ID 			= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID");
		
		szYD_UP_STK_COL_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_COL_GP");
		szYD_UP_STK_BED_NO 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_BED_NO");
		szYD_DN_STK_COL_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_COL_GP");
		szYD_DN_STK_BED_NO 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_BED_NO");
		
		szYD_UP_WO_LOC			= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_WO_LOC");
		szYD_UP_WO_LAYER		= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_WO_LAYER");
		szYD_DN_WO_LOC			= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_WO_LOC");
		szYD_DN_WO_LAYER		= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_WO_LAYER");
		
		szYD_EQP_WRK_SH			= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");
		szYD_EQP_WRK_WT			= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");
		szYD_EQP_WRK_T			= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");
		szYD_EQP_WRK_MAX_W		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_MAX_W");
		szYD_EQP_WRK_MAX_L		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_MAX_L");
		szYD_TO_LOC_GUIDE       = ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE"); // AT000 물류시스템개선 2022.10.31 중간이적bed용 
		
		szYD_GP = szYD_UP_STK_COL_GP.substring(0, 1);
		
		//-------------------------------------------------------------------------------------------------------------
		
		
		//-------------------------------------------------------------------------------------------------------------
		//	크레인 허용 오차 및 크레인 X, Y좌표 계산
		//-------------------------------------------------------------------------------------------------------------
		
		if( szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD) || szYD_GP.equals(YdConstant.YD_GP_PLATE2_GDS_YARD) ) {	//후판제품창고, 2후판제품창고
			
			szLogMsg	= "["+szOperationName+"] -------------------------- 후판제품 X,Y 좌표계산 시작 -------------------------------";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			recPara 				= JDTORecordFactory.getInstance().create();
			JDTORecord recResult 	= JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", 		szYD_CRN_SCH_ID);
			recPara.setField("YD_EQP_ID", 			szYD_EQP_ID);
			recPara.setField("YD_UP_STK_COL_GP", 	szYD_UP_STK_COL_GP);
			recPara.setField("YD_UP_STK_BED_NO", 	szYD_UP_STK_BED_NO);
			recPara.setField("YD_DN_STK_COL_GP", 	szYD_DN_STK_COL_GP);
			recPara.setField("YD_DN_STK_BED_NO", 	szYD_DN_STK_BED_NO);
			
			if( szYD_GP.equals(YdConstant.YD_GP_PLATE2_GDS_YARD)) {
				//2후판제품창고
				PlateGdsYdUtil.procXYCalForPlateCrane3G(recPara, recResult);
				
				szUp_Crane_Grab_Use_Gp 		= ydDaoUtils.paraRecChkNull(recResult, "Crane_Grab_Use_Gp");
				szUp_Grab_X_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_X_Value");
				szUp_Grab_Y_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_Y_Value");
				szUp_Grab_Y1_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_Y1_Value");
				szUp_Grab_Y2_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_Y2_Value");
				
				szDn_Crane_Grab_Use_Gp 		= ydDaoUtils.paraRecChkNull(recResult, "Crane_Grab_Use_Gp");
				szDn_Grab_X_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_X_Value");
				szDn_Grab_Y_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_Y_Value");
				szDn_Grab_Y1_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_Y1_Value");
				szDn_Grab_Y2_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_Y2_Value");	
				
				//X,Y 허용오차를 recResult 로 부터 읽어온다.
				intCRANE_GAP_UP_X			= ydDaoUtils.paraRecChkNullInt(recResult, "CRANE_GAP_UP_X");
				intCRANE_GAP_UP_Y			= ydDaoUtils.paraRecChkNullInt(recResult, "CRANE_GAP_UP_Y");
				intCRANE_GAP_UP_Z			= ydDaoUtils.paraRecChkNullInt(recResult, "CRANE_GAP_UP_Z");
				intCRANE_GAP_DN_X			= ydDaoUtils.paraRecChkNullInt(recResult, "CRANE_GAP_DN_X");
				intCRANE_GAP_DN_Y			= ydDaoUtils.paraRecChkNullInt(recResult, "CRANE_GAP_DN_Y");
				intCRANE_GAP_DN_Z			= ydDaoUtils.paraRecChkNullInt(recResult, "CRANE_GAP_DN_Z");	
				
				// 2021. 1. 21 전사물류개선
				szYD_UP_WO_LOC_ZAXIS        = ydDaoUtils.paraRecChkNull(recResult, "YD_STK_BED_UP_ZAXIS");
				szYD_DN_WO_LOC_ZAXIS        = ydDaoUtils.paraRecChkNull(recResult, "YD_STK_BED_DN_ZAXIS");
				
				if(szYD_UP_STK_COL_GP.startsWith(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRD")||szYD_UP_STK_COL_GP.startsWith(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRE")||szYD_UP_STK_COL_GP.startsWith(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRF")){
					//20220802 박성열   후판 E동 RT일 경우 권상 갭을 5500으로 고정   박종호 책임 요청
					// 5500을 7000으로 값 수정 신진희책
					szLogMsg	= "["+szOperationName+"] -------------------------- CrnSchUtil ERT 권상 -------------------------------";
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					intCRANE_GAP_UP_Y			= 7000;
				}		
			} else {
				//1후판제품창고
				PlateGdsYdUtil.procXYCalForPlateCrane(recPara, recResult);
				
				szUp_Crane_Grab_Use_Gp 		= ydDaoUtils.paraRecChkNull(recResult, "Crane_Grab_Use_Gp");
				szUp_Grab_X_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_X_Value");
				szUp_Grab_Y_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_Y_Value");
				szUp_Grab_Y1_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_Y1_Value");
				szUp_Grab_Y2_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_Y2_Value");
				
				szDn_Crane_Grab_Use_Gp 		= ydDaoUtils.paraRecChkNull(recResult, "Crane_Grab_Use_Gp");
				szDn_Grab_X_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_X_Value");
				szDn_Grab_Y_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_Y_Value");
				szDn_Grab_Y1_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_Y1_Value");
				szDn_Grab_Y2_Value     		= ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_Y2_Value");		
				
				/*
				 * 임시적용 2014.02.12 윤재광 
				 * F동 4/5/6스판만 적용 - Y축 허용오차 4000(4M)으로 강제셋팅
				 */
				if(szYD_UP_STK_COL_GP.startsWith(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "F04")||szYD_UP_STK_COL_GP.startsWith(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "F05")||szYD_UP_STK_COL_GP.startsWith(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "F06")){
					intCRANE_GAP_UP_Y			= 4000;
				}
				if(szYD_DN_STK_COL_GP.startsWith(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "F04")||szYD_DN_STK_COL_GP.startsWith(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "F05")||szYD_DN_STK_COL_GP.startsWith(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "F06")){
					intCRANE_GAP_DN_Y			= 4000;
				}
				
				/*
				 * 임시적용 2014.03.12 윤재광 
				 * 1후판 제품창고 권상위치 R/T, T/F 일때  X,Y 허용오차 100으로 셋팅
				 */
				if( szYD_UP_STK_COL_GP.substring(2, 4).equals("RT")||
					szYD_UP_STK_COL_GP.substring(2, 4).equals("TF")){
					
					intCRANE_GAP_UP_X = 100;
					intCRANE_GAP_UP_Y = 100;
				 
				}
			} 
			
			szLogMsg	= "["+szOperationName+"] -------------------------- 후판제품 X,Y 좌표계산 완료 -------------------------------";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
		}else if( szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD) ) {			//A후판슬라브야드
			
			intCRANE_GAP_UP_X = YdConstant.A_PLATE_SLAB_CRANE_GAP;
			intCRANE_GAP_UP_Y = YdConstant.A_PLATE_SLAB_CRANE_GAP;
			intCRANE_GAP_UP_Z = YdConstant.A_PLATE_SLAB_CRANE_GAP;
			
			intCRANE_GAP_DN_X = YdConstant.A_PLATE_SLAB_CRANE_GAP;
			intCRANE_GAP_DN_Y = YdConstant.A_PLATE_SLAB_CRANE_GAP;
			intCRANE_GAP_DN_Z = YdConstant.A_PLATE_SLAB_CRANE_GAP;
			
			isUP_DN_XY_CAL		= true;
			
			
		}else{
			
			if( szYD_UP_STK_COL_GP.substring(2, 4).equals("TC") 
				|| szYD_DN_STK_COL_GP.substring(2, 4).equals("TC")) {
				//대차인 경우
				intCRANE_GAP_UP_X = YdConstant.C_SLAB_CRANE_GAP_X1;
				intCRANE_GAP_UP_Y = YdConstant.C_SLAB_CRANE_GAP_Y1;
				
				intCRANE_GAP_DN_X = YdConstant.C_SLAB_CRANE_GAP_X1;
				intCRANE_GAP_DN_Y = YdConstant.C_SLAB_CRANE_GAP_Y1;
			}else if( szYD_UP_STK_COL_GP.substring(2, 4).equals("SB")
					|| szYD_DN_STK_COL_GP.substring(2, 4).equals("SB")) {
					//스카핑베드인 경우
				intCRANE_GAP_UP_X = YdConstant.C_SLAB_CRANE_GAP_Y1;
				intCRANE_GAP_UP_Y = YdConstant.C_SLAB_CRANE_GAP_Y1;
					
				intCRANE_GAP_DN_X = YdConstant.C_SLAB_CRANE_GAP_Y1;
				intCRANE_GAP_DN_Y = YdConstant.C_SLAB_CRANE_GAP_Y1;
			}else{
				intCRANE_GAP_UP_X = YdConstant.C_SLAB_CRANE_GAP_X;
				intCRANE_GAP_UP_Y = YdConstant.C_SLAB_CRANE_GAP_Y;
				
				intCRANE_GAP_DN_X = YdConstant.C_SLAB_CRANE_GAP_X;
				intCRANE_GAP_DN_Y = YdConstant.C_SLAB_CRANE_GAP_Y;
			}
			intCRANE_GAP_UP_Z = YdConstant.C_SLAB_CRANE_GAP_Z;
			
			intCRANE_GAP_DN_Z = YdConstant.C_SLAB_CRANE_GAP_Z;
			
			isUP_DN_XY_CAL		= true;
			
		}
		
		if( isUP_DN_XY_CAL ) {
			
			//------------------------------------------------------------------------------------------------------
			//	권상지시베드의 X,Y 좌표 조회
			//------------------------------------------------------------------------------------------------------
			
			szLogMsg	= "["+szOperationName+"] 권상지시베드[적치열:"+szYD_UP_STK_COL_GP+",적치베드:"+szYD_UP_STK_BED_NO+"]의 X,Y 좌표 조회 시작";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 		= JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_STK_COL_GP", 		szYD_UP_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", 		szYD_UP_STK_BED_NO);
			
			szRtnMsg		= DaoManager.getYdStkbed(recPara, rsResult, 0);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szLogMsg	= "["+szOperationName+"] 권상지시베드[적치열:"+szYD_UP_STK_COL_GP+",적치베드:"+szYD_UP_STK_BED_NO+"]의 X,Y 좌표 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				return szRtnMsg;
			}
			
			rsResult.first();
			recPara			= rsResult.getRecord();
			
			szUp_Grab_X_Value		= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_XAXIS");
			szUp_Grab_Y_Value		= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_YAXIS");
			
			szLogMsg	= "["+szOperationName+"] 권상지시베드[적치열:"+szYD_UP_STK_COL_GP+",적치베드:"+szYD_UP_STK_BED_NO+"]의 X,Y 좌표 조회 완료";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------------------
			
			//szDn_Grab_X_Value			= szYD_DN_STK_BED_XAXIS;
			//szDn_Grab_Y_Value			= szYD_DN_STK_BED_YAXIS;
			
			//------------------------------------------------------------------------------------------------------
			//	권하지시베드의 X,Y 좌표 조회
			//------------------------------------------------------------------------------------------------------
			
			szLogMsg	= "["+szOperationName+"] 권하지시베드[적치열:"+szYD_DN_STK_COL_GP+",적치베드:"+szYD_DN_STK_BED_NO+"]의 X,Y 좌표 조회 시작";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 		= JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_STK_COL_GP", 		szYD_DN_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", 		szYD_DN_STK_BED_NO);
			
			szRtnMsg		= DaoManager.getYdStkbed(recPara, rsResult, 0);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szLogMsg	= "["+szOperationName+"] 권하지시베드[적치열:"+szYD_DN_STK_COL_GP+",적치베드:"+szYD_DN_STK_BED_NO+"]의 X,Y 좌표 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				return szRtnMsg;
			}
			
			rsResult.first();
			recPara			= rsResult.getRecord();
			
			szDn_Grab_X_Value		= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_XAXIS");
			szDn_Grab_Y_Value		= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_YAXIS");
			
			szLogMsg	= "["+szOperationName+"] 권하지시베드[적치열:"+szYD_DN_STK_COL_GP+",적치베드:"+szYD_DN_STK_BED_NO+"]의 X,Y 좌표 조회 완료";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------------------
			
		}
		
		//-------------------------------------------------------------------------------------------------------------
		
		
		//-------------------------------------------------------------------------------------------------------------
		//크레인 스케줄  권하지시위치 업데이트
		//-------------------------------------------------------------------------------------------------------------
		recUpdCrnSchData = JDTORecordFactory.getInstance().create();
		
		recUpdCrnSchData.setField("YD_CRN_SCH_ID",  			szYD_CRN_SCH_ID);
		
		recUpdCrnSchData.setField("YD_TO_LOC_GUIDE",  szYD_TO_LOC_GUIDE); // AT000 물류시스템개선 2022.10.31 중간이적bed용 
		recUpdCrnSchData.setField("YD_EQP_WRK_SH",    szYD_EQP_WRK_SH);
		recUpdCrnSchData.setField("YD_EQP_WRK_WT",    szYD_EQP_WRK_WT);
		recUpdCrnSchData.setField("YD_EQP_WRK_T",     szYD_EQP_WRK_T);
		recUpdCrnSchData.setField("YD_EQP_WRK_MAX_W", szYD_EQP_WRK_MAX_W);
		recUpdCrnSchData.setField("YD_EQP_WRK_MAX_L", szYD_EQP_WRK_MAX_L);
		
		if( szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD) || szYD_GP.equals(YdConstant.YD_GP_PLATE2_GDS_YARD) ) {	//후판제품창고, 2후판제품창고
			//Grab 사용 구분
			recUpdCrnSchData.setField("YD_CRN_SB_CTL_H", szUp_Crane_Grab_Use_Gp);
		}
		
		if( !szYD_UP_WO_LOC.equals("") ) {
			recUpdCrnSchData.setField("YD_UP_WO_LOC",   			szYD_UP_WO_LOC) ;
			recUpdCrnSchData.setField("YD_UP_WO_LAYER", 			szYD_UP_WO_LAYER ) ;
		}
		
		recUpdCrnSchData.setField("YD_UP_WO_LOC_XAXIS",   		szUp_Grab_X_Value) ;
		recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_UP_X) ) ;
		recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_UP_X) ) ;
		recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS",   		szUp_Grab_Y_Value) ;
		recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS1",  		szUp_Grab_Y1_Value ) ;
		recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS2",  		szUp_Grab_Y2_Value ) ;
		recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_UP_Y) ) ;
		recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_UP_Y) ) ;
		recUpdCrnSchData.setField("YD_UP_WO_LOC_ZAXIS",  		szYD_UP_WO_LOC_ZAXIS ) ;
		recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_UP_Z) ) ;
		recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_UP_Z) ) ;
		
		if( !szYD_DN_WO_LOC.equals("") ) {
			recUpdCrnSchData.setField("YD_DN_WO_LOC",   			szYD_DN_WO_LOC) ;
			recUpdCrnSchData.setField("YD_DN_WO_LAYER", 			szYD_DN_WO_LAYER ) ;
		}
		
		recUpdCrnSchData.setField("YD_DN_WO_LOC_XAXIS",   		szDn_Grab_X_Value) ;
		recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_DN_X) ) ;
		recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_DN_X) ) ;
		recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS",   		szDn_Grab_Y_Value) ;
		recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS1",  		szDn_Grab_Y1_Value ) ;
		recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS2",  		szDn_Grab_Y2_Value ) ;
		recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_DN_Y) ) ;
		recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_DN_Y) ) ;
		recUpdCrnSchData.setField("YD_DN_WO_LOC_ZAXIS",  		szYD_DN_WO_LOC_ZAXIS ) ;
		recUpdCrnSchData.setField("YD_DN_WO_ZAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_DN_Z) ) ;
		recUpdCrnSchData.setField("YD_DN_WO_ZAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_DN_Z) ) ;
		
		
		ydUtils.displayRecord(szOperationName, recUpdCrnSchData);
		
//SJH0621		
//		szRtnMsg = DaoManager.updYdCrnsch(recUpdCrnSchData, 0);
		/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnWrkSidedelyn*/
		szRtnMsg = DaoManager.updYdCrnsch(recUpdCrnSchData, 303);
			
		//-------------------------------------------------------------------------------------------------------------
		
		//return YdConstant.RETN_CD_SUCCESS;
		return szRtnMsg;
	}
	
	/**
	 * 후판제품크레인스케줄수행판단
	 * @param recPara
	 * @return
	 */
	public static String procCheckIfCrnSchRunnableForPlateGds(JDTORecord recPara) {
		//기본변수정의
		String szLogMsg						= null;
		String szOperationName				= "후판제품크레인스케줄수행판단";
		String szMethodName					= "procCheckIfCrnSchRunnableForPlateGds";
		String szRtnMsg						= null;
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(recPara, "T");  // JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "후판제품크레인스케줄수행판단(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		szLogMsg	= "["+szOperationName+"] -------------------------- 메소드 시작 - 파라미터확인 -------------------------------";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szRtnMsg		= procCheckIfCrnSchRunnable(recPara);
		
		szLogMsg	= "["+szOperationName+"] 크레인스케줄수행판단 모듈 호출 후 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szLogMsg	= "["+szOperationName+"] -------------------------- 메소드 끝 -------------------------------";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
szLogMsg = "후판제품크레인스케줄수행판단(" + szMethodName + ") 완료";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return szRtnMsg;
	}
	
	/**
	 * 크레인스케줄수행판단
	 * @param recPara
	 * @return
	 */
	public static String procCheckIfCrnSchRunnable(JDTORecord recPara) {
		
		//기본변수정의
		String szLogMsg						= null;
		String szOperationName				= "크레인스케줄수행판단";
		String szMethodName					= "procCheckIfCrnSchRunnable";
		String szRtnMsg						= null;
		
		JDTORecord		recTemp				= null;
		
		//로컬변수 정의
		String szYD_SCH_CD					= null;					//스케줄코드
		String szYD_WBOOK_ID				= null;					//작업예약ID

		
		String logId                     = ydUtils.getJDTOLogId(recPara, "T");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
		
		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
		
		szLogMsg = "크레인스케줄수행판단(" + szMethodName + ") 시작";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		try {
			
			//-------------------------------------------------------------------------------------------------------------
			//	파라미터 확인
			//-------------------------------------------------------------------------------------------------------------
			szYD_SCH_CD				= ydDaoUtils.paraRecChkNull(recPara, 	"YD_SCH_CD");
			szYD_WBOOK_ID			= ydDaoUtils.paraRecChkNull(recPara, 	"YD_WBOOK_ID");
			
			szLogMsg	= "["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"]의 스케줄수행판단 시작";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			//-------------------------------------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄 기준 체크 - 스케줄금지유무와 주/대체크레인 교체 유무 판단
			//-------------------------------------------------------------------------------------------------------------
			if( !szYD_SCH_CD.equals("") ) {	//크레인스케줄코드가 존재할 때만 체크

				szLogMsg="[" + szOperationName + "] 크레인스케줄을 수행할 작업예약["+szYD_WBOOK_ID+"]의 스케줄코드["+szYD_SCH_CD+"]의 스케줄기준 조회 시작";
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				
				JDTORecord recParaTmp = JDTORecordFactory.getInstance().create();
				
				//신 통합스케줄 기준 조회
				//여기서  작업단위로 단계적으로 적용을 위하여 설비와 작업구분에 따라 구 스케줄 기준으로 분기 시킨다.
				//String szEQP_GP = szYD_SCH_CD.substring(2,4);
				//String szWRK_GP = szYD_SCH_CD.substring(6,7);
					
				//-- 통합 크레인 스케줄 조회 -----------------------------------------------------------------start--
				JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecordSet rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord recInPara = JDTORecordFactory.getInstance().create();
				YdPlateCommDAO commDao = new YdPlateCommDAO();
				
				recInPara.setField("YD_SCH_CD", szYD_SCH_CD);
				
				int intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0095");	
				
				if(intRtnVal == 0) {
					//szRtnMsg = YdConstant.YD_EQP_STAT_BREAK;
					//구 스케줄 기준 조회
					szRtnMsg	= YdCommonUtils.getWrkableCrnBySchRule(szYD_SCH_CD, recPara);							
				} else {
				
					//레코드 추출
					rsResult.first();
					recParaTmp = rsResult.getRecord();
					
					String wrkAbleCrn = ydDaoUtils.paraRecChkNull(recParaTmp, "WRK_CRN");
					
					String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "015");//후판 개발 적용여부
					//25.05.13 임진후기사 요청 B동 4번크레인 포트 고장으로 22950 이상 작업 등록시 B3 으로 가도록 예외처리
					if("Y".equals(sApplyYnPI) && "TBCRB4".equals(wrkAbleCrn)){
						recInPara = JDTORecordFactory.getInstance().create();
						
						//작업예약 ID 및 작업 크레인으로 작업재료 작업 가능여부 검색
						recInPara.setField("WRK_CRN", wrkAbleCrn);
						recInPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
						
						intRtnVal = commDao.select(recInPara, rsResult2, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.chkCrnSpecWithWbookId");	
						rsResult2.first();
						
						JDTORecord recParaTmp2 = JDTORecordFactory.getInstance().create();
						recParaTmp2 = rsResult2.getRecord();
						
						String ableYn = ydDaoUtils.paraRecChkNull(recParaTmp2, "WRK_ABLE_YN");
						
						if("N".equals(ableYn)){
							szLogMsg	= "["+szOperationName+"] TBCRB4 크레인이 작업할 수 없는 길이이므로 TBCRB3 으로 크레인 변경";
							ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
							wrkAbleCrn = "TBCRB3";
							
							recPara.setField("FORCE_CHANGE_YN", 		"Y");// B4->B3 강제 변경에 따라 추후 로직에서 B3 로 크레인 고정을 위해 생성	
						}
						
					}
					
					
					//작업가능한 크레인
					recPara.setField("YD_WRKABLE_CRN", 		wrkAbleCrn);					
					//스케쥴우선순위
					recPara.setField("YD_SCH_PRIOR", 		ydDaoUtils.paraRecChkNull(recParaTmp, "CRN_PRIOR"));	
					
					szRtnMsg = YdConstant.RETN_CD_SUCCESS;
				}
				//-- 통합 크레인 스케줄 조회 ------------------------------------------------------------------end---
				
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					szLogMsg="[" + szOperationName + "] 크레인스케줄을 수행할 작업예약["+szYD_WBOOK_ID+"]의 스케줄코드["+szYD_SCH_CD+"]의 스케줄기준 조회 후 오류발생 - 메세지 : " + szRtnMsg;
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					return szRtnMsg;
				}
				
				szLogMsg="[" + szOperationName + "] 크레인스케줄을 수행할 작업예약["+szYD_WBOOK_ID+"]의 스케줄코드["+szYD_SCH_CD+"]의 스케줄기준 조회 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				
			}
			//-------------------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------------------
			//	해당 작업예약의 작업예약재료들을 대상으로 크레인스케줄수행판단 모듈 호출
			//-------------------------------------------------------------------------------------------------------------
			szLogMsg	= "["+szOperationName+"] 작업예약["+szYD_SCH_CD+"]의 작업재료로 크레인스케줄수행판단 모듈 호출 시작";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			recTemp 		= JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);
			recTemp.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);

			recTemp.setField("LOG_ID", logId);
			
			szRtnMsg		= checkIfSchableByYdWbookId(recTemp);
			
			szLogMsg	= "["+szOperationName+"] 작업예약["+szYD_SCH_CD+"]의 작업재료로 크레인스케줄수행판단 모듈 호출 완료 - 메세지 : " + szRtnMsg;
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				szLogMsg	= "["+szOperationName+"] 작업예약["+szYD_SCH_CD+"]의 작업재료로 크레인스케줄수행판단 모듈 호출 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
				
				return szRtnMsg;
			}
			//-------------------------------------------------------------------------------------------------------------
			
			szLogMsg	= "["+szOperationName+"] -------------------------- 메소드 끝 -------------------------------";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
		}catch(Exception ex) {
			
			szLogMsg	= "["+szOperationName+"] 예외발생 - " + ex.getMessage();
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			
			throw new DAOException(ex);
		}
		
		szLogMsg = "크레인스케줄수행판단(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 크레인스케줄수행판단(작업예약ID)
	 * @param recPara
	 * @return
	 * @throws JDTOException
	 */
	public static String checkIfSchableByYdWbookId(JDTORecord recPara) throws JDTOException {
		/*
		 * 업무기준 	:	1. 스케줄코드로 스케줄기준 조회 후 스케줄금지유무판단과 주/대체작업크레인 조회
		 * 				2. 작업예약의 작업재료들의 상단 더미재가 크레인스케줄의 작업재료로 등록되어 있는 지 조회
		 * 					2-1. 등록되어 있으면 동일한 스케줄코드인 지 판단.
		 * 
		 * 파라미터	:	1. YD_WBOOK_ID		- 작업예약ID
		 * 				2. YD_SCH_CD		- 스케줄코드
		 * 
		 * 등록자	: 임춘수
		 * 등록일	: 2010.01.07
		 * 수정일	: 				
		 */
		//기본변수정의
		String szLogMsg						= null;
		String szOperationName				= "크레인스케줄수행판단(작업예약ID)";
		String szMethodName					= "checkIfSchableByYdWbookId";
		String szRtnMsg						= null;
		
		JDTORecord		recTemp				= null;
		JDTORecordSet	rsResult			= null;
		
		//로컬변수 정의
		String szYD_WBOOK_ID				= null;							//작업예약ID
		String szYD_CRN_SCH_ID				= null;							//크레인스케줄ID
		String szSTL_NO						= null;							//작업재료
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(recPara, "T");  // JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "크레인스케줄수행판단(작업예약ID)(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);

// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		//-------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//-------------------------------------------------------------------------------------------------------------
		szYD_WBOOK_ID			= ydDaoUtils.paraRecChkNull(recPara, 	"YD_WBOOK_ID");
		//-------------------------------------------------------------------------------------------------------------
		
		
		//-------------------------------------------------------------------------------------------------------------
		//	1. 작업예약재료 대상재들이 이미 크레인 스케줄의 작업대상으로 되어 있는 지를 판단
		//		크레인스케줄의 작업대상으로 되어 있으면 크레인스케줄을 수행하지 않는다.
		//		해당 크레인스케줄 수행이 완료되고 난 후 권하실적 처리에서 크레인작업요구지시 모듈을 호출하면 모듈내에서
		//		작업예약을 풀어준다.
		//-------------------------------------------------------------------------------------------------------------
		
		szLogMsg	= "["+szOperationName+"] 해당작업예약["+szYD_WBOOK_ID+"]의 작업재료가 다른 작업예약의 크레인스케줄의 작업재료로 존재하는지 조회 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		rsResult			=  JDTORecordFactory.getInstance().createRecordSet("");
		
		szRtnMsg			= DaoManager.getYdCrnwrkmtl(recPara, rsResult, 18);
		
		szLogMsg	= "["+szOperationName+"] 해당작업예약["+szYD_WBOOK_ID+"]의 작업재료가 다른 작업예약의 크레인스케줄의 작업재료로 존재하는지 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			
			rsResult.first();
			
			recTemp				= rsResult.getRecord();
			
			szYD_CRN_SCH_ID		= ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_SCH_ID");
			
			szSTL_NO			= ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
			
			szLogMsg		= "["+szOperationName+"] 해당작업예약["+szYD_WBOOK_ID+"]의 작업재료가 다른 작업예약의 크레인스케줄["+szYD_CRN_SCH_ID+"]의 작업재료["+szSTL_NO+"]로 존재함으로 스케줄기동이 불가합니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			
			return YdConstant.RETN_CRN_EXIST_SCH;
			
		}else if( !szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
			
			szLogMsg	= "["+szOperationName+"] 해당작업예약["+szYD_WBOOK_ID+"]의 작업재료가 다른 작업예약의 크레인스케줄의 작업재료로 존재하는지 조회 시 오류발생 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			
			return szRtnMsg;
		}
		
		//-------------------------------------------------------------------------------------------------------------
		
		
		//-------------------------------------------------------------------------------------------------------------
		//	2. 작업예약재료 대상재들의 상단 더미재들이 크레인스케줄의 작업대상재로 존재하는지 판단
		//		존재하는 경우에는 동일한 스케줄일 때만 크레인스케줄 수행을 처리
		//-------------------------------------------------------------------------------------------------------------
		
		szLogMsg		= "["+szOperationName+"] 해당작업예약["+szYD_WBOOK_ID+"]의 작업재료들의 상단재료들이 다른 스케줄코드의 크레인스케줄의 작업재료로 존재하는지 조회 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		rsResult			=  JDTORecordFactory.getInstance().createRecordSet("");
		
		szRtnMsg			= DaoManager.getYdCrnwrkmtl(recPara, rsResult, 19);
		
		szLogMsg		= "["+szOperationName+"] 해당작업예약["+szYD_WBOOK_ID+"]의 작업재료들의 상단재료들이 다른 스케줄코드의 크레인스케줄의 작업재료로 존재하는지 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			
			rsResult.first();
			
			recTemp				= rsResult.getRecord();
			
			szYD_CRN_SCH_ID				= ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_SCH_ID");
			
			szSTL_NO					= ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
			
			szLogMsg		= "["+szOperationName+"] 해당작업예약["+szYD_WBOOK_ID+"]의 작업재료들의 상단재료들이 다른 스케줄코드의 크레인스케줄["+szYD_CRN_SCH_ID+"]의 작업재료["+szSTL_NO+"]로 존재함으로 스케줄기동이 불가합니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			
			return YdConstant.RETN_CRN_EXIST_SCH;
			
		}else if( !szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
			
			szLogMsg		= "["+szOperationName+"] 해당작업예약["+szYD_WBOOK_ID+"]의 작업재료들의 상단재료들이 다른 스케줄코드의 크레인스케줄의 작업재료로 존재하는지 조회 시 오류발생 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			
			return szRtnMsg;
		}
		
		//-------------------------------------------------------------------------------------------------------------
		
		szLogMsg	= "["+szOperationName+"] -------------------------- 메소드 끝 -------------------------------";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "크레인스케줄수행판단(작업예약ID)(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
}