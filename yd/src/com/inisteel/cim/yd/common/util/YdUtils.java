/*
 * @(#) 야드공통 UTIL
 *
 * @version         V1.00
 * @author          야드공통
 * @date            모름
 *
 * @description     야드공통 UTIL
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.01  2012/12/03   김현우      김현우       strToFloat 메서드 추가  
 */

package com.inisteel.cim.yd.common.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordImplMap;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.mail.MailSender;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;

import com.inisteel.cim.yd.common.util.YdDaoUtils;



import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jsp.common.CmnUtil;
import com.metis.rapi4j.ResultData;

import flex.messaging.MessageBroker;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.util.UUIDUtils;


public class YdUtils {

    private String szSessionName =getClass().getName();


//  boolean bDebugFlag=true;

    private boolean bDebugFlag=false;
    private Logger logger =new Logger("yd");

    // 전사물류개선 2021. 1.6
    // 클래스 생성자에서 로그아이디를 생성한다.
    private String logId = "";
    public YdUtils(){
        this.setLogId("<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">");
    }
    private void setLogId(String logId){
        this.logId = logId;
    }
    public String getLogId(){
        return this.logId;
    }

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

        Calendar cal    = Calendar.getInstance();

        cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - 6);

        Date    date    = cal.getTime();

        SimpleDateFormat sdf    = new SimpleDateFormat("yyyyMMdd");

        String sDate    = sdf.format(date);

        return sDate;
    }

    /**
     * 현재 시점을 기준으로 계상일자를 구하는 메소드(7시기준)
     * @return String
     */
    public static String getDefaultHdsDate7() {

        Calendar cal    = Calendar.getInstance();

        cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - 7);

        Date    date    = cal.getTime();

        SimpleDateFormat sdf    = new SimpleDateFormat("yyyyMMdd");

        String sDate    = sdf.format(date);

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

