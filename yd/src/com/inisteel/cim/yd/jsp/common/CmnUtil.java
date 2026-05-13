package com.inisteel.cim.yd.jsp.common;

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
import java.util.Set;

import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import kr.co.gtone.bre.extend.CommCodeMng;
import xlib.cmc.GridData;
import xlib.cmc.GridHeader;

public class CmnUtil {
	private static Logger logger = new Logger("yd");
	
	//슬라브 비중
	public static final double SLAB_GRAVITY = 7.82; 

	//코일 비중
	public static final double COIL_GRAVITY = 7.85; 

	/**
	 * 문자열이 null 일때 임의의 문자열을 반환한다.
	 * @param value
	 * @param defaultValue
	 * @return
	 */
    public static String nvl(String value, String defaultValue) {
    	return (value == null || "".equals(value.trim()) || "null".equals(value.trim())) ? defaultValue : value;
    }

    public static String nvl(Object o, String defaultValue) {
        return (o == null) ? defaultValue.trim() : o.toString().trim();
    }
    
    /**
     * 페이징 처리 변수 가져오기
     */
    public static int[] getCurrRow(GridData gdData) throws Exception {
    	try {
	    	return getCurrRow(gridDataTojdtoRecord(gdData));
    	} catch(Exception e) {
    		throw e;
    	}
    }
    
    public static int[] getCurrRow(JDTORecord record) throws Exception {
    	try {
	    	int viewRows  = Integer.parseInt(nvl(record.getFieldString("viewRows"), "10")); //ROW 갯수
			int currpage  = Integer.parseInt(nvl(record.getFieldString("viewPage"), "1")); //현재 페이지   
			int startRow = (currpage-1) * viewRows + 1;
	 		int endRow   = currpage * viewRows;
	    	
	    	return new int[]{startRow, endRow};
		} catch(Exception e) {
			throw e;
		}
    }
    
    //해쉬맵의 내용을 GridData의 파라미터로 담는다.
    public static GridData hashMapToGridData(HashMap inMap) throws Exception {
    	GridData returnGridData = new GridData();

    	if(inMap == null || inMap.size() == 0)
    		return returnGridData;

    	try{
			java.util.Set set = inMap.keySet();
			java.util.Iterator iterator = set.iterator();

			String key = "";
			while(iterator.hasNext()) {
				key = ""+iterator.next();
				returnGridData.addParam(""+key, nvl(inMap.get(key), ""));
			}

	    	return returnGridData;
    	} catch(Exception e) {
    		throw e;
    	}
    }

    //해쉬맵의 내용을 JDTORecord로 담는다.
    public static JDTORecord hashMapTojdtoRecord(HashMap inMap) throws Exception {
    	JDTORecord returnJRecord = JDTORecordFactory.getInstance().create();

    	if(inMap == null || inMap.size() == 0)
    		return returnJRecord;

    	try{
			java.util.Set set = inMap.keySet();
			java.util.Iterator iterator = set.iterator();

			String key = "";
			while(iterator.hasNext()) {
				key = ""+iterator.next();
				returnJRecord.addField(""+key, nvl(inMap.get(key), ""));
			}

	    	return returnJRecord;
    	} catch(Exception e) {
    		throw e;
    	}
    }

    //해쉬맵의 내용을 JDTORecord로 담는다.
    public static JDTORecord gridDataTojdtoRecord(GridData gdData) throws Exception {
		JDTORecord rowJrecord = JDTORecordFactory.getInstance().create();

    	if(gdData == null) return rowJrecord;

    	try {
			String params[] = gdData.getParamNames();
			for (int ii = 0; ii < params.length; ii++) {
				rowJrecord.addField(params[ii], nvl(gdData.getParam(params[ii]), ""));
			}

			return rowJrecord;
		} catch(Exception e) {
			throw e;
		}
    }

    //해쉬맵의 내용을 JDTORecord로 담는다.
    public static HashMap jdtoRecordTohashMap(JDTORecord inJRecord) throws Exception {
    	HashMap returnMap = new HashMap();

    	if(inJRecord == null || inJRecord.size() == 0)
    		return returnMap;

    	try{
			java.util.Iterator iterator = inJRecord.iterateName();

			String key = "";
			while(iterator.hasNext()) {
				key = (String)iterator.next();

				returnMap.put(key, nvl(inJRecord.getField(key), ""));
			}

	    	return returnMap;
    	} catch(Exception e) {
    		throw e;
    	}
    }

    //List의 JDTORecord를 HashMap으로 변환한다.
    public static List listJdtoRecordTohashMap(List inDataList) throws Exception {
    	List returnList = new ArrayList();

    	if(inDataList == null || inDataList.size() == 0)
    		return returnList;

    	try{
    		for(int ii=0; ii<inDataList.size(); ii++) {
    			returnList.add(jdtoRecordTohashMap((JDTORecord)inDataList.get(ii)));
    		}

	    	return returnList;
    	} catch(Exception e) {
    		throw e;
    	}
    }

    /**
     * 모든 문자열을 치환한다.
     * @param Source
     * @param FindText
     * @param ReplaceText
     * @return
     */
    public static String replaceAll(String Source, String FindText, String ReplaceText) {
    	String rtnSource = "";
    	if(Source == null || "".equals(Source)){
    		return rtnSource;
    	} else {
    		try {
    			String rtnCheck  = "";
    			int FindLength     = Source.length();
    			int FindTextLength = FindText.length();
    			char SourceArrayChar[] = new char[FindLength];
    			SourceArrayChar = Source.toCharArray();
    			for (int i = 0; i < FindLength; i++) {
    				rtnCheck = Source.substring(i, i + 1);
    				int j = i;
    				if (FindLength - j >= FindTextLength)
    					rtnCheck = Source.substring(j, j + FindTextLength);
    				else
    					rtnCheck = Source.substring(j, j + 1);

    				if (rtnCheck.equals(FindText)) {
    					rtnSource += ReplaceText;
    					i += (FindTextLength - 1);
    				} else {
    					rtnSource += SourceArrayChar[i];
    				}
    			}

    			return rtnSource;
    		} catch (Exception e){
    			return Source;
    		}
    	}
    }

