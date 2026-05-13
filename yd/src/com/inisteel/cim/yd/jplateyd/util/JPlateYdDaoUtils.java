/*
 * @(#) 2후판정정야드 DAO관련 UTIL
 *
 * @version			V1.00
 * @author			김현우
 * @date			2013.04.03
 *
 * @description		2후판정정야드 DAO관련 UTIL
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.01  2013/04/03   김현우      김현우       신규작성 (기존 YdDaoUtil을 복사해서 사용)
 */

package com.inisteel.cim.yd.jplateyd.util;


import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import java.util.Iterator;

import com.inisteel.cim.common.exception.DAOException;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO;

public class JPlateYdDaoUtils {

	private static final String szClassName 	= JPlateYdDaoUtils.class.getName();
//	private static final char 	STRING_TYPE		= 'S';
	private static final char 	DATETIME_TYPE 	= 'T';
	private static final char 	DOUBLE_TYPE 	= 'D';
	private static final char 	LONG_TYPE		= 'L';
	private static final char 	INTEGER_TYPE	= 'I';
	private static final char 	PAGE_COUNT_TYPE	= 'P';
	private static final char 	ROW_COUNT_TYPE	= 'R';

	private JPlateYdUtils ydUtils = new JPlateYdUtils();

	/**
	 *      [A] 오퍼레이션명 : chkParam
	 *
	 * @param String szData			// 체크 대상 문자열
	 *        int    intDataLen     // 체크 대상 문자열 최대 길이
	 *        int    intNullChk     // Null Check 구분 0: primary key Check, 1: Null Check Length Check,
	 *                                                2: Length Check, 3: No Check
	 * @return int      			// 0:성공, -1:pk error, -3:data length over
	 * @throws
	 */
	public int chkParam(String szData, int intDataLen, int intNullChk) {
		String szMsg        = null;
		String szMethodName = "chkParam";
		int intRtnVal = 0;

		try {
			if (intNullChk == 0) {
				//not null이고 고정길이 체크
				if("".equals(szData) || szData.length() != intDataLen) {
					intRtnVal = -1;
				}
			} else if (intNullChk == 1) {
				//not null이고 가변길이 체크
				if ("".equals(szData)) {
					intRtnVal = -1;
				} else {
					//제한길이보다 길면 cut
					if (szData.trim().length() > intDataLen) {
						intRtnVal = -3;
					}
				}
			} else if (intNullChk == 2) {
				//가변길이 체크
				if ("".equals(szData)) {
					intRtnVal = 0;
				} else {
					//제한길이보다 길면 cut
					if (szData.trim().length() > intDataLen) {
						intRtnVal = -3;
					}
				}
			} else if (intNullChk == 3) {
				//no check
				intRtnVal = 0;
			}
		} catch(Exception e) {

			szMsg = "Exception: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.ERROR);
			return intRtnVal = 0;
		}
		return intRtnVal;
	} // end of chkParam

