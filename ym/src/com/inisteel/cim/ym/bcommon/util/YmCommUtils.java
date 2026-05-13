/**
 * @(#)YmCommUtils
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
package com.inisteel.cim.ym.bcommon.util;
 
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.inisteel.cim.cm.message.MessageSenderAuto;
import com.inisteel.cim.common.exception.AppRuntimeException;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

import xlib.cmc.GridData;
import xlib.cmc.GridHeader;
import xlib.cmc.OperateGridData;

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
import com.inisteel.cim.common.jms.model.po.YMPO155;
import com.inisteel.cim.common.jms.model.po.YMPO159;
import com.inisteel.cim.common.jms.model.po.YMPO161;
/**
 * [A] 클래스명 : 야드관리 공통 Utils
 *
 */

public class YmCommUtils {
	
	private static Logger logger = new Logger("ym");

	private boolean bDebugFlag=false;
	private String szSessionName =getClass().getName();
	/**
	 * 문자열이 null 일때 임의의 문자열을 반환한다.
	 * @param value
	 * @param defaultValue
	 * @return String
	 */
	public String nvl(String value, String defaultValue) {
		return (value == null || "".equals(value) || "NULL".equals(value.trim().toUpperCase())) ? defaultValue : value;
	}

	public String nvl(Object o, String defaultValue) {
		return (o == null) ? defaultValue : o.toString();
	}

	/**
	 * 문자열이 null 일때 ""을 반환한다.
	 * @param value
	 * @return String
	 */
	public String trim(String value) {
		return (value == null || "NULL".equals(value.trim().toUpperCase())) ? "" : value.trim();
	}

	/**
	 * Object가 null 일때 true를 반환한다.
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
	 * 페이징 처리 변수 가져오기
	 */
	public int[] getCurrRow(GridData gdData) throws Exception {
		return getCurrRow(gridDataTojdtoRecord(gdData));
	}

	public int[] getCurrRow(JDTORecord record) {
		int viewRows  = Integer.parseInt(nvl(record.getFieldString("viewRows"), "10")); //ROW 갯수
		int currpage  = Integer.parseInt(nvl(record.getFieldString("viewPage"), "1")); //현재 페이지
		int startRow = (currpage - 1) * viewRows + 1;
		int endRow   = currpage * viewRows;

		return new int[]{startRow, endRow};
	}

	//해쉬맵의 내용을 GridData의 파라미터로 담는다.
	public GridData hashMapToGridData(HashMap inMap) throws Exception {
		GridData returnGridData = new GridData();

		if (inMap == null || inMap.isEmpty()) {
			return returnGridData;
		}

		java.util.Set set = inMap.keySet();
		java.util.Iterator iterator = set.iterator();

		String key = "";
		while (iterator.hasNext()) {
			key = String.valueOf(iterator.next());
			returnGridData.addParam(key, nvl(inMap.get(key), ""));
		}

		return returnGridData;
	}

	//해쉬맵의 내용을 JDTORecord로 담는다.
	public JDTORecord hashMapTojdtoRecord(HashMap inMap) throws Exception {
		JDTORecord returnJRecord = JDTORecordFactory.getInstance().create();

		if (inMap == null || inMap.isEmpty()) {
			return returnJRecord;
		}

		java.util.Set set = inMap.keySet();
		java.util.Iterator iterator = set.iterator();

		String key = "";
		while (iterator.hasNext()) {
			key = String.valueOf(iterator.next());
			returnJRecord.addField(key, nvl(inMap.get(key), ""));
		}

		return returnJRecord;
	}

	//해쉬맵의 내용을 JDTORecord로 담는다.
	public JDTORecord gridDataTojdtoRecord(GridData gdData) throws Exception {
		JDTORecord rowJrecord = JDTORecordFactory.getInstance().create();

		if (gdData == null) {
			return rowJrecord;
		}

		String params[] = gdData.getParamNames();
		for (int ii = 0; ii < params.length; ii++) {
			rowJrecord.addField(params[ii], nvl(gdData.getParam(params[ii]), ""));
		}

		return rowJrecord;
	}

	//해쉬맵의 내용을 JDTORecord로 담는다.
	public HashMap jdtoRecordTohashMap(JDTORecord inJRecord) throws Exception {
		HashMap returnMap = new HashMap();

		if (inJRecord == null || inJRecord.size() == 0) {
			return returnMap;
		}

		java.util.Iterator iterator = inJRecord.iterateName();

		String key = "";
		while (iterator.hasNext()) {
			key = String.valueOf(iterator.next());
			returnMap.put(key, nvl(inJRecord.getField(key), ""));
		}

		return returnMap;
	}

