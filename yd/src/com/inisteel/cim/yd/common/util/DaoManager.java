/**
 * DAO를 관리하는 클래스
 */
package com.inisteel.cim.yd.common.util;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.common.dao.ptPlateCommDao.PtPlateCommDao;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSpecDao.YdCrnSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydLocSrchRngDao.YdLocSrchRngDao;
import com.inisteel.cim.yd.common.dao.ydPilingGrpDao.YdPilingGrpDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydTcarFtmvMtlDao.YdTcarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;

/**
 * @author 임춘수
 *
 */
public class DaoManager {
	//클래스명
	private static String szClassName = YdCommonUtils.class.getName();
	//DAO 유틸리티 클래스
	private static YdDaoUtils ydDaoUtils = new YdDaoUtils();
	//유틸리티클래스
	private static YdUtils ydUtils = new YdUtils();
	
	/**
	 * 생성자 : 외부에서 직접 호출 불가능
	 */
	private DaoManager() {}
	
	/*------------------------------------------------------------------------------------------------
	 * 작업예약 DAO
	 ------------------------------------------------------------------------------------------------*/
	
	/**
	 * 작업예약조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdWrkbook(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdWrkbook";
		String szOperationName			= "작업예약조회";
		int intRtnVal						= -100;
		
		
		YdWrkbookDao		ydWrkbookDao		= new YdWrkbookDao();
		
		intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	
	/**
	 * 작업예약수정
	 * @param recPara
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String updYdWrkbook(JDTORecord recPara, int intGp) throws JDTOException {
		String szMsg					= "";
		String szMethodName				= "updYdWrkbook";
		String szOperationName			= "작업예약수정";
		int intRtnVal					= -100;
		
		YdWrkbookDao		ydWrkbookDao		= new YdWrkbookDao();
		
		intRtnVal			= ydWrkbookDao.updYdWrkbook(recPara, intGp);
		
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 작업예약이 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -1) {
				szMsg="["+szOperationName+"] 작업예약이 중복됩니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_DUPLICATE;
			}else if(intRtnVal == -2) {
				szMsg="["+szOperationName+"] 작업예약 수정 시 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}else if(intRtnVal == -3){
				szMsg="["+szOperationName+"] 작업예약 수정 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	//------------------------------------------------------------------------------------------------
	
	
	/*------------------------------------------------------------------------------------------------
	 * 작업예약재료 DAO
	 ------------------------------------------------------------------------------------------------*/
	
	/**
	 * 작업예약재료조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdWrkbookmtl(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdWrkbookmtl";
		String szOperationName			= "작업예약재료조회";
		int intRtnVal						= -100;
		
		
		YdWrkbookMtlDao		ydWrkbookMtlDao		= new YdWrkbookMtlDao();
		
		intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	/**
	 * 작업예약재료수정
	 * @param recPara
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String updYdWrkbookmtl(JDTORecord recPara, int intGp) throws JDTOException {
		String szMsg					= "";
		String szMethodName				= "updYdWrkbookmtl";
		String szOperationName			= "작업예약재료수정";
		int intRtnVal					= -100;
		
		YdWrkbookMtlDao		ydWrkbookMtlDao		= new YdWrkbookMtlDao();
		
		intRtnVal			= ydWrkbookMtlDao.updYdWrkbookmtl(recPara, intGp);
		
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 작업예약재료가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -1) {
				szMsg="["+szOperationName+"] 작업예약재료가 중복됩니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_DUPLICATE;
			}else if(intRtnVal == -2) {
				szMsg="["+szOperationName+"] 작업예약재료 수정 시 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}else if(intRtnVal == -3){
				szMsg="["+szOperationName+"] 작업예약재료 수정 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 작업예약재료삭제
	 * @param recPara
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String delYdWrkbookmtlByWBookId(JDTORecord recPara) throws JDTOException {
		String szMsg					= "";
		String szMethodName				= "delYdWrkbookmtlByWBookId";
		String szOperationName			= "작업예약재료삭제(작업예약ID)";
		int intRtnVal					= -100;
		
		YdWrkbookMtlDao		ydWrkbookMtlDao		= new YdWrkbookMtlDao();
		
		intRtnVal			= ydWrkbookMtlDao.updYdWrkbookmtlDelete(recPara);
		
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 작업예약재료가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -2) {
				szMsg="["+szOperationName+"] 작업예약재료 삭제 시 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}else if(intRtnVal == -3){
				szMsg="["+szOperationName+"] 작업예약재료 삭제 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}else{
			szMsg="["+szOperationName+"] 작업예약재료가 성공적으로 삭제되었습니다. - 대상재 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	//------------------------------------------------------------------------------------------------
	
	
	/*------------------------------------------------------------------------------------------------
	 * 크레인스케줄 DAO
	 ------------------------------------------------------------------------------------------------*/
	