	/**
	 *      [A] 오퍼레이션명 : chkField
	 *
	 * @param JDTORecord inRec	         대상 레코드,
	 *        String     szFieldName     Field Name,
	 *        int        intMaxLen       Field Length,
	 *        int        intNullChk      Null Check 구분(0: primary key Check, 1: Null Check Length Check,
	 *                                                  2: Length Check,  3: No Check),
	 *        char       chDataType      DataType('S':String, 'D':double, 'L':long, 'P':PAGE[LONG], 'R':ROW[LONG]),
	 *        int        intPre          지수부 길이,
	 *        int        intPost         소수부 길이,
	 * @return true, false			     true:성공, false:실패
	 * @throws JDTOException
	 */
	public boolean chkField (JDTORecord inRec, String szFieldName, int intMaxLen,
			                 int intChkNull, char chDataType , int intPre, int  intPost) throws JDTOException {
		String szMethodName = "chkField";
		int intRtnVal = 0;
		double dblVal = 0;
		Double dblObj = null;
		int intVal = 0;
		Integer intObj = null;
		long lngVal = 0;
		Long lngObj = null;
		boolean blnRtnVal = true;
		String szTemp = null;
		String szData = null;
		String szMsg = null;

		try {
			szTemp = this.paraRecChkNull2(inRec, szFieldName);

			// parameter check
			intRtnVal = this.chkParam(szTemp, intMaxLen, intChkNull);

			// primary key error return
			if (intRtnVal == -1 || intRtnVal == -2) {
				szMsg = szFieldName + " Error!!!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.ERROR);
				return blnRtnVal = false;
			}

			// data length error
			if (intRtnVal == -3) {
				//data cut
				szData = this.dataCut(szTemp, intMaxLen);
			} else {
				szData = szTemp;
			}

			if ("".equals(szData)) {
				inRec.setField(szFieldName, szData);
			} else {
				//double
				if (chDataType == DOUBLE_TYPE) {
					dblVal = StringHelper.parseDouble(szData, this.makeErrorDouble(intPre, intPost));
					dblObj = new Double(dblVal);
					inRec.setField(szFieldName, dblObj);
				} else if( chDataType == INTEGER_TYPE ) {
				//int
					intVal = StringHelper.parseInt(szData);
					intObj = new Integer(intVal);
					inRec.setField(szFieldName, intObj);
				//long
				} else if (chDataType == LONG_TYPE) {
					lngVal = StringHelper.parseLong(szData, this.makeErrorLong(intMaxLen));
					lngObj = new Long(lngVal);
					inRec.setField(szFieldName, lngObj);
				//Page Count 처리
				} else if (chDataType == PAGE_COUNT_TYPE) {
					lngVal = StringHelper.parseLong(szData, 1);
					lngObj = new Long(lngVal);
					inRec.setField(szFieldName, lngObj);
				//Row Count 처리
				} else if (chDataType == ROW_COUNT_TYPE) {
					lngVal = StringHelper.parseLong(szData, 10);
					lngObj = new Long(lngVal);
					inRec.setField(szFieldName, lngObj);
				} else if (chDataType == DATETIME_TYPE ) {
					inRec.setField(szFieldName, szData.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", ""));
				//String
				} else {
					inRec.setField(szFieldName, szData);
				}
			}
		} catch (Exception e) {
			szMsg = "chkField() Exception";
			ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szClassName + e.getMessage(), e);
		}

		return blnRtnVal;
	}


	/**
	 *      [A] 오퍼레이션명 : mappingData
	 *
	 * @param JDTORecord inRec	     // 변환 레코드
	 *        JDTORecord outRec	     // 기준 레코드
	 *        String     szFieldName // 필드 명
	 * @return void
	 * @throws JDTOException
	 */
	public void mappingData(JDTORecord inRec, JDTORecord outRec, String szFieldName) throws JDTOException {

		try {
			if (inRec.getField(szFieldName) == null) {
				// SKIP
			} else if("".equals(inRec.getField(szFieldName))) {
				outRec.setField(szFieldName, "");
			} else {
				outRec.setField(szFieldName, inRec.getField(szFieldName));
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szClassName + e.getMessage(), e);
		}
	} // end of mappingData


	/**
	 *      [A] 오퍼레이션명 : dataCut
	 *
	 * @param int intMaxLen         // Field Length
	 * @return String     			// 데이터 길이로 보정된 String
	 */
	public String dataCut(String strValue, int intMaxLen) {
		String strRtnVal = "";

		for(int i = 0; i < intMaxLen; i++){
			strRtnVal = strRtnVal + strValue.charAt(i);
		}

		return strRtnVal;
	}


	/**
	 *      [A] 오퍼레이션명 : makeErrorLong
	 *
	 * @param int intMaxLen         // Field Length
	 * @return long     			// 데이터 길이만큼 9로 채워진 long 값
	 */
	public long makeErrorLong(int intMaxLen) {

		long lngRtnVal;
		String strTemp = "";

		for (int i = 0; i < intMaxLen; i++) {
			strTemp = strTemp.concat("9");
		}
		lngRtnVal = StringHelper.parseLong(strTemp);

		return lngRtnVal;
	} // end of makeErrorLong


