/**
 * @(#)YdSlabUtils
 *
 * @version          V1.00
 * @author           허철호
 * @date             2012/11/22
 *
 * @description      Slab야드 Utils
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2012/11/22   허철호      허철호      최초 등록
 */
package com.inisteel.cim.yd.common.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.StringTokenizer;
import java.util.Vector;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.inisteel.cim.common.exception.DAOException;

import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.query.QueryService;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.GridHeader;
import xlib.cmc.OperateGridData;

/**
 * [A] 클래스명 : Slab야드 Utils
 *
 */

public class YdSlabUtils {

	private static Logger logger = new Logger("yd");

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
		logger.println(LogLevel.DEBUG, "========  genGridData -> JDTORecord []  ================");
		logger.println(LogLevel.DEBUG, "헤더갯수:" + hCount);
		logger.println(LogLevel.DEBUG, "Row갯수:" + rCount);

		logger.println(LogLevel.DEBUG, "========== genGridData inDto ROW DATA ===========");
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
				jrParam.setField("V_MODIFIER", modifier);	//수정자
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

}
