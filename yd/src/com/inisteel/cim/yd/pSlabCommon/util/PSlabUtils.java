/*
 * @(#)PSlabUtils 야드공통 UTIL
 *
 * @version			V1.00
 * @author			야드공통
 * @date			모름
 *
 * @description		야드공통 UTIL
 * --------------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2020/05/06   염용선      염용선      최초 등록
 */

package com.inisteel.cim.yd.pSlabCommon.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordImplMap;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.metis.rapi4j.ResultData;

import xlib.cmc.GridData;
import xlib.cmc.GridHeader;
import xlib.cmc.OperateGridData;

public class PSlabUtils {

	private String szSessionName =getClass().getName();
	private boolean bDebugFlag=false;
	private Logger logger =new Logger("yd");


//
//	//
//	// String type 형식에 맞춘 now 값 return
//	// y:년, M:월, d:날, E:요일, a:오전/오후,
//	// H:시, m:분, s:초, S:밀리초
//	//
//
//	public static String getCurDate(String type){
//		SimpleDateFormat simpledateformat = new SimpleDateFormat(type);
//        Date date = new Date();
//        return simpledateformat.format(date);
//
//	} // end of getCurDate()
//
//
//	/**
//	 * 현재 시점을 기준으로 계상일자를 구하는 메소드
//	 * @return String
//	 */
//	public static String getDefaultHdsDate() {
//
//		Calendar cal	= Calendar.getInstance();
//
//		cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - 6);
//
//		Date	date	= cal.getTime();
//
//		SimpleDateFormat sdf	= new SimpleDateFormat("yyyyMMdd");
//
//		String sDate	= sdf.format(date);
//
//		return sDate;
//	}
//
//	/**
//	 * 현재 시점을 기준으로 계상일자를 구하는 메소드(7시기준)
//	 * @return String
//	 */
//	public static String getDefaultHdsDate7() {
//
//		Calendar cal	= Calendar.getInstance();
//
//		cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - 7);
//
//		Date	date	= cal.getTime();
//
//		SimpleDateFormat sdf	= new SimpleDateFormat("yyyyMMdd");
//
//		String sDate	= sdf.format(date);
//
//		return sDate;
//	}
//
//	/**
//	 * jSpeed공통코드 코드값에서 명칭 뽑아내기
//	*/
//    public static String jSpeedCommonCodeToName(String[][] arr, String code) {
//        int arrSize = arr[0].length;
//
//        for(int ii=0 ; ii<arrSize ; ii++) {
//            if(code.equals(arr[0][ii])) {
//                 return arr[1][ii];
//            }
//        }
//
//        return code;
//    }
//
//	/**
//	 * 오퍼레이션명 : Get TC Code
//	 *
//	 * @param inRecord
//	 * @return
//	 * @throws JDTOException
//	 */
//	public String getTcCode(JDTORecord inRecord){
//
//
//		String szMsg="";
//		String szMethodName="getTcCode";
//		String szRcvTcCode="";
//
//		try{
//			// 내부인터페이스(JMS Queue)
//			szRcvTcCode = inRecord.getFieldString("JMS_TC_CD");
//
//			// 외부인터페이스(L2 EAI)
//			if(szRcvTcCode == null){
//				szRcvTcCode=inRecord.getFieldString("MSG_ID");
//			}
//
//			// 외부인터페이스(RemoteEAI)
//			if(szRcvTcCode == null){
//				szRcvTcCode=inRecord.getFieldString("TC_CODE");
//
//			}
//
//			if(szRcvTcCode == null){
//				szRcvTcCode="";
//
//			}	// end if
//
//			szRcvTcCode=szRcvTcCode.trim();
//			szRcvTcCode=szRcvTcCode.toUpperCase();
//
//		}catch(Exception e){
//			szMsg=szMethodName+" Exception Error : "+e.getMessage();
//			this.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//
//			return null;
//		} // end of try-catch
//
//		return szRcvTcCode;
//
//
//	} // end of getTcCode();
//
//
//
//
//	/**
//	 * 오퍼레이션명 : putLog
//	 *
//	 * @param String szClassName	// Logging 요청 Class name
//	 *        String szMethodName 	// Logging 요청 Method Name
//	 *        String szLogMsg		// Logging Message
//	 * @return
//	 * @throws DAOException, JDTOException
//	 */
//	public void putLog(String szClassName, String szMethodName, String szLogMsg, int nLogLevel)  {
//
//		String szMsg="";
//		String strCurDate = PSlabUtils.getCurDate("yyyy-MM-dd HH:mm:ss:SSS");
//
//		szMsg =szClassName + "::" + szMethodName +"() " + "\n\t"+szLogMsg;
//
//
//		try{
//
//			if(bDebugFlag){
//
//				switch(nLogLevel){
//
//				case 1:
//					szMsg="[ERROR] "+szMsg;
//					break;
//
//				case 2:
//					szMsg="[WARNING] "+szMsg;
//					break;
//
//				case 3:
//					szMsg="[INFO] "+szMsg;
//					break;
//
//				default:
//					szMsg="[DEBUG] "+szMsg;
//					break;
//
//				} 
//
//				
//			} else {
//
//				
//				switch(nLogLevel){
//				case 1:
//					logger.println(LogLevel.ERROR, this, szMsg);
//					//logger.println(LogLevel.DEBUG, this, szMsg);
//					break;
//
//				case 2:
//					logger.println(LogLevel.WARNING, this, szMsg);
//					break;
//
//				case 3:
//					logger.println(LogLevel.INFO, this, szMsg);
//					break;
//
//				default:
//					logger.println(LogLevel.DEBUG, this, szMsg);
//				break;
//
//
//				} 
//
//			} 
//
//
//		}catch (Exception e){
//
//			szMsg =szClassName + "::" + szMethodName +"() " + "\n\t"+szMethodName+" Exception Error : "+e.getLocalizedMessage();
//
//				logger.println(LogLevel.ERROR, this, szMsg);
//
//		} 
//
//	}
//
//
//
//
//	/**
//	 *      [A] 오퍼레이션명 : fillSpZr
//	 *
//	 * @param String szData			// 변환대상 문자열
//	 *        int    nLen 			// 변환 후 목적 문자열 길이
//	 *        int    nChgMd			// 변환 방식 (0: 숫자열변환, !0: 문자열변환
//	 * @return String 				// 변환 완료 된 문자열
//	 * @throws
//	 */
//	public static String fillSpZr(String szData, int nLen, int nChgMd){
//
//		String szFillData="";
//		int i=0;
//		int nDataLen =0;
//
//
//		try{
//			szFillData= szData.trim();
//			nDataLen =szFillData.length();
//			if (nDataLen >=nLen)
//				return szFillData.substring(0, nLen);
//
//			for(i=nDataLen; i<nLen; i++){
//				if(nChgMd==0)
//					szFillData="0"+szFillData;
//				else
//					szFillData+=" ";
//
//			} // end of for()
//
//		}catch(Exception e){
//			for(i=0;i<nLen;i++){
//				if(nChgMd==0) szFillData="0"+szFillData;
//				else		  szFillData+=" ";
//
//			}
//
//		}
//
//		return szFillData;
//
//	} 
//
//
//
//
//	/**
//	 * 오퍼레이션명 : RuleData to JDTORecord Converter
//	 *
//	 * @param 	String szRuleName	// Rule Name
//	 * @param	String szItems[]	// Rule Item List
//	 * @param	Hashtable htRule	// Source Hashtable
//	 * @param	JDTORecord jdtoRec	// Target JDTORecrd
//	 * @return  Converted JDTORecord
//	 * @throws 	JDTOException
//	 */
//	public boolean cvtTblToRec(String szRuleName, String szItems[],
//			Hashtable htRule, JDTORecord jdtoRec, String szClassName) {
//
//		String szMsg="";
//		String szMethodName="cvtTblToRec";
//
//		PSlabUtils ydUtils = new PSlabUtils();
//
//		ResultData rData= (ResultData) htRule.get(szRuleName);
//		try{
//
//			for (int i = 0 ; i < rData.getColumnCount(); i++) {
//
//				if (i > szItems.length ) {
//					jdtoRec.setField(i + "" , rData.get(0, i));
//				} else {
//					jdtoRec.setField(szItems[i], rData.get(0, i));
//
//				}
//
//			}
//
//		}catch (Exception je){
//
//			szMsg= szMethodName+  " Exception Error : "+ je.getLocalizedMessage();
//			ydUtils.putLog(szClassName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//
//			return false;
//
//		}// end of try
//
//
//		//
//		// Debugging 용
//		//
//		szMsg="Rule Query Successfully";
//		ydUtils.putLog(szClassName, szMethodName, szMsg, PSlabYdConstant.DEBUG);
//
//
//		return true;
//
//	} // end of cvtTblToRec()
//
//
//
//	/**
//	 * Facade별 수신 메시지 분석 처리
//	 *
//	 * @param inRecord
//	 * @return: true/false
//	 * @throws DAOException
//	 */
//	public boolean rcvMsgChk(JDTORecord inRecord,
//			String szSessionName, String szMethodName) throws DAOException {
//
//		//
//		// Facade에서 수신 한 메시지에 대한 정합성 Check
//		//
//
//		PSlabYdTcConst ydTcConst =new PSlabYdTcConst();
//
//		int nRtc=0;
//
//		String szMsg="";
//		String szRcvTcCode="";
//		String szTcUniqId="";
//
//
//
//		try{
//
//			//
//			// 수신 메시지의 인터페이스 Unique ID Check
//			szTcUniqId =inRecord.getFieldString("UNIQUE_ID");
//			if( szTcUniqId==null){
//				szTcUniqId="";
//			}
//
//
//			//
//			// 수신메시지의 TC 유효성 검사
//			//
//			szRcvTcCode=this.getTcCode(inRecord);
//			if(szRcvTcCode==null){
//				szMsg ="["+szTcUniqId+"] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
//				this.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//
//				return false;
//			}
//
//
//
//			//
//			// 수신 메시지 로깅
//			//
//			szMsg="["+szTcUniqId+"] 전문수신 : TCCODE=" +szRcvTcCode;
//			putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.DEBUG);
//
//
//
//			// 수신 Tc Check
//			nRtc=ydTcConst.chkTcType(szRcvTcCode);
//
//			switch(nRtc){
//
//			case 1:
//
//				// 내부 인터페이스 TC 수신
//				szMsg="내부인터페이스 TC 수신 : " + szRcvTcCode;
//				this.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.DEBUG);
//				break;
//
//			case 2:
//
//				// 리모트 인터페이스 TC 수신
//				szMsg="리모트인터페이스 TC 수신 : " + szRcvTcCode;
//				this.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.DEBUG);
//				break;
//
//			case 3:
//
//				// L2 인터페이스 TC 수신
//				szMsg="L2 인터페이스 TC 수신 : " + szRcvTcCode;
//				this.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.DEBUG);
//				break;
//
//			default:
//
//				// Unknown TC 수신
//				szMsg="Unknown TC Error : " + szRcvTcCode + " ErrCode="+nRtc;
//				this.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//
//				return false;
//
//			} // end of switch()
//
//
//
//
//			//
//			// TC Code vs Method Check
//			//
//			if( !(ydTcConst.chkTcMethod(szRcvTcCode, szMethodName)) ){
//				szMsg="Unknown TC Method TCCode="+szRcvTcCode+" MethodName="+szMethodName;
//				this.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//
//				return false;
//
//			} 
//
//
//		}catch (Exception e){
//			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
//			this.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//
//			return false;
//		}
//
//		return true;
//
//	} 
//
//
//
//	/**
//	 * JDTORecord의 Key값을 지정 배열로 리턴한다.
//	 *
//	 * @param inRecord
//	 * @return
//	 */
//	public String[] getRecKey(JDTORecord inRecord) {
//
//		int nRecCnt=-1;
//
//		Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
//		nRecCnt =objTemp.length;
//		String[] szaKeys =new String[nRecCnt];
//		for(int i=0; i<nRecCnt; i++)
//			szaKeys[i]=objTemp[i].toString();
//
//		return szaKeys;
//
//	} 
//
//
//
//	/**
//	 * JDTORecord의 Key 갯수 리턴
//	 *
//	 * @param fillerSize
//	 * @param inRecord
//	 * @return
//	 */
//	public int getRecKeyCnt(JDTORecord inRecord) {
//
//		int nKeyCnt=-1;
//		String [] szaRecKeys =null;
//
//		try{
//			szaRecKeys=this.getRecKey(inRecord);
//			nKeyCnt =szaRecKeys.length;
//
//		}catch(Exception e){
//			return -1;
//		}
//
//		return nKeyCnt;
//
//	} 
//
//
//
//	/**
//	 * JDTORecord의 지정 Key값를 삭제한다.
//	 *
//	 * @param fillerSize
//	 * @param inRecord
//	 * @return
//	 */
//	public JDTORecord delRecKey(JDTORecord inRecord, String szKey) {
//
//		String szMsg="";
//		String szMethodName ="delRecKey";
//
//		JDTORecord outRecord =JDTORecordFactory.getInstance().create();
//
//		int nRecCnt=-1;
//
//		Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
//		nRecCnt =objTemp.length;
//		String szRecKey="";
//		String szValue="";
//
//		for(int i=0; i<nRecCnt; i++){
//			try {
//				szRecKey =objTemp[i].toString();
//				szValue =inRecord.getFieldString(szRecKey);
//
//				if(szKey.equals(szRecKey))
//					continue;
//
//
//				outRecord.setField(szRecKey, szValue);
//
//			} catch (JDTOException e) {
//				szMsg="Exception Error : "+e.getLocalizedMessage();
//				this.putLog(szMsg, szSessionName, szMethodName, 1);
//				e.printStackTrace();
//
//
//				return null;
//			}
//
//		} 
//
//		return outRecord;
//
//	} 
//
//
//
//	/**
//	 * JDTORecord의 내용을 Key값=데이터값의 형식으로 표시한다.
//	 *
//	 * @param fillerSize
//	 * @param inRecord
//	 * @return
//	 */
//	public int disyRec(JDTORecord inRecord)	{
//		int nRecCnt=-1;
//
//		Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
//		nRecCnt =objTemp.length;
//		String szRecKey="";
//		String szValue="";
//		String szMsg="";
//
//		for(int i=0; i<nRecCnt; i++){
//			szRecKey =objTemp[i].toString();
//			szValue =inRecord.getFieldString(szRecKey);
//
//			if(szValue==null)
//				szValue="(null)";
//
//			szMsg= "["+(i+1)+"]"
//			     + "\t"+szRecKey
//			     + "\t["+szValue+"]";
//			//System.out.println(szMsg);
//			logger.println(LogLevel.INFO, this, szMsg);
//
//		} 
//
//		return nRecCnt;
//
//	} 
//
//
//
//	/**
//	 * JDTORecord의 내용 중 키값의 데이터들을 문자열로 리턴한다.
//	 *
//	 * @param fillerSize
//	 * @param inRecord
//	 * @return
//	 */
//	public String makeRec2Str(JDTORecord inRecord)	{
//
//
//		Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
//		int nItemCnt =objTemp.length;
//
//		String szItemKey="";
//		String szValue="";
//		String szRecMsg="";
//
//
//		for(int i=0; i<nItemCnt; i++){
//			szItemKey =objTemp[i].toString();
//			szValue =inRecord.getFieldString(szItemKey);
//
//			if(szValue==null)
//				szValue="";
//
//			szRecMsg+=szValue;
//	
//			logger.println(LogLevel.DEBUG, this, szRecMsg);
//
//		} 
//
//		return szRecMsg;
//
//	}
//
//
//
//	/**
//	 * JDTORecordSet을 JDTORecord형으로 변환
//	 *
//	 * @param fillerSize
//	 * @param inRecord
//	 * @return
//	 */
//	public int chgRecSet2Rec(JDTORecordSet inRecSet, JDTORecord outRec)	{
//
//		String szMsg="";
//		String szMethodName="chgRecSet2Rec";
//
//		int nRecCnt =0;
//
//
//		try{
//
//
//			if( nRecCnt <=0)
//				return -1;
//
//			if( !inRecSet.isFirst())
//				inRecSet.first();
//
//			for(int i=1;i<=nRecCnt;i++){
//
//				outRec.setField(""+i, inRecSet.getRecord());
//				inRecSet.next();
//
//			} // end of for()
//
//
//		} catch(Exception e){
//			szMsg=szMethodName+" Exfeption Error : "+ e.getLocalizedMessage();
//			this.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//
//			return -1;
//		}
//
//		return nRecCnt;
//
//
//	} // end makeRec2Str()
//
//	/**
//	 * 문자열 좌측을 지정한 값으로 채워넣음
//	 *
//	 * @param str
//	 * @param len
//	 * @param pad
//	 * @return
//	 * @throws Exception
//	 */
//	public String addLeftStr(String str, int len, char pad) throws Exception
//	{
//		String szMethodName = "addLeftStr";
//		String result = "";
//		int templen = 0;
//
//		try{
//			templen = len - str.getBytes().length;
//			if(templen >= 0){
//				for(int i=0; i<templen; i++)
//					str = pad + str;
//				result = str;
//			}
//		}catch(Exception e){
//			this.putLog(szSessionName, szMethodName, "FloatLRPAD() : " + e.toString() + " : " + e.getMessage(), 4);
//		}
//
//		return result;
//	}
//
//	/**
//	 * 문자열 우측을 지정한 값으로 채워넣음
//	 *
//	 * @param str
//	 * @param len
//	 * @param pad
//	 * @return
//	 * @throws Exception
//	 */
//	public String addRightStr(String str, int len, char pad) throws Exception
//	{
//		String szMethodName = "addRightStr";
//		String result = "";
//		int templen = 0;
//
//		try{
//			templen = len - str.getBytes().length;
//			if(templen >= 0){
//				for(int i=0; i<templen; i++)
//					str = str + pad;
//				result = str;
//			}
//		}catch(Exception e){
//			this.putLog(szSessionName, szMethodName, "FloatLRPAD() : " + e.toString() + " : " + e.getMessage(), 4);
//		}
//
//		return result;
//	}
//
//	/**
//	 * 실수 문자열값 좌우측을 채워넣음
//	 *
//	 * @param strOrg
//	 * @param nTotal
//	 * @param nFloat
//	 * @return
//	 * @throws Exception
//	 */
//	public String FloatLRPAD(String strOrg, int nTotal, int nFloat, char ch) throws Exception
//	{
//		String szMethodName = "FloatLRPAD";
//		String strTemp1 = "";
//		String strTemp2 = "";
//		int nJisu = nTotal - nFloat;
//		int nSosu = nFloat;
//
//		try{
//			if(strOrg == null || "".equals(strOrg.trim()))
//				return addLeftStr("", nTotal, (char)ch);
//
//			int nIdx = strOrg.indexOf(".");
//			if(nIdx <= 0){
//				strTemp1 = this.addLeftStr(strOrg.trim(), nJisu, (char)ch);
//				strTemp2 = this.addRightStr("0", nSosu, (char)ch);
//				if(strTemp1.trim().equals("")){
//					return null;
//				}
//
//			}else {
//				String[] strSplit = strOrg.trim().split("\\.");
//
//				strTemp1 = this.addLeftStr(strSplit[0], nJisu, (char)ch);
//				strTemp2 = this.addRightStr(strSplit[1], nSosu, (char)ch);
//
//				if(strTemp1.equals("") || strTemp2.equals("")){
//					return null;
//				}
//			}
//		}catch(Exception e){
//			this.putLog(szSessionName, szMethodName, "FloatLRPAD() : " + e.toString() + " : " + e.getMessage(), 4);
//		}finally{
//		}
//
//		return 	strTemp1 + strTemp2;
//	}
//
//
//
//
//
//
//	/**
//	 * JDTORecord의 내용을 Key값=데이터값의 형식으로 표시한다.
//	 * @param inRecord
//	 * @return
//	 */
//	public int disyRecInfo(JDTORecord inRecord)	{
//		int nRecCnt=-1;
//
//		Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
//		nRecCnt =objTemp.length;
//		String szRecKey="";
//		String szValue="";
//		String szMsg="";
//
//		for(int i=0; i<nRecCnt; i++){
//			szRecKey =objTemp[i].toString();
//			szValue =inRecord.getFieldString(szRecKey);
//
//			if(szValue==null)
//				szValue="(null)";
//
//			szMsg= "["+(i+1)+"]"
//			     + "\t"+szRecKey
//			     + "\t["+szValue+"]";
//
//			logger.println(LogLevel.INFO, this, szMsg);
//		} // end of for()
//
//		return nRecCnt;
//	}
//
//	/**
//	 * JDTORecord의 내용을 Key값=데이터값의 형식으로 표시한다.
//	 * @param inRecord
//	 * @return
//	 */
//	public int displayRecord(String szOperationName, JDTORecord inRecord)	{
//		
//		int nRecCnt = 0;
//
//		if(false){
//			Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
//			nRecCnt =objTemp.length;
//			String szRecKey="";
//			String szValue="";
//			String szMsg="";
//
//			for(int i=0; i<nRecCnt; i++){
//				szRecKey =objTemp[i].toString();
//				szValue =inRecord.getFieldString(szRecKey);
//
//				if(szValue==null)
//					szValue="(null)";
//
//				szMsg = szOperationName + " ["+(i+1)+"]" + szRecKey + " = ["+szValue+"]";
//
//				logger.println(LogLevel.DEBUG, this, szMsg);
//			} // end of for()
//		}
//		return nRecCnt;
//	}
//
//
//
//
//
//	/**
//	 * 문자열에 한글이 포함이 되어 있는지 검사하는 메서드
//	 * @param szData
//	 * @return boolean
//	 */
//	public boolean IsInclude_Hangul(String szData){
//		String szFillData = "";
//		boolean bResult  = false;
//
//		szFillData = szData.trim();
//
//		for(int i=0; i<szFillData.length(); i++){
//			if(Character.getType(szFillData.charAt(i)) == 5)
//				bResult = true;
//		}
//
//		return bResult;
//	}
//
//
//
//
//
//	/**
//	 * 숫자표시형 문자열데이터값을 증가치만큼 증가 시키고 공백은 자릿수 만큼 '0'으로 채워넣음
//	 *
//	 * @param  수치표시형 문자열버퍼, 증가치, 자릿수
//	 * @return 증가된 수치표시형 문자열
//	 * @throws Exception
//	 */
//	public String IncreaseStrToInt(String strTemp, int nIncreaseCnt, int nDigit) throws Exception{
//		String szBufferObj = "";
//		int nTemp          = 0;
//
//		nTemp = Integer.parseInt(strTemp);
//		nTemp += nIncreaseCnt;
//
//		szBufferObj = this.addLeftStr("" + nTemp, nDigit, '0');
//		return szBufferObj;
//	}
//
//
//	
//
//	/**
//	 *  공백이나 0 채워넣는 함수에서 한글 2바이트 문제 처리  ㅡ.ㅡ
//	 *
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param szData, nLen, nChgMd
//	 * @return String
//	 */
//	public static String fillSpZr_KOR(String szData, int nLen, int nChgMd){
//
//		String szFillData = "";
//		int nDataLen      = 0;
//		int nCntKOR       = 0;
//		int nCntEXP       = 0;
//		int i             = 0; 
//		PSlabUtils 		ydUtils     = new PSlabUtils();
//		try{
//			szFillData = szData.trim();
//
//			for(i=0; i<szFillData.length(); i++){
//				
//				ydUtils.putLog("YdUtils", "fillSpZr_KOR", "Character!!"+Character.getType(szFillData.charAt(i)), PSlabYdConstant.DEBUG); 
//				if(Character.getType(szFillData.charAt(i)) == 5 || Character.getType(szFillData.charAt(i)) == 28)
//					nCntKOR++;
//				else
//					nCntEXP++;
//			}
//
//			nDataLen = szFillData.length() + nCntKOR;
//			if(nDataLen > nLen)
//				return cutString(szFillData, nLen);
//
//			for(i=nDataLen; i<nLen; i++){
//				if(nChgMd == 0)
//					szFillData = "0" + szFillData;
//				else
//					szFillData += " ";
//			} // end of for()
//
//		}catch(Exception e){
//			for(i=0;i<nLen;i++){
//				if(nChgMd==0)
//					szFillData = "0" + szFillData;
//				else
//					szFillData += " ";
//			} // end of for();
//
//		} // end of try-catch
//
//		return szFillData;
//	}
//
//
//	/**
//     * 와이즈그리드 콤보 스크립트 생성(오버로딩)
//     */
//	public static String setWiseGridCombo(String hTitle, String[][] comboStrArr) {
//		return setWiseGridCombo("GridObj", hTitle, comboStrArr, 1, "N");
//	}
//
//	/**
//     * 와이즈그리드 콤보 스크립트 생성(오버로딩)
//     */
//	public static String setWiseGridCombo(String hTitle, String[][] comboStrArr, int cdVal) {
//		return setWiseGridCombo("GridObj", hTitle, comboStrArr, cdVal, "N");
//	}
//
//	/**
//     * 와이즈그리드 콤보 스크립트 생성(오버로딩)
//     */
//	public static String setWiseGridCombo(String hTitle, String[][] comboStrArr, String headTextYn) {
//		return setWiseGridCombo("GridObj", hTitle, comboStrArr, 1, headTextYn);
//	}
//
//	/**
//     * 와이즈그리드 콤보 스크립트 생성(오버로딩)
//     */
//	public static String setWiseGridCombo(String hTitle, String[][] comboStrArr, int cdVal, String headTextYn) {
//		return setWiseGridCombo("GridObj", hTitle, comboStrArr, cdVal, headTextYn);
//	}
//
//	/**
//     * 와이즈그리드 콤보 스크립트 생성(오버로딩)
//     */
//	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr) {
//		return setWiseGridCombo(obj, hTitle, comboStrArr, 1, "N");
//	}
//
//	/**
//     * 와이즈그리드 콤보 스크립트 생성(오버로딩)
//     */
//	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr, String headTextYn) {
//		return setWiseGridCombo(obj, hTitle, comboStrArr, 1, headTextYn);
//	}
//
//	/**
//     * 와이즈그리드 콤보 스크립트 생성(오버로딩)
//     */
//	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr, int cdVal) {
//		return setWiseGridCombo(obj, hTitle, comboStrArr, cdVal, "N");
//	}
//
//	/**
//     * 와이즈그리드 콤보 스크립트 생성
//     * @param 	obj			: 와이즈그리드 Object
//     * 			hTitle		: 와이즈그리드 헤더 TITLE
//     * 			comboStrArr	: 와이즈그리드 콤보 Value, Text
//     * 			cdVal		: 콤보에 보여줄 값이 Value, Text 결정(0:Value, 1:Text, 2:Value(Text))
//     * 			headTextYn	: 빈칸 유무
//     * @return 	와이즈그리드 콤보 자바스크립트
//     */
//	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr, int cdVal, String headTextYn) {
//		String comboStr = "";
//
//		if(comboStrArr != null) {
//
//			if("Y".equals(headTextYn)) {
//				comboStr = obj + ".AddComboListValue('" + hTitle + "', '', '');\n";
//			}
//
//			if("S".equals(headTextYn)) {
//				comboStr = obj + ".AddComboListValue('" + hTitle + "', '선택', '');\n";
//			}
//
//			if(cdVal == 0 || cdVal == 1) {
//				for(int ii=0; ii < comboStrArr[0].length; ii++) {
//					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + PSlabUtils.setEscapeStr(comboStrArr[cdVal][ii]) + "', '" + comboStrArr[0][ii] + "');\n";
//				}
//			} else {
//				for(int ii=0; ii < comboStrArr[0].length; ii++) {
//					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" +
//										comboStrArr[0][ii] + " (" + PSlabUtils.setEscapeStr(comboStrArr[1][ii]) + ")', '" + comboStrArr[0][ii] + "');\n";
//				}
//			}
//		}
//
//		return comboStr;
//	}
//
//	/**
//	 * ' 일때 이스케이프 문자를 추가한다.
//	 */
//	public static String setEscapeStr(String str) {
//
//		if(str.indexOf("\'") != -1) {
//			str = str.replaceAll("'", "\\\\'");
//		}
//
//		return str;
//	}
//
//	/**
//	 *  문자열에서  특정자리수 만큼 잘라오는 함수 (한글, 영문, 숫자 포함)	 
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param str, nCutSize
//	 * @return
//	 */
//	public static String cutString(String str, int nCutSize)
//	{
//		String[] StrSub = new String[30];
//		byte[] bTemp = str.getBytes();
//
//		int nCnt = 0;
//		int str_nCnt = 0;
//
//		while(bTemp.length > nCutSize)
//		{
//			nCnt = 0;
//			for(int i=0; i<nCutSize; i++) {
//				if(bTemp[i]<0)
//					nCnt++;
//			}
//
//			if(nCnt%2!=0){
//				StrSub[str_nCnt] = new String(bTemp, 0, nCutSize+1);
//				bTemp = new String(bTemp, nCutSize+1, bTemp.length-(nCutSize+1)).getBytes();
//			}else{
//				StrSub[str_nCnt] = new String(bTemp, 0, nCutSize);
//				bTemp = new String(bTemp, nCutSize, bTemp.length-nCutSize).getBytes();
//			}
//
//			str_nCnt++;
//		}
//
//		StrSub[str_nCnt] = new String(bTemp);
//
//		return StrSub[0];
//	}
//
//
//
//
//
//
//
//  
//
//    /**
//	 *  이적 스케줄 코드를 생성
//	 *
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param String pzYdGp , String pzYdBayGp , String pzYdEqpGp
//	 * @return String (올바르게 만들어 지지 않았을경우 - 8자리가 아닐경우는 "" Retrun)
//	 */
//	public  String getMakeSchCdMM (String pzYdGp , String pzYdBayGp , String pzYdEqpGp )
//	{
//		String szMsg = "";
//		String szRtnValue = "";
//		String szMethodName		= "insCSlabSupPrepSchManual";
//		String szOperationName		= " 이적 스케줄 코드를 생성";
//		String szYdSchCd = "";
//		String szYdEqpGp = "";
//
//
//		szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
//		putLog( szSessionName, szMethodName, szMsg, PSlabYdConstant.DEBUG);
//
//
//
//		szMsg = "[JSP Session : "+szOperationName+"] 입력받은 정보 (야드,동,스판) =  " + "(" + pzYdGp + ", " + pzYdBayGp + "," +  pzYdEqpGp+")";
//		putLog( szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//
//
//
//		if (pzYdGp.equals("")){
//			szMsg = "[JSP Session : "+szOperationName+"]야드구분이 올바르지 않습니다. ";
//			putLog( szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//			return "";
//		}
//
//
//		if (pzYdBayGp.equals("")){
//			szMsg = "[JSP Session : "+szOperationName+"] 동 구분이 올바르지 않습니다. ";
//			putLog( szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//			return "";
//		}
//
//
//		if (pzYdEqpGp.equals("")){
//			szMsg = "[JSP Session : "+szOperationName+"] 설비구분이 올바르지 않습니다. ";
//			putLog( szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//			return "";
//		}
//
//		szYdEqpGp = pzYdEqpGp;
//
//		//	후판제품야드 스케줄 설비정보 판단 Logic
//		if(pzYdGp.equals(PSlabYdConstant.YD_GP_PLATE_GDS_YARD)){
//
//			if(pzYdEqpGp.equals(PSlabYdConstant.SPAN_ORDER_NEW_04)|| pzYdEqpGp.equals(PSlabYdConstant.SPAN_ORDER_NEW_05)|| pzYdEqpGp.equals(PSlabYdConstant.SPAN_ORDER_NEW_06)) {
//				szYdEqpGp = "12";
//			}else if( pzYdEqpGp.equals(PSlabYdConstant.SPAN_ORDER_NEW_07) || pzYdEqpGp.equals(PSlabYdConstant.SPAN_ORDER_NEW_TP) ){
//				szYdEqpGp = "34";
//			}else{
//				szYdEqpGp = "";
//			}
//		}
//		//야드별로 추가 하고 싶을경우는 추가로직을 여기에 넣는다
//
//		szYdSchCd =  pzYdGp + pzYdBayGp + "YD" + szYdEqpGp + "MM";
//
//		//스케줄코드가 제대로 생성되지 않았을 경우
//		if(szYdSchCd.length() != 8){
//			szMsg = "[JSP Session : "+szOperationName+"] 스케줄 코드가 올바르게 생성되지 않았습니다.. ";
//			putLog( szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//			return "";
//		}
//
//		return szYdSchCd;
//	}
//
//
//
//	//=====================================================================
//	// 정수 or 실수값에 대해 3자리마다 콤마처리
//	// ex) 12345678   => "12,345.678" (실수는 3자리까지 처리 늘어나면 포맷 변경)
//	//     12345678   => "12,345,678"
//	//     "1234567"  => "1,234,567"
//	//     "12345.67" => "12,345.67"
//	//=====================================================================
//	public static String FormatCommaStr(double dVal)
//	{
//		NumberFormat nf = new DecimalFormat(",###.###");
//		return (String)nf.format(dVal);
//	}
//
//	public static String FormatCommaStr(long lVal)
//	{
//
//		NumberFormat nf = new DecimalFormat("#,###");
//		return (String)nf.format(lVal);
//	}
//
//	public static String FormatCommaStr(String szVal)
//	{
//		if(szVal == null || szVal.trim().equals("")){
//			return "";
//		}
//
//		int nRet = szVal.trim().indexOf(".");
//		String szRet = "";
//		if(nRet != -1){
//			szRet = FormatCommaStr(Double.parseDouble(szVal.trim()));
//		} else {
//			szRet = FormatCommaStr(Integer.parseInt(szVal.trim()));
//		}
//
//		return szRet;
//	}
//
//
//
//
//
//	//=====================================================================
//	//
//	// 적치열구분(6) + 적치BED번호(2) + 적치단번호(3) 를 입력받아서 야드별로 구분하여
//	// 10자리의 위치정보를 만들어 내는 함수
//	//
// 	// 연주슬라브(A) : 6 + 2 + 2
//	// 후판슬라브(D) : 6 + 2 + 2
//	// 후판제품   (K) : 6 + 1 + 3
//	// 코일소재   (H) : 6 + 2 + 2
//	// 코일제품   (J) : 6 + 2 + 2
//	// 통합슬라브(S) : 6 + 2 + 2
//	//=====================================================================
//	public String ParsingStkColGpBedLyr(String szStkColGp, String szStkBedNo, String szStkLyrNo){
//		// 변수 선언
//		String szMethodName = "ParsingStkColGpBedLyr";
//		String szMsg        = "";
//		String szYdGp       = "";
//		String szRet        = "";
//
//
//		// 파라미터 유효성 체크
//		if(szStkColGp == null || szStkColGp.equals("") || szStkColGp.trim().length() != 6){
//			szMsg = "넘어온 파라미터에서 적치열구분 항목이 유효한 데이터가 아닙니다.";
//			this.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//			return "";
//		}
//
//		if(szStkBedNo == null || szStkBedNo.equals("") || szStkBedNo.trim().length() != 2){
//			szMsg = "넘어온 파라미터에서 적치BED번호 항목이 유효한 데이터가 아닙니다.";
//			this.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//			return "";
//		}
//
//		if(szStkLyrNo == null || szStkLyrNo.equals("") || szStkLyrNo.trim().length() != 3){
//			szMsg = "넘어온 파라미터에서 적치단번호 항목이 유효한 데이터가 아닙니다.";
//			this.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//			return "";
//		}
//
//
//		szMsg = "[수신 항목] 적치열구분(" + szStkColGp + ") 적치BED번호(" + szStkBedNo + ") 적치단번호(" + szStkLyrNo + ")";
//		this.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.DEBUG);
//
//
//		// 야드값 추출
//		szYdGp = szStkColGp.substring(0, 1);
//
//
//		// 야드값에 따른 분기
//		if(szYdGp.equals(PSlabYdConstant.YD_GP_C_SLAB_YARD)){
//			// C연주슬라브야드
//			szRet = szStkColGp + szStkBedNo + PSlabUtils.fillSpZr("" + Integer.parseInt(szStkLyrNo), 2, 0);
//			szMsg = "편집된 위치정보 : C연주슬라브야드(" + szRet + ")";
//		} else if(szYdGp.equals(PSlabYdConstant.YD_GP_A_PLATE_SLAB_YARD)){
//			// A후판슬라브야드
//			szRet = szStkColGp + szStkBedNo + PSlabUtils.fillSpZr("" + Integer.parseInt(szStkLyrNo), 2, 0);
//			szMsg = "편집된 위치정보 : A후판슬라브야드(" + szRet + ")";
//		} else if(szYdGp.equals(PSlabYdConstant.YD_GP_PLATE_GDS_YARD) || szYdGp.equals(PSlabYdConstant.YD_GP_PLATE2_GDS_YARD)){ //--
//			// 1,2후판제품창고야드
//			String sTmp = "";
//			try{
//				sTmp = PSlabUtils.fillSpZr("" + Integer.parseInt(szStkBedNo), 1, 0);
//			}catch(Exception e){
//				sTmp = szStkBedNo;
//			}
//			szRet = szStkColGp + sTmp + szStkLyrNo;
//			szMsg = "편집된 위치정보 : 후판제품창고야드(" + szRet + ")";
//		} else if(szYdGp.equals(PSlabYdConstant.YD_GP_C_HR_COIL_MATL_YARD)){
//			// C열연코일소재야드
//			szRet = szStkColGp + szStkBedNo + PSlabUtils.fillSpZr("" + Integer.parseInt(szStkLyrNo), 2, 0);
//			szMsg = "편집된 위치정보 : C열연코일소재야드(" + szRet + ")";
//		} else if(szYdGp.equals(PSlabYdConstant.YD_GP_C_HR_COIL_GDS_YARD)){
//			// C열연코일제품야드
//			szRet = szStkColGp + szStkBedNo + PSlabUtils.fillSpZr("" + Integer.parseInt(szStkLyrNo), 2, 0);
//			szMsg = "편집된 위치정보 : C열연코일제품야드(" + szRet + ")";
//		} else if(szYdGp.equals(PSlabYdConstant.YD_GP_INTGR_YARD)){
//			// 통합야드A(부두)
//			szRet = szStkColGp + szStkBedNo + PSlabUtils.fillSpZr("" + Integer.parseInt(szStkLyrNo), 2, 0);
//			szMsg = "편집된 위치정보 : 통합야드A부두(" + szRet + ")";
//		} else if(szYdGp.equals(PSlabYdConstant.YD_GP_PORT_SLAB_YARD)){
//			// C3#스카핑야드
//			szRet = szStkColGp + szStkBedNo + PSlabUtils.fillSpZr("" + Integer.parseInt(szStkLyrNo), 2, 0);
//			szMsg = "편집된 위치정보 : C3#스카핑야드(" + szRet + ")";
//		} else {
//			szMsg = "*** 적치열구분, 적치BED번호, 적치단 번호를 이용해 위치정보 편성에 실패 ***";
//			this.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
//			return "";
//		}
//
//		this.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.DEBUG);
//
//		return szRet;
//	}
//
//
//	//---------------------------------------------------------------------------
//	/**
//	 *      [A] 오퍼레이션명 : 문자열을 float로 변환
//	 *          예) 123456 , 6, 3 -> 123.456
//	 *
//	 * @param String strData		// 변환대상 문자열 [소수점없는 숫자값]
//	 *        int    nLen			// 전체자릿수
//	 *        int    nDeci			// 소수부분
//	 * @return float 				// 변환 완료 된 float값
//	 * @throws
//	*/
//	
//	public float strToFloat(String strData, int nLen, int nDeci) {
//
//		try {
//			if (strData.length() != nLen) {
//				nLen = strData.length();
//			}
//			int iData1  = Integer.parseInt(strData.substring(0, (nLen-nDeci)));
//			int iData2  = Integer.parseInt(strData.substring(nLen-nDeci, nLen));
//			String sRtnVal = Integer.toString(iData1) + "." + Integer.toString(iData2);
//			return Float.valueOf(sRtnVal).floatValue();
//
//		} catch (Exception e) {
//			return Float.valueOf(strData).floatValue();
//		}
//	}
//
//
//	
//	public static void main(String[] args){
//
//		try {
//		PSlabUtils im =new PSlabUtils();
//
//		JDTORecord testRec =JDTORecordFactory.getInstance().create();
//		String szOperationName = "일관제철소정보관리시스템 테스트";
//		String [] szaKeys=null;
//
//		int nRtc=0;
//
//
////		try {
//
//			//
//			// make JDTORecord
//			testRec.setField("NAME", new String("김인홍"));
//			testRec.setField("JOB", new String("Computer Programmer/Architect"));
//			testRec.setField("PHONE", new String("010-6257-3209"));
//			testRec.setField("EMAIL", new String("yhwhman@gmail.com"));
//			testRec.setField("TEST1", null);
//			testRec.setField("JMS_TC_CD", null);
//
//
//			//
//			// Get Record Keys
//			szaKeys=im.getRecKey(testRec);
//			nRtc =szaKeys.length;
//
//			
//			// Record Display
//			im.displayRecord(szOperationName, testRec);
//
//			
//			String szMsg =im.makeRec2Str(testRec);
//
//			
//
//		} catch (Exception e) {
//			
//		} 
//
//
//
//	} 
//
//
//	/**
//	 * 문자열이 null 일때 임의의 문자열을 반환한다.
//	 * @param value
//	 * @param defaultValue
//	 * @return String
//	 */
//	public String nvl(String value, String defaultValue) {
//		return (value == null || "".equals(value) || "NULL".equals(value.trim().toUpperCase())) ? defaultValue : value;
//	}
//
//	public String nvl(Object o, String defaultValue) {
//		return (o == null) ? defaultValue : o.toString();
//	}
//
//	/**
//	 * GridData의 입력/수정/삭제  정보를 JDTORecord [] 으로 변환하여 리턴한다.
//	 * (GridData의 입력/수정/삭제 항목을 가져오기위해 사용)
//	 * @param inDto
//	 * @return
//	 */
//	public JDTORecord [] genJDTORecordSet(GridData inDto) throws Exception{
//		boolean isUpperKey = false;		
////		YDDataUtil yDDataUtil = new YDDataUtil();
//		String szUserId = "";
//		String szCRUD ="";
//		String szydEqpId ="";
//		
//		if(inDto.getParam("set_upper") != null && inDto.getParam("set_upper").equals("true")){
//			isUpperKey = true;
//		}
//		
//		szUserId  = nvl(inDto.getParam("YD_USER_ID"), "");
//		szydEqpId = nvl(inDto.getParam("YD_EQP_ID"), "");
//
//		GridHeader [] ghs = inDto.getHeaders();
//		int hCount = ghs.length;
//		int rCount = 0;
//		if(hCount > 0){
//			rCount = ghs[0].getRowCount();
//		}
//		JDTORecord [] jdtoAl = new JDTORecord[rCount];
//		logger.println(LogLevel.DEBUG,   "========  GridData -> JDTORecord []  ================");
//		logger.println(LogLevel.DEBUG,   "헤더갯수:"+hCount);
//		logger.println(LogLevel.DEBUG,   "Row갯수:"+rCount);
//
//		logger.println(LogLevel.DEBUG,   "========== GridData inDto ROW DATA ===========");
//		for(int i=0;i<rCount;i++){
//			GridHeader chHeader = inDto.getHeader("CHECK");
//			boolean Checked = (Integer.parseInt(chHeader.getValue(i)) == 1)? true:false;
//			if(Checked){
//				JDTORecord jDto = JDTORecordFactory.getInstance().create();
//				for(int j=0;j<hCount;j++){
//					String key = ghs[j].getID();
//					String rValue = "";
//					String hValue = "";
//					if(ghs[j].getDataType().equals(OperateGridData.t_combo)){
//						int iSelectedIdx = ghs[j].getSelectedIndex(i);
//						if(iSelectedIdx >= 0){
//							if(ghs[j].hasComboList()){
//								rValue = StringHelper.evl( ghs[j].getComboHiddenValues(ghs[j].getSelectedComboListKey(i))[iSelectedIdx], "");
//								hValue = StringHelper.evl( ghs[j].getComboValues(ghs[j].getSelectedComboListKey(i))[iSelectedIdx], "");
//							}else{
//								rValue = StringHelper.evl( ghs[j].getComboHiddenValues()[iSelectedIdx], "");
//								hValue = StringHelper.evl( ghs[j].getComboValues()[iSelectedIdx], "");							
//							}
//						}else{
//							rValue = "";
//							hValue = "";
//						}
//							
//					}
//					else {
//						rValue = StringHelper.evl(ghs[j].getValue(i), "");
//						hValue = StringHelper.evl(ghs[j].getHiddenValue(i), "");
//					}
//
//					jDto.addField((isUpperKey)?key.toUpperCase():key, rValue);
//				}
//				//수정자 ,등록자 SETTING
//				
//				szCRUD = nvl(jDto.getField("CRUD"),"");
//				
//				if("C".equals(szCRUD))
//				{
//					jDto.setField("REGISTER",szUserId);
//				}else if("U".equals(szCRUD)){
//					jDto.setField("MODIFIER",szUserId);
//				}else {					
//				}
//				jDto.setField("YD_USER_ID",szUserId);
//				
//				if(!szydEqpId.equals("")){
//					jDto.setField("YD_EQP_ID",szydEqpId);
//				}
//				  
//				jdtoAl[i] = jDto;
//			}
//		}
//
//		logger.println(LogLevel.DEBUG,   "========== JDTORecord START ===========");
//		for(int ss=0;ss<jdtoAl.length;ss++){
//			logger.println(LogLevel.DEBUG,   jdtoAl[ss].toString());
//		}
//		logger.println(LogLevel.DEBUG,   "========== JDTORecord END ===========");
//
//		return jdtoAl;
//	}			

	
	
//---------------------------------------------------------------------------------
} // end of class