	/**
	 *      [A] 오퍼레이션명 : makeErrorDouble
	 *
	 * @param int intPre            // Field Length(지수부)
	 *        int intPost           // Field Length(소수부)
	 * @return double			    // 데이터 길이만큼 9로 채워진 double 값
	 */
	public double makeErrorDouble(int intPre, int intPost) {

		double dblRtnVal;
		String strTemp = "";

		if (intPre == 0 && intPost == 0) {
			return dblRtnVal = 0;
		}
		for (int i = 0; i < intPre; i++) {
			strTemp = strTemp.concat("9");
		}
		if (intPost > 0) {
			strTemp = strTemp.concat(".");
			for (int i = 0; i < intPost; i++) {
				strTemp = strTemp.concat("9");
			}
		}
		dblRtnVal = StringHelper.parseDouble(strTemp);

		return dblRtnVal;
	} // end of makeErrorDouble

	/**
	 *      [A] 오퍼레이션명 : paraRecChkNull
	 *
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return String			     // 해당 필드의 데이터
	 * @throws JDTOException
	 */
	public String paraRecChkNull(JDTORecord recPara, String pFieldName) throws JDTOException {
		String szRtnVal = null;
		if (recPara == null) {
			szRtnVal = "";
		} else if (recPara.getField(pFieldName) == null) {
			szRtnVal = "";
		} else {
			szRtnVal = recPara.getFieldString(pFieldName).trim();				//임춘수 2009.04.24 수정 trim() 추가
		}
		return szRtnVal;
	} // end of paraRecChkNull

	/**
	 *      [A] 오퍼레이션명 : paraRecChkNull Overriding
	 *
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return String			     // 해당 필드의 데이터
	 * @throws JDTOException
	 */
	public String paraRecChkNull(JDTORecord recPara, String pFieldName, String pDefStr) throws JDTOException {
		String szRtnVal = paraRecChkNull(recPara, pFieldName);
		if ("".equals(szRtnVal)) {
			szRtnVal = pDefStr;
		}
		return szRtnVal;
	} // end of paraRecChkNull

	/**
	 *      [A] 오퍼레이션명 : 파라미터에서 수정자 항목 Get
	 *
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return String			     // 해당 필드의 데이터
	 * @throws JDTOException
	 */
	public String paraRecModifier(JDTORecord recPara) throws JDTOException {
		String szModifier = "";

		szModifier = this.paraRecChkNull(recPara, "YD_USER_ID");
		if ("".equals(szModifier)) {
			szModifier	= this.paraRecChkNull(recPara, "MODIFIER");
		}
		if ("".equals(szModifier)) {
			szModifier	= this.paraRecChkNull(recPara, "REGISTER");
		}
		if ("".equals(szModifier)) {
			szModifier	= ydUtils.getTcCode(recPara);
		}
		if ("".equals(szModifier)) {
			szModifier	= "YARDSYSTEM";
		}

		return szModifier;
	} // end of paraRecChkNull

	/**
	 *      [A] 오퍼레이션명 : paraRecChkNull2
	 *
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return String			     // 해당 필드의 데이터
	 * @throws JDTOException
	 */
	public String paraRecChkNull2(JDTORecord recPara, String szFieldName) throws JDTOException {
		String szRtnVal = null;
		if (recPara.getField(szFieldName) == null) {
			szRtnVal = "";
		} else {
			szRtnVal = recPara.getFieldString(szFieldName);				//임춘수 2009.04.24 수정 trim() 추가
		}
		return szRtnVal;
	} // end of paraRecChkNull

	/**
	 *      [A] 오퍼레이션명 : paraRecChkNullDouble
	 *
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return String			     // 해당 필드의 데이터
	 * @throws JDTOException
	 */
	public double paraRecChkNullDouble(JDTORecord recPara, String szFieldName) throws JDTOException {
		double dlRtnVal = 0;
		if (recPara.getField(szFieldName) == null) {
			dlRtnVal = 0;
		} else {
			if ("".equals(recPara.getFieldString(szFieldName).trim())) {
				dlRtnVal = 0;
			} else {
				dlRtnVal = Double.parseDouble(recPara.getFieldString(szFieldName));
			}
		}
		return dlRtnVal;
	} // end of paraRecChkNull