	/**
	 * 크레인스케줄조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdCrnsch(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdCrnsch";
		String szOperationName			= "크레인스케줄조회";
		int intRtnVal						= -100;
		
		
		YdCrnSchDao		ydCrnSchDao		= new YdCrnSchDao();
		
		intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	/**
	 * 크레인스케줄 수정
	 * @param recCrnSch
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String updYdCrnsch(JDTORecord recCrnSch, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdStklyr";
		String szOperationName			= "크레인스케줄수정";
		int intRtnVal						= -100;
		
		YdCrnSchDao		ydCrnSchDao		= new YdCrnSchDao();
		
		intRtnVal = ydCrnSchDao.updYdCrnsch(recCrnSch, intGp);
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 크레인 스케줄이 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -1) {
				szMsg="["+szOperationName+"] 크레인 스케줄이 중복됩니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_DUPLICATE;
			}else if(intRtnVal == -2) {
				szMsg="["+szOperationName+"] 크레인 스케줄 수정 시 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}else if(intRtnVal == -3){
				szMsg="["+szOperationName+"] 크레인스케줄 수정 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}
		
		return szRtnMsg;
	}
	
	//------------------------------------------------------------------------------------------------
	
	
	/*------------------------------------------------------------------------------------------------
	 * 크레인스케줄 작업재료 DAO
	 ------------------------------------------------------------------------------------------------*/
	
	/**
	 * 크레인스케줄작업재료조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdCrnwrkmtl(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdCrnwrkmtl";
		String szOperationName			= "크레인스케줄작업재료조회";
		int intRtnVal						= -100;
		
		
		YdCrnWrkMtlDao		ydCrnWrkMtlDao		= new YdCrnWrkMtlDao();
		
		intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	/**
	 * 크레인스케줄 작업재료 수정
	 * @param recCrnSch
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String updYdCrnwrkmtl(JDTORecord recCrnSch, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdStklyr";
		String szOperationName			= "크레인스케줄 작업재료 수정";
		int intRtnVal						= -100;
		
		YdCrnWrkMtlDao		ydCrnWrkMtlDao		= new YdCrnWrkMtlDao();
		
		intRtnVal = ydCrnWrkMtlDao.updYdCrnwrkmtl(recCrnSch, intGp);
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -1) {
				szMsg="["+szOperationName+"] 중복됩니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_DUPLICATE;
			}else if(intRtnVal == -2) {
				szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}else if(intRtnVal == -3){
				szMsg="["+szOperationName+"] 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}
		
		return szRtnMsg;
	}
	
	//------------------------------------------------------------------------------------------------
	
	
	/*------------------------------------------------------------------------------------------------
	 * 적치열 DAO
	 ------------------------------------------------------------------------------------------------*/
	
	/**
	 * 적치열조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdStkcol(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdStkcol";
		String szOperationName			= "적치열조회";
		int intRtnVal						= -100;
		
		
		YdStkColDao		ydStkColDao		= new YdStkColDao();
		
		intRtnVal = ydStkColDao.getYdStkcol(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	//------------------------------------------------------------------------------------------------
	
	
	/*------------------------------------------------------------------------------------------------
	 * 적치베드 DAO
	 ------------------------------------------------------------------------------------------------*/
	
