/**
 * 
 * @(#)YmCommonUtil
 * 
 * @version    :
 * @author     : 이봉준
 * @date         : 2005. 7. 20
 *
 * @description :
 * 
 */
package com.inisteel.cim.ym.common;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.sql.ResultSet;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordImplMap;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import jspeed.base.http.HttpRequestWrapper;
import jspeed.base.http.HttpResponseWrapper;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.query.DBAssistant;
import jspeed.base.query.QueryService;
import jspeed.fc.session.JspeedSession;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.yd.common.dao.ydMsgInfoMgtDao.YdMsgInfoMgtDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.scheduling.crane.dao.YdSchRuleDAO;
import com.inisteel.cim.ym.common.DetailClass2CodeDTO;

// 최규성 추가.
import flex.messaging.MessageBroker;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.util.UUIDUtils;


public class YmCommonUtil{   
	String szClassName = this.getClass().getName();
	//YmCommonUtil ymCommonUtil = new YmCommonUtil();	
	public final String FACADE_TCCODE ="FACADEPT";
	private String szSessionName =getClass().getName();
	private Logger logger = null;

	
	public YmCommonUtil(){
	    logger = LogService.getInstance().getLogServiceContext().getLogger("ym");
    }

    public static Map getCdAndValudOfModel(String logData) {
        Map cdValue = new HashMap();
        String[] equalSplit = null;
        String[] commaSplit = logData.split(",");
        for(int i = 0; i < commaSplit.length; i++) {
            equalSplit = commaSplit[i].split("=");
            for(int j = 0; j < equalSplit.length; j++) {
                if((j % 2) == 1) {
                    cdValue.put(equalSplit[j-1].trim(), equalSplit[j].trim());
                }
            }
        }
        return cdValue;    
    }
    
	/**
     * TB_CM_CDCLASS2 Table에서 리스트를 조회한다 * 
     * @param sTYPE_CD Code Type
     * @param sCLASS1_CD class1 Code
     * @param sDEL_YN 삭제유무
     * @param sort 정렬순서(ASC|DSC)
     * @return
     * @throws DAOException
	 */
	public ArrayList getCodeList(String sTYPE_CD, String sCLASS1_CD, String sDEL_YN, String sort) throws DAOException {
		
		DBAssistant dba  = null;
		ResultSet   rset = null;
				
		try {
			
			// 리스트를 조회한다.
			dba = new DBAssistant(this);			
			String queryCode = "ym.code.dao.CodeDAO.getDetailClass1CodeListDEL";			
			
			if(sort == null || sort.length() <= 0) {
			    rset = dba.executeQueryUsingId(queryCode, QueryService.getInstance().getSQL(queryCode)+"\n ORDER BY c.TYPE_CD, c.CLASS1_CD)A ORDER BY sel DESC ",  new Object[]{ sCLASS1_CD, sDEL_YN, sTYPE_CD});
			} else {
			    rset = dba.executeQueryUsingId(queryCode, QueryService.getInstance().getSQL(queryCode)+"\n )A "+ sort, new Object[]{sCLASS1_CD, sDEL_YN, sTYPE_CD});
			}
			
			ArrayList list = new ArrayList();
			 
			while(rset.next()) {
				
				DetailClass2CodeDTO dto = new DetailClass2CodeDTO();
				
 				dto.setTYPE_CD(			rset.getString("TYPE_CD"));
				dto.setCLASS1_CD(		rset.getString("CLASS1_CD"));
				dto.setREG_DDTT(		rset.getString("REG_DDTT"));
				dto.setMODIFIER(		rset.getString("MODIFIER"));
				dto.setMOD_DDTT(		rset.getString("MOD_DDTT"));
				dto.setDEL_YN(			rset.getString("DEL_YN"));		
				dto.setCLASS1_NAME1(   	rset.getString("CLASS1_NAME1"));
				
				list.add(dto);
			} 
				
			return list;
			
		}catch(Exception e) {            
            
			throw new DAOException(e);
		}finally {
			if(rset != null) try{ rset.close(); } catch(Exception e) {}
			if(dba != null)try{dba.close();} catch(Exception e){}
		}
	}
	
    /** 코드값 반환
    *
    * @param code
    * @param msg
    * @return
    */
   public static String getCodeStringF(String[][] code, String msg)
   {
       try
       {
           for(int ii=0; ii < code[0].length; ii++)
           {
               if(code[0][ii].equals(msg.trim()))
               {
                   return code[1][ii];
               }
           }
           return msg;
       }catch(Exception e){
           return "&nbsp;";
       }
   }
    
	/**
	 * 코드와 코드명만 조회 ( CLASS 2)
	 * @param sTYPE_CD
	 * @param sCLASS1_CD
	 * @return
	 * @throws DAOException
	 */
	public String[][] getListClass1Code_T(String sTYPE_CD, String sCLASS1_CD) throws DAOException {
		 logger.println(LogLevel.INFO,this,"1111111111111" + sTYPE_CD+sCLASS1_CD);
	    return getListClass1Code_T(sTYPE_CD,sCLASS1_CD, null);
	}
	
	/**
	 * 코드와 코드명만 조회 ( CLASS 2)
	 * @param sTYPE_CD
	 * @param sCLASS1_CD
	 * @param sort
	 * @return
	 * @throws DAOException
	 */
	public String[][] getListClass1Code_T(String sTYPE_CD, String sCLASS1_CD, String sort) throws DAOException {
		 logger.println(LogLevel.INFO,this,"22222222222222" + sTYPE_CD+sCLASS1_CD);
		
		int count = 0;
		try {
			ArrayList dtoRecord = getCodeList(sTYPE_CD, sCLASS1_CD, "N", sort);
			count  = dtoRecord.size();
			//System.out.println("getListClass2Code============" + count);
			String[][] returnArr = new String[2][count];
			
			Iterator it = dtoRecord.iterator();

			int i = 0;
				
			while(it.hasNext()) { 
				it.next();
				DetailClass2CodeDTO dto = (DetailClass2CodeDTO)dtoRecord.get(i);
				returnArr[0][i] = dto.getCLASS1_CD();
				returnArr[1][i] = dto.getCLASS1_NAME1();
				i++;
			}

			return returnArr;
		} catch(DAOException e){
			e.printStackTrace();
            throw e;
		}
	}

    
	/**
	 * 수신 한 Record를 기반으로 송신 할 TC Data 생성
	 * 
	 * @param msgRecord, tcRecSet
	 * @return 생성 한 Key의 갯수
	 */
	public int makeTc(JDTORecord msgRecord, JDTORecordSet tcRecSet){
		
		String szMsg="";
		String szMethodName ="makeTc";
		int nRtc=-99;
		
		String szTcCode="";
		
		try{
			
			szTcCode =getTcCode(msgRecord);
			if( szTcCode==null || szTcCode.equals("")){
				szMsg="TC Code("+szTcCode+") Error";
				putLog(szSessionName, szMethodName, szMsg, 1);
				
				return -2;
				
			}			
			
		}catch(Exception e){
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			putLog(szSessionName, szMethodName, szMsg, 1);
			
			throw new EJBServiceException(e);
			
		} // end of try-catch	
		return 0;
	}
	
	public static String getTcDate(String type){
		SimpleDateFormat simpledateformat = new SimpleDateFormat(type);
        Date date = new Date();
        return simpledateformat.format(date);
    
	} // end of getTcDate()
	
					
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
	
	
    public static String msgService(String[][] arr, String code) {
    	
    	String returnMsg = msgService2(arr, code, 1);
    	
        return returnMsg;	//2005.0824 수정 return msg; --> return "<font color=red>"+code+"</font>"; 
    }
    
	/** 공통코드 값에서 Name 값 뽑아내기   
	 *    IN  : String[][] arr(공통코드에서 뽑아낸 Code 값), String code(레코드에서 뽑아낸 코드값), nameType(1:CLASS_NAME1, 2:CLASS_NAME2)
	 *    OUT : 레코드에서 뽑아낸 코드값의 Name Value
	*/
    public static String msgService2(String[][] arr, String code, int nameType) {
        int arrSize = arr[0].length;
        //String msg = "";

        for(int ii=0 ; ii<arrSize ; ii++) {
            if(code.equals(arr[0][ii])) {
                 return arr[nameType][ii]; // 2005.08.22 수정 (msg = arr[1][ii]; --> return msg = arr[1][ii];) 
            }
        }
        return "<font color=red>"+code+"</font>";	//2005.0824 수정 return msg; --> return "<font color=red>"+code+"</font>"; 
    }
	
	
	
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

			if(szValue==null)
				szValue="(null)";

			szMsg= "["+(i+1)+"]"
			     + "\t"+szRecKey
			     + "\t["+szValue+"]";
			System.out.println(szMsg);
				     
		} // end of for()
			
		return nRecCnt;
		
	} // end addFiller()
	
    
    public static String getLogData(String logData, Map itemLen) {
        int len				= 0;
        String val			= null;
        String[] equalSplit = null;
        String[] commaSplit = logData.split(",");
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < commaSplit.length; i++) {
            equalSplit = commaSplit[i].split("=");            
            for(int j = 0; j < equalSplit.length; j++) {
                if((j % 2) == 1) {
                    val = (String)itemLen.get(equalSplit[j-1].trim());
                    len = equalSplit[j].trim().length();
                    if(! val.equals(""+ len)) {
                        buffer.append(equalSplit[j].trim() + fillSpace(len, Integer.parseInt(val)));
                    }else {
                        buffer.append(equalSplit[j].trim());                        
                    }
                }
            }
        }
        return buffer.toString();    
    }

    /**
     * @param start	시작IDX
     * @param end	끝IDX
     */
    private static String fillSpace(int start, int end) {
        StringBuffer space = new StringBuffer();
        for(int i = start; i < end; i++) {
            space.append(" ");
        }
        return space.toString();
    }

    public static int getTotalLenOfTc(Map tc) {
        int total = 0;
        Iterator iter = tc.keySet().iterator();
        while(iter.hasNext()) {
            total += Integer.parseInt((String)(tc.get((String)iter.next())));
        }
        return total;
    }
    
    public static String makeLogMessage(String word, String length){
    	int stand_length = Integer.parseInt(length);
    	int user_length = word.trim().length();
    	String blankSTR = "";
    	for(int i = 0 ; i < stand_length - user_length ; i++){
    		blankSTR += " ";
    	}
    	return word+blankSTR;
    }
    
    public static String deletePoint(String val) {
        if(val.indexOf(".") == -1) {
            return val;
        }else {
            return val.substring(0, val.indexOf("."));
        }
    }
    
    public static String format(String val, int pos) {
        StringBuffer merge 	= new StringBuffer();
        int spotPos 		= val.indexOf(".");
        if(spotPos != -1) {
            String left 	= val.substring(0, spotPos);
            String right 	= val.substring(spotPos + 1, val.length());
            int sum = left.length() + right.length();
            if(sum > pos) {
                merge.append(left);
                merge.append(right.substring(0, (right.length() - (sum - pos))));                
            }else {
                merge.append(left);
                merge.append(right);
                if(sum != pos) {
                    appendFormat(merge, (pos - sum));
                }                
            }
        }else {
            if(val.length() > pos) {
                merge.append(val.substring(0, (val.length() - pos)));
            }else {
                merge.append(val);
                appendFormat(merge, (pos - val.length()));                
            }
        }
        return merge.toString();
    }

	/**
	 *      [A] 오퍼레이션명 : fillSpZr
	 * 
	 * @param String szData			// 변환대상 문자열
	 *        int    nLen 			// 변환 후 목적 문자열 길이
	 *        int    nChgMd			// 변환 방식 (0: 숫자열변환, !0: 문자열변환
	 * @return String 				// 변환 완료 된 문자열
	 * @throws
	 */
	public static String fillSpZr(String szData, int nLen, int nChgMd){
		
		String szFillData="";
		int i=0;
		int nDataLen =0;
		

		try{
			szFillData= szData.trim();
			nDataLen =szFillData.length();;
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
				else		  szFillData+=" ";
				
			} // end of for();
		
		} // end of try-catch
		
		return szFillData;

	} // end of fillSpZr()
	
	/**
	 * TC코드로 내부, 외부, Facade 송신을 구분해서 리턴한다
	 * @param  	inTcCode
	 * @return  1:내부JMS, 2:리모트 EAI, 3:L2 EAI, 9:Facade, 
	 *          0:Unknown, -1:Error
	 */
	public int chkTcType(String szTcCode)
	{
		String szMethodName = "chkTcType";
		
		
		String szChkID="";

	
		if( szTcCode==null || szTcCode.equals(""))	//	TC Code is null
			return -1;
		
		szTcCode.trim().toUpperCase();

		//
		// Facade 송신  Call Check
		//
		if( szTcCode.equals(FACADE_TCCODE))
			return 3;
		
		
		// Get Check ID
		szChkID=szTcCode.substring(4,5);

				
		//
		// TC Type Check
		//
		if("J".equals(szChkID) )		// 내부 JMS MSG
			return 1;
		else if("R".equals(szChkID))	// Remote EAI MSG
			return 2;
		else if("L".equals(szChkID))	// L2 System EAI MSG
			return 3;
		else							// Unknown MSG
			return 0;
		
		
	} // end of chkTcType()
	
	
		
	
    
    public static String format(String val, int left, int right) {
        if(val != null && ! "".equals(val)) {
            val = getSubPos(val, right + 1);
            StringBuffer buffer = new StringBuffer();
            appendFormat(buffer, left);
            if(right != 0) {
                buffer.append(".");
            }
            appendFormat(buffer, right);
            DecimalFormat df = new DecimalFormat(buffer.toString());
            return df.format(Float.parseFloat(val));            
        }else {
            return val;
        }        
    }
    
	
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
		long lngVal = 0;
		Long lngObj = null;
		boolean blnRtnVal = true;
		String szTemp = null;
		String szData = null;
		String szMsg = null;
		
		try {
			szTemp = this.paraRecChkNull(inRec, szFieldName);
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
				putLog(szClassName, szMethodName, szMsg, 1);
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
				if (chDataType == 'D') {
					dblVal = StringHelper.parseDouble(szData, this.makeErrorDouble(intPre, intPost));
					dblObj = new Double(dblVal);
					inRec.setField(szFieldName, dblObj);
				//long
				} else if (chDataType == 'L') {
					lngVal = StringHelper.parseLong(szData, this.makeErrorLong(intMaxLen));
					lngObj = new Long(lngVal);
					inRec.setField(szFieldName, lngObj);
				//Page Count 처리
				} else if (chDataType == 'P') {
					lngVal = StringHelper.parseLong(szData, 1);
					lngObj = new Long(lngVal);
					inRec.setField(szFieldName, lngObj);
				//Row Count 처리	
				} else if (chDataType == 'R') {	
					lngVal = StringHelper.parseLong(szData, 10);
					lngObj = new Long(lngVal);
					inRec.setField(szFieldName, lngObj);
					
				//String	
				} else
					inRec.setField(szFieldName, szData);
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szClassName + e.getMessage(), e);
		}
		
		return blnRtnVal;
	}
	
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
				if(szData.equals("") || ((!szData.equals("")) && szData.length() != intDataLen)) intRtnVal = -1;
			} else if (intNullChk == 1) {
				//not null이고 가변길이 체크
				if(szData.equals(""))
					intRtnVal = -1;
				else
					//제한길이보다 길면 cut
					if (szData.trim().length() > intDataLen) intRtnVal = -3;
			} else if (intNullChk == 2) {
				//가변길이 체크
				if (szData.equals(""))
					intRtnVal = 0;
				else
					//제한길이보다 길면 cut
					if (szData.trim().length() > intDataLen) intRtnVal = -3;
			} else if (intNullChk == 3)
				//no check
				intRtnVal = 0;
		} catch(Exception e) {
			
			szMsg = "Exception: " + e.getMessage();
			putLog(szClassName, szMethodName, szMsg, 1);
			return intRtnVal = 0;
			
		}
		return intRtnVal;
	} // end of chkParam
	
	
	
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
			if (inRec.getFieldString(szFieldName) != null
					&& inRec.getFieldString(szFieldName) != "")
				outRec.setField(szFieldName, inRec.getFieldString(szFieldName));
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szClassName + e.getMessage(), e);
		}
	} // end of mappingData
	
	
    /**
	 * 오퍼레이션명 : Get TC Code
	 * 
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public static String getTcCode(JDTORecord inRecord){

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
			throw new EJBServiceException(e);
		} // end of try-catch
		
		return szRcvTcCode;	
	} // end of getTcCode();
	

	
	/**
	 *      [A] 오퍼레이션명 : paraRecChkNull 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return String			     // 해당 필드의 데이터
	 * @throws JDTOException 
	 */
	public static String paraRecChkNull(JDTORecord recPara, String szFieldName) throws JDTOException {
		String szRtnVal = null;
		
		if (recPara.getFieldString(szFieldName) == null)
			szRtnVal = "";
		else
			szRtnVal = recPara.getFieldString(szFieldName);
		
		return szRtnVal;
	} // end of paraRecChkNull
	
	
	/**
	 *      [A] 오퍼레이션명 : paraRecChkNullInt
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return int			         // 해당 필드의 데이터
	 * @throws JDTOException 
	 */
	public static int paraRecChkNullInt(JDTORecord recPara, String szFieldName) throws JDTOException {
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
	} // end of paraRecChkNullInt
	