    /**
     * 입력값을 원하는 포멧으로 변화하는 메소드
     * @param no
     * @param formatter
     * @return
     */
    public static String format(String no, String formatter){
        try {
            return format(Double.parseDouble(no), formatter);
        } catch(NumberFormatException nfe) {
            return "";
        } catch(Exception e) {
            return "";
        }
    }

    public static String format(int no, String formatter) {
        return format(no, formatter);
    }

    public static String format(float no, String formatter) {
        return format(no, formatter);
    }

    public static String format(long no, String formatter) {
        DecimalFormat df = new DecimalFormat(formatter);
        return df.format(no);
    }

    public static String format(double no, String formatter) {
        DecimalFormat df = new DecimalFormat(formatter);
        return df.format(no);
    }

 //************************************** WISEGRID **************************************

    /*
     * WiseGrid 관련 Helper Method
     * 디비 결과값 List를 GridData로 변환한다.
     * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
     */
    public static GridData jdtoRecordToGridData(GridData returnGrid, List dataList) throws Exception {
    	return jdtoRecordToGridData(returnGrid, dataList, null, null);
    }

    /*
     * WiseGrid 관련 Helper Method
     * 디비 결과값JDTORecord를 GridData로 변환한다.
     * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
     */
    public static GridData jdtoRecordToGridData(GridData returnGrid, JDTORecord dataJrecord) throws Exception {
    	return jdtoRecordToGridData(returnGrid, dataJrecord, null, null);
    }

    /*
     * WiseGrid 관련 Helper Method
     * 디비 결과값 List를 GridData로 변환한다.
     * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
     */
    public static GridData jdtoRecordToGridData(GridData returnGrid, List dataList, GridData gdReq) throws Exception {
    	return jdtoRecordToGridData(returnGrid, dataList, gdReq, null);
    }

    public static GridData jdtoRecordToGridData(GridData returnGrid, JDTORecordSet dataSet, GridData gdReq) throws Exception {
    	return jdtoRecordToGridData(returnGrid, dataSet.toList(), gdReq, null);
    }
    
    public static GridData jdtoRecordToGridData(GridData returnGrid, JDTORecordSet dataSet, GridData gdReq, String numberType) throws Exception {
    	return jdtoRecordToGridData(returnGrid, dataSet.toList(), gdReq, numberType);
    }

    /*
     * WiseGrid 관련 Helper Method
     * 디비 결과값JDTORecord를 GridData로 변환한다.
     * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
     */
    public static GridData jdtoRecordToGridData(GridData returnGrid, JDTORecord dataJrecord, GridData gdReq) throws Exception {
    	return jdtoRecordToGridData(returnGrid, dataJrecord, gdReq, null);
    }

    public static GridData jdtoRecordToGridData(GridData returnGrid, List dataList, GridData gdReq, String numberType) throws Exception {
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
		JDTORecord dataJrecord = null;

		try {
			GridHeader[] gridHeader = returnGrid.getHeaders();
			String headerName		= "";
			String dataType			= "";

			if(dataList == null || dataList.size() == 0) {
				returnGrid.addParam("TOTAL", "0");
				//returnGrid.addParam("SELECT_MSG", MessageHelper.getUserMessage("MSG0103", new String[]{""}, ""));
			} else {
				String totCount = "0";
				for(int ii=0; ii<gridHeader.length; ii++) {
					headerName = gridHeader[ii].getID();
					dataType = gridHeader[ii].getDataType();

					/*
					 * 컬럼에 맞게 데이타를 세팅한다.
					 * SEQNO, CHECK, CRUD은  디비에서 가져오지 않는다. 화면에서 가져오는 값이다. 화면에 없다면..패스.
					 */
					if("SEQNO".equals(headerName)) {
						for(int kk=0; kk<dataList.size(); kk++) {
							returnGrid.getHeader("SEQNO").addValue("" + (kk + 1), "");
						}
					} else if("CHECK".equals(headerName)) {
						for(int kk=0; kk<dataList.size(); kk++) {
							returnGrid.getHeader("CHECK").addValue("0", "");
						}
					} else if("CRUD".equals(headerName)) {
						for(int kk=0; kk<dataList.size(); kk++) {
							returnGrid.getHeader("CRUD").addValue("R","R");
						}
					} else {
						for(int jj=0; jj<dataList.size(); jj++) {
							dataJrecord = (JDTORecord)dataList.get(jj);

							/*
							 * 페이징 처리를 했었을 경우 total row값이 들어가는 파라미터를 생성해 줘야 한다.
							 */
							if("0".equals(totCount)) {
								totCount = StringHelper.evl(dataJrecord.getFieldString("TOTAL"), "0");
							}

							if("L".equals(dataType)) {
								//t_combo 일때...
								returnGrid.getHeader(headerName).addSelectedHiddenValue(StringHelper.evl(dataJrecord.getFieldString(headerName), ""));
							} else if("C".equals(dataType)) {
								//t_checkbox 일때...
								if(StringHelper.evl(dataJrecord.getFieldString(headerName), "").length() > 0){
									if(dataJrecord.getFieldString(headerName).charAt(0) == '0' || dataJrecord.getFieldString(headerName).charAt(0) == '1') {
										returnGrid.getHeader(headerName).addValue(""+dataJrecord.getFieldString(headerName).charAt(0), "");
									} else {
										//언체크로 세팅(0)
										returnGrid.getHeader(headerName).addValue("0", "");
									}
								} else {
									//언체크로 세팅(0)
									returnGrid.getHeader(headerName).addValue("0", "");
								}
							} else if("R".equals(dataType)) {
								//t_radio 일때... 어떻게 쓰이는지 모름...
								//0, 1 이 아니면 에러가 남....에러가 나지 않도록...세팅함
								if(StringHelper.evl(dataJrecord.getFieldString(headerName), "").length() > 0){
									if(dataJrecord.getFieldString(headerName).charAt(0) == '0' || dataJrecord.getFieldString(headerName).charAt(0) == '1') {
										returnGrid.getHeader(headerName).addValue(""+dataJrecord.getFieldString(headerName).charAt(0), "");
									} else {
										//0으로 세팅
										returnGrid.getHeader(headerName).addValue("0", "");
									}
								} else {
									//0으로 세팅
									returnGrid.getHeader(headerName).addValue("0", "");
								}
							} else if("D".equals(dataType)) {
								//t_date 일때...YYYYMMDD 형식이 아니면 에러가 떨어짐.
								if(StringHelper.evl(dataJrecord.getFieldString(headerName), "").length() > 10){
									returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(dataJrecord.getFieldString(headerName).substring(0, 10), "-", ""), "");
								} else if(StringHelper.evl(dataJrecord.getFieldString(headerName), "").length() == 10 || StringHelper.evl(dataJrecord.getFieldString(headerName), "").length() == 8){
									returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(dataJrecord.getFieldString(headerName), "-", ""), "");
								} else {
									returnGrid.getHeader(headerName).addValue("", "");
								}
							} else if("I".equals(dataType)) {
								//t_imagetext 일때...
								returnGrid.getHeader(headerName).addValue(StringHelper.evl(dataJrecord.getFieldString(headerName), ""), StringHelper.evl(dataJrecord.getFieldString(headerName), ""), 0);
							} else if("N".equals(dataType)) {
								//t_number 일때 값이 0이면  space를 전송한다.
								if(!"number".equals(numberType)) {
									if(!"0".equals(StringHelper.evl(dataJrecord.getFieldString(headerName), ""))){
										returnGrid.getHeader(headerName).addValue(StringHelper.evl(dataJrecord.getFieldString(headerName), ""), "");
									} else {
										returnGrid.getHeader(headerName).addValue("", "");
									}
								} else {
									returnGrid.getHeader(headerName).addValue(StringHelper.evl(dataJrecord.getFieldString(headerName), ""), "");
								}
							} else {
								returnGrid.getHeader(headerName).addValue(StringHelper.evl(dataJrecord.getFieldString(headerName), ""), "");
							}
						}//for
					}//if
				}//for

				/*
				 * 페이징 처리를 했었을 경우 total row값이 들어가는 파라미터를 생성해 줘야 한다.
				 */
				//total row 세팅..
				returnGrid.addParam("TOTAL", totCount);
			}