	/**
	 * 적치베드조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdStkbed(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdStkbed";
		String szOperationName			= "적치베드조회";
		int intRtnVal						= -100;
		
		
		YdStkBedDao		ydStkBedDao		= new YdStkBedDao();
		
		intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	/**
	 * 적치베드수정
	 * @param recPara
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String updYdStkbed(JDTORecord recPara, int intGp) throws JDTOException {
		String szMsg					= "";
		String szMethodName				= "updYdStkbed";
		String szOperationName			= "적치베드수정";
		int intRtnVal					= -100;
		
		YdStkBedDao		ydStkBedDao		= new YdStkBedDao();
		
		intRtnVal			= ydStkBedDao.updYdStkbed(recPara, intGp);
		
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 적치베드이 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -1) {
				szMsg="["+szOperationName+"] 적치베드이 중복됩니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_DUPLICATE;
			}else if(intRtnVal == -2) {
				szMsg="["+szOperationName+"] 적치베드 수정 시 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}else if(intRtnVal == -3){
				szMsg="["+szOperationName+"] 적치베드 수정 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	//------------------------------------------------------------------------------------------------
	
	
	/*------------------------------------------------------------------------------------------------
	 * 적치단 DAO
	 ------------------------------------------------------------------------------------------------*/
	
	/**
	 * 적치단조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdStklyr(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdStklyr";
		String szOperationName			= "적치단조회";
		int intRtnVal						= -100;
		
		
		YdStkLyrDao		ydStkLyrDao		= new YdStkLyrDao();
		
		intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	/**
	 * 적치단수정
	 * @param recPara
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String updYdStklyr(JDTORecord recPara, int intGp) throws JDTOException {
		String szMsg					= "";
		String szMethodName				= "uptYdStklyr";
		String szOperationName			= "적치단수정";
		int intRtnVal					= -100;
		
		YdStkLyrDao		ydStkLyrDao		= new YdStkLyrDao();
		
		intRtnVal			= ydStkLyrDao.updYdStklyr(recPara, intGp);
		
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 적치단이 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -1) {
				szMsg="["+szOperationName+"] 적치단이 중복됩니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_DUPLICATE;
			}else if(intRtnVal == -2) {
				szMsg="["+szOperationName+"] 적치단 수정 시 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}else if(intRtnVal == -3){
				szMsg="["+szOperationName+"] 적치단 수정 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	//------------------------------------------------------------------------------------------------
	
	
	
	
	
	/*------------------------------------------------------------------------------------------------
	 * 저장품 DAO
	 ------------------------------------------------------------------------------------------------*/
	
	/**
	 * 저장품조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdStock(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdStock";
		String szOperationName			= "저장품조회";
		int intRtnVal						= -100;
		
		
		YdStockDao		ydStockDao		= new YdStockDao();
		
		intRtnVal = ydStockDao.getYdStock(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	/**
	 * 저장품의 작업예약ID/스케줄코드삭제
	 * @param recPara
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String updYdStockDelYdWBookId(JDTORecord recPara) throws JDTOException {
		String szMsg					= "";
		String szMethodName				= "updYdStockDelYdWBookId";
		String szOperationName			= "저장품의 작업예약ID/스케줄코드삭제";
		int intRtnVal					= -100;
		
		YdStockDao		ydStockDao		= new YdStockDao();
		
		intRtnVal			= ydStockDao.updYdStockDelYdWBookId(recPara);
		
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -1) {
				szMsg="["+szOperationName+"] 중복됩니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_DUPLICATE;
			}else if(intRtnVal == -2) {
				szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}else if(intRtnVal == -3){
				szMsg="["+szOperationName+"] 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/*------------------------------------------------------------------------------------------------
	 * 대차스케줄 DAO
	 ------------------------------------------------------------------------------------------------*/
	
