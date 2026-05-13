/**
 * @(#)YfCommtils
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      야드관리 공통 Utils
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.yf.common;

import java.io.File;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inisteel.cim.common.exception.AppRuntimeException;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.util.CommonUtil;

import com.inisteel.cim.cm.message.MessageSenderAuto;

import com.inisteel.cim.sb.common.util.SbConstant;

import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;

import com.inisteel.cim.yf.common.YfConstant;
import com.inisteel.cim.yf.common.dao.YfCommDAO;

import xlib.cmc.GridData;
import xlib.cmc.GridHeader;
import xlib.cmc.OperateGridData;

import jspeed.base.http.HttpRequestWrapper;
import jspeed.base.http.HttpResponseWrapper;
import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.query.QueryService;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordImplMap;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;
import jspeed.base.util.StringHelper;
import jspeed.fc.session.JspeedSession;

/**
 * [A] 클래스명 : 야드관리 공통 Utils
 *
 */
public class YfCommUtils implements YfQueryIF, YfQueryIF2
{
	private static Logger logger = new Logger("yf");

	private boolean bDebugFlag=false;
	private String szSessionName =getClass().getName();
	
	public static final char STRING_TYPE			= 'S';
	public static final char DATETIME_TYPE 			= 'T';
	public static final char DOUBLE_TYPE 			= 'D';
	public static final char LONG_TYPE				= 'L';
	public static final char INTEGER_TYPE			= 'I';
	public static final char PAGE_COUNT_TYPE		= 'P';
	public static final char ROW_COUNT_TYPE			= 'R';
	
	/**
	 * 문자열이 null 일때 임의의 문자열을 반환한다.
	 * @param value
	 * @param defaultValue
	 * @return String
	 */
	public String nvl(String value, String defaultValue) 
	{
		return (value == null || "".equals(value) || "NULL".equals(value.trim().toUpperCase())) ? defaultValue : value;
	}

	public String nvl(Object o, String defaultValue) 
	{
		return (o == null) ? defaultValue : o.toString();
	}

	public String nvl(String value) 
	{
		return (value == null || "".equals(value) || "NULL".equals(value.trim().toUpperCase())) ? "" : value;
	}
	
	/**
     * 일정 길이만큼 뒤에 공백을 채운다.
     * @String in_strValue, int in_intLength 
     */ 
	public static String FillToString(String in_strValue, int in_intLength )
	{
		
		String in_strRet = "";
   		try
   		{
			if (CommonUtil.getLength(in_strValue) > in_intLength)
			{
				in_strRet = CommonUtil.substr(in_strValue, 0, in_intLength);
			}
			else
			{
				in_strRet = in_strValue + MakeSpace(in_intLength - CommonUtil.getLength(in_strValue)," ");
			}
		}
   		catch(Exception e)
   		{
   			
   		}
		
		//LogService.getInstance().getLogger("ym").println(LogLevel.DEBUG, "[FilltTOString] [입력:"+in_strValue+","+in_intLength+"] [출력:"+in_strRet+"]"); 
		return in_strRet;
    }
	
	
	/**
	 * 문자열이 null 일때 ""을 반환한다.
	 * @param value
	 * @return String
	 */
	public String trim(String value) 
	{
		return (value == null || "NULL".equals(value.trim().toUpperCase())) ? "" : value.trim();
	}

	/**
	 * Object가 null 일때 true를 반환한다.
	 * @param obj
	 * @return boolean
	 */
	public boolean isEmpty(Object obj) 
	{
		if (obj == null) 
		{
			return true;
		}
		else if (obj instanceof String) 
		{
			if ("".equals(obj)) 
			{
				return true;
			}
		}
		else if (obj instanceof JDTORecord) 
		{
			if (((JDTORecord)obj).size() <= 0) 
			{
				return true;
			}
		} 
		else if (obj instanceof JDTORecord[]) 
		{
			if (((JDTORecord[])obj).length <= 0) 
			{
				return true;
			}
		} 
		else if (obj instanceof JDTORecordSet) 
		{
			if (((JDTORecordSet)obj).size() <= 0) 
			{
				return true;
			}
		}
		else if (obj instanceof Object[]) 
		{
			if (((Object[])obj).length <= 0) 
			{
				return true;
			}
		} 
		else if (obj instanceof Object[][]) 
		{
			if (((Object[][])obj).length <= 0) 
			{
				return true;
			}
		}

		return false;
	}
	

	
	/**
	 * 페이징 처리 변수 가져오기
	 */
	public int[] getCurrRow(GridData gdData) throws Exception 
	{
		return getCurrRow(gridDataTojdtoRecord(gdData));
	}

	public int[] getCurrRow(JDTORecord record) 
	{
		int viewRows  = Integer.parseInt(nvl(record.getFieldString("viewRows"), "10")); //ROW 갯수
		int currpage  = Integer.parseInt(nvl(record.getFieldString("viewPage"), "1")); //현재 페이지
		int startRow = (currpage - 1) * viewRows + 1;
		int endRow   = currpage * viewRows;

		return new int[]{startRow, endRow};
	}

	//해쉬맵의 내용을 GridData의 파라미터로 담는다.
	public GridData hashMapToGridData(HashMap inMap) throws Exception 
	{
		GridData returnGridData = new GridData();

		if (inMap == null || inMap.isEmpty()) 
		{
			return returnGridData;
		}

		java.util.Set set = inMap.keySet();
		java.util.Iterator iterator = set.iterator();

		String key = "";
		while (iterator.hasNext()) 
		{
			key = String.valueOf(iterator.next());
			returnGridData.addParam(key, nvl(inMap.get(key), ""));
		}

		return returnGridData;
	}

	//해쉬맵의 내용을 JDTORecord로 담는다.
	public JDTORecord hashMapTojdtoRecord(HashMap inMap) throws Exception 
	{
		JDTORecord returnJRecord = JDTORecordFactory.getInstance().create();

		if (inMap == null || inMap.isEmpty()) 
		{
			return returnJRecord;
		}

		java.util.Set set = inMap.keySet();
		java.util.Iterator iterator = set.iterator();

		String key = "";
		while (iterator.hasNext()) 
		{
			key = String.valueOf(iterator.next());
			returnJRecord.addField(key, nvl(inMap.get(key), ""));
		}

		return returnJRecord;
	}

	//해쉬맵의 내용을 JDTORecord로 담는다.
	public JDTORecord gridDataTojdtoRecord(GridData gdData) throws Exception 
	{
		JDTORecord rowJrecord = JDTORecordFactory.getInstance().create();

		if (gdData == null) 
		{
			return rowJrecord;
		}

		String params[] = gdData.getParamNames();
		for (int ii = 0; ii < params.length; ii++) 
		{
			rowJrecord.addField(params[ii], nvl(gdData.getParam(params[ii]), ""));
		}

		return rowJrecord;
	}

	//해쉬맵의 내용을 JDTORecord로 담는다.
	public HashMap jdtoRecordTohashMap(JDTORecord inJRecord) throws Exception 
	{
		HashMap returnMap = new HashMap();

		if (inJRecord == null || inJRecord.size() == 0) 
		{
			return returnMap;
		}

		java.util.Iterator iterator = inJRecord.iterateName();

		String key = "";
		
		while (iterator.hasNext()) 
		{
			key = String.valueOf(iterator.next());
			returnMap.put(key, nvl(inJRecord.getField(key), ""));
		}

		return returnMap;
	}

	//List의 JDTORecord를 HashMap으로 변환한다.
	public List listJdtoRecordTohashMap(List inDataList) throws Exception 
	{
		List returnList = new ArrayList();

		if (inDataList == null || inDataList.isEmpty()) 
		{
			return returnList;
		}

		for (int ii = 0; ii < inDataList.size(); ii++) 
		{
			returnList.add(jdtoRecordTohashMap((JDTORecord)inDataList.get(ii)));
		}

		return returnList;
	}

	/**
	 * 입력값을 원하는 포멧으로 변화하는 메소드
	 * @param no
	 * @param formatter
	 * @return
	 */
	public String format(String no, String formatter) 
	{
		try 
		{
			return format(Double.parseDouble(no), formatter);
		} 
		catch (Exception e) 
		{
			return "";
		}
	}

	public String format(int no, String formatter) 
	{
		DecimalFormat df = new DecimalFormat(formatter);
		return df.format(no);
	}

	public String format(float no, String formatter) 
	{
		DecimalFormat df = new DecimalFormat(formatter);
		return df.format(no);
	}

	public String format(long no, String formatter) 
	{
		DecimalFormat df = new DecimalFormat(formatter);
		return df.format(no);
	}

	public String format(double no, String formatter) 
	{
		DecimalFormat df = new DecimalFormat(formatter);
		return df.format(no);
	}

	public String format(int no, int len) 
	{
		DecimalFormat df = new DecimalFormat(addStr(len, "0"));
		return df.format(no);
	}

	public String formatMaxNo(int no, int maxNo) 
	{
		DecimalFormat df = new DecimalFormat(addStr((String.valueOf(maxNo)).length(), "0"));
		return df.format(no);
	}
	
	public static int getTotalLenOfTc(Map tc) 
	{
        int total = 0;
        Iterator iter = tc.keySet().iterator();
        
        while(iter.hasNext()) 
        {
            total += Integer.parseInt((String)(tc.get((String)iter.next())));
        }
        
        return total;
    }

	public double trunc(double val, int digit) 
	{
		double val2 = 0.0;
		
		if (val > 0) 
		{
			val2 = Math.floor(val * Math.pow(10, digit));
		} 
		else if (val < 0) 
		{
			val2 = Math.ceil(val * Math.pow(10, digit));
		}
		
		return val2 / Math.pow(10, digit);
	}

	public float trunc(float val, int digit) 
	{
		double val2 = 0.0;
		
		if (val > 0) 
		{
			val2 = Math.floor(val * Math.pow(10, digit));
		} 
		else if (val < 0) 
		{
			val2 = Math.ceil(val * Math.pow(10, digit));
		}
		
		return (float)(val2 / Math.pow(10, digit));
	}

	public double round(double val, int digit) 
	{
		return Math.round(val * Math.pow(10, digit)) / Math.pow(10, digit);
	}

	public float round(float val, int digit) 
	{
		return (float)(Math.round(val * Math.pow(10, digit)) / Math.pow(10, digit));
	}
	
	/**
     * 현재일자를 여러형태의 TYPE 으로 리턴한다.
     * ex) yyyy-mm-dd, hh-mm-ss, yyyyMMddhhmmss
     */ 
	public static String getCurDate(String type)
	{
		SimpleDateFormat simpledateformat = new SimpleDateFormat(type);
        Date date = new Date();
        return simpledateformat.format(date);
    }
	
	public String getStringYMDHM() {
        return getCurDate("yyyyMMddHHmm");
    }
    
    public static String getStringYMDHMS() {
        return getCurDate("yyyyMMddHHmmss");
    }    

    public String getStringHMS() {
        return getCurDate("HHmmss");
    }    

    public String getStringHMS(String seperate) {
        return getCurDate("HH"+ seperate +"mm"+ seperate +"ss");
    }    

    public String getStringYMD() {
        return getCurDate("yyyyMMdd");
    }    

    public String getStringSubYMD() {
        return getCurDate("yyMMdd");
    }    

    public String getStringYMD(String seperate) {
        return getCurDate("yyyy"+ seperate +"MM"+ seperate +"dd");
    }
    
    public static String getTcDate(String type)
    {
		SimpleDateFormat simpledateformat = new SimpleDateFormat(type);
        Date date = new Date();
        return simpledateformat.format(date);
	}
	
    /**
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
	public String setLegacyPositionWithCur(String sUpLoc)
	{	
		String sPosition = "";
		  
		if(sUpLoc.length() == 10)
		{	
			YfCommDAO dao = new YfCommDAO();
			String 		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getLegacyEquipNoWithCurEquipNo";
			JDTORecord 	jtR   	 = dao.getCommonInfo(sQueryId,new Object[]{sUpLoc.substring(0, 6)});
    		
    		if(jtR != null)
    		{
    			sPosition = StringHelper.evl(jtR.getFieldString("EQUIP_GP"), "");
    		}
    		else
    		{
			    sPosition = "H" + sUpLoc.substring(1, 4) + sUpLoc.substring(5, 6) + sUpLoc.substring(9,10) + sUpLoc.substring(6, 8);
			}
		}
		
	    return sPosition;
	}
	
	/**
	 *	현재 TR 정보의 A열연 Legacy 위치정보로 바꾼다.
	 *
	 * @param  String
     * @return String
     * @throws  
     */		
	public static String setLegacyPositionWithCurTr(String sUpLoc,String sStockId)
	{	
		String sPosition = "";
		  
		if(sUpLoc.length() == 10)
		{
			
			sPosition = "T" + sUpLoc.substring(1,2) + sUpLoc.substring(5,6);
    		 			 
			YfCommDAO	dao			= new YfCommDAO();
			String		sQueryId	= getDmCarInfo;
			JDTORecord 	jtR			= dao.getCommonInfo(sQueryId,new Object[]{sStockId});
    		 
    		if(jtR != null)
    		{
    			sPosition += StringHelper.evl(jtR.getFieldString("CAR_NO_ADDR"), "");
    		}
		}
		
	    return sPosition;
	}

	//************************************** WISEGRID **************************************
	/**
	 * WiseGrid 관련 Helper Method
	 * 디비 결과값 List를 GridData로 변환한다.
	 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
	 */
	public GridData jdtoRecordToGridData(GridData returnGrid, List dataList) throws Exception 
	{
		return jdtoRecordToGridData(returnGrid, dataList, null, null);
	}