	/**
	 *      [A] 오퍼레이션명 : paraRecChkNullInt
	 *
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return int			         // 해당 필드의 데이터
	 * @throws JDTOException
	 */
	public int paraRecChkNullInt(JDTORecord recPara, String szFieldName) throws JDTOException {
		int intRtnVal;

		if (recPara.getFieldString(szFieldName) == null) {
			intRtnVal = 0;
		} else {
			if ("".equals(recPara.getFieldString(szFieldName).trim())) {
				intRtnVal = 0;
			} else {
				intRtnVal = recPara.getFieldInt(szFieldName);
			}
		}

		return intRtnVal;
	} // end of paraRecChkNull


	/**
	 *      [A] 오퍼레이션명 : paraRecChkNullLong
	 *
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return long			     // 해당 필드의 데이터
	 * @throws JDTOException
	 */
	public long paraRecChkNullLong(JDTORecord recPara, String szFieldName) throws JDTOException {
		long lngRtnVal;

		if (recPara.getFieldString(szFieldName) == null) {
			lngRtnVal = 0;
		} else {
			if ("".equals(recPara.getFieldString(szFieldName).trim())) {
				lngRtnVal = 0;
			} else {
				lngRtnVal = Long.parseLong(recPara.getFieldString(szFieldName));
			}
		}
		return lngRtnVal;
	} // end of paraRecChkNullLong


	/**
	 *      [A] 오퍼레이션명 : stringPlusInt
	 *
	 * @param  String szPara         // 문자 값
	 *         int intPara           // 숫자 값
	 * @return String			     // 계산결과 문자열
	 */
	public String stringPlusInt(String szPara, int intPara) {
		String szRtnVal = null;
		int intTemp = 0;

		try {
			intTemp = Integer.parseInt(szPara) + intPara;
		} catch(Exception e) {
			//
			intTemp = 0;
		}

		if (intTemp < 10) {
			szRtnVal = "00" + intTemp;
		} else if (intTemp > 9 && intTemp < 100) {
			szRtnVal = "0" + intTemp;
		} else if (intTemp > 99) {
			szRtnVal = "" + intTemp;
		}

		return szRtnVal;
	} // end of stringPlusInt

	/**
	 *      [A] 오퍼레이션명 : stringPlusInt2
	 *
	 * @param  String szPara         // 문자 값
	 *         int intPara           // 숫자 값
	 * @return String			     // 계산결과 문자열
	 */
	public String stringPlusInt2(String szPara, int intPara) {
		String szRtnVal = null;
		int intTemp = 0;
		try{
			intTemp = Integer.parseInt(szPara) + intPara;
		} catch(Exception e) {
			// Exception Skip ??
			intTemp = 0;
		}

		if (intTemp < 10) {
			szRtnVal = "0" + intTemp;
		} else if (intTemp > 9 && intTemp < 100) {
			szRtnVal = "" + intTemp;
		}
		return szRtnVal;
	} // end of stringPlusInt2


	/**
	 *      [A] 오퍼레이션명 : stringPlusInt2
	 *
	 * @param  String szPara         // 문자 값
	 *         int intPara           // 숫자 값
	 * @return String			     // 계산결과 문자열
	 */
	public String stringPlusNext(String szPara, int intPara) {
		String szRtnVal = null;
		String szTemp   = null;
		int intTemp = 0;

		try{
			if (szPara.matches("\\d\\d")) {
				intTemp = Integer.parseInt(szPara) + intPara;
				if (intTemp < 10) {
					szRtnVal = "0" + intTemp;
				} else if (intTemp > 9 && intTemp < 100){
					szRtnVal = "" + intTemp;
				} else if (intTemp == 100){
					szRtnVal = "9A";
				}
			} else {

				if("9".equals(szPara.substring(1, 2))) {
					szTemp = "A";
				} else if("A".equals(szPara.substring(1, 2))) {
					szTemp = "B";
				} else if("B".equals(szPara.substring(1, 2))) {
					szTemp = "C";
				} else if("C".equals(szPara.substring(1, 2))) {
					szTemp = "D";
				} else if("D".equals(szPara.substring(1, 2))) {
					szTemp = "E";
				} else if("E".equals(szPara.substring(1, 2))) {
					szTemp = "F";
				} else {
					szTemp = ""+ (Integer.parseInt(szPara.substring(1, 2)) + intPara);
				}

				szRtnVal = szPara.substring(0, 1) + szTemp;

			}
		} catch(Exception e) {
			// Exception Skip
			szRtnVal = "";
		}
		return szRtnVal;
	} // end of stringPlusInt2

