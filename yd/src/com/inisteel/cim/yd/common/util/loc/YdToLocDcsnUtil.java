/**
 * @(#)YdToLocDcsnUtil.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2011/07/11
 * 
 * @description		이클래스는 YD에서 사용되는 TO위치결정하는 클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2011/07/11                    최초 등록
 * V1.01  2013/05/04   조병기       조병기      getYdBayLocPln3G 추가  
 *                 
 */

/**
 * YD에서 사용되는 TO위치결정하는 클래스
 */
package com.inisteel.cim.yd.common.util.loc;

import java.util.ArrayList;
import java.util.List;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ptOsCommDao.PtOsCommDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.rule.GetBreRule0;
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.crn.CrnSchUtil;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

import edu.emory.mathcs.backport.java.util.Collections;



/**
 * @author 임춘수
 *
 */
public class YdToLocDcsnUtil {
	private static String szClassName = YdToLocDcsnUtil.class.getName();
	private static YdUtils ydUtils = new YdUtils();
	private static YdDaoUtils ydDaoUtils = new YdDaoUtils();
	private String szSessionName=getClass().getName();
	private static YDDataUtil  yddatautil          = new YDDataUtil();
	
	
	//------------------------------------------------------------------------------------------------------------------------------------
	// 사용자가 지정한 위치로 TO위치를 결정
	//------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * 사용자가 지정한 위치로 TO위치를 결정하는 메소드 - 각 야드별로 분리
	 * @param recPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String procUserAsgnToLoc(JDTORecord recPara, List listToLoc) throws JDTOException {
		/*
		 * 업무기준 :	1. 사용자가 지정한 위치 정보를 사용하여 야드별로 모듈을 분리
		 * 수정자 : 임춘수
		 * 수정일 : 
		 * 				1) 2009.11.16 - 최초 등록
		 * 파라미터정의:	1) YD_TO_LOC_GUIDE	- 사용자지정위치(적치열+적치베드)
		 * 				2) YD_EQP_WRK_SH	- 작업총매수
		 * 				3) YD_EQP_WRK_WT	- 작업총중량
		 * 				4) YD_EQP_WRK_T		- 작업총두께
		 * 				5) YD_SCH_CD		- 스케줄코드
		 */
		if( listToLoc == null ) listToLoc = new ArrayList();
		String szMethodName		= "procUserAsgnToLoc";
		String szOperationName	= "To위치결정-사용자지정";
		String szLogMsg			= null;
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szYD_GP	= null;
		String szYD_TO_LOC_GUIDE = null;
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		szYD_TO_LOC_GUIDE = ydDaoUtils.paraRecChkNull(recPara, "YD_TO_LOC_GUIDE");
		
		szLogMsg = "["+ szOperationName +"] 파라미터로 전달된 TO위치가이드["+szYD_TO_LOC_GUIDE+"]입니다.";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		if( szYD_TO_LOC_GUIDE.equals("") ) {
			szLogMsg = "["+ szOperationName +"] TO위치가이드가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		
		szYD_GP = szYD_TO_LOC_GUIDE.substring(0, 1);
		
		if( szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD)) {							//C연주슬라브야드
			szRtnMsg = procUserAsgnToLocForCSlabYard(recPara, listToLoc);
		}else if( szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD)) {				//항만슬라브야드 기능추가 - 2016.01.07 LeeJY
			szRtnMsg = procUserAsgnToLocForCSlabYard(recPara, listToLoc);
		}else if( szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)) {				//A후판슬라브야드
			
		}else if( szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD)) {			//C열연코일소재야드
			
		}else if( szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD)) {			//C열연코일제품야드
			
		}else if( szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD)) {				//후판제품창고야드
			szRtnMsg = procUserAsgnToLocForPlateGdsYard(recPara, listToLoc);
		}else if( szYD_GP.equals(YdConstant.YD_GP_INTGR_YARD)) {					//통합야드
			
		}
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return szRtnMsg;
	}
	
	/**
	 * 사용자가 지정한 위치로 TO위치를 결정하는 메소드 - C연주슬라브야드
	 * @param szYD_TO_LOC_GUIDE
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String procUserAsgnToLocForCSlabYard(JDTORecord recPara,  List listToLoc) throws JDTOException {
		/*
		 * 업무기준 :		1. BED MAX 개수을 넘어서면 적치불가능
		 * 				2. BED MAX 중량을 넘어서면 적치불가능
		 * 				3. 해당 위치의 상단이 크레인스케줄이 존재하는 지 판단
		 * 					3-1. 크레인 스케줄이 존재하면
		 * 						3-1-1. 권하인 지 판단
		 * 							3-1-1-1. 권하이면서 같은 스케줄코드이면 **** 적치가능 ****
		 * 							3-1-1-2. 권하이면서 다른 스케줄코드이고 우선순위가 빠른 스케줄인 지 판단
		 * 								3-1-1-2-1. 빠른 스케줄이면 **** 적치가능 ****
		 * 								3-1-1-2-2. 늦은 스케줄이면 적치불가능
		 * 						3-1-2. 권상이면 적치불가능
		 * 					3-2. 크레인 스케줄이 존재하지 않으면  **** 적치가능  ****
		 */
		if( listToLoc == null ) listToLoc = new ArrayList();
		String szMethodName		= "procUserAsgnToLocForCSlabYard";
		String szOperationName	= "To위치결정-사용자지정(C연주슬라브야드)";
		String szLogMsg			= null;
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		
		szRtnMsg = procAsgnedBedStackable(recPara, listToLoc, szOperationName);
		
		return szRtnMsg;
		
	}
	
	/**
	 * 사용자가 지정한 위치로 TO위치를 결정하는 메소드 - 후판제품야드
	 * @param szYD_TO_LOC_GUIDE
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String procUserAsgnToLocForPlateGdsYard(JDTORecord recPara,  List listToLoc) throws JDTOException {
		/*
		 * 업무기준 :		1. BED MAX 개수을 넘어서면 적치불가능
		 * 				2. BED MAX 중량을 넘어서면 적치불가능
		 * 				3. 해당 위치의 상단이 크레인스케줄이 존재하는 지 판단
		 * 					3-1. 크레인 스케줄이 존재하면
		 * 						3-1-1. 권하인 지 판단
		 * 							3-1-1-1. 권하이면서 같은 스케줄코드이면 **** 적치가능 ****
		 * 							3-1-1-2. 권하이면서 다른 스케줄코드이고 우선순위가 빠른 스케줄인 지 판단
		 * 								3-1-1-2-1. 빠른 스케줄이면 **** 적치가능 ****
		 * 								3-1-1-2-2. 늦은 스케줄이면 적치불가능
		 * 						3-1-2. 권상이면 적치불가능
		 * 					3-2. 크레인 스케줄이 존재하지 않으면  **** 적치가능  ****
		 */
		if( listToLoc == null ) listToLoc = new ArrayList();
		String szMethodName		= "procUserAsgnToLocForPlateGdsYard";
		String szOperationName	= "To위치결정-사용자지정(후판제품야드)";
		String szLogMsg			= null;
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		
		szRtnMsg = procAsgnedBedStackable(recPara, listToLoc, szOperationName);
		
		return szRtnMsg;
		
	}
	
	/**
	 * To위치결정-사용자지정(공통) : 해당bed에 적치가능 유무 판단 로직으로 사용
	 * @param recPara
	 * @param listToLoc
	 * @param szFromMethod
	 * @return
	 * @throws JDTOException
	 */
	public static String procAsgnedBedStackable(JDTORecord recPara,  List listToLoc, String szFromMethod) throws JDTOException {
		/*
		 * 업무기준 :		0. 해당 위치의 BED정보와 단 정보를 조회
		 * 				1. BED MAX 개수을 넘어서면 적치불가능
		 * 				2. BED MAX 중량을 넘어서면 적치불가능
		 * 				3. 해당 위치의 상단이 크레인스케줄이 존재하는 지 판단
		 * 					3-1. 크레인 스케줄이 존재하면
		 * 						3-1-1. 권하인 지 판단
		 * 							3-1-1-1. 권하이면서 같은 스케줄코드이면 **** 적치가능 ****
		 * 							3-1-1-2. 권하이면서 다른 스케줄코드이고 우선순위가 빠른 스케줄인 지 판단
		 * 								3-1-1-2-1. 빠른 스케줄이면 **** 적치가능 ****
		 * 								3-1-1-2-2. 늦은 스케줄이면 적치불가능
		 * 						3-1-2. 권상이면 적치불가능
		 * 					3-2. 크레인 스케줄이 존재하지 않으면  **** 적치가능  ****
		 * 수정자 : 임춘수
		 * 수정일 : 
		 * 				1) 2009.11.16 - 최초 등록
		 * 
		 * 파라미터정의:	1) YD_TO_LOC_GUIDE	- 사용자지정위치(적치열+적치베드)
		 * 				2) YD_EQP_WRK_SH	- 작업총매수
		 * 				3) YD_EQP_WRK_WT	- 작업총중량
		 * 				4) YD_EQP_WRK_T		- 작업총두께
		 * 				5) YD_SCH_CD		- 스케줄코드
		 */
		if( listToLoc == null ) listToLoc = new ArrayList();
		String szMethodName				= "procAsgnedBedStackable";
		String szOperationName			= "To위치결정-사용자지정(공통)";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		int	intYD_BED_ERR_CD			= 0;
		
		JDTORecord recTemp				= null;
		JDTORecordSet rsTemp			= null;
		
		String szYD_TO_LOC_GUIDE 		= null;
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_STK_LYR_NO			= null;
		String szSTL_NO					= null;
		String szYD_SCH_CD				= null;
		String szYD_STK_BED_ACT_STAT	= null;
		String szYD_STK_BED_WHIO_STAT	= null;
		String szYD_STK_LYR_MTL_STAT	= null;
		
		YdStkLocVO ydStkLocVO			= null;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(recPara, "T");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "To위치결정-사용자지정(공통)(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		szYD_TO_LOC_GUIDE 	= ydDaoUtils.paraRecChkNull(recPara, "YD_TO_LOC_GUIDE");				//사용자지정위치
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");						//스케줄코드
		
		szLogMsg = "["+ szOperationName +"] 파라미터로 전달된 TO위치가이드["+szYD_TO_LOC_GUIDE+"]입니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		if( szYD_TO_LOC_GUIDE.equals("") ) {
			szLogMsg = "["+ szOperationName +"] TO위치가이드가 존재하지 않습니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	베드분석정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYD_STK_COL_GP = szYD_TO_LOC_GUIDE.substring(0, 6);
		szYD_STK_BED_NO = szYD_TO_LOC_GUIDE.substring(6);
		
		//베드분석정보 조회
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 베드정보 조회 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
		recTemp = JDTORecordFactory.getInstance().create();
		recTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
		recTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// getYdStkBedAnalysis call 시  inRecord 에 logId SET 추가 개선
		recTemp.setField("LOG_ID", logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		szRtnMsg = getYdStkBedAnalysis(recTemp, rsTemp, YdConstant.MTL_STAT_C_U_D);
		
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 베드정보 조회 완료 - 반환값 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 베드정보 조회 시 오류발생";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	베드상태와 기존 스케줄이 존재하는 지 체크
		//----------------------------------------------------------------------------------------------------------------------
		rsTemp.first();
		recTemp = rsTemp.getRecord();
		
		szSTL_NO 				= ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
		szYD_STK_BED_ACT_STAT 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_ACT_STAT");
		szYD_STK_BED_WHIO_STAT 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT");
		//szYD_STK_LYR_MTL_STAT 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT"); 
		szYD_STK_LYR_MTL_STAT 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT2"); //2013.05.27 수정 chobg
		
		if( !szYD_STK_BED_ACT_STAT.equals(YdConstant.YD_STK_BED_ACTIVE) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 활성상태["+szYD_STK_BED_ACT_STAT+"]가 적치가능상태가 아닙니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_BED_INACT;
		}
		
		if( !szYD_STK_BED_WHIO_STAT.equals(YdConstant.YD_STK_BED_WHIO_ENABLE) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 야드적치Bed입출고상태["+szYD_STK_BED_WHIO_STAT+"]가 입고가능상태가 아닙니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_BED_WHIO_NOT_IN;
		}
		
		if( szYD_STK_LYR_MTL_STAT.equals(YdConstant.YD_STK_LYR_MTL_STAT_UN_WAIT)) {				//권상대기이면 적치불가능
			szLogMsg = "["+ szOperationName +"] 적치된 재료["+szSTL_NO+"]보다 적치재료상태["+szYD_STK_LYR_MTL_STAT+"]가 권상대기이므로 적치불가능";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_BED_UN_WAIT;
		}else if( szYD_STK_LYR_MTL_STAT.equals(YdConstant.YD_STK_LYR_MTL_STAT_DN_WAIT)) {		//권하대기이면 스케줄코드비교
			/*
			 * 2010.12.24 윤재광 - 권하대기 제품에 대해서는 체크 SKIP
			 */
			/*
			szRtnMsg = compareYdSchCdWithStlInStk(szSTL_NO, szYD_SCH_CD);
			//같은 스케줄코드이거나 적치된 재료보다 우선순위가 늦으면 적치가능
			if( szRtnMsg.equals(YdConstant.RETN_SAME_SCH_CD) 
				|| szRtnMsg.equals(YdConstant.RETN_SCH_LATE_PRIOR)) {
				szLogMsg = "["+ szOperationName +"] 같은 스케줄코드이거나 적치된 재료["+szSTL_NO+"]보다 스케줄 우선순위가 늦으므로 적치가능";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
			}else{
				szLogMsg = "["+ szOperationName +"] 같은 스케줄코드아니고 적치된 재료["+szSTL_NO+"]보다 스케줄 우선순위가 빠르므로 적치불가능";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
				return szRtnMsg;
			}
			*/
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	베드 적치가능유무 판단
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]에 적치가능유무 판단 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		intYD_BED_ERR_CD = YdCommonUtils.chkBedStackable(recPara, recTemp);

		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]에 적치가능유무 판단 완료 - 반환값 : " + intYD_BED_ERR_CD;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);

		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	적치가능한 베드의 적치단을 1 증가 시킴 - 값이 없으면 001(1단)으로 설정
		//----------------------------------------------------------------------------------------------------------------------
		if( intYD_BED_ERR_CD == YdConstant.YD_BED_STACKABLE ) {
			
			szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");
			
			szLogMsg = "["+ szOperationName +"] 적치베드의 조회된 적치단["+szYD_STK_LYR_NO+"]에 1 증가시킴 - 값이 없으면 001(1단)으로 설정  ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			
			if( szYD_STK_LYR_NO.equals("") ) {							//값이 없으면
				szYD_STK_LYR_NO = "001";										//1단
			}else{														//값이 존재하면
				szYD_STK_LYR_NO = ydDaoUtils.stringPlusInt(szYD_STK_LYR_NO, 1);	//조회된 적치단 + 1
			}
			
			szLogMsg = "["+ szOperationName +"] 계산된 단["+szYD_STK_LYR_NO+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		}else{
			
			szYD_STK_LYR_NO = "000";
			
		}
		
		recTemp.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
		
		ydUtils.displayRecord(szOperationName, recTemp);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	JDTORecord를 VO객체로 변환
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 레코드를 VO객체로 변환 시작  ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		ydStkLocVO = procRecord2StkLoc(recTemp);
		
		szLogMsg = "["+ szOperationName +"] 레코드를 VO객체로 변환 완료  ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		ydStkLocVO.setSeq(YdConstant.TO_LOC_PRIOR_USER);
		ydStkLocVO.setPrior(YdConstant.TO_LOC_PRIOR_USER);
		
		listToLoc.add(ydStkLocVO);
		//----------------------------------------------------------------------------------------------------------------------
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "To위치결정-사용자지정(공통)(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
		
	}
	

	
	/**
	 * BED적치가능유무판단
	 * @param recPara
	 * @param listToLoc
	 * @param szFromMethod
	 * @return
	 * @throws JDTOException
	 */
	public static String procBedStackable(JDTORecord recPara, YdStkLocVO ydStkLocVO, String szFromMethod) throws JDTOException {
		/*
		 * 업무기준 :		0. 해당 위치의 BED정보와 단 정보를 조회
		 * 				1. BED MAX 개수을 넘어서면 적치불가능
		 * 				2. BED MAX 중량을 넘어서면 적치불가능
		 * 				3. 해당 위치의 상단이 크레인스케줄이 존재하는 지 판단
		 * 					3-1. 크레인 스케줄이 존재하면
		 * 						3-1-1. 권하인 지 판단
		 * 							3-1-1-1. 권하이면서 같은 스케줄코드이면 **** 적치가능 ****
		 * 							3-1-1-2. 권하이면서 다른 스케줄코드이고 우선순위가 빠른 스케줄인 지 판단
		 * 								3-1-1-2-1. 빠른 스케줄이면 **** 적치가능 ****
		 * 								3-1-1-2-2. 늦은 스케줄이면 적치불가능
		 * 						3-1-2. 권상이면 적치불가능
		 * 					3-2. 크레인 스케줄이 존재하지 않으면  **** 적치가능  ****
		 * 수정자 : 임춘수
		 * 수정일 : 
		 * 				1) 2009.11.16 - 최초 등록
		 * 
		 * 파라미터정의:	1) YD_STK_COL_GP	- 적치열
		 * 				2) YD_STK_BED_NO	- 적치베드
		 * 				3) YD_EQP_WRK_SH	- 작업총매수
		 * 				4) YD_EQP_WRK_WT	- 작업총중량
		 * 				5) YD_EQP_WRK_T		- 작업총두께
		 * 				6) YD_SCH_CD		- 스케줄코드
		 */
		if( ydStkLocVO == null ) ydStkLocVO = new YdStkLocVO();
		String szMethodName			= "procBedStackable";
		String szOperationName		= "BED적치가능유무판단";
		String szLogMsg				= null;
		String szRtnMsg 			= YdConstant.RETN_CD_SUCCESS;
		int	intYD_BED_ERR_CD		= 0;
		
		JDTORecord recTemp			= null;
		JDTORecordSet rsTemp		= null;
		
		//String szYD_TO_LOC_GUIDE 	= null;
		String szYD_STK_COL_GP		= null;
		String szYD_STK_BED_NO		= null;
		String szYD_STK_LYR_NO		= null;
		String szSTL_NO				= null;
		String szYD_SCH_CD			= null;
		String szYD_STK_BED_ACT_STAT	= null;
		String szYD_STK_BED_WHIO_STAT	= null;
		String szYD_STK_LYR_MTL_STAT	= null;
		String forceDownYn				= null;
//		int intYD_STK_BED_LYR_MAX	= 0;					//베드정보 - 단MAX
//		int intYD_STK_BED_WT_MAX	= 0;					//베드정보 - 총중량
//		double dblYD_STK_BED_H_MAX	= 0;					//베드정보 - 총높이
//		
//		int intYD_MTL_SH			= 0;					//적치된 재료의 총매수
//		int intYD_MTL_WT_SUM		= 0;					//적치된 재료의 총중량
//		double dblYD_MTL_T_SUM		= 0;					//적치된 재료의 총두께
//		
//		int intYD_EQP_WRK_SH		= 0;					//크레인작업총매수
//		int intYD_EQP_WRK_WT		= 0;					//크레인작업총중량
//		double dblYD_EQP_WRK_T		= 0;					//크레인작업총두께
		
		//YdStkLocVO ydStkLocVO		= null;
		
		String logId                            = ydUtils.getJDTOLogId(recPara, "T");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
		
		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
		
		szLogMsg = "BED적치가능유무판단(" + szMethodName + ") 시작";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		//szYD_TO_LOC_GUIDE 	= ydDaoUtils.paraRecChkNull(recPara, "YD_TO_LOC_GUIDE");			//사용자지정위치
		
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");				//적치열구분
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");				//적치베드번호
		
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");					//스케줄코드
		
		forceDownYn = ydDaoUtils.paraRecChkNull(recPara, "FORCE_DOWN_YN");//권하위치 변경시 강제변경 여부
		
//		intYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNullInt(recPara, "YD_EQP_WRK_SH");				//크레인작업총매수
//		intYD_EQP_WRK_WT = ydDaoUtils.paraRecChkNullInt(recPara, "YD_EQP_WRK_WT");				//크레인작업총중량
//		dblYD_EQP_WRK_T = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_EQP_WRK_T");				//크레인작업총두께
		
		
		if( szYD_STK_COL_GP.equals("") ) {
			szLogMsg = "["+ szOperationName +"] 적치열구분이 존재하지 않습니다.";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		
		if( szYD_STK_BED_NO.equals("") ) {
			szLogMsg = "["+ szOperationName +"] 적치베드번호가 존재하지 않습니다.";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	베드분석정보 조회
		//----------------------------------------------------------------------------------------------------------------------
//		szYD_STK_COL_GP = szYD_TO_LOC_GUIDE.substring(0, 6);
//		szYD_STK_BED_NO = szYD_TO_LOC_GUIDE.substring(6);
		
		//베드분석정보 조회
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 베드정보 조회 시작";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
		recTemp = JDTORecordFactory.getInstance().create();
		recTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
		recTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
		recTemp.setField("FORCE_DOWN_YN", forceDownYn);
		recTemp.setField("LOG_ID", logId);
		
		szRtnMsg = getYdStkBedAnalysis(recTemp, rsTemp, YdConstant.MTL_STAT_C_U_D);
		
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 베드정보 조회 완료 - 반환값 : " + szRtnMsg;
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 베드정보 조회 시 오류발생";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	베드상태와 기존 스케줄이 존재하는 지 체크
		//----------------------------------------------------------------------------------------------------------------------
		rsTemp.first();
		recTemp = rsTemp.getRecord();
		
		szSTL_NO = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
		szYD_STK_BED_ACT_STAT = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_ACT_STAT");
		szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT");
		//szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT");
		szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT2"); //2013.05.27 수정 chobg
		
		if( !szYD_STK_BED_ACT_STAT.equals(YdConstant.YD_STK_BED_ACTIVE) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 활성상태["+szYD_STK_BED_ACT_STAT+"]가 적치가능상태가 아닙니다.";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_BED_INACT;
		}
		
		//25.05.14 임진후기사 요청. "완산베드 제외". 가적베드, 입출고불가베드 등은 강제권하위치변경시 허용되어야 함. 
		if("Y".equals(forceDownYn) && !szYD_STK_BED_WHIO_STAT.equals(YdConstant.YD_STK_BED_WHIO_FULL)){
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 입출고상태["+szYD_STK_BED_WHIO_STAT+"]가 적치가능상태가 아니지만, 강제권하위치변경이므로 허용.";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
		}
		else if( !szYD_STK_BED_WHIO_STAT.equals(YdConstant.YD_STK_BED_WHIO_ENABLE) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 입출고상태["+szYD_STK_BED_WHIO_STAT+"]가 입고가능상태가 아닙니다.";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_BED_WHIO_NOT_IN;
		}
		
		if( szYD_STK_LYR_MTL_STAT.equals(YdConstant.YD_STK_LYR_MTL_STAT_UN_WAIT)) {				//권상대기이면 적치불가능
			szLogMsg = "["+ szOperationName +"] 적치된 재료["+szSTL_NO+"]보다 적치재료상태["+szYD_STK_LYR_MTL_STAT+"]가 권상대기이므로 적치불가능";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_BED_UN_WAIT;
		}else if( szYD_STK_LYR_MTL_STAT.equals(YdConstant.YD_STK_LYR_MTL_STAT_DN_WAIT)) {		//권하대기이면 스케줄코드비교
			/*
			 * 2010.12.24 윤재광 - 권하대기 제품에 대해서는 체크 SKIP
			 */
			/*
			szRtnMsg = compareYdSchCdWithStlInStk(szSTL_NO, szYD_SCH_CD);
			//같은 스케줄코드이거나 적치된 재료보다 우선순위가 늦으면 적치가능
			if( szRtnMsg.equals(YdConstant.RETN_SAME_SCH_CD) 
				|| szRtnMsg.equals(YdConstant.RETN_SCH_LATE_PRIOR)) {
				szLogMsg = "["+ szOperationName +"] 같은 스케줄코드이거나 적치된 재료["+szSTL_NO+"]보다 스케줄 우선순위가 늦으므로 적치가능";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
			}else{
				szLogMsg = "["+ szOperationName +"] 같은 스케줄코드아니고 적치된 재료["+szSTL_NO+"]보다 스케줄 우선순위가 빠르므로 적치불가능";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
				return szRtnMsg;
			}
			*/
			
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	베드에 적치중인 운송대기(N) 대상이 있는지 체크
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//----------------------------------------------------------------------------------------------------------------------
		//	베드 적치가능유무 판단
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]에 적치가능유무 판단 시작";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		intYD_BED_ERR_CD = YdCommonUtils.chkBedStackable(recPara, recTemp);

		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]에 적치가능유무 판단 완료 - 반환값 : " + intYD_BED_ERR_CD;
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	적치가능한 베드의 적치단을 1 증가 시킴 - 값이 없으면 001(1단)으로 설정
		//----------------------------------------------------------------------------------------------------------------------
		if( intYD_BED_ERR_CD == YdConstant.YD_BED_STACKABLE ) {
			
			szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");
			
			szLogMsg = "["+ szOperationName +"] 적치베드의 조회된 적치단["+szYD_STK_LYR_NO+"]에 1 증가시킴 - 값이 없으면 001(1단)으로 설정  ";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			
			if( szYD_STK_LYR_NO.equals("") ) {							//값이 없으면
				szYD_STK_LYR_NO = "001";										//1단
			}else{														//값이 존재하면
				szYD_STK_LYR_NO = ydDaoUtils.stringPlusInt(szYD_STK_LYR_NO, 1);	//조회된 적치단 + 1
			}
			
			szLogMsg = "["+ szOperationName +"] 계산된 단["+szYD_STK_LYR_NO+"]";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		}else{
			
			szYD_STK_LYR_NO = "000";
			procRecord2StkLoc(recTemp, ydStkLocVO);
			
			return Integer.toString(intYD_BED_ERR_CD);
			
		}
		
		recTemp.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
		
		ydUtils.displayRecord(szOperationName, recTemp);
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	JDTORecord를 VO객체로 변환
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 레코드를 VO객체로 변환 시작  ";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		procRecord2StkLoc(recTemp, ydStkLocVO);
		
		szLogMsg = "["+ szOperationName +"] 레코드를 VO객체로 변환 완료  ";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		//ydStkLocVO.setSeq(YdConstant.TO_LOC_PRIOR_USER);
		//ydStkLocVO.setPrior(YdConstant.TO_LOC_PRIOR_USER);
		
		//listToLoc.add(ydStkLocVO);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "BED적치가능유무판단(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		return YdConstant.RETN_CD_SUCCESS;
		
	}
	
	/**
	 * BED적치가능유무판단(입고가적베드)
	 * @param recPara
	 * @param listToLoc
	 * @param szFromMethod
	 * @return
	 * @throws JDTOException
	 */
	public static String procBedStackableTmpBed(JDTORecord recPara, YdStkLocVO ydStkLocVO, String szFromMethod) throws JDTOException {
		/*
		 * 업무기준 :		0. 해당 위치의 BED정보와 단 정보를 조회
		 * 				1. BED MAX 개수을 넘어서면 적치불가능
		 * 				2. BED MAX 중량을 넘어서면 적치불가능
		 * 				3. 해당 위치의 상단이 크레인스케줄이 존재하는 지 판단
		 * 					3-1. 크레인 스케줄이 존재하면
		 * 						3-1-1. 권하인 지 판단
		 * 							3-1-1-1. 권하이면서 같은 스케줄코드이면 **** 적치가능 ****
		 * 							3-1-1-2. 권하이면서 다른 스케줄코드이고 우선순위가 빠른 스케줄인 지 판단
		 * 								3-1-1-2-1. 빠른 스케줄이면 **** 적치가능 ****
		 * 								3-1-1-2-2. 늦은 스케줄이면 적치불가능
		 * 						3-1-2. 권상이면 적치불가능
		 * 					3-2. 크레인 스케줄이 존재하지 않으면  **** 적치가능  ****
		 * 수정자 : 임춘수
		 * 수정일 : 
		 * 				1) 2009.11.16 - 최초 등록
		 * 
		 * 파라미터정의:	1) YD_STK_COL_GP	- 적치열
		 * 				2) YD_STK_BED_NO	- 적치베드
		 * 				3) YD_EQP_WRK_SH	- 작업총매수
		 * 				4) YD_EQP_WRK_WT	- 작업총중량
		 * 				5) YD_EQP_WRK_T		- 작업총두께
		 * 				6) YD_SCH_CD		- 스케줄코드
		 */
		if( ydStkLocVO == null ) ydStkLocVO = new YdStkLocVO();
		String szMethodName			= "procBedStackable";
		String szOperationName		= "BED적치가능유무판단";
		String szLogMsg				= null;
		String szRtnMsg 			= YdConstant.RETN_CD_SUCCESS;
		int	intYD_BED_ERR_CD		= 0;
		
		JDTORecord recTemp			= null;
		JDTORecordSet rsTemp		= null;
		
		String szYD_STK_COL_GP		= null;
		String szYD_STK_BED_NO		= null;
		String szYD_STK_LYR_NO		= null;
		String szSTL_NO				= null;
		String szYD_SCH_CD			= null;
		String szYD_STK_BED_ACT_STAT	= null;
		String szYD_STK_BED_WHIO_STAT	= null;
		String szYD_STK_LYR_MTL_STAT	= null;
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");				//적치열구분
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");				//적치베드번호
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");					//스케줄코드
		
		if( szYD_STK_COL_GP.equals("") ) {
			szLogMsg = "["+ szOperationName +"] 적치열구분이 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		
		if( szYD_STK_BED_NO.equals("") ) {
			szLogMsg = "["+ szOperationName +"] 적치베드번호가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	베드분석정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		//베드분석정보 조회
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 베드정보 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
		recTemp = JDTORecordFactory.getInstance().create();
		recTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
		recTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
		
		szRtnMsg = getYdStkBedAnalysis(recTemp, rsTemp, YdConstant.MTL_STAT_C_U_D);
		
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 베드정보 조회 완료 - 반환값 : " + szRtnMsg;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 베드정보 조회 시 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		//----------------------------------------------------------------------------------------------------------------------
		//----------------------------------------------------------------------------------------------------------------------
		//	베드상태와 기존 스케줄이 존재하는 지 체크
		//----------------------------------------------------------------------------------------------------------------------
		rsTemp.first();
		recTemp = rsTemp.getRecord();
		
		szSTL_NO = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
		szYD_STK_BED_ACT_STAT = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_ACT_STAT");
		szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT");
		szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT2"); //2013.05.27 수정 chobg
		
		if( !szYD_STK_BED_ACT_STAT.equals(YdConstant.YD_STK_BED_ACTIVE) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 활성상태["+szYD_STK_BED_ACT_STAT+"]가 적치가능상태가 아닙니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			//return YdConstant.RETN_BED_INACT;
		}
		
		if( !szYD_STK_BED_WHIO_STAT.equals("H") ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 입출고상태["+szYD_STK_BED_WHIO_STAT+"]가 입고가적베드상태가 아닙니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_BED_WHIO_NOT_IN;
		}
		
		if( szYD_STK_LYR_MTL_STAT.equals(YdConstant.YD_STK_LYR_MTL_STAT_UN_WAIT)) {				//권상대기이면 적치불가능
			szLogMsg = "["+ szOperationName +"] 적치된 재료["+szSTL_NO+"]보다 적치재료상태["+szYD_STK_LYR_MTL_STAT+"]가 권상대기이므로 적치불가능";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_BED_UN_WAIT;
		}else if( szYD_STK_LYR_MTL_STAT.equals(YdConstant.YD_STK_LYR_MTL_STAT_DN_WAIT)) {		//권하대기이면 스케줄코드비교
			
		}
		//----------------------------------------------------------------------------------------------------------------------
		//----------------------------------------------------------------------------------------------------------------------
		//	베드 적치가능유무 판단
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]에 적치가능유무 판단 시작";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		intYD_BED_ERR_CD = YdCommonUtils.chkBedStackable(recPara, recTemp);

		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]에 적치가능유무 판단 완료 - 반환값 : " + intYD_BED_ERR_CD;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	적치가능한 베드의 적치단을 1 증가 시킴 - 값이 없으면 001(1단)으로 설정
		//----------------------------------------------------------------------------------------------------------------------
		if( intYD_BED_ERR_CD == YdConstant.YD_BED_STACKABLE ) {
			
			szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");
			
			szLogMsg = "["+ szOperationName +"] 적치베드의 조회된 적치단["+szYD_STK_LYR_NO+"]에 1 증가시킴 - 값이 없으면 001(1단)으로 설정  ";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
			
			if( szYD_STK_LYR_NO.equals("") ) {							//값이 없으면
				szYD_STK_LYR_NO = "001";										//1단
			}else{														//값이 존재하면
				szYD_STK_LYR_NO = ydDaoUtils.stringPlusInt(szYD_STK_LYR_NO, 1);	//조회된 적치단 + 1
			}
			
			szLogMsg = "["+ szOperationName +"] 계산된 단["+szYD_STK_LYR_NO+"]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		}else{
			
			szYD_STK_LYR_NO = "000";
			
		}
		
		recTemp.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
		
		ydUtils.displayRecord(szOperationName, recTemp);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	JDTORecord를 VO객체로 변환
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 레코드를 VO객체로 변환 시작  ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		procRecord2StkLoc(recTemp, ydStkLocVO);
		
		szLogMsg = "["+ szOperationName +"] 레코드를 VO객체로 변환 완료  ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		//----------------------------------------------------------------------------------------------------------------------
		
		return YdConstant.RETN_CD_SUCCESS;
		
	}
	//------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param szSTL_NO
	 * @param szYD_SCH_CD
	 * @return
	 * @throws JDTOException
	 */
	public static String compareYdSchCdWithStlInStk(String szSTL_NO, String szYD_SCH_CD) throws JDTOException{
		String szRtnMsg 			= YdConstant.RETN_CD_SUCCESS;
		int intRtnVal				= -100;
		JDTORecord	inRec			= null;
		JDTORecordSet	outRecSet	= null;
		YdCrnWrkMtlDao	ydCrnWrkMtlDao	= new YdCrnWrkMtlDao();
		
		String szYD_SCH_CD_STL		= null;
		int intYD_SCH_PRIOR_STL		= -100;
		int intYD_SCH_PRIOR			= -100;
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	재료번호로 크레인작업재료로 들어있는 크레인스케줄의 우선순위가 빠른 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
		inRec = JDTORecordFactory.getInstance().create();
		inRec.setField("STL_NO", szSTL_NO);
		intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(inRec, outRecSet, 15);
		
		if( intRtnVal == 0 ) {
			return YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal < 0 ) {
			return YdConstant.RETN_CD_FAILURE;
		}
		outRecSet.first();
		inRec = outRecSet.getRecord();
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	스케줄코드비교, 우선순위 비교하여 반환
		//----------------------------------------------------------------------------------------------------------------------
		szYD_SCH_CD_STL = ydDaoUtils.paraRecChkNull(inRec, "YD_SCH_CD");
		intYD_SCH_PRIOR_STL = ydDaoUtils.paraRecChkNullInt(inRec, "YD_SCH_PRIOR");
		
		if( szYD_SCH_CD.equals(szYD_SCH_CD_STL) ) {
			//스케줄코드가 같으므로 적치가능
			szRtnMsg = YdConstant.RETN_SAME_SCH_CD;
		}else{
			//우선순위를 비교
			inRec = JDTORecordFactory.getInstance().create();
			YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, inRec);
			
			intYD_SCH_PRIOR = ydDaoUtils.paraRecChkNullInt(inRec, "YD_SCH_PRIOR");
			
			if( intYD_SCH_PRIOR_STL <= intYD_SCH_PRIOR ) {
				//현재 적치된 재료보다 우선순위가 늦으면 적치가능
				szRtnMsg = YdConstant.RETN_SCH_LATE_PRIOR;
			}else{
				szRtnMsg = YdConstant.RETN_SCH_EARLY_PRIOR;
			}
		}
		
		//----------------------------------------------------------------------------------------------------------------------
		
		return szRtnMsg;
	}
	
	
	/**
	 * 베드정보조회
	 * @param recPara
	 * @param rsOutPara
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdStkBed(JDTORecord recPara, JDTORecordSet rsOutPara, int intGp) throws JDTOException {
		String szMethodName			= "getYdStkBed";
		String szOperationName		= "베드정보조회";
		String szLogMsg				= null;
		String szRtnMsg 			= YdConstant.RETN_CD_SUCCESS;
		int intRtnVal				= -100;
		String szYD_STK_COL_GP		= null;
		String szYD_STK_BED_NO		= null;
		
		szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
		szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
		
		YdStkBedDao	ydStkBedDao		= new YdStkBedDao();
		
		intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsOutPara, intGp);
		
		if( intRtnVal == 0 ) {
			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 적치베드:"+szYD_STK_BED_NO+"] 정보가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal < 0 ) {
			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 적치베드:"+szYD_STK_BED_NO+"] 정보 조회 시 오류발생 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}else{
			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 적치베드:"+szYD_STK_BED_NO+"] 정보가 존재합니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	/**
	 * 베드분석정보조회
	 * @param recPara
	 * @param rsOutPara
	 * @param szQUERY_TYPE
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdStkBedAnalysis(JDTORecord recPara, JDTORecordSet rsOutPara, String szQUERY_TYPE) throws JDTOException {
		/*
		 * 업무기준	:	해당베드에 적치된 정보 분석 - 베드와 단을 OUTER JOIN : 베드가 존재하는 경우에는 하나의 레코드는 반환이 됨
		 * 				1) 베드의 MAX 매수
		 * 				2) 베드의 MAX 중량
		 * 				3) 베드의 야드적치Bed활성상태
		 * 				4) 베드의 야드적치Bed활성상태
		 * 				5) 베드의 적치된 총매수
		 * 				6) 베드의 적치된 총중량
		 * 				7) 베드의 산적LOT타입별 개수
		 * 				8) 베드의 산적LOT코드별 개수
		 * 				9) 
		 */
// 2024.09.?? szMethodName getYdStkBed -> getYdStkBedAnalysis 변경
//		String szMethodName			= "getYdStkBed";
		String szMethodName			= "getYdStkBedAnalysis";
		String szOperationName		= "베드분석정보조회";
		String szLogMsg				= null;
		String szRtnMsg 			= YdConstant.RETN_CD_SUCCESS;
		int intRtnVal				= -100;
		String szYD_STK_COL_GP		= null;
		String szYD_STK_BED_NO		= null;
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(recPara, "T");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "베드분석정보조회(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
//2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		YdStkBedDao	ydStkBedDao		= new YdStkBedDao();

		szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
		szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
		
		if( szQUERY_TYPE.equals(YdConstant.MTL_STAT_C_U_D)) {
			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 적치베드:"+szYD_STK_BED_NO+"]의 적치중,권상,권하대기인 재료를 조회 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsOutPara, 24);
			
			if( intRtnVal == 0 ) {
				szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 적치베드:"+szYD_STK_BED_NO+"]의 적치중,권상,권하대기인 재료를 조회 시 정보가 존재하지 않습니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if( intRtnVal < 0 ) {
				szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 적치베드:"+szYD_STK_BED_NO+"] 의 적치중,권상,권하대기인 재료를 조회 시 오류발생 - 반환값 : " + intRtnVal;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
				return YdConstant.RETN_CD_FAILURE;
			}else{
				szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 적치베드:"+szYD_STK_BED_NO+"] 의 적치중,권상,권하대기인 재료정보가 존재합니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			}
			
		}else{
			szLogMsg = "["+ szOperationName +"] 지원하지 않는 쿼리타입["+szQUERY_TYPE+"]입니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		}
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "베드분석정보조회(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		return szRtnMsg;
	}
	
	/**------------------------------------------------------------------------------------------------------------------------------------
	 * ------------------------------------------------------------------------------------------------------------------------------------
	 * 후판제품 TO위치 결정 모듈 시작
	 * ------------------------------------------------------------------------------------------------------------------------------------
	 * ------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	//------------------------------------------------------------------------------------------------------------------------------------
	//	RT상으로 위치 결정 - RT반납 시
	//------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * RT상TO위치결정(후판제품)
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @return
	 * @throws JDTOException
	 */
	public static String procRtToLocForPlateYd(JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recWbook, String logId) throws JDTOException {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// procRtToLocForPlateYd argument 에 logId 항목 추가 개선
// public static String procRtToLocForPlateYd(JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recWbook) throws JDTOException { 
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		String szMethodName				= "procRtToLocForPlateYd";
		String szOperationName			= "RT상TO위치결정(후판제품)";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		//int intRtnVal					= -100;
		
		ArrayList		listToLoc		= null;
		
		//YdCrnSchDao		ydCrnSchDao		= new YdCrnSchDao();
		JDTORecord	recLogMsg			= null;
		JDTORecord	recPara				= null;
		JDTORecord	recTemp				= null;
		JDTORecord	recTemp2			= null;
		JDTORecord	recUpCrnSch			= null;
		JDTORecordSet	rsResult		= null;
		
		String szSTL_NO					= null;
		String[] szYD_STK_LYR_MTL_STAT	= {"D", "U"};
		String szYD_UP_WO_LOC			= null;
		String szYD_UP_WO_LAYER			= null;
		String szYD_DN_WO_LOC			= null;
		String szYD_DN_WO_LAYER			= null;
		String szYD_UP_STK_COL_GP		= null;
		String szYD_UP_STK_BED_NO		= null;
		String szYD_DN_STK_COL_GP		= null;
		String szYD_DN_STK_BED_NO		= null;
		String szYD_MTL_L_GP			= null;
		String szYD_SCH_CD				= null;
		String szYD_CRN_SCH_ID			= null;
		String szYD_EQP_ID				= null;
		
		int intYD_EQP_WRK_SH			= 0;						//야드설비작업매수
		int intYD_EQP_WRK_WT			= 0;						//야드설비작업중량
		double dblYD_EQP_WRK_T			= 0;						//야드설비작업총두께
		String szYD_EQP_WRK_MAX_W		= null;						//작업재료 중 최대 폭
		String szYD_EQP_WRK_MAX_L		= null;						//작업재료 중 최대 길이
		double dbMAX_MTL_L				= 0;
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// logId Empty 이면 logId 신규 생성 개선
if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "RT상TO위치결정(후판제품)(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);

//2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 크레인스케줄확인 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		ydUtils.displayRecord(szOperationName, recCrnSch);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------
		
		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		
		szLogMsg = "["+ szOperationName +"] -------------------- 크레인작업재료의 최하단 재료정보 확인 --------------------";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		szLogMsg = "["+ szOperationName +"] -------------------- 크레인스케줄정보 확인 --------------------";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		ydUtils.displayRecord(szOperationName, recCrnSch);
		
		szYD_CRN_SCH_ID      	= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_CRN_SCH_ID");
		szYD_EQP_ID      		= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_EQP_ID");
		
		intYD_EQP_WRK_SH      	= ydDaoUtils.paraRecChkNullInt(recPara,"SH_CNT");
		intYD_EQP_WRK_WT      	= ydDaoUtils.paraRecChkNullInt(recPara,"SUM_MTL_WT");
		dblYD_EQP_WRK_T      	= ydDaoUtils.paraRecChkNullDouble(recPara,"SUM_MTL_T");
		
		szYD_EQP_WRK_MAX_W		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_W");
		szYD_EQP_WRK_MAX_L		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_L");
		dbMAX_MTL_L				= ydDaoUtils.paraRecChkNullDouble(recPara, "MAX_MTL_L");
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 총매수 :" + intYD_EQP_WRK_SH;
		szLogMsg += ", 총중량 :" + intYD_EQP_WRK_WT;
		szLogMsg += ", 총높이  :" + dblYD_EQP_WRK_T;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	최하단 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		szSTL_NO	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		szRtnMsg = DaoManager.getYdStock(recPara, rsResult, 0);
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			return szRtnMsg;
		}
		
		rsResult.first();
		recTemp = rsResult.getRecord();
		
		szYD_MTL_L_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 완료 - 길이구분["+szYD_MTL_L_GP+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	권상중이거나 권하중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		szRtnMsg = YdCommonUtils.getYdStkLyrWithMtlStat(recPara, rsResult, szYD_STK_LYR_MTL_STAT);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			return szRtnMsg;
		}
		
		rsResult.first();
		recTemp = rsResult.getRecord();
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 완료  ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szYD_UP_STK_COL_GP = recTemp.getFieldString("YD_STK_COL_GP");
		szYD_UP_STK_BED_NO = recTemp.getFieldString("YD_STK_BED_NO");
		szYD_UP_WO_LOC = szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
		szYD_UP_WO_LAYER = recTemp.getFieldString("YD_STK_LYR_NO");
		
		szLogMsg = "["+ szOperationName +"] 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
    	//----------------------------------------------------------------------------------------------------------------------
    	//	권상지시위치의 정보를 분석해서 RT상의 위치 구하기
    	//----------------------------------------------------------------------------------------------------------------------
    	
    	szLogMsg = "["+ szOperationName +"] 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]를 사용하여 RT상의 베드번호 추출 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
    	szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD");
    	
    	if("TCRTUTUM".equals(szYD_SCH_CD)){
	    	String szYD_TO_LOC_GUIDE		= ydDaoUtils.paraRecChkNull(recWbook, "YD_TO_LOC_GUIDE");
			if( szYD_TO_LOC_GUIDE.length() == 8 ) {
				szYD_DN_STK_COL_GP		= szYD_TO_LOC_GUIDE.substring(0, 6);
				szYD_DN_STK_BED_NO		= szYD_TO_LOC_GUIDE.substring(6);
			}
    	}else{
	    	szYD_DN_STK_COL_GP	= szYD_SCH_CD.substring(0, 6);
	    	szYD_DN_STK_BED_NO 	= getRtStkLocByBedNoMtlLGp(szYD_UP_STK_COL_GP, szYD_UP_STK_BED_NO, szYD_MTL_L_GP, dbMAX_MTL_L, szYD_DN_STK_COL_GP); //통합스케줄적용
	    	
	    	szLogMsg = "["+ szOperationName +"] 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]를 사용하여 RT상의 베드번호 추출 완료 - 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    	}
    	//----------------------------------------------------------------------------------------------------------------------
    	
    	
    	//----------------------------------------------------------------------------------------------------------------------
    	//	해당베드정보에 적치가능한 지를 체크
    	//YD_TO_LOC_GUIDE	- 사용자지정위치(적치열+적치베드)
		// * 				2) YD_EQP_WRK_SH	- 작업총매수
		// * 				3) YD_EQP_WRK_WT	- 작업총중량
		// * 				4) YD_EQP_WRK_T		- 작업총두께
		// * 				5) YD_SCH_CD		- 스케줄코드
    	//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]에 적치가능한 지 비교 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
    	listToLoc = new ArrayList();
    	
    	recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO);
    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH));
    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT));
    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T));
    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// procAsgnedBedStackable call 시  recTemp 에 logId SET 추가 개선
    	recTemp.setField("LOG_ID", logId);
//2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
    	szRtnMsg = YdToLocDcsnUtil.procAsgnedBedStackable(recTemp, listToLoc, szMethodName);
    	
    	int intYD_BED_ERR_CD		= 0;
    	
    	if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {

    		YdStkLocVO ydStkLocVO = (YdStkLocVO)listToLoc.get(0);
    		
    		intYD_BED_ERR_CD		= ydStkLocVO.getYdBedErrCd();
    		
    		if( intYD_BED_ERR_CD == YdConstant.YD_BED_STACKABLE ) {
    		
	    		szYD_DN_WO_LOC 		= ydStkLocVO.getYdStkColGp() + ydStkLocVO.getYdStkBedNo();
	    		szYD_DN_WO_LAYER	= ydStkLocVO.getYdStkLyrNo();
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
	    		//	적치단의 권하지시위치에 재료별 권하상태로 수정
	    		//----------------------------------------------------------------------------------------------------------------------
	    		recTemp = JDTORecordFactory.getInstance().create();
	    		String szYD_DN_WO_LAYER2		= "";
	    		for(int i = 1; i <= rsCrnwrkmtl.size(); i++ ) {
	    			rsCrnwrkmtl.absolute(i);
	    			recTemp2 = rsCrnwrkmtl.getRecord();
	    			
	    			szSTL_NO = ydDaoUtils.paraRecChkNull(recTemp2, "STL_NO");
	    			szYD_DN_WO_LAYER2 = ydDaoUtils.stringPlusInt(szYD_DN_WO_LAYER, i - 1);
	    			
	    	    	recTemp.setField("STL_NO", 					szSTL_NO);
	    	    	recTemp.setField("YD_STK_COL_GP", 			ydStkLocVO.getYdStkColGp());
	    	    	recTemp.setField("YD_STK_BED_NO", 			ydStkLocVO.getYdStkBedNo());
	    	    	recTemp.setField("YD_STK_LYR_NO", 			szYD_DN_WO_LAYER2);
	    	    	recTemp.setField("YD_STK_LYR_MTL_STAT", 	YdConstant.YD_STK_LYR_MTL_STAT_DN_WAIT);
	    	    	
	    	    	szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER2+"]에 재료["+szSTL_NO+"] 등록 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//	        		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	        		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    	    	
	    	    	String szRtnMsg2 = DaoManager.updYdStklyr(recTemp, 0);
	    	    	
	    	    	szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER2+"]에 재료["+szSTL_NO+"] 등록 완료 - 메세지 : " + szRtnMsg2;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//	        		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	        		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    	    	
	    		}
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
	    		
	    		szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		
	    		recUpCrnSch = JDTORecordFactory.getInstance().create();
	    		recUpCrnSch.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);
	    		recUpCrnSch.setField("YD_EQP_ID", 				szYD_EQP_ID);
	    		
	    		recUpCrnSch.setField("YD_UP_STK_COL_GP", 		szYD_UP_STK_COL_GP);
				recUpCrnSch.setField("YD_UP_STK_BED_NO", 		szYD_UP_STK_BED_NO);
				recUpCrnSch.setField("YD_DN_STK_COL_GP", 		szYD_DN_STK_COL_GP);
				recUpCrnSch.setField("YD_DN_STK_BED_NO", 		szYD_DN_STK_BED_NO);
				
				//----------------------------------------------------------------------------------------------------------------------
				//	TO위치결정방법이 T인 경우에는 크레인스케줄의 권상지시위치 업데이트
				//----------------------------------------------------------------------------------------------------------------------
				
	    		if(recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD").equals("T") ) {
	    			szLogMsg = "["+ szOperationName +"] TO위치결정방법이 T인 경우에는 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"] 크레인스케줄에 등록";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//	        		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	        		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    			
	    			recUpCrnSch.setField("YD_UP_WO_LOC", 		szYD_UP_WO_LOC);
	    			recUpCrnSch.setField("YD_UP_WO_LAYER", 		szYD_UP_WO_LAYER);
	    		}
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
	    		
	    		recUpCrnSch.setField("YD_DN_WO_LOC", 			szYD_DN_WO_LOC);
	    		recUpCrnSch.setField("YD_DN_WO_LAYER", 			szYD_DN_WO_LAYER);
	    		
	    		recUpCrnSch.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));
	    		recUpCrnSch.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));
	    		recUpCrnSch.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));
	    		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);
	    		recUpCrnSch.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);
	    		
	    		recUpCrnSch.setField("MODIFIER", 				szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
	    		
	    		szRtnMsg = CrnSchUtil.uptCrnSchXYCord(recUpCrnSch);
	        	
	        	szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 완료";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    		}else{
    			//--------------------------------------------------------------------------------------------
    			//	해당베드정보에 적치가능한 지를 체크 후 적치불가능이면 로그메세지 전송
    			//--------------------------------------------------------------------------------------------
    			
    			recLogMsg = JDTORecordFactory.getInstance().create();
    			recLogMsg.setField("JMS_TC_CD", 				YdConstant.YDYDJ702);
    			recLogMsg.setField("YD_GP", 					szYD_EQP_ID.substring(0, 1));
    			recLogMsg.setField("MONITORING_CHANNEL", 		YdConstant.YD_MONITORING_CHANNEL_K);
    			recLogMsg.setField("YD_BAY_GP", 				szYD_EQP_ID.substring(1, 2));
    			recLogMsg.setField("YD_EQP_ID", 				szYD_EQP_ID);
    			recLogMsg.setField("YD_SCH_CD", 				szYD_SCH_CD);
    			recLogMsg.setField("YD_EVT_GP", 				YdConstant.YD_EVT_CRANE);
    			recLogMsg.setField("YD_MSG_OUTPWR_GRD", 		"E");
    			recLogMsg.setField("YD_PGM_TP", 				YdConstant.YD_PGMGP_SCH);
    			recLogMsg.setField("YD_IF_CD", 					"");
    			recLogMsg.setField("YD_E_J_B_ID", 				szClassName);
    			recLogMsg.setField("YD_MSG_NM", 				szMethodName);
    			
    			YdCommonUtils.sndLogMsgForBedStatusInfo(intYD_BED_ERR_CD, szYD_CRN_SCH_ID, szYD_DN_STK_COL_GP, szYD_DN_STK_BED_NO, recLogMsg);
    			
    			//--------------------------------------------------------------------------------------------
    			
    			szLogMsg = "["+ szOperationName +"] 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]에 적치불가능 - 베드에러코드["+intYD_BED_ERR_CD+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//    			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
    			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
    		}
    	}else{
    		
    		//--------------------------------------------------------------------------------------------
			//	해당베드정보에 적치가능한 지를 체크 후 적치불가능이면 로그메세지 전송
			//--------------------------------------------------------------------------------------------
			
    		recLogMsg = JDTORecordFactory.getInstance().create();
    		recLogMsg.setField("JMS_TC_CD", 				YdConstant.YDYDJ702);
    		recLogMsg.setField("YD_GP", 					szYD_EQP_ID.substring(0, 1));
    		recLogMsg.setField("MONITORING_CHANNEL", 		YdConstant.YD_MONITORING_CHANNEL_K);
    		recLogMsg.setField("YD_BAY_GP", 				szYD_EQP_ID.substring(1, 2));
    		recLogMsg.setField("YD_EQP_ID", 				szYD_EQP_ID);
    		recLogMsg.setField("YD_SCH_CD", 				szYD_SCH_CD);
    		recLogMsg.setField("YD_EVT_GP", 				YdConstant.YD_EVT_CRANE);
    		recLogMsg.setField("YD_MSG_OUTPWR_GRD", 		"E");
    		recLogMsg.setField("YD_PGM_TP", 				YdConstant.YD_PGMGP_SCH);
    		recLogMsg.setField("YD_IF_CD", 					"");
    		recLogMsg.setField("YD_E_J_B_ID", 				szClassName);
    		recLogMsg.setField("YD_MSG_NM", 				szMethodName);
			
			YdCommonUtils.sndLogMsgForBedInfo(szRtnMsg, szYD_CRN_SCH_ID, szYD_DN_STK_COL_GP, szYD_DN_STK_BED_NO, recLogMsg);
			
			//--------------------------------------------------------------------------------------------
    		
    		
    		szLogMsg = "["+ szOperationName +"] 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]에 적치불가능 - 에러메세지["+szRtnMsg+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
    	}
    	
    	szLogMsg = "["+ szOperationName +"] 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]에 적치가능한 지 비교 완료";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    	
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "RT상TO위치결정(후판제품)(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		//return YdConstant.RETN_CD_SUCCESS;
		return szRtnMsg;
	} // end of procRtToLocForPlateYd
	
	/**
	 * 주작업TO위치결정(후판제품)
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String procMainWrkToLocForPlateYd(
			JDTORecord msgRecord					/* 전문 */
			, JDTORecordSet rsCrnwrkmtl				/* 크레인작업재료 */
			, JDTORecord recCrnSch					/* 크레인스케줄정보 */
			, JDTORecord recWbook					/* 작업예약정보 */
			) throws JDTOException {
		/*
		 * 업무기준	:	1. 
		 * 
		 * 수정자	: 윤재광.
		 * 수정일	:
		 * 				1. 2009.11.30 - 최초등록
		 * 				2. 2010.06.21 - 수정작업
		 */
		String szMethodName				= "procMainWrkToLocForPlateYd";
		String szOperationName			= "주작업TO위치결정(후판제품)";
		String szDesc					= "";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		ArrayList		listToLoc		= null;
		YdStkLocVO		ydStkLocVO		= null;
		
		JDTORecordSet	rsResult		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		JDTORecord		recLogMsg		= null;
		
		String szYD_UP_WO_LOC			= null;
		String szYD_UP_WO_LAYER			= null;
		String szYD_DN_WO_LOC			= null;
		String szYD_DN_WO_LAYER			= null;
		String szYD_UP_STK_COL_GP		= null;
		String szYD_UP_STK_BED_NO		= null;
		String szYD_DN_STK_COL_GP		= null;
		String szYD_DN_STK_BED_NO		= null;
		String szYD_SCH_CD				= null;
		
		boolean bUP_UPDT_NEEDED			= false;
		
		String	szSTL_NO				= null;
		String[] szYD_STK_LYR_MTL_STAT	= {"D", "U"};
		
		String	szYD_TO_LOC_GUIDE		= null;
		String	szYD_TO_LOC_GUIDEbak		= null;  //입력받은 시점의 to위치가이드 백업용
		
		String szYD_CRN_SCH_ID			= null;
		String szYD_EQP_ID				= null;
		
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_GP				= null;
		
		String szYD_MTL_W_GP			= null;						//야드재료폭구분
		String szYD_MTL_T_GP			= null;						//야드재료두께구분
		String szYD_MTL_L_GP			= null;						//야드재료길이구분
		
		String szYD_PILING_CD			= null;						//야드Piling코드
		
		int intYD_EQP_WRK_SH			= 0;						//야드설비작업매수
		int intYD_EQP_WRK_WT			= 0;						//야드설비작업중량
		double dblYD_EQP_WRK_T			= 0;						//야드설비작업총두께
		String szYD_EQP_WRK_MAX_W		= null;						//작업재료 중 최대 폭
		String szYD_EQP_WRK_MAX_L		= null;						//작업재료 중 최대 길이
		String szSEARCH_CHANGE          = "N";                      //대형고객사 입고 시 검색순서 변경 : P-C,혼적,공BED -> P-C,공BED,혼적
//SJH04004
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();	
		YdEqpDao	ydEqpDao 	= new YdEqpDao();
		YdStockDao		ydStockDao		= new YdStockDao();
		YdStkBedDao ydStkbedDao = new YdStkBedDao();
		
		JDTORecordSet outResult1 	= null;		
		JDTORecord inRecord3 		= null;		
		JDTORecord inRecord2 		= null;		
		String szFROM_DONG			= "";		
		String szTO_DONG  			= "";		
		String szYD_CRN_GRAB_TP     = "";
		String szCHECK_FLAG         = "N";
		int intRtnVal               = 0; 
		String szYD_RCPT_PLN_STR_LOC = "";
		boolean bIsToLocStackable				= false;
		// AT000 물류시스템 개선 2022.10.27
		YdPlateCommDAO commDao = new YdPlateCommDAO(); 
		String sCrnWrkMode2             = "";
		double dblYD_EQP_WRK_W		  = 0;				      //작업재료 중 최대 폭
		String szYD_STK_BED_W_GP_G      ="";
		
		String sFNL_CHK ="N";  //크레인 고장범위 속하는지 여부 체크 추가 변수
		String sAL_FROM ="";  //고장범위 속한다면 대체범위 FROM
		String sAL_TO ="";    //고장범위 속한다면 대체범위 TO
		
		String szYd_SCH_ST_GP = "";

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(msgRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "주작업TO위치결정(후판제품)(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
				
		//----------------------------------------------------------------------------------------------------------------------
		//------------------------------------- 사용자정의위치(입고예정위치)에 대한 TO위치결정 -------------------------------------
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------
		
		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		
		szYD_SCH_CD	= ydDaoUtils.paraRecChkNull(recWbook, "YD_SCH_CD");
		
		if( szYD_SCH_CD.substring(2, 4).equals("PT") ) {
			szDesc			= "차량";
		}else if( szYD_SCH_CD.substring(2, 4).equals("TR") ) {
			szDesc			= "차량";
		}else if( szYD_SCH_CD.substring(2, 4).equals("RT") ) {
			szDesc			= "RollerTable";
		}else if( szYD_SCH_CD.substring(2, 4).equals("TF") ) {
			szDesc			= "Transfer";
		}else if( szYD_SCH_CD.substring(2, 4).equals("YD") ) {
			szDesc			= "야드";
		}else if( szYD_SCH_CD.substring(2, 4).equals("SL") ) {
			szDesc			= "선별";			
		}
		
		if( szYD_SCH_CD.substring(6, 7).equals("L")) {
			szDesc		+= "입고";
		}else if( szYD_SCH_CD.substring(6, 7).equals("U")) {
			szDesc		+= "출고";
		}else if( szYD_SCH_CD.substring(6, 7).equals("M")) {
			szDesc		+= "이적";
		}
		
		if( !szDesc.equals("") ) szOperationName		+= "-" + szDesc;
		
		szYD_CRN_SCH_ID      	= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_CRN_SCH_ID");					//크레인스케줄ID
		szYD_EQP_ID      		= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_EQP_ID");						//크레인설비ID
		
		intYD_EQP_WRK_SH      	= ydDaoUtils.paraRecChkNullInt(recPara,"SH_CNT");						//크레인작업재료 총매수
		intYD_EQP_WRK_WT      	= ydDaoUtils.paraRecChkNullInt(recPara,"SUM_MTL_WT");					//크레인작업재료 총중량
		dblYD_EQP_WRK_T      	= ydDaoUtils.paraRecChkNullDouble(recPara,"SUM_MTL_T");					//크레인작업재료 총높이
		
		szYD_EQP_WRK_MAX_W		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_W");						//크레인작업재료 중 최대 폭
		szYD_EQP_WRK_MAX_L		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_L");						//크레인작업재료 중 최대 길이
		dblYD_EQP_WRK_W         = ydDaoUtils.paraRecChkNullDouble(recPara,"MAX_MTL_W");                 //AT000 물류시스템 개선 2022.10.27 최대폭 
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 총매수 :" + intYD_EQP_WRK_SH;
		szLogMsg += ", 총중량 :" + intYD_EQP_WRK_WT;
		szLogMsg += ", 총높이  :" + dblYD_EQP_WRK_T;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szYD_TO_LOC_GUIDE		= ydDaoUtils.paraRecChkNull(recWbook, "YD_TO_LOC_GUIDE");
		szYd_SCH_ST_GP			= ydDaoUtils.paraRecChkNull(recWbook, "YD_SCH_ST_GP");
		
		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
			szYD_DN_STK_COL_GP		= szYD_TO_LOC_GUIDE.substring(0, 6);
			szYD_DN_STK_BED_NO		= szYD_TO_LOC_GUIDE.substring(6);
		}
		
		szYD_TO_LOC_GUIDEbak=szYD_TO_LOC_GUIDE;
		
		szLogMsg = "["+ szOperationName +"] ---------------------- 야드To위치Guide : " + szYD_TO_LOC_GUIDE + " --------------------------";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	최하단 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------

		szSTL_NO	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		szRtnMsg = DaoManager.getYdStock(recPara, rsResult, 0);
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			return szRtnMsg;
		}
		
		rsResult.first();
		recTemp = rsResult.getRecord();
		
		szYD_MTL_L_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");			//크레인작업 최하단재료의 길이구분
		szYD_MTL_W_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_W_GP");			//크레인작업 최하단재료의 폭구분
		szYD_PILING_CD 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_PILING_CD");			//크레인작업 최하단재료의 Piling코드

		szYD_RCPT_PLN_STR_LOC 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_RCPT_PLN_STR_LOC");//입고예정위치
		
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 완료 - 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"], Piling코드["+szYD_PILING_CD+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				
		//----------------------------------------------------------------------------------------------------------------------
		//	권하중이거나 권상중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYD_UP_WO_LOC 		= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");		
		szYD_UP_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER");
		
		if( szYD_UP_WO_LOC.equals("") ) {
			
			szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			szRtnMsg = YdCommonUtils.getYdStkLyrWithMtlStat(recPara, rsResult, szYD_STK_LYR_MTL_STAT);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				return szRtnMsg;
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 완료  ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YD_STK_COL_GP");
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YD_STK_BED_NO");
			szYD_UP_WO_LOC 			= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYD_UP_WO_LAYER 		= recTemp.getFieldString("YD_STK_LYR_NO");
			
			bUP_UPDT_NEEDED			= true;
			
			szLogMsg = "["+ szOperationName +"] 조회된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		}else{
			szYD_UP_STK_COL_GP = szYD_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYD_UP_WO_LOC.substring(6);
			
			szLogMsg = "["+ szOperationName +"] 크레인스케줄에 등록된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		
		YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();	
		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "016");//후판 개발 적용여부
		
		szYD_EQP_GP		= szYD_UP_WO_LOC.substring(2, 4);
		//신입고로직 사용시 신규 to위치 모듈 호출
		if( !(szYD_EQP_GP.equals("PT") ||
	    		szYD_EQP_GP.equals("TR") ||
	    		szYD_EQP_GP.equals("SL")
	    		)){
			
			if("Y".equals(sApplyYnPI)){
				szLogMsg = "신이적/입고로직 사용시, 주작업TO위치결정(후판제품)(" + szMethodName + ") 신규모듈 호출";
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
				
				return procMainWrkToLocForPlateYd2nd(msgRecord,  rsCrnwrkmtl, recCrnSch	, recWbook	);
			}
		
		}
		
		
		//----------------------------------------------------------------------------------------------------------------------
    	//	해당베드정보에 적치가능한 지를 체크
    	//YD_TO_LOC_GUIDE	- 사용자지정위치(적치열+적치베드)
		// * 				2) YD_EQP_WRK_SH	- 작업총매수
		// * 				3) YD_EQP_WRK_WT	- 작업총중량
		// * 				4) YD_EQP_WRK_T		- 작업총두께
		// * 				5) YD_SCH_CD		- 스케줄코드
    	//----------------------------------------------------------------------------------------------------------------------
		 
		JDTORecordSet 	rsDanPok   = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord 		recDanPok  = JDTORecordFactory.getInstance().create();
		
		/* 
		 * 2014.07.18 윤재광 긴급요청사항
		 * 2015.06.06 권영환 요청사항으로 막음
		 * 2016.07.01 오프라인 평탄도계 불량재 처리기능으로 대체
		 * 2017.04.27 노형준 사원 요청으로 평탄도계 불량재 체크로직 막음           
		 * 
		 * 제목: 폭 1500이하 운영방안 건.

		   	조건 1 : 1후판 입고 및 이적이고,
			
			조건2 : 제품폭 <=1500이고, 고객사 코드 = / 현대3사(A11119, A14469, A14478)이고,
					 - 제품길이  6800이하면:  E042301.02.03.04   
					                       E042501.02.03.04
					 - 제품길이  9200이하면:  D042601.03
					 - 제품길이 14000이하면:  C042601.03 로 위치를 고정하여 사용
		 
		
		boolean isDanPok = false;
		
		recDanPok.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
		
		szRtnMsg = DaoManager.getToLocWithDanPok(recDanPok, rsDanPok, 1);
		
		szLogMsg = "["+ szOperationName +"] 오프라인평탄도불량 운영방안["+szRtnMsg+"]";
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
    		
			rsDanPok.first();
    		
			recDanPok			= rsDanPok.getRecord();
    		
    		String sDanPok = ydDaoUtils.paraRecChkNull(recDanPok, "IS_DANPOK");
    		
    		if("Y".equals(sDanPok)){ 
    			isDanPok = true; 
    		}else{
    			isDanPok = false; 
    		}
    		szLogMsg = "["+ szOperationName +"] 오프라인평탄도불량 운영방안["+isDanPok+"]";
    	}
		*/
		
		listToLoc = new ArrayList();
		
		boolean bIS_BED_STACKABLE	= false;
		String szCflag     = "";          //AT000 물류시스템 개선 2022.11.17
		
		//24.08.28 REQ202408611589 허동수 책임 요청 후판 제품장 입고시 후순위적치율 집계 및 화면 구축
				//"P" : 파일링코드에 의해 
				//"S" : 혼적베드
				//"G" : 동일사이즈 일반베드
				//"E" : 공베드
				//"T"  : 1후판 가적베드
				//"I" : 단일파일링
				//"A" : 기타 
				//"X" : 위치 못찾음
		String plnLocDcsnGp  = "X";  //예정위치결정구분  (파일링코드에 의해, 동일사이즈 베드, 공베드, 위치 못찾음) 
		
		/*
		 * 2014.03.25 윤재광
		 * 2후판 제품창고 사내절단장 북아웃 요구시 디폴트 저장위치로 TO위치 결정
		 */
		if("TB010101".equals(szYD_TO_LOC_GUIDE)||
		   "TB033101".equals(szYD_TO_LOC_GUIDE)||
		   "TB032801".equals(szYD_TO_LOC_GUIDE)  //TB033101->TB032801로 변경 요청. 2022.11.30  후판품질팀 서승범 책임. 1673
		   ){
			
			szYD_DN_WO_LOC		= szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;
			szYD_DN_WO_LAYER 	= "001";
		/*
		}else if(isDanPok){
			
			rsDanPok 	= JDTORecordFactory.getInstance().createRecordSet("");
			recDanPok 	= JDTORecordFactory.getInstance().create();
			recDanPok.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);

	    	szRtnMsg = DaoManager.getToLocWithDanPok(recDanPok, rsDanPok, 3);
	    	
	    	if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    		
	    		rsDanPok.first();
	    		
	    		recDanPok			= rsDanPok.getRecord();
	    		
	    		//스케쥴 대상재 여부 체크
	    		if("0".equals(ydDaoUtils.paraRecChkNull(recDanPok, "SCH_CNT"))){
	    		
		    		szYD_DN_STK_COL_GP	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_COL_GP"); 
		    		szYD_DN_STK_BED_NO	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_BED_NO"); 
		    		szYD_DN_WO_LOC	 	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_BED_NO");
		    		szYD_DN_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_LYR_NO");
		    		
	    		}else{
	    			return YdConstant.RETN_NOT_EXIST_BED;
	    		}
	    		
	    	}else{
	    		return YdConstant.RETN_NOT_EXIST_BED;
	    	}
	    */	
	    }else if("TCRTUT45".equals(szYD_TO_LOC_GUIDE)||
				 "TCRTUT13".equals(szYD_TO_LOC_GUIDE)||
				 "TC010101".equals(szYD_TO_LOC_GUIDE)||
				 "TC010103".equals(szYD_TO_LOC_GUIDE)|| // 2020.10.14 (김도훈 매니저 요청, TESTPLATE To위치 지정 베드)
				 "TC010201".equals(szYD_TO_LOC_GUIDE)||
				 "TC010301".equals(szYD_TO_LOC_GUIDE)||
				 "TC010303".equals(szYD_TO_LOC_GUIDE)||
				 ("TCRTRA30".equals(szYD_TO_LOC_GUIDE)&&!szYD_RCPT_PLN_STR_LOC.startsWith("TC"))||
				 ("TCRTRA40".equals(szYD_TO_LOC_GUIDE)&&!szYD_RCPT_PLN_STR_LOC.startsWith("TC"))||
				 ("TCRTRB30".equals(szYD_TO_LOC_GUIDE)&&!szYD_RCPT_PLN_STR_LOC.startsWith("TC"))||
				 ("TCRTRB40".equals(szYD_TO_LOC_GUIDE)&&!szYD_RCPT_PLN_STR_LOC.startsWith("TC"))){
			  
			rsDanPok 	= JDTORecordFactory.getInstance().createRecordSet("");
			recDanPok 	= JDTORecordFactory.getInstance().create();
			recDanPok.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
			recDanPok.setField("TO_LOC", szYD_TO_LOC_GUIDE);
			
			String sGbn = "";
			if("TCRTUT45".equals(szYD_TO_LOC_GUIDE)){
				sGbn = "RA";
			}else{
				sGbn = "UT";
			}
			recDanPok.setField("GBN", sGbn);

	    	szRtnMsg = DaoManager.getToLocWithDanPok(recDanPok, rsDanPok, 2);
	    	
	    	if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    		
	    		rsDanPok.first();
	    		
	    		recDanPok			= rsDanPok.getRecord();
	    		
	    		szYD_DN_STK_COL_GP	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_COL_GP"); 
	    		szYD_DN_STK_BED_NO	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_BED_NO"); 
	    		szYD_DN_WO_LOC	 	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_BED_NO");
	    		szYD_DN_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_LYR_NO");
	    		
	    	}else{
	    		return YdConstant.RETN_NOT_EXIST_BED;
	    	}
	    	
		}else{
			
			// 입고시 예정위치정보는 SKIP
			if( szYD_TO_LOC_GUIDE.length() == 8 && 
			   !szYD_SCH_CD.substring(6, 7).equals("L")) { 
				
				szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]과 권상지시적치열["+szYD_UP_STK_COL_GP+"]의 동이 같은 지 비교 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				
				//----------------------------------------------------------------------------------------------------------------------
				//	TO위치가이드와 권상지시위치의 동이 다른 경우에는 TO위치가이드가 적치가능한 지를 체크하는 부분을 SKIP한다.
				//----------------------------------------------------------------------------------------------------------------------
				boolean isSameBay	= false;
				
				if( szYD_TO_LOC_GUIDE.substring(1, 2).equals(szYD_UP_STK_COL_GP.substring(1, 2)) )	{
					
					isSameBay		= true;
					
					szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]과 권상지시적치열["+szYD_UP_STK_COL_GP+"]의 동이 같은 지 비교 완료 - 동이 같음";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					
				}else{
					
					szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]과 권상지시적치열["+szYD_UP_STK_COL_GP+"]의 동이 같은 지 비교 완료 - 동이 다름";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				}
				
				//----------------------------------------------------------------------------------------------------------------------
				bIsToLocStackable				= false;
				
				if( isSameBay ) {
					
					//----------------------------------------------------------------------------------------------------------------------
					//	일반야드인 경우에는 입고, 이적인 경우 최하단 크레인작업재료와 TO위치가이드위치의 Bed Size가 다르면 제외
					//	베드의 야드적치Bed입출고상태가 E(입출고가능)가 아니면 베드 제외
					//	수정일 : 2010.03.10 - 임춘수
					//----------------------------------------------------------------------------------------------------------------------
					
					if( szYD_TO_LOC_GUIDE.substring(2, 4).matches("\\d\\d") ) {	//일반야드는 숫자이므로 설비는 숫자가 아님
						
						//일반야드인 경우에는 입고, 이적인 경우 최하단 크레인작업재료와 TO위치가이드위치의 Bed Size가 다르면 제외
						//베드의 야드적치Bed입출고상태가 E(입출고가능)가 아니면 베드 제외
						
						szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]가 적치가능한 size[폭,길이]와 야드적치Bed입출고상태를 체크하기 위해 조회 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						
						rsResult = JDTORecordFactory.getInstance().createRecordSet("");
						recTemp = JDTORecordFactory.getInstance().create();
				    	recTemp.setField("YD_STK_COL_GP", 	szYD_TO_LOC_GUIDE.substring(0, 6));
				    	recTemp.setField("YD_STK_BED_NO", 	szYD_TO_LOC_GUIDE.substring(6));
				    	
				    	szRtnMsg			= DaoManager.getYdStkbed(recTemp, rsResult, 0);
				    	
				    	if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				    		rsResult.first();
				    		
				    		recTemp			= rsResult.getRecord();
				    		
				    		String szYD_STK_BED_L_GP			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP");
				    		String szYD_STK_BED_W_GP			= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP");
				    		szYD_STK_BED_W_GP_G                 = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP"); //AT000 최종 목적 bed 폭구분 
				    		String szYD_STK_BED_WHIO_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT");
				    		
				    		if( szYD_STK_BED_WHIO_STAT.equals("E") ) {
				    			
				    			szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]의 야드적치Bed입출고상태가 입출고가능하므로 적치가능하므로 재료와 베드의 size비교 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//								ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
								ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
								
								/*25.02.17 임진후기사 요청 TO위치 지정시, 재료의 폭 구분이 권하위치의 폭구분과 다르다 할지라도 권하 되게끔 요청
								 * szYD_PILING_CD : 크레인 최하단재료의 파일링코드. 
								 * 
								 * 
								 * 소폭 -> 중폭,광폭 가능
								 * 중폭 -> 중폭,광폭 가능
								 * 광폭 -> 광폭 가능
								 * 
								 * */
								
								sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "011");//후판 개발 적용여부
								
								if( szYD_STK_BED_L_GP.length() == 1 && 
									szYD_STK_BED_W_GP.length() == 1 ) {
									
					    			if( szYD_PILING_CD.substring(4, 5).equals(szYD_STK_BED_W_GP) && 
					    				szYD_PILING_CD.substring(6, 7).equals(szYD_STK_BED_L_GP) ) {
					    				szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]에 폭구분["+szYD_STK_BED_W_GP+"], 길이구분["+szYD_STK_BED_L_GP+"]과 최하단 크레인작업재료의 SIZE["+szYD_PILING_CD+"]가 동일하므로 적치가능";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//										ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
										ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
										
										bIsToLocStackable			= true;
					    			}else{
					    				/*25.02.17 임진후기사 요청 TO위치 지정시, 재료의 폭 구분이 권하위치의 폭구분과 다르다 할지라도 권하 되게끔 요청 */
					    				if("Y".equals(sApplyYnPI)){
					    					String stlWgp = szYD_PILING_CD.substring(4, 5);
					    					if("S".equals(stlWgp) && ("S".equals(szYD_STK_BED_W_GP) || "M".equals(szYD_STK_BED_W_GP) ||("L".equals(szYD_STK_BED_W_GP) ) )){
					    						szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]에 폭구분["+szYD_STK_BED_W_GP+"], 길이구분["+szYD_STK_BED_L_GP+"]과 최하단 크레인작업재료의 SIZE["+szYD_PILING_CD+"]의 폭구분 ["+stlWgp+"] 다르지만, 소폭->소/중/광폭 허용으로 작업가능";
												ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
												
												bIsToLocStackable			= true;
					    					}
					    					else if("M".equals(stlWgp) && ("M".equals(szYD_STK_BED_W_GP) ||("L".equals(szYD_STK_BED_W_GP) ) )){
					    						szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]에 폭구분["+szYD_STK_BED_W_GP+"], 길이구분["+szYD_STK_BED_L_GP+"]과 최하단 크레인작업재료의 SIZE["+szYD_PILING_CD+"]의 폭구분 ["+stlWgp+"] 다르지만, 중폭->중/광폭 허용으로 작업가능";
												ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
												
												bIsToLocStackable			= true;
					    					}					    					
					    					
					    						
					    				}
					    				else {
					    					szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]에 폭구분["+szYD_STK_BED_W_GP+"], 길이구분["+szYD_STK_BED_L_GP+"]과 최하단 크레인작업재료의 SIZE["+szYD_PILING_CD+"]가 동일하지 않으므로 적치불가능";
											ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					    				}
					    			}
					    			
					    		}else{
					    			szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]에 폭구분["+szYD_STK_BED_W_GP+"], 길이구분["+szYD_STK_BED_L_GP+"]이 존재하지 않습니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//									ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
									ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					    		}
								
				    		}else{
				    			szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]의 야드적치Bed입출고상태가 입출고가능하지 않으므로 적치불가능";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//								ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
								ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				    		}
				    	}else{
				    		return szRtnMsg;
				    	}
					}else{
						bIsToLocStackable				= true;
					}
					
					if( szYD_SCH_CD.substring(2, 4).equals("SL")) {	//선별 작업은 설비와 동일하게 처리
						szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]의 선별작업입니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						bIsToLocStackable				= true;
					}
					//----------------------------------------------------------------------------------------------------------------------
					
					//----------------------------------------------------------------------------------------------------------------------
					//	TO위치가이드와 권상지시위치의 동이 같으므로 TO위치가이드가 적치가능한 지를 비교
					//----------------------------------------------------------------------------------------------------------------------
					if( bIsToLocStackable ) {
						
						szLogMsg = "["+ szOperationName +"] 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]에 적치가능한 지 비교 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						
				    	recTemp = JDTORecordFactory.getInstance().create();
				    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE);
				    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH));						//크레인작업재료 총매수
				    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT));						//크레인작업재료 총중량
				    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T));						//크레인작업재료 총높이
				    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);
	
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// procAsgnedBedStackable_rt, procAsgnedBedStackable call 시  recTemp 에 logId SET 추가 개선
				    	recTemp.setField("LOG_ID", logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
				    	
				    	if( szYD_SCH_CD.substring(2, 4).equals("SL")) {	//선별 작업은  RT 가적배드와 동일하게
				    		szRtnMsg = YdToLocDcsnUtil.procAsgnedBedStackable_rt(recTemp, listToLoc, szMethodName);
				    	} else {
				    		szRtnMsg = YdToLocDcsnUtil.procAsgnedBedStackable(recTemp, listToLoc, szMethodName);
				    	}
				    	if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				    		
				    		ydStkLocVO = (YdStkLocVO)listToLoc.get(0);
				    		ydStkLocVO.setSeq(YdConstant.TO_LOC_PRIOR_USER);
				    		ydStkLocVO.setPrior(YdConstant.TO_LOC_PRIOR_USER);
				    		
				    		if( ydStkLocVO.getYdBedErrCd() != YdConstant.YD_BED_STACKABLE) {
					    		
				    		}
				    	}else{
	
				    	}
				    	
				    	szLogMsg = "["+ szOperationName +"] 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]에 적치가능한 지 비교 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					}else{
						szLogMsg = "["+ szOperationName +"] 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]에 적치불가능하므로 적치가능비교하지 않음";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					}
					//----------------------------------------------------------------------------------------------------------------------
				}
			}else{
				
				if( szYD_SCH_CD.substring(6, 7).equals("L")) { 
					
					if( szYD_TO_LOC_GUIDE.substring(1, 2).equals(szYD_UP_STK_COL_GP.substring(1, 2)) )	{
					}else{
						
						szLogMsg = "["+ szOperationName +"] 입고시 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]가 야드To위치Guide["+szYD_UP_STK_COL_GP+"]가 상이합니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						
						szYD_TO_LOC_GUIDE = "";
					}
					
				}else{	
					szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]가 존재하지 않거나 자리수(8)가 맞지 않으므로 해당 TO위치Guide를 검색하지 않습니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				}
			}
	
			//------------------------------------------------------------------------------------------------------------
			//	입고가적 적용여부 추가
			//  아래의 전체 입고가적베드 기능으로 통합, 따라서 K00140 기준값 'N'으로 셋팅함 - 2020.12.07 윤재광
			//------------------------------------------------------------------------------------------------------------
			JDTORecordSet 	outResult9 	= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord 		inRecord19 	= JDTORecordFactory.getInstance().create();
			JDTORecord 		outRecord19	= JDTORecordFactory.getInstance().create();
			String szAPPLY_YN9 			= "N";
			
			inRecord19.setField("REPR_CD_GP", "K00140");    //입고가적 적용여부
			
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
			intRtnVal = ydEqpDao.getYdEqp(inRecord19, outResult9, 999);
			if(intRtnVal > 0) {
				outResult9.first();
				outRecord19  = outResult9.getRecord();
				szAPPLY_YN9 = outRecord19.getFieldString("ITEM1");				
			}
			szLogMsg="입고가적 적용여부 " + szAPPLY_YN9 ;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG);			
			ydUtils.putLogNew(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);			
			
			if(szAPPLY_YN9.equals("Y")) {	
				if((szYD_RCPT_PLN_STR_LOC.length() > 2) && (szYD_TO_LOC_GUIDE.length() > 2)){
		    	//----------------------------------------------------------------------------------------------------------------------
					if( szYD_TO_LOC_GUIDE.length() == 8 && 
					    szYD_SCH_CD.substring(6, 7).equals("L")&& 
					    szYD_SCH_CD.substring(1, 2).equals("D")&&
					    szYD_TO_LOC_GUIDE.substring(1, 2).equals("D")&&
					    szYD_RCPT_PLN_STR_LOC.substring(1, 2).equals("F")&&
					    szYD_UP_STK_COL_GP.substring(1, 2).equals("D") ) { // 입고이고  D동 가적 BED인 경우
							
						bIsToLocStackable				= false;
								
						rsResult = JDTORecordFactory.getInstance().createRecordSet("");
						recTemp = JDTORecordFactory.getInstance().create();
				    	recTemp.setField("YD_STK_COL_GP", 	szYD_TO_LOC_GUIDE.substring(0, 6));
				    	recTemp.setField("YD_STK_BED_NO", 	szYD_TO_LOC_GUIDE.substring(6));
				    	
				    	/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedAll*/
				    	intRtnVal = ydStkbedDao.getYdStkbed(recTemp, rsResult, 313);
				    	
				    	if( intRtnVal > 0 ) {
				    		rsResult.first();
				    		recTemp			= rsResult.getRecord();
				    		String szYD_STK_BED_WHIO_STAT	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT");
				    		String szYD_STK_BED_SEL_GP		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_SEL_GP");
				    		if( szYD_STK_BED_WHIO_STAT.equals("H")) {
				    			szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]의 야드적치Bed입출고상태가 가적BED이므로 적치가능하므로 재료와 베드의 size비교 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//								ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
								ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
									
								bIsToLocStackable				= true;
				    		}
						}
						//----------------------------------------------------------------------------------------------------------------------
						
						//----------------------------------------------------------------------------------------------------------------------
						//	TO위치가이드와 권상지시위치의 동이 같으므로 TO위치가이드가 적치가능한 지를 비교
						//----------------------------------------------------------------------------------------------------------------------
						if( bIsToLocStackable ) {
							
							szLogMsg = "["+ szOperationName +"] 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]에 적치가능한 지 비교 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
							ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
							
					    	recTemp = JDTORecordFactory.getInstance().create();
					    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE);
					    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH));						//크레인작업재료 총매수
					    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT));						//크레인작업재료 총중량
					    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T));						//크레인작업재료 총높이
					    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);
		//SJH			    	
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// procAsgnedBedStackable_rt call 시  recTemp 에 logId SET 추가 개선
					    	recTemp.setField("LOG_ID", logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
					    	szRtnMsg = YdToLocDcsnUtil.procAsgnedBedStackable_rt(recTemp, listToLoc, szMethodName);
							
					    	if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					    		
					    		ydStkLocVO = (YdStkLocVO)listToLoc.get(0);
					    		ydStkLocVO.setSeq(YdConstant.TO_LOC_PRIOR_USER);
					    		ydStkLocVO.setPrior(YdConstant.TO_LOC_PRIOR_USER);
					    		
					    		if( ydStkLocVO.getYdBedErrCd() != YdConstant.YD_BED_STACKABLE) {
						    		
					    		}
					    	}else{
			
					    	}
					    	
					    	szLogMsg = "["+ szOperationName +"] 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]에 적치가능한 지 비교 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
							ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						}else{
							szLogMsg = "["+ szOperationName +"] 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]에 적치불가능하므로 적치가능비교하지 않음";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
							ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						}
							//----------------------------------------------------------------------------------------------------------------------
					}
				}
			}
			
			//----------------------------------------------------------------------------------------------------------------------
			//	입고예정위치가 존재하지 않는 경우에는 입고재료와 같은 Piling Code의 베드 OR 길이구분/폭구분이 같은 해당 동의 모든 위치를 조회 
			//----------------------------------------------------------------------------------------------------------------------
	    	szYD_GP			= szYD_UP_WO_LOC.substring(0, 1);
	    	szYD_BAY_GP		= szYD_UP_WO_LOC.substring(1, 2);
	    	szYD_EQP_GP		= szYD_UP_WO_LOC.substring(2, 4);
	    	
	    	//권상지시위치에 따라 알맞은 적치가능한 베드 검색 방법을 적용
	    	recTemp = JDTORecordFactory.getInstance().create();
	    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE);
	    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH));		//크레인작업재료 총매수
	    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT));		//크레인작업재료 총중량
	    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T));		//크레인작업재료 총높이
	    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);							//크레인스케줄코드
	    	recTemp.setField("YD_PILING_CD", 		szYD_PILING_CD);						//크레인작업 최하단재료의 Piling Code
	    	recTemp.setField("YD_MTL_L_GP", 		szYD_MTL_L_GP);							//크레인작업 최하단재료의 길이구분
	    	recTemp.setField("YD_MTL_W_GP", 		szYD_MTL_W_GP);							//크레인작업 최하단재료의 폭구분
	    	recTemp.setField("YD_STK_COL_GP", 		szYD_UP_STK_COL_GP);					//권상지시위치 - 적치열
	    	recTemp.setField("YD_EQP_ID", 			szYD_EQP_ID);							//크레인설비ID
	    	
	    	if( szYD_EQP_GP.equals("RT") || 
	    		szYD_EQP_GP.equals("TF")) {
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
	    		//	RT에서 입고 시는 최하단 크레인작업재료의 Piling Code와 베드의 최상단 재료의 Piling Code가 동일한 베드를 검색
	    		//	권상지시위치의 베드위치에 따라 01, 02, 03번지의 베드를 선택적으로 조회 필요.
	    		//	==> 차후에 기능 개선 필요 
	    		//----------------------------------------------------------------------------------------------------------------------
	    		szLogMsg  = "["+ szOperationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
	    		szLogMsg += "["+ szOperationName +"] RT/TF입고인 경우 Piling Code가 동일한 베드를 검색을 위한 검색범위와 검색방향을 설정 \n";
	    		szLogMsg += "["+ szOperationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
				//	권상 RT/TF베드를  야드베드번지로 변환
				//----------------------------------------------------------------------------------------------------------------------
	    		
	    		szLogMsg = "["+ szOperationName +"] 권상 "+szYD_EQP_GP+"베드[" + szYD_UP_STK_BED_NO + "]를  야드베드번지로 변환 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		
				String szYD_STK_BED_NO = "";
				
				if( szYD_EQP_GP.equals("RT") ) {
					szYD_STK_BED_NO = PlateGdsYdUtil.getYdBedNoFromRtBedNo(szYD_UP_STK_BED_NO);
				}else if( szYD_EQP_GP.equals("TF") ) {
					szYD_STK_BED_NO = PlateGdsYdUtil.getYdBedNoFromTfBedNo(szYD_UP_STK_BED_NO);
				}
	    		szLogMsg = "["+ szOperationName +"] 권상 "+szYD_EQP_GP+"베드[" + szYD_UP_STK_BED_NO + "]를  야드베드번지["+szYD_STK_BED_NO+"]변환 완료";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		//----------------------------------------------------------------------------------------------------------------------
				
				/*
				 * Crane1호기 2Grab(04, 05, 06스판작업) - 04, 05, 06스판의 STOPPER위치가 01번지이면 01, 02번지만 검색, 03번지이면 02, 03번지만 검색
				 * Crane2호기 1Grab(07스판작업) - 07스판을 모두 검색
				 * 검색방향은 출하에서 RT방향으로
				 * 1. 동일한 Piling Code로 조회
				 * 2. 공베드 조회 시 - 길이구분/폭구분으로 조회
				 * 	2-1. 입고예정위치가 존재하면 해당스판을 기준으로 해서 우선적으로 검색
				 *  2-2. 입고예정위치가 존재하지 않으면 임의의 순서로 검색
				 * 3. 혼적베드 검색 - 길이구분/폭구분으로 조회 : 위와 동일한 방법으로 검색
				 * 4. 임의의 베드를 강제로 적용. - 권하위치가 없다는 표시로 처리?
				 */
				
				//----------------------------------------------------------------------------------------------------------------------
				//	스판검색범위와 검색정렬방법을 설정
				//----------------------------------------------------------------------------------------------------------------------
				recTemp.setField("YD_STK_BED_NO", 	szYD_STK_BED_NO);
				
				if( szYD_TO_LOC_GUIDE.equals("") || 
					szYD_TO_LOC_GUIDE.length() != 8 ) {
					//-------------------------------------------------------------------------------
					//	야드TO위치Guide가 없는 경우에는 스케줄코드의 입고방향으로 검색범위와 방향 정의
					//	수정자 : 임춘수
					//	수정일 : 2009.12.22
					//-------------------------------------------------------------------------------
					if( szYD_SCH_CD.substring(7).equals("L") ) {									//스케줄코드가 Left
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);			//스판검색범위(04, 05, 06스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);			//검색방향(차량출하->RT방향)
					}else{																			//스케줄코드가 Right
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);			//스판검색범위(07스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);			//검색방향(차량출하->RT방향)
					}
					
					szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]가 존재하지 않거나 자리수(8)가 맞지 않으므로 스케줄코드의 입고방향으로 검색범위와 방향정의";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					//-------------------------------------------------------------------------------
				}else{
					if( szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_01) || 
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) ||
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);			//스판검색범위(1후판:04, 05, 06스판 / 2후판:01,02 스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);			//검색방향(차량출하->RT방향)
					}else if( szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_02) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_03) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);			//스판검색범위(1후판:07스판 / 2후판:02,03스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);			//검색방향(차량출하->RT방향)
					}
				}
	    		
	    	}else if( szYD_EQP_GP.equals("PT"))	{
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
	    		//	차량입고 시
	    		//----------------------------------------------------------------------------------------------------------------------
	    		szLogMsg  = "["+ szOperationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
	    		szLogMsg += "["+ szOperationName +"] 차량입고인 경우 Piling Code가 동일한 베드를 검색을 위한 검색범위와 검색방향을 설정 \n";
	    		szLogMsg += "["+ szOperationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
	    		//	스케줄코드로 통로 분석
	    		//----------------------------------------------------------------------------------------------------------------------
	    		String szPATH = szYD_SCH_CD.substring(5, 6);
	    		
	    		if( szPATH.equals("1"))	{
	    			//A통로 - 04, 05, 06스판 검색
	    		}else if( szPATH.equals("2"))	{
	    			//B통로 - 07스판 검색
	    		}
	    		//----------------------------------------------------------------------------------------------------------------------
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
				//	스판검색범위와 검색정렬방법을 설정
				//----------------------------------------------------------------------------------------------------------------------
				
				recTemp.setField("YD_STK_BED_NO", 	"");			//01, 02, 03번지 모두 검색
				
				if( szYD_TO_LOC_GUIDE.equals("") || 
					szYD_TO_LOC_GUIDE.length() != 8 ) {
					//-------------------------------------------------------------------------------
					//	야드TO위치Guide가 없는 경우에는 차량의 통로를 사용하여 검색범위와 방향 정의
					//	수정자 : 임춘수
					//	수정일 : 2009.12.22
					//-------------------------------------------------------------------------------
					if( szPATH.equals("1") || szPATH.equals("3"))	{
		    			//A통로 - 04, 05, 06스판 검색 , 2후판 : 01,02 스판 검색
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);		//스판검색범위(1후판:04, 05, 06스판 / 2후판:01,02 스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);		//검색방향(차량출하->RT방향)
		    		}else if( szPATH.equals("2") || szPATH.equals("4"))	{
		    			//B통로 - 07스판 검색 , 2후판 : 02,03 스판검색
		    			recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);		//스판검색범위(1후판:07스판 / 2후판:02,03스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);		//검색방향(차량출하->RT방향)
		    		}
					
					szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]가 존재하지 않거나 자리수(8)가 맞지 않으므로 차량의 통로를 사용하여 검색범위와 방향정의";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					//-------------------------------------------------------------------------------
				}else{
					if( szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_01) || 
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) ||	
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);				//스판검색범위(1후판:04, 05, 06스판 / 2후판:01,02 스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
					}else if( szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_02) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_03) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);				//스판검색범위(1후판:07스판 / 2후판:02,03스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
					}
				}
				//----------------------------------------------------------------------------------------------------------------------
	    	}else{
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
	    		//	이적/차량출고 인 경우에 적용 - 별도의 모듈화가 필요 시 제거
	    		//----------------------------------------------------------------------------------------------------------------------
	    		
	    		szLogMsg  = "["+ szOperationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
	    		szLogMsg += "["+ szOperationName +"] 이적/차량출고[" + szYD_SCH_CD + "]인 경우 Piling Code가 동일한 베드를 검색을 위한 검색범위와 검색방향을 설정 \n";
	    		szLogMsg += "["+ szOperationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
				//	스판검색범위와 검색정렬방법을 설정
				//----------------------------------------------------------------------------------------------------------------------
				recTemp.setField("YD_STK_BED_NO", 	szYD_UP_STK_BED_NO);
				
				if( szYD_TO_LOC_GUIDE.equals("") || 
					szYD_TO_LOC_GUIDE.length() != 8 ) {
					if( szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_01) || 
						szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
						szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) || 
						szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);				//스판검색범위(1후판:04, 05, 06스판 / 2후판:01,02 스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
					}else if( szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_02) || 
							  szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_03) || 
							  szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
							  szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);				//스판검색범위(1후판:07스판 / 2후판:02,03스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
					}
					
					szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]가 존재하지 않거나 자리수(8)가 맞지 않으므로 권상지시위치를 사용하여 검색범위와 방향정의";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					
				}else{
					if( szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_01) || 
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) ||		
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);				//스판검색범위(1후판:04, 05, 06스판 / 2후판:01,02 스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
					}else if( szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_02) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_03) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);				//스판검색범위(1후판:07스판 / 2후판:02,03스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
					}
				}
	    	}
	    	
	    	if(szYD_EQP_GP.equals("RT")|| szYD_EQP_GP.equals("TF"))  //입고작업 한정체크
	    	{
					outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
					inRecord2 	= JDTORecordFactory.getInstance().create();
					inRecord3 	= JDTORecordFactory.getInstance().create();
					inRecord3.setField("YD_CRN_SCH_ID"  ,szYD_CRN_SCH_ID);
					
					intRtnVal=0;  //intRtnVal 초기화
					
					//1후판 고장범위 체크 및 대체범위 반환쿼리
					if("D".equals(szYD_SCH_CD.substring(5, 6))||  //D/E/F RT일경우(1후판)
					   "E".equals(szYD_SCH_CD.substring(5, 6))||	
					   "F".equals(szYD_SCH_CD.substring(5, 6))||
					   "G".equals(szYD_SCH_CD.substring(5, 6))
					   )
					{
						intRtnVal = commDao.select(inRecord3, outResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.chkBrLocFor1");
					}
					else if("A".equals(szYD_SCH_CD.substring(5, 6))||  //A/B RT일경우(2후판)
						    "B".equals(szYD_SCH_CD.substring(5, 6))
						    )
					{
						intRtnVal = commDao.select(inRecord3, outResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.chkBrLocFor2");
					}//여기서 에러발생. 2후판 입고 작업중 스케줄코드가 TF로 되는 입고도 있음. 그건 쿼리 조건 둘다 안걸림.
					 //근데 intRtnVal값을 초기화 안하고 재사용하고있어서, 쿼리는 없는데 intRtnVal 값은 있는 상황 발생.
					
					if(intRtnVal>0){
						outResult1.first();
						inRecord2  = outResult1.getRecord();
						
						sFNL_CHK=ydDaoUtils.paraRecChkNull(inRecord2, "FNL_CHK");
						if(sFNL_CHK.equals("Y")){  //TO위치가 고장범위 속하고 설비가 고장이면
							sAL_FROM =ydDaoUtils.paraRecChkNull(inRecord2, "AL_FROM");  //TC073101
							sAL_FROM =sAL_FROM.substring(2,8);  //073101
							
							sAL_TO =ydDaoUtils.paraRecChkNull(inRecord2, "AL_TO");  //TC080199
							sAL_TO =sAL_TO.substring(2,8);  //080199	
							
							szLogMsg = "["+ szOperationName +"] FNL_CHK:"+sFNL_CHK+", sAL_FROM:"+sAL_FROM+", sAL_TO:"+sAL_TO;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
							ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						}
					}
				//}	    	
	    	}
	    	
	    	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	1후판 입고 적치가능한 가적베드 검색조회 시작
			//----------------------------------------------------------------------------------------------------------------------
			szLogMsg = "["+ szYD_SCH_CD.substring(5, 6) +"] 입고RT / ["+szYD_BAY_GP+"]입고동 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			if( szYD_EQP_GP.equals("RT")|| 
				szYD_EQP_GP.equals("TF")){  ////여기에(입고) 최종 결정된 TO위치가 존재하고, 그 위치가 고장설비 범위라면, 대체 TO위치 다시 탐색.20240215 박종호 REQ202401530768
				
				//WRKBOOK의 YD_TO_LOC_GUIDE가 고장 설비의 고장범위에 속하는지 체크 쿼리 후 속하면 대체범위로 FROM/TO DONG 재지정하자.
				//szYD_CRN_SCH_ID szYD_EQP_ID szYD_TO_LOC_GUIDE
				
				
/*				
				if(("D".equals(szYD_SCH_CD.substring(5, 6))|| 	//입고RT(1후판 전 라인라인으로 수정 2020.11.04 윤재광)
				    "E".equals(szYD_SCH_CD.substring(5, 6))||
				    "F".equals(szYD_SCH_CD.substring(5, 6)))&&
				    !szYD_MTL_W_GP.startsWith("L")){ 			// 광폭은 제외	
*/
				if("D".equals(szYD_SCH_CD.substring(5, 6))|| 	//입고RT(1후판 전 라인라인으로 수정 2020.11.04 윤재광)
				    "E".equals(szYD_SCH_CD.substring(5, 6))||
				    "F".equals(szYD_SCH_CD.substring(5, 6))|| //광폭재도 포함되도록 변경(박종호. 2022.04.28 임진후 사원 요청사항.)
				    "G".equals(szYD_SCH_CD.substring(5, 6))) 
					
					{ 		
				
					szLogMsg = "["+ szOperationName +"] 1후판 온라인 E동 적치가능한 가적베드 조회 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					
					outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
					inRecord3 	= JDTORecordFactory.getInstance().create();
					inRecord2 	= JDTORecordFactory.getInstance().create();

					inRecord3.setField("REPR_CD_GP" ,"T00261");	
					inRecord3.setField("CD_GP" ,szYD_SCH_CD.substring(5, 6));	// 입고R/T
					inRecord3.setField("ITEM"  ,szYD_BAY_GP);   				// 입고동
					
					/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
					intRtnVal = ydEqpDao.getYdEqp(inRecord3, outResult1, 999);
					if(intRtnVal > 0) {
						outResult1.first();
						inRecord2  = outResult1.getRecord();
						szFROM_DONG = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, "ITEM1"),"0101")+"01";
						szTO_DONG   = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, "ITEM2"),"0799")+"99";		
					} else {
						szFROM_DONG = "0101";
						szTO_DONG   = "0799"; 
					}
					
					if(sFNL_CHK.equals("Y")){  //크레인고장범위시 대체범위로 조정.(입고작업한정)
						szFROM_DONG=sAL_FROM;
						szTO_DONG=sAL_TO; 

						szLogMsg = "크레인고장범위시 대체범위로 조정. sAL_FROM:"+sAL_FROM+"sAL_TO:"+sAL_TO;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					}
					
					JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
					
					recPara1.setField("YD_GP"			, szYD_GP);
					recPara1.setField("YD_BAY_GP"		, szYD_BAY_GP);
					recPara1.setField("YD_PILING_CD"	, szYD_PILING_CD);
					recPara1.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
					recPara1.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);
					recPara1.setField("FR_YD_STK_BED_NO", "01");
					recPara1.setField("YD_EQP_WRK_SH"	, String.valueOf(intYD_EQP_WRK_SH));
					recPara1.setField("YD_EQP_WRK_WT"	, String.valueOf(intYD_EQP_WRK_WT));
					recPara1.setField("YD_EQP_WRK_T"	, String.valueOf(dblYD_EQP_WRK_T));
					recPara1.setField("YD_SCH_CD"		, szYD_SCH_CD);
					
					//----------------------------------------------------------------------------------------------------------------------
					//	조건에 해당하는 단 정보 조회 - 1후판 적치가능한 가적베드 정보 조회
					//----------------------------------------------------------------------------------------------------------------------
					JDTORecordSet rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
					
					/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithTempBedRatioReNew*/
					szRtnMsg = DaoManager.getYdStklyr(recPara1, rsResult1, 631);
					//----------------------------------------------------------------------------------------------------------------------
					if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {

						srchNconvRecord2VoTmpBed("","",recPara1, rsResult1, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD);
					}
					szLogMsg = "["+ szOperationName +"] 1후판 온라인 E동 적치가능한 가적베드 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					
					//----------------------------------------------------------------------------------------------------------------------
					// Sorting ?
					//----------------------------------------------------------------------------------------------------------------------
					if( listToLoc.size() > 0 ) {
						Collections.sort(listToLoc, new YdStkLocComparator());
					}
					//----------------------------------------------------------------------------------------------------------------------
					 
					//----------------------------------------------------------------------------------------------------------------------
					//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
					//----------------------------------------------------------------------------------------------------------------------
								
					for(int i = 0; i < listToLoc.size(); i++ ) {
						
						ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
						
						szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 1후판 적치가능한 가적베드 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						
			    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
			    			
							bIS_BED_STACKABLE		= true;
							plnLocDcsnGp = "T";
							szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
				    		szLogMsg += "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 1후판 온라인 E동 적치가능한 가적베드 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++\n";
				    		szLogMsg += "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				    		
							break;
						}
					}		
				}
				//----------------------------------------------------------------------------------------------------------------------
				//	1후판 온라인 E동 적치가능한 가적베드 검색조회 완료
				//----------------------------------------------------------------------------------------------------------------------
			}
			String szAPPLY_YDTOYD 		= "N";    // 신이적로직 적용여부
			
			if( !bIS_BED_STACKABLE) {
	    	
		    	//----------------------------------------------------------------------------------------------------------------------
				//	동일한 Piling Code의 적치가능한 베드 조회
				//----------------------------------------------------------------------------------------------------------------------
				szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]의 적치가능한 베드 조회 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				
				//------------------------------------------------------------------------------------------------------------
				//	신 이적작업  적용여부
				//------------------------------------------------------------------------------------------------------------
				if( szYD_EQP_GP.equals("PT") ||
		    		szYD_EQP_GP.equals("TR") ||
		    		szYD_EQP_GP.equals("SL") ) {
					// 기존거 사용함
					szAPPLY_YDTOYD = "N";
			   	} else {	
			   		// 이적,입고만 신규 사용
			   		szAPPLY_YDTOYD = "Y";
			   	}	
						
				if (szAPPLY_YDTOYD.equals("Y")){
				
					if( szYD_EQP_GP.equals("RT")|| 
				    	szYD_EQP_GP.equals("TF")){
						
						szLogMsg="신입고 로직으로 TO위치 검색";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						
						//----------------------------------------------------------------------------------------------------------------------
						//	이적스판검색범위
						//----------------------------------------------------------------------------------------------------------------------
						outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
						inRecord3 	= JDTORecordFactory.getInstance().create();
						inRecord2 	= JDTORecordFactory.getInstance().create();
	
						inRecord3.setField("REPR_CD_GP" ,"T00011");	
						inRecord3.setField("CD_GP" ,szYD_SCH_CD.substring(5, 6));   // 입고R/T
						inRecord3.setField("ITEM"  ,szYD_BAY_GP);    				// 입고동
						
						/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
						intRtnVal = ydEqpDao.getYdEqp(inRecord3, outResult1, 999);
						if(intRtnVal > 0) {
							outResult1.first();
							inRecord2  = outResult1.getRecord();
							szFROM_DONG = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, "ITEM1"),"0101");
							szTO_DONG   = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, "ITEM2"),"0799");		
						} else {
							szFROM_DONG = "0101";
							szTO_DONG   = "0799"; 
						}
						
						if(sFNL_CHK.equals("Y")){  //크레인고장범위시 대체범위로 조정.
							szFROM_DONG=sAL_FROM;
							szTO_DONG=sAL_TO; 

							szLogMsg = "크레인고장범위시 대체범위로 조정. sAL_FROM:"+sAL_FROM+"sAL_TO:"+sAL_TO;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
							ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						}
						
					}else{
						szLogMsg="신이적/선별 로직으로 TO위치 검색";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						
						//----------------------------------------------------------------------------------------------------------------------
						//	이적/선별(ProC로 검색함) 스판검색범위
						//----------------------------------------------------------------------------------------------------------------------
						String sAbleYn = ""; //창고간 통로허용 여부(S:분리, T:통합)
						String sRuleId = ""; //T00021(분리),TI0021(통합)
						
						outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
						inRecord3 	= JDTORecordFactory.getInstance().create();
						inRecord2 	= JDTORecordFactory.getInstance().create();
						
						inRecord3.setField("REPR_CD_GP" ,"TI0001");	
						inRecord3.setField("CD_GP" ,szYD_BAY_GP);    			
						inRecord3.setField("ITEM"  ,"S");	//분리기준
						
						/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
						intRtnVal = ydEqpDao.getYdEqp(inRecord3, outResult1, 999);
						
						if (intRtnVal > 0) {
							sAbleYn = "S";
							sRuleId = "T00021";
							
						} else {
							sAbleYn = "T";
							sRuleId = "TI0021";
						}
						
						outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
						inRecord3 	= JDTORecordFactory.getInstance().create();
						inRecord2 	= JDTORecordFactory.getInstance().create();
						
						inRecord3.setField("REPR_CD_GP" ,sRuleId);	
						inRecord3.setField("CD_GP" ,szYD_EQP_ID.substring(5, 6));    // 크레인 호기			
			
						/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgt*/
						intRtnVal = ydCarSchDao.getYdCarsch(inRecord3, outResult1, 400);
						
						if (intRtnVal > 0) {
							outResult1.first();
							inRecord2 = outResult1.getRecord();
							szFROM_DONG = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, szYD_BAY_GP+"_DONG"),"0101");
							szTO_DONG   = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, szYD_BAY_GP+"_DONG2"),"0799");
						
						} else {
							szFROM_DONG = "0101";
							szTO_DONG   = "0799"; 
						}
					}
					
					//---------------------------------------------------------------------------------------------------------
					// 크레인사양 조회
					// 설비ID로  크크레인 Grab 구분 추출
					//---------------------------------------------------------------------------------------------------------
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, "To위치결정[크레인GRAB구분확인]"+szYD_EQP_ID, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, "To위치결정[크레인GRAB구분확인]"+szYD_EQP_ID, YdConstant.DEBUG, logId);
					
					JDTORecord recTemp1 	= JDTORecordFactory.getInstance().create();
					JDTORecord recSpec 		= JDTORecordFactory.getInstance().create();
					JDTORecordSet specSet 	= JDTORecordFactory.getInstance().createRecordSet("");
					recTemp1.setField("YD_EQP_ID", szYD_EQP_ID);
					
					szRtnMsg	= DaoManager.getYdCrnspec(recTemp1, specSet, 0);
					
					specSet.first();
					recSpec = specSet.getRecord();
					
					szYD_CRN_GRAB_TP = ydDaoUtils.paraRecChkNull(recSpec, "YD_CRN_GRAB_TP"); // 크레인 Grab 구분


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// getBedWithSamePilingCdNew call 시  recTemp 에 logId SET 추가 개선
					recTemp.setField("LOG_ID", logId);
//2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
					
					szRtnMsg = getBedWithSamePilingCdNew(szFROM_DONG,szTO_DONG, szYD_CRN_GRAB_TP, recTemp, listToLoc);
				}else{
					
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// getBedWithSamePilingCd call 시  recTemp 에 logId SET 추가 개선
					recTemp.setField("LOG_ID", logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
					szRtnMsg = getBedWithSamePilingCd(recTemp, listToLoc);
				} 
				
				szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]의 적치가능한 베드 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				//----------------------------------------------------------------------------------------------------------------------
			
				//----------------------------------------------------------------------------------------------------------------------
				// Sorting ?
				//----------------------------------------------------------------------------------------------------------------------
				if( listToLoc.size() > 0 ) {
					Collections.sort(listToLoc, new YdStkLocComparator());
				}
				//----------------------------------------------------------------------------------------------------------------------
				 
				//----------------------------------------------------------------------------------------------------------------------
				//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
				//----------------------------------------------------------------------------------------------------------------------
				//boolean bIS_BED_STACKABLE	= false;
				
				for(int i = 0; i < listToLoc.size(); i++ ) {
					
					ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
					
					szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";

		    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					
		    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
		    			
						bIS_BED_STACKABLE		= true;
						plnLocDcsnGp = "P";
						szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
			    		szLogMsg += "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++\n";
			    		szLogMsg += "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			    		
						break;
					}
				}
				
				// SJH05001
				// 입고작업일경우 대형 고객사와 주문량을 CHECK 하여 저장위치 검색 별도 처리 함	(2014.05.23 이적작업일경우도 포함시킴)	
				if( !bIS_BED_STACKABLE ) {
				 	if( szYD_EQP_GP.equals("RT") || 
				    	szYD_EQP_GP.equals("TF") ||
				    	szYD_EQP_GP.equals("PT") ||
				    	szYD_SCH_CD.substring(6, 7).equals("M")	) {
						
				 		JDTORecordSet outResult99 = JDTORecordFactory.getInstance().createRecordSet("");
						JDTORecord 	  inRecord99  = JDTORecordFactory.getInstance().create();
						JDTORecord 	  outRecord99 = JDTORecordFactory.getInstance().create();
						JDTORecord 	  recTemp1 	  = JDTORecordFactory.getInstance().create();
						String szAPPLY_YN1 		  = "N";
						
						if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)) {
							inRecord99.setField("REPR_CD_GP", "T00070");    //저장그룹편성보완
						} else {
							inRecord99.setField("REPR_CD_GP", "K00070");    //저장그룹편성보완
						}
						
						/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
						intRtnVal = ydEqpDao.getYdEqp(inRecord99, outResult99, 999);
						if(intRtnVal > 0) {
							outResult99.first();
							outRecord99 = outResult99.getRecord();
							szAPPLY_YN1 = outRecord99.getFieldString("ITEM1");				
						}
						
						szLogMsg="신저장그룹편성보완 적용 " + szAPPLY_YN1 ;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG);			
						ydUtils.putLogNew(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);			
						
						if(szAPPLY_YN1.equals("Y")) {
							szSTL_NO	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
							szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
							ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
							
							rsResult = JDTORecordFactory.getInstance().createRecordSet("");
							/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdOrdWgtCheck*/
							intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 606);
							if( intRtnVal < 1 ) {
								return szRtnMsg;
							}
							
							rsResult.first();
							recTemp1 = rsResult.getRecord();
							szCHECK_FLAG 	= ydDaoUtils.paraRecChkNull(recTemp1, "CHECK_FLAG");		        //주문량이 합(수량x두께) > 121	
							
							szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 완료 - 주문량이 합["+szCHECK_FLAG+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
							ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
							
							if(szCHECK_FLAG.equals("Y")) {
								szSEARCH_CHANGE = "Y";
							} else {
								szSEARCH_CHANGE = "N";
							}
						}
				 	}	
				}
			}	
			//----------------------------------------------------------------------------------------------------------------------
			//	동일한 Piling Code의 베드가 존재하지 않을 경우에는 
			//	혼적 bed 조회
			//----------------------------------------------------------------------------------------------------------------------
			
			if( !bIS_BED_STACKABLE) {
				
				if(szSEARCH_CHANGE.equals("Y")) {     //입고 대형 고객사 
				
				} else {	
					
					ydStkLocVO			= null;
					
					listToLoc 			= new ArrayList();
					
					szLogMsg = "["+ szOperationName +"] 혼적베드 조회 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// getBedWithSimilarGpNew, getBedWithSimilarGp call 시  recTemp 에 logId SET 추가 개선
					recTemp.setField("LOG_ID", logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
					if(	szAPPLY_YDTOYD.equals("Y")){
						
						szRtnMsg = getBedWithSimilarGpNew(szSTL_NO, szFROM_DONG, szTO_DONG, szYD_CRN_GRAB_TP, recTemp, listToLoc);
					} else {
						szRtnMsg = getBedWithSimilarGp(szSTL_NO, recTemp, listToLoc);
					}	
					szLogMsg = "["+ szOperationName +"] 혼적베드 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					
					//----------------------------------------------------------------------------------------------------------------------
					// Sorting
					//----------------------------------------------------------------------------------------------------------------------
					if( listToLoc.size() > 0 ) {
						Collections.sort(listToLoc, new YdStkLocComparator());
					}
					//----------------------------------------------------------------------------------------------------------------------
					
					//----------------------------------------------------------------------------------------------------------------------
					//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
					//----------------------------------------------------------------------------------------------------------------------
					
					for(int i = 0; i < listToLoc.size(); i++ ) {
						
						ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
						
						szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 혼적베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";						
			    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			    		
			    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
							
							bIS_BED_STACKABLE		= true;
							plnLocDcsnGp = "S";
							szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
							szLogMsg = "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 혼적베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		    	    		szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		    	    		
							break;
						}
					}
					//----------------------------------------------------------------------------------------------------------------------
				}	
			}
			
			//----------------------------------------------------------------------------------------------------------------------
			//	동일한 Piling Code의 베드가 존재하지 않을 경우에는 
			//	길이구분/폭구분이 동일한 공베드를 조회
			//----------------------------------------------------------------------------------------------------------------------
			if( !bIS_BED_STACKABLE ) {
				
				//------------------------------------------------------------------------------
				//	위에서 조회된 결과를 갖고 있으므로 사용하기 전에 값을 초기화시킴
				//	수정자 : 임춘수
				//	수정일 : 2010.01.04
				//------------------------------------------------------------------------------
				ydStkLocVO			= null;
				
				listToLoc 			= new ArrayList();
				
				szLogMsg = "["+ szOperationName +"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 적치가능한 공베드 조회 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				
				if(	szAPPLY_YDTOYD.equals("Y")){
					szRtnMsg = getEmptyBedWithSameLWGpNew(szFROM_DONG, szTO_DONG, szYD_CRN_GRAB_TP, recTemp, listToLoc, szYD_CRN_SCH_ID);
				} else {
					szRtnMsg = getEmptyBedWithSameLWGp(recTemp, listToLoc);
				}	
	
				
				szLogMsg = "["+ szOperationName +"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 적치가능한 공베드 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				
				//----------------------------------------------------------------------------------------------------------------------
				// Sorting
				//----------------------------------------------------------------------------------------------------------------------
				if( listToLoc.size() > 0 ) {
					Collections.sort(listToLoc, new YdStkLocComparator());
				}
				//----------------------------------------------------------------------------------------------------------------------
				
				//----------------------------------------------------------------------------------------------------------------------
				//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
				//----------------------------------------------------------------------------------------------------------------------
				
				for(int i = 0; i < listToLoc.size(); i++ ) {
					
					ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
					
					szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);

		    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
						
						bIS_BED_STACKABLE		= true;
						plnLocDcsnGp = "E";
						szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						szLogMsg = "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//	    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    	    		szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//	    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    	    		
						break;
					}
				}
			}
			
			//대체크레인 입고 TO위치 재탐색일 경우에는, 같은사이즈그룹의 일반베드까지 한번 더 탐색해준다. 2024-03-13 박종호 임진후 기사 요청사항.
			if( !bIS_BED_STACKABLE ) {  //공베드까지 조회했음에도 못찾았다면 모든 일반베드 대상으로 사이즈그룹 동일한 위치 탐색.

					//------------------------------------------------------------------------------
					//	위에서 조회된 결과를 갖고 있으므로 사용하기 전에 값을 초기화시킴
					//	수정자 : 임춘수
					//	수정일 : 2010.01.04
					//------------------------------------------------------------------------------
					ydStkLocVO			= null;
					
					listToLoc 			= new ArrayList();
					
					szLogMsg = "["+ szOperationName +"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 적치가능한 모든베드 조회 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					
					//if(	szAPPLY_YDTOYD.equals("Y")){
					//기존 공베드 탐색 로직임. 쿼리에 공베드조건만 빼서 다시 검색하게 바꾸자..
						szRtnMsg = getNormalBedWithSameLWGpNew(szFROM_DONG, szTO_DONG, szYD_CRN_GRAB_TP, recTemp, listToLoc, szYD_CRN_SCH_ID);
					//} else {
					//	szRtnMsg = getEmptyBedWithSameLWGp(recTemp, listToLoc);
					//}	
		
					
					szLogMsg = "["+ szOperationName +"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 적치가능한 공베드 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					
					//----------------------------------------------------------------------------------------------------------------------
					// Sorting
					//----------------------------------------------------------------------------------------------------------------------
					if( listToLoc.size() > 0 ) {
						Collections.sort(listToLoc, new YdStkLocComparator());
					}
					//----------------------------------------------------------------------------------------------------------------------
					
					//----------------------------------------------------------------------------------------------------------------------
					//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
					//----------------------------------------------------------------------------------------------------------------------
					
					for(int i = 0; i < listToLoc.size(); i++ ) {
						
						ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
						
						szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 일반베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
						ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
							
							bIS_BED_STACKABLE		= true;
							plnLocDcsnGp = "G";
							szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
							szLogMsg = "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		    	    		szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		    	    		
							break;
						}
					}
				
			}
			
	
			boolean isAutoB_toXX=false; //1후판 B동 소폭재이고, 입고이적시 최종 TO위치(소폭베드) 못찾은 경우 
		 	 
			//TO위치 탐색방법 UPDATE 
			updCrnschToLocFindMethodBySchId(szYD_CRN_SCH_ID,plnLocDcsnGp,logId);
			if( !bIS_BED_STACKABLE ) {
				 if( PlateGdsYdUtil.isApplyYn("입고이적 권하위치(XX00)예외 로직 적용 여부") ){
					 //일반적인 협폭재 중간경유 베드 검색의 경우도 여기에(bIS_BED_STACKABLE=False)걸리는지 확인필요
					 //bIS_BED_STACKABLE=False이면서 협폭재인 경우, 아래에서 에러 발생.권하위치 셋팅 변수가 null
  					 if (("TBCRB3".equals(szYD_EQP_ID) || "TBCRB4".equals(szYD_EQP_ID)) && ("RT".equals(szYD_EQP_GP) && dblYD_EQP_WRK_W <= 2100)){
  						isAutoB_toXX=true;  //1후판 B동 소폭재 입고 작업의 최종 TO위치(소폭베드) 못찾은 경우
  						szLogMsg = "["+ szOperationName +"] 협폭 권하위치검색 실패 - 협폭재 중간경유 BED 검색, isAutoB_toXX:"+isAutoB_toXX;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
// 						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
  						ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
  					 }
  					 else {
    					 if(szSEARCH_CHANGE.equals("Y")) {
    							
    						szLogMsg = "["+ szOperationName +"] 권하위치검색 실패 - 대형고객사 적치가능한 베드가 존재하지 않습니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//    						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    						ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    						return YdConstant.RETN_BIG_NOT_EXIST_BED;
    						
    					 } else {
    						szLogMsg = "["+ szOperationName +"] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//    						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    						ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    						return YdConstant.RETN_NOT_EXIST_BED;
    					 }
  				     }
  			     }
  			     else {
  					 if(szSEARCH_CHANGE.equals("Y")) {
  							
  						szLogMsg = "["+ szOperationName +"] 권하위치검색 실패 - 대형고객사 적치가능한 베드가 존재하지 않습니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
// 						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
  						ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
  						return YdConstant.RETN_BIG_NOT_EXIST_BED;
  						
  					 } else {
  						szLogMsg = "["+ szOperationName +"] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
// 						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
  						ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
  						return YdConstant.RETN_NOT_EXIST_BED;
  					 }	
  			     }
			}
	
			/*
			 * 권하위치 최종결정정보 셋팅.
			 */
			
			if(isAutoB_toXX){  //1후판B동 소폭재 중폭베드 입고시, 최종위치(소폭베드) 못찾은 경우는 위치값이 비어있어서, 위치셋팅하면 NULL EXCEPTION 발생함.
					szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 협폭check:Y 이면서, 최종 TO위치 탐색 실패하여, 권하예정지 셋팅 안함(NULL EXCEPTION 방지)";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			}
			else{
				/*250325 임진후기사 요청 RITM0993257
				 * 
				 * 파일링 시, 도중에 높이제한 문제로 권하위치가 변경시, 크레인 크로스 현상 발생 가능성이 있음
				 * (크레인크로스: 기존엔 E1 크레인 작업영역 이었다가 TO위치 변경되면서 E2 크레인 작업영역으로 지시 가능성)
				 * 이때, 한번의 파일링 작업정도는 높이제한보다 높게 쌓아도 문제 없기 때문에, 그 파일링 작업에 한해서 높이초과여도 고정.
				 * 
				 * */
				sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "013");//후판 개발 적용여부
				
				
				//최초 파일링지시의 권하위치 get 
				YdStkLocVO		initYdStkLocVO		= new YdStkLocVO();
				
				if( "Y".equals(sApplyYnPI)
				    &&( "S".equals(szYd_SCH_ST_GP) || "E".equals(szYd_SCH_ST_GP) ) 
	                &&szYD_SCH_CD.substring(6, 7).equals("L") ){
					initYdStkLocVO = getInitialPilingCrnschIdByCurSchId( szYD_CRN_SCH_ID, logId );
					
					if(initYdStkLocVO != null){
						//해당 권하위치의 베드 에러코드 get 
						JDTORecord  tempParam     = JDTORecordFactory.getInstance().create();
						
						tempParam.setField("YD_STK_COL_GP", initYdStkLocVO.getYdStkColGp());
						tempParam.setField("YD_STK_BED_NO", initYdStkLocVO.getYdStkBedNo());
						tempParam.setField("YD_SCH_CD", szYD_SCH_CD);
						tempParam.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH));		//크레인작업재료 총매수
						tempParam.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT));		//크레인작업재료 총중량
						tempParam.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T));		//크레인작업재료 총높이
						tempParam.setField("LOG_ID", logId);
						
						szRtnMsg = procBedStackable(tempParam, null, szMethodName);
						
						szLogMsg = "["+ szOperationName +"] 베드["+initYdStkLocVO.getYdStkColGp()+" - "+initYdStkLocVO.getYdStkBedNo()+"]가 적치가능한 지 확인 완료. 결과코드 ["+szRtnMsg+"]";
						ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					}
					else{
						szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] 최초 파일링지시 존재 안함";
						ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					}
						
				}
				
				//입고스케줄에 파일링작업이고, 높이제한초과코드면 기존 권하위치 유지
				if( "Y".equals(sApplyYnPI)
				    &&( "S".equals(szYd_SCH_ST_GP) || "E".equals(szYd_SCH_ST_GP) ) 
	                &&szYD_SCH_CD.substring(6, 7).equals("L")
	                &&Integer.toString(YdConstant.YD_BED_ERR_CD_H_OVER).equals(szRtnMsg) ) { 
							
					szYD_DN_STK_COL_GP 	= initYdStkLocVO.getYdStkColGp();
					szYD_DN_STK_BED_NO 	= initYdStkLocVO.getYdStkBedNo();
					szYD_DN_WO_LOC		= szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;
					szYD_DN_WO_LAYER 	= initYdStkLocVO.getYdStkLyrNo();
				}
				else {
					szYD_DN_STK_COL_GP 	= ydStkLocVO.getYdStkColGp();
					szYD_DN_STK_BED_NO 	= ydStkLocVO.getYdStkBedNo();
					szYD_DN_WO_LOC		= szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;
					szYD_DN_WO_LAYER 	= ydStkLocVO.getYdStkLyrNo();
				}
			}
			
			/*	
			else{
			szYD_DN_STK_COL_GP 	= ydStkLocVO.getYdStkColGp();
			szYD_DN_STK_BED_NO 	= ydStkLocVO.getYdStkBedNo();
			szYD_DN_WO_LOC		= szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;
			szYD_DN_WO_LAYER 	= ydStkLocVO.getYdStkLyrNo();
			}
			*/
				
			// AT0000_물류시스템 개선 2022.10.27 Start
			// B동 B3,B4 무인mode RT/TF 입고 시 협폭 2100mm이하 일 경우 중폭 베드에서 정렬 후 최종 위치에 권하
//			JDTORecordSet 	outResult 	= JDTORecordFactory.getInstance().createRecordSet("");
//			JDTORecord 		inRecord1 	= JDTORecordFactory.getInstance().create();
//			JDTORecord 		outRecord1	= JDTORecordFactory.getInstance().create();			
			JDTORecord      params     = JDTORecordFactory.getInstance().create();
			
//			inRecord1.setField("YD_EQP_ID", szYD_EQP_ID);  
//			intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 0);
//			if(intRtnVal > 0) {
//			   outResult.first();
//			   outRecord1  = outResult.getRecord();
//			   sCrnWrkMode2 = outRecord1.getFieldString("YD_EQP_WRK_MODE2");				
//			}
			//크레인 자동화 소폭 > 중폭 입고이적 체크(TO위치를 기존 소폭 TO위치에서 중폭 경유 TO위치로 변경) 김기태 부장.
			szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 협폭check 시작  대상재 폭 : " + dblYD_EQP_WRK_W;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 최종목적지 bed :" + szYD_DN_WO_LOC + "목적지 폭구분 : " + szYD_STK_BED_W_GP_G ;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						
			if ("TBCRB3".equals(szYD_EQP_ID) || "TBCRB4".equals(szYD_EQP_ID)){	
			   if (("RT".equals(szYD_EQP_GP)) && dblYD_EQP_WRK_W < 2250){
				   if( PlateGdsYdUtil.isApplyYn("소폭제 중간 BED 사용 신규로직 적용 여부") ){
				       if ("S".equals(szYD_MTL_W_GP.substring(0,1))){
						   JDTORecordSet   rsStopover   = JDTORecordFactory.getInstance().createRecordSet("");
						   String szFROM_COL_GP="";
						   String szTO_COL_GP ="";
						   
						   if(isAutoB_toXX){ //최종 to위치 탐색 실패시, 작업예약 등록시 셋팅된 초기 to위치값으로 지정함.
								   szYD_TO_LOC_GUIDE = szYD_TO_LOC_GUIDEbak;   
						   }
						   else{
								   szYD_TO_LOC_GUIDE = szYD_DN_WO_LOC;
						   }
							   
						   szFROM_COL_GP ="TB" + szFROM_DONG;  //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리
						   szTO_COL_GP =  "TB" + szTO_DONG; //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리	   
						   /*	    
						   else{
							szYD_TO_LOC_GUIDE = szYD_DN_WO_LOC;	
						    	    	
						    szFROM_COL_GP =  szYD_DN_STK_COL_GP.substring(0,2) + szFROM_DONG;
						    szTO_COL_GP =  szYD_DN_STK_COL_GP.substring(0,2) + szTO_DONG;
						   }
						   */	        
						    szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 경유위치 결정대상New : From Bed :" + szFROM_COL_GP + " To Bed : " + szTO_COL_GP;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						   	ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						   	ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						        		    
						   	params.setField("FROM_COL_GP", szFROM_COL_GP); 
						   	params.setField("TO_COL_GP",   szTO_COL_GP);
						   	params.setField("MTL_L_GP",    szYD_MTL_L_GP);
						        			
						   	intRtnVal = commDao.select(params, rsStopover, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectStopOverQuery");  
						   	if (intRtnVal <= 0){
						   	   szLogMsg = "["+szOperationName+"] 스케줄[" + szYD_CRN_SCH_ID + "] : 주작업[TO위치결정방법- 경유위치 결정 실패!!] intRtnVal : " + intRtnVal;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						       ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
						       ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
						       return YdConstant.RETN_NOT_EXIST_BED;
						   	}
						  	
							szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 주작업[TO위치결정방법- 경유위치 결정S 성공 intRtnVal : " + intRtnVal;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						   	ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						   	ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						   
						   	szYD_DN_STK_COL_GP = rsStopover.getRecord(0).getFieldString("YD_STK_COL_GP"); 
						   	szYD_DN_STK_BED_NO = rsStopover.getRecord(0).getFieldString("YD_STK_BED_NO");  
						    szYD_DN_WO_LAYER   = PlateGdsYdUtil.trim(rsStopover.getRecord(0).getFieldString("YD_STK_LYR_NO"));
						   	szYD_DN_WO_LOC     = szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;				   
						   	szCflag = "Ok";
						  	szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 주작업[TO위치결정방법- 경유위치 결정E 성공 : " + szYD_DN_STK_COL_GP + " szCflag : " + szCflag;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						   	ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						   	ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					    }
				   }
				   else {
					   if ("S0".equals(szYD_MTL_W_GP)){
						   JDTORecordSet   rsStopover   = JDTORecordFactory.getInstance().createRecordSet("");
							szYD_TO_LOC_GUIDE = szYD_DN_WO_LOC;	
						    	    	
						    String szFROM_COL_GP =  szYD_DN_STK_COL_GP.substring(0,2) + szFROM_DONG;
						    String szTO_COL_GP =  szYD_DN_STK_COL_GP.substring(0,2) + szTO_DONG;
						    	        
						    szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 경유위치 결정대상 : From Bed :" + szFROM_COL_GP + " To Bed : " + szTO_COL_GP;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						   	ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						   	ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						        		    
						   	params.setField("FROM_COL_GP", szFROM_COL_GP); 
						   	params.setField("TO_COL_GP",   szTO_COL_GP);
						   	params.setField("MTL_L_GP",    szYD_MTL_L_GP);
						        			
						   	intRtnVal = commDao.select(params, rsStopover, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectStopOverQuery");  
						   	if (intRtnVal <= 0){
						   	   szLogMsg = "["+szOperationName+"] 스케줄[" + szYD_CRN_SCH_ID + "] : 주작업[TO위치결정방법- 경유위치 결정 실패!!] intRtnVal : " + intRtnVal;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						       ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
						       ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
						       return YdConstant.RETN_NOT_EXIST_BED;
						   	}
						  	
							szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 주작업[TO위치결정방법- 경유위치 결정S 성공 intRtnVal : " + intRtnVal;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						   	ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						   	ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						   
						   	szYD_DN_STK_COL_GP = rsStopover.getRecord(0).getFieldString("YD_STK_COL_GP"); 
						   	szYD_DN_STK_BED_NO = rsStopover.getRecord(0).getFieldString("YD_STK_BED_NO");  
						    szYD_DN_WO_LAYER   = PlateGdsYdUtil.trim(rsStopover.getRecord(0).getFieldString("YD_STK_LYR_NO"));
						   	szYD_DN_WO_LOC     = szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;				   
						   	szCflag = "Ok";
						  	szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 주작업[TO위치결정방법- 경유위치 결정E 성공 : " + szYD_DN_STK_COL_GP + " szCflag : " + szCflag;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						   	ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						   	ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					    }
				    }
				}
			}
				
			// AT0000_물류시스템 개선 2022.10.27 End
	
			if( szYD_SCH_CD.substring(2, 4).equals("SL")) {	
				if( !szYD_TO_LOC_GUIDE.equals(szYD_DN_WO_LOC)){ 
					szLogMsg = "["+ szOperationName +"] 권하위치검색 실패 - 선별은 야드To위치Guide만 처리됨";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					return YdConstant.RETN_NOT_EXIST_BED;
				}	
			}
		}	
		//----------------------------------------------------------------------------------------------------------------------
		// 권하지시위치 수정
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		JDTORecord recUpCrnSch	= null;
		
		recUpCrnSch = JDTORecordFactory.getInstance().create();
		recUpCrnSch.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);									//크레인스케줄ID
		if ("Ok".equals(szCflag)) {                                                                         //ATOOO 물류시스템 개선 2022.10.27 최종 to위치
			recUpCrnSch.setField("YD_TO_LOC_GUIDE", 		szYD_TO_LOC_GUIDE);	                               
		}
		recUpCrnSch.setField("YD_EQP_ID", 				szYD_EQP_ID);										//크레인설비ID
		recUpCrnSch.setField("YD_UP_STK_COL_GP", 		szYD_UP_STK_COL_GP);								//권상지시위치 - 적치열
		recUpCrnSch.setField("YD_UP_STK_BED_NO", 		szYD_UP_STK_BED_NO);								//권상지시위치 - 적치베드
		recUpCrnSch.setField("YD_DN_STK_COL_GP", 		szYD_DN_STK_COL_GP);								//권하지시위치 - 적치열
		recUpCrnSch.setField("YD_DN_STK_BED_NO", 		szYD_DN_STK_BED_NO);								//권하지시위치 - 적치베드
		
		//----------------------------------------------------------------------------------------------------------------------
		//	권상지시위치 업데이트
		//----------------------------------------------------------------------------------------------------------------------
		if( bUP_UPDT_NEEDED ) {
			szLogMsg = "["+ szOperationName +"] 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"] 크레인스케줄에 등록";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			recUpCrnSch.setField("YD_UP_WO_LOC", 		szYD_UP_WO_LOC);									//권상지시위치
			recUpCrnSch.setField("YD_UP_WO_LAYER", 		szYD_UP_WO_LAYER);									//권상지시단
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		recUpCrnSch.setField("YD_DN_WO_LOC", 			szYD_DN_WO_LOC);									//권하지시위치
		recUpCrnSch.setField("YD_DN_WO_LAYER", 			szYD_DN_WO_LAYER);									//권하지시단
		recUpCrnSch.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));					//크레인작업재료 총매수
		recUpCrnSch.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));					//크레인작업재료 총중량
		recUpCrnSch.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));					//크레인작업재료 총높이
		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);								//크레인작업재료 중 최대 폭
		recUpCrnSch.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);								//크레인작업재료 중 최대 길이
		recUpCrnSch.setField("MODIFIER", 				szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		
		szRtnMsg = CrnSchUtil.uptCrnSchXYCord(recUpCrnSch);
    	
    	szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 완료";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
    	
		//----------------------------------------------------------------------------------------------------------------------
		//	권하지시위치에 재료를 권하대기로 등록
		//----------------------------------------------------------------------------------------------------------------------
    	szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]에 크레인작업재료 등록 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szRtnMsg	= uptDnWaitOnYdStkLyr(rsCrnwrkmtl, szYD_DN_WO_LOC, szYD_DN_WO_LAYER);
    	
		szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]에 크레인작업재료 등록 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "주작업TO위치결정(후판제품)(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
	} // end of procMainWrkToLocForPlateYd
	
	/**
	 * 주작업TO위치결정(후판제품) 25.05.27 renew 버전. by hjw 
	 * 후판제품 임진후 기사 요청대로 to위치 결정 방법 개선 및 to위치 결정 순서 조정기능 추가.
	 * 
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String procMainWrkToLocForPlateYd2nd(
			JDTORecord msgRecord					/* 전문 */
			, JDTORecordSet rsCrnwrkmtl				/* 크레인작업재료 */
			, JDTORecord recCrnSch					/* 크레인스케줄정보 */
			, JDTORecord recWbook					/* 작업예약정보 */
			) throws JDTOException {
		
		String methodName				 = "procMainWrkToLocForPlateYd2nd";
		String operationName			 = "주작업TO위치결정(후판제품)2nd";
		String logMsg					 = null;
		String rtnMsg 				     = YdConstant.RETN_CD_SUCCESS;
		String[] ydStkLyrMtlStat         = {YdConstant.YD_STK_LYR_MTL_STAT_DN_WAIT, YdConstant.YD_STK_LYR_MTL_STAT_UN_WAIT};
		
		String logId                     = ydUtils.getJDTOLogId(msgRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		logMsg = "주작업TO위치결정(후판제품)(" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
		YdPlateCommDAO commDao = new YdPlateCommDAO(); 
		JDTORecord		recPara		 = null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//------------------------------------- 사용자정의위치(입고예정위치)에 대한 TO위치결정 -------------------------------------
		//----------------------------------------------------------------------------------------------------------------------
		
		//함수명 추가 메세지 설정
		String ydSchCd	  = ydDaoUtils.paraRecChkNull(recWbook, "YD_SCH_CD");
		String operationMsg = getOperationMsgFromSchCd(ydSchCd);
				
		if(!operationMsg.isEmpty()){
			operationName += "-" +operationMsg;
		}
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------
		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		
		String ydCrnSchId       = ydDaoUtils.paraRecChkNull(recCrnSch,"YD_CRN_SCH_ID");					//크레인스케줄ID
		String ydEqpId          = ydDaoUtils.paraRecChkNull(recCrnSch,"YD_EQP_ID");						//크레인설비ID
		
		int ydEqpWrkSh      	= ydDaoUtils.paraRecChkNullInt(recPara,"SH_CNT");						//크레인작업재료 총매수
		int ydEqpWrkWt      	= ydDaoUtils.paraRecChkNullInt(recPara,"SUM_MTL_WT");					//크레인작업재료 총중량
		double ydEqpWrkT      	= ydDaoUtils.paraRecChkNullDouble(recPara,"SUM_MTL_T");					//크레인작업재료 총높이
		
		String ydEqpWrkMaxW		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_W");						//크레인작업재료 중 최대 폭
		String ydEqpWrkMaxL		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_L");						//크레인작업재료 중 최대 길이 
		
		String ydToLocGuide     = ydDaoUtils.paraRecChkNull(recWbook, "YD_TO_LOC_GUIDE");				//to위치 가이드
		String ydSchStGp        = ydDaoUtils.paraRecChkNull(recWbook, "YD_SCH_ST_GP");					//파일링작업 구분. S:파일링시작 E:파일링 끝
		
		String ydDnStkColGp 	= "";
		String ydDnStkBedNo 	= "";
		if( ydToLocGuide.length() == 8 ) {
			ydDnStkColGp		= ydToLocGuide.substring(0, 6);
			ydDnStkBedNo		= ydToLocGuide.substring(6);
		}
		
		logMsg = "["+ operationName +"] 크레인작업재료의 총매수 :" + ydEqpWrkSh;
		logMsg += ", 총중량 :" + ydEqpWrkWt;
		logMsg += ", 총높이  :" + ydEqpWrkT;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		logMsg = "["+ operationName +"] ---------------------- 야드To위치Guide : " + ydToLocGuide + " --------------------------";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		String stlNo	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
		
		logMsg = "["+ operationName +"] 크레인작업재료의 최하단 재료정보["+stlNo+"]를 저장품에서 조회 시작 ";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		rtnMsg = DaoManager.getYdStock(recPara, rsResult, 0);
		if( !rtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			return rtnMsg;
		}
		
		rsResult.first();
		JDTORecord recTemp = rsResult.getRecord();
		
		String ydMtlLGp 	   = ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");			//크레인작업 최하단재료의 길이구분
		String ydMtlWGp 	   = ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_W_GP");			//크레인작업 최하단재료의 폭구분
		String ydPilingCd 	   = ydDaoUtils.paraRecChkNull(recTemp, "YD_PILING_CD");			//크레인작업 최하단재료의 Piling코드

		String ydRcptPlnStrLoc = ydDaoUtils.paraRecChkNull(recTemp, "YD_RCPT_PLN_STR_LOC");//입고예정위치
		 
		
		logMsg = "["+ operationName +"] 크레인작업재료의 최하단 재료정보["+stlNo+"]를 저장품에서 조회 완료 "
				+ "- 길이구분["+ydMtlLGp+"], 폭구분["+ydMtlWGp+"], Piling코드["+ydPilingCd+"], 입고예정위치 ["+ydRcptPlnStrLoc+"]";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	권하중이거나 권상중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		String ydUpWoLoc	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");		
		String ydUpWoLayer 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER");
		String ydDnWoLoc    = "";
		String ydDnWoLayer  = "";
		String ydUpStkColGp = "";
		String ydUpStkBedNo = "";
		boolean isUpLocUpdateNeed = false;
		
		if( ydUpWoLoc.isEmpty() ) {
			logMsg = "["+ operationName +"] 크레인작업재료의 최하단 재료정보["+stlNo+"]에 대한 권하 또는 권상위치 조회 시작 ";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			rtnMsg = YdCommonUtils.getYdStkLyrWithMtlStat(recPara, rsResult, ydStkLyrMtlStat);
			
			if( !rtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				return rtnMsg;
			}
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			logMsg = "["+ operationName +"] 크레인작업재료의 최하단 재료정보["+stlNo+"]에 대한 권하 또는 권상위치 조회 완료  ";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			
			ydUpStkColGp 		= recTemp.getFieldString("YD_STK_COL_GP");
			ydUpStkBedNo 		= recTemp.getFieldString("YD_STK_BED_NO");
			ydUpWoLoc 			= ydUpStkColGp + ydUpStkBedNo;
			ydUpWoLayer 		= recTemp.getFieldString("YD_STK_LYR_NO");
			
			isUpLocUpdateNeed			= true;
			
			logMsg = "["+ operationName +"] 조회된 권상지시위치["+ydUpWoLoc+"], 권상지시단["+ydUpWoLayer+"]";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		}else{
			ydUpStkColGp = ydUpWoLoc.substring(0, 6);
			ydUpStkBedNo = ydUpWoLoc.substring(6);
			
			logMsg = "["+ operationName +"] 크레인스케줄에 등록된 권상지시위치["+ydUpWoLoc+"], 권상지시단["+ydUpWoLayer+"]";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		}
		
				
		//특정 조건에 대한 예외처리 후 return 
		/*
		 * 2014.03.25 윤재광
		 * 2후판 제품창고 사내절단장 북아웃 요구시 디폴트 저장위치로 TO위치 결정
		 */
		if("TB010101".equals(ydToLocGuide)||
		   "TB033101".equals(ydToLocGuide)||
		   "TB032801".equals(ydToLocGuide)  //TB033101->TB032801로 변경 요청. 2022.11.30  후판품질팀 서승범 책임. 1673
		   ){
			ydDnWoLoc		= ydDnStkColGp + ydDnStkBedNo;
			ydDnWoLayer 	= "001";
			
			JDTORecord recUpdCrnSch = JDTORecordFactory.getInstance().create();
			recUpdCrnSch.setField("LOG_ID", logId);
			
			recUpdCrnSch.setField("YD_CRN_SCH_ID"			, ydCrnSchId);
			recUpdCrnSch.setField("YD_EQP_ID"				, ydEqpId);
			recUpdCrnSch.setField("YD_TO_LOC_GUIDE"			, ydToLocGuide);
			recUpdCrnSch.setField("YD_SCH_ST_GP"			, ydSchStGp);
			recUpdCrnSch.setField("YD_UP_WO_LOC"			, ydUpWoLoc);
			recUpdCrnSch.setField("YD_UP_WO_LAYER"			, ydUpWoLayer);
			recUpdCrnSch.setField("YD_DN_WO_LOC"			, ydDnWoLoc);
			recUpdCrnSch.setField("YD_DN_WO_LAYER"			, ydDnWoLayer);
			recUpdCrnSch.setField("IS_UP_LOC_UPDATE_NEED"	, isUpLocUpdateNeed);
			recUpdCrnSch.setField("MAX_MTL_L"	, ydEqpWrkMaxL);
							
			return updCrnSchDnLoc(recUpdCrnSch, rsCrnwrkmtl);
			
		} else if("TCRTUT45".equals(ydToLocGuide)||
				 "TCRTUT13".equals(ydToLocGuide)||
				 "TC010101".equals(ydToLocGuide)||
				 "TC010103".equals(ydToLocGuide)|| // 2020.10.14 (김도훈 매니저 요청, TESTPLATE To위치 지정 베드)
				 "TC010201".equals(ydToLocGuide)||
				 "TC010301".equals(ydToLocGuide)||
				 "TC010303".equals(ydToLocGuide)||
				 ("TCRTRA30".equals(ydToLocGuide)&&!ydRcptPlnStrLoc.startsWith("TC"))||
				 ("TCRTRA40".equals(ydToLocGuide)&&!ydRcptPlnStrLoc.startsWith("TC"))||
				 ("TCRTRB30".equals(ydToLocGuide)&&!ydRcptPlnStrLoc.startsWith("TC"))||
				 ("TCRTRB40".equals(ydToLocGuide)&&!ydRcptPlnStrLoc.startsWith("TC"))){
			  
			JDTORecordSet rsDanPok 	= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recDanPok 	= JDTORecordFactory.getInstance().create();
			recDanPok.setField("YD_CRN_SCH_ID", ydCrnSchId);
			recDanPok.setField("TO_LOC"		  , ydToLocGuide);
			
			String sGbn = "";
			if("TCRTUT45".equals(ydToLocGuide)){
				sGbn = "RA";
			}else{
				sGbn = "UT";
			}
			recDanPok.setField("GBN", sGbn);

			rtnMsg = DaoManager.getToLocWithDanPok(recDanPok, rsDanPok, 2);
	    	
	    	if( rtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    		
	    		rsDanPok.first();
	    		
	    		recDanPok			= rsDanPok.getRecord();
	    		
	    		ydDnStkColGp	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_COL_GP"); 
	    		ydDnStkBedNo	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_BED_NO"); 
	    		ydDnWoLoc	 	= ydDnStkColGp + ydDnStkBedNo;
	    		ydDnWoLayer 	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_LYR_NO");
	    		
	    		JDTORecord recUpdCrnSch = JDTORecordFactory.getInstance().create();
				recUpdCrnSch.setResultCode(logId);
				
				recUpdCrnSch.setField("YD_CRN_SCH_ID"			, ydCrnSchId);
				recUpdCrnSch.setField("YD_EQP_ID"				, ydEqpId);
				recUpdCrnSch.setField("YD_TO_LOC_GUIDE"			, ydToLocGuide);
				recUpdCrnSch.setField("YD_SCH_ST_GP"			, ydSchStGp);
				recUpdCrnSch.setField("YD_UP_WO_LOC"			, ydUpWoLoc);
				recUpdCrnSch.setField("YD_UP_WO_LAYER"			, ydUpWoLayer);
				recUpdCrnSch.setField("YD_DN_WO_LOC"			, ydDnWoLoc);
				recUpdCrnSch.setField("YD_DN_WO_LAYER"			, ydDnWoLayer);
				recUpdCrnSch.setField("IS_UP_LOC_UPDATE_NEED"	, isUpLocUpdateNeed);
				recUpdCrnSch.setField("MAX_MTL_L"	, ydEqpWrkMaxL);
				
				return updCrnSchDnLoc(recUpdCrnSch, rsCrnwrkmtl);
	    		
	    	}else{
	    		return YdConstant.RETN_NOT_EXIST_BED;
	    	}
	    	
		}
		
		//특정 조건에 대한 예외처리 종료 
		YdStkLocVO		ydStkLocVO		= null;
		String szCflag     = "";          //AT000 물류시스템 개선 2022.11.17
		
		//24.08.28 REQ202408611589 허동수 책임 요청 후판 제품장 입고시 후순위적치율 집계 및 화면 구축
				//"P" : 파일링코드에 의해 
				//"S" : 혼적베드
				//"G" : 동일사이즈 일반베드
				//"E" : 공베드
				//"T"  : 1후판 가적베드
				//"I" : 단일파일링
				//"A" : 기타 (to위치 guide 혹은 다른..)
				//"X" : 위치 못찾음
		String plnLocDcsnGp  = "X";  //예정위치결정구분  (파일링코드에 의해, 동일사이즈 베드, 공베드, 위치 못찾음) 
				
		
		// 입고시 예정위치정보는 SKIP
		// TO위치GUIDE에 대한 베드 적치 가능 여부 확인
		if( ydToLocGuide.length() == 8 && 
		   !ydSchCd.substring(6, 7).equals(YdConstant.YD_GNT_GP_RCPT)) {
			
			logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]과 권상지시적치열["+ydUpStkColGp+"]의 동이 같은 지 비교 시작";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
			//	To위치 guide가 적치 가능한지 확인. 적치 가능하다면 VO 에 데이터. 불가능시 VO null
			//----------------------------------------------------------------------------------------------------------------------
		
			ydStkLocVO = getbedwithtolocguide(ydUpStkColGp,ydPilingCd,recWbook,rsCrnwrkmtl,logId);
			
			
			if(ydStkLocVO != null){
				plnLocDcsnGp = ydStkLocVO.getPlnLocDcsnGp();
				updCrnschToLocFindMethodBySchId(ydCrnSchId,plnLocDcsnGp,logId);
				
				String ydEqpGp = ydUpWoLoc.substring(2,4);
				ydDnStkColGp   		= ydStkLocVO.getYdStkColGp();
				ydDnStkBedNo 	    = ydStkLocVO.getYdStkBedNo();
				ydDnWoLoc		    = ydDnStkColGp + ydDnStkBedNo;
				ydDnWoLayer 		= ydStkLocVO.getYdStkLyrNo();
				
				//자동화 소폭재 예외처리
				if ("TBCRB3".equals(ydEqpId) || "TBCRB4".equals(ydEqpId)){	
				   if (("RT".equals(ydEqpGp)) && Double.parseDouble(ydEqpWrkMaxW) < 2250 ){
					   
					   //----------------------------------------------------------------------------------------------------------------------
					   //	이적스판검색범위
					   //----------------------------------------------------------------------------------------------------------------------
					   JDTORecord moveSpanRangeRecord = JDTORecordFactory.getInstance().create();
					   
					   moveSpanRangeRecord.setField("YD_CRN_SCH_ID", ydCrnSchId);
					   moveSpanRangeRecord.setField("YD_EQP_GP", ydEqpGp);
					   moveSpanRangeRecord.setField("YD_SCH_CD", ydSchCd);
					   moveSpanRangeRecord.setField("YD_BAY_GP", ydUpWoLoc.substring(1, 2));
					   moveSpanRangeRecord.setField("YD_EQP_ID", ydEqpId);
					   
					   //이적스판 검색범위 검색 
					   JDTORecord moveSpanRangeResult = findMoveSpanSearchRange(moveSpanRangeRecord);
					   
					   String fromDong = moveSpanRangeResult.getFieldString("FIND_FROM_LOC");
					   String toDong = moveSpanRangeResult.getFieldString("FIND_TO_LOC");
								   
					   if( PlateGdsYdUtil.isApplyYn("소폭제 중간 BED 사용 신규로직 적용 여부") ){
					       if ("S".equals(ydMtlWGp.substring(0,1))){
							   JDTORecordSet   rsStopover   = JDTORecordFactory.getInstance().createRecordSet("");
							   String fromColGp="";
							   String toColGp ="";
							   
							   ydToLocGuide = ydDnWoLoc;
								   
							   fromColGp ="TB" + fromDong;  //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리
							   toColGp =  "TB" + toDong; //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리	   
       
							   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 경유위치 결정대상New : From Bed :" + fromColGp + " To Bed : " + toColGp;
							   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
							   
							   JDTORecord recParam = JDTORecordFactory.getInstance().create();
							   recParam.setField("FROM_COL_GP", fromColGp); 
							   recParam.setField("TO_COL_GP",   toColGp);
							   recParam.setField("MTL_L_GP",    ydMtlLGp);
							        			
							   int rtnVal = commDao.select(recParam, rsStopover, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectStopOverQuery");  
							   if (rtnVal <= 0){
								   logMsg = "["+operationName+"] 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정 실패!!] intRtnVal : " + rtnVal;
							   	   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
							       return YdConstant.RETN_NOT_EXIST_BED;
							   	}
							  	
							   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정S 성공 intRtnVal : " + rtnVal;
							   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
							   
							   ydDnStkColGp = rsStopover.getRecord(0).getFieldString("YD_STK_COL_GP"); 
							   ydDnStkBedNo = rsStopover.getRecord(0).getFieldString("YD_STK_BED_NO");  
							   ydDnWoLayer   = PlateGdsYdUtil.trim(rsStopover.getRecord(0).getFieldString("YD_STK_LYR_NO"));
							   ydDnWoLoc     = ydDnStkColGp + ydDnStkBedNo;				   
							   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정E 성공 : " + ydDnStkColGp + " szCflag : " + szCflag;
							   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
						    }
					   }
					   else {
						   if ("S0".equals(ydMtlWGp)){
							   JDTORecordSet   rsStopover   = JDTORecordFactory.getInstance().createRecordSet("");
							   ydToLocGuide = ydDnWoLoc;
								
							   String fromColGp="";
							   String toColGp ="";
							    	    	
							   fromColGp ="TB" + fromDong;  //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리
							   toColGp =  "TB" + toDong; //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리	   
							    	        
							   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 경유위치 결정대상 : From Bed :" + fromColGp + " To Bed : " + toColGp;
							   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
							   
							   JDTORecord recParam = JDTORecordFactory.getInstance().create();
							   recParam.setField("FROM_COL_GP", fromColGp); 
							   recParam.setField("TO_COL_GP",   toColGp);
							   recParam.setField("MTL_L_GP",    ydMtlLGp);
							       			
							   int rtnVal = commDao.select(recParam, rsStopover, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectStopOverQuery");  
							   if (rtnVal <= 0){
								   logMsg = "["+operationName+"] 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정 실패!!] intRtnVal : " + rtnVal;
							      ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
							      return YdConstant.RETN_NOT_EXIST_BED;
							   }
							  	
							   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정S 성공 intRtnVal : " + rtnVal;
							   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
							   
							   ydDnStkColGp = rsStopover.getRecord(0).getFieldString("YD_STK_COL_GP"); 
							   ydDnStkBedNo = rsStopover.getRecord(0).getFieldString("YD_STK_BED_NO");  
							   ydDnWoLayer   = PlateGdsYdUtil.trim(rsStopover.getRecord(0).getFieldString("YD_STK_LYR_NO"));
							   ydDnWoLoc     = ydDnStkColGp + ydDnStkBedNo;				   
							   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정E 성공 : " + ydDnStkColGp + " szCflag : " + szCflag;
							   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
						    }
					    }
					}
				}
				
				JDTORecord recUpdCrnSch = JDTORecordFactory.getInstance().create();
				recUpdCrnSch.setResultCode(logId);
				
				recUpdCrnSch.setField("YD_CRN_SCH_ID"			, ydCrnSchId);
				recUpdCrnSch.setField("YD_EQP_ID"				, ydEqpId);
				recUpdCrnSch.setField("YD_TO_LOC_GUIDE"			, ydToLocGuide);
				recUpdCrnSch.setField("YD_SCH_ST_GP"			, ydSchStGp);
				recUpdCrnSch.setField("YD_UP_WO_LOC"			, ydUpWoLoc);
				recUpdCrnSch.setField("YD_UP_WO_LAYER"			, ydUpWoLayer);
				recUpdCrnSch.setField("YD_DN_WO_LOC"			, ydDnWoLoc);
				recUpdCrnSch.setField("YD_DN_WO_LAYER"			, ydDnWoLayer);
				recUpdCrnSch.setField("IS_UP_LOC_UPDATE_NEED"	, isUpLocUpdateNeed);
				recUpdCrnSch.setField("MAX_MTL_L"	, ydEqpWrkMaxL);
				return updCrnSchDnLoc(recUpdCrnSch, rsCrnwrkmtl);
				
			}
			//선별작업인데 to위치 guide 에 적치 불가능할 경우 실패 
			else if( ydSchCd.substring(2, 4).equals("SL")) {	
				logMsg = "["+ operationName +"] 권하위치검색 실패 - 선별은 야드To위치Guide만 처리됨";
				ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
				return YdConstant.RETN_NOT_EXIST_BED;
			}
			
			
		}else{
			
			if( ydSchCd.substring(6, 7).equals("L")) { 
				
				if( ydToLocGuide.substring(1, 2).equals(ydUpStkColGp.substring(1, 2)) )	{
				}else{
					
					logMsg = "["+ operationName +"] 입고시 야드To위치Guide["+ydToLocGuide+"]가 권상위치["+ydUpStkColGp+"]가 상이합니다. To위치Guide Clear";
					ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					ydToLocGuide = "";
				}
				
			}else{	
				logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]가 존재하지 않거나 자리수(8)가 맞지 않으므로 해당 TO위치Guide를 검색하지 않습니다.";
				ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			}
		}
		
		//to위치 guide 확인 끝. 입고가적 -> 동일파일링 -> 혼적 -> 공베드 ->단일파일링 베드 검색 
		//입고가적은 1순위.  동일파일링,혼적,공베드,단일파일링은 순서 바꿀수있도록 개발 
		
		String ydGp		= ydUpWoLoc.substring(0, 1);
    	String ydBayGp  = ydUpWoLoc.substring(1, 2);
    	String ydEqpGp	= ydUpWoLoc.substring(2, 4);
		
    	recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("YD_TO_LOC_GUIDE", 	ydToLocGuide);
    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(ydEqpWrkSh));		//크레인작업재료 총매수
    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(ydEqpWrkWt));		//크레인작업재료 총중량
    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(ydEqpWrkT));			//크레인작업재료 총높이
    	recTemp.setField("YD_SCH_CD", 			ydSchCd);							//크레인스케줄코드
    	recTemp.setField("YD_PILING_CD", 		ydPilingCd);						//크레인작업 최하단재료의 Piling Code
    	recTemp.setField("YD_MTL_L_GP", 		ydMtlLGp);							//크레인작업 최하단재료의 길이구분
    	recTemp.setField("YD_MTL_W_GP", 		ydMtlWGp);							//크레인작업 최하단재료의 폭구분
    	recTemp.setField("YD_STK_COL_GP", 		ydUpStkColGp);						//권상지시위치 - 적치열
    	recTemp.setField("YD_EQP_ID", 			ydEqpId);							//크레인설비ID
    	recTemp.setField("YD_UP_WO_LOC", 			ydUpWoLoc);
    	recTemp.setField("STL_NO", 			stlNo);
    	recTemp.setField("YD_CRN_SCH_ID", 			ydCrnSchId);
    	
    	JDTORecord moveSpanDirRecord = JDTORecordFactory.getInstance().create();
		   
    	moveSpanDirRecord.setField("YD_EQP_GP", ydEqpGp);
    	moveSpanDirRecord.setField("YD_UP_STK_COL_GP", ydUpStkColGp);
    	moveSpanDirRecord.setField("YD_UP_STK_BED_NO", ydUpStkBedNo);
    	moveSpanDirRecord.setField("YD_TO_LOC_GUIDE", ydToLocGuide);
    	moveSpanDirRecord.setField("YD_BAY_GP", ydBayGp);
    	moveSpanDirRecord.setField("YD_SCH_CD", ydSchCd);  
    	moveSpanDirRecord.setField("LOG_ID", logId);
    	
    	//이적스판 검색방향 검색 
		JDTORecord moveSpanDirResult = findMoveSpanSearchDir(moveSpanDirRecord);
		
		String searchYdStkBedNo = moveSpanDirResult.getFieldString("YD_STK_BED_NO");
		String spanOrder = moveSpanDirResult.getFieldString("SPAN_ORDER");
		String scanDir = moveSpanDirResult.getFieldString("SCAN_DIR");
		
		
		//----------------------------------------------------------------------------------------------------------------------
	    //	이적스판검색범위
	    //----------------------------------------------------------------------------------------------------------------------
	    JDTORecord moveSpanRangeRecord = JDTORecordFactory.getInstance().create();
	   
	    moveSpanRangeRecord.setField("YD_CRN_SCH_ID", ydCrnSchId);
	    moveSpanRangeRecord.setField("YD_EQP_GP", ydEqpGp);
	    moveSpanRangeRecord.setField("YD_SCH_CD", ydSchCd);
	    moveSpanRangeRecord.setField("YD_BAY_GP", ydUpWoLoc.substring(1, 2));
	    moveSpanRangeRecord.setField("YD_EQP_ID", ydEqpId);
	    moveSpanRangeRecord.setField("LOG_ID", logId);
	   
	    //이적스판 검색범위 검색 
	    JDTORecord moveSpanRangeResult = findMoveSpanSearchRange(moveSpanRangeRecord);
	   
	    String fromDong = moveSpanRangeResult.getFieldString("FIND_FROM_LOC");
	    String toDong = moveSpanRangeResult.getFieldString("FIND_TO_LOC");
 	
    	recTemp.setField("FROM_DONG", 			fromDong);
    	recTemp.setField("TO_DONG", 			toDong);
    	    	
    	recTemp.setField("SPAN_ORDER", 			spanOrder);
    	recTemp.setField("SCAN_DIR", 			scanDir);
    	
    	recTemp.setField("YD_STK_BED_NO", 			searchYdStkBedNo);
    	recTemp.setField("LOG_ID"		, 		logId);	
    	
    	
	    
		/////
		String schGp= ydSchCd.substring(4,6); 
    	String sTmpYdGp = "1";
    	//Y4CrnSchCrnSpecCheckDtl 의 스케줄코드 기준 1,2후판 구분 채용
    	if( "RA".equals(schGp)||
				"RB".equals(schGp)||
				"RC".equals(schGp)||
				"10".equals(schGp)||
				"20".equals(schGp)||
				"21".equals(schGp)||
				"22".equals(schGp)||
				"UT".equals(schGp)||
				"01".equals(schGp)||
				"23".equals(schGp)||
				"00".equals(schGp)){   //21,22, UT 추가
					
    				sTmpYdGp = "2";  //구2후판
				}
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
    	recTemp.setField("REPR_CD_GP", "T00072");
    	recTemp.setField("CD_GP", sTmpYdGp + ydSchCd.substring(1,2) + ydSchCd.substring(2,4) + ydSchCd.substring(6,7));
  
    	
    	int rtnVal = commDao.select(recTemp, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdRuleList");
    	
    	String [] toLocFindOrder;
    	
		//"P" : 파일링코드에 의해 
		//"S" : 혼적베드
		//"G" : 동일사이즈 일반베드
		//"E" : 공베드
		//"T"  : 1후판 가적베드 
		//"A" : 기타 
    	//"I" : 단일파일링베드
		//"X" : 위치 못찾음
    	plnLocDcsnGp  = "X";  //예정위치결정구분  (파일링코드에 의해, 동일사이즈 베드, 공베드, 위치 못찾음) 

    	JDTORecord ruleRsTemp = JDTORecordFactory.getInstance().create("");
    	
    	if (rtnVal > 0) {
    		toLocFindOrder = new String[rtnVal+1];
    		
    		//1후판 입고가적베드 탐색은 항상 첫순위
    		toLocFindOrder[0] = "TEMP";
    		logMsg = "["+ operationName +"] 스케줄코드 ["+ ydSchCd +"]의 첫번째 검색순서["+ toLocFindOrder[0] +"] ";
    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
    		
    		for(int i=1; i<=rtnVal; i++){
    			rsResult.absolute(i);
    			ruleRsTemp			= rsResult.getRecord();
    			toLocFindOrder[i] = ruleRsTemp.getFieldString("ITEM");
    			
				logMsg = "["+ operationName +"] ["+ Integer.toString(i+1) +"] 번째 검색순서["+ toLocFindOrder[i] +"] ";
				ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
    		}
		}
    	//기준에 없는경우 디폴트 순서인 동일파일링 + 혼적베드 + 공베드 + 단일파일링코드
    	else{
    		logMsg = "["+ operationName +"] 스케줄코드 ["+ ydSchCd +"]의 순서기준 없는경우 파일링-혼적-공베드 순 검색";
    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			
    		rtnVal = 5;
    		toLocFindOrder = new String[rtnVal];
    		toLocFindOrder[0] = "TEMP";
    		toLocFindOrder[1] = "SAME";
    		toLocFindOrder[2] = "SIMIL";
    		toLocFindOrder[3] = "EMPTY";
    		toLocFindOrder[4] = "SINGL";
    	}
    	
    	for(int i=0; i<toLocFindOrder.length; i++){
    		    		   		
    		ydStkLocVO = callFunctionGetBedForMainWrk(toLocFindOrder[i] , recTemp);
    		
    		if(ydStkLocVO != null) {
    			plnLocDcsnGp = ydStkLocVO.getPlnLocDcsnGp();
    			updCrnschToLocFindMethodBySchId(ydCrnSchId,plnLocDcsnGp,logId);
    			
    			String findMethod = "";
    			if("P".equals(plnLocDcsnGp)){
    				findMethod = "동일파일링코드";
    			}
    			else if("S".equals(plnLocDcsnGp)){
    				findMethod = "혼적베드";
    			}
    			else if("E".equals(plnLocDcsnGp)){
    				findMethod = "공베드";
    			}
    			else if("I".equals(plnLocDcsnGp)){
    				findMethod = "단일파일링";
    			}
    			logMsg = "["+ operationName +"] To위치 찾기 성공 ["+ findMethod +"]";
    			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
    			break;
    		}
    	}
   
    	boolean isAutoB_toXX = false;
		if( ydStkLocVO == null ) {
			if( PlateGdsYdUtil.isApplyYn("입고이적 권하위치(XX00)예외 로직 적용 여부") ){
				//일반적인 협폭재 중간경유 베드 검색의 경우도 여기에(bIS_BED_STACKABLE=False)걸리는지 확인필요
				//bIS_BED_STACKABLE=False이면서 협폭재인 경우, 아래에서 에러 발생.권하위치 셋팅 변수가 null
				 if (("TBCRB3".equals(ydEqpId) || "TBCRB4".equals(ydEqpId)) && ("RT".equals(ydEqpGp) && Double.parseDouble(ydEqpWrkMaxW) <= 2100)){
					isAutoB_toXX=true;  //1후판 B동 소폭재 입고 작업의 최종 TO위치(소폭베드) 못찾은 경우
					logMsg = "["+ operationName +"] 협폭 권하위치검색 실패 - 협폭재 중간경유 BED 검색, isAutoB_toXX:"+isAutoB_toXX;
					ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
				 }
				 else {
					 if(isRcvPlateBigCust(recTemp)) {
						 logMsg = "["+ operationName +"] 권하위치검색 실패 - 대형고객사 적치가능한 베드가 존재하지 않습니다.";
						 ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
						 return YdConstant.RETN_BIG_NOT_EXIST_BED;
						
					 } else {
						 logMsg = "["+ operationName +"] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
						 ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
						return YdConstant.RETN_NOT_EXIST_BED;
					 }
			     }
		     }
		     else {
				 if(isRcvPlateBigCust(recTemp)) {
					 logMsg = "["+ operationName +"] 권하위치검색 실패 - 대형고객사 적치가능한 베드가 존재하지 않습니다.";
					 ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					 return YdConstant.RETN_BIG_NOT_EXIST_BED;
					
				 } else {
					 logMsg = "["+ operationName +"] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
					 ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					return YdConstant.RETN_NOT_EXIST_BED;
				 }	
		     }
		}
		
		if(isAutoB_toXX){//1후판B동 소폭재 중폭베드 입고시, 최종위치(소폭베드) 못찾은 경우는 위치값이 비어있어서, 위치셋팅하면 NULL EXCEPTION 발생함.
			logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 협폭check:Y 이면서, 최종 TO위치 탐색 실패하여, 권하예정지 셋팅 안함(NULL EXCEPTION 방지)";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		}
		else{

			/*250325 임진후기사 요청 RITM0993257
			 * 
			 * 파일링 시, 도중에 높이제한 문제로 권하위치가 변경시, 크레인 크로스 현상 발생 가능성이 있음
			 * (크레인크로스: 기존엔 E1 크레인 작업영역 이었다가 TO위치 변경되면서 E2 크레인 작업영역으로 지시 가능성)
			 * 이때, 한번의 파일링 작업정도는 높이제한보다 높게 쌓아도 문제 없기 때문에, 그 파일링 작업에 한해서 높이초과여도 고정.
			 * 
			 * */
			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", operationName, "APP060", "T", "013");//후판 개발 적용여부
			
			
			//최초 파일링지시의 권하위치 get 
			YdStkLocVO		initYdStkLocVO		= new YdStkLocVO();
			
			if( "Y".equals(sApplyYnPI)
			    &&( "S".equals(ydSchStGp) || "E".equals(ydSchStGp) ) 
                &&ydSchCd.substring(6, 7).equals("L") ){
				initYdStkLocVO = getInitialPilingCrnschIdByCurSchId( ydCrnSchId, logId );
				
				if(initYdStkLocVO != null){
					//해당 권하위치의 베드 에러코드 get 
					JDTORecord  tempParam     = JDTORecordFactory.getInstance().create();
					
					tempParam.setField("YD_STK_COL_GP", initYdStkLocVO.getYdStkColGp());
					tempParam.setField("YD_STK_BED_NO", initYdStkLocVO.getYdStkBedNo());
					tempParam.setField("YD_SCH_CD", ydSchCd);
					tempParam.setField("YD_EQP_WRK_SH", 		ydEqpWrkSh);		//크레인작업재료 총매수
					tempParam.setField("YD_EQP_WRK_WT", 		ydEqpWrkWt);		//크레인작업재료 총중량
					tempParam.setField("YD_EQP_WRK_T", 		ydEqpWrkT);		//크레인작업재료 총높이
					tempParam.setField("LOG_ID", logId);
					
					rtnMsg = procBedStackable(tempParam, null, methodName);
					
					logMsg = "["+ operationName +"] 베드["+initYdStkLocVO.getYdStkColGp()+" - "+initYdStkLocVO.getYdStkBedNo()+"]가 적치가능한 지 확인 완료. 결과코드 ["+rtnMsg+"]";
					ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
				}
				else{
					logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] 최초 파일링지시 존재 안함";
					ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
				}
				
			}
			
			//입고스케줄에 파일링작업이고, 높이제한초과코드면 기존 권하위치 유지
			if( "Y".equals(sApplyYnPI)
			    &&( "S".equals(ydSchStGp) || "E".equals(ydSchStGp) ) 
                &&ydSchCd.substring(6, 7).equals("L")
                &&Integer.toString(YdConstant.YD_BED_ERR_CD_H_OVER).equals(rtnMsg) ) { 
						
				ydDnStkColGp 	= initYdStkLocVO.getYdStkColGp();
				ydDnStkBedNo 	= initYdStkLocVO.getYdStkBedNo();
				ydDnWoLoc		= ydDnStkColGp + ydDnStkBedNo;
				ydDnWoLayer 	= initYdStkLocVO.getYdStkLyrNo();
			}
			else {
				ydDnStkColGp 		= ydStkLocVO.getYdStkColGp();
				ydDnStkBedNo 	= ydStkLocVO.getYdStkBedNo();
				ydDnWoLoc		= ydDnStkColGp + ydDnStkBedNo;
				ydDnWoLayer 	= ydStkLocVO.getYdStkLyrNo();
			}
		
			
		}
		
		//크레인 자동화 소폭 > 중폭 입고이적 체크(TO위치를 기존 소폭 TO위치에서 중폭 경유 TO위치로 변경) 김기태 부장.
		logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 협폭check 시작  대상재 폭 : " + ydEqpWrkMaxW;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 최종목적지 bed :" + ydDnWoLoc;

		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		//자동화 소폭재 예외처리
		if ("TBCRB3".equals(ydEqpId) || "TBCRB4".equals(ydEqpId)){	
		   if (("RT".equals(ydEqpGp)) && Double.parseDouble(ydEqpWrkMaxW) < 2250 ){
			   
				if( PlateGdsYdUtil.isApplyYn("소폭제 중간 BED 사용 신규로직 적용 여부") ){
			       if ("S".equals(ydMtlWGp.substring(0,1))){
					   JDTORecordSet   rsStopover   = JDTORecordFactory.getInstance().createRecordSet("");
					   String fromColGp="";
					   String toColGp ="";
					   
					   ydToLocGuide = ydDnWoLoc;
						   
					   fromColGp ="TB" + fromDong;  //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리
					   toColGp =  "TB" + toDong; //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리	   
		
					   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 경유위치 결정대상New : From Bed :" + fromColGp + " To Bed : " + toColGp;
					   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					   
					   JDTORecord recParam = JDTORecordFactory.getInstance().create();
					   recParam.setField("FROM_COL_GP", fromColGp); 
					   recParam.setField("TO_COL_GP",   toColGp);
					   recParam.setField("MTL_L_GP",    ydMtlLGp);
					        			
					   rtnVal = commDao.select(recParam, rsStopover, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectStopOverQuery");  
					   if (rtnVal <= 0){
						   logMsg = "["+operationName+"] 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정 실패!!] intRtnVal : " + rtnVal;
					   	   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					       return YdConstant.RETN_NOT_EXIST_BED;
					   	}
					  	
					   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정S 성공 intRtnVal : " + rtnVal;
					   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					   
					   ydDnStkColGp = rsStopover.getRecord(0).getFieldString("YD_STK_COL_GP"); 
					   ydDnStkBedNo = rsStopover.getRecord(0).getFieldString("YD_STK_BED_NO");  
					   ydDnWoLayer   = PlateGdsYdUtil.trim(rsStopover.getRecord(0).getFieldString("YD_STK_LYR_NO"));
					   ydDnWoLoc     = ydDnStkColGp + ydDnStkBedNo;				   
					   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정E 성공 : " + ydDnStkColGp + " szCflag : " + szCflag;
					   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
				    }
			   }
			   else {
				   if ("S0".equals(ydMtlWGp)){
					   JDTORecordSet   rsStopover   = JDTORecordFactory.getInstance().createRecordSet("");
					   ydToLocGuide = ydDnWoLoc;
						
					   String fromColGp="";
					   String toColGp ="";
					    	    	
					   fromColGp ="TB" + fromDong;  //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리
					   toColGp =  "TB" + toDong; //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리	   
					    	        
					   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 경유위치 결정대상 : From Bed :" + fromColGp + " To Bed : " + toColGp;
					   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					   
					   JDTORecord recParam = JDTORecordFactory.getInstance().create();
					   recParam.setField("FROM_COL_GP", fromColGp); 
					   recParam.setField("TO_COL_GP",   toColGp);
					   recParam.setField("MTL_L_GP",    ydMtlLGp);
					       			
					   rtnVal = commDao.select(recParam, rsStopover, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectStopOverQuery");  
					   if (rtnVal <= 0){
						   logMsg = "["+operationName+"] 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정 실패!!] intRtnVal : " + rtnVal;
					      ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					      return YdConstant.RETN_NOT_EXIST_BED;
					   }
					  	
					   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정S 성공 intRtnVal : " + rtnVal;
					   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					   
					   ydDnStkColGp = rsStopover.getRecord(0).getFieldString("YD_STK_COL_GP"); 
					   ydDnStkBedNo = rsStopover.getRecord(0).getFieldString("YD_STK_BED_NO");  
					   ydDnWoLayer   = PlateGdsYdUtil.trim(rsStopover.getRecord(0).getFieldString("YD_STK_LYR_NO"));
					   ydDnWoLoc     = ydDnStkColGp + ydDnStkBedNo;				   
					   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정E 성공 : " + ydDnStkColGp + " szCflag : " + szCflag;
					   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
				    }
			    }
		   }
		}
		
		////////////////////////////////////
		//25.06.30 추가 -- 임진후기사 요청.
		//크레인 고장범위 속할 시 대체범위 찾는 부분 RENEW
		//com.inisteel.cim.yd.common.dao.YdPlateCommDao.chkBrLocFor1 쿼리 응용하여, 최종 찾은 TO위치가 N호기 고장범위에 속하고 N호기 고장일시 
		//대체위치로 변경
		if(YdConstant.YD_EQP_GP_ROLLERTABLE.equals(ydEqpGp)|| YdConstant.YD_EQP_GP_TRANSFER.equals(ydEqpGp))  //입고작업 한정체크
    	{
			
			logMsg = "["+operationName+"] 입고작업 스케줄코드 [" + ydSchCd + "] 권하위치 [ " + ydDnWoLoc + "] 고장범위 여부 체크";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			
			JDTORecordSet chkResultSet 	= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord    chkPara    	= JDTORecordFactory.getInstance().create();
			JDTORecord    chkResult 	= JDTORecordFactory.getInstance().create("");
			chkPara.setField("YD_SCH_CD", ydSchCd);
			chkPara.setField("YD_DN_WO_LOC", ydDnWoLoc);
			
			rtnVal=0;  //intRtnVal 초기화
			
			//1후판 고장범위 체크 및 대체범위 반환쿼리
			if("D".equals(ydSchCd.substring(5, 6))||  //D/E/F RT일경우(1후판)
			   "E".equals(ydSchCd.substring(5, 6))||	
			   "F".equals(ydSchCd.substring(5, 6))||
			   "G".equals(ydSchCd.substring(5, 6))
			   )
			{
				rtnVal = commDao.select(chkPara, chkResultSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.chkBrLocByToLocFor1");
			}
			else if("A".equals(ydSchCd.substring(5, 6))||  //A/B RT일경우(2후판)
				    "B".equals(ydSchCd.substring(5, 6))
				    )
			{
				rtnVal = commDao.select(chkPara, chkResultSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.chkBrLocByToLocFor2");
			}
			
			if(rtnVal>0){
				chkResultSet.first();
				chkResult  = chkResultSet.getRecord();
				
				String fnlChk  =ydDaoUtils.paraRecChkNull(chkResult, "FNL_CHK");
				String altFrom =ydDaoUtils.paraRecChkNull(chkResult, "AL_FROM");
				String altTo   =ydDaoUtils.paraRecChkNull(chkResult, "AL_TO");
				
				logMsg = "["+ operationName +"] FNL_CHK:"+fnlChk+", sAL_FROM:"+altFrom+", sAL_TO:"+altTo;
				ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
				
				if("Y".equals(fnlChk)){
					JDTORecord    altPara    	= JDTORecordFactory.getInstance().create();
					
					altPara.setField("YD_EQP_WRK_SH", 		String.valueOf(ydEqpWrkSh));		//크레인작업재료 총매수
					altPara.setField("YD_EQP_WRK_WT", 		String.valueOf(ydEqpWrkWt));		//크레인작업재료 총중량
					altPara.setField("YD_EQP_WRK_T", 		String.valueOf(ydEqpWrkT));			//크레인작업재료 총높이
					altPara.setField("YD_SCH_CD", 			ydSchCd);							//크레인스케줄코드
					altPara.setField("YD_MTL_L_GP", 		ydMtlLGp);							//크레인작업 최하단재료의 길이구분
					altPara.setField("YD_MTL_W_GP", 		ydMtlWGp);							//크레인작업 최하단재료의 폭구분
					altPara.setField("YD_STK_COL_GP",		ydUpStkColGp);
					altPara.setField("YD_STK_BED_NO",		ydUpStkBedNo);
					altPara.setField("AL_FROM", 			altFrom);
					altPara.setField("AL_TO", 			altTo);
					altPara.setField("LOG_ID", 			logId);
			    	
			    				
					//rt에서 가까운순으로 고장범위 내에서 탐색 
					ydStkLocVO = getBedWithAltRangeAndRt(altPara);
					
					if(ydStkLocVO !=null){
						ydDnStkColGp 		= ydStkLocVO.getYdStkColGp();
						ydDnStkBedNo 	= ydStkLocVO.getYdStkBedNo();
						ydDnWoLoc		= ydDnStkColGp + ydDnStkBedNo;
						ydDnWoLayer 	= ydStkLocVO.getYdStkLyrNo();
						
						logMsg = "["+ operationName +"] 고장범위 대체 저장위치 ["+ydDnWoLoc+"], 단 ["+ydDnWoLayer+"]";
						ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					}
					else {
						logMsg = "["+ operationName +"] 권하위치검색 실패 ["+ydSchCd+"] 작업크레인 고장 시 대체위치 검색 실패 .";
						ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
						return YdConstant.RETN_NOT_EXIST_BED;
					}
				}
			}
    	
    	}
		
		///////////////////////////////////
		
		JDTORecord recUpdCrnSch = JDTORecordFactory.getInstance().create();
		recUpdCrnSch.setResultCode(logId);
		
		recUpdCrnSch.setField("YD_CRN_SCH_ID"			, ydCrnSchId);
		recUpdCrnSch.setField("YD_EQP_ID"				, ydEqpId);
		recUpdCrnSch.setField("YD_TO_LOC_GUIDE"			, ydToLocGuide);
		recUpdCrnSch.setField("YD_SCH_ST_GP"			, ydSchStGp);
		recUpdCrnSch.setField("YD_UP_WO_LOC"			, ydUpWoLoc);
		recUpdCrnSch.setField("YD_UP_WO_LAYER"			, ydUpWoLayer);
		recUpdCrnSch.setField("YD_DN_WO_LOC"			, ydDnWoLoc);
		recUpdCrnSch.setField("YD_DN_WO_LAYER"			, ydDnWoLayer);
		recUpdCrnSch.setField("IS_UP_LOC_UPDATE_NEED"	, isUpLocUpdateNeed);
		recUpdCrnSch.setField("MAX_MTL_L"	, ydEqpWrkMaxL);
		return updCrnSchDnLoc(recUpdCrnSch, rsCrnwrkmtl);
	}
	
	
	public static String getOperationMsgFromSchCd(String ydSchCd) {
		if (ydSchCd.length() < 7) return "";
		String ydSpanCode = ydSchCd.substring(2, 4);
		String ydGntGp    = ydSchCd.substring(6, 7);
		String operationMsg				 = "";
		
	    switch (ydSpanCode){
		case YdConstant.YD_EQP_GP_PALLET:
		case YdConstant.YD_EQP_GP_TRAILER:
			operationMsg += "차량";
			break;
		case YdConstant.YD_EQP_GP_ROLLERTABLE:
			operationMsg += "RollerTable";
			break;
		case YdConstant.YD_EQP_GP_TRANSFER:
			operationMsg += "Transfer";
			break;
		case YdConstant.YD_EQP_GP_YARD:
			operationMsg += "야드";
			break;
		case YdConstant.YD_EQP_GP_SORTINGLINE:
			operationMsg += "선별";
			break;
			
		}
	    
		switch (ydGntGp){
		case YdConstant.YD_GNT_GP_RCPT:
			operationMsg += "입고";
			break;
		case YdConstant.YD_GNT_GP_MVSTK:
			operationMsg += "이적";
			break;
		case YdConstant.YD_GNT_GP_ISSUE:
			operationMsg += "출고";
			break;
		}
	    return operationMsg;
	}
	/**
	 * 크레인스케줄 권하지시위치 수정
	 * @param recCrnSch
	 * @return
	 * @throws JDTOException
	 */
	
	public static String updCrnSchDnLoc(JDTORecord recUpdCrnSch, JDTORecordSet rsCrnwrkmtl	) throws JDTOException{
		String methodName				 = "updCrnSchDnLoc";
		String operationName			 = "크레인스케줄 권하지시위치 수정";
		String logMsg					 = null;
		String rtnMsg 				     = YdConstant.RETN_CD_SUCCESS;
		
		String logId                     = ydUtils.getJDTOLogId(recUpdCrnSch, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		logMsg = operationName+"(" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		rsCrnwrkmtl.first();
		JDTORecord recPara = rsCrnwrkmtl.getRecord();
		
		String ydCrnSchId       = ydDaoUtils.paraRecChkNull(recUpdCrnSch,"YD_CRN_SCH_ID");					//크레인스케줄ID
		String ydEqpId          = ydDaoUtils.paraRecChkNull(recUpdCrnSch,"YD_EQP_ID");						//크레인설비ID
		
		int ydEqpWrkSh      	= ydDaoUtils.paraRecChkNullInt(recPara,"SH_CNT");						//크레인작업재료 총매수
		int ydEqpWrkWt      	= ydDaoUtils.paraRecChkNullInt(recPara,"SUM_MTL_WT");					//크레인작업재료 총중량
		double ydEqpWrkT      	= ydDaoUtils.paraRecChkNullDouble(recPara,"SUM_MTL_T");					//크레인작업재료 총높이
		
		String ydEqpWrkMaxW		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_W");						//크레인작업재료 중 최대 폭
		String ydEqpWrkMaxL		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_L");						//크레인작업재료 중 최대 길이 
		
		String ydToLocGuide     = ydDaoUtils.paraRecChkNull(recUpdCrnSch, "YD_TO_LOC_GUIDE");				//to위치 가이드
		
		String ydUpWoLoc		= ydDaoUtils.paraRecChkNull(recUpdCrnSch, "YD_UP_WO_LOC");		
		String ydUpWoLayer 		= ydDaoUtils.paraRecChkNull(recUpdCrnSch, "YD_UP_WO_LAYER");
		String ydDnWoLoc    	= ydDaoUtils.paraRecChkNull(recUpdCrnSch, "YD_DN_WO_LOC");
		String ydDnWoLayer  	= ydDaoUtils.paraRecChkNull(recUpdCrnSch, "YD_DN_WO_LAYER");
		
		String ydUpStkColGp = ydUpWoLoc.substring(0, 6);
		String ydUpStkBedNo = ydUpWoLoc.substring(6);
		
		String ydDnStkColGp = ydDnWoLoc.substring(0, 6);
		String ydDnStkBedNo = ydDnWoLoc.substring(6);
		
		boolean isUpLocUpdateNeed = Boolean.parseBoolean(ydDaoUtils.paraRecChkNull(recUpdCrnSch, "IS_UP_LOC_UPDATE_NEED"));
		
		//----------------------------------------------------------------------------------------------------------------------
		// 권하지시위치 수정
		//----------------------------------------------------------------------------------------------------------------------
		logMsg = "["+ operationName +"] 권하지시위치["+ydDnWoLoc+"], 권하지시단["+ydDnWoLayer+"]을 크레인스케줄에 수정 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		JDTORecord recUpCrnSch	= null;
		
		recUpCrnSch = JDTORecordFactory.getInstance().create();
		recUpCrnSch.setField("YD_CRN_SCH_ID", 			ydCrnSchId);									//크레인스케줄ID
	                                                                       //ATOOO 물류시스템 개선 2022.10.27 최종 to위치
		recUpCrnSch.setField("YD_TO_LOC_GUIDE", 		ydToLocGuide);	                               
		
		recUpCrnSch.setField("YD_EQP_ID", 				ydEqpId);										//크레인설비ID
		recUpCrnSch.setField("YD_UP_STK_COL_GP", 		ydUpStkColGp);								//권상지시위치 - 적치열
		recUpCrnSch.setField("YD_UP_STK_BED_NO", 		ydUpStkBedNo);								//권상지시위치 - 적치베드
		recUpCrnSch.setField("YD_DN_STK_COL_GP", 		ydDnStkColGp);								//권하지시위치 - 적치열
		recUpCrnSch.setField("YD_DN_STK_BED_NO", 		ydDnStkBedNo);								//권하지시위치 - 적치베드
		
		//----------------------------------------------------------------------------------------------------------------------
		//	권상지시위치 업데이트
		//----------------------------------------------------------------------------------------------------------------------
		if( isUpLocUpdateNeed ) {
			logMsg = "["+ operationName +"] 권상지시위치["+ydUpWoLoc+"], 권상지시단["+ydUpWoLayer+"] 크레인스케줄에 등록";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			
			recUpCrnSch.setField("YD_UP_WO_LOC", 		ydUpWoLoc);									//권상지시위치
			recUpCrnSch.setField("YD_UP_WO_LAYER", 		ydUpWoLayer);									//권상지시단
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		recUpCrnSch.setField("YD_DN_WO_LOC", 			ydDnWoLoc);									//권하지시위치
		recUpCrnSch.setField("YD_DN_WO_LAYER", 			ydDnWoLayer);									//권하지시단
		recUpCrnSch.setField("YD_EQP_WRK_SH", 			String.valueOf(ydEqpWrkSh));					//크레인작업재료 총매수
		recUpCrnSch.setField("YD_EQP_WRK_WT", 			String.valueOf(ydEqpWrkWt));					//크레인작업재료 총중량
		recUpCrnSch.setField("YD_EQP_WRK_T", 			String.valueOf(ydEqpWrkT));					//크레인작업재료 총높이
		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 		ydEqpWrkMaxW);								//크레인작업재료 중 최대 폭
		recUpCrnSch.setField("YD_EQP_WRK_MAX_L", 		ydEqpWrkMaxL);								//크레인작업재료 중 최대 길이
		recUpCrnSch.setField("MODIFIER", 				methodName.length() > 10 ? methodName.substring(0, 10) : methodName);
		
		rtnMsg = CrnSchUtil.uptCrnSchXYCord(recUpCrnSch);
    	
		logMsg = "["+ operationName +"] 권하지시위치["+ydDnWoLoc+"], 권하지시단["+ydDnWoLayer+"]을 크레인스케줄에 수정 완료";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
    	
		//----------------------------------------------------------------------------------------------------------------------
		//	권하지시위치에 재료를 권하대기로 등록
		//----------------------------------------------------------------------------------------------------------------------
		logMsg = "["+ operationName +"] 권하지시위치["+ydDnWoLoc+"], 권하지시단["+ydDnWoLayer+"]에 크레인작업재료 등록 시작";
    	ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
    	rtnMsg	= uptDnWaitOnYdStkLyr(rsCrnwrkmtl, ydDnWoLoc, ydDnWoLayer);
    	
		logMsg = "["+ operationName +"] 권하지시위치["+ydDnWoLoc+"], 권하지시단["+ydDnWoLayer+"]에 크레인작업재료 등록 완료 - 메세지 : " + rtnMsg;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		return rtnMsg;
	}
	

	/**
	 * 파일링코드로 to위치 guide 가 적치 가능한지 검사
	 * @param ydToLocGuide
	 * @param ydPilingCd
	 * @param logId
	 * @return
	 * @throws JDTOException
	 */
	
	public static YdStkLocVO getbedwithtolocguide(String ydUpStkColGp, String ydPilingCd,JDTORecord recWbook, JDTORecordSet rsCrnwrkmtl, String logId) throws JDTOException{
		String methodName				 = "chkToLocGuideStackableByYdPilingCd";
		String operationName			 = "파일링코드로 to위치 guide 가 적치 가능한지 검사";
		String logMsg					 = null;
		String rtnMsg					 = null;
		
		YdStkLocVO		ydStkLocVO		= null;
		
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		
		logMsg = operationName+"(" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		
		String ydToLocGuide =  ydDaoUtils.paraRecChkNull(recWbook, "YD_TO_LOC_GUIDE");
		String ydSchCd   	= ydDaoUtils.paraRecChkNull(recWbook, "YD_SCH_CD");
		
		int ydEqpWrkSh      	= ydDaoUtils.paraRecChkNullInt(recPara,"SH_CNT");						//크레인작업재료 총매수
		int ydEqpWrkWt      	= ydDaoUtils.paraRecChkNullInt(recPara,"SUM_MTL_WT");					//크레인작업재료 총중량
		double ydEqpWrkT      	= ydDaoUtils.paraRecChkNullDouble(recPara,"SUM_MTL_T");					//크레인작업재료 총높이
		
		
		
		logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]과 권상지시적치열["+ydUpStkColGp+"]의 동이 같은 지 비교 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	TO위치가이드와 권상지시위치의 동이 다른 경우에는 TO위치가이드가 적치가능한 지를 체크하는 부분을 SKIP한다.
		//----------------------------------------------------------------------------------------------------------------------
		boolean isSameBay	= false;
		
		if( ydToLocGuide.substring(1, 2).equals(ydUpStkColGp.substring(1, 2)) )	{
			
			isSameBay		= true;
			logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]과 권상지시적치열["+ydUpStkColGp+"]의 동이 같은 지 비교 완료 - 동이 같음";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			
		}else{
			logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]과 권상지시적치열["+ydUpStkColGp+"]의 동이 같은 지 비교 완료 - 동이 다름";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		}
		
		if(!isSameBay){
			return null;
		}
		
				
		//----------------------------------------------------------------------------------------------------------------------
		//	일반야드인 경우에는 입고, 이적인 경우 최하단 크레인작업재료와 TO위치가이드위치의 Bed Size가 다르면 제외
		//	베드의 야드적치Bed입출고상태가 E(입출고가능)가 아니면 베드 제외
		//	수정일 : 2010.03.10 - 임춘수
		//----------------------------------------------------------------------------------------------------------------------
		boolean isToLocStackable	= false;
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		recPara = JDTORecordFactory.getInstance().create();
		
		String ydStkBedWGp = "";
		
		if( ydToLocGuide.substring(2, 4).matches("\\d\\d") ) {	//일반야드는 숫자이므로 설비는 숫자가 아님
			
			//일반야드인 경우에는 입고, 이적인 경우 최하단 크레인작업재료와 TO위치가이드위치의 Bed Size가 다르면 제외
			//베드의 야드적치Bed입출고상태가 E(입출고가능)가 아니면 베드 제외
			
			logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]가 적치가능한 size[폭,길이]와 야드적치Bed입출고상태를 체크하기 위해 조회 시작";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			
			recPara.setField("YD_STK_COL_GP", 	ydToLocGuide.substring(0, 6));
			recPara.setField("YD_STK_BED_NO", 	ydToLocGuide.substring(6));
	    	
	    	rtnMsg			= DaoManager.getYdStkbed(recPara, rsResult, 0);
	    	
	    	if(!rtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
	    		logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"] 베드정보 조회 존재하지 않음 ";
				ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
	    		return null;
	    	}
	    				    	
    		rsResult.first();
    		
    		recPara			= rsResult.getRecord();
    		
    		String ydStkBedLGp			= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_L_GP");
    		ydStkBedWGp			= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_W_GP");
    		String ydStkBedWhioStat= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_WHIO_STAT");
    		YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
    		
    		if( ydStkBedWhioStat.equals(YdConstant.YD_STK_BED_WHIO_ENABLE) ) { //입출고 상태 적치 가능
    			
    			logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]의 야드적치Bed입출고상태가 입출고가능하므로 적치가능하므로 재료와 베드의 size비교 시작";
    			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
				
				/*25.02.17 임진후기사 요청 TO위치 지정시, 재료의 폭 구분이 권하위치의 폭구분과 다르다 할지라도 권하 되게끔 요청
				 * szYD_PILING_CD : 크레인 최하단재료의 파일링코드. 
				 * 
				 * 
				 * 소폭 -> 중폭,광폭 가능
				 * 중폭 -> 중폭,광폭 가능
				 * 광폭 -> 광폭 가능
				 * 
				 * */
				
				String sApplyYnPI = ydPICommDAO.ApplyYnPI("", operationName, "APP060", "T", "011");//후판 개발 적용여부
				
				if( ydStkBedLGp.length() == 1 && 
						ydStkBedWGp.length() == 1 ) {
					
	    			if( ydPilingCd.substring(4, 5).equals(ydStkBedWGp) && 
	    					ydPilingCd.substring(6, 7).equals(ydStkBedLGp) ) {
	    				logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]에 폭구분["+ydStkBedWGp+"], 길이구분["+ydStkBedLGp+"]과 최하단 크레인작업재료의 SIZE["+ydPilingCd+"]가 동일하므로 적치가능";
	    				ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
						
	    				isToLocStackable			= true;
	    			}else{
	    				/*25.02.17 임진후기사 요청 TO위치 지정시, 재료의 폭 구분이 권하위치의 폭구분과 다르다 할지라도 권하 되게끔 요청 */
	    				if("Y".equals(sApplyYnPI)){
	    					String stlWgp = ydPilingCd.substring(4, 5);
	    					if("S".equals(stlWgp) && ("S".equals(ydStkBedWGp) || "M".equals(ydStkBedWGp) ||("L".equals(ydStkBedWGp) ) )){
	    						logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]에 폭구분["+ydStkBedWGp+"], 길이구분["+ydStkBedLGp+"]과 최하단 크레인작업재료의 SIZE["+ydPilingCd+"]의 폭구분 ["+stlWgp+"] 다르지만, 소폭->소/중/광폭 허용으로 작업가능";
	    						ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
								
	    						isToLocStackable			= true;
	    					}
	    					else if("M".equals(stlWgp) && ("M".equals(ydStkBedWGp) ||("L".equals(ydStkBedWGp) ) )){
	    						logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]에 폭구분["+ydStkBedWGp+"], 길이구분["+ydStkBedLGp+"]과 최하단 크레인작업재료의 SIZE["+ydPilingCd+"]의 폭구분 ["+stlWgp+"] 다르지만, 중폭->중/광폭 허용으로 작업가능";
	    						ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
								
	    						isToLocStackable			= true;
	    					}					    					
	    					
	    						
	    				}
	    				else {
	    					logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]에 폭구분["+ydStkBedWGp+"], 길이구분["+ydStkBedLGp+"]과 최하단 크레인작업재료의 SIZE["+ydPilingCd+"]가 동일하지 않으므로 적치불가능";
	    					ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
	    				}
	    			}
	    			
	    		}else{
	    			logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]에 폭구분["+ydStkBedWGp+"], 길이구분["+ydStkBedLGp+"]이 존재하지 않습니다.";
	    			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
	    		}
				
    		}else{
    			logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]의 야드적치Bed입출고상태가 입출고가능하지 않으므로 적치불가능";
    			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
    		}
	    	
		}else{
			isToLocStackable				= true;
		}
		
		if( ydSchCd.substring(2, 4).equals("SL")) {	//선별 작업은 설비와 동일하게 처리
			logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]의 선별작업입니다.";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			isToLocStackable				= true;
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	TO위치가이드와 권상지시위치의 동이 같으므로 TO위치가이드가 적치가능한 지를 비교
		//----------------------------------------------------------------------------------------------------------------------
		
		ArrayList listToLoc = new ArrayList();
		String ydDnStkColGp     = ydToLocGuide.substring(0, 6);
		String ydDnStkBedNo		= ydToLocGuide.substring(6);
		if( isToLocStackable ) {
			
			logMsg = "["+ operationName +"] 권하지시적치열["+ydDnStkColGp+"], 권하지시베드["+ydDnStkBedNo+"]에 적치가능한 지 비교 시작";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_TO_LOC_GUIDE", 	ydToLocGuide);
	    	recPara.setField("YD_EQP_WRK_SH", 		String.valueOf(ydEqpWrkSh));						//크레인작업재료 총매수
	    	recPara.setField("YD_EQP_WRK_WT", 		String.valueOf(ydEqpWrkWt));						//크레인작업재료 총중량
	    	recPara.setField("YD_EQP_WRK_T", 		String.valueOf(ydEqpWrkT));						//크레인작업재료 총높이
	    	recPara.setField("YD_SCH_CD", 			ydSchCd);


	    	recPara.setField("LOG_ID", logId);

	    	if( ydSchCd.substring(2, 4).equals("SL")) {	//선별 작업은  RT 가적배드와 동일하게
	    		rtnMsg = YdToLocDcsnUtil.procAsgnedBedStackable_rt(recPara, listToLoc, methodName);
	    	} else {
	    		rtnMsg = YdToLocDcsnUtil.procAsgnedBedStackable(recPara, listToLoc, methodName);
	    	}
	    	if( rtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    		
	    		ydStkLocVO = (YdStkLocVO)listToLoc.get(0);
	    		ydStkLocVO.setSeq(YdConstant.TO_LOC_PRIOR_USER);
	    		ydStkLocVO.setPrior(YdConstant.TO_LOC_PRIOR_USER);
	    		
	    		
	    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
										
	    			logMsg = "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		    		logMsg = "["+ operationName +"] +++++++++++++++ To위치 guide 베드 ["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
		    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		    		logMsg = "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
		    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		    		
		    		//to위치 탐색방법 setting
					ydStkLocVO.setPlnLocDcsnGp("A");
		    		
					logMsg = "["+operationName+"] 최종목적지 bed :" + ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo() + "목적지 폭구분 : " + ydStkBedWGp ;	
				}
	    	}
	    	
	    	logMsg = "["+ operationName +"] 권하지시적치열["+ydDnStkColGp+"], 권하지시베드["+ydDnStkBedNo+"]에 적치가능한 지 비교 완료 - 메세지 : " + rtnMsg;
	    	ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		}else{
			logMsg = "["+ operationName +"] 권하지시적치열["+ydDnStkColGp+"], 권하지시베드["+ydDnStkBedNo+"]에 적치불가능하므로 적치가능비교하지 않음";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		}
		//----------------------------------------------------------------------------------------------------------------------

		return ydStkLocVO;
	}
	
	/**
	 * 이적스판검색범위 탐색 
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws JDTOException
	 */
	
	public static JDTORecord findMoveSpanSearchRange(JDTORecord findRecord	) throws JDTOException{
		String methodName				 = "findMoveSpanSearchRange";
		String operationName			 = "이적스판검색범위 탐색";
		String logMsg					 = null;

		YdPlateCommDAO commDao = new YdPlateCommDAO(); 
		YdEqpDao	ydEqpDao 	= new YdEqpDao();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();	
		
		JDTORecord rsResult = JDTORecordFactory.getInstance().create();
		
		JDTORecordSet outResultSet 	= JDTORecordFactory.getInstance().createRecordSet("");		
		JDTORecord recPara          = JDTORecordFactory.getInstance().create();
		JDTORecord tempResult          = JDTORecordFactory.getInstance().create();
		
		int rtnVal = 0;
		
		String findFromLoc = "";
		String findToLoc   = "";
		
		String ydCrnSchId = ydDaoUtils.paraRecChkNull(findRecord,"YD_CRN_SCH_ID");
		String ydEqpGp 	  = ydDaoUtils.paraRecChkNull(findRecord,"YD_EQP_GP");
		String ydSchCd    = ydDaoUtils.paraRecChkNull(findRecord,"YD_SCH_CD");
		String ydBayGp    = ydDaoUtils.paraRecChkNull(findRecord,"YD_BAY_GP");
		String ydEqpId    = ydDaoUtils.paraRecChkNull(findRecord,"YD_EQP_ID");
		
		String ydInRt = ydSchCd.substring(5, 6);
		
		String ydEqpNum = "";
		
		if(ydEqpId.length() >=6){
			ydEqpNum = ydEqpId.substring(5, 6);
		}

		
		String logId                     = ydUtils.getJDTOLogId(findRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
		
		logMsg = operationName+"(" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);

	
		
		//신입고 로직으로 to위치 검색
		if(YdConstant.YD_EQP_GP_ROLLERTABLE.equals(ydEqpGp) || YdConstant.YD_EQP_GP_TRANSFER.equals(ydEqpGp) ){
			logMsg="신입고 로직으로 TO위치 검색";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
			//	이적스판검색범위
			//  T00241, 동구분, 스케줄코드, 작업크레인 으로 크레인별 위치할당구역 FIND
			//----------------------------------------------------------------------------------------------------------------------
			outResultSet 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			tempResult 	= JDTORecordFactory.getInstance().create();

			recPara.setField("REPR_CD_GP" ,"T00241");	
			recPara.setField("YD_SCH_CD" ,ydSchCd);   // 입고R/T
			recPara.setField("YD_EQP_ID"  ,ydEqpId);    				// 입고동
			
	        rtnVal = commDao.select(recPara, outResultSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.findMoveSpanSearchRange");
			
			if(rtnVal > 0) {
				outResultSet.first();
				tempResult  = outResultSet.getRecord();
				
				findFromLoc = StringHelper.evl(ydDaoUtils.paraRecChkNull(tempResult, "FIND_FROM_LOC"),"0101");
				findToLoc   = StringHelper.evl(ydDaoUtils.paraRecChkNull(tempResult, "FIND_TO_LOC"),"0799");		
			} else {
				String sTmpYdGp = "1";
				String schGp= ydSchCd.substring(4,6); 
				//Y4CrnSchCrnSpecCheckDtl 의 스케줄코드 기준 1,2후판 구분 채용
		    	if( "RA".equals(schGp)||
						"RB".equals(schGp)||
						"RC".equals(schGp)||
						"10".equals(schGp)||
						"20".equals(schGp)||
						"21".equals(schGp)||
						"22".equals(schGp)||
						"UT".equals(schGp)||
						"01".equals(schGp)||
						"23".equals(schGp)||
						"00".equals(schGp)){   //21,22, UT 추가
							
		    				sTmpYdGp = "2";  //구2후판
				}
		    	
		    	if("1".equals(sTmpYdGp)){
		    		logMsg = "예정위치 xx0101 에 의한 스케줄코드는 디폴트값 사용 현재야드 1후판";
		    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		    		findFromLoc = "0401";
					findToLoc   = "0799"; 
		    	}
		    	else{
		    		logMsg = "예정위치 xx0101 에 의한 스케줄코드는 디폴트값 사용 현재야드 2후판";
		    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		    		findFromLoc = "0101";
					findToLoc   = "0399"; 
		    	}
				
			}
		
		
		}else{
			logMsg="신이적/선별 로직으로 TO위치 검색";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
			//	이적/선별(ProC로 검색함) 스판검색범위
			//----------------------------------------------------------------------------------------------------------------------
			String sAbleYn = ""; //창고간 통로허용 여부(S:분리, T:통합)
			String sRuleId = ""; //T00021(분리),TI0021(통합)
			
			outResultSet 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			
			recPara.setField("REPR_CD_GP" ,"TI0001");	
			recPara.setField("CD_GP" ,ydBayGp);    			
			recPara.setField("ITEM"  ,"S");	//분리기준
			
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
			rtnVal = ydEqpDao.getYdEqp(recPara, outResultSet, 999);
			
			if (rtnVal > 0) {
				sAbleYn = "S";
				sRuleId = "T00021";
				
			} else {
				sAbleYn = "T";
				sRuleId = "TI0021";
			}
			
			outResultSet 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			tempResult 	= JDTORecordFactory.getInstance().create();

			recPara.setField("REPR_CD_GP" ,sRuleId);	
			recPara.setField("CD_GP" ,ydEqpNum);    // 크레인 호기			

			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgt*/
			rtnVal = ydCarSchDao.getYdCarsch(recPara, outResultSet, 400);
			
			if (rtnVal > 0) {
				outResultSet.first();
				tempResult = outResultSet.getRecord();
				findFromLoc = StringHelper.evl(ydDaoUtils.paraRecChkNull(tempResult, ydBayGp+"_DONG"),"0101");
				findToLoc   = StringHelper.evl(ydDaoUtils.paraRecChkNull(tempResult, ydBayGp+"_DONG2"),"0799");
			
			} else {
				findFromLoc = "0101";
				findToLoc   = "0799"; 
			}
		}
		
		logMsg = "탐색한 검색위치. findFromLoc:"+findFromLoc+"findToLoc:"+findToLoc;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			
			
			
		
		
		rsResult.setField("FIND_FROM_LOC", findFromLoc);
		rsResult.setField("FIND_TO_LOC", findToLoc);		
		
		logMsg = operationName+"(" + methodName + ") 끝";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
	
		
		return rsResult;
	}
	/**
	 * 이적스판검색범위 탐색 (입고대기존 탐색용)
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws JDTOException
	 */
	
	public static JDTORecord findMoveSpanSearchRangeForPre(JDTORecord findRecord	) throws JDTOException{
		String methodName				 = "findMoveSpanSearchRangeForPre";
		String operationName			 = "입고대기존 이적스판검색범위 탐색";
		String logMsg					 = null;
		
		YdEqpDao	ydEqpDao 	= new YdEqpDao();
		
		JDTORecord rsResult = JDTORecordFactory.getInstance().create();
		
		JDTORecordSet outResultSet 	= JDTORecordFactory.getInstance().createRecordSet("");		
		JDTORecord recPara          = JDTORecordFactory.getInstance().create();
		JDTORecord tempResult          = JDTORecordFactory.getInstance().create();
		
		int rtnVal = 0;
		
		String findFromLoc = "";
		String findToLoc   = "";

		String ydBayGp    = ydDaoUtils.paraRecChkNull(findRecord,"YD_BAY_GP");
		String ydInRt	   = ydDaoUtils.paraRecChkNull(findRecord,"YD_IN_RT");
		
		
		String logId                     = ydUtils.getJDTOLogId(findRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
		
		logMsg = operationName+"(" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		logMsg="동구분 ["+ydBayGp+"] R/T구분 ["+ydInRt+"]" ;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	이적스판검색범위
		//----------------------------------------------------------------------------------------------------------------------
		outResultSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		tempResult 	= JDTORecordFactory.getInstance().create();

		recPara.setField("REPR_CD_GP" ,"T00011");	
		recPara.setField("CD_GP" ,ydInRt);   // 입고R/T
		recPara.setField("ITEM"  ,ydBayGp);    				// 입고동
		
		/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
		rtnVal = ydEqpDao.getYdEqp(recPara, outResultSet, 999);
		
		if(rtnVal > 0) {
			outResultSet.first();
			tempResult  = outResultSet.getRecord();
			
			findFromLoc = StringHelper.evl(ydDaoUtils.paraRecChkNull(tempResult, "ITEM1"),"0101");
			findToLoc   = StringHelper.evl(ydDaoUtils.paraRecChkNull(tempResult, "ITEM2"),"0799");		
		} else {
			String sTmpYdGp = "1";
			//Y4CrnSchCrnSpecCheckDtl 의 스케줄코드 기준 1,2후판 구분 채용
	    	if( "RA".equals(ydInRt)||
					"RB".equals(ydInRt)||
					"RC".equals(ydInRt)){
						
	    				sTmpYdGp = "2";  //구2후판
			}
	    	
	    	if("1".equals(sTmpYdGp)){
	    		logMsg = "예정위치 xx0101 에 의한 스케줄코드는 디폴트값 사용 현재야드 1후판";
	    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
	    		findFromLoc = "0401";
				findToLoc   = "0799"; 
	    	}
	    	else{
	    		logMsg = "예정위치 xx0101 에 의한 스케줄코드는 디폴트값 사용 현재야드 2후판";
	    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
	    		findFromLoc = "0101";
				findToLoc   = "0399"; 
	    	}
		}
		
		logMsg = "탐색한 검색위치. findFromLoc:"+findFromLoc+"findToLoc:"+findToLoc;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		
		rsResult.setField("FIND_FROM_LOC", findFromLoc);
		rsResult.setField("FIND_TO_LOC", findToLoc);
		
		logMsg = operationName+"(" + methodName + ") 끝";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
	
		
		return rsResult;
	}
	/**
	 * 이적스판검색방향 설정 
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws JDTOException
	 */
	
	public static JDTORecord findMoveSpanSearchDir(JDTORecord findRecord ) throws JDTOException{
		String methodName				 = "findMoveSpanSearchDir";
		String operationName			 = "이적스판검색방향 탐색";
		String logMsg					 = null;
		String rtnMsg 				     = YdConstant.RETN_CD_SUCCESS;
		
		String logId                     = ydUtils.getJDTOLogId(findRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
		
		logMsg = operationName+"(" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		JDTORecord rsResult = JDTORecordFactory.getInstance().create();
		
		JDTORecordSet outResultSet 	= JDTORecordFactory.getInstance().createRecordSet("");		
		JDTORecord recPara          = JDTORecordFactory.getInstance().create();
		JDTORecord tempResult          = JDTORecordFactory.getInstance().create();
		
		
		String ydEqpGp 	  	= ydDaoUtils.paraRecChkNull(findRecord,"YD_EQP_GP");
		String ydUpStkColGp = ydDaoUtils.paraRecChkNull(findRecord,"YD_UP_STK_COL_GP");
		String ydUpStkBedNo = ydDaoUtils.paraRecChkNull(findRecord,"YD_UP_STK_BED_NO");
		String ydToLocGuide = ydDaoUtils.paraRecChkNull(findRecord,"YD_TO_LOC_GUIDE");
		String ydBayGp    	= ydDaoUtils.paraRecChkNull(findRecord,"YD_BAY_GP");
		String ydSchCd    	= ydDaoUtils.paraRecChkNull(findRecord,"YD_SCH_CD");
		
		if(YdConstant.YD_EQP_GP_ROLLERTABLE.equals(ydEqpGp) || YdConstant.YD_EQP_GP_TRANSFER.equals(ydEqpGp) ) {
	    		
    		//----------------------------------------------------------------------------------------------------------------------
    		//	RT에서 입고 시는 최하단 크레인작업재료의 Piling Code와 베드의 최상단 재료의 Piling Code가 동일한 베드를 검색
    		//	권상지시위치의 베드위치에 따라 01, 02, 03번지의 베드를 선택적으로 조회 필요.
    		//	==> 차후에 기능 개선 필요 
    		//----------------------------------------------------------------------------------------------------------------------
			logMsg  = "["+ operationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
			logMsg += "["+ operationName +"] RT/TF입고인 경우 Piling Code가 동일한 베드를 검색을 위한 검색범위와 검색방향을 설정 \n";
			logMsg += "["+ operationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
    		
    		//----------------------------------------------------------------------------------------------------------------------
			//	권상 RT/TF베드를  야드베드번지로 변환
			//----------------------------------------------------------------------------------------------------------------------
    		
			logMsg = "["+ operationName +"] 권상 "+ydEqpGp+"베드[" + ydUpStkBedNo + "]를  야드베드번지로 변환 시작";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
    		
			String ydStkBedNo = "";
			
			if( YdConstant.YD_EQP_GP_ROLLERTABLE.equals(ydEqpGp) ) {
				ydStkBedNo = PlateGdsYdUtil.getYdBedNoFromRtBedNo(ydUpStkBedNo);
			}else if( YdConstant.YD_EQP_GP_TRANSFER.equals(ydEqpGp) ) {
				ydStkBedNo = PlateGdsYdUtil.getYdBedNoFromTfBedNo(ydUpStkBedNo);
			}
			logMsg = "["+ operationName +"] 권상 "+ydBayGp+"베드[" + ydUpStkBedNo + "]를  야드베드번지["+ydStkBedNo+"]변환 완료";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
    		//----------------------------------------------------------------------------------------------------------------------
			
			/*
			 * Crane1호기 2Grab(04, 05, 06스판작업) - 04, 05, 06스판의 STOPPER위치가 01번지이면 01, 02번지만 검색, 03번지이면 02, 03번지만 검색
			 * Crane2호기 1Grab(07스판작업) - 07스판을 모두 검색
			 * 검색방향은 출하에서 RT방향으로
			 * 1. 동일한 Piling Code로 조회
			 * 2. 공베드 조회 시 - 길이구분/폭구분으로 조회
			 * 	2-1. 입고예정위치가 존재하면 해당스판을 기준으로 해서 우선적으로 검색
			 *  2-2. 입고예정위치가 존재하지 않으면 임의의 순서로 검색
			 * 3. 혼적베드 검색 - 길이구분/폭구분으로 조회 : 위와 동일한 방법으로 검색
			 * 4. 임의의 베드를 강제로 적용. - 권하위치가 없다는 표시로 처리?
			 */
			
			//----------------------------------------------------------------------------------------------------------------------
			//	스판검색범위와 검색정렬방법을 설정
			//----------------------------------------------------------------------------------------------------------------------
			rsResult.setField("YD_STK_BED_NO", 	ydStkBedNo);
			
			if( ydToLocGuide.equals("") || 
					ydToLocGuide.length() != 8 ) {
				//-------------------------------------------------------------------------------
				//	야드TO위치Guide가 없는 경우에는 스케줄코드의 입고방향으로 검색범위와 방향 정의
				//	수정자 : 임춘수
				//	수정일 : 2009.12.22
				//-------------------------------------------------------------------------------
				if( ydSchCd.substring(7).equals("L") ) {									//스케줄코드가 Left
					rsResult.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);			//스판검색범위(04, 05, 06스판)
					rsResult.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);			//검색방향(차량출하->RT방향)
				}else{																			//스케줄코드가 Right
					rsResult.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);			//스판검색범위(07스판)
					rsResult.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);			//검색방향(차량출하->RT방향)
				}
				
				logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]가 존재하지 않거나 자리수(8)가 맞지 않으므로 스케줄코드의 입고방향으로 검색범위와 방향정의";
				ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
				//-------------------------------------------------------------------------------
			}else{
				if( ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_01) || 
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) ||
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06) ) {
					rsResult.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);			//스판검색범위(1후판:04, 05, 06스판 / 2후판:01,02 스판)
					rsResult.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);			//검색방향(차량출하->RT방향)
				}else if( ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_02) || 
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_03) || 
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
					rsResult.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);			//스판검색범위(1후판:07스판 / 2후판:02,03스판)
					rsResult.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);			//검색방향(차량출하->RT방향)
				}
			}
    		
    	}else if( YdConstant.YD_EQP_GP_PALLET.equals(ydEqpGp) )	{
    		
    		//----------------------------------------------------------------------------------------------------------------------
    		//	차량입고 시
    		//----------------------------------------------------------------------------------------------------------------------
    		logMsg  = "["+ operationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
    		logMsg += "["+ operationName +"] 차량입고인 경우 Piling Code가 동일한 베드를 검색을 위한 검색범위와 검색방향을 설정 \n";
    		logMsg += "["+ operationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
    		
    		//----------------------------------------------------------------------------------------------------------------------
    		//	스케줄코드로 통로 분석
    		//----------------------------------------------------------------------------------------------------------------------
    		String szPATH = ydSchCd.substring(5, 6);
    		
    		if( szPATH.equals("1"))	{
    			//A통로 - 04, 05, 06스판 검색
    		}else if( szPATH.equals("2"))	{
    			//B통로 - 07스판 검색
    		}
    		//----------------------------------------------------------------------------------------------------------------------
    		
    		//----------------------------------------------------------------------------------------------------------------------
			//	스판검색범위와 검색정렬방법을 설정
			//----------------------------------------------------------------------------------------------------------------------
			
    		rsResult.setField("YD_STK_BED_NO", 	"");			//01, 02, 03번지 모두 검색
			
			if( ydToLocGuide.equals("") || 
					ydToLocGuide.length() != 8 ) {
				//-------------------------------------------------------------------------------
				//	야드TO위치Guide가 없는 경우에는 차량의 통로를 사용하여 검색범위와 방향 정의
				//	수정자 : 임춘수
				//	수정일 : 2009.12.22
				//-------------------------------------------------------------------------------
				if( szPATH.equals("1") || szPATH.equals("3"))	{
	    			//A통로 - 04, 05, 06스판 검색 , 2후판 : 01,02 스판 검색
					rsResult.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);		//스판검색범위(1후판:04, 05, 06스판 / 2후판:01,02 스판)
					rsResult.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);		//검색방향(차량출하->RT방향)
	    		}else if( szPATH.equals("2") || szPATH.equals("4"))	{
	    			//B통로 - 07스판 검색 , 2후판 : 02,03 스판검색
	    			rsResult.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);		//스판검색범위(1후판:07스판 / 2후판:02,03스판)
	    			rsResult.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);		//검색방향(차량출하->RT방향)
	    		}
				
				logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]가 존재하지 않거나 자리수(8)가 맞지 않으므로 차량의 통로를 사용하여 검색범위와 방향정의";
				ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
				//-------------------------------------------------------------------------------
			}else{
				if( ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_01) || 
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) ||	
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06) ) {
					rsResult.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);				//스판검색범위(1후판:04, 05, 06스판 / 2후판:01,02 스판)
					rsResult.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
				}else if( ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_02) || 
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_03) || 
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
					rsResult.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);				//스판검색범위(1후판:07스판 / 2후판:02,03스판)
					rsResult.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
				}
			}
			//----------------------------------------------------------------------------------------------------------------------
    	}else{
    		
    		//----------------------------------------------------------------------------------------------------------------------
    		//	이적/차량출고 인 경우에 적용 - 별도의 모듈화가 필요 시 제거
    		//----------------------------------------------------------------------------------------------------------------------
    		
    		logMsg  = "["+ operationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
    		logMsg += "["+ operationName +"] 이적/차량출고[" + ydSchCd + "]인 경우 Piling Code가 동일한 베드를 검색을 위한 검색범위와 검색방향을 설정 \n";
    		logMsg += "["+ operationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
    		
    		//----------------------------------------------------------------------------------------------------------------------
			//	스판검색범위와 검색정렬방법을 설정
			//----------------------------------------------------------------------------------------------------------------------
    		rsResult.setField("YD_STK_BED_NO", 	ydUpStkBedNo);
			
			if( ydToLocGuide.equals("") || 
					ydToLocGuide.length() != 8 ) {
				if( ydUpStkColGp.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_01) || 
						ydUpStkColGp.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
						ydUpStkColGp.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) || 
						ydUpStkColGp.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06) ) {
					rsResult.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);				//스판검색범위(1후판:04, 05, 06스판 / 2후판:01,02 스판)
					rsResult.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
				}else if( ydUpStkColGp.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_02) || 
						ydUpStkColGp.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_03) || 
						  ydUpStkColGp.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
						  ydUpStkColGp.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
					rsResult.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);				//스판검색범위(1후판:07스판 / 2후판:02,03스판)
					rsResult.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
				}
				
				logMsg = "["+ operationName +"] 야드To위치Guide["+ydToLocGuide+"]가 존재하지 않거나 자리수(8)가 맞지 않으므로 권상지시위치를 사용하여 검색범위와 방향정의";
				ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
				
			}else{
				if( ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_01) || 
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) ||		
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06) ) {
					rsResult.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);				//스판검색범위(1후판:04, 05, 06스판 / 2후판:01,02 스판)
					rsResult.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
				}else if( ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_02) || 
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_03) || 
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
						ydToLocGuide.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
					rsResult.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);				//스판검색범위(1후판:07스판 / 2후판:02,03스판)
					rsResult.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
				}
			}
    	}
		
		
		logMsg = operationName+"(" + methodName + ") 결과 YD_STK_BED_NO ["+ydDaoUtils.paraRecChkNull(rsResult,"YD_STK_BED_NO")+"] "
						+ "SPAN_ORDER ["+ydDaoUtils.paraRecChkNull(rsResult,"SPAN_ORDER")+"] "
						+ "SCAN_DIR ["+ydDaoUtils.paraRecChkNull(rsResult,"SCAN_DIR")+"]";
		
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		logMsg = operationName+"(" + methodName + ") 끝";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
	
		
		return rsResult;
	}
	/**
	 * 입고작업 재료가 대형고객사인지 여부 
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws JDTOException
	 */
	
	public static boolean isRcvPlateBigCust(JDTORecord findRecord ) throws JDTOException{
		String methodName				 = "isRcvPlateBigCust";
		String operationName			 = "입고작업 재료가 대형고객사인지 여부";
		String logMsg					 = null;
		String rtnMsg 				     = YdConstant.RETN_CD_SUCCESS;
		
		String logId                     = ydUtils.getJDTOLogId(findRecord, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
		
		logMsg = operationName+"(" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		YdEqpDao	ydEqpDao 	= new YdEqpDao();
		YdStockDao		ydStockDao		= new YdStockDao();
				
		
		String ydStkColGp    = ydDaoUtils.paraRecChkNull(findRecord,"YD_STK_COL_GP");
		String ydSchCd    	= ydDaoUtils.paraRecChkNull(findRecord,"YD_SCH_CD");
		String stlNo        = ydDaoUtils.paraRecChkNull(findRecord,"STL_NO");
		
		String ydGp			= ydStkColGp.substring(0, 1);
    	String ydEqpGp		= ydStkColGp.substring(2, 4);
    	
    	String ydGntGp      = "";
    	
    	//입고존 사전to위치 검색시 ydschcd 값 없기때문에 예외처리추가.
    	if(!ydSchCd.isEmpty() && ydSchCd.length()>=8){
    		ydGntGp = ydSchCd.substring(6, 7);
    	}
		//RT, TF,PT 입고작업 아닌경우 SKIP
		if( !( YdConstant.YD_EQP_GP_ROLLERTABLE.equals(ydEqpGp) || YdConstant.YD_EQP_GP_TRANSFER.equals(ydEqpGp)
			  || YdConstant.YD_EQP_GP_PALLET.equals(ydEqpGp) || YdConstant.YD_GNT_GP_MVSTK.equals(ydGntGp)
			  ) 		
		   ) {
			logMsg = operationName+"(" + methodName + ") 입고작업 아니므로 종료";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			return false;
			
		}
		
		JDTORecordSet outResult = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord 	  inRecord  = JDTORecordFactory.getInstance().create();
		JDTORecord 	  outRecord = JDTORecordFactory.getInstance().create();
		JDTORecord 	  recTemp 	  = JDTORecordFactory.getInstance().create();
		String applyYn 		  = "N";
		
		if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(ydGp)) {
			inRecord.setField("REPR_CD_GP", "T00070");    //저장그룹편성보완
		} else {
			inRecord.setField("REPR_CD_GP", "K00070");    //저장그룹편성보완
		}
		
		/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
		int rtnVal = ydEqpDao.getYdEqp(inRecord, outResult, 999);
		if(rtnVal > 0) {
			outResult.first();
			outRecord = outResult.getRecord();
			applyYn = outRecord.getFieldString("ITEM1");				
		}
		
		logMsg="신저장그룹편성보완 적용 " + applyYn ;		
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);	
		
		if(!"Y".equals(applyYn)){
			logMsg = operationName+"(" + methodName + ") 신저장그룹편성보완 적용 여부 N 이므로 종료";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			return false;
		}
		
		logMsg = "["+ operationName +"] 크레인작업재료의 최하단 재료정보["+stlNo+"]를 저장품에서 조회 시작 ";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		outResult = JDTORecordFactory.getInstance().createRecordSet("");
		inRecord  = JDTORecordFactory.getInstance().create();
		
		/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdOrdWgtCheck*/
		rtnVal = ydStockDao.getYdStock(inRecord, outResult, 606);
		if( rtnVal < 1 ) {
			logMsg = "["+ operationName +"] 크레인작업재료의 최하단 재료정보["+stlNo+"]를 저장품에서 조회 실패 ";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			return false;
		}
		
		outResult.first();
		recTemp = outResult.getRecord();
		String chkFlag 	= ydDaoUtils.paraRecChkNull(recTemp, "CHECK_FLAG");		        //주문량이 합(수량x두께) > 121	
		
		logMsg = "["+ operationName +"] 크레인작업재료의 최하단 재료정보["+stlNo+"]를 저장품에서 조회 완료 - 주문량이 합["+chkFlag+"]";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		if(chkFlag.equals("Y")) {
			return true;
		} 		
		
		return false;
	}
	
	/**
	 * 입고 권하위치에따른 작업크레인 탐색
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws JDTOException
	 */
	
	public static JDTORecord findRcptEqpIdByDnLoc(String ydBayGp,String rtGp,String ydUpWoLoc, String ydDnWoLoc, String logId) throws JDTOException{
		String methodName				 = "findRcptEqpIdByDnLoc";
		String operationName			 = "입고 권하위치에따른 작업크레인 탐색";
		String logMsg					 = null;
		String rtnMsg 				     = YdConstant.RETN_CD_SUCCESS;
		
		
		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
		
		logMsg = operationName+"(" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		JDTORecordSet rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdPlateCommDAO commDao = new YdPlateCommDAO();
        
        recPara.setField("YD_BAY_GP"  , ydBayGp);
        recPara.setField("RT_GP"      , rtGp);
        recPara.setField("YD_LOC"     , ydDnWoLoc);
        

        int rtnVal = commDao.select(recPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0134");
        
        String ydSchCdSuffix = "";
        if(rtnVal == 0) {

            if( ydDnWoLoc.length() >= 4) {
                String ydSpanNo = ydDnWoLoc.substring(2, 4);

                if("RA".equals(rtGp) || "RB".equals(rtGp) || "RC".equals(rtGp)) {

                    if(YdConstant.SPAN_ORDER_NEW_01.equals(ydSpanNo) ) {
                        ydSchCdSuffix = "LL";
                    } else if("TCRTUT45".equals(ydDnWoLoc)){
                        ydSchCdSuffix = "LL";
                    } else if("AP".equals(ydSpanNo)) {//#2RT B동파일링
                        ydSchCdSuffix = "AP";
                    } else {
                        ydSchCdSuffix = "LR";
                    }

                } else if("RD".equals(rtGp)) {
                    if("XX".equals(ydSpanNo)){ydSpanNo = "05";}
                    if(Integer.valueOf(ydSpanNo).compareTo(Integer.valueOf("05")) < 0) {
                        ydSchCdSuffix = "LL";
                    } else {
                        ydSchCdSuffix = "LR";
                    }

                } else if("RE".equals(rtGp)) {
                    if("XX".equals(ydSpanNo)){ydSpanNo = "06";}
                    if(Integer.valueOf(ydSpanNo).compareTo(Integer.valueOf("06")) < 0) {
                        ydSchCdSuffix = "LL";
                    } else {
                        ydSchCdSuffix = "LR";
                    }

                } else if("RF".equals(rtGp)) {
                    if("XX".equals(ydSpanNo)){ydSpanNo = "07";}
                    if(Integer.valueOf(ydSpanNo).compareTo(Integer.valueOf("07")) < 0) {
                        ydSchCdSuffix = "LL";
                    } else {
                        ydSchCdSuffix = "LR";
                    }
                } else if("UT".equals(rtGp)) {
                    ydSchCdSuffix = "LM";
                }
            }else{
                if("UT".equals(rtGp)) {
                	ydSchCdSuffix = "LM";
                }else{
                	ydSchCdSuffix = "LR";
                }
            }

            logMsg = "["+operationName+"] 검색대상이 없어 ["+ydSchCdSuffix+"]를 사용";
            ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);

        }else{

            //레코드 추출
            rsResult.first();
            recPara = rsResult.getRecord();

            ydSchCdSuffix  = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");

            logMsg = "["+operationName+"] 최종 검색대상 ["+ydSchCdSuffix+"]를 사용";
            ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
        }
        
        String ydSchCd = ydUpWoLoc.substring(0,4) + rtGp + ydSchCdSuffix;

        logMsg = "["+operationName+"] 권하지시위치 ["+ydDnWoLoc+"]를 사용하여 스케줄코드 생성 - ["+ydSchCd+"]";
        ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);


        //-- 통합 크레인 스케줄 조회 -----------------------------------------------------------------start--
        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
        recPara = JDTORecordFactory.getInstance().create();

        recPara.setField("YD_SCH_CD", ydSchCd);

        rtnVal = commDao.select(recPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0095");

        String wrkCrn   = "";
        String crnPrior = "";
        if(rtnVal == 0) {

        	logMsg = "["+operationName+"] 통합 크레인 스케줄 코드 조회 0건 - ["+ydSchCd+"]";
        	ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
            return null;

        }

        //레코드 추출
        rsResult.first();
        recPara = rsResult.getRecord();

        wrkCrn           = ydDaoUtils.paraRecChkNull(recPara, "WRK_CRN");
        crnPrior  = ydDaoUtils.paraRecChkNull(recPara, "CRN_PRIOR");
        
        logMsg=ydSchCd+"스케줄기준 검색시 작업크레인. wrkCrn ="+wrkCrn +"우선순위" + crnPrior;
        ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
        //-- 통합 크레인 스케줄 조회 ------------------------------------------------------------------end---
        

        if("".equals(wrkCrn)&&"TBRTRAAP".equals(ydSchCd)){

        	logMsg="[TBRTRAAP]진행중인 설비ID를  크레인  스케줄정보에서 검색합니다.";
        	ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
            //------------------------------------------------------------------------------------------------
            JDTORecordSet rsResult1     = JDTORecordFactory.getInstance().createRecordSet("");
            JDTORecord    recInTemp     = JDTORecordFactory.getInstance().create();

            recInTemp.setField("YD_SCH_CD", ydSchCd);
            rtnVal = commDao.select(recInTemp, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0137");

            
            if(rtnVal <=0){
            	logMsg = "["+operationName+"] 통합 크레인 스케줄 코드 조회 0건 - ["+ydSchCd+"]";
            	ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
                return null;
            }
            
           
            rsResult1.first();
            recInTemp = rsResult1.getRecord();

            wrkCrn                   = ydDaoUtils.paraRecChkNull(recInTemp, "YD_EQP_ID");
            crnPrior          = "1";

            

            logMsg="[TBRTRAAP]진행중인 설비ID를  크레인  스케줄정보에서 검색합니다. szYD_WRKABLE_CRN ="+wrkCrn;
            ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
            //------------------------------------------------------------------------------------------------
        }

        JDTORecord result = JDTORecordFactory.getInstance().create();
        result.setField("WRK_CRN", wrkCrn);
        result.setField("CRN_PRIOR", crnPrior);
		
		return result;
	}
	
	/**
	 * 크레인변경(고장)에 따른 TO위치 재탐색 로직 신규  procMainWrkToLocForPlateYd 기반 수정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String procMainWrkToLocForPlateYdForChgCrn(
			//JDTORecord msgRecord					/* 전문 */  //안쓰이는듯 제외시키자.
			 JDTORecordSet rsCrnwrkmtl				/* 크레인작업재료 */
			, JDTORecord recCrnSch					/* 크레인스케줄정보 */
		//	, JDTORecord recWbook					/* 작업예약정보 */    //SCH_CD와 TO위치가이드 가져오기위한것같은데, SCH_CD는 스케줄꺼 참고하면되고, TO위치가이드는 빈값으로 할것이니 인자 제외시키자.
			, String YD_TO_LOC_GUIDE_FROM           /* to위치 from */  //변수 추가
			, String YD_TO_LOC_GUIDE_TO             /* to위치 to */    //변수 추가
			) throws JDTOException {
		/*
		 * 업무기준	:	1. 
		 * 
		 * 수정자	: 박종호.
		 * 수정일	:
		 * 				1. 2024.02.01 - 최초등록
		 */
		String szMethodName				= "procMainWrkToLocForPlateYdForChgCrn";
		String szOperationName			= "크레인변경(고장)에 따른 TO위치 재탐색 로직";
		String szDesc					= "";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		ArrayList		listToLoc		= null;
		YdStkLocVO		ydStkLocVO		= null;
		
		JDTORecordSet	rsResult		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		JDTORecord		recLogMsg		= null;
		
		String szYD_UP_WO_LOC			= null;
		String szYD_UP_WO_LAYER			= null;
		String szYD_DN_WO_LOC			= null;
		String szYD_DN_WO_LAYER			= null;
		String szYD_UP_STK_COL_GP		= null;
		String szYD_UP_STK_BED_NO		= null;
		String szYD_DN_STK_COL_GP		= null;
		String szYD_DN_STK_BED_NO		= null;
		String szYD_SCH_CD				= null;
		
		boolean bUP_UPDT_NEEDED			= false;
		
		String	szSTL_NO				= null;
		String[] szYD_STK_LYR_MTL_STAT	= {"D", "U"};
		
		String	szYD_TO_LOC_GUIDE		= null;
		String	szYD_TO_LOC_GUIDEbak		= null;  //입력받은 시점의 to위치가이드 백업용
		
		String	szYD_TO_LOC_GUIDE_FROM		= null;  //범위로 계산하므로 FROM/TO 추가
		String	szYD_TO_LOC_GUIDE_TO		= null;  //범위로 계산하므로 FROM/TO 추가
		
		String szYD_CRN_SCH_ID			= null;
		String szYD_EQP_ID				= null;
		
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_GP				= null;
		
		String szYD_MTL_W_GP			= null;						//야드재료폭구분
		String szYD_MTL_T_GP			= null;						//야드재료두께구분
		String szYD_MTL_L_GP			= null;						//야드재료길이구분
		
		String szYD_PILING_CD			= null;						//야드Piling코드
		
		int intYD_EQP_WRK_SH			= 0;						//야드설비작업매수
		int intYD_EQP_WRK_WT			= 0;						//야드설비작업중량
		double dblYD_EQP_WRK_T			= 0;						//야드설비작업총두께
		String szYD_EQP_WRK_MAX_W		= null;						//작업재료 중 최대 폭
		String szYD_EQP_WRK_MAX_L		= null;						//작업재료 중 최대 길이
		String szSEARCH_CHANGE          = "N";                      //대형고객사 입고 시 검색순서 변경 : P-C,혼적,공BED -> P-C,공BED,혼적
//SJH04004
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();	
		YdEqpDao	ydEqpDao 	= new YdEqpDao();
		YdStockDao		ydStockDao		= new YdStockDao();
		YdStkBedDao ydStkbedDao = new YdStkBedDao();
		
		JDTORecordSet outResult1 	= null;		
		JDTORecord inRecord3 		= null;		
		JDTORecord inRecord2 		= null;		
		String szFROM_DONG			= "";		
		String szTO_DONG  			= "";		
		String szYD_CRN_GRAB_TP     = "";
		String szCHECK_FLAG         = "N";
		int intRtnVal               = 0; 
		String szYD_RCPT_PLN_STR_LOC = "";
		boolean bIsToLocStackable				= false;
		// AT000 물류시스템 개선 2022.10.27
		YdPlateCommDAO commDao = new YdPlateCommDAO(); 
		String sCrnWrkMode2             = "";
		double dblYD_EQP_WRK_W		  = 0;				      //작업재료 중 최대 폭
		String szYD_STK_BED_W_GP_G      ="";
		
		YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();	
		//----------------------------------------------------------------------------------------------------------------------
		//------------------------------------- 사용자정의위치(입고예정위치)에 대한 TO위치결정 -------------------------------------
		//----------------------------------------------------------------------------------------------------------------------
		
		//로직 다시 분석할때까지 남긴다. -> 설비고장복구실적, 운전모드 전환 시 호출되는 함수
		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "016");//후판 개발 적용여부
		
		if("Y".equals(sApplyYnPI)){
			szLogMsg = "크레인변경(고장)에 따른 TO위치 재탐색 로직(" + szMethodName + ") 신규모듈 호출";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			return procMainWrkToLocForPlateYdForChgCrn2nd( rsCrnwrkmtl, recCrnSch	, YD_TO_LOC_GUIDE_FROM  , YD_TO_LOC_GUIDE_TO  ) ;
		}
		
		
		szYD_TO_LOC_GUIDE_FROM=YD_TO_LOC_GUIDE_FROM;
		szYD_TO_LOC_GUIDE_TO=YD_TO_LOC_GUIDE_TO;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------
		
		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		
		//szYD_SCH_CD	= ydDaoUtils.paraRecChkNull(recWbook, "YD_SCH_CD");
		szYD_SCH_CD	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD"); //스케줄의 스케줄코드로 변경
		
		
		if( szYD_SCH_CD.substring(2, 4).equals("PT") ) {
			szDesc			= "차량";
		}else if( szYD_SCH_CD.substring(2, 4).equals("TR") ) {
			szDesc			= "차량";
		}else if( szYD_SCH_CD.substring(2, 4).equals("RT") ) {
			szDesc			= "RollerTable";
		}else if( szYD_SCH_CD.substring(2, 4).equals("TF") ) {
			szDesc			= "Transfer";
		}else if( szYD_SCH_CD.substring(2, 4).equals("YD") ) {
			szDesc			= "야드";
		}else if( szYD_SCH_CD.substring(2, 4).equals("SL") ) {
			szDesc			= "선별";			
		}
		
		if( szYD_SCH_CD.substring(6, 7).equals("L")) {
			szDesc		+= "입고";
		}else if( szYD_SCH_CD.substring(6, 7).equals("U")) {
			szDesc		+= "출고";
		}else if( szYD_SCH_CD.substring(6, 7).equals("M")) {
			szDesc		+= "이적";
		}
		
		if( !szDesc.equals("") ) szOperationName		+= "-" + szDesc;
		
		szYD_CRN_SCH_ID      	= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_CRN_SCH_ID");					//크레인스케줄ID
		szYD_EQP_ID      		= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_EQP_ID");						//크레인설비ID
		
		intYD_EQP_WRK_SH      	= ydDaoUtils.paraRecChkNullInt(recPara,"SH_CNT");						//크레인작업재료 총매수
		intYD_EQP_WRK_WT      	= ydDaoUtils.paraRecChkNullInt(recPara,"SUM_MTL_WT");					//크레인작업재료 총중량
		dblYD_EQP_WRK_T      	= ydDaoUtils.paraRecChkNullDouble(recPara,"SUM_MTL_T");					//크레인작업재료 총높이
		
		szYD_EQP_WRK_MAX_W		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_W");						//크레인작업재료 중 최대 폭
		szYD_EQP_WRK_MAX_L		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_L");						//크레인작업재료 중 최대 길이
		dblYD_EQP_WRK_W         = ydDaoUtils.paraRecChkNullDouble(recPara,"MAX_MTL_W");                 //AT000 물류시스템 개선 2022.10.27 최대폭 
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 총매수 :" + intYD_EQP_WRK_SH;
		szLogMsg += ", 총중량 :" + intYD_EQP_WRK_WT;
		szLogMsg += ", 총높이  :" + dblYD_EQP_WRK_T;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		//szYD_TO_LOC_GUIDE		= ydDaoUtils.paraRecChkNull(recWbook, "YD_TO_LOC_GUIDE");
		szYD_TO_LOC_GUIDE		= ""; //TO위치가이드 빈값으로 변경
		szYD_TO_LOC_GUIDE_FROM=YD_TO_LOC_GUIDE_FROM;
		szYD_TO_LOC_GUIDE_TO=YD_TO_LOC_GUIDE_TO;
		
		
		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
			szYD_DN_STK_COL_GP		= szYD_TO_LOC_GUIDE.substring(0, 6);
			szYD_DN_STK_BED_NO		= szYD_TO_LOC_GUIDE.substring(6);
		}
		
		szYD_TO_LOC_GUIDEbak=szYD_TO_LOC_GUIDE;
		
		szLogMsg = "["+ szOperationName +"] ---------------------- 야드To위치Guide : " + szYD_TO_LOC_GUIDE + " --------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	최하단 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------

		szSTL_NO	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 시작 ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		szRtnMsg = DaoManager.getYdStock(recPara, rsResult, 0);
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			return szRtnMsg;
		}
		
		rsResult.first();
		recTemp = rsResult.getRecord();
		
		szYD_MTL_L_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");			//크레인작업 최하단재료의 길이구분
		szYD_MTL_W_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_W_GP");			//크레인작업 최하단재료의 폭구분
		szYD_PILING_CD 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_PILING_CD");			//크레인작업 최하단재료의 Piling코드

		szYD_RCPT_PLN_STR_LOC 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_RCPT_PLN_STR_LOC");//입고예정위치
		
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 완료 - 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"], Piling코드["+szYD_PILING_CD+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
		//----------------------------------------------------------------------------------------------------------------------
		//	권하중이거나 권상중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		szYD_UP_WO_LOC 		= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");		
		szYD_UP_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER");
		
	
		if( szYD_UP_WO_LOC.equals("") ) {
			
			szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			szRtnMsg = YdCommonUtils.getYdStkLyrWithMtlStat(recPara, rsResult, szYD_STK_LYR_MTL_STAT);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				return szRtnMsg;
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 완료  ";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YD_STK_COL_GP");
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YD_STK_BED_NO");
			szYD_UP_WO_LOC 			= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYD_UP_WO_LAYER 		= recTemp.getFieldString("YD_STK_LYR_NO");
			
			bUP_UPDT_NEEDED			= true;
			
			szLogMsg = "["+ szOperationName +"] 조회된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}else{
			szYD_UP_STK_COL_GP = szYD_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYD_UP_WO_LOC.substring(6);
			
			szLogMsg = "["+ szOperationName +"] 크레인스케줄에 등록된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
    	//	해당베드정보에 적치가능한 지를 체크
    	//YD_TO_LOC_GUIDE	- 사용자지정위치(적치열+적치베드)
		// * 				2) YD_EQP_WRK_SH	- 작업총매수
		// * 				3) YD_EQP_WRK_WT	- 작업총중량
		// * 				4) YD_EQP_WRK_T		- 작업총두께
		// * 				5) YD_SCH_CD		- 스케줄코드
    	//----------------------------------------------------------------------------------------------------------------------
		 
		JDTORecordSet 	rsDanPok   = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord 		recDanPok  = JDTORecordFactory.getInstance().create();
		
		listToLoc = new ArrayList();
		
		boolean bIS_BED_STACKABLE	= false;
		String szCflag     = "";          //AT000 물류시스템 개선 2022.11.17
		
		
		boolean isJJ=false;  //정정야드인지 체크
		
		/*
		 * 2014.03.25 윤재광
		 * 2후판 제품창고 사내절단장 북아웃 요구시 디폴트 저장위치로 TO위치 결정
		 */
		if("TB010101".equals(szYD_TO_LOC_GUIDE)||
		   "TB033101".equals(szYD_TO_LOC_GUIDE)||
		   "TB032801".equals(szYD_TO_LOC_GUIDE)  //TB033101->TB032801로 변경 요청. 2022.11.30  후판품질팀 서승범 책임. 1673
		   ){
			
			szYD_DN_WO_LOC		= szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;
			szYD_DN_WO_LAYER 	= "001";
			isJJ=true;
			
	    }else if("TCRTUT45".equals(szYD_TO_LOC_GUIDE)||
				 "TCRTUT13".equals(szYD_TO_LOC_GUIDE)||
				 "TC010101".equals(szYD_TO_LOC_GUIDE)||
				 "TC010103".equals(szYD_TO_LOC_GUIDE)|| // 2020.10.14 (김도훈 매니저 요청, TESTPLATE To위치 지정 베드)
				 "TC010201".equals(szYD_TO_LOC_GUIDE)||
				 "TC010301".equals(szYD_TO_LOC_GUIDE)||
				 "TC010303".equals(szYD_TO_LOC_GUIDE)||
				 ("TCRTRA30".equals(szYD_TO_LOC_GUIDE)&&!szYD_RCPT_PLN_STR_LOC.startsWith("TC"))||
				 ("TCRTRA40".equals(szYD_TO_LOC_GUIDE)&&!szYD_RCPT_PLN_STR_LOC.startsWith("TC"))||
				 ("TCRTRB30".equals(szYD_TO_LOC_GUIDE)&&!szYD_RCPT_PLN_STR_LOC.startsWith("TC"))||
				 ("TCRTRB40".equals(szYD_TO_LOC_GUIDE)&&!szYD_RCPT_PLN_STR_LOC.startsWith("TC"))){
			  
			rsDanPok 	= JDTORecordFactory.getInstance().createRecordSet("");
			recDanPok 	= JDTORecordFactory.getInstance().create();
			recDanPok.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
			recDanPok.setField("TO_LOC", szYD_TO_LOC_GUIDE);
			
			String sGbn = "";
			if("TCRTUT45".equals(szYD_TO_LOC_GUIDE)){
				sGbn = "RA";
			}else{
				sGbn = "UT";
			}
			recDanPok.setField("GBN", sGbn);

	    	szRtnMsg = DaoManager.getToLocWithDanPok(recDanPok, rsDanPok, 2);
	    	
	    	
	    	if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    		isJJ=true;
	    		rsDanPok.first();
	    		
	    		recDanPok			= rsDanPok.getRecord();
	    		
	    		szYD_DN_STK_COL_GP	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_COL_GP"); 
	    		szYD_DN_STK_BED_NO	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_BED_NO"); 
	    		szYD_DN_WO_LOC	 	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_BED_NO");
	    		szYD_DN_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_LYR_NO");
	    		
	    	}else{
	    		return YdConstant.RETN_NOT_EXIST_BED;
	    	}
	    	
		}else{  
			isJJ=false;
			//여기 TO위치 가이드에 따라 적치가능여부 체크 로직있는데, 어차피 TO위치 지정안했을때 전체 베드 대상으로 찾는 로직 아래에 있으므로 그냥 스킵
			
			//------------------------------------------------------------------------------------------------------------
			//	입고가적 적용여부 추가
			//  아래의 전체 입고가적베드 기능으로 통합, 따라서 K00140 기준값 'N'으로 셋팅함 - 2020.12.07 윤재광
			//------------------------------------------------------------------------------------------------------------
			JDTORecordSet 	outResult9 	= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord 		inRecord19 	= JDTORecordFactory.getInstance().create();
			JDTORecord 		outRecord19	= JDTORecordFactory.getInstance().create();
			String szAPPLY_YN9 			= "N";
			
			inRecord19.setField("REPR_CD_GP", "K00140");    //입고가적 적용여부
			
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
			intRtnVal = ydEqpDao.getYdEqp(inRecord19, outResult9, 999);
			if(intRtnVal > 0) {
				outResult9.first();
				outRecord19  = outResult9.getRecord();
				szAPPLY_YN9 = outRecord19.getFieldString("ITEM1");				
			}
			szLogMsg="입고가적 적용여부 " + szAPPLY_YN9 ;
			ydUtils.putLog(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG);			
			
				
			if(szAPPLY_YN9.equals("Y")) {
				//입고가적 적용여부쪽 로직 다 제외 
			}
			
			//----------------------------------------------------------------------------------------------------------------------
			//	입고예정위치가 존재하지 않는 경우에는 입고재료와 같은 Piling Code의 베드 OR 길이구분/폭구분이 같은 해당 동의 모든 위치를 조회 
			//----------------------------------------------------------------------------------------------------------------------
	    	szYD_GP			= szYD_UP_WO_LOC.substring(0, 1);  //NULL임.....
	    	szYD_BAY_GP		= szYD_UP_WO_LOC.substring(1, 2);
	    	szYD_EQP_GP		= szYD_UP_WO_LOC.substring(2, 4);
	    	
	    	//권상지시위치에 따라 알맞은 적치가능한 베드 검색 방법을 적용
	    	recTemp = JDTORecordFactory.getInstance().create();
	    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE);
	    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH));		//크레인작업재료 총매수
	    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT));		//크레인작업재료 총중량
	    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T));		//크레인작업재료 총높이
	    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);							//크레인스케줄코드
	    	recTemp.setField("YD_PILING_CD", 		szYD_PILING_CD);						//크레인작업 최하단재료의 Piling Code
	    	recTemp.setField("YD_MTL_L_GP", 		szYD_MTL_L_GP);							//크레인작업 최하단재료의 길이구분
	    	recTemp.setField("YD_MTL_W_GP", 		szYD_MTL_W_GP);							//크레인작업 최하단재료의 폭구분
	    	recTemp.setField("YD_STK_COL_GP", 		szYD_UP_STK_COL_GP);					//권상지시위치 - 적치열
	    	recTemp.setField("YD_EQP_ID", 			szYD_EQP_ID);							//크레인설비ID
	    	
	    	if( szYD_EQP_GP.equals("RT") || 
	    		szYD_EQP_GP.equals("TF")) {  //입고시
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
	    		//	RT에서 입고 시는 최하단 크레인작업재료의 Piling Code와 베드의 최상단 재료의 Piling Code가 동일한 베드를 검색
	    		//	권상지시위치의 베드위치에 따라 01, 02, 03번지의 베드를 선택적으로 조회 필요.
	    		//	==> 차후에 기능 개선 필요 
	    		//----------------------------------------------------------------------------------------------------------------------
	    		szLogMsg  = "["+ szOperationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
	    		szLogMsg += "["+ szOperationName +"] RT/TF입고인 경우 Piling Code가 동일한 베드를 검색을 위한 검색범위와 검색방향을 설정 \n";
	    		szLogMsg += "["+ szOperationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
				//	권상 RT/TF베드를  야드베드번지로 변환
				//----------------------------------------------------------------------------------------------------------------------
	    		
	    		szLogMsg = "["+ szOperationName +"] 권상 "+szYD_EQP_GP+"베드[" + szYD_UP_STK_BED_NO + "]를  야드베드번지로 변환 시작";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    		
				String szYD_STK_BED_NO = "";
				
				if( szYD_EQP_GP.equals("RT") ) {
					szYD_STK_BED_NO = PlateGdsYdUtil.getYdBedNoFromRtBedNo(szYD_UP_STK_BED_NO);
				}else if( szYD_EQP_GP.equals("TF") ) {
					szYD_STK_BED_NO = PlateGdsYdUtil.getYdBedNoFromTfBedNo(szYD_UP_STK_BED_NO);
				}
	    		szLogMsg = "["+ szOperationName +"] 권상 "+szYD_EQP_GP+"베드[" + szYD_UP_STK_BED_NO + "]를  야드베드번지["+szYD_STK_BED_NO+"]변환 완료";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    		//----------------------------------------------------------------------------------------------------------------------
				
				/*
				 * Crane1호기 2Grab(04, 05, 06스판작업) - 04, 05, 06스판의 STOPPER위치가 01번지이면 01, 02번지만 검색, 03번지이면 02, 03번지만 검색
				 * Crane2호기 1Grab(07스판작업) - 07스판을 모두 검색
				 * 검색방향은 출하에서 RT방향으로
				 * 1. 동일한 Piling Code로 조회
				 * 2. 공베드 조회 시 - 길이구분/폭구분으로 조회
				 * 	2-1. 입고예정위치가 존재하면 해당스판을 기준으로 해서 우선적으로 검색
				 *  2-2. 입고예정위치가 존재하지 않으면 임의의 순서로 검색
				 * 3. 혼적베드 검색 - 길이구분/폭구분으로 조회 : 위와 동일한 방법으로 검색
				 * 4. 임의의 베드를 강제로 적용. - 권하위치가 없다는 표시로 처리?
				 */
				
				//----------------------------------------------------------------------------------------------------------------------
				//	스판검색범위와 검색정렬방법을 설정
				//----------------------------------------------------------------------------------------------------------------------
				recTemp.setField("YD_STK_BED_NO", 	szYD_STK_BED_NO);
				
				if( szYD_TO_LOC_GUIDE.equals("") || 
					szYD_TO_LOC_GUIDE.length() != 8 ) {
					//-------------------------------------------------------------------------------
					//	야드TO위치Guide가 없는 경우에는 스케줄코드의 입고방향으로 검색범위와 방향 정의
					//	수정자 : 임춘수
					//	수정일 : 2009.12.22
					//-------------------------------------------------------------------------------
					if( szYD_SCH_CD.substring(7).equals("L") ) {									//스케줄코드가 Left
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);			//스판검색범위(04, 05, 06스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);			//검색방향(차량출하->RT방향)
					}else{																			//스케줄코드가 Right
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);			//스판검색범위(07스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);			//검색방향(차량출하->RT방향)
					}
					
					szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]가 존재하지 않거나 자리수(8)가 맞지 않으므로 스케줄코드의 입고방향으로 검색범위와 방향정의";
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					//-------------------------------------------------------------------------------
				}else{
					if( szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_01) || 
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) ||
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);			//스판검색범위(1후판:04, 05, 06스판 / 2후판:01,02 스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);			//검색방향(차량출하->RT방향)
					}else if( szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_02) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_03) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);			//스판검색범위(1후판:07스판 / 2후판:02,03스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);			//검색방향(차량출하->RT방향)
					}
				}
	    		
	    	}else if( szYD_EQP_GP.equals("PT"))	{
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
	    		//	차량입고 시
	    		//----------------------------------------------------------------------------------------------------------------------
	    		szLogMsg  = "["+ szOperationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
	    		szLogMsg += "["+ szOperationName +"] 차량입고인 경우 Piling Code가 동일한 베드를 검색을 위한 검색범위와 검색방향을 설정 \n";
	    		szLogMsg += "["+ szOperationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
	    		//	스케줄코드로 통로 분석
	    		//----------------------------------------------------------------------------------------------------------------------
	    		String szPATH = szYD_SCH_CD.substring(5, 6);
	    		
	    		if( szPATH.equals("1"))	{
	    			//A통로 - 04, 05, 06스판 검색
	    		}else if( szPATH.equals("2"))	{
	    			//B통로 - 07스판 검색
	    		}
	    		//----------------------------------------------------------------------------------------------------------------------
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
				//	스판검색범위와 검색정렬방법을 설정
				//----------------------------------------------------------------------------------------------------------------------
				
				recTemp.setField("YD_STK_BED_NO", 	"");			//01, 02, 03번지 모두 검색
				
				if( szYD_TO_LOC_GUIDE.equals("") || 
					szYD_TO_LOC_GUIDE.length() != 8 ) {
					//-------------------------------------------------------------------------------
					//	야드TO위치Guide가 없는 경우에는 차량의 통로를 사용하여 검색범위와 방향 정의
					//	수정자 : 임춘수
					//	수정일 : 2009.12.22
					//-------------------------------------------------------------------------------
					if( szPATH.equals("1") || szPATH.equals("3"))	{
		    			//A통로 - 04, 05, 06스판 검색 , 2후판 : 01,02 스판 검색
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);		//스판검색범위(1후판:04, 05, 06스판 / 2후판:01,02 스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);		//검색방향(차량출하->RT방향)
		    		}else if( szPATH.equals("2") || szPATH.equals("4"))	{
		    			//B통로 - 07스판 검색 , 2후판 : 02,03 스판검색
		    			recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);		//스판검색범위(1후판:07스판 / 2후판:02,03스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);		//검색방향(차량출하->RT방향)
		    		}
					
					szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]가 존재하지 않거나 자리수(8)가 맞지 않으므로 차량의 통로를 사용하여 검색범위와 방향정의";
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					//-------------------------------------------------------------------------------
				}else{
					if( szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_01) || 
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) ||	
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);				//스판검색범위(1후판:04, 05, 06스판 / 2후판:01,02 스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
					}else if( szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_02) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_03) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);				//스판검색범위(1후판:07스판 / 2후판:02,03스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
					}
				}
				//----------------------------------------------------------------------------------------------------------------------
	    	}else{
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
	    		//	이적/차량출고 인 경우에 적용 - 별도의 모듈화가 필요 시 제거
	    		//----------------------------------------------------------------------------------------------------------------------
	    		
	    		szLogMsg  = "["+ szOperationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
	    		szLogMsg += "["+ szOperationName +"] 이적/차량출고[" + szYD_SCH_CD + "]인 경우 Piling Code가 동일한 베드를 검색을 위한 검색범위와 검색방향을 설정 \n";
	    		szLogMsg += "["+ szOperationName +"]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
				//	스판검색범위와 검색정렬방법을 설정
				//----------------------------------------------------------------------------------------------------------------------
				recTemp.setField("YD_STK_BED_NO", 	szYD_UP_STK_BED_NO);
				
				if( szYD_TO_LOC_GUIDE.equals("") || 
					szYD_TO_LOC_GUIDE.length() != 8 ) {
					if( szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_01) || 
						szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
						szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) || 
						szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);				//스판검색범위(1후판:04, 05, 06스판 / 2후판:01,02 스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
					}else if( szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_02) || 
							  szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_03) || 
							  szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
							  szYD_UP_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);				//스판검색범위(1후판:07스판 / 2후판:02,03스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
					}
					
					szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]가 존재하지 않거나 자리수(8)가 맞지 않으므로 권상지시위치를 사용하여 검색범위와 방향정의";
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
				}else{
					if( szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_01) || 
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) ||		
						szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_12);				//스판검색범위(1후판:04, 05, 06스판 / 2후판:01,02 스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
					}else if( szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_02) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_03) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
							  szYD_TO_LOC_GUIDE.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP) ) {
						recTemp.setField("SPAN_ORDER", 			YdConstant.SPAN_ORDER_34);				//스판검색범위(1후판:07스판 / 2후판:02,03스판)
						recTemp.setField("SCAN_DIR", 			YdConstant.SCAN_DIR_PT2RT);				//검색방향(차량출하->RT방향)
					}
				}
	    	}
	    	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	1후판 입고 적치가능한 가적베드 검색조회 시작
			//----------------------------------------------------------------------------------------------------------------------
			szLogMsg = "["+ szYD_SCH_CD.substring(5, 6) +"] 입고RT / ["+szYD_BAY_GP+"]입고동 ";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			if( szYD_EQP_GP.equals("RT")||
				szYD_EQP_GP.equals("TF")){
/*				
				if(("D".equals(szYD_SCH_CD.substring(5, 6))|| 	//입고RT(1후판 전 라인라인으로 수정 2020.11.04 윤재광)
				    "E".equals(szYD_SCH_CD.substring(5, 6))||
				    "F".equals(szYD_SCH_CD.substring(5, 6)))&&
				    !szYD_MTL_W_GP.startsWith("L")){ 			// 광폭은 제외	
*/
				if("D".equals(szYD_SCH_CD.substring(5, 6))|| 	//입고RT(1후판 전 라인라인으로 수정 2020.11.04 윤재광)
				    "E".equals(szYD_SCH_CD.substring(5, 6))||
				    "F".equals(szYD_SCH_CD.substring(5, 6))|| //광폭재도 포함되도록 변경(박종호. 2022.04.28 임진후 사원 요청사항.)
				    "G".equals(szYD_SCH_CD.substring(5, 6)))
					{ 			
					
					szLogMsg = "["+ szOperationName +"] 1후판 온라인 E동 적치가능한 가적베드 조회 시작";
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
					inRecord3 	= JDTORecordFactory.getInstance().create();
					inRecord2 	= JDTORecordFactory.getInstance().create();

					inRecord3.setField("REPR_CD_GP" ,"T00261");	
					inRecord3.setField("CD_GP" ,szYD_SCH_CD.substring(5, 6));	// 입고R/T
					inRecord3.setField("ITEM"  ,szYD_BAY_GP);   				// 입고동
					
					/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
					intRtnVal = ydEqpDao.getYdEqp(inRecord3, outResult1, 999);
					if(intRtnVal > 0) {
						outResult1.first();
						inRecord2  = outResult1.getRecord();
						szFROM_DONG = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, "ITEM1"),"0101")+"01";
						szTO_DONG   = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, "ITEM2"),"0799")+"99";		
					} else {
						szFROM_DONG = "0101";
						szTO_DONG   = "0799"; 
					}
					
					JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
					
					recPara1.setField("YD_GP"			, szYD_GP);
					recPara1.setField("YD_BAY_GP"		, szYD_BAY_GP);
					recPara1.setField("YD_PILING_CD"	, szYD_PILING_CD);
					recPara1.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
					recPara1.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);
					recPara1.setField("FR_YD_STK_BED_NO", "01");
					recPara1.setField("YD_EQP_WRK_SH"	, String.valueOf(intYD_EQP_WRK_SH));
					recPara1.setField("YD_EQP_WRK_WT"	, String.valueOf(intYD_EQP_WRK_WT));
					recPara1.setField("YD_EQP_WRK_T"	, String.valueOf(dblYD_EQP_WRK_T));
					recPara1.setField("YD_SCH_CD"		, szYD_SCH_CD);
					recPara1.setField("AL_FROM"		, szYD_TO_LOC_GUIDE_FROM.substring(0, 6));  //TC072101->TC0721
					recPara1.setField("AL_TO"		, szYD_TO_LOC_GUIDE_TO.substring(0, 6));
					
					//----------------------------------------------------------------------------------------------------------------------
					//	조건에 해당하는 단 정보 조회 - 1후판 적치가능한 가적베드 정보 조회
					//----------------------------------------------------------------------------------------------------------------------
					JDTORecordSet rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
					
					//여기에 검색 범위 추가로 기입필요...대체위치 FROM~TO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  REQ202401530768
					/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithTempBedRatioReNew*/
					/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithTempBedRatioReNewWithAlFrTo */ //신규쿼리 대체범위조건 추가
					//szRtnMsg = DaoManager.getYdStklyr(recPara1, rsResult1, 631);
					szRtnMsg = DaoManager.getYdStklyr(recPara1, rsResult1, 633);
					//----------------------------------------------------------------------------------------------------------------------
					if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {

						srchNconvRecord2VoTmpBed("","",recPara1, rsResult1, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD);
					}
					szLogMsg = "["+ szOperationName +"] 1후판 온라인 E동 적치가능한 가적베드 조회 완료 - 메세지 : " + szRtnMsg;
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					//----------------------------------------------------------------------------------------------------------------------
					// Sorting ?
					//----------------------------------------------------------------------------------------------------------------------
					if( listToLoc.size() > 0 ) {
						Collections.sort(listToLoc, new YdStkLocComparator());
					}
					//----------------------------------------------------------------------------------------------------------------------
					 
					//----------------------------------------------------------------------------------------------------------------------
					//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
					//----------------------------------------------------------------------------------------------------------------------
								
					for(int i = 0; i < listToLoc.size(); i++ ) {
						
						ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
						
						szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 1후판 적치가능한 가적베드 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
			    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
			    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
			    			
							bIS_BED_STACKABLE		= true;
							
							szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
				    		szLogMsg += "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 1후판 온라인 E동 적치가능한 가적베드 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++\n";
				    		szLogMsg += "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
				    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				    		
							break;
						}
					}		
				}
				//----------------------------------------------------------------------------------------------------------------------
				//	1후판 온라인 E동 적치가능한 가적베드 검색조회 완료
				//----------------------------------------------------------------------------------------------------------------------
			}
			String szAPPLY_YDTOYD 		= "N";    // 신이적로직 적용여부
			
			if( !bIS_BED_STACKABLE) {
	    	
		    	//----------------------------------------------------------------------------------------------------------------------
				//	동일한 Piling Code의 적치가능한 베드 조회
				//----------------------------------------------------------------------------------------------------------------------
				szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]의 적치가능한 베드 조회 시작";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------------------------
				//	신 이적작업  적용여부
				//------------------------------------------------------------------------------------------------------------
				if( szYD_EQP_GP.equals("PT") ||
		    		szYD_EQP_GP.equals("TR") ||
		    		szYD_EQP_GP.equals("SL") ) {
					// 기존거 사용함
					szAPPLY_YDTOYD = "N";
			   	} else {	
			   		// 이적만 신규 사용
			   		szAPPLY_YDTOYD = "Y";
			   	}	
						
				if (szAPPLY_YDTOYD.equals("Y")){
				
					if( szYD_EQP_GP.equals("RT")|| 
				    	szYD_EQP_GP.equals("TF")){
						
						szLogMsg="신입고 로직으로 TO위치 검색";
						ydUtils.putLog(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						//----------------------------------------------------------------------------------------------------------------------
						//	이적스판검색범위
						//----------------------------------------------------------------------------------------------------------------------
						outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
						inRecord3 	= JDTORecordFactory.getInstance().create();
						inRecord2 	= JDTORecordFactory.getInstance().create();
	
						inRecord3.setField("REPR_CD_GP" ,"T00011");	
						inRecord3.setField("CD_GP" ,szYD_SCH_CD.substring(5, 6));   // 입고R/T
						inRecord3.setField("ITEM"  ,szYD_BAY_GP);    				// 입고동
						
						/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
						intRtnVal = ydEqpDao.getYdEqp(inRecord3, outResult1, 999);
						if(intRtnVal > 0) {
							outResult1.first();
							inRecord2  = outResult1.getRecord();
							szFROM_DONG = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, "ITEM1"),"0101");
							szTO_DONG   = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, "ITEM2"),"0799");		
						} else {
							szFROM_DONG = "0101";
							szTO_DONG   = "0799"; 
						}
						
					}else{
						szLogMsg="신이적/선별 로직으로 TO위치 검색";
						ydUtils.putLog(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						//----------------------------------------------------------------------------------------------------------------------
						//	이적/선별(ProC로 검색함) 스판검색범위
						//----------------------------------------------------------------------------------------------------------------------
						String sAbleYn = ""; //창고간 통로허용 여부(S:분리, T:통합)
						String sRuleId = ""; //T00021(분리),TI0021(통합)
						
						outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
						inRecord3 	= JDTORecordFactory.getInstance().create();
						inRecord2 	= JDTORecordFactory.getInstance().create();
						
						inRecord3.setField("REPR_CD_GP" ,"TI0001");	
						inRecord3.setField("CD_GP" ,szYD_BAY_GP);    			
						inRecord3.setField("ITEM"  ,"S");	//분리기준
						
						/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
						intRtnVal = ydEqpDao.getYdEqp(inRecord3, outResult1, 999);
						
						if (intRtnVal > 0) {
							sAbleYn = "S";
							sRuleId = "T00021";
							
						} else {
							sAbleYn = "T";
							sRuleId = "TI0021";
						}
						
						outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
						inRecord3 	= JDTORecordFactory.getInstance().create();
						inRecord2 	= JDTORecordFactory.getInstance().create();
						
						inRecord3.setField("REPR_CD_GP" ,sRuleId);	
						inRecord3.setField("CD_GP" ,szYD_EQP_ID.substring(5, 6));    // 크레인 호기			
			
						/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgt*/
						intRtnVal = ydCarSchDao.getYdCarsch(inRecord3, outResult1, 400);
						
						if (intRtnVal > 0) {
							outResult1.first();
							inRecord2 = outResult1.getRecord();
							szFROM_DONG = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, szYD_BAY_GP+"_DONG"),"0101");
							szTO_DONG   = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, szYD_BAY_GP+"_DONG2"),"0799");
						
						} else {
							szFROM_DONG = "0101";
							szTO_DONG   = "0799"; 
						}
					}
					
					//---------------------------------------------------------------------------------------------------------
					// 크레인사양 조회
					// 설비ID로  크크레인 Grab 구분 추출
					//---------------------------------------------------------------------------------------------------------
					ydUtils.putLog(szClassName, szMethodName, "To위치결정[크레인GRAB구분확인]"+szYD_EQP_ID, YdConstant.DEBUG);
					
					JDTORecord recTemp1 	= JDTORecordFactory.getInstance().create();
					JDTORecord recSpec 		= JDTORecordFactory.getInstance().create();
					JDTORecordSet specSet 	= JDTORecordFactory.getInstance().createRecordSet("");
					recTemp1.setField("YD_EQP_ID", szYD_EQP_ID);
					
					szRtnMsg	= DaoManager.getYdCrnspec(recTemp1, specSet, 0);
					
					specSet.first();
					recSpec = specSet.getRecord();
					
					szYD_CRN_GRAB_TP = ydDaoUtils.paraRecChkNull(recSpec, "YD_CRN_GRAB_TP"); // 크레인 Grab 구분
					
					//여기에 검색 범위 추가로 기입 대체위치 FROM~TO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  REQ202401530768
					recTemp.setField("ALT_FLAG", "Y");  //대체범위 추가 필터 플래그 설정
					recTemp.setField("AL_FROM"		, szYD_TO_LOC_GUIDE_FROM.substring(2, 6));  //TC072101->0721
					recTemp.setField("AL_TO"		, szYD_TO_LOC_GUIDE_TO.substring(2, 6));   
					szRtnMsg = getBedWithSamePilingCdNew(szFROM_DONG,szTO_DONG, szYD_CRN_GRAB_TP, recTemp, listToLoc);
				}else{
					//여기에 검색 범위 추가로 기입 대체위치 FROM~TO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  REQ202401530768
					recTemp.setField("ALT_FLAG", "Y");  //대체범위 추가 필터 플래그 설정
					recTemp.setField("AL_FROM"		, szYD_TO_LOC_GUIDE_FROM.substring(2, 6));  //TC072101->0721
					recTemp.setField("AL_TO"		, szYD_TO_LOC_GUIDE_TO.substring(2, 6));					
					
					szRtnMsg = getBedWithSamePilingCd(recTemp, listToLoc);  
				} 
				
				szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]의 적치가능한 베드 조회 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				//----------------------------------------------------------------------------------------------------------------------
			
				//----------------------------------------------------------------------------------------------------------------------
				// Sorting ?
				//----------------------------------------------------------------------------------------------------------------------
				if( listToLoc.size() > 0 ) {
					Collections.sort(listToLoc, new YdStkLocComparator());
				}
				//----------------------------------------------------------------------------------------------------------------------
				 
				//----------------------------------------------------------------------------------------------------------------------
				//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
				//----------------------------------------------------------------------------------------------------------------------
				//boolean bIS_BED_STACKABLE	= false;
				
				for(int i = 0; i < listToLoc.size(); i++ ) {
					
					ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
					
					szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
		    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
		    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
		    			
						bIS_BED_STACKABLE		= true;
						
						szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
			    		szLogMsg += "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++\n";
			    		szLogMsg += "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
			    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			    		
						break;
					}
				}
				
				// SJH05001
				// 입고작업일경우 대형 고객사와 주문량을 CHECK 하여 저장위치 검색 별도 처리 함	(2014.05.23 이적작업일경우도 포함시킴)	
				if( !bIS_BED_STACKABLE ) {
				 	if( szYD_EQP_GP.equals("RT") || 
				    	szYD_EQP_GP.equals("TF") ||
				    	szYD_EQP_GP.equals("PT") ||
				    	szYD_SCH_CD.substring(6, 7).equals("M")	) {
						
				 		JDTORecordSet outResult99 = JDTORecordFactory.getInstance().createRecordSet("");
						JDTORecord 	  inRecord99  = JDTORecordFactory.getInstance().create();
						JDTORecord 	  outRecord99 = JDTORecordFactory.getInstance().create();
						JDTORecord 	  recTemp1 	  = JDTORecordFactory.getInstance().create();
						String szAPPLY_YN1 		  = "N";
						
						if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)) {
							inRecord99.setField("REPR_CD_GP", "T00070");    //저장그룹편성보완
						} else {
							inRecord99.setField("REPR_CD_GP", "K00070");    //저장그룹편성보완
						}
						
						/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
						intRtnVal = ydEqpDao.getYdEqp(inRecord99, outResult99, 999);
						if(intRtnVal > 0) {
							outResult99.first();
							outRecord99 = outResult99.getRecord();
							szAPPLY_YN1 = outRecord99.getFieldString("ITEM1");				
						}
						
						szLogMsg="신저장그룹편성보완 적용 " + szAPPLY_YN1 ;
						ydUtils.putLog(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG);			
						
						if(szAPPLY_YN1.equals("Y")) {
							szSTL_NO	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
							szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 시작 ";
							ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
							
							rsResult = JDTORecordFactory.getInstance().createRecordSet("");
							/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdOrdWgtCheck*/
							intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 606);
							if( intRtnVal < 1 ) {
								return szRtnMsg;
							}
							
							rsResult.first();
							recTemp1 = rsResult.getRecord();
							szCHECK_FLAG 	= ydDaoUtils.paraRecChkNull(recTemp1, "CHECK_FLAG");		        //주문량이 합(수량x두께) > 121	
							
							szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 완료 - 주문량이 합["+szCHECK_FLAG+"]";
							ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
							
							if(szCHECK_FLAG.equals("Y")) {
								szSEARCH_CHANGE = "Y";
							} else {
								szSEARCH_CHANGE = "N";
							}
						}
				 	}	
				}
			}	
			//----------------------------------------------------------------------------------------------------------------------
			//	동일한 Piling Code의 베드가 존재하지 않을 경우에는 
			//	혼적 bed 조회
			//----------------------------------------------------------------------------------------------------------------------
			
			if( !bIS_BED_STACKABLE) {
				
				if(szSEARCH_CHANGE.equals("Y")) {     //입고 대형 고객사 
				
				} else {	
					
					ydStkLocVO			= null;
					
					listToLoc 			= new ArrayList();
					
					szLogMsg = "["+ szOperationName +"] 혼적베드 조회 시작";
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);

					if(	szAPPLY_YDTOYD.equals("Y")){
						//여기에 검색 범위 추가로 기입 대체위치 FROM~TO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  REQ202401530768
						szRtnMsg = getBedWithSimilarGpNew(szSTL_NO, szFROM_DONG, szTO_DONG, szYD_CRN_GRAB_TP, recTemp, listToLoc);  
					} else {
						//여기에 검색 범위 추가로 기입 대체위치 FROM~TO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  REQ202401530768
						szRtnMsg = getBedWithSimilarGp(szSTL_NO, recTemp, listToLoc);
					}	
					szLogMsg = "["+ szOperationName +"] 혼적베드 조회 완료 - 메세지 : " + szRtnMsg;
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					//----------------------------------------------------------------------------------------------------------------------
					// Sorting
					//----------------------------------------------------------------------------------------------------------------------
					if( listToLoc.size() > 0 ) {
						Collections.sort(listToLoc, new YdStkLocComparator());
					}
					//----------------------------------------------------------------------------------------------------------------------
					
					//----------------------------------------------------------------------------------------------------------------------
					//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
					//----------------------------------------------------------------------------------------------------------------------
					
					for(int i = 0; i < listToLoc.size(); i++ ) {
						
						ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
						
						szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 혼적베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
			    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
			    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
							
							bIS_BED_STACKABLE		= true;
							
							szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
				    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
							szLogMsg = "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 혼적베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
		    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    	    		szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
		    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    	    		
							break;
						}
					}
					//----------------------------------------------------------------------------------------------------------------------
				}	
			}
			
			//----------------------------------------------------------------------------------------------------------------------
			//	동일한 Piling Code의 베드가 존재하지 않을 경우에는 
			//	길이구분/폭구분이 동일한 공베드를 조회
			//----------------------------------------------------------------------------------------------------------------------
			if( !bIS_BED_STACKABLE ) {
				
				//------------------------------------------------------------------------------
				//	위에서 조회된 결과를 갖고 있으므로 사용하기 전에 값을 초기화시킴
				//	수정자 : 임춘수
				//	수정일 : 2010.01.04
				//------------------------------------------------------------------------------
				ydStkLocVO			= null;
				
				listToLoc 			= new ArrayList();
				
				szLogMsg = "["+ szOperationName +"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 적치가능한 공베드 조회 시작";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				if(	szAPPLY_YDTOYD.equals("Y")){
					//여기에 검색 범위 추가로 기입...대체위치 FROM~TO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  REQ202401530768
					szRtnMsg = getEmptyBedWithSameLWGpNew(szFROM_DONG, szTO_DONG, szYD_CRN_GRAB_TP, recTemp, listToLoc, szYD_CRN_SCH_ID);  //개발함.
				} else {
					//여기에 검색 범위 추가로 기입...대체위치 FROM~TO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  REQ202401530768					
					szRtnMsg = getEmptyBedWithSameLWGp(recTemp, listToLoc); //개발함.
				}	
	
				
				szLogMsg = "["+ szOperationName +"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 적치가능한 공베드 조회 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				//----------------------------------------------------------------------------------------------------------------------
				// Sorting
				//----------------------------------------------------------------------------------------------------------------------
				if( listToLoc.size() > 0 ) {
					Collections.sort(listToLoc, new YdStkLocComparator());
				}
				//----------------------------------------------------------------------------------------------------------------------
				
				//----------------------------------------------------------------------------------------------------------------------
				//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
				//----------------------------------------------------------------------------------------------------------------------
				
				for(int i = 0; i < listToLoc.size(); i++ ) {
					
					ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
					
					szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
		    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
		    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
						
						bIS_BED_STACKABLE		= true;
						
						szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
			    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						szLogMsg = "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
	    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    	    		szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    	    		
						break;
					}
				}
			}
	
			boolean isAutoB_toXX=false; //1후판 B동 소폭재이고, 입고이적시 최종 TO위치(소폭베드) 못찾은 경우 
		 	 
		 	
			if( !bIS_BED_STACKABLE ) {
				 if( PlateGdsYdUtil.isApplyYn("입고이적 권하위치(XX00)예외 로직 적용 여부") ){
					 //일반적인 협폭재 중간경유 베드 검색의 경우도 여기에(bIS_BED_STACKABLE=False)걸리는지 확인필요
					 //bIS_BED_STACKABLE=False이면서 협폭재인 경우, 아래에서 에러 발생.권하위치 셋팅 변수가 null
  					 if (("TBCRB3".equals(szYD_EQP_ID) || "TBCRB4".equals(szYD_EQP_ID)) && ("RT".equals(szYD_EQP_GP) && dblYD_EQP_WRK_W <= 2100)){
  						isAutoB_toXX=true;  //1후판 B동 소폭재 입고 작업의 최종 TO위치(소폭베드) 못찾은 경우
  						szLogMsg = "["+ szOperationName +"] 협폭 권하위치검색 실패 - 협폭재 중간경유 BED 검색, isAutoB_toXX:"+isAutoB_toXX;
  						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
  					 }
  					 else {
    					 if(szSEARCH_CHANGE.equals("Y")) {
    							
    						szLogMsg = "["+ szOperationName +"] 권하위치검색 실패 - 대형고객사 적치가능한 베드가 존재하지 않습니다.";
    						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    						return YdConstant.RETN_BIG_NOT_EXIST_BED;
    						
    					 } else {
    						szLogMsg = "["+ szOperationName +"] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
    						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    						return YdConstant.RETN_NOT_EXIST_BED;
    					 }
  				     }
  			     }
  			     else {
  					 if(szSEARCH_CHANGE.equals("Y")) {
  							
  						szLogMsg = "["+ szOperationName +"] 권하위치검색 실패 - 대형고객사 적치가능한 베드가 존재하지 않습니다.";
  						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
  						return YdConstant.RETN_BIG_NOT_EXIST_BED;
  						
  					 } else {
  						szLogMsg = "["+ szOperationName +"] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
  						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
  						return YdConstant.RETN_NOT_EXIST_BED;
  					 }	
  			     }
			}
	
			/*
			 * 권하위치 최종결정정보 셋팅.
			 */
			if(isAutoB_toXX){  //1후판B동 소폭재 중폭베드 입고시, 최종위치(소폭베드) 못찾은 경우는 위치값이 비어있어서, 위치셋팅하면 NULL EXCEPTION 발생함.
					szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 협폭check:Y 이면서, 최종 TO위치 탐색 실패하여, 권하예정지 셋팅 안함(NULL EXCEPTION 방지)";
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}
			else{
					szYD_DN_STK_COL_GP 	= ydStkLocVO.getYdStkColGp();
					szYD_DN_STK_BED_NO 	= ydStkLocVO.getYdStkBedNo();
					szYD_DN_WO_LOC		= szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;
					szYD_DN_WO_LAYER 	= ydStkLocVO.getYdStkLyrNo();					
			}
			/*	
			else{
			szYD_DN_STK_COL_GP 	= ydStkLocVO.getYdStkColGp();
			szYD_DN_STK_BED_NO 	= ydStkLocVO.getYdStkBedNo();
			szYD_DN_WO_LOC		= szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;
			szYD_DN_WO_LAYER 	= ydStkLocVO.getYdStkLyrNo();
			}
			*/
			
			// AT0000_물류시스템 개선 2022.10.27 Start
			// B동 B3,B4 무인mode RT/TF 입고 시 협폭 2100mm이하 일 경우 중폭 베드에서 정렬 후 최종 위치에 권하
//			JDTORecordSet 	outResult 	= JDTORecordFactory.getInstance().createRecordSet("");
//			JDTORecord 		inRecord1 	= JDTORecordFactory.getInstance().create();
//			JDTORecord 		outRecord1	= JDTORecordFactory.getInstance().create();			
			JDTORecord      params     = JDTORecordFactory.getInstance().create();
			
//			inRecord1.setField("YD_EQP_ID", szYD_EQP_ID);  
//			intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 0);
//			if(intRtnVal > 0) {
//			   outResult.first();
//			   outRecord1  = outResult.getRecord();
//			   sCrnWrkMode2 = outRecord1.getFieldString("YD_EQP_WRK_MODE2");				
//			}
			//크레인 자동화 소폭 > 중폭 입고이적 체크(TO위치를 기존 소폭 TO위치에서 중폭 경유 TO위치로 변경) 김기태 부장.
			szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 협폭check 시작  대상재 폭 : " + dblYD_EQP_WRK_W;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 최종목적지 bed :" + szYD_DN_WO_LOC + "목적지 폭구분 : " + szYD_STK_BED_W_GP_G ;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
			if ("TBCRB3".equals(szYD_EQP_ID) || "TBCRB4".equals(szYD_EQP_ID)){	
			   if (("RT".equals(szYD_EQP_GP)) && dblYD_EQP_WRK_W < 2250){
				   if( PlateGdsYdUtil.isApplyYn("소폭제 중간 BED 사용 신규로직 적용 여부") ){
				       if ("S".equals(szYD_MTL_W_GP.substring(0,1))){
						   JDTORecordSet   rsStopover   = JDTORecordFactory.getInstance().createRecordSet("");
						   String szFROM_COL_GP="";
						   String szTO_COL_GP ="";
						   
						   if(isAutoB_toXX){ //최종 to위치 탐색 실패시, 작업예약 등록시 셋팅된 초기 to위치값으로 지정함.
							   szYD_TO_LOC_GUIDE = szYD_TO_LOC_GUIDEbak;   
						   }
						   else{
							   szYD_TO_LOC_GUIDE = szYD_DN_WO_LOC;
						   }
							   
						    szFROM_COL_GP ="TB" + szFROM_DONG;  //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리
						    szTO_COL_GP =  "TB" + szTO_DONG; //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리
						   /*
						   else{
							szYD_TO_LOC_GUIDE = szYD_DN_WO_LOC;	
						    	    	
						    szFROM_COL_GP =  szYD_DN_STK_COL_GP.substring(0,2) + szFROM_DONG;
						    szTO_COL_GP =  szYD_DN_STK_COL_GP.substring(0,2) + szTO_DONG;
						   }
						   */	        
						    szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 경유위치 결정대상New : From Bed :" + szFROM_COL_GP + " To Bed : " + szTO_COL_GP;
						   	ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						        		    
						   	params.setField("FROM_COL_GP", szFROM_COL_GP); 
						   	params.setField("TO_COL_GP",   szTO_COL_GP);
						   	params.setField("MTL_L_GP",    szYD_MTL_L_GP);
						        			
						   	intRtnVal = commDao.select(params, rsStopover, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectStopOverQuery");  
						   	if (intRtnVal <= 0){
						   	   szLogMsg = "["+szOperationName+"] 스케줄[" + szYD_CRN_SCH_ID + "] : 주작업[TO위치결정방법- 경유위치 결정 실패!!] intRtnVal : " + intRtnVal;
						       ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
						       return YdConstant.RETN_NOT_EXIST_BED;
						   	}
						  	
							szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 주작업[TO위치결정방법- 경유위치 결정S 성공 intRtnVal : " + intRtnVal;
						   	ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						   
						   	szYD_DN_STK_COL_GP = rsStopover.getRecord(0).getFieldString("YD_STK_COL_GP"); 
						   	szYD_DN_STK_BED_NO = rsStopover.getRecord(0).getFieldString("YD_STK_BED_NO");  
						    szYD_DN_WO_LAYER   = PlateGdsYdUtil.trim(rsStopover.getRecord(0).getFieldString("YD_STK_LYR_NO"));
						   	szYD_DN_WO_LOC     = szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;				   
						   	szCflag = "Ok";
						  	szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 주작업[TO위치결정방법- 경유위치 결정E 성공 : " + szYD_DN_STK_COL_GP + " szCflag : " + szCflag;
						   	ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					    }
				   }
				   else {
					   if ("S0".equals(szYD_MTL_W_GP)){
						   JDTORecordSet   rsStopover   = JDTORecordFactory.getInstance().createRecordSet("");
							szYD_TO_LOC_GUIDE = szYD_DN_WO_LOC;	
						    	    	
						    String szFROM_COL_GP =  szYD_DN_STK_COL_GP.substring(0,2) + szFROM_DONG;
						    String szTO_COL_GP =  szYD_DN_STK_COL_GP.substring(0,2) + szTO_DONG;
						    	        
						    szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 경유위치 결정대상 : From Bed :" + szFROM_COL_GP + " To Bed : " + szTO_COL_GP;
						   	ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						        		    
						   	params.setField("FROM_COL_GP", szFROM_COL_GP); 
						   	params.setField("TO_COL_GP",   szTO_COL_GP);
						   	params.setField("MTL_L_GP",    szYD_MTL_L_GP);
						        			
						   	intRtnVal = commDao.select(params, rsStopover, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectStopOverQuery");  
						   	if (intRtnVal <= 0){
						   	   szLogMsg = "["+szOperationName+"] 스케줄[" + szYD_CRN_SCH_ID + "] : 주작업[TO위치결정방법- 경유위치 결정 실패!!] intRtnVal : " + intRtnVal;
						       ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
						       return YdConstant.RETN_NOT_EXIST_BED;
						   	}
						  	
							szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 주작업[TO위치결정방법- 경유위치 결정S 성공 intRtnVal : " + intRtnVal;
						   	ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						   
						   	szYD_DN_STK_COL_GP = rsStopover.getRecord(0).getFieldString("YD_STK_COL_GP"); 
						   	szYD_DN_STK_BED_NO = rsStopover.getRecord(0).getFieldString("YD_STK_BED_NO");  
						    szYD_DN_WO_LAYER   = PlateGdsYdUtil.trim(rsStopover.getRecord(0).getFieldString("YD_STK_LYR_NO"));
						   	szYD_DN_WO_LOC     = szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;				   
						   	szCflag = "Ok";
						  	szLogMsg = "["+szOperationName+"] 크레인 스케줄[" + szYD_CRN_SCH_ID + "] : 주작업[TO위치결정방법- 경유위치 결정E 성공 : " + szYD_DN_STK_COL_GP + " szCflag : " + szCflag;
						   	ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					    }
				    }
				}
			}
				
			// AT0000_물류시스템 개선 2022.10.27 End
	
			if( szYD_SCH_CD.substring(2, 4).equals("SL")) {	
				if( !szYD_TO_LOC_GUIDE.equals(szYD_DN_WO_LOC)){ 
					szLogMsg = "["+ szOperationName +"] 권하위치검색 실패 - 선별은 야드To위치Guide만 처리됨";
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					return YdConstant.RETN_NOT_EXIST_BED;
				}	
			}
		}	
		//----------------------------------------------------------------------------------------------------------------------
		// 권하지시위치 수정
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 시작";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		JDTORecord recUpCrnSch	= null;
		
		recUpCrnSch = JDTORecordFactory.getInstance().create();
		recUpCrnSch.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);									//크레인스케줄ID
		if ("Ok".equals(szCflag)) {                                                                         //ATOOO 물류시스템 개선 2022.10.27 최종 to위치
			recUpCrnSch.setField("YD_TO_LOC_GUIDE", 		szYD_TO_LOC_GUIDE);	                               
		}
		recUpCrnSch.setField("YD_EQP_ID", 				szYD_EQP_ID);										//크레인설비ID
		recUpCrnSch.setField("YD_UP_STK_COL_GP", 		szYD_UP_STK_COL_GP);								//권상지시위치 - 적치열
		recUpCrnSch.setField("YD_UP_STK_BED_NO", 		szYD_UP_STK_BED_NO);								//권상지시위치 - 적치베드
		recUpCrnSch.setField("YD_DN_STK_COL_GP", 		szYD_DN_STK_COL_GP);								//권하지시위치 - 적치열
		recUpCrnSch.setField("YD_DN_STK_BED_NO", 		szYD_DN_STK_BED_NO);								//권하지시위치 - 적치베드
		
		//----------------------------------------------------------------------------------------------------------------------
		//	권상지시위치 업데이트
		//----------------------------------------------------------------------------------------------------------------------
		if( bUP_UPDT_NEEDED ) {
			szLogMsg = "["+ szOperationName +"] 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"] 크레인스케줄에 등록";
    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			recUpCrnSch.setField("YD_UP_WO_LOC", 		szYD_UP_WO_LOC);									//권상지시위치
			recUpCrnSch.setField("YD_UP_WO_LAYER", 		szYD_UP_WO_LAYER);									//권상지시단
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		recUpCrnSch.setField("YD_DN_WO_LOC", 			szYD_DN_WO_LOC);									//권하지시위치
		recUpCrnSch.setField("YD_DN_WO_LAYER", 			szYD_DN_WO_LAYER);									//권하지시단
		recUpCrnSch.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));					//크레인작업재료 총매수
		recUpCrnSch.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));					//크레인작업재료 총중량
		recUpCrnSch.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));					//크레인작업재료 총높이
		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);								//크레인작업재료 중 최대 폭
		recUpCrnSch.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);								//크레인작업재료 중 최대 길이
		recUpCrnSch.setField("MODIFIER", 				szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		
		szRtnMsg = CrnSchUtil.uptCrnSchXYCord(recUpCrnSch);
    	
    	szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 완료";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		//----------------------------------------------------------------------------------------------------------------------
    	
		//----------------------------------------------------------------------------------------------------------------------
		//	권하지시위치에 재료를 권하대기로 등록
		//----------------------------------------------------------------------------------------------------------------------
    	szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]에 크레인작업재료 등록 시작";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		szRtnMsg	= uptDnWaitOnYdStkLyr(rsCrnwrkmtl, szYD_DN_WO_LOC, szYD_DN_WO_LAYER);
    	
		szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]에 크레인작업재료 등록 완료 - 메세지 : " + szRtnMsg;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		//----------------------------------------------------------------------------------------------------------------------
		
		return YdConstant.RETN_CD_SUCCESS;
	} // end of procMainWrkToLocForPlateYdForChgCrn  --크레인변경(고장)에 따른 TO위치 재탐색 로직 신규	
	
	/**
	 * 크레인변경(고장)에 따른 TO위치 재탐색 로직 신규 25.05.27 renew 버전. by hjw   procMainWrkToLocForPlateYd2nd 기반 수정
	 * 후판제품 임진후 기사 요청대로 to위치 결정 방법 개선 및 to위치 결정 순서 조정기능 추가.
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String procMainWrkToLocForPlateYdForChgCrn2nd(
			//JDTORecord msgRecord					/* 전문 */  //안쓰이는듯 제외시키자.
			 JDTORecordSet rsCrnwrkmtl				/* 크레인작업재료 */
			, JDTORecord recCrnSch					/* 크레인스케줄정보 */
		//	, JDTORecord recWbook					/* 작업예약정보 */    //SCH_CD와 TO위치가이드 가져오기위한것같은데, SCH_CD는 스케줄꺼 참고하면되고, TO위치가이드는 빈값으로 할것이니 인자 제외시키자.
			, String YD_TO_LOC_GUIDE_FROM           /* to위치 from */  //변수 추가
			, String YD_TO_LOC_GUIDE_TO             /* to위치 to */    //변수 추가
			) throws JDTOException {
		/*
		 * 업무기준	:	1. 
		 * 
		 * 수정자	: 허정욱.
		 * 수정일	:
		 * 				1. 2025.06.11 - 최초등록
		 */
		
		String methodName				 = "procMainWrkToLocForPlateYd2nd";
		String operationName			 = "주작업TO위치결정(후판제품)2nd";
		String logMsg					 = null;
		String rtnMsg 				     = YdConstant.RETN_CD_SUCCESS;
		String[] ydStkLyrMtlStat         = {YdConstant.YD_STK_LYR_MTL_STAT_DN_WAIT, YdConstant.YD_STK_LYR_MTL_STAT_UN_WAIT};
		
		String logId                     = ydUtils.getJDTOLogId(recCrnSch, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		logMsg = "주작업TO위치결정(후판제품)(" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
		YdPlateCommDAO commDao = new YdPlateCommDAO(); 
		JDTORecord		recPara		 = null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//------------------------------------- 사용자정의위치(입고예정위치)에 대한 TO위치결정 -------------------------------------
		//----------------------------------------------------------------------------------------------------------------------
	
		//함수명 추가 메세지 설정
		String ydSchCd	  = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD");
		String operationMsg = getOperationMsgFromSchCd(ydSchCd);
				
		if(!operationMsg.isEmpty()){
			operationName += "-" +operationMsg;
		}
		
	
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------
		
		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		
		String ydCrnSchId       = ydDaoUtils.paraRecChkNull(recCrnSch,"YD_CRN_SCH_ID");					//크레인스케줄ID
		String ydEqpId          = ydDaoUtils.paraRecChkNull(recCrnSch,"YD_EQP_ID");						//크레인설비ID
		
		int ydEqpWrkSh      	= ydDaoUtils.paraRecChkNullInt(recPara,"SH_CNT");						//크레인작업재료 총매수
		int ydEqpWrkWt      	= ydDaoUtils.paraRecChkNullInt(recPara,"SUM_MTL_WT");					//크레인작업재료 총중량
		double ydEqpWrkT      	= ydDaoUtils.paraRecChkNullDouble(recPara,"SUM_MTL_T");					//크레인작업재료 총높이
		
		String ydEqpWrkMaxW		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_W");						//크레인작업재료 중 최대 폭
		String ydEqpWrkMaxL		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_L");						//크레인작업재료 중 최대 길이 
				
		String ydDnStkColGp 	= "";
		String ydDnStkBedNo 	= "";
		
		logMsg = "["+ operationName +"] 크레인작업재료의 총매수 :" + ydEqpWrkSh;
		logMsg += ", 총중량 :" + ydEqpWrkWt;
		logMsg += ", 총높이  :" + ydEqpWrkT;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		String ydToLocGuide		= ""; //TO위치가이드 빈값으로 변경
		
		String ydToLocGuideFrom = YD_TO_LOC_GUIDE_FROM;
		String ydToLocGuideTo   = YD_TO_LOC_GUIDE_TO;
		
		logMsg = "["+ operationName +"] ---------------------- 야드To위치Guide From : " + ydToLocGuideFrom + " 야드To위치Guide To : " + ydToLocGuideTo + " --------------------------";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	최하단 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------

		String stlNo	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
		
		logMsg = "["+ operationName +"] 크레인작업재료의 최하단 재료정보["+stlNo+"]를 저장품에서 조회 시작 ";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		rtnMsg = DaoManager.getYdStock(recPara, rsResult, 0);
		if( !rtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			return rtnMsg;
		}
		
		rsResult.first();
		JDTORecord recTemp = rsResult.getRecord();
		
		String ydMtlLGp 	   = ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");			//크레인작업 최하단재료의 길이구분
		String ydMtlWGp 	   = ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_W_GP");			//크레인작업 최하단재료의 폭구분
		String ydPilingCd 	   = ydDaoUtils.paraRecChkNull(recTemp, "YD_PILING_CD");			//크레인작업 최하단재료의 Piling코드

		String ydRcptPlnStrLoc = ydDaoUtils.paraRecChkNull(recTemp, "YD_RCPT_PLN_STR_LOC");//입고예정위치
		
			
		
		logMsg = "["+ operationName +"] 크레인작업재료의 최하단 재료정보["+stlNo+"]를 저장품에서 조회 완료 "
				+ "- 길이구분["+ydMtlLGp+"], 폭구분["+ydMtlWGp+"], Piling코드["+ydPilingCd+"], 입고예정위치 ["+ydRcptPlnStrLoc+"]";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
				
		//----------------------------------------------------------------------------------------------------------------------
		//	권하중이거나 권상중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		String ydUpWoLoc	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");		
		String ydUpWoLayer 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER");
		String ydDnWoLoc    = "";
		String ydDnWoLayer  = "";
		String ydUpStkColGp = "";
		String ydUpStkBedNo = "";
		boolean isUpLocUpdateNeed = false;
		
	
		if( ydUpWoLoc.isEmpty() ) {
			logMsg = "["+ operationName +"] 크레인작업재료의 최하단 재료정보["+stlNo+"]에 대한 권하 또는 권상위치 조회 시작 ";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			rtnMsg = YdCommonUtils.getYdStkLyrWithMtlStat(recPara, rsResult, ydStkLyrMtlStat);
			
			if( !rtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				return rtnMsg;
			}
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			logMsg = "["+ operationName +"] 크레인작업재료의 최하단 재료정보["+stlNo+"]에 대한 권하 또는 권상위치 조회 완료  ";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			
			ydUpStkColGp 		= recTemp.getFieldString("YD_STK_COL_GP");
			ydUpStkBedNo 		= recTemp.getFieldString("YD_STK_BED_NO");
			ydUpWoLoc 			= ydUpStkColGp + ydUpStkBedNo;
			ydUpWoLayer 		= recTemp.getFieldString("YD_STK_LYR_NO");
			
			isUpLocUpdateNeed			= true;
			
			logMsg = "["+ operationName +"] 조회된 권상지시위치["+ydUpWoLoc+"], 권상지시단["+ydUpWoLayer+"]";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		}else{
			ydUpStkColGp = ydUpWoLoc.substring(0, 6);
			ydUpStkBedNo = ydUpWoLoc.substring(6);
			
			logMsg = "["+ operationName +"] 크레인스케줄에 등록된 권상지시위치["+ydUpWoLoc+"], 권상지시단["+ydUpWoLayer+"]";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
    	//	해당베드정보에 적치가능한 지를 체크
    	//YD_TO_LOC_GUIDE	- 사용자지정위치(적치열+적치베드)
		// * 				2) YD_EQP_WRK_SH	- 작업총매수
		// * 				3) YD_EQP_WRK_WT	- 작업총중량
		// * 				4) YD_EQP_WRK_T		- 작업총두께
		// * 				5) YD_SCH_CD		- 스케줄코드
    	//----------------------------------------------------------------------------------------------------------------------
		//특정 조건에 대한 예외처리 후 return 
		/*
		 * 2014.03.25 윤재광
		 * 2후판 제품창고 사내절단장 북아웃 요구시 디폴트 저장위치로 TO위치 결정
		 */
		if("TB010101".equals(ydToLocGuide)||
		   "TB033101".equals(ydToLocGuide)||
		   "TB032801".equals(ydToLocGuide)  //TB033101->TB032801로 변경 요청. 2022.11.30  후판품질팀 서승범 책임. 1673
		   ){
			ydDnWoLoc		= ydDnStkColGp + ydDnStkBedNo;
			ydDnWoLayer 	= "001";
			
			JDTORecord recUpdCrnSch = JDTORecordFactory.getInstance().create();
			recUpdCrnSch.setField("LOG_ID", logId);
			
			recUpdCrnSch.setField("YD_CRN_SCH_ID"			, ydCrnSchId);
			recUpdCrnSch.setField("YD_EQP_ID"				, ydEqpId);
			recUpdCrnSch.setField("YD_TO_LOC_GUIDE"			, ydToLocGuide);
			recUpdCrnSch.setField("YD_UP_WO_LOC"			, ydUpWoLoc);
			recUpdCrnSch.setField("YD_UP_WO_LAYER"			, ydUpWoLayer);
			recUpdCrnSch.setField("YD_DN_WO_LOC"			, ydDnWoLoc);
			recUpdCrnSch.setField("YD_DN_WO_LAYER"			, ydDnWoLayer);
			recUpdCrnSch.setField("IS_UP_LOC_UPDATE_NEED"	, isUpLocUpdateNeed);
			recUpdCrnSch.setField("MAX_MTL_L"	, ydEqpWrkMaxL);
							
			return updCrnSchDnLoc(recUpdCrnSch, rsCrnwrkmtl);
			
		} else if("TCRTUT45".equals(ydToLocGuide)||
				 "TCRTUT13".equals(ydToLocGuide)||
				 "TC010101".equals(ydToLocGuide)||
				 "TC010103".equals(ydToLocGuide)|| // 2020.10.14 (김도훈 매니저 요청, TESTPLATE To위치 지정 베드)
				 "TC010201".equals(ydToLocGuide)||
				 "TC010301".equals(ydToLocGuide)||
				 "TC010303".equals(ydToLocGuide)||
				 ("TCRTRA30".equals(ydToLocGuide)&&!ydRcptPlnStrLoc.startsWith("TC"))||
				 ("TCRTRA40".equals(ydToLocGuide)&&!ydRcptPlnStrLoc.startsWith("TC"))||
				 ("TCRTRB30".equals(ydToLocGuide)&&!ydRcptPlnStrLoc.startsWith("TC"))||
				 ("TCRTRB40".equals(ydToLocGuide)&&!ydRcptPlnStrLoc.startsWith("TC"))){
			  
			JDTORecordSet rsDanPok 	= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recDanPok 	= JDTORecordFactory.getInstance().create();
			recDanPok.setField("YD_CRN_SCH_ID", ydCrnSchId);
			recDanPok.setField("TO_LOC"		  , ydToLocGuide);
			
			String sGbn = "";
			if("TCRTUT45".equals(ydToLocGuide)){
				sGbn = "RA";
			}else{
				sGbn = "UT";
			}
			recDanPok.setField("GBN", sGbn);

			rtnMsg = DaoManager.getToLocWithDanPok(recDanPok, rsDanPok, 2);
	    	
	    	if( rtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    		
	    		rsDanPok.first();
	    		
	    		recDanPok			= rsDanPok.getRecord();
	    		
	    		ydDnStkColGp	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_COL_GP"); 
	    		ydDnStkBedNo	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_BED_NO"); 
	    		ydDnWoLoc	 	= ydDnStkColGp + ydDnStkBedNo;
	    		ydDnWoLayer 	= ydDaoUtils.paraRecChkNull(recDanPok, "YD_STK_LYR_NO");
	    		
	    		JDTORecord recUpdCrnSch = JDTORecordFactory.getInstance().create();
				recUpdCrnSch.setResultCode(logId);
				
				recUpdCrnSch.setField("YD_CRN_SCH_ID"			, ydCrnSchId);
				recUpdCrnSch.setField("YD_EQP_ID"				, ydEqpId);
				recUpdCrnSch.setField("YD_TO_LOC_GUIDE"			, ydToLocGuide);
				recUpdCrnSch.setField("YD_UP_WO_LOC"			, ydUpWoLoc);
				recUpdCrnSch.setField("YD_UP_WO_LAYER"			, ydUpWoLayer);
				recUpdCrnSch.setField("YD_DN_WO_LOC"			, ydDnWoLoc);
				recUpdCrnSch.setField("YD_DN_WO_LAYER"			, ydDnWoLayer);
				recUpdCrnSch.setField("IS_UP_LOC_UPDATE_NEED"	, isUpLocUpdateNeed);
				recUpdCrnSch.setField("MAX_MTL_L"	, ydEqpWrkMaxL);
				
				return updCrnSchDnLoc(recUpdCrnSch, rsCrnwrkmtl);
	    		
	    	}else{
	    		return YdConstant.RETN_NOT_EXIST_BED;
	    	}
	    	
		}
		
		//선별작업인데 to위치 guide 에 적치 불가능할 경우 실패 
		else if( ydSchCd.substring(2, 4).equals("SL")) {	
			logMsg = "["+ operationName +"] 권하위치검색 실패 - 선별은 야드To위치Guide만 처리됨";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_NOT_EXIST_BED;
		}
		//특정 조건에 대한 예외처리 종료 
    	String ydEqpGp	= ydUpWoLoc.substring(2, 4);
    	
    	
    	//입고작업이 아닐땐 대체위치 탐색 X
		if( !YdConstant.YD_EQP_GP_ROLLERTABLE.equals(ydEqpGp) && !YdConstant.YD_EQP_GP_TRANSFER.equals(ydEqpGp) ) {
			logMsg = "입고작업이 아닐시, 고장시 대체검색범위 탐색 안함 (" + methodName + ") 끝";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			
			return YdConstant.RETN_NOT_EXIST_BED;
		}
 
		
    /////
    	String szCflag     = "";          //AT000 물류시스템 개선 2022.11.17


    	YdStkLocVO		ydStkLocVO		= null;

		
		JDTORecord    altPara    	= JDTORecordFactory.getInstance().create();
		
		altPara.setField("YD_EQP_WRK_SH", 		String.valueOf(ydEqpWrkSh));		//크레인작업재료 총매수
		altPara.setField("YD_EQP_WRK_WT", 		String.valueOf(ydEqpWrkWt));		//크레인작업재료 총중량
		altPara.setField("YD_EQP_WRK_T", 		String.valueOf(ydEqpWrkT));			//크레인작업재료 총높이
		altPara.setField("YD_SCH_CD", 			ydSchCd);							//크레인스케줄코드
		altPara.setField("YD_MTL_L_GP", 		ydMtlLGp);							//크레인작업 최하단재료의 길이구분
		altPara.setField("YD_MTL_W_GP", 		ydMtlWGp);							//크레인작업 최하단재료의 폭구분
		altPara.setField("YD_STK_COL_GP",		ydUpStkColGp);
		altPara.setField("YD_STK_BED_NO",		ydUpStkBedNo);
		altPara.setField("AL_FROM", 			ydToLocGuideFrom);
		altPara.setField("AL_TO", 			ydToLocGuideTo);
		altPara.setField("LOG_ID", 			logId);
    	
    				
		//rt에서 가까운순으로 고장범위 내에서 탐색 
		ydStkLocVO = getBedWithAltRangeAndRt(altPara);
    	
    	boolean isAutoB_toXX = false;
		if( ydStkLocVO == null ) {
			if( PlateGdsYdUtil.isApplyYn("입고이적 권하위치(XX00)예외 로직 적용 여부") ){
				//일반적인 협폭재 중간경유 베드 검색의 경우도 여기에(bIS_BED_STACKABLE=False)걸리는지 확인필요
				//bIS_BED_STACKABLE=False이면서 협폭재인 경우, 아래에서 에러 발생.권하위치 셋팅 변수가 null
				 if (("TBCRB3".equals(ydEqpId) || "TBCRB4".equals(ydEqpId)) && ("RT".equals(ydEqpGp) && Double.parseDouble(ydEqpWrkMaxW) <= 2100)){
					isAutoB_toXX=true;  //1후판 B동 소폭재 입고 작업의 최종 TO위치(소폭베드) 못찾은 경우
					logMsg = "["+ operationName +"] 협폭 권하위치검색 실패 - 협폭재 중간경유 BED 검색, isAutoB_toXX:"+isAutoB_toXX;
					ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
				 }
				 else {
					 if(isRcvPlateBigCust(recTemp)) {
						 logMsg = "["+ operationName +"] 권하위치검색 실패 - 대형고객사 적치가능한 베드가 존재하지 않습니다.";
						 ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
						 return YdConstant.RETN_BIG_NOT_EXIST_BED;
						
					 } else {
						 logMsg = "["+ operationName +"] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
						 ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
						return YdConstant.RETN_NOT_EXIST_BED;
					 }
			     }
		     }
		     else {
				 if(isRcvPlateBigCust(recTemp)) {
					 logMsg = "["+ operationName +"] 권하위치검색 실패 - 대형고객사 적치가능한 베드가 존재하지 않습니다.";
					 ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					 return YdConstant.RETN_BIG_NOT_EXIST_BED;
					
				 } else {
					 logMsg = "["+ operationName +"] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
					 ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					return YdConstant.RETN_NOT_EXIST_BED;
				 }	
		     }
		}
		
	
		
		if(isAutoB_toXX){//1후판B동 소폭재 중폭베드 입고시, 최종위치(소폭베드) 못찾은 경우는 위치값이 비어있어서, 위치셋팅하면 NULL EXCEPTION 발생함.
			logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 협폭check:Y 이면서, 최종 TO위치 탐색 실패하여, 권하예정지 셋팅 안함(NULL EXCEPTION 방지)";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		}
		else{

			ydDnStkColGp 		= ydStkLocVO.getYdStkColGp();
			ydDnStkBedNo 	= ydStkLocVO.getYdStkBedNo();
			ydDnWoLoc		= ydDnStkColGp + ydDnStkBedNo;
			ydDnWoLayer 	= ydStkLocVO.getYdStkLyrNo();
		
			
		}
		
		//크레인 자동화 소폭 > 중폭 입고이적 체크(TO위치를 기존 소폭 TO위치에서 중폭 경유 TO위치로 변경) 김기태 부장.
		logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 협폭check 시작  대상재 폭 : " + ydEqpWrkMaxW;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 최종목적지 bed :" + ydDnWoLoc;

		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		//자동화 소폭재 예외처리
		if ("TBCRB3".equals(ydEqpId) || "TBCRB4".equals(ydEqpId)){	
		   if (("RT".equals(ydEqpGp)) && Double.parseDouble(ydEqpWrkMaxW) < 2250 ){
				if( PlateGdsYdUtil.isApplyYn("소폭제 중간 BED 사용 신규로직 적용 여부") ){
			       if ("S".equals(ydMtlWGp.substring(0,1))){
					   JDTORecordSet   rsStopover   = JDTORecordFactory.getInstance().createRecordSet("");
					   String fromColGp="";
					   String toColGp ="";
					   
					   ydToLocGuide = ydDnWoLoc;
						   
					   fromColGp ="TB" + ydToLocGuideFrom.substring(2);  //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리
					   toColGp =  "TB" + ydToLocGuideTo.substring(2); //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리	   
		
					   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 경유위치 결정대상New : From Bed :" + fromColGp + " To Bed : " + toColGp;
					   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					   
					   JDTORecord recParam = JDTORecordFactory.getInstance().create();
					   recParam.setField("FROM_COL_GP", fromColGp); 
					   recParam.setField("TO_COL_GP",   toColGp);
					   recParam.setField("MTL_L_GP",    ydMtlLGp);
					        			
					   int rtnVal = commDao.select(recParam, rsStopover, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectStopOverQuery");  
					   if (rtnVal <= 0){
						   logMsg = "["+operationName+"] 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정 실패!!] intRtnVal : " + rtnVal;
					   	   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					       return YdConstant.RETN_NOT_EXIST_BED;
					   	}
					  	
					   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정S 성공 intRtnVal : " + rtnVal;
					   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					   
					   ydDnStkColGp = rsStopover.getRecord(0).getFieldString("YD_STK_COL_GP"); 
					   ydDnStkBedNo = rsStopover.getRecord(0).getFieldString("YD_STK_BED_NO");  
					   ydDnWoLayer   = PlateGdsYdUtil.trim(rsStopover.getRecord(0).getFieldString("YD_STK_LYR_NO"));
					   ydDnWoLoc     = ydDnStkColGp + ydDnStkBedNo;				   
					   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정E 성공 : " + ydDnStkColGp + " szCflag : " + szCflag;
					   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
				    }
			   }
			   else {
				   if ("S0".equals(ydMtlWGp)){
					   JDTORecordSet   rsStopover   = JDTORecordFactory.getInstance().createRecordSet("");
					   ydToLocGuide = ydDnWoLoc;
						
					   String fromColGp="";
					   String toColGp ="";
					    	    	
					   fromColGp ="TB" + ydToLocGuideFrom.substring(2);  //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리
					   toColGp =  "TB" + ydToLocGuideTo.substring(2); //1후판B동 한정이므로, szYD_DN_STK_COL_GP 값 못가져올것대비 "TB"로 상수화처리	   
					    	        
					   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 경유위치 결정대상 : From Bed :" + fromColGp + " To Bed : " + toColGp;
					   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					   
					   JDTORecord recParam = JDTORecordFactory.getInstance().create();
					   recParam.setField("FROM_COL_GP", fromColGp); 
					   recParam.setField("TO_COL_GP",   toColGp);
					   recParam.setField("MTL_L_GP",    ydMtlLGp);
					       			
					   int rtnVal = commDao.select(recParam, rsStopover, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectStopOverQuery");  
					   if (rtnVal <= 0){
						   logMsg = "["+operationName+"] 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정 실패!!] intRtnVal : " + rtnVal;
					      ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					      return YdConstant.RETN_NOT_EXIST_BED;
					   }
					  	
					   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정S 성공 intRtnVal : " + rtnVal;
					   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
					   
					   ydDnStkColGp = rsStopover.getRecord(0).getFieldString("YD_STK_COL_GP"); 
					   ydDnStkBedNo = rsStopover.getRecord(0).getFieldString("YD_STK_BED_NO");  
					   ydDnWoLayer   = PlateGdsYdUtil.trim(rsStopover.getRecord(0).getFieldString("YD_STK_LYR_NO"));
					   ydDnWoLoc     = ydDnStkColGp + ydDnStkBedNo;				   
					   logMsg = "["+operationName+"] 크레인 스케줄[" + ydCrnSchId + "] : 주작업[TO위치결정방법- 경유위치 결정E 성공 : " + ydDnStkColGp + " szCflag : " + szCflag;
					   ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
				    }
			    }
		   }
		}
		
		
		///////////////////////////////////
		
		JDTORecord recUpdCrnSch = JDTORecordFactory.getInstance().create();
		recUpdCrnSch.setResultCode(logId);
		
		recUpdCrnSch.setField("YD_CRN_SCH_ID"			, ydCrnSchId);
		recUpdCrnSch.setField("YD_EQP_ID"				, ydEqpId);
		recUpdCrnSch.setField("YD_TO_LOC_GUIDE"			, ydToLocGuide);
		recUpdCrnSch.setField("YD_UP_WO_LOC"			, ydUpWoLoc);
		recUpdCrnSch.setField("YD_UP_WO_LAYER"			, ydUpWoLayer);
		recUpdCrnSch.setField("YD_DN_WO_LOC"			, ydDnWoLoc);
		recUpdCrnSch.setField("YD_DN_WO_LAYER"			, ydDnWoLayer);
		recUpdCrnSch.setField("IS_UP_LOC_UPDATE_NEED"	, isUpLocUpdateNeed);
		recUpdCrnSch.setField("MAX_MTL_L"	, ydEqpWrkMaxL);
		return updCrnSchDnLoc(recUpdCrnSch, rsCrnwrkmtl);
		

	} // end of procMainWrkToLocForPlateYdForChgCrn  --크레인변경(고장)에 따른 TO위치 재탐색 로직 신규	

	
	/**
	 * 입고존 도착시 사전 TO위치결정(후판제품)
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String procPreMainWrkToLocForPlateYd(
			  String sStlNo					/* 입고존 도착재료 */
			, String sRtGp 					/* 입고존 RT */
			, String sBayGp					/* 입고동(동간입고 및 이적) */
			) throws JDTOException {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
// procPreMainWrkToLocForPlateYd 사용하는곳이 여러곳이라 argument 에 logId 항목 추가 하지 않고
// 기존  procPreMainWrkToLocForPlateYd(String, String, String) 에서	
// logId 새로 발본 하여 기존 로직은 그대로 이고 logId argument 만 추가 하여 		
// 신규  procPreMainWrkToLocForPlateYd(String, String, String, String) 작성		
// 기존 putLog -> putLogNew logId 출력 되게 개선
String szMethodName				= "procPreMainWrkToLocForPlateYd";
String szLogMsg					= null;
String logId				    = ydUtils.getLogIdNew("T");	// 후판 제품 log id 새로 발번

//szLogMsg = "입고존 도착시 사전 TO위치결정(후판제품)(YdToLocDcsnUtil." + szMethodName + ") 시작";
//ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);

		try {

			return YdToLocDcsnUtil.procPreMainWrkToLocForPlateYd(sStlNo, sRtGp, sBayGp, logId);

		} catch (Exception e) {

			szLogMsg = "입고존 도착시 사전 TO위치결정(후판제품) ERROR : " + e.getMessage();
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
	
            throw new DAOException(szClassName + e.getMessage(), e);

		}   // end try catch

//2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
    } //end of procPreMainWrkToLocForPlateYd()
	
	/**
	 * 입고존 도착시 사전 TO위치결정(후판제품)
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String procPreMainWrkToLocForPlateYd(
			  String sStlNo					/* 입고존 도착재료 */
			, String sRtGp 					/* 입고존 RT */
			, String sBayGp					/* 입고동(동간입고 및 이적) */
			, String logId					/* 로그일련번호 */
			) throws JDTOException {
		/*
		 * 업무기준	:	1. 
		 * 
		 * 수정자	: 윤재광.
		 * 수정일	:
		 * 				1. 2016.08.17 - 최초등록
		 */
		String szMethodName				= "procPreMainWrkToLocForPlateYd";
		String szOperationName			= "입고존 도착시 사전 TO위치결정(후판제품)";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		ArrayList		listToLoc		= null;
		YdStkLocVO		ydStkLocVO		= null;
		
		JDTORecordSet	rsResult		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		String szYD_DN_WO_LOC			= null;
		String szYD_DN_STK_COL_GP		= null;
		String szYD_DN_STK_BED_NO		= null;
		String szYD_GP					= "T";
		String szYD_BAY_GP				= null;
		String szYD_MTL_W_GP			= null;						//야드재료폭구분
		String szYD_MTL_L_GP			= null;						//야드재료길이구분
		String szYD_PILING_CD			= null;						//야드Piling코드
		String szSEARCH_CHANGE          = "N";                      //대형고객사 입고 시 검색순서 변경 : P-C,혼적,공BED -> P-C,공BED,혼적
		String szCHECK_FLAG         	= "N";
		String szYD_RCPT_PLN_STR_LOC 	= "";
		String sRTN_BOOKOUT_LOC 		= "";
		
		YdEqpDao	ydEqpDao 			= new YdEqpDao();
		YdStockDao	ydStockDao			= new YdStockDao();
		JDTORecordSet outResult1 		= null;		
		JDTORecord inRecord3 			= null;		
		JDTORecord inRecord2 			= null;	
		JDTORecord outRec1 			= null;	
		
		String szFROM_DONG				= "";		
		String szTO_DONG  				= "";		
		
		String szYD_EQP_WRK_SH 			= "";			//크레인작업재료 총매수
		String szYD_EQP_WRK_WT 			= "";			//크레인작업재료 총중량
		String szYD_EQP_WRK_T 			= "";			//크레인작업재료 총높이
		
		int intRtnVal               	= 0; 

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
// procPreMainWrkToLocForPlateYd 사용하는곳이 여러곳이라 argument 에 logId 항목 추가 하지 않고
// 로직은 이전 그대로인 신규 procPreMainWrkToLocForPlateYd(String, String, String, String) 작성		
// 기존 putLog -> putLogNew logId 출력 되게 개선
if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "입고존 도착시 사전 TO위치결정(후판제품)(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);

// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
		
		YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
		YdPlateCommDAO commDao = new YdPlateCommDAO();	
		PtOsCommDao ptOsCommDao = new PtOsCommDao();
		YDDataUtil yddatautil 	= new YDDataUtil();
		
		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "016");//후판 개발 적용여부
		
		if("Y".equals(sApplyYnPI)){
			szLogMsg = "입고존 도착시 사전 TO위치결정(후판제품) (" + szMethodName + ") 신규모듈 호출";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			
			return procPreMainWrkToLocForPlateYd2nd(sStlNo,  sRtGp, sBayGp	, logId	);
		}
		//----------------------------------------------------------------------------------------------------------------------
		//	입고존 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		
		recPara.setField("STL_NO", sStlNo);
		
		szLogMsg = "["+ szOperationName +"] 입고존 재료정보["+sStlNo+"]를 저장품에서 조회 시작 ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szRtnMsg = DaoManager.getYdStock(recPara, rsResult, 0);
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			return szRtnMsg;
		}
		
		rsResult.first();
		recTemp = rsResult.getRecord();
		
		szYD_MTL_L_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");			//크레인작업 최하단재료의 길이구분
		szYD_MTL_W_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_W_GP");			//크레인작업 최하단재료의 폭구분
		szYD_PILING_CD 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_PILING_CD");			//크레인작업 최하단재료의 Piling코드
		
		szYD_EQP_WRK_SH = "1";														//크레인작업재료 총매수
		szYD_EQP_WRK_WT = ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_WT");			//크레인작업재료 총중량
		szYD_EQP_WRK_T 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_T");			//크레인작업재료 총높이
		
	    
		szYD_RCPT_PLN_STR_LOC 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_RCPT_PLN_STR_LOC");//입고예정위치
		
		if("".equals(szYD_PILING_CD)){
			szLogMsg = "["+ szOperationName +"] 파일링코드 정보가  존재하지 않습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_BIG_NOT_EXIST_BED;
		}
		
		if(szYD_PILING_CD.length() != 8){
			szLogMsg = "["+ szOperationName +"] 파일링코드 정보 자릿수가  8자리가 아닙니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_BIG_NOT_EXIST_BED;
		}
		
		if(szYD_RCPT_PLN_STR_LOC.length() < 2){//기존로직상으로는 예정위치동정보 없으면 FAIL, 신규로직상으로는 기존 입고예정위치동 무시하고 다시 계산하도록 변경
		
			szLogMsg = "["+ szOperationName +"] 입고예정위치 동 정보가  존재하지 않습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_BIG_NOT_EXIST_BED;
		} 
		
		if("".equals(szYD_MTL_W_GP)){
			szYD_MTL_W_GP =  szYD_PILING_CD.substring(4, 6);
		}
		
		if("".equals(szYD_MTL_L_GP)){
			szYD_MTL_L_GP =  szYD_PILING_CD.substring(6, 8);
		}
		
		szYD_BAY_GP				= szYD_RCPT_PLN_STR_LOC.substring(1, 2);
		
		// 동간입고 및 이적시에 작업예약의 To위치 동 정보를 활용한다.
		if(!"".equals(sBayGp)){
			szYD_BAY_GP = sBayGp;
		}
		
		//여기에 입고동 다시 탐색하는 로직 추가  REQ202407592586 저장계획 변경시, 입고예정위치 재탐색(후판동별저장계획) 임진후 기사 요청건
		
		sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APPPI6", "T", "*");//APPPI4:후판 개발 적용여부
		
		if(sApplyYnPI.equals("Y")){
			szLogMsg = "["+ szOperationName +"] 입고대상동 재탐색 로직 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);		
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);		
		
			String szORD_NO			= ydDaoUtils.paraRecChkNull(recTemp, "ORD_NO");
			String szORD_DTL		= ydDaoUtils.paraRecChkNull(recTemp, "ORD_DTL");


			//24.08.20 임진후기사 요청. 1/2후판 분리를 기존 조업공장구분이 아닌 R/T 기준으로 적용 필요
			//String szPTOP_PLNT_GP   = ydDaoUtils.paraRecChkNull(recTemp, "PTOP_PLNT_GP");
			String szPTOP_PLNT_GP     = "PA";
					
			if("A".equals(sRtGp)|| 	
				    "B".equals(sRtGp)||
				    "C".equals(sRtGp)){
				szPTOP_PLNT_GP = "PB";
			}
			
			szLogMsg = "["+ szOperationName +"] 해당주문 :"+ szORD_NO + "-" +szORD_DTL + "저장계획코드 READ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);		
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);		
			
			//recPara 		= JDTORecordFactory.getInstance().create();
			recPara.setField("ORD_NO",    	szORD_NO);			
			recPara.setField("ORD_DTL",    	szORD_DTL);			
			
			outResult1= JDTORecordFactory.getInstance().createRecordSet("");
			//저장계획 코드 Read
			/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommLocPlanCd_PIDEV*/
			intRtnVal = ptOsCommDao.getPtOsComm(recPara, outResult1, 300);
			
			outResult1.absolute(1);
			outRec1 = JDTORecordFactory.getInstance().create();
			outRec1 = outResult1.getRecord();

	
			String szLOC_PLAN_CD		= ydDaoUtils.paraRecChkNull(outRec1,"LOC_PLAN_CD");
			String szMAIN_TRANS_AREA 	= ydDaoUtils.paraRecChkNull(outRec1,"MAIN_TRANS_AREA");
			
			outResult1= JDTORecordFactory.getInstance().createRecordSet("");
			recPara.setField("YD_PILING_CD",   	szYD_PILING_CD);			
			recPara.setField("LOC_PLAN_CD",    	szLOC_PLAN_CD);			
			recPara.setField("MAIN_TRANS_AREA", szMAIN_TRANS_AREA);
			/* 
			 * 2016.03.21 윤재광 
			 * - 1후판 저장계획 추가에 따른 적용을 위해 추가 PARAM  
			 */
			recPara.setField("PTOP_PLNT_GP",    szPTOP_PLNT_GP); 
			
			/*
			 * 2024.09.13 후판동별저장계획 화면 개선요청 임진후 기사 요청 --REQ202408611796
			 * 수출재 신규고객사 추가. 고객사별 개별셋팅을 하기때문에 szLOC_PLAN_CD 는 버리고 파일링코드 앞 4자리 사용
			 * 
			 * 2024.10.04 주문은 파일링 E453 이나, 주문전환? (사외창고 가거나..) 하는 경우 입고전 재료의 파일링코드가 주문외(M001) 로 빠지는 경우가 있음
			 * 이 경우 신규동별저장계획 적용시, 파라미터가 아래와같음. 
			 * :V_PTOP_PLNT_GP      PA	
			 * :V_MAIN_TRANS_AREA	E
		     * :V_YD_PILING_CD	    M001S0U0
		     * :V_LOC_PLAN_CD		M001
		     * 
		     * --> V_MAIN_TRANS_AREA 가 'E' 코드가 나오는게 문제. 24.10.04 임진후 기사 통화에서 이런 CASE는 주문외 적용해달라 답변
		     * V_MAIN_TRANS_AREA 값을 TB_YD_RULE 의 CD_GP 와 맵핑하지 말고, V_LOC_PLAN_CD 의 첫글자와 맵핑
			 * 
			 * */
			String szORD_GP = szORD_NO.substring(0,1);
			sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "002");
			if ("Y".equals(sApplyYnPI) && ( (szORD_GP.equals("E")) || (szORD_GP.equals("F")) ) ){
				szLogMsg = "신규 동별저장계획기준 해당주문 :"+ szORD_NO + "-" +szORD_DTL +"권역구분["+szLOC_PLAN_CD+ "] 대신 ["+szYD_PILING_CD.substring(0,4)+"]사용";
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				szLOC_PLAN_CD = szYD_PILING_CD.substring(0,4);
				recPara.setField("LOC_PLAN_CD",    	szLOC_PLAN_CD);
			}
			
			intRtnVal = commDao.select(recPara, outResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0059");
			
			outResult1.absolute(1);
			outRec1 = JDTORecordFactory.getInstance().create();
			outRec1 = outResult1.getRecord();
	
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(outRec1,"DONG");
			
			szLogMsg = "["+ szOperationName +"] 입고대상동 재탐색 로직 종료. 재탐색 입고동 : ["+szYD_BAY_GP+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		}
	
			
		szLogMsg = "["+ szOperationName +"] 입고존 재료정보["+sStlNo+"]를 저장품에서 조회 완료 - 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"], Piling코드["+szYD_PILING_CD+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);

		listToLoc = new ArrayList();
		
		boolean bIS_BED_STACKABLE	= false;
		//----------------------------------------------------------------------------------------------------------------------
		//	1후판  적치가능한 가적베드 검색조회 시작
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ sRtGp +"] 입고RT / ["+szYD_BAY_GP+"]입고동 ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		/*
		if(("D".equals(sRtGp)|| 	//입고RT(1후판 전 라인라인으로 수정 2020.11.04 윤재광)
		    "E".equals(sRtGp)||
		    "F".equals(sRtGp))&&
		    !szYD_MTL_W_GP.startsWith("L")){ 			// 광폭은 제외	
		*/	
		if("D".equals(sRtGp)|| 	//입고RT(1후판 전 라인라인으로 수정 2020.11.04 윤재광)
		    "E".equals(sRtGp)||
		    "F".equals(sRtGp)|| //광폭재도 포함되도록 변경(박종호. 2022.04.28 임진후 사원 요청사항.)
		    "G".equals(sRtGp)){  
		
			szLogMsg = "["+ szOperationName +"] 1후판 온라인 E동 적치가능한 가적베드 조회 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
//			2021.08. 06 전사물류개선
//			요청사항
//             - F동 중척제품 입고시 크레인 이동거리 증가로 인한 사이클 타임 증가 (5분이상/매 소요)
//             - F동 D-R/T 중척제품의 경우 D동으로 입고되도록 시스템 개선 입고 사이클 타임 축소 (3분이하/매)
			if("D".equals(sRtGp) && "F".equals(szYD_BAY_GP) && szYD_MTL_W_GP.startsWith("M") ){
				if( PlateGdsYdUtil.isApplyYn("1후판F동중척재D동이적적용여부") ){
					szLogMsg = "["+ szOperationName +"] 1후판 "+szYD_BAY_GP+"동 D-RT 중척재("+szYD_MTL_W_GP+") D동으로 강제셋팅";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					szYD_BAY_GP = "D";
				}
			}
			
//			2022.08. 23  레일공사로 인한 임시 로직 변경
//			요청사항  D동으로 오는 제품들을 F, C동으로 이전
//			제품 SIZE MM : F동 , 
//			제품 SIZE LM : C동, SM도 C동	
/*=============================================================================================================================================*/
/*			
			szLogMsg = "["+ szOperationName +"] [임시로직]확인1 YD_BAY_GP : "+ szYD_BAY_GP + " szYD_MTL_W_GP : "+ szYD_MTL_W_GP.substring(0, 1)+ " szYD_MTL_L_GP : "+szYD_MTL_L_GP.substring(0, 1);
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			if ("D".equals(szYD_BAY_GP) && sStlNo.startsWith("FC")) 
			{
				if ("M".equals(szYD_MTL_W_GP.substring(0, 1))&&"M".equals(szYD_MTL_L_GP.substring(0, 1)))
				{
					szYD_BAY_GP = "F";
				}
				else if ("L".equals(szYD_MTL_W_GP.substring(0, 1))&&"M".equals(szYD_MTL_L_GP.substring(0, 1)))
				{
					szYD_BAY_GP = "C";
				}
				else if ("S".equals(szYD_MTL_W_GP.substring(0, 1))&&"M".equals(szYD_MTL_L_GP.substring(0, 1)))
				{
					szYD_BAY_GP = "C";
				}
			}
			szLogMsg = "["+ szOperationName +"] [임시로직]확인2 YD_BAY_GP : "+ szYD_BAY_GP;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
*/			
/*=============================================================================================================================================*/			
			
			outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			inRecord3 	= JDTORecordFactory.getInstance().create();
			inRecord2 	= JDTORecordFactory.getInstance().create();

			inRecord3.setField("REPR_CD_GP" ,"T00261");	
			inRecord3.setField("CD_GP" ,sRtGp);	// 입고R/T
			inRecord3.setField("ITEM"  ,szYD_BAY_GP);   				// 입고동
			
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
			intRtnVal = ydEqpDao.getYdEqp(inRecord3, outResult1, 999);
			if(intRtnVal > 0) {
				outResult1.first();
				inRecord2  = outResult1.getRecord();
				szFROM_DONG = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, "ITEM1"),"0101")+"01";
				szTO_DONG   = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, "ITEM2"),"0799")+"99";			
			} else {
				szFROM_DONG = "0101";
				szTO_DONG   = "0799"; 
			}
			
			JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
			
			recPara1.setField("YD_GP"			, szYD_GP);
			recPara1.setField("YD_BAY_GP"		, szYD_BAY_GP);
			recPara1.setField("YD_PILING_CD"	, szYD_PILING_CD);
			recPara1.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
			recPara1.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);
			recPara1.setField("FR_YD_STK_BED_NO", "01");
			
			recPara1.setField("YD_EQP_WRK_SH"	, szYD_EQP_WRK_SH);
			recPara1.setField("YD_EQP_WRK_WT"	, szYD_EQP_WRK_WT);
			recPara1.setField("YD_EQP_WRK_T"	, szYD_EQP_WRK_T);
			
			//----------------------------------------------------------------------------------------------------------------------
			//	조건에 해당하는 단 정보 조회 - 1후판 적치가능한 가적베드 정보 조회
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecordSet rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
			
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithTempBedRatioReNew*/
			szRtnMsg = DaoManager.getYdStklyr(recPara1, rsResult1, 631);
			//----------------------------------------------------------------------------------------------------------------------
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {

				srchNconvRecord2VoTmpBed("","",recPara1, rsResult1, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD);
			}
			szLogMsg = "["+ szOperationName +"] 1후판 온라인 E동 적치가능한 가적베드 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
			// Sorting ?
			//----------------------------------------------------------------------------------------------------------------------
			if( listToLoc.size() > 0 ) {
				Collections.sort(listToLoc, new YdStkLocComparator());
			}
			//----------------------------------------------------------------------------------------------------------------------
			 
			//----------------------------------------------------------------------------------------------------------------------
			//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
			//----------------------------------------------------------------------------------------------------------------------
						
			for(int i = 0; i < listToLoc.size(); i++ ) {
				
				ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
				
				szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 1후판 온라인 E동 적치가능한 가적베드 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				
	    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
	    			
					bIS_BED_STACKABLE		= true;
					
					szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
		    		szLogMsg += "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 1후판 온라인 E동 적치가능한 가적베드 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++\n";
		    		szLogMsg += "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		    		
					break;
				}
			}
		}
		
////		2022.08. 23  레일공사로 인한 임시 로직 변경
////		요청사항  D동으로 오는 제품들을 F, C동으로 이전
////		제품 SIZE MM : F동 , 
////		제품 SIZE LM : C동, SM도 C동	
///*=============================================================================================================================================*/
//		szLogMsg = "["+ szOperationName +"] [확인3] YD_BAY_GP : "+ szYD_BAY_GP + " szYD_MTL_W_GP : "+ szYD_MTL_W_GP.substring(0, 1)+ " szYD_MTL_L_GP : "+szYD_MTL_L_GP.substring(0, 1);
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
//		if ("D".equals(szYD_BAY_GP) && sStlNo.startsWith("FC"))
//		{
//			if ("M".equals(szYD_MTL_W_GP.substring(0, 1))&&"M".equals(szYD_MTL_L_GP.substring(0, 1)))
//			{
//				szYD_BAY_GP = "F";
//			}
//			else if ("L".equals(szYD_MTL_W_GP.substring(0, 1))&&"M".equals(szYD_MTL_L_GP.substring(0, 1)))
//			{
//				szYD_BAY_GP = "C";
//			}
//			else if ("S".equals(szYD_MTL_W_GP.substring(0, 1))&&"M".equals(szYD_MTL_L_GP.substring(0, 1)))
//			{
//				szYD_BAY_GP = "C";
//			}
//		}
//		szLogMsg = "["+ szOperationName +"] [확인4] YD_BAY_GP : "+ szYD_BAY_GP;
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
///*=============================================================================================================================================*/
		//----------------------------------------------------------------------------------------------------------------------
		//	1후판 적치가능한 가적베드 검색조회 완료
		//----------------------------------------------------------------------------------------------------------------------
		
		if( !bIS_BED_STACKABLE) {
			
			//----------------------------------------------------------------------------------------------------------------------
			//	동일한 Piling Code의 적치가능한 베드 조회
			//----------------------------------------------------------------------------------------------------------------------
			szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]의 적치가능한 베드 조회 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			{
				outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				inRecord3 	= JDTORecordFactory.getInstance().create();
				inRecord2 	= JDTORecordFactory.getInstance().create();

				inRecord3.setField("REPR_CD_GP" ,"T00011");	
				inRecord3.setField("CD_GP" ,sRtGp);   		// 입고R/T
				inRecord3.setField("ITEM"  ,szYD_BAY_GP);   // 입고동
				
				/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
				intRtnVal = ydEqpDao.getYdEqp(inRecord3, outResult1, 999);
				if(intRtnVal > 0) {
					outResult1.first();
					inRecord2  = outResult1.getRecord();
					szFROM_DONG = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, "ITEM1"),"0101");
					szTO_DONG   = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, "ITEM2"),"0799");		
				} else {
					szFROM_DONG = "0101";
					szTO_DONG   = "0799"; 
				}
				
				JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
				
				recPara1.setField("YD_GP"			, szYD_GP);
				recPara1.setField("YD_BAY_GP"		, szYD_BAY_GP);
				recPara1.setField("YD_PILING_CD"	, szYD_PILING_CD);
				recPara1.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
				recPara1.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);
				recPara1.setField("FR_YD_STK_BED_NO", "01");
				
				recPara1.setField("YD_EQP_WRK_SH"	, szYD_EQP_WRK_SH);
				recPara1.setField("YD_EQP_WRK_WT"	, szYD_EQP_WRK_WT);
				recPara1.setField("YD_EQP_WRK_T"	, szYD_EQP_WRK_T);
				
				//----------------------------------------------------------------------------------------------------------------------
				//	조건에 해당하는 단 정보 조회 - 동일한 Piling Code를 가진 최상단 재료가 있는 모든 베드 정보 조회
				//----------------------------------------------------------------------------------------------------------------------
				JDTORecordSet rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
				
				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatioReNew*/
				szRtnMsg = DaoManager.getYdStklyr(recPara1, rsResult1, 624);
				//----------------------------------------------------------------------------------------------------------------------
				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					srchNconvRecord2Vo("","",recPara1, rsResult1, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD,logId);
				}
			}
			szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]의 적치가능한 베드 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			//----------------------------------------------------------------------------------------------------------------------
		
			//----------------------------------------------------------------------------------------------------------------------
			// Sorting ?
			//----------------------------------------------------------------------------------------------------------------------
			if( listToLoc.size() > 0 ) {
				Collections.sort(listToLoc, new YdStkLocComparator());
			}
			//----------------------------------------------------------------------------------------------------------------------
			 
			//----------------------------------------------------------------------------------------------------------------------
			//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
			//----------------------------------------------------------------------------------------------------------------------
			
			for(int i = 0; i < listToLoc.size(); i++ ) {
				
				ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
				
				szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				
	    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
	    			
					bIS_BED_STACKABLE		= true;
					
					szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
		    		szLogMsg += "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++\n";
		    		szLogMsg += "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		    		
					break;
				}
			}
		}
    	
		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 Piling Code의 베드가 존재하지 않을 경우에는 
		//	혼적 bed 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		if( !bIS_BED_STACKABLE) {
			
			JDTORecordSet outResult99 = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord 	  inRecord99  = JDTORecordFactory.getInstance().create();
			JDTORecord 	  outRecord99 = JDTORecordFactory.getInstance().create();
			JDTORecord 	  recTemp1 	  = JDTORecordFactory.getInstance().create();

			String szAPPLY_YN1 		  = "N";
			
			inRecord99.setField("REPR_CD_GP", "T00070");    //저장그룹편성보완
			
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
			intRtnVal = ydEqpDao.getYdEqp(inRecord99, outResult99, 999);
			if(intRtnVal > 0) {
				outResult99.first();
				outRecord99 = outResult99.getRecord();
				szAPPLY_YN1 = outRecord99.getFieldString("ITEM1");				
			}
			
			szLogMsg="신저장그룹편성보완 적용 " + szAPPLY_YN1 ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG);			
			ydUtils.putLogNew(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);			
			
			if(szAPPLY_YN1.equals("Y")) {
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdOrdWgtCheck*/
				intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 606);
				if( intRtnVal < 1 ) {
					return szRtnMsg;
				}
				
				rsResult.first();
				recTemp1 		= rsResult.getRecord();
				szCHECK_FLAG 	= ydDaoUtils.paraRecChkNull(recTemp1, "CHECK_FLAG");		        //주문량이 합(수량x두께) > 121	
				
				szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+sStlNo+"]를 저장품에서 조회 완료 - 주문량이 합["+szCHECK_FLAG+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				
				if(szCHECK_FLAG.equals("Y")) {
					szSEARCH_CHANGE = "Y";
				} else {
					szSEARCH_CHANGE = "N";
				}
			}
			
			if(szSEARCH_CHANGE.equals("Y")) {     //입고 대형 고객사 
			
			} else {	
				
				ydStkLocVO			= null;
				listToLoc 			= new ArrayList();
				
				szLogMsg = "["+ szOperationName +"] 혼적베드 조회 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);

				//szRtnMsg = getBedWithSimilarGpNew(sStlNo, szFROM_DONG, szTO_DONG, "", recTemp, listToLoc); 
				{
					
					//----------------------------------------------------------------------------------------------------------------------
					//	혼적베드 정보 조회
					//----------------------------------------------------------------------------------------------------------------------
					szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 혼적베드 정보 조회 시작 ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					
					JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
					recPara2.setField("YD_GP"			, szYD_GP);
					recPara2.setField("YD_BAY_GP"		, szYD_BAY_GP);
					recPara2.setField("YD_STK_BED_L_GP"	, szYD_MTL_L_GP.substring(0, 1));
					recPara2.setField("YD_STK_BED_W_GP"	, szYD_MTL_W_GP.substring(0, 1));
					recPara2.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
					recPara2.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);
					
					recPara2.setField("YD_EQP_WRK_SH"	, szYD_EQP_WRK_SH);
					recPara2.setField("YD_EQP_WRK_WT"	, szYD_EQP_WRK_WT);
					recPara2.setField("YD_EQP_WRK_T"	, szYD_EQP_WRK_T);
					
					//----------------------------------------------------------------------------------------------------------------------
					//	조건에 해당하는 베드 정보 조회 - 혼적베드 정보 조회
					//----------------------------------------------------------------------------------------------------------------------
					recPara2.setField("STL_NO",	sStlNo);
					
					JDTORecord recRecord2 	= JDTORecordFactory.getInstance().create();
					JDTORecordSet rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
					
					//PIDEV_S:병행가동용:PI_YD
					recPara2.setField("PI_YD",    	szYD_GP);						
					/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getOsPilingCdByStlNo_PIDEV*/
					szRtnMsg = DaoManager.getYdStock(recPara2, rsResult2, 213);
					
					rsResult2.first();
					recRecord2 = rsResult2.getRecord();
					
					String sGbn			= ydDaoUtils.paraRecChkNull(recRecord2, "GBN");
					String sYdMtlWGp	= ydDaoUtils.paraRecChkNull(recRecord2, "YD_MTL_W_GP");
					String sYdMtlLGp	= ydDaoUtils.paraRecChkNull(recRecord2, "YD_MTL_L_GP");
					String sYdPilingCd	= ydDaoUtils.paraRecChkNull(recRecord2, "YD_PILING_CD");
					String sDemanderCd	= ydDaoUtils.paraRecChkNull(recRecord2, "DEMANDER_CD");
					String sYdStkBedLGp	= ydDaoUtils.paraRecChkNull(recRecord2, "YD_STK_BED_L_GP");
					String sYdStkBedWGp	= ydDaoUtils.paraRecChkNull(recRecord2, "YD_STK_BED_W_GP");
					String sShipCd		= ydDaoUtils.paraRecChkNull(recRecord2, "SHIP_CD");
					String sDelTermCd	= ydDaoUtils.paraRecChkNull(recRecord2, "DELIVER_TERM_CD"); // 검색
					String sCustCd		= ydDaoUtils.paraRecChkNull(recRecord2, "CUST_CD");
					String sDetailArrCd	= ydDaoUtils.paraRecChkNull(recRecord2, "DETAIL_ARR_CD");		
					
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, "sGbn=>"+sGbn, 				YdConstant.DEBUG);
//					ydUtils.putLog(szClassName, szMethodName, "sYdMtlWGp=>"+sYdMtlWGp, 		YdConstant.DEBUG);
//					ydUtils.putLog(szClassName, szMethodName, "sYdMtlLGp=>"+sYdMtlLGp, 		YdConstant.DEBUG);
//					ydUtils.putLog(szClassName, szMethodName, "sYdPilingCd=>"+sYdPilingCd, 	YdConstant.DEBUG);
//					ydUtils.putLog(szClassName, szMethodName, "sDemanderCd=>"+sDemanderCd, 	YdConstant.DEBUG);
//					
//					ydUtils.putLog(szClassName, szMethodName, "sYdStkBedLGp=>"+sYdStkBedLGp,YdConstant.DEBUG);
//					ydUtils.putLog(szClassName, szMethodName, "sYdStkBedWGp=>"+sYdStkBedWGp,YdConstant.DEBUG);
//					ydUtils.putLog(szClassName, szMethodName, "sShipCd=>"+sShipCd, 			YdConstant.DEBUG);
//					ydUtils.putLog(szClassName, szMethodName, "sDelTermCd=>"+sDelTermCd, 	YdConstant.DEBUG);
//					ydUtils.putLog(szClassName, szMethodName, "sDetailArrCd=>"+sDetailArrCd,YdConstant.DEBUG);

					ydUtils.putLogNew(szClassName, szMethodName, "sGbn=>"+sGbn, 				YdConstant.DEBUG, logId);
					ydUtils.putLogNew(szClassName, szMethodName, "sYdMtlWGp=>"+sYdMtlWGp, 		YdConstant.DEBUG, logId);
					ydUtils.putLogNew(szClassName, szMethodName, "sYdMtlLGp=>"+sYdMtlLGp, 		YdConstant.DEBUG, logId);
					ydUtils.putLogNew(szClassName, szMethodName, "sYdPilingCd=>"+sYdPilingCd, 	YdConstant.DEBUG, logId);
					ydUtils.putLogNew(szClassName, szMethodName, "sDemanderCd=>"+sDemanderCd, 	YdConstant.DEBUG, logId);
					
					ydUtils.putLogNew(szClassName, szMethodName, "sYdStkBedLGp=>"+sYdStkBedLGp,YdConstant.DEBUG, logId);
					ydUtils.putLogNew(szClassName, szMethodName, "sYdStkBedWGp=>"+sYdStkBedWGp,YdConstant.DEBUG, logId);
					ydUtils.putLogNew(szClassName, szMethodName, "sShipCd=>"+sShipCd, 			YdConstant.DEBUG, logId);
					ydUtils.putLogNew(szClassName, szMethodName, "sDelTermCd=>"+sDelTermCd, 	YdConstant.DEBUG, logId);
					ydUtils.putLogNew(szClassName, szMethodName, "sDetailArrCd=>"+sDetailArrCd,YdConstant.DEBUG, logId);
					
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
					
					recPara2.setField("YD_MTL_W_GP",		sYdMtlWGp);
					recPara2.setField("YD_MTL_L_GP",		sYdMtlLGp);
					recPara2.setField("YD_PILING_CD",		sYdPilingCd);
					recPara2.setField("DEMANDER_CD",		sDemanderCd);
					recPara2.setField("YD_STK_BED_L_GP",	sYdStkBedLGp);
					recPara2.setField("YD_STK_BED_W_GP",	sYdStkBedWGp);
					recPara2.setField("SHIP_CD",			sShipCd);
					recPara2.setField("DELIVER_TERM_CD",	sDelTermCd);
					recPara2.setField("CUST_CD",			sCustCd);
					recPara2.setField("DETAIL_ARR_CD",		sDetailArrCd);
					
					int iLength		= 0;
					String[] arryS 	= null;
					
					if("2".equals(sGbn)){		// 해송

						iLength = 1;
						arryS 	= new String[iLength];
						arryS[0] 	= "3";
						
					}else if("3".equals(sGbn)){	// 주문외

						iLength = 2;
						arryS 	= new String[iLength];
						arryS[0] 	= "4";
						arryS[1] 	= "2";
					}else{						// 육송

						iLength = 3;
						arryS 		= new String[iLength];
						arryS[0] 	= "1";
						arryS[1] 	= "*"; 	//출하권역별 검색
						arryS[2] 	= "2";
					}
				
					for(int idx = 0; idx < iLength; idx++ ){
						
						recPara2.setField("SEARCH_GBN",arryS[idx]);
						
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						
						if("*".equals(arryS[idx])){
							/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithShipSimilarBedReNew_PIDEV*/
							//PIDEV_S :병행가동용:PI_YD
							recPara2.setField("PI_YD",    	szYD_GP);										
							szRtnMsg = DaoManager.getYdStklyr(recPara2, rsResult2, 626);
						}else{
							/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBedReNew*/
							szRtnMsg = DaoManager.getYdStklyr(recPara2, rsResult2, 625);
						}
						
						if(( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) && ( rsResult2.size() > 0 )) {

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// srchNconvRecord2Vo argument 에 logId 항목 추가 개선
//							srchNconvRecord2Vo("","",recPara2, rsResult2, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_EMPTY_BED);
							srchNconvRecord2Vo("","",recPara2, rsResult2, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_EMPTY_BED, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
							if( listToLoc.size() > 0 ) {
								break;
							}	
						}
					}
				}

				szLogMsg = "["+ szOperationName +"] 혼적베드 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				
				//----------------------------------------------------------------------------------------------------------------------
				// Sorting
				//----------------------------------------------------------------------------------------------------------------------
				if( listToLoc.size() > 0 ) {
					Collections.sort(listToLoc, new YdStkLocComparator());
				}
				//----------------------------------------------------------------------------------------------------------------------
				
				//----------------------------------------------------------------------------------------------------------------------
				//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
				//----------------------------------------------------------------------------------------------------------------------
				
				for(int i = 0; i < listToLoc.size(); i++ ) {
					
					ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
					
					szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 혼적베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					
		    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
						
						bIS_BED_STACKABLE		= true;
						
						szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
						szLogMsg = "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 혼적베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    	    		szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    	    		
						break;
					}
				}
				//----------------------------------------------------------------------------------------------------------------------
			}	
		}
		
		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 Piling Code의 베드가 존재하지 않을 경우에는 
		//	길이구분/폭구분이 동일한 공베드를 조회
		//----------------------------------------------------------------------------------------------------------------------
		if( !bIS_BED_STACKABLE ) {
			
			ydStkLocVO			= null;
			listToLoc 			= new ArrayList();
			
			szLogMsg = "["+ szOperationName +"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 적치가능한 공베드 조회 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			//szRtnMsg = getEmptyBedWithSameLWGpNew(szFROM_DONG, szTO_DONG, "", recTemp, listToLoc, "");//szYD_CRN_SCH_ID);
			{
				//----------------------------------------------------------------------------------------------------------------------
				//	동일한 길이구분/폭구분을  가진 모든 공베드 정보 조회
				//----------------------------------------------------------------------------------------------------------------------
				szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 동일한 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"]을 가진 모든 공베드 정보 조회 시작 ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);

				JDTORecord recPara3 = JDTORecordFactory.getInstance().create();
				recPara3.setField("YD_GP"			, szYD_GP);
				recPara3.setField("YD_BAY_GP"		, szYD_BAY_GP);
				recPara3.setField("YD_STK_BED_L_GP"	, szYD_MTL_L_GP.substring(0, 1));
				recPara3.setField("YD_STK_BED_W_GP"	, szYD_MTL_W_GP.substring(0, 1));
				recPara3.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
				recPara3.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);
				recPara3.setField("RT_GP"			, sRtGp);
				recPara3.setField("YD_PILING_CD"	, szYD_PILING_CD);
				
				recPara3.setField("YD_EQP_WRK_SH"	, szYD_EQP_WRK_SH);
				recPara3.setField("YD_EQP_WRK_WT"	, szYD_EQP_WRK_WT);
				recPara3.setField("YD_EQP_WRK_T"	, szYD_EQP_WRK_T);
				
				JDTORecordSet rsResult3 = JDTORecordFactory.getInstance().createRecordSet("");
				
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColNew*/
				szRtnMsg = DaoManager.getYdStkbed(recPara3, rsResult3, 320);

				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// srchNconvRecord2Vo argument 에 logId 항목 추가 개선
//					srchNconvRecord2Vo("","",recPara3, rsResult3, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_EMPTY_BED);
					srchNconvRecord2Vo("","",recPara3, rsResult3, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_EMPTY_BED, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
				}
			}
			szLogMsg = "["+ szOperationName +"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 적치가능한 공베드 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
			// Sorting
			//----------------------------------------------------------------------------------------------------------------------
			if( listToLoc.size() > 0 ) {
				Collections.sort(listToLoc, new YdStkLocComparator());
			}
			//----------------------------------------------------------------------------------------------------------------------
			
			//----------------------------------------------------------------------------------------------------------------------
			//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
			//----------------------------------------------------------------------------------------------------------------------
			
			for(int i = 0; i < listToLoc.size(); i++ ) {
				
				ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
				
				szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				
	    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
					
					bIS_BED_STACKABLE		= true;
					
					szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					szLogMsg = "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    	    		szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    	    		
					break;
				}
			}
		}
	 	
		if( !bIS_BED_STACKABLE ) {
			if(szSEARCH_CHANGE.equals("Y")) {

				szLogMsg = "["+ szOperationName +"] 권하위치검색 실패 - 대형고객사 적치가능한 베드가 존재하지 않습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				return YdConstant.RETN_BIG_NOT_EXIST_BED;
				
			} else {
				szLogMsg = "["+ szOperationName +"] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				return YdConstant.RETN_NOT_EXIST_BED;
			}	
		}
		


		/*
		 * 입고예정위치  최종결정정보 셋팅.
		 */
		recTemp 	= JDTORecordFactory.getInstance().create();
		
		szYD_DN_STK_COL_GP 	= ydStkLocVO.getYdStkColGp();
		szYD_DN_STK_BED_NO 	= ydStkLocVO.getYdStkBedNo();

		szYD_DN_WO_LOC		= szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;
		
		/*
		 * 2014.10.15 윤재광 - 이명운대리 요청
		 * G동 중척재이하는 무조건 2베드로 셋팅
		 */
		if("G".equals(szYD_BAY_GP) && ("M".equals(szYD_PILING_CD.substring(6,7))||
				                       "S".equals(szYD_PILING_CD.substring(6,7))||
				                       "U".equals(szYD_PILING_CD.substring(6,7)))){
			recTemp.setField("YD_STK_BED_NO",   "02");
		}else{
			recTemp.setField("YD_STK_BED_NO",   szYD_DN_STK_BED_NO);
		}
		
		//업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준) 야드구분값 셋팅
		if("A".equals(sRtGp)||"B".equals(sRtGp)||"C".equals(sRtGp)){
			recTemp.setField("YD_GP", 	"T");
		}else{
			recTemp.setField("YD_GP", 	"K");
		}
		recTemp.setField("YD_BAY_GP"		, szYD_BAY_GP);
		
		//업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준)
    	if( GetBreRule6.getYDB674(recTemp) ) {
    		sRTN_BOOKOUT_LOC = StringHelper.evl(recTemp.getFieldString("YDB674_RV01_YD_BOOK_OUT_LOC"), "00000"); // 업무기준 YDB674 반환값#1 YD_BOOK_OUT_LOC
    	} else {
    		sRTN_BOOKOUT_LOC ="";
    	}							
		//-------------------------------------------------------
		
		//-------------------------------------------------------
    	recTemp 	= JDTORecordFactory.getInstance().create();
    	
    	recTemp.setField("STL_NO"				, sStlNo);
    	recTemp.setField("YD_RCPT_PLN_STR_LOC"	, szYD_DN_WO_LOC);
    	recTemp.setField("YD_BOOK_OUT_LOC"		, sRTN_BOOKOUT_LOC);
    	recTemp.setField("MODIFIER"				, "PRELOCREG");
		
    	/*
    	 * com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0057         
			UPDATE TB_YD_STOCK
			   SET YD_BOOK_OUT_LOC      = :V_YD_BOOK_OUT_LOC
			      ,YD_RCPT_PLN_STR_LOC  = :V_YD_RCPT_PLN_STR_LOC
			 WHERE STL_NO = :V_STL_NO      
    	 */
		intRtnVal = commDao.update(recTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0057");
		
		/*
		 * com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0058        
			UPDATE TB_PT_PLATECOMM
			   SET YD_BOOK_OUT_LOC = :V_YD_BOOK_OUT_LOC
			 WHERE PLATE_NO = :V_STL_NO      
		 */
		intRtnVal = commDao.update(recTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0058");
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 END

szLogMsg = "입고존 도착시 사전 TO위치결정(후판제품)(" + szMethodName + ") 완료";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);

// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
    } //end of procPreMainWrkToLocForPlateYd()
	
	/**
	 * 입고존 도착시 사전 TO위치결정(후판제품)25.05.27 renew 버전. by hjw 
	 * 후판제품 임진후 기사 요청대로 to위치 결정 방법 개선 및 to위치 결정 순서 조정기능 추가.
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String procPreMainWrkToLocForPlateYd2nd(
			  String sStlNo					/* 입고존 도착재료 */
			, String sRtGp 					/* 입고존 RT */
			, String sBayGp					/* 입고동(동간입고 및 이적) */
			, String logId					/* 로그일련번호 */
			) throws JDTOException {
		/*
		 * 업무기준	:	1. 
		 * 
		 * 수정자	: 허정욱.
		 * 수정일	:
		 * 				1. 2025.06.11- 최초등록
		 */
		String methodName				 = "procPreMainWrkToLocForPlateYd2nd";
		String operationName			 = "입고존 도착시 사전 TO위치결정(후판제품)2nd";
		String logMsg					 = null;
		String rtnMsg 				     = YdConstant.RETN_CD_SUCCESS;
		String[] ydStkLyrMtlStat         = {YdConstant.YD_STK_LYR_MTL_STAT_DN_WAIT, YdConstant.YD_STK_LYR_MTL_STAT_UN_WAIT};
		
		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		logMsg = operationName+"(" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
		YdPlateCommDAO commDao = new YdPlateCommDAO();	
		PtOsCommDao ptOsCommDao = new PtOsCommDao();
		YDDataUtil yddatautil 	= new YDDataUtil();
		
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	입고존 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		JDTORecordSet rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recPara 	= JDTORecordFactory.getInstance().create();
		
		recPara.setField("STL_NO", sStlNo);
		
		logMsg = "["+ operationName +"] 입고존 재료정보["+sStlNo+"]를 저장품에서 조회 시작 ";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		rtnMsg = DaoManager.getYdStock(recPara, rsResult, 0);
		if( !rtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			return rtnMsg;
		}
		
		rsResult.first();
		JDTORecord recTemp = rsResult.getRecord();
		
		String ydMtlLGp 	   = ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");			//크레인작업 최하단재료의 길이구분
		String ydMtlWGp 	   = ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_W_GP");			//크레인작업 최하단재료의 폭구분
		String ydPilingCd 	   = ydDaoUtils.paraRecChkNull(recTemp, "YD_PILING_CD");			//크레인작업 최하단재료의 Piling코드
		
		String ydEqpWrkSh = "1";														//크레인작업재료 총매수
		String ydEqpWrkWt = ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_WT");			//크레인작업재료 총중량
		String ydEqpWrkT 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_T");			//크레인작업재료 총높이
		
		String ydRcptPlnStrLoc = ydDaoUtils.paraRecChkNull(recTemp, "YD_RCPT_PLN_STR_LOC");//입고예정위치
		
		if("".equals(ydPilingCd)){
			logMsg = "["+ operationName +"] 파일링코드 정보가  존재하지 않습니다.";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_BIG_NOT_EXIST_BED;
		}
		
		if(ydPilingCd.length() != 8){
			logMsg = "["+ operationName +"] 파일링코드 정보 자릿수가  8자리가 아닙니다.";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_BIG_NOT_EXIST_BED;
		}
		
		if(ydRcptPlnStrLoc.length() < 2){//기존로직상으로는 예정위치동정보 없으면 FAIL, 신규로직상으로는 기존 입고예정위치동 무시하고 다시 계산하도록 변경
		
			logMsg = "["+ operationName +"] 입고예정위치 동 정보가  존재하지 않습니다.";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_BIG_NOT_EXIST_BED;
		} 
		
		if("".equals(ydMtlWGp)){
			ydMtlWGp =  ydPilingCd.substring(4, 6);
		}
		
		if("".equals(ydMtlLGp)){
			ydMtlLGp =  ydPilingCd.substring(6, 8);
		}
		
		String ydBayGp				= ydRcptPlnStrLoc.substring(1, 2);
		
		// 동간입고 및 이적시에 작업예약의 To위치 동 정보를 활용한다.
		if(!"".equals(sBayGp)){
			ydBayGp = sBayGp;
		}
		
		//여기에 입고동 다시 탐색하는 로직 추가  REQ202407592586 저장계획 변경시, 입고예정위치 재탐색(후판동별저장계획) 임진후 기사 요청건
		
		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", operationName, "APPPI6", "T", "*");//APPPI4:후판 개발 적용여부
		
		if(sApplyYnPI.equals("Y")){
			logMsg = "["+ operationName +"] 입고대상동 재탐색 로직 시작";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);			
		
			String ordNo			= ydDaoUtils.paraRecChkNull(recTemp, "ORD_NO");
			String ordDtl			= ydDaoUtils.paraRecChkNull(recTemp, "ORD_DTL");


			//24.08.20 임진후기사 요청. 1/2후판 분리를 기존 조업공장구분이 아닌 R/T 기준으로 적용 필요
			//String szPTOP_PLNT_GP   = ydDaoUtils.paraRecChkNull(recTemp, "PTOP_PLNT_GP");
			String ptopPlntGp     = "PA";
					
			if("A".equals(sRtGp)|| 	
				    "B".equals(sRtGp)||
				    "C".equals(sRtGp)){
				ptopPlntGp = "PB";
			}
			
			logMsg = "["+ operationName +"] 해당주문 :"+ ordNo + "-" +ordDtl + "저장계획코드 READ";	
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);		
			
			//recPara 		= JDTORecordFactory.getInstance().create();
			recPara.setField("ORD_NO",    	ordNo);			
			recPara.setField("ORD_DTL",    	ordDtl);			
			
			rsResult= JDTORecordFactory.getInstance().createRecordSet("");
			//저장계획 코드 Read
			/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommLocPlanCd_PIDEV*/
			int rtnVal = ptOsCommDao.getPtOsComm(recPara, rsResult, 300);
			
			rsResult.absolute(1);
			JDTORecord outRec = JDTORecordFactory.getInstance().create();
			outRec = rsResult.getRecord();

	
			String locPlanCd		= ydDaoUtils.paraRecChkNull(outRec,"LOC_PLAN_CD");
			String mainTransArea 	= ydDaoUtils.paraRecChkNull(outRec,"MAIN_TRANS_AREA");
			
			rsResult= JDTORecordFactory.getInstance().createRecordSet("");
			recPara.setField("YD_PILING_CD",   	ydPilingCd);			
			recPara.setField("LOC_PLAN_CD",    	locPlanCd);			
			recPara.setField("MAIN_TRANS_AREA", mainTransArea);
			/* 
			 * 2016.03.21 윤재광 
			 * - 1후판 저장계획 추가에 따른 적용을 위해 추가 PARAM  
			 */
			recPara.setField("PTOP_PLNT_GP",    ptopPlntGp); 
			
			/*
			 * 2024.09.13 후판동별저장계획 화면 개선요청 임진후 기사 요청 --REQ202408611796
			 * 수출재 신규고객사 추가. 고객사별 개별셋팅을 하기때문에 szLOC_PLAN_CD 는 버리고 파일링코드 앞 4자리 사용
			 * 
			 * 2024.10.04 주문은 파일링 E453 이나, 주문전환? (사외창고 가거나..) 하는 경우 입고전 재료의 파일링코드가 주문외(M001) 로 빠지는 경우가 있음
			 * 이 경우 신규동별저장계획 적용시, 파라미터가 아래와같음. 
			 * :V_PTOP_PLNT_GP      PA	
			 * :V_MAIN_TRANS_AREA	E
		     * :V_YD_PILING_CD	    M001S0U0
		     * :V_LOC_PLAN_CD		M001
		     * 
		     * --> V_MAIN_TRANS_AREA 가 'E' 코드가 나오는게 문제. 24.10.04 임진후 기사 통화에서 이런 CASE는 주문외 적용해달라 답변
		     * V_MAIN_TRANS_AREA 값을 TB_YD_RULE 의 CD_GP 와 맵핑하지 말고, V_LOC_PLAN_CD 의 첫글자와 맵핑
			 * 
			 * */
			String szORD_GP = ordNo.substring(0,1);
			sApplyYnPI = ydPICommDAO.ApplyYnPI("", operationName, "APP060", "T", "002");
			if ("Y".equals(sApplyYnPI) && ( (szORD_GP.equals("E")) || (szORD_GP.equals("F")) ) ){
				logMsg = "신규 동별저장계획기준 해당주문 :"+ ordNo + "-" +ordDtl +"권역구분["+locPlanCd+ "] 대신 ["+ydPilingCd.substring(0,4)+"]사용";
				ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
				locPlanCd = ydPilingCd.substring(0,4);
				recPara.setField("LOC_PLAN_CD",    	locPlanCd);
			}
			
			rtnVal = commDao.select(recPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0059");
			
			rsResult.absolute(1);
			outRec = JDTORecordFactory.getInstance().create();
			outRec = rsResult.getRecord();
	
			ydBayGp = ydDaoUtils.paraRecChkNull(outRec,"DONG");
			
			logMsg = "["+ operationName +"] 입고대상동 재탐색 로직 종료. 재탐색 입고동 : ["+ydBayGp+"]";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
		}
		
		logMsg = "["+ operationName +"] 입고존 재료정보["+sStlNo+"]를 저장품에서 조회 완료 - 길이구분["+ydMtlLGp+"], 폭구분["+ydMtlWGp+"], Piling코드["+ydPilingCd+"]";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	

		logMsg = "["+ sRtGp +"] 입고RT / ["+ydBayGp+"]입고동 ";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	

		
		String sTmpYdGp = "1";
    	//Y4CrnSchCrnSpecCheckDtl 의 스케줄코드 기준 1,2후판 구분 채용
    	if("A".equals(sRtGp)|| 	
			    "B".equals(sRtGp)||
			    "C".equals(sRtGp)){   //21,22, UT 추가
				
				sTmpYdGp = "2";  //구2후판
		}
		
		//----------------------------------------------------------------------------------------------------------------------
	    //	이적스판검색범위
	    //----------------------------------------------------------------------------------------------------------------------
	    JDTORecord moveSpanRangeRecord = JDTORecordFactory.getInstance().create();
	   
	    moveSpanRangeRecord.setField("YD_EQP_GP", "RT");
	    moveSpanRangeRecord.setField("YD_IN_RT", sRtGp);
	    moveSpanRangeRecord.setField("YD_BAY_GP", ydBayGp);
	    moveSpanRangeRecord.setField("LOG_ID", logId);
	   
	    //이적스판 검색범위 검색 
	    JDTORecord moveSpanRangeResult = findMoveSpanSearchRangeForPre(moveSpanRangeRecord);
	   
	    String fromDong = moveSpanRangeResult.getFieldString("FIND_FROM_LOC");
	    String toDong = moveSpanRangeResult.getFieldString("FIND_TO_LOC");
	    
	    recPara 	= JDTORecordFactory.getInstance().create();
	    
	    String ydGp = "T";
    	//
	    recPara.setField("YD_GP"			, ydGp);
		recPara.setField("YD_BAY_GP"		, ydBayGp);
		recPara.setField("YD_PILING_CD"	, ydPilingCd);
		recPara.setField("FROM_STK_COL_GP"	, ydGp + ydBayGp + fromDong); 
		recPara.setField("TO_STK_COL_GP"  	, ydGp + ydBayGp + toDong);
		
		recPara.setField("YD_EQP_WRK_SH"	, ydEqpWrkSh);
		recPara.setField("YD_EQP_WRK_WT"	, ydEqpWrkWt);
		recPara.setField("YD_EQP_WRK_T"	, ydEqpWrkT);
	    
	    recPara.setField("FROM_DONG", 			fromDong);
	    recPara.setField("TO_DONG", 			toDong);
    	
	    recPara.setField("YD_STK_COL_GP", 			ydGp+ydBayGp+"RTR"+sRtGp);
	    recPara.setField("YD_STK_BED_NO", 			"01");

	    recPara.setField("STL_NO", 			sStlNo);
	    recPara.setField("YD_MTL_W_GP", 			ydMtlWGp);
	    recPara.setField("YD_MTL_L_GP", 			ydMtlLGp);
	    recPara.setField("LOG_ID"		, 		logId);	
		
		
    	//입고 예정위치이므로 입고에 대해서 검색
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
    	recTemp.setField("REPR_CD_GP", "T00072");
    	recTemp.setField("CD_GP", sTmpYdGp + ydBayGp + "RTL");
  
    	
    	int rtnVal = commDao.select(recTemp, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdRuleList");
    	
    	String [] toLocFindOrder;
    	    	
    	JDTORecord ruleRsTemp = JDTORecordFactory.getInstance().create("");
    	
    	if (rtnVal > 0) {
    		toLocFindOrder = new String[rtnVal+1];
    		
    		//1후판 입고가적베드 탐색은 항상 첫순위
    		toLocFindOrder[0] = "TEMP";
    		logMsg = "["+ operationName +"] 의 첫번째 검색순서["+ toLocFindOrder[0] +"] ";
    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
    		
    		for(int i=1; i<=rtnVal; i++){
    			rsResult.absolute(i);
    			ruleRsTemp			= rsResult.getRecord();
    			toLocFindOrder[i] = ruleRsTemp.getFieldString("ITEM");
    			
				logMsg = "["+ operationName +"] ["+ Integer.toString(i+1) +"] 번째 검색순서["+ toLocFindOrder[i] +"] ";
				ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
    		}
		}
    	//기준에 없는경우 디폴트 순서인 동일파일링 + 혼적베드 + 공베드 + 단일파일링코드
    	else{
    		logMsg = "["+ operationName +"] 의 순서기준 없는경우 파일링-혼적-공베드 순 검색";
    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
			
    		rtnVal = 5;
    		toLocFindOrder = new String[rtnVal];
    		toLocFindOrder[0] = "TEMP";
    		toLocFindOrder[1] = "SAME";
    		toLocFindOrder[2] = "SIMIL";
    		toLocFindOrder[3] = "EMPTY";
    		toLocFindOrder[4] = "SINGL";
    	}
    	
    	YdStkLocVO ydStkLocVO = new YdStkLocVO();
    	
    	for(int i=0; i<rtnVal; i++){
    				
    		ydStkLocVO = callFunctionGetBedForMainWrk(toLocFindOrder[i] , recPara);
    		
    		if(ydStkLocVO != null) {
    			String plnLocDcsnGp = ydStkLocVO.getPlnLocDcsnGp();
    			
    			String findMethod = "";
    			if("P".equals(plnLocDcsnGp)){
    				findMethod = "동일파일링코드";
    			}
    			else if("S".equals(plnLocDcsnGp)){
    				findMethod = "혼적베드";
    			}
    			else if("E".equals(plnLocDcsnGp)){
    				findMethod = "공베드";
    			}
    			else if("I".equals(plnLocDcsnGp)){
    				findMethod = "단일파일링";
    			}
    			logMsg = "["+ operationName +"] 입고예정위치 찾기 성공 ["+ findMethod +"]";
    			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
    			break;
    		}
    	}
    	if( ydStkLocVO == null ) {
			if(isRcvPlateBigCust(recPara)) {

				logMsg = "["+ operationName +"] 권하위치검색 실패 - 대형고객사 적치가능한 베드가 존재하지 않습니다.";
				ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
				return YdConstant.RETN_BIG_NOT_EXIST_BED;
				
			} else {
				logMsg = "["+ operationName +"] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
				ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
				return YdConstant.RETN_NOT_EXIST_BED;
			}	
		}

		/*
		 * 입고예정위치  최종결정정보 셋팅.
		 */
		recTemp 	= JDTORecordFactory.getInstance().create();
		
		String ydDnStkColGp 	= ydStkLocVO.getYdStkColGp();
		String ydDnStkBedNo 	= ydStkLocVO.getYdStkBedNo();

		String ydDnWoLoc        = ydDnStkColGp + ydDnStkBedNo;
		
		/*
		 * 2014.10.15 윤재광 - 이명운대리 요청
		 * G동 중척재이하는 무조건 2베드로 셋팅
		 */
		if("G".equals(ydBayGp) && ("M".equals(ydPilingCd.substring(6,7))||
				                       "S".equals(ydPilingCd.substring(6,7))||
				                       "U".equals(ydPilingCd.substring(6,7)))){
			recTemp.setField("YD_STK_BED_NO",   "02");
		}else{
			recTemp.setField("YD_STK_BED_NO",   ydDnStkBedNo);
		}
		
		//업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준) 야드구분값 셋팅
		if("A".equals(sRtGp)||"B".equals(sRtGp)||"C".equals(sRtGp)){
			recTemp.setField("YD_GP", 	"T");
		}else{
			recTemp.setField("YD_GP", 	"K");
		}
		recTemp.setField("YD_BAY_GP"		, ydBayGp);
		
		String rtnBookOutLoc = "";
		//업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준)
    	if( GetBreRule6.getYDB674(recTemp) ) {
    		rtnBookOutLoc = StringHelper.evl(recTemp.getFieldString("YDB674_RV01_YD_BOOK_OUT_LOC"), "00000"); // 업무기준 YDB674 반환값#1 YD_BOOK_OUT_LOC
    	} else {
    		rtnBookOutLoc ="";
    	}							
		//-------------------------------------------------------
		
		//-------------------------------------------------------
    	recTemp 	= JDTORecordFactory.getInstance().create();
    	
    	recTemp.setField("STL_NO"				, sStlNo);
    	recTemp.setField("YD_RCPT_PLN_STR_LOC"	, ydDnWoLoc);
    	recTemp.setField("YD_BOOK_OUT_LOC"		, rtnBookOutLoc);
    	recTemp.setField("MODIFIER"				, "PRELOCREG");
		
    	/*
    	 * com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0057         
			UPDATE TB_YD_STOCK
			   SET YD_BOOK_OUT_LOC      = :V_YD_BOOK_OUT_LOC
			      ,YD_RCPT_PLN_STR_LOC  = :V_YD_RCPT_PLN_STR_LOC
			 WHERE STL_NO = :V_STL_NO      
    	 */
		rtnVal = commDao.update(recTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0057");
		
		/*
		 * com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0058        
			UPDATE TB_PT_PLATECOMM
			   SET YD_BOOK_OUT_LOC = :V_YD_BOOK_OUT_LOC
			 WHERE PLATE_NO = :V_STL_NO      
		 */
		rtnVal = commDao.update(recTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0058");
				
		
		logMsg = operationName+"(" + methodName + ") 완료";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);


	
	return YdConstant.RETN_CD_SUCCESS;
	} //end of procPreMainWrkToLocForPlateYd()

	

	/**
	 * 입고존 도착시 동별 분산 로직 추가요청 2020.12.09 이명운
	 * - R/T상 입고 제품 기준 E/F동 자동분산(두께 합산 50T미만)
	 * - D/E R/T
	 * - 제품 초단척 제품(길이그룹 U)
	 * ----신규 동별저장계획으로 인해 더이상 불필요 24.11.04 부로 삭제 --임진후기사 요청 
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String procPreMainWrkBookOutLocForPlateYd(
			  String sStlNo					/* 입고존 도착재료 */
			, String sRtGp 					/* 입고존 RT */
			, String logId					/* logId */
			) throws JDTOException {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// procPreMainWrkBookOutLocForPlateYd argument 에 logId 항목 추가 개선
//	public static String procPreMainWrkBookOutLocForPlateYd(
//			  String sStlNo					/* 입고존 도착재료 */
//			, String sRtGp 					/* 입고존 RT */
//			) throws JDTOException {
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

		String szMethodName				= "procPreMainWrkBookOutLocForPlateYd";
		String szOperationName			= "입고존 도착시 동별 분산 로직 추가(후판제품)";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		JDTORecordSet	rsResult		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		String szYD_BAY_GP				= "";
		String szYD_MTL_L_GP			= "";						//야드재료길이구분
		String szYD_PILING_CD			= "";						//야드Piling코드
		String szYD_RCPT_PLN_STR_LOC 	= "";
		String sRTN_BOOKOUT_LOC 		= "";
		String szYD_EQP_WRK_T 			= "";			//크레인작업재료 총높이
		
		String szYD_TR_GP=""; //운송구분  E:수출, S:육송  Y:연안 M:주문외 등
		
		int intRtnVal               	= 0; 

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// logId Empty 이면 logId 신규 생성 개선
if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "입고존 도착시 동별 분산(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);

// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		//----------------------------------------------------------------------------------------------------------------------
		//	입고존 재료를 저장품으로부터 조회 시작
		//----------------------------------------------------------------------------------------------------------------------
		rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		
		recPara.setField("STL_NO", sStlNo);
		
		szLogMsg = "["+ szOperationName +"] 입고존 재료정보["+sStlNo+"]를 저장품에서 조회 시작 ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szRtnMsg = DaoManager.getYdStock(recPara, rsResult, 0);
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			return szRtnMsg;
		}
		
		rsResult.first();
		recTemp = rsResult.getRecord();
		
		szYD_PILING_CD 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_PILING_CD");			//크레인작업 최하단재료의 Piling코드
		szYD_RCPT_PLN_STR_LOC 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_RCPT_PLN_STR_LOC");	//입고예정위치
		szYD_EQP_WRK_T 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_T");				//크레인작업재료 총높이
		
		if("".equals(szYD_PILING_CD)){
			szLogMsg = "["+ szOperationName +"] 파일링코드 정보가  존재하지 않습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_BIG_NOT_EXIST_BED;
		}
		
		if(szYD_PILING_CD.length() != 8){
			szLogMsg = "["+ szOperationName +"] 파일링코드 정보 자릿수가  8자리가 아닙니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_BIG_NOT_EXIST_BED;
		}
		
		if(szYD_RCPT_PLN_STR_LOC.length() < 2){
		
			szLogMsg = "["+ szOperationName +"] 입고예정위치 동 정보가  존재하지 않습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_BIG_NOT_EXIST_BED;
		} 
		
		szYD_MTL_L_GP 	= szYD_PILING_CD.substring(6, 7);
		szYD_TR_GP          = szYD_PILING_CD.substring(0, 1);  //파일링 코드 앞자리(운송구분)
		szYD_BAY_GP		= szYD_RCPT_PLN_STR_LOC.substring(1, 2);
		
		szLogMsg = "["+ szOperationName +"] 입고존 재료정보["+sStlNo+"]를 저장품에서 조회 완료 - R/T["+sRtGp+"],예정동["+szYD_BAY_GP+"],길이구분["+szYD_MTL_L_GP+"], Piling코드["+szYD_PILING_CD+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		//	입고존 재료를 저장품으로부터 조회 끝
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	입고 분산로직을 수행할 조건인지 체크 시작 
		//----------------------------------------------------------------------------------------------------------------------
		if("Y".equals(szYD_TR_GP)){  //연안해송물량(운송구분:Y) 제외 요청. 제품출하팀 최한국 사원. 2022.07.14
			szLogMsg = "["+ szOperationName +"] 대상 운송구분 아님(Y)="+szYD_TR_GP;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_CD_FAILURE;
		}		
		
		if(!"D".equals(sRtGp)&&!"E".equals(sRtGp)){
			szLogMsg = "["+ szOperationName +"] 입고R/T 기준아님="+sRtGp;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_CD_FAILURE;
		}
		if(!"U".equals(szYD_MTL_L_GP)){
			szLogMsg = "["+ szOperationName +"] 길이그룹 기준아님="+szYD_MTL_L_GP;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_CD_FAILURE;
		}
		if(!"E".equals(szYD_BAY_GP)&&!"F".equals(szYD_BAY_GP)){
			szLogMsg = "["+ szOperationName +"] 입고동 기준아님="+szYD_BAY_GP;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_CD_FAILURE;
		}
		//----------------------------------------------------------------------------------------------------------------------
		//	입고 분산로직을 통한 입고동 정복 가져오기
		//----------------------------------------------------------------------------------------------------------------------
		recTemp 	= JDTORecordFactory.getInstance().create();
    	
		recTemp.setField("STL_NO"	, sStlNo);
    	recTemp.setField("RT_GP"	, sRtGp);
    	recTemp.setField("BAY_GP"	, szYD_BAY_GP);
    	recTemp.setField("MTL_T"	, szYD_EQP_WRK_T);
    	
		JDTORecord recRecord2 	= JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
		
		/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getBayGpByStlNo*/
		szRtnMsg = DaoManager.getYdStock(recTemp, rsResult2, 228);
		
		rsResult2.first();
		recRecord2 = rsResult2.getRecord();
		
		String sAIM_DONG = ydDaoUtils.paraRecChkNull(recRecord2, "AIM_DONG");
		
		szLogMsg = "["+ szOperationName +"] 분산로직에 의한 원래 입고동 ="+szYD_BAY_GP;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		if(!"".equals(sAIM_DONG)){
			szYD_BAY_GP = sAIM_DONG;
		}
		
		szLogMsg = "["+ szOperationName +"] 분산로직에 의한 변경 입고동 ="+sAIM_DONG;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		//	입고동 정보 셋팅하기
		//----------------------------------------------------------------------------------------------------------------------
		YdPlateCommDAO commDao = new YdPlateCommDAO();
		
		recTemp	= JDTORecordFactory.getInstance().create();
		
		//업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준) 야드구분값 셋팅
		if("A".equals(sRtGp)||"B".equals(sRtGp)||"C".equals(sRtGp)){
			recTemp.setField("YD_GP", 	"T");
		}else{
			recTemp.setField("YD_GP", 	"K");
		}
		recTemp.setField("YD_BAY_GP"	, 	szYD_BAY_GP);
		recTemp.setField("YD_STK_BED_NO",   "01");
		
		//업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준)
    	if( GetBreRule6.getYDB674(recTemp) ) {
    		sRTN_BOOKOUT_LOC = StringHelper.evl(recTemp.getFieldString("YDB674_RV01_YD_BOOK_OUT_LOC"), "00000"); // 업무기준 YDB674 반환값#1 YD_BOOK_OUT_LOC
    	} else {
    		sRTN_BOOKOUT_LOC ="";
    	}							
		//-------------------------------------------------------
		
		//-------------------------------------------------------
    	recTemp 	= JDTORecordFactory.getInstance().create();
    	
    	recTemp.setField("STL_NO"				, sStlNo);
    	recTemp.setField("YD_RCPT_PLN_STR_LOC"	, "T"+szYD_BAY_GP+"XXXX01");
    	recTemp.setField("YD_BOOK_OUT_LOC"		, sRTN_BOOKOUT_LOC);
    	recTemp.setField("MODIFIER"				, "PRELOCREG");
		
    	/*
    	 * com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0057         
			UPDATE TB_YD_STOCK
			   SET YD_BOOK_OUT_LOC      = :V_YD_BOOK_OUT_LOC
			      ,YD_RCPT_PLN_STR_LOC  = :V_YD_RCPT_PLN_STR_LOC
			 WHERE STL_NO = :V_STL_NO      
    	 */
		intRtnVal = commDao.update(recTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0057");
		
		/*
		 * com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0058        
			UPDATE TB_PT_PLATECOMM
			   SET YD_BOOK_OUT_LOC = :V_YD_BOOK_OUT_LOC
			 WHERE PLATE_NO = :V_STL_NO      
		 */
		intRtnVal = commDao.update(recTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0058");

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  END
szLogMsg = "입고존 도착시 동별 분산(" + szMethodName + ") 완료";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 보조작업TO위치결정(후판제품)
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String procAidWrkToLocForPlateYd(
			JDTORecord msgRecord					/* 전문 */
			, JDTORecordSet rsCrnwrkmtl				/* 크레인작업재료 */
			, JDTORecord recCrnSch					/* 크레인스케줄정보 */
			, JDTORecord recWbook					/* 작업예약정보 */
			) throws JDTOException {
		/*
		 * 업무기준	:	1. 
		 * 
		 * 수정자	: 임춘수
		 * 수정일	:
		 * 				1. 2009.11.30 - 최초등록
		 * 
		 */
		String szMethodName				= "procAidWrkToLocForPlateYd";
		String szOperationName			= "보조작업TO위치결정(후판제품)";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		ArrayList		listToLoc		= null;
		YdStkLocVO		ydStkLocVO		= null;
		
		JDTORecordSet	rsResult		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		JDTORecord		recLogMsg		= null;
		
		String szYD_UP_WO_LOC			= null;
		String szYD_UP_WO_LAYER			= null;
		String szYD_DN_WO_LOC			= null;
		String szYD_DN_WO_LAYER			= null;
		String szYD_UP_STK_COL_GP		= null;
		String szYD_UP_STK_BED_NO		= null;
		String szYD_DN_STK_COL_GP		= null;
		String szYD_DN_STK_BED_NO		= null;
		String szYD_SCH_CD				= null;
		
		boolean bUP_UPDT_NEEDED			= false;
		
		String	szSTL_NO				= null;
		String[] szYD_STK_LYR_MTL_STAT	= {"D", "U"};
		
		String	szYD_TO_LOC_GUIDE		= null;
		
		String szYD_CRN_SCH_ID			= null;
		String szYD_EQP_ID				= null;
		
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_GP				= null;
		
		String szYD_MTL_W_GP			= null;						//야드재료폭구분
		//String szYD_MTL_T_GP			= null;						//야드재료두께구분
		String szYD_MTL_L_GP			= null;						//야드재료길이구분
		
		String szYD_PILING_CD			= null;						//야드Piling코드
		
		int intYD_EQP_WRK_SH			= 0;						//야드설비작업매수
		int intYD_EQP_WRK_WT			= 0;						//야드설비작업중량
		double dblYD_EQP_WRK_T			= 0;						//야드설비작업총두께
		String szYD_EQP_WRK_MAX_W		= null;						//작업재료 중 최대 폭
		String szYD_EQP_WRK_MAX_L		= null;						//작업재료 중 최대 길이

		JDTORecordSet outResult1 		= null;
		JDTORecord inRecord3 			= null;
		JDTORecord inRecord2 			= null;
		YdCarSchDao ydCarSchDao = new YdCarSchDao();	
		String szFROM_DONG 				= "";
		String szTO_DONG   				= "";
		String szYD_CRN_GRAB_TP			= "";
		int intRtnVal					= 0;
		
		////////////////////////////////////////////////////////////////////////////////////////
		// 2024.09.?? 로그 개선  START
		// 기존 putLog -> putLogNew logId 출력 되게 개선
		String logId                            = ydUtils.getJDTOLogId(msgRecord, "T");  // JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
		
		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
		
		szLogMsg = "보조작업TO위치결정(후판제품)(" + szMethodName + ") 시작";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		// 2024.09.?? 로그 개선  END
		////////////////////////////////////////////////////////////////////////////////////////

		//2024.12.12 임진후기사 요청 RITM0791916
		//스케줄별로 가적->파일링->혼적->공베드->일반 베드 순서의 to위치 탐색 순서를 유동적으로 조절할수있게 변경
		//신규함수 사용 
		YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();	
		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "007");//후판 개발 적용여부
		
		if("Y".equals(sApplyYnPI)){
			szLogMsg = "procAidWrkToLocForPlateYd 신규메소드 사용";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			
			return procAidWrkToLocForPlateYdNew(msgRecord, rsCrnwrkmtl, recCrnSch, recWbook);
		}
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------
		
		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		
		szLogMsg = "["+ szOperationName +"] -------------------- 크레인작업재료의 최하단 재료정보 확인 --------------------";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		szYD_CRN_SCH_ID      	= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_CRN_SCH_ID");					//크레인스케줄ID
		szYD_EQP_ID      		= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_EQP_ID");						//크레인설비ID
		
		intYD_EQP_WRK_SH      	= ydDaoUtils.paraRecChkNullInt(recPara,"SH_CNT");						//크레인작업재료 총매수
		intYD_EQP_WRK_WT      	= ydDaoUtils.paraRecChkNullInt(recPara,"SUM_MTL_WT");					//크레인작업재료 총중량
		dblYD_EQP_WRK_T      	= ydDaoUtils.paraRecChkNullDouble(recPara,"SUM_MTL_T");					//크레인작업재료 총높이
		
		szYD_EQP_WRK_MAX_W		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_W");						//크레인작업재료 중 최대 폭
		szYD_EQP_WRK_MAX_L		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_L");						//크레인작업재료 중 최대 길이
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 총매수 :" + intYD_EQP_WRK_SH;
		szLogMsg += ", 총중량 :" + intYD_EQP_WRK_WT;
		szLogMsg += ", 총높이  :" + dblYD_EQP_WRK_T;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		
		szLogMsg = "["+ szOperationName +"] -------------------- 작업예약정보 확인 --------------------";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szYD_TO_LOC_GUIDE		= ydDaoUtils.paraRecChkNull(recWbook, "YD_TO_LOC_GUIDE");				//사용자지정위치
		szYD_SCH_CD				= ydDaoUtils.paraRecChkNull(recWbook, "YD_SCH_CD");						//크레인스케줄코드
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	최하단 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		szSTL_NO	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		szRtnMsg = DaoManager.getYdStock(recPara, rsResult, 0);
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			return szRtnMsg;
		}
		
		rsResult.first();
		recTemp = rsResult.getRecord();
		
		szYD_MTL_L_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");			//크레인작업 최하단재료의길이구분
		szYD_MTL_W_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_W_GP");			//크레인작업 최하단재료의폭구분
		szYD_PILING_CD 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_PILING_CD");			//크레인작업 최하단재료의Piling코드
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 완료 - 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"], Piling코드["+szYD_PILING_CD+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	권상중이거나 권하중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYD_UP_WO_LOC 		= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");
		szYD_UP_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER");
		
		if( szYD_UP_WO_LOC.equals("") ) {
			
			szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			szRtnMsg = YdCommonUtils.getYdStkLyrWithMtlStat(recPara, rsResult, szYD_STK_LYR_MTL_STAT);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				return szRtnMsg;
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 완료  ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YD_STK_COL_GP");
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YD_STK_BED_NO");
			szYD_UP_WO_LOC 			= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYD_UP_WO_LAYER 		= recTemp.getFieldString("YD_STK_LYR_NO");
			
			bUP_UPDT_NEEDED			= true;
			
			szLogMsg = "["+ szOperationName +"] 조회된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		}else{
			szYD_UP_STK_COL_GP = szYD_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYD_UP_WO_LOC.substring(6);
			
			szLogMsg = "["+ szOperationName +"] 크레인스케줄에 등록된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		listToLoc = new ArrayList();
		
		//----------------------------------------------------------------------------------------------------------------------
		//	입고재료와 같은 Piling Code의 적치가능한 베드 OR 길이구분/폭구분이 같은 해당 동의 모든 위치를 조회 
		//----------------------------------------------------------------------------------------------------------------------
		
		//권상지시위치에 따라 알맞은 적치가능한 베드 검색 방법을 적용
		
		szYD_GP			= szYD_UP_WO_LOC.substring(0, 1);
    	szYD_BAY_GP		= szYD_UP_WO_LOC.substring(1, 2);
    	szYD_EQP_GP		= szYD_UP_WO_LOC.substring(2, 4);
    	
    	recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("YD_STK_COL_GP", 		szYD_UP_STK_COL_GP);								//권상지시위치 - 적치열
    	recTemp.setField("YD_STK_BED_NO", 		szYD_UP_STK_BED_NO);								//권상지시위치 - 적치베드
    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH));					//크레인작업재료 총매수
    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT));					//크레인작업재료 총중량
    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T));					//크레인작업재료 총높이
    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);										//크레인 스케줄코드
    	recTemp.setField("YD_PILING_CD", 		szYD_PILING_CD);									//크레인작업 최하단재료의 Piling Code
    	recTemp.setField("YD_MTL_L_GP", 		szYD_MTL_L_GP);										//크레인작업 최하단재료의 길이구분
    	recTemp.setField("YD_MTL_W_GP", 		szYD_MTL_W_GP);										//크레인작업 최하단재료의 폭구분
    	recTemp.setField("YD_EQP_ID", 			szYD_EQP_ID);										//크레인설비ID
    	
    	//----------------------------------------------------------------------------------------------------------------------
		//	동일한 Piling Code의 적치가능한 베드 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]의 적치가능한 베드 조회 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
		//----------------------------------------------------------------------------------------------------------------------
		//	이적스판검색범위
		//----------------------------------------------------------------------------------------------------------------------
		outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
		inRecord3 	= JDTORecordFactory.getInstance().create();
		inRecord2 	= JDTORecordFactory.getInstance().create();
		
		inRecord3.setField("REPR_CD_GP" ,"TI0021");	//이적위치검색범위
		inRecord3.setField("CD_GP" ,szYD_EQP_ID.substring(5, 6));    // 크레인 호기			

		/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgt*/
		intRtnVal = ydCarSchDao.getYdCarsch(inRecord3, outResult1, 400);
		
		if (intRtnVal > 0) {
			outResult1.first();
			inRecord2 = outResult1.getRecord();
			szFROM_DONG = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, szYD_BAY_GP+"_DONG"),"0101");
			szTO_DONG   = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, szYD_BAY_GP+"_DONG2"),"0799");
		} else {
			szFROM_DONG = "0101";
			szTO_DONG   = "0799";
		}
		
		//---------------------------------------------------------------------------------------------------------
		// 크레인사양 조회
		// 설비ID로  크크레인 Grab 구분 추출
		//---------------------------------------------------------------------------------------------------------
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, "To위치결정[크레인GRAB구분확인]"+szYD_EQP_ID, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, "To위치결정[크레인GRAB구분확인]"+szYD_EQP_ID, YdConstant.DEBUG, logId);
		
		JDTORecord recTemp1 	= JDTORecordFactory.getInstance().create();
		JDTORecord recSpec 		= JDTORecordFactory.getInstance().create();
		JDTORecordSet specSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recTemp1.setField("YD_EQP_ID", szYD_EQP_ID);
		
		szRtnMsg	= DaoManager.getYdCrnspec(recTemp1, specSet, 0);
		
		specSet.first();
		recSpec = specSet.getRecord();
		
		szYD_CRN_GRAB_TP = ydDaoUtils.paraRecChkNull(recSpec, "YD_CRN_GRAB_TP"); // 크레인 Grab 구분
	
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// getBedWithSamePilingCdForAidWrkNew call 시  recTemp 에 logId SET 추가 개선
		recTemp.setField("LOG_ID", logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		szRtnMsg = getBedWithSamePilingCdForAidWrkNew(szFROM_DONG, szTO_DONG, szYD_CRN_GRAB_TP,  recTemp, listToLoc);
	
		szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]의 적치가능한 베드 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
	
		//----------------------------------------------------------------------------------------------------------------------
		// Sorting
		//----------------------------------------------------------------------------------------------------------------------
		if( listToLoc.size() > 0 ) {
			Collections.sort(listToLoc, new YdStkLocComparator());
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
		boolean bIS_BED_STACKABLE		= false;
		
		for(int i = 0; i < listToLoc.size(); i++ ) {
			
			ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
			szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    		
    		//----------------------------------------------------------------------------------------------------------------------
    		//	야드To위치Guide가 존재하고 길이정합성이 맞는 경우에는 보조작업 TO위치 결정 시는 SKIP 시킴
    		//----------------------------------------------------------------------------------------------------------------------
    		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
    			//----------------------------------------------------------------------------------------------------------------------
    			//	야드To위치결정방법이 F이고 야드To위치Guide가 존재하는 경우에는 보조작업이적 시에는 SKIP시킴
    			//----------------------------------------------------------------------------------------------------------------------
    			if( szYD_TO_LOC_GUIDE.substring(0, 6).equals(ydStkLocVO.getYdStkColGp()) && 
    				szYD_TO_LOC_GUIDE.substring(6).equals(ydStkLocVO.getYdStkBedNo())  ) {
    				szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]가 작업예약의 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]와 동일하므로 SKIP시킴";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    				continue;
    			}
    			//----------------------------------------------------------------------------------------------------------------------
    		}
    		//----------------------------------------------------------------------------------------------------------------------
    		
    		//----------------------------------------------------------------------------------------------------------------------
    		//	베드가 적치가능한 지 판단 - 가능하면 루프 종료
    		//----------------------------------------------------------------------------------------------------------------------
    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
				
				bIS_BED_STACKABLE		= true;
				
				szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				szLogMsg = "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		
				break;
			}
			//----------------------------------------------------------------------------------------------------------------------
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	길이구분/폭구분이 동일한 공베드가 존재하지 않을 경우에는 
		//	혼적베드를 조회
		//----------------------------------------------------------------------------------------------------------------------
		if( !bIS_BED_STACKABLE) {
			
			ydStkLocVO	= null;
			
			listToLoc 	= new ArrayList();
			
			szLogMsg = "["+ szOperationName +"] 혼적베드 조회 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// getBedWithSimilarGpForAidWrkNew call 시  recTemp 에 logId SET 추가 개선
			recTemp.setField("LOG_ID", logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
			szRtnMsg = getBedWithSimilarGpForAidWrkNew(szSTL_NO, szFROM_DONG, szTO_DONG, szYD_CRN_GRAB_TP,  recTemp, listToLoc);
			
			szLogMsg = "["+ szOperationName +"] 혼적베드 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
			// Sorting
			//----------------------------------------------------------------------------------------------------------------------
			if( listToLoc.size() > 0 ) {
				Collections.sort(listToLoc, new YdStkLocComparator());
			}
			//----------------------------------------------------------------------------------------------------------------------
			 
			//----------------------------------------------------------------------------------------------------------------------
			//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
			//----------------------------------------------------------------------------------------------------------------------
			for(int i = 0; i < listToLoc.size(); i++ ) {
				ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
				szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 혼적베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
	    		//	야드To위치Guide가 존재하고 길이정합성이 맞는 경우에는 보조작업 TO위치 결정 시는 SKIP 시킴
	    		//----------------------------------------------------------------------------------------------------------------------
	    		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
	    			//----------------------------------------------------------------------------------------------------------------------
	    			//	야드To위치결정방법이 F이고 야드To위치Guide가 존재하는 경우에는 보조작업이적 시에는 SKIP시킴
	    			//----------------------------------------------------------------------------------------------------------------------
	    			if( szYD_TO_LOC_GUIDE.substring(0, 6).equals(ydStkLocVO.getYdStkColGp()) && 
	    				szYD_TO_LOC_GUIDE.substring(6).equals(ydStkLocVO.getYdStkBedNo())  ) {
	    				szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 혼적베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]가 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]와 동일하므로 SKIP시킴";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//	    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    				continue;
	    			}
	    			//----------------------------------------------------------------------------------------------------------------------
	    		}
	    		//----------------------------------------------------------------------------------------------------------------------
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
	    		//	베드가 적치가능한 지 판단 - 가능하면 루프 종료
	    		//----------------------------------------------------------------------------------------------------------------------
	    		
				if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
					
					bIS_BED_STACKABLE		= true;
					
					szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					szLogMsg = "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 혼적베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    	    		szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    	    		
					break;
				}
				//----------------------------------------------------------------------------------------------------------------------
			}
			//----------------------------------------------------------------------------------------------------------------------
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 Piling Code의 베드가 존재하지 않을 경우에는 
		//	길이구분/폭구분이 동일한 공베드를 조회
		//----------------------------------------------------------------------------------------------------------------------
		if( !bIS_BED_STACKABLE  ) {
			
			ydStkLocVO	= null;
			
			listToLoc 	= new ArrayList();
			
			szLogMsg = "["+ szOperationName +"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 적치가능한 공베드 조회 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			szRtnMsg = getEmptyBedWithSameLWGpForAidWrkNew(szFROM_DONG, szTO_DONG, szYD_CRN_GRAB_TP,recTemp,listToLoc, szYD_CRN_SCH_ID);
			
			szLogMsg = "["+ szOperationName +"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 적치가능한 공베드 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
			// Sorting
			//----------------------------------------------------------------------------------------------------------------------
			if( listToLoc.size() > 0 ) {
				Collections.sort(listToLoc, new YdStkLocComparator());
			}
			//----------------------------------------------------------------------------------------------------------------------
			 
			//----------------------------------------------------------------------------------------------------------------------
			//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
			//----------------------------------------------------------------------------------------------------------------------
			for(int i = 0; i < listToLoc.size(); i++ ) {
				ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
				szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
	    		//	야드To위치Guide가 존재하고 길이정합성이 맞는 경우에는 보조작업 TO위치 결정 시는 SKIP 시킴
	    		//----------------------------------------------------------------------------------------------------------------------
	    		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
	    			//----------------------------------------------------------------------------------------------------------------------
	    			//	야드To위치결정방법이 F이고 야드To위치Guide가 존재하는 경우에는 보조작업이적 시에는 SKIP시킴
	    			//----------------------------------------------------------------------------------------------------------------------
	    			if( szYD_TO_LOC_GUIDE.substring(0, 6).equals(ydStkLocVO.getYdStkColGp()) && 
	    				szYD_TO_LOC_GUIDE.substring(6).equals(ydStkLocVO.getYdStkBedNo())  ) {
	    				szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]가 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]와 동일하므로 SKIP시킴";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//	    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    				continue;
	    			}
	    			//----------------------------------------------------------------------------------------------------------------------
	    		}
	    		//----------------------------------------------------------------------------------------------------------------------
	    		
	    		//----------------------------------------------------------------------------------------------------------------------
	    		//	베드가 적치가능한 지 판단 - 가능하면 루프 종료
	    		//----------------------------------------------------------------------------------------------------------------------
	    		
				if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
					
					bIS_BED_STACKABLE		= true;
					
					szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					szLogMsg = "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    	    		szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//    	    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    	    		
					break;
				}
				//----------------------------------------------------------------------------------------------------------------------
			}
			//----------------------------------------------------------------------------------------------------------------------
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		if( !bIS_BED_STACKABLE ) {
			
			szLogMsg = "["+ szOperationName +"] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_NOT_EXIST_BED;
		}
		
		/*
		 * 권하위치 최종결정정보 셋팅.
		 */
		szYD_DN_STK_COL_GP 	= ydStkLocVO.getYdStkColGp();
		szYD_DN_STK_BED_NO 	= ydStkLocVO.getYdStkBedNo();
		szYD_DN_WO_LOC		= szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;
		szYD_DN_WO_LAYER 	= ydStkLocVO.getYdStkLyrNo();
		
		
		//----------------------------------------------------------------------------------------------------------------------
		// 권하지시위치 수정
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		JDTORecord recUpCrnSch	= null;
		
		recUpCrnSch = JDTORecordFactory.getInstance().create();
		recUpCrnSch.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);										//크레인스케줄ID
		recUpCrnSch.setField("YD_EQP_ID", 				szYD_EQP_ID);											//크레인설비ID
		
		recUpCrnSch.setField("YD_UP_STK_COL_GP", 		szYD_UP_STK_COL_GP);									//권상지시위치 - 적치열
		recUpCrnSch.setField("YD_UP_STK_BED_NO", 		szYD_UP_STK_BED_NO);									//권상지시위치 - 적치베드
		recUpCrnSch.setField("YD_DN_STK_COL_GP", 		szYD_DN_STK_COL_GP);									//권하지시위치 - 적치열
		recUpCrnSch.setField("YD_DN_STK_BED_NO", 		szYD_DN_STK_BED_NO);									//권하지시위치 - 적치베드
		
		//----------------------------------------------------------------------------------------------------------------------
		//	권상지시위치 업데이트
		//----------------------------------------------------------------------------------------------------------------------
		if( bUP_UPDT_NEEDED ) {
			szLogMsg = "["+ szOperationName +"] 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"] 크레인스케줄에 등록";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			recUpCrnSch.setField("YD_UP_WO_LOC", 		szYD_UP_WO_LOC);										//권상지시위치
			recUpCrnSch.setField("YD_UP_WO_LAYER", 		szYD_UP_WO_LAYER);										//권상지시단
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		recUpCrnSch.setField("YD_DN_WO_LOC", 			szYD_DN_WO_LOC);										//권하지시위치
		recUpCrnSch.setField("YD_DN_WO_LAYER", 			szYD_DN_WO_LAYER);										//권하지시단
		recUpCrnSch.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));						//크레인작업재료 총매수
		recUpCrnSch.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));						//크레인작업재료 총중량
		recUpCrnSch.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));						//크레인작업재료 총높이
		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);									//크레인작업재료 중 최대 폭
		recUpCrnSch.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);									//크레인작업재료 중 최대 길이
		recUpCrnSch.setField("MODIFIER", 				szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		
		szRtnMsg = CrnSchUtil.uptCrnSchXYCord(recUpCrnSch);
    	
    	szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 완료";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
    	
		//----------------------------------------------------------------------------------------------------------------------
		//	권하지시위치에 재료를 권하대기로 등록
		//----------------------------------------------------------------------------------------------------------------------
    	szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]에 크레인작업재료 등록 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szRtnMsg	= uptDnWaitOnYdStkLyr(rsCrnwrkmtl, szYD_DN_WO_LOC, szYD_DN_WO_LAYER);
    	
		szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]에 크레인작업재료 등록 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
    	
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "보조작업TO위치결정(후판제품)(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		//----------------------------------------------------------------------------------------------------------------------
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 보조작업TO위치결정신규(후판제품) 
	 * 2024.12.12 임진후기사 요청 RITM0791916 BY 허정욱
	 * 스케줄별로 가적->파일링->혼적->공베드->일반 베드 순서의 to위치 탐색 순서를 유동적으로 조절할수있게 변경
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String procAidWrkToLocForPlateYdNew(
			JDTORecord msgRecord					/* 전문 */
			, JDTORecordSet rsCrnwrkmtl				/* 크레인작업재료 */
			, JDTORecord recCrnSch					/* 크레인스케줄정보 */
			, JDTORecord recWbook					/* 작업예약정보 */
			) throws JDTOException {

		String szMethodName				= "procAidWrkToLocForPlateYdNew";
		String szOperationName			= "보조작업TO위치결정(후판제품)NEW";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		YdStkLocVO		ydStkLocVO		= null;
		
		JDTORecordSet	rsResult		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		
		String szYD_UP_WO_LOC			= null;
		String szYD_UP_WO_LAYER			= null;
		String szYD_DN_WO_LOC			= null;
		String szYD_DN_WO_LAYER			= null;
		String szYD_UP_STK_COL_GP		= null;
		String szYD_UP_STK_BED_NO		= null;
		String szYD_DN_STK_COL_GP		= null;
		String szYD_DN_STK_BED_NO		= null;
		String szYD_SCH_CD				= null;
		
		boolean bUP_UPDT_NEEDED			= false;
		
		String	szSTL_NO				= null;
		String[] szYD_STK_LYR_MTL_STAT	= {"D", "U"};
		
		String	szYD_TO_LOC_GUIDE		= null;
		
		String szYD_CRN_SCH_ID			= null;
		String szYD_EQP_ID				= null;
		
		String szYD_BAY_GP				= null;
		
		String szYD_MTL_W_GP			= null;						//야드재료폭구분
		//String szYD_MTL_T_GP			= null;						//야드재료두께구분
		String szYD_MTL_L_GP			= null;						//야드재료길이구분
		
		String szYD_PILING_CD			= null;						//야드Piling코드
		
		int intYD_EQP_WRK_SH			= 0;						//야드설비작업매수
		int intYD_EQP_WRK_WT			= 0;						//야드설비작업중량
		double dblYD_EQP_WRK_T			= 0;						//야드설비작업총두께
		String szYD_EQP_WRK_MAX_W		= null;						//작업재료 중 최대 폭
		String szYD_EQP_WRK_MAX_L		= null;						//작업재료 중 최대 길이

		JDTORecordSet outResult1 		= null;
		JDTORecord inRecord3 			= null;
		JDTORecord inRecord2 			= null;
		YdCarSchDao ydCarSchDao = new YdCarSchDao();	
		String szFROM_DONG 				= "";
		String szTO_DONG   				= "";
		int intRtnVal					= 0;
		
		YdPlateCommDAO commDao = new YdPlateCommDAO();	
		
		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
		String logId                            = ydUtils.getJDTOLogId(msgRecord, "T"); 
		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
		szLogMsg = "보조작업TO위치결정(후판제품)(" + szMethodName + ") 시작";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------
		
		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		
		szLogMsg = "["+ szOperationName +"] -------------------- 크레인작업재료의 최하단 재료정보 확인 --------------------";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		ydUtils.displayRecord(szOperationName, recPara);
		
		szYD_CRN_SCH_ID      	= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_CRN_SCH_ID");					//크레인스케줄ID
		szYD_EQP_ID      		= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_EQP_ID");						//크레인설비ID
		
		intYD_EQP_WRK_SH      	= ydDaoUtils.paraRecChkNullInt(recPara,"SH_CNT");						//크레인작업재료 총매수
		intYD_EQP_WRK_WT      	= ydDaoUtils.paraRecChkNullInt(recPara,"SUM_MTL_WT");					//크레인작업재료 총중량
		dblYD_EQP_WRK_T      	= ydDaoUtils.paraRecChkNullDouble(recPara,"SUM_MTL_T");					//크레인작업재료 총높이
		
		szYD_EQP_WRK_MAX_W		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_W");						//크레인작업재료 중 최대 폭
		szYD_EQP_WRK_MAX_L		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_L");						//크레인작업재료 중 최대 길이
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 총매수 :" + intYD_EQP_WRK_SH;
		szLogMsg += ", 총중량 :" + intYD_EQP_WRK_WT;
		szLogMsg += ", 총높이  :" + dblYD_EQP_WRK_T;
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		
		szLogMsg = "["+ szOperationName +"] -------------------- 작업예약정보 확인 --------------------";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szYD_TO_LOC_GUIDE		= ydDaoUtils.paraRecChkNull(recWbook, "YD_TO_LOC_GUIDE");				//사용자지정위치
		szYD_SCH_CD				= ydDaoUtils.paraRecChkNull(recWbook, "YD_SCH_CD");						//크레인스케줄코드
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	최하단 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		szSTL_NO	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 시작 ";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		szRtnMsg = DaoManager.getYdStock(recPara, rsResult, 0);
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			return szRtnMsg;
		}
		
		rsResult.first();
		recTemp = rsResult.getRecord();
		
		szYD_MTL_L_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");			//크레인작업 최하단재료의길이구분
		szYD_MTL_W_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_W_GP");			//크레인작업 최하단재료의폭구분
		szYD_PILING_CD 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_PILING_CD");			//크레인작업 최하단재료의Piling코드
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 완료 - 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"], Piling코드["+szYD_PILING_CD+"]";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	권상중이거나 권하중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYD_UP_WO_LOC 		= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");
		szYD_UP_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER");
		
		if( szYD_UP_WO_LOC.equals("") ) {
			
			szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			szRtnMsg = YdCommonUtils.getYdStkLyrWithMtlStat(recPara, rsResult, szYD_STK_LYR_MTL_STAT);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				return szRtnMsg;
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 완료  ";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YD_STK_COL_GP");
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YD_STK_BED_NO");
			szYD_UP_WO_LOC 			= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYD_UP_WO_LAYER 		= recTemp.getFieldString("YD_STK_LYR_NO");
			
			bUP_UPDT_NEEDED			= true;
			
			szLogMsg = "["+ szOperationName +"] 조회된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		}else{
			szYD_UP_STK_COL_GP = szYD_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYD_UP_WO_LOC.substring(6);
			
			szLogMsg = "["+ szOperationName +"] 크레인스케줄에 등록된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		}
		//----------------------------------------------------------------------------------------------------------------------
		
    	szYD_BAY_GP		= szYD_UP_WO_LOC.substring(1, 2);
		
    	//----------------------------------------------------------------------------------------------------------------------
		//	이적스판검색범위
		//----------------------------------------------------------------------------------------------------------------------
		outResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
		inRecord3 	= JDTORecordFactory.getInstance().create();
		inRecord2 	= JDTORecordFactory.getInstance().create();
		
		inRecord3.setField("REPR_CD_GP" ,"TI0021");	//이적위치검색범위
		inRecord3.setField("CD_GP" ,szYD_EQP_ID.substring(5, 6));    // 크레인 호기			

		/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgt*/
		intRtnVal = ydCarSchDao.getYdCarsch(inRecord3, outResult1, 400);
		
		if (intRtnVal > 0) {
			outResult1.first();
			inRecord2 = outResult1.getRecord();
			szFROM_DONG = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, szYD_BAY_GP+"_DONG"),"0101");
			szTO_DONG   = StringHelper.evl(ydDaoUtils.paraRecChkNull(inRecord2, szYD_BAY_GP+"_DONG2"),"0799");
		} else {
			szFROM_DONG = "0101";
			szTO_DONG   = "0799";
		}
		
		JDTORecord recTemp1 	= JDTORecordFactory.getInstance().create();

    	recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("YD_STK_COL_GP", 		szYD_UP_STK_COL_GP);								//권상지시위치 - 적치열
    	recTemp.setField("YD_STK_BED_NO", 		szYD_UP_STK_BED_NO);								//권상지시위치 - 적치베드
    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH));					//크레인작업재료 총매수
    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT));					//크레인작업재료 총중량
    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T));					//크레인작업재료 총높이
    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);										//크레인 스케줄코드
    	recTemp.setField("YD_PILING_CD", 		szYD_PILING_CD);									//크레인작업 최하단재료의 Piling Code
    	recTemp.setField("YD_MTL_L_GP", 		szYD_MTL_L_GP);										//크레인작업 최하단재료의 길이구분
    	recTemp.setField("YD_MTL_W_GP", 		szYD_MTL_W_GP);										//크레인작업 최하단재료의 폭구분
    	recTemp.setField("YD_EQP_ID", 			szYD_EQP_ID);										//크레인설비ID
		recTemp.setField("YD_PILING_CD", 		szYD_PILING_CD);									//크레인작업 최하단재료의 Piling Code
    	recTemp.setField("YD_MTL_L_GP", 		szYD_MTL_L_GP);										//크레인작업 최하단재료의 길이구분
    	recTemp.setField("YD_MTL_W_GP", 		szYD_MTL_W_GP);										//크레인작업 최하단재료의 폭구분
    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE);									//TO위치 GUIDE
    	recTemp.setField("FROM_DONG", 			szFROM_DONG);										//이적스판검색범위 FROM
    	recTemp.setField("TO_DONG", 			szTO_DONG);											//이적스판검색범위 TO
    	recTemp.setField("YD_CRN_SCH_ID", 		szYD_CRN_SCH_ID);									//크레인스케줄ID
    	recTemp.setField("STL_NO_BOTTOM", 		szSTL_NO);											//최하단재료번호
    	recTemp.setField("LOG_ID"		, 		logId);		
    	
    	
    	String schGp= szYD_SCH_CD.substring(4,6); 
    	String sTmpYdGp = "1";
    	//Y4CrnSchCrnSpecCheckDtl 의 스케줄코드 기준 1,2후판 구분 채용
    	if( "RA".equals(schGp)||
				"RB".equals(schGp)||
				"RC".equals(schGp)||
				"10".equals(schGp)||
				"20".equals(schGp)||
				"21".equals(schGp)||
				"22".equals(schGp)||
				"UT".equals(schGp)||
				"01".equals(schGp)||
				"23".equals(schGp)||
				"00".equals(schGp)){   //21,22, UT 추가
					
    				sTmpYdGp = "2";  //구2후판
				}
			
    	
    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
    	recTemp1.setField("REPR_CD_GP", "T00071");
    	recTemp1.setField("CD_GP", sTmpYdGp + szYD_SCH_CD.substring(1,2) + szYD_SCH_CD.substring(2,4) + szYD_SCH_CD.substring(6,7));
  
    	
    	intRtnVal = commDao.select(recTemp1, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdRuleList");
    	
    	String [] toLocFindOrder;
    	
		//"P" : 파일링코드에 의해 
		//"S" : 혼적베드
		//"G" : 동일사이즈 일반베드
		//"E" : 공베드
		//"T"  : 1후판 가적베드 
		//"A" : 기타 
		//"X" : 위치 못찾음
    	String plnLocDcsnGp  = "X";  //예정위치결정구분  (파일링코드에 의해, 동일사이즈 베드, 공베드, 위치 못찾음) 
    	
    	if (intRtnVal > 0) {
    		toLocFindOrder = new String[intRtnVal];
    		for(int i=0; i<intRtnVal; i++){
    			rsResult.absolute(i+1);
    			recTemp1			= rsResult.getRecord();
    			toLocFindOrder[i] = recTemp1.getFieldString("ITEM");
    			
    			szLogMsg = "["+ szOperationName +"] 스케줄코드 ["+ szYD_SCH_CD +"]의 ["+ Integer.toString(i+1) +"] 번째 검색순서["+ toLocFindOrder[i] +"] ";
    			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    		}
		}
    	//기준에 없는경우 디폴트 순서인 동일파일링 + 혼적베드 + 공베드
    	else{
    		szLogMsg = "["+ szOperationName +"] 스케줄코드 ["+ szYD_SCH_CD +"]의 순서기준 없는경우 파일링-혼적-공베드 순 검색";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
    		intRtnVal = 4;
    		toLocFindOrder = new String[intRtnVal];
    		toLocFindOrder[0] = "SAME";
    		toLocFindOrder[1] = "SIMIL";
    		toLocFindOrder[2] = "EMPTY";
    		toLocFindOrder[3] = "SINGL";
    	}
    	
    	for(int i=0; i<intRtnVal; i++){
    		ydStkLocVO = callFunctionGetBed(toLocFindOrder[i] , recTemp);
    		
    		if(ydStkLocVO != null) {
    			plnLocDcsnGp = ydStkLocVO.getPlnLocDcsnGp();
    			
    			String findMethod = "";
    			if("P".equals(plnLocDcsnGp)){
    				findMethod = "동일파일링코드";
    			}
    			else if("S".equals(plnLocDcsnGp)){
    				findMethod = "혼적베드";
    			}
    			else if("E".equals(plnLocDcsnGp)){
    				findMethod = "공베드";
    			}
    			else if("I".equals(plnLocDcsnGp)){
    				findMethod = "단일파일링";
    			}
    			szLogMsg = "["+ szOperationName +"] To위치 찾기 성공 ["+ findMethod +"]";
    			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    			break;
    		}
    	}
   
		if( ydStkLocVO == null ) {
			
			szLogMsg = "["+ szOperationName +"] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			return YdConstant.RETN_NOT_EXIST_BED;
		}
		
		
		updCrnschToLocFindMethodBySchId(szYD_CRN_SCH_ID,plnLocDcsnGp,logId);
		/*
		 * 권하위치 최종결정정보 셋팅.
		 */
		szYD_DN_STK_COL_GP 	= ydStkLocVO.getYdStkColGp();
		szYD_DN_STK_BED_NO 	= ydStkLocVO.getYdStkBedNo();
		szYD_DN_WO_LOC		= szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;
		szYD_DN_WO_LAYER 	= ydStkLocVO.getYdStkLyrNo();
		
		
		//----------------------------------------------------------------------------------------------------------------------
		// 권하지시위치 수정
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 시작";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		JDTORecord recUpCrnSch	= null;
		
		recUpCrnSch = JDTORecordFactory.getInstance().create();
		recUpCrnSch.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);										//크레인스케줄ID
		recUpCrnSch.setField("YD_EQP_ID", 				szYD_EQP_ID);											//크레인설비ID
		
		recUpCrnSch.setField("YD_UP_STK_COL_GP", 		szYD_UP_STK_COL_GP);									//권상지시위치 - 적치열
		recUpCrnSch.setField("YD_UP_STK_BED_NO", 		szYD_UP_STK_BED_NO);									//권상지시위치 - 적치베드
		recUpCrnSch.setField("YD_DN_STK_COL_GP", 		szYD_DN_STK_COL_GP);									//권하지시위치 - 적치열
		recUpCrnSch.setField("YD_DN_STK_BED_NO", 		szYD_DN_STK_BED_NO);									//권하지시위치 - 적치베드
		
		//----------------------------------------------------------------------------------------------------------------------
		//	권상지시위치 업데이트
		//----------------------------------------------------------------------------------------------------------------------
		if( bUP_UPDT_NEEDED ) {
			szLogMsg = "["+ szOperationName +"] 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"] 크레인스케줄에 등록";
    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			recUpCrnSch.setField("YD_UP_WO_LOC", 		szYD_UP_WO_LOC);										//권상지시위치
			recUpCrnSch.setField("YD_UP_WO_LAYER", 		szYD_UP_WO_LAYER);										//권상지시단
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		recUpCrnSch.setField("YD_DN_WO_LOC", 			szYD_DN_WO_LOC);										//권하지시위치
		recUpCrnSch.setField("YD_DN_WO_LAYER", 			szYD_DN_WO_LAYER);										//권하지시단
		recUpCrnSch.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));						//크레인작업재료 총매수
		recUpCrnSch.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));						//크레인작업재료 총중량
		recUpCrnSch.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));						//크레인작업재료 총높이
		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);									//크레인작업재료 중 최대 폭
		recUpCrnSch.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);									//크레인작업재료 중 최대 길이
		recUpCrnSch.setField("MODIFIER", 				szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		
		szRtnMsg = CrnSchUtil.uptCrnSchXYCord(recUpCrnSch);
    	
    	szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
    	
		//----------------------------------------------------------------------------------------------------------------------
		//	권하지시위치에 재료를 권하대기로 등록
		//----------------------------------------------------------------------------------------------------------------------
    	szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]에 크레인작업재료 등록 시작";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szRtnMsg	= uptDnWaitOnYdStkLyr(rsCrnwrkmtl, szYD_DN_WO_LOC, szYD_DN_WO_LAYER);
    	
		szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]에 크레인작업재료 등록 완료 - 메세지 : " + szRtnMsg;
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "보조작업TO위치결정(후판제품)(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**------------------------------------------------------------------------------------------------------------------------------------
	 * ------------------------------------------------------------------------------------------------------------------------------------
	 * 후판제품 TO위치 결정 모듈 끝
	 * ------------------------------------------------------------------------------------------------------------------------------------
	 * ------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	/**
	 * 가열로보급TO위치결정(A후판슬라브)
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String  procRefurSupplyToLocForAPlateSlab(
			JDTORecordSet rsCrnwrkmtl				/* 크레인작업재료 */
			, JDTORecord recCrnSch					/* 크레인스케줄정보 */
			, JDTORecord recWbook					/* 작업예약정보 */
			) throws JDTOException {
		/*
		 * 업무기준	:	1. 
		 * 
		 * 등록자	:	임춘수
		 * 등록일	:	2010.01.15
		 * 수정일	:	1)
		 * 				
		 */
		String szMethodName				= "procRefurSupplyToLocForAPlateSlab";
		String szOperationName			= "가열로보급TO위치결정(A후판슬라브)";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		JDTORecord		recInOutPara	= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		JDTORecordSet	rsResult		= null;
		
		ArrayList		listToLoc		= null;
		YdStkLocVO		ydStkLocVO		= null;
		
		String szYD_TO_LOC_GUIDE		= null;								//야드To위치Guide
		String szYD_SCH_CD				= null;								//야드스케쥴코드
		
		String szYD_CRN_SCH_ID			= null;
		String szYD_EQP_ID				= null;
		int intYD_EQP_WRK_SH			= 0;
		int intYD_EQP_WRK_WT			= 0;
		double dblYD_EQP_WRK_T			= 0;
		String szYD_EQP_WRK_MAX_W		= null;
		String szYD_EQP_WRK_MAX_L		= null;
		String szYD_UP_WO_LOC			= null;								//권상지시위치
		String szYD_UP_WO_LAYER			= null;
		String szSTL_NO					= null;
		String szREFUR_CHG_PLN_SERNO	= null;								//가열로장입예정일련번호
		String szYD_ROUTE_GP			= null;
		
		String szYD_UP_STK_COL_GP		= null;
		String szYD_UP_STK_BED_NO		= null;
		String szYD_DN_STK_COL_GP		= null;
		String szYD_DN_STK_BED_NO		= null;
		String szYD_DN_WO_LAYER			= null;
		
		boolean bUP_UPDT_NEEDED			= false;
		boolean bUPDATABLE				= false;
		String szREFUR_CHG_PLN_SERNO_CMPR	= null;
		
		String[] szYD_STK_LYR_MTL_STAT	= {"D", "U"};
		
		szLogMsg = "["+ szOperationName +"] ------------------------------- 메소드 시작 -------------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------
		
		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		
		szLogMsg = "["+ szOperationName +"] -------------------- 크레인작업재료의 최하단 재료정보 확인 --------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		szYD_CRN_SCH_ID      	= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_CRN_SCH_ID");					//크레인스케줄ID
		szYD_EQP_ID      		= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_EQP_ID");						//크레인설비ID
		
		szYD_UP_WO_LOC 			= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");
		
		intYD_EQP_WRK_SH      	= ydDaoUtils.paraRecChkNullInt(recPara,"SH_CNT");						//크레인작업재료 총매수
		intYD_EQP_WRK_WT      	= ydDaoUtils.paraRecChkNullInt(recPara,"SUM_MTL_WT");					//크레인작업재료 총중량
		dblYD_EQP_WRK_T      	= ydDaoUtils.paraRecChkNullDouble(recPara,"SUM_MTL_T");					//크레인작업재료 총높이
		
		szYD_EQP_WRK_MAX_W		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_W");						//크레인작업재료 중 최대 폭
		szYD_EQP_WRK_MAX_L		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_L");						//크레인작업재료 중 최대 길이
		
		szSTL_NO				= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");							//크레인작업재료의 최하단 재료번호
		szREFUR_CHG_PLN_SERNO	= ydDaoUtils.paraRecChkNull(recPara, "REFUR_CHG_PLN_SERNO");			//크레인작업재료의 최하단 재료번호의 가열로장입예정일련번호
		szYD_ROUTE_GP			= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_RT_GP");					//목표행선
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 총매수 :" + intYD_EQP_WRK_SH;
		szLogMsg += ", 총중량 :" + intYD_EQP_WRK_WT;
		szLogMsg += ", 총높이  :" + dblYD_EQP_WRK_T;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		
		szYD_TO_LOC_GUIDE		= ydDaoUtils.paraRecChkNull(recWbook, "YD_TO_LOC_GUIDE");				//야드To위치Guide
		szYD_SCH_CD				= ydDaoUtils.paraRecChkNull(recWbook, "YD_SCH_CD");						//야드스케쥴코드
		
		
		
		szLogMsg = "["+ szOperationName +"] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"], 스케쥴코드["+szYD_SCH_CD+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	권상지시위치가 존재하지 않는 경우 권하중이거나 권상중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYD_UP_WO_LOC 		= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");		
		szYD_UP_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER");
		//szSTL_NO	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
		
		if( szYD_UP_WO_LOC.equals("") ) {
			szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			szRtnMsg = YdCommonUtils.getYdStkLyrWithMtlStat(recPara, rsResult, szYD_STK_LYR_MTL_STAT);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				return szRtnMsg;
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 완료  ";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YD_STK_COL_GP");
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YD_STK_BED_NO");
			szYD_UP_WO_LOC 			= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYD_UP_WO_LAYER 		= recTemp.getFieldString("YD_STK_LYR_NO");
			
			bUP_UPDT_NEEDED			= true;
			
			szLogMsg = "["+ szOperationName +"] 조회된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}else{
			szYD_UP_STK_COL_GP = szYD_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYD_UP_WO_LOC.substring(6);
			
			szLogMsg = "["+ szOperationName +"] 크레인스케줄에 등록된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}
		
		//----------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------
		//	사용자지정위치가 존재하는 지 체크
		//	사용자지정위치가 존재하면 해당 Pickup Bed로 TO위치 결정
		//----------------------------------------------------------------------------------------------------
		
		listToLoc		= new ArrayList();
		
		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
			szYD_DN_STK_COL_GP		= szYD_TO_LOC_GUIDE.substring(0, 6);
			szYD_DN_STK_BED_NO		= szYD_TO_LOC_GUIDE.substring(6);
			
			recTemp = JDTORecordFactory.getInstance().create();
	    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE);
	    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH));						//크레인작업재료 총매수
	    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT));						//크레인작업재료 총중량
	    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T));						//크레인작업재료 총높이
	    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);
	    	szRtnMsg = YdToLocDcsnUtil.procAsgnedBedStackable(recTemp, listToLoc, szMethodName);
			
	    	if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    		ydStkLocVO = (YdStkLocVO)listToLoc.get(0);
	    		ydStkLocVO.setSeq(YdConstant.TO_LOC_PRIOR_USER);
	    		ydStkLocVO.setPrior(YdConstant.TO_LOC_PRIOR_USER);
			
	    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE) {
	    			//----------------------------------------------------------------------------------------------------
	    			//	해당베드가 적치가능해도 크레인작업재료중 최하단 재료의 가열로장입예정일련번호 
	    			//----------------------------------------------------------------------------------------------------
	    			if( ydStkLocVO.getYdStkLyrNo().equals("001")) {
	    				//----------------------------------------------------------------------------------------------------
	    				//	공베드인 경우에는 무조건 적치가능함
	    				//----------------------------------------------------------------------------------------------------
	    				
	    				bUPDATABLE			= true;
	    				
	    				szLogMsg = "["+ szOperationName +"] 사용자지정위치["+szYD_TO_LOC_GUIDE+"]가 공베드이므로 적치가능";
	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    				
	    			}else{
	    				//----------------------------------------------------------------------------------------------------
	    				//	공베드가 아닌 경우에는 해당베드의 권하대기이거나 적치중인 최상단 재료의 가열로장입예정일련번호와 
	    				//	크레인작업재료중 최하단 재료의 가열로장입예정일련번호를 비교 후 크레인작업재료중 최하단 재료의 가열로장입예정일련번호가
	    				//	빠르면 적치가능
	    				//----------------------------------------------------------------------------------------------------
	    				recInOutPara		= JDTORecordFactory.getInstance().create();
	    				recInOutPara.setField("STL_NO", 				szSTL_NO);
	    				recInOutPara.setField("REFUR_CHG_PLN_SERNO", 	szREFUR_CHG_PLN_SERNO);
	    				recInOutPara.setField("YD_STK_COL_GP", 			szYD_DN_STK_COL_GP);
	    				recInOutPara.setField("YD_STK_BED_NO", 			szYD_DN_STK_BED_NO);
	    				
	    				szLogMsg = "["+ szOperationName +"] 크레인작업재료중 최하단 재료["+szSTL_NO+"]의 가열로장입예정일련번호["+szREFUR_CHG_PLN_SERNO+"]를 사용자지정위치의 적치중인 재료와 비교 시작";
	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    				
	    				szRtnMsg		= compareRefurChgPlnSernoWithStlAtBed(recInOutPara);
	    				
	    				szLogMsg = "["+ szOperationName +"] 크레인작업재료중 최하단 재료["+szSTL_NO+"]의 가열로장입예정일련번호["+szREFUR_CHG_PLN_SERNO+"]를 사용자지정위치의 적치중인 재료와 비교 완료 - 메세지 : " + szRtnMsg;
	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    				
	    				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    					szREFUR_CHG_PLN_SERNO_CMPR		= ydDaoUtils.paraRecChkNull(recInOutPara, "REFUR_CHG_PLN_SERNO_CMPR");
	    					
	    					if( !szREFUR_CHG_PLN_SERNO_CMPR.equals(YdConstant.REFUR_CHG_PLN_SERNO_BIG) ) {
	    						
	    						//----------------------------------------------------------------------------------------------------
	    						//	적치가능
	    						//----------------------------------------------------------------------------------------------------
	    						
	    						bUPDATABLE			= true;
	    						
	    						szLogMsg = "["+ szOperationName +"] 사용자지정위치["+szYD_TO_LOC_GUIDE+"]의 적치중인 재료의 가열로장입예정일련번호가 큰 수이므로 적치가능";
	    	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    					}
	    				}else{
	    					//----------------------------------------------------------------------------------------------------
	    					//	적치불가능
	    					//----------------------------------------------------------------------------------------------------
	    					
	    					szLogMsg = "["+ szOperationName +"] 사용자지정위치["+szYD_TO_LOC_GUIDE+"]의 적치중인 재료의 가열로장입예정일련번호가 같거나 작은 수이므로 적치불가능";
    	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    					
	    				}
	    				//----------------------------------------------------------------------------------------------------
	    			}
	    			
	    			if( bUPDATABLE ) {
	    				//----------------------------------------------------------------------------------------------------
						//	크레인스케줄 권하위치정보및 좌표 값 수정
						//----------------------------------------------------------------------------------------------------
						
	    				szYD_DN_WO_LAYER				= ydStkLocVO.getYdStkLyrNo();
	    				
						recTemp = JDTORecordFactory.getInstance().create();
				    	recTemp.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);
				    	recTemp.setField("YD_EQP_ID", 				szYD_EQP_ID);
				    	recTemp.setField("YD_UP_STK_COL_GP", 		szYD_UP_STK_COL_GP);
				    	recTemp.setField("YD_UP_STK_BED_NO", 		szYD_UP_STK_BED_NO);
				    	recTemp.setField("YD_DN_STK_COL_GP", 		szYD_DN_STK_COL_GP);
				    	recTemp.setField("YD_DN_STK_BED_NO", 		szYD_DN_STK_BED_NO);
				    	if( bUP_UPDT_NEEDED ) {
				    		recTemp.setField("YD_UP_WO_LOC", 			szYD_UP_WO_LOC);
				    		recTemp.setField("YD_UP_WO_LAYER", 			szYD_UP_WO_LAYER);
				    	}
				    	recTemp.setField("YD_DN_WO_LOC", 			szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO);
				    	recTemp.setField("YD_DN_WO_LAYER", 			szYD_DN_WO_LAYER);
				    	recTemp.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));
				    	recTemp.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));
				    	recTemp.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));
				    	recTemp.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);
				    	recTemp.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);
				    	
				    	szLogMsg = "["+ szOperationName +"] 크레인스케줄 수정 시작";
	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				    	
				    	szRtnMsg		= CrnSchUtil.uptCrnSchXYCord(recTemp);
				    	
				    	szLogMsg = "["+ szOperationName +"] 크레인스케줄 수정 완료 - 메세지 : " + szRtnMsg;
	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				    	
	    				szLogMsg = "["+ szOperationName +"] 권하위치["+szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO+", "+szYD_DN_WO_LAYER+"]에 재료 등록 시작";
	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    				
				    	szRtnMsg		= uptDnWaitOnYdStkLyr(rsCrnwrkmtl, szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO, szYD_DN_WO_LAYER);
						
				    	szLogMsg = "["+ szOperationName +"] 권하위치["+szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO+", "+szYD_DN_WO_LAYER+"]에 재료 등록 완료 - 메세지 : " + szRtnMsg;
	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				    	
				    	return szRtnMsg;
						//----------------------------------------------------------------------------------------------------
	    			}
	    			
	    			//----------------------------------------------------------------------------------------------------
	    		}
	    		
	    	}
			
		}
		
		//----------------------------------------------------------------------------------------------------
		
		
		
		//----------------------------------------------------------------------------------------------------
		//	사용자지정위치가 존재하지 않거나 사용자지정위치로 갈 수 없는 경우에는 위치검색베드를 사용해서 TO위치결정
		//----------------------------------------------------------------------------------------------------
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);
    	recTemp.setField("YD_ROUTE_GP", 		szYD_ROUTE_GP);
		
    	szRtnMsg			= DaoManager.getYdLocsrchrng(recTemp, rsResult, 1);
    	
    	if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
    		
    		szLogMsg = "["+ szOperationName +"] 스케줄코드["+szYD_SCH_CD+"], 목표행선["+szYD_ROUTE_GP+"]에 대한 위치검색베드가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			
    		return szRtnMsg;
    	}
    	
		//----------------------------------------------------------------------------------------------------
		
    	
    	//----------------------------------------------------------------------------------------------------
    	//	조회된 베드에 대한 적치가능 여부 판단
    	//----------------------------------------------------------------------------------------------------
    	bUPDATABLE						= false;
    	//String szYD_STK_COL_GP			= null;
    	//String szYD_STK_BED_NO			= null;
    	
    	for(int i = 1; i <= rsResult.size(); i++  ) {
    		rsResult.absolute(i);
    		recPara			= rsResult.getRecord();
    		
    		szYD_DN_STK_COL_GP			= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
    		szYD_DN_STK_BED_NO			= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
    		
    		szLogMsg = "["+ szOperationName +"] ---------------------- ["+i+"] 위치검색베드[적치열:"+szYD_DN_STK_COL_GP+", 적치베드:"+szYD_DN_STK_BED_NO+"]에 대한 적치가능 여부 체크 ----------------------";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    		
    		if( szYD_TO_LOC_GUIDE.equals(szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO)) {
    			szLogMsg = "["+ szOperationName +"] ["+i+"] 위치검색베드[적치열:"+szYD_DN_STK_COL_GP+", 적치베드:"+szYD_DN_STK_BED_NO+"]가 사용자지정위치["+szYD_TO_LOC_GUIDE+"]와 동일하므로 SKIP시킴";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    			continue;
    		}
    		
    		recTemp = JDTORecordFactory.getInstance().create();
	    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO);
	    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH));						//크레인작업재료 총매수
	    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT));						//크레인작업재료 총중량
	    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T));						//크레인작업재료 총높이
	    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);
	    	
	    	listToLoc		= new ArrayList();
	    	
	    	szRtnMsg = YdToLocDcsnUtil.procAsgnedBedStackable(recTemp, listToLoc, szMethodName);
			
	    	if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    		ydStkLocVO = (YdStkLocVO)listToLoc.get(0);
	    		ydStkLocVO.setSeq(YdConstant.TO_LOC_PRIOR_USER);
	    		ydStkLocVO.setPrior(YdConstant.TO_LOC_PRIOR_USER);
			
	    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE) {
	    			//----------------------------------------------------------------------------------------------------
	    			//	해당베드가 적치가능해도 크레인작업재료중 최하단 재료의 가열로장입예정일련번호 
	    			//----------------------------------------------------------------------------------------------------
	    			if( ydStkLocVO.getYdStkLyrNo().equals("001")) {
	    				//----------------------------------------------------------------------------------------------------
	    				//	공베드인 경우에는 무조건 적치가능함
	    				//----------------------------------------------------------------------------------------------------
	    				
	    				bUPDATABLE			= true;
	    				
	    				szLogMsg = "["+ szOperationName +"] ["+i+"] 위치검색베드[적치열:"+szYD_DN_STK_COL_GP+", 적치베드:"+szYD_DN_STK_BED_NO+"]가 공베드이므로 적치가능";
	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    				
	    			}else{
	    				//----------------------------------------------------------------------------------------------------
	    				//	공베드가 아닌 경우에는 해당베드의 권하대기이거나 적치중인 최상단 재료의 가열로장입예정일련번호와 
	    				//	크레인작업재료중 최하단 재료의 가열로장입예정일련번호를 비교 후 크레인작업재료중 최하단 재료의 가열로장입예정일련번호가
	    				//	빠르면 적치가능
	    				//----------------------------------------------------------------------------------------------------
	    				recInOutPara		= JDTORecordFactory.getInstance().create();
	    				recInOutPara.setField("STL_NO", 				szSTL_NO);
	    				recInOutPara.setField("REFUR_CHG_PLN_SERNO", 	szREFUR_CHG_PLN_SERNO);
	    				recInOutPara.setField("YD_STK_COL_GP", 			szYD_DN_STK_COL_GP);
	    				recInOutPara.setField("YD_STK_BED_NO", 			szYD_DN_STK_BED_NO);
	    				
	    				szLogMsg = "["+ szOperationName +"] ["+i+"] 크레인작업재료중 최하단 재료["+szSTL_NO+"]의 가열로장입예정일련번호["+szREFUR_CHG_PLN_SERNO+"]를 위치검색베드[적치열:"+szYD_DN_STK_COL_GP+", 적치베드:"+szYD_DN_STK_BED_NO+"]의 적치중인 재료와 비교 시작";
	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    				
	    				szRtnMsg		= compareRefurChgPlnSernoWithStlAtBed(recInOutPara);
	    				
	    				szLogMsg = "["+ szOperationName +"] ["+i+"] 크레인작업재료중 최하단 재료["+szSTL_NO+"]의 가열로장입예정일련번호["+szREFUR_CHG_PLN_SERNO+"]를 위치검색베드[적치열:"+szYD_DN_STK_COL_GP+", 적치베드:"+szYD_DN_STK_BED_NO+"]의 적치중인 재료와 비교 완료 - 메세지 : " + szRtnMsg;
	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    				
	    				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    					szREFUR_CHG_PLN_SERNO_CMPR		= ydDaoUtils.paraRecChkNull(recInOutPara, "REFUR_CHG_PLN_SERNO_CMPR");
	    					
	    					if( !szREFUR_CHG_PLN_SERNO_CMPR.equals(YdConstant.REFUR_CHG_PLN_SERNO_BIG) ) {
	    						
	    						//----------------------------------------------------------------------------------------------------
	    						//	적치가능
	    						//----------------------------------------------------------------------------------------------------
	    						
	    						bUPDATABLE			= true;
	    						
	    						szLogMsg = "["+ szOperationName +"] ["+i+"] 위치검색베드[적치열:"+szYD_DN_STK_COL_GP+", 적치베드:"+szYD_DN_STK_BED_NO+"]의 적치중인 재료의 가열로장입예정일련번호가 큰 수이므로 적치가능";
	    	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    					}
	    				}else{
	    					//----------------------------------------------------------------------------------------------------
	    					//	적치불가능
	    					//----------------------------------------------------------------------------------------------------
	    					
	    					szLogMsg = "["+ szOperationName +"] ["+i+"] 위치검색베드[적치열:"+szYD_DN_STK_COL_GP+", 적치베드:"+szYD_DN_STK_BED_NO+"]의 적치중인 재료의 가열로장입예정일련번호가 같거나 작은 수이므로 적치불가능";
    	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    					
	    				}
	    				//----------------------------------------------------------------------------------------------------
	    			}
	    			
	    			if( bUPDATABLE ) {
	    				//----------------------------------------------------------------------------------------------------
						//	크레인스케줄 권하위치정보및 좌표 값 수정
						//----------------------------------------------------------------------------------------------------
						
	    				szYD_DN_WO_LAYER			= ydStkLocVO.getYdStkLyrNo();
	    				
						recTemp = JDTORecordFactory.getInstance().create();
				    	recTemp.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);
				    	recTemp.setField("YD_EQP_ID", 				szYD_EQP_ID);
				    	recTemp.setField("YD_UP_STK_COL_GP", 		szYD_UP_STK_COL_GP);
				    	recTemp.setField("YD_UP_STK_BED_NO", 		szYD_UP_STK_BED_NO);
				    	recTemp.setField("YD_DN_STK_COL_GP", 		szYD_DN_STK_COL_GP);
				    	recTemp.setField("YD_DN_STK_BED_NO", 		szYD_DN_STK_BED_NO);
				    	if( bUP_UPDT_NEEDED ) {
				    		recTemp.setField("YD_UP_WO_LOC", 			szYD_UP_WO_LOC);
				    		recTemp.setField("YD_UP_WO_LAYER", 			szYD_UP_WO_LAYER);
				    	}
				    	recTemp.setField("YD_DN_WO_LOC", 			szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO);
				    	recTemp.setField("YD_DN_WO_LAYER", 			szYD_DN_WO_LAYER);
				    	recTemp.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));
				    	recTemp.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));
				    	recTemp.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));
				    	recTemp.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);
				    	recTemp.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);
				    	
				    	szLogMsg = "["+ szOperationName +"] 크레인스케줄 수정 시작";
	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				    	
				    	szRtnMsg		= CrnSchUtil.uptCrnSchXYCord(recTemp);
				    	
				    	szLogMsg = "["+ szOperationName +"] 크레인스케줄 수정 완료 - 메세지 : " + szRtnMsg;
	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				    	
				    	
				    	szLogMsg = "["+ szOperationName +"] 권하위치["+szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO+", "+szYD_DN_WO_LAYER+"]에 재료 등록 시작";
	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    				
				    	szRtnMsg		= uptDnWaitOnYdStkLyr(rsCrnwrkmtl, szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO, szYD_DN_WO_LAYER);
						
				    	szLogMsg = "["+ szOperationName +"] 권하위치["+szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO+", "+szYD_DN_WO_LAYER+"]에 재료 등록 완료 - 메세지 : " + szRtnMsg;
	    				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
				    	return szRtnMsg;
						//----------------------------------------------------------------------------------------------------
	    			}
	    			
	    			//----------------------------------------------------------------------------------------------------
	    		}
	    		
	    	}
	    	
    	}
    	
    	//----------------------------------------------------------------------------------------------------
    	
		szLogMsg = "["+ szOperationName +"] ------------------------------- 메소드 끝 -------------------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 가열로장입예정일련번호비교
	 * @param recInOutPara
	 * @return
	 * @throws JDTOException
	 */
	public static String compareRefurChgPlnSernoWithStlAtBed(JDTORecord	recInOutPara) throws JDTOException {
		/*
		 * 업무기준	:	1. 파라미터로 넘겨진 재료의 가열로장입예정일련번호와 
		 * 				해당베드에 권하대기이거나 적치된 최상단재료의 가열로장입예정일련번호를 비교 후 반환
		 * 
		 * 등록자	:	임춘수
		 * 등록일	:	2010.01.15
		 * 수정일	:	1)
		 * 				
		 */
		String szMethodName				= "compareRefurChgPlnSernoWithStlAtBed";
		String szOperationName			= "베드상단재료-가열로장입예정일련번호비교";
		String szLogMsg					= null;
		String szRtnMsg 				= null;
		
		JDTORecord			recPara		= null;
		JDTORecordSet		rsResult	= null;
		
		String szSTL_NO					= null;
		String szREFUR_CHG_PLN_SERNO	= null;
		int intREFUR_CHG_PLN_SERNO		= 0;
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_STK_LYR_MTL_STAT	= null;
		
		String szUPPER_STL_NO				= null;
		String szUPPER_REFUR_CHG_PLN_SERNO	= null;
		int intUPPER_REFUR_CHG_PLN_SERNO	= 0;
		
		String szREFUR_CHG_PLN_SERNO_CMPR	= null;
		
		szSTL_NO				= ydDaoUtils.paraRecChkNull(recInOutPara, "STL_NO");
		szREFUR_CHG_PLN_SERNO	= ydDaoUtils.paraRecChkNull(recInOutPara, "REFUR_CHG_PLN_SERNO");
		szYD_STK_COL_GP			= ydDaoUtils.paraRecChkNull(recInOutPara, "YD_STK_COL_GP");
		szYD_STK_BED_NO			= ydDaoUtils.paraRecChkNull(recInOutPara, "YD_STK_BED_NO");
		
		//----------------------------------------------------------------------------------------------------
		//	해당베드에 권하대기이거나 적치중인 재료 조회
		//----------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 해당베드 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]에 권하대기이거나 적치중인 재료 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		rsResult 		= JDTORecordFactory.getInstance().createRecordSet("");
		recPara			= JDTORecordFactory.getInstance().create();
		
		recPara.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
	
		szRtnMsg		= DaoManager.getYdStock(recPara, rsResult, 183);
		
		szLogMsg = "["+ szOperationName +"] 해당베드 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]에 권하대기이거나 적치중인 재료 조회 완료 - 메세지 : " + szRtnMsg + ", 대상재건수 : " + rsResult.size();
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			return szRtnMsg;
		}
		
		//----------------------------------------------------------------------------------------------------
		
		rsResult.first();
		recPara			= rsResult.getRecord();
		
		szUPPER_STL_NO				= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
		szUPPER_REFUR_CHG_PLN_SERNO	= ydDaoUtils.paraRecChkNull(recPara, "REFUR_CHG_PLN_SERNO");
		szYD_STK_LYR_MTL_STAT		= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT");
		
		szLogMsg = "["+ szOperationName +"] 재료["+szSTL_NO+"]의 가열로장입예정일련번호["+szREFUR_CHG_PLN_SERNO+"]와 ";
		szLogMsg += "해당베드 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]에 상태가 ["+szYD_STK_LYR_MTL_STAT+"]인 최상단재료["+szUPPER_STL_NO+"]의 가열로장입예정일련번호["+szUPPER_REFUR_CHG_PLN_SERNO+"]를 비교 시작";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		intUPPER_REFUR_CHG_PLN_SERNO		= Integer.parseInt(szUPPER_REFUR_CHG_PLN_SERNO);
		
		intREFUR_CHG_PLN_SERNO				= Integer.parseInt(szREFUR_CHG_PLN_SERNO); 
		
		if( intREFUR_CHG_PLN_SERNO > intUPPER_REFUR_CHG_PLN_SERNO ) {
			szREFUR_CHG_PLN_SERNO_CMPR		= YdConstant.REFUR_CHG_PLN_SERNO_BIG;
		}else if( intREFUR_CHG_PLN_SERNO < intUPPER_REFUR_CHG_PLN_SERNO ) {
			szREFUR_CHG_PLN_SERNO_CMPR		= YdConstant.REFUR_CHG_PLN_SERNO_SMALL;
		}else{
			szREFUR_CHG_PLN_SERNO_CMPR		= YdConstant.REFUR_CHG_PLN_SERNO_EQUAL;
		}
		
		recInOutPara.setField("REFUR_CHG_PLN_SERNO_CMPR", szREFUR_CHG_PLN_SERNO_CMPR);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	

	/**
	 * 권하위치재료등록
	 * @param rsCrnwrkmtl
	 * @param szYD_DN_WO_LOC
	 * @param szYD_DN_WO_LAYER
	 * @return
	 * @throws JDTOException
	 */
	public static String uptDnWaitOnYdStkLyr(
			JDTORecordSet rsCrnwrkmtl				/* 크레인작업재료 */
			, String szYD_DN_WO_LOC
			, String szYD_DN_WO_LAYER
			) throws JDTOException {
		String szMethodName			= "";
		String szOperationName		= "권하위치재료등록";
		String szLogMsg				= null;
		String szRtnMsg				= null;
		String szYD_STK_COL_GP		= szYD_DN_WO_LOC.substring(0, 6);
		String szYD_STK_BED_NO		= szYD_DN_WO_LOC.substring(6);
		String szYD_STK_LYR_NO		= "";
		String szSTL_NO				= null;
		JDTORecord		recPara		= null;
		JDTORecord		recTemp		= null;
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 크레인작업재료 건수["+rsCrnwrkmtl.size()+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		recPara		=  JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
		for(int i = 1; i <= rsCrnwrkmtl.size(); i++ ) {
			rsCrnwrkmtl.absolute(i);
			recTemp		= rsCrnwrkmtl.getRecord();
			
			szSTL_NO 			= ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
			
			szYD_STK_LYR_NO		= ydDaoUtils.stringPlusInt(szYD_DN_WO_LAYER, (i - 1) );
			
			szLogMsg = "["+ szOperationName +"] ["+i+"] 크레인작업재료["+szSTL_NO+"] - 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"] 권하대기로 등록 시작 ";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			recPara.setField("STL_NO", 					szSTL_NO);
			recPara.setField("YD_STK_LYR_NO", 			szYD_STK_LYR_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT", 	YdConstant.YD_STK_LYR_MTL_STAT_DN_WAIT);
			
			szRtnMsg	= DaoManager.updYdStklyr(recPara, 0);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szLogMsg = "["+ szOperationName +"] ["+i+"] 크레인작업재료["+szSTL_NO+"] - 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"] 권하대기로 등록 시 오류발생 - " + szRtnMsg ;
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				return szRtnMsg;
			}
			
			szLogMsg = "["+ szOperationName +"] ["+i+"] 크레인작업재료["+szSTL_NO+"] - 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"] 권하대기로 등록 완료 - " + szRtnMsg ;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	

	/**
	 * 동일한PilingCode베드검색
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getBedWithSamePilingCd(
			JDTORecord recInPara
			, List listToLoc) throws JDTOException {
		/*
		 * 최상단 재료의 Piling Code와 동일한 Piling Code를 가진 베드 검색
		 * szYD_PILING_CD : Piling Code
		 * szYD_STK_BED_NO : 권상지시위치의 베드 번지
		 * szSPAN_ORDER : 검색할 스판의 순서
		 * 		1) 1, 2, 3, 4 - 해당 스판만 검색
		 * 		2) 1234 - 04, 05, 06스판을 먼저 검색 후 07 스판을 검색
		 * 		3) 3412 - 07스판을 먼저 검색 후 04, 05, 06 스판을 검색
		 * szSCAN_DIR	: 검색할 방향
		 * 		1) R2P : RT -> PT(차량)방향
		 * 		2) P2R : PT(차량) -> RT방향
		 * 
		 */
		String szMethodName				= "getBedWithSamePilingCd";
		String szOperationName			= "동일한PilingCode베드검색";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		String szYD_TO_LOC_GUIDE		= null;
		String szYD_PILING_CD			= null;
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		
		String szSPAN_ORDER				= null;
		String szSCAN_DIR				= null;
		
		String szALT_FLAG               = null;
		String szAL_FROM                = null;
		String szAL_TO                  = null;
		
		JDTORecord		recPara			= null;
		JDTORecord		recOut			= null;
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "동일한PilingCode베드검색(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		ydUtils.displayRecord(szOperationName, recInPara);
		
		szYD_TO_LOC_GUIDE 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");						//사용자지정위치(권하지시위치)
		szYD_PILING_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_PILING_CD");							//크레인작업 최하단재료의 Piling Code
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시위치 - 적치베드
		szSPAN_ORDER 		= ydDaoUtils.paraRecChkNull(recInPara, "SPAN_ORDER");							//스판검색범위
		szSCAN_DIR 			= ydDaoUtils.paraRecChkNull(recInPara, "SCAN_DIR");								//검색방향
		
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		
		szALT_FLAG          = ydDaoUtils.paraRecChkNull(recInPara, "ALT_FLAG");							//대체크레인 범위 추가 필터 사용여부 FLAG
		szAL_FROM           = ydDaoUtils.paraRecChkNull(recInPara, "AL_FROM");							//대체범위 FROM
		szAL_TO             = ydDaoUtils.paraRecChkNull(recInPara, "AL_TO");							//대체범위 TO
		
		//----------------------------------------------------------------------------------------------------------------------
		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
			
			szYD_GP			= szYD_TO_LOC_GUIDE.substring(0, 1);
			szYD_BAY_GP		= szYD_TO_LOC_GUIDE.substring(1, 2);
			
		}else{
			//----------------------------------------------------------------------------------------------------------------------
			//	사용자지정위치(입고예정위치)가 존재하지 않는 경우에는 업무종료
			//	==>권상지시위치를 사용. : 2009.12.22
			//----------------------------------------------------------------------------------------------------------------------
			szYD_GP			= szYD_STK_COL_GP.substring(0, 1);
			szYD_BAY_GP		= szYD_STK_COL_GP.substring(1, 2);
			
			szLogMsg = "["+ szOperationName +"] 사용자지정위치(입고예정위치)가 존재하지 않으므로 권상지시위치를 사용";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
		}
		//SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_PILING_CD
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
		recPara.setField("YD_PILING_CD", 	szYD_PILING_CD);
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	스판검색범위와 검색정렬방법을 정의
		//----------------------------------------------------------------------------------------------------------------------
		recOut = JDTORecordFactory.getInstance().create();
		setSpanSearchGpNColOrder(szSPAN_ORDER, szSCAN_DIR, recOut);
		
		String[] szSPAN_SEARCH_GP		= (String[])recOut.getField("SPAN_SEARCH_GP");
		String[] szCOL_ORDER			= (String[])recOut.getField("COL_ORDER");
		
		//---------------------------------------------------------------------------------------------------------
		// 크레인사양 조회
		// 설비ID로  크크레인 Grab 구분 추출
		//---------------------------------------------------------------------------------------------------------
		String szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID");
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, "To위치결정[크레인GRAB구분확인]"+szYD_EQP_ID, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, "To위치결정[크레인GRAB구분확인]"+szYD_EQP_ID, YdConstant.DEBUG, logId);
		
		JDTORecord recTemp 		= JDTORecordFactory.getInstance().create();
		JDTORecord recSpec 		= JDTORecordFactory.getInstance().create();
		JDTORecordSet specSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recTemp.setField("YD_EQP_ID", szYD_EQP_ID);
		
		szRtnMsg	= DaoManager.getYdCrnspec(recTemp, specSet, 0);
		
		specSet.first();
		recSpec = specSet.getRecord();
		
		String szYD_CRN_GRAB_TP = ydDaoUtils.paraRecChkNull(recSpec, "YD_CRN_GRAB_TP"); // 크레인 Grab 구분
		
		if("X".equals(szYD_CRN_GRAB_TP)){
			if( szYD_STK_BED_NO.equals("") ) {
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
			}else if(szYD_STK_BED_NO.equals("01")){
				// 01, 02번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"2");
			}else if(szYD_STK_BED_NO.equals("03")){
				// 02, 03번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"3");
			}
		}else{
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 Piling Code를 가진 최상단재료가 있는 모든 베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 동일한 Piling Code["+szYD_PILING_CD+"]를 가진 최상단재료가 있는 모든 베드 정보 조회 시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		for(int i = 0 ; i < szSPAN_SEARCH_GP.length; i++ ) {
			
			recPara.setField("SPAN_SEARCH_GP", 	szSPAN_SEARCH_GP[i]);
			recPara.setField("COL_ORDER", 		szCOL_ORDER[i]);
			recPara.setField("LOOP_I", 			String.valueOf(i + 1));
			
			recPara.setField("ALT_FLAG",  szALT_FLAG);
			recPara.setField("AL_FROM" ,  szAL_FROM);
			recPara.setField("AL_TO"   ,  szAL_TO);
			//------------------------------------------------------------------
			//	사용자정의위치(TO위치가이드)를 제거하지 않고 조회
			//	필요한 경우에는 쿼리에서 해당위치를 제거하거나 소스를 수정 필요 검토 -> 현 로직에서는 문제없음
			//	수정자 : 임춘수
			//	수정일 : 2010.01.04
			//------------------------------------------------------------------
			
			srchBedWithSamePilingCd(recPara, listToLoc);
			
			//------------------------------------------------------------------
		}
		
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 동일한 Piling Code["+szYD_PILING_CD+"]를 가진 최상단재료가 있는 모든 베드 정보 조회 완료 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "동일한PilingCode베드검색(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	

	
	/**
	 * 동일한PilingCode베드검색(보조작업)
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getBedWithSamePilingCdForAidWrk(
			JDTORecord recInPara
			, List listToLoc) throws JDTOException {
		/*
		 * 최상단 재료의 Piling Code와 동일한 Piling Code를 가진 베드 검색
		 * szYD_PILING_CD : Piling Code
		 * szYD_STK_BED_NO : 권상지시위치의 베드 번지
		 * szSPAN_ORDER : 검색할 스판의 순서
		 * 		1) 1, 2, 3, 4 - 해당 스판만 검색
		 * 		2) 1234 - 04, 05, 06스판을 먼저 검색 후 07 스판을 검색
		 * 		3) 3412 - 07스판을 먼저 검색 후 04, 05, 06 스판을 검색
		 * szSCAN_DIR	: 검색할 방향
		 * 		1) R2P : RT -> PT(차량)방향
		 * 		2) P2R : PT(차량) -> RT방향
		 * 
		 */
		String szMethodName				= "getBedWithSamePilingCdForAidWrk";
		String szOperationName			= "동일한PilingCode베드검색(보조작업)";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		//String szYD_TO_LOC_GUIDE		= null;
		String szYD_PILING_CD			= null;
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		
		String szSPAN_ORDER				= null;
		String szSCAN_DIR				= null;
		
		JDTORecord		recPara			= null;
		JDTORecord		recOut			= null;
		JDTORecordSet	rsResult		= null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		szYD_PILING_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_PILING_CD");						//크레인작업 최하단 재료의 Piling Code
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");					//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");					//권상지시베드
		
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");					//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");					//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");						//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");						//크레인스케줄코드
		
		szYD_GP				= szYD_STK_COL_GP.substring(0, 1);
		szYD_BAY_GP			= szYD_STK_COL_GP.substring(1, 2);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	스판검색범위와 검색정렬방법을 정의
		//----------------------------------------------------------------------------------------------------------------------
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);								//야드구분
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);							//동구분
		recPara.setField("YD_STK_COL_GP", 	szYD_STK_COL_GP);						//권상지시위치 - 적치열
		recPara.setField("YD_STK_BED_NO", 	szYD_STK_BED_NO);						//권상지시베드
		recPara.setField("YD_PILING_CD", 	szYD_PILING_CD);						//크레인작업 최하단 재료의 Piling Code
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);						//크레인작업재료 총매수
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);						//크레인작업재료 총중량
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);						//크레인작업재료 총높이
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);							//크레인스케줄코드
		
		//---------------------------------------------------------------------------------------------------------
		// 크레인사양 조회
		// 설비ID로  크크레인 Grab 구분 추출
		//---------------------------------------------------------------------------------------------------------
		String szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID");
		ydUtils.putLog(szClassName, szMethodName, "To위치결정[크레인GRAB구분확인]"+szYD_EQP_ID, YdConstant.DEBUG);
		
		JDTORecord recTemp 		= JDTORecordFactory.getInstance().create();
		JDTORecord recSpec 		= JDTORecordFactory.getInstance().create();
		JDTORecordSet specSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recTemp.setField("YD_EQP_ID", szYD_EQP_ID);
		
		szRtnMsg	= DaoManager.getYdCrnspec(recTemp, specSet, 0);
		
		specSet.first();
		recSpec = specSet.getRecord();
		
		String szYD_CRN_GRAB_TP = ydDaoUtils.paraRecChkNull(recSpec, "YD_CRN_GRAB_TP"); // 크레인 Grab 구분
		
		if("X".equals(szYD_CRN_GRAB_TP)){
			if( szYD_STK_BED_NO.equals("") ) {
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
			}else if(szYD_STK_BED_NO.equals("01")){
				// 01, 02번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"2");
			}else if(szYD_STK_BED_NO.equals("03")){
				// 02, 03번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"3");
			}
		}else{
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		if( szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
			szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) || 	
			szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06)) {
			
			recPara.setField("SPAN_SEARCH_GP", 	"3"); //스판검색범위(04, 05, 06스판)
			
		}else if( szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
				  szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP)) {
			
			recPara.setField("SPAN_SEARCH_GP", 	"4"); //스판검색범위(07스판)
		}
		
		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 Piling Code를 가진 재료가 있는 모든 베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]를 가진 재료가 있는 모든 베드 정보 조회 시작 ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 105);
		
		szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]를 가진 재료가 있는 모든 베드 정보 조회 완료  - 메세지 : " + szRtnMsg;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		//----------------------------------------------------------------------------------------------------------------------
		
		recPara.setField("LOOP_I", 			"1");
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) && rsResult.size() > 0 ) {
			srchNconvRecord2Vo(	szYD_STK_COL_GP,
								szYD_STK_BED_NO,
								recPara, 
								rsResult, 
								listToLoc, 
								YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD);
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 동일길이/폭구분공베드검색
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getEmptyBedWithSameLWGp(
			JDTORecord recInPara
			, List listToLoc) throws JDTOException {
		/*
		 * 최상단 재료의 Piling Code와 동일한 Piling Code를 가진 베드 검색
		 * szYD_PILING_CD : Piling Code
		 * szYD_STK_BED_NO : 권상지시위치의 베드 번지
		 * szSPAN_ORDER : 검색할 스판의 순서
		 * 		1) 1, 2, 3, 4 - 해당 스판만 검색
		 * 		2) 1234 - 04, 05, 0642스판을 먼저 검색 후 07 스판을 검색
		 * 		3) 3412 - 07스판을 먼저 검색 후 04, 05, 06 스판을 검색
		 * szSCAN_DIR	: 검색할 방향
		 * 		1) R2P : RT -> PT(차량)방향
		 * 		2) P2R : PT(차량) -> RT방향
		 * 
		 */
		String szMethodName				= "getEmptyBedWithSameLWGp";
		String szOperationName			= "동일길이/폭구분공베드검색";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		String szYD_TO_LOC_GUIDE		= null;
		String szYD_MTL_L_GP			= null;
		String szYD_MTL_W_GP			= null;
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		
		String szALT_FLAG               = null;
		String szAL_FROM                = null;
		String szAL_TO                  = null;		
		
		String szSPAN_ORDER				= null;
		String szSCAN_DIR				= null;
		
		JDTORecord		recPara			= null;
		JDTORecord		recOut			= null;
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, recInPara);
		
		szYD_TO_LOC_GUIDE 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");						//사용자지정위치
		szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시위치 - 적치베드
		szSPAN_ORDER 		= ydDaoUtils.paraRecChkNull(recInPara, "SPAN_ORDER");							//스판검색범위
		szSCAN_DIR 			= ydDaoUtils.paraRecChkNull(recInPara, "SCAN_DIR");								//검색방향
		
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		
		szALT_FLAG          = ydDaoUtils.paraRecChkNull(recInPara, "ALT_FLAG");							    //대체크레인범위 추가 필터 여부
		szAL_FROM           = ydDaoUtils.paraRecChkNull(recInPara, "AL_FROM");							    //대체 FROM
		szAL_TO             = ydDaoUtils.paraRecChkNull(recInPara,   "AL_TO");							    //대체 TO		
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
		
			szYD_GP			= szYD_TO_LOC_GUIDE.substring(0, 1);
			szYD_BAY_GP		= szYD_TO_LOC_GUIDE.substring(1, 2);
			//SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_PILING_CD
		}else{
			//----------------------------------------------------------------------------------------------------------------------
			//	사용자지정위치(입고예정위치)가 존재하지 않는 경우에는 업무종료
			//	==>권상지시위치를 사용. : 2009.12.22
			//----------------------------------------------------------------------------------------------------------------------
			
			szYD_GP			= szYD_STK_COL_GP.substring(0, 1);
			szYD_BAY_GP		= szYD_STK_COL_GP.substring(1, 2);
			
			szLogMsg = "["+ szOperationName +"] 사용자지정위치(입고예정위치)가 존재하지 않으므로 권상지시위치를 사용";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//----------------------------------------------------------------------------------------------------------------------
		}
		
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
		recPara.setField("YD_STK_BED_L_GP", szYD_MTL_L_GP.substring(0, 1));
		recPara.setField("YD_STK_BED_W_GP", szYD_MTL_W_GP.substring(0, 1));
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	스판검색범위와 검색정렬방법을 정의
		//----------------------------------------------------------------------------------------------------------------------
		
		recOut = JDTORecordFactory.getInstance().create();
		setSpanSearchGpNColOrder(szSPAN_ORDER, szSCAN_DIR, recOut);
		
		String[] szSPAN_SEARCH_GP		= (String[])recOut.getField("SPAN_SEARCH_GP");
		String[] szCOL_ORDER			= (String[])recOut.getField("COL_ORDER");
		
		//---------------------------------------------------------------------------------------------------------
		// 크레인사양 조회
		// 설비ID로  크크레인 Grab 구분 추출
		//---------------------------------------------------------------------------------------------------------
		String szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID");
		ydUtils.putLog(szClassName, szMethodName, "To위치결정[크레인GRAB구분확인]"+szYD_EQP_ID, YdConstant.DEBUG);
		
		JDTORecord recTemp 		= JDTORecordFactory.getInstance().create();
		JDTORecord recSpec 		= JDTORecordFactory.getInstance().create();
		JDTORecordSet specSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recTemp.setField("YD_EQP_ID", szYD_EQP_ID);
		
		szRtnMsg	= DaoManager.getYdCrnspec(recTemp, specSet, 0);
		
		specSet.first();
		recSpec = specSet.getRecord();
		
		String szYD_CRN_GRAB_TP = ydDaoUtils.paraRecChkNull(recSpec, "YD_CRN_GRAB_TP"); // 크레인 Grab 구분
		
		if("X".equals(szYD_CRN_GRAB_TP)){
			if( szYD_STK_BED_NO.equals("") ) {
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
			}else if(szYD_STK_BED_NO.equals("01")){
				// 01, 02번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"2");
			}else if(szYD_STK_BED_NO.equals("03")){
				// 02, 03번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"3");
			}
		}else{
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 길이구분/폭구분을  가진 모든 공베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 동일한 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"]을 가진 모든 공베드 정보 조회 시작 ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		for(int i = 0 ; i < szSPAN_SEARCH_GP.length; i++ ) {
			
			recPara.setField("SPAN_SEARCH_GP", 	szSPAN_SEARCH_GP[i]);
			recPara.setField("COL_ORDER", 		szCOL_ORDER[i]);
			recPara.setField("LOOP_I", 			String.valueOf(i + 1));
			
			recPara.setField("ALT_FLAG", 	szALT_FLAG);
			recPara.setField("AL_FROM", 	szAL_FROM);
			recPara.setField("AL_TO", 		szAL_TO);
			
			srchEmptyBedWithSameLWGp(recPara, listToLoc);
		
		}
		
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 동일한 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"]을 가진 모든 공베드 정보 조회 완료 ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		//----------------------------------------------------------------------------------------------------------------------
			
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 혼적베드검색(주작업)
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getBedWithSimilarGp(
			  String sStlNo
			, JDTORecord recInPara
			, List listToLoc) throws JDTOException {
		/*
		 * szYD_STK_BED_NO : 권상지시위치의 베드 번지
		 * szSPAN_ORDER : 검색할 스판의 순서
		 * 		1) 1, 2, 3, 4 - 해당 스판만 검색
		 * 		2) 1234 - 04, 05, 0642스판을 먼저 검색 후 07 스판을 검색
		 * 		3) 3412 - 07스판을 먼저 검색 후 04, 05, 06 스판을 검색
		 * szSCAN_DIR	: 검색할 방향
		 * 		1) R2P : RT -> PT(차량)방향
		 * 		2) P2R : PT(차량) -> RT방향
		 * 
		 */
		String szMethodName				= "getBedWithSimilarGp";
		String szOperationName			= "혼적베드검색";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		String szYD_TO_LOC_GUIDE		= null;
		String szYD_MTL_L_GP			= null;
		String szYD_MTL_W_GP			= null;
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		
		String szALT_FLAG               = null;
		String szAL_FROM                = null;
		String szAL_TO                  = null;
		
		String szSPAN_ORDER				= null;
		String szSCAN_DIR				= null;
		
		JDTORecord		recPara			= null;
		JDTORecord		recOut			= null;
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(recInPara, "T"); // JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "혼적베드검색(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		ydUtils.displayRecord(szOperationName, recInPara);
		
		szYD_TO_LOC_GUIDE 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");						//사용자지정위치
		szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시위치 - 적치베드
		szSPAN_ORDER 		= ydDaoUtils.paraRecChkNull(recInPara, "SPAN_ORDER");							//스판검색범위
		szSCAN_DIR 			= ydDaoUtils.paraRecChkNull(recInPara, "SCAN_DIR");								//검색방향
		
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
		
			szYD_GP			= szYD_TO_LOC_GUIDE.substring(0, 1);
			szYD_BAY_GP		= szYD_TO_LOC_GUIDE.substring(1, 2);
			//SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_PILING_CD
		}else{
			//----------------------------------------------------------------------------------------------------------------------
			//	사용자지정위치(입고예정위치)가 존재하지 않는 경우에는 업무종료
			//	==>권상지시위치를 사용. : 2009.12.22
			//----------------------------------------------------------------------------------------------------------------------
			
			szYD_GP			= szYD_STK_COL_GP.substring(0, 1);
			szYD_BAY_GP		= szYD_STK_COL_GP.substring(1, 2);
			
			szLogMsg = "["+ szOperationName +"] 사용자지정위치(입고예정위치)가 존재하지 않으므로 권상지시위치를 사용";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
		}
		
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
		recPara.setField("YD_STK_BED_L_GP", szYD_MTL_L_GP.substring(0, 1));
		recPara.setField("YD_STK_BED_W_GP", szYD_MTL_W_GP.substring(0, 1));
		
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	스판검색범위와 검색정렬방법을 정의
		//----------------------------------------------------------------------------------------------------------------------
		
		recOut = JDTORecordFactory.getInstance().create();
		setSpanSearchGpNColOrder(szSPAN_ORDER, szSCAN_DIR, recOut);
		
		String[] szSPAN_SEARCH_GP		= (String[])recOut.getField("SPAN_SEARCH_GP");
		String[] szCOL_ORDER			= (String[])recOut.getField("COL_ORDER");
		
		//---------------------------------------------------------------------------------------------------------
		// 크레인사양 조회
		// 설비ID로  크크레인 Grab 구분 추출
		//---------------------------------------------------------------------------------------------------------
		String szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID");
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, "To위치결정[크레인GRAB구분확인]"+szYD_EQP_ID, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, "To위치결정[크레인GRAB구분확인]"+szYD_EQP_ID, YdConstant.DEBUG, logId);
		
		JDTORecord recTemp 		= JDTORecordFactory.getInstance().create();
		JDTORecord recSpec 		= JDTORecordFactory.getInstance().create();
		JDTORecordSet specSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recTemp.setField("YD_EQP_ID", szYD_EQP_ID);
		
		szRtnMsg	= DaoManager.getYdCrnspec(recTemp, specSet, 0);
		
		specSet.first();
		recSpec = specSet.getRecord();
		
		String szYD_CRN_GRAB_TP = ydDaoUtils.paraRecChkNull(recSpec, "YD_CRN_GRAB_TP"); // 크레인 Grab 구분
		
		if("X".equals(szYD_CRN_GRAB_TP)){
			if( szYD_STK_BED_NO.equals("") ) {
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
			}else if(szYD_STK_BED_NO.equals("01")){
				// 01, 02번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"2");
			}else if(szYD_STK_BED_NO.equals("03")){
				// 02, 03번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"3");
			}
		}else{
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	혼적베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 혼적베드 정보 조회 시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		for(int i = 0 ; i < szSPAN_SEARCH_GP.length; i++ ) {
			
			recPara.setField("SPAN_SEARCH_GP", 	szSPAN_SEARCH_GP[i]);
			recPara.setField("COL_ORDER", 		szCOL_ORDER[i]);
			recPara.setField("LOOP_I", 			String.valueOf(i + 1));
			
			recPara.setField("ALT_FLAG", szALT_FLAG);
			recPara.setField("AL_FROM" , szAL_FROM);
			recPara.setField("AL_TO"   , szAL_TO);
			
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// srchBedWithSimilarGp call 시  recPara 에 logId SET 추가 개선
			recPara.setField("LOG_ID", logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
			
			srchBedWithSimilarGp(sStlNo, recPara, listToLoc);
		
		}
		
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 혼적베드 정보 조회 완료 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
			
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "혼적베드검색(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 동일길이/폭구분공베드검색 - 보조작업
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getEmptyBedWithSameLWGpForAidWrk(
			JDTORecord recInPara
			, List listToLoc) throws JDTOException {
		/*
		 * 최상단 재료의 Piling Code와 동일한 Piling Code를 가진 베드 검색
		 * szYD_PILING_CD : Piling Code
		 * szYD_STK_BED_NO : 권상지시위치의 베드 번지
		 * szSPAN_ORDER : 검색할 스판의 순서
		 * 		1) 1, 2, 3, 4 - 해당 스판만 검색
		 * 		2) 1234 - 04, 05, 0642스판을 먼저 검색 후 07 스판을 검색
		 * 		3) 3412 - 07스판을 먼저 검색 후 04, 05, 06 스판을 검색
		 * szSCAN_DIR	: 검색할 방향
		 * 		1) R2P : RT -> PT(차량)방향
		 * 		2) P2R : PT(차량) -> RT방향
		 * 
		 */
		String szMethodName				= "getEmptyBedWithSameLWGpForAidWrk";
		String szOperationName			= "동일길이/폭구분공베드검색(보조작업)";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		String szYD_STK_COL_GP			= null;
		String szYD_MTL_L_GP			= null;
		String szYD_MTL_W_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		
		JDTORecord		recPara			= null;
		JDTORecord		recOut			= null;
		JDTORecordSet	rsResult		= null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시베드
		szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시위치의 베드
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		
		szYD_GP				= szYD_STK_COL_GP.substring(0, 1);
		szYD_BAY_GP			= szYD_STK_COL_GP.substring(1, 2);
		//----------------------------------------------------------------------------------------------------------------------
		
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
		recPara.setField("YD_STK_COL_GP",	szYD_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", 	szYD_STK_BED_NO);
		recPara.setField("YD_STK_BED_L_GP", szYD_MTL_L_GP.substring(0, 1));
		recPara.setField("YD_STK_BED_W_GP", szYD_MTL_W_GP.substring(0, 1));
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
		
		//---------------------------------------------------------------------------------------------------------
		// 크레인사양 조회
		// 설비ID로  크크레인 Grab 구분 추출
		//---------------------------------------------------------------------------------------------------------
		String szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID");
		ydUtils.putLog(szClassName, szMethodName, "To위치결정[크레인GRAB구분확인]"+szYD_EQP_ID, YdConstant.DEBUG);
		
		JDTORecord recTemp 		= JDTORecordFactory.getInstance().create();
		JDTORecord recSpec 		= JDTORecordFactory.getInstance().create();
		JDTORecordSet specSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recTemp.setField("YD_EQP_ID", szYD_EQP_ID);
		
		szRtnMsg	= DaoManager.getYdCrnspec(recTemp, specSet, 0);
		
		specSet.first();
		recSpec = specSet.getRecord();
		
		String szYD_CRN_GRAB_TP = ydDaoUtils.paraRecChkNull(recSpec, "YD_CRN_GRAB_TP"); // 크레인 Grab 구분
		
		if("X".equals(szYD_CRN_GRAB_TP)){
			if( szYD_STK_BED_NO.equals("") ) {
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
			}else if(szYD_STK_BED_NO.equals("01")){
				// 01, 02번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"2");
			}else if(szYD_STK_BED_NO.equals("03")){
				// 02, 03번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"3");
			}
		}else{
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		/*
		 * 04, 05, 06스판인 경우
		 */
		if( szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
			szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) || 	
			szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06)) {
			
			//----------------------------------------------------------------------------------------------------------------------
			//	동일한 길이/폭구분 공베드 검색 
			//----------------------------------------------------------------------------------------------------------------------
			szLogMsg = "["+ szOperationName +"] 권상위치보다 크고 검색ASC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 시작 ";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//1. 해당 권상위치보다 큰 열
			recPara.setField("COL_GT_LT_GP", 	"1"); //해당 적치열보다 큰 열 검색
			recPara.setField("SPAN_SEARCH_GP", 	"3"); //스판검색범위(01, 02스판)
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 27);
			
			szLogMsg = "["+ szOperationName +"] 권상위치보다 크고 검색ASC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 완료  - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//2. 해당 권상위치보다 작은 열
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) && rsResult.size() == 0 ) {
				
				szLogMsg = "["+ szOperationName +"] 권상위치보다 작고 검색DESC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 시작 ";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				recPara.setField("COL_GT_LT_GP", 	"2"); //해당 적치열보다 작은 열 검색
				recPara.setField("SPAN_SEARCH_GP", 	"3"); //스판검색범위(01, 02스판)
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 28);
				
				szLogMsg = "["+ szOperationName +"] 권상위치보다 작고 검색DESC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}
		/*
		 * 07스판인 경우
		 */	
		}else if( szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
				  szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP)) {
			
			//----------------------------------------------------------------------------------------------------------------------
			//	동일한 길이/폭구분 공베드 검색 
			//----------------------------------------------------------------------------------------------------------------------
			szLogMsg = "["+ szOperationName +"] 권상위치보다 작고 검색DESC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 시작 ";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//1. 해당 권상위치보다 적은 열
			recPara.setField("COL_GT_LT_GP", 	"2"); //해당 적치열보다 작은 열 검색
			recPara.setField("SPAN_SEARCH_GP", 	"4"); //스판검색범위(07스판)
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 28);

			szLogMsg = "["+ szOperationName +"] 권상위치보다 작고 검색DESC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 완료  - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//2. 해당 권상위치보다 큰 열
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) && rsResult.size() == 0 ) {
				
				szLogMsg = "["+ szOperationName +"] 권상위치보다 크고 검색ASC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 시작 ";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				recPara.setField("COL_GT_LT_GP", 	"1");	//해당 적치열보다 큰 열 검색
				recPara.setField("SPAN_SEARCH_GP", 	"4");	//스판검색범위(07스판)
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 27);
				
				szLogMsg = "["+ szOperationName +"] 권상위치보다 크고 검색ASC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}
		}
		
		recPara.setField("LOOP_I", 			"1");
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) && rsResult.size() > 0 ) {
			srchNconvRecord2Vo(	szYD_STK_COL_GP,
								szYD_STK_BED_NO,
								recPara, 
								rsResult, 
								listToLoc, 
								YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD);
		}
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 혼적베드검색 - 보조작업
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getBedWithSimilarGpForAidWrk(
			  String sStlNo
			, JDTORecord recInPara
			, List listToLoc) throws JDTOException {
		/*
		 * 최상단 재료의 Piling Code와 동일한 Piling Code를 가진 베드 검색
		 * szYD_PILING_CD : Piling Code
		 * szYD_STK_BED_NO : 권상지시위치의 베드 번지
		 * szSPAN_ORDER : 검색할 스판의 순서
		 * 		1) 1, 2, 3, 4 - 해당 스판만 검색
		 * 		2) 1234 - 04, 05, 0642스판을 먼저 검색 후 07 스판을 검색
		 * 		3) 3412 - 07스판을 먼저 검색 후 04, 05, 06 스판을 검색
		 * szSCAN_DIR	: 검색할 방향
		 * 		1) R2P : RT -> PT(차량)방향
		 * 		2) P2R : PT(차량) -> RT방향
		 * 
		 */
		String szMethodName				= "getBedWithSimilarGpForAidWrk";
		String szOperationName			= "혼적베드검색(보조작업)";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		String szYD_STK_COL_GP			= null;
		String szYD_MTL_L_GP			= null;
		String szYD_MTL_W_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		
		JDTORecord		recPara			= null;
		JDTORecord		recRecord		= null;
		JDTORecord		recOut			= null;
		
		JDTORecordSet	rsResult		= null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시베드
		szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시위치의 베드
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		
		szYD_GP				= szYD_STK_COL_GP.substring(0, 1);
		szYD_BAY_GP			= szYD_STK_COL_GP.substring(1, 2);
		//----------------------------------------------------------------------------------------------------------------------
		
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
		recPara.setField("YD_STK_COL_GP",	szYD_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", 	szYD_STK_BED_NO);
		recPara.setField("YD_STK_BED_L_GP", szYD_MTL_L_GP.substring(0, 1));
		recPara.setField("YD_STK_BED_W_GP", szYD_MTL_W_GP.substring(0, 1));
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
		
		//---------------------------------------------------------------------------------------------------------
		// 크레인사양 조회
		// 설비ID로  크크레인 Grab 구분 추출
		//---------------------------------------------------------------------------------------------------------
		String szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID");
		ydUtils.putLog(szClassName, szMethodName, "To위치결정[크레인GRAB구분확인]"+szYD_EQP_ID, YdConstant.DEBUG);
		
		JDTORecord recTemp 		= JDTORecordFactory.getInstance().create();
		JDTORecord recSpec 		= JDTORecordFactory.getInstance().create();
		JDTORecordSet specSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		recTemp.setField("YD_EQP_ID", szYD_EQP_ID);
		
		szRtnMsg	= DaoManager.getYdCrnspec(recTemp, specSet, 0);
		
		specSet.first();
		recSpec = specSet.getRecord();
		
		String szYD_CRN_GRAB_TP = ydDaoUtils.paraRecChkNull(recSpec, "YD_CRN_GRAB_TP"); // 크레인 Grab 구분
		
		if("X".equals(szYD_CRN_GRAB_TP)){
			if( szYD_STK_BED_NO.equals("") ) {
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
			}else if(szYD_STK_BED_NO.equals("01")){
				// 01, 02번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"2");
			}else if(szYD_STK_BED_NO.equals("03")){
				// 02, 03번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"3");
			}
		}else{
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		/*
		 * 04, 05, 06스판인 경우
		 */
		if( szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
			szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) || 	
			szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06)) {	

			recPara.setField("SPAN_SEARCH_GP", 	"3"); //스판검색범위(04, 05, 06스판)
			
		/*
		 * 07스판인 경우
		 */	
		}else if( szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
				  szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP)) {
			
			recPara.setField("SPAN_SEARCH_GP", 	"4"); //스판검색범위(07스판)
			
		}
		
		//----------------------------------------------------------------------------------------------------------------------
		//	혼적베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"]혼적베드(보조작업) 정보 조회 시작 ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		recPara.setField("STL_NO",	sStlNo);
		
		recRecord = JDTORecordFactory.getInstance().create();
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		//PIDEV_S :병행가동용:PI_YD
		recPara.setField("PI_YD",    	szYD_GP);					
		/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getOsPilingCdByStlNo_PIDEV*/
		szRtnMsg = DaoManager.getYdStock(recPara, rsResult, 213);
		
		rsResult.first();
		recRecord = rsResult.getRecord();
		
		String sSearchGbn	= "";
		String sGbn			= ydDaoUtils.paraRecChkNull(recRecord, "GBN");
		String sYdMtlWGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_MTL_W_GP");
		String sYdMtlLGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_MTL_L_GP");
		String sYdPilingGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_PILING_GP");
		String sDemanderCd	= ydDaoUtils.paraRecChkNull(recRecord, "DEMANDER_CD");
		
		String sYdStkBedLGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_STK_BED_L_GP");
		String sYdStkBedWGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_STK_BED_W_GP");
		String sShipCd		= ydDaoUtils.paraRecChkNull(recRecord, "SHIP_CD");

//		SJH05004
		String sDelTermCd	= ydDaoUtils.paraRecChkNull(recRecord, "DELIVER_TERM_CD"); // 검색
		String sCustCd		= ydDaoUtils.paraRecChkNull(recRecord, "CUST_CD");
		
		ydUtils.putLog(szClassName, szMethodName, "sGbn=>"+sGbn, 				YdConstant.DEBUG);
		ydUtils.putLog(szClassName, szMethodName, "sYdMtlWGp=>"+sYdMtlWGp, 		YdConstant.DEBUG);
		ydUtils.putLog(szClassName, szMethodName, "sYdMtlLGp=>"+sYdMtlLGp, 		YdConstant.DEBUG);
		ydUtils.putLog(szClassName, szMethodName, "sYdPilingGp=>"+sYdPilingGp, 	YdConstant.DEBUG);
		ydUtils.putLog(szClassName, szMethodName, "sDemanderCd=>"+sDemanderCd, 	YdConstant.DEBUG);
		
		ydUtils.putLog(szClassName, szMethodName, "sYdStkBedLGp=>"+sYdStkBedLGp,YdConstant.DEBUG);
		ydUtils.putLog(szClassName, szMethodName, "sYdStkBedWGp=>"+sYdStkBedWGp,YdConstant.DEBUG);
		ydUtils.putLog(szClassName, szMethodName, "sShipCd=>"+sShipCd, 			YdConstant.DEBUG);
		
		recPara.setField("YD_MTL_W_GP",		sYdMtlWGp);
		recPara.setField("YD_MTL_L_GP",		sYdMtlLGp);
		recPara.setField("YD_PILING_GP",	sYdPilingGp);
		recPara.setField("DEMANDER_CD",		sDemanderCd);
		
		recPara.setField("YD_STK_BED_L_GP",	sYdStkBedLGp);
		recPara.setField("YD_STK_BED_W_GP",	sYdStkBedWGp);
		recPara.setField("SHIP_CD",			sShipCd);

//		SJH05004		
		recPara.setField("DELIVER_TERM_CD",	sDelTermCd);
		recPara.setField("CUST_CD",			sCustCd);
		
		int iLength		= 0;
		String[] arryS 	= null;
		
		if("2".equals(sGbn)){	// 해송
			iLength = 4;
			arryS 	= new String[iLength];
			arryS[0] 	= "1";
			arryS[1] 	= "2";
			arryS[2] 	= "4";
			arryS[3] 	= "5";
		}else if("3".equals(sGbn)){	// 주문외
			iLength = 3;
			arryS 	= new String[iLength];
			arryS[0] 	= "3";
			arryS[1] 	= "6";
			arryS[2] 	= "7";
		}else{					// 육송
			iLength = 3;
			arryS 		= new String[iLength];
			arryS[0] 	= "1";
			arryS[1] 	= "2";
			arryS[2] 	= "*"; //출하권역별 검색
		}

		for(int idx = 0; idx < iLength; idx++ ){
			
			recPara.setField("SEARCH_GBN",arryS[idx]);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			if("*".equals(arryS[idx])){
//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD", szYD_GP);						
				szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 107);
			}else{
				szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 106);
			}
			
			recPara.setField("LOOP_I", 			"1");
			
			if(( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) && ( rsResult.size() > 0 )) {
				srchNconvRecord2Vo(	szYD_STK_COL_GP,
									szYD_STK_BED_NO,
									recPara, 
									rsResult, 
									listToLoc, 
									YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD);
				
				if(listToLoc.size() > 0) {
					break;
				}	
			}
		}
		
		szLogMsg = "["+ szOperationName +"] 혼적베드(보조작업) 정보 조회 완료  - 메세지 : " + szRtnMsg;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	
	/**
	 * 스판검색범위와 검색정렬방법을 정의
	 * @param szSPAN_ORDER
	 * @param szSCAN_DIR
	 * @param recOut
	 * @throws JDTOException
	 */
	public static void setSpanSearchGpNColOrder(String szSPAN_ORDER, String szSCAN_DIR, JDTORecord recOut) throws JDTOException {
		String[] szSPAN_SEARCH_GP 	= null;
		String[] szCOL_ORDER		= null;
		if( szSPAN_ORDER.equals(YdConstant.SPAN_ORDER_1234)) {
			szSPAN_SEARCH_GP		= new String[2];
			szCOL_ORDER				= new String[2];
			//12스판
			szSPAN_SEARCH_GP[0]		= "3";
			szCOL_ORDER[0]			= YdConstant.ORDER_BY_ASC;
			
			//34스판
			szSPAN_SEARCH_GP[1]		= "4";
			szCOL_ORDER[1]			= YdConstant.ORDER_BY_DESC;
			
		}else if( szSPAN_ORDER.equals(YdConstant.SPAN_ORDER_3412)) {
			szSPAN_SEARCH_GP		= new String[2];
			szCOL_ORDER				= new String[2];
			//34스판

			szSPAN_SEARCH_GP[0]		= "4";
			szCOL_ORDER[0]			= YdConstant.ORDER_BY_DESC;
			//12스판
			szSPAN_SEARCH_GP[1]		= "3";
			szCOL_ORDER[1]			= YdConstant.ORDER_BY_ASC;
			
		}else if( szSPAN_ORDER.equals(YdConstant.SPAN_ORDER_12)) {
			szSPAN_SEARCH_GP		= new String[1];
			szCOL_ORDER				= new String[1];
			//12스판만
			szSPAN_SEARCH_GP[0]		= "3";
			
			if( szSCAN_DIR.equals(YdConstant.SCAN_DIR_RT2PT)) {
				szCOL_ORDER[0]			= YdConstant.ORDER_BY_DESC;
			}else if( szSCAN_DIR.equals(YdConstant.SCAN_DIR_PT2RT)) {
				szCOL_ORDER[0]			= YdConstant.ORDER_BY_ASC;
			}
		}else if( szSPAN_ORDER.equals(YdConstant.SPAN_ORDER_34)) {
			szSPAN_SEARCH_GP		= new String[1];
			szCOL_ORDER				= new String[1];
			//34스판만
			szSPAN_SEARCH_GP[0]		= "4";
			
			if( szSCAN_DIR.equals(YdConstant.SCAN_DIR_RT2PT)) {
				szCOL_ORDER[0]			= YdConstant.ORDER_BY_ASC;
			}else if( szSCAN_DIR.equals(YdConstant.SCAN_DIR_PT2RT)) {
				szCOL_ORDER[0]			= YdConstant.ORDER_BY_DESC;
			}
		}else{
			//모든 스판
			szSPAN_SEARCH_GP		= new String[1];
			szCOL_ORDER				= new String[1];
			
			szSPAN_SEARCH_GP[0]		= "1";
			szCOL_ORDER[0]			= YdConstant.ORDER_BY_ASC;
		}
		
		recOut.setField("SPAN_SEARCH_GP", 	szSPAN_SEARCH_GP);
		recOut.setField("COL_ORDER", 		szCOL_ORDER);
	}
	
	
	/**
	 * 동일Piling재료베드정보조회
	 * @param recPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String srchBedWithSamePilingCd(JDTORecord recPara, List listToLoc) throws JDTOException {
		String szMethodName				= "srchBedWithSamePilingCd";
		String szOperationName			= "동일Piling재료베드정보조회";
		String szLogMsg					= null;
		String szRtnMsg					= null;
		
		JDTORecordSet	rsResult		= null;
		String szCOL_ORDER				= null;
		
		String szALT_FLAG               = null;
		String szAL_FROM                = null;
		String szAL_TO                  = null;
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		szCOL_ORDER 		= ydDaoUtils.paraRecChkNull(recPara, "COL_ORDER");
		
		szALT_FLAG         = ydDaoUtils.paraRecChkNull(recPara, "ALT_FLAG");
		szAL_FROM          = ydDaoUtils.paraRecChkNull(recPara, "AL_FROM");
		szAL_TO            = ydDaoUtils.paraRecChkNull(recPara, "AL_TO");
		
		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 단 정보 조회 - 동일한 Piling Code를 가진 최상단 재료가 있는 모든 베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		if(szALT_FLAG.equals("Y")){  //대체크레인 추가 범위 필터 사용여부
		/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatioWithAlFrTo*/
			
		//쿼리 짜야됨..
		szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 110);			
		}
		else{
		/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatio*/
		szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 105);
		}
		szLogMsg = "["+ szOperationName +"] 동일한 Piling Code를 가진 최상단 재료가 있는 모든 베드 정보 조회 완료 - 메세지 : " + szRtnMsg;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		//----------------------------------------------------------------------------------------------------------------------

		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {

			srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD);
		}
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 동일길이/폭구분 공베드정보조회
	 * @param recPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String srchEmptyBedWithSameLWGp(JDTORecord recPara, List listToLoc) throws JDTOException {
		String szMethodName				= "srchEmptyBedWithSameLWGp";
		String szOperationName			= "동일길이/폭구분 공베드정보조회";
		String szLogMsg					= null;
		String szRtnMsg					= null;
		
		JDTORecordSet	rsResult		= null;
		
		String szCOL_ORDER				= null;
		
		String szALT_FLAG               = null;
		String szAL_FROM                = null;
		String szAL_TO                  = null;		
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		szCOL_ORDER 		= ydDaoUtils.paraRecChkNull(recPara, "COL_ORDER");
		
		szALT_FLAG          = ydDaoUtils.paraRecChkNull(recPara, "ALT_FLAG");							    //대체크레인범위 추가 필터 여부
		szAL_FROM           = ydDaoUtils.paraRecChkNull(recPara, "AL_FROM");							    //대체 FROM
		szAL_TO             = ydDaoUtils.paraRecChkNull(recPara,   "AL_TO");							    //대체 TO			

		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 베드 정보 조회 - 동일길이/폭구분을 가진  모든 공베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		if( szCOL_ORDER.equals(YdConstant.ORDER_BY_ASC)) {
			if(szALT_FLAG.equals("Y")){
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAscWithAlFrTo*/
				szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 45);
			}
			else{
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAsc*/
				szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 25);
			}
		}else if( szCOL_ORDER.equals(YdConstant.ORDER_BY_DESC)) {
			if(szALT_FLAG.equals("Y")){
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColDescWithAlFrTo*/
				szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 46);
			}
			else{
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColDesc*/
				szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 26);
			}
		}
		
		szLogMsg = "["+ szOperationName +"] 동일길이/폭구분을 가진  모든 공베드 정보 조회 완료 - 메세지 : " + szRtnMsg;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		//----------------------------------------------------------------------------------------------------------------------
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {

			srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_EMPTY_BED);
			
		}
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 혼적베드정보조회(주작업)
	 * @param recPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String srchBedWithSimilarGp(String sStlNo, JDTORecord recPara, List listToLoc) throws JDTOException {
		String szMethodName				= "srchBedWithSimilarGp";
		String szOperationName			= "혼적베드정보조회(주작업)";
		String szLogMsg					= null;
		String szRtnMsg					= null;
		
		JDTORecordSet	rsResult		= null;
		JDTORecord		recRecord		= null;
		
		String szALT_FLAG               = null;
		String szAL_FROM                = null;
		String szAL_TO                  = null;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(recPara, "T");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "혼적베드정보조회(주작업)(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 베드 정보 조회 - 혼적베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		recPara.setField("STL_NO",	sStlNo);
		
		recRecord = JDTORecordFactory.getInstance().create();
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		//PIDEV_S :병행가동용:PI_YD
		recPara.setField("PI_YD",    	"T");	
		
		szALT_FLAG= ydDaoUtils.paraRecChkNull(recPara, "ALT_FLAG");
		szAL_FROM= ydDaoUtils.paraRecChkNull(recPara, "AL_FROM");
		szAL_TO= ydDaoUtils.paraRecChkNull(recPara, "AL_TO");
		
		
		/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getOsPilingCdByStlNo_PIDEV*/
		szRtnMsg = DaoManager.getYdStock(recPara, rsResult, 213);			
		
		rsResult.first();
		recRecord = rsResult.getRecord();
		
		String sSearchGbn	= "";
		String sGbn			= ydDaoUtils.paraRecChkNull(recRecord, "GBN");
		String sYdMtlWGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_MTL_W_GP");
		String sYdMtlLGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_MTL_L_GP");
		String sYdPilingGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_PILING_GP");
		String sDemanderCd	= ydDaoUtils.paraRecChkNull(recRecord, "DEMANDER_CD");
		
		String sYdStkBedLGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_STK_BED_L_GP");
		String sYdStkBedWGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_STK_BED_W_GP");
		String sShipCd		= ydDaoUtils.paraRecChkNull(recRecord, "SHIP_CD");
//		SJH05004
		String sDelTermCd	= ydDaoUtils.paraRecChkNull(recRecord, "DELIVER_TERM_CD"); // 검색
		String sCustCd		= ydDaoUtils.paraRecChkNull(recRecord, "CUST_CD");
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
//		ydUtils.putLog(szClassName, szMethodName, "sGbn=>"+sGbn, 				YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sYdMtlWGp=>"+sYdMtlWGp, 		YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sYdMtlLGp=>"+sYdMtlLGp, 		YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sYdPilingGp=>"+sYdPilingGp, 	YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sDemanderCd=>"+sDemanderCd, 	YdConstant.DEBUG);
//		
//		ydUtils.putLog(szClassName, szMethodName, "sYdStkBedLGp=>"+sYdStkBedLGp,YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sYdStkBedWGp=>"+sYdStkBedWGp,YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sShipCd=>"+sShipCd, 			YdConstant.DEBUG);

		ydUtils.putLogNew(szClassName, szMethodName, "sGbn=>"+sGbn, 				YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdMtlWGp=>"+sYdMtlWGp, 		YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdMtlLGp=>"+sYdMtlLGp, 		YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdPilingGp=>"+sYdPilingGp, 	YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sDemanderCd=>"+sDemanderCd, 	YdConstant.DEBUG, logId);
		
		ydUtils.putLogNew(szClassName, szMethodName, "sYdStkBedLGp=>"+sYdStkBedLGp,	YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdStkBedWGp=>"+sYdStkBedWGp,	YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sShipCd=>"+sShipCd, 			YdConstant.DEBUG, logId);
		
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		recPara.setField("YD_MTL_W_GP",		sYdMtlWGp);
		recPara.setField("YD_MTL_L_GP",		sYdMtlLGp);
		recPara.setField("YD_PILING_GP",	sYdPilingGp);
		recPara.setField("DEMANDER_CD",		sDemanderCd);
		
		recPara.setField("YD_STK_BED_L_GP",	sYdStkBedLGp);
		recPara.setField("YD_STK_BED_W_GP",	sYdStkBedWGp);
		recPara.setField("SHIP_CD",			sShipCd);

//		SJH05004		
		recPara.setField("DELIVER_TERM_CD",	sDelTermCd);
		recPara.setField("CUST_CD",			sCustCd);
		
		int iLength		= 0;
		String[] arryS 	= null;
		
		if("2".equals(sGbn)){	// 해송
			iLength = 4;
			arryS 	= new String[iLength];
			arryS[0] 	= "1";
			arryS[1] 	= "2";
			arryS[2] 	= "4";
			arryS[3] 	= "5";
		}else if("3".equals(sGbn)){	// 주문외
			iLength = 3;
			arryS 	= new String[iLength];
			arryS[0] 	= "3";
			arryS[1] 	= "6";
			arryS[2] 	= "7";
		}else{					// 육송
			iLength = 3;
			arryS 		= new String[iLength];
			arryS[0] 	= "1";
			arryS[1] 	= "2";
			arryS[2] 	= "*"; //출하권역별 검색
		}
	
//SJH		
		for(int idx = 0; idx < iLength; idx++ ){
			
			recPara.setField("SEARCH_GBN",arryS[idx]);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			if("*".equals(arryS[idx])){
//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD",    	"T");	
				
				if(szALT_FLAG.equals("Y")){
					//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithShipSimilarBed_PIDEVWithAlFrTo
					szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 111);  //신규 쿼리 생성
				}
				else{
					//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithShipSimilarBed_PIDEV
					szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 107);
				}
			}else{
				if(szALT_FLAG.equals("Y")){
					//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBedWithAlFrTo
					szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 112);  //신규 쿼리 생성 
				}
				else{
					//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBed
					szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 106);
				}
			}
			
			if(( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) && ( rsResult.size() > 0 )) {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// srchNconvRecord2Vo argument 에 logId 항목 추가 개선
//				srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_EMPTY_BED);
				srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_EMPTY_BED, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
				if( listToLoc.size() > 0 ) {
					break;
				}	
			}
		}
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "혼적베드정보조회(주작업)(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 베드적치유무판단/VO변환
	 * @param recPara
	 * @param rsResult
	 * @param listToLoc
	 * @param intPRIOR
	 * @throws JDTOException
	 */
	public static void srchNconvRecord2Vo(
			  String sOrgStkCol
			, String sOrgStkBed
			, JDTORecord recPara
			, JDTORecordSet	rsResult
			, List listToLoc
			, int intPRIOR
			) throws JDTOException {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선 START
// srchNconvRecord2Vo 사용하는곳이 여러곳이라 argument 에 logId 항목 추가 하지 않고
// 기존  srchNconvRecord2Vo(String, String, JDTORecord, JDTORecordSet, List, int) 에서	
// logId 새로 발본 하여 기존 로직은 그대로 이고 logId argument 만 추가 하여 		
// 신규  srchNconvRecord2Vo(String, String, JDTORecord, JDTORecordSet, List, int, String) 작성		
// 기존 putLog -> putLogNew logId 출력 되게 개선
String szMethodName				= "srchNconvRecord2Vo";
String szLogMsg					= null;
String logId				    = ydUtils.getLogIdNew("T");	// 후판 제품 log id 새로 발번

	try {
		
		YdToLocDcsnUtil.srchNconvRecord2Vo(sOrgStkCol, sOrgStkBed, recPara, rsResult, listToLoc, intPRIOR, logId);
		
		return ;
	
	} catch (Exception e) {
	
		szLogMsg = "베드적치유무판단/VO변환 ERROR : " + e.getMessage();
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
		
		throw new DAOException(szClassName + e.getMessage(), e);
	
	}   // end try catch

// 2024.09.?? 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
	
	} // end of srchNconvRecord2Vo	
	
	/**
	 * 베드적치유무판단/VO변환
	 * @param recPara
	 * @param rsResult
	 * @param listToLoc
	 * @param intPRIOR
	 * @throws JDTOException
	 */
	public static void srchNconvRecord2Vo(
			  String sOrgStkCol
			, String sOrgStkBed
			, JDTORecord recPara
			, JDTORecordSet	rsResult
			, List listToLoc
			, int intPRIOR
			, String logId					/* 로그일련번호 */
			) throws JDTOException {
		String szMethodName			= "srchNconvRecord2Vo";
		String szOperationName		= "베드적치유무판단/VO변환";
		String szLogMsg				= null;
		String szRtnMsg				= null;
		
		JDTORecord recTemp			= null;
		JDTORecord recTemp1			= null;
		
		YdStkLocVO	ydStkLocVO		= null;
		
		String szYD_STK_COL_GP		= null;
		String szYD_STK_BED_NO		= null;
		
		String szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		String szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		String szYD_EQP_WRK_T 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_T");						//크레인작업재료 총높이
		String szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");							//크레인스케줄코드
		
		int intLOOP_I 			= ydDaoUtils.paraRecChkNullInt(recPara, "LOOP_I");
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선 START
// srchNconvRecord2Vo 사용하는곳이 여러곳이라 argument 에 
// logId 항목 추가된 신규 srchNconvRecord2Vo 새로 작성		
// 기존 putLog -> putLogNew logId 출력 되게 개선

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "베드적치유무판단/VO변환(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);

// 2024.09.?? 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
		
		recTemp1 = JDTORecordFactory.getInstance().create();
		for(int i = 1; i <= rsResult.size(); i++) {
			rsResult.absolute(i);
			recTemp = rsResult.getRecord();
			
			szLogMsg = "["+ szOperationName +"] 레코드 추출["+i+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO");
			
			if(sOrgStkCol.equals(szYD_STK_COL_GP)&&
			   sOrgStkBed.equals(szYD_STK_BED_NO)){
				
				szLogMsg = "["+ szOperationName +"] 권상베드와 To위치가 동일.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				continue;
			}
			
			recTemp1.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
			recTemp1.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
			recTemp1.setField("YD_EQP_WRK_SH", 		szYD_EQP_WRK_SH);
			recTemp1.setField("YD_EQP_WRK_WT", 		szYD_EQP_WRK_WT);
			recTemp1.setField("YD_EQP_WRK_T", 		szYD_EQP_WRK_T);
			recTemp1.setField("YD_SCH_CD", 			szYD_SCH_CD);
			
			ydStkLocVO		= new YdStkLocVO();
			
			szLogMsg = "["+ szOperationName +"] 베드["+szYD_STK_COL_GP+" - "+szYD_STK_BED_NO+"]가 적치가능한 지 확인 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// procBedStackable call 시  recTemp1 에 logId SET 추가 개선
			recTemp1.setField("LOG_ID", logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
			szRtnMsg = procBedStackable(recTemp1, ydStkLocVO, szMethodName);
			
			szLogMsg = "["+ szOperationName +"] 베드["+szYD_STK_COL_GP+" - "+szYD_STK_BED_NO+"]가 적치가능한 지 확인 완료. 결과코드 ["+szRtnMsg+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			
				ydStkLocVO.setPrior(intPRIOR);
				ydStkLocVO.setSeq(i + ( intLOOP_I * YdConstant.TO_LOC_PRIOR_STEP * intPRIOR));
				
				listToLoc.add(ydStkLocVO);
			}
		}
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선 START
szLogMsg = "베드적치유무판단/VO변환(" + szMethodName + ") 완료";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////

	}
	
	
	/**
	 * 베드적치유무판단/VO변환(입고가적베드)
	 * @param recPara
	 * @param rsResult
	 * @param listToLoc
	 * @param intPRIOR
	 * @throws JDTOException
	 */
	public static void srchNconvRecord2VoTmpBed(
			  String sOrgStkCol
			, String sOrgStkBed
			, JDTORecord recPara
			, JDTORecordSet	rsResult
			, List listToLoc
			, int intPRIOR
			) throws JDTOException {
		String szMethodName			= "srchNconvRecord2VoTmpBed";
		String szOperationName		= "베드적치유무판단/VO변환";
		String szLogMsg				= null;
		String szRtnMsg				= null;
		
		JDTORecord recTemp			= null;
		JDTORecord recTemp1			= null;
		
		YdStkLocVO	ydStkLocVO		= null;
		
		String szYD_STK_COL_GP		= null;
		String szYD_STK_BED_NO		= null;
		
		String szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		String szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		String szYD_EQP_WRK_T 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_T");						//크레인작업재료 총높이
		String szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");							//크레인스케줄코드
		
		int intLOOP_I 			= ydDaoUtils.paraRecChkNullInt(recPara, "LOOP_I");
		
		recTemp1 = JDTORecordFactory.getInstance().create();
		for(int i = 1; i <= rsResult.size(); i++) {
			rsResult.absolute(i);
			recTemp = rsResult.getRecord();
			
			szLogMsg = "["+ szOperationName +"] 레코드 추출["+i+"]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO");
			
			if(sOrgStkCol.equals(szYD_STK_COL_GP)&&
			   sOrgStkBed.equals(szYD_STK_BED_NO)){
				
				szLogMsg = "["+ szOperationName +"] 권상베드와 To위치가 동일.";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				continue;
			}
			
			recTemp1.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
			recTemp1.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
			recTemp1.setField("YD_EQP_WRK_SH", 		szYD_EQP_WRK_SH);
			recTemp1.setField("YD_EQP_WRK_WT", 		szYD_EQP_WRK_WT);
			recTemp1.setField("YD_EQP_WRK_T", 		szYD_EQP_WRK_T);
			recTemp1.setField("YD_SCH_CD", 			szYD_SCH_CD);
			
			ydStkLocVO		= new YdStkLocVO();
			
			szLogMsg = "["+ szOperationName +"] 베드["+szYD_STK_COL_GP+" - "+szYD_STK_BED_NO+"]가 적치가능한 지 확인 시작";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			szRtnMsg = procBedStackableTmpBed(recTemp1, ydStkLocVO, szMethodName);
			
			szLogMsg = "["+ szOperationName +"] 베드["+szYD_STK_COL_GP+" - "+szYD_STK_BED_NO+"]가 적치가능한 지 확인 완료";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			
				ydStkLocVO.setPrior(intPRIOR);
				ydStkLocVO.setSeq(i + ( intLOOP_I * YdConstant.TO_LOC_PRIOR_STEP * intPRIOR));
				
				listToLoc.add(ydStkLocVO);
			}
		}
	}
	
	
	//------------------------------------------------------------------------------------------------------------------------------------
	//	공통 메소드
	//------------------------------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * RT상의 베드구하기
	 * @param szYD_UP_STK_COL_GP
	 * @param szYD_UP_STK_BED_NO
	 * @param szYD_MTL_L_GP
	 * @return
	 */
	public static String getRtStkLocByBedNoMtlLGp(String szYD_UP_STK_COL_GP, String szYD_UP_STK_BED_NO, String szYD_MTL_L_GP) {
		String szMethodName		= "getRtStkLocByBedNoMtlLGp";
		String szOperationName	= "RT상의 베드구하기";
		String szLogMsg			= "";
		String szYD_STK_BED_NO 	= "";
		String szYD_GP			= szYD_UP_STK_COL_GP.substring(0, 1); //야드구분
		String szYD_BAY_GP 		= szYD_UP_STK_COL_GP.substring(1, 2); //동구분
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터확인 : 권상적치열구분["+szYD_UP_STK_COL_GP+"], 권상베드번호["+szYD_UP_STK_BED_NO+"], 재료길이구분["+szYD_MTL_L_GP+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)) {
			//2후판
			if( szYD_UP_STK_BED_NO.equals("01") ) {
				if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "20";
				}else if( szYD_BAY_GP.equals("C") ) {
					//szYD_STK_BED_NO		= "35"; 
					szYD_STK_BED_NO		= "40"; 
				}else if( szYD_BAY_GP.equals("D") ) {
					szYD_STK_BED_NO		= "60";
				}else if( szYD_BAY_GP.equals("E") ) {
					szYD_STK_BED_NO		= "80";
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "00";
				}else if( szYD_BAY_GP.equals("G") ) {
					szYD_STK_BED_NO		= "20";
				}
			}else{
				if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "10";
				}else if( szYD_BAY_GP.equals("C") ) {
					//szYD_STK_BED_NO		= "25";
					szYD_STK_BED_NO		= "30";
				}else if( szYD_BAY_GP.equals("D") ) {
					szYD_STK_BED_NO		= "55";
				}else if( szYD_BAY_GP.equals("E") ) {
					szYD_STK_BED_NO		= "65";
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "95";
				}else if( szYD_BAY_GP.equals("G") ) {
					szYD_STK_BED_NO		= "10";
				}
			}
			
		} else {
			//1후판
			if( szYD_UP_STK_BED_NO.equals("01") ) {
				if( szYD_BAY_GP.equals("A") ) {
					szYD_STK_BED_NO		= "20";
				}else if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "40";
				}else if( szYD_BAY_GP.equals("C") ) {
					szYD_STK_BED_NO		= "60";
				}else if( szYD_BAY_GP.equals("D") ) {
					szYD_STK_BED_NO		= "80";
				}else if( szYD_BAY_GP.equals("E") ) {
					szYD_STK_BED_NO		= "C0";
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "E0";
				}
			}else if( szYD_UP_STK_BED_NO.equals("02") ) {
				if( szYD_BAY_GP.equals("A") ) {
					szYD_STK_BED_NO		= "10";
				}else if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "30";
				}else if( szYD_BAY_GP.equals("C") ) {
					szYD_STK_BED_NO		= "50";
				}else if( szYD_BAY_GP.equals("D") ) {
					szYD_STK_BED_NO		= "F0";
				}else if( szYD_BAY_GP.equals("E") ) {
					szYD_STK_BED_NO		= "B0";
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "D0";
				}
			}else if( szYD_UP_STK_BED_NO.equals("03") ) {
				if( szYD_BAY_GP.equals("A") ) {
					szYD_STK_BED_NO		= "10";
				}else if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "30";
				}else if( szYD_BAY_GP.equals("C") ) {
					szYD_STK_BED_NO		= "50";
				}else if( szYD_BAY_GP.equals("D") ) {
					szYD_STK_BED_NO		= "70";
				}else if( szYD_BAY_GP.equals("E") ) {
					szYD_STK_BED_NO		= "A0";
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "D0";
				}
			}else {
				if( szYD_BAY_GP.equals("A") ) {
					szYD_STK_BED_NO		= "10";
				}else if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "30";
				}else if( szYD_BAY_GP.equals("C") ) {
					szYD_STK_BED_NO		= "50";
				}else if( szYD_BAY_GP.equals("D") ) {
					szYD_STK_BED_NO		= "70";
				}else if( szYD_BAY_GP.equals("E") ) {
					szYD_STK_BED_NO		= "90";
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "D0";
				}
			}
		}

		
		szLogMsg = "["+ szOperationName +"] RT상 베드번호["+szYD_STK_BED_NO+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return szYD_STK_BED_NO;
	}

	/**
	 * RT상의 베드구하기
	 * @param szYD_UP_STK_COL_GP
	 * @param szYD_UP_STK_BED_NO
	 * @param szYD_MTL_L_GP
	 * @param dbMAX_MTL_L
	 * @return
	 */
	public static String getRtStkLocByBedNoMtlLGp(String szYD_UP_STK_COL_GP, String szYD_UP_STK_BED_NO, String szYD_MTL_L_GP, double dbMAX_MTL_L) {
		String szMethodName		= "getRtStkLocByBedNoMtlLGp";
		String szOperationName	= "RT상의 베드구하기";
		String szLogMsg			= "";
		String szYD_STK_BED_NO 	= "";
		String szYD_GP			= szYD_UP_STK_COL_GP.substring(0, 1); //야드구분
		String szYD_BAY_GP 		= szYD_UP_STK_COL_GP.substring(1, 2); //동구분
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터확인 : 권상적치열구분["+szYD_UP_STK_COL_GP+"], 권상베드번호["+szYD_UP_STK_BED_NO+"], 재료길이구분["+szYD_MTL_L_GP+"], 재료최대길이["+dbMAX_MTL_L+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)) {
			//2후판
			if( szYD_UP_STK_BED_NO.equals("01") ) {
				if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "20";
				}else if( szYD_BAY_GP.equals("C") ) {
					if(YdConstant.YD_MTL_LEN_LONG.equals(szYD_MTL_L_GP)) { 
						szYD_STK_BED_NO		= "35"; //장척은  35 으로
					} else {
						szYD_STK_BED_NO		= "40"; //초장척은 40 으로
					}
				}else if( szYD_BAY_GP.equals("D") ) {
					if(dbMAX_MTL_L < 9220) {
						szYD_STK_BED_NO		= "55";
					} else {
						szYD_STK_BED_NO		= "60";
					}
				}else if( szYD_BAY_GP.equals("E") ) {
					if(dbMAX_MTL_L < 6820) {
						szYD_STK_BED_NO		= "75";
					} else {
						szYD_STK_BED_NO		= "80";
					}
				}else if( szYD_BAY_GP.equals("F") ) {
					//if(dbMAX_MTL_L < 11340) {
					if(dbMAX_MTL_L < 12670) {
						szYD_STK_BED_NO		= "95";
					} else {
						szYD_STK_BED_NO		= "00";
					}
				}else if( szYD_BAY_GP.equals("G") ) {
					if(dbMAX_MTL_L < 9220) {
						szYD_STK_BED_NO		= "15";
					} else {
						szYD_STK_BED_NO		= "20";
					}
				}
			}else{
				if( szYD_BAY_GP.equals("B") ) {
					if("TBRTRC".equals(szYD_UP_STK_COL_GP)) {
						//C-RT --> A-RT 동간이적시 권상위치BED 번호를 권하위치 BED로 사용한다. 2014.02.04
						szYD_STK_BED_NO		= szYD_UP_STK_BED_NO;
					} else {
						//그 외 RT 반납일경우 
						szYD_STK_BED_NO		= "10";
					}
				}else if( szYD_BAY_GP.equals("C") ) {
					//szYD_STK_BED_NO		= "25";
					szYD_STK_BED_NO		= "30";
				}else if( szYD_BAY_GP.equals("D") ) {
					if( szYD_UP_STK_BED_NO.equals("02")) {
						szYD_STK_BED_NO		= "55";
					} else {
						szYD_STK_BED_NO		= "55";
					}
				}else if( szYD_BAY_GP.equals("E") ) {
					if( szYD_UP_STK_BED_NO.equals("02")) {
						szYD_STK_BED_NO		= "75";
					} else if( szYD_UP_STK_BED_NO.equals("03")) {
						szYD_STK_BED_NO		= "70";
					} else {
						szYD_STK_BED_NO		= "65";
					}					
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "95";
				}else if( szYD_BAY_GP.equals("G") ) {
					szYD_STK_BED_NO		= "10";
				}
			}
			
		} else {
			//1후판
			if( szYD_UP_STK_BED_NO.equals("01") ) {
				if( szYD_BAY_GP.equals("A") ) {
					szYD_STK_BED_NO		= "20";
				}else if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "40";
				}else if( szYD_BAY_GP.equals("C") ) {
					szYD_STK_BED_NO		= "60";
				}else if( szYD_BAY_GP.equals("D") ) {
					szYD_STK_BED_NO		= "80";
				}else if( szYD_BAY_GP.equals("E") ) {
					szYD_STK_BED_NO		= "C0";
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "E0";
				}
			}else if( szYD_UP_STK_BED_NO.equals("02") ) {
				if( szYD_BAY_GP.equals("A") ) {
					szYD_STK_BED_NO		= "10";
				}else if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "30";
				}else if( szYD_BAY_GP.equals("C") ) {
					szYD_STK_BED_NO		= "50";
				}else if( szYD_BAY_GP.equals("D") ) {
					szYD_STK_BED_NO		= "F0";
				}else if( szYD_BAY_GP.equals("E") ) {
					szYD_STK_BED_NO		= "B0";
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "D0";
				}
			}else if( szYD_UP_STK_BED_NO.equals("03") ) {
				if( szYD_BAY_GP.equals("A") ) {
					szYD_STK_BED_NO		= "10";
				}else if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "30";
				}else if( szYD_BAY_GP.equals("C") ) {
					szYD_STK_BED_NO		= "50";
				}else if( szYD_BAY_GP.equals("D") ) {
					szYD_STK_BED_NO		= "70";
				}else if( szYD_BAY_GP.equals("E") ) {
					szYD_STK_BED_NO		= "A0";
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "D0";
				}
			}else {
				if( szYD_BAY_GP.equals("A") ) {
					szYD_STK_BED_NO		= "10";
				}else if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "30";
				}else if( szYD_BAY_GP.equals("C") ) {
					szYD_STK_BED_NO		= "50";
				}else if( szYD_BAY_GP.equals("D") ) {
					szYD_STK_BED_NO		= "70";
				}else if( szYD_BAY_GP.equals("E") ) {
					szYD_STK_BED_NO		= "90";
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "D0";
				}
			}
		}

		
		szLogMsg = "["+ szOperationName +"] RT상 베드번호["+szYD_STK_BED_NO+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return szYD_STK_BED_NO;
	}
	
	/**
	 * RT상의 베드구하기 (통합크레인스케줄)
	 * @param szYD_UP_STK_COL_GP
	 * @param szYD_UP_STK_BED_NO
	 * @param szYD_MTL_L_GP
	 * @param dbMAX_MTL_L
	 * @param szYD_DN_STK_COL_GP
	 * @return
	 */
	public static String getRtStkLocByBedNoMtlLGp(String szYD_UP_STK_COL_GP, String szYD_UP_STK_BED_NO, String szYD_MTL_L_GP, double dbMAX_MTL_L, String szYD_DN_STK_COL_GP) {
		String szMethodName		= "getRtStkLocByBedNoMtlLGp";
		String szOperationName	= "RT상의 베드구하기";
		String szLogMsg			= "";
		String szYD_STK_BED_NO 	= "";
		//String szYD_GP			= szYD_UP_STK_COL_GP.substring(0, 1); //야드구분
		String szYD_BAY_GP 		= szYD_UP_STK_COL_GP.substring(1, 2); //동구분
		String szRT_GP			= szYD_DN_STK_COL_GP.substring(4, 6); //RT 구분 : 2후판 (RA, RB, RC), 1후판 ( RD, RE, RF )
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터확인 : 권상적치열구분["+szYD_UP_STK_COL_GP+"], 권상베드번호["+szYD_UP_STK_BED_NO+"], 재료길이구분["+szYD_MTL_L_GP+"], 재료최대길이["+dbMAX_MTL_L+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		if("RA".equals(szRT_GP) || "RB".equals(szRT_GP) || "RC".equals(szRT_GP)) {
			//2후판
			if( szYD_UP_STK_BED_NO.equals("01") ) {
				if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "20";
				}else if( szYD_BAY_GP.equals("C") ) {
					if(YdConstant.YD_MTL_LEN_LONG.equals(szYD_MTL_L_GP)) { 
						szYD_STK_BED_NO		= "35"; //장척은  35 으로
					} else {
						szYD_STK_BED_NO		= "40"; //초장척은 40 으로
					}
				}else if( szYD_BAY_GP.equals("D") ) {
					if(dbMAX_MTL_L < 9220) {
						szYD_STK_BED_NO		= "55";
					} else {
						szYD_STK_BED_NO		= "60";
					}
				}else if( szYD_BAY_GP.equals("E") ) {
					if(dbMAX_MTL_L < 6820) {
						szYD_STK_BED_NO		= "75";
					} else {
						szYD_STK_BED_NO		= "80";
					}
				}else if( szYD_BAY_GP.equals("F") ) {
					//if(dbMAX_MTL_L < 11340) {
					if(dbMAX_MTL_L < 12670) {
						szYD_STK_BED_NO		= "95";
					} else {
						szYD_STK_BED_NO		= "00";
					}
				}else if( szYD_BAY_GP.equals("G") ) {
					if(dbMAX_MTL_L < 9220) {
						szYD_STK_BED_NO		= "15";
					} else {
						szYD_STK_BED_NO		= "20";
					}
				}
			}else{
				if( szYD_BAY_GP.equals("B") ) {
					if("TBRTRC".equals(szYD_UP_STK_COL_GP)) {
						//C-RT --> A-RT 동간이적시 권상위치BED 번호를 권하위치 BED로 사용한다. 2014.02.04
						szYD_STK_BED_NO		= szYD_UP_STK_BED_NO;
					} else {
						//그 외 RT 반납일경우 
						szYD_STK_BED_NO		= "10";
					}
				}else if( szYD_BAY_GP.equals("C") ) {
					//szYD_STK_BED_NO		= "25";
					szYD_STK_BED_NO		= "30";
				}else if( szYD_BAY_GP.equals("D") ) {
					if( szYD_UP_STK_BED_NO.equals("02")) {
						szYD_STK_BED_NO		= "55";
					} else {
						szYD_STK_BED_NO		= "55";
					}
				}else if( szYD_BAY_GP.equals("E") ) {
					if( szYD_UP_STK_BED_NO.equals("02")) {
						szYD_STK_BED_NO		= "75";
					} else if( szYD_UP_STK_BED_NO.equals("03")) {
						szYD_STK_BED_NO		= "70";
					} else {
						szYD_STK_BED_NO		= "65";
					}					
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "95";
				}else if( szYD_BAY_GP.equals("G") ) {
					szYD_STK_BED_NO		= "10";
				}
			}
			
		} else {
			//1후판
			if( szYD_UP_STK_BED_NO.equals("01") ) {
				if( szYD_BAY_GP.equals("A") ) {
					szYD_STK_BED_NO		= "20";
				}else if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "40";
				}else if( szYD_BAY_GP.equals("C") ) {
					szYD_STK_BED_NO		= "60";
				}else if( szYD_BAY_GP.equals("D") ) {
					szYD_STK_BED_NO		= "80";
				}else if( szYD_BAY_GP.equals("E") ) {
					szYD_STK_BED_NO		= "C0";
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "E0";
				}
			}else if( szYD_UP_STK_BED_NO.equals("02") ) {
				if( szYD_BAY_GP.equals("A") ) {
					szYD_STK_BED_NO		= "10";
				}else if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "30";
				}else if( szYD_BAY_GP.equals("C") ) {
					szYD_STK_BED_NO		= "50";
				}else if( szYD_BAY_GP.equals("D") ) {
					szYD_STK_BED_NO		= "F0";
				}else if( szYD_BAY_GP.equals("E") ) {
					szYD_STK_BED_NO		= "B0";
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "D0";
				}
			}else if( szYD_UP_STK_BED_NO.equals("03") ) {
				if( szYD_BAY_GP.equals("A") ) {
					szYD_STK_BED_NO		= "10";
				}else if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "30";
				}else if( szYD_BAY_GP.equals("C") ) {
					szYD_STK_BED_NO		= "50";
				}else if( szYD_BAY_GP.equals("D") ) {
					szYD_STK_BED_NO		= "70";
				}else if( szYD_BAY_GP.equals("E") ) {
					szYD_STK_BED_NO		= "A0";
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "D0";
				}
			}else {
				if( szYD_BAY_GP.equals("A") ) {
					szYD_STK_BED_NO		= "10";
				}else if( szYD_BAY_GP.equals("B") ) {
					szYD_STK_BED_NO		= "30";
				}else if( szYD_BAY_GP.equals("C") ) {
					szYD_STK_BED_NO		= "50";
				}else if( szYD_BAY_GP.equals("D") ) {
					szYD_STK_BED_NO		= "70";
				}else if( szYD_BAY_GP.equals("E") ) {
					szYD_STK_BED_NO		= "90";
				}else if( szYD_BAY_GP.equals("F") ) {
					szYD_STK_BED_NO		= "D0";
				}
			}
		}
	
		szLogMsg = "["+ szOperationName +"] RT상 베드번호["+szYD_STK_BED_NO+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return szYD_STK_BED_NO;
	}	
	
	/**
	 * JDTO Record를 VO객체로 변환하는 메소드
	 * @param recPara
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO procRecord2StkLoc(JDTORecord recPara) throws JDTOException {
		YdStkLocVO ydStkLocVO = new YdStkLocVO();
		
		ydStkLocVO.setYdStkColGp(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP"));
		ydStkLocVO.setYdStkBedNo(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"));
		ydStkLocVO.setYdStkLyrNo(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO"));
		ydStkLocVO.setYdStkBedLGp(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_L_GP"));
		ydStkLocVO.setYdStkBedWGp(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_W_GP"));
		ydStkLocVO.setYdStkBedActStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_ACT_STAT"));
		ydStkLocVO.setYdStkBedWhioStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_WHIO_STAT"));
		ydStkLocVO.setYdStkBedLyrMax(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STK_BED_LYR_MAX"));
		ydStkLocVO.setYdStkBedWtMax(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STK_BED_WT_MAX"));
		ydStkLocVO.setYdStkBedHMax(ydDaoUtils.paraRecChkNullDouble(recPara, "YD_STK_BED_H_MAX"));
		ydStkLocVO.setYdStkLyrActStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_ACT_STAT"));
		ydStkLocVO.setYdStkLyrMtlStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT"));
		
		ydStkLocVO.setYdStkableBedLyr(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STKABLE_BED_LYR"));
		ydStkLocVO.setYdStkableBedWt(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STKABLE_BED_WT"));
		ydStkLocVO.setYdStkableBedH(ydDaoUtils.paraRecChkNullDouble(recPara, "YD_STKABLE_BED_H"));
		ydStkLocVO.setYdBedErrCd(ydDaoUtils.paraRecChkNullInt(recPara, "BED_ERR_CD"));
		
		return ydStkLocVO;
	}
	
	
	/**
	 * JDTO Record를 VO객체로 변환하는 메소드
	 * @param recPara
	 * @return
	 * @throws JDTOException
	 */
	public static void procRecord2StkLoc(JDTORecord recPara, YdStkLocVO ydStkLocVO) throws JDTOException {
		
		ydStkLocVO.setYdStkColGp(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP"));
		ydStkLocVO.setYdStkBedNo(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"));
		ydStkLocVO.setYdStkLyrNo(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO"));
		ydStkLocVO.setYdStkBedLGp(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_L_GP"));
		ydStkLocVO.setYdStkBedWGp(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_W_GP"));
		ydStkLocVO.setYdStkBedActStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_ACT_STAT"));
		ydStkLocVO.setYdStkBedWhioStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_WHIO_STAT"));
		ydStkLocVO.setYdStkBedLyrMax(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STK_BED_LYR_MAX"));
		ydStkLocVO.setYdStkBedWtMax(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STK_BED_WT_MAX"));
		ydStkLocVO.setYdStkBedHMax(ydDaoUtils.paraRecChkNullDouble(recPara, "YD_STK_BED_H_MAX"));
		ydStkLocVO.setYdStkLyrActStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_ACT_STAT"));
		ydStkLocVO.setYdStkLyrMtlStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT"));
		
		ydStkLocVO.setYdStkableBedLyr(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STKABLE_BED_LYR"));
		ydStkLocVO.setYdStkableBedWt(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STKABLE_BED_WT"));
		ydStkLocVO.setYdStkableBedH(ydDaoUtils.paraRecChkNullDouble(recPara, "YD_STKABLE_BED_H"));
		ydStkLocVO.setYdBedErrCd(ydDaoUtils.paraRecChkNullInt(recPara, "BED_ERR_CD"));
		
		//return ydStkLocVO;
	}
	
	/**
	 * 베드사양체크
	 * @param recToBed
	 * @param recLowestCrnWrkMtl
	 * @param recStkHighestLyr
	 * @return
	 * @throws JDTOException
	 */
	public static String procBedStackable(
			JDTORecord recToBed								/* TO위치베드 정보 */
			, JDTORecord recLowestCrnWrkMtl					/* 최하단의 크레인작업재료 정보 */
			, JDTORecord recStkHighestLyr					/* TO위치베드의 최상단의 적치중이거나 권하대기인 재료정보, null이면 공베드 */
			) throws JDTOException {
		String szOperationName				= "베드사양체크";
		String szMethodName					= "procBedStackable";
		String szLogMsg						= "";
		
		String szYD_STK_COL_GP				= null;
		String szYD_STK_BED_NO				= null;
		
		//베드 정보
		int intYD_STK_BED_LYR_MAX			= 0;						//베드에 적치가능한 단수
		int intYD_STK_BED_WT_MAX			= 0;						//베드에 적치가능한 중량
		double dblYD_STK_BED_H_MAX			= 0;						//베드에 적치가능한 높이
		
		//베드에 적치중이거나 권하대기인 재료들의 정보
		int intLYR_STK_SH_CNT      			= 0;						//베드에 적치중이거나 권하대기인 총 매수
		int intSUM_LYR_STK_MTL_WT      		= 0;						//베드에 적치중이거나 권하대기인 총 중량
		double dblSUM_LYR_STK_MTL_T       	= 0;						//베드에 적치중이거나 권하대기인 총 높이
		
		//크레인작업재료들의 정보
		int intSH_CNT      					= 0;						//크레인작업재료 총 매수
		int intSUM_MTL_WT      				= 0;						//크레인작업재료 총 중량
		double dblSUM_MTL_T       			= 0;						//크레인작업재료 총 높이
		
		//-------------------------------------------------------------------------------
		// TO위치베드의 적치가능 유무 판단.
		//-------------------------------------------------------------------------------
		
		
		szYD_STK_COL_GP						= ydDaoUtils.paraRecChkNull(recToBed, "YD_STK_COL_GP");
		szYD_STK_BED_NO						= ydDaoUtils.paraRecChkNull(recToBed, "YD_STK_BED_NO");
		
		intYD_STK_BED_LYR_MAX				= ydDaoUtils.paraRecChkNullInt(recToBed, "YD_STK_BED_LYR_MAX");							//베드에 적치가능한 단수
		intYD_STK_BED_WT_MAX				= ydDaoUtils.paraRecChkNullInt(recToBed, "YD_STK_BED_WT_MAX");							//베드에 적치가능한 중량
		dblYD_STK_BED_H_MAX					= ydDaoUtils.paraRecChkNullDouble(recToBed, "YD_STK_BED_H_MAX");						//베드에 적치가능한 높이
		
		intSH_CNT							= ydDaoUtils.paraRecChkNullInt(recLowestCrnWrkMtl, "SH_CNT");							//크레인작업재료 총 매수
		intSUM_MTL_WT						= ydDaoUtils.paraRecChkNullInt(recLowestCrnWrkMtl, "SUM_MTL_WT");						//크레인작업재료 총 중량
		dblSUM_MTL_T						= ydDaoUtils.paraRecChkNullDouble(recLowestCrnWrkMtl, "SUM_MTL_T");						//크레인작업재료 총 높이
		
		if( recStkHighestLyr != null ) {					//NULL이 아니면 적치중이거나 권하대기인 재료가 존재
			intLYR_STK_SH_CNT							= ydDaoUtils.paraRecChkNullInt(recStkHighestLyr, "SH_CNT");					//베드에 적치중이거나 권하대기인 총 매수
			intSUM_LYR_STK_MTL_WT						= ydDaoUtils.paraRecChkNullInt(recStkHighestLyr, "SUM_MTL_WT");				//베드에 적치중이거나 권하대기인 총 중량
			dblSUM_LYR_STK_MTL_T						= ydDaoUtils.paraRecChkNullDouble(recStkHighestLyr, "SUM_MTL_T");			//베드에 적치중이거나 권하대기인 총 높이
		}else{
			
			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]는 공베드임";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
		}
		
		if( intYD_STK_BED_LYR_MAX < intSH_CNT +  intLYR_STK_SH_CNT ) {
			
			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]에 적치가능한 매수["+intYD_STK_BED_LYR_MAX+"] 초과 - 적치중이거나 권하대기인 매수["+intLYR_STK_SH_CNT+"], 크레인작업재료 매수["+intSH_CNT+"]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			return YdConstant.RETN_SH_OVER;
		}
		
		if( intYD_STK_BED_WT_MAX < intSUM_MTL_WT + intSUM_LYR_STK_MTL_WT ) {
			
			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]에 적치가능한 중량["+intYD_STK_BED_WT_MAX+"] 초과 - 적치중이거나 권하대기인 총중량["+intSUM_LYR_STK_MTL_WT+"], 크레인작업재료 총중량["+intSUM_MTL_WT+"]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			return YdConstant.RETN_WT_OVER;
		}
		
		if( dblYD_STK_BED_H_MAX < dblSUM_MTL_T + dblSUM_LYR_STK_MTL_T ) {
			
			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]에 적치가능한 높이["+dblYD_STK_BED_H_MAX+"] 초과 - 적치중이거나 권하대기인 총높이["+dblSUM_LYR_STK_MTL_T+"], 크레인작업재료 총높이["+dblSUM_MTL_T+"]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			return YdConstant.RETN_H_OVER;
		}
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	
	/**
	 * 슬라브TO위치평점판단
	 * @param recToBed
	 * @param recLowestCrnWrkMtl
	 * @param recStkHighestLyr
	 * @param recOutParam
	 * @return
	 * @throws JDTOException
	 */
	public static String procToLocGradeTestForSlabYard(
			JDTORecord recToBed								/* TO위치베드 정보 */
			, JDTORecord recLowestCrnWrkMtl					/* 최하단의 크레인작업재료 정보 */
			, JDTORecord recStkHighestLyr					/* TO위치베드의 최상단의 적치중이거나 권하대기인 재료정보, null이면 공베드 */
			, JDTORecord recOutParam						/* TO위치 평점 */
			) throws JDTOException {
		String szRtnMsg						= YdConstant.RETN_CD_SUCCESS;
		String szOperationName				= "슬라브TO위치평점판단";
		String szMethodName					= "procToLocGradeTestForSlabYard";
		String szLogMsg						= "";
		
		JDTORecord		recInTemp			= null;
		JDTORecordSet	rsTemp				= null;
		
		//String szYD_STK_LOT_TP_GRADE		= "";						//산적LOT TYPE비교 결과
		String szYD_STK_LOT_CD_GRADE		= "";						//산적LOT CODE비교 결과
		String szYD_STL_W_CMP_GRADE			= "";						//슬라브폭비교결과
		
		String szYD_STK_COL_GP				= null;
		String szYD_STK_BED_NO				= null;
		
		//베드 정보
//		int intYD_STK_BED_LYR_MAX			= 0;						//베드에 적치가능한 단수
//		int intYD_STK_BED_WT_MAX			= 0;						//베드에 적치가능한 중량
//		double dblYD_STK_BED_H_MAX			= 0;						//베드에 적치가능한 높이
		
		//베드에 적치중이거나 권하대기인 재료들의 정보
//		int intLYR_STK_SH_CNT      			= 0;						//베드에 적치중이거나 권하대기인 총 매수
//		int intSUM_LYR_STK_MTL_WT      		= 0;						//베드에 적치중이거나 권하대기인 총 중량
//		double dblSUM_LYR_STK_MTL_T       	= 0;						//베드에 적치중이거나 권하대기인 총 높이
		
		//적치단의 최상단 재료의 정보
		String szYD_STK_LOT_TP				= null;						//적치단의 최상단 재료의 산적LOT TYPE
		String szYD_STK_LOT_CD				= null;						//적치단의 최상단 재료의산적LOT CODE
		double dblYD_MTL_W					= 0;						//적치단의 최상단 재료의 폭
		double dblYD_MTL_L					= 0;						//적치단의 최상단 재료의 길이 
		String szSTL_NO						= "";						//적치단의 최상단 재료번호
		
		//크레인작업재료들의 정보
//		int intSH_CNT      					= 0;						//크레인작업재료 총 매수
//		int intSUM_MTL_WT      				= 0;						//크레인작업재료 총 중량
//		double dblSUM_MTL_T       			= 0;						//크레인작업재료 총 높이
		
		//최하단 크레인작업재료 정보
		String szCRN_YD_STK_LOT_TP			= null;						//최하단 크레인작업재료 산적LOT TYPE
		String szCRN_YD_STK_LOT_CD			= null;						//최하단 크레인작업재료 산적LOT CODE
		double dblCRN_YD_MTL_W				= 0;						//최하단 크레인작업재료폭
		double dblCRN_YD_MTL_L				= 0;						//최하단 크레인작업재료길이 
		String szCRN_STL_NO					= null;						//최하단 크레인작업재료번호
		
		//크레인설비ID
		String szYD_EQP_ID					= null;
		double dblYD_CRN_TONG_W_TOL			= 0;
		double dblYD_STLLOT_CNT				= 0;
		
//		boolean bIsOverWt					= false;
//		boolean bIsOverT					= false;
//		boolean bIsOverSh					= false;
		
		//String szTO_LOC_GRADE				= "";
		
		//-------------------------------------------------------------------------------
		// TO위치베드의 적치가능 유무 판단.
		//-------------------------------------------------------------------------------
		
		szYD_EQP_ID							= ydDaoUtils.paraRecChkNull(recLowestCrnWrkMtl, "YD_EQP_ID");
		
		szYD_STK_COL_GP						= ydDaoUtils.paraRecChkNull(recToBed, "YD_STK_COL_GP");
		szYD_STK_BED_NO						= ydDaoUtils.paraRecChkNull(recToBed, "YD_STK_BED_NO");
		
//		intYD_STK_BED_LYR_MAX				= ydDaoUtils.paraRecChkNullInt(recToBed, "YD_STK_BED_LYR_MAX");							//베드에 적치가능한 단수
//		intYD_STK_BED_WT_MAX				= ydDaoUtils.paraRecChkNullInt(recToBed, "YD_STK_BED_WT_MAX");							//베드에 적치가능한 중량
//		dblYD_STK_BED_H_MAX					= ydDaoUtils.paraRecChkNullDouble(recToBed, "YD_STK_BED_H_MAX");						//베드에 적치가능한 높이
		
//		intSH_CNT							= ydDaoUtils.paraRecChkNullInt(recLowestCrnWrkMtl, "SH_CNT");							//크레인작업재료 총 매수
//		intSUM_MTL_WT						= ydDaoUtils.paraRecChkNullInt(recLowestCrnWrkMtl, "SUM_MTL_WT");						//크레인작업재료 총 중량
//		dblSUM_MTL_T						= ydDaoUtils.paraRecChkNullDouble(recLowestCrnWrkMtl, "SUM_MTL_T");						//크레인작업재료 총 높이
		
		szCRN_YD_STK_LOT_TP					= ydDaoUtils.paraRecChkNull(recLowestCrnWrkMtl, "YD_STK_LOT_TP");						//최하단 크레인작업재료 산적LOT TYPE
		szCRN_YD_STK_LOT_CD					= ydDaoUtils.paraRecChkNull(recLowestCrnWrkMtl, "YD_STK_LOT_CD");						//최하단 크레인작업재료 산적LOT CODE
		dblCRN_YD_MTL_W						= ydDaoUtils.paraRecChkNullDouble(recLowestCrnWrkMtl, "YD_MTL_W");						//최하단 크레인작업재료폭
		dblCRN_YD_MTL_L						= ydDaoUtils.paraRecChkNullDouble(recLowestCrnWrkMtl, "YD_MTL_L");						//최하단 크레인작업재료길이
		szCRN_STL_NO						= ydDaoUtils.paraRecChkNull(recLowestCrnWrkMtl, "STL_NO");								//최하단 크레인작업재료폭
		
		
		if( recStkHighestLyr != null ) {					//NULL이 아니면 적치중이거나 권하대기인 재료가 존재
//			intLYR_STK_SH_CNT							= ydDaoUtils.paraRecChkNullInt(recStkHighestLyr, "SH_CNT");					//베드에 적치중이거나 권하대기인 총 매수
//			intSUM_LYR_STK_MTL_WT						= ydDaoUtils.paraRecChkNullInt(recStkHighestLyr, "SUM_MTL_WT");				//베드에 적치중이거나 권하대기인 총 중량
//			dblSUM_LYR_STK_MTL_T						= ydDaoUtils.paraRecChkNullDouble(recStkHighestLyr, "SUM_MTL_T");			//베드에 적치중이거나 권하대기인 총 높이
			
			szYD_STK_LOT_TP								= ydDaoUtils.paraRecChkNull(recStkHighestLyr, "YD_STK_LOT_TP");				//적치단의 최상단 재료의 산적LOT TYPE
			szYD_STK_LOT_CD								= ydDaoUtils.paraRecChkNull(recStkHighestLyr, "YD_STK_LOT_CD");				//적치단의 최상단 재료의산적LOT CODE
			dblYD_MTL_W									= ydDaoUtils.paraRecChkNullDouble(recStkHighestLyr, "YD_MTL_W");			//적치단의 최상단 재료의 폭
			dblYD_MTL_L									= ydDaoUtils.paraRecChkNullDouble(recStkHighestLyr, "YD_MTL_L");			//적치단의 최상단 재료의 길이
			szSTL_NO									= ydDaoUtils.paraRecChkNull(recStkHighestLyr, "STL_NO");					//적치단의 최상단 재료번호
			
		}else{
			
			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]는 공베드임";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
		}
		
//		if( intYD_STK_BED_LYR_MAX < intSH_CNT +  intLYR_STK_SH_CNT ) {
//			bIsOverSh				= true;
//			
//			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]에 적치가능한 매수["+intYD_STK_BED_LYR_MAX+"] 초과 - 적치중이거나 권하대기인 매수["+intLYR_STK_SH_CNT+"], 크레인작업재료 매수["+intSH_CNT+"]";
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
//			
//			return YdConstant.RETN_SH_OVER;
//		}
//		
//		if( intYD_STK_BED_WT_MAX < intSUM_MTL_WT + intSUM_LYR_STK_MTL_WT ) {
//			bIsOverWt				= true;
//			
//			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]에 적치가능한 중량["+intYD_STK_BED_WT_MAX+"] 초과 - 적치중이거나 권하대기인 총중량["+intSUM_LYR_STK_MTL_WT+"], 크레인작업재료 총중량["+intSUM_MTL_WT+"]";
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
//			
//			return YdConstant.RETN_WT_OVER;
//		}
//		
//		if( dblYD_STK_BED_H_MAX < dblSUM_MTL_T + dblSUM_LYR_STK_MTL_T ) {
//			bIsOverT				= true;
//			
//			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]에 적치가능한 높이["+dblYD_STK_BED_H_MAX+"] 초과 - 적치중이거나 권하대기인 총높이["+dblSUM_LYR_STK_MTL_T+"], 크레인작업재료 총높이["+dblSUM_MTL_T+"]";
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
//			
//			return YdConstant.RETN_H_OVER;
//		}
//		
//		if( bIsOverSh || bIsOverWt || bIsOverT ) {
//			return YdConstant.
//		}
		
		//-------------------------------------------------------------------------------
		
		//-------------------------------------------------------------------------------
		//	크레인설비사양 조회
		//-------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 크레인설비["+szYD_EQP_ID+"]에 대한 사양 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		rsTemp					= JDTORecordFactory.getInstance().createRecordSet("");
		recInTemp				= JDTORecordFactory.getInstance().create();
		recInTemp.setField("YD_EQP_ID", 				szYD_EQP_ID);
		
		szRtnMsg			= DaoManager.getYdCrnspec(recInTemp, rsTemp, 0);
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			
			rsTemp.first();
			
			recInTemp			= rsTemp.getRecord();
		
			dblYD_CRN_TONG_W_TOL= ydDaoUtils.paraRecChkNullDouble(recInTemp, "YD_CRN_TONG_W_TOL");
			
			szLogMsg = "["+ szOperationName +"] 크레인설비["+szYD_EQP_ID+"]에 대한 사양 조회 성공 - 야드크레인집게폭허용오차[YD_CRN_TONG_W_TOL:"+dblYD_CRN_TONG_W_TOL+"]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		}else{
			szLogMsg = "["+ szOperationName +"] 크레인설비["+szYD_EQP_ID+"]에 대한 사양 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			return szRtnMsg;
		}
		//-------------------------------------------------------------------------------
		
		//-------------------------------------------------------------------------------
		//	TO위치베드의 정보에 따른 분기 처리
		//-------------------------------------------------------------------------------
		
		if( recStkHighestLyr == null ) {					// NULL이면 공베드
			szYD_STK_LOT_TP						= "E";
			szYD_STK_LOT_CD_GRADE				= "E1";
			szYD_STL_W_CMP_GRADE				= "E1";
			
			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]는 공베드이므로 야드산적LOT코드비교결과["+szYD_STK_LOT_CD_GRADE+"], 슬라브폭편차비교결과["+szYD_STL_W_CMP_GRADE+"]를 사용";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}else{												//공베드가 아님
			//-------------------------------------------------------------------------------
			// 해당베드에 장입재의 존재유무를 체크한다. 장입재 존재하면 해당베드는 SKIP
			// 우선 SA/SB/SY/SE/SG 만 체크한다. 
			//-------------------------------------------------------------------------------
			if( szCRN_YD_STK_LOT_TP.equals("SA") || 
				szCRN_YD_STK_LOT_TP.equals("SB") ||
				szCRN_YD_STK_LOT_TP.equals("SY") ||
				szCRN_YD_STK_LOT_TP.equals("SE") ||
				szCRN_YD_STK_LOT_TP.equals("SG") ){		
				
				rsTemp					= JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp				= JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
				recInTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
				
				szRtnMsg = DaoManager.getYdStklyr(recInTemp, rsTemp, 103);
				
				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					
					rsTemp.first();
					
					recInTemp = rsTemp.getRecord();
				
					dblYD_STLLOT_CNT = ydDaoUtils.paraRecChkNullDouble(recInTemp, "CNT");
					
					szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]는 장입재 적치갯수["+dblYD_STLLOT_CNT+"]";
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				}else{
					szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"] 장입재 적치갯수 조회 시 오류발생 - 메세지 : " + szRtnMsg;
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					return szRtnMsg;
				}
			//-------------------------------------------------------------------------------
			// 해당베드에 장입재의 우선순위를 체크한다. 해당재료보다 우선순위가 빠른 대상재가 적치되어 있으면 SKIP
			//-------------------------------------------------------------------------------
			}else if( szCRN_YD_STK_LOT_TP.equals("SP") || 
					  szCRN_YD_STK_LOT_TP.equals("SL") ){		
				
				rsTemp					= JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp				= JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_LOT_TP", szCRN_YD_STK_LOT_TP);
				recInTemp.setField("YD_STK_LOT_CD", szCRN_YD_STK_LOT_CD);
				recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
				recInTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
				
				szRtnMsg = DaoManager.getYdStklyr(recInTemp, rsTemp, 104);
				
				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					
					rsTemp.first();
					
					recInTemp = rsTemp.getRecord();
				
					dblYD_STLLOT_CNT = ydDaoUtils.paraRecChkNullDouble(recInTemp, "CNT");
					
					szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]는 장입재 적치갯수["+dblYD_STLLOT_CNT+"]";
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				}else{
					szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"] 장입재 적치갯수 조회 시 오류발생 - 메세지 : " + szRtnMsg;
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					return szRtnMsg;
				}
			}
			
			if(dblYD_STLLOT_CNT == 0){
			
				//-------------------------------------------------------------------------------
				// 크레인작업재료와 검색위치재료 폭 비교
				//-------------------------------------------------------------------------------
				szYD_STL_W_CMP_GRADE			= getWidthCmpGrade(dblCRN_YD_MTL_W, dblYD_MTL_W, dblYD_CRN_TONG_W_TOL);
				
				szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]는 공베드가 아님 - 슬라브폭편차비교결과["+szYD_STL_W_CMP_GRADE+"]";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				//-------------------------------------------------------------------------------
				// 크레인작업재료와 검색위치재료 길이 비교
				//-------------------------------------------------------------------------------
				if(getLengthCmpGrade(dblCRN_YD_MTL_L,dblYD_MTL_L)){
					//-------------------------------------------------------------------------------
					// TO위치 평점 판단
					//-------------------------------------------------------------------------------
					szLogMsg = "["+ szOperationName +"] 최하단크레인작업재료["+szCRN_STL_NO+"]정보[야드산적LOT TYPE:"+szCRN_YD_STK_LOT_TP+", 야드산적LOT코드:"+szCRN_YD_STK_LOT_CD+"], 최상단의 재료["+szSTL_NO+"]정보[야드산적LOT TYPE:"+szYD_STK_LOT_TP+", 야드산적LOT코드:"+szYD_STK_LOT_CD+"]";
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					if( szCRN_YD_STK_LOT_TP.equals("SP") ) {							//후판장입일련번호 비교
						szYD_STK_LOT_CD_GRADE					= getPlateChgSeqCmp(szYD_STK_LOT_TP, szCRN_YD_STK_LOT_CD, szYD_STK_LOT_CD);
					}else if( szCRN_YD_STK_LOT_TP.equals("SL") ) {						//열연장입Lot번호 비교
						szYD_STK_LOT_CD_GRADE					= getHrChgSeqCmp(szYD_STK_LOT_TP, szCRN_YD_STK_LOT_CD, szYD_STK_LOT_CD);
					}else if( szCRN_YD_STK_LOT_TP.equals("SA") ) {						//정정대기산적Lot코드비교
						szYD_STK_LOT_CD_GRADE					= getCorrectionWaitCmp(szYD_STK_LOT_TP, szCRN_YD_STK_LOT_CD, szYD_STK_LOT_CD);
					}else if( szCRN_YD_STK_LOT_TP.equals("SB") ) {						//지시대기산적Lot코드비교
						szYD_STK_LOT_CD_GRADE					= getOrderWaitCmp(szYD_STK_LOT_TP, szCRN_YD_STK_LOT_CD, szYD_STK_LOT_CD);
					}else if( szCRN_YD_STK_LOT_TP.equals("SY") ) {						//충당대기산적Lot코드비교
						szYD_STK_LOT_CD_GRADE					= getMatchWaitCmp(szYD_STK_LOT_TP, szCRN_YD_STK_LOT_CD, szYD_STK_LOT_CD);
					}else if( szCRN_YD_STK_LOT_TP.equals("SE") ) {						//이송대기산적Lot코드비교
						szYD_STK_LOT_CD_GRADE					= getFtMvWaitCmp(szYD_STK_LOT_TP, szCRN_YD_STK_LOT_CD, szYD_STK_LOT_CD);
					}else if( szCRN_YD_STK_LOT_TP.equals("SG") ) {						//외판대기산적Lot코드비교
						szYD_STK_LOT_CD_GRADE					= getOutPlWaitCmp(szYD_STK_LOT_TP, szCRN_YD_STK_LOT_CD, szYD_STK_LOT_CD);
					}else{
						//최하단 크레인작업재료의 산적Lot Type이 존재하지 않으므로 To위치 결정 실패.
						szLogMsg = "["+ szOperationName +"] 최하단 크레인작업재료["+szCRN_STL_NO+"]의 산적Lot Type["+szCRN_YD_STK_LOT_TP+"]이 TO위치평점판단을 위한 산적Lot Type이 아니므로 To위치 결정 실패";
						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						return YdConstant.RETN_CD_NOTEXIST;
					}
					
					szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]는 공베드가 아님 - 야드산적LOT코드비교결과["+szYD_STK_LOT_CD_GRADE+"]";
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else{
					// 비교대상재 길이 절대값 차이가 1000(100Cm)이상이면.
					szYD_STK_LOT_CD_GRADE	= "N1";
				}
			}else{
				// 적치베드 장입재 존재시 N1으로 셋팅.
				szYD_STK_LOT_CD_GRADE	= "N1";
			}
		}
		
		//-------------------------------------------------------------------------------
		
		//-------------------------------------------------------------------------------
		//	BRE Rule로부터 TO위치 평점 결정
		//-------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]에 대해 야드산적LOT코드비교결과["+szYD_STK_LOT_CD_GRADE+"], 슬라브폭편차비교결과["+szYD_STL_W_CMP_GRADE+"]를 사용해서 BRE Rule로부터 TO위치평점 호출 시작";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		JDTORecord jdtoRcd			= JDTORecordFactory.getInstance().create();
		boolean bRule			= GetBreRule0.getYDB005(szYD_STK_LOT_CD_GRADE, szYD_STL_W_CMP_GRADE, jdtoRcd);
		
		szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]에 대해 야드산적LOT코드비교결과["+szYD_STK_LOT_CD_GRADE+"], 슬라브폭편차비교결과["+szYD_STL_W_CMP_GRADE+"]를 사용해서 BRE Rule로부터 TO위치평점 호출 완료 - " + bRule;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		String szYD_LOC_SRCH_RNG_SEQ		= "";
		
		if( bRule ) {
			szYD_LOC_SRCH_RNG_SEQ			= ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_LOC_SRCH_RNG_SEQ");
		}else{
			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]에 대해 야드산적LOT코드비교결과["+szYD_STK_LOT_CD_GRADE+"], 슬라브폭편차비교결과["+szYD_STL_W_CMP_GRADE+"]를 사용해서 BRE Rule로부터 TO위치평점 호출 시 오류발생 - " + bRule;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			
			return YdConstant.RETN_CD_FAILURE;
		}
		
		szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]에 대해 야드산적LOT코드비교결과["+szYD_STK_LOT_CD_GRADE+"], 슬라브폭편차비교결과["+szYD_STL_W_CMP_GRADE+"]를 사용해서 BRE Rule로부터 TO위치평점결과["+szYD_LOC_SRCH_RNG_SEQ+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		recOutParam.setField("TO_LOC_GRADE", szYD_LOC_SRCH_RNG_SEQ);
		
		//-------------------------------------------------------------------------------
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 폭비교
	 * @param dblCRN_YD_MTL_W
	 * @param dblYD_MTL_W
	 * @param dblYD_CRN_TONG_W_TOL
	 * @return
	 */
	public static String getWidthCmpGrade(double dblCRN_YD_MTL_W, double dblYD_MTL_W, double dblYD_CRN_TONG_W_TOL ) {
		if( dblCRN_YD_MTL_W > dblYD_MTL_W ) {
			if( dblCRN_YD_MTL_W - dblYD_MTL_W <= dblYD_CRN_TONG_W_TOL ) {
				return "W1";
			}else{
				return "W2";
			}
		}else{
			return "W1";
		}
	}
	
	/**
	 * 길이비교
	 * @param dblCRN_YD_MTL_W
	 * @param dblYD_MTL_W
	 * @param 
	 * @return
	 */
	public static boolean getLengthCmpGrade(double dblCRN_YD_MTL_W, double dblYD_MTL_W) {
		
		ydUtils.putLog(szClassName, "길이비교메소드", "두 재료의 길이비교 절대값="+ Math.abs(dblCRN_YD_MTL_W - dblYD_MTL_W), YdConstant.DEBUG);
		
		if(Math.abs(dblCRN_YD_MTL_W - dblYD_MTL_W) > 1000) {
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 후판장입일련번호비교
	 * @param szYD_STK_LOT_TP
	 * @param szCRN_YD_STK_LOT_CD
	 * @param szYD_STK_LOT_CD
	 * @return
	 */
	public static String getPlateChgSeqCmp(String szYD_STK_LOT_TP, String szCRN_YD_STK_LOT_CD, String szYD_STK_LOT_CD) {
		String szOperationName				= "후판장입일련번호비교";
		String szMethodName					= "getPlateChgSeqCmp";
		String szLogMsg						= "";
		
		String szYD_STK_LOT_CD_GRADE = "";
		
		szLogMsg = "["+ szOperationName +"] 최하단크레인작업재료정보[야드산적LOT코드:"+szCRN_YD_STK_LOT_CD+"], 최상단의 재료정보[야드산적LOT TYPE:"+szYD_STK_LOT_TP+", 야드산적LOT코드:"+szYD_STK_LOT_CD+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		if( szYD_STK_LOT_TP.equals("SB") ) {
			szYD_STK_LOT_CD_GRADE			= "D1";
		}else if( szYD_STK_LOT_TP.equals("SY") ) {
			szYD_STK_LOT_CD_GRADE			= "D2";
		}else if( szYD_STK_LOT_TP.equals("SA") ) {
			szYD_STK_LOT_CD_GRADE			= "D3";
		}else if( szYD_STK_LOT_TP.equals("SG") ) {
			szYD_STK_LOT_CD_GRADE			= "D4";
		}else if( szYD_STK_LOT_TP.equals("SE") ) {
			szYD_STK_LOT_CD_GRADE			= "D5";
		}
		
		if( !szYD_STK_LOT_TP.equals("SP") ) {
			return szYD_STK_LOT_CD_GRADE;
		}
		
		if( szYD_STK_LOT_CD.length() > 2 && szCRN_YD_STK_LOT_CD.length() > 2 ) {
			
			String szREFUR_CHG_PLN_SERNO	 = szYD_STK_LOT_CD.substring(2);
			String szCRN_REFUR_CHG_PLN_SERNO = szCRN_YD_STK_LOT_CD.substring(2);
			
			szLogMsg = "["+ szOperationName +"] 최하단크레인작업재료정보[가열로장입예정일련번호:"+szCRN_REFUR_CHG_PLN_SERNO+"], 가열로장입예정일련번호:"+szREFUR_CHG_PLN_SERNO+"]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			double doubleREFUR_CHG_PLN_SERNO			= 0;
			double doubleCRN_REFUR_CHG_PLN_SERNO		= 0;
			
			try {
				doubleREFUR_CHG_PLN_SERNO			= Double.parseDouble(szREFUR_CHG_PLN_SERNO);
				doubleCRN_REFUR_CHG_PLN_SERNO		= Double.parseDouble(szCRN_REFUR_CHG_PLN_SERNO);
			}catch(NumberFormatException ex) {
				szLogMsg = "["+ szOperationName +"] 숫자로 변환 시 오류발생";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
				return "N1";
			}
			
			if( doubleCRN_REFUR_CHG_PLN_SERNO <= doubleREFUR_CHG_PLN_SERNO ) {
				if( doubleREFUR_CHG_PLN_SERNO - doubleCRN_REFUR_CHG_PLN_SERNO == 1  ) {
					szYD_STK_LOT_CD_GRADE			= "S1";
				}else if( doubleREFUR_CHG_PLN_SERNO - doubleCRN_REFUR_CHG_PLN_SERNO == 2  ) {
					szYD_STK_LOT_CD_GRADE			= "S2";
				}else if( doubleREFUR_CHG_PLN_SERNO - doubleCRN_REFUR_CHG_PLN_SERNO == 3  ) {
					szYD_STK_LOT_CD_GRADE			= "S3";
				}else if( doubleREFUR_CHG_PLN_SERNO - doubleCRN_REFUR_CHG_PLN_SERNO == 4  ) {
					szYD_STK_LOT_CD_GRADE			= "S4";
				}else if( doubleREFUR_CHG_PLN_SERNO - doubleCRN_REFUR_CHG_PLN_SERNO == 5  ) {
					szYD_STK_LOT_CD_GRADE			= "S5";
				}else if( doubleREFUR_CHG_PLN_SERNO - doubleCRN_REFUR_CHG_PLN_SERNO == 6  ) {
					szYD_STK_LOT_CD_GRADE			= "S6";	
				}else{
					szYD_STK_LOT_CD_GRADE			= "S7";
				}
			}else{
				szYD_STK_LOT_CD_GRADE			= "N1";
			}
		}else{
			szYD_STK_LOT_CD_GRADE			= "N1";
		}
		
		return szYD_STK_LOT_CD_GRADE;
	}
	
	/**
	 * 열연장입LOT번호비교
	 * @param szYD_STK_LOT_TP
	 * @param szCRN_YD_STK_LOT_CD
	 * @param szYD_STK_LOT_CD
	 * @return
	 */
	public static String getHrChgSeqCmp(String szYD_STK_LOT_TP, String szCRN_YD_STK_LOT_CD, String szYD_STK_LOT_CD) {
		String szOperationName				= "열연장입LOT번호비교";
		String szMethodName					= "getHrChgSeqCmp";
		String szLogMsg						= "";
		
		String szYD_STK_LOT_CD_GRADE = "";
		
		szLogMsg = "["+ szOperationName +"] 최하단크레인작업재료정보[야드산적LOT코드:"+szCRN_YD_STK_LOT_CD+"], 최상단의 재료정보[야드산적LOT TYPE:"+szYD_STK_LOT_TP+", 야드산적LOT코드:"+szYD_STK_LOT_CD+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		if( szYD_STK_LOT_TP.equals("SB") ) {
			szYD_STK_LOT_CD_GRADE			= "D1";
		}else if( szYD_STK_LOT_TP.equals("SY") ) {
			szYD_STK_LOT_CD_GRADE			= "D2";
		}else if( szYD_STK_LOT_TP.equals("SA") ) {
			szYD_STK_LOT_CD_GRADE			= "D3";
		}else if( szYD_STK_LOT_TP.equals("SG") ) {
			szYD_STK_LOT_CD_GRADE			= "D4";
		}else if( szYD_STK_LOT_TP.equals("SE") ) {
			szYD_STK_LOT_CD_GRADE			= "D5";
		}
		
		if( !szYD_STK_LOT_TP.equals("SL") ) {
			return szYD_STK_LOT_CD_GRADE;
		}
		
		if( szYD_STK_LOT_CD.length() > 2 && szCRN_YD_STK_LOT_CD.length() > 2 ) {
			String szREFUR_CHG_LOT_NO		= szYD_STK_LOT_CD.substring(2);
			String szCRN_REFUR_CHG_LOT_NO	= szCRN_YD_STK_LOT_CD.substring(2);
			
			szLogMsg = "["+ szOperationName +"] 최하단크레인작업재료정보[가열로장입LOT번호:"+szCRN_REFUR_CHG_LOT_NO+", 최상단의 재료정보[가열로장입LOT번호:"+szREFUR_CHG_LOT_NO;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			int intREFUR_CHG_LOT_NO				= 0;
			int intCRN_REFUR_CHG_LOT_NO			= 0;
			
			try {

				intREFUR_CHG_LOT_NO				= Integer.parseInt(szREFUR_CHG_LOT_NO);
				intCRN_REFUR_CHG_LOT_NO			= Integer.parseInt(szCRN_REFUR_CHG_LOT_NO);
				
			}catch(NumberFormatException ex) {
				szLogMsg = "["+ szOperationName +"] 숫자로 변환 시 오류발생";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
				return "N1";
			}
			
			if( intCRN_REFUR_CHG_LOT_NO == intREFUR_CHG_LOT_NO ) {
					szYD_STK_LOT_CD_GRADE			= "S1";
			}else{
				if( intCRN_REFUR_CHG_LOT_NO < intREFUR_CHG_LOT_NO ) {
					if( intREFUR_CHG_LOT_NO - intCRN_REFUR_CHG_LOT_NO == 1  ) {
						szYD_STK_LOT_CD_GRADE			= "S2";
					}else if( intREFUR_CHG_LOT_NO - intCRN_REFUR_CHG_LOT_NO == 2  ) {
						szYD_STK_LOT_CD_GRADE			= "S3";
					}else if( intREFUR_CHG_LOT_NO - intCRN_REFUR_CHG_LOT_NO == 3  ) {
						szYD_STK_LOT_CD_GRADE			= "S4";
					}else if( intREFUR_CHG_LOT_NO - intCRN_REFUR_CHG_LOT_NO == 4  ) {
						szYD_STK_LOT_CD_GRADE			= "S5";
					}else if( intREFUR_CHG_LOT_NO - intCRN_REFUR_CHG_LOT_NO == 5  ) {
						szYD_STK_LOT_CD_GRADE			= "S6";
					}else{
						szYD_STK_LOT_CD_GRADE			= "S7";
					}
				}else{
					szYD_STK_LOT_CD_GRADE			= "N1";
				}
			}
		}else{
			szYD_STK_LOT_CD_GRADE			= "N1";
		}
		
		return szYD_STK_LOT_CD_GRADE;
	}
	
	/**
	 * 정정대기산적LOT코드비교
	 * @param szYD_STK_LOT_TP
	 * @param szCRN_YD_STK_LOT_CD
	 * @param szYD_STK_LOT_CD
	 * @return
	 */
	public static String getCorrectionWaitCmp(String szYD_STK_LOT_TP, String szCRN_YD_STK_LOT_CD, String szYD_STK_LOT_CD) {
		String szOperationName				= "정정대기산적LOT코드비교";
		String szMethodName					= "getCorrectionWaitCmp";
		String szLogMsg						= "";
		
		String szYD_STK_LOT_CD_GRADE = "";
		
		szLogMsg = "["+ szOperationName +"] 최하단크레인작업재료정보[야드산적LOT코드:"+szCRN_YD_STK_LOT_CD+"], 최상단의 재료정보[야드산적LOT TYPE:"+szYD_STK_LOT_TP+", 야드산적LOT코드:"+szYD_STK_LOT_CD+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		if( szYD_STK_LOT_CD.length() >= 3 && szCRN_YD_STK_LOT_CD.length() >= 3 ) {
		
			if( szYD_STK_LOT_TP.equals("SA") ) {
				
				// 2011.12.01 윤재광 - 후판정정재 야드산적LOT기준 변경
				////////////////////////////////////////////
				
				if(szCRN_YD_STK_LOT_CD.length() >= 14 && szYD_STK_LOT_CD.length() >= 14){		// 크레인:후판정정재, 야드:후판정정재
					
					String szYD_AIM_RT_GP		= szYD_STK_LOT_CD.substring(3,5);
					String szORD_YEOJAE_GP		= szYD_STK_LOT_CD.substring(5,6);
					String szPROD_DUE_DATE		= szYD_STK_LOT_CD.substring(6);
					
					String szCRN_YD_AIM_RT_GP	= szCRN_YD_STK_LOT_CD.substring(3,5);
					String szCRN_ORD_YEOJAE_GP	= szCRN_YD_STK_LOT_CD.substring(5,6);
					String szCRN_PROD_DUE_DATE	= szCRN_YD_STK_LOT_CD.substring(6);
					
					szLogMsg = "["+ szOperationName +"] 최하단크레인작업재료정보[생산기한일:"+szCRN_YD_STK_LOT_CD.substring(5)+", 최상단의 재료정보[생산기한일:"+szYD_STK_LOT_CD.substring(5);
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					int intPROD_DUE_DATE				= 0;
					int intCRN_PROD_DUE_DATE			= 0;
					
					try {

						intPROD_DUE_DATE				= Integer.parseInt(szPROD_DUE_DATE);
						intCRN_PROD_DUE_DATE			= Integer.parseInt(szCRN_PROD_DUE_DATE);
						
					}catch(NumberFormatException ex) {
						szLogMsg = "["+ szOperationName +"] 숫자로 변환 시 오류발생";
						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
						return "N1";
					}
					if(szCRN_ORD_YEOJAE_GP.equals(szORD_YEOJAE_GP)){
						
						if(szCRN_YD_AIM_RT_GP.equals(szYD_AIM_RT_GP)){
						
							if( intCRN_PROD_DUE_DATE == intPROD_DUE_DATE ) {
									szYD_STK_LOT_CD_GRADE			= "S1";
							}else{
								if( intCRN_PROD_DUE_DATE < intPROD_DUE_DATE ) {
									if( intPROD_DUE_DATE - intCRN_PROD_DUE_DATE < 5  ) {
										szYD_STK_LOT_CD_GRADE			= "S2";
									}else if( intPROD_DUE_DATE - intCRN_PROD_DUE_DATE < 10  ) {
										szYD_STK_LOT_CD_GRADE			= "S3";
									}else if( intPROD_DUE_DATE - intCRN_PROD_DUE_DATE < 20  ) {
										szYD_STK_LOT_CD_GRADE			= "S4";
									}else{
										szYD_STK_LOT_CD_GRADE			= "S5";
									}
								}else{
									szYD_STK_LOT_CD_GRADE			= "N1";
								}
							}
						}else{
							szYD_STK_LOT_CD_GRADE			= "S6";
						}
					}else{
						szYD_STK_LOT_CD_GRADE			= "S7";
					}
				}else if(szCRN_YD_STK_LOT_CD.length() >= 14 && szYD_STK_LOT_CD.length() < 14){// 크레인:후판정정재, 야드:일반정정재		
						szYD_STK_LOT_CD_GRADE			= "S7";
				}else if(szCRN_YD_STK_LOT_CD.length() < 14 && szYD_STK_LOT_CD.length() >= 14){// 크레인:일반정정재, 야드:후판정정재		
						szYD_STK_LOT_CD_GRADE			= "S7";
				}else if(szCRN_YD_STK_LOT_CD.length() < 14 && szYD_STK_LOT_CD.length() < 14){ // 크레인:일반정정재, 야드:일반정정재	
				
					if( szYD_STK_LOT_CD.equals(szCRN_YD_STK_LOT_CD) ) {
						szYD_STK_LOT_CD_GRADE			= "S1";
					}else if( szYD_STK_LOT_CD.substring(0, 3).equals( szCRN_YD_STK_LOT_CD.substring(0, 3)) ) {
						szYD_STK_LOT_CD_GRADE			= "S2";
					}else{
						szYD_STK_LOT_CD_GRADE			= "S3";
					}
				}
				/////////////////
				
			}else if( szYD_STK_LOT_TP.equals("SY") ) {
				if( szYD_STK_LOT_CD.substring(0, 1).equals( szCRN_YD_STK_LOT_CD.substring(0, 1)) ) {
					szYD_STK_LOT_CD_GRADE			= "D1";
				}else{
					szYD_STK_LOT_CD_GRADE			= "D2";
				}
			}else if( szYD_STK_LOT_TP.equals("SB") ) {
				szYD_STK_LOT_CD_GRADE			= "D3";
			}else if( szYD_STK_LOT_TP.equals("SG") ) {
				szYD_STK_LOT_CD_GRADE			= "D4";
			}else if( szYD_STK_LOT_TP.equals("SE") ) {
				szYD_STK_LOT_CD_GRADE			= "D5";
			}else{
				szYD_STK_LOT_CD_GRADE			= "N1";
			}
			
		}else{
			szYD_STK_LOT_CD_GRADE			= "N1";
		}
		return szYD_STK_LOT_CD_GRADE;
	}
	
	/**
	 * 지시대기산적LOT코드비교
	 * @param szYD_STK_LOT_TP
	 * @param szCRN_YD_STK_LOT_CD
	 * @param szYD_STK_LOT_CD
	 * @return
	 */
	public static String getOrderWaitCmp(String szYD_STK_LOT_TP, String szCRN_YD_STK_LOT_CD, String szYD_STK_LOT_CD) {
		String szOperationName				= "지시대기산적LOT코드비교";
		String szMethodName					= "getOrderWaitCmp";
		String szLogMsg						= "";
		
		String szYD_STK_LOT_CD_GRADE = "";
		
		szLogMsg = "["+ szOperationName +"] 최하단크레인작업재료정보[야드산적LOT코드:"+szCRN_YD_STK_LOT_CD+"], 최상단의 재료정보[야드산적LOT TYPE:"+szYD_STK_LOT_TP+", 야드산적LOT코드:"+szYD_STK_LOT_CD+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		if( szYD_STK_LOT_CD.length() >= 3 && szCRN_YD_STK_LOT_CD.length() >= 3 ) {
		
			if( szYD_STK_LOT_TP.equals("SB") ) {
				/*
				       주문재  : 공장구분(2) + HCR구분(1) + 속성(2) + 주문두께그룹(2) + Coil/날판 폭그룹(3) + Slab폭그룹(3)
					여재    : 여재코드(2) + 행선(2) + 탈산방법(1) + Slab두께그룹(1) + Slab폭그룹(3)
					구입재 : 여재코드(2) + 행선(2) + Slab폭그룹(3) + 출강목표(7)
					판매재 : 계약번호행번(14)
				 */
				///////////////////////////////////////////////////
				szYD_STK_LOT_CD 	= FillToString(szYD_STK_LOT_CD,16);
				szCRN_YD_STK_LOT_CD = FillToString(szCRN_YD_STK_LOT_CD,16);
				
				if( szYD_STK_LOT_CD.equals(szCRN_YD_STK_LOT_CD) ) {
					szYD_STK_LOT_CD_GRADE			= "S1";
				}else if( szYD_STK_LOT_CD.substring(0, 13).equals( szCRN_YD_STK_LOT_CD.substring(0, 13)) ) {
					szYD_STK_LOT_CD_GRADE			= "S2";
				}else if( szYD_STK_LOT_CD.substring(0, 10).equals( szCRN_YD_STK_LOT_CD.substring(0, 10)) ) {
					szYD_STK_LOT_CD_GRADE			= "S3";	
				}else if( szYD_STK_LOT_CD.substring(0, 8).equals( szCRN_YD_STK_LOT_CD.substring(0, 8)) ) {
					szYD_STK_LOT_CD_GRADE			= "S4";		
				}else if( szYD_STK_LOT_CD.substring(0, 3).equals( szCRN_YD_STK_LOT_CD.substring(0, 3)) ) {
					szYD_STK_LOT_CD_GRADE			= "S5";
				}else if( szYD_STK_LOT_CD.substring(0, 1).equals( szCRN_YD_STK_LOT_CD.substring(0, 1)) ) {
					szYD_STK_LOT_CD_GRADE			= "S6";
				}else{
					szYD_STK_LOT_CD_GRADE			= "S7";
				}
				///////////////////////////////////////////////////
			}else if( szYD_STK_LOT_TP.equals("SY") ) {
				if( szYD_STK_LOT_CD.substring(0, 3).equals( szCRN_YD_STK_LOT_CD.substring(0, 3)) ) {
					szYD_STK_LOT_CD_GRADE			= "D1";
				}else if( szYD_STK_LOT_CD.substring(1, 3).equals( szCRN_YD_STK_LOT_CD.substring(1, 3)) ) {
					szYD_STK_LOT_CD_GRADE			= "D2";
				}else{
					szYD_STK_LOT_CD_GRADE			= "D3";
				}
			}else if( szYD_STK_LOT_TP.equals("SA") ) {
				if( szYD_STK_LOT_CD.substring(0, 3).equals( szCRN_YD_STK_LOT_CD.substring(0, 3)) ) {
					szYD_STK_LOT_CD_GRADE			= "D1";
				}else if( szYD_STK_LOT_CD.substring(1, 3).equals( szCRN_YD_STK_LOT_CD.substring(1, 3)) ) {
					szYD_STK_LOT_CD_GRADE			= "D2";
				}else{
					szYD_STK_LOT_CD_GRADE			= "D3";
				}
			}else if( szYD_STK_LOT_TP.equals("SG") ) {
				szYD_STK_LOT_CD_GRADE			= "D4";
			}else if( szYD_STK_LOT_TP.equals("SE") ) {
				szYD_STK_LOT_CD_GRADE			= "D5";
			}else{
				szYD_STK_LOT_CD_GRADE			= "N1";
			}
			
		}else{
			szYD_STK_LOT_CD_GRADE			= "N1";
		}
		
		return szYD_STK_LOT_CD_GRADE;
	}
	
	/**
	 * 충당대기산적LOT코드비교
	 * @param szYD_STK_LOT_TP
	 * @param szCRN_YD_STK_LOT_CD
	 * @param szYD_STK_LOT_CD
	 * @return
	 */
	public static String getMatchWaitCmp(String szYD_STK_LOT_TP, String szCRN_YD_STK_LOT_CD, String szYD_STK_LOT_CD) {
		String szOperationName				= "충당대기산적LOT코드비교";
		String szMethodName					= "getMatchWaitCmp";
		String szLogMsg						= "";
		
		String szYD_STK_LOT_CD_GRADE = "";
		
		szLogMsg = "["+ szOperationName +"] 최하단크레인작업재료정보[야드산적LOT코드:"+szCRN_YD_STK_LOT_CD+"], 최상단의 재료정보[야드산적LOT TYPE:"+szYD_STK_LOT_TP+", 야드산적LOT코드:"+szYD_STK_LOT_CD+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		if( szYD_STK_LOT_CD.length() >= 3 && szCRN_YD_STK_LOT_CD.length() >= 3 ) {
		
			if( szYD_STK_LOT_TP.equals("SY") ) {
				/*
				       주문재  : 공장구분(2) + HCR구분(1) + 속성(2) + 주문두께그룹(2) + Coil/날판 폭그룹(3) + Slab폭그룹(3)
					여재    : 여재코드(2) + 행선(2) + 탈산방법(1) + Slab두께그룹(1) + Slab폭그룹(3)
					판매재 : 계약번호행번(14)
				 */
				///////////////////////////////////////////////////
				szYD_STK_LOT_CD 	= FillToString(szYD_STK_LOT_CD,12);
				szCRN_YD_STK_LOT_CD = FillToString(szCRN_YD_STK_LOT_CD,12);
				
				if( szYD_STK_LOT_CD.equals(szCRN_YD_STK_LOT_CD) ) {
					szYD_STK_LOT_CD_GRADE			= "S1";
				}else if( szYD_STK_LOT_CD.substring(0, 9).equals( szCRN_YD_STK_LOT_CD.substring(0, 9)) ) {
					szYD_STK_LOT_CD_GRADE			= "S2";
				}else if( szYD_STK_LOT_CD.substring(0, 8).equals( szCRN_YD_STK_LOT_CD.substring(0, 8)) ) {
					szYD_STK_LOT_CD_GRADE			= "S3";	
				}else if( szYD_STK_LOT_CD.substring(0, 7).equals( szCRN_YD_STK_LOT_CD.substring(0, 7)) ) {
					szYD_STK_LOT_CD_GRADE			= "S4";		
				}else if( szYD_STK_LOT_CD.substring(0, 3).equals( szCRN_YD_STK_LOT_CD.substring(0, 3)) ) {
					szYD_STK_LOT_CD_GRADE			= "S5";
				}else if( szYD_STK_LOT_CD.substring(0, 1).equals( szCRN_YD_STK_LOT_CD.substring(0, 1)) ) {
					szYD_STK_LOT_CD_GRADE			= "S6";
				}else{
					szYD_STK_LOT_CD_GRADE			= "S7";
				}
				///////////////////////////////////////////////////
			}else if( szYD_STK_LOT_TP.equals("SA") ) {
				if( szYD_STK_LOT_CD.substring(0, 3).equals( szCRN_YD_STK_LOT_CD.substring(0, 3)) ) {
					szYD_STK_LOT_CD_GRADE			= "D1";
				}else if( szYD_STK_LOT_CD.substring(0, 1).equals( szCRN_YD_STK_LOT_CD.substring(0, 1)) ) {
					szYD_STK_LOT_CD_GRADE			= "D2";
				}else{
					szYD_STK_LOT_CD_GRADE			= "D3";
				}
			}else if( szYD_STK_LOT_TP.equals("SB") ) {
				if( szYD_STK_LOT_CD.substring(0, 3).equals( szCRN_YD_STK_LOT_CD.substring(0, 3)) ) {
					szYD_STK_LOT_CD_GRADE			= "D4";
				}else if( szYD_STK_LOT_CD.substring(1, 3).equals( szCRN_YD_STK_LOT_CD.substring(1, 3)) ) {
					szYD_STK_LOT_CD_GRADE			= "D5";
				}else{
					szYD_STK_LOT_CD_GRADE			= "D6";
				}
			}else if( szYD_STK_LOT_TP.equals("SG") ) {
				szYD_STK_LOT_CD_GRADE			= "D7";
			}else if( szYD_STK_LOT_TP.equals("SE") ) {
				szYD_STK_LOT_CD_GRADE			= "D8";
			}else{
				szYD_STK_LOT_CD_GRADE			= "N1";
			}
			
		}else{
			szYD_STK_LOT_CD_GRADE			= "N1";
		}
		return szYD_STK_LOT_CD_GRADE;
	}
	
	
	/**
	 * 외판대기산적LOT코드비교
	 * @param szYD_STK_LOT_TP
	 * @param szCRN_YD_STK_LOT_CD
	 * @param szYD_STK_LOT_CD
	 * @return
	 */
	public static String getOutPlWaitCmp(String szYD_STK_LOT_TP, String szCRN_YD_STK_LOT_CD, String szYD_STK_LOT_CD) {
		String szOperationName				= "외판대기산적LOT코드비교";
		String szMethodName					= "getOutPlWaitCmp";
		String szLogMsg						= "";
		
		String szYD_STK_LOT_CD_GRADE = "";
		
		szLogMsg = "["+ szOperationName +"] 최하단크레인작업재료정보[야드산적LOT코드:"+szCRN_YD_STK_LOT_CD+"], 최상단의 재료정보[야드산적LOT TYPE:"+szYD_STK_LOT_TP+", 야드산적LOT코드:"+szYD_STK_LOT_CD+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		if( szYD_STK_LOT_CD.length() >= 1 && szCRN_YD_STK_LOT_CD.length() >= 1 ) {
		
			if( szYD_STK_LOT_TP.equals("SG") ) {
				if( szYD_STK_LOT_CD.equals(szCRN_YD_STK_LOT_CD) ) {
					szYD_STK_LOT_CD_GRADE			= "S1";
				}else if( szYD_STK_LOT_CD.substring(0, 1).equals( szCRN_YD_STK_LOT_CD.substring(0, 1)) ) {
					szYD_STK_LOT_CD_GRADE			= "S2";
				}else{
					szYD_STK_LOT_CD_GRADE			= "S3";
				}
			}else if( szYD_STK_LOT_TP.equals("SA") ) {
				if( szYD_STK_LOT_CD.substring(0, 1).equals( szCRN_YD_STK_LOT_CD.substring(0, 1)) ) {
					szYD_STK_LOT_CD_GRADE			= "D1";
				}else{ 
					szYD_STK_LOT_CD_GRADE			= "D2";
				}
			}else if( szYD_STK_LOT_TP.equals("SB") ) {
				szYD_STK_LOT_CD_GRADE			= "D3";
			}else if( szYD_STK_LOT_TP.equals("SE") ) {
				szYD_STK_LOT_CD_GRADE			= "D4";
			}else{
				szYD_STK_LOT_CD_GRADE			= "N1";
			}
			
		}else{
			szYD_STK_LOT_CD_GRADE			= "N1";
		}
		
		return szYD_STK_LOT_CD_GRADE;
	}
	
	/**
	 * 이송대기산적LOT코드비교
	 * @param szYD_STK_LOT_TP
	 * @param szCRN_YD_STK_LOT_CD
	 * @param szYD_STK_LOT_CD
	 * @return
	 */
	public static String getFtMvWaitCmp(String szYD_STK_LOT_TP, String szCRN_YD_STK_LOT_CD, String szYD_STK_LOT_CD) {
		String szOperationName				= "이송대기산적LOT코드비교";
		String szMethodName					= "getFtMvWaitCmp";
		String szLogMsg						= "";
		
		String szYD_STK_LOT_CD_GRADE = "";
		
		szLogMsg = "["+ szOperationName +"] 최하단크레인작업재료정보[야드산적LOT코드:"+szCRN_YD_STK_LOT_CD+"], 최상단의 재료정보[야드산적LOT TYPE:"+szYD_STK_LOT_TP+", 야드산적LOT코드:"+szYD_STK_LOT_CD+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		if( szYD_STK_LOT_CD.length() >= 3 && szCRN_YD_STK_LOT_CD.length() >= 3 ) {
		
			if( szYD_STK_LOT_TP.equals("SE") ) {
				if( szYD_STK_LOT_CD.equals(szCRN_YD_STK_LOT_CD) ) {
					szYD_STK_LOT_CD_GRADE			= "S1";
				}else if( szYD_STK_LOT_CD.substring(1, 3).equals( szCRN_YD_STK_LOT_CD.substring(1, 3)) ) {
					szYD_STK_LOT_CD_GRADE			= "S2";
				}else{
					szYD_STK_LOT_CD_GRADE			= "S3";
				}
			}else if( szYD_STK_LOT_TP.equals("SY") ) {
				szYD_STK_LOT_CD_GRADE			= "D1";
			}else if( szYD_STK_LOT_TP.equals("SA") ) {
				szYD_STK_LOT_CD_GRADE			= "D2";
			}else if( szYD_STK_LOT_TP.equals("SB") ) {
				szYD_STK_LOT_CD_GRADE			= "D3";
			}else if( szYD_STK_LOT_TP.equals("SG") ) {
				szYD_STK_LOT_CD_GRADE			= "D4";
			}else{
				szYD_STK_LOT_CD_GRADE			= "N1";
			}
			
		}else{
			szYD_STK_LOT_CD_GRADE			= "N1";
		}
		
		return szYD_STK_LOT_CD_GRADE;
	}
	
	/**
	 * YJK
     * 일정 길이만큼 뒤에 공백을 채운다.
     * @String in_strValue, int in_intLength 
     */ 
	public static String FillToString(String in_strValue, int in_intLength )
	{
		
		String in_strRet = "";
   		try{
			if (CommonUtil.getLength(in_strValue) > in_intLength){
				in_strRet = CommonUtil.substr(in_strValue, 0, in_intLength);
			}else{
				in_strRet = in_strValue + MakeSpace(in_intLength - CommonUtil.getLength(in_strValue)," ");
			}
		}catch(Exception e){
			in_strRet = in_strValue;
		}
		
		return in_strRet;
    }
	
	/**
     * YJK
     * in_intLength 만큼 공백를 생성한다.
     * @int in_intLength
     */
	public static String MakeSpace(int in_intLength,String sVal)
	{
		String in_strValue = "";

		for(int j=0; j < in_intLength ; j++)
		{
			in_strValue +=sVal;
		}
		return in_strValue;
    }
	
	/**
	 * 통합슬라브야드TO위치평점판단
	 * @param recToBed
	 * @param recLowestCrnWrkMtl
	 * @param recStkHighestLyr
	 * @param recOutParam
	 * @return
	 * @throws JDTOException
	 */
	public static String procToLocGradeTestForSlabTot(
			JDTORecord recToBed								/* TO위치베드 정보 */
			, JDTORecord recLowestCrnWrkMtl					/* 최하단의 크레인작업재료 정보 */
			, JDTORecord recStkHighestLyr					/* TO위치베드의 최상단의 적치중이거나 권하대기인 재료정보, null이면 공베드 */
			, JDTORecord recOutParam						/* TO위치 평점 */
			) throws JDTOException {
		String szRtnMsg						= YdConstant.RETN_CD_SUCCESS;
		String szOperationName				= "통합슬라브야드TO위치평점판단";
		String szMethodName					= "procToLocGradeTestForSlabTot";
		String szLogMsg						= "";
		
//		JDTORecord		recInTemp			= null;
//		JDTORecordSet	rsTemp				= null;
		
		//String szYD_STK_LOT_TP_GRADE		= "";						//산적LOT TYPE비교 결과
//		String szYD_STK_LOT_CD_GRADE		= "";						//산적LOT CODE비교 결과
//		String szYD_STL_W_CMP_GRADE			= "";						//슬라브폭비교결과
		
		String szTO_LOC_GRADE				= "";
		
		String szYD_STK_COL_GP				= null;
		String szYD_STK_BED_NO				= null;
		
		//적치단의 최상단 재료의 정보
		String szYD_STK_LOT_TP				= null;						//적치단의 최상단 재료의 산적LOT TYPE
		String szYD_STK_LOT_CD				= null;						//적치단의 최상단 재료의산적LOT CODE
		//double dblYD_MTL_W					= 0;						//적치단의 최상단 재료의 폭
		String szSTL_NO						= "";						//적치단의 최상단 재료번호
		
		//최하단 크레인작업재료 정보
		String szCRN_YD_STK_LOT_TP			= null;						//최하단 크레인작업재료 산적LOT TYPE
		String szCRN_YD_STK_LOT_CD			= null;						//최하단 크레인작업재료 산적LOT CODE
		//double dblCRN_YD_MTL_W				= 0;						//최하단 크레인작업재료폭
		String szCRN_STL_NO					= null;						//최하단 크레인작업재료번호
		
		//크레인설비ID
		//String szYD_EQP_ID					= null;
		//double dblYD_CRN_TONG_W_TOL			= 0;
		
		//-------------------------------------------------------------------------------
		// TO위치베드의 적치가능 유무 판단.
		//-------------------------------------------------------------------------------
		
		//szYD_EQP_ID							= ydDaoUtils.paraRecChkNull(recLowestCrnWrkMtl, "YD_EQP_ID");
		
		szYD_STK_COL_GP						= ydDaoUtils.paraRecChkNull(recToBed, "YD_STK_COL_GP");
		szYD_STK_BED_NO						= ydDaoUtils.paraRecChkNull(recToBed, "YD_STK_BED_NO");
		
		szCRN_YD_STK_LOT_TP					= ydDaoUtils.paraRecChkNull(recLowestCrnWrkMtl, "YD_STK_LOT_TP");						//최하단 크레인작업재료 산적LOT TYPE
		szCRN_YD_STK_LOT_CD					= ydDaoUtils.paraRecChkNull(recLowestCrnWrkMtl, "YD_STK_LOT_CD");						//최하단 크레인작업재료 산적LOT CODE
		//dblCRN_YD_MTL_W						= ydDaoUtils.paraRecChkNullDouble(recLowestCrnWrkMtl, "YD_MTL_W");						//최하단 크레인작업재료폭
		szCRN_STL_NO						= ydDaoUtils.paraRecChkNull(recLowestCrnWrkMtl, "STL_NO");								//최하단 크레인작업재료폭
		
		
		if( recStkHighestLyr != null ) {					//NULL이 아니면 적치중이거나 권하대기인 재료가 존재
			
			szYD_STK_LOT_TP								= ydDaoUtils.paraRecChkNull(recStkHighestLyr, "YD_STK_LOT_TP");				//적치단의 최상단 재료의 산적LOT TYPE
			szYD_STK_LOT_CD								= ydDaoUtils.paraRecChkNull(recStkHighestLyr, "YD_STK_LOT_CD");				//적치단의 최상단 재료의산적LOT CODE
			//dblYD_MTL_W									= ydDaoUtils.paraRecChkNullDouble(recStkHighestLyr, "YD_MTL_W");			//적치단의 최상단 재료의 폭
			szSTL_NO									= ydDaoUtils.paraRecChkNull(recStkHighestLyr, "STL_NO");					//적치단의 최상단 재료번호
			
		}else{
			
			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"]는 공베드임";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
		}
		
		//-------------------------------------------------------------------------------
		
		//-------------------------------------------------------------------------------
		//	최하단 크레인작업재료와 적치단의 최상단 재료의 산적LOT코드 비교
		//-------------------------------------------------------------------------------
		
		if( szCRN_YD_STK_LOT_TP.equals("SA") 
			|| szCRN_YD_STK_LOT_TP.equals("SB")
			|| szCRN_YD_STK_LOT_TP.equals("SY")
			|| szCRN_YD_STK_LOT_TP.equals("SG")
			|| szCRN_YD_STK_LOT_TP.equals("SE")
		) {
			if( szCRN_YD_STK_LOT_TP.equals(szYD_STK_LOT_TP)  ) {
				if( szCRN_YD_STK_LOT_CD.length() >=3 && szYD_STK_LOT_CD.length() >=3 ) {
					if( szCRN_YD_STK_LOT_CD.substring(0, 3).equals(szYD_STK_LOT_CD.substring(0, 3))) {
						szTO_LOC_GRADE				= "1";
						
						szLogMsg = "["+ szOperationName +"] 동일한 산적LOT타입["+szCRN_YD_STK_LOT_TP+"]인 경우 최하단 크레인작업재료["+szCRN_STL_NO+"]의 산적LOT코드["+szCRN_YD_STK_LOT_CD+"]과 적치단의 최상단 재료["+szSTL_NO+"]의 산적LOT코드["+szYD_STK_LOT_CD+"]의 앞3자리가 동일 - 등급["+szTO_LOC_GRADE+"]";
						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
					}else if( szCRN_YD_STK_LOT_CD.substring(1, 3).equals(szYD_STK_LOT_CD.substring(1, 3))) {
						szTO_LOC_GRADE				= "2";
						
						szLogMsg = "["+ szOperationName +"] 동일한 산적LOT타입["+szCRN_YD_STK_LOT_TP+"]인 경우 최하단 크레인작업재료["+szCRN_STL_NO+"]의 산적LOT코드["+szCRN_YD_STK_LOT_CD+"]과 적치단의 최상단 재료["+szSTL_NO+"]의 산적LOT코드["+szYD_STK_LOT_CD+"]의 2자리부터 두개의 문자 동일 - 등급["+szTO_LOC_GRADE+"]";
						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}else{
						szTO_LOC_GRADE				= "4";
						
						szLogMsg = "["+ szOperationName +"] 동일한 산적LOT타입["+szCRN_YD_STK_LOT_TP+"]인 경우 최하단 크레인작업재료["+szCRN_STL_NO+"]의 산적LOT코드["+szCRN_YD_STK_LOT_CD+"]과 적치단의 최상단 재료["+szSTL_NO+"]의 산적LOT코드["+szYD_STK_LOT_CD+"]의 앞3문자가 동일하지 않음 - 등급["+szTO_LOC_GRADE+"]";
						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}
				}else{
					szTO_LOC_GRADE				= "4";
					
					szLogMsg = "["+ szOperationName +"] 동일한 산적LOT타입["+szCRN_YD_STK_LOT_TP+"]인 경우 최하단 크레인작업재료["+szCRN_STL_NO+"]의 산적LOT코드["+szCRN_YD_STK_LOT_CD+"]과 적치단의 최상단 재료["+szSTL_NO+"]의 산적LOT코드["+szYD_STK_LOT_CD+"]의 길이가 3자리미만입니다. - 등급["+szTO_LOC_GRADE+"]";
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
			}else{
				if( szCRN_YD_STK_LOT_CD.length() >=3 && szYD_STK_LOT_CD.length() >=3 ) {
					if( szCRN_YD_STK_LOT_CD.substring(1, 3).equals(szYD_STK_LOT_CD.substring(1, 3))) {
						szTO_LOC_GRADE				= "5";
						
						szLogMsg = "["+ szOperationName +"] 다른 산적LOT타입["+szCRN_YD_STK_LOT_TP+"]인 경우 최하단 크레인작업재료["+szCRN_STL_NO+"]의 산적LOT코드["+szCRN_YD_STK_LOT_CD+"]과 적치단의 최상단 재료["+szSTL_NO+"]의 산적LOT코드["+szYD_STK_LOT_CD+"]의 2자리부터 두개의 문자 동일 - 등급["+szTO_LOC_GRADE+"]";
						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}else{
						szTO_LOC_GRADE				= "6";
						
						szLogMsg = "["+ szOperationName +"] 다른 산적LOT타입["+szCRN_YD_STK_LOT_TP+"]인 경우 최하단 크레인작업재료["+szCRN_STL_NO+"]의 산적LOT코드["+szCRN_YD_STK_LOT_CD+"]과 적치단의 최상단 재료["+szSTL_NO+"]의 산적LOT코드["+szYD_STK_LOT_CD+"]의 2자리부터 두개의 문자 동일하지 않은 경우 - 등급["+szTO_LOC_GRADE+"]";
						ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}
				}else{
					szTO_LOC_GRADE				= "7";
					
					szLogMsg = "["+ szOperationName +"] 다른 산적LOT타입["+szCRN_YD_STK_LOT_TP+"]인 경우 최하단 크레인작업재료["+szCRN_STL_NO+"]의 산적LOT코드["+szCRN_YD_STK_LOT_CD+"]과 적치단의 최상단 재료["+szSTL_NO+"]의 산적LOT코드["+szYD_STK_LOT_CD+"]의 길이가 3자리미만입니다. - 등급["+szTO_LOC_GRADE+"]";
					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
			}
		}else{
			szTO_LOC_GRADE				= "7";
			
			szLogMsg = "["+ szOperationName +"] 최하단 크레인작업재료["+szCRN_STL_NO+"]의 산적LOT타입["+szCRN_YD_STK_LOT_TP+"]이 존재하지 않거나 평점판단을 위한 타입이 아닙니다. - 등급["+szTO_LOC_GRADE+"]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}
		
		recOutParam.setField("TO_LOC_GRADE", szTO_LOC_GRADE);
			
		//-------------------------------------------------------------------------------
		
		return YdConstant.RETN_CD_SUCCESS;
	}

	/**
	 * 동일한PilingCode베드검색 신 
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getBedWithSamePilingCdNew(String szFROM_DONG,String szTO_DONG, String szYD_CRN_GRAB_TP, JDTORecord recInPara , List listToLoc) throws JDTOException {
		/*
		 * 최상단 재료의 Piling Code와 동일한 Piling Code를 가진 베드 검색
		 * szYD_PILING_CD : Piling Code
		 * szYD_STK_BED_NO : 권상지시위치의 베드 번지
		 * szSPAN_ORDER : 검색할 스판의 순서
		 * 		1) 1, 2, 3, 4 - 해당 스판만 검색
		 * 		2) 1234 - 04, 05, 0642스판을 먼저 검색 후 07 스판을 검색
		 * 		3) 3412 - 07스판을 먼저 검색 후 04, 05, 06 스판을 검색
		 * szSCAN_DIR	: 검색할 방향
		 * 		1) R2P : RT -> PT(차량)방향
		 * 		2) P2R : PT(차량) -> RT방향
		 * 
		 */
		String szMethodName				= "getBedWithSamePilingCdNew";
		String szOperationName			= "동일한PilingCode베드검색_NEW";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		String szYD_TO_LOC_GUIDE		= null;
		String szYD_PILING_CD			= null;
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;

		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;

		String szALT_FLAG               = null;
		String szAL_FROM                = null;
		String szAL_TO                  = null;
		
		JDTORecord		recPara			= null;
		JDTORecordSet	rsResult		= null;
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "동일한PilingCode베드검색_NEW(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		ydUtils.displayRecord(szOperationName, recInPara);
		
		szYD_TO_LOC_GUIDE 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");						//사용자지정위치(권하지시위치)
		szYD_PILING_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_PILING_CD");							//크레인작업 최하단재료의 Piling Code
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시위치 - 적치베드

		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		
		szALT_FLAG          = ydDaoUtils.paraRecChkNull(recInPara, "ALT_FLAG");							//대체범위 추가 필터 여부 플래그
		szAL_FROM           = ydDaoUtils.paraRecChkNull(recInPara, "AL_FROM");							//대체범위 FROM
		szAL_TO             = ydDaoUtils.paraRecChkNull(recInPara, "AL_TO");							//대체범위 TO
		
		//----------------------------------------------------------------------------------------------------------------------
		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
			
			szYD_GP			= szYD_TO_LOC_GUIDE.substring(0, 1);
			szYD_BAY_GP		= szYD_TO_LOC_GUIDE.substring(1, 2);
			
		}else{
			//----------------------------------------------------------------------------------------------------------------------
			//	==>권상지시위치를 사용. : 2009.12.22
			//----------------------------------------------------------------------------------------------------------------------
			szYD_GP			= szYD_STK_COL_GP.substring(0, 1);
			szYD_BAY_GP		= szYD_STK_COL_GP.substring(1, 2);
			
			szLogMsg = "["+ szOperationName +"] 사용자지정위치(입고예정위치)가 존재하지 않으므로 권상지시위치를 사용";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
		}
		//SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_PILING_CD
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
		recPara.setField("YD_PILING_CD", 	szYD_PILING_CD);
		recPara.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
		recPara.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);

		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
		
		recPara.setField("FR_YD_STK_COL_GP", 	szYD_STK_COL_GP);
		recPara.setField("FR_YD_STK_BED_NO", 	szYD_STK_BED_NO);
		
		/*사용안함
		if("X".equals(szYD_CRN_GRAB_TP)){
			if( szYD_STK_BED_NO.equals("") ) {
				recPara.setField("BED_SEARCH_GP", 	"1");		//Full Scan
			}else if(szYD_STK_BED_NO.equals("01")){
				recPara.setField("BED_SEARCH_GP", 	"2");		// 01, 02번지 Scan
			}else if(szYD_STK_BED_NO.equals("03")){
				recPara.setField("BED_SEARCH_GP", 	"3");		// 02, 03번지 Scan
			}
		} else {
			recPara.setField("BED_SEARCH_GP", 	"1");		//Full Scan
		}
		*/
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 Piling Code를 가진 최상단재료가 있는 모든 베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 동일한 Piling Code["+szYD_PILING_CD+"]를 가진 최상단재료가 있는 모든 베드 정보 조회 시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 단 정보 조회 - 동일한 Piling Code를 가진 최상단 재료가 있는 모든 베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		if(szALT_FLAG.equals("Y")){  //고장등록에 따른 대체범위 추가 필터 사용여부
			recPara.setField("AL_FROM", 	szYD_GP + szYD_BAY_GP +szAL_FROM);  //대체 범위 추가 필터 셋팅
			recPara.setField("AL_TO",    	szYD_GP + szYD_BAY_GP +szAL_TO);    //대체 범위 추가 필터 셋팅
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatioReNewWithAlFrTo*/
			szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 634);
		}
		else{ //기존 로직
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatioReNew*/
			szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 624);			
		}


		szLogMsg = "["+ szOperationName +"] 동일한 Piling Code를 가진 최상단 재료가 있는 모든 베드 정보 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------

		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// srchNconvRecord2Vo argument 에 logId 항목 추가 개선
//			srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD);
			srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		}
				
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 동일한 Piling Code["+szYD_PILING_CD+"]를 가진 최상단재료가 있는 모든 베드 정보 조회 완료 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "동일한PilingCode베드검색_NEW(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
	}	
	
	/**
	 * 혼적베드검색(주작업:신이적)
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getBedWithSimilarGpNew( String sStlNo ,String szFROM_DONG,String szTO_DONG, String szYD_CRN_GRAB_TP, JDTORecord recInPara , List listToLoc) throws JDTOException {
		/*
		 * szYD_STK_BED_NO : 권상지시위치의 베드 번지
		 * szSPAN_ORDER : 검색할 스판의 순서
		 * 		1) 1, 2, 3, 4 - 해당 스판만 검색
		 * 		2) 1234 - 04, 05, 0642스판을 먼저 검색 후 07 스판을 검색
		 * 		3) 3412 - 07스판을 먼저 검색 후 04, 05, 06 스판을 검색
		 * szSCAN_DIR	: 검색할 방향
		 * 		1) R2P : RT -> PT(차량)방향
		 * 		2) P2R : PT(차량) -> RT방향
		 * 
		 */
		String szMethodName				= "getBedWithSimilarGpNew";
		String szOperationName			= "신혼적베드검색";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		String szYD_TO_LOC_GUIDE		= null;
		String szYD_MTL_L_GP			= null;
		String szYD_MTL_W_GP			= null;
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		
		String szALT_FLAG               = null;
		String szAL_FROM                = null;
		String szAL_TO                  = null;

		JDTORecord		recPara			= null;
		JDTORecord		recRecord		= null;
		
		JDTORecordSet	rsResult		= null;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(recInPara, "T");  // JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "신혼적베드검색(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		ydUtils.displayRecord(szOperationName, recInPara);
		
		szYD_TO_LOC_GUIDE 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");						//사용자지정위치
		szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시위치 - 적치베드

		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		
		szALT_FLAG 		= ydDaoUtils.paraRecChkNull(recInPara, "ALT_FLAG");							        //대체크레인 추가범위 필터 사용여부
		szAL_FROM 		= ydDaoUtils.paraRecChkNull(recInPara, "AL_FROM");							        //대체크레인 FROM 범위
		szAL_TO 		= ydDaoUtils.paraRecChkNull(recInPara, "AL_TO");							        //대체크레인 TO 범위
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
		
			szYD_GP			= szYD_TO_LOC_GUIDE.substring(0, 1);
			szYD_BAY_GP		= szYD_TO_LOC_GUIDE.substring(1, 2);
			//SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_PILING_CD
		}else{
			//----------------------------------------------------------------------------------------------------------------------
			//	==>권상지시위치를 사용. : 2009.12.22
			//----------------------------------------------------------------------------------------------------------------------
			
			szYD_GP			= szYD_STK_COL_GP.substring(0, 1);
			szYD_BAY_GP		= szYD_STK_COL_GP.substring(1, 2);
			
			szLogMsg = "["+ szOperationName +"] 사용자지정위치(입고예정위치)가 존재하지 않으므로 권상지시위치를 사용";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
		}
		
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP"			, szYD_GP);
		recPara.setField("YD_BAY_GP"		, szYD_BAY_GP);
		recPara.setField("YD_STK_BED_L_GP"	, szYD_MTL_L_GP.substring(0, 1));
		recPara.setField("YD_STK_BED_W_GP"	, szYD_MTL_W_GP.substring(0, 1));
		recPara.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
		recPara.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);

		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
	
		/* 사용안함
		if("X".equals(szYD_CRN_GRAB_TP)){
			if( szYD_STK_BED_NO.equals("") ) {
				recPara.setField("BED_SEARCH_GP", 	"1");	//Full Scan
			}else if(szYD_STK_BED_NO.equals("01")){
				recPara.setField("BED_SEARCH_GP", 	"2");	// 01, 02번지 Scan
			}else if(szYD_STK_BED_NO.equals("03")){
				recPara.setField("BED_SEARCH_GP", 	"3");	// 02, 03번지 Scan
			}
		} else {
			recPara.setField("BED_SEARCH_GP", 	"1");	//Full Scan
		}
		*/
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	혼적베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 혼적베드 정보 조회 시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 베드 정보 조회 - 혼적베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		recPara.setField("STL_NO",	sStlNo);
		
		recRecord = JDTORecordFactory.getInstance().create();
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		
		//PIDEV_S :병행가동용:PI_YD
		recPara.setField("PI_YD",    	szYD_GP);
		
		/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getOsPilingCdByStlNo_PIDEV*/
		szRtnMsg = DaoManager.getYdStock(recPara, rsResult, 213);			
		
		rsResult.first();
		recRecord = rsResult.getRecord();
		
		String sGbn			= ydDaoUtils.paraRecChkNull(recRecord, "GBN");
		String sYdMtlWGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_MTL_W_GP");
		String sYdMtlLGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_MTL_L_GP");
		String sYdPilingCd	= ydDaoUtils.paraRecChkNull(recRecord, "YD_PILING_CD");
		String sDemanderCd	= ydDaoUtils.paraRecChkNull(recRecord, "DEMANDER_CD");
		String sYdStkBedLGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_STK_BED_L_GP");
		String sYdStkBedWGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_STK_BED_W_GP");
		String sShipCd		= ydDaoUtils.paraRecChkNull(recRecord, "SHIP_CD");
		String sDelTermCd	= ydDaoUtils.paraRecChkNull(recRecord, "DELIVER_TERM_CD"); // 검색
		String sCustCd		= ydDaoUtils.paraRecChkNull(recRecord, "CUST_CD");
		String sDetailArrCd	= ydDaoUtils.paraRecChkNull(recRecord, "DETAIL_ARR_CD");		
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
//		ydUtils.putLog(szClassName, szMethodName, "sGbn=>"+sGbn, 				YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sYdMtlWGp=>"+sYdMtlWGp, 		YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sYdMtlLGp=>"+sYdMtlLGp, 		YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sYdPilingCd=>"+sYdPilingCd, 	YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sDemanderCd=>"+sDemanderCd, 	YdConstant.DEBUG);
//		
//		ydUtils.putLog(szClassName, szMethodName, "sYdStkBedLGp=>"+sYdStkBedLGp,YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sYdStkBedWGp=>"+sYdStkBedWGp,YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sShipCd=>"+sShipCd, 			YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sDelTermCd=>"+sDelTermCd, 	YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sDetailArrCd=>"+sDetailArrCd,YdConstant.DEBUG);

		ydUtils.putLogNew(szClassName, szMethodName, "sGbn=>"+sGbn, 				YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdMtlWGp=>"+sYdMtlWGp, 		YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdMtlLGp=>"+sYdMtlLGp, 		YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdPilingCd=>"+sYdPilingCd, 	YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sDemanderCd=>"+sDemanderCd, 	YdConstant.DEBUG, logId);
		
		ydUtils.putLogNew(szClassName, szMethodName, "sYdStkBedLGp=>"+sYdStkBedLGp,YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdStkBedWGp=>"+sYdStkBedWGp,YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sShipCd=>"+sShipCd, 			YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sDelTermCd=>"+sDelTermCd, 	YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sDetailArrCd=>"+sDetailArrCd,YdConstant.DEBUG, logId);
		
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		recPara.setField("YD_MTL_W_GP",		sYdMtlWGp);
		recPara.setField("YD_MTL_L_GP",		sYdMtlLGp);
		recPara.setField("YD_PILING_CD",	sYdPilingCd);
		recPara.setField("DEMANDER_CD",		sDemanderCd);
		recPara.setField("YD_STK_BED_L_GP",	sYdStkBedLGp);
		recPara.setField("YD_STK_BED_W_GP",	sYdStkBedWGp);
		recPara.setField("SHIP_CD",			sShipCd);
		recPara.setField("DELIVER_TERM_CD",	sDelTermCd);
		recPara.setField("CUST_CD",			sCustCd);
		recPara.setField("DETAIL_ARR_CD",	sDetailArrCd);
		
		int iLength		= 0;
		String[] arryS 	= null;
		
		if("2".equals(sGbn)){		// 해송

			iLength = 1;
			arryS 	= new String[iLength];
			arryS[0] 	= "3";
			
		}else if("3".equals(sGbn)){	// 주문외

			iLength = 2;
			arryS 	= new String[iLength];
			arryS[0] 	= "4";
			arryS[1] 	= "2";
		}else{						// 육송

			iLength = 3;
			arryS 		= new String[iLength];
			arryS[0] 	= "1";
			arryS[1] 	= "*"; 	//출하권역별 검색
			arryS[2] 	= "2";
		}
	
		for(int idx = 0; idx < iLength; idx++ ){
			
			recPara.setField("SEARCH_GBN",arryS[idx]);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			if("*".equals(arryS[idx])){
				
				//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD",    	szYD_GP);
				
				if(szALT_FLAG.equals("Y")){
					recPara.setField("AL_FROM",			szAL_FROM);
					recPara.setField("AL_TO",			szAL_TO);
					/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithShipSimilarBedReNew_PIDEVWithAlFrTo*/ //신규쿼리 생성 
					szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 635);
				}
				else{
					/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithShipSimilarBedReNew_PIDEV*/
					szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 626);
				}
			}else{
				if(szALT_FLAG.equals("Y")){
					recPara.setField("AL_FROM",			szAL_FROM);
					recPara.setField("AL_TO",			szAL_TO);
					/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBedReNewWithAlFrTo*/ //신규쿼리 생성 
					szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 636);
				}
				else{
					/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBedReNew*/
					szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 625);
				}
			}
			
			if(( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) && ( rsResult.size() > 0 )) {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// srchNconvRecord2Vo argument 에 logId 항목 추가 개선
//				srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_EMPTY_BED);
				srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_EMPTY_BED, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
				if( listToLoc.size() > 0 ) {
					break;
				}	
			}
		}
			
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 혼적베드 정보 조회 완료 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "신혼적베드검색(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
	}

	/**
	 * 동일길이/폭구분공베드검색
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getEmptyBedWithSameLWGpNew(String szFROM_DONG,String szTO_DONG, String szYD_CRN_GRAB_TP,JDTORecord recInPara, List listToLoc,String szYD_CRN_SCH_ID) throws JDTOException {
		/*
		 * 최상단 재료의 Piling Code와 동일한 Piling Code를 가진 베드 검색
		 * szYD_PILING_CD : Piling Code
		 * szYD_STK_BED_NO : 권상지시위치의 베드 번지
		 * szSPAN_ORDER : 검색할 스판의 순서
		 * 		1) 1, 2, 3, 4 - 해당 스판만 검색
		 * 		2) 1234 - 04, 05, 0642스판을 먼저 검색 후 07 스판을 검색
		 * 		3) 3412 - 07스판을 먼저 검색 후 04, 05, 06 스판을 검색
		 * szSCAN_DIR	: 검색할 방향
		 * 		1) R2P : RT -> PT(차량)방향
		 * 		2) P2R : PT(차량) -> RT방향
		 * 
		 */
		String szMethodName				= "getEmptyBedWithSameLWGpNew";
		String szOperationName			= "동일길이/폭구분공베드검색";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		String szYD_TO_LOC_GUIDE		= null;
		String szYD_MTL_L_GP			= null;
		String szYD_MTL_W_GP			= null;
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		String szYD_SPAN_GP             = null;
		
		String szALT_FLAG               = null;
		String szAL_FROM                = null;
		String szAL_TO                  = null;
		
		JDTORecord		recPara			= null;
		JDTORecordSet	rsResult		= null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, recInPara);
		
		szYD_TO_LOC_GUIDE 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");						//사용자지정위치
		szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시위치 - 적치베드
		
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		
		szALT_FLAG          = ydDaoUtils.paraRecChkNull(recInPara, "ALT_FLAG");							    //대체크레인범위 추가 필터 여부
		szAL_FROM           = ydDaoUtils.paraRecChkNull(recInPara, "AL_FROM");							    //대체 FROM
		szAL_TO             = ydDaoUtils.paraRecChkNull(recInPara,   "AL_TO");							    //대체 TO
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
		
			szYD_GP			= szYD_TO_LOC_GUIDE.substring(0, 1);
			szYD_BAY_GP		= szYD_TO_LOC_GUIDE.substring(1, 2);
			szYD_SPAN_GP	= szYD_TO_LOC_GUIDE.substring(2, 4);
			//SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_PILING_CD
		}else{
			//----------------------------------------------------------------------------------------------------------------------
			//	사용자지정위치(입고예정위치)가 존재하지 않는 경우에는 업무종료
			//	==>권상지시위치를 사용. : 2009.12.22
			//----------------------------------------------------------------------------------------------------------------------
			
			szYD_GP			= szYD_STK_COL_GP.substring(0, 1);
			szYD_BAY_GP		= szYD_STK_COL_GP.substring(1, 2);
			szYD_SPAN_GP	= szYD_STK_COL_GP.substring(2, 4);
			
			szLogMsg = "["+ szOperationName +"] 사용자지정위치(입고예정위치)가 존재하지 않으므로 권상지시위치를 사용";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//----------------------------------------------------------------------------------------------------------------------
		}
		
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
		recPara.setField("YD_STK_BED_L_GP", szYD_MTL_L_GP.substring(0, 1));
		recPara.setField("YD_STK_BED_W_GP", szYD_MTL_W_GP.substring(0, 1));
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
		recPara.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
		recPara.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);
		recPara.setField("BED_SEARCH_GP", 	"1");	//Full Scan
		recPara.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID);
		
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 szYD_CRN_SCH_ID["+szYD_CRN_SCH_ID+"]로  모든 공베드 정보 조회 시작 ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		/* 디폴트 사용
		if("X".equals(szYD_CRN_GRAB_TP)){
			if( szYD_STK_BED_NO.equals("") ) {
				recPara.setField("BED_SEARCH_GP", 	"1"); //Full Scan
			}else if(szYD_STK_BED_NO.equals("01")){
				recPara.setField("BED_SEARCH_GP", 	"2");	// 01, 02번지 Scan
			}else if(szYD_STK_BED_NO.equals("03")){
				recPara.setField("BED_SEARCH_GP", 	"3");	// 02, 03번지 Scan
			}
		}else{
				recPara.setField("BED_SEARCH_GP", 	"1");	//Full Scan
		}
		*/
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 길이구분/폭구분을  가진 모든 공베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 동일한 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"]을 가진 모든 공베드 정보 조회 시작 ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 베드 정보 조회 - 동일길이/폭구분을 가진  모든 공베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		if( szYD_SPAN_GP.equals(YdConstant.SPAN_ORDER_NEW_01) ||
			szYD_SPAN_GP.equals(YdConstant.SPAN_ORDER_NEW_04) || 
			szYD_SPAN_GP.equals(YdConstant.SPAN_ORDER_NEW_05) || 
			szYD_SPAN_GP.equals(YdConstant.SPAN_ORDER_NEW_06) ) {
			
			if(szALT_FLAG.equals("Y")){
				recPara.setField("AL_FROM"	, szYD_GP + szYD_BAY_GP + szAL_FROM);
				recPara.setField("AL_TO"	, szYD_GP + szYD_BAY_GP + szAL_TO);
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAscNewWithAlFr*/
				szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 331);				
			}
			else{
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAscNew*/
				szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 306);
			}
		} else {
			if(szALT_FLAG.equals("Y")){
				recPara.setField("AL_FROM"	, szYD_GP + szYD_BAY_GP + szAL_FROM);
				recPara.setField("AL_TO"	, szYD_GP + szYD_BAY_GP + szAL_TO);
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColDescNewWithAlFr*/
				szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 332);				
			}else{
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColDescNew*/
				szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 307);	
			}
			
		}
		
		szLogMsg = "["+ szOperationName +"] 동일길이/폭구분을 가진  모든 공베드 정보 조회 완료 - 메세지 : " + szRtnMsg;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		//----------------------------------------------------------------------------------------------------------------------
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {

			srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_EMPTY_BED);
			
		}
		
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 동일한 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"]을 가진 모든 공베드 정보 조회 완료 ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		//----------------------------------------------------------------------------------------------------------------------
			
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 동일길이/폭구분 모든베드검색(일반베드대상)  --공베드 검색 로직 참조.
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getNormalBedWithSameLWGpNew(String szFROM_DONG,String szTO_DONG, String szYD_CRN_GRAB_TP,JDTORecord recInPara, List listToLoc,String szYD_CRN_SCH_ID) throws JDTOException {
		/*
		 * 최상단 재료의 Piling Code와 동일한 Piling Code를 가진 베드 검색
		 * szYD_PILING_CD : Piling Code
		 * szYD_STK_BED_NO : 권상지시위치의 베드 번지
		 * szSPAN_ORDER : 검색할 스판의 순서
		 * 		1) 1, 2, 3, 4 - 해당 스판만 검색
		 * 		2) 1234 - 04, 05, 0642스판을 먼저 검색 후 07 스판을 검색
		 * 		3) 3412 - 07스판을 먼저 검색 후 04, 05, 06 스판을 검색
		 * szSCAN_DIR	: 검색할 방향
		 * 		1) R2P : RT -> PT(차량)방향
		 * 		2) P2R : PT(차량) -> RT방향
		 * 
		 */
		String szMethodName				= "getNormalBedWithSameLWGpNew";
		String szOperationName			= "동일길이/폭구분 모든베드(일반베드)검색";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		String szYD_TO_LOC_GUIDE		= null;
		String szYD_MTL_L_GP			= null;
		String szYD_MTL_W_GP			= null;
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		String szYD_SPAN_GP             = null;
		
		//String szALT_FLAG               = null;
		//String szAL_FROM                = null;
		//String szAL_TO                  = null;
		
		JDTORecord		recPara			= null;
		JDTORecordSet	rsResult		= null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, recInPara);
		
		szYD_TO_LOC_GUIDE 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");						//사용자지정위치
		szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시위치 - 적치베드
		
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		
		//szALT_FLAG          = ydDaoUtils.paraRecChkNull(recInPara, "ALT_FLAG");							    //대체크레인범위 추가 필터 여부
		//szAL_FROM           = ydDaoUtils.paraRecChkNull(recInPara, "AL_FROM");							    //대체 FROM
		//szAL_TO             = ydDaoUtils.paraRecChkNull(recInPara,   "AL_TO");							    //대체 TO
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
		
			szYD_GP			= szYD_TO_LOC_GUIDE.substring(0, 1);
			szYD_BAY_GP		= szYD_TO_LOC_GUIDE.substring(1, 2);
			szYD_SPAN_GP	= szYD_TO_LOC_GUIDE.substring(2, 4);
			//SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_PILING_CD
		}else{
			//----------------------------------------------------------------------------------------------------------------------
			//	사용자지정위치(입고예정위치)가 존재하지 않는 경우에는 업무종료
			//	==>권상지시위치를 사용. : 2009.12.22
			//----------------------------------------------------------------------------------------------------------------------
			
			szYD_GP			= szYD_STK_COL_GP.substring(0, 1);
			szYD_BAY_GP		= szYD_STK_COL_GP.substring(1, 2);
			szYD_SPAN_GP	= szYD_STK_COL_GP.substring(2, 4);
			
			szLogMsg = "["+ szOperationName +"] 사용자지정위치(입고예정위치)가 존재하지 않으므로 권상지시위치를 사용";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			//----------------------------------------------------------------------------------------------------------------------
		}
		
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
		recPara.setField("YD_STK_BED_L_GP", szYD_MTL_L_GP.substring(0, 1));
		recPara.setField("YD_STK_BED_W_GP", szYD_MTL_W_GP.substring(0, 1));
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
		recPara.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
		recPara.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);
		recPara.setField("BED_SEARCH_GP", 	"1");	//Full Scan
		recPara.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID);
		
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 szYD_CRN_SCH_ID["+szYD_CRN_SCH_ID+"]로  모든 일반베드 정보 조회 시작 ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		/* 디폴트 사용
		if("X".equals(szYD_CRN_GRAB_TP)){
			if( szYD_STK_BED_NO.equals("") ) {
				recPara.setField("BED_SEARCH_GP", 	"1"); //Full Scan
			}else if(szYD_STK_BED_NO.equals("01")){
				recPara.setField("BED_SEARCH_GP", 	"2");	// 01, 02번지 Scan
			}else if(szYD_STK_BED_NO.equals("03")){
				recPara.setField("BED_SEARCH_GP", 	"3");	// 02, 03번지 Scan
			}
		}else{
				recPara.setField("BED_SEARCH_GP", 	"1");	//Full Scan
		}
		*/
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 길이구분/폭구분을  가진 모든 일반베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 동일한 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"]을 가진 모든 일반베드 정보 조회 시작 ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 베드 정보 조회 - 동일길이/폭구분을 가진  모든 일반베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		
		//if( szYD_SPAN_GP.equals(YdConstant.SPAN_ORDER_NEW_01) ||
		//	szYD_SPAN_GP.equals(YdConstant.SPAN_ORDER_NEW_04) || 
		//	szYD_SPAN_GP.equals(YdConstant.SPAN_ORDER_NEW_05) || 
		//	szYD_SPAN_GP.equals(YdConstant.SPAN_ORDER_NEW_06) ) {
			
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAscNew*/
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getNormalBedWithSameLWGpColAscNew*///추가필요
		//	szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 306);//바꾸고 셋팅필요.
			
		//} else {
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColDescNew*/
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getNormalBedWithSameLWGpColDescNew*///추가필요
		//	szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 307);	//바꾸고 셋팅필요.
		//}
		
		//오름차순/내림차순 나누는 기준이 입고예정위치의 SPAN값을 기준으로하는데,
		//대체크레인 대체범위 탐색시에는 입고예정위치가 의미없는 값임.(대체대기전 예정위치이므로)
		//따라서 입고예정위치 기반 오름차순/내림차순 나누는작업이 무의미함.
		//우선 일괄적으로 가장 작은 열순으로 조회되도록 셋팅. 이후 협의결과에 따라 어떻게 나눌지 결정하자.
		/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAscNew*/
		/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSameSizeGroup*///추가필요
			szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 333);//바꾸고 셋팅필요. 333번셋팅완.
		
		szLogMsg = "["+ szOperationName +"] 동일길이/폭구분을 가진  모든 일반베드 정보 조회 완료 - 메세지 : " + szRtnMsg;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		//----------------------------------------------------------------------------------------------------------------------
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {

			srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_EMPTY_BED);
			
		}
		
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 동일한 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"]을 가진 모든 일반베드 정보 조회 완료 ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		//----------------------------------------------------------------------------------------------------------------------
			
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}	
	/**
	 * 동일한PilingCode베드검색(신보조작업)
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getBedWithSamePilingCdForAidWrkNew(String szFROM_DONG,String szTO_DONG, String szYD_CRN_GRAB_TP,JDTORecord recInPara, List listToLoc) throws JDTOException {
		/*
		 * 최상단 재료의 Piling Code와 동일한 Piling Code를 가진 베드 검색
		 * szYD_PILING_CD : Piling Code
		 * szYD_STK_BED_NO : 권상지시위치의 베드 번지
		 * szSPAN_ORDER : 검색할 스판의 순서
		 * 		1) 1, 2, 3, 4 - 해당 스판만 검색
		 * 		2) 1234 - 04, 05, 0642스판을 먼저 검색 후 07 스판을 검색
		 * 		3) 3412 - 07스판을 먼저 검색 후 04, 05, 06 스판을 검색
		 * szSCAN_DIR	: 검색할 방향
		 * 		1) R2P : RT -> PT(차량)방향
		 * 		2) P2R : PT(차량) -> RT방향
		 * 
		 */
		String szMethodName				= "getBedWithSamePilingCdForAidWrkNew";
		String szOperationName			= "동일한PilingCode베드검색(신보조작업)";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		//String szYD_TO_LOC_GUIDE		= null;
		String szYD_PILING_CD			= null;
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		
		JDTORecord		recPara			= null;
		JDTORecordSet	rsResult		= null;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "동일한PilingCode베드검색(신보조작업)(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szYD_PILING_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_PILING_CD");						//크레인작업 최하단 재료의 Piling Code
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");					//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");					//권상지시베드
		
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");					//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");					//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");						//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");						//크레인스케줄코드
		
		szYD_GP				= szYD_STK_COL_GP.substring(0, 1);
		szYD_BAY_GP			= szYD_STK_COL_GP.substring(1, 2);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	스판검색범위와 검색정렬방법을 정의
		//----------------------------------------------------------------------------------------------------------------------
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);								//야드구분
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);							//동구분
		recPara.setField("YD_STK_COL_GP", 	szYD_STK_COL_GP);						//권상지시위치 - 적치열
		recPara.setField("YD_STK_BED_NO", 	szYD_STK_BED_NO);						//권상지시베드
		recPara.setField("YD_PILING_CD", 	szYD_PILING_CD);						//크레인작업 최하단 재료의 Piling Code
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);						//크레인작업재료 총매수
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);						//크레인작업재료 총중량
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);						//크레인작업재료 총높이
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);							//크레인스케줄코드

		recPara.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
		recPara.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);
		recPara.setField("FR_YD_STK_COL_GP",  szYD_STK_COL_GP);
		recPara.setField("FR_YD_STK_BED_NO",  szYD_STK_BED_NO);
		/* 사용안함
		if("X".equals(szYD_CRN_GRAB_TP)){
			if( szYD_STK_BED_NO.equals("") ) {
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
			}else if(szYD_STK_BED_NO.equals("01")){
				// 01, 02번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"2");
			}else if(szYD_STK_BED_NO.equals("03")){
				// 02, 03번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"3");
			}
		}else{
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
		}
		*/
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 Piling Code를 가진 재료가 있는 모든 베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]를 가진 재료가 있는 모든 베드 정보 조회 시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatioReNew*/
		szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 624);

		
		szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]를 가진 재료가 있는 모든 베드 정보 조회 완료  - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		
		recPara.setField("LOOP_I", 			"1");
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) && rsResult.size() > 0 ) {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// srchNconvRecord2Vo argument 에 logId 항목 추가 개선
//			srchNconvRecord2Vo(	szYD_STK_COL_GP,
//								szYD_STK_BED_NO,
//								recPara, 
//								rsResult, 
//								listToLoc, 
//								YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD);
			srchNconvRecord2Vo(	szYD_STK_COL_GP,
					szYD_STK_BED_NO,
					recPara, 
					rsResult, 
					listToLoc, 
					YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD,
					logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "동일한PilingCode베드검색(신보조작업)(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
	}
		
	/**
	 * 혼적베드검색 - 신보조작업
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getBedWithSimilarGpForAidWrkNew( String sStlNo, String szFROM_DONG,String szTO_DONG, String szYD_CRN_GRAB_TP, JDTORecord recInPara, List listToLoc) throws JDTOException {
		/*
		 * 최상단 재료의 Piling Code와 동일한 Piling Code를 가진 베드 검색
		 * szYD_PILING_CD : Piling Code
		 * szYD_STK_BED_NO : 권상지시위치의 베드 번지
		 * szSPAN_ORDER : 검색할 스판의 순서
		 * 		1) 1, 2, 3, 4 - 해당 스판만 검색
		 * 		2) 1234 - 04, 05, 0642스판을 먼저 검색 후 07 스판을 검색
		 * 		3) 3412 - 07스판을 먼저 검색 후 04, 05, 06 스판을 검색
		 * szSCAN_DIR	: 검색할 방향
		 * 		1) R2P : RT -> PT(차량)방향
		 * 		2) P2R : PT(차량) -> RT방향
		 * 
		 */
		String szMethodName				= "getBedWithSimilarGpForAidWrkNew";
		String szOperationName			= "혼적베드검색(신보조작업)";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		String szYD_STK_COL_GP			= null;
		String szYD_MTL_L_GP			= null;
		String szYD_MTL_W_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		
		JDTORecord		recPara			= null;
		JDTORecord		recRecord		= null;
		
		JDTORecordSet	rsResult		= null;
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "혼적베드검색(신보조작업)(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시베드
		szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		
		szYD_GP				= szYD_STK_COL_GP.substring(0, 1);
		szYD_BAY_GP			= szYD_STK_COL_GP.substring(1, 2);
		//----------------------------------------------------------------------------------------------------------------------
		
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
		recPara.setField("YD_STK_COL_GP",	szYD_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", 	szYD_STK_BED_NO);
		recPara.setField("YD_STK_BED_L_GP", szYD_MTL_L_GP.substring(0, 1));
		recPara.setField("YD_STK_BED_W_GP", szYD_MTL_W_GP.substring(0, 1));
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);

		recPara.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
		recPara.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);
		/* 사용안함
		if("X".equals(szYD_CRN_GRAB_TP)){
			if( szYD_STK_BED_NO.equals("") ) {
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
			}else if(szYD_STK_BED_NO.equals("01")){
				// 01, 02번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"2");
			}else if(szYD_STK_BED_NO.equals("03")){
				// 02, 03번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"3");
			}
		}else{
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
		}
		*/
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	혼적베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"]혼적베드(보조작업) 정보 조회 시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		recPara.setField("STL_NO",	sStlNo);
		
		recRecord = JDTORecordFactory.getInstance().create();
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
//PIDEV_S :병행가동용:PI_YD
		recPara.setField("PI_YD",    	szYD_GP);					
		/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getOsPilingCdByStlNo_PIDEV*/
		szRtnMsg = DaoManager.getYdStock(recPara, rsResult, 213);
		
		rsResult.first();
		recRecord = rsResult.getRecord();
		
		String sGbn			= ydDaoUtils.paraRecChkNull(recRecord, "GBN");
		String sYdMtlWGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_MTL_W_GP");
		String sYdMtlLGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_MTL_L_GP");
		String sYdPilingCd	= ydDaoUtils.paraRecChkNull(recRecord, "YD_PILING_CD");
		String sDemanderCd	= ydDaoUtils.paraRecChkNull(recRecord, "DEMANDER_CD");
		String sYdStkBedLGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_STK_BED_L_GP");
		String sYdStkBedWGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_STK_BED_W_GP");
		String sShipCd		= ydDaoUtils.paraRecChkNull(recRecord, "SHIP_CD");
		String sDelTermCd	= ydDaoUtils.paraRecChkNull(recRecord, "DELIVER_TERM_CD"); // 검색
		String sCustCd		= ydDaoUtils.paraRecChkNull(recRecord, "CUST_CD");
		String sDetailArrCd	= ydDaoUtils.paraRecChkNull(recRecord, "DETAIL_ARR_CD");
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, "sGbn=>"+sGbn, 				YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sYdMtlWGp=>"+sYdMtlWGp, 		YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sYdMtlLGp=>"+sYdMtlLGp, 		YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sYdPilingCd=>"+sYdPilingCd, 	YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sDemanderCd=>"+sDemanderCd, 	YdConstant.DEBUG);
//		
//		ydUtils.putLog(szClassName, szMethodName, "sYdStkBedLGp=>"+sYdStkBedLGp,YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sYdStkBedWGp=>"+sYdStkBedWGp,YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sShipCd=>"+sShipCd, 			YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sDelTermCd=>"+sDelTermCd, 	YdConstant.DEBUG);
//		ydUtils.putLog(szClassName, szMethodName, "sDetailArrCd=>"+sDetailArrCd,YdConstant.DEBUG);
		
		ydUtils.putLogNew(szClassName, szMethodName, "sGbn=>"+sGbn, 				YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdMtlWGp=>"+sYdMtlWGp, 		YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdMtlLGp=>"+sYdMtlLGp, 		YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdPilingCd=>"+sYdPilingCd, 	YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sDemanderCd=>"+sDemanderCd, 	YdConstant.DEBUG, logId);
		
		ydUtils.putLogNew(szClassName, szMethodName, "sYdStkBedLGp=>"+sYdStkBedLGp,YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdStkBedWGp=>"+sYdStkBedWGp,YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sShipCd=>"+sShipCd, 			YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sDelTermCd=>"+sDelTermCd, 	YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sDetailArrCd=>"+sDetailArrCd,YdConstant.DEBUG, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		recPara.setField("YD_MTL_W_GP",		sYdMtlWGp);
		recPara.setField("YD_MTL_L_GP",		sYdMtlLGp);
		recPara.setField("YD_PILING_CD",	sYdPilingCd);
		recPara.setField("DEMANDER_CD",		sDemanderCd);
		recPara.setField("YD_STK_BED_L_GP",	sYdStkBedLGp);
		recPara.setField("YD_STK_BED_W_GP",	sYdStkBedWGp);
		recPara.setField("SHIP_CD",			sShipCd);
		recPara.setField("DELIVER_TERM_CD",	sDelTermCd);
		recPara.setField("CUST_CD",			sCustCd);
		recPara.setField("DETAIL_ARR_CD",	sDetailArrCd);
		
		int iLength		= 0;
		String[] arryS 	= null;
		
		if("2".equals(sGbn)){		// 해송

			iLength = 1;
			arryS 	= new String[iLength];
			arryS[0] 	= "3";
			
		}else if("3".equals(sGbn)){	// 주문외

			iLength = 2;
			arryS 	= new String[iLength];
			arryS[0] 	= "4";
			arryS[1] 	= "2";
		}else{						// 육송

			iLength = 3;
			arryS 		= new String[iLength];
			arryS[0] 	= "1";
			arryS[1] 	= "*"; 	//출하권역별 검색
			arryS[2] 	= "2";
		}

		for(int idx = 0; idx < iLength; idx++ ){
			
			recPara.setField("SEARCH_GBN",arryS[idx]);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			if("*".equals(arryS[idx])){
				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithShipSimilarBedReNew_PIDEV*/
//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD",    	szYD_GP);					
				szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 626);
			}else{
				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBedReNew*/
				szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 625);
			}
			
			recPara.setField("LOOP_I", 			"1");
			
			if(( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) && ( rsResult.size() > 0 )) {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// srchNconvRecord2Vo argument 에 logId 항목 추가 개선
//				srchNconvRecord2Vo(	szYD_STK_COL_GP,
//									szYD_STK_BED_NO,
//									recPara, 
//									rsResult, 
//									listToLoc, 
//									YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD);
				srchNconvRecord2Vo(	szYD_STK_COL_GP,
						szYD_STK_BED_NO,
						recPara, 
						rsResult, 
						listToLoc, 
						YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD, 
						logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
				
				if(listToLoc.size() > 0) {
					break;
				}	
			}
		}
		
		szLogMsg = "["+ szOperationName +"] 혼적베드(보조작업) 정보 조회 완료  - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "혼적베드검색(신보조작업)(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
	}

	/**
	 * 동일길이/폭구분공베드검색 - 신보조작업
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getEmptyBedWithSameLWGpForAidWrkNew(String szFROM_DONG,String szTO_DONG, String szYD_CRN_GRAB_TP,JDTORecord recInPara , List listToLoc,String szYD_CRN_SCH_ID) throws JDTOException {
		/*
		 * 최상단 재료의 Piling Code와 동일한 Piling Code를 가진 베드 검색
		 * szYD_PILING_CD : Piling Code
		 * szYD_STK_BED_NO : 권상지시위치의 베드 번지
		 * szSPAN_ORDER : 검색할 스판의 순서
		 * 		1) 1, 2, 3, 4 - 해당 스판만 검색
		 * 		2) 1234 - 04, 05, 0642스판을 먼저 검색 후 07 스판을 검색
		 * 		3) 3412 - 07스판을 먼저 검색 후 04, 05, 06 스판을 검색
		 * szSCAN_DIR	: 검색할 방향
		 * 		1) R2P : RT -> PT(차량)방향
		 * 		2) P2R : PT(차량) -> RT방향
		 * 
		 */
		String szMethodName				= "getEmptyBedWithSameLWGpForAidWrk";
		String szOperationName			= "동일길이/폭구분공베드검색(신보조작업)";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		String szYD_STK_COL_GP			= null;
		String szYD_MTL_L_GP			= null;
		String szYD_MTL_W_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		
		JDTORecord		recPara			= null;
		JDTORecordSet	rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시위치의 베드
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		
		szYD_GP				= szYD_STK_COL_GP.substring(0, 1);
		szYD_BAY_GP			= szYD_STK_COL_GP.substring(1, 2);
		//----------------------------------------------------------------------------------------------------------------------
		
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
		recPara.setField("YD_STK_COL_GP",	szYD_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", 	szYD_STK_BED_NO);
		recPara.setField("YD_STK_BED_L_GP", szYD_MTL_L_GP.substring(0, 1));
		recPara.setField("YD_STK_BED_W_GP", szYD_MTL_W_GP.substring(0, 1));
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
		recPara.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
		recPara.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);
		recPara.setField("BED_SEARCH_GP", 	"1");
		recPara.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID);
		
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 szYD_CRN_SCH_ID["+szYD_CRN_SCH_ID+"]로  모든 공베드 정보 조회 시작 ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		
		/* 디폴트 사용
		if("X".equals(szYD_CRN_GRAB_TP)){
			if( szYD_STK_BED_NO.equals("") ) {
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
			}else if(szYD_STK_BED_NO.equals("01")){
				// 01, 02번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"2");
			}else if(szYD_STK_BED_NO.equals("03")){
				// 02, 03번지 Scan
				recPara.setField("BED_SEARCH_GP", 	"3");
			}
		}else{
				//Full Scan
				recPara.setField("BED_SEARCH_GP", 	"1");
		}
		*/
		//----------------------------------------------------------------------------------------------------------------------
		
		/*
		 * 04, 05, 06 스판인 경우 +
		 */
		if( szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_01) ||
			szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
			szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) || 	
			szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06)) {
			
			//----------------------------------------------------------------------------------------------------------------------
			//	동일한 길이/폭구분 공베드 검색 
			//----------------------------------------------------------------------------------------------------------------------
			szLogMsg = "["+ szOperationName +"] 권상위치보다 크고 검색ASC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 시작 ";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAscNew*/
			szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 306);
			
			szLogMsg = "["+ szOperationName +"] 권상위치보다 크고 검색ASC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 완료  - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
		/*
		 * 07스판인 경우
		 */	
		}else if( szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_02) ||
				  szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_03) ||
				  szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
				  szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP)) {
			
			//----------------------------------------------------------------------------------------------------------------------
			//	동일한 길이/폭구분 공베드 검색 
			//----------------------------------------------------------------------------------------------------------------------
			szLogMsg = "["+ szOperationName +"] 권상위치보다 작고 검색DESC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 시작 ";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColDescNew*/
			szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 307);

			szLogMsg = "["+ szOperationName +"] 권상위치보다 작고 검색DESC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 완료  - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}
		
		recPara.setField("LOOP_I", 			"1");

		
		szLogMsg = "["+ szOperationName +"] YdConstant.RETN_CD_SUCCESS " + szRtnMsg + "rsResult.size() :" + rsResult.size();
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) && rsResult.size() > 0 ) {
			srchNconvRecord2Vo(	szYD_STK_COL_GP,
								szYD_STK_BED_NO,
								recPara, 
								rsResult, 
								listToLoc, 
								YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD);
		}

		szLogMsg = "["+ szOperationName +"] YdConstant.RETN_CD_SUCCESS " + szRtnMsg + "listToLoc.size() :" + listToLoc.size();
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * To위치결정-가적bed 사용자지정(공통) : 해당bed에 적치가능 유무 판단 로직으로 사용
	 * @param recPara
	 * @param listToLoc
	 * @param szFromMethod
	 * @return
	 * @throws JDTOException
	 */
	public static String procAsgnedBedStackable_rt(JDTORecord recPara,  List listToLoc, String szFromMethod) throws JDTOException {
		/*
		 */
		if( listToLoc == null ) listToLoc = new ArrayList();
		String szMethodName				= "procAsgnedBedStackable";
		String szOperationName			= "가적To위치결정-사용자지정(공통)";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		int	intYD_BED_ERR_CD			= 0;
		
		JDTORecord recTemp				= null;
		JDTORecordSet rsTemp			= null;
		
		String szYD_TO_LOC_GUIDE 		= null;
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_STK_LYR_NO			= null;
		String szSTL_NO					= null;
		String szYD_SCH_CD				= null;
		String szYD_STK_BED_ACT_STAT	= null;
		String szYD_STK_BED_WHIO_STAT	= null;
		String szYD_STK_LYR_MTL_STAT	= null;
		
		YdStkLocVO ydStkLocVO			= null;
	
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(recPara, "T");  // JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "가적To위치결정-사용자지정(공통)(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		szYD_TO_LOC_GUIDE 	= ydDaoUtils.paraRecChkNull(recPara, "YD_TO_LOC_GUIDE");				//사용자지정위치
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");						//스케줄코드
		
		szLogMsg = "["+ szOperationName +"] 파라미터로 전달된 TO위치가이드["+szYD_TO_LOC_GUIDE+"]입니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		if( szYD_TO_LOC_GUIDE.equals("") ) {
			szLogMsg = "["+ szOperationName +"] TO위치가이드가 존재하지 않습니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	베드분석정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYD_STK_COL_GP = szYD_TO_LOC_GUIDE.substring(0, 6);
		szYD_STK_BED_NO = szYD_TO_LOC_GUIDE.substring(6);
		
		//베드분석정보 조회
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 베드정보 조회 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
		recTemp = JDTORecordFactory.getInstance().create();
		recTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
		recTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// getYdStkBedAnalysis call 시  recTemp 에 logId SET 추가 개선
		recTemp.setField("LOG_ID", logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		szRtnMsg = getYdStkBedAnalysis(recTemp, rsTemp, YdConstant.MTL_STAT_C_U_D);
		
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 베드정보 조회 완료 - 반환값 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 베드정보 조회 시 오류발생";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	베드상태와 기존 스케줄이 존재하는 지 체크
		//----------------------------------------------------------------------------------------------------------------------
		rsTemp.first();
		recTemp = rsTemp.getRecord();
		
		szSTL_NO 				= ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
		szYD_STK_BED_ACT_STAT 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_ACT_STAT");
		szYD_STK_BED_WHIO_STAT 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT");
		//szYD_STK_LYR_MTL_STAT 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT");
		szYD_STK_LYR_MTL_STAT 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT2"); //2013.05.27 수정 chobg
		
		if( !szYD_STK_BED_ACT_STAT.equals(YdConstant.YD_STK_BED_ACTIVE) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 활성상태["+szYD_STK_BED_ACT_STAT+"]가 적치가능상태가 아닙니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_BED_INACT;
		}
		
		if( !szYD_STK_BED_WHIO_STAT.equals(YdConstant.YD_STK_BED_WHIO_ENABLE) ) {
			//선별
			if( szYD_SCH_CD.substring(2, 4).equals("SL")){
				if( !szYD_STK_BED_WHIO_STAT.equals("G") ) {
					szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 야드적치Bed입출고상태["+szYD_STK_BED_WHIO_STAT+"]가 출하가적상태가 아닙니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
					return YdConstant.RETN_BED_WHIO_NOT_IN;
				}
			} else {
			// 입고가적	
				if( !szYD_STK_BED_WHIO_STAT.equals("H") ) {
					szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 야드적치Bed입출고상태["+szYD_STK_BED_WHIO_STAT+"]가 입고가적상태가 아닙니다.";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
					ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
					return YdConstant.RETN_BED_WHIO_NOT_IN;
				}
				
			}	
		}
		
		if( szYD_STK_LYR_MTL_STAT.equals(YdConstant.YD_STK_LYR_MTL_STAT_UN_WAIT)) {				//권상대기이면 적치불가능
			szLogMsg = "["+ szOperationName +"] 적치된 재료["+szSTL_NO+"]보다 적치재료상태["+szYD_STK_LYR_MTL_STAT+"]가 권상대기이므로 적치불가능";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			return YdConstant.RETN_BED_UN_WAIT;
		}else if( szYD_STK_LYR_MTL_STAT.equals(YdConstant.YD_STK_LYR_MTL_STAT_DN_WAIT)) {		//권하대기이면 스케줄코드비교
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	베드 적치가능유무 판단
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]에 적치가능유무 판단 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		intYD_BED_ERR_CD = YdCommonUtils.chkBedStackable(recPara, recTemp);

		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]에 적치가능유무 판단 완료 - 반환값 : " + intYD_BED_ERR_CD;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);

		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	적치가능한 베드의 적치단을 1 증가 시킴 - 값이 없으면 001(1단)으로 설정
		//----------------------------------------------------------------------------------------------------------------------
		if( intYD_BED_ERR_CD == YdConstant.YD_BED_STACKABLE ) {
			
			szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");
			
			szLogMsg = "["+ szOperationName +"] 적치베드의 조회된 적치단["+szYD_STK_LYR_NO+"]에 1 증가시킴 - 값이 없으면 001(1단)으로 설정  ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			
			if( szYD_STK_LYR_NO.equals("") ) {							//값이 없으면
				szYD_STK_LYR_NO = "001";										//1단
			}else{														//값이 존재하면
				szYD_STK_LYR_NO = ydDaoUtils.stringPlusInt(szYD_STK_LYR_NO, 1);	//조회된 적치단 + 1
			}
			
			szLogMsg = "["+ szOperationName +"] 계산된 단["+szYD_STK_LYR_NO+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		}else{
			
			szYD_STK_LYR_NO = "000";
			
		}
		
		recTemp.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
		
		ydUtils.displayRecord(szOperationName, recTemp);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	JDTORecord를 VO객체로 변환
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 레코드를 VO객체로 변환 시작  ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		ydStkLocVO = procRecord2StkLoc(recTemp);
		
		szLogMsg = "["+ szOperationName +"] 레코드를 VO객체로 변환 완료  ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		ydStkLocVO.setSeq(YdConstant.TO_LOC_PRIOR_USER);
		ydStkLocVO.setPrior(YdConstant.TO_LOC_PRIOR_USER);
		
		listToLoc.add(ydStkLocVO);
		//----------------------------------------------------------------------------------------------------------------------
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "가적To위치결정-사용자지정(공통)(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
		
	}
	
	/**
	 * 동별저장계획에 의해 정해진 동에서 입고 위치를 찾는 메소드 
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdBayLocPln3G(JDTORecord recInPara) throws JDTOException {
		/*
		 * 
		 */
		String szMethodName				= "getYdBayLocPln3G";
		String szOperationName			= "동별저장계획에의한입고위치검색";
		String szLogMsg					= null;
		
		JDTORecord		recPara			= null;
		JDTORecord 		recTemp			= null;
		
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		YdPlateCommDAO commDao = new YdPlateCommDAO();	
		
		int intRtnVal 			= 0;
		
		//boolean bIS_BED_STACKABLE	= false;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		
		/*com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0056*/
		intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0056");	
		
		if( intRtnVal > 0 ) {
			
			rsResult.absolute(1);
			recTemp = rsResult.getRecord();			
	
			szLogMsg = "["+ szOperationName +"] 메소드 끝";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			return ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO");
		}		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 Piling Code의 베드가 존재하지 않을 경우에는 
		//	길이구분/폭구분이 동일한 공베드를 조회
		//----------------------------------------------------------------------------------------------------------------------
		/*com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0061*/
		intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0061");			

		if( intRtnVal > 0 ) {

			rsResult.absolute(1);
			recTemp = rsResult.getRecord();			
	
			szLogMsg = "["+ szOperationName +"] 메소드 끝";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			return ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO");
		}		
					
	 	
		szLogMsg = "["+ szOperationName +"] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return recInPara.getFieldString("YD_GP") + recInPara.getFieldString("YD_BAY_GP") + "XX0101";
		
	}// getYdBayLocPln3G
	
	/**
	 * 차량입고(반품,회송,출고취소) 주작업TO위치결정(후판제품)
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String procMainWrkToLocForCarInStockPlateYd(
			JDTORecord msgRecord					/* 전문 */
			, JDTORecordSet rsCrnwrkmtl				/* 크레인작업재료 */
			, JDTORecord recCrnSch					/* 크레인스케줄정보 */
			, JDTORecord recWbook					/* 작업예약정보 */
			) throws JDTOException { 
		
		String szMethodName				= "procCarInStockMainWrkToLocForPlateYd";
		String szOperationName			= "차량입고(반품,회송,출고취소) 주작업TO위치결정(후판제품)";
		String szDesc					= "";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		
		JDTORecordSet	rsResult		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		
		String szYD_UP_WO_LOC			= null;
		String szYD_UP_WO_LAYER			= null;
		String szYD_DN_WO_LOC			= null;
		String szYD_DN_WO_LAYER			= null;
		String szYD_UP_STK_COL_GP		= null;
		String szYD_UP_STK_BED_NO		= null;
		String szYD_DN_STK_COL_GP		= null;
		String szYD_DN_STK_BED_NO		= null;
		String szYD_SCH_CD				= null;
		
		boolean bUP_UPDT_NEEDED			= false;
		
		String	szSTL_NO				= null;
		String[] szYD_STK_LYR_MTL_STAT	= {"D", "U"};
		
		String	szYD_TO_LOC_GUIDE		= null;
		
		String szYD_CRN_SCH_ID			= null;
		String szYD_EQP_ID				= null;
		
		int intYD_EQP_WRK_SH			= 0;						//야드설비작업매수
		int intYD_EQP_WRK_WT			= 0;						//야드설비작업중량
		double dblYD_EQP_WRK_T			= 0;						//야드설비작업총두께
		String szYD_EQP_WRK_MAX_W		= null;						//작업재료 중 최대 폭
		String szYD_EQP_WRK_MAX_L		= null;						//작업재료 중 최대 길이
		
////////////////////////////////////////////////////////////////////////////////////////
//2024.09.?? 로그 개선  START
//기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(msgRecord, "T");  // JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "차량입고(반품,회송,출고취소) 주작업TO위치결정(후판제품)(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
//2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		//----------------------------------------------------------------------------------------------------------------------
		//------------------------------------- 사용자정의위치(입고예정위치)에 대한 TO위치결정 -------------------------------------
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------
		
		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();
		
		szYD_SCH_CD	= ydDaoUtils.paraRecChkNull(recWbook, "YD_SCH_CD");
		szDesc = "차량(반품회송출고취소)입고";
		 
		if( !szDesc.equals("") ) szOperationName		+= "-" + szDesc;
		
		szYD_CRN_SCH_ID      	= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_CRN_SCH_ID");					//크레인스케줄ID
		szYD_EQP_ID      		= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_EQP_ID");						//크레인설비ID
		
		intYD_EQP_WRK_SH      	= ydDaoUtils.paraRecChkNullInt(recPara,"SH_CNT");						//크레인작업재료 총매수
		intYD_EQP_WRK_WT      	= ydDaoUtils.paraRecChkNullInt(recPara,"SUM_MTL_WT");					//크레인작업재료 총중량
		dblYD_EQP_WRK_T      	= ydDaoUtils.paraRecChkNullDouble(recPara,"SUM_MTL_T");					//크레인작업재료 총높이
		
		szYD_EQP_WRK_MAX_W		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_W");						//크레인작업재료 중 최대 폭
		szYD_EQP_WRK_MAX_L		= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_L");						//크레인작업재료 중 최대 길이
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 총매수 :" + intYD_EQP_WRK_SH;
		szLogMsg += ", 총중량 :" + intYD_EQP_WRK_WT;
		szLogMsg += ", 총높이  :" + dblYD_EQP_WRK_T;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szYD_TO_LOC_GUIDE		= ydDaoUtils.paraRecChkNull(recWbook, "YD_TO_LOC_GUIDE");
		
		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
			szYD_DN_STK_COL_GP		= szYD_TO_LOC_GUIDE.substring(0, 6);
			szYD_DN_STK_BED_NO		= szYD_TO_LOC_GUIDE.substring(6);
		}
		
		szLogMsg = "["+ szOperationName +"] ---------------------- 야드To위치Guide : " + szYD_TO_LOC_GUIDE + " --------------------------";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	최하단 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szSTL_NO	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
		
		szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		szRtnMsg = DaoManager.getYdStock(recPara, rsResult, 0);
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			return szRtnMsg;
		}
		 
		//----------------------------------------------------------------------------------------------------------------------
		//	권하중이거나 권상중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYD_UP_WO_LOC 		= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");		
		szYD_UP_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER");
		
		if( szYD_UP_WO_LOC.equals("") ) {
			
			szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			szRtnMsg = YdCommonUtils.getYdStkLyrWithMtlStat(recPara, rsResult, szYD_STK_LYR_MTL_STAT);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				return szRtnMsg;
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szLogMsg = "["+ szOperationName +"] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 완료  ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YD_STK_COL_GP");
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YD_STK_BED_NO");
			szYD_UP_WO_LOC 			= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYD_UP_WO_LAYER 		= recTemp.getFieldString("YD_STK_LYR_NO");
			
			bUP_UPDT_NEEDED			= true;
			
			szLogMsg = "["+ szOperationName +"] 조회된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		}else{
			szYD_UP_STK_COL_GP = szYD_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYD_UP_WO_LOC.substring(6);
			
			szLogMsg = "["+ szOperationName +"] 크레인스케줄에 등록된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		if( szYD_TO_LOC_GUIDE.length() == 8 ){
			JDTORecord params = JDTORecordFactory.getInstance().create();
			// Bed까지 지정된 To위치가이드이면 지정된 곳으로 아니면 기존 로직을 태운다.
			szLogMsg = "["+ szOperationName +"] 반품의 경우 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]를 사용함. 기존 To위치로직을 사용안함";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			szYD_DN_STK_COL_GP		= szYD_TO_LOC_GUIDE.substring(0, 6);
			szYD_DN_STK_BED_NO		= szYD_TO_LOC_GUIDE.substring(6);
			szYD_DN_WO_LOC	 		= szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;
			
			// Bed의 정합성을 체크
			params = JDTORecordFactory.getInstance().create();
			params.setField("YD_STK_COL_GP", 		szYD_DN_STK_COL_GP);
			params.setField("YD_STK_BED_NO", 		szYD_DN_STK_BED_NO);
			params.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH));
			params.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT));
			params.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T));
			params.setField("YD_SCH_CD", 			szYD_SCH_CD);
			YdStkLocVO toLocGuide = new YdStkLocVO();
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, "["+ szOperationName +"] 베드["+szYD_DN_STK_COL_GP+" - "+szYD_DN_STK_BED_NO+"]가 적치가능한 지 확인 시작", YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, "["+ szOperationName +"] 베드["+szYD_DN_STK_COL_GP+" - "+szYD_DN_STK_BED_NO+"]가 적치가능한 지 확인 시작", YdConstant.DEBUG, logId);
			szRtnMsg = procBedStackableTmpBedForCarInStock(params, toLocGuide, szMethodName);
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, "["+ szOperationName +"] 베드["+szYD_DN_STK_COL_GP+" - "+szYD_DN_STK_BED_NO+"]가 적치가능한 지 확인 완료", YdConstant.DEBUG);
			ydUtils.putLogNew(szClassName, szMethodName, "["+ szOperationName +"] 베드["+szYD_DN_STK_COL_GP+" - "+szYD_DN_STK_BED_NO+"]가 적치가능한 지 확인 완료", YdConstant.DEBUG, logId);
			
			
			/*
			 * 권하위치 최종결정정보 셋팅.
			 */
			if(szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
				szYD_DN_WO_LAYER 	= toLocGuide.getYdStkLyrNo();
			}
		}
		
		// 베드상태를 검증 후 적치할 수 없는 곳이면 Return처리하며, XX가 뜨게 유도한다.
		if( szYD_TO_LOC_GUIDE.length() != 8 || !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szYD_DN_STK_COL_GP		= "";
			szYD_DN_STK_BED_NO		= "";
			szYD_DN_WO_LOC	 		= "";
			return YdConstant.RETN_NOT_EXIST_BED;
		}
		
 		//----------------------------------------------------------------------------------------------------------------------
		// 권하지시위치 수정
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		JDTORecord recUpCrnSch	= null;
		
		recUpCrnSch = JDTORecordFactory.getInstance().create();
		recUpCrnSch.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);									//크레인스케줄ID
		recUpCrnSch.setField("YD_EQP_ID", 				szYD_EQP_ID);										//크레인설비ID
		recUpCrnSch.setField("YD_UP_STK_COL_GP", 		szYD_UP_STK_COL_GP);								//권상지시위치 - 적치열
		recUpCrnSch.setField("YD_UP_STK_BED_NO", 		szYD_UP_STK_BED_NO);								//권상지시위치 - 적치베드
		recUpCrnSch.setField("YD_DN_STK_COL_GP", 		szYD_DN_STK_COL_GP);								//권하지시위치 - 적치열
		recUpCrnSch.setField("YD_DN_STK_BED_NO", 		szYD_DN_STK_BED_NO);								//권하지시위치 - 적치베드
		
		//----------------------------------------------------------------------------------------------------------------------
		//	권상지시위치 업데이트
		//----------------------------------------------------------------------------------------------------------------------
		if( bUP_UPDT_NEEDED ) {
			szLogMsg = "["+ szOperationName +"] 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"] 크레인스케줄에 등록";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//    		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			recUpCrnSch.setField("YD_UP_WO_LOC", 		szYD_UP_WO_LOC);									//권상지시위치
			recUpCrnSch.setField("YD_UP_WO_LAYER", 		szYD_UP_WO_LAYER);									//권상지시단
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		recUpCrnSch.setField("YD_DN_WO_LOC", 			szYD_DN_WO_LOC);									//권하지시위치
		recUpCrnSch.setField("YD_DN_WO_LAYER", 			szYD_DN_WO_LAYER);									//권하지시단
		recUpCrnSch.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));					//크레인작업재료 총매수
		recUpCrnSch.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));					//크레인작업재료 총중량
		recUpCrnSch.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));					//크레인작업재료 총높이
		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);								//크레인작업재료 중 최대 폭
		recUpCrnSch.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);								//크레인작업재료 중 최대 길이
		recUpCrnSch.setField("MODIFIER", 				szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		
		szRtnMsg = CrnSchUtil.uptCrnSchXYCord(recUpCrnSch);
    	
    	szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 완료";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
    	
		//----------------------------------------------------------------------------------------------------------------------
		//	권하지시위치에 재료를 권하대기로 등록
		//----------------------------------------------------------------------------------------------------------------------
    	szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]에 크레인작업재료 등록 시작";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szRtnMsg	= uptDnWaitOnYdStkLyr(rsCrnwrkmtl, szYD_DN_WO_LOC, szYD_DN_WO_LAYER);
    	
		szLogMsg = "["+ szOperationName +"] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]에 크레인작업재료 등록 완료 - 메세지 : " + szRtnMsg;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "차량입고(반품,회송,출고취소) 주작업TO위치결정(후판제품)(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
	} // end of procMainWrkToLocForPlateYd
	
	/**
	 * 차량입고용 BED적치가능유무판단(입고가적베드)
	 * 	- 전사물류개선 추가
	 * @param recPara
	 * @param listToLoc
	 * @param szFromMethod
	 * @return
	 * @throws JDTOException
	 */
	public static String procBedStackableTmpBedForCarInStock(JDTORecord recPara, YdStkLocVO ydStkLocVO, String szFromMethod) throws JDTOException {
		/*
		 * 업무기준 :		0. 해당 위치의 BED정보와 단 정보를 조회
		 * 				1. BED MAX 개수을 넘어서면 적치불가능
		 * 				2. BED MAX 중량을 넘어서면 적치불가능
		 * 				3. 해당 위치의 상단이 크레인스케줄이 존재하는 지 판단
		 * 					3-1. 크레인 스케줄이 존재하면
		 * 						3-1-1. 권하인 지 판단
		 * 							3-1-1-1. 권하이면서 같은 스케줄코드이면 **** 적치가능 ****
		 * 							3-1-1-2. 권하이면서 다른 스케줄코드이고 우선순위가 빠른 스케줄인 지 판단
		 * 								3-1-1-2-1. 빠른 스케줄이면 **** 적치가능 ****
		 * 								3-1-1-2-2. 늦은 스케줄이면 적치불가능
		 * 						3-1-2. 권상이면 적치불가능
		 * 					3-2. 크레인 스케줄이 존재하지 않으면  **** 적치가능  ****
		 * 수정자 : 임춘수
		 * 수정일 : 
		 * 				1) 2009.11.16 - 최초 등록
		 * 
		 * 파라미터정의:	1) YD_STK_COL_GP	- 적치열
		 * 				2) YD_STK_BED_NO	- 적치베드
		 * 				3) YD_EQP_WRK_SH	- 작업총매수
		 * 				4) YD_EQP_WRK_WT	- 작업총중량
		 * 				5) YD_EQP_WRK_T		- 작업총두께
		 * 				6) YD_SCH_CD		- 스케줄코드
		 */
		if( ydStkLocVO == null ) ydStkLocVO = new YdStkLocVO();
		String szMethodName			= "procBedStackableTmpBedForCarInStock";
		String szOperationName		= "차량입고용 BED적치가능유무판단";
		String szLogMsg				= null;
		String szRtnMsg 			= YdConstant.RETN_CD_SUCCESS;
		int	intYD_BED_ERR_CD		= 0;
		
		JDTORecord recTemp			= null;
		JDTORecordSet rsTemp		= null;
		
		String szYD_STK_COL_GP		= null;
		String szYD_STK_BED_NO		= null;
		String szYD_STK_LYR_NO		= null;
		String szSTL_NO				= null;
		String szYD_SCH_CD			= null;
		String szYD_STK_BED_ACT_STAT	= null;
		String szYD_STK_BED_WHIO_STAT	= null;
		String szYD_STK_LYR_MTL_STAT	= null;
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");				//적치열구분
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");				//적치베드번호
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");					//스케줄코드
		
		if( szYD_STK_COL_GP.equals("") ) {
			szLogMsg = "["+ szOperationName +"] 적치열구분이 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		
		if( szYD_STK_BED_NO.equals("") ) {
			szLogMsg = "["+ szOperationName +"] 적치베드번호가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	베드분석정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		//베드분석정보 조회
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 베드정보 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
		recTemp = JDTORecordFactory.getInstance().create();
		recTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
		recTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
		
		szRtnMsg = getYdStkBedAnalysis(recTemp, rsTemp, YdConstant.MTL_STAT_C_U_D);
		
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 베드정보 조회 완료 - 반환값 : " + szRtnMsg;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 베드정보 조회 시 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		//----------------------------------------------------------------------------------------------------------------------
		//----------------------------------------------------------------------------------------------------------------------
		//	베드상태와 기존 스케줄이 존재하는 지 체크
		//----------------------------------------------------------------------------------------------------------------------
		rsTemp.first();
		recTemp = rsTemp.getRecord();
		
		szSTL_NO = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
		szYD_STK_BED_ACT_STAT = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_ACT_STAT");
		szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT");
		szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT2"); //2013.05.27 수정 chobg
		
		if( !szYD_STK_BED_ACT_STAT.equals(YdConstant.YD_STK_BED_ACTIVE) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 활성상태["+szYD_STK_BED_ACT_STAT+"]가 적치가능상태가 아닙니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			//return YdConstant.RETN_BED_INACT;
		}
		
//		입고가적베드를 확인하는 로직은 제외한다.
//		if( !szYD_STK_BED_WHIO_STAT.equals("H") ) {
//			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 입출고상태["+szYD_STK_BED_WHIO_STAT+"]가 입고가적베드상태가 아닙니다.";
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
//			return YdConstant.RETN_BED_WHIO_NOT_IN;
//		}
		
		if( szYD_STK_LYR_MTL_STAT.equals(YdConstant.YD_STK_LYR_MTL_STAT_UN_WAIT)) {				//권상대기이면 적치불가능
			szLogMsg = "["+ szOperationName +"] 적치된 재료["+szSTL_NO+"]보다 적치재료상태["+szYD_STK_LYR_MTL_STAT+"]가 권상대기이므로 적치불가능";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_BED_UN_WAIT;
		}else if( szYD_STK_LYR_MTL_STAT.equals(YdConstant.YD_STK_LYR_MTL_STAT_DN_WAIT)) {		//권하대기이면 스케줄코드비교
			
		}
		//----------------------------------------------------------------------------------------------------------------------
		//----------------------------------------------------------------------------------------------------------------------
		//	베드 적치가능유무 판단
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]에 적치가능유무 판단 시작";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		intYD_BED_ERR_CD = YdCommonUtils.chkBedStackable(recPara, recTemp);

		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]에 적치가능유무 판단 완료 - 반환값 : " + intYD_BED_ERR_CD;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	적치가능한 베드의 적치단을 1 증가 시킴 - 값이 없으면 001(1단)으로 설정
		//----------------------------------------------------------------------------------------------------------------------
		if( intYD_BED_ERR_CD == YdConstant.YD_BED_STACKABLE ) {
			
			szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");
			
			szLogMsg = "["+ szOperationName +"] 적치베드의 조회된 적치단["+szYD_STK_LYR_NO+"]에 1 증가시킴 - 값이 없으면 001(1단)으로 설정  ";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
			
			if( szYD_STK_LYR_NO.equals("") ) {							//값이 없으면
				szYD_STK_LYR_NO = "001";										//1단
			}else{														//값이 존재하면
				szYD_STK_LYR_NO = ydDaoUtils.stringPlusInt(szYD_STK_LYR_NO, 1);	//조회된 적치단 + 1
			}
			
			szLogMsg = "["+ szOperationName +"] 계산된 단["+szYD_STK_LYR_NO+"]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		}else{
			
			szYD_STK_LYR_NO = "000";
			
		}
		
		recTemp.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
		
		ydUtils.displayRecord(szOperationName, recTemp);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	JDTORecord를 VO객체로 변환
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 레코드를 VO객체로 변환 시작  ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		procRecord2StkLoc(recTemp, ydStkLocVO);
		
		szLogMsg = "["+ szOperationName +"] 레코드를 VO객체로 변환 완료  ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		//----------------------------------------------------------------------------------------------------------------------
		
		return YdConstant.RETN_CD_SUCCESS;
		
	}
	//------------------------------------------------------------------------------------------------------------------------------------
	
	
	
	/**
	 * 크레인스케줄 ID 이용하여 저장품에 TO위치 검색방법 UPDATE 
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static void updCrnschToLocFindMethodBySchId(String ydCrnSchId, String toLocFindMethod, String logId) throws JDTOException {
		
		JDTORecord params = JDTORecordFactory.getInstance().create();
		params.setField("YD_CRN_SCH_ID", ydCrnSchId);
		params.setField("TO_LOC_FIND_METHOD", toLocFindMethod);
		params.setField("MODIFIER", "toLocFind");
		YdPlateCommDAO commDao = new YdPlateCommDAO();
		
		commDao.update(params, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updCrnschToLocFindMethodBySchId");
		
	}
	
	/**
	 * TO위치 검색 함수 분기처리 및 호출을 위한 함수
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO callFunctionGetBed(String func , JDTORecord recInPara) throws JDTOException {
		String logId                            = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
		
		String szMethodName				= "callFunctionGetBed";
		String szOperationName			= " TO위치 검색 함수 분기처리";
		String szLogMsg					= null;
		
		YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();	
		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "021");//후판 개발 적용여부
		
		
		if("SAME".equals(func)){
			szLogMsg = "동일한PilingCode To위치 검색(getToLocWithSamePilingCdForAidWrk) 호출";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			
			if("Y".equals(sApplyYnPI)){
				szLogMsg = "신규로직 적용 동일한PilingCode To위치 검색(getToLocWithSamePilingCdForAidWrkNew) 호출";
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
				return getToLocWithSamePilingCdForAidWrkNew( recInPara);
			}
			return getToLocWithSamePilingCdForAidWrk( recInPara);
		}
		else if("SIMIL".equals(func)){
			szLogMsg = "혼적베드 To위치 검색(getToLocWithSimilarGp) 호출";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			
			if("Y".equals(sApplyYnPI)){
				szLogMsg = "신규로직 적용 혼적베드 To위치 검색(getToLocWithSimilarGpForAidWrkNew) 호출";
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
				return getToLocWithSimilarGpForAidWrkNew( recInPara);
			}
			return getToLocWithSimilarGpForAidWrk(recInPara);
		}
		else if("EMPTY".equals(func)){
			szLogMsg = "공베드 To위치 검색(getToLocWithEmptyBedSameLWGpForAidWrk) 호출";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			return getToLocWithEmptyBedSameLWGpForAidWrk(recInPara);
		}
		else if("SINGL".equals(func)){
			
			if("Y".equals(sApplyYnPI)){
				szLogMsg = "신규로직 적용 단일파일링베드 To위치 검색 for 보조작업 (getSinglePilingBedForAidWrk) 호출";
				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
				return getSinglePilingBedForAidWrk( recInPara);
			}
			szLogMsg = "신규로직 적용 아닐땐 단일파일링베드 To위치 검색 하지 않음 return null";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			
			return null;
		}
		else {
			szLogMsg = "올바른 func 값이 아님";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			return null;
		}
		
	}
	
	/**
	 * 동일한PilingCode To위치 검색 for 보조작업 -2025.01.06 신규 RITM0791916
	 * @param 
	 * @param 
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO getToLocWithSamePilingCdForAidWrk(JDTORecord recInPara ) throws JDTOException {
		/*
		 * 최상단 재료의 Piling Code와 동일한 Piling Code를 가진 베드 검색
		 * szYD_PILING_CD : Piling Code
		 * szYD_STK_BED_NO : 권상지시위치의 베드 번지
		 * szSPAN_ORDER : 검색할 스판의 순서
		 * 		1) 1, 2, 3, 4 - 해당 스판만 검색
		 * 		2) 1234 - 04, 05, 0642스판을 먼저 검색 후 07 스판을 검색
		 * 		3) 3412 - 07스판을 먼저 검색 후 04, 05, 06 스판을 검색
		 * szSCAN_DIR	: 검색할 방향
		 * 		1) R2P : RT -> PT(차량)방향
		 * 		2) P2R : PT(차량) -> RT방향
		 * 
		 */
		String szMethodName				= "getToLocWithSamePilingCdForAidWrk";
		String szOperationName			= "동일한PilingCode To위치검색 for 보조작업";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		//String szYD_TO_LOC_GUIDE		= null;
		String szYD_PILING_CD			= null;
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		
		JDTORecord		recPara			= null;
		JDTORecordSet	rsResult		= null;

		YdStkLocVO		ydStkLocVO		= null;
		
		String szYD_TO_LOC_GUIDE 		= null;
		String szFROM_DONG	     		= null;
		String szTO_DONG  		 		= null; 
		ArrayList listToLoc 	        = new ArrayList();
		String logId                            = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
		
		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
		
		szLogMsg = "동일한PilingCode To위치검색 for 보조작업(" + szMethodName + ") 시작";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		ydUtils.displayRecord(szOperationName, recInPara);

		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";

		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szYD_PILING_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_PILING_CD");						//크레인작업 최하단 재료의 Piling Code
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");					//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");					//권상지시베드
		
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");					//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");					//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");						//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");						//크레인스케줄코드
		
		szYD_TO_LOC_GUIDE	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");						//권하지정위치
		szFROM_DONG	= ydDaoUtils.paraRecChkNull(recInPara, "FROM_DONG");								//스판검색 from dong
		szTO_DONG	= ydDaoUtils.paraRecChkNull(recInPara, "TO_DONG");									//스판검색 to dong		
		
		szYD_GP				= szYD_STK_COL_GP.substring(0, 1);
		szYD_BAY_GP			= szYD_STK_COL_GP.substring(1, 2);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	스판검색범위와 검색정렬방법을 정의
		//----------------------------------------------------------------------------------------------------------------------
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);								//야드구분
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);							//동구분
		recPara.setField("YD_STK_COL_GP", 	szYD_STK_COL_GP);						//권상지시위치 - 적치열
		recPara.setField("YD_STK_BED_NO", 	szYD_STK_BED_NO);						//권상지시베드
		recPara.setField("YD_PILING_CD", 	szYD_PILING_CD);						//크레인작업 최하단 재료의 Piling Code
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);						//크레인작업재료 총매수
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);						//크레인작업재료 총중량
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);						//크레인작업재료 총높이
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);							//크레인스케줄코드

		recPara.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
		recPara.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);
		recPara.setField("FR_YD_STK_COL_GP",  szYD_STK_COL_GP);
		recPara.setField("FR_YD_STK_BED_NO",  szYD_STK_BED_NO);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 Piling Code를 가진 재료가 있는 모든 베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]를 가진 재료가 있는 모든 베드 정보 조회 시작 ";

		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatioReNew*/
		szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 624);

		
		szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]를 가진 재료가 있는 모든 베드 정보 조회 완료  - 메세지 : " + szRtnMsg;
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		
		recPara.setField("LOOP_I", 			"1");
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) && rsResult.size() > 0 ) {
			srchNconvRecord2Vo(	szYD_STK_COL_GP,
					szYD_STK_BED_NO,
					recPara, 
					rsResult, 
					listToLoc, 
					YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD,
					logId);
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
		// Sorting
		//----------------------------------------------------------------------------------------------------------------------
		if( listToLoc.size() > 0 ) {
			Collections.sort(listToLoc, new YdStkLocComparator());
		}
		
		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
		boolean bIS_BED_STACKABLE		= false;
		
		for(int i = 0; i < listToLoc.size(); i++ ) {
			
			ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
			szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";

    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    		
    		//----------------------------------------------------------------------------------------------------------------------
    		//	야드To위치Guide가 존재하고 길이정합성이 맞는 경우에는 보조작업 TO위치 결정 시는 SKIP 시킴
    		//----------------------------------------------------------------------------------------------------------------------
    		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
    			//----------------------------------------------------------------------------------------------------------------------
    			//	야드To위치결정방법이 F이고 야드To위치Guide가 존재하는 경우에는 보조작업이적 시에는 SKIP시킴
    			//----------------------------------------------------------------------------------------------------------------------
    			if( szYD_TO_LOC_GUIDE.substring(0, 6).equals(ydStkLocVO.getYdStkColGp()) && 
    				szYD_TO_LOC_GUIDE.substring(6).equals(ydStkLocVO.getYdStkBedNo())  ) {
    				szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]가 작업예약의 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]와 동일하므로 SKIP시킴";
    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    				continue;
    			}
    			//----------------------------------------------------------------------------------------------------------------------
    		}
    		//----------------------------------------------------------------------------------------------------------------------
    		
    		//----------------------------------------------------------------------------------------------------------------------
    		//	베드가 적치가능한 지 판단 - 가능하면 루프 종료
    		//----------------------------------------------------------------------------------------------------------------------
    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
				
				bIS_BED_STACKABLE		= true;
				
				szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				szLogMsg = "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		
	    		//to위치 탐색방법 setting
	    		ydStkLocVO.setPlnLocDcsnGp("P");
	    		
				break;
			}
			//----------------------------------------------------------------------------------------------------------------------
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		return (bIS_BED_STACKABLE )? ydStkLocVO : null; // 검색위치를 찾았다면, ydStkLocVO 를 반환하고 아닌경우 null을 반환하여 분기처리할 수 있도록.
	}	
	/**
	 * 동일한PilingCode To위치 검색 for 보조작업New -2025.09.09 신규 
	 * @param 
	 * @param 
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO getToLocWithSamePilingCdForAidWrkNew(JDTORecord recInPara ) throws JDTOException {
		/*
		 * 최상단 재료의 Piling Code와 동일한 Piling Code를 가진 베드 검색
		 * szYD_PILING_CD : Piling Code
		 * szYD_STK_BED_NO : 권상지시위치의 베드 번지
		 * szSPAN_ORDER : 검색할 스판의 순서
		 * 		1) 1, 2, 3, 4 - 해당 스판만 검색
		 * 		2) 1234 - 04, 05, 0642스판을 먼저 검색 후 07 스판을 검색
		 * 		3) 3412 - 07스판을 먼저 검색 후 04, 05, 06 스판을 검색
		 * szSCAN_DIR	: 검색할 방향
		 * 		1) R2P : RT -> PT(차량)방향
		 * 		2) P2R : PT(차량) -> RT방향
		 * 
		 */
		String szMethodName				= "getToLocWithSamePilingCdForAidWrkNew";
		String szOperationName			= "동일한PilingCode To위치검색 for 보조작업New";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		//String szYD_TO_LOC_GUIDE		= null;
		String szYD_PILING_CD			= null;
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		
		JDTORecord		recPara			= null;
		JDTORecordSet	rsResult		= null;

		YdStkLocVO		ydStkLocVO		= null;
		
		String szYD_TO_LOC_GUIDE 		= null;
		String szFROM_DONG	     		= null;
		String szTO_DONG  		 		= null; 
		ArrayList listToLoc 	        = new ArrayList();
		String logId                            = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
		
		YdPlateCommDAO commDao = new YdPlateCommDAO(); 
		
		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
		
		szLogMsg = "동일한PilingCode To위치검색 for 보조작업New (" + szMethodName + ") 시작";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		ydUtils.displayRecord(szOperationName, recInPara);

		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";

		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		szYD_PILING_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_PILING_CD");						//크레인작업 최하단 재료의 Piling Code
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");					//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");					//권상지시베드
		
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");					//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");					//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");						//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");						//크레인스케줄코드
		
		szYD_TO_LOC_GUIDE	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");						//권하지정위치
		szFROM_DONG	= ydDaoUtils.paraRecChkNull(recInPara, "FROM_DONG");								//스판검색 from dong
		szTO_DONG	= ydDaoUtils.paraRecChkNull(recInPara, "TO_DONG");									//스판검색 to dong		
		
		szYD_GP				= szYD_STK_COL_GP.substring(0, 1);
		szYD_BAY_GP			= szYD_STK_COL_GP.substring(1, 2);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	스판검색범위와 검색정렬방법을 정의
		//----------------------------------------------------------------------------------------------------------------------
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);								//야드구분
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);							//동구분
		recPara.setField("YD_STK_COL_GP", 	szYD_STK_COL_GP);						//권상지시위치 - 적치열
		recPara.setField("YD_STK_BED_NO", 	szYD_STK_BED_NO);						//권상지시베드
		recPara.setField("YD_PILING_CD", 	szYD_PILING_CD);						//크레인작업 최하단 재료의 Piling Code
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);						//크레인작업재료 총매수
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);						//크레인작업재료 총중량
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);						//크레인작업재료 총높이
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);							//크레인스케줄코드

		recPara.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
		recPara.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);
		recPara.setField("FR_YD_STK_COL_GP",  szYD_STK_COL_GP);
		recPara.setField("FR_YD_STK_BED_NO",  szYD_STK_BED_NO);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 Piling Code를 가진 재료가 있는 모든 베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]를 가진 재료가 있는 모든 베드 정보 조회 시작 ";

		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		int rtnVal = 0;
		rtnVal = commDao.select(recPara, rsResult, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatioReNew2nd");
		
		szLogMsg = "["+ szOperationName +"] 동일한 Piling Code["+szYD_PILING_CD+"]를 가진 재료가 있는 모든 베드 정보 조회 완료  - 메세지 : " + rtnVal;
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		
		recPara.setField("LOOP_I", 			"1");
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) && rsResult.size() > 0 ) {
			srchNconvRecord2Vo(	szYD_STK_COL_GP,
					szYD_STK_BED_NO,
					recPara, 
					rsResult, 
					listToLoc, 
					YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD,
					logId);
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
		// Sorting
		//----------------------------------------------------------------------------------------------------------------------
		if( listToLoc.size() > 0 ) {
			Collections.sort(listToLoc, new YdStkLocComparator());
		}
		
		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
		boolean bIS_BED_STACKABLE		= false;
		
		for(int i = 0; i < listToLoc.size(); i++ ) {
			
			ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
			szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";

    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    		
    		//----------------------------------------------------------------------------------------------------------------------
    		//	야드To위치Guide가 존재하고 길이정합성이 맞는 경우에는 보조작업 TO위치 결정 시는 SKIP 시킴
    		//----------------------------------------------------------------------------------------------------------------------
    		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
    			//----------------------------------------------------------------------------------------------------------------------
    			//	야드To위치결정방법이 F이고 야드To위치Guide가 존재하는 경우에는 보조작업이적 시에는 SKIP시킴
    			//----------------------------------------------------------------------------------------------------------------------
    			if( szYD_TO_LOC_GUIDE.substring(0, 6).equals(ydStkLocVO.getYdStkColGp()) && 
    				szYD_TO_LOC_GUIDE.substring(6).equals(ydStkLocVO.getYdStkBedNo())  ) {
    				szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]가 작업예약의 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]와 동일하므로 SKIP시킴";
    	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    				continue;
    			}
    			//----------------------------------------------------------------------------------------------------------------------
    		}
    		//----------------------------------------------------------------------------------------------------------------------
    		
    		//----------------------------------------------------------------------------------------------------------------------
    		//	베드가 적치가능한 지 판단 - 가능하면 루프 종료
    		//----------------------------------------------------------------------------------------------------------------------
    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
				
				bIS_BED_STACKABLE		= true;
				
				szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				szLogMsg = "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		
	    		//to위치 탐색방법 setting
	    		ydStkLocVO.setPlnLocDcsnGp("P");
	    		
				break;
			}
			//----------------------------------------------------------------------------------------------------------------------
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		return (bIS_BED_STACKABLE )? ydStkLocVO : null; // 검색위치를 찾았다면, ydStkLocVO 를 반환하고 아닌경우 null을 반환하여 분기처리할 수 있도록.
	}	
	
	/**
	 * 혼적베드 To위치 검색 for 보조작업 -2025.01.06 신규 RITM0791916
	 * @param 
	 * @param 
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO getToLocWithSimilarGpForAidWrk(JDTORecord recInPara) throws JDTOException {

		String szMethodName				= "getToLocWithSimilarGpForAidWrk";
		String szOperationName			= "혼적베드 To위치 검색 for 보조작업";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		String szYD_STK_COL_GP			= null;
		String szYD_MTL_L_GP			= null;
		String szYD_MTL_W_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		
		JDTORecord		recPara			= null;
		JDTORecord		recRecord		= null;
		
		JDTORecordSet	rsResult		= null;
		
		YdStkLocVO		ydStkLocVO		= null;
		
		String szYD_TO_LOC_GUIDE 		= null;
		String szFROM_DONG	     		= null;
		String szTO_DONG  		 		= null; 
		String sStlNo					= null;
		ArrayList listToLoc 	        = new ArrayList();
		
		String logId                            = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		szLogMsg = "혼적베드 To위치 검색 for 보조작업(" + szMethodName + ") 시작";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		ydUtils.displayRecord(szOperationName, recInPara);
		
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시베드
		szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		szYD_TO_LOC_GUIDE	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");						//권하지정위치
		szFROM_DONG			= ydDaoUtils.paraRecChkNull(recInPara, "FROM_DONG");							//스판검색 from dong
		szTO_DONG			= ydDaoUtils.paraRecChkNull(recInPara, "TO_DONG");								//스판검색 to dong		
		sStlNo				= ydDaoUtils.paraRecChkNull(recInPara, "STL_NO_BOTTOM");								//최하단재료		
		
		szYD_GP				= szYD_STK_COL_GP.substring(0, 1);
		szYD_BAY_GP			= szYD_STK_COL_GP.substring(1, 2);
		//----------------------------------------------------------------------------------------------------------------------
		
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
		recPara.setField("YD_STK_COL_GP",	szYD_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", 	szYD_STK_BED_NO);
		recPara.setField("YD_STK_BED_L_GP", szYD_MTL_L_GP.substring(0, 1));
		recPara.setField("YD_STK_BED_W_GP", szYD_MTL_W_GP.substring(0, 1));
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);

		recPara.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
		recPara.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);

		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	혼적베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"]혼적베드(보조작업) 정보 조회 시작";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		recPara.setField("STL_NO",	sStlNo);
		
		recRecord = JDTORecordFactory.getInstance().create();
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
//PIDEV_S :병행가동용:PI_YD
		recPara.setField("PI_YD",    	szYD_GP);					
		/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getOsPilingCdByStlNo_PIDEV*/
		szRtnMsg = DaoManager.getYdStock(recPara, rsResult, 213);
		
		rsResult.first();
		recRecord = rsResult.getRecord();
		
		String sGbn			= ydDaoUtils.paraRecChkNull(recRecord, "GBN");
		String sYdMtlWGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_MTL_W_GP");
		String sYdMtlLGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_MTL_L_GP");
		String sYdPilingCd	= ydDaoUtils.paraRecChkNull(recRecord, "YD_PILING_CD");
		String sDemanderCd	= ydDaoUtils.paraRecChkNull(recRecord, "DEMANDER_CD");
		String sYdStkBedLGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_STK_BED_L_GP");
		String sYdStkBedWGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_STK_BED_W_GP");
		String sShipCd		= ydDaoUtils.paraRecChkNull(recRecord, "SHIP_CD");
		String sDelTermCd	= ydDaoUtils.paraRecChkNull(recRecord, "DELIVER_TERM_CD"); // 검색
		String sCustCd		= ydDaoUtils.paraRecChkNull(recRecord, "CUST_CD");
		String sDetailArrCd	= ydDaoUtils.paraRecChkNull(recRecord, "DETAIL_ARR_CD");
		
		ydUtils.putLogNew(szClassName, szMethodName, "sGbn=>"+sGbn, 				YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdMtlWGp=>"+sYdMtlWGp, 		YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdMtlLGp=>"+sYdMtlLGp, 		YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdPilingCd=>"+sYdPilingCd, 	YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sDemanderCd=>"+sDemanderCd, 	YdConstant.DEBUG, logId);
		
		ydUtils.putLogNew(szClassName, szMethodName, "sYdStkBedLGp=>"+sYdStkBedLGp,YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sYdStkBedWGp=>"+sYdStkBedWGp,YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sShipCd=>"+sShipCd, 			YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sDelTermCd=>"+sDelTermCd, 	YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, szMethodName, "sDetailArrCd=>"+sDetailArrCd,YdConstant.DEBUG, logId);
	
		recPara.setField("YD_MTL_W_GP",		sYdMtlWGp);
		recPara.setField("YD_MTL_L_GP",		sYdMtlLGp);
		recPara.setField("YD_PILING_CD",	sYdPilingCd);
		recPara.setField("DEMANDER_CD",		sDemanderCd);
		recPara.setField("YD_STK_BED_L_GP",	sYdStkBedLGp);
		recPara.setField("YD_STK_BED_W_GP",	sYdStkBedWGp);
		recPara.setField("SHIP_CD",			sShipCd);
		recPara.setField("DELIVER_TERM_CD",	sDelTermCd);
		recPara.setField("CUST_CD",			sCustCd);
		recPara.setField("DETAIL_ARR_CD",	sDetailArrCd);
		
		int iLength		= 0;
		String[] arryS 	= null;
		
		if("2".equals(sGbn)){		// 해송

			iLength = 1;
			arryS 	= new String[iLength];
			arryS[0] 	= "3";
			
		}else if("3".equals(sGbn)){	// 주문외

			iLength = 2;
			arryS 	= new String[iLength];
			arryS[0] 	= "4";
			arryS[1] 	= "2";
		}else{						// 육송

			iLength = 3;
			arryS 		= new String[iLength];
			arryS[0] 	= "1";
			arryS[1] 	= "*"; 	//출하권역별 검색
			arryS[2] 	= "2";
		}

		for(int idx = 0; idx < iLength; idx++ ){
			
			recPara.setField("SEARCH_GBN",arryS[idx]);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			if("*".equals(arryS[idx])){
				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithShipSimilarBedReNew_PIDEV*/
//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD",    	szYD_GP);					
				szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 626);
			}else{
				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBedReNew*/
				szRtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 625);
			}
			
			recPara.setField("LOOP_I", 			"1");
			
			if(( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) && ( rsResult.size() > 0 )) {
				srchNconvRecord2Vo(	szYD_STK_COL_GP,
						szYD_STK_BED_NO,
						recPara, 
						rsResult, 
						listToLoc, 
						YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD, 
						logId);
				
				if(listToLoc.size() > 0) {
					break;
				}	
			}
		}
		
		szLogMsg = "["+ szOperationName +"] 혼적베드(보조작업) 정보 조회 완료  - 메세지 : " + szRtnMsg;
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		// Sorting
		//----------------------------------------------------------------------------------------------------------------------
		if( listToLoc.size() > 0 ) {
			Collections.sort(listToLoc, new YdStkLocComparator());
		}
		
		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
		boolean bIS_BED_STACKABLE		= false;
		
		for(int i = 0; i < listToLoc.size(); i++ ) {
			
			ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
			szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 혼적베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    		
    		//----------------------------------------------------------------------------------------------------------------------
    		//	야드To위치Guide가 존재하고 길이정합성이 맞는 경우에는 보조작업 TO위치 결정 시는 SKIP 시킴
    		//----------------------------------------------------------------------------------------------------------------------
    		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
    			//----------------------------------------------------------------------------------------------------------------------
    			//	야드To위치결정방법이 F이고 야드To위치Guide가 존재하는 경우에는 보조작업이적 시에는 SKIP시킴
    			//----------------------------------------------------------------------------------------------------------------------
    			if( szYD_TO_LOC_GUIDE.substring(0, 6).equals(ydStkLocVO.getYdStkColGp()) && 
    				szYD_TO_LOC_GUIDE.substring(6).equals(ydStkLocVO.getYdStkBedNo())  ) {
    				szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 혼적베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]가 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]와 동일하므로 SKIP시킴";
    				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    				continue;
    			}
    			//----------------------------------------------------------------------------------------------------------------------
    		}
    		//----------------------------------------------------------------------------------------------------------------------
    		
    		//----------------------------------------------------------------------------------------------------------------------
    		//	베드가 적치가능한 지 판단 - 가능하면 루프 종료
    		//----------------------------------------------------------------------------------------------------------------------
    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
				
				bIS_BED_STACKABLE		= true;
				
				szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				szLogMsg = "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 혼적베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		
	    		//to위치 탐색방법 setting
				ydStkLocVO.setPlnLocDcsnGp("S");
	    		
				break;
			}
			//----------------------------------------------------------------------------------------------------------------------
		}
		//----------------------------------------------------------------------------------------------------------------------

		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		return (bIS_BED_STACKABLE )? ydStkLocVO : null; // 검색위치를 찾았다면, ydStkLocVO 를 반환하고 아닌경우 null을 반환하여 분기처리할 수 있도록.
	}	
	/**
	 * 혼적베드 To위치 검색 for 보조작업 -2025.09.09 신규
	 * @param 
	 * @param 
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO getToLocWithSimilarGpForAidWrkNew(JDTORecord recInPara) throws JDTOException {

		String methodName				 = "getToLocWithSimilarGpForAidWrkNew";
		String operationName			 = "혼적베드 To위치검색 for 보조작업New";
		String logMsg					 = null;
		String rtnMsg 				     = YdConstant.RETN_CD_SUCCESS;
		
		String logId                     = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		logMsg = operationName+" (" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		
		String ydSchCd    		= ydDaoUtils.paraRecChkNull(recInPara,"YD_SCH_CD");
		String ydStkColGp    = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_COL_GP");  //권상지시위치 - 적치열
		String ydStkBedNo     = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_BED_NO"); //권상지시위치 - 적치베드
		String ydPilingCd   	= ydDaoUtils.paraRecChkNull(recInPara,"YD_PILING_CD");
		String ydEqpWrkSh      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_SH");						//크레인작업재료 총매수
		String ydEqpWrkWt      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_WT");					//크레인작업재료 총중량
		String ydEqpWrkT      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_T");					//크레인작업재료 총높이
		String ydToLocGuide = ydDaoUtils.paraRecChkNull(recInPara,"YD_TO_LOC_GUIDE");
		
		String ydMtlLGp 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		String ydMtlWGp 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		
		String fromDong     = ydDaoUtils.paraRecChkNull(recInPara,"FROM_DONG");
		String toDong       = ydDaoUtils.paraRecChkNull(recInPara,"TO_DONG");
		
		String spanOrder     = ydDaoUtils.paraRecChkNull(recInPara,"SPAN_ORDER");
		String scanDir       = ydDaoUtils.paraRecChkNull(recInPara,"SCAN_DIR");
		
		String stlNo       = ydDaoUtils.paraRecChkNull(recInPara,"STL_NO_BOTTOM");//최하단 작업재료 재료번호
		
		String ydGp			= "";
    	String ydBayGp		= "";
    	

		
		YdStkLocVO		ydStkLocVO		= null;		
		ArrayList listToLoc 	        = new ArrayList();
		YdPlateCommDAO commDao = new YdPlateCommDAO(); 
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		
		logMsg = "["+ operationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		ydUtils.displayRecord(operationName, recInPara);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		ydGp			= ydStkColGp.substring(0, 1);
		ydBayGp			= ydStkColGp.substring(1, 2);
		
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		recPara.setField("YD_GP"			, ydGp);
		recPara.setField("YD_BAY_GP"		, ydBayGp);
		recPara.setField("YD_STK_BED_L_GP"	, ydMtlLGp.substring(0, 1));
		recPara.setField("YD_STK_BED_W_GP"	, ydMtlWGp.substring(0, 1));
		recPara.setField("FROM_STK_COL_GP"	, ydGp + ydBayGp + fromDong); 
		recPara.setField("TO_STK_COL_GP"  	, ydGp + ydBayGp + toDong);
		recPara.setField("FR_YD_STK_BED_NO" , "01");
		recPara.setField("YD_EQP_WRK_SH"	, ydEqpWrkSh);
		recPara.setField("YD_EQP_WRK_WT"	, ydEqpWrkWt);
		recPara.setField("YD_EQP_WRK_T"		, ydEqpWrkT);
		recPara.setField("YD_SCH_CD"		, ydSchCd);
	
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	혼적베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		logMsg = "["+ operationName +"] 파라미터 설정 후 혼적베드 정보 조회 시작 ";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 베드 정보 조회 - 혼적베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		recPara.setField("STL_NO",	stlNo);
		
		JDTORecord recRecord = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		
		//PIDEV_S :병행가동용:PI_YD
		recPara.setField("PI_YD",    	ydGp);
		
		/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getOsPilingCdByStlNo_PIDEV*/
		rtnMsg= DaoManager.getYdStock(recPara, rsResult, 213);			
		
		rsResult.first();
		recRecord = rsResult.getRecord();
		
		String sGbn			= ydDaoUtils.paraRecChkNull(recRecord, "GBN");
		String sYdMtlWGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_MTL_W_GP");
		String sYdMtlLGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_MTL_L_GP");
		String sYdPilingCd	= ydDaoUtils.paraRecChkNull(recRecord, "YD_PILING_CD");
		String sDemanderCd	= ydDaoUtils.paraRecChkNull(recRecord, "DEMANDER_CD");
		String sYdStkBedLGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_STK_BED_L_GP");
		String sYdStkBedWGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_STK_BED_W_GP");
		String sShipCd		= ydDaoUtils.paraRecChkNull(recRecord, "SHIP_CD");
		String sDelTermCd	= ydDaoUtils.paraRecChkNull(recRecord, "DELIVER_TERM_CD"); // 검색
		String sCustCd		= ydDaoUtils.paraRecChkNull(recRecord, "CUST_CD");
		String sDetailArrCd	= ydDaoUtils.paraRecChkNull(recRecord, "DETAIL_ARR_CD");		

		ydUtils.putLogNew(szClassName, methodName, "sGbn=>"+sGbn, 				YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sYdMtlWGp=>"+sYdMtlWGp, 		YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sYdMtlLGp=>"+sYdMtlLGp, 		YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sYdPilingCd=>"+sYdPilingCd, 	YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sDemanderCd=>"+sDemanderCd, 	YdConstant.DEBUG, logId);
		
		ydUtils.putLogNew(szClassName, methodName, "sYdStkBedLGp=>"+sYdStkBedLGp,YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sYdStkBedWGp=>"+sYdStkBedWGp,YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sShipCd=>"+sShipCd, 			YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sDelTermCd=>"+sDelTermCd, 	YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sDetailArrCd=>"+sDetailArrCd,YdConstant.DEBUG, logId);

		
		recPara.setField("YD_MTL_W_GP",		sYdMtlWGp);
		recPara.setField("YD_MTL_L_GP",		sYdMtlLGp);
		recPara.setField("YD_PILING_CD",	sYdPilingCd);
		recPara.setField("DEMANDER_CD",		sDemanderCd);
		recPara.setField("YD_STK_BED_L_GP",	sYdStkBedLGp);
		recPara.setField("YD_STK_BED_W_GP",	sYdStkBedWGp);
		recPara.setField("SHIP_CD",			sShipCd);
		recPara.setField("DELIVER_TERM_CD",	sDelTermCd);
		recPara.setField("CUST_CD",			sCustCd);
		recPara.setField("DETAIL_ARR_CD",	sDetailArrCd);
		recPara.setField("PI_YD",    	ydGp);
		
		int rtnVal = 0;
		/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBedReNew*/
		//rtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 624);			
		rtnVal = commDao.select(recPara, rsResult, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBedReNew2nd");
		
		logMsg = "["+ operationName +"] 혼적베드 정보 조회 완료 - 메세지 : " + rtnVal;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		//----------------------------------------------------------------------------------------------------------------------

			
		if( rtnVal>0 ) {
			srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD, logId);

		}

		
		//----------------------------------------------------------------------------------------------------------------------
	
		//----------------------------------------------------------------------------------------------------------------------
		// Sorting ?
		//----------------------------------------------------------------------------------------------------------------------
		if( listToLoc.size() > 0 ) {
			Collections.sort(listToLoc, new YdStkLocComparator());
		}
		//----------------------------------------------------------------------------------------------------------------------
		 
		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
				
		boolean isBedStackable = false;
		
		for(int i = 0; i < listToLoc.size(); i++ ) {
			
			ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
			
			logMsg = "["+ operationName +"] ["+( i + 1 )+"] 혼적베드"
					+ "["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";						
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
			
    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
    			
				isBedStackable		= true;
				logMsg = "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++ ["+( i + 1 )+"] 혼적 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
	    		
	    		//to위치 탐색방법 setting
				ydStkLocVO.setPlnLocDcsnGp("S");
	    		
				break;
			}
		}
		
		return (isBedStackable )? ydStkLocVO : null; // 검색위치를 찾았다면, ydStkLocVO 를 반환하고 아닌경우 null을 반환하여 분기처리할 수 있도록.
	}	
	/**
	 * 공베드 To위치 검색 for 보조작업 -2025.01.06 신규 RITM0791916
	 * @param 
	 * @param 
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO getToLocWithEmptyBedSameLWGpForAidWrk(JDTORecord recInPara) throws JDTOException {

		String szMethodName				= "getToLocWithEmptyBedSameLWGpForAidWrk";
		String szOperationName			= "공베드 To위치 검색 for 보조작업";
		String szLogMsg					= null;
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		
		String szYD_STK_COL_GP			= null;
		String szYD_MTL_L_GP			= null;
		String szYD_MTL_W_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_EQP_WRK_SH			= null;
		String szYD_EQP_WRK_WT			= null;
		String szYD_EQP_WRK_T			= null;
		String szYD_SCH_CD				= null;
		
		JDTORecord		recPara			= null;
		JDTORecordSet	rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
		
		YdStkLocVO		ydStkLocVO		= null;
		
		String szYD_TO_LOC_GUIDE 		= null;
		String szFROM_DONG	     		= null;
		String szTO_DONG  		 		= null; 
		String szYD_CRN_SCH_ID			= null;
		ArrayList listToLoc 	        = new ArrayList();
		
		String logId                            = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		szLogMsg = "공베드 To위치 검색 for 보조작업(" + szMethodName + ") 시작";
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		ydUtils.displayRecord(szOperationName, recInPara);
		
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시위치의 베드
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		szYD_TO_LOC_GUIDE	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");						//권하지정위치
		szFROM_DONG			= ydDaoUtils.paraRecChkNull(recInPara, "FROM_DONG");							//스판검색 from dong
		szTO_DONG			= ydDaoUtils.paraRecChkNull(recInPara, "TO_DONG");								//스판검색 to dong		
		szYD_CRN_SCH_ID		= ydDaoUtils.paraRecChkNull(recInPara, "YD_CRN_SCH_ID");						//크레인스케줄ID
		
		szYD_GP				= szYD_STK_COL_GP.substring(0, 1);
		szYD_BAY_GP			= szYD_STK_COL_GP.substring(1, 2);
		//----------------------------------------------------------------------------------------------------------------------
		
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
		recPara.setField("YD_STK_COL_GP",	szYD_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", 	szYD_STK_BED_NO);
		recPara.setField("YD_STK_BED_L_GP", szYD_MTL_L_GP.substring(0, 1));
		recPara.setField("YD_STK_BED_W_GP", szYD_MTL_W_GP.substring(0, 1));
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
		recPara.setField("FROM_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + szFROM_DONG); 
		recPara.setField("TO_STK_COL_GP"  	, szYD_GP + szYD_BAY_GP + szTO_DONG);
		recPara.setField("BED_SEARCH_GP", 	"1");
		recPara.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID);
		
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 szYD_CRN_SCH_ID["+szYD_CRN_SCH_ID+"]로  모든 공베드 정보 조회 시작 ";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		/*
		 * 04, 05, 06 스판인 경우 +
		 */
		if( szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_01) ||
			szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_04) || 
			szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_05) || 	
			szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_06)) {
			
			//----------------------------------------------------------------------------------------------------------------------
			//	동일한 길이/폭구분 공베드 검색 
			//----------------------------------------------------------------------------------------------------------------------
			szLogMsg = "["+ szOperationName +"] 권상위치보다 크고 검색ASC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 시작 ";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAscNew*/
			szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 306);
			
			szLogMsg = "["+ szOperationName +"] 권상위치보다 크고 검색ASC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 완료  - 메세지 : " + szRtnMsg;
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
		/*
		 * 07스판인 경우
		 */	
		}else if( szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_02) ||
				  szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_03) ||
				  szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_07) || 
				  szYD_STK_COL_GP.substring(2, 4).equals(YdConstant.SPAN_ORDER_NEW_TP)) {
			
			//----------------------------------------------------------------------------------------------------------------------
			//	동일한 길이/폭구분 공베드 검색 
			//----------------------------------------------------------------------------------------------------------------------
			szLogMsg = "["+ szOperationName +"] 권상위치보다 작고 검색DESC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 시작 ";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColDescNew*/
			szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 307);

			szLogMsg = "["+ szOperationName +"] 권상위치보다 작고 검색DESC - 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 완료  - 메세지 : " + szRtnMsg;
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		}
		
		recPara.setField("LOOP_I", 			"1");

		
		szLogMsg = "["+ szOperationName +"] YdConstant.RETN_CD_SUCCESS " + szRtnMsg + "rsResult.size() :" + rsResult.size();
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) && rsResult.size() > 0 ) {
			srchNconvRecord2Vo(	szYD_STK_COL_GP,
								szYD_STK_BED_NO,
								recPara, 
								rsResult, 
								listToLoc, 
								YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD);
		}

		szLogMsg = "["+ szOperationName +"] YdConstant.RETN_CD_SUCCESS " + szRtnMsg + "listToLoc.size() :" + listToLoc.size();
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		// Sorting
		//----------------------------------------------------------------------------------------------------------------------
		if( listToLoc.size() > 0 ) {
			Collections.sort(listToLoc, new YdStkLocComparator());
		}
		
		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
		boolean bIS_BED_STACKABLE		= false;
		
		for(int i = 0; i < listToLoc.size(); i++ ) {
			
			ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
			szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";

			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    		
    		//----------------------------------------------------------------------------------------------------------------------
    		//	야드To위치Guide가 존재하고 길이정합성이 맞는 경우에는 보조작업 TO위치 결정 시는 SKIP 시킴
    		//----------------------------------------------------------------------------------------------------------------------
    		if( szYD_TO_LOC_GUIDE.length() == 8 ) {
    			//----------------------------------------------------------------------------------------------------------------------
    			//	야드To위치결정방법이 F이고 야드To위치Guide가 존재하는 경우에는 보조작업이적 시에는 SKIP시킴
    			//----------------------------------------------------------------------------------------------------------------------
    			if( szYD_TO_LOC_GUIDE.substring(0, 6).equals(ydStkLocVO.getYdStkColGp()) && 
    				szYD_TO_LOC_GUIDE.substring(6).equals(ydStkLocVO.getYdStkBedNo())  ) {
    				szLogMsg = "["+ szOperationName +"] ["+( i + 1 )+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]가 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]와 동일하므로 SKIP시킴";
    				ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
    				continue;
    			}
    			//----------------------------------------------------------------------------------------------------------------------
    		}
    		//----------------------------------------------------------------------------------------------------------------------
    		
    		//----------------------------------------------------------------------------------------------------------------------
    		//	베드가 적치가능한 지 판단 - 가능하면 루프 종료
    		//----------------------------------------------------------------------------------------------------------------------
    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
				
				bIS_BED_STACKABLE		= true;
				
				szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				szLogMsg = "["+ szOperationName +"] +++++++++++++++ ["+( i + 1 )+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		szLogMsg = "["+ szOperationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    		
	    		//to위치 탐색방법 setting
	    		ydStkLocVO.setPlnLocDcsnGp("E");
	    		
				break;
			}
			//----------------------------------------------------------------------------------------------------------------------
		}
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		return (bIS_BED_STACKABLE )? ydStkLocVO : null; // 검색위치를 찾았다면, ydStkLocVO 를 반환하고 아닌경우 null을 반환하여 분기처리할 수 있도록.
	}	
	
	/**
	 * 단일파일링베드 검색--2025.09.10 callFunctionGetBed 용 함수
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO getSinglePilingBedForAidWrk(JDTORecord recInPara) throws JDTOException {

		String methodName				 = "getSinglePilingBedForAidWrk";
		String operationName			 = "단일파일링 To위치검색 for 보조작업";
		String logMsg					 = null;
		String rtnMsg 				     = YdConstant.RETN_CD_SUCCESS;
		
		String logId                     = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		logMsg = operationName+" (" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		YdPlateCommDAO commDao = new YdPlateCommDAO(); 
		
		String ydSchCd    		= ydDaoUtils.paraRecChkNull(recInPara,"YD_SCH_CD");
		String ydStkColGp    = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_COL_GP");  //권상지시위치 - 적치열
		String ydStkBedNo     = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_BED_NO"); //권상지시위치 - 적치베드
		String ydPilingCd   	= ydDaoUtils.paraRecChkNull(recInPara,"YD_PILING_CD");
		String ydEqpWrkSh      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_SH");						//크레인작업재료 총매수
		String ydEqpWrkWt      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_WT");					//크레인작업재료 총중량
		String ydEqpWrkT      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_T");					//크레인작업재료 총높이
		String ydToLocGuide = ydDaoUtils.paraRecChkNull(recInPara,"YD_TO_LOC_GUIDE");
		
		String ydMtlLGp 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		String ydMtlWGp 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		
		String fromDong     = ydDaoUtils.paraRecChkNull(recInPara,"FROM_DONG");
		String toDong       = ydDaoUtils.paraRecChkNull(recInPara,"TO_DONG");
		
		String spanOrder     = ydDaoUtils.paraRecChkNull(recInPara,"SPAN_ORDER");
		String scanDir       = ydDaoUtils.paraRecChkNull(recInPara,"SCAN_DIR");
		
		String stlNo       = ydDaoUtils.paraRecChkNull(recInPara,"STL_NO");//최하단 작업재료 재료번호
		
		

		String ydGp			= ydStkColGp.substring(0, 1);
    	String ydBayGp		= ydStkColGp.substring(1, 2);
    	
		YdStkLocVO		ydStkLocVO		= null;		
		ArrayList listToLoc 	        = new ArrayList();
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		
		logMsg = "["+ operationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		ydUtils.displayRecord(operationName, recInPara);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		if( ydToLocGuide.length() == 8 ) {
			
			ydGp			= ydToLocGuide.substring(0, 1);
			ydBayGp			= ydToLocGuide.substring(1, 2);
			
		}else{
			//----------------------------------------------------------------------------------------------------------------------
			//	==>권상지시위치를 사용. : 2009.12.22
			//----------------------------------------------------------------------------------------------------------------------
			ydGp			= ydStkColGp.substring(0, 1);
			ydBayGp			= ydStkColGp.substring(1, 2);
			
			logMsg = "["+ operationName +"] 사용자지정위치(입고예정위치)가 존재하지 않으므로 권상지시위치를 사용";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
		}
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		recPara.setField("FR_YD_STK_COL_GP", 	ydStkColGp);
		recPara.setField("FR_YD_STK_BED_NO", 	ydStkBedNo);
		
		recPara.setField("YD_GP"			, ydGp);
		recPara.setField("YD_BAY_GP"		, ydBayGp);
		recPara.setField("YD_PILING_CD"		, ydPilingCd);
		recPara.setField("FROM_STK_COL_GP"	, ydGp + ydBayGp + fromDong); 
		recPara.setField("TO_STK_COL_GP"  	, ydGp + ydBayGp + toDong);
		recPara.setField("FR_YD_STK_BED_NO" , "01");
		recPara.setField("YD_MTL_W_GP",		ydMtlWGp);
		recPara.setField("YD_MTL_L_GP",		ydMtlLGp);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	단일 파일링만 적치되어있는 모든 베드 탐색
		//----------------------------------------------------------------------------------------------------------------------
		logMsg = "["+ operationName +"] 파라미터 설정 후 단일 Piling Code 인 모든 베드 정보 조회 시작 ";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 단 정보 조회 - 동일한 Piling Code를 가진 최상단 재료가 있는 모든 베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		int rtnVal = 0;
		/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatioReNew*/
		//rtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 624);			
		rtnVal = commDao.select(recPara, rsResult, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getSinglePilingBedForMainWrk");


		logMsg = "["+ operationName +"] 단일 Piling Code를 가진 모든 베드 정보 조회 완료 - 메세지 : " + rtnVal;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		//----------------------------------------------------------------------------------------------------------------------
	
		if( rtnVal>0 ) {
			srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD, logId);

		}

		
		//----------------------------------------------------------------------------------------------------------------------
	
		//----------------------------------------------------------------------------------------------------------------------
		// Sorting ?
		//----------------------------------------------------------------------------------------------------------------------
		if( listToLoc.size() > 0 ) {
			Collections.sort(listToLoc, new YdStkLocComparator());
		}
		//----------------------------------------------------------------------------------------------------------------------
		 
		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
				
		boolean isBedStackable = false;
		
		for(int i = 0; i < listToLoc.size(); i++ ) {
			
			ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
			
			logMsg = "["+ operationName +"] ["+( i + 1 )+"] 단일 파일링베드"
					+ "["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";						
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
			
    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
    			
				isBedStackable		= true;
				logMsg = "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++ ["+( i + 1 )+"] 단일 파일링베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
	    		
	    		//to위치 탐색방법 setting
				ydStkLocVO.setPlnLocDcsnGp("I");
	    		
				break;
			}
		}
		
		
		return (isBedStackable )? ydStkLocVO : null; // 검색위치를 찾았다면, ydStkLocVO 를 반환하고 아닌경우 null을 반환하여 분기처리할 수 있도록.
	}
	
	/**
	 * 크레인스케줄 ID 이용하여 최초 파일링재료의 권하지시위치 RETURN 
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO getInitialPilingCrnschIdByCurSchId(String ydCrnSchId, String logId ) throws JDTOException {
		
		String szMethodName				= "getInitialPilingCrnschIdByCurSchId";
		String szOperationName			= "최초 파일링재료의 권하지시위치 탐색";
		String szLogMsg					= "";
		
		JDTORecord rstRecord = JDTORecordFactory.getInstance().create();
		JDTORecord params = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult= JDTORecordFactory.getInstance().createRecordSet("");
		params.setField("YD_CRN_SCH_ID", ydCrnSchId);
		params.setField("MODIFIER", "toLocFind");
		
		YdPlateCommDAO commDao = new YdPlateCommDAO();
		YdStkLocVO ydStkLocVO  = new YdStkLocVO();
		
		szLogMsg = "["+ szOperationName +"] 크레인스케줄 id["+ydCrnSchId+"]로 최초 파일링지시의 권하위치 탐색 ";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		
		int intRtnVal = commDao.select(params, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getInitialPilingCrnschIdByCurSchId");
		
		if(intRtnVal <=0){
			szLogMsg = "["+ szOperationName +"] 크레인스케줄 id["+ydCrnSchId+"]로 최초 파일링지시의 권하위치 탐색불가";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
			return null;
		}
		
		rsResult.first();
		rstRecord  = rsResult.getRecord();
		
		String initYdStkColGp = ydDaoUtils.paraRecChkNull(rstRecord, "YD_STK_COL_GP");
		String initYdStkBedNo = ydDaoUtils.paraRecChkNull(rstRecord, "YD_STK_BED_NO");
		String initYdStkLyrNo = ydDaoUtils.paraRecChkNull(rstRecord, "YD_STK_LYR_NO");
		
		if( initYdStkLyrNo.equals("") ) {							//값이 없으면
			initYdStkLyrNo = "001";										//1단
		}else{														//값이 존재하면
			initYdStkLyrNo = ydDaoUtils.stringPlusInt(initYdStkLyrNo, 1);	//조회된 적치단 + 1
		}
		
		szLogMsg = "["+ szOperationName +"] 최초 파일링지시의 권하위치 탐색 결과 적치열 ["+initYdStkColGp+"] 베드 ["+initYdStkBedNo+"] 단+1 ["+initYdStkLyrNo+"]";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
		
		
		
		JDTORecord tempParam = JDTORecordFactory.getInstance().create();
		
		tempParam.setField("YD_STK_COL_GP", initYdStkColGp);
		tempParam.setField("YD_STK_BED_NO", initYdStkBedNo);
		tempParam.setField("YD_STK_LYR_NO", initYdStkLyrNo);
		
		
		procRecord2StkLoc(tempParam, ydStkLocVO);
		
		return ydStkLocVO;

		
	}
	
	/**
	 * TO위치 검색 함수 분기처리 및 호출을 위한 함수
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO callFunctionGetBedForMainWrk(String func , JDTORecord recInPara) throws JDTOException {
		String logId                            = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
		
		String szMethodName				= "callFunctionGetBedForMainWrk";
		String szOperationName			= " TO위치 검색 함수 분기처리";
		String szLogMsg					= null;
		
		
		if("TEMP".equals(func)){
			szLogMsg = "1후판 입고가적베드 탐색(getBedWith1stPlateLoadTempBedForMainWrk) 호출";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			return getBedWith1stPlateRcvTempBedForMainWrk( recInPara);
		}
		else if("SAME".equals(func)){
			szLogMsg = "동일한PilingCode To위치 검색New(getBedWithSamePilingCdForMainWrkNew) 호출";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			return getBedWithSamePilingCdForMainWrkNew( recInPara);
		}
		else if("SIMIL".equals(func)){
			szLogMsg = "혼적베드 To위치 검색New(getBedWithSimilarGpForMainWrkNew) 호출";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			return getBedWithSimilarGpForMainWrkNew(recInPara);
		}
		else if("EMPTY".equals(func)){
			szLogMsg = "공베드 To위치 검색 for 주작업 NEW(getEmptyBedWithSameLWGpNewForMainWrkNew) 호출";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			return getEmptyBedWithSameLWGpNewForMainWrkNew(recInPara);
		}
		else if("SINGL".equals(func)){
			szLogMsg = "단일파일링베드 To위치 검색 for 주작업(getSinglePilingBedForMainWrk) 호출";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			return getSinglePilingBedForMainWrk(recInPara);
		}
		else {
			szLogMsg = "올바른 func 값이 아님";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
			return null;
		}
		
	}
	/**
	 * 1후판 입고가적베드 탐색 --2025.06.09 callFunctionGetBedForMainWrk 용 함수
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO getBedWith1stPlateRcvTempBedForMainWrk(JDTORecord recInPara) throws JDTOException {
		String methodName				 = "getBedWith1stPlateRcvTempBedForMainWrk";
		String operationName			 = "1후판 입고가적베드 탐색";
		String logMsg					 = null;
		String rtnMsg 				     = YdConstant.RETN_CD_SUCCESS;
		
		String logId                     = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		logMsg = "1후판 입고가적베드 탐색 (" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		
		String ydSchCd    	= ydDaoUtils.paraRecChkNull(recInPara,"YD_SCH_CD");
		String ydStkColGp    = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_COL_GP");  //권상지시위치 - 적치열
		String ydStkBedNo     = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_BED_NO"); //권상지시위치 - 적치베드
		
		String ydPilingCd   = ydDaoUtils.paraRecChkNull(recInPara,"YD_PILING_CD");
		String ydEqpWrkSh      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_SH");						//크레인작업재료 총매수
		String ydEqpWrkWt      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_WT");					//크레인작업재료 총중량
		String ydEqpWrkT      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_T");					//크레인작업재료 총높이

		String fromDong     = ydDaoUtils.paraRecChkNull(recInPara,"FROM_DONG");
		String toDong       = ydDaoUtils.paraRecChkNull(recInPara,"TO_DONG");
		

		
		String ydGp			= ydStkColGp.substring(0, 1);
    	String ydBayGp		= ydStkColGp.substring(1, 2);
    	String ydEqpGp		= ydStkColGp.substring(2, 4);
		
		YdStkLocVO		ydStkLocVO		= null;		
		ArrayList listToLoc 	        = new ArrayList();
		
		String rtGp         = "";
		
		if(ydSchCd.length()<6){
			rtGp = ydStkColGp.substring(5, 6);
			
		}
		else{
			rtGp = ydSchCd.substring(5, 6);
		}
		
		
		
		//입고작업이 아닐땐 입고 가적베드 탐색하지 않는다 
		if( !YdConstant.YD_EQP_GP_ROLLERTABLE.equals(ydEqpGp) && !YdConstant.YD_EQP_GP_TRANSFER.equals(ydEqpGp) ) {
			logMsg = "입고작업이 아닐시, 입고가적베드 탐색 안함 (" + methodName + ") 끝";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			
			return null;
		}
		
		//1후판 작업이 아닐땐 가적베드 탐색하지 않는다.
		if("D".equals(rtGp)|| 	//입고RT(1후판 전 라인라인으로 수정 2020.11.04 윤재광)
		    "E".equals(rtGp)||
		    "F".equals(rtGp)|| //광폭재도 포함되도록 변경(박종호. 2022.04.28 임진후 사원 요청사항.)
		    "G".equals(rtGp)) 
			{ 
			logMsg = "["+ rtGp +"] 입고RT / ["+ydBayGp+"]입고동 1후판 r/t 아닐시, 입고가적베드 탐색 안함 (" + methodName + ") 끝";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			
			return null;
		}
		
		logMsg = "["+ rtGp +"] 입고RT / ["+ydBayGp+"]입고동 ";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
 		
		
		logMsg = "["+ operationName +"] 1후판 온라인 E동 적치가능한 가적베드 조회 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		recPara.setField("YD_GP"			, ydGp);
		recPara.setField("YD_BAY_GP"		, ydBayGp);
		recPara.setField("YD_PILING_CD"		, ydPilingCd);
		recPara.setField("FROM_STK_COL_GP"	, ydGp + ydBayGp + fromDong); 
		recPara.setField("TO_STK_COL_GP"  	, ydGp + ydBayGp + toDong);
		recPara.setField("FR_YD_STK_BED_NO" , "01");
		recPara.setField("YD_EQP_WRK_SH"	, ydEqpWrkSh);
		recPara.setField("YD_EQP_WRK_WT"	, ydEqpWrkWt);
		recPara.setField("YD_EQP_WRK_T"		, ydEqpWrkT);
		recPara.setField("YD_SCH_CD"		, ydSchCd);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 단 정보 조회 - 1후판 적치가능한 가적베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithTempBedRatioReNew*/
		rtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 631);
		//----------------------------------------------------------------------------------------------------------------------
		if( rtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {

			srchNconvRecord2VoTmpBed("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD);
		}
		logMsg = "["+ operationName +"] 1후판 온라인 E동 적치가능한 가적베드 조회 완료 - 메세지 : " + rtnMsg;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
		
		//----------------------------------------------------------------------------------------------------------------------
		// Sorting ?
		//----------------------------------------------------------------------------------------------------------------------
		if( listToLoc.size() > 0 ) {
			Collections.sort(listToLoc, new YdStkLocComparator());
		}
		//----------------------------------------------------------------------------------------------------------------------
		 
		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
		boolean isBedStackable = false;
		
		for(int i = 0; i < listToLoc.size(); i++ ) {
			
			ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
			
			logMsg = "["+ operationName +"] ["+( i + 1 )+"] 1후판 적치가능한 가적베드 조회된 베드"
					+ "["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
			
    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
    			
				isBedStackable		= true;
				logMsg = "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++ ["+( i + 1 )+"] 1후판 온라인 E동 적치가능한 가적베드 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
	    		
	    		//to위치 탐색방법 setting
				ydStkLocVO.setPlnLocDcsnGp("T");
	    		
				break;
			}
		}
		
		
		logMsg = "["+ operationName +"] 메소드 끝";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
		
		return (isBedStackable )? ydStkLocVO : null; // 검색위치를 찾았다면, ydStkLocVO 를 반환하고 아닌경우 null을 반환하여 분기처리할 수 있도록.
	}
	/**
	 * 동일한PilingCode베드검색 NEW --2025.06.04 callFunctionGetBedForMainWrk 용 함수
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO getBedWithSamePilingCdForMainWrkNew(JDTORecord recInPara) throws JDTOException {

		String methodName				 = "getBedWithSamePilingCdForMainWrkNew";
		String operationName			 = "동일한PilingCode To위치검색 for 주작업 NEW";
		String logMsg					 = null;
		String rtnMsg 				     = YdConstant.RETN_CD_SUCCESS;
		
		YdPlateCommDAO commDao = new YdPlateCommDAO(); 
		
		String logId                     = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		logMsg = "동일한PilingCode To위치검색 for 주작업 NEW (" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		
		String ydSchCd    	= ydDaoUtils.paraRecChkNull(recInPara,"YD_SCH_CD");
		String ydStkColGp    = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_COL_GP");  //권상지시위치 - 적치열
		String ydStkBedNo     = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_BED_NO"); //권상지시위치 - 적치베드
		String ydPilingCd   = ydDaoUtils.paraRecChkNull(recInPara,"YD_PILING_CD");
		String ydEqpWrkSh      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_SH");						//크레인작업재료 총매수
		String ydEqpWrkWt      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_WT");					//크레인작업재료 총중량
		String ydEqpWrkT      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_T");					//크레인작업재료 총높이
		String ydToLocGuide = ydDaoUtils.paraRecChkNull(recInPara,"YD_TO_LOC_GUIDE");
		
		String fromDong     = ydDaoUtils.paraRecChkNull(recInPara,"FROM_DONG");
		String toDong       = ydDaoUtils.paraRecChkNull(recInPara,"TO_DONG");
		
		
		YdStkLocVO		ydStkLocVO		= null;		
		ArrayList listToLoc 	        = new ArrayList();
		
		
    	//----------------------------------------------------------------------------------------------------------------------
		//	동일한 Piling Code의 적치가능한 베드 조회
		//----------------------------------------------------------------------------------------------------------------------
		logMsg = "["+ operationName +"] 동일한 Piling Code["+ydPilingCd+"]의 적치가능한 베드 조회 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		String ydGp = "";
		String ydBayGp = "";
		
		if( ydToLocGuide.length() == 8 ) {
			
			ydGp			= ydToLocGuide.substring(0, 1);
			ydBayGp			= ydToLocGuide.substring(1, 2);
			
		}else{
			//----------------------------------------------------------------------------------------------------------------------
			//	==>권상지시위치를 사용. : 2009.12.22
			//----------------------------------------------------------------------------------------------------------------------
			ydGp			= ydStkColGp.substring(0, 1);
			ydBayGp			= ydStkColGp.substring(1, 2);
			
			logMsg = "["+ operationName +"] 사용자지정위치(입고예정위치)가 존재하지 않으므로 권상지시위치를 사용";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
		}
		//SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_PILING_CD
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
	
		recPara.setField("FR_YD_STK_COL_GP", 	ydStkColGp);
		recPara.setField("FR_YD_STK_BED_NO", 	ydStkBedNo);
		
		recPara.setField("YD_GP"			, ydGp);
		recPara.setField("YD_BAY_GP"		, ydBayGp);
		recPara.setField("YD_PILING_CD"		, ydPilingCd);
		recPara.setField("FROM_STK_COL_GP"	, ydGp + ydBayGp + fromDong); 
		recPara.setField("TO_STK_COL_GP"  	, ydGp + ydBayGp + toDong);
		recPara.setField("FR_YD_STK_BED_NO" , "01");
		recPara.setField("YD_EQP_WRK_SH"	, ydEqpWrkSh);
		recPara.setField("YD_EQP_WRK_WT"	, ydEqpWrkWt);
		recPara.setField("YD_EQP_WRK_T"		, ydEqpWrkT);
		recPara.setField("YD_SCH_CD"		, ydSchCd);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 Piling Code를 가진 최상단재료가 있는 모든 베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		logMsg = "["+ operationName +"] 파라미터 설정 후 동일한 Piling Code["+ydPilingCd+"]를 가진 최상단재료가 있는 모든 베드 정보 조회 시작 ";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 단 정보 조회 - 동일한 Piling Code를 가진 최상단 재료가 있는 모든 베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		int rtnVal = 0;
		//고장등록시 로직 제거 --> 최종 to위치 탐색 후 고장범위 내이면 신규 고장to위치 탐색로직 사용
		/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatioReNew*/
		//rtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 624);			
		rtnVal = commDao.select(recPara, rsResult, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatioReNew2nd");


		logMsg = "["+ operationName +"] 동일한 Piling Code를 가진 최상단 재료가 있는 모든 베드 정보 조회 완료 - 메세지 : " + rtnVal;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		//----------------------------------------------------------------------------------------------------------------------

		if( rtnVal>0 ) {
			srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD, logId);

		}
				
		logMsg = "["+ operationName +"] 파라미터 설정 후 동일한 Piling Code["+ydPilingCd+"]를 가진 최상단재료가 있는 모든 베드 정보 조회 완료 ";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
			
		
		logMsg = "["+ operationName +"] 동일한 Piling Code["+ydPilingCd+"]의 적치가능한 베드 조회 완료 - 메세지 : " + rtnMsg;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		//----------------------------------------------------------------------------------------------------------------------
	
		//----------------------------------------------------------------------------------------------------------------------
		// Sorting ?
		//----------------------------------------------------------------------------------------------------------------------
		if( listToLoc.size() > 0 ) {
			Collections.sort(listToLoc, new YdStkLocComparator());
		}
		//----------------------------------------------------------------------------------------------------------------------
		 
		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
				
		boolean isBedStackable = false;
		
		for(int i = 0; i < listToLoc.size(); i++ ) {
			
			ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
			
			logMsg = "["+ operationName +"] ["+( i + 1 )+"] 1후판 적치 가능한 동일 Piling Code 재료 적치 베드"
					+ "["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
			
    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
    			
				isBedStackable		= true;
				logMsg = "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++ ["+( i + 1 )+"] 동일한 Piling Code재료로 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
	    		
	    		//to위치 탐색방법 setting
				ydStkLocVO.setPlnLocDcsnGp("P");
	    		
				break;
			}
		}
		
		return (isBedStackable )? ydStkLocVO : null; // 검색위치를 찾았다면, ydStkLocVO 를 반환하고 아닌경우 null을 반환하여 분기처리할 수 있도록.
	}
	
	
	/**
	 * 혼적베드 검색 New--2025.06.04 callFunctionGetBedForMainWrk 용 함수
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO getBedWithSimilarGpForMainWrkNew(JDTORecord recInPara) throws JDTOException {

		String methodName				 = "getBedWithSimilarGpForMainWrkNew";
		String operationName			 = "혼적베드 To위치검색 for 주작업New";
		String logMsg					 = null;
		String rtnMsg 				     = YdConstant.RETN_CD_SUCCESS;
		
		String logId                     = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		logMsg = operationName+" (" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		
		String ydSchCd    		= ydDaoUtils.paraRecChkNull(recInPara,"YD_SCH_CD");
		String ydStkColGp    = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_COL_GP");  //권상지시위치 - 적치열
		String ydStkBedNo     = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_BED_NO"); //권상지시위치 - 적치베드
		String ydPilingCd   	= ydDaoUtils.paraRecChkNull(recInPara,"YD_PILING_CD");
		String ydEqpWrkSh      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_SH");						//크레인작업재료 총매수
		String ydEqpWrkWt      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_WT");					//크레인작업재료 총중량
		String ydEqpWrkT      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_T");					//크레인작업재료 총높이
		String ydToLocGuide = ydDaoUtils.paraRecChkNull(recInPara,"YD_TO_LOC_GUIDE");
		
		String ydMtlLGp 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		String ydMtlWGp 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		
		String fromDong     = ydDaoUtils.paraRecChkNull(recInPara,"FROM_DONG");
		String toDong       = ydDaoUtils.paraRecChkNull(recInPara,"TO_DONG");
		
		String spanOrder     = ydDaoUtils.paraRecChkNull(recInPara,"SPAN_ORDER");
		String scanDir       = ydDaoUtils.paraRecChkNull(recInPara,"SCAN_DIR");
		
		String stlNo       = ydDaoUtils.paraRecChkNull(recInPara,"STL_NO");//최하단 작업재료 재료번호
		
		String ydGp			= "";
    	String ydBayGp		= "";
    	

		
		YdStkLocVO		ydStkLocVO		= null;		
		ArrayList listToLoc 	        = new ArrayList();
		YdPlateCommDAO commDao = new YdPlateCommDAO(); 
		
		
		if(isRcvPlateBigCust(recInPara)){
			logMsg = operationName+"입고대형고객사는 혼적위치 탐색 skip";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			
			logMsg = operationName+" (" + methodName + ") 끝";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			return null;
		};
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		
		logMsg = "["+ operationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		ydUtils.displayRecord(operationName, recInPara);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		if( ydToLocGuide.length() == 8 ) {
			
			ydGp			= ydToLocGuide.substring(0, 1);
			ydBayGp			= ydToLocGuide.substring(1, 2);
			
		}else{
			//----------------------------------------------------------------------------------------------------------------------
			//	==>권상지시위치를 사용. : 2009.12.22
			//----------------------------------------------------------------------------------------------------------------------
			ydGp			= ydStkColGp.substring(0, 1);
			ydBayGp			= ydStkColGp.substring(1, 2);
			
			logMsg = "["+ operationName +"] 사용자지정위치(입고예정위치)가 존재하지 않으므로 권상지시위치를 사용";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
		}
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		recPara.setField("YD_GP"			, ydGp);
		recPara.setField("YD_BAY_GP"		, ydBayGp);
		recPara.setField("YD_STK_BED_L_GP"	, ydMtlLGp.substring(0, 1));
		recPara.setField("YD_STK_BED_W_GP"	, ydMtlWGp.substring(0, 1));
		recPara.setField("FROM_STK_COL_GP"	, ydGp + ydBayGp + fromDong); 
		recPara.setField("TO_STK_COL_GP"  	, ydGp + ydBayGp + toDong);
		recPara.setField("FR_YD_STK_BED_NO" , "01");
		recPara.setField("YD_EQP_WRK_SH"	, ydEqpWrkSh);
		recPara.setField("YD_EQP_WRK_WT"	, ydEqpWrkWt);
		recPara.setField("YD_EQP_WRK_T"		, ydEqpWrkT);
		recPara.setField("YD_SCH_CD"		, ydSchCd);
	
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	혼적베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		logMsg = "["+ operationName +"] 파라미터 설정 후 혼적베드 정보 조회 시작 ";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 베드 정보 조회 - 혼적베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		recPara.setField("STL_NO",	stlNo);
		
		JDTORecord recRecord = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		
		//PIDEV_S :병행가동용:PI_YD
		recPara.setField("PI_YD",    	ydGp);
		
		/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getOsPilingCdByStlNo_PIDEV*/
		rtnMsg= DaoManager.getYdStock(recPara, rsResult, 213);			
		
		rsResult.first();
		recRecord = rsResult.getRecord();
		
		String sGbn			= ydDaoUtils.paraRecChkNull(recRecord, "GBN");
		String sYdMtlWGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_MTL_W_GP");
		String sYdMtlLGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_MTL_L_GP");
		String sYdPilingCd	= ydDaoUtils.paraRecChkNull(recRecord, "YD_PILING_CD");
		String sDemanderCd	= ydDaoUtils.paraRecChkNull(recRecord, "DEMANDER_CD");
		String sYdStkBedLGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_STK_BED_L_GP");
		String sYdStkBedWGp	= ydDaoUtils.paraRecChkNull(recRecord, "YD_STK_BED_W_GP");
		String sShipCd		= ydDaoUtils.paraRecChkNull(recRecord, "SHIP_CD");
		String sDelTermCd	= ydDaoUtils.paraRecChkNull(recRecord, "DELIVER_TERM_CD"); // 검색
		String sCustCd		= ydDaoUtils.paraRecChkNull(recRecord, "CUST_CD");
		String sDetailArrCd	= ydDaoUtils.paraRecChkNull(recRecord, "DETAIL_ARR_CD");		

		ydUtils.putLogNew(szClassName, methodName, "sGbn=>"+sGbn, 				YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sYdMtlWGp=>"+sYdMtlWGp, 		YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sYdMtlLGp=>"+sYdMtlLGp, 		YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sYdPilingCd=>"+sYdPilingCd, 	YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sDemanderCd=>"+sDemanderCd, 	YdConstant.DEBUG, logId);
		
		ydUtils.putLogNew(szClassName, methodName, "sYdStkBedLGp=>"+sYdStkBedLGp,YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sYdStkBedWGp=>"+sYdStkBedWGp,YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sShipCd=>"+sShipCd, 			YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sDelTermCd=>"+sDelTermCd, 	YdConstant.DEBUG, logId);
		ydUtils.putLogNew(szClassName, methodName, "sDetailArrCd=>"+sDetailArrCd,YdConstant.DEBUG, logId);

		
		recPara.setField("YD_MTL_W_GP",		sYdMtlWGp);
		recPara.setField("YD_MTL_L_GP",		sYdMtlLGp);
		recPara.setField("YD_PILING_CD",	sYdPilingCd);
		recPara.setField("DEMANDER_CD",		sDemanderCd);
		recPara.setField("YD_STK_BED_L_GP",	sYdStkBedLGp);
		recPara.setField("YD_STK_BED_W_GP",	sYdStkBedWGp);
		recPara.setField("SHIP_CD",			sShipCd);
		recPara.setField("DELIVER_TERM_CD",	sDelTermCd);
		recPara.setField("CUST_CD",			sCustCd);
		recPara.setField("DETAIL_ARR_CD",	sDetailArrCd);
		recPara.setField("PI_YD",    	ydGp);
		
		int rtnVal = 0;
		/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBedReNew*/
		//rtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 624);			
		rtnVal = commDao.select(recPara, rsResult, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBedReNew2nd");
		
		logMsg = "["+ operationName +"] 혼적베드 정보 조회 완료 - 메세지 : " + rtnVal;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		//----------------------------------------------------------------------------------------------------------------------

			
		if( rtnVal>0 ) {
			srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD, logId);

		}

		
		//----------------------------------------------------------------------------------------------------------------------
	
		//----------------------------------------------------------------------------------------------------------------------
		// Sorting ?
		//----------------------------------------------------------------------------------------------------------------------
		if( listToLoc.size() > 0 ) {
			Collections.sort(listToLoc, new YdStkLocComparator());
		}
		//----------------------------------------------------------------------------------------------------------------------
		 
		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
				
		boolean isBedStackable = false;
		
		for(int i = 0; i < listToLoc.size(); i++ ) {
			
			ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
			
			logMsg = "["+ operationName +"] ["+( i + 1 )+"] 혼적베드"
					+ "["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";						
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
			
    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
    			
				isBedStackable		= true;
				logMsg = "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++ ["+( i + 1 )+"] 혼적 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
	    		
	    		//to위치 탐색방법 setting
				ydStkLocVO.setPlnLocDcsnGp("S");
	    		
				break;
			}
		}
		
		return (isBedStackable )? ydStkLocVO : null; // 검색위치를 찾았다면, ydStkLocVO 를 반환하고 아닌경우 null을 반환하여 분기처리할 수 있도록.
	}
	
	
	
	/**
	 * 공베드 검색 New--2025.06.04 callFunctionGetBedForMainWrk 용 함수
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO getEmptyBedWithSameLWGpNewForMainWrkNew(JDTORecord recInPara) throws JDTOException {

		String methodName				 = "getEmptyBedWithSameLWGpNewForMainWrkNew";
		String operationName			 = "공베드 To위치검색 for 주작업New";
		String logMsg					 = null;
		String rtnMsg 				     = YdConstant.RETN_CD_SUCCESS;
		
		String logId                     = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		logMsg = operationName+" (" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		
		String ydSchCd    		= ydDaoUtils.paraRecChkNull(recInPara,"YD_SCH_CD");
		String ydStkColGp    = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_COL_GP");  //권상지시위치 - 적치열
		String ydStkBedNo     = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_BED_NO"); //권상지시위치 - 적치베드
		String ydPilingCd   	= ydDaoUtils.paraRecChkNull(recInPara,"YD_PILING_CD");
		String ydEqpWrkSh      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_SH");						//크레인작업재료 총매수
		String ydEqpWrkWt      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_WT");					//크레인작업재료 총중량
		String ydEqpWrkT      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_T");					//크레인작업재료 총높이
		String ydToLocGuide = ydDaoUtils.paraRecChkNull(recInPara,"YD_TO_LOC_GUIDE");
		
		String ydMtlLGp 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		String ydMtlWGp 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		
		String ydCrnSchId       = ydDaoUtils.paraRecChkNull(recInPara, "YD_CRN_SCH_ID");
		
		String fromDong     = ydDaoUtils.paraRecChkNull(recInPara,"FROM_DONG");
		String toDong       = ydDaoUtils.paraRecChkNull(recInPara,"TO_DONG");
		
		String ydGp			= "";
    	String ydBayGp		= "";
		String ydSpanGp		= "";
    	
		
		YdStkLocVO		ydStkLocVO		= null;		
		ArrayList listToLoc 	        = new ArrayList();
	
		
		logMsg = "["+ operationName +"] 길이구분["+ydMtlLGp+"]/폭구분["+ydMtlWGp+"]이 동일한 적치가능한 공베드 조회 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		
		logMsg = "["+ operationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		ydUtils.displayRecord(operationName, recInPara);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		if( ydToLocGuide.length() == 8 ) {
			
			ydGp			= ydToLocGuide.substring(0, 1);
			ydBayGp			= ydToLocGuide.substring(1, 2);
			ydSpanGp		= ydToLocGuide.substring(2, 4);
			
		}else{
			//----------------------------------------------------------------------------------------------------------------------
			//	==>권상지시위치를 사용. : 2009.12.22
			//----------------------------------------------------------------------------------------------------------------------
			ydGp			= ydStkColGp.substring(0, 1);
			ydBayGp			= ydStkColGp.substring(1, 2);
			ydSpanGp		= ydStkColGp.substring(2, 4);
			
			logMsg = "["+ operationName +"] 사용자지정위치(입고예정위치)가 존재하지 않으므로 권상지시위치를 사용";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
		}
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		recPara.setField("YD_GP"			, ydGp);
		recPara.setField("YD_BAY_GP"		, ydBayGp);
		recPara.setField("YD_STK_BED_L_GP"	, ydMtlLGp.substring(0, 1));
		recPara.setField("YD_STK_BED_W_GP"	, ydMtlWGp.substring(0, 1));
		recPara.setField("FROM_STK_COL_GP"	, ydGp + ydBayGp + fromDong); 
		recPara.setField("TO_STK_COL_GP"  	, ydGp + ydBayGp + toDong);
		recPara.setField("FR_YD_STK_BED_NO" , "01");
		recPara.setField("YD_EQP_WRK_SH"	, ydEqpWrkSh);
		recPara.setField("YD_EQP_WRK_WT"	, ydEqpWrkWt);
		recPara.setField("YD_EQP_WRK_T"		, ydEqpWrkT);
		recPara.setField("YD_SCH_CD"		, ydSchCd);
		recPara.setField("BED_SEARCH_GP", 	"1");	//Full Scan
		recPara.setField("YD_CRN_SCH_ID", 	ydCrnSchId);	//Full Scan
	
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	공베드 정보 조회 
		//----------------------------------------------------------------------------------------------------------------------
		logMsg = "["+ operationName +"] 파라미터 설정 후  szYD_CRN_SCH_ID["+ydCrnSchId+"]로  모든 공베드 정보 조회 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		JDTORecordSet	rsResult		= null;
	
		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 길이구분/폭구분을  가진 모든 공베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		logMsg = "["+ operationName +"] 파라미터 설정 후 동일한 길이구분["+ydMtlLGp+"], 폭구분["+ydMtlWGp+"]을 가진 모든 공베드 정보 조회 시작 ";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 베드 정보 조회 - 동일길이/폭구분을 가진  모든 공베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		if( ydSpanGp.equals(YdConstant.SPAN_ORDER_NEW_01) ||
				ydSpanGp.equals(YdConstant.SPAN_ORDER_NEW_04) || 
				ydSpanGp.equals(YdConstant.SPAN_ORDER_NEW_05) || 
				ydSpanGp.equals(YdConstant.SPAN_ORDER_NEW_06) ) {
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAscNew*/
			rtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 306);
			
		} else {
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColDescNew*/
			rtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 307);	
			
		}
		
		logMsg = "["+ operationName +"] 동일길이/폭구분을 가진  모든 공베드 정보 조회 완료 - 메세지 : " + rtnMsg;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------
		
		if( rtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {

			srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_EMPTY_BED);
			
		}
		
		logMsg = "["+ operationName +"] 파라미터 설정 후 동일한 길이구분["+ydMtlLGp+"], 폭구분["+ydMtlWGp+"]을 가진 모든 공베드 정보 조회 완료 ";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------------------
	
		//----------------------------------------------------------------------------------------------------------------------
		// Sorting ?
		//----------------------------------------------------------------------------------------------------------------------
		if( listToLoc.size() > 0 ) {
			Collections.sort(listToLoc, new YdStkLocComparator());
		}
		//----------------------------------------------------------------------------------------------------------------------
		 
		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
				
		boolean isBedStackable = false;
		
		for(int i = 0; i < listToLoc.size(); i++ ) {
			
			ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
			
			logMsg = "["+ operationName +"] ["+( i + 1 )+"] 길이구분["+ydMtlLGp+"]/폭구분["+ydMtlWGp+"]"
					+ "이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";			
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
			
    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
    			
				isBedStackable		= true;
				logMsg = "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++ ["+( i + 1 )+"] 길이구분["+ydMtlLGp+"]/폭구분["+ydMtlWGp+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
	    		
	    		//to위치 탐색방법 setting
				ydStkLocVO.setPlnLocDcsnGp("E");
	    		
				break;
			}
		}
		
		return (isBedStackable )? ydStkLocVO : null; // 검색위치를 찾았다면, ydStkLocVO 를 반환하고 아닌경우 null을 반환하여 분기처리할 수 있도록.
	}
	
	
	/**
	 * 단일파일링베드 검색--2025.06.04 callFunctionGetBedForMainWrk 용 함수
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO getSinglePilingBedForMainWrk(JDTORecord recInPara) throws JDTOException {

		String methodName				 = "getSinglePilingBedForMainWrk";
		String operationName			 = "단일파일링 To위치검색 for 주작업";
		String logMsg					 = null;
		String rtnMsg 				     = YdConstant.RETN_CD_SUCCESS;
		
		String logId                     = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		logMsg = operationName+" (" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		YdPlateCommDAO commDao = new YdPlateCommDAO(); 
		
		String ydSchCd    		= ydDaoUtils.paraRecChkNull(recInPara,"YD_SCH_CD");
		String ydStkColGp    = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_COL_GP");  //권상지시위치 - 적치열
		String ydStkBedNo     = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_BED_NO"); //권상지시위치 - 적치베드
		String ydPilingCd   	= ydDaoUtils.paraRecChkNull(recInPara,"YD_PILING_CD");
		String ydEqpWrkSh      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_SH");						//크레인작업재료 총매수
		String ydEqpWrkWt      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_WT");					//크레인작업재료 총중량
		String ydEqpWrkT      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_T");					//크레인작업재료 총높이
		String ydToLocGuide = ydDaoUtils.paraRecChkNull(recInPara,"YD_TO_LOC_GUIDE");
		
		String ydMtlLGp 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		String ydMtlWGp 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		
		String fromDong     = ydDaoUtils.paraRecChkNull(recInPara,"FROM_DONG");
		String toDong       = ydDaoUtils.paraRecChkNull(recInPara,"TO_DONG");
		
		String spanOrder     = ydDaoUtils.paraRecChkNull(recInPara,"SPAN_ORDER");
		String scanDir       = ydDaoUtils.paraRecChkNull(recInPara,"SCAN_DIR");
		
		String stlNo       = ydDaoUtils.paraRecChkNull(recInPara,"STL_NO");//최하단 작업재료 재료번호
		
		

		String ydGp			= ydStkColGp.substring(0, 1);
    	String ydBayGp		= ydStkColGp.substring(1, 2);
    	
		YdStkLocVO		ydStkLocVO		= null;		
		ArrayList listToLoc 	        = new ArrayList();
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		
		logMsg = "["+ operationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		ydUtils.displayRecord(operationName, recInPara);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
		if( ydToLocGuide.length() == 8 ) {
			
			ydGp			= ydToLocGuide.substring(0, 1);
			ydBayGp			= ydToLocGuide.substring(1, 2);
			
		}else{
			//----------------------------------------------------------------------------------------------------------------------
			//	==>권상지시위치를 사용. : 2009.12.22
			//----------------------------------------------------------------------------------------------------------------------
			ydGp			= ydStkColGp.substring(0, 1);
			ydBayGp			= ydStkColGp.substring(1, 2);
			
			logMsg = "["+ operationName +"] 사용자지정위치(입고예정위치)가 존재하지 않으므로 권상지시위치를 사용";
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
			
			//----------------------------------------------------------------------------------------------------------------------
		}
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		recPara.setField("FR_YD_STK_COL_GP", 	ydStkColGp);
		recPara.setField("FR_YD_STK_BED_NO", 	ydStkBedNo);
		
		recPara.setField("YD_GP"			, ydGp);
		recPara.setField("YD_BAY_GP"		, ydBayGp);
		recPara.setField("YD_PILING_CD"		, ydPilingCd);
		recPara.setField("FROM_STK_COL_GP"	, ydGp + ydBayGp + fromDong); 
		recPara.setField("TO_STK_COL_GP"  	, ydGp + ydBayGp + toDong);
		recPara.setField("FR_YD_STK_BED_NO" , "01");
		recPara.setField("YD_MTL_W_GP",		ydMtlWGp);
		recPara.setField("YD_MTL_L_GP",		ydMtlLGp);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	단일 파일링만 적치되어있는 모든 베드 탐색
		//----------------------------------------------------------------------------------------------------------------------
		logMsg = "["+ operationName +"] 파라미터 설정 후 단일 Piling Code 인 모든 베드 정보 조회 시작 ";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 단 정보 조회 - 동일한 Piling Code를 가진 최상단 재료가 있는 모든 베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		int rtnVal = 0;
		/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatioReNew*/
		//rtnMsg = DaoManager.getYdStklyr(recPara, rsResult, 624);			
		rtnVal = commDao.select(recPara, rsResult, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getSinglePilingBedForMainWrk");


		logMsg = "["+ operationName +"] 단일 Piling Code를 가진 모든 베드 정보 조회 완료 - 메세지 : " + rtnVal;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		//----------------------------------------------------------------------------------------------------------------------
	
		if( rtnVal>0 ) {
			srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD, logId);

		}

		
		//----------------------------------------------------------------------------------------------------------------------
	
		//----------------------------------------------------------------------------------------------------------------------
		// Sorting ?
		//----------------------------------------------------------------------------------------------------------------------
		if( listToLoc.size() > 0 ) {
			Collections.sort(listToLoc, new YdStkLocComparator());
		}
		//----------------------------------------------------------------------------------------------------------------------
		 
		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
				
		boolean isBedStackable = false;
		
		for(int i = 0; i < listToLoc.size(); i++ ) {
			
			ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
			
			logMsg = "["+ operationName +"] ["+( i + 1 )+"] 단일 파일링베드"
					+ "["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";						
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
			
    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
    			
				isBedStackable		= true;
				logMsg = "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++ ["+( i + 1 )+"] 단일 파일링베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
	    		
	    		//to위치 탐색방법 setting
				ydStkLocVO.setPlnLocDcsnGp("I");
	    		
				break;
			}
		}
		
		
		return (isBedStackable )? ydStkLocVO : null; // 검색위치를 찾았다면, ydStkLocVO 를 반환하고 아닌경우 null을 반환하여 분기처리할 수 있도록.
	}
	
	/**
	 * 대체 to위치 검색 rt 에서 가까운순으로
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static YdStkLocVO getBedWithAltRangeAndRt(JDTORecord recInPara) throws JDTOException {

		String methodName				 = "getBedWithAltRangeAndRt";
		String operationName			 = "R/T에서 가까운순으로 대체to위치 검색";
		String logMsg					 = null;
	
		String logId                     = ydUtils.getJDTOLogId(recInPara, "T");	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		logMsg = operationName+" (" + methodName + ") 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		YdPlateCommDAO commDao = new YdPlateCommDAO(); 

		
		String ydStkColGp    = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_COL_GP");  //권상지시위치 - 적치열
		String ydStkBedNo     = ydDaoUtils.paraRecChkNull(recInPara,"YD_STK_BED_NO"); //권상지시위치 - 적치베드

		String ydEqpWrkSh      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_SH");						//크레인작업재료 총매수
		String ydEqpWrkWt      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_WT");					//크레인작업재료 총중량
		String ydEqpWrkT      	= ydDaoUtils.paraRecChkNull(recInPara,"YD_EQP_WRK_T");					//크레인작업재료 총높이
		
		String ydMtlLGp 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		String ydMtlWGp 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분

		String altFrom     = ydDaoUtils.paraRecChkNull(recInPara,"AL_FROM");
		String altTo       = ydDaoUtils.paraRecChkNull(recInPara,"AL_TO");
		

		String ydGp			= ydStkColGp.substring(0, 1);
    	String ydBayGp		= ydStkColGp.substring(1, 2);
    	
		YdStkLocVO		ydStkLocVO		= null;		
		ArrayList listToLoc 	        = new ArrayList();
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		
		logMsg = "["+ operationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);
		
		ydUtils.displayRecord(operationName, recInPara);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		recPara.setField("FR_YD_STK_COL_GP", 	ydStkColGp);
		recPara.setField("FR_YD_STK_BED_NO", 	ydStkBedNo);
		
		recPara.setField("YD_GP"			, ydGp);
		recPara.setField("YD_BAY_GP"		, ydBayGp);
		recPara.setField("FROM_STK_COL_GP"	, altFrom); 
		recPara.setField("TO_STK_COL_GP"  	,  altTo);
		recPara.setField("FR_YD_STK_BED_NO" , ydStkBedNo);
		recPara.setField("YD_MTL_W_GP",		ydMtlWGp);
		recPara.setField("YD_MTL_L_GP",		ydMtlLGp);
		
		recPara.setField("YD_EQP_WRK_SH"	, ydEqpWrkSh);
		recPara.setField("YD_EQP_WRK_WT"	, ydEqpWrkWt);
		recPara.setField("YD_EQP_WRK_T"		, ydEqpWrkT);
		//----------------------------------------------------------------------------------------------------------------------
		
		//----------------------------------------------------------------------------------------------------------------------
		//	r/t에서 가까운 대체위치 검색 
		//----------------------------------------------------------------------------------------------------------------------
		logMsg = "["+ operationName +"] 파라미터 설정 후 대체위치 검색 시작";
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		
		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 단 정보 조회 - 대체위치 검색 
		//----------------------------------------------------------------------------------------------------------------------
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		int rtnVal = 0;		
		rtnVal = commDao.select(recPara, rsResult, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getBedWithAltRangeAndRt");


		logMsg = "["+ operationName +"] r/t에서 가까운 순으로 대체위치 검색 완료 - 메세지 : " + rtnVal;
		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.INFO, logId);
		//----------------------------------------------------------------------------------------------------------------------
	
		if( rtnVal>0 ) {
			srchNconvRecord2Vo("","",recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD, logId);

		}

		
		//----------------------------------------------------------------------------------------------------------------------
	
		//----------------------------------------------------------------------------------------------------------------------
		// Sorting ?
		//----------------------------------------------------------------------------------------------------------------------
		if( listToLoc.size() > 0 ) {
			Collections.sort(listToLoc, new YdStkLocComparator());
		}
		//----------------------------------------------------------------------------------------------------------------------
		 
		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
				
		boolean isBedStackable = false;
		
		for(int i = 0; i < listToLoc.size(); i++ ) {
			
			ydStkLocVO = (YdStkLocVO)listToLoc.get(i);
			
			logMsg = "["+ operationName +"] ["+( i + 1 )+"] 대체위치 베드"
					+ "["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";						
			ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
			
    		if( ydStkLocVO.getYdBedErrCd() == YdConstant.YD_BED_STACKABLE ) {
    			
				isBedStackable		= true;
				logMsg = "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++ ["+( i + 1 )+"]  대체위치 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++\n";
				logMsg += "["+ operationName +"] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydUtils.putLogNew(szClassName, methodName, logMsg, YdConstant.DEBUG, logId);	
	    		
	    		//to위치 탐색방법 setting
				ydStkLocVO.setPlnLocDcsnGp("A");
	    		
				break;
			}
		}
		
		
		return (isBedStackable )? ydStkLocVO : null; // 검색위치를 찾았다면, ydStkLocVO 를 반환하고 아닌경우 null을 반환하여 분기처리할 수 있도록.
	}
	
}