	/**
	 * WiseGrid 관련 Helper Method
	 * 디비 결과값JDTORecord를 GridData로 변환한다.
	 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
	 */
	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecord dataJrecord) throws Exception 
	{
		return jdtoRecordToGridData(returnGrid, dataJrecord, null, null);
	}

	/**
	 * WiseGrid 관련 Helper Method
	 * 디비 결과값 List를 GridData로 변환한다.
	 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
	 */
	public GridData jdtoRecordToGridData(GridData returnGrid, List dataList, GridData gdReq) throws Exception 
	{
		return jdtoRecordToGridData(returnGrid, dataList, gdReq, null);
	}

	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecordSet dataSet, GridData gdReq) throws Exception 
	{
		return jdtoRecordToGridData(returnGrid, dataSet.toList(), gdReq, null);
	}

	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecordSet dataSet, GridData gdReq, String numberType) throws Exception 
	{
		return jdtoRecordToGridData(returnGrid, dataSet.toList(), gdReq, numberType);
	}

	/**
	 * WiseGrid 관련 Helper Method
	 * 디비 결과값JDTORecord를 GridData로 변환한다.
	 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
	 */
	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecord dataJrecord, GridData gdReq) throws Exception 
	{
		return jdtoRecordToGridData(returnGrid, dataJrecord, gdReq, null);
	}

	public GridData jdtoRecordToGridData(GridData returnGrid, List dataList, GridData gdReq, String numberType) throws Exception 
	{
		/*
		 * DataType
		 * C - t_checkbox
		 * L - t_combo
		 * N - t_number
		 * T - t_text
		 * D - t_date
		 * I - t_imagetext
		 * R - t_radio
		 */
		JDTORecord dataJrecord  = null;
		GridHeader[] gridHeader = returnGrid.getHeaders();
		String headerName		= "";
		String dataType			= "";
		String headerNameVal	= "";
		String headerNameChar	= "";

		if (dataList == null || dataList.isEmpty()) 
		{
			returnGrid.addParam("TOTALCOUNT", "0");
			//returnGrid.addParam("SELECT_MSG", MessageHelper.getUserMessage("MSG0103", new String[]{""}, ""));
		} 
		else 
		{
			String totCount = "0";
			
			for (int ii = 0; ii < gridHeader.length; ii++) 
			{
				headerName = gridHeader[ii].getID();
				dataType = gridHeader[ii].getDataType();

				/*
				 * 컬럼에 맞게 데이타를 세팅한다.
				 * SEQNO, CHECK, CRUD은  디비에서 가져오지 않는다. 화면에서 가져오는 값이다. 화면에 없다면..패스.
				 */
				if ("SEQNO".equals(headerName)) 
				{
					for (int kk = 0; kk < dataList.size(); kk++) 
					{
						returnGrid.getHeader("SEQNO").addValue(String.valueOf(kk + 1), "");
					}
				} 
				else if ("CHECK".equals(headerName)) 
				{
					for (int kk = 0; kk < dataList.size(); kk++) 
					{
						returnGrid.getHeader("CHECK").addValue("0", "");
					}
				} 
				else if ("CRUD".equals(headerName)) 
				{
					for (int kk = 0; kk < dataList.size(); kk++) 
					{
						returnGrid.getHeader("CRUD").addValue("R", "R");
					}
				} 
				else 
				{
					for (int jj = 0; jj < dataList.size(); jj++) 
					{
						dataJrecord = (JDTORecord)dataList.get(jj);

						/*
						 * 페이징 처리를 했었을 경우 total row값이 들어가는 파라미터를 생성해 줘야 한다.
						 */
						if ("0".equals(totCount)) 
						{
							totCount = StringHelper.evl(dataJrecord.getFieldString("TOTALCOUNT"), "0");
						}

						headerNameVal = StringHelper.evl(dataJrecord.getFieldString(headerName), "");
						
						if (!"".equals(headerNameVal)) 
						{
							headerNameChar = headerNameVal.substring(0, 1);
						}

						if ("L".equals(dataType)) 
						{
							returnGrid.getHeader(headerName).addSelectedHiddenValue(headerNameVal);	//t_combo 일때...
						} 
						else if ("C".equals(dataType) || "R".equals(dataType)) 
						{
							//t_checkbox, t_radio 일때...
							//0, 1 이 아니면 에러가 남....에러가 나지 않도록...세팅함
							if ("0".equals(headerNameChar) || "1".equals(headerNameChar)) 
							{
								returnGrid.getHeader(headerName).addValue(headerNameVal, "");
							} 
							else 
							{
								returnGrid.getHeader(headerName).addValue("0", "");	//언체크로 세팅(0)
							}
						} 
						else if ("D".equals(dataType)) 
						{
							//t_date 일때...YYYYMMDD 형식이 아니면 에러가 떨어짐.
							if (headerNameVal.length() > 10) 
							{
								returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(headerNameVal.substring(0, 10), "-", ""), "");
							} 
							else if (headerNameVal.length() == 10 || headerNameVal.length() == 8) 
							{
								returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(headerNameVal, "-", ""), "");
							} 
							else 
							{
								returnGrid.getHeader(headerName).addValue("", "");
							}
						} 
						else if ("I".equals(dataType)) 
						{
							returnGrid.getHeader(headerName).addValue(headerNameVal, headerNameVal, 0);	//t_imagetext 일때...
						}
						else if ("N".equals(dataType)) 
						{
							//t_number 일때 값이 0이면  space를 전송한다.
							if (!"number".equals(numberType)) 
							{
								if (!"0".equals(headerNameVal)) 
								{
									returnGrid.getHeader(headerName).addValue(headerNameVal, "");
								} 
								else 
								{
									returnGrid.getHeader(headerName).addValue("", "");
								}
							} 
							else 
							{
								returnGrid.getHeader(headerName).addValue(headerNameVal, "");
							}
						} 
						else 
						{
							returnGrid.getHeader(headerName).addValue(headerNameVal, "");
						}
					}//for
				}//if
			}//for

			/*
			 * 페이징 처리를 했었을 경우 total row값이 들어가는 파라미터를 생성해 줘야 한다.
			 */
			returnGrid.addParam("TOTALCOUNT", totCount);	//total row 세팅..
		}

		/*
		 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
		 */
		if (gdReq != null) 
		{
			String params[] = gdReq.getParamNames();

			for (int ii=0; ii<params.length; ii++) 
			{
				returnGrid.addParam(params[ii], StringHelper.evl(gdReq.getParam(params[ii]), ""));
			}
		}

		return returnGrid;
	}

	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecord dataJrecord, GridData gdReq, String numberType) throws Exception 
	{
		GridHeader[] gridHeader = returnGrid.getHeaders();
		String headerName		= "";
		String dataType			= "";
		String headerNameVal	= "";
		String headerNameChar	= "";

		/*
		 * 컬럼에 맞게 데이타를 세팅한다.
		 * SEQ_NO, SELECTED은 따로 생성한다. 이 두개의 컬럼은 디비에서 가져오지 않는다.
		 */
		if (dataJrecord == null || dataJrecord.size() == 0) 
		{
			returnGrid.addParam("TOTALCOUNT", "0");
		} 
		else 
		{
			for (int ii = 0; ii < gridHeader.length; ii++) 
			{
				headerName = gridHeader[ii].getID();
				dataType = gridHeader[ii].getDataType();

				/*
				 * 페이징 처리를 했었을 경우 total row값이 들어가는 파라미터를 생성해 줘야 한다.
				 */
				if (ii == 0) 
				{
					returnGrid.addParam("TOTALCOUNT", StringHelper.evl(dataJrecord.getFieldString("TOTALCOUNT"), "0"));	//total row 세팅..
				}

				/*
				 * SEQNO, CHECK, CRUD은  디비에서 가져오지 않는다. 화면에서 가져오는 값이다. 화면에 없다면..패스.
				 */
				if ("SEQNO".equals(headerName)) 
				{
					returnGrid.getHeader("SEQNO").addValue("1", "");
				} 
				else if ("CHECK".equals(headerName)) 
				{
					returnGrid.getHeader("CHECK").addValue("0", "");
				} 
				else if ("CRUD".equals(headerName)) 
				{
					returnGrid.getHeader("CRUD").addValue("R", "R");
				} 
				else 
				{
					/*
					 * 컬럼에 맞게 데이타를 세팅한다.
					 */
					headerNameVal = StringHelper.evl(dataJrecord.getFieldString(headerName), "");
					
					if (!"".equals(headerNameVal)) 
					{
						headerNameChar = headerNameVal.substring(0, 1);
					}

					if ("L".equals(dataType)) 
					{
						returnGrid.getHeader(headerName).addSelectedHiddenValue(headerNameVal);	//t_combo 일때...
					} 
					else if ("C".equals(dataType) || "R".equals(dataType)) 
					{
						//t_checkbox, t_radio 일때...
						//0, 1 이 아니면 에러가 남....에러가 나지 않도록...세팅함
						if ("0".equals(headerNameChar) || "1".equals(headerNameChar)) 
						{
							returnGrid.getHeader(headerName).addValue(headerNameChar, "");
						} 
						else 
						{
							returnGrid.getHeader(headerName).addValue("0", "");	//언체크로 세팅(0)
						}
					} 
					else if ("D".equals(dataType)) 
					{
						//t_date 일때...YYYYMMDD 형식이 아니면 에러가 떨어짐.
						if (headerNameVal.length() > 10) 
						{
							returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(headerNameVal.substring(0, 10), "-", ""), "");
						} 
						else if (headerNameVal.length() == 10 || headerNameVal.length() == 8) 
						{
							returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(headerNameVal, "-", ""), "");
						} 
						else 
						{
							returnGrid.getHeader(headerName).addValue("", "");
						}
					} 
					else if ("I".equals(dataType)) 
					{
						returnGrid.getHeader(headerName).addValue(headerNameVal, headerNameVal, 0);	//t_imagetext 일때...
					} 
					else if ("N".equals(dataType)) 
					{
						//t_number 일때 값이 0이면  space를 전송한다.
						if (!"number".equals(numberType)) 
						{
							if (!"0".equals(headerNameVal)) 
							{
								returnGrid.getHeader(headerName).addValue(headerNameVal, "");
							} 
							else 
							{
								returnGrid.getHeader(headerName).addValue("", "");
							}
						} 
						else 
						{
							returnGrid.getHeader(headerName).addValue(headerNameVal, "");
						}
					} 
					else 
					{
						returnGrid.getHeader(headerName).addValue(headerNameVal, "");
					}
				}
			}//for
		}

		/*
		 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
		 */
		if (gdReq != null) 
		{
			String params[] = gdReq.getParamNames();

			for (int ii = 0; ii < params.length; ii++) 
			{
				returnGrid.addParam(params[ii], StringHelper.evl(gdReq.getParam(params[ii]), ""));
			}
		}
		
		return returnGrid;
	}


	/**
	 * Pk로 조회된 값을 GridData의 파리미터로 세팅한다.
	 * jsp에서는 event="EndQuery()"에서 GridObj.GetParam(key)으로 받으면 된다.
	 */
	public GridData jdtoRecordToGridParam(JDTORecord dataJrecord) throws Exception 
	{
		GridData returnGrid = new GridData();
		java.util.Iterator iterator = dataJrecord.iterateName();
		String key = "";

		while (iterator.hasNext()) 
		{
			key = String.valueOf(iterator.next());
			returnGrid.addParam(key, String.valueOf(dataJrecord.getField(key)));
		}

		return returnGrid;
	}

	/**
	 * Pk로 조회된 값을 GridData의 파리미터로 세팅한다.(기존의 Grid에 추가하고싶을때)
	 * jsp에서는 event="EndQuery()"에서 GridObj.GetParam(key)으로 받으면 된다.
	 */
	public GridData jdtoRecordToGridParam(JDTORecord dataJrecord, GridData returnGrid) throws Exception 
	{
		java.util.Iterator iterator = dataJrecord.iterateName();
		String key = "";

		while (iterator.hasNext()) 
		{
			key = String.valueOf(iterator.next());
			returnGrid.addParam(key, String.valueOf(dataJrecord.getField(key)));
		}

		return returnGrid;
	}

	/**
	 * GridData 의 내용을 List로 변환한다.
	 */
	public List GridDataToList(GridData dataGrid) throws Exception 
	{
		List returnList = new ArrayList();
		JDTORecord rowJrecord = null;

		if (dataGrid == null) 
		{
			return returnList;
		}

		GridHeader[] gridHeaders = dataGrid.getHeaders();

		for (int ii = 0; ii < gridHeaders[0].getRowCount(); ii++) 
		{
			rowJrecord = JDTORecordFactory.getInstance().create();
			for (int jj = 0; jj < gridHeaders.length; jj++) 
			{
				rowJrecord.addField(gridHeaders[jj].getID(), StringHelper.evl(gridHeaders[jj].getValue(ii), "").trim());
			}

			returnList.add(rowJrecord);
		}

		return returnList;
	}

	/**
	 * GridData의 PARAM 정보를 JDTORecord 으로 변환하여 리턴한다.(GridData의 조회 조건을 가져오기위해 사용)
	 *
	 * @param inDto
	 * @return
	 */
	public JDTORecord genParamToJDTORecord(GridData inDto) {
		JDTORecord outRecord = JDTORecordFactory.getInstance().create();
		boolean isUpperKey = false;

		try 
		{
			if (inDto.getParam("set_upper") != null && "true".equals(inDto.getParam("set_upper"))) 
			{
				isUpperKey = true;
			}

			outRecord = JDTORecordFactory.getInstance().create();
			String params[] = inDto.getParamNames();
			
			for (int ii = 0; ii < params.length; ii++) 
			{
				String key = (String) params[ii];
				String value = StringHelper.nvl(inDto.getParam(params[ii]), "");

				outRecord.setField((isUpperKey) ? key.toUpperCase() : key, value);	// DBAssistant 에 전달할 JDTORecord를 설정합니다.
			}
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.DEBUG, e.getMessage());
		}

		return outRecord;
	}

	/**
	 * GridData의 입력/수정/삭제 정보를 JDTORecord [] 으로 변환하여 리턴한다. (GridData의 입력/수정/삭제
	 * 항목을 가져오기위해 사용)
	 *
	 * @param inDto
	 * @return
	 */
	public JDTORecord[] genGridToJDTORecord(GridData inDto) throws Exception 
	{
		boolean isUpperKey = false;

		if (inDto.getParam("set_upper") != null && "true".equals(inDto.getParam("set_upper"))) 
		{
			isUpperKey = true;
		}

		GridHeader[] ghs = inDto.getHeaders();
		int hCount = ghs.length;
		int rCount = 0;
		if (hCount > 0) 
		{
			rCount = ghs[0].getRowCount();
		}
		JDTORecord[] jdtoAl = new JDTORecord[rCount];
		logger.println(LogLevel.DEBUG, "========  GridData -> JDTORecord []  ================");
		logger.println(LogLevel.DEBUG, "헤더갯수:" + hCount);
		logger.println(LogLevel.DEBUG, "Row갯수:" + rCount);

		logger.println(LogLevel.DEBUG, "========== GridData inDto ROW DATA ===========");
		for (int ii = 0; ii < rCount; ii++) 
		{
			GridHeader chHeader = inDto.getHeader("CHECK");
			boolean Checked = (Integer.parseInt(chHeader.getValue(ii)) == 1) ? true : false;
			
			if (Checked) 
			{
				JDTORecord jDto = genParamToJDTORecord(inDto);

				for (int jj = 0; jj < hCount; jj++) 
				{
					String key = ghs[jj].getID();
					String rValue = "";

					if (ghs[jj].getDataType().equals(OperateGridData.t_combo)) 
					{
						rValue = StringHelper.evl(ghs[jj].getComboHiddenValues()[ghs[jj].getSelectedIndex(ii)], "");
					} 
					else
					{
						rValue = StringHelper.evl(ghs[jj].getValue(ii), "");
					}

					jDto.setField((isUpperKey) ? key.toUpperCase() : key, rValue);
				}
				jdtoAl[ii] = jDto;
			}
		}

		logger.println(LogLevel.DEBUG, "========== JDTORecord START ===========");
		for (int mm = 0; mm < jdtoAl.length; mm++) 
		{
			logger.println(LogLevel.DEBUG, jdtoAl[mm].toString());
		}
		logger.println(LogLevel.DEBUG, "========== JDTORecord END ===========");

		return jdtoAl;
	}

	/**
	 * executeBatch를 사용했을 경우 성공여부를 리턴하는 메소드
	 * 사용 예)
	 * int[] results = new CommonDAO.executeBatch(?, ?);
	 * if (isBatchSuccess(results)) { 성공 } else { 실패 };
	 */
	public boolean isBatchSuccess(int[] results) 
	{
		if (results == null || results.length == 0) 
		{
			return false;
		}

		boolean result = true;

		for (int ii = 0; ii < results.length; ii++) 
		{
			if (results[ii] == -3) 
			{
				result = false;
				break;
			}
		}

		return result;
	}

	/**
	 * 20070402의 날짜포멧을 원하는 구분자로 바꾸고 싶을때..
	 * 사용 예)
	 * CmnUtil.addDateGubunStr("20070405", "-")
	 * 6자리일때도 가능하게 추가(200705 -> 2007-05)
	 */
	public String addDateGubunStr(String src, String gubun) 
	{
		String temp = StringHelper.replaceStr(StringHelper.replaceStr(src, "/", ""), ".", "");
		
		if (temp.length() == 8) 
		{
			return temp.substring(0, 4) + gubun + temp.substring(4, 6) + gubun + temp.substring(6);
		} 
		else if (temp.length() == 6) 
		{
			return temp.substring(0, 4) + gubun + temp.substring(4, 6);
		} 
		else 
		{
			return src;
		}
	}

	/**
	 * HH24:MI:SS의 시간포멧을 원하는 구분자로 바꾸고 싶을때..
	 * 사용 예)
	 * CmnUtil.addTimeGubunStr("HH24:MI:SS", " ")
	 *
	 */
	public String addTimeGubunStr(String src, String gubun) 
	{
		String temp = StringHelper.replaceStr(StringHelper.replaceStr(src, ":", ""), " ", "");
		
		if (temp.length() == 6) 
		{
			return temp.substring(0, 2) + gubun + temp.substring(2, 4) + gubun + temp.substring(4,6);
		} 
		else 
		{
			return src;
		}
	}

	/**
	 *메서드명 : getCalsDate
	 *메서드 기능 : 원하는 시점의 날짜를 찾는다.
	 *PARAM : string, int
	 *     getCalsDate(0, 1) :오늘
	 *     getCalsDate(1, 1) :년, -1(1년전 오늘),-2(2년전 오늘)
	 *     getCalsDate(2, 1) :개월, -1(1개월전 오늘),-2(2개월전 오늘), 1(1개월후 오늘)
	 *     getCalsDate(3 or 4 or 8,1) :주, -1(일주일전 같은요일), 1(1주일후 같은요일)
	 *     getCalsDate(5 or 6 or 7,1) :하루, -1(오늘부터 하루전), 1(오늘부터 하루후)
	 *     getCalsDate(9, 1) :12시간, -1(12시간전) 1(12시간후) 2(24시간후
	 *PARAM fmtStr : 출력을 원하는 날짜 형식 ex) "yyyyMMdd", "yyyy-MM-dd"
	 *RETURN VALUE : string
	 */
	public String getCalsDate(String yyddtt, int y, int z, String fmtStr) 
	{
		int yy = Integer.parseInt(yyddtt.substring(0, 4));     // 일시에서  년도
		int mm = Integer.parseInt(yyddtt.substring(4, 6)) - 1; // 월은 0부터 11로 0은 1월 ~ 11은 12월이다. 그래서 월에서 1을 뺀다.
		int dd = Integer.parseInt(yyddtt.substring(6, 8));     // 일시에서 일

		Calendar cal = Calendar.getInstance(Locale.KOREAN);
		cal.set(yy, mm, dd);
		cal.add(y, z);
		Date currentTime = cal.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(fmtStr, Locale.KOREAN);
		String timestr = formatter.format(currentTime);

		return timestr;
	}

	/**
	 *메서드명 : getHour
	 *메서드 기능 : 두일자의 차이의 구하고자 하는 day, hour, minute, second를 얻는다.
	 *PARAM sDt: 시작일자
	 *PARAM eDt: 종료일자
	 *PARAM type : 구하고자 하는 type(day, hour, minute, second)
	 *RETURN VALUE : string
	*/
	public String getHour(String sDt, String eDt, String type) throws Exception 
	{
		String time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		Date date1 = sdf.parse(sDt);
		Date date2 = sdf.parse(eDt);

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(date1);
		c2.setTime(date2);

		long intervalMilli = c2.getTimeInMillis() - c1.getTimeInMillis();
		long second = 1000;
		long minute = second * 60;
		long hour = minute * 60;
		long day = hour * 24;

		if ("second".equals(type)) 
		{
			time = String.valueOf(intervalMilli / hour);
		} 
		else if ("minute".equals(type)) 
		{
			time = String.valueOf(intervalMilli / minute);
		} 
		else if ("hour".equals(type)) 
		{
			time = String.valueOf(intervalMilli / hour);
		} 
		else if ("day".equals(type)) 
		{
			time = String.valueOf(intervalMilli / day);
		}

		return time;
	}
	/**
	 *메서드명 : getDeci
	 *메서드 기능 : 숫자를 sFormat형식으로 변환하여 반환한다.
	 *PARAM strData : 받은 수치 데이터
	 *PARAM sFormat : sFormat형식
	 *RETURN VALUE : string
	*/
    public String getDeci(String sName, String sFormat) {  // 숫자를 sFormat형식으로 변환해주는 메소드
    	String sReturn = "";
        try{
    		if(sName != null || !"".equals(sName)){
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
	 *메서드명 : getDecimal
	 *메서드 기능 : String으로 받은 수치 소수점을 찍어서 반환한다.
	 *PARAM strData : 받은 수치 데이터
	 *PARAM strDecimal : 소수점을 찍어줄 자리수
	 *RETURN VALUE : string
	*/
	public String getDecimal(String strData, String strDecimal) 
	{
		try 
		{
			String temData1 = String.valueOf(Integer.parseInt(strData.substring(0, strData.length()-Integer.parseInt(strDecimal))));
			String temData2 = String.valueOf(Integer.parseInt(strData.substring(strData.length()-Integer.parseInt(strDecimal),strData.length())));

			return temData1 + "." + temData2;
		} 
		catch (Exception e) 
		{
			return strData;
		}
	}

	/**
	 *메서드명 : setAddDate
	 *메서드 기능 : String으로 받은 날짜를 int로 넘어온 날짜로 더한다.
	 *PARAM pDate : 년월일을 더할 기준값.
	 *PARAM pYy, pMm,pDd, pHh, pMi : Int형으로 더할 년월일시분
	 *RETURN VALUE : string
	*/
	public String setAddDate(String pDate, int pYy, int pMm, int pDd, int pHh, int pMi) 
	{
		int yy = 0;
		int mm = 0;
		int dd = 0;
		int hh = 0;
		int mi = 0;

		String result = "";

		if (pDate.length() == 14) 
		{
			yy = Integer.parseInt(pDate.substring( 0,  4));
			mm = Integer.parseInt(pDate.substring( 4,  6));
			dd = Integer.parseInt(pDate.substring( 6,  8));
			hh = Integer.parseInt(pDate.substring( 8, 10));
			mi = Integer.parseInt(pDate.substring(10, 12));

			DateFormat format = new SimpleDateFormat("yyyyMMddHHmm");

			//Calendar 에서는 1월부터 12월을 주소값으로 0부터 11까지 가지고 있으므로 실제 월에서 -1을 해준다.
			mm--;

			Calendar cal = Calendar.getInstance(Locale.KOREAN);

			//기준일로 세팅
			cal.set(yy, mm, dd, hh, mi);

			//기준일에 파라미터로 넘어온 년월일시분을 더해준다.
			cal.add(Calendar.YEAR  , pYy);
			cal.add(Calendar.MONTH , pMm);
			cal.add(Calendar.DATE  , pDd);
			cal.add(Calendar.HOUR  , pHh);
			cal.add(Calendar.MINUTE, pMi);

			result = format.format(cal.getTime()) + "00";
		}

		return result;
	}

	 /**
	 * 수치데이터 확인
	 * @param gdReq
	 * @return gdRes
	 */
	public String getInterFlag(String intData) 
	{
		try 
		{
			new Integer(intData);

			return "Y";
		} 
		catch (Exception e) 
		{
			return "N";
		}
	}

	/**
	 * 길이 만큼 Char 추가
	 * @param len 추가할 길이
	 * @param chr 추가 Char
	 * @return 가공하여 Return
	 */
	public String addStr(int len, String chr) 
	{
		StringBuffer sb = new StringBuffer();

		for (int ii = 0; ii < len; ii++) 
		{
			sb = sb.append(chr);
		}

		return substr(sb.toString(), 0, len);
	}

	/**
	 * String 길이 만큼 우측에 Char 추가
	 * @param src 현재 값
	 * @param len 가공후 길이
	 * @param chr 추가 Char
	 * @return 가공하여 Return
	 */
	public String getRPad(String src, int len, String chr) 
	{
		String ret = "";                  //Return String
		int sLen = src.getBytes().length; //원본 길이

		if (sLen < len) 
		{
			ret = src + addStr(len - sLen, chr);
		} 
		else if (sLen > len) 
		{
			ret = substr(src, 0, len);
		} 
		else 
		{
			ret = src;
		}

		return ret;
	}

	/**
	 * String 길이 만큼 좌측에 Char 추가
	 * @param src 현재 값
	 * @param len 가공후 길이
	 * @param chr 추가 Char
	 * @return 가공하여 Return
	 */
	public String getLPad(String src, int len, String chr) 
	{
		String ret = "";                  //Return String
		int sLen = src.getBytes().length; //원본 길이

		if (sLen < len) 
		{
			ret = addStr(len - sLen, chr) + src;
		} 
		else if (sLen > len) 
		{
			ret = substr(src, sLen - len, len);
		} 
		else 
		{
			ret = src;
		}

		return ret;
	}

	/**
	 * String 길이 만큼 우측에 " " 추가
	 * @param src 현재 값
	 * @param len 가공후 길이
	 * @return 가공하여 Return
	 */
	public String getRPadSpc(String src, int len) 
	{
		return getRPad(src, len, " ");
	}

	/**
	 * String 길이 만큼 좌측에 "0" 추가
	 * @param src 현재 값
	 * @param len 가공후 길이
	 * @return 가공하여 Return
	 */
	public String getLPadZero(String src, int len) 
	{
		return getLPad(src, len, "0");
	}

	
	/* Date Format : "yyyyMMddHHmmss" */
	public static String getCreDateTime() throws Exception {
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), SbConstant.DATE_FORMAT_CRE_DT);
	}
	
	
	/* Date Format : "yyyyMMddHHmmss" */
	public String getDateTime14() 
	{
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyyMMddHHmmss");
	}

	/* Date Format : "yyyy-MM-ddHH:mm:ss" */
	public String getDateTime18() 
	{
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyy-MM-ddHH:mm:ss");
	}

	/* Date Format : "yyyy-MM-dd HH:mm:ss" */
	public String getDateTime19() 
	{
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm:ss");
	}

	/* Date Format : "yyyyMMdd" */
	public String getDate8() 
	{
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyyMMdd");
	}

	/* Date Format : "yyyy-MM-dd" */
	public String getDate10() 
	{
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyy-MM-dd");
	}

	/* Date Format : "HHmmss" */
	public String getTime6() 
	{
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmss");
	}

	/* Date Format : "HH:mm:ss" */
	public String getTime8() 
	{
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HH:mm:ss");
	}

	/**
	 * 한글을 2byte로 계산하여 길이 구하기
	 * @param String str
	 * @return
	 */
	public int getLength(String str) 
	{
		return str.getBytes().length;
	}

	/**
	 * Char 단위 substr
	 * @param String strLine
	 * @param int start
	 * @param int len
	 * @return String
	 */
	public String substr(String strLine, int start, int  len) 
	{
		byte[] bytes = strLine.getBytes();

		if (bytes == null || bytes.length <= start || len <= 0) 
		{
			return "";
		}

		byte[] rbytes = new byte[len];

		for (int ii = 0; ii < len; ii++) 
		{
			rbytes[ii] = bytes[start + ii];
		}

		return new String(rbytes);
	}

	/**
	 * Char 단위 substr
	 * @param String strLine
	 * @param int start
	 * @return
	 */
	public String substr(String strLine, int start) 
	{
		return substr(strLine, start, strLine.getBytes().length);
	}

	/**
	 * String Array를 String으로 변환
	 * @param String[] arrStr
	 * @return String
	 */
	public String toString(String[] arrStr) 
	{
		StringBuffer sb = new StringBuffer();
		int aLen = arrStr.length;
		
		if (aLen > 0) 
		{
			sb = sb.append(arrStr[0]);
		}

		for (int ii = 1; ii < aLen; ii++) 
		{
			sb = sb.append(", " + arrStr[ii]);
		}

		return sb.toString();
	}

	/**
	 *  14자리 12자리 8자리 String 형식의 날짜를 입력받아 날짜 범위가 올바른지 판단*
	 * @param strDate
	 * @return boolean
	 */
	public boolean checkDateFormat(String strDate) 
	{
		int year = 0;
		int mon  = 0;
		int day  = 0;
		int hour = 0;
		int min  = 0;
		int sec  = 0;
		int lastDay = 0;
		Date tmpDate = new Date();

		try 
		{
			if (strDate.length() == 14) 
			{
				year = StringHelper.parseInt(strDate.substring( 0, 4), 0);
				mon  = StringHelper.parseInt(strDate.substring( 4, 6), 0);
				day  = StringHelper.parseInt(strDate.substring( 6, 8), 0);
				hour = StringHelper.parseInt(strDate.substring( 8,10),-1);
				min  = StringHelper.parseInt(strDate.substring(10,12),-1);
				sec  = StringHelper.parseInt(strDate.substring(12,14),-1);
				tmpDate = DateHelper.toUtilDate(year, mon, day, hour, min, sec);
			} 
			else if (strDate.length() == 12) 
			{
				year = StringHelper.parseInt(strDate.substring( 0, 4), 0);
				mon  = StringHelper.parseInt(strDate.substring( 4, 6), 0);
				day  = StringHelper.parseInt(strDate.substring( 6, 8), 0);
				hour = StringHelper.parseInt(strDate.substring( 8,10),-1);
				min  = StringHelper.parseInt(strDate.substring(10,12),-1);
				tmpDate = DateHelper.toUtilDate(year, mon, day, hour, min);
			} 
			else if (strDate.length() == 8) 
			{
				year = StringHelper.parseInt(strDate.substring(0,4),0);
				mon  = StringHelper.parseInt(strDate.substring(4,6),0);
				day  = StringHelper.parseInt(strDate.substring(6,8),0);
				tmpDate = DateHelper.toUtilDate(year, mon, day);
			} 
			else 
			{
				return false;
			}

			lastDay = DateHelper.lastDay(tmpDate);
		} 
		catch (Exception e) 
		{
			return false;
		}

		if (year < 1000) 
		{
			return false;
		} 
		else if (mon < 1 || mon > 12) 
		{
			return false;
		} 
		else if (day < 1 || day > lastDay) 
		{
			return false;
		} 
		else if (hour < 0 || hour > 23) 
		{
			return false;
		} 
		else if (min < 0 || min > 59) 
		{
			return false;
		} 
		else if (sec < 0 || sec > 59) 
		{
			return false;
		} 
		else 
		{
			return true;
		}
	}

	/**
	 * 해당 값이 있는지를 Check
	 * @param String[] arrStr
	 * @param String str
	 * @return boolean
	 */
	public boolean chkExist(String[] arrStr, String str) 
	{
		boolean chkRst = false;

		if (arrStr != null && !"".equals(str)) 
		{
			int arrCnt = arrStr.length;
		
			for (int ii = 0; ii < arrCnt; ii++) 
			{
				if (arrStr[ii] != null && str.equals(arrStr[ii])) 
				{
					chkRst = true;
					break;
				}
			}
		}

		return chkRst;
	}
	
	/**
	 * 숫자형 문자인지를 Check
	 * @param String str
	 * @return boolean
	 */
	public boolean isNumber(String str) 
	{
		Pattern p = Pattern.compile("[\\d]");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 *      [A] 오퍼레이션명 : Logging 을 위한 ID 생성
	 *
	 *      @return String
	*/
	public String getLogId() 
	{
		return "<" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
	}

	/**
	 *      [A] 오퍼레이션명 : 야드구분을 포함 한 Logging 을 위한 ID 생성
	 *
	 *      @return String
	*/
	public String getLogId(String yfGp) {
		return "["+yfGp+"]"+"<" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
	}
	
	/**
	 *      [A] 오퍼레이션명 : 상위 Method 명, Logging 을 위한 ID 및 수정자를 Set
	 *
	 *      @param JDTORecord jrParam
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String modifier
	 *      @return JDTORecord
	*/
	public JDTORecord getParam(String logId, String methodNm, String modifier) 
	{
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		
		try 
		{
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
			
			if (!"".equals(modifier)) 
			{
				jrParam.setField("MODIFIER", modifier);	//수정자
			}
		} 
		catch(Exception e) 
		{
			
		}
		
		return jrParam;
	}

	/**
	 *      [A] 오퍼레이션명 : HashMap을 JDTORecord로 변환하고
	 *                       상위 Method 명, Logging 을 위한 ID Set
	 *
	 *      @param JDTORecord jrParam
	 *      @param String logId
	 *      @param String methodNm
	 *      @param HashMap hmReq
	 *      @return JDTORecord
	*/
	public JDTORecord getParam(String logId, String methodNm, HashMap hmReq) 
	{
		//Logging
		printLog(logId, methodNm, "F+");
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		
		try 
		{
			jrParam = this.hashMapTojdtoRecord(hmReq);
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
		} 
		catch(Exception e) 
		{
			
		}

		return jrParam;
	}

	/**
	 *      [A] 오퍼레이션명 : 1 Line Error Log 만들기
	 *
	 *      @param String logId
	 *      @param String logMsg
	 *      @return String
	*/
	public String makeErrorLine(String logId, String logMsg) 
	{
		return "\n" + logId + " ■Error■ " + logMsg;
	}

	/**
	 *      [A] 오퍼레이션명 : 1 Line Warning Log 만들기
	 *
	 *      @param String logId
	 *      @param String logMsg
	 *      @return String
	*/
	public String makeWarnLine(String logId, String logMsg) 
	{
		return logId + " ■Warning■ " + logMsg;
	}

	/**
	 *      [A] 오퍼레이션명 : Error Log 만들기
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String errMsg
	 *      @return String
	*/
	public String makeErrorLog(String logId, String methodNm, String errMsg) 
	{
		return makeErrorLine(logId, "Method  : " + methodNm) + makeErrorLine(logId, "Message : " + errMsg);
	}

	/**
	 *      [A] 오퍼레이션명 : Exception Error Log 만들기
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param Exception e
	 *      @return String
	*/
	public String makeErrorLog(String logId, String methodNm, Exception e) 
	{
		StackTraceElement [] str = e.getStackTrace();
		logger.println(LogLevel.ERROR, logId + " [에러발생위치] -> " + str[0]);
		logger.println(LogLevel.ERROR, logId + " [원인		] -> " + e.toString());
		logger.println(LogLevel.ERROR, logId + " [메소드명	] -> " + methodNm);
		logger.println(LogLevel.ERROR, logId + " [추가정보	] -> " + e.getMessage());
		
		
		return makeErrorLine(logId, "Method  : " + methodNm) + makeErrorLine(logId, "Message : " + e.getMessage());
	}
	/**
	 *      [A] 오퍼레이션명 : Exception Error Log 만들기
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param Exception e
	 *      @return String
	*/
	public String makeUserErrorLog(String logId, String methodNm, Exception e) 
	{
		StackTraceElement [] str = e.getStackTrace();
		logger.println(LogLevel.ERROR, logId + " [에러발생위치] -> " + str[0]);
		logger.println(LogLevel.ERROR, logId + " [원인		] -> " + e.toString());
		logger.println(LogLevel.ERROR, logId + " [메소드명	] -> " + methodNm);
		logger.println(LogLevel.ERROR, logId + " [추가정보	] -> " + e.getMessage());
		return "[USER_EXCEPTION]"+e.getMessage()+" "+"[LogId ::"+ logId + "]";
	}
	/**
	 *      [A] 오퍼레이션명 : Debug Logging
	 *
	 *      @param String logId
	 *      @param String logMsg
	 *      @param String logGp ([시작, 종료] + 구분)
	 *      @return void
	*/
	public void printLog(String logId, String logMsg, String logGp) 
	{
		String prnLog = "";
		
		//Thread.currentThread().getName();

		if (isEmpty(logGp)) 
		{
			prnLog = logMsg;
		} 
		else if (logGp.endsWith("+")) 
		{
			prnLog = "▼" + logGp + "▼ " + logMsg;
		} 
		else if (logGp.endsWith("-")) 
		{
			prnLog = "▲" + logGp + "▲ " + logMsg;
		} 
		else 
		{
			prnLog = "●" + logGp + "● " + logMsg;
		}

		logger.println(LogLevel.DEBUG, logId + " " + prnLog);
	}

	/**
	 *      [A] 오퍼레이션명 : Debug Logging
	 *                        Method 시작시 Logging 및 gdReq 에 상위 Method 명, Logging 을 위한 ID 를 Set
	 *
	 *      @param String logId
	 *      @param String logMsg
	 *      @param String logGp (Main 시작, Sub 시작)
	 *      @return void
	*/
	public void printLog(String logId, String logMsg, String logGp, GridData gdReq) 
	{
		//Logging
		printLog(logId, logMsg, logGp);

		if (logGp.endsWith("+")) 
		{
			gdReq.setNavigateValue(logMsg);	//상위 Method 명

			if ("F+".equals(logGp)) 
			{
				gdReq.setIPAddress(logId);	//Logging 을 위한 ID
			}
		}
	}

	/**
	 *      [A] 오퍼레이션명 : Debug Logging
	 *                        Method 시작시 Logging 및 gdReq 에 상위 Method 명, Logging 을 위한 ID 를 Set
	 *
	 *      @param String logId
	 *      @param String logMsg
	 *      @param String logGp (Main 시작, Sub 시작)
	 *      @return void
	*/
	public void printLog(String logId, String logMsg, String logGp, JDTORecord jrParam) 
	{
		//Logging
		printLog(logId, logMsg, logGp);

		try 
		{
			if (logGp.endsWith("+")) 
			{
				jrParam.setResultMsg(logMsg);		//상위 Method 명
	
				if ("F+".equals(logGp)) 
				{
					jrParam.setResultCode(logId);	//Logging 을 위한 ID
				}
			}
		} 
		catch(Exception e) 
		{
			
		}
	}

	/**
	 *      [A] 오퍼레이션명 : Default Error Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param Object caller
	 *      @param Exception e
	 *      @return void
	*/
	public void printErrorLog(String logId, String methodNm, Object caller, Exception e) 
	{
		logger.println(LogLevel.ERROR, makeErrorLine(logId, "Method  : " + methodNm));
		logger.println(LogLevel.ERROR, caller, "\n", e);
		logger.println(LogLevel.ERROR, logId + " ▲Error▲ " + methodNm);
	}

	/**
	 *      [A] 오퍼레이션명 : Error Message 있는 Error Logging
	 *
	 *      @param String ErrMsg
	 *      @param Object caller
	 *      @param Exception e
	 *      @return void
	*/
	public void printErrorLog(String ErrMsg, Object caller, Exception e) 
	{
		logger.println(LogLevel.ERROR, caller, ErrMsg + "\n", e);
	}

	/**
	 *      [A] 오퍼레이션명 : Error Message 있는 Error Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String ErrMsg
	 *      @param Object caller
	 *      @param Exception e
	 *      @return void
	*/
	public void printErrorLog(String logId, String methodNm, String ErrMsg, Object caller, Exception e) 
	{
		logger.println(LogLevel.ERROR, caller, ErrMsg + "\n", e);
		logger.println(LogLevel.ERROR, logId + " ▲Error▲ " + methodNm);
	}

	/**
	 *      [A] 오퍼레이션명 : Warning Message Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String WarnMsg
	 *      @return void
	*/
	public void printWarnLog(String logId, String methodNm, String WarnMsg) 
	{
		logger.println(LogLevel.WARNING, makeWarnLine(logId, "Method  : " + methodNm));
		logger.println(LogLevel.WARNING, makeWarnLine(logId, "Message : " + WarnMsg ));
	}

	/**
	 *      [A] 오퍼레이션명 : SQL Error Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String jspeed_query_id
	 *      @param Object[] param
	 *      @param Exception e
	 *      @return String
	*/
	public String makeSqlErrorLog(String logId, String methodNm, String jspeed_query_id, Object[] param, Exception e) 
	{
		StringBuffer sb = new StringBuffer();

		try 
		{
			sb = sb.append(makeErrorLine(logId, "Method  : " + methodNm       ));
			sb = sb.append(makeErrorLine(logId, "Message : " + e.getMessage() ));
			sb = sb.append(makeErrorLine(logId, "QueryID : " + jspeed_query_id));

			if (!isEmpty(jspeed_query_id)) 
			{
				sb = sb.append("\n" + QueryService.getInstance().getSQL(jspeed_query_id));

				if (param != null) 
				{
					int pLen = param.length;
					
					sb = sb.append("\n▩ {");
					
					for (int ii = 0; ii < pLen; ii++) 
					{
						if (ii > 0) { sb = sb.append(", "); }
						sb = sb.append(ii + "=" + param[ii].toString());
					}
					sb = sb.append("}");
				}
			} 
			else 
			{
				sb = sb.append(makeErrorLine(logId, "jspeed_query_id is null"));
			}
		} 
		catch (Exception ex) 
		{
			sb = sb.append(makeErrorLine(logId, "jSpeed Query Service에 등록되지 않은 jspeed_query_id 입니다."));
		}
		
		sb = sb.append("\n");

		return sb.toString();
	}

	/**
	 *      [A] 오퍼레이션명 : SQL Error Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String jspeed_query_id
	 *      @param Object[][] param
	 *      @param Exception e
	 *      @return String
	*/
	public String makeSqlErrorLog(String logId, String methodNm, String jspeed_query_id, Object[][] param, Exception e) 
	{
		StringBuffer sb = new StringBuffer();

		try 
		{
			sb = sb.append(makeErrorLine(logId, "Method  : " + methodNm       ));
			sb = sb.append(makeErrorLine(logId, "Message : " + e.getMessage() ));
			sb = sb.append(makeErrorLine(logId, "QueryID : " + jspeed_query_id));

			if (!isEmpty(jspeed_query_id)) 
			{
				sb = sb.append("\n" + QueryService.getInstance().getSQL(jspeed_query_id));

				if (param != null) 
				{
					int pLen1 = param.length;
					int pLen2 = 0;

					for (int ii = 0; ii < pLen1; ii++) 
					{
						pLen2 = param[ii].length;
						sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : {");
						
						for (int jj = 0; jj < pLen2; jj++) 
						{
							if (jj > 0) { sb = sb.append(", "); }
							sb = sb.append(jj + "=" + param[ii][jj].toString());
						}
						sb = sb.append("}");
					}
				}
			} 
			else 
			{
				sb = sb.append(makeErrorLine(logId, "jspeed_query_id is null"));
			}
		} 
		catch (Exception ex) 
		{
			sb = sb.append(makeErrorLine(logId, "jSpeed Query Service에 등록되지 않은 jspeed_query_id 입니다."));
		}
		sb = sb.append("\n");

		return sb.toString();
	}

	/**
	 *      [A] 오퍼레이션명 : SQL Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String jspeed_query_id
	 *      @param Object[] param
	 *      @return void
	*/
	public void printSqlLog(String logId, String logMsg, String jspeed_query_id, Object[] param) 
	{
		try 
		{
			if (!isEmpty(jspeed_query_id)) 
			{
				StringBuffer sb = new StringBuffer("\n▩▩ " + logId + " " + logMsg + " ▩▩");
				sb = sb.append("\n▩ " + jspeed_query_id + "\n");
				sb = sb.append(QueryService.getInstance().getSQL(jspeed_query_id));

				if (param != null) 
				{
					int pLen = param.length;

					sb = sb.append("\n▩ {");
					
					for (int ii = 0; ii < pLen; ii++) 
					{
						if (ii > 0) { sb = sb.append(", "); }
						sb = sb.append(ii + "=" + param[ii].toString());
					}
					sb = sb.append("}");
				}

				sb = sb.append("\n▩▩ " + logId + " " + logMsg + " ▩▩");

				logger.println(LogLevel.DEBUG, sb.toString());
			}
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.ERROR, logId + " ■□■□■ Error : " + jspeed_query_id  + " << " + logMsg);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : SQL Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String jspeed_query_id
	 *      @param Object[][] param
	 *      @return void
	*/
	public void printSqlLog(String logId, String logMsg, String jspeed_query_id, Object[][] param) 
	{
		try 
		{
			if (!isEmpty(jspeed_query_id)) 
			{
				StringBuffer sb = new StringBuffer("\n▩▩ " + logId + " " + logMsg + " ▩▩");
				sb = sb.append("\n▩ " + jspeed_query_id + "\n");
				sb = sb.append(QueryService.getInstance().getSQL(jspeed_query_id));

				if (param != null) 
				{
					int pLen1 = param.length;
					int pLen2 = 0;

					for (int ii = 0; ii < pLen1; ii++) 
					{
						pLen2 = param[ii].length;
						sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : {");
						
						for (int jj = 0; jj < pLen2; jj++) 
						{
							if (jj > 0) { sb = sb.append(", "); }
							sb = sb.append(jj + "=" + param[ii][jj].toString());
						}
						sb = sb.append("}");
					}
				}

				sb = sb.append("\n▩▩ " + logId + " " + logMsg + " ▩▩");

				logger.println(LogLevel.DEBUG, sb.toString());
			}
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.ERROR, logId + " ■□■□■ Error : " + jspeed_query_id  + " << " + logMsg);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : DB DML Parameter Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param Object[][] param
	 *      @return void
	*/
	public void printParam(String logId, String logMsg, String[][] param) 
	{
		try 
		{
			int pLen1 = param.length;
			int pLen2 = 0;
			StringBuffer sb = new StringBuffer("\n▩▩ " + logId + " " + logMsg + " ▩▩");

			for (int ii = 0; ii < pLen1; ii++) 
			{
				pLen2 = param[ii].length;
				sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : {");
				
				for (int jj = 0; jj < pLen2; jj++) 
				{
					if (jj > 0) 
					{ 
						sb = sb.append(", "); 
					}
					
					sb = sb.append(jj + "=" + param[ii][jj].toString());
				}
				sb = sb.append("}");
			}

			sb = sb.append("\n▩▩ " + logId + " " + logMsg + " ▩▩");

			logger.println(LogLevel.DEBUG, sb.toString());
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : DB DML Parameter Logging - 삭제 대상
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param Object[][] param
	 *      @param int[] trtRst
	 *      @return void
	*/
	public void printParam(String logId, String logMsg, Object[][] param, int[] trtRst) 
	{
		try 
		{
			int pLen1 = param.length;
			int pLen2 = 0;
			StringBuffer sb = new StringBuffer("\n▩▩ " + logId + " " + logMsg + " ▩▩");

			for (int ii = 0; ii < pLen1; ii++) 
			{
				pLen2 = param[ii].length;
				sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : {");
				
				for (int jj = 0; jj < pLen2; jj++) 
				{
					if (jj > 0) { sb = sb.append(", "); }
					sb = sb.append(jj + "=" + param[ii][jj].toString());
				}
				sb = sb.append("}");
			}

			sb = sb.append("\n▩▩ " + logId + " " + logMsg + " ▩▩");

			logger.println(LogLevel.DEBUG, sb.toString());
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : Parameter Logging
	 *
	 *      @param String paramNm
	 *      @param Object obj
	 *      @return void
	*/
	public void printParam(String paramNm, Object obj) 
	{
		if (obj == null) 
		{
			return; 
		}

		try 
		{
			int pLen1 = 0;
			int pLen2 = 0;
			StringBuffer sb = new StringBuffer("\n▩▩ " + paramNm + " ▩▩");

			if (obj instanceof JDTORecord) 
			{
				JDTORecord param = (JDTORecord)obj;
				Iterator itr = param.iterateName();

				String key = "";
				StringBuffer sb1 = new StringBuffer();
				StringBuffer sb2 = new StringBuffer();
				
				while (itr.hasNext()) 
				{
					key = (String)itr.next();
					Object obj2 = param.getField(key);

					if (obj2 instanceof JDTORecord) 
					{
						JDTORecord param2 = (JDTORecord)obj2;
						sb2 = sb2.append("\n▩ " + key + " : " + param2.toString());
					} 
					else if (obj2 instanceof JDTORecordSet) 
					{
						JDTORecordSet param2 = (JDTORecordSet)obj2;
						pLen2 = param2.size();
						
						for (int jj = 0; jj < pLen2; jj++) 
						{
							sb2 = sb2.append("\n▩ " + key + "-" + formatMaxNo(jj, pLen2) + " : " + param2.getRecord(jj).toString());
						}
					} 
					else 
					{
						if (sb1.length() > 0) 
						{ 
							sb1 = sb1.append(", "); 
						}
						
						sb1 = sb1.append(key + "=" + obj2.toString());
					}
				}

				if (sb1.length() > 0) 
				{
					sb = sb.append("\n▩ {");
					sb = sb.append(sb1);
					sb = sb.append("}");
				}
				
				if (sb2.length() > 0) 
				{
					sb = sb.append(sb2);
				}
			} 
			else if (obj instanceof JDTORecord[]) 
			{
				JDTORecord[] param = (JDTORecord[])obj;
				pLen1 = param.length;
				
				for (int ii = 0; ii < pLen1; ii++) 
				{
					sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : " + param[ii].toString());
				}	
			} 
			else if (obj instanceof JDTORecordSet) 
			{
				JDTORecordSet param = (JDTORecordSet)obj;
				pLen1 = param.size();
				
				for (int ii = 0; ii < pLen1; ii++) 
				{
					sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : " + param.getRecord(ii).toString());
				}
			} 
			else if (obj instanceof Vector) 
			{
				Vector param = (Vector)obj;
				pLen1 = param.size();
				
				for (int ii = 0; ii < pLen1; ii++) 
				{
					Object obj2 = param.get(ii);
					
					if (obj2 instanceof JDTORecord) 
					{
						JDTORecord param2 = (JDTORecord)obj2;
						sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : " + param2.toString());
					} 
					else if (obj2 instanceof JDTORecordSet) 
					{
						JDTORecordSet param2 = (JDTORecordSet)obj2;
						pLen2 = param2.size();
						
						for (int jj = 0; jj < pLen2; jj++) 
						{
							sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + "-" + formatMaxNo(jj, pLen2) + " : " + param2.getRecord(jj).toString());
						}
					} 
					else 
					{
						sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : " + obj.toString());
					}
				}
			} 
			else if (obj instanceof Object[]) 
			{
				Object[] param = (Object[])obj;
				pLen1 = param.length;
				sb = sb.append("\n▩ {");
				
				for (int ii = 0; ii < pLen1; ii++) 
				{
					if (ii > 0) { sb = sb.append(", "); }
					sb = sb.append(ii + "=" + param[ii].toString());
				}
				
				sb = sb.append("}");
			} 
			else 
			{
				sb = sb.append("\n▩ " + obj.toString());
			}

			sb = sb.append("\n▩▩ " + paramNm + " ▩▩");

			logger.println(LogLevel.DEBUG, sb.toString());
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : Parameter Logging
	 *
	 *      @param String paramNm
	 *      @param Object obj
	 *      @param String prnItm
	 *      @return void
	*/
	public void printParam(String paramNm, Object obj, String prnItm) 
	{
		if (obj == null) { return; }

		try 
		{
			if
			(
				prnItm == null || "".equals(prnItm) ||
				!(
					obj instanceof JDTORecord		||
					obj instanceof JDTORecord[]		||
					obj instanceof JDTORecordSet	||
					obj instanceof Vector
				  )
			) 
			{
				printParam(paramNm, obj);
				return;
			}

			StringTokenizer st = new StringTokenizer(prnItm, ";");
			int itmCnt = st.countTokens();
			String[] arrItm = new String[itmCnt];
			
			for (int ii = 0; ii < itmCnt; ii++) 
			{
				arrItm[ii] = st.nextToken();
			}

			int pLen1 = 0;
			
			StringBuffer sb = new StringBuffer("\n▩▩ " + paramNm + " ▩▩");
			
			if (obj instanceof JDTORecord) 
			{
				JDTORecord param = (JDTORecord)obj;
				Iterator itr = param.iterateName();

				String key = "";
				StringBuffer sb1 = new StringBuffer();
				StringBuffer sb2 = new StringBuffer();
				
				while (itr.hasNext()) 
				{
					key = (String)itr.next();
					Object obj2 = param.getField(key);

					if (obj2 instanceof JDTORecordSet) 
					{
						sb2 = sb2.append(this.getParamJs((JDTORecordSet)obj2, arrItm, key, false));
					} 
					else if (obj2 instanceof JDTORecord) 
					{
						sb2 = sb2.append(this.getParamJr((JDTORecord)obj2, arrItm, key));
					} 
					else  
					{
						if (sb1.length() > 0) 
						{ 
							sb1 = sb1.append(", "); 
						}
						
						sb1 = sb1.append(key + "=" + obj2.toString());
					}
				}

				if (sb1.length() > 0) 
				{
					sb = sb.append("\n▩ {");
					sb = sb.append(sb1);
					sb = sb.append("}");
				}
				
				if (sb2.length() > 0) 
				{
					sb = sb.append(sb2);
				}
			} 
			else if (obj instanceof JDTORecord[]) 
			{
				sb = sb.append(getParamJa((JDTORecord[])obj, arrItm, ""));
			} 
			else if (obj instanceof JDTORecordSet) 
			{
				sb = sb.append(getParamJs((JDTORecordSet)obj, arrItm, "", true));
			} 
			else if (obj instanceof Vector) 
			{
				Vector param = (Vector)obj;
				pLen1 = param.size();
				
				for (int ii = 0; ii < pLen1; ii++) 
				{
					Object obj2 = param.get(ii);
					if (obj2 instanceof JDTORecordSet) 
					{
						if (ii == 0) 
						{
							sb = sb.append(this.getParamJs((JDTORecordSet)obj2, arrItm, formatMaxNo(ii, pLen1), true));
						} 
						else 
						{
							sb = sb.append(this.getParamJs((JDTORecordSet)obj2, arrItm, formatMaxNo(ii, pLen1), false));
						}
					} 
					else if (obj2 instanceof JDTORecord) 
					{
						sb = sb.append(this.getParamJr((JDTORecord)obj2, arrItm, formatMaxNo(ii, pLen1)));
					} 
					else 
					{
						sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : " + obj.toString());
					}
				}
			}

			sb = sb.append("\n▩▩ " + paramNm + " ▩▩");

			logger.println(LogLevel.DEBUG, sb.toString());
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : Parameter Logging
	 *
	 *      @param String paramNm
	 *      @param Object obj
	 *      @param String prnItm
	 *      @return void
	*/
	public void printParam(String paramNm, Object[][] obj) 
	{
		if (obj == null) 
		{ 
			return; 
		}

		try 
		{
			String itmVal = "";
			int rowCnt = obj.length;
			int itmCnt = obj[0].length;
			int itmLen = 0;
			int[] arrLen = new int[itmCnt];
			
			for (int jj = 0; jj < itmCnt; jj++) 
			{
				arrLen[jj] = 4;
			}
			
			for (int ii = 0; ii < rowCnt; ii++) 
			{
				for (int jj = 0; jj < itmCnt; jj++) 
				{
					itmVal = obj[ii][jj].toString();
					
					if (itmVal != null && !"".equals(itmVal)) 
					{
						itmLen = itmVal.getBytes().length;
						
						if (itmLen > arrLen[jj]) 
						{
							arrLen[jj] = itmLen;
						}
					}
				}
			}

			StringBuffer sb = new StringBuffer("\n▩▩ " + paramNm + " ▩▩");

			sb = sb.append("\n▩ ----- ");

			for (int jj = 0; jj < itmCnt; jj++) 
			{
				sb = sb.append(this.getRPad(String.valueOf(jj), arrLen[jj], "-") + " ");
			}
			
			for (int ii = 0; ii < rowCnt; ii++) 
			{
				sb = sb.append("\n▩ " + this.format(ii, 3) + " : ");
				
				for (int jj = 0; jj < itmCnt; jj++) 
				{
					itmVal = obj[ii][jj].toString();
					
					if (itmVal == null || "".equals(itmVal)) 
					{
						sb = sb.append(this.getRPad(" ", arrLen[jj] + 1, " "));
					} 
					else 
					{
						sb = sb.append(this.getRPad(itmVal, arrLen[jj] + 1, " "));
					}
				}
			}

			sb = sb.append("\n▩▩ " + paramNm + " ▩▩");

			logger.println(LogLevel.DEBUG, sb.toString());
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : JDTORecordSet Parameter Logging을 위한 String 만들기
	 *
	 *      @param JDTORecordSet jsParam
	 *      @param String[] arrItm
	 *      @param String prefix
	 *      @param boolean titleYn
	 *      @return StringBuffer
	*/
	public StringBuffer getParamJs(JDTORecordSet jsParam, String[] arrItm, String prefix, boolean titleYn) 
	{
		StringBuffer sb = new StringBuffer();

		try 
		{
			int itmLen = 0; //항목값길이
			int rowCnt = jsParam.size();
			String itmVal = "";
			JDTORecord jrRow = null;

			int itmCnt = arrItm.length;
			int[] arrLen = new int[itmCnt];

			for (int jj = 0; jj < itmCnt; jj++) 
			{
				arrLen[jj] = arrItm[jj].getBytes().length;
			}
			
			for (int ii = 0; ii < rowCnt; ii++) 
			{
				jrRow = jsParam.getRecord(ii);
				
				for (int jj = 0; jj < itmCnt; jj++) 
				{
					itmVal = jrRow.getFieldString(arrItm[jj]);
					
					if (itmVal != null && !"".equals(itmVal)) 
					{
						itmLen = itmVal.getBytes().length;
						
						if (itmLen > arrLen[jj]) 
						{
							arrLen[jj] = itmLen;
						}
					}
				}
			}

			//접두사길이 결정
			itmLen = 5;
			String preStr = "";
			
			if (prefix != null && !"".equals(prefix)) 
			{
				preStr = prefix + "-";
				int len1 = preStr.getBytes().length;
				int len2 = (String.valueOf(rowCnt)).length();
				
				if (len1 + len2 > 5)
				{
					sb = sb.append("\n▩ " + prefix);
					preStr = "";
				} 
				else if (len1 + len2 < 5) 
				{
					itmLen = 5 - len1;
				}
			}

			if (titleYn) 
			{
				sb = sb.append("\n▩ Title : ");
	
				for (int jj = 0; jj < itmCnt; jj++) 
				{
					sb = sb.append(this.getRPad(arrItm[jj], arrLen[jj] + 1, " "));
				}
			}
			
			for (int ii = 0; ii < rowCnt; ii++) 
			{
				jrRow = jsParam.getRecord(ii);
				sb = sb.append("\n▩ " + preStr + format(ii, itmLen) + " : ");
				
				for (int jj = 0; jj < itmCnt; jj++) 
				{
					itmVal = jrRow.getFieldString(arrItm[jj]);
					
					if (itmVal == null || "".equals(itmVal)) 
					{
						sb = sb.append(this.getRPad(" ", arrLen[jj] + 1, " "));
					} 
					else 
					{
						sb = sb.append(this.getRPad(jrRow.getFieldString(arrItm[jj]), arrLen[jj] + 1, " "));
					}
				}
			}
			
			return sb;
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
			return sb;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : JDTORecordSet Parameter Logging을 위한 String 만들기
	 *
	 *      @param JDTORecord[] jaParam
	 *      @param String[] arrItm
	 *      @param String prefix
	 *      @return StringBuffer
	*/
	public StringBuffer getParamJa(JDTORecord[] jaParam, String[] arrItm, String prefix) 
	{
		StringBuffer sb = new StringBuffer();

		try 
		{
			int itmLen = 0; //항목값길이
			int rowCnt = jaParam.length;
			String itmVal = "";
			JDTORecord jrRow = null;

			int itmCnt = arrItm.length;
			int[] arrLen = new int[itmCnt];
			
			for (int jj = 0; jj < itmCnt; jj++) 
			{
				arrLen[jj] = arrItm[jj].getBytes().length;
			}
			
			for (int ii = 0; ii < rowCnt; ii++) 
			{
				jrRow = jaParam[ii];
				
				for (int jj = 0; jj < itmCnt; jj++) 
				{
					itmVal = jrRow.getFieldString(arrItm[jj]);
					
					if (itmVal != null && !"".equals(itmVal)) 
					{
						itmLen = itmVal.getBytes().length;
						
						if (itmLen > arrLen[jj]) 
						{
							arrLen[jj] = itmLen;
						}
					}
				}
			}

			//접두사길이 결정
			itmLen = 5;
			String preStr = "";
			if (prefix != null && !"".equals(prefix))
			{
				preStr = prefix + "-";
				int len1 = preStr.getBytes().length;
				int len2 = (String.valueOf(rowCnt)).length();
				
				if (len1 + len2 > 5) 
				{
					sb = sb.append("\n▩ " + prefix);
					preStr = "";
				} 
				else if (len1 + len2 < 5) 
				{
					itmLen = 5 - len1;
				}
			}

			sb = sb.append("\n▩ Title : ");

			for (int jj = 0; jj < itmCnt; jj++) 
			{
				sb = sb.append(this.getRPad(arrItm[jj], arrLen[jj] + 1, " "));
			}
			
			for (int ii = 0; ii < rowCnt; ii++) 
			{
				jrRow = jaParam[ii];
				sb = sb.append("\n▩ " + preStr + format(ii, itmLen) + " : ");
				
				for (int jj = 0; jj < itmCnt; jj++) 
				{
					itmVal = jrRow.getFieldString(arrItm[jj]);
					
					if (itmVal == null || "".equals(itmVal)) 
					{
						sb = sb.append(this.getRPad(" ", arrLen[jj] + 1, " "));
					}
					else 
					{
						sb = sb.append(this.getRPad(jrRow.getFieldString(arrItm[jj]), arrLen[jj] + 1, " "));
					}
				}
			}
			
			return sb;
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
			return sb;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : JDTORecord Parameter Logging을 위한 String 만들기
	 *
	 *      @param JDTORecord[] jaParam
	 *      @param String[] arrItm
	 *      @param String prefix
	 *      @return StringBuffer
	*/
	public StringBuffer getParamJr(JDTORecord jrParam, String[] arrItm, String prefix) 
	{
		StringBuffer sb = new StringBuffer();

		try 
		{
			int itmCnt = arrItm.length;

			sb = sb.append("\n▩ " + prefix + " : {");

			for (int jj = 0; jj < itmCnt; jj++) 
			{
				sb = sb.append(arrItm[jj] + "=" + trim(jrParam.getFieldString(arrItm[jj])));
				if (jj < itmCnt - 1) { sb = sb.append(", "); }
			}

			sb = sb.append("}");
			
			return sb;
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
			return sb;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 수신 전문의 MSG_ID를 추출
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return String
	*/
	public String getMsgId(JDTORecord rcvMsg) 
	{
		String msgId = ""; //인터페이스ID

		try 
		{
			//JMS일 경우는 JMS_TC_CD
			msgId = trim(rcvMsg.getFieldString("JMS_TC_CD"));

			//EAI일 경우는 MSG_ID
			if (isEmpty(msgId)) 
			{
				msgId = trim(rcvMsg.getFieldString("MSG_ID"));
			}

			//기타(출하관리 등)일 경우는 TC_CODE
			if (isEmpty(msgId)) 
			{
				msgId = trim(rcvMsg.getFieldString("TC_CODE"));
			}
			
			if (isEmpty(msgId)) 
			{
				msgId = trim(rcvMsg.getFieldString("TcCode"));
			}
			
			if (isEmpty(msgId)) 
			{
				msgId = trim(rcvMsg.getFieldString("tcCode"));
			}
			// PIDEV
			// rabbitMq
			if (isEmpty(msgId)) 
			{
				msgId = trim(rcvMsg.getFieldString("MQ_TC_CD"));
			}

			return msgId;
		} 
		catch (Exception e) 
		{
			return msgId;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 전송할 전문을 추가
	 *
	 *      @param JDTORecord jrExt
	 *      @param JDTORecordSet jsAdd
	 *      @return JDTORecord
	*/
	public JDTORecord addSndData(JDTORecord jrExt, JDTORecordSet jsAdd) 
	{
		try 
		{
			//추가할 전문이 없으면 기존 그대로
			if (isEmpty(jsAdd)) 
			{
				return jrExt;
			}

			//Return Data
			JDTORecordSet rtnData = JDTORecordFactory.getInstance().createRecordSet("");

			//기존 전문이 있으면 기존 먼저 추가
			if (!isEmpty(jrExt)) 
			{
				JDTORecordSet extData = (JDTORecordSet)jrExt.getField("SEND_DATA");

				if (!isEmpty(extData)) 
				{
					rtnData.addAll(extData);
				}
			}

			//추가할 전문 추가
			rtnData.addAll(jsAdd);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			jrRtn.addField("SEND_DATA", rtnData);

			return jrRtn;
		} 
		catch (Exception e) 
		{
			return jrExt;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 전송할 전문을 추가
	 *
	 *      @param JDTORecord jrExt
	 *      @param JDTORecord jrAdd
	 *      @return JDTORecord
	*/
	public JDTORecord addSndData(JDTORecord jrExt, JDTORecord jrAdd) 
	{
		try 
		{
			//추가할 전문이 없으면 기존 그대로
			if (isEmpty(jrAdd)) 
			{
				return jrExt;
			}

			JDTORecordSet addData = null;

			//I/F ID를 먼저 Check
			String msgId = this.getMsgId(jrAdd);

			if (!isEmpty(msgId)) 
			{
				//I/F ID가 존재할 경우는 전문 1건 추가
				addData = JDTORecordFactory.getInstance().createRecordSet("");
				addData.addRecord(jrAdd);
			} 
			else 
			{
				//SEND_DATA로 있을 경우
				addData = (JDTORecordSet)jrAdd.getField("SEND_DATA");
			}

			return addSndData(jrExt, addData);
		} 
		catch (Exception e) 
		{
			return jrExt;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 전송할 전문을 추가
	 *
	 *      @param JDTORecordSet jsAdd
	 *      @return JDTORecord
	*/
	public JDTORecord addSndData(JDTORecordSet jsAdd) 
	{
		try 
		{
			return addSndData(null, jsAdd);
		} 
		catch (Exception e) 
		{
			return null;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 전송할 전문을 추가
	 *
	 *      @param JDTORecord jrAdd
	 *      @return JDTORecord
	*/
	public JDTORecord addSndData(JDTORecord jrAdd) 
	{
		try 
		{
			return addSndData(null, jrAdd);
		} 
		catch (Exception e) 
		{
			return null;
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : Grid에서 값 추출하기
	 *
	 *      @param JDTORecord jrAdd
	 *      @return JDTORecord
	*/
	public String getValue(GridData gdReq, String headerNm, int ii) 
	{
		try 
		{
			String rtnValue;
			
			if (gdReq.getHeader(headerNm).getDataType().equals(OperateGridData.t_combo)) 
			{
				rtnValue = this.nvl(this.trim(gdReq.getHeader(headerNm).getComboHiddenValues()[gdReq.getHeader(headerNm).getSelectedIndex(ii)]),"");
			} 
			else if (gdReq.getHeader(headerNm).getDataType().equals(OperateGridData.t_number)) 
			{	
				rtnValue = this.nvl(this.trim(gdReq.getHeader(headerNm).getValue(ii)),"0");
			} 
			else 
			{
				rtnValue = this.nvl(this.trim(gdReq.getHeader(headerNm).getValue(ii)),"");
			}
			
			return rtnValue; 
		} 
		catch (Exception e) 
		{
			return "";
		}
	}
	/**
	 *      [A] 오퍼레이션명 : stringPlusInt 
	 * 
	 * @param  String szPara         // 문자 값
	 *         int intPara           // 숫자 값
	 * @return String			     // 계산결과 문자열
	 */
	public String stringPlusInt(String szPara, int intPara) 
	{
		String szRtnVal = null;
		int intTemp = 0;
		
		try
		{
			intTemp = Integer.parseInt(szPara) + intPara;
		}
		catch(Exception e)
		{
			
		}
		if (intTemp < 10)
		{
			szRtnVal = "0" + intTemp;
		}			
		else if (intTemp > 9 && intTemp < 100)
		{
			szRtnVal = "" + intTemp;
		}
		
		return szRtnVal;
	} // end of stringPlusInt
	
	/**
	 *      [A] 오퍼레이션명 : paraRecChkNullLong
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return long			     // 해당 필드의 데이터
	 * @throws JDTOException 
	 */
	public long paraRecChkNullLong(JDTORecord recPara, String szFieldName) throws JDTOException 
	{
		long lngRtnVal;
		
		if (recPara.getFieldString(szFieldName) == null)
		{
			lngRtnVal = 0;
		}
		else 
		{
			if ("".equals(recPara.getFieldString(szFieldName).trim()))
			{
				lngRtnVal = 0;
			}
			else
			{
				lngRtnVal = Long.parseLong(recPara.getFieldString(szFieldName));
			}
		}
		
		return lngRtnVal;
	} // end of paraRecChkNullLong
	
	/**
	 *      [A] 오퍼레이션명 : paraRecChkNull_2 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return String			     // 해당 필드의 데이터
	 * @throws JDTOException 
	 */
	public double paraRecChkNullDouble(JDTORecord recPara, String szFieldName) throws JDTOException 
	{
		double dlRtnVal = 0;
		
		if (recPara.getField(szFieldName) == null)
		{
			dlRtnVal = 0;
		}
		else
		{
			if ("".equals(recPara.getFieldString(szFieldName).trim()))
			{
				dlRtnVal = 0;
			}
			else
			{
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
	public int paraRecChkNullInt(JDTORecord recPara, String szFieldName) throws JDTOException 
	{
		int intRtnVal;
		
		if (recPara.getFieldString(szFieldName) == null)
		{
			intRtnVal = 0;
		}
		else 
		{
			if ("".equals(recPara.getFieldString(szFieldName).trim()))
			{
				intRtnVal = 0;
			}
			else
			{
				intRtnVal = recPara.getFieldInt(szFieldName);
			}
		}
		
		return intRtnVal;
	} // end of paraRecChkNull
	
	/**
	 *      [A] 오퍼레이션명 : paraRecChkNull 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return String			     // 해당 필드의 데이터
	 * @throws JDTOException 
	 */
	public String paraRecChkNull(JDTORecord recPara, String szFieldName) throws JDTOException 
	{
		String szRtnVal = null;
		
		if (recPara.getField(szFieldName) == null)
		{
			szRtnVal = "";
		}
		else
		{
			szRtnVal = recPara.getFieldString(szFieldName).trim();				//임춘수 2009.04.24 수정 trim() 추가
		}
		
		return szRtnVal;
	}
	
	/**
	 *      [A] 오퍼레이션명 : stringPlusInt2 
	 * 
	 * @param  String szPara         // 문자 값
	 *         int intPara           // 숫자 값
	 * @return String			     // 계산결과 문자열
	 */
	public String stringPlusInt2(String szPara, int intPara) 
	{
		String szRtnVal = null;
		int intTemp = 0;
		
		try
		{
			intTemp = Integer.parseInt(szPara) + intPara;
		}
		catch(Exception e)
		{
			
		}
		
		if (intTemp < 10)
		{
			szRtnVal = "0" + intTemp;
		}
		else if (intTemp > 9 && intTemp < 100)
		{
			szRtnVal = "" + intTemp;
		}
		
		return szRtnVal;
	} // end of stringPlusInt2	
	

	//////////////////////////////////////////////////////////////////////////////////////	
	// 추가
	/////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * 두개의 JDTORecord를 하나의 JDTORecord로 합치는 method
	 * 
	 * @param map :
	 *            JDTORecord, JDTORecord
	 * 
	 * @return : JDTORecord
	 */
	public JDTORecord mixJDTORecord(JDTORecord a, JDTORecord b) 
	{
		try 
		{
			String key = "";

			Map mMap = b.getMap();
			Set set = mMap.keySet();
			Object[] hmKeys = set.toArray();
			
			for (int i = 0; i < hmKeys.length; i++) 
			{
				key = (String) hmKeys[i];
				a.setField(key, (String) mMap.get(key));
			}
		} 
		catch (Exception e) 
		{

		}
		
		return a;
	}
	
	/**
	 * LINE IN 작업인지를 판단한다.
     * @param  String
     * @return boolean
     * @throws 
     */	
	public static boolean isLineInWork(String sSchCode)
	{
	   boolean isTrue = false;
	   
	   if
	   (
		  (
		      "DC".equals(sSchCode.substring(2,4)) || 
		      "FE".equals(sSchCode.substring(2,4)) ||
		      "KD".equals(sSchCode.substring(2,4)) ||
		      "HS".equals(sSchCode.substring(2,4)) ||
		      "KE".equals(sSchCode.substring(2,4))
		  ) &&
		  "U".equals(sSchCode.substring(6,7))
	   ) 
	   {
//	      YfConstant.NEW_SCH_WORK_KIND_CKLI.equals(sSchCode)||  // SPM 보급
//	   	  YfConstant.NEW_SCH_WORK_KIND_CKTI.equals(sSchCode)||  // SPM Take In
//	   	  YfConstant.NEW_SCH_WORK_KIND_EQLI.equals(sSchCode)||  // EQL 보급
//	   	  YfConstant.NEW_SCH_WORK_KIND_EQTI.equals(sSchCode)||  // EQL Take In
//	   	  YfConstant.NEW_SCH_WORK_KIND_CFTI.equals(sSchCode)||  // HFL Take In
//	   	  YfConstant.NEW_SCH_WORK_KIND_CFLI.equals(sSchCode)||  // HFL 보급
//	   	  YfConstant.NEW_SCH_WORK_KIND_SSLI.equals(sSchCode)||    // SCARFING 보급
//	   	  YfConstant.NEW_SCH_WORK_KIND_CNLI.equals(sSchCode)||	// #2 SPM 보급
//	   	  YfConstant.NEW_SCH_WORK_KIND_CNTI.equals(sSchCode)||	// #2 SPM Take In
//	   	  YfConstant.NEW_SCH_WORK_KIND_CHLI.equals(sSchCode)||	// #2 HFL 보급
//	   	  YfConstant.NEW_SCH_WORK_KIND_CFSI.equals(sSchCode)	  // HFL 결속대 보급
//	   	 ){
	   		isTrue = true; 	
	   	} 
		return isTrue;
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
		
		if("P".equals(sType))
		{
			lVal = lVal + 1;
		}
		else if("M".equals(sType))
		{
			lVal = lVal - 1;
		}
		
		return df.format(lVal);
	}	
	
    /**
     * 작업근조를 가져온다.
     */
    public static String getWorkDuty() 
    {    
        int[] date 		 = getIntYMDHMS();   
        String workGroup = "0";
        
		if(date[3] >= 7 && date[3] <= 15)
		{
		    workGroup = "1";
		}
		else if(date[3] >= 16 && date[3] <= 23)
		{
		    workGroup = "2";
		}
		else 
		{
		    workGroup = "3";
		}
		
		return workGroup;
    }
    
    public static int[] getIntYMDHMS() 
    {
        String now = getStringYMDHMS();
        return new int[]
        {
        	Integer.parseInt(now.substring(0,4)),
        	Integer.parseInt(now.substring(4,6)),
        	Integer.parseInt(now.substring(6,8)),
        	Integer.parseInt(now.substring(8,10)),
        	Integer.parseInt(now.substring(10,12)),
        	Integer.parseInt(now.substring(12,14))
        };
    }  
    
    /**
     * 작업근조를 가져온다.
     */
    public static String getWorkParty() 
    {    
        CommonUtil comUtil = new CommonUtil();
		int[] date 		= getIntYMDHMS();
	    String steam ="";	
	    steam = comUtil.getTeam(date[0],date[1],date[2],date[3]);
		
	    if("".equals(steam))
	    {
	    	steam ="E";
	    }
	    
        return steam ;
    }
    
	public boolean isNumeric(String str)
	{  
		try
		{  
			double d = Double.parseDouble(str);  

		}
		catch(NumberFormatException nfe)
		{  
			return false;  
		}
		
		return true;  
	}
	
	//SJH	
	/**
	 * 대차작업지정기준조회1
	 * @param szYD_EQP_ID			대차호기
	 * @return String[]
	 */
	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr) 
	{
		return setWiseGridCombo(obj, hTitle, comboStrArr, 1, "N");
	}
	
	/**
 	 * 대차작업지정기준조회_코일1
	 * @param szYD_EQP_ID			대차호기
	 * @return String[]
    */
	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr, int cdVal, String headTextYn) 
	{
		String comboStr = "";
		
		if(comboStrArr != null) 
		{
			if("Y".equals(headTextYn)) 
			{
				comboStr = obj + ".AddComboListValue('" + hTitle + "', '', '');";
			}
			
			if(cdVal == 0 || cdVal == 1) 
			{
				for(int ii=0; ii < comboStrArr[0].length; ii++) 
				{
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + comboStrArr[cdVal][ii] + "', '" + comboStrArr[0][ii] + "');";
				}
			}
			else if(cdVal == 2) 
			{
				//YD에 쓸수 있게 코드/코드명 형식으로 출력				
				for(int ii=0; ii < comboStrArr[0].length; ii++) 
				{
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + 
									"[" +comboStrArr[0][ii] + "] " + comboStrArr[1][ii] + "', '" + comboStrArr[0][ii] + "');";
				} 
			}
			else 
			{
				for(int ii=0; ii < comboStrArr[0].length; ii++) 
				{
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + 
										comboStrArr[0][ii] + " (" + comboStrArr[1][ii] + ")', '" + comboStrArr[0][ii] + "');";
				}
			}
		}
		
		return comboStr;
	}
	
	/**
	 * 오퍼레이션명 : putLog
	 *
	 * @param String szClassName	// Logging 요청 Class name
	 *        String szMethodName 	// Logging 요청 Method Name
	 *        String szLogMsg		// Logging Message
	 * @return
	 * @throws DAOException, JDTOException
	 */
	public void putLog(String szClassName, String szMethodName, String szLogMsg, int nLogLevel)  
	{

		String szMsg="";
		String strCurDate = this.getCurDate("yyyy-MM-dd HH:mm:ss:SSS");

		szMsg =szClassName + "::" + szMethodName +"() " + "\n\t"+szLogMsg;

		try
		{
			if(bDebugFlag)
			{
				switch(nLogLevel)
				{
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
				}// end of switch(nLogLevel) 
				
				// 2012-11-14 (김현우) 표준화 지적사항 반영으로 주석처리
				//System.out.println("\n---<"+ strCurDate +">-----------------------------------");
				//System.out.println(szMsg);
			} 
			else 
			{
				// Message Logging
				switch(nLogLevel)
				{
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
		}
		catch (Exception e)
		{
			szMsg =szClassName + "::" + szMethodName +"() " + "\n\t"+szMethodName+" Exception Error : "+e.getLocalizedMessage();

			// 2012-11-14 (김현우) 표준화 지적사항 반영으로 주석처리
			//if(bDebugFlag)
			//	System.out.println(szMsg);
			//else
				logger.println(LogLevel.ERROR, this, szMsg);
		} // end of try-catch()
	} // end of putLog();
	
    /**
     * Object Data Default 값을넣어주는 Function 
     * PO
     * @param  Object , String  
     * @return String 
     * @throws Exception
     */	
	public String setDataDefault (Object sObj, String sDef) throws Exception 
	{	
		if ( sObj ==null || "".equals(sObj.toString()))  
		{			
			return sDef;			
		}
		
		return sObj.toString();
	} // end of setDataDefault
	
	/**
	 * GridData의 입력/수정/삭제  정보를 JDTORecord [] 으로 변환하여 리턴한다.
	 * (GridData의 입력/수정/삭제 항목을 가져오기위해 사용)
	 * @param inDto
	 * @return
	 */
	public JDTORecord [] genJDTORecordSet(GridData inDto) throws Exception
	{
		boolean isUpperKey = false;		
		YDDataUtil yDDataUtil = new YDDataUtil();
		String szUserId = "";
		String szCRUD ="";
		String szydEqpId ="";
		
		if(inDto.getParam("set_upper") != null && "true".equals(inDto.getParam("set_upper")))
		{
			isUpperKey = true;
		}
		
		szUserId= yDDataUtil.setDataDefault(inDto.getParam("YD_USER_ID"), "");
		szydEqpId= yDDataUtil.setDataDefault(inDto.getParam("YD_EQP_ID"), "");

		GridHeader [] ghs = inDto.getHeaders();
		int hCount = ghs.length;
		int rCount = 0;
		if(hCount > 0)
		{
			rCount = ghs[0].getRowCount();
		}
		
		JDTORecord [] jdtoAl = new JDTORecord[rCount];
		logger.println(LogLevel.DEBUG,   "========  GridData -> JDTORecord []  ================");
		logger.println(LogLevel.DEBUG,   "헤더갯수:"+hCount);
		logger.println(LogLevel.DEBUG,   "Row갯수:"+rCount);

		logger.println(LogLevel.DEBUG,   "========== GridData inDto ROW DATA ===========");
		
		for(int i=0;i<rCount;i++)
		{
			GridHeader chHeader = inDto.getHeader("CHECK");
			boolean Checked = (Integer.parseInt(chHeader.getValue(i)) == 1)? true:false;
			
			if(Checked)
			{
				JDTORecord jDto = JDTORecordFactory.getInstance().create();
				
				for(int j=0;j<hCount;j++)
				{
					String key = ghs[j].getID();
					String rValue = "";
					String hValue = "";
					
					if(ghs[j].getDataType().equals(OperateGridData.t_combo))
					{
						int iSelectedIdx = ghs[j].getSelectedIndex(i);
						
						if(iSelectedIdx >= 0)
						{
							if(ghs[j].hasComboList())
							{
								rValue = StringHelper.evl( ghs[j].getComboHiddenValues(ghs[j].getSelectedComboListKey(i))[iSelectedIdx], "");
								hValue = StringHelper.evl( ghs[j].getComboValues(ghs[j].getSelectedComboListKey(i))[iSelectedIdx], "");
							}
							else
							{
								rValue = StringHelper.evl( ghs[j].getComboHiddenValues()[iSelectedIdx], "");
								hValue = StringHelper.evl( ghs[j].getComboValues()[iSelectedIdx], "");							
							}
						}
						else
						{
							rValue = "";
							hValue = "";
						}
					}
					else 
					{
						rValue = StringHelper.evl(ghs[j].getValue(i), "");
						hValue = StringHelper.evl(ghs[j].getHiddenValue(i), "");
					}

					jDto.addField((isUpperKey)?key.toUpperCase():key, rValue);
				}
				//수정자 ,등록자 SETTING
				
				szCRUD =yDDataUtil.setDataDefault(jDto.getField("CRUD"),"");
				
				if("C".equals(szCRUD))
				{
					jDto.setField("REGISTER",szUserId);
				}
				else if("U".equals(szCRUD))
				{
					jDto.setField("MODIFIER",szUserId);
				}
				else 
				{
					
				}
				jDto.setField("YD_USER_ID",szUserId);
				
				if(!"".equals(szydEqpId))
				{
					jDto.setField("YD_EQP_ID",szydEqpId);
				}
				  
				jdtoAl[i] = jDto;
			}
		}
		
		logger.println(LogLevel.DEBUG,   "========== JDTORecord START ===========");
		for(int ss=0;ss<jdtoAl.length;ss++)
		{
			logger.println(LogLevel.DEBUG,   jdtoAl[ss].toString());
		}
		logger.println(LogLevel.DEBUG,   "========== JDTORecord END ===========");

		return jdtoAl;
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

		try
		{
			if(strOrg == null || "".equals(strOrg.trim()))
			{
				return addLeftStr("", nTotal, (char)ch);
			}

			int nIdx = strOrg.indexOf(".");
			if(nIdx <= 0)
			{
				strTemp1 = this.addLeftStr(strOrg.trim(), nJisu, (char)ch);
				strTemp2 = this.addRightStr("0", nSosu, (char)ch);
				
				if("".equals(strTemp1.trim()))
				{
					return null;
				}
			}
			else 
			{
				String[] strSplit = strOrg.trim().split("\\.");

				strTemp1 = this.addLeftStr(strSplit[0], nJisu, (char)ch);
				strTemp2 = this.addRightStr(strSplit[1], nSosu, (char)ch);

				if("".equals(strTemp1) || "".equals(strTemp2))
				{
					return null;
				}
			}
		}
		catch(Exception e)
		{
			this.putLog(szSessionName, szMethodName, "FloatLRPAD() : " + e.toString() + " : " + e.getMessage(), 4);
		}
		finally
		{
			
		}

		return 	strTemp1 + strTemp2;
	}

	/**
	 * 문자열 좌측을 지정한 값으로 채워넣음
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

		try
		{
			templen = len - str.getBytes().length;
			if(templen >= 0)
			{
				for(int i=0; i<templen; i++)
				{
					str = pad + str;
				}
				
				result = str;
			}
		}
		catch(Exception e)
		{
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

		try
		{
			templen = len - str.getBytes().length;
			
			if(templen >= 0)
			{
				for(int i=0; i<templen; i++)
				{
					str = str + pad;
				}
				
				result = str;
			}
		}
		catch(Exception e)
		{
			this.putLog(szSessionName, szMethodName, "FloatLRPAD() : " + e.toString() + " : " + e.getMessage(), 4);
		}

		return result;
	}	
	/**
	 * 오퍼레이션명 : Get TC Code
	 *
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public String getTcCode(JDTORecord inRecord)
	{
		String szMsg="";
		String szMethodName="getTcCode";
		String szRcvTcCode="";

		try
		{
			// 내부인터페이스(JMS Queue)
			szRcvTcCode = inRecord.getFieldString("JMS_TC_CD");

			if(szRcvTcCode == null)
			{
				szRcvTcCode=inRecord.getFieldString("TcCode");
			}
			
			// 외부인터페이스(L2 EAI)
			if(szRcvTcCode == null)
			{
				szRcvTcCode=inRecord.getFieldString("MSG_ID");
			}

			// 외부인터페이스(RemoteEAI)
			if(szRcvTcCode == null)
			{
				szRcvTcCode=inRecord.getFieldString("TC_CODE");
			}
			
			// PIDEV
			// rabbitMq
			if(szRcvTcCode == null)
			{
				szRcvTcCode=inRecord.getFieldString("MQ_TC_CD");
			}			

			if(szRcvTcCode == null)
			{
				szRcvTcCode="";
			}	// end if

			szRcvTcCode=szRcvTcCode.trim();
			szRcvTcCode=szRcvTcCode.toUpperCase();
		}
		catch(Exception e)
		{
			szMsg=szMethodName+" Exception Error : "+e.getMessage();
			this.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return null;
		} // end of try-catch

		return szRcvTcCode;
	} // end of getTcCode();
	
	/**
	 * 야드목표행선지구분를 지정한다.
     *
     * @param  String	sItemGp :	제품구분(S:SLAB, C:COIL ,P: 후판 ) ,JDTORecord inRecord
     *
     * @return String
     * @throws  
     */		
	public String[] getYdAimRtGp(String sItemGp, JDTORecord inRecord)
	{
		// 메세지
		String logId = inRecord.getResultCode();
		String szMsg = null;
		String currProgCd = null;
		String ydAimRtGp = null;
		String sYD_AIM_RT_GP2 = "";
		String sHCR_GP = "";
		String sSKINPASS_YN = "";
		
		// 메소드명
		String szMethodName = "getYdAimRtGp";
		String sNextProc = ""; // 다음공정
		String sPlanProc1 = ""; // 열연계획작업코드1

		String[] rVal = new String[2];

		JDTORecord recEditInRecord = JDTORecordFactory.getInstance().create();

		YfCommDAO commDao = new YfCommDAO();

		try 
		{
			// PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", szMethodName, "APPPI0", "*", "*");
			String pidevProc = "Y";
			
			if("Y".equals(pidevProc)) {
				rVal= this.getYdAimRtGp_PIDEV(sItemGp,inRecord);
				return rVal;
			}
			
			// 전문받아서 szRcvTcCode에 대입
			String szRcvTcCode = this.getTcCode(inRecord);
			String sSTL_NO = this.trim(inRecord.getFieldString("STL_NO"));

//			if ("P".equals(sItemGp))
//			{
//				// 수신한 재료번호로 plate공통
//				// 읽기***************************************************************************************************
//				if (!"".equals(sSTL_NO)) 
//				{
//					recEditInRecord.setField("PLATE_NO", sSTL_NO);
//
//					JDTORecordSet loadPlatecomm = commDao.select(recEditInRecord,"com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getPLATECOMM",logId, szMethodName, "후판공통검색");
//
//					if (loadPlatecomm.size() <= 0) 
//					{
//						szMsg = "plate공통 SELECT Error :: [" + sSTL_NO + "]" + "DO NOT EXIST";
//						this.printLog(logId, szMsg, "SL");
//						return rVal;
//					} 
//					else 
//					{
//						szMsg = sSTL_NO + " :: plate공통 SELECT Success :: [" + loadPlatecomm.size() + "]";
//						this.printLog(logId, szMsg, "SL");
//
//						// 진도코드 존제여부 체크
//						if ("".equals(this.trim(loadPlatecomm.getRecord(0).getFieldString("CURR_PROG_CD")))) 
//						{
//							szMsg = "진도코드가  존재  안 함";
//							this.printLog(logId, szMsg, "SL");
//							return rVal;
//						} 
//						else 
//						{
//							// 진도코드
//							currProgCd = this.trim(loadPlatecomm.getRecord(0).getFieldString("CURR_PROG_CD"));
//						}
//
//					}
//				} 
//				else 
//				{
//					// 진도코드
//					currProgCd = this.trim(inRecord.getFieldString("CURR_PROG_CD"));
//				}
//				
//				currProgCd = this.trim(inRecord.getFieldString("CURR_PROG_CD"));
//				szMsg = "진도코드::" + currProgCd;
//				this.printLog(logId, szMsg, "SL");
//
//				if ("DMYDR006".equals(szRcvTcCode)) 
//				{
//					ydAimRtGp = "K3"; // 출하지시대기
//					currProgCd = "K";
//				} 
//				else if ("DMYDR018".equals(szRcvTcCode)) 
//				{
//					ydAimRtGp = "N3"; // 운송지시대기
//					currProgCd = "N";
//				} 
//				else if ("DMYDR021".equals(szRcvTcCode)) 
//				{
//					ydAimRtGp = "L6"; // 운송상차지시
//					currProgCd = "L";
//				} 
//				else if ("DMYDR031".equals(szRcvTcCode)) 
//				{
//					ydAimRtGp = "M3"; // 출하완료
//					currProgCd = "M";
//				} 
//				else if ("Y".equals(currProgCd)) 
//				{
//					ydAimRtGp = currProgCd + "C"; // 재공충당대기(A후판plate)
//				} 
//				else if ("G".equals(currProgCd)) 
//				{
//					ydAimRtGp = currProgCd + "3"; // 종합판정대기
//				} 
//				else if ("I".equals(currProgCd)) 
//				{
//					ydAimRtGp = currProgCd + "3"; // 반송대기
//				} 
//				else if ("H".equals(currProgCd)) 
//				{
//					ydAimRtGp = currProgCd + "3"; // 입고대기
//				} 
//				else if ("J".equals(currProgCd)) 
//				{
//					ydAimRtGp = currProgCd + "3"; // 반납대기
//				} 
//				else if ("Z".equals(currProgCd)) 
//				{
//					ydAimRtGp = currProgCd + "3"; // 제품충당대기
//				} 
//				else if ("X".equals(currProgCd)) 
//				{
//					ydAimRtGp = currProgCd + "3"; // 경매대상선정
//				} 
//				else if ("K".equals(currProgCd)) 
//				{
//					ydAimRtGp = currProgCd + "3"; // 출하지시대기
//				}
//				// ***************************************************************************************************************************
//			} 
//			else 
			if ("C".equals(sItemGp)) 
			{
				// 수신한 재료번호로 코일공통
				// 읽기***************************************************************************************************
				if (!"".equals(sSTL_NO)) 
				{
					recEditInRecord.setField("STL_NO", sSTL_NO);

					JDTORecordSet loadYdStock = commDao.select(recEditInRecord, getCOILCOMM1,logId, szMethodName, "코일공통검색");

					if (loadYdStock.size() <= 0) 
					{
						szMsg = "코일공통 SELECT Error :: [" + sSTL_NO + "]" + "DO NOT EXIST";
						this.printLog(logId, szMsg, "SL");
						return rVal;
					} 
					else 
					{
						szMsg = inRecord.getFieldString("STL_NO") + " :: 코일공통 SELECT Success :: [" + loadYdStock.size() + "]";
						this.printLog(logId, szMsg, "SL");

						sYD_AIM_RT_GP2 = this.trim(loadYdStock.getRecord(0).getFieldString("YD_AIM_RT_GP2"));
						sHCR_GP = this.trim(loadYdStock.getRecord(0).getFieldString("HCR_GP"));
						sSKINPASS_YN = this.trim(loadYdStock.getRecord(0).getFieldString("SKINPASS_YN"));

						// 진도코드 존제여부 체크
						if ("".equals(this.trim(loadYdStock.getRecord(0).getFieldString("CURR_PROG_CD")))) 
						{
							szMsg = "진도코드가  존재  안 함";
							this.printLog(logId, szMsg, "SL");
							return rVal;
						}
						
						sNextProc = this.trim(loadYdStock.getRecord(0).getFieldString("NEXT_PROC"));
						//sNextProc = this.trim(loadYdStock.getRecord(0).getFieldString("NEXT_PROC"));
						sPlanProc1 = this.trim(loadYdStock.getRecord(0).getFieldString("PLAN_PROC1"));
					}

					// 진도코드
					currProgCd = this.trim(loadYdStock.getRecord(0).getFieldString("CURR_PROG_CD"));
				} 
				else 
				{
					// 진도코드
					currProgCd = this.trim(inRecord.getFieldString("CURR_PROG_CD"));
				}

				szMsg = "진도코드::" + currProgCd;
				this.printLog(logId, szMsg, "SL");

				// ***********************************************************//
				if ("DMYDR005".equals(szRcvTcCode)) 
				{
					ydAimRtGp = "K2"; // 출하지시대기
					currProgCd = "K";
				} 
				else if ("DMYDR020".equals(szRcvTcCode)) 
				{
					ydAimRtGp = "L2"; // 운송지시
					currProgCd = "L";
				} 
				else if ("DMYDR023".equals(szRcvTcCode) || "DMYDR060".equals(szRcvTcCode)) 
				{
					ydAimRtGp = "L5"; // 상차지시
					currProgCd = "L";
				} 
				else if ("DMYDR030".equals(szRcvTcCode)) 
				{
					ydAimRtGp = "M2"; // 출하완료
					currProgCd = "M";
					// ***********************************************************//
				} 
				else if ("G".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 종합판정대기
				} 
				else if ("I".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 반송대기
				} 
				else if ("H".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 입고대기
				} 
				else if ("Y".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "C"; // 재공충당대기(C열연정정)
				} 
				else if ("B".equals(currProgCd)) 
				{ 
					// 지시대기
					String sWorkProc = "";
					// sNextProc =
					// this.trim(loadYdStock.getRecord(0).getFieldString("NEXT_PROC"));

					// C 동 수입인 경우 에만 spm재와 hfl재를 분리해서 적치 한다.
					// if("C".equals(sNextProc.substring(0,1))){
					if ("H".equals(sNextProc.substring(1, 2))) 
					{
						ydAimRtGp = currProgCd + "3"; // 지시대기
					} 
					else 
					{
						ydAimRtGp = currProgCd + "4"; // 지시대기
					}
					// }else{
					// //HCR재 - H, WCR재 - W, CCR재 - C
					// if("H".equals(sHCR_GP)){
					// ydAimRtGp =currProgCd+"3"; //지시대기
					// }else {
					// ydAimRtGp =currProgCd+"4"; //지시대기
					// }
					// }
				} 
				else if ("J".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 반납대기
					// ydAimRtGp ="B3"; //반납대기 ?????
					// }else if("K".equals(currProgCd)){
					// ydAimRtGp =currProgCd+"2"; //출하지시대기
					// }else if("L".equals(currProgCd)){
					// if("DMYDR023".equals(szRcvTcCode)){ //코일제품상차지시
					// ydAimRtGp =currProgCd+"5"; //상차대기
					// }else {
					// ydAimRtGp =currProgCd+"2"; //운송대기
					// }
					// }else if("M".equals(currProgCd)){
					// ydAimRtGp =currProgCd+"2"; //출하완료
				} 
				else if ("Z".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 제품충당대기
				} 
				else if ("X".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 경매대상선정
				} 
				else if ("E".equals(currProgCd) || "D".equals(currProgCd)) 
				{
					// 재공이송작업대기
					String sWorkProc = "";
					// sNextProc =
					// this.trim(loadYdStock.getRecord(0).getFieldString("NEXT_PROC"));
					// sPlanProc1
					// =this.trim(loadYdStock.getRecord(0).getFieldString("PLAN_PROC1"));

					if (!"".equals(sNextProc)) 
					{
						sWorkProc = sNextProc;
					} 
					else 
					{
						sWorkProc = sPlanProc1;
					}
					// 계획공정정보를 가지고 야드행선을 셋팅
					if (sWorkProc.startsWith("1")) 
					{
						ydAimRtGp = "EA";
					} 
					else if (sWorkProc.startsWith("5") || sWorkProc.startsWith("6")) 
					{
						ydAimRtGp = "EB";
					} 
					else if (sWorkProc.startsWith("9S")) 
					{
						ydAimRtGp = "ED";
					} 
					else 
					{
						ydAimRtGp = "EC";
					}
				} 
				else if ("C".equals(currProgCd)) 
				{
					// 정정작업지시대기
					String sWorkProc = "";
					// sNextProc =
					// this.trim(loadYdStock.getRecord(0).getFieldString("NEXT_PROC"));
					// sPlanProc1 =
					// this.trim(loadYdStock.getRecord(0).getFieldString("PLAN_PROC1"));

					if (!"".equals(sNextProc)) 
					{
						sWorkProc = sNextProc;
					} 
					else 
					{
						sWorkProc = sPlanProc1;
					}

					szMsg = "다음공정(계획공정)::" + sWorkProc;
					this.printLog(logId, szMsg, "SL");
					/*
					 * 계획공정코드 DH C열연 D Line No3HFL C열연 D Line No3HFL(정정LINE구분 :
					 * No3HFL) 11 DA C열연 D Line 공냉 C열연 D Line 공냉(Hysco向) 12 EH
					 * C열연 E Line Hot Final C열연 E Line Hot Final(정정LINE구분:SPM2)
					 * 13 EK C열연 E Line Skin Pass C열연 E Line Skin
					 * Pass(정정LINE구분:SPM2) 14 ER C열연 E Line Recoiling C열연 E Line
					 * Recoiling(정정LINE구분:SPM2) 15 EA C열연 E Line 공냉 C열연 E Line
					 * 공냉(Hysco向) 16 FH C열연 F Line No2HFL C열연 F Line
					 * No2HFL(정정LINE구분:No2HFL) 17 FA C열연 F Line 공냉 C열연 F Line
					 * 공냉(Hysco向) 18 GA C열연 G Line 공냉 C열연 G Line
					 * 공냉(정정LINE구분:No1HFL) 19 GH C열연 G Line No1HFL C열연 G Line
					 * No1HFL(정정LINE구분:No1HFL) 20 GT C열연 G Line 수냉 C열연 G Line
					 * 수냉(정정LINE구분:No1HFL) 21 HH C열연 H Line Hot Final C열연 H Line
					 * Hot Final(정정LINE구분:SPM1) 22 HK C열연 H Line Skin Pass C열연 H
					 * Line Skin Pass(정정LINE구분:SPM1) 23 HR C열연 H Line Recoiling
					 * C열연 H Line Recoiling(정정LINE구분:SPM1) 24 HA C열연 H Line 공냉
					 * C열연 H Line 공냉(Hysco向) 25 야드행선구분 CE 작업대기(C열연 HFL) CF
					 * 작업대기(C열연 SPM1) CG 작업대기(C열연 SPM2) CH 작업대기(C열연#1결속대) CI
					 * 작업대기(C열연#2결속대)
					 */

					// 계획공정정보를 가지고 야드행선을 셋팅 _ 추후 다시 셋팅 (C열연만 셋팅 )
					if ("DH".equals(sWorkProc) || "FH".equals(sWorkProc)
							|| "GA".equals(sWorkProc) || "GH".equals(sWorkProc)
							|| "CA".equals(sWorkProc) || "CH".equals(sWorkProc)
							|| "AA".equals(sWorkProc) || "BH".equals(sWorkProc)
							|| "GT".equals(sWorkProc)) 
					{
						ydAimRtGp = "CE";
					} 
					else if ("HH".equals(sWorkProc) || "HK".equals(sWorkProc) || "HR".equals(sWorkProc)) 
					{
						ydAimRtGp = "CF";
					} 
					else if ("EH".equals(sWorkProc) || "EK".equals(sWorkProc) || "ER".equals(sWorkProc)) 
					{
						ydAimRtGp = "CG";
					} 
					else if ("CK".equals(sWorkProc) || "CR".equals(sWorkProc)) 
					{
						ydAimRtGp = "CF";
					} 
					else if ("BK".equals(sWorkProc) || "BR".equals(sWorkProc)) 
					{
						ydAimRtGp = "CF";
					} 
					else if ("AK".equals(sWorkProc) || "AR".equals(sWorkProc)) 
					{
						ydAimRtGp = "CF";
					} 
					else 
					{
						ydAimRtGp = "XX";
					}
					
					if ("F4".equals(sYD_AIM_RT_GP2) || "F5".equals(sYD_AIM_RT_GP2)) 
					{ 
						// 재작업인 경우
						ydAimRtGp = sYD_AIM_RT_GP2; // 재작업인(C열연정정)
					}
				} 
				else if ("F".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "3"; // 판정보류
				}

				// 2pass재 작업 대상
				if ("Z".equals(sSKINPASS_YN) && ("C".equals(currProgCd) || "D".equals(currProgCd))) 
				{
					ydAimRtGp = "EA";
				}
				// ***************************************************************************************************************************
			} 
//			else if ("S".equals(sItemGp)) 
//			{
//				// 수신한 재료번호로 슬라브공통을 읽기
//				// ***************************************************************************************************
//				recEditInRecord.setField("SLAB_NO", sSTL_NO);
//
//				JDTORecordSet loadYdStock1 = commDao.select(recEditInRecord,"com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getSLABCOMM",logId, szMethodName, "슬라브공통검색");
//
//				if (loadYdStock1.size() <= 0) 
//				{
//					szMsg = "슬라브공통을 SELECT Error :: [" + sSTL_NO + "]" + "DO NOT EXIST";
//					this.printLog(logId, szMsg, "SL");
//					return rVal;
//				} 
//				else 
//				{
//					szMsg = sSTL_NO + " :: 슬라브공통을 SELECT Success :: [" + loadYdStock1.size() + "]";
//					this.printLog(logId, szMsg, "SL");
//
//					// 진도코드 존제여부 체크
//					if ("".equals(this.trim(loadYdStock1.getRecord(0).getFieldString("CURR_PROG_CD")))) 
//					{
//						szMsg = "진도코드가  존재  안 함";
//						this.printLog(logId, szMsg, "SL");
//						return rVal;
//					} 
//					else 
//					{
//						// 진도코드
//						currProgCd = this.trim(loadYdStock1.getRecord(0).getFieldString("CURR_PROG_CD"));
//						szMsg = "진도코드::" + currProgCd;
//						this.printLog(logId, szMsg, "SL");
//						// ***********************************************************//
//						if ("DMYDR004".equals(szRcvTcCode)) 
//						{
//							ydAimRtGp = "K1"; // 출하지시대기
//							currProgCd = "K";
//						} 
//						else if ("DMYDR016".equals(szRcvTcCode)) 
//						{
//							ydAimRtGp = "N1"; // 운송지시대기
//							currProgCd = "N";
//						} 
//						else if ("DMYDR022".equals(szRcvTcCode)) 
//						{
//							ydAimRtGp = "L4"; // 운송상차지시
//							currProgCd = "L";
//						} 
//						else if ("DMYDR029".equals(szRcvTcCode)) 
//						{
//							ydAimRtGp = "M1"; // 출하완료
//							currProgCd = "M";
//							// ***********************************************************//
//						} 
//						else if ("G".equals(currProgCd)) 
//						{
//							ydAimRtGp = currProgCd + "1"; // 종합판정대기
//						} 
//						else if ("H".equals(currProgCd)) 
//						{
//							ydAimRtGp = currProgCd + "1"; // 입고대기
//						} 
//						else if ("J".equals(currProgCd)) 
//						{
//							ydAimRtGp = currProgCd + "1"; // 반납대기
//						} 
//						else if ("K".equals(currProgCd)) 
//						{
//							ydAimRtGp = currProgCd + "1"; // 출하지시대기
//						} 
//						else if ("L".equals(currProgCd)) 
//						{
//							ydAimRtGp = currProgCd + "1"; // 운송대기
//						} 
//						else if ("N".equals(currProgCd)) 
//						{
//							ydAimRtGp = currProgCd + "1"; // 운송지시대기
//						} 
//						else if ("M".equals(currProgCd)) 
//						{
//							ydAimRtGp = currProgCd + "1"; // 출하완료
//						} 
//						else if ("Z".equals(currProgCd)) 
//						{
//							ydAimRtGp = currProgCd + "1"; // 제품충당대기
//						} 
//						else if ("X".equals(currProgCd)) 
//						{
//							ydAimRtGp = currProgCd + "1"; // 경매대상선정
//						}
//					}
//				}
//			}
			// ***************************************************************************************************************************
		} 
		catch (Exception e) 
		{
			szMsg = "야드목표행선지구분 예외발생! 예외메세지: " + e.getMessage();
			this.printErrorLog(logId, szMethodName, szMsg, this, e);

		}

		szMsg = "진도코드: " + currProgCd+" 야드목표행선지구분: " + ydAimRtGp;
		this.printLog(logId, szMsg, "SL");
 
		rVal[0] = ydAimRtGp;
		rVal[1] = currProgCd;
		return rVal;
	}	

//	/**
//	 * 운송지시 변경 작업(차량스케줄,검수재료,저장품)
//	 * @param recPara
//	 * @param szCaller
//	 * @return
//	 * @throws JDTOException
//	 */
//	public  String transOrdChange(JDTORecord recPara ) throws JDTOException 
//  {
//		String logId = recPara.getResultCode();
//		String	szMethodName			= "transOrdChange";
//		String	szOperationName			= "운송지시 변경 작업";
//		String	szMsg					= null;
//		String 	szRtnMsg				= null;
//		YdCarSchDao ydCarSchDao = new YdCarSchDao();
//		
//		JDTORecordSet	rsResult		= null;
//		JDTORecord		recTemp			= null;
//		
//		String	szOLD_TRANS_WORD_DATE		= null;
//		String 	szOLD_TRANS_WORD_SEQNO		= null;
//		String	szNEW_TRANS_WORD_DATE		= null;
//		String 	szNEW_TRANS_WORD_SEQNO		= null;
//		String 	szCHK_GP					= null;
//		YmCommDAO commDao = new YmCommDAO();
//		int 	cnt	=0;
//		int intRtnVal =0;
//		//--------------------------------------------------------------------------------
//		//	운송지시일자, 운송지시순번으로 차량스케줄 조회
//		//--------------------------------------------------------------------------------
//		
//		szMsg = "["+szOperationName+"] --------------- 메소드 시작 ---------------";
//		
// 
//		printLog(logId, szMethodName, "S+");
//		this.printLog(logId, szMsg, "SL");
//		szOLD_TRANS_WORD_DATE			= this.trim(recPara.getFieldString("OLD_TRANS_WORD_DATE")); 
//		szOLD_TRANS_WORD_SEQNO			= this.trim(recPara.getFieldString("OLD_TRANS_WORD_SEQNO"));
//		szNEW_TRANS_WORD_DATE			= this.trim(recPara.getFieldString("NEW_TRANS_WORD_DATE"));
//		szNEW_TRANS_WORD_SEQNO			= this.trim(recPara.getFieldString("NEW_TRANS_WORD_SEQNO"));
//		szCHK_GP						= this.trim(recPara.getFieldString("CHK_GP"));
//
//		recTemp			= JDTORecordFactory.getInstance().create();
//		recTemp.setField("OLD_TRANS_WORD_DATE" , szOLD_TRANS_WORD_DATE);
//		recTemp.setField("OLD_TRANS_WORD_SEQNO", szOLD_TRANS_WORD_SEQNO);
//		recTemp.setField("NEW_TRANS_WORD_DATE" , szNEW_TRANS_WORD_DATE);
//		recTemp.setField("NEW_TRANS_WORD_SEQNO", szNEW_TRANS_WORD_SEQNO);
//		recTemp.setField("MODIFIER"				, "trOChange");
//		
//		//--------------------------------------------------------------------------------
//		//	차량스케줄 운송지시 변경
//		//--------------------------------------------------------------------------------
// 
//		szMsg = "["+szOperationName+"] 차량스케줄 운송지시 변경 시작";
//		this.printLog(logId, szMsg, "▣");
//		
//	
//			//차량스케줄 운송지시 변경
//    		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarschTransOrd */
//			/*UPDATE USRYDA.TB_YD_CARSCH
//				SET TRANS_ORD_DATE=:V_NEW_TRANS_WORD_DATE
//				, TRANS_ORD_SEQNO=:V_NEW_TRANS_WORD_SEQNO
//				, MODIFIER =:V_MODIFIER
//				,MOD_DDTT=sysdate
//				WHERE TRANS_ORD_DATE=:V_OLD_TRANS_WORD_DATE
//				 AND TRANS_ORD_SEQNO=:V_OLD_TRANS_WORD_SEQNO*/
//
//			
//			intRtnVal = commDao.update(recTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarschTransOrd", logId, szOperationName, "TB_YD_CARSCH 차량스케줄 운송지시 변경");
//			if(intRtnVal <= 0)
//			{
//				szMsg="["+szOperationName+"]"  
//                + " TB_YD_CARSCH UPDATE Error " 
//				+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//				+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//				+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//				+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;
//     
//				this.printLog(logId, szMsg, "SL");
//				return "";
//	 
//			}
//			else
//			{
//				szMsg="["+szOperationName+"]" 
//				+ " TB_YD_CARSCH UPDATE Success " 				
//				+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//				+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//				+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//				+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;    
//				this.printLog(logId, szMsg, "SL");
//			}	
//
//		szMsg = "["+szOperationName+"] 차량스케줄 운송지시 변경 완료:"+cnt;
//		this.printLog(logId, szMsg, "▣");
//		
//		//--------------------------------------------------------------------------------
//		 
//		
//		//--------------------------------------------------------------------------------
//		//	검수재료 운송지시 변경
//		//--------------------------------------------------------------------------------
// 
//		szMsg = "["+szOperationName+"] 검수재료 운송지시 변경 시작";
//		this.printLog(logId, szMsg, "▣");	
//		
//		
//		
//		//검수재료 운송지시 변경
//		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdExamTransOrd */
//		/*UPDATE USRYDA.TB_YD_EXAMINATIONCHKLIST
//			SET TRANS_ORD_DATE=:V_NEW_TRANS_WORD_DATE
//			, TRANS_ORD_SEQNO=:V_NEW_TRANS_WORD_SEQNO
//			, MODIFIER =:V_MODIFIER
//			,MOD_DDTT=sysdate
//			, DEL_YN='N'
//			, CHECKING_YN='N'
//			, LABEL_YN=NULL
//			, YD_AB_CD=NULL
//			WHERE TRANS_ORD_DATE=:V_OLD_TRANS_WORD_DATE
//			 AND TRANS_ORD_SEQNO=:V_OLD_TRANS_WORD_SEQNO*/
//
//		
//		intRtnVal = commDao.update(recTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdExamTransOrd", logId, szOperationName, "TB_YD_EXAMINATIONCHKLIST 검수재료 운송지시 변경");
//		if(intRtnVal <= 0)
//		{
//			szMsg="["+szOperationName+"]"  
//            + " TB_YD_EXAMINATIONCHKLIST UPDATE Error " 
//			+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//			+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//			+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//			+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;
// 
//			this.printLog(logId, szMsg, "SL");
//			return "";
//		}
//		else
//		{
//			szMsg="["+szOperationName+"]" 
//			+ " TB_YD_EXAMINATIONCHKLIST UPDATE Success " 
//			+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//			+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//			+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//			+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;    
//			this.printLog(logId, szMsg, "SL");
//
//		}	
//		
// 
//		szMsg = "["+szOperationName+"] 검수재료 운송지시 변경 완료:"+cnt;
//		this.printLog(logId, szMsg, "▣");
//		
//		//--------------------------------------------------------------------------------
//		
//		//--------------------------------------------------------------------------------
//		//	재료정보 운송지시 변경
//		//--------------------------------------------------------------------------------
// 
//		szMsg = "["+szOperationName+"] 재료정보 운송지시 변경 시작";
//		this.printLog(logId, szMsg, "▣");
//		
//		if("YD".equals(szCHK_GP))
//		{
//			
//			//재료정보 운송지시 변경
//			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStockTransOrd*/
//			/*	UPDATE USRYDA.TB_YD_STOCK
//				SET TRANS_ORD_DATE=:V_NEW_TRANS_WORD_DATE
//				, TRANS_ORD_SEQNO=:V_NEW_TRANS_WORD_SEQNO
//				, MODIFIER =:V_MODIFIER
//				,MOD_DDTT=sysdate
//				WHERE TRANS_ORD_DATE=:V_OLD_TRANS_WORD_DATE
//				 AND TRANS_ORD_SEQNO=:V_OLD_TRANS_WORD_SEQNO*/
//
//			
//			intRtnVal = commDao.update(recTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStockTransOrd", logId, szOperationName, "TB_YD_STOCK 검수재료 운송지시 변경");
//			if(intRtnVal <= 0)
//			{
//				szMsg="["+szOperationName+"]"  
//	            + " TB_YD_STOCK UPDATE Error " 
//	            + " szCHK_GP: "+szCHK_GP    
//				+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//				+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//				+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//				+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;
//	 
//				this.printLog(logId, szMsg, "SL");
//				return "";
//			}
//			else
//			{
//				szMsg="["+szOperationName+"]" 
//				+ " TB_YD_STOCK UPDATE Success " 
//				+ " szCHK_GP: "+szCHK_GP    
//				+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//				+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//				+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//				+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;    
//				this.printLog(logId, szMsg, "SL");
//
//			}	
//		}
//		else
//		{
//			
//			//재료정보 운송지시 변경
//			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYmStockTransOrd*/
//			/*UPDATE USRYMA.TB_YM_STOCK
//				SET TRANS_WORD_NO=:V_NEW_TRANS_WORD_DATE||:V_NEW_TRANS_WORD_SEQNO
//				    , MODIFIER =:V_MODIFIER
//				    , MOD_DDTT=sysdate
//				WHERE TRANS_WORD_NO=:V_OLD_TRANS_WORD_DATE||:V_OLD_TRANS_WORD_SEQNO*/
//
//			
//			intRtnVal = commDao.update(recTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYmStockTransOrd", logId, szOperationName, "TB_YD_STOCK 검수재료 운송지시 변경");
//			if(intRtnVal <= 0)
//			{
//				szMsg="["+szOperationName+"]"  
//	            + " TB_YD_STOCK UPDATE Error " 
//	            + " szCHK_GP: "+szCHK_GP    
//				+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//				+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//				+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//				+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;
//	 
//				this.printLog(logId, szMsg, "SL");
//				return "";
//			}
//			else
//			{
//				szMsg="["+szOperationName+"]" 
//				+ " szCHK_GP: "+szCHK_GP    
//				+ " TB_YD_STOCK UPDATE Success " 
//				+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//				+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//				+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//				+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;    
//				this.printLog(logId, szMsg, "SL");
//
//			}
//			
//			/*com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYmStockTransOrd*/
//			cnt = ydCarSchDao.updYdTransOrdChange(recTemp, 3);
//		}
// 
//		szMsg = "["+szOperationName+"] 재료정보 운송지시 변경 완료:"+cnt;
//		this.printLog(logId, szMsg, "▣");
//		
//		//--------------------------------------------------------------------------------
//		
//		
//		//--------------------------------------------------------------------------------
//		
//		szMsg = "["+szOperationName+"] --------------- 메소드 끝 ---------------";
//		 
//		printLog(logId, szMethodName, "S-");
//		this.printLog(logId, szMsg, "SL");
//		return szRtnMsg;
//	}
	
	/**
	 * GridData의 입력/수정/삭제 정보를 JDTORecord [] 으로 변환하여 리턴한다. (GridData의 입력/수정/삭제
	 * 항목을 가져오기위해 사용) - 추가 : 체크가 되지않고 모든 그리드 정보를 변환
	 * 
	 * @param inDto
	 * @return
	 */
	public JDTORecord[] genGridToJDTORecordAll(GridData inDto) throws Exception 
	{
		boolean isUpperKey = false;
		YDDataUtil yDDataUtil = new YDDataUtil();
		
		if (inDto.getParam("set_upper") != null && "true".equals(inDto.getParam("set_upper"))) 
		{
			isUpperKey = true;
		}
		
		String szUserId= yDDataUtil.setDataDefault(inDto.getParam("YD_USER_ID"), "");

		GridHeader[] ghs = inDto.getHeaders();
		int hCount = ghs.length;
		int rCount = 0;
		
		if (hCount > 0) 
		{
			rCount = ghs[0].getRowCount();
		}
		
		JDTORecord[] jdtoAl = new JDTORecord[rCount];
		logger.println(LogLevel.DEBUG, "========  GridData -> JDTORecord []  ================");
		logger.println(LogLevel.DEBUG, "헤더갯수:" + hCount);
		logger.println(LogLevel.DEBUG, "Row갯수:" + rCount);

		logger.println(LogLevel.DEBUG, "========== GridData inDto ROW DATA ===========");
		for (int i = 0; i < rCount; i++) 
		{	
			JDTORecord jDto = this.genParamToJDTORecord(inDto);

			for (int j = 0; j < hCount; j++) 
			{
				String key = ghs[j].getID();
				String rValue = "";
				String hValue = "";
				
				//수정_이현성 [콤보박스일때 문제점 해결하기 위함]
				if (ghs[j].getDataType().equals(OperateGridData.t_combo)) 
				{
					
					int iSelectedIdx = ghs[j].getSelectedIndex(i);
					if(iSelectedIdx >= 0)
					{
						if(ghs[j].hasComboList())
						{
							rValue = StringHelper.evl( ghs[j].getComboHiddenValues(ghs[j].getSelectedComboListKey(i))[iSelectedIdx], "");
							hValue = StringHelper.evl( ghs[j].getComboValues(ghs[j].getSelectedComboListKey(i))[iSelectedIdx], "");
						}
						else
						{
							rValue = StringHelper.evl( ghs[j].getComboHiddenValues()[iSelectedIdx], "");
							hValue = StringHelper.evl( ghs[j].getComboValues()[iSelectedIdx], "");							
						}
					}
					else
					{
						rValue = "";
						hValue = "";
					}
				}
				else 
				{
					rValue = StringHelper.evl(ghs[j].getValue(i), "");
					hValue = StringHelper.evl(ghs[j].getHiddenValue(i), "");
				}

				jDto.setField((isUpperKey) ? key.toUpperCase() : key, rValue);
			}
			jDto.setField("YD_USER_ID",szUserId);
			jdtoAl[i] = jDto;
			
		}

		logger.println(LogLevel.DEBUG, "========== JDTORecord START ===========");
		for (int ss = 0; ss < jdtoAl.length; ss++) 
		{
			logger.println(LogLevel.DEBUG, jdtoAl[ss].toString());
		}
		logger.println(LogLevel.DEBUG, "========== JDTORecord END ===========");

		return jdtoAl;
	}
	
	/**
	 * 오퍼레이션명 : 카드번호로 차량 작업 구분
	 *
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public String getCarMoveYN(String cardNo)
	{
		String carMove = "N";
		
		//차량동간이적 9990~9994
		if
		(
			(YfConstant.CAR_BAY_TRANS_CARD_NO_9990.equals(cardNo)) || 
			(YfConstant.CAR_BAY_TRANS_CARD_NO_9991.equals(cardNo)) || 
			(YfConstant.CAR_BAY_TRANS_CARD_NO_9992.equals(cardNo)) || 
			(YfConstant.CAR_BAY_TRANS_CARD_NO_9993.equals(cardNo)) || 
			(YfConstant.CAR_BAY_TRANS_CARD_NO_9994.equals(cardNo))
		) 
		{
			carMove = "Y"; 
		}
		
		return carMove;


	} // end of getTcCode();
	
	/**
	 * Object 객체로부터 JDTORecord 의 객체를 생성합니다. Method 클래스의 invoke 메소드를 사용하여 Object
	 * 에 담겨진 데이터를 JDTORecord 의 데이터로 설정합니다. JDTORecord 의 KEY 값은 Object 의 set/get
	 * 펑션명칭과 동일하게 구성됩니다( 대소문자 구분됨 )
	 * 
	 * @param oClass
	 *            Object 객체의 Class
	 * @param oInstance
	 *            입력 Object 객체
	 * @return 출력 JDTORecord 객체
	 * @throws AppRuntimeException
	 */
	public static JDTORecord genJDTO(Class oClass, Object oInstance) throws AppRuntimeException 
	{
		// 출력 JDTORecord 객체
		JDTORecord jdto = null;
		// 입력 Object 클래스가 가지고 있는 메소드 배열
		Method[] oMethodArr = null;

		try 
		{
			// 출력 jdto 객체 생성
			jdto = JDTORecordFactory.getInstance().create();

			// 입력 Class 로부터 메소드 명칭 배열을 취득합니다.
			oMethodArr = oClass.getMethods();

			// 입력 Class 의 메소드 명칭 배열만큼 반복
			for (int ii = 0; ii < oMethodArr.length; ii++) 
			{
				// 입력 Class 의 메소드
				Method oMethod = oMethodArr[ii];
				// 입력 Class 의 메소드명칭
				String methodName = oMethod.getName();

				logger.println(LogLevel.DEBUG, " [ 메소드 명칭 ] " + methodName);

				// 메소드 호출 뒤 반환값을 저장하는 변수
				Object obj = null;

				// JDTORecord의 key
				String key = null;

				// 메소드의 명칭이 get_prefix 경우에 실행
				if (methodName.startsWith("get")) 
				{
					if(!"getClass".equals(methodName))
					{
						// JDTORecord의 key 변수에 get_prefix 를 제외한 값을 설정
						key = methodName.substring(3);
						// oInstance 객체의 set 메소드를 실행(invoke)
						//obj = oMethod.invoke(oInstance, null);
						// JDTORecord 에 값을 저장
						jdto.setField(key, obj);
					}
				}
			}
		} 
		catch (Exception e) 
		{
			throw new AppRuntimeException(e.toString(), e);
		} 
		finally 
		{

		}
		// 출력 JDTORecord 객체 반환
		return jdto;
	}	
		
	/**
	 * SMS SENDER
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
    public static String updSmsMsgSend(JDTORecord recInPara) throws DAOException 
    {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;
		
		szMsg        				= "";
		szMethodName 				= "updSmsMsgSend";
		String szOperationName 		= "SMS SENDER";
		 
		JDTORecord	inRecord 		= null;
		
		try 
		{	
			szMsg = "SMS SENDER 시작";
			
			// JDTORecord 생성
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("FROM_SENDER_NAME", new String("야드관리"));	// SMS 보내는 사람 성명
			
			inRecord.setField("FROM_PHONE_NO", recInPara.getFieldString("FROM_PHONE_NO"));	// SMS 보내는 사람 핸드펀번호
			inRecord.setField("TO_PHONE_NO"  , recInPara.getFieldString("TO_PHONE_NO"));	// SMS 받는 사람 핸드펀번호
			inRecord.setField("TO_CONTENT"   , recInPara.getFieldString("TO_CONTENT"));		// SMS 전송 내용
			inRecord.setField("TO_SEND_TIME" , new String(""));								// SMS 전송시간
			
			//---------------------------------------------------------------------
		    MessageSenderAuto    sender = new MessageSenderAuto();
		    inRecord.setField("RECV_ID", "YD00001");
    		inRecord.setField("GROUP_ID", "MMS1");
		    inRecord.setField("PROGRAM_ID", "updSmsMsgSendYD");

		    sender.sendAutoSMS(inRecord);
		    //---------------------------------------------------------------------
		    
			szMsg = "SMS SENDER 끝";
		}
		catch(Exception ex) 
		{
			szMsg = "["+szOperationName+"] SMS 송신 ERROR - 메세지 : " + ex.getMessage();
		}
		return YdConstant.RETN_CD_SUCCESS;
	}	// end of updSmsMsgSend
    
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
	public int chkParam(String szData, int intDataLen, int intNullChk)
	{
		String szMsg        = null;
		String szMethodName = "chkParam";
		int intRtnVal = 0;
		
		try 
		{
			if (intNullChk == 0) 
			{
				//not null이고 고정길이 체크
				if("".equals(szData) || ((!"".equals(szData)) && szData.length() != intDataLen)) 
				{
					intRtnVal = -1;
				}
			} 
			else if (intNullChk == 1) 
			{
				//not null이고 가변길이 체크
				if("".equals(szData)) 
				{
					intRtnVal = -1;
				}
				else 
				{
					//제한길이보다 길면 cut
					if (szData.trim().length() > intDataLen) 
					{
						intRtnVal = -3;
					}
				}
			}
			else if (intNullChk == 2) 
			{
				//가변길이 체크
				if ("".equals(szData)) 
				{
					intRtnVal = 0;
				}
				else
				{
					//제한길이보다 길면 cut
					if (szData.trim().length() > intDataLen) 
					{
						intRtnVal = -3;
					}
				}
			}
			else if (intNullChk == 3) 
			{
				//no check
				intRtnVal = 0;
			}
		} 
		catch(Exception e) 
		{	
			szMsg = "Exception: " + e.getMessage();
			putLog(szSessionName, szMethodName, szMsg, YfConstant.ERROR);
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
	public long makeErrorLong(int intMaxLen) 
	{
		String szMsg        = null;
		String szMethodName = "makeErrorLong";
		long lngRtnVal;
		String strTemp = new String();
		
		for (int i = 0; i < intMaxLen; i++) 
		{
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
	public double makeErrorDouble(int intPre, int intPost) 
	{

		double dblRtnVal;
		String strTemp = new String();

		if (intPre == 0 && intPost == 0)
		{
			return dblRtnVal = 0;
		}
		
		for (int i = 0; i < intPre; i++) 
		{
			strTemp = strTemp.concat("9");
		}
		
		if (intPost > 0) 
		{
			strTemp = strTemp.concat(".");
			
			for (int i = 0; i < intPost; i++) 
			{
				strTemp = strTemp.concat("9");
			}
		}
		
		dblRtnVal = StringHelper.parseDouble(strTemp);

		return dblRtnVal;
	} // end of makeErrorDouble
    
    /**
	 *      [A] 오퍼레이션명 : paraRecChkNull_2 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return String			     // 해당 필드의 데이터
	 * @throws JDTOException 
	 */
	public String paraRecChkNull_2(JDTORecord recPara, String szFieldName) throws JDTOException 
	{
		String szRtnVal = null;
		
		if (recPara.getField(szFieldName) == null)
		{
			szRtnVal = "";
		}
		else
		{
			szRtnVal = recPara.getFieldString(szFieldName);
		}
		
		return szRtnVal;
	} // end of paraRecChkNull
	
	/**
	 *      [A] 오퍼레이션명 : dataCut 
	 * 
	 * @param int intMaxLen         // Field Length
	 * @return String     			// 데이터 길이로 보정된 String
	 */	
	public String dataCut(String strValue, int intMaxLen) 
	{
		String strRtnVal = new String();

		for(int i = 0; i < intMaxLen; i++)
		{
			strRtnVal = strRtnVal + strValue.charAt(i);
		}

		return strRtnVal;
	}
	
	/**
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
     * 일정 길이만큼 앞에 공백을 채운다.
     * @String in_strValue, int in_intLength 
     */
	public static String FillToNumber(String in_strValue, int in_intLength )
	{
		String in_strRet = "";
		try
		{	
			if (CommonUtil.getLength(in_strValue) > in_intLength)
			{
				in_strRet = CommonUtil.substr(in_strValue, 0, in_intLength);
			}
			else
			{
				in_strRet = MakeSpace(in_intLength - CommonUtil.getLength(in_strValue),"0") + in_strValue  ;
			}
		}
		catch(Exception e)
		{
			
		}
		
		return in_strRet;
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
	public boolean chkField 
	(
		JDTORecord inRec, 
		String szFieldName, 
		int intMaxLen, 
		int intChkNull, 
		char chDataType , 
		int intPre, 
		int  intPost
	)
	throws JDTOException 
	{
		
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
		
		try 
		{
			szTemp = this.paraRecChkNull_2(inRec, szFieldName);
	
			// parameter check
			intRtnVal = this.chkParam(szTemp, intMaxLen, intChkNull);
			
			// primary key error return
			if (intRtnVal == -1 || intRtnVal == -2) 
			{
				szMsg = szFieldName + " Error!!!";
				putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}
			
			// data length error
			if (intRtnVal == -3)
			{
				szData = this.dataCut(szTemp, intMaxLen);	//data cut
			}
			else
			{
				szData = szTemp;
			}
			
			if ("".equals(szData)) 
			{
				inRec.setField(szFieldName, szData);
			} 
			else 
			{
				if (chDataType == DOUBLE_TYPE) 
				{
					//double
					dblVal = StringHelper.parseDouble(szData, this.makeErrorDouble(intPre, intPost));
					dblObj = new Double(dblVal);
					inRec.setField(szFieldName, dblObj);
				}
				else if( chDataType == INTEGER_TYPE ) 
				{
					//int
					intVal = StringHelper.parseInt(szData);
					intObj = new Integer(intVal);
					inRec.setField(szFieldName, intObj);
				 
				} 
				else if (chDataType == LONG_TYPE) 
				{
					//long
					lngVal = StringHelper.parseLong(szData, this.makeErrorLong(intMaxLen));
					lngObj = new Long(lngVal);
					inRec.setField(szFieldName, lngObj);
				
				}
				else if (chDataType == PAGE_COUNT_TYPE) 
				{
					//Page Count 처리
					lngVal = StringHelper.parseLong(szData, 1);
					lngObj = new Long(lngVal);
					inRec.setField(szFieldName, lngObj);
				
				}
				else if (chDataType == ROW_COUNT_TYPE) 
				{
					//Row Count 처리	
					lngVal = StringHelper.parseLong(szData, 10);
					lngObj = new Long(lngVal);
					inRec.setField(szFieldName, lngObj);
				}
				else if( chDataType == DATETIME_TYPE ) 
				{
					//DATETIME 처리
					inRec.setField(szFieldName, szData.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", ""));
				} 
				else
				{
					//String
					inRec.setField(szFieldName, szData);
				}
					
			}
		} 
		catch (Exception e) 
		{
			szMsg = "chkField() Exception";
			putLog(szSessionName, szMethodName, szMsg, YfConstant.DEBUG);
		
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szSessionName + e.getMessage(), e);
		}
		
		return blnRtnVal;
	}
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치열 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int        intGp  구분(0:YD_STK_COL_GP,
	 *                               1:YD_GP,YD_BAY_GP,YD_EQP_GP, YD_STK_COL_ACT_STAT
	 *                               2:YD_GP,YD_BAY_GP,YD_EQP_GP,YD_STK_COL_NO , YD_STK_COL_ACT_STAT
	 *                               3:YD_STK_COL_NO1 ,YD_STK_COL_NO2,YD_STK_COL_NO3,PAGE_CNT1,ROW_CNT1,PAGE_CNT2,ROW_CNT2
	 *                               4:V_WLOC_CD ,  V_YD_PNT_CD
	 *                               5:V_YD_GP, V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, V_ROW_CNT2
	 *                               6:V_YD_STK_COL_GP
	 *                               8:YD_STK_COL_GP
	 *                               9:V_YD_GP, V_YD_BAY_GP, V_YD_EQP_GP, V_YD_STK_COL_NO
	 *                               10:V_YD_GP, V_YD_BAY_GP, V_YD_EQP_GP, V_YD_STK_COL_NO
	 *                               11:YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_BED_NO_R
	 *                               12:YD_GP
	 *                               16:YD_STK_COL_GP
	 *                               18:YD_GP
	 *                               
	 *                               21:V_YD_GP, V_YD_BAY_GP, V_YD_EQP_GP
	 *                               )
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdStkcol(JDTORecord inRec, int intGp) throws JDTOException  
	{
		String szFieldName = null;
		boolean blnErr = true;
		
		try 
		{
			if (intGp == 0 || intGp == 6 || intGp == 8 || intGp == 16) 
			{
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			} 
			else if(intGp == 1)
			{
			
				szFieldName = "V_YD_GP";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				
				szFieldName = "V_YD_STK_COL_ACT_STAT";
				blnErr = chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);	
			} 
			else if(intGp == 2)
			{
				
				szFieldName = "V_YD_GP";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO";
				blnErr = chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				
				szFieldName = "V_V_YD_STK_COL_ACT_STAT";
				blnErr = chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			}
			else if ( intGp == 13) 
			{	
				szFieldName = "V_YD_GP";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO";
				blnErr = chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			}
			else if (intGp == 3)
			{
				szFieldName = "V_YD_STK_COL_NO1";
				blnErr = chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO2";
				blnErr = chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO3";
				blnErr = chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_ACT_STAT";
				blnErr = chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			}
			else if (intGp == 4)
			{	
				szFieldName = "V_WLOC_CD";
				blnErr = chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PNT_CD";
				blnErr = chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
			} 
			else if (intGp == 5) 
			{
				szFieldName = "V_YD_GP";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_PAGE_CNT1";
				blnErr = chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			} 
			else if (intGp == 7) 
			{
				szFieldName = "V_YD_GP";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_BAY_GP";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO";
				blnErr = chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			}
			else if (intGp == 9) 
			{
				szFieldName = "V_YD_GP";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = chkField(inRec, szFieldName, 1,2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_COIL_OUTDIA_GRP_GP";
				blnErr = chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_W_GP";
				blnErr = chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			} 
			else if (intGp == 10) 
			{ 
				//!A 외경군, 폭별재고조회
				szFieldName = "V_YD_GP";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = chkField(inRec, szFieldName, 1,2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "YD_STK_COL_NO"; //!A 열정보 추가 (박지열 - 2010/03/23) 
				blnErr = chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_COIL_OUTDIA_GRP_GP";
				blnErr = chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_W_GP";
				blnErr = chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			} 
			else if (intGp == 11) 
			{
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_STK_BED_NO";
				blnErr = chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO_R";
				blnErr = chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			}
			else if (intGp == 12 || intGp ==18 ) 
			{
				szFieldName = "V_YD_GP";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);	
			} 
			else if (intGp == 15) 
			{
				szFieldName = "V_YD_GP";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PNT_CD";			
				blnErr = chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
			} 
			else if (intGp == 17) 
			{
				szFieldName = "V_BRANCH_CD1";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_BRANCH_CD2";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_BRANCH_CD3";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_BRANCH_CD4";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_BRANCH_CD5";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			} 
			else if (intGp == 19) 
			{
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			} 
			else if (intGp == 20) 
			{
				szFieldName = "V_START_POS";
				blnErr = chkField(inRec, szFieldName, 1, 1, YdDaoUtils.INTEGER_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = chkField(inRec, szFieldName, 5, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			} 
			else if(intGp == 21)
			{	
				szFieldName = "V_YD_GP";
				blnErr = chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "YD_STK_COL_NO";
				blnErr = chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			} 
			else if (intGp == 23) 
			{
				//항만슬라브야드 기능추가
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_WLOC_CD";
				blnErr = chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}
		} 
		catch (Exception e) 
		{
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szSessionName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdStkcol
	
	/**
     * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
     * 1.TC_CD	: CM1PB10.
     * 2.I/F ID	: YM-BIF-020.
     * 		전문코드		TC					CHAR	7		
     * 		발생일자		Date				CHAR	10		YYYY-MM-DD
     * 		발생시간		Time				CHAR	8		HH-MM-SS
     * 		전문구분		Form				CHAR	1		I  : Initialize, U : Update D : Delete  R : Re-request
     * 		전문길이		Message_Length		CHAR	4		
     * 		송수신구분	SendReq				CHAR	1		R:요구, A:응답
     * 		BED ADDRESS	BedAddress			CHAR	8		야드(1)+동(1)+Span(2)+ Row(2)+BED(2)
     * 		사용유무		UseCheck			CHAR	1		BED 사용유무
     * 		적치가능매수	StackUseCount		CHAR	2		BED 적치 가능 매수
     * 		적치매수		StackCount			CHAR	2		현재 적치 매수
     * 		적치 SEQ		StackSeq			CHAR	2		SLAB 적치 단
     * 		SLAB NO		SlabNo				CHAR	11		SPACE : 적치 무
     * 		제작번호/행번	ProductNo			CHAR	13		
     * 		두께			Thck				CHAR	7		㎜	소수점3자리 (###.###)
     * 		폭			Width				CHAR	6		㎜	소수점1자리 (####.#)
     * 		중량			Weight				CHAR	5		kg	
     * 		길이			Length				CHAR	6		
     * 		X 물리위치	X_Physical_Address	CHAR	6		
     * 		Y 물리위치	Y_Physical_Address	CHAR	6		
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
	public static String setBSlabMapMsgInfo(String sPutLoc)
	{	
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
						   
		try
		{
			//mch A열연 SLAB야드
			if(YfConstant.YD_GP_0.equals(sPutLoc.substring(0, 1)) && YfConstant.BAY_GP_A.equals(sPutLoc.substring(1, 2)))
			{
				sTC	= "YfConstant.TC_HM1PB09";	//차후 수정해야함
			}
			else if(YfConstant.YD_GP_0.equals(sPutLoc.substring(0, 1)) && YfConstant.BAY_GP_B.equals(sPutLoc.substring(1, 2)))
			{
				sTC	= "YfConstant.TC_HM1PB59";	//차후 수정해야함
			}
			else
			{
				sTC	= "YfConstant.TC_CM1PB10";	//차후 수정해야함
			}
			
			sDate				= getCurDate("yyyy-MM-dd");
			sTime				= getCurDate("HH-mm-ss");
			sForm				= "I";
			sMessage_Length		= iTotalLength + "";
			sSendReq			= "R";
			sBedAddress			= sPutLoc.substring(0, 8);
			
			sMsg.append(FillToNumber(sTC,					iTC));
			sMsg.append(FillToNumber(sDate,					iDate));
			sMsg.append(FillToNumber(sTime,					iTime));
			sMsg.append(FillToNumber(sForm,					iForm));
			sMsg.append(FillToNumber(sMessage_Length,		iMessage_Length));
			sMsg.append(FillToNumber(sSendReq,				iSendReq));
			sMsg.append(FillToNumber(sBedAddress,			iBedAddress));
			sMsg.append(FillToNumber(sUseCheck,				iUseCheck));
			sMsg.append(FillToNumber(sStackUseCount,		iStackUseCount));
			sMsg.append(FillToNumber(sStackCount,			iStackCount));
			sMsg.append(FillToNumber(sStackSeq,				iStackSeq));
			sMsg.append(FillToNumber(sSlabNo,				iSlabNo));
			sMsg.append(FillToNumber(sProductNo,			iProductNo));
			sMsg.append(FillToNumber(sThck,					iThck));
			sMsg.append(FillToNumber(sWidth,				iWidth));
			sMsg.append(FillToNumber(sWeight,				iWeight));
			sMsg.append(FillToNumber(sLength,				iLength));
			sMsg.append(FillToNumber(sX_Physical_Address,	iX_Physical_Address));
			sMsg.append(FillToNumber(sY_Physical_Address,	iY_Physical_Address));
			sMsg.append(FillToNumber(sX_Plus_Range,			iX_Plus_Range));
			sMsg.append(FillToNumber(sX_Minus_Range,		iX_Minus_Range));
			sMsg.append(FillToNumber(sY_Plus_Range,			iY_Plus_Range));
			sMsg.append(FillToNumber(sY_Minus_Range,		iY_Minus_Range));
	    }
		catch(Exception e)
	    {
	        throw new EJBServiceException(e);
	    }
		
	    return sMsg.toString();
	}
	
	/** 
	 * 코일 공통 테이블의 진도코드를 참조해서 
	 * 저장품이동조건을 가져온다.
     *
     * @param  String	:	저장품ID
     * @param  String	:	TC_CODE
     * @return String
     * @throws  
     */			 
	public static String[] getCoilCurrProgCd(String sStockId, String TcCode){	
		String[] rVal = new String[2];
		
		String sProgCd   = "";
		String sStocMv   = "";
		String sReturnGp = "";
			
		YfCommDAO 	dao 		= new YfCommDAO();
		YfCommUtils commUtils	= new YfCommUtils();
		
		try{
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();

			jrParam.setField("COIL_NO"	, sStockId);

			JDTORecordSet jsStl = dao.select(jrParam, getCoilComByCurrProgCd);
		
			if(jsStl != null && jsStl.size() > 0)
			{	
				sProgCd 	= commUtils.trim(jsStl.getRecord(0).getFieldString("CURR_PROG_CD"));//진도코드
				sReturnGp 	= commUtils.trim(jsStl.getRecord(0).getFieldString("RETURN_GP")); 	//반납구분
			}
		}catch (DAOException e){
			return rVal;
		}catch (Exception e){
			return rVal;
		}
    	
//    	DMYDR002   //코일제품보류확정			없음
//    	DMYDR005   //코일제품출하지시대기		K
//    	DMYDR008   //코일제품반납대기			J
//    	DMYDR011   //코일제품고간이송지시		없음
//    	DMYDR014   //코일제품목전			주문번호
//    	DMYDR020   //코일제품운송지시			L
//    	DMYDR023   //코일제품상차지시			없음
//    	DMYDR027   //코일제품보관지시			M
//    	DMYDR030   //코일제품출하완료			M
//    	DMYDR033   //코일제품반품			K			
//    	DMYDR036   //코일제품출하차량도착실적		없음       
//    	DMYDR037   //코일임가공차량도착실적		없음       
//    	DMYDR040   //코일제품출하차량출발실적		없음       
//    	DMYDR041   //코일임가공차량출발실적		없음
    	
    	// 일관제철 진도코드
    	if(YfConstant.DMYDR008.equals(TcCode))
    	{			//코일제품반납대기
    		if(YfConstant.RETURN_GP_1.equals(sReturnGp))
    		{
    			sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_JR;
    		}
    		else
    		{
    			sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_JG;
    		}
    	}
    	else if
    	(	
    		YfConstant.DMYDR005.equals(TcCode)||	//코일제품출하지시대기 
    		YfConstant.DMYDR004.equals(TcCode)|| 	//외판슬라브출하지시대기
    		YfConstant.DMYDR033.equals(TcCode)		//코일제품반품	
    	)
    	{				
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_KG;
    	}
    	else if
    	(
    		YfConstant.DMYDR027.equals(TcCode)||	//코일제품보관지시 
    		YfConstant.DMYDR030.equals(TcCode)		//코일제품출하완료
    	)
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_MG;
    	}
    	else if(YfConstant.DMYDR016.equals(TcCode))	//외판슬라브운송지시대기
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_NG;
    	}
    	else if
    	(
    		YfConstant.DMYDR020.equals(TcCode)||	//코일제품운송지시
    		YfConstant.DMYDR022.equals(TcCode)		//외판슬라브운송상차지시 
    	)
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_LG;
    	}
    	else if
    	(
    		YfConstant.CURR_PROG_CD_COIL_A.equals(sProgCd)||
    		YfConstant.CURR_PROG_CD_COIL_R.equals(sProgCd)
    	)
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_AC;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_B.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_BC;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_C.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_CC;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_D.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_DC;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_E.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_CS;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_F.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_FC;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_K.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_KG;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_G.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_GC;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_H.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_HG;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_J.equals(sProgCd))
    	{
    		if(YfConstant.RETURN_GP_1.equals(sReturnGp))
    		{
    			sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_JR;
    		}
    		else
    		{
    			sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_JG;
    		}
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_L.equals(sProgCd))	//코일제품상차지시
    	{ 
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_LG;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_N.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_NG;
    	}
    	else if
    	(
    		YfConstant.CURR_PROG_CD_COIL_M.equals(sProgCd)||
    		YfConstant.CURR_PROG_CD_COIL_P.equals(sProgCd)
    	)
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_MG;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_X.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_XG;	
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_Y.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_YG;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_Z.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_ZG;
    	}															
    	
    	rVal[0] = sProgCd;
    	rVal[1] = sStocMv;
		    	
	    return rVal;
	}
	
	/**
	 * 진도코드로 저장품이동조건을 가져온다.
     *
     * @param  String	:	저장품ID
     * @param  String	:	PROG_CD
     * @return String
     * @throws  
     */			 
	public static String[] getCoilCurrProgCd2(String sStockId ,String sProgCd)
	{	
		String[] rVal = new String[2];
		
		String sStocMv   = "";
		String sReturnGp = "";
			
		YfCommDAO 	dao 		= new YfCommDAO();
		YfCommUtils commUtils	= new YfCommUtils();
    	
    	try
		{
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();

			jrParam.setField("COIL_NO"	, sStockId);

			JDTORecordSet jsStl = dao.select(jrParam, getCoilComByCurrProgCd);
		
			if(jsStl != null && jsStl.size() > 0)
			{	
				sReturnGp 	= commUtils.trim(jsStl.getRecord(0).getFieldString("RETURN_GP")); 	//반납구분
			}
		}
		catch (DAOException e)
		{
			return rVal;
		}
		catch (Exception e)
		{
			return rVal;
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
    	if(YfConstant.CURR_PROG_CD_COIL_B.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_BC;
    	}
    	else if
    	(
    		YfConstant.CURR_PROG_CD_COIL_A.equals(sProgCd) ||
    		YfConstant.CURR_PROG_CD_COIL_R.equals(sProgCd)
    	)
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_AC;	
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_C.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_CC;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_D.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_DC;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_E.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_CS;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_F.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_FC;
    	}
    	else if
    	(
    		YfConstant.CURR_PROG_CD_COIL_K.equals(sProgCd) ||
    		YfConstant.CURR_PROG_CD_COIL_N.equals(sProgCd)
    	)
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_KG;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_G.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_GC;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_H.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_HG;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_J.equals(sProgCd))
    	{
    		if(YfConstant.RETURN_GP_1.equals(sReturnGp))
    		{
    			sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_JR;
    		}
    		else
    		{
    			sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_JG;
    		}
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_L.equals(sProgCd))	//코일제품상차지시 
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_LG;
    	}
    	else if
    	(
    		YfConstant.CURR_PROG_CD_COIL_M.equals(sProgCd) ||
    		YfConstant.CURR_PROG_CD_COIL_P.equals(sProgCd)
    	)
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_MG;	
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_N.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_NG;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_X.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_XG;	
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_Y.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_YG;
    	}
    	else if(YfConstant.CURR_PROG_CD_COIL_Z.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_ZG;
    	}															
    	
    	rVal[0] = sProgCd;
    	rVal[1] = sStocMv;
		    	
	    return rVal;
	}
	
	/**
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
		
		String sProgCd   			= "";
		String sStocMv   			= "";
		String sWO_MSLAB_RPR_MTD	= "";

		YfCommDAO 	dao = new YfCommDAO();
		JDTORecord 	jtR = dao.getCommonInfo(selectSlabMatirialInfo,new Object[]{sStockId});
    		
    	if(jtR != null)
    	{
    		sProgCd				= StringHelper.evl(jtR.getFieldString("CURR_PROG_CD"), "");
    		sWO_MSLAB_RPR_MTD	= StringHelper.evl(jtR.getFieldString("WO_MSLAB_RPR_MTD"), "");
    	}
    	
    	/* AB열연 진도코드 
		if(YfConstant.CURR_PROG_CD_SLAB_1.equals(sProgCd)){
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_1S;
    	}else if(YfConstant.CURR_PROG_CD_SLAB_3.equals(sProgCd)){
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_3S;
    	}else if(YfConstant.CURR_PROG_CD_SLAB_A.equals(sProgCd)){
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_AS;
    	}else if(YfConstant.CURR_PROG_CD_SLAB_B.equals(sProgCd)){
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_BS;
    	}else if(YfConstant.CURR_PROG_CD_SLAB_C.equals(sProgCd)){
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_CS;
    	}else if(YfConstant.CURR_PROG_CD_SLAB_D.equals(sProgCd)){
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_DS;
    	}else if(YfConstant.CURR_PROG_CD_SLAB_E.equals(sProgCd)){
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_ES;
    	}else if(YfConstant.CURR_PROG_CD_SLAB_F.equals(sProgCd)){
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_FS;
    	
    	}else if(YfConstant.CURR_PROG_CD_SLAB_K.equals(sProgCd)){
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_KS;
		}else if(YfConstant.CURR_PROG_CD_SLAB_L.equals(sProgCd)){
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_LS;
		}else if(YfConstant.CURR_PROG_CD_SLAB_M.equals(sProgCd)){
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_MS;    		    		
    		
    	}else if(YfConstant.CURR_PROG_CD_SLAB_Y.equals(sProgCd)){
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_YS;
    	}else if(YfConstant.CURR_PROG_CD_SLAB_Z.equals(sProgCd)){
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_ZS;
    	}															
    	*/
    	/* 일관제철 진도코드 */
    	if(YfConstant.DMYDR016.equals(TcCode))	
    	{				
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_NS;	//외판슬라브운송지시대기
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_0.equals(sProgCd))
    	{    		
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_11; 
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_1.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_12;	
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_A.equals(sProgCd))
    	{
    		if("Q".equals(sWO_MSLAB_RPR_MTD))
    		{
        		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_D3;	//핸드스카핑작업대기
    		}
    		else
    		{
    			sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_DS;
    		}
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_B.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_ES;
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_C.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_FS;
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_D.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_BS;
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_E.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_CS;
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_F.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_YS;
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_G.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_GS;	//종합판정대기
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_H.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_HS;	//입고대기
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_J.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_JS;	//반납대기
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_K.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_KS;
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_L.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_LS;
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_M.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_MS;    		
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_N.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_NS;    	
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_Y.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_ZS;    	
    	}
    	else if(YfConstant.CURR_PROG_CD_SLAB_Z.equals(sProgCd))
    	{
    		sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_ZS;
    	}															
    	
    	rVal[0] = sProgCd;
    	rVal[1] = sStocMv;
		    	
	    return rVal;
	}
	
	/**
     * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
     * @param data
     * @param name
     * @return
     */
	public String getField(JDTORecord data, String name) 
    {
    	if(data == null)
    	{
    		return "";	
    	}
    	else
    	{
    		return StringHelper.evl(data.getFieldString(name), "").trim();
    	}
    }
	
	///////////////////////
	// YmCommonUtil.java 일부 Method 옮김 시작
	// - 김광철
	/////////////////////
	/**
	 * 날짜 형식을 만들어낸다.
	 * @param getData   yyyymmdd 형식의 일자
	 * @param getChar 구분자.. "."
	 * @return yyyy.mm.dd
	 */
	public String makeDate(String getData, String getChar) {
		String rtnStr = "";
		if(!isEmpty(getData)){
			// 날짜처리
			if(getData.length() != 8){
				rtnStr = getData;
			}
			else{
				rtnStr = getData.substring(0, 4) 
					+ getChar 
					+ getData.substring(4, 6) 
					+ getChar 
					+ getData.substring(6, 8);
			}
		}
		return rtnStr;
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
			getUsrId =  nvl((String) jsession.getAttribute("login_session", "L_USERID"), "") ;
			return getUsrId;
		}catch(Exception e) {
			logger.println(LogLevel.DEBUG, e.getMessage());
			getUsrId = "---";
		}
		return getUsrId;
	}
	
	/**
	 * 날짜형식의 문자열 S 에서 년도 I, 월 J, 일 K 에 값 음수또는 양수 를 넣어 해당 날짜를 얻어낸다.	
	 * @param getData
	 * @param i
	 * @param j
	 * @param k
	 * @return
	 */
	public String getAddDate(String getData,  int k){
		
		String rtnStr = "";
		if(!isEmpty(getData)){
			
			if(getData.trim().length() < 8){
				rtnStr = trim(getData);
			}
			else{
				int l = Integer.parseInt(getData.substring(0, 4));
				int i1 = Integer.parseInt(getData.substring(4, 6));
				int j1 = Integer.parseInt(getData.substring(6, 8));
				Calendar calendar = Calendar.getInstance();
				calendar.set(l, i1 - 1, j1);
				calendar.add(1, 0);
				calendar.add(2, 0);
				calendar.add(5, k);
				
				rtnStr = String.valueOf(calendar.get(1));
				
				if(calendar.get(2) + 1 < 10)
					rtnStr = rtnStr + "0" + (calendar.get(2) + 1);
				else
					rtnStr = rtnStr + (calendar.get(2) + 1);
					
				if(calendar.get(5) < 10)
					rtnStr = rtnStr + "0" + calendar.get(5);
				else
					rtnStr = rtnStr + calendar.get(5);
			}
		} 
			
		return rtnStr;
	}
	
	/**
	 * 날자형식 변경
	 * @param getData : 날자 형식(yyyymmddhhmmss)
	 * @param getChar : 분리자, 분리자를 "" 으로 하면 yyyymmddhhmmss 로 형식 변환.
	 * @return
	 * @throws NullPointerException
	 */
	public String formatDT(String getData, String getChar){
		String rtnData ="";
		
		if( !isEmpty(getData)){
			
			if(getData.length() == 8){
				rtnData = getData.substring(0, 4) 
						+ getChar 
						+ getData.substring(4, 6)
						+ getChar
						+ getData.substring(6, 8);
			
			}else if(getData.length() == 6){
				rtnData = getData.substring(0, 2)
						+ getChar
						+ getData.substring(2, 4)
						+ getChar
						+ getData.substring(4, 6);
			
			}else{
				rtnData = getData ;
			}
		} 
		
		return rtnData;
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
		try{
			if(getParam != null && getParam.size() > 0  ) {
				for(int ii = 0; ii < getParam.size(); ii++) {
					getParamData += nvl((String)getParam.get(ii),"").trim();
					if(ii == 0) {
						setRtnStr = (String) getParam.get(ii)+" ";
					}else {
						setRtnStr += getStr+" "+(String)getParam.get(ii)+( ii==getParam.size()-1 ?"":" " );
					}
				}
				
				if(getParamData.length() <= 0) 
					setRtnStr = getParamData;
			}
		}catch(Exception e){
			setRtnStr = "";
		}
		return setRtnStr;
	}
	
	/**
	 * 숫자형 문자열의 콤마를 제거한다.
	 * @param getData
	 * @return
	 */
	public String replaceFloat(String getData){
		String rtnStr = "";
		try{
			if( !isEmpty(getData)){
				rtnStr = replace(getData, ",", "");
				rtnStr = trim(rtnStr);			
			} 			
		}catch(Exception e){
			rtnStr = "";
		}
		return rtnStr;
	}

	/**
	 * 특정 iitem String을 찾아 다른 String 으로 교체
	 * @param String getData 원래
	 * @param String getChar 
	 * @param String setChar
	 * @return String
	 */
	public String replace(String getData, String getChar, String setChar){
		
		StringBuffer rtnStr = new StringBuffer();;
		try{
			if( !isEmpty(getData)){
				
				int iiTargetLen = getChar.length();
				int ii = 0;
				int ij = 0;

				while (ij > -1){
					ij = getData.indexOf(getChar, ii);
					if (ij > -1){
						rtnStr.append(getData.substring( ii, ij)).append(setChar);
						ii = ij + iiTargetLen;
					}
				}
				rtnStr.append(getData.substring( ii, getData.length()));
			}
		}catch(Exception e){
			rtnStr.append(getData);
		}
		
		return rtnStr.toString();
	}
	
	///////////////////////
	// YmCommonUtil.java 일부 Method 옮김 종료
	// - 김광철
	/////////////////////
	
	/**
	 * [A] 오퍼레이션명 : fillSpZr
	 *
	 * @param String szData			// 변환대상 문자열
	 *        int    nLen 			// 변환 후 목적 문자열 길이
	 *        int    nChgMd			// 변환 방식 (0: 숫자열변환, !0: 문자열변환
	 * @return String 				// 변환 완료 된 문자열
	 * @throws
	 */
	public static String fillSpZr(String szData, int nLen, int nChgMd)
	{
		String szFillData="";
		int i=0;
		int nDataLen =0;

		try
		{
			szFillData	= szData.trim();
			nDataLen	= szFillData.length();
			
			if(nDataLen >= nLen)
			{
				return szFillData.substring(0, nLen);
			}

			for( i = nDataLen; i < nLen; i++ )
			{
				if( nChgMd == 0 )
				{
					szFillData = "0" + szFillData;
				}
				else
				{
					szFillData += " ";
				}

			} // end of for()
		}
		catch(Exception e)
		{
			for(i=0;i<nLen;i++)
			{
				if(nChgMd==0)
				{
					szFillData="0"+szFillData;
				}
				else
				{
					szFillData+=" ";
				}
			} // end of for();
		} // end of try-catch

		return szFillData;
		
	} // end of fillSpZr()
    
	/*
	 * 파일삭제
	 * param : 서버측 파일 경로
	 */
	public static boolean fileDelete(String fileFullName){
		File deleteFile = new File(fileFullName);
		boolean deleteRst = true;
		
		logger.println(LogLevel.INFO,"삭제파일경로 : " + deleteFile.getAbsolutePath());
		
		if(deleteFile.exists()) {
			deleteRst = deleteFile.delete();
			logger.println(LogLevel.INFO,"삭제완료");
		}else{ 
			logger.println(LogLevel.INFO,"삭제대상 파일 존재하지 않음");
		}
		
		return deleteRst;
	}
	
	/*
	 * 
	 */
	public static String getZoneColor(String ydZoneGp){
		String zoneColor = "BBBBBB";
		
		if("AZ".equals(ydZoneGp)){
			zoneColor = "FF9DFF";
		}else if("BZ".equals(ydZoneGp)){
			zoneColor = "AFFFEE";
		}else if("CZ".equals(ydZoneGp)){
			zoneColor = "82F987";
		}else if("DZ".equals(ydZoneGp)){
			zoneColor = "FF8C8C";
		}else if("EZ".equals(ydZoneGp)){
			zoneColor = "FFE846";
		}else if("GZ".equals(ydZoneGp)){
			zoneColor = "74D190";
		}else if("HZ".equals(ydZoneGp)){
			zoneColor = "64AAFF";
		}
		
		return zoneColor;
	}
	
	/**
	 * 카멜표기법으로 변환한다.
	 * @param sStr
	 * @return
	 */
	public static String getCamelCase(String sStr){
		
		if(sStr.indexOf("_") < 0 
				&& Character.isLowerCase(sStr.charAt(0))
		){
			return sStr;
		}
			
		StringBuffer result = new StringBuffer();
		boolean nextUpper = false;
		
		int len = sStr.length();
		for( int i=0; i<len; i++){
			char currentChar = sStr.charAt(i);
			
			if(currentChar == '_'){
				nextUpper = true;
			}
			else{
				if(nextUpper){
					result.append(Character.toUpperCase(currentChar));
					nextUpper = false;
				}
				else{
					result.append(Character.toLowerCase(currentChar));
				}
			}
		}
		
		return result.toString();
		
	} 
	
/***********************************
    PIDEV 개발
***********************************/		
	
	/**
	 * 야드목표행선지구분를 지정한다.
     *
     * @param  String	sItemGp :	제품구분(S:SLAB, C:COIL ,P: 후판 ) ,JDTORecord inRecord
     *
     * @return String
     * @throws  
     */		
	public String[] getYdAimRtGp_PIDEV(String sItemGp, JDTORecord inRecord)
	{
		// 메세지
		String logId = inRecord.getResultCode();
		String szMsg = null;
		String currProgCd = null;
		String ydAimRtGp = null;
		String sYD_AIM_RT_GP2 = "";
		String sHCR_GP = "";
		String sSKINPASS_YN = "";
		
		// 메소드명
		String szMethodName = "getYdAimRtGp_PIDEV";
		String sNextProc = ""; // 다음공정
		String sPlanProc1 = ""; // 열연계획작업코드1

		String[] rVal = new String[2];

		JDTORecord recEditInRecord = JDTORecordFactory.getInstance().create();

		YfCommDAO commDao = new YfCommDAO();

		try 
		{
			// 전문받아서 szRcvTcCode에 대입
			String szRcvTcCode = this.getTcCode(inRecord);
			String sSTL_NO     = this.trim(inRecord.getFieldString("STL_NO"));
			String infoGp      = this.trim(inRecord.getFieldString("INFO_GP"));

			if ("C".equals(sItemGp)) 
			{
				// 수신한 재료번호로 코일공통
				// 읽기***************************************************************************************************
				if (!"".equals(sSTL_NO)) 
				{
					recEditInRecord.setField("STL_NO", sSTL_NO);

					JDTORecordSet loadYdStock = commDao.select(recEditInRecord, getCOILCOMM1,logId, szMethodName, "코일공통검색");

					if (loadYdStock.size() <= 0) 
					{
						szMsg = "코일공통 SELECT Error :: [" + sSTL_NO + "]" + "DO NOT EXIST";
						this.printLog(logId, szMsg, "SL");
						return rVal;
					} 
					else 
					{
						szMsg = inRecord.getFieldString("STL_NO") + " :: 코일공통 SELECT Success :: [" + loadYdStock.size() + "]";
						this.printLog(logId, szMsg, "SL");

						sYD_AIM_RT_GP2 = this.trim(loadYdStock.getRecord(0).getFieldString("YD_AIM_RT_GP2"));
						sHCR_GP        = this.trim(loadYdStock.getRecord(0).getFieldString("HCR_GP"));
						sSKINPASS_YN   = this.trim(loadYdStock.getRecord(0).getFieldString("SKINPASS_YN"));

						// 진도코드 존제여부 체크
						if ("".equals(this.trim(loadYdStock.getRecord(0).getFieldString("CURR_PROG_CD")))) 
						{
							szMsg = "진도코드가  존재  안 함";
							this.printLog(logId, szMsg, "SL");
							return rVal;
						}
						
						sNextProc = this.trim(loadYdStock.getRecord(0).getFieldString("NEXT_PROC"));
						//sNextProc = this.trim(loadYdStock.getRecord(0).getFieldString("NEXT_PROC"));
						sPlanProc1 = this.trim(loadYdStock.getRecord(0).getFieldString("PLAN_PROC1"));
					}

					// 진도코드
					currProgCd = this.trim(loadYdStock.getRecord(0).getFieldString("CURR_PROG_CD"));
				} 
				else 
				{
					// 진도코드
					currProgCd = this.trim(inRecord.getFieldString("CURR_PROG_CD"));
				}

				szMsg = "진도코드::" + currProgCd;
				this.printLog(logId, szMsg, "SL");

				// ***********************************************************//
				if ("M10LMYDJ1011".equals(szRcvTcCode) && ("4".equals(infoGp) ) )  //("DMYDR005".equals(szRcvTcCode)) 
				{
					ydAimRtGp = "K2"; // 출하지시대기
					currProgCd = "K";
				} 
//				else if ("DMYDR020".equals(szRcvTcCode)) 
//				{
//					ydAimRtGp = "L2"; // 운송지시
//					currProgCd = "L";
//				} 
//				else if ("DMYDR023".equals(szRcvTcCode) || "DMYDR060".equals(szRcvTcCode)) 
				else if ("M10LMYDJ1031".equals(szRcvTcCode))					
				{
					ydAimRtGp = "L5"; // 상차지시
					currProgCd = "L";
				} 
				else if ("M10LMYDJ1071".equals(szRcvTcCode)) 
				{
					ydAimRtGp = "M2"; // 출하완료
					currProgCd = "M";
				} 
				else if ("G".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 종합판정대기
				} 
				else if ("I".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 반송대기
				} 
				else if ("H".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 입고대기
				} 
				else if ("Y".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "C"; // 재공충당대기(C열연정정)
				} 
				else if ("B".equals(currProgCd)) 
				{ 
					// 지시대기
					String sWorkProc = "";
					if ("H".equals(sNextProc.substring(1, 2))) 
					{
						ydAimRtGp = currProgCd + "3"; // 지시대기
					} 
					else 
					{
						ydAimRtGp = currProgCd + "4"; // 지시대기
					}
				} 
				else if ("J".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 반납대기
				} 
				else if ("Z".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 제품충당대기
				} 
				else if ("X".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 경매대상선정
				} 
				else if ("E".equals(currProgCd) || "D".equals(currProgCd)) 
				{
					// 재공이송작업대기
					String sWorkProc = "";

					if (!"".equals(sNextProc)) 
					{
						sWorkProc = sNextProc;
					} 
					else 
					{
						sWorkProc = sPlanProc1;
					}
					// 계획공정정보를 가지고 야드행선을 셋팅
					if (sWorkProc.startsWith("1")) 
					{
						ydAimRtGp = "EA";
					} 
					else if (sWorkProc.startsWith("5") || sWorkProc.startsWith("6")) 
					{
						ydAimRtGp = "EB";
					} 
					else if (sWorkProc.startsWith("9S")) 
					{
						ydAimRtGp = "ED";
					} 
					else 
					{
						ydAimRtGp = "EC";
					}
				} 
				else if ("C".equals(currProgCd)) 
				{
					// 정정작업지시대기
					String sWorkProc = "";

					if (!"".equals(sNextProc)) 
					{
						sWorkProc = sNextProc;
					} 
					else 
					{
						sWorkProc = sPlanProc1;
					}

					szMsg = "다음공정(계획공정)::" + sWorkProc;
					this.printLog(logId, szMsg, "SL");

					// 계획공정정보를 가지고 야드행선을 셋팅 _ 추후 다시 셋팅 (C열연만 셋팅 )
					if ("DH".equals(sWorkProc) || "FH".equals(sWorkProc)
							|| "GA".equals(sWorkProc) || "GH".equals(sWorkProc)
							|| "CA".equals(sWorkProc) || "CH".equals(sWorkProc)
							|| "AA".equals(sWorkProc) || "BH".equals(sWorkProc)
							|| "GT".equals(sWorkProc)) 
					{
						ydAimRtGp = "CE";
					} 
					else if ("HH".equals(sWorkProc) || "HK".equals(sWorkProc) || "HR".equals(sWorkProc)) 
					{
						ydAimRtGp = "CF";
					} 
					else if ("EH".equals(sWorkProc) || "EK".equals(sWorkProc) || "ER".equals(sWorkProc)) 
					{
						ydAimRtGp = "CG";
					} 
					else if ("CK".equals(sWorkProc) || "CR".equals(sWorkProc)) 
					{
						ydAimRtGp = "CF";
					} 
					else if ("BK".equals(sWorkProc) || "BR".equals(sWorkProc)) 
					{
						ydAimRtGp = "CF";
					} 
					else if ("AK".equals(sWorkProc) || "AR".equals(sWorkProc)) 
					{
						ydAimRtGp = "CF";
					} 
					else 
					{
						ydAimRtGp = "XX";
					}
					
					if ("F4".equals(sYD_AIM_RT_GP2) || "F5".equals(sYD_AIM_RT_GP2)) 
					{ 
						// 재작업인 경우
						ydAimRtGp = sYD_AIM_RT_GP2; // 재작업인(C열연정정)
					}
				} 
				else if ("F".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "3"; // 판정보류
				}

				// 2pass재 작업 대상
				if ("Z".equals(sSKINPASS_YN) && ("C".equals(currProgCd) || "D".equals(currProgCd))) 
				{
					ydAimRtGp = "EA";
				}
			} 
		} 
		catch (Exception e) 
		{
			szMsg = "야드목표행선지구분 예외발생! 예외메세지: " + e.getMessage();
			this.printErrorLog(logId, szMethodName, szMsg, this, e);

		}

		szMsg = "진도코드: " + currProgCd+" 야드목표행선지구분: " + ydAimRtGp;
		this.printLog(logId, szMsg, "SL");
 
		rVal[0] = ydAimRtGp;
		rVal[1] = currProgCd;
		return rVal;
	}		
}