	/**
	 * 대차스케줄조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdTcarsch(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdTcarsch";
		String szOperationName			= "대차스케줄조회";
		int intRtnVal						= -100;
		
		
		YdTcarSchDao		ydTcarSchDao		= new YdTcarSchDao();
		
		intRtnVal = ydTcarSchDao.getYdTcarsch(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	
	/**
	 * 대차스케줄수정
	 * @param recPara
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String updYdTcarsch(JDTORecord recPara, int intGp) throws JDTOException {
		String szMsg					= "";
		String szMethodName				= "updYdTcarsch";
		String szOperationName			= "대차스케줄수정";
		int intRtnVal					= -100;
		
		YdTcarSchDao		ydTcarSchDao		= new YdTcarSchDao();
		
		intRtnVal			= ydTcarSchDao.updYdTcarsch(recPara, intGp);
		
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -1) {
				szMsg="["+szOperationName+"] 중복됩니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_DUPLICATE;
			}else if(intRtnVal == -2) {
				szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}else if(intRtnVal == -3){
				szMsg="["+szOperationName+"] 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 대차스케줄수정
	 * @param recPara
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String updYdTCarschDir(JDTORecord recPara, int intGp) throws JDTOException {
		String szMsg					= "";
		String szMethodName				= "updYdTcarsch";
		String szOperationName			= "대차스케줄수정";
		int intRtnVal					= -100;
		
		YdTcarSchDao		ydTcarSchDao		= new YdTcarSchDao();
		
		intRtnVal			= ydTcarSchDao.updYdTCarschDir(recPara, intGp);
		
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -2) {
				szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}else if(intRtnVal == -3){
				szMsg="["+szOperationName+"] 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	//------------------------------------------------------------------------------------------------
	
	
	/*------------------------------------------------------------------------------------------------
	 * 대차이송재료 DAO
	 ------------------------------------------------------------------------------------------------*/
	
	/**
	 * 대차이송재료조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdTcarftmvmtl(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdTcarftmvmtl";
		String szOperationName			= "대차이송재료조회";
		int intRtnVal						= -100;
		
		
		YdTcarFtmvMtlDao		ydTcarFtmvMtlDao		= new YdTcarFtmvMtlDao();
		
		intRtnVal = ydTcarFtmvMtlDao.getYdTcarftmvmtl(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	
	/**
	 * 대차이송재료수정
	 * @param recPara
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String updYdTcarftmvmtl(JDTORecord recPara, int intGp) throws JDTOException {
		String szMsg					= "";
		String szMethodName				= "updYdTcarftmvmtl";
		String szOperationName			= "대차이송재료수정";
		int intRtnVal					= -100;
		
		YdTcarFtmvMtlDao		ydTcarFtmvMtlDao		= new YdTcarFtmvMtlDao();
		
		intRtnVal			= ydTcarFtmvMtlDao.updYdTcarftmvmtl(recPara, intGp);
		
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -1) {
				szMsg="["+szOperationName+"] 중복됩니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_DUPLICATE;
			}else if(intRtnVal == -2) {
				szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}else if(intRtnVal == -3){
				szMsg="["+szOperationName+"] 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	
	
	/*------------------------------------------------------------------------------------------------
	 * 차량스케줄 DAO
	 ------------------------------------------------------------------------------------------------*/
	
	/**
	 * 차량스케줄조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdCarsch(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdCarsch";
		String szOperationName			= "차량스케줄조회";
		int intRtnVal						= -100;
		
		
		YdCarSchDao		ydCarSchDao		= new YdCarSchDao();
		
		intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	
	/**
	 * 차량스케줄수정
	 * @param recPara
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String updYdCarsch(JDTORecord recPara, int intGp) throws JDTOException {
		String szMsg					= "";
		String szMethodName				= "updYdCarsch";
		String szOperationName			= "차량스케줄수정";
		int intRtnVal					= -100;
		
		YdCarSchDao		ydCarSchDao		= new YdCarSchDao();
		
		intRtnVal			= ydCarSchDao.updYdCarsch(recPara, intGp);
		
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -1) {
				szMsg="["+szOperationName+"] 중복됩니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_DUPLICATE;
			}else if(intRtnVal == -2) {
				szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}else if(intRtnVal == -3){
				szMsg="["+szOperationName+"] 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	//------------------------------------------------------------------------------------------------
	
	
	/*------------------------------------------------------------------------------------------------
	 * 차량이송재료 DAO
	 ------------------------------------------------------------------------------------------------*/
	