	/**
	 *      [A] 오퍼레이션명 : conversionFieldname
	 *
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         int intGp             // 구분(0:"V_" 추가, 1:"V_" 제거
	 * @return JDTORecord			 // 필드명을 변환한 결과레코드
	 * @throws JDTOException
	 */
	public JDTORecord conversionFieldname(JDTORecord recPara, int intGp) throws JDTOException {
		JDTORecord recRtnVal = JDTORecordFactory.getInstance().create();
		String szFieldName = null;
		Iterator itrFieldName = null;

		//필드명을 가져온다.
		itrFieldName = recPara.iterateName();

		//필드명 갯수만큼 루프를 돈다.
		while(itrFieldName.hasNext()) {

			szFieldName = (String)itrFieldName.next();
			//"V_" 추가
			if (intGp == 0) {
				recRtnVal.setField("V_" + szFieldName, recPara.getField(szFieldName));
			//"V_" 제거
			} else {
				recRtnVal.setField(szFieldName.substring(2), recPara.getField(szFieldName));
			}
		}
		return recRtnVal ;
	}

    /**
     * Object Data Default 값을넣어주는 Function
     * PO
     * @param  Object , String
     * @return String
     * @throws Exception
     */
	public String setDataDefault(Object sObj, String sDef) throws Exception {

		if ( sObj == null || "".equals(sObj.toString())) {
			return sDef;
		}
		return sObj.toString();
	} // end of setDataDefault

	/**
	 * 오퍼레이션명 :	차량 또는 대차 작업예약 ID 삭제 Module (작업취소시 모듈임 Simple하게 수정할것!!)
	 *				[야드 이적 작업도 작업예약 취소하도록 변경 : 2013-10-21]
	 *				[RT북인 작업도 작업예약 취소하도록 변경 : 2013-10-28]
	 *
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord (JDTORecord : YD_WBOOK_ID , MODIFIER)
	 * @return
	 * @throws JDTOException
	 */
	public String delWBookBefoCarOrTCar(JDTORecord msgRecord)throws JDTOException  {

		/*
		 *  요약 : 작업예약 삭제 전에 해당 작업에 물려 있는 차량스케줄 또는 대차 스케줄 작업예약을
		 *  	   Clear 하기 위함
		 *
		 *  1. 취소할 작업예약 ID를 받는다.
		 *  2. 작업 예약 ID로 스케줄 코드를 조회한다.
		 *  3. 해당 작업예약 스케줄이 차량 대차 작업 일경우 상하차 작업을 확인하여 해당 스케줄의
		 *     상하차 작업 예약 ID를 Clear 한다.
		 *
		 *
		 */

		//대차 , 차량스케줄 DAO
		JPlateYdTcarSchDAO 		ydTcarSchDao 	= new JPlateYdTcarSchDAO();
		JPlateYdWrkbookDAO 		ydWrkbookDao	= new JPlateYdWrkbookDAO();
		JPlateYdWrkbookMtlDAO 	ydWrkbookMtlDao	= new JPlateYdWrkbookMtlDAO();

		JDTORecord inRec 	= JDTORecordFactory.getInstance().create();
		JDTORecord recPara 	= JDTORecordFactory.getInstance().create();
		JDTORecord recSch 	= JDTORecordFactory.getInstance().create();
		JDTORecord recWBook	= JDTORecordFactory.getInstance().create();

		//작업예약   레코드셋 생성
		JDTORecordSet rsYdWBook = null;

		//파라미터 스크링 변수
		String 	szOperationName = "대차/이적 작업예약 ID 삭제 Module";
		String 	szMethodName	= "delWBookBefoCarOrTCar";
		String 	szSchCd 		= null;
		String 	szULGp 			= null;
		String 	szYdWBookId 	= null;
		String	szModifier		= "";
		String	szYdStkColGp	= "";
		int		iRemainSchCnt	= 0;
		//리턴값
		int 	intRtnVal 		= 0;

		//체크 값
		String 	szMsg			= "";

		try {
			szMsg = "[Jsp Session  -  " + szOperationName +"] 대차/이적/RT 작업예약 삭제 처리 .... 시작 >>>> " + msgRecord.toString();
			ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.DEBUG);

			/*
			 *  입력받은 인자 Check
			 */
			ydUtils.displayRecord(szOperationName, msgRecord);
			szYdWBookId = this.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
			szModifier	= this.paraRecModifier(msgRecord);

			if ("".equals(szYdWBookId.trim())){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 작업예약 ID가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.ERROR);
				return JPlateYdConst.RETN_CD_EXIST;
			}

