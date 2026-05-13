/*
 * @(#) 2후판정정 야드공통 UTIL
 *
 * @version			V1.00
 * @author			김현우
 * @date			2013.04.03
 *
 * @description		2후판정정 야드공통 UTIL
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.01  2013/04/03   김현우      김현우       신규작성 (기존 YdUtil을 복사해서 사용) 
 */

package com.inisteel.cim.yd.jplateyd.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordImplMap;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;

import com.metis.rapi4j.ResultData;

public class JPlateYdUtils {

	private static final String szSessionName = JPlateYdUtils.class.getName();

	private static boolean bDebugFlag 	= false;
	private Logger logger 				= new Logger("yd");

	private	static JPlateYdDaoUtils ydDaoUtils = new JPlateYdDaoUtils();

	//
	// String type 형식에 맞춘 now 값 return
	// y:년, M:월, d:날, E:요일, a:오전/오후,
	// H:시, m:분, s:초, S:밀리초
	//

	public static String getCurDate(String type){
		SimpleDateFormat simpledateformat = new SimpleDateFormat(type);
        Date date = new Date();
        return simpledateformat.format(date);

	} // end of getCurDate()


	/**
	 * 현재 시점을 기준으로 계상일자를 구하는 메소드
	 * @return String
	 */
	public static String getDefaultHdsDate() {

		Calendar cal	= Calendar.getInstance();

		cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - 6);

		Date	date	= cal.getTime();

		SimpleDateFormat sdf	= new SimpleDateFormat("yyyyMMdd");

		String sDate	= sdf.format(date);

