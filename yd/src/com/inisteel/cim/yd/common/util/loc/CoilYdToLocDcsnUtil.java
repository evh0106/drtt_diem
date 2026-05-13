/**
 * YD에서 사용되는 TO위치결정하는 클래스
 */
package com.inisteel.cim.yd.common.util.loc;

import java.util.ArrayList;
import java.util.List;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ptOsCommDao.PtOsCommDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydLocSrchRngDao.YdLocSrchRngDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydTcarFtmvMtlDao.YdTcarFtmvMtlDao;
import com.inisteel.cim.yd.common.rule.GetBreRule0;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.crn.CrnSchUtil;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import jspeed.base.util.StringHelper;
import edu.emory.mathcs.backport.java.util.Collections;
import com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao;
import com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO;


/**
 * @author 임춘수
 *
 */
public class CoilYdToLocDcsnUtil {
	private static String szClassName = CoilYdToLocDcsnUtil.class.getName();
	private static YdUtils ydUtils = new YdUtils();
	private static YdDaoUtils ydDaoUtils = new YdDaoUtils();
	private String szSessionName=getClass().getName();
	private SlabYdCommDAO commDao= new SlabYdCommDAO ();
	
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
		
		if( szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD)) {			//C열연코일소재야드
			
		}else if( szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD)) {			//C열연코일제품야드
		}
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
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
		String szMethodName			= "procAsgnedBedStackable";
		String szOperationName		= "To위치결정-사용자지정(공통)";
		String szLogMsg				= null;
		String szRtnMsg 			= YdConstant.RETN_CD_SUCCESS;
		int	intYD_BED_ERR_CD		= 0;
		
		JDTORecord recTemp			= null;
		JDTORecordSet rsTemp		= null;
		
		String szYD_TO_LOC_GUIDE 	= null;
		String szYD_STK_COL_GP		= null;
		String szYD_STK_BED_NO		= null;
		String szYD_STK_LYR_NO		= null;
		String szSTL_NO				= null;
		String szYD_SCH_CD			= null;
		String szYD_STK_BED_ACT_STAT	= null;
		String szYD_STK_BED_WHIO_STAT	= null;
		String szYD_STK_LYR_MTL_STAT	= null;
//		int intYD_STK_BED_LYR_MAX	= 0;					//베드정보 - 단MAX
//		int intYD_STK_BED_WT_MAX	= 0;					//베드정보 - 총중량
//		double dblYD_STK_BED_H_MAX	= 0;					//베드정보 - 총높이
//		
//		int intYD_MTL_SH			= 0;					//적치된 재료의 총매수
//		int intYD_MTL_WT_SUM		= 0;					//적치된 재료의 총중량
//		double dblYD_MTL_T_SUM		= 0;					//적치된 재료의 총두께
//		
//		int intYD_EQP_WRK_SH		= 0;					//작업총매수
//		int intYD_EQP_WRK_WT		= 0;					//작업총중량
//		double dblYD_EQP_WRK_T		= 0;					//작업총두께
		
		YdStkLocVO ydStkLocVO		= null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		szYD_TO_LOC_GUIDE = ydDaoUtils.paraRecChkNull(recPara, "YD_TO_LOC_GUIDE");				//사용자지정위치
		szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");							//스케줄코드
		
//		intYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNullInt(recPara, "YD_EQP_WRK_SH");				//작업총매수
//		intYD_EQP_WRK_WT = ydDaoUtils.paraRecChkNullInt(recPara, "YD_EQP_WRK_WT");				//작업총중량
//		dblYD_EQP_WRK_T = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_EQP_WRK_T");				//작업총두께
		
		szLogMsg = "["+ szOperationName +"] 파라미터로 전달된 TO위치가이드["+szYD_TO_LOC_GUIDE+"]입니다.";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		if( szYD_TO_LOC_GUIDE.equals("") ) {
			szLogMsg = "["+ szOperationName +"] TO위치가이드가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
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
		szYD_STK_BED_ACT_STAT 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_ACT_STAT");
		szYD_STK_BED_WHIO_STAT 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT");
		szYD_STK_LYR_MTL_STAT 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT");
		
		if( !szYD_STK_BED_ACT_STAT.equals(YdConstant.YD_STK_BED_ACTIVE) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 활성상태["+szYD_STK_BED_ACT_STAT+"]가 적치가능상태가 아닙니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_BED_INACT;
		}
		
		if( !szYD_STK_BED_WHIO_STAT.equals(YdConstant.YD_STK_BED_WHIO_ENABLE) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 야드적치Bed입출고상태["+szYD_STK_BED_WHIO_STAT+"]가 입고가능상태가 아닙니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_BED_WHIO_NOT_IN;
		}
		
		if( szYD_STK_LYR_MTL_STAT.equals(YdConstant.YD_STK_LYR_MTL_STAT_UN_WAIT)) {				//권상대기이면 적치불가능
			szLogMsg = "["+ szOperationName +"] 적치된 재료["+szSTL_NO+"]보다 적치재료상태["+szYD_STK_LYR_MTL_STAT+"]가 권상대기이므로 적치불가능";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_BED_UN_WAIT;
		}else if( szYD_STK_LYR_MTL_STAT.equals(YdConstant.YD_STK_LYR_MTL_STAT_DN_WAIT)) {		//권하대기이면 스케줄코드비교
			
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
		
		ydStkLocVO = procRecord2StkLoc(recTemp);
		
		szLogMsg = "["+ szOperationName +"] 레코드를 VO객체로 변환 완료  ";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		ydStkLocVO.setSeq(YdConstant.TO_LOC_PRIOR_USER);
		ydStkLocVO.setPrior(YdConstant.TO_LOC_PRIOR_USER);
		
		listToLoc.add(ydStkLocVO);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		
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
		
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		//szYD_TO_LOC_GUIDE 	= ydDaoUtils.paraRecChkNull(recPara, "YD_TO_LOC_GUIDE");			//사용자지정위치
		
		szYD_STK_COL_GP		= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");				//적치열구분
		szYD_STK_BED_NO		= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");				//적치베드번호
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");					//스케줄코드
		
//		intYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNullInt(recPara, "YD_EQP_WRK_SH");				//크레인작업총매수
//		intYD_EQP_WRK_WT = ydDaoUtils.paraRecChkNullInt(recPara, "YD_EQP_WRK_WT");				//크레인작업총중량
//		dblYD_EQP_WRK_T = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_EQP_WRK_T");				//크레인작업총두께
		
//		szLogMsg = "["+ szOperationName +"] 파라미터로 전달된 TO위치가이드["+szYD_TO_LOC_GUIDE+"]입니다.";
//		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
		
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
//		szYD_STK_COL_GP = szYD_TO_LOC_GUIDE.substring(0, 6);
//		szYD_STK_BED_NO = szYD_TO_LOC_GUIDE.substring(6);
		
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
		szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT");
		
		if( !szYD_STK_BED_ACT_STAT.equals(YdConstant.YD_STK_BED_ACTIVE) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 활성상태["+szYD_STK_BED_ACT_STAT+"]가 적치가능상태가 아닙니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_BED_INACT;
		}
		
		if( !szYD_STK_BED_WHIO_STAT.equals(YdConstant.YD_STK_BED_WHIO_ENABLE) ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]의 입출고상태["+szYD_STK_BED_WHIO_STAT+"]가 입고가능상태가 아닙니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_BED_WHIO_NOT_IN;
		}
		
		if( szYD_STK_LYR_MTL_STAT.equals(YdConstant.YD_STK_LYR_MTL_STAT_UN_WAIT)) {				//권상대기이면 적치불가능
			szLogMsg = "["+ szOperationName +"] 적치된 재료["+szSTL_NO+"]보다 적치재료상태["+szYD_STK_LYR_MTL_STAT+"]가 권상대기이므로 적치불가능";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_BED_UN_WAIT;
		}else if( szYD_STK_LYR_MTL_STAT.equals(YdConstant.YD_STK_LYR_MTL_STAT_DN_WAIT)) {		//권하대기이면 스케줄코드비교
			
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
		
		//ydStkLocVO.setSeq(YdConstant.TO_LOC_PRIOR_USER);
		//ydStkLocVO.setPrior(YdConstant.TO_LOC_PRIOR_USER);
		
		//listToLoc.add(ydStkLocVO);
		
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
		String szMethodName			= "getYdStkBed";
		String szOperationName		= "베드분석정보조회";
		String szLogMsg				= null;
		String szRtnMsg 			= YdConstant.RETN_CD_SUCCESS;
		int intRtnVal				= -100;
		String szYD_STK_COL_GP		= null;
		String szYD_STK_BED_NO		= null;
		
		YdStkBedDao	ydStkBedDao		= new YdStkBedDao();

		szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
		szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
		
		if( szQUERY_TYPE.equals(YdConstant.MTL_STAT_C_U_D)) {
			szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 적치베드:"+szYD_STK_BED_NO+"]의 적치중,권상,권하대기인 재료를 조회 시작";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsOutPara, 24);
			
			if( intRtnVal == 0 ) {
				szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 적치베드:"+szYD_STK_BED_NO+"]의 적치중,권상,권하대기인 재료를 조회 시 정보가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if( intRtnVal < 0 ) {
				szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 적치베드:"+szYD_STK_BED_NO+"] 의 적치중,권상,권하대기인 재료를 조회 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}else{
				szLogMsg = "["+ szOperationName +"] 해당 베드[적치열:"+szYD_STK_COL_GP+", 적치베드:"+szYD_STK_BED_NO+"] 의 적치중,권상,권하대기인 재료정보가 존재합니다.";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}
			
		}else{
			szLogMsg = "["+ szOperationName +"] 지원하지 않는 쿼리타입["+szQUERY_TYPE+"]입니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
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
		//JDTORecord		recTemp			= null;
		//JDTORecord		recTemp1		= null;
		
//		YdStkLocVO	ydStkLocVO			= null;
		
//		String szYD_STK_COL_GP			= null;
//		String szYD_STK_BED_NO			= null;
//		String szYD_EQP_WRK_SH			= null;
//		String szYD_EQP_WRK_WT			= null;
//		String szYD_EQP_WRK_T			= null;
//		String szYD_SCH_CD				= null;
		
		String szCOL_ORDER				= null;
//		int	intLOOP_I					= 0;
		
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
//		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_SH");
//		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_WT");
//		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_T");
//		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");
		
		
		szCOL_ORDER 		= ydDaoUtils.paraRecChkNull(recPara, "COL_ORDER");
//		intLOOP_I 			= ydDaoUtils.paraRecChkNullInt(recPara, "LOOP_I");
		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 베드 정보 조회 - 동일길이/폭구분을 가진  모든 공베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		if( szCOL_ORDER.equals(YdConstant.ORDER_BY_ASC)) {
			szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 25);
		}else if( szCOL_ORDER.equals(YdConstant.ORDER_BY_DESC)) {
			szRtnMsg = DaoManager.getYdStkbed(recPara, rsResult, 26);
		}
		
		
		szLogMsg = "["+ szOperationName +"] 동일길이/폭구분을 가진  모든 공베드 정보 조회 완료 - 메세지 : " + szRtnMsg;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		//----------------------------------------------------------------------------------------------------------------------
		
		//recTemp1 = JDTORecordFactory.getInstance().create();
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//			for(int i = 1; i <= rsResult.size(); i++) {
//				rsResult.absolute(i);
//				recTemp = rsResult.getRecord();
//				
//				szLogMsg = "["+ szOperationName +"] 레코드 추출["+i+"]";
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
//				
//				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
//				szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO");
//				
//				recTemp1.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
//				recTemp1.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
//				recTemp1.setField("YD_EQP_WRK_SH", 		szYD_EQP_WRK_SH);
//				recTemp1.setField("YD_EQP_WRK_WT", 		szYD_EQP_WRK_WT);
//				recTemp1.setField("YD_EQP_WRK_T", 		szYD_EQP_WRK_T);
//				recTemp1.setField("YD_SCH_CD", 			szYD_SCH_CD);
//				
//				ydStkLocVO		= new YdStkLocVO();
//				
//				szLogMsg = "["+ szOperationName +"] 베드["+szYD_STK_COL_GP+" - "+szYD_STK_BED_NO+"]가 적치가능한 지 확인 시작";
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
//				
//				szRtnMsg = procBedStackable(recTemp1, ydStkLocVO, szMethodName);
//				
//				szLogMsg = "["+ szOperationName +"] 베드["+szYD_STK_COL_GP+" - "+szYD_STK_BED_NO+"]가 적치가능한 지 확인 완료";
//				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
//				
//				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//				
//					ydStkLocVO.setPrior(YdConstant.TO_LOC_PRIOR_PLATE_SAME_PILING_CD);
//					ydStkLocVO.setSeq(i + ( intLOOP_I * YdConstant.TO_LOC_PRIOR_STEP ));
//					
//					listToLoc.add(ydStkLocVO);
//				}
//			}
			srchNconvRecord2Vo(recPara, rsResult, listToLoc, YdConstant.TO_LOC_PRIOR_PLATE_EMPTY_BED);
			
		}
		
		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
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
			JDTORecord recPara
			, JDTORecordSet	rsResult
			, List listToLoc
			, int intPRIOR
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
		
		recTemp1 = JDTORecordFactory.getInstance().create();
		for(int i = 1; i <= rsResult.size(); i++) {
			rsResult.absolute(i);
			recTemp = rsResult.getRecord();
			
			szLogMsg = "["+ szOperationName +"] 레코드 추출["+i+"]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO");
			
			recTemp1.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
			recTemp1.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
			recTemp1.setField("YD_EQP_WRK_SH", 		szYD_EQP_WRK_SH);
			recTemp1.setField("YD_EQP_WRK_WT", 		szYD_EQP_WRK_WT);
			recTemp1.setField("YD_EQP_WRK_T", 		szYD_EQP_WRK_T);
			recTemp1.setField("YD_SCH_CD", 			szYD_SCH_CD);
			
			ydStkLocVO		= new YdStkLocVO();
			
			szLogMsg = "["+ szOperationName +"] 베드["+szYD_STK_COL_GP+" - "+szYD_STK_BED_NO+"]가 적치가능한 지 확인 시작";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			szRtnMsg = procBedStackable(recTemp1, ydStkLocVO, szMethodName);
			
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
	 * COIL 폭비교
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
	 * [A] 오퍼레이션명 : 코일야드 보조작업To위치결정:보조작업인 경우(나선형검색)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procCoilYdAidWkToPosDecision(String sYdCrnSchId, String strStlNo, String strColGp, String strBedNo, String strLyrNo, String szSchCd, String szEqpId) throws JDTOException  {
		
//kkk		//코일공통 DAO
		PtOsCommDao ptOsCommDao     = new PtOsCommDao();
		CoilGdsJspDao CoilGdsJspDao = new CoilGdsJspDao();
		//리턴값(int)
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"procCoilYdAidWkToPosDecision";
		String szOperationName	=	"코일야드 보조작업To위치결정";
		JDTORecord recCoil1      = null;		
		JDTORecord recPara 		= null;
		JDTORecordSet outRecSet = null;
		JDTORecord recCoil      = null;
		JDTORecordSet outRecSet1 = null;		
		String szFieldName		= null;
		String szFieldValue		= null;
		String szStlNo          = null;
		String szColGp          = null;
		String szBedNo          = null;
		String szLyrNo          = null;
		String szCurrProgCd     = null;
		String szYdGp           = null;
		JDTORecord outRecord  		= JDTORecordFactory.getInstance().create(); 
		JDTORecord outRecord2  		= JDTORecordFactory.getInstance().create(); 
		String szRouteGp        = null;
		boolean blnRtnVal = true;
		
		String  sRTN_BED = "";
		JDTORecord 		outRecord1 	= JDTORecordFactory.getInstance().create();
//		String  szRtnVal = YdConstant.RETN_CD_FAILURE;
		String sRTN_MSG = "";
		String szRouteGpTemp        = null;
		String sORD_YEOJAE_GP 	= null;
		String sDELIVER_TERM_CD = null;
		String sYD_COIL_OUTDIA_GRP_GP = null;		
		try {
			
			szStlNo = strStlNo;
			szColGp = strColGp;
			szBedNo = strBedNo;
			szLyrNo = strLyrNo;
			
			if("".equals(szStlNo)) {
				szMsg = "재료번호가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", szMsg);	

				return outRecord;
			} else {
				szMsg = "전달받은 재료번호는 [" + szStlNo + "] 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			if("".equals(szColGp)) {
				szMsg = "적치열이 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", szMsg);	

				return outRecord;
			} else {
				szMsg = "전달받은 적치열은 [" + szColGp + "] 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			if("".equals(szBedNo)) {
				szMsg = "적치배드가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", szMsg);	

				return outRecord;
			} else {
				szMsg = "전달받은 적치배드는 [" + szBedNo + "] 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			recPara	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szStlNo);
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			
			intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet, 19);
			
			//리턴값 메세지처리
			if (intRtnVal == 0) {
				szMsg = "재료번호(" + szStlNo + ")에 대한 코일공통데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	

				return outRecord;
			} else if (intRtnVal < 0) {
				szMsg = "재료번호(" + szStlNo + ")로 코일공통데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	

				return outRecord;
			}
			
			outRecSet.absolute(1);
			recCoil = outRecSet.getRecord();
			
			szCurrProgCd	= ydDaoUtils.paraRecChkNull(recCoil, "CURR_PROG_CD");
			//szYdGp 			= ydDaoUtils.paraRecChkNull(recCoil, "YD_GP");
			szYdGp			= szColGp.substring(0 , 1);
			
			if("".equals(szYdGp)) {
				szMsg = "재료번호(" + szStlNo + ")에 대한 재료진도와 야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				szYdGp 			= ydDaoUtils.paraRecChkNull(recCoil, "YD_GP");
			}
			
			
			if ("".equals(szCurrProgCd) || "".equals(szYdGp)) {
				szMsg = "재료번호(" + szStlNo + ")에 대한 재료진도와 야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", szMsg);	

				return outRecord;

			}
//F0
			if ("J".equals(szYdGp)){
				sORD_YEOJAE_GP 	= ydDaoUtils.paraRecChkNull(recCoil, "ORD_YEOJAE_GP");
				sDELIVER_TERM_CD= ydDaoUtils.paraRecChkNull(recCoil, "DELIVER_TERM_CD");
	    		recPara = JDTORecordFactory.getInstance().create();
				
	    		if(sORD_YEOJAE_GP.equals("2")) {
		    		recPara.setField("V_ORD_YEOJAE_GP"		, sORD_YEOJAE_GP);
	    		} else {
		    		recPara.setField("V_ORD_YEOJAE_GP"		, sORD_YEOJAE_GP);
	    			recPara.setField("V_ITM_NM"				, sDELIVER_TERM_CD.substring(0, 1));
	    		}	
    			outRecSet1 = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	    /*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtCoilCommByCoilNo */
    			outRecSet1 = CoilGdsJspDao.getYDB700ComboList(recPara);
    			if(outRecSet1.size() != 1) {
    				outRecord.setField("RTN_CD" , "-1");	
    				outRecord.setField("RTN_MSG", "제품검색조건(BRE) DATA 이상.");	
    				return outRecord;				
    				
    			}
    			outRecSet1.absolute(1);
    			
    			recCoil1 = outRecSet1.getRecord();
    			szRouteGpTemp	= ydDaoUtils.paraRecChkNull(recCoil1, "CODE");
    			
    			if("".equals(szRouteGpTemp)){
    				outRecord.setField("RTN_CD" , "-1");	
    				outRecord.setField("RTN_MSG", "제품검색조건(BRE) DATA 이상.");	
    				return outRecord;				
    			} else {
    				szRouteGp = szRouteGpTemp;
    			}
 			}			
			
//			if ((("B".equals(szCurrProgCd) || 
//					 "C".equals(szCurrProgCd) ||
//					 "D".equals(szCurrProgCd) ||
//					 "Y".equals(szCurrProgCd) || 
//					 "F".equals(szCurrProgCd) || 
//					 "G".equals(szCurrProgCd) || 
//					 "H".equals(szCurrProgCd) || 
//					 "K".equals(szCurrProgCd) || 
//					 "L".equals(szCurrProgCd) || 
//					 "E".equals(szCurrProgCd) || 
//					 "R".equals(szCurrProgCd) ||
//					 "J".equals(szCurrProgCd) ) && "H".equals(szYdGp))){
			if("H".equals(szYdGp)){
				 // 코일소재야드 To위치bed검색 Method 호출보조작업
				sRTN_BED = searchCoilYdAidWkToPosition(szStlNo, szColGp, szBedNo, szLyrNo);
		
//			} else if ((("G".equals(szCurrProgCd) ||
//					    "B".equals(szCurrProgCd) ||
//						"D".equals(szCurrProgCd) ||
//						"E".equals(szCurrProgCd) ||
//						"H".equals(szCurrProgCd) || 
//						"K".equals(szCurrProgCd) || 
//						"L".equals(szCurrProgCd) || 
//						"O".equals(szCurrProgCd) ||
//						"Z".equals(szCurrProgCd) ||
//						"M".equals(szCurrProgCd) ||
//						"N".equals(szCurrProgCd) ||
//						"F".equals(szCurrProgCd) || 
//						"J".equals(szCurrProgCd) ) && "J".equals(szYdGp))){
			} else if("J".equals(szYdGp)){
				// 코일창고야드 To위치검색 bed Method 호출보조작업
				sRTN_BED = searchCoilGdsYdAidWkToPosition(szStlNo, szColGp, szBedNo, szLyrNo, szEqpId);
				  
			} else {
				
				szMsg = "재료번호(" + szStlNo + ")가 코일야드, 열연창고 해당하지 않는 진도코드 입니다.진도:"+szCurrProgCd+" 야드구분:"+szYdGp;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

//LOG_TABLE    					
				EJBConnector ejbConn = null;
				JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
				recLog.setField("STL_NO"			, szStlNo);
				recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
				recLog.setField("YD_GP"				, szYdGp);
				recLog.setField("YD_SCH_CD"			, szSchCd);
				recLog.setField("YD_USER_ID"		, "log");
				recLog.setField("MSG"				, szMsg);
				
				ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
				ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
				

							
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			}
		
			outRecord.setField("RTN_CD" , "1");	
			outRecord.setField("RTN_MSG", szMsg);	
			outRecord.setField("RTN_BED", sRTN_BED);	
			return outRecord;

		} catch(Exception e) {
			szMsg = "코일야드 보조작업To위치결정 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;
		}
		
		
	} // end of procCoilYdAidWkToPosDecision()
	
	/**
	 *      [A] 오퍼레이션명 : 코일소재야드 보조작업To위치검색(H/J)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return  BED
	 * @throws JDTOException
	 */
	public String searchCoilYdAidWkToPosition(String szStlNo, String szColGp, String szBedNo,String szLyrNo) throws JDTOException  {
	// bed 단위로 read 하여 처리함	
		
		YdStkLyrDao     ydStkLyrDao  = new YdStkLyrDao();
		JDTORecord outRecord1	= JDTORecordFactory.getInstance().create();
		int intRtnVal           = 0;
		
		String szMsg			= "";
		String szMethodName		= "searchCoilYdAidWkToPosition";
		String szOperationName	= "코일소재야드 보조작업To위치검색";
		
		JDTORecord recPara 		= null;
		JDTORecordSet rsBed     = null;
		JDTORecord recBed       = null;
		String szSchCoilStat    = null;  //스케줄코일상태
		String sRTN_BED = "";
		
		try {
			  
			szSchCoilStat = "H";
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szColGp);
			recPara.setField("YD_STK_BED_NO", szBedNo);
			recPara.setField("YD_STK_LYR_NO", szLyrNo);
			recPara.setField("STL_NO", szStlNo);
        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    /* com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrBED */

//        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBed, 1);
        	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyr*/
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBed, 0);
        			
        	if (intRtnVal < 0) {
				szMsg = "적치열(" + szColGp + ") 적치배드(" + szBedNo + ")로 위치검색 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return sRTN_BED = "";
			}
        	
        	if (intRtnVal == 0) {
        		return sRTN_BED = "";
        	}
        	
//        	rsSaveLoc = JDTORecordFactory.getInstance().createRecordSet("Temp");
        	
        	rsBed.first();
			for (int nLoop =0 ; nLoop<rsBed.size(); nLoop++ ){
				
				// 검색배드 정보를 Setting
				recBed = JDTORecordFactory.getInstance().create();
				recBed = rsBed.getRecord(nLoop);
	
				outRecord1 = this.CoilLyrBaseCheck(szStlNo, recBed);
				
				String sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				String sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				sRTN_BED			= StringHelper.evl(outRecord1.getFieldString("RTN_BED"), "");
				if ("-1".equals(sRTN_CD)) {
					continue;
				}	
				if ("0".equals(sRTN_CD)) {
					return sRTN_BED = "";
				}	

				return sRTN_BED;
			} //end of for	
			
			
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드(보조작업) TO위치결정 END★★★★★", YdConstant.INFO);
			return sRTN_BED = "";
		} catch(Exception e) {
			szMsg = "코일소재야드 보조작업To위치검색 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return sRTN_BED = "";
		}
	} // end of searchCoilYdAidWkToPosition
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일제품야드 보조작업To위치검색
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilGdsYdAidWkToPosition(String szStlNo, String szColGp, String szBedNo, String szLyrNo, String szEqpId) throws JDTOException  {
		
		YdStkLyrDao     ydStkLyrDao  = new YdStkLyrDao();
		
		int intRtnVal         = 0;
		JDTORecord outRecord1	= JDTORecordFactory.getInstance().create();
		String szMsg			=	"";
		String szMethodName		=	"searchCoilGdsYdAidWkToPosition";
		String szOperationName	=	"코일제품야드 보조작업To위치검색";
		
		JDTORecord recPara 		= null;
		JDTORecordSet rsBed     = null;
		JDTORecord recBed       = null;
		
		String szSchCoilStat    = null;  //스케줄코일상태
		
		String szYdStkColGp     = null;  // 적치열번호
		String szYdStkBedNo     = null;  // 적치배드번호
		String szYdStkLyrNo     = null;  // 적치단번호

		String szRtnVal = "";
		String sRTN_BED = "";
		
		try {
			
			szSchCoilStat = "G";
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szColGp);
			recPara.setField("YD_STK_BED_NO", szBedNo);
			recPara.setField("YD_STK_LYR_NO", szLyrNo);
			recPara.setField("STL_NO", szStlNo);
        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    /*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyr*/
 //       	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBed, 1);
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBed, 0);
			
        	if (intRtnVal < 0) {
				szMsg = "적치열(" + szColGp + ") 적치배드(" + szBedNo + ")로 위치검색 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return sRTN_BED = "";
			}
        	
        	if (intRtnVal == 0) {
        		return sRTN_BED = "";
        	}
        	
    	
        	rsBed.first();
			for (int nLoop =0 ; nLoop<rsBed.size(); nLoop++ ){
				
				// 검색배드 정보를 Setting
				recBed =JDTORecordFactory.getInstance().create();
				recBed = rsBed.getRecord(nLoop);
				
				outRecord1 = this.CoilGdsLyrBaseCheck(szStlNo, recBed);
				String sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				String sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				sRTN_BED			= StringHelper.evl(outRecord1.getFieldString("RTN_BED"), "");
				ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드(보조작업) sRTN_CD->" + sRTN_CD +"★★★★★C열연코일제품야드(보조작업) sRTN_BED->" + sRTN_BED, YdConstant.INFO);
				if ("-1".equals(sRTN_CD)) {
					continue; 
				}	
				if ("0".equals(sRTN_CD)) {
					return sRTN_BED = "";
				}	
	           	
				ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드(보조작업) szYdStkColGp->" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo, YdConstant.INFO);
				
        		return sRTN_BED;	        	
	        	
 				
			} //end of for	
			
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드(보조작업) TO위치결정 END★★★★★", YdConstant.INFO);
			return szRtnVal;
		} catch(Exception e) {
			szMsg = "코일창고야드 보조작업To위치검색 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
	} // end of searchCoilGdsYdAidWkToPosition
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일야드 To위치결정 주작업(H/J)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */

	public JDTORecord procCoilYdToPosDecision(String sYdCrnSchId, String strStlNo, String strSchCd, String strRouteGp, String szYD_CAR_USE_GP, String szTRN_EQP_CD, String szCAR_NO, String szCARD_NO,String sYD_TO_LOC_DCSN_MTD,String sYD_TO_LOC_GUIDE,String szEqpId) throws JDTOException  {
		
		//코일공통 DAO
		PtOsCommDao ptOsCommDao     = new PtOsCommDao();
		CoilGdsJspDao CoilGdsJspDao = new CoilGdsJspDao();
		YdEqpDao		ydEqpDao	= new YdEqpDao();
		//리턴값(int)
		int intRtnVal         = 0;
		
		String szMsg				= "";
		String szMethodName			= "procCoilYdToPosDecision";
		String szOperationName		= "코일야드 주작업  To위치결정";
		
		JDTORecordSet outRecSet 	= null;
		JDTORecordSet outRecSet1 	= null;
		JDTORecord recPara 			= null;
		JDTORecord recCoil      	= null;
		JDTORecord recCoil1      	= null;
		
		String szStlNo          	= null;
		String szSchCd          	= null;
		String szRouteGp        	= null;
		String szCurrProgCd     	= null;
		String szYdGp           	= null;
		String szYD_MTL_ITEM    	= null;
		String sUSAGE_CD			= null;
		String sORD_YEOJAE_GP 		= null;
		String sDELIVER_TERM_CD 	= null;
		String sWRAP_METHOD_CD      = "";
		boolean blnRtnVal = true;
		JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
		JDTORecord 		outRecord1 	= JDTORecordFactory.getInstance().create();
//		String  szRtnVal = YdConstant.RETN_CD_FAILURE;
		String sRTN_BED = "";
		String sRTN_MSG = "";
		String szRouteGpTemp        = null;
		
		try {
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일야드 TO위치결정 START★★★★★", YdConstant.INFO);
			
			szStlNo = strStlNo;
			szSchCd = strSchCd;
			szRouteGp = strRouteGp;
			
			if("".equals(szStlNo)) {
				szMsg = "재료번호가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;				
				
			} else {
				szMsg = "전달받은 재료번호는 [" + szStlNo + "] 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			if("".equals(szSchCd)) {
				szMsg = "스케줄코드가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;				
			} else {
				szMsg = "전달받은 스케줄코드는 [" + szSchCd + "] 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			if("".equals(szRouteGp)) {
				szMsg = "야드행선구분이 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;				
			} else {
				szMsg = "전달받은 야드행선구분은 [" + szRouteGp + "] 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			recPara	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szStlNo);
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtCoilCommByCoilNo*/
			intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet, 19);
			
			//리턴값 메세지처리
			if (intRtnVal == 0) {
				szMsg = "재료번호(" + szStlNo + ")에 대한 코일공통데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;				

			} else if (intRtnVal < 0) {
				szMsg = "재료번호(" + szStlNo + ")로 코일공통데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;				
			}
			
			outRecSet.absolute(1);
			recCoil = outRecSet.getRecord();
			
			szCurrProgCd 	= ydDaoUtils.paraRecChkNull(recCoil, "CURR_PROG_CD"); //진도코드
			szYdGp 			= ydDaoUtils.paraRecChkNull(recCoil, "YD_GP");  //야드구분
			szYD_MTL_ITEM 	= ydDaoUtils.paraRecChkNull(recCoil, "YD_MTL_ITEM");
			sORD_YEOJAE_GP 	= ydDaoUtils.paraRecChkNull(recCoil, "ORD_YEOJAE_GP");
			sDELIVER_TERM_CD= ydDaoUtils.paraRecChkNull(recCoil, "DELIVER_TERM_CD");

			sWRAP_METHOD_CD = ydDaoUtils.paraRecChkNull(recCoil, "WRAP_METHOD_CD");
			sUSAGE_CD= ydDaoUtils.paraRecChkNull(recCoil, "USAGE_CD");
			
			if ("".equals(szCurrProgCd) || "".equals(szYdGp)) {
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", "진도코드나 공장구분이 없습니다.");	
				return outRecord;				
			}
// 기준추가 제품은 행선으로 검색bed를 찾는것이 아니라 bre에 등록된 코드를 기준으로 검색처리 함			
			if ("J".equals(szYdGp)){
				
				if("F3".equals(szRouteGp)) {
					szRouteGp = "F0";
				} else {
		    		recPara = JDTORecordFactory.getInstance().create();
					
		    		if(sORD_YEOJAE_GP.equals("2")) {
			    		recPara.setField("V_ORD_YEOJAE_GP"		, sORD_YEOJAE_GP);
		    		} else {
			    		recPara.setField("V_ORD_YEOJAE_GP"		, sORD_YEOJAE_GP);
		    			recPara.setField("V_ITM_NM"				, sDELIVER_TERM_CD.substring(0, 1));
		    		}	
	    			outRecSet1 = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    	    /*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getYDB700ComboList */
	    			outRecSet1 = CoilGdsJspDao.getYDB700ComboList(recPara);
	    			if(outRecSet1.size() != 1) {
	    				outRecord.setField("RTN_CD" , "-1");	
	    				outRecord.setField("RTN_MSG", "제품검색조건(BRE) DATA 이상.");	
	    				return outRecord;				
	    				
	    			}
	    			outRecSet1.absolute(1);
	    			
	    			recCoil1 = outRecSet1.getRecord();
	    			szRouteGpTemp	= ydDaoUtils.paraRecChkNull(recCoil1, "CODE");
	    			
	    			if("".equals(szRouteGpTemp)){
	    				outRecord.setField("RTN_CD" , "-1");	
	    				outRecord.setField("RTN_MSG", "제품검색조건(BRE) DATA 이상.");	
	    				return outRecord;				
	    			} else {
	    				szRouteGp = szRouteGpTemp;
	    			}
				}
//SJH1215
//지포장 관련:입고시 포장타입이 EB 이고 용도코드가 F가 아닌 경우 포장장으로 				
				if(//( szSchCd.equals("JBFD01LM") )||                            
           		   ( szSchCd.equals("JBKD01LM") )|| 
           		   ( szSchCd.equals("JAKD01LM") )||
	               ( szSchCd.equals("JBTC01MM") )|| 
	               ( szSchCd.equals("JBTC02MM") )|| 
	               ( szSchCd.equals("JBTC05MM") )||
	               ( szSchCd.equals("JATC05MM") )|| 
	               ( szSchCd.equals("JCFD01LM") )|| 
	               ( szSchCd.equals("JCKD01LM") )|| 
	               ( szSchCd.equals("JCTC01MM") )|| 
	               ( szSchCd.equals("JCTC02MM") )|| 
	               ( szSchCd.equals("JCTC05MM") )|| 
	               ( szSchCd.equals("JDFD01LM") )|| 
	               ( szSchCd.equals("JDTC01MM") )|| 
	               ( szSchCd.equals("JDTC02MM") )|| 
	               ( szSchCd.equals("JEDD01LM") )|| 
	               ( szSchCd.equals("JETC01MM") )|| 
	               ( szSchCd.equals("JETC02MM") )|| 
	               ( szSchCd.equals("JFFD01LM") )|| 
	               ( szSchCd.equals("JFTC01MM") )|| 
	               ( szSchCd.equals("JFTC02MM") )|| 
	               ( szSchCd.equals("JGFD01LM") )|| 
	               ( szSchCd.equals("JGTC01MM") )|| 
            	   ( szSchCd.equals("JGTC02MM") )|| 
 	               ( szSchCd.equals("JHKD01LM") )|| 
 	               ( szSchCd.equals("JHTC01MM") )||
 	               ( szSchCd.equals("JHTC02MM") ) ){	
					if (sWRAP_METHOD_CD.equals("EB") && !sUSAGE_CD.equals("F")){
						ydUtils.putLog(szSessionName, szMethodName, "입고시 포장타입이 EB 임으로 포장장으로-->" , YdConstant.DEBUG);
						szRouteGp = "G0";
					}
				}else if( szSchCd.equals("JBGF01LM") || szSchCd.equals("JCGF01LM") || szSchCd.equals("JEGF01LM") ||szSchCd.equals("JFGF01LM") || szSchCd.equals("JHGF01LM") ){
//					151214 hun 지포장장 입고 스케줄 G0 으로 세팅
//					szRouteGp = "G0";
				}
	 	               
 			}
			
			if("H".equals(szYdGp)){
				if( szSchCd.equals("HBGF01UM") || szSchCd.equals("HCGF01UM") || szSchCd.equals("HEGF01UM") || szSchCd.equals("HFGF01UM") ||szSchCd.equals("HHGF01UM") ){
//					151214 hun 지포장장 입고 스케줄 G0 으로 세팅
					szRouteGp = "G0";
				}
				
				 // 코일소재야드 To위치검색 Method 호출
				ydUtils.putLog(szSessionName, szMethodName, "코일소재야드 To위치검색 Method 호출 :szYdGp=" + szYdGp + "/szCurrProgCd:="+szCurrProgCd, YdConstant.DEBUG);
				ydUtils.putLog(szSessionName, szMethodName, "코일소재야드 To위치검색 Method 호출 :YD_TO_LOC_DCSN_MTD=" + sYD_TO_LOC_DCSN_MTD + "/sYD_TO_LOC_GUIDE:="+sYD_TO_LOC_GUIDE, YdConstant.DEBUG);
				outRecord1 = (JDTORecord)searchCoilYdToPosition(sYdCrnSchId, szStlNo, szSchCd, szRouteGp, szYD_CAR_USE_GP, szTRN_EQP_CD, szCAR_NO, szCARD_NO,sYD_TO_LOC_DCSN_MTD,sYD_TO_LOC_GUIDE);
				sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				sRTN_BED	= StringHelper.evl(outRecord1.getFieldString("RTN_BED"), "");
				
			}else if("J".equals(szYdGp))	{
				// 코일창고야드 To위치검색 Method 호출
				ydUtils.putLog(szSessionName, szMethodName, "코일제품야드 To위치검색 Method 호출 :szYdGp=" + szYdGp + "/szCurrProgCd:="+szCurrProgCd, YdConstant.DEBUG);
				ydUtils.putLog(szSessionName, szMethodName, "코일제품야드 To위치검색 Method 호출 :YD_TO_LOC_DCSN_MTD=" + sYD_TO_LOC_DCSN_MTD + "/sYD_TO_LOC_GUIDE:="+sYD_TO_LOC_GUIDE, YdConstant.DEBUG);
				outRecord1 = (JDTORecord)searchCoilGdsYdToPosition(sYdCrnSchId, szStlNo, szSchCd, szRouteGp, szYD_CAR_USE_GP, szTRN_EQP_CD, szCAR_NO, szCARD_NO,sYD_TO_LOC_DCSN_MTD,sYD_TO_LOC_GUIDE,szEqpId);
				
				sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				sRTN_BED	= StringHelper.evl(outRecord1.getFieldString("RTN_BED"), "");
				
				if(ydEqpDao.chkAutoCrn(szEqpId)){
					if ("".equals(sRTN_BED)) {
						ydUtils.putLog(szSessionName, szMethodName, "코일제품야드 AutoCrn To위치검색 Second Method 호출", YdConstant.DEBUG);	 
						outRecord1 = (JDTORecord)searchCoilGdsYdToPositionAutoSecond(sYdCrnSchId, szStlNo, szSchCd, szRouteGp, szYD_CAR_USE_GP, szTRN_EQP_CD, szCAR_NO, szCARD_NO,sYD_TO_LOC_DCSN_MTD,sYD_TO_LOC_GUIDE,szEqpId);
						
						sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
						sRTN_BED	= StringHelper.evl(outRecord1.getFieldString("RTN_BED"), "");
						
						if ("".equals(sRTN_BED)) {
							ydUtils.putLog(szSessionName, szMethodName, "코일제품야드 AutoCrn To위치검색 Third Method 호출", YdConstant.DEBUG);	 
							outRecord1 = (JDTORecord)searchCoilGdsYdToPositionAutoThird(sYdCrnSchId, szStlNo, szSchCd, szRouteGp, szYD_CAR_USE_GP, szTRN_EQP_CD, szCAR_NO, szCARD_NO,sYD_TO_LOC_DCSN_MTD,sYD_TO_LOC_GUIDE,szEqpId);
							
							sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
							sRTN_BED	= StringHelper.evl(outRecord1.getFieldString("RTN_BED"), "");
						
						}
					}
				}
				
			} else {
								
//LOG_TABLE    					
				EJBConnector ejbConn = null;
				JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
				recLog.setField("STL_NO"			, szStlNo);
				recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
				recLog.setField("YD_GP"				, szYdGp);
				recLog.setField("YD_SCH_CD"			, szSchCd);
				recLog.setField("YD_USER_ID"		, "log");
				recLog.setField("MSG"				, sRTN_MSG);
				
				ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
				ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });

				szMsg = "재료번호(" + szStlNo + ")가 코일야드, 열연창고 해당하지 않는 진도코드 입니다.진도:"+szCurrProgCd+" 야드구분:"+szYdGp;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;				
			}
			if ("".equals(sRTN_BED)) {
 
				EJBConnector ejbConn = null;
				JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
				recLog.setField("STL_NO"			, szStlNo);
				recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
				recLog.setField("YD_GP"				, szYdGp);
				recLog.setField("YD_SCH_CD"			, szSchCd);
				recLog.setField("YD_USER_ID"		, "log");
				recLog.setField("MSG"				, sRTN_MSG);
//LOG_TABLE				
				ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
				ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
				
				szMsg = " 코일야드 To위치검색 중 ERROR 발생";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" , "-1");	
				outRecord.setField("RTN_MSG", sRTN_MSG);	
				return outRecord;
			
			}			
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일야드 TO위치결정END ("+ sRTN_BED +")★★★★★", YdConstant.INFO);
			outRecord.setField("RTN_BED" , sRTN_BED);	
			outRecord.setField("RTN_CD" , "1");	
			return outRecord;	
			
		} catch(Exception e) {
			szMsg = "코일야드 To위치결정 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일야드 TO위치결정 ERROR★★★★★", YdConstant.INFO);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;				
		}
		
		
	} // end of procCoilYdToPosDecision()
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일소재야드 To위치검색(주작업)(H/J)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return sRTN_BED
	 * @throws JDTOException
	 */
	public JDTORecord searchCoilYdToPosition(String sYdCrnSchId, String szStlNo, String szSchCd, String szRouteGp, String szYD_CAR_USE_GP, String szTRN_EQP_CD, String szCAR_NO, String szCARD_NO,String sYD_TO_LOC_DCSN_MTD,String sYD_TO_LOC_GUIDE) throws JDTOException  {
		YdLocSrchRngDao ydLocSrchRngDao = new YdLocSrchRngDao();
		YdStkLyrDao    ydStkLyrDao = new YdStkLyrDao();
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilYdToPosition";
		String szOperationName	=	"코일소재야드 To위치검색";
		
		JDTORecord recPara 		= null;
		JDTORecord recPara1		= null;
		JDTORecordSet rsBed     = null;
		JDTORecordSet rsResult  = null;
		JDTORecord recBed       = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1	= JDTORecordFactory.getInstance().create();
		
		JDTORecordSet rsSaveLoc = null;
		JDTORecord recSaveLoc 	= null;
		JDTORecord recSaveLoc1 	= null;		
		String szSchCoilStat    = null;  //스케줄코일상태
		String szLeftGrade      = null;  //좌측평점
		String szRightGrade     = null;  //좌측평점
		String szMsgList        = "";
		
		String szToPosGrade = "";
		
		String szRtnVal = "";
		String szGrdColGp = "";
		String szGrdBedNo = "";
		String szGrdLyrNo = "";
		String szToPosGrd = "999";
		String sRTN_BED = "";
		String sMsg = "";
		
		String szYdStkColGp = "";
		String szYdStkBedNo = "";
		String szYdStkLyrNo = "";
		String szLeftColGp 	= "";
		String szLeftBedNo 	= "";
		String szLeftLyrNo 	= "";
		String szLeftStlNo 	= "";
		String szRightColGp = "";
		String szRightBedNo = "";
		String szRightLyrNo = "";
		String szRightStlNo = "";
		
		String szGrdColGp1 = "";
		String szGrdBedNo1 = "";
		String szGrdLyrNo1 = "";
		String szToPosGrd1 = "";
		String szLToPosGrd = "";
		String szRToPosGrd = "";
		String szRTN_MSG   = "";
		
		JDTORecord outRecord	= JDTORecordFactory.getInstance().create();		
		
		try {
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 TO위치결정 START★★★★★", YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, szStlNo+"검색SQL", YdConstant.INFO);
			szSchCoilStat = "H";

				 
			if((sYD_TO_LOC_DCSN_MTD.equals("F")) && (szSchCd.substring(2, 4).equals("TC")) && (szSchCd.substring(6, 7).equals("U"))){
	    		recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_SCH_CD"	, szSchCd);
    			recPara.setField("YD_ROUTE_GP"	, szRouteGp);
    			recPara.setField("STL_NO"		, szStlNo);
	        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	    /*com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserGdsByToLoc8*/
	        	intRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, rsBed, 501);

			} else if ((sYD_TO_LOC_DCSN_MTD.equals("F")) && (szSchCd.substring(2, 4).equals("HC"))  && (sYD_TO_LOC_GUIDE.trim().length() == 4)) {
				//사용자 지정 BED 검색:4 (결로재 보급)
	    		recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", szStlNo);
	        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	    /*com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserByToLoc4*/
	        	intRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, rsBed, 603);

			} else if ((sYD_TO_LOC_DCSN_MTD.equals("F")) && (sYD_TO_LOC_GUIDE.trim().length() == 6)) {
				//사용자 지정 BED 검색:6
	    		recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", szStlNo);
	        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	    /*com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserByToLoc6*/
	        	intRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, rsBed, 400);

			} else if ((sYD_TO_LOC_DCSN_MTD.equals("F")) && (sYD_TO_LOC_GUIDE.trim().length() == 8)){

				//결로방지 보급
				if("HC01LM".equals(szSchCd.substring(2,8))){
					recBed.setField("YD_STK_COL_GP", "J"+sYD_TO_LOC_GUIDE.substring(0,5));
				}else{
					recBed.setField("YD_STK_COL_GP", "H"+sYD_TO_LOC_GUIDE.substring(0,5));
				}
				
				recBed.setField("YD_STK_BED_NO", sYD_TO_LOC_GUIDE.substring(6,8));
				recBed.setField("YD_STK_LYR_NO", "00"+ sYD_TO_LOC_GUIDE.substring(5,6));
//C증설				
				if((szSchCd.equals("HBKE01UM")) 
				 ||(szSchCd.equals("HAKE01UM"))
				 ||(szSchCd.equals("HBFE01UM"))
				 ||(szSchCd.equals("HBFE03UM"))
				 ||(szSchCd.equals("HBKE03UM")) 
				 ||(szSchCd.equals("HAKE03UM"))
				 ||(szSchCd.equals("HBKD01UM"))
				 ||(szSchCd.equals("HAKD01UM"))
				 ||(szSchCd.equals("HCFE01UM"))
				 ||(szSchCd.equals("HCKE01UM")) 
				 ||(szSchCd.equals("HCFE03UM"))
				 ||(szSchCd.equals("HCKE03UM")) 
				 ||(szSchCd.equals("HCKD01UM"))
				 ||(szSchCd.equals("HEDE01UM"))
				 ||(szSchCd.equals("HEDE03UM"))
				 ||(szSchCd.equals("HEDD01UM")) 
				 ||(szSchCd.equals("HGFE01UM"))
				 ||(szSchCd.equals("HGFE03UM"))
				 ||(szSchCd.equals("HHKE01UM")) 
				 ||(szSchCd.equals("HHKE03UM")) 
				 ||(szSchCd.equals("HHKD01UM"))
				 
				){   

					outRecord.setField("RTN_BED" 	, "H"+sYD_TO_LOC_GUIDE.substring(0,5) + "00" + "001");	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
										
				} else {
					
					rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					intRtnVal = ydStkLyrDao.getYdStklyr(recBed, rsResult, 0);
					if (intRtnVal < 1) {
						outRecord.setField("RTN_BED" 	, "");	
						outRecord.setField("RTN_CD" 	, "1");	
						return outRecord;
					}


					//적치단정보 레코드 추출
					rsResult.first();
					recPara1 = JDTORecordFactory.getInstance().create();
					recPara1 = rsResult.getRecord();
					
					String szYD_STK_LYR_ACT_STAT = ydDaoUtils.paraRecChkNull(recPara1, "YD_STK_LYR_ACT_STAT");

					ydUtils.putLog(szSessionName, szMethodName, "szYD_STK_LYR_ACT_STAT" +szYD_STK_LYR_ACT_STAT , YdConstant.DEBUG);
					
					//적치단 재료상태가 적치가능('E')가 아니면 에러 리턴
					if(!(szYD_STK_LYR_ACT_STAT.equals("E")) && !(szYD_STK_LYR_ACT_STAT.equals("S"))) {
						
						szMsg = "적치단 재료상태(" + szYD_STK_LYR_ACT_STAT + ") 적치가능 상태가 아닙니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
						outRecord.setField("RTN_BED" 	, "");	
						outRecord.setField("RTN_CD" 	, "1");	
						return outRecord;
					}
					
					//결로방지재 적치장 처리 
					if(szYD_STK_LYR_ACT_STAT.equals("S")){
						outRecord.setField("RTN_BED" 	, "J"+ sYD_TO_LOC_GUIDE.substring(0,5) + sYD_TO_LOC_GUIDE.substring(6,8) + "00"+ sYD_TO_LOC_GUIDE.substring(5,6));	
						outRecord.setField("RTN_CD" 	, "1");	
						return outRecord;
					}else{
						outRecord1 = this.CoilLyrBaseCheck(szStlNo, recBed);
					}
									
		        	
					String sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
					szYdStkColGp = StringHelper.evl(outRecord1.getFieldString("RTN_COLGP"), "");
					szYdStkBedNo = StringHelper.evl(outRecord1.getFieldString("RTN_BEDNO"), "");
					szYdStkLyrNo = StringHelper.evl(outRecord1.getFieldString("RTN_LYRNO"), "");
					String sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");					
					if ("1".equals(sRTN_CD)) {
						outRecord.setField("RTN_BED" 	, szYdStkColGp + szYdStkBedNo + szYdStkLyrNo);	
						outRecord.setField("RTN_CD" 	, "1");	
						return outRecord;

					} else {
//LOG_TABLE    					
		    			EJBConnector ejbConn = null;
		    			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
		    			recLog.setField("STL_NO"			, szStlNo);
		    			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
		    			recLog.setField("YD_GP"				, "H");
		    			recLog.setField("YD_SCH_CD"			, szSchCd);
		    			recLog.setField("YD_USER_ID"		, "log");
		    			recLog.setField("MSG"				, sRTN_MSG);
		    			
		    			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
		    			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });

						outRecord.setField("RTN_BED" 	, "");	
						outRecord.setField("RTN_CD" 	, "1");	
						return outRecord;

					}	
				}	
			} else {	
				
            	
            	//F동 수입 인경우 결로적치대 존재 여부 체크////////////////////////////////////////////////
            	if("HFCV01LM".equals(szSchCd) || "HECV01LM".equals(szSchCd)){
            		
            		recPara = JDTORecordFactory.getInstance().create();
            		recPara.setField("STL_NO", szStlNo);
            		recPara.setField("YD_BAY_GP", szSchCd.substring(1 , 2));
                    rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
                    /*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrHotCoilInChk*/
                    intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 629);
                    
                    if(intRtnVal > 0) {
                    	 rsResult.absolute(1);
                    	 JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
                         recOutTemp.setRecord(rsResult.getRecord());
                         
     	                
                         String szYdStkColGpHot = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_COL_GP");
         				 String szYdStkBedNoHot = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_NO");
         				 String szYdStkLyrNoHot = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_LYR_NO");
         				
         				outRecord.setField("RTN_BED" 	, szYdStkColGpHot + szYdStkBedNoHot +szYdStkLyrNoHot);	
						outRecord.setField("RTN_CD" 	, "1");	
						return outRecord;
                    }
            	}
            	///////////////////////////////////////////////////////////////////////////////////
            	
            	
                    
                  //위치검색베드로 to위치를 결정하는 경우
            		recPara = JDTORecordFactory.getInstance().create();
        			recPara.setField("YD_SCH_CD", szSchCd);
        			recPara.setField("YD_ROUTE_GP", szRouteGp);
        			recPara.setField("STL_NO", szStlNo);
                	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
            	
            	// 일반적이 검색 BED임  
            	/*com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngBySchCdandRouteGpCoilStlNo*/
            	intRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, rsBed, 303);

           	}
			
        	if (intRtnVal < 0) {
				szMsg = "재료번호(" + szStlNo + ")로 위치검색배드 없습니다. 위치검색베드를 추가하세요:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_BED" 	, "");	
				outRecord.setField("RTN_CD" 	, "1");	 //RTN_MSG
				outRecord.setField("RTN_MSG" 	, "위치검색순서조건이 이상합니다.<br>" + sMsg );	 //RTN_MSG
				return outRecord;
			}
        	
        	if (intRtnVal == 0) {
        		sRTN_BED = "";
				outRecord.setField("RTN_BED" 	, "");	
				outRecord.setField("RTN_CD" 	, "1");	 //RTN_MSG
				outRecord.setField("RTN_MSG" 	, "위치검색순서조건이 이상합니다.<br>" + sMsg );	 //RTN_MSG
				return outRecord;
        	}
        	
        	rsSaveLoc = JDTORecordFactory.getInstance().createRecordSet("Temp");
        	
        	rsBed.first();
        	
        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 적치대상재 검색 START★★★★★", YdConstant.INFO);
        	
			for (int nLoop =0 ; nLoop<rsBed.size(); nLoop++ ){
				
				// 검색배드 정보를 Setting
				recBed =JDTORecordFactory.getInstance().create();
				recBed = rsBed.getRecord(nLoop);

				String szYdStkColGpCHK = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_COL_GP");
				String szYdStkBedNoCHK = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_BED_NO");
				String szYdStkLyrNoCHK = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_LYR_NO");
				
				
				//결로방지재 적치장 처리 
				if(szSchCd.substring(2, 4).equals("HC") && szYdStkColGpCHK.substring(0, 1).equals("J")){
					outRecord.setField("RTN_BED" 	, szYdStkColGpCHK + szYdStkBedNoCHK + szYdStkLyrNoCHK);	
					outRecord.setField("RTN_CD" 	, "1");	
					
					ydUtils.putLog(szSessionName, szMethodName, "######>>>>결로재HOT코일 보급위치:"+ szYdStkColGpCHK + szYdStkBedNoCHK + szYdStkLyrNoCHK +" 결정완료", YdConstant.INFO);
					
					return outRecord;
				}
				
				ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 검색>>"+nLoop+"★★★★★"+szYdStkColGpCHK+szYdStkBedNoCHK+szYdStkLyrNoCHK+">>"+szStlNo, YdConstant.INFO);
				
				outRecord1 = this.CoilLyrBaseCheck(szStlNo, recBed);
				String sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				String sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				if ("-1".equals(sRTN_CD)) {
					
					recSaveLoc = JDTORecordFactory.getInstance().create();
		        	recSaveLoc.setField("YD_STK_COL_GP", szYdStkColGpCHK);
		        	recSaveLoc.setField("YD_STK_BED_NO", szYdStkBedNoCHK);
		        	recSaveLoc.setField("YD_STK_LYR_NO", szYdStkLyrNoCHK);
		        	recSaveLoc.setField("TO_POS_GRADE" , "999");
		        	recSaveLoc.setField("RTN_MSG" , sRTN_MSG);
 
		        	ydUtils.displayRecord(szOperationName, recSaveLoc);
		        	
		        	rsSaveLoc.addRecord(recSaveLoc);
					continue;
				}	
				if ("0".equals(sRTN_CD)) {
					outRecord.setField("RTN_BED" 	, "");	
					outRecord.setField("RTN_CD" 	, "1");	 //RTN_MSG
					return outRecord;
					
				}	
				szYdStkColGp = StringHelper.evl(outRecord1.getFieldString("RTN_COLGP"), "");
				szYdStkBedNo = StringHelper.evl(outRecord1.getFieldString("RTN_BEDNO"), "");
				szYdStkLyrNo = StringHelper.evl(outRecord1.getFieldString("RTN_LYRNO"), "");
				szLeftColGp 	= StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_COLGP"), "");
				szLeftBedNo 	= StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_BEDNO"), "");
				szLeftLyrNo 	= StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_LYRNO"), "");
				szLeftStlNo 	= StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_STLNO"), "");
				szRightColGp = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_COLGP"), "");
				szRightBedNo = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_BEDNO"), "");
				szRightLyrNo = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_LYRNO"), "");
				szRightStlNo = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_STLNO"), "");

//				//대차는 평점 check 하지 않는다
//				설비는 평점 check 하지 않는다
				if((szYdStkColGp.substring(2, 3).equals("0"))||
					(szYdStkColGp.substring(2, 3).equals("1"))||
					(szYdStkColGp.substring(2, 3).equals("2"))||
					(szYdStkColGp.substring(2, 3).equals("3"))||
					(szYdStkColGp.substring(2, 3).equals("4"))||
					(szYdStkColGp.substring(2, 3).equals("5")) ) {   //확장야드 변경시 작업대상
	        	} else {	
	        		szRtnVal = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
	        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 TO위치=>>>>"+szRtnVal, YdConstant.INFO);
					outRecord.setField("RTN_BED" 	, szRtnVal);	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
	        	}				
					
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 1단 좌측 코일 To위치평점항목  체크Set  START★★★★★", YdConstant.INFO);
	        	// 1단 좌측 코일 To위치평점항목 Set.
	        	szLeftGrade = SearchCoilYdLeftCoilGrade(szYdStkColGp, szYdStkBedNo, szYdStkLyrNo, szStlNo, szLeftColGp, szLeftBedNo, szLeftLyrNo, szLeftStlNo);
	        	
	        	if (szLeftGrade.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"좌측적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 재료번호[" + szLeftStlNo + "]의 평점항목Set에 오류가 있습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recSaveLoc = JDTORecordFactory.getInstance().create();
		        	recSaveLoc.setField("YD_STK_COL_GP", szYdStkColGpCHK);
		        	recSaveLoc.setField("YD_STK_BED_NO", szYdStkBedNoCHK);
		        	recSaveLoc.setField("YD_STK_LYR_NO", szYdStkLyrNoCHK);
		        	recSaveLoc.setField("TO_POS_GRADE" , "999");
		        	recSaveLoc.setField("RTN_MSG" , szMsg);
 
		        	ydUtils.displayRecord(szOperationName, recSaveLoc);
		        	
		        	rsSaveLoc.addRecord(recSaveLoc);
		        	
					continue;
        		} else {
        			szMsg = "확인:"+szStlNo+"좌측적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 재료번호[" + szLeftStlNo + "]의 평점항목Set[" + szLeftGrade + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		}
	        	
	        	// 1단 우측 코일 To위치평점항목 Set.
	        	szRightGrade = SearchCoilYdRightCoilGrade(szYdStkColGp, szYdStkBedNo, szYdStkLyrNo, szStlNo, szRightColGp, szRightBedNo, szRightLyrNo, szRightStlNo);
	        	
	        	if (szRightGrade.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"우측적치단(" + szRightColGp + szRightBedNo + szRightLyrNo + ")의 재료번호[" + szRightStlNo + "]의 평점항목Set에 오류가 있습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
					recSaveLoc = JDTORecordFactory.getInstance().create();
		        	recSaveLoc.setField("YD_STK_COL_GP", szYdStkColGpCHK);
		        	recSaveLoc.setField("YD_STK_BED_NO", szYdStkBedNoCHK);
		        	recSaveLoc.setField("YD_STK_LYR_NO", szYdStkLyrNoCHK);
		        	recSaveLoc.setField("TO_POS_GRADE" , "999");
		        	recSaveLoc.setField("RTN_MSG" , szMsg);
 
		        	ydUtils.displayRecord(szOperationName, recSaveLoc);
		        	
		        	rsSaveLoc.addRecord(recSaveLoc);
		        	
					continue;
        		} else {
        			szMsg = "확인:"+szStlNo+"우측적치단(" + szRightColGp + szRightBedNo + szRightLyrNo + ")의 재료번호[" + szRightStlNo + "]의 평점항목Set[" + szRightGrade + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		}
	        	
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 1단 좌측 코일 To위치평점항목 체크Set  END★★★★★", YdConstant.INFO);
	        	
	        	
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★", YdConstant.INFO);
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 To위치 평점 계산  START★★★★★", YdConstant.INFO);
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★", YdConstant.INFO);
	        	// To위치 평점항목 Set
	        	szToPosGrade = getToPosGrade(szSchCoilStat, szYdStkLyrNo, szLeftGrade, szRightGrade);
	        	
	        	
	    		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일야드 BRE: 스케줄코일상태("+szSchCoilStat+") TO위치적치단("+szYdStkLyrNo+") 좌측1단코일상태("+szLeftGrade+") 우측1단코일상태("+szRightGrade+")", YdConstant.INFO);	    		
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 TO위치 평점결과:loop순번:"+(nLoop+1)+" 재료번호(" + szStlNo + ")저장위치("+szYdStkColGp + szYdStkBedNo + szYdStkLyrNo+")▶▶▶▶"+szToPosGrade+"점◀◀◀◀", YdConstant.INFO);
	        	
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★", YdConstant.INFO);
	        	
	        	if ("1".equals(szToPosGrade)) {
	        		sRTN_BED = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
	        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 TO위치=>>>>"+szRtnVal, YdConstant.INFO);
//LOG_TABLE    					
	    			EJBConnector ejbConn = null;
	    			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
	    			recLog.setField("STL_NO"			, szStlNo);
	    			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
	    			recLog.setField("YD_GP"				, "H");
	    			recLog.setField("YD_SCH_CD"			, szSchCd);
	    			recLog.setField("YD_USER_ID"		, "log");
	    			recLog.setField("MSG"				, "★★★★★C열연코일소재야드 TO위치=>>>>"+szRtnVal + ":평점 1점");
	    			
	    			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
	    			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });

	        		outRecord.setField("RTN_BED" 	, sRTN_BED);	
					outRecord.setField("RTN_CD" 	, "1");	 //RTN_MSG
					return outRecord;
	        	}
	        	
	        	/*
	        	 * TO위치 대상건이 50건이 넘는 경우 
	        	 * 평점대상 기준을 30점 이하로 조정 한다.
	        	 */
	        	int toPosGrade = Integer.parseInt(szToPosGrade);
	        	
	        	if (nLoop>=50 && toPosGrade <= 30) {
	        		szRtnVal = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
	        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 TO위치2=>>>>"+szRtnVal, YdConstant.INFO);

//LOG_TABLE    					
	    			EJBConnector ejbConn = null;
	    			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
	    			recLog.setField("STL_NO"			, szStlNo);
	    			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
	    			recLog.setField("YD_GP"				, "H");
	    			recLog.setField("YD_SCH_CD"			, szSchCd);
	    			recLog.setField("YD_USER_ID"		, "log");
	    			recLog.setField("MSG"				, "★★★★★C열연코일소재야드 TO위치2=>>>>"+szRtnVal + ":50회 이상 평점 30점 이하:" +szToPosGrade+"점에서 결정됨");
	    			
	    			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
	    			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
	        		
	        		
	        		outRecord.setField("RTN_BED" 	, szRtnVal);	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord; 
	        	}
	        	
	        	recSaveLoc = JDTORecordFactory.getInstance().create();
	        	recSaveLoc.setField("YD_STK_COL_GP", szYdStkColGp);
	        	recSaveLoc.setField("YD_STK_BED_NO", szYdStkBedNo);
	        	recSaveLoc.setField("YD_STK_LYR_NO", szYdStkLyrNo);
	        	recSaveLoc.setField("TO_POS_GRADE" , szToPosGrade);
	        	recSaveLoc.setField("TO_LEFT_GRADE" , szLeftGrade);
	        	recSaveLoc.setField("TO_RIGTH_GRADE" , szRightGrade);
	        	
	        	ydUtils.displayRecord(szOperationName, recSaveLoc);
	        	
	        	rsSaveLoc.addRecord(recSaveLoc);
				
				
			} //end of for	
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 적치대상제 검색 END★★★★★", YdConstant.INFO);
			
			
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 평점 1점이 아닌경우 대상 찾기 START★★★★★", YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "C열연코일소재야드 "+ szStlNo + "평점리스트", YdConstant.INFO);
			
			if (rsSaveLoc.size() > 0) {
				for(int nLoop =0 ; nLoop<rsSaveLoc.size(); nLoop++ ) {
					rsSaveLoc.absolute(nLoop);
					recSaveLoc1 = JDTORecordFactory.getInstance().create();
					recSaveLoc1 = rsSaveLoc.getRecord(nLoop);
					
					szGrdColGp1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "YD_STK_COL_GP");
					szGrdBedNo1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "YD_STK_BED_NO");
					szGrdLyrNo1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "YD_STK_LYR_NO");
					szToPosGrd1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "TO_POS_GRADE");
					szLToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc1, "TO_LEFT_GRADE");
					szRToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc1, "TO_RIGTH_GRADE");
					szRTN_MSG   =  ydDaoUtils.paraRecChkNull(recSaveLoc1, "RTN_MSG");
					
					szMsg ="[" + szStlNo + "]"+"["+ nLoop+"]" + szGrdColGp1 + szGrdBedNo1 + szGrdLyrNo1 +"<<Score="+szToPosGrd1+"(" + "L:"+szLToPosGrd+ "|R:"+szRToPosGrd+")>>,"+szRTN_MSG+"\r\n";
					szMsgList = szMsgList + szMsg;
					
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				} // end of for
			}				

//LOG_TABLE    					
			EJBConnector ejbConn = null;
			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
			recLog.setField("STL_NO"			, szStlNo);
			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
			recLog.setField("YD_GP"				, "H");
			recLog.setField("YD_SCH_CD"			, szSchCd);
			recLog.setField("YD_USER_ID"		, "log");
			recLog.setField("MSG"				, szMsgList);
			
			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
			
			if (rsSaveLoc.size() > 0) {
				for(int nLoop =0 ; nLoop<rsSaveLoc.size(); nLoop++ ) {
					recSaveLoc = JDTORecordFactory.getInstance().create();
					recSaveLoc = rsSaveLoc.getRecord(nLoop);
					
					 
					if(nLoop ==0){							 
						szToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc, "TO_POS_GRADE");
						
						if(!szToPosGrd.equals("999")){
							szGrdColGp = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_COL_GP");
							szGrdBedNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_BED_NO");
							szGrdLyrNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_LYR_NO");
						} 
					}
		 
					
//					szMsg ="["+ nLoop+"] 재료번호(" + szStlNo + ") C열연코일소재야드 TO위치=>>>>(" + szGrdColGp + szGrdBedNo + szGrdLyrNo +")점수:"+szToPosGrd;
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
						if (Integer.parseInt(szToPosGrd) > Integer.parseInt(ydDaoUtils.paraRecChkNull(recSaveLoc, "TO_POS_GRADE"))) {
							szGrdColGp = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_COL_GP");
							szGrdBedNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_BED_NO");
							szGrdLyrNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_LYR_NO");
							szToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc, "TO_POS_GRADE");
						}
				 
					
					
				} // end of for
				
				sRTN_BED = szGrdColGp + szGrdBedNo + szGrdLyrNo;
				
				ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 TO위치=>>>>"+szRtnVal, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 평점 1점이 아닌경우 대상 찾기 END★★★★★", YdConstant.INFO);
			} else {
				sRTN_BED = "";
				
			}
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 TO위치결정 END★★★★★", YdConstant.INFO);
			outRecord.setField("RTN_BED" 	, sRTN_BED);	
			outRecord.setField("RTN_CD" 	, "1");	 //RTN_MSG
			return outRecord;

		} catch(Exception e) {
			szMsg = "코일소재야드 To위치검색 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 TO위치결정 ERROR★★★★★", YdConstant.INFO);

			outRecord.setField("RTN_BED" 	, "");	
			outRecord.setField("RTN_CD" 	, "1");	 //RTN_MSG
			return outRecord;
		}
	} // end of searchCoilYdToPosition
	
	
	
	/**
	 * To위치평점BreRule
	 * @return String
	 */
	public String getToPosGrade(String szSchCoilStat, String szYdStkLyr, String szLeftGrade, String szRightGrade) {
		String szOperationName		= "To위치평점BreRule";
		String szMethodName			= "getToPosGrade";
		String szMsg				= null;
		boolean bBreRule			= false;
    	JDTORecord jdtoRcd			= JDTORecordFactory.getInstance().create();
    	
    	String szTO_POS_GRD			= null;					//To위치평점
    	
		String szRule 			= null;
		
		szMsg="["+szOperationName+"] 스케줄코일상태["+szSchCoilStat+"] 적치단[" + szYdStkLyr + "]에 대한 To위치편점 BRE Rule로부터 조회 시작";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
//		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일야드 BRE 스케줄코일상태==>>>"+szSchCoilStat, YdConstant.INFO);
//		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일야드 BRE 적치단==>>>"+szYdStkLyr, YdConstant.INFO);
//		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일야드 BRE 좌측1단코일상태==>>>"+szLeftGrade, YdConstant.INFO);
//		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일야드 BRE 우측1단코일상태==>>>"+szRightGrade, YdConstant.INFO);
		
		bBreRule = GetBreRule0.getYDB010(szSchCoilStat, szYdStkLyr, szLeftGrade, szRightGrade, jdtoRcd);
				
		szMsg="["+szOperationName+"] 스케줄코일상태["+szSchCoilStat+"] 적치단[" + szYdStkLyr + "]에 대한 To위치편점 BRE Rule로부터 조회 완료 - 반환값 : " + bBreRule;
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		if( bBreRule ) {
			try {
    			
    			ydUtils.displayRecord(szOperationName, jdtoRcd);
    			
    			szTO_POS_GRD		= ydDaoUtils.paraRecChkNull(jdtoRcd, "TO_POS_GRD");
    			
    			if(szTO_POS_GRD.equals("") || szTO_POS_GRD.equals(null)){
    				szTO_POS_GRD 	= "99";
    			}
    			
    		}catch(JDTOException ex) {
    			szMsg="["+szOperationName+"] 스케줄코일상태["+szSchCoilStat+"] 적치단[" + szYdStkLyr + "]에 대한 To위치편점 BRE Rule로부터 조회 시 오류발생 - 기본값으로 설정[99]";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			
    			szTO_POS_GRD		= "99";
    		}
    		
		}else{
			szTO_POS_GRD			= "99";

		}
		
		szRule	= szTO_POS_GRD;
		
		return szRule;
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일소재야드여재원인Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilYdYeojaeCheck(String szStlNo,String szYEOJAE_CAUSE_CD, String szLeftStlNo,String szLeftYEOJAE_CAUSE_CD, String szRightStlNo,String szRightYEOJAE_CAUSE_CD) throws JDTOException  {
		
		YdStockDao     ydStockDao  = new YdStockDao();
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilYdYeojaeCheck";
		String szOperationName	=	"코일소재야드여재원인Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		
		
		
		try {
			
        	
//SJH1229        	if ("3G".equals(szYEOJAE_CAUSE_CD) || "3G".equals(szLeftYEOJAE_CAUSE_CD) || "3G".equals(szRightYEOJAE_CAUSE_CD)) {
        	if ( "3G".equals(szLeftYEOJAE_CAUSE_CD) || "3G".equals(szRightYEOJAE_CAUSE_CD)) {
               	szRtnVal = YdConstant.RETN_CD_FAILURE;
        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 짱구코일 여재구분실패(본,LEFT,RIGHT)=>>"+szYEOJAE_CAUSE_CD+"-"+szLeftYEOJAE_CAUSE_CD+"-"+szRightYEOJAE_CAUSE_CD, YdConstant.INFO);
        	} else {
        		szRtnVal = YdConstant.RETN_CD_SUCCESS;
        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 짱구코일 여재구분성공(본,LEFT,RIGHT)=>>"+szYEOJAE_CAUSE_CD+"-"+szLeftYEOJAE_CAUSE_CD+"-"+szRightYEOJAE_CAUSE_CD, YdConstant.INFO);
        	}

        	return szRtnVal;

		} catch(Exception e) {
			szMsg = "코일소재야드여재원인Check 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} // end of searchCoilYdYeojaeCheck
	
	/**
	 *      [A] 오퍼레이션명 : 코일소재야드중량Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilYdWtCheck(String szStlNo, String szThick, String szWeigth, String szLeftStlNo, String szLeftThick, String szLeftWeigth,String szRightStlNo,String szRightThick, String szRightWeigth, long LngFR_VAL1, long LngFR_VAL2) throws JDTOException  {
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilYdWtCheck";
		String szOperationName	=	"코일소재야드중량Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		
		double dblThick  		= 0;  // 좌측코일두께
		double dblLeftThick  	= 0;  // 우측코일두께
		double dblRightThick  	= 0;
		double dblMinThick  	= 0;

		long lngWeigth  		= 0;  // 좌측코일중량
		long lngLeftWeigth  	= 0;  // 좌측코일중량
		long lngRightWeigth  	= 0;  // 좌측코일중량
		long lngMinWeigth  	= 0;  // 좌측코일중량
		
		
		
		try {
			
			if("".equals(szLeftStlNo) || "".equals(szRightStlNo)) {
				return szRtnVal;
			}
			
			dblThick 		= Double.parseDouble(szThick);
			lngWeigth 		= Long.parseLong(szWeigth);
			
			dblLeftThick 	= Double.parseDouble(szLeftThick);
			lngLeftWeigth 	= Long.parseLong(szLeftWeigth);
			
			dblRightThick 	= Double.parseDouble(szRightThick);
			lngRightWeigth	= Long.parseLong(szRightWeigth);
		
			if(dblLeftThick >= dblRightThick){
				dblMinThick = dblRightThick;
			} else {
				dblMinThick = dblLeftThick;
			}

			if(lngLeftWeigth >= lngRightWeigth){
				lngMinWeigth = lngRightWeigth;
			} else {
				lngMinWeigth = lngLeftWeigth;
			}
 			
        	
        	szMsg = "1단좌측재료번호(" + szLeftStlNo + ")=>>좌측코일중량,좌측코일두께:"+lngLeftWeigth+","+dblLeftThick ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단우측재료번호(" + szRightStlNo + ")=>>우측코일중량,우측코일두께:"+lngRightWeigth+","+dblRightThick ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
        	szMsg = "1단우측재료번호(" + szRightStlNo + ")=>>소 중량,소두께:"+lngMinWeigth+","+dblMinThick;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
        	//if (스케줄코일-중량 - 1단중량) < 중량편차상수
			if (dblMinThick < 2.5){
				if ((lngMinWeigth+ LngFR_VAL1) >= lngWeigth){
					szRtnVal = YdConstant.RETN_CD_SUCCESS;
				} else {
					szRtnVal = YdConstant.RETN_CD_FAILURE;
				}	
        	} else {
				if ((lngMinWeigth+ LngFR_VAL2) >= lngWeigth){
					szRtnVal = YdConstant.RETN_CD_SUCCESS;
				} else {
					szRtnVal = YdConstant.RETN_CD_FAILURE;
				}	
        	}
			
			return szRtnVal;

		} catch(Exception e) {
			szMsg = "코일소재야드중량Check 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} // end of searchCoilYdWtCheck
	/**
	 *      [A] 오퍼레이션명 : 코일소재야드 HOTCOIL Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilYdHotCoilCheck(String szStlNo      , String szHotCoilTm      , String szYdStkLyrNo
			                             , String szLeftStlNo  , String szLeftHotCoilTm  
			                             , String szRightStlNo , String szRightHotCoilTm , long LngFR_VAL1) throws JDTOException  {
		
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilYdHotCoilCheck";
		String szOperationName	=	"코일소재야드HOTCOILCheck";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		

		long lngHotCoilTm  		= 0;  // 좌측코일
		long lngLeftHotCoilTm  	= 0;  // 좌측코일
		long lngRightHotCoilTm  = 0;  // 

		
		String sHotCoilTmYn  		= "";  // 좌측코일
		String sLeftHotCoilTmYn  	= "";  // 좌측코일
		String sRightHotCoilTmYn  	= "";  // 
		
		
		
		try {
			
//			if("".equals(szLeftStlNo) || "".equals(szRightStlNo)) {
//				return szRtnVal;
//			}
//			
			lngHotCoilTm		= Long.parseLong(szHotCoilTm);
			lngLeftHotCoilTm	= Long.parseLong(szLeftHotCoilTm);
			lngRightHotCoilTm	= Long.parseLong(szRightHotCoilTm);
		
			if(lngHotCoilTm < LngFR_VAL1){
				sHotCoilTmYn = "Y";
			} else {
				sHotCoilTmYn = "N";
			}
			
			if(lngLeftHotCoilTm < LngFR_VAL1){
				sLeftHotCoilTmYn = "Y";
			} else {
				sLeftHotCoilTmYn = "N";
			}
			
			if(lngRightHotCoilTm < LngFR_VAL1){
				sRightHotCoilTmYn = "Y";
			} else {
				sRightHotCoilTmYn = "N";
			}
			
        	szMsg = "1단좌측재료번호(" + szLeftStlNo + ")=>>좌측코일HOTCOIL 시간:"+lngLeftHotCoilTm ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단우측재료번호(" + szRightStlNo + ")=>>우측코일HOTCOIL 시간:"+lngRightHotCoilTm ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
        	szMsg = "재료번호(" + szStlNo + ")=>>HOTCOIL 시간:"+lngHotCoilTm;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
//1단 적치기준//
			if ("001".equals(szYdStkLyrNo)){
				// 좌우가 비워있는경우
				if ("".equals(szLeftStlNo) && "".equals(szRightStlNo)) {  // 좌우가 비워있는경우
					szRtnVal = YdConstant.RETN_CD_SUCCESS;
				// 좌가 비어 있고 우측에 HOTCOIL인 경우	
				} else if ("".equals(szLeftStlNo) && ((!"".equals(szRightStlNo)))&& (sRightHotCoilTmYn.equals("Y"))) {
					szRtnVal = YdConstant.RETN_CD_SUCCESS;
				// 우가 비어 있고 좌측에 HOTCOIL인 경우	
				} else if ("".equals(szRightStlNo) && ((!"".equals(szLeftStlNo))) && (sLeftHotCoilTmYn.equals("Y"))) {
					szRtnVal = YdConstant.RETN_CD_SUCCESS;
				} else {
					szRtnVal = YdConstant.RETN_CD_FAILURE;
				}	
			} 
//2단 적치기준
			if (("002".equals(szYdStkLyrNo)) && (!"".equals(szLeftStlNo)) && (!"".equals(szRightStlNo))) {
				if ((sHotCoilTmYn.equals("Y")) && (sLeftHotCoilTmYn.equals("Y"))&& (sRightHotCoilTmYn.equals("Y"))) {
						szRtnVal = YdConstant.RETN_CD_SUCCESS;
				} else {
						szRtnVal = YdConstant.RETN_CD_FAILURE;
				}
			}
			return szRtnVal;

		} catch(Exception e) {
			szMsg = "코일소재야드중량Check 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} // end of searchCoilYdHotCoilCheck

	
	/**
	 *      [A] 오퍼레이션명 : 외경전도 Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilYdOutDiaDiffCheck1(String szStlNo, String lOutDia, String szLeftStlNo,String lLeftOutDia, String szRightStlNo,String lRightOutDia
			                 ,long LngFR_VAL1, long LngTO_VAL1,	long LngFR_VAL2, long LngTO_VAL2,	long LngFR_VAL3, long LngTO_VAL3,	long LngFR_VAL4, long LngTO_VAL4) throws JDTOException  {
		
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilYdOutDiaDiffCheck1";
		String szOperationName	=	"코일소재야드외경전도 Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		
		long   lngOutDia   		= 0;           //코일외경
		long   lngLeftOutDia   	= 0;           //좌측코일외경
		long   lngRightOutDia  	= 0;           //우측코일외경
		long   lngMinOutDia  	= 0;           //좌우측을 비교하여 작은 외경 값
		
		try {
			
			if("".equals(szLeftStlNo) || "".equals(szRightStlNo)) {
				return szRtnVal;
			}
			
			lngOutDia 		= Long.parseLong(lOutDia);
			lngLeftOutDia 	= Long.parseLong(lLeftOutDia);
			lngRightOutDia 	= Long.parseLong(lRightOutDia);
			
			if(lngLeftOutDia >= lngRightOutDia){
				lngMinOutDia = lngRightOutDia;
			} else {
				lngMinOutDia = lngLeftOutDia;
			}
        	
        	szMsg = "1단좌측재료번호(" + szLeftStlNo + ")=>>좌측코일외경:"+lngLeftOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단우측재료번호(" + szRightStlNo + ")=>>우측코일외경:"+lngRightOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
        	szMsg = "코일외경:"+lngOutDia +"=>>최소 외경 값:"+lngMinOutDia;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
	       	if (Math.abs(lngLeftOutDia - lngRightOutDia) <= LngFR_VAL1) {  //50
	       		if ((lngMinOutDia + LngTO_VAL1) >= lngOutDia) {  //50
	       			szRtnVal = YdConstant.RETN_CD_SUCCESS;
	       		} else {
	        		szRtnVal = YdConstant.RETN_CD_FAILURE;
	       		} 
        	} else if (Math.abs(lngLeftOutDia - lngRightOutDia) <= LngFR_VAL2) { //100
	       		if ((lngMinOutDia + LngTO_VAL2) >= lngOutDia) {  //-50
	       			szRtnVal = YdConstant.RETN_CD_SUCCESS;
	       		} else {
	        		szRtnVal = YdConstant.RETN_CD_FAILURE;
	       		} 
           	} else if (Math.abs(lngLeftOutDia - lngRightOutDia) <= LngFR_VAL3) {  //200
           		if ((lngMinOutDia + LngTO_VAL3) >= lngOutDia) {  //-100
	       			szRtnVal = YdConstant.RETN_CD_SUCCESS;
	       		} else {
	        		szRtnVal = YdConstant.RETN_CD_FAILURE;
	       		} 
           	} else if (Math.abs(lngLeftOutDia - lngRightOutDia) <= LngFR_VAL4) {  ////300
	       		if ((lngMinOutDia + LngTO_VAL4) >= lngOutDia) {  //-150 
	       			szRtnVal = YdConstant.RETN_CD_SUCCESS;
	       		} else {
	        		szRtnVal = YdConstant.RETN_CD_FAILURE;
	       		} 
        	} else {
        		szRtnVal = YdConstant.RETN_CD_FAILURE;
        	}

			return szRtnVal;
		} catch(Exception e) {
			szMsg = "외경전도CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} 
	/**
	 *      [A] 오퍼레이션명 : 외경낙반 Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilYdOutDiaDiffCheck2(String szStlNo, String lOutDia, String szLeftStlNo,String lLeftOutDia, String szRightStlNo,String lRightOutDia, long LngFR_VAL1) throws JDTOException  {
		
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilYdOutDiaDiffCheck2";
		String szOperationName	=	"코일소재야드외경낙반 Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		
		long   lngOutDia   		= 0;           //코일외경
		long   lngLeftOutDia   	= 0;           //좌측코일외경
		long   lngRightOutDia  	= 0;           //우측코일외경
		long   lngMinOutDia  	= 0;           //좌우측을 비교하여 작은 외경 값
		
		try {
			
			if("".equals(szLeftStlNo) || "".equals(szRightStlNo)) {
				return szRtnVal;
			}
			
			lngOutDia 		= Long.parseLong(lOutDia);
			lngLeftOutDia 	= Long.parseLong(lLeftOutDia);
			lngRightOutDia 	= Long.parseLong(lRightOutDia);
	
//			
			
        	
        	szMsg = "1단좌측재료번호(" + szLeftStlNo + ")=>>좌측코일외경:"+lngLeftOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단우측재료번호(" + szRightStlNo + ")=>>우측코일외경:"+lngRightOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
	       	if(((lngLeftOutDia/2) + (lngRightOutDia/2) + lngOutDia) >= LngFR_VAL1) {  //
	       		szRtnVal = YdConstant.RETN_CD_SUCCESS;
	      	} else {
	        	szRtnVal = YdConstant.RETN_CD_FAILURE;
	      	}
	       	
			return szRtnVal;
			
		} catch(Exception e) {
			szMsg = "외경낙반CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} 
	/**
	 *      [A] 오퍼레이션명 : 폭 Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilYdWidthDiffCheck(String szStlNo, String lWidth, String szLeftStlNo,String lLeftWidth, String szRightStlNo,String lRightWidth, long LngFR_VAL1, long LngTO_VAL1) throws JDTOException  {
		
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilYdWidthDiffCheck";
		String szOperationName	=	"코일소재야드 폭 Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		 
		double   lngWidth   		= 0;           //코일폭
		double   lngLeftWidth   	= 0;           //좌측코일폭
		double   lngRighdWidth  	= 0;           //우측코일폭
		double   lngMinWidth  		= 0;           //좌우측을 비교하여 작은 폭 값
		
		
		
		try {
			
			if("".equals(szLeftStlNo) || "".equals(szRightStlNo)) {
				return szRtnVal;
			}
			lngWidth 		= Double.parseDouble(lWidth);
			lngLeftWidth 	= Double.parseDouble(lLeftWidth);
			lngRighdWidth 	= Double.parseDouble(lRightWidth);
			
			if(lngLeftWidth >= lngRighdWidth){
				lngMinWidth = lngRighdWidth;
			} else {
				lngMinWidth = lngLeftWidth;
			}
        	
        	szMsg = "1단좌측재료번호(" + szLeftStlNo + ")=>>좌측코일폭:"+lngLeftWidth ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단우측재료번호(" + szRightStlNo + ")=>>우측코일폭:"+lngRighdWidth ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
        	szMsg = "코일외경:"+lngWidth +"=>>최소 폭 값:"+lngMinWidth;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
	       	if (Math.abs(lngLeftWidth - lngRighdWidth) <= LngFR_VAL1) { //200
	       		if ((lngMinWidth - lngWidth) >= LngTO_VAL1) {   //-20
	       			szRtnVal = YdConstant.RETN_CD_SUCCESS;
	       		} else {
	        		szRtnVal = YdConstant.RETN_CD_FAILURE;
	       		} 
        	} else {
        		szRtnVal = YdConstant.RETN_CD_FAILURE;
        	}

			return szRtnVal;
		} catch(Exception e) {
			szMsg = "폭CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} 
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일소재야드좌측코일To위치평점항목Set
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String SearchCoilYdLeftCoilGrade(String szYdStkColGp, String szYdStkBedNo, String szYdStkLyrNo, String szStlNo, String szLeftColGp, String szLeftBedNo, String szLeftLyrNo, String szLeftStlNo) throws JDTOException  {

		YdStkLyrDao    ydStkLyrDao = new YdStkLyrDao();
		//코일공통 DAO
		PtOsCommDao ptOsCommDao     = new PtOsCommDao();
		
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"SearchCoilYdLeftCoilGrade";
		String szOperationName	=	"코일소재야드좌측코일To위치평점항목Set";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		JDTORecord recPara 		= null;
		
		JDTORecordSet rsLyr     = null;
		JDTORecord recLyr       = null;
		
		JDTORecordSet outRecSet = null;
		JDTORecord recCoil      = null;
		
		String szCurrProgCd = "";
		String szCoilStat = "";
				
		String szLeftCurrProgCd = "";
		String szLeftCoilStat = "";
				
		String szLeftGrade = "";
		
		
		try {
						
			// 좌측적치단정보
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szLeftColGp);
			recPara.setField("YD_STK_BED_NO", szLeftBedNo);
			recPara.setField("YD_STK_LYR_NO", szLeftLyrNo);
			rsLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsLyr, 0);
			
        	if (intRtnVal <= 0) {
				szMsg = "확인:"+szStlNo+"좌측적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			}
        	
        	rsLyr.first();
        	recLyr = rsLyr.getRecord();
        	
        	if("001".equals(szYdStkLyrNo) && ("X".equals(ydDaoUtils.paraRecChkNull(recLyr, "YD_STK_LYR_MTL_STAT")) || "E".equals(ydDaoUtils.paraRecChkNull(recLyr, "YD_STK_LYR_MTL_STAT")))) {
        		return szLeftGrade = "E";
        	}
        	
        	// 스케줄재료
        	recPara	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szStlNo);
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			
			intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet, 19);
			
			//리턴값 메세지처리
			if (intRtnVal == 0) {
				szMsg = "재료번호(" + szStlNo + ")에 대한 코일공통데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			} else if (intRtnVal < 0) {
				szMsg = "재료번호(" + szStlNo + ")로 코일공통데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			}
			
			outRecSet.absolute(1);
			recCoil = outRecSet.getRecord();
			
			//진도코드
			szCurrProgCd = ydDaoUtils.paraRecChkNull(recCoil, "CURR_PROG_CD");
			//코일상태
			szCoilStat = ydDaoUtils.paraRecChkNull(recCoil, "COIL_STAT");
						
			//--------------------------------------------------------------------------------------------------------------------------
			// 좌측재료
        	recPara	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szLeftStlNo);
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			
			intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet, 19);
			
			//리턴값 메세지처리
			if (intRtnVal == 0) {
				szMsg = "좌측재료번호(" + szLeftStlNo + ")에 대한 코일공통데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			} else if (intRtnVal < 0) {
				szMsg = "좌측재료번호(" + szLeftStlNo + ")로 코일공통데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			}
			
			outRecSet.absolute(1);
			recCoil = outRecSet.getRecord();
			
			//진도코드
			szLeftCurrProgCd = ydDaoUtils.paraRecChkNull(recCoil, "CURR_PROG_CD");
			//코일상태
			szLeftCoilStat = ydDaoUtils.paraRecChkNull(recCoil, "COIL_STAT");
			
			if(szCoilStat.equals(szLeftCoilStat) && szCurrProgCd.equals(szLeftCurrProgCd)) {
				szLeftGrade = "1";
			} else if (szCoilStat.equals(szLeftCoilStat) && !szCurrProgCd.equals(szLeftCurrProgCd)) {
				szLeftGrade = "2";
			} else if (!szCoilStat.equals(szLeftCoilStat) && szCurrProgCd.equals(szLeftCurrProgCd)) {
				szLeftGrade = "3";
			} else if (!szCoilStat.equals(szLeftCoilStat) && !szCurrProgCd.equals(szLeftCurrProgCd)) {
				szLeftGrade = "4";
			}
			
        	
			return szLeftGrade;
		} catch(Exception e) {
			szMsg = "코일소재야드좌측코일To위치평점항목Set 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} // end of SearchCoilYdLeftCoilGrade
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일소재야드우측코일To위치평점항목Set
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String SearchCoilYdRightCoilGrade(String szYdStkColGp, String szYdStkBedNo, String szYdStkLyrNo, String szStlNo, String szRightColGp, String szRightBedNo, String szRightLyrNo, String szRightStlNo) throws JDTOException  {
		
		YdStkLyrDao    ydStkLyrDao = new YdStkLyrDao();
		//코일공통 DAO
		PtOsCommDao ptOsCommDao     = new PtOsCommDao();
		
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"SearchCoilYdRightCoilGrade";
		String szOperationName	=	"코일소재야드우측코일To위치평점항목Set";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		JDTORecord recPara 		= null;
		
		JDTORecordSet rsLyr     = null;
		JDTORecord recLyr       = null;
		
		JDTORecordSet outRecSet = null;
		JDTORecord recCoil      = null;
		
		String szCurrProgCd = "";
		String szCoilStat = "";
				
		String szRightCurrProgCd = "";
		String szRightCoilStat = "";
		
		String szRightGrade = "";
		
		
		try {
						
			// 좌측적치단정보
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szRightColGp);
			recPara.setField("YD_STK_BED_NO", szRightBedNo);
			recPara.setField("YD_STK_LYR_NO", szRightLyrNo);
			rsLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsLyr, 0);
			
        	if (intRtnVal <= 0) {
				szMsg = "확인:"+szStlNo+"좌측적치단(" + szRightColGp + szRightBedNo + szRightLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			}
        	
        	rsLyr.first();
        	recLyr = rsLyr.getRecord();
        	
        	if("001".equals(szYdStkLyrNo) && ("X".equals(ydDaoUtils.paraRecChkNull(recLyr, "YD_STK_LYR_MTL_STAT")) || "E".equals(ydDaoUtils.paraRecChkNull(recLyr, "YD_STK_LYR_MTL_STAT")))) {
        		return szRightGrade = "E";
        	}
        	
        	// 스케줄재료
        	recPara	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szStlNo);
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			
			intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet, 19);
			
			//리턴값 메세지처리
			if (intRtnVal == 0) {
				szMsg = "재료번호(" + szStlNo + ")에 대한 코일공통데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			} else if (intRtnVal < 0) {
				szMsg = "재료번호(" + szStlNo + ")로 코일공통데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			}
			
			outRecSet.absolute(1);
			recCoil = outRecSet.getRecord();
			
			//진도코드
			szCurrProgCd = ydDaoUtils.paraRecChkNull(recCoil, "CURR_PROG_CD");
			//코일상태
			szCoilStat = ydDaoUtils.paraRecChkNull(recCoil, "COIL_STAT");
			
			//--------------------------------------------------------------------------------------------------------------------------
			// 우측재료
        	recPara	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szRightStlNo);
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			
			intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet, 19);
			
			//리턴값 메세지처리
			if (intRtnVal == 0) {
				szMsg = "좌측재료번호(" + szRightStlNo + ")에 대한 코일공통데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			} else if (intRtnVal < 0) {
				szMsg = "좌측재료번호(" + szRightStlNo + ")로 코일공통데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			}
			
			outRecSet.absolute(1);
			recCoil = outRecSet.getRecord();
			
			//진도코드
			szRightCurrProgCd = ydDaoUtils.paraRecChkNull(recCoil, "CURR_PROG_CD");
			//코일상태
			szRightCoilStat = ydDaoUtils.paraRecChkNull(recCoil, "COIL_STAT");
			
			if(szCoilStat.equals(szRightCoilStat) && szCurrProgCd.equals(szRightCurrProgCd)) {
				szRightGrade = "1";
			} else if (szCoilStat.equals(szRightCoilStat) && !szCurrProgCd.equals(szRightCurrProgCd)) {
				szRightGrade = "2";
			} else if (!szCoilStat.equals(szRightCoilStat) && szCurrProgCd.equals(szRightCurrProgCd)) {
				szRightGrade = "3";
			} else if (!szCoilStat.equals(szRightCoilStat) && !szCurrProgCd.equals(szRightCurrProgCd)) {
				szRightGrade = "4";
			}
        	
			
			return szRightGrade;
		} catch(Exception e) {
			szMsg = "코일소재야드우측코일To위치평점항목Set 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} // end of SearchCoilYdRightCoilGrade
	
	
	/**
	 * [A] 오퍼레이션명 : 코일제품야드 To위치검색(일반
	 * 송정현 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord searchCoilGdsYdToPosition(String sYdCrnSchId, String szStlNo, String szSchCd, String szRouteGp, String szYD_CAR_USE_GP, String szTRN_EQP_CD, String szCAR_NO, String szCARD_NO,String sYD_TO_LOC_DCSN_MTD,String sYD_TO_LOC_GUIDE,String szEqpId) throws JDTOException  {
		 
		YdLocSrchRngDao ydLocSrchRngDao = new YdLocSrchRngDao();
		YdStkBedDao     ydStkBedDao  	= new YdStkBedDao();
		YdStkLyrDao    	ydStkLyrDao 	= new YdStkLyrDao();
		YdEqpDao		ydEqpDao	= new YdEqpDao();
		
		int intRtnVal         = 0;
		JDTORecord outRecord1	= JDTORecordFactory.getInstance().create();
		String szMsg			=	"";
		String szMsgList		=	"";
		String szMethodName		=	"searchCoilGdsYdToPosition";
		String szOperationName	=	"코일제품 야드 To위치검색";
		JDTORecordSet rsResult  = null;
		JDTORecord recPara 		= null;
		JDTORecord recPara1 		= null;
		JDTORecordSet rsBed     = null;
		JDTORecord recBed       = JDTORecordFactory.getInstance().create(); 
		JDTORecordSet rsSaveLoc = null;
		JDTORecord   recSaveLoc = null;
		JDTORecord   recSaveLoc1 = null;
		JDTORecord	 inRecord2  = null;
		CoilYdToLocVO coilYdToLocVO = null;
		
		String szSchCoilStat    = null;  //스케줄코일상태
		String szLeftGrade      = null;  //좌측평점
		String szRightGrade     = null;  //좌측평점
		
		
//		String szYdStkColGp     = null;  // 적치열번호
//		String szYdStkBedNo     = null;  // 적치배드번호
//		String szYdStkLyrNo     = null;  // 적치단번호
//		String szYdStkBedUsgGp  = null;  // 적치배드용도구분
		
		String szRtnVal = "";
		String szToPosGrade = "";
		String szGrdColGp = "";
		String szGrdBedNo = "";
		String szGrdLyrNo = "";
		String szToPosGrd = "99";
		String sRTN_BED = "";
		String sMsg = "";
		
		String szYdStkColGp = "";
		String szYdStkBedNo = "";
		String szYdStkLyrNo = "";
		String szLeftColGp 	= "";
		String szLeftBedNo 	= "";
		String szLeftLyrNo 	= "";
		String szLeftStlNo 	= "";
		String szRightColGp = "";
		String szRightBedNo = "";
		String szRightLyrNo = "";
		String szRightStlNo = "";
		String sRTN_CD = "";
		String sRTN_MSG = "";
		
		String sRTN_CD2	= "";
		String sSTL_CNT	= "";
		String sSUM_WGT	= "";
		
		String szGrdColGp1 = "";
		String szGrdBedNo1 = "";
		String szGrdLyrNo1 = "";
		String szToPosGrd1 = "";
		String szLToPosGrd = "";
		String szRToPosGrd = "";
		String szRTN_MSG   = "";
		String szMATL_SUP_MTD_GP ="";  // 적치 폭기준
		String szCHK= "N";
		
		JDTORecord outRecord	= JDTORecordFactory.getInstance().create();
		
		try {
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치결정 START★★★★", YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "확인:"+szStlNo+"검색SQL 1st start", YdConstant.INFO);
			szSchCoilStat = "J";

			

			//PO 1단 적재 ////////////////////////////////////////////////
			/*
			 * E/H동에서 D동으로 이적시 아래 조건을 충족할수 있도록 통합이적지시 수정 요청

				 - 수요가 : 아세아철강 (A52981)
				 - 용도코드 : AK14, HF3, EZ0, EA5, EA5S, NA1
				 - 두께 : 3.2t 이하
			 * 
			 */
        	if("JEDD01LM".equals(szSchCd) || "JHKD01LM".equals(szSchCd)
        	   ||"JHTC01MM".equals(szSchCd)||"JHTC02MM".equals(szSchCd)
        	   ||"JETC01MM".equals(szSchCd)||"JETC02MM".equals(szSchCd)
        	   ){
        		
        		recPara = JDTORecordFactory.getInstance().create();
        		recPara.setField("STL_NO", szStlNo); 
        		recPara.setField("YD_BAY_GP", szSchCd.substring(1 , 2));
                rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
                /*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrPoCoilInChk*/
                intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 630);
                
                if(intRtnVal > 0) { 
                	for (int nLoop =0 ; nLoop<rsResult.size(); nLoop++ ){
        				
        				// 검색배드 정보를 Setting
        				recBed =JDTORecordFactory.getInstance().create();
        				recBed = rsResult.getRecord(nLoop);
        				
        				String szYdStkColGpCHK = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_COL_GP");
        				String szYdStkBedNoCHK = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_BED_NO");
        				String szYdStkLyrNoCHK = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_LYR_NO"); 
 
     				
        				outRecord1 = this.CoilGdsLyrBaseCheck(szStlNo, recBed);
				 
				
			        	sRTN_CD		 = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
						szYdStkColGp = StringHelper.evl(outRecord1.getFieldString("RTN_COLGP"), "");
						szYdStkBedNo = StringHelper.evl(outRecord1.getFieldString("RTN_BEDNO"), "");
						szYdStkLyrNo = StringHelper.evl(outRecord1.getFieldString("RTN_LYRNO"), "");
						sRTN_MSG	 = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
					
						if ("1".equals(sRTN_CD)) {
							outRecord.setField("RTN_BED" 	, szYdStkColGp + szYdStkBedNo + szYdStkLyrNo);	
							outRecord.setField("RTN_CD" 	, "1");	
							return outRecord;			   						
						}				
                	}
                }
        	}
        	///////////////////////////////////////////////////////////////////////////////////
			            	
			
			if(szYD_CAR_USE_GP.equals("L")) {
	        	// 구내운송인 경우(차량사용구분, 운송장비코드)
				recPara = JDTORecordFactory.getInstance().create();
				rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
				recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
				recPara.setField("TRN_EQP_CD",    szTRN_EQP_CD);
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedByCarUseGpandTrnEqpCd*/
				intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsBed, 9);
					
        	}else if (szYD_CAR_USE_GP.equals("G")){
        	// 출하인 경우(차량사용구분, 카드번호, 차량번호)
        		recPara = JDTORecordFactory.getInstance().create();
        		rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
        		recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
        		recPara.setField("CAR_NO",        szCAR_NO);
        		recPara.setField("CARD_NO",       szCARD_NO);
        		/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedByCarUseGpandCarNoCardNo_PIDEV*/
        		intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsBed, 20);
        		
        	}else if ((szSchCd.substring(2, 4).equals("TC")) && (szSchCd.substring(6, 7).equals("U"))){
	    		recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_SCH_CD", szSchCd);
    			recPara.setField("YD_ROUTE_GP", szRouteGp);
    			recPara.setField("STL_NO", szStlNo);
	        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	    /*com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserGdsByToLoc8*/
	        	intRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, rsBed, 501);

        	}else if ((sYD_TO_LOC_DCSN_MTD.equals("F")) && (szSchCd.substring(2, 4).equals("HC"))  && (sYD_TO_LOC_GUIDE.trim().length() == 4)) {
	 				//사용자 지정 BED 검색:4 (결로재 보급)
	 	    		recPara = JDTORecordFactory.getInstance().create();
	 				recPara.setField("STL_NO", szStlNo);
	 	        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
	 	    	    /*com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserByToLocIn*/
	 	        	intRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, rsBed, 604);
	 	
        	}else if ((sYD_TO_LOC_DCSN_MTD.equals("F")) && (sYD_TO_LOC_GUIDE.trim().length() == 8) ){
				szMsg = "sYD_TO_LOC_GUIDE(" + sYD_TO_LOC_GUIDE + ")"+ "sYD_TO_LOC_DCSN_MTD(" + sYD_TO_LOC_DCSN_MTD + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	        	recBed.setField("YD_STK_COL_GP", "J"+ sYD_TO_LOC_GUIDE.substring(0,5));
				recBed.setField("YD_STK_BED_NO", sYD_TO_LOC_GUIDE.substring(6,8));
				recBed.setField("YD_STK_LYR_NO", "00"+ sYD_TO_LOC_GUIDE.substring(5,6));
	        	
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal = ydStkLyrDao.getYdStklyr(recBed, rsResult, 0);
				if (intRtnVal < 1) {
					outRecord.setField("RTN_BED" 	, "");	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
				}


				//적치단정보 레코드 추출
				rsResult.first();
				recPara1 = JDTORecordFactory.getInstance().create();
				recPara1 = rsResult.getRecord();
				
				String szYD_STK_LYR_ACT_STAT = ydDaoUtils.paraRecChkNull(recPara1, "YD_STK_LYR_ACT_STAT");

				ydUtils.putLog(szSessionName, szMethodName, "szYD_STK_LYR_ACT_STAT" +szYD_STK_LYR_ACT_STAT , YdConstant.DEBUG);
				
				//적치단 재료상태가 적치가능('E')가 아니면 에러 리턴
				if(!(szYD_STK_LYR_ACT_STAT.equals("E")) && !(szYD_STK_LYR_ACT_STAT.equals("S"))) {
					
					szMsg = "적치단 재료상태(" + szYD_STK_LYR_ACT_STAT + ") 적치가능 상태가 아닙니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					outRecord.setField("RTN_BED" 	, "");	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
				}

				//결로방지재 적치장 처리 
				if(szYD_STK_LYR_ACT_STAT.equals("S")){
					outRecord.setField("RTN_BED" 	, "J"+ sYD_TO_LOC_GUIDE.substring(0,5) + sYD_TO_LOC_GUIDE.substring(6,8) + "00"+ sYD_TO_LOC_GUIDE.substring(5,6));	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
				}else{
					outRecord1 = this.CoilGdsLyrBaseCheck(szStlNo, recBed);
				}
				
	        	sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				szYdStkColGp = StringHelper.evl(outRecord1.getFieldString("RTN_COLGP"), "");
				szYdStkBedNo = StringHelper.evl(outRecord1.getFieldString("RTN_BEDNO"), "");
				szYdStkLyrNo = StringHelper.evl(outRecord1.getFieldString("RTN_LYRNO"), "");
				sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				
				if ("1".equals(sRTN_CD)) {
					outRecord.setField("RTN_BED" 	, szYdStkColGp + szYdStkBedNo + szYdStkLyrNo);	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
		   						
				} else {
					
//LOG_TABLE    					
	    			EJBConnector ejbConn = null;
	    			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
	    			recLog.setField("STL_NO"			, szStlNo);
	    			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
	    			recLog.setField("YD_GP"				, "J");
	    			recLog.setField("YD_SCH_CD"			, szSchCd);
	    			recLog.setField("YD_USER_ID"		, "log");
	    			recLog.setField("MSG"				, sRTN_MSG);
	    			
	    			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
	    			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
					
					outRecord.setField("RTN_BED" 	, "");	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
				}	
        	

        	}else if ((sYD_TO_LOC_DCSN_MTD.equals("F")) && (sYD_TO_LOC_GUIDE.trim().length() == 6) ){
				szMsg = "sYD_TO_LOC_GUIDE(" + sYD_TO_LOC_GUIDE + ")"+ "sYD_TO_LOC_DCSN_MTD(" + sYD_TO_LOC_DCSN_MTD + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
					//사용자 지정 BED 검색:6
	    		recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", szStlNo); 
	        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	    /*com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserGdsByToLoc6*/
	        	intRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, rsBed, 500);

			} else{
    				
					//위치검색베드로 to위치를 결정하는 경우
            		recPara = JDTORecordFactory.getInstance().create();
        			recPara.setField("YD_SCH_CD", szSchCd);
        			recPara.setField("YD_ROUTE_GP", szRouteGp);
        			recPara.setField("STL_NO", szStlNo);
                	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
                	// 일반적이 검색 BED임 
                	/*com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngBySchCdRtGpandStlNo_PIDEV*/
                	intRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, rsBed, 7);
                	
    				
        	}
			
        	if (intRtnVal < 0) {
				szMsg = "재료번호(" + szStlNo + ")로 위치검색배드 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return szRtnVal = YdConstant.RETN_CD_FAILURE;
				outRecord.setField("RTN_BED" 	, "");	
				outRecord.setField("RTN_CD" 	, "1");	 //RTN_MSG
				outRecord.setField("RTN_MSG" 	, "위치검색순서조건이 이상합니다.<br>" + sMsg );	 //RTN_MSG
				return outRecord;

        	}
        	
        	if (intRtnVal == 0) {

				outRecord.setField("RTN_BED" 	, "");	
				outRecord.setField("RTN_CD" 	, "1");	
				outRecord.setField("RTN_MSG" 	, "위치검색순서조건이 이상합니다.<br>" + sMsg );	 //RTN_MSG
				
				return outRecord;
        		
//        		return szRtnVal;
        	}
        	
        	rsSaveLoc = JDTORecordFactory.getInstance().createRecordSet("Temp");
        	ArrayList listSaveGrade = new ArrayList();
        	
        	rsBed.first();
        	
        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 적치대상제 검색 START★★★★★", YdConstant.INFO);
        	
			for (int nLoop =0 ; nLoop<rsBed.size(); nLoop++ ){
				
				// 검색배드 정보를 Setting
				recBed =JDTORecordFactory.getInstance().create();
				recBed = rsBed.getRecord(nLoop);
				
				String szYdStkColGpCHK = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_COL_GP");
				String szYdStkBedNoCHK = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_BED_NO");
				String szYdStkLyrNoCHK = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_LYR_NO");
				szMATL_SUP_MTD_GP = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_LYR_NO");
				
				//base기본 적치 기준 점검
				outRecord1 = this.CoilGdsLyrBaseCheck(szStlNo, recBed);
				sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				
				//결로방지재 적치장 처리 
				if(szSchCd.substring(2, 4).equals("HC") ){
					String sCHK_YN ="Y"; 
					JDTORecord jrParam = JDTORecordFactory.getInstance().create();
					JDTORecordSet jsSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
					jrParam.setField("YD_BAY_GP"    , szYdStkColGpCHK.substring(1 , 2)  ); //동정보 
					 
					jsSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getJ10006ChkYN", "SYSTEM", szMethodName, "HOT COIL적치 BASE기준체크 조회");
					
					if (jsSch.size() > 0) {
						/**********************************************************
						* 해당동 사용여부
						**********************************************************/
						sCHK_YN    		=  StringHelper.evl(jsSch.getRecord(0).getFieldString("CHK_YN" )  , "N"); 
						
						if("N".equals(sCHK_YN)){
							sRTN_CD ="1";
						}
					}
					ydUtils.putLog(szSessionName, szMethodName, "★★★★★HOT COIL적치 BASE기준체크 sCHK_YN:"+sCHK_YN +" ,sRTN_CD:"+sRTN_CD, YdConstant.INFO);
					
					if ("1".equals(sRTN_CD)) {
						outRecord.setField("RTN_BED" 	, szYdStkColGpCHK + szYdStkBedNoCHK + szYdStkLyrNoCHK);	
						outRecord.setField("RTN_CD" 	, "1");	
						
						ydUtils.putLog(szSessionName, szMethodName, "######>>>>결로재HOT코일 보급위치:"+ szYdStkColGpCHK + szYdStkBedNoCHK + szYdStkLyrNoCHK +" 결정완료", YdConstant.INFO);
						
						return outRecord;
					}else{
						continue;
					}
				}

				ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 검색>>"+nLoop+"★★★★★"+szYdStkColGpCHK+szYdStkBedNoCHK+szYdStkLyrNoCHK+">>"+szStlNo, YdConstant.INFO);

//				outRecord1 = this.CoilGdsLyrBaseCheck(szStlNo, recBed);
//				sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//				sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				
				if ("-1".equals(sRTN_CD)) {
					/*
					recSaveLoc = JDTORecordFactory.getInstance().create();
		        	recSaveLoc.setField("YD_STK_COL_GP", szYdStkColGpCHK);
		        	recSaveLoc.setField("YD_STK_BED_NO", szYdStkBedNoCHK);
		        	recSaveLoc.setField("YD_STK_LYR_NO", szYdStkLyrNoCHK);
		        	recSaveLoc.setField("TO_POS_GRADE" , "999");
		        	recSaveLoc.setField("RTN_MSG" , sRTN_MSG);
		        	ydUtils.displayRecord(szOperationName, recSaveLoc);
		        	*/
		        	rsSaveLoc.addRecord(recSaveLoc);
//		        	150909 hun list에 VO들 담아서 소팅
		        	coilYdToLocVO = new CoilYdToLocVO();
		        	coilYdToLocVO.setYdStkColGp(szYdStkColGp);
		        	coilYdToLocVO.setYdStkBedNo(szYdStkBedNo);
		        	coilYdToLocVO.setYdStkLyrNo(szYdStkLyrNo);
		        	coilYdToLocVO.setToPosGrade(999 );
		        	
		        	listSaveGrade.add(coilYdToLocVO);

					continue;
				}	
				if ("0".equals(sRTN_CD)) {
					outRecord.setField("RTN_BED" 	, "");	
					outRecord.setField("RTN_CD" 	, "1");	

//					return sRTN_BED = "";
				}	
				szYdStkColGp = StringHelper.evl(outRecord1.getFieldString("RTN_COLGP"), "");
				szYdStkBedNo = StringHelper.evl(outRecord1.getFieldString("RTN_BEDNO"), "");
				szYdStkLyrNo = StringHelper.evl(outRecord1.getFieldString("RTN_LYRNO"), "");
				szLeftColGp 	= StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_COLGP"), "");
				szLeftBedNo 	= StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_BEDNO"), "");
				szLeftLyrNo 	= StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_LYRNO"), "");
				szLeftStlNo 	= StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_STLNO"), "");
				szRightColGp = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_COLGP"), "");
				szRightBedNo = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_BEDNO"), "");
				szRightLyrNo = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_LYRNO"), "");
				szRightStlNo = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_STLNO"), "");
				
				//대차는 평점 check 하지 않는다
//	        	if(szYdStkColGp.substring(2, 4).equals("TC")) {
				if((szYdStkColGp.substring(2, 3).equals("0"))||
						(szYdStkColGp.substring(2, 3).equals("1"))||
						(szYdStkColGp.substring(2, 3).equals("2"))||
						(szYdStkColGp.substring(2, 3).equals("3"))||
						(szYdStkColGp.substring(2, 3).equals("4"))||
						(szYdStkColGp.substring(2, 3).equals("5"))||
						(szYdStkColGp.substring(2, 3).equals("6"))||
						szCHK.equals("Y")) { //확장야드 시 변경 대상부분
					
	        	} else {
	        		szRtnVal = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
	        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품 대차 중량체크 로직=>>>>"+szRtnVal, YdConstant.INFO);
	 					inRecord2 = JDTORecordFactory.getInstance().create();
	 					JDTORecord 		outRecord4 = JDTORecordFactory.getInstance().create();
	 					
 	 					inRecord2.setField("YD_SCH_CD"		, szSchCd);//스케줄코드
 	 					inRecord2.setField("YD_CRN_SCH_ID"	, sYdCrnSchId);  //
 	 					EJBConnector  ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
 	 					outRecord4 = (JDTORecord)ejbConn.trx("getStkColTCarUpChk2", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
 	 					
 	 					sRTN_CD2	= StringHelper.evl(outRecord4.getFieldString("RTN_CD"), "0");
 	 					sSTL_CNT	= StringHelper.evl(outRecord4.getFieldString("STL_CNT"), "0");
 	 					sSUM_WGT	= StringHelper.evl(outRecord4.getFieldString("SUM_WGT"), "0");
 	 					
 	 					if ("0".equals(sRTN_CD2)) {
 	 						szMsg = "상차 가능 CHECK시  ERROR";
 	 						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 	 					}	
 	 					
 	 					double dSTL_CNT = Integer.parseInt(sSTL_CNT);
 	 					double dSUM_WGT = Integer.parseInt(sSUM_WGT);
 	 					if (dSTL_CNT <= 3) {
 	 						if (dSUM_WGT > YdConstant.YD_COIL_TC_WEIGH_MAX) {
	 	 						szMsg = "중량초 오버로 대차상차 불가능 (대차 자동 출발 )";
	 	 						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	 	 						
	 	 						szCHK = "Y";
	 	 						
	 	 						YdTcarFtmvMtlDao ydTcarftmvmtlDao = new YdTcarFtmvMtlDao();
	 	 						JDTORecordSet    outRecSet  = null;
	 	 						recPara = JDTORecordFactory.getInstance().create();				
	 	 			    		recPara.setField("YD_EQP_ID", szSchCd);
	 	 			    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp"); 
	 	 			    		/*com.inisteel.cim.yd.dao.ydtcarftmvmtldao.YdTcarftmvmtlDao.getYdStockTcarSchCHK*/
	 	 			    		int nMtlCnt   = ydTcarftmvmtlDao.getYdTcarftmvmtl(recPara, outRecSet,303);

	 	 			    		if (nMtlCnt > 0 ) {	 	
		 	 						//대차 출발 처리 ***********************************
		 	 			    		JDTORecord[] inRecord  =  null;
		 	 			    		inRecord = new JDTORecord[1];
		 	 			    		
		 	 			    		inRecord[0] = JDTORecordFactory.getInstance().create();
		 	 			    		inRecord[0].setRecord(outRecSet.getRecord(0));
		 	 			    		inRecord[0].setField("YD_CRN_SCH_ID"	, sYdCrnSchId);
		 	 			    		inRecord[0].setField("YD_SCH_CD"	, szSchCd);
		 	 			    		
		 	 						//대차 출발처리 
		 	 						EJBConnector  ejbConnT = new EJBConnector("default", "CoilGdsJspSeEJB", this);
		 	 	 					ejbConnT.trx("CoilYdTcarStsSetTcarD", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

	 	 			    		} 
 
//	 	 			        	150909 hun list에 VO들 담아서 소팅
	 	 			        	coilYdToLocVO = new CoilYdToLocVO();
	 	 			        	coilYdToLocVO.setYdStkColGp(szYdStkColGp);
	 	 			        	coilYdToLocVO.setYdStkBedNo(szYdStkBedNo);
	 	 			        	coilYdToLocVO.setYdStkLyrNo(szYdStkLyrNo);
	 	 			        	coilYdToLocVO.setToPosGrade(999 );
	 	 			        	
	 	 			        	listSaveGrade.add(coilYdToLocVO);
	 	 			    		 

	 	 						continue;   			    				
 	 						}
 	 					}
	        		
	        		
	        		
	        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품설비 TO위치=>>>>"+szRtnVal, YdConstant.INFO);
					outRecord.setField("RTN_BED" 	, szRtnVal);	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
//	        		return szRtnVal;
	        	}
	        	
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 1단 좌측 코일 To위치평점항목  체크Set  START★★★★★", YdConstant.INFO);	        	
	        	// 1단 좌측 코일 To위치평점항목 Set.
	        	szLeftGrade = SearchCoilGdsYdLeftCoilGrade(szYdStkColGp, szYdStkBedNo, szYdStkLyrNo, szStlNo, szLeftColGp, szLeftBedNo, szLeftLyrNo, szLeftStlNo);
	        	
	        	if (szLeftGrade.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"좌측적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 재료번호[" + szLeftStlNo + "]의 평점항목Set에 오류가 있습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					/*
					recSaveLoc = JDTORecordFactory.getInstance().create();
		        	recSaveLoc.setField("YD_STK_COL_GP", szYdStkColGpCHK);
		        	recSaveLoc.setField("YD_STK_BED_NO", szYdStkBedNoCHK);
		        	recSaveLoc.setField("YD_STK_LYR_NO", szYdStkLyrNoCHK);
		        	recSaveLoc.setField("TO_POS_GRADE" , "999");
		        	recSaveLoc.setField("RTN_MSG" , szMsg);
		        	ydUtils.displayRecord(szOperationName, recSaveLoc);
		        	
		        	rsSaveLoc.addRecord(recSaveLoc);
		        	*/
//		        	150909 hun list에 VO들 담아서 소팅
		        	coilYdToLocVO = new CoilYdToLocVO();
		        	coilYdToLocVO.setYdStkColGp(szYdStkColGp);
		        	coilYdToLocVO.setYdStkBedNo(szYdStkBedNo);
		        	coilYdToLocVO.setYdStkLyrNo(szYdStkLyrNo);
		        	coilYdToLocVO.setToPosGrade(999 );
		        	
		        	listSaveGrade.add(coilYdToLocVO);

					continue;
        		}else {
        			szMsg = "확인:"+szStlNo+"좌측적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 재료번호[" + szLeftStlNo + "]의 평점항목Set[" + szLeftGrade + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		}
	        	
	        	// 1단 우측 코일 To위치평점항목 Set.
	        	szRightGrade = SearchCoilGdsYdRightCoilGrade(szYdStkColGp, szYdStkBedNo, szYdStkLyrNo, szStlNo, szRightColGp, szRightBedNo, szRightLyrNo, szRightStlNo);
	        	
	        	if (szLeftGrade.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"우측적치단(" + szRightColGp + szRightBedNo + szRightLyrNo + ")의 재료번호[" + szRightStlNo + "]의 평점항목Set에 오류가 있습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					/*
					recSaveLoc = JDTORecordFactory.getInstance().create();
		        	recSaveLoc.setField("YD_STK_COL_GP", szYdStkColGpCHK);
		        	recSaveLoc.setField("YD_STK_BED_NO", szYdStkBedNoCHK);
		        	recSaveLoc.setField("YD_STK_LYR_NO", szYdStkLyrNoCHK);
		        	recSaveLoc.setField("TO_POS_GRADE" , "999");
		        	recSaveLoc.setField("RTN_MSG" , szMsg);
		        	ydUtils.displayRecord(szOperationName, recSaveLoc);
		        	
		        	rsSaveLoc.addRecord(recSaveLoc);
		        	*/
//		        	150909 hun list에 VO들 담아서 소팅
		        	coilYdToLocVO = new CoilYdToLocVO();
		        	coilYdToLocVO.setYdStkColGp(szYdStkColGp);
		        	coilYdToLocVO.setYdStkBedNo(szYdStkBedNo);
		        	coilYdToLocVO.setYdStkLyrNo(szYdStkLyrNo);
		        	coilYdToLocVO.setToPosGrade(999 );
		        	
		        	listSaveGrade.add(coilYdToLocVO);

					continue;
        		}else {
        			szMsg = "확인:"+szStlNo+"우측적치단(" + szRightColGp + szRightBedNo + szRightLyrNo + ")의 재료번호[" + szRightStlNo + "]의 평점항목Set[" + szRightGrade + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		}
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 1단 좌측 코일 To위치평점항목 체크Set  END★★★★★", YdConstant.INFO);
	        	
	        	
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★", YdConstant.INFO);
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 To위치 평점 계산  START★★★★★", YdConstant.INFO);
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★", YdConstant.INFO);
	        	
	        	// To위치 평점항목 Set
	        	szToPosGrade = getToPosGrade(szSchCoilStat, szYdStkLyrNo, szLeftGrade, szRightGrade);
	        	
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일야드 BRE: 스케줄코일상태("+szSchCoilStat+") TO위치적치단("+szYdStkLyrNo+") 좌측1단코일상태("+szLeftGrade+") 우측1단코일상태("+szRightGrade+")", YdConstant.INFO);	    		
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치 평점결과:loop순번:"+(nLoop+1)+" 재료번호(" + szStlNo + ")("+szYdStkColGp + szYdStkBedNo + szYdStkLyrNo+") ▶▶▶▶"+szToPosGrade+"점◀◀◀◀", YdConstant.INFO);
	        	
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★", YdConstant.INFO);
	        	
	        	 
	        	ydUtils.putLog(szSessionName, szMethodName, "151016 hun 코일 적치폭기준 제품군 모으기 start", YdConstant.INFO);
//	        	적치폭기준 0:1000~1100 , 1:1100~1199 , 4:1400~1499 , 5:1500~1600 제품군 지정 베드에 우선 배정
	        	if(ydEqpDao.chkCoilSupMtdGp(szStlNo, szYdStkColGp, szYdStkBedNo, szYdStkLyrNo)){
	        		szToPosGrade = "1";
	        		
	        		ydUtils.putLog(szSessionName, szMethodName, "151016 hun 코일 적치폭기준 제품군 모으기 평점1점 set !!", YdConstant.INFO);
	        	}
	        	ydUtils.putLog(szSessionName, szMethodName, "151016 hun 코일 제품군 모으기 end", YdConstant.INFO);
	        	
	        	
	        	// 평점이 1순위면 바로 리턴한다.
	        	if ("1".equals(szToPosGrade)) {
	        		szRtnVal = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
	        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치=>>>>"+szRtnVal, YdConstant.INFO);

//LOG_TABLE    					
	    			EJBConnector ejbConn = null;
	    			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
	    			recLog.setField("STL_NO"			, szStlNo);
	    			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
	    			recLog.setField("YD_GP"				, "J");
	    			recLog.setField("YD_SCH_CD"			, szSchCd);
	    			recLog.setField("YD_USER_ID"		, "log");
	    			recLog.setField("MSG"				, "★★★★★C열연코일제품야드 TO위치=>>>>"+szRtnVal + ":평점 1점");
	    			
	    			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
	    			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
	        		
	        		
	        		outRecord.setField("RTN_BED" 	, szRtnVal);	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord; 
	        	}
	        	
	        	/*
	        	 * TO위치 대상건이 50건이 넘는 경우 
	        	 * 평점대상 기준을 30점 이하로 조정 한다.
	        	 */
	        	int toPosGrade = Integer.parseInt(szToPosGrade);
	        	
	        	if (nLoop>=50 && toPosGrade <= 30) {
	        		szRtnVal = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
	        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치2=>>>>"+szRtnVal, YdConstant.INFO);

//LOG_TABLE    					
	    			EJBConnector ejbConn = null;
	    			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
	    			recLog.setField("STL_NO"			, szStlNo);
	    			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
	    			recLog.setField("YD_GP"				, "J");
	    			recLog.setField("YD_SCH_CD"			, szSchCd);
	    			recLog.setField("YD_USER_ID"		, "log");
	    			recLog.setField("MSG"				, "★★★★★C열연코일제품야드 TO위치=>>>>"+szRtnVal + ":50회 이상 평점 30점 이하:" +szToPosGrade+"점에서 결정됨");
	    			
	    			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
	    			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
	        		
	        		
	        		outRecord.setField("RTN_BED" 	, szRtnVal);	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord; 
	        	}
	        	
	        	
	        	
	        	
	        	
//	        	150909 hun list에 VO들 담아서 소팅
	        	coilYdToLocVO = new CoilYdToLocVO();
	        	coilYdToLocVO.setYdStkColGp(szYdStkColGp);
	        	coilYdToLocVO.setYdStkBedNo(szYdStkBedNo);
	        	coilYdToLocVO.setYdStkLyrNo(szYdStkLyrNo);
	        	coilYdToLocVO.setToPosGrade(Integer.parseInt(szToPosGrade) );
	        	
	        	listSaveGrade.add(coilYdToLocVO);

				
			} //end of for	
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 적치대상제 검색 END★★★★★", YdConstant.INFO);
			
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★1C열연코일제품야드 평점 1점이 아닌경우 대상 찾기 START★★★★★", YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "C열연코일제품야드 "+ szStlNo + "평점리스트", YdConstant.INFO);

			/*
			if (rsSaveLoc.size() > 0) {
				for(int nLoop =0 ; nLoop<rsSaveLoc.size(); nLoop++ ) {
					rsSaveLoc.absolute(nLoop);
					recSaveLoc1 = JDTORecordFactory.getInstance().create();
					recSaveLoc1 = rsSaveLoc.getRecord(nLoop);
					
					szGrdColGp1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "YD_STK_COL_GP");
					szGrdBedNo1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "YD_STK_BED_NO");
					szGrdLyrNo1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "YD_STK_LYR_NO");
					szToPosGrd1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "TO_POS_GRADE");
					szLToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc1, "TO_LEFT_GRADE");
					szRToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc1, "TO_RIGTH_GRADE");
					szRTN_MSG   =  ydDaoUtils.paraRecChkNull(recSaveLoc1, "RTN_MSG");
					
					szMsg ="[" + szStlNo + "]"+"["+ nLoop+"]" + szGrdColGp1 + szGrdBedNo1 + szGrdLyrNo1 +"<<Score="+szToPosGrd1+"(" + "L:"+szLToPosGrd+ "|R:"+szRToPosGrd+")>>,"+szRTN_MSG+"\r\n";
					szMsgList = szMsgList + szMsg;
					
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				} // end of for
			}	
			*/
			szMsg ="[ 평점 sort arrayList ] size="+listSaveGrade.size()+"\r\n";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
//			151015 hun 동점경우 2단 우선 배치를 위해 평점 뒤에 적치단 정보 입력 ( 002-> 1점, 001->2점, ""->3점 )
			if(listSaveGrade.size()>0){
				for(int nLoop =0 ; nLoop<listSaveGrade.size(); nLoop++ ) {
					coilYdToLocVO = (CoilYdToLocVO)listSaveGrade.get(nLoop);
					if("001".equals( coilYdToLocVO.getYdStkLyrNo()) ){
						coilYdToLocVO.setYdStkLyrGrade( Integer.parseInt( coilYdToLocVO.getToPosGrade() + "2"));
					}else if("002".equals( coilYdToLocVO.getYdStkLyrNo()) ){
						coilYdToLocVO.setYdStkLyrGrade( Integer.parseInt( coilYdToLocVO.getToPosGrade() + "1"));
					}else {
						coilYdToLocVO.setYdStkLyrGrade( Integer.parseInt( coilYdToLocVO.getToPosGrade() + "3"));
					}
				}
			}
			
//			150908 hun 평점을 arrayList에 담아서 소트
			if(listSaveGrade.size()>0){
				Collections.sort(listSaveGrade, new CoilYdToLayComparator());
			}
			
			if (listSaveGrade.size() > 0) {
				for(int nLoop =0 ; nLoop<listSaveGrade.size(); nLoop++ ) {
					coilYdToLocVO = (CoilYdToLocVO)listSaveGrade.get(nLoop);
					szMsg ="sort Grade[" + szStlNo + "]"+nLoop+",ColGp="+ coilYdToLocVO.getYdStkColGp() + coilYdToLocVO.getYdStkBedNo() + coilYdToLocVO.getYdStkLyrNo() +
					"<<Score="+coilYdToLocVO.getToPosGrade()+">>\r\n";
					szMsgList = szMsgList + szMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				} // end of for
			}	

//LOG_TABLE    					
			EJBConnector ejbConn = null;
			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
			recLog.setField("STL_NO"			, szStlNo);
			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
			recLog.setField("YD_GP"				, "J");
			recLog.setField("YD_SCH_CD"			, szSchCd);
			recLog.setField("YD_USER_ID"		, "log");
			//recLog.setField("MSG"				, szMsgList.substring(0, 999));
			recLog.setField("MSG"				, szMsgList);
			
			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
			
			/*
			if (rsSaveLoc.size() > 0) {
				for(int nLoop =0 ; nLoop<rsSaveLoc.size(); nLoop++ ) {
					rsSaveLoc.absolute(nLoop);
					recSaveLoc = JDTORecordFactory.getInstance().create();
					recSaveLoc = rsSaveLoc.getRecord(nLoop);
					
					if(nLoop ==0){							 
						szToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc, "TO_POS_GRADE");
						
						if(!szToPosGrd.equals("999")){
							szGrdColGp = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_COL_GP");
							szGrdBedNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_BED_NO");
							szGrdLyrNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_LYR_NO");
						} 
					}
					
//					szMsg ="["+ nLoop+"] 재료번호(" + szStlNo + ") C열연코일제품야드 TO위치=>>>>(" + szGrdColGp + szGrdBedNo + szGrdLyrNo +")점수:"+szToPosGrd;
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
						if (Integer.parseInt(szToPosGrd) > Integer.parseInt(ydDaoUtils.paraRecChkNull(recSaveLoc, "TO_POS_GRADE"))) {
							szGrdColGp = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_COL_GP");
							szGrdBedNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_BED_NO");
							szGrdLyrNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_LYR_NO");
							szToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc, "TO_POS_GRADE");
						}
										
				} // end of for
				*/
			if (listSaveGrade.size() > 0) {
				for(int nLoop =0 ; nLoop<listSaveGrade.size(); nLoop++ ) {
					coilYdToLocVO = (CoilYdToLocVO)listSaveGrade.get(nLoop);
					
					szGrdColGp = coilYdToLocVO.getYdStkColGp();
					szGrdBedNo = coilYdToLocVO.getYdStkBedNo();
					szGrdLyrNo = coilYdToLocVO.getYdStkLyrNo();
					szToPosGrd = String.valueOf(coilYdToLocVO.getToPosGrade());
					
//					999 점 pass
					if(coilYdToLocVO.getToPosGrade()==999){
						continue;
					}
					//마지막 체크 To위치에 코일이 있는지 확인
		        	ydUtils.putLog(szSessionName, szMethodName, "===================선택된 저장위치가 공위치인지 CHECK 1st메서드====================", YdConstant.INFO);
					
		        	recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", szGrdColGp);
					recPara.setField("YD_STK_BED_NO", szGrdBedNo);
					recPara.setField("YD_STK_LYR_NO", szGrdLyrNo);
					
		        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
		        	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyr*/
		        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBed, 0);
		        			
		        	if (intRtnVal > 0) {
		        		rsBed.first();
		    			recBed =JDTORecordFactory.getInstance().create();
		    			recBed = rsBed.getRecord();
		    			String sYD_STK_LYR_MTL_STAT = recBed.getFieldString("YD_STK_LYR_MTL_STAT");
		    			
		    			if(!sYD_STK_LYR_MTL_STAT.equals("E")){
		    	   			szMsg = "확인:"+szStlNo+"적치단(" + szGrdColGp + szGrdBedNo + szGrdLyrNo + ") 선택된 To위치에 coil 발견 CHECK ERROR.";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
			        		continue;
		    			}else{
					
			        		szRtnVal = szGrdColGp + szGrdBedNo + szGrdLyrNo;
			        		
			        		ydUtils.putLog(szSessionName, szMethodName, szStlNo+"1st end To위치=>>>>"+szRtnVal, YdConstant.INFO);
			        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 평점 1점이 아닌경우 대상 찾기 END★★★★★", YdConstant.INFO);
			    			outRecord.setField("RTN_BED" 		, szRtnVal);	
			    			outRecord.setField("RTN_CD" 		, "1");	
			    			
			    			return outRecord;
		    			}
		        	}
					
				}
				
				ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치=>>>>"+szRtnVal, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 평점 1점이 아닌경우 대상 찾기 END★★★★★", YdConstant.INFO);

			} else {
				szRtnVal = "";
			}				
			
			
			
			ydUtils.putLog(szSessionName, szMethodName, szStlNo+"1st end", YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치결정 END★★★★★", YdConstant.INFO);
			outRecord.setField("RTN_BED" 		, szRtnVal);	
			outRecord.setField("RTN_CD" 		, "1");	
			
			return outRecord;

//			return szRtnVal;
		} catch(Exception e) {
			szMsg = "코일창고야드 To위치검색 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치결정 ERROR★★★★★", YdConstant.INFO);
			outRecord.setField("RTN_BED" 	, "");	
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;

			//			return szRtnVal = "";
		}
	} // end of searchCoilGdsYdToPosition
	

	/**
	 * [A] 오퍼레이션명 : 코일제품야드 To위치 AutoCrn 두번째 검색 -- 적재용도별 위치검색
	 * hun
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord searchCoilGdsYdToPositionAutoSecond(String sYdCrnSchId, String szStlNo, String szSchCd, String szRouteGp, String szYD_CAR_USE_GP, String szTRN_EQP_CD, String szCAR_NO, String szCARD_NO,String sYD_TO_LOC_DCSN_MTD,String sYD_TO_LOC_GUIDE,String szEqpId) throws JDTOException  {
		 
		YdLocSrchRngDao ydLocSrchRngDao = new YdLocSrchRngDao();
		YdStkBedDao     ydStkBedDao  	= new YdStkBedDao();
		YdStkLyrDao    	ydStkLyrDao 	= new YdStkLyrDao();
		
		int intRtnVal         = 0;
		JDTORecord outRecord1	= JDTORecordFactory.getInstance().create();
		String szMsg			=	"";
		String szMsgList		=	"";
		String szMethodName		=	"searchCoilGdsYdToPositionAutoSecond";
		String szOperationName	=	"코일제품 야드 To위치검색 AutoCrn 2nd 검색";
		JDTORecordSet rsResult  = null;
		JDTORecord recPara 		= null;
		JDTORecord recPara1 		= null;
		JDTORecordSet rsBed     = null;
		JDTORecord recBed       = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsSaveLoc = null;
		JDTORecord   recSaveLoc = null;
		JDTORecord   recSaveLoc1 = null;
		JDTORecord	 inRecord2  = null;
		CoilYdToLocVO coilYdToLocVO = null;
		
		String szSchCoilStat    = null;  //스케줄코일상태
		String szLeftGrade      = null;  //좌측평점
		String szRightGrade     = null;  //좌측평점
		
		
//		String szYdStkColGp     = null;  // 적치열번호
//		String szYdStkBedNo     = null;  // 적치배드번호
//		String szYdStkLyrNo     = null;  // 적치단번호
//		String szYdStkBedUsgGp  = null;  // 적치배드용도구분
		
		String szRtnVal = "";
		String szToPosGrade = "";
		String szGrdColGp = "";
		String szGrdBedNo = "";
		String szGrdLyrNo = "";
		String szToPosGrd = "99";
		String sRTN_BED = "";
		String sMsg = "";
		
		String szYdStkColGp = "";
		String szYdStkBedNo = "";
		String szYdStkLyrNo = "";
		String szLeftColGp 	= "";
		String szLeftBedNo 	= "";
		String szLeftLyrNo 	= "";
		String szLeftStlNo 	= "";
		String szRightColGp = "";
		String szRightBedNo = "";
		String szRightLyrNo = "";
		String szRightStlNo = "";
		
		String sRTN_CD2	= "";
		String sSTL_CNT	= "";
		String sSUM_WGT	= "";
		
		JDTORecord outRecord	= JDTORecordFactory.getInstance().create();
		
		try {
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 AutoCrn TO위치결정 두번째 START★★★★", YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "확인:"+szStlNo+"검색SQL 2nd start", YdConstant.INFO);
			szSchCoilStat = "J";

			
			if(szYD_CAR_USE_GP.equals("L")) {
	        	// 구내운송인 경우(차량사용구분, 운송장비코드)
				recPara = JDTORecordFactory.getInstance().create();
				rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
				recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
				recPara.setField("TRN_EQP_CD",    szTRN_EQP_CD);
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedByCarUseGpandTrnEqpCd*/
				intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsBed, 9);
					
        	}else if (szYD_CAR_USE_GP.equals("G")){
        	// 출하인 경우(차량사용구분, 카드번호, 차량번호)
        		recPara = JDTORecordFactory.getInstance().create();
        		rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
        		recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
        		recPara.setField("CAR_NO",        szCAR_NO);
        		recPara.setField("CARD_NO",       szCARD_NO);
        		/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedByCarUseGpandCarNoCardNo_PIDEV*/
        		intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsBed, 20);
        		
        	}else if ((szSchCd.substring(2, 4).equals("TC")) && (szSchCd.substring(6, 7).equals("U"))){
	    		recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_SCH_CD", szSchCd);
    			recPara.setField("YD_ROUTE_GP", szRouteGp);
    			recPara.setField("STL_NO", szStlNo);
	        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	    /*com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserGdsByToLoc8*/
	        	intRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, rsBed, 501);

        	}else if ((sYD_TO_LOC_DCSN_MTD.equals("F")) && (sYD_TO_LOC_GUIDE.trim().length() == 8) ){
				szMsg = "sYD_TO_LOC_GUIDE(" + sYD_TO_LOC_GUIDE + ")"+ "sYD_TO_LOC_DCSN_MTD(" + sYD_TO_LOC_DCSN_MTD + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	        	recBed.setField("YD_STK_COL_GP", "J"+ sYD_TO_LOC_GUIDE.substring(0,5));
				recBed.setField("YD_STK_BED_NO", sYD_TO_LOC_GUIDE.substring(6,8));
				recBed.setField("YD_STK_LYR_NO", "00"+ sYD_TO_LOC_GUIDE.substring(5,6));
	        	
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal = ydStkLyrDao.getYdStklyr(recBed, rsResult, 0);
				if (intRtnVal < 1) {
					outRecord.setField("RTN_BED" 	, "");	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
				}


				//적치단정보 레코드 추출
				rsResult.first();
				recPara1 = JDTORecordFactory.getInstance().create();
				recPara1 = rsResult.getRecord();
				
				String szYD_STK_LYR_ACT_STAT = ydDaoUtils.paraRecChkNull(recPara1, "YD_STK_LYR_ACT_STAT");

				ydUtils.putLog(szSessionName, szMethodName, "szYD_STK_LYR_ACT_STAT" +szYD_STK_LYR_ACT_STAT , YdConstant.DEBUG);
				
				//적치단 재료상태가 적치가능('E')가 아니면 에러 리턴
				if(!(szYD_STK_LYR_ACT_STAT.equals("E")) && !(szYD_STK_LYR_ACT_STAT.equals("S"))) {
					
					szMsg = "적치단 재료상태(" + szYD_STK_LYR_ACT_STAT + ") 적치가능 상태가 아닙니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					outRecord.setField("RTN_BED" 	, "");	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
				}

				//결로방지재 적치장 처리 
				if(szYD_STK_LYR_ACT_STAT.equals("S")){
					outRecord.setField("RTN_BED" 	, "J"+ sYD_TO_LOC_GUIDE.substring(0,5) + sYD_TO_LOC_GUIDE.substring(6,8) + "00"+ sYD_TO_LOC_GUIDE.substring(5,6));	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
				}else{
					outRecord1 = this.CoilGdsLyrBaseCheck(szStlNo, recBed);
				}
				
	        	String sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				szYdStkColGp = StringHelper.evl(outRecord1.getFieldString("RTN_COLGP"), "");
				szYdStkBedNo = StringHelper.evl(outRecord1.getFieldString("RTN_BEDNO"), "");
				szYdStkLyrNo = StringHelper.evl(outRecord1.getFieldString("RTN_LYRNO"), "");
				String sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				
				if ("1".equals(sRTN_CD)) {
					outRecord.setField("RTN_BED" 	, szYdStkColGp + szYdStkBedNo + szYdStkLyrNo);	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
		   						
				} else {
					
//LOG_TABLE    					
	    			EJBConnector ejbConn = null;
	    			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
	    			recLog.setField("STL_NO"			, szStlNo);
	    			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
	    			recLog.setField("YD_GP"				, "J");
	    			recLog.setField("YD_SCH_CD"			, szSchCd);
	    			recLog.setField("YD_USER_ID"		, "log");
	    			recLog.setField("MSG"				, sRTN_MSG);
	    			
	    			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
	    			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
					
					outRecord.setField("RTN_BED" 	, "");	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
				}	
        	

        	}else if ((sYD_TO_LOC_DCSN_MTD.equals("F")) && (sYD_TO_LOC_GUIDE.trim().length() == 6) ){
				szMsg = "sYD_TO_LOC_GUIDE(" + sYD_TO_LOC_GUIDE + ")"+ "sYD_TO_LOC_DCSN_MTD(" + sYD_TO_LOC_DCSN_MTD + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
					//사용자 지정 BED 검색:6
	    		recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", szStlNo); 
	        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	    /*com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserGdsByToLoc6*/
	        	intRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, rsBed, 500);

			} else{
    				//위치검색베드로 to위치를 결정하는 경우
            		recPara = JDTORecordFactory.getInstance().create();
        			recPara.setField("YD_SCH_CD", szSchCd);
        			recPara.setField("YD_ROUTE_GP", szRouteGp);
        			recPara.setField("STL_NO", szStlNo);
        			recPara.setField("YD_EQP_ID", szEqpId);
                	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
                	// 일반적이 검색 BED임 
                	/*com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngBySchCdandStkBedUsg*/
                	intRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, rsBed, 601);
        	}
			
        	if (intRtnVal < 0) {
				szMsg = "재료번호(" + szStlNo + ")로 위치검색배드 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return szRtnVal = YdConstant.RETN_CD_FAILURE;
				outRecord.setField("RTN_BED" 	, "");	
				outRecord.setField("RTN_CD" 	, "1");	 //RTN_MSG
				outRecord.setField("RTN_MSG" 	, "위치검색순서조건이 이상합니다.<br>" + sMsg );	 //RTN_MSG
				return outRecord;

        	}
        	
        	if (intRtnVal == 0) {

				outRecord.setField("RTN_BED" 	, "");	
				outRecord.setField("RTN_CD" 	, "1");	
				outRecord.setField("RTN_MSG" 	, "위치검색순서조건이 이상합니다.<br>" + sMsg );	 //RTN_MSG
				
				return outRecord;
        		
//        		return szRtnVal;
        	}
        	
        	rsSaveLoc = JDTORecordFactory.getInstance().createRecordSet("Temp");
        	ArrayList listSaveGrade = new ArrayList();
        	
        	rsBed.first();
        	
        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 적치대상제 검색 START★★★★★", YdConstant.INFO);
        	
			for (int nLoop =0 ; nLoop<rsBed.size(); nLoop++ ){
				
				// 검색배드 정보를 Setting
				recBed =JDTORecordFactory.getInstance().create();
				recBed = rsBed.getRecord(nLoop);
				
				String szYdStkColGpCHK = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_COL_GP");
				String szYdStkBedNoCHK = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_BED_NO");
				String szYdStkLyrNoCHK = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_LYR_NO");

				ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 검색>>"+nLoop+"★★★★★"+szYdStkColGpCHK+szYdStkBedNoCHK+szYdStkLyrNoCHK+">>"+szStlNo, YdConstant.INFO);

				// BaseCheck 해당 코일이 해당Bed에 적치 가능유무 판단...
				outRecord1 = this.CoilGdsLyrBaseCheck(szStlNo, recBed);
				String sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				String sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				if ("-1".equals(sRTN_CD)) {
					/*
					recSaveLoc = JDTORecordFactory.getInstance().create();
		        	recSaveLoc.setField("YD_STK_COL_GP", szYdStkColGpCHK);
		        	recSaveLoc.setField("YD_STK_BED_NO", szYdStkBedNoCHK);
		        	recSaveLoc.setField("YD_STK_LYR_NO", szYdStkLyrNoCHK);
		        	recSaveLoc.setField("TO_POS_GRADE" , "999");
		        	recSaveLoc.setField("RTN_MSG" , sRTN_MSG);
		        	ydUtils.displayRecord(szOperationName, recSaveLoc);
		        	
		        	rsSaveLoc.addRecord(recSaveLoc);
		        	*/
//		        	150909 hun list에 VO들 담아서 소팅
		        	coilYdToLocVO = new CoilYdToLocVO();
		        	coilYdToLocVO.setYdStkColGp(szYdStkColGpCHK);
		        	coilYdToLocVO.setYdStkBedNo(szYdStkBedNoCHK);
		        	coilYdToLocVO.setYdStkLyrNo(szYdStkLyrNoCHK);
		        	coilYdToLocVO.setToPosGrade(999 );
		        	
		        	listSaveGrade.add(coilYdToLocVO);

					continue;
				}	
				if ("0".equals(sRTN_CD)) {
					outRecord.setField("RTN_BED" 	, "");	
					outRecord.setField("RTN_CD" 	, "1");	

//					return sRTN_BED = "";
				}	
				szYdStkColGp = StringHelper.evl(outRecord1.getFieldString("RTN_COLGP"), "");
				szYdStkBedNo = StringHelper.evl(outRecord1.getFieldString("RTN_BEDNO"), "");
				szYdStkLyrNo = StringHelper.evl(outRecord1.getFieldString("RTN_LYRNO"), "");
				szLeftColGp  = StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_COLGP"), "");
				szLeftBedNo  = StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_BEDNO"), "");
				szLeftLyrNo  = StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_LYRNO"), "");
				szLeftStlNo  = StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_STLNO"), "");
				szRightColGp = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_COLGP"), "");
				szRightBedNo = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_BEDNO"), "");
				szRightLyrNo = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_LYRNO"), "");
				szRightStlNo = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_STLNO"), "");
				
				//대차는 평점 check 하지 않는다
//	        	if(szYdStkColGp.substring(2, 4).equals("TC")) {
				if((szYdStkColGp.substring(2, 3).equals("0"))||
						(szYdStkColGp.substring(2, 3).equals("1"))||
						(szYdStkColGp.substring(2, 3).equals("2"))||
						(szYdStkColGp.substring(2, 3).equals("3"))||
						(szYdStkColGp.substring(2, 3).equals("4"))||
						(szYdStkColGp.substring(2, 3).equals("5"))||
						(szYdStkColGp.substring(2, 3).equals("6"))) {
	        	} else {
	        		szRtnVal = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
	        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품 대차 중량체크 로직=>>>>"+szRtnVal, YdConstant.INFO);
	 					inRecord2 = JDTORecordFactory.getInstance().create();
	 					JDTORecord 		outRecord4 = JDTORecordFactory.getInstance().create();
	 					
 	 					inRecord2.setField("YD_SCH_CD"		, szSchCd);//스케줄코드
 	 					inRecord2.setField("YD_CRN_SCH_ID"	, sYdCrnSchId);  //
 	 					EJBConnector  ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
 	 					outRecord4 = (JDTORecord)ejbConn.trx("getStkColTCarUpChk2", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
 	 					
 	 					sRTN_CD2	= StringHelper.evl(outRecord4.getFieldString("RTN_CD"), "0");
 	 					sSTL_CNT	= StringHelper.evl(outRecord4.getFieldString("STL_CNT"), "0");
 	 					sSUM_WGT	= StringHelper.evl(outRecord4.getFieldString("SUM_WGT"), "0");
 	 					
 	 					if ("0".equals(sRTN_CD2)) {
 	 						szMsg = "상차 가능 CHECK시  ERROR";
 	 						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 	 					}	
 	 					
 	 					double dSTL_CNT = Integer.parseInt(sSTL_CNT);
 	 					double dSUM_WGT = Integer.parseInt(sSUM_WGT);
 	 					if (dSTL_CNT <= 3) {
 	 						if (dSUM_WGT > YdConstant.YD_COIL_TC_WEIGH_MAX) {
	 	 						szMsg = "중량초 오버로 대차상차 불가능 (대차 자동 출발 )";
	 	 						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	 	 						
	 	 						YdTcarFtmvMtlDao ydTcarftmvmtlDao = new YdTcarFtmvMtlDao();
	 	 						JDTORecordSet    outRecSet  = null;
	 	 						recPara = JDTORecordFactory.getInstance().create();				
	 	 			    		recPara.setField("YD_EQP_ID", szSchCd);
	 	 			    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp"); 
	 	 			    		/*com.inisteel.cim.yd.dao.ydtcarftmvmtldao.YdTcarftmvmtlDao.getYdStockTcarSchCHK*/
	 	 			    		int nMtlCnt   = ydTcarftmvmtlDao.getYdTcarftmvmtl(recPara, outRecSet,303);

	 	 			    		if (nMtlCnt > 0 ) {	 	
		 	 						
		 	 			    		JDTORecord[] inRecord  =  null;
		 	 			    		inRecord = new JDTORecord[1];
		 	 			    		
		 	 			    		inRecord[0] = JDTORecordFactory.getInstance().create();
		 	 			    		inRecord[0].setRecord(outRecSet.getRecord(0));
		 	 			    		inRecord[0].setField("YD_CRN_SCH_ID"	, sYdCrnSchId);
		 	 			    		inRecord[0].setField("YD_SCH_CD"	, szSchCd);

		 	 						//대차 출발처리 
		 	 						EJBConnector  ejbConnT = new EJBConnector("default", "CoilGdsJspSeEJB", this);
		 	 	 					ejbConnT.trx("CoilYdTcarStsSetTcarD", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

	 	 			    		}
	 	 			    		/*
	 	 			    		recSaveLoc = JDTORecordFactory.getInstance().create();
	 	 			        	recSaveLoc.setField("YD_STK_COL_GP", szYdStkColGpCHK);
	 	 			        	recSaveLoc.setField("YD_STK_BED_NO", szYdStkBedNoCHK);
	 	 			        	recSaveLoc.setField("YD_STK_LYR_NO", szYdStkLyrNoCHK);
	 	 			        	recSaveLoc.setField("TO_POS_GRADE" , "999");
	 	 			        	recSaveLoc.setField("RTN_MSG" , szMsg);
	 	 			        	ydUtils.displayRecord(szOperationName, recSaveLoc);
	 	 			        	
	 	 			        	rsSaveLoc.addRecord(recSaveLoc);
	 	 			        	*/
//	 	 			        	150909 hun list에 VO들 담아서 소팅
	 	 			        	coilYdToLocVO = new CoilYdToLocVO();
	 	 			        	coilYdToLocVO.setYdStkColGp(szYdStkColGpCHK);
	 	 			        	coilYdToLocVO.setYdStkBedNo(szYdStkBedNoCHK);
	 	 			        	coilYdToLocVO.setYdStkLyrNo(szYdStkLyrNoCHK);
	 	 			        	coilYdToLocVO.setToPosGrade(999 );
	 	 			        	
	 	 			        	listSaveGrade.add(coilYdToLocVO);

	 	 						continue;   			    				
 	 						}
 	 					}
	        		
	        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품설비 TO위치=>>>>"+szRtnVal, YdConstant.INFO);
					outRecord.setField("RTN_BED" 	, szRtnVal);	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
//	        		return szRtnVal;
	        	}
	        	
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 1단 좌측 코일 To위치평점항목  체크Set  START★★★★★", YdConstant.INFO);	        	
	        	// 1단 좌측 코일 To위치평점항목 Set.
	        	szLeftGrade = SearchCoilGdsYdLeftCoilGrade(szYdStkColGp, szYdStkBedNo, szYdStkLyrNo, szStlNo, szLeftColGp, szLeftBedNo, szLeftLyrNo, szLeftStlNo);
	        	
	        	if (szLeftGrade.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"좌측적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 재료번호[" + szLeftStlNo + "]의 평점항목Set에 오류가 있습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					/*
					recSaveLoc = JDTORecordFactory.getInstance().create();
		        	recSaveLoc.setField("YD_STK_COL_GP", szYdStkColGpCHK);
		        	recSaveLoc.setField("YD_STK_BED_NO", szYdStkBedNoCHK);
		        	recSaveLoc.setField("YD_STK_LYR_NO", szYdStkLyrNoCHK);
		        	recSaveLoc.setField("TO_POS_GRADE" , "999");
		        	recSaveLoc.setField("RTN_MSG" , szMsg);
		        	ydUtils.displayRecord(szOperationName, recSaveLoc);
		        	
		        	rsSaveLoc.addRecord(recSaveLoc);
		        	*/
//		        	150909 hun list에 VO들 담아서 소팅
		        	coilYdToLocVO = new CoilYdToLocVO();
		        	coilYdToLocVO.setYdStkColGp(szYdStkColGpCHK);
		        	coilYdToLocVO.setYdStkBedNo(szYdStkBedNoCHK);
		        	coilYdToLocVO.setYdStkLyrNo(szYdStkLyrNoCHK);
		        	coilYdToLocVO.setToPosGrade(999 );
		        	
		        	listSaveGrade.add(coilYdToLocVO);

					continue;
        		}else {
        			szMsg = "좌측적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 재료번호[" + szLeftStlNo + "]의 평점항목Set[" + szLeftGrade + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		}
	        	
	        	// 1단 우측 코일 To위치평점항목 Set.
	        	szRightGrade = SearchCoilGdsYdRightCoilGrade(szYdStkColGp, szYdStkBedNo, szYdStkLyrNo, szStlNo, szRightColGp, szRightBedNo, szRightLyrNo, szRightStlNo);
	        	
	        	if (szLeftGrade.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"우측적치단(" + szRightColGp + szRightBedNo + szRightLyrNo + ")의 재료번호[" + szRightStlNo + "]의 평점항목Set에 오류가 있습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					/*
					recSaveLoc = JDTORecordFactory.getInstance().create();
		        	recSaveLoc.setField("YD_STK_COL_GP", szYdStkColGpCHK);
		        	recSaveLoc.setField("YD_STK_BED_NO", szYdStkBedNoCHK);
		        	recSaveLoc.setField("YD_STK_LYR_NO", szYdStkLyrNoCHK);
		        	recSaveLoc.setField("TO_POS_GRADE" , "999");
		        	recSaveLoc.setField("RTN_MSG" , szMsg);
		        	ydUtils.displayRecord(szOperationName, recSaveLoc);
		        	
		        	rsSaveLoc.addRecord(recSaveLoc);
		        	*/
//		        	150909 hun list에 VO들 담아서 소팅
		        	coilYdToLocVO = new CoilYdToLocVO();
		        	coilYdToLocVO.setYdStkColGp(szYdStkColGpCHK);
		        	coilYdToLocVO.setYdStkBedNo(szYdStkBedNoCHK);
		        	coilYdToLocVO.setYdStkLyrNo(szYdStkLyrNoCHK);
		        	coilYdToLocVO.setToPosGrade(999 );
		        	
		        	listSaveGrade.add(coilYdToLocVO);

		        	
					continue;
        		}else {
        			szMsg = "우측적치단(" + szRightColGp + szRightBedNo + szRightLyrNo + ")의 재료번호[" + szRightStlNo + "]의 평점항목Set[" + szRightGrade + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		}
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 1단 좌측 코일 To위치평점항목 체크Set  END★★★★★", YdConstant.INFO);
	        	
	        	
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★", YdConstant.INFO);
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 To위치 평점 계산  START★★★★★", YdConstant.INFO);
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★", YdConstant.INFO);
	        	
	        	// To위치 평점항목 Set
	        	szToPosGrade = getToPosGrade(szSchCoilStat, szYdStkLyrNo, szLeftGrade, szRightGrade);
	        	
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일야드 BRE: 스케줄코일상태("+szSchCoilStat+") TO위치적치단("+szYdStkLyrNo+") 좌측1단코일상태("+szLeftGrade+") 우측1단코일상태("+szRightGrade+")", YdConstant.INFO);	    		
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치 평점결과:loop순번:"+(nLoop+1)+" 재료번호(" + szStlNo + ")("+szYdStkColGp + szYdStkBedNo + szYdStkLyrNo+") ▶▶▶▶"+szToPosGrade+"점◀◀◀◀", YdConstant.INFO);
	        	
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★", YdConstant.INFO);
	        	
	        	// 평점이 1순위면 바로 리턴한다.
	        	if ("1".equals(szToPosGrade)) {
	        		szRtnVal = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
	        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치=>>>>"+szRtnVal, YdConstant.INFO);

//LOG_TABLE    					
	    			EJBConnector ejbConn = null;
	    			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
	    			recLog.setField("STL_NO"			, szStlNo);
	    			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
	    			recLog.setField("YD_GP"				, "J");
	    			recLog.setField("YD_SCH_CD"			, szSchCd);
	    			recLog.setField("YD_USER_ID"		, "log");
	    			recLog.setField("MSG"				, "★★★★★C열연코일제품야드 TO위치=>>>>"+szRtnVal + ":평점 1점");
	    			
	    			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
	    			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
	        		
	        		
	        		outRecord.setField("RTN_BED" 	, szRtnVal);	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;

//	        		return szRtnVal;
	        	}
	        	 
	        	/*
	        	 * TO위치 대상건이 50건이 넘는 경우 
	        	 * 평점대상 기준을 30점 이하로 조정 한다.
	        	 */
	        	int toPosGrade = Integer.parseInt(szToPosGrade);
	        	
	        	if (nLoop>=50 && toPosGrade <= 30) {
	        		szRtnVal = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
	        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치3=>>>>"+szRtnVal, YdConstant.INFO);

//LOG_TABLE    					
	    			EJBConnector ejbConn = null;
	    			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
	    			recLog.setField("STL_NO"			, szStlNo);
	    			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
	    			recLog.setField("YD_GP"				, "J");
	    			recLog.setField("YD_SCH_CD"			, szSchCd);
	    			recLog.setField("YD_USER_ID"		, "log");
	    			recLog.setField("MSG"				, "★★★★★C열연코일제품야드 TO위치3=>>>>"+szRtnVal + ":50회 이상 평점 30점 이하:" +szToPosGrade+"점에서 결정됨");
	    			
	    			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
	    			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
	        		
	        		
	        		outRecord.setField("RTN_BED" 	, szRtnVal);	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord; 
	        	}
	        	
//	        	150909 hun list에 VO들 담아서 소팅
	        	coilYdToLocVO = new CoilYdToLocVO();
	        	coilYdToLocVO.setYdStkColGp(szYdStkColGp);
	        	coilYdToLocVO.setYdStkBedNo(szYdStkBedNo);
	        	coilYdToLocVO.setYdStkLyrNo(szYdStkLyrNo);
	        	coilYdToLocVO.setToPosGrade(Integer.parseInt(StringHelper.evl(szToPosGrade , "0") ) );
	        	
	        	listSaveGrade.add(coilYdToLocVO);

				
			} //end of for	
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 적치대상제 검색 END★★★★★", YdConstant.INFO);
			
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★1C열연코일제품야드 평점 1점이 아닌경우 대상 찾기 START★★★★★", YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "C열연코일제품야드 "+ szStlNo + "평점리스트", YdConstant.INFO);
			
			/*
			if (rsSaveLoc.size() > 0) {
				for(int nLoop =0 ; nLoop<rsSaveLoc.size(); nLoop++ ) {
					rsSaveLoc.absolute(nLoop);
					recSaveLoc1 = JDTORecordFactory.getInstance().create();
					recSaveLoc1 = rsSaveLoc.getRecord(nLoop);
					
					String szGrdColGp1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "YD_STK_COL_GP");
					String szGrdBedNo1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "YD_STK_BED_NO");
					String szGrdLyrNo1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "YD_STK_LYR_NO");
					String szToPosGrd1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "TO_POS_GRADE");
					String szLToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc1, "TO_LEFT_GRADE");
					String szRToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc1, "TO_RIGTH_GRADE");
					String szRTN_MSG   =  ydDaoUtils.paraRecChkNull(recSaveLoc1, "RTN_MSG");
					
					szMsg ="[" + szStlNo + "]"+"["+ nLoop+"]" + szGrdColGp1 + szGrdBedNo1 + szGrdLyrNo1 +"<<Score="+szToPosGrd1+"(" + "L:"+szLToPosGrd+ "|R:"+szRToPosGrd+")>>,"+szRTN_MSG+"\r\n";
					szMsgList = szMsgList + szMsg;
					
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				} // end of for
			}		
			*/

			szMsg ="[ 평점 sort arrayList ] size="+listSaveGrade.size()+"\r\n";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

//			151015 hun 동점경우 2단 우선 배치를 위해 평점 뒤에 적치단 정보 입력 ( 002-> 1점, 001->2점, ""->3점 )
			if(listSaveGrade.size()>0){
				for(int nLoop =0 ; nLoop<listSaveGrade.size(); nLoop++ ) {
					coilYdToLocVO = (CoilYdToLocVO)listSaveGrade.get(nLoop);
					if("001".equals( coilYdToLocVO.getYdStkLyrNo()) ){
						coilYdToLocVO.setYdStkLyrGrade( Integer.parseInt( coilYdToLocVO.getToPosGrade() + "2"));
					}else if("002".equals( coilYdToLocVO.getYdStkLyrNo()) ){
						coilYdToLocVO.setYdStkLyrGrade(Integer.parseInt( coilYdToLocVO.getToPosGrade() + "1"));
					}else {
						coilYdToLocVO.setYdStkLyrGrade(Integer.parseInt( coilYdToLocVO.getToPosGrade() + "3"));
					}
				}
			}
//			150908 hun 평점을 arrayList에 담아서 소트
			if(listSaveGrade.size()>0){
				Collections.sort(listSaveGrade, new CoilYdToLayComparator());
			}
			
			if (listSaveGrade.size() > 0) {
				for(int nLoop =0 ; nLoop<listSaveGrade.size(); nLoop++ ) {
					coilYdToLocVO = (CoilYdToLocVO)listSaveGrade.get(nLoop);
					szMsg ="sort Grade[" + szStlNo + "]"+nLoop+",ColGp="+ coilYdToLocVO.getYdStkColGp() + coilYdToLocVO.getYdStkBedNo() + coilYdToLocVO.getYdStkLyrNo() +
					"<<Score="+coilYdToLocVO.getToPosGrade()+">>\r\n";
					szMsgList = szMsgList + szMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				} // end of for
			}	

//LOG_TABLE    					
			EJBConnector ejbConn = null;
			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
			recLog.setField("STL_NO"			, szStlNo);
			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
			recLog.setField("YD_GP"				, "J");
			recLog.setField("YD_SCH_CD"			, szSchCd);
			recLog.setField("YD_USER_ID"		, "log");
			//recLog.setField("MSG"				, szMsgList.substring(0, 999));
			recLog.setField("MSG"				, szMsgList);
			
			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
			
			/*
			if (rsSaveLoc.size() > 0) {
				
				for(int nLoop =0 ; nLoop<rsSaveLoc.size(); nLoop++ ) {
					rsSaveLoc.absolute(nLoop);
					recSaveLoc = JDTORecordFactory.getInstance().create();
					recSaveLoc = rsSaveLoc.getRecord(nLoop);
					
					if(nLoop ==0){							 
						szToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc, "TO_POS_GRADE");
						
						if(!szToPosGrd.equals("999")){
							szGrdColGp = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_COL_GP");
							szGrdBedNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_BED_NO");
							szGrdLyrNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_LYR_NO");
						} 
					}
						if (Integer.parseInt(szToPosGrd) > Integer.parseInt(ydDaoUtils.paraRecChkNull(recSaveLoc, "TO_POS_GRADE"))) {
							szGrdColGp = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_COL_GP");
							szGrdBedNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_BED_NO");
							szGrdLyrNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_LYR_NO");
							szToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc, "TO_POS_GRADE");
						}
				} // end of for
				*/
			if (listSaveGrade.size() > 0) {
				for(int nLoop =0 ; nLoop<listSaveGrade.size(); nLoop++ ) {
					coilYdToLocVO = (CoilYdToLocVO)listSaveGrade.get(nLoop);
					
					szGrdColGp = coilYdToLocVO.getYdStkColGp();
					szGrdBedNo = coilYdToLocVO.getYdStkBedNo();
					szGrdLyrNo = coilYdToLocVO.getYdStkLyrNo();
					szToPosGrd = String.valueOf(coilYdToLocVO.getToPosGrade());
					
//					999 점 pass
					if(coilYdToLocVO.getToPosGrade()==999){
						continue;
					}
					
					//마지막 체크 To위치에 코일이 있는지 확인
		        	ydUtils.putLog(szSessionName, szMethodName, "===================선택된 저장위치가 공위치인지 2nd CHECK====================", YdConstant.INFO);
					
		        	recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", szGrdColGp);
					recPara.setField("YD_STK_BED_NO", szGrdBedNo);
					recPara.setField("YD_STK_LYR_NO", szGrdLyrNo);
					
		        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
		        	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyr*/
		        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBed, 0);
		        			
		        	if (intRtnVal > 0) {
		        		rsBed.first();
		    			recBed =JDTORecordFactory.getInstance().create();
		    			recBed = rsBed.getRecord();
		    			String sYD_STK_LYR_MTL_STAT = recBed.getFieldString("YD_STK_LYR_MTL_STAT");
		    			
		    			if(!sYD_STK_LYR_MTL_STAT.equals("E")){
		    	   			szMsg = "확인:"+szStlNo+"적치단(" + szGrdColGp + szGrdBedNo + szGrdLyrNo + ") 선택된 To위치에 coil 발견 CHECK ERROR.";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
			        		continue;
		    			}else{
					
			        		szRtnVal = szGrdColGp + szGrdBedNo + szGrdLyrNo;
			        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 2nd TO위치=>>>>"+szRtnVal, YdConstant.INFO);
			        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 평점 1점이 아닌경우 대상 찾기 END★★★★★", YdConstant.INFO);
			    			outRecord.setField("RTN_BED" 		, szRtnVal);	
			    			outRecord.setField("RTN_CD" 		, "1");	
			    			
			    			return outRecord;
		    			}
		        	}
					
				}
				
				ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 2nd TO위치=>>>>"+szRtnVal, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 평점 1점이 아닌경우 대상 찾기 END★★★★★", YdConstant.INFO);

			} else {
				szRtnVal = "";
			}				
			
			
			
			ydUtils.putLog(szSessionName, szMethodName, szStlNo+"2nd end", YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치결정 END★★★★★", YdConstant.INFO);
			
			outRecord.setField("RTN_BED" 		, szRtnVal);	
			outRecord.setField("RTN_CD" 		, "1");	
			
			return outRecord;

//			return szRtnVal;
		} catch(Exception e) {
			szMsg = "코일창고야드 To위치검색 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치결정 ERROR★★★★★", YdConstant.INFO);
			outRecord.setField("RTN_BED" 	, "");	
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;

			//			return szRtnVal = "";
		}
	} // end of searchCoilGdsYdToPositionAutoSecond
	

	/**
	 * [A] 오퍼레이션명 : 코일제품야드 To위치 AutoCrn 세번째 검색 - 전체동 대상
	 * hun
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord searchCoilGdsYdToPositionAutoThird(String sYdCrnSchId, String szStlNo, String szSchCd, String szRouteGp, String szYD_CAR_USE_GP, String szTRN_EQP_CD, String szCAR_NO, String szCARD_NO,String sYD_TO_LOC_DCSN_MTD,String sYD_TO_LOC_GUIDE,String szEqpId) throws JDTOException  {
		 
		YdLocSrchRngDao ydLocSrchRngDao = new YdLocSrchRngDao();
		YdStkBedDao     ydStkBedDao  	= new YdStkBedDao();
		YdStkLyrDao    	ydStkLyrDao 	= new YdStkLyrDao();
		
		int intRtnVal         = 0;
		JDTORecord outRecord1	= JDTORecordFactory.getInstance().create();
		String szMsg			=	"";
		String szMsgList		=	"";
		String szMethodName		=	"searchCoilGdsYdToPositionAutoThird";
		String szOperationName	=	"코일제품 야드 To위치검색 AutoCrn 3rd 검색";
		JDTORecordSet rsResult  = null;
		JDTORecord recPara 		= null;
		JDTORecord recPara1 		= null;
		JDTORecordSet rsBed     = null;
		JDTORecord recBed       = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsSaveLoc = null;
		JDTORecord   recSaveLoc = null;
		JDTORecord   recSaveLoc1 = null;
		JDTORecord	 inRecord2  = null;
		
		String szSchCoilStat    = null;  //스케줄코일상태
		String szLeftGrade      = null;  //좌측평점
		String szRightGrade     = null;  //좌측평점
		
		CoilYdToLocVO coilYdToLocVO = null;
		
//		String szYdStkColGp     = null;  // 적치열번호
//		String szYdStkBedNo     = null;  // 적치배드번호
//		String szYdStkLyrNo     = null;  // 적치단번호
//		String szYdStkBedUsgGp  = null;  // 적치배드용도구분
		
		String szRtnVal = "";
		String szToPosGrade = "";
		String szGrdColGp = "";
		String szGrdBedNo = "";
		String szGrdLyrNo = "";
		String szToPosGrd = "99";
		String sRTN_BED = "";
		String sMsg = "";
		
		String szYdStkColGpCHK = "";
		String szYdStkBedNoCHK = "";
		String szYdStkLyrNoCHK = "";
		
		String sRTN_CD		= "";
		String sRTN_MSG		= "";
		
		String szYdStkColGp = "";
		String szYdStkBedNo = "";
		String szYdStkLyrNo = "";
		String szLeftColGp 	= "";
		String szLeftBedNo 	= "";
		String szLeftLyrNo 	= "";
		String szLeftStlNo 	= "";
		String szRightColGp = "";
		String szRightBedNo = "";
		String szRightLyrNo = "";
		String szRightStlNo = "";
		
		JDTORecord outRecord	= JDTORecordFactory.getInstance().create();
		
		try {
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 AutoCrn TO위치결정 3rd START★★★★", YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "확인:"+szStlNo+"검색SQL 3rd start", YdConstant.INFO);
			szSchCoilStat = "J";

			
			if(szYD_CAR_USE_GP.equals("L")) {
	        	// 구내운송인 경우(차량사용구분, 운송장비코드)
				recPara = JDTORecordFactory.getInstance().create();
				rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
				recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
				recPara.setField("TRN_EQP_CD",    szTRN_EQP_CD);
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedByCarUseGpandTrnEqpCd*/
				intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsBed, 9);
					
        	}else if (szYD_CAR_USE_GP.equals("G")){
        	// 출하인 경우(차량사용구분, 카드번호, 차량번호)
        		recPara = JDTORecordFactory.getInstance().create();
        		rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
        		recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
        		recPara.setField("CAR_NO",        szCAR_NO);
        		recPara.setField("CARD_NO",       szCARD_NO);
        		/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedByCarUseGpandCarNoCardNo_PIDEV*/
        		intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsBed, 20);
        		
        	}else if ((szSchCd.substring(2, 4).equals("TC")) && (szSchCd.substring(6, 7).equals("U"))){
	    		recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_SCH_CD", szSchCd);
    			recPara.setField("YD_ROUTE_GP", szRouteGp);
    			recPara.setField("STL_NO", szStlNo);
	        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	    /*com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserGdsByToLoc8*/
	        	intRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, rsBed, 501);

        	}else if ((sYD_TO_LOC_DCSN_MTD.equals("F")) && (sYD_TO_LOC_GUIDE.trim().length() == 8) ){
				szMsg = "sYD_TO_LOC_GUIDE(" + sYD_TO_LOC_GUIDE + ")"+ "sYD_TO_LOC_DCSN_MTD(" + sYD_TO_LOC_DCSN_MTD + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	        	recBed.setField("YD_STK_COL_GP", "J"+ sYD_TO_LOC_GUIDE.substring(0,5));
				recBed.setField("YD_STK_BED_NO", sYD_TO_LOC_GUIDE.substring(6,8));
				recBed.setField("YD_STK_LYR_NO", "00"+ sYD_TO_LOC_GUIDE.substring(5,6));
	        	
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal = ydStkLyrDao.getYdStklyr(recBed, rsResult, 0);
				if (intRtnVal < 1) {
					outRecord.setField("RTN_BED" 	, "");	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
				}


				//적치단정보 레코드 추출
				rsResult.first();
				recPara1 = JDTORecordFactory.getInstance().create();
				recPara1 = rsResult.getRecord();
				
				String szYD_STK_LYR_ACT_STAT = ydDaoUtils.paraRecChkNull(recPara1, "YD_STK_LYR_ACT_STAT");

				ydUtils.putLog(szSessionName, szMethodName, "szYD_STK_LYR_ACT_STAT" +szYD_STK_LYR_ACT_STAT , YdConstant.DEBUG);
				
				//적치단 재료상태가 적치가능('E')가 아니면 에러 리턴
				if(!(szYD_STK_LYR_ACT_STAT.equals("E")) && !(szYD_STK_LYR_ACT_STAT.equals("S"))) {
					
					szMsg = "적치단 재료상태(" + szYD_STK_LYR_ACT_STAT + ") 적치가능 상태가 아닙니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					outRecord.setField("RTN_BED" 	, "");	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
				}

				//결로방지재 적치장 처리 
				if(szYD_STK_LYR_ACT_STAT.equals("S")){
					outRecord.setField("RTN_BED" 	, "J"+ sYD_TO_LOC_GUIDE.substring(0,5) + sYD_TO_LOC_GUIDE.substring(6,8) + "00"+ sYD_TO_LOC_GUIDE.substring(5,6));	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
				}else{
					outRecord1 = this.CoilGdsLyrBaseCheck(szStlNo, recBed);
				}
				
	        	sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				szYdStkColGp = StringHelper.evl(outRecord1.getFieldString("RTN_COLGP"), "");
				szYdStkBedNo = StringHelper.evl(outRecord1.getFieldString("RTN_BEDNO"), "");
				szYdStkLyrNo = StringHelper.evl(outRecord1.getFieldString("RTN_LYRNO"), "");
				sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				
				if ("1".equals(sRTN_CD)) {
					outRecord.setField("RTN_BED" 	, szYdStkColGp + szYdStkBedNo + szYdStkLyrNo);	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
		   						
				} else {
					
//LOG_TABLE    					
	    			EJBConnector ejbConn = null;
	    			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
	    			recLog.setField("STL_NO"			, szStlNo);
	    			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
	    			recLog.setField("YD_GP"				, "J");
	    			recLog.setField("YD_SCH_CD"			, szSchCd);
	    			recLog.setField("YD_USER_ID"		, "log");
	    			recLog.setField("MSG"				, sRTN_MSG);
	    			
	    			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
	    			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
					
					outRecord.setField("RTN_BED" 	, "");	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
				}	
        	

        	}else if ((sYD_TO_LOC_DCSN_MTD.equals("F")) && (sYD_TO_LOC_GUIDE.trim().length() == 6) ){
				szMsg = "sYD_TO_LOC_GUIDE(" + sYD_TO_LOC_GUIDE + ")"+ "sYD_TO_LOC_DCSN_MTD(" + sYD_TO_LOC_DCSN_MTD + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
					//사용자 지정 BED 검색:6
	    		recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", szStlNo); 
	        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	    /*com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserGdsByToLoc6*/
	        	intRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, rsBed, 500);

			} else{
    				//위치검색베드로 to위치를 결정하는 경우
            		recPara = JDTORecordFactory.getInstance().create();
        			recPara.setField("YD_SCH_CD", szSchCd);
        			recPara.setField("YD_ROUTE_GP", szRouteGp);
        			recPara.setField("STL_NO", szStlNo);
        			recPara.setField("YD_EQP_ID", szEqpId);
                	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
                	// 일반적이 검색 BED임 
                	/*com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngBySchCdandStkAllBed*/
                	intRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, rsBed, 602);
        	}
			
        	if (intRtnVal < 0) {
				szMsg = "재료번호(" + szStlNo + ")로 위치검색배드 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return szRtnVal = YdConstant.RETN_CD_FAILURE;
				outRecord.setField("RTN_BED" 	, "");	
				outRecord.setField("RTN_CD" 	, "1");	 //RTN_MSG
				outRecord.setField("RTN_MSG" 	, "위치검색순서조건이 이상합니다.<br>" + sMsg );	 //RTN_MSG
				return outRecord;

        	}
        	
        	if (intRtnVal == 0) {

				outRecord.setField("RTN_BED" 	, "");	
				outRecord.setField("RTN_CD" 	, "1");	
				outRecord.setField("RTN_MSG" 	, "위치검색순서조건이 이상합니다.<br>" + sMsg );	 //RTN_MSG
				
				return outRecord;
        		
//        		return szRtnVal;
        	}
        	
        	rsSaveLoc = JDTORecordFactory.getInstance().createRecordSet("Temp");
        	ArrayList listSaveGrade = new ArrayList();
        	rsBed.first();
        	
        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 적치대상제 검색 START★★★★★", YdConstant.INFO);
        	
			for (int nLoop =0 ; nLoop<rsBed.size(); nLoop++ ){
				
				// 검색배드 정보를 Setting
				recBed =JDTORecordFactory.getInstance().create();
				recBed = rsBed.getRecord(nLoop);
				
				szYdStkColGpCHK = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_COL_GP");
				szYdStkBedNoCHK = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_BED_NO");
				szYdStkLyrNoCHK = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_LYR_NO");

				ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 검색>>"+nLoop+"★★★★★"+szYdStkColGpCHK+szYdStkBedNoCHK+szYdStkLyrNoCHK+">>"+szStlNo, YdConstant.INFO);

				// BaseCheck 해당 코일이 해당Bed에 적치 가능유무 판단...
				outRecord1 = this.CoilGdsLyrBaseCheck(szStlNo, recBed);
				sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				if ("-1".equals(sRTN_CD)) {
					
					recSaveLoc = JDTORecordFactory.getInstance().create();
		        	recSaveLoc.setField("YD_STK_COL_GP", szYdStkColGpCHK);
		        	recSaveLoc.setField("YD_STK_BED_NO", szYdStkBedNoCHK);
		        	recSaveLoc.setField("YD_STK_LYR_NO", szYdStkLyrNoCHK);
		        	recSaveLoc.setField("TO_POS_GRADE" , "999");
		        	recSaveLoc.setField("RTN_MSG" , sRTN_MSG);
 
		        	ydUtils.displayRecord(szOperationName, recSaveLoc);
		        	
		        	rsSaveLoc.addRecord(recSaveLoc);
//		        	150909 hun list에 VO들 담아서 소팅
		        	coilYdToLocVO = new CoilYdToLocVO();
		        	coilYdToLocVO.setYdStkColGp(szYdStkColGpCHK);
		        	coilYdToLocVO.setYdStkBedNo(szYdStkBedNoCHK);
		        	coilYdToLocVO.setYdStkLyrNo(szYdStkLyrNoCHK);
		        	coilYdToLocVO.setToPosGrade(999 );
		        	
		        	listSaveGrade.add(coilYdToLocVO);

					continue;
				}	
				if ("0".equals(sRTN_CD)) {
					outRecord.setField("RTN_BED" 	, "");	
					outRecord.setField("RTN_CD" 	, "1");	

//					return sRTN_BED = "";
				}	
				szYdStkColGp = StringHelper.evl(outRecord1.getFieldString("RTN_COLGP"), "");
				szYdStkBedNo = StringHelper.evl(outRecord1.getFieldString("RTN_BEDNO"), "");
				szYdStkLyrNo = StringHelper.evl(outRecord1.getFieldString("RTN_LYRNO"), "");
				szLeftColGp 	= StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_COLGP"), "");
				szLeftBedNo 	= StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_BEDNO"), "");
				szLeftLyrNo 	= StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_LYRNO"), "");
				szLeftStlNo 	= StringHelper.evl(outRecord1.getFieldString("RTN_LEFT_STLNO"), "");
				szRightColGp = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_COLGP"), "");
				szRightBedNo = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_BEDNO"), "");
				szRightLyrNo = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_LYRNO"), "");
				szRightStlNo = StringHelper.evl(outRecord1.getFieldString("RTN_RIGHT_STLNO"), "");
				
				//대차는 평점 check 하지 않는다
//	        	if(szYdStkColGp.substring(2, 4).equals("TC")) {
				if((szYdStkColGp.substring(2, 3).equals("0"))||
						(szYdStkColGp.substring(2, 3).equals("1"))||
						(szYdStkColGp.substring(2, 3).equals("2"))||
						(szYdStkColGp.substring(2, 3).equals("3"))||
						(szYdStkColGp.substring(2, 3).equals("4"))||
						(szYdStkColGp.substring(2, 3).equals("5"))||
						(szYdStkColGp.substring(2, 3).equals("6"))
				     ) {
	        	} else {
	        		szRtnVal = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
	        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품 대차 중량체크 로직=>>>>"+szRtnVal, YdConstant.INFO);
	 					inRecord2 = JDTORecordFactory.getInstance().create();
	 					JDTORecord 		outRecord4 = JDTORecordFactory.getInstance().create();
	 					
 	 					inRecord2.setField("YD_SCH_CD"		, szSchCd);//스케줄코드
 	 					inRecord2.setField("YD_CRN_SCH_ID"	, sYdCrnSchId);  //
 	 					EJBConnector  ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
 	 					outRecord4 = (JDTORecord)ejbConn.trx("getStkColTCarUpChk2", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
 	 					
 	 					String sRTN_CD2	= StringHelper.evl(outRecord4.getFieldString("RTN_CD"), "0");
 	 					String sSTL_CNT	= StringHelper.evl(outRecord4.getFieldString("STL_CNT"), "0");
 	 					String sSUM_WGT	= StringHelper.evl(outRecord4.getFieldString("SUM_WGT"), "0");
 	 					
 	 					if ("0".equals(sRTN_CD2)) {
 	 						szMsg = "상차 가능 CHECK시  ERROR";
 	 						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 	 					}	
 	 					
 	 					double dSTL_CNT = Integer.parseInt(sSTL_CNT);
 	 					double dSUM_WGT = Integer.parseInt(sSUM_WGT);
 	 					if (dSTL_CNT <= 3) {
 	 						if (dSUM_WGT > YdConstant.YD_COIL_TC_WEIGH_MAX) {
	 	 						szMsg = "중량초 오버로 대차상차 불가능 (대차 자동 출발 )";
	 	 						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	 	 						
	 	 						YdTcarFtmvMtlDao ydTcarftmvmtlDao = new YdTcarFtmvMtlDao();
	 	 						JDTORecordSet    outRecSet  = null;
	 	 						recPara = JDTORecordFactory.getInstance().create();				
	 	 			    		recPara.setField("YD_EQP_ID", szSchCd);
	 	 			    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp"); 
	 	 			    		/*com.inisteel.cim.yd.dao.ydtcarftmvmtldao.YdTcarftmvmtlDao.getYdStockTcarSchCHK*/
	 	 			    		int nMtlCnt   = ydTcarftmvmtlDao.getYdTcarftmvmtl(recPara, outRecSet,303);

	 	 			    		if (nMtlCnt > 0 ) {	 	
		 	 						
		 	 			    		JDTORecord[] inRecord  =  null;
		 	 			    		inRecord = new JDTORecord[1];
		 	 			    		
		 	 			    		inRecord[0] = JDTORecordFactory.getInstance().create();
		 	 			    		inRecord[0].setRecord(outRecSet.getRecord(0));
		 	 			    		inRecord[0].setField("YD_CRN_SCH_ID"	, sYdCrnSchId);
		 	 			    		inRecord[0].setField("YD_SCH_CD"	, szSchCd);

		 	 						//대차 출발처리 
		 	 						EJBConnector  ejbConnT = new EJBConnector("default", "CoilGdsJspSeEJB", this);
		 	 	 					ejbConnT.trx("CoilYdTcarStsSetTcarD", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

	 	 			    		}
	 	 			    		/*
	 	 			    		recSaveLoc = JDTORecordFactory.getInstance().create();
	 	 			        	recSaveLoc.setField("YD_STK_COL_GP", szYdStkColGpCHK);
	 	 			        	recSaveLoc.setField("YD_STK_BED_NO", szYdStkBedNoCHK);
	 	 			        	recSaveLoc.setField("YD_STK_LYR_NO", szYdStkLyrNoCHK);
	 	 			        	recSaveLoc.setField("TO_POS_GRADE" , "999");
	 	 			        	recSaveLoc.setField("RTN_MSG" , szMsg);
	 	 			        	ydUtils.displayRecord(szOperationName, recSaveLoc);
	 	 			        	
	 	 			        	rsSaveLoc.addRecord(recSaveLoc);
	 	 			        	*/
//	 	 			        	150909 hun list에 VO들 담아서 소팅
	 	 			        	coilYdToLocVO = new CoilYdToLocVO();
	 	 			        	coilYdToLocVO.setYdStkColGp(szYdStkColGpCHK);
	 	 			        	coilYdToLocVO.setYdStkBedNo(szYdStkBedNoCHK);
	 	 			        	coilYdToLocVO.setYdStkLyrNo(szYdStkLyrNoCHK);
	 	 			        	coilYdToLocVO.setToPosGrade(999);
	 	 			        	
	 	 			        	listSaveGrade.add(coilYdToLocVO);

	 	 						continue;   			    				
 	 						}
 	 					}
	        		
	        		
	        		
	        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품설비 TO위치=>>>>"+szRtnVal, YdConstant.INFO);
					outRecord.setField("RTN_BED" 	, szRtnVal);	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
//	        		return szRtnVal;
	        	}
	        	
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 1단 좌측 코일 To위치평점항목  체크Set  START★★★★★", YdConstant.INFO);	        	
	        	// 1단 좌측 코일 To위치평점항목 Set.
	        	szLeftGrade = SearchCoilGdsYdLeftCoilGrade(szYdStkColGp, szYdStkBedNo, szYdStkLyrNo, szStlNo, szLeftColGp, szLeftBedNo, szLeftLyrNo, szLeftStlNo);
	        	
	        	if (szLeftGrade.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"좌측적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 재료번호[" + szLeftStlNo + "]의 평점항목Set에 오류가 있습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					/*
					recSaveLoc = JDTORecordFactory.getInstance().create();
		        	recSaveLoc.setField("YD_STK_COL_GP", szYdStkColGpCHK);
		        	recSaveLoc.setField("YD_STK_BED_NO", szYdStkBedNoCHK);
		        	recSaveLoc.setField("YD_STK_LYR_NO", szYdStkLyrNoCHK);
		        	recSaveLoc.setField("TO_POS_GRADE" , "999");
		        	recSaveLoc.setField("RTN_MSG" , szMsg);
		        	ydUtils.displayRecord(szOperationName, recSaveLoc);
		        	
		        	rsSaveLoc.addRecord(recSaveLoc);
		        	*/
//		        	150909 hun list에 VO들 담아서 소팅
		        	coilYdToLocVO = new CoilYdToLocVO();
		        	coilYdToLocVO.setYdStkColGp(szYdStkColGpCHK);
		        	coilYdToLocVO.setYdStkBedNo(szYdStkBedNoCHK);
		        	coilYdToLocVO.setYdStkLyrNo(szYdStkLyrNoCHK);
		        	coilYdToLocVO.setToPosGrade(999 );
		        	
		        	listSaveGrade.add(coilYdToLocVO);

					continue;
        		}else {
        			szMsg = "좌측적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 재료번호[" + szLeftStlNo + "]의 평점항목Set[" + szLeftGrade + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		}
	        	
	        	// 1단 우측 코일 To위치평점항목 Set.
	        	szRightGrade = SearchCoilGdsYdRightCoilGrade(szYdStkColGp, szYdStkBedNo, szYdStkLyrNo, szStlNo, szRightColGp, szRightBedNo, szRightLyrNo, szRightStlNo);
	        	
	        	if (szLeftGrade.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"우측적치단(" + szRightColGp + szRightBedNo + szRightLyrNo + ")의 재료번호[" + szRightStlNo + "]의 평점항목Set에 오류가 있습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					/*
					recSaveLoc = JDTORecordFactory.getInstance().create();
		        	recSaveLoc.setField("YD_STK_COL_GP", szYdStkColGpCHK);
		        	recSaveLoc.setField("YD_STK_BED_NO", szYdStkBedNoCHK);
		        	recSaveLoc.setField("YD_STK_LYR_NO", szYdStkLyrNoCHK);
		        	recSaveLoc.setField("TO_POS_GRADE" , "999");
		        	recSaveLoc.setField("RTN_MSG" , szMsg);
		        	ydUtils.displayRecord(szOperationName, recSaveLoc);
		        	
		        	rsSaveLoc.addRecord(recSaveLoc);
		        	*/
//		        	150909 hun list에 VO들 담아서 소팅
		        	coilYdToLocVO = new CoilYdToLocVO();
		        	coilYdToLocVO.setYdStkColGp(szYdStkColGpCHK);
		        	coilYdToLocVO.setYdStkBedNo(szYdStkBedNoCHK);
		        	coilYdToLocVO.setYdStkLyrNo(szYdStkLyrNoCHK);
		        	coilYdToLocVO.setToPosGrade(999 );
		        	
		        	listSaveGrade.add(coilYdToLocVO);

					continue;
        		}else {
        			szMsg = "우측적치단(" + szRightColGp + szRightBedNo + szRightLyrNo + ")의 재료번호[" + szRightStlNo + "]의 평점항목Set[" + szRightGrade + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		}
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 1단 좌측 코일 To위치평점항목 체크Set  END★★★★★", YdConstant.INFO);
	        	
	        	
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★", YdConstant.INFO);
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 To위치 평점 계산  START★★★★★", YdConstant.INFO);
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★", YdConstant.INFO);
	        	
	        	// To위치 평점항목 Set
	        	szToPosGrade = getToPosGrade(szSchCoilStat, szYdStkLyrNo, szLeftGrade, szRightGrade);
	        	
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일야드 BRE: 스케줄코일상태("+szSchCoilStat+") TO위치적치단("+szYdStkLyrNo+") 좌측1단코일상태("+szLeftGrade+") 우측1단코일상태("+szRightGrade+")", YdConstant.INFO);	    		
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치 평점결과:loop순번:"+(nLoop+1)+" 재료번호(" + szStlNo + ")("+szYdStkColGp + szYdStkBedNo + szYdStkLyrNo+") ▶▶▶▶"+szToPosGrade+"점◀◀◀◀", YdConstant.INFO);
	        	
	        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★", YdConstant.INFO);
	        	
	        	// 평점이 1순위면 바로 리턴한다.
	        	if ("1".equals(szToPosGrade)) {
	        		szRtnVal = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
	        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치=>>>>"+szRtnVal, YdConstant.INFO);

//LOG_TABLE    					
	    			EJBConnector ejbConn = null;
	    			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
	    			recLog.setField("STL_NO"			, szStlNo);
	    			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
	    			recLog.setField("YD_GP"				, "J");
	    			recLog.setField("YD_SCH_CD"			, szSchCd);
	    			recLog.setField("YD_USER_ID"		, "log");
	    			recLog.setField("MSG"				, "★★★★★C열연코일제품야드 TO위치=>>>>"+szRtnVal + ":평점 1점");
	    			
	    			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
	    			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
	        		
	        		
	        		outRecord.setField("RTN_BED" 	, szRtnVal);	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;

//	        		return szRtnVal;
	        	}
	        	 
	        	/*
	        	 * TO위치 대상건이 50건이 넘는 경우 
	        	 * 평점대상 기준을 30점 이하로 조정 한다.
	        	 */
	        	int toPosGrade = Integer.parseInt(szToPosGrade);
	        	
	        	if (nLoop>=50 && toPosGrade <= 30) {
	        		szRtnVal = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
	        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치4=>>>>"+szRtnVal, YdConstant.INFO);

//LOG_TABLE    					
	    			EJBConnector ejbConn = null;
	    			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
	    			recLog.setField("STL_NO"			, szStlNo);
	    			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
	    			recLog.setField("YD_GP"				, "J");
	    			recLog.setField("YD_SCH_CD"			, szSchCd);
	    			recLog.setField("YD_USER_ID"		, "log");
	    			recLog.setField("MSG"				, "★★★★★C열연코일제품야드 TO위치4=>>>>"+szRtnVal + ":50회 이상 평점 30점 이하:" +szToPosGrade+"점에서 결정됨");
	    			
	    			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
	    			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
	        		
	        		
	        		outRecord.setField("RTN_BED" 	, szRtnVal);	
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord; 
	        	}
	        	
//	        	150909 hun list에 VO들 담아서 소팅
	        	coilYdToLocVO = new CoilYdToLocVO();
	        	coilYdToLocVO.setYdStkColGp(szYdStkColGp);
	        	coilYdToLocVO.setYdStkBedNo(szYdStkBedNo);
	        	coilYdToLocVO.setYdStkLyrNo(szYdStkLyrNo);
	        	coilYdToLocVO.setToPosGrade(Integer.parseInt(szToPosGrade) );
	        	
	        	listSaveGrade.add(coilYdToLocVO);
				
			} //end of for	
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 적치대상제 검색 END★★★★★", YdConstant.INFO);
			
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★1C열연코일제품야드 평점 1점이 아닌경우 대상 찾기 START★★★★★", YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "C열연코일제품야드 "+ szStlNo + "평점리스트", YdConstant.INFO);
			/*
			if (rsSaveLoc.size() > 0) {
				for(int nLoop =0 ; nLoop<rsSaveLoc.size(); nLoop++ ) {
					rsSaveLoc.absolute(nLoop);
					recSaveLoc1 = JDTORecordFactory.getInstance().create();
					recSaveLoc1 = rsSaveLoc.getRecord(nLoop);
					
					String szGrdColGp1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "YD_STK_COL_GP");
					String szGrdBedNo1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "YD_STK_BED_NO");
					String szGrdLyrNo1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "YD_STK_LYR_NO");
					String szToPosGrd1 = ydDaoUtils.paraRecChkNull(recSaveLoc1, "TO_POS_GRADE");
					String szLToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc1, "TO_LEFT_GRADE");
					String szRToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc1, "TO_RIGTH_GRADE");
					String szRTN_MSG   =  ydDaoUtils.paraRecChkNull(recSaveLoc1, "RTN_MSG");
					
					szMsg ="[" + szStlNo + "]"+"["+ nLoop+"]" + szGrdColGp1 + szGrdBedNo1 + szGrdLyrNo1 +"<<Score="+szToPosGrd1+"(" + "L:"+szLToPosGrd+ "|R:"+szRToPosGrd+")>>,"+szRTN_MSG+"\r\n";
					szMsgList = szMsgList + szMsg;
					
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				} // end of for
			}			
			*/
			
			szMsg ="[ 평점 sort arrayList ] size="+listSaveGrade.size()+"\r\n";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

//			151015 hun 동점경우 2단 우선 배치를 위해 평점 뒤에 적치단 정보 입력 ( 002-> 1점, 001->2점, ""->3점 )
			if(listSaveGrade.size()>0){
				for(int nLoop =0 ; nLoop<listSaveGrade.size(); nLoop++ ) {
					coilYdToLocVO = (CoilYdToLocVO)listSaveGrade.get(nLoop);
					if("001".equals( coilYdToLocVO.getYdStkLyrNo()) ){
						coilYdToLocVO.setYdStkLyrGrade( Integer.parseInt( coilYdToLocVO.getToPosGrade() + "2"));
					}else if("002".equals( coilYdToLocVO.getYdStkLyrNo()) ){
						coilYdToLocVO.setYdStkLyrGrade( Integer.parseInt( coilYdToLocVO.getToPosGrade() + "1"));
					}else {
						coilYdToLocVO.setYdStkLyrGrade( Integer.parseInt( coilYdToLocVO.getToPosGrade() + "3"));
					}
				}
			}
//			150908 hun 평점을 arrayList에 담아서 소트
			if(listSaveGrade.size()>0){
				Collections.sort(listSaveGrade, new CoilYdToLayComparator());
			}
			
			if (listSaveGrade.size() > 0) {
				for(int nLoop =0 ; nLoop<listSaveGrade.size(); nLoop++ ) {
					coilYdToLocVO = (CoilYdToLocVO)listSaveGrade.get(nLoop);
					szMsg ="sort Grade[" + szStlNo + "]"+nLoop+",ColGp="+ coilYdToLocVO.getYdStkColGp() + coilYdToLocVO.getYdStkBedNo() + coilYdToLocVO.getYdStkLyrNo() +
					       "<<Score="+coilYdToLocVO.getToPosGrade()+">>\r\n";
					szMsgList = szMsgList + szMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				} // end of for
			}	

//LOG_TABLE    					
			EJBConnector ejbConn = null;
			JDTORecord recLog  			= JDTORecordFactory.getInstance().create(); 
			recLog.setField("STL_NO"			, szStlNo);
			recLog.setField("YD_CRN_SCH_ID"		, sYdCrnSchId);
			recLog.setField("YD_GP"				, "J");
			recLog.setField("YD_SCH_CD"			, szSchCd);
			recLog.setField("YD_USER_ID"		, "log");
			//recLog.setField("MSG"				, szMsgList.substring(0, 999));
			recLog.setField("MSG"				, szMsgList);
			
			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
			ejbConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { recLog });
			
			/*
			if (rsSaveLoc.size() > 0) {
			
				for(int nLoop =0 ; nLoop<rsSaveLoc.size(); nLoop++ ) {
					rsSaveLoc.absolute(nLoop);
					recSaveLoc = JDTORecordFactory.getInstance().create();
					recSaveLoc = rsSaveLoc.getRecord(nLoop);
					
					if(nLoop ==0){							 
						szToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc, "TO_POS_GRADE");
						
						if(!szToPosGrd.equals("999")){
							szGrdColGp = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_COL_GP");
							szGrdBedNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_BED_NO");
							szGrdLyrNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_LYR_NO");
						} 
					}
						if (Integer.parseInt(szToPosGrd) > Integer.parseInt(ydDaoUtils.paraRecChkNull(recSaveLoc, "TO_POS_GRADE"))) {
							szGrdColGp = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_COL_GP");
							szGrdBedNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_BED_NO");
							szGrdLyrNo = ydDaoUtils.paraRecChkNull(recSaveLoc, "YD_STK_LYR_NO");
							szToPosGrd = ydDaoUtils.paraRecChkNull(recSaveLoc, "TO_POS_GRADE");
						}
				} // end of for
				*/
			if (listSaveGrade.size() > 0) {
				for(int nLoop =0 ; nLoop<listSaveGrade.size(); nLoop++ ) {
					coilYdToLocVO = (CoilYdToLocVO)listSaveGrade.get(nLoop);
					
//					999 점 pass
					if(coilYdToLocVO.getToPosGrade()==999){
						continue;
					}
					
					szGrdColGp = coilYdToLocVO.getYdStkColGp();
					szGrdBedNo = coilYdToLocVO.getYdStkBedNo();
					szGrdLyrNo = coilYdToLocVO.getYdStkLyrNo();
					szToPosGrd = String.valueOf(coilYdToLocVO.getToPosGrade());
					
					//마지막 체크 To위치에 코일이 있는지 확인
		        	ydUtils.putLog(szSessionName, szMethodName, "===================선택된 저장위치가 공위치인지 3nd CHECK====================", YdConstant.INFO);
					
		        	recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", szGrdColGp);
					recPara.setField("YD_STK_BED_NO", szGrdBedNo);
					recPara.setField("YD_STK_LYR_NO", szGrdLyrNo);
					
		        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
		        	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyr*/
		        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBed, 0);
		        			
		        	if (intRtnVal > 0) {
		        		rsBed.first();
		    			recBed =JDTORecordFactory.getInstance().create();
		    			recBed = rsBed.getRecord();
		    			String sYD_STK_LYR_MTL_STAT = recBed.getFieldString("YD_STK_LYR_MTL_STAT");
		    			
		    			if(!sYD_STK_LYR_MTL_STAT.equals("E")){
		    	   			szMsg = "확인:"+szStlNo+"적치단(" + szGrdColGp + szGrdBedNo + szGrdLyrNo + ") 선택된 To위치에 coil 발견 CHECK ERROR.";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
			        		continue;
		    			}else{
					
			        		szRtnVal = szGrdColGp + szGrdBedNo + szGrdLyrNo;
			        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 3rd TO위치=>>>>"+szRtnVal, YdConstant.INFO);
			        		ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 평점 1점이 아닌경우 대상 찾기 END★★★★★", YdConstant.INFO);
			    			outRecord.setField("RTN_BED" 		, szRtnVal);	
			    			outRecord.setField("RTN_CD" 		, "1");	
			    			
			    			return outRecord;
		    			}
		        	}
					
				}
				
				ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 3rd TO위치=>>>>"+szRtnVal, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 평점 1점이 아닌경우 대상 찾기 END★★★★★", YdConstant.INFO);

			} else {
				szRtnVal = "";
			}				
			
			
			ydUtils.putLog(szSessionName, szMethodName, szStlNo+"3rd end", YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치결정 END★★★★★", YdConstant.INFO);
			outRecord.setField("RTN_BED" 		, szRtnVal);	
			outRecord.setField("RTN_CD" 		, "1");	
			
			return outRecord;

//			return szRtnVal;
		} catch(Exception e) {
			szMsg = "코일창고야드 To위치검색 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 TO위치결정 ERROR★★★★★", YdConstant.INFO);
			outRecord.setField("RTN_BED" 	, "");	
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;

			//			return szRtnVal = "";
		}
	} // end of searchCoilGdsYdToPositionAutoThird
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일창고야드중량/폭 편차Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilGdsYdWtCheck(String szStlNo, String szThick, String szWidth, String szWeigth, String szLeftStlNo, String szLeftThick,  String szLeftWidth, String szLeftWeigth,String szRightStlNo,String szRightThick,  String szRightWidth, String szRightWeigth) throws JDTOException  {
		
		YdStockDao     ydStockDao  = new YdStockDao(); 
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilGdsYdWtCheck";
		String szOperationName	=	"코일창고야드중량/폭 편차Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		double dblThick  		= 0;  // 
		double dblLeftThick  	= 0;  // 우측코일두께
		double dblRightThick  	= 0;
		double dblMinThick  	= 0;

		long lngWeigth  		= 0;  // 코일중량
		long lngLeftWeigth  	= 0;  // 좌측코일중량
		long lngRightWeigth  	= 0;  // 우측코일중량
		long lngMinWeigth  		= 0;  // 최소코일중량
		double dblEndWeigth  		= 0;  // 최소코일중량

		
		double dblWidth 			= 0;  // 폭
		double dblLeftWidth  		= 0;  // 좌측코일폭
		double dblRightWidth  	= 0;  // 우측코일폭
		double dblMinWidth  	= 0;  //
		double dblMaxWidth  	= 0;  //
		
		double dblChkVal  		= 0;  // 
		
		
		try {
			
			if("".equals(szLeftStlNo) || "".equals(szRightStlNo)) {
				return szRtnVal;
			}
			dblThick 		= Double.parseDouble(szThick);
			lngWeigth 		= Long.parseLong(szWeigth);
			dblWidth 		= Double.parseDouble(szWidth);
			
			dblLeftThick 	= Double.parseDouble(szLeftThick);
			lngLeftWeigth 	= Long.parseLong(szLeftWeigth);
			dblLeftWidth 	= Double.parseDouble(szLeftWidth);
			
			dblRightThick 	= Double.parseDouble(szRightThick);
			lngRightWeigth	= Long.parseLong(szRightWeigth);
			dblRightWidth	= Double.parseDouble(szRightWidth);
			

			if(lngLeftWeigth >= lngRightWeigth){
				lngMinWeigth = lngRightWeigth;  //1단 중량
				dblMinThick  = dblRightThick;   //1단 두께
				
			} else {
				lngMinWeigth = lngLeftWeigth;
				dblMinThick  = dblLeftThick;
			}
 			
			if(dblLeftWidth >= dblRightWidth){
				dblMinWidth = dblRightWidth;
				dblMaxWidth = dblLeftWidth;
				
			} else {
				dblMinWidth = dblLeftWidth;
				dblMaxWidth = dblRightWidth;
				
			}
			
			//---------------------------폭 허용오차 가져 오기 --------------------------------
			JDTORecord 		inRecord 		= JDTORecordFactory.getInstance().create();
			JDTORecord 		outRecord2  	= JDTORecordFactory.getInstance().create();
			JDTORecordSet 	outRecord 	= JDTORecordFactory.getInstance().createRecordSet("");
			YdEqpDao		ydEqpDao 	= new YdEqpDao();
			
			String szRuleWidth="100";
			
			
			inRecord.setField("REPR_CD_GP", "J00006");
			
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
			intRtnVal = ydEqpDao.getYdEqp(inRecord, outRecord, 999);
			if(intRtnVal > 0) {
				outRecord.first();
				outRecord2  = outRecord.getRecord();
				szRuleWidth = outRecord2.getFieldString("ITEM1");				
			}
			double szRuleWidth2 =  Double.parseDouble(szRuleWidth);
			//---------------------------폭 허용오차 가져 오기 --------------------------------
			
       	
        	szMsg = "대상재료번호(" + szStlNo + ")=>>대상코일중량,두께,폭:"+lngWeigth+","+dblThick +","+dblWidth ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
        	szMsg = "1단좌측재료번호(" + szLeftStlNo + ")=>>좌측중량,두께,폭:"+lngLeftWeigth+","+dblLeftThick+","+dblLeftWidth  ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단우측재료번호(" + szRightStlNo + ")=>>우측중량,두께,폭:"+lngRightWeigth+","+dblRightThick+","+dblRightWidth  ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
        	szMsg = "소 중량,소두께,소폭,큰폭:"+lngMinWeigth+","+dblMinThick+","+dblMinWidth+","+dblMaxWidth;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


//			if  (dblMinThick < 5  ){
//			if  (dblMinThick < 9  ){
//				if  (dblMinWidth < 1401  ){
//					dblChkVal = lngMinWeigth * 0.16;
//				} else {
//					dblChkVal = lngMinWeigth * 0.17;
//				}	
//			} else {
//				if  (dblMinWidth < 1401  ){
//					dblChkVal = lngMinWeigth * 0.17;
//				} else {
//					dblChkVal = lngMinWeigth * 0.18;
//				}	
//			}
//		} else {
//			if  (dblMinWidth < 1401  ){
//				dblChkVal = lngMinWeigth * 0.15;
//			} else {
//				dblChkVal = lngMinWeigth * 0.16;
//			}	
//		}	
			if  (dblMinThick < 7  ){
				if  (dblMinWidth < 1301  ){
					dblChkVal = lngMinWeigth * 0.13;
				} else {
					dblChkVal = lngMinWeigth * 0.14;
				}	
			} else {
				if  (dblMinWidth < 1301  ){
					dblChkVal = lngMinWeigth * 0.14;
				} else {
					dblChkVal = lngMinWeigth * 0.15;
				}	
			}
			dblEndWeigth = lngMinWeigth + dblChkVal;
			
			if (lngWeigth <= dblEndWeigth){
				
				if (dblWidth <= dblMaxWidth + szRuleWidth2){
					szRtnVal = "SUCCESS";
				} else {
					szRtnVal = "WID_FAILURE";
				}

			} else {
//				szRtnVal = "FAILURE";
				szRtnVal = "WGT_FAILURE";
			}
				
	
			return szRtnVal;
		} catch(Exception e) {
			szMsg = "코일창고야드중량Check 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} // end of searchCoilGdsYdWtCheck
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일창고야드중량/폭 편차CheckABC
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilGdsYdWtCheckABC(String szStlNo, String szThick, String szWidth, String szWeigth, String szLeftStlNo, String szLeftThick,  String szLeftWidth, String szLeftWeigth,String szRightStlNo,String szRightThick,  String szRightWidth, String szRightWeigth) throws JDTOException  {
		
		YdStockDao     ydStockDao  = new YdStockDao();
		
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilGdsYdWtCheckABC";
		String szOperationName	=	"코일창고야드중량/폭 편차CheckABC";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		double dblThick  		= 0;  // 
		double dblLeftThick  	= 0;  // 우측코일두께
		double dblRightThick  	= 0;
		double dblMinThick  	= 0;

		long lngWeigth  		= 0;  // 코일중량
		long lngLeftWeigth  	= 0;  // 좌측코일중량
		long lngRightWeigth  	= 0;  // 우측코일중량
		long lngMinWeigth  		= 0;  // 최소코일중량
		double dblEndWeigth  		= 0;  // 최소코일중량

		
		double dblWidth 			= 0;  // 폭
		double dblLeftWidth  		= 0;  // 좌측코일폭
		double dblRightWidth  	= 0;  // 우측코일폭
		double dblMinWidth  	= 0;  //
		double dblMaxWidth  	= 0;  //
		
		double dblChkVal  		= 0;  // 
		
		
		try {
			
			if("".equals(szLeftStlNo) || "".equals(szRightStlNo)) {
				return szRtnVal;
			}
			dblThick 		= Double.parseDouble(szThick);
			lngWeigth 		= Long.parseLong(szWeigth);
			dblWidth 		= Double.parseDouble(szWidth);
			
			dblLeftThick 	= Double.parseDouble(szLeftThick);
			lngLeftWeigth 	= Long.parseLong(szLeftWeigth);
			dblLeftWidth 	= Double.parseDouble(szLeftWidth);
			
			dblRightThick 	= Double.parseDouble(szRightThick);
			lngRightWeigth	= Long.parseLong(szRightWeigth);
			dblRightWidth	= Double.parseDouble(szRightWidth);
			

			if(lngLeftWeigth >= lngRightWeigth){
				lngMinWeigth = lngRightWeigth;  //1단 중량
				dblMinThick  = dblRightThick;   //1단 두께
				
			} else {
				lngMinWeigth = lngLeftWeigth;
				dblMinThick  = dblLeftThick;
			}
 			
			if(dblLeftWidth >= dblRightWidth){
				dblMinWidth = dblRightWidth;
				dblMaxWidth = dblLeftWidth;
				
			} else {
				dblMinWidth = dblLeftWidth;
				dblMaxWidth = dblRightWidth;
				
			}
			
			//---------------------------폭 허용오차 가져 오기 --------------------------------
			JDTORecord 		inRecord 		= JDTORecordFactory.getInstance().create();
			JDTORecord 		outRecord2  	= JDTORecordFactory.getInstance().create();
			JDTORecordSet 	outRecord 	= JDTORecordFactory.getInstance().createRecordSet("");
			YdEqpDao		ydEqpDao 	= new YdEqpDao();
			
			String szRuleWidth="100";
			
			
			inRecord.setField("REPR_CD_GP", "J00006");
			
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
			intRtnVal = ydEqpDao.getYdEqp(inRecord, outRecord, 999);
			if(intRtnVal > 0) {
				outRecord.first();
				outRecord2  = outRecord.getRecord();
				szRuleWidth = outRecord2.getFieldString("ITEM1");				
			}
			double szRuleWidth2 =  Double.parseDouble(szRuleWidth);
			//---------------------------폭 허용오차 가져 오기 --------------------------------
			
       	
        	szMsg = "대상재료번호(" + szStlNo + ")=>>대상코일중량,두께,폭:"+lngWeigth+","+dblThick +","+dblWidth ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
        	szMsg = "1단좌측재료번호(" + szLeftStlNo + ")=>>좌측중량,두께,폭:"+lngLeftWeigth+","+dblLeftThick+","+dblLeftWidth  ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단우측재료번호(" + szRightStlNo + ")=>>우측중량,두께,폭:"+lngRightWeigth+","+dblRightThick+","+dblRightWidth  ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
        	szMsg = "소 중량,소두께,소폭,큰폭:"+lngMinWeigth+","+dblMinThick+","+dblMinWidth+","+dblMaxWidth;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
 	
			if  (dblMinThick < 7  ){
				if  (dblMinWidth < 1301  ){
					dblChkVal = lngMinWeigth * 0.13;
				} else {
					dblChkVal = lngMinWeigth * 0.14;
				}	
			} else {
				if  (dblMinWidth < 1301  ){
					dblChkVal = lngMinWeigth * 0.14;
				} else {
					dblChkVal = lngMinWeigth * 0.15;
				}	
			}
			dblEndWeigth = lngMinWeigth + dblChkVal;
			
			if (lngWeigth <= dblEndWeigth){
				
				if (dblWidth <= dblMaxWidth+szRuleWidth2){
					szRtnVal = "SUCCESS";
				} else {
					szRtnVal = "WID_FAILURE";
				}

			} else {
 				szRtnVal = "WGT_FAILURE";
			}
				
	
			return szRtnVal;
		} catch(Exception e) {
			szMsg = "코일창고야드중량/폭 편차Check 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} // end of searchCoilGdsYdWtCheckABC
	
	/**
	 *      [A] 오퍼레이션명 : 코일창고야드외경간격Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilGdsYdOutDiaIntervalCheck(String szYdStkColGp, String szYdStkBedNo ,String szStlNo, String szYdStkLyrNo, String lOutDia, String szLeftStlNo,String lLeftOutDia, String szRightStlNo,String lRightOutDia) throws JDTOException  {
		
		YdStockDao     ydStockDao  = new YdStockDao();
		YdStkBedDao    ydStkBedDao = new YdStkBedDao();
		
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilGdsYdOutDiaIntervalCheck";
		String szOperationName	=	"코일창고야드외경간격Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		JDTORecord recPara 		= null;
		JDTORecordSet rsStock     = null;
		JDTORecord recStock       = null;
		
		JDTORecordSet rsBed     = null;
		JDTORecord recBed       = null;
		
		String szYD_COIL_OUTDIA_GRP_GP = "";  //BED외경군
		long   lngDiaDiffConst = 0;           //외경간격상수
		long   lngBedLength    = 0;           //군별소요길이
		long   lngLeftOutDia   = 0;           //좌측코일외경
		long   lngRightOutDia  = 0;           //우측코일외경
		long   lngOutDiaInterval = 0;         //외경간격
		long   lngOutDia 		= 0;         
		long   lngLength 		= 0;         
		
		try {
			
			if("".equals(szLeftStlNo) || "".equals(szRightStlNo)) {
				return szRtnVal;
			}
			
			// BED정보
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
			rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    
        	intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsBed, 0);
			
        	if (intRtnVal <= 0) {
				szMsg = "적치베드(" + szYdStkColGp + szYdStkBedNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			}
        	
        	rsBed.first();
        	recBed = rsBed.getRecord();
        	
        	szYD_COIL_OUTDIA_GRP_GP = ydDaoUtils.paraRecChkNull(recBed, "YD_COIL_OUTDIA_GRP_GP");
    
			
        	//권하대상코일 외경 ;
        	lngOutDia = Long.parseLong(lOutDia);
        	lngLeftOutDia = Long.parseLong(lLeftOutDia);
        	lngRightOutDia = Long.parseLong(lRightOutDia);
        	
        	if ("A".equals(szYD_COIL_OUTDIA_GRP_GP)) {
        		lngDiaDiffConst = 550;     
        		lngBedLength    = 1300; 
        	} else if ("B".equals(szYD_COIL_OUTDIA_GRP_GP)) {
        		lngDiaDiffConst = 820;     
        		lngBedLength    = 1950;
        	} else if ("C".equals(szYD_COIL_OUTDIA_GRP_GP)) {
        		lngDiaDiffConst = 850;     
        		lngBedLength    = 2600;
        	} else {
        		szMsg = "적치베드(" + szYdStkColGp + szYdStkBedNo + ") 외경군 설정이 안되어 있습니다.외경군설정을 확인하세요";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        		return szRtnVal = YdConstant.RETN_CD_FAILURE;
        	}
        	
        	szMsg = "1단좌측재료번호(" + szLeftStlNo + ")=>>좌측코일외경:"+lngLeftOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단우측재료번호(" + szRightStlNo + ")=>>우측코일외경:"+lngRightOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
        	szMsg = "대상재료번호(" + szStlNo + "):"+"대상코일외경군(" + szYD_COIL_OUTDIA_GRP_GP + "외경간격상수:"+lngDiaDiffConst;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			lngLength = lngOutDia -(lngBedLength - ((lngLeftOutDia + lngRightOutDia) / 2));
			
        	//lngOutDiaInterval = lngOutDia - (lngBedLength - ((lngLeftOutDia + lngRightOutDia) / 2));
        	
        	ydUtils.putLog(szSessionName, szMethodName, "lngLength:"+lngLength, YdConstant.INFO);
        	
			if (lngLength > lngDiaDiffConst) {
        		szRtnVal = YdConstant.RETN_CD_SUCCESS;
        	} else {
        		szRtnVal = YdConstant.RETN_CD_FAILURE;
        	}
			
			return szRtnVal;
		} catch(Exception e) {
			szMsg = "코일창고야드외경간격Check 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} // end of searchCoilGdsYdOutDiaIntervalCheck
	

	/**
	 *      [A] 오퍼레이션명 : 횡행좌표 거리 계산 값에 의한 외경 CHECK
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilGdsYdOutDiaOverRunCheck(String szYdStkColGp, String szYdStkBedNo ,String szStlNo, String szYdStkLyrNo, String lOutDia, String szLeftStlNo,String lLeftOutDia, String szRightStlNo,String lRightOutDia) throws JDTOException  {
		
		YdStkBedDao    ydStkBedDao = new YdStkBedDao();
		
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilGdsYdOutDiaOverRunCheck";
		String szOperationName	=	"횡행좌표 거리 계산 값에 의한 외경 CHECK";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		JDTORecord recPara 		= null;
		JDTORecordSet rsBed     = null;
		JDTORecord recBed       = null;
		
		String szCHK = "";
		
		try {
			
			if("".equals(szLeftStlNo) || "".equals(szRightStlNo)) {
				return szRtnVal;
			}
			
			// BED정보
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
			recPara.setField("STL_NO", szStlNo);
			rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    
        	intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsBed, 319);
			
        	if (intRtnVal <= 0) {
				szMsg = "적치베드(" + szYdStkColGp + szYdStkBedNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			}
        	
        	rsBed.first();
        	recBed = rsBed.getRecord();
        	
        	szCHK = ydDaoUtils.paraRecChkNull(recBed, "CHK");
        	
        	if("Y".equals(szCHK)){
        		szRtnVal = YdConstant.RETN_CD_SUCCESS;
        	}else{
        		szRtnVal = YdConstant.RETN_CD_FAILURE;
        	}
			
			return szRtnVal;
		} catch(Exception e) {
			szMsg = "횡행좌표 거리 계산 값에 의한 외경 CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} // end of searchCoilGdsYdOutDiaOverRunCheck
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일창고야드외경간격Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilGdsYdOutDiaIntervalCheckABC(String szYdStkColGp, String szYdStkBedNo ,String szStlNo, String szYdStkLyrNo, String lOutDia, String szLeftStlNo,String lLeftOutDia, String szRightStlNo,String lRightOutDia) throws JDTOException  {
		
		YdStockDao     ydStockDao  = new YdStockDao();
		YdStkBedDao    ydStkBedDao = new YdStkBedDao();
		
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilGdsYdOutDiaIntervalCheckABC";
		String szOperationName	=	"코일창고야드외경간격Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		JDTORecord recPara 		= null;
		JDTORecordSet rsStock     = null;
		JDTORecord recStock       = null;
		
		JDTORecordSet rsBed     = null;
		JDTORecord recBed       = null;
		
		String szYD_COIL_OUTDIA_GRP_GP = "";  //BED외경군
		long   lngDiaDiffConst = 0;           //외경간격상수
		long   lngBedLength    = 0;           //군별소요길이
		long   lngLeftOutDia   = 0;           //좌측코일외경
		long   lngRightOutDia  = 0;           //우측코일외경
		long   lngOutDiaInterval = 0;         //외경간격
		long   lngOutDia 		= 0;         
		long   lngLength 		= 0;         
		
		try {
			
			if("".equals(szLeftStlNo) || "".equals(szRightStlNo)) {
				return szRtnVal;
			}
			
			// BED정보
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
			rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    
        	intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsBed, 0);
			
        	if (intRtnVal <= 0) {
				szMsg = "적치베드(" + szYdStkColGp + szYdStkBedNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			}
        	
        	rsBed.first();
        	recBed = rsBed.getRecord();
        	
        	szYD_COIL_OUTDIA_GRP_GP = ydDaoUtils.paraRecChkNull(recBed, "YD_COIL_OUTDIA_GRP_GP");
    
			
        	//권하대상코일 외경 ;
        	lngOutDia = Long.parseLong(lOutDia);
        	lngLeftOutDia = Long.parseLong(lLeftOutDia);
        	lngRightOutDia = Long.parseLong(lRightOutDia);
        	
        	if ("A".equals(szYD_COIL_OUTDIA_GRP_GP)) {
        		lngDiaDiffConst = 550;     
        		lngBedLength    = 2200; 
        	} else if ("B".equals(szYD_COIL_OUTDIA_GRP_GP)) {
        		lngDiaDiffConst = 820;     
        		lngBedLength    = 2200;
        	} else if ("C".equals(szYD_COIL_OUTDIA_GRP_GP)) {
        		lngDiaDiffConst = 850;     
        		lngBedLength    = 2200;
        	} else {
        		szMsg = "적치베드(" + szYdStkColGp + szYdStkBedNo + ") 외경군 설정이 안되어 있습니다.외경군설정을 확인하세요";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        		return szRtnVal = YdConstant.RETN_CD_FAILURE;
        	}
        	
        	szMsg = "1단좌측재료번호(" + szLeftStlNo + ")=>>좌측코일외경:"+lngLeftOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단우측재료번호(" + szRightStlNo + ")=>>우측코일외경:"+lngRightOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
        	szMsg = "대상재료번호(" + szStlNo + "):"+"대상코일외경군(" + szYD_COIL_OUTDIA_GRP_GP + "외경간격상수:"+lngDiaDiffConst;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			lngLength = lngOutDia -(lngBedLength - ((lngLeftOutDia + lngRightOutDia) / 2));
			
        	//lngOutDiaInterval = lngOutDia - (lngBedLength - ((lngLeftOutDia + lngRightOutDia) / 2));
        	
        	ydUtils.putLog(szSessionName, szMethodName, "lngLength:"+lngLength, YdConstant.INFO);
        	
			if (lngLength > lngDiaDiffConst) {
        		szRtnVal = YdConstant.RETN_CD_SUCCESS;
        	} else {
        		szRtnVal = YdConstant.RETN_CD_FAILURE;
        	}
			
			return szRtnVal;
		} catch(Exception e) {
			szMsg = "코일창고야드외경간격Check 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} // end of searchCoilGdsYdOutDiaIntervalCheckABC
	
	/**
	 *      [A] 오퍼레이션명 : 코일창고야드외경편차Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilGdsYdOutDiaDiffCheck(String szStlNo, String szYdStkLyrNo, String lOutDia, String szLeftStlNo,String lLeftOutDia, String szRightStlNo,String lRightOutDia) throws JDTOException  {
		
		YdStockDao     ydStockDao  = new YdStockDao();
		
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilGdsYdOutDiaDiffCheck";
		String szOperationName	=	"코일창고야드외경편차Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		JDTORecord recPara 		= null;
		JDTORecordSet rsStock     = null;
		JDTORecord recStock       = null;
		
		
		long   lngOutDia   		= 0;           //코일외경
		long   lngLeftOutDia   	= 0;           //좌측코일외경
		long   lngRightOutDia  	= 0;           //우측코일외경
		
		try {
			
//			if("".equals(szLeftStlNo) || "".equals(szRightStlNo)) {
//				return szRtnVal;
//			}

			lngOutDia 		= Long.parseLong(lOutDia);
			lngLeftOutDia 	= Long.parseLong(lLeftOutDia);
			lngRightOutDia 	= Long.parseLong(lRightOutDia);
			
			//좌우재료번호가 이적대상과 동일 한 경우 
			if(szRightStlNo.equals(szStlNo)){
				lngRightOutDia=0;
				szRightStlNo="";
			}else if(szLeftStlNo.equals(szStlNo)){
				lngLeftOutDia=0;
				szLeftStlNo="";
			}

			szMsg = "대상재료번호(" + szStlNo + ")=>>대상코일외경:"+lngOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단좌측재료번호(" + szLeftStlNo + ")=>>좌측코일외경:"+lngLeftOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단우측재료번호(" + szRightStlNo + ")=>>우측코일외경:"+lngRightOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);


			
			if ("001".equals(szYdStkLyrNo)){
				// 좌우가 비워있는경우
				if ("".equals(szLeftStlNo) && "".equals(szRightStlNo)) {  // 좌우가 비워있는경우
	        		szRtnVal = YdConstant.RETN_CD_SUCCESS;
	        		
				}else {
					ydUtils.putLog(szSessionName, szMethodName, "확인:"+szStlNo+"Math.abs(lngOutDia - lngRightOutDia)" + Math.abs(lngOutDia - lngRightOutDia), YdConstant.INFO);
					ydUtils.putLog(szSessionName, szMethodName, "확인:"+szStlNo+"Math.abs(lngOutDia - lngLeftOutDia))" + Math.abs(lngOutDia - lngLeftOutDia), YdConstant.INFO);
					
					//좌우코일이 존재 하는 경우 
					if(!"".equals(szLeftStlNo) && (!"".equals(szRightStlNo))) {
//						if (Math.abs(lngOutDia - lngRightOutDia) <= 200 && Math.abs(lngOutDia - lngLeftOutDia) <= 200) {
						if (Math.abs(lngOutDia - lngRightOutDia) <= 180 && Math.abs(lngOutDia - lngLeftOutDia) <= 180) {
			        		szRtnVal = YdConstant.RETN_CD_SUCCESS;
			        	} else {
			        		szRtnVal = YdConstant.RETN_CD_FAILURE;
			        	}
					}else {
//						if (Math.abs(lngOutDia - lngRightOutDia) <= 200 || Math.abs(lngOutDia - lngLeftOutDia) <= 200) {
						if (Math.abs(lngOutDia - lngRightOutDia) <= 180 || Math.abs(lngOutDia - lngLeftOutDia) <= 180) {
			        		szRtnVal = YdConstant.RETN_CD_SUCCESS;
			        	} else {
			        		szRtnVal = YdConstant.RETN_CD_FAILURE;
			        	}
					}
					
					
				}

			} 
        	
			return szRtnVal;
		} catch(Exception e) {
			szMsg = "코일제품창고야드외경편차Check 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} // end of searchCoilGdsYdOutDiaDiffCheck
	
	/**
	 *      [A] 오퍼레이션명 : 제품폭 Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilYdGdsWidthDiffCheck(String szStlNo,  String szYdStkLyrNo,String lWidth, String szLeftStlNo,String lLeftWidth, String szRightStlNo,String lRightWidth) throws JDTOException  {
		
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilYdGdsWidthDiffCheck";
		String szOperationName	=	"코일제품야드 폭 Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		 
		
		double   lngWidth   		= 0;           //코일폭
		double   lngLeftWidth   	= 0;           //좌측코일폭
		double   lngRighdWidth  	= 0;           //우측코일폭
		

		try {
			
//			if("".equals(szLeftStlNo) || "".equals(szRightStlNo)) {
//				return szRtnVal;
//			}
			
			lngWidth 		= Double.parseDouble(lWidth);
			lngLeftWidth 	= Double.parseDouble(lLeftWidth);
			lngRighdWidth 	= Double.parseDouble(lRightWidth);
			
//			좌우재료번호가 이적대상과 동일 한 경우 
			if(szRightStlNo.equals(szStlNo)){
				lngRighdWidth=0;
				szRightStlNo="";
			}else if(szLeftStlNo.equals(szStlNo)){
				lngLeftWidth=0;
				szLeftStlNo="";
			}

			szMsg = "대상재료번호(" + szStlNo + ")=>>대상코일폭:"+lngWidth ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단좌측재료번호(" + szLeftStlNo + ")=>>좌측코일폭:"+lngLeftWidth ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단우측재료번호(" + szRightStlNo + ")=>>우측코일폭:"+lngRighdWidth ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			if ("001".equals(szYdStkLyrNo)){
				// 좌우가 비워있는경우
				if ("".equals(szLeftStlNo) && "".equals(szRightStlNo)) {  // 좌우가 비워있는경우
	        		szRtnVal = YdConstant.RETN_CD_SUCCESS;
	        		
				}else{
					ydUtils.putLog(szSessionName, szMethodName, "확인:"+szStlNo+"Math.abs(lngWidth - lngRighdWidth)" + Math.abs(lngWidth - lngRighdWidth), YdConstant.INFO);
					ydUtils.putLog(szSessionName, szMethodName, "확인:"+szStlNo+"Math.abs(lngWidth - lngLeftWidth))" + Math.abs(lngWidth - lngLeftWidth), YdConstant.INFO);
					
//					좌우코일이 존재 하는 경우
					if (!"".equals(szLeftStlNo) && (!"".equals(szRightStlNo))) {
						if (Math.abs(lngWidth - lngRighdWidth) <= 200 && Math.abs(lngWidth - lngLeftWidth) <= 200) {
			        		szRtnVal = YdConstant.RETN_CD_SUCCESS;
			        	} else {
			        		szRtnVal = YdConstant.RETN_CD_FAILURE;
			        	}
					}else{
						if (Math.abs(lngWidth - lngRighdWidth) <= 200 || Math.abs(lngWidth - lngLeftWidth) <= 200) {
			        		szRtnVal = YdConstant.RETN_CD_SUCCESS;
			        	} else {
			        		szRtnVal = YdConstant.RETN_CD_FAILURE;
			        	}
					}
				}

//				// 좌가 비어 있고 우측에 코일이 있는 경우	
//				} else if ("".equals(szLeftStlNo) && (!"".equals(szRightStlNo))) {
//		        	if (Math.abs(lngWidth - lngRighdWidth) <= 200) {
//		        		szRtnVal = YdConstant.RETN_CD_SUCCESS;
//		        	} else {
//		        		szRtnVal = YdConstant.RETN_CD_FAILURE;
//		        	}
//					
//				// 우가 비어 있고 좌측에 코일이 있는 경우
//				} else if ("".equals(szRightStlNo) && (!"".equals(szLeftStlNo))) {
//		        	if (Math.abs(lngWidth - lngLeftWidth) <= 200) {
//		        		szRtnVal = YdConstant.RETN_CD_SUCCESS;
//		        	} else {
//		        		szRtnVal = YdConstant.RETN_CD_FAILURE;
//		        	}
//				} else {
//					szRtnVal = YdConstant.RETN_CD_FAILURE;
//				}	
			} 
  
 			return szRtnVal;
		} catch(Exception e) {
			szMsg = "폭CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} 	
	
	/**
	 *      [A] 오퍼레이션명 : 외경편차1 Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilYdGdsWidthDiffCheck2(String szStlNo,  String szYdStkLyrNo, String lOutDia, String szLeftStlNo,String lLeftOutDia, String szRightStlNo,String lRightOutDia) throws JDTOException  {
		
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilYdGdsWidthDiffCheck2";
		String szOperationName	=	"코일제품야드 2단 외경편차1 Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
				
		long   lngOutDia   		= 0;           //코일외경
		long   lngLeftOutDia   	= 0;           //좌측코일외경
		long   lngRightOutDia  	= 0;           //우측코일외경
		
		try {
			
			if("".equals(szLeftStlNo) || "".equals(szRightStlNo)) {
				return szRtnVal;
			}
//외경편차			
			lngOutDia 		= Long.parseLong(lOutDia);
			lngLeftOutDia 	= Long.parseLong(lLeftOutDia);
			lngRightOutDia 	= Long.parseLong(lRightOutDia);
			
//		if (Math.abs(lngLeftOutDia - lngRightOutDia) < 200) {  
			if (Math.abs(lngLeftOutDia - lngRightOutDia) < 180) {  
				szRtnVal = YdConstant.RETN_CD_SUCCESS;
			} else {
				szRtnVal = YdConstant.RETN_CD_FAILURE;
			}
			
			szMsg = "대상재료번호(" + szStlNo + ")=>>대상코일외경:"+lngOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단좌측재료번호(" + szLeftStlNo + ")=>>좌측코일외경:"+lngLeftOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단우측재료번호(" + szRightStlNo + ")=>>우측코일외경:"+lngRightOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
 			return szRtnVal;
		} catch(Exception e) {
			szMsg = "외경CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} 	
	/**
	 *      [A] 오퍼레이션명 : 외경편차2 Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilYdGdsWidthDiffCheck4(String szStlNo,  String szYdStkLyrNo, String lOutDia, String szLeftStlNo,String lLeftOutDia, String szRightStlNo,String lRightOutDia) throws JDTOException  {
		
		
		int intRtnVal         = 0;
		YdEqpDao	ydEqpDao 	= new YdEqpDao();
		
		JDTORecordSet rsResult	= null;
		JDTORecord recInTemp   	= null;
		JDTORecord recOutTemp  	= null;
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilYdGdsWidthDiffCheck4";
		String szOperationName	=	"코일제품야드 2단 외경편차2(1500미만) Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
				
		long   lngOutDia   		= 0;           //코일외경
		long   lngLeftOutDia   	= 0;           //좌측코일외경
		long   lngRightOutDia  	= 0;           //우측코일외경
 		
		try {
			
			if("".equals(szLeftStlNo) || "".equals(szRightStlNo)) {
				return szRtnVal;
			}
//외경편차			
			lngOutDia 		= Long.parseLong(lOutDia);
			lngLeftOutDia 	= Long.parseLong(lLeftOutDia);
			lngRightOutDia 	= Long.parseLong(lRightOutDia);
	
			
			if (Math.abs(lngLeftOutDia) < 1500 || Math.abs(lngRightOutDia) < 1500) {  
				szRtnVal = YdConstant.RETN_CD_FAILURE;
			} else {
				szRtnVal = YdConstant.RETN_CD_SUCCESS;
			}
			

//			150921 hun 아래 하드코딩 BRE로 전환 start
	       	//BRE 사용 조건 READ	
        	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
        	recInTemp 	= JDTORecordFactory.getInstance().create();
        	recInTemp.setField("YD_STK_COL_GP_4STR"	, "");	
        	recInTemp.setField("YD_STK_COL_GP" 		, szYdStkLyrNo);
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB704*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 704);
			if(intRtnVal <= 0) {
				
			} else {
				rsResult.absolute(1);
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord());
				
				szMsg = "BreYDB704 szYdStkLyrNo =" + szYdStkLyrNo + " DEL_YN:"+ydDaoUtils.paraRecChkNull(recOutTemp, "DEL_YN") ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				if("Y".equals(ydDaoUtils.paraRecChkNull(recOutTemp, "DEL_YN"))){
					
					szRtnVal = YdConstant.RETN_CD_SUCCESS;
					return szRtnVal;
				}else if("N".equals(ydDaoUtils.paraRecChkNull(recOutTemp, "DEL_YN"))){
					
					szRtnVal = YdConstant.RETN_CD_FAILURE;
					return szRtnVal;
				}
			}	
			/*
			if(szYdStkLyrNo.equals("JB0101")||
				szYdStkLyrNo.equals("JB0102")||
				szYdStkLyrNo.equals("JC0101")||
				szYdStkLyrNo.equals("JC0102")){
				szRtnVal = YdConstant.RETN_CD_FAILURE;
				
				szMsg = "대상재료번호(" + szStlNo + ")=>>제외처리 스판열:"+szYdStkLyrNo ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				return szRtnVal;
			}
			*/
//			150921 hun 아래 하드코딩 BRE로 전환 end
			szMsg = "대상재료번호(" + szStlNo + ")=>>대상코일외경:"+lngOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단좌측재료번호(" + szLeftStlNo + ")=>>좌측코일외경:"+lngLeftOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "1단우측재료번호(" + szRightStlNo + ")=>>우측코일외경:"+lngRightOutDia ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
 			return szRtnVal;
		} catch(Exception e) {
			szMsg = "외경(1500미만)CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} 	

	/**
	 *      [A] 오퍼레이션명 : 폭간섭 Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilYdGdsWidthDiffCheck3( String szStlNo, String szYdStkColGp, String szYdStkBedNo, String szYdStkLyrNo , String sYD_STK_COL_W_GP, String szWidth , String szOutDia , String szMaxBedNo ) throws JDTOException  {
		
		int intRtnVal         = 0;
		YdEqpDao	ydEqpDao 	= new YdEqpDao();
		YdStkLyrDao    ydStkLyrDao = new YdStkLyrDao();
		JDTORecordSet rsResult	= null;
		JDTORecord recInTemp   	= null;
		JDTORecord recOutTemp  	= null;
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilYdGdsWidthDiffCheck3";
		String szOperationName	=	"코일제품야드 1단 폭간섭 Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		String szYelGp 		= "";		
		String sYel 		= "";		
		String sSpan 		= "";		
		String sOutDiaGp	= "";
		String sBunGp       = "";
		String sBun         = "";
		
		long   lngOutDia   	= 0;           //코일외경
		
		double dblWidth 	= 0;  // 폭 
		String sWidth_GP 	= "";
		String sMaxBedNo 	= "";
		double dblMAX_W 	= 0;
		double dblWidthGap 	= 0;
		
		try {
			
//외경편차			
			lngOutDia 		= Long.parseLong(szOutDia);
			dblWidth 		= Double.parseDouble(szWidth);
			sWidth_GP		= sYD_STK_COL_W_GP;
			sMaxBedNo 		= szMaxBedNo;
			sBun            = szYdStkBedNo;
			
			
			szMsg = "대상재료번호(" + szStlNo + ")=>>대상코일외경:"+lngOutDia + " 대상코일폭:"+dblWidth + " 대상코일폭구분:"+sWidth_GP + " 대상열max:"+szMaxBedNo ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "대상재료번호(" + szStlNo + ")=>>적치열 구분:"+szYdStkColGp + " 번지:"+szYdStkBedNo + " 단:"+szYdStkLyrNo ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			if (szYdStkColGp.equals("")) {  
				szRtnVal = YdConstant.RETN_CD_FAILURE;
				return szRtnVal;
			}
			if (sWidth_GP.equals("")) {  
				szRtnVal = YdConstant.RETN_CD_FAILURE;
				return szRtnVal;
			}
			
//			150921 hun 아래 하드코딩 BRE로 전환 start
	       	//BRE 사용 조건 READ	
        	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
        	recInTemp 	= JDTORecordFactory.getInstance().create();
        	recInTemp.setField("YD_STK_COL_GP_4STR"	, szYdStkColGp.substring(2,6));	
        	recInTemp.setField("YD_STK_COL_GP" 		, szYdStkColGp);
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB704*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 704);
			if(intRtnVal <= 0) {
				
			} else {
				rsResult.absolute(1);
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord());
				
				szMsg = "BreYDB704 ColGp =" + szYdStkColGp + " DEL_YN:"+ydDaoUtils.paraRecChkNull(recOutTemp, "DEL_YN") ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				if("Y".equals(ydDaoUtils.paraRecChkNull(recOutTemp, "DEL_YN"))){
					
					szRtnVal = YdConstant.RETN_CD_SUCCESS;
					return szRtnVal;
				}else if("N".equals(ydDaoUtils.paraRecChkNull(recOutTemp, "DEL_YN"))){
					
					szRtnVal = YdConstant.RETN_CD_FAILURE;
					return szRtnVal;
				}
			}	
 
//			150921 hun 아래 하드코딩 BRE로 전환 end
			
			sSpan			= szYdStkColGp.substring(0,4);
			sYel			= szYdStkColGp.substring(4,6);
			if(szYdStkBedNo.equals("01")){
				sBunGp = "S";
			} else if(szYdStkBedNo.equals(sMaxBedNo)){
				sBunGp = "E";
			} else {
				sBunGp = "M";
			}
			
	       	//BRE 사용 조건 READ	
        	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
        	recInTemp 	= JDTORecordFactory.getInstance().create();
        	recInTemp.setField("YD_STK_COL_GP" 	, szYdStkColGp);	
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB800*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 800);
			if(intRtnVal <= 0) {
				szYelGp	= "M";		
			} else {
				rsResult.absolute(1);
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord());
				szYelGp	= ydDaoUtils.paraRecChkNull(recOutTemp, "CD_GP");	//첫열(S) 및 마지막 열(E) read		
			}	

			szMsg = "폭구분(" + sWidth_GP + " 대상코일폭:"+dblWidth + " 대상코일폭구분:"+sWidth_GP + " 대상열max:"+szMaxBedNo ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			if(sWidth_GP.equals("M")) { 
				if (dblWidth > 1040) {  // 보폭존이고 폭인 1040이상
					ydUtils.putLog(szSessionName, szMethodName, "lngOutDia1" + lngOutDia, YdConstant.INFO);
					if(lngOutDia < 1281) {
						sOutDiaGp = "A";
					} else if(lngOutDia < 1931) {
						sOutDiaGp = "B";
					} else {
						sOutDiaGp = "C";
					}
				} else {
					ydUtils.putLog(szSessionName, szMethodName, "정상", YdConstant.INFO);
					
					szRtnVal = YdConstant.RETN_CD_SUCCESS;
					return szRtnVal;

				}
			}
			if(sWidth_GP.equals("L")) { 
				if	(dblWidth > 1640) {  // 광폭존이고 폭인 1640이상
					ydUtils.putLog(szSessionName, szMethodName, "lngOutDia2" + lngOutDia, YdConstant.INFO);
					if(lngOutDia < 1281) {
						sOutDiaGp = "A";
					} else if(lngOutDia < 1931) {
						sOutDiaGp = "B";
					} else {
						sOutDiaGp = "C";
					}
				} else {	
					szRtnVal = YdConstant.RETN_CD_SUCCESS;
					return szRtnVal;
				}
			}	

        	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
        	recInTemp 	= JDTORecordFactory.getInstance().create();
        	recInTemp.setField("YEL_GP" 			, szYelGp);	
        	recInTemp.setField("YEL" 				, sYel);	
        	recInTemp.setField("DIA_GP" 			, sOutDiaGp);	
           	recInTemp.setField("BUN_GP" 			, sBunGp);	
          	recInTemp.setField("BUN" 				, sBun);	
          	recInTemp.setField("YD_STK_COL_GP_SPAN" , sSpan);	
          	recInTemp.setField("STL_NO" 			, szStlNo);	

	    	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilCOMMMaxWidth*/
        	intRtnVal = ydStkLyrDao.getYdStklyr(recInTemp, rsResult, 500);
			if(intRtnVal <= 0) {
				return szRtnVal = YdConstant.RETN_CD_FAILURE;
			} else {
				rsResult.absolute(1);
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord());
				dblMAX_W	= ydDaoUtils.paraRecChkNullDouble(recOutTemp, "COIL_W");
		 
				szMsg = "폭간섭위치 최대폭 대상(STL_NO:" + ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO")
													 +" 위치:"+ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_COL_GP")
													 +ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_NO")
													 +ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_LYR_NO")
													 +")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			}	

			szMsg = "폭간섭위치 최대폭" + dblMAX_W+" ROWCNT:"+intRtnVal   ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			if(dblMAX_W == 0){
				
				szMsg = "대상폭간격" + dblWidth + "폭구분" + sWidth_GP  ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
						 
					if( "JH0601".equals(szYdStkColGp)){ 
						//||"JH4806".equals(szYdStkColGp)){ //2017.09.25 적용
						
						szMsg = "확인:"+szStlNo+"대상코일폭:" + dblWidth + "<= 1580 >> 970mm  예외열대상:" + szYdStkColGp  ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
						
							if(dblWidth <= 1580){  //970mm 대상
								szRtnVal = YdConstant.RETN_CD_SUCCESS;
							} else {
								szRtnVal = YdConstant.RETN_CD_FAILURE;
							}
					}else if( "JD0301".equals(szYdStkColGp)
							|| "JE0301".equals(szYdStkColGp)
							|| "JF0301".equals(szYdStkColGp)
							|| "JE0701".equals(szYdStkColGp) 
							|| "JE0702".equals(szYdStkColGp)
							
							|| "JD5207".equals(szYdStkColGp)	
							|| "JE5207".equals(szYdStkColGp)
							|| "JF5207".equals(szYdStkColGp)
							|| "JE4702".equals(szYdStkColGp)
							|| "JE4701".equals(szYdStkColGp)
							){
							
							szMsg = "확인:"+szStlNo+"대상코일폭:" + dblWidth + "<= 1440 >> 900mm  예외열대상:" + szYdStkColGp  ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
							
								if(dblWidth <= 1440){  //900mm 대상
									szRtnVal = YdConstant.RETN_CD_SUCCESS;
								} else {
									szRtnVal = YdConstant.RETN_CD_FAILURE;
								}
						}else if("JG0301".equals(szYdStkColGp) 
								|| "JH0301".equals(szYdStkColGp) 
								
								|| "JG5207".equals(szYdStkColGp)
								|| "JH5207".equals(szYdStkColGp)
								){
						
						szMsg = "확인:"+szStlNo+"대상코일폭:" + dblWidth + "<= 1400 >> 880mm  예외열대상:" + szYdStkColGp  ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
						
							if(dblWidth <= 1400){  //880mm 대상
								szRtnVal = YdConstant.RETN_CD_SUCCESS;
							} else {
								szRtnVal = YdConstant.RETN_CD_FAILURE;
							}
					}else if("JF0207".equals(szYdStkColGp) 
							|| "JG0207".equals(szYdStkColGp) 
							
							|| "JF5401".equals(szYdStkColGp) 
							){
						
						szMsg = "확인:"+szStlNo+"대상코일폭:" + dblWidth + "<= 1380 >> 870mm  예외열대상:" + szYdStkColGp  ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
							if(dblWidth <= 1380){  //870mm 대상
								szRtnVal = YdConstant.RETN_CD_SUCCESS;
							} else {
								szRtnVal = YdConstant.RETN_CD_FAILURE;
							}
					}else if("JD0207".equals(szYdStkColGp) 
							|| "JE0207".equals(szYdStkColGp)
							|| "JD0506".equals(szYdStkColGp)
							|| "JE0506".equals(szYdStkColGp)
							|| "JF0506".equals(szYdStkColGp)
							|| "JG0506".equals(szYdStkColGp)
							|| "JH0506".equals(szYdStkColGp)
							
							|| "JD5401".equals(szYdStkColGp)
							|| "JE5401".equals(szYdStkColGp)
							|| "JD5001".equals(szYdStkColGp)
							|| "JE5001".equals(szYdStkColGp)
							|| "JF5001".equals(szYdStkColGp)
							|| "JG5001".equals(szYdStkColGp)
							|| "JH5001".equals(szYdStkColGp)
					          ){
						
						szMsg = "확인:"+szStlNo+"대상코일폭:" + dblWidth + "<= 1340 >> 850mm  예외열대상:" + szYdStkColGp  ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
							if(dblWidth <= 1340){  //850mm 대상
								szRtnVal = YdConstant.RETN_CD_SUCCESS;
							} else {
								szRtnVal = YdConstant.RETN_CD_FAILURE;
							}
					}else if("JH0207".equals(szYdStkColGp) ){
						
						szMsg = "확인:"+szStlNo+"대상코일폭:" + dblWidth + "<= 1300 >> 830mm  예외열대상:" + szYdStkColGp  ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
							if(dblWidth <= 1300){  //830mm 대상
								szRtnVal = YdConstant.RETN_CD_SUCCESS;
							} else {
								szRtnVal = YdConstant.RETN_CD_FAILURE;
							}		
					}else {	
						if(sWidth_GP.equals("M")){
							szMsg = "대상코일폭:" + dblWidth + "<= 1600 >> 1000mm  보폭:"   ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
							if(dblWidth <= 1600){  //1000mm 대상
								szRtnVal = YdConstant.RETN_CD_SUCCESS;
							} else {
								szRtnVal = YdConstant.RETN_CD_FAILURE;
							}	
						}else {
							szMsg = "확인:"+szStlNo+"대상코일폭:" + dblWidth + "<= 2100 >> 1500mm  광폭:"   ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
							if(dblWidth <= 2100){  //1500mm 대상
								szRtnVal = YdConstant.RETN_CD_SUCCESS;
							} else {
								szRtnVal = YdConstant.RETN_CD_FAILURE;
							}
						}
						 
					}
			} else {

				dblWidthGap = Math.abs(((dblWidth / 2) - 500) + ((dblMAX_W / 2) - 500)) ;

				szMsg = "최종폭간격" + dblWidthGap + "폭구분" + sWidth_GP  ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
//					150922 hun 보폭 광폭으로 변경 ( 예외처리 삭제 )
					if((szYdStkColGp.substring(2,4).equals("55")) //스판변환 6400208
							&& (  szYdStkColGp.substring(0,2).equals("JD") 
								||szYdStkColGp.substring(0,2).equals("JE")
								||szYdStkColGp.substring(0,2).equals("JF")
								||szYdStkColGp.substring(0,2).equals("JG")
								||szYdStkColGp.substring(0,2).equals("JH")) ){						
						szMsg = "확인:"+szStlNo+"최종폭간격:" + dblWidthGap + "< 951 >> 1600mm  보폭열 예외열대상(55스판):" + szYdStkColGp  ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
						if(dblWidthGap < 951){  //1600mm 대상
							szRtnVal = YdConstant.RETN_CD_SUCCESS;
						} else {
							szRtnVal = YdConstant.RETN_CD_FAILURE;
						}
					}else if(((   szYdStkColGp.substring(2,6).equals("5402")
								||szYdStkColGp.substring(2,6).equals("5403")
								||szYdStkColGp.substring(2,6).equals("5404")
								||szYdStkColGp.substring(2,6).equals("5405")
								||szYdStkColGp.substring(2,6).equals("5406")
								)//스판변환 6400208
							
							   && ( szYdStkColGp.substring(0,2).equals("JG")||szYdStkColGp.substring(0,2).equals("JH")))
							||
							((szYdStkColGp.substring(2,4).equals("48"))//스판변환 6400208
							   && (   szYdStkColGp.substring(0,2).equals("JD") 
									||szYdStkColGp.substring(0,2).equals("JE")
									||szYdStkColGp.substring(0,2).equals("JF")
									||szYdStkColGp.substring(0,2).equals("JG")
									||szYdStkColGp.substring(0,2).equals("JH")) && !(szYdStkColGp.substring(4,6).equals("01")))
							||
							((szYdStkColGp.substring(2,4).equals("56")||szYdStkColGp.substring(2,4).equals("57")||szYdStkColGp.substring(2,4).equals("58"))//스판변환  
							   && (   szYdStkColGp.substring(0,2).equals("JA") 
									||szYdStkColGp.substring(0,2).equals("JB")
									||szYdStkColGp.substring(0,2).equals("JC")
									||szYdStkColGp.substring(0,2).equals("JD")
									||szYdStkColGp.substring(0,2).equals("JE")
									||szYdStkColGp.substring(0,2).equals("JF")
									||szYdStkColGp.substring(0,2).equals("JG")
									||szYdStkColGp.substring(0,2).equals("JH"))  //신규열 추가 작업에 따른 예외처리 2019.02.11
									)		
							||
							((szYdStkColGp.substring(2,6).equals("0505") ||szYdStkColGp.substring(2,4).equals("06"))//스판변환 6400208 가상스판 포함 
							   && ( szYdStkColGp.substring(0,2).equals("JB") 
								  ||szYdStkColGp.substring(0,2).equals("JC") ))	
					       ){						
						szMsg = "확인:"+szStlNo+"최종폭간격:" + dblWidthGap + "< 851 >> 1500mm  보폭열 예외열대상(G,H 54스판 / 48스판/ 56,57,58스판):" + szYdStkColGp  ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
						if(dblWidthGap < 851){  //1500mm 대상
							szRtnVal = YdConstant.RETN_CD_SUCCESS;
						} else {
							szRtnVal = YdConstant.RETN_CD_FAILURE;
						}
					}else if(((   szYdStkColGp.substring(2,6).equals("5206")								
								||szYdStkColGp.substring(2,6).equals("5205")
								||szYdStkColGp.substring(2,6).equals("5204")
								||szYdStkColGp.substring(2,6).equals("5203")
								||szYdStkColGp.substring(2,6).equals("5202")
								||szYdStkColGp.substring(2,6).equals("5401")
					           )//스판변환 6400208
							   && ( szYdStkColGp.substring(0,2).equals("JG")||szYdStkColGp.substring(0,2).equals("JH")))
								 
						       ){						
							szMsg = "확인:"+szStlNo+"최종폭간격:" + dblWidthGap + "< 351 >> 1000mm  보폭열 예외열대상(G,H 52스판):" + szYdStkColGp  ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
							if(dblWidthGap < 351){  //1000mm 대상
								szRtnVal = YdConstant.RETN_CD_SUCCESS;
							} else {
								szRtnVal = YdConstant.RETN_CD_FAILURE;
							}
					}else if((( szYdStkColGp.substring(2,6).equals("4801") ) //서승석 주임 요청 2016.03.03
						   && ( szYdStkColGp.substring(0,2).equals("JG")||szYdStkColGp.substring(0,2).equals("JF")||szYdStkColGp.substring(0,2).equals("JD")))							 
					       ){						
						szMsg = "확인:"+szStlNo+"최종폭간격:" + dblWidthGap + "< 651 >> 1300mm  보폭열 예외열대상(D,F,G 48스판1열):" + szYdStkColGp  ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
						if(dblWidthGap < 651){  //1300mm 대상
							szRtnVal = YdConstant.RETN_CD_SUCCESS;
						} else {
							szRtnVal = YdConstant.RETN_CD_FAILURE;
						}
				   }else{

						if(sWidth_GP.equals("M")){
							if(dblWidthGap < 351){ //1000mm 대상
								szMsg = "확인:"+szStlNo+"최종폭간격:" + dblWidthGap + "< 351 >> 1000mm  보폭열:" + szYdStkColGp  ;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
								szRtnVal = YdConstant.RETN_CD_SUCCESS;
							} else {
								szRtnVal = YdConstant.RETN_CD_FAILURE;
							}
						} else {
							if(dblWidthGap < 851){ //1500mm 대상
								szMsg = "확인:"+szStlNo+"최종폭간격:" + dblWidthGap + "< 851 >> 1500mm  광폭열:" + szYdStkColGp  ;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
								szRtnVal = YdConstant.RETN_CD_SUCCESS;
							} else {
								szRtnVal = YdConstant.RETN_CD_FAILURE;
							}
						}
					}
			}
				
			
			return szRtnVal;

		} catch(Exception e) {
			szMsg = "확인:"+szStlNo+"1단 폭간섭 Check 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} 	
		

	/**
	 *      [A] 오퍼레이션명 : 폭간섭 Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilYdGdsWidthDiffCheck3ABC( String szStlNo, String szYdStkColGp, String szYdStkBedNo, String szYdStkLyrNo , String sYD_STK_COL_W_GP, String szWidth , String szOutDia , String szMaxBedNo ) throws JDTOException  {
		
		int intRtnVal         = 0;
		YdEqpDao	ydEqpDao 	= new YdEqpDao();
		YdStkLyrDao    ydStkLyrDao = new YdStkLyrDao();
		JDTORecordSet rsResult	= null;
		JDTORecord recInTemp   	= null;
		JDTORecord recOutTemp  	= null;
		
		String szMsg			=	"";
		String szMethodName		=	"searchCoilYdGdsWidthDiffCheck3ABC";
		String szOperationName	=	"코일제품야드 고정skid 1단 폭간섭 Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		String szYelGp 		= "";		
		String sYel 		= "";		
		String sSpan 		= "";		
		String sOutDiaGp	= "";
		String sBunGp       = "";
		String sBun         = "";
		
		long   lngOutDia   	= 0;           //코일외경
		
		double dblWidth 	= 0;  // 폭 
		String sWidth_GP 	= "";
		String sMaxBedNo 	= "";
		double dblMAX_W 	= 0;
		double dblWidthGap 	= 0;
		
		try {
			
//외경편차			
			lngOutDia 		= Long.parseLong(szOutDia);
			dblWidth 		= Double.parseDouble(szWidth);
			sWidth_GP		= sYD_STK_COL_W_GP;
			sMaxBedNo 		= szMaxBedNo;
			sBun            = szYdStkBedNo;
			
			
			szMsg = "대상재료번호(" + szStlNo + ")=>>대상코일외경:"+lngOutDia + " 대상코일폭:"+dblWidth + " 대상코일폭구분:"+sWidth_GP + " 대상열max:"+szMaxBedNo ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			szMsg = "대상재료번호(" + szStlNo + ")=>>적치열 구분:"+szYdStkColGp + " 번지:"+szYdStkBedNo + " 단:"+szYdStkLyrNo ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			if (szYdStkColGp.equals("")) {  
				szRtnVal = YdConstant.RETN_CD_FAILURE;
				return szRtnVal;
			}
			if (sWidth_GP.equals("")) {  
				szRtnVal = YdConstant.RETN_CD_FAILURE;
				return szRtnVal;
			}
			
//			150921 hun 아래 하드코딩 BRE로 전환 start
	       	//BRE 사용 조건 READ	
        	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
        	recInTemp 	= JDTORecordFactory.getInstance().create();
        	recInTemp.setField("YD_STK_COL_GP_4STR"	, szYdStkColGp.substring(2,6));	
        	recInTemp.setField("YD_STK_COL_GP" 		, szYdStkColGp);
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB704*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 704);
			if(intRtnVal <= 0) {
				
			} else {
				rsResult.absolute(1);
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord());
				
				szMsg = "BreYDB704 ColGp =" + szYdStkColGp + " DEL_YN:"+ydDaoUtils.paraRecChkNull(recOutTemp, "DEL_YN") ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				if("Y".equals(ydDaoUtils.paraRecChkNull(recOutTemp, "DEL_YN"))){
					
					szRtnVal = YdConstant.RETN_CD_SUCCESS;
					return szRtnVal;
				}else if("N".equals(ydDaoUtils.paraRecChkNull(recOutTemp, "DEL_YN"))){
					
					szRtnVal = YdConstant.RETN_CD_FAILURE;
					return szRtnVal;
				}
			}	
			
//			150921 hun 아래 하드코딩 BRE로 전환 end
			
			sSpan			= szYdStkColGp.substring(0,4);
			sYel			= szYdStkColGp.substring(4,6);
			if(szYdStkBedNo.equals("01")){
				sBunGp = "S";
			} else if(szYdStkBedNo.equals(sMaxBedNo)){
				sBunGp = "E";
			} else {
				sBunGp = "M";
			}
			
	       	//BRE 사용 조건 READ	
        	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
        	recInTemp 	= JDTORecordFactory.getInstance().create();
        	recInTemp.setField("YD_STK_COL_GP" 	, szYdStkColGp);	
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB800*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 800);
			if(intRtnVal <= 0) {
				szYelGp	= "M";		
			} else {
				rsResult.absolute(1);
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord());
				szYelGp	= ydDaoUtils.paraRecChkNull(recOutTemp, "CD_GP");	//첫열(S) 및 마지막 열(E) read		
			}	

			szMsg = "폭구분(" + sWidth_GP + " 대상코일폭:"+dblWidth + " 대상코일폭구분:"+sWidth_GP + " 대상열max:"+szMaxBedNo ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			if(sWidth_GP.equals("M")) { 
				if (dblWidth > 1040) {  // 보폭존이고 폭인 1040이상
					ydUtils.putLog(szSessionName, szMethodName, "lngOutDia1" + lngOutDia, YdConstant.INFO);
					if(lngOutDia < 1281) {
						sOutDiaGp = "A";
					} else if(lngOutDia < 1931) {
						sOutDiaGp = "B";
					} else {
						sOutDiaGp = "C";
					}
				} else {
					ydUtils.putLog(szSessionName, szMethodName, "정상", YdConstant.INFO);
					
					szRtnVal = YdConstant.RETN_CD_SUCCESS;
					return szRtnVal;

				}
			}
			if(sWidth_GP.equals("L")) { 
				if	(dblWidth > 1640) {  // 광폭존이고 폭인 1640이상
					ydUtils.putLog(szSessionName, szMethodName, "lngOutDia2" + lngOutDia, YdConstant.INFO);
					if(lngOutDia < 1281) {
						sOutDiaGp = "A";
					} else if(lngOutDia < 1931) {
						sOutDiaGp = "B";
					} else {
						sOutDiaGp = "C";
					}
				} else {	
					szRtnVal = YdConstant.RETN_CD_SUCCESS;
					return szRtnVal;
				}
			}	

        	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
        	recInTemp 	= JDTORecordFactory.getInstance().create();
        	recInTemp.setField("YEL_GP" 			, szYelGp);	
        	recInTemp.setField("YEL" 				, sYel);	
        	recInTemp.setField("DIA_GP" 			, sOutDiaGp);	
           	recInTemp.setField("BUN_GP" 			, sBunGp);	
          	recInTemp.setField("BUN" 				, sBun);	
          	recInTemp.setField("YD_STK_COL_GP_SPAN" , sSpan);	
          	recInTemp.setField("STL_NO" 			, szStlNo);	

	    	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilCOMMMaxWidth*/
        	intRtnVal = ydStkLyrDao.getYdStklyr(recInTemp, rsResult, 500);
			if(intRtnVal <= 0) {
				return szRtnVal = YdConstant.RETN_CD_FAILURE;
			} else {
				rsResult.absolute(1);
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord());
				dblMAX_W	= ydDaoUtils.paraRecChkNullDouble(recOutTemp, "COIL_W");
				
				szMsg = "폭간섭위치 최대폭 대상(고정)(STL_NO:" + ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO")
														 +" 위치:"+ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_COL_GP")
														 +ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_NO")
														 +ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_LYR_NO")
														 +")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			}	

			szMsg = "폭간섭위치 최대폭(고정)" + dblMAX_W+" ROWCNT:"+intRtnVal  ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			if(dblMAX_W == 0){
				szMsg = "대상폭간격" + dblWidth + "폭구분" + sWidth_GP  ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);						 
					 	
						if(sWidth_GP.equals("M")){
							
							szMsg = "확인:"+szStlNo+"대상코일폭(고정):" + dblWidth + "<= 1600 >> 1140mm  보폭:"   ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
							if(dblWidth <= 1600){  //1140mm 대상
								szRtnVal = YdConstant.RETN_CD_SUCCESS;
							} else {
								szRtnVal = YdConstant.RETN_CD_FAILURE;
							}
							
						}else {
							szMsg = "확인:"+szStlNo+"대상코일폭(고정):" + dblWidth + "<= 2100 >> 1500mm  광폭:"   ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
							if(dblWidth <= 2100){  //1500mm 대상
								szRtnVal = YdConstant.RETN_CD_SUCCESS;
							} else {
								szRtnVal = YdConstant.RETN_CD_FAILURE;
							}
						}
					 
			} else {

				dblWidthGap = Math.abs(((dblWidth / 2) - 500) + ((dblMAX_W / 2) - 500)) ;

				szMsg = "최종폭간격" + dblWidthGap + "폭구분" + sWidth_GP  ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
									
//					150922 hun ABC동 보폭 420 으로 적용 
				if((szYdStkColGp.substring(2,4).equals("55")) //스판변환 6400208
						&& (  szYdStkColGp.substring(0,2).equals("JD") 
							||szYdStkColGp.substring(0,2).equals("JE")
							||szYdStkColGp.substring(0,2).equals("JF")
							||szYdStkColGp.substring(0,2).equals("JG")
							||szYdStkColGp.substring(0,2).equals("JH")) ){						
					szMsg = "확인:"+szStlNo+"최종폭간격(고정):" + dblWidthGap + "< 951 >> 1600mm  보폭열 예외열대상(1스판):" + szYdStkColGp  ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					if(dblWidthGap < 951){  //1600mm 대상
						szRtnVal = YdConstant.RETN_CD_SUCCESS;
					} else {
						szRtnVal = YdConstant.RETN_CD_FAILURE;
					}
				}else if(((   szYdStkColGp.substring(2,6).equals("5402")
							||szYdStkColGp.substring(2,6).equals("5403")
							||szYdStkColGp.substring(2,6).equals("5404")
							||szYdStkColGp.substring(2,6).equals("5405")
							||szYdStkColGp.substring(2,6).equals("5406")
							)//스판변환 6400208
						
						   && ( szYdStkColGp.substring(0,2).equals("JG")||szYdStkColGp.substring(0,2).equals("JH")))
						||
						((szYdStkColGp.substring(2,4).equals("48"))//스판변환 6400208
						   && (   szYdStkColGp.substring(0,2).equals("JD") 
								||szYdStkColGp.substring(0,2).equals("JE")
								||szYdStkColGp.substring(0,2).equals("JF")
								||szYdStkColGp.substring(0,2).equals("JG")
								||szYdStkColGp.substring(0,2).equals("JH")))
						||
						((szYdStkColGp.substring(2,4).equals("56")||szYdStkColGp.substring(2,4).equals("57")||szYdStkColGp.substring(2,4).equals("58"))//스판변환  
								   && (   szYdStkColGp.substring(0,2).equals("JA") 
										||szYdStkColGp.substring(0,2).equals("JB")
										||szYdStkColGp.substring(0,2).equals("JC")
										||szYdStkColGp.substring(0,2).equals("JD")
										||szYdStkColGp.substring(0,2).equals("JE")
										||szYdStkColGp.substring(0,2).equals("JF")))		
						||
						((szYdStkColGp.substring(2,6).equals("0505") ||szYdStkColGp.substring(2,4).equals("06"))//스판변환 6400208 가상스판 포함 
						   && ( szYdStkColGp.substring(0,2).equals("JB") 
							  ||szYdStkColGp.substring(0,2).equals("JC") ))	
				       ){						
					szMsg = "확인:"+szStlNo+"최종폭간격(고정):" + dblWidthGap + "< 851 >> 1500mm  보폭열 예외열대상(G,H 2스판 / 6스판):" + szYdStkColGp  ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					if(dblWidthGap < 851){  //1500mm 대상
						szRtnVal = YdConstant.RETN_CD_SUCCESS;
					} else {
						szRtnVal = YdConstant.RETN_CD_FAILURE;
					}
				}else if(((   szYdStkColGp.substring(2,6).equals("5206")								
							||szYdStkColGp.substring(2,6).equals("5205")
							||szYdStkColGp.substring(2,6).equals("5204")
							||szYdStkColGp.substring(2,6).equals("5203")
							||szYdStkColGp.substring(2,6).equals("5202")
							||szYdStkColGp.substring(2,6).equals("5401")
				           )//스판변환 6400208
						   && ( szYdStkColGp.substring(0,2).equals("JG")||szYdStkColGp.substring(0,2).equals("JH")))
							 
					       ){						
						szMsg = "확인:"+szStlNo+"최종폭간격(고정):" + dblWidthGap + "< 351 >> 1000mm  보폭열 예외열대상(G,H 3스판):" + szYdStkColGp  ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
						if(dblWidthGap < 351){  //1000mm 대상
							szRtnVal = YdConstant.RETN_CD_SUCCESS;
						} else {
							szRtnVal = YdConstant.RETN_CD_FAILURE;
						}
				}else   if(szYdStkColGp.substring(1,4).equals("A51") 
						|| szYdStkColGp.substring(1,4).equals("A52")
						|| szYdStkColGp.substring(1,4).equals("A53")
						|| szYdStkColGp.substring(1,4).equals("A54")
						|| szYdStkColGp.substring(1,4).equals("A55")
					
						|| szYdStkColGp.substring(1,4).equals("B02")
						|| szYdStkColGp.substring(1,4).equals("B03")
						|| szYdStkColGp.substring(1,4).equals("B04")
						|| szYdStkColGp.substring(1,4).equals("B05")
						|| szYdStkColGp.substring(1,4).equals("B06")
						|| szYdStkColGp.substring(1,4).equals("B50")
						|| szYdStkColGp.substring(1,4).equals("B51")
						|| szYdStkColGp.substring(1,4).equals("B52")
						|| szYdStkColGp.substring(1,4).equals("B53")
						|| szYdStkColGp.substring(1,4).equals("B54")
						|| szYdStkColGp.substring(1,4).equals("B55")
						
						|| szYdStkColGp.substring(1,4).equals("C02")
						|| szYdStkColGp.substring(1,4).equals("C03")
						|| szYdStkColGp.substring(1,4).equals("C04")
						|| szYdStkColGp.substring(1,4).equals("C05")
						|| szYdStkColGp.substring(1,4).equals("C06")						 
						|| szYdStkColGp.substring(1,6).equals("C5105")
						|| szYdStkColGp.substring(1,4).equals("C52")
						|| szYdStkColGp.substring(1,4).equals("C53")
						|| szYdStkColGp.substring(1,4).equals("C54")
						|| szYdStkColGp.substring(1,4).equals("C55")
					){						
						szMsg = "확인:"+szStlNo+"최종폭간격(고정):" + dblWidthGap + "< 851 >> 1500mm  보폭열 예외열대상(2~6,50~55스판):" + szYdStkColGp  ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
						if(dblWidthGap < 851){  //1500mm 대상
							szRtnVal = YdConstant.RETN_CD_SUCCESS;
						} else {
							szRtnVal = YdConstant.RETN_CD_FAILURE;
						}
					}else{
						if(sWidth_GP.equals("M")){
							szMsg = "확인:"+szStlNo+"최종폭간격(고정):" + dblWidthGap + "< 491 >> 1140mm  보폭열:" + szYdStkColGp  ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
							
//							151229 hun 확장스판 58 이상    (dblWidthGap < 351 ) 1000mm 기준 적용
							if(Integer.parseInt(szYdStkColGp.substring(2,4))>57 && isNumeric(szYdStkColGp.substring(2,4))){
								szMsg = "확인:"+szStlNo+"확장스판"+szYdStkColGp.substring(2,4)+" 최종폭간격(고정):" + dblWidthGap + "< 351 >> 1000mm  보폭열:" + szYdStkColGp  ;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
								if(dblWidthGap < 351){  //1000mm 대상
									szRtnVal = YdConstant.RETN_CD_SUCCESS;
								} else {
									szRtnVal = YdConstant.RETN_CD_FAILURE;
								}
							}else{
								szMsg = "확인:"+szStlNo+"기존스판"+szYdStkColGp.substring(2,4)+" 최종폭간격(고정):" + dblWidthGap + "< 491 >> 1140mm  보폭열:" + szYdStkColGp  ;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
								if(dblWidthGap < 491){								
									szRtnVal = YdConstant.RETN_CD_SUCCESS;
								} else {
									szRtnVal = YdConstant.RETN_CD_FAILURE;
								}
							}
						} else {
							szMsg = "확인:"+szStlNo+"최종폭간격(고정):" + dblWidthGap + "< 851 >> 1500mm  광폭열:" + szYdStkColGp  ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
							if(dblWidthGap < 851){
								szRtnVal = YdConstant.RETN_CD_SUCCESS;
							} else {
								szRtnVal = YdConstant.RETN_CD_FAILURE;
							}
						}
					}
			}
				
			
			return szRtnVal;

		} catch(Exception e) {
			szMsg = "1단 폭간섭 Check 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} 	
	
	public boolean isNumeric(String str){  

	  try  {  

	    double d = Double.parseDouble(str);  

	  }catch(NumberFormatException nfe){  
	    return false;  
	  }  
	  return true;  

	}

		
		
	/**
	 *      [A] 오퍼레이션명 : 코일창고야드좌측코일To위치평점항목Set
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String SearchCoilGdsYdLeftCoilGrade(String szYdStkColGp, String szYdStkBedNo, String szYdStkLyrNo, String szStlNo, String szLeftColGp, String szLeftBedNo, String szLeftLyrNo, String szLeftStlNo) throws JDTOException  {

		YdStkLyrDao    ydStkLyrDao = new YdStkLyrDao();
		//코일공통 DAO
		PtOsCommDao ptOsCommDao     = new PtOsCommDao();
		
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"SearchCoilGdsYdLeftCoilGrade";
		String szOperationName	=	"코일창고야드좌측코일To위치평점항목Set";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		JDTORecord recPara 		= null;
		
		JDTORecordSet rsLyr     = null;
		JDTORecord recLyr       = null;
		
		JDTORecordSet outRecSet = null;
		JDTORecord recCoil      = null;
		
		String szCurrProgCd 	= "";
		String szOrdYeojaeGp 	= "";
		String szOrdNo 			= "";
		String szOrdDtl = "";
		String szDestCd = "";
		String szOrdGp = "";
		String szTransOrder = "";

		String sCOIL_OUTDIA  = "";
		long   lngOutDia 	= 0;
		String sCOIL_W 		= "";
		double dblWidth 	= 0;  // 폭
		String sCUST_CD = "";
		
		String szLeftCurrProgCd = "";
		String szLeftOrdYeojaeGp = "";
		String szLeftOrdNo = "";
		String szLeftOrdDtl = "";
		String szLeftDestCd = "";
		String szLeftOrdGp = "";
		String szLeftTransOrder = "";
		
		String szLeftGrade = "9";
		
		String sLeftCOIL_OUTDIA  = "";
		String sLeftCOIL_W = "";
		String sLeftCUST_CD = "";
		long   lngLeftOutDia = 0;
		double dblLeftWidth  =	0;  // 폭
		
		try {
						
			// 좌측적치단정보
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szLeftColGp);
			recPara.setField("YD_STK_BED_NO", szLeftBedNo);
			recPara.setField("YD_STK_LYR_NO", szLeftLyrNo);
			rsLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsLyr, 0);
			
        	if (intRtnVal <= 0) {
				szMsg = "확인:"+szStlNo+"좌측적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			}
        	
        	rsLyr.first();
        	recLyr = rsLyr.getRecord();

//1단좌측코일상태-공Bed. 적치불가
        	
        	if("001".equals(szYdStkLyrNo) && ("X".equals(ydDaoUtils.paraRecChkNull(recLyr, "YD_STK_LYR_MTL_STAT")) 
                    					   || "E".equals(ydDaoUtils.paraRecChkNull(recLyr, "YD_STK_LYR_MTL_STAT")))) {
        		return szLeftGrade = "E";
        	}
        	
        	// 스케줄재료
        	recPara	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szStlNo);
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtCoilCommByCoilNo*/
			intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet, 19);
			
			//리턴값 메세지처리
			if (intRtnVal == 0) {
				szMsg = "재료번호(" + szStlNo + ")에 대한 코일공통데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			} else if (intRtnVal < 0) {
				szMsg = "재료번호(" + szStlNo + ")로 코일공통데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			}
			
			outRecSet.absolute(1);
			recCoil = outRecSet.getRecord();
			
			szCurrProgCd 	= ydDaoUtils.paraRecChkNull(recCoil, "CURR_PROG_CD");   	//진도코드
			szOrdYeojaeGp 	= ydDaoUtils.paraRecChkNull(recCoil, "ORD_YEOJAE_GP");		//주여구분
			szOrdNo 		= ydDaoUtils.paraRecChkNull(recCoil, "ORD_NO");				//주문번호
			szOrdDtl 		= ydDaoUtils.paraRecChkNull(recCoil, "ORD_DTL");			//주문행번
			szDestCd 		= ydDaoUtils.paraRecChkNull(recCoil, "DEST_CD");			//목적지
			szOrdGp 		= ydDaoUtils.paraRecChkNull(recCoil, "ORD_GP");				//수주구분
			szTransOrder 	= ydDaoUtils.paraRecChkNull(recCoil, "TRANS_ORDER");		//운송지시
		
			sCOIL_OUTDIA  	= ydDaoUtils.paraRecChkNull(recCoil, "COIL_OUTDIA");        //외경	
		 	lngOutDia = Long.parseLong(sCOIL_OUTDIA);
        	
			sCOIL_W			= ydDaoUtils.paraRecChkNull(recCoil, "COIL_W");				//폭
			dblWidth 		= Double.parseDouble(sCOIL_W);
			
			sCUST_CD 		= ydDaoUtils.paraRecChkNull(recCoil, "CUST_CD");			//고객사	
			
			//--------------------------------------------------------------------------------------------------------------------------
			// 좌측재료
        	recPara	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szLeftStlNo);
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			
			/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtCoilCommByCoilNo*/
			intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet, 19);
			
			//리턴값 메세지처리
			if (intRtnVal == 0) {
				szMsg = "좌측재료번호(" + szLeftStlNo + ")에 대한 코일공통데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			} else if (intRtnVal < 0) {
				szMsg = "좌측재료번호(" + szLeftStlNo + ")로 코일공통데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			}
			
			outRecSet.absolute(1);
			recCoil = outRecSet.getRecord();
			
			
			szLeftCurrProgCd 	= ydDaoUtils.paraRecChkNull(recCoil, "CURR_PROG_CD");	//진도코드
			szLeftOrdYeojaeGp 	= ydDaoUtils.paraRecChkNull(recCoil, "ORD_YEOJAE_GP");	//주여구분
			szLeftOrdNo 		= ydDaoUtils.paraRecChkNull(recCoil, "ORD_NO");			//주문번호
			szLeftOrdDtl 		= ydDaoUtils.paraRecChkNull(recCoil, "ORD_DTL");		//주문행번
			szLeftDestCd 		= ydDaoUtils.paraRecChkNull(recCoil, "DEST_CD");		//목적지
			szLeftOrdGp 		= ydDaoUtils.paraRecChkNull(recCoil, "ORD_GP");			//수주구분
			szLeftTransOrder 	= ydDaoUtils.paraRecChkNull(recCoil, "TRANS_ORDER");	//운송지시
			
			sLeftCOIL_OUTDIA  	= ydDaoUtils.paraRecChkNull(recCoil, "COIL_OUTDIA");        //외경	
			lngLeftOutDia 		= Long.parseLong(sLeftCOIL_OUTDIA);
			
			sLeftCOIL_W			= ydDaoUtils.paraRecChkNull(recCoil, "COIL_W");				//폭
			dblLeftWidth 			= Double.parseDouble(sLeftCOIL_W);
			sLeftCUST_CD 		= ydDaoUtils.paraRecChkNull(recCoil, "CUST_CD");			//고객사	
			
   

//주문재 입고, 이적
	       	if ("1".equals(szOrdYeojaeGp)) {
	       		if("002".equals(szYdStkLyrNo) || (("001".equals(szYdStkLyrNo)) && (Math.abs(lngOutDia - lngLeftOutDia) < 180) 
	       				                                                       && (Math.abs(dblWidth - dblLeftWidth)   < 200))){
        			if ("1".equals(szLeftOrdYeojaeGp)) { // 같은 주문재 인경우 1~5점까지 
        					if(szOrdNo.equals(szLeftOrdNo) && szOrdDtl.equals(szLeftOrdDtl)) {
        						szLeftGrade = "1";
        					} else if (szOrdNo.equals(szLeftOrdNo)) {
        						szLeftGrade = "2";
        					} else if (sCUST_CD.equals(sLeftCUST_CD)) {
        						szLeftGrade = "3";
        					} else if (szOrdGp.equals(szLeftOrdGp)) {
        						szLeftGrade = "4";
        					} else if (szCurrProgCd.equals(szLeftCurrProgCd)) {
        						szLeftGrade = "5";
        					} else{
        						szLeftGrade = "5";
        					}
    				} else {
    					szLeftGrade = "9";
    				}
        		} else {
        			szLeftGrade = "9";
        		}
        	}
	       	
//여재 입고, 이적         	
        	if ("2".equals(szOrdYeojaeGp)) {
	       		if("002".equals(szYdStkLyrNo) || (("001".equals(szYdStkLyrNo)) && (Math.abs(lngOutDia - lngLeftOutDia) < 180) 
                           													   && (Math.abs(dblWidth - dblLeftWidth)   < 200))){
        			if ("2".equals(szLeftOrdYeojaeGp)) {
                		if (szCurrProgCd.equals(szLeftCurrProgCd)) {
                			szLeftGrade = "1";
                		} else {
                			szLeftGrade = "5";
                		}
                	} else {
                		szLeftGrade = "9";
                	}
        		} else {
        			szLeftGrade = "9";
        		}	
        	}
        	
//운송대기/운송지시대기 이적
        	if ("L".equals(szCurrProgCd)||"N".equals(szCurrProgCd)) {
        		if("L".equals(szLeftCurrProgCd)||"N".equals(szLeftCurrProgCd)) {
        			if(!("**".equals(szTransOrder) && "**".equals(szLeftTransOrder))) { //null이면 (*)로처리됨sql문에서
        				if (szTransOrder.equals(szLeftTransOrder)) {
        					szLeftGrade = "1";
        				} else {
        					szLeftGrade = "2";
        				}
        			} else {
        				szLeftGrade = "2";
        			}
        		} else if ("F".equals(szLeftCurrProgCd) ||"G".equals(szLeftCurrProgCd) || "H".equals(szLeftCurrProgCd) || "K".equals(szLeftCurrProgCd) || "Y".equals(szLeftCurrProgCd)|| "Z".equals(szLeftCurrProgCd)) {
        			if ("002".equals(szYdStkLyrNo)) {
        				szLeftGrade = "3";
        			} else {
        				szLeftGrade = "4";
        			}
        		} else {
        			szLeftGrade = "9";
        		}
        	}

//운송대기/운송지시대기 이적(냉연)
        	if ("4".equals(szCurrProgCd)||"6".equals(szCurrProgCd)) {
        		if("4".equals(szLeftCurrProgCd)||"6".equals(szLeftCurrProgCd)) {
        			if(!("**".equals(szTransOrder) && "**".equals(szLeftTransOrder))) { //null이면 (*)로처리됨sql문에서
        				if (szTransOrder.equals(szLeftTransOrder)) {
        					szLeftGrade = "1";
        				} else {
        					szLeftGrade = "2";
        				}
        			} else {
        				szLeftGrade = "2";
        			}
        		} else if ("F".equals(szLeftCurrProgCd) ||"G".equals(szLeftCurrProgCd) || "H".equals(szLeftCurrProgCd) || "2".equals(szLeftCurrProgCd) || "Y".equals(szLeftCurrProgCd)|| "Z".equals(szLeftCurrProgCd)) {
        			if ("002".equals(szYdStkLyrNo)) {
        				szLeftGrade = "3";
        			} else {
        				szLeftGrade = "4";
        			}
        		} else {
        			szLeftGrade = "9";
        		}
        	}
        	
//고간이송대기 이적        	
        	if ("Q".equals(szCurrProgCd)) {
        		if("Q".equals(szLeftCurrProgCd)) {
        			szLeftGrade = "1";
        		} else if ("F".equals(szLeftCurrProgCd) ||"G".equals(szLeftCurrProgCd) || "H".equals(szLeftCurrProgCd) || "K".equals(szLeftCurrProgCd) || "Y".equals(szLeftCurrProgCd)|| "Z".equals(szLeftCurrProgCd)) {
        			szLeftGrade = "4";
        		} else {
        			szLeftGrade = "9";
        		}
        	}
//반납대상 이적        	
        	if ("J".equals(szCurrProgCd)) {
        		if("J".equals(szLeftCurrProgCd)) {
        			szLeftGrade = "1";
        		} else if ("F".equals(szLeftCurrProgCd) ||"G".equals(szLeftCurrProgCd) || "H".equals(szLeftCurrProgCd) || "K".equals(szLeftCurrProgCd) || "Y".equals(szLeftCurrProgCd)|| "Z".equals(szLeftCurrProgCd)) {
        			szLeftGrade = "4";
        		} else {
        			szLeftGrade = "9";
        		}
        	}
        	
			
			return szLeftGrade;
		} catch(Exception e) {
			szMsg = "코일창고야드좌측코일To위치평점항목Set 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} // end of SearchCoilGdsYdLeftCoilGrade
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일창고야드우측코일To위치평점항목Set
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String SearchCoilGdsYdRightCoilGrade(String szYdStkColGp, String szYdStkBedNo, String szYdStkLyrNo, String szStlNo, String szRightColGp, String szRightBedNo, String szRightLyrNo, String szRightStlNo) throws JDTOException  {
		
		YdStkLyrDao    ydStkLyrDao = new YdStkLyrDao();
		//코일공통 DAO
		PtOsCommDao ptOsCommDao     = new PtOsCommDao();
		
		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"SearchCoilGdsYdRightCoilGrade";
		String szOperationName	=	"코일창고야드우측코일To위치평점항목Set";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		
		JDTORecord recPara 		= null;
		
		JDTORecordSet rsLyr     = null;
		JDTORecord recLyr       = null;
		
		JDTORecordSet outRecSet = null;
		JDTORecord recCoil      = null;
		
		String szCurrProgCd = "";
		String szOrdYeojaeGp = "";
		String szOrdNo = "";
		String szOrdDtl = "";
		String szDestCd = "";
		String szOrdGp = "";
		String szTransOrder = "";
		String sCOIL_OUTDIA  = "";
		long   lngOutDia 	= 0;
		String sCOIL_W 		= "";
		double dblWidth 	= 0;  // 폭
		String sCUST_CD = "";
		
		String szRightCurrProgCd = "";
		String szRightOrdYeojaeGp = "";
		String szRightOrdNo = "";
		String szRightOrdDtl = "";
		String szRightDestCd = "";
		String szRightOrdGp = "";
		String szRightTransOrder = "";
		String sRightCOIL_OUTDIA  = "";
		String sRightCOIL_W = "";
		String sRightCUST_CD = "";
		long   lngRightOutDia = 0;
		double dblRightWidth  =	0;  // 폭
		
		String szRightGrade = "9";
		
		
		try {
						
			// 우측적치단정보
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szRightColGp);
			recPara.setField("YD_STK_BED_NO", szRightBedNo);
			recPara.setField("YD_STK_LYR_NO", szRightLyrNo);
			rsLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsLyr, 0);
			
        	if (intRtnVal <= 0) {
				szMsg = "확인:"+szStlNo+"우측적치단(" + szRightColGp + szRightBedNo + szRightLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			}
        	
        	rsLyr.first();
        	recLyr = rsLyr.getRecord();
        	
        	if("001".equals(szYdStkLyrNo) && ("X".equals(ydDaoUtils.paraRecChkNull(recLyr, "YD_STK_LYR_MTL_STAT")) 
        			                       || "E".equals(ydDaoUtils.paraRecChkNull(recLyr, "YD_STK_LYR_MTL_STAT")))) {
        		return szRightGrade = "E";
        	}
        	// 스케줄재료
        	recPara	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szStlNo);
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			
			intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet, 19);
			
			//리턴값 메세지처리
			if (intRtnVal == 0) {
				szMsg = "재료번호(" + szStlNo + ")에 대한 코일공통데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			} else if (intRtnVal < 0) {
				szMsg = "재료번호(" + szStlNo + ")로 코일공통데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			}
			
			outRecSet.absolute(1);
			recCoil = outRecSet.getRecord();
			
			szCurrProgCd 	= ydDaoUtils.paraRecChkNull(recCoil, "CURR_PROG_CD");//진도코드
			szOrdYeojaeGp	= ydDaoUtils.paraRecChkNull(recCoil, "ORD_YEOJAE_GP");//주여구분
			szOrdNo 		= ydDaoUtils.paraRecChkNull(recCoil, "ORD_NO");//주문번호
			szOrdDtl 		= ydDaoUtils.paraRecChkNull(recCoil, "ORD_DTL");//주문행번
			szDestCd 		= ydDaoUtils.paraRecChkNull(recCoil, "DEST_CD");//목적지
			szOrdGp 		= ydDaoUtils.paraRecChkNull(recCoil, "ORD_GP");//수주구분
			szTransOrder 	= ydDaoUtils.paraRecChkNull(recCoil, "TRANS_ORDER");//운송지시
			sCOIL_OUTDIA  	= ydDaoUtils.paraRecChkNull(recCoil, "COIL_OUTDIA");        //외경	
		 	lngOutDia = Long.parseLong(sCOIL_OUTDIA);
        	
			sCOIL_W			= ydDaoUtils.paraRecChkNull(recCoil, "COIL_W");				//폭
			dblWidth 		= Double.parseDouble(sCOIL_W);
			
			sCUST_CD 		= ydDaoUtils.paraRecChkNull(recCoil, "CUST_CD");			//고객사	
			
			//--------------------------------------------------------------------------------------------------------------------------
			// 우측재료
        	recPara	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szRightStlNo);
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			
			intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet, 19);
			
			//리턴값 메세지처리
			if (intRtnVal == 0) {
				szMsg = "좌측재료번호(" + szRightStlNo + ")에 대한 코일공통데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			} else if (intRtnVal < 0) {
				szMsg = "좌측재료번호(" + szRightStlNo + ")로 코일공통데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szRtnVal;
			}
			
			outRecSet.absolute(1);
			recCoil = outRecSet.getRecord();
			
			szRightCurrProgCd 	= ydDaoUtils.paraRecChkNull(recCoil, "CURR_PROG_CD"); //진도코드
			szRightOrdYeojaeGp	= ydDaoUtils.paraRecChkNull(recCoil, "ORD_YEOJAE_GP");//주여구분
			szRightOrdNo 		= ydDaoUtils.paraRecChkNull(recCoil, "ORD_NO");//주문번호
			szRightOrdDtl 		= ydDaoUtils.paraRecChkNull(recCoil, "ORD_DTL");//주문행번
			szRightDestCd 		= ydDaoUtils.paraRecChkNull(recCoil, "DEST_CD");//목적지
			szRightOrdGp 		= ydDaoUtils.paraRecChkNull(recCoil, "ORD_GP");//수주구분
			szRightTransOrder 	= ydDaoUtils.paraRecChkNull(recCoil, "TRANS_ORDER");//운송지시

			sRightCOIL_OUTDIA  	= ydDaoUtils.paraRecChkNull(recCoil, "COIL_OUTDIA");        //외경	
			lngRightOutDia 		= Long.parseLong(sRightCOIL_OUTDIA);
			
			sRightCOIL_W			= ydDaoUtils.paraRecChkNull(recCoil, "COIL_W");				//폭
			dblRightWidth 			= Double.parseDouble(sRightCOIL_W);
			sRightCUST_CD 		= ydDaoUtils.paraRecChkNull(recCoil, "CUST_CD");			//고객사	
       	
//			정품 출하지시대기 입고, 이적 			
//	       	if ("1".equals(szOrdYeojaeGp) && ("F".equals(szCurrProgCd) ||"G".equals(szCurrProgCd) || "H".equals(szCurrProgCd) || "K".equals(szCurrProgCd))) {
//	       		if("002".equals(szYdStkLyrNo) || (("001".equals(szYdStkLyrNo)) && (Math.abs(lngOutDia - lngRightOutDia) < 180) 
//	       				                                                       && (Math.abs(dblWidth - dblRightWidth)   < 200))){
//        			if ("1".equals(szRightOrdYeojaeGp)) {
//        				if ("F".equals(szRightCurrProgCd) ||"G".equals(szRightCurrProgCd) || "H".equals(szRightCurrProgCd) || "K".equals(szRightCurrProgCd)) {
//        					if(szOrdNo.equals(szRightOrdNo) && szOrdDtl.equals(szRightOrdDtl)) {
//        						szRightGrade = "1";
//        					} else if (szOrdNo.equals(szRightOrdNo)) {
//        						szRightGrade = "2";
//        					} else if (sCUST_CD.equals(sRightCUST_CD)) {
//        						szRightGrade = "3";
//        					} else if (szOrdGp.equals(szRightOrdGp)) {
//        						szRightGrade = "4";
//        					} else {
//        						szRightGrade = "5";
//        					}
//        				} else {
//        					szRightGrade = "5";
//        				}
//    				} else {
//    					szRightGrade = "9";
//    				}
//        		} else {
//        			szRightGrade = "9";
//        		}
//        	}
//	       	
////여재 입고, 이적         	
//        	if ("2".equals(szOrdYeojaeGp) && ("F".equals(szCurrProgCd) ||"G".equals(szCurrProgCd) || "H".equals(szCurrProgCd) || "Z".equals(szCurrProgCd))) {
//	       		if("002".equals(szYdStkLyrNo) || (("001".equals(szYdStkLyrNo)) && (Math.abs(lngOutDia - lngRightOutDia) < 180) 
//                           													   && (Math.abs(dblWidth - dblRightWidth)   < 200))){
//        			if ("2".equals(szRightOrdYeojaeGp)) {
//                		if ("F".equals(szRightCurrProgCd) ||"G".equals(szRightCurrProgCd) || "H".equals(szRightCurrProgCd) || "Z".equals(szRightCurrProgCd)) {
//                			szRightGrade = "1";
//                		} else {
//                			szRightGrade = "4";
//                		}
//                	} else {
//                		szRightGrade = "9";
//                	}
//        		} else {
//        			szRightGrade = "9";
//        		}	
//        	}
//        	
////운송대기/운송지시대기 이적
//        	if ("L".equals(szCurrProgCd)||"N".equals(szCurrProgCd)) {
//        		if("L".equals(szRightCurrProgCd)||"N".equals(szRightCurrProgCd)) {
//        			if(!("**".equals(szTransOrder) && "**".equals(szRightTransOrder))) { //null이면 (*)로처리됨sql문에서
//        				if (szTransOrder.equals(szRightTransOrder)) {
//        					szRightGrade = "1";
//        				} else {
//        					szRightGrade = "2";
//        				}
//        			} else {
//        				szRightGrade = "2";
//        			}
//        		} else if ("F".equals(szRightCurrProgCd) ||"G".equals(szRightCurrProgCd) || "H".equals(szRightCurrProgCd) || "K".equals(szRightCurrProgCd) || "Z".equals(szRightCurrProgCd)) {
//        			if ("002".equals(szYdStkLyrNo)) {
//        				szRightGrade = "3";
//        			} else {
//        				szRightGrade = "4";
//        			}
//        		} else {
//        			szRightGrade = "9";
//        		}
//        	}
//        	
////고간이송대기 이적        	
//        	if ("Q".equals(szCurrProgCd)) {
//        		if("Q".equals(szRightCurrProgCd)) {
//        			szRightGrade = "1";
//        		} else if ("F".equals(szRightCurrProgCd) ||"G".equals(szRightCurrProgCd) || "H".equals(szRightCurrProgCd) || "K".equals(szRightCurrProgCd) || "Z".equals(szRightCurrProgCd)) {
//        			szRightGrade = "4";
//        		} else {
//        			szRightGrade = "9";
//        		}
//        	}
////반납대상 이적        	
//        	if ("J".equals(szCurrProgCd)) {
//        		if("J".equals(szRightCurrProgCd)) {
//        			szRightGrade = "1";
//        		} else if ("F".equals(szRightCurrProgCd) ||"G".equals(szRightCurrProgCd) || "H".equals(szRightCurrProgCd) || "K".equals(szRightCurrProgCd) || "Z".equals(szRightCurrProgCd)) {
//        			szRightGrade = "4";
//        		} else {
//        			szRightGrade = "9";
//        		}
//        	}
        	 
			
//주문재 입고, 이적
	       	if ("1".equals(szOrdYeojaeGp)) {
	       		if("002".equals(szYdStkLyrNo) || (("001".equals(szYdStkLyrNo)) && (Math.abs(lngOutDia - lngRightOutDia) < 180) 
	       				                                                       && (Math.abs(dblWidth - dblRightWidth)   < 200))){
        			if ("1".equals(szRightOrdYeojaeGp)) { // 같은 주문재 인경우 1~5점까지 
        					if(szOrdNo.equals(szRightOrdNo) && szOrdDtl.equals(szRightOrdDtl)) {
        						szRightGrade = "1";
        					} else if (szOrdNo.equals(szRightOrdNo)) {
        						szRightGrade = "2";
        					} else if (sCUST_CD.equals(sRightCUST_CD)) {
        						szRightGrade = "3";
        					} else if (szOrdGp.equals(szRightOrdGp)) {
        						szRightGrade = "4";
        					} else if (szCurrProgCd.equals(szRightCurrProgCd)) {
        						szRightGrade = "5";
        					} else{
        						szRightGrade = "5";
        					}
    				} else {
    					szRightGrade = "9";
    				}
        		} else {
        			szRightGrade = "9";
        		}
        	}
	       	
//여재 입고, 이적         	
        	if ("2".equals(szOrdYeojaeGp)) {
	       		if("002".equals(szYdStkLyrNo) || (("001".equals(szYdStkLyrNo)) && (Math.abs(lngOutDia - lngRightOutDia) < 180) 
                           													   && (Math.abs(dblWidth - dblRightWidth)   < 200))){
        			if ("2".equals(szRightOrdYeojaeGp)) {
                		if (szCurrProgCd.equals(szRightCurrProgCd)) {
                			szRightGrade = "1";
                		} else {
                			szRightGrade = "5";
                		}
                	} else {
                		szRightGrade = "9";
                	}
        		} else {
        			szRightGrade = "9";
        		}	
        	}
        	
//운송대기/운송지시대기 이적
        	if ("L".equals(szCurrProgCd)||"N".equals(szCurrProgCd)) {
        		if("L".equals(szRightCurrProgCd)||"N".equals(szRightCurrProgCd)) {
        			if(!("**".equals(szTransOrder) && "**".equals(szRightTransOrder))) { //null이면 (*)로처리됨sql문에서
        				if (szTransOrder.equals(szRightTransOrder)) {
        					szRightGrade = "1";
        				} else {
        					szRightGrade = "2";
        				}
        			} else {
        				szRightGrade = "2";
        			}
        		} else if ("F".equals(szRightCurrProgCd) ||"G".equals(szRightCurrProgCd) || "H".equals(szRightCurrProgCd) || "K".equals(szRightCurrProgCd) || "Y".equals(szRightCurrProgCd)|| "Z".equals(szRightCurrProgCd)) {
        			if ("002".equals(szYdStkLyrNo)) {
        				szRightGrade = "3";
        			} else {
        				szRightGrade = "4";
        			}
        		} else {
        			szRightGrade = "9";
        		}
        	}

//운송대기/운송지시대기 이적(냉연)
        	if ("4".equals(szCurrProgCd)||"6".equals(szCurrProgCd)) {
        		if("4".equals(szRightCurrProgCd)||"6".equals(szRightCurrProgCd)) {
        			if(!("**".equals(szTransOrder) && "**".equals(szRightTransOrder))) { //null이면 (*)로처리됨sql문에서
        				if (szTransOrder.equals(szRightTransOrder)) {
        					szRightGrade = "1";
        				} else {
        					szRightGrade = "2";
        				}
        			} else {
        				szRightGrade = "2";
        			}
        		} else if ("F".equals(szRightCurrProgCd) ||"G".equals(szRightCurrProgCd) || "H".equals(szRightCurrProgCd) || "2".equals(szRightCurrProgCd) || "Y".equals(szRightCurrProgCd)|| "Z".equals(szRightCurrProgCd)) {
        			if ("002".equals(szYdStkLyrNo)) {
        				szRightGrade = "3";
        			} else {
        				szRightGrade = "4";
        			}
        		} else {
        			szRightGrade = "9";
        		}
        	}
        	
//고간이송대기 이적        	
        	if ("Q".equals(szCurrProgCd)) {
        		if("Q".equals(szRightCurrProgCd)) {
        			szRightGrade = "1";
        		} else if ("F".equals(szRightCurrProgCd) ||"G".equals(szRightCurrProgCd) || "H".equals(szRightCurrProgCd) || "K".equals(szRightCurrProgCd) || "Y".equals(szRightCurrProgCd)|| "Z".equals(szRightCurrProgCd)) {
        			szRightGrade = "4";
        		} else {
        			szRightGrade = "9";
        		}
        	}
//반납대상 이적        	
        	if ("J".equals(szCurrProgCd)) {
        		if("J".equals(szRightCurrProgCd)) {
        			szRightGrade = "1";
        		} else if ("F".equals(szRightCurrProgCd) ||"G".equals(szRightCurrProgCd) || "H".equals(szRightCurrProgCd) || "K".equals(szRightCurrProgCd) || "Y".equals(szRightCurrProgCd)|| "Z".equals(szRightCurrProgCd)) {
        			szRightGrade = "4";
        		} else {
        			szRightGrade = "9";
        		}
        	}
			
			return szRightGrade;
		} catch(Exception e) {
			szMsg = "코일창고야드우측코일To위치평점항목Set 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} // end of SearchCoilGdsYdRightCoilGrade
	
	/**
	 * [A] 오퍼레이션명 : 소재코일적치가능공통
	 *     : 작업예약에서는 적치단이 중간에
	 *       스케쥴에서는   적치단이 마지막에
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord CoilLyrBaseCheck(String szStlNo, JDTORecord inRecord) throws JDTOException  {

		YdStkBedDao     ydStkBedDao  = new YdStkBedDao();
		YdStkLyrDao     ydStkLyrDao  = new YdStkLyrDao();
		YdEqpDao 		ydEqpDao 	 = new YdEqpDao();	
		JDTORecord outRecord    = JDTORecordFactory.getInstance().create();

		int intRtnVal         = 0;
		
		String szMsg			=	"";
		String szMethodName		=	"CoilLyrBaseCheck";
		String szOperationName	=	"소재코일적치가능공통";
		JDTORecord recPara 		= null;
		JDTORecordSet rsMaxBed  = null;
		JDTORecord recMaxBed    = null;
		
		JDTORecordSet rsStkLyr  = null;
		JDTORecord recStkLyr    = null;
		JDTORecord recInTemp    = null;
		JDTORecord recOutTemp   = null;
				
/////		
		String szYdStkColGp     = "";  // 적치열번호
		String szYdStkBedNo     = "";  // 적치배드번호
		String szYdStkLyrNo     = "";  // 적치단번호
    	String szMtlStat 		= "";
    	String szActStat 		= "";
		String szYEOJAE_CAUSE_CD= "";
		String szOutDia 		= "0";
		String szWidth 			= "0";
		String szThick 			= "0";
		String szWeigth 		= "0";
		String szHotCoilTm		= "0";
	
		///////		
		String szLeftColGp 		= "";
		String szLeftBedNo 		= "";
		String szLeftLyrNo 		= "";
		String szLeftStlNo 		= "";
		String szLeftMtlStat 	= "";
		String szLeftYEOJAE_CAUSE_CD = "";
		String szLeftOutDia 	= "0";
		String szLeftWidth 		= "0";
		String szLeftThick 		= "0";
		String szLeftWeigth 	= "0";
		String szLeftHotCoilTm	= "0";
		
		///		
		String szRightColGp 	= "";
		String szRightBedNo 	= "";
		String szRightLyrNo 	= "";
		String szRightStlNo 	= "";
		String szRightMtlStat 	= "";
		String szRightYEOJAE_CAUSE_CD = "";
		String szRightOutDia 	= "0";
		String szRightWidth 	= "0";
		String szRightThick 	= "0";
		String szRightWeigth 	= "0";
		String szRightHotCoilTm	= "0";
		
		JDTORecordSet rsBed     = null;
		JDTORecord recBed    	= null;
		
		int   intCompBedNo 		= 0;
		long  LngHotCoilTm 		= 0;
		long  LngLeftHotCoilTm 	= 0;
		long  LngRightHotCoilTm = 0;
		
		String szMaxBedNo = null;
				
		String szRtnVal = "";
		
		JDTORecordSet rsResult          = null;
		String sGRP_GP_CD	= "";
		String sUSE_YN		= "";
		String sRE_LOC			="N";
		long LngFR_VAL1	= 0;
		long LngTO_VAL1	= 0;	//		
		long LngFR_VAL2	= 0;//		
		long LngTO_VAL2	= 0;
		long LngFR_VAL3 = 0;	
		long LngTO_VAL3	= 0;	
		long LngFR_VAL4	= 0;
		long LngTO_VAL4	= 0;

		
		
		try {
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연소재코일적치가능공통 START★★★★★", YdConstant.INFO);

			szYdStkColGp 	= ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_COL_GP");
			szYdStkBedNo 	= ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_NO");
			szYdStkLyrNo 	= ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LYR_NO");

				
			szMsg = "적치단(" + szStlNo +"//"+ szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 작업합니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			////////////////////////////////////////////////////////////////////////
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
			recPara.setField("YD_STK_LYR_NO", szYdStkLyrNo);
			rsStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr, 0);
			
        	if (intRtnVal <= 0) {
				szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;

			}

        	rsStkLyr.first();
        	recStkLyr = rsStkLyr.getRecord();
        	
        	szMtlStat = ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_MTL_STAT");
        	szActStat = ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_ACT_STAT");
        	
    		if (("C".equals(szMtlStat)) ||( "D".equals(szMtlStat))|| (!("E".equals(szActStat)))) {
    			szMsg = "확인:"+szStlNo+"좌측1적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")의 상태가 적치가 불가능한 단입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
    		}
    				
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("STL_NO"		, szStlNo);
			rsMaxBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getCoilMaxBedNoByColGp*/    
        	intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsMaxBed, 303);
				
        	if (intRtnVal <= 0) {
				szMsg = "적치열(" + szYdStkColGp + ")로 MAX배드 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
	        	
        	rsMaxBed.first();
        	recMaxBed = rsMaxBed.getRecord();
	        	
        	szMaxBedNo 		= ydDaoUtils.paraRecChkNull(recMaxBed, "MAX_BED_NO");
        	
			if ("002".equals(szYdStkLyrNo)) {  // 검색적치단이 2단일 경우
				szLeftColGp = szYdStkColGp;
				szLeftBedNo = szYdStkBedNo;
				szLeftLyrNo = "001";
				
				szRightColGp = szYdStkColGp;
				intCompBedNo = Integer.parseInt(szYdStkBedNo);
				intCompBedNo++;
				if (intCompBedNo >= 10) {
					szRightBedNo = "" + intCompBedNo;
				} else {
					szRightBedNo = "0" + intCompBedNo;
				}
				szRightLyrNo = "001";
				
				szMsg = "검색적치단 2단인경우 좌측(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ") 우측(" + szRightColGp + szRightBedNo + szRightLyrNo + ")로 작업합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
			
			if ("001".equals(szYdStkLyrNo)) {  // 검색적치단이 1단일 경우
				
	        	if("01".equals(szYdStkBedNo)) { // 검색BED가 1인 경우
	        		szLeftColGp = szYdStkColGp;
					szLeftBedNo = szYdStkBedNo;
					szLeftLyrNo = "001";
					
					szRightColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					intCompBedNo++;
					if (intCompBedNo >= 10) {
						szRightBedNo = "" + intCompBedNo;
					} else {
						szRightBedNo = "0" + intCompBedNo;
					}
					szRightLyrNo = "001";
	        	} else if (szMaxBedNo.equals(szYdStkBedNo)) { // 검색BED가 마지막인 경우
	        		
	        		szLeftColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					intCompBedNo--;
					if (intCompBedNo >= 10) {
						szLeftBedNo = "" + intCompBedNo;
					} else {
						szLeftBedNo = "0" + intCompBedNo;
					}
					szLeftLyrNo = "001";
	        		
	        		
	        		szRightColGp = szYdStkColGp;
					szRightBedNo = szYdStkBedNo;
					szRightLyrNo = "001";
	        	} else {
	        		
	        		szLeftColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					intCompBedNo--;
					if (intCompBedNo >= 10) {
						szLeftBedNo = "" + intCompBedNo;
					} else {
						szLeftBedNo = "0" + intCompBedNo;
					}
					szLeftLyrNo = "001";
					
					szRightColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					intCompBedNo++;
					if (intCompBedNo >= 10) {
						szRightBedNo = "" + intCompBedNo;
					} else {
						szRightBedNo = "0" + intCompBedNo;
					}
					szRightLyrNo = "001";
	        	}
	        	
	        	szMsg = "검색적치단 1단인경우 좌측(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ") 우측(" + szRightColGp + szRightBedNo + szRightLyrNo + ")로 작업합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
						
			
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 왼쪽,오른쪽  적치단 조회 START★★★★★", YdConstant.INFO);
			// 왼쪽 적치단 조회//////////////////////////////////////////////////////////////////////
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("COIL_NO", szStlNo);
			recPara.setField("YD_STK_COL_GPL", szLeftColGp+szLeftBedNo+szLeftLyrNo);
			recPara.setField("YD_STK_COL_GPR", szRightColGp+szRightBedNo+szRightLyrNo);
			recPara.setField("YD_STK_COL_GP1", szLeftColGp);
			recPara.setField("YD_STK_BED_NO1", szLeftBedNo);
			recPara.setField("YD_STK_LYR_NO1", szLeftLyrNo);
			recPara.setField("YD_STK_COL_GP2", szRightColGp);
			recPara.setField("YD_STK_BED_NO2", szRightBedNo);
			recPara.setField("YD_STK_LYR_NO2", szRightLyrNo);

			rsStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilCOMM*/			
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr, 402);
 	    	if (intRtnVal <= 0) {
				szMsg = "확인:"+szStlNo+"적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")로 조회중 에러 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;

			}
			for(int Loop_i = 1; Loop_i <= rsStkLyr.size(); Loop_i++) {
				rsStkLyr.absolute(Loop_i);
				recStkLyr = rsStkLyr.getRecord();
				
				String sTARGET_GP = ydDaoUtils.paraRecChkNull(recStkLyr, "TARGET_GP");
//대상
				if (sTARGET_GP.equals("T")) {
					if (Loop_i == 1) {
						szStlNo 			= ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO"); 
						szYEOJAE_CAUSE_CD 	= ydDaoUtils.paraRecChkNull(recStkLyr, "YEOJAE_CAUSE_CD"); 
						szOutDia 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_OUTDIA"),"0");  
						szThick 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_T"),"0");  
						szWidth 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_W"),"0");  
						szWeigth 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_WT"),"0"); 
						szHotCoilTm 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "HOT_COIL_MIN"),"0"); 
						LngHotCoilTm        = Long.parseLong(szHotCoilTm);
					
					
					} else {
						szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 에러  발생! 대상 정보이상:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD" 	, "-1");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
				
					}


				}
				if (sTARGET_GP.equals("L")) {	
					if (Loop_i == 2) {
						szLeftStlNo 			= ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO"); 
			        	szLeftMtlStat 			= ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_MTL_STAT");
						szLeftYEOJAE_CAUSE_CD 	= ydDaoUtils.paraRecChkNull(recStkLyr, "YEOJAE_CAUSE_CD"); 
						szLeftOutDia 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_OUTDIA"),"0");  
						szLeftThick 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_T"),"0");  
						szLeftWidth 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_W"),"0");  
						szLeftWeigth 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_WT"),"0"); 
						szLeftHotCoilTm 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "HOT_COIL_MIN"),"0"); 
						LngLeftHotCoilTm       	= Long.parseLong(szLeftHotCoilTm);
			        	if ("002".equals(szYdStkLyrNo)) {
			        		if (!("C".equals(szLeftMtlStat) || "D".equals(szLeftMtlStat))) {
			        			szMsg = "확인:"+szStlNo+"좌측1적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 상태가 적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}
			        	}
					} else {
						szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 에러  발생! left 코일 정보 이상:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD" 	, "-1");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
				
					}
			        	
				}
				
				if (sTARGET_GP.equals("R")) {	
					if (Loop_i == 3) {

						szRightStlNo 			= ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO");
			        	szRightMtlStat 			= ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_MTL_STAT");
						szRightYEOJAE_CAUSE_CD 	= ydDaoUtils.paraRecChkNull(recStkLyr, "YEOJAE_CAUSE_CD"); 
						szRightOutDia 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_OUTDIA"),"0");  
						szRightThick 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_T"),"0");  
						szRightWidth 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_W"),"0");  
						szRightWeigth 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_WT"),"0");
						szRightHotCoilTm		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "HOT_COIL_MIN"),"0"); 
						LngRightHotCoilTm      	= Long.parseLong(szRightHotCoilTm);
			        	if ("002".equals(szYdStkLyrNo)) {
			        		if (!("C".equals(szRightMtlStat) || "D".equals(szRightMtlStat))) {
			        			szMsg = "확인:"+szStlNo+"우측1적치단(" + szRightColGp + szRightBedNo + szRightLyrNo + ")의 상태가 적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}
			        	}
					} else {
						szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 에러 발생! Right 코일정보 이상:"+ intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD" 	, "-1");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
				
					}

				}
			}

        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일소재야드 왼쪽,오른쪽  적치단 조회 END★★★★★", YdConstant.INFO);
        	//BRE 사용 조건 READ	
        	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
        	recInTemp 	= JDTORecordFactory.getInstance().create();
        	//recInTemp.setField("TEMP" 	, "0");	
        	recInTemp.setField("TEMP" 	, szYdStkColGp);	
        	
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB500*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 500);
			if(intRtnVal <= 0) {
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
    		}
			
			for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				recOutTemp = JDTORecordFactory.getInstance().create();
				rsResult.absolute(Loop_i);
				recOutTemp.setRecord(rsResult.getRecord());
				
				sGRP_GP_CD	= ydDaoUtils.paraRecChkNull(recOutTemp, "GRP_GP_CD");	//CHECK_GPT		
				sUSE_YN		= ydDaoUtils.paraRecChkNull(recOutTemp, "USE_YN");	    //사용가능 Y,N
				sRE_LOC		= ydDaoUtils.paraRecChkNull(recOutTemp, "RE_LOC");	    //반품장여부 Y,N
				LngFR_VAL1	= ydDaoUtils.paraRecChkNullLong(recOutTemp, "FR_VAL1");	//		
				LngTO_VAL1	= ydDaoUtils.paraRecChkNullLong(recOutTemp, "TO_VAL1");	//		
				LngFR_VAL2	= ydDaoUtils.paraRecChkNullLong(recOutTemp, "FR_VAL2");	//		
				LngTO_VAL2	= ydDaoUtils.paraRecChkNullLong(recOutTemp, "TO_VAL2");	//		
				LngFR_VAL3	= ydDaoUtils.paraRecChkNullLong(recOutTemp, "FR_VAL3");	//		
				LngTO_VAL3	= ydDaoUtils.paraRecChkNullLong(recOutTemp, "TO_VAL3");	//		
				LngFR_VAL4	= ydDaoUtils.paraRecChkNullLong(recOutTemp, "FR_VAL4");	//		
				LngTO_VAL4	= ydDaoUtils.paraRecChkNullLong(recOutTemp, "TO_VAL4");	//		
	
	    		ydUtils.putLog(szSessionName, szMethodName, "LngFR_VAL1:" + LngFR_VAL1, YdConstant.INFO);
	    		ydUtils.putLog(szSessionName, szMethodName, "LngTO_VAL1:" + LngTO_VAL1, YdConstant.INFO);
	    		ydUtils.putLog(szSessionName, szMethodName, "LngFR_VAL2:" + LngFR_VAL2, YdConstant.INFO);
	    		ydUtils.putLog(szSessionName, szMethodName, "LngTO_VAL2:" + LngTO_VAL2, YdConstant.INFO);
	    		ydUtils.putLog(szSessionName, szMethodName, "LngFR_VAL3:" + LngFR_VAL3, YdConstant.INFO);
	    		ydUtils.putLog(szSessionName, szMethodName, "LngTO_VAL3:" + LngTO_VAL3, YdConstant.INFO);
	    		ydUtils.putLog(szSessionName, szMethodName, "LngFR_VAL4:" + LngFR_VAL4, YdConstant.INFO);
	    		ydUtils.putLog(szSessionName, szMethodName, "LngTO_VAL4:" + LngTO_VAL4, YdConstant.INFO);

				if(sGRP_GP_CD.equals("1")  && (sUSE_YN.equals("Y"))) { 
		        	// 짱구코일 2단적치 제외
		        	if ("002".equals(szYdStkLyrNo) && !"".equals(szLeftStlNo) && !"".equals(szRightStlNo)) {
		        		if (LngHotCoilTm > LngFR_VAL1){   //20
		        			szYEOJAE_CAUSE_CD = "";
		        		}
		        		if (LngLeftHotCoilTm > LngFR_VAL1){   //20
		        			szLeftYEOJAE_CAUSE_CD = "";
		        		}
		        		if (LngRightHotCoilTm > LngFR_VAL1){   //20
		        			szRightYEOJAE_CAUSE_CD = "";
		        		}
		        		ydUtils.putLog(szSessionName, szMethodName, "==================== 짱구코일 2단적치 제외  START.====================", YdConstant.INFO);
		            	szRtnVal = searchCoilYdYeojaeCheck(szStlNo, szYEOJAE_CAUSE_CD,szLeftStlNo,szLeftYEOJAE_CAUSE_CD, szRightStlNo, szRightYEOJAE_CAUSE_CD);
		        		
		        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
		        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 짱구 코일 2단 적치 제한에 해당합니다.";
							outRecord.setField("RTN_CD" 	, "-1");	
							outRecord.setField("RTN_MSG" 	, szMsg);	
							return outRecord;
		        		}
		        	}
				}	
	    		
	        	// HOTCOIL
//				if((sGRP_GP_CD.equals("1")) && (sUSE_YN.equals("Y"))) { 
//		        	if (LngHotCoilTm <= LngFR_VAL1){   //20
//		        		ydUtils.putLog(szSessionName, szMethodName, "==================== HOT코일 CHECK  START.====================", YdConstant.INFO);
//		            	szRtnVal = searchCoilYdHotCoilCheck(szStlNo, szHotCoilTm, szYdStkLyrNo, szLeftStlNo, szLeftHotCoilTm, szRightStlNo, szRightHotCoilTm,LngFR_VAL1);
//		
//			    		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
//			    			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") HOT코일 적치 제한에 해당합니다.";
//							outRecord.setField("RTN_CD" 	, "-1");	
//							outRecord.setField("RTN_MSG" 	, szMsg);	
//							return outRecord;
//			    		}
//					}
//				}
	        	if((sGRP_GP_CD.equals("2")) && (sUSE_YN.equals("Y"))) { 
		        	
		           	// 2단일 경우 외경전도를 계산한다.
		        	if ("002".equals(szYdStkLyrNo) && !"".equals(szLeftStlNo) && !"".equals(szRightStlNo)) {
		        		ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 외경(전도)를 계산한다.====================", YdConstant.INFO);
		            	szRtnVal = searchCoilYdOutDiaDiffCheck1(szStlNo, szOutDia, szLeftStlNo, szLeftOutDia,  szRightStlNo, szRightOutDia,LngFR_VAL1,	LngTO_VAL1,	LngFR_VAL2,	LngTO_VAL2,	LngFR_VAL3,	LngTO_VAL3,	LngFR_VAL4,	LngTO_VAL4);
		        		
		        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
		        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과 외경전도기준으로로 적치가 불가능한 2단 입니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							outRecord.setField("RTN_CD" 	, "-1");	
							outRecord.setField("RTN_MSG" 	, szMsg);	
							return outRecord;
		        		}
		        	}
	        	}	
	        	if((sGRP_GP_CD.equals("3")) && (sUSE_YN.equals("Y"))) { 
		    	        
		         	// 2단일 경우 외경낙반를 계산한다.
		        	if ("002".equals(szYdStkLyrNo) && !"".equals(szLeftStlNo) && !"".equals(szRightStlNo)) {
		        		ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 외경(낙반)를 계산한다.====================", YdConstant.INFO);
		            	szRtnVal = searchCoilYdOutDiaDiffCheck2(szStlNo, szOutDia, szLeftStlNo, szLeftOutDia,  szRightStlNo, szRightOutDia,LngFR_VAL1);
		        		
		        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
		        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과 외경낙반기준로 적치가 불가능한 2단 입니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							outRecord.setField("RTN_CD" 	, "-1");	
							outRecord.setField("RTN_MSG" 	, szMsg);	
							return outRecord;
		        		}
		        	}
	        	}	
	           	if((sGRP_GP_CD.equals("4")) && (sUSE_YN.equals("Y"))) { 
		
		        	// 2단일 경우 폭기준를 계산한다.
		        	if ("002".equals(szYdStkLyrNo) && !"".equals(szLeftStlNo) && !"".equals(szRightStlNo)) {
		        		ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 폭기준를 계산한다.====================", YdConstant.INFO);
		            	szRtnVal = searchCoilYdWidthDiffCheck( szStlNo, szWidth, szLeftStlNo, szLeftWidth,  szRightStlNo, szRightWidth,LngFR_VAL1,	LngTO_VAL1);
		        		
		        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
		        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과 폭기준로 적치가 불가능한 2단 입니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							outRecord.setField("RTN_CD" 	, "-1");	
							outRecord.setField("RTN_MSG" 	, szMsg);	
							return outRecord;
		        		}
		        	}
	           	}	
	          	//if((sGRP_GP_CD.equals("5")) && (sUSE_YN.equals("Y"))) { 
	          	if(((sGRP_GP_CD.equals("5")) && (sUSE_YN.equals("Y"))) || "Y".equals(sRE_LOC)) { // 반품장인 경우 2단 중량편차를 무조건 계산 
	          		
		        	// 2단일 경우 중량편차를 계산한다.
		        	if ("002".equals(szYdStkLyrNo) && !"".equals(szLeftStlNo) && !"".equals(szRightStlNo)) {
		        		ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 중량편차를 계산한다.====================", YdConstant.INFO);
		            	szRtnVal = searchCoilYdWtCheck(szStlNo, szThick, szWeigth, szLeftStlNo, szLeftThick, szLeftWeigth,szRightStlNo,szRightThick, szRightWeigth ,LngFR_VAL1,	LngFR_VAL2);
		        		
		        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
		        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과 중량편차로 적치가 불가능한 2단 입니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							outRecord.setField("RTN_CD" 	, "-1");	
							outRecord.setField("RTN_MSG" 	, szMsg);	
							return outRecord;
		        		}
		        	}
	          	}
			}	
           	// 마지막 조건 check
        	ydUtils.putLog(szSessionName, szMethodName, "===================선택된 저장위치가 공위치인지 CHECK====================", YdConstant.INFO);
			
        	recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
			recPara.setField("YD_STK_LYR_NO", szYdStkLyrNo);
			
        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
        	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyr*/
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBed, 0);
        			
        	if (intRtnVal <= 0) {
    			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 마지막으로 적치단 CHECK ERROR.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
        	}
        	
        	rsBed.first();
			recBed =JDTORecordFactory.getInstance().create();
			recBed = rsBed.getRecord();
			String sYD_STK_LYR_MTL_STAT = recBed.getFieldString("YD_STK_LYR_MTL_STAT");
			
			if(!sYD_STK_LYR_MTL_STAT.equals("E")){
	   			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 비워있는 위치가 없습니다. CHECK ERROR.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
 				
			}       	
         	outRecord.setField("RTN_BED" 			, szYdStkColGp + szYdStkBedNo + szYdStkLyrNo);	
           	outRecord.setField("RTN_COLGP" 			, szYdStkColGp);	
        	outRecord.setField("RTN_BEDNO" 			, szYdStkBedNo);	
        	outRecord.setField("RTN_LYRNO" 			, szYdStkLyrNo);	
        	outRecord.setField("RTN_STLNO" 			, szStlNo);	
           	outRecord.setField("RTN_LEFT_COLGP" 	, szLeftColGp);	
        	outRecord.setField("RTN_LEFT_BEDNO" 	, szLeftBedNo);	
        	outRecord.setField("RTN_LEFT_LYRNO" 	, szLeftLyrNo);	
        	outRecord.setField("RTN_LEFT_STLNO" 	, szLeftStlNo);	
           	outRecord.setField("RTN_RIGHT_COLGP" 	, szRightColGp);	
        	outRecord.setField("RTN_RIGHT_BEDNO" 	, szRightBedNo);	
        	outRecord.setField("RTN_RIGHT_LYRNO" 	, szRightLyrNo);	
        	outRecord.setField("RTN_RIGHT_STLNO" 	, szRightStlNo);
        	outRecord.setField("RTN_TO_BEDNO"		, szYdStkColGp.substring(1,6) +szYdStkLyrNo.substring(2,3) + szYdStkBedNo);	
			ydUtils.putLog(szSessionName, szMethodName, szYdStkColGp.substring(1,6) +szYdStkLyrNo.substring(2,3) + szYdStkBedNo, YdConstant.INFO);
    		
 			outRecord.setField("RTN_CD" 			, "1");	
			return outRecord;

		} catch(Exception e) {
			szMsg = "코일소재야드 To위치검색 중 예외발생! 예외메세지: ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" 	, "0");	
			outRecord.setField("RTN_MSG" 	, szMsg);	
			return outRecord;
		}
		
	} 
	
	/**
	 * [A] 오퍼레이션명 : 제품코일적치가능공통
	 *     : 작업예약에서는 적치단이 중간에
	 *       스케쥴에서는   적치단이 마지막에
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord CoilGdsLyrBaseCheck(String szStlNo, JDTORecord inRecord) throws JDTOException  {

		YdStkBedDao     ydStkBedDao  = new YdStkBedDao();
		YdStkLyrDao     ydStkLyrDao  = new YdStkLyrDao();
		YdEqpDao	ydEqpDao 	= new YdEqpDao();
		JDTORecord outRecord    = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1   = JDTORecordFactory.getInstance().create();


		
		int intRtnVal         	= 0;
		
		String szMsg			= "";
		String szMethodName		= "CoilGdsLyrBaseCheck";
		String szOperationName	= "제품코일적치가능공통";
		
		JDTORecord recPara 		= null;

		
		JDTORecordSet rsMaxBed  = null;
		JDTORecordSet rsBed     = null;
		JDTORecordSet rsOutTemp  = null;

		JDTORecordSet rsStkLyr  = null;
		JDTORecordSet rsStkLyr1 = null;
		JDTORecord recStkLyr    = null;
		JDTORecord recStkLyr1   = null;
		JDTORecord recBed       = null;
		JDTORecord recMaxBed    = null;
		JDTORecord recOutTemp1  = null;
		
		String szYdStkColGp     = null;  // 적치열번호
		String szYdStkBedNo     = null;  // 적치배드번호
		String szYdStkLyrNo     = null;  // 적치단번호
		
		String szOutDia 		= "0";
		String szWidth 			= "0";
		String szThick 			= "0";
		String szWeigth 		= "0";
		
		String szLeftColGp 		= "";
		String szLeftBedNo 		= "";
		String szLeftLyrNo 		= "";
		String szLeftStlNo 		= "";
		String szLeftMtlStat 	= "";
		String szLeftOutDia 	= "0";
		String szLeftWidth 		= "0";
		String szLeftThick 		= "0";
		String szLeftWeigth 	= "0";
		
		String szRightColGp 	= "";
		String szRightBedNo 	= "";
		String szRightLyrNo 	= "";
		String szRightStlNo 	= "";
		String szRightMtlStat 	= "";
		String szRightOutDia 	= "0";
		String szRightWidth 	= "0";
		String szRightThick 	= "0";
		String szRightWeigth 	= "0";

		String szYD_COIL_OUTDIA_GRP_GP = "";  //
		String sCOIL_OUTDIA_GRP_GP  = "";  //대상재 외경군
		String sYD_STK_COL_W_GP = "";
		
		int   intCompBedNo 		= 0;
		
		String szMaxBedNo 		= null;
		String sCOIL_W_GP 		= "";		
		String szRtnVal 		= "";

		String sGRP_GP_LOC      = "";
		String sCOIL_NO_A 		= "";
		String sCOIL_OUTDIA_A 	= "";
		String sCOIL_NO_B 		= "";
		String sCOIL_OUTDIA_B 	= "";
		String sCOIL_NO_C 		= "";
		String sCOIL_OUTDIA_C 	= "";
		String sCOIL_NO_D 		= "";
		String sCOIL_OUTDIA_D 	= "";
		String sCOIL_NO_E 		= "";
		String sCOIL_OUTDIA_E 	= "";
		String sCOIL_NO_F 		= "";
		String sCOIL_OUTDIA_F 	= "";
		String sCOIL_NO_G 		= "";
		String sCOIL_OUTDIA_G 	= "";
		String sGRP_GP_CD       = "";
		String sUSE_YN          = "";
		String sBRE_CHK1        = "N";
		String sBRE_CHK2        = "N";
		String sBRE_CHK3        = "N";
		String sBRE_CHK5        = "N";
		String szYD_STK_COL_GP_YN = "";
		//String szBreSkipYn      = "Y";
		
		
		long   lngsCOIL_OUTDIA_A  	= 0;           //코일외경
		long   lngsCOIL_OUTDIA_B   	= 0;           
		long   lngsCOIL_OUTDIA_C   	= 0;           
		long   lngsCOIL_OUTDIA_D   	= 0;           
		long   lngsCOIL_OUTDIA_E   	= 0;           
		long   lngsCOIL_OUTDIA_F   	= 0;           
		long   lngsCOIL_OUTDIA_G   	= 0;           
		
		String szBRE_YDB701_4_USE_YN = null;
		String szBRE_YDB701_6_USE_YN = null;
		String szISSUE_TG_YN	= null;
		
		String sCLINE_CHK       = "";

		try {
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일 제품코일적치가능공통 START★★★★★", YdConstant.INFO);

			szYdStkColGp = ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_COL_GP");
			szYdStkBedNo = ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_NO");
			szYdStkLyrNo = ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LYR_NO");
			//szBreSkipYn = ydDaoUtils.paraRecChkNull(inRecord, "BRE_CHK1_SKIP_YN");
			
			if("PT".equals(szYdStkColGp.substring(2 , 4)) && "".equals(szYdStkLyrNo)){
				szYdStkLyrNo ="01";
			}
			
			//지포장 제품적치 체크
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			rsOutTemp = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    /*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedYardWrap*/
        	intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsOutTemp, 314);
			
        	if (intRtnVal > 0) {
            	rsOutTemp.first();
            	recOutTemp1 = rsOutTemp.getRecord();
            	szYD_STK_COL_GP_YN = ydDaoUtils.paraRecChkNull(recOutTemp1, "YD_STK_COL_GP_YN");
            	if(szYD_STK_COL_GP_YN.equals("Y")) {
            		szMsg = "적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 지포장 제품적치 체크 시작:" ;//+ szBreSkipYn ;
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            		outRecord1 = this.CoilGdsLyrBaseCheckYardWrap(szStlNo, inRecord);
            		return outRecord1;
            	}	
			}
        	
        	/*
			//ABC는 별도 로직 구현함			
			if(szYdStkColGp.substring(1,2).equals("A")||
			   szYdStkColGp.substring(1,2).equals("B")||
			   szYdStkColGp.substring(1,2).equals("C")){
				
				szMsg = "적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") ABC동  제품적치 체크 시작:" ;//+ szBreSkipYn ;
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord1 = this.CoilGdsLyrBaseCheckABC(szStlNo, inRecord);
				return outRecord1;
			}
			*/
//        	150908 hun 기존 ABC체크 로직 고정스키드 분류로 변경
        	if(ydEqpDao.chkFixedSkid(szYdStkColGp)){
        		szMsg = "적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") ABC체크 OK 고정스키드 분류로 변경" ;
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			outRecord1 = this.CoilGdsLyrBaseCheckABC(szStlNo, inRecord);
				return outRecord1;
        		
        	}
 
  	
			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") BED로 검색시작   ------>저장위치 수정유무:" ;//+ szBreSkipYn ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			if((szYdStkColGp.substring(2, 3).equals("0"))||
					(szYdStkColGp.substring(2, 3).equals("1"))||
					(szYdStkColGp.substring(2, 3).equals("2"))||
					(szYdStkColGp.substring(2, 3).equals("3"))||
					(szYdStkColGp.substring(2, 3).equals("4"))||
					(szYdStkColGp.substring(2, 3).equals("5"))|| 
					(szYdStkColGp.substring(2, 3).equals("6"))
			) {
        	} else {	
        		szRtnVal = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
        		ydUtils.putLog(szSessionName, szMethodName, "C열연코일제품야드 설비위치=>>>>"+szRtnVal, YdConstant.INFO);
               	outRecord.setField("RTN_BED" 			, szYdStkColGp + szYdStkBedNo + szYdStkLyrNo);	
               	outRecord.setField("RTN_COLGP" 			, szYdStkColGp);	
            	outRecord.setField("RTN_BEDNO" 			, szYdStkBedNo);	
            	outRecord.setField("RTN_LYRNO" 			, szYdStkLyrNo);	
            	outRecord.setField("RTN_STLNO" 			, szStlNo);	
        		outRecord.setField("RTN_CD" 			, "1");	
	 	       	outRecord.setField("RTN_TO_BEDNO"		, szYdStkColGp.substring(1,6) +szYdStkLyrNo.substring(2,3) + szYdStkBedNo);	
							
				return outRecord;
//        		return szRtnVal;
        	}
			
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("STL_NO"		, szStlNo);
			rsMaxBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    /*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getCoilMaxBedNoByColGp*/
        	intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsMaxBed, 303);
			
        	if (intRtnVal <= 0) {
				szMsg = "적치열(" + szYdStkColGp + ")로 MAX배드 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
        	
        	rsMaxBed.first();
        	recMaxBed = rsMaxBed.getRecord();
        	
        	szMaxBedNo = ydDaoUtils.paraRecChkNull(recMaxBed, "MAX_BED_NO");
        	szYD_COIL_OUTDIA_GRP_GP = ydDaoUtils.paraRecChkNull(recMaxBed, "YD_COIL_OUTDIA_GRP_GP");
        	sYD_STK_COL_W_GP 		= ydDaoUtils.paraRecChkNull(recMaxBed, "YD_STK_COL_W_GP");
        	sCOIL_OUTDIA_GRP_GP		= ydDaoUtils.paraRecChkNull(recMaxBed, "OUTDIA_GRP_GP");
        	sCOIL_W_GP				= ydDaoUtils.paraRecChkNull(recMaxBed, "COIL_W_GP");
        	
 				
        	ydUtils.putLog(szSessionName, szMethodName, "szYD_COIL_OUTDIA_GRP_GP-->" + szYD_COIL_OUTDIA_GRP_GP, YdConstant.INFO);
        	ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_COL_W_GP-->" + sYD_STK_COL_W_GP, YdConstant.INFO);
        	ydUtils.putLog(szSessionName, szMethodName, "sCOIL_OUTDIA_GRP_GP-->" + sCOIL_OUTDIA_GRP_GP, YdConstant.INFO);
        	
	
 			if ("002".equals(szYdStkLyrNo)) {  // 검색적치단이 2단일 경우
				szLeftColGp = szYdStkColGp;
				szLeftBedNo = szYdStkBedNo;
				szLeftLyrNo = "001";
				
				szRightColGp = szYdStkColGp;
				intCompBedNo = Integer.parseInt(szYdStkBedNo);
				if(szYD_COIL_OUTDIA_GRP_GP.equals("A")) {
					intCompBedNo = intCompBedNo + 2;	
				} else if(szYD_COIL_OUTDIA_GRP_GP.equals("B")) {
					intCompBedNo = intCompBedNo + 3;	
				} else if(szYD_COIL_OUTDIA_GRP_GP.equals("C")) {
					intCompBedNo = intCompBedNo + 4;	
				}
				
				if (intCompBedNo >= 10) {
					szRightBedNo = "" + intCompBedNo;
				} else {
					szRightBedNo = "0" + intCompBedNo;
				}
				szRightLyrNo = "001";
			}
			
			if ("001".equals(szYdStkLyrNo)) {  // 검색적치단이 1단일 경우
				
				
	        	
	        	if("01".equals(szYdStkBedNo)) { // 검색BED가 1인 경우
	        		szLeftColGp = szYdStkColGp;
					szLeftBedNo = szYdStkBedNo;
					szLeftLyrNo = "001";
					
					szRightColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					
					if(szYD_COIL_OUTDIA_GRP_GP.equals("A")) {
						intCompBedNo = intCompBedNo + 2;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("B")) {
						intCompBedNo = intCompBedNo + 3;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("C")) {
						intCompBedNo = intCompBedNo + 4;	
					}

					if (intCompBedNo >= 10) {
						szRightBedNo = "" + intCompBedNo;
					} else {
						szRightBedNo = "0" + intCompBedNo;
					}
					szRightLyrNo = "001";
	        	} else if (szMaxBedNo.equals(szYdStkBedNo)) { // 검색BED가 마지막인 경우
	        		
	        		szLeftColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					if(szYD_COIL_OUTDIA_GRP_GP.equals("A")) {
						intCompBedNo = intCompBedNo - 2;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("B")) {
						intCompBedNo = intCompBedNo - 3;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("C")) {
						intCompBedNo = intCompBedNo - 4;	
					}
					if (intCompBedNo >= 10) {
						szLeftBedNo = "" + intCompBedNo;
					} else {
						szLeftBedNo = "0" + intCompBedNo;
					}
					szLeftLyrNo = "001";
	        		
	        		
	        		szRightColGp = szYdStkColGp;
					szRightBedNo = szYdStkBedNo;
					szRightLyrNo = "001";
	        	} else {
	        		
	        		szLeftColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					if(szYD_COIL_OUTDIA_GRP_GP.equals("A")) {
						intCompBedNo = intCompBedNo - 2;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("B")) {
						intCompBedNo = intCompBedNo - 3;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("C")) {
						intCompBedNo = intCompBedNo - 4;	
					}
					if (intCompBedNo >= 10) {
						szLeftBedNo = "" + intCompBedNo;
					} else {
						szLeftBedNo = "0" + intCompBedNo;
					}
					if (intCompBedNo >= 10) {
						szLeftBedNo = "" + intCompBedNo;
					} else {
						szLeftBedNo = "0" + intCompBedNo;
					}
					szLeftLyrNo = "001";
					
					szRightColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					if(szYD_COIL_OUTDIA_GRP_GP.equals("A")) {
						intCompBedNo = intCompBedNo + 2;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("B")) {
						intCompBedNo = intCompBedNo + 3;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("C")) {
						intCompBedNo = intCompBedNo + 4;	
					}

					if (intCompBedNo >= 10) {
						szRightBedNo = "" + intCompBedNo;
					} else {
						szRightBedNo = "0" + intCompBedNo;
					}
					szRightLyrNo = "001";
	        	}
				
	        	
			}
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 왼쪽,오른쪽  적치단 조회 START★★★★★", YdConstant.INFO);

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("COIL_NO", szStlNo);
			recPara.setField("YD_STK_COL_GPL", szLeftColGp+szLeftBedNo+szLeftLyrNo);
			recPara.setField("YD_STK_COL_GPR", szRightColGp+szRightBedNo+szRightLyrNo);
			recPara.setField("YD_STK_COL_GP1", szLeftColGp);
			recPara.setField("YD_STK_BED_NO1", szLeftBedNo);
			recPara.setField("YD_STK_LYR_NO1", szLeftLyrNo);
			recPara.setField("YD_STK_COL_GP2", szRightColGp);
			recPara.setField("YD_STK_BED_NO2", szRightBedNo);
			recPara.setField("YD_STK_LYR_NO2", szRightLyrNo);

			rsStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilCOMM*/			
        	//intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr, 402);
			
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilCOMM_AUTO*/			
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr, 404);
			
 	    	if (intRtnVal <= 0) {
				szMsg = "확인:"+szStlNo+"적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;

			}
        	if (intRtnVal > 3) {
				szMsg = "확인:"+szStlNo+"적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")로 조회중 error 발생! 적치건수:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
 	    	
			for(int Loop_i = 1; Loop_i <= rsStkLyr.size(); Loop_i++) {
				rsStkLyr.absolute(Loop_i);
				recStkLyr = rsStkLyr.getRecord();
				
				String sTARGET_GP = ydDaoUtils.paraRecChkNull(recStkLyr, "TARGET_GP");
//대상
				if (sTARGET_GP.equals("T")) {
					if (Loop_i == 1) {
						szStlNo 			= ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO"); 
						szOutDia 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_OUTDIA"),"0");  
						szThick 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_T"),"0");  
						szWidth 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_W"),"0");  
						szWeigth 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_WT"),"0"); 
					} else {
						szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD" 	, "-1");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
					}
				}
				if (sTARGET_GP.equals("L")) {	
					if (Loop_i == 2) {
			        	szLeftStlNo 		= ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO"); 
			        	szLeftMtlStat 		= ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_MTL_STAT");
						szLeftOutDia 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_OUTDIA"),"0");  
						szLeftThick 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_T"),"0");  
						szLeftWidth 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_W"),"0");  
						szLeftWeigth 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_WT"),"0"); 
			
						szBRE_YDB701_4_USE_YN = StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "BRE_YDB701_4_USE_YN"),"N");  
						szBRE_YDB701_6_USE_YN = StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "BRE_YDB701_6_USE_YN"),"N");
						szISSUE_TG_YN		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "ISSUE_TG_YN"),"N");
						
			        	if ("002".equals(szYdStkLyrNo)) {
//			        		hun 150826 1단코일이 예약(D)상태에서 2단 적치 불가 
//			        		if (!("C".equals(szLeftMtlStat) || "D".equals(szLeftMtlStat))) {
			        		if ( !"C".equals(szLeftMtlStat) || ("D".equals(szLeftMtlStat) && "Y".equals(szBRE_YDB701_6_USE_YN)) ) {
			        			szMsg = "확인:"+szStlNo+"좌측1적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 상태가 적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}
			        		
			        		if("Y".equals(szBRE_YDB701_4_USE_YN) && "Y".equals(szISSUE_TG_YN)) {
			        			//hun 출고대상코일위적치금지 이면서 출고대상이면 2단적치 불가
			        			szMsg = "확인:"+szStlNo+"좌측1적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 코일 : "+ szLeftStlNo +"가 출고대상으로  적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}
			        	}
					} else {
						szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD" 	, "-1");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
				
					}

				}
				
				if (sTARGET_GP.equals("R")) {	
					if (Loop_i == 3) {
			        	szRightStlNo 		= ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO");
			        	szRightMtlStat 		= ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_MTL_STAT");
						szRightOutDia 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_OUTDIA"),"0");  
						szRightThick 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_T"),"0");  
						szRightWidth 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_W"),"0");  
						szRightWeigth 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_WT"),"0");
						
						szBRE_YDB701_4_USE_YN = StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "BRE_YDB701_4_USE_YN"),"N");  
						szBRE_YDB701_6_USE_YN = StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "BRE_YDB701_6_USE_YN"),"N");
						szISSUE_TG_YN		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "ISSUE_TG_YN"),"N");
						
			        	if ("002".equals(szYdStkLyrNo)) {
//			        		hun 150826 1단코일이 예약(D)상태에서 2단 적치 불가 
//			        		if (!("C".equals(szRightMtlStat) || "D".equals(szRightMtlStat))) {
			        		if (!"C".equals(szRightMtlStat) || ("D".equals(szRightMtlStat) && "Y".equals(szBRE_YDB701_6_USE_YN)) ) {
			        			szMsg = "확인:"+szStlNo+"우측1적치단(" + szRightColGp + szRightBedNo + szRightLyrNo + ")의 상태가 적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}
			        		
                            if("Y".equals(szBRE_YDB701_4_USE_YN) && "Y".equals(szISSUE_TG_YN)) {
			        			//출고대상코일위적치금지 이면서 출고대상이면 2단적치 불가
			        			szMsg = "확인:"+szStlNo+"우측1적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 코일 : "+ szRightStlNo +"가 출고대상으로  적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}
			        	}
					} else {
						szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD" 	, "-1");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
				
					}
				}
			}
			
			// 왼쪽,오른쪽  적치단 로그 Start ----------------------------------------------------------------------------------------------------------
			szMsg = "확인:"+szStlNo+"★★★ 좌측1적치단(" + szLeftColGp + "-" + szLeftBedNo + "-" +szLeftLyrNo + ") 코일: "+ szLeftStlNo + " 상태: " +  szLeftMtlStat + " 외경: " + szLeftOutDia + " 중량: " + szLeftWeigth;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			szMsg = "확인:"+szStlNo+"★★★ 우측1적치단(" + szRightColGp + "-" + szRightBedNo + "-" +szRightLyrNo + ") 코일: "+ szRightStlNo + " 상태: " +  szRightMtlStat + " 외경: " + szRightOutDia + " 중량: " + szRightWeigth;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			// 왼쪽,오른쪽  적치단 로그 End ------------------------------------------------------------------------------------------------------------

        	//BRE 사용 조건 READ	
			JDTORecordSet rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
        	recInTemp.setField("TEMP" 	, "0");	
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB701*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 701);
			if(intRtnVal <= 0) {
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
    		}
			
			for(int Loop1_i = 1; Loop1_i <= rsResult.size(); Loop1_i++) {
				JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
				rsResult.absolute(Loop1_i);
				recOutTemp.setRecord(rsResult.getRecord());

				sGRP_GP_CD	= ydDaoUtils.paraRecChkNull(recOutTemp, "GRP_GP_CD");	//CHECK_GPT		
				sUSE_YN		= ydDaoUtils.paraRecChkNull(recOutTemp, "USE_YN");	//사용가능 Y,N	
				
				if((sGRP_GP_CD.equals("1")) && (sUSE_YN.equals("Y"))) {
					sBRE_CHK1 = "Y";
				} else if((sGRP_GP_CD.equals("2")) && (sUSE_YN.equals("Y"))) {
					sBRE_CHK2 = "Y";
				} else if((sGRP_GP_CD.equals("5")) && (sUSE_YN.equals("Y"))) {
					sBRE_CHK5 = "Y";
				}
			}		

    		ydUtils.putLog(szSessionName, szMethodName, "sBRE_CHK1:" + sBRE_CHK1, YdConstant.INFO);
    		ydUtils.putLog(szSessionName, szMethodName, "sBRE_CHK2:" + sBRE_CHK2, YdConstant.INFO);
    		ydUtils.putLog(szSessionName, szMethodName, "sBRE_CHK5:" + sBRE_CHK5, YdConstant.INFO);

			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 왼쪽,오른쪽  적치단 조회 END★★★★★", YdConstant.INFO);
        	
        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 중량편차,외경간격,외경편차   START★★★★★", YdConstant.INFO);
    
			szMsg = "대상(" + sCOIL_OUTDIA_GRP_GP +")"+ intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	       	sYD_STK_COL_W_GP 		= ydDaoUtils.paraRecChkNull(recMaxBed, "YD_STK_COL_W_GP");
        	sCOIL_OUTDIA_GRP_GP		= ydDaoUtils.paraRecChkNull(recMaxBed, "OUTDIA_GRP_GP");
        	sCOIL_W_GP				= ydDaoUtils.paraRecChkNull(recMaxBed, "COIL_W_GP");
 			
			
        	ydUtils.putLog(szSessionName, szMethodName, "==================== 폭구분 계산한다.====================", YdConstant.INFO);
        	if(!sYD_STK_COL_W_GP.equals(sCOIL_W_GP)){
    			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 코일"+ sCOIL_W_GP+ "과 적치위치"+ sYD_STK_COL_W_GP+ "구분이 틀립니다..";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
        	}
        	
        	ydUtils.putLog(szSessionName, szMethodName, "==================== 외경군 계산한다.====================", YdConstant.INFO);
        	if(!sCOIL_OUTDIA_GRP_GP.equals(szYD_COIL_OUTDIA_GRP_GP)){
    			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 코일"+ sCOIL_OUTDIA_GRP_GP+ "과 적치위치"+ szYD_COIL_OUTDIA_GRP_GP+ "군이 틀립니다..";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
        	}
        	
        	
        	// 1단일 경우 외경편차를 계산한다.
        	ydUtils.putLog(szSessionName, szMethodName, "====================1단일 경우 외경편차를 계산한다.====================", YdConstant.INFO);
        	if ("001".equals(szYdStkLyrNo)) {
        		szRtnVal = searchCoilGdsYdOutDiaDiffCheck( szStlNo, szYdStkLyrNo, szOutDia, szLeftStlNo, szLeftOutDia,  szRightStlNo, szRightOutDia);
				
        		ydUtils.putLog(szSessionName, szMethodName, "외경CHECK :" + szRtnVal, YdConstant.DEBUG);
				    		
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 코일과 외경차이(180mm)로 적치가 불가능한 1 단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
         	// 1단일 경우 폭기준를 계산한다. 
        	if ("001".equals(szYdStkLyrNo)) {
        		ydUtils.putLog(szSessionName, szMethodName, "====================1단일 경우 폭기준를 계산한다.====================", YdConstant.INFO);
            	szRtnVal = searchCoilYdGdsWidthDiffCheck( szStlNo, szYdStkLyrNo, szWidth, szLeftStlNo, szLeftWidth,  szRightStlNo, szRightWidth);
        		
            	ydUtils.putLog(szSessionName, szMethodName, "폭CHECK :" + szRtnVal, YdConstant.DEBUG);
        		        		
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 코일과 폭기준(200mm)로 적치가 불가능한 1단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
        	
         	//  hun 150922 BRE 적용 1단일 경우 폭간섭를 계산한다. (저장위치수정인 경우 삭제 한다.)
        	if (sBRE_CHK1.equals("Y") && ("001".equals(szYdStkLyrNo)) //&& szBreSkipYn.equals("Y")
        			) {
        		ydUtils.putLog(szSessionName, szMethodName, "====================1단일 경우 폭간섭를 계산한다.====================", YdConstant.INFO);
            	szRtnVal = searchCoilYdGdsWidthDiffCheck3( szStlNo, szYdStkColGp, szYdStkBedNo, szYdStkLyrNo ,  sYD_STK_COL_W_GP, szWidth ,szOutDia,szMaxBedNo);
        		
            	ydUtils.putLog(szSessionName, szMethodName, "확인:"+szStlNo+"폭간섭CHECK :" + szRtnVal, YdConstant.DEBUG);
        		        		
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일 폭 간섭으로 적치가 불가능한 위치입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}

        	
        	// 2단일 경우 중량/폭편차를 계산한다.
        	ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 중량/폭편차를 계산한다.====================", YdConstant.INFO);
        	if ("002".equals(szYdStkLyrNo) && !"".equals(szLeftStlNo) && !"".equals(szRightStlNo)) {
        		szRtnVal = searchCoilGdsYdWtCheck(szStlNo, szThick,szWidth, szWeigth, szLeftStlNo, szLeftThick,szLeftWidth, szLeftWeigth,szRightStlNo,szRightThick,szRightWidth, szRightWeigth);
 
               	ydUtils.putLog(szSessionName, szMethodName, "중량CHECK :" + szRtnVal, YdConstant.DEBUG);
                
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과 중량/폭편차로 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
           		} else if (szRtnVal.equals("WID_FAILURE")) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과폭편차로 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
           		} else if (szRtnVal.equals("WGT_FAILURE")) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과중량편차로 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
        	
        	// 150904 hun 횡행좌표 거리 계산 값에 의한 외경 CHECK
        	ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 횡행좌표 거리 계산 값에 의한 외경체크====================", YdConstant.INFO);
        	if (sBRE_CHK5.equals("Y") && "002".equals(szYdStkLyrNo)) {
        		szRtnVal = searchCoilGdsYdOutDiaOverRunCheck( szYdStkColGp, szYdStkBedNo, szStlNo, szYdStkLyrNo, szOutDia, szLeftStlNo, szLeftOutDia,  szRightStlNo, szRightOutDia);
              	ydUtils.putLog(szSessionName, szMethodName, "횡행좌표 거리 계산 값에 의한 외경 CHECK :" + szRtnVal, YdConstant.DEBUG);
        		
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일이 횡행좌표 거리 계산 값에 의한 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
        	
        	// 2단일 경우 외경간격을 계산한다.(OK)
        	ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 외경간격을 계산한다.====================", YdConstant.INFO);
        	if ("002".equals(szYdStkLyrNo) && !"".equals(szLeftStlNo) && !"".equals(szRightStlNo)) {
        		szRtnVal = searchCoilGdsYdOutDiaIntervalCheck( szYdStkColGp, szYdStkBedNo, szStlNo, szYdStkLyrNo, szOutDia, szLeftStlNo, szLeftOutDia,  szRightStlNo, szRightOutDia);
              	ydUtils.putLog(szSessionName, szMethodName, "외경간격CHECK :" + szRtnVal, YdConstant.DEBUG);
        		
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일이 외경간격편차로 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
           	
        	// 2단일 경우 외경편차1를 계산한다.
        	ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 외경편차1를 계산한다.====================", YdConstant.INFO);
        	if ("002".equals(szYdStkLyrNo) && !"".equals(szLeftStlNo) && !"".equals(szRightStlNo)) {
        		szRtnVal = searchCoilYdGdsWidthDiffCheck2( szStlNo, szYdStkLyrNo, szOutDia, szLeftStlNo, szLeftOutDia,  szRightStlNo, szRightOutDia);
 
               	ydUtils.putLog(szSessionName, szMethodName, "외경 :" + szRtnVal, YdConstant.DEBUG);
                
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과 외경편차1로 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}        	
           	

 
        	// 2단일 경우 기울기 공식 적용.
        	ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 기울기를 계산한다.====================", YdConstant.INFO);
// 2단 우측에 코일이 있을 경우
        	if (sBRE_CHK2.equals("Y") && ("002".equals(szYdStkLyrNo))) {
 
    			rsStkLyr1 = JDTORecordFactory.getInstance().createRecordSet("Temp");

    			recPara = JDTORecordFactory.getInstance().create();
    			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
    			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
    			recPara.setField("YD_STK_LYR_NO", szYdStkLyrNo);
       	    
    			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilCline*/			
            	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr1, 600);
     	    	if (intRtnVal <= 0) {
    				szMsg = "확인:"+szStlNo+"적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				outRecord.setField("RTN_CD" 	, "-1");	
    				outRecord.setField("RTN_MSG" 	, szMsg);	
    				return outRecord;
    			}
     	    	
     	    	
     	    	rsStkLyr1.first();
     	    	recStkLyr1 = JDTORecordFactory.getInstance().create();
     	    	recStkLyr1 = rsStkLyr1.getRecord();

     	    	for(int Loop_i = 1; Loop_i <= rsStkLyr1.size(); Loop_i++) {
					rsStkLyr1.absolute(Loop_i);
					recStkLyr1 = rsStkLyr1.getRecord();
					
					String sCOIL_GP = ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_GP");
					//좌측2단
					if (sCOIL_GP.equals("A")) {
						sCOIL_NO_A 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO");
						sCOIL_OUTDIA_A 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");
						lngsCOIL_OUTDIA_A	= Long.parseLong(sCOIL_OUTDIA_A);
						sGRP_GP_LOC		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "GRP_GP_LOC"),"0");  
					//좌측1단
					} else if (sCOIL_GP.equals("B")) {
						sCOIL_NO_B 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO"); 
						sCOIL_OUTDIA_B 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");  
						lngsCOIL_OUTDIA_B	= Long.parseLong(sCOIL_OUTDIA_B);
					//대상1단
					} else if (sCOIL_GP.equals("C")) {
						sCOIL_NO_C 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO"); 
						sCOIL_OUTDIA_C 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");  
						lngsCOIL_OUTDIA_C	= Long.parseLong(sCOIL_OUTDIA_C);
					//대상2단 : 목적
					} else if (sCOIL_GP.equals("D")) {
//						sCOIL_NO_D 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO"); 
//						sCOIL_OUTDIA_D 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");  
						sCOIL_NO_D 		= szStlNo;
						sCOIL_OUTDIA_D 	= szOutDia;  
						lngsCOIL_OUTDIA_D	= Long.parseLong(sCOIL_OUTDIA_D);
					//우측1단
					} else if (sCOIL_GP.equals("E")) {
						sCOIL_NO_E 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO"); 
						sCOIL_OUTDIA_E 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");  
						lngsCOIL_OUTDIA_E	= Long.parseLong(sCOIL_OUTDIA_E);
					//우측2단
					} else if (sCOIL_GP.equals("F")) {
						sCOIL_NO_F 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO"); 
						sCOIL_OUTDIA_F 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");  
						lngsCOIL_OUTDIA_F	= Long.parseLong(sCOIL_OUTDIA_F);
					//우측+1 1단
					} else if (sCOIL_GP.equals("G")) {
						sCOIL_NO_G 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO"); 
						sCOIL_OUTDIA_G 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");  
						lngsCOIL_OUTDIA_G	= Long.parseLong(sCOIL_OUTDIA_G);
					}
				}
     	    		
     	    	if((!sCOIL_NO_A.equals("")) 
     	    			&& ( lngsCOIL_OUTDIA_B >= lngsCOIL_OUTDIA_C) 
     	    			&& ( lngsCOIL_OUTDIA_E >= lngsCOIL_OUTDIA_C)){
       				ydUtils.putLog(szSessionName, szMethodName, "기울기 공식적용 ", YdConstant.DEBUG);

       				szRtnVal = searchCoilYdGdsCline( szYdStkColGp, szYdStkBedNo, szYdStkLyrNo, sGRP_GP_LOC
       						                        ,sCOIL_NO_A,sCOIL_OUTDIA_A
       						                        ,sCOIL_NO_B,sCOIL_OUTDIA_B
       						                        ,sCOIL_NO_C,sCOIL_OUTDIA_C
               				                        ,sCOIL_NO_D,sCOIL_OUTDIA_D
               				                        ,sCOIL_NO_E,sCOIL_OUTDIA_E);

               		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
            			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 2단 기울기 편차 불가 합니다..";
    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    					outRecord.setField("RTN_CD" 	, "-1");	
    					outRecord.setField("RTN_MSG" 	, szMsg);	
    					return outRecord;
            		}
     	    	} else if((!sCOIL_NO_F.equals("")) && (!sCOIL_NO_G.equals("")) 
     	    			&& ( lngsCOIL_OUTDIA_C >= lngsCOIL_OUTDIA_E ) 
     	    			&& ( lngsCOIL_OUTDIA_G >= lngsCOIL_OUTDIA_E)){	

     	    		ydUtils.putLog(szSessionName, szMethodName, "기울기 공식적용 ", YdConstant.DEBUG);

       				szRtnVal = searchCoilYdGdsCline( szYdStkColGp, szYdStkBedNo, szYdStkLyrNo, sGRP_GP_LOC
       												,sCOIL_NO_D,sCOIL_OUTDIA_D
       												,sCOIL_NO_C,sCOIL_OUTDIA_C
       												,sCOIL_NO_E,sCOIL_OUTDIA_E
       						                        ,sCOIL_NO_F,sCOIL_OUTDIA_F
       						                        ,sCOIL_NO_G,sCOIL_OUTDIA_G);

               		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
            			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 2단 기울기 편차 불가 합니다..";
    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    					outRecord.setField("RTN_CD" 	, "-1");	
    					outRecord.setField("RTN_MSG" 	, szMsg);	
    					return outRecord;
            		}
            	} else {
               		ydUtils.putLog(szSessionName, szMethodName, "기울기 공식적용 적용안함", YdConstant.DEBUG);
               	}	
         	}        	
 
           	// 마지막 조건 check
        	ydUtils.putLog(szSessionName, szMethodName, "===================선택된 저장위치가 공위치인지 CHECK====================", YdConstant.INFO);
			
        	recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
			recPara.setField("YD_STK_LYR_NO", szYdStkLyrNo);
			
        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
        	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyr*/
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBed, 0);
        			
        	if (intRtnVal <= 0) {
    			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 마지막으로 적치단 CHECK ERROR.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
        	}
        	
        	rsBed.first();
			recBed =JDTORecordFactory.getInstance().create();
			recBed = rsBed.getRecord();
			String sYD_STK_LYR_MTL_STAT = recBed.getFieldString("YD_STK_LYR_MTL_STAT");
			
			if(!sYD_STK_LYR_MTL_STAT.equals("E")){
	   			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 비워있는 위치가 없습니다. CHECK ERROR.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
 				
			}
        	
        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 2단 중량편차,외경간격,외경편차   END★★★★★", YdConstant.INFO);
        	
        	outRecord.setField("RTN_BED" 			, szYdStkColGp + szYdStkBedNo + szYdStkLyrNo);	
           	outRecord.setField("RTN_COLGP" 			, szYdStkColGp);	
        	outRecord.setField("RTN_BEDNO" 			, szYdStkBedNo);	
        	outRecord.setField("RTN_LYRNO" 			, szYdStkLyrNo);	
        	outRecord.setField("RTN_STLNO" 			, szStlNo);	
           	outRecord.setField("RTN_LEFT_COLGP" 	, szLeftColGp);	
        	outRecord.setField("RTN_LEFT_BEDNO" 	, szLeftBedNo);	
        	outRecord.setField("RTN_LEFT_LYRNO" 	, szLeftLyrNo);	
        	outRecord.setField("RTN_LEFT_STLNO" 	, szLeftStlNo);	
           	outRecord.setField("RTN_RIGHT_COLGP" 	, szRightColGp);	
        	outRecord.setField("RTN_RIGHT_BEDNO" 	, szRightBedNo);	
        	outRecord.setField("RTN_RIGHT_LYRNO" 	, szRightLyrNo);	
        	outRecord.setField("RTN_RIGHT_STLNO" 	, szRightStlNo);	
 			outRecord.setField("RTN_CD" 			, "1");	
 	       	outRecord.setField("RTN_TO_BEDNO"		, szYdStkColGp.substring(1,6) +szYdStkLyrNo.substring(2,3) + szYdStkBedNo);	
 	        ydUtils.putLog(szSessionName, szMethodName, szYdStkColGp.substring(1,6) +szYdStkLyrNo.substring(2,3) + szYdStkBedNo, YdConstant.INFO);

			return outRecord;
			
		} catch(Exception e) {
			szMsg = "코일제품야드 To위치검색 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" 	, "0");	
			outRecord.setField("RTN_MSG" 	, szMsg);	
			return outRecord;
		}
		
	} 
	

	/**
	 * [A] 오퍼레이션명 : 제품코일적치가능공통
	 *     : 작업예약에서는 적치단이 중간에
	 *       스케쥴에서는   적치단이 마지막에
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord CoilGdsLyrBaseCheckAuto(String szStlNo, JDTORecord inRecord ) throws JDTOException  {

		YdStkBedDao     ydStkBedDao  = new YdStkBedDao();
		YdStkLyrDao     ydStkLyrDao  = new YdStkLyrDao();
		YdStkColDao		ydStkColDao  = new YdStkColDao();
		YdEqpDao	ydEqpDao 	= new YdEqpDao();
		JDTORecord outRecord    = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1   = JDTORecordFactory.getInstance().create();


		
		int intRtnVal         	= 0;
		
		String szMsg			= "";
		String szMethodName		= "CoilGdsLyrBaseCheck";
		String szOperationName	= "제품코일적치가능공통";
		
		JDTORecord recPara 		= null;

		
		JDTORecordSet rsMaxBed  = null;
		JDTORecordSet rsBed     = null;
		JDTORecordSet rsOutTemp  = null;

		JDTORecordSet rsStkLyr  = null;
		JDTORecordSet rsStkLyr1 = null;
		JDTORecord recStkLyr    = null;
		JDTORecord recStkLyr1   = null;
		JDTORecord recBed       = null;
		JDTORecord recMaxBed    = null;
		JDTORecord recOutTemp1  = null;
		
		String szYdStkColGp     = null;  // 적치열번호
		String szYdStkBedNo     = null;  // 적치배드번호
		String szYdStkLyrNo     = null;  // 적치단번호
		
		String szOutDia 		= "0";
		String szWidth 			= "0";
		String szThick 			= "0";
		String szWeigth 		= "0";
		
		String szLeftColGp 		= "";
		String szLeftBedNo 		= "";
		String szLeftLyrNo 		= "";
		String szLeftStlNo 		= "";
		String szLeftMtlStat 	= "";
		String szLeftOutDia 	= "0";
		String szLeftWidth 		= "0";
		String szLeftThick 		= "0";
		String szLeftWeigth 	= "0";
		
		String szRightColGp 	= "";
		String szRightBedNo 	= "";
		String szRightLyrNo 	= "";
		String szRightStlNo 	= "";
		String szRightMtlStat 	= "";
		String szRightOutDia 	= "0";
		String szRightWidth 	= "0";
		String szRightThick 	= "0";
		String szRightWeigth 	= "0";
		
		String szBRE_YDB701_4_USE_YN = null;
		String szBRE_YDB701_6_USE_YN = null;
		String szISSUE_TG_YN	= null;

		String szYD_COIL_OUTDIA_GRP_GP = "";  //
		String sCOIL_OUTDIA_GRP_GP  = "";  //대상재 외경군
		String sYD_STK_COL_W_GP = "";
		
		int   intCompBedNo 		= 0;
		
		String szMaxBedNo 		= null;
		String sCOIL_W_GP 		= "";		
		String szRtnVal 		= "";

		String sGRP_GP_LOC      = "";
		String sCOIL_NO_A 		= "";
		String sCOIL_OUTDIA_A 	= "";
		String sCOIL_NO_B 		= "";
		String sCOIL_OUTDIA_B 	= "";
		String sCOIL_NO_C 		= "";
		String sCOIL_OUTDIA_C 	= "";
		String sCOIL_NO_D 		= "";
		String sCOIL_OUTDIA_D 	= "";
		String sCOIL_NO_E 		= "";
		String sCOIL_OUTDIA_E 	= "";
		String sCOIL_NO_F 		= "";
		String sCOIL_OUTDIA_F 	= "";
		String sCOIL_NO_G 		= "";
		String sCOIL_OUTDIA_G 	= "";
		String sGRP_GP_CD       = "";
		String sUSE_YN          = "";
		String sBRE_CHK1        = "N";
		String sBRE_CHK2        = "N";
		String sBRE_CHK3        = "N";
		String sBRE_CHK5        = "N";
		String szYD_STK_COL_GP_YN = "";
		//String szBreSkipYn      = "Y";
		
		
		long   lngsCOIL_OUTDIA_A  	= 0;           //코일외경
		long   lngsCOIL_OUTDIA_B   	= 0;           
		long   lngsCOIL_OUTDIA_C   	= 0;           
		long   lngsCOIL_OUTDIA_D   	= 0;           
		long   lngsCOIL_OUTDIA_E   	= 0;           
		long   lngsCOIL_OUTDIA_F   	= 0;           
		long   lngsCOIL_OUTDIA_G   	= 0;           
		
		
		String sCLINE_CHK       = "";

		try {
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일 제품코일적치가능공통 AutoCrn START★★★★★", YdConstant.INFO);

			szYdStkColGp = ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_COL_GP");
			szYdStkBedNo = ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_NO");
			szYdStkLyrNo = ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LYR_NO");
			//szBreSkipYn = ydDaoUtils.paraRecChkNull(inRecord, "BRE_CHK1_SKIP_YN");
			
			//지포장 제품적치 체크
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			rsOutTemp = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    /*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedYardWrap*/
        	intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsOutTemp, 314);
			
        	if (intRtnVal > 0) {
            	rsOutTemp.first();
            	recOutTemp1 = rsOutTemp.getRecord();
            	szYD_STK_COL_GP_YN = ydDaoUtils.paraRecChkNull(recOutTemp1, "YD_STK_COL_GP_YN");
            	if(szYD_STK_COL_GP_YN.equals("Y")) {
            		szMsg = "적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 지포장 제품적치 체크 시작:" ;//+ szBreSkipYn ;
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            		outRecord1 = this.CoilGdsLyrBaseCheckYardWrap(szStlNo, inRecord);
            		return outRecord1;
            	}	
			}
        	
        	
        	
        	
//			hun 기존 고정SKID 주석
			//ABC는 별도 로직 구현함			
//			if(szYdStkColGp.substring(1,2).equals("A")||
//			   szYdStkColGp.substring(1,2).equals("B")||
//			   szYdStkColGp.substring(1,2).equals("C")){
//				
//				szMsg = "적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") ABC동  제품적치 체크 시작:" ;//+ szBreSkipYn ;
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				outRecord1 = this.CoilGdsLyrBaseCheckABC(szStlNo, inRecord);
//				return outRecord1;
//			}
//        	150723 hun 신규 SKID 구분 추가
        	if(ydEqpDao.chkFixedSkid(szYdStkColGp)){
        		szMsg = "적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") Auto 크레인 체크 시작: 기존ABC동" ;
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			outRecord1 = this.CoilGdsLyrBaseCheckABC(szStlNo, inRecord);
				return outRecord1;
        		
        	}
 
        	
			
			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") BED로 검색시작   ------>저장위치 수정유무:" ;//+ szBreSkipYn ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			if((szYdStkColGp.substring(2, 3).equals("0"))||
					(szYdStkColGp.substring(2, 3).equals("1"))||
					(szYdStkColGp.substring(2, 3).equals("2"))||
					(szYdStkColGp.substring(2, 3).equals("3"))||
					(szYdStkColGp.substring(2, 3).equals("4"))||
					(szYdStkColGp.substring(2, 3).equals("5"))||
					(szYdStkColGp.substring(2, 3).equals("6"))) {
        	} else {	
        		szRtnVal = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
        		ydUtils.putLog(szSessionName, szMethodName, "C열연코일제품야드 설비위치=>>>>"+szRtnVal, YdConstant.INFO);
               	outRecord.setField("RTN_BED" 			, szYdStkColGp + szYdStkBedNo + szYdStkLyrNo);	
               	outRecord.setField("RTN_COLGP" 			, szYdStkColGp);	
            	outRecord.setField("RTN_BEDNO" 			, szYdStkBedNo);	
            	outRecord.setField("RTN_LYRNO" 			, szYdStkLyrNo);	
            	outRecord.setField("RTN_STLNO" 			, szStlNo);	
        		outRecord.setField("RTN_CD" 			, "1");	
	 	       	outRecord.setField("RTN_TO_BEDNO"		, szYdStkColGp.substring(1,6) +szYdStkLyrNo.substring(2,3) + szYdStkBedNo);	
							
				return outRecord;
//        		return szRtnVal;
        	}
			
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("STL_NO"		, szStlNo);
			rsMaxBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    /*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getCoilMaxBedNoByColGp*/
        	intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsMaxBed, 303);
			
        	if (intRtnVal <= 0) {
				szMsg = "적치열(" + szYdStkColGp + ")로 MAX배드 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
        	
        	rsMaxBed.first();
        	recMaxBed = rsMaxBed.getRecord();
        	
        	szMaxBedNo = ydDaoUtils.paraRecChkNull(recMaxBed, "MAX_BED_NO");
        	szYD_COIL_OUTDIA_GRP_GP = ydDaoUtils.paraRecChkNull(recMaxBed, "YD_COIL_OUTDIA_GRP_GP");
        	sYD_STK_COL_W_GP 		= ydDaoUtils.paraRecChkNull(recMaxBed, "YD_STK_COL_W_GP");
        	sCOIL_OUTDIA_GRP_GP		= ydDaoUtils.paraRecChkNull(recMaxBed, "OUTDIA_GRP_GP");
        	sCOIL_W_GP				= ydDaoUtils.paraRecChkNull(recMaxBed, "COIL_W_GP");
        	
 				
        	ydUtils.putLog(szSessionName, szMethodName, "szYD_COIL_OUTDIA_GRP_GP-->" + szYD_COIL_OUTDIA_GRP_GP, YdConstant.INFO);
        	ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_COL_W_GP-->" + sYD_STK_COL_W_GP, YdConstant.INFO);
        	ydUtils.putLog(szSessionName, szMethodName, "sCOIL_OUTDIA_GRP_GP-->" + sCOIL_OUTDIA_GRP_GP, YdConstant.INFO);
        	
	
 			if ("002".equals(szYdStkLyrNo)) {  // 검색적치단이 2단일 경우
				szLeftColGp = szYdStkColGp;
				szLeftBedNo = szYdStkBedNo;
				szLeftLyrNo = "001";
				
				szRightColGp = szYdStkColGp;
				intCompBedNo = Integer.parseInt(szYdStkBedNo);
				if(szYD_COIL_OUTDIA_GRP_GP.equals("A")) {
					intCompBedNo = intCompBedNo + 2;	
				} else if(szYD_COIL_OUTDIA_GRP_GP.equals("B")) {
					intCompBedNo = intCompBedNo + 3;	
				} else if(szYD_COIL_OUTDIA_GRP_GP.equals("C")) {
					intCompBedNo = intCompBedNo + 4;	
				}
				
				if (intCompBedNo >= 10) {
					szRightBedNo = "" + intCompBedNo;
				} else {
					szRightBedNo = "0" + intCompBedNo;
				}
				szRightLyrNo = "001";
			}
			
			if ("001".equals(szYdStkLyrNo)) {  // 검색적치단이 1단일 경우
				
				
	        	
	        	if("01".equals(szYdStkBedNo)) { // 검색BED가 1인 경우
	        		szLeftColGp = szYdStkColGp;
					szLeftBedNo = szYdStkBedNo;
					szLeftLyrNo = "001";
					
					szRightColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					
					if(szYD_COIL_OUTDIA_GRP_GP.equals("A")) {
						intCompBedNo = intCompBedNo + 2;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("B")) {
						intCompBedNo = intCompBedNo + 3;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("C")) {
						intCompBedNo = intCompBedNo + 4;	
					}

					if (intCompBedNo >= 10) {
						szRightBedNo = "" + intCompBedNo;
					} else {
						szRightBedNo = "0" + intCompBedNo;
					}
					szRightLyrNo = "001";
	        	} else if (szMaxBedNo.equals(szYdStkBedNo)) { // 검색BED가 마지막인 경우
	        		
	        		szLeftColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					if(szYD_COIL_OUTDIA_GRP_GP.equals("A")) {
						intCompBedNo = intCompBedNo - 2;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("B")) {
						intCompBedNo = intCompBedNo - 3;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("C")) {
						intCompBedNo = intCompBedNo - 4;	
					}
					if (intCompBedNo >= 10) {
						szLeftBedNo = "" + intCompBedNo;
					} else {
						szLeftBedNo = "0" + intCompBedNo;
					}
					szLeftLyrNo = "001";
	        		
	        		
	        		szRightColGp = szYdStkColGp;
					szRightBedNo = szYdStkBedNo;
					szRightLyrNo = "001";
	        	} else {
	        		
	        		szLeftColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					if(szYD_COIL_OUTDIA_GRP_GP.equals("A")) {
						intCompBedNo = intCompBedNo - 2;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("B")) {
						intCompBedNo = intCompBedNo - 3;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("C")) {
						intCompBedNo = intCompBedNo - 4;	
					}
					if (intCompBedNo >= 10) {
						szLeftBedNo = "" + intCompBedNo;
					} else {
						szLeftBedNo = "0" + intCompBedNo;
					}
					if (intCompBedNo >= 10) {
						szLeftBedNo = "" + intCompBedNo;
					} else {
						szLeftBedNo = "0" + intCompBedNo;
					}
					szLeftLyrNo = "001";
					
					szRightColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					if(szYD_COIL_OUTDIA_GRP_GP.equals("A")) {
						intCompBedNo = intCompBedNo + 2;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("B")) {
						intCompBedNo = intCompBedNo + 3;	
					} else if(szYD_COIL_OUTDIA_GRP_GP.equals("C")) {
						intCompBedNo = intCompBedNo + 4;	
					}

					if (intCompBedNo >= 10) {
						szRightBedNo = "" + intCompBedNo;
					} else {
						szRightBedNo = "0" + intCompBedNo;
					}
					szRightLyrNo = "001";
	        	}
				
	        	
			}
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 왼쪽,오른쪽  적치단 조회 START★★★★★", YdConstant.INFO);

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("COIL_NO", szStlNo);
			recPara.setField("YD_STK_COL_GPL", szLeftColGp+szLeftBedNo+szLeftLyrNo);
			recPara.setField("YD_STK_COL_GPR", szRightColGp+szRightBedNo+szRightLyrNo);
			recPara.setField("YD_STK_COL_GP1", szLeftColGp);
			recPara.setField("YD_STK_BED_NO1", szLeftBedNo);
			recPara.setField("YD_STK_LYR_NO1", szLeftLyrNo);
			recPara.setField("YD_STK_COL_GP2", szRightColGp);
			recPara.setField("YD_STK_BED_NO2", szRightBedNo);
			recPara.setField("YD_STK_LYR_NO2", szRightLyrNo);

			rsStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilCOMM_AUTO*/			
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr, 404);
 	    	if (intRtnVal <= 0) {
				szMsg = "확인:"+szStlNo+"적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;

			}
        	if (intRtnVal > 3) {
				szMsg = "확인:"+szStlNo+"적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")로 조회중 error 발생! 적치건수:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
 	    	
			for(int Loop_i = 1; Loop_i <= rsStkLyr.size(); Loop_i++) {
				rsStkLyr.absolute(Loop_i);
				recStkLyr = rsStkLyr.getRecord();
				
				String sTARGET_GP = ydDaoUtils.paraRecChkNull(recStkLyr, "TARGET_GP");
//대상
				if (sTARGET_GP.equals("T")) {
					if (Loop_i == 1) {
						szStlNo 			= ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO"); 
						szOutDia 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_OUTDIA"),"0");  
						szThick 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_T"),"0");  
						szWidth 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_W"),"0");  
						szWeigth 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_WT"),"0"); 
					} else {
						szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD" 	, "-1");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
					}
				}
				if (sTARGET_GP.equals("L")) {	
					if (Loop_i == 2) {
			        	szLeftStlNo 		= ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO"); 
			        	szLeftMtlStat 		= ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_MTL_STAT");
						szLeftOutDia 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_OUTDIA"),"0");  
						szLeftThick 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_T"),"0");  
						szLeftWidth 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_W"),"0");  
						szLeftWeigth 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_WT"),"0"); 
						
						szBRE_YDB701_4_USE_YN = StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "BRE_YDB701_4_USE_YN"),"N");  
						szBRE_YDB701_6_USE_YN = StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "BRE_YDB701_6_USE_YN"),"N");
						szISSUE_TG_YN		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "ISSUE_TG_YN"),"N");
			
			        	if ("002".equals(szYdStkLyrNo)) {
//			        		hun 150826 1단코일이 예약(D)상태에서 2단 적치 불가 
//			        		if (!("C".equals(szLeftMtlStat) || "D".equals(szLeftMtlStat))) {
			        		if (!"C".equals(szLeftMtlStat) || ("D".equals(szLeftMtlStat) && "Y".equals(szBRE_YDB701_6_USE_YN)) ) {
			        			szMsg = "확인:"+szStlNo+"좌측1적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 상태가 적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}
			        		
			        		if("Y".equals(szBRE_YDB701_4_USE_YN) && "Y".equals(szISSUE_TG_YN)) {
			        			//hun 출고대상코일위적치금지 이면서 출고대상이면 2단적치 불가
			        			szMsg = "확인:"+szStlNo+"좌측1적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 코일 : "+ szLeftStlNo +"가 출고대상으로  적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}
			        	}
					} else {
						szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD" 	, "-1");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
				
					}

				}
				
				if (sTARGET_GP.equals("R")) {	
					if (Loop_i == 3) {
			        	szRightStlNo 		= ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO");
			        	szRightMtlStat 		= ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_MTL_STAT");
						szRightOutDia 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_OUTDIA"),"0");  
						szRightThick 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_T"),"0");  
						szRightWidth 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_W"),"0");  
						szRightWeigth 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_WT"),"0");
						
						szBRE_YDB701_4_USE_YN = StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "BRE_YDB701_4_USE_YN"),"N");  
						szBRE_YDB701_6_USE_YN = StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "BRE_YDB701_6_USE_YN"),"N");
						szISSUE_TG_YN		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "ISSUE_TG_YN"),"N");
						
			        	if ("002".equals(szYdStkLyrNo)) {
//			        		hun 150826 1단코일이 예약(D)상태에서 2단 적치 불가 
//			        		if (!("C".equals(szRightMtlStat) || "D".equals(szRightMtlStat))) {
			        		if (!"C".equals(szRightMtlStat) || ("D".equals(szRightMtlStat) && "Y".equals(szBRE_YDB701_6_USE_YN)) ) {
			        			szMsg = "확인:"+szStlNo+"우측1적치단(" + szRightColGp + szRightBedNo + szRightLyrNo + ")의 상태가 적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}
			        		
			        		if("Y".equals(szBRE_YDB701_4_USE_YN) && "Y".equals(szISSUE_TG_YN)) {
			        			//출고대상코일위적치금지 이면서 출고대상이면 2단적치 불가
			        			szMsg = "확인:"+szStlNo+"우측1적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 코일 : "+ szRightStlNo +"가 출고대상으로  적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}
			        		
			        	}
					} else {
						szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD" 	, "-1");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
				
					}
				}
			}
			// 왼쪽,오른쪽  적치단 로그 Start ----------------------------------------------------------------------------------------------------------
			szMsg = "확인:"+szStlNo+"★★★ 좌측1적치단(" + szLeftColGp + "-" + szLeftBedNo + "-" +szLeftLyrNo + ") 코일: "+ szLeftStlNo + " 상태: " +  szLeftMtlStat + " 외경: " + szLeftOutDia + " 중량: " + szLeftWeigth;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			szMsg = "확인:"+szStlNo+"★★★ 우측1적치단(" + szRightColGp + "-" + szRightBedNo + "-" +szRightLyrNo + ") 코일: "+ szRightStlNo + " 상태: " +  szRightMtlStat + " 외경: " + szRightOutDia + " 중량: " + szRightWeigth;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			// 왼쪽,오른쪽  적치단 로그 End ------------------------------------------------------------------------------------------------------------

        	//BRE 사용 조건 READ	
			JDTORecordSet rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
        	recInTemp.setField("TEMP" 	, "0");	
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB701*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 701);
			if(intRtnVal <= 0) {
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
    		}
			
			for(int Loop1_i = 1; Loop1_i <= rsResult.size(); Loop1_i++) {
				JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
				rsResult.absolute(Loop1_i);
				recOutTemp.setRecord(rsResult.getRecord());

				sGRP_GP_CD	= ydDaoUtils.paraRecChkNull(recOutTemp, "GRP_GP_CD");	//CHECK_GPT		
				sUSE_YN		= ydDaoUtils.paraRecChkNull(recOutTemp, "USE_YN");	//사용가능 Y,N	
				
				if((sGRP_GP_CD.equals("1")) && (sUSE_YN.equals("Y"))) {
					sBRE_CHK1 = "Y";
				} else if((sGRP_GP_CD.equals("2")) && (sUSE_YN.equals("Y"))) {
					sBRE_CHK2 = "Y";
				} else if((sGRP_GP_CD.equals("5")) && (sUSE_YN.equals("Y"))) {
					sBRE_CHK5 = "Y";
				}
			}		

    		ydUtils.putLog(szSessionName, szMethodName, "sBRE_CHK1:" + sBRE_CHK1, YdConstant.INFO);
    		ydUtils.putLog(szSessionName, szMethodName, "sBRE_CHK2:" + sBRE_CHK2, YdConstant.INFO);
    		ydUtils.putLog(szSessionName, szMethodName, "sBRE_CHK5:" + sBRE_CHK5, YdConstant.INFO);

			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 왼쪽,오른쪽  적치단 조회 END★★★★★", YdConstant.INFO);
        	
        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 중량편차,외경간격,외경편차   START★★★★★", YdConstant.INFO);
    
			szMsg = "대상(" + sCOIL_OUTDIA_GRP_GP +")"+ intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	       	sYD_STK_COL_W_GP 		= ydDaoUtils.paraRecChkNull(recMaxBed, "YD_STK_COL_W_GP");
        	sCOIL_OUTDIA_GRP_GP		= ydDaoUtils.paraRecChkNull(recMaxBed, "OUTDIA_GRP_GP");
        	sCOIL_W_GP				= ydDaoUtils.paraRecChkNull(recMaxBed, "COIL_W_GP");
 			
			
        	ydUtils.putLog(szSessionName, szMethodName, "==================== 폭 계산한다.====================", YdConstant.INFO);
        	if(!sYD_STK_COL_W_GP.equals(sCOIL_W_GP)){
    			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 코일"+ sCOIL_W_GP+ "과 적치위치"+ sYD_STK_COL_W_GP+ "구분이 틀립니다..";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
        	}
        	
        	ydUtils.putLog(szSessionName, szMethodName, "==================== 외경군 계산한다.====================", YdConstant.INFO);
        	if(!sCOIL_OUTDIA_GRP_GP.equals(szYD_COIL_OUTDIA_GRP_GP)){
    			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 코일"+ sCOIL_OUTDIA_GRP_GP+ "과 적치위치"+ szYD_COIL_OUTDIA_GRP_GP+ "군이 틀립니다..";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
        	}
        	
        	
        	// 1단일 경우 외경편차를 계산한다.
        	ydUtils.putLog(szSessionName, szMethodName, "====================1단일 경우 외경편차를 계산한다.====================", YdConstant.INFO);
        	if ("001".equals(szYdStkLyrNo)) {
        		szRtnVal = searchCoilGdsYdOutDiaDiffCheck( szStlNo, szYdStkLyrNo, szOutDia, szLeftStlNo, szLeftOutDia,  szRightStlNo, szRightOutDia);
				
        		ydUtils.putLog(szSessionName, szMethodName, "외경CHECK :" + szRtnVal, YdConstant.DEBUG);
				    		
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 코일과 외경차이(180mm)로 적치가 불가능한 1 단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
         	// 1단일 경우 폭기준를 계산한다. 
        	if ("001".equals(szYdStkLyrNo)) {
        		ydUtils.putLog(szSessionName, szMethodName, "====================1단일 경우 폭기준를 계산한다.====================", YdConstant.INFO);
            	szRtnVal = searchCoilYdGdsWidthDiffCheck( szStlNo, szYdStkLyrNo, szWidth, szLeftStlNo, szLeftWidth,  szRightStlNo, szRightWidth);
        		
            	ydUtils.putLog(szSessionName, szMethodName, "폭CHECK :" + szRtnVal, YdConstant.DEBUG);
        		        		
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 코일과 폭기준(180mm)로 적치가 불가능한 1단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
        	
         	// 1단일 경우 폭간섭를 계산한다. (저장위치수정인 경우 삭제 한다.)
        	if (sBRE_CHK1.equals("Y") && ("001".equals(szYdStkLyrNo)) //&& szBreSkipYn.equals("Y")
        			) {
        		ydUtils.putLog(szSessionName, szMethodName, "====================1단일 경우 폭간섭를 계산한다.====================", YdConstant.INFO);
            	szRtnVal = searchCoilYdGdsWidthDiffCheck3( szStlNo, szYdStkColGp, szYdStkBedNo, szYdStkLyrNo ,  sYD_STK_COL_W_GP, szWidth ,szOutDia,szMaxBedNo);
        		
            	ydUtils.putLog(szSessionName, szMethodName, "폭간섭CHECK :" + szRtnVal, YdConstant.DEBUG);
        		        		
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일 폭 간섭으로 적치가 불가능한 위치입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}

        	
        	// 2단일 경우 중량/폭편차를 계산한다.
        	ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 중량/폭편차를 계산한다.====================", YdConstant.INFO);
        	if ("002".equals(szYdStkLyrNo) && !"".equals(szLeftStlNo) && !"".equals(szRightStlNo)) {
        		szRtnVal = searchCoilGdsYdWtCheck(szStlNo, szThick,szWidth, szWeigth, szLeftStlNo, szLeftThick,szLeftWidth, szLeftWeigth,szRightStlNo,szRightThick,szRightWidth, szRightWeigth);
 
               	ydUtils.putLog(szSessionName, szMethodName, "중량CHECK :" + szRtnVal, YdConstant.DEBUG);
                
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과 중량/폭편차로 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
           		} else if (szRtnVal.equals("WID_FAILURE")) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과폭편차로 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
           		} else if (szRtnVal.equals("WGT_FAILURE")) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과중량편차로 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
        	
        	// 150904 hun 횡행좌표 거리 계산 값에 의한 외경 CHECK
        	ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 횡행좌표 거리 계산 값에 의한 외경체크====================", YdConstant.INFO);
        	if (sBRE_CHK5.equals("Y") && "002".equals(szYdStkLyrNo)) {
        		szRtnVal = searchCoilGdsYdOutDiaOverRunCheck( szYdStkColGp, szYdStkBedNo, szStlNo, szYdStkLyrNo, szOutDia, szLeftStlNo, szLeftOutDia,  szRightStlNo, szRightOutDia);
              	ydUtils.putLog(szSessionName, szMethodName, "횡행좌표 거리 계산 값에 의한 외경 CHECK :" + szRtnVal, YdConstant.DEBUG);
        		
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일이 횡행좌표 거리 계산 값에 의한 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
        	
        	// 2단일 경우 외경간격을 계산한다.(OK)
        	ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 외경간격을 계산한다.====================", YdConstant.INFO);
        	if ("002".equals(szYdStkLyrNo) && !"".equals(szLeftStlNo) && !"".equals(szRightStlNo)) {
        		szRtnVal = searchCoilGdsYdOutDiaIntervalCheck( szYdStkColGp, szYdStkBedNo, szStlNo, szYdStkLyrNo, szOutDia, szLeftStlNo, szLeftOutDia,  szRightStlNo, szRightOutDia);
              	ydUtils.putLog(szSessionName, szMethodName, "외경간격CHECK :" + szRtnVal, YdConstant.DEBUG);
        		
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일이 외경간격편차로 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
           	
        	// 2단일 경우 외경편차1를 계산한다.
        	ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 외경편차1를 계산한다.====================", YdConstant.INFO);
        	if ("002".equals(szYdStkLyrNo) && !"".equals(szLeftStlNo) && !"".equals(szRightStlNo)) {
        		szRtnVal = searchCoilYdGdsWidthDiffCheck2( szStlNo, szYdStkLyrNo, szOutDia, szLeftStlNo, szLeftOutDia,  szRightStlNo, szRightOutDia);
 
               	ydUtils.putLog(szSessionName, szMethodName, "외경 :" + szRtnVal, YdConstant.DEBUG);
                
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과 외경편차1로 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}        	
           	

 
        	// 2단일 경우 기울기 공식 적용.
        	ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 기울기를 계산한다.====================", YdConstant.INFO);
// 2단 우측에 코일이 있을 경우
        	if (sBRE_CHK2.equals("Y") && ("002".equals(szYdStkLyrNo))) {
 
    			rsStkLyr1 = JDTORecordFactory.getInstance().createRecordSet("Temp");

    			recPara = JDTORecordFactory.getInstance().create();
    			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
    			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
    			recPara.setField("YD_STK_LYR_NO", szYdStkLyrNo);
       	    
    			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilCline*/			
            	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr1, 600);
     	    	if (intRtnVal <= 0) {
    				szMsg = "확인:"+szStlNo+"적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				outRecord.setField("RTN_CD" 	, "-1");	
    				outRecord.setField("RTN_MSG" 	, szMsg);	
    				return outRecord;
    			}
     	    	
     	    	
     	    	rsStkLyr1.first();
     	    	recStkLyr1 = JDTORecordFactory.getInstance().create();
     	    	recStkLyr1 = rsStkLyr1.getRecord();

     	    	for(int Loop_i = 1; Loop_i <= rsStkLyr1.size(); Loop_i++) {
					rsStkLyr1.absolute(Loop_i);
					recStkLyr1 = rsStkLyr1.getRecord();
					
					String sCOIL_GP = ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_GP");
					//좌측2단
					if (sCOIL_GP.equals("A")) {
						sCOIL_NO_A 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO");
						sCOIL_OUTDIA_A 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");
						lngsCOIL_OUTDIA_A	= Long.parseLong(sCOIL_OUTDIA_A);
						sGRP_GP_LOC		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "GRP_GP_LOC"),"0");  
					//좌측1단
					} else if (sCOIL_GP.equals("B")) {
						sCOIL_NO_B 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO"); 
						sCOIL_OUTDIA_B 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");  
						lngsCOIL_OUTDIA_B	= Long.parseLong(sCOIL_OUTDIA_B);
					//대상1단
					} else if (sCOIL_GP.equals("C")) {
						sCOIL_NO_C 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO"); 
						sCOIL_OUTDIA_C 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");  
						lngsCOIL_OUTDIA_C	= Long.parseLong(sCOIL_OUTDIA_C);
					//대상2단 : 목적
					} else if (sCOIL_GP.equals("D")) {
//						sCOIL_NO_D 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO"); 
//						sCOIL_OUTDIA_D 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");  
						sCOIL_NO_D 		= szStlNo;
						sCOIL_OUTDIA_D 	= szOutDia;  
						lngsCOIL_OUTDIA_D	= Long.parseLong(sCOIL_OUTDIA_D);
					//우측1단
					} else if (sCOIL_GP.equals("E")) {
						sCOIL_NO_E 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO"); 
						sCOIL_OUTDIA_E 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");  
						lngsCOIL_OUTDIA_E	= Long.parseLong(sCOIL_OUTDIA_E);
					//우측2단
					} else if (sCOIL_GP.equals("F")) {
						sCOIL_NO_F 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO"); 
						sCOIL_OUTDIA_F 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");  
						lngsCOIL_OUTDIA_F	= Long.parseLong(sCOIL_OUTDIA_F);
					//우측+1 1단
					} else if (sCOIL_GP.equals("G")) {
						sCOIL_NO_G 		= ydDaoUtils.paraRecChkNull(recStkLyr1, "STL_NO"); 
						sCOIL_OUTDIA_G 	= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr1, "COIL_OUTDIA"),"0");  
						lngsCOIL_OUTDIA_G	= Long.parseLong(sCOIL_OUTDIA_G);
					}
				}
     	    		
     	    	if((!sCOIL_NO_A.equals("")) 
     	    			&& ( lngsCOIL_OUTDIA_B >= lngsCOIL_OUTDIA_C) 
     	    			&& ( lngsCOIL_OUTDIA_E >= lngsCOIL_OUTDIA_C)){
       				ydUtils.putLog(szSessionName, szMethodName, "기울기 공식적용 ", YdConstant.DEBUG);

       				szRtnVal = searchCoilYdGdsCline( szYdStkColGp, szYdStkBedNo, szYdStkLyrNo, sGRP_GP_LOC
       						                        ,sCOIL_NO_A,sCOIL_OUTDIA_A
       						                        ,sCOIL_NO_B,sCOIL_OUTDIA_B
       						                        ,sCOIL_NO_C,sCOIL_OUTDIA_C
               				                        ,sCOIL_NO_D,sCOIL_OUTDIA_D
               				                        ,sCOIL_NO_E,sCOIL_OUTDIA_E);

               		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
            			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 2단 기울기 편차 불가 합니다..";
    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    					outRecord.setField("RTN_CD" 	, "-1");	
    					outRecord.setField("RTN_MSG" 	, szMsg);	
    					return outRecord;
            		}
     	    	} else if((!sCOIL_NO_F.equals("")) && (!sCOIL_NO_G.equals("")) 
     	    			&& ( lngsCOIL_OUTDIA_C >= lngsCOIL_OUTDIA_E ) 
     	    			&& ( lngsCOIL_OUTDIA_G >= lngsCOIL_OUTDIA_E)){	

     	    		ydUtils.putLog(szSessionName, szMethodName, "기울기 공식적용 ", YdConstant.DEBUG);

       				szRtnVal = searchCoilYdGdsCline( szYdStkColGp, szYdStkBedNo, szYdStkLyrNo, sGRP_GP_LOC
       												,sCOIL_NO_D,sCOIL_OUTDIA_D
       												,sCOIL_NO_C,sCOIL_OUTDIA_C
       												,sCOIL_NO_E,sCOIL_OUTDIA_E
       						                        ,sCOIL_NO_F,sCOIL_OUTDIA_F
       						                        ,sCOIL_NO_G,sCOIL_OUTDIA_G);

               		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
            			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 2단 기울기 편차 불가 합니다..";
    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    					outRecord.setField("RTN_CD" 	, "-1");	
    					outRecord.setField("RTN_MSG" 	, szMsg);	
    					return outRecord;
            		}
            	} else {
               		ydUtils.putLog(szSessionName, szMethodName, "기울기 공식적용 적용안함", YdConstant.DEBUG);
               	}	
         	}        	
 
           	// 마지막 조건 check
        	ydUtils.putLog(szSessionName, szMethodName, "===================선택된 저장위치가 공위치인지 CHECK====================", YdConstant.INFO);
			
        	recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
			recPara.setField("YD_STK_LYR_NO", szYdStkLyrNo);
			
        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
        	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyr*/
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBed, 0);
        			
        	if (intRtnVal <= 0) {
    			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 마지막으로 적치단 CHECK ERROR.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
        	}
        	
        	rsBed.first();
			recBed =JDTORecordFactory.getInstance().create();
			recBed = rsBed.getRecord();
			String sYD_STK_LYR_MTL_STAT = recBed.getFieldString("YD_STK_LYR_MTL_STAT");
			
			if(!sYD_STK_LYR_MTL_STAT.equals("E")){
	   			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 비워있는 위치가 없습니다. CHECK ERROR.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
 				
			}
        	
        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 2단 중량편차,외경간격,외경편차   END★★★★★", YdConstant.INFO);
        	
        	outRecord.setField("RTN_BED" 			, szYdStkColGp + szYdStkBedNo + szYdStkLyrNo);	
           	outRecord.setField("RTN_COLGP" 			, szYdStkColGp);	
        	outRecord.setField("RTN_BEDNO" 			, szYdStkBedNo);	
        	outRecord.setField("RTN_LYRNO" 			, szYdStkLyrNo);	
        	outRecord.setField("RTN_STLNO" 			, szStlNo);	
           	outRecord.setField("RTN_LEFT_COLGP" 	, szLeftColGp);	
        	outRecord.setField("RTN_LEFT_BEDNO" 	, szLeftBedNo);	
        	outRecord.setField("RTN_LEFT_LYRNO" 	, szLeftLyrNo);	
        	outRecord.setField("RTN_LEFT_STLNO" 	, szLeftStlNo);	
           	outRecord.setField("RTN_RIGHT_COLGP" 	, szRightColGp);	
        	outRecord.setField("RTN_RIGHT_BEDNO" 	, szRightBedNo);	
        	outRecord.setField("RTN_RIGHT_LYRNO" 	, szRightLyrNo);	
        	outRecord.setField("RTN_RIGHT_STLNO" 	, szRightStlNo);	
 			outRecord.setField("RTN_CD" 			, "1");	
 	       	outRecord.setField("RTN_TO_BEDNO"		, szYdStkColGp.substring(1,6) +szYdStkLyrNo.substring(2,3) + szYdStkBedNo);	
 	        ydUtils.putLog(szSessionName, szMethodName, szYdStkColGp.substring(1,6) +szYdStkLyrNo.substring(2,3) + szYdStkBedNo, YdConstant.INFO);

			return outRecord;
			
		} catch(Exception e) {
			szMsg = "코일제품야드 To위치검색 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" 	, "0");	
			outRecord.setField("RTN_MSG" 	, szMsg);	
			return outRecord;
		}
		
	} 
	
	/**
	 * [A] 오퍼레이션명 : 이적시 코일야드권상순서 변경
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String upCoilSeqChange( JDTORecord[] inRecordArr, JDTORecord[] outRecordArr) throws JDTOException  {

		JDTORecord outRecord    = JDTORecordFactory.getInstance().create();
		String sUP_POS = "";
		String sUP_POS1 = "";
		String sUP_POS2 = "";
		String sUP_POS3 = "";
		String sFLAG1    = "0";
		int intStlCnt           = 0;
		String szMsg			=	"";
		String szMethodName		=	"upCoilSeqChange";
		String szOperationName	=	"코일야드권상순서 변경";
		
		try {
			
			szMsg = "[Jsp Session  -  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			for(int Loopi = 0; Loopi < inRecordArr.length; Loopi++) {
				sUP_POS =  ydDaoUtils.paraRecChkNull(inRecordArr[Loopi], "UP_POS");
				sUP_POS1 =  sUP_POS.substring(0, 5);
				sUP_POS2 =  sUP_POS.substring(5, 6);
				sUP_POS3 =  sUP_POS.substring(6, 8);
				
				ydUtils.putLog(szSessionName, szMethodName, "sUP_POS1-->"+sUP_POS1+"sUP_POS2-->"+sUP_POS2+"sUP_POS3-->"+sUP_POS3, YdConstant.DEBUG);
				if(sUP_POS.substring(5, 6).equals("2")) { 
					
					outRecordArr[intStlCnt] = JDTORecordFactory.getInstance().create();
					sFLAG1 = "0";
					for(int i = 0; i < outRecordArr.length; i++) {
						String sCHK_UP_POS1 =  ydDaoUtils.paraRecChkNull(outRecordArr[i], "UP_POS");
						if(sUP_POS.equals(sCHK_UP_POS1)) { 
				            i = outRecordArr.length + 1;
				            sFLAG1 = "1";
						}
					}	
					if(sFLAG1.equals("0")){
						outRecordArr[intStlCnt] = JDTORecordFactory.getInstance().create();
						outRecordArr[intStlCnt] = inRecordArr[Loopi];
			            intStlCnt++;
					}
				} else {
				 	for(int j = 0; j < inRecordArr.length; j++) {
						String sCHK_UP_POS2 =  ydDaoUtils.paraRecChkNull(inRecordArr[j], "UP_POS");
					
						sUP_POS =  sUP_POS1 + "2" + sUP_POS3;  //2단 CHECK
						
						if(sUP_POS.equals(sCHK_UP_POS2)) { 
							//2단 등록
							outRecordArr[intStlCnt] = JDTORecordFactory.getInstance().create();
							outRecordArr[intStlCnt] = inRecordArr[j];
				            intStlCnt++;
				            j = inRecordArr.length + 1;
						}
					}	
					outRecordArr[intStlCnt] = JDTORecordFactory.getInstance().create();
					outRecordArr[intStlCnt] = inRecordArr[Loopi];
		            intStlCnt++;
					
					// 작업예약 등록 호출
				}
			}
			
			
			szMsg = "[Jsp Session  -  " + szOperationName +"] 코일야드권상순서 변경.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			return "1";
		
		} catch(Exception e) {
			szMsg = "코일야드권상순서 변경! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" 	, "0");	
			outRecord.setField("RTN_MSG" 	, szMsg);	
			return "0";
		}
		
	} 
	
	/**
	 *      [A] 오퍼레이션명 : 기울기 공식 적용
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilYdGdsCline( String szYdStkColGp,String  szYdStkBedNo,String  szYdStkLyrNo, String sGRP_GP_LOC,
			                            String sCOIL_NO_A,String sCOIL_OUTDIA_A,String sCOIL_NO_B,String sCOIL_OUTDIA_B, String sCOIL_NO_C,String sCOIL_OUTDIA_C,
			                            String sCOIL_NO_D,String sCOIL_OUTDIA_D,String sCOIL_NO_E,String sCOIL_OUTDIA_E) throws JDTOException  {
		
		int intRtnVal         = 0;
		YdStkLyrDao     ydStkLyrDao  = new YdStkLyrDao();
		String szMsg			=	"";
		String szMethodName		=	"searchCoilYdGdsCline";
		String szOperationName	=	"코일제품야드 2단 기울기 편차 Check";
		
		String szRtnVal = YdConstant.RETN_CD_FAILURE;
		JDTORecordSet rsStkLyr1  = null;
		JDTORecord 	recPara    = null;
		JDTORecord 	recStkLyr1    = null;
		
				
		long   lngsCOIL_OUTDIA_A  	= 0;           //코일외경
		long   lngsCOIL_OUTDIA_B   	= 0;           
		long   lngsCOIL_OUTDIA_C   	= 0;           
		long   lngsCOIL_OUTDIA_D   	= 0;           
		long   lngsCOIL_OUTDIA_E   	= 0;           
		int    iY1 = 0; 
		int    iY2 = 0; 
		
		try {
			
			lngsCOIL_OUTDIA_A	= Long.parseLong(sCOIL_OUTDIA_A);
			lngsCOIL_OUTDIA_B	= Long.parseLong(sCOIL_OUTDIA_B);
			lngsCOIL_OUTDIA_C	= Long.parseLong(sCOIL_OUTDIA_C);
			lngsCOIL_OUTDIA_D	= Long.parseLong(sCOIL_OUTDIA_D);
			lngsCOIL_OUTDIA_E	= Long.parseLong(sCOIL_OUTDIA_E);

			
			if(lngsCOIL_OUTDIA_C <= lngsCOIL_OUTDIA_E){

    			rsStkLyr1 = JDTORecordFactory.getInstance().createRecordSet("Temp");

    			recPara = JDTORecordFactory.getInstance().create();
    			recPara.setField("GRP_GP_LOC"	, sGRP_GP_LOC);
    			recPara.setField("COIL_OUTDIA_A", sCOIL_OUTDIA_A);
    			recPara.setField("COIL_OUTDIA_B", sCOIL_OUTDIA_B);
    			recPara.setField("COIL_OUTDIA_C", sCOIL_OUTDIA_C);
    			recPara.setField("COIL_OUTDIA_D", sCOIL_OUTDIA_D);
    			recPara.setField("COIL_OUTDIA_E", sCOIL_OUTDIA_E);
    			
    			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilClineCheck1*/			
            	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr1, 601);
     	    	if (intRtnVal <= 0) {
     	    		return szRtnVal = YdConstant.RETN_CD_FAILURE;
     	    	}
     	    	rsStkLyr1.first();
     	    	recStkLyr1 = JDTORecordFactory.getInstance().create();
     	    	recStkLyr1 = rsStkLyr1.getRecord();
     	    	iY1 = ydDaoUtils.paraRecChkNullInt(recStkLyr1, "Y1");
     			
     	    	szMsg = "대상재료번호(" + sCOIL_NO_D + ")=>>Y1:"+iY1 ;
     	    	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
     	    	
     	    	if(lngsCOIL_OUTDIA_B >= lngsCOIL_OUTDIA_C){

        			rsStkLyr1 = JDTORecordFactory.getInstance().createRecordSet("Temp");

          			recPara = JDTORecordFactory.getInstance().create();
        			recPara.setField("GRP_GP_LOC"	, sGRP_GP_LOC);
        			recPara.setField("COIL_OUTDIA_A", sCOIL_OUTDIA_A);
        			recPara.setField("COIL_OUTDIA_B", sCOIL_OUTDIA_B);
        			recPara.setField("COIL_OUTDIA_C", sCOIL_OUTDIA_C);
        			recPara.setField("COIL_OUTDIA_D", sCOIL_OUTDIA_D);
        			recPara.setField("COIL_OUTDIA_E", sCOIL_OUTDIA_E);
                	    
        			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilClineCheck2*/			
                	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr1, 602);
         	    	if (intRtnVal <= 0) {
         	    		return szRtnVal = YdConstant.RETN_CD_FAILURE;
         	    	}
         	    	rsStkLyr1.first();
         	    	recStkLyr1 = JDTORecordFactory.getInstance().create();
         	    	recStkLyr1 = rsStkLyr1.getRecord();
         	    	iY2 = ydDaoUtils.paraRecChkNullInt(recStkLyr1, "Y2");
         	    	
         	    	szMsg = "대상재료번호(" + sCOIL_NO_D + ")=>>Y2:"+iY2 ;
         	    	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
         	    	

         	    	szMsg = "대상재료번호(" + sCOIL_NO_D + ")=>>Y1+Y2:" + (iY1 + iY2 ) ;
         	    	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
         	    	
         	    	szMsg = "대상재료번호(" + sCOIL_NO_D + ")=>>lngsCOIL_OUTDIA_A+lngsCOIL_OUTDIA_D/2:" + ((lngsCOIL_OUTDIA_A + lngsCOIL_OUTDIA_D)/2) + 50 ;
         	    	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
         	    	
         	    	if((iY1 + iY2) > (((lngsCOIL_OUTDIA_A + lngsCOIL_OUTDIA_D)/2) + 50)){
         	    		return szRtnVal = YdConstant.RETN_CD_SUCCESS;
         	    	} else {
         	    		return szRtnVal = YdConstant.RETN_CD_FAILURE;
        			}

    			} else {
    				return szRtnVal = YdConstant.RETN_CD_FAILURE;
    			}
     
			} else {
				return szRtnVal = YdConstant.RETN_CD_FAILURE;
			}
 			
				
 		} catch(Exception e) {
			szMsg = "기울기 편차 CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnVal = YdConstant.RETN_CD_FAILURE;
		}
		
	} 	

	/**
	 * [A] 오퍼레이션명 : ABC동 제품코일적치가능공통
	 *     : 작업예약에서는 적치단이 중간에
	 *       스케쥴에서는   적치단이 마지막에
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord CoilGdsLyrBaseCheckABC(String szStlNo, JDTORecord inRecord) throws JDTOException  {

		YdStkBedDao     ydStkBedDao  = new YdStkBedDao();
		YdStkLyrDao     ydStkLyrDao  = new YdStkLyrDao();
		YdEqpDao	ydEqpDao 	= new YdEqpDao();
		JDTORecord outRecord    = JDTORecordFactory.getInstance().create();


		
		int intRtnVal         	= 0;
		
		String szMsg			= "";
		String szMethodName		= "CoilGdsLyrBaseCheckABC";
		String szOperationName	= "제품코일적치가능공통(ABC)";
		
		JDTORecord recPara 		= null;

		
		JDTORecordSet rsMaxBed  = null;
		JDTORecordSet rsBed     = null;
		JDTORecord recMaxBed    = null;
		
		JDTORecordSet rsStkLyr  = null;
		JDTORecord recStkLyr    = null;
		JDTORecord recBed       = null;
		
		String szYdStkColGp     = null;  // 적치열번호
		String szYdStkBedNo     = null;  // 적치배드번호
		String szYdStkLyrNo     = null;  // 적치단번호
		
		String szOutDia 		= "0";
		String szWidth 			= "0";
		String szThick 			= "0";
		String szWeigth 		= "0";
		
		String szLeftColGp 		= "";
		String szLeftBedNo 		= "";
		String szLeftLyrNo 		= "";
		String szLeftStlNo 		= "";
		String szLeftMtlStat 	= "";
		String szLeftOutDia 	= "0";
		String szLeftWidth 		= "0";
		String szLeftThick 		= "0";
		String szLeftWeigth 	= "0";
		
		String szRightColGp 	= "";
		String szRightBedNo 	= "";
		String szRightLyrNo 	= "";
		String szRightStlNo 	= "";
		String szRightMtlStat 	= "";
		String szRightOutDia 	= "0";
		String szRightWidth 	= "0";
		String szRightThick 	= "0";
		String szRightWeigth 	= "0";

		String szYD_COIL_OUTDIA_GRP_GP = "";  //
		String sCOIL_OUTDIA_GRP_GP  = "";  //대상재 외경군
		String sYD_STK_COL_W_GP = "";
		
		int   intCompBedNo 		= 0;
		
		String szMaxBedNo 		= null;
		String sCOIL_W_GP 		= "";		
		String szRtnVal 		= "";
		
		String szBRE_YDB701_4_USE_YN = null;
		String szBRE_YDB701_6_USE_YN = null;
		String szISSUE_TG_YN	= null;
		String sGRP_GP_CD       = "";
		String sUSE_YN          = "";
		
		String sBRE_CHK5        = "N";
		String sBRE_CHK1        = "N";
		String sBRE_CHK2        = "N";
		String sBRE_CHK3        = "N";

		try {
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일 제품코일적치가능공통ABC START★★★★★", YdConstant.INFO);

			szYdStkColGp = ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_COL_GP");
			szYdStkBedNo = ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_NO");
			szYdStkLyrNo = ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LYR_NO");
			//szBreSkipYn = ydDaoUtils.paraRecChkNull(inRecord, "BRE_CHK1_SKIP_YN");

			szMsg = "적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") BED로 검색시작   ------>저장위치 수정유무:" ;//+ szBreSkipYn ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
        	if((szYdStkColGp.substring(2, 3).equals("0"))
        			||(szYdStkColGp.substring(2, 3).equals("1")) 
        			||(szYdStkColGp.substring(2, 3).equals("2")) 
        			||(szYdStkColGp.substring(2, 3).equals("3")) 
        			||(szYdStkColGp.substring(2, 3).equals("4")) 
        			||(szYdStkColGp.substring(2, 3).equals("5")) 
        			||(szYdStkColGp.substring(2, 3).equals("6")) 
        			) {
        	} else {	
        		szRtnVal = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
        		ydUtils.putLog(szSessionName, szMethodName, "C열연코일제품야드 설비위치=>>>>"+szRtnVal, YdConstant.INFO);
               	outRecord.setField("RTN_BED" 			, szYdStkColGp + szYdStkBedNo + szYdStkLyrNo);	
               	outRecord.setField("RTN_COLGP" 			, szYdStkColGp);	
            	outRecord.setField("RTN_BEDNO" 			, szYdStkBedNo);	
            	outRecord.setField("RTN_LYRNO" 			, szYdStkLyrNo);	
            	outRecord.setField("RTN_STLNO" 			, szStlNo);	
        		outRecord.setField("RTN_CD" 			, "1");	
	 	       	outRecord.setField("RTN_TO_BEDNO"		, szYdStkColGp.substring(1,6) +szYdStkLyrNo.substring(2,3) + szYdStkBedNo);	
							
				return outRecord;
        	}
			
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("STL_NO"		, szStlNo);
			rsMaxBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    /*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getCoilMaxBedNoByColGp*/
        	intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsMaxBed, 303);
			
        	if (intRtnVal <= 0) {
				szMsg = "적치열(" + szYdStkColGp + ")로 MAX배드 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
        	
        	rsMaxBed.first();
        	recMaxBed = rsMaxBed.getRecord();
        	
        	szMaxBedNo = ydDaoUtils.paraRecChkNull(recMaxBed, "MAX_BED_NO");
        	szYD_COIL_OUTDIA_GRP_GP = ydDaoUtils.paraRecChkNull(recMaxBed, "YD_COIL_OUTDIA_GRP_GP");
        	sYD_STK_COL_W_GP 		= ydDaoUtils.paraRecChkNull(recMaxBed, "YD_STK_COL_W_GP");
        	sCOIL_OUTDIA_GRP_GP		= ydDaoUtils.paraRecChkNull(recMaxBed, "OUTDIA_GRP_GP");
        	sCOIL_W_GP				= ydDaoUtils.paraRecChkNull(recMaxBed, "COIL_W_GP");
        	
 				
        	ydUtils.putLog(szSessionName, szMethodName, "szYD_COIL_OUTDIA_GRP_GP-->" + szYD_COIL_OUTDIA_GRP_GP, YdConstant.INFO);
        	ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_COL_W_GP-->" + sYD_STK_COL_W_GP, YdConstant.INFO);
        	ydUtils.putLog(szSessionName, szMethodName, "sCOIL_OUTDIA_GRP_GP-->" + sCOIL_OUTDIA_GRP_GP, YdConstant.INFO);
        	
 			if ("002".equals(szYdStkLyrNo)) {  // 검색적치단이 2단일 경우
				szLeftColGp = szYdStkColGp;
				szLeftBedNo = szYdStkBedNo;
				szLeftLyrNo = "001";
				
				szRightColGp = szYdStkColGp;
				intCompBedNo = Integer.parseInt(szYdStkBedNo);
				intCompBedNo ++;				
				if (intCompBedNo >= 10) {
					szRightBedNo = "" + intCompBedNo;
				} else {
					szRightBedNo = "0" + intCompBedNo;
				}
				szRightLyrNo = "001";
			}
			
			if ("001".equals(szYdStkLyrNo)) {  // 검색적치단이 1단일 경우
				
				
	        	
	        	if("01".equals(szYdStkBedNo)) { // 검색BED가 1인 경우
	        		szLeftColGp = szYdStkColGp;
					szLeftBedNo = szYdStkBedNo;
					szLeftLyrNo = "001";
					
					szRightColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					
					intCompBedNo++;
					if (intCompBedNo >= 10) {
						szRightBedNo = "" + intCompBedNo;
					} else {
						szRightBedNo = "0" + intCompBedNo;
					}
					szRightLyrNo = "001";
	        	} else if (szMaxBedNo.equals(szYdStkBedNo)) { // 검색BED가 마지막인 경우
	        		
	        		szLeftColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					intCompBedNo--;
					
					if (intCompBedNo >= 10) {
						szLeftBedNo = "" + intCompBedNo;
					} else {
						szLeftBedNo = "0" + intCompBedNo;
					}
					szLeftLyrNo = "001";
	        		
	        		
	        		szRightColGp = szYdStkColGp;
					szRightBedNo = szYdStkBedNo;
					szRightLyrNo = "001";
	        	} else {
	        		
	        		szLeftColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					intCompBedNo--;
					if (intCompBedNo >= 10) {
						szLeftBedNo = "" + intCompBedNo;
					} else {
						szLeftBedNo = "0" + intCompBedNo;
					}
					if (intCompBedNo >= 10) {
						szLeftBedNo = "" + intCompBedNo;
					} else {
						szLeftBedNo = "0" + intCompBedNo;
					}
					szLeftLyrNo = "001";
					
					szRightColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					
					intCompBedNo++;	
					if (intCompBedNo >= 10) {
						szRightBedNo = "" + intCompBedNo;
					} else {
						szRightBedNo = "0" + intCompBedNo;
					}
					szRightLyrNo = "001";
	        	}
				
	        	
			}
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 왼쪽,오른쪽  적치단 조회ABC START★★★★★", YdConstant.INFO);

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("COIL_NO", szStlNo);
			recPara.setField("YD_STK_COL_GPL", szLeftColGp+szLeftBedNo+szLeftLyrNo);
			recPara.setField("YD_STK_COL_GPR", szRightColGp+szRightBedNo+szRightLyrNo);
			recPara.setField("YD_STK_COL_GP1", szLeftColGp);
			recPara.setField("YD_STK_BED_NO1", szLeftBedNo);
			recPara.setField("YD_STK_LYR_NO1", szLeftLyrNo);
			recPara.setField("YD_STK_COL_GP2", szRightColGp);
			recPara.setField("YD_STK_BED_NO2", szRightBedNo);
			recPara.setField("YD_STK_LYR_NO2", szRightLyrNo);

			rsStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilCOMM*/			
        	//intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr, 402);
			
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilCOMM_AUTO*/			
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr, 404);
			
 	    	if (intRtnVal <= 0) {
				szMsg = "확인:"+szStlNo+"적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;

			}
        	if (intRtnVal > 3) {
				szMsg = "확인:"+szStlNo+"적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")로 조회중 error 발생! 적치건수:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
 	    	
			for(int Loop_i = 1; Loop_i <= rsStkLyr.size(); Loop_i++) {
				rsStkLyr.absolute(Loop_i);
				recStkLyr = rsStkLyr.getRecord();
				
				String sTARGET_GP = ydDaoUtils.paraRecChkNull(recStkLyr, "TARGET_GP");
//대상
				if (sTARGET_GP.equals("T")) {
					if (Loop_i == 1) {
						szStlNo 			= ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO"); 
						szOutDia 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_OUTDIA"),"0");  
						szThick 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_T"),"0");  
						szWidth 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_W"),"0");  
						szWeigth 			= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_WT"),"0"); 
					} else {
						szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD" 	, "-1");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
					}
				}
				if (sTARGET_GP.equals("L")) {	
					if (Loop_i == 2) {
			        	szLeftStlNo 		= ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO"); 
			        	szLeftMtlStat 		= ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_MTL_STAT");
						szLeftOutDia 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_OUTDIA"),"0");  
						szLeftThick 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_T"),"0");  
						szLeftWidth 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_W"),"0");  
						szLeftWeigth 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_WT"),"0"); 
						
						szBRE_YDB701_4_USE_YN = StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "BRE_YDB701_4_USE_YN"),"N");  
						szBRE_YDB701_6_USE_YN = StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "BRE_YDB701_6_USE_YN"),"N");
						szISSUE_TG_YN		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "ISSUE_TG_YN"),"N");
			
			        	if ("002".equals(szYdStkLyrNo)) {
//			        		hun 150826 1단코일이 예약(D)상태에서 2단 적치 불가 
//			        		if (!("C".equals(szLeftMtlStat) || "D".equals(szLeftMtlStat))) {
			        		if ( !"C".equals(szLeftMtlStat) || ("D".equals(szLeftMtlStat) && "Y".equals(szBRE_YDB701_6_USE_YN)) ) {
			        			szMsg = "확인:"+szStlNo+"좌측1적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 상태가 적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}
			        		
			        		if("Y".equals(szBRE_YDB701_4_USE_YN) && "Y".equals(szISSUE_TG_YN)) {
			        			//hun 출고대상코일위적치금지 이면서 출고대상이면 2단적치 불가
			        			szMsg = "확인:"+szStlNo+"좌측1적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 코일 : "+ szLeftStlNo +"가 출고대상으로  적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}			        		
			        	}
					} else {
						szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD" 	, "-1");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
				
					}

				}
				
				if (sTARGET_GP.equals("R")) {	
					if (Loop_i == 3) {
			        	szRightStlNo 		= ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO");
			        	szRightMtlStat 		= ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_MTL_STAT");
						szRightOutDia 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_OUTDIA"),"0");  
						szRightThick 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_T"),"0");  
						szRightWidth 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_W"),"0");  
						szRightWeigth 		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "COIL_WT"),"0");
						
						szBRE_YDB701_4_USE_YN = StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "BRE_YDB701_4_USE_YN"),"N");  
						szBRE_YDB701_6_USE_YN = StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "BRE_YDB701_6_USE_YN"),"N");
						szISSUE_TG_YN		= StringHelper.evl(ydDaoUtils.paraRecChkNull(recStkLyr, "ISSUE_TG_YN"),"N");
						
			        	if ("002".equals(szYdStkLyrNo)) {
//			        		hun 150826 1단코일이 예약(D)상태에서 2단 적치 불가 
//			        		if (!("C".equals(szRightMtlStat) || "D".equals(szRightMtlStat))) {
			        		if ( !"C".equals(szRightMtlStat) || ("D".equals(szRightMtlStat) && "Y".equals(szBRE_YDB701_6_USE_YN)) ) {
			        			szMsg = "확인:"+szStlNo+"우측1적치단(" + szRightColGp + szRightBedNo + szRightLyrNo + ")의 상태가 적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}
			        		
                            if("Y".equals(szBRE_YDB701_4_USE_YN) && "Y".equals(szISSUE_TG_YN)) {
			        			//출고대상코일위적치금지 이면서 출고대상이면 2단적치 불가
			        			szMsg = "확인:"+szStlNo+"우측1적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 코일 : "+ szRightStlNo +"가 출고대상으로  적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}
		        		
			        	}
					} else {
						szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD" 	, "-1");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
				
					}
				}
			}

			// 왼쪽,오른쪽  적치단 로그 Start ----------------------------------------------------------------------------------------------------------
			szMsg = "확인:"+szStlNo+"★★★ 좌측1적치단(" + szLeftColGp + "-" + szLeftBedNo + "-" +szLeftLyrNo + ") 코일: "+ szLeftStlNo + " 상태: " +  szLeftMtlStat + " 외경: " + szLeftOutDia + " 중량: " + szLeftWeigth;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			szMsg = "확인:"+szStlNo+"★★★ 우측1적치단(" + szRightColGp + "-" + szRightBedNo + "-" +szRightLyrNo + ") 코일: "+ szRightStlNo + " 상태: " +  szRightMtlStat + " 외경: " + szRightOutDia + " 중량: " + szRightWeigth;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			// 왼쪽,오른쪽  적치단 로그 End ------------------------------------------------------------------------------------------------------------
			
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 왼쪽,오른쪽  적치단 조회ABC END★★★★★", YdConstant.INFO);
        	
			
			//BRE 사용 조건 READ	
			JDTORecordSet rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
        	recInTemp.setField("TEMP" 	, "0");	
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB701*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 701);
			if(intRtnVal <= 0) {
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
    		}
			
			for(int Loop1_i = 1; Loop1_i <= rsResult.size(); Loop1_i++) {
				JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
				rsResult.absolute(Loop1_i);
				recOutTemp.setRecord(rsResult.getRecord());

				sGRP_GP_CD	= ydDaoUtils.paraRecChkNull(recOutTemp, "GRP_GP_CD");	//CHECK_GPT		
				sUSE_YN		= ydDaoUtils.paraRecChkNull(recOutTemp, "USE_YN");	//사용가능 Y,N	
				
				if((sGRP_GP_CD.equals("1")) && (sUSE_YN.equals("Y"))) {
					sBRE_CHK1 = "Y";
				} else if((sGRP_GP_CD.equals("2")) && (sUSE_YN.equals("Y"))) {
					sBRE_CHK2 = "Y";
				} else if((sGRP_GP_CD.equals("5")) && (sUSE_YN.equals("Y"))) {
					sBRE_CHK5 = "Y";
				}
			}		

    		ydUtils.putLog(szSessionName, szMethodName, "sBRE_CHK1:" + sBRE_CHK1, YdConstant.INFO);
    		ydUtils.putLog(szSessionName, szMethodName, "sBRE_CHK2:" + sBRE_CHK2, YdConstant.INFO);
    		ydUtils.putLog(szSessionName, szMethodName, "sBRE_CHK5:" + sBRE_CHK5, YdConstant.INFO);
			
			
			
        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 중량편차,외경간격,외경편차ABC   START★★★★★", YdConstant.INFO);
    
			szMsg = "대상(" + sCOIL_OUTDIA_GRP_GP +")"+ intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	       	sYD_STK_COL_W_GP 		= ydDaoUtils.paraRecChkNull(recMaxBed, "YD_STK_COL_W_GP");
        	sCOIL_OUTDIA_GRP_GP		= ydDaoUtils.paraRecChkNull(recMaxBed, "OUTDIA_GRP_GP");
        	sCOIL_W_GP				= ydDaoUtils.paraRecChkNull(recMaxBed, "COIL_W_GP");
 			
			
        	ydUtils.putLog(szSessionName, szMethodName, "==================== 폭구분 비교한다.====================", YdConstant.INFO);
        	if(!sYD_STK_COL_W_GP.equals(sCOIL_W_GP)){
    			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 코일"+ sCOIL_W_GP+ "과 적치위치"+ sYD_STK_COL_W_GP+ "구분이 틀립니다..";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
        	}
        	
        	ydUtils.putLog(szSessionName, szMethodName, "==================== 외경군 계산한다.====================", YdConstant.INFO);
        	if(!sCOIL_OUTDIA_GRP_GP.equals(szYD_COIL_OUTDIA_GRP_GP)){
    			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 코일"+ sCOIL_OUTDIA_GRP_GP+ "과 적치위치"+ szYD_COIL_OUTDIA_GRP_GP+ "군이 틀립니다..";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
        	}
        	
        	
        	// 1단일 경우 외경편차를 계산한다.
        	ydUtils.putLog(szSessionName, szMethodName, "====================1단일 경우 외경편차를 계산한다.====================", YdConstant.INFO);
        	if ("001".equals(szYdStkLyrNo)) {
        		szRtnVal = searchCoilGdsYdOutDiaDiffCheck( szStlNo, szYdStkLyrNo, szOutDia, szLeftStlNo, szLeftOutDia,  szRightStlNo, szRightOutDia);
				
        		ydUtils.putLog(szSessionName, szMethodName, "외경CHECK :" + szRtnVal, YdConstant.DEBUG);
				    		
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 코일과 외경차이(180mm)로 적치가 불가능한 1 단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
         	// 1단일 경우 폭기준를 계산한다. 
        	if ("001".equals(szYdStkLyrNo)) {
        		ydUtils.putLog(szSessionName, szMethodName, "====================1단일 경우 폭기준를 계산한다.====================", YdConstant.INFO);
            	szRtnVal = searchCoilYdGdsWidthDiffCheck( szStlNo, szYdStkLyrNo, szWidth, szLeftStlNo, szLeftWidth,  szRightStlNo, szRightWidth);
        		
            	ydUtils.putLog(szSessionName, szMethodName, "폭CHECK :" + szRtnVal, YdConstant.DEBUG);
        		        		
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 코일과 폭기준(200mm)로 적치가 불가능한 1단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
        	
        //  hun 150922 BRE 적용 1단일 경우 폭간섭를 계산한다. (저장위치수정인 경우 삭제 한다.)
        	if (sBRE_CHK1.equals("Y") && ("001".equals(szYdStkLyrNo)) //&& szBreSkipYn.equals("Y")
        		) {
        		ydUtils.putLog(szSessionName, szMethodName, "====================1단일 경우 고정skid 폭간섭를 계산한다.====================", YdConstant.INFO);
            	szRtnVal = searchCoilYdGdsWidthDiffCheck3ABC( szStlNo, szYdStkColGp, szYdStkBedNo, szYdStkLyrNo ,  sYD_STK_COL_W_GP, szWidth ,szOutDia,szMaxBedNo);
        		
            	ydUtils.putLog(szSessionName, szMethodName, "폭간섭CHECK :" + szRtnVal, YdConstant.DEBUG);
        		        		
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일 폭 간섭으로 적치가 불가능한 위치입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
        	
        	// 2단일 경우 중량/폭편차를 계산한다.
        	ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 중량/폭편차를 계산한다.====================", YdConstant.INFO);
        	if ("002".equals(szYdStkLyrNo) && !"".equals(szLeftStlNo) && !"".equals(szRightStlNo)) {
        		szRtnVal = searchCoilGdsYdWtCheckABC(szStlNo, szThick,szWidth, szWeigth, szLeftStlNo, szLeftThick,szLeftWidth, szLeftWeigth,szRightStlNo,szRightThick,szRightWidth, szRightWeigth);
 
               	ydUtils.putLog(szSessionName, szMethodName, "중량CHECK :" + szRtnVal, YdConstant.DEBUG);
                
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과 중량/폭편차로 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
           		} else if (szRtnVal.equals("WID_FAILURE")) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과폭편차로 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
           		} else if (szRtnVal.equals("WGT_FAILURE")) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과중량편차로 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
        	
        	// 150904 hun 횡행좌표 거리 계산 값에 의한 외경 CHECK
        	ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 횡행좌표 거리 계산 값에 의한 외경체크====================", YdConstant.INFO);
        	if (sBRE_CHK5.equals("Y") && "002".equals(szYdStkLyrNo)) {
        		szRtnVal = searchCoilGdsYdOutDiaOverRunCheck( szYdStkColGp, szYdStkBedNo, szStlNo, szYdStkLyrNo, szOutDia, szLeftStlNo, szLeftOutDia,  szRightStlNo, szRightOutDia);
              	ydUtils.putLog(szSessionName, szMethodName, "횡행좌표 거리 계산 값에 의한 외경 CHECK :" + szRtnVal, YdConstant.DEBUG);
        		
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일이 횡행좌표 거리 계산 값에 의한 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
        	
        	// 2단일 경우 외경간격을 계산한다.(OK)
        	ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 외경간격을 계산한다.====================", YdConstant.INFO);
        	if ("002".equals(szYdStkLyrNo) && !"".equals(szLeftStlNo) && !"".equals(szRightStlNo)) {
        		szRtnVal = searchCoilGdsYdOutDiaIntervalCheckABC( szYdStkColGp, szYdStkBedNo, szStlNo, szYdStkLyrNo, szOutDia, szLeftStlNo, szLeftOutDia,  szRightStlNo, szRightOutDia);
              	ydUtils.putLog(szSessionName, szMethodName, "외경간격CHECK :" + szRtnVal, YdConstant.DEBUG);
        		
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일이 외경간격편차로 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}
           	
        	// 2단일 경우 외경편차1를 계산한다.
        	ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 외경편차1를 계산한다.====================", YdConstant.INFO);
        	if ("002".equals(szYdStkLyrNo) && !"".equals(szLeftStlNo) && !"".equals(szRightStlNo)) {
        		szRtnVal = searchCoilYdGdsWidthDiffCheck2( szStlNo, szYdStkLyrNo, szOutDia, szLeftStlNo, szLeftOutDia,  szRightStlNo, szRightOutDia);
 
               	ydUtils.putLog(szSessionName, szMethodName, "외경 :" + szRtnVal, YdConstant.DEBUG);
                
        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과 외경편차1로 적치가 불가능한 2단 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
        		}
        	}        	
           	
        	
        	//ABC동 이면서 B군 인경우 			
			if( szYD_COIL_OUTDIA_GRP_GP.equals("B")){
	        	// 2단일 경우 외경편차2를 계산한다.
	        	ydUtils.putLog(szSessionName, szMethodName, "====================2단일 경우 외경2(1500미만)를 계산한다.====================", YdConstant.INFO);
		       	if ("002".equals(szYdStkLyrNo) && !"".equals(szLeftStlNo) && !"".equals(szRightStlNo)) {
		        		szRtnVal = searchCoilYdGdsWidthDiffCheck4( szStlNo, szYdStkLyrNo, szOutDia, szLeftStlNo, szLeftOutDia,  szRightStlNo, szRightOutDia);
		 
		              	ydUtils.putLog(szSessionName, szMethodName, "외경 :" + szRtnVal, YdConstant.DEBUG);
		                
		        		if (szRtnVal.equals(YdConstant.RETN_CD_FAILURE)) {
		        			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 1단 코일과 외경(1500 미만)로 적치가 불가능한 2단 입니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							outRecord.setField("RTN_CD" 	, "-1");	
							outRecord.setField("RTN_MSG" 	, szMsg);	
							return outRecord;
		        		}
		        	}      
			}

           	// 마지막 조건 check
        	ydUtils.putLog(szSessionName, szMethodName, "===================선택된 저장위치가 공위치인지ABC CHECK====================", YdConstant.INFO);
			
        	recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
			recPara.setField("YD_STK_LYR_NO", szYdStkLyrNo);
			
        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
        	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyr*/
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBed, 0);
        			
        	if (intRtnVal <= 0) {
    			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 마지막으로 적치단 CHECK ERROR.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
        	}
        	
        	rsBed.first();
			recBed =JDTORecordFactory.getInstance().create();
			recBed = rsBed.getRecord();
			String sYD_STK_LYR_MTL_STAT = recBed.getFieldString("YD_STK_LYR_MTL_STAT");
			
			if(!sYD_STK_LYR_MTL_STAT.equals("E")){
	   			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 비워있는 위치가 없습니다. CHECK ERROR.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
 				
			}
        	
        	ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 2단 중량편차,외경간격,외경편차ABC   END★★★★★", YdConstant.INFO);
        	
        	outRecord.setField("RTN_BED" 			, szYdStkColGp + szYdStkBedNo + szYdStkLyrNo);	
           	outRecord.setField("RTN_COLGP" 			, szYdStkColGp);	
        	outRecord.setField("RTN_BEDNO" 			, szYdStkBedNo);	
        	outRecord.setField("RTN_LYRNO" 			, szYdStkLyrNo);	
        	outRecord.setField("RTN_STLNO" 			, szStlNo);	
           	outRecord.setField("RTN_LEFT_COLGP" 	, szLeftColGp);	
        	outRecord.setField("RTN_LEFT_BEDNO" 	, szLeftBedNo);	
        	outRecord.setField("RTN_LEFT_LYRNO" 	, szLeftLyrNo);	
        	outRecord.setField("RTN_LEFT_STLNO" 	, szLeftStlNo);	
           	outRecord.setField("RTN_RIGHT_COLGP" 	, szRightColGp);	
        	outRecord.setField("RTN_RIGHT_BEDNO" 	, szRightBedNo);	
        	outRecord.setField("RTN_RIGHT_LYRNO" 	, szRightLyrNo);	
        	outRecord.setField("RTN_RIGHT_STLNO" 	, szRightStlNo);	
 			outRecord.setField("RTN_CD" 			, "1");	
 	       	outRecord.setField("RTN_TO_BEDNO"		, szYdStkColGp.substring(1,6) +szYdStkLyrNo.substring(2,3) + szYdStkBedNo);	
 	        ydUtils.putLog(szSessionName, szMethodName, szYdStkColGp.substring(1,6) +szYdStkLyrNo.substring(2,3) + szYdStkBedNo, YdConstant.INFO);

			return outRecord;
			
		} catch(Exception e) {
			szMsg = "코일제품야드ABC To위치검색 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" 	, "0");	
			outRecord.setField("RTN_MSG" 	, szMsg);	
			return outRecord;
		}
		
	} 
	/**
	 * [A] 오퍼레이션명 : 야드포장장 제품코일적치가능공통
	 *     : 작업예약에서는 적치단이 중간에
	 *       스케쥴에서는   적치단이 마지막에
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord CoilGdsLyrBaseCheckYardWrap(String szStlNo, JDTORecord inRecord) throws JDTOException  {

		YdStkBedDao     ydStkBedDao  = new YdStkBedDao();
		YdStkLyrDao     ydStkLyrDao  = new YdStkLyrDao();
		JDTORecord outRecord    = JDTORecordFactory.getInstance().create();
		YdEqpDao	ydEqpDao 	= new YdEqpDao();

		
		int intRtnVal         	= 0;
		
		String szMsg			= "";
		String szMethodName		= "CoilGdsLyrBaseCheckYardWrap";
		String szOperationName	= "제품코일적치가능공통(야드포장장)";
		
		JDTORecord recPara 		= null;

		
		JDTORecordSet rsMaxBed  = null;
		JDTORecordSet rsBed     = null;
		JDTORecord recMaxBed    = null;
		
		JDTORecordSet rsStkLyr  = null;
		JDTORecord recStkLyr    = null;
		JDTORecord recBed       = null;
		
		String szYdStkColGp     = null;  // 적치열번호
		String szYdStkBedNo     = null;  // 적치배드번호
		String szYdStkLyrNo     = null;  // 적치단번호
		
		
		String szLeftColGp 		= "";
		String szLeftBedNo 		= "";
		String szLeftLyrNo 		= "";
		String szLeftStlNo 		= "";
		String szLeftMtlStat 	= "";
		
		String szRightColGp 	= "";
		String szRightBedNo 	= "";
		String szRightLyrNo 	= "";
		String szRightStlNo 	= "";
		String szRightMtlStat 	= "";

		String szYD_COIL_OUTDIA_GRP_GP = "";  //
		String sCOIL_OUTDIA_GRP_GP  = "";  //대상재 외경군
		String sYD_STK_COL_W_GP = "";
		
		int   intCompBedNo 		= 0;
		int   num 		= 0;
		
		String szMaxBedNo 		= null;
		String szRtnVal 		= "";

		try {
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일 제품코일적치가능공통(야드포장장) START★★★★★", YdConstant.INFO);

			szYdStkColGp = ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_COL_GP");
			szYdStkBedNo = ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_NO");
			szYdStkLyrNo = ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LYR_NO");
			//szBreSkipYn = ydDaoUtils.paraRecChkNull(inRecord, "BRE_CHK1_SKIP_YN");

			szMsg = "적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") BED로 검색시작   ------>저장위치 수정유무:" ;//+ szBreSkipYn ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			if((szYdStkColGp.substring(2, 3).equals("0"))||
					(szYdStkColGp.substring(2, 3).equals("1"))||
					(szYdStkColGp.substring(2, 3).equals("2"))||
					(szYdStkColGp.substring(2, 3).equals("3"))||
					(szYdStkColGp.substring(2, 3).equals("4"))||
					(szYdStkColGp.substring(2, 3).equals("5"))||
					(szYdStkColGp.substring(2, 3).equals("6"))) {
        	} else {	
        		szRtnVal = szYdStkColGp + szYdStkBedNo + szYdStkLyrNo;
        		ydUtils.putLog(szSessionName, szMethodName, "C열연코일제품야드 설비위치=>>>>"+szRtnVal, YdConstant.INFO);
               	outRecord.setField("RTN_BED" 			, szYdStkColGp + szYdStkBedNo + szYdStkLyrNo);	
               	outRecord.setField("RTN_COLGP" 			, szYdStkColGp);	
            	outRecord.setField("RTN_BEDNO" 			, szYdStkBedNo);	
            	outRecord.setField("RTN_LYRNO" 			, szYdStkLyrNo);	
            	outRecord.setField("RTN_STLNO" 			, szStlNo);	
        		outRecord.setField("RTN_CD" 			, "1");	
	 	       	outRecord.setField("RTN_TO_BEDNO"		, szYdStkColGp.substring(1,6) +szYdStkLyrNo.substring(2,3) + szYdStkBedNo);	
							
				return outRecord;
        	}
			
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("STL_NO"		, szStlNo);
			rsMaxBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    /*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getCoilMaxBedNoByColGp*/
        	intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsMaxBed, 303);
			
        	if (intRtnVal <= 0) {
				szMsg = "적치열(" + szYdStkColGp + ")로 MAX배드 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
        	
        	rsMaxBed.first();
        	recMaxBed = rsMaxBed.getRecord();
        	
        	szMaxBedNo = ydDaoUtils.paraRecChkNull(recMaxBed, "MAX_BED_NO");
        	szYD_COIL_OUTDIA_GRP_GP = ydDaoUtils.paraRecChkNull(recMaxBed, "YD_COIL_OUTDIA_GRP_GP");
        	sYD_STK_COL_W_GP 		= ydDaoUtils.paraRecChkNull(recMaxBed, "YD_STK_COL_W_GP");
        	sCOIL_OUTDIA_GRP_GP		= ydDaoUtils.paraRecChkNull(recMaxBed, "OUTDIA_GRP_GP");
        	
 				
        	ydUtils.putLog(szSessionName, szMethodName, "szYD_COIL_OUTDIA_GRP_GP-->" + szYD_COIL_OUTDIA_GRP_GP, YdConstant.INFO);
        	ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_COL_W_GP-->" + sYD_STK_COL_W_GP, YdConstant.INFO);
        	ydUtils.putLog(szSessionName, szMethodName, "sCOIL_OUTDIA_GRP_GP-->" + sCOIL_OUTDIA_GRP_GP, YdConstant.INFO);
        	
        	//고정스키드 A,B,C동 인 경우 
//        	if(szYdStkColGp.substring(1, 2).equals("A")||
//        			szYdStkColGp.substring(1, 2).equals("B")||	
//        			szYdStkColGp.substring(1, 2).equals("C")
//        	  ){
        	if(ydEqpDao.chkFixedSkid(szYdStkColGp)){
        		num =1 ;
        	}else{
        		num =4 ;
        	}
        	
 			if ("002".equals(szYdStkLyrNo)) {  // 검색적치단이 2단일 경우
				szLeftColGp = szYdStkColGp;
				szLeftBedNo = szYdStkBedNo;
				szLeftLyrNo = "001";
				
				szRightColGp = szYdStkColGp;
				intCompBedNo = Integer.parseInt(szYdStkBedNo);
				intCompBedNo = intCompBedNo + num;				
				if (intCompBedNo >= 10) {
					szRightBedNo = "" + intCompBedNo;
				} else {
					szRightBedNo = "0" + intCompBedNo;
				}
				szRightLyrNo = "001";
			}
			
			if ("001".equals(szYdStkLyrNo)) {  // 검색적치단이 1단일 경우
				
				
	        	
	        	if("01".equals(szYdStkBedNo)) { // 검색BED가 1인 경우
	        		szLeftColGp = szYdStkColGp;
					szLeftBedNo = szYdStkBedNo;
					szLeftLyrNo = "001";
					
					szRightColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					
//					intCompBedNo++;
					intCompBedNo = intCompBedNo + num;	
					if (intCompBedNo >= 10) {
						szRightBedNo = "" + intCompBedNo;
					} else {
						szRightBedNo = "0" + intCompBedNo;
					}
					szRightLyrNo = "001";
	        	} else if (szMaxBedNo.equals(szYdStkBedNo)) { // 검색BED가 마지막인 경우
	        		
	        		szLeftColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
//					intCompBedNo--;
					intCompBedNo = intCompBedNo - num;	
					if (intCompBedNo >= 10) {
						szLeftBedNo = "" + intCompBedNo;
					} else {
						szLeftBedNo = "0" + intCompBedNo;
					}
					szLeftLyrNo = "001";
	        		
	        		
	        		szRightColGp = szYdStkColGp;
					szRightBedNo = szYdStkBedNo;
					szRightLyrNo = "001";
	        	} else {
	        		
	        		szLeftColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
//					intCompBedNo--;
					intCompBedNo = intCompBedNo - num;	
					if (intCompBedNo >= 10) {
						szLeftBedNo = "" + intCompBedNo;
					} else {
						szLeftBedNo = "0" + intCompBedNo;
					}
					if (intCompBedNo >= 10) {
						szLeftBedNo = "" + intCompBedNo;
					} else {
						szLeftBedNo = "0" + intCompBedNo;
					}
					szLeftLyrNo = "001";
					
					szRightColGp = szYdStkColGp;
					intCompBedNo = Integer.parseInt(szYdStkBedNo);
					
//					intCompBedNo++;
					intCompBedNo = intCompBedNo + num;	
					if (intCompBedNo >= 10) {
						szRightBedNo = "" + intCompBedNo;
					} else {
						szRightBedNo = "0" + intCompBedNo;
					}
					szRightLyrNo = "001";
	        	}
				
	        	
			}
			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 왼쪽,오른쪽  적치단 조회(야드포장장) START★★★★★", YdConstant.INFO);

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("COIL_NO", szStlNo);
			recPara.setField("YD_STK_COL_GPL", szLeftColGp+szLeftBedNo+szLeftLyrNo);
			recPara.setField("YD_STK_COL_GPR", szRightColGp+szRightBedNo+szRightLyrNo);
			recPara.setField("YD_STK_COL_GP1", szLeftColGp);
			recPara.setField("YD_STK_BED_NO1", szLeftBedNo);
			recPara.setField("YD_STK_LYR_NO1", szLeftLyrNo);
			recPara.setField("YD_STK_COL_GP2", szRightColGp);
			recPara.setField("YD_STK_BED_NO2", szRightBedNo);
			recPara.setField("YD_STK_LYR_NO2", szRightLyrNo);

			rsStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilCOMM*/			
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr, 402);
 	    	if (intRtnVal <= 0) {
				szMsg = "확인:"+szStlNo+"적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;

			}
        	if (intRtnVal > 3) {
				szMsg = "확인:"+szStlNo+"적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")로 조회중 error 발생! 적치건수:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
 	    	
			for(int Loop_i = 1; Loop_i <= rsStkLyr.size(); Loop_i++) {
				rsStkLyr.absolute(Loop_i);
				recStkLyr = rsStkLyr.getRecord();
				
				String sTARGET_GP = ydDaoUtils.paraRecChkNull(recStkLyr, "TARGET_GP");
//대상
				if (sTARGET_GP.equals("T")) {
					if (Loop_i == 1) {
						szStlNo 			= ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO"); 
					} else {
						szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD" 	, "-1");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
					}
				}
				if (sTARGET_GP.equals("L")) {	
					if (Loop_i == 2) {
			        	szLeftStlNo 		= ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO"); 
			        	szLeftMtlStat 		= ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_MTL_STAT");
			
			        	if ("002".equals(szYdStkLyrNo)) {
			        		if (!("C".equals(szLeftMtlStat) || "D".equals(szLeftMtlStat))) {
			        			szMsg = "확인:"+szStlNo+"좌측1적치단(" + szLeftColGp + szLeftBedNo + szLeftLyrNo + ")의 상태가 적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}
			        	}
					} else {
						szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD" 	, "-1");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
				
					}

				}
				
				if (sTARGET_GP.equals("R")) {	
					if (Loop_i == 3) {
			        	szRightStlNo 		= ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO");
			        	szRightMtlStat 		= ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_MTL_STAT");
						
			        	if ("002".equals(szYdStkLyrNo)) {
			        		if (!("C".equals(szRightMtlStat) || "D".equals(szRightMtlStat))) {
			        			szMsg = "확인:"+szStlNo+"우측1적치단(" + szRightColGp + szRightBedNo + szRightLyrNo + ")의 상태가 적치가 불가능한 2단입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD" 	, "-1");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
			        		}
			        	}
					} else {
						szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ")로 조회중 error 발생! 에러코드:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD" 	, "-1");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
				
					}
				}
			}

			ydUtils.putLog(szSessionName, szMethodName, "★★★★★C열연코일제품야드 왼쪽,오른쪽  적치단 조회(야드포장장) END★★★★★", YdConstant.INFO);
        	
			szMsg = "대상(" + sCOIL_OUTDIA_GRP_GP +")"+ intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	       	sYD_STK_COL_W_GP 		= ydDaoUtils.paraRecChkNull(recMaxBed, "YD_STK_COL_W_GP");
        	sCOIL_OUTDIA_GRP_GP		= ydDaoUtils.paraRecChkNull(recMaxBed, "OUTDIA_GRP_GP");
 			
           	// 마지막 조건 check
        	ydUtils.putLog(szSessionName, szMethodName, "===================선택된 저장위치가 공위치인지(야드포장장) CHECK====================", YdConstant.INFO);
			
        	recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
			recPara.setField("YD_STK_LYR_NO", szYdStkLyrNo);
			
        	rsBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
        	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyr*/
        	intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBed, 0);
        			
        	if (intRtnVal <= 0) {
    			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 마지막으로 적치단 CHECK ERROR.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
        	}
        	
        	rsBed.first();
			recBed =JDTORecordFactory.getInstance().create();
			recBed = rsBed.getRecord();
			String sYD_STK_LYR_MTL_STAT = recBed.getFieldString("YD_STK_LYR_MTL_STAT");
			
			if(!sYD_STK_LYR_MTL_STAT.equals("E")){
	   			szMsg = "확인:"+szStlNo+"적치단(" + szYdStkColGp + szYdStkBedNo + szYdStkLyrNo + ") 비워있는 위치가 없습니다. CHECK ERROR.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
 				
			}
        	
        	outRecord.setField("RTN_BED" 			, szYdStkColGp + szYdStkBedNo + szYdStkLyrNo);	
           	outRecord.setField("RTN_COLGP" 			, szYdStkColGp);	
        	outRecord.setField("RTN_BEDNO" 			, szYdStkBedNo);	
        	outRecord.setField("RTN_LYRNO" 			, szYdStkLyrNo);	
        	outRecord.setField("RTN_STLNO" 			, szStlNo);	
           	outRecord.setField("RTN_LEFT_COLGP" 	, szLeftColGp);	
        	outRecord.setField("RTN_LEFT_BEDNO" 	, szLeftBedNo);	
        	outRecord.setField("RTN_LEFT_LYRNO" 	, szLeftLyrNo);	
        	outRecord.setField("RTN_LEFT_STLNO" 	, szLeftStlNo);	
           	outRecord.setField("RTN_RIGHT_COLGP" 	, szRightColGp);	
        	outRecord.setField("RTN_RIGHT_BEDNO" 	, szRightBedNo);	
        	outRecord.setField("RTN_RIGHT_LYRNO" 	, szRightLyrNo);	
        	outRecord.setField("RTN_RIGHT_STLNO" 	, szRightStlNo);	
 			outRecord.setField("RTN_CD" 			, "1");	
 	       	outRecord.setField("RTN_TO_BEDNO"		, szYdStkColGp.substring(1,6) +szYdStkLyrNo.substring(2,3) + szYdStkBedNo);	
 	        ydUtils.putLog(szSessionName, szMethodName, szYdStkColGp.substring(1,6) +szYdStkLyrNo.substring(2,3) + szYdStkBedNo, YdConstant.INFO);

			return outRecord;
			
		} catch(Exception e) {
			szMsg = "코일제품야드(야드포장장) To위치검색 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" 	, "0");	
			outRecord.setField("RTN_MSG" 	, szMsg);	
			return outRecord;
		}
		
	} 
	
}