			/*
			 * 작업예약 ID정보로 작업예약 정보를 조회하여 스케줄 코드를 얻는다.
			 */
			rsYdWBook = JDTORecordFactory.getInstance().createRecordSet("rsYdWBook");

			intRtnVal = ydWrkbookDao.getYdWrkbook(msgRecord, rsYdWBook);		// intGp == 0

			if(intRtnVal < 0 ){

				szMsg = "[Jsp Session  -  " + szOperationName +"] 작업예약 조회 ERROR";
				ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.ERROR);

				return JPlateYdConst.RETN_CD_FAILURE;

			} else if(intRtnVal == 0){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 작업예약 데이터가 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return JPlateYdConst.RETN_CD_EXIST;
			}

			recSch = JDTORecordFactory.getInstance().create();

			rsYdWBook.first();
			recSch 			= rsYdWBook.getRecord();
			szSchCd 		= this.paraRecChkNull(recSch, 		"YD_SCH_CD");
			szYdStkColGp	= this.paraRecChkNull(recSch, 		"YD_STK_COL_GP");
			iRemainSchCnt	= this.paraRecChkNullInt(recSch, 	"REMAIN_SCH_CNT");

			if (iRemainSchCnt > 0) {
				szMsg = "[" + szOperationName +"] 크레인스케쥴이 존재하여 SKIP >>>> 건수 ::" + iRemainSchCnt;
				ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return JPlateYdConst.RETN_CD_SUCCESS;
			}

			if ("TC".equals(ydUtils.substr(szSchCd, 2, 2))) {
				// 작업예약 정보 삭제 [ 조건 : 해당스케쥴 코드로 크레인스케쥴 미존재시 ]
				if (iRemainSchCnt < 1) {

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_SCH_CD", 	szSchCd);
					recPara.setField("MODIFIER", 	szModifier);

					rsYdWBook = JDTORecordFactory.getInstance().createRecordSet("rsYdWBook");

					intRtnVal = ydWrkbookDao.getBySchCdWithCrnSchNo(recPara, rsYdWBook);
					if (intRtnVal > 0) {

						szMsg = "[Jsp Session  -  " + szOperationName +"] 대차 작업취소 START >>>> 건수 :" + intRtnVal;
						ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.DEBUG);

						rsYdWBook.first();
						for(int ii=0; ii<rsYdWBook.size(); ii++) {
							recWBook = JDTORecordFactory.getInstance().create();
							recWBook = rsYdWBook.getRecord(ii);
							recWBook.setField("MODIFIER", szModifier);

							szMsg = "["+szOperationName+"] 대차 작업예약 삭제  : " + ii + ">>>>" + this.paraRecChkNull(recSch, "YD_WBOOK_ID");
							ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.DEBUG);

							// 작업예약 삭제
							intRtnVal = ydWrkbookDao.delYdWrkbook(recWBook);

							// 작업예약 재료 삭제
							intRtnVal = ydWrkbookMtlDao.deldWrkbookMtl(recWBook);
						}
					}
				}

				szULGp = ydUtils.substr(szSchCd, 6, 1);
				inRec  = JDTORecordFactory.getInstance().create();

				if(JPlateYdConst.YD_CRN_SCH_CD_UD.equals(szULGp)) {

					//상차 인경우  작업예약 정보 삭제
					szMsg = "[Jsp Session  -  " + szOperationName +"] 대차 작업 상차  예약정보 삭제 시작";
					ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.DEBUG);

					inRec.setField("MODIFIER", 				szModifier);
					inRec.setField("YD_CARLD_WRK_BOOK_ID", 	szYdWBookId);

					intRtnVal = ydTcarSchDao.clsYdCarLdWrkBookId(inRec);

					if (intRtnVal < 0 ){
						szMsg = "[Jsp Session  -  " + szOperationName +"] 대차 작업 상차작업 삭제시 ERROR";
						ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.ERROR);
						return JPlateYdConst.RETN_CD_FAILURE;
					} else if(intRtnVal == 0 ){
						szMsg = "[Jsp Session  -  " + szOperationName +"] 대차 작업 상차할 작업이 없습니다.";
						ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.WARNING);
					} else {
						szMsg = "[Jsp Session  -  " + szOperationName +"] 대차스케줄의 상차 작업예약 ID 삭제하였습니다.";
						ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.DEBUG);
					}

				} else if (JPlateYdConst.YD_CRN_SCH_CD_LD.equals(szULGp)) {
					//하차인경우 작업예약 정보 삭제

					szMsg = "[Jsp Session  -  " + szOperationName +"] 대차 작업 하차  예약정보 삭제 시작";
					ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.DEBUG);

					inRec.setField("MODIFIER", 				szModifier);
					inRec.setField("YD_CARUD_WRK_BOOK_ID", 	szYdWBookId);
					intRtnVal = ydTcarSchDao.clsYdCarUdWrkBookId(inRec);

					if (intRtnVal <0 ){
						szMsg = "[Jsp Session  -  " + szOperationName +"] 대차 작업 하차작업 삭제시 ERROR";
						ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.ERROR);
						return JPlateYdConst.RETN_CD_FAILURE;
					} else if(intRtnVal == 0 ){
						szMsg = "[Jsp Session  -  " + szOperationName +"] 대차 작업하차할 작업이 없습니다.";
						ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.WARNING);
					} else {
						szMsg = "[Jsp Session  -  " + szOperationName +"] 대차스케줄의 하차 작업예약 ID 삭제하였습니다.";
						ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.DEBUG);
					}
				}
			} else if ("YD".equals(ydUtils.substr(szSchCd, 2, 2)) || "RT".equals(ydUtils.substr(szSchCd, 2, 2))) {
				// 작업예약 정보 삭제 [ 조건 : 해당스케쥴 코드로 크레인스케쥴 미존재시 ]
				if (iRemainSchCnt < 1) {

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_SCH_CD", 		szSchCd);
					recPara.setField("YD_STK_COL_GP",	szYdStkColGp);

					rsYdWBook = JDTORecordFactory.getInstance().createRecordSet("rsYdWBook");

					intRtnVal = ydWrkbookDao.getBySchCdWithYdStkColGp(recPara, rsYdWBook);
					if (intRtnVal > 0) {

						szMsg = "[Jsp Session  -  " + szOperationName +"] 이적/RT 작업예약 취소 START >>>> 건수 :" + intRtnVal;
						ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.DEBUG);

						rsYdWBook.first();
						for(int ii=0; ii<rsYdWBook.size(); ii++) {
							recWBook = JDTORecordFactory.getInstance().create();
							recWBook = rsYdWBook.getRecord(ii);
							recWBook.setField("MODIFIER", szModifier);

							szMsg = "["+szOperationName+"] 이적/RT 작업예약 삭제  : " + ii + ">>>>" + this.paraRecChkNull(recSch, "YD_WBOOK_ID");
							ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.DEBUG);

							// 작업예약 삭제
							intRtnVal = ydWrkbookDao.delYdWrkbook(recWBook);

							// 작업예약 재료 삭제
							intRtnVal = ydWrkbookMtlDao.deldWrkbookMtl(recWBook);
						}
					}
				}
			} else {
				szMsg = "[Jsp Session  -  " + szOperationName +"] 대차/이적/RT 작업아님으로 SKIP >>>> " + szSchCd;
				ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

} // end of class