		return sDate;
	}


	/**
	 * 현재 시점을 기준으로 작업근을 구하는 메소드
	 * @return String
	 */
	public static String getDefaultDuty() {

	/*
	 *  CASE WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '000000' AND '065959' THEN '3'
             WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '070000' AND '145959' THEN '1'
             WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '150000' AND '225959' THEN '2'
             WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '230000' AND '235959' THEN '3'
     	END                                            -- 야드작업근
	 */

		String	sDuty = "";
		int 	iTime = Integer.parseInt(JPlateYdUtils.getCurDate("HHmmss"));

		if (iTime >= 0 && iTime <= 65959) {
			sDuty = "3";
		} else if (iTime >=  70000 && iTime <= 145959) {
			sDuty = "1";
		} else if (iTime >= 150000 && iTime <= 225959) {
			sDuty = "2";
		} else if (iTime >= 230000 && iTime <= 235959) {
			sDuty = "3";
		}

		return sDuty;
	}

	/**
	 * 현재 시점을 기준으로 계상일자를 구하는 메소드(7시기준)
	 * @return String
	 */
	public static String getDefaultHdsDate7() {

		Calendar cal	= Calendar.getInstance();

		cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - 7);

		Date	date	= cal.getTime();

		SimpleDateFormat sdf	= new SimpleDateFormat("yyyyMMdd");

		String sDate	= sdf.format(date);

		return sDate;
	}

	/**
	 * jSpeed공통코드 코드값에서 명칭 뽑아내기
	*/
    public static String jSpeedCommonCodeToName(String[][] arr, String code) {
        int arrSize = arr[0].length;

        for(int ii=0 ; ii<arrSize ; ii++) {
            if(code.equals(arr[0][ii])) {
                 return arr[1][ii];
            }
        }

        return code;
    }

	/**
	 * 오퍼레이션명 : Get TC Code
	 *
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public String getTcCode(JDTORecord inRecord){


		String szMsg="";
		String szMethodName="getTcCode";
		String szRcvTcCode="";

		try{
			// 내부인터페이스(JMS Queue)
			szRcvTcCode = inRecord.getFieldString("JMS_TC_CD");

			// 외부인터페이스(L2 EAI)
			if(szRcvTcCode == null){
				szRcvTcCode=inRecord.getFieldString("MSG_ID");
			}

			// 외부인터페이스(RemoteEAI)
			if(szRcvTcCode == null){
				szRcvTcCode=inRecord.getFieldString("TC_CODE");

			}

			if(szRcvTcCode == null){
				szRcvTcCode="";

			}	// end if

			szRcvTcCode=szRcvTcCode.trim();
			szRcvTcCode=szRcvTcCode.toUpperCase();

		}catch(Exception e){
			szMsg=szMethodName+" Exception Error : "+e.getMessage();
			this.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);

			return null;
		} // end of try-catch

		return szRcvTcCode;


	} // end of getTcCode();

	/**
	 * 오퍼레이션명 : putLog
	 *
	 * @param String szClassName	// Logging 요청 Class name
	 *        String szMethodName 	// Logging 요청 Method Name
	 *        String szLogMsg		// Logging Message
	 * @return
	 * @throws DAOException, JDTOException
	 */
	public void putLog(String szClassName, String szMethodName, String szLogMsg, int nLogLevel)  {

		String szMsg = "";
//		String strCurDate = JPlateYdUtils.getCurDate("yyyy-MM-dd HH:mm:ss:SSS");

		szMsg =szClassName + "::" + szMethodName +"() " + "\n\t"+szLogMsg;

		try{

			if(bDebugFlag){

				switch(nLogLevel){

				case 1:
					szMsg="[ERROR] "+szMsg;
					break;

				case 2:
					szMsg="[WARNING] "+szMsg;
					break;

				case 3:
					szMsg="[INFO] "+szMsg;
					break;

				default:
					szMsg="[DEBUG] "+szMsg;
					break;

				} // end of switch(nLogLevel)

				// 2012-11-14 (김현우) 표준화 지적사항 반영으로 주석처리
				//System.out.println("\n---<"+ strCurDate +">-----------------------------------");
				//System.out.println(szMsg);

			} else {

				// Message Logging
				switch(nLogLevel){
				case 1:
					//logger.println(LogLevel.ERROR, this, szMsg);
					logger.println(LogLevel.DEBUG, this, szMsg);
					break;

				case 2:
					logger.println(LogLevel.WARNING, this, szMsg);
					break;

				case 3:
					logger.println(LogLevel.INFO, this, szMsg);
					break;

				default:
					logger.println(LogLevel.DEBUG, this, szMsg);
				break;


				} // end of switch(nLogLevel)

			} // end of if(bDebugFlag)


		}catch (Exception e){

			szMsg =szClassName + "::" + szMethodName +"() " + "\n\t"+szMethodName+" Exception Error : "+e.getLocalizedMessage();

			// 2012-11-14 (김현우) 표준화 지적사항 반영으로 주석처리
			//if(bDebugFlag)
			//	System.out.println(szMsg);
			//else
				logger.println(LogLevel.ERROR, this, szMsg);

		} // end of try-catch()

	} // end of putLog();

	/**
	 *      [A] 오퍼레이션명 : fillSpZr
	 *
	 * @param String szData			// 변환대상 문자열
	 *        int    nLen 			// 변환 후 목적 문자열 길이
	 *        int    nChgMd			// 변환 방식 (0: 숫자열변환, 9:숫자열변환(0일때는 space) , !0: 문자열변환
	 * @return String 				// 변환 완료 된 문자열
	 * @throws
	 */
	public static String fillSpZr(String szData, int nLen, int nChgMd){

		String 	szFillData 	= "";
		int 	ii 			= 0;
		int 	nDataLen 	= 0;

		try {
			szFillData = szData.trim();
			nDataLen   = (szFillData==null) ? 0 : szFillData.length();
			if (nDataLen >= nLen) {
				return szFillData.substring(0, nLen);
			}

			// 모드가 9이고 데이타가 0일때 공백으로 편집
			if (nChgMd==9 && (nDataLen == 0 || "0".equals(szFillData))) {
				szFillData = "";
				for(ii=0; ii<nLen; ii++){
					szFillData += " ";
				} // end of for()

			} else {

				for(ii=nDataLen; ii<nLen; ii++){
					if (nChgMd==0 || nChgMd==9) {
						szFillData = "0"+szFillData;
					} else {
						szFillData += " ";
					}
				} // end of for()
			}

		}catch(Exception e){
			for(ii=0; ii<nLen; ii++){
				if (nChgMd==0 || nChgMd==9) {
					szFillData = "0"+szFillData;
				} else {
					szFillData += " ";
				}
			} // end of for();

		} // end of try-catch

		return szFillData;

	} // end of fillSpZr()


	/**
	 *      [A] 오퍼레이션명 : fillSpZrRec
	 *
	 * @param String szData			// 변환대상 문자열
	 *        int    nLen 			// 변환 후 목적 문자열 길이
	 *        int    nChgMd			// 변환 방식 (0: 숫자열변환, 9:숫자열변환(0일때는 space) , !0: 문자열변환
	 * @return String 				// 변환 완료 된 문자열
	 * @throws
	 */
	public static String fillSpZrRec(JDTORecord jdtoRec, String szData, int nLen, int nChgMd){

		String 	szFillData		= "";
		String	szRtnStr 		= "";
//		String 	szMethodName	= "fillSpZrRec";

    	try {
    		if (jdtoRec == null) {
    			szFillData = (szData==null) ? "" : szData;
    		} else {
    			szFillData = ydDaoUtils.paraRecChkNull(jdtoRec, szData);
    		}
    		szRtnStr = fillSpZr(szFillData, nLen, nChgMd);

    	} catch(Exception e) {
    	//	String szMsg = "<fillSpZrRec Exception> :: "+ e.getLocalizedMessage();
		//	putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
			szRtnStr = "";
    	}

		return szRtnStr;

	} // end of fillSpZrRec()

	/**
	 * 오퍼레이션명 : RuleData to JDTORecord Converter
	 *
	 * @param 	String szRuleName	// Rule Name
	 * @param	String szItems[]	// Rule Item List
	 * @param	Hashtable htRule	// Source Hashtable
	 * @param	JDTORecord jdtoRec	// Target JDTORecrd
	 * @return  Converted JDTORecord
	 * @throws 	JDTOException
	 */
	public boolean cvtTblToRec(String szRuleName, String szItems[],
			Hashtable htRule, JDTORecord jdtoRec, String szClassName) {

		String szMsg="";
		String szMethodName="cvtTblToRec";

		JPlateYdUtils ydUtils = new JPlateYdUtils();

		ResultData rData= (ResultData) htRule.get(szRuleName);
		try{

			for (int i = 0 ; i < rData.getColumnCount(); i++) {

				if (i > szItems.length ) {
					jdtoRec.setField(i + "" , rData.get(0, i));
				} else {
					jdtoRec.setField(szItems[i], rData.get(0, i));
				}
			}

		}catch (Exception je){

			szMsg= szMethodName+  " Exception Error : "+ je.getLocalizedMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.ERROR);
			return false;

		}// end of try


		//
		// Debugging 용
		//
		szMsg="Rule Query Successfully";
		ydUtils.putLog(szClassName, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return true;

	} // end of cvtTblToRec()



	/**
	 * Facade별 수신 메시지 분석 처리
	 *
	 * @param inRecord
	 * @return: true/false
	 * @throws DAOException
	 */
	public boolean rcvMsgChk(JDTORecord inRecord,
			String szSessionName, String szMethodName) throws DAOException {

		//
		// Facade에서 수신 한 메시지에 대한 정합성 Check
		//

		JPlateYdTcConst ydTcConst = new JPlateYdTcConst();

		int nRtc = 0;

		String szMsg 		= "";
		String szRcvTcCode	= "";
		String szTcUniqId	= "";

		try {

			//
			// 수신 메시지의 인터페이스 Unique ID Check
			szTcUniqId = inRecord.getFieldString("UNIQUE_ID");
			if (szTcUniqId == null) {
				szTcUniqId = "";
			}

			//
			// 수신메시지의 TC 유효성 검사
			//
			szRcvTcCode = this.getTcCode(inRecord);
			if (szRcvTcCode==null) {
				szMsg = "["+szTcUniqId+"] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
				this.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
				return false;
			}

			//
			// 수신 메시지 로깅
			//
			szMsg = "["+szTcUniqId+"] 전문수신 : TCCODE=" +szRcvTcCode;
			putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// 수신 Tc Check
			nRtc = ydTcConst.chkTcType(szRcvTcCode);

			switch(nRtc){

				case 1:

					// 내부 인터페이스 TC 수신
					szMsg="내부인터페이스 TC 수신 : " + szRcvTcCode;
					this.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);
					break;

				case 2:

					// 리모트 인터페이스 TC 수신
					szMsg="리모트인터페이스 TC 수신 : " + szRcvTcCode;
					this.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);
					break;

				case 3:

					// L2 인터페이스 TC 수신
					szMsg="L2 인터페이스 TC 수신 : " + szRcvTcCode;
					this.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);
					break;

				default:

					// Unknown TC 수신
					szMsg="Unknown TC Error : " + szRcvTcCode + " ErrCode="+nRtc;
					this.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);

					return false;

			} // end of switch()


			//
			// TC Code vs Method Check
			//
			if( !(ydTcConst.chkTcMethod(szRcvTcCode, szMethodName))) {
				szMsg = "Unknown TC Method TCCode="+szRcvTcCode+" MethodName="+szMethodName;
				this.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
				return false;

			} // end of if()


		} catch (Exception e) {
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			this.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);

			return false;
		}

		return true;

	} // end of rcvMsgChk()



	/**
	 * JDTORecord의 Key값을 지정 배열로 리턴한다.
	 *
	 * @param inRecord
	 * @return
	 */
	public String[] getRecKey(JDTORecord inRecord) {

		int nRecCnt=-1;

		Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
		nRecCnt = objTemp.length;
		String[] szaKeys =new String[nRecCnt];
		for(int i=0; i<nRecCnt; i++) {
			szaKeys[i] = objTemp[i].toString();
		}
		return szaKeys;

	} // end addFiller()



	/**
	 * JDTORecord의 Key 갯수 리턴
	 *
	 * @param fillerSize
	 * @param inRecord
	 * @return
	 */
	public int getRecKeyCnt(JDTORecord inRecord) {

		int nKeyCnt=-1;
		String [] szaRecKeys =null;

		try{
			szaRecKeys=this.getRecKey(inRecord);
			nKeyCnt =szaRecKeys.length;

		}catch(Exception e){
			return -1;
		}

		return nKeyCnt;

	} // end addFiller()



	/**
	 * JDTORecord의 지정 Key값를 삭제한다.
	 *
	 * @param fillerSize
	 * @param inRecord
	 * @return
	 */
	public JDTORecord delRecKey(JDTORecord inRecord, String szKey) {

		String szMsg="";
		String szMethodName ="delRecKey";

		JDTORecord outRecord =JDTORecordFactory.getInstance().create();

		int nRecCnt=-1;

		Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
		nRecCnt =objTemp.length;
		String szRecKey="";
		String szValue="";

		for(int i=0; i<nRecCnt; i++){
			try {
				szRecKey =objTemp[i].toString();
				szValue =inRecord.getFieldString(szRecKey);

				if(szKey.equals(szRecKey)) {
					continue;
				}

				outRecord.setField(szRecKey, szValue);

			} catch (JDTOException e) {
				szMsg="Exception Error : "+e.getLocalizedMessage();
				this.putLog(szMsg, szSessionName, szMethodName, 1);

				return null;
			}

		} // end of for()

		return outRecord;

	} // end addFiller()



	/**
	 * JDTORecord의 내용을 Key값=데이터값의 형식으로 표시한다.
	 *
	 * @param fillerSize
	 * @param inRecord
	 * @return
	 */
	public int disyRec(JDTORecord inRecord)	{
		int nRecCnt=-1;

		Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
		nRecCnt =objTemp.length;
		String szRecKey="";
		String szValue="";
		String szMsg="";

		for(int i=0; i<nRecCnt; i++){
			szRecKey =objTemp[i].toString();
			szValue =inRecord.getFieldString(szRecKey);

			if(szValue==null) {
				szValue="(null)";
			}
			szMsg = "["+(i+1)+"]"
			      + "\t"+szRecKey
			      + "\t["+szValue+"]";
			//System.out.println(szMsg);
			logger.println(LogLevel.INFO, this, szMsg);

		} // end of for()

		return nRecCnt;

	} // end addFiller()



	/**
	 * JDTORecord의 내용 중 키값의 데이터들을 문자열로 리턴한다.
	 *
	 * @param fillerSize
	 * @param inRecord
	 * @return
	 */
	public String makeRec2Str(JDTORecord inRecord)	{


		Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
		int nItemCnt =objTemp.length;

		String szItemKey="";
		String szValue="";
		String szRecMsg="";
//		String szMsg="";

		for(int i=0; i<nItemCnt; i++){
			szItemKey =objTemp[i].toString();
			szValue =inRecord.getFieldString(szItemKey);

			if(szValue==null) {
				szValue="";
			}
			szRecMsg+=szValue;

			logger.println(LogLevel.DEBUG, this, szRecMsg);

		} // end of for()

		return szRecMsg;

	} // end makeRec2Str()



	/**
	 * JDTORecordSet을 JDTORecord형으로 변환
	 *
	 * @param fillerSize
	 * @param inRecord
	 * @return
	 */
	public int chgRecSet2Rec(JDTORecordSet inRecSet, JDTORecord outRec)	{

		String szMsg="";
		String szMethodName="chgRecSet2Rec";

		int nRecCnt =0;

		try{

			if( nRecCnt <=0) {
				return -1;
			}

			if( !inRecSet.isFirst()) {
				inRecSet.first();
			}

			for(int i=1;i<=nRecCnt;i++){

				outRec.setField(""+i, inRecSet.getRecord());
				inRecSet.next();

			} // end of for()


		} catch(Exception e) {
			szMsg=szMethodName+" Exfeption Error : "+ e.getLocalizedMessage();
			this.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);

			return -1;
		}

		return nRecCnt;


	} // end makeRec2Str()

	/**
	 * 문자열 좌측을 지정한 값으로 채워넣음
	 * 권오창
	 *
	 * @param str
	 * @param len
	 * @param pad
	 * @return
	 * @throws Exception
	 */
	public String addLeftStr(String str, int len, char pad) throws Exception
	{
		String szMethodName = "addLeftStr";
		String result = "";
		int templen = 0;

		try {
			if (str == null || str.length() < 1) {
				templen = len;
			} else {
				templen = len - str.getBytes().length;
			}
			if(templen >= 0){
				for(int i=0; i<templen; i++) {
					str = pad + str;
				}
				result = str;
			} else {
				result = this.substr(str, 0, len);
			}
		}catch(Exception e){
			this.putLog(szSessionName, szMethodName, "floatLRPAD() : " + e.toString() + " : " + e.getMessage(), 4);
		}

		return result;
	}

	/**
	 * 문자열 우측을 지정한 값으로 채워넣음
	 *
	 * @param str
	 * @param len
	 * @param pad
	 * @return
	 * @throws Exception
	 */
	public String addRightStr(String str, int len, char pad) throws Exception
	{
		String szMethodName = "addRightStr";
		String result = "";
		int templen = 0;

		try{
			if (str == null || str.length() <= 0) {
				templen = len;
			} else {
				templen = len - str.getBytes().length;
			}
			if(templen >= 0){
				for(int i=0; i<templen; i++) {
					str = str + pad;
				}
				result = str;
			} else {
				result = this.substr(str, 0, len);
			}
		} catch(Exception e) {
		//	this.putLog(szSessionName, szMethodName, "addRightStr() : " + e.toString() + " : " + e.getMessage(), 4);
			this.putLog(szSessionName, szMethodName, "Exception 발생", 4);
		}

		return result;
	}

	/**
	 * 실수 문자열값 좌우측을 채워넣음
	 * 권오창
	 *
	 * @param strOrg
	 * @param nTotal
	 * @param nFloat
	 * @return
	 * @throws Exception
	 */
	public String floatLRPAD(String strOrg, int nTotal, int nFloat, char ch) throws Exception
	{
		String 	szMethodName 	= "floatLRPAD";
		String 	strTemp1 	 	= "";
		String 	strTemp2 		= "";
		String	rtnStr			= "";
		int nJisu = nTotal - nFloat;
		int nSosu = nFloat;

		try {
			if(strOrg == null || "".equals(strOrg.trim())) {
				rtnStr = addLeftStr("", nTotal, (char)ch);
			} else {
				int nIdx = strOrg.indexOf(".");
				if(nIdx <= 0){
					strTemp1 = this.addLeftStr(strOrg.trim(), nJisu, (char)ch);
					strTemp2 = this.addRightStr("0", nSosu, (char)ch);
				} else {
					String[] strSplit = strOrg.trim().split("\\.");

					strTemp1 = this.addLeftStr(strSplit[0], nJisu, (char)ch);
					strTemp2 = this.addRightStr(strSplit[1], nSosu, (char)ch);
				}
				rtnStr = strTemp1 + strTemp2;
			}
		} catch(Exception e) {
			this.putLog(szSessionName, szMethodName, "Exception 발생", 4);
		}

		if (rtnStr == null || rtnStr.length() < 0) {
		//	rtnStr = addLeftStr("", nTotal, ch);
			rtnStr = addLeftStr("", nTotal, '0');
		} else {
			if (rtnStr.length() > nTotal) {
				rtnStr = this.substr(rtnStr, 0, nTotal);
			}
		}

		return rtnStr;
	}

	/**
	 * JDTORecord의 내용을 Key값=데이터값의 형식으로 표시한다.
	 *  2009.05.19 권오창
	 * @param inRecord
	 * @return
	 */
	public int disyRecInfo(JDTORecord inRecord)	{
		int nRecCnt=-1;

		Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
		nRecCnt = objTemp.length;
		String szRecKey="";
		String szValue="";
		String szMsg="";

		for(int i=0; i<nRecCnt; i++){
			szRecKey =objTemp[i].toString();
			szValue =inRecord.getFieldString(szRecKey);

			if (szValue == null) {
				szValue="(null)";
			}

			szMsg = "["+(i+1)+"]" + "\t"+szRecKey + "\t["+szValue+"]";
			logger.println(LogLevel.INFO, this, szMsg);
		} // end of for()

		return nRecCnt;
	}

	/**
	 * JDTORecord의 내용을 Key값=데이터값의 형식으로 표시한다.
	 *  2009.05.19 권오창
	 * @param inRecord
	 * @return
	 */
	public int displayRecord(String szOperationName, JDTORecord inRecord)	{
		/*
		 * 2010.04.14 윤재광 : 로그정보가 너무많아 임시처리
		 */
		int nRecCnt = 0;
/*
		Object [] objTemp = ((JDTORecordImplMap)inRecord).getFieldNames();
		nRecCnt = objTemp.length;
		String 	szRecKey= "";
		String 	szValue	= "";
		String 	szMsg	= "";
		int		iMsgLen = 0;
		int		iSumLen	= 0;

		for(int ii=0; ii<nRecCnt; ii++){
			szRecKey = objTemp[ii].toString();
			szValue  = inRecord.getFieldString(szRecKey);

			if (szValue == null) {
				szValue = "(null)";
			}
			if ("MSG_LEN".equals(szRecKey)) {
				iMsgLen = Integer.parseInt(szValue);
			}

			szMsg = szOperationName + " ["+(ii+1)+"]\t(" + szValue.getBytes().length + ")\t" + szRecKey + " = ["+szValue+"]";
			logger.println(LogLevel.DEBUG, this, szMsg);

			iSumLen = iSumLen + szValue.getBytes().length;
		} // end of for()

		if (iMsgLen > 0) {
			if ((iMsgLen+60) == iSumLen) {
				szMsg = szOperationName + " >>>> 전문길이 정상 [ HEADER :: 60, MSG_LEN :: " + iMsgLen + ", 실제길이 :: " + iSumLen + "]";
				logger.println(LogLevel.DEBUG, this, szMsg);
			} else {
				szMsg = szOperationName + " >>>> 전문길이 오류 [ HEADER :: 60, MSG_LEN :: " + iMsgLen + ", 실제길이 :: " + iSumLen + ", 차이 :: " +(iMsgLen+60-iSumLen)+ "]";
				logger.println(LogLevel.DEBUG, this, szMsg);
			}
		}
*/
		return nRecCnt;
	}

	/**
	 * 문자열에 한글이 포함이 되어 있는지 검사하는 메서드
	 *
	 * 2009.08.06 권오창
	 * @param szData
	 * @return boolean
	 */
	public boolean isIncludeHangul(String szData){
		String szFillData = "";
		boolean bResult  = false;

		szFillData = szData.trim();

		for(int i=0; i<szFillData.length(); i++){
			if(Character.getType(szFillData.charAt(i)) == 5) {
				bResult = true;
			}
		}

		return bResult;
	}

	/**
	 * 숫자표시형 문자열데이터값을 증가치만큼 증가 시키고 공백은 자릿수 만큼 '0'으로 채워넣음
	 * 2009.11.06 권오창
	 *
	 * @param  수치표시형 문자열버퍼, 증가치, 자릿수
	 * @return 증가된 수치표시형 문자열
	 * @throws Exception
	 */
	public String increaseStrToInt(String strTemp, int nIncreaseCnt, int nDigit) throws Exception{
		String szBufferObj = "";
		int nTemp          = 0;

		nTemp = Integer.parseInt(strTemp);
		nTemp += nIncreaseCnt;

		szBufferObj = this.addLeftStr("" + nTemp, nDigit, '0');
		return szBufferObj;
	}

	/**
	 * 오퍼레이션명 : putLogMsg
	 *
	 * @param String szYdGp           // 야드구분
	 *         String desti            // Monitoring Channel
	 *         String szLogMsg         // Logging Message
	 *         String szYdBayGp        //  야드동구분
	 *         String szYdEqpId        //  설비 ID
	 *         String szYdSchCd        // 스케줄 코드
	 *         String szYdEvtGp        // 야드 Event 구분 (C:크레인, Q:설비, E:에러, W:경고, I:정보, Z:기타)
	 *         String szYdMsgOutpwrGrd // Message출력등급(A~E 5단계)
	 *         String szYdPgmTp        //  야드프로그램유형 (W:화면, S:스케줄, I:인터페이스)
	 *         String szYdIfCd         //  야드 인터페이스 코드(TC CODE)
	 *         String szEJBId	       // Logging 요청 Class name
	 *         String szMsgName 	   // Logging 요청 Method Name
	 * @return
	 * @throws DAOException, JDTOException
	 */
	public void putLogMsg(String szYdGp, String desti, String szLogMsg, String szYdBayGp, String  szYdEqpId, String szYdSchCd, String szYdEvtGp, String szYdMsgOutpwrGrd, String szYdPgmTp, String szYdIfCd, String szEJBId, String szMsgName)  {
		/*
		 * 야드 LOG 정보 막음. 2010.08.01 윤재광.
		 *
		YdMsgInfoMgtDao ydMsgInfoMgtDao = new YdMsgInfoMgtDao();

		JDTORecord inRec = null;
		String szMsg = "";
		int intRtnVal;
		String szMethodName = "putLogMsg";


		try {
			if ("".equals(desti)) {
				desti = JPlateYdConst.YD_MONITORING_CHANNEL;
			}

			if ("".equals(szYdGp)) {
				szMsg = "야드구분을 설정하지 않았습니다";
				logger.println(LogLevel.DEBUG, this, szMsg);
				//throw new DAOException(szMethodName + szMsg);
			}

			inRec = JDTORecordFactory.getInstance().create();
			inRec.setField("YD_GP", szYdGp);
			inRec.setField("MSG_CONTENTS", szLogMsg);
			inRec.setField("YD_BAY_GP", szYdBayGp);
			inRec.setField("YD_EQP_ID", szYdEqpId);
			inRec.setField("YD_SCH_CD", szYdSchCd);
			inRec.setField("YD_EVT_GP", szYdEvtGp);
			inRec.setField("YD_MSG_OUTPWR_GRD", szYdMsgOutpwrGrd);
			inRec.setField("YD_PGM_TP", szYdPgmTp);
			inRec.setField("YD_IF_CD", szYdIfCd);
			inRec.setField("YD_E_J_B_ID", szEJBId);
			inRec.setField("YD_MSG_NM", szMsgName);

			intRtnVal = ydMsgInfoMgtDao.insYdMsginfomgt(inRec);

			if(intRtnVal<=0) {
				szMsg = "Message정보관리에 대한 INSERT가 실패하였습니다.";
				logger.println(LogLevel.ERROR, this, szMsg);
				//throw new DAOException(szMethodName + szMsg);
			}

			putLogToMonitoring(desti, szEJBId, szMsgName, szLogMsg, szYdEvtGp ,szYdGp);
			return;

		} catch(Exception e) {
			szMsg =szSessionName + "::" + szMethodName +"() " + "\n\t"+szMethodName+" Exception Error : "+e.getLocalizedMessage();
			logger.println(LogLevel.ERROR, this, szMsg);
		}
		return;
		*/
	}


	/**
	 *  공백이나 0 채워넣는 함수에서 한글 2바이트 문제 처리  ㅡ.ㅡ
	 *  2009.07.01 권오창
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szData, nLen, nChgMd
	 * @return String
	 */
	public static String fillSpZrKor(String szData, int nLen, int nChgMd){

		String szFillData = "";
		int nDataLen      = 0;
		int nCntKOR       = 0;
		int nCntEXP       = 0;
		int i             = 0;

		try{
			szFillData = szData.trim();

			for(i=0; i<szFillData.length(); i++){
				if(Character.getType(szFillData.charAt(i)) == 5) {
					nCntKOR++;
				} else {
					nCntEXP++;
				}
			}

			nDataLen = szFillData.length() + nCntKOR;
			if(nDataLen > nLen) {
				return cutString(szFillData, nLen);
			}

			for(i=nDataLen; i<nLen; i++) {
				if(nChgMd == 0) {
					szFillData = "0" + szFillData;
				} else {
					szFillData += " ";
				}
			} // end of for()

		}catch(Exception e){
			for(i=0;i<nLen;i++){
				if(nChgMd==0) {
					szFillData = "0" + szFillData;
				} else {
					szFillData += " ";
				}
			} // end of for();

		} // end of try-catch

		return szFillData;
	}

	/**
	 *  (makeYDN7L007)다양한 특수문자로 인한 전문길이 수정을 위해 100자 뒤에 절삭
	 *  2020.07.15 박비오
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szData, nLen, nChgMd
	 * @return String
	 */
	public static String fillSpZrKorJYDY7L007(String szData, int nLen, int nChgMd){
		
		String szFillData = "";
		int nDataLen      = 0;
		int nCntKOR       = 0;
		int nCntEXP       = 0;
		int i             = 0;

		try{
			szFillData = szData.trim();

			for(i=0; i<szFillData.length(); i++){
				if(Character.getType(szFillData.charAt(i)) == 5) {
					nCntKOR++;
				} else {
					nCntEXP++;
				}
			}

			nDataLen = szFillData.length() + nCntKOR;
			if(nDataLen > nLen) {
				return cutString(szFillData, nLen);
			}

			for(i=nDataLen; i<nLen; i++) {
				if(nChgMd == 0) {
					szFillData = "0" + szFillData;
				} else {
					szFillData += " ";
				}
			} // end of for()

		}catch(Exception e){
			for(i=0;i<nLen;i++){
				if(nChgMd==0) {
					szFillData = "0" + szFillData;
				} else {
					szFillData += " ";
				}
			} // end of for();

		} // end of try-catch

		return cutString(szFillData, nLen); // (makeYDN7L007)다양한 특수문자로 인한 전문길이 수정을 위해 100자 뒤에 절삭
	}
	
	/**
     * 와이즈그리드 콤보 스크립트 생성(오버로딩)
     */
	public static String setWiseGridCombo(String hTitle, String[][] comboStrArr) {
		return setWiseGridCombo("GridObj", hTitle, comboStrArr, 1, "N");
	}

	/**
     * 와이즈그리드 콤보 스크립트 생성(오버로딩)
     */
	public static String setWiseGridCombo(String hTitle, String[][] comboStrArr, int cdVal) {
		return setWiseGridCombo("GridObj", hTitle, comboStrArr, cdVal, "N");
	}

	/**
     * 와이즈그리드 콤보 스크립트 생성(오버로딩)
     */
	public static String setWiseGridCombo(String hTitle, String[][] comboStrArr, String headTextYn) {
		return setWiseGridCombo("GridObj", hTitle, comboStrArr, 1, headTextYn);
	}

	/**
     * 와이즈그리드 콤보 스크립트 생성(오버로딩)
     */
	public static String setWiseGridCombo(String hTitle, String[][] comboStrArr, int cdVal, String headTextYn) {
		return setWiseGridCombo("GridObj", hTitle, comboStrArr, cdVal, headTextYn);
	}

	/**
     * 와이즈그리드 콤보 스크립트 생성(오버로딩)
     */
	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr) {
		return setWiseGridCombo(obj, hTitle, comboStrArr, 1, "N");
	}

	/**
     * 와이즈그리드 콤보 스크립트 생성(오버로딩)
     */
	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr, String headTextYn) {
		return setWiseGridCombo(obj, hTitle, comboStrArr, 1, headTextYn);
	}

	/**
     * 와이즈그리드 콤보 스크립트 생성(오버로딩)
     */
	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr, int cdVal) {
		return setWiseGridCombo(obj, hTitle, comboStrArr, cdVal, "N");
	}

	/**
     * 와이즈그리드 콤보 스크립트 생성
     * @param 	obj			: 와이즈그리드 Object
     * 			hTitle		: 와이즈그리드 헤더 TITLE
     * 			comboStrArr	: 와이즈그리드 콤보 Value, Text
     * 			cdVal		: 콤보에 보여줄 값이 Value, Text 결정(0:Value, 1:Text, 2:Value(Text))
     * 			headTextYn	: 빈칸 유무
     * @return 	와이즈그리드 콤보 자바스크립트
     */
	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr, int cdVal, String headTextYn) {
		String comboStr = "";

		if(comboStrArr != null) {

			if("Y".equals(headTextYn)) {
				comboStr = obj + ".AddComboListValue('" + hTitle + "', '', '');\n";
			}

			if("S".equals(headTextYn)) {
				comboStr = obj + ".AddComboListValue('" + hTitle + "', '선택', '');\n";
			}

			if(cdVal == 0 || cdVal == 1) {
				for(int ii=0; ii < comboStrArr[0].length; ii++) {
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + JPlateYdUtils.setEscapeStr(comboStrArr[cdVal][ii]) + "', '" + comboStrArr[0][ii] + "');\n";
				}
			} else {
				for(int ii=0; ii < comboStrArr[0].length; ii++) {
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" +
										comboStrArr[0][ii] + " (" + JPlateYdUtils.setEscapeStr(comboStrArr[1][ii]) + ")', '" + comboStrArr[0][ii] + "');\n";
				}
			}
		}

		return comboStr;
	}

	/**
	 * ' 일때 이스케이프 문자를 추가한다.
	 */
	public static String setEscapeStr(String str) {

		if(str.indexOf("\'") != -1) {
			str = str.replaceAll("'", "\\\\'");
		}

		return str;
	}

	/**
	 *  문자열에서  특정자리수 만큼 잘라오는 함수 (한글, 영문, 숫자 포함)
	 *  2009.07.01 권오창
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param str, nCutSize
	 * @return
	 */
	public static String cutString(String str, int nCutSize)
	{
		String[] StrSub = new String[30];
		byte[] bTemp = str.getBytes();

		int nCnt = 0;
		int str_nCnt = 0;

		while(bTemp.length > nCutSize)
		{
			nCnt = 0;
			for(int i=0; i<nCutSize; i++) {
				if (bTemp[i]<0) {
					nCnt++;
				}
			}

			if(nCnt%2 != 0){
				StrSub[str_nCnt] = new String(bTemp, 0, nCutSize+1);
				bTemp = new String(bTemp, nCutSize+1, bTemp.length-(nCutSize+1)).getBytes();
			} else {
				StrSub[str_nCnt] = new String(bTemp, 0, nCutSize);
				bTemp = new String(bTemp, nCutSize, bTemp.length-nCutSize).getBytes();
			}

			str_nCnt++;
		}

		StrSub[str_nCnt] = new String(bTemp);

		return StrSub[0];
	}

	//=====================================================================
	// 2009.12.09
	// 권오창
	//
	// 정수 or 실수값에 대해 3자리마다 콤마처리
	// ex) 12345678   => "12,345.678" (실수는 3자리까지 처리 늘어나면 포맷 변경)
	//     12345678   => "12,345,678"
	//     "1234567"  => "1,234,567"
	//     "12345.67" => "12,345.67"
	//=====================================================================
	public static String formatCommaStr(double dVal)
	{
		NumberFormat nf = new DecimalFormat(",###.###");
		return (String)nf.format(dVal);
	}

	public static String formatCommaStr(long lVal)
	{

		NumberFormat nf = new DecimalFormat("#,###");
		return (String)nf.format(lVal);
	}

	public static String formatCommaStr(String szVal)
	{
		if(szVal == null || "".equals(szVal.trim())){
			return "";
		}

		int nRet = szVal.trim().indexOf(".");
		String szRet = "";
		if(nRet != -1){
			szRet = formatCommaStr(Double.parseDouble(szVal.trim()));
		} else {
			szRet = formatCommaStr(Integer.parseInt(szVal.trim()));
		}

		return szRet;
	}


    /**
     * 오퍼레이션명 : 크레인스케줄 권상 및 권하위치 제원정보 등록
     *
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public static boolean updYdCrnschBedData(JDTORecord recCrnSch){

    	JPlateYdUtils     	ydUtils     = new JPlateYdUtils();
    	JPlateYdDaoUtils  	ydDaoUtils  = new JPlateYdDaoUtils();

    	JPlateYdCrnSchDAO 		ydCrnschDao 	= new JPlateYdCrnSchDAO();
    	JPlateYdStkBedDAO 		ydStkBedDao 	= new JPlateYdStkBedDAO();
    	JPlateYdStkLyrDAO 		ydStkLyrDao 	= new JPlateYdStkLyrDAO();
    	JPlateYdCrnWrkMtlDAO 	ydCrnWrkMtlDao 	= new JPlateYdCrnWrkMtlDAO();

    	String 	szMsg        			= "";
    	String 	szMethodName 			= "updYdCrnschBedData";
    	String 	szOperationName			= "크레인스케줄제원정보등록";
    	int 	intRtnVal = 0;

    	JDTORecord recGetStkBedData 	= null;
    	JDTORecord recUpdCrnSchData 	= null;
    	JDTORecord recInPara        	= null;
    	JDTORecord recUpPara        	= null;
    	JDTORecord recDnPara        	= null;
    	JDTORecord recUpStkBed      	= null;
    	JDTORecord recDnStkBed      	= null;
    	JDTORecord recResultCrnwrkmtl 	= null;

    	JDTORecordSet rsDnStkBed    	= null;
    	JDTORecordSet outRecSet     	= null;
    	JDTORecordSet rsUpStkBed    	= null;
    	JDTORecordSet rsGetStkLyrT  	= null;
    	JDTORecordSet rsResultCrnwrkmtl = null;

    	String 	szYD_GP					= null;
    	String 	szYD_UP_STK_COL_GP		= null;
    	String 	szYD_UP_STK_BED_NO		= null;
    	String 	szYD_UP_STK_LYR_NO		= null;

    	String 	szYD_DN_STK_COL_GP		= null;
    	String 	szYD_DN_STK_BED_NO		= null;
    	String 	szYD_DN_STK_LYR_NO		= null;

    	String 	szYD_CRN_SCH_ID			= null;
    	String 	szYD_EQP_ID				= null;
    	String	szMODIFIER				= null;

		String	szYD_UP_WO_LOC			= null;
		String	szYD_UP_WO_LAYER		= null;
		String	szYD_DN_WO_LOC			= null;
		String	szYD_DN_WO_LAYER		= null;
		String	szYD_TO_LOC_GUIDE		= null;

    	double 	dblSUM_MTL_T 			= 0;
    	String 	szYD_UP_WO_LOC_ZAXIS 	= null;
    	String 	szYD_DN_WO_LOC_ZAXIS 	= null;
    	String 	szSessionName 			= "ydUtils";

    	try {

			szMsg = "크레인스케줄 권상 및 권하위치 제원정보 등록 .... START >>>> " + recCrnSch.toString();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMODIFIER = ydDaoUtils.paraRecModifier(recCrnSch);

    		//1.크레인스케줄을 조회한다.
    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("yd");
    		intRtnVal = ydCrnschDao.getYdCrnSch(recCrnSch, outRecSet);			// intGp == 0

    		outRecSet.absolute(1);
    		recUpdCrnSchData = JDTORecordFactory.getInstance().create();
    		recUpdCrnSchData.setRecord(outRecSet.getRecord());

    		szYD_CRN_SCH_ID    	= ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_CRN_SCH_ID");
    		szYD_EQP_ID		   	= ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_EQP_ID");

    		szYD_UP_WO_LOC		= ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_UP_WO_LOC");
    		szYD_UP_WO_LAYER	= ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_UP_WO_LAYER");
    		szYD_UP_STK_COL_GP 	= ydUtils.substr(szYD_UP_WO_LOC, 0, 6);
    		szYD_UP_STK_BED_NO 	= ydUtils.substr(szYD_UP_WO_LOC, 6, 2);
    		szYD_UP_STK_LYR_NO 	= ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_UP_WO_LAYER");
    		szYD_TO_LOC_GUIDE	= ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_TO_LOC_GUIDE");

    		szYD_DN_WO_LOC		= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC");
    		szYD_DN_WO_LAYER	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LAYER");
    		szYD_DN_STK_COL_GP 	= ydUtils.substr(szYD_DN_WO_LOC, 0, 6);
    		szYD_DN_STK_BED_NO 	= ydUtils.substr(szYD_DN_WO_LOC, 6, 2);
    		szYD_DN_STK_LYR_NO 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LAYER");

    		szYD_GP = szYD_UP_STK_COL_GP.substring(0, 1);

    		//1.크레인 작업재료의 높이합을 구한다.
    		rsResultCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("");
        	intRtnVal = ydCrnWrkMtlDao.getSumMtlByYdCrnSchId(recCrnSch, rsResultCrnwrkmtl);		// intGp == 8

        	rsResultCrnwrkmtl.absolute(1);
        	recResultCrnwrkmtl = JDTORecordFactory.getInstance().create();
        	recResultCrnwrkmtl.setRecord(rsResultCrnwrkmtl.getRecord());
        	dblSUM_MTL_T = ydDaoUtils.paraRecChkNullDouble(recResultCrnwrkmtl, "SUM_MTL_T");

    		szMsg = "권상지시베드조회 전.. ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//권상지시베드조회
			recGetStkBedData = JDTORecordFactory.getInstance().create();
			rsUpStkBed = JDTORecordFactory.getInstance().createRecordSet("tempYd");
			recGetStkBedData.setField("YD_STK_COL_GP",  szYD_UP_STK_COL_GP);
			recGetStkBedData.setField("YD_STK_BED_NO",  szYD_UP_STK_BED_NO);
			intRtnVal = ydStkBedDao.getYdStkbed(recGetStkBedData, rsUpStkBed);			// intGp == 0
			if (intRtnVal <= 0) {
				szMsg="updYdCrnschBedData 권상지시 베드 정보조회 중 Error!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
				return false;
			}
			rsUpStkBed.absolute(1);
			recUpStkBed = JDTORecordFactory.getInstance().create();
			recUpStkBed.setRecord(rsUpStkBed.getRecord());

			szMsg = "권상지시베드조회 후.. ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, recUpStkBed);

			recUpPara = JDTORecordFactory.getInstance().create();
			rsGetStkLyrT = JDTORecordFactory.getInstance().createRecordSet("");
			recUpPara.setField("YD_STK_COL_GP", szYD_UP_STK_COL_GP);
			recUpPara.setField("YD_STK_BED_NO", szYD_UP_STK_BED_NO);
			recUpPara.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(szYD_UP_STK_LYR_NO, -1));
			intRtnVal = ydStkLyrDao.getStkLyrMtlSumT(recUpPara, rsGetStkLyrT);			// intGp == 71
			if (intRtnVal <= 0) {
				szYD_UP_WO_LOC_ZAXIS = "0";
			} else {
				rsGetStkLyrT.absolute(1);
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setRecord(rsGetStkLyrT.getRecord());
				szYD_UP_WO_LOC_ZAXIS = String.valueOf(ydDaoUtils.paraRecChkNullDouble(recInPara, "SUM_MTL_T"));

				int idx = szYD_UP_WO_LOC_ZAXIS.lastIndexOf(".");
				if( idx >= 0 ) {
					szYD_UP_WO_LOC_ZAXIS = szYD_UP_WO_LOC_ZAXIS.substring(0, idx);
				}
			}

			//권하지시베드조회
			rsDnStkBed = JDTORecordFactory.getInstance().createRecordSet("");
			recGetStkBedData = JDTORecordFactory.getInstance().create();
			recGetStkBedData.setField("YD_STK_COL_GP",  szYD_DN_STK_COL_GP);
			recGetStkBedData.setField("YD_STK_BED_NO",  szYD_DN_STK_BED_NO);
			intRtnVal = ydStkBedDao.getYdStkbed(recGetStkBedData, rsDnStkBed);			// intGp == 0
			if(intRtnVal <= 0){
				szMsg="updYdCrnschBedData 권하지시 베드 정보조회 중 Error!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
				return false;
			}
			rsDnStkBed.absolute(1);
			recDnStkBed = JDTORecordFactory.getInstance().create();
			recDnStkBed.setRecord(rsDnStkBed.getRecord());

			szMsg="권하지시베드조회 후.. ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, recDnStkBed);

			recDnPara = JDTORecordFactory.getInstance().create();
			rsGetStkLyrT = JDTORecordFactory.getInstance().createRecordSet("");
			recDnPara.setField("YD_STK_COL_GP", szYD_DN_STK_COL_GP);
			recDnPara.setField("YD_STK_BED_NO", szYD_DN_STK_BED_NO);
			recDnPara.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(szYD_DN_STK_LYR_NO, -1));
			intRtnVal = ydStkLyrDao.getStkLyrMtlSumT(recDnPara, rsGetStkLyrT);			// intGp == 71

			szMsg = "dblSUM_MTL_T :" + dblSUM_MTL_T;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "szYD_DN_WO_LOC_ZAXIS :" + szYD_DN_WO_LOC_ZAXIS;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if (intRtnVal <= 0) {
				szMsg = "================PASS1============================";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);
				//szYD_DN_WO_LOC_ZAXIS = ""+ (int)dblSUM_MTL_T;
				szYD_DN_WO_LOC_ZAXIS = String.valueOf(dblSUM_MTL_T);

			} else {
				rsGetStkLyrT.absolute(1);
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setRecord(rsGetStkLyrT.getRecord());

				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szMsg = "================PASS2============================";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);
				szYD_DN_WO_LOC_ZAXIS = String.valueOf(ydDaoUtils.paraRecChkNullDouble(recInPara, "SUM_MTL_T") + dblSUM_MTL_T);

				//szYD_DN_WO_LOC_ZAXIS =  ""+ (int)(ydDaoUtils.paraRecChkNullDouble(recInPara, "SUM_MTL_T")+dblSUM_MTL_T);
			}

			int idx = szYD_DN_WO_LOC_ZAXIS.lastIndexOf(".");
			if (idx >= 0 ) {
				szYD_DN_WO_LOC_ZAXIS = szYD_DN_WO_LOC_ZAXIS.substring(0, idx);
			}

			//-------------------------------------------------------------------------------------------------------------
			//	크레인 허용 오차 및 크레인 X, Y좌표 계산
			//-------------------------------------------------------------------------------------------------------------
			int intCRANE_GAP_UP_X		= JPlateYdConst.PLATE_CRANE_GAP_X;
			int intCRANE_GAP_UP_Y		= JPlateYdConst.PLATE_CRANE_GAP_Y;
			int intCRANE_GAP_UP_Z		= JPlateYdConst.PLATE_CRANE_GAP_Z;

			int intCRANE_GAP_DN_X		= JPlateYdConst.PLATE_CRANE_GAP_X;
			int intCRANE_GAP_DN_Y		= JPlateYdConst.PLATE_CRANE_GAP_Y;
			int intCRANE_GAP_DN_Z		= JPlateYdConst.PLATE_CRANE_GAP_Z;

			String szUP_GRAB_X_VALUE    = "";
			String szUP_GRAB_Y_VALUE    = "";
			String szUP_GRAB_Y1_VALUE   = "";
			String szUP_GRAB_Y2_VALUE   = "";

			String szDN_GRAB_X_VALUE    = "";
			String szDN_GRAB_Y_VALUE    = "";
			String szDN_GRAB_Y1_VALUE   = "";
			String szDN_GRAB_Y2_VALUE	= "";

			if( JPlateYdConst.YD_GP_F_PLATE_YARD.equals(szYD_GP)) {	 // 2후판정정야드

				//----------------------------------------------------------------
				//	권상 시 크레인 허용오차
				//----------------------------------------------------------------
				if ("PT".equals(szYD_UP_STK_COL_GP.substring(2, 4))) {
					intCRANE_GAP_UP_X = JPlateYdConst.PLATE_CRANE_PT_GAP_X;
					intCRANE_GAP_UP_Y = JPlateYdConst.PLATE_CRANE_PT_GAP_Y;
				} else {
					intCRANE_GAP_UP_X = JPlateYdConst.PLATE_CRANE_GAP_X;
					intCRANE_GAP_UP_Y = JPlateYdConst.PLATE_CRANE_GAP_Y;
				}
				intCRANE_GAP_UP_Z = JPlateYdConst.PLATE_CRANE_GAP_Z;

				//----------------------------------------------------------------
				//	권하 시 크레인 허용오차
				//----------------------------------------------------------------
				if ("PT".equals(szYD_DN_STK_COL_GP.substring(2, 4))) {
					intCRANE_GAP_DN_X = JPlateYdConst.PLATE_CRANE_PT_GAP_X;
					intCRANE_GAP_DN_Y = JPlateYdConst.PLATE_CRANE_PT_GAP_Y;
				} else {
					intCRANE_GAP_DN_X = JPlateYdConst.PLATE_CRANE_GAP_X;
					intCRANE_GAP_DN_Y = JPlateYdConst.PLATE_CRANE_GAP_Y;
				}
				intCRANE_GAP_DN_Z = JPlateYdConst.PLATE_CRANE_GAP_Z;

				szMsg	= "["+szOperationName+"] -------------------------- X,Y 좌표계산 시작 -------------------------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

				JDTORecord recPara = JDTORecordFactory.getInstance().create();
				JDTORecord recResult = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", 		szYD_CRN_SCH_ID);
				recPara.setField("YD_EQP_ID", 			szYD_EQP_ID);
				recPara.setField("YD_UP_STK_COL_GP", 	szYD_UP_STK_COL_GP);
				recPara.setField("YD_UP_STK_BED_NO", 	szYD_UP_STK_BED_NO);
				recPara.setField("YD_DN_STK_COL_GP", 	szYD_DN_STK_COL_GP);
				recPara.setField("YD_DN_STK_BED_NO", 	szYD_DN_STK_BED_NO);

				JPlateYdGdsUtil.procXYCalForPlateCrane(recPara, recResult);

				szUP_GRAB_X_VALUE   = ydDaoUtils.paraRecChkNull(recResult, "UP_GRAB_X_VALUE");
				szUP_GRAB_Y_VALUE   = ydDaoUtils.paraRecChkNull(recResult, "UP_GRAB_Y_VALUE");
				szUP_GRAB_Y1_VALUE  = ydDaoUtils.paraRecChkNull(recResult, "UP_GRAB_Y1_VALUE");
				szUP_GRAB_Y2_VALUE  = ydDaoUtils.paraRecChkNull(recResult, "UP_GRAB_Y2_VALUE");

				szDN_GRAB_X_VALUE   = ydDaoUtils.paraRecChkNull(recResult, "DN_GRAB_X_VALUE");
				szDN_GRAB_Y_VALUE   = ydDaoUtils.paraRecChkNull(recResult, "DN_GRAB_Y_VALUE");
				szDN_GRAB_Y1_VALUE  = ydDaoUtils.paraRecChkNull(recResult, "DN_GRAB_Y1_VALUE");
				szDN_GRAB_Y2_VALUE	= ydDaoUtils.paraRecChkNull(recResult, "DN_GRAB_Y2_VALUE");

				szMsg = "["+szOperationName+"] -------------------------- X,Y 좌표계산 완료 -------------------------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			// 2013.05.02 김현우 물리위치 보정 (차상국에서  UP_GRAB_Y1_VALUE , DN_GRAB_Y1_VALUE 좌표값을 사용)
			if ("".equals(szDN_GRAB_Y1_VALUE)) {
				szUP_GRAB_Y1_VALUE = szUP_GRAB_Y_VALUE;
				szDN_GRAB_Y1_VALUE = szDN_GRAB_Y_VALUE;
			}

			//-------------------------------------------------------------------------------------------------------------
			// 크레인 스케줄  권하지시위치 업데이트
			//-------------------------------------------------------------------------------------------------------------
			recUpdCrnSchData = JDTORecordFactory.getInstance().create();

			recUpdCrnSchData.setField("YD_CRN_SCH_ID",  			recResultCrnwrkmtl.getFieldString("YD_CRN_SCH_ID"));

			recUpdCrnSchData.setField("YD_EQP_WRK_SH",    			ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "SH_CNT"));
			recUpdCrnSchData.setField("YD_EQP_WRK_WT",    			ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "SUM_MTL_WT"));
			recUpdCrnSchData.setField("YD_EQP_WRK_T",     			ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "SUM_MTL_T"));
			recUpdCrnSchData.setField("YD_EQP_WRK_MAX_W", 			ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "MAX_MTL_W"));
			recUpdCrnSchData.setField("YD_EQP_WRK_MAX_L", 			ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "MAX_MTL_L"));

			recUpdCrnSchData.setField("YD_UP_WO_LOC_XAXIS",   		szUP_GRAB_X_VALUE);
			recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_UP_X));
			recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_UP_X));
			recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS",   		szUP_GRAB_Y_VALUE);
			recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS1",  		szUP_GRAB_Y1_VALUE);
			recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS2",  		szUP_GRAB_Y2_VALUE);
			recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_UP_Y));
			recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_UP_Y));
			recUpdCrnSchData.setField("YD_UP_WO_LOC_ZAXIS",  		szYD_UP_WO_LOC_ZAXIS);
			recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_UP_Z));
			recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_UP_Z));

			recUpdCrnSchData.setField("YD_DN_WO_LOC_XAXIS",   		szDN_GRAB_X_VALUE);
			recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_DN_X));
			recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_DN_X));
			recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS",   		szDN_GRAB_Y_VALUE);
			recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS1",  		szDN_GRAB_Y1_VALUE);
			recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS2",  		szDN_GRAB_Y2_VALUE);
			recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_DN_Y));
			recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_DN_Y));
			recUpdCrnSchData.setField("YD_DN_WO_LOC_ZAXIS",  		szYD_DN_WO_LOC_ZAXIS);
			recUpdCrnSchData.setField("YD_DN_WO_ZAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_DN_Z));
			recUpdCrnSchData.setField("YD_DN_WO_ZAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_DN_Z));

			recUpdCrnSchData.setField("MODIFIER",  					szMODIFIER);
			recUpdCrnSchData.setField("YD_UP_WO_LOC",				szYD_UP_WO_LOC);
			recUpdCrnSchData.setField("YD_UP_WO_LAYER",				szYD_UP_WO_LAYER);
			recUpdCrnSchData.setField("YD_DN_WO_LOC",				szYD_DN_WO_LOC);
			recUpdCrnSchData.setField("YD_DN_WO_LAYER",				szYD_DN_WO_LAYER);

			if ("".equals(szYD_TO_LOC_GUIDE)) {
				szYD_TO_LOC_GUIDE = szYD_DN_WO_LOC;
			}
			recUpdCrnSchData.setField("YD_TO_LOC_GUIDE",			szYD_TO_LOC_GUIDE);

			intRtnVal = ydCrnschDao.updEqpUpDnWoInfo(recUpdCrnSchData);		// 303
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="<updYdCrnschBedData> updYdCrnsch data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg,JPlateYdConst.WARNING);
    			} else if(intRtnVal == -1) {
    				szMsg="<updYdCrnschBedData> updYdCrnsch duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
    			} else if(intRtnVal == -2) {
    				szMsg="<updYdCrnschBedData> updYdCrnsch parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
    			} else if(intRtnVal == -3){
    				szMsg="<updYdCrnschBedData> updYdCrnsch execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
    			}
    			return false;
    		}

    	} catch(Exception e) {
			szMsg="<updYdCrnschBedData> Error : "+ e.getLocalizedMessage();
			ydUtils.putLog("YdUtils", szMethodName, szMsg, JPlateYdConst.ERROR);
			return false;
    	}
    	return true;
    }

    /**
	 *  이적 스케줄 코드를 생성
	 *  2009.11.12 이현성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param String pzYdGp , String pzYdBayGp , String pzYdEqpGp
	 * @return String (올바르게 만들어 지지 않았을경우 - 8자리가 아닐경우는 "" Retrun)
	 */
	public String getMakeSchCdMM(String pzYdGp , String pzYdBayGp , String pzYdEqpGp )	{

		String szMsg 			= "";
		String szMethodName		= "getMakeSchCdMM";
		String szOperationName	= " 이적 스케줄 코드를 생성";
		String szYdSchCd 		= "";
		String szYdEqpGp 		= "";

		szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
		putLog( szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

		szMsg = "[JSP Session : "+szOperationName+"] 입력받은 정보 (야드,동,스판) =  " + "(" + pzYdGp + ", " + pzYdBayGp + "," +  pzYdEqpGp+")";
		putLog( szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);

		if ("".equals(pzYdGp)){
			szMsg = "[JSP Session : "+szOperationName+"]야드구분이 올바르지 않습니다. ";
			putLog( szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
			return "";
		}

		if ("".equals(pzYdBayGp)){
			szMsg = "[JSP Session : "+szOperationName+"] 동 구분이 올바르지 않습니다. ";
			putLog( szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
			return "";
		}

		if ("".equals(pzYdEqpGp)){
			szMsg = "[JSP Session : "+szOperationName+"] 설비구분이 올바르지 않습니다. ";
			putLog( szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
			return "";
		}

		szYdEqpGp = pzYdEqpGp;

		//야드별로 추가 하고 싶을경우는 추가로직을 여기에 넣는다

		szYdSchCd =  pzYdGp + pzYdBayGp + "YD" + szYdEqpGp + "MM";

		//스케줄코드가 제대로 생성되지 않았을 경우
		if(szYdSchCd.length() != 8){
			szMsg = "[JSP Session : "+szOperationName+"] 스케줄 코드가 올바르게 생성되지 않았습니다.. ";
			putLog( szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
			return "";
		}

		return szYdSchCd;
	}

	//---------------------------------------------------------------------------
	/**
	 *      [A] 오퍼레이션명 : 문자열을 float로 변환
	 *          예) 123456 , 6, 3 -> 123.456
	 *
	 * @param String strData		// 변환대상 문자열 [소수점없는 숫자값]
	 *        int    nLen			// 전체자릿수
	 *        int    nDeci			// 소수부분
	 * @return float 				// 변환 완료 된 float값
	 * @throws
	*/
	// 2012-12-03  김현우 추가
	public float strToFloat(String strData, int nLen, int nDeci) {

		try {
			if (strData.length() != nLen) {
				nLen = strData.length();
			}
			int iData1  = Integer.parseInt(strData.substring(0, (nLen-nDeci)));
			int iData2  = Integer.parseInt(strData.substring(nLen-nDeci, nLen));
			String sRtnVal = Integer.toString(iData1) + "." + Integer.toString(iData2);
			return Float.valueOf(sRtnVal).floatValue();

		} catch (Exception e) {
			return Float.valueOf(strData).floatValue();
		}
	}

	//
	// 문자열 Null Check하여 substring 처리한다
	// 파라미터 : 문자열, start idx, length
	//
	// 2014-04-15  김현우 추가
	public String substr(String pStr, int iSta, int iLen){

		String rtnStr = "";
		if (pStr != null) {
		//	rtnStr = pStr;
			if (pStr.length() >= (iSta+iLen)) {
				rtnStr = pStr.substring(iSta, (iSta+iLen));
			} else {
				if (pStr.length() > iSta) {
					rtnStr = pStr.substring(iSta, pStr.length());
				}
			}
		}
		return rtnStr;
	} // end of substr

	//
	// 문자열 Null Check하여 substring 처리한다
	// 파라미터 : 문자열, start idx, end idx
	//
	// 2014-04-15  김현우 추가
	public String substring(String pStr, int iSta, int iEnd){

		String rtnStr = "";
		if (pStr != null && iEnd > iSta) {
		//	rtnStr = pStr;
			if (pStr.length() > iSta && pStr.length() >= iEnd) {
				rtnStr = pStr.substring(iSta, iEnd);
			} else {
				if (pStr.length() > iSta) {
					rtnStr = pStr.substring(iSta, pStr.length());
				}
			}
		}
		return rtnStr;
	} // end of substring

	//
	// 스케쥴코드가 Book-In인지 체크
	// 파라미터 : 스케쥴코드
	//
	public boolean isBookInSchCd(String pScnCd){

		boolean rtnFlag = false;

		// SCH_CD_JPLATE_BOOK_IN_A = "FART00UM";			//BOOK-IN  (A동)

		if (pScnCd != null && pScnCd.length() == 8) {

		//	if (JPlateYdConst.BOOK_IN_GP.equals(pScnCd.substring(6,7))) {
	    //		if ("RT".equals(pScnCd.substring(2,4)) ||		// RT
		//			"CN".equals(pScnCd.substring(2,4)) ||		// 가스장
		//			"BS".equals(pScnCd.substring(2,4)) ||		// 보수장
		//			"TD".equals(pScnCd.substring(2,4))) {		// TOD
		//
	    //			rtnFlag = true;
		//		}
	    //	}

			if ("U".equals(pScnCd.substring(6,7))) {
    			rtnFlag = true;
	    	}
		}

		return rtnFlag;
	} // end of isBookInSchCd

	//
	// 스케쥴코드가 Book-Out인지 체크
	// 파라미터 : 스케쥴코드
	//
	public boolean isBookOutSchCd(String pScnCd){

		boolean rtnFlag = false;

		// SCH_CD_JPLATE_BOOK_OUT_A = "FART0?LM";			//BOOK-OUT  (A동)

		if (pScnCd != null && pScnCd.length() == 8) {

		//	if (JPlateYdConst.BOOK_OUT_GP.equals(pScnCd.substring(6,7))) {
			if ("L".equals(pScnCd.substring(6,7))) {
    			rtnFlag = true;
	    	}
		}

		return rtnFlag;
	} // end of isBookOutSchCd

	//
	// RT 스케쥴코드 변환
	// 파라미터 : 저장위치, In/Out Flag (LM, UM)
	//
	public String getRtSchCd(String pYdStrLoc, String pSchFlag){

		String 	rtnSchCd = "";
		String 	sRtLoc	= this.substr(pYdStrLoc, 0, 6);
		String 	sBayGp	= this.substr(pYdStrLoc, 1, 1);

		if ("A".equals(sBayGp)) {
			if ("FART01".equals(sRtLoc) || "FART02".equals(sRtLoc) ||
				"FART03".equals(sRtLoc) || "FART04".equals(sRtLoc) || "FART05".equals(sRtLoc)) {
				rtnSchCd = "FART01" + pSchFlag;
			} else if("FART13".equals(sRtLoc)){
				rtnSchCd = "FART03" + pSchFlag; //2016.02.18 윤재광 입고분기 스케쥴코드 분리
			} else {
				rtnSchCd = "FART02" + pSchFlag;
			}
		} else if ("B".equals(sBayGp)) {
			if ("FBRT01".equals(sRtLoc) || "FBRT02".equals(sRtLoc)) {
				rtnSchCd = "FBRT01" + pSchFlag;
			} else if("FBRT03".equals(sRtLoc) || "FBRT04".equals(sRtLoc) || "FBRT05".equals(sRtLoc)) {
				rtnSchCd = "FBRT02" + pSchFlag;
			} else if("FBRT06".equals(sRtLoc) || "FBRT07".equals(sRtLoc) || "FBRT08".equals(sRtLoc) ||
					  "FBRT09".equals(sRtLoc) || 
					  "FBRT11".equals(sRtLoc) || "FBRT12".equals(sRtLoc)) {
				rtnSchCd = "FBRT03" + pSchFlag;
			} else if("FBRT10".equals(sRtLoc)) {
				rtnSchCd = "FBRT06" + pSchFlag;	
			} else if("FBRT13".equals(sRtLoc) || "FBRT14".equals(sRtLoc) || "FBRT15".equals(sRtLoc) ||
					  "FBRT16".equals(sRtLoc) || "FBRT17".equals(sRtLoc) || "FBRT18".equals(sRtLoc) ||
					  "FBRT19".equals(sRtLoc)) {
				rtnSchCd = "FBRT04" + pSchFlag;
			} else {
				rtnSchCd = "FBRT05" + pSchFlag;
			}
		} else if ("C".equals(sBayGp)) {
			rtnSchCd = "FCRT01" + pSchFlag;
		}

		return rtnSchCd;
	} // end of isBookOutSchCd

	//
	// 크레인작업지시 X축 허용오차 조회
	// 파라미터 : 저장위치
	//
	public int getCraneGapX(String pYdStkColGp){

		int iCraneGapX = JPlateYdConst.PLATE_CRANE_GAP_X;

		if ("PT".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapX = JPlateYdConst.PLATE_CRANE_PT_GAP_X;
		} else if ("BS".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapX = JPlateYdConst.PLATE_CRANE_BS_GAP_X;
		} else if ("CN".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapX = JPlateYdConst.PLATE_CRANE_CN_GAP_X;
		} else if ("TC".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapX = JPlateYdConst.PLATE_CRANE_TC_GAP_X;
		} else if ("TD".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapX = JPlateYdConst.PLATE_CRANE_TD_GAP_X;
		} else if (pYdStkColGp.startsWith("FART03")||pYdStkColGp.startsWith("FBRT06")||pYdStkColGp.startsWith("FBRT09")) {
			iCraneGapX = 20000;
		} else if (pYdStkColGp.startsWith("FBRT22")) {
			iCraneGapX = 50000;	
		} else if ("RT".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapX = JPlateYdConst.PLATE_CRANE_RT_GAP_X;
		} else if ("CB".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapX = JPlateYdConst.PLATE_CRANE_CB_GAP_X;
		} else {
			iCraneGapX = JPlateYdConst.PLATE_CRANE_GAP_X;
		}

		return iCraneGapX;
	} // end of CraneGapX

	//
	// 크레인작업지시 Y축 허용오차 조회
	// 파라미터 : 저장위치
	//
	public int getCraneGapY(String pYdStkColGp){

		int iCraneGapY = JPlateYdConst.PLATE_CRANE_GAP_Y;

		if ("PT".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_PT_GAP_Y;
		} else if ("BS".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_BS_GAP_Y;
		} else if ("CN".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_CN_GAP_Y;
		} else if ("TC".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_TC_GAP_Y;
		} else if ("TD".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_TD_GAP_Y;
		} else if ("RT".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_RT_GAP_Y;
		} else if ("CB".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_CB_GAP_Y;
		} else {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_GAP_Y;
		}

		return iCraneGapY;
	} // end of CraneGapY

	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//
	//                      일관제철소정보관리시스템-야드관리
	//              			Common Utility Class
	//                          2008.09.30 YHWHman
	//
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

	public static void main(String[] args){

		try {
			JPlateYdUtils im = new JPlateYdUtils();

			JDTORecord testRec =JDTORecordFactory.getInstance().create();
			String szOperationName = "일관제철소정보관리시스템 테스트";
//			String [] szaKeys= null;
//			int nRtc=0;

//		try {

			//
			// make JDTORecord
			testRec.setField("NAME", 		"김인홍");
			testRec.setField("JOB",  		"Computer Programmer/Architect");
			testRec.setField("PHONE", 		"010-6257-3209");
			testRec.setField("EMAIL", 		"yhwhman@gmail.com");
			testRec.setField("TEST1", 		null);
			//testRec.setField("JMS_TC_CD", "YDYDJ999");
			testRec.setField("JMS_TC_CD", 	null);

			//
			// Get Record Keys
//			szaKeys = im.getRecKey(testRec);
//			nRtc = szaKeys.length;

			//
			// Record Display
			im.displayRecord(szOperationName, testRec);

			//
			// Convert Data JDTORecord to String Stream
			im.makeRec2Str(testRec);

		} catch (Exception e) {
			// 2012-11-14 (김현우) 표준화 지적사항 반영으로 주석처리
			//System.out.println("Exception Error : "+e.getLocalizedMessage());
			//e.printStackTrace();
		} // end of try-catch

	} // end of testMain()

	
	/**********************************************************
	* 1후판정정추가 SJH16
	**********************************************************/	 	
	
	
	/**
	 * 길이 만큼 Char 추가
	 * @param len 추가할 길이
	 * @param chr 추가 Char
	 * @return 가공하여 Return
	 */
	public String addStr(int len, String chr) {
		StringBuffer sb = new StringBuffer();

		for (int ii = 0; ii < len; ii++) {
			sb = sb.append(chr);
		}

		return substr(sb.toString(), 0, len);
	}
	
	public String formatMaxNo(int no, int maxNo) {
		DecimalFormat df = new DecimalFormat(addStr((String.valueOf(maxNo)).length(), "0"));
		return df.format(no);
	}
	
	/**
	 * JDTORecord의 내용을 Key값=데이터값의 형식으로 표시한다.
	 * 일시 1후판 정정 TEST
	 * @param inRecord
	 * @return
	 */
	/**
	 *      [A] 오퍼레이션명 : Parameter Logging
	 *
	 *      @param String paramNm
	 *      @param Object obj
	 *      @return void
	*/
	public void printParam(String paramNm, Object obj) {
		if (obj == null) { return; }

		try {
			int pLen1 = 0;
			int pLen2 = 0;
			StringBuffer sb = new StringBuffer("\n▩▩ " + paramNm + " ▩▩");

			if (obj instanceof JDTORecord) {
				JDTORecord param = (JDTORecord)obj;
				Iterator itr = param.iterateName();

				String key = "";
				StringBuffer sb1 = new StringBuffer();
				StringBuffer sb2 = new StringBuffer();
				while (itr.hasNext()) {
					key = (String)itr.next();
					Object obj2 = param.getField(key);

					if (obj2 instanceof JDTORecord) {
						JDTORecord param2 = (JDTORecord)obj2;
						sb2 = sb2.append("\n▩ " + key + " : " + param2.toString());
					} else if (obj2 instanceof JDTORecordSet) {
						JDTORecordSet param2 = (JDTORecordSet)obj2;
						pLen2 = param2.size();
						for (int jj = 0; jj < pLen2; jj++) {
							sb2 = sb2.append("\n▩ " + key + "-" + formatMaxNo(jj, pLen2) + " : " + param2.getRecord(jj).toString());
						}
					} else {
						if (sb1.length() > 0) { sb1 = sb1.append(", "); }
						sb1 = sb1.append(key + "=" + obj2.toString());
					}
				}

				if (sb1.length() > 0) {
					sb = sb.append("\n▩ {");
					sb = sb.append(sb1);
					sb = sb.append("}");
				}
				
				if (sb2.length() > 0) {
					sb = sb.append(sb2);
				}
			} else if (obj instanceof JDTORecord[]) {
				JDTORecord[] param = (JDTORecord[])obj;
				pLen1 = param.length;
				for (int ii = 0; ii < pLen1; ii++) {
					sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : " + param[ii].toString());
				}
			} else if (obj instanceof JDTORecordSet) {
				JDTORecordSet param = (JDTORecordSet)obj;
				pLen1 = param.size();
				for (int ii = 0; ii < pLen1; ii++) {
					sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : " + param.getRecord(ii).toString());
				}
			} else if (obj instanceof Vector) {
				Vector param = (Vector)obj;
				pLen1 = param.size();
				for (int ii = 0; ii < pLen1; ii++) {
					Object obj2 = param.get(ii);
					if (obj2 instanceof JDTORecord) {
						JDTORecord param2 = (JDTORecord)obj2;
						sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : " + param2.toString());
					} else if (obj2 instanceof JDTORecordSet) {
						JDTORecordSet param2 = (JDTORecordSet)obj2;
						pLen2 = param2.size();
						for (int jj = 0; jj < pLen2; jj++) {
							sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + "-" + formatMaxNo(jj, pLen2) + " : " + param2.getRecord(jj).toString());
						}
					} else {
						sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : " + obj.toString());
					}
				}
			} else if (obj instanceof Object[]) {
				Object[] param = (Object[])obj;
				pLen1 = param.length;
				sb = sb.append("\n▩ {");
				for (int ii = 0; ii < pLen1; ii++) {
					if (ii > 0) { sb = sb.append(", "); }
					sb = sb.append(ii + "=" + param[ii].toString());
				}
				sb = sb.append("}");
			} else {
				sb = sb.append("\n▩ " + obj.toString());
			}

			sb = sb.append("\n▩▩ " + paramNm + " ▩▩");

			logger.println(LogLevel.DEBUG, sb.toString());
		} catch (Exception e) {
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
		}
	}
	
   /**
     * 오퍼레이션명 : 크레인스케줄 권상 및 권하위치 제원정보 등록
     *
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public static boolean updYdCrnschBedDataYdP(JDTORecord recCrnSch){

    	JPlateYdUtils     	ydUtils     = new JPlateYdUtils();
    	JPlateYdDaoUtils  	ydDaoUtils  = new JPlateYdDaoUtils();

    	JPlateYdCrnSchDAO 		ydCrnschDao 	= new JPlateYdCrnSchDAO();
    	JPlateYdStkBedDAO 		ydStkBedDao 	= new JPlateYdStkBedDAO();
    	JPlateYdStkLyrDAO 		ydStkLyrDao 	= new JPlateYdStkLyrDAO();
    	JPlateYdCrnWrkMtlDAO 	ydCrnWrkMtlDao 	= new JPlateYdCrnWrkMtlDAO();

    	String 	szMsg        			= "";
    	String 	szMethodName 			= "updYdCrnschBedDataYdP";
    	String 	szOperationName			= "크레인스케줄제원정보등록";
    	int 	intRtnVal = 0;

    	JDTORecord recGetStkBedData 	= null;
    	JDTORecord recUpdCrnSchData 	= null;
    	JDTORecord recInPara        	= null;
    	JDTORecord recUpPara        	= null;
    	JDTORecord recDnPara        	= null;
    	JDTORecord recUpStkBed      	= null;
    	JDTORecord recDnStkBed      	= null;
    	JDTORecord recResultCrnwrkmtl 	= null;

    	JDTORecordSet rsDnStkBed    	= null;
    	JDTORecordSet outRecSet     	= null;
    	JDTORecordSet rsUpStkBed    	= null;
    	JDTORecordSet rsGetStkLyrT  	= null;
    	JDTORecordSet rsResultCrnwrkmtl = null;

    	String 	szYD_GP					= null;
    	String 	szYD_UP_STK_COL_GP		= null;
    	String 	szYD_UP_STK_BED_NO		= null;
    	String 	szYD_UP_STK_LYR_NO		= null;

    	String 	szYD_DN_STK_COL_GP		= null;
    	String 	szYD_DN_STK_BED_NO		= null;
    	String 	szYD_DN_STK_LYR_NO		= null;

    	String 	szYD_CRN_SCH_ID			= null;
    	String 	szYD_EQP_ID				= null;
    	String	szMODIFIER				= null;

		String	szYD_UP_WO_LOC			= null;
		String	szYD_UP_WO_LAYER		= null;
		String	szYD_DN_WO_LOC			= null;
		String	szYD_DN_WO_LAYER		= null;
		String	szYD_TO_LOC_GUIDE		= null;

    	double 	dblSUM_MTL_T 			= 0;
    	String 	szYD_UP_WO_LOC_ZAXIS 	= null;
    	String 	szYD_DN_WO_LOC_ZAXIS 	= null;
    	String 	szSessionName 			= "ydUtils";

    	try {

			szMsg = "크레인스케줄 권상 및 권하위치 제원정보 등록 .... START >>>> " + recCrnSch.toString();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMODIFIER = ydDaoUtils.paraRecModifier(recCrnSch);

    		//1.크레인스케줄을 조회한다.
    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("yd");
    		intRtnVal = ydCrnschDao.getYdCrnSch(recCrnSch, outRecSet);			// intGp == 0

    		outRecSet.absolute(1);
    		recUpdCrnSchData = JDTORecordFactory.getInstance().create();
    		recUpdCrnSchData.setRecord(outRecSet.getRecord());

    		szYD_CRN_SCH_ID    	= ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_CRN_SCH_ID");
    		szYD_EQP_ID		   	= ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_EQP_ID");

    		szYD_UP_WO_LOC		= ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_UP_WO_LOC");
    		szYD_UP_WO_LAYER	= ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_UP_WO_LAYER");
    		szYD_UP_STK_COL_GP 	= ydUtils.substr(szYD_UP_WO_LOC, 0, 6);
    		szYD_UP_STK_BED_NO 	= ydUtils.substr(szYD_UP_WO_LOC, 6, 2);
    		szYD_UP_STK_LYR_NO 	= ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_UP_WO_LAYER");
    		szYD_TO_LOC_GUIDE	= ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_TO_LOC_GUIDE");

    		szYD_DN_WO_LOC		= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC");
    		szYD_DN_WO_LAYER	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LAYER");
    		szYD_DN_STK_COL_GP 	= ydUtils.substr(szYD_DN_WO_LOC, 0, 6);
    		szYD_DN_STK_BED_NO 	= ydUtils.substr(szYD_DN_WO_LOC, 6, 2);
    		szYD_DN_STK_LYR_NO 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LAYER");

    		szYD_GP = szYD_UP_STK_COL_GP.substring(0, 1);

    		//1.크레인 작업재료의 높이합을 구한다.
    		rsResultCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("");
        	intRtnVal = ydCrnWrkMtlDao.getSumMtlByYdCrnSchId(recCrnSch, rsResultCrnwrkmtl);		// intGp == 8

        	rsResultCrnwrkmtl.absolute(1);
        	recResultCrnwrkmtl = JDTORecordFactory.getInstance().create();
        	recResultCrnwrkmtl.setRecord(rsResultCrnwrkmtl.getRecord());
        	dblSUM_MTL_T = ydDaoUtils.paraRecChkNullDouble(recResultCrnwrkmtl, "SUM_MTL_T");

    		szMsg = "권상지시베드조회 전.. ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//권상지시베드조회
			recGetStkBedData = JDTORecordFactory.getInstance().create();
			rsUpStkBed = JDTORecordFactory.getInstance().createRecordSet("tempYd");
			recGetStkBedData.setField("YD_STK_COL_GP",  szYD_UP_STK_COL_GP);
			recGetStkBedData.setField("YD_STK_BED_NO",  szYD_UP_STK_BED_NO);
			intRtnVal = ydStkBedDao.getYdStkbed(recGetStkBedData, rsUpStkBed);			// intGp == 0
			if (intRtnVal <= 0) {
				szMsg="updYdCrnschBedDataYdP 권상지시 베드 정보조회 중 Error!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
				return false;
			}
			rsUpStkBed.absolute(1);
			recUpStkBed = JDTORecordFactory.getInstance().create();
			recUpStkBed.setRecord(rsUpStkBed.getRecord());

			szMsg = "권상지시베드조회 후.. ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, recUpStkBed);

			recUpPara = JDTORecordFactory.getInstance().create();
			rsGetStkLyrT = JDTORecordFactory.getInstance().createRecordSet("");
			recUpPara.setField("YD_STK_COL_GP", szYD_UP_STK_COL_GP);
			recUpPara.setField("YD_STK_BED_NO", szYD_UP_STK_BED_NO);
			recUpPara.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(szYD_UP_STK_LYR_NO, -1));
			intRtnVal = ydStkLyrDao.getStkLyrMtlSumT(recUpPara, rsGetStkLyrT);			// intGp == 71
			if (intRtnVal <= 0) {
				szYD_UP_WO_LOC_ZAXIS = "0";
			} else {
				rsGetStkLyrT.absolute(1);
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setRecord(rsGetStkLyrT.getRecord());
				szYD_UP_WO_LOC_ZAXIS = String.valueOf(ydDaoUtils.paraRecChkNullDouble(recInPara, "SUM_MTL_T"));

				int idx = szYD_UP_WO_LOC_ZAXIS.lastIndexOf(".");
				if( idx >= 0 ) {
					szYD_UP_WO_LOC_ZAXIS = szYD_UP_WO_LOC_ZAXIS.substring(0, idx);
				}
			}

			//권하지시베드조회
			rsDnStkBed = JDTORecordFactory.getInstance().createRecordSet("");
			recGetStkBedData = JDTORecordFactory.getInstance().create();
			recGetStkBedData.setField("YD_STK_COL_GP",  szYD_DN_STK_COL_GP);
			recGetStkBedData.setField("YD_STK_BED_NO",  szYD_DN_STK_BED_NO);
			intRtnVal = ydStkBedDao.getYdStkbed(recGetStkBedData, rsDnStkBed);			// intGp == 0
			if(intRtnVal <= 0){
				szMsg="updYdCrnschBedDataYdP 권하지시 베드 정보조회 중 Error!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
				return false;
			}
			rsDnStkBed.absolute(1);
			recDnStkBed = JDTORecordFactory.getInstance().create();
			recDnStkBed.setRecord(rsDnStkBed.getRecord());

			szMsg="권하지시베드조회 후.. ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, recDnStkBed);

			recDnPara = JDTORecordFactory.getInstance().create();
			rsGetStkLyrT = JDTORecordFactory.getInstance().createRecordSet("");
			recDnPara.setField("YD_STK_COL_GP", szYD_DN_STK_COL_GP);
			recDnPara.setField("YD_STK_BED_NO", szYD_DN_STK_BED_NO);
			recDnPara.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(szYD_DN_STK_LYR_NO, -1));
			intRtnVal = ydStkLyrDao.getStkLyrMtlSumT(recDnPara, rsGetStkLyrT);			// intGp == 71

			szMsg = "dblSUM_MTL_T :" + dblSUM_MTL_T;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "szYD_DN_WO_LOC_ZAXIS :" + szYD_DN_WO_LOC_ZAXIS;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if (intRtnVal <= 0) {
				szMsg = "================PASS1============================";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);
				//szYD_DN_WO_LOC_ZAXIS = ""+ (int)dblSUM_MTL_T;
				szYD_DN_WO_LOC_ZAXIS = String.valueOf(dblSUM_MTL_T);

			} else {
				rsGetStkLyrT.absolute(1);
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setRecord(rsGetStkLyrT.getRecord());

				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szMsg = "================PASS2============================";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);
				szYD_DN_WO_LOC_ZAXIS = String.valueOf(ydDaoUtils.paraRecChkNullDouble(recInPara, "SUM_MTL_T") + dblSUM_MTL_T);

				//szYD_DN_WO_LOC_ZAXIS =  ""+ (int)(ydDaoUtils.paraRecChkNullDouble(recInPara, "SUM_MTL_T")+dblSUM_MTL_T);
			}

			int idx = szYD_DN_WO_LOC_ZAXIS.lastIndexOf(".");
			if (idx >= 0 ) {
				szYD_DN_WO_LOC_ZAXIS = szYD_DN_WO_LOC_ZAXIS.substring(0, idx);
			}

			//-------------------------------------------------------------------------------------------------------------
			//	크레인 허용 오차 및 크레인 X, Y좌표 계산
			//-------------------------------------------------------------------------------------------------------------
			int intCRANE_GAP_UP_X		= JPlateYdConst.PLATE_CRANE_GAP_X;
			int intCRANE_GAP_UP_Y		= JPlateYdConst.PLATE_CRANE_GAP_Y;
			int intCRANE_GAP_UP_Z		= JPlateYdConst.PLATE_CRANE_GAP_Z;

			int intCRANE_GAP_DN_X		= JPlateYdConst.PLATE_CRANE_GAP_X;
			int intCRANE_GAP_DN_Y		= JPlateYdConst.PLATE_CRANE_GAP_Y;
			int intCRANE_GAP_DN_Z		= JPlateYdConst.PLATE_CRANE_GAP_Z;

			String szUP_GRAB_X_VALUE    = "";
			String szUP_GRAB_Y_VALUE    = "";
			String szUP_GRAB_Y1_VALUE   = "";
			String szUP_GRAB_Y2_VALUE   = "";

			String szDN_GRAB_X_VALUE    = "";
			String szDN_GRAB_Y_VALUE    = "";
			String szDN_GRAB_Y1_VALUE   = "";
			String szDN_GRAB_Y2_VALUE	= "";


			intCRANE_GAP_UP_X		= JPlateYdConst.PPLATE_CRANE_GAP_X;
			intCRANE_GAP_UP_Y		= JPlateYdConst.PPLATE_CRANE_GAP_Y;
			intCRANE_GAP_UP_Z		= JPlateYdConst.PPLATE_CRANE_GAP_Z;

			intCRANE_GAP_DN_X		= JPlateYdConst.PPLATE_CRANE_GAP_X;
			intCRANE_GAP_DN_Y		= JPlateYdConst.PPLATE_CRANE_GAP_Y;
			intCRANE_GAP_DN_Z		= JPlateYdConst.PPLATE_CRANE_GAP_Z;
			
			//----------------------------------------------------------------
			//	권상 시 크레인 허용오차
			//----------------------------------------------------------------
			if ("PT".equals(szYD_UP_STK_COL_GP.substring(2, 4))) {
				intCRANE_GAP_UP_X = JPlateYdConst.PPLATE_CRANE_PT_GAP_X;
				intCRANE_GAP_UP_Y = JPlateYdConst.PPLATE_CRANE_PT_GAP_Y;
			} else {
				intCRANE_GAP_UP_X = JPlateYdConst.PPLATE_CRANE_GAP_X;
				intCRANE_GAP_UP_Y = JPlateYdConst.PPLATE_CRANE_GAP_Y;
			}
			intCRANE_GAP_UP_Z     = JPlateYdConst.PPLATE_CRANE_GAP_Z;

			//----------------------------------------------------------------
			//	권하 시 크레인 허용오차
			//----------------------------------------------------------------
			if ("PT".equals(szYD_DN_STK_COL_GP.substring(2, 4))) {
				intCRANE_GAP_DN_X = JPlateYdConst.PPLATE_CRANE_PT_GAP_X;
				intCRANE_GAP_DN_Y = JPlateYdConst.PPLATE_CRANE_PT_GAP_Y;
			} else {
				intCRANE_GAP_DN_X = JPlateYdConst.PPLATE_CRANE_GAP_X;
				intCRANE_GAP_DN_Y = JPlateYdConst.PPLATE_CRANE_GAP_Y;
			}
			intCRANE_GAP_DN_Z     = JPlateYdConst.PPLATE_CRANE_GAP_Z;

			szMsg	= "["+szOperationName+"] -------------------------- X,Y 좌표계산 시작 -------------------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecord recResult = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", 		szYD_CRN_SCH_ID);
			recPara.setField("YD_EQP_ID", 			szYD_EQP_ID);
			recPara.setField("YD_UP_STK_COL_GP", 	szYD_UP_STK_COL_GP);
			recPara.setField("YD_UP_STK_BED_NO", 	szYD_UP_STK_BED_NO);
			recPara.setField("YD_DN_STK_COL_GP", 	szYD_DN_STK_COL_GP);
			recPara.setField("YD_DN_STK_BED_NO", 	szYD_DN_STK_BED_NO);

			JPlateYdGdsUtil.procXYCalForPlateCraneYdP(recPara, recResult);

			szUP_GRAB_X_VALUE   = ydDaoUtils.paraRecChkNull(recResult, "UP_GRAB_X_VALUE");
			szUP_GRAB_Y_VALUE   = ydDaoUtils.paraRecChkNull(recResult, "UP_GRAB_Y_VALUE");
			szUP_GRAB_Y1_VALUE  = ydDaoUtils.paraRecChkNull(recResult, "UP_GRAB_Y1_VALUE");
			szUP_GRAB_Y2_VALUE  = ydDaoUtils.paraRecChkNull(recResult, "UP_GRAB_Y2_VALUE");

			szDN_GRAB_X_VALUE   = ydDaoUtils.paraRecChkNull(recResult, "DN_GRAB_X_VALUE");
			szDN_GRAB_Y_VALUE   = ydDaoUtils.paraRecChkNull(recResult, "DN_GRAB_Y_VALUE");
			szDN_GRAB_Y1_VALUE  = ydDaoUtils.paraRecChkNull(recResult, "DN_GRAB_Y1_VALUE");
			szDN_GRAB_Y2_VALUE	= ydDaoUtils.paraRecChkNull(recResult, "DN_GRAB_Y2_VALUE");

			szMsg = "["+szOperationName+"] -------------------------- X,Y 좌표계산 완료 -------------------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);			
				
			// 2013.05.02 김현우 물리위치 보정 (차상국에서  UP_GRAB_Y1_VALUE , DN_GRAB_Y1_VALUE 좌표값을 사용)
			if ("".equals(szDN_GRAB_Y1_VALUE)) {
				szUP_GRAB_Y1_VALUE = szUP_GRAB_Y_VALUE;
				szDN_GRAB_Y1_VALUE = szDN_GRAB_Y_VALUE;
			}

			//-------------------------------------------------------------------------------------------------------------
			// 크레인 스케줄  권하지시위치 업데이트
			//-------------------------------------------------------------------------------------------------------------
			recUpdCrnSchData = JDTORecordFactory.getInstance().create();

			recUpdCrnSchData.setField("YD_CRN_SCH_ID",  			recResultCrnwrkmtl.getFieldString("YD_CRN_SCH_ID"));

			recUpdCrnSchData.setField("YD_EQP_WRK_SH",    			ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "SH_CNT"));
			recUpdCrnSchData.setField("YD_EQP_WRK_WT",    			ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "SUM_MTL_WT"));
			recUpdCrnSchData.setField("YD_EQP_WRK_T",     			ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "SUM_MTL_T"));
			recUpdCrnSchData.setField("YD_EQP_WRK_MAX_W", 			ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "MAX_MTL_W"));
			recUpdCrnSchData.setField("YD_EQP_WRK_MAX_L", 			ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "MAX_MTL_L"));

			recUpdCrnSchData.setField("YD_UP_WO_LOC_XAXIS",   		szUP_GRAB_X_VALUE);
			recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_UP_X));
			recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_UP_X));
			recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS",   		szUP_GRAB_Y_VALUE);
			recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS1",  		szUP_GRAB_Y1_VALUE);
			recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS2",  		szUP_GRAB_Y2_VALUE);
			recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_UP_Y));
			recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_UP_Y));
			recUpdCrnSchData.setField("YD_UP_WO_LOC_ZAXIS",  		szYD_UP_WO_LOC_ZAXIS);
			recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_UP_Z));
			recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_UP_Z));

			recUpdCrnSchData.setField("YD_DN_WO_LOC_XAXIS",   		szDN_GRAB_X_VALUE);
			recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_DN_X));
			recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_DN_X));
			recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS",   		szDN_GRAB_Y_VALUE);
			recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS1",  		szDN_GRAB_Y1_VALUE);
			recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS2",  		szDN_GRAB_Y2_VALUE);
			recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_DN_Y));
			recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_DN_Y));
			recUpdCrnSchData.setField("YD_DN_WO_LOC_ZAXIS",  		szYD_DN_WO_LOC_ZAXIS);
			recUpdCrnSchData.setField("YD_DN_WO_ZAXIS_GAP_MAX",  	String.valueOf(intCRANE_GAP_DN_Z));
			recUpdCrnSchData.setField("YD_DN_WO_ZAXIS_GAP_MIN",  	String.valueOf(intCRANE_GAP_DN_Z));

			recUpdCrnSchData.setField("MODIFIER",  					szMODIFIER);
			recUpdCrnSchData.setField("YD_UP_WO_LOC",				szYD_UP_WO_LOC);
			recUpdCrnSchData.setField("YD_UP_WO_LAYER",				szYD_UP_WO_LAYER);
			recUpdCrnSchData.setField("YD_DN_WO_LOC",				szYD_DN_WO_LOC);
			recUpdCrnSchData.setField("YD_DN_WO_LAYER",				szYD_DN_WO_LAYER);

			if ("".equals(szYD_TO_LOC_GUIDE)) {
				szYD_TO_LOC_GUIDE = szYD_DN_WO_LOC;
			}
			recUpdCrnSchData.setField("YD_TO_LOC_GUIDE",			szYD_TO_LOC_GUIDE);

			intRtnVal = ydCrnschDao.updEqpUpDnWoInfo(recUpdCrnSchData);		// 303
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="<updYdCrnschBedDataYdP> updYdCrnsch data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg,JPlateYdConst.WARNING);
    			} else if(intRtnVal == -1) {
    				szMsg="<updYdCrnschBedDataYdP> updYdCrnsch duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
    			} else if(intRtnVal == -2) {
    				szMsg="<updYdCrnschBedDataYdP> updYdCrnsch parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
    			} else if(intRtnVal == -3){
    				szMsg="<updYdCrnschBedDataYdP> updYdCrnsch execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
    			}
    			return false;
    		}

    	} catch(Exception e) {
			szMsg="<updYdCrnschBedDataYdP> Error : "+ e.getLocalizedMessage();
			ydUtils.putLog("YdUtils", szMethodName, szMsg, JPlateYdConst.ERROR);
			return false;
    	}
    	return true;
    }
	
	//
	// 1후판 정정 RT 스케쥴코드 변환
	// 파라미터 : 저장위치, In/Out Flag (LM, UM)
	//
	public String getRtSchCdYdP(String pYdStrLoc, String pSchFlag){

		String 	rtnSchCd = "";
		String 	sRtLoc	= this.substr(pYdStrLoc, 0, 6);
		String 	sBayGp	= this.substr(pYdStrLoc, 1, 1);

		if ("A".equals(sBayGp)) {
//---------------------------------------------------------------------------------------------
// 2024.11.26 #2 열처리 저장위치 PART31, PART32, PART34, PART35 
//            #2 S/B    저장위치 PART23
//---------------------------------------------------------------------------------------------
//			if ("PART13".equals(sRtLoc) || "PART14".equals(sRtLoc)) {
			if (  "PART13".equals(sRtLoc) 
			   || "PART14".equals(sRtLoc)
			   || "PART31".equals(sRtLoc)
			   || "PART32".equals(sRtLoc)
			   || "PART34".equals(sRtLoc)
			   || "PART35".equals(sRtLoc)
			   || "PART23".equals(sRtLoc)
			   ) {
//---------------------------------------------------------------------------------------------
				rtnSchCd = sRtLoc + pSchFlag;
			} else {
				rtnSchCd = this.substr(pYdStrLoc, 0, 5) +"0"+ pSchFlag;
			}
		} else if ("B".equals(sBayGp)) {
			rtnSchCd = this.substr(pYdStrLoc, 0, 5) +"0"+ pSchFlag;
		} else if ("F".equals(sBayGp)) {
//---------------------------------------------------------------------------------------------
// 2024.11.26 #2 S/B 저장위치 PFRT21
//---------------------------------------------------------------------------------------------
			if ( "PFRT21".equals(sRtLoc) ) {
				rtnSchCd = sRtLoc + pSchFlag;
			}	
			else {
				rtnSchCd = this.substr(pYdStrLoc, 0, 5) +"0"+ pSchFlag;
			}
			
		} else {
			rtnSchCd = sRtLoc + pSchFlag;
		}

		return rtnSchCd;
	} // end of isBookOutSchCd
	//
	// 크레인작업지시 X축 허용오차 조회
	// 파라미터 : 저장위치
	//
	public int getCraneGapXYdP(String pYdStkColGp){

		int iCraneGapX = JPlateYdConst.PPLATE_CRANE_GAP_X;
		
		if(pYdStkColGp.startsWith("PBRT4")){
			//1후판 열처리라인 #4 RT일경우 #5 RT에서 권상이 가능하도록 허용오차 범위 최대확대
			iCraneGapX = 70000;
		}else{
			if ("PT".equals(this.substr(pYdStkColGp, 2, 2))) {
				iCraneGapX = JPlateYdConst.PLATE_CRANE_PT_GAP_X;
			} else if ("BS".equals(this.substr(pYdStkColGp, 2, 2))) {
				iCraneGapX = JPlateYdConst.PLATE_CRANE_BS_GAP_X;
			} else if ("CN".equals(this.substr(pYdStkColGp, 2, 2))) {
				iCraneGapX = JPlateYdConst.PLATE_CRANE_CN_GAP_X;
			} else if ("TC".equals(this.substr(pYdStkColGp, 2, 2))) {
				iCraneGapX = JPlateYdConst.PLATE_CRANE_TC_GAP_X;
			} else if ("TD".equals(this.substr(pYdStkColGp, 2, 2))) {
				iCraneGapX = JPlateYdConst.PLATE_CRANE_TD_GAP_X;
			} else if ("RT".equals(this.substr(pYdStkColGp, 2, 2))) {
				iCraneGapX = JPlateYdConst.PLATE_CRANE_RT_GAP_X;
			} else if ("TF".equals(this.substr(pYdStkColGp, 2, 2))) {
				iCraneGapX = JPlateYdConst.PLATE_CRANE_RT_GAP_X;	
			} else if ("CB".equals(this.substr(pYdStkColGp, 2, 2))) {
				iCraneGapX = JPlateYdConst.PLATE_CRANE_CB_GAP_X;
			} else if ("TR".equals(this.substr(pYdStkColGp, 2, 2))) {
				iCraneGapX = JPlateYdConst.PLATE_CRANE_RT_GAP_X;
			} else if ("BC".equals(this.substr(pYdStkColGp, 2, 2))) {
				iCraneGapX = JPlateYdConst.PLATE_CRANE_CN_GAP_X;    // 일단 가스장과 동일 하게
			} else {
				iCraneGapX = JPlateYdConst.PPLATE_CRANE_GAP_X;
			}
		}
		return iCraneGapX;
	} // end of CraneGapX

	//
	// 크레인작업지시 Y축 허용오차 조회
	// 파라미터 : 저장위치
	//
	public int getCraneGapYYdP(String pYdStkColGp){

		int iCraneGapY = JPlateYdConst.PPLATE_CRANE_GAP_Y;

		if ("PT".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_PT_GAP_Y;
		} else if ("BS".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_BS_GAP_Y;
		} else if ("CN".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_CN_GAP_Y;
		} else if ("TC".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_TC_GAP_Y;
		} else if ("TD".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_TD_GAP_Y;
		} else if ("RT".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_HT_GAP_Y;
		} else if ("TF".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_HT_GAP_Y;
		} else if ("TR".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_RT_GAP_Y;
		} else if ("CB".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_CB_GAP_Y;
		} else if ("BC".equals(this.substr(pYdStkColGp, 2, 2))) {
			iCraneGapY = JPlateYdConst.PLATE_CRANE_CN_GAP_Y; // 일단 가스장과 동일 하게
		} else {
			iCraneGapY = JPlateYdConst.PPLATE_CRANE_GAP_Y;
		}

		return iCraneGapY;
	} // end of CraneGapY	
	
	//스케줄코드와 권하위치로 #2열처리 book-in 스케줄인지 판별하는 함수
	//return true 시, YDP8L501 B00K-IN COMPLETE 전문 보낼 목적.
	//25.02.28 수정 #2열처리 BOOK-IN 스케줄로 권하위치 변경하여 타 R/T에 내려놓는경우 전문전송 불필요
	public boolean is2ndHeatBookInSchdule(String ydSchcd, String ydDnLoc){
		
		if( 
		     ( ydDnLoc.startsWith("PFRT21") // F동 #2 쇼트 입측 BOOK-IN(0021N)
			  ||ydDnLoc.startsWith("PART23") // A동 #2 쇼트 출측 BOOK-IN(0023N)
			  ||ydDnLoc.startsWith("PART31") // A동 #2 열처리 입측 BOOK-IN(0031N)
			  ||ydDnLoc.startsWith("PART32") // A동 #2 열처리 입측 BOOK-IN(0032N)
			  ||ydDnLoc.startsWith("PART34") // A동 #2 열처리 출측 BOOK-IN(0034N)
			  ||ydDnLoc.startsWith("PART35") // A동 #2 열처리 출측 BOOK-IN(0035N)
			  
			  )
		     
           )
		{
			return true;
		}
		else if (
				//#2 쇼트 출측에서 #2 열처리 입측으로 바로 갖다놓는경우 
				("PART23LM".equals(ydSchcd)&& (ydDnLoc.startsWith("PART31") || ydDnLoc.startsWith("PART32") ) )
				//#2 열처리 출측에서 #2 열처리 입측으로 다시 갖다놓는경우 (열처리 재작업)
				|| ("PART34LM".equals(ydSchcd)&& (ydDnLoc.startsWith("PART31") || ydDnLoc.startsWith("PART32") ) )
				|| ("PART35LM".equals(ydSchcd)&& (ydDnLoc.startsWith("PART31") || ydDnLoc.startsWith("PART32") ) )
				)
		{
			return true;
		}
		else {
			return false;
		}
				
	} //is2ndHeatBookInSchdule	
	
	//스케줄코드로 #2열처리 book-out 스케줄인지 판별하는 함수
	//return true 시, YDP8L501 B00K-OUT COMPLETE 전문 보낼 목적.
	public boolean is2ndHeatBookOutSchdule(String ydSchcd, String ydUpLoc){
		
		if( "PART31LM".equals(ydSchcd)			// A동 #2 열처리 입측 BOOK-OUT(0031N)
	        || "PART32LM".equals(ydSchcd)			// A동 #2 열처리 입측 BOOK-OUT(0032N)	
	        || "PART34LM".equals(ydSchcd)			// A동 #2 열처리 출측 BOOK-OUT(0034N)
	        || "PART35LM".equals(ydSchcd) 		// A동 #2 열처리 출측 BOOK-OUT(0035N)
	        || "PFRT21LM".equals(ydSchcd) 		// F동 #2 쇼트 입측 BOOK-OUT(0021N)
	        || "PART23LM".equals(ydSchcd) 		// A동 #2 쇼트 출측 BOOK-OUT(0023N)
		  
		  )
		{
			return true;
		}
		
		else {
			return false;
		}
				
	} //is2ndHeatBookInSchdule	
//---------------------------------------------------------------------------------
} // end of class