	/**
	 * 차량이송재료조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdCarftmvmtl(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdCarsch";
		String szOperationName			= "차량이송재료조회";
		int intRtnVal						= -100;
		
		
		YdCarFtmvMtlDao		ydCarFtmvMtlDao		= new YdCarFtmvMtlDao();
		
		intRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	/**
	 * 차량이송재료수정
	 * @param recPara
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String updYdCarftmvmtl(JDTORecord recPara, int intGp) throws JDTOException {
		String szMsg					= "";
		String szMethodName				= "updYdCarftmvmtl";
		String szOperationName			= "차량이송재료수정";
		int intRtnVal					= -100;
		
		YdCarFtmvMtlDao		ydCarFtmvMtlDao		= new YdCarFtmvMtlDao();
		
		intRtnVal			= ydCarFtmvMtlDao.updYdCarftmvmtl(recPara, intGp);
		
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -1) {
				szMsg="["+szOperationName+"] 중복됩니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_DUPLICATE;
			}else if(intRtnVal == -2) {
				szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}else if(intRtnVal == -3){
				szMsg="["+szOperationName+"] 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	
	//------------------------------------------------------------------------------------------------
	
	
	/*------------------------------------------------------------------------------------------------
	 * 저장속성그룹 DAO
	 ------------------------------------------------------------------------------------------------*/
	
	/**
	 * 저장속성그룹조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdPilingGrp(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdPilingGrp";
		String szOperationName			= "저장속성그룹조회";
		int intRtnVal						= -100;
		
		
		YdPilingGrpDao		ydPilingGrpDao		= new YdPilingGrpDao();
		
		intRtnVal = ydPilingGrpDao.getYdPilingGrp(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	//------------------------------------------------------------------------------------------------
	
	
	/**
	 * 설비조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdEqp(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdEqp";
		String szOperationName			= "설비조회";
		int intRtnVal						= -100;
		
		
		YdEqpDao		ydEqpDao		= new YdEqpDao();
		
		intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	/**
	 * 설비수정
	 * @param recPara
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String updYdEqp(JDTORecord recPara, int intGp) throws JDTOException {
		String szMsg					= "";
		String szMethodName				= "updYdEqp";
		String szOperationName			= "설비수정";
		int intRtnVal					= -100;
		
		YdEqpDao		ydEqpDao		= new YdEqpDao();
		
		intRtnVal			= ydEqpDao.updYdEqp(recPara, intGp);
		
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -1) {
				szMsg="["+szOperationName+"] 중복됩니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_DUPLICATE;
			}else if(intRtnVal == -2) {
				szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}else if(intRtnVal == -3){
				szMsg="["+szOperationName+"] 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	//------------------------------------------------------------------------------------------------
	
	/**
	 * 크레인사양조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdCrnspec(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdCrnspec";
		String szOperationName			= "크레인사양조회";
		int intRtnVal					= -100;
		
		YdCrnSpecDao		ydCrnSpecDao		= new YdCrnSpecDao();
		
		intRtnVal = ydCrnSpecDao.getYdCrnspec(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	
	/**
	 * 스케줄기준조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdSchrule(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdSchrule";
		String szOperationName			= "스케줄기준조회";
		int intRtnVal						= -100;
		
		
		YdSchRuleDao		ydSchRuleDao		= new YdSchRuleDao();
		
		intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	/**
	 * 위치검색범위조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdLocsrchrng(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdLocsrchrng";
		String szOperationName			= "위치검색범위조회";
		int intRtnVal						= -100;
		
		
		YdLocSrchRngDao		ydLocSrchRngDao		= new YdLocSrchRngDao();
		
		intRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	
	/**
	 * 후판제품공통테이블수정
	 * @param recPara
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String updPtPlateComm(JDTORecord recPara, int intGp) throws JDTOException {
		String szMsg					= "";
		String szMethodName				= "updPtPlateComm";
		String szOperationName			= "후판제품공통테이블수정";
		int intRtnVal					= -100;
		
		PtPlateCommDao		ptPlateCommDao		= new PtPlateCommDao();
		
		intRtnVal			= ptPlateCommDao.updPtPlateComm(recPara, intGp);
		
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -1) {
				szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}else if(intRtnVal == -3){
				szMsg="["+szOperationName+"] 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 크레인스케쥴의 재료정보가 특정조건일때 특정To위치를 검색하는 기능
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getToLocWithDanPok(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdLocsrchrng";
		String szOperationName			= "특정To위치를 검색";
		int intRtnVal					= -100;
		
		YdLocSrchRngDao		ydLocSrchRngDao		= new YdLocSrchRngDao();
		
		intRtnVal = ydLocSrchRngDao.getToLocWithDanPok(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if( intRtnVal == -2 ) {
			szMsg="["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 오류발생";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
			szMsg="["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}

}
