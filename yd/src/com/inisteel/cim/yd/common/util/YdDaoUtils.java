package com.inisteel.cim.yd.common.util;


import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

import java.util.Iterator;

public class YdDaoUtils {
	
	String szClassName = this.getClass().getName();
	YdUtils ydUtils = new YdUtils();
	
	public static final char STRING_TYPE			= 'S';
	public static final char DATETIME_TYPE 			= 'T';
	public static final char DOUBLE_TYPE 			= 'D';
	public static final char LONG_TYPE				= 'L';
	public static final char INTEGER_TYPE			= 'I';
	public static final char PAGE_COUNT_TYPE		= 'P';
	public static final char ROW_COUNT_TYPE			= 'R';
	
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
				if(szData.equals("") || ((!szData.equals("")) && szData.length() != intDataLen)) {
					intRtnVal = -1;
					//szMsg="<"+szMethodName+"  >>>>>  null이거나 고정길이 Error : Data ("+szData
					//         +"), Length("+szData.length()+", "+intDataLen+")";
					//ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.WARNING);
				}
			} else if (intNullChk == 1) {
				//not null이고 가변길이 체크
				if(szData.equals("")) {
					intRtnVal = -1;
					//szMsg="<"+szMethodName+"  >>>>> Null Data P1 : Data ("+szData+")";
					//ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.WARNING);
				}
				else {
					//제한길이보다 길면 cut
					if (szData.trim().length() > intDataLen) {
						//szMsg="<"+szMethodName+"  >>>>> Data길이 Error : Length("+szData.length()+", "+intDataLen+")";
						//ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.WARNING);
						intRtnVal = -3;
					}
				}
			} else if (intNullChk == 2) {
				//가변길이 체크
				if (szData.equals("")) {
					intRtnVal = 0;
					//szMsg="<"+szMethodName+"  >>>>> Null Data P2 : Data ("+szData+")";
					//ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.WARNING);
				}
				else
					//제한길이보다 길면 cut
					if (szData.trim().length() > intDataLen) {
						intRtnVal = -3;
						//szMsg="<"+szMethodName+"  >>>>> 제한길이보다 큼 : Length("+szData.length()+", "+intDataLen+")";
						//ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.WARNING);
					}
			} else if (intNullChk == 3) {
				//no check
				intRtnVal = 0;
			}
		} catch(Exception e) {
			
			szMsg = "Exception: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
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
			szTemp = this.paraRecChkNull_2(inRec, szFieldName);
	//		if (inRec.getFieldString(szFieldName) == null) {
	//			szTemp = "";
	//		}
	//		else
	//			szTemp = inRec.getFieldString(szFieldName);
	
			// parameter check
			intRtnVal = this.chkParam(szTemp, intMaxLen, intChkNull);
			
			// primary key error return
			if (intRtnVal == -1 || intRtnVal == -2) {
				szMsg = szFieldName + " Error!!!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}
			
			// data length error
			if (intRtnVal == -3)
				//data cut
				szData = this.dataCut(szTemp, intMaxLen);
			else
				szData = szTemp;
			
			
			if (szData.equals("")) {
				inRec.setField(szFieldName, szData);
			} else {
				//double
				if (chDataType == DOUBLE_TYPE) {
					dblVal = StringHelper.parseDouble(szData, this.makeErrorDouble(intPre, intPost));
					dblObj = new Double(dblVal);
					inRec.setField(szFieldName, dblObj);
				}else if( chDataType == INTEGER_TYPE ) {
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
				}else if( chDataType == DATETIME_TYPE ) {
					inRec.setField(szFieldName, szData.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", ""));
				//String	
				} else
					inRec.setField(szFieldName, szData);
			}
		} catch (Exception e) {
			szMsg = "chkField() Exception";
			ydUtils.putLog(YdCarSchDao.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
		
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
			if (inRec.getField(szFieldName) == null)
			{}
			else if("".equals(inRec.getField(szFieldName))){
				outRec.setField(szFieldName,"");
			}
			else {
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
		String strRtnVal = new String();

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
		String szMsg        = null;
		String szMethodName = "makeErrorLong";
		long lngRtnVal;
		String strTemp = new String();
		
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
		String strTemp = new String();

		if (intPre == 0 && intPost == 0)
			return dblRtnVal = 0;
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
	public String paraRecChkNull(JDTORecord recPara, String szFieldName) throws JDTOException {
		String szRtnVal = null;
		if (recPara.getField(szFieldName) == null)
			szRtnVal = "";
		else
			szRtnVal = recPara.getFieldString(szFieldName).trim();				//임춘수 2009.04.24 수정 trim() 추가
		
		return szRtnVal;
	} // end of paraRecChkNull

	
	/**
	 *      [A] 오퍼레이션명 : paraRecChkNull_2 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return String			     // 해당 필드의 데이터
	 * @throws JDTOException 
	 */
	public String paraRecChkNull_2(JDTORecord recPara, String szFieldName) throws JDTOException {
		String szRtnVal = null;
		if (recPara.getField(szFieldName) == null)
			szRtnVal = "";
		else
			szRtnVal = recPara.getFieldString(szFieldName);				//임춘수 2009.04.24 수정 trim() 추가
		
		return szRtnVal;
	} // end of paraRecChkNull
	
	/**
	 *      [A] 오퍼레이션명 : paraRecChkNull_2 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return String			     // 해당 필드의 데이터
	 * @throws JDTOException 
	 */
	public double paraRecChkNullDouble(JDTORecord recPara, String szFieldName) throws JDTOException {
		double dlRtnVal = 0;
		if (recPara.getField(szFieldName) == null)
			dlRtnVal = 0;
		else{
			if (recPara.getFieldString(szFieldName).trim().equals(""))
				dlRtnVal = 0;
			else
			dlRtnVal = Double.parseDouble(recPara.getFieldString(szFieldName));
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
		
		if (recPara.getFieldString(szFieldName) == null)
			intRtnVal = 0;
		else {
			
			if (recPara.getFieldString(szFieldName).trim().equals(""))
				intRtnVal = 0;
			else
				intRtnVal = recPara.getFieldInt(szFieldName);
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
		
		if (recPara.getFieldString(szFieldName) == null)
			lngRtnVal = 0;
		else {
			if (recPara.getFieldString(szFieldName).trim().equals(""))
				lngRtnVal = 0;
			else
				lngRtnVal = Long.parseLong(recPara.getFieldString(szFieldName));
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
		
		try{
		intTemp = Integer.parseInt(szPara) + intPara;
		}catch(Exception e){
		}
		if (intTemp < 10)
			szRtnVal = "00" + intTemp;
		else if (intTemp > 9 && intTemp < 100) 
			szRtnVal = "0" + intTemp;
		else if (intTemp > 99)
			szRtnVal = "" + intTemp;
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
		}catch(Exception e){
		}
		if (intTemp < 10)
			szRtnVal = "0" + intTemp;
		else if (intTemp > 9 && intTemp < 100)
			szRtnVal = "" + intTemp;
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
			if(szPara.matches("\\d\\d")) {
				intTemp = Integer.parseInt(szPara) + intPara;
				if (intTemp < 10) {
					szRtnVal = "0" + intTemp;
				} else if (intTemp > 9 && intTemp < 100){
					szRtnVal = "" + intTemp;
				} else if (intTemp == 100){
					szRtnVal = "9A";
				}	
			} else {
				
				if(szPara.substring(1, 2).equals("9")) {
					szTemp = "A";
				} else if(szPara.substring(1, 2).equals("A")) {
					szTemp = "B";
				} else if(szPara.substring(1, 2).equals("B")) {
					szTemp = "C";
				} else if(szPara.substring(1, 2).equals("C")) {
					szTemp = "D";
				} else if(szPara.substring(1, 2).equals("D")) {
					szTemp = "E";
				} else if(szPara.substring(1, 2).equals("E")) {
					szTemp = "F";
				} else {
					szTemp = ""+ (Integer.parseInt(szPara.substring(1, 2)) + intPara);
				}
				
				szRtnVal = szPara.substring(0, 1) + szTemp;
				
			}	
		}catch(Exception e){
			
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

} // end of class