	//List의 JDTORecord를 HashMap으로 변환한다.
	public List listJdtoRecordTohashMap(List inDataList) throws Exception {
		List returnList = new ArrayList();

		if (inDataList == null || inDataList.isEmpty()) {
			return returnList;
		}

		for (int ii = 0; ii < inDataList.size(); ii++) {
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
	public String format(String no, String formatter) {
		try {
			return format(Double.parseDouble(no), formatter);
		} catch (Exception e) {
			return "";
		}
	}

	public String format(int no, String formatter) {
		DecimalFormat df = new DecimalFormat(formatter);
		return df.format(no);
	}

	public String format(float no, String formatter) {
		DecimalFormat df = new DecimalFormat(formatter);
		return df.format(no);
	}

	public String format(long no, String formatter) {
		DecimalFormat df = new DecimalFormat(formatter);
		return df.format(no);
	}

	public String format(double no, String formatter) {
		DecimalFormat df = new DecimalFormat(formatter);
		return df.format(no);
	}

	public String format(int no, int len) {
		DecimalFormat df = new DecimalFormat(addStr(len, "0"));
		return df.format(no);
	}

	public String formatMaxNo(int no, int maxNo) {
		DecimalFormat df = new DecimalFormat(addStr((String.valueOf(maxNo)).length(), "0"));
		return df.format(no);
	}

	public double trunc(double val, int digit) {
		double val2 = 0.0;
		if (val > 0) {
			val2 = Math.floor(val * Math.pow(10, digit));
		} else if (val < 0) {
			val2 = Math.ceil(val * Math.pow(10, digit));
		}
		return val2 / Math.pow(10, digit);
	}

	public float trunc(float val, int digit) {
		double val2 = 0.0;
		if (val > 0) {
			val2 = Math.floor(val * Math.pow(10, digit));
		} else if (val < 0) {
			val2 = Math.ceil(val * Math.pow(10, digit));
		}
		return (float)(val2 / Math.pow(10, digit));
	}

	public double round(double val, int digit) {
		return Math.round(val * Math.pow(10, digit)) / Math.pow(10, digit);
	}

	public float round(float val, int digit) {
		return (float)(Math.round(val * Math.pow(10, digit)) / Math.pow(10, digit));
	}

 //************************************** WISEGRID **************************************

	/**
	 * WiseGrid 관련 Helper Method
	 * 디비 결과값 List를 GridData로 변환한다.
	 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
	 */
	public GridData jdtoRecordToGridData(GridData returnGrid, List dataList) throws Exception {
		return jdtoRecordToGridData(returnGrid, dataList, null, null);
	}

	/**
	 * WiseGrid 관련 Helper Method
	 * 디비 결과값JDTORecord를 GridData로 변환한다.
	 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
	 */
	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecord dataJrecord) throws Exception {
		return jdtoRecordToGridData(returnGrid, dataJrecord, null, null);
	}

	/**
	 * WiseGrid 관련 Helper Method
	 * 디비 결과값 List를 GridData로 변환한다.
	 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
	 */
	public GridData jdtoRecordToGridData(GridData returnGrid, List dataList, GridData gdReq) throws Exception {
		return jdtoRecordToGridData(returnGrid, dataList, gdReq, null);
	}

	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecordSet dataSet, GridData gdReq) throws Exception {
		return jdtoRecordToGridData(returnGrid, dataSet.toList(), gdReq, null);
	}

	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecordSet dataSet, GridData gdReq, String numberType) throws Exception {
		return jdtoRecordToGridData(returnGrid, dataSet.toList(), gdReq, numberType);
	}

	/**
	 * WiseGrid 관련 Helper Method
	 * 디비 결과값JDTORecord를 GridData로 변환한다.
	 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
	 */
	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecord dataJrecord, GridData gdReq) throws Exception {
		return jdtoRecordToGridData(returnGrid, dataJrecord, gdReq, null);
	}

	public GridData jdtoRecordToGridData(GridData returnGrid, List dataList, GridData gdReq, String numberType) throws Exception {
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

		if (dataList == null || dataList.isEmpty()) {
			returnGrid.addParam("TOTALCOUNT", "0");
			//returnGrid.addParam("SELECT_MSG", MessageHelper.getUserMessage("MSG0103", new String[]{""}, ""));
		} else {
			String totCount = "0";
			for (int ii = 0; ii < gridHeader.length; ii++) {
				headerName = gridHeader[ii].getID();
				dataType = gridHeader[ii].getDataType();

				/*
				 * 컬럼에 맞게 데이타를 세팅한다.
				 * SEQNO, CHECK, CRUD은  디비에서 가져오지 않는다. 화면에서 가져오는 값이다. 화면에 없다면..패스.
				 */
				if ("SEQNO".equals(headerName)) {
					for (int kk = 0; kk < dataList.size(); kk++) {
						returnGrid.getHeader("SEQNO").addValue(String.valueOf(kk + 1), "");
					}
				} else if ("CHECK".equals(headerName)) {
					for (int kk = 0; kk < dataList.size(); kk++) {
						returnGrid.getHeader("CHECK").addValue("0", "");
					}
				} else if ("CRUD".equals(headerName)) {
					for (int kk = 0; kk < dataList.size(); kk++) {
						returnGrid.getHeader("CRUD").addValue("R", "R");
					}
				} else {
					for (int jj = 0; jj < dataList.size(); jj++) {
						dataJrecord = (JDTORecord)dataList.get(jj);

						/*
						 * 페이징 처리를 했었을 경우 total row값이 들어가는 파라미터를 생성해 줘야 한다.
						 */
						if ("0".equals(totCount)) {
							totCount = StringHelper.evl(dataJrecord.getFieldString("TOTALCOUNT"), "0");
						}

						headerNameVal = StringHelper.evl(dataJrecord.getFieldString(headerName), "");
						if (!"".equals(headerNameVal)) {
							headerNameChar = headerNameVal.substring(0, 1);
						}

						if ("L".equals(dataType)) {
							//t_combo 일때...
							returnGrid.getHeader(headerName).addSelectedHiddenValue(headerNameVal);
						} else if ("C".equals(dataType) || "R".equals(dataType)) {
							//t_checkbox, t_radio 일때...
							//0, 1 이 아니면 에러가 남....에러가 나지 않도록...세팅함
							if ("0".equals(headerNameChar) || "1".equals(headerNameChar)) {
								returnGrid.getHeader(headerName).addValue(headerNameVal, "");
							} else {
								//언체크로 세팅(0)
								returnGrid.getHeader(headerName).addValue("0", "");
							}
						} else if ("D".equals(dataType)) {
							//t_date 일때...YYYYMMDD 형식이 아니면 에러가 떨어짐.
							if (headerNameVal.length() > 10) {
								returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(headerNameVal.substring(0, 10), "-", ""), "");
							} else if (headerNameVal.length() == 10 || headerNameVal.length() == 8) {
								returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(headerNameVal, "-", ""), "");
							} else {
								returnGrid.getHeader(headerName).addValue("", "");
							}
						} else if ("I".equals(dataType)) {
							//t_imagetext 일때...
							returnGrid.getHeader(headerName).addValue(headerNameVal, headerNameVal, 0);
						} else if ("N".equals(dataType)) {
							//t_number 일때 값이 0이면  space를 전송한다.
							if (!"number".equals(numberType)) {
								if (!"0".equals(headerNameVal)) {
									returnGrid.getHeader(headerName).addValue(headerNameVal, "");
								} else {
									returnGrid.getHeader(headerName).addValue("", "");
								}
							} else {
								returnGrid.getHeader(headerName).addValue(headerNameVal, "");
							}
						} else {
							returnGrid.getHeader(headerName).addValue(headerNameVal, "");
						}
					}//for
				}//if
			}//for

			/*
			 * 페이징 처리를 했었을 경우 total row값이 들어가는 파라미터를 생성해 줘야 한다.
			 */
			//total row 세팅..
			returnGrid.addParam("TOTALCOUNT", totCount);
		}

		/*
		 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
		 */
		if (gdReq != null) {
			String params[] = gdReq.getParamNames();

			for (int ii=0; ii<params.length; ii++) {
				returnGrid.addParam(params[ii], StringHelper.evl(gdReq.getParam(params[ii]), ""));
			}
		}

		return returnGrid;
	}

	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecord dataJrecord, GridData gdReq, String numberType) throws Exception {
		GridHeader[] gridHeader = returnGrid.getHeaders();
		String headerName		= "";
		String dataType			= "";
		String headerNameVal	= "";
		String headerNameChar	= "";

		/*
		 * 컬럼에 맞게 데이타를 세팅한다.
		 * SEQ_NO, SELECTED은 따로 생성한다. 이 두개의 컬럼은 디비에서 가져오지 않는다.
		 */
		if (dataJrecord == null || dataJrecord.size() == 0) {
			returnGrid.addParam("TOTALCOUNT", "0");
		} else {
			for (int ii = 0; ii < gridHeader.length; ii++) {
				headerName = gridHeader[ii].getID();
				dataType = gridHeader[ii].getDataType();

				/*
				 * 페이징 처리를 했었을 경우 total row값이 들어가는 파라미터를 생성해 줘야 한다.
				 */
				if (ii == 0) {
					//total row 세팅..
					returnGrid.addParam("TOTALCOUNT", StringHelper.evl(dataJrecord.getFieldString("TOTALCOUNT"), "0"));
				}

				/*
				 * SEQNO, CHECK, CRUD은  디비에서 가져오지 않는다. 화면에서 가져오는 값이다. 화면에 없다면..패스.
				 */
				if ("SEQNO".equals(headerName)) {
					returnGrid.getHeader("SEQNO").addValue("1", "");
				} else if ("CHECK".equals(headerName)) {
					returnGrid.getHeader("CHECK").addValue("0", "");
				} else if ("CRUD".equals(headerName)) {
					returnGrid.getHeader("CRUD").addValue("R", "R");
				} else {
					/*
					 * 컬럼에 맞게 데이타를 세팅한다.
					 */
					headerNameVal = StringHelper.evl(dataJrecord.getFieldString(headerName), "");
					if (!"".equals(headerNameVal)) {
						headerNameChar = headerNameVal.substring(0, 1);
					}

					if ("L".equals(dataType)) {
						//t_combo 일때...
						returnGrid.getHeader(headerName).addSelectedHiddenValue(headerNameVal);
					} else if ("C".equals(dataType) || "R".equals(dataType)) {
						//t_checkbox, t_radio 일때...
						//0, 1 이 아니면 에러가 남....에러가 나지 않도록...세팅함
						if ("0".equals(headerNameChar) || "1".equals(headerNameChar)) {
							returnGrid.getHeader(headerName).addValue(headerNameChar, "");
						} else {
							//언체크로 세팅(0)
							returnGrid.getHeader(headerName).addValue("0", "");
						}
					} else if ("D".equals(dataType)) {
						//t_date 일때...YYYYMMDD 형식이 아니면 에러가 떨어짐.
						if (headerNameVal.length() > 10) {
							returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(headerNameVal.substring(0, 10), "-", ""), "");
						} else if (headerNameVal.length() == 10 || headerNameVal.length() == 8) {
							returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(headerNameVal, "-", ""), "");
						} else {
							returnGrid.getHeader(headerName).addValue("", "");
						}
					} else if ("I".equals(dataType)) {
						//t_imagetext 일때...
						returnGrid.getHeader(headerName).addValue(headerNameVal, headerNameVal, 0);
					} else if ("N".equals(dataType)) {
						//t_number 일때 값이 0이면  space를 전송한다.
						if (!"number".equals(numberType)) {
							if (!"0".equals(headerNameVal)) {
								returnGrid.getHeader(headerName).addValue(headerNameVal, "");
							} else {
								returnGrid.getHeader(headerName).addValue("", "");
							}
						} else {
							returnGrid.getHeader(headerName).addValue(headerNameVal, "");
						}
					} else {
						returnGrid.getHeader(headerName).addValue(headerNameVal, "");
					}
				}
			}//for
		}

		/*
		 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
		 */
		if (gdReq != null) {
			String params[] = gdReq.getParamNames();

			for (int ii = 0; ii < params.length; ii++) {
				returnGrid.addParam(params[ii], StringHelper.evl(gdReq.getParam(params[ii]), ""));
			}
		}

		return returnGrid;
	}


	/**
	 * Pk로 조회된 값을 GridData의 파리미터로 세팅한다.
	 * jsp에서는 event="EndQuery()"에서 GridObj.GetParam(key)으로 받으면 된다.
	 */
	public GridData jdtoRecordToGridParam(JDTORecord dataJrecord) throws Exception {
		GridData returnGrid = new GridData();
		java.util.Iterator iterator = dataJrecord.iterateName();
		String key = "";

		while (iterator.hasNext()) {
			key = String.valueOf(iterator.next());
			returnGrid.addParam(key, String.valueOf(dataJrecord.getField(key)));
		}

		return returnGrid;
	}

	/**
	 * Pk로 조회된 값을 GridData의 파리미터로 세팅한다.(기존의 Grid에 추가하고싶을때)
	 * jsp에서는 event="EndQuery()"에서 GridObj.GetParam(key)으로 받으면 된다.
	 */
	public GridData jdtoRecordToGridParam(JDTORecord dataJrecord, GridData returnGrid) throws Exception {
		java.util.Iterator iterator = dataJrecord.iterateName();
		String key = "";

		while (iterator.hasNext()) {
			key = String.valueOf(iterator.next());
			returnGrid.addParam(key, String.valueOf(dataJrecord.getField(key)));
		}

		return returnGrid;
	}

	/**
	 * GridData 의 내용을 List로 변환한다.
	 */
	public List GridDataToList(GridData dataGrid) throws Exception {
		List returnList = new ArrayList();
		JDTORecord rowJrecord = null;

		if (dataGrid == null) {
			return returnList;
		}

		GridHeader[] gridHeaders = dataGrid.getHeaders();

		for (int ii = 0; ii < gridHeaders[0].getRowCount(); ii++) {
			rowJrecord = JDTORecordFactory.getInstance().create();
			for (int jj = 0; jj < gridHeaders.length; jj++) {
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

		try {
			if (inDto.getParam("set_upper") != null && inDto.getParam("set_upper").equals("true")) {
				isUpperKey = true;
			}

			outRecord = JDTORecordFactory.getInstance().create();
			String params[] = inDto.getParamNames();
			for (int ii = 0; ii < params.length; ii++) {
				String key = (String) params[ii];
				String value = StringHelper.nvl(inDto.getParam(params[ii]), "");

				// DBAssistant 에 전달할 JDTORecord를 설정합니다.
				outRecord.setField((isUpperKey) ? key.toUpperCase() : key, value);
			}
		} catch (Exception e) {
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
	public JDTORecord[] genGridToJDTORecord(GridData inDto) throws Exception {
		boolean isUpperKey = false;

		if (inDto.getParam("set_upper") != null && inDto.getParam("set_upper").equals("true")) {
			isUpperKey = true;
		}

		GridHeader[] ghs = inDto.getHeaders();
		int hCount = ghs.length;
		int rCount = 0;
		if (hCount > 0) {
			rCount = ghs[0].getRowCount();
		}
		JDTORecord[] jdtoAl = new JDTORecord[rCount];
		logger.println(LogLevel.DEBUG, "========  GridData -> JDTORecord []  ================");
		logger.println(LogLevel.DEBUG, "헤더갯수:" + hCount);
		logger.println(LogLevel.DEBUG, "Row갯수:" + rCount);

		logger.println(LogLevel.DEBUG, "========== GridData inDto ROW DATA ===========");
		for (int ii = 0; ii < rCount; ii++) {
			GridHeader chHeader = inDto.getHeader("CHECK");
			boolean Checked = (Integer.parseInt(chHeader.getValue(ii)) == 1) ? true : false;
			if (Checked) {
				JDTORecord jDto = genParamToJDTORecord(inDto);

				for (int jj = 0; jj < hCount; jj++) {
					String key = ghs[jj].getID();
					String rValue = "";

					if (ghs[jj].getDataType().equals(OperateGridData.t_combo)) {
						rValue = StringHelper.evl(ghs[jj].getComboHiddenValues()[ghs[jj].getSelectedIndex(ii)], "");
					} else {
						rValue = StringHelper.evl(ghs[jj].getValue(ii), "");
					}

					jDto.setField((isUpperKey) ? key.toUpperCase() : key, rValue);
				}
				jdtoAl[ii] = jDto;
			}
		}

		logger.println(LogLevel.DEBUG, "========== JDTORecord START ===========");
		for (int mm = 0; mm < jdtoAl.length; mm++) {
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
	public boolean isBatchSuccess(int[] results) {
		if (results == null || results.length == 0) {
			return false;
		}

		boolean result = true;

		for (int ii = 0; ii < results.length; ii++) {
			if (results[ii] == -3) {
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
	public String addDateGubunStr(String src, String gubun) {
		String temp = StringHelper.replaceStr(StringHelper.replaceStr(src, "/", ""), ".", "");
		if (temp.length() == 8) {
			return temp.substring(0, 4) + gubun + temp.substring(4, 6) + gubun + temp.substring(6);
		} else if (temp.length() == 6) {
			return temp.substring(0, 4) + gubun + temp.substring(4, 6);
		} else {
			return src;
		}
	}

	/**
	 * HH24:MI:SS의 시간포멧을 원하는 구분자로 바꾸고 싶을때..
	 * 사용 예)
	 * CmnUtil.addTimeGubunStr("HH24:MI:SS", " ")
	 *
	 */
	public String addTimeGubunStr(String src, String gubun) {
		String temp = StringHelper.replaceStr(StringHelper.replaceStr(src, ":", ""), " ", "");
		if (temp.length() == 6) {
			return temp.substring(0, 2) + gubun + temp.substring(2, 4) + gubun + temp.substring(4,6);
		} else {
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
	public String getCalsDate(String yyddtt, int y, int z, String fmtStr) {
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
	public String getHour(String sDt, String eDt, String type) throws Exception {
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

		if ("second".equals(type)) {
			time = String.valueOf(intervalMilli / hour);
		} else if ("minute".equals(type)) {
			time = String.valueOf(intervalMilli / minute);
		} else if ("hour".equals(type)) {
			time = String.valueOf(intervalMilli / hour);
		} else if ("day".equals(type)) {
			time = String.valueOf(intervalMilli / day);
		}

		return time;
	}

	/**
	 *메서드명 : getDecimal
	 *메서드 기능 : String으로 받은 수치 소수점을 찍어서 반환한다.
	 *PARAM strData : 받은 수치 데이터
	 *PARAM strDecimal : 소수점을 찍어줄 자리수
	 *RETURN VALUE : string
	*/
	public String getDecimal(String strData, String strDecimal) {
		try {
			String temData1 = String.valueOf(Integer.parseInt(strData.substring(0, strData.length()-Integer.parseInt(strDecimal))));
			String temData2 = String.valueOf(Integer.parseInt(strData.substring(strData.length()-Integer.parseInt(strDecimal),strData.length())));

			return temData1 + "." + temData2;
		} catch (Exception e) {
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
	static public String setAddDate(String pDate, int pYy, int pMm, int pDd, int pHh, int pMi) {
		int yy = 0;
		int mm = 0;
		int dd = 0;
		int hh = 0;
		int mi = 0;

		String result = "";

		if (pDate.length() == 14) {
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
	public String getInterFlag(String intData) {
		try {
			new Integer(intData);

			return "Y";
		} catch (Exception e) {
			return "N";
		}
	}

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

	/**
	 * String 길이 만큼 우측에 Char 추가
	 * @param src 현재 값
	 * @param len 가공후 길이
	 * @param chr 추가 Char
	 * @return 가공하여 Return
	 */
	public String getRPad(String src, int len, String chr) {
		String ret = "";                  //Return String
		int sLen = src.getBytes().length; //원본 길이

		if (sLen < len) {
			ret = src + addStr(len - sLen, chr);
		} else if (sLen > len) {
			ret = substr(src, 0, len);
		} else {
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
	public String getLPad(String src, int len, String chr) {
		String ret = "";                  //Return String
		int sLen = src.getBytes().length; //원본 길이

		if (sLen < len) {
			ret = addStr(len - sLen, chr) + src;
		} else if (sLen > len) {
			ret = substr(src, sLen - len, len);
		} else {
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
	public String getRPadSpc(String src, int len) {
		return getRPad(src, len, " ");
	}

	/**
	 * String 길이 만큼 좌측에 "0" 추가
	 * @param src 현재 값
	 * @param len 가공후 길이
	 * @return 가공하여 Return
	 */
	public String getLPadZero(String src, int len) {
		return getLPad(src, len, "0");
	}

	/* Date Format : "yyyyMMddHHmmss" */
	public String getDateTime14() {
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyyMMddHHmmss");
	}

	/* Date Format : "yyyy-MM-ddHH:mm:ss" */
	public String getDateTime18() {
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyy-MM-ddHH:mm:ss");
	}

	/* Date Format : "yyyy-MM-dd HH:mm:ss" */
	public String getDateTime19() {
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm:ss");
	}

	/* Date Format : "yyyyMMdd" */
	public String getDate8() {
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyyMMdd");
	}

	/* Date Format : "yyyy-MM-dd" */
	public String getDate10() {
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyy-MM-dd");
	}

	/* Date Format : "HHmmss" */
	public String getTime6() {
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmss");
	}

	/* Date Format : "HH:mm:ss" */
	public String getTime8() {
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HH:mm:ss");
	}

	/**
	 * 한글을 2byte로 계산하여 길이 구하기
	 * @param String str
	 * @return
	 */
	public int getLength(String str) {
		return str.getBytes().length;
	}

	/**
	 * Char 단위 substr
	 * @param String strLine
	 * @param int start
	 * @param int len
	 * @return String
	 */
	public String substr(String strLine, int start, int  len) {
		byte[] bytes = strLine.getBytes();

		if (bytes == null || bytes.length <= start || len <= 0) {
			return "";
		}

		byte[] rbytes = new byte[len];

		for (int ii = 0; ii < len; ii++) {
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
	public String substr(String strLine, int start) {
		return substr(strLine, start, strLine.getBytes().length);
	}

	/**
	 * String Array를 String으로 변환
	 * @param String[] arrStr
	 * @return String
	 */
	public String toString(String[] arrStr) {
		StringBuffer sb = new StringBuffer();
		int aLen = arrStr.length;
		
		if (aLen > 0) {
			sb = sb.append(arrStr[0]);
		}

		for (int ii = 1; ii < aLen; ii++) {
			sb = sb.append(", " + arrStr[ii]);
		}

		return sb.toString();
	}

	/**
	 *  14자리 12자리 8자리 String 형식의 날짜를 입력받아 날짜 범위가 올바른지 판단*
	 * @param strDate
	 * @return boolean
	 */
	public boolean checkDateFormat(String strDate) {
		int year = 0;
		int mon  = 0;
		int day  = 0;
		int hour = 0;
		int min  = 0;
		int sec  = 0;
		int lastDay = 0;
		Date tmpDate = new Date();

		try {
			if (strDate.length() == 14) {
				year = StringHelper.parseInt(strDate.substring( 0, 4), 0);
				mon  = StringHelper.parseInt(strDate.substring( 4, 6), 0);
				day  = StringHelper.parseInt(strDate.substring( 6, 8), 0);
				hour = StringHelper.parseInt(strDate.substring( 8,10),-1);
				min  = StringHelper.parseInt(strDate.substring(10,12),-1);
				sec  = StringHelper.parseInt(strDate.substring(12,14),-1);
				tmpDate = DateHelper.toUtilDate(year, mon, day, hour, min, sec);
			} else if (strDate.length() == 12) {
				year = StringHelper.parseInt(strDate.substring( 0, 4), 0);
				mon  = StringHelper.parseInt(strDate.substring( 4, 6), 0);
				day  = StringHelper.parseInt(strDate.substring( 6, 8), 0);
				hour = StringHelper.parseInt(strDate.substring( 8,10),-1);
				min  = StringHelper.parseInt(strDate.substring(10,12),-1);
				tmpDate = DateHelper.toUtilDate(year, mon, day, hour, min);
			} else if (strDate.length() == 8) {
				year = StringHelper.parseInt(strDate.substring(0,4),0);
				mon  = StringHelper.parseInt(strDate.substring(4,6),0);
				day  = StringHelper.parseInt(strDate.substring(6,8),0);
				tmpDate = DateHelper.toUtilDate(year, mon, day);
			} else {
				return false;
			}

			lastDay = DateHelper.lastDay(tmpDate);
		} catch (Exception e) {
			return false;
		}

		if (year < 1000) {
			return false;
		} else if (mon < 1 || mon > 12) {
			return false;
		} else if (day < 1 || day > lastDay) {
			return false;
		} else if (hour < 0 || hour > 23) {
			return false;
		} else if (min < 0 || min > 59) {
			return false;
		} else if (sec < 0 || sec > 59) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 해당 값이 있는지를 Check
	 * @param String[] arrStr
	 * @param String str
	 * @return boolean
	 */
	public boolean chkExist(String[] arrStr, String str) {
		boolean chkRst = false;

		if (arrStr != null && !"".equals(str)) {
			int arrCnt = arrStr.length;
		
			for (int ii = 0; ii < arrCnt; ii++) {
				if (arrStr[ii] != null && str.equals(arrStr[ii])) {
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
	public boolean isNumber(String str) {
		Pattern p = Pattern.compile("[\\d]");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 *      [A] 오퍼레이션명 : Logging 을 위한 ID 생성
	 *
	 *      @return String
	*/
	public String getLogId() {
		return "<" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
	}

	/**
	 *      [A] 오퍼레이션명 : 야드구분을 포함 한 Logging 을 위한 ID 생성
	 *
	 *      @return String
	*/
	public String getLogId(String ydGp) {
		return "["+ydGp+"]"+"<" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
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
	public JDTORecord getParam(String logId, String methodNm, String modifier) {
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		try {
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
			if (!"".equals(modifier)) {
				jrParam.setField("MODIFIER", modifier);	//수정자
			}
		} catch(Exception e) {}
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
	public JDTORecord getParam(String logId, String methodNm, HashMap hmReq) {
		//Logging
		printLog(logId, methodNm, "F+");

		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		try {
			jrParam = this.hashMapTojdtoRecord(hmReq);
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
		} catch(Exception e) {}

		return jrParam;
	}

	/**
	 *      [A] 오퍼레이션명 : 1 Line Error Log 만들기
	 *
	 *      @param String logId
	 *      @param String logMsg
	 *      @return String
	*/
	public String makeErrorLine(String logId, String logMsg) {
		return "\n" + logId + " ■Error■ " + logMsg;
	}

	/**
	 *      [A] 오퍼레이션명 : 1 Line Warning Log 만들기
	 *
	 *      @param String logId
	 *      @param String logMsg
	 *      @return String
	*/
	public String makeWarnLine(String logId, String logMsg) {
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
	public String makeErrorLog(String logId, String methodNm, String errMsg) {
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
	public String makeErrorLog(String logId, String methodNm, Exception e) {
		return makeErrorLine(logId, "Method  : " + methodNm) + makeErrorLine(logId, "Message : " + e.getMessage());
	}

	/**
	 *      [A] 오퍼레이션명 : Debug Logging
	 *
	 *      @param String logId
	 *      @param String logMsg
	 *      @param String logGp ([시작, 종료] + 구분)
	 *      @return void
	*/
	public void printLog(String logId, String logMsg, String logGp) {
		String prnLog = "";

		if (isEmpty(logGp)) {
			prnLog = logMsg;
		} else if (logGp.endsWith("+")) {
			prnLog = "▼" + logGp + "▼ " + logMsg;
		} else if (logGp.endsWith("-")) {
			prnLog = "▲" + logGp + "▲ " + logMsg;
		} else {
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
	public void printLog(String logId, String logMsg, String logGp, GridData gdReq) {
		//Logging
		printLog(logId, logMsg, logGp);

		if (logGp.endsWith("+")) {
			gdReq.setNavigateValue(logMsg);	//상위 Method 명

			if ("F+".equals(logGp)) {
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
	public void printLog(String logId, String logMsg, String logGp, JDTORecord jrParam) {
		//Logging
		printLog(logId, logMsg, logGp);

		try {
			if (logGp.endsWith("+")) {
				jrParam.setResultMsg(logMsg);		//상위 Method 명
	
				if ("F+".equals(logGp)) {
					jrParam.setResultCode(logId);	//Logging 을 위한 ID
				}
			}
		} catch(Exception e) {}
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
	public void printErrorLog(String logId, String methodNm, Object caller, Exception e) {
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
	public void printErrorLog(String ErrMsg, Object caller, Exception e) {
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
	public void printErrorLog(String logId, String methodNm, String ErrMsg, Object caller, Exception e) {
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
	public void printWarnLog(String logId, String methodNm, String WarnMsg) {
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
	public String makeSqlErrorLog(String logId, String methodNm, String jspeed_query_id, Object[] param, Exception e) {
		StringBuffer sb = new StringBuffer();

		try {
			sb = sb.append(makeErrorLine(logId, "Method  : " + methodNm       ));
			sb = sb.append(makeErrorLine(logId, "Message : " + e.getMessage() ));
			sb = sb.append(makeErrorLine(logId, "QueryID : " + jspeed_query_id));

			if (!isEmpty(jspeed_query_id)) {
				sb = sb.append("\n" + QueryService.getInstance().getSQL(jspeed_query_id));

				if (param != null) {
					int pLen = param.length;
					
					sb = sb.append("\n▩ {");
					for (int ii = 0; ii < pLen; ii++) {
						if (ii > 0) { sb = sb.append(", "); }
						sb = sb.append(ii + "=" + param[ii].toString());
					}
					sb = sb.append("}");
				}
			} else {
				sb = sb.append(makeErrorLine(logId, "jspeed_query_id is null"));
			}
		} catch (Exception ex) {
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
	public String makeSqlErrorLog(String logId, String methodNm, String jspeed_query_id, Object[][] param, Exception e) {
		StringBuffer sb = new StringBuffer();

		try {
			sb = sb.append(makeErrorLine(logId, "Method  : " + methodNm       ));
			sb = sb.append(makeErrorLine(logId, "Message : " + e.getMessage() ));
			sb = sb.append(makeErrorLine(logId, "QueryID : " + jspeed_query_id));

			if (!isEmpty(jspeed_query_id)) {
				sb = sb.append("\n" + QueryService.getInstance().getSQL(jspeed_query_id));

				if (param != null) {
					int pLen1 = param.length;
					int pLen2 = 0;

					for (int ii = 0; ii < pLen1; ii++) {
						pLen2 = param[ii].length;
						sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : {");
						for (int jj = 0; jj < pLen2; jj++) {
							if (jj > 0) { sb = sb.append(", "); }
							sb = sb.append(jj + "=" + param[ii][jj].toString());
						}
						sb = sb.append("}");
					}
				}
			} else {
				sb = sb.append(makeErrorLine(logId, "jspeed_query_id is null"));
			}
		} catch (Exception ex) {
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
	public void printSqlLog(String logId, String logMsg, String jspeed_query_id, Object[] param) {
		try {
			if (!isEmpty(jspeed_query_id)) {
				StringBuffer sb = new StringBuffer("\n▩▩ " + logId + " " + logMsg + " ▩▩");
				sb = sb.append("\n▩ " + jspeed_query_id + "\n");
				sb = sb.append(QueryService.getInstance().getSQL(jspeed_query_id));

				if (param != null) {
					int pLen = param.length;

					sb = sb.append("\n▩ {");
					for (int ii = 0; ii < pLen; ii++) {
						if (ii > 0) { sb = sb.append(", "); }
						sb = sb.append(ii + "=" + param[ii].toString());
					}
					sb = sb.append("}");
				}

				sb = sb.append("\n▩▩ " + logId + " " + logMsg + " ▩▩");

				logger.println(LogLevel.DEBUG, sb.toString());
			}
		} catch (Exception e) {
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
	public void printSqlLog(String logId, String logMsg, String jspeed_query_id, Object[][] param) {
		try {
			if (!isEmpty(jspeed_query_id)) {
				StringBuffer sb = new StringBuffer("\n▩▩ " + logId + " " + logMsg + " ▩▩");
				sb = sb.append("\n▩ " + jspeed_query_id + "\n");
				sb = sb.append(QueryService.getInstance().getSQL(jspeed_query_id));

				if (param != null) {
					int pLen1 = param.length;
					int pLen2 = 0;

					for (int ii = 0; ii < pLen1; ii++) {
						pLen2 = param[ii].length;
						sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : {");
						for (int jj = 0; jj < pLen2; jj++) {
							if (jj > 0) { sb = sb.append(", "); }
							sb = sb.append(jj + "=" + param[ii][jj].toString());
						}
						sb = sb.append("}");
					}
				}

				sb = sb.append("\n▩▩ " + logId + " " + logMsg + " ▩▩");

				logger.println(LogLevel.DEBUG, sb.toString());
			}
		} catch (Exception e) {
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
	public void printParam(String logId, String logMsg, String[][] param) {
		try {
			int pLen1 = param.length;
			int pLen2 = 0;
			StringBuffer sb = new StringBuffer("\n▩▩ " + logId + " " + logMsg + " ▩▩");

			for (int ii = 0; ii < pLen1; ii++) {
				pLen2 = param[ii].length;
				sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : {");
				for (int jj = 0; jj < pLen2; jj++) {
					if (jj > 0) { sb = sb.append(", "); }
					sb = sb.append(jj + "=" + param[ii][jj].toString());
				}
				sb = sb.append("}");
			}

			sb = sb.append("\n▩▩ " + logId + " " + logMsg + " ▩▩");

			logger.println(LogLevel.DEBUG, sb.toString());
		} catch (Exception e) {
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
	public void printParam(String logId, String logMsg, Object[][] param, int[] trtRst) {
		try {
			int pLen1 = param.length;
			int pLen2 = 0;
			StringBuffer sb = new StringBuffer("\n▩▩ " + logId + " " + logMsg + " ▩▩");

			for (int ii = 0; ii < pLen1; ii++) {
				pLen2 = param[ii].length;
				sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : {");
				for (int jj = 0; jj < pLen2; jj++) {
					if (jj > 0) { sb = sb.append(", "); }
					sb = sb.append(jj + "=" + param[ii][jj].toString());
				}
				sb = sb.append("}");
			}

			sb = sb.append("\n▩▩ " + logId + " " + logMsg + " ▩▩");

			logger.println(LogLevel.DEBUG, sb.toString());
		} catch (Exception e) {
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
	 *      [A] 오퍼레이션명 : Parameter Logging
	 *
	 *      @param String paramNm
	 *      @param Object obj
	 *      @param String prnItm
	 *      @return void
	*/
	public void printParam(String paramNm, Object obj, String prnItm) {
		if (obj == null) { return; }

		try {
			if (prnItm == null || "".equals(prnItm) ||
				!(obj instanceof JDTORecord         ||
			      obj instanceof JDTORecord[]       ||
				  obj instanceof JDTORecordSet      ||
				  obj instanceof Vector)) {
				printParam(paramNm, obj);
				return;
			}

			StringTokenizer st = new StringTokenizer(prnItm, ";");
			int itmCnt = st.countTokens();
			String[] arrItm = new String[itmCnt];
			
			for (int ii = 0; ii < itmCnt; ii++) {
				arrItm[ii] = st.nextToken();
			}

			int pLen1 = 0;
			
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

					if (obj2 instanceof JDTORecordSet) {
						sb2 = sb2.append(this.getParamJs((JDTORecordSet)obj2, arrItm, key, false));
					} else if (obj2 instanceof JDTORecord) {
						sb2 = sb2.append(this.getParamJr((JDTORecord)obj2, arrItm, key));
					} else  {
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
				sb = sb.append(getParamJa((JDTORecord[])obj, arrItm, ""));
			} else if (obj instanceof JDTORecordSet) {
				sb = sb.append(getParamJs((JDTORecordSet)obj, arrItm, "", true));
			} else if (obj instanceof Vector) {
				Vector param = (Vector)obj;
				pLen1 = param.size();
				for (int ii = 0; ii < pLen1; ii++) {
					Object obj2 = param.get(ii);
					if (obj2 instanceof JDTORecordSet) {
						if (ii == 0) {
							sb = sb.append(this.getParamJs((JDTORecordSet)obj2, arrItm, formatMaxNo(ii, pLen1), true));
						} else {
							sb = sb.append(this.getParamJs((JDTORecordSet)obj2, arrItm, formatMaxNo(ii, pLen1), false));
						}
					} else if (obj2 instanceof JDTORecord) {
						sb = sb.append(this.getParamJr((JDTORecord)obj2, arrItm, formatMaxNo(ii, pLen1)));
					} else {
						sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : " + obj.toString());
					}
				}
			}

			sb = sb.append("\n▩▩ " + paramNm + " ▩▩");

			logger.println(LogLevel.DEBUG, sb.toString());
		} catch (Exception e) {
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
	public void printParam(String paramNm, Object[][] obj) {
		if (obj == null) { return; }

		try {
			String itmVal = "";
			int rowCnt = obj.length;
			int itmCnt = obj[0].length;
			int itmLen = 0;
			int[] arrLen = new int[itmCnt];
			
			for (int jj = 0; jj < itmCnt; jj++) {
				arrLen[jj] = 4;
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				for (int jj = 0; jj < itmCnt; jj++) {
					itmVal = obj[ii][jj].toString();
					if (itmVal != null && !"".equals(itmVal)) {
						itmLen = itmVal.getBytes().length;
						if (itmLen > arrLen[jj]) {
							arrLen[jj] = itmLen;
						}
					}
				}
			}

			StringBuffer sb = new StringBuffer("\n▩▩ " + paramNm + " ▩▩");

			sb = sb.append("\n▩ ----- ");

			for (int jj = 0; jj < itmCnt; jj++) {
				sb = sb.append(this.getRPad(String.valueOf(jj), arrLen[jj], "-") + " ");
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				sb = sb.append("\n▩ " + this.format(ii, 3) + " : ");
				for (int jj = 0; jj < itmCnt; jj++) {
					itmVal = obj[ii][jj].toString();
					if (itmVal == null || "".equals(itmVal)) {
						sb = sb.append(this.getRPad(" ", arrLen[jj] + 1, " "));
					} else {
						sb = sb.append(this.getRPad(itmVal, arrLen[jj] + 1, " "));
					}
				}
			}

			sb = sb.append("\n▩▩ " + paramNm + " ▩▩");

			logger.println(LogLevel.DEBUG, sb.toString());
		} catch (Exception e) {
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
	public StringBuffer getParamJs(JDTORecordSet jsParam, String[] arrItm, String prefix, boolean titleYn) {
		StringBuffer sb = new StringBuffer();

		try {
			int itmLen = 0; //항목값길이
			int rowCnt = jsParam.size();
			String itmVal = "";
			JDTORecord jrRow = null;

			int itmCnt = arrItm.length;
			int[] arrLen = new int[itmCnt];

			for (int jj = 0; jj < itmCnt; jj++) {
				arrLen[jj] = arrItm[jj].getBytes().length;
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrRow = jsParam.getRecord(ii);
				for (int jj = 0; jj < itmCnt; jj++) {
					itmVal = jrRow.getFieldString(arrItm[jj]);
					if (itmVal != null && !"".equals(itmVal)) {
						itmLen = itmVal.getBytes().length;
						if (itmLen > arrLen[jj]) {
							arrLen[jj] = itmLen;
						}
					}
				}
			}

			//접두사길이 결정
			itmLen = 5;
			String preStr = "";
			if (prefix != null && !"".equals(prefix)) {
				preStr = prefix + "-";
				int len1 = preStr.getBytes().length;
				int len2 = (String.valueOf(rowCnt)).length();
				if (len1 + len2 > 5) {
					sb = sb.append("\n▩ " + prefix);
					preStr = "";
				} else if (len1 + len2 < 5) {
					itmLen = 5 - len1;
				}
			}

			if (titleYn) {
				sb = sb.append("\n▩ Title : ");
	
				for (int jj = 0; jj < itmCnt; jj++) {
					sb = sb.append(this.getRPad(arrItm[jj], arrLen[jj] + 1, " "));
				}
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrRow = jsParam.getRecord(ii);
				sb = sb.append("\n▩ " + preStr + format(ii, itmLen) + " : ");
				for (int jj = 0; jj < itmCnt; jj++) {
					itmVal = jrRow.getFieldString(arrItm[jj]);
					if (itmVal == null || "".equals(itmVal)) {
						sb = sb.append(this.getRPad(" ", arrLen[jj] + 1, " "));
					} else {
						sb = sb.append(this.getRPad(jrRow.getFieldString(arrItm[jj]), arrLen[jj] + 1, " "));
					}
				}
			}
			
			return sb;
		} catch (Exception e) {
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
	public StringBuffer getParamJa(JDTORecord[] jaParam, String[] arrItm, String prefix) {
		StringBuffer sb = new StringBuffer();

		try {
			int itmLen = 0; //항목값길이
			int rowCnt = jaParam.length;
			String itmVal = "";
			JDTORecord jrRow = null;

			int itmCnt = arrItm.length;
			int[] arrLen = new int[itmCnt];
			
			for (int jj = 0; jj < itmCnt; jj++) {
				arrLen[jj] = arrItm[jj].getBytes().length;
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrRow = jaParam[ii];
				for (int jj = 0; jj < itmCnt; jj++) {
					itmVal = jrRow.getFieldString(arrItm[jj]);
					if (itmVal != null && !"".equals(itmVal)) {
						itmLen = itmVal.getBytes().length;
						if (itmLen > arrLen[jj]) {
							arrLen[jj] = itmLen;
						}
					}
				}
			}

			//접두사길이 결정
			itmLen = 5;
			String preStr = "";
			if (prefix != null && !"".equals(prefix)) {
				preStr = prefix + "-";
				int len1 = preStr.getBytes().length;
				int len2 = (String.valueOf(rowCnt)).length();
				if (len1 + len2 > 5) {
					sb = sb.append("\n▩ " + prefix);
					preStr = "";
				} else if (len1 + len2 < 5) {
					itmLen = 5 - len1;
				}
			}

			sb = sb.append("\n▩ Title : ");

			for (int jj = 0; jj < itmCnt; jj++) {
				sb = sb.append(this.getRPad(arrItm[jj], arrLen[jj] + 1, " "));
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrRow = jaParam[ii];
				sb = sb.append("\n▩ " + preStr + format(ii, itmLen) + " : ");
				for (int jj = 0; jj < itmCnt; jj++) {
					itmVal = jrRow.getFieldString(arrItm[jj]);
					if (itmVal == null || "".equals(itmVal)) {
						sb = sb.append(this.getRPad(" ", arrLen[jj] + 1, " "));
					} else {
						sb = sb.append(this.getRPad(jrRow.getFieldString(arrItm[jj]), arrLen[jj] + 1, " "));
					}
				}
			}
			
			return sb;
		} catch (Exception e) {
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
	public StringBuffer getParamJr(JDTORecord jrParam, String[] arrItm, String prefix) {
		StringBuffer sb = new StringBuffer();

		try {
			int itmCnt = arrItm.length;

			sb = sb.append("\n▩ " + prefix + " : {");

			for (int jj = 0; jj < itmCnt; jj++) {
				sb = sb.append(arrItm[jj] + "=" + trim(jrParam.getFieldString(arrItm[jj])));
				if (jj < itmCnt - 1) { sb = sb.append(", "); }
			}

			sb = sb.append("}");
			
			return sb;
		} catch (Exception e) {
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
	public String getMsgId(JDTORecord rcvMsg) {
		String msgId = ""; //인터페이스ID

		try {
			//JMS일 경우는 JMS_TC_CD
			msgId = trim(rcvMsg.getFieldString("JMS_TC_CD"));

			//EAI일 경우는 MSG_ID
			if (isEmpty(msgId)) {
				msgId = trim(rcvMsg.getFieldString("MSG_ID"));
			}

			//기타(출하관리 등)일 경우는 TC_CODE
			if (isEmpty(msgId)) {
				msgId = trim(rcvMsg.getFieldString("TC_CODE"));
			}
			
			// PIDEV
			//기타(출하관리 등)일 경우는 TC_CODE
			if (isEmpty(msgId)) {
				msgId = trim(rcvMsg.getFieldString("MQ_TC_CD"));
			}
			
			return msgId;
		} catch (Exception e) {
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
	public JDTORecord addSndData(JDTORecord jrExt, JDTORecordSet jsAdd) {
		try {
			//추가할 전문이 없으면 기존 그대로
			if (isEmpty(jsAdd)) {
				return jrExt;
			}

			//Return Data
			JDTORecordSet rtnData = JDTORecordFactory.getInstance().createRecordSet("");

			//기존 전문이 있으면 기존 먼저 추가
			if (!isEmpty(jrExt)) {
				JDTORecordSet extData = (JDTORecordSet)jrExt.getField("SEND_DATA");

				if (!isEmpty(extData)) {
					rtnData.addAll(extData);
				}
			}

			//추가할 전문 추가
			rtnData.addAll(jsAdd);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			jrRtn.addField("SEND_DATA", rtnData);

			return jrRtn;
		} catch (Exception e) {
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
	public JDTORecord addSndData(JDTORecord jrExt, JDTORecord jrAdd) {
		try {
			//추가할 전문이 없으면 기존 그대로
			if (isEmpty(jrAdd)) {
				return jrExt;
			}

			JDTORecordSet addData = null;

			//I/F ID를 먼저 Check
			String msgId = this.getMsgId(jrAdd);

			if (!isEmpty(msgId)) {
				//I/F ID가 존재할 경우는 전문 1건 추가
				addData = JDTORecordFactory.getInstance().createRecordSet("");
				addData.addRecord(jrAdd);
			} else {
				//SEND_DATA로 있을 경우
				addData = (JDTORecordSet)jrAdd.getField("SEND_DATA");
			}

			return addSndData(jrExt, addData);
		} catch (Exception e) {
			return jrExt;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 전송할 전문을 추가
	 *
	 *      @param JDTORecordSet jsAdd
	 *      @return JDTORecord
	*/
	public JDTORecord addSndData(JDTORecordSet jsAdd) {
		try {
			return addSndData(null, jsAdd);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 전송할 전문을 추가
	 *
	 *      @param JDTORecord jrAdd
	 *      @return JDTORecord
	*/
	public JDTORecord addSndData(JDTORecord jrAdd) {
		try {
			return addSndData(null, jrAdd);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : Grid에서 값 추출하기
	 *
	 *      @param JDTORecord jrAdd
	 *      @return JDTORecord
	*/
	public String getValue(GridData gdReq, String headerNm, int ii) {
		try {
			String rtnValue;
			if (gdReq.getHeader(headerNm).getDataType().equals(OperateGridData.t_combo)) {
				rtnValue = this.nvl(this.trim(gdReq.getHeader(headerNm).getComboHiddenValues()[gdReq.getHeader(headerNm).getSelectedIndex(ii)]),"");
			} else if (gdReq.getHeader(headerNm).getDataType().equals(OperateGridData.t_number)) {	
				rtnValue = this.nvl(this.trim(gdReq.getHeader(headerNm).getValue(ii)),"0");
			} else {
				rtnValue = this.nvl(this.trim(gdReq.getHeader(headerNm).getValue(ii)),"");
			}
			return rtnValue; 
		} catch (Exception e) {
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
	public String stringPlusInt(String szPara, int intPara) {
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
	} // end of stringPlusInt
	
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
	}
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
	

//////////////////////////////////////////////////////////////////////////////////////	
// B 열연 추가
/////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * 두개의 JDTORecord를 하나의 JDTORecord로 합치는 method
	 * 
	 * @param map :
	 *            JDTORecord, JDTORecord
	 * 
	 * @return : JDTORecord
	 */
	public JDTORecord mixJDTORecord(JDTORecord a, JDTORecord b) {

		try {
			String key = "";

			Map mMap = b.getMap();
			Set set = mMap.keySet();
			Object[] hmKeys = set.toArray();
			for (int i = 0; i < hmKeys.length; i++) {
				key = (String) hmKeys[i];
				a.setField(key, (String) mMap.get(key));
			}
		} catch (Exception e) {

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
	   if ((sSchCode.substring(2,4).equals("DC") 
		 || sSchCode.substring(2,4).equals("FE")   
		 || sSchCode.substring(2,4).equals("KD")   
		 || sSchCode.substring(2,4).equals("HS")   
		 || sSchCode.substring(2,4).equals("KE")) 
		 && sSchCode.substring(6,7).equals("U")) {   
//	      YmCommonConst.NEW_SCH_WORK_KIND_CKLI.equals(sSchCode)||  // SPM 보급
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_CKTI.equals(sSchCode)||  // SPM Take In
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_EQLI.equals(sSchCode)||  // EQL 보급
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_EQTI.equals(sSchCode)||  // EQL Take In
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_CFTI.equals(sSchCode)||  // HFL Take In
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_CFLI.equals(sSchCode)||  // HFL 보급
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_SSLI.equals(sSchCode)||    // SCARFING 보급
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_CNLI.equals(sSchCode)||	// #2 SPM 보급
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_CNTI.equals(sSchCode)||	// #2 SPM Take In
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_CHLI.equals(sSchCode)||	// #2 HFL 보급
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_CFSI.equals(sSchCode)	  // HFL 결속대 보급
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
		
		if("P".equals(sType)){
			lVal = lVal + 1;
		}else if("M".equals(sType)){
			lVal = lVal - 1;
		}
		
		return df.format(lVal);
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
    public static String getStringYMDHMS() {
        return getCurDate("yyyyMMddHHmmss");
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
    
	
	public boolean isNumeric(String str){  

		try  {  

			double d = Double.parseDouble(str);  

		}catch(NumberFormatException nfe){  
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
	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr) {
		return setWiseGridCombo(obj, hTitle, comboStrArr, 1, "N");
	}
	
	/**
 	 * 대차작업지정기준조회_코일1
	 * @param szYD_EQP_ID			대차호기
	 * @return String[]
    */
	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr, int cdVal, String headTextYn) {
		String comboStr = "";
		
		if(comboStrArr != null) {
			
			if("Y".equals(headTextYn)) {
				comboStr = obj + ".AddComboListValue('" + hTitle + "', '', '');";
			}
			
			if(cdVal == 0 || cdVal == 1) {
				for(int ii=0; ii < comboStrArr[0].length; ii++) {
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + comboStrArr[cdVal][ii] + "', '" + comboStrArr[0][ii] + "');";
				}
			}else if(cdVal == 2) { //YD에 쓸수 있게 코드/코드명 형식으로 출력				
				for(int ii=0; ii < comboStrArr[0].length; ii++) {
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + 
									"[" +comboStrArr[0][ii] + "] " + comboStrArr[1][ii] + "', '" + comboStrArr[0][ii] + "');";
				} 
			}else {
				for(int ii=0; ii < comboStrArr[0].length; ii++) {
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
	public void putLog(String szClassName, String szMethodName, String szLogMsg, int nLogLevel)  {

		String szMsg="";
		String strCurDate = YdUtils.getCurDate("yyyy-MM-dd HH:mm:ss:SSS");

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
     * Object Data Default 값을넣어주는 Function 
     * PO
     * @param  Object , String  
     * @return String 
     * @throws Exception
     */	
	public String setDataDefault (Object sObj, String sDef) throws Exception {
		
			
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
	public JDTORecord [] genJDTORecordSet(GridData inDto) throws Exception{
		boolean isUpperKey = false;		
		YDDataUtil yDDataUtil = new YDDataUtil();
		String szUserId = "";
		String szCRUD ="";
		String szydEqpId ="";
		
		if(inDto.getParam("set_upper") != null && inDto.getParam("set_upper").equals("true")){
			isUpperKey = true;
		}
		
		szUserId= yDDataUtil.setDataDefault(inDto.getParam("YD_USER_ID"), "");
		szydEqpId= yDDataUtil.setDataDefault(inDto.getParam("YD_EQP_ID"), "");

		GridHeader [] ghs = inDto.getHeaders();
		int hCount = ghs.length;
		int rCount = 0;
		if(hCount > 0){
			rCount = ghs[0].getRowCount();
		}
		JDTORecord [] jdtoAl = new JDTORecord[rCount];
		logger.println(LogLevel.DEBUG,   "========  GridData -> JDTORecord []  ================");
		logger.println(LogLevel.DEBUG,   "헤더갯수:"+hCount);
		logger.println(LogLevel.DEBUG,   "Row갯수:"+rCount);

		logger.println(LogLevel.DEBUG,   "========== GridData inDto ROW DATA ===========");
		for(int i=0;i<rCount;i++){
			GridHeader chHeader = inDto.getHeader("CHECK");
			boolean Checked = (Integer.parseInt(chHeader.getValue(i)) == 1)? true:false;
			if(Checked){
				JDTORecord jDto = JDTORecordFactory.getInstance().create();
				for(int j=0;j<hCount;j++){
					String key = ghs[j].getID();
					String rValue = "";
					String hValue = "";
					if(ghs[j].getDataType().equals(OperateGridData.t_combo)){
						int iSelectedIdx = ghs[j].getSelectedIndex(i);
						if(iSelectedIdx >= 0){
							if(ghs[j].hasComboList()){
								rValue = StringHelper.evl( ghs[j].getComboHiddenValues(ghs[j].getSelectedComboListKey(i))[iSelectedIdx], "");
								hValue = StringHelper.evl( ghs[j].getComboValues(ghs[j].getSelectedComboListKey(i))[iSelectedIdx], "");
							}else{
								rValue = StringHelper.evl( ghs[j].getComboHiddenValues()[iSelectedIdx], "");
								hValue = StringHelper.evl( ghs[j].getComboValues()[iSelectedIdx], "");							
							}
						}else{
							rValue = "";
							hValue = "";
						}
							
					}
					else {
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
				}else if("U".equals(szCRUD)){
					jDto.setField("MODIFIER",szUserId);
				}else {					
				}
				jDto.setField("YD_USER_ID",szUserId);
				
				if(!szydEqpId.equals("")){
					jDto.setField("YD_EQP_ID",szydEqpId);
				}
				  
				jdtoAl[i] = jDto;
			}
		}

		logger.println(LogLevel.DEBUG,   "========== JDTORecord START ===========");
		for(int ss=0;ss<jdtoAl.length;ss++){
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
			this.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return null;
		} // end of try-catch

		return szRcvTcCode;


	} // end of getTcCode();	
	/**
     * JJK
	 * 야드목표행선지구분를 지정한다.
     *
     * @param  String	sItemGp :	제품구분(S:SLAB, C:COIL ,P: 후판 ) ,JDTORecord inRecord
     *
     * @return String
     * @throws  
     */		
	public String[] getYdAimRtGp(String sItemGp, JDTORecord inRecord) {
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

		YmCommDAO commDao = new YmCommDAO();

		try {	
			//	PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", "YmCommCarMvSeEJBSBean => getYdAimRtGp", "APPPI0", "*", "*");
					
			if("PIDEV".equals("PIDEV")) {
				rVal= this.getYdAimRtGp_PIDEV("C",inRecord );	
			}
			
			// 전문받아서 szRcvTcCode에 대입
			String szRcvTcCode = this.getTcCode(inRecord);
			String sSTL_NO = this.trim(inRecord.getFieldString("STL_NO"));

			if (sItemGp.equals("P")) {
				// 수신한 재료번호로 plate공통
				// 읽기***************************************************************************************************

				if (!sSTL_NO.equals("")) {
					recEditInRecord.setField("PLATE_NO", sSTL_NO);

					JDTORecordSet loadPlatecomm = commDao.select(recEditInRecord,"com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getPLATECOMM",logId, szMethodName, "후판공통검색");

					if (loadPlatecomm.size() <= 0) {
						szMsg = "plate공통 SELECT Error :: [" + sSTL_NO + "]"
								+ "DO NOT EXIST";
						this.printLog(logId, szMsg, "SL");
						;
						return rVal;
					} else {
						szMsg = sSTL_NO + " :: plate공통 SELECT Success :: ["
								+ loadPlatecomm.size() + "]";
						this.printLog(logId, szMsg, "SL");

						// 진도코드 존제여부 체크
						if (this.trim(
								loadPlatecomm.getRecord(0).getFieldString(
										"CURR_PROG_CD")).equals("")) {
							szMsg = "진도코드가  존재  안 함";
							this.printLog(logId, szMsg, "SL");
							return rVal;
						} else {
							// 진도코드
							currProgCd = this.trim(loadPlatecomm.getRecord(0)
									.getFieldString("CURR_PROG_CD"));
						}

					}

				} else {
					// 진도코드
					currProgCd = this.trim(inRecord.getFieldString("CURR_PROG_CD"));
				}
				currProgCd = this.trim(inRecord.getFieldString("CURR_PROG_CD"));
				szMsg = "진도코드::" + currProgCd;
				this.printLog(logId, szMsg, "SL");

				if (szRcvTcCode.equals("DMYDR006")) {
					ydAimRtGp = "K3"; // 출하지시대기
					currProgCd = "K";
				} else if (szRcvTcCode.equals("DMYDR018")) {
					ydAimRtGp = "N3"; // 운송지시대기
					currProgCd = "N";
				} else if (szRcvTcCode.equals("DMYDR021")) {
					ydAimRtGp = "L6"; // 운송상차지시
					currProgCd = "L";
				} else if (szRcvTcCode.equals("DMYDR031")) {
					ydAimRtGp = "M3"; // 출하완료
					currProgCd = "M";
				} else if (currProgCd.equals("Y")) {
					ydAimRtGp = currProgCd + "C"; // 재공충당대기(A후판plate)
				} else if (currProgCd.equals("G")) {
					ydAimRtGp = currProgCd + "3"; // 종합판정대기
				} else if (currProgCd.equals("I")) {
					ydAimRtGp = currProgCd + "3"; // 반송대기
				} else if (currProgCd.equals("H")) {
					ydAimRtGp = currProgCd + "3"; // 입고대기
				} else if (currProgCd.equals("J")) {
					ydAimRtGp = currProgCd + "3"; // 반납대기
				} else if (currProgCd.equals("Z")) {
					ydAimRtGp = currProgCd + "3"; // 제품충당대기
				} else if (currProgCd.equals("X")) {
					ydAimRtGp = currProgCd + "3"; // 경매대상선정
				} else if (currProgCd.equals("K")) {
					ydAimRtGp = currProgCd + "3"; // 출하지시대기
				}
				// ***************************************************************************************************************************
			} else if (sItemGp.equals("C")) {
				// 수신한 재료번호로 코일공통
				// 읽기***************************************************************************************************
				if (!sSTL_NO.equals("")) {
					recEditInRecord.setField("COIL_NO", sSTL_NO);

					JDTORecordSet loadYdStock = commDao.select(recEditInRecord,"com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCOILCOMM1",logId, szMethodName, "코일공통검색");

					if (loadYdStock.size() <= 0) {
						szMsg = "코일공통 SELECT Error :: [" + sSTL_NO + "]"
								+ "DO NOT EXIST";
						this.printLog(logId, szMsg, "SL");
						return rVal;
					} else {
						szMsg = inRecord.getFieldString("STL_NO")
								+ " :: 코일공통 SELECT Success :: ["
								+ loadYdStock.size() + "]";
						this.printLog(logId, szMsg, "SL");

						sYD_AIM_RT_GP2 = this.trim(loadYdStock.getRecord(0).getFieldString("YD_AIM_RT_GP2"));
						sHCR_GP = this.trim(loadYdStock.getRecord(0)
								.getFieldString("HCR_GP"));
						sSKINPASS_YN = this.trim(loadYdStock.getRecord(0).getFieldString("SKINPASS_YN"));

						// 진도코드 존제여부 체크
						if (this.trim(
								loadYdStock.getRecord(0).getFieldString(
										"CURR_PROG_CD")).equals("")) {
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
				} else {
					// 진도코드
					currProgCd = this.trim(inRecord.getFieldString("CURR_PROG_CD"));
				}

				szMsg = "진도코드::" + currProgCd;
				this.printLog(logId, szMsg, "SL");

				// ***********************************************************//
				if (szRcvTcCode.equals("DMYDR005")) {
					ydAimRtGp = "K2"; // 출하지시대기
					currProgCd = "K";
				} else if (szRcvTcCode.equals("DMYDR020")) {
					ydAimRtGp = "L2"; // 운송지시
					currProgCd = "L";
				} else if (szRcvTcCode.equals("DMYDR023")
						|| szRcvTcCode.equals("DMYDR060")) {
					ydAimRtGp = "L5"; // 상차지시
					currProgCd = "L";
				} else if (szRcvTcCode.equals("DMYDR030")) {
					ydAimRtGp = "M2"; // 출하완료
					currProgCd = "M";
					// ***********************************************************//
				} else if (currProgCd.equals("G")) {
					ydAimRtGp = currProgCd + "2"; // 종합판정대기
				} else if (currProgCd.equals("I")) {
					ydAimRtGp = currProgCd + "2"; // 반송대기
				} else if (currProgCd.equals("H")) {
					ydAimRtGp = currProgCd + "2"; // 입고대기
				} else if (currProgCd.equals("Y")) {
					ydAimRtGp = currProgCd + "C"; // 재공충당대기(C열연정정)
				} else if (currProgCd.equals("B")) { // 지시대기

					String sWorkProc = "";
					// sNextProc =
					// this.trim(loadYdStock.getRecord(0).getFieldString("NEXT_PROC"));

					// C 동 수입인 경우 에만 spm재와 hfl재를 분리해서 적치 한다.
					// if(sNextProc.substring(0,1).equals("C")){
					if (sNextProc.substring(1, 2).equals("H")) {
						ydAimRtGp = currProgCd + "3"; // 지시대기
					} else {
						ydAimRtGp = currProgCd + "4"; // 지시대기
					}
					// }else{
					// //HCR재 - H, WCR재 - W, CCR재 - C
					// if(sHCR_GP.equals("H")){
					// ydAimRtGp =currProgCd+"3"; //지시대기
					// }else {
					// ydAimRtGp =currProgCd+"4"; //지시대기
					// }
					// }
				} else if (currProgCd.equals("J")) {
					ydAimRtGp = currProgCd + "2"; // 반납대기
					// ydAimRtGp ="B3"; //반납대기 ?????
					// }else if(currProgCd.equals("K")){
					// ydAimRtGp =currProgCd+"2"; //출하지시대기
					// }else if(currProgCd.equals("L")){
					// if(szRcvTcCode.equals("DMYDR023")){ //코일제품상차지시
					// ydAimRtGp =currProgCd+"5"; //상차대기
					// }else {
					// ydAimRtGp =currProgCd+"2"; //운송대기
					// }
					// }else if(currProgCd.equals("M")){
					// ydAimRtGp =currProgCd+"2"; //출하완료
				} else if (currProgCd.equals("Z")) {
					ydAimRtGp = currProgCd + "2"; // 제품충당대기
				} else if (currProgCd.equals("X")) {
					ydAimRtGp = currProgCd + "2"; // 경매대상선정
				} else if (currProgCd.equals("E") || currProgCd.equals("D")) {
					// 재공이송작업대기
					String sWorkProc = "";
					// sNextProc =
					// this.trim(loadYdStock.getRecord(0).getFieldString("NEXT_PROC"));
					// sPlanProc1
					// =this.trim(loadYdStock.getRecord(0).getFieldString("PLAN_PROC1"));

					if (!"".equals(sNextProc)) {
						sWorkProc = sNextProc;
					} else {
						sWorkProc = sPlanProc1;
					}
					// 계획공정정보를 가지고 야드행선을 셋팅
					if (sWorkProc.startsWith("1")) {
						ydAimRtGp = "EA";
					} else if (sWorkProc.startsWith("5")
							|| sWorkProc.startsWith("6")) {
						ydAimRtGp = "EB";
					} else if (sWorkProc.startsWith("9S")) {
						ydAimRtGp = "ED";
					} else {
						ydAimRtGp = "EC";
					}
				} else if (currProgCd.equals("C")) {
					// 정정작업지시대기
					String sWorkProc = "";
					// sNextProc =
					// this.trim(loadYdStock.getRecord(0).getFieldString("NEXT_PROC"));
					// sPlanProc1 =
					// this.trim(loadYdStock.getRecord(0).getFieldString("PLAN_PROC1"));

					if (!"".equals(sNextProc)) {
						sWorkProc = sNextProc;
					} else {
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
					if (sWorkProc.equals("DH") || sWorkProc.equals("FH")
							|| sWorkProc.equals("GA") || sWorkProc.equals("GH")
							|| sWorkProc.equals("CA") || sWorkProc.equals("CH")
							|| sWorkProc.equals("AA") || sWorkProc.equals("BH")
							|| sWorkProc.equals("GT")) {
						ydAimRtGp = "CE";
					} else if (sWorkProc.equals("HH") || sWorkProc.equals("HK")
							|| sWorkProc.equals("HR")) {
						ydAimRtGp = "CF";
					} else if (sWorkProc.equals("EH") || sWorkProc.equals("EK")
							|| sWorkProc.equals("ER")) {
						ydAimRtGp = "CG";
					} else if (sWorkProc.equals("CK") || sWorkProc.equals("CR")) {
						ydAimRtGp = "CF";
					} else if (sWorkProc.equals("BK") || sWorkProc.equals("BR")) {
						ydAimRtGp = "CF";
					} else if (sWorkProc.equals("AK") || sWorkProc.equals("AR")) {
						ydAimRtGp = "CF";
					} else {
						ydAimRtGp = "XX";
					}
					if (sYD_AIM_RT_GP2.equals("F4")
							|| sYD_AIM_RT_GP2.equals("F5")) { // 재작업인 경우
						ydAimRtGp = sYD_AIM_RT_GP2; // 재작업인(C열연정정)
					}

				} else if (currProgCd.equals("F")) {

					ydAimRtGp = currProgCd + "3"; // 판정보류

				}

				// 2pass재 작업 대상
				if (sSKINPASS_YN.equals("Z")
						&& (currProgCd.equals("C") || currProgCd.equals("D"))) {
					ydAimRtGp = "EA";
				}
				// ***************************************************************************************************************************
			} else if (sItemGp.equals("S")) {
				// 수신한 재료번호로 슬라브공통을 읽기
				// ***************************************************************************************************
				recEditInRecord.setField("SLAB_NO", sSTL_NO);

				JDTORecordSet loadYdStock1 = commDao.select(recEditInRecord,"com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getSLABCOMM",logId, szMethodName, "슬라브공통검색");

				if (loadYdStock1.size() <= 0) {
					szMsg = "슬라브공통을 SELECT Error :: [" + sSTL_NO + "]"
							+ "DO NOT EXIST";
					this.printLog(logId, szMsg, "SL");
					return rVal;
				} else {
					szMsg = sSTL_NO + " :: 슬라브공통을 SELECT Success :: ["
							+ loadYdStock1.size() + "]";
					this.printLog(logId, szMsg, "SL");

					// 진도코드 존제여부 체크
					if (this.trim(
							loadYdStock1.getRecord(0).getFieldString("CURR_PROG_CD")).equals("")) {
						szMsg = "진도코드가  존재  안 함";
						this.printLog(logId, szMsg, "SL");
						return rVal;
					} else {
						// 진도코드
						currProgCd = this.trim(loadYdStock1.getRecord(0).getFieldString("CURR_PROG_CD"));
						szMsg = "진도코드::" + currProgCd;
						this.printLog(logId, szMsg, "SL");
						// ***********************************************************//
						if (szRcvTcCode.equals("DMYDR004")) {
							ydAimRtGp = "K1"; // 출하지시대기
							currProgCd = "K";
						} else if (szRcvTcCode.equals("DMYDR016")) {
							ydAimRtGp = "N1"; // 운송지시대기
							currProgCd = "N";
						} else if (szRcvTcCode.equals("DMYDR022")) {
							ydAimRtGp = "L4"; // 운송상차지시
							currProgCd = "L";
						} else if (szRcvTcCode.equals("DMYDR029")) {
							ydAimRtGp = "M1"; // 출하완료
							currProgCd = "M";
							// ***********************************************************//
						} else if (currProgCd.equals("G")) {
							ydAimRtGp = currProgCd + "1"; // 종합판정대기
						} else if (currProgCd.equals("H")) {
							ydAimRtGp = currProgCd + "1"; // 입고대기
						} else if (currProgCd.equals("J")) {
							ydAimRtGp = currProgCd + "1"; // 반납대기
						} else if (currProgCd.equals("K")) {
							ydAimRtGp = currProgCd + "1"; // 출하지시대기
						} else if (currProgCd.equals("L")) {
							ydAimRtGp = currProgCd + "1"; // 운송대기
						} else if (currProgCd.equals("N")) {
							ydAimRtGp = currProgCd + "1"; // 운송지시대기
						} else if (currProgCd.equals("M")) {
							ydAimRtGp = currProgCd + "1"; // 출하완료
						} else if (currProgCd.equals("Z")) {
							ydAimRtGp = currProgCd + "1"; // 제품충당대기
						} else if (currProgCd.equals("X")) {
							ydAimRtGp = currProgCd + "1"; // 경매대상선정
						}
					}
				}

			}
			// ***************************************************************************************************************************
		} catch (Exception e) {
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
//	public  String transOrdChange(JDTORecord recPara ) throws JDTOException {
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
//			if(intRtnVal <= 0){
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
//			}else{
//				szMsg="["+szOperationName+"]" 
//				+ " TB_YD_CARSCH UPDATE Success " 				
//				+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//				+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//				+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//				+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;    
//				this.printLog(logId, szMsg, "SL");
//
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
//		if(intRtnVal <= 0){
//			szMsg="["+szOperationName+"]"  
//            + " TB_YD_EXAMINATIONCHKLIST UPDATE Error " 
//			+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//			+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//			+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//			+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;
// 
//			this.printLog(logId, szMsg, "SL");
//			return "";
// 
//		}else{
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
//		if(szCHK_GP.equals("YD")){
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
//			if(intRtnVal <= 0){
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
//	 
//			}else{
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
//
//		}else{
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
//			if(intRtnVal <= 0){
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
//	 
//			}else{
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
//			
//			
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
	public JDTORecord[] genGridToJDTORecordAll(GridData inDto) throws Exception {
		boolean isUpperKey = false;
		YDDataUtil yDDataUtil = new YDDataUtil();
		if (inDto.getParam("set_upper") != null && inDto.getParam("set_upper").equals("true")) {
			isUpperKey = true;
		}
		
		String szUserId= yDDataUtil.setDataDefault(inDto.getParam("YD_USER_ID"), "");

		GridHeader[] ghs = inDto.getHeaders();
		int hCount = ghs.length;
		int rCount = 0;
		if (hCount > 0) {
			rCount = ghs[0].getRowCount();
		}
		JDTORecord[] jdtoAl = new JDTORecord[rCount];
		logger.println(LogLevel.DEBUG, "========  GridData -> JDTORecord []  ================");
		logger.println(LogLevel.DEBUG, "헤더갯수:" + hCount);
		logger.println(LogLevel.DEBUG, "Row갯수:" + rCount);

		logger.println(LogLevel.DEBUG, "========== GridData inDto ROW DATA ===========");
		for (int i = 0; i < rCount; i++) {
			
			JDTORecord jDto = this.genParamToJDTORecord(inDto);

			for (int j = 0; j < hCount; j++) {
				String key = ghs[j].getID();
				String rValue = "";
				String hValue = "";
				
				
				//수정_이현성 [콤보박스일때 문제점 해결하기 위함]
				if (ghs[j].getDataType().equals(OperateGridData.t_combo)) {
					
					int iSelectedIdx = ghs[j].getSelectedIndex(i);
					if(iSelectedIdx >= 0){
						if(ghs[j].hasComboList()){
							rValue = StringHelper.evl( ghs[j].getComboHiddenValues(ghs[j].getSelectedComboListKey(i))[iSelectedIdx], "");
							hValue = StringHelper.evl( ghs[j].getComboValues(ghs[j].getSelectedComboListKey(i))[iSelectedIdx], "");
						}else{
							rValue = StringHelper.evl( ghs[j].getComboHiddenValues()[iSelectedIdx], "");
							hValue = StringHelper.evl( ghs[j].getComboValues()[iSelectedIdx], "");							
						}
					}else{
						rValue = "";
						hValue = "";
					}
				}
				else {
					rValue = StringHelper.evl(ghs[j].getValue(i), "");
					hValue = StringHelper.evl(ghs[j].getHiddenValue(i), "");
				}

				jDto.setField((isUpperKey) ? key.toUpperCase() : key, rValue);
			}
			jDto.setField("YD_USER_ID",szUserId);
			jdtoAl[i] = jDto;
			
		}

		logger.println(LogLevel.DEBUG, "========== JDTORecord START ===========");
		for (int ss = 0; ss < jdtoAl.length; ss++) {
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
	public String getCarMoveYN(String cardNo){
		String carMove = "N";
		if ((cardNo.equals(YmConstant.CAR_BAY_TRANS_CARD_NO_1))
   	    			|| (cardNo.equals(YmConstant.CAR_BAY_TRANS_CARD_NO_2))
   	    			|| (cardNo.equals(YmConstant.CAR_BAY_TRANS_CARD_NO_3))
   	    			|| (cardNo.equals(YmConstant.CAR_BAY_TRANS_CARD_NO_4))
   	    			|| (cardNo.equals(YmConstant.CAR_BAY_TRANS_CARD_NO_5))) {
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
	public static JDTORecord genJDTO(Class oClass, Object oInstance) throws AppRuntimeException {

		// 출력 JDTORecord 객체
		JDTORecord jdto = null;
		// 입력 Object 클래스가 가지고 있는 메소드 배열
		Method[] oMethodArr = null;

		try {
			// 출력 jdto 객체 생성
			jdto = JDTORecordFactory.getInstance().create();

			// 입력 Class 로부터 메소드 명칭 배열을 취득합니다.
			oMethodArr = oClass.getMethods();

			// 입력 Class 의 메소드 명칭 배열만큼 반복
			for (int ii = 0; ii < oMethodArr.length; ii++) {
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
				if (methodName.startsWith("get")) {
					if(!methodName.equals("getClass")){
						// JDTORecord의 key 변수에 get_prefix 를 제외한 값을 설정
						key = methodName.substring(3);
						// oInstance 객체의 set 메소드를 실행(invoke)
						obj = oMethod.invoke(oInstance, null);
						// JDTORecord 에 값을 저장
						jdto.setField(key, obj);
					}
				}
			}
		} catch (Exception e) {
			throw new AppRuntimeException(e.toString(), e);
		} finally {

		}
		// 출력 JDTORecord 객체 반환
		return jdto;
	}	
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
	public static JDTORecord genJDTO1(YMPO155 model) throws AppRuntimeException {

		// 출력 JDTORecord 객체
		JDTORecord jdto = JDTORecordFactory.getInstance().create();

		try {
			// 출력 jdto 객체 생성
			jdto.setField("JMS_TC_CD"			, "YMPOJ155");
			jdto.setField("JMS_TC_CREATE_DDTT"	, new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			jdto.setField("tcCode"				, "YMPOJ155");
			jdto.setField("tcDate"				, model.getTcDate());
			jdto.setField("tcTime"				, model.getTcTime());
			jdto.setField("slabNo"				, model.getslabNo());
			jdto.setField("upDownGbn"			, model.getupDownGbn());
			jdto.setField("upDownDate"			, model.getupDownDate());
			jdto.setField("upDownLoc"			, model.getupDownLoc());
		} catch (Exception e) {
			throw new AppRuntimeException(e.toString(), e);
		} finally {

		}
		// 출력 JDTORecord 객체 반환
		return jdto;
	}	
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
	public static JDTORecord genJDTO2(YMPO159 model) throws AppRuntimeException {

		// 출력 JDTORecord 객체
		JDTORecord jdto = JDTORecordFactory.getInstance().create();

		try {
			// 출력 jdto 객체 생성
			jdto.setField("JMS_TC_CD"			, "YMPOJ159");
			jdto.setField("JMS_TC_CREATE_DDTT"	, new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			jdto.setField("tcCode"				, "YMPOJ159");
			jdto.setField("tcDate"				, model.getTcDate());
			jdto.setField("tcTime"				, model.getTcTime());
			jdto.setField("coilNo"				, model.getcoilNo());
			jdto.setField("upDownGbn"			, model.getupDownGbn());
			jdto.setField("upDownDate"			, model.getupDownDate());
			jdto.setField("upDownLoc"			, model.getupDownLoc());
		} catch (Exception e) {
			throw new AppRuntimeException(e.toString(), e);
		} finally {

		}
		// 출력 JDTORecord 객체 반환
		return jdto;
	}	
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
	public static JDTORecord genJDTO3(YMPO161 model) throws AppRuntimeException {

		// 출력 JDTORecord 객체
		JDTORecord jdto = JDTORecordFactory.getInstance().create();

		try { 
			// 출력 jdto 객체 생성
			jdto.setField("JMS_TC_CD"			, "YMPOJ161");   // 윤 요청
			jdto.setField("JMS_TC_CREATE_DDTT"	, new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			jdto.setField("tcCode"				, "YMPOJ161");
			jdto.setField("tcDate"				, model.getTcDate());
			jdto.setField("tcTime"				, model.getTcTime());
			jdto.setField("plantGbn"			, model.getplantGbn());
			jdto.setField("procGbn"				, model.getprocGbn());
			jdto.setField("coilNo"				, model.getcoilNo());
			jdto.setField("processId"			, model.getProcessId());
			jdto.setField("downDate"			, model.getdownDate());
			jdto.setField("downTime"			, model.getdownTime());
			jdto.setField("positionNo"			, model.getpositionNo());
			logger.println(LogLevel.DEBUG, "========== JDTORecord END =========== "+  model.getcoilNo());
			
		} catch (Exception e) {
			throw new AppRuntimeException(e.toString(), e);
		} finally {

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
    public static String updSmsMsgSend(JDTORecord recInPara) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;
		
		szMsg        				= "";
		szMethodName 				= "updSmsMsgSend";
		String szOperationName 		= "SMS SENDER";
		 
		JDTORecord	inRecord 		= null;
		try {
			
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
			
		}catch(Exception ex) {
			szMsg = "["+szOperationName+"] SMS 송신 ERROR - 메세지 : " + ex.getMessage();
		}
		return YdConstant.RETN_CD_SUCCESS;
		
		
	}	// end of updSmsMsgSend										
    
    // PIDEV
	/***********************************
	    PIDEV 개발
	***********************************/
  	//해쉬맵의 내용을 JDTORecord로 담는다.
  	public HashMap jdtoRecordToLinkedHashMap(JDTORecord inJRecord) throws Exception {
  		LinkedHashMap returnMap = new LinkedHashMap();

  		if (inJRecord == null || inJRecord.size() == 0) {
  			return returnMap;
  		}

  		java.util.Iterator iterator = inJRecord.iterateName();

  		String key = ""; 
  		while (iterator.hasNext()) { 
  			key = String.valueOf(iterator.next());
  			returnMap.put(key, nvl(inJRecord.getField(key), ""));
  		}
  		return returnMap;
  	}

  	
	/**
	 * 야드목표행선지구분를 지정한다. PI
     *
     * @param  String	sItemGp :	제품구분(S:SLAB, C:COIL ,P: 후판 ) ,JDTORecord inRecord
     *
     * @return String
     * @throws  
     */		
	public String[] getYdAimRtGp_PIDEV(String sItemGp, JDTORecord inRecord) {
		// 메세지
		String logId = inRecord.getResultCode();
		String szMsg = null;
		String currProgCd = null;
		String ydAimRtGp = null;
		String sYD_AIM_RT_GP2 = "";
		String sHCR_GP = "";
		String sSKINPASS_YN = "";
		// 메소드명
		String szMethodName = "getYdAimRtGpPI";
		String sNextProc = ""; // 다음공정
		String sPlanProc1 = ""; // 열연계획작업코드1

		String[] rVal = new String[2];

		JDTORecord recEditInRecord = JDTORecordFactory.getInstance().create();

		YmCommDAO commDao = new YmCommDAO();

		try {

			// 전문받아서 szRcvTcCode에 대입
			String szRcvTcCode = this.getTcCode(inRecord);
			String sSTL_NO = this.trim(inRecord.getFieldString("STL_NO"));

			if (sItemGp.equals("C")) {
				// 수신한 재료번호로 코일공통
				// 읽기***************************************************************************************************
				if (!sSTL_NO.equals("")) {
					recEditInRecord.setField("COIL_NO", sSTL_NO);

					JDTORecordSet loadYdStock = commDao.select(recEditInRecord,"com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCOILCOMM1",logId, szMethodName, "코일공통검색");

					if (loadYdStock.size() <= 0) {
						szMsg = "코일공통 SELECT Error :: [" + sSTL_NO + "]"
								+ "DO NOT EXIST";
						this.printLog(logId, szMsg, "SL");
						return rVal;
					} else {
						szMsg = inRecord.getFieldString("STL_NO") + " :: 코일공통 SELECT Success :: [" + loadYdStock.size() + "]";
						this.printLog(logId, szMsg, "SL");

						sYD_AIM_RT_GP2 = this.trim(loadYdStock.getRecord(0).getFieldString("YD_AIM_RT_GP2"));
						sHCR_GP = this.trim(loadYdStock.getRecord(0).getFieldString("HCR_GP"));
						sSKINPASS_YN = this.trim(loadYdStock.getRecord(0).getFieldString("SKINPASS_YN"));

						// 진도코드 존제여부 체크
						if (this.trim(loadYdStock.getRecord(0).getFieldString("CURR_PROG_CD")).equals("")) {
							szMsg = "진도코드가  존재  안 함";
							this.printLog(logId, szMsg, "SL");
							return rVal;
						}
						sNextProc  = this.trim(loadYdStock.getRecord(0).getFieldString("NEXT_PROC"));
						sPlanProc1 = this.trim(loadYdStock.getRecord(0).getFieldString("PLAN_PROC1"));
					}

					// 진도코드
					currProgCd = this.trim(loadYdStock.getRecord(0).getFieldString("CURR_PROG_CD"));
				} else {
					// 진도코드
					currProgCd = this.trim(inRecord.getFieldString("CURR_PROG_CD"));
				}

				szMsg = "진도코드::" + currProgCd;
				this.printLog(logId, szMsg, "SL");

				// ***********************************************************//
				if (szRcvTcCode.equals("M10LMYDJ1011")) {
					ydAimRtGp = "K2"; // 코일출하지시대기
					currProgCd = "K";
				} else if (szRcvTcCode.equals("M10LMYDJ1031")) {
					ydAimRtGp = "L5"; // 상차지시
					currProgCd = "L";
				} else if (szRcvTcCode.equals("M10LMYDJ1071")) {
					ydAimRtGp = "M2"; // 코일출하완료
					currProgCd = "M";
					// ***********************************************************//
				} else if (currProgCd.equals("G")) {
					ydAimRtGp = currProgCd + "2"; // 종합판정대기
				} else if (currProgCd.equals("I")) {
					ydAimRtGp = currProgCd + "2"; // 반송대기
				} else if (currProgCd.equals("H")) {
					ydAimRtGp = currProgCd + "2"; // 입고대기
				} else if (currProgCd.equals("Y")) {
					ydAimRtGp = currProgCd + "C"; // 재공충당대기(C열연정정)
				} else if (currProgCd.equals("B")) { // 지시대기

					// sNextProc =
					// commUtils.trim(loadYdStock.getRecord(0).getFieldString("NEXT_PROC"));

					// C 동 수입인 경우 에만 spm재와 hfl재를 분리해서 적치 한다.
					// if(sNextProc.substring(0,1).equals("C")){
					if (sNextProc.substring(1, 2).equals("H")) {
						ydAimRtGp = currProgCd + "3"; // 지시대기
					} else {
						ydAimRtGp = currProgCd + "4"; // 지시대기
					}
					// }else{
					// //HCR재 - H, WCR재 - W, CCR재 - C
					// if(sHCR_GP.equals("H")){
					// ydAimRtGp =currProgCd+"3"; //지시대기
					// }else {
					// ydAimRtGp =currProgCd+"4"; //지시대기
					// }
					// }
				} else if (currProgCd.equals("J")) {
					ydAimRtGp = currProgCd + "2"; // 반납대기
					// ydAimRtGp ="B3"; //반납대기 ?????
					// }else if(currProgCd.equals("K")){
					// ydAimRtGp =currProgCd+"2"; //출하지시대기
					// }else if(currProgCd.equals("L")){
					// if(szRcvTcCode.equals("DMYDR023")){ //코일제품상차지시
					// ydAimRtGp =currProgCd+"5"; //상차대기
					// }else {
					// ydAimRtGp =currProgCd+"2"; //운송대기
					// }
					// }else if(currProgCd.equals("M")){
					// ydAimRtGp =currProgCd+"2"; //출하완료
				} else if (currProgCd.equals("Z")) {
					ydAimRtGp = currProgCd + "2"; // 제품충당대기
				} else if (currProgCd.equals("X")) {
					ydAimRtGp = currProgCd + "2"; // 경매대상선정
				} else if (currProgCd.equals("E") || currProgCd.equals("D")) {
					// 재공이송작업대기
					String sWorkProc = "";
					// sNextProc =
					// commUtils.trim(loadYdStock.getRecord(0).getFieldString("NEXT_PROC"));
					// sPlanProc1
					// =commUtils.trim(loadYdStock.getRecord(0).getFieldString("PLAN_PROC1"));

					if (!"".equals(sNextProc)) {
						sWorkProc = sNextProc;
					} else {
						sWorkProc = sPlanProc1;
					}
					// 계획공정정보를 가지고 야드행선을 셋팅
					if (sWorkProc.startsWith("1")) {
						ydAimRtGp = "EA";
					} else if (sWorkProc.startsWith("5")
							|| sWorkProc.startsWith("6")) {
						ydAimRtGp = "EB";
					} else if (sWorkProc.startsWith("9S")) {
						ydAimRtGp = "ED";
					} else {
						ydAimRtGp = "EC";
					}
				} else if (currProgCd.equals("C")) {
					// 정정작업지시대기
					String sWorkProc = "";
					// sNextProc =
					// commUtils.trim(loadYdStock.getRecord(0).getFieldString("NEXT_PROC"));
					// sPlanProc1 =
					// commUtils.trim(loadYdStock.getRecord(0).getFieldString("PLAN_PROC1"));

					if (!"".equals(sNextProc)) {
						sWorkProc = sNextProc;
					} else {
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
					if (sWorkProc.equals("DH") || sWorkProc.equals("FH")
							|| sWorkProc.equals("GA") || sWorkProc.equals("GH")
							|| sWorkProc.equals("CA") || sWorkProc.equals("CH")
							|| sWorkProc.equals("AA") || sWorkProc.equals("BH")
							|| sWorkProc.equals("GT")) {
						ydAimRtGp = "CE";
					} else if (sWorkProc.equals("HH") || sWorkProc.equals("HK")
							|| sWorkProc.equals("HR")) {
						ydAimRtGp = "CF";
					} else if (sWorkProc.equals("EH") || sWorkProc.equals("EK")
							|| sWorkProc.equals("ER")) {
						ydAimRtGp = "CG";
					} else if (sWorkProc.equals("CK") || sWorkProc.equals("CR")) {
						ydAimRtGp = "CF";
					} else if (sWorkProc.equals("BK") || sWorkProc.equals("BR")) {
						ydAimRtGp = "CF";
					} else if (sWorkProc.equals("AK") || sWorkProc.equals("AR")) {
						ydAimRtGp = "CF";
					} else {
						ydAimRtGp = "XX";
					}
					if (sYD_AIM_RT_GP2.equals("F4")
							|| sYD_AIM_RT_GP2.equals("F5")) { // 재작업인 경우
						ydAimRtGp = sYD_AIM_RT_GP2; // 재작업인(C열연정정)
					}

				} else if (currProgCd.equals("F")) {

					ydAimRtGp = currProgCd + "3"; // 판정보류

				}

				// 2pass재 작업 대상
				if (sSKINPASS_YN.equals("Z")
						&& (currProgCd.equals("C") || currProgCd.equals("D"))) {
					ydAimRtGp = "EA";
				}
				// ***************************************************************************************************************************
			} 

			// ***************************************************************************************************************************
		} catch (Exception e) {
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