//	 
	// String type 형식에 맞춘 now 값 return
	// y:년, M:월, d:날, E:요일, a:오전/오후, 
	// H:시, m:분, s:초, S:밀리초
	//

	public static String get_CurDate(String type){
		SimpleDateFormat simpledateformat = new SimpleDateFormat(type);
        Date date = new Date();
        return simpledateformat.format(date);
    
	} // end of getCurDate()	
	
	/**
	 * 오퍼레이션명 : putLog
	 * 
	 * @param String szClassName	// Logging 요청 Class name
	 *        String szMethodName 	// Logging 요청 Method Name
	 *        String szLogMsg		// Logging Message
	 * @return
	 * @throws DAOException, JDTOException
	 */
	public static void putLog(String szClassName, String szMethodName, String szLogMsg, int nLogLevel)  {
		
		boolean bDebugFlag=false; 
		Logger logger =new Logger("ym"); 
		String szMsg="";
		String strCurDate = get_CurDate("yyyy-MM-dd HH:mm:ss:SSS");
		
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
				
			
				System.out.println("\n---<"+ strCurDate +">-----------------------------------");
				System.out.println(szMsg);
			
			} else {			

				// Message Logging
				switch(nLogLevel){
				case 1:
					logger.println(LogLevel.ERROR,"", szMsg);
					break;

				case 2:
					logger.println(LogLevel.WARNING, "", szMsg);
					break;

				case 3:
					logger.println(LogLevel.INFO, "", szMsg);
					break;

				default:
					logger.println(LogLevel.DEBUG, "", szMsg);
				break;


				} // end of switch(nLogLevel)
				
			} // end of if(bDebugFlag)		
	
			
		}catch (Exception e){
			
			szMsg =szClassName + "::" + szMethodName +"() " + "\n\t"+szMethodName+" Exception Error : "+e.getLocalizedMessage();
			if(bDebugFlag)
				System.out.println(szMsg);
			else
				logger.println(LogLevel.ERROR, "", szMsg);
				
		} // end of try-catch()
		
	} // end of putLog();
	
	
    
    /**
     * @param val
     * @param right
     * @return
     */
    private static String getSubPos(String val, int right) {
        float tempVal = 10;
        for(int i = 0; i < right-2; i++) {
            tempVal *= 10;
        }
        return String.valueOf((int)(Float.parseFloat(val)*tempVal) / tempVal);
    }

    /**
     * @param string
     */
    private static void appendFormat(StringBuffer buffer, int cnt) {
        for(int i = 0; i < cnt; i++) {
            buffer.append("0");
        }
    }
    
    public static int[] getIntYMDHMS() {
        String now = getStringYMDHMS();
        return new int[]{
                Integer.parseInt(now.substring(0,4)),
                Integer.parseInt(now.substring(4,6)),
                Integer.parseInt(now.substring(6,8)),
                Integer.parseInt(now.substring(8,10)),
                Integer.parseInt(now.substring(10,12)),
                Integer.parseInt(now.substring(12,14))};
    }
    
    public static String getStringYMDHM() {
        return getCurDate("yyyyMMddHHmm");
    }
    
    public static String getStringYMDHMS() {
        return getCurDate("yyyyMMddHHmmss");
    }    

    public static String getStringHMS() {
        return getCurDate("HHmmss");
    }    

    public static String getStringHMS(String seperate) {
        return getCurDate("HH"+ seperate +"mm"+ seperate +"ss");
    }    

    public static String getStringYMD() {
        return getCurDate("yyyyMMdd");
    }    

    public static String getStringSubYMD() {
        return getCurDate("yyMMdd");
    }    

    public static String getStringYMD(String seperate) {
        return getCurDate("yyyy"+ seperate +"MM"+ seperate +"dd");
    }    

    public static String addZero(String source) {
        if(source.length() == 1) {
            source = "0" + source;
        }
        return source;
    }
    
    /**
     * NULL을 "-"으로 변환하는 method이다.
     *
     * @param value : request parameter값
     * 
     * @return      : String
     */
    public static String checkNullToString(String value){    
		if(value == null | value.equals("") | value.length() < 1 | value.equals("null")){
			return "-";
		}else{
			return value;
		}

    }
    
    
    public static String getCodeToName(String class1, String class2, String code)throws DAOException{
    	ymCommonDAO ycd = ymCommonDAO.getInstance();
    	Object obj[] = {class1, class2, code};
    	String queryId = "ym.common.dao.ymCommonDAO.getCodeToName";
    	JDTORecord jdt = ycd.getCodeToName(queryId,obj);    
    	if(jdt != null){
    		return jdt.getFieldString("CLASS2_NAME1");   	
    	}else{
    		return "";
    	}
    }
    
    public static String getCodeToName(String class1, String class2, String code1, String code2)throws DAOException{
    	ymCommonDAO ycd = ymCommonDAO.getInstance();
    	Object obj[] = {class1, class2, code1, code2};
    	String queryId = "ym.common.dao.ymCommonDAO.getCodeToName2";
    	JDTORecord jdt = ycd.getCodeToName(queryId,obj);    
    	if(jdt != null){
    		return jdt.getFieldString("CLASS3_NAME1");   	
    	}else{
    		return "";
    	}
    }
    
    public static String getCodeToName(String class1, String class2) throws DAOException{
    	ymCommonDAO ycd = ymCommonDAO.getInstance();
    	Object obj[] = {class1, class2};
    	String queryId = "ym.common.dao.ymCommonDAO.getCodeToName4";
    	JDTORecord jdt = ycd.getCodeToName(queryId,obj);    
    	if(jdt != null){
    		return jdt.getFieldString("CLASS1_NAME1");   	
    	}else{
    		return "";
    	}
    }
    
    public static String getCodeToName(String class1, String class2, String code1, String code2, String code3)throws DAOException{
    	ymCommonDAO ycd = ymCommonDAO.getInstance();
    	Object obj[] = {class1, class2, code1, code2, code3};
    	String queryId = "ym.common.dao.ymCommonDAO.getCodeToName3";
    	JDTORecord jdt = ycd.getCodeToName(queryId,obj);    
    	if(jdt != null){
    		return jdt.getFieldString("CLASS4_NAME1");   	
    	}else{
    		return "";
    	}
    }



   
	/**
     * 구 설비코드를 신설비코드로 변환하여 반환
     * @param yd_gp : 야드구분
     * @param facility_gp : 구 설비코드
     * 
     * @return      : String
     */
	public static String convertEquipNo(String yd_gp, String facility_gp)throws DAOException{
    	ymCommonDAO ycd = ymCommonDAO.getInstance();
    	Object obj[] = {yd_gp, facility_gp};
    	String queryId = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.convertEquipNo";
    	JDTORecord jdt = ycd.getCodeToName(queryId,obj);    
    	if(jdt != null){
    		return jdt.getFieldString("EQUIPNO");   	
    	}else{
    		return "-";
    	}
    }
	
	

	/**
     * 신 설비코드를 구설비코드로 변환하여 반환
     * @param yd_gp : 야드구분
     * @param facility_gp : 신 설비코드
     * 
     * @return      : String
     */
	public static String convertEquipNo2(String yd_gp, String new_facility_gp)throws DAOException{
    	ymCommonDAO ycd = ymCommonDAO.getInstance();	
    	Object obj[] = {yd_gp, new_facility_gp};
    	String queryId = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.convertEquipNo2";
    	JDTORecord jdt = ycd.getCodeToName(queryId,obj);    
    	if(jdt != null){
    		return jdt.getFieldString("EQUIPNO");   	
    	}else{
    		return "-";
    	}
    }
    



    /**
     * NULL 데이터를 명시적으로 변환하는 method이다.
     * @param value : request parameter값
     * @param param : null 일경우 처리값
     * 
     * @return      : String
     */
    public static String checkNullToString(Object value, String param){    
    	return value != null ? value.toString() : param;
    }




    /**
     * "yyyyMMddhhmmss"형식을 yyyy-MM-dd hh:mm:ss으로 합치는 기능이다. 
     * DB 형식을 맞추기 위함이다.
     * @param   String  : "yyyy-MM-dd hh:mm:ss"
     * @return  String  : "yyyyMMddhhmmss"
     */
    public static String getHabString(String dateTime){   
    	if(dateTime.length() >= 12){
	        return new StringBuffer(dateTime.substring(0, 4)).append("-")   //y
	               .append(dateTime.substring(4, 6)).append("-")            //m
	               .append(dateTime.substring(6, 8)).append(" ")            //d
	               .append(dateTime.substring(8, 10)).append(":")           //h
	               .append(dateTime.substring(10, 12)).toString();       //m
    	}else{
    		return dateTime;
    	}
    	

    }

	/**
     * "yyyyMMddhhmmss"형식을 yyyy-MM-dd hh:mm:ss으로 합치는 기능이다. 
     * DB 형식을 맞추기 위함이다.
     * @param   String  : "yyyy-MM-dd"
     * @return  String  : "yyyyMMddhhmmss"
     */
    public static String getHabString2(String dateTime){   
    	if(dateTime.length() >= 8){
	        return new StringBuffer(dateTime.substring(0, 4)).append("-")   //y
	               .append(dateTime.substring(4, 6)).append("-")            //m
	               .append(dateTime.substring(6, 8)).toString();          //d
    	}else{
    		return dateTime;
    	}
    	

    }
    
    public static List getSchKindLisk(String ydgp, String baygp){
    	YdSchRuleDAO ydschruleDao = null;	    
	    List coilHis = null;
	    try{
	    	ydschruleDao = new YdSchRuleDAO();
	    	coilHis = ydschruleDao.getListData("ym.scheduling.crane.dao.YdSchRuleDAO.getListSchRuleLov",new Object[]{ydgp, baygp});
	    	return coilHis;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
    
    public static List getSchKindLisk2(String ydgp, String baygp){
    	YdSchRuleDAO ydschruleDao = null;	    
	    List coilHis = null;
	    try{
	    	ydschruleDao = new YdSchRuleDAO();
	    	coilHis = ydschruleDao.getListData("ym.scheduling.crane.dao.YdSchRuleDAO.getListSchRuleLov2",new Object[]{ydgp, baygp});
	    	return coilHis;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
    
    /*

    public static String betweenHour(String fromdate, String todate){
    	
    	DateHelper datehelper =  new DateHelper();
    	
    	if(fromdate != null && fromdate.length() == 12 && todate != null && todate.length() == 12){
    		return datehelper.betweenHour(datehelper.toUtilDate(Integer.parseInt(fromdate.substring(0,4)),Integer.parseInt(fromdate.substring(4,6)),Integer.parseInt(fromdate.substring(6,8)),Integer.parseInt(fromdate.substring(8,10)),Integer.parseInt(fromdate.substring(10,12))),datehelper.toUtilDate(Integer.parseInt(todate.substring(0,4)),Integer.parseInt(todate.substring(4,6)),Integer.parseInt(todate.substring(6,8)),Integer.parseInt(todate.substring(8,10)),Integer.parseInt(todate.substring(10,12)))) + "";
    	}else{
    		return "입력된 파라미터가 null 이거나 길이가 맞지 않습니다.";
    	}
    }*/
    
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
		}catch(Exception e){}
		
		//LogService.getInstance().getLogger("ym").println(LogLevel.DEBUG, "[FilltTOString] [입력:"+in_strValue+","+in_intLength+"] [출력:"+in_strRet+"]"); 
		return in_strRet;
    }
    
    public static String FillToStringDesc(String in_strValue, int in_intLength )
	{
		String in_strRet = "";
		try{	
			if (CommonUtil.getLength(in_strValue) > in_intLength){
				in_strRet = CommonUtil.substr(in_strValue, 0, in_intLength);
			}else{
				in_strRet = MakeSpace(in_intLength - CommonUtil.getLength(in_strValue)," ") + in_strValue  ;
			}
		}catch(Exception e){}
		return in_strRet;
    }
    
    /**
     * YJK
     * 일정 길이만큼 앞에 공백을 채운다.
     * @String in_strValue, int in_intLength 
     */
	public static String FillToNumber(String in_strValue, int in_intLength )
	{
		String in_strRet = "";
		try{	
			if (CommonUtil.getLength(in_strValue) > in_intLength){
				in_strRet = CommonUtil.substr(in_strValue, 0, in_intLength);
			}else{
				in_strRet = MakeSpace(in_intLength - CommonUtil.getLength(in_strValue),"0") + in_strValue  ;
			}
		}catch(Exception e){}
		return in_strRet;
    }
    
    /**
     * YJK
     * 현재일자를 여러형태의 TYPE 으로 리턴한다.
     * ex) yyyy-mm-dd, hh-mm-ss, yyyyMMddhhmmss
     */ 
	public static String getCurDate(String type){
		SimpleDateFormat simpledateformat = new SimpleDateFormat(type);
        Date date = new Date();
        return simpledateformat.format(date);
    }
    
    /**
     * YJK
	 * A열연인 경우 --
	 * Legacy 적치단 정보를 현재 운영중인 적치단 정보로 수정
     *
     * ex) H C 05  3  2 05 - legacy system
     *     1 C 05 03 05 02 - current system   
     *	   LCAR,LSPMI,SCL01,LHFPI,LHCVO,LCAR  
     * @param  String
     * @return String
     * @throws  
     */			 
	public static String setCurPositionWithLegacy(String sUp_Position,
												  String sYd_Gp,
												  String sBay_Gp)
	{	
		String sPosition = "";
		
		if(sUp_Position.trim().length() == 7){
			
			sPosition = sYd_Gp 	+ 
    					sBay_Gp +
						"TR0"	+
						sUp_Position.substring(2,3)+
						YmCommonConst.STACK_BED_GP_01 + 
			    		YmCommonConst.STACK_LAYER_GP_01;
			    		
		}else if(sUp_Position.trim().length() == 8){
			
			sPosition = "1"+sUp_Position.substring(1,2)+
							sUp_Position.substring(2,4)+
	        			"0"+sUp_Position.substring(4,5)+
	        				sUp_Position.substring(6,8)+
	        			"0"+sUp_Position.substring(5,6);
		}else{
			 
			ymCommonDAO dao = ymCommonDAO.getInstance();
			String 		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCurEquipNoWithLegacyEquipNo";
			JDTORecord 	equipV   = dao.getCommonInfo(sQueryId,new Object[]{sUp_Position.trim()});
		
			if(equipV != null){
    
				//A열연 SPM 재작업 추출인 경우 
				if("LSPMI".equals(sUp_Position.trim()) && "1".equals(sYd_Gp) && "E".equals(sBay_Gp)){
					sPosition = sYd_Gp + 
								sBay_Gp +
								"KE02" +
				    			YmCommonConst.STACK_BED_GP_01 + 
				    			YmCommonConst.STACK_LAYER_GP_01;
				}else {
					sPosition = sYd_Gp + 
	    						sBay_Gp +
								StringHelper.evl(equipV.getFieldString("EQUIP_GP"), "") +
				    			YmCommonConst.STACK_BED_GP_01 + 
				    			YmCommonConst.STACK_LAYER_GP_01;
				}
    		}
	    }
	    
		return sPosition;
	}
	
	/**
     * YJK
	 * A열연인 경우 --
	 * 현재 운영중인 적치단 정보를 Legacy 적치단 정보로 수정
     *
     * ex) H C 05  3  2 05 - legacy system
     *     1 C 05 03 05 02 - current system   
     *	   LCAR,LSPMI,SCL01,LHFPI,LHCVO,LCAR  
     * @param  String
     * @return String
     * @throws  
     */			 
	public static String setLegacyPositionWithCur(String sUpLoc)
	{	
		String sPosition = "";
		  
		if(sUpLoc.length() == 10){
			
			ymCommonDAO dao = ymCommonDAO.getInstance();
			String 		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getLegacyEquipNoWithCurEquipNo";
			JDTORecord 	jtR   	 = dao.getCommonInfo(sQueryId,new Object[]{sUpLoc.substring(0, 6)});
    		
    		if(jtR != null){
    			sPosition = StringHelper.evl(jtR.getFieldString("EQUIP_GP"), "");
    		}else{
			    sPosition = "H" +
						 	sUpLoc.substring(1, 4)+
						 	sUpLoc.substring(5, 6)+
						 	sUpLoc.substring(9,10)+
					     	sUpLoc.substring(6, 8);
			}
		}
	    return sPosition;
	}
	
	/**
     * YJK
	 * 코일 공통 테이블의 진도코드를 참조해서 
	 * 저장품이동조건을 가져온다.
     *
     * @param  String	:	저장품ID
     *
     * @return String
     * @throws  
     */			 
	public static String[] getCoilCurrProgCd(String sStockId,String TcCode)
	{	
		String[] rVal = new String[2];
		
		String sProgCd   = "";
		String sStocMv   = "";
		String sReturnGp = "";
			
		ymCommonDAO dao = ymCommonDAO.getInstance();

		String sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilCommonInfo";	
		
		JDTORecord 	jtR = dao.getCommonInfo(sQueryId,new Object[]{sStockId});
		
		if(jtR != null){
    		sProgCd 	= StringHelper.evl(jtR.getFieldString("CURR_PROG_CD"), "");
    		sReturnGp	= StringHelper.evl(jtR.getFieldString("RETURN_GP"), "");
    	}
    	/* AB열연 진도코드
    	if(YmCommonConst.CURR_PROG_CD_COIL_1.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_1C;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_3.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_3C;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_A.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_AC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_B.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_BC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_C.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_CC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_D.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_DC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_E.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_EC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_F.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_FC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_G.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_GC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_H.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_HG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_J.equals(sProgCd)){
    		if(YmCommonConst.RETURN_GP_1.equals(sReturnGp)){
    			sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_JR;
    		}else{
    			sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_JG;
    		}
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_K.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_KG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_L.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_LG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_M.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_MG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_X.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_XG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_Y.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_YG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_Z.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_ZG;
    	}															
    	*/
    	
    	
//    	DMYDR002   //코일제품보류확정             없음
//    	DMYDR005   //코일제품출하지시대기      K    
//    	DMYDR008   //코일제품반납대기            J  
//    	DMYDR011   //코일제품고간이송지시  없음        
//    	DMYDR014   //코일제품목전              주문번호
//    	DMYDR020   //코일제품운송지시        L      
//    	DMYDR023   //코일제품상차지시       없음 
//    	DMYDR027   //코일제품보관지시        M    
//    	DMYDR030   //코일제품출하완료        M  
//    	DMYDR033   //코일제품반품	 	  K			
//    	DMYDR036   //코일제품출하차량도착실적 	없음       
//    	DMYDR037   //코일임가공차량도착실적 		없음       
//    	DMYDR040   //코일제품출하차량출발실적 	없음       
//    	DMYDR041   //코일임가공차량출발실적		없음       
    	
    	
    	// 일관제철 진도코드
    	if(YmCommonConst.DMYDR008.equals(TcCode)){			//코일제품반납대기
    		if(YmCommonConst.RETURN_GP_1.equals(sReturnGp)){
    			sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_JR;
    		}else{
    			sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_JG;
    		}
    	}else if(YmCommonConst.DMYDR005.equals(TcCode)||			//코일제품출하지시대기 
    			YmCommonConst.DMYDR004.equals(TcCode)|| 			//외판슬라브출하지시대기
    			YmCommonConst.DMYDR033.equals(TcCode)){				//코일제품반품
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_KG;
    	}else if(YmCommonConst.DMYDR027.equals(TcCode)||			//코일제품보관지시 
    			 YmCommonConst.DMYDR030.equals(TcCode)){			//코일제품출하완료
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_MG;
    	}else if(YmCommonConst.DMYDR016.equals(TcCode)){			//외판슬라브운송지시대기
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_NG;
    	}else if(YmCommonConst.DMYDR020.equals(TcCode)||			//코일제품운송지시
    			 YmCommonConst.DMYDR022.equals(TcCode) ){			//외판슬라브운송상차지시 
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_LG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_A.equals(sProgCd)||
    			YmCommonConst.CURR_PROG_CD_COIL_R.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_AC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_B.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_BC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_C.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_CC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_D.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_DC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_E.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_CS;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_F.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_FC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_K.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_KG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_G.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_GC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_H.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_HG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_J.equals(sProgCd)){
    		if(YmCommonConst.RETURN_GP_1.equals(sReturnGp)){
    			sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_JR;
    		}else{
    			sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_JG;
    		}
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_L.equals(sProgCd)){//코일제품상차지시 
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_LG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_N.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_NG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_M.equals(sProgCd)||
    			YmCommonConst.CURR_PROG_CD_COIL_P.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_MG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_X.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_XG;	
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_Y.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_YG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_Z.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_ZG;
    	}															
    	
    	rVal[0] = sProgCd;
    	rVal[1] = sStocMv;
		    	
	    return rVal;
	}
	
	
	/**
     * 
	 * 진도코드로 저장품이동조건을 가져온다.
	 * 
     *
     * @param  String	:	저장품ID
     *
     * @return String
     * @throws  
     */			 
	public static String[] getCoilCurrProgCd2(String sStockId ,String sProgCd)
	{	
		String[] rVal = new String[2];
		
		String sStocMv   = "";
		String sReturnGp = "";
			
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		String sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilCommonInfo";	
		
		JDTORecord 	jtR = dao.getCommonInfo(sQueryId,new Object[]{sStockId});
    		
    	if(jtR != null){
    		sReturnGp	= StringHelper.evl(jtR.getFieldString("RETURN_GP"), "");
    	}
    	
    	
//    	DMYDR002   //코일제품보류확정             없음
//    	DMYDR005   //코일제품출하지시대기      K    
//    	DMYDR008   //코일제품반납대기            J  
//    	DMYDR011   //코일제품고간이송지시  없음        
//    	DMYDR014   //코일제품목전              주문번호
//    	DMYDR020   //코일제품운송지시        L      
//    	DMYDR023   //코일제품상차지시       없음 
//    	DMYDR027   //코일제품보관지시        M    
//    	DMYDR030   //코일제품출하완료        M  
//    	DMYDR033   //코일제품반품	 	  K			
//    	DMYDR036   //코일제품출하차량도착실적 	없음       
//    	DMYDR037   //코일임가공차량도착실적 		없음       
//    	DMYDR040   //코일제품출하차량출발실적 	없음       
//    	DMYDR041   //코일임가공차량출발실적		없음       
    	
    	
    	// 일관제철 진도코드
    	
    	if(YmCommonConst.CURR_PROG_CD_COIL_B.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_BC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_A.equals(sProgCd)||
    			YmCommonConst.CURR_PROG_CD_COIL_R.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_AC;	
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_C.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_CC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_D.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_DC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_E.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_CS;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_F.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_FC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_K.equals(sProgCd)||
    			YmCommonConst.CURR_PROG_CD_COIL_N.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_KG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_G.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_GC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_H.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_HG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_J.equals(sProgCd)){
    		if(YmCommonConst.RETURN_GP_1.equals(sReturnGp)){
    			sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_JR;
    		}else{
    			sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_JG;
    		}
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_L.equals(sProgCd)){//코일제품상차지시 
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_LG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_M.equals(sProgCd)||
    			YmCommonConst.CURR_PROG_CD_COIL_P.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_MG;	
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_N.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_NG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_X.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_XG;	
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_Y.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_YG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_Z.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_ZG;
    	}															
    	
    	rVal[0] = sProgCd;
    	rVal[1] = sStocMv;
		    	
	    return rVal;
	}
	
	/**
     * 2007.04.17 이정훈
	 * 코일 공통 테이블의 진도코드를 참조해서 
	 * 저장품이동조건을 가져온다.
     *
     * @param  String	:	저장품ID
     *
     * @return String
     * @throws  
     */			 
	public static String[] getCoilCurrProgCd_PO(String sStockId)
	{	
		String[] rVal = new String[3];
		
		String sProgCd   = "";
		String sStocMv   = "";
		String sReturnGp = "";
		String sDemander = "";
			
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		String sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilCommonInfo_PO";	
		
		JDTORecord 	jtR = dao.getCommonInfo(sQueryId,new Object[]{sStockId});
    		
    	if(jtR != null){
    		sProgCd 	= StringHelper.evl(jtR.getFieldString("CURR_PROG_CD"), "");
    		sReturnGp	= StringHelper.evl(jtR.getFieldString("RETURN_GP"), "");
    		sDemander   = StringHelper.evl(jtR.getFieldString("수요가"), "");
    	}
    	/* AB열연 진도코드
    	if(YmCommonConst.CURR_PROG_CD_COIL_1.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_1C;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_3.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_3C;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_A.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_AC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_B.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_BC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_C.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_CC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_D.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_DC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_E.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_EC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_F.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_FC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_G.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_GC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_H.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_HG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_J.equals(sProgCd)){
    		if(YmCommonConst.RETURN_GP_1.equals(sReturnGp)){
    			sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_JR;
    		}else{
    			sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_JG;
    		}
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_K.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_KG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_L.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_LG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_M.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_MG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_X.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_XG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_Y.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_YG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_Z.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_ZG;
    	}															
    	*/
    	// 일관제철 진도코드
    	if(YmCommonConst.CURR_PROG_CD_COIL_B.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_BC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_A.equals(sProgCd)||
    			YmCommonConst.CURR_PROG_CD_COIL_R.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_AC;	
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_C.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_CC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_D.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_DC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_E.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_CS;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_F.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_FC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_G.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_GC;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_H.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_HG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_J.equals(sProgCd)){
    		if(YmCommonConst.RETURN_GP_1.equals(sReturnGp)){
    			sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_JR;
    		}else{
    			sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_JG;
    		}
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_K.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_KG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_L.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_LG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_M.equals(sProgCd)||
    			YmCommonConst.CURR_PROG_CD_COIL_P.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_MG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_N.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_NG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_X.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_XG;	
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_Y.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_YG;
    	}else if(YmCommonConst.CURR_PROG_CD_COIL_Z.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_ZG;
    	}															
    	
    	rVal[0] = sProgCd;
    	rVal[1] = sStocMv;
    	rVal[2] = sDemander;
		    	
	    return rVal;
	}
	/**
     * YJK
	 * 슬라브 공통 테이블의 진도코드를 참조해서 
	 * 저장품이동조건을 가져온다.
     *
     * @param  String	:	저장품ID
     *
     * @return String
     * @throws  
     */			 
	public static String[] getSlabCurrProgCd(String sStockId ,String TcCode)
	{	
		String[] rVal = new String[2];
		
		String sProgCd   = "";
		String sStocMv   = "";
		String sWO_MSLAB_RPR_MTD= "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		/*
		--ym.common.dao.selectSlabMatirialInfo
		SELECT  SLAB_NO,
		        PLAN_SLAB_NO,               --예정 SLAB 번호
		        BUY_SLAB_NO,                --구입 SLAB 번호
		        ORD_NO,                     --주문 번호
		        ORD_DTL,                    --주문 행번
		        CC_PLNT_GP  AS PLANT_GP,    --공장구분
		        SLAB_T      AS SLAB_T,      --SLAB 두께
		        SLAB_W      AS SLAB_W,	    --SLAB 폭
		        SLAB_LEN    AS SLAB_LEN,	--SLAB 길이
		        CAL_SLAB_WT AS SLAB_WT,	    --SLAB 중량
		        CURR_PROG_CD,
		        HEAT_NO,
		        SPEC_ABBSYM,		        -- 규격약호
		        INGR_STAMP_GRADE,
		        REAGENT_PICK_TARGET_YN,     -- 시편채취유무
				REAGENTPICK_DONE_YN,        -- 시편완료유무
				SCARFING_YN,                -- Scarfing유무
				SCARFING_DONE_YN,           -- Scarfing완료유무
		        WO_MSLAB_RPR_MTD,	        -- Scarfing Pattern
				SCARFING_DEPTH,		        -- Scarfing 깊이
				'' AS INGR_C,				-- 성분C
				SLAB_WO_RT_CD,              -- SLAB지시행선코드
				ORD_HCR_GP,                 -- WCR/CCR 구분
				DECODE(ORD_HCR_GP,NULL,'0',
		               DECODE(LEAST(TRUNC((SYSDATE - SLAB_CREATE_DDTT)*24),DECODE(SCARFING_YN,'Y',24,12)),DECODE(SCARFING_YN,'Y',24,12),'0',NULL,'0','1')             
		        )AS TIMES
		FROM  (SELECT 
		        *
		       FROM VW_YD_SLABCOMM A, TB_QM_BUYSLABINFO B
		       WHERE A.MSLAB_NO = B.MSLAB_NO(+)
		      )SLABCOMM
		WHERE SLABCOMM.SLAB_NO = :SLAB_NO
		*/
		String sQueryId  = "ym.common.dao.selectSlabMatirialInfo";
		
		JDTORecord 	jtR = dao.getCommonInfo(sQueryId,new Object[]{sStockId});
    		
    	if(jtR != null){
    		sProgCd = StringHelper.evl(jtR.getFieldString("CURR_PROG_CD"), "");
    		sWO_MSLAB_RPR_MTD= StringHelper.evl(jtR.getFieldString("WO_MSLAB_RPR_MTD"), "");
    	}
    	/* AB열연 진도코드 
	if(YmCommonConst.CURR_PROG_CD_SLAB_1.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_1S;
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_3.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_3S;
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_A.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_AS;
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_B.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_BS;
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_C.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_CS;
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_D.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_DS;
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_E.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_ES;
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_F.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_FS;
    	
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_K.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_KS;
	}else if(YmCommonConst.CURR_PROG_CD_SLAB_L.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_LS;
	}else if(YmCommonConst.CURR_PROG_CD_SLAB_M.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_MS;    		    		
    		
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_Y.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_YS;
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_Z.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_ZS;
    	}															
    	*/
    	/* 일관제철 진도코드 */
    	if(YmCommonConst.DMYDR016.equals(TcCode) ){				//외판슬라브운송지시대기
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_NS;
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_0.equals(sProgCd)){    		
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_11; 
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_1.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_12;	
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_A.equals(sProgCd)){
    		if("Q".equals(sWO_MSLAB_RPR_MTD)){
        		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_D3;	//핸드스카핑작업대기
    		}else{
    			sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_DS;
    		}
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_B.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_ES;
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_C.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_FS;
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_D.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_BS;
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_E.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_CS;
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_F.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_YS;
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_G.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_GS; // 종합판정대기
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_H.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_HS; // 입고대기
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_J.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_JS; // 반납대기
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_K.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_KS;
	}else if(YmCommonConst.CURR_PROG_CD_SLAB_L.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_LS;
	}else if(YmCommonConst.CURR_PROG_CD_SLAB_M.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_MS;    		
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_N.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_NS;    	
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_Y.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_ZS;    	
    	}else if(YmCommonConst.CURR_PROG_CD_SLAB_Z.equals(sProgCd)){
    		sStocMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_ZS;
    	}															
    	
    	rVal[0] = sProgCd;
    	rVal[1] = sStocMv;
		    	
	    return rVal;
	}
	
	/**
     * YJK
	 *
	 *	현재 TR 정보의 A열연 Legacy 위치정보로 바꾼다.
	 *
	 * @param  String
     * @return String
     * @throws  
     */		
	public static String setLegacyPositionWithCurTr(String sUpLoc,String sStockId)
	{	
		String sPosition = "";
		  
		if(sUpLoc.length() == 10){
			
			sPosition = "T" + 
    		 			sUpLoc.substring(1,2) +
    		 			sUpLoc.substring(5,6);
    		 			 
			ymCommonDAO dao = ymCommonDAO.getInstance();
			String 		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getDmCarInfo_PIDEV";
			JDTORecord 	jtR   	 = dao.getCommonInfo(sQueryId,new Object[]{sStockId});
    		 
    		if(jtR != null){
    			sPosition += StringHelper.evl(jtR.getFieldString("CAR_NO_ADDR"), "");
    		}
		}
	    return sPosition;
	}
	
	
	/**
     * YJK
	 * 임가공 PIDEV
	 *	현재 TR 정보의 A열연 Legacy 위치정보로 바꾼다.
	 *
	 * @param  String
     * @return String
     * @throws  
     */		
	public static String setLegacyPositionWithCurTrPI(String sUpLoc,String sStockId)
	{	
		String sPosition = "";
		  
		if(sUpLoc.length() == 10){
			
			sPosition = "T" + 
    		 			sUpLoc.substring(1,2) +
    		 			sUpLoc.substring(5,6);
    		 			 
			ymCommonDAO dao = ymCommonDAO.getInstance();
			String 		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getDmCarInfo_PIDEV";
			JDTORecord 	jtR   	 = dao.getCommonInfo(sQueryId,new Object[]{sStockId});
    		 
    		if(jtR != null){
    			sPosition += StringHelper.evl(jtR.getFieldString("CAR_NO_ADDR"), "");
    		}
		}
	    return sPosition;
	}	
	
	/**
     * YJK
	 *
	 *	해당위치의 적치열 용도코드를 가뎌온다.
	 *
	 * @param  String
     * @return String
     * @throws  
     */			
	public static String getStackColInfoWithPk(String sCol)
	{	
		String sPutUsageCd = "";
		
		ymCommonDAO dao 		= ymCommonDAO.getInstance();
		String 		sQueryId 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackColInfoWithPk";
		JDTORecord 	jtR   	 	= dao.getCommonInfo(sQueryId,new Object[]{sCol});
		 
		if(jtR != null){
			sPutUsageCd = StringHelper.evl(jtR.getFieldString("STACK_COL_USAGE_CD"), "");
		}
		
		return sPutUsageCd;
	}
	
	/**
     * YJK
	 * LEVEL2전문을 통해 오는 항목을 
	 * 현 시스템 사용항목값으로 변환한다.
     * ex) 01 --> M(주작업)
     *	   02 --> S(보조작업)
     * @param  String
     * @return String
     * @throws  
     */			 
    public static String getCurDataWithLegacy(String sData)
	{	
		if(sData.length() == 2){
			sData = "0"+sData.substring(1);
		}
		if(YmCommonConst.MAIN_WORK_01.equals(sData)){
			sData = YmCommonConst.MAIN_WORK_M;
		}else if(YmCommonConst.SUB_WORK_02.equals(sData)){
			sData = YmCommonConst.SUB_WORK_S;
		}
		return sData;
	}
	public static String getLegacyDataWithCur(String sData)
	{	
		if(YmCommonConst.MAIN_WORK_M.equals(sData)){
			sData = YmCommonConst.MAIN_WORK_01 ;
		}else if(YmCommonConst.SUB_WORK_S.equals(sData)){
			sData = YmCommonConst.SUB_WORK_02;
	    }
		return sData;
	}
	
	/**
     * YJK
	 * 적치단,열의 상,하,좌,우 정보를 포맷에 맞춰 가져온다.
     * TYPE P - +1
     *      M - -1
     * ex) '03' -> '02'
     *
     * @param  String
     * @return String
     * @throws 
     */			 
	public static String changeLayerFormat(String sStr , String sType)
	{	
		java.text.DecimalFormat df = new java.text.DecimalFormat("00");
		  
		long lVal = Long.parseLong(sStr);
		
		if("P".equals(sType)){
			lVal = lVal + 1;
		}else if("M".equals(sType)){
			lVal = lVal - 1;
		}
		
		return df.format(lVal);
	}
	
	/**
     * YJK
	 * LINE IN 작업인지를 판단한다.
     * LiNE IN 작업대상중에 Buffer식 관리정보만 해당한다.
     *
     * @param  String
     * @return boolean
     * @throws 
     */	
	public static boolean isLineInWork(String sSchCode)
	{
	   boolean isTrue = false;
	   // CGS 추가
	   // #2 SPM에 대한 내용 추가.
	   // #2 HFL에 대한 내용 추가. 2010-02-05
	   if(
	      YmCommonConst.NEW_SCH_WORK_KIND_CKLI.equals(sSchCode)||  // SPM 보급
	   	  YmCommonConst.NEW_SCH_WORK_KIND_CKTI.equals(sSchCode)||  // SPM Take In
	   	  YmCommonConst.NEW_SCH_WORK_KIND_EQLI.equals(sSchCode)||  // EQL 보급
	   	  YmCommonConst.NEW_SCH_WORK_KIND_EQTI.equals(sSchCode)||  // EQL Take In
	   	  YmCommonConst.NEW_SCH_WORK_KIND_CFTI.equals(sSchCode)||  // HFL Take In
	   	  YmCommonConst.NEW_SCH_WORK_KIND_CFLI.equals(sSchCode)||  // HFL 보급
	   	  YmCommonConst.NEW_SCH_WORK_KIND_SSLI.equals(sSchCode)||    // SCARFING 보급
	   	  YmCommonConst.NEW_SCH_WORK_KIND_CNLI.equals(sSchCode)||	// #2 SPM 보급
	   	  YmCommonConst.NEW_SCH_WORK_KIND_CNTI.equals(sSchCode)||	// #2 SPM Take In
	   	  YmCommonConst.NEW_SCH_WORK_KIND_CHLI.equals(sSchCode)||	// #2 HFL 보급
	   	  YmCommonConst.NEW_SCH_WORK_KIND_CFSI.equals(sSchCode)	  // HFL 결속대 보급
	   	 ){
	   		isTrue = true; 	
	   	} 
		return isTrue;
	}
	
	/**
     * YJK
	 * LINE OFF 작업인지를 판단한다.
     *
     * @param  String
     * @return boolean
     * @throws 
     */	
	public static boolean isLineOffWork(String sSchCode)
	{
	   boolean isTrue = false;
	   // 최규성 추가
	   // #2 SPM에 대한 내용 추가.
	   // #2 HFL에 대한 내용 추가.2010-02-05
	   if(
		  YmCommonConst.NEW_SCH_WORK_KIND_CKTO.equals(sSchCode)|| // SPM TAKE OUT	 
		  YmCommonConst.NEW_SCH_WORK_KIND_CKLO.equals(sSchCode)|| // SPM 추출
		  YmCommonConst.NEW_SCH_WORK_KIND_EQTO.equals(sSchCode)|| // EQL TAKE OUT	 
		  YmCommonConst.NEW_SCH_WORK_KIND_EQLO.equals(sSchCode)|| // EQL 추출
		  YmCommonConst.NEW_SCH_WORK_KIND_CFTO.equals(sSchCode)|| // HFL TAKE OUT	
		  YmCommonConst.NEW_SCH_WORK_KIND_CFLO.equals(sSchCode)|| // HFL 추출
		  YmCommonConst.NEW_SCH_WORK_KIND_SSLO.equals(sSchCode)|| // SCARFING 추출
		  YmCommonConst.NEW_SCH_WORK_KIND_SSTO.equals(sSchCode)   // SCARFING Take
		  || YmCommonConst.NEW_SCH_WORK_KIND_CNTO.equals(sSchCode)	// #2 SPM Take Out
		  || YmCommonConst.NEW_SCH_WORK_KIND_CNLO.equals(sSchCode)	// #2 SPM 추출
		  || YmCommonConst.NEW_SCH_WORK_KIND_CHLO.equals(sSchCode)	// #2 HFL 추출
		  
		 ){
	   		isTrue = true; 	
	   	} 
		return isTrue;
	}
	
	/**
     * YJK
	 * SPM, HFL LINE OFF 작업인지를 판단한다.
     *
     * 공정구분	CHAR(1)  H : Hot Filnal, S :  SkinPass	, Q : EQL
     * 처리구분 CHAR(1)  1:보급,2:보급취소,3:추출,4:Take-Out,5:Take-In 
     *
     * @param  String
     * @return boolean
     * @throws 
     */	
	public static String getSPM_HFL_LineOffWork(String sSchCode)
	{
		String sVal = "";
		// CGS 추가
		// #2 SPM에 대한 내용 추가	
    	if(YmCommonConst.NEW_SCH_WORK_KIND_CKLI.equals(sSchCode)  ){ // SPM 보급
			sVal = "S1";
		}else if(YmCommonConst.NEW_SCH_WORK_KIND_CKLO.equals(sSchCode) ){ // SPM 추출
			sVal = "S3";
		}else if(YmCommonConst.NEW_SCH_WORK_KIND_CKTO.equals(sSchCode)  ){ // SPM TAKE OUT	 
			sVal = "S4";
		}else if(YmCommonConst.NEW_SCH_WORK_KIND_CKTI.equals(sSchCode) ){ // SPM Take In
	   		sVal = "S5";
	   	}else if(YmCommonConst.NEW_SCH_WORK_KIND_CFLI.equals(sSchCode)){ // HFL 보급
	   		sVal = "H1";
	   	}else if(YmCommonConst.NEW_SCH_WORK_KIND_CFLO.equals(sSchCode)){ // HFL 추출
			sVal = "H3";
		}else if(YmCommonConst.NEW_SCH_WORK_KIND_CFTO.equals(sSchCode)){ // HFL TAKE OUT	
			sVal = "H4";
		}else if(YmCommonConst.NEW_SCH_WORK_KIND_CFTI.equals(sSchCode)){ // HFL Take In
			sVal = "H5";
		}else if(YmCommonConst.NEW_SCH_WORK_KIND_CHLI.equals(sSchCode)) {	// #2 SPM  HFL보급
			sVal = "F1";
		}else if(YmCommonConst.NEW_SCH_WORK_KIND_CHLO.equals(sSchCode)) {	// #2 SPM HFL 추출
			sVal = "F3";
		}else if(YmCommonConst.NEW_SCH_WORK_KIND_CNLI.equals(sSchCode)) {	// #2 SPM Take Out
			sVal = "N1";
		}else if(YmCommonConst.NEW_SCH_WORK_KIND_CNLO.equals(sSchCode)) {	// #2 SPM Take In
			sVal = "N2";
		}else if(YmCommonConst.NEW_SCH_WORK_KIND_CNTO.equals(sSchCode)) {	// #2 SPM Take Out
			sVal = "N4";
		}else if(YmCommonConst.NEW_SCH_WORK_KIND_CNTI.equals(sSchCode)) {	// #2 SPM Take In
			sVal = "N5";
		}
		
		return sVal;
	}
	
	
	/**
     * YJK
	 * 연속작업대상 작업인지를 판단한다.
     *
     * @param  String
     * @return boolean
     * @throws 
     */	
	public static boolean isContinueWork(String sSchCode)
	{
	   boolean isTrue = false;
	   	
	   if(
	   	  YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchCode)|| // COIL 제품 이송상차
	   	  YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchCode)|| // COIL 제품 이송상차
	   	  YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchCode)|| // COIL 제품 이송상차
		  YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode)|| // COIL 제품 이송하차	 	
		  YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode)|| // COIL 제품 이송하차	 	
		  YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSchCode)|| // COIL 제품 이송하차	 	
	      YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSchCode)|| // COIL 소재 이송상차
	      YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSchCode)|| // COIL 소재 이송상차
	      YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSchCode)|| // COIL 소재 이송상차
		  YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchCode)|| // COIL 소재 이송하차	 
		  YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(sSchCode)|| // COIL 소재 이송하차	 
		  YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(sSchCode)|| // COIL 소재 이송하차	 
		  YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(sSchCode)|| // COIL 제품 출하상차
		  YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(sSchCode)|| // COIL 제품 출하상차
		  YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(sSchCode)|| // COIL 제품 출하상차
		  
		  YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(sSchCode)|| // COIL 제품 출하상차
		  YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(sSchCode)|| // COIL 제품 출하상차
		  YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(sSchCode)|| // COIL 제품 출하상차
		  
		  YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(sSchCode)|| // COIL 제품 출하상차
		  YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(sSchCode)|| // COIL 제품 출하상차
		  YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(sSchCode)|| // COIL 제품 출하상차
		  //YmCommonConst.NEW_SCH_WORK_KIND_CTFL.equals(sSchCode)|| // COIL 대차출하상차	 
		  YmCommonConst.NEW_SCH_WORK_KIND_CTFU.equals(sSchCode)|| // COIL 대차출하하차	 
		  //YmCommonConst.NEW_SCH_WORK_KIND_CTML.equals(sSchCode)|| // COIL 동간이적상차	 
		  //YmCommonConst.NEW_SCH_WORK_KIND_CTM2.equals(sSchCode)|| // COIL 동간이적상차	 
		  //YmCommonConst.NEW_SCH_WORK_KIND_CTM3.equals(sSchCode)|| // COIL 동간이적상차	 
		  YmCommonConst.NEW_SCH_WORK_KIND_CTSL.equals(sSchCode)|| // COIL 동간보급상차
		  YmCommonConst.NEW_SCH_WORK_KIND_CTS2.equals(sSchCode)|| // COIL 동간보급상차
		  YmCommonConst.NEW_SCH_WORK_KIND_CTS3.equals(sSchCode)|| // COIL 동간보급상차
		  //YmCommonConst.NEW_SCH_WORK_KIND_CTMU.equals(sSchCode)|| // COIL 대차하차
		  //YmCommonConst.NEW_SCH_WORK_KIND_CTM4.equals(sSchCode)|| // COIL 대차하차
		  YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(sSchCode)|| // SLAB 이송상차
		  YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(sSchCode)|| // SLAB 이송하차
		  YmCommonConst.NEW_SCH_WORK_KIND_STSL.equals(sSchCode)|| // SLAB 동간보급상차
		  YmCommonConst.NEW_SCH_WORK_KIND_STML.equals(sSchCode)||// SLAB 동간이적상차
		  YmCommonConst.NEW_SCH_WORK_KIND_STM2.equals(sSchCode)   // SLAB 동간이적상차
		  //YmCommonConst.NEW_SCH_WORK_KIND_STMU.equals(sSchCode)   // SLAB 대차하차(1)
		  //YmCommonConst.NEW_SCH_WORK_KIND_STM4.equals(sSchCode)   // SLAB 대차하차(2)
		 ){
	   		isTrue = true; 	
	   	} 
		return isTrue;
	}
	
	/**
     * YJK
	 * 동내,동간이적 상,하차 작업인지를 판단한다.
     *
     * @param  String
     * @return boolean
     * @throws 
     */	
	public static boolean isDongCrossWork(String sSchCode)
	{
	   boolean isTrue = false;
	   	
	   // CGS 추가 
	   // 신규대차에 대한 스케줄 코드 추가 
	   if(
	      YmCommonConst.NEW_SCH_WORK_KIND_CYMM.equals(sSchCode)|| // COIL 동내이적
	      YmCommonConst.NEW_SCH_WORK_KIND_CYM2.equals(sSchCode)|| // COIL 동내이적
	      YmCommonConst.NEW_SCH_WORK_KIND_CYM3.equals(sSchCode)|| // COIL 동내이적
		  YmCommonConst.NEW_SCH_WORK_KIND_CTML.equals(sSchCode)|| // COIL 동간이적상차
		  YmCommonConst.NEW_SCH_WORK_KIND_CTM2.equals(sSchCode)|| // COIL 동간이적상차	 
		  YmCommonConst.NEW_SCH_WORK_KIND_CTM3.equals(sSchCode)|| // COIL 동간이적상차	 
		  YmCommonConst.NEW_SCH_WORK_KIND_CTMU.equals(sSchCode)|| // COIL 대차하차
		  YmCommonConst.NEW_SCH_WORK_KIND_CTM4.equals(sSchCode)|| // COIL 대차하차
		  YmCommonConst.NEW_SCH_WORK_KIND_CCMU.equals(sSchCode)|| // COIL CTS 하차
		  YmCommonConst.NEW_SCH_WORK_KIND_CCMR.equals(sSchCode)|| // COIL CTS 하차(2)
		  YmCommonConst.NEW_SCH_WORK_KIND_SYMM.equals(sSchCode)|| // SLAB 동내이적
		  YmCommonConst.NEW_SCH_WORK_KIND_STML.equals(sSchCode)|| // SLAB 동간이적상차
		  YmCommonConst.NEW_SCH_WORK_KIND_STM2.equals(sSchCode)|| // SLAB 동간이적상차
		  YmCommonConst.NEW_SCH_WORK_KIND_STMU.equals(sSchCode)|| // SLAB 대차하차(1)
		  YmCommonConst.NEW_SCH_WORK_KIND_STM4.equals(sSchCode)    // SLAB 대차하차(2)
		  ||YmCommonConst.NEW_SCH_WORK_KIND_CTM5.equals(sSchCode) // COIL 동간이적상차(#1)
		  ||YmCommonConst.NEW_SCH_WORK_KIND_CTM6.equals(sSchCode) // COIL 동간이적상차(#2)
		  ||YmCommonConst.NEW_SCH_WORK_KIND_CTM7.equals(sSchCode) // COIL 동간이적상차(#3)
		  ||YmCommonConst.NEW_SCH_WORK_KIND_CTM8.equals(sSchCode) // COIL 대차하차(#1)
		  ||YmCommonConst.NEW_SCH_WORK_KIND_CTM9.equals(sSchCode) // COIL 대차하차(#2)
		  ||YmCommonConst.NEW_SCH_WORK_KIND_CTMX.equals(sSchCode) // COIL 대차하차(#3)
		 ){
	   		isTrue = true; 	
	   	} 
		return isTrue;
	}
	
	/**
     * YJK
	 * 적치열정보가 설비정보인지를 체크한다.
     *
     * @param  String
     * @return boolean
     * @throws 
     */	
	public static boolean isEquipLoc(String sCol)
	{
	   boolean isTrue = false;
	   	
	   if(
		  YmCommonConst.STACK_COL_USAGE_CD_BK.equals(sCol)||	// 보온카바위치
		  YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sCol)||	// 대차정지위치
		  YmCommonConst.STACK_COL_USAGE_CD_TX.equals(sCol)||	// 차량정지위치
		  YmCommonConst.STACK_COL_USAGE_CD_FS.equals(sCol)||	// CTS FROM SADDLE
		  YmCommonConst.STACK_COL_USAGE_CD_TS.equals(sCol)||	// CTS TO SADDLE
		  YmCommonConst.STACK_COL_USAGE_CD_CT.equals(sCol)||	// SLAB CTC
		  YmCommonConst.STACK_COL_USAGE_CD_HD.equals(sCol)||	// SLAB Holding Bed
		  YmCommonConst.STACK_COL_USAGE_CD_RT.equals(sCol)||	// SLAB Roller Table
		  YmCommonConst.STACK_COL_USAGE_CD_WB.equals(sCol)||	// SLAB Walking Beam
		  YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sCol)||	// SLAB Scafing 입측
		  YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sCol)||	// SLAB Scafing 출측
		  YmCommonConst.STACK_COL_USAGE_CD_CC.equals(sCol)||	// COIL 분기콘베이어
		  YmCommonConst.STACK_COL_USAGE_CD_CE.equals(sCol)||	// COIL 확장콘베이어
		  YmCommonConst.STACK_COL_USAGE_CD_CW.equals(sCol)||	// COIL 수냉탱크
		  YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sCol)||	// COIL HFL보급위치
		  YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sCol)||	// COIL HFLTAKEIN위치
		  YmCommonConst.STACK_COL_USAGE_CD_FD.equals(sCol)||	// COIL HFL추출위치
		  YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sCol)||	// COIL SPM보급위치
		  YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sCol)||	// COIL SPMTAKEIN위치
		  YmCommonConst.STACK_COL_USAGE_CD_KD.equals(sCol)		// COIL SPM추출위치
		 ){
	   		isTrue = true; 	
	   	} 
		return isTrue;
	}
	
    /**
     * 작업근조를 가져온다.
     */
    public static String getWorkGroup() {
        CommonUtil comUtil = new CommonUtil();
		int[] date 		= getIntYMDHMS();
        String result 	= comUtil.getTeam(date[0],date[1],date[2],date[3]);
        
        int workGroup = 0;
		if(date[3] >= 7 && date[3] <= 15)  {
		    workGroup = 1;
		}else if(date[3] >= 16 && date[3] <= 23)  {
		    workGroup = 2;
		}else {
		    workGroup = 3;
		}		
		return (""+ workGroup) + result;
    }
    
    /**
     * 작업근조를 가져온다.
     */
    public static String getWorkDuty() {
        
        int[] date 		= getIntYMDHMS();
        
        String workGroup = "0";
		if(date[3] >= 7 && date[3] <= 15)  {
		    workGroup = "1";
		}else if(date[3] >= 16 && date[3] <= 23)  {
		    workGroup = "2";
		}else {
		    workGroup = "3";
		}		
		return workGroup;
    }
    
    /**
     * 작업근조를 가져온다.
     */
    public static String getWorkParty() {
        
        CommonUtil comUtil = new CommonUtil();
		int[] date 		= getIntYMDHMS();
	    String steam ="";	
	    steam = comUtil.getTeam(date[0],date[1],date[2],date[3]);
		
	    if(steam.equals("")){
	    	steam ="E";
	    }
        return steam ;
        
    }
    
    
    public static String getDeci(String sName, String sFormat) {  // 숫자를 sFormat형식으로 변환해주는 메소드
    	String sReturn = "";
        try{
    		if(sName != null || !sName.equals("")){
    			DecimalFormat oReturnFormat = new DecimalFormat(sFormat);
    			double nResult = Double.parseDouble(sName);
    			sReturn = oReturnFormat.format(nResult);
    		}else{
    			sReturn = sName;    			
    		}
        }catch(Exception e){
    	}

        return sReturn;
    }
    
    /**
     * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
     * 1.TC_CD	: CN1PB11.
     * 2.I/F ID	: YM-BIF-020.
     * 		전문코드		TC					CHAR	7		
     * 		발생일자		Date				CHAR	10		YYYY-MM-DD
     * 		발생시간		Time				CHAR	8		HH-MM-SS
     * 		전문구분		Form				CHAR	1		I  : Initialize, U : Update D : Delete  R : Re-request
     * 		전문길이		Message_Length		CHAR	4		
     * 		송수신구분		SendReq				CHAR	1		R:요구, A:응답
     * 		SEQ NO			SeqNo				CHAR	3		001 ~ 999, END
     * 		SKID ADDRESS	Skid_Address		CHAR	10		YARD(1) + 동(1) + SPAN(2) + 열(2) + 번지(2) + 단(2)
     * 		사용유무		Use_Check			CHAR	1		SKID 사용유무-->0, 1
     * 		COIL NO			Coil_No				CHAR	10		SPACE : 코일무
     * 		군정보			Group_Info			CHAR	1		
     * 		제작번호/행번	ProductNo			CHAR	13		
     * 		두께			Thick				CHAR	7		㎜	소수점3자리 (###.###)
     * 		폭				Width				CHAR	6		㎜	소수점1자리 (####.#)
     * 		길이			Length				CHAR	6		㎜	
     * 		외경			Outdia				CHAR	5		㎜	
     * 		중량			Weight				CHAR	5		Kg	X 물리위치	X_Physical_Address	CHAR	6		
     * 		Y 물리위치		Y_Physical_Address	CHAR	6		
     * 		X 허용오차(+)	X_Plus_Range		CHAR	4		
     * 		X 허용오차(-)	X_Minus_Range		CHAR	4		
     * 		Y 허용오차(+)	Y_Plus_Range		CHAR	4		
     * 		Y 허용오차(-)	Y_Minus_Range		CHAR	4		
	 * 
     * @param schInfo : SCHEDULE INFO                             
     *
     * @return
     * @throws 
     */	 
    public static String setBCoilMapMsgInfo(String sPutLoc){
		
		StringBuffer sMsg = new StringBuffer();
		
		String sTC					="";
		String sDate				="";
		String sTime				="";
		String sForm				="";
		String sMessage_Length		="";
		String sSendReq				="";
		String sSeqNo				="";
		String sSkid_Address		="";
		String sUse_Check			="";
		String sCoil_No				="";
		String sGroup_Info			="";
		String sProductNo			="";
		String sThick				="";
		String sWidth				="";
		String sLength				="";
		String sOutdia				="";
		String sWeight				="";
		String sY_Physical_Address	="";
		String sX_Plus_Range		="";
		String sX_Minus_Range		="";
		String sY_Plus_Range		="";
		String sY_Minus_Range		="";
		
		int iTC					=  	7;	
		int iDate				=  10;	
		int iTime				=	8;	
		int iForm				=	1;	
		int iMessage_Length		=	4;	
		int iSendReq			=	1;	
		int iSeqNo				=	3;	
		int iSkid_Address		=  10;	
		int iUse_Check			=	1;	
		int iCoil_No			=  10;	
		int iGroup_Info			=	1;	
		int iProductNo			=  13;	
		int iThick				=	7;	
		int iWidth				=	6;	
		int iLength				=	6;	
		int iOutdia				=	5;	
		int iWeight				=	5;	
		int iY_Physical_Address	=	6;	
		int iX_Plus_Range		=	4;	
		int iX_Minus_Range		=	4;	
		int iY_Plus_Range		=	4;	
		int iY_Minus_Range		=	4;	
		int iTotalLength		=  90;
								   
		try{
			
			sTC					= YmCommonConst.TC_CN1PB11;
			sDate				= getCurDate("yyyy-MM-dd");
			sTime				= getCurDate("HH-mm-ss");
			sForm				= "I";
			sMessage_Length		= iTotalLength+"";
			sSendReq			= "R";
			sSkid_Address		= sPutLoc;	
						
			sMsg.append(FillToString(sTC					, iTC					));	        
			sMsg.append(FillToString(sDate					, iDate					));	
			sMsg.append(FillToString(sTime					, iTime					));	
			sMsg.append(FillToString(sForm					, iForm					));	
			sMsg.append(FillToString(sMessage_Length		, iMessage_Length		));	
			sMsg.append(FillToString(sSendReq				, iSendReq				));	
			sMsg.append(FillToString(sSeqNo				, iSeqNo				));	
			sMsg.append(FillToString(sSkid_Address			, iSkid_Address			));	
			sMsg.append(FillToString(sUse_Check			, iUse_Check			));	
			sMsg.append(FillToString(sCoil_No				, iCoil_No				));	
			sMsg.append(FillToString(sGroup_Info			, iGroup_Info			));	
			sMsg.append(FillToString(sProductNo			, iProductNo			));	
			sMsg.append(FillToString(sThick				, iThick				));	
			sMsg.append(FillToString(sWidth				, iWidth				));	
			sMsg.append(FillToString(sLength				, iLength				));	
			sMsg.append(FillToString(sOutdia				, iOutdia				));	
			sMsg.append(FillToString(sWeight				, iWeight				));	
			sMsg.append(FillToString(sY_Physical_Address	, iY_Physical_Address	));	
			sMsg.append(FillToString(sX_Plus_Range			, iX_Plus_Range			));	
			sMsg.append(FillToString(sX_Minus_Range		, iX_Minus_Range		));	
			sMsg.append(FillToString(sY_Plus_Range			, iY_Plus_Range			));	
			sMsg.append(FillToString(sY_Minus_Range		, iY_Minus_Range		));	
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}
	/**
     * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
     * 1.TC_CD	: CM1PB10.
     * 2.I/F ID	: YM-BIF-020.
     * 		전문코드		TC					CHAR	7		
     * 		발생일자		Date				CHAR	10		YYYY-MM-DD
     * 		발생시간		Time				CHAR	8		HH-MM-SS
     * 		전문구분		Form				CHAR	1		I  : Initialize, U : Update D : Delete  R : Re-request
     * 		전문길이		Message_Length		CHAR	4		
     * 		송수신구분		SendReq				CHAR	1		R:요구, A:응답
     * 		BED ADDRESS		BedAddress			CHAR	8		야드(1)+동(1)+Span(2)+ Row(2)+BED(2)
     * 		사용유무		UseCheck			CHAR	1		BED 사용유무
     * 		적치가능매수	StackUseCount		CHAR	2		BED 적치 가능 매수
     * 		적치매수		StackCount			CHAR	2		현재 적치 매수
     * 		적치 SEQ		StackSeq			CHAR	2		SLAB 적치 단
     * 		SLAB NO			SlabNo				CHAR	11		SPACE : 적치 무
     * 		제작번호/행번	ProductNo			CHAR	13		
     * 		두께			Thck				CHAR	7		㎜	소수점3자리 (###.###)
     * 		폭				Width				CHAR	6		㎜	소수점1자리 (####.#)
     * 		중량			Weight				CHAR	5		kg	
     * 		길이			Length				CHAR	6		
     * 		X 물리위치		X_Physical_Address	CHAR	6		
     * 		Y 물리위치		Y_Physical_Address	CHAR	6		
     * 		X 허용오차(+)	X_Plus_Range		CHAR	4		
     * 		X 허용오차(-)	X_Minus_Range		CHAR	4		
     * 		Y 허용오차(+)	Y_Plus_Range		CHAR	4		
     * 		Y 허용오차(-)	Y_Minus_Range		CHAR	4				
	 * 
     * @param schInfo : SCHEDULE INFO                             
     *
     * @return
     * @throws 
     */	 
	public static String setBSlabMapMsgInfo(String sPutLoc){
		
		StringBuffer sMsg = new StringBuffer();
		
		String sTC					= "";
		String sDate				= "";
		String sTime				= "";
		String sForm				= "";
		String sMessage_Length		= "";
		String sSendReq				= "";
		String sBedAddress			= "";
		String sUseCheck			= "";
		String sStackUseCount		= "";
		String sStackCount			= "";
		String sStackSeq			= "";
		String sSlabNo				= "";
		String sProductNo			= "";
		String sThck				= "";
		String sWidth				= "";
		String sWeight				= "";
		String sLength				= "";
		String sX_Physical_Address	= "";
		String sY_Physical_Address	= "";
		String sX_Plus_Range		= "";
		String sX_Minus_Range		= "";
		String sY_Plus_Range		= "";
		String sY_Minus_Range		= "";

		int iTC					=  	7;	
		int iDate				=  10;	
		int iTime				=	8;	
		int iForm				=	1;	
		int iMessage_Length		=	4;	
		int iSendReq			=	1;	
		int iBedAddress			=	8;	
		int iUseCheck			=	1;	
		int iStackUseCount		=	2;	
		int iStackCount			=	2;	
		int iStackSeq			=	2;	
		int iSlabNo				=  11;	
		int iProductNo			=  13;	
		int iThck				=	7;	
		int iWidth				=	6;	
		int iWeight				=	5;	
		int iLength				=	6;	
		int iX_Physical_Address	=	6;	
		int iY_Physical_Address	=	6;	
		int iX_Plus_Range		=	4;	
		int iX_Minus_Range		=	4;	
		int iY_Plus_Range		=	4;	
		int iY_Minus_Range		=	4;	
		int iTotalLength		=  92;
						   
		try{
			//mch A열연 SLAB야드
			if(YmCommonConst.YD_GP_0.equals(sPutLoc.substring(0, 1))
				&& YmCommonConst.BAY_GP_A.equals(sPutLoc.substring(1, 2))){
				sTC			= YmCommonConst.TC_HM1PB09;	
			}else if(YmCommonConst.YD_GP_0.equals(sPutLoc.substring(0, 1))
					&& YmCommonConst.BAY_GP_B.equals(sPutLoc.substring(1, 2))){
				sTC			= YmCommonConst.TC_HM1PB59;	
			}else{
				sTC			= YmCommonConst.TC_CM1PB10;	
			}
			
			sDate				= getCurDate("yyyy-MM-dd");
			sTime				= getCurDate("HH-mm-ss");
			sForm				= "I";
			sMessage_Length		= iTotalLength+"";
			sSendReq			= "R";
			sBedAddress			= sPutLoc.substring(0, 8);
			
			sMsg.append(FillToNumber(sTC					, iTC					));	        
			sMsg.append(FillToNumber(sDate					, iDate					));																												
			sMsg.append(FillToNumber(sTime					, iTime					));																												
			sMsg.append(FillToNumber(sForm					, iForm					));																												
			sMsg.append(FillToNumber(sMessage_Length		, iMessage_Length		));																												
			sMsg.append(FillToNumber(sSendReq				, iSendReq				));																												
			sMsg.append(FillToNumber(sBedAddress			, iBedAddress			));																												
			sMsg.append(FillToNumber(sUseCheck				, iUseCheck				));																												
			sMsg.append(FillToNumber(sStackUseCount		, iStackUseCount		));																												
			sMsg.append(FillToNumber(sStackCount			, iStackCount			));																												
			sMsg.append(FillToNumber(sStackSeq				, iStackSeq				));																												
			sMsg.append(FillToNumber(sSlabNo				, iSlabNo				));																												
			sMsg.append(FillToNumber(sProductNo			, iProductNo			));																												
			sMsg.append(FillToNumber(sThck					, iThck					));																												
			sMsg.append(FillToNumber(sWidth				, iWidth				));																												
			sMsg.append(FillToNumber(sWeight				, iWeight				));																												
			sMsg.append(FillToNumber(sLength				, iLength				));																												
			sMsg.append(FillToNumber(sX_Physical_Address	, iX_Physical_Address	));																												
			sMsg.append(FillToNumber(sY_Physical_Address	, iY_Physical_Address	));																												
			sMsg.append(FillToNumber(sX_Plus_Range			, iX_Plus_Range			));																												
			sMsg.append(FillToNumber(sX_Minus_Range		, iX_Minus_Range		));																												
			sMsg.append(FillToNumber(sY_Plus_Range			, iY_Plus_Range			));																												
			sMsg.append(FillToNumber(sY_Minus_Range		, iY_Minus_Range		));																												
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}

	/**
     * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
     * 1.TC_CD	: HM1PB09.
     * 2.I/F ID	: YM-BIF-031.
     * 		전문코드			TC					CHAR	7		
     * 		발생일자			Date				CHAR	10		YYYY-MM-DD
     * 		발생시간			Time				CHAR	8		HH-MM-SS
     * 		전문구분			Form				CHAR	1		I  : Initialize, U : Update D : Delete  R : Re-request
     * 		전문길이			Message_Length		CHAR	4		
     * 		송수신구분		SendReq				CHAR	1		R:요구, A:응답
     * 		BED ADDRESS		BedAddress			CHAR	8		야드(1)+동(1)+Span(2)+ Row(2)+BED(2)
     * 		사용유무			UseCheck			CHAR	1		BED 사용유무
     * 		적치가능매수		StackUseCount		CHAR	2		BED 적치 가능 매수
     * 		적치매수			StackCount			CHAR	2		현재 적치 매수
     * 		적치 SEQ			StackSeq			CHAR	2		SLAB 적치 단
     * 		SLAB NO			SlabNo				CHAR	11		SPACE : 적치 무
     * 		제작번호/행번		ProductNo			CHAR	13		
     * 		두께				Thck				CHAR	7		㎜	소수점3자리 (###.###)
     * 		폭				Width				CHAR	6		㎜	소수점1자리 (####.#)
     * 		중량				Weight				CHAR	5		kg	
     * 		길이				Length				CHAR	6		
     * 		X 물리위치		X_Physical_Address	CHAR	6		
     * 		Y 물리위치		Y_Physical_Address	CHAR	6		
     * 		X 허용오차(+)		X_Plus_Range		CHAR	4		
     * 		X 허용오차(-)		X_Minus_Range		CHAR	4		
     * 		Y 허용오차(+)		Y_Plus_Range		CHAR	4		
     * 		Y 허용오차(-)		Y_Minus_Range		CHAR	4				
	 * 
     * @param schInfo : SCHEDULE INFO                             
     *
     * @return
     * @throws 
     */	 
	public static String setASlabMapMsgInfo(String sPutLoc){
		
		StringBuffer sMsg = new StringBuffer();
		
		String sTC					= "";
		String sDate				= "";
		String sTime				= "";
		String sForm				= "";
		String sMessage_Length		= "";
		String sSendReq				= "";
		String sBedAddress			= "";
		String sUseCheck			= "";
		String sStackUseCount		= "";
		String sStackCount			= "";
		String sStackSeq			= "";
		String sSlabNo				= "";
		String sProductNo			= "";
		String sThck				= "";
		String sWidth				= "";
		String sWeight				= "";
		String sLength				= "";
		String sX_Physical_Address	= "";
		String sY_Physical_Address	= "";
		String sX_Plus_Range		= "";
		String sX_Minus_Range		= "";
		String sY_Plus_Range		= "";
		String sY_Minus_Range		= "";

		int iTC					=  	7;	
		int iDate				=  10;	
		int iTime				=	8;	
		int iForm				=	1;	
		int iMessage_Length		=	4;	
		int iSendReq			=	1;	
		int iBedAddress			=	8;	
		int iUseCheck			=	1;	
		int iStackUseCount		=	2;	
		int iStackCount			=	2;	
		int iStackSeq			=	2;	
		int iSlabNo				=  11;	
		int iProductNo			=  13;	
		int iThck				=	7;	
		int iWidth				=	6;	
		int iWeight				=	5;	
		int iLength				=	6;	
		int iX_Physical_Address	=	6;	
		int iY_Physical_Address	=	6;	
		int iX_Plus_Range		=	4;	
		int iX_Minus_Range		=	4;	
		int iY_Plus_Range		=	4;	
		int iY_Minus_Range		=	4;	
		int iTotalLength		=  92;
						   
		try{
			
			
			if("A".equals(sPutLoc.substring(1, 2))){
				sTC					= YmCommonConst.TC_HM1PB09;	
			}else{
				sTC					= YmCommonConst.TC_HM1PB59;
			}
			sDate				= getCurDate("yyyy-MM-dd");
			sTime				= getCurDate("HH-mm-ss");
			sForm				= "I";
			sMessage_Length		= iTotalLength+"";
			sSendReq			= "R";
			sBedAddress			= sPutLoc.substring(0, 8);
			
			sMsg.append(FillToNumber(sTC					, iTC					));	        
			sMsg.append(FillToNumber(sDate					, iDate					));																												
			sMsg.append(FillToNumber(sTime					, iTime					));																												
			sMsg.append(FillToNumber(sForm					, iForm					));																												
			sMsg.append(FillToNumber(sMessage_Length		, iMessage_Length		));																												
			sMsg.append(FillToNumber(sSendReq				, iSendReq				));																												
			sMsg.append(FillToNumber(sBedAddress			, iBedAddress			));																												
			sMsg.append(FillToNumber(sUseCheck				, iUseCheck				));																												
			sMsg.append(FillToNumber(sStackUseCount		, iStackUseCount		));																												
			sMsg.append(FillToNumber(sStackCount			, iStackCount			));																												
			sMsg.append(FillToNumber(sStackSeq				, iStackSeq				));																												
			sMsg.append(FillToNumber(sSlabNo				, iSlabNo				));																												
			sMsg.append(FillToNumber(sProductNo			, iProductNo			));																												
			sMsg.append(FillToNumber(sThck					, iThck					));																												
			sMsg.append(FillToNumber(sWidth				, iWidth				));																												
			sMsg.append(FillToNumber(sWeight				, iWeight				));																												
			sMsg.append(FillToNumber(sLength				, iLength				));																												
			sMsg.append(FillToNumber(sX_Physical_Address	, iX_Physical_Address	));																												
			sMsg.append(FillToNumber(sY_Physical_Address	, iY_Physical_Address	));																												
			sMsg.append(FillToNumber(sX_Plus_Range			, iX_Plus_Range			));																												
			sMsg.append(FillToNumber(sX_Minus_Range		, iX_Minus_Range		));																												
			sMsg.append(FillToNumber(sY_Plus_Range			, iY_Plus_Range			));																												
			sMsg.append(FillToNumber(sY_Minus_Range		, iY_Minus_Range		));																												
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}
	
	/**
     * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
     * 1.TC_CD	: HC3PB52.
     * 2.I/F ID	: YM-BIF-031.
     * 		전문코드			TC					CHAR	7		
     * 		발생일자			Date				CHAR	10		YYYY-MM-DD
     * 		발생시간			Time				CHAR	8		HH-MM-SS
     * 		전문구분			Form				CHAR	1		I  : Initialize, U : Update D : Delete  R : Re-request
     * 		전문길이			Message_Length		CHAR	4		
     * 		송수신구분		SendReq				CHAR	1		R:요구, A:응답
     * 		BED ADDRESS		BedAddress			CHAR	8		야드(1)+동(1)+Span(2)+ Row(2)+BED(2)
     * 		사용유무			UseCheck			CHAR	1		BED 사용유무
     * 		적치가능매수		StackUseCount		CHAR	2		BED 적치 가능 매수
     * 		적치매수			StackCount			CHAR	2		현재 적치 매수
     * 		적치 SEQ			StackSeq			CHAR	2		SLAB 적치 단
     * 		SLAB NO			SlabNo				CHAR	11		SPACE : 적치 무
     * 		제작번호/행번		ProductNo			CHAR	13		
     * 		두께				Thck				CHAR	7		㎜	소수점3자리 (###.###)
     * 		폭				Width				CHAR	6		㎜	소수점1자리 (####.#)
     * 		중량				Weight				CHAR	5		kg	
     * 		길이				Length				CHAR	6		
     * 		X 물리위치		X_Physical_Address	CHAR	6		
     * 		Y 물리위치		Y_Physical_Address	CHAR	6		
     * 		X 허용오차(+)		X_Plus_Range		CHAR	4		
     * 		X 허용오차(-)		X_Minus_Range		CHAR	4		
     * 		Y 허용오차(+)		Y_Plus_Range		CHAR	4		
     * 		Y 허용오차(-)		Y_Minus_Range		CHAR	4				
	 * 
     * @param schInfo : SCHEDULE INFO                             
     *
     * @return
     * @throws 
     */	 
	public static String setASlabMapMsgInfo7(String sPutLoc){
		
		StringBuffer sMsg = new StringBuffer();
		
		String sTC					= "";
		String sDate				= "";
		String sTime				= "";
		String sForm				= "";
		String sMessage_Length		= "";
		String sSendReq				= "";
		String sBedAddress			= "";
		String sUseCheck			= "";
		String sStackUseCount		= "";
		String sStackCount			= "";
		String sStackSeq			= "";
		String sSlabNo				= "";
		String sProductNo			= "";
		String sThck				= "";
		String sWidth				= "";
		String sWeight				= "";
		String sLength				= "";
		String sX_Physical_Address	= "";
		String sY_Physical_Address	= "";
		String sX_Plus_Range		= "";
		String sX_Minus_Range		= "";
		String sY_Plus_Range		= "";
		String sY_Minus_Range		= "";

		int iTC					=  	7;	
		int iDate				=  10;	
		int iTime				=	8;	
		int iForm				=	1;	
		int iMessage_Length		=	4;	
		int iSendReq			=	1;	
		int iBedAddress			=	8;	
		int iUseCheck			=	1;	
		int iStackUseCount		=	2;	
		int iStackCount			=	2;	
		int iStackSeq			=	2;	
		int iSlabNo				=  11;	
		int iProductNo			=  13;	
		int iThck				=	7;	
		int iWidth				=	6;	
		int iWeight				=	5;	
		int iLength				=	6;	
		int iX_Physical_Address	=	6;	
		int iY_Physical_Address	=	6;	
		int iX_Plus_Range		=	4;	
		int iX_Minus_Range		=	4;	
		int iY_Plus_Range		=	4;	
		int iY_Minus_Range		=	4;	
		int iTotalLength		=  92;
						   
		try{
			
			sTC					= YmCommonConst.TC_HC3PB52;
			sDate				= getCurDate("yyyy-MM-dd");
			sTime				= getCurDate("HH-mm-ss");
			sForm				= "I";
			sMessage_Length		= iTotalLength+"";
			sSendReq			= "R";
			sBedAddress			= sPutLoc.substring(0, 8);
			
			sMsg.append(FillToNumber(sTC					, iTC					));	        
			sMsg.append(FillToNumber(sDate					, iDate					));																												
			sMsg.append(FillToNumber(sTime					, iTime					));																												
			sMsg.append(FillToNumber(sForm					, iForm					));																												
			sMsg.append(FillToNumber(sMessage_Length		, iMessage_Length		));																												
			sMsg.append(FillToNumber(sSendReq				, iSendReq				));																												
			sMsg.append(FillToNumber(sBedAddress			, iBedAddress			));																												
			sMsg.append(FillToNumber(sUseCheck				, iUseCheck				));																												
			sMsg.append(FillToNumber(sStackUseCount		, iStackUseCount		));																												
			sMsg.append(FillToNumber(sStackCount			, iStackCount			));																												
			sMsg.append(FillToNumber(sStackSeq				, iStackSeq				));																												
			sMsg.append(FillToNumber(sSlabNo				, iSlabNo				));																												
			sMsg.append(FillToNumber(sProductNo			, iProductNo			));																												
			sMsg.append(FillToNumber(sThck					, iThck					));																												
			sMsg.append(FillToNumber(sWidth				, iWidth				));																												
			sMsg.append(FillToNumber(sWeight				, iWeight				));																												
			sMsg.append(FillToNumber(sLength				, iLength				));																												
			sMsg.append(FillToNumber(sX_Physical_Address	, iX_Physical_Address	));																												
			sMsg.append(FillToNumber(sY_Physical_Address	, iY_Physical_Address	));																												
			sMsg.append(FillToNumber(sX_Plus_Range			, iX_Plus_Range			));																												
			sMsg.append(FillToNumber(sX_Minus_Range		, iX_Minus_Range		));																												
			sMsg.append(FillToNumber(sY_Plus_Range			, iY_Plus_Range			));																												
			sMsg.append(FillToNumber(sY_Minus_Range		, iY_Minus_Range		));																												
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}	
	
	/**
     * 장입확정시 L-2로 슬라브정보를 송신한다.
     * @param dto	슬라브정보
     */
	public static String getSlabMsgInfo(JDTORecord slabInfo,String sFormGp) {
		
		StringBuffer sendMsg 	= new StringBuffer();
		
		try{
		    sendMsg.append(YmCommonConst.TC_CM1BP02);
	        sendMsg.append(YmCommonUtil.getStringYMD("/"));
	        sendMsg.append(YmCommonUtil.getStringHMS(":"));
	        sendMsg.append(sFormGp);
	        appendMsgNum(sendMsg, 123+"", 	4);
	        appendMsg(sendMsg, getField(slabInfo, "STL_NO"),					11);
	        appendMsg(sendMsg, "1",												1);
	        appendMsg(sendMsg, getField(slabInfo, "ORD_YEOJAE_GP"), 			1);
	        appendMsg(sendMsg, getField(slabInfo, "PRODUC_NO"),					13);
	        String t 	= YmCommonUtil.format(getField(slabInfo, "SLAB_T"), 3, 3).replace('.', ' ');
	        String w 	= YmCommonUtil.format(getField(slabInfo, "SLAB_W"), 4, 1).replace('.', ' ');
	        appendMsgNum(sendMsg, t.replaceAll(" ", ""),						7);
	        appendMsgNum(sendMsg, w.replaceAll(" ", ""),						6);
	        appendMsgNum(sendMsg, getField(slabInfo, "SLAB_LEN"), 				6);
	        appendMsgNum(sendMsg, getField(slabInfo, "SLAB_WT"),				5);
	        appendMsg(sendMsg, getField(slabInfo, "COIL_NO"), 					10);
	        appendMsg(sendMsg, getField(slabInfo, "MILL_PLAN_DDTT"),			14);
	        appendMsg(sendMsg, getField(slabInfo, "REFUR_CHG_LOT_NO"),			10);
	        appendMsgNum(sendMsg, getField(slabInfo, "LOT_IN_SLAB_PRIOR"),		4);
	        appendMsg(sendMsg, getField(slabInfo, "BUY_SLAB_NO"), 				25);
	        appendMsg(sendMsg, getField(slabInfo, "YD_CHG_NO"), 				10);
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sendMsg.toString();
    }
	
	
	
	
	/**
     * 삭제 장입순번  L-2로 슬라브정보를 송신한다.
     * @param dto	슬라브정보
     */
	public static String getSlabMsgInfoD(JDTORecord slabInfo,String sFormGp) {
		
		StringBuffer sendMsg 	= new StringBuffer();
		
		try{
		    sendMsg.append(YmCommonConst.TC_CM1BP02);
	        sendMsg.append(YmCommonUtil.getStringYMD("/"));
	        sendMsg.append(YmCommonUtil.getStringHMS(":"));
	        sendMsg.append(sFormGp);
	        appendMsgNum(sendMsg, 123+"", 	4);
	        
	        
	        appendMsg(sendMsg, "",			11);
	        appendMsg(sendMsg, "1",			1);
	        appendMsg(sendMsg, "", 			1);
	        appendMsg(sendMsg, "",			13);
	        appendMsgNum(sendMsg,"",		7);
	        appendMsgNum(sendMsg, "",		6);
	        appendMsgNum(sendMsg, "", 		6); 
	        appendMsgNum(sendMsg, "",		5);
	        appendMsg(sendMsg, "", 			10);
	        appendMsg(sendMsg, "",			14);
	        appendMsg(sendMsg, "",			10);
	        appendMsgNum(sendMsg, "",		4);
	        appendMsg(sendMsg, "", 			25);
	        appendMsg(sendMsg, "", 			10);
	        
	        
	        /*
	        appendMsg(sendMsg, getField(slabInfo, "STL_NO"),					11);
	        appendMsg(sendMsg, "1",												1);
	        appendMsg(sendMsg, getField(slabInfo, "ORD_YEOJAE_GP"), 			1);
	        appendMsg(sendMsg, getField(slabInfo, "PRODUC_NO"),					13);
	        String t 	= YmCommonUtil.format(getField(slabInfo, "SLAB_T"), 3, 3).replace('.', ' ');
	        String w 	= YmCommonUtil.format(getField(slabInfo, "SLAB_W"), 4, 1).replace('.', ' ');
	        appendMsgNum(sendMsg, t.replaceAll(" ", ""),						7);
	        appendMsgNum(sendMsg, w.replaceAll(" ", ""),						6);
	        appendMsgNum(sendMsg, getField(slabInfo, "SLAB_LEN"), 				6);
	        appendMsgNum(sendMsg, getField(slabInfo, "SLAB_WT"),				5);
	        appendMsg(sendMsg, getField(slabInfo, "COIL_NO"), 					10);
	        appendMsg(sendMsg, getField(slabInfo, "MILL_PLAN_DDTT"),			14);
	        appendMsg(sendMsg, getField(slabInfo, "REFUR_CHG_LOT_NO"),			10);
	        appendMsgNum(sendMsg, getField(slabInfo, "LOT_IN_SLAB_PRIOR"),		4);
	        appendMsg(sendMsg, getField(slabInfo, "BUY_SLAB_NO"), 				25);
	        appendMsg(sendMsg, getField(slabInfo, "YD_CHG_NO"), 				10);*/
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sendMsg.toString();
    }
	
	
	
	
	
	/**
     * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
     * @param data
     * @param name
     * @return
     */
    private static String getField(JDTORecord data, String name) {
        return StringHelper.evl(data.getFieldString(name), "").trim();
    }
    /**
     * name parameter에 대한 값을 반환한다.
     * @param data	
     * @param name  
     * @return
     */
    private static int getFieldLen(Map data, String name) {
        return StringHelper.parseInt((String)data.get(name), 0);
    }
    /**
     * 공백을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private static void appendMsg(StringBuffer buffer, String field, int cnt) {
	    try{ 	
	    	if("".equals(field)) {
	            fillSpace(buffer, cnt);
	        }else if(CommonUtil.getLength(field) > cnt) {
	        	buffer.append(CommonUtil.substr(field, 0, cnt));
	        }else if(CommonUtil.getLength(field) < cnt) {
	            buffer.append(field);
	            fillSpace(buffer, cnt - CommonUtil.getLength(field));
	        }else {
	            buffer.append(field);
	        }
	    }catch(Exception e){}
    }
    
    /**
     * 0을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private static void appendMsgNum(StringBuffer buffer, String field, int cnt) {
	    try{    
	        if("".equals(field)) {
	            fillZeroSpace(buffer, cnt);
	        }else if(CommonUtil.getLength(field) > cnt) {
	            buffer.append(CommonUtil.substr(field, 0, cnt));
	        }else if(CommonUtil.getLength(field) < cnt) {
	            fillZeroSpace(buffer, cnt - CommonUtil.getLength(field));
	            buffer.append(field);
	        }else {
	            buffer.append(field);
	        }
	    }catch(Exception e){}
    }
    /**
     * 공백을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private static void fillSpace(StringBuffer buffer, int cnt) {
        for(int i = 0; i < cnt; i++) {
            buffer.append(" ");
        }
    }
    /**
     * 0을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private static void fillZeroSpace(StringBuffer buffer, int cnt) {
        for(int i = 0; i < cnt; i++) {
            buffer.append("0");
        }
    }

    // 플렉스 관련 메소드
    // putLogMsg()
    // putLogToMonitoring()
    // pushToFlexClient()
    // 최규성 
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
	public static void putLogMsg(String szYdGp, 
			                     String desti, 
			                     String szLogMsg, 
			                     String szYdBayGp, 
			                     String  szYdEqpId, 
			                     String szYdSchCd, 
			                     String szYdEvtGp, 
			                     String szYdMsgOutpwrGrd, 
			                     String szYdPgmTp, 
			                     String szYdIfCd, 
			                     String szEJBId, 
			                     String szMsgName)  {
		YdMsgInfoMgtDao ydMsgInfoMgtDao = new YdMsgInfoMgtDao();
		
		JDTORecord inRec = null;
		String szMsg = "";
		int intRtnVal;
		String szMethodName = "putLogMsg";
		
		
		try {
			if (desti.equals("")) {
				desti = "yd_monitor3";
			}
			
			if (szYdGp.equals("")) {
				szMsg = "야드구분을 설정하지 않았습니다";
				throw new DAOException(szMethodName + szMsg);
			}
			/* B열연은 DB INSERT 부분을 사용하지 않는다. 최규성
			
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
				throw new DAOException(szMethodName + szMsg);
			}
			*/
			putLogToMonitoring(desti, szEJBId, szMsgName, szLogMsg, szYdEvtGp);
			
			
		} catch(Exception e) {
			szMsg =szMethodName +"() " + "\n\t"+szMethodName+" Exception Error : "+e.getLocalizedMessage();
			//logger.println(LogLevel.ERROR, this, szMsg);
		}
	}
	/**
	 * 오퍼레이션명 : putLogMsg - 최규성
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
	public static void putLogMsg(JDTORecord jtrData_IN){
		//logger.println(LogLevel.ERROR, this, "start - putLogMsg(JDTORecord)");
		String szMsg = "";
		int intRtnVal;
		String szMethodName = "putLogMsg(JDTORecord)";

		try {
			String desti="";           // Monitoring Channel
			String szLogMsg ="";       // Logging Message
			String szYdEvtGp ="W";     // 야드 Event 구분 (C:크레인, Q:설비, E:에러, W:경고, I:정보, Z:기타)
			String szEJBId	 ="";      // Logging 요청 Class name
			String szMsgName ="";	   // Logging 요청 Method Name
			
			desti = jtrData_IN.getFieldString("DESTI");
			szLogMsg = jtrData_IN.getFieldString("LOGMSG");
			putLogToMonitoring(desti, szEJBId, szMsgName, szLogMsg, szYdEvtGp);
			
			//logger.println(LogLevel.ERROR, this, "end - putLogMsg(JDTORecord)");
		} catch(Exception e) {
			szMsg =szMethodName +"() " + "\n\t"+szMethodName+" Exception Error : "+e.getLocalizedMessage();
			//logger.println(LogLevel.ERROR, this, szMsg);
		}
	}
	
	// 최규성 2009-12-08
	// 메시지만 인자로 받음.
	public static void putLogMsg(String sMsg){
		//logger.println(LogLevel.ERROR, this, "start - putLogMsg(JDTORecord)");
		String szMsg = "";
		int intRtnVal;
		String szMethodName = "putLogMsg(String)";

		try {
			String desti="yd_monitor3";           // Monitoring Channel
			String szLogMsg ="";       // Logging Message
			String szYdEvtGp ="W";     // 야드 Event 구분 (C:크레인, Q:설비, E:에러, W:경고, I:정보, Z:기타)
			String szEJBId	 ="";      // Logging 요청 Class name
			String szMsgName ="";	   // Logging 요청 Method Name
			
			//desti = jtrData_IN.getFieldString("DESTI");
			szLogMsg = sMsg;//jtrData_IN.getFieldString("LOGMSG");
			putLogToMonitoring(desti, szEJBId, szMsgName, szLogMsg, szYdEvtGp);
			
			//logger.println(LogLevel.ERROR, this, "end - putLogMsg(JDTORecord)");
		} catch(Exception e) {
			szMsg =szMethodName +"() " + "\n\t"+szMethodName+" Exception Error : "+e.getLocalizedMessage();
			//logger.println(LogLevel.ERROR, this, szMsg);
		}
	}	
	
	/**
	 * 오퍼레이션명 : putLogToMonitoring
	 * 
	 * @param String desti	        // Monitoring Channel
	 *        String szClassName	// Logging 요청 Class name
	 *        String szMethodName 	// Logging 요청 Method Name
	 *        String szLogMsg		// Logging Message
	 *        String szYdEvtGp		// 야드 Event 구분 (C:크레인, Q:설비, E:에러, W:경고, I:정보, Z:기타)
	 * @return
	 * @throws DAOException, JDTOException
	 */
	public static void putLogToMonitoring(String desti, 
										  String szClassName, 
										  String szMethodName, 
										  String szLogMsg, 
										  String szYdEvtGp)  {
		
		String szMsg="";
		String strCurDate = YdUtils.getCurDate("yyyy-MM-dd HH:mm:ss:SSS");
		
		szMsg =szClassName + "::" + szMethodName +"() " + "\n\t"+szLogMsg;

		HashMap hmap = new HashMap();
		
		try{
			if (szYdEvtGp.equals("C")){
				szMsg="[크레인] "+szMsg;
			}else if(szYdEvtGp.equals("Q")){
				szMsg="[설비] "+szMsg;
			}else if(szYdEvtGp.equals("E")){
				szMsg="[에러] "+szMsg;
			}else if(szYdEvtGp.equals("W")){
				szMsg="[경고] "+szMsg;
			}else if(szYdEvtGp.equals("I")){
				szMsg="[정보] "+szMsg;
			}else if(szYdEvtGp.equals("Z")){
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
			
			hmap.put("MSG_GP", "2");			
			hmap.put("YD_MSG", szMsg);
			hmap.put("YD_GP", "3");
			
			//FLAX_PUSH사용여부 체크 /////////////////////////////////////////////////
		 	JDTORecord recPara =  JDTORecordFactory.getInstance().create();
	    	JDTORecord recInTemp =  JDTORecordFactory.getInstance().create();
	    	String CHK ="N";
			JDTORecordSet outRecSet = null;
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			
	    	outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			recPara =  JDTORecordFactory.getInstance().create();
			recPara.setField("YD_GP","3");
			/*com.inisteel.cim.yd.common.util.YdUtils.chklist*/
			int intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 999);
			
			if( intRtnVal > 0 ) {
				outRecSet.first();
				recInTemp = outRecSet.getRecord();
				CHK = recInTemp.getFieldString("CHK").trim();
			}
			 
			/////////////////////////////////////////////////////////////////////////
	    	if(CHK.equals("Y")){			
			pushToFlexClient(YmCommonConst.YD_MONITORING_CHANNEL_3, hmap);
	    	}
			
		}catch (Exception e){
			
			szMsg =szClassName + "::" + szMethodName +"() " + "\n\t"+szMethodName+" Exception Error : "+e.getLocalizedMessage();
			//logger.println(LogLevel.ERROR, this, szMsg);
				
		} // end of try-catch()
		
	} // end of putLogToMonitoring();

	
	/**
	 * 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param desti
	 * @param pushData
	 */	
	public static void pushToFlexClient(String desti,Object pushData) {
		String sMethod = "pushToFlexClient(String,Object)";
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
			     <destination id="yd_monitor3">
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
			
			System.out.println("pushToFlexClient SEND COUNT>>ym>>pushToFlexClient!!");
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				System.out.println("pushToFlexClient exception!!");
				//throw new DAOException(getClass().getName() + e.getMessage());
				throw new DAOException(sMethod + e.getMessage());
		} finally {
		}	
			
	}
	
	/**
	 * 날짜 형식을 만들어낸다.
	 * @param getData   yyyymmdd
	 * @param getChar 구분자.. "."
	 * @return yyyy.mm.dd
	 * @throws NullPointerException
	 */
	public static String makeDate(String getData, String getChar) throws NullPointerException	{
		if(getData == null)
      return "";
		// 날짜처리
		if(getData.length() != 8)
      return getData;
		
		else
			return getData.substring(0, 4) + getChar + getData.substring(4, 6) + getChar + getData.substring(6, 8);
 
	}
	/**
	 * 시간형식 변경
	 * @param getData 예) hhMMss
	 * @param getChar 예)":"
	 * @return 예)24:12:59
	 * @throws NullPointerException
	 */
	public static String makeTime(String getData, String getChar) throws NullPointerException	{
		if(getData == null)
      return "";
		if(getData.length() != 6)
      return getData;
		else
      return getData.substring(0, 2) + getChar + getData.substring(2, 4) + getChar + getData.substring(4, 6);
 
	}
	
	  
	  /**
		 * 날짜형식의 문자열 S 에서 년도 I, 월 J, 일 K 에 값 음수또는 양수 를 넣어 해당 날짜를 얻어낸다.	
		 * @param getData
		 * @param i
		 * @param j
		 * @param k
		 * @return
		 */
		public  String getAddDate(String getData,  int k){
	    if(getData == null)
	      return "";
	    if(getData.trim().length() < 8)
	      return getData.trim();
	    int l = Integer.parseInt(getData.substring(0, 4));
	    int i1 = Integer.parseInt(getData.substring(4, 6));
	    int j1 = Integer.parseInt(getData.substring(6, 8));
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(l, i1 - 1, j1);
	    calendar.add(1, 0);
	    calendar.add(2, 0);
	    calendar.add(5, k);
	    String rtnResult = String.valueOf(calendar.get(1));
	    if(calendar.get(2) + 1 < 10)
	    	rtnResult = rtnResult + "0" + (calendar.get(2) + 1);
	    else
	    	rtnResult = rtnResult + (calendar.get(2) + 1);
	    if(calendar.get(5) < 10)
	    	rtnResult = rtnResult + "0" + calendar.get(5);
	    else
	    	rtnResult = rtnResult + calendar.get(5);
	    return rtnResult;
		}
		
		/**
		 * 날자형식 변경
		 * @param getData : 날자 형식(yyyymmddhhmmss)
		 * @param getChar : 분리자, 분리자를 "" 으로 하면 yyyymmddhhmmss 로 형식 변환.
		 * @return
		 * @throws NullPointerException
		 */
		public static String formatDT(String getData, String getChar) throws NullPointerException	{
			String rtnData ="";
			if(getData == null)
	      return "";
			if(getData.length() == 8){
				rtnData = getData.substring(0, 4) + getChar + getData.substring(4, 6) + getChar + getData.substring(6, 8);
	      return rtnData;
			}else if(getData.length() == 6){
				rtnData = getData.substring(0, 2) + getChar + getData.substring(2, 4) + getChar + getData.substring(4, 6);
				return rtnData;
			}else{
				rtnData = getData ;
				return rtnData;
			}
			
		}

		/**
		 * 로그인 사용자 ID
		 * @param HttpRequestWrapper
		 * @param HttpResponseWrapper
		 * @return
		 */
		public String getUserId(HttpRequestWrapper req, HttpResponseWrapper res) {
			String getUsrId = "";
			try {
				JspeedSession jsession  = new JspeedSession (req, res);
				getUsrId =  StringHelper.nvl((String) jsession.getAttribute("login_session", "L_USERID"), "") ;
				return getUsrId;
			}catch(Exception e) {
				System.out.println(e);
				getUsrId = "---";
			}
			return getUsrId;
		}
		
		  /**
		   * 여러개의 구분자 처리할때. XXX / XXX / XXX .....
		   * @param getParam
		   * @param getStr
		   * @return
		   */
		  public String genBetweenStr(List getParam, String getStr) {
		  	String setRtnStr = "";
		  	String getParamData = "";
		  	
		  	if(getParam.size() > 0  ) {
		  		for(int ii = 0; ii < getParam.size(); ii++) {
		        getParamData += StringHelper.nvl( (String)getParam.get(ii),"").trim();
		  			if(ii == 0) {
		  				setRtnStr = (String) getParam.get(ii)+" ";
		  			}else {
		  				setRtnStr += getStr+" "+(String)getParam.get(ii)+( ii==getParam.size()-1 ?"":" " );
		  			}
		  		}
		      if(getParamData.length() <= 0) return getParamData;
		  	}else {
		  		return setRtnStr;
		  	}
		  	return setRtnStr;
		  }

		  public static String replaceFloat(String getData){
			    if ( getData == null ) return "";
			    getData = replace(getData, ",", "");
			    getData = trim(getData);
			    return getData;
			  }
		  
		  
		  /**
			 * 특정 iitem String을 찾아 다른 String 으로 교체
			 * @param String getData 원래
			 * @param String getChar 
			 * @param String setChar
			 * @return String
			 */
			public static String replace(String getData, String getChar, String setChar){
				if(getData==null) return "";

				int iiTargetLen = getChar.length();

				StringBuffer rtnResult = new StringBuffer();
				int ii = 0;
				int ij = 0;

				while (ij > -1){
					ij = getData.indexOf(getChar, ii);
					if (ij > -1){
						rtnResult.append(getData.substring( ii, ij)).append(setChar);
						ii = ij + iiTargetLen;
					}
				}
				rtnResult.append(getData.substring( ii, getData.length()));

				return rtnResult.toString();
			}
			
			
			/**
			 * 좌우측 공백 제거
			 * @param str
			 * @return
			 */
			public static String trim(String str)
			{
				int iidx = 0;
				char[] val = str.toCharArray();
				int count = val.length;
				int len = count;

				while ((iidx < len) && (val[iidx] <= ' '))   iidx++;
				while ((iidx < len) && (val[len - 1] <= ' '))  len--;
				//while ((idx < len) && ((val[idx] <= ' ') || (val[idx] == '　') ) )   idx++;
				//while ((idx < len) && ((val[len - 1] <= ' ') || (val[len-1] == '　')))  len--;

				return ((iidx > 0) || (len < count)) ? str.substring(iidx, len) : str;
			}
			
			/**
			 * 지정된 크기만큼 문자열 앞부분을 분리하는 메소드
			 * @param  java.lang.String
			 * @return splitted string from the source string.
			 */
			public static String splitHead(String str, int limit)
			{
				if (str == null || limit < 4) return str;

				int len = str.length();
				int cnt=0, index=0;

				while (index < len && cnt < limit)
				{
					if (str.charAt(index++) < 256) // 1바이트 문자라면...
						cnt++;     // 길이 1 증가
					else // 2바이트 문자라면...
						cnt += 2;  // 길이 2 증가
				}

				if (index < len)
					str = str.substring(0, index) + "...";

				return str;
			}
			
			/**
			 * 분리자가 "-"일때 - 일시 형식을 yyyy-mm-dd hh:mm:ss
			 * @param  getData java.lang.String 날짜 String
			 * @param  delimiter java.lang.String 분리자
			 * @return Formatted DateTime string.
			 */
			public static String formatDateTime(String getData, String getChar){
				if ( getData == null ) return "";
				if ( getData.length() > 14 ){
					getData = replace(getData, "/", "");
					getData = replace(getData, "-", "");
					getData = replace(getData, ".", "");
					getData = replace(getData, ":", "");
					getData = trim(getData);
				}
				if ( getData.length() < 14 ) return getData;	//"yyyy/mm/dd hh:mi:ss";

				String format_date_time = getData.substring(0,4) + getChar + getData.substring(4,6) + getChar + getData.substring(6,8)
								+ " " +  getData.substring(8,10) + ":" +  getData.substring(10,12) + ":" +  getData.substring(12,14);
				return format_date_time;
			}
			
			/**
		     * JGK- 이슈번호(11725)
			 *
			 *	해당위치의 코일번호를 가져온다.
			 *
			 * @param  String
		     * @return String
		     * @throws  
		     */			
			public static String getStackColInfoCoilPk(String StackColGp,String StackBedGp ,String StackLayerGp)
			{	
				String sStock_id = "";
				
				ymCommonDAO dao 		= ymCommonDAO.getInstance();
				String 		sQueryId 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackColInfoCoilPk";
				JDTORecord 	jtR   	 	= dao.getCommonInfo(sQueryId,new Object[]{StackColGp,StackBedGp,StackLayerGp});
				 
				if(jtR != null){
					sStock_id = StringHelper.evl(jtR.getFieldString("STOCK_ID"), "");
				}
				
				return sStock_id;
			}
}