//PIDEV
            // RABBIT MQ
            if(szRcvTcCode == null){
                szRcvTcCode=inRecord.getFieldString("MQ_TC_CD");
            }

            if(szRcvTcCode == null){
                szRcvTcCode="";

            }   // end if

            szRcvTcCode=szRcvTcCode.trim();
            szRcvTcCode=szRcvTcCode.toUpperCase();

        }catch(Exception e){
            szMsg=szMethodName+" Exception Error : "+e.getMessage();
            this.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return null;
        } // end of try-catch

        return szRcvTcCode;


    } // end of getTcCode();




    /**
     * 오퍼레이션명 : putLog
     *
     * @param String szClassName    // Logging 요청 Class name
     *        String szMethodName   // Logging 요청 Method Name
     *        String szLogMsg       // Logging Message
     * @return
     * @throws DAOException, JDTOException
     */
    public void putLog(String szClassName, String szMethodName, String szLogMsg, int nLogLevel)  {

        String szMsg="";
        String strCurDate = YdUtils.getCurDate("yyyy-MM-dd HH:mm:ss:SSS");

        szMsg = szClassName + "::" + szMethodName +"() " + "\n\t"+this.getLogId()+szLogMsg;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 로그 개선 적용 전문중  기존 putLog 사용하는 부분이 있는지 체크
//        System.out.println("[******putLog] " + szMsg);
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

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
                    logger.println(LogLevel.ERROR, this, szMsg);
                    //logger.println(LogLevel.DEBUG, this, szMsg);
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
            //  System.out.println(szMsg);
            //else
                logger.println(LogLevel.ERROR, this, szMsg);

        } // end of try-catch()

    } // end of putLog();




    /**
     *      [A] 오퍼레이션명 : fillSpZr
     *
     * @param String szData         // 변환대상 문자열
     *        int    nLen           // 변환 후 목적 문자열 길이
     *        int    nChgMd         // 변환 방식 (0: 숫자열변환, !0: 문자열변환
     * @return String               // 변환 완료 된 문자열
     * @throws
     */
    public static String fillSpZr(String szData, int nLen, int nChgMd){

        String szFillData="";
        int i=0;
        int nDataLen =0;


        try{
            szFillData= szData.trim();
            nDataLen =szFillData.length();
            if (nDataLen >=nLen)
                return szFillData.substring(0, nLen);

            for(i=nDataLen; i<nLen; i++){
                if(nChgMd==0)
                    szFillData="0"+szFillData;
                else
                    szFillData+=" ";

            } // end of for()

        }catch(Exception e){
            for(i=0;i<nLen;i++){
                if(nChgMd==0) szFillData="0"+szFillData;
                else          szFillData+=" ";

            } // end of for();

        } // end of try-catch

        return szFillData;

    } // end of fillSpZr()




    /**
     * 오퍼레이션명 : RuleData to JDTORecord Converter
     *
     * @param   String szRuleName   // Rule Name
     * @param   String szItems[]    // Rule Item List
     * @param   Hashtable htRule    // Source Hashtable
     * @param   JDTORecord jdtoRec  // Target JDTORecrd
     * @return  Converted JDTORecord
     * @throws  JDTOException
     */
    public boolean cvtTblToRec(String szRuleName, String szItems[],
            Hashtable htRule, JDTORecord jdtoRec, String szClassName) {

        String szMsg="";
        String szMethodName="cvtTblToRec";

        YdUtils ydUtils = new YdUtils();

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
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

            return false;

        }// end of try


        //
        // Debugging 용
        //
        szMsg="Rule Query Successfully";
        ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);


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

        YdTcConst ydTcConst =new YdTcConst();

        int nRtc=0;

        String szMsg="";
        String szRcvTcCode="";
        String szTcUniqId="";


        try{

            //
            // 수신 메시지의 인터페이스 Unique ID Check
            szTcUniqId =inRecord.getFieldString("UNIQUE_ID");
            if( szTcUniqId==null){
                szTcUniqId="";
            }


            //
            // 수신메시지의 TC 유효성 검사
            //
            szRcvTcCode=this.getTcCode(inRecord);
            if(szRcvTcCode==null){
                szMsg ="["+szTcUniqId+"] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
                this.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                return false;
            }



            //
            // 수신 메시지 로깅
            //
            szMsg="["+szTcUniqId+"] 전문수신 : TCCODE=" +szRcvTcCode;
            putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



            // 수신 Tc Check
            nRtc=ydTcConst.chkTcType(szRcvTcCode);

            switch(nRtc){

            case 1:

                // 내부 인터페이스 TC 수신
                szMsg="내부인터페이스 TC 수신 : " + szRcvTcCode;
                this.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                break;

            case 2:

                // 리모트 인터페이스 TC 수신
                szMsg="리모트인터페이스 TC 수신 : " + szRcvTcCode;
                this.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                break;

            case 3:

                // L2 인터페이스 TC 수신
                szMsg="L2 인터페이스 TC 수신 : " + szRcvTcCode;
                this.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                break;

            default:

                // Unknown TC 수신
                szMsg="Unknown TC Error : " + szRcvTcCode + " ErrCode="+nRtc;
                this.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                return false;

            } // end of switch()




            //
            // TC Code vs Method Check
            //
            if( !(ydTcConst.chkTcMethod(szRcvTcCode, szMethodName)) ){
                szMsg="Unknown TC Method TCCode="+szRcvTcCode+" MethodName="+szMethodName;
                this.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

                return false;

            } // end of if()


        }catch (Exception e){
            szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
            this.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

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
        nRecCnt =objTemp.length;
        String[] szaKeys =new String[nRecCnt];
        for(int i=0; i<nRecCnt; i++)
            szaKeys[i]=objTemp[i].toString();

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

                if(szKey.equals(szRecKey))
                    continue;


                outRecord.setField(szRecKey, szValue);

            } catch (JDTOException e) {
                szMsg="Exception Error : "+e.getLocalizedMessage();
                this.putLog(szMsg, szSessionName, szMethodName, 1);
                e.printStackTrace();


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
    public int disyRec(JDTORecord inRecord) {
        int nRecCnt=-1;

        Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
        nRecCnt =objTemp.length;
        String szRecKey="";
        String szValue="";
        String szMsg="";

        for(int i=0; i<nRecCnt; i++){
            szRecKey =objTemp[i].toString();
            szValue =inRecord.getFieldString(szRecKey);

            if(szValue==null)
                szValue="(null)";

            szMsg= "["+(i+1)+"]"
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
    public String makeRec2Str(JDTORecord inRecord)  {


        Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
        int nItemCnt =objTemp.length;

        String szItemKey="";
        String szValue="";
        String szRecMsg="";
//      String szMsg="";

        for(int i=0; i<nItemCnt; i++){
            szItemKey =objTemp[i].toString();
            szValue =inRecord.getFieldString(szItemKey);

            if(szValue==null)
                szValue="";

            szRecMsg+=szValue;

            //
            // Debug MSG
            // 2012-11-14 (김현우) 표준화 지적사항 반영으로 주석처리 및 logger.println 로 변경
            //System.out.println(szRecMsg);
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
    public int chgRecSet2Rec(JDTORecordSet inRecSet, JDTORecord outRec) {

        String szMsg="";
        String szMethodName="chgRecSet2Rec";

        int nRecCnt =0;


        try{


            if( nRecCnt <=0)
                return -1;

            if( !inRecSet.isFirst())
                inRecSet.first();

            for(int i=1;i<=nRecCnt;i++){

                outRec.setField(""+i, inRecSet.getRecord());
                inRecSet.next();

            } // end of for()


        } catch(Exception e){
            szMsg=szMethodName+" Exfeption Error : "+ e.getLocalizedMessage();
            this.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

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

        try{
            templen = len - str.getBytes().length;
            if(templen >= 0){
                for(int i=0; i<templen; i++)
                    str = pad + str;
                result = str;
            }
        }catch(Exception e){
            this.putLog(szSessionName, szMethodName, "FloatLRPAD() : " + e.toString() + " : " + e.getMessage(), 4);
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
            templen = len - str.getBytes().length;
            if(templen >= 0){
                for(int i=0; i<templen; i++)
                    str = str + pad;
                result = str;
            }
        }catch(Exception e){
            this.putLog(szSessionName, szMethodName, "FloatLRPAD() : " + e.toString() + " : " + e.getMessage(), 4);
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
    public String FloatLRPAD(String strOrg, int nTotal, int nFloat, char ch) throws Exception
    {
        String szMethodName = "FloatLRPAD";
        String strTemp1 = "";
        String strTemp2 = "";
        int nJisu = nTotal - nFloat;
        int nSosu = nFloat;

        try{
            if(strOrg == null || "".equals(strOrg.trim()))
                return addLeftStr("", nTotal, (char)ch);

            int nIdx = strOrg.indexOf(".");
            if(nIdx <= 0){
                strTemp1 = this.addLeftStr(strOrg.trim(), nJisu, (char)ch);
                strTemp2 = this.addRightStr("0", nSosu, (char)ch);
                if(strTemp1.trim().equals("")){
                    return null;
                }

            }else {
                String[] strSplit = strOrg.trim().split("\\.");

                strTemp1 = this.addLeftStr(strSplit[0], nJisu, (char)ch);
                strTemp2 = this.addRightStr(strSplit[1], nSosu, (char)ch);

                if(strTemp1.equals("") || strTemp2.equals("")){
                    return null;
                }
            }
        }catch(Exception e){
            this.putLog(szSessionName, szMethodName, "FloatLRPAD() : " + e.toString() + " : " + e.getMessage(), 4);
        }finally{
        }

        return  strTemp1 + strTemp2;
    }






    /**
     * JDTORecord의 내용을 Key값=데이터값의 형식으로 표시한다.
     *  2009.05.19 권오창
     * @param inRecord
     * @return
     */
    public int disyRecInfo(JDTORecord inRecord) {
        int nRecCnt=-1;

        Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
        nRecCnt =objTemp.length;
        String szRecKey="";
        String szValue="";
        String szMsg="";

        for(int i=0; i<nRecCnt; i++){
            szRecKey =objTemp[i].toString();
            szValue =inRecord.getFieldString(szRecKey);

            if(szValue==null)
                szValue="(null)";

            szMsg= "["+(i+1)+"]"
                 + "\t"+szRecKey
                 + "\t["+szValue+"]";

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
    public int displayRecord(String szOperationName, JDTORecord inRecord)   {
        /*
         * 2010.04.14 윤재광 : 로그정보가 너무많아 임시처리
         */
        int nRecCnt = 0;

        if(false){
            Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
            nRecCnt =objTemp.length;
            String szRecKey="";
            String szValue="";
            String szMsg="";

            for(int i=0; i<nRecCnt; i++){
                szRecKey =objTemp[i].toString();
                szValue =inRecord.getFieldString(szRecKey);

                if(szValue==null)
                    szValue="(null)";

                szMsg = szOperationName + " ["+(i+1)+"]" + szRecKey + " = ["+szValue+"]";

                logger.println(LogLevel.DEBUG, this, szMsg);
            } // end of for()
        }
        return nRecCnt;
    }





    /**
     * 문자열에 한글이 포함이 되어 있는지 검사하는 메서드
     *
     * 2009.08.06 권오창
     * @param szData
     * @return boolean
     */
    public boolean IsInclude_Hangul(String szData){
        String szFillData = "";
        boolean bResult  = false;

        szFillData = szData.trim();

        for(int i=0; i<szFillData.length(); i++){
            if(Character.getType(szFillData.charAt(i)) == 5)
                bResult = true;
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
    public String IncreaseStrToInt(String strTemp, int nIncreaseCnt, int nDigit) throws Exception{
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
     *         String szEJBId          // Logging 요청 Class name
     *         String szMsgName        // Logging 요청 Method Name
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
            if (desti.equals("")) {
                desti = YdConstant.YD_MONITORING_CHANNEL;
            }

            if (szYdGp.equals("")) {
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

            putLogToMonitoring(desti, szEJBId, szMsgName, szLogMsg, szYdEvtGp ,szYdGp );

            return;


        } catch(Exception e) {
            szMsg =szSessionName + "::" + szMethodName +"() " + "\n\t"+szMethodName+" Exception Error : "+e.getLocalizedMessage();
            if(bDebugFlag)
                System.out.println(szMsg);
            else
                logger.println(LogLevel.ERROR, this, szMsg);
        }
        return;
        */
    }



    /**
     * 오퍼레이션명 : putLogToMonitoring
     *
     * @param String desti          // Monitoring Channel
     *        String szClassName    // Logging 요청 Class name
     *        String szMethodName   // Logging 요청 Method Name
     *        String szLogMsg       // Logging Message
     *        String szYdEvtGp      // 야드 Event 구분 (C:크레인, Q:설비, E:에러, W:경고, I:정보, Z:기타)
     * @return
     * @throws DAOException, JDTOException
     */
    public void putLogToMonitoring(String desti, String szClassName, String szMethodName, String szLogMsg, String szYdEvtGp ,String szYdGp)  {

        String szMsg="";
//      String strCurDate = YdUtils.getCurDate("yyyy-MM-dd HH:mm:ss:SSS");

        szMsg =szClassName + "::" + szMethodName +"() " + "\n\t"+szLogMsg;
        HashMap hmap = new HashMap();

        try{
            if (szYdEvtGp.equals(YdConstant.YD_EVT_CRANE)){
                szMsg="[크레인] "+szMsg;
            }else if(szYdEvtGp.equals(YdConstant.YD_EVT_EQP)){
                szMsg="[설비] "+szMsg;
            }else if(szYdEvtGp.equals(YdConstant.YD_EVT_ERROR)){
                szMsg="[에러] "+szMsg;
            }else if(szYdEvtGp.equals(YdConstant.YD_EVT_WARNING)){
                szMsg="[경고] "+szMsg;
            }else if(szYdEvtGp.equals(YdConstant.YD_EVT_INFO)){
                szMsg="[정보] "+szMsg;
            }else if(szYdEvtGp.equals(YdConstant.YD_EVT_ETC)){
                szMsg="[기타] "+szMsg;
            }
            /*
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
            */

            hmap.put("MSG_GP", szYdEvtGp);
            hmap.put("YD_MSG", szMsg);
            hmap.put("YD_GP", szYdGp);

            //FLAX_PUSH destid 지정 /////////////////////////////////////////////////
            if(szYdGp.equals("A")){
                desti = YdConstant.YD_MONITORING_CHANNEL_A;
            }else if(szYdGp.equals("D")){
                desti = YdConstant.YD_MONITORING_CHANNEL_D;
            }else if(szYdGp.equals("K")){
                desti = YdConstant.YD_MONITORING_CHANNEL_K;
            }else if(szYdGp.equals("T")){
                desti = YdConstant.YD_MONITORING_CHANNEL_T;
            }else if(szYdGp.equals("H")){
                desti = YdConstant.YD_MONITORING_CHANNEL_H;
            }else if(szYdGp.equals("J")){
                desti = YdConstant.YD_MONITORING_CHANNEL_J;
            }else if(szYdGp.equals("S")){
                desti = YdConstant.YD_MONITORING_CHANNEL_S;
            }else {
                desti = YdConstant.YD_MONITORING_CHANNEL_A;
            }
            /////////////////////////////////////////////////////////////////////////

            //FLAX_PUSH사용여부 체크 /////////////////////////////////////////////////
            JDTORecord recPara =  JDTORecordFactory.getInstance().create();
            JDTORecord recInTemp =  JDTORecordFactory.getInstance().create();
            String CHK ="N";
            JDTORecordSet outRecSet = null;
            YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

            outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
            recPara =  JDTORecordFactory.getInstance().create();
            recPara.setField("YD_GP",szYdGp);
            /*com.inisteel.cim.yd.common.util.YdUtils.chklist*/
            int intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 999);

            if( intRtnVal > 0 ) {
                outRecSet.first();
                recInTemp = outRecSet.getRecord();
                CHK = recInTemp.getFieldString("CHK").trim();
            }

            logger.println(LogLevel.INFO, this, szYdGp+":야드 FLAX PUSH 사용유무:"+CHK+",destID:"+desti);
            /////////////////////////////////////////////////////////////////////////
            if(CHK.equals("Y")){
                pushToFlexClient(desti, hmap);
            }
        }catch (Exception e){

            szMsg =szClassName + "::" + szMethodName +"() " + "\n\t"+szMethodName+" Exception Error : "+e.getLocalizedMessage();
            // 2012-11-14 (김현우) 표준화 지적사항 반영으로 주석처리
            //if(bDebugFlag)
            //  System.out.println(szMsg);
            //else
                logger.println(LogLevel.ERROR, this, szMsg);

        } // end of try-catch()

    } // end of putLogToMonitoring();



    /**
     * 오퍼레이션명 : 크레인 위치 정보 전송(Flex)
     *
     * @param String destid         // Monitoring Channel
     *        String szYdGp         // 야드구분
     *        String szCrnName      // 크레인 설비명
     *        int intPosX           // 크레인 현 X 좌표
     *        int intPosY           // 크레인 현 Y 좌표
     * @return
     * @throws DAOException, JDTOException
     */
    public void putYdFlexCrnPos(String destid, String szYdGp , String szCrnName , int intPosX, int intPosY)  {

        /*
         * 1. 기능 : 크레인 위치 정보를 받아 선택된 채널 정보에 위치정보를 전송하는 기능
         * 2. 작성자  : 이현성
         * 3. 작성일시 : 2009.12.31
         *
         */

        String szMsg="";
        HashMap hmap = new HashMap();
        String szOperationName = "크레인 위치 정보 전송(Flex)";
        String szMethodName = "putYdFlexCrnPos";

        try{

            if(destid.equals("")){
                destid = YdConstant.YD_MONITORING_CHANNEL_A;
            }

            hmap.put("MSG_GP", YdConstant.YD_EVT_CRANE);  // Flex 크레인 좌표받는 메세지 구분
            hmap.put("YD_GP", szYdGp);
            hmap.put("YD_EQP_ID", szCrnName);
            hmap.put("YD_POS_X", new Integer(intPosX));
            hmap.put("YD_POS_Y", new Integer(intPosY));

            szMsg   = "[YdUtils : "+szOperationName+"] 채널 전송";
            this.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


            //FLAX_PUSH destid 지정 /////////////////////////////////////////////////
            if(szYdGp.equals("A")){
                destid = YdConstant.YD_MONITORING_CHANNEL_A;
            }else if(szYdGp.equals("D")){
                destid = YdConstant.YD_MONITORING_CHANNEL_D;
            }else if(szYdGp.equals("K")){
                destid = YdConstant.YD_MONITORING_CHANNEL_K;
            }else if(szYdGp.equals("T")){
                destid = YdConstant.YD_MONITORING_CHANNEL_T;
            }else if(szYdGp.equals("H")){
                destid = YdConstant.YD_MONITORING_CHANNEL_H;
            }else if(szYdGp.equals("J")){
                destid = YdConstant.YD_MONITORING_CHANNEL_J;
            }else if(szYdGp.equals("S")){
                destid = YdConstant.YD_MONITORING_CHANNEL_S;
            }else {
                destid = YdConstant.YD_MONITORING_CHANNEL_A;
            }
            /////////////////////////////////////////////////////////////////////////

            //FLAX_PUSH사용여부 체크 /////////////////////////////////////////////////
            JDTORecord recPara =  JDTORecordFactory.getInstance().create();
            JDTORecord recInTemp =  JDTORecordFactory.getInstance().create();
            String CHK ="N";
            JDTORecordSet outRecSet = null;
            YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

            outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
            recPara =  JDTORecordFactory.getInstance().create();
            recPara.setField("YD_GP",szYdGp);
            /*com.inisteel.cim.yd.common.util.YdUtils.chklist*/
            int intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 999);

            if( intRtnVal > 0 ) {
                outRecSet.first();
                recInTemp = outRecSet.getRecord();
                CHK = recInTemp.getFieldString("CHK").trim();
            }

            logger.println(LogLevel.INFO, this, szYdGp+":야드 FLAX PUSH 사용유무:"+CHK+",destID:"+destid);
            /////////////////////////////////////////////////////////////////////////
            if(CHK.equals("Y")){
            pushToFlexClient(destid, hmap);
            }

        }catch (Exception e){

            szMsg   = "[YdUtils : "+szOperationName+"] Exception Error : "+ e.getLocalizedMessage();
            this.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

        } // end of try-catch()

    } // end of putYdCrnPos

    /**
     * 오퍼레이션명 : 크레인 위치 정보 전송(Flex)
     *
     * @param String destid         // Monitoring Channel
     *        String szYdGp         // 야드구분
     *        String szCrnName      // 크레인 설비명
     *        int intPosX           // 크레인 현 X 좌표
     *        int intPosY           // 크레인 현 Y 좌표
     * @return
     * @throws DAOException, JDTOException
     */
    public void putYdCrnPosMult(String destid, String szYdGp , JDTORecord msgRecord)  {

        /*
         * 1. 기능 : 크레인 위치 정보를 받아 선택된 채널 정보에 위치정보를 전송하는 기능
         * 2. 작성자  : 이현성
         * 3. 작성일시 : 2009.12.31
         *
         */

        String szMsg="";

        String szYD_EQP_ID="";
        String szYD_CRN_XAXIS="";
        String szYD_CRN_YAXIS="";

        HashMap hmap = new HashMap();
        YdDaoUtils ydDaoUtils = new YdDaoUtils();
        String szOperationName = "크레인 위치 정보 멀티전송(Flex)";
        String szMethodName = "putYdCrnPosMult";
        try{

            if(destid.equals("")){
                destid = YdConstant.YD_MONITORING_CHANNEL_A;
            }

            logger.println(LogLevel.INFO, this, szYdGp+":크레인현재위치 화면처리 위한 호출");

            hmap.put("MSG_GP", YdConstant.YD_EVT_CRANE);  // Flex 크레인 좌표받는 메세지 구분
            hmap.put("YD_GP", szYdGp);


            for(int i=0; i<20; i++){
                szYD_EQP_ID    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID" + (i+1));
                szYD_CRN_XAXIS = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS" + (i+1));
                szYD_CRN_YAXIS = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS" + (i+1));

                // 존재하는 설비ID에 대한 위치정보 Flex화면에서 보여지기 위한 호출
                if(!szYD_EQP_ID.trim().equals("")){
                    hmap.put("YD_EQP_ID"+ (i+1), szYD_EQP_ID);
                    hmap.put("YD_POS_X"+ (i+1), new Integer(szYD_CRN_XAXIS));
                    hmap.put("YD_POS_Y"+ (i+1), new Integer(szYD_CRN_YAXIS));
                }
            }





            szMsg   = "[YdUtils : "+szOperationName+"] 채널 전송";
            this.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


            //FLAX_PUSH destid 지정 /////////////////////////////////////////////////
            if(szYdGp.equals("A")){
                destid = YdConstant.YD_MONITORING_CHANNEL_A;
            }else if(szYdGp.equals("D")){
                destid = YdConstant.YD_MONITORING_CHANNEL_D;
            }else if(szYdGp.equals("K")){
                destid = YdConstant.YD_MONITORING_CHANNEL_K;
            }else if(szYdGp.equals("T")){
                destid = YdConstant.YD_MONITORING_CHANNEL_T;
            }else if(szYdGp.equals("H")){
                destid = YdConstant.YD_MONITORING_CHANNEL_H;
            }else if(szYdGp.equals("J")){
                destid = YdConstant.YD_MONITORING_CHANNEL_J;
            }else if(szYdGp.equals("S")){
                destid = YdConstant.YD_MONITORING_CHANNEL_S;
            }else {
                destid = YdConstant.YD_MONITORING_CHANNEL_A;
            }
            /////////////////////////////////////////////////////////////////////////

            //FLAX_PUSH사용여부 체크 /////////////////////////////////////////////////
            JDTORecord recPara =  JDTORecordFactory.getInstance().create();
            JDTORecord recInTemp =  JDTORecordFactory.getInstance().create();
            String CHK ="N";
            JDTORecordSet outRecSet = null;
            YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

            outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
            recPara =  JDTORecordFactory.getInstance().create();
            recPara.setField("YD_GP",szYdGp);
            /*com.inisteel.cim.yd.common.util.YdUtils.chklist*/
            int intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 999);

            if( intRtnVal > 0 ) {
                outRecSet.first();
                recInTemp = outRecSet.getRecord();
                CHK = recInTemp.getFieldString("CHK").trim();
            }

            logger.println(LogLevel.INFO, this, szYdGp+":야드 FLAX PUSH 사용유무:"+CHK+",destID:"+destid);
            /////////////////////////////////////////////////////////////////////////
            if(CHK.equals("Y")){
            pushToFlexClient(destid, hmap);
            }

        }catch (Exception e){

            szMsg   = "[YdUtils : "+szOperationName+"] Exception Error : "+ e.getLocalizedMessage();
            this.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

        } // end of try-catch()

    } // end of putYdCrnPosMult

    /**
     * 오퍼레이션명 : 크레인 작업 실적 위치 및 설비 전송
     *
     * @param String destid         // Monitoring Channel
     *        String szYdGp         // 야드구분
     *        String szCrnName      // 크레인 설비명
     *        int intPosX           // 크레인 현 X 좌표
     *        int intPosY           // 크레인 현 Y 좌표
     * @return
     * @throws DAOException, JDTOException
     */
    public void putYdFlexCrnWrk(String destid, JDTORecord param)  {


            String szMsg="";
            HashMap hmap = new HashMap();

            HashMap subhmap = new HashMap();
            String szOperationName = "크레인 작업 실적 위치 및 설비 전송";
            String szMethodName = "putYdFlexCrnWrk";
            String szEqpId = "";
            String szFromStkPos = "";
            String szToStkPos = "";
            String szYdGp = "";
            JDTORecord recPara =  JDTORecordFactory.getInstance().create();
            JDTORecordSet outRecSet = null;
            int intRtn = 0;

            YdDaoUtils ydDaoUtils = new YdDaoUtils();
            YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

            YdEqpDao ydEqpDao = new YdEqpDao();


            try{

                this.displayRecord(szOperationName, param);

                szYdGp = ydDaoUtils.paraRecChkNull(param, "YD_GP");
                szEqpId = ydDaoUtils.paraRecChkNull(param, "YD_EQP_ID");
                szFromStkPos = ydDaoUtils.paraRecChkNull(param, "YD_UP_WR_LOC");
                szToStkPos = ydDaoUtils.paraRecChkNull(param, "YD_DN_WR_LOC");

                destid = YdConstant.YD_MONITORING_CHANNEL_A;


                hmap.put("MSG_GP", YdConstant.YD_EVT_FUN);  //  야드 실적정보받아 처리 ('F')
                hmap.put("YD_GP", szYdGp );
                hmap.put("YD_EQP_ID", szEqpId);

                hmap.put("YD_UP_WR_LOC", szFromStkPos);
                hmap.put("YD_DN_WR_LOC", szToStkPos);

                szMsg   = "[YdUtils : "+szOperationName+"] 채널 전송";
                this.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


                if(szFromStkPos.length()==8){
                    outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
                    recPara =  JDTORecordFactory.getInstance().create();
                    recPara.setField("YD_STK_COL_GP",szFromStkPos.substring(0,6));
                    recPara.setField("YD_STK_BED_NO",szFromStkPos.substring(6,8));
                    intRtn = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 94);
                    szMsg   = "[YdUtils : "+szOperationName+"]  UP 조회건수 :" + intRtn +"건";
                    this.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    hmap.put("YD_UP_WR_LOC_ARR" , CmnUtil.listJdtoRecordTohashMap(outRecSet.toList()));

                }


                if(szToStkPos.length()==8){
                    outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
                    recPara =  JDTORecordFactory.getInstance().create();
                    recPara.setField("YD_STK_COL_GP",szToStkPos.substring(0,6));
                    recPara.setField("YD_STK_BED_NO",szToStkPos.substring(6,8));
                    intRtn = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 94);


                    szMsg   = "[YdUtils : "+szOperationName+"] DN 조회건수 :" + intRtn +"건";
                    this.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                    hmap.put("YD_DN_WR_LOC_ARR" , CmnUtil.listJdtoRecordTohashMap(outRecSet.toList()));

                }

                if(szEqpId.length() == 6){
                    outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
                    recPara =  JDTORecordFactory.getInstance().create();
                    //recPara.setField("YD_GP",szEqpId.substring(0,1));
                    recPara.setField("YD_EQP_ID",szEqpId);
                    intRtn = ydEqpDao.getYdEqp(recPara, outRecSet, 16);
                    szMsg   = "[YdUtils : "+szOperationName+"]크레인 조회건수 " + intRtn +"건";
                    this.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    hmap.put("YD_EQP_ID_ARR" , CmnUtil.listJdtoRecordTohashMap(outRecSet.toList()));
                }

                //FLAX_PUSH destid 지정 /////////////////////////////////////////////////
                if(szYdGp.equals("A")){
                    destid = YdConstant.YD_MONITORING_CHANNEL_A;
                }else if(szYdGp.equals("D")){
                    destid = YdConstant.YD_MONITORING_CHANNEL_D;
                }else if(szYdGp.equals("K")){
                    destid = YdConstant.YD_MONITORING_CHANNEL_K;
                }else if(szYdGp.equals("T")){
                    destid = YdConstant.YD_MONITORING_CHANNEL_T;
                }else if(szYdGp.equals("H")){
                    destid = YdConstant.YD_MONITORING_CHANNEL_H;
                }else if(szYdGp.equals("J")){
                    destid = YdConstant.YD_MONITORING_CHANNEL_J;
                }else if(szYdGp.equals("S")){
                    destid = YdConstant.YD_MONITORING_CHANNEL_S;
                }else {
                    destid = YdConstant.YD_MONITORING_CHANNEL_A;
                }
                /////////////////////////////////////////////////////////////////////////

                //FLAX_PUSH사용여부 체크 /////////////////////////////////////////////////
                //JDTORecord recPara =  JDTORecordFactory.getInstance().create();
                JDTORecord recInTemp =  JDTORecordFactory.getInstance().create();
                String CHK ="N";
                //JDTORecordSet outRecSet = null;
                //YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

                outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
                recPara =  JDTORecordFactory.getInstance().create();
                recPara.setField("YD_GP",szYdGp);
                /*com.inisteel.cim.yd.common.util.YdUtils.chklist*/
                int intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 999);

                if( intRtnVal > 0 ) {
                    outRecSet.first();
                    recInTemp = outRecSet.getRecord();
                    CHK = recInTemp.getFieldString("CHK").trim();
                }

                logger.println(LogLevel.INFO, this, szYdGp+":야드 FLAX PUSH 사용유무:"+CHK+",destID:"+destid);
                /////////////////////////////////////////////////////////////////////////
                if(CHK.equals("Y")){
                this.pushToFlexClient(destid, hmap);
                }


            }catch (Exception e){

                szMsg   = "[YdUtils : "+szOperationName+"] Exception Error : "+ e.getLocalizedMessage();
                this.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            } // end of try-catch()


    } // end of putYdFlexCrnWrk




    /**
     *
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param desti
     * @param pushData
     */
    public void pushToFlexClient(String desti,Object pushData) {

        try {

            /*
             *
             *  *************************************************
                    import flex.messaging.MessageBroker;
                    import flex.messaging.messages.AsyncMessage;
                    import flex.messaging.util.UUIDUtils;
             *
             *      : 상위 3개의 클래스를 import 해줘야합니다.
             *  ***************************************************
             *
             *
             *  PushData  : 보내는 곳에서는 Map형식으로 전송해 줘야 합니다.
             *  desti     : 목적 id 이므로 함수내에서 지정해서 사용해도 무방함.
             *
             *
             *
             * *******************************************************************************
             *
             *
             *
             *
             * 파일 위치 : \hsteelApp\hsteelWeb\WEB-INF\flex\messaging-config.xml
                 <destination id="yd_monitor">
                    <properties>
                        <network>
                            <session-timeout>0</session-timeout>
                        </network>
                        <server>
                            <max-cache-size>1000</max-cache-size>
                            <message-time-to-live>0</message-time-to-live>
                            <durable>false</durable>
                            <cluster-message-routing>server-to-server</cluster-message-routing>
                        </server>
                        <jms>
                            <destination-type>Topic</destination-type>
                            <message-type>javax.jms.ObjectMessage</message-type>
                            <connection-factory>weblogic.jms.ConnectionFactory</connection-factory>
                            <destination-jndi-name>jms/FLEX_TOPIC</destination-jndi-name>
                            <delivery-mode>NON_PERSISTENT</delivery-mode>
                            <message-priority>DEFAULT_PRIORITY</message-priority>
                            <acknowledge-mode>AUTO_ACKNOWLEDGE</acknowledge-mode>
                            <transacted-sessions>false</transacted-sessions>
                            <max-producers>1</max-producers>
                        </jms>
                    </properties>
                    <adapter ref="jms" />

                </destination>
             *
             *
             * : 상위의 부분중    destination id="++++++++++" :  사용하실 명으로 넣으시고
             *  JMS 서버 담당자에게 추가 요청 하셔야 합니다.
             *
             *
             *
             * ******************************************************************************************
             *
             */


            MessageBroker msgBroker = MessageBroker.getMessageBroker(null);
            String cliendID = UUIDUtils.createUUID(false);
            AsyncMessage msg = new AsyncMessage();

            msg.setDestination(desti);
            msg.setClientId(cliendID);
            msg.setMessageId( UUIDUtils.createUUID(false));
            msg.setTimestamp(System.currentTimeMillis());


            msg.setBody(pushData);

            msgBroker.routeMessageToService(msg, null);

            logger.println(LogLevel.INFO, this, "pushToFlexClient SEND COUNT>>ydUtil>>pushToFlexClient!!");
        } catch(Exception e) {
            // Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
            // 2012-11-14 (김현우) 표준화 지적사항 반영으로 주석처리 및 logger.println 로 변경
            //  System.out.println("pushToFlexClient exception!!");
            logger.println(LogLevel.ERROR, this, "pushToFlexClient exception!!");

                //throw new DAOException(getClass().getName() + e.getMessage());
        }
    }


    /**
     *  공백이나 0 채워넣는 함수에서 한글 2바이트 문제 처리  ㅡ.ㅡ
     *  2009.07.01 권오창
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param szData, nLen, nChgMd
     * @return String
     */
    public static String fillSpZr_KOR(String szData, int nLen, int nChgMd){

        String szFillData = "";
        int nDataLen      = 0;
        int nCntKOR       = 0;
        int nCntEXP       = 0;
        int i             = 0;
        YdUtils         ydUtils     = new YdUtils();
        try{
            szFillData = szData.trim();

            for(i=0; i<szFillData.length(); i++){

                ydUtils.putLog("YdUtils", "fillSpZr_KOR", "Character!!"+Character.getType(szFillData.charAt(i)), JPlateYdConst.DEBUG);
                if(Character.getType(szFillData.charAt(i)) == 5 || Character.getType(szFillData.charAt(i)) == 28)
                    nCntKOR++;
                else
                    nCntEXP++;
            }

            nDataLen = szFillData.length() + nCntKOR;
            if(nDataLen > nLen)
                return cutString(szFillData, nLen);

            for(i=nDataLen; i<nLen; i++){
                if(nChgMd == 0)
                    szFillData = "0" + szFillData;
                else
                    szFillData += " ";
            } // end of for()

        }catch(Exception e){
            for(i=0;i<nLen;i++){
                if(nChgMd==0)
                    szFillData = "0" + szFillData;
                else
                    szFillData += " ";
            } // end of for();

        } // end of try-catch

        return szFillData;
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
     * @param   obj         : 와이즈그리드 Object
     *          hTitle      : 와이즈그리드 헤더 TITLE
     *          comboStrArr : 와이즈그리드 콤보 Value, Text
     *          cdVal       : 콤보에 보여줄 값이 Value, Text 결정(0:Value, 1:Text, 2:Value(Text))
     *          headTextYn  : 빈칸 유무
     * @return  와이즈그리드 콤보 자바스크립트
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
                    comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + YdUtils.setEscapeStr(comboStrArr[cdVal][ii]) + "', '" + comboStrArr[0][ii] + "');\n";
                }
            } else {
                for(int ii=0; ii < comboStrArr[0].length; ii++) {
                    comboStr += obj + ".AddComboListValue('" + hTitle + "', '" +
                                        comboStrArr[0][ii] + " (" + YdUtils.setEscapeStr(comboStrArr[1][ii]) + ")', '" + comboStrArr[0][ii] + "');\n";
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
                if(bTemp[i]<0)
                    nCnt++;
            }

            if(nCnt%2!=0){
                StrSub[str_nCnt] = new String(bTemp, 0, nCutSize+1);
                bTemp = new String(bTemp, nCutSize+1, bTemp.length-(nCutSize+1)).getBytes();
            }else{
                StrSub[str_nCnt] = new String(bTemp, 0, nCutSize);
                bTemp = new String(bTemp, nCutSize, bTemp.length-nCutSize).getBytes();
            }

            str_nCnt++;
        }

        StrSub[str_nCnt] = new String(bTemp);

        return StrSub[0];
    }







    /**
     * 오퍼레이션명 : 크레인스케줄 권상 및 권하위치 제원정보 등록
     *
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public static boolean updYdCrnschBedData(JDTORecord recCrnSchId){
        YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
        YdStkBedDao ydStkBedDao = new YdStkBedDao();
        YdUtils     ydUtils     = new YdUtils();
        YdDaoUtils  ydDaoUtils  = new YdDaoUtils();
        YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
        YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
        String szMsg        = "";
        String szMethodName = "updYdCrnschBedData";
        String szOperationName      = "크레인스케줄제원정보등록";
        int intRtnVal = 0;

        JDTORecord recGetStkBedData = null;
        JDTORecord recUpdCrnSchData = null;
        JDTORecord recInPara        = null;
        JDTORecord recUpPara        = null;
        JDTORecord recDnPara        = null;
        JDTORecord recUpStkBed      = null;
        JDTORecord recDnStkBed      = null;
        JDTORecord recResultCrnwrkmtl = null;

        JDTORecordSet rsDnStkBed    = null;
        JDTORecordSet outRecSet     = null;
        JDTORecordSet rsUpStkBed    = null;
        JDTORecordSet rsGetStkLyrT  = null;
        JDTORecordSet rsResultCrnwrkmtl = null;

        String szYD_GP              = null;
        String szYD_UP_STK_COL_GP       = null;
        String szYD_UP_STK_BED_NO   = null;
        String szYD_UP_STK_LYR_NO   = null;

        String szYD_DN_STK_COL_GP       = null;
        String szYD_DN_STK_BED_NO   = null;
        String szYD_DN_STK_LYR_NO   = null;

        String szYD_CRN_SCH_ID          = null;
        String szYD_EQP_ID              = null;
        String szYD_SCH_CD              = null;

        double dblSUM_MTL_T = 0;
        String szYD_UP_WO_LOC_ZAXIS = null;
        String szYD_DN_WO_LOC_ZAXIS = null;
        String szSessionName = "ydUtils";
        try{
            //1.크레인스케줄을 조회한다.
            outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
            intRtnVal = ydCrnschDao.getYdCrnsch(recCrnSchId, outRecSet, 0);
            if(intRtnVal <= 0 ) {

            }
            outRecSet.absolute(1);
            recUpdCrnSchData = JDTORecordFactory.getInstance().create();
            recUpdCrnSchData.setRecord(outRecSet.getRecord());

            szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_CRN_SCH_ID");
            szYD_EQP_ID =  ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_EQP_ID");

            szYD_SCH_CD         = ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_SCH_CD");

            szYD_UP_STK_COL_GP = ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_UP_WO_LOC").substring(0,6);
            szYD_UP_STK_BED_NO = ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_UP_WO_LOC").substring(6,8);
            szYD_UP_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_UP_WO_LAYER");

            szYD_DN_STK_COL_GP = ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_DN_WO_LOC").substring(0,6);
            szYD_DN_STK_BED_NO = ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_DN_WO_LOC").substring(6,8);
            szYD_DN_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recUpdCrnSchData, "YD_DN_WO_LAYER");

            szYD_GP = szYD_UP_STK_COL_GP.substring(0, 1);

            //1.크레인 작업재료의 높이합을 구한다.
            rsResultCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("");
            intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recCrnSchId, rsResultCrnwrkmtl, 8);
            if(intRtnVal <= 0) {

            }
            rsResultCrnwrkmtl.absolute(1);
            recResultCrnwrkmtl = JDTORecordFactory.getInstance().create();
            recResultCrnwrkmtl.setRecord(rsResultCrnwrkmtl.getRecord());
            dblSUM_MTL_T = ydDaoUtils.paraRecChkNullDouble(recResultCrnwrkmtl, "SUM_MTL_T");


            szMsg="권상지시베드조회 전.. ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            //권상지시베드조회
            recGetStkBedData = JDTORecordFactory.getInstance().create();
            rsUpStkBed = JDTORecordFactory.getInstance().createRecordSet("");
            recGetStkBedData.setField("YD_STK_COL_GP",  szYD_UP_STK_COL_GP);
            recGetStkBedData.setField("YD_STK_BED_NO",  szYD_UP_STK_BED_NO);
            intRtnVal = ydStkBedDao.getYdStkbed(recGetStkBedData, rsUpStkBed, 0);
            if(intRtnVal <= 0){
                szMsg="updYdCrnschBedData 권상지시 베드 정보조회 중 Error!!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return false;
            }
            rsUpStkBed.absolute(1);
            recUpStkBed = JDTORecordFactory.getInstance().create();
            recUpStkBed.setRecord(rsUpStkBed.getRecord());

            szMsg="권상지시베드조회 후.. ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.displayRecord(szOperationName, recUpStkBed);

            recUpPara = JDTORecordFactory.getInstance().create();
            rsGetStkLyrT = JDTORecordFactory.getInstance().createRecordSet("");
            recUpPara.setField("YD_STK_COL_GP", szYD_UP_STK_COL_GP);
            recUpPara.setField("YD_STK_BED_NO", szYD_UP_STK_BED_NO);
            recUpPara.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(szYD_UP_STK_LYR_NO, -1));
            intRtnVal = ydStkLyrDao.getYdStklyr(recUpPara, rsGetStkLyrT, 71);
            if( intRtnVal <= 0 ) {
                szYD_UP_WO_LOC_ZAXIS = "0";
            }else{
                rsGetStkLyrT.absolute(1);
                recInPara = JDTORecordFactory.getInstance().create();
                recInPara.setRecord(rsGetStkLyrT.getRecord());
                szYD_UP_WO_LOC_ZAXIS = String.valueOf(ydDaoUtils.paraRecChkNullDouble(recInPara, "SUM_MTL_T"));

                int idx     = szYD_UP_WO_LOC_ZAXIS.lastIndexOf(".");
                if( idx >= 0 ) {
                    szYD_UP_WO_LOC_ZAXIS        = szYD_UP_WO_LOC_ZAXIS.substring(0, idx);
                }
            }


            //권하지시베드조회
            rsDnStkBed = JDTORecordFactory.getInstance().createRecordSet("");
            recGetStkBedData = JDTORecordFactory.getInstance().create();
            recGetStkBedData.setField("YD_STK_COL_GP",  szYD_DN_STK_COL_GP);
            recGetStkBedData.setField("YD_STK_BED_NO",  szYD_DN_STK_BED_NO);
            intRtnVal = ydStkBedDao.getYdStkbed(recGetStkBedData, rsDnStkBed, 0);
            if(intRtnVal <= 0){
                szMsg="updYdCrnschBedData 권하지시 베드 정보조회 중 Error!!";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return false;
            }
            rsDnStkBed.absolute(1);
            recDnStkBed = JDTORecordFactory.getInstance().create();
            recDnStkBed.setRecord(rsDnStkBed.getRecord());

            szMsg="권하지시베드조회 후.. ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.displayRecord(szOperationName, recDnStkBed);

            recDnPara = JDTORecordFactory.getInstance().create();
            rsGetStkLyrT = JDTORecordFactory.getInstance().createRecordSet("");
            recDnPara.setField("YD_STK_COL_GP", szYD_DN_STK_COL_GP);
            recDnPara.setField("YD_STK_BED_NO", szYD_DN_STK_BED_NO);
            recDnPara.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(szYD_DN_STK_LYR_NO, -1));
            intRtnVal = ydStkLyrDao.getYdStklyr(recDnPara, rsGetStkLyrT, 71);



            szMsg="dblSUM_MTL_T :" + dblSUM_MTL_T;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


            szMsg="szYD_DN_WO_LOC_ZAXIS :" + szYD_DN_WO_LOC_ZAXIS;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);




//          double Temp ;

            if( intRtnVal <= 0 ) {
                szMsg="================PASS1============================";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                //szYD_DN_WO_LOC_ZAXIS = ""+ (int)dblSUM_MTL_T;
                szYD_DN_WO_LOC_ZAXIS = String.valueOf(dblSUM_MTL_T);

            }else{
                rsGetStkLyrT.absolute(1);
                recInPara = JDTORecordFactory.getInstance().create();
                recInPara.setRecord(rsGetStkLyrT.getRecord());

                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                szMsg="================PASS2============================";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                szYD_DN_WO_LOC_ZAXIS = String.valueOf(ydDaoUtils.paraRecChkNullDouble(recInPara, "SUM_MTL_T") + dblSUM_MTL_T);

                //szYD_DN_WO_LOC_ZAXIS =  ""+ (int)(ydDaoUtils.paraRecChkNullDouble(recInPara, "SUM_MTL_T")+dblSUM_MTL_T );

            }

            int idx     = szYD_DN_WO_LOC_ZAXIS.lastIndexOf(".");
            if( idx >= 0 ) {
                szYD_DN_WO_LOC_ZAXIS        = szYD_DN_WO_LOC_ZAXIS.substring(0, idx);
            }

            //-------------------------------------------------------------------------------------------------------------
            //  크레인 허용 오차 및 크레인 X, Y좌표 계산 - 임춘수 2009.11.26
            //-------------------------------------------------------------------------------------------------------------
            int intCRANE_GAP_UP_X   = 20;
            int intCRANE_GAP_UP_Y   = 20;
            int intCRANE_GAP_UP_Z   = 20;

            int intCRANE_GAP_DN_X   = 20;
            int intCRANE_GAP_DN_Y   = 20;
            int intCRANE_GAP_DN_Z   = 20;

            String szUp_Crane_Grab_Use_Gp       = "";
            String szUp_Grab_X_Value            = "";
            String szUp_Grab_Y_Value            = "";
            String szUp_Grab_Y1_Value           = "";
            String szUp_Grab_Y2_Value           = "";

            String szDn_Crane_Grab_Use_Gp       = "";
            String szDn_Grab_X_Value            = "";
            String szDn_Grab_Y_Value            = "";
            String szDn_Grab_Y1_Value           = "";
            String szDn_Grab_Y2_Value           = "";

            if( YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYD_GP)|| YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP) ) {   //1,2후판제품창고 - 2012.12.28 수정 (3기)

                szMsg   = "["+szOperationName+"] -------------------------- X,Y 좌표계산 시작 -------------------------------";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                JDTORecord recPara = JDTORecordFactory.getInstance().create();
                JDTORecord recResult = JDTORecordFactory.getInstance().create();
                recPara.setField("YD_CRN_SCH_ID",       szYD_CRN_SCH_ID);
                recPara.setField("YD_EQP_ID",           szYD_EQP_ID);
                recPara.setField("YD_UP_STK_COL_GP",    szYD_UP_STK_COL_GP);
                recPara.setField("YD_UP_STK_BED_NO",    szYD_UP_STK_BED_NO);
                recPara.setField("YD_DN_STK_COL_GP",    szYD_DN_STK_COL_GP);
                recPara.setField("YD_DN_STK_BED_NO",    szYD_DN_STK_BED_NO);

                if( szYD_GP.equals(YdConstant.YD_GP_PLATE2_GDS_YARD)) {
                    //2후판제품창고
                    PlateGdsYdUtil.procXYCalForPlateCrane3G(recPara, recResult);

                    szUp_Crane_Grab_Use_Gp      = ydDaoUtils.paraRecChkNull(recResult, "Crane_Grab_Use_Gp");
                    szUp_Grab_X_Value           = ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_X_Value");
                    szUp_Grab_Y_Value           = ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_Y_Value");
                    szUp_Grab_Y1_Value          = ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_Y1_Value");
                    szUp_Grab_Y2_Value          = ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_Y2_Value");

                    szDn_Crane_Grab_Use_Gp      = ydDaoUtils.paraRecChkNull(recResult, "Crane_Grab_Use_Gp");
                    szDn_Grab_X_Value           = ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_X_Value");
                    szDn_Grab_Y_Value           = ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_Y_Value");
                    szDn_Grab_Y1_Value          = ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_Y1_Value");
                    szDn_Grab_Y2_Value          = ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_Y2_Value");

                    //X,Y 허용오차를 recResult 로 부터 읽어온다.
                    intCRANE_GAP_UP_X           = ydDaoUtils.paraRecChkNullInt(recResult, "CRANE_GAP_UP_X");
                    intCRANE_GAP_UP_Y           = ydDaoUtils.paraRecChkNullInt(recResult, "CRANE_GAP_UP_Y");
                    intCRANE_GAP_UP_Z           = ydDaoUtils.paraRecChkNullInt(recResult, "CRANE_GAP_UP_Z");
                    intCRANE_GAP_DN_X           = ydDaoUtils.paraRecChkNullInt(recResult, "CRANE_GAP_DN_X");
                    intCRANE_GAP_DN_Y           = ydDaoUtils.paraRecChkNullInt(recResult, "CRANE_GAP_DN_Y");
                    intCRANE_GAP_DN_Z           = ydDaoUtils.paraRecChkNullInt(recResult, "CRANE_GAP_DN_Z");

                    if(szYD_UP_STK_COL_GP.startsWith(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRD")||szYD_UP_STK_COL_GP.startsWith(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRE")||szYD_UP_STK_COL_GP.startsWith(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRF")){
                        //20220802 박성열   후판 E동 RT일 경우 권상 갭을 5500으로 고정   박종호 책임 요청
                        szMsg   = "["+szOperationName+"] --------------------------YdUtils ERT 권상 -------------------------------";
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        intCRANE_GAP_UP_Y           = 5500;
                    }

                } else {
                    //1후판제품창고
                    PlateGdsYdUtil.procXYCalForPlateCrane(recPara, recResult);

                    szUp_Crane_Grab_Use_Gp      = ydDaoUtils.paraRecChkNull(recResult, "Crane_Grab_Use_Gp");
                    szUp_Grab_X_Value           = ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_X_Value");
                    szUp_Grab_Y_Value           = ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_Y_Value");
                    //String szUp_Grab_Y1_Addr              = ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_Y1_Addr");
                    szUp_Grab_Y1_Value          = ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_Y1_Value");
                    //String szUp_Grab_Y2_Addr              = ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_Y2_Addr");
                    szUp_Grab_Y2_Value          = ydDaoUtils.paraRecChkNull(recResult, "Up_Grab_Y2_Value");

                    szDn_Crane_Grab_Use_Gp      = ydDaoUtils.paraRecChkNull(recResult, "Crane_Grab_Use_Gp");
                    szDn_Grab_X_Value           = ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_X_Value");
                    szDn_Grab_Y_Value           = ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_Y_Value");
                    //String szDn_Grab_Y1_Addr              = ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_Y1_Addr");
                    szDn_Grab_Y1_Value          = ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_Y1_Value");
                    //String szDn_Grab_Y2_Addr              = ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_Y2_Addr");
                    szDn_Grab_Y2_Value          = ydDaoUtils.paraRecChkNull(recResult, "Dn_Grab_Y2_Value");

                    /*
                     * 임시적용 2014.02.12 윤재광
                     * F동 4/5/6스판만 적용 - Y축 허용오차 4000(4M)으로 강제셋팅
                     */
                    if(szYD_UP_STK_COL_GP.startsWith("KF04")||szYD_UP_STK_COL_GP.startsWith("KF05")||szYD_UP_STK_COL_GP.startsWith("KF06")){
                        intCRANE_GAP_UP_Y           = 4000;
                    }
                    if(szYD_DN_STK_COL_GP.startsWith("KF04")||szYD_DN_STK_COL_GP.startsWith("KF05")||szYD_DN_STK_COL_GP.startsWith("KF06")){
                        intCRANE_GAP_DN_Y           = 4000;
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

                szMsg   = "["+szOperationName+"] -------------------------- X,Y 좌표계산 완료 -------------------------------";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            }else if( szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD) ) {            //A후판슬라브야드
                intCRANE_GAP_UP_X = YdConstant.A_PLATE_SLAB_CRANE_GAP;
                intCRANE_GAP_UP_Y = YdConstant.A_PLATE_SLAB_CRANE_GAP;
                intCRANE_GAP_UP_Z = YdConstant.A_PLATE_SLAB_CRANE_GAP;

                intCRANE_GAP_DN_X = YdConstant.A_PLATE_SLAB_CRANE_GAP;
                intCRANE_GAP_DN_Y = YdConstant.A_PLATE_SLAB_CRANE_GAP;
                intCRANE_GAP_DN_Z = YdConstant.A_PLATE_SLAB_CRANE_GAP;

                szUp_Grab_X_Value           = ydDaoUtils.paraRecChkNull(recUpStkBed, "YD_STK_BED_XAXIS");
                szUp_Grab_Y_Value           = ydDaoUtils.paraRecChkNull(recUpStkBed, "YD_STK_BED_YAXIS");

                szDn_Grab_X_Value           = ydDaoUtils.paraRecChkNull(recDnStkBed, "YD_STK_BED_XAXIS");
                szDn_Grab_Y_Value           = ydDaoUtils.paraRecChkNull(recDnStkBed, "YD_STK_BED_YAXIS");

            }else{
                if( szYD_SCH_CD.substring(2, 4).equals("TC") ) {
                    intCRANE_GAP_UP_X = YdConstant.C_SLAB_CRANE_GAP_X1;
                    intCRANE_GAP_UP_Y = YdConstant.C_SLAB_CRANE_GAP_Y1;

                    intCRANE_GAP_DN_X = YdConstant.C_SLAB_CRANE_GAP_X1;
                    intCRANE_GAP_DN_Y = YdConstant.C_SLAB_CRANE_GAP_Y1;
                }else if( szYD_SCH_CD.substring(2, 4).equals("SB") ) {
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

                szUp_Grab_X_Value           = ydDaoUtils.paraRecChkNull(recUpStkBed, "YD_STK_BED_XAXIS");
                szUp_Grab_Y_Value           = ydDaoUtils.paraRecChkNull(recUpStkBed, "YD_STK_BED_YAXIS");

                szDn_Grab_X_Value           = ydDaoUtils.paraRecChkNull(recDnStkBed, "YD_STK_BED_XAXIS");
                szDn_Grab_Y_Value           = ydDaoUtils.paraRecChkNull(recDnStkBed, "YD_STK_BED_YAXIS");
            }

            //-------------------------------------------------------------------------------------------------------------


            //-------------------------------------------------------------------------------------------------------------
            //크레인 스케줄  권하지시위치 업데이트
            //-------------------------------------------------------------------------------------------------------------
            recUpdCrnSchData = JDTORecordFactory.getInstance().create();

            recUpdCrnSchData.setField("YD_CRN_SCH_ID",              recResultCrnwrkmtl.getFieldString("YD_CRN_SCH_ID") );

            recUpdCrnSchData.setField("YD_EQP_WRK_SH",              ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "SH_CNT"));
            recUpdCrnSchData.setField("YD_EQP_WRK_WT",              ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "SUM_MTL_WT"));
            recUpdCrnSchData.setField("YD_EQP_WRK_T",               ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "SUM_MTL_T"));
            recUpdCrnSchData.setField("YD_EQP_WRK_MAX_W",           ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "MAX_MTL_W"));
            recUpdCrnSchData.setField("YD_EQP_WRK_MAX_L",           ydDaoUtils.paraRecChkNull(recResultCrnwrkmtl, "MAX_MTL_L"));

            if( szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD) ) {                     //후판제품창고
                //Grab 사용 구분
                recUpdCrnSchData.setField("YD_CRN_SB_CTL_H",        szUp_Crane_Grab_Use_Gp);
            }


        //  recUpdCrnSchData.setField("YD_DN_WO_LOC",   recDnPara.getFieldString("YD_STK_COL_GP") + recDnPara.getFieldString("YD_STK_BED_NO")) ;
        //  recUpdCrnSchData.setField("YD_DN_WO_LAYER", recDnPara.getFieldString("YD_STK_LYR_NO") ) ;
            recUpdCrnSchData.setField("YD_UP_WO_LOC_XAXIS",         szUp_Grab_X_Value) ;
            recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MAX",     String.valueOf(intCRANE_GAP_UP_X) ) ;
            recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MIN",     String.valueOf(intCRANE_GAP_UP_X) ) ;
            recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS",         szUp_Grab_Y_Value) ;
            recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS1",        szUp_Grab_Y1_Value ) ;
            recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS2",        szUp_Grab_Y2_Value ) ;
            recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MAX",     String.valueOf(intCRANE_GAP_UP_Y) ) ;
            recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MIN",     String.valueOf(intCRANE_GAP_UP_Y) ) ;
            recUpdCrnSchData.setField("YD_UP_WO_LOC_ZAXIS",         szYD_UP_WO_LOC_ZAXIS ) ;
            recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MAX",     String.valueOf(intCRANE_GAP_UP_Z) ) ;
            recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MIN",     String.valueOf(intCRANE_GAP_UP_Z) ) ;

            recUpdCrnSchData.setField("YD_DN_WO_LOC_XAXIS",         szDn_Grab_X_Value) ;
            recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MAX",     String.valueOf(intCRANE_GAP_DN_X) ) ;
            recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MIN",     String.valueOf(intCRANE_GAP_DN_X) ) ;
            recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS",         szDn_Grab_Y_Value) ;
            recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS1",        szDn_Grab_Y1_Value ) ;
            recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS2",        szDn_Grab_Y2_Value ) ;
            recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MAX",     String.valueOf(intCRANE_GAP_DN_Y) ) ;
            recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MIN",     String.valueOf(intCRANE_GAP_DN_Y) ) ;
            recUpdCrnSchData.setField("YD_DN_WO_LOC_ZAXIS",         szYD_DN_WO_LOC_ZAXIS ) ;
            recUpdCrnSchData.setField("YD_DN_WO_ZAXIS_GAP_MAX",     String.valueOf(intCRANE_GAP_DN_Z) ) ;
            recUpdCrnSchData.setField("YD_DN_WO_ZAXIS_GAP_MIN",     String.valueOf(intCRANE_GAP_DN_Z) ) ;


            ydUtils.displayRecord(szOperationName, recUpdCrnSchData);