			/*
			 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
			 */
			if(gdReq != null) {
				String params[] = gdReq.getParamNames();

				for(int ii=0; ii<params.length; ii++) {
					returnGrid.addParam(params[ii], StringHelper.evl(gdReq.getParam(params[ii]), ""));
				}
			}

			return returnGrid;
		} catch(Exception e) {
			throw e;
		}
    }

    public static GridData jdtoRecordToGridData(GridData returnGrid, JDTORecord dataJrecord, GridData gdReq, String numberType) throws Exception {
    	try {
			GridHeader[] gridHeader = returnGrid.getHeaders();
			String headerName		= "";
			String dataType			= "";
			/*
			 * 컬럼에 맞게 데이타를 세팅한다.
			 * SEQ_NO, SELECTED은 따로 생성한다. 이 두개의 컬럼은 디비에서 가져오지 않는다.
			 */
			if(dataJrecord == null || dataJrecord.size() == 0) {
				returnGrid.addParam("TOTAL", "0");
				//returnGrid.addParam("SELECT_MSG", MessageHelper.getUserMessage("MSG0103", new String[]{""}, ""));
			} else {
				for(int ii=0; ii<gridHeader.length; ii++) {
					headerName = gridHeader[ii].getID();
					dataType = gridHeader[ii].getDataType();

					/*
					 * 페이징 처리를 했었을 경우 total row값이 들어가는 파라미터를 생성해 줘야 한다.
					 */
					if(ii==0) {
						//total row 세팅..
						returnGrid.addParam("TOTAL", StringHelper.evl(dataJrecord.getFieldString("TOTAL"), "0"));
					}
					
					/*
					 * SEQNO, CHECK, CRUD은  디비에서 가져오지 않는다. 화면에서 가져오는 값이다. 화면에 없다면..패스.
					 */
					if("SEQNO".equals(headerName)) {
						returnGrid.getHeader("SEQNO").addValue("1", "");
					} else if("CHECK".equals(headerName)) {
						returnGrid.getHeader("CHECK").addValue("0", "");
					} else if("CRUD".equals(headerName)) {
						returnGrid.getHeader("CRUD").addValue("R", "R");
					} else {
						/*
						 * 컬럼에 맞게 데이타를 세팅한다.
						 */
						if("L".equals(dataType)) {
							//t_combo 일때...
							returnGrid.getHeader(headerName).addSelectedHiddenValue(StringHelper.evl(dataJrecord.getFieldString(headerName), ""));
						} else if("C".equals(dataType)) {
							//t_checkbox 일때...
							if(StringHelper.evl(dataJrecord.getFieldString(headerName), "").length() > 0){
								if(dataJrecord.getFieldString(headerName).charAt(0) == '0' || dataJrecord.getFieldString(headerName).charAt(0) == '1') {
									returnGrid.getHeader(headerName).addValue(""+dataJrecord.getFieldString(headerName).charAt(0), "");
								} else {
									//언체크로 세팅(0)
									returnGrid.getHeader(headerName).addValue("0", "");
								}
							} else {
								//언체크로 세팅(0)
								returnGrid.getHeader(headerName).addValue("0", "");
							}
						} else if("R".equals(dataType)) {
							//t_radio 일때... 어떻게 쓰이는지 모름...
							//0, 1 이 아니면 에러가 남....에러가 나지 않도록...세팅함
							if(StringHelper.evl(dataJrecord.getFieldString(headerName), "").length() > 0){
								if(dataJrecord.getFieldString(headerName).charAt(0) == '0' || dataJrecord.getFieldString(headerName).charAt(0) == '1') {
									returnGrid.getHeader(headerName).addValue(""+dataJrecord.getFieldString(headerName).charAt(0), "");
								} else {
									//0으로 세팅
									returnGrid.getHeader(headerName).addValue("0", "");
								}
							} else {
								//0으로 세팅
								returnGrid.getHeader(headerName).addValue("0", "");
							}
						} else if("D".equals(dataType)) {
							//t_date 일때...YYYYMMDD 형식이 아니면 에러가 떨어짐.
							if(StringHelper.evl(dataJrecord.getFieldString(headerName), "").length() > 10){
								returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(dataJrecord.getFieldString(headerName).substring(0, 10), "-", ""), "");
							} else if(StringHelper.evl(dataJrecord.getFieldString(headerName), "").length() == 10 || StringHelper.evl(dataJrecord.getFieldString(headerName), "").length() == 8){
								returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(dataJrecord.getFieldString(headerName), "-", ""), "");
							} else {
								returnGrid.getHeader(headerName).addValue("", "");
							}
						} else if("I".equals(dataType)) {
							//t_imagetext 일때...
							returnGrid.getHeader(headerName).addValue(StringHelper.evl(dataJrecord.getFieldString(headerName), ""), StringHelper.evl(dataJrecord.getFieldString(headerName), ""), 0);
						} else if("N".equals(dataType)) {
							//t_number 일때 값이 0이면  space를 전송한다.
							if(!"number".equals(numberType)) {
								if(!"0".equals(StringHelper.evl(dataJrecord.getFieldString(headerName), ""))){
									returnGrid.getHeader(headerName).addValue(StringHelper.evl(dataJrecord.getFieldString(headerName), ""), "");
								} else {
									returnGrid.getHeader(headerName).addValue("", "");
								}
							} else {
								returnGrid.getHeader(headerName).addValue(StringHelper.evl(dataJrecord.getFieldString(headerName), ""), "");
							}
						} else {
							returnGrid.getHeader(headerName).addValue(StringHelper.evl(dataJrecord.getFieldString(headerName), ""), "");
						}
					}
				}//for
			}

			/*
			 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
			 */
			if(gdReq != null) {
				String params[] = gdReq.getParamNames();

				for(int ii=0; ii<params.length; ii++) {
					returnGrid.addParam(params[ii], StringHelper.evl(gdReq.getParam(params[ii]), ""));
				}
			}

			return returnGrid;
		} catch(Exception e) {
			throw e;
		}
    }


    /*
     * Pk로 조회된 값을 GridData의 파리미터로 세팅한다.
     * jsp에서는 event="EndQuery()"에서 GridObj.GetParam(key)으로 받으면 된다.
     */
    public static GridData jdtoRecordToGridParam(JDTORecord dataJrecord) throws Exception {
    	GridData returnGrid = null;

    	try {
    		returnGrid = new GridData();

    		java.util.Iterator iterator = dataJrecord.iterateName();
    		String key = "";
    		while(iterator.hasNext()) {
    			key = ""+iterator.next();
    			returnGrid.addParam(""+key, ""+dataJrecord.getField(key));
    		}

			return returnGrid;
		} catch(Exception e) {
			throw e;
		}
    }

    /*
     * Pk로 조회된 값을 GridData의 파리미터로 세팅한다.(기존의 Grid에 추가하고싶을때)
     * jsp에서는 event="EndQuery()"에서 GridObj.GetParam(key)으로 받으면 된다.
     */
    public static GridData jdtoRecordToGridParam(JDTORecord dataJrecord, GridData returnGrid) throws Exception {
    	try {
    		java.util.Iterator iterator = dataJrecord.iterateName();
    		String key = "";
    		while(iterator.hasNext()) {
    			key = ""+iterator.next();
    			returnGrid.addParam(""+key, ""+dataJrecord.getField(key));
    		}

			return returnGrid;
		} catch(Exception e) {
			throw e;
		}
    }

    /*
     * GridData 의 내용을 List로 변환한다.
     */
    public static List GridDataToList(GridData dataGrid) throws Exception {
    	List returnList = new ArrayList();
		JDTORecord rowJrecord = null;

    	if(dataGrid == null) return returnList;

    	try {
			GridHeader[] gridHeaders = dataGrid.getHeaders();

			for(int ii=0; ii<gridHeaders[0].getRowCount(); ii++) {
				rowJrecord = JDTORecordFactory.getInstance().create();
				for(int jj=0; jj<gridHeaders.length; jj++) {
					rowJrecord.addField( gridHeaders[jj].getID(), StringHelper.evl(gridHeaders[jj].getValue(ii), "").trim() );
				}

				returnList.add(rowJrecord);
			}

			return returnList;
		} catch(Exception e) {
			throw e;
		}
    }

    /*
     * executeBatch를 사용했을 경우 성공여부를 리턴하는 메소드
     * 사용 예)
     * int[] results = new CommonDAO.executeBatch(?, ?);
     * if(isBatchSuccess(results)){ 성공 } else { 실패 };
     */
    public static boolean isBatchSuccess(int[] results) {
    	if(results == null || results.length == 0) return false;

    	boolean result = true;

        for(int i=0; i < results.length; i++) {
            if(results[i] == -3) {
            	result = false;
            	break;
            }
        }

        return result;
    }

    /*
     * 20070402의 날짜포멧을 원하는 구분자로 바꾸고 싶을때..
     * 사용 예)
     * CmnUtil.addDateGubunStr("20070405", "-")
     * 6자리일때도 가능하게 추가(200705 -> 2007-05)
     */
    public static String addDateGubunStr(String src, String gubun) {
		String temp = StringHelper.replaceStr(StringHelper.replaceStr(src, "/", ""), ".", "");
		if(temp.length() == 8) {
			return temp.substring(0, 4) + gubun + temp.substring(4, 6) + gubun + temp.substring(6);
		} else if (temp.length() == 6) {
			return temp.substring(0, 4) + gubun + temp.substring(4, 6);
		} else {
			return src;
		}
	}

    /**
     * 컬렉션으로 넘어오는 파라미터를 보기좋게 찍을때 사용한다.
     *
     * @param methodName
     * @param obj
     * @throws Exception
     */
	public static void setEjbParamLog(String methodName, Object obj) throws Exception {
		if(obj == null) {
			return;
		}

		logger.println(LogLevel.DEBUG, "▒▒▒ <<"+methodName+">> PARAM Info Start ▒▒▒");

		Set set			  = null;
		Iterator iterator = null;
		String key		  = "";

		JDTORecord paramJRecord = null;
		HashMap paramMap = null;

		if(obj instanceof JDTORecord) {
			paramJRecord = (JDTORecord)obj;
			iterator = paramJRecord.iterateName();
			while(iterator.hasNext()) {
				key = (String)iterator.next();
				logger.println(LogLevel.DEBUG, "▒▒▒ "+key+" : [" + paramJRecord.getField(key)+"]");
			}
		} else if(obj instanceof HashMap) {
			paramMap = (HashMap)obj;
			set = paramMap.keySet();
			iterator = set.iterator();
			while(iterator.hasNext()) {
				key = (String)iterator.next();
				logger.println(LogLevel.DEBUG, "▒▒▒ "+key+" : [" + paramMap.get(key)+"]");
			}
		} else {
			logger.println(LogLevel.DEBUG, "▒▒▒ obj : [" + obj.toString()+"]");
		}

		logger.println(LogLevel.DEBUG, "▒▒▒ <<"+methodName+">> PARAM Info End   ▒▒▒");
	}

    /*
     * 소재설계 - 포항 PO-NO 체번하는 메소드
     * 공장(1) + 년(1) + 월(1) + SEQ(3)
     */
    public static String getPOYear(String yyyy) {
		String[] alpha = {"A", "B", "C", "D", "E",
				  		  "F", "G", "H", "I", "J",
				  		  "K", "L", "M", "N", "O",
				  		  "P", "Q", "R", "S", "T",
				  		  "U", "V", "W", "X", "Y",
				  		  "Z"};
		int start = 0;
		int limit = 25;
		for(int ii=2000; ii<5000; ii++) {
			if(start > limit) {
				start = 0;
			}

			if(ii == Integer.parseInt(CmnUtil.nvl(yyyy, new java.text.SimpleDateFormat("yyyy").format(new java.util.Date(System.currentTimeMillis()))))) break;

			start++;
		}

		return alpha[start];
    }

    /*
     * 소재설계 - 포항 PO-NO 체번하는 메소드
     * 공장(1) + 년(1) + 월(1) + SEQ(3)
     * in_mm은 06 이거나...6으로 들어와도..된다.
     */
    public static String getPOMonth(String in_mm) {
		String[] alpha = {"A", "B", "C", "D", "E",
				  		  "F", "G", "H", "I", "J",
				  		  "K", "L"};
		String mm = "";
		for(int ii=0; ii<12; ii++) {
			if(ii == (Integer.parseInt(CmnUtil.nvl(in_mm, new java.text.SimpleDateFormat("MM").format(new java.util.Date(System.currentTimeMillis())))) - 1)){
				mm = alpha[ii];
				break;
			}
		}

		return mm;
    }

    /*
     * 소재길이 절삭하는 메소드
     */
    public static String getMatlLenTrunc(String bizOffice, String plant_gp, String in_matl_len) {
        // 4125mm -> 4150mm
        // 4124mm -> 4100mm
        
    	double cal_matl_len = 0;
    	double matl_len = Double.parseDouble(nvl(in_matl_len, "0"));

    	//8886으로 테스트..해봄..
    	if("I".equals(bizOffice) && "5".equals(plant_gp)){
    		// 인천 대형공장
    		// 인천 대형의 경우 50mm 반올림이다.
            // 원리 -
            // 4125mm * 2 = double 8250 / 100 = 82.50 Round -> 83 * 100 = 8300 / 2 = 4150
            // 4124mm * 2 = double 8248 / 100 = 82.48 Round -> 82 * 100 = 8200 / 2 = 4100
    		cal_matl_len = matl_len * 2;
    		cal_matl_len = cal_matl_len / 100.0;
    		cal_matl_len = Math.round(cal_matl_len);
    		cal_matl_len = cal_matl_len * 100;
    		cal_matl_len = cal_matl_len / 2;
    	} else if("I".equals(bizOffice) && "6".equals(plant_gp)){
    		// 인천 중형의 경우 10mm 반올림이다.
            // 4125mm -> 4130mm
            // 4124mm -> 4120mm
            	
            // 원리 -
            // 4125mm = 4125 / 10 = 412.5 Round -> 413 * 10 =  = 4130
            // 4124mm = 4124 / 10 = 412.4 Round -> 412 * 10 =  = 4120
    		cal_matl_len = matl_len / 10.0;
    		cal_matl_len = Math.round(cal_matl_len);
    		cal_matl_len = cal_matl_len * 10;
    	} else if("P".equals(bizOffice) && "U".equals(plant_gp)){
    		// 포항 대형의 경우 100mm 반올림이다.
            // 4155mm -> 4200mm
            // 4144mm -> 4100mm

            // 원리 -
            // 4155mm = 4155 / 100 = 41.55 Round -> 42 * 100 =  = 4200
            // 4124mm = 4144 / 100 = 41.44 Round -> 41 * 100 =  = 4100
    		cal_matl_len = matl_len / 100.0;
    		cal_matl_len = Math.round(cal_matl_len);
    		cal_matl_len = cal_matl_len * 100;
    	} else if("P".equals(bizOffice) && "M".equals(plant_gp)){
    		// 포항 중형의 경우 10mm 반올림이다.
            // 4125mm -> 4130mm
            // 4124mm -> 4120mm

            // 원리 -
            // 4125mm = 4125 / 10 = 412.5 Round -> 413 * 10 =  = 4130
            // 4124mm = 4124 / 10 = 412.4 Round -> 412 * 10 =  = 4120
    		cal_matl_len = matl_len / 10.0;
    		cal_matl_len = Math.round(cal_matl_len);
    		cal_matl_len = cal_matl_len * 10;
    	} else {
    		cal_matl_len = matl_len;
    	}

    	return ""+cal_matl_len;
    }

    /*
   * 필드별 전문 생성시 정해진 전문 길이와 맞게 데이터를 가공(오른쪽에 " "값으로 채움)
   * @param getParam  필드의 현재 값 - 가공전
   * @param getLength TC 필드의 원 길이 - 가공후 이 길이가 되어야 함.
   * @return TC 필드의 의 원래 길이로 가공하여 Return
   */
	public static String getTcTableR(String getParam, int getLength){
		String setTcTable = "";   // 완성된 필드의 String
		int chkLength = 0;        // TC 필드의 길이 - 현재 값의 길이
		if(getParam.length() <= getLength){
			if(getParam.length() <= 0){
				chkLength = getLength;
			}else{
				chkLength = getLength - getParam.length();
			}
			for(int ii = 0; ii < chkLength; ii++){
				setTcTable += " ";
			}
			setTcTable = getParam + setTcTable  ;
		}
		return setTcTable.toUpperCase();
	}

  /*
   * 필드별 전문 생성시 정해진 전문 길이와 맞게 데이터를 가공(왼쪽에 "0"값으로 채움)
   * @param getParam  필드의 현재 값 - 가공전
   * @param getLength TC 필드의 원 길이 - 가공후 이 길이가 되어야 함.
   * @return TC 필드의 의 원래 길이로 가공하여 Return
   */
	public static String getTcTableL(String getParam, int getLength){
		String setTcTable = "";   // 완성된 필드의 String
		int chkLength = 0;        // TC 필드의 길이 - 현재 값의 길이
		if(getParam.length() <= getLength){
			if(getParam.length() <= 0){
				chkLength = getLength;
			}else{
				chkLength = getLength - getParam.length();
			}
			
			for(int ii = 0; ii < chkLength; ii++){
				setTcTable += "0";
			}
			
			setTcTable = setTcTable + getParam;
		}
		
		return setTcTable.toUpperCase();
	}

	 /*
     * 포항에서 넘어오는 공장3자리를 받아서 뒤에 1자리를 매치데이타와 매치하여 인천공장코드로 변환한다.
     * String 값을 리턴한다.
     * String PLANT_GP 	: 	부모창에서 넘겨준 코드NAME(구분자 ',')
     */
    public static String getPohangPlantInchonconver(String PLANT_GP) throws Exception {
    	String returnPlantGP	=	null;

    	try{
    		String PohangPLANT_GP	=	PLANT_GP.substring(2,3);

    		if("2".equals(PohangPLANT_GP)) {
    			returnPlantGP	=	"R";
    		} else if("3".equals(PohangPLANT_GP)) {
    			returnPlantGP	=	"P";
    		} else if("4".equals(PohangPLANT_GP)) {
    			returnPlantGP	=	"U";
    		} else if("5".equals(PohangPLANT_GP)) {
    			returnPlantGP	=	"M";
    		} else {
    			returnPlantGP	=	PohangPLANT_GP;
    		}

    		return returnPlantGP;
    	} catch (Exception e) {
    		return " ";
		}
    }
    
    /*
     * 포항에서 넘어오는 공장3자리를 받아서 뒤에 1자리를 매치데이타와 매치하여 인천공장코드로 변환한다.
     * String 값을 리턴한다.
     * String HEAT_NO
     */
    public static String getPohangPlantInchonconver_1(String HEAT_NO) throws Exception {
    	String returnPlantGP	=	null;

    	try{
    		String PohangPLANT_GP	=	HEAT_NO.substring(0,1);

    		if("1".equals(PohangPLANT_GP)) {
    			returnPlantGP	=	"F";
    		} else if("2".equals(PohangPLANT_GP)) {
    			returnPlantGP	=	"G";
    		} else if("3".equals(PohangPLANT_GP)) {
    			returnPlantGP	=	"H";
    		} else if("4".equals(PohangPLANT_GP)) {
    			returnPlantGP	=	"J";
    		} else {
    			returnPlantGP	=	PohangPLANT_GP;
    		}

    		return returnPlantGP;
    	} catch (Exception e) {
    		return " ";
		}
    }

    /*
     * 포항에서 넘어오는 공장3자리를 받아서 뒤에 1자리를 매치데이타와 매치하여 인천공장코드로 변환한다.
     * String 값을 리턴한다.
     * String PLANT_GP 	: 	부모창에서 넘겨준 코드NAME(구분자 ',')
     */
    public static String getInchonPlantInchonconver(String PLANT_GP) throws Exception {
    	String returnPlantGP	=	null;

    	try{
    		String inchonPLANT_GP	=	PLANT_GP.substring(2,3);

    		if("R".equals(inchonPLANT_GP)) {
    			returnPlantGP	=	"2";
    		} else if("P".equals(inchonPLANT_GP)) {
    			returnPlantGP	=	"3";
    		} else if("U".equals(inchonPLANT_GP)) {
    			returnPlantGP	=	"4";
    		} else if("M".equals(inchonPLANT_GP)) {
    			returnPlantGP	=	"5";
    		} else {
    			returnPlantGP	=	inchonPLANT_GP;
    		}

    		return returnPlantGP;
    	} catch (Exception e) {
    		throw e;
		}
    }

    /*
     *메서드명 : getCalsDate
     *메서드 기능 : 원하는 시점의 날짜를 찾는다.
     *PARAM : string, int
     *     getCalsDate(0, 1) :오늘
     *     getCalsDate(1, 1) :년, -1(1년전 오늘),-2(2년전 오늘)
     *     getCalsDate(2, 1) :개월, -1(1개월전 오늘),-2(2개월전 오늘), 1(1개월후 오늘)
     *     getCalsDate(3 or 4 or 8,1) :주, -1(일주일전 같은요일), 1(1주일후 같은요일)
     *     getCalsDate(5 or 6 or 7,1) :하루, -1(오늘부터 하루전), 1(오늘부터 하루후)
     *     getCalsDate(9, 1) :12시간, -1(12시간전) 1(12시간후) 2(24시간후
     *PARAM date_type : 출력을 원하는 날짜형식 ex) "yyyyMMdd", "yyyy-MM-dd"
     *RETURN VALUE : string
     */
	public static String getCalsDate(String yyddtt, int y, int z, String date_type) throws Exception {

		int year = 0;
		int mm   = 0;
		int dd   = 0;
		/* calendar 에서는  0 부터 1월이 11이 12월 */
		try {
	         year = Integer.parseInt(yyddtt.substring(0, 4));        // 일시에서  년도
	         mm       = Integer.parseInt(yyddtt.substring(4, 6));    // 일시에서 월
	         dd       = Integer.parseInt(yyddtt.substring(6, 8));    // 일시에서 일

	         Calendar cal=Calendar.getInstance(Locale.KOREAN);
	         cal.set(year, mm-1, dd);                // 월은 0부터 11로 0은 1월 ~ 11은 12월이다. 그래서 받아온 월에서 1을 빼서 일자를 셋팅한다.
	         cal.add(y,z);
	         Date currentTime=cal.getTime();
	         SimpleDateFormat formatter=new SimpleDateFormat(date_type,Locale.KOREAN);
	         String timestr=formatter.format(currentTime);

		return timestr;
		} catch(Exception e) {
	         throw e;
		}
	}

	/*
     *메서드명 : getHour
     *메서드 기능 : 두일자의 차이의 구하고자 하는 day, hour, minute, second를 얻는다.
     *PARAM startDDTT: 시작일자
     *PARAM endDDTT: 종료일자
     *PARAM type : 구하고자 하는 type(day, hour, minute, second)
     *RETURN VALUE : string
    */
	public static String getHour(String startDDTT, String endDDTT, String type) throws Exception{
	     String time = null;
	     Date date1 = null;
	     Date date2 = null;
	     SimpleDateFormat sdf = new SimpleDateFormat();
	     sdf.applyPattern("yyyyMMddhhmmss");


	     try {
	         date1 = sdf.parse(startDDTT);
	         date2 = sdf.parse(endDDTT);

	     } catch (Exception e) {
	         e.printStackTrace();
	     }

	     Calendar c1 = Calendar.getInstance();
	     Calendar c2 = Calendar.getInstance();
	     c1.setTime(date1);
	     c2.setTime(date2);

	     long intervalMilli =
	         c2.getTimeInMillis() - c1.getTimeInMillis();

	     long second = 1000;
	     long minute = second * 60;
	     long hour = minute * 60;
	     long day = hour * 24;

	     if( type == "second"){
	         time = String.valueOf(intervalMilli / hour);

	         } else if( type == "minute"){
	         time = String.valueOf(intervalMilli / minute);

	     } else if( type == "hour"){
	         time = String.valueOf(intervalMilli / hour);

	     } else if( type == "day"){
	         time = String.valueOf(intervalMilli / day);

	     }
	     return time;
	 }

	/*
     *메서드명 : getDecimal
     *메서드 기능 : String으로 받은 수치 소수점을 찍어서 반환한다.
     *PARAM strData : 받은 수치 데이터
     *PARAM strDecimal : 소수점을 찍어줄 자리수
     *RETURN VALUE : string
    */
	public static String getDecimal(String strData, String strDecimal) throws Exception{
		String returnStr = new String();

	    try {
	    	String temData1 = ""+Integer.parseInt(strData.substring(0, strData.length()-Integer.parseInt(strDecimal)));
	    	String temData2 = ""+Integer.parseInt(strData.substring(strData.length()-Integer.parseInt(strDecimal),strData.length()));

	    	returnStr = temData1 + "." + temData2;

	    	return returnStr;
	    } catch (Exception e) {
	    	return strData;
	    }
	}

	/*
     *메서드명 : setAddDate
     *메서드 기능 : String으로받은 날짜를 int로 넘어온 날짜로 더한다.
     *PARAM pDate : 년월일을 더할 기준값.
     *PARAM pYy, pMm,pDd, pHh, pMi : Int형으로 더할 년월일시분
     *RETURN VALUE : string
    */
	static public String setAddDate(String pDate, int pYy, int pMm, int pDd, int pHh, int pMi){
		int yy = 0;
		int mm = 0;
		int dd = 0;
		int hh = 0;
		int mi = 0;

		String result = "";

		if(pDate.length() == 14){
			yy = Integer.parseInt(pDate.substring(0, 4));
			mm = Integer.parseInt(pDate.substring(4, 6));
			dd = Integer.parseInt(pDate.substring(6, 8));
			hh = Integer.parseInt(pDate.substring(8, 10));
			mi = Integer.parseInt(pDate.substring(10, 12));

			DateFormat format = new SimpleDateFormat("yyyyMMddHHmm");

			//Calendar 에서는 1월부터 12월을 주소값으로 0부터 11까지 가지고 있으므로 실제 월에서 -1을 해준다.
			mm--;

			Calendar cal = Calendar.getInstance(Locale.KOREAN);

			//기준일로 세팅
			cal.set(yy,mm,dd,hh,mi);

			//기준일에 파라미터로 넘어온 년월일시분을 더해준다.
			cal.add(Calendar.YEAR, pYy);
			cal.add(Calendar.MONTH, pMm);
			cal.add(Calendar.DATE, pDd);
			cal.add(Calendar.HOUR, pHh);
			cal.add(Calendar.MINUTE, pMi);

			result = format.format(cal.getTime())+"00";
		}

		return result;
	}
	
	 /**
     * 수치데이터 확인 
     * @param gdReq
     * @return gdRes
     */	
	public static String getInterFlag(String intData) throws Exception {
		
		try{
			new Integer(intData);
	
			return "Y";
		} catch (Exception e) {
			return "N";
		}
	}
	
	 /**
     * ComboList 문자열 반환
     * 
     * @param String[][] CodeUtil을 통해 가져온 Code Array 정보
     * @return ComoboBox 생성하기 위한 ComboList 문자열
     */
	public static String getArrToComboList( String[][] arrList )
	{
		String comboList = "";
		if(arrList[0].length == 1){
			comboList = arrList[0][0] + "|" + arrList[1][0];					
		}else{
			for (int i=0; i<arrList[0].length; i++) {
				comboList = comboList + (i!=0 ? "," : "") + arrList[0][i] + "|" + arrList[1][i];
			}				
		}
		
		return comboList;
	}
	
    /**
     * ComboList 문자열 반환
     * 
     * @param List EJBConnector를 통해 가져온 List 정보
     * @return ComoboBox 생성하기 위한 StringBuffer 문자열
     */
	public static StringBuffer getArrToComboList( String all, List dataList )
	{		
		JDTORecord jRecordSeq= null;
		StringBuffer buff = new StringBuffer();
		
		String tmp_code = "";
		String tmp_name = "";

		if(all != null){
			
			buff = buff.append("<option value = '"+all+"'>전체</option>");
			//buff = buff.append("<option value = 'all'>" + all + "</option>");
		}
		
		if(dataList.size() > 0){
			for(int i=0; i<dataList.size(); i++){
			
				jRecordSeq = (JDTORecord)dataList.get(i);	
				tmp_code = StringHelper.nvl(jRecordSeq.getFieldString("CODE"), "");
				tmp_name = StringHelper.nvl(jRecordSeq.getFieldString("NAME"), ""); 	
				
				buff = buff.append("<option value=");
				buff = buff.append(tmp_code);
				buff = buff.append(">");							
				buff = buff.append(tmp_name);
				buff = buff.append("</option>");				
				
			}
		}
		
		return buff;		
	}	
 
    /**
     * ComboList 문자열 반환
     * 
     * @param List CodeUtil을 통해 가져온 List 정보
     * @return ComoboBox 생성하기 위한 StringBuffer 문자열(-로 연결구분하여 콤보박스 텍스트에 out)
     * <option value = CODE>CODE|NAME</option>
     */
	public static StringBuffer getArrToComboList1( String all, List dataList )
	{		
		JDTORecord jRecordSeq= null;
		StringBuffer buff = new StringBuffer();
		
		String tmp_code = "";
		String tmp_name = "";
		
		if(all != null){
			
			buff = buff.append("<option value = '"+all+"'>전체</option>");
		}
		
		if(dataList.size() > 0){
			for(int i=0; i<dataList.size(); i++){
			
				jRecordSeq = (JDTORecord)dataList.get(i);	
				tmp_code = StringHelper.nvl(jRecordSeq.getFieldString("CODE"), "");
				tmp_name = StringHelper.nvl(jRecordSeq.getFieldString("NAME"), ""); 	
				
				buff = buff.append("<option value=");
				buff = buff.append(tmp_code);
				buff = buff.append(">");	
				buff = buff.append(tmp_code);
				buff = buff.append(" - ");
				buff = buff.append(tmp_name);
				buff = buff.append("</option>");				
				
			}
		}
		
		return buff;		
	}
	
    /**
     * ComboList 문자열 반환
     * 
     * @param List CodeUtil을 통해 가져온 List 정보
     * @return ComoboBox 생성하기 위한 StringBuffer 문자열(-로 연결구분하여 콤보박스 텍스트에 out)
     * <option value = CODE>CODE - NAME</option>
     * 콤보박스 초기값의 텍스트를 arg로 받아서 처리 
     */
	public static StringBuffer getArrToComboList( String all, String text, List dataList )
	{		
		JDTORecord jRecordSeq= null;
		StringBuffer buff = new StringBuffer();
		
		String tmp_code = "";
		String tmp_name = "";
		
		if(all != null){
			
			buff = buff.append("<option value = '"+all+"'>" + text + "</option>");
		}
		
		if(dataList.size() > 0){
			for(int i=0; i<dataList.size(); i++){
			
				jRecordSeq = (JDTORecord)dataList.get(i);	
				tmp_code = StringHelper.nvl(jRecordSeq.getFieldString("CODE"), "");
				tmp_name = StringHelper.nvl(jRecordSeq.getFieldString("NAME"), ""); 	
				
				buff = buff.append("<option value=");
				buff = buff.append(tmp_code);
				buff = buff.append(">");	
				buff = buff.append(tmp_code);
				buff = buff.append(" - ");
				buff = buff.append(tmp_name);
				buff = buff.append("</option>");				
				
			}
		}
		
		return buff;		
	}	
	
    /**
     * ComboList 문자열 반환
     * 
     * @param String all : 전체 유무 
     * 		  String text: 콤보텍스트
     * 		  int gbn: value 값과 text 설정	
     * 			(0일경우 value = code, text = code)
     * 			(1일경우 value = code, text = name)
     * 			(2일경우 value = code, text = code - name)
     * 		  List CodeUtil을 통해 가져온 List 정보
     * @return ComoboBox 생성하기 위한 StringBuffer 문자열
     * <option value = CODE>CODE - NAME</option>
     * 콤보박스 초기값의 텍스트를 arg로 받아서 처리 
     */
	public static StringBuffer getArrToComboList( String all, String text, int gbn, List dataList )
	{		
		JDTORecord jRecordSeq= null;
		StringBuffer buff = new StringBuffer();
		
		String tmp_code = "";
		String tmp_name = "";
		
		if(all != null){
			
			buff = buff.append("<option value = '"+all+"'>" + text + "</option>");
		}
		
		if(dataList.size() > 0){
			for(int i=0; i<dataList.size(); i++){
			
				jRecordSeq = (JDTORecord)dataList.get(i);	
				tmp_code = StringHelper.nvl(jRecordSeq.getFieldString("CODE"), "");
				if(gbn == 0){					
					tmp_name = StringHelper.nvl(jRecordSeq.getFieldString("CODE"), "");
					
					buff = buff.append("<option value=");
					buff = buff.append(tmp_code);
					buff = buff.append(">");
					buff = buff.append(tmp_name);
					buff = buff.append("</option>");	
				}else if(gbn == 1){
					tmp_name = StringHelper.nvl(jRecordSeq.getFieldString("NAME"), "");
					
					buff = buff.append("<option value=");
					buff = buff.append(tmp_code);
					buff = buff.append(">");
					buff = buff.append(tmp_name);
					buff = buff.append("</option>");
				}else if(gbn == 2){
					buff = buff.append("<option value=");
					buff = buff.append(tmp_code);
					buff = buff.append(">");
					buff = buff.append(tmp_code);
					buff = buff.append(" - ");
					buff = buff.append(tmp_name);
					buff = buff.append("</option>");	
				}else{
					buff = buff.append("<option value=''></option>");					
				}				 															
			}
		}
		
		return buff;		
	}

	public static JDTORecordSet genCodeCommbo(String code_id, String cat_id) {
		JDTORecordSet recordSet = JDTORecordFactory.getInstance().createRecordSet("recordSet");
		
		/*
		try{
			JDTORecord [] dtos = CommCodeMng.CMCode(code_id, cat_id);

			for(int ii=0;ii<dtos.length;ii++) {
				codes += dtos[ii].getFieldString("CD_VAL") + ";" ;
				names += dtos[ii].getFieldString("CD_MNNG")+ ";";
			}
			codes = codes.substring(0, codes.lastIndexOf(";"));
			names = names.substring(0, names.lastIndexOf(";"));
		} catch(Exception e){
			codes = "";
			names = "";
		}
		*/
		
		return recordSet;
	}
}