//SJH0621
//          intRtnVal = ydCrnschDao.updYdCrnsch(recUpdCrnSchData, 0);
            /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnWrkSidedelyn*/
            intRtnVal = ydCrnschDao.updYdCrnsch(recUpdCrnSchData, 303);
            if(intRtnVal <= 0) {
                if(intRtnVal == 0) {
                    szMsg="<updYdCrnschBedData> updYdCrnsch data not found";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.WARNING);
                }else if(intRtnVal == -1) {
                    szMsg="<updYdCrnschBedData> updYdCrnsch duplicate data,";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -2) {
                    szMsg="<updYdCrnschBedData> updYdCrnsch parameter error";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }else if(intRtnVal == -3){
                    szMsg="<updYdCrnschBedData> updYdCrnsch execution failed";
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                }
                return false;
            }

            //-------------------------------------------------------------------------------------------------------------

        }catch(Exception e){
            szMsg="<updYdCrnschBedData> Error : "+ e.getLocalizedMessage();
            ydUtils.putLog("YdUtils", szMethodName, szMsg, YdConstant.ERROR);
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
    public  String getMakeSchCdMM (String pzYdGp , String pzYdBayGp , String pzYdEqpGp )
    {
        String szMsg = "";
        String szRtnValue = "";
        String szMethodName     = "insCSlabSupPrepSchManual";
        String szOperationName      = " 이적 스케줄 코드를 생성";
        String szYdSchCd = "";
        String szYdEqpGp = "";


        szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
        putLog( szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



        szMsg = "[JSP Session : "+szOperationName+"] 입력받은 정보 (야드,동,스판) =  " + "(" + pzYdGp + ", " + pzYdBayGp + "," +  pzYdEqpGp+")";
        putLog( szSessionName, szMethodName, szMsg, YdConstant.ERROR);



        if (pzYdGp.equals("")){
            szMsg = "[JSP Session : "+szOperationName+"]야드구분이 올바르지 않습니다. ";
            putLog( szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return "";
        }


        if (pzYdBayGp.equals("")){
            szMsg = "[JSP Session : "+szOperationName+"] 동 구분이 올바르지 않습니다. ";
            putLog( szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return "";
        }


        if (pzYdEqpGp.equals("")){
            szMsg = "[JSP Session : "+szOperationName+"] 설비구분이 올바르지 않습니다. ";
            putLog( szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return "";
        }

        szYdEqpGp = pzYdEqpGp;

        //  후판제품야드 스케줄 설비정보 판단 Logic
        if(pzYdGp.equals(YdConstant.YD_GP_PLATE_GDS_YARD)){

            if(pzYdEqpGp.equals(YdConstant.SPAN_ORDER_NEW_04)|| pzYdEqpGp.equals(YdConstant.SPAN_ORDER_NEW_05)|| pzYdEqpGp.equals(YdConstant.SPAN_ORDER_NEW_06)) {
                szYdEqpGp = "12";
            }else if( pzYdEqpGp.equals(YdConstant.SPAN_ORDER_NEW_07) || pzYdEqpGp.equals(YdConstant.SPAN_ORDER_NEW_TP) ){
                szYdEqpGp = "34";
            }else{
                szYdEqpGp = "";
            }
        }
        //야드별로 추가 하고 싶을경우는 추가로직을 여기에 넣는다

        szYdSchCd =  pzYdGp + pzYdBayGp + "YD" + szYdEqpGp + "MM";

        //스케줄코드가 제대로 생성되지 않았을 경우
        if(szYdSchCd.length() != 8){
            szMsg = "[JSP Session : "+szOperationName+"] 스케줄 코드가 올바르게 생성되지 않았습니다.. ";
            putLog( szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return "";
        }

        return szYdSchCd;
    }



    /**
     *  스케줄 코드를 생성(입고, 이적) - 제작중
     *  2009.12.28 이현성
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param String pzYdGp , String pzYdBayGp , String pzYdEqpGp
     * @return String (올바르게 만들어 지지 않았을경우 - 8자리가 아닐경우는 "" Retrun)
     */
    public  String getMakeSchCd (String pzYdGp , String pzYdBayGp , String pzYdEqpGp , String pzStlNo)
    {
        String szMsg = "";
        String szRtnValue = "";
        String szMethodName     = "insCSlabSupPrepSchManual";
        String szOperationName      = " 이적 스케줄 코드를 생성";
        String szYdSchCd = "";
        String szYdEqpGp = "";
        String szYdSchGp = null;


        int nRtnVal = 0;
        JDTORecord recPara = null;

        JDTORecordSet rsStock    = null;


        YdStockDao ydStockDao  = new YdStockDao();
        YdDaoUtils  ydDaoUtils  = new YdDaoUtils();



        szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
        putLog( szSessionName, szMethodName, szMsg, YdConstant.DEBUG);



        szMsg = "[JSP Session : "+szOperationName+"] 입력받은 정보 (야드,동,스판) =  " + "(" + pzYdGp + ", " + pzYdBayGp + "," +  pzYdEqpGp+")";
        putLog( szSessionName, szMethodName, szMsg, YdConstant.ERROR);



        if (pzYdGp.equals("")){
            szMsg = "[JSP Session : "+szOperationName+"]야드구분이 올바르지 않습니다. ";
            putLog( szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return "";
        }


        if (pzYdBayGp.equals("")){
            szMsg = "[JSP Session : "+szOperationName+"] 동 구분이 올바르지 않습니다. ";
            putLog( szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return "";
        }


        if (pzYdEqpGp.equals("")){
            szMsg = "[JSP Session : "+szOperationName+"] 설비구분이 올바르지 않습니다. ";
            putLog( szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return "";
        }


        if (pzStlNo.equals("")){
            szMsg = "[JSP Session : "+szOperationName+"] 재료번호가 올바르지 않습니다. ";
            putLog( szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return "";
        }


        szYdEqpGp = pzYdEqpGp;






        //  후판제품야드 스케줄 설비정보 판단 Logic

        if(pzYdGp.equals(YdConstant.YD_GP_PLATE_GDS_YARD)){

            if(pzYdEqpGp.equals(YdConstant.SPAN_ORDER_NEW_04)|| pzYdEqpGp.equals(YdConstant.SPAN_ORDER_NEW_05)|| pzYdEqpGp.equals(YdConstant.SPAN_ORDER_NEW_06)) {
                szYdEqpGp = "12";

            }else if( pzYdEqpGp.equals(YdConstant.SPAN_ORDER_NEW_07) || pzYdEqpGp.equals(YdConstant.SPAN_ORDER_NEW_TP) ){
                szYdEqpGp = "34";
            }else{
                szYdEqpGp = "";
            }
        }
        //야드별로 추가 하고 싶을경우는 추가로직을 여기에 넣는다

        //재료정보에 입고일자가 있는지 판단하여 없는경우는 입고스케줄로 편성함.
        try {

            recPara = JDTORecordFactory.getInstance().create();
            recPara.setField("STL_NO", pzStlNo);
            rsStock =  JDTORecordFactory.getInstance().createRecordSet("");

            nRtnVal = ydStockDao.getYdStock(recPara, rsStock, 0);

            if(nRtnVal < 0 ){
                szMsg = "[JSP Session : "+szOperationName+"] STOCK 조회 ERROR ";
                putLog( szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return "";

            } else if(nRtnVal == 0){
                szMsg = "[JSP Session : "+szOperationName+"] STOCK 조회 데이터가 없습니다 ";
                putLog( szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return "";

            } else {
                szMsg = "[JSP Session : "+szOperationName+"] STOCK 조회 성공 ";
                putLog( szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }


            recPara = JDTORecordFactory.getInstance().create();

            rsStock.first();
            recPara = rsStock.getRecord();


            //입고일자가 있는지 판단!!!!!!!!!!!!!!!!!!!!!!!
            //입고시 대표 스케줄 코드 필요함!!!!!!!!!!!!!!

            if(ydDaoUtils.paraRecChkNull(recPara, "").equals("")){
                szMsg = "[JSP Session : "+szOperationName+"] 입고일자 미존재시 입고 판단.";
                putLog( szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                szYdSchGp = "LM";

            }else{
                szMsg = "[JSP Session : "+szOperationName+"] 입고일자 존재시 이적으로 판단.";
                putLog( szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                szYdSchGp = "MM";
                szYdSchCd =  pzYdGp + pzYdBayGp + "YD" + szYdEqpGp + szYdSchGp ;
            }




        } catch (JDTOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }






        //스케줄코드가 제대로 생성되지 않았을 경우
        if(szYdSchCd.length() != 8){
            szMsg = "[JSP Session : "+szOperationName+"] 스케줄 코드가 올바르게 생성되지 않았습니다.. ";
            putLog( szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return "";
        }

        return szYdSchCd;
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
    public static String FormatCommaStr(double dVal)
    {
        NumberFormat nf = new DecimalFormat(",###.###");
        return (String)nf.format(dVal);
    }

    public static String FormatCommaStr(long lVal)
    {

        NumberFormat nf = new DecimalFormat("#,###");
        return (String)nf.format(lVal);
    }

    public static String FormatCommaStr(String szVal)
    {
        if(szVal == null || szVal.trim().equals("")){
            return "";
        }

        int nRet = szVal.trim().indexOf(".");
        String szRet = "";
        if(nRet != -1){
            szRet = FormatCommaStr(Double.parseDouble(szVal.trim()));
        } else {
            szRet = FormatCommaStr(Integer.parseInt(szVal.trim()));
        }

        return szRet;
    }





    //=====================================================================
    // 2009.12.15
    // 권오창
    //
    // 적치열구분(6) + 적치BED번호(2) + 적치단번호(3) 를 입력받아서 야드별로 구분하여
    // 10자리의 위치정보를 만들어 내는 함수
    //
    // 연주슬라브(A) : 6 + 2 + 2
    // 후판슬라브(D) : 6 + 2 + 2
    // 후판제품   (K) : 6 + 1 + 3
    // 코일소재   (H) : 6 + 2 + 2
    // 코일제품   (J) : 6 + 2 + 2
    // 통합슬라브(S) : 6 + 2 + 2
    //=====================================================================
    public String ParsingStkColGpBedLyr(String szStkColGp, String szStkBedNo, String szStkLyrNo){
        // 변수 선언
        String szMethodName = "ParsingStkColGpBedLyr";
        String szMsg        = "";
        String szYdGp       = "";
        String szRet        = "";


        // 파라미터 유효성 체크
        if(szStkColGp == null || szStkColGp.equals("") || szStkColGp.trim().length() != 6){
            szMsg = "넘어온 파라미터에서 적치열구분 항목이 유효한 데이터가 아닙니다.";
            this.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return "";
        }

        if(szStkBedNo == null || szStkBedNo.equals("") || szStkBedNo.trim().length() != 2){
            szMsg = "넘어온 파라미터에서 적치BED번호 항목이 유효한 데이터가 아닙니다.";
            this.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return "";
        }

        if(szStkLyrNo == null || szStkLyrNo.equals("") || szStkLyrNo.trim().length() != 3){
            szMsg = "넘어온 파라미터에서 적치단번호 항목이 유효한 데이터가 아닙니다.";
            this.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return "";
        }


        szMsg = "[수신 항목] 적치열구분(" + szStkColGp + ") 적치BED번호(" + szStkBedNo + ") 적치단번호(" + szStkLyrNo + ")";
        this.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


        // 야드값 추출
        szYdGp = szStkColGp.substring(0, 1);


        // 야드값에 따른 분기
        if(szYdGp.equals(YdConstant.YD_GP_C_SLAB_YARD)){
            // C연주슬라브야드
            szRet = szStkColGp + szStkBedNo + YdUtils.fillSpZr("" + Integer.parseInt(szStkLyrNo), 2, 0);
            szMsg = "편집된 위치정보 : C연주슬라브야드(" + szRet + ")";
        } else if(szYdGp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)){
            // A후판슬라브야드
            szRet = szStkColGp + szStkBedNo + YdUtils.fillSpZr("" + Integer.parseInt(szStkLyrNo), 2, 0);
            szMsg = "편집된 위치정보 : A후판슬라브야드(" + szRet + ")";
        } else if(szYdGp.equals(YdConstant.YD_GP_PLATE_GDS_YARD) || szYdGp.equals(YdConstant.YD_GP_PLATE2_GDS_YARD) || szYdGp.equals("4")){ //--2013.02.07 수정 (3기)
            // 1,2후판제품창고야드
            String sTmp = "";
            try{
                sTmp = YdUtils.fillSpZr("" + Integer.parseInt(szStkBedNo), 1, 0);
            }catch(Exception e){
                sTmp = szStkBedNo;
            }
            szRet = szStkColGp + sTmp + szStkLyrNo;
            szMsg = "편집된 위치정보 : 후판제품창고야드(" + szRet + ")";
        } else if(szYdGp.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD)){
            // C열연코일소재야드
            szRet = szStkColGp + szStkBedNo + YdUtils.fillSpZr("" + Integer.parseInt(szStkLyrNo), 2, 0);
            szMsg = "편집된 위치정보 : C열연코일소재야드(" + szRet + ")";
        } else if(szYdGp.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD)){
            // C열연코일제품야드
            szRet = szStkColGp + szStkBedNo + YdUtils.fillSpZr("" + Integer.parseInt(szStkLyrNo), 2, 0);
            szMsg = "편집된 위치정보 : C열연코일제품야드(" + szRet + ")";
        } else if(szYdGp.equals(YdConstant.YD_GP_INTGR_YARD)){
            // 통합야드A(부두)
            szRet = szStkColGp + szStkBedNo + YdUtils.fillSpZr("" + Integer.parseInt(szStkLyrNo), 2, 0);
            szMsg = "편집된 위치정보 : 통합야드A부두(" + szRet + ")";
        } else if(szYdGp.equals(YdConstant.YD_GP_PORT_SLAB_YARD)){
            // C3#스카핑야드
            szRet = szStkColGp + szStkBedNo + YdUtils.fillSpZr("" + Integer.parseInt(szStkLyrNo), 2, 0);
            szMsg = "편집된 위치정보 : C3#스카핑야드(" + szRet + ")";
        } else if(szYdGp.equals(YdConstant.YD_GP_PLATE_JJ_YARD)){
            // 1후판정정야드
            szRet = szStkColGp + szStkBedNo + YdUtils.fillSpZr("" + Integer.parseInt(szStkLyrNo), 2, 0);
            szMsg = "편집된 위치정보 : 1후판정정야드(" + szRet + ")";
        }  else {
            szMsg = "*** 적치열구분, 적치BED번호, 적치단 번호를 이용해 위치정보 편성에 실패 ***";
            this.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            return "";
        }

        this.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        return szRet;
    }


    //---------------------------------------------------------------------------
    /**
     *      [A] 오퍼레이션명 : 문자열을 float로 변환
     *          예) 123456 , 6, 3 -> 123.456
     *
     * @param String strData        // 변환대상 문자열 [소수점없는 숫자값]
     *        int    nLen           // 전체자릿수
     *        int    nDeci          // 소수부분
     * @return float                // 변환 완료 된 float값
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


    //┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
    //
    //                      일관제철소정보관리시스템-야드관리
    //                          Common Utility Class
    //                          2008.09.30 YHWHman
    //
    //┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

    public static void main(String[] args){

        try {
        YdUtils im =new YdUtils();

        JDTORecord testRec =JDTORecordFactory.getInstance().create();
        String szOperationName = "일관제철소정보관리시스템 테스트";
        String [] szaKeys=null;

        int nRtc=0;


//      try {

            //
            // make JDTORecord
            testRec.setField("NAME", new String("김인홍"));
            testRec.setField("JOB", new String("Computer Programmer/Architect"));
            testRec.setField("PHONE", new String("010-6257-3209"));
            testRec.setField("EMAIL", new String("yhwhman@gmail.com"));
            testRec.setField("TEST1", null);
            //testRec.setField("JMS_TC_CD", "YDYDJ999");
            testRec.setField("JMS_TC_CD", null);


            //
            // Get Record Keys
            szaKeys=im.getRecKey(testRec);
            nRtc =szaKeys.length;

            // 2012-11-14 (김현우) 표준화 지적사항 반영으로 주석처리
            //System.out.println("nRtc=["+nRtc+"]");
            //System.out.println("-------------------------------");

            // 2012-11-14 (김현우) 표준화 지적사항 반영으로 주석처리
            //for(int i=0; i<nRtc;i++){
                //System.out.println(szaKeys[i]+" : ["+testRec.getFieldString(szaKeys[i])+"]");
            //} // end of for()

            // 2012-11-14 (김현우) 표준화 지적사항 반영으로 주석처리
            //System.out.println("\n");

            //
            // Record Display
            im.displayRecord(szOperationName, testRec);

            // 2012-11-14 (김현우) 표준화 지적사항 반영으로 주석처리
            //System.out.println("\n");

            //
            // Convert Data JDTORecord to String Stream
            String szMsg =im.makeRec2Str(testRec);

            // 2012-11-14 (김현우) 표준화 지적사항 반영으로 주석처리
            //System.out.println("Stream Data=["+szMsg+"]");


        } catch (Exception e) {
            // 2012-11-14 (김현우) 표준화 지적사항 반영으로 주석처리
            //System.out.println("Exception Error : "+e.getLocalizedMessage());
            //e.printStackTrace();
        } // end of try-catch



    } // end of testMain()

    /**
     *      [A] 오퍼레이션명 : 메일 전송
     *
     * @ejb.interface-method
     * @param jRecordParam
     * @throws DAOException
    */
    public void sendMail(JDTORecord jRecordParam) throws DAOException{
        // 메일전송 객체
        MailSender sender = null;

        // 메일 전송 메세지 JDTORecord객체
        JDTORecord inRecord = null;

        try {
            String methodNm = "sendMail";
            String szMsg="";

            // 객체생성
            sender = new MailSender();

            // 객체초기화
            sender.initService();

            // 전송 메세지 편집
            inRecord = JDTORecordFactory.getInstance().create();

            szMsg= "▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨";
            this.putLog(szSessionName, methodNm, szMsg, YdConstant.DEBUG);
            szMsg= "SENDER_ADDR : "+jRecordParam.getField("SENDER_ADDR");
            this.putLog(szSessionName, methodNm, szMsg, YdConstant.DEBUG);
            szMsg= "SENDER_NAME : "+jRecordParam.getField("SENDER_NAME");
            this.putLog(szSessionName, methodNm, szMsg, YdConstant.DEBUG);
            szMsg= "SUBJECT : "+jRecordParam.getField("SUBJECT");
            this.putLog(szSessionName, methodNm, szMsg, YdConstant.DEBUG);
            //szMsg= "CONTENT : "+jRecordParam.getField("CONTENT");
            szMsg= "CONTENT : "+jRecordParam.getField("CONTENT");
            this.putLog(szSessionName, methodNm, szMsg, YdConstant.DEBUG);
            szMsg= "RECEVER_ADDR : "+jRecordParam.getField("RECEVER_ADDR");
            this.putLog(szSessionName, methodNm, szMsg, YdConstant.DEBUG);
            szMsg= "RECEVER : "+jRecordParam.getField("RECEVER");
            this.putLog(szSessionName, methodNm, szMsg, YdConstant.DEBUG);
            szMsg= "▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨▨";
            this.putLog(szSessionName, methodNm, szMsg, YdConstant.DEBUG);

            // 메일 보내는 사람 메일 주소
            inRecord.setField("FROM_MAIL_ADDRESS", jRecordParam.getField("SENDER_ADDR"));

            // 메일 보내는 사람 성명
            inRecord.setField("FROM_MAIL_NAME", jRecordParam.getField("SENDER_NAME"));

            // 메일 제목
            inRecord.setField("TO_SUBJECT", jRecordParam.getField("SUBJECT"));

            inRecord.setField("TO_CONTENT", jRecordParam.getField("CONTENT").toString());

            // 메일 받는 사람들 메일 주소
            inRecord.setField("TO_MAIL_ADDRESS", jRecordParam.getField("RECEVER_ADDR"));

            // 메일 받는 사람들 성명
            inRecord.setField("TO_MAIL_NAME", jRecordParam.getField("RECEVER"));
            //inRecord.setField("TO_MAIL_NAME", "1523629");

            // 메일 참조받는 사람들 메일 주소
            inRecord.setField("CC_MAIL_ADDRESS", new String[] {  });
            // 메일 참조받는 사람들 성명
            inRecord.setField("CC_MAIL_NAME", new String[] {  });

            szMsg= "===============메일 내용========================";
            //여기에 inRecord 값들 표기
            szMsg= "==============================================";

            // 메일 전송
            sender.send(inRecord);


        }catch(javax.mail.SendFailedException sfe){
            String szMsg= "존재하지 않는 메일주소입니다. 확인해주세요.";
            this.putLog(szSessionName, "sendMail", szMsg, YdConstant.DEBUG);
            //msg = MessageHelper.getUserMessage("MSG0160", new String[] {msg}, msg);
            //throw new DAOException(msg);
        }catch(Exception sfe){
            String szMsg= "메일주소가 존재하지 않습니다. 확인해주세요.";
            this.putLog(szSessionName, "sendMail", szMsg, YdConstant.DEBUG);
        }finally {

        }
    }


    /**
     * 오퍼레이션명 : putLogNew          // 2024.09.02 로그 개선
     *
     * @param String szClassName    // Logging 요청 Class name
     *        String szMethodName   // Logging 요청 Method Name
     *        String szLogMsg       // Logging Message
     *        int    nLogLevel      // Logging Level
     *        String szLogId        // Logging ID
     * @return
     * @throws DAOException, JDTOException
     */
    public void putLogNew(String szClassName, String szMethodName, String szLogMsg, int nLogLevel, String szLogId)  {

        String szMsg="";

        szMsg = szLogId + " :: " + szMethodName + "() :: " + szLogMsg;
//        System.out.println("***********************************************");
//        System.out.println("putLogNew " + szMsg);
//        System.out.println("***********************************************");

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

            } else {

                // Message Logging
                switch(nLogLevel){
                case 1:
//                  logger.println(LogLevel.ERROR, this, szMsg);
                    logger.println(LogLevel.ERROR, "", szMsg);
                    break;

                case 2:
//                  logger.println(LogLevel.WARNING, this, szMsg);
                    logger.println(LogLevel.WARNING, "", szMsg);
                    break;

                case 3:
//                  logger.println(LogLevel.INFO, this, szMsg);
                    logger.println(LogLevel.INFO, "", " " + szMsg);
                    break;

                default:
//                  logger.println(LogLevel.DEBUG, this, szMsg);
                    logger.println(LogLevel.DEBUG, "", szMsg);
                break;


                } // end of switch(nLogLevel)

            } // end of if(bDebugFlag)


        }catch (Exception e){

            szMsg =szClassName + "::" + szMethodName +"() " + "\n\t"+szMethodName+" Exception Error : "+e.getLocalizedMessage();

            logger.println(LogLevel.ERROR, this, szMsg);

        } // end of try-catch()

    } // end of putLogNew();


    /**
     * [A] 오퍼레이션명 : Logging 을 위한 ID 생성 // 2024.09.02 로그 개선
     * @param String
     * @return String
    */
    public String getLogIdNew(String szYD_GP) {
        String szMsg="";
        String logId="";
        String szMethodName="getLogIdNew";

        logId = "[" + szYD_GP + "]<" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
      
        szMsg = szMethodName + " logId 새로 발본 : " + logId;
        this.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

        return logId;
    }

    /**
     * Object가 null 일때 true를 반환한다.  // 2024.09.02 로그 개선
     * @param obj
     * @return boolean
     */
    public boolean isEmpty(Object obj) {
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
     * [A] 오퍼레이션명 : JDTORecord Logging ID Get // 2024.09.02 로그 개선
     * @param JDTORecord
     * @return String
    */
    public String getJDTOLogId(JDTORecord inRecord, String szYD_GP) {

    	String szMsg="";
        String szMethodName="getJDTOLogId";

    	String logId        = inRecord.getResultCode(); 				// 전문으로 부터 logid get	

    	if(this.isEmpty(logId)) {
        	// JDTORecord 에서 UNIQUE_ID Field 있으면 LogId 로 설정
    	    logId           = inRecord.getFieldString("UNIQUE_ID");	
    		
        	if(this.isEmpty(logId)) {
        		// JDTORecord 에서 LOG_ID Field 있으면 LogId 로 설정 : LOG_ID Field 는 이미 ([T]<logId>) 형식
        	    logId       = inRecord.getFieldString("LOG_ID");	
        	    if(this.isEmpty(logId)) {
        	        logId   = this.getLogIdNew(szYD_GP);                // log id 가 비어있는경우 log id 새로 발번
        	    }
        	    else {
        	        szMsg = szMethodName + " LOG_ID : " + logId;
                    this.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
        	    }
        	}
        	else {
        		
        		// [야드구분]< + UNIQUE_ID + > 
    	        logId       = "[" + szYD_GP + "]<" + logId + ">";

    	        szMsg = szMethodName + " UNIQUE_ID : " + logId;
                this.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
        	}
    		
    	}
    	else {
    		
    		// [T] + getResultCode : getResultCode 는 <logId> 형식이기 때문
    		// 2024.11.20 getResultCode == "0000" 이면 LOG_ID -> UNIQUE_ID -> 새로 발본 
    		if ("0000".equals(logId)) {
        		// JDTORecord 에서 LOG_ID Field 있으면 LogId 로 설정 : LOG_ID Field 는 이미 ([T]<logId>) 형식
        	    logId       = inRecord.getFieldString("LOG_ID");	
        	    if(this.isEmpty(logId)) {
            	    logId           = inRecord.getFieldString("UNIQUE_ID");
            	    if(this.isEmpty(logId)) {
                	    logId   = this.getLogIdNew(szYD_GP);                // log id 가 비어있는경우 log id 새로 발번
            	    }
            	    else {
	            		// [야드구분]< + UNIQUE_ID + > 
	        	        logId       = "[" + szYD_GP + "]<" + logId + ">";
	
	        	        szMsg = szMethodName + " getResultCode == 0000 이라 UNIQUE_ID 사용 : " + logId;
	                    this.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
            	    }

        	    }
        	    else {
            		// JDTORecord 에서 LOG_ID Field 있으면 LogId 로 설정 : LOG_ID Field 는 이미 ([T]<logId>) 형식
        	        szMsg = szMethodName + " getResultCode == 0000 이라 LOG_ID 사용 : " + logId;
                    this.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
        	    }
    		}else{
    	        logId       = "[" + szYD_GP + "]" + logId + "";
    	        
    	        szMsg = szMethodName + " getResultCode : " + logId;
                this.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
    		}

    	}

        return logId;
    }
    

//---------------------------------------------------------------------------------
} // end of class
